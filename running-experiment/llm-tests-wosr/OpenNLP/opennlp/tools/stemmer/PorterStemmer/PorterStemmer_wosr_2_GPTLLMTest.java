public class PorterStemmer_wosr_2_GPTLLMTest { 

 @Test
  public void testStem_simplePlural() {
    PorterStemmer stemmer = new PorterStemmer();
    String result = stemmer.stem("cats");
    assertEquals("cat", result);
  }
@Test
  public void testStem_pluralEndingWithEs() {
    PorterStemmer stemmer = new PorterStemmer();
    String result = stemmer.stem("caresses");
    assertEquals("caress", result);
  }
@Test
  public void testStem_pluralEndingWithIes() {
    PorterStemmer stemmer = new PorterStemmer();
    String result = stemmer.stem("ponies");
    assertEquals("poni", result);
  }
@Test
  public void testStem_edEnding_wordWithVowel() {
    PorterStemmer stemmer = new PorterStemmer();
    String result = stemmer.stem("agreed");
    assertEquals("agree", result);
  }
@Test
  public void testStem_ingEnding_wordWithVowel() {
    PorterStemmer stemmer = new PorterStemmer();
    String result = stemmer.stem("meeting");
    assertEquals("meet", result);
  }
@Test
  public void testStem_ingWithCvcEnding() {
    PorterStemmer stemmer = new PorterStemmer();
    String result = stemmer.stem("hoping");
    assertEquals("hope", result);
  }
@Test
  public void testStem_edEnding_noVowel() {
    PorterStemmer stemmer = new PorterStemmer();
    String result = stemmer.stem("bled");
    assertEquals("bled", result);
  }
@Test
  public void testStem_terminalY_withVowel() {
    PorterStemmer stemmer = new PorterStemmer();
    String result = stemmer.stem("happy");
    assertEquals("happi", result);
  }
@Test
  public void testStem_terminalY_noVowel() {
    PorterStemmer stemmer = new PorterStemmer();
    String result = stemmer.stem("sky");
    assertEquals("sky", result);
  }
@Test
  public void testStem_doubleSuffix() {
    PorterStemmer stemmer = new PorterStemmer();
    String result = stemmer.stem("nationalization");
    assertEquals("nation", result);
  }
@Test
  public void testStem_step4Suffix_icate() {
    PorterStemmer stemmer = new PorterStemmer();
    String result = stemmer.stem("communicate");
    assertEquals("communic", result);
  }
@Test
  public void testStem_step4Suffix_ative() {
    PorterStemmer stemmer = new PorterStemmer();
    String result = stemmer.stem("formative");
    assertEquals("form", result);
  }
@Test
  public void testStem_step4Suffix_alize() {
    PorterStemmer stemmer = new PorterStemmer();
    String result = stemmer.stem("finalize");
    assertEquals("final", result);
  }
@Test
  public void testStem_removeFinalE_longWord() {
    PorterStemmer stemmer = new PorterStemmer();
    String result = stemmer.stem("terminate");
    assertEquals("termin", result);
  }
@Test
  public void testStem_removeFinalL_doubleConsonant() {
    PorterStemmer stemmer = new PorterStemmer();
    String result = stemmer.stem("controlled");
    assertEquals("control", result);
  }
@Test
  public void testStem_noChange_wordTooShort() {
    PorterStemmer stemmer = new PorterStemmer();
    String result = stemmer.stem("is");
    assertEquals("is", result);
  }
@Test
  public void testStem_consistency_addCharThenStem() {
    PorterStemmer stemmer = new PorterStemmer();
    stemmer.reset();
    stemmer.add('b');
    stemmer.add('i');
    stemmer.add('l');
    stemmer.add('l');
    stemmer.add('i');
    stemmer.add('n');
    stemmer.add('g');
    boolean changed = stemmer.stem();
    String result = stemmer.toString();
    assertTrue(changed);
    assertEquals("bill", result);
  }
@Test
  public void testStem_charArray_interface() {
    PorterStemmer stemmer = new PorterStemmer();
    char[] word = "meeting".toCharArray();
    boolean changed = stemmer.stem(word);
    String result = stemmer.toString();
    assertTrue(changed);
    assertEquals("meet", result);
  }
@Test
  public void testStem_charArrayOffset_interface() {
    PorterStemmer stemmer = new PorterStemmer();
    char[] buffer = "xxmeetingyy".toCharArray();
    boolean changed = stemmer.stem(buffer, 2, 7); 
    String result = stemmer.toString();
    assertTrue(changed);
    assertEquals("meet", result);
  }
@Test
  public void testStem_charSequence_interface() {
    PorterStemmer stemmer = new PorterStemmer();
    CharSequence result = stemmer.stem((CharSequence) "generalization");
    assertEquals("general", result.toString());
  }
@Test
  public void testStem_doublecCondition_keepsZ() {
    PorterStemmer stemmer = new PorterStemmer();
    String result = stemmer.stem("buzzing");
    assertEquals("buzz", result);
  }
@Test
  public void testStem_doublecRemoves_lastL() {
    PorterStemmer stemmer = new PorterStemmer();
    String result = stemmer.stem("milling");
    assertEquals("mill", result);
  }
@Test
  public void test_getResultBufferReflectsStem() {
    PorterStemmer stemmer = new PorterStemmer();
    stemmer.stem("rationalization");
    char[] buffer = stemmer.getResultBuffer();
    int length = stemmer.getResultLength();
    String result = new String(buffer, 0, length);
    assertEquals("ration", result);
  }
@Test
  public void testStemStep5_withIonEndingBugFix_s() {
    PorterStemmer stemmer = new PorterStemmer();
    String result = stemmer.stem("extension");
    assertEquals("extens", result);
  }
@Test
  public void testStemStep5_withIonEndingBugFix_t() {
    PorterStemmer stemmer = new PorterStemmer();
    String result = stemmer.stem("motion");
    assertEquals("mot", result);
  }
@Test
  public void testStem_noChangeOnNonStemmableWord() {
    PorterStemmer stemmer = new PorterStemmer();
    String result = stemmer.stem("a");
    assertEquals("a", result);
  }
@Test
  public void testStem_multipleWordsIndependentBuffer() {
    PorterStemmer stemmer = new PorterStemmer();
    String first = stemmer.stem("running");
    assertEquals("run", first);
    String second = stemmer.stem("meetings");
    assertEquals("meet", second);
  }
@Test
  public void testStem_bufferExpansion() {
    PorterStemmer stemmer = new PorterStemmer();
    stemmer.reset();
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
    boolean changed = stemmer.stem();
    assertFalse(changed); 
    String result = stemmer.toString();
    assertEquals("supercalifragilistic", result);
  } 
}