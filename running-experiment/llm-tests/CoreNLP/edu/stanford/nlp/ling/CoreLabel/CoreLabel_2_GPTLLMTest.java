package edu.stanford.nlp.ling;

import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.trees.ud.CoNLLUFeatures;
import edu.stanford.nlp.util.CoreMap;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

import static org.junit.Assert.*;

public class CoreLabel_2_GPTLLMTest {

 @Test
  public void testDefaultConstructor() {
    CoreLabel label = new CoreLabel();
    assertNotNull(label);
    assertTrue(label.keySet().isEmpty());
  }
@Test
  public void testConstructorWithCapacity() {
    CoreLabel label = new CoreLabel(5);
    assertNotNull(label);
    assertTrue(label.keySet().isEmpty());
  }
@Test
  public void testCopyConstructorFromCoreLabel() {
    CoreLabel original = new CoreLabel();
    original.setWord("sample");
    original.setTag("NN");
    original.setValue("sample");

    CoreLabel copy = new CoreLabel(original);
    assertEquals("sample", copy.word());
    assertEquals("NN", copy.tag());
    assertEquals("sample", copy.value());
  }
@Test
  public void testCopyConstructorFromLabel() {
    CoreLabel original = new CoreLabel();
    original.setWord("beta");
    original.setTag("VB");
    original.setCategory("VERB");
    original.setIndex(42);
    original.setBeginPosition(3);
    original.setEndPosition(8);
    original.setValue("beta");

    Label copy = new CoreLabel(original);
    CoreLabel result = (CoreLabel) copy;

    assertEquals("beta", result.word());
    assertEquals("VB", result.tag());
    assertEquals("VERB", result.category());
    assertEquals(42, result.index());
    assertEquals(3, result.beginPosition());
    assertEquals(8, result.endPosition());
    assertEquals("beta", result.value());
  }
@Test
  public void testCopyConstructorFromIndexedWord() {
    CoreLabel label = new CoreLabel();
    label.setWord("token");
    label.setIndex(10);
    IndexedWord indexedWord = new IndexedWord(label);

    CoreLabel copy = new CoreLabel(indexedWord);
    assertEquals("token", copy.word());
    assertEquals(10, copy.index());
  }
@Test(expected = UnsupportedOperationException.class)
  public void testSetFromStringThrowsException() {
    CoreLabel label = new CoreLabel();
    label.setFromString("test_string");
  }
@Test
  public void testBasicAnnotations() {
    CoreLabel label = new CoreLabel();

    label.setWord("run");
    label.setOriginalText("Run");
    label.setValue("run_value");
    label.setTag("VB");
    label.setNER("O");
    label.setLemma("run");
    label.setCategory("VERB");
    label.setIndex(1);
    label.setDocID("abc_doc");
    label.setBefore(" ");
    label.setAfter(".");

    assertEquals("run", label.word());
    assertEquals("Run", label.originalText());
    assertEquals("run_value", label.value());
    assertEquals("VB", label.tag());
    assertEquals("O", label.ner());
    assertEquals("run", label.lemma());
    assertEquals("VERB", label.category());
    assertEquals(1, label.index());
    assertEquals("abc_doc", label.docID());
    assertEquals(" ", label.before());
    assertEquals(".", label.after());
  }
@Test
  public void testCharacterOffsets() {
    CoreLabel label = new CoreLabel();
    label.setBeginPosition(5);
    label.setEndPosition(10);

    assertEquals(5, label.beginPosition());
    assertEquals(10, label.endPosition());
  }
@Test
  public void testIsNewlineTrueAndFalse() {
    CoreLabel label = new CoreLabel();
    label.setIsNewline(true);
    assertTrue(label.isNewline());

    CoreLabel label2 = new CoreLabel();
    label2.setIsNewline(false);
    assertFalse(label2.isNewline());
  }
@Test
  public void testIsMWTAndIsFirstMWT() {
    CoreLabel label1 = new CoreLabel();
    label1.setIsMWT(true);
    assertTrue(label1.isMWT());

    CoreLabel label2 = new CoreLabel();
    label2.setIsMWTFirst(true);
    assertTrue(label2.isMWTFirst());

    CoreLabel label3 = new CoreLabel();
    label3.setIsMWT(false);
    assertFalse(label3.isMWT());

    CoreLabel label4 = new CoreLabel();
    label4.setIsMWTFirst(false);
    assertFalse(label4.isMWTFirst());
  }
@Test
  public void testEmptyIndexOperations() {
    CoreLabel label = new CoreLabel();

    assertEquals(0, label.getEmptyIndex());
    assertFalse(label.hasEmptyIndex());

    label.setEmptyIndex(3);
    assertEquals(3, label.getEmptyIndex());
    assertTrue(label.hasEmptyIndex());
  }
@Test
  public void testSentIndex() {
    CoreLabel labelA = new CoreLabel();
    labelA.setSentIndex(5);
    assertEquals(5, labelA.sentIndex());

    CoreLabel labelB = new CoreLabel();
    assertEquals(-1, labelB.sentIndex());
  }
@Test
  public void testLabelFactoryNewLabelFromString() {
    LabelFactory factory = CoreLabel.factory();
    Label newLabel = factory.newLabel("token_value");
    assertEquals("token_value", newLabel.value());
  }
@Test
  public void testLabelFactoryLabelConversion() {
    LabelFactory factory = CoreLabel.factory();
    CoreLabel original = new CoreLabel();
    original.setWord("walk");
    original.setTag("VB");
    original.setIndex(9);
    original.setValue("walk");

    Label result = factory.newLabel(original);
    CoreLabel converted = (CoreLabel) result;

    assertEquals("walk", converted.word());
    assertEquals("VB", converted.tag());
    assertEquals(9, converted.index());
    assertEquals("walk", converted.value());
  }
@Test(expected = UnsupportedOperationException.class)
  public void testNewLabelFromStringThrows() {
    LabelFactory factory = CoreLabel.factory();
    factory.newLabelFromString("not_supported");
  }
@Test
  public void testToStringAllFormats() {
    CoreLabel label = new CoreLabel();
    label.setWord("home");
    label.setValue("home");
    label.setTag("NN");
    label.setNER("LOCATION");
    label.setLemma("home");
    label.setIndex(7);
    label.setEmptyIndex(2);

    String valueIndex = label.toString(CoreLabel.OutputFormat.VALUE_INDEX);
    assertEquals("home-7.2", valueIndex);

    String valueTag = label.toString(CoreLabel.OutputFormat.VALUE_TAG);
    assertEquals("home/NN", valueTag);

    String valueTagIndex = label.toString(CoreLabel.OutputFormat.VALUE_TAG_INDEX);
    assertEquals("home/NN-7.2", valueTagIndex);

    String word = label.toString(CoreLabel.OutputFormat.WORD);
    assertEquals("home", word);

    String lemmaIndex = label.toString(CoreLabel.OutputFormat.LEMMA_INDEX);
    assertEquals("home-7.2", lemmaIndex);

    String valueTagNer = label.toString(CoreLabel.OutputFormat.VALUE_TAG_NER);
    assertEquals("home/NN/LOCATION", valueTagNer);
  }
@Test(expected = IllegalArgumentException.class)
  public void testToStringWithInvalidFormat() {
    CoreLabel label = new CoreLabel();
    label.toString(null);
  }
@Test
  public void testParseStringKeysValid() {
    String[] keys = new String[2];
    keys[0] = "TextAnnotation";
    keys[1] = "PartOfSpeechAnnotation";

    Class[] result = CoreLabel.parseStringKeys(keys);
    assertEquals(2, result.length);
    assertEquals(CoreAnnotations.TextAnnotation.class, result[0]);
    assertEquals(CoreAnnotations.PartOfSpeechAnnotation.class, result[1]);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testParseStringKeysInvalid() {
    String[] keys = new String[1];
    keys[0] = "NotAnAnnotation";
    CoreLabel.parseStringKeys(keys);
  }
@Test
  public void testInitFromStringsWithClassArray() {
    String[] values = new String[3];
    values[0] = "brown";
    values[1] = "JJ";
    values[2] = "true";

    Class[] keys = new Class[3];
    keys[0] = CoreAnnotations.TextAnnotation.class;
    keys[1] = CoreAnnotations.PartOfSpeechAnnotation.class;
    keys[2] = CoreAnnotations.IsNewlineAnnotation.class;

    CoreLabel label = new CoreLabel(keys, values);
    assertEquals("brown", label.word());
    assertEquals("JJ", label.tag());
    assertTrue(label.isNewline());
  }
@Test(expected = UnsupportedOperationException.class)
  public void testInitFromStringsMismatchedLength() {
    String[] keys = {"TextAnnotation", "ValueAnnotation"};
    String[] values = {"hello"};
    CoreLabel label = new CoreLabel(keys, values);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testInitFromStringsBadKeyName() {
    String[] keys = {"InvalidAnnotation"};
    String[] values = {"somevalue"};
    CoreLabel label = new CoreLabel(keys, values);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testInitFromStringsBadValueFormat() {
    String[] keys = {"IndexAnnotation"};
    String[] values = {"not_a_number"};
    CoreLabel label = new CoreLabel(keys, values);
  }
@Test
  public void testWordFromStringUtilityMethod() {
    CoreLabel label = CoreLabel.wordFromString("walked");
    assertEquals("walked", label.word());
    assertEquals("walked", label.originalText());
    assertEquals("walked", label.value());
  }
@Test
  public void testToStringValueMapOmitsCategoryAndValue() {
    CoreLabel label = new CoreLabel();
    label.setWord("token");
    label.setValue("value");
    label.setTag("NN");
    label.setCategory("NP");

    String output = label.toString(CoreLabel.OutputFormat.VALUE_MAP);
    assertTrue(output.startsWith("value"));
    assertFalse(output.contains("ValueAnnotation"));
  }
@Test
  public void testToStringValueIndexMapOmitsIndexAndValueKeys() {
    CoreLabel label = new CoreLabel();
    label.setValue("dog");
    label.setIndex(3);
    label.setWord("dog");
    label.setTag("NN");
    label.setCategory("NP");

    String output = label.toString(CoreLabel.OutputFormat.VALUE_INDEX_MAP);
    assertTrue(output.contains("dog-3"));
    assertFalse(output.contains("ValueAnnotation"));
    assertFalse(output.contains("IndexAnnotation"));
  }
@Test
  public void testToStringMapSortedAlphabetically() {
    CoreLabel label = new CoreLabel();
    label.setWord("zebra");
    label.setTag("NN");
    label.setNER("ANIMAL");
    label.setValue("zebra");

    String mapStr = label.toString(CoreLabel.OutputFormat.MAP);
    int pos1 = mapStr.indexOf("NamedEntityTagAnnotation");
    int pos2 = mapStr.indexOf("PartOfSpeechAnnotation");
    int pos3 = mapStr.indexOf("TextAnnotation");

    assertTrue(pos1 < pos2);
    assertTrue(pos2 < pos3);
  }
@Test
  public void testEmptyIndexDefaultsToZero() {
    CoreLabel label = new CoreLabel();
    assertEquals(0, label.getEmptyIndex());
    assertFalse(label.hasEmptyIndex());
  }
@Test
  public void testGetStringWithDefaultValue() {
    CoreLabel label = new CoreLabel();
    String result = label.getString(CoreAnnotations.DocIDAnnotation.class, "defaultDoc");
    assertEquals("defaultDoc", result);
  }
@Test
  public void testGetStringWithoutDefaultReturnsEmpty() {
    CoreLabel label = new CoreLabel();
    String result = label.getString(CoreAnnotations.DocIDAnnotation.class);
    assertEquals("", result);
  }
@Test
  public void testNERConfidenceMapNullSafe() {
    CoreLabel label = new CoreLabel();
    Map<String, Double> confidences = label.nerConfidence();
    assertNull(confidences);
  }
@Test
  public void testGenericKeysAreInitiallyEmpty() {
    assertTrue(CoreLabel.genericKeys.isEmpty());
    assertTrue(CoreLabel.genericValues.isEmpty());
  }
@Test
  public void testOutputFormatAllIncludesAllKeys() {
    CoreLabel label = new CoreLabel();
    label.setWord("alpha");
    label.setTag("NN");
    label.setNER("X");
    label.setIndex(1);
    label.setValue("alpha");

    String all = label.toString(CoreLabel.OutputFormat.ALL);
    assertTrue(all.contains("TextAnnotation"));
    assertTrue(all.contains("ValueAnnotation"));
    assertTrue(all.contains("PartOfSpeechAnnotation"));
    assertTrue(all.contains("NamedEntityTagAnnotation"));
    assertTrue(all.contains("IndexAnnotation"));
  }
@Test
  public void testFactoryConvertsOldInterfaces() {
    LabelFactory factory = CoreLabel.factory();

    Label source = new Word("wordValue");
    Label result = factory.newLabel(source);

    assertEquals("wordValue", result.value());
    assertTrue(result instanceof CoreLabel);
    assertEquals("wordValue", ((CoreLabel) result).word());
  }
@Test
  public void testCoreLabelImplementsHasCategory() {
    CoreLabel label = new CoreLabel();
    assertTrue(label instanceof HasCategory);

    label.setCategory("S");
    assertEquals("S", label.category());
  }
@Test
  public void testConstructorWithKnownStringKeysAndValues() {
    String[] keys = {"TextAnnotation", "PartOfSpeechAnnotation"};
    String[] values = {"dogs", "NNS"};
    CoreLabel label = new CoreLabel(keys, values);
    assertEquals("dogs", label.word());
    assertEquals("NNS", label.tag());
  }
@Test
  public void testConstructorWithDoubleValueParsesCorrectly() {
    String[] keys = {"SomeDoubleAnnotation"};
    String[] values = {"3.14159"};

    Class<? extends CoreAnnotation<?>> doubleAnn = new CoreAnnotation<Double>() {
      @Override public Class<Double> getType() { return Double.class; }
    }.getClass();

    CoreLabel.genericKeys.put("SomeDoubleAnnotation", (Class<? extends CoreLabel.GenericAnnotation<String>>) doubleAnn);
    CoreLabel.genericValues.put((Class<? extends CoreLabel.GenericAnnotation<String>>) doubleAnn, "SomeDoubleAnnotation");

    CoreLabel label = new CoreLabel(keys, values);
//    Object value = label.get(doubleAnn);
//    assertNotNull(value);
//    assertEquals(3.14159, (Double) value, 0.00001);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testUnknownKeyThrowsDuringConstruction() {
    String[] keys = {"UnknownAnnotation"};
    String[] values = {"value"};
    new CoreLabel(keys, values);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testInvalidValueTypeThrowsDuringConstruction() {
    String[] keys = {"IndexAnnotation"};
    String[] values = {"NaN"};
    new CoreLabel(keys, values);
  }
@Test
  public void testToStringHandlesNullTagAndNERGracefully() {
    CoreLabel label = new CoreLabel();
    label.setValue("word");

    String valueTag = label.toString(CoreLabel.OutputFormat.VALUE_TAG);
    String valueTagNER = label.toString(CoreLabel.OutputFormat.VALUE_TAG_NER);

    assertEquals("word", valueTag);
    assertEquals("word", valueTagNER);
  }
@Test
  public void testToStringWithEmptyLabel() {
    CoreLabel label = new CoreLabel();
    String result = label.toString(CoreLabel.OutputFormat.VALUE);
    assertNull(result);
  }
@Test
  public void testToStringValueMapOmitsValueAnnotationOnly() {
    CoreLabel label = new CoreLabel();
    label.setValue("value");
    label.setWord("word");
    label.setTag("TAG");

    String output = label.toString(CoreLabel.OutputFormat.VALUE_MAP);
    assertFalse(output.contains("ValueAnnotation"));
    assertTrue(output.contains("TextAnnotation"));
    assertTrue(output.contains("PartOfSpeechAnnotation"));
  }
@Test
  public void testToStringWordIndexWithEmptyIndex() {
    CoreLabel label = new CoreLabel();
    label.setWord("test");
    label.setIndex(5);
    label.setEmptyIndex(0); 

    String output = label.toString(CoreLabel.OutputFormat.WORD_INDEX);
    assertEquals("test-5", output);
  }
@Test
  public void testToStringWordIndexWithNonZeroEmptyIndex() {
    CoreLabel label = new CoreLabel();
    label.setWord("test");
    label.setIndex(8);
    label.setEmptyIndex(1);

    String output = label.toString(CoreLabel.OutputFormat.WORD_INDEX);
    assertEquals("test-8.1", output);
  }
@Test
  public void testEmptyIndexExplicitlySetToZeroBehavesAsUnset() {
    CoreLabel label = new CoreLabel();
    label.setEmptyIndex(0);
    assertEquals(0, label.getEmptyIndex());
    assertTrue(label.hasEmptyIndex()); 
  }
@Test
  public void testSetAndGetBooleanAnnotationTrueThenFalse() {
    CoreLabel label = new CoreLabel();
    label.setIsNewline(true);
    assertTrue(label.isNewline());

    label.setIsNewline(false);
    assertFalse(label.isNewline());
  }
@Test
  public void testNamedEntityTagProbsAnnotationNullSafe() {
    CoreLabel label = new CoreLabel();
    Map<String, Double> probs = label.nerConfidence();
    assertNull(probs);
  }
@Test
  public void testOutputFormatMapEmptyLabel() {
    CoreLabel label = new CoreLabel();
    String output = label.toString(CoreLabel.OutputFormat.MAP);
    assertEquals("{}", output);
  }
@Test
  public void testOutputFormatValueMapWithOnlyValue() {
    CoreLabel label = new CoreLabel();
    label.setValue("x");
    String output = label.toString(CoreLabel.OutputFormat.VALUE_MAP);
    assertEquals("x{}", output);  
  }
@Test
  public void testOutputFormatAllWithVariousAnnotations() {
    CoreLabel label = new CoreLabel();
    label.setWord("tiger");
    label.setTag("NN");
    label.setValue("tiger");
    label.setIndex(123);
    label.setNER("ANIMAL");

    String output = label.toString(CoreLabel.OutputFormat.ALL);
    assertTrue(output.contains("TextAnnotation"));
    assertTrue(output.contains("NamedEntityTagAnnotation"));
    assertTrue(output.contains("IndexAnnotation"));
    assertTrue(output.contains("ValueAnnotation"));
  }
@Test
  public void testToStringValueIndexWithOnlyValue() {
    CoreLabel label = new CoreLabel();
    label.setValue("lion");

    String result = label.toString(CoreLabel.OutputFormat.VALUE_INDEX);
    assertEquals("lion", result);
  }
@Test
  public void testToStringValueTagWithOnlyValue() {
    CoreLabel label = new CoreLabel();
    label.setValue("wolf");

    String result = label.toString(CoreLabel.OutputFormat.VALUE_TAG);
    assertEquals("wolf", result);
  }
@Test
  public void testToStringValueTagIndexWithOnlyValue() {
    CoreLabel label = new CoreLabel();
    label.setValue("fox");

    String result = label.toString(CoreLabel.OutputFormat.VALUE_TAG_INDEX);
    assertEquals("fox", result);
  }
@Test
  public void testToStringValueTagIndexWithAllFields() {
    CoreLabel label = new CoreLabel();
    label.setValue("bird");
    label.setTag("NN");
    label.setIndex(22);
    label.setEmptyIndex(3);

    String result = label.toString(CoreLabel.OutputFormat.VALUE_TAG_INDEX);
    assertEquals("bird/NN-22.3", result);
  }
@Test
  public void testToStringLemmaIndexOnlyLemma() {
    CoreLabel label = new CoreLabel();
    label.setLemma("run");

    String output = label.toString(CoreLabel.OutputFormat.LEMMA_INDEX);
    assertEquals("run", output);
  }
@Test
  public void testToStringLemmaIndexFull() {
    CoreLabel label = new CoreLabel();
    label.setLemma("jump");
    label.setIndex(7);
    label.setEmptyIndex(2);

    String output = label.toString(CoreLabel.OutputFormat.LEMMA_INDEX);
    assertEquals("jump-7.2", output);
  }
@Test
  public void testToStringValueTagNERWithMissingNER() {
    CoreLabel label = new CoreLabel();
    label.setValue("bank");
    label.setTag("NN");

    String output = label.toString(CoreLabel.OutputFormat.VALUE_TAG_NER);
    assertEquals("bank/NN", output);
  }
@Test
  public void testToStringValueTagNERWithOnlyValue() {
    CoreLabel label = new CoreLabel();
    label.setValue("tree");

    String output = label.toString(CoreLabel.OutputFormat.VALUE_TAG_NER);
    assertEquals("tree", output);
  }
//@Test(expected = IllegalArgumentException.class)
//  public void testToStringWithUnsupportedOutputFormat() {
//    CoreLabel label = new CoreLabel();
//
//    CoreLabel.OutputFormat fakeFormat = CoreLabel.OutputFormat.valueOf("VALUE_INDEX");
//    CoreLabel.OutputFormat corrupted = new CoreLabel.OutputFormat() {
//      @Override
//      public String name() { return "INVALID"; }
//
//      @Override
//      public int ordinal() { return 999; }
//    };
//
//    label.toString(corrupted);
//  }
@Test
  public void testIndexNegativeWhenNotSet() {
    CoreLabel label = new CoreLabel();
    int index = label.index();
    assertEquals(-1, index);
  }
@Test
  public void testBeginPositionDefaultWhenUnset() {
    CoreLabel label = new CoreLabel();
    int begin = label.beginPosition();
    assertEquals(-1, begin);
  }
@Test
  public void testEndPositionDefaultWhenUnset() {
    CoreLabel label = new CoreLabel();
    int end = label.endPosition();
    assertEquals(-1, end);
  }
@Test
  public void testGetStringReturnsExplicitDefault() {
    CoreLabel label = new CoreLabel();
    String defaultValue = label.getString(CoreAnnotations.TextAnnotation.class, "DEFAULT_TEXT");
    assertEquals("DEFAULT_TEXT", defaultValue);
  }
@Test
  public void testSetIndexAndGetIndexConsistency() {
    CoreLabel label = new CoreLabel();
    label.setIndex(42);
    int index = label.index();
    assertEquals(42, index);
  }
@Test
  public void testSetBeginEndPositionsAndVerify() {
    CoreLabel label = new CoreLabel();
    label.setBeginPosition(100);
    label.setEndPosition(150);
    assertEquals(100, label.beginPosition());
    assertEquals(150, label.endPosition());
  }
@Test
  public void testSetDocIDThenGetDocID() {
    CoreLabel label = new CoreLabel();
    label.setDocID("doc-001");
    assertEquals("doc-001", label.docID());
  }
@Test
  public void testSetNERThenClearAndCheckNull() {
    CoreLabel label = new CoreLabel();
    label.setNER("PERSON");
    assertEquals("PERSON", label.ner());

    label.setNER(null);
    assertNull(label.ner());
  }
@Test
  public void testSetAndUnsetBooleanAnnotations() {
    CoreLabel label = new CoreLabel();
    label.setIsMWT(true);
    assertTrue(label.isMWT());

    label.setIsMWT(false);
    assertFalse(label.isMWT());

    label.setIsMWTFirst(true);
    assertTrue(label.isMWTFirst());

    label.setIsMWTFirst(false);
    assertFalse(label.isMWTFirst());
  }
@Test
  public void testDifferentEmptyIndexValues() {
    CoreLabel label = new CoreLabel();
    label.setEmptyIndex(0);
    assertEquals(0, label.getEmptyIndex());

    label.setEmptyIndex(5);
    assertEquals(5, label.getEmptyIndex());
  }
@Test
  public void testHasEmptyIndexTrueForZeroValue() {
    CoreLabel label = new CoreLabel();
    label.setEmptyIndex(0);
    assertTrue(label.hasEmptyIndex());
  }
@Test
  public void testSetAndGetBeforeAndAfter() {
    CoreLabel label = new CoreLabel();
    label.setBefore("prev");
    label.setAfter("next");

    assertEquals("prev", label.before());
    assertEquals("next", label.after());
  }
@Test
  public void testToStringWithValueIndexFormatOnlyIndexSet() {
    CoreLabel label = new CoreLabel();
    label.setValue("bird");
    label.setIndex(12);

    String output = label.toString(CoreLabel.OutputFormat.VALUE_INDEX);
    assertEquals("bird-12", output);
  }
@Test
  public void testToStringWithValueIndexWithZeroEmptyIndex() {
    CoreLabel label = new CoreLabel();
    label.setValue("fish");
    label.setIndex(3);
    label.setEmptyIndex(0);

    String output = label.toString(CoreLabel.OutputFormat.VALUE_INDEX);
    assertEquals("fish-3", output);
  }
//@Test
//  public void testFactoryNewLabelWithNullValue() {
//    CoreLabel.CoreLabelFactory factory = new CoreLabel.CoreLabelFactory();
//    Label label = factory.newLabel((String) null);
//    assertNull(label.value());
//  }
//@Test
//  public void testFactoryNewLabelWithNullOldLabel() {
//    CoreLabel.CoreLabelFactory factory = new CoreLabel.CoreLabelFactory();
//    Label newLabel = factory.newLabel((Label) null);
//    assertNull(newLabel.value());
//  }
//@Test
//  public void testFactoryNewLabelWithOnlyWordFromOldLabel() {
//    CoreLabel.CoreLabelFactory factory = new CoreLabel.CoreLabelFactory();
//    Label label = new Word("testWord");
//    Label result = factory.newLabel(label);
//
//    assertTrue(result instanceof CoreLabel);
//    CoreLabel coreLabel = (CoreLabel) result;
//    assertEquals("testWord", coreLabel.word());
//    assertEquals("testWord", coreLabel.value());
//  }
@Test
  public void testToStringWordFormatWithOnlyWordSet() {
    CoreLabel label = new CoreLabel();
    label.setWord("banana");

    String result = label.toString(CoreLabel.OutputFormat.WORD);
    assertEquals("banana", result);
  }
@Test
  public void testToStringWithAllFormatIncludesAllKeys() {
    CoreLabel label = new CoreLabel();
    label.setWord("alpha");
    label.setTag("NN");
    label.setIndex(7);
    label.setValue("alpha");
    label.setNER("ANIMAL");
    label.setCategory("NOUN");

    String output = label.toString(CoreLabel.OutputFormat.ALL);

    assertTrue(output.contains("TextAnnotation"));
    assertTrue(output.contains("PartOfSpeechAnnotation"));
    assertTrue(output.contains("NamedEntityTagAnnotation"));
    assertTrue(output.contains("IndexAnnotation"));
    assertTrue(output.contains("ValueAnnotation"));
    assertTrue(output.contains("CategoryAnnotation"));
  }
@Test
  public void testToStringReturnsCorrectOrderForTagAndNER() {
    CoreLabel label = new CoreLabel();
    label.setValue("value");
    label.setTag("TAG");
    label.setNER("NER");

    String result = label.toString(CoreLabel.OutputFormat.VALUE_TAG_NER);
    assertEquals("value/TAG/NER", result);
  }
@Test
  public void testToStringReturnsCorrectResultWithTagOnly() {
    CoreLabel label = new CoreLabel();
    label.setValue("hello");
    label.setTag("XX");

    String result = label.toString(CoreLabel.OutputFormat.VALUE_TAG);
    assertEquals("hello/XX", result);
  }
@Test
  public void testSetOriginalTextThenRetrieve() {
    CoreLabel label = new CoreLabel();
    label.setOriginalText("original");
    assertEquals("original", label.originalText());
  }
@Test
  public void testSetAndUnsetValueAnnotation() {
    CoreLabel label = new CoreLabel();
    label.setValue("someValue");
    assertEquals("someValue", label.value());
    label.set(CoreAnnotations.ValueAnnotation.class, null);
    assertNull(label.value());
  }
@Test
  public void testInitFromStringsWithBooleanValueTrueFalse() {
    Class[] keys = new Class[2];
    keys[0] = CoreAnnotations.IsNewlineAnnotation.class;
    keys[1] = CoreAnnotations.IsMultiWordTokenAnnotation.class;

    String[] values = new String[2];
    values[0] = "true";
    values[1] = "false";

    CoreLabel label = new CoreLabel(keys, values);
    assertTrue(label.isNewline());
    assertFalse(label.isMWT());
  }
@Test(expected = UnsupportedOperationException.class)
  public void testInitFromStringsWithUnsupportedValueType() {
    Class[] keys = new Class[1];
    keys[0] = CoreLabel.GenericAnnotation.class;
    String[] values = new String[1];
    values[0] = "someValue";

    CoreLabel label = new CoreLabel(keys, values);
  }
@Test
  public void testToStringWithEmptyMapInValueIndexMap() {
    CoreLabel label = new CoreLabel();
    label.setValue("val");
    label.setIndex(1);

    String result = label.toString(CoreLabel.OutputFormat.VALUE_INDEX_MAP);
    assertEquals("val-1", result);
  }
@Test
  public void testToStringWithExtraAnnotationsValueIndexMap() {
    CoreLabel label = new CoreLabel();
    label.setValue("word");
    label.setIndex(10);
    label.setBefore("[[");
    label.setAfter("]]");

    String result = label.toString(CoreLabel.OutputFormat.VALUE_INDEX_MAP);
    assertTrue(result.contains("BeforeAnnotation"));
    assertTrue(result.contains("AfterAnnotation"));
    assertFalse(result.contains("IndexAnnotation"));
    assertFalse(result.contains("ValueAnnotation"));
  }
@Test
  public void testSetAndGetMultipleStringKeys() {
    CoreLabel label = new CoreLabel();
    label.setWord("apple");
    label.setOriginalText("Apple");
    label.setValue("fruit");
    assertEquals("apple", label.word());
    assertEquals("Apple", label.originalText());
    assertEquals("fruit", label.value());
  }
@Test
  public void testEmptyKeyAndNullValueInInitFromStringsThrows() {
    String[] keys = new String[1];
    keys[0] = null;

    String[] values = new String[1];
    values[0] = null;

    try {
      new CoreLabel(keys, values);
      fail("Expected UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      assertTrue(e.getMessage().contains("Unknown key"));
    }
  }
@Test
  public void testEmptyIndexIncludedWhenNonZero() {
    CoreLabel label = new CoreLabel();
    label.setValue("test");
    label.setIndex(4);
    label.setEmptyIndex(1);
    String str = label.toString(CoreLabel.OutputFormat.VALUE_INDEX);
    assertEquals("test-4.1", str);
  }
@Test
  public void testFactoryNewLabelPreservesOffsets() {
    CoreLabel original = new CoreLabel();
    original.setWord("bird");
    original.setBeginPosition(5);
    original.setEndPosition(9);

    LabelFactory factory = CoreLabel.factory();
    Label copy = factory.newLabel(original);
    CoreLabel newLabel = (CoreLabel) copy;

    assertEquals(5, newLabel.beginPosition());
    assertEquals(9, newLabel.endPosition());
  }
@Test
  public void testToStringHandlesNullInNERAndTagGracefully() {
    CoreLabel label = new CoreLabel();
    label.setValue("valueOnly");

    assertEquals("valueOnly", label.toString(CoreLabel.OutputFormat.VALUE_TAG));
    assertEquals("valueOnly", label.toString(CoreLabel.OutputFormat.VALUE_TAG_INDEX));
    assertEquals("valueOnly", label.toString(CoreLabel.OutputFormat.VALUE_TAG_NER));
  }
@Test
  public void testValueTagNERHandlesAllNullsGracefully() {
    CoreLabel label = new CoreLabel();
    String result = label.toString(CoreLabel.OutputFormat.VALUE_TAG_NER);
    assertNull(result);
  }
@Test
  public void testTagAndNERNullButWordPresent() {
    CoreLabel label = new CoreLabel();
    label.setValue("dog");

    String result = label.toString(CoreLabel.OutputFormat.VALUE_TAG_NER);
    assertEquals("dog", result);
  }
@Test
  public void testInitFromStringsParsesIntegerCorrectly() {
    Class[] keys = new Class[1];
    keys[0] = CoreAnnotations.IndexAnnotation.class;
    String[] values = new String[1];
    values[0] = "123";

    CoreLabel label = new CoreLabel(keys, values);
    assertEquals(123, label.index());
  }
@Test(expected = UnsupportedOperationException.class)
  public void testInitFromStringsWithLengthMismatchThrows() {
    Class[] keys = new Class[2];
    keys[0] = CoreAnnotations.TextAnnotation.class;
    keys[1] = CoreAnnotations.ValueAnnotation.class;

    String[] values = new String[1];
    values[0] = "value";

    new CoreLabel(keys, values);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testInitFromStringsParsesBadLongValue() {
    Class[] keys = new Class[1];
    keys[0] = CoreAnnotations.CharacterOffsetBeginAnnotation.class; 
    String[] values = new String[1];
    values[0] = "notAnInt";

    new CoreLabel(keys, values);
  }
@Test
  public void testSetTagNullThenGet() {
    CoreLabel label = new CoreLabel();
    label.setTag(null);
    assertNull(label.tag());
  }
@Test
  public void testToStringValueOnlyWhenIndexAndEmptyIndexNotSet() {
    CoreLabel label = new CoreLabel();
    label.setValue("plain");

    String output = label.toString(CoreLabel.OutputFormat.VALUE_INDEX);
    assertEquals("plain", output);
  }
@Test
  public void testToStringMapWithMultipleCoreKeysSortedAlphabetically() {
    CoreLabel label = new CoreLabel();
    label.setWord("word");
    label.setTag("NN");
    label.setNER("ENTITY");
    label.setCategory("X");
    label.setBeginPosition(2);

    String mapOutput = label.toString(CoreLabel.OutputFormat.MAP);
    int pos1 = mapOutput.indexOf("CategoryAnnotation");
    int pos2 = mapOutput.indexOf("NamedEntityTagAnnotation");
    int pos3 = mapOutput.indexOf("PartOfSpeechAnnotation");
    int pos4 = mapOutput.indexOf("TextAnnotation");

    assertTrue(pos1 < pos2);
    assertTrue(pos2 < pos3);
    assertTrue(pos3 < pos4);
  }
@Test
  public void testMultipleSetValueOverridesPrevious() {
    CoreLabel label = new CoreLabel();
    label.setValue("first");
    assertEquals("first", label.value());
    label.setValue("second");
    assertEquals("second", label.value());
  }
@Test
  public void testSetWordClearsLemmaBehavior() {
    CoreLabel label = new CoreLabel();
    label.setLemma("running");
    assertEquals("running", label.lemma());
    label.setWord("walk");
    
    assertEquals("running", label.lemma());
  }
@Test
  public void testToStringValueTagIndexWithOnlyTag() {
    CoreLabel label = new CoreLabel();
    label.setValue("token");
    label.setTag("JJ");
    String result = label.toString(CoreLabel.OutputFormat.VALUE_TAG_INDEX);
    assertEquals("token/JJ", result);
  }
@Test
  public void testToStringValueTagOnly() {
    CoreLabel label = new CoreLabel();
    label.setValue("sing");
    label.setTag("VB");
    String result = label.toString(CoreLabel.OutputFormat.VALUE_TAG);
    assertEquals("sing/VB", result);
  }
@Test
  public void testToStringWhenOnlyIndexSet() {
    CoreLabel label = new CoreLabel();
    label.setValue("test");
    label.setIndex(9);
    String result = label.toString(CoreLabel.OutputFormat.VALUE_INDEX);
    assertEquals("test-9", result);
  }
@Test
  public void testToStringWordIndexNullWord() {
    CoreLabel label = new CoreLabel();
    label.setIndex(5);
    String result = label.toString(CoreLabel.OutputFormat.WORD_INDEX);
    assertNull(result);
  }
@Test
  public void testToStringWordIndexNoIndexSet() {
    CoreLabel label = new CoreLabel();
    label.setWord("leaf");
    String result = label.toString(CoreLabel.OutputFormat.WORD_INDEX);
    assertEquals("leaf", result);
  }
@Test
  public void testSetCategoryNullThenRetrieve() {
    CoreLabel label = new CoreLabel();
    label.setCategory(null);
    assertNull(label.category());
  }
@Test
  public void testToStringAllWithEmptyLabel() {
    CoreLabel label = new CoreLabel();
    String result = label.toString(CoreLabel.OutputFormat.ALL);
    assertEquals("", result);
  }
@Test
  public void testGetEmptyIndexWhenNotPresentReturnsZero() {
    CoreLabel label = new CoreLabel();
    assertEquals(0, label.getEmptyIndex());
  }
@Test
  public void testSetAndRetrieveEmptyIndexPositiveValue() {
    CoreLabel label = new CoreLabel();
    label.setEmptyIndex(10);
    assertEquals(10, label.getEmptyIndex());
  }
@Test
  public void testSetAndGetIndexAnnotationExplicitNull() {
    CoreLabel label = new CoreLabel();
    label.set(CoreAnnotations.IndexAnnotation.class, null);
    assertEquals(-1, label.index());
  }
@Test
  public void testSettingNonStandardAnnotationAndRetrievingRaw() {
    CoreLabel label = new CoreLabel();
    label.set(CoreAnnotations.AfterAnnotation.class, "aftermath");
    Object rawValue = label.get(CoreAnnotations.AfterAnnotation.class);
    assertEquals("aftermath", rawValue);
  }
@Test
  public void testInvalidatePreviouslySetAnnotations() {
    CoreLabel label = new CoreLabel();
    label.setValue("oldValue");
    label.set(CoreAnnotations.ValueAnnotation.class, null);
    assertNull(label.value());
  }
@Test
  public void testValueMapRemovesOnlyValueAnnotation() {
    CoreLabel label = new CoreLabel();
    label.setValue("word");
    label.setTag("VBZ");
    label.setWord("word");

    String result = label.toString(CoreLabel.OutputFormat.VALUE_MAP);
    assertFalse(result.contains("ValueAnnotation"));
    assertTrue(result.contains("TextAnnotation"));
    assertTrue(result.contains("PartOfSpeechAnnotation"));
  }
@Test
  public void testValueTagIndexWithIndexAndEmptyIndexPresent() {
    CoreLabel label = new CoreLabel();
    label.setValue("ex");
    label.setTag("NN");
    label.setIndex(15);
    label.setEmptyIndex(2);
    String output = label.toString(CoreLabel.OutputFormat.VALUE_TAG_INDEX);
    assertEquals("ex/NN-15.2", output);
  }
@Test
  public void testSetNullNERAndRetrieve() {
    CoreLabel label = new CoreLabel();
    label.setNER(null);
    assertNull(label.ner());
  }
@Test
  public void testSetLemmaAndRetrieveNullSafe() {
    CoreLabel label = new CoreLabel();
    label.setLemma("eat");
    assertEquals("eat", label.lemma());
    label.setLemma(null);
    assertNull(label.lemma());
  }
@Test
  public void testCloneUsingCopyConstructorPreservesCustomKeys() {
    CoreLabel label = new CoreLabel();
    label.setWord("clone");
    label.setNER("PERSON");

    CoreLabel copied = new CoreLabel(label);
    assertEquals("clone", copied.word());
    assertEquals("PERSON", copied.ner());
  }
@Test
  public void testToStringWordFormatNullWord() {
    CoreLabel label = new CoreLabel();
    String result = label.toString(CoreLabel.OutputFormat.WORD);
    assertNull(result);
  }
@Test
  public void testNewLabelFromLabelWithFullLegacyInterfaces() {
//    CoreLabel.CoreLabelFactory factory = new CoreLabel.CoreLabelFactory();

    CoreLabel legacy = new CoreLabel();
    legacy.setWord("walk");
    legacy.setTag("VB");
    legacy.setCategory("verb");
    legacy.setIndex(11);
    legacy.setBeginPosition(1);
    legacy.setEndPosition(4);
    legacy.setValue("walk");

//    Label result = factory.newLabel(legacy);

//    assertTrue(result instanceof CoreLabel);
//    CoreLabel copied = (CoreLabel) result;
//
//    assertEquals("VB", copied.tag());
//    assertEquals("verb", copied.category());
//    assertEquals(11, copied.index());
//    assertEquals(1, copied.beginPosition());
//    assertEquals(4, copied.endPosition());
  }
@Test
  public void testInitFromStringsSetsCoNLLUFeaturesCorrectly() {
    Class[] keys = new Class[] { CoreAnnotations.CoNLLUFeats.class };
    String[] values = new String[] { "Number=Plur|Gender=Fem" };

    CoreLabel label = new CoreLabel(keys, values);
    Object feats = label.get(CoreAnnotations.CoNLLUFeats.class);

    assertNotNull(feats);
    assertTrue(feats instanceof CoNLLUFeatures);
    assertEquals("Number=Plur|Gender=Fem", feats.toString());
  }
@Test
  public void testToStringValueIndexWithIndexZero() {
    CoreLabel label = new CoreLabel();
    label.setValue("zero");
    label.setIndex(0);
    String result = label.toString(CoreLabel.OutputFormat.VALUE_INDEX);
    assertEquals("zero-0", result);
  }
@Test
  public void testToStringWithNullValue() {
    CoreLabel label = new CoreLabel();
    label.setIndex(7);
    String result = label.toString(CoreLabel.OutputFormat.VALUE_INDEX);
    assertEquals("-7", result); 
  }
@Test
  public void testToStringValueTagIndexNullValueTag() {
    CoreLabel label = new CoreLabel();
    label.setIndex(9);
    String result = label.toString(CoreLabel.OutputFormat.VALUE_TAG_INDEX);
    assertEquals("-9", result);
  }
@Test
  public void testToStringValueTagNERWithTagNullNERSet() {
    CoreLabel label = new CoreLabel();
    label.setValue("ent");
    label.setNER("ORG");
    String result = label.toString(CoreLabel.OutputFormat.VALUE_TAG_NER);
    assertEquals("ent/ORG", result); 
  }
@Test
  public void testSetNullWordAndOriginalText() {
    CoreLabel label = new CoreLabel();
    label.setWord(null);
    label.setOriginalText(null);
    assertNull(label.word());
    assertNull(label.originalText());
  }
@Test
  public void testSetAndClearIsFirstWordOfMWT() {
    CoreLabel label = new CoreLabel();
    label.setIsMWTFirst(true);
    assertTrue(label.isMWTFirst());
    label.set(CoreAnnotations.IsFirstWordOfMWTAnnotation.class, null);
    assertNull(label.isMWTFirst());
  }
//@Test
//  public void testLabelFactoryCreatesLabelWithNullFields() {
//    CoreLabel.CoreLabelFactory factory = new CoreLabel.CoreLabelFactory();
//    Label result = factory.newLabel(null);
//    assertNotNull(result);
//    assertTrue(result instanceof CoreLabel);
//    assertNull(result.value());
//  }
//@Test
//  public void testLabelFactoryHandlesUnknownLegacyLabel() {
//    class UnknownLegacyLabel implements Label {
//      @Override public String value() { return "legacy"; }
//      @Override public void setValue(String value) { }
//    }
//
//    CoreLabel.CoreLabelFactory factory = new CoreLabel.CoreLabelFactory();
//    Label result = factory.newLabel(new UnknownLegacyLabel());
//    assertTrue(result instanceof CoreLabel);
//    assertEquals("legacy", result.value());
//  }
@Test
  public void testToStringWordIndexWithNoWordOrIndex() {
    CoreLabel label = new CoreLabel();
    String result = label.toString(CoreLabel.OutputFormat.WORD_INDEX);
    assertNull(result);
  }
@Test
  public void testToStringLemmaIndexWithoutIndex() {
    CoreLabel label = new CoreLabel();
    label.setLemma("run");
    String result = label.toString(CoreLabel.OutputFormat.LEMMA_INDEX);
    assertEquals("run", result);
  }
@Test
  public void testToStringWithZeroEmptyIndexIncluded() {
    CoreLabel label = new CoreLabel();
    label.setValue("text");
    label.setIndex(1);
    label.setEmptyIndex(0);
    String result = label.toString(CoreLabel.OutputFormat.VALUE_INDEX);
    assertEquals("text-1", result);
  }
@Test
  public void testToStringWithEmptyIndexNonZeroIncluded() {
    CoreLabel label = new CoreLabel();
    label.setValue("text");
    label.setIndex(1);
    label.setEmptyIndex(5);
    String result = label.toString(CoreLabel.OutputFormat.VALUE_INDEX);
    assertEquals("text-1.5", result);
  }
@Test
  public void testAddMultipleNumericTypesToLabel() {
    CoreLabel label = new CoreLabel();
    label.set(CoreAnnotations.IndexAnnotation.class, 100);
    label.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    label.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);
    assertEquals(100, label.index());
    assertEquals(5, label.beginPosition());
    assertEquals(12, label.endPosition());
  }
@Test
  public void testSetNERConfidenceMapManually() {
    CoreLabel label = new CoreLabel();
    Map<String, Double> probs = new java.util.HashMap<>();
    probs.put("ORG", 0.7);
    label.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, probs);
    assertNotNull(label.nerConfidence());
    assertEquals(0.7, label.nerConfidence().get("ORG"), 0.0001);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testInitFromStringsThrowsOnNullKey() {
    String[] keys = new String[] {null};
    String[] values = new String[] {"text"};
    new CoreLabel(keys, values);
  }
@Test
  public void testToStringAllWithNullEntries() {
    CoreLabel label = new CoreLabel();
    label.set(CoreAnnotations.ValueAnnotation.class, null);
    label.set(CoreAnnotations.IndexAnnotation.class, null);
    String output = label.toString(CoreLabel.OutputFormat.ALL);
    assertTrue(output.contains("ValueAnnotation:null"));
    assertTrue(output.contains("IndexAnnotation:null"));
  }
@Test
  public void testToStringAllSkipsEmptyMap() {
    CoreLabel label = new CoreLabel();
    String output = label.toString(CoreLabel.OutputFormat.ALL);
    assertEquals("", output);
  }
//@Test
//  public void testNewLabelPreservesCategoryFromLegacyInterface() {
//    CoreLabel.CoreLabelFactory factory = new CoreLabel.CoreLabelFactory();
//    class OldLabel implements Label, HasCategory {
//      public String category = "NP";
//      public String value = "dog";
//      @Override public String category() { return category; }
//      @Override public void setCategory(String category) { this.category = category; }
//      @Override public String value() { return value; }
//      @Override public void setValue(String v) { this.value = v; }
//    }
//
//    OldLabel old = new OldLabel();
//    CoreLabel converted = (CoreLabel) factory.newLabel(old);
//    assertEquals("NP", converted.category());
//    assertEquals("dog", converted.value());
//  }
}