public class NERFeatureFactory_wosr_1_GPTLLMTest { 

 @Test
  public void testGetCliqueFeatures_CliqueC_WithMinimalFlags() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Barack");
    token.set(CoreAnnotations.DomainAnnotation.class, "test");

    PaddedList<CoreLabel> paddedList = new PaddedList<>();
    paddedList.add(token);

    Set<String> features = new HashSet<>(factory.getCliqueFeatures(paddedList, 0, Clique.valueOf("C")));

    assertNotNull(features);
    assertTrue(features.contains("Barack-WORD|C")); 
  }
@Test
  public void testGetCliqueFeatures_CliqueCpC_WithPrevNextFeatures() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.usePrev = true;
    flags.useNext = true;
    flags.useSequences = true;
    flags.usePrevSequences = true;
    flags.useNextSequences = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel prev = new CoreLabel();
    prev.set(CoreAnnotations.TextAnnotation.class, "Mr");
    prev.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    prev.set(CoreAnnotations.ShapeAnnotation.class, "Xx");
    prev.set(CoreAnnotations.DomainAnnotation.class, "test");

    CoreLabel current = new CoreLabel();
    current.set(CoreAnnotations.TextAnnotation.class, "Smith");
    current.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    current.set(CoreAnnotations.ShapeAnnotation.class, "Xxxxx");
    current.set(CoreAnnotations.DomainAnnotation.class, "test");

    CoreLabel next = new CoreLabel();
    next.set(CoreAnnotations.TextAnnotation.class, "is");
    next.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBZ");
    next.set(CoreAnnotations.ShapeAnnotation.class, "xx");
    next.set(CoreAnnotations.DomainAnnotation.class, "test");

    PaddedList<CoreLabel> list = new PaddedList<>();
    list.add(prev);
    list.add(current);
    list.add(next);

    Set<String> features = new HashSet<>(factory.getCliqueFeatures(list, 1, Clique.valueOf("CpC")));

    assertNotNull(features);
    assertTrue(features.contains("PSEQ|CpC"));
    assertTrue(features.contains("Smith-PSEQW|CpC"));
    assertTrue(features.contains("Mr-SWORDS|CpC")); 
  }
@Test
  public void testGetCliqueFeatures_UnknownClique_ThrowsException() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Invalid");
    token.set(CoreAnnotations.DomainAnnotation.class, "test");

    PaddedList<CoreLabel> list = new PaddedList<>();
    list.add(token);

    try {
      factory.getCliqueFeatures(list, 0, Clique.valueOf("UnknownClique"));
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("Unknown clique"));
    }
  }
@Test
  public void testDistSimInitializationWithCasedWord() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useDistSim = true;
    flags.distSimLexicon = "edu/stanford/nlp/ie/sample-lexicon.txt";
    flags.inputEncoding = "utf-8";
    flags.casedDistSim = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    assertNotNull(factory.describeDistsimLexicon());
    assertTrue(factory.describeDistsimLexicon().startsWith("Distsim lexicon of size"));
  }
@Test
  public void testWordToSubstringsCacheBehavior() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useInternal = true;
    flags.useWord = true;
    flags.useNGrams = true;
    flags.cacheNGrams = true;
    flags.maxNGramLeng = 3;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel token = new CoreLabel();
    token.setWord("Stanford");
    token.set(CoreAnnotations.DomainAnnotation.class, "test");

    PaddedList<CoreLabel> padded = new PaddedList<>();
    padded.add(token);

    Set<String> output1 = new HashSet<>(factory.getCliqueFeatures(padded, 0, Clique.valueOf("C")));
    Set<String> output2 = new HashSet<>(factory.getCliqueFeatures(padded, 0, Clique.valueOf("C")));

    assertEquals(output1, output2); 
  }
@Test
  public void testDehyphenateWord() {
    String input = "<A-B-C>";
    String result = NERFeatureFactory.dehyphenate(input);
    assertEquals("<ABC>", result);
  }
@Test
  public void testGreekifyReplacesGreekLetters() {
    String input = "alpha blocker and beta signal";
    String result = NERFeatureFactory.greekify(input);
    assertTrue(result.contains("~ blocker and ~ signal"));
  }
@Test
  public void testIsNameCase_ValidName() {
    boolean result = NERFeatureFactory.isNameCase("John");
    assertTrue(result);
  }
@Test
  public void testIsNameCase_InvalidName() {
    boolean result = NERFeatureFactory.isNameCase("john");
    assertFalse(result);
  }
@Test
  public void testNoUpperCase_OnlyLowerCase() {
    boolean result = NERFeatureFactory.noUpperCase("lowercase");
    assertTrue(result);
  }
@Test
  public void testNoUpperCase_WithUpperCase() {
    boolean result = NERFeatureFactory.noUpperCase("notLower");
    assertFalse(result);
  }
@Test
  public void testHasLetter_Valid() {
    boolean result = NERFeatureFactory.hasLetter("123abc");
    assertTrue(result);
  }
@Test
  public void testHasLetter_Invalid() {
    boolean result = NERFeatureFactory.hasLetter("12345");
    assertFalse(result);
  }
@Test
  public void testIsOrdinal_MatchingOrdinal() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel token = new CoreLabel();
    token.setWord("1st");
    List<CoreLabel> list = new ArrayList<>();
    list.add(token);

    boolean result = factory.isOrdinal(list, 0);
    assertTrue(result);
  }
@Test
  public void testIsOrdinal_MatchingWordOrdinal() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel token = new CoreLabel();
    token.setWord("first");

    List<CoreLabel> list = new ArrayList<>();
    list.add(token);

    boolean result = factory.isOrdinal(list, 0);
    assertTrue(result);
  }
@Test
  public void testClearMemoryEmptiesCaches() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useInternal = true;
    flags.useWord = true;
    flags.useNGrams = true;
    flags.cacheNGrams = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel token = new CoreLabel();
    token.setWord("Entity");
    token.set(CoreAnnotations.DomainAnnotation.class, "mock");

    PaddedList<CoreLabel> padded = new PaddedList<>();
    padded.add(token);

    factory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
    assertFalse(factory.wordToSubstrings.isEmpty());
    factory.clearMemory();
    assertTrue(factory.wordToSubstrings.isEmpty());
  } 
}