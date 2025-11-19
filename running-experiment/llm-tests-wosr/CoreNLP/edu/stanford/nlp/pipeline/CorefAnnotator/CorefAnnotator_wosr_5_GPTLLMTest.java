public class CorefAnnotator_wosr_5_GPTLLMTest { 

 @Test
  public void testConstructorWithDefaultProperties() {
    Properties props = new Properties();
    CorefAnnotator annotator = new CorefAnnotator(props);
    assertNotNull(annotator);
  }
@Test(expected = RuntimeException.class)
  public void testConstructorThrowsOnUnsupportedHybridEnglish() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "hybrid");
    props.setProperty("coref.language", "en");
    new CorefAnnotator(props);
  }
@Test
  public void testGetLinksSingleChainTwoMentions() {
    CorefMention m1 = new CorefMention(0, new IntTuple(3), "he", "he", "he", 0, 0, 0);
    m1.mentionID = 1;
    CorefMention m2 = new CorefMention(0, new IntTuple(3), "john", "john", "john", 0, 1, 0);
    m2.mentionID = 2;
    CorefChain chain = new CorefChain(1, Arrays.asList(m1, m2));
    Map<Integer, CorefChain> map = new HashMap<>();
    map.put(1, chain);

    List<Pair<IntTuple, IntTuple>> links = CorefAnnotator.getLinks(map);
    assertEquals(1, links.size());
    assertEquals(m2.position, links.get(0).first);
    assertEquals(m1.position, links.get(0).second);
  }
@Test
  public void testGetLinksMultipleChainsMultipleMentions() {
    CorefMention m1 = new CorefMention(0, new IntTuple(3), "she", "she", "she", 0, 0, 0);
    m1.mentionID = 1;
    CorefMention m2 = new CorefMention(0, new IntTuple(3), "mary", "mary", "mary", 0, 1, 0);
    m2.mentionID = 2;

    CorefMention m3 = new CorefMention(1, new IntTuple(3), "bob", "bob", "bob", 1, 2, 0);
    m3.mentionID = 3;
    CorefMention m4 = new CorefMention(1, new IntTuple(3), "he", "he", "he", 1, 3, 0);
    m4.mentionID = 4;

    CorefChain chain1 = new CorefChain(10, Arrays.asList(m1, m2));
    CorefChain chain2 = new CorefChain(20, Arrays.asList(m3, m4));

    Map<Integer, CorefChain> map = new HashMap<>();
    map.put(10, chain1);
    map.put(20, chain2);

    List<Pair<IntTuple, IntTuple>> links = CorefAnnotator.getLinks(map);
    assertEquals(2, links.size());
  }
@Test
  public void testAnnotateSkipsWhenNoSentencesAnnotation() {
    Annotation annotation = new Annotation("test text");
    Properties props = new Properties();
    CorefAnnotator annotator = new CorefAnnotator(props);

    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "PERSON");
    List<CoreLabel> tokens = Collections.singletonList(token);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("Annotator should return early if SentencesAnnotation is missing.");
    }
  }
@Test
  public void testSetNamedEntityTagGranularityFine() {
    Annotation annotation = new Annotation("dummy");
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "DATE");
    List<CoreLabel> tokens = Arrays.asList(token);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    CorefAnnotator annotator = new CorefAnnotator(new Properties());
    Annotation fineAnnotation = new Annotation("fine test");
    fineAnnotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    try {
      CorefAnnotator.setNamedEntityTagGranularity(fineAnnotation, "fine");
      assertEquals("DATE", token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
    } catch (Exception e) {
      fail("Granularity fine should not raise.");
    }
  }
@Test
  public void testSetNamedEntityTagGranularityCoarse() {
    Annotation annotation = new Annotation("dummy");
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "LOCATION");
    List<CoreLabel> tokens = Arrays.asList(token);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    CorefAnnotator.setNamedEntityTagGranularity(annotation, "coarse");
    assertEquals("LOCATION", token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }
@Test
  public void testRequiresDefaultDependency() {
    Properties props = new Properties();
    CorefAnnotator annotator = new CorefAnnotator(props);
    Set<Class<? extends CoreAnnotation>> requirements = annotator.requires();
    assertTrue(requirements.contains(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testRequiresNoMentionDetection() {
    Properties props = new Properties();
    props.setProperty("coref.useCustomMentionDetection", "true");
    CorefAnnotator annotator = new CorefAnnotator(props);
    Set<Class<? extends CoreAnnotation>> requirements = annotator.requires();
    assertTrue(requirements.contains(CorefCoreAnnotations.CorefMentionsAnnotation.class));
  }
@Test
  public void testRequirementsSatisfied() {
    Properties props = new Properties();
    CorefAnnotator annotator = new CorefAnnotator(props);
    Set<Class<? extends CoreAnnotation>> output = annotator.requirementsSatisfied();
    assertTrue(output.contains(CorefCoreAnnotations.CorefChainAnnotation.class));
    assertTrue(output.contains(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class));
  }
@Test
  public void testExactRequirementsDefault() {
    Properties props = new Properties();
    CorefAnnotator annotator = new CorefAnnotator(props);
    Collection<String> result = annotator.exactRequirements();
    assertNotNull(result);
    assertTrue(result.contains("tokenize"));
  } 
}