public class CorefAnnotator_wosr_2_GPTLLMTest { 

 @Test
  public void testConstructorWithEnglishAndHybridThrows() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "hybrid");
    Exception exception = null;
    try {
      new CorefAnnotator(props);
    } catch (RuntimeException e) {
      exception = e;
    }
    assertNotNull(exception);
  }
@Test
  public void testConstructorCreatesAnnotatorWithValidSettings() {
    Properties props = new Properties();
    props.setProperty("coref.language", "en");
    props.setProperty("coref.algorithm", "neural");
    CorefAnnotator annotator = null;
    try {
      annotator = new CorefAnnotator(props);
    } catch (Exception e) {
      fail("Expected constructor to succeed, but it threw: " + e.getMessage());
    }
    assertNotNull(annotator);
  }
@Test
  public void testAnnotateWithMissingSentencesAnnotationDoesNothing() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "neural");
    props.setProperty("coref.language", "en");
    Annotation ann = new Annotation("test");

    CorefAnnotator annotator = new CorefAnnotator(props);
    annotator.annotate(ann);

    assertNull(ann.get(CorefCoreAnnotations.CorefChainAnnotation.class));
  }
@Test
  public void testSetNamedEntityTagGranularityCoarseOverwritesNER() {
    Annotation ann = new Annotation("text");

    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "ORG");
    token1.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "Organization");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "MISC");
    tokens.add(token1);

    ann.set(CoreAnnotations.TokensAnnotation.class, tokens);

    CorefAnnotator annotator = new CorefAnnotator(new Properties());
    try {
      CorefAnnotator.class.getDeclaredMethod("setNamedEntityTagGranularity", Annotation.class, String.class)
              .invoke(null, ann, "coarse");
    } catch (Exception e) {
      fail("Reflection failed: " + e.getMessage());
    }

    assertEquals("ORG", tokens.get(0).get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }
@Test
  public void testGetLinksExtractsSortedLinksCorrectly() {
    CorefMention mention1 = new CorefMention();
    mention1.mentionID = 1;
    mention1.position = new IntTuple(new int[]{1, 2});
    CorefMention mention2 = new CorefMention();
    mention2.mentionID = 2;
    mention2.position = new IntTuple(new int[]{1, 3});

    List<CorefMention> mentions = new ArrayList<>();
    mentions.add(mention1);
    mentions.add(mention2);

    CorefChain chain = new CorefChain(mention1, mentions);
    Map<Integer, CorefChain> map = new HashMap<>();
    map.put(1, chain);

    List<Pair<IntTuple, IntTuple>> links = CorefAnnotator.getLinks(map);

    assertEquals(1, links.size());
    Pair<IntTuple, IntTuple> pair = links.get(0);
    assertEquals(1, pair.first.get(0));
    assertEquals(2, pair.first.get(1));
    assertEquals(1, pair.second.get(0));
    assertEquals(3, pair.second.get(1));
  }
@Test
  public void testFindBestCoreferentEntityMentionFindsLongest() throws Exception {
    Annotation ann = new Annotation("text");
    Map<Integer, Integer> em2cm = new HashMap<>();
    em2cm.put(0, 5);
    ann.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, em2cm);

    Mention m1 = new Mention(5, 0, null, null);
    ann.set(CorefCoreAnnotations.CorefMentionsAnnotation.class, Arrays.asList(m1));

    CorefMention cm1 = new CorefMention();
    cm1.mentionID = 5;
    CorefChain chain = new CorefChain(cm1, Arrays.asList(cm1));
    ann.set(CorefCoreAnnotations.CorefChainAnnotation.class, Collections.singletonMap(100, chain));

    Map<Integer, Integer> cm2em = new HashMap<>();
    cm2em.put(5, 11);
    ann.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, cm2em);

    CoreMap longest = new Annotation("longest entity");
    longest.set(CoreAnnotations.TextAnnotation.class, "longest entity");
    longest.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 11);

    ann.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(longest));

    CoreMap original = new Annotation("short");
    original.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);

    Optional<CoreMap> best = null;
    try {
      best = (Optional<CoreMap>) CorefAnnotator.class
              .getDeclaredMethod("findBestCoreferentEntityMention", CoreMap.class, Annotation.class)
              .invoke(null, original, ann);
    } catch (Exception e) {
      fail("Reflection failed: " + e.getMessage());
    }

    assertTrue(best.isPresent());
    assertEquals("longest entity", best.get().get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testHasSpeakerAnnotationsTrue() {
    CoreLabel label = new CoreLabel();
    label.set(CoreAnnotations.SpeakerAnnotation.class, "Mike");

    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(label));

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation document = new Annotation("text");
    document.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    boolean result = false;
    try {
      result = (boolean) CorefAnnotator.class
              .getDeclaredMethod("hasSpeakerAnnotations", Annotation.class)
              .invoke(null, document);
    } catch (Exception e) {
      fail("Reflection failed: " + e.getMessage());
    }

    assertTrue(result);
  }
@Test
  public void testHasSpeakerAnnotationsFalse() {
    CoreLabel label = new CoreLabel();

    CoreMap sentence = new Annotation("sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(label));

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation document = new Annotation("text");
    document.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    boolean result = true;
    try {
      result = (boolean) CorefAnnotator.class
              .getDeclaredMethod("hasSpeakerAnnotations", Annotation.class)
              .invoke(null, document);
    } catch (Exception e) {
      fail("Reflection failed: " + e.getMessage());
    }

    assertFalse(result);
  }
@Test
  public void testRequirementsAndSatisfiedConsistency() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "neural");
    props.setProperty("coref.language", "en");
    CorefAnnotator annotator = new CorefAnnotator(props);

    Set<Class<? extends CoreAnnotation>> requires = annotator.requires();
    Set<Class<? extends CoreAnnotation>> satisfies = annotator.requirementsSatisfied();

    assertNotNull(requires);
    assertNotNull(satisfies);
    assertTrue(requires.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(satisfies.contains(CorefCoreAnnotations.CorefChainAnnotation.class));
  }
@Test
  public void testExactRequirementsReturnsNonNull() {
    Properties props = new Properties();
    props.setProperty("coref.algorithm", "neural");
    props.setProperty("coref.language", "en");
    CorefAnnotator annotator = new CorefAnnotator(props);

    Collection<String> reqs = annotator.exactRequirements();
    assertNotNull(reqs);
    assertFalse(reqs.isEmpty());
  } 
}