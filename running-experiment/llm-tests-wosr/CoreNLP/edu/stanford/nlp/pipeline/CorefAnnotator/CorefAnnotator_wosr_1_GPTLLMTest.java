public class CorefAnnotator_wosr_1_GPTLLMTest { 

 @Test
  public void testConstructor_WithDefaultSettings() {
    Properties props = new Properties();
    CorefAnnotator annotator = new CorefAnnotator(props);
    assertNotNull(annotator);
  }
@Test(expected = RuntimeException.class)
  public void testConstructor_HybridEnglishFails() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "hybrid");
    props.setProperty("coref.language", "en");
    new CorefAnnotator(props);
  }
@Test
  public void testRequires_DefaultConfiguration() {
    Properties props = new Properties();
    CorefAnnotator annotator = new CorefAnnotator(props);
    Set<Class<? extends CoreAnnotation>> required = annotator.requires();
    assertTrue(required.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.NamedEntityTagAnnotation.class));
  }
@Test
  public void testRequires_DisableMentionDetection() {
    Properties props = new Properties();
    props.setProperty("coref.useCustomMentionDetection", "true");
    CorefAnnotator annotator = new CorefAnnotator(props);
    Set<Class<? extends CoreAnnotation>> required = annotator.requires();
    assertTrue(required.contains(CorefCoreAnnotations.CorefMentionsAnnotation.class));
  }
@Test
  public void testAnnotate_WithMinimalData() {
    Properties props = new Properties();
    CorefAnnotator annotator = new CorefAnnotator(props);

    Annotation annotation = new Annotation("John loves his dog.");
    CoreLabel token1 = new CoreLabel(); token1.setValue("John"); token1.set(CoreAnnotations.TextAnnotation.class, "John");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    token1.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "PERSON");
    token1.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");

    CoreLabel token2 = new CoreLabel(); token2.setValue("loves"); token2.set(CoreAnnotations.TextAnnotation.class, "loves");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    token2.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "O");
    token2.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "O");

    CoreLabel token3 = new CoreLabel(); token3.setValue("his"); token3.set(CoreAnnotations.TextAnnotation.class, "his");
    token3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    token3.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "O");
    token3.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "O");

    CoreLabel token4 = new CoreLabel(); token4.setValue("dog"); token4.set(CoreAnnotations.TextAnnotation.class, "dog");
    token4.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ANIMAL");
    token4.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "ANIMAL");
    token4.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "ANIMAL");

    CoreLabel token5 = new CoreLabel(); token5.setValue("."); token5.set(CoreAnnotations.TextAnnotation.class, ".");
    token5.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    token5.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "O");
    token5.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "O");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5);

    CoreMap sentence = new Annotation("John loves his dog.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    
    Tree tree = Tree.valueOf("(ROOT (S (NP (NN John)) (VP (VBZ loves) (NP (PRP his) (NN dog))) (. .)))");
    sentence.set(CoreAnnotations.TreeAnnotation.class, tree);

    SemanticGraph sg = new SemanticGraph();
    sentence.set(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class, sg);
    sentence.set(SemanticGraphCoreAnnotations.EnhancedDependenciesAnnotation.class, sg);

    annotation.set(CoreAnnotations.TextAnnotation.class, "John loves his dog.");

    annotator.annotate(annotation);

    Map<Integer, CorefChain> result = annotation.get(CorefCoreAnnotations.CorefChainAnnotation.class);
    assertNotNull(result);
    assertFalse(result.isEmpty());
  }
@Test
  public void testGetLinks_ValidInput() {
    IntTuple tuple1 = new IntTuple(3); tuple1.set(0, 1); tuple1.set(1, 2); tuple1.set(2, 3);
    IntTuple tuple2 = new IntTuple(3); tuple2.set(0, 1); tuple2.set(1, 4); tuple2.set(2, 5);

    CorefMention mention1 = new CorefMention(0, 1, 2, "John", 1, false);
    mention1.position = tuple1;

    CorefMention mention2 = new CorefMention(0, 1, 4, "he", 1, false);
    mention2.position = tuple2;

    CorefChain chain = new CorefChain(1, Arrays.asList(mention1, mention2));
    Map<Integer, CorefChain> map = new HashMap<>();
    map.put(1, chain);

    List<Pair<IntTuple, IntTuple>> links = CorefAnnotator.getLinks(map);

    assertEquals(1, links.size());
    assertTrue(links.get(0).first.equals(tuple2));
    assertTrue(links.get(0).second.equals(tuple1));
  }
@Test
  public void testHasSpeakerAnnotations_False() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "John");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    CoreMap sentence = new Annotation("John");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    Annotation annotation = new Annotation("John");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    boolean result = invokeHasSpeakerAnnotations(annotation);
    assertFalse(result);
  }
@Test
  public void testHasSpeakerAnnotations_True() {
    CoreLabel tokenWithSpeaker = new CoreLabel();
    tokenWithSpeaker.set(CoreAnnotations.SpeakerAnnotation.class, "John");

    CoreMap sentence = new Annotation("Text");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(tokenWithSpeaker));

    Annotation annotation = new Annotation("Text");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    boolean result = invokeHasSpeakerAnnotations(annotation);
    assertTrue(result);
  }
@Test
  public void testRequirementsSatisfied() {
    Properties props = new Properties();
    CorefAnnotator annotator = new CorefAnnotator(props);
    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
    assertTrue(satisfied.contains(CorefCoreAnnotations.CorefChainAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class));
  }
@Test
  public void testExactRequirements_NonDependencyMD() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "RULE");
    CorefAnnotator annotator = new CorefAnnotator(props);
    Collection<String> requirements = annotator.exactRequirements();
    assertNotNull(requirements);
    assertTrue(requirements.contains("parse"));
  }
@Test
  public void testExactRequirements_DependencyMD() {
    Properties props = new Properties();
    props.setProperty("coref.md.type", "dependency");
    CorefAnnotator annotator = new CorefAnnotator(props);
    Collection<String> requirements = annotator.exactRequirements();
    assertNotNull(requirements);
    assertTrue(requirements.contains("depparse"));
  } 
}