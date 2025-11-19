public class POSTaggerAnnotator_wosr_5_GPTLLMTest { 

 @Test
  public void testDefaultConstructorDoesNotThrow() {
    POSTaggerAnnotator annotator = new POSTaggerAnnotator();
    assertNotNull(annotator);
    Set<Class<? extends CoreAnnotations.CoreAnnotation>> required = annotator.requires();
    assertTrue(required.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
  }
@Test
  public void testAnnotateWithValidAnnotation() {
    MaxentTagger dummyTagger = new MaxentTagger() {
      @Override
      public List<TaggedWord> tagSentence(List<? extends edu.stanford.nlp.ling.HasWord> sentence, boolean reuseTags) {
        List<TaggedWord> result = new ArrayList<>();
        for (edu.stanford.nlp.ling.HasWord word : sentence) {
          result.add(new TaggedWord(word.word(), "NN"));
        }
        return result;
      }
    };

    POSTaggerAnnotator annotator = new POSTaggerAnnotator(dummyTagger);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("This");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("works");

    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = Collections.singletonList(sentence);

    edu.stanford.nlp.pipeline.Annotation annotation = new edu.stanford.nlp.pipeline.Annotation("test");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    for (CoreLabel token : tokens) {
      assertEquals("NN", token.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    }
  }
@Test
  public void testAnnotateWithNoSentencesKeyThrows() {
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(new MaxentTagger() {
      @Override
      public List<TaggedWord> tagSentence(List<? extends edu.stanford.nlp.ling.HasWord> sentence, boolean reuseTags) {
        return Collections.emptyList();
      }
    });

    edu.stanford.nlp.pipeline.Annotation annotation = new edu.stanford.nlp.pipeline.Annotation("bad data");

    try {
      annotator.annotate(annotation);
      fail("Expected RuntimeException due to missing sentence key");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("unable to find words/tokens"));
    }
  }
@Test
  public void testTaggingSkipsLongSentence() {
    MaxentTagger dummyTagger = new MaxentTagger() {
      @Override
      public List<TaggedWord> tagSentence(List<? extends edu.stanford.nlp.ling.HasWord> sentence, boolean reuseTags) {
        fail("Should not tag sentence longer than maxSentenceLength");
        return null;
      }
    };

    int maxSentenceLength = 1;
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(dummyTagger, maxSentenceLength, 1);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Too");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("long");

    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = Collections.singletonList(sentence);

    edu.stanford.nlp.pipeline.Annotation annotation = new edu.stanford.nlp.pipeline.Annotation("test");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    assertEquals("X", token1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("X", token2.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testOOMDuringTaggingResultsInXTag() {
    MaxentTagger oomTagger = new MaxentTagger() {
      @Override
      public List<TaggedWord> tagSentence(List<? extends edu.stanford.nlp.ling.HasWord> sentence, boolean reuseTags) {
        throw new OutOfMemoryError("Simulated OOM");
      }
    };

    POSTaggerAnnotator annotator = new POSTaggerAnnotator(oomTagger);

    CoreLabel token = new CoreLabel();
    token.setWord("fail");

    List<CoreLabel> tokens = Collections.singletonList(token);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = Collections.singletonList(sentence);

    edu.stanford.nlp.pipeline.Annotation annotation = new edu.stanford.nlp.pipeline.Annotation("test");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    assertEquals("X", token.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testConstructorWithPropertiesReadsCorrectly() {
    Properties props = new Properties();
    props.setProperty("pos.model", "edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger");
    props.setProperty("pos.verbose", "false");
    props.setProperty("pos.maxlen", "10");
    props.setProperty("pos.nthreads", "2");
    props.setProperty("pos.reuseTags", "true");

    POSTaggerAnnotator annotator = new POSTaggerAnnotator("pos", props);

    assertNotNull(annotator);
    Set<Class<? extends CoreAnnotations.CoreAnnotation>> output = annotator.requirementsSatisfied();
    assertTrue(output.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testRequirementsSatisfiedReturnsCorrectOutput() {
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(false);
    Set<Class<? extends CoreAnnotations.CoreAnnotation>> output = annotator.requirementsSatisfied();
    assertEquals(1, output.size());
    assertTrue(output.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testRequiresReturnsExpectedSet() {
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(false);
    Set<Class<? extends CoreAnnotations.CoreAnnotation>> required = annotator.requires();
    assertTrue(required.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.CharacterOffsetEndAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
  }
@Test
  public void testAnnotatorWithMultipleThreadsProcessesCorrectly() {
    MaxentTagger tagger = new MaxentTagger() {
      @Override
      public List<TaggedWord> tagSentence(List<? extends edu.stanford.nlp.ling.HasWord> sentence, boolean reuseTags) {
        List<TaggedWord> result = new ArrayList<>();
        for (edu.stanford.nlp.ling.HasWord word : sentence) {
          result.add(new TaggedWord(word.word(), "VB"));
        }
        return result;
      }
    };

    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, Integer.MAX_VALUE, 2);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("run");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("fast");

    CoreMap sentence1 = new ArrayCoreMap();
    sentence1.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1));

    CoreMap sentence2 = new ArrayCoreMap();
    sentence2.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token2));

    List<CoreMap> sentences = Arrays.asList(sentence1, sentence2);

    edu.stanford.nlp.pipeline.Annotation annotation = new edu.stanford.nlp.pipeline.Annotation("multi-threaded test");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    assertEquals("VB", token1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("VB", token2.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testReuseTagsPropertyIsApplied() {
    final boolean[] reuseFlag = {false};

    MaxentTagger tagger = new MaxentTagger() {
      @Override
      public List<TaggedWord> tagSentence(List<? extends edu.stanford.nlp.ling.HasWord> sentence, boolean reuseTags) {
        reuseFlag[0] = reuseTags;
        List<TaggedWord> result = new ArrayList<>();
        for (edu.stanford.nlp.ling.HasWord word : sentence) {
          result.add(new TaggedWord(word.word(), "TAG"));
        }
        return result;
      }
    };

    Properties props = new Properties();
    props.setProperty("tagger.model", "dummy");
    props.setProperty("tagger.reuseTags", "true");

    POSTaggerAnnotator annotator = new POSTaggerAnnotator("tagger", props);

    CoreLabel token = new CoreLabel();
    token.setWord("reusethis");
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    List<CoreMap> sentences = Collections.singletonList(sentence);
    edu.stanford.nlp.pipeline.Annotation annotation = new edu.stanford.nlp.pipeline.Annotation("reuse test");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    assertTrue(reuseFlag[0]);
  } 
}