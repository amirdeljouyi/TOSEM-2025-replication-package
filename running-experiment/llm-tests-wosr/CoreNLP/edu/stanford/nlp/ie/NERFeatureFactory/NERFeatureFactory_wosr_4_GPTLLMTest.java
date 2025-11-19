public class NERFeatureFactory_wosr_4_GPTLLMTest { 

 @Test
  public void testGetCliqueFeatures_withCliqueC_basicFeatureExtraction() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    factory.init(flags);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Stanford");
    token.set(CoreAnnotations.ShapeAnnotation.class, "Xxxxx");
    token.set(CoreAnnotations.DomainAnnotation.class, "test");

    CoreLabel pad = new CoreLabel();
    pad.set(CoreAnnotations.TextAnnotation.class, "*PAD*");
    pad.set(CoreAnnotations.ShapeAnnotation.class, "*PAD*");

    List<CoreLabel> paddedList = Arrays.asList(pad, pad, token, pad, pad);
    PaddedList<CoreLabel> padded = new PaddedList<>(paddedList, pad);

    Collection<String> result = factory.getCliqueFeatures(padded, 2, Clique.cliqueC);

    assertNotNull(result);
    assertTrue(result.size() > 0);
    assertTrue(result.stream().anyMatch(f -> f.contains("TYPE")));
  }
@Test
  public void testGetCliqueFeatures_withCliqueCpC_sequenceFeatures() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.usePrev = true;
    flags.useSequences = true;
    flags.usePrevSequences = true;
    factory.init(flags);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "class");
    token.set(CoreAnnotations.ShapeAnnotation.class, "xxxx");
    token.set(CoreAnnotations.DomainAnnotation.class, "code");

    CoreLabel prev = new CoreLabel();
    prev.set(CoreAnnotations.TextAnnotation.class, "public");
    prev.set(CoreAnnotations.ShapeAnnotation.class, "xxxx");

    CoreLabel pad = new CoreLabel();
    pad.set(CoreAnnotations.TextAnnotation.class, "*PAD*");
    pad.set(CoreAnnotations.ShapeAnnotation.class, "*PAD*");

    List<CoreLabel> paddedList = Arrays.asList(pad, pad, prev, token, pad);
    PaddedList<CoreLabel> padded = new PaddedList<>(paddedList, pad);

    Collection<String> result = factory.getCliqueFeatures(padded, 3, Clique.cliqueCpC);

    assertNotNull(result);
    assertTrue(result.contains("PSEQ"));
  }
@Test
  public void testGetCliqueFeatures_withUnknownClique_shouldThrowException() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    factory.init(flags);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.DomainAnnotation.class, "test");

    CoreLabel pad = new CoreLabel();
    pad.set(CoreAnnotations.TextAnnotation.class, "*PAD*");

    List<CoreLabel> paddedList = Arrays.asList(pad, token, pad);
    PaddedList<CoreLabel> padded = new PaddedList<>(paddedList, pad);

    Clique unknown = new Clique("X-Y-Z");

    try {
      factory.getCliqueFeatures(padded, 1, unknown);
      fail("Expected IllegalArgumentException for unknown clique");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("Unknown clique"));
    }
  }
@Test
  public void testGetCliqueFeatures_withNullDomain_shouldHandleGracefully() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    factory.init(flags);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "token");
    token.set(CoreAnnotations.ShapeAnnotation.class, "xxxx");

    CoreLabel pad = new CoreLabel();
    pad.set(CoreAnnotations.TextAnnotation.class, "*PAD*");
    pad.set(CoreAnnotations.ShapeAnnotation.class, "*PAD*");

    List<CoreLabel> paddedList = Arrays.asList(pad, token, pad);
    PaddedList<CoreLabel> padded = new PaddedList<>(paddedList, pad);

    Collection<String> features = factory.getCliqueFeatures(padded, 1, Clique.cliqueC);

    assertNotNull(features);
    assertTrue(features.size() > 0);
  }
@Test
  public void testFeatureExtraction_withUsePrevNextLemmas_enabled() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.usePrevNextLemmas = true;
    factory.init(flags);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "eats");
    token.set(CoreAnnotations.LemmaAnnotation.class, "eat");
    token.set(CoreAnnotations.DomainAnnotation.class, "test");

    CoreLabel prev = new CoreLabel();
    prev.set(CoreAnnotations.LemmaAnnotation.class, "he");

    CoreLabel next = new CoreLabel();
    next.set(CoreAnnotations.LemmaAnnotation.class, "apple");

    CoreLabel pad = new CoreLabel();
    pad.set(CoreAnnotations.LemmaAnnotation.class, "*PAD*");

    List<CoreLabel> paddedList = Arrays.asList(pad, pad, prev, token, next, pad, pad);
    PaddedList<CoreLabel> padded = new PaddedList<>(paddedList, pad);

    Collection<String> features = factory.getCliqueFeatures(padded, 3, Clique.cliqueC);

    assertTrue(features.stream().anyMatch(f -> f.contains("-PLEM")));
    assertTrue(features.stream().anyMatch(f -> f.contains("-NLEM")));
  }
@Test
  public void testFeatureExtraction_withNgramCaching_disabled() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useInternal = true;
    flags.useWord = true;
    flags.useNGrams = true;
    flags.maxNGramLeng = 3;
    flags.cacheNGrams = false;
    factory.init(flags);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "test");
    token.set(CoreAnnotations.ShapeAnnotation.class, "xxxx");
    token.set(CoreAnnotations.DomainAnnotation.class, "test");

    CoreLabel pad = new CoreLabel();
    pad.set(CoreAnnotations.TextAnnotation.class, "*PAD*");
    pad.set(CoreAnnotations.ShapeAnnotation.class, "*PAD*");

    List<CoreLabel> paddedList = Arrays.asList(pad, token, pad);
    PaddedList<CoreLabel> padded = new PaddedList<>(paddedList, pad);

    Collection<String> features = factory.getCliqueFeatures(padded, 1, Clique.cliqueC);

    assertTrue(features.stream().anyMatch(f -> f.startsWith("#<te")));
  }
@Test
  public void testFeatureExtraction_withUseTitle_enabled() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useTitle = true;
    factory.init(flags);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Dr.");
    token.set(CoreAnnotations.ShapeAnnotation.class, "Xx.");
    token.set(CoreAnnotations.DomainAnnotation.class, "test");

    CoreLabel pad = new CoreLabel();
    pad.set(CoreAnnotations.TextAnnotation.class, "*PAD*");
    pad.set(CoreAnnotations.ShapeAnnotation.class, "*PAD*");

    List<CoreLabel> paddedList = Arrays.asList(pad, pad, token, pad, pad);
    PaddedList<CoreLabel> padded = new PaddedList<>(paddedList, pad);

    Collection<String> features = factory.getCliqueFeatures(padded, 2, Clique.cliqueC);

    assertTrue(features.contains("IS_TITLE"));
  }
@Test
  public void testDescribeDistsimLexicon_withoutInitialization() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    String description = factory.describeDistsimLexicon();
    assertEquals("No distsim lexicon", description);
  }
@Test
  public void testClearMemory_resetsLexicon() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useDistSim = true;
    flags.distSimLexicon = "test-data/dummy-lexicon.txt"; 
    factory.init(flags);
    factory.clearMemory();
    String description = factory.describeDistsimLexicon();
    assertEquals("No distsim lexicon", description);
  } 
}