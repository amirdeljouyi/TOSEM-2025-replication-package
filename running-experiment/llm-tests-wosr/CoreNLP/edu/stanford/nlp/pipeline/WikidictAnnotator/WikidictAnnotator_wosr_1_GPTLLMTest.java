public class WikidictAnnotator_wosr_1_GPTLLMTest { 

 @Test
  public void testNormalizeTimexWithTime() {
    String result = WikidictAnnotator.normalizeTimex("2024-05-01T20:40");
    assertEquals("2024-05-01", result);
  }
@Test
  public void testNormalizeTimexWithoutTime() {
    String result = WikidictAnnotator.normalizeTimex("2024-01-01");
    assertEquals("2024-01-01", result);
  }
@Test
  public void testNormalizeTimexPresent() {
    String result = WikidictAnnotator.normalizeTimex("PRESENT");
    assertEquals("PRESENT", result);
  }
@Test
  public void testLinkWithDateTimex() {
    Properties props = new Properties();
    WikidictAnnotator annotator = new WikidictAnnotator(props);

    CoreMap mention = new TypesafeMap<>();
    mention.set(CoreAnnotations.TextAnnotation.class, "April 25, 2024");
    mention.set(CoreAnnotations.OriginalTextAnnotation.class, "April 25, 2024");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");

    
    Timex timex = new Timex("2024-04-25");
    mention.set(TimeAnnotations.TimexAnnotation.class, timex);

    Optional<String> result = annotator.link(mention);
    assertTrue(result.isPresent());
    assertEquals("2024-04-25", result.get());
  }
@Test
  public void testLinkWithPresentTimexReturnsEmpty() {
    Properties props = new Properties();
    WikidictAnnotator annotator = new WikidictAnnotator(props);

    CoreMap mention = new TypesafeMap<>();
    mention.set(CoreAnnotations.TextAnnotation.class, "present");
    mention.set(CoreAnnotations.OriginalTextAnnotation.class, "PRESENT");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");

    Timex timex = new Timex("PRESENT");
    mention.set(TimeAnnotations.TimexAnnotation.class, timex);

    Optional<String> result = annotator.link(mention);
    assertFalse(result.isPresent());
  }
@Test
  public void testLinkWithOrdinal() {
    Properties props = new Properties();
    WikidictAnnotator annotator = new WikidictAnnotator(props);

    CoreMap mention = new TypesafeMap<>();
    mention.set(CoreAnnotations.TextAnnotation.class, "third");
    mention.set(CoreAnnotations.OriginalTextAnnotation.class, "third");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORDINAL");
    mention.set(CoreAnnotations.NumericValueAnnotation.class, 3);

    Optional<String> result = annotator.link(mention);
    assertTrue(result.isPresent());
    assertEquals("3", result.get());
  }
@Test
  public void testLinkWithNumericValue() {
    Properties props = new Properties();
    WikidictAnnotator annotator = new WikidictAnnotator(props);

    CoreMap mention = new TypesafeMap<>();
    mention.set(CoreAnnotations.TextAnnotation.class, "123.45");
    mention.set(CoreAnnotations.OriginalTextAnnotation.class, "123.45");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "NUMBER");

    Optional<String> result = annotator.link(mention);
    assertTrue(result.isPresent());
    assertEquals("123.45", result.get());
  }
@Test
  public void testLinkWithDictionaryMatch() {
    Properties props = new Properties();
    props.setProperty("wikidict", "src/test/resources/test-wikidict.tsv"); 
    WikidictAnnotator annotator = new WikidictAnnotator(props);

    
    annotator.dictionary.put("Barack Obama", "Barack_Obama");

    CoreMap mention = new TypesafeMap<>();
    mention.set(CoreAnnotations.TextAnnotation.class, "Barack Obama");
    mention.set(CoreAnnotations.OriginalTextAnnotation.class, "Barack Obama");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    Optional<String> result = annotator.link(mention);
    assertTrue(result.isPresent());
    assertEquals("Barack_Obama", result.get());
  }
@Test
  public void testLinkWithCaselessDictionaryMatch() {
    Properties props = new Properties();
    props.setProperty("wikidict", "src/test/resources/test-wikidict.tsv");
    props.setProperty("caseless", "true");
    WikidictAnnotator annotator = new WikidictAnnotator(props);
    annotator.wikidictCaseless = true;
    annotator.dictionary.put("barack obama", "Barack_Obama");

    CoreMap mention = new TypesafeMap<>();
    mention.set(CoreAnnotations.TextAnnotation.class, "Barack Obama");
    mention.set(CoreAnnotations.OriginalTextAnnotation.class, "Barack Obama");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    Optional<String> result = annotator.link(mention);
    assertTrue(result.isPresent());
    assertEquals("Barack_Obama", result.get());
  }
@Test
  public void testLinkWithNoMatchReturnsEmpty() {
    Properties props = new Properties();
    WikidictAnnotator annotator = new WikidictAnnotator(props);

    CoreMap mention = new TypesafeMap<>();
    mention.set(CoreAnnotations.TextAnnotation.class, "UnknownEntity");
    mention.set(CoreAnnotations.OriginalTextAnnotation.class, "UnknownEntity");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    Optional<String> result = annotator.link(mention);
    assertFalse(result.isPresent());
  }
@Test
  public void testLinkWithNoNERReturnsEmpty() {
    Properties props = new Properties();
    WikidictAnnotator annotator = new WikidictAnnotator(props);

    CoreMap mention = new TypesafeMap<>();
    mention.set(CoreAnnotations.TextAnnotation.class, "Mountain");
    mention.set(CoreAnnotations.OriginalTextAnnotation.class, "Mountain");

    Optional<String> result = annotator.link(mention);
    assertFalse(result.isPresent());
  }
@Test
  public void testRequirementsSatisfied() {
    Properties props = new Properties();
    WikidictAnnotator annotator = new WikidictAnnotator(props);
    Set<Class<? extends CoreAnnotations.CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
    assertTrue(satisfied.contains(CoreAnnotations.WikipediaEntityAnnotation.class));
  }
@Test
  public void testRequiresContainsExpectedAnnotations() {
    Properties props = new Properties();
    WikidictAnnotator annotator = new WikidictAnnotator(props);
    Set<Class<? extends CoreAnnotations.CoreAnnotation>> required = annotator.requires();

    assertTrue(required.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.OriginalTextAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.MentionsAnnotation.class));
  }
@Test
  public void testNThreadsDefault() {
    Properties props = new Properties();
    WikidictAnnotator annotator = new WikidictAnnotator(props);
    assertEquals(1, annotator.nThreads());
  }
@Test
  public void testMaxTimeIsMinusOne() {
    Properties props = new Properties();
    WikidictAnnotator annotator = new WikidictAnnotator(props);
    assertEquals(-1L, annotator.maxTime());
  }
@Test
  public void testDoOneFailedSentenceDoesNothing() {
    Properties props = new Properties();
    WikidictAnnotator annotator = new WikidictAnnotator(props);
    CoreMap sentence = new TypesafeMap<>();
    Annotation annotation = new Annotation("");

    try {
      annotator.doOneFailedSentence(annotation, sentence);
    } catch (Exception e) {
      fail("doOneFailedSentence should not throw an exception");
    }
  } 
}