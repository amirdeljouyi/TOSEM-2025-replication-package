public class ParserAnnotator_wosr_2_GPTLLMTest { 

 @Test
  public void testParserAnnotatorConstructionWithBasicConstructor() {
    ParserGrammar parserGrammar = new DummyParserGrammar(true);
    ParserAnnotator annotator = new ParserAnnotator(parserGrammar, true, 100);
    Assert.assertNotNull(annotator);
  }
@Test
  public void testParserAnnotatorConstructionWithExtendedConstructor() {
    ParserGrammar parserGrammar = new DummyParserGrammar(false);
    Function<Tree, Tree> identityMap = tree -> tree;
    ParserAnnotator annotator = new ParserAnnotator(parserGrammar, false, 20, identityMap);
    Assert.assertNotNull(annotator);
  }
@Test
  public void testDoOneFailedSentenceGeneratesFallbackTree() {
    ParserGrammar parserGrammar = new DummyParserGrammar(true);
    ParserAnnotator annotator = new ParserAnnotator(parserGrammar, false, 10);

    Annotation docAnno = new Annotation("");
    CoreMap sentence = new Annotation();
    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("XYZ");
    token1.setTag(null);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("123");
    token2.setTag(null);

    tokens.add(token1);
    tokens.add(token2);

    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotator.doOneFailedSentence(docAnno, sentence);

    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    Assert.assertNotNull(tree);
    Assert.assertEquals("X", tree.value());
    for (CoreLabel token : tokens) {
      Assert.assertEquals("XX", token.tag());
    }
  }
@Test
  public void testDoOneSentenceSkipsIfNoSquashEnabledAndTreePresent() {
    ParserGrammar parserGrammar = new DummyParserGrammar(true);
    ParserAnnotator annotator = new ParserAnnotator(parserGrammar, true, 100);
    CoreLabel word = new CoreLabel();
    word.setWord("Test");
    word.setTag("NN");

    Tree dummyTree = Tree.valueOf("(ROOT (NN Test))");
    CoreMap sentence = new Annotation();
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, dummyTree);
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(word));

    Annotation doc = new Annotation("test");
    annotator.doOneSentence(doc, sentence);

    Assert.assertEquals(dummyTree, sentence.get(TreeCoreAnnotations.TreeAnnotation.class));
  }
@Test
  public void testDoOneSentenceParsesNormally() {
    ParserGrammar parserGrammar = new DummyParserGrammar(true);
    ParserAnnotator annotator = new ParserAnnotator(parserGrammar, true, 100);

    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token = new CoreLabel();
    token.setWord("Apple");
    token.setTag("NN");
    tokens.add(token);

    CoreMap sentence = new Annotation();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation doc = new Annotation("");
    annotator.doOneSentence(doc, sentence);

    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    Assert.assertNotNull(tree);
  }
@Test
  public void testFinishSentenceAppliesTreeMapIfPresent() {
    ParserGrammar parserGrammar = new DummyParserGrammar(true);
    Function<Tree, Tree> relabelToDummy = tree -> Tree.valueOf("(DUMMY dummy)");
    ParserAnnotator annotator = new ParserAnnotator(parserGrammar, true, 10, relabelToDummy);

    CoreMap sentence = new Annotation();
    Tree tree = Tree.valueOf("(ROOT (NN cats))");

    List<Tree> treeList = new ArrayList<>();
    treeList.add(tree);

    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
    annotator.doOneFailedSentence(new Annotation(""), sentence);

    Tree mappedTree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    Assert.assertNotNull(mappedTree);
  }
@Test
  public void testSignatureGeneratesConsistentString() {
    Properties props = new Properties();
    props.setProperty("parse.flags", "-maxLength 100");
    props.setProperty("parse.model", "edu/test-model.ser.gz");
    props.setProperty("parse.maxlen", "100");
    props.setProperty("parse.maxheight", "50");

    String sig1 = ParserAnnotator.signature("parse", props);
    String sig2 = ParserAnnotator.signature("parse", props);

    Assert.assertEquals(sig1, sig2);
  }
@Test
  public void testDoOneSentenceSkipsIfSentenceExceedsMaxLength() {
    ParserGrammar parserGrammar = new DummyParserGrammar(true);
    ParserAnnotator annotator = new ParserAnnotator(parserGrammar, false, 1);

    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token1 = new CoreLabel();
    token1.setWord("One");
    token1.setTag("CD");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Two");
    token2.setTag("CD");

    tokens.add(token1);
    tokens.add(token2);

    CoreMap sentence = new Annotation();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation doc = new Annotation("");
    annotator.doOneSentence(doc, sentence);

    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    Assert.assertNotNull(tree);
    Assert.assertEquals("X", tree.value());
  }
@Test
  public void testRequirementsSatisfiedIncludesExpectedAnnotations() {
    ParserGrammar parserGrammar = new DummyParserGrammar(true);
    ParserAnnotator annotator = new ParserAnnotator(parserGrammar, true, 100);

    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    Assert.assertTrue(satisfied.contains(TreeCoreAnnotations.TreeAnnotation.class));
    Assert.assertTrue(satisfied.contains(CoreAnnotations.CategoryAnnotation.class));
  }
@Test
  public void testRequiresIncludesTokensAnnotation() {
    ParserGrammar parserGrammar = new DummyParserGrammar(false); 
    ParserAnnotator annotator = new ParserAnnotator(parserGrammar, false, 100);

    Set<Class<? extends CoreAnnotation>> required = annotator.requires();
    Assert.assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
  }
@Test
  public void testExceptionDuringParseFallsBackGracefully() {
    ParserGrammar parserGrammar = new ParserGrammar() {
      @Override
      public ParserQuery parserQuery() {
        return new ParserQuery() {
          @Override
          public boolean parse(List<? extends HasWord> sentence) {
            throw new RuntimeException("Forced failure");
          }

          @Override
          public Tree getBestParse() {
            return null;
          }

          @Override
          public double getBestScore() {
            return 0;
          }
        };
      }
    };

    CoreLabel token = new CoreLabel();
    token.setWord("Error");
    token.setTag("NN");
    List<CoreLabel> tokens = Collections.singletonList(token);
    CoreMap sentence = new Annotation();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    ParserAnnotator annotator = new ParserAnnotator(parserGrammar, false, 100);
    annotator.doOneSentence(new Annotation(""), sentence);

    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    Assert.assertNotNull(tree);
    Assert.assertEquals("X", tree.value());
  } 
}