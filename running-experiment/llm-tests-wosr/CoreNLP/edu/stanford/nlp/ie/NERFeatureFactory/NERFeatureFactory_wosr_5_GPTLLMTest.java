public class NERFeatureFactory_wosr_5_GPTLLMTest { 

 @Test
  public void testGetCliqueFeatures_CliqueC_WithWordAndNGramFeatures() {
    NERFeatureFactory<CoreLabel> featureFactory = new NERFeatureFactory<>();

    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useWord = true;
    flags.useInternal = true;
    flags.useNGrams = true;
    flags.maxNGramLeng = 3;
    flags.lowercaseNGrams = true;
    flags.inputEncoding = "utf-8";

    featureFactory.init(flags);

    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token = new CoreLabel();
    token.setWord("Testing");
    token.set(CoreAnnotations.ShapeAnnotation.class, "Xxxxx");
    token.set(CoreAnnotations.DomainAnnotation.class, "default");
    tokens.add(token);
    PaddedList<CoreLabel> paddedTokens = new PaddedList<>(tokens);

    Collection<String> features = featureFactory.getCliqueFeatures(paddedTokens, 0, Clique.valueOf("C"));

    assertNotNull(features);
    assertTrue(features.stream().anyMatch(f -> f.contains("WORD")));
    assertTrue(features.stream().anyMatch(f -> f.contains("-C")));
  }
@Test
  public void testGetCliqueFeatures_CpCFeaturesWithPrev() {
    NERFeatureFactory<CoreLabel> featureFactory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useInternal = true;
    flags.useExternal = true;
    flags.usePrev = true;
    flags.useSequences = true;
    flags.usePrevSequences = true;
    flags.useWord = true;
    flags.inputEncoding = "utf-8";

    featureFactory.init(flags);

    List<CoreLabel> rawTokens = new ArrayList<>();
    CoreLabel t0 = new CoreLabel();
    t0.setWord("previous");
    t0.set(CoreAnnotations.DomainAnnotation.class, "bio");
    t0.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    rawTokens.add(t0);
    CoreLabel t1 = new CoreLabel();
    t1.setWord("current");
    t1.set(CoreAnnotations.DomainAnnotation.class, "bio");
    t1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    rawTokens.add(t1);
    PaddedList<CoreLabel> padded = new PaddedList<>(rawTokens);

    Collection<String> features = featureFactory.getCliqueFeatures(padded, 1, Clique.valueOf("CpC"));

    assertNotNull(features);
    assertTrue(features.contains("PSEQ"));
  }
@Test
  public void testGetCliqueFeatures_InvalidCliqueThrowsException() {
    NERFeatureFactory<CoreLabel> featureFactory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    featureFactory.init(flags);

    PaddedList<CoreLabel> tokens = new PaddedList<>(Collections.singletonList(new CoreLabel()));
    tokens.get(0).set(CoreAnnotations.DomainAnnotation.class, "domain");

    try {
      featureFactory.getCliqueFeatures(tokens, 0, Clique.valueOf("Unknown"));
      fail("Expected IllegalArgumentException due to unknown clique");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("Unknown clique"));
    }
  }
@Test
  public void testGetCliqueFeatures_CliqueCpCnCWithProperFlags() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();

    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useInternal = true;
    flags.useExternal = true;
    flags.usePrev = true;
    flags.useNext = true;
    flags.useSequences = true;
    flags.usePrevSequences = true;
    flags.useNextSequences = true;
    flags.inputEncoding = "utf-8";

    factory.init(flags);

    List<CoreLabel> list = new ArrayList<>();
    CoreLabel prev = new CoreLabel();
    prev.setWord("Before");
    prev.set(CoreAnnotations.DomainAnnotation.class, "default");
    CoreLabel current = new CoreLabel();
    current.setWord("Current");
    current.set(CoreAnnotations.DomainAnnotation.class, "default");
    list.add(prev);
    list.add(current);
    PaddedList<CoreLabel> pad = new PaddedList<>(list);

    Collection<String> features = factory.getCliqueFeatures(pad, 1, Clique.valueOf("CpCp2C"));

    assertNotNull(features);
    assertTrue(features.contains("PPSEQ"));
  }
@Test
  public void testGetCliqueFeatures_CliqueCpCp2Cp3WithShapeFeatures() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useInternal = true;
    flags.useExternal = true;
    flags.useSequences = true;
    flags.usePrevSequences = true;
    flags.useTaggySequences = true;
    flags.useTags = true;
    flags.maxLeft = 3;
    flags.dontExtendTaggy = false;
    flags.inputEncoding = "utf-8";

    factory.init(flags);

    List<CoreLabel> list = new ArrayList<>();
    CoreLabel t0 = new CoreLabel();
    t0.setWord("A");
    t0.set(CoreAnnotations.DomainAnnotation.class, "test");
    t0.set(CoreAnnotations.PartOfSpeechAnnotation.class, "DT");
    CoreLabel t1 = new CoreLabel();
    t1.setWord("B");
    t1.set(CoreAnnotations.DomainAnnotation.class, "test");
    t1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    CoreLabel t2 = new CoreLabel();
    t2.setWord("C");
    t2.set(CoreAnnotations.DomainAnnotation.class, "test");
    t2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    CoreLabel t3 = new CoreLabel();
    t3.setWord("D");
    t3.set(CoreAnnotations.DomainAnnotation.class, "test");
    t3.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    list.add(t0);
    list.add(t1);
    list.add(t2);
    list.add(t3);

    PaddedList<CoreLabel> padded = new PaddedList<>(list);

    Collection<String> features = factory.getCliqueFeatures(padded, 3, Clique.valueOf("CpCp2Cp3C"));

    assertNotNull(features);
    assertTrue(features.stream().anyMatch(f -> f.contains("TTTS")));
  }
@Test
  public void testDistSimLexiconEmptyDoesNotFailInitialization() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useDistSim = true;
    flags.distSimLexicon = null;

    factory.init(flags);

    assertEquals("No distsim lexicon", factory.describeDistsimLexicon());
  }
@Test
  public void testClearMemoryResetsLexicon() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();

    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useDistSim = true;
    flags.distSimLexicon = null;

    factory.init(flags);

    factory.clearMemory();
    assertEquals("No distsim lexicon", factory.describeDistsimLexicon());
  }
@Test
  public void testFeatureCollectorAddsSuffixAndDomainCopies() {
    Set<String> output = new HashSet<>();
    NERFeatureFactory.FeatureCollector collector = new NERFeatureFactory.FeatureCollector(output);
    collector.setSuffix("C");
    collector.setDomain("myDomain");

    collector.build().append("token").add();

    assertEquals(2, output.size());
    assertTrue(output.contains("token|C"));
    assertTrue(output.contains("token|myDomain-C"));
  } 
}