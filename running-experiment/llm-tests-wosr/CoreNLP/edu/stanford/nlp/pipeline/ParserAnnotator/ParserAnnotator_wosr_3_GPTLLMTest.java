public class ParserAnnotator_wosr_3_GPTLLMTest { 

 @Test
  public void testConstructorWithParserGrammar() {
    LexicalizedParser lp = LexicalizedParser.loadModel();
    ParserGrammar pg = ParserGrammar.transformGrammar(lp);
    ParserAnnotator annotator = new ParserAnnotator(pg, true, 100);

    assertNotNull(annotator);
  }
@Test
  public void testConstructorWithProperties_DefaultValues() {
    Properties props = new Properties();
    props.setProperty("testParser.model", LexicalizedParser.DEFAULT_PARSER_LOC);
    props.setProperty("testParser.debug", "true");
    ParserAnnotator annotator = new ParserAnnotator("testParser", props);

    assertNotNull(annotator);
  }
@Test(expected = IllegalArgumentException.class)
  public void testConstructorWithProperties_MissingModel() {
    Properties props = new Properties();
    ParserAnnotator annotator = new ParserAnnotator("foo", props);
  }
@Test
  public void testSignature_GeneratesConsistentString() {
    Properties props = new Properties();
    props.setProperty("test.model", "englishPCFG.ser.gz");
    props.setProperty("test.maxlen", "250");
    props.setProperty("test.maxtime", "1000");
    String signature = ParserAnnotator.signature("test", props);

    assertNotNull(signature);
    assertTrue(signature.contains("test.model:englishPCFG.ser.gz"));
    assertTrue(signature.contains("test.maxlen:250"));
    assertTrue(signature.contains("test.maxtime:1000"));
  }
@Test
  public void testRequires_WithTag() {
    LexicalizedParser lp = LexicalizedParser.loadModel();
    ParserGrammar grammar = ParserGrammar.transformGrammar(lp);
    ParserAnnotator annotator = new ParserAnnotator(grammar, true, 100);

    Set<Class<? extends CoreAnnotation>> required = annotator.requires();

    assertNotNull(required);
    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
  }
@Test
  public void testRequirementsSatisfied_BuildGraphsTrueBinaryTreesFalse() {
    LexicalizedParser lp = LexicalizedParser.loadModel();
    ParserGrammar grammar = ParserGrammar.transformGrammar(lp);
    ParserAnnotator parserAnnotator = new ParserAnnotator(grammar, true, 50);

    Set<Class<? extends CoreAnnotation>> satisfied = parserAnnotator.requirementsSatisfied();

    assertNotNull(satisfied);
    assertTrue(satisfied.contains(TreeCoreAnnotations.TreeAnnotation.class));
  }
@Test
  public void testDoOneFailedSentence_SetsTreeAnnotation() {
    LexicalizedParser lp = LexicalizedParser.loadModel();
    ParserGrammar grammar = ParserGrammar.transformGrammar(lp);
    ParserAnnotator annotator = new ParserAnnotator(grammar, false, 10);

    CoreMap sentence = new Annotation("This is a test.");
    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel word1 = new CoreLabel();
    word1.setWord("test");
    tokens.add(word1);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation ann = new Annotation("Doc");
    annotator.doOneFailedSentence(ann, sentence);

    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(tree);
    assertEquals("X", tree.label().value());
  }
@Test
  public void testParseTooLongSentenceTriggersFailureParsing() {
    LexicalizedParser lp = LexicalizedParser.loadModel();
    ParserGrammar grammar = ParserGrammar.transformGrammar(lp);
    ParserAnnotator annotator = new ParserAnnotator(grammar, false, 2);

    CoreMap sentence = new Annotation("This sentence is too long.");
    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel w1 = new CoreLabel();
    w1.setWord("This");
    w1.setTag("DT");

    CoreLabel w2 = new CoreLabel();
    w2.setWord("sentence");
    w2.setTag("NN");

    CoreLabel w3 = new CoreLabel();
    w3.setWord("is");
    w3.setTag("VBZ");

    CoreLabel w4 = new CoreLabel();
    w4.setWord("long");
    w4.setTag("JJ");

    tokens.add(w1);
    tokens.add(w2);
    tokens.add(w3);
    tokens.add(w4);

    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation doc = new Annotation("Some text");
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    doc.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.doOneSentence(doc, sentence);

    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(tree);
    assertEquals("X", tree.label().value());
  }
@Test
  public void testDoOneSentence_CorrectlyParsesShortSentence() {
    LexicalizedParser lp = LexicalizedParser.loadModel();
    ParserGrammar grammar = ParserGrammar.transformGrammar(lp);
    ParserAnnotator annotator = new ParserAnnotator(grammar, false, 10);

    CoreMap sentence = new Annotation("Hello world.");
    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel word1 = new CoreLabel();
    word1.setWord("Hello");
    word1.setTag("UH");

    CoreLabel word2 = new CoreLabel();
    word2.setWord("world");
    word2.setTag("NN");

    tokens.add(word1);
    tokens.add(word2);

    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation ann = new Annotation("Text");
    List<CoreMap> list = new ArrayList<>();
    list.add(sentence);
    ann.set(CoreAnnotations.SentencesAnnotation.class, list);

    annotator.doOneSentence(ann, sentence);

    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(tree);
  }
@Test
  public void testNoSquashSkipsPreParsedSentence() {
    LexicalizedParser lp = LexicalizedParser.loadModel();
    ParserGrammar grammar = ParserGrammar.transformGrammar(lp);

    ParserAnnotator annotator = new ParserAnnotator(grammar, false, 5) {
      @Override
      protected void doOneSentence(Annotation annotation, CoreMap sentence) {
        sentence.set(TreeCoreAnnotations.TreeAnnotation.class, Tree.valueOf("(ROOT X)"));
      }
    };

    CoreLabel token = new CoreLabel();
    token.setWord("Hello");
    token.setTag("UH");
    List<CoreLabel> tokens = Collections.singletonList(token);

    CoreMap sentence = new Annotation("Sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, Tree.valueOf("(ROOT (X Hello))"));

    Annotation ann = new Annotation("Doc");
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    ann.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.doOneSentence(ann, sentence);

    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertEquals("X", tree.firstChild().label().value());
  }
@Test
  public void testFinishSentence_WithTreeMapTransformation() {
    LexicalizedParser lp = LexicalizedParser.loadModel();
    ParserGrammar grammar = ParserGrammar.transformGrammar(lp);

    Function<Tree, Tree> identityMap = t -> {
      Tree copy = t.deepCopy();
      copy.label().setValue("REMAPPED");
      return copy;
    };

    ParserAnnotator annotator = new ParserAnnotator(grammar, false, 10, identityMap);

    CoreMap sentence = new Annotation("Simple");
    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel word1 = new CoreLabel();
    word1.setWord("Go");
    word1.setTag("VB");

    tokens.add(word1);

    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Tree root = Tree.valueOf("(ROOT (VB Go))");
    List<Tree> trees = new ArrayList<>();
    trees.add(root);

    annotator.doOneFailedSentence(new Annotation("dummy"), sentence);

    Tree actual = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(actual);
    
    assertEquals("X", actual.label().value());
  } 
}