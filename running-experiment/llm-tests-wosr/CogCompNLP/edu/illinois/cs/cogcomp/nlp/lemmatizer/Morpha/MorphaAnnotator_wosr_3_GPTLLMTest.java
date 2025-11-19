public class MorphaAnnotator_wosr_3_GPTLLMTest { 

 @Test
  public void testAnnotateSingleSentenceSimpleVerb() {
    Annotation annotation = new Annotation("testing"); 

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "running");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBG");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new Annotation("dummy") {
      private final TypesafeMap map = new TypesafeMap();

      @Override
      public <VALUE> VALUE get(Class<? extends CoreAnnotation<VALUE>> key) {
        if (key == CoreAnnotations.TokensAnnotation.class) {
          return (VALUE) tokens;
        }
        return super.get(key);
      }

      @Override
      public <VALUE> void set(Class<? extends CoreAnnotation<VALUE>> key, VALUE value) {
        map.set(key, value);
      }
    };

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    MorphaAnnotator annotator = new MorphaAnnotator(false);
    annotator.annotate(annotation);

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("run", lemma);
  }
@Test
  public void testAnnotateWithMissingPOSFallbacksToStem() {
    Annotation annotation = new Annotation("stem");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "books");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, ""); 

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new Annotation("dummy") {
      private final TypesafeMap map = new TypesafeMap();

      @Override
      public <VALUE> VALUE get(Class<? extends CoreAnnotation<VALUE>> key) {
        if (key == CoreAnnotations.TokensAnnotation.class) {
          return (VALUE) tokens;
        }
        return super.get(key);
      }

      @Override
      public <VALUE> void set(Class<? extends CoreAnnotation<VALUE>> key, VALUE value) {
        map.set(key, value);
      }
    };

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    MorphaAnnotator annotator = new MorphaAnnotator(false);
    annotator.annotate(annotation);

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("book", lemma);
  }
@Test
  public void testPhrasalVerbHandling() {
    Annotation annotation = new Annotation("phrase");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "take_off");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new Annotation("dummy") {
      private final TypesafeMap map = new TypesafeMap();

      @Override
      public <VALUE> VALUE get(Class<? extends CoreAnnotation<VALUE>> key) {
        if (key == CoreAnnotations.TokensAnnotation.class) {
          return (VALUE) tokens;
        }
        return super.get(key);
      }

      @Override
      public <VALUE> void set(Class<? extends CoreAnnotation<VALUE>> key, VALUE value) {
        map.set(key, value);
      }
    };

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    MorphaAnnotator annotator = new MorphaAnnotator(false);
    annotator.annotate(annotation);

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("take_off", lemma);
  }
@Test
  public void testPhrasalVerbNotInParticlesReturnsNormalLemma() {
    Annotation annotation = new Annotation("phrase");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "get_xyz");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new Annotation("dummy") {
      private final TypesafeMap map = new TypesafeMap();

      @Override
      public <VALUE> VALUE get(Class<? extends CoreAnnotation<VALUE>> key) {
        if (key == CoreAnnotations.TokensAnnotation.class) {
          return (VALUE) tokens;
        }
        return super.get(key);
      }

      @Override
      public <VALUE> void set(Class<? extends CoreAnnotation<VALUE>> key, VALUE value) {
        map.set(key, value);
      }
    };

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    MorphaAnnotator annotator = new MorphaAnnotator(false);
    annotator.annotate(annotation);

    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("get_xyz", lemma);
  }
@Test(expected = RuntimeException.class)
  public void testAnnotateThrowsIfNoSentencesAnnotation() {
    Annotation annotation = new Annotation("empty");
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    annotator.annotate(annotation);
  }
@Test
  public void testEmptySentenceDoesNotFail() {
    Annotation annotation = new Annotation("noop");

    CoreMap sentence = new Annotation("dummy") {
      private final TypesafeMap map = new TypesafeMap();

      @Override
      public <VALUE> VALUE get(Class<? extends CoreAnnotation<VALUE>> key) {
        if (key == CoreAnnotations.TokensAnnotation.class) {
          return (VALUE) new ArrayList<>();
        }
        return super.get(key);
      }

      @Override
      public <VALUE> void set(Class<? extends CoreAnnotation<VALUE>> key, VALUE value) {
        map.set(key, value);
      }
    };

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    MorphaAnnotator annotator = new MorphaAnnotator(false);
    annotator.annotate(annotation);
  }
@Test
  public void testRequirementsIncludeCorrectKeys() {
    MorphaAnnotator annotator = new MorphaAnnotator();
    Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> required = annotator.requires();

    assertTrue(required.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
    assertEquals(4, required.size());
  }
@Test
  public void testRequirementsSatisfiedContainsLemmaAnnotation() {
    MorphaAnnotator annotator = new MorphaAnnotator();
    Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertEquals(1, satisfied.size());
    assertTrue(satisfied.contains(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testMultipleTokensInSentence() {
    Annotation annotation = new Annotation("multi");

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "playing");
    token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBG");

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "games");
    token2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNS");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);

    CoreMap sentence = new Annotation("dummy") {
      private final TypesafeMap map = new TypesafeMap();

      @Override
      public <VALUE> VALUE get(Class<? extends CoreAnnotation<VALUE>> key) {
        if (key == CoreAnnotations.TokensAnnotation.class) {
          return (VALUE) tokens;
        }
        return super.get(key);
      }

      @Override
      public <VALUE> void set(Class<? extends CoreAnnotation<VALUE>> key, VALUE value) {
        map.set(key, value);
      }
    };

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    MorphaAnnotator annotator = new MorphaAnnotator(false);
    annotator.annotate(annotation);

    assertEquals("play", token1.get(CoreAnnotations.LemmaAnnotation.class));
    assertEquals("game", token2.get(CoreAnnotations.LemmaAnnotation.class));
  } 
}