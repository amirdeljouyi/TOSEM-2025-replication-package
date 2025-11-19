public class ParserAnnotator_wosr_1_GPTLLMTest { 

 @Test
  public void testConstructorWithModelAndDefaults() {
    ParserGrammar parser = ParserGrammar.loadModel(ParserGrammar.DEFAULT_PARSER_LOC);
    ParserAnnotator annotator = new ParserAnnotator(parser, false, 100);
    assertNotNull(annotator);
  }
@Test
  public void testConstructorWithPropsWithoutTreemap() {
    Properties props = new Properties();
    props.setProperty("parse.model", ParserGrammar.DEFAULT_PARSER_LOC);
    props.setProperty("parse.debug", "true");
    props.setProperty("parse.maxlen", "50");
    props.setProperty("parse.buildgraphs", "true");
    props.setProperty("parse.nthreads", "1");
    props.setProperty("parse.keepPunct", "false");
    props.setProperty("parse.flags", "-retainTmpSubcategories");
    props.setProperty("parse.binaryTrees", "true");

    ParserAnnotator annotator = new ParserAnnotator("parse", props);
    assertNotNull(annotator);
  }
@Test
  public void testSignatureIncludesAllKeysProperly() {
    Properties props = new Properties();
    props.setProperty("parse.model", "edu/model/path");
    props.setProperty("parse.debug", "true");
    props.setProperty("parse.flags", "-foo -bar");
    props.setProperty("parse.maxlen", "25");
    props.setProperty("parse.maxheight", "50");
    props.setProperty("parse.treemap", "edu.SomeTreeMapper");
    props.setProperty("parse.maxtime", "5000");
    props.setProperty("parse.originalDependencies", "true");
    props.setProperty("parse.buildgraphs", "true");
    props.setProperty("parse.nthreads", "2");
    props.setProperty("nthreads", "4");
    props.setProperty("parse.nosquash", "true");
    props.setProperty("parse.keepPunct", "true");
    props.setProperty("parse.extradependencies", "NONE");
    props.setProperty("parse.binaryTrees", "true");

    String signature = ParserAnnotator.signature("parse", props);

    assertTrue(signature.contains("parse.model:edu/model/path"));
    assertTrue(signature.contains("parse.debug:true"));
    assertTrue(signature.contains("parse.flags:-foo -bar"));
    assertTrue(signature.contains("parse.maxlen:25"));
    assertTrue(signature.contains("parse.maxheight:50"));
    assertTrue(signature.contains("parse.treemap:edu.SomeTreeMapper"));
    assertTrue(signature.contains("parse.maxtime:5000"));
    assertTrue(signature.contains("parse.originalDependencies:true"));
    assertTrue(signature.contains("parse.buildgraphs:true"));
    assertTrue(signature.contains("parse.nthreads:2"));
    assertTrue(signature.contains("parse.nosquash:true"));
    assertTrue(signature.contains("parse.keepPunct:true"));
    assertTrue(signature.contains("parse.binaryTrees:true"));
    assertTrue(signature.contains("parse.extradependencies:none"));
  }
@Test
  public void testConstructorWithInvalidModelThrowsException() {
    Properties props = new Properties();
    try {
      new ParserAnnotator("invalidName", props);
      fail("Expected IllegalArgumentException due to missing model property");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("No model specified"));
    }
  }
@Test
  public void testConvertFlagsToArrayEmpty() {
    String[] flags = invokeConvertFlagsToArray(null);
    assertEquals(0, flags.length);
  }
@Test
  public void testConvertFlagsToArraySingle() {
    String[] flags = invokeConvertFlagsToArray("-flag1");
    assertEquals(1, flags.length);
    assertEquals("-flag1", flags[0]);
  }
@Test
  public void testConvertFlagsToArrayMultiple() {
    String[] flags = invokeConvertFlagsToArray("-a -b -c");
    assertEquals(3, flags.length);
    assertEquals("-a", flags[0]);
    assertEquals("-b", flags[1]);
    assertEquals("-c", flags[2]);
  }
@Test
  public void testDoOneFailedSentenceCreatesXTree() {
    ParserGrammar parser = ParserGrammar.loadModel(ParserGrammar.DEFAULT_PARSER_LOC);
    ParserAnnotator annotator = new ParserAnnotator(parser, false, 10);

    CoreMap sentence = new ArrayCoreMap();
    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token1 = new CoreLabel();
    token1.setWord("This");
    token1.setTag(null);
    tokens.add(token1);
    CoreLabel token2 = new CoreLabel();
    token2.setWord("fails");
    token2.setTag(null);
    tokens.add(token2);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation annotation = new Annotation("This fails.");
    List<CoreMap> sentList = new ArrayList<>();
    sentList.add(sentence);
    annotation.set(SentencesAnnotation.class, sentList);

    annotator.doOneFailedSentence(annotation, sentence);

    Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertNotNull(tree);
    assertEquals("X", tree.value());
    assertEquals(2, tree.children().length);
    assertEquals("This", tree.children()[0].value());
    assertEquals("fails", tree.children()[1].value());
    assertEquals("XX", tokens.get(0).tag());
    assertEquals("XX", tokens.get(1).tag());
  } 
}