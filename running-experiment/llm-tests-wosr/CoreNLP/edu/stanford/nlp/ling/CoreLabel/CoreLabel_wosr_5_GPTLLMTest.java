public class CoreLabel_wosr_5_GPTLLMTest { 

 @Test
  public void testDefaultConstructorHasNoAnnotations() {
    CoreLabel label = new CoreLabel();
    assertNull(label.word());
    assertNull(label.value());
    assertNull(label.tag());
    assertEquals(-1, label.index());
    assertEquals(-1, label.sentIndex());
    assertEquals(-1, label.beginPosition());
    assertEquals(-1, label.endPosition());
  }
@Test
  public void testConstructorWithCapacity() {
    CoreLabel label = new CoreLabel(10);
    label.setWord("test");
    label.setValue("test");
    assertEquals("test", label.word());
    assertEquals("test", label.value());
  }
@Test
  public void testWordSetAndGet() {
    CoreLabel label = new CoreLabel();
    label.setWord("banana");
    assertEquals("banana", label.word());
  }
@Test
  public void testValueSetAndGet() {
    CoreLabel label = new CoreLabel();
    label.setValue("fruit");
    assertEquals("fruit", label.value());
  }
@Test
  public void testTagSetAndGet() {
    CoreLabel label = new CoreLabel();
    label.setTag("NN");
    assertEquals("NN", label.tag());
  }
@Test
  public void testLemmaSetAndGet() {
    CoreLabel label = new CoreLabel();
    label.setLemma("run");
    assertEquals("run", label.lemma());
  }
@Test
  public void testNERSetAndGet() {
    CoreLabel label = new CoreLabel();
    label.setNER("PERSON");
    assertEquals("PERSON", label.ner());
  }
@Test
  public void testCategorySetAndGet() {
    CoreLabel label = new CoreLabel();
    label.setCategory("NP");
    assertEquals("NP", label.category());
  }
@Test
  public void testBeforeAndAfter() {
    CoreLabel label = new CoreLabel();
    label.setBefore(" ");
    label.setAfter(".");
    assertEquals(" ", label.before());
    assertEquals(".", label.after());
  }
@Test
  public void testOriginalTextAnnotation() {
    CoreLabel label = new CoreLabel();
    label.setOriginalText("original");
    assertEquals("original", label.originalText());
  }
@Test
  public void testDocIDAnnotation() {
    CoreLabel label = new CoreLabel();
    label.setDocID("doc123");
    assertEquals("doc123", label.docID());
  }
@Test
  public void testIndexAndSentIndex() {
    CoreLabel label = new CoreLabel();
    label.setIndex(5);
    label.setSentIndex(2);
    assertEquals(5, label.index());
    assertEquals(2, label.sentIndex());
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
  public void testEmptyIndex() {
    CoreLabel label = new CoreLabel();
    label.setEmptyIndex(3);
    assertTrue(label.hasEmptyIndex());
    assertEquals(3, label.getEmptyIndex());
  }
@Test
  public void testIsNewlineAndMWTAnnotations() {
    CoreLabel label = new CoreLabel();
    label.setIsNewline(true);
    label.setIsMWT(true);
    label.setIsMWTFirst(true);
    assertTrue(label.isNewline());
    assertTrue(label.isMWT());
    assertTrue(label.isMWTFirst());
  }
@Test
  public void testToStringVALUE_INDEX() {
    CoreLabel label = new CoreLabel();
    label.setValue("token");
    label.setIndex(4);
    String result = label.toString(CoreLabel.OutputFormat.VALUE_INDEX);
    assertEquals("token-4", result);
  }
@Test
  public void testToStringVALUE_TAG_INDEX() {
    CoreLabel label = new CoreLabel();
    label.setValue("token");
    label.setTag("NN");
    label.setIndex(2);
    String result = label.toString(CoreLabel.OutputFormat.VALUE_TAG_INDEX);
    assertEquals("token/NN-2", result);
  }
@Test
  public void testToStringVALUE_TAG_NER() {
    CoreLabel label = new CoreLabel();
    label.setValue("John");
    label.setTag("NNP");
    label.setNER("PERSON");
    String result = label.toString(CoreLabel.OutputFormat.VALUE_TAG_NER);
    assertEquals("John/NNP/PERSON", result);
  }
@Test
  public void testToStringMAPFormat() {
    CoreLabel label = new CoreLabel();
    label.setValue("yes");
    label.setTag("RB");
    Map<String, Object> expectedMap = new TreeMap<>();
    expectedMap.put(CoreAnnotations.PartOfSpeechAnnotation.class.getName(), "RB");
    expectedMap.put(CoreAnnotations.ValueAnnotation.class.getName(), "yes");
    String stringified = label.toString(CoreLabel.OutputFormat.MAP);
    assertTrue(stringified.contains("RB"));
    assertTrue(stringified.contains("yes"));
  }
@Test
  public void testParseStringKeysValid() {
    String[] keyNames = new String[] {
        "TextAnnotation",
        "ValueAnnotation",
        "PartOfSpeechAnnotation"
    };
    Class[] parsed = CoreLabel.parseStringKeys(keyNames);
    assertEquals(CoreAnnotations.TextAnnotation.class, parsed[0]);
    assertEquals(CoreAnnotations.ValueAnnotation.class, parsed[1]);
    assertEquals(CoreAnnotations.PartOfSpeechAnnotation.class, parsed[2]);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testParseStringKeysThrowsOnUnknownKey() {
    String[] keyNames = new String[] {"InvalidKey"};
    CoreLabel.parseStringKeys(keyNames);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testConstructorWithMisalignedKeysAndValues() {
    String[] keys = {"TextAnnotation", "PartOfSpeechAnnotation"};
    String[] values = {"hello"};
    new CoreLabel(keys, values);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testConstructorWithUnknownKey() {
    String[] keys = {"NonexistentAnnotation"};
    String[] values = {"hello"};
    new CoreLabel(keys, values);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testInitFromStringsWithBadValueFormat() {
    String[] keys = {"IndexAnnotation"};
    String[] values = {"notAnInteger"};
    new CoreLabel(keys, values);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testSetFromStringNotSupported() {
    CoreLabel label = new CoreLabel();
    label.setFromString("anything");
  }
@Test
  public void testWordFromStringSetsFields() {
    CoreLabel label = CoreLabel.wordFromString("apple");
    assertEquals("apple", label.word());
    assertEquals("apple", label.originalText());
    assertEquals("apple", label.value());
  }
@Test
  public void testCopyConstructorFromCoreMapPreservesValues() {
    CoreLabel original = new CoreLabel();
    original.setWord("dog");
    original.setValue("dog");
    CoreMap copied = new CoreLabel(original);
    assertEquals("dog", copied.get(CoreAnnotations.TextAnnotation.class));
    assertEquals("dog", copied.get(CoreAnnotations.ValueAnnotation.class));
  }
@Test
  public void testLabelFactoryCreatesNewCoreLabel() {
    CoreLabel.LabelFactory factory = CoreLabel.factory();
    Label label = factory.newLabel("GO!");
    assertTrue(label instanceof CoreLabel);
    assertEquals("GO!", label.value());
  }
@Test
  public void testLabelFactoryCopyFromCoreLabel() {
    CoreLabel original = new CoreLabel();
    original.setWord("go");
    original.setTag("VB");
    Label label = CoreLabel.factory().newLabel(original);
    assertTrue(label instanceof CoreLabel);
    assertEquals("go", ((CoreLabel) label).word());
    assertEquals("VB", ((CoreLabel) label).tag());
  }
@Test
  public void testLabelFactoryCopyFromOtherLabelInterfaces() {
    Label old = new Label() {
      public String value() { return "word"; }
      public void setValue(String v) { }
    };
    Label label = CoreLabel.factory().newLabel(old);
    assertTrue(label instanceof CoreLabel);
    assertEquals("word", label.value());
  }
@Test(expected = UnsupportedOperationException.class)
  public void testUnsupportedToStringFormatThrows() {
    CoreLabel label = new CoreLabel();
    label.toString(null);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testNewLabelFromStringThrows() {
    CoreLabel.LabelFactory factory = CoreLabel.factory();
    factory.newLabelFromString("some string");
  } 
}