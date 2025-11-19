package edu.stanford.nlp.ling;

import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.trees.ud.CoNLLUFeatures;
import edu.stanford.nlp.util.CoreMap;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

import static org.junit.Assert.*;

public class CoreLabel_3_GPTLLMTest {

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
  public void testCopyConstructorWithCoreLabel() {
    CoreLabel original = new CoreLabel();
    original.setWord("fox");
    original.setTag("NN");
    CoreLabel copied = new CoreLabel(original);
    assertEquals("fox", copied.word());
    assertEquals("NN", copied.tag());
  }
@Test
  public void testCopyConstructorWithCoreMap() {
    CoreLabel original = new CoreLabel();
    original.setNER("PERSON");
    CoreMap map = original;
    CoreLabel copied = new CoreLabel(map);
    assertEquals("PERSON", copied.ner());
  }
@Test
  public void testConstructorWithStringKeysAndValues_Success() {
    String[] keys = new String[] { "Text", "PartOfSpeech" };
    String[] values = new String[] { "dog", "NN" };
    CoreLabel label = new CoreLabel(keys, values);
    assertEquals("dog", label.word());
    assertEquals("NN", label.tag());
  }
@Test(expected = UnsupportedOperationException.class)
  public void testConstructorWithStringKeysAndValues_MismatchedLengths() {
    String[] keys = new String[] { "Text" };
    String[] values = new String[] { "dog", "extra" };
    new CoreLabel(keys, values);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testConstructorWithUnknownStringKey() {
    String[] keys = new String[] { "UnknownKey" };
    String[] values = new String[] { "value" };
    new CoreLabel(keys, values);
  }
@Test
  public void testSettersAndGetters_BasicFields() {
    CoreLabel label = new CoreLabel();
    label.setWord("quick");
    label.setTag("JJ");
    label.setValue("quick");
    label.setOriginalText("Quick");
    assertEquals("quick", label.word());
    assertEquals("JJ", label.tag());
    assertEquals("quick", label.value());
    assertEquals("Quick", label.originalText());
  }
@Test
  public void testNERFields() {
    CoreLabel label = new CoreLabel();
    label.setNER("ORG");
    assertEquals("ORG", label.ner());

    Map<String, Double> probs = new HashMap<String, Double>();
    probs.put("ORG", 0.92);
    label.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, probs);
    Map<String, Double> returned = label.nerConfidence();
    assertTrue(returned.containsKey("ORG"));
    assertEquals(0.92, returned.get("ORG"), 0.001);
  }
@Test
  public void testSetIsNewlineAndIsMWT() {
    CoreLabel label = new CoreLabel();
    label.setIsNewline(true);
    label.setIsMWT(true);
    label.setIsMWTFirst(true);
    assertTrue(label.isNewline());
    assertTrue(label.isMWT());
    assertTrue(label.isMWTFirst());
  }
@Test
  public void testSetIndexAndEmptyIndex() {
    CoreLabel label = new CoreLabel();
    label.setIndex(3);
    label.setEmptyIndex(1);
    assertEquals(3, label.index());
    assertEquals(1, label.getEmptyIndex());
    assertTrue(label.hasEmptyIndex());
  }
@Test
  public void testToString_VALUE_TAG_INDEX() {
    CoreLabel label = new CoreLabel();
    label.setValue("hello");
    label.setTag("NN");
    label.setIndex(15);
    label.setEmptyIndex(2);
    String actual = label.toString(CoreLabel.OutputFormat.VALUE_TAG_INDEX);
    assertEquals("hello/NN-15.2", actual);
  }
@Test
  public void testToString_WORD_INDEX() {
    CoreLabel label = new CoreLabel();
    label.setWord("status");
    label.setIndex(4);
    String result = label.toString(CoreLabel.OutputFormat.WORD_INDEX);
    assertEquals("status-4", result);
  }
@Test
  public void testSetBeginAndEndPosition() {
    CoreLabel label = new CoreLabel();
    label.setBeginPosition(10);
    label.setEndPosition(20);
    assertEquals(10, label.beginPosition());
    assertEquals(20, label.endPosition());
  }
@Test
  public void testSetSentIndex() {
    CoreLabel label = new CoreLabel();
    label.setSentIndex(5);
    assertEquals(5, label.sentIndex());
  }
@Test
  public void testSetAndGetLemmaAndNER() {
    CoreLabel label = new CoreLabel();
    label.setLemma("run");
    label.setNER("ACTION");
    assertEquals("run", label.lemma());
    assertEquals("ACTION", label.ner());
  }
@Test
  public void testSetDocID() {
    CoreLabel label = new CoreLabel();
    label.setDocID("doc456");
    assertEquals("doc456", label.docID());
  }
@Test
  public void testToStringFormat_VALUE_INDEX() {
    CoreLabel label = new CoreLabel();
    label.setValue("walk");
    label.setIndex(9);
    label.setEmptyIndex(2);
    String result = label.toString(CoreLabel.OutputFormat.VALUE_INDEX);
    assertEquals("walk-9.2", result);
  }
@Test
  public void testValueTagNERFormat() {
    CoreLabel label = new CoreLabel();
    label.setValue("Google");
    label.setTag("NNP");
    label.setNER("ORG");
    String result = label.toString(CoreLabel.OutputFormat.VALUE_TAG_NER);
    assertEquals("Google/NNP/ORG", result);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testSetFromStringUnsupported() {
    CoreLabel label = new CoreLabel();
    label.setFromString("example");
  }
@Test
  public void testToString_MAP() {
    CoreLabel label = new CoreLabel();
    label.setWord("hello");
    String output = label.toString(CoreLabel.OutputFormat.MAP);
    assertTrue(output.contains("TextAnnotation"));
    assertTrue(output.contains("hello"));
  }
@Test
  public void testToString_VALUE_MAP() {
    CoreLabel label = new CoreLabel();
    label.setWord("hi");
    label.setValue("hi");
    label.setNER("GREETING");
    String output = label.toString(CoreLabel.OutputFormat.VALUE_MAP);
    assertTrue(output.startsWith("hi"));
    assertFalse(output.contains("ValueAnnotation"));
    assertTrue(output.contains("NamedEntityTagAnnotation"));
  }
@Test
  public void testToString_LEMMA_INDEX_EmptyIndexMissing() {
    CoreLabel label = new CoreLabel();
    label.setLemma("eat");
    label.setIndex(1);
    String output = label.toString(CoreLabel.OutputFormat.LEMMA_INDEX);
    assertEquals("eat-1", output);
  }
@Test
  public void testToString_WORD_INDEX_MissingIndex() {
    CoreLabel label = new CoreLabel();
    label.setWord("missingIndex");
    String output = label.toString(CoreLabel.OutputFormat.WORD_INDEX);
    assertEquals("missingIndex", output); 
  }
@Test
  public void testToString_VALUE_INDEX_MAP_WithAnnotations() {
    CoreLabel label = new CoreLabel();
    label.setValue("valueX");
    label.setIndex(99);
    label.setNER("PERSON");
    label.setTag("NNP");
    String output = label.toString(CoreLabel.OutputFormat.VALUE_INDEX_MAP);
    assertTrue(output.startsWith("valueX-99"));
    assertTrue(output.contains("NamedEntityTagAnnotation"));
    assertFalse(output.contains("IndexAnnotation"));
    assertFalse(output.contains("ValueAnnotation"));
  }
//@Test(expected = UnsupportedOperationException.class)
//  public void testInitFromStringsTyped_UnknownValueClass() {
//    Class[] keys = new Class[] { GenericAnnotationImpl.class };
//    String[] values = new String[] { "anything" };
//    new CoreLabel(keys, values);
//  }
@Test
  public void testParseEmptyStringKeys() {
    String[] keys = new String[] {};
    Class[] result = CoreLabel.parseStringKeys(keys);
    assertEquals(0, result.length);
  }
@Test
  public void testToString_ALL_WhenEmpty() {
    CoreLabel label = new CoreLabel();
    String result = label.toString(CoreLabel.OutputFormat.ALL);
    assertEquals("", result);
  }
@Test
  public void testIndexAndSentIndexNotSet() {
    CoreLabel label = new CoreLabel();
    assertEquals(-1, label.index());
    assertEquals(-1, label.sentIndex());
  }
@Test
  public void testBeginAndEndPositionNotSet() {
    CoreLabel label = new CoreLabel();
    assertEquals(-1, label.beginPosition());
    assertEquals(-1, label.endPosition());
  }
@Test
  public void testEmptyIndexUnset() {
    CoreLabel label = new CoreLabel();
    assertEquals(0, label.getEmptyIndex());
    assertFalse(label.hasEmptyIndex());
  }
@Test
  public void testGetStringWithDefault() {
    CoreLabel label = new CoreLabel();
    String result = label.getString(CoreAnnotations.TextAnnotation.class, "defaultValue");
    assertEquals("defaultValue", result);
  }
@Test
  public void testFactoryNewLabelFromStringUnsupported() {
    try {
      CoreLabel.factory().newLabelFromString("dummy");
      fail("Expected UnsupportedOperationException not thrown");
    } catch (UnsupportedOperationException e) {
      assertTrue(e.getMessage().contains("we do not understand"));
    }
  }
@Test
  public void testSetBeforeAndAfter() {
    CoreLabel label = new CoreLabel();
    label.setBefore(" ");
    label.setAfter(".");
    assertEquals(" ", label.before());
    assertEquals(".", label.after());
  }
@Test
  public void testToString_WORD_WhenNull() {
    CoreLabel label = new CoreLabel();
    String result = label.toString(CoreLabel.OutputFormat.WORD);
    assertNull(label.word());
    assertNull(result);
  }
@Test
  public void testMultipleAnnotationsOrderingInMAP() {
    CoreLabel label = new CoreLabel();
    label.setNER("LOCATION");
    label.setTag("NN");
    label.setWord("Paris");
    String output = label.toString(CoreLabel.OutputFormat.MAP);
    assertTrue(output.contains("TextAnnotation"));
    assertTrue(output.contains("PartOfSpeechAnnotation"));
    assertTrue(output.contains("NamedEntityTagAnnotation"));
  }
//@Test
//  public void testSetAndGetGenericAnnotationManually() {
//    CoreLabel label = new CoreLabel();
//    label.set(GenericAnnotationImpl.class, "custom");
//    String value = label.get(GenericAnnotationImpl.class);
//    assertEquals("custom", value);
//  }
//@Test
//  public void testConstructorWithLabel_HasWordAndHasTag() {
//    Label label = new Label() {
//      public String word() { return "walk"; }
//      public String tag() { return "VB"; }
//      public String value() { return "v"; }
//    };
//    CoreLabel coreLabel = new CoreLabel(CoreLabel.factory().newLabel(label));
//    assertEquals("walk", coreLabel.word());
//    assertEquals("VB", coreLabel.tag());
//    assertEquals("v", coreLabel.value());
//  }
@Test
  public void testInitFromStrings_ParseBooleanValue() {
    Class[] keys = new Class[] { CoreAnnotations.IsNewlineAnnotation.class };
    String[] values = new String[] { "true" };
    CoreLabel label = new CoreLabel(keys, values);
    assertTrue(label.isNewline());
  }
@Test
  public void testInitFromStrings_ParseIntegerValue() {
    Class[] keys = new Class[] { CoreAnnotations.IndexAnnotation.class };
    String[] values = new String[] { "10" };
    CoreLabel label = new CoreLabel(keys, values);
    assertEquals(10, label.index());
  }
//@Test
//  public void testInitFromStrings_ParseDoubleValue() {
//    Class[] keys = new Class[] { ConfidenceAnnotation.class };
//    String[] values = new String[] { "0.85" };
//    CoreLabel label = new CoreLabel(keys, values);
//    Double parsed = label.get(ConfidenceAnnotation.class);
//    assertEquals(0.85, parsed, 0.0001);
//  }
//@Test
//  public void testInitFromStrings_ParseLongValue() {
//    Class[] keys = new Class[] { BeginPositionAnnotation.class };
//    String[] values = new String[] { "100" };
//    CoreLabel label = new CoreLabel(keys, values);
//    int begin = label.beginPosition();
//    assertEquals(100, begin);
//  }
@Test
  public void testInitFromStrings_ParseCoNLLUFeatures() {
    Class[] keys = new Class[] { CoreAnnotations.CoNLLUFeats.class };
    String[] values = new String[] { "Case=Nom|Number=Sing" };
    CoreLabel label = new CoreLabel(keys, values);
    CoNLLUFeatures feats = label.get(CoreAnnotations.CoNLLUFeats.class);
    assertNotNull(feats);
    assertTrue(feats.toString().contains("Case=Nom"));
  }
@Test(expected = UnsupportedOperationException.class)
  public void testInitFromStrings_UnsupportedValueTypeThrows() {
    Class[] keys = new Class[] { CoreLabel.OutputFormat.class };
    String[] values = new String[] { "VALUE" };
    new CoreLabel(keys, values);
  }
@Test
  public void testToStringOutputFormat_VALUE_TAG_NER_OnlyNER() {
    CoreLabel label = new CoreLabel();
    label.setValue("matrix");
    label.setNER("OBJECT");
    String result = label.toString(CoreLabel.OutputFormat.VALUE_TAG_NER);
    assertEquals("matrix/OBJECT", result);
  }
@Test
  public void testToStringOutputFormat_VALUE_INDEX_MissingBothIndices() {
    CoreLabel label = new CoreLabel();
    label.setValue("go");
    String result = label.toString(CoreLabel.OutputFormat.VALUE_INDEX);
    assertEquals("go", result);
  }
@Test
  public void testToStringOutputFormat_VALUE_TAG_MissingTag() {
    CoreLabel label = new CoreLabel();
    label.setValue("the");
    String result = label.toString(CoreLabel.OutputFormat.VALUE_TAG);
    assertEquals("the", result);
  }
@Test
  public void testToStringOutputFormat_WORD_INDEX_MissingEmptyIndex() {
    CoreLabel label = new CoreLabel();
    label.setWord("connect");
    label.setIndex(5);
    String output = label.toString(CoreLabel.OutputFormat.WORD_INDEX);
    assertEquals("connect-5", output);
  }
@Test
  public void testToStringOutputFormat_VALUE_INDEX_MAP_EmptyAnnotations() {
    CoreLabel label = new CoreLabel();
    label.setValue("jump");
    label.setIndex(7);
    String result = label.toString(CoreLabel.OutputFormat.VALUE_INDEX_MAP);
    assertTrue(result.startsWith("jump-7"));
  }
@Test
  public void testToStringOutputFormat_ALL_WithMultipleAnnotations() {
    CoreLabel label = new CoreLabel();
    label.setWord("sun");
    label.setTag("NN");
    label.setNER("NATURE");
    String output = label.toString(CoreLabel.OutputFormat.ALL);
    assertTrue(output.contains("TextAnnotation"));
    assertTrue(output.contains("NamedEntityTagAnnotation"));
    assertTrue(output.contains("PartOfSpeechAnnotation"));
  }
@Test
  public void testGetStringFallbackToDefault() {
    CoreLabel label = new CoreLabel();
    String actual = label.getString(CoreAnnotations.CategoryAnnotation.class, "defaultCat");
    assertEquals("defaultCat", actual);
  }
@Test
  public void testSetEmptyIndexZeroReturnsZero() {
    CoreLabel label = new CoreLabel();
    label.setEmptyIndex(0);
    int result = label.getEmptyIndex();
    assertEquals(0, result);
    assertFalse(label.hasEmptyIndex());
  }
@Test
  public void testMultipleSettersChainedAndVerified() {
    CoreLabel label = new CoreLabel();
    label.setWord("fish");
    label.setTag("NN");
    label.setNER("ANIMAL");
    label.setValue("fish");
    label.setBeginPosition(2);
    label.setEndPosition(6);
    label.setIndex(101);
    label.setSentIndex(5);
    assertEquals("fish", label.word());
    assertEquals("NN", label.tag());
    assertEquals("ANIMAL", label.ner());
    assertEquals("fish", label.value());
    assertEquals(2, label.beginPosition());
    assertEquals(6, label.endPosition());
    assertEquals(101, label.index());
    assertEquals(5, label.sentIndex());
  }
@Test
  public void testCoreLabelConstructorWithEmptyArrays() {
    String[] keys = new String[0];
    String[] values = new String[0];
    CoreLabel label = new CoreLabel(keys, values);
    assertNotNull(label);
    assertTrue(label.keySet().isEmpty());
  }
@Test
  public void testToString_VALUE_TAG_INDEX_MissingTagAndIndex() {
    CoreLabel label = new CoreLabel();
    label.setValue("token");
    String result = label.toString(CoreLabel.OutputFormat.VALUE_TAG_INDEX);
    assertEquals("token", result);
  }
//@Test
//  public void testToString_ALL_WithNullValues() {
//    CoreLabel label = new CoreLabel();
//    label.set(TagAnnotation.class, null);
//    label.set(ValueAnnotation.class, null);
//    String output = label.toString(CoreLabel.OutputFormat.ALL);
//    assertTrue(output.contains("null"));
//  }
@Test(expected = IllegalArgumentException.class)
  public void testToString_IllegalOutputFormatThrows() {
    CoreLabel label = new CoreLabel();
    label.toString(null);
  }
@Test
  public void testConstructorWithIndexedWord_CopiesAnnotations() {
    CoreLabel base = new CoreLabel();
    base.setWord("idea");
    base.setNER("CONCEPT");
    IndexedWord iw = new IndexedWord(base);
    CoreLabel copied = new CoreLabel(iw);
    assertEquals("idea", copied.word());
    assertEquals("CONCEPT", copied.ner());
  }
@Test
  public void testSetFromStringThrowsExpectedException() {
    CoreLabel label = new CoreLabel();
    try {
      label.setFromString("invalid");
      fail("Should throw UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      assertEquals("Cannot set from string", e.getMessage());
    }
  }
@Test
  public void testUnknownGenericAnnotationThrowsWhenUnavailableKey() {
    String[] keys = new String[] { "unknown.annotation.Name" };
    String[] values = new String[] { "test" };
    try {
      new CoreLabel(keys, values);
      fail("Expected UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      assertTrue(e.getMessage().contains("Unknown key"));
      assertTrue(e.getMessage().contains("unknown.annotation.Name"));
    }
  }
//@Test
//  public void testNullKeyInInitFromStringsSkips() {
//    String[] keys = new String[] { null };
//    String[] values = new String[] { "value" };
//    CoreLabel label = new CoreLabel(keys.length);
//    label.set(ValueAnnotation.class, "preset");
//    try {
//      label = new CoreLabel(keys, values);
//
//    } catch (Exception e) {
//      fail("Should not throw; null key should be skipped");
//    }
//  }
@Test
  public void testEmptyAnnotationStringsDoNotThrow() {
    String[] keys = new String[] { "NER" };
    String[] values = new String[] { "" };
    CoreLabel label = new CoreLabel(keys, values);
    assertEquals("", label.ner());
  }
@Test
  public void testDoubleSetSameKey_OverwritesValue() {
    CoreLabel label = new CoreLabel();
    label.setWord("first");
    label.setWord("second");
    String word = label.get(CoreAnnotations.TextAnnotation.class);
    assertEquals("second", word);
  }
@Test
  public void testToString_VALUE_INDEX_MAP_NoValueAnnotation() {
    CoreLabel label = new CoreLabel();
    label.setIndex(6);
    label.setNER("LOC");
    String result = label.toString(CoreLabel.OutputFormat.VALUE_INDEX_MAP);
    assertTrue(result.startsWith("-6"));
    assertTrue(result.contains("NamedEntityTagAnnotation"));
    assertFalse(result.contains("ValueAnnotation"));
  }
@Test
  public void testConstructorCoreLabelWithEmptyKeySet() {
    CoreLabel original = new CoreLabel();
    CoreLabel copied = new CoreLabel(original);
    assertNotNull(copied);
    assertTrue(copied.keySet().isEmpty());
  }
@Test
  public void testSetNullValueStillStoresKey() {
    CoreLabel label = new CoreLabel();
    label.set(CoreAnnotations.CategoryAnnotation.class, null);
    assertTrue(label.keySet().contains(CoreAnnotations.CategoryAnnotation.class));
  }
@Test
  public void testToStringWithAllStandardFields() {
    CoreLabel label = new CoreLabel();
    label.setWord("xyz");
    label.setTag("DT");
    label.setNER("TYPE");
    label.setIndex(3);
    label.setEmptyIndex(1);
    label.setOriginalText("XYZ");
    label.setValue("xyz");
    label.setBeginPosition(0);
    label.setEndPosition(3);
    label.setSentIndex(2);
    String result = label.toString(CoreLabel.OutputFormat.ALL);
    assertTrue(result.contains("xyz"));
    assertTrue(result.contains("DT"));
    assertTrue(result.contains("TYPE"));
    assertTrue(result.contains("OriginalTextAnnotation"));
    assertTrue(result.contains("BeginPositionAnnotation"));
  }
@Test
  public void testAnnotationOverwriteRestoresTypeCorrectly() {
    CoreLabel label = new CoreLabel();
    label.set(CoreAnnotations.LemmaAnnotation.class, "run");
    label.set(CoreAnnotations.LemmaAnnotation.class, "go");
    assertEquals("go", label.lemma());
  }
@Test
  public void testNERConfidencesMapReturnsNullIfUnset() {
    CoreLabel label = new CoreLabel();
    Map<String, Double> conf = label.nerConfidence();
    assertNull(conf);
  }
@Test
  public void testEqualsAndHashCodeConsistency() {
    CoreLabel label1 = new CoreLabel();
    CoreLabel label2 = new CoreLabel();
    label1.setWord("same");
    label2.setWord("same");
    assertEquals(label1, label2);
    assertEquals(label1.hashCode(), label2.hashCode());
  }
@Test
  public void testToString_WORD_NullWord() {
    CoreLabel label = new CoreLabel();
    String output = label.toString(CoreLabel.OutputFormat.WORD);
    assertNull(label.word());
    assertNull(output);
  }
@Test
  public void testToString_MAP_ShowsFullQualifiedNames() {
    CoreLabel label = new CoreLabel();
    label.setWord("value");
    String output = label.toString(CoreLabel.OutputFormat.MAP);
    assertTrue(output.contains("edu.stanford.nlp.ling.CoreAnnotations$TextAnnotation"));
  }
@Test
  public void testUnknownFormatThrowsGracefully() {
    CoreLabel label = new CoreLabel();
    try {
      label.toString(CoreLabel.OutputFormat.valueOf("UNSUPPORTED_FORMAT"));
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
      
    }
  }
@Test
  public void testOutputFormatDefaultsToValueIndex() {
    CoreLabel label = new CoreLabel();
    label.setValue("x");
    label.setIndex(9);
    String result = label.toString(); 
    assertEquals("x-9", result);
  }
//@Test
//  public void testCoreLabelFactoryCreatesFromHasWordOnly() {
//    Label label = new Label() {
//      public String word() { return "wordOnly"; }
//      public String value() { return "valueOnly"; }
//    };
//    Label result = CoreLabel.factory().newLabel(label);
//    assertTrue(result instanceof CoreLabel);
//    CoreLabel cl = (CoreLabel) result;
//    assertEquals("wordOnly", cl.word());
//    assertEquals("valueOnly", cl.value());
//  }
//@Test
//  public void testCoreLabelFactoryCreatesFromHasOffset() {
//    Label label = new Label() {
//      public int beginPosition() { return 1; }
//      public int endPosition() { return 3; }
//      public String value() { return "offsetWord"; }
//    };
//    Label cl = CoreLabel.factory().newLabel(label);
//    assertTrue(cl instanceof CoreLabel);
//    CoreLabel core = (CoreLabel) cl;
//    assertEquals(1, core.beginPosition());
//    assertEquals(3, core.endPosition());
//    assertEquals("offsetWord", core.value());
//  }
@Test
  public void testEmptyStringsForNERAndTagAndOriginalText() {
    CoreLabel cl = new CoreLabel();
    cl.setNER("");
    cl.setTag("");
    cl.setOriginalText("");
    assertEquals("", cl.ner());
    assertEquals("", cl.tag());
    assertEquals("", cl.originalText());
  }
@Test
  public void testNullWordAndValueLeavesToStringSafe() {
    CoreLabel cl = new CoreLabel();
    String valueIndex = cl.toString(CoreLabel.OutputFormat.VALUE_INDEX);
    assertEquals(null, cl.value());
    assertEquals("null", valueIndex);
  }
@Test
  public void testCoNLLUFeaturesParsedFromConstructor() {
    Class[] keys = new Class[] { CoreAnnotations.CoNLLUFeats.class };
    String[] values = new String[] { "PronType=Int|Reflex=Yes" };
    CoreLabel cl = new CoreLabel(keys, values);
    CoNLLUFeatures feats = cl.get(CoreAnnotations.CoNLLUFeats.class);
    assertNotNull(feats);
    assertTrue(feats.toString().contains("PronType=Int"));
  }
@Test(expected = UnsupportedOperationException.class)
  public void testInitFromStringsIntegerFailsOnNonnumeric() {
    Class[] keys = new Class[] { CoreAnnotations.IndexAnnotation.class };
    String[] values = new String[] { "NaN" };
    new CoreLabel(keys, values);
  }
//@Test(expected = UnsupportedOperationException.class)
//  public void testInitFromStringsDoubleFailsOnNonnumeric() {
//    Class[] keys = new Class[] { ConfidenceAnnotation.class };
//    String[] values = new String[] { "NotADouble" };
//    new CoreLabel(keys, values);
//  }
//@Test(expected = UnsupportedOperationException.class)
//  public void testInitFromStringsUnsupportedClassType() {
//    Class[] keys = new Class[] { OutputFormat.class };
//    String[] values = new String[] { "irrelevant" };
//    new CoreLabel(keys, values);
//  }
@Test
  public void testCoreLabelCopyConstructorIgnoresNullAnnotationValues() {
    CoreLabel src = new CoreLabel();
    src.set(CoreAnnotations.TextAnnotation.class, null);
    CoreLabel copy = new CoreLabel(src);
    assertTrue(copy.keySet().contains(CoreAnnotations.TextAnnotation.class));
    assertNull(copy.word());
  }
@Test
  public void testCoreLabelToStringOutputsDefaultValue() {
    CoreLabel label = new CoreLabel();
    label.setValue("defaultValue");
    String str = label.toString();
    assertEquals("defaultValue", str);
  }
@Test
  public void testHasEmptyIndexReturnsFalseWhenUnset() {
    CoreLabel cl = new CoreLabel();
    assertFalse(cl.hasEmptyIndex());
    assertEquals(0, cl.getEmptyIndex());
  }
@Test
  public void testSetGetBooleanAnnotationExplicitFalse() {
    CoreLabel label = new CoreLabel();
    label.set(CoreAnnotations.IsMultiWordTokenAnnotation.class, false);
    Boolean result = label.get(CoreAnnotations.IsMultiWordTokenAnnotation.class);
    assertNotNull(result);
    assertFalse(result);
  }
@Test
  public void testSetBeforeAndAfterEmptyString() {
    CoreLabel cl = new CoreLabel();
    cl.setBefore("");
    cl.setAfter("");
    assertEquals("", cl.before());
    assertEquals("", cl.after());
  }
@Test
  public void testToString_VALUE_TAG_INDEX_MissingTagPresentIndex() {
    CoreLabel label = new CoreLabel();
    label.setValue("vtag");
    label.setIndex(5);
    String result = label.toString(CoreLabel.OutputFormat.VALUE_TAG_INDEX);
    assertEquals("vtag-5", result);
  }
@Test
  public void testToString_VALUE_TAG_INDEX_MissingEverything() {
    CoreLabel label = new CoreLabel();
    String result = label.toString(CoreLabel.OutputFormat.VALUE_TAG_INDEX);
    assertEquals("null", result);
  }
@Test
  public void testToString_VALUE_INDEX_MAP_OmitsEmptyIndexZero() {
    CoreLabel label = new CoreLabel();
    label.setValue("val");
    label.setIndex(3);
    label.setEmptyIndex(0);
    label.setNER("X");
    String output = label.toString(CoreLabel.OutputFormat.VALUE_INDEX_MAP);
    assertTrue(output.startsWith("val-3"));
    assertTrue(output.contains("NamedEntityTagAnnotation"));
    assertFalse(output.contains("EmptyIndexAnnotation"));
  }
@Test
  public void testToString_LEMMA_INDEX_WithoutLemma() {
    CoreLabel label = new CoreLabel();
    label.setIndex(7);
    label.setEmptyIndex(2);
    String result = label.toString(CoreLabel.OutputFormat.LEMMA_INDEX);
    assertEquals("null-7.2", result);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testCoreLabelStringConstructorThrowsOnLengthMismatch() {
    String[] keys = new String[] { "Text", "NER" };
    String[] vals = new String[] { "A" };
    new CoreLabel(keys, vals);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testCoreLabelConstructorWithBrokenKeyName() {
    String[] keys = new String[] { "edu.stanford.UnknownAnnotation" };
    String[] vals = new String[] { "value" };
    new CoreLabel(keys, vals);
  }
@Test
  public void testParseStringKeysFailsOnNullKey() {
    try {
      CoreLabel.parseStringKeys(new String[] { null });
      fail("Expected exception");
    } catch (UnsupportedOperationException e) {
      assertTrue(e.getMessage().contains("Unknown key"));
    }
  }
//@Test
//  public void testFactoryNewLabelFromLegacyInterfaces() {
//    Label legacy = new Label() {
//      public String word() { return "glue"; }
//      public String tag() { return "VBZ"; }
//      public int beginPosition() { return 1; }
//      public int endPosition() { return 5; }
//      public int index() { return 42; }
//      public String category() { return "X" ; }
//      public String value() { return "G"; }
//    };
//    Label output = CoreLabel.factory().newLabel(legacy);
//    assertTrue(output instanceof CoreLabel);
//    CoreLabel cl = (CoreLabel) output;
//    assertEquals("glue", cl.word());
//    assertEquals("VBZ", cl.tag());
//    assertEquals("X", cl.category());
//    assertEquals(42, cl.index());
//    assertEquals(1, cl.beginPosition());
//    assertEquals(5, cl.endPosition());
//    assertEquals("G", cl.value());
//  }
//@Test
//  public void testSetNullAnnotationDoesNotCrash() {
//    CoreLabel label = new CoreLabel();
//    label.set(TagAnnotation.class, null);
//    String tag = label.tag();
//    assertNull(tag);
//  }
@Test
  public void testRemoveAnnotationBySettingNullValue() {
    CoreLabel label = new CoreLabel();
    label.set(CoreAnnotations.OriginalTextAnnotation.class, "original");
    label.set(CoreAnnotations.OriginalTextAnnotation.class, null);
    assertNull(label.originalText());
  }
@Test
  public void testToString_ALL_WithMixedSetState() {
    CoreLabel label = new CoreLabel();
    label.setWord("sun");
    label.setIndex(2);
    label.setNER("LOC");
    label.setOriginalText("Sun");
    String str = label.toString(CoreLabel.OutputFormat.ALL);
    assertTrue(str.contains("TextAnnotation"));
    assertTrue(str.contains("IndexAnnotation"));
    assertTrue(str.contains("NamedEntityTagAnnotation"));
    assertTrue(str.contains("OriginalTextAnnotation"));
  }
@Test
  public void testCoreLabelWithCoNLLUFeaturesStoresAnnotationObjectCorrectly() {
    CoreLabel label = new CoreLabel();
    CoNLLUFeatures feats = new CoNLLUFeatures("Gender=Masc|Number=Sing");
    label.set(CoreAnnotations.CoNLLUFeats.class, feats);
    CoNLLUFeatures extracted = label.get(CoreAnnotations.CoNLLUFeats.class);
    assertNotNull(extracted);
    assertEquals("Gender=Masc|Number=Sing", extracted.toString());
  }
//@Test
//  public void testGenericAnnotationSetDirectlyWithUnknownType() {
//    CoreLabel label = new CoreLabel();
//    label.set(GenericTextAnnotation.class, "example");
//    String value = label.get(GenericTextAnnotation.class);
//    assertEquals("example", value);
//  }
@Test
  public void testToString_VALUE_TAG_NER_PartialFilled() {
    CoreLabel label = new CoreLabel();
    label.setValue("bike");
    label.setNER("VEHICLE");
    String output = label.toString(CoreLabel.OutputFormat.VALUE_TAG_NER);
    assertEquals("bike/VEHICLE", output);
  }
@Test
  public void testToString_WORD_INDEX_HandlesEmptyIndex() {
    CoreLabel label = new CoreLabel();
    label.setWord("Dusk");
    label.setIndex(1);
    label.setEmptyIndex(0);
    String result = label.toString(CoreLabel.OutputFormat.WORD_INDEX);
    assertEquals("Dusk-1", result);
  }
@Test
  public void testToString_VALUE_MAP_WithOnlyValueAnnotation() {
    CoreLabel label = new CoreLabel();
    label.setValue("onlyValue");
    String result = label.toString(CoreLabel.OutputFormat.VALUE_MAP);
    assertEquals("onlyValue{}", result);
  }
@Test
  public void testToString_MAP_WithMultipleAnnotationsAlphabeticalOrder() {
    CoreLabel label = new CoreLabel();
    label.setWord("zebra");
    label.setTag("NN");
    label.setNER("ANIMAL");
    String output = label.toString(CoreLabel.OutputFormat.MAP);
    assertTrue(output.contains("TextAnnotation"));
    assertTrue(output.contains("PartOfSpeechAnnotation"));
    assertTrue(output.contains("NamedEntityTagAnnotation"));
  }
@Test(expected = UnsupportedOperationException.class)
  public void testSetFromStringUnsupportedExplicitThrow() {
    CoreLabel label = new CoreLabel();
    label.setFromString("some string");
  }
@Test
  public void testEqualsAndHashCode_AnnotationChangeCausesInequality() {
    CoreLabel label1 = new CoreLabel();
    CoreLabel label2 = new CoreLabel();
    label1.setWord("test");
    label1.setNER("LOC");
    label2.setWord("test");
    label2.setNER("ORG");
    assertNotEquals(label1, label2);
    assertNotEquals(label1.hashCode(), label2.hashCode());
  }
@Test
  public void testRemoveAnnotationBySettingToNull() {
    CoreLabel label = new CoreLabel();
    label.setNER("PERSON");
    assertEquals("PERSON", label.get(CoreAnnotations.NamedEntityTagAnnotation.class));
    label.set(CoreAnnotations.NamedEntityTagAnnotation.class, null);
    assertNull(label.get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }
@Test
  public void testValueAndWordAreIndependent() {
    CoreLabel label = new CoreLabel();
    label.setValue("valueOnly");
    label.setWord("wordOnly");
    assertEquals("valueOnly", label.value());
    assertEquals("wordOnly", label.word());
  }
@Test(expected = IllegalArgumentException.class)
  public void testToString_IllegalOutputFormatDefinedOutsideEnum() {
    CoreLabel label = new CoreLabel();
    label.toString(CoreLabel.OutputFormat.valueOf("UNKNOWN_FORMAT"));
  }
//@Test
//  public void testSetAndGetCustomAnnotation() {
//    CoreLabel label = new CoreLabel();
//    label.set(CoreLabelRemainingCoverageTest.MyStringAnnotation.class, "custom");
//    String result = label.get(CoreLabelRemainingCoverageTest.MyStringAnnotation.class);
//    assertEquals("custom", result);
//  }
@Test
  public void testToString_VALUE_INDEX_NoIndexOrEmptyIndex() {
    CoreLabel label = new CoreLabel();
    label.setValue("alpha");
    String result = label.toString(CoreLabel.OutputFormat.VALUE_INDEX);
    assertEquals("alpha", result);
  }
@Test
  public void testToString_WORD_INDEX_WithEmptyIndexSetToZero() {
    CoreLabel label = new CoreLabel();
    label.setWord("bravo");
    label.setIndex(2);
    label.setEmptyIndex(0);
    String result = label.toString(CoreLabel.OutputFormat.WORD_INDEX);
    assertEquals("bravo-2", result);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testParseStringKeys_WithInvalidKeyThrows() {
    String[] keys = new String[] { "invalid_key_#$%" };
    CoreLabel.parseStringKeys(keys);
  }
//@Test
//  public void testFactoryNewLabelWithNullValue() {
//    CoreLabel.LabelFactory factory = CoreLabel.factory();
//    Label l = factory.newLabel(null);
//    assertNotNull(l);
//    assertTrue(l instanceof CoreLabel);
//    assertNull(((CoreLabel) l).value());
//  }
@Test
  public void testCoNLLUFeaturesParseStoredAndRetrieved() {
    CoreLabel label = new CoreLabel();
    CoNLLUFeatures feats = new CoNLLUFeatures("Gender=Fem|Case=Nom");
    label.set(CoreAnnotations.CoNLLUFeats.class, feats);
    CoNLLUFeatures retrieved = label.get(CoreAnnotations.CoNLLUFeats.class);
    assertEquals("Gender=Fem|Case=Nom", retrieved.toString());
  }
@Test
  public void testEmptyLabelToStringFormats() {
    CoreLabel label = new CoreLabel();
    String value = label.toString(CoreLabel.OutputFormat.VALUE);
    String word = label.toString(CoreLabel.OutputFormat.WORD);
    String valueTag = label.toString(CoreLabel.OutputFormat.VALUE_TAG);
    String valueTagIndex = label.toString(CoreLabel.OutputFormat.VALUE_TAG_INDEX);
    assertEquals("null", value);
    assertNull(word);
    assertEquals("null", valueTag);
    assertEquals("null", valueTagIndex);
  } 
}