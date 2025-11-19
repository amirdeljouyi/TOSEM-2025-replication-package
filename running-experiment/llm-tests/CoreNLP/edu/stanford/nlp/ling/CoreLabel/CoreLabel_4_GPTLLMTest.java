package edu.stanford.nlp.ling;

import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.trees.ud.CoNLLUFeatures;
import edu.stanford.nlp.util.CoreMap;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

import static org.junit.Assert.*;

public class CoreLabel_4_GPTLLMTest {

 @Test
  public void testSetAndGetWord() {
    CoreLabel label = new CoreLabel();
    label.setWord("NLP");
    assertEquals("NLP", label.word());
  }
@Test
  public void testSetAndGetValue() {
    CoreLabel label = new CoreLabel();
    label.setValue("token");
    assertEquals("token", label.value());
  }
@Test
  public void testSetAndGetTag() {
    CoreLabel label = new CoreLabel();
    label.setTag("NN");
    assertEquals("NN", label.tag());
  }
@Test
  public void testSetAndGetCategory() {
    CoreLabel label = new CoreLabel();
    label.setCategory("Location");
    assertEquals("Location", label.category());
  }
@Test
  public void testSetAndGetLemma() {
    CoreLabel label = new CoreLabel();
    label.setLemma("run");
    assertEquals("run", label.lemma());
  }
@Test
  public void testSetAndGetNER() {
    CoreLabel label = new CoreLabel();
    label.setNER("PERSON");
    assertEquals("PERSON", label.ner());
  }
@Test
  public void testSetAndGetDocID() {
    CoreLabel label = new CoreLabel();
    label.setDocID("doc-001");
    assertEquals("doc-001", label.docID());
  }
@Test
  public void testSetAndGetOriginalText() {
    CoreLabel label = new CoreLabel();
    label.setOriginalText("Original");
    assertEquals("Original", label.originalText());
  }
@Test
  public void testSetAndGetBeforeAfter() {
    CoreLabel label = new CoreLabel();
    label.setBefore(" ");
    label.setAfter(".");
    assertEquals(" ", label.before());
    assertEquals(".", label.after());
  }
@Test
  public void testSetAndGetIndexAndSentIndex() {
    CoreLabel label = new CoreLabel();
    label.setIndex(5);
    label.setSentIndex(2);
    assertEquals(5, label.index());
    assertEquals(2, label.sentIndex());
  }
@Test
  public void testSetAndGetBeginAndEndPositions() {
    CoreLabel label = new CoreLabel();
    label.setBeginPosition(3);
    label.setEndPosition(7);
    assertEquals(3, label.beginPosition());
    assertEquals(7, label.endPosition());
  }
@Test(expected = UnsupportedOperationException.class)
  public void testSetFromStringThrows() {
    CoreLabel label = new CoreLabel();
    label.setFromString("invalid");
  }
@Test
  public void testToStringWithValueIndexFormat() {
    CoreLabel label = new CoreLabel();
    label.setValue("token");
    label.setIndex(10);
    assertEquals("token-10", label.toString(CoreLabel.OutputFormat.VALUE_INDEX));
  }
@Test
  public void testToStringWithValueTagFormat() {
    CoreLabel label = new CoreLabel();
    label.setValue("run");
    label.setTag("VB");
    assertEquals("run/VB", label.toString(CoreLabel.OutputFormat.VALUE_TAG));
  }
@Test
  public void testToStringWithValueTagIndexFormat() {
    CoreLabel label = new CoreLabel();
    label.setValue("jumped");
    label.setTag("VBD");
    label.setIndex(3);
    assertEquals("jumped/VBD-3", label.toString(CoreLabel.OutputFormat.VALUE_TAG_INDEX));
  }
@Test
  public void testToStringWithWordIndexFormat() {
    CoreLabel label = new CoreLabel();
    label.setWord("sky");
    label.setIndex(2);
    assertEquals("sky-2", label.toString(CoreLabel.OutputFormat.WORD_INDEX));
  }
@Test
  public void testToStringWithLemmaIndexFormat() {
    CoreLabel label = new CoreLabel();
    label.setLemma("see");
    label.setIndex(4);
    assertEquals("see-4", label.toString(CoreLabel.OutputFormat.LEMMA_INDEX));
  }
@Test
  public void testToStringWithValueTagNERFormat() {
    CoreLabel label = new CoreLabel();
    label.setValue("Obama");
    label.setTag("NNP");
    label.setNER("PERSON");
    assertEquals("Obama/NNP/PERSON", label.toString(CoreLabel.OutputFormat.VALUE_TAG_NER));
  }
@Test
  public void testToStringWithMapFormat() {
    CoreLabel label = new CoreLabel();
    label.setWord("test");
    String str = label.toString(CoreLabel.OutputFormat.MAP);
    assertTrue(str.contains("TextAnnotation"));
    assertTrue(str.contains("test"));
  }
@Test
  public void testToStringWithAllFormat() {
    CoreLabel label = new CoreLabel();
    label.setWord("science");
    label.setNER("O");
    String str = label.toString(CoreLabel.OutputFormat.ALL);
    assertTrue(str.contains("TextAnnotation"));
    assertTrue(str.contains("NamedEntityTagAnnotation"));
  }
@Test
  public void testEmptyIndexHandling() {
    CoreLabel label = new CoreLabel();
    assertEquals(0, label.getEmptyIndex());
    assertFalse(label.hasEmptyIndex());

    label.setEmptyIndex(2);
    assertEquals(2, label.getEmptyIndex());
    assertTrue(label.hasEmptyIndex());
  }
@Test
  public void testSetMWTFlags() {
    CoreLabel label = new CoreLabel();
    label.setIsMWT(true);
    label.setIsMWTFirst(true);
    assertTrue(label.isMWT());
    assertTrue(label.isMWTFirst());
  }
@Test
  public void testIsNewlineFlag() {
    CoreLabel label = new CoreLabel();
    label.setIsNewline(true);
    assertTrue(label.isNewline());

    label.setIsNewline(false);
    assertFalse(label.isNewline());
  }
@Test
  public void testWordFromStringStaticFactory() {
    CoreLabel label = CoreLabel.wordFromString("test");
    assertEquals("test", label.word());
    assertEquals("test", label.originalText());
    assertEquals("test", label.value());
  }
@Test
  public void testCopyConstructorWithCoreLabel() {
    CoreLabel original = new CoreLabel();
    original.setWord("word");
    original.setTag("TAG");

    CoreLabel copy = new CoreLabel(original);
    assertEquals("word", copy.word());
    assertEquals("TAG", copy.tag());
  }
@Test
  public void testInitFromStringArray() {
    String[] keys = {"Text", "Index", "Tag", "Value"};
    String[] values = {"sky", "1", "NN", "sky"};

    CoreLabel label = new CoreLabel(keys, values);
    assertEquals("sky", label.word());
    assertEquals("NN", label.tag());
    assertEquals(1, label.index());
    assertEquals("sky", label.value());
  }
@Test(expected = UnsupportedOperationException.class)
  public void testInitFromMismatchedLengthArrays() {
    String[] keys = {"Text"};
    String[] values = {"a", "b"};
    new CoreLabel(keys, values);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testInitFromInvalidKey() {
    String[] keys = {"NotARealAnnotation"};
    String[] values = {"value"};
    new CoreLabel(keys, values);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testInitFromInvalidValueType() {
    String[] keys = {"Index"};
    String[] values = {"notAnInt"};
    new CoreLabel(keys, values);
  }
@Test
  public void testNERConfidenceAnnotation() {
    CoreLabel label = new CoreLabel();
    Map<String, Double> probabilities = new HashMap<>();
    probabilities.put("ORG", 0.75);

    label.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, probabilities);
    Map<String, Double> result = label.nerConfidence();

    assertNotNull(result);
    assertEquals(0.75, result.get("ORG"), 0.0001);
  }
@Test
  public void testGetStringWithDefaultFallback() {
    CoreLabel label = new CoreLabel();
    String value = label.getString(CoreAnnotations.BeforeAnnotation.class, "default");
    assertEquals("default", value);

    label.setBefore("prefix");
    String updated = label.getString(CoreAnnotations.BeforeAnnotation.class, "default");
    assertEquals("prefix", updated);
  }
@Test
  public void testLabelFactoryCreatesLabelWithValue() {
    LabelFactory factory = CoreLabel.factory();
    Label label = factory.newLabel("hello world");
    assertEquals("hello world", label.value());
  }
@Test
  public void testLabelFactoryNewLabelFromLegacy() {
    CoreLabel legacy = new CoreLabel();
    legacy.setWord("xyz");
    legacy.setTag("SYM");

    LabelFactory factory = CoreLabel.factory();
    Label clone = factory.newLabel(legacy);

    assertTrue(clone instanceof CoreLabel);
    assertEquals("xyz", ((CoreLabel) clone).word());
    assertEquals("SYM", ((CoreLabel) clone).tag());
  }
@Test(expected = UnsupportedOperationException.class)
  public void testLabelFactoryNewLabelFromStringThrows() {
    LabelFactory factory = CoreLabel.factory();
    factory.newLabelFromString("invalid=string");
  }
@Test(expected = IllegalArgumentException.class)
  public void testToStringWithNullFormatThrows() {
    CoreLabel label = new CoreLabel();
    label.toString(null);
  }
@Test
  public void testConstructorWithIndexedWord() {
    CoreLabel base = new CoreLabel();
    base.setWord("AI");
    base.setTag("NNP");
    IndexedWord iw = new IndexedWord(base);
    CoreLabel constructed = new CoreLabel((Label) iw);
    assertEquals("AI", constructed.word());
    assertEquals("NNP", constructed.tag());
  }
//@Test
//  public void testConstructorWithMinimalLabelHasWord() {
//    Label label = new HasWord() {
//      @Override public String word() { return "wordX"; }
//      @Override public void setWord(String word) {}
//      @Override public String value() { return "valX"; }
//      @Override public void setValue(String value) {}
//    };
//    CoreLabel result = new CoreLabel(label);
//    assertEquals("wordX", result.word());
//    assertEquals("valX", result.value());
//  }
//@Test
//  public void testConstructorWithMinimalLabelOnlyValue() {
//    Label label = new Label() {
//      @Override public String value() { return "staticValue"; }
//      @Override public void setValue(String value) {}
//    };
//    CoreLabel result = new CoreLabel(label);
//    assertEquals("staticValue", result.value());
//    assertNull(result.word());
//  }
@Test
  public void testToStringValueIndexWithEmptyIndex() {
    CoreLabel label = new CoreLabel();
    label.setValue("X");
    label.setIndex(7);
    label.setEmptyIndex(1);
    assertEquals("X-7.1", label.toString(CoreLabel.OutputFormat.VALUE_INDEX));
  }
@Test
  public void testToStringValueTagIndexWithMissingTag() {
    CoreLabel label = new CoreLabel();
    label.setValue("Token");
    label.setIndex(10);
    assertEquals("Token-10", label.toString(CoreLabel.OutputFormat.VALUE_TAG_INDEX));
  }
@Test
  public void testToStringOutputFormatWordWithNullWord() {
    CoreLabel label = new CoreLabel();
    assertNull(label.word());
    assertEquals("null", label.toString(CoreLabel.OutputFormat.WORD));
  }
@Test
  public void testToStringMapIncludesMultipleKeysOrdered() {
    CoreLabel label = new CoreLabel();
    label.setWord("w1");
    label.setTag("NN");
    String result = label.toString(CoreLabel.OutputFormat.MAP);
    assertTrue(result.contains("TextAnnotation"));
    assertTrue(result.contains("PartOfSpeechAnnotation"));
  }
@Test
  public void testToStringValueMapExcludesValueKey() {
    CoreLabel label = new CoreLabel();
    label.setValue("house");
    label.setNER("LOCATION");
    String value = label.toString(CoreLabel.OutputFormat.VALUE_MAP);
    assertTrue(value.startsWith("house"));
    assertFalse(value.contains("ValueAnnotation"));
  }
@Test
  public void testToStringValueIndexMapExcludesAnnotations() {
    CoreLabel label = new CoreLabel();
    label.setValue("rain");
    label.setIndex(4);
    label.setNER("O");
    String out = label.toString(CoreLabel.OutputFormat.VALUE_INDEX_MAP);
    assertTrue(out.contains("rain-4"));
    assertFalse(out.contains("ValueAnnotation"));
    assertFalse(out.contains("IndexAnnotation"));
    assertTrue(out.contains("NamedEntityTagAnnotation"));
  }
@Test
  public void testToStringAllContainsAllAnnotations() {
    CoreLabel label = new CoreLabel();
    label.setValue("car");
    label.setLemma("vehicle");
    label.setTag("NN");
    String output = label.toString(CoreLabel.OutputFormat.ALL);
    assertTrue(output.contains("ValueAnnotation"));
    assertTrue(output.contains("LemmaAnnotation"));
    assertTrue(output.contains("PartOfSpeechAnnotation"));
  }
@Test
  public void testParseStringKeysValidKeys() {
    String[] keys = {"Tag", "Text"};
    Class<?>[] results = CoreLabel.parseStringKeys(keys);
    assertEquals(CoreAnnotations.PartOfSpeechAnnotation.class, results[0]);
    assertEquals(CoreAnnotations.TextAnnotation.class, results[1]);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testParseStringKeysInvalidKey() {
    String[] keys = {"FakeAnnotation"};
    CoreLabel.parseStringKeys(keys);
  }
@Test
  public void testInitFromClassKeyWithBooleanValue() {
    Class<?>[] keys = {CoreAnnotations.IsNewlineAnnotation.class};
    String[] values = {"true"};
    CoreLabel label = new CoreLabel(keys, values);
    assertTrue(label.isNewline());
  }
//@Test
//  public void testInitFromClassKeyWithDoubleValue() {
//    Class<?> dummyKey = DummyDoubleAnnotation.class;
//    Class<?>[] keys = {dummyKey};
//    String[] values = {"2.718"};
//    try {
//      CoreLabel label = new CoreLabel(keys, values);
//      Double result = label.get(DummyDoubleAnnotation.class);
//      assertEquals(2.718, result, 0.001);
//    } catch (UnsupportedOperationException e) {
//
//      assertTrue(e.getMessage().contains("Can't handle"));
//    }
//  }
@Test
  public void testCloneConstructorPreservesAllFields() {
    CoreLabel original = new CoreLabel();
    original.setWord("cloneTest");
    original.setTag("NN");
    original.setNER("LOC");
    original.setIndex(9);
    CoreLabel copy = new CoreLabel(original);
    assertEquals("cloneTest", copy.word());
    assertEquals("NN", copy.tag());
    assertEquals("LOC", copy.ner());
    assertEquals(9, copy.index());
  }
@Test
  public void testToStringValueWhenNull() {
    CoreLabel label = new CoreLabel();
    String result = label.toString(CoreLabel.OutputFormat.VALUE);
    assertEquals("null", result);
  }
@Test
  public void testToStringValueIndexWithNullIndex() {
    CoreLabel label = new CoreLabel();
    label.setValue("hello");
    String result = label.toString(CoreLabel.OutputFormat.VALUE_INDEX);
    assertEquals("hello", result);
  }
@Test
  public void testToStringValueTagWithNullTag() {
    CoreLabel label = new CoreLabel();
    label.setValue("run");
    String result = label.toString(CoreLabel.OutputFormat.VALUE_TAG);
    assertEquals("run", result);
  }
@Test
  public void testToStringWordIndexWithNullIndex() {
    CoreLabel label = new CoreLabel();
    label.setWord("token");
    String result = label.toString(CoreLabel.OutputFormat.WORD_INDEX);
    assertEquals("token", result);
  }
@Test
  public void testToStringValueTagIndexWithEmptyIndexZeroSuppressed() {
    CoreLabel label = new CoreLabel();
    label.setValue("abc");
    label.setTag("JJ");
    label.setIndex(2);
    label.setEmptyIndex(0);
    String result = label.toString(CoreLabel.OutputFormat.VALUE_TAG_INDEX);
    assertEquals("abc/JJ-2", result);
  }
@Test
  public void testToStringValueIndexMapWithEmptyIndexNonZeroIncluded() {
    CoreLabel label = new CoreLabel();
    label.setValue("datapoint");
    label.setIndex(5);
    label.setEmptyIndex(3);
    label.setTag("NN");
    String result = label.toString(CoreLabel.OutputFormat.VALUE_INDEX_MAP);
    assertTrue(result.startsWith("datapoint-5.3"));
    assertTrue(result.contains("PartOfSpeechAnnotation"));
  }
@Test
  public void testToStringAllWithNoAnnotations() {
    CoreLabel label = new CoreLabel();
    String result = label.toString(CoreLabel.OutputFormat.ALL);
    assertEquals("", result);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testInitFromUnknownGenericAnnotationKeyFails() {
    String[] keys = {"DummyAnnotation"};
    String[] values = {"100"};
    CoreLabel label = new CoreLabel(keys, values);
  }
@Test
  public void testGetStringWithDefaultWhenPresentReturnsValue() {
    CoreLabel label = new CoreLabel();
    label.set(CoreAnnotations.BeforeAnnotation.class, "beforeText");
    String out = label.getString(CoreAnnotations.BeforeAnnotation.class, "defaultValue");
    assertEquals("beforeText", out);
  }
@Test
  public void testNERConfidenceReturnsNullWhenMissing() {
    CoreLabel label = new CoreLabel();
    Map<String, Double> map = label.nerConfidence();
    assertNull(map);
  }
@Test
  public void testSetAndGetBooleanFalseValues() {
    CoreLabel label = new CoreLabel();
    label.setIsNewline(false);
    label.setIsMWT(false);
    label.setIsMWTFirst(false);
    assertFalse(label.isNewline());
    assertFalse(label.isMWT());
    assertFalse(label.isMWTFirst());
  }
@Test
  public void testSetIndexNegativeValueAccepted() {
    CoreLabel label = new CoreLabel();
    label.setIndex(-5);
    assertEquals(-5, label.index());
  }
@Test
  public void testSetSentIndexNegativeAccepted() {
    CoreLabel label = new CoreLabel();
    label.setSentIndex(-2);
    assertEquals(-2, label.sentIndex());
  }
@Test
  public void testBeginEndPositionDefaultFallback() {
    CoreLabel label = new CoreLabel();
    assertEquals(-1, label.beginPosition());
    assertEquals(-1, label.endPosition());
  }
@Test
  public void testSetEmptyIndexZeroEdgeCase() {
    CoreLabel label = new CoreLabel();
    label.setEmptyIndex(0);
    assertEquals(0, label.getEmptyIndex());
    assertTrue(label.hasEmptyIndex());
  }
//@Test
//  public void testSetAndGetCoNLLUFeatures() {
//    CoreLabel label = new CoreLabel();
//    CoNLLUFeatures feats = new CoNLLUFeatures("Gender=Masc|Number=Sing");
//    label.set(CoAnnotations.CoNLLUFeats.class, feats);
//    CoNLLUFeatures output = label.get(CoAnnotations.CoNLLUFeats.class);
//    assertEquals("Gender=Masc|Number=Sing", output.toString());
//  }
@Test
  public void testSetAndGetUnknownAnnotationTypeThrows() {
    class UnknownAnnotation implements CoreAnnotation<Object> {
      @Override
      public Class<Object> getType() {
        return Object.class;
      }
    }

    CoreLabel label = new CoreLabel();
    label.set(UnknownAnnotation.class, new Object());
    Object value = label.get(UnknownAnnotation.class);
    assertNotNull(value);
  }
@Test
  public void testFactoryCreatesLabelWithNullValue() {
    Label label = CoreLabel.factory().newLabel((String) null);
    assertNull(label.value());
  }
//@Test
//  public void testFactoryWrapsNullLabel() {
//    class NullLabel implements Label {
//      @Override public String value() { return null; }
//      @Override public void setValue(String value) { }
//    }
//
//    CoreLabel result = new CoreLabel(new NullLabel());
//    assertNull(result.value());
//  }
@Test
  public void testToStringValueTagNERMissingNER() {
    CoreLabel label = new CoreLabel();
    label.setValue("dragon");
    label.setTag("NN");
    String result = label.toString(CoreLabel.OutputFormat.VALUE_TAG_NER);
    assertEquals("dragon/NN", result);
  }
@Test
  public void testToStringValueTagNERMissingTagAndNER() {
    CoreLabel label = new CoreLabel();
    label.setValue("dragon");
    String result = label.toString(CoreLabel.OutputFormat.VALUE_TAG_NER);
    assertEquals("dragon", result);
  }
@Test
  public void testToStringWordIndexWithEmptyIndexOnly() {
    CoreLabel label = new CoreLabel();
    label.setWord("hi");
    label.setEmptyIndex(4);
    String result = label.toString(CoreLabel.OutputFormat.WORD_INDEX);
    assertEquals("hi.4", result);
  }
//@Test
//  public void testFactoryNewLabelWrapsLegacyInterfaces() {
//    Label label = new Label() {
//      @Override public String value() { return "wrappedValue"; }
//      @Override public void setValue(String value) {}
//    };
//
//    CoreLabel result = (CoreLabel) CoreLabel.factory().newLabel(label);
//    assertEquals("wrappedValue", result.value());
//  }
//@Test
//  public void testFactoryNewLabelWithAllLegacyInterfaces() {
//    Label label = new Label() implements HasWord, HasTag, HasCategory, HasIndex {
//      @Override public String value() { return "legacyVal"; }
//      @Override public void setValue(String value) {}
//      @Override public String word() { return "wordX"; }
//      @Override public void setWord(String word) {}
//      @Override public String tag() { return "TAGX"; }
//      @Override public void setTag(String tag) {}
//      @Override public String category() { return "catX"; }
//      @Override public void setCategory(String category) {}
//      @Override public int index() { return 42; }
//      @Override public void setIndex(int index) {}
//    };
//
//    CoreLabel result = (CoreLabel) CoreLabel.factory().newLabel(label);
//    assertEquals("wordX", result.word());
//    assertEquals("TAGX", result.tag());
//    assertEquals("catX", result.category());
//    assertEquals(42, result.index());
//    assertEquals("legacyVal", result.value());
//  }
@Test
  public void testEmptyIndexUnsetStillReturnsFalse() {
    CoreLabel label = new CoreLabel();
    assertFalse(label.hasEmptyIndex());
  }
@Test
  public void testToStringValueIndexWithOnlyEmptyIndex() {
    CoreLabel label = new CoreLabel();
    label.setValue("Y");
    label.setEmptyIndex(7);
    String result = label.toString(CoreLabel.OutputFormat.VALUE_INDEX);
    assertEquals("Y.7", result);
  }
@Test
  public void testToStringWordIndexEmptyIndexOnly() {
    CoreLabel label = new CoreLabel();
    label.setWord("w");
    label.setEmptyIndex(5);
    String str = label.toString(CoreLabel.OutputFormat.WORD_INDEX);
    assertEquals("w.5", str);
  }
@Test
  public void testToStringLemmaIndexWithOnlyEmptyIndex() {
    CoreLabel label = new CoreLabel();
    label.setLemma("go");
    label.setEmptyIndex(6);
    String out = label.toString(CoreLabel.OutputFormat.LEMMA_INDEX);
    assertEquals("go.6", out);
  }
@Test
  public void testToStringAllWithMultipleAnnotations() {
    CoreLabel label = new CoreLabel();
    label.setWord("wordZ");
    label.setNER("DATE");
    label.setDocID("doc42");
    String str = label.toString(CoreLabel.OutputFormat.ALL);
    assertTrue(str.contains("TextAnnotation"));
    assertTrue(str.contains("NamedEntityTagAnnotation"));
    assertTrue(str.contains("DocIDAnnotation"));
  }
@Test
  public void testSetAndGetAfterAnnotationWithNullFallback() {
    CoreLabel label = new CoreLabel();
    String result = label.getString(CoreAnnotations.AfterAnnotation.class, "defaultAfter");
    assertEquals("defaultAfter", result);
  }
@Test
  public void testNERConfidenceWithMultipleValues() {
    CoreLabel label = new CoreLabel();
    Map<String, Double> map = new HashMap<>();
    map.put("PERSON", 0.91);
    map.put("LOCATION", 0.03);
    label.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, map);
    Map<String, Double> read = label.nerConfidence();
    assertEquals(2, read.size());
    assertEquals(0.91, read.get("PERSON"), 0.001);
    assertEquals(0.03, read.get("LOCATION"), 0.001);
  }
@Test
  public void testSetThenOverwriteTagValue() {
    CoreLabel label = new CoreLabel();
    label.setTag("VB");
    assertEquals("VB", label.tag());
    label.setTag("VBD");
    assertEquals("VBD", label.tag());
  }
@Test
  public void testSetAndGetUnknownAnnotationSubclass() {
    class CustomAnnotation implements CoreAnnotation<String> {
      public Class<String> getType() { return String.class; }
    }
    CoreLabel label = new CoreLabel();
    label.set(CustomAnnotation.class, "customValue");
    String result = label.get(CustomAnnotation.class);
    assertEquals("customValue", result);
  }
@Test
  public void testToStringValueMapWithMultipleValues() {
    CoreLabel label = new CoreLabel();
    label.setValue("car");
    label.setNER("VEHICLE");
    label.setTag("NN");
    String output = label.toString(CoreLabel.OutputFormat.VALUE_MAP);
    assertTrue(output.startsWith("car"));
    assertTrue(output.contains("NamedEntityTagAnnotation"));
    assertTrue(output.contains("PartOfSpeechAnnotation"));
    assertFalse(output.contains("ValueAnnotation"));
  }
@Test
  public void testToStringValueIndexMapKeyShortening() {
    CoreLabel label = new CoreLabel();
    label.setValue("X");
    label.setIndex(1);
    label.setNER("ENTITY");
    String output = label.toString(CoreLabel.OutputFormat.VALUE_INDEX_MAP);
    assertTrue(output.startsWith("X-1"));
    assertTrue(output.contains("NamedEntityTagAnnotation"));
    assertFalse(output.contains("IndexAnnotation"));
    assertFalse(output.contains("ValueAnnotation"));
  }
//@Test(expected = UnsupportedOperationException.class)
//  public void testInitFromStringsFailsOnDoubleWithWrongFormat() {
//    class DoubleAnn implements CoreAnnotation<Double> {
//      public Class<Double> getType() { return Double.class; }
//    }
//    CoreLabel.genericKeys.put("DoubleAnn", DoubleAnn.class);
//    CoreLabel.genericValues.put(DoubleAnn.class, "DoubleAnn");
//    String[] keys = { "DoubleAnn" };
//    String[] values = { "not_a_double" };
//    CoreLabel label = new CoreLabel(keys, values);
//  }
@Test
  public void testToStringOutputFormatALLEmptyReturnsEmptyString() {
    CoreLabel label = new CoreLabel();
    String result = label.toString(CoreLabel.OutputFormat.ALL);
    assertEquals("", result);
  }
@Test
  public void testSetWordOverridesPreviousWord() {
    CoreLabel label = new CoreLabel();
    label.setWord("initial");
    assertEquals("initial", label.word());
    label.setWord("updated");
    assertEquals("updated", label.word());
  }
@Test
  public void testSetOriginalTextOverridesPrevious() {
    CoreLabel label = new CoreLabel();
    label.setOriginalText("orig1");
    assertEquals("orig1", label.originalText());
    label.setOriginalText("orig2");
    assertEquals("orig2", label.originalText());
  }
@Test
  public void testSetNEROverridesPrevious() {
    CoreLabel label = new CoreLabel();
    label.setNER("LOCATION");
    assertEquals("LOCATION", label.ner());
    label.setNER("ORG");
    assertEquals("ORG", label.ner());
  }
@Test
  public void testToStringValueTagNERNoTagButWithNER() {
    CoreLabel label = new CoreLabel();
    label.setValue("item");
    label.setNER("PRODUCT");
    String result = label.toString(CoreLabel.OutputFormat.VALUE_TAG_NER);
    assertEquals("item/PRODUCT", result);
  }
@Test
  public void testToStringValueTagNERAllNulls() {
    CoreLabel label = new CoreLabel();
    String result = label.toString(CoreLabel.OutputFormat.VALUE_TAG_NER);
    assertEquals("null", result);
  }
@Test
  public void testToStringWordNullReturnsNullString() {
    CoreLabel label = new CoreLabel();
    String result = label.toString(CoreLabel.OutputFormat.WORD);
    assertEquals("null", result);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testInitFromStringsWithNullKeyThrows() {
    String[] keys = { null };
    String[] values = { "value" };
    new CoreLabel(keys, values);
  }
@Test
  public void testSetAndGetNullableFieldsWithNullValues() {
    CoreLabel label = new CoreLabel();
    label.setValue(null);
    label.setWord(null);
    label.setTag(null);
    label.setNER(null);
    label.setLemma(null);
    label.setDocID(null);

    assertNull(label.value());
    assertNull(label.word());
    assertNull(label.tag());
    assertNull(label.ner());
    assertNull(label.lemma());
    assertNull(label.docID());
  }
@Test
  public void testConstructorWithCapacityZero() {
    CoreLabel label = new CoreLabel(0);
    assertNotNull(label);
    label.setWord("token");
    assertEquals("token", label.word());
  }
@Test
  public void testConstructorWithCoreMapContainingMultipleAnnotations() {
    CoreLabel original = new CoreLabel();
    original.setWord("origin");
    original.setNER("PERSON");
    original.setIndex(100);
    CoreMap copyMap = original;
    CoreLabel copy = new CoreLabel(copyMap);
    assertEquals("origin", copy.word());
    assertEquals("PERSON", copy.ner());
    assertEquals(100, copy.index());
  }
@Test(expected = UnsupportedOperationException.class)
  public void testToStringInvalidEnumThrows() {
    CoreLabel label = new CoreLabel();
    label.toString(CoreLabel.OutputFormat.valueOf("NON_EXISTENT"));
  }
@Test
  public void testGetStringReturnsEmptyWhenNullValueAndNoDefault() {
    CoreLabel label = new CoreLabel();
    String result = label.getString(CoreAnnotations.AfterAnnotation.class);
    assertEquals("", result);
  }
@Test
  public void testGetStringReturnsDefaultWhenNullStored() {
    CoreLabel label = new CoreLabel();
    String result = label.getString(CoreAnnotations.BeforeAnnotation.class, "customDefault");
    assertEquals("customDefault", result);
  }
@Test
  public void testSetGetIsNewlineExplicitFalse() {
    CoreLabel label = new CoreLabel();
    label.setIsNewline(false);
    assertFalse(label.isNewline());
  }
@Test
  public void testHasEmptyIndexPresenceToggle() {
    CoreLabel label = new CoreLabel();
    assertFalse(label.hasEmptyIndex());
    label.setEmptyIndex(1);
    assertTrue(label.hasEmptyIndex());
    label.setEmptyIndex(0);
    assertTrue(label.hasEmptyIndex());
  }
@Test
  public void testSetAndGetBooleanTrueAndFalse() {
    CoreLabel label = new CoreLabel();
    label.setIsMWT(true);
    label.setIsMWTFirst(true);
    assertTrue(label.isMWT());
    assertTrue(label.isMWTFirst());

    label.setIsMWT(false);
    label.setIsMWTFirst(false);
    assertFalse(label.isMWT());
    assertFalse(label.isMWTFirst());
  }
@Test
  public void testIndexAndSentIndexUnsetDefaults() {
    CoreLabel label = new CoreLabel();
    assertEquals(-1, label.index());
    assertEquals(-1, label.sentIndex());
  }
@Test
  public void testSetNegativeBeginEndPosition() {
    CoreLabel label = new CoreLabel();
    label.setBeginPosition(-10);
    label.setEndPosition(-5);
    assertEquals(-10, label.beginPosition());
    assertEquals(-5, label.endPosition());
  }
@Test
  public void testCoreLabelFactoryCreatesInstanceWithValue() {
    LabelFactory factory = CoreLabel.factory();
    Label label = factory.newLabel("factoryText");
    assertTrue(label instanceof CoreLabel);
    assertEquals("factoryText", label.value());
  }
@Test(expected = UnsupportedOperationException.class)
  public void testFactoryNewLabelFromStringUnsupported() {
    LabelFactory factory = CoreLabel.factory();
    factory.newLabelFromString("not_supported");
  }
@Test
  public void testToStringMapContainsAllEntriesAlphabetically() {
    CoreLabel label = new CoreLabel();
    label.setWord("alpha");
    label.setTag("TAG");
    label.setNER("NER");
    String result = label.toString(CoreLabel.OutputFormat.MAP);
    assertTrue(result.contains("TextAnnotation"));
    assertTrue(result.contains("PartOfSpeechAnnotation"));
    assertTrue(result.contains("NamedEntityTagAnnotation"));
  }
@Test
  public void testToStringValueMapEliminatesCoreValue() {
    CoreLabel label = new CoreLabel();
    label.setValue("core");
    label.setTag("VBZ");
    String result = label.toString(CoreLabel.OutputFormat.VALUE_MAP);
    assertTrue(result.startsWith("core"));
    assertFalse(result.contains("ValueAnnotation"));
    assertTrue(result.contains("PartOfSpeechAnnotation"));
  }
@Test
  public void testToStringValueIndexMapExcludesIndexAnnotation() {
    CoreLabel label = new CoreLabel();
    label.setValue("x");
    label.setIndex(99);
    label.setNER("N");
    String result = label.toString(CoreLabel.OutputFormat.VALUE_INDEX_MAP);
    assertTrue(result.startsWith("x-99"));
    assertFalse(result.contains("IndexAnnotation"));
    assertFalse(result.contains("ValueAnnotation"));
  }
//@Test
//  public void testConstructorWithLabelOnlyHasValueField() {
//    Label label = new Label() {
//      @Override public String value() { return "onlyValue"; }
//      @Override public void setValue(String value) {}
//    };
//    CoreLabel result = new CoreLabel(label);
//    assertEquals("onlyValue", result.value());
//    assertNull(result.word());
//  }
@Test
  public void testConstructorWithIndexedWordAndBackingLabel() {
    CoreLabel backing = new CoreLabel();
    backing.setWord("backed");
    backing.setTag("NN");
    IndexedWord iw = new IndexedWord(backing);
    CoreLabel copy = new CoreLabel(iw);
    assertEquals("backed", copy.word());
    assertEquals("NN", copy.tag());
  }
@Test
  public void testInitFromStringsParsesIntegerCorrectly() {
    String[] keys = { "Index" };
    String[] values = { "123" };
    CoreLabel label = new CoreLabel(keys, values);
    assertEquals(123, label.index());
  }
@Test(expected = UnsupportedOperationException.class)
  public void testInitFromStringsMismatchedLengthThrows() {
    String[] keys = { "Index", "Text" };
    String[] values = { "1" };
    new CoreLabel(keys, values);
  }
@Test
  public void testInitFromClassArrayParsesBooleanCorrectly() {
    Class[] keys = { CoreAnnotations.IsNewlineAnnotation.class };
    String[] values = { "true" };
    CoreLabel label = new CoreLabel(keys, values);
    assertTrue(label.isNewline());
  }
@Test
  public void testInitFromClassArrayInvalidIntegerThrows() {
    Class[] keys = { CoreAnnotations.IndexAnnotation.class };
    String[] values = { "abc" };
    try {
      new CoreLabel(keys, values);
      fail("Expected UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      assertTrue(e.getMessage().contains("Bad type for"));
    }
  }
@Test
  public void testToStringWithAllOutputFormatIncludesAllKeys() {
    CoreLabel label = new CoreLabel();
    label.setWord("sample");
    label.setNER("O");
    label.setIndex(1);
    label.setValue("sample");
    String result = label.toString(CoreLabel.OutputFormat.ALL);
    assertTrue(result.contains("TextAnnotation"));
    assertTrue(result.contains("NamedEntityTagAnnotation"));
    assertTrue(result.contains("IndexAnnotation"));
    assertTrue(result.contains("ValueAnnotation"));
  }
//@Test
//  public void testToStringMapWithUnusualAnnotationKey() {
//    CoreLabel label = new CoreLabel();
//    class StrangeAnnotation implements CoreAnnotations.GenericAnnotation<String> {
//      public Class<String> getType() { return String.class; }
//    }
//    label.set(StrangeAnnotation.class, "xyz");
//    String output = label.toString(CoreLabel.OutputFormat.MAP);
//    assertTrue(output.contains("StrangeAnnotation"));
//  }
@Test
  public void testSetAndGetIsMWTAndIsMWTFirstFalseByDefault() {
    CoreLabel label = new CoreLabel();
    assertNull(label.isMWT());
    assertNull(label.isMWTFirst());
  }
@Test
  public void testIndexAnnotationUnsetReturnsNegativeOne() {
    CoreLabel label = new CoreLabel();
    assertEquals(-1, label.index());
  }
@Test
  public void testSetEmptyIndexZeroStillCountsAsPresent() {
    CoreLabel label = new CoreLabel();
    label.setEmptyIndex(0);
    assertEquals(0, label.getEmptyIndex());
    assertTrue(label.hasEmptyIndex());
  }
@Test
  public void testCopyConstructorClonesAllFields() {
    CoreLabel original = new CoreLabel();
    original.setWord("text");
    original.setNER("MONEY");
    original.setTag("NN");
    original.setLemma("lex");
    original.setIndex(42);
    original.setOriginalText("raw");
    CoreLabel clone = new CoreLabel(original);
    assertEquals("text", clone.word());
    assertEquals("MONEY", clone.ner());
    assertEquals("NN", clone.tag());
    assertEquals("lex", clone.lemma());
    assertEquals(42, clone.index());
    assertEquals("raw", clone.originalText());
  }
@Test
  public void testFactoryCreatesLabelFromStringWithNull() {
    Label result = CoreLabel.factory().newLabel((String)null);
    assertNull(result.value());
  }
@Test
  public void testSetNERConfidenceMapWithEmptyProbs() {
    CoreLabel label = new CoreLabel();
    Map<String, Double> emptyMap = new HashMap<>();
    label.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, emptyMap);
    Map<String, Double> returned = label.nerConfidence();
    assertNotNull(returned);
    assertTrue(returned.isEmpty());
  }
@Test
  public void testSetAndGetAfterAnnotationWithEmptyString() {
    CoreLabel label = new CoreLabel();
    label.setAfter("");
    assertEquals("", label.after());
  }
@Test
  public void testSetAndGetBeforeAnnotationWithWhitespace() {
    CoreLabel label = new CoreLabel();
    label.setBefore("    ");
    assertEquals("    ", label.before());
  }
@Test
  public void testToStringValueWhenOnlyEmptyIndexPresent() {
    CoreLabel label = new CoreLabel();
    label.setValue("form");
    label.setEmptyIndex(5);
    String result = label.toString(CoreLabel.OutputFormat.VALUE_INDEX);
    assertEquals("form.5", result);
  }
@Test
  public void testToStringLemmaIndexWithNoIndexOrEmptyIndex() {
    CoreLabel label = new CoreLabel();
    label.setLemma("lemmaText");
    String result = label.toString(CoreLabel.OutputFormat.LEMMA_INDEX);
    assertEquals("lemmaText", result);
  }
@Test
  public void testToStringValueMapExcludesIndexAndValueAnnotationKeys() {
    CoreLabel label = new CoreLabel();
    label.setValue("text");
    label.setIndex(101);
    label.setNER("ENTITY");
    String result = label.toString(CoreLabel.OutputFormat.VALUE_INDEX_MAP);
    assertTrue(result.contains("NamedEntityTagAnnotation"));
    assertFalse(result.contains("IndexAnnotation"));
    assertFalse(result.contains("ValueAnnotation"));
  }
@Test
  public void testToStringReturnsMapSortedByClassName() {
    CoreLabel label = new CoreLabel();
    label.setWord("zoo");
    label.setNER("O");
    label.setTag("NN");
    String output = label.toString(CoreLabel.OutputFormat.MAP);
    int idxText = output.indexOf("TextAnnotation");
    int idxNER = output.indexOf("NamedEntityTagAnnotation");
    int idxTag = output.indexOf("PartOfSpeechAnnotation");
    assertTrue(idxNER < idxTag);
    assertTrue(idxTag < idxText || idxText < idxNER); 
  }
@Test
  public void testToStringWordIndexWhenBothIndexesPresent() {
    CoreLabel label = new CoreLabel();
    label.setWord("run");
    label.setIndex(3);
    label.setEmptyIndex(1);
    String result = label.toString(CoreLabel.OutputFormat.WORD_INDEX);
    assertEquals("run-3.1", result);
  }
@Test
  public void testSetFromStringExplicitlyThrows() {
    CoreLabel label = new CoreLabel();
    try {
      label.setFromString("anyString");
      fail("Expected UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      assertEquals("Cannot set from string", e.getMessage());
    }
  }
@Test
  public void testToStringDefaultUsesValueIndex() {
    CoreLabel label = new CoreLabel();
    label.setValue("default");
    label.setIndex(9);
    assertEquals("default-9", label.toString());
  }
@Test
  public void testConstructorWithCoreLabelPreservesAllKeys() {
    CoreLabel original = new CoreLabel();
    original.setValue("copied");
    original.setNER("O");
    original.setIndex(7);
    original.setOriginalText("copied");
    CoreLabel constructed = new CoreLabel(original);
    assertEquals("copied", constructed.value());
    assertEquals("O", constructed.ner());
    assertEquals(7, constructed.index());
    assertEquals("copied", constructed.originalText());
  }
//@Test
//  public void testConstructorWithLabelOnlyHasWord() {
//    Label label = new Label() implements edu.stanford.nlp.ling.HasWord {
//      @Override public String value() { return "labelValue"; }
//      @Override public void setValue(String value) {}
//      @Override public String word() { return "labelWord"; }
//      @Override public void setWord(String word) {}
//    };
//    CoreLabel result = new CoreLabel(label);
//    assertEquals("labelValue", result.value());
//    assertEquals("labelWord", result.word());
//  }
@Test
  public void testToStringValueIndexWhenIndexIsZero() {
    CoreLabel label = new CoreLabel();
    label.setValue("zero");
    label.setIndex(0);
    assertEquals("zero-0", label.toString(CoreLabel.OutputFormat.VALUE_INDEX));
  }
@Test
  public void testToStringWordIndexHasEmptyIndexOnly() {
    CoreLabel label = new CoreLabel();
    label.setWord("hello");
    label.setEmptyIndex(5);
    assertEquals("hello.5", label.toString(CoreLabel.OutputFormat.WORD_INDEX));
  }
@Test
  public void testToStringValueTagIndexMissingTag() {
    CoreLabel label = new CoreLabel();
    label.setValue("missing");
    label.setIndex(2);
    assertEquals("missing-2", label.toString(CoreLabel.OutputFormat.VALUE_TAG_INDEX));
  }
@Test(expected = IllegalArgumentException.class)
  public void testToStringWithUnknownOutputFormat() {
    CoreLabel.OutputFormat unknown = CoreLabel.OutputFormat.valueOf("ALL"); 
    CoreLabel label = new CoreLabel();
    label.toString(null); 
  }
@Test
  public void testGetStringWithNullStoredValueReturnsDefault() {
    CoreLabel label = new CoreLabel();
    String result = label.getString(CoreAnnotations.DocIDAnnotation.class, "defaultDoc");
    assertEquals("defaultDoc", result);
  }
@Test
  public void testEmptyIndexRemovedStillReturnsTrue() {
    CoreLabel label = new CoreLabel();
    label.setEmptyIndex(1);
    Integer value = label.get(CoreAnnotations.EmptyIndexAnnotation.class);
    assertEquals(Integer.valueOf(1), value);
    assertTrue(label.hasEmptyIndex());
    label.set(CoreAnnotations.EmptyIndexAnnotation.class, null);
    assertFalse(label.hasEmptyIndex());
  }
@Test
  public void testNERConfidenceReturnsNullIfUnset() {
    CoreLabel label = new CoreLabel();
    assertNull(label.nerConfidence());
  }
@Test
  public void testNERConfidenceReturnsNonEmptyProbabilities() {
    CoreLabel label = new CoreLabel();
    Map<String, Double> map = new HashMap<>();
    map.put("PER", 0.9);
    label.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, map);
    assertEquals(0.9, label.nerConfidence().get("PER"), 0.0001);
  }
@Test
  public void testToStringValueMapRemovesValueAnnotation() {
    CoreLabel label = new CoreLabel();
    label.setValue("token");
    label.setNER("LOCATION");
    String result = label.toString(CoreLabel.OutputFormat.VALUE_MAP);
    assertTrue(result.startsWith("token"));
    assertFalse(result.contains("ValueAnnotation"));
    assertTrue(result.contains("NamedEntityTagAnnotation"));
  }
@Test
  public void testToStringValueIndexMapRemovesIndexAndValueAnnotation() {
    CoreLabel label = new CoreLabel();
    label.setValue("word");
    label.setIndex(3);
    label.setNER("ENTITY");
    String output = label.toString(CoreLabel.OutputFormat.VALUE_INDEX_MAP);
    assertTrue(output.startsWith("word-3"));
    assertFalse(output.contains("IndexAnnotation"));
    assertFalse(output.contains("ValueAnnotation"));
    assertTrue(output.contains("NamedEntityTagAnnotation"));
  }
@Test
  public void testSetAndGetCategoryRoundTrip() {
    CoreLabel label = new CoreLabel();
    label.setCategory("categoryX");
    assertEquals("categoryX", label.category());
    label.setCategory(null);
    assertNull(label.category());
  }
@Test
  public void testSetOriginalTextIsDistinctFromWordSetter() {
    CoreLabel label = new CoreLabel();
    label.setWord("base");
    label.setOriginalText("rawText");
    assertEquals("base", label.word());
    assertEquals("rawText", label.originalText());
  }
//@Test
//  public void testInitFromClassParsesLongCorrectly() {
//    class DummyLongAnnotation implements CoreAnnotations.GenericAnnotation<Long> {
//      @Override public Class<Long> getType() { return Long.class; }
//    }
//    CoreLabel.genericKeys.put("DummyLongAnnotation", DummyLongAnnotation.class);
//    CoreLabel.genericValues.put(DummyLongAnnotation.class, "DummyLongAnnotation");
//    String[] keys = { "DummyLongAnnotation" };
//    String[] values = { "1234567890" };
//    CoreLabel label = new CoreLabel(keys, values);
//    Long parsed = label.get(DummyLongAnnotation.class);
//    assertEquals(Long.valueOf(1234567890L), parsed);
//  }
//@Test
//  public void testInitFromStringsDoubleParseSuccess() {
//    class CustomDoubleAnnotation implements CoreAnnotations.GenericAnnotation<Double> {
//      @Override public Class<Double> getType() { return Double.class; }
//    }
//    CoreLabel.genericKeys.put("CustomDoubleAnnotation", CustomDoubleAnnotation.class);
//    CoreLabel.genericValues.put(CustomDoubleAnnotation.class, "CustomDoubleAnnotation");
//    String[] keys = { "CustomDoubleAnnotation" };
//    String[] values = { "3.1415" };
//    CoreLabel label = new CoreLabel(keys, values);
//    Double value = label.get(CustomDoubleAnnotation.class);
//    assertEquals(3.1415, value, 0.0001);
//  }
@Test
  public void testToStringWordIndexIncludesBothIndexes() {
    CoreLabel label = new CoreLabel();
    label.setWord("joint");
    label.setIndex(12);
    label.setEmptyIndex(3);
    String output = label.toString(CoreLabel.OutputFormat.WORD_INDEX);
    assertEquals("joint-12.3", output);
  }
@Test
  public void testToStringLemmaWithEmptyIndexOnly() {
    CoreLabel label = new CoreLabel();
    label.setLemma("run");
    label.setEmptyIndex(8);
    String str = label.toString(CoreLabel.OutputFormat.LEMMA_INDEX);
    assertEquals("run.8", str);
  } 
}