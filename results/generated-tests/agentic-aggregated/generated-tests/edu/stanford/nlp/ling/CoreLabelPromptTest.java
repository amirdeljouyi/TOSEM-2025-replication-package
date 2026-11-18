package edu.stanford.nlp.ling;

import edu.stanford.nlp.trees.ud.CoNLLUFeatures;
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.CoreMap;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class CoreLabelPromptTest {

  @Test
  public void defaultAndCapacityConstructorsHaveExpectedMissingValues() {
    CoreLabel empty = new CoreLabel();
    CoreLabel reserved = new CoreLabel(2);

    assertNull(empty.value());
    assertNull(empty.word());
    assertNull(empty.tag());
    assertEquals(-1, empty.index());
    assertEquals(-1, empty.sentIndex());
    assertEquals(-1, empty.beginPosition());
    assertEquals(-1, empty.endPosition());
    assertEquals(0, empty.getEmptyIndex());
    assertFalse(empty.hasEmptyIndex());
    assertEquals(0, reserved.size());
  }

  @Test
  public void wordFromStringInitializesThreeRepresentations() {
    CoreLabel label = CoreLabel.wordFromString("café");

    assertEquals("café", label.word());
    assertEquals("café", label.originalText());
    assertEquals("café", label.value());
    assertEquals(3, label.size());
  }

  @Test
  public void convenienceAccessorsRoundTripAnnotations() {
    CoreLabel label = new CoreLabel();
    label.setValue("value");
    label.setWord("word");
    label.setTag("NN");
    label.setCategory("NP");
    label.setBefore("  ");
    label.setAfter("\n");
    label.setOriginalText("Word");
    label.setDocID("doc-7");
    label.setNER("ORG");
    label.setLemma("lemma");

    assertEquals("value", label.value());
    assertEquals("word", label.word());
    assertEquals("NN", label.tag());
    assertEquals("NP", label.category());
    assertEquals("  ", label.before());
    assertEquals("\n", label.after());
    assertEquals("Word", label.originalText());
    assertEquals("doc-7", label.docID());
    assertEquals("ORG", label.ner());
    assertEquals("lemma", label.lemma());
  }

  @Test
  public void stringAccessorsApplyDefaultsOnlyToMissingOrNullValues() {
    CoreLabel label = new CoreLabel();
    label.set(CoreAnnotations.TextAnnotation.class, "present");

    assertEquals("present", label.getString(CoreAnnotations.TextAnnotation.class));
    assertEquals("present", label.getString(CoreAnnotations.TextAnnotation.class, "fallback"));
    assertEquals("", label.getString(CoreAnnotations.LemmaAnnotation.class));
    assertEquals("fallback", label.getString(CoreAnnotations.LemmaAnnotation.class, "fallback"));
    assertEquals("", label.before());
    assertEquals("", label.after());
    assertEquals("", label.originalText());
  }

  @Test
  public void booleanAndPositionAccessorsRoundTripBoundaryValues() {
    CoreLabel label = new CoreLabel();
    assertNull(label.isNewline());
    assertNull(label.isMWT());
    assertNull(label.isMWTFirst());

    label.setIsNewline(true);
    label.setIsMWT(false);
    label.setIsMWTFirst(true);
    label.setIndex(0);
    label.setSentIndex(-3);
    label.setBeginPosition(12);
    label.setEndPosition(12);

    assertEquals(Boolean.TRUE, label.isNewline());
    assertEquals(Boolean.FALSE, label.isMWT());
    assertEquals(Boolean.TRUE, label.isMWTFirst());
    assertEquals(0, label.index());
    assertEquals(-3, label.sentIndex());
    assertEquals(12, label.beginPosition());
    assertEquals(12, label.endPosition());
  }

  @Test
  public void emptyIndexDistinguishesAbsentFromExplicitZero() {
    CoreLabel label = new CoreLabel();
    assertEquals(0, label.getEmptyIndex());
    assertFalse(label.hasEmptyIndex());

    label.setEmptyIndex(0);
    assertEquals(0, label.getEmptyIndex());
    assertTrue(label.hasEmptyIndex());

    label.setEmptyIndex(4);
    assertEquals(4, label.getEmptyIndex());
    assertTrue(label.hasEmptyIndex());
  }

  @Test
  public void nerConfidenceReturnsStoredMapAndMissingIsNull() {
    CoreLabel label = new CoreLabel();
    assertNull(label.nerConfidence());
    Map<String, Double> scores = new HashMap<>();
    scores.put("PERSON", 0.75);
    scores.put("O", 0.25);
    label.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, scores);

    assertSame(scores, label.nerConfidence());
    assertEquals(Double.valueOf(0.75), label.nerConfidence().get("PERSON"));
    assertEquals(2, label.nerConfidence().size());
  }

  @Test
  public void copyConstructorCopiesAllMappingsButAllowsIndependentReplacement() {
    CoreLabel original = CoreLabel.wordFromString("dogs");
    original.setLemma("dog");
    original.setIndex(2);
    CoreLabel copy = new CoreLabel(original);

    copy.setWord("cats");
    copy.setIndex(8);

    assertEquals("dogs", original.word());
    assertEquals(2, original.index());
    assertEquals("cats", copy.word());
    assertEquals(8, copy.index());
    assertEquals("dog", copy.lemma());
    assertEquals(original.size(), copy.size());
  }

  @Test
  public void coreMapConstructorCopiesHeterogeneousMappings() {
    CoreMap map = new ArrayCoreMap();
    map.set(CoreAnnotations.TextAnnotation.class, "token");
    map.set(CoreAnnotations.IndexAnnotation.class, 11);
    map.set(CoreAnnotations.IsNewlineAnnotation.class, true);
    CoreLabel label = new CoreLabel(map);

    map.set(CoreAnnotations.TextAnnotation.class, "changed");

    assertEquals("token", label.word());
    assertEquals(11, label.index());
    assertEquals(Boolean.TRUE, label.isNewline());
  }

  @Test
  public void labelConstructorFromLegacyLabelCopiesValueAndWordOnly() {
    TaggedWord tagged = new TaggedWord("running", "VBG");
    CoreLabel label = new CoreLabel((Label) tagged);

    assertEquals("running", label.value());
    assertEquals("running", label.word());
    assertNull(label.tag());
    assertEquals(2, label.size());
  }

  @Test
  public void labelConstructorFromCoreLabelCopiesEntireMap() {
    Label source = CoreLabel.wordFromString("Alice");
    ((CoreLabel) source).setNER("PERSON");
    ((CoreLabel) source).setTag("NNP");
    CoreLabel label = new CoreLabel(source);

    assertEquals("Alice", label.value());
    assertEquals("Alice", label.word());
    assertEquals("Alice", label.originalText());
    assertEquals("PERSON", label.ner());
    assertEquals("NNP", label.tag());
  }

  @Test
  public void labelConstructorFromIndexedWordCopiesBackingAnnotations() {
    CoreLabel backing = CoreLabel.wordFromString("node");
    backing.setDocID("graph");
    backing.setSentIndex(3);
    backing.setIndex(5);
    IndexedWord indexed = new IndexedWord(backing);
    CoreLabel label = new CoreLabel((Label) indexed);

    backing.setWord("mutated");

    assertEquals("node", label.word());
    assertEquals("graph", label.docID());
    assertEquals(3, label.sentIndex());
    assertEquals(5, label.index());
  }

  @Test
  public void stringKeyConstructorParsesSupportedScalarTypes() {
    CoreLabel label = new CoreLabel(
        new String[]{"word", "idx", "idf", "is_mwt", "docID"},
        new String[]{"birds", "17", "2.5", "TrUe", "document"});

    assertEquals("birds", label.word());
    assertEquals(17, label.index());
    assertEquals(Double.valueOf(2.5), label.get(CoreAnnotations.IDFAnnotation.class));
    assertEquals(Boolean.TRUE, label.isMWT());
    assertEquals("document", label.docID());
  }

  @Test
  public void classKeyConstructorParsesSupportedValuesAndConlluFeatures() {
    CoreLabel label = new CoreLabel(
        new Class[]{CoreAnnotations.ValueAnnotation.class, CoreAnnotations.IndexAnnotation.class,
            CoreAnnotations.IDFAnnotation.class, CoreAnnotations.IsMultiWordTokenAnnotation.class,
            CoreAnnotations.CoNLLUFeats.class},
        new String[]{"ate", "9", "1.25", "false", "Number=Sing|Tense=Past"});

    assertEquals("ate", label.value());
    assertEquals(9, label.index());
    assertEquals(Double.valueOf(1.25), label.get(CoreAnnotations.IDFAnnotation.class));
    assertEquals(Boolean.FALSE, label.isMWT());
    CoNLLUFeatures features = label.get(CoreAnnotations.CoNLLUFeats.class);
    assertEquals("Sing", features.get("Number"));
    assertEquals("Past", features.get("Tense"));
  }

  @Test
  public void constructorsRejectMismatchedArrayLengths() {
    UnsupportedOperationException byName = assertThrows(UnsupportedOperationException.class,
        () -> new CoreLabel(new String[]{"word", "tag"}, new String[]{"one"}));
    UnsupportedOperationException byClass = assertThrows(UnsupportedOperationException.class,
        () -> new CoreLabel(new Class[]{CoreAnnotations.TextAnnotation.class}, new String[]{}));

    assertTrue(byName.getMessage().contains("Argument array lengths differ"));
    assertTrue(byName.getMessage().contains("word"));
    assertTrue(byClass.getMessage().contains("Argument array lengths differ"));
  }

  @Test
  public void stringKeyConstructorRejectsUnknownKey() {
    UnsupportedOperationException thrown = assertThrows(UnsupportedOperationException.class,
        () -> new CoreLabel(new String[]{"definitely-not-an-annotation"}, new String[]{"x"}));

    assertEquals("Unknown key definitely-not-an-annotation", thrown.getMessage());
  }

  @Test
  public void constructorsWrapMalformedNumbersWithUsefulCause() {
    UnsupportedOperationException byName = assertThrows(UnsupportedOperationException.class,
        () -> new CoreLabel(new String[]{"idx"}, new String[]{"nine"}));
    UnsupportedOperationException byClass = assertThrows(UnsupportedOperationException.class,
        () -> new CoreLabel(new Class[]{CoreAnnotations.IDFAnnotation.class}, new String[]{"high"}));

    assertTrue(byName.getMessage().contains("Bad type for idx"));
    assertTrue(byName.getMessage().contains("nine"));
    assertTrue(byName.getCause() instanceof NumberFormatException);
    assertTrue(byClass.getMessage().contains("IDFAnnotation"));
    assertTrue(byClass.getCause() instanceof NumberFormatException);
  }

  @Test
  public void classKeyConstructorRejectsUnsupportedValueType() {
    UnsupportedOperationException thrown = assertThrows(UnsupportedOperationException.class,
        () -> new CoreLabel(new Class[]{CoreAnnotations.TokensAnnotation.class}, new String[]{"token"}));

    assertTrue(thrown.getMessage().contains("Can't handle"));
    assertTrue(thrown.getMessage().contains("TokensAnnotation"));
  }

  @Test
  public void parseStringKeysResolvesAliasesInOriginalOrder() {
    Class[] parsed = CoreLabel.parseStringKeys(new String[]{"word", "pos", "idx", "morphofeatures"});

    assertArrayEquals(new Class[]{CoreAnnotations.TextAnnotation.class,
        CoreAnnotations.PartOfSpeechAnnotation.class, CoreAnnotations.IndexAnnotation.class,
        CoreAnnotations.CoNLLUFeats.class}, parsed);
  }

  @Test
  public void parseStringKeysRejectsUnknownAndNullKeys() {
    UnsupportedOperationException unknown = assertThrows(UnsupportedOperationException.class,
        () -> CoreLabel.parseStringKeys(new String[]{"word", "?"}));
    UnsupportedOperationException nullKey = assertThrows(UnsupportedOperationException.class,
        () -> CoreLabel.parseStringKeys(new String[]{null}));

    assertEquals("Unknown key ?", unknown.getMessage());
    assertEquals("Unknown key null", nullKey.getMessage());
  }

  @Test
  public void factoryCreatesValueLabelAndIgnoresOptions() {
    LabelFactory factory = CoreLabel.factory();
    Label plain = factory.newLabel("alpha");
    Label optioned = factory.newLabel("beta", 12345);

    assertTrue(plain instanceof CoreLabel);
    assertEquals("alpha", plain.value());
    assertNull(((CoreLabel) plain).word());
    assertTrue(optioned instanceof CoreLabel);
    assertEquals("beta", optioned.value());
  }

  @Test
  public void labelFactoryCopiesLegacyInterfacesIntoCoreLabel() {
    CategoryWordTag source = new CategoryWordTag("NP", "Stanford", "NNP");
    Label result = CoreLabel.factory().newLabel(source);
    CoreLabel label = (CoreLabel) result;

    assertEquals("NP", label.value());
    assertEquals("NP", label.category());
    assertEquals("Stanford", label.word());
    assertEquals("NNP", label.tag());
  }

  @Test
  public void labelFactoryMakesIndependentCopyOfCoreLabel() {
    CoreLabel source = CoreLabel.wordFromString("source");
    source.setIndex(4);
    CoreLabel copy = (CoreLabel) source.labelFactory().newLabel(source);
    copy.setWord("copy");

    assertNotSame(source, copy);
    assertEquals("source", source.word());
    assertEquals("copy", copy.word());
    assertEquals(4, copy.index());
  }

  @Test
  public void unsupportedStringDecodingMethodsThrow() {
    CoreLabel label = new CoreLabel();
    UnsupportedOperationException instanceFailure = assertThrows(UnsupportedOperationException.class,
        () -> label.setFromString("encoded"));
    UnsupportedOperationException factoryFailure = assertThrows(UnsupportedOperationException.class,
        () -> CoreLabel.factory().newLabelFromString("encoded"));

    assertEquals("Cannot set from string", instanceFailure.getMessage());
    assertTrue(factoryFailure.getMessage().contains("do not understand"));
  }

  @Test
  public void valueAndIndexFormatsHandleTagsNerLemmaAndEmptyIndex() {
    CoreLabel label = CoreLabel.wordFromString("ran");
    label.setTag("VBD");
    label.setNER("O");
    label.setLemma("run");
    label.setIndex(6);
    label.setEmptyIndex(2);

    assertEquals("ran-6.2", label.toString());
    assertEquals("ran", label.toString(CoreLabel.OutputFormat.VALUE));
    assertEquals("ran-6.2", label.toString(CoreLabel.OutputFormat.VALUE_INDEX));
    assertEquals("ran/VBD", label.toString(CoreLabel.OutputFormat.VALUE_TAG));
    assertEquals("ran/VBD-6.2", label.toString(CoreLabel.OutputFormat.VALUE_TAG_INDEX));
    assertEquals("ran/VBD/O", label.toString(CoreLabel.OutputFormat.VALUE_TAG_NER));
    assertEquals("run-6.2", label.toString(CoreLabel.OutputFormat.LEMMA_INDEX));
  }

  @Test
  public void formatsOmitOptionalSuffixesWhenAnnotationsAreAbsentOrZero() {
    CoreLabel label = new CoreLabel();
    label.setValue("x");
    label.setWord("w");
    label.setEmptyIndex(0);

    assertEquals("x", label.toString(CoreLabel.OutputFormat.VALUE_INDEX));
    assertEquals("x", label.toString(CoreLabel.OutputFormat.VALUE_TAG));
    assertEquals("x", label.toString(CoreLabel.OutputFormat.VALUE_TAG_INDEX));
    assertEquals("x", label.toString(CoreLabel.OutputFormat.VALUE_TAG_NER));
    assertEquals("w", label.toString(CoreLabel.OutputFormat.WORD));
    assertEquals("w", label.toString(CoreLabel.OutputFormat.WORD_INDEX));
    assertEquals("null", label.toString(CoreLabel.OutputFormat.LEMMA_INDEX));
  }

  @Test
  public void mapFormatsExposeAnnotationsAndRemoveTheirPrefixComponents() {
    CoreLabel label = new CoreLabel();
    label.setValue("v");
    label.setWord("w");
    label.setIndex(3);
    label.setAfter(" ");

    String map = label.toString(CoreLabel.OutputFormat.MAP);
    String valueMap = label.toString(CoreLabel.OutputFormat.VALUE_MAP);
    String valueIndexMap = label.toString(CoreLabel.OutputFormat.VALUE_INDEX_MAP);

    assertTrue(map.contains("CoreAnnotations$AfterAnnotation= "));
    assertTrue(map.contains("CoreAnnotations$IndexAnnotation=3"));
    assertTrue(map.contains("CoreAnnotations$TextAnnotation=w"));
    assertTrue(map.contains("CoreAnnotations$ValueAnnotation=v"));
    assertTrue(valueMap.startsWith("v{"));
    assertFalse(valueMap.contains("ValueAnnotation"));
    assertTrue(valueMap.contains("TextAnnotation"));
    assertTrue(valueIndexMap.startsWith("v-3{"));
    assertFalse(valueIndexMap.contains("IndexAnnotation"));
    assertFalse(valueIndexMap.contains("ValueAnnotation"));
    assertTrue(valueIndexMap.contains("AfterAnnotation= "));
  }

  @Test
  public void valueIndexMapDoesNotAppendEmptyMap() {
    CoreLabel label = new CoreLabel();
    label.setValue("only");
    label.setIndex(1);

    assertEquals("only-1", label.toString(CoreLabel.OutputFormat.VALUE_INDEX_MAP));
  }

  @Test
  public void wordIndexAndAllFormatsReflectCurrentAnnotations() {
    CoreLabel label = new CoreLabel();
    label.setWord("surface");
    label.setIndex(10);
    label.setEmptyIndex(1);
    label.setTag("NN");

    assertEquals("surface-10.1", label.toString(CoreLabel.OutputFormat.WORD_INDEX));
    String all = label.toString(CoreLabel.OutputFormat.ALL);
    assertTrue(all.startsWith(";"));
    assertTrue(all.contains("TextAnnotation:surface"));
    assertTrue(all.contains("IndexAnnotation:10"));
    assertTrue(all.contains("EmptyIndexAnnotation:1"));
    assertTrue(all.contains("PartOfSpeechAnnotation:NN"));
  }
}
