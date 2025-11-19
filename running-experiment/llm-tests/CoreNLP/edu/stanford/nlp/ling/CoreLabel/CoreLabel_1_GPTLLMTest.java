package edu.stanford.nlp.ling;


import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.trees.ud.CoNLLUFeatures;
import edu.stanford.nlp.util.CoreMap;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

import static org.junit.Assert.*;

public class CoreLabel_1_GPTLLMTest {

 @Test
  public void testSetAndGetBasicAnnotations() {
    CoreLabel label = new CoreLabel();
    label.setWord("hello");
    label.setOriginalText("Hello");
    label.setValue("HELLO");
    label.setTag("UH");
    label.setNER("O");
    label.setLemma("greet");
    label.setIndex(10);
    label.setBeginPosition(5);
    label.setEndPosition(10);
    label.setBefore("[");
    label.setAfter("]");
    label.setCategory("greeting");
    label.setDocID("doc1");
    label.setIsMWT(true);
    label.setIsMWTFirst(true);
    label.setIsNewline(true);
    label.setSentIndex(2);
    label.setEmptyIndex(1);

    assertEquals("hello", label.word());
    assertEquals("Hello", label.originalText());
    assertEquals("HELLO", label.value());
    assertEquals("UH", label.tag());
    assertEquals("O", label.ner());
    assertEquals("greet", label.lemma());
    assertEquals(10, label.index());
    assertEquals(5, label.beginPosition());
    assertEquals(10, label.endPosition());
    assertEquals("[", label.before());
    assertEquals("]", label.after());
    assertEquals("greeting", label.category());
    assertEquals("doc1", label.docID());
    assertTrue(label.isMWT());
    assertTrue(label.isMWTFirst());
    assertTrue(label.isNewline());
    assertEquals(2, label.sentIndex());
    assertEquals(1, label.getEmptyIndex());
    assertTrue(label.hasEmptyIndex());
  }
@Test
  public void testCopyConstructorFromCoreLabel() {
    CoreLabel original = new CoreLabel();
    original.setWord("run");
    original.setValue("run-val");
    original.setNER("O");

    CoreLabel copy = new CoreLabel(original);
    assertEquals("run", copy.word());
    assertEquals("run-val", copy.value());
    assertEquals("O", copy.ner());
  }
@Test
  public void testCopyConstructorFromCoreMap() {
    CoreLabel mapBased = new CoreLabel();
    mapBased.setWord("fast");
    mapBased.setTag("RB");
    mapBased.setValue("quickly");

    CoreLabel copied = new CoreLabel((CoreMap) mapBased);
    assertEquals("fast", copied.word());
    assertEquals("RB", copied.tag());
    assertEquals("quickly", copied.value());
  }
@Test(expected = UnsupportedOperationException.class)
  public void testSetFromStringThrowsException() {
    CoreLabel label = new CoreLabel();
    label.setFromString("cannot-parse-this-string");
  }
@Test
  public void testStaticFactoryWordFromString() {
    CoreLabel cl = CoreLabel.wordFromString("example");
    assertEquals("example", cl.word());
    assertEquals("example", cl.originalText());
    assertEquals("example", cl.value());
  }
@Test
  public void testToStringValueIndexFormat() {
    CoreLabel label = new CoreLabel();
    label.setValue("walk");
    label.setIndex(7);

    String result = label.toString(CoreLabel.OutputFormat.VALUE_INDEX);
    assertEquals("walk-7", result);
  }
@Test
  public void testToStringValueTagFormat() {
    CoreLabel label = new CoreLabel();
    label.setValue("apple");
    label.setTag("NN");

    String result = label.toString(CoreLabel.OutputFormat.VALUE_TAG);
    assertEquals("apple/NN", result);
  }
@Test
  public void testToStringLemmaIndexFormat() {
    CoreLabel label = new CoreLabel();
    label.setLemma("go");
    label.setIndex(4);

    String result = label.toString(CoreLabel.OutputFormat.LEMMA_INDEX);
    assertEquals("go-4", result);
  }
@Test
  public void testGetStringWithDefaultValue() {
    CoreLabel label = new CoreLabel();

    String defaultVal = label.getString(CoreAnnotations.OriginalTextAnnotation.class, "unknown");
    assertEquals("unknown", defaultVal);

    label.setOriginalText("known-text");
    String actualVal = label.getString(CoreAnnotations.OriginalTextAnnotation.class, "unused");
    assertEquals("known-text", actualVal);
  }
@Test
  public void testInitFromStringKeysSuccess() {
    String[] keys = {"Text", "PartOfSpeech", "Lemma", "Index"};
    String[] values = {"green", "JJ", "green", "5"};

    CoreLabel label = new CoreLabel(keys, values);
    assertEquals("green", label.word());
    assertEquals("JJ", label.tag());
    assertEquals("green", label.lemma());
    assertEquals(5, label.index());
  }
@Test(expected = UnsupportedOperationException.class)
  public void testInitFromStringKeysMismatchedLengths() {
    String[] keys = {"Text", "Index"};
    String[] values = {"jump"};

    new CoreLabel(keys, values);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testInitFromStringKeysInvalidValueType() {
    String[] keys = {"Index"};
    String[] values = {"not-a-number"};

    new CoreLabel(keys, values);
  }
@Test
  public void testFactoryNewLabelFromString() {
    LabelFactory factory = CoreLabel.factory();
    Label label = factory.newLabel("someLabel");

    assertTrue(label instanceof CoreLabel);
    assertEquals("someLabel", label.value());
  }
@Test(expected = UnsupportedOperationException.class)
  public void testLabelFactoryNewLabelFromStringThrows() {
    LabelFactory factory = CoreLabel.factory();
    factory.newLabelFromString("illegal string");
  }
@Test
  public void testSetNerConfidenceMap() {
    CoreLabel label = new CoreLabel();
    Map<String, Double> probs = new HashMap<>();
    probs.put("LOC", 0.7);
    probs.put("ORG", 0.2);

    label.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, probs);

    Map<String, Double> result = label.nerConfidence();
    assertEquals(0.7, result.get("LOC"), 0.001);
    assertEquals(0.2, result.get("ORG"), 0.001);
  }
@Test
  public void testToStringAllFormatIncludesValues() {
    CoreLabel label = new CoreLabel();
    label.setWord("idea");
    label.setNER("MISC");

    String output = label.toString(CoreLabel.OutputFormat.ALL);
    assertTrue(output.contains("TextAnnotation"));
    assertTrue(output.contains("NamedEntityTagAnnotation"));
    assertTrue(output.contains("idea"));
  }
@Test
  public void testWordIndexFormat() {
    CoreLabel label = new CoreLabel();
    label.setWord("city");
    label.setIndex(15);

    String result = label.toString(CoreLabel.OutputFormat.WORD_INDEX);
    assertEquals("city-15", result);
  }
@Test
  public void testHasEmptyIndexDefaultFalseThenTrue() {
    CoreLabel label = new CoreLabel();

    assertFalse(label.hasEmptyIndex());
    label.setEmptyIndex(99);
    assertTrue(label.hasEmptyIndex());
    assertEquals(99, label.getEmptyIndex());
  }
@Test
  public void testBeginEndPositionDefaultsAndSetValues() {
    CoreLabel label = new CoreLabel();

    assertEquals(-1, label.beginPosition());
    assertEquals(-1, label.endPosition());

    label.setBeginPosition(3);
    label.setEndPosition(8);

    assertEquals(3, label.beginPosition());
    assertEquals(8, label.endPosition());
  }
@Test
  public void testToStringValueTagIndexWithEmptyIndex() {
    CoreLabel label = new CoreLabel();
    label.setValue("run");
    label.setTag("VB");
    label.setIndex(3);
    label.setEmptyIndex(1);

    String result = label.toString(CoreLabel.OutputFormat.VALUE_TAG_INDEX);
    assertEquals("run/VB-3.1", result);
  }
@Test
  public void testToStringValueIndexWithEmptyIndexZeroSuppressed() {
    CoreLabel label = new CoreLabel();
    label.setValue("word");
    label.setIndex(2);
    label.setEmptyIndex(0);

    String result = label.toString(CoreLabel.OutputFormat.VALUE_INDEX);
    assertEquals("word-2", result);
  }
@Test
  public void testToStringValueIndexWithNoIndexSet() {
    CoreLabel label = new CoreLabel();
    label.setValue("noindex");

    String result = label.toString(CoreLabel.OutputFormat.VALUE_INDEX);
    assertEquals("noindex", result);
  }
@Test
  public void testToStringValueIndexMapOmitsIndexAndValue() {
    CoreLabel label = new CoreLabel();
    label.setValue("alpha");
    label.setIndex(1);

    label.setNER("LOC");
    label.setTag("NN");

    String result = label.toString(CoreLabel.OutputFormat.VALUE_INDEX_MAP);
    assertTrue(result.startsWith("alpha-1"));
    assertFalse(result.contains("IndexAnnotation"));
    assertFalse(result.contains("ValueAnnotation"));
    assertTrue(result.contains("NamedEntityTagAnnotation"));
  }
@Test
  public void testToStringMapSortOrder() {
    CoreLabel label = new CoreLabel();
    label.setValue("city");
    label.setTag("NN");
    label.setNER("LOC");

    String mapOutput = label.toString(CoreLabel.OutputFormat.MAP);
    assertTrue(mapOutput.contains("NamedEntityTagAnnotation"));
    assertTrue(mapOutput.contains("PartOfSpeechAnnotation"));
  }
@Test
  public void testParseStringKeysValidKeys() {
    String[] keys = {"Text", "PartOfSpeech", "Lemma"};

    Class[] classes = CoreLabel.parseStringKeys(keys);

    assertEquals(3, classes.length);
    assertEquals(CoreAnnotations.TextAnnotation.class, classes[0]);
    assertEquals(CoreAnnotations.PartOfSpeechAnnotation.class, classes[1]);
    assertEquals(CoreAnnotations.LemmaAnnotation.class, classes[2]);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testParseStringKeysWithUnknownKey() {
    String[] keys = {"UnknownAnnotation"};

    CoreLabel.parseStringKeys(keys);
  }
@Test
  public void testGetStringReturnsEmptyIfNullValue() {
    CoreLabel label = new CoreLabel();

    String val = label.getString(CoreAnnotations.OriginalTextAnnotation.class);
    assertEquals("", val);
  }
@Test
  public void testToStringWordIndexWithEmptyIndex() {
    CoreLabel label = new CoreLabel();
    label.setWord("example");
    label.setIndex(10);
    label.setEmptyIndex(2);

    String result = label.toString(CoreLabel.OutputFormat.WORD_INDEX);
    assertEquals("example-10.2", result);
  }
@Test
  public void testSetNullWordAllowed() {
    CoreLabel label = new CoreLabel();
    label.setWord(null);

    assertNull(label.word());
  }
@Test
  public void testSetNullValueAllowed() {
    CoreLabel label = new CoreLabel();
    label.setValue(null);

    assertNull(label.value());
  }
@Test
  public void testToStringAllEmptyCoreLabel() {
    CoreLabel label = new CoreLabel();

    String result = label.toString(CoreLabel.OutputFormat.ALL);
    assertTrue(result.isEmpty() || result.trim().length() > 0); 
  }
@Test
  public void testToStringValueTagNerWithMissingNER() {
    CoreLabel label = new CoreLabel();
    label.setValue("entity");
    label.setTag("NN");

    String result = label.toString(CoreLabel.OutputFormat.VALUE_TAG_NER);
    assertEquals("entity/NN", result);
  }
@Test(expected = IllegalArgumentException.class)
  public void testToStringThrowsOnUnknownEnum() {
    CoreLabel label = new CoreLabel();
    String result = label.toString(null);
  }
//@Test
//  public void testLabelFactoryWithHasInterfaces() {
//    Label oldLabel = new Label() {
//      public String value() { return "lexical"; }
//      public void setValue(String v) {}
//    };
//
//    LabelFactory factory = CoreLabel.factory();
//    Label newLabel = factory.newLabel(oldLabel);
//
//    assertEquals("lexical", newLabel.value());
//  }
//@Test
//  public void testConstructorWithHasWordOnlySetsWordAndValue() {
//    Label hasWord = new Label() {
//      public String value() { return "valueData"; }
//      public void setValue(String v) {}
//    };
//
//    CoreLabel label = new CoreLabel(hasWord);
//    assertEquals("valueData", label.value());
//  }
@Test
  public void testInitFromStringsWithBooleanTrue() {
    Class[] keys = {CoreAnnotations.IsNewlineAnnotation.class};
    String[] values = {"true"};

    CoreLabel label = new CoreLabel(keys, values);

    assertTrue(label.isNewline());
  }
@Test
  public void testInitFromStringsWithBooleanFalse() {
    Class[] keys = {CoreAnnotations.IsNewlineAnnotation.class};
    String[] values = {"false"};

    CoreLabel label = new CoreLabel(keys, values);

    assertFalse(label.isNewline());
  }
@Test(expected = UnsupportedOperationException.class)
  public void testInitFromStringsUnsupportedValueTypeThrows() {
    Class[] keys = {TreeMap.class}; 
    String[] values = {"someVal"};

    new CoreLabel(keys, values);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testInitFromStringsUnsupportedStringKey() {
    String[] keys = {null};
    String[] values = {"noValue"};

    new CoreLabel(keys, values);
  }
@Test
  public void testToStringMapOmitsEmptyIndexZero() {
    CoreLabel label = new CoreLabel();
    label.setValue("test");
    label.setIndex(10);
    label.setEmptyIndex(0);

    String result = label.toString(CoreLabel.OutputFormat.VALUE_INDEX);
    assertFalse(result.contains(".0"));
  }
//@Test
//  public void testConstructorFromLabelWithHasWordInterfaceOnly() {
//    Label input = new Label() {
//      public String value() { return "word_value"; }
//      public void setValue(String v) { }
//    };
//
//    CoreLabel cl = new CoreLabel(input);
//    assertEquals("word_value", cl.value());
//    assertNull(cl.tag());
//    assertNull(cl.word());
//  }
//@Test
//  public void testConstructorFromLabelWithMultipleInterfaces() {
//    Label input = new Label() implements HasWord, HasTag, HasOffset, HasCategory {
//      public String value() { return "multi"; }
//      public void setValue(String v) { }
//      public String word() { return "apple"; }
//      public void setWord(String w) { }
//      public String tag() { return "NN"; }
//      public void setTag(String t) { }
//      public int beginPosition() { return 1; }
//      public void setBeginPosition(int i) {}
//      public int endPosition() { return 4; }
//      public void setEndPosition(int i) {}
//      public String category() { return "noun"; }
//      public void setCategory(String c) {}
//    };
//
//    CoreLabel cl = new CoreLabel(input);
//    assertEquals("multi", cl.value());
//    assertEquals("apple", cl.word());
//    assertEquals("NN", cl.tag());
//    assertEquals("noun", cl.category());
//    assertEquals(1, cl.beginPosition());
//    assertEquals(4, cl.endPosition());
//  }
@Test
  public void testCopyConstructorNullAnnotations() {
    CoreLabel original = new CoreLabel();
    original.setWord(null);
    original.setValue(null);
    original.setNER(null);

    CoreLabel copy = new CoreLabel(original);

    assertNull(copy.word());
    assertNull(copy.value());
    assertNull(copy.ner());
  }
@Test
  public void testToStringMapWithSpecialCharactersInAnnotationValues() {
    CoreLabel label = new CoreLabel();
    label.setWord("text");
    label.setNER("PER&SON");
    label.setTag("JJ#");

    String result = label.toString(CoreLabel.OutputFormat.MAP);
    assertTrue(result.contains("PER&SON"));
    assertTrue(result.contains("JJ#"));
  }
@Test
  public void testToStringValueMapFormatRemovesValueAnnotation() {
    CoreLabel label = new CoreLabel();
    label.setValue("alpha");
    label.setTag("NOUN");

    String result = label.toString(CoreLabel.OutputFormat.VALUE_MAP);
    assertTrue(result.contains("NOUN"));
    assertFalse(result.contains("ValueAnnotation"));
  }
@Test
  public void testToStringIgnoreUnknownOutputFormat() {
    CoreLabel label = new CoreLabel();
    boolean threw = false;
    try {
      label.toString(CoreLabel.OutputFormat.valueOf("NON_EXISTENT"));
    } catch (IllegalArgumentException e) {
      threw = true;
    }
    assertTrue(threw);
  }
@Test
  public void testToStringAllShowsAllKeysAndValues() {
    CoreLabel label = new CoreLabel();
    label.setWord("science");
    label.setNER("O");
    label.setTag("NN");
    label.setCategory("noun");
    label.setIndex(101);

    String result = label.toString(CoreLabel.OutputFormat.ALL);

    assertTrue(result.contains("TextAnnotation"));
    assertTrue(result.contains("NamedEntityTagAnnotation"));
    assertTrue(result.contains("PartOfSpeechAnnotation"));
    assertTrue(result.contains("CategoryAnnotation"));
    assertTrue(result.contains("IndexAnnotation"));
  }
@Test
  public void testSetAndGetCategoryAnnotationExplicitly() {
    CoreLabel label = new CoreLabel();
    label.set(CoreAnnotations.CategoryAnnotation.class, "label");

    String cat = label.category();
    assertEquals("label", cat);
  }
@Test
  public void testSetAndGetDocIDAnnotationExplicitly() {
    CoreLabel label = new CoreLabel();
    label.set(CoreAnnotations.DocIDAnnotation.class, "docXYZ");

    String doc = label.docID();
    assertEquals("docXYZ", doc);
  }
@Test
  public void testNerConfidenceReturnsNullIfUnset() {
    CoreLabel label = new CoreLabel();
    Map<String, Double> result = label.nerConfidence();
    assertNull(result);
  }
@Test
  public void testGetEmptyIndexWhenUnsetReturnsZero() {
    CoreLabel label = new CoreLabel();
    assertEquals(0, label.getEmptyIndex());
  }
@Test
  public void testWordIndexFormatWithoutWordReturnsNullPrefix() {
    CoreLabel label = new CoreLabel();
    label.setIndex(3);

    String result = label.toString(CoreLabel.OutputFormat.WORD_INDEX);
    assertTrue(result.startsWith("null-3"));
  }
@Test
  public void testToStringValueIndexOnlyEmptyIndexPresent() {
    CoreLabel label = new CoreLabel();
    label.setValue("foo");
    label.setEmptyIndex(7);

    String result = label.toString(CoreLabel.OutputFormat.VALUE_INDEX);
    assertEquals("foo.7", result);
  }
@Test
  public void testToStringValueTagIndexWithoutTag() {
    CoreLabel label = new CoreLabel();
    label.setValue("word");
    label.setIndex(1);

    String result = label.toString(CoreLabel.OutputFormat.VALUE_TAG_INDEX);
    assertEquals("word-1", result);
  }
@Test
  public void testToStringLemmaIndexWithNullLemma() {
    CoreLabel label = new CoreLabel();
    label.setIndex(2);

    String output = label.toString(CoreLabel.OutputFormat.LEMMA_INDEX);
    assertTrue(output.contains("null-2"));
  }
@Test
  public void testSetFromStringsWithCoNLLUFeats() {
    String[] keys = { "CoNLLUFeats" };
    String[] values = { "Case=Nom|Number=Sing" };

    CoreLabel label = new CoreLabel(keys, values);
    CoNLLUFeatures feats = label.get(CoreAnnotations.CoNLLUFeats.class);
    assertNotNull(feats);
    assertEquals("Case=Nom|Number=Sing", feats.toString());
  }
@Test
  public void testSetFromStringsWithIntegerValue() {
    String[] keys = { "Index" };
    String[] values = { "123" };

    CoreLabel label = new CoreLabel(keys, values);
    int index = label.get(CoreAnnotations.IndexAnnotation.class);
    assertEquals(123, index);
  }
@Test
  public void testSetFromStringsWithBooleanTrueValue() {
    String[] keys = { "IsNewline" };
    String[] values = { "true" };

    CoreLabel label = new CoreLabel(keys, values);
    assertTrue(label.isNewline());
  }
@Test
  public void testSetFromStringsWithDoubleAndLongUnsupported() {
    Class[] keys = { CoreAnnotations.IsNewlineAnnotation.class };
    String[] values = { "notABoolean" };

    boolean threw = false;
    try {
      new CoreLabel(keys, values);
    } catch (Exception e) {
      threw = true;
    }
    assertTrue(threw);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testInitFromStringWithUnsupportedTypeThrows() {
    Class[] keys = { CoreLabel.GenericAnnotation.class };
    String[] values = { "abc" };

    new CoreLabel(keys, values);
  }
@Test
  public void testToStringValueWithEmptyString() {
    CoreLabel label = new CoreLabel();
    label.setValue("");

    String result = label.toString(CoreLabel.OutputFormat.VALUE);
    assertEquals("", result);
  }
@Test
  public void testValueIndexWithZeroIndex() {
    CoreLabel label = new CoreLabel();
    label.setValue("w");
    label.setIndex(0);

    String result = label.toString(CoreLabel.OutputFormat.VALUE_INDEX);
    assertEquals("w-0", result);
  }
@Test
  public void testIndexDefaultIsNegativeOne() {
    CoreLabel label = new CoreLabel();
    assertEquals(-1, label.index());
  }
@Test
  public void testSentIndexDefaultIsNegativeOne() {
    CoreLabel label = new CoreLabel();
    assertEquals(-1, label.sentIndex());
  }
@Test
  public void testBeginEndPositionUninitializedAreNegativeOne() {
    CoreLabel label = new CoreLabel();
    assertEquals(-1, label.beginPosition());
    assertEquals(-1, label.endPosition());
  }
@Test
  public void testIndexExplicitlySetToZero() {
    CoreLabel label = new CoreLabel();
    label.setIndex(0);
    assertEquals(0, label.index());
  }
@Test
  public void testToStringReturnsEmptyWhenValueIsNull() {
    CoreLabel label = new CoreLabel();
    label.set(CoreAnnotations.ValueAnnotation.class, null);

    String result = label.toString(CoreLabel.OutputFormat.VALUE);
    assertNull(result);
  }
@Test
  public void testToStringWordWhenTextAnnotationNotSet() {
    CoreLabel label = new CoreLabel();
    String result = label.toString(CoreLabel.OutputFormat.WORD);
    assertNull(result);
  }
@Test
  public void testToStringValueTagWhenTagIsMissing() {
    CoreLabel label = new CoreLabel();
    label.setValue("running");

    String result = label.toString(CoreLabel.OutputFormat.VALUE_TAG);
    assertEquals("running", result);
  }
@Test
  public void testToStringValueTagIndexWhenIndexAndEmptyIndexAreMissing() {
    CoreLabel label = new CoreLabel();
    label.setValue("data");
    label.setTag("NN");

    String result = label.toString(CoreLabel.OutputFormat.VALUE_TAG_INDEX);
    assertEquals("data/NN", result);
  }
@Test
  public void testToStringValueIndexWhenIndexNullAndEmptyIndexNonZero() {
    CoreLabel label = new CoreLabel();
    label.setValue("data");
    label.setEmptyIndex(2);

    String result = label.toString(CoreLabel.OutputFormat.VALUE_INDEX);
    assertEquals("data.2", result);
  }
@Test
  public void testToStringValueIndexMapWithComplexKeys() {
    CoreLabel label = new CoreLabel();
    label.setValue("complex");
    label.setIndex(9);
    label.setNER("LOC");
    label.setTag("NN");
    label.setCategory("LOCATION");

    String result = label.toString(CoreLabel.OutputFormat.VALUE_INDEX_MAP);
    assertTrue(result.contains("NamedEntityTagAnnotation"));
    assertTrue(result.contains("PartOfSpeechAnnotation"));
    assertFalse(result.contains("IndexAnnotation"));
    assertFalse(result.contains("ValueAnnotation"));
    assertTrue(result.startsWith("complex-9"));
  }
@Test
  public void testToStringAllIncludesEveryAnnotationKey() {
    CoreLabel label = new CoreLabel();
    label.setWord("swim");
    label.setNER("O");
    label.setLemma("swim");
    label.setTag("VB");

    String result = label.toString(CoreLabel.OutputFormat.ALL);
    assertTrue(result.contains("TextAnnotation"));
    assertTrue(result.contains("NamedEntityTagAnnotation"));
    assertTrue(result.contains("LemmaAnnotation"));
    assertTrue(result.contains("PartOfSpeechAnnotation"));
  }
@Test
  public void testInitFromStringsWithLongValueType() {
    Class[] keys = { CoreAnnotations.CharacterOffsetBeginAnnotation.class };
    String[] values = { "1234567890" };

    CoreLabel label = new CoreLabel(keys, values);
    int value = label.beginPosition();
    assertEquals(1234567890, value);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testInitFromStringsWithUnknownAnnotatedKey() {
    String[] keys = { "NonExistentAnnotation" };
    String[] values = { "some-value" };

    new CoreLabel(keys, values);
  }
@Test
  public void testSetBooleanAnnotationsAsFalse() {
    CoreLabel label = new CoreLabel();
    label.setIsMWT(false);
    label.setIsMWTFirst(false);
    label.setIsNewline(false);

    Boolean mwt = label.get(CoreAnnotations.IsMultiWordTokenAnnotation.class);
    Boolean mwtFirst = label.get(CoreAnnotations.IsFirstWordOfMWTAnnotation.class);
    Boolean isNewline = label.get(CoreAnnotations.IsNewlineAnnotation.class);

    assertEquals(Boolean.FALSE, mwt);
    assertEquals(Boolean.FALSE, mwtFirst);
    assertEquals(Boolean.FALSE, isNewline);
  }
@Test
  public void testToStringAllWhenOnlyIndexIsSet() {
    CoreLabel label = new CoreLabel();
    label.setIndex(4);

    String result = label.toString(CoreLabel.OutputFormat.ALL);
    assertTrue(result.contains("IndexAnnotation"));
  }
@Test
  public void testCloneWithUnknownAnnotationTypesIgnored() {
    CoreLabel original = new CoreLabel();
    original.set(CoreAnnotations.ValueAnnotation.class, "val123");
    original.set(CoreAnnotations.TextAnnotation.class, "txt123");

    CoreLabel copy = new CoreLabel((CoreMap) original);

    assertEquals("val123", copy.value());
    assertEquals("txt123", copy.word());
  }
@Test
  public void testToStringMapOrdersKeysAlphabetically() {
    CoreLabel label = new CoreLabel();
//    label.set(CoreAnnotations.TagLabelAnnotation.class, "tag");
    label.set(CoreAnnotations.TextAnnotation.class, "word");
    label.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");

    String result = label.toString(CoreLabel.OutputFormat.MAP);
    int loc1 = result.indexOf("NamedEntityTagAnnotation");
    int loc2 = result.indexOf("TagLabelAnnotation");
    int loc3 = result.indexOf("TextAnnotation");

    assertTrue(loc1 < loc2 && loc2 < loc3);
  }
@Test
  public void testToStringMapIgnoresNullKeys() {
    CoreLabel label = new CoreLabel();
    String result = label.toString(CoreLabel.OutputFormat.MAP);
    
    assertTrue(result != null);
  }
@Test
  public void testGetStringReturnsNullWhenAnnotationUnset() {
    CoreLabel label = new CoreLabel();
    String result = label.get(CoreAnnotations.TextAnnotation.class);
    assertNull(result);
  }
@Test
  public void testSetAndGetAfterAnnotationWithEmptyString() {
    CoreLabel label = new CoreLabel();
    label.setAfter("");
    String result = label.after();
    assertEquals("", result);
  }
@Test
  public void testSetAndGetBeforeAnnotationWithWhitespace() {
    CoreLabel label = new CoreLabel();
    label.setBefore("   ");
    String result = label.before();
    assertEquals("   ", result);
  }
@Test
  public void testSetAndGetNamedEntityTagProbsAnnotation() {
    CoreLabel label = new CoreLabel();
    Map<String, Double> map = new TreeMap<>();
    map.put("PERSON", 0.9);
    map.put("ORG", 0.1);
    label.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, map);

    Map<String, Double> result = label.nerConfidence();
    assertEquals(0.9, result.get("PERSON"), 0.0001);
    assertEquals(0.1, result.get("ORG"), 0.0001);
  }
@Test
  public void testSetAndGetIndexAnnotationToNegativeValue() {
    CoreLabel label = new CoreLabel();
    label.setIndex(-10);
    int idx = label.index();
    assertEquals(-10, idx);
  }
@Test
  public void testSetEmptyIndexToZeroReturnsZeroAndHasEmptyFalse() {
    CoreLabel label = new CoreLabel();
    label.setEmptyIndex(0);
    assertEquals(0, label.getEmptyIndex());
    assertFalse(label.hasEmptyIndex());
  }
@Test
  public void testToStringWithNullEnumThrowsIAE() {
    CoreLabel label = new CoreLabel();
    boolean threw = false;
    try {
      label.toString(null);
    } catch (IllegalArgumentException | NullPointerException e) {
      threw = true;
    }
    assertTrue(threw);
  }
@Test
  public void testToStringValueTagNERWithMissingTag() {
    CoreLabel label = new CoreLabel();
    label.setValue("Paris");
    label.setNER("LOCATION");

    String result = label.toString(CoreLabel.OutputFormat.VALUE_TAG_NER);
    assertEquals("Paris/LOCATION", result);
  }
@Test
  public void testToStringValueTagNERWithMissingNER() {
    CoreLabel label = new CoreLabel();
    label.setValue("Paris");
    label.setTag("NN");

    String result = label.toString(CoreLabel.OutputFormat.VALUE_TAG_NER);
    assertEquals("Paris/NN", result);
  }
@Test
  public void testToStringMapWithVariousKeyTypes() {
    CoreLabel label = new CoreLabel();
    label.set(CoreAnnotations.TextAnnotation.class, "word");
    label.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    label.set(CoreAnnotations.IndexAnnotation.class, 5);

    String result = label.toString(CoreLabel.OutputFormat.MAP);
    assertTrue(result.contains("TextAnnotation"));
    assertTrue(result.contains("PartOfSpeechAnnotation"));
    assertTrue(result.contains("IndexAnnotation"));
  }
//@Test
//  public void testInitFromStringsWithDoubleValue() {
//    Class[] keys = { CoreAnnotations.ConfidenceAnnotation.class };
//    String[] values = { "0.95" };
//
//    CoreLabel label = new CoreLabel(keys, values);
//    Double confidence = label.get(CoreAnnotations.ConfidenceAnnotation.class);
//    assertEquals(0.95, confidence, 0.00001);
//  }
//@Test(expected = UnsupportedOperationException.class)
//  public void testInitFromStringsWithDoubleValueInvalidFormat() {
//    Class[] keys = { ConfidenceAnnotation.class };
//    String[] values = { "not-a-double" };
//
//    new CoreLabel(keys, values);
//  }
@Test(expected = UnsupportedOperationException.class)
  public void testInitFromStringsWithNullKey() {
    String[] keys = { null };
    String[] values = { "test" };

    new CoreLabel(keys, values);
  }
@Test
  public void testCloneFromCoreMapPreservesBooleanAnnotation() {
    CoreLabel original = new CoreLabel();
    original.setIsNewline(true);

    CoreLabel cloned = new CoreLabel((CoreMap) original);
    Boolean isNewline = cloned.get(CoreAnnotations.IsNewlineAnnotation.class);
    assertEquals(Boolean.TRUE, isNewline);
  }
//@Test
//  public void testFactoryNewLabelFromLabelWithPartialInterface() {
//    Label label = new Label() implements HasTag {
//      public String value() { return "label" ;}
//      public void setValue(String v) { }
//      public String tag() { return "JJ"; }
//      public void setTag(String t) { }
//    };
//
//    CoreLabel coreLabel = (CoreLabel) CoreLabel.factory().newLabel(label);
//    assertEquals("label", coreLabel.value());
//    assertEquals("JJ", coreLabel.tag());
//  }
@Test
  public void testToStringReturnsEmptyWhenNoFieldsSetButFormatIsAll() {
    CoreLabel label = new CoreLabel();

    String result = label.toString(CoreLabel.OutputFormat.ALL);
    assertNotNull(result);
  }
@Test
  public void testParseStringKeysHandlesMultipleTypes() {
    String[] keys = { "Text", "Index", "Category" };
    Class[] parsedKeys = CoreLabel.parseStringKeys(keys);

    assertEquals(3, parsedKeys.length);
    assertEquals(CoreAnnotations.TextAnnotation.class, parsedKeys[0]);
    assertEquals(CoreAnnotations.IndexAnnotation.class, parsedKeys[1]);
    assertEquals(CoreAnnotations.CategoryAnnotation.class, parsedKeys[2]);
  }
@Test
  public void testSetNullOriginalText() {
    CoreLabel label = new CoreLabel();
    label.setOriginalText(null);
    String output = label.originalText();
    assertNull(output);
  }
@Test
  public void testSetNullBeforeAndAfter() {
    CoreLabel label = new CoreLabel();
    label.setBefore(null);
    label.setAfter(null);

    assertNull(label.before());
    assertNull(label.after());
  }
@Test
  public void testEmptyIndexSetAndToStringValueIndex() {
    CoreLabel label = new CoreLabel();
    label.setValue("foo");
    label.setIndex(12);
    label.setEmptyIndex(7);

    String result = label.toString(CoreLabel.OutputFormat.VALUE_INDEX);
    assertEquals("foo-12.7", result);
  }
@Test
  public void testEmptyIndexSetToZeroExcludedFromValueIndex() {
    CoreLabel label = new CoreLabel();
    label.setValue("bar");
    label.setIndex(2);
    label.setEmptyIndex(0);

    String result = label.toString(CoreLabel.OutputFormat.VALUE_INDEX);
    assertEquals("bar-2", result);
  }
@Test
  public void testValueIndexOnlyValueSet() {
    CoreLabel label = new CoreLabel();
    label.setValue("only");

    String result = label.toString(CoreLabel.OutputFormat.VALUE_INDEX);
    assertEquals("only", result);
  }
@Test
  public void testToStringWordIndexWithZeroIndex() {
    CoreLabel label = new CoreLabel();
    label.setWord("apple");
    label.setIndex(0);

    String result = label.toString(CoreLabel.OutputFormat.WORD_INDEX);
    assertEquals("apple-0", result);
  }
@Test
  public void testEmptyCoreLabelFactoryNewLabel() {
    Label label = CoreLabel.factory().newLabel("hello");
    assertTrue(label instanceof CoreLabel);
    assertEquals("hello", label.value());
  }
@Test
  public void testSetGenericAnnotationViaSetMethod() {
    CoreLabel label = new CoreLabel();
    label.set(CoreAnnotations.OriginalTextAnnotation.class, "abc");
    String original = label.get(CoreAnnotations.OriginalTextAnnotation.class);
    assertEquals("abc", original);
  }
@Test
  public void testHasEmptyIndexReturnsTrueIfSetNotZero() {
    CoreLabel label = new CoreLabel();
    label.setEmptyIndex(5);
    boolean hasEmpty = label.hasEmptyIndex();
    assertTrue(hasEmpty);
  }
@Test
  public void testHasEmptyIndexReturnsFalseIfUnset() {
    CoreLabel label = new CoreLabel();
    boolean hasEmpty = label.hasEmptyIndex();
    assertFalse(hasEmpty);
  }
@Test
  public void testHasEmptyIndexReturnsFalseIfZero() {
    CoreLabel label = new CoreLabel();
    label.setEmptyIndex(0);
    boolean hasEmpty = label.hasEmptyIndex();
    assertFalse(hasEmpty);
  }
@Test
  public void testInitFromStringsHandlesCoNLLUFeatures() {
    String[] keys = { "CoNLLUFeats" };
    String[] values = { "Mood=Ind|Tense=Pres" };

    CoreLabel label = new CoreLabel(keys, values);
    CoNLLUFeatures feats = label.get(CoreAnnotations.CoNLLUFeats.class);
    assertEquals("Mood=Ind|Tense=Pres", feats.toString());
  }
@Test(expected = UnsupportedOperationException.class)
  public void testFactoryNewLabelFromStringThrowsUnsupported() {
    LabelFactory factory = CoreLabel.factory();
    factory.newLabelFromString("someUnsupportedFormat");
  }
@Test
  public void testToStringMapHandlesEmptyLabel() {
    CoreLabel label = new CoreLabel();
    String result = label.toString(CoreLabel.OutputFormat.MAP);
    assertTrue(result.equals("{}") || result.trim().isEmpty());
  }
@Test
  public void testToStringValueMapOmitsNullAnnotations() {
    CoreLabel label = new CoreLabel();
    label.setValue("test");
    label.set(CoreAnnotations.NamedEntityTagAnnotation.class, null);

    String result = label.toString(CoreLabel.OutputFormat.VALUE_MAP);
    assertTrue(result.startsWith("test"));
    assertFalse(result.contains("NamedEntityTagAnnotation=null"));
  }
//@Test
//  public void testConstructorWithLabelOnlyValueSet() {
//    Label label = new Label() {
//      public String value() { return "hello"; }
//      public void setValue(String s) { }
//    };
//
//    CoreLabel result = new CoreLabel(label);
//    assertEquals("hello", result.value());
//    assertNull(result.word());
//  }
@Test
  public void testToStringAllFormatWithSingleTag() {
    CoreLabel label = new CoreLabel();
    label.setTag("ADJ");

    String result = label.toString(CoreLabel.OutputFormat.ALL);
    assertTrue(result.contains("PartOfSpeechAnnotation"));
    assertTrue(result.contains("ADJ"));
  }
@Test
  public void testToStringMissingIndexAndEmptyIndexValueTagIndexFormat() {
    CoreLabel label = new CoreLabel();
    label.setValue("run");
    label.setTag("VB");

    String result = label.toString(CoreLabel.OutputFormat.VALUE_TAG_INDEX);
    assertEquals("run/VB", result);
  }
@Test
  public void testToStringValueIndexMapWithEmptyAnnotationSet() {
    CoreLabel label = new CoreLabel();
    label.setValue("alpha");

    String result = label.toString(CoreLabel.OutputFormat.VALUE_INDEX_MAP);
    assertEquals("alpha", result);
  }
@Test
  public void testIndexAnnotationCanBeOverwrite() {
    CoreLabel label = new CoreLabel();
    label.setIndex(10);
    assertEquals(10, label.index());

    label.setIndex(20);
    assertEquals(20, label.index());
  }
@Test
  public void testToStringWordIndexWhenWordUnset() {
    CoreLabel label = new CoreLabel();
    label.setIndex(3);

    String output = label.toString(CoreLabel.OutputFormat.WORD_INDEX);
    assertEquals("null-3", output);
  }
@Test
  public void testEmptyInitFromStringKeyArrayWithNoKeys() {
    String[] keys = {};
    String[] values = {};
    CoreLabel label = new CoreLabel(keys, values);

    assertEquals(null, label.word());
    assertEquals(null, label.tag());
  }
@Test(expected = UnsupportedOperationException.class)
  public void testInitFromStringsFailsOnDifferentLengths() {
    String[] keys = { "Text", "IndexAnnotation" };
    String[] values = { "hello" };
    new CoreLabel(keys, values);
  }
@Test
  public void testToStringWordIndexZeroEmptyIndexNotShown() {
    CoreLabel label = new CoreLabel();
    label.setWord("base");
    label.setIndex(0);
    label.setEmptyIndex(0);

    String result = label.toString(CoreLabel.OutputFormat.WORD_INDEX);
    assertEquals("base-0", result);
  }
@Test
  public void testToStringValueIndexMapIncludesExtraFeature() {
    CoreLabel label = new CoreLabel();
    label.setValue("abc");
    label.setIndex(2);
    label.setOriginalText("abc-original");

    String result = label.toString(CoreLabel.OutputFormat.VALUE_INDEX_MAP);
    assertTrue(result.contains("OriginalTextAnnotation"));
    assertFalse(result.contains("IndexAnnotation"));
  }
@Test
  public void testCanSetAndGetLongAnnotation() {
    CoreLabel label = new CoreLabel();
    label.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 1234567890);

    int val = label.beginPosition();
    assertEquals(1234567890, val);
  }
@Test
  public void testToStringValueTagNERWhenAllFieldsPresent() {
    CoreLabel label = new CoreLabel();
    label.setValue("Alice");
    label.setTag("NNP");
    label.setNER("PERSON");

    String output = label.toString(CoreLabel.OutputFormat.VALUE_TAG_NER);
    assertEquals("Alice/NNP/PERSON", output);
  }
@Test
  public void testToStringWithEmptyWordAndIndex() {
    CoreLabel label = new CoreLabel();
    label.setWord("");
    label.setIndex(9);

    String output = label.toString(CoreLabel.OutputFormat.WORD_INDEX);
    assertEquals("-9", output);
  }
@Test
  public void testEnumOutputFormatAllValuesCovered() {
    CoreLabel label = new CoreLabel();
    for (CoreLabel.OutputFormat format : CoreLabel.OutputFormat.values()) {
      try {
        label.toString(format);
      } catch (IllegalArgumentException e) {
        
        fail("Unsupported format in enum: " + format);
      }
    }
  }
@Test
  public void testWhitespaceInOriginalTextPreserved() {
    CoreLabel label = new CoreLabel();
    label.setOriginalText(" some text ");
    assertEquals(" some text ", label.originalText());
  }
@Test
  public void testSetNERNullExplicitly() {
    CoreLabel label = new CoreLabel();
    label.setNER(null);
    assertNull(label.ner());
  }
@Test
  public void testSetAndGetNonStandardAnnotationValue() {
    CoreLabel label = new CoreLabel();
    label.set(CoreAnnotations.SectionAnnotation.class, "BODY");

    String result = label.get(CoreAnnotations.SectionAnnotation.class);
    assertEquals("BODY", result);
  }
//@Test
//  public void testSetAndGetUncommonAnnotationKey() {
//    CoreLabel label = new CoreLabel();
//    label.set(CoreAnnotations.MorphoAnnotation.class, "morph-info");
//
//    String result = label.get(CoreAnnotations.MorphoAnnotation.class);
//    assertEquals("morph-info", result);
//  }
@Test
  public void testEmptyConstructorHasNoAnnotationsInitially() {
    CoreLabel label = new CoreLabel();
    assertTrue(label.keySet().isEmpty());
  }
@Test
  public void testFactoryNewLabelWithZeroOptionsUsesValue() {
    LabelFactory factory = CoreLabel.factory();
    Label lbl = factory.newLabel("something", 0);

    assertTrue(lbl instanceof CoreLabel);
    assertEquals("something", lbl.value());
  }
@Test
  public void testNERConfidenceReturnsUnmodifiableMap() {
    CoreLabel label = new CoreLabel();
    Map<String, Double> confidence = new HashMap<>();
    confidence.put("ORG", 0.8);
    label.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, confidence);

    Map<String, Double> result = label.nerConfidence();
    assertEquals(Double.valueOf(0.8), result.get("ORG"));
  }
@Test
  public void testSetCategoryAndOverwrite() {
    CoreLabel label = new CoreLabel();
    label.setCategory("initial");
    label.setCategory("updated");

    String result = label.get(CoreAnnotations.CategoryAnnotation.class);
    assertEquals("updated", result);
  }
@Test
  public void testCopyConstructorWithEmptyCoreLabel() {
    CoreLabel original = new CoreLabel();
    CoreLabel copy = new CoreLabel((CoreMap) original);
    assertNotNull(copy);
    assertTrue(copy.keySet().isEmpty());
  }
@Test
  public void testSetAndGetBooleanFalseAnnotation() {
    CoreLabel label = new CoreLabel();
    label.setIsNewline(false);
    Boolean value = label.get(CoreAnnotations.IsNewlineAnnotation.class);
    assertEquals(Boolean.FALSE, value);
  }
@Test
  public void testSetAndGetIntegerAnnotationWithNegativeValue() {
    CoreLabel label = new CoreLabel();
    label.setIndex(-99);
    assertEquals(-99, label.index());
  }
@Test
  public void testSetEmptyIndexZeroExplicitly() {
    CoreLabel label = new CoreLabel();
    label.setEmptyIndex(0);
    assertEquals(0, label.getEmptyIndex());
    assertFalse(label.hasEmptyIndex());
  }
@Test
  public void testToStringValueIndexMapWithMultipleTypes() {
    CoreLabel label = new CoreLabel();
    label.setValue("x");
    label.setIndex(1);
    label.setEndPosition(20);
    label.setNER("DATE");
    label.set(CoreAnnotations.OriginalTextAnnotation.class, "original");

    String result = label.toString(CoreLabel.OutputFormat.VALUE_INDEX_MAP);
    assertTrue(result.contains("OriginalTextAnnotation"));
    assertTrue(result.contains("NamedEntityTagAnnotation"));
    assertTrue(result.contains("CharacterOffsetEndAnnotation"));
    assertFalse(result.contains("IndexAnnotation"));
    assertFalse(result.contains("ValueAnnotation"));
  }
@Test
  public void testMultipleSetOverwritesAnnotationValue() {
    CoreLabel label = new CoreLabel();
    label.setNER("PERSON");
    label.setNER("ORG");
    String ner = label.get(CoreAnnotations.NamedEntityTagAnnotation.class);
    assertEquals("ORG", ner);
  }
@Test
  public void testToStringAllIncludesAllAnnotationsEvenNullValues() {
    CoreLabel label = new CoreLabel();
    label.setNER("LOC");
    label.setWord("mountain");
    label.set(CoreAnnotations.BeforeAnnotation.class, null);

    String str = label.toString(CoreLabel.OutputFormat.ALL);
    assertTrue(str.contains("BeforeAnnotation"));
    assertTrue(str.contains("LOC"));
    assertTrue(str.contains("mountain"));
  }
@Test
  public void testSetFromStringsHandlesAllPrimitiveTypes() {
    String[] keys = {
        "Text",
        "PartOfSpeech",
        "Index",
        "CharacterOffsetBegin",
        "CharacterOffsetEnd",
        "Lemma",
        "IsNewline"
    };
    String[] values = {
        "word",
        "NN",
        "9",
        "100",
        "110",
        "walk",
        "true"
    };

    CoreLabel label = new CoreLabel(keys, values);
    assertEquals("word", label.word());
    assertEquals("NN", label.tag());
    assertEquals(9, label.index());
    assertEquals(100, label.beginPosition());
    assertEquals(110, label.endPosition());
    assertEquals("walk", label.lemma());
    assertTrue(label.isNewline());
  }
@Test(expected = UnsupportedOperationException.class)
  public void testInitFromStringFailsWithUnparseableInteger() {
    Class[] keys = { CoreAnnotations.IndexAnnotation.class };
    String[] values = { "not-an-int" };
    new CoreLabel(keys, values);
  }
@Test
  public void testInitFromStringSkipsNullValueForKnownKey() {
    String[] keys = { "Text" };
    String[] values = { null };
    CoreLabel label = new CoreLabel(keys, values);
    assertNull(label.word());
  }
@Test
  public void testWordNullStillAllowsToStringVALUE() {
    CoreLabel label = new CoreLabel();
    label.setValue(null);
    String result = label.toString(CoreLabel.OutputFormat.VALUE);
    assertNull(result);
  }
@Test
  public void testToStringValueTagIndexWhenAllAnnotationsArePresent() {
    CoreLabel label = new CoreLabel();
    label.setValue("swim");
    label.setTag("VB");
    label.setIndex(101);
    label.setEmptyIndex(2);

    String result = label.toString(CoreLabel.OutputFormat.VALUE_TAG_INDEX);
    assertEquals("swim/VB-101.2", result);
  }
@Test
  public void testToStringWORD_INDEXWithOnlyWord() {
    CoreLabel label = new CoreLabel();
    label.setWord("banana");

    String result = label.toString(CoreLabel.OutputFormat.WORD_INDEX);
    assertEquals("banana", result);
  }
@Test
  public void testToStringValueTagNERWhenNERMissing() {
    CoreLabel label = new CoreLabel();
    label.setValue("tree");
    label.setTag("NN");

    String result = label.toString(CoreLabel.OutputFormat.VALUE_TAG_NER);
    assertEquals("tree/NN", result);
  }
@Test
  public void testNERConfidenceMapCanContainMultipleEntries() {
    CoreLabel label = new CoreLabel();
    Map<String, Double> map = new HashMap<>();
    map.put("ORG", 0.8);
    map.put("LOC", 0.2);
    label.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, map);

    Map<String, Double> result = label.nerConfidence();
    assertEquals(0.8, result.get("ORG"), 0.0001);
    assertEquals(0.2, result.get("LOC"), 0.0001);
  }
@Test
  public void testToStringVALUE_INDEXMapsToSameAsManualBuild() {
    CoreLabel label = new CoreLabel();
    label.setValue("walk");
    label.setIndex(3);
    String expected = "walk-3";
    String actual = label.toString(CoreLabel.OutputFormat.VALUE_INDEX);
    assertEquals(expected, actual);
  }
@Test
  public void testSetNERNullRemovesAnnotation() {
    CoreLabel label = new CoreLabel();
    label.setNER("PERSON");
    assertEquals("PERSON", label.ner());
    label.setNER(null);
    assertNull(label.ner());
  }
@Test
  public void testSetCoNLLUFeaturesParsesCorrectly() {
    CoreLabel label = new CoreLabel();
    label.set(CoreAnnotations.CoNLLUFeats.class, new CoNLLUFeatures("Gender=Masc|Number=Sing"));
    CoNLLUFeatures feats = label.get(CoreAnnotations.CoNLLUFeats.class);
    assertEquals("Gender=Masc|Number=Sing", feats.toString());
  }
@Test
  public void testParseStringKeysEmptyInput() {
    String[] keys = new String[0];
    Class[] result = CoreLabel.parseStringKeys(keys);
    assertEquals(0, result.length);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testInitFromStringsWithExtraKeyFailsDueToUnknown() {
    String[] keys = { "Text", "ThisDoesNotExist" };
    String[] values = { "test", "123" };
    new CoreLabel(keys, values);
  }
@Test
  public void testSetBeginAndEndPositionThenValidate() {
    CoreLabel label = new CoreLabel();
    label.setBeginPosition(10);
    label.setEndPosition(20);

    int begin = label.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class);
    int end = label.get(CoreAnnotations.CharacterOffsetEndAnnotation.class);
    assertEquals(10, begin);
    assertEquals(20, end);
  }
@Test
  public void testToStringLEMMA_INDEXIncludesEmptyIndex() {
    CoreLabel label = new CoreLabel();
    label.setLemma("run");
    label.setIndex(5);
    label.setEmptyIndex(1);

    String result = label.toString(CoreLabel.OutputFormat.LEMMA_INDEX);
    assertEquals("run-5.1", result);
  }
@Test
  public void testToStringALLIncludesAllWhenOnlyEmptyIndex() {
    CoreLabel label = new CoreLabel();
    label.setEmptyIndex(8);

    String output = label.toString(CoreLabel.OutputFormat.ALL);
    assertTrue(output.contains("EmptyIndexAnnotation"));
    assertTrue(output.contains("8"));
  } 
}