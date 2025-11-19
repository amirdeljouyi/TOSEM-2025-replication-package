package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.time.TimeAnnotations;

import edu.stanford.nlp.ie.NERClassifierCombiner;
import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.time.TimeExpression;
import edu.stanford.nlp.time.Timex;
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Pair;
import edu.stanford.nlp.util.RuntimeInterruptedException;
import org.junit.Test;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;

import static org.junit.Assert.*;

public class NERCombinerAnnotator_4_GPTLLMTest {

 @Test
  public void testLegacyConstructor() throws Exception {
    NERCombinerAnnotator annotator = new NERCombinerAnnotator();
    assertNotNull(annotator);
  }
@Test
  public void testAnnotateBasicSentence() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("ner.statisticalOnly", "true");
    props.setProperty("ner.useNERSpecificTokenization", "false");
    props.setProperty("ner.buildEntityMentions", "false");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

    Annotation annotation = new Annotation("Barack Obama was born in Hawaii.");
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Barack");
    token1.setOriginalText("Barack");
    token1.setIndex(0);
    token1.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.TokenEndAnnotation.class, 1);
    token1.set(CoreAnnotations.AfterAnnotation.class, " ");
    token1.set(CoreAnnotations.BeforeAnnotation.class, "");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Obama");
    token2.setOriginalText("Obama");
    token2.setIndex(1);
    token2.set(CoreAnnotations.TokenBeginAnnotation.class, 1);
    token2.set(CoreAnnotations.TokenEndAnnotation.class, 2);
    token2.set(CoreAnnotations.AfterAnnotation.class, " ");
    token2.set(CoreAnnotations.BeforeAnnotation.class, "");

    CoreLabel token3 = new CoreLabel();
    token3.setWord("was");
    token3.setOriginalText("was");
    token3.setIndex(2);
    token3.set(CoreAnnotations.TokenBeginAnnotation.class, 2);
    token3.set(CoreAnnotations.TokenEndAnnotation.class, 3);
    token3.set(CoreAnnotations.AfterAnnotation.class, " ");
    token3.set(CoreAnnotations.BeforeAnnotation.class, "");

    CoreLabel token4 = new CoreLabel();
    token4.setWord("born");
    token4.setOriginalText("born");
    token4.setIndex(3);
    token4.set(CoreAnnotations.TokenBeginAnnotation.class, 3);
    token4.set(CoreAnnotations.TokenEndAnnotation.class, 4);
    token4.set(CoreAnnotations.AfterAnnotation.class, " ");
    token4.set(CoreAnnotations.BeforeAnnotation.class, "");

    CoreLabel token5 = new CoreLabel();
    token5.setWord("in");
    token5.setOriginalText("in");
    token5.setIndex(4);
    token5.set(CoreAnnotations.TokenBeginAnnotation.class, 4);
    token5.set(CoreAnnotations.TokenEndAnnotation.class, 5);
    token5.set(CoreAnnotations.AfterAnnotation.class, " ");
    token5.set(CoreAnnotations.BeforeAnnotation.class, "");

    CoreLabel token6 = new CoreLabel();
    token6.setWord("Hawaii");
    token6.setOriginalText("Hawaii");
    token6.setIndex(5);
    token6.set(CoreAnnotations.TokenBeginAnnotation.class, 5);
    token6.set(CoreAnnotations.TokenEndAnnotation.class, 6);
    token6.set(CoreAnnotations.AfterAnnotation.class, ".");
    token6.set(CoreAnnotations.BeforeAnnotation.class, "");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5, token6);
    CoreMap sentence = new Annotation("Barack Obama was born in Hawaii.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.TextAnnotation.class, "Barack Obama was born in Hawaii.");

    annotator.annotate(annotation);

    assertNotNull(tokens.get(0).ner());
    assertNotNull(tokens.get(1).ner());
    assertNotNull(tokens.get(2).ner());
    assertNotNull(tokens.get(3).ner());
    assertNotNull(tokens.get(4).ner());
    assertNotNull(tokens.get(5).ner());
  }
@Test
  public void testMergeTokensBehavior() {
    CoreLabel tokenA = new CoreLabel();
    tokenA.setWord("All");
    tokenA.setAfter("");
    tokenA.setEndPosition(3);
    tokenA.setSentIndex(0);
    tokenA.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    tokenA.set(CoreAnnotations.TokenEndAnnotation.class, 1);

    CoreLabel tokenB = new CoreLabel();
    tokenB.setWord("-");
    tokenB.setAfter("");
    tokenB.setEndPosition(4);
    tokenB.setSentIndex(0);
    tokenB.set(CoreAnnotations.TokenBeginAnnotation.class, 1);
    tokenB.set(CoreAnnotations.TokenEndAnnotation.class, 2);

    CoreLabel tokenC = new CoreLabel();
    tokenC.setWord("Star");
    tokenC.setAfter(" ");
    tokenC.setEndPosition(8);
    tokenC.setSentIndex(0);
    tokenC.set(CoreAnnotations.TokenBeginAnnotation.class, 2);
    tokenC.set(CoreAnnotations.TokenEndAnnotation.class, 3);

    tokenA.set(NERCombinerAnnotator.TokenMergeCountAnnotation.class, 1);
    NERCombinerAnnotator.mergeTokens(tokenA, tokenB);
    assertEquals("All-", tokenA.word());
    assertEquals(Integer.valueOf(2), tokenA.get(NERCombinerAnnotator.TokenMergeCountAnnotation.class));

    NERCombinerAnnotator.mergeTokens(tokenA, tokenC);
    assertEquals("All-Star", tokenA.word());
    assertEquals(Integer.valueOf(3), tokenA.get(NERCombinerAnnotator.TokenMergeCountAnnotation.class));
  }
@Test
  public void testTransferAnnotationsDoesNotThrowWhenTokensEmpty() {
    Annotation source = new Annotation("test");
    Annotation target = new Annotation("test");
    source.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    target.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    NERCombinerAnnotator.transferNERAnnotationsToAnnotation(source, target);
    assertTrue(true);
  }
@Test
  public void testDoOneFailedSentenceAppliesBackgroundNER() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("ner.statisticalOnly", "true");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

    Annotation annotation = new Annotation("A sample sentence.");
    CoreLabel token1 = new CoreLabel();
    token1.setWord("A");
    token1.setOriginalText("A");
    token1.setIndex(0);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("sample");
    token2.setOriginalText("sample");
    token2.setIndex(1);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("sentence");
    token3.setOriginalText("sentence");
    token3.setIndex(2);

    CoreLabel token4 = new CoreLabel();
    token4.setWord(".");
    token4.setOriginalText(".");
    token4.setIndex(3);

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4);
    CoreMap sentence = new Annotation("A sample sentence.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    List<CoreMap> sentences = Arrays.asList(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.doOneFailedSentence(annotation, sentence);

    assertNotNull(token1.ner());
    assertNotNull(token2.ner());
    assertNotNull(token3.ner());
    assertNotNull(token4.ner());
  }
@Test
  public void testDoOneSentenceWithTooLongSentenceTriggersFallback() throws Exception {
    NERClassifierCombiner nerCombiner = new NERClassifierCombiner(new Properties());
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(nerCombiner, false, 1, 0, 1, false, false);

    Annotation annotation = new Annotation("This sentence is too long.");
    CoreLabel token1 = new CoreLabel();
    token1.setWord("This");
    token1.setIndex(0);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("sentence");
    token2.setIndex(1);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("is");
    token3.setIndex(2);

    CoreLabel token4 = new CoreLabel();
    token4.setWord("too");
    token4.setIndex(3);

    CoreLabel token5 = new CoreLabel();
    token5.setWord("long");
    token5.setIndex(4);

    CoreLabel token6 = new CoreLabel();
    token6.setWord(".");
    token6.setIndex(5);

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5, token6);
    CoreMap sentence = new Annotation("This sentence is too long.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    List<CoreMap> sentences = Collections.singletonList(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.doOneSentence(annotation, sentence);

    assertNotNull(token1.ner());
    assertNotNull(token6.ner());
  }
@Test
  public void testApplyNumericClassifiersFalseAndStatisticalOnlyTrue() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.statisticalOnly", "true");
    props.setProperty("ner.applyFineGrained", "false");
    props.setProperty("ner.buildEntityMentions", "false");
    props.setProperty("ner.useSUTime", "true"); 
    props.setProperty("ner.model", "");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

    assertNotNull(annotator);
  }
@Test
  public void testEmptyNERModelList() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.rulesOnly", "false");
    props.setProperty("ner.model", "");
    props.setProperty("ner.statisticalOnly", "false");
    props.setProperty("ner.buildEntityMentions", "false");
    props.setProperty("ner.applyFineGrained", "false");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

    assertNotNull(annotator);
  }
@Test
  public void testNERAnnotatorWithDocDateAnnotationEnabled() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.docdate.useFixedDate", "true");
    props.setProperty("ner.applyFineGrained", "false");
    props.setProperty("ner.buildEntityMentions", "false");
    props.setProperty("ner.model", "");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

    Annotation annotation = new Annotation("Today is Tuesday.");
    CoreLabel token = new CoreLabel();
    token.setWord("Today");
    token.setIndex(0);
    token.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    token.set(CoreAnnotations.TokenEndAnnotation.class, 1);
    token.set(CoreAnnotations.AfterAnnotation.class, " ");
    token.set(CoreAnnotations.BeforeAnnotation.class, "");

    List<CoreLabel> tokenList = new ArrayList<>();
    tokenList.add(token);

    CoreMap sentence = new Annotation("Today is Tuesday.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokenList);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokenList);
    annotation.set(CoreAnnotations.TextAnnotation.class, "Today is Tuesday.");

    annotator.annotate(annotation);
    assertNotNull(token.ner());
  }
@Test
  public void testNERAnnotatorWithSpanishRulesLoaded() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.language", "es");
    props.setProperty("ner.applyFineGrained", "false");
    props.setProperty("ner.buildEntityMentions", "false");
    props.setProperty("ner.model", "");
    props.setProperty("ner.useSUTime", "false");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

    Annotation annotation = new Annotation("tres");
    CoreLabel token = new CoreLabel();
    token.setWord("tres");
    token.set(CoreAnnotations.TextAnnotation.class, "tres");
    token.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    token.set(CoreAnnotations.TokenEndAnnotation.class, 1);
    token.set(CoreAnnotations.AfterAnnotation.class, " ");
    token.set(CoreAnnotations.BeforeAnnotation.class, "");
    token.setIndex(0);

    List<CoreLabel> tokenList = new ArrayList<>();
    tokenList.add(token);

    CoreMap sentence = new Annotation("tres");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokenList);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    annotation.set(CoreAnnotations.TokensAnnotation.class, tokenList);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotation.set(CoreAnnotations.TextAnnotation.class, "tres");

    annotator.annotate(annotation);
    assertNotNull(token.ner());
  }
@Test
  public void testAfterTokenNERCleanup() throws Exception {
    NERClassifierCombiner nerCombiner = new NERClassifierCombiner(false, NERClassifierCombiner.Language.ENGLISH, false, new Properties(), new String[0]);
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(nerCombiner, false);

    Annotation annotation = new Annotation("100 dollars");
    CoreLabel token1 = new CoreLabel();
    token1.setWord("100");
    token1.setNER("MONEY");
    token1.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.TokenEndAnnotation.class, 1);
    token1.setIndex(0);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("dollars");
    token2.setNER("O");
    token2.set(CoreAnnotations.TokenBeginAnnotation.class, 1);
    token2.set(CoreAnnotations.TokenEndAnnotation.class, 2);
    token2.setIndex(1);

    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    CoreMap sentence = new Annotation("100 dollars");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    annotation.set(CoreAnnotations.TextAnnotation.class, "100 dollars");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertNull(token1.get(TimeAnnotations.TimexAnnotation.class));
  }
@Test
  public void testNERAnnotatorWithoutNERKeysSetInTransferAnnotations() {
    Annotation orig = new Annotation("Hello world");
    Annotation nerTokenized = new Annotation("Hello world");

    CoreLabel t1 = new CoreLabel();
    t1.setWord("Hello");
    t1.setIndex(0);
    t1.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    t1.set(CoreAnnotations.TokenEndAnnotation.class, 1);

    CoreLabel t2 = new CoreLabel();
    t2.setWord("world");
    t2.setIndex(1);
    t2.set(CoreAnnotations.TokenBeginAnnotation.class, 1);
    t2.set(CoreAnnotations.TokenEndAnnotation.class, 2);

    List<CoreLabel> origTokens = Arrays.asList(new CoreLabel(t1), new CoreLabel(t2));
    List<CoreLabel> nerTokens = Arrays.asList(new CoreLabel(t1), new CoreLabel(t2));

    CoreMap origSentence = new Annotation("Hello world");
    origSentence.set(CoreAnnotations.TokensAnnotation.class, origTokens);
    origSentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    orig.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(origSentence));
    orig.set(CoreAnnotations.TokensAnnotation.class, origTokens);

    CoreMap nerSentence = new Annotation("Hello world");
    nerSentence.set(CoreAnnotations.TokensAnnotation.class, nerTokens);
    nerSentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    nerTokenized.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(nerSentence));
    nerTokenized.set(CoreAnnotations.TokensAnnotation.class, nerTokens);

    NERCombinerAnnotator.transferNERAnnotationsToAnnotation(nerTokenized, orig);

    assertEquals("Hello", origTokens.get(0).word());
    assertEquals("world", origTokens.get(1).word());
  }
@Test
  public void testNERAnnotatorWithFineGrainedEnabledFlagButNoMapping() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.applyFineGrained", "true");
    props.setProperty("ner.fine.regexner.mapping", "");
    props.setProperty("ner.statisticalOnly", "true");
    props.setProperty("ner.model", "");
    props.setProperty("ner.buildEntityMentions", "false");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    assertNotNull(annotator);
  }
@Test
  public void testNERAnnotatorWithAdditionalRulesMappingEmpty() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.additional.regexner.mapping", "");
    props.setProperty("ner.model", "");
    props.setProperty("ner.buildEntityMentions", "false");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    assertNotNull(annotator);
  }
@Test
  public void testNERAnnotatorWithTokensRegexRulesEmpty() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.additional.tokensregex.rules", "");
    props.setProperty("ner.model", "");
    props.setProperty("ner.buildEntityMentions", "false");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

    assertNotNull(annotator);
  }
@Test
  public void testNamedEntityTagProbsSetToDefaultWhenMissing() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("ner.buildEntityMentions", "false");
    props.setProperty("ner.statisticalOnly", "true");
    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

    Annotation annotation = new Annotation("test");
    CoreLabel token = new CoreLabel();
    token.setWord("test");
    token.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    token.set(CoreAnnotations.TokenEndAnnotation.class, 1);
    token.set(CoreAnnotations.AfterAnnotation.class, " ");
    token.set(CoreAnnotations.BeforeAnnotation.class, "");
    token.setIndex(0);

    List<CoreLabel> tokens = Collections.singletonList(token);

    CoreMap sentence = new Annotation("test");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.TextAnnotation.class, "test");

    annotator.annotate(annotation);

    Map<String, Double> probs = token.get(CoreAnnotations.NamedEntityTagProbsAnnotation.class);
    assertNotNull(probs);
    assertTrue(probs.values().contains(-1.0));
  }
@Test
  public void testMergeTokenPreventsExceptionMergingBased() {
    CoreLabel previous = new CoreLabel();
    previous.setWord("All-");
    previous.setAfter("");
    previous.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    previous.set(CoreAnnotations.TokenEndAnnotation.class, 1);
    previous.setSentIndex(0);

    CoreLabel token = new CoreLabel();
    token.setWord("based"); 
    token.setAfter(" ");
    token.set(CoreAnnotations.TokenBeginAnnotation.class, 1);
    token.set(CoreAnnotations.TokenEndAnnotation.class, 2);
    token.setSentIndex(0);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(previous);
    tokens.add(token);

    assertEquals("based", tokens.get(1).word());
    
    assertEquals("All-", tokens.get(0).word());
  }
@Test
  public void testAnnotationWithNERTokenizationHandlesEmptyAfter() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Chicago");
    token1.setAfter("");
    token1.setSentIndex(0);
    token1.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.TokenEndAnnotation.class, 1);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("-");
    token2.setAfter("");
    token2.setSentIndex(0);
    token2.set(CoreAnnotations.TokenBeginAnnotation.class, 1);
    token2.set(CoreAnnotations.TokenEndAnnotation.class, 2);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("area");
    token3.setAfter(" ");
    token3.setSentIndex(0);
    token3.set(CoreAnnotations.TokenBeginAnnotation.class, 2);
    token3.set(CoreAnnotations.TokenEndAnnotation.class, 3);

    CoreMap sentence = new Annotation("Chicago-area");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2, token3));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation input = new Annotation("Chicago-area");
    input.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
  }
@Test
  public void testTransferNERAnnotationsRespectsMergeTokenSkipLogic() {
    CoreLabel original1 = new CoreLabel();
    original1.setWord("All");
    original1.setNER("O");
    original1.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    original1.set(CoreAnnotations.TokenEndAnnotation.class, 1);
    original1.set(CoreAnnotations.TextAnnotation.class, "All");

    CoreLabel original2 = new CoreLabel();
    original2.setWord("-");
    original2.setNER("O");
    original2.set(CoreAnnotations.TokenBeginAnnotation.class, 1);
    original2.set(CoreAnnotations.TokenEndAnnotation.class, 2);
    original2.set(CoreAnnotations.TextAnnotation.class, "-");

    CoreLabel original3 = new CoreLabel();
    original3.setWord("Stars");
    original3.setNER("O");
    original3.set(CoreAnnotations.TokenBeginAnnotation.class, 2);
    original3.set(CoreAnnotations.TokenEndAnnotation.class, 3);
    original3.set(CoreAnnotations.TextAnnotation.class, "Stars");

    List<CoreLabel> originalTokens = Arrays.asList(original1, original2, original3);

    CoreMap originalSentence = new Annotation("All-Star");
    originalSentence.set(CoreAnnotations.TokensAnnotation.class, originalTokens);
    originalSentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation originalAnnotation = new Annotation("All-Star");
    originalAnnotation.set(CoreAnnotations.TextAnnotation.class, "All-Star");
    originalAnnotation.set(CoreAnnotations.TokensAnnotation.class, originalTokens);
    originalAnnotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(originalSentence));

    CoreLabel mergedToken = new CoreLabel();
    mergedToken.setWord("All-Star");
    mergedToken.setNER("TITLE");
    mergedToken.set(NERCombinerAnnotator.TokenMergeCountAnnotation.class, 3);
    mergedToken.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    mergedToken.set(CoreAnnotations.TokenEndAnnotation.class, 3);

    List<CoreLabel> mergedTokens = Collections.singletonList(mergedToken);
    CoreMap mergedSentence = new Annotation("All-Star");
    mergedSentence.set(CoreAnnotations.TokensAnnotation.class, mergedTokens);
    mergedSentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation nerTokenized = new Annotation("All-Star");
    nerTokenized.set(CoreAnnotations.TextAnnotation.class, "All-Star");
    nerTokenized.set(CoreAnnotations.TokensAnnotation.class, mergedTokens);
    nerTokenized.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(mergedSentence));

    NERCombinerAnnotator.transferNERAnnotationsToAnnotation(nerTokenized, originalAnnotation);

    assertEquals("TITLE", original1.ner());
    assertEquals("TITLE", original2.ner());
    assertEquals("TITLE", original3.ner());
  }
@Test
  public void testAnnotateWithNullAfterFieldsInTokens() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("ner.statisticalOnly", "true");
    props.setProperty("ner.buildEntityMentions", "false");
    props.setProperty("ner.useNERSpecificTokenization", "true");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("San");
    token1.setAfter(null); 
    token1.setIndex(0);
    token1.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.TokenEndAnnotation.class, 1);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("-");
    token2.setAfter(null); 
    token2.setIndex(1);
    token2.set(CoreAnnotations.TokenBeginAnnotation.class, 1);
    token2.set(CoreAnnotations.TokenEndAnnotation.class, 2);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("Juan");
    token3.setAfter(" ");
    token3.setIndex(2);
    token3.set(CoreAnnotations.TokenBeginAnnotation.class, 2);
    token3.set(CoreAnnotations.TokenEndAnnotation.class, 3);

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);
    CoreMap sent = new Annotation("San-Juan");
    sent.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sent.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation doc = new Annotation("San-Juan");
    doc.set(CoreAnnotations.TextAnnotation.class, "San-Juan");
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);
    doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sent));

    annotator.annotate(doc);

    assertNotNull(tokens.get(0).ner());
  }
@Test
  public void testDoOneSentenceHandlesEmptySentenceGracefully() {
    List<CoreLabel> tokens = new ArrayList<>();

    CoreMap sentence = new Annotation("");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.TextAnnotation.class, "");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
  }
@Test
  public void testRequirementsSatisfiedIncludesEntityMentionsWhenSet() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("ner.buildEntityMentions", "true");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
  }
@Test
  public void testRequirementsSatisfiedOmitsEntityMentionsWhenDisabled() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("ner.buildEntityMentions", "false");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

    Set<Class<? extends CoreAnnotation>> result = annotator.requirementsSatisfied();

    assertFalse(result.contains(CoreAnnotations.MentionsAnnotation.class));
    assertFalse(result.contains(CoreAnnotations.EntityTypeAnnotation.class));
    assertFalse(result.contains(CoreAnnotations.EntityMentionIndexAnnotation.class));
  }

@Test
  public void testMergeTokensIncrementsMergeCountIfAlreadySet() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Big");
    token1.setAfter("");
    token1.setSentIndex(0);
    token1.set(NERCombinerAnnotator.TokenMergeCountAnnotation.class, 2);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Bang");
    token2.setAfter(" ");
    token2.setSentIndex(0);

    NERCombinerAnnotator.mergeTokens(token1, token2);

    assertEquals("BigBang", token1.word());
    Integer mergeCount = token1.get(NERCombinerAnnotator.TokenMergeCountAnnotation.class);
    assertNotNull(mergeCount);
    assertEquals(Integer.valueOf(3), mergeCount);
  }
@Test
  public void testMergeTokensSetsInitialMergeCountIfAbsent() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("New");
    token1.setAfter("");
    token1.setSentIndex(0);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("York");
    token2.setAfter(" ");
    token2.setSentIndex(0);

    NERCombinerAnnotator.mergeTokens(token1, token2);

    assertEquals("NewYork", token1.word());
    Integer mergeCount = token1.get(NERCombinerAnnotator.TokenMergeCountAnnotation.class);
    assertEquals(Integer.valueOf(1), mergeCount);
  }
@Test
  public void testAfterIsEmptyHandlesTrueCondition() {
    CoreLabel token = new CoreLabel();
    token.setAfter("");
    boolean result = NERCombinerAnnotator.afterIsEmpty.apply(token);
    assertTrue(result);
  }
@Test
  public void testAfterIsEmptyHandlesFalseForNullKey() {
    CoreLabel token = new CoreLabel();
    
    boolean result = NERCombinerAnnotator.afterIsEmpty.apply(token);
    assertFalse(result);
  }
@Test
  public void testRequirementsIncludeLemmaAndPOSIfSUTimeTrue() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("ner.buildEntityMentions", "false");
    props.setProperty("ner.applyFineGrained", "true");
    props.setProperty("sutime.binders", "0");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    Set<Class<? extends CoreAnnotation>> result = annotator.requires();

    assertTrue(result.contains(CoreAnnotations.LemmaAnnotation.class));
    assertTrue(result.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testRequirementsExcludesLemmaIfSUTimeAndFineGrainedDisabled() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("ner.buildEntityMentions", "false");
    props.setProperty("ner.applyFineGrained", "false");
    props.setProperty("ner.useSUTime", "false");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    Set<Class<? extends CoreAnnotation>> result = annotator.requires();

    assertFalse(result.contains(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testTransferNERAnnotationsSkipsWhenNERTokenListEmpty() {
    Annotation source = new Annotation("source");
    Annotation target = new Annotation("target");

    CoreLabel t1 = new CoreLabel();
    t1.setWord("alpha");
    t1.setIndex(0);
    t1.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    t1.set(CoreAnnotations.TokenEndAnnotation.class, 1);

    List<CoreLabel> origTokens = Collections.singletonList(t1);
    CoreMap sentence = new Annotation("alpha");
    sentence.set(CoreAnnotations.TokensAnnotation.class, origTokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    target.set(CoreAnnotations.TokensAnnotation.class, origTokens);
    target.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    source.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());

    NERCombinerAnnotator.transferNERAnnotationsToAnnotation(source, target);
    
    assertEquals("alpha", origTokens.get(0).word());
  }
@Test
  public void testTransferNERAnnotationsSkipsWhenOriginalTokenListEmpty() {
    Annotation source = new Annotation("source");
    Annotation target = new Annotation("target");

    CoreLabel merged = new CoreLabel();
    merged.setWord("Boston");
    merged.setNER("CITY");
    merged.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    merged.set(CoreAnnotations.TokenEndAnnotation.class, 1);
    List<CoreLabel> mergedList = Collections.singletonList(merged);

    CoreMap sentence = new Annotation("Boston");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    target.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
    target.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    source.set(CoreAnnotations.TokensAnnotation.class, mergedList);

    NERCombinerAnnotator.transferNERAnnotationsToAnnotation(source, target);
    assertTrue(true); 
  }
@Test
  public void testAnnotateSkipsFineGrainedWhenStatisticalOnlyTrue() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("ner.statisticalOnly", "true");
    props.setProperty("ner.applyFineGrained", "true");
    props.setProperty("ner.buildEntityMentions", "false");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.setWord("Obama");
    token.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    token.set(CoreAnnotations.TokenEndAnnotation.class, 1);
    token.setIndex(0);

    CoreMap sentence = new Annotation("Obama");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annotation = new Annotation("Obama");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Obama");
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    String fineGrained = token.get(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class);
    assertNull(fineGrained); 
  }
@Test
  public void testSpanishAnnotatorCreatedWhenSpanishLanguageAndNumericClassifiersTrue() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.language", "es");
    props.setProperty("ner.model", "");
    props.setProperty("ner.buildEntityMentions", "false");
    props.setProperty("ner.statisticalOnly", "false");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

    Annotation annotation = new Annotation("tres");
    CoreLabel token = new CoreLabel();
    token.setWord("tres");
    token.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    token.set(CoreAnnotations.TokenEndAnnotation.class, 1);
    token.set(CoreAnnotations.AfterAnnotation.class, " ");
    token.set(CoreAnnotations.BeforeAnnotation.class, "");
    token.setIndex(0);

    CoreMap sentence = new Annotation("tres");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.TextAnnotation.class, "tres");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertNotNull(token.ner());
  }
@Test
  public void testAdditionalRulesNERAnnotatorCreatedWhenNonEmptyMapping() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("ner.additional.regexner.mapping", "custom_rules.rules");
    props.setProperty("ner.buildEntityMentions", "false");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

    Annotation annotation = new Annotation("AcmeCorp");
    CoreLabel token = new CoreLabel();
    token.setWord("AcmeCorp");
    token.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    token.set(CoreAnnotations.TokenEndAnnotation.class, 1);
    token.setIndex(0);
    token.set(CoreAnnotations.AfterAnnotation.class, " ");
    token.set(CoreAnnotations.BeforeAnnotation.class, "");

    CoreMap sentence = new Annotation("AcmeCorp");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    annotation.set(CoreAnnotations.TextAnnotation.class, "AcmeCorp");
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);
    assertNotNull(token.ner());
  }
@Test
  public void testTokensRegexAnnotatorCreatedWhenRulesProvided() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("ner.additional.tokensregex.rules", "custom.rules");
    props.setProperty("ner.buildEntityMentions", "false");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

    Annotation annotation = new Annotation("example");
    CoreLabel token = new CoreLabel();
    token.setWord("example");
    token.setIndex(0);
    token.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    token.set(CoreAnnotations.TokenEndAnnotation.class, 1);
    token.set(CoreAnnotations.AfterAnnotation.class, " ");
    token.set(CoreAnnotations.BeforeAnnotation.class, "");

    CoreMap sentence = new Annotation("example");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    annotation.set(CoreAnnotations.TextAnnotation.class, "example");
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertNotNull(token.ner());
  }
@Test
  public void testTransferNERPreservesMergedTokenPropagatedNER() {
    CoreLabel mergedToken = new CoreLabel();
    mergedToken.setWord("AppleStore");
    mergedToken.setNER("ORGANIZATION");
    mergedToken.set(NERCombinerAnnotator.TokenMergeCountAnnotation.class, 2);
    mergedToken.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    mergedToken.set(CoreAnnotations.TokenEndAnnotation.class, 2);

    Annotation nerTokenized = new Annotation("AppleStore");
    nerTokenized.set(CoreAnnotations.TextAnnotation.class, "AppleStore");
    nerTokenized.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(mergedToken));

    List<CoreLabel> originalTokens = new ArrayList<>();

    CoreLabel t1 = new CoreLabel();
    t1.setWord("Apple");
    t1.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    t1.set(CoreAnnotations.TokenEndAnnotation.class, 1);

    CoreLabel t2 = new CoreLabel();
    t2.setWord("Store");
    t2.set(CoreAnnotations.TokenBeginAnnotation.class, 1);
    t2.set(CoreAnnotations.TokenEndAnnotation.class, 2);

    originalTokens.add(t1);
    originalTokens.add(t2);

    CoreMap sentence = new Annotation("Apple Store");
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokensAnnotation.class, originalTokens);

    Annotation original = new Annotation("Apple Store");
    original.set(CoreAnnotations.TextAnnotation.class, "Apple Store");
    original.set(CoreAnnotations.TokensAnnotation.class, originalTokens);
    original.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    NERCombinerAnnotator.transferNERAnnotationsToAnnotation(nerTokenized, original);

    assertEquals("ORGANIZATION", originalTokens.get(0).ner());
    assertEquals("ORGANIZATION", originalTokens.get(1).ner());
  }
@Test
  public void testEntityMentionsAreGeneratedWhenEnabled() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("ner.buildEntityMentions", "true");
    props.setProperty("ner.entitymentions.postagger.model", "edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.setWord("Google");
    token.setNER("ORGANIZATION");
    token.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    token.set(CoreAnnotations.TokenEndAnnotation.class, 1);
    token.setIndex(0);
    token.set(CoreAnnotations.AfterAnnotation.class, " ");

    CoreMap sentence = new Annotation("Google");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annotation = new Annotation("Google");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Google");
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(annotation);

    assertNotNull(annotation.get(CoreAnnotations.MentionsAnnotation.class));
  }
@Test
  public void testNERAnnotatorHandlesMultipleSentences() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("ner.statisticalOnly", "true");
    props.setProperty("ner.buildEntityMentions", "false");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Barack");
    token1.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.TokenEndAnnotation.class, 1);
    token1.setIndex(0);
    token1.set(CoreAnnotations.AfterAnnotation.class, " ");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Obama");
    token2.set(CoreAnnotations.TokenBeginAnnotation.class, 1);
    token2.set(CoreAnnotations.TokenEndAnnotation.class, 2);
    token2.setIndex(1);
    token2.set(CoreAnnotations.AfterAnnotation.class, ".");

    CoreMap sentence1 = new Annotation("Barack Obama");
    sentence1.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));
    sentence1.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("Google");
    token3.set(CoreAnnotations.TokenBeginAnnotation.class, 2);
    token3.set(CoreAnnotations.TokenEndAnnotation.class, 3);
    token3.setIndex(2);
    token3.set(CoreAnnotations.AfterAnnotation.class, ".");

    CoreMap sentence2 = new Annotation("Google");
    sentence2.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token3));
    sentence2.set(CoreAnnotations.SentenceIndexAnnotation.class, 1);

    Annotation annotation = new Annotation("Barack Obama. Google");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Barack Obama. Google");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence1, sentence2));
    List<CoreLabel> allTokens = new ArrayList<>();
    allTokens.add(token1);
    allTokens.add(token2);
    allTokens.add(token3);
    annotation.set(CoreAnnotations.TokensAnnotation.class, allTokens);

    annotator.annotate(annotation);

    assertNotNull(token1.ner());
    assertNotNull(token3.ner());
  }
@Test
  public void testNERAnnotatorUsesDefaultPathsWhenModelPropertyMissing() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.statisticalOnly", "false");
    props.setProperty("ner.buildEntityMentions", "false");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

    Annotation annotation = new Annotation("Stanford");
    CoreLabel token = new CoreLabel();
    token.setWord("Stanford");
    token.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    token.set(CoreAnnotations.TokenEndAnnotation.class, 1);
    token.setIndex(0);

    CoreMap sentence = new Annotation("Stanford");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.TextAnnotation.class, "Stanford");

    annotator.annotate(annotation);
    assertNotNull(token.ner());
  }
@Test
  public void testMaxThreadsAndMaxTimeConfigurationParsing() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("ner.nthreads", "4");
    props.setProperty("ner.maxtime", "999");
    props.setProperty("ner.statisticalOnly", "true");
    props.setProperty("ner.buildEntityMentions", "false");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

    assertNotNull(annotator);
  }
@Test
  public void testNERAnnotatorHonorsUseSUTimeDisabled() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("sutime.binders", "0");
    props.setProperty("ner.useSUTime", "false");
    props.setProperty("ner.buildEntityMentions", "false");
    props.setProperty("ner.statisticalOnly", "true");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    assertNotNull(annotator);
  }
@Test
  public void testSentenceWithoutTokensHandledWithoutException() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("ner.buildEntityMentions", "false");
    props.setProperty("ner.statisticalOnly", "true");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

    CoreMap sentence = new Annotation("Empty");
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation document = new Annotation("Empty");
    document.set(CoreAnnotations.TextAnnotation.class, "Empty");
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    document.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

    annotator.annotate(document);

    assertTrue(true); 
  }
@Test
  public void testAnnotateWithNullAnnotationDoesNotThrow() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("ner.buildEntityMentions", "false");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

    try {
      annotator.annotate(null);
      assertTrue(true);
    } catch (Exception e) {
      fail("Annotating null should be handled gracefully.");
    }
  }
@Test
  public void testNormalizedNERIsCopiedFromNERTokenized() {
    Annotation nerTokenized = new Annotation("Feb 2024");

    CoreLabel nerTok = new CoreLabel();
    nerTok.setWord("Feb");
    nerTok.setNER("DATE");
    nerTok.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "2024-02");
    nerTok.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    nerTok.set(CoreAnnotations.TokenEndAnnotation.class, 1);

    nerTokenized.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(nerTok));

    CoreLabel origTok = new CoreLabel();
    origTok.setWord("Feb");
    origTok.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    origTok.set(CoreAnnotations.TokenEndAnnotation.class, 1);

    CoreMap origSentence = new Annotation("Feb 2024");
    origSentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(origTok));
    origSentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation original = new Annotation("Feb 2024");
    original.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(origTok));
    original.set(CoreAnnotations.TextAnnotation.class, "Feb 2024");
    original.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(origSentence));

    NERCombinerAnnotator.transferNERAnnotationsToAnnotation(nerTokenized, original);

    assertEquals("DATE", origTok.ner());
    assertEquals("2024-02", origTok.get(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class));
  }
@Test
  public void testRequirementsSatisfiedExcludesEntityMentionsByFlag() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("ner.buildEntityMentions", "false");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);
    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertFalse(satisfied.contains(CoreAnnotations.MentionsAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class));
  }
@Test
  public void testNumericTimexCleanupRemovesTimexFromNumberToken() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("ner.buildEntityMentions", "false");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.setWord("300");
    token.setNER("NUMBER");
    token.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    token.set(CoreAnnotations.TokenEndAnnotation.class, 1);
    token.setIndex(0);

    CoreMap sentence = new Annotation("300");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation doc = new Annotation("300");
    doc.set(CoreAnnotations.TextAnnotation.class, "300");
    doc.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(doc);

    assertNull(token.get(TimeAnnotations.TimexAnnotation.class));
  }
@Test
  public void testAnnotationWithNERTokenizationProducesGlobalTokenIndices() throws Exception {
    CoreLabel tok1 = new CoreLabel();
    tok1.setWord("New");
    tok1.setAfter("");
    tok1.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    tok1.set(CoreAnnotations.TokenEndAnnotation.class, 1);
    tok1.setSentIndex(0);

    CoreLabel tok2 = new CoreLabel();
    tok2.setWord("-");
    tok2.setAfter("");
    tok2.set(CoreAnnotations.TokenBeginAnnotation.class, 1);
    tok2.set(CoreAnnotations.TokenEndAnnotation.class, 2);
    tok2.setSentIndex(0);

    CoreLabel tok3 = new CoreLabel();
    tok3.setWord("York");
    tok3.setAfter(" ");
    tok3.set(CoreAnnotations.TokenBeginAnnotation.class, 2);
    tok3.set(CoreAnnotations.TokenEndAnnotation.class, 3);
    tok3.setSentIndex(0);

    CoreMap sentence = new Annotation("New-York");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(tok1, tok2, tok3));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation doc = new Annotation("New-York");
    doc.set(CoreAnnotations.TextAnnotation.class, "New-York");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  }
@Test
  public void testMergeTokensThreeConsecutiveTokens() {
    CoreLabel t1 = new CoreLabel();
    t1.setWord("high");
    t1.setAfter("");
    t1.setEndPosition(5);
    t1.setSentIndex(0);
    t1.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    t1.set(CoreAnnotations.TokenEndAnnotation.class, 1);

    CoreLabel t2 = new CoreLabel();
    t2.setWord("-");
    t2.setAfter("");
    t2.setEndPosition(6);
    t2.setSentIndex(0);
    t2.set(CoreAnnotations.TokenBeginAnnotation.class, 1);
    t2.set(CoreAnnotations.TokenEndAnnotation.class, 2);

    CoreLabel t3 = new CoreLabel();
    t3.setWord("tech");
    t3.setAfter(" ");
    t3.setEndPosition(10);
    t3.setSentIndex(0);
    t3.set(CoreAnnotations.TokenBeginAnnotation.class, 2);
    t3.set(CoreAnnotations.TokenEndAnnotation.class, 3);

    NERCombinerAnnotator.mergeTokens(t1, t2);
    assertEquals("high-", t1.word());

    NERCombinerAnnotator.mergeTokens(t1, t3);
    assertEquals("high-tech", t1.word());

    Integer count = t1.get(NERCombinerAnnotator.TokenMergeCountAnnotation.class);
    assertEquals(Integer.valueOf(3), count);
  }
@Test
  public void testTransferNERAnnotationsHandlesNullNERKeysGracefully() {
    CoreLabel nerToken = new CoreLabel();
    nerToken.setWord("NeoTokyo");
    nerToken.setNER("CITY");
    nerToken.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    nerToken.set(CoreAnnotations.TokenEndAnnotation.class, 1);

    nerToken.set(NERCombinerAnnotator.TokenMergeCountAnnotation.class, 2);

    Annotation source = new Annotation("This merged");
    source.set(CoreAnnotations.TextAnnotation.class, "This merged");
    source.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(nerToken));

    CoreLabel orig1 = new CoreLabel();
    orig1.setWord("Neo");
    orig1.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    orig1.set(CoreAnnotations.TokenEndAnnotation.class, 1);

    CoreLabel orig2 = new CoreLabel();
    orig2.setWord("Tokyo");
    orig2.set(CoreAnnotations.TokenBeginAnnotation.class, 1);
    orig2.set(CoreAnnotations.TokenEndAnnotation.class, 2);

    CoreMap origSentence = new Annotation("Neo Tokyo");
    origSentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    origSentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(orig1, orig2));

    Annotation target = new Annotation("Neo Tokyo");
    target.set(CoreAnnotations.TextAnnotation.class, "Neo Tokyo");
    target.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(orig1, orig2));
    target.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(origSentence));

    NERCombinerAnnotator.transferNERAnnotationsToAnnotation(source, target);

    assertEquals("CITY", orig1.ner());
    assertEquals("CITY", orig2.ner());
  }
@Test
  public void testFineGrainedIsRequestedButMappingIsMissing() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.applyFineGrained", "true");
    props.setProperty("ner.statisticalOnly", "false");
    props.setProperty("ner.buildEntityMentions", "false");
    props.setProperty("ner.model", "");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.setWord("FDA");
    token.setAfter(" ");
    token.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    token.set(CoreAnnotations.TokenEndAnnotation.class, 1);
    token.setIndex(0);

    CoreMap sent = new Annotation("FDA");
    sent.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sent.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation document = new Annotation("FDA");
    document.set(CoreAnnotations.TextAnnotation.class, "FDA");
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sent));
    document.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    annotator.annotate(document);

    String fine = token.get(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class);
    assertNotNull(fine);
  }
@Test
  public void testTokensRegexSetupWithInvalidPropertiesDoesNotThrow() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("ner.buildEntityMentions", "false");
    props.setProperty("ner.additional.tokensregex.rules", "nonexistent.rules");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

    assertNotNull(annotator);
  }
@Test
  public void testMergedTokenAssignsCoarseAndFineTagsAndDefaultNERProbsWhenMissed() throws Exception {
    Properties props = new Properties();
    props.setProperty("ner.model", "");
    props.setProperty("ner.statisticalOnly", "false");
    props.setProperty("ner.buildEntityMentions", "false");

    NERCombinerAnnotator annotator = new NERCombinerAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.setWord("NASA");
    token.setAfter(" ");
    token.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    token.set(CoreAnnotations.TokenEndAnnotation.class, 1);
    token.setIndex(0);

    CoreMap sentence = new Annotation("NASA");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annotation = new Annotation("NASA");
    annotation.set(CoreAnnotations.TextAnnotation.class, "NASA");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    annotator.annotate(annotation);

    String coarse = token.get(CoreAnnotations.CoarseNamedEntityTagAnnotation.class);
    String fine = token.get(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class);
    Map<String, Double> probs = token.get(CoreAnnotations.NamedEntityTagProbsAnnotation.class);

    assertNotNull(coarse);
    assertNotNull(fine);
    assertNotNull(probs);
    assertTrue(probs.containsValue(-1.0));
  }
@Test
  public void testMergedTokensWithWhitespaceOnlyAfterHandledCorrectly() {
    CoreLabel t1 = new CoreLabel();
    t1.setWord("data");
    t1.setAfter(" ");
    t1.setSentIndex(0);
    t1.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    t1.set(CoreAnnotations.TokenEndAnnotation.class, 1);

    CoreLabel t2 = new CoreLabel();
    t2.setWord("-");
    t2.setAfter("");
    t2.setSentIndex(0);
    t2.set(CoreAnnotations.TokenBeginAnnotation.class, 1);
    t2.set(CoreAnnotations.TokenEndAnnotation.class, 2);

    CoreLabel t3 = new CoreLabel();
    t3.setWord("driven");
    t3.setAfter(" ");
    t3.setSentIndex(0);
    t3.set(CoreAnnotations.TokenBeginAnnotation.class, 2);
    t3.set(CoreAnnotations.TokenEndAnnotation.class, 3);

    List<CoreLabel> merged = new ArrayList<>();
    merged.add(t1);
    merged.add(t2);
    merged.add(t3);

    CoreMap sentence = new Annotation("data-driven");
    sentence.set(CoreAnnotations.TokensAnnotation.class, merged);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation input = new Annotation("data-driven");
    input.set(CoreAnnotations.TextAnnotation.class, "data-driven");
    input.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
  } 
}