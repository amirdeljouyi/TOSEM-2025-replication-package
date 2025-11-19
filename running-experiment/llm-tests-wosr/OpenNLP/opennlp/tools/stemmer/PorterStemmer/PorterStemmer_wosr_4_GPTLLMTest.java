public class PorterStemmer_wosr_4_GPTLLMTest { 

 @Test
  public void testStem_simplePlural_singularReturned() {
    PorterStemmer stemmer = new PorterStemmer();
    String stemmed = stemmer.stem("cats");
    assertEquals("cat", stemmed);
  }
@Test
  public void testStem_pluralEndingWithEs_singularReturned() {
    PorterStemmer stemmer = new PorterStemmer();
    String stemmed = stemmer.stem("dresses");
    assertEquals("dress", stemmed);
  }
@Test
  public void testStem_pluralEndingWithIes_singularReturned() {
    PorterStemmer stemmer = new PorterStemmer();
    String stemmed = stemmer.stem("ponies");
    assertEquals("poni", stemmed);
  }
@Test
  public void testStem_verbEndingInEd_processedCorrectly() {
    PorterStemmer stemmer = new PorterStemmer();
    String stemmed = stemmer.stem("agreed");
    assertEquals("agree", stemmed);
  }
@Test
  public void testStem_verbEndingInIng_processedCorrectly() {
    PorterStemmer stemmer = new PorterStemmer();
    String stemmed = stemmer.stem("meeting");
    assertEquals("meet", stemmed);
  }
@Test
  public void testStem_doubleConsonant_retainsLastLetterIfInList() {
    PorterStemmer stemmer = new PorterStemmer();
    String stemmed = stemmer.stem("miss");
    assertEquals("miss", stemmed);
  }
@Test
  public void testStem_doubleConsonant_notRetainedIfNotInList() {
    PorterStemmer stemmer = new PorterStemmer();
    String stemmed = stemmer.stem("stopping");
    assertEquals("stop", stemmed);
  }
@Test
  public void testStem_shortWordEndingInE_retainsE() {
    PorterStemmer stemmer = new PorterStemmer();
    String stemmed = stemmer.stem("hope");
    assertEquals("hope", stemmed);
  }
@Test
  public void testStem_terminalYChangedToI() {
    PorterStemmer stemmer = new PorterStemmer();
    String stemmed = stemmer.stem("happy");
    assertEquals("happi", stemmed);
  }
@Test
  public void testStem_compoundSuffix_ationalToAte() {
    PorterStemmer stemmer = new PorterStemmer();
    String stemmed = stemmer.stem("motivational");
    assertEquals("motivate", stemmed);
  }
@Test
  public void testStem_compoundSuffix_tionalToTion() {
    PorterStemmer stemmer = new PorterStemmer();
    String stemmed = stemmer.stem("national");
    assertEquals("nation", stemmed);
  }
@Test
  public void testStem_suffixfulness_removed() {
    PorterStemmer stemmer = new PorterStemmer();
    String stemmed = stemmer.stem("helpfulness");
    assertEquals("help", stemmed);
  }
@Test
  public void testStem_suffixation_replacedWithAte() {
    PorterStemmer stemmer = new PorterStemmer();
    String stemmed = stemmer.stem("automation");
    assertEquals("automate", stemmed);
  }
@Test
  public void testStem_suffixizer_replacedWithIze() {
    PorterStemmer stemmer = new PorterStemmer();
    String stemmed = stemmer.stem("organizer");
    assertEquals("organize", stemmed);
  }
@Test
  public void testStem_suffixness_removed() {
    PorterStemmer stemmer = new PorterStemmer();
    String stemmed = stemmer.stem("kindness");
    assertEquals("kind", stemmed);
  }
@Test
  public void testStem_suffixence_removed() {
    PorterStemmer stemmer = new PorterStemmer();
    String stemmed = stemmer.stem("absence");
    assertEquals("absenc", stemmed);
  }
@Test
  public void testStem_suffixmentStripped() {
    PorterStemmer stemmer = new PorterStemmer();
    String stemmed = stemmer.stem("adjustment");
    assertEquals("adjust", stemmed);
  }
@Test
  public void testStem_suffixion_conditionallyStripped() {
    PorterStemmer stemmer = new PorterStemmer();
    String stemmed = stemmer.stem("vibration");
    assertEquals("vibrat", stemmed);
  }
@Test
  public void testStem_suffixive_removed() {
    PorterStemmer stemmer = new PorterStemmer();
    String stemmed = stemmer.stem("active");
    assertEquals("activ", stemmed);
  }
@Test
  public void testStem_suffixabilityToBle() {
    PorterStemmer stemmer = new PorterStemmer();
    String stemmed = stemmer.stem("availability");
    assertEquals("avail", stemmed);
  }
@Test
  public void testStem_suffixfull_removed() {
    PorterStemmer stemmer = new PorterStemmer();
    String stemmed = stemmer.stem("hopeful");
    assertEquals("hope", stemmed);
  }
@Test
  public void testStem_suffixicitiToIc() {
    PorterStemmer stemmer = new PorterStemmer();
    String stemmed = stemmer.stem("electricity");
    assertEquals("electric", stemmed);
  }
@Test
  public void testStem_suffixicalToIc() {
    PorterStemmer stemmer = new PorterStemmer();
    String stemmed = stemmer.stem("magical");
    assertEquals("magic", stemmed);
  }
@Test
  public void testStem_suffixness_onShortWord() {
    PorterStemmer stemmer = new PorterStemmer();
    String stemmed = stemmer.stem("sadness");
    assertEquals("sad", stemmed);
  }
@Test
  public void testStem_complexCase_multipleSteps() {
    PorterStemmer stemmer = new PorterStemmer();
    String stemmed = stemmer.stem("relationally");
    assertEquals("relate", stemmed);
  }
@Test
  public void testStem_addCharacterOneAtATime() {
    PorterStemmer stemmer = new PorterStemmer();
    stemmer.reset();
    stemmer.add('w');
    stemmer.add('a');
    stemmer.add('i');
    stemmer.add('t');
    stemmer.add('i');
    stemmer.add('n');
    stemmer.add('g');
    boolean changed = stemmer.stem();
    assertEquals("wait", stemmer.toString());
    assertTrue(changed);
  }
@Test
  public void testStem_shortWord_noChange() {
    PorterStemmer stemmer = new PorterStemmer();
    String stemmed = stemmer.stem("to");
    assertEquals("to", stemmed);
  }
@Test
  public void testStem_emptyString() {
    PorterStemmer stemmer = new PorterStemmer();
    String stemmed = stemmer.stem("");
    assertEquals("", stemmed);
  }
@Test
  public void testStem_singleCharacterWord() {
    PorterStemmer stemmer = new PorterStemmer();
    String stemmed = stemmer.stem("a");
    assertEquals("a", stemmed);
  }
@Test
  public void testStem_withCharArray_directStem() {
    PorterStemmer stemmer = new PorterStemmer();
    boolean changed = stemmer.stem("failing".toCharArray());
    assertEquals("fail", stemmer.toString());
    assertTrue(changed);
  }
@Test
  public void testStem_withCharArrayAndOffset() {
    PorterStemmer stemmer = new PorterStemmer();
    char[] word = {'x', 'y', 'z', 's', 't', 'u', 'd', 'y'};
    boolean changed = stemmer.stem(word, 3, 5);
    assertEquals("studi", stemmer.toString());
    assertTrue(changed);
  }
@Test
  public void testToString_afterStem() {
    PorterStemmer stemmer = new PorterStemmer();
    stemmer.stem("caresses");
    assertEquals("caress", stemmer.toString());
  }
@Test
  public void testGetResultLengthAndBuffer() {
    PorterStemmer stemmer = new PorterStemmer();
    stemmer.stem("caresses");
    char[] result = stemmer.getResultBuffer();
    int length = stemmer.getResultLength();
    String output = new String(result, 0, length);
    assertEquals("caress", output);
  }
@Test
  public void testConsecutiveStemming_invocationsResetCorrectly() {
    PorterStemmer stemmer = new PorterStemmer();
    String first = stemmer.stem("meeting");
    assertEquals("meet", first);
    String second = stemmer.stem("meetings");
    assertEquals("meet", second);
  } 
}