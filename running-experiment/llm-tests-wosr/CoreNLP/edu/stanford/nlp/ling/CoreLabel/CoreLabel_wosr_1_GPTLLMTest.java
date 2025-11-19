public class CoreLabel_wosr_1_GPTLLMTest { 

 @Test
  public void testDefaultConstructor() {
    CoreLabel label = new CoreLabel();
    assertNotNull(label);
    assertTrue(label.keySet().isEmpty());
  }
@Test
  public void testCapacityConstructor() {
    CoreLabel label = new CoreLabel(10);
    assertNotNull(label);
    assertTrue(label.keySet().isEmpty());
  }
@Test
  public void testSetAndGetWord() {
    CoreLabel label = new CoreLabel();
    label.setWord("hello");
    assertEquals("hello", label.word());
  }
@Test
  public void testSetAndGetValue() {
    CoreLabel label = new CoreLabel();
    label.setValue("testValue");
    assertEquals("testValue", label.value());
  }
@Test
  public void testSetAndGetOriginalText() {
    CoreLabel label = new CoreLabel();
    label.setOriginalText("Original");
    assertEquals("Original", label.originalText());
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
    label.setCategory("CATEGORY");
    assertEquals("CATEGORY", label.category());
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
    label.setDocID("doc123");
    assertEquals("doc123", label.docID());
  }
@Test
  public void testSetAndGetLemma() {
    CoreLabel label = new CoreLabel();
    label.setLemma("run");
    assertEquals("run", label.lemma());
  }
@Test
  public void testSetAndGetIsNewline() {
    CoreLabel label = new CoreLabel();
    label.setIsNewline(true);
    assertTrue(label.isNewline());
  }
@Test
  public void testSetAndGetIsMWT() {
    CoreLabel label = new CoreLabel();
    label.setIsMWT(true);
    assertTrue(label.isMWT());
  }
@Test
  public void testSetAndGetIsMWTFirst() {
    CoreLabel label = new CoreLabel();
    label.setIsMWTFirst(true);
    assertTrue(label.isMWTFirst());
  }
@Test
  public void testSetAndGetIndex() {
    CoreLabel label = new CoreLabel();
    label.setIndex(5);
    assertEquals(5, label.index());
  }
@Test
  public void testSetAndGetSentIndex() {
    CoreLabel label = new CoreLabel();
    label.setSentIndex(3);
    assertEquals(3, label.sentIndex());
  }
@Test
  public void testSetAndGetBeginEndPosition() {
    CoreLabel label = new CoreLabel();
    label.setBeginPosition(10);
    label.setEndPosition(20);
    assertEquals(10, label.beginPosition());
    assertEquals(20, label.endPosition());
  }
@Test
  public void testGetEmptyIndexWhenUnset() {
    CoreLabel label = new CoreLabel();
    assertEquals(0, label.getEmptyIndex());
  }
@Test
  public void testSetAndHasEmptyIndex() {
    CoreLabel label = new CoreLabel();
    label.setEmptyIndex(2);
    assertTrue(label.hasEmptyIndex());
    assertEquals(2, label.getEmptyIndex());
  }
@Test
  public void testToStringValueIndexFormat() {
    CoreLabel label = new CoreLabel();
    label.setValue("abc");
    label.setIndex(1);
    String expected = "abc-1";
    assertEquals(expected, label.toString(CoreLabel.OutputFormat.VALUE_INDEX));
  }
@Test
  public void testToStringWithEmptyIndex() {
    CoreLabel label = new CoreLabel();
    label.setValue("abc");
    label.setIndex(5);
    label.setEmptyIndex(2);
    assertEquals("abc-5.2", label.toString(CoreLabel.OutputFormat.VALUE_INDEX));
  }
@Test
  public void testToStringWithValueTag() {
    CoreLabel label = new CoreLabel();
    label.setValue("dog");
    label.setTag("NN");
    assertEquals("dog/NN", label.toString(CoreLabel.OutputFormat.VALUE_TAG));
  }
@Test
  public void testToStringWithValueTagNER() {
    CoreLabel label = new CoreLabel();
    label.setValue("John");
    label.setTag("NNP");
    label.setNER("PERSON");
    assertEquals("John/NNP/PERSON", label.toString(CoreLabel.OutputFormat.VALUE_TAG_NER));
  }
@Test
  public void testToStringWordIndex() {
    CoreLabel label = new CoreLabel();
    label.setWord("hello");
    label.setIndex(10);
    assertEquals("hello-10", label.toString(CoreLabel.OutputFormat.WORD_INDEX));
  }
@Test
  public void testToStringLemmaIndex() {
    CoreLabel label = new CoreLabel();
    label.setLemma("run");
    label.setIndex(2);
    assertEquals("run-2", label.toString(CoreLabel.OutputFormat.LEMMA_INDEX));
  }
@Test(expected = UnsupportedOperationException.class)
  public void testSetFromStringUnsupported() {
    CoreLabel label = new CoreLabel();
    label.setFromString("value");
  }
@Test(expected = UnsupportedOperationException.class)
  public void testInitFromStringsDifferentLengthsStringKeys() {
    String[] keys = {"TextAnnotation", "ValueAnnotation"};
    String[] values = {"wordOnly"};
    CoreLabel label = new CoreLabel(keys, values);
  }
@Test
  public void testInitFromStringsValidStringKeys() {
    String[] keys = {"TextAnnotation", "ValueAnnotation"};
    String[] values = {"word", "word"};
    CoreLabel label = new CoreLabel(keys, values);
    assertEquals("word", label.word());
    assertEquals("word", label.value());
  }
@Test(expected = UnsupportedOperationException.class)
  public void testInitFromStringsInvalidKey() {
    String[] keys = {"InvalidAnnotation"};
    String[] values = {"value"};
    new CoreLabel(keys, values);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testInvalidAnnotationValueType() {
    Class[] keys = new Class[]{IndexAnnotation.class};
    String[] values = new String[]{"notanumber"};
    new CoreLabel(keys, values);
  }
@Test
  public void testCopyConstructorFromCoreLabel() {
    CoreLabel original = new CoreLabel();
    original.setValue("value");
    original.setWord("word");
    original.setTag("TAG");

    CoreLabel copy = new CoreLabel(original);

    assertEquals("value", copy.value());
    assertEquals("word", copy.word());
    assertEquals("TAG", copy.tag());
  }
@Test
  public void testFactoryCreatesLabelWithValue() {
    LabelFactory factory = CoreLabel.factory();
    Label lbl = factory.newLabel("hello");
    assertTrue(lbl instanceof CoreLabel);
    assertEquals("hello", lbl.value());
  }
@Test
  public void testToStringMapFormat() {
    CoreLabel label = new CoreLabel();
    label.setValue("val");
    label.setTag("NN");
    assertTrue(label.toString(CoreLabel.OutputFormat.MAP).contains("PartOfSpeechAnnotation"));
  }
@Test
  public void testToStringAllFormat() {
    CoreLabel label = new CoreLabel();
    label.setWord("word");
    label.setValue("value");
    label.setNER("ORG");
    String result = label.toString(CoreLabel.OutputFormat.ALL);
    assertTrue(result.contains("TextAnnotation"));
    assertTrue(result.contains("ValueAnnotation"));
    assertTrue(result.contains("NamedEntityTagAnnotation"));
  }
@Test(expected = IllegalArgumentException.class)
  public void testToStringInvalidFormat() {
    CoreLabel label = new CoreLabel();
    label.toString(null);
  } 
}