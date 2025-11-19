public class TokensRegexNERAnnotator_wosr_1_GPTLLMTest { 

 @Test
  public void testBasicAnnotationWithTokensRegexPattern() {
    InputAnnotation annotation = new InputAnnotation();
    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("University");
    token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("of");
    token2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "IN");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    CoreLabel token3 = new CoreLabel();
    token3.setWord("California");
    token3.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    token3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    tokens.add(token1);
    tokens.add(token2);
    tokens.add(token3);

    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Properties props = new Properties();
    props.setProperty("regexner.mapping", "src/test/resources/tokensregexner_test1.tab");
    props.setProperty("regexner.ignorecase", "false");
    props.setProperty("regexner.mapping.header", "pattern,ner");

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("regexner", props);
    annotator.annotate(annotation);

    assertEquals("ORGANIZATION", tokens.get(0).get(CoreAnnotations.NamedEntityTagAnnotation.class));
    assertEquals("ORGANIZATION", tokens.get(1).get(CoreAnnotations.NamedEntityTagAnnotation.class));
    assertEquals("ORGANIZATION", tokens.get(2).get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }
@Test
  public void testAnnotationIsNotOverwrittenWithHigherPriorityNER() {
    InputAnnotation annotation = new InputAnnotation();
    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Stanford");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");
    token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");

    tokens.add(token1);

    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Properties props = new Properties();
    props.setProperty("regexner.mapping", "src/test/resources/tokensregexner_test2.tab");
    props.setProperty("regexner.mapping.header", "pattern,ner");
    props.setProperty("regexner.ignorecase", "false");

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("regexner", props);
    annotator.annotate(annotation);

    assertEquals("LOCATION", tokens.get(0).get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }
@Test
  public void testOverwriteOandMISCLabelsWithDefaultBackground() {
    InputAnnotation annotation = new InputAnnotation();
    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Amazon");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");

    tokens.add(token1);

    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Properties props = new Properties();
    props.setProperty("regexner.mapping", "src/test/resources/tokensregexner_test3.tab");
    props.setProperty("regexner.mapping.header", "pattern,ner");
    props.setProperty("regexner.backgroundSymbol", "O,MISC");

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("regexner", props);
    annotator.annotate(annotation);

    assertEquals("COMPANY", tokens.get(0).get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }
@Test
  public void testDoNotOverwriteENTITYIfNotInMyLabels() {
    InputAnnotation annotation = new InputAnnotation();
    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Apple");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ENTITY");
    token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");

    tokens.add(token1);

    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Properties props = new Properties();
    props.setProperty("regexner.mapping", "src/test/resources/tokensregexner_test4.tab");
    props.setProperty("regexner.mapping.header", "pattern,ner");

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("regexner", props);
    annotator.annotate(annotation);

    assertEquals("ENTITY", tokens.get(0).get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }
@Test
  public void testSupportsOverwriteForConfiguredTypes() {
    InputAnnotation annotation = new InputAnnotation();
    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("London");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "CITY");
    token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");

    tokens.add(token1);

    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Properties props = new Properties();
    props.setProperty("regexner.mapping", "src/test/resources/tokensregexner_test5.tab");
    props.setProperty("regexner.mapping.header", "pattern,ner,overwrite");
    props.setProperty("regexner.noDefaultOverwriteLabels", "CITY");
    props.setProperty("regexner.ignorecase", "false");

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("regexner", props);
    annotator.annotate(annotation);

    assertEquals("CAPITAL", tokens.get(0).get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }
@Test
  public void testAnnotationWithExplicitOverwriteList() {
    InputAnnotation annotation = new InputAnnotation();
    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Paris");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "CITY");
    token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");

    tokens.add(token1);

    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Properties props = new Properties();
    props.setProperty("regexner.mapping", "src/test/resources/tokensregexner_test6.tab");
    props.setProperty("regexner.mapping.header", "pattern,ner,overwrite");

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("regexner", props);
    annotator.annotate(annotation);

    assertEquals("CAPITAL", tokens.get(0).get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }
@Test
  public void testCommonWordsAreNotAnnotated() {
    InputAnnotation annotation = new InputAnnotation();
    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("The");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "DT");

    tokens.add(token1);

    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Properties props = new Properties();
    props.setProperty("regexner.mapping", "src/test/resources/tokensregexner_test7.tab");
    props.setProperty("regexner.mapping.header", "pattern,ner");
    props.setProperty("regexner.commonWords", "src/test/resources/commonwords.txt");

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("regexner", props);
    annotator.annotate(annotation);

    assertEquals("O", tokens.get(0).get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }
@Test(expected = RuntimeException.class)
  public void testAnnotationWithoutTokensThrowsException() {
    InputAnnotation annotation = new InputAnnotation();
    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("regexner", new Properties());
    annotator.annotate(annotation);
  } 
}