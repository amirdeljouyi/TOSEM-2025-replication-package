public class CoreLabel_wosr_3_GPTLLMTest { 

 @Test
  public void testDefaultConstructorInitializesEmptyCoreLabel() {
    CoreLabel label = new CoreLabel();
    assertNull(label.word());
    assertNull(label.value());
    assertEquals("", label.toString(CoreLabel.OutputFormat.VALUE));
  }
@Test
  public void testConstructorWithCapacityAllocatesCorrectly() {
    CoreLabel label = new CoreLabel(10);
    label.set(CoreAnnotations.TextAnnotation.class, "test");
    assertEquals("test", label.word());
  }
@Test
  public void testWordFromStringSetsTextOriginalTextAndValue() {
    CoreLabel label = CoreLabel.wordFromString("hello");
    assertEquals("hello", label.word());
    assertEquals("hello", label.originalText());
    assertEquals("hello", label.value());
  }
@Test
  public void testSetAndGetWord() {
    CoreLabel label = new CoreLabel();
    label.setWord("Stanford");
    assertEquals("Stanford", label.word());
  }
@Test
  public void testSetAndGetTag() {
    CoreLabel label = new CoreLabel();
    label.setTag("NNP");
    assertEquals("NNP", label.tag());
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
  public void testSetAndGetCategory() {
    CoreLabel label = new CoreLabel();
    label.setCategory("CategoryX");
    assertEquals("CategoryX", label.category());
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
  public void testSetAndGetOriginalText() {
    CoreLabel label = new CoreLabel();
    label.setOriginalText("Stanford");
    assertEquals("Stanford", label.originalText());
  }
@Test
  public void testSetAndGetDocID() {
    CoreLabel label = new CoreLabel();
    label.setDocID("doc123");
    assertEquals("doc123", label.docID());
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
  public void testSetValueAndValueMethod() {
    CoreLabel label = new CoreLabel();
    label.setValue("hello");
    assertEquals("hello", label.value());
  }
@Test
  public void testSetAndGetBeginEndPosition() {
    CoreLabel label = new CoreLabel();
    label.setBeginPosition(2);
    label.setEndPosition(7);
    assertEquals(2, label.beginPosition());
    assertEquals(7, label.endPosition());
  }
@Test
  public void testToStringValueTagIndexFormat() {
    CoreLabel label = new CoreLabel();
    label.setValue("word");
    label.setTag("VB");
    label.setIndex(1);
    String expected = "word/VB-1";
    assertEquals(expected, label.toString(CoreLabel.OutputFormat.VALUE_TAG_INDEX));
  }
@Test
  public void testToStringValueIndexMapFormat() {
    CoreLabel label = new CoreLabel();
    label.setValue("word");
    label.setIndex(1);
    label.setNER("LOCATION");
    String output = label.toString(CoreLabel.OutputFormat.VALUE_INDEX_MAP);
    assertTrue(output.contains("word-1"));
    assertTrue(output.contains("NamedEntityTagAnnotation"));
  }
@Test
  public void testIsNewlineAnnotation() {
    CoreLabel label = new CoreLabel();
    label.setIsNewline(true);
    assertTrue(label.isNewline());
  }
@Test
  public void testIsMWTAndIsMWTFirst() {
    CoreLabel label = new CoreLabel();
    label.setIsMWT(true);
    label.setIsMWTFirst(true);
    assertTrue(label.isMWT());
    assertTrue(label.isMWTFirst());
  }
@Test
  public void testEmptyIndexAndHasEmptyIndex() {
    CoreLabel label = new CoreLabel();
    label.setEmptyIndex(2);
    assertEquals(2, label.getEmptyIndex());
    assertTrue(label.hasEmptyIndex());
  }
@Test
  public void testInitFromStringKeysAndValuesValid() {
    String[] keys = {
      "Text", 
      "PartOfSpeech", 
      "NamedEntityTag", 
      "CharacterOffsetBegin", 
      "CharacterOffsetEnd"
    };
    String[] values = {"Paris", "NNP", "LOCATION", "0", "5"};

    CoreLabel label = new CoreLabel(keys, values);
    assertEquals("Paris", label.word());
    assertEquals("NNP", label.tag());
    assertEquals("LOCATION", label.ner());
    assertEquals(0, label.beginPosition());
    assertEquals(5, label.endPosition());
  }
@Test(expected = UnsupportedOperationException.class)
  public void testInitFromStringKeysValueMismatchThrows() {
    String[] keys = {"Text", "PartOfSpeech"};
    String[] values = {"Paris"};
    new CoreLabel(keys, values);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testInitFromStringKeysInvalidKeyThrows() {
    String[] keys = {"NonexistentAnnotation"};
    String[] values = {"value"};
    new CoreLabel(keys, values);
  }
@Test
  public void testConstructorFromCoreLabelCopiesAll() {
    CoreLabel original = CoreLabel.wordFromString("London");
    original.setTag("NNP");
    original.setNER("LOCATION");
    CoreLabel copy = new CoreLabel(original);
    assertEquals("London", copy.word());
    assertEquals("NNP", copy.tag());
    assertEquals("LOCATION", copy.ner());
  }
@Test
  public void testToStringAllFormatContainsAllKeys() {
    CoreLabel label = new CoreLabel();
    label.setValue("Word");
    label.setWord("Word");
    label.setTag("NN");
    label.setNER("O");
    String result = label.toString(CoreLabel.OutputFormat.ALL);
    assertTrue(result.contains("ValueAnnotation"));
    assertTrue(result.contains("TextAnnotation"));
  }
@Test
  public void testFactoryCreatesCoreLabelWithLabelStr() {
    LabelFactory factory = CoreLabel.factory();
    Label label = factory.newLabel("myValue");
    assertTrue(label instanceof CoreLabel);
    assertEquals("myValue", label.value());
  }
@Test
  public void testFactoryCopiesOldLabelHasWord() {
    CoreLabel label = new CoreLabel();
    label.setWord("Stanford");
    Label copy = CoreLabel.factory().newLabel(label);
    assertEquals("Stanford", copy.value());
  }
@Test(expected = UnsupportedOperationException.class)
  public void testSetFromStringThrows() {
    CoreLabel label = new CoreLabel();
    label.setFromString("not supported");
  }
@Test(expected = UnsupportedOperationException.class)
  public void testFactoryNewLabelFromStringUnsupported() {
    LabelFactory factory = CoreLabel.factory();
    factory.newLabelFromString("unsupported");
  }
@Test(expected = UnsupportedOperationException.class)
  public void testInitFromStringKeysBadValueType() {
    String[] keys = {"CharacterOffsetBegin"};
    String[] values = {"notAnInt"};
    new CoreLabel(keys, values);
  }
@Test
  public void testGetStringWithDefaultReturnsDefaultWhenNull() {
    CoreLabel label = new CoreLabel();
    String result = label.getString(TextAnnotation.class, "default");
    assertEquals("default", result);
  }
@Test
  public void testParseStringKeysValidKeys() {
    String[] keys = {"Text", "Lemma"};
    Class<?>[] parsed = CoreLabel.parseStringKeys(keys);
    assertEquals(2, parsed.length);
    assertEquals(CoreAnnotations.TextAnnotation.class, parsed[0]);
    assertEquals(CoreAnnotations.LemmaAnnotation.class, parsed[1]);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testParseStringKeysThrowsOnInvalidKey() {
    String[] keys = {"InvalidKey"};
    CoreLabel.parseStringKeys(keys);
  } 
}