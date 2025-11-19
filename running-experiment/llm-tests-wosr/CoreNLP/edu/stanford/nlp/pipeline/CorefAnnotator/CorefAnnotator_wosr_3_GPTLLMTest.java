public class CorefAnnotator_wosr_3_GPTLLMTest { 

 @Test(expected = RuntimeException.class)
  public void testUnsupportedHybridAlgorithmForEnglish() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "hybrid");
    props.setProperty("coref.language", "en");
    new CorefAnnotator(props);
  }
@Test
  public void testSupportedAlgorithmAndLanguage() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "statistical");
    props.setProperty("coref.language", "en");
    CorefAnnotator annotator = new CorefAnnotator(props);
    Assert.assertNotNull(annotator);
  }
@Test
  public void testAnnotateWithEmptyAnnotation() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "statistical");
    CorefAnnotator annotator = new CorefAnnotator(props);

    Annotation annotation = new Annotation("");
    annotator.annotate(annotation);

    
    Assert.assertFalse(annotation.containsKey(CorefCoreAnnotations.CorefChainAnnotation.class));
  }
@Test
  public void testAnnotateWithSpeakerAnnotations() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "statistical");
    CorefAnnotator annotator = new CorefAnnotator(props);

    Annotation annotation = new Annotation("Some text");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "John");
    token.set(CoreAnnotations.SpeakerAnnotation.class, "John");

    CoreMap sentence = new Annotation("John");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    Assert.assertTrue(annotation.get(CoreAnnotations.UseMarkedDiscourseAnnotation.class));
  }
@Test
  public void testSetNamedEntityTagGranularityFineToCoarse() {
    Annotation ann = new Annotation("example");
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "PERSON");
    token1.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "INDIVIDUAL");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "");
    ann.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1));

    CorefAnnotator.setNamedEntityTagGranularity(ann, "coarse");

    String tag = ann.get(CoreAnnotations.TokensAnnotation.class).get(0).get(CoreAnnotations.NamedEntityTagAnnotation.class);
    Assert.assertEquals("INDIVIDUAL", tag);
  }
@Test
  public void testSetNamedEntityTagGranularityCoarseToFine() {
    Annotation ann = new Annotation("example");
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "ORG_FINE");
    token1.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "ORG");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "");
    ann.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1));

    CorefAnnotator.setNamedEntityTagGranularity(ann, "fine");

    String tag = ann.get(CoreAnnotations.TokensAnnotation.class).get(0).get(CoreAnnotations.NamedEntityTagAnnotation.class);
    Assert.assertEquals("ORG_FINE", tag);
  }
@Test
  public void testGetLinksEmptyMap() {
    Map<Integer, CorefChain> corefMap = new HashMap<>();
    List<Pair<IntTuple, IntTuple>> links = CorefAnnotator.getLinks(corefMap);
    Assert.assertTrue(links.isEmpty());
  }
@Test
  public void testGetLinksBasic() {
    IntTuple pos1 = new IntTuple(1);
    pos1.set(0, 1);
    IntTuple pos2 = new IntTuple(1);
    pos2.set(0, 2);

    CorefMention cm1 = new CorefMention();
    cm1.mentionID = 101;
    cm1.position = pos1;

    CorefMention cm2 = new CorefMention();
    cm2.mentionID = 102;
    cm2.position = pos2;

    List<CorefMention> mentions = Arrays.asList(cm1, cm2);

    CorefChain chain = new CorefChain(204, mentions);

    Map<Integer, CorefChain> corefMap = new HashMap<>();
    corefMap.put(204, chain);

    List<Pair<IntTuple, IntTuple>> links = CorefAnnotator.getLinks(corefMap);

    Assert.assertEquals(1, links.size());
    Assert.assertEquals(pos2, links.get(0).first);  
    Assert.assertEquals(pos1, links.get(0).second);
  }
@Test
  public void testHasSpeakerAnnotationsTrue() {
    Annotation annotation = new Annotation("sample");
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.SpeakerAnnotation.class, "Bob");

    CoreMap sentence = new Annotation("John");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    boolean result = CorefAnnotator.hasSpeakerAnnotations(annotation);
    Assert.assertTrue(result);
  }
@Test
  public void testHasSpeakerAnnotationsFalse() {
    Annotation annotation = new Annotation("sample");
    CoreLabel token = new CoreLabel();

    CoreMap sentence = new Annotation("Test");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    boolean result = CorefAnnotator.hasSpeakerAnnotations(annotation);
    Assert.assertFalse(result);
  }
@Test
  public void testRequiresWhenDependencyMentionDetection() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "statistical");
    props.setProperty("coref.md.type", "dependency");

    CorefAnnotator annotator = new CorefAnnotator(props);

    Set<Class<? extends CoreAnnotation>> req = annotator.requires();
    Assert.assertFalse(req.contains(TreeCoreAnnotations.TreeAnnotation.class));
  }
@Test
  public void testRequiresWhenNonDependencyMentionDetection() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "statistical");
    props.setProperty("coref.md.type", "regex");

    CorefAnnotator annotator = new CorefAnnotator(props);

    Set<Class<? extends CoreAnnotation>> req = annotator.requires();
    Assert.assertTrue(req.contains(TreeCoreAnnotations.TreeAnnotation.class));
  }
@Test
  public void testRequirementsSatisfied() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "statistical");

    CorefAnnotator annotator = new CorefAnnotator(props);
    Set<Class<? extends CoreAnnotation>> output = annotator.requirementsSatisfied();

    Assert.assertTrue(output.contains(CorefCoreAnnotations.CorefChainAnnotation.class));
    Assert.assertTrue(output.contains(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class));
  } 
}