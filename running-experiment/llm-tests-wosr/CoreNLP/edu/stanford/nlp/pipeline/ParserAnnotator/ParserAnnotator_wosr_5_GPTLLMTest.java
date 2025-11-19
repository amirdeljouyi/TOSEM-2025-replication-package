public class ParserAnnotator_wosr_5_GPTLLMTest { 

 @Test
  public void testConstructorWithVerboseAndMaxSentence() {
    ParserGrammar mockParser = mock(ParserGrammar.class);
    TreebankLanguagePack mockTlp = mock(TreebankLanguagePack.class);
    HeadFinder mockHeadFinder = mock(HeadFinder.class);
    when(mockParser.getTLPParams()).thenReturn(new TLPParams() {
      @Override public TreebankLanguagePack treebankLanguagePack() { return mockTlp; }
      @Override public HeadFinder typedDependencyHeadFinder() { return mockHeadFinder; }
      @Override public void setGenerateOriginalDependencies(boolean flag) {}
      @Override public boolean supportsBasicDependencies() { return true; }
    });
    when(mockTlp.punctuationWordRejectFilter()).thenReturn(t -> false);
    when(mockTlp.grammaticalStructureFactory(any(), any())).thenReturn(mock(GrammaticalStructureFactory.class));

    ParserAnnotator annotator = new ParserAnnotator(mockParser, true, 100);
    assertNotNull(annotator);
  }
@Test
  public void testSignatureGeneratesExpectedFields() {
    Properties properties = new Properties();
    properties.setProperty("annot.model", "englishPCFG.ser.gz");
    properties.setProperty("annot.debug", "true");
    properties.setProperty("annot.flags", "-basic");
    properties.setProperty("annot.maxlen", "120");
    properties.setProperty("annot.maxheight", "200");
    properties.setProperty("annot.treemap", "com.foo.TMap");
    properties.setProperty("annot.maxtime", "1000");
    properties.setProperty("annot.originalDependencies", "true");
    properties.setProperty("annot.buildgraphs", "false");
    properties.setProperty("annot.nthreads", "4");
    properties.setProperty("annot.nosquash", "true");
    properties.setProperty("annot.keepPunct", "false");
    properties.setProperty("annot.extradependencies", "MAXIMAL");
    properties.setProperty("annot.binaryTrees", "true");

    String result = ParserAnnotator.signature("annot", properties);
    assertTrue(result.contains("annot.model:englishPCFG.ser.gz"));
    assertTrue(result.contains("annot.debug:true"));
    assertTrue(result.contains("annot.flags:-basic"));
    assertTrue(result.contains("annot.maxlen:120"));
    assertTrue(result.contains("annot.maxheight:200"));
    assertTrue(result.contains("annot.treemap:com.foo.TMap"));
    assertTrue(result.contains("annot.maxtime:1000"));
    assertTrue(result.contains("annot.originalDependencies:true"));
    assertTrue(result.contains("annot.buildgraphs:false"));
    assertTrue(result.contains("annot.nthreads:4"));
    assertTrue(result.contains("annot.nosquash:true"));
    assertTrue(result.contains("annot.keepPunct:false"));
    assertTrue(result.contains("annot.extradependencies:maximal"));
    assertTrue(result.contains("annot.binaryTrees:true"));
  }
@Test
  public void testDoOneFailedSentenceSetsTreeWithXXPOS() {
    ParserGrammar parser = mock(ParserGrammar.class);
    when(parser.getTLPParams()).thenReturn(mock(TLPParams.class));
    ParserAnnotator annotator = new ParserAnnotator(parser, false, 10);

    CoreLabel token = new CoreLabel();
    token.setWord("fail");
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = mock(CoreMap.class);
    Annotation annotation = mock(Annotation.class);

    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
    doAnswer(invocation -> {
      Tree tree = invocation.getArgument(1);
      assertEquals("X", tree.label().value());
      return null;
    }).when(sentence).set(eq(TreeCoreAnnotations.TreeAnnotation.class), any());

    annotator.doOneFailedSentence(annotation, sentence);
    assertEquals("XX", tokens.get(0).tag());
  }
@Test
  public void testDoOneSentenceSkipsTooLongSentence() {
    ParserGrammar parser = mock(ParserGrammar.class);
    TLPParams tlpParams = mock(TLPParams.class);
    TreebankLanguagePack tlp = mock(TreebankLanguagePack.class);
    GrammaticalStructureFactory gsf = mock(GrammaticalStructureFactory.class);
    HeadFinder hf = mock(HeadFinder.class);

    when(parser.getTLPParams()).thenReturn(tlpParams);
    when(tlpParams.supportsBasicDependencies()).thenReturn(true);
    when(tlpParams.treebankLanguagePack()).thenReturn(tlp);
    when(tlpParams.typedDependencyHeadFinder()).thenReturn(hf);
    when(tlp.grammaticalStructureFactory(any(), any())).thenReturn(gsf);

    ParserAnnotator annotator = new ParserAnnotator(parser, false, 1);

    CoreMap sentence = mock(CoreMap.class);
    Annotation annotation = mock(Annotation.class);
    CoreLabel tok = new CoreLabel();
    tok.setWord("Test");
    tok.setTag("NN");
    List<CoreLabel> tokens = Arrays.asList(tok, tok);

    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
    doAnswer(invocation -> {
      List<Tree> trees = invocation.getArgument(1);
      assertEquals(1, trees.size());
      return null;
    }).when(sentence).set(eq(TreeCoreAnnotations.TreeAnnotation.class), any());

    annotator.doOneSentence(annotation, sentence);
  }
@Test
  public void testDoOneSentenceWithPreAnnotatedTreeAndNoSquash() {
    ParserGrammar parser = mock(ParserGrammar.class);
    TLPParams tlpParams = mock(TLPParams.class);
    TreebankLanguagePack tlp = mock(TreebankLanguagePack.class);
    HeadFinder hf = mock(HeadFinder.class);

    when(parser.getTLPParams()).thenReturn(tlpParams);
    when(tlpParams.supportsBasicDependencies()).thenReturn(true);
    when(tlpParams.treebankLanguagePack()).thenReturn(tlp);
    when(tlpParams.typedDependencyHeadFinder()).thenReturn(hf);
    when(tlp.grammaticalStructureFactory(any(), any())).thenReturn(mock(GrammaticalStructureFactory.class));

    ParserAnnotator annotator = new ParserAnnotator(parser, false, 5);
    CoreMap sentence = mock(CoreMap.class);
    Annotation annotation = mock(Annotation.class);

    Tree preTree = Tree.valueOf("(S (NP I) (VP run))");
    preTree.label().setValue("S");
    when(sentence.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(preTree);

    CoreLabel tok = new CoreLabel();
    tok.setWord("I");
    tok.setTag("PRP");

    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(tok));

    ParserAnnotator spyAnno = spy(annotator);
    doReturn(true).when(spyAnno).noSquash;

    spyAnno.doOneSentence(annotation, sentence);

    verify(sentence, never()).set(eq(TreeCoreAnnotations.TreeAnnotation.class), any());
  }
@Test
  public void testRequirementsSatisfiedDefaultConfigReturnsTreeAnnotation() {
    ParserGrammar parser = mock(ParserGrammar.class);
    TLPParams tlpParams = mock(TLPParams.class);
    when(parser.getTLPParams()).thenReturn(tlpParams);
    when(tlpParams.supportsBasicDependencies()).thenReturn(false);
    ParserAnnotator annotator = new ParserAnnotator(parser, false, 5);

    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertTrue(satisfied.contains(TreeCoreAnnotations.TreeAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.CategoryAnnotation.class));
  }
@Test
  public void testRequiresReturnsPOSWhenParserRequiresTags() {
    ParserGrammar parser = mock(ParserGrammar.class);
    when(parser.requiresTags()).thenReturn(true);
    TLPParams tlpParams = mock(TLPParams.class);
    when(parser.getTLPParams()).thenReturn(tlpParams);
    when(tlpParams.supportsBasicDependencies()).thenReturn(false);

    ParserAnnotator annotator = new ParserAnnotator(parser, false, 5);
    Set<Class<? extends CoreAnnotation>> required = annotator.requires();
    assertTrue(required.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testRequiresReturnsNoPOSWhenParserDoesNotRequireTags() {
    ParserGrammar parser = mock(ParserGrammar.class);
    when(parser.requiresTags()).thenReturn(false);
    TLPParams tlpParams = mock(TLPParams.class);
    when(parser.getTLPParams()).thenReturn(tlpParams);
    when(tlpParams.supportsBasicDependencies()).thenReturn(false);

    ParserAnnotator annotator = new ParserAnnotator(parser, false, 5);
    Set<Class<? extends CoreAnnotation>> required = annotator.requires();
    assertFalse(required.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testLoadModelReturnsParserGrammarInstance() {
    ParserGrammar actual = ParserAnnotator.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz", false, new String[0]);
    assertNotNull(actual);
  }
@Test
  public void testConvertFlagsToArrayHandlesNullAndEmpty() {
    String[] result1 = ParserAnnotator.convertFlagsToArray(null);
    String[] result2 = ParserAnnotator.convertFlagsToArray("");
    String[] result3 = ParserAnnotator.convertFlagsToArray(" -x -y -z ");

    assertEquals(0, result1.length);
    assertEquals(0, result2.length);
    assertArrayEquals(new String[]{"-x", "-y", "-z"}, result3);
  } 
}