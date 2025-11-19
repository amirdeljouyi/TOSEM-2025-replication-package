public class CorefAnnotator_wosr_4_GPTLLMTest { 

 @Test(expected = RuntimeException.class)
  public void testConstructorThrowsForHybridEnglish() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "hybrid");
    props.setProperty("coref.language", "en");
    new CorefAnnotator(props);
  }
@Test
  public void testConstructorValidConfiguration() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "statistical");
    props.setProperty("coref.language", "en");
    CorefAnnotator annotator = new CorefAnnotator(props);
    assertNotNull(annotator);
  }
@Test
  public void testRequiresWithDefaultMentionDetection() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "statistical");
    props.setProperty("coref.useCustomMentionDetection", "false");
    CorefAnnotator annotator = new CorefAnnotator(props);
    Set<Class<? extends CoreAnnotation>> requires = annotator.requires();
    assertTrue(requires.contains(TokensAnnotation.class));
    assertTrue(requires.contains(TreeCoreAnnotations.TreeAnnotation.class));
    assertFalse(requires.contains(CorefCoreAnnotations.CorefMentionsAnnotation.class));
  }
@Test
  public void testRequiresWithCustomMentionDetection() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "statistical");
    props.setProperty("coref.useCustomMentionDetection", "true");
    CorefAnnotator annotator = new CorefAnnotator(props);
    Set<Class<? extends CoreAnnotation>> requires = annotator.requires();
    assertTrue(requires.contains(CorefCoreAnnotations.CorefMentionsAnnotation.class));
  }
@Test
  public void testRequirementsSatisfied() {
    Properties props = new Properties();
    CorefAnnotator annotator = new CorefAnnotator(props);
    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
    assertTrue(satisfied.contains(CorefCoreAnnotations.CorefChainAnnotation.class));
    assertTrue(satisfied.contains(CanonicalEntityMentionIndexAnnotation.class));
  }
@Test
  public void testAnnotateSkipsIfNoSentencesAnnotation() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "statistical");
    Annotation annotation = new Annotation("");
    CorefAnnotator annotator = new CorefAnnotator(props);
    
    annotator.annotate(annotation);
    
    assertNull(annotation.get(CorefCoreAnnotations.CorefChainAnnotation.class));
  }
@Test
  public void testHasSpeakerAnnotationsReturnsTrue() {
    List<CoreMap> sentences = new ArrayList<>();
    CoreMap sentence = new Annotation("He said something.");
    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token = new CoreLabel();
    token.set(SpeakerAnnotation.class, "John");
    tokens.add(token);
    sentence.set(TokensAnnotation.class, tokens);
    sentences.add(sentence);

    Annotation ann = new Annotation("");
    ann.set(SentencesAnnotation.class, sentences);

    boolean result = invokeHasSpeakerAnnotationsViaPublicEntryPoint(ann);
    assertTrue(result);
  }
@Test
  public void testHasSpeakerAnnotationsReturnsFalse() {
    List<CoreMap> sentences = new ArrayList<>();
    CoreMap sentence = new Annotation("Plain sentence.");
    sentence.set(TokensAnnotation.class, Collections.singletonList(new CoreLabel()));
    sentences.add(sentence);

    Annotation ann = new Annotation("");
    ann.set(SentencesAnnotation.class, sentences);

    boolean result = invokeHasSpeakerAnnotationsViaPublicEntryPoint(ann);
    assertFalse(result);
  }
@Test
  public void testGetLinksWithEmptyCorefMap() {
    Map<Integer, CorefChain> corefMap = Collections.emptyMap();
    List<Pair<IntTuple, IntTuple>> result = CorefAnnotator.getLinks(corefMap);
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
@Test
  public void testGetLinksReturnsPairs() {
    IntTuple t1 = new IntTuple(3);
    t1.set(0, 0); t1.set(1, 1); t1.set(2, 2);
    IntTuple t2 = new IntTuple(3);
    t2.set(0, 0); t2.set(1, 1); t2.set(2, 5);

    CorefMention m1 = new CorefMention(1, 1, 1, 1, 1, 1, 1, "he", true, true);
    m1.position = t1;
    CorefMention m2 = new CorefMention(1, 1, 1, 1, 1, 1, 2, "John", true, true);
    m2.position = t2;

    List<CorefMention> mentions = Arrays.asList(m1, m2);

    CorefChain chain = new CorefChain(mentions.get(0), mentions, Collections.emptyMap());
    Map<Integer, CorefChain> map = new HashMap<>();
    map.put(1, chain);

    List<Pair<IntTuple, IntTuple>> result = CorefAnnotator.getLinks(map);
    assertEquals(1, result.size());
    Pair<IntTuple, IntTuple> pair = result.get(0);
    assertEquals(t2, pair.first);
    assertEquals(t1, pair.second);
  } 
}