public class KBPAnnotator_wosr_2_GPTLLMTest { 

 @Test
  public void testKBPAnnotatorInitializationWithDefaultModelAndPatterns() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.language", "en");

    KBPAnnotator annotator = new KBPAnnotator("kbp", props);

    assertNotNull(annotator);
    assertNotNull(annotator.extractor);
    assertEquals(LanguageInfo.HumanLanguage.ENGLISH,
                 Reflection.getFieldValue(annotator, "kbpLanguage"));
  }
@Test
  public void testKBPAnnotatorInitializationSpanishCorefComponentAdded() {
    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.language", "es");

    KBPAnnotator annotator = new KBPAnnotator("kbp", props);

    assertNotNull(annotator);
    assertNotNull(Reflection.getFieldValue(annotator, "spanishCorefSystem"));
    assertEquals(LanguageInfo.HumanLanguage.SPANISH,
        Reflection.getFieldValue(annotator, "kbpLanguage"));
  }
@Test
  public void testAnnotateWithEmptyDocumentAddsNoKBPTriples() {
    Annotation annotation = new Annotation("");
    List<CoreMap> sentences = new ArrayList<>();
    Annotation.setAnnotations(annotation, CoreAnnotations.SentencesAnnotation.class, sentences);

    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.language", "en");

    KBPAnnotator annotator = new KBPAnnotator("kbp", props);
    annotator.annotate(annotation);

    assertTrue(annotation.get(CoreAnnotations.SentencesAnnotation.class).isEmpty());
  }
@Test
  public void testAnnotateSetsKBPTriplesForShortSentence() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Barack");
    token1.set(CoreAnnotations.TextAnnotation.class, "Barack");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    token1.setIndex(1);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Obama");
    token2.set(CoreAnnotations.TextAnnotation.class, "Obama");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    token2.setIndex(2);

    List<CoreLabel> tokens = Arrays.asList(token1, token2);

    CoreMap mention = new Annotation("Barack Obama");
    mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    mention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    mention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);
    mention.set(CoreAnnotations.TextAnnotation.class, "Barack Obama");
    mention.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    List<CoreMap> mentions = Collections.singletonList(mention);

    CoreMap sentence = new Annotation("Barack Obama");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
    sentence.set(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class, new SemanticGraph());

    List<CoreMap> sentences = Collections.singletonList(sentence);

    Annotation annotation = new Annotation("Barack Obama");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    Properties props = new Properties();
    props.setProperty("kbp.model", "none");
    props.setProperty("kbp.semgrex", "none");
    props.setProperty("kbp.tokensregex", "none");
    props.setProperty("kbp.language", "en");

    KBPAnnotator annotator = new KBPAnnotator("kbp", props);
    annotator.annotate(annotation);

    List<RelationTriple> triples = sentence.get(CoreAnnotations.KBPTriplesAnnotation.class);
    assertNotNull(triples);
    assertTrue(triples.isEmpty()); 
  }
@Test
  public void testRequirementsDeclared() {
    KBPAnnotator annotator = new KBPAnnotator("kbp", new Properties());
    Set<Class<? extends CoreAnnotation>> requires = annotator.requires();
    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertNotNull(requires);
    assertNotNull(satisfied);
    assertTrue(requires.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.KBPTriplesAnnotation.class));
  }
@Test
  public void testCorefChainToKBPMentionsReturnsCorrectLongestMention() {
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);
    token1.set(CoreAnnotations.TextAnnotation.class, "IBM");

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 31);
    token2.set(CoreAnnotations.TextAnnotation.class, "International Business Machines");

    CoreMap shortMention = new Annotation("IBM");
    shortMention.set(CoreAnnotations.TextAnnotation.class, "IBM");
    shortMention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token1));

    CoreMap longMention = new Annotation("International Business Machines");
    longMention.set(CoreAnnotations.TextAnnotation.class, "International Business Machines");
    longMention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token2));

    List<CoreMap> annotationSentences = new ArrayList<>();
    CoreMap sentence = new Annotation("Fake sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));
    annotationSentences.add(sentence);

    Annotation annotation = new Annotation("Text");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, annotationSentences);

    Pair<Integer, Integer> shortSpan = new Pair<>(0, 3);
    Pair<Integer, Integer> longSpan = new Pair<>(10, 31);
    HashMap<Pair<Integer, Integer>, CoreMap> kbpMentions = new HashMap<>();
    kbpMentions.put(shortSpan, shortMention);
    kbpMentions.put(longSpan, longMention);

    CorefChain.CorefMention m1 = new CorefChain.CorefMention(1, 1, 0, 1, 1, 0, "IBM", true);
    CorefChain.CorefMention m2 = new CorefChain.CorefMention(1, 1, 0, 2, 2, 0, "International Business Machines", true);
    List<CorefChain.CorefMention> mentions = Arrays.asList(m1, m2);
    CorefChain c = new CorefChain(1, mentions);

    KBPAnnotator annotator = new KBPAnnotator(new Properties());
    Pair<List<CoreMap>, CoreMap> result = annotator.corefChainToKBPMentions(c, annotation, kbpMentions);

    assertEquals(2, result.first().size());
    assertEquals("International Business Machines", result.second().get(CoreAnnotations.TextAnnotation.class));
  } 
}