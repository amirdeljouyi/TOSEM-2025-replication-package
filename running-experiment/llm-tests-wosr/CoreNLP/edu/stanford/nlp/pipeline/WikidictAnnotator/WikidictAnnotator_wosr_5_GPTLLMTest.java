public class WikidictAnnotator_wosr_5_GPTLLMTest { 

 @Test
  public void testNormalizeTimexRegularDate() {
    String input = "2020-01-01T12:00";
    String expected = "2020-01-01";
    String actual = WikidictAnnotator.normalizeTimex(input);
    assertEquals(expected, actual);
  }
@Test
  public void testNormalizeTimexPresent() {
    String input = "PRESENT";
    String expected = "PRESENT";
    String actual = WikidictAnnotator.normalizeTimex(input);
    assertEquals(expected, actual);
  }
@Test
  public void testLinkWithKnownEntity() throws Exception {
    String tempDictPath = "temp_dict.tsv";
    PrintWriter writer = new PrintWriter(new FileWriter(tempDictPath));
    writer.println("Barack Obama\tBarack_Obama\t1.0");
    writer.close();

    Properties props = new Properties();
    props.setProperty("wikidict", tempDictPath);

    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

    CoreMap mention = new CoreLabel();
    mention.set(CoreAnnotations.OriginalTextAnnotation.class, "Barack Obama");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    Optional<String> result = annotator.link(mention);
    assertTrue(result.isPresent());
    assertEquals("Barack_Obama", result.get());

    new java.io.File(tempDictPath).delete();
  }
@Test
  public void testLinkWithCaselessMatch() throws Exception {
    String tempDictPath = "temp_dict.tsv";
    PrintWriter writer = new PrintWriter(new FileWriter(tempDictPath));
    writer.println("barack obama\tBarack_Obama\t1.0");
    writer.close();

    Properties props = new Properties();
    props.setProperty("wikidict", tempDictPath);
    props.setProperty("caseless", "true");

    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

    CoreMap mention = new CoreLabel();
    mention.set(CoreAnnotations.OriginalTextAnnotation.class, "Barack Obama");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    Optional<String> result = annotator.link(mention);
    assertTrue(result.isPresent());
    assertEquals("Barack_Obama", result.get());

    new java.io.File(tempDictPath).delete();
  }
@Test
  public void testLinkWithScoreBelowThreshold() throws Exception {
    String tempDictPath = "temp_dict.tsv";
    PrintWriter writer = new PrintWriter(new FileWriter(tempDictPath));
    writer.println("Barack Obama\tBarack_Obama\t0.1");
    writer.close();

    Properties props = new Properties();
    props.setProperty("wikidict", tempDictPath);
    props.setProperty("threshold", "0.2");

    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

    CoreMap mention = new CoreLabel();
    mention.set(CoreAnnotations.OriginalTextAnnotation.class, "Barack Obama");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    Optional<String> result = annotator.link(mention);
    assertFalse(result.isPresent());

    new java.io.File(tempDictPath).delete();
  }
@Test
  public void testLinkWithDateNormalization() throws Exception {
    Properties props = new Properties();
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

    CoreMap mention = new CoreLabel();
    mention.set(CoreAnnotations.TextAnnotation.class, "yesterday");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");

    Timex timex = new Timex("2020-01-01T00:00");
    mention.set(TimeAnnotations.TimexAnnotation.class, timex);

    Optional<String> result = annotator.link(mention);
    assertTrue(result.isPresent());
    assertEquals("2020-01-01", result.get());
  }
@Test
  public void testLinkWithTimexPresentKeepsEmpty() throws Exception {
    Properties props = new Properties();
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

    CoreMap mention = new CoreLabel();
    mention.set(CoreAnnotations.TextAnnotation.class, "now");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");

    Timex timex = new Timex("PRESENT");
    mention.set(TimeAnnotations.TimexAnnotation.class, timex);

    Optional<String> result = annotator.link(mention);
    assertFalse(result.isPresent());
  }
@Test
  public void testLinkOrdinalWithNumericValue() throws Exception {
    Properties props = new Properties();
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

    CoreMap mention = new CoreLabel();
    mention.set(CoreAnnotations.TextAnnotation.class, "first");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORDINAL");
    mention.set(CoreAnnotations.NumericValueAnnotation.class, 1);

    Optional<String> result = annotator.link(mention);
    assertTrue(result.isPresent());
    assertEquals("1", result.get());
  }
@Test
  public void testLinkWithPureNumber() throws Exception {
    Properties props = new Properties();
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

    CoreMap mention = new CoreLabel();
    mention.set(CoreAnnotations.TextAnnotation.class, "12345");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    Optional<String> result = annotator.link(mention);
    assertTrue(result.isPresent());
    assertEquals("12345", result.get());
  }
@Test
  public void testLinkWithUnrecognizedMentionIsEmpty() throws Exception {
    Properties props = new Properties();
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

    CoreMap mention = new CoreLabel();
    mention.set(CoreAnnotations.TextAnnotation.class, "UnknownEntity");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    Optional<String> result = annotator.link(mention);
    assertFalse(result.isPresent());
  }
@Test
  public void testConstructorWithDefaultName() throws Exception {
    String tempDictPath = "temp_dict.tsv";
    PrintWriter writer = new PrintWriter(new FileWriter(tempDictPath));
    writer.println("Tesla\tTesla_Motors\t0.9");
    writer.close();

    Properties props = new Properties();
    props.setProperty("wikidict", tempDictPath);

    WikidictAnnotator annotator = new WikidictAnnotator(props);

    CoreMap mention = new CoreLabel();
    mention.set(CoreAnnotations.OriginalTextAnnotation.class, "Tesla");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");

    Optional<String> result = annotator.link(mention);
    assertTrue(result.isPresent());
    assertEquals("Tesla_Motors", result.get());

    new java.io.File(tempDictPath).delete();
  }
@Test
  public void testRequirementsSatisfiedNotNull() {
    Properties props = new Properties();
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);
    assertNotNull(annotator.requirementsSatisfied());
    assertTrue(annotator.requirementsSatisfied().contains(CoreAnnotations.WikipediaEntityAnnotation.class));
  }
@Test
  public void testRequiresNotEmpty() {
    Properties props = new Properties();
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);
    Set<Class<? extends CoreAnnotations>> required = annotator.requires();
    assertNotNull(required);
    assertTrue(required.contains(CoreAnnotations.MentionsAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testNThreadsDefault() {
    Properties props = new Properties();
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);
    assertEquals(1, annotator.nThreads());
  }
@Test
  public void testMaxTimeAlwaysNegative() {
    Properties props = new Properties();
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);
    assertEquals(-1L, annotator.maxTime());
  } 
}