public class NERFeatureFactory_wosr_2_GPTLLMTest { 

 @Test
  public void testGetCliqueFeatures_basicCurrentClique() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    factory.init(flags);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Stanford");
    token.set(CoreAnnotations.ShapeAnnotation.class, "Xxxxx");
    token.set(CoreAnnotations.DomainAnnotation.class, "domain-x");

    PaddedList<CoreLabel> paddedList = new PaddedList<>(
      Arrays.asList(new CoreLabel(), new CoreLabel(), token, new CoreLabel(), new CoreLabel())
    );

    paddedList.get(0).set(CoreAnnotations.TextAnnotation.class, "PAD");
    paddedList.get(1).set(CoreAnnotations.TextAnnotation.class, "The");
    paddedList.get(3).set(CoreAnnotations.TextAnnotation.class, "University");
    paddedList.get(4).set(CoreAnnotations.TextAnnotation.class, "PAD");
    paddedList.setPad(new CoreLabel());

    Collection<String> features = factory.getCliqueFeatures(paddedList, 2, Clique.valueOf("C"));
    assertNotNull(features);
    assertFalse(features.isEmpty());
  }
@Test
  public void testGetCliqueFeatures_invalidCliqueThrowsException() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    factory.init(flags);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.DomainAnnotation.class, "domain-z");

    PaddedList<CoreLabel> paddedList = new PaddedList<>(Arrays.asList(token));
    paddedList.setPad(new CoreLabel());

    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("Unknown clique");

    factory.getCliqueFeatures(paddedList, 0, new Clique("UNKNOWN_CLIQUE"));
  }
@Test
  public void testGetCliqueFeatures_cpCReturnsFeature() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.usePrev = true;
    factory.init(flags);

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.DomainAnnotation.class, "domain-a");
    token1.set(CoreAnnotations.TextAnnotation.class, "Barack");
    token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    token1.set(CoreAnnotations.ShapeAnnotation.class, "Xxxxx");

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.DomainAnnotation.class, "domain-a");
    token2.set(CoreAnnotations.TextAnnotation.class, "Obama");
    token2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    token2.set(CoreAnnotations.ShapeAnnotation.class, "Xxxxx");

    CoreLabel token3 = new CoreLabel();
    token3.set(CoreAnnotations.DomainAnnotation.class, "domain-a");
    token3.set(CoreAnnotations.TextAnnotation.class, ".");
    token3.set(CoreAnnotations.PartOfSpeechAnnotation.class, ".");
    token3.set(CoreAnnotations.ShapeAnnotation.class, ".");

    PaddedList<CoreLabel> paddedList = new PaddedList<>(Arrays.asList(token1, token2, token3));
    paddedList.setPad(new CoreLabel());

    Collection<String> features = factory.getCliqueFeatures(paddedList, 1, Clique.valueOf("CpC"));
    assertNotNull(features);
    assertFalse(features.isEmpty());
  }
@Test
  public void testGetCliqueFeatures_cp2CNoException() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useParenMatching = true;
    factory.init(flags);

    CoreLabel a = new CoreLabel();
    CoreLabel b = new CoreLabel();
    CoreLabel c = new CoreLabel();
    CoreLabel d = new CoreLabel();

    a.set(CoreAnnotations.DomainAnnotation.class, "d");
    b.set(CoreAnnotations.DomainAnnotation.class, "d");
    c.set(CoreAnnotations.DomainAnnotation.class, "d");
    d.set(CoreAnnotations.DomainAnnotation.class, "d");

    c.set(CoreAnnotations.TextAnnotation.class, ")");
    b.set(CoreAnnotations.TextAnnotation.class, "and");
    a.set(CoreAnnotations.TextAnnotation.class, "(");

    PaddedList<CoreLabel> padded = new PaddedList<>(Arrays.asList(a, b, c, d));
    padded.setPad(new CoreLabel());

    Collection<String> feats = factory.getCliqueFeatures(padded, 2, Clique.valueOf("Cp2C"));
    assertNotNull(feats);
    assertFalse(feats.isEmpty());
  }
@Test
  public void testClearMemoryDoesNotThrow() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.clearMemory();
  }
@Test
  public void testDescribeDistsimLexiconEmpty() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    String result = factory.describeDistsimLexicon();
    assertEquals("No distsim lexicon", result);
  }
@Test
  public void testDescribeDistsimLexiconAfterInitialize() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    factory.init(flags);
    factory.clearMemory(); 
    String description = factory.describeDistsimLexicon();
    assertEquals("No distsim lexicon", description);
  }
@Test
  public void testGetCliqueFeatures_cpCp2CcDoesNotThrow() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    factory.init(flags);

    CoreLabel pad = new CoreLabel();
    pad.set(CoreAnnotations.DomainAnnotation.class, "xx");

    PaddedList<CoreLabel> list = new PaddedList<>(Arrays.asList(
      pad, pad, pad
    ));
    list.setPad(new CoreLabel());

    Collection<String> result = factory.getCliqueFeatures(list, 1, Clique.valueOf("CpCp2C"));
    assertNotNull(result);
  }
@Test
  public void testGetCliqueFeaturesCpCp2Cp3Cp4C() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.maxLeft = 4;
    flags.useLongSequences = true;
    factory.init(flags);

    CoreLabel label = new CoreLabel();
    label.set(CoreAnnotations.TextAnnotation.class, "entity");
    label.set(CoreAnnotations.DomainAnnotation.class, "bio");

    CoreLabel blank = new CoreLabel();
    blank.set(CoreAnnotations.DomainAnnotation.class, "bio");

    PaddedList<CoreLabel> list = new PaddedList<>(
      Arrays.asList(blank, blank, blank, blank, label)
    );
    list.setPad(blank);

    Collection<String> features = factory.getCliqueFeatures(list, 4, Clique.valueOf("CpCp2Cp3Cp4C"));
    assertNotNull(features);
    assertTrue(features.contains("PPPPSEQ"));
  } 
}