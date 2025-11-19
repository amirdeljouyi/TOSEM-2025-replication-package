public class PorterStemmer_wosr_1_GPTLLMTest { 

 @Test
  public void testSimplePluralRemoval() {
    PorterStemmer stemmer = new PorterStemmer();
    String result = stemmer.stem("cars");
    assertEquals("car", result);
  }
@Test
  public void testDoubleSPlural() {
    PorterStemmer stemmer = new PorterStemmer();
    String result = stemmer.stem("classes");
    assertEquals("class", result);
  }
@Test
  public void testIESPlural() {
    PorterStemmer stemmer = new PorterStemmer();
    String result = stemmer.stem("ponies");
    assertEquals("poni", result);
  }
@Test
  public void testEDSuffix() {
    PorterStemmer stemmer = new PorterStemmer();
    String result = stemmer.stem("agreed");
    assertEquals("agree", result);
  }
@Test
  public void testINGSuffix() {
    PorterStemmer stemmer = new PorterStemmer();
    String result = stemmer.stem("meeting");
    assertEquals("meet", result);
  }
@Test
  public void testStep1DoublingConsonants() {
    PorterStemmer stemmer = new PorterStemmer();
    String result = stemmer.stem("fizzing");
    assertEquals("fizz", result);
  }
@Test
  public void testStep1ShortWordSuffixRestoredWithE() {
    PorterStemmer stemmer = new PorterStemmer();
    String result = stemmer.stem("hoping");
    assertEquals("hope", result);
  }
@Test
  public void testStep2YtoIConversion() {
    PorterStemmer stemmer = new PorterStemmer();
    String result = stemmer.stem("happy");
    assertEquals("happi", result);
  }
@Test
  public void testStep3ReplaceIZER() {
    PorterStemmer stemmer = new PorterStemmer();
    String result = stemmer.stem("computerize");
    assertEquals("computer", result);
  }
@Test
  public void testStep3ComplexSuffix() {
    PorterStemmer stemmer = new PorterStemmer();
    String result = stemmer.stem("organizational");
    assertEquals("organ", result);
  }
@Test
  public void testStep3ATION() {
    PorterStemmer stemmer = new PorterStemmer();
    String result = stemmer.stem("activation");
    assertEquals("activ", result);
  }
@Test
  public void testStep4ICATE() {
    PorterStemmer stemmer = new PorterStemmer();
    String result = stemmer.stem("predicate");
    assertEquals("predic", result);
  }
@Test
  public void testStep4ALIZE() {
    PorterStemmer stemmer = new PorterStemmer();
    String result = stemmer.stem("realize");
    assertEquals("real", result);
  }
@Test
  public void testStep5ENCE() {
    PorterStemmer stemmer = new PorterStemmer();
    String result = stemmer.stem("independence");
    assertEquals("independ", result);
  }
@Test
  public void testStep5IVE() {
    PorterStemmer stemmer = new PorterStemmer();
    String result = stemmer.stem("sensitive");
    assertEquals("sensit", result);
  }
@Test
  public void testStep6FinalE() {
    PorterStemmer stemmer = new PorterStemmer();
    String result = stemmer.stem("rate");
    assertEquals("rate", result); 
  }
@Test
  public void testStep6DoubleL() {
    PorterStemmer stemmer = new PorterStemmer();
    String result = stemmer.stem("controll");
    assertEquals("control", result);
  }
@Test
  public void testStemReturnsOriginalForShortWords() {
    PorterStemmer stemmer = new PorterStemmer();
    String result = stemmer.stem("is");
    assertEquals("is", result);
  }
@Test
  public void testStemReturnsOriginalForIrregularWord() {
    PorterStemmer stemmer = new PorterStemmer();
    String result = stemmer.stem("sky");
    assertEquals("sky", result);
  }
@Test
  public void testStemFromCharArrayReturnsTrueOnChange() {
    PorterStemmer stemmer = new PorterStemmer();
    char[] word = "meeting".toCharArray();
    boolean changed = stemmer.stem(word, word.length);
    assertTrue(changed);
    assertEquals("meet", stemmer.toString());
  }
@Test
  public void testStemFromCharArrayReturnsFalseOnNoChange() {
    PorterStemmer stemmer = new PorterStemmer();
    char[] word = "book".toCharArray();
    boolean changed = stemmer.stem(word, word.length);
    assertFalse(changed);
    assertEquals("book", stemmer.toString());
  }
@Test
  public void testStemCharSequence() {
    PorterStemmer stemmer = new PorterStemmer();
    CharSequence seq = "happiness";
    CharSequence stemmed = stemmer.stem(seq);
    assertEquals("happi", stemmed.toString());
  }
@Test
  public void testResetClearsBuffer() {
    PorterStemmer stemmer = new PorterStemmer();
    stemmer.add('a');
    stemmer.reset();
    stemmer.add('b');
    stemmer.stem();
    assertEquals("b", stemmer.toString());
  }
@Test
  public void testToStringAndBufferResult() {
    PorterStemmer stemmer = new PorterStemmer();
    String result = stemmer.stem("running");
    assertEquals("run", result);
    assertEquals(3, stemmer.getResultLength());
    assertEquals('r', stemmer.getResultBuffer()[0]);
    assertEquals('u', stemmer.getResultBuffer()[1]);
    assertEquals('n', stemmer.getResultBuffer()[2]);
  }
@Test
  public void testStemWithAddChar() {
    PorterStemmer stemmer = new PorterStemmer();
    stemmer.add('j');
    stemmer.add('u');
    stemmer.add('m');
    stemmer.add('p');
    stemmer.add('i');
    stemmer.add('n');
    stemmer.add('g');
    boolean changed = stemmer.stem();
    assertTrue(changed);
    assertEquals("jump", stemmer.toString());
  }
@Test
  public void testStemCharArrayWithOffset() {
    PorterStemmer stemmer = new PorterStemmer();
    char[] array = "_running!".toCharArray();
    boolean changed = stemmer.stem(array, 1, 7);
    assertTrue(changed);
    assertEquals("run", stemmer.toString());
  }
@Test
  public void testNoModificationReturnsSameWord() {
    PorterStemmer stemmer = new PorterStemmer();
    String stemmed = stemmer.stem("sun");
    assertEquals("sun", stemmed);
  }
@Test
  public void testBufferGrowth() {
    PorterStemmer stemmer = new PorterStemmer();
    stemmer.add('s');
    stemmer.add('u');
    stemmer.add('p');
    stemmer.add('e');
    stemmer.add('r');
    stemmer.add('c');
    stemmer.add('a');
    stemmer.add('l');
    stemmer.add('i');
    stemmer.add('f');
    stemmer.add('r');
    stemmer.add('a');
    stemmer.add('g');
    stemmer.add('i');
    stemmer.add('l');
    stemmer.add('i');
    stemmer.add('s');
    stemmer.add('t');
    stemmer.add('i');
    stemmer.add('c');
    stemmer.add('e');
    stemmer.add('x');
    stemmer.add('p');
    stemmer.add('i');
    stemmer.add('a');
    stemmer.add('l');
    stemmer.add('i');
    stemmer.add('d');
    stemmer.add('o');
    stemmer.add('c');
    stemmer.add('i');
    stemmer.add('o');
    stemmer.add('u');
    stemmer.add('s');
    boolean changed = stemmer.stem();
    assertTrue(stemmer.getResultLength() > 0);
    assertNotNull(stemmer.toString());
    assertTrue(stemmer.getResultBuffer().length >= stemmer.getResultLength());
  } 
}