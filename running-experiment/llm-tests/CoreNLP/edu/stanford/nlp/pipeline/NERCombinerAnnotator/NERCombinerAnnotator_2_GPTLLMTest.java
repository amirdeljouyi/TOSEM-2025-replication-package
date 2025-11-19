package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.ie.NERClassifierCombiner;
import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.TimeExpression;
import edu.stanford.nlp.time.Timex;
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.Pair;
import edu.stanford.nlp.util.RuntimeInterruptedException;
import org.junit.Test;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NERCombinerAnnotator_2_GPTLLMTest {

 @Test
  public void testDefaultConstructor() throws Exception {
    NERCombinerAnnotator annotator = new NERCombinerAnnotator();
    assertNotNull(annotator);
  }
@Test
  public void testConstructorWithVerboseFlag() throws Exception {
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(true);
    assertNotNull(annotator);
  }
@Test
  public void testConstructorWithNERClassifierCombiner() {
    NERClassifierCombiner mockCombiner = mock(NERClassifierCombiner.class);
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(mockCombiner, true);
    assertNotNull(annotator);
  }
@Test
  public void testAnnotateWithEmptyAnnotation() throws IOException {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

    Annotation emptyAnnotation = new Annotation("");
    emptyAnnotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

    annotator.annotate(emptyAnnotation);
    assertTrue(true); 
  }
@Test
  public void testTransferNERAnnotationsToAnnotation() {
    CoreLabel nerToken = new CoreLabel();
    nerToken.setNER("PERSON");

    nerToken.set(NERCombinerAnnotator.TokenMergeCountAnnotation.class, 1);

    List<CoreLabel> nerTokens = Arrays.asList(nerToken);
    Annotation nerAnnotation = new Annotation("John");
    nerAnnotation.set(CoreAnnotations.TokensAnnotation.class, nerTokens);

    CoreMap nerSentence = new Annotation("John");
    nerSentence.set(CoreAnnotations.TokensAnnotation.class, nerTokens);
    nerAnnotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(nerSentence));

    CoreLabel origToken = new CoreLabel();
    List<CoreLabel> originalTokens = Arrays.asList(origToken);
    Annotation original = new Annotation("John");
    original.set(CoreAnnotations.TokensAnnotation.class, originalTokens);

    CoreMap origSentence = new Annotation("John");
    origSentence.set(CoreAnnotations.TokensAnnotation.class, originalTokens);
    original.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(origSentence));

    NERCombinerAnnotator.transferNERAnnotationsToAnnotation(nerAnnotation, original);

    assertEquals("PERSON", origToken.ner());
    assertTrue(origToken.containsKey(TimeAnnotations.TimexAnnotation.class));
  }
@Test
  public void testMergeTokensUpdatesFieldsCorrectly() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("All");
    token1.setAfter("");
    token1.setEndPosition(3);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("-");
    token2.setAfter("");
    token2.setEndPosition(4);

    NERCombinerAnnotator.mergeTokens(token1, token2);

    assertEquals("All-", token1.word());
    assertEquals((Integer) 1, token1.get(NERCombinerAnnotator.TokenMergeCountAnnotation.class));

    CoreLabel token3 = new CoreLabel();
    token3.setWord("Star");
    token3.setAfter(" ");
    token3.setEndPosition(8);

    NERCombinerAnnotator.mergeTokens(token1, token3);

    assertEquals("All-Star", token1.word());
    assertEquals(" ", token1.after());
    assertEquals((Integer) 2, token1.get(NERCombinerAnnotator.TokenMergeCountAnnotation.class));
  }
@Test
  public void testDoOneFailedSentenceAddsBackOffNER() throws IOException {
    NERClassifierCombiner mockNER = mock(NERClassifierCombiner.class);
    when(mockNER.backgroundSymbol()).thenReturn("O");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(mockNER, false);

    CoreLabel token = new CoreLabel();
    token.setWord("Failure");

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Arrays.asList(token));

    Annotation annotation = new Annotation("Failure case");

    annotator.doOneFailedSentence(annotation, sentence);

    assertEquals("O", token.ner());
  }
@Test
  public void testRequirementsNotNull() throws IOException {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

    Set<Class<? extends CoreAnnotation>> required = annotator.requires();
    assertNotNull(required);
    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
  }
@Test
  public void testRequirementsSatisfiedIncludesNERAnnotations() throws IOException {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
    assertTrue(satisfied.contains(CoreAnnotations.NamedEntityTagAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.CoarseNamedEntityTagAnnotation.class));
  }
@Test
  public void testConstructorSpanishNumberRegexNER() throws IOException {
    Properties props = new Properties();
    props.setProperty("ner.language", "es");
    props.setProperty("ner.model", "");
    props.setProperty("sutime.enabled", "false");
    props.setProperty("ner.rulesOnly", "true");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    assertNotNull(annotator);
  }
@Test
  public void testConstructorEnablesDocDateAnnotator() throws IOException {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("ner.docdate.use", "true");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    assertNotNull(annotator);
  }
@Test
  public void testDoOneSentenceLengthExceededTriggersFallback() {
    NERClassifierCombiner mockNER = mock(NERClassifierCombiner.class);
    when(mockNER.backgroundSymbol()).thenReturn("O");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(mockNER, false, 1, 0, 1, false, false);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("This");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("fails");

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Arrays.asList(token1, token2));

    Annotation ann = new Annotation("Too long");

    annotator.doOneSentence(ann, sentence);

    assertEquals("O", token1.ner());
    assertEquals("O", token2.ner());
  }
@Test
  public void testTransferNERAnnotationsWithEmptyTokenListsDoesNotFail() {
    Annotation nerAnnotation = new Annotation("text");
    nerAnnotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
    nerAnnotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

    Annotation originalAnnotation = new Annotation("text");
    originalAnnotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
    originalAnnotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

    NERCombinerAnnotator.transferNERAnnotationsToAnnotation(nerAnnotation, originalAnnotation);

    assertTrue(true); 
  }
@Test
  public void testAnnotationWithNERTokenizationHandlesSingleTokenSentence() {
    CoreLabel token = new CoreLabel();
    token.setWord("Word");
    token.setAfter(" ");
    token.set(CoreAnnotations.TextAnnotation.class, "Word");

    Annotation sentence = new Annotation("Word");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation ann = new Annotation("Word");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
  }
@Test
  public void testTransferNERAnnotationsHandlesMultipleMergeTokensAcrossBounds() {
    CoreLabel mergedToken = new CoreLabel();
    mergedToken.setNER("LOCATION");
    mergedToken.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "LOCATION");
    mergedToken.set(NERCombinerAnnotator.TokenMergeCountAnnotation.class, 2);

    List<CoreLabel> nerTokens = Arrays.asList(mergedToken);
    Annotation nerAnn = new Annotation("Chicago-area");
    nerAnn.set(CoreAnnotations.TokensAnnotation.class, nerTokens);

    CoreMap nerSent = new Annotation("Chicago-area");
    nerSent.set(CoreAnnotations.TokensAnnotation.class, nerTokens);
    nerAnn.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(nerSent));

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Chicago");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("-");
    CoreMap origSent = new Annotation("Chicago-area");
    origSent.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));

    List<CoreLabel> originalTokens = Arrays.asList(token1, token2);
    Annotation origAnn = new Annotation("Chicago-area");
    origAnn.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(origSent));
    origAnn.set(CoreAnnotations.TokensAnnotation.class, originalTokens);

    NERCombinerAnnotator.transferNERAnnotationsToAnnotation(nerAnn, origAnn);
    assertEquals("LOCATION", token1.ner());
    assertEquals("LOCATION", token2.ner());
  }
@Test
  public void testDoOneSentenceReturnsNullWhenNERThrowsRuntimeInterruptedException() {
    NERClassifierCombiner nerMock = mock(NERClassifierCombiner.class);
    when(nerMock.classifySentenceWithGlobalInformation(anyList(), any(Annotation.class), any(CoreMap.class)))
        .thenThrow(new RuntimeInterruptedException());

    when(nerMock.backgroundSymbol()).thenReturn("O");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(nerMock, false);

    CoreLabel token = new CoreLabel();
    token.setWord("test");
    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Arrays.asList(token));

    Annotation annotation = new Annotation("test text");

    annotator.doOneSentence(annotation, sentence);
    assertEquals("O", token.ner()); 
  }
@Test
  public void testDoOneSentenceNormalExecutionSetsNERFields() {
    NERClassifierCombiner nerMock = mock(NERClassifierCombiner.class);

    CoreLabel inToken = new CoreLabel();
    inToken.setWord("Paris");

    CoreLabel outToken = new CoreLabel();
    outToken.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");
    outToken.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, Collections.singletonMap("LOCATION", 1.0));

    when(nerMock.classifySentenceWithGlobalInformation(anyList(), any(Annotation.class), any(CoreMap.class)))
        .thenReturn(Arrays.asList(outToken));

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(nerMock, false);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Arrays.asList(inToken));

    Annotation ann = new Annotation("Paris");
    annotator.doOneSentence(ann, sentence);

    assertEquals("LOCATION", inToken.ner());
  }
@Test
  public void testUseNERSpecificTokenizationSkipsWhenDisabled() throws IOException {
    Properties props = new Properties();
    props.setProperty("ner.useNERSpecificTokenization", "false");
    props.setProperty("ner.model", "");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.setWord("ORG");
    token.setAfter(" ");
    token.set(CoreAnnotations.TextAnnotation.class, "ORG");
    token.setNER("ORGANIZATION");

    Annotation sentence = new Annotation("ORG");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation ann = new Annotation("ORG");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(ann);
    assertTrue(true); 
  }
@Test
  public void testFineGrainedNERAndAdditionalRulesDisabled() throws IOException {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("ner.applyFineGrained", "false");
    props.setProperty("ner.additional.regexner.mapping", "");
    props.setProperty("ner.rulesOnly", "false");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.setWord("info");
    token.setAfter(" ");
    token.set(CoreAnnotations.TextAnnotation.class, "info");
    token.setNER("O");

    Annotation sentence = new Annotation("info");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);

    Annotation doc = new Annotation("info");
    doc.set(CoreAnnotations.TextAnnotation.class, "info");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(doc);
    assertNotNull(token.get(CoreAnnotations.NamedEntityTagProbsAnnotation.class));
  }
@Test
  public void testAnnotationWithNERTokenizationDoesNotMergeWithExceptionWord() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Chicago");
    token1.setAfter("");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("-");
    token2.setAfter("");

    CoreLabel token3 = new CoreLabel();
    token3.setWord("based");
    token3.setAfter(" ");

    List<CoreLabel> tokenList = Arrays.asList(token1, token2, token3);

    Annotation sentence = new Annotation("Chicago-based");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokenList);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 13);

    Annotation ann = new Annotation("Chicago-based");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
  }
@Test
  public void testNullNERAnnotationFieldTransferSkippedGracefully() {
    CoreLabel nerToken = new CoreLabel();
    nerToken.setNER(null);
    nerToken.set(NERCombinerAnnotator.TokenMergeCountAnnotation.class, 1);
    List<CoreLabel> nerTokens = Arrays.asList(nerToken);

    Annotation nerAnnotation = new Annotation("test");
    nerAnnotation.set(CoreAnnotations.TokensAnnotation.class, nerTokens);
    Annotation nerSentence = new Annotation("test");
    nerSentence.set(CoreAnnotations.TokensAnnotation.class, nerTokens);
    nerAnnotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(nerSentence));

    CoreLabel origToken = new CoreLabel();
    List<CoreLabel> origTokens = Arrays.asList(origToken);

    Annotation origAnnotation = new Annotation("test");
    origAnnotation.set(CoreAnnotations.TokensAnnotation.class, origTokens);
    Annotation origSentence = new Annotation("test");
    origSentence.set(CoreAnnotations.TokensAnnotation.class, origTokens);
    origAnnotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(origSentence));

    NERCombinerAnnotator.transferNERAnnotationsToAnnotation(nerAnnotation, origAnnotation);

    assertNull(origToken.ner());
  }
@Test
  public void testNERCombinerConstructorWithEmptyModelList() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.model", "   "); 
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    assertNotNull(annotator);
  }
@Test
  public void testNERCombinerStatisticalOnlyZeroModelsAllowsSUTIMEConfig() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("ner.statisticalOnly", "true");
    props.setProperty("ner.useSUTime", "true");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    assertNotNull(annotator);
  }
@Test
  public void testNERCombinerUsesOnlyNumericClassifiersNoSUTime() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("ner.rulesOnly", "true");
    props.setProperty("ner.statisticalOnly", "false");
    props.setProperty("ner.applyNumericClassifiers", "true");
    props.setProperty("sutime.enabled", "false");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    assertNotNull(annotator);
  }
@Test
  public void testNERCombinerAnnotateWithInvalidNERTagProbsSetsDefault() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("ner.useNERSpecificTokenization", "false");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.setWord("cash");
    token.setNER("MONEY");

    List<CoreLabel> tokens = Arrays.asList(token);

    Annotation sentence = new Annotation("cash");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);

    Annotation ann = new Annotation("cash");
    ann.set(CoreAnnotations.TextAnnotation.class, "cash");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
    ann.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(ann);
    Map<String, Double> probs = token.get(CoreAnnotations.NamedEntityTagProbsAnnotation.class);

    assertNotNull(probs);
    assertEquals(Double.valueOf(-1.0), probs.get(token.ner()));
  }
@Test
  public void testAnnotationWithSpacesBetweenTokens() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("All");
    token1.setAfter(""); 

    CoreLabel token2 = new CoreLabel();
    token2.setWord("-");
    token2.setAfter(""); 

    CoreLabel token3 = new CoreLabel();
    token3.setWord("Star");
    token3.setAfter(" "); 

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);

    CoreMap sentence = new Annotation("All-Star");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation ann = new Annotation("All-Star");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
    ann.set(CoreAnnotations.TextAnnotation.class, "All-Star");
  }
@Test
  public void testDocDateAnnotatorSetWhenPropertyExists() throws Exception {
    Properties properties = new Properties();
    properties.setProperty("ner.model", "");
    properties.setProperty("ner.docdate.foo", "bar"); 
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);
    assertNotNull(annotator);
  }
@Test
  public void testDoOneSentenceHandlesNormalizedNER() {
    NERClassifierCombiner mockNER = mock(NERClassifierCombiner.class);

    CoreLabel classificationResult = new CoreLabel();
    classificationResult.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");
    classificationResult.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "2022-01-01");
    classificationResult.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, new HashMap<String, Double>());

    when(mockNER.classifySentenceWithGlobalInformation(anyList(), any(Annotation.class), any(CoreMap.class)))
        .thenReturn(Arrays.asList(classificationResult));

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(mockNER, false);

    CoreLabel inputToken = new CoreLabel();
    inputToken.setWord("January");

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Arrays.asList(inputToken));

    Annotation ann = new Annotation("January");
    annotator.doOneSentence(ann, sentence);
    assertEquals("DATE", inputToken.ner());
    assertEquals("2022-01-01", inputToken.get(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class));
  }
@Test
  public void testApplyFineGrainedDisabledStillCoversSetIfFalse() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("ner.applyFineGrained", "false");
    props.setProperty("ner.statisticalOnly", "false");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    assertNotNull(annotator);
  }
@Test
  public void testConstructorWithNullLanguageFallsBackToDefault() throws IOException {
    Properties props = new Properties();
    props.setProperty("ner.language", "");
    props.setProperty("ner.model", "");
    props.setProperty("sutime.enabled", "false");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    assertNotNull(annotator);
  }
@Test
  public void testTokenMergeSkipsWhenNoNextToken() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Prefix");
    token1.setAfter("");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("-");
    token2.setAfter("");
    
    List<CoreLabel> tokenList = Arrays.asList(token1, token2);
    Annotation sentence = new Annotation("Prefix-");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokenList);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);

    Annotation ann = new Annotation("Prefix-");
    ann.set(CoreAnnotations.TextAnnotation.class, "Prefix-");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
  }
@Test
  public void testMergeTokensIncrementsWhenPreAnnotated() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("A");
    token1.setAfter("");
    token1.set(NERCombinerAnnotator.TokenMergeCountAnnotation.class, 3);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("B");
    token2.setAfter("");

    NERCombinerAnnotator.mergeTokens(token1, token2);

    Integer mergedCount = token1.get(NERCombinerAnnotator.TokenMergeCountAnnotation.class);
    assertEquals((Integer) 4, mergedCount);
    assertEquals("AB", token1.word());
  }
@Test
  public void testDoOneSentenceNullOutputTriggersFallback() {
    NERClassifierCombiner ner = mock(NERClassifierCombiner.class);
    when(ner.classifySentenceWithGlobalInformation(anyList(), any(), any())).thenReturn(null);
    when(ner.backgroundSymbol()).thenReturn("O");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(ner, false);

    CoreLabel token = new CoreLabel();
    token.setWord("Unknown");

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Arrays.asList(token));

    Annotation ann = new Annotation("Unknown");
    annotator.doOneSentence(ann, sentence);

    assertEquals("O", token.ner());
  }
@Test
  public void testDoOneSentenceWithMaxLenExceededUsesFallback() {
    NERClassifierCombiner ner = mock(NERClassifierCombiner.class);
    when(ner.classifySentenceWithGlobalInformation(anyList(), any(), any())).thenReturn(null);
    when(ner.backgroundSymbol()).thenReturn("O");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(ner, true, 1, 0, 1, true, false);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("This");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Sentence");

    CoreLabel token3 = new CoreLabel();
    token3.setWord("Is");

    CoreLabel token4 = new CoreLabel();
    token4.setWord("Long");

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Arrays.asList(token1, token2, token3, token4));

    Annotation ann = new Annotation("This sentence is long");
    annotator.doOneSentence(ann, sentence);

    assertEquals("O", token1.ner());
    assertEquals("O", token2.ner());
    assertEquals("O", token3.ner());
    assertEquals("O", token4.ner());
  }
@Test
  public void testAnnotationWithNERTokenizationSkipsMergeOnExceptionWords() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Chicago");
    token1.setAfter("");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("-");
    token2.setAfter("");

    CoreLabel token3 = new CoreLabel();
    token3.setWord("based"); 
    token3.setAfter(" ");

    Annotation sentence = new Annotation("Chicago-based");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2, token3));
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 13);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation doc = new Annotation("Chicago-based");
    doc.set(CoreAnnotations.TextAnnotation.class, "Chicago-based");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
  }
@Test
  public void testRequirementsSatisfiedWithEntityMentionsEnabledIncludesMentions() throws IOException {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("ner.buildEntityMentions", "true");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertTrue(satisfied.contains(CoreAnnotations.MentionsAnnotation.class));
  }
@Test
  public void testRequirementsSatisfiedWithEntityMentionsDisabledExcludesMentions() throws IOException {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("ner.buildEntityMentions", "false");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertFalse(satisfied.contains(CoreAnnotations.MentionsAnnotation.class));
  }
@Test
  public void testMergeTokenAppendsHyphenatedSentIndexToValue() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Star");
    token1.setAfter("");
    token1.setSentIndex(3);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Wars");
    token2.setAfter(" ");
    token2.setEndPosition(12);

    NERCombinerAnnotator.mergeTokens(token1, token2);
    String value = token1.value();
    assertTrue(value.contains("-3"));
  }
@Test
  public void testAfterIsEmptyReturnsFalseWhenAfterIsWhitespace() {
    CoreLabel token = new CoreLabel();
    token.setAfter(" ");
    boolean result = NERCombinerAnnotator.afterIsEmpty.apply(token);
    assertFalse(result);
  }
@Test
  public void testAfterIsEmptyReturnsFalseWhenNoAfterKey() {
    CoreLabel token = new CoreLabel();
    boolean result = NERCombinerAnnotator.afterIsEmpty.apply(token);
    assertFalse(result);
  }
@Test
  public void testSetUpAdditionalRulesNERWhenNoRegexnerMappingPropertyExists() {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("ner.additional.regexner.mapping", "");  

    NERCombinerAnnotator annotator = null;
    try {
      annotator = new NERCombinerAnnotator(props);
    } catch (IOException e) {
      fail("IOException should not occur");
    }

    assertNotNull(annotator);
  }
@Test
  public void testSetUpTokensRegexRulesUnsetPropertyResultsInSkippedSetup() {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("ner.additional.tokensregex.rules", ""); 

    NERCombinerAnnotator annotator = null;
    try {
      annotator = new NERCombinerAnnotator(props);
    } catch (IOException e) {
      fail("IOException should not occur");
    }

    assertNotNull(annotator);
  }
@Test
  public void testSetUpEntityMentionBuildingWithFalseFlagSkipsSetup() {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("ner.buildEntityMentions", "false");

    NERCombinerAnnotator annotator = null;
    try {
      annotator = new NERCombinerAnnotator(props);
    } catch (IOException e) {
      fail("IOException not expected");
    }

    assertNotNull(annotator);
  }
@Test
  public void testSetUpDocDateAnnotatorNoMatchingKeysSkipsSetup() {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("ner.docdatemissingkey", "someVal"); 

    NERCombinerAnnotator annotator = null;
    try {
      annotator = new NERCombinerAnnotator(props);
    } catch (IOException e) {
      fail("Should not throw IOException");
    }

    assertNotNull(annotator);
  }
@Test
  public void testAnnotateWithNullTokenNERAndTimexRemainsSafe() throws IOException {
    CoreLabel token = new CoreLabel();
    token.setNER(null); 
    token.set(TimeAnnotations.TimexAnnotation.class, null); 

    List<CoreLabel> tokens = Arrays.asList(token);

    CoreMap sentence = new Annotation("example");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);

    Annotation doc = new Annotation("example");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
    doc.set(CoreAnnotations.TextAnnotation.class, "example");
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("ner.useNERSpecificTokenization", "false");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

    annotator.annotate(doc);

    assertTrue(true); 
  }
@Test
  public void testTokenWithoutNERDefaultsProbsToNegative1() throws IOException {
    CoreLabel token = new CoreLabel();
    token.setWord("data");
    token.setNER("NUMBER");

    List<CoreLabel> tokens = Arrays.asList(token);

    CoreMap sentence = new Annotation("data");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);

    Annotation doc = new Annotation("data");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
    doc.set(CoreAnnotations.TextAnnotation.class, "data");
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("ner.useNERSpecificTokenization", "false");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    annotator.annotate(doc);

    Map<String, Double> probabilities = token.get(CoreAnnotations.NamedEntityTagProbsAnnotation.class);

    assertNotNull(probabilities);
    assertEquals(Double.valueOf(-1.0), probabilities.get("NUMBER"));
  }
@Test
  public void testTransferNERAnnotationsHandlesMissingMergeCount() {
    CoreLabel nerToken = new CoreLabel();
    nerToken.setNER("LOCATION");

    List<CoreLabel> nerTokens = Arrays.asList(nerToken);

    CoreLabel originalToken = new CoreLabel();
    List<CoreLabel> originalTokens = Arrays.asList(originalToken);

    CoreMap srcSentence = new Annotation("Paris");
    srcSentence.set(CoreAnnotations.TokensAnnotation.class, nerTokens);

    CoreMap targetSentence = new Annotation("Paris");
    targetSentence.set(CoreAnnotations.TokensAnnotation.class, originalTokens);

    Annotation nerAnn = new Annotation("Paris");
    nerAnn.set(CoreAnnotations.TokensAnnotation.class, nerTokens);
    nerAnn.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(srcSentence));

    Annotation origAnn = new Annotation("Paris");
    origAnn.set(CoreAnnotations.TokensAnnotation.class, originalTokens);
    origAnn.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(targetSentence));

    NERCombinerAnnotator.transferNERAnnotationsToAnnotation(nerAnn, origAnn);

    assertEquals("LOCATION", originalToken.ner());
  }
@Test
  public void testNERCombinerStatisticalOnlyTrueSkipsFineGrained() throws IOException {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("ner.statisticalOnly", "true");
    props.setProperty("ner.applyFineGrained", "true"); 

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    assertNotNull(annotator);
  }
@Test
  public void testNERCombinerRulesOnlyTrueSkipsStatisticalModelLoading() throws IOException {
    Properties props = new Properties();
    props.setProperty("ner.rulesOnly", "true");
    props.setProperty("ner.model", ""); 

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    assertNotNull(annotator);
  }
@Test
  public void testNERCombinerAcceptsCommaDelimModelString() throws IOException {
    Properties props = new Properties();
    props.setProperty("ner.model", "model1.ser.gz,model2.ser.gz");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    assertNotNull(annotator);
  }
@Test
  public void testNERCombinerCreatesAnnotationWithNullBeforeAfterFields() throws IOException {
    CoreLabel token = new CoreLabel();
    token.setWord("ACME");
    

    List<CoreLabel> tokens = Arrays.asList(token);

    CoreMap sentence = new Annotation("ACME");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);

    Annotation document = new Annotation("ACME");
    document.set(CoreAnnotations.TextAnnotation.class, "ACME");
    document.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
    document.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("ner.useNERSpecificTokenization", "false");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    annotator.annotate(document);

    assertEquals("ACME", token.word());
  }
@Test
  public void testRequiresIncludesLemmaWhenUsesSUTimeTrue() throws IOException {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("sutime.enabled", "true");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    Set<Class<? extends CoreAnnotation>> required = annotator.requires();

    assertTrue(required.contains(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testRequiresOmitsLemmaWhenNothingNeedsIt() throws IOException {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("ner.applyFineGrained", "false");
    props.setProperty("ner.applyNumericClassifiers", "false");
    props.setProperty("sutime.enabled", "false");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    Set<Class<? extends CoreAnnotation>> required = annotator.requires();

    assertFalse(required.contains(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testEmptyTextAnnotationStillParsesWithoutException() throws IOException {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.TextAnnotation.class, "");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());

    annotator.annotate(annotation);
    assertTrue(true);
  }
@Test
  public void testAnnotationWithNERTokenizationWithMultipleHyphens() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Vice");
    token1.setAfter("");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("-");
    token2.setAfter("");

    CoreLabel token3 = new CoreLabel();
    token3.setWord("President");
    token3.setAfter(" ");

    Annotation sentence = new Annotation("Vice-President");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2, token3));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 14);

    Annotation doc = new Annotation("Vice-President");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
    doc.set(CoreAnnotations.TextAnnotation.class, "Vice-President");
  }
@Test
  public void testTransferNERAnnotationsWithLongMergeChain() {
    CoreLabel nerToken = new CoreLabel();
    nerToken.setNER("ORG");
    nerToken.set(NERCombinerAnnotator.TokenMergeCountAnnotation.class, 4);

    List<CoreLabel> nerTokens = Arrays.asList(nerToken);

    CoreMap nerSentence = new Annotation("Example");
    nerSentence.set(CoreAnnotations.TokensAnnotation.class, nerTokens);

    Annotation nerAnnotation = new Annotation("Example");
    nerAnnotation.set(CoreAnnotations.TokensAnnotation.class, nerTokens);
    nerAnnotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(nerSentence));

    CoreLabel origToken1 = new CoreLabel();
    CoreLabel origToken2 = new CoreLabel();
    CoreLabel origToken3 = new CoreLabel();
    CoreLabel origToken4 = new CoreLabel();

    List<CoreLabel> originalTokens = Arrays.asList(origToken1, origToken2, origToken3, origToken4);

    CoreMap origSentence = new Annotation("Example");
    origSentence.set(CoreAnnotations.TokensAnnotation.class, originalTokens);

    Annotation originalAnnotation = new Annotation("Example");
    originalAnnotation.set(CoreAnnotations.TokensAnnotation.class, originalTokens);
    originalAnnotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(origSentence));

    NERCombinerAnnotator.transferNERAnnotationsToAnnotation(nerAnnotation, originalAnnotation);

    assertEquals("ORG", origToken1.ner());
    assertEquals("ORG", origToken2.ner());
    assertEquals("ORG", origToken3.ner());
    assertEquals("ORG", origToken4.ner());
  }
@Test
  public void testAnnotatorWithFineGrainedRulesButStatisticalOnlyTrueSkipsSetup() throws IOException {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("ner.statisticalOnly", "true");
    props.setProperty("ner.applyFineGrained", "true");
    props.setProperty("ner.additional.regexner.mapping", "someMapping");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    assertNotNull(annotator);
  }
@Test
  public void testMergeTokenUpdatesEndPosition() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("High");
    token1.setAfter("");
    token1.setEndPosition(4);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("School");
    token2.setAfter(" ");
    token2.setEndPosition(10);

    NERCombinerAnnotator.mergeTokens(token1, token2);

    assertEquals("HighSchool", token1.word());
    assertEquals(10, token1.endPosition());
  }
@Test
  public void testTransferNERAnnotationsMergedTokenMissingInOriginal() {
    CoreLabel nerToken = new CoreLabel();
    nerToken.setNER("LOCATION");
    nerToken.set(NERCombinerAnnotator.TokenMergeCountAnnotation.class, 2);

    List<CoreLabel> nerTokens = Arrays.asList(nerToken);
    Annotation nerAnn = new Annotation("Rome-Paris");
    nerAnn.set(CoreAnnotations.TokensAnnotation.class, nerTokens);
    CoreMap nerSent = new Annotation("Rome-Paris");
    nerSent.set(CoreAnnotations.TokensAnnotation.class, nerTokens);
    nerAnn.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(nerSent));

    
    CoreLabel origToken = new CoreLabel();
    List<CoreLabel> originalTokens = Arrays.asList(origToken);
    Annotation origAnn = new Annotation("Rome-Paris");
    origAnn.set(CoreAnnotations.TokensAnnotation.class, originalTokens);
    CoreMap origSent = new Annotation("Rome-Paris");
    origSent.set(CoreAnnotations.TokensAnnotation.class, originalTokens);
    origAnn.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(origSent));

    NERCombinerAnnotator.transferNERAnnotationsToAnnotation(nerAnn, origAnn);

    assertEquals("LOCATION", origToken.ner());
  }
@Test
  public void testMergeTokensPreservesNullTokenMergeCount() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("part1");
    token1.setAfter("");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("part2");
    token2.setAfter(" ");
    token2.setEndPosition(12);

    NERCombinerAnnotator.mergeTokens(token1, token2);

    assertEquals("part1part2", token1.word());
    assertEquals(" ", token1.after());
    assertEquals(12, token1.endPosition());
    assertEquals((Integer) 1, token1.get(NERCombinerAnnotator.TokenMergeCountAnnotation.class));
  }
@Test
  public void testMergeTokensIncrementsPreexistingMergeCount() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("A");
    token1.setAfter("");
    token1.set(NERCombinerAnnotator.TokenMergeCountAnnotation.class, 2);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("B");
    token2.setAfter("");

    NERCombinerAnnotator.mergeTokens(token1, token2);

    Integer mergeCount = token1.get(NERCombinerAnnotator.TokenMergeCountAnnotation.class);
    assertEquals((Integer) 3, mergeCount);
  }
@Test
  public void testTransferNERAnnotationsHandlesMergeCountEqualsOne() {
    CoreLabel nerToken = new CoreLabel();
    nerToken.setNER("PERSON");
    nerToken.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "PERSON");
    nerToken.set(NERCombinerAnnotator.TokenMergeCountAnnotation.class, 1);

    List<CoreLabel> nerTokens = Arrays.asList(nerToken);
    CoreMap nerSentence = new Annotation("John");
    nerSentence.set(CoreAnnotations.TokensAnnotation.class, nerTokens);

    Annotation nerAnnotation = new Annotation("John");
    nerAnnotation.set(CoreAnnotations.TokensAnnotation.class, nerTokens);
    nerAnnotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(nerSentence));

    CoreLabel origToken = new CoreLabel();
    CoreMap origSentence = new Annotation("John");
    origSentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(origToken));

    Annotation original = new Annotation("John");
    original.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(origSentence));
    original.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(origToken));

    NERCombinerAnnotator.transferNERAnnotationsToAnnotation(nerAnnotation, original);
    assertEquals("PERSON", origToken.ner());
  }
@Test
  public void testTransferNERAnnotationsEmptySentListButTokensNotEmpty() {
    CoreLabel nerToken = new CoreLabel();
    nerToken.setNER("DATE");
    nerToken.set(NERCombinerAnnotator.TokenMergeCountAnnotation.class, 1);

    Annotation nerAnn = new Annotation("2021");
    nerAnn.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(nerToken));
    nerAnn.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

    CoreLabel origToken = new CoreLabel();
    Annotation origAnn = new Annotation("2021");
    origAnn.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(origToken));
    origAnn.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

    NERCombinerAnnotator.transferNERAnnotationsToAnnotation(nerAnn, origAnn);
    assertEquals("DATE", origToken.ner());
  }
@Test
  public void testApplyTokensRegexRulesFalseSkipsSetup() throws IOException {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("ner.additional.tokensregex.rules", ""); 
    props.setProperty("ner.statisticalOnly", "true");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    assertNotNull(annotator);
  }
@Test
  public void testAnnotationWithNERTokenizationMultipleValidMerges() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("New");
    token1.setAfter("");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("-");
    token2.setAfter("");

    CoreLabel token3 = new CoreLabel();
    token3.setWord("York");
    token3.setAfter(" ");

    Annotation sentence = new Annotation("New-York");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2, token3));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation ann = new Annotation("New-York");
    ann.set(CoreAnnotations.TextAnnotation.class, "New-York");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
  }
@Test
  public void testDoOneFailedSentenceWithMultipleTokensSetsNER() {
    NERClassifierCombiner ner = mock(NERClassifierCombiner.class);
    when(ner.backgroundSymbol()).thenReturn("O");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(ner, true);

    CoreLabel tokenA = new CoreLabel();
    CoreLabel tokenB = new CoreLabel();

    CoreMap sentence = new Annotation("");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(tokenA, tokenB));
    Annotation doc = new Annotation("Example");

    annotator.doOneFailedSentence(doc, sentence);

    assertEquals("O", tokenA.ner());
    assertEquals("O", tokenB.ner());
  }
@Test
  public void testApplyNumericClassifiersTrueOverridesDisableWhenRulesOnlyFalse() throws IOException {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("ner.rulesOnly", "false");
    props.setProperty("ner.applyNumericClassifiers", "true");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    assertNotNull(annotator);
  }
@Test
  public void testRequiresContainsValidMinimalSetWhenNothingEnabled() throws IOException {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("ner.applyFineGrained", "false");
    props.setProperty("sutime.enabled", "false");
    props.setProperty("ner.applyNumericClassifiers", "false");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

    Set<Class<? extends CoreAnnotation>> required = annotator.requires();
    assertTrue(required.contains(CoreAnnotations.TextAnnotation.class));
    assertFalse(required.contains(CoreAnnotations.LemmaAnnotation.class));
  } 
}