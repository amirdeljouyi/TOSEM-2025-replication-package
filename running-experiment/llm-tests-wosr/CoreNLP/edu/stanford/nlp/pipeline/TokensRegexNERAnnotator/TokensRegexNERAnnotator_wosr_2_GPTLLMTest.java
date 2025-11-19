public class TokensRegexNERAnnotator_wosr_2_GPTLLMTest { 

 @Test
  public void testAnnotateSingleTokenMatch() throws IOException {
    String mappingFile = "test-ner-single.tab";
    try (FileWriter writer = new FileWriter(mappingFile)) {
      writer.write("pattern\tner\n");
      writer.write("Stanford\tORGANIZATION\n");
    }

    Properties props = new Properties();
    props.setProperty("tokenregexner.mapping", mappingFile);

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Stanford");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    List<CoreLabel> tokens = Collections.singletonList(token);

    Annotation annotation = new Annotation("Stanford");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotator.annotate(annotation);

    assertEquals("ORGANIZATION", token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
    new java.io.File(mappingFile).delete();
  }
@Test
  public void testNoAnnotationOnNonMatch() throws IOException {
    String mappingFile = "test-ner-nomatch.tab";
    try (FileWriter writer = new FileWriter(mappingFile)) {
      writer.write("pattern\tner\n");
      writer.write("Google\tORGANIZATION\n");
    }

    Properties props = new Properties();
    props.setProperty("tokenregexner.mapping", mappingFile);

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Stanford");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    List<CoreLabel> tokens = Collections.singletonList(token);

    Annotation annotation = new Annotation("Stanford");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotator.annotate(annotation);

    assertNull(token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
    new java.io.File(mappingFile).delete();
  }
@Test
  public void testTokenSequenceMatch() throws IOException {
    String mappingFile = "test-ner-sequence.tab";
    try (FileWriter writer = new FileWriter(mappingFile)) {
      writer.write("pattern\tner\n");
      writer.write("Stanford\tUniversity\tSCHOOL\n");
    }

    Properties props = new Properties();
    props.setProperty("tokenregexner.mapping", mappingFile);

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Stanford");
    token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "University");
    token2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");

    List<CoreLabel> tokens = Arrays.asList(token1, token2);

    Annotation annotation = new Annotation("Stanford University");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotator.annotate(annotation);

    assertEquals("SCHOOL", token1.get(CoreAnnotations.NamedEntityTagAnnotation.class));
    assertEquals("SCHOOL", token2.get(CoreAnnotations.NamedEntityTagAnnotation.class));
    new java.io.File(mappingFile).delete();
  }
@Test
  public void testMatchOverNonOverridableTag() throws IOException {
    String mappingFile = "test-ner-overwrite.tab";
    try (FileWriter writer = new FileWriter(mappingFile)) {
      writer.write("pattern\tner\toverwrite\n");
      writer.write("City\tCITY\tSTATE\n");
    }

    Properties props = new Properties();
    props.setProperty("tokenregexner.mapping", mappingFile);

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "City");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "CITY");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

    List<CoreLabel> tokens = Collections.singletonList(token);
    Annotation annotation = new Annotation("City");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotator.annotate(annotation);

    assertEquals("CITY", token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
    new java.io.File(mappingFile).delete();
  }
@Test
  public void testOverwriteIfAllowed() throws IOException {
    String mappingFile = "test-ner-allowoverwrite.tab";
    try (FileWriter writer = new FileWriter(mappingFile)) {
      writer.write("pattern\tner\toverwrite\n");
      writer.write("City\tLOCATION\tCITY\n");
    }

    Properties props = new Properties();
    props.setProperty("tokenregexner.mapping", mappingFile);

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "City");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "CITY");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

    List<CoreLabel> tokens = Collections.singletonList(token);
    Annotation annotation = new Annotation("City");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotator.annotate(annotation);

    assertEquals("LOCATION", token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
    new java.io.File(mappingFile).delete();
  }
@Test(expected = RuntimeException.class)
  public void testInvalidPOSPatternThrowsException() throws IOException {
    String mappingFile = "test-ner-invalid-pos.tab";
    try (FileWriter writer = new FileWriter(mappingFile)) {
      writer.write("pattern\tner\n");
      writer.write(".*\tFOO\n");
    }

    Properties props = new Properties();
    props.setProperty("tokenregexner.mapping", mappingFile);
    props.setProperty("tokenregexner.validpospattern", "*("); 

    new TokensRegexNERAnnotator("tokenregexner", props);
    new java.io.File(mappingFile).delete();
  }
@Test
  public void testAnnotationWithNoSentences() throws IOException {
    String mappingFile = "test-ner-nosentences.tab";
    try (FileWriter writer = new FileWriter(mappingFile)) {
      writer.write("pattern\tner\n");
      writer.write("Hello\tGREETING\n");
    }

    Properties props = new Properties();
    props.setProperty("tokenregexner.mapping", mappingFile);

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Hello");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "UH");

    List<CoreLabel> tokens = Collections.singletonList(token);
    Annotation annotation = new Annotation("Hello");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    assertEquals("GREETING", token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
    new java.io.File(mappingFile).delete();
  }
@Test(expected = RuntimeException.class)
  public void testInvalidGroupThrowsException() throws IOException {
    String mappingFile = "test-ner-badgroup.tab";
    try (FileWriter writer = new FileWriter(mappingFile)) {
      writer.write("pattern\tner\tgroup\n");
      writer.write("( /Hello/ )\tHELLO\t5\n");
    }

    Properties props = new Properties();
    props.setProperty("tokenregexner.mapping", mappingFile);

    new TokensRegexNERAnnotator("tokenregexner", props);
    new java.io.File(mappingFile).delete();
  }
@Test
  public void testIgnoreCommonWord() throws IOException {
    String mappingFile = "test-ner-common.tab";
    String commonFile = "test-common.txt";
    try (FileWriter writer1 = new FileWriter(mappingFile);
         FileWriter writer2 = new FileWriter(commonFile)) {
      writer1.write("pattern\tner\n");
      writer1.write("foo\tLABEL\n");
      writer2.write("foo\n");
    }

    Properties props = new Properties();
    props.setProperty("tokenregexner.mapping", mappingFile);
    props.setProperty("tokenregexner.commonWords", commonFile);
    props.setProperty("tokenregexner.verbose", "true");

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "foo");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

    List<CoreLabel> tokens = Collections.singletonList(token);
    Annotation annotation = new Annotation("foo");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotator.annotate(annotation);

    assertNull(token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
    new java.io.File(mappingFile).delete();
    new java.io.File(commonFile).delete();
  } 
}