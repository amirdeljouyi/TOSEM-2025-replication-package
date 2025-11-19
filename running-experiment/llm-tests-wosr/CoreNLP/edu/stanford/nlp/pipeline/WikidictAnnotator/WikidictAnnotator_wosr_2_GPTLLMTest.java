public class WikidictAnnotator_wosr_2_GPTLLMTest { 

 @Test
  public void testNormalizeTimexWithTimeComponent() {
    String timex = "2023-04-18T10:00:00";
    String result = WikidictAnnotator.normalizeTimex(timex);
    assertEquals("2023-04-18", result);
  }
@Test
  public void testNormalizeTimexWithoutTimeComponent() {
    String timex = "2023-04-18";
    String result = WikidictAnnotator.normalizeTimex(timex);
    assertEquals("2023-04-18", result);
  }
@Test
  public void testNormalizeTimexPresent() {
    String timex = "PRESENT";
    String result = WikidictAnnotator.normalizeTimex(timex);
    assertEquals("PRESENT", result);
  }
@Test
  public void testLinkReturnsEmptyForUnknownEntity() {
    Properties props = new Properties();
    WikidictAnnotator annotator = new WikidictAnnotator("test", props);

    CoreMap mention = new Annotation("NoLink");
    mention.set(CoreAnnotations.TextAnnotation.class, "NoLink");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    Optional<String> result = annotator.link(mention);
    assertFalse(result.isPresent());
  }
@Test
  public void testLinkRecognizesNumber() {
    Properties props = new Properties();
    WikidictAnnotator annotator = new WikidictAnnotator("test", props);

    CoreMap mention = new Annotation("1234");
    mention.set(CoreAnnotations.TextAnnotation.class, "1234");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "NUMBER");

    Optional<String> result = annotator.link(mention);
    assertTrue(result.isPresent());
    assertEquals("1234", result.get());
  }
@Test
  public void testLinkOrdinalNormalization() {
    Properties props = new Properties();
    WikidictAnnotator annotator = new WikidictAnnotator("test", props);

    CoreMap mention = new Annotation("1st");
    mention.set(CoreAnnotations.TextAnnotation.class, "1st");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORDINAL");
    mention.set(CoreAnnotations.NumericValueAnnotation.class, 1);

    Optional<String> result = annotator.link(mention);
    assertTrue(result.isPresent());
    assertEquals("1", result.get());
  }
@Test
  public void testLinkWithDateTimexNormalization() {
    Properties props = new Properties();
    WikidictAnnotator annotator = new WikidictAnnotator("test", props);

    Annotation mention = new Annotation("April 1, 2020");
    mention.set(CoreAnnotations.TextAnnotation.class, "April 1, 2020");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");

    Timex timex = new Timex("t1", "2020-04-01T00:00");
    mention.set(TimeAnnotations.TimexAnnotation.class, timex);

    Optional<String> result = annotator.link(mention);
    assertTrue(result.isPresent());
    assertEquals("2020-04-01", result.get());
  }
@Test
  public void testLinkWithDateTimexPlaceholderValues() {
    Properties props = new Properties();
    WikidictAnnotator annotator = new WikidictAnnotator("test", props);

    Annotation mention = new Annotation("now");
    mention.set(CoreAnnotations.TextAnnotation.class, "now");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");

    Timex timex = new Timex("t0", "PRESENT");
    mention.set(TimeAnnotations.TimexAnnotation.class, timex);

    Optional<String> result = annotator.link(mention);
    assertFalse(result.isPresent());
  }
@Test
  public void testLinkWithSurfaceFormInDictionary() {
    Properties props = new Properties();
    props.setProperty("wikidict", "src/test/resources/fake_wikidict.tsv");
    WikidictAnnotator annotator = new WikidictAnnotator("test", props);

    
    annotator.link(new Annotation("init")); 
    CoreLabel label = new CoreLabel();
    label.set(CoreAnnotations.TextAnnotation.class, "Barack Obama");
    label.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    Annotation mention = new Annotation("Barack Obama");
    mention.set(CoreAnnotations.TextAnnotation.class, "Barack Obama");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    annotator.dictionary.put("Barack Obama", "Barack_Obama"); 

    Optional<String> result = annotator.link(mention);
    assertTrue(result.isPresent());
    assertEquals("Barack_Obama", result.get());
  }
@Test
  public void testLinkWithOriginalTextPreferredOverTextAnnotation() {
    Properties props = new Properties();
    WikidictAnnotator annotator = new WikidictAnnotator("test", props);

    CoreMap mention = new Annotation("not used");
    mention.set(CoreAnnotations.TextAnnotation.class, "some text");
    mention.set(CoreAnnotations.OriginalTextAnnotation.class, "SomeEntity");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    annotator.dictionary.put("SomeEntity", "Some_Wiki_Page");

    Optional<String> result = annotator.link(mention);
    assertTrue(result.isPresent());
    assertEquals("Some_Wiki_Page", result.get());
  }
@Test
  public void testLinkWithCaseInsensitiveLookup() {
    Properties props = new Properties();
    props.setProperty("caseless", "true");
    WikidictAnnotator annotator = new WikidictAnnotator("test", props);

    CoreMap mention = new Annotation("NEW YORK");
    mention.set(CoreAnnotations.TextAnnotation.class, "NEW YORK");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "CITY");

    annotator.dictionary.put("new york", "New_York_City");

    Optional<String> result = annotator.link(mention);
    assertTrue(result.isPresent());
    assertEquals("New_York_City", result.get());
  }
@Test
  public void testRequirementsSatisfiedReturnsWikipediaEntityClass() {
    Properties props = new Properties();
    WikidictAnnotator annotator = new WikidictAnnotator("test", props);

    Set<Class<? extends CoreAnnotation>> result = annotator.requirementsSatisfied();
    assertEquals(1, result.size());
    assertTrue(result.contains(CoreAnnotations.WikipediaEntityAnnotation.class));
  }
@Test
  public void testRequiresReturnsAllRequiredAnnotations() {
    Properties props = new Properties();
    WikidictAnnotator annotator = new WikidictAnnotator("test", props);

    Set<Class<? extends CoreAnnotation>> required = annotator.requires();
    assertTrue(required.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.OriginalTextAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.MentionsAnnotation.class));
  }
@Test
  public void testDoOneSentenceAnnotatesWikipediaEntity() {
    Properties props = new Properties();
    props.setProperty("wikidict", "ignored");
    WikidictAnnotator annotator = new WikidictAnnotator("test", props);

    annotator.dictionary.put("some entity", "Some_Entity_Link");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "some");
    CoreMap mention = new Annotation("some entity");
    mention.set(CoreAnnotations.TextAnnotation.class, "some entity");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORG");
    mention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    CoreMap sentence = new Annotation("");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));

    Annotation doc = new Annotation("");
    annotator.doOneSentence(doc, sentence);

    assertEquals("Some_Entity_Link", token.get(CoreAnnotations.WikipediaEntityAnnotation.class));
    assertEquals("Some_Entity_Link", mention.get(CoreAnnotations.WikipediaEntityAnnotation.class));
  }
@Test
  public void testDoOneFailedSentenceDoesNothing() {
    Properties props = new Properties();
    WikidictAnnotator annotator = new WikidictAnnotator("test", props);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "irrelevant");

    CoreMap sentence = new Annotation("");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    Annotation doc = new Annotation("");
    annotator.doOneFailedSentence(doc, sentence);

    assertNull(token.get(CoreAnnotations.WikipediaEntityAnnotation.class));
  }
@Test
  public void testNThreadsDefaultValue() {
    Properties props = new Properties();
    WikidictAnnotator annotator = new WikidictAnnotator("test", props);
    assertEquals(1, annotator.nThreads());
  }
@Test
  public void testMaxTimeReturnsNegativeOne() {
    Properties props = new Properties();
    WikidictAnnotator annotator = new WikidictAnnotator("test", props);
    assertEquals(-1L, annotator.maxTime());
  } 
}