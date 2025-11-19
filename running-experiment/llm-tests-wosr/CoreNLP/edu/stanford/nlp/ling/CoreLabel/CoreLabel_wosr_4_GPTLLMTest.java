public class CoreLabel_wosr_4_GPTLLMTest { 

 @Test
  public void testDefaultConstructor() {
    CoreLabel label = new CoreLabel();
    assertNull(label.word());
    assertNull(label.value());
  }
@Test
  public void testConstructorWithCapacity() {
    CoreLabel label = new CoreLabel(10);
    assertNotNull(label);
    label.setWord("apple");
    assertEquals("apple", label.word());
  }
@Test
  public void testWordFromString() {
    CoreLabel label = CoreLabel.wordFromString("banana");
    assertEquals("banana", label.word());
    assertEquals("banana", label.originalText());
    assertEquals("banana", label.value());
  }
@Test
  public void testSetAndGetLinguisticAnnotations() {
    CoreLabel label = new CoreLabel();
    label.setWord("run");
    label.setLemma("run");
    label.setTag("VB");
    label.setNER("O");
    label.setCategory("verb");
    label.setOriginalText("run");
    label.setBefore(" ");
    label.setAfter(".");
    label.setDocID("doc-1");

    assertEquals("run", label.word());
    assertEquals("run", label.lemma());
    assertEquals("VB", label.tag());
    assertEquals("O", label.ner());
    assertEquals("verb", label.category());
    assertEquals("run", label.originalText());
    assertEquals(" ", label.before());
    assertEquals(".", label.after());
    assertEquals("doc-1", label.docID());
  }
@Test
  public void testGetStringWithDefault() {
    CoreLabel label = new CoreLabel();
    String result = label.getString(CoreAnnotations.TextAnnotation.class, "default");
    assertEquals("default", result);
  }
@Test
  public void testCopyConstructorFromCoreLabel() {
    CoreLabel label1 = CoreLabel.wordFromString("jump");
    label1.setTag("VB");
    label1.setIndex(2);

    CoreLabel label2 = new CoreLabel(label1);
    assertEquals(label1.word(), label2.word());
    assertEquals(label1.tag(), label2.tag());
    assertEquals(label1.index(), label2.index());
  }
@Test
  public void testConstructorFromLabelInterface() {
    CoreLabel original = CoreLabel.wordFromString("eat");
    original.setTag("VB");
    Label copy = new CoreLabel(original);
    assertEquals("eat", copy.value());
    assertEquals("VB", ((CoreLabel) copy).tag());
  }
@Test
  public void testConstructorFromStringKeysAndValues() {
    String[] keys = {"Text", "PartOfSpeech", "Index"};
    String[] values = {"cat", "NN", "4"};
    CoreLabel label = new CoreLabel(keys, values);

    assertEquals("cat", label.word());
    assertEquals("NN", label.tag());
    assertEquals(4, label.index());
  }
@Test(expected = UnsupportedOperationException.class)
  public void testInitFromStringsWithMismatchedLengths() {
    String[] keys = {"Text", "Lemma"};
    String[] values = {"dog"};
    new CoreLabel(keys, values);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testInitFromStringsWithInvalidKey() {
    String[] keys = {"NonExistentKey"};
    String[] values = {"value"};
    new CoreLabel(keys, values);
  }
@Test
  public void testConstructorFromClassArrayAndValues() {
    Class[] keys = {
      CoreAnnotations.TextAnnotation.class,
      CoreAnnotations.PartOfSpeechAnnotation.class,
      CoreAnnotations.IndexAnnotation.class
    };
    String[] values = {"mouse", "NN", "6"};
    CoreLabel label = new CoreLabel(keys, values);

    assertEquals("mouse", label.word());
    assertEquals("NN", label.tag());
    assertEquals(6, label.index());
  }
@Test
  public void testIsNewlineAndMWTFlags() {
    CoreLabel label = CoreLabel.wordFromString("newline");
    label.setIsNewline(true);
    label.setIsMWT(true);
    label.setIsMWTFirst(true);

    assertTrue(label.isNewline());
    assertTrue(label.isMWT());
    assertTrue(label.isMWTFirst());
  }
@Test
  public void testIndexAndPositionMethods() {
    CoreLabel label = new CoreLabel();
    label.setIndex(10);
    label.setSentIndex(2);
    label.setBeginPosition(0);
    label.setEndPosition(4);

    assertEquals(10, label.index());
    assertEquals(2, label.sentIndex());
    assertEquals(0, label.beginPosition());
    assertEquals(4, label.endPosition());
  }
@Test
  public void testEmptyIndexHandling() {
    CoreLabel label = new CoreLabel();
    assertEquals(0, label.getEmptyIndex());
    assertFalse(label.hasEmptyIndex());

    label.setEmptyIndex(7);
    assertEquals(7, label.getEmptyIndex());
    assertTrue(label.hasEmptyIndex());
  }
@Test
  public void testToStringFormatsValueIndex() {
    CoreLabel label = CoreLabel.wordFromString("house");
    label.setIndex(3);
    String output = label.toString(CoreLabel.OutputFormat.VALUE_INDEX);
    assertEquals("house-3", output);
  }
@Test
  public void testToStringFormatValueTag() {
    CoreLabel label = CoreLabel.wordFromString("plays");
    label.setTag("VBZ");
    String output = label.toString(CoreLabel.OutputFormat.VALUE_TAG);
    assertEquals("plays/VBZ", output);
  }
@Test
  public void testToStringFormatValueTagNER() {
    CoreLabel label = CoreLabel.wordFromString("Paris");
    label.setTag("NNP");
    label.setNER("LOCATION");
    String output = label.toString(CoreLabel.OutputFormat.VALUE_TAG_NER);
    assertEquals("Paris/NNP/LOCATION", output);
  }
@Test
  public void testToStringFormatWordIndex() {
    CoreLabel label = new CoreLabel();
    label.setWord("sun");
    label.setIndex(5);
    String output = label.toString(CoreLabel.OutputFormat.WORD_INDEX);
    assertEquals("sun-5", output);
  }
@Test
  public void testToStringFormatLemmaIndex() {
    CoreLabel label = new CoreLabel();
    label.setLemma("run");
    label.setIndex(9);
    String output = label.toString(CoreLabel.OutputFormat.LEMMA_INDEX);
    assertEquals("run-9", output);
  }
@Test
  public void testToStringValueMapIncludesOnlyExpectedKeys() {
    CoreLabel label = new CoreLabel();
    label.setWord("water");
    label.setTag("NN");
    String output = label.toString(CoreLabel.OutputFormat.VALUE_MAP);
    assertTrue(output.contains("water"));
    assertTrue(output.contains("PartOfSpeechAnnotation"));
    assertFalse(output.contains("ValueAnnotation")); 
  }
@Test(expected = UnsupportedOperationException.class)
  public void testSetFromStringUnsupported() {
    CoreLabel label = new CoreLabel();
    label.setFromString("foo");
  }
@Test
  public void testNerConfidenceDefaultIsNull() {
    CoreLabel label = new CoreLabel();
    Map<String, Double> confidence = label.nerConfidence();
    assertNull(confidence);
  }
@Test
  public void testLabelFactoryNewLabel() {
    LabelFactory factory = CoreLabel.factory();
    Label label = factory.newLabel("hello");
    assertTrue(label instanceof CoreLabel);
    assertEquals("hello", label.value());
  }
@Test(expected = UnsupportedOperationException.class)
  public void testNewLabelFromStringThrows() {
    LabelFactory factory = CoreLabel.factory();
    factory.newLabelFromString("encoded");
  }
@Test
  public void testSetGetCategoryAnnotation() {
    CoreLabel label = new CoreLabel();
    label.setCategory("NOUN");
    assertEquals("NOUN", label.category());
  }
@Test
  public void testHasEmptyIndexTrue() {
    CoreLabel label = new CoreLabel();
    label.setEmptyIndex(99);
    assertTrue(label.hasEmptyIndex());
    assertEquals(99, label.getEmptyIndex());
  }
@Test
  public void testToStringDefaultFormat() {
    CoreLabel label = new CoreLabel();
    label.setValue("val");
    label.setIndex(1);
    assertEquals("val-1", label.toString());
  }
@Test(expected = IllegalArgumentException.class)
  public void testToStringWithInvalidFormat() {
    CoreLabel label = new CoreLabel();
    label.toString(null);
  } 
}