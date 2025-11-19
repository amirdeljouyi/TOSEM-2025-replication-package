public class TokensRegexNERAnnotator_wosr_5_GPTLLMTest { 

 @Test
  public void testSimplePatternAnnotation() throws Exception {
    String mappingFilePath = "testPattern1.tab";
    PrintWriter writer = new PrintWriter(new FileWriter(mappingFilePath));
    writer.println("pattern\tner\toverwrite\tpriority\tgroup");
    writer.println("Stanford\tSCHOOL\tO\t1\t0");
    writer.close();

    Properties props = new Properties();
    props.setProperty("tokenregexner.mapping", mappingFilePath);
    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Stanford");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new edu.stanford.nlp.util.ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("Stanford");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    String ner = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
    Assert.assertEquals("SCHOOL", ner);
  }
@Test
  public void testIgnoreCaseMatch() throws Exception {
    String mappingFilePath = "testIgnoreCase.tab";
    PrintWriter writer = new PrintWriter(new FileWriter(mappingFilePath));
    writer.println("pattern\tner\toverwrite\tpriority\tgroup");
    writer.println("harvard\tSCHOOL\tO\t1\t0");
    writer.close();

    Properties props = new Properties();
    props.setProperty("tokenregexner.mapping", mappingFilePath);
    props.setProperty("tokenregexner.ignorecase", "true");

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Harvard");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new edu.stanford.nlp.util.ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("Harvard");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    String ner = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
    Assert.assertEquals("SCHOOL", ner);
  }
@Test
  public void testPatternDoesNotMatch() throws Exception {
    String mappingFilePath = "testNoMatch.tab";
    PrintWriter writer = new PrintWriter(new FileWriter(mappingFilePath));
    writer.println("pattern\tner\toverwrite\tpriority\tgroup");
    writer.println("Harvard\tSCHOOL\tO\t1\t0");
    writer.close();

    Properties props = new Properties();
    props.setProperty("tokenregexner.mapping", mappingFilePath);

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Yale");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new edu.stanford.nlp.util.ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("Yale");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    String ner = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
    Assert.assertEquals("O", ner);
  }
@Test
  public void testOverwriteRestriction() throws Exception {
    String mappingFilePath = "testOverwrite.tab";
    PrintWriter writer = new PrintWriter(new FileWriter(mappingFilePath));
    writer.println("pattern\tner\toverwrite\tpriority\tgroup");
    writer.println("Berkeley\tUNIVERSITY\tMISC\t1\t0");
    writer.close();

    Properties props = new Properties();
    props.setProperty("tokenregexner.mapping", mappingFilePath);
    props.setProperty("tokenregexner.backgroundSymbol", "MISC");

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Berkeley");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "MISC");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new edu.stanford.nlp.util.ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("Berkeley");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    String ner = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
    Assert.assertEquals("UNIVERSITY", ner);
  }
@Test
  public void testValidPosPatternEnforced() throws Exception {
    String mappingFilePath = "testPos.tab";
    PrintWriter writer = new PrintWriter(new FileWriter(mappingFilePath));
    writer.println("pattern\tner\toverwrite\tpriority\tgroup");
    writer.println("IBM\tORG\tO\t1\t0");
    writer.close();

    Properties props = new Properties();
    props.setProperty("tokenregexner.mapping", mappingFilePath);
    props.setProperty("tokenregexner.validpospattern", "NNP");
    props.setProperty("tokenregexner.posmatchtype", "MATCH_ALL_TOKENS");

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "IBM");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB"); 
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new edu.stanford.nlp.util.ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("IBM");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    String ner = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
    Assert.assertEquals("O", ner); 
  }
@Test
  public void testTokensRegexSyntaxPattern() throws Exception {
    String mappingFilePath = "testSyntax.tab";
    PrintWriter writer = new PrintWriter(new FileWriter(mappingFilePath));
    writer.println("pattern\tner\toverwrite\tpriority\tgroup");
    writer.println("( /The/ /Company/ )\tORG\tO\t1\t0");
    writer.close();

    Properties props = new Properties();
    props.setProperty("tokenregexner.mapping", mappingFilePath);

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "The");
    token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "DT");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "Company");
    token2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);

    CoreMap sentence = new edu.stanford.nlp.util.ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("The Company");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    Assert.assertEquals("ORG", token1.get(CoreAnnotations.NamedEntityTagAnnotation.class));
    Assert.assertEquals("ORG", token2.get(CoreAnnotations.NamedEntityTagAnnotation.class));
  } 
}