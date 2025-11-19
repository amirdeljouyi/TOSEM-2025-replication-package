public class TokensRegexNERAnnotator_wosr_3_GPTLLMTest { 

 @Test
  public void testSimpleRegexMappingAnnotatesEntity() throws IOException {
    String mapping = "pattern\tner\nStanford\tORG\n";
    String mappingPath = createTempMappingFile(mapping);
    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(mappingPath);

    Annotation annotation = new Annotation("I studied at Stanford .");
    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("I");
    token1.set(CoreAnnotations.TextAnnotation.class, "I");
    token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "PRP");
    tokens.add(token1);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("studied");
    token2.set(CoreAnnotations.TextAnnotation.class, "studied");
    token2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBD");
    tokens.add(token2);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("at");
    token3.set(CoreAnnotations.TextAnnotation.class, "at");
    token3.set(CoreAnnotations.PartOfSpeechAnnotation.class, "IN");
    tokens.add(token3);

    CoreLabel token4 = new CoreLabel();
    token4.setWord("Stanford");
    token4.set(CoreAnnotations.TextAnnotation.class, "Stanford");
    token4.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    tokens.add(token4);

    CoreLabel token5 = new CoreLabel();
    token5.setWord(".");
    token5.set(CoreAnnotations.TextAnnotation.class, ".");
    token5.set(CoreAnnotations.PartOfSpeechAnnotation.class, ".");
    tokens.add(token5);

    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotator.annotate(annotation);

    assertNull(tokens.get(0).ner());
    assertNull(tokens.get(1).ner());
    assertNull(tokens.get(2).ner());
    assertEquals("ORG", tokens.get(3).ner());
    assertNull(tokens.get(4).ner());
  }
@Test
  public void testCaseInsensitiveMatch() throws IOException {
    String mapping = "pattern\tner\nstanford\tORG\n";
    String mappingPath = createTempMappingFile(mapping);
    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(mappingPath, true);

    Annotation annotation = new Annotation("Stanford is a university.");
    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Stanford");
    token1.set(CoreAnnotations.TextAnnotation.class, "Stanford");
    token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    tokens.add(token1);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("is");
    token2.set(CoreAnnotations.TextAnnotation.class, "is");
    token2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBZ");
    tokens.add(token2);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("a");
    token3.set(CoreAnnotations.TextAnnotation.class, "a");
    token3.set(CoreAnnotations.PartOfSpeechAnnotation.class, "DT");
    tokens.add(token3);

    CoreLabel token4 = new CoreLabel();
    token4.setWord("university");
    token4.set(CoreAnnotations.TextAnnotation.class, "university");
    token4.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    tokens.add(token4);

    CoreLabel token5 = new CoreLabel();
    token5.setWord(".");
    token5.set(CoreAnnotations.TextAnnotation.class, ".");
    token5.set(CoreAnnotations.PartOfSpeechAnnotation.class, ".");
    tokens.add(token5);

    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotator.annotate(annotation);

    assertEquals("ORG", tokens.get(0).ner());
    assertNull(tokens.get(1).ner());
    assertNull(tokens.get(2).ner());
    assertNull(tokens.get(3).ner());
    assertNull(tokens.get(4).ner());
  }
@Test
  public void testNoMatchWhenCaseSensitive() throws IOException {
    String mapping = "pattern\tner\nstanford\tORG\n";
    String mappingPath = createTempMappingFile(mapping);
    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(mappingPath, false);

    Annotation annotation = new Annotation("Stanford is great.");
    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Stanford");
    token1.set(CoreAnnotations.TextAnnotation.class, "Stanford");
    token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    tokens.add(token1);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("is");
    token2.set(CoreAnnotations.TextAnnotation.class, "is");
    token2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBZ");
    tokens.add(token2);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("great");
    token3.set(CoreAnnotations.TextAnnotation.class, "great");
    token3.set(CoreAnnotations.PartOfSpeechAnnotation.class, "JJ");
    tokens.add(token3);

    CoreLabel token4 = new CoreLabel();
    token4.setWord(".");
    token4.set(CoreAnnotations.TextAnnotation.class, ".");
    token4.set(CoreAnnotations.PartOfSpeechAnnotation.class, ".");
    tokens.add(token4);

    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotator.annotate(annotation);

    assertNull(tokens.get(0).ner());
    assertNull(tokens.get(1).ner());
    assertNull(tokens.get(2).ner());
    assertNull(tokens.get(3).ner());
  }
@Test
  public void testTokensRegexPattern() throws IOException {
    String mapping = "pattern\tner\n( /University/ /of/ /California/ )\tORG\n";
    String mappingPath = createTempMappingFile(mapping);
    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(mappingPath);

    Annotation annotation = new Annotation("He studied at University of California.");
    List<CoreLabel> tokens = new ArrayList<>();

    String[] words = {"He", "studied", "at", "University", "of", "California", "."};
    String[] posTags = {"PRP", "VBD", "IN", "NNP", "IN", "NNP", "."};
    for (int i = 0; i < words.length; i++) {
      CoreLabel token = new CoreLabel();
      token.setWord(words[i]);
      token.set(CoreAnnotations.TextAnnotation.class, words[i]);
      token.set(CoreAnnotations.PartOfSpeechAnnotation.class, posTags[i]);
      tokens.add(token);
    }

    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotator.annotate(annotation);

    assertNull(tokens.get(0).ner());
    assertNull(tokens.get(1).ner());
    assertNull(tokens.get(2).ner());
    assertEquals("ORG", tokens.get(3).ner());
    assertEquals("ORG", tokens.get(4).ner());
    assertEquals("ORG", tokens.get(5).ner());
    assertNull(tokens.get(6).ner());
  }
@Test
  public void testNoAnnotationForCommonWords() throws IOException {
    String commonWordsFile = java.io.File.createTempFile("commonwords", ".txt").getAbsolutePath();
    try (FileWriter writer = new FileWriter(commonWordsFile)) {
      writer.write("Stanford\n");
    }

    String mapping = "pattern\tner\nStanford\tORG\n";
    String mappingPath = createTempMappingFile(mapping);

    Properties props = new Properties();
    props.setProperty("tokenregexner.mapping", mappingPath);
    props.setProperty("tokenregexner.commonWords", commonWordsFile);
    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);

    Annotation annotation = new Annotation("I visited Stanford yesterday.");
    List<CoreLabel> tokens = new ArrayList<>();

    String[] words = {"I", "visited", "Stanford", "yesterday", "."};
    String[] pos = {"PRP", "VBD", "NNP", "NN", "."};
    for (int i = 0; i < words.length; i++) {
      CoreLabel token = new CoreLabel();
      token.setWord(words[i]);
      token.set(CoreAnnotations.TextAnnotation.class, words[i]);
      token.set(CoreAnnotations.PartOfSpeechAnnotation.class, pos[i]);
      tokens.add(token);
    }

    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotator.annotate(annotation);

    for (CoreLabel token : tokens) {
      assertNull(token.ner());
    }
  }
@Test
  public void testOverwriteWithMISCNER() throws IOException {
    String mapping = "pattern\toverwrite\tner\nStanford\tO\tORG\n";
    String mappingPath = createTempMappingFile(mapping);
    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(mappingPath, false);

    Annotation annotation = new Annotation("I studied at Stanford.");
    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Stanford");
    token1.set(CoreAnnotations.TextAnnotation.class, "Stanford");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "MISC");
    token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    tokens.add(token1);

    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotator.annotate(annotation);

    assertEquals("ORG", tokens.get(0).ner());
  }
@Test(expected = RuntimeException.class)
  public void testMissingTokensThrowsException() throws IOException {
    String mapping = "pattern\tner\nStanford\tORG\n";
    String mappingPath = createTempMappingFile(mapping);
    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(mappingPath);

    Annotation annotation = new Annotation("No tokens here");
    annotator.annotate(annotation);
  }
@Test
  public void testPosPatternMatching() throws IOException {
    String mapping = "pattern\tner\nStanford\tORG\n";
    String mappingPath = createTempMappingFile(mapping);

    Properties props = new Properties();
    props.setProperty("tokenregexner.mapping", mappingPath);
    props.setProperty("tokenregexner.validpospattern", "NNP");
    props.setProperty("tokenregexner.posmatchtype", "MATCH_ALL_TOKENS");
    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);

    Annotation annotation = new Annotation("I saw Stanford.");
    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Stanford");
    token1.set(CoreAnnotations.TextAnnotation.class, "Stanford");
    token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    tokens.add(token1);

    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotator.annotate(annotation);
    assertEquals("ORG", tokens.get(0).ner());
  }
@Test
  public void testPosPatternDoesNotMatch() throws IOException {
    String mapping = "pattern\tner\nStanford\tORG\n";
    String mappingPath = createTempMappingFile(mapping);

    Properties props = new Properties();
    props.setProperty("tokenregexner.mapping", mappingPath);
    props.setProperty("tokenregexner.validpospattern", "CD");
    props.setProperty("tokenregexner.posmatchtype", "MATCH_ALL_TOKENS");
    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);

    Annotation annotation = new Annotation("I study at Stanford.");
    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Stanford");
    token1.set(CoreAnnotations.TextAnnotation.class, "Stanford");
    token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    tokens.add(token1);

    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotator.annotate(annotation);

    assertNull(tokens.get(0).ner());
  } 
}