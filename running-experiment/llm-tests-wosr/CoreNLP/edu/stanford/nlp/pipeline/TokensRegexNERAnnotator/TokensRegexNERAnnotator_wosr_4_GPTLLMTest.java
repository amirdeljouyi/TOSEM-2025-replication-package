public class TokensRegexNERAnnotator_wosr_4_GPTLLMTest { 

 @Test
  public void testSimpleRegexPatternAppliesAnnotationToSingleToken() throws IOException {
    String filePath = "testSimpleRegex.tab";
    FileWriter writer = new FileWriter(filePath);
    writer.write("pattern\tner\nStanford\tLOCATION\n");
    writer.close();

    Properties props = new Properties();
    props.setProperty("tokenregexner.mapping", filePath);

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Stanford");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    assertEquals("LOCATION", tokens.get(0).ner());
  }
@Test
  public void testCaseInsensitiveMatchAppliesAnnotation() throws IOException {
    String filePath = "testCaseInsensitive.tab";
    FileWriter writer = new FileWriter(filePath);
    writer.write("pattern\tner\nstanford\tLOCATION\n");
    writer.close();

    Properties props = new Properties();
    props.setProperty("tokenregexner.mapping", filePath);
    props.setProperty("tokenregexner.ignorecase", "true");

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Stanford");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    assertEquals("LOCATION", tokens.get(0).ner());
  }
@Test
  public void testNoAnnotationIfRegexDoesNotMatch() throws IOException {
    String filePath = "testNoMatch.tab";
    FileWriter writer = new FileWriter(filePath);
    writer.write("pattern\tner\nBerkeley\tORGANIZATION\n");
    writer.close();

    Properties props = new Properties();
    props.setProperty("tokenregexner.mapping", filePath);

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Stanford");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    assertNull(tokens.get(0).ner());
  }
@Test
  public void testOverwriteEnabledTypeGetsReplaced() throws IOException {
    String filePath = "testOverwrite.tab";
    FileWriter writer = new FileWriter(filePath);
    writer.write("pattern\toverwrite\tner\nStanford\tORGANIZATION\tSCHOOL\n");
    writer.close();

    Properties props = new Properties();
    props.setProperty("tokenregexner.mapping", filePath);

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Stanford");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    assertEquals("SCHOOL", tokens.get(0).ner());
  }
@Test
  public void testAnnotationIgnoredForCommonWord() throws IOException {
    String regexFile = "testCommonWords_regex.tab";
    FileWriter writer = new FileWriter(regexFile);
    writer.write("pattern\tner\nTest\tENTITY\n");
    writer.close();

    String commonWordsFile = "commonWords.txt";
    FileWriter commonWriter = new FileWriter(commonWordsFile);
    commonWriter.write("Test\n");
    commonWriter.close();

    Properties props = new Properties();
    props.setProperty("tokenregexner.mapping", regexFile);
    props.setProperty("tokenregexner.commonWords", commonWordsFile);

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Test");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    assertNull(tokens.get(0).ner());
  }
@Test
  public void testTokensRegexPatternAnnotation() throws IOException {
    String filePath = "testTokensRegex.tab";
    FileWriter writer = new FileWriter(filePath);
    writer.write("pattern\tner\n( /University/ /of/ /California/ )\tSCHOOL\n");
    writer.close();

    Properties props = new Properties();
    props.setProperty("tokenregexner.mapping", filePath);

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "University");

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "of");

    CoreLabel token3 = new CoreLabel();
    token3.set(CoreAnnotations.TextAnnotation.class, "California");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);

    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    assertEquals("SCHOOL", token1.ner());
    assertEquals("SCHOOL", token2.ner());
    assertEquals("SCHOOL", token3.ner());
  }
@Test
  public void testInvalidTabFileMissingColumnsThrowsException() throws IOException {
    String filePath = "testInvalid.tab";
    FileWriter writer = new FileWriter(filePath);
    writer.write("pattern\nOnlyPatternFieldWithoutNER\n");
    writer.close();

    Properties props = new Properties();
    props.setProperty("tokenregexner.mapping", filePath);

    try {
      new TokensRegexNERAnnotator("tokenregexner", props);
      fail("Expected IllegalArgumentException due to missing columns");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("tab-separated columns"));
    }
  }
@Test
  public void testHeaderLineIgnoredWhenRequested() throws IOException {
    String filePath = "testHeaderTrue.tab";
    FileWriter writer = new FileWriter(filePath);
    writer.write("pattern\toverwrite\tner\nStanford\tORGANIZATION\tUNIVERSITY\n");
    writer.write("Berkeley\tPERSON\tSCHOOL\n");
    writer.close();

    Properties props = new Properties();
    props.setProperty("tokenregexner.mapping", "header=true," + filePath);

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Berkeley");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    assertEquals("SCHOOL", token.ner());
  }
@Test
  public void testDefaultOverwriteBackgroundSymbolO() throws IOException {
    String filePath = "testBackgroundO.tab";
    FileWriter writer = new FileWriter(filePath);
    writer.write("pattern\tner\nTest\tENTITY\n");
    writer.close();

    Properties props = new Properties();
    props.setProperty("tokenregexner.mapping", filePath);

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Test");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    assertEquals("ENTITY", token.ner());
  } 
}