public class CoreLabel_wosr_2_GPTLLMTest { 

 @Test
  public void testDefaultConstructorValueIsNull() {
    CoreLabel label = new CoreLabel();
    assertNull(label.value());
    assertNull(label.word());
    assertNull(label.get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testConstructorWithCapacity() {
    CoreLabel label = new CoreLabel(5);
    label.setWord("cat");
    assertEquals("cat", label.word());
  }
@Test
  public void testConstructorFromCoreLabel() {
    CoreLabel src = new CoreLabel();
    src.setWord("dog");
    src.setTag("NN");
    src.setLemma("dog");
    CoreLabel copy = new CoreLabel(src);
    assertEquals("dog", copy.word());
    assertEquals("NN", copy.tag());
    assertEquals("dog", copy.lemma());
  }
@Test
  public void testConstructorFromLabel_HasWordInterface() {
    Label label = new Word("run");
    CoreLabel cl = new CoreLabel(label);
    assertEquals("run", cl.word());
    assertEquals("run", cl.value());
  }
@Test
  public void testConstructorFromLabel_NonCoreMap() {
    Label label = new WordTag("run", "VB");
    CoreLabel cl = new CoreLabel(label);
    assertEquals("run", cl.word());
    assertEquals("VB", cl.tag());
    assertEquals("run", cl.value());
  }
@Test
  public void testSetAndGetNER() {
    CoreLabel label = new CoreLabel();
    label.setNER("PERSON");
    assertEquals("PERSON", label.ner());
  }
@Test
  public void testSetAndGetLemma() {
    CoreLabel label = new CoreLabel();
    label.setLemma("eat");
    assertEquals("eat", label.lemma());
  }
@Test
  public void testIndexAndEmptyIndex() {
    CoreLabel label = new CoreLabel();
    label.setIndex(5);
    label.setEmptyIndex(2);
    assertEquals(5, label.index());
    assertEquals(2, label.getEmptyIndex());
    assertTrue(label.hasEmptyIndex());
  }
@Test
  public void testBeginAndEndPosition() {
    CoreLabel label = new CoreLabel();
    label.setBeginPosition(10);
    label.setEndPosition(15);
    assertEquals(10, label.beginPosition());
    assertEquals(15, label.endPosition());
  }
@Test
  public void testToString_VALUE_INDEX() {
    CoreLabel label = new CoreLabel();
    label.setValue("John");
    label.setIndex(3);
    assertEquals("John-3", label.toString(CoreLabel.OutputFormat.VALUE_INDEX));
  }
@Test
  public void testToString_VALUE_TAG_INDEX() {
    CoreLabel label = new CoreLabel();
    label.setValue("run");
    label.setTag("VB");
    label.setIndex(7);
    label.setEmptyIndex(1);
    assertEquals("run/VB-7.1", label.toString(CoreLabel.OutputFormat.VALUE_TAG_INDEX));
  }
@Test
  public void testToString_WORD_INDEX() {
    CoreLabel label = new CoreLabel();
    label.setWord("house");
    label.setIndex(1);
    assertEquals("house-1", label.toString(CoreLabel.OutputFormat.WORD_INDEX));
  }
@Test
  public void testSetIsNewLineAndRetrieve() {
    CoreLabel label = new CoreLabel();
    label.setIsNewline(true);
    assertTrue(label.isNewline());
  }
@Test
  public void testSetIsMWTAndFirst() {
    CoreLabel label = new CoreLabel();
    label.setIsMWT(true);
    label.setIsMWTFirst(true);
    assertTrue(label.isMWT());
    assertTrue(label.isMWTFirst());
  }
@Test
  public void testSetCategoryAndGetCategory() {
    CoreLabel label = new CoreLabel();
    label.setCategory("NP");
    assertEquals("NP", label.category());
  }
@Test
  public void testSetGetBeforeAfterAnnotations() {
    CoreLabel label = new CoreLabel();
    label.setBefore(" ");
    label.setAfter(".");
    assertEquals(" ", label.before());
    assertEquals(".", label.after());
  }
@Test
  public void testSetOriginalTextAndGet() {
    CoreLabel label = new CoreLabel();
    label.setOriginalText("jumped");
    assertEquals("jumped", label.originalText());
  }
@Test
  public void testSetAndGetDocID() {
    CoreLabel label = new CoreLabel();
    label.setDocID("doc123");
    assertEquals("doc123", label.docID());
  }
@Test
  public void testWordFromStringStaticMethod() {
    CoreLabel label = CoreLabel.wordFromString("hello");
    assertEquals("hello", label.word());
    assertEquals("hello", label.originalText());
    assertEquals("hello", label.value());
  }
@Test
  public void testInitFromStringKeysValid() {
    String[] keys = {"TextAnnotation", "PartOfSpeechAnnotation"};
    String[] values = {"horse", "NN"};
    CoreLabel label = new CoreLabel(keys, values);
    assertEquals("horse", label.word());
    assertEquals("NN", label.tag());
  }
@Test(expected = UnsupportedOperationException.class)
  public void testInitFromStringKeysInvalidLengthMismatch() {
    String[] keys = {"TextAnnotation"};
    String[] values = {"word", "extra"};
    new CoreLabel(keys, values);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testInitFromStringKeysInvalidType() {
    String[] keys = {"IndexAnnotation"};
    String[] values = {"notAnInt"};
    new CoreLabel(keys, values);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testSetFromStringThrowsException() {
    CoreLabel label = new CoreLabel();
    label.setFromString("dummy");
  }
@Test
  public void testToString_CompleteMapOutput() {
    CoreLabel label = new CoreLabel();
    label.setValue("cat");
    label.setWord("cat");
    label.setTag("NN");
    String str = label.toString(CoreLabel.OutputFormat.MAP);
    assertTrue(str.contains("Word"));
    assertTrue(str.contains("PartOfSpeech"));
  }
@Test
  public void testToString_ValueMap() {
    CoreLabel label = new CoreLabel();
    label.setValue("apple");
    label.setTag("NN");
    String str = label.toString(CoreLabel.OutputFormat.VALUE_MAP);
    assertTrue(str.startsWith("apple"));
    assertTrue(str.contains("PartOfSpeech"));
  }
@Test
  public void testToString_ValueTag() {
    CoreLabel label = new CoreLabel();
    label.setValue("bird");
    label.setTag("NN");
    assertEquals("bird/NN", label.toString(CoreLabel.OutputFormat.VALUE_TAG));
  }
@Test
  public void testToString_ValueTagNER() {
    CoreLabel label = new CoreLabel();
    label.setValue("John");
    label.setTag("NNP");
    label.setNER("PERSON");
    assertEquals("John/NNP/PERSON", label.toString(CoreLabel.OutputFormat.VALUE_TAG_NER));
  }
@Test
  public void testToString_LEMMA_INDEX() {
    CoreLabel label = new CoreLabel();
    label.setLemma("be");
    label.setIndex(1);
    label.setEmptyIndex(2);
    assertEquals("be-1.2", label.toString(CoreLabel.OutputFormat.LEMMA_INDEX));
  }
@Test
  public void testToString_ALL() {
    CoreLabel label = new CoreLabel();
    label.setWord("fish");
    label.setTag("NN");
    label.setIndex(1);
    String str = label.toString(CoreLabel.OutputFormat.ALL);
    assertTrue(str.contains("TextAnnotation"));
    assertTrue(str.contains("PartOfSpeechAnnotation"));
    assertTrue(str.contains("IndexAnnotation"));
  }
@Test
  public void testNERConfidenceMapStorage() {
    CoreLabel label = new CoreLabel();
    Map<String, Double> map = new HashMap<>();
    map.put("PERSON", 0.9);
    label.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, map);
    assertEquals((Double)0.9, label.nerConfidence().get("PERSON"));
  }
@Test
  public void testLabelFactoryNewLabelFromCoreLabel() {
    CoreLabel original = new CoreLabel();
    original.setValue("lion");
    LabelFactory factory = CoreLabel.factory();
    Label clonedLabel = factory.newLabel(original);
    assertTrue(clonedLabel instanceof CoreLabel);
    assertEquals("lion", clonedLabel.value());
  }
@Test
  public void testSentIndexSetting() {
    CoreLabel label = new CoreLabel();
    label.setSentIndex(4);
    assertEquals(4, label.sentIndex());
  }
@Test(expected = UnsupportedOperationException.class)
  public void testInvalidKeyInCoreLabelStringConstructor() {
    String[] keys = {"NonExistentAnnotationKey"};
    String[] values = {"value"};
    new CoreLabel(keys, values);
  } 
}