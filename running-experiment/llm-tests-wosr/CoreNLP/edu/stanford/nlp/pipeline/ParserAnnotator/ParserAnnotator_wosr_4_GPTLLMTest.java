public class ParserAnnotator_wosr_4_GPTLLMTest { 

 @Test
  public void testConstructorWithParserGrammar() {
    ParserGrammar mockGrammar = ParserGrammar.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    ParserAnnotator annotator = new ParserAnnotator(mockGrammar, true, 100);

    assertNotNull(annotator);
  }
@Test
  public void testConstructorWithParserGrammarAndTreeMap() {
    ParserGrammar mockGrammar = ParserGrammar.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    ParserAnnotator annotator = new ParserAnnotator(mockGrammar, false, 50, tree -> tree);

    assertNotNull(annotator);
  }
@Test
  public void testConstructorWithProperties() {
    Properties props = new Properties();
    props.setProperty("test.model", LexicalizedParser.DEFAULT_PARSER_LOC);
    props.setProperty("test.debug", "true");
    props.setProperty("test.maxlen", "80");
    props.setProperty("test.buildgraphs", "true");

    ParserAnnotator annotator = new ParserAnnotator("test", props);

    assertNotNull(annotator);
  }
@Test(expected = IllegalArgumentException.class)
  public void testConstructorThrowsOnMissingModel() {
    Properties props = new Properties();
    new ParserAnnotator("missing", props);
  }
@Test
  public void testSignatureIncludesExpectedFields() {
    Properties props = new Properties();
    props.setProperty("parsing.model", "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
    props.setProperty("parsing.debug", "true");
    props.setProperty("parsing.flags", "-maxLength 80");
    props.setProperty("parsing.maxlen", "80");
    props.setProperty("parsing.maxheight", "90");
    props.setProperty("parsing.treemap", "some.class.Name");
    props.setProperty("parsing.maxtime", "1500");
    props.setProperty("parsing.originalDependencies", "true");
    props.setProperty("parsing.buildgraphs", "true");
    props.setProperty("parsing.nthreads", "2");
    props.setProperty("parsing.nosquash", "true");
    props.setProperty("parsing.keepPunct", "false");
    props.setProperty("parsing.extradependencies", "ALL");
    props.setProperty("parsing.binaryTrees", "true");

    String signature = ParserAnnotator.signature("parsing", props);

    assertTrue(signature.contains("parsing.model:edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz"));
    assertTrue(signature.contains("parsing.debug:true"));
    assertTrue(signature.contains("parsing.flags:-maxLength 80"));
    assertTrue(signature.contains("parsing.maxlen:80"));
    assertTrue(signature.contains("parsing.maxheight:90"));
    assertTrue(signature.contains("parsing.treemap:some.class.Name"));
    assertTrue(signature.contains("parsing.maxtime:1500"));
    assertTrue(signature.contains("parsing.originalDependencies:true"));
    assertTrue(signature.contains("parsing.buildgraphs:true"));
    assertTrue(signature.contains("parsing.nthreads:2"));
    assertTrue(signature.contains("parsing.nosquash:true"));
    assertTrue(signature.contains("parsing.keepPunct:false"));
    assertTrue(signature.contains("parsing.extradependencies:all"));
    assertTrue(signature.contains("parsing.binaryTrees:true"));
  }
@Test
  public void testDoOneFailedSentenceAddsTreeAnnotation() {
    ParserGrammar mockGrammar = ParserGrammar.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    ParserAnnotator annotator = new ParserAnnotator(mockGrammar, false, 100);

    Annotation document = new Annotation("");
    CoreMap sentence = new ArrayCoreMap();
    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Hello");
    token1.setTag("UH");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("world");
    token2.setTag(null); 

    tokens.add(token1);
    tokens.add(token2);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.doOneFailedSentence(document, sentence);

    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);

    assertNotNull(tree);
    assertEquals("ROOT", tree.label().value());
    assertEquals(tokens.size(), tree.getLeaves().size());
    assertEquals("XX", tokens.get(1).tag());
  }
@Test
  public void testDoOneSentenceSkipsLongSentences() {
    ParserGrammar mockGrammar = ParserGrammar.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    ParserAnnotator annotator = new ParserAnnotator(mockGrammar, true, 1); 

    Annotation document = new Annotation("");
    CoreMap sentence = new ArrayCoreMap();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("This");
    token1.setTag("DT");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("fails");
    token2.setTag("VBZ");

    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.doOneSentence(document, sentence);

    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);

    assertNotNull(tree);
    assertEquals("ROOT", tree.label().value());
  }
@Test
  public void testNoSquashSkipsAnnotatedSentences() {
    ParserGrammar mockGrammar = ParserGrammar.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    ParserAnnotator annotator = new ParserAnnotator(mockGrammar, false, 100) {
      {
        try {
          java.lang.reflect.Field field = ParserAnnotator.class.getDeclaredField("noSquash");
          field.setAccessible(true);
          field.setBoolean(this, true);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    };

    Annotation doc = new Annotation("");
    CoreMap sentence = new ArrayCoreMap();

    Tree tree = Tree.valueOf("(ROOT (X dummy))");
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());

    annotator.doOneSentence(doc, sentence);

    Tree existingTree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);

    assertEquals("X", existingTree.label().value());
  }
@Test
  public void testRequiresReturnsExpectedTags() {
    ParserGrammar mockGrammar = ParserGrammar.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
    ParserAnnotator annotator = new ParserAnnotator(mockGrammar, false, 10);

    Set<Class<? extends CoreAnnotations.CoreAnnotation>> required = annotator.requires();

    assertNotNull(required);
    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
  }
@Test
  public void testRequirementsSatisfiedContentForBuildGraphsBinaryTreesFalse() {
    Properties props = new Properties();
    props.setProperty("parser.model", LexicalizedParser.DEFAULT_PARSER_LOC);
    props.setProperty("parser.buildgraphs", "true");
    props.setProperty("parser.binaryTrees", "false");

    ParserAnnotator annotator = new ParserAnnotator("parser", props);

    Set<Class<? extends CoreAnnotations.CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertTrue(satisfied.contains(TreeCoreAnnotations.TreeAnnotation.class));
    assertFalse(satisfied.contains(TreeCoreAnnotations.BinarizedTreeAnnotation.class));
    assertTrue(satisfied.contains(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class));
    assertTrue(satisfied.contains(SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class));
  }
@Test
  public void testRequirementsSatisfiedContentWhenBuildGraphsFalseSaveBinaryTrue() {
    Properties props = new Properties();
    props.setProperty("parser.model", LexicalizedParser.DEFAULT_PARSER_LOC);
    props.setProperty("parser.buildgraphs", "false");
    props.setProperty("parser.binaryTrees", "true");

    ParserAnnotator annotator = new ParserAnnotator("parser", props);

    Set<Class<? extends CoreAnnotations.CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertTrue(satisfied.contains(TreeCoreAnnotations.TreeAnnotation.class));
    assertTrue(satisfied.contains(TreeCoreAnnotations.BinarizedTreeAnnotation.class));
    assertFalse(satisfied.contains(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class));
  } 
}