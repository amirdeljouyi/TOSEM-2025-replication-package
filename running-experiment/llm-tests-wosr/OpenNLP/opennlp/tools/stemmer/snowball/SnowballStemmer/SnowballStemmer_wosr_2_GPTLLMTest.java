public class SnowballStemmer_wosr_2_GPTLLMTest { 

 @Test
  public void testEnglishStemmingDefaultRepeat() {
    SnowballStemmer stemmer = new SnowballStemmer(ALGORITHM.ENGLISH);
    CharSequence result = stemmer.stem("running");
    assertEquals("run", result.toString());
  }
@Test
  public void testEnglishStemmingWithRepeat() {
    SnowballStemmer stemmer = new SnowballStemmer(ALGORITHM.ENGLISH, 2);
    CharSequence result = stemmer.stem("running");
    assertEquals("run", result.toString());
  }
@Test
  public void testDutchStemming() {
    SnowballStemmer stemmer = new SnowballStemmer(ALGORITHM.DUTCH);
    CharSequence result = stemmer.stem("lichter");
    assertEquals("licht", result.toString());
  }
@Test
  public void testGermanStemming() {
    SnowballStemmer stemmer = new SnowballStemmer(ALGORITHM.GERMAN);
    CharSequence result = stemmer.stem("häuser");
    assertEquals("haus", result.toString());
  }
@Test
  public void testFrenchStemming() {
    SnowballStemmer stemmer = new SnowballStemmer(ALGORITHM.FRENCH);
    CharSequence result = stemmer.stem("mangera");
    assertEquals("manger", result.toString());
  }
@Test
  public void testSpanishStemming() {
    SnowballStemmer stemmer = new SnowballStemmer(ALGORITHM.SPANISH);
    CharSequence result = stemmer.stem("hablando");
    assertEquals("habl", result.toString());
  }
@Test
  public void testPortugueseStemming() {
    SnowballStemmer stemmer = new SnowballStemmer(ALGORITHM.PORTUGUESE);
    CharSequence result = stemmer.stem("corriendo");
    assertEquals("corr", result.toString());
  }
@Test
  public void testItalianStemming() {
    SnowballStemmer stemmer = new SnowballStemmer(ALGORITHM.ITALIAN);
    CharSequence result = stemmer.stem("amando");
    assertEquals("am", result.toString());
  }
@Test
  public void testRussianStemming() {
    SnowballStemmer stemmer = new SnowballStemmer(ALGORITHM.RUSSIAN);
    CharSequence result = stemmer.stem("машины");
    assertEquals("машин", result.toString());
  }
@Test
  public void testTurkishStemming() {
    SnowballStemmer stemmer = new SnowballStemmer(ALGORITHM.TURKISH);
    CharSequence result = stemmer.stem("kitaplar");
    assertEquals("kitap", result.toString());
  }
@Test
  public void testGreekStemming() {
    SnowballStemmer stemmer = new SnowballStemmer(ALGORITHM.GREEK);
    CharSequence result = stemmer.stem("άνθρωποι");
    assertEquals("ανθρωπ", result.toString());
  }
@Test
  public void testFinnishStemming() {
    SnowballStemmer stemmer = new SnowballStemmer(ALGORITHM.FINNISH);
    CharSequence result = stemmer.stem("juoksemassa");
    assertEquals("juoks", result.toString());
  }
@Test
  public void testIrishStemming() {
    SnowballStemmer stemmer = new SnowballStemmer(ALGORITHM.IRISH);
    CharSequence result = stemmer.stem("hataí");
    assertEquals("hat", result.toString());
  }
@Test
  public void testHungarianStemming() {
    SnowballStemmer stemmer = new SnowballStemmer(ALGORITHM.HUNGARIAN);
    CharSequence result = stemmer.stem("házaim");
    assertEquals("ház", result.toString());
  }
@Test
  public void testRomanianStemming() {
    SnowballStemmer stemmer = new SnowballStemmer(ALGORITHM.ROMANIAN);
    CharSequence result = stemmer.stem("copiilor");
    assertEquals("copi", result.toString());
  }
@Test
  public void testIndonesianStemming() {
    SnowballStemmer stemmer = new SnowballStemmer(ALGORITHM.INDONESIAN);
    CharSequence result = stemmer.stem("berlari");
    assertEquals("lari", result.toString());
  }
@Test
  public void testNorwegianStemming() {
    SnowballStemmer stemmer = new SnowballStemmer(ALGORITHM.NORWEGIAN);
    CharSequence result = stemmer.stem("bøkene");
    assertEquals("bøk", result.toString());
  }
@Test
  public void testDanishStemming() {
    SnowballStemmer stemmer = new SnowballStemmer(ALGORITHM.DANISH);
    CharSequence result = stemmer.stem("arbejdende");
    assertEquals("arbejd", result.toString());
  }
@Test
  public void testCatalanStemming() {
    SnowballStemmer stemmer = new SnowballStemmer(ALGORITHM.CATALAN);
    CharSequence result = stemmer.stem("cantant");
    assertEquals("cant", result.toString());
  }
@Test
  public void testArabicStemming() {
    SnowballStemmer stemmer = new SnowballStemmer(ALGORITHM.ARABIC);
    CharSequence result = stemmer.stem("الكتابات");
    assertEquals("كتاب", result.toString());
  }
@Test
  public void testSwedishStemming() {
    SnowballStemmer stemmer = new SnowballStemmer(ALGORITHM.SWEDISH);
    CharSequence result = stemmer.stem("spelade");
    assertEquals("spel", result.toString());
  }
@Test
  public void testPorterStemming() {
    SnowballStemmer stemmer = new SnowballStemmer(ALGORITHM.PORTER);
    CharSequence result = stemmer.stem("generalization");
    assertEquals("general", result.toString());
  }
@Test(expected = IllegalStateException.class)
  public void testInvalidAlgorithmThrowsException() {
    SnowballStemmer stemmer = new SnowballStemmer(null);
    stemmer.stem("example");
  }
@Test
  public void testEmptyInput() {
    SnowballStemmer stemmer = new SnowballStemmer(ALGORITHM.ENGLISH);
    CharSequence result = stemmer.stem("");
    assertEquals("", result.toString());
  }
@Test
  public void testWhitespaceInput() {
    SnowballStemmer stemmer = new SnowballStemmer(ALGORITHM.ENGLISH);
    CharSequence result = stemmer.stem(" ");
    assertEquals("", result.toString());
  }
@Test
  public void testPunctuationInput() {
    SnowballStemmer stemmer = new SnowballStemmer(ALGORITHM.ENGLISH);
    CharSequence result = stemmer.stem("!?.,;");
    assertEquals("!?.,;", result.toString());
  }
@Test
  public void testNumericInput() {
    SnowballStemmer stemmer = new SnowballStemmer(ALGORITHM.ENGLISH);
    CharSequence result = stemmer.stem("123456");
    assertEquals("123456", result.toString());
  }
@Test
  public void testUppercaseInput() {
    SnowballStemmer stemmer = new SnowballStemmer(ALGORITHM.ENGLISH);
    CharSequence result = stemmer.stem("RUNNING");
    assertEquals("run", result.toString());
  }
@Test
  public void testSingleCharacterInput() {
    SnowballStemmer stemmer = new SnowballStemmer(ALGORITHM.ENGLISH);
    CharSequence result = stemmer.stem("a");
    assertEquals("a", result.toString());
  }
@Test
  public void testLongWordInput() {
    SnowballStemmer stemmer = new SnowballStemmer(ALGORITHM.ENGLISH);
    CharSequence result = stemmer.stem("internationalization");
    assertEquals("intern", result.toString());
  }
@Test
  public void testStemMultipleTimesPreservesState() {
    SnowballStemmer stemmer = new SnowballStemmer(ALGORITHM.ENGLISH);
    CharSequence first = stemmer.stem("running");
    assertEquals("run", first.toString());
    CharSequence second = stemmer.stem("jogging");
    assertEquals("jog", second.toString());
  } 
}