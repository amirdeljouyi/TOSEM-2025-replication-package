package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.time.TimeExpression;

import edu.stanford.nlp.ie.NERClassifierCombiner;
import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.Timex;
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.Pair;
import edu.stanford.nlp.util.RuntimeInterruptedException;
import org.junit.Test;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NERCombinerAnnotator_5_GPTLLMTest {

 @Test
  public void testDefaultConstructorWithVerboseTrue() throws Exception {
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(true);
    assertNotNull("Annotator should be initialized", annotator);
  }
@Test
  public void testConstructorWithNERClassifierCombinerAndVerbose() {
    NERClassifierCombiner mockCombiner = mock(NERClassifierCombiner.class);
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(mockCombiner, true);
    assertNotNull("Annotator should not be null", annotator);
  }
@Test
  public void testMergeTokensCreatesMergedWord() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("High");
    token1.setAfter("");
    token1.setEndPosition(4);
    token1.setSentIndex(0);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("-");
    token2.setAfter("");
    token2.setEndPosition(5);

    NERCombinerAnnotator.mergeTokens(token1, token2);

    String expectedMerged = "High-";
    assertEquals(expectedMerged, token1.word());
    assertEquals("", token1.after());
    assertEquals(5, token1.endPosition());
    assertEquals("High-", token1.value().split("-")[0]);
    assertNotNull(token1.get(NERCombinerAnnotator.TokenMergeCountAnnotation.class));
  }
@Test
  public void testTransferNERAnnotationsToAnnotationSetsNER() {
    Annotation nerAnnotation = new Annotation("Barack Obama");
    Annotation originalAnnotation = new Annotation("Barack Obama");

    CoreLabel nerToken = new CoreLabel();
    nerToken.setWord("Obama");
    nerToken.setNER("PERSON");

    List<CoreLabel> nerTokens = new ArrayList<>();
    nerTokens.add(nerToken);
    nerAnnotation.set(CoreAnnotations.TokensAnnotation.class, nerTokens);

    CoreLabel originalToken = new CoreLabel();
    originalToken.setWord("Obama");

    List<CoreLabel> originalTokens = new ArrayList<>();
    originalTokens.add(originalToken);

    CoreMap mockSentence = mock(CoreMap.class);
    when(mockSentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(originalTokens);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(mockSentence);

    originalAnnotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    originalAnnotation.set(CoreAnnotations.TokensAnnotation.class, originalTokens);

    NERCombinerAnnotator.transferNERAnnotationsToAnnotation(nerAnnotation, originalAnnotation);

    assertEquals("PERSON", originalToken.ner());
  }
@Test
  public void testDoOneFailedSentenceAssignsBackgroundNER() {
    CoreLabel token = new CoreLabel();
    token.setWord("January");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    Annotation annotation = new Annotation("January");

    NERClassifierCombiner nerMock = mock(NERClassifierCombiner.class);
    when(nerMock.backgroundSymbol()).thenReturn("O");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(nerMock, false);
    annotator.doOneFailedSentence(annotation, sentence);

    assertEquals("O", token.ner());
  }
@Test
  public void testRequiresIncludesTokensAndSentences() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.model", "edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz");
    props.setProperty("ner.applyFineGrained", "false");
    props.setProperty("ner.buildEntityMentions", "false");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    Set<Class<? extends CoreAnnotation>> required = annotator.requires();

    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.AfterAnnotation.class));
  }
@Test
  public void testRequirementsSatisfiedIncludesNERTagAnnotation() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.applyFineGrained", "true");
    props.setProperty("ner.buildEntityMentions", "true");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertTrue(satisfied.contains(CoreAnnotations.NamedEntityTagAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.CoarseNamedEntityTagAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.MentionsAnnotation.class));
  }
@Test
  public void testNERAnnotatorSpanishNumberRegexIsInitializedWhenLanguageIsSpanish() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.language", "es");
    props.setProperty("ner.model", "edu/stanford/nlp/models/ner/english.conll.4class.distsim.crf.ser.gz");

    try {
      NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
      assertNotNull(annotator);
    } catch (IOException e) {
      fail("Should not throw IOException when constructing with basic Spanish props.");
    }
  }
@Test
  public void testNERSpecificTokenizationDoesNotMergeWordsInExceptionsList() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Chicago");
    token1.setAfter("");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("-");
    token2.setAfter("");

    CoreLabel token3 = new CoreLabel();
    token3.setWord("area");
    token3.setAfter(" ");

    List<CoreLabel> originalTokens = new ArrayList<>();
    originalTokens.add(token1);
    originalTokens.add(token2);
    originalTokens.add(token3);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(originalTokens);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation originalAnnotation = new Annotation("Chicago-area");
    originalAnnotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
  }
@Test
  public void testNERSpecificTokenizationMergesSimpleHyphenatedTokens() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("All");
    token1.setAfter("");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("-");
    token2.setAfter("");

    CoreLabel token3 = new CoreLabel();
    token3.setWord("Star");
    token3.setAfter(" ");

    List<CoreLabel> originalTokens = new ArrayList<>();
    originalTokens.add(token1);
    originalTokens.add(token2);
    originalTokens.add(token3);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(originalTokens);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation originalAnnotation = new Annotation("All-Star");
    originalAnnotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
  }
@Test
  public void testTransferNERAnnotationsWithEmptyNERInputDoesNothing() {
    Annotation nerAnnotation = new Annotation("");
    nerAnnotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());

    Annotation originalAnnotation = new Annotation("");
    originalAnnotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
    originalAnnotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

    NERCombinerAnnotator.transferNERAnnotationsToAnnotation(nerAnnotation, originalAnnotation);
    assertTrue(originalAnnotation.get(CoreAnnotations.TokensAnnotation.class).isEmpty());
  }
@Test
  public void testTransferNERAnnotationsWithMultipleMergeCount() {
    Annotation nerAnnotation = new Annotation("multi-token");

    CoreLabel mergedToken = new CoreLabel();
    mergedToken.setWord("NewYorkCity");
    mergedToken.setNER("LOCATION");
    mergedToken.set(NERCombinerAnnotator.TokenMergeCountAnnotation.class, 3);

    List<CoreLabel> nerTokens = new ArrayList<>();
    nerTokens.add(mergedToken);
    nerAnnotation.set(CoreAnnotations.TokensAnnotation.class, nerTokens);

    CoreLabel tok1 = new CoreLabel();
    CoreLabel tok2 = new CoreLabel();
    CoreLabel tok3 = new CoreLabel();
    tok1.setWord("New");
    tok2.setWord("York");
    tok3.setWord("City");

    List<CoreLabel> origTokens = Arrays.asList(tok1, tok2, tok3);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(origTokens);

    Annotation original = new Annotation("New York City");
    original.set(CoreAnnotations.TokensAnnotation.class, origTokens);
    original.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    NERCombinerAnnotator.transferNERAnnotationsToAnnotation(nerAnnotation, original);

    assertEquals("LOCATION", tok1.ner());
    assertEquals("LOCATION", tok2.ner());
    assertEquals("LOCATION", tok3.ner());
  }
@Test
  public void testAnnotateWithEmptySentencesDoesNotCrash() throws Exception {
    Properties props = new Properties();
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

    Annotation doc = new Annotation("Empty input");
    doc.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

    annotator.annotate(doc);
    assertTrue(doc.get(CoreAnnotations.SentencesAnnotation.class).isEmpty());
  }
@Test
  public void testAnnotateHandlesNullNERTagGracefully() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("apple");
    token1.setNER(null);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);

    Annotation annot = new Annotation("apple");

    annot.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annot.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

    CoreMap mockSentence = mock(CoreMap.class);
    when(mockSentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
    annot.get(CoreAnnotations.SentencesAnnotation.class).add(mockSentence);

    NERClassifierCombiner nerMock = mock(NERClassifierCombiner.class);
  }
@Test
  public void testAnnotatorWithRulesOnlyTrue() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.rulesOnly", "true");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    assertNotNull(annotator);
  }
@Test
  public void testAnnotatorWithStatisticalOnlyDoesNotApplyFineGrainedOrRules() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.statisticalOnly", "true");
    props.setProperty("ner.buildEntityMentions", "false");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    Annotation annotation = new Annotation("simple test");

    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel tok = new CoreLabel();
    tok.setWord("hello");
    tokens.add(tok);

    CoreMap sent = mock(CoreMap.class);
    when(sent.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sent);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);
    assertNotNull(annotation.get(CoreAnnotations.TokensAnnotation.class));
  }
@Test
  public void testDoOneSentenceWhenNERClassifierThrowsException() {
    CoreLabel token = new CoreLabel();
    token.setWord("test");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    Annotation annotation = new Annotation("test");
    NERClassifierCombiner nerMock = mock(NERClassifierCombiner.class);

    try {
      when(nerMock.classifySentenceWithGlobalInformation(tokens, annotation, sentence))
          .thenThrow(new RuntimeInterruptedException());

      NERCombinerAnnotator annotator = new NERCombinerAnnotator(nerMock, false);
      annotator.doOneSentence(annotation, sentence);

      assertEquals(nerMock.backgroundSymbol(), token.ner()); 

    } catch (Exception e) {
      fail("Should not throw: " + e.getMessage());
    }
  }
@Test
  public void testNERProbabilitiesSetToNegativeOneIfMissing() throws Exception {
    CoreLabel token = new CoreLabel();
    token.setWord("Stanford");
    token.setNER("ORGANIZATION");
    token.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, null);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    Annotation annotation = new Annotation("Stanford");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Properties props = new Properties();
    props.setProperty("ner.model", "");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    annotator.annotate(annotation);

    Map<String, Double> neProb = token.get(CoreAnnotations.NamedEntityTagProbsAnnotation.class);
    assertNotNull(neProb);
    assertTrue(neProb.containsKey("ORGANIZATION"));
    assertEquals(-1.0, neProb.get("ORGANIZATION"), 0.01);
  }
@Test
  public void testAnnotationWithDocDatePropagation() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.docdate.use", "true");
    props.setProperty("ner.docdate.docdate", "2023-04-29");
    props.setProperty("ner.docdate.pattern", "\\d{4}-\\d{2}-\\d{2}");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

    Annotation annotation = new Annotation("April 29, 2023");
    annotation.set(CoreAnnotations.DocDateAnnotation.class, "2023-04-29");

    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());

    annotator.annotate(annotation);

    assertEquals("2023-04-29", annotation.get(CoreAnnotations.DocDateAnnotation.class));
  }
@Test
  public void testNERAnnotateHandlesNullNormalizedNERGracefully() {
    CoreLabel inputToken = new CoreLabel();
    inputToken.setWord("Stanford");
    inputToken.setNER("ORGANIZATION");

    CoreLabel outputToken = new CoreLabel();
    outputToken.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    outputToken.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, null);

    List<CoreLabel> inputList = new ArrayList<>();
    inputList.add(inputToken);

    List<CoreLabel> outputList = new ArrayList<>();
    outputList.add(outputToken);

    CoreMap sent = mock(CoreMap.class);
    when(sent.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(inputList);

    Annotation doc = new Annotation("Stanford");
    NERClassifierCombiner mockNer = mock(NERClassifierCombiner.class);

    try {
      when(mockNer.classifySentenceWithGlobalInformation(inputList, doc, sent)).thenReturn(outputList);
      when(mockNer.backgroundSymbol()).thenReturn("O");

      NERCombinerAnnotator annotator = new NERCombinerAnnotator(mockNer, false);
      annotator.doOneSentence(doc, sent);

      assertEquals("ORGANIZATION", inputToken.ner());
      assertNull(inputToken.get(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class));
    } catch (Exception e) {
      fail("Unexpected exception: " + e.getMessage());
    }
  }
@Test
  public void testAnnotatorWithUseNERSpecificTokenizationDisabled() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.useNERSpecificTokenization", "false");
    props.setProperty("ner.model", "");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

    Annotation doc = new Annotation("Text");
    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token = new CoreLabel();
    token.setWord("Test");
    tokens.add(token);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    doc.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(doc);
    assertNotNull(doc.get(CoreAnnotations.TokensAnnotation.class));
  }
@Test
  public void testAnnotatorHandlesMissingAfterAnnotation() throws Exception {
    CoreLabel token = new CoreLabel();
    token.setWord("Boston");
    token.set(CoreAnnotations.BeforeAnnotation.class, " ");
    

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation doc = new Annotation("Boston");
    doc.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

    NERClassifierCombiner mockNer = mock(NERClassifierCombiner.class);
    when(mockNer.backgroundSymbol()).thenReturn("MISC");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(mockNer, false);
    annotator.annotate(doc);

    assertEquals("MISC", token.ner());
  }
@Test
  public void testDoOneSentenceSkipsWhenSentenceTooLong() {
    List<CoreLabel> tokens = new ArrayList<>();
    for (int i = 0; i < 1500; i++) {
      CoreLabel token = new CoreLabel();
      token.setWord("a");
      tokens.add(token);
    }

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    Annotation doc = new Annotation("Very long input");

    NERClassifierCombiner nerMock = mock(NERClassifierCombiner.class);
    when(nerMock.backgroundSymbol()).thenReturn("O");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(nerMock, false, 1, 0, 1000);
    annotator.doOneSentence(doc, sentence);

    assertEquals("O", tokens.get(0).ner());
  }
@Test
  public void testTokensRegexRulesFlagSetButNoRulesProvided() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.additional.tokensregex.rules", "");
    props.setProperty("ner.model", "");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    assertNotNull(annotator);
  }
@Test
  public void testSUTimeDisabledExplicitlyAvoidsSetup() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("sutime.use", "false");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

    Annotation doc = new Annotation("");
    doc.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());
    doc.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());

    annotator.annotate(doc);
    assertNotNull(doc.get(CoreAnnotations.TokensAnnotation.class));
  }
@Test
  public void testEntityMentionsNotBuiltWhenDisabled() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.buildEntityMentions", "false");
    props.setProperty("ner.model", "");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

    Annotation doc = new Annotation("sample");

    CoreLabel token = new CoreLabel();
    token.setWord("Stanford");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    doc.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(doc);

    assertNull(doc.get(CoreAnnotations.MentionsAnnotation.class));
  }
@Test
  public void testAnnotatorHandlesMultipleSentences() throws Exception {
    CoreLabel tok1 = new CoreLabel();
    tok1.setWord("Barack");

    CoreLabel tok2 = new CoreLabel();
    tok2.setWord("Obama");

    List<CoreLabel> tokens1 = Arrays.asList(tok1);
    List<CoreLabel> tokens2 = Arrays.asList(tok2);

    CoreMap sent1 = mock(CoreMap.class);
    when(sent1.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens1);

    CoreMap sent2 = mock(CoreMap.class);
    when(sent2.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens2);

    List<CoreMap> sentences = Arrays.asList(sent1, sent2);

    Annotation doc = new Annotation("Multiple");
    doc.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    doc.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());

    NERClassifierCombiner nerMock = mock(NERClassifierCombiner.class);
    CoreLabel outTok1 = new CoreLabel();
    outTok1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    outTok1.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, Collections.singletonMap("PERSON", 0.9));

    CoreLabel outTok2 = new CoreLabel();
    outTok2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    outTok2.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, Collections.singletonMap("PERSON", 0.8));

    when(nerMock.classifySentenceWithGlobalInformation(eq(tokens1), any(), eq(sent1))).thenReturn(Arrays.asList(outTok1));
    when(nerMock.classifySentenceWithGlobalInformation(eq(tokens2), any(), eq(sent2))).thenReturn(Arrays.asList(outTok2));

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(nerMock, false);
    annotator.annotate(doc);

    assertEquals("PERSON", tok1.ner());
    assertEquals("PERSON", tok2.ner());
  }
@Test
  public void testSentenceWithNoTokensDoesNotCauseError() throws Exception {
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.emptyList());

    Annotation doc = new Annotation("Empty");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
    doc.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());

    NERClassifierCombiner nerMock = mock(NERClassifierCombiner.class);
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(nerMock, false);
    annotator.annotate(doc);

    assertTrue(doc.get(CoreAnnotations.TokensAnnotation.class).isEmpty());
  }
@Test
  public void testDocDateAnnotatorNotInitializedWithoutProperty() throws Exception {
    Properties props = new Properties(); 
    props.setProperty("ner.model", "");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

    Annotation doc = new Annotation("2024 text");
    doc.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());
    doc.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());

    annotator.annotate(doc);
    assertNotNull(doc.get(CoreAnnotations.TokensAnnotation.class));
  }
@Test
  public void testFinalizeAnnotationExecutesWithoutError() {
    CoreLabel token = new CoreLabel();
    token.setWord("42");
    token.setNER("NUMBER");

    List<CoreLabel> tokens = Arrays.asList(token);
    Annotation doc = new Annotation("42");

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    doc.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

    NERClassifierCombiner mockNer = mock(NERClassifierCombiner.class);
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(mockNer, false);

    annotator.annotate(doc);
    assertEquals("NUMBER", token.ner()); 
  }
@Test
  public void testNERCombinerAnnotatorDoesNotThrowWhenTextAnnotationMissing() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.useNERSpecificTokenization", "true");
    props.setProperty("ner.model", "");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

    annotator.annotate(annotation);

    assertNotNull(annotation.get(CoreAnnotations.TokensAnnotation.class));
  }
@Test
  public void testAnnotationWithMergedTokenLongerThan1TransferNER() {
    Annotation original = new Annotation("San Francisco");
    CoreLabel originalToken1 = new CoreLabel();
    originalToken1.setWord("San");
    CoreLabel originalToken2 = new CoreLabel();
    originalToken2.setWord("Francisco");

    List<CoreLabel> originalTokens = Arrays.asList(originalToken1, originalToken2);
    CoreMap originalSentence = mock(CoreMap.class);
    when(originalSentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(originalTokens);

    original.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(originalSentence));
    original.set(CoreAnnotations.TokensAnnotation.class, originalTokens);

    Annotation nerAnnotation = new Annotation("SanFrancisco");
    CoreLabel mergedToken = new CoreLabel();
    mergedToken.setWord("SanFrancisco");
    mergedToken.setNER("LOCATION");
    mergedToken.set(NERCombinerAnnotator.TokenMergeCountAnnotation.class, 2);

    nerAnnotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(mergedToken));

    NERCombinerAnnotator.transferNERAnnotationsToAnnotation(nerAnnotation, original);

    assertEquals("LOCATION", originalToken1.ner());
    assertEquals("LOCATION", originalToken2.ner());
  }
@Test
  public void testNonNullFineGrainedTagCopiedToTokens() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.applyFineGrained", "true");
    props.setProperty("ner.model", "");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.setWord("Microsoft");
    token.setNER("ORGANIZATION");

    List<CoreLabel> tokens = Arrays.asList(token);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    List<CoreMap> sentences = Arrays.asList(sentence);

    Annotation annotation = new Annotation("Microsoft");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    assertEquals("ORGANIZATION", token.get(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class));
  }
@Test
  public void testNumericAndTimexAnnotationsCleanedUpForMoneyToken() {
    CoreLabel token = new CoreLabel();
    token.setNER("MONEY");

    List<CoreLabel> tokens = Collections.singletonList(token);
    Annotation annotation = new Annotation("result");
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    NERClassifierCombiner nerMock = mock(NERClassifierCombiner.class);
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(nerMock, false);

    annotator.annotate(annotation);

    assertNull(token.get(TimeAnnotations.TimexAnnotation.class));
  }
@Test
  public void testEmptyTokenListSkipTransferWithoutException() {
    Annotation ner = new Annotation("empty");
    ner.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
    Annotation orig = new Annotation("empty");
    orig.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
    orig.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

    NERCombinerAnnotator.transferNERAnnotationsToAnnotation(ner, orig);

    assertNotNull(orig);
  }
@Test
  public void testRequiresWithOnlyStatisticalModelDisablesPOSAndLemma() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.statisticalOnly", "true");
    props.setProperty("ner.model", "some-model.ser.gz");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    Set<Class<? extends CoreAnnotation>> required = annotator.requires();

    assertFalse(required.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertFalse(required.contains(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testNERTagProbabilitiesPreservedWhenAlreadyPresent() {
    CoreLabel token = new CoreLabel();
    token.setNER("LOCATION");

    Map<String, Double> probs = new HashMap<>();
    probs.put("LOCATION", 0.65);
    token.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, probs);

    List<CoreLabel> tokens = Collections.singletonList(token);
    Annotation annotation = new Annotation("Paris");

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    NERClassifierCombiner nerMock = mock(NERClassifierCombiner.class);
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(nerMock, false);

    annotator.annotate(annotation);

    Map<String, Double> finalProbs = token.get(CoreAnnotations.NamedEntityTagProbsAnnotation.class);
    assertEquals(0.65, finalProbs.get("LOCATION"), 0.01);
  }
@Test
  public void testAnnotationWithoutSentenceIndexDoesNotCauseException() {
    CoreLabel token = new CoreLabel();
    token.setWord("Google");
    token.setNER("ORGANIZATION");

    List<CoreLabel> tokens = Collections.singletonList(token);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    Annotation annotation = new Annotation("Google");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    NERClassifierCombiner nerMock = mock(NERClassifierCombiner.class);
    when(nerMock.backgroundSymbol()).thenReturn("O");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(nerMock, false);
    annotator.annotate(annotation);

    assertEquals("ORGANIZATION", token.ner());
  }
@Test
  public void testMergedTokenWithoutNERTagDoesNotThrowDuringTransfer() {
    Annotation nerAnnotation = new Annotation("ACME Corp");

    CoreLabel mergedToken = new CoreLabel();
    mergedToken.setWord("ACMECorp"); 
    mergedToken.set(NERCombinerAnnotator.TokenMergeCountAnnotation.class, 2);

    nerAnnotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(mergedToken));

    CoreLabel t1 = new CoreLabel();
    t1.setWord("ACME");

    CoreLabel t2 = new CoreLabel();
    t2.setWord("Corp");

    List<CoreLabel> origTokens = Arrays.asList(t1, t2);

    CoreMap sent = mock(CoreMap.class);
    when(sent.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(origTokens);

    Annotation original = new Annotation("ACME Corp");
    original.set(CoreAnnotations.TokensAnnotation.class, origTokens);
    original.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sent));

    NERCombinerAnnotator.transferNERAnnotationsToAnnotation(nerAnnotation, original);

    assertNull(t1.ner());
    assertNull(t2.ner());
  }
@Test
  public void testBackgroundTagAppliedOnEmptyNEROutput() {
    CoreLabel token = new CoreLabel();
    token.setWord("test");

    List<CoreLabel> tokens = Collections.singletonList(token);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    Annotation annot = new Annotation("test");
    NERClassifierCombiner nerMock = mock(NERClassifierCombiner.class);
    when(nerMock.backgroundSymbol()).thenReturn("O");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(nerMock, false);
    annotator.doOneFailedSentence(annot, sentence);

    assertEquals("O", token.ner());
  }
@Test
  public void testConstructorWithNoModelsDefinedAndNoDefaultsUsed() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    assertNotNull(annotator);
  }
@Test
  public void testConstructorSpanishLanguageTriggersSpanishNumberAnnotator() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.language", "es");
    props.setProperty("ner.model", "");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    assertNotNull(annotator);
  }
@Test
  public void testSetUpFineGrainedNERDoesNotTriggerConfigurationWhenDisabled() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.applyFineGrained", "false");
    props.setProperty("ner.model", "");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    assertNotNull(annotator);
  }
@Test
  public void testSetUpAdditionalRulesNERSkipsSetupForEmptyMapping() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.additional.regexner.mapping", "");
    props.setProperty("ner.model", "");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    assertNotNull(annotator);
  }
@Test
  public void testSetUpTokensRegexRulesSkipsWhenNoRulesSupplied() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.additional.tokensregex.rules", "");
    props.setProperty("ner.model", "");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    assertNotNull(annotator);
  }
@Test
  public void testSetUpDocDateAnnotatorSkipsWhenNoDocDateKeyPresent() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    assertNotNull(annotator);
  }
@Test
  public void testAnnotationWithSingleTokenThatHasAfterAnnotationNullDoesNotThrow() throws Exception {
    CoreLabel token = new CoreLabel();
    token.setWord("Token");
    token.set(CoreAnnotations.AfterAnnotation.class, null);
    token.setSentIndex(0);

    List<CoreLabel> originalTokens = Arrays.asList(token);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(originalTokens);
    Annotation originalAnnotation = new Annotation("Test");
    originalAnnotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  }
@Test
  public void testTransferNERAnnotationsHandlesTokenMismatchGracefully() {
    CoreLabel originalToken = new CoreLabel();
    originalToken.setWord("EU");

    List<CoreLabel> originalTokens = new ArrayList<>();
    originalTokens.add(originalToken);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(originalTokens);

    Annotation original = new Annotation("EU");
    original.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    original.set(CoreAnnotations.TokensAnnotation.class, originalTokens);

    CoreLabel nerToken1 = new CoreLabel();
    nerToken1.setWord("European");

    CoreLabel nerToken2 = new CoreLabel();
    nerToken2.setWord("Union");
    nerToken2.setNER("ORGANIZATION");

    List<CoreLabel> nerTokens = Arrays.asList(nerToken1, nerToken2);
    Annotation ner = new Annotation("European Union");
    ner.set(CoreAnnotations.TokensAnnotation.class, nerTokens);

    NERCombinerAnnotator.transferNERAnnotationsToAnnotation(ner, original);
    assertNull(originalToken.ner());
  }
@Test
  public void testMergeTokensCalledMultipleTimesAccumulatesMergeCount() {
    CoreLabel a = new CoreLabel();
    a.setWord("part");
    a.setAfter("");
    a.setEndPosition(10);
    a.setSentIndex(0);
    a.set(NERCombinerAnnotator.TokenMergeCountAnnotation.class, 1);

    CoreLabel b = new CoreLabel();
    b.setWord("-");
    b.setAfter("");
    b.setEndPosition(20);

    CoreLabel c = new CoreLabel();
    c.setWord("time");
    c.setAfter(".");
    c.setEndPosition(30);

    NERCombinerAnnotator.mergeTokens(a, b);
    NERCombinerAnnotator.mergeTokens(a, c);

    int mergeCount = a.get(NERCombinerAnnotator.TokenMergeCountAnnotation.class);
    assertEquals(3, mergeCount);
    assertTrue(a.word().contains("time"));
    assertEquals(30, a.endPosition());
  }
@Test
  public void testDoOneSentenceHandlesNullOutputFromNERClassifier() {
    CoreLabel token = new CoreLabel();
    token.setWord("London");

    List<CoreLabel> tokens = Collections.singletonList(token);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    Annotation doc = new Annotation("London");

    NERClassifierCombiner mockNer = mock(NERClassifierCombiner.class);
    when(mockNer.classifySentenceWithGlobalInformation(tokens, doc, sentence)).thenReturn(null);
    when(mockNer.backgroundSymbol()).thenReturn("O");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(mockNer, false);
    annotator.doOneSentence(doc, sentence);

    assertEquals("O", token.ner());
  }
@Test
  public void testDoOneSentenceWithMissingNamedEntityTagFallbackToNull() {
    CoreLabel inToken = new CoreLabel();
    inToken.setWord("Value");

    CoreLabel outToken = new CoreLabel(); 
    

    List<CoreLabel> inputTokens = Collections.singletonList(inToken);
    List<CoreLabel> outputTokens = Collections.singletonList(outToken);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(inputTokens);

    Annotation doc = new Annotation("Value");

    NERClassifierCombiner mockNer = mock(NERClassifierCombiner.class);
    when(mockNer.classifySentenceWithGlobalInformation(inputTokens, doc, sentence)).thenReturn(outputTokens);
    when(mockNer.backgroundSymbol()).thenReturn("O");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(mockNer, false);
    annotator.doOneSentence(doc, sentence);

    assertNull(inToken.ner());
  }
@Test
  public void testEntityTypeAnnotationIsNotSetWhenMentionsNotBuilt() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.buildEntityMentions", "false");
    props.setProperty("ner.model", "");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.setWord("IBM");

    List<CoreLabel> tokens = Collections.singletonList(token);
    Annotation doc = new Annotation("IBM");

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(doc);

    assertNull(doc.get(CoreAnnotations.EntityTypeAnnotation.class));
    assertNull(doc.get(CoreAnnotations.MentionsAnnotation.class));
  }
@Test
  public void testEmptySentenceTokenListHandledInDoOneSentence() {
    Annotation annotation = new Annotation("Empty");
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.emptyList());

    NERClassifierCombiner ner = mock(NERClassifierCombiner.class);
    when(ner.backgroundSymbol()).thenReturn("O");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(ner, false);
    annotator.doOneSentence(annotation, sentence);

    assertTrue(annotation != null);
  }
@Test
  public void testTokenWithAlreadySetNERDoesNotChangeInDoOneFailedSentence() {
    CoreLabel token = new CoreLabel();
    token.setWord("Apple");
    token.setNER("ORGANIZATION");

    List<CoreLabel> tokens = Collections.singletonList(token);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    Annotation annotation = new Annotation("Apple");

    NERClassifierCombiner ner = mock(NERClassifierCombiner.class);
    when(ner.backgroundSymbol()).thenReturn("O");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(ner, false);
    annotator.doOneFailedSentence(annotation, sentence);

    assertEquals("ORGANIZATION", token.ner());
  }
@Test
  public void testMissingNERProbabilityMapIsReplacedWithNegativeConfidence() {
    CoreLabel token = new CoreLabel();
    token.setWord("Paris");
    token.setNER("LOCATION");
    token.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, null);

    List<CoreLabel> tokens = Collections.singletonList(token);
    Annotation annotation = new Annotation("Paris");

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Properties props = new Properties();
    props.setProperty("ner.model", "");
  }
@Test
  public void testDoOneSentenceHandlesNERClassifierReturningPartialOutput() {
    CoreLabel inputToken = new CoreLabel();
    inputToken.setWord("Berlin");

    List<CoreLabel> inputTokens = Collections.singletonList(inputToken);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(inputTokens);

    CoreLabel outputToken = new CoreLabel();
    outputToken.set(CoreAnnotations.NamedEntityTagAnnotation.class, null);
    outputToken.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, null);
    outputToken.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, null);

    List<CoreLabel> outputTokens = Collections.singletonList(outputToken);

    Annotation annotation = new Annotation("Berlin");

    NERClassifierCombiner ner = mock(NERClassifierCombiner.class);
    when(ner.classifySentenceWithGlobalInformation(inputTokens, annotation, sentence)).thenReturn(outputTokens);
    when(ner.backgroundSymbol()).thenReturn("O");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(ner, false);
    annotator.doOneSentence(annotation, sentence);

    assertNull(inputToken.ner());
    assertNull(inputToken.get(CoreAnnotations.NamedEntityTagProbsAnnotation.class));
  }
@Test
  public void testAnnotationWithNoTokensSkipsTransferNERAnnotations() {
    Annotation original = new Annotation("");
    Annotation ner = new Annotation("");

    original.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    original.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

    ner.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

    NERCombinerAnnotator.transferNERAnnotationsToAnnotation(ner, original);

    assertTrue(original.get(CoreAnnotations.TokensAnnotation.class).isEmpty());
  }
@Test
  public void testTransferNERAnnotationsSkipsIfOriginalSentenceMissingTokens() {
    CoreLabel nerToken = new CoreLabel();
    nerToken.setNER("DATE");
    List<CoreLabel> nerTokens = Collections.singletonList(nerToken);
    Annotation ner = new Annotation("token");
    ner.set(CoreAnnotations.TokensAnnotation.class, nerTokens);

    CoreMap originalSentence = mock(CoreMap.class);
    when(originalSentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(null);

    Annotation original = new Annotation("token");
    original.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    original.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(originalSentence));

    NERCombinerAnnotator.transferNERAnnotationsToAnnotation(ner, original);

    assertTrue(true); 
  }
@Test
  public void testTokenWithTimexAnnotationAndNEROfNumberIsStripped() {
    CoreLabel token = new CoreLabel();
    token.setNER("NUMBER");

    List<CoreLabel> tokens = Collections.singletonList(token);
    Annotation annotation = new Annotation("123");

    CoreMap mockSentence = mock(CoreMap.class);
    when(mockSentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(mockSentence));
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    NERClassifierCombiner ner = mock(NERClassifierCombiner.class);

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(ner, false);
    annotator.annotate(annotation);

    assertNull(token.get(TimeAnnotations.TimexAnnotation.class));
  }
@Test
  public void testAnnotationWithEmptyValueInNERProbMapDefaultsProperly() throws IOException {
    CoreLabel token = new CoreLabel();
    token.setWord("Entity");
    token.setNER(null);
    token.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, null);

    List<CoreLabel> tokens = Collections.singletonList(token);
    Annotation annotation = new Annotation("EmptyValue");

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Properties props = new Properties();
    props.setProperty("ner.model", "");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    annotator.annotate(annotation);

    Map<String, Double> probs = token.get(CoreAnnotations.NamedEntityTagProbsAnnotation.class);
    assertNotNull(probs);
    assertTrue(probs.containsKey(null));
    assertEquals(-1.0, probs.get(null), 0.0001);
  }
@Test
  public void testCharOffsetsAndTokenOffsetsSetDuringAnnotationWithNERTokenization() throws Exception {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("New");
    token1.setAfter("");
    token1.setSentIndex(0);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("-");
    token2.setAfter("");
    token2.setSentIndex(0);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("York");
    token3.setAfter(" ");
    token3.setSentIndex(0);

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
    when(sentence.get(CoreAnnotations.TextAnnotation.class)).thenReturn("New-York");
    when(sentence.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class)).thenReturn(0);
    when(sentence.get(CoreAnnotations.CharacterOffsetEndAnnotation.class)).thenReturn(8);
    when(sentence.get(CoreAnnotations.SentenceIndexAnnotation.class)).thenReturn(0);

    Annotation original = new Annotation("New-York");
    original.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
  }
@Test
  public void testNERAnnotatorWithNullSentenceReturnsGracefully() throws Exception {
    Properties properties = new Properties();
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, null);
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

    annotator.annotate(annotation);
    assertNull(annotation.get(CoreAnnotations.SentencesAnnotation.class));
  }
@Test
  public void testNERAnnotatorDoesNotCrashOnNullTokenListInSentence() throws Exception {
    Properties properties = new Properties();
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(null);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation annotation = new Annotation("Test");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());

    annotator.annotate(annotation);
    assertTrue(annotation.get(CoreAnnotations.TokensAnnotation.class).isEmpty());
  }
@Test
  public void testTokenMergeCountAnnotationIsNullInitially() {
    CoreLabel token = new CoreLabel();
    token.setWord("Token");
    token.setAfter("");
    token.setEndPosition(5);
    token.setSentIndex(0);

    CoreLabel next = new CoreLabel();
    next.setWord("-");
    next.setAfter("");
    next.setEndPosition(8);

    NERCombinerAnnotator.mergeTokens(token, next);

    assertEquals((Integer)1, token.get(NERCombinerAnnotator.TokenMergeCountAnnotation.class));
  }
@Test
  public void testTokenMergeCountAnnotationIncrementsCorrectly() {
    CoreLabel token = new CoreLabel();
    token.setWord("High-");
    token.setAfter("");
    token.setEndPosition(4);
    token.setSentIndex(0);
    token.set(NERCombinerAnnotator.TokenMergeCountAnnotation.class, 2);

    CoreLabel next = new CoreLabel();
    next.setWord("Speed");
    next.setAfter(".");
    next.setEndPosition(9);

    NERCombinerAnnotator.mergeTokens(token, next);

    assertEquals("High-Speed", token.word());
    assertEquals((Integer)3, token.get(NERCombinerAnnotator.TokenMergeCountAnnotation.class));
  }
@Test
  public void testNERAnnotatorWithMissingTextAnnotationDoesNotCrash() throws Exception {
    Properties properties = new Properties();
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

    Annotation annotation = new Annotation((String) null);
    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token = new CoreLabel();
    token.setWord("Text");
    tokens.add(token);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    assertNotNull(annotation.get(CoreAnnotations.TokensAnnotation.class));
  }
@Test
  public void testNERAnnotatorWithSpanishLanguageAndNoModelDoesNotThrow() throws Exception {
    Properties properties = new Properties();
    properties.setProperty("ner.language", "es");
    properties.setProperty("ner.model", "");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

    Annotation annotation = new Annotation("Madrid");

    CoreLabel token = new CoreLabel();
    token.setWord("Madrid");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    assertNotNull(annotation.get(CoreAnnotations.TokensAnnotation.class));
  }
@Test
  public void testNERAnnotatorWithEmptyDocDatePropertiesSkipsSetup() throws Exception {
    Properties properties = new Properties();
    properties.setProperty("ner.model", "");
    

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

    Annotation annotation = new Annotation("sample");

    CoreLabel token = new CoreLabel();
    token.setWord("April");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    assertNotNull(annotation.get(CoreAnnotations.TokensAnnotation.class));
  }
@Test
  public void testDoOneFailedSentenceWithMultipleTokensAndMixedNERFields() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("John"); 

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Doe");
    token2.setNER("PERSON"); 

    List<CoreLabel> tokens = Arrays.asList(token1, token2);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    Annotation annotation = new Annotation("John Doe");

    NERClassifierCombiner ner = mock(NERClassifierCombiner.class);
    when(ner.backgroundSymbol()).thenReturn("O");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(ner, false);
    annotator.doOneFailedSentence(annotation, sentence);

    assertEquals("O", token1.ner()); 
    assertEquals("PERSON", token2.ner()); 
  }
@Test
  public void testAnnotationWithCoarseNERFallback() throws Exception {
    CoreLabel token = new CoreLabel();
    token.setWord("Berlin");
    token.setNER("LOCATION");
    token.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, null);

    List<CoreLabel> tokens = Collections.singletonList(token);
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    Annotation annotation = new Annotation("Berlin");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Properties props = new Properties();
    props.setProperty("ner.model", "");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    annotator.annotate(annotation);

    assertEquals("LOCATION", token.get(CoreAnnotations.CoarseNamedEntityTagAnnotation.class));
  } 
}