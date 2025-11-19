public class Morphology_wosr_3_GPTLLMTest { 

 @Test
  public void testStemWithRegularVerb() {
    Morphology morphology = new Morphology();
    String result = morphology.stem("walking");
    assertEquals("walk", result);
  }
@Test
  public void testStemWithIrregularVerb() {
    Morphology morphology = new Morphology();
    String result = morphology.stem("went");
    assertEquals("go", result);
  }
@Test
  public void testStemWordObject() {
    Morphology morphology = new Morphology();
    Word word = new Word("dogs");
    Word result = morphology.stem(word);
    assertEquals("dog", result.word());
  }
@Test
  public void testNextReturnsNullOnEmptyStream() throws IOException {
    Morphology morphology = new Morphology(new StringReader(""));
    Word result = morphology.next();
    assertNull(result);
  }
@Test
  public void testNextReturnsWord() throws IOException {
    Morphology morphology = new Morphology(new StringReader("running"));
    Word result = morphology.next();
    assertNotNull(result);
    assertEquals("run", result.word());
  }
@Test
  public void testLemmaWithLowercaseFlagTrue() {
    Morphology morphology = new Morphology();
    String result = morphology.lemma("Running", "VBG", true);
    assertEquals("run", result);
  }
@Test
  public void testLemmaWithLowercaseFlagFalseProperNoun() {
    Morphology morphology = new Morphology();
    String result = morphology.lemma("Chicago", "NNP", false);
    assertEquals("Chicago", result);
  }
@Test
  public void testStemCoreLabelAddsLemmaAnnotation() {
    Morphology morphology = new Morphology();
    CoreLabel label = new CoreLabel();
    label.setWord("faster");
    label.setTag("RBR");
    morphology.stem(label);
    String lemma = label.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("fast", lemma);
  }
@Test
  public void testApplyWithWordTag() {
    Morphology morphology = new Morphology();
    WordTag input = new WordTag("chased", "VBD");
    Object result = morphology.apply(input);
    assertTrue(result instanceof WordTag);
    assertEquals("chase", ((WordTag) result).word());
  }
@Test
  public void testApplyWithWord() {
    Morphology morphology = new Morphology();
    Word input = new Word("jumped");
    Object result = morphology.apply(input);
    assertTrue(result instanceof Word);
    assertEquals("jump", ((Word) result).word());
  }
@Test
  public void testApplyWithUnknownObjectReturnsSame() {
    Morphology morphology = new Morphology();
    Object input = "test";
    Object result = morphology.apply(input);
    assertEquals("test", result);
  }
@Test
  public void testStemStaticWithWordTag() {
    WordTag result = Morphology.stemStatic("played", "VBD");
    assertEquals("play", result.word());
    assertEquals("VBD", result.tag());
  }
@Test
  public void testStemStaticWithWordTagObject() {
    WordTag input = new WordTag("cats", "NNS");
    WordTag result = Morphology.stemStatic(input);
    assertEquals("cat", result.word());
    assertEquals("NNS", result.tag());
  }
@Test
  public void testLemmaStaticDefaultLowercase() {
    String lemma = Morphology.lemmaStatic("JUMPING", "VBG");
    assertEquals("jump", lemma);
  }
@Test
  public void testLemmaStaticNoLowercaseOnProperNoun() {
    String lemma = Morphology.lemmaStatic("Stanford", "NNP", false);
    assertEquals("Stanford", lemma);
  }
@Test
  public void testLemmatizeInstanceReturnsCorrectResult() {
    Morphology morphology = new Morphology();
    WordTag input = new WordTag("children", "NNS");
    WordLemmaTag result = morphology.lemmatize(input);
    assertEquals("children", result.word());
    assertEquals("child", result.lemma());
    assertEquals("NNS", result.tag());
  }
@Test
  public void testLemmatizeStaticReturnsCorrectResult() {
    WordTag input = new WordTag("better", "JJR");
    WordLemmaTag result = Morphology.lemmatizeStatic(input);
    assertEquals("better", result.word());
    assertEquals("good", result.lemma());
    assertEquals("JJR", result.tag());
  }
@Test
  public void testStemWithNullWordReturnsNull() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.stem("");
    assertEquals("", result);
  }
@Test
  public void testStemCoreLabelWithDifferentAnnotationClass() {
    Morphology morphology = new Morphology();
    CoreLabel label = new CoreLabel();
    label.setWord("Prettier");
    label.setTag("JJR");
    morphology.stem(label, CoreAnnotations.LemmaAnnotation.class);
    String lemma = label.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("pretty", lemma);
  }
@Test
  public void testLemmatizeWithUnderscoreInWord() {
    Morphology morphology = new Morphology();
    String result = morphology.lemma("multi_word", "NN");
    assertEquals("multi_word", result);
  }
@Test
  public void testLemmatizeWithSpaceInWord() {
    Morphology morphology = new Morphology();
    String result = morphology.lemma("red apple", "NN");
    assertEquals("red apple", result);
  }
@Test
  public void testLemmatizeWithNewlineInWord() {
    Morphology morphology = new Morphology();
    String result = morphology.lemma("red\napple", "NN");
    assertEquals("red\napple", result);
  }
@Test
  public void testStemWithMultipleWords() {
    Morphology morphology = new Morphology();
    String result1 = morphology.stem("houses");
    String result2 = morphology.stem("running");
    String result3 = morphology.stem("children");
    assertEquals("house", result1);
    assertEquals("run", result2);
    assertEquals("child", result3);
  }
@Test
  public void testStemHandlesIOException() {
    Morphology morphology = new Morphology();
    String faultyInput = "\u0000\u0000";
    String result = morphology.stem(faultyInput);
    assertEquals(faultyInput, result);
  }
@Test
  public void testLemmatizeHandlesIOException() {
    Morphology morphology = new Morphology();
    String faultyInput = "\u0000\u0000";
    String result = morphology.lemma(faultyInput, "NN");
    assertEquals(faultyInput, result);
  }
@Test
  public void testStemLowerCaseNoun() {
    Morphology morphology = new Morphology();
    String lemma = morphology.lemma("Balls", "NNS", true);
    assertEquals("ball", lemma);
  }
@Test
  public void testStemUpperCaseProperNoun() {
    Morphology morphology = new Morphology();
    String lemma = morphology.lemma("John", "NNP", true);
    assertEquals("John", lemma);
  }
@Test
  public void testStemVerbEnding() {
    Morphology morphology = new Morphology();
    String lemma = morphology.lemma("swimming", "VBG");
    assertEquals("swim", lemma);
  } 
}