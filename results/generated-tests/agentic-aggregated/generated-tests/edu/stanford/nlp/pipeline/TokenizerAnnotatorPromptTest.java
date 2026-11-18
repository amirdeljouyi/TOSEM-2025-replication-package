package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.util.CoreMap;
import org.junit.Test;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import static org.junit.Assert.*;

public class TokenizerAnnotatorPromptTest {

  @Test
  public void tokenizerTypeDefaultsToEnglish() {
    Properties properties = new Properties();

    assertEquals(TokenizerAnnotator.TokenizerType.English,
        TokenizerAnnotator.TokenizerType.getTokenizerType(properties));
  }

  @Test
  public void tokenizerTypeRecognizesLanguageAbbreviationCaseInsensitively() {
    Properties properties = new Properties();
    properties.setProperty("tokenize.language", "dE");

    assertEquals(TokenizerAnnotator.TokenizerType.German,
        TokenizerAnnotator.TokenizerType.getTokenizerType(properties));
  }

  @Test
  public void tokenizerTypeRecognizesFullLanguageNameCaseInsensitively() {
    Properties properties = new Properties();
    properties.setProperty("tokenize.language", "sPaNiSh");

    assertEquals(TokenizerAnnotator.TokenizerType.Spanish,
        TokenizerAnnotator.TokenizerType.getTokenizerType(properties));
  }

  @Test
  public void tokenizerClassOverridesLanguage() {
    Properties properties = new Properties();
    properties.setProperty("tokenize.language", "fr");
    properties.setProperty("tokenize.class", "PTBTokenizer");

    assertEquals(TokenizerAnnotator.TokenizerType.English,
        TokenizerAnnotator.TokenizerType.getTokenizerType(properties));
  }

  @Test
  public void whitespaceFlagOverridesClassAndLanguage() {
    Properties properties = new Properties();
    properties.setProperty("tokenize.whitespace", "true");
    properties.setProperty("tokenize.class", "not-a-tokenizer");
    properties.setProperty("tokenize.language", "not-a-language");

    assertEquals(TokenizerAnnotator.TokenizerType.Whitespace,
        TokenizerAnnotator.TokenizerType.getTokenizerType(properties));
  }

  @Test(expected = IllegalArgumentException.class)
  public void unknownTokenizerClassIsRejected() {
    Properties properties = new Properties();
    properties.setProperty("tokenize.class", "MissingTokenizer");

    TokenizerAnnotator.TokenizerType.getTokenizerType(properties);
  }

  @Test(expected = IllegalArgumentException.class)
  public void unknownTokenizerLanguageIsRejected() {
    Properties properties = new Properties();
    properties.setProperty("tokenize.language", "klingon");

    TokenizerAnnotator.TokenizerType.getTokenizerType(properties);
  }

  @Test
  public void tokenizerTypeDefaultOptionsExposeLanguageSpecificChoices() {
    assertEquals("invertible", TokenizerAnnotator.TokenizerType.English.getDefaultOptions());
    assertEquals("", TokenizerAnnotator.TokenizerType.Whitespace.getDefaultOptions());
    assertTrue(TokenizerAnnotator.TokenizerType.German.getDefaultOptions().contains("splitHyphenated=true"));
    assertEquals("", TokenizerAnnotator.TokenizerType.Chinese.getDefaultOptions());
  }

  @Test
  public void directTokenizerSupportsPeekNextAndTokenizeAcrossInstances() {
    Properties properties = new Properties();
    properties.setProperty("tokenize.ssplit", "false");
    TokenizerAnnotator annotator = new TokenizerAnnotator(false, properties);
    Tokenizer<CoreLabel> first = annotator.getTokenizer(new StringReader("One, two."));
    Tokenizer<CoreLabel> second = annotator.getTokenizer(new StringReader("Three!"));

    assertEquals("One", first.peek().word());
    assertEquals("One", first.next().word());
    assertEquals(",", first.next().word());
    assertEquals("two", first.next().word());
    assertEquals(".", first.next().word());
    assertFalse(first.hasNext());
    assertEquals("Three", second.next().word());
    assertEquals("!", second.next().word());
    assertFalse(second.hasNext());
  }

  @Test
  public void explicitPtbOptionsCanDisableParenthesisEscaping() {
    TokenizerAnnotator annotator = new TokenizerAnnotator(false, "en",
        "invertible,ptb3Escaping=false");
    List<CoreLabel> tokens = annotator.getTokenizer(new StringReader("(x)")).tokenize();

    assertEquals(3, tokens.size());
    assertEquals("(", tokens.get(0).word());
    assertEquals("x", tokens.get(1).word());
    assertEquals(")", tokens.get(2).word());
  }

  @Test
  public void germanConstructorUsesHyphenSplittingDefault() {
    TokenizerAnnotator annotator = new TokenizerAnnotator(false,
        TokenizerAnnotator.TokenizerType.German);
    List<CoreLabel> tokens = annotator.getTokenizer(new StringReader("Berlin-Brandenburg")).tokenize();

    assertEquals(3, tokens.size());
    assertEquals("Berlin", tokens.get(0).word());
    assertEquals("-", tokens.get(1).word());
    assertEquals("Brandenburg", tokens.get(2).word());
  }

  @Test
  public void whitespaceTokenizerKeepsPunctuationAttached() {
    Properties properties = new Properties();
    properties.setProperty("tokenize.whitespace", "true");
    properties.setProperty("tokenize.ssplit", "false");
    TokenizerAnnotator annotator = new TokenizerAnnotator(properties);
    List<CoreLabel> tokens = annotator.getTokenizer(new StringReader("alpha,beta  gamma!")).tokenize();

    assertEquals(2, tokens.size());
    assertEquals("alpha,beta", tokens.get(0).word());
    assertEquals("gamma!", tokens.get(1).word());
  }

  @Test
  public void annotateCreatesInvertibleTokensAndDocumentIndexes() {
    Properties properties = new Properties();
    properties.setProperty("tokenize.ssplit", "false");
    TokenizerAnnotator annotator = new TokenizerAnnotator(false, properties);
    Annotation document = new Annotation("  Hello, world!  ");

    annotator.annotate(document);

    List<CoreLabel> tokens = document.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(4, tokens.size());
    assertEquals("Hello", tokens.get(0).word());
    assertEquals("  ", tokens.get(0).before());
    assertEquals("Hello", tokens.get(0).originalText());
    assertEquals(2, tokens.get(0).beginPosition());
    assertEquals(7, tokens.get(0).endPosition());
    assertEquals(Integer.valueOf(0), tokens.get(0).get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals(Integer.valueOf(1), tokens.get(0).get(CoreAnnotations.TokenEndAnnotation.class));
    assertEquals(",", tokens.get(1).word());
    assertEquals(" ", tokens.get(1).after());
    assertEquals("world", tokens.get(2).word());
    assertEquals("!", tokens.get(3).word());
    assertEquals("  ", tokens.get(3).after());
    assertEquals(Integer.valueOf(3), tokens.get(3).get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals(Integer.valueOf(4), tokens.get(3).get(CoreAnnotations.TokenEndAnnotation.class));
    assertEquals(Boolean.FALSE, tokens.get(3).get(CoreAnnotations.IsNewlineAnnotation.class));
    assertFalse(document.containsKey(CoreAnnotations.SentencesAnnotation.class));
  }

  @Test
  public void annotateEmptyTextProducesEmptyTokenList() {
    Properties properties = new Properties();
    properties.setProperty("tokenize.ssplit", "false");
    Annotation document = new Annotation("");

    new TokenizerAnnotator(properties).annotate(document);

    assertNotNull(document.get(CoreAnnotations.TokensAnnotation.class));
    assertTrue(document.get(CoreAnnotations.TokensAnnotation.class).isEmpty());
  }

  @Test(expected = RuntimeException.class)
  public void annotateRejectsAnnotationWithoutText() {
    Properties properties = new Properties();
    properties.setProperty("tokenize.ssplit", "false");
    Annotation document = new Annotation();

    new TokenizerAnnotator(properties).annotate(document);
  }

  @Test
  public void annotateReplacesPreexistingTokensAndRemovesStaleSentences() {
    Properties properties = new Properties();
    properties.setProperty("tokenize.ssplit", "false");
    Annotation document = new Annotation("new text");
    document.set(CoreAnnotations.TokensAnnotation.class,
        Collections.singletonList(CoreLabel.wordFromString("stale")));
    List<CoreMap> staleSentences = new ArrayList<>();
    staleSentences.add(new Annotation("stale sentence"));
    document.set(CoreAnnotations.SentencesAnnotation.class, staleSentences);

    new TokenizerAnnotator(properties).annotate(document);

    assertEquals(2, document.get(CoreAnnotations.TokensAnnotation.class).size());
    assertEquals("new", document.get(CoreAnnotations.TokensAnnotation.class).get(0).word());
    assertEquals("text", document.get(CoreAnnotations.TokensAnnotation.class).get(1).word());
    assertFalse(document.containsKey(CoreAnnotations.SentencesAnnotation.class));
  }

  @Test
  public void defaultSentenceSplitterCreatesMultipleSentences() {
    Annotation document = new Annotation("First sentence. Second sentence!");

    new TokenizerAnnotator().annotate(document);

    List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(2, sentences.size());
    assertEquals("First sentence.", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("Second sentence!", sentences.get(1).get(CoreAnnotations.TextAnnotation.class));
    assertEquals(Integer.valueOf(0),
        sentences.get(0).get(CoreAnnotations.SentenceIndexAnnotation.class));
    assertEquals(Integer.valueOf(1),
        sentences.get(1).get(CoreAnnotations.SentenceIndexAnnotation.class));
  }

  @Test
  public void keepEolWhitespaceTokenIsMarkedAsNewline() {
    Properties properties = new Properties();
    properties.setProperty("tokenize.whitespace", "true");
    properties.setProperty("tokenize.keepeol", "true");
    properties.setProperty("tokenize.ssplit", "false");
    Annotation document = new Annotation("alpha\r\nbeta");

    new TokenizerAnnotator(properties).annotate(document);

    List<CoreLabel> tokens = document.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(3, tokens.size());
    assertEquals("alpha", tokens.get(0).word());
    assertEquals("*NL*", tokens.get(1).word());
    assertEquals("*NL*", tokens.get(1).originalText());
    assertEquals(Boolean.TRUE, tokens.get(1).get(CoreAnnotations.IsNewlineAnnotation.class));
    assertEquals(Boolean.FALSE, tokens.get(2).get(CoreAnnotations.IsNewlineAnnotation.class));
  }

  @Test
  public void newlineSentenceBreakOptionPreservesNewlineWithoutExplicitKeepEol() {
    Properties properties = new Properties();
    properties.setProperty("tokenize.whitespace", "true");
    properties.setProperty("ssplit.newlineIsSentenceBreak", "always");
    Annotation document = new Annotation("alpha\nbeta");

    new TokenizerAnnotator(properties).annotate(document);

    List<CoreLabel> tokens = document.get(CoreAnnotations.TokensAnnotation.class);
    List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(2, tokens.size());
    assertEquals("alpha", tokens.get(0).word());
    assertEquals("beta", tokens.get(1).word());
    assertEquals(2, sentences.size());
    assertEquals("alpha", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("beta", sentences.get(1).get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void codepointPostProcessorTracksSupplementaryCharacters() {
    Properties properties = new Properties();
    properties.setProperty("tokenize.ssplit", "false");
    properties.setProperty("tokenize.codepoint", "true");
    Annotation document = new Annotation("😀 x");

    new TokenizerAnnotator(properties).annotate(document);

    List<CoreLabel> tokens = document.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(2, tokens.size());
    assertEquals(0, tokens.get(0).beginPosition());
    assertEquals(2, tokens.get(0).endPosition());
    assertEquals(Integer.valueOf(0),
        tokens.get(0).get(CoreAnnotations.CodepointOffsetBeginAnnotation.class));
    assertEquals(Integer.valueOf(1),
        tokens.get(0).get(CoreAnnotations.CodepointOffsetEndAnnotation.class));
    assertEquals(Integer.valueOf(2),
        tokens.get(1).get(CoreAnnotations.CodepointOffsetBeginAnnotation.class));
    assertEquals(Integer.valueOf(3),
        tokens.get(1).get(CoreAnnotations.CodepointOffsetEndAnnotation.class));
  }

  @Test
  public void annotatorWorksWhenInvokedThroughAnnotationPipeline() {
    Properties properties = new Properties();
    properties.setProperty("tokenize.whitespace", "true");
    AnnotationPipeline pipeline = new AnnotationPipeline();
    pipeline.addAnnotator(new TokenizerAnnotator(properties));
    Annotation firstDocument = new Annotation("one two.");
    Annotation secondDocument = new Annotation("three four!");

    pipeline.annotate(firstDocument);
    pipeline.annotate(secondDocument);

    assertEquals(2, firstDocument.get(CoreAnnotations.TokensAnnotation.class).size());
    assertEquals("one", firstDocument.get(CoreAnnotations.TokensAnnotation.class).get(0).word());
    assertEquals(1, firstDocument.get(CoreAnnotations.SentencesAnnotation.class).size());
    assertEquals(2, secondDocument.get(CoreAnnotations.TokensAnnotation.class).size());
    assertEquals("four!", secondDocument.get(CoreAnnotations.TokensAnnotation.class).get(1).word());
    assertEquals(1, secondDocument.get(CoreAnnotations.SentencesAnnotation.class).size());
  }

  @Test
  public void repeatedAnnotationUsesCurrentTextAndFreshIndexes() {
    Properties properties = new Properties();
    properties.setProperty("tokenize.ssplit", "false");
    TokenizerAnnotator annotator = new TokenizerAnnotator(false, properties, null);
    Annotation document = new Annotation("old");
    annotator.annotate(document);
    document.set(CoreAnnotations.TextAnnotation.class, "fresh tokens");

    annotator.annotate(document);

    List<CoreLabel> tokens = document.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(2, tokens.size());
    assertEquals("fresh", tokens.get(0).word());
    assertEquals(Integer.valueOf(0), tokens.get(0).get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals("tokens", tokens.get(1).word());
    assertEquals(Integer.valueOf(2), tokens.get(1).get(CoreAnnotations.TokenEndAnnotation.class));
  }

  @Test
  public void adjustFinalTokenAcceptsNullAndEmptyLists() {
    List<CoreLabel> empty = new ArrayList<>();

    TokenizerAnnotator.adjustFinalToken(null);
    TokenizerAnnotator.adjustFinalToken(empty);

    assertTrue(empty.isEmpty());
  }

  @Test
  public void adjustFinalTokenRemovesExactlyOneSyntheticSpace() {
    CoreLabel first = CoreLabel.wordFromString("first");
    first.setAfter("unchanged");
    CoreLabel last = CoreLabel.wordFromString("last");
    last.setAfter(" \t ");
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(first);
    tokens.add(last);

    TokenizerAnnotator.adjustFinalToken(tokens);

    assertEquals("unchanged", first.after());
    assertEquals(" \t", last.after());
  }

  @Test
  public void adjustFinalTokenLeavesNullAndEmptyAfterValuesUntouched() {
    CoreLabel first = CoreLabel.wordFromString("first");
    CoreLabel last = CoreLabel.wordFromString("last");
    last.setAfter("");
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(first);
    tokens.add(last);

    TokenizerAnnotator.adjustFinalToken(tokens);
    assertEquals("", first.after());
    assertEquals("", last.after());
  }

  @Test(expected = IllegalArgumentException.class)
  public void adjustFinalTokenRejectsUnexpectedTrailingCharacter() {
    CoreLabel token = CoreLabel.wordFromString("word");
    token.setAfter("\n");

    TokenizerAnnotator.adjustFinalToken(Collections.singletonList(token));
  }

  @Test
  public void annotatorContractReportsNoPrerequisitesAndCompleteOutputs() {
    TokenizerAnnotator annotator = new TokenizerAnnotator(false);
    Set<Class<? extends CoreAnnotation>> required = annotator.requires();
    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertTrue(required.isEmpty());
    assertEquals(15, satisfied.size());
    assertTrue(satisfied.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.CharacterOffsetEndAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.BeforeAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.AfterAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.TokenBeginAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.TokenEndAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.PositionAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.IndexAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.OriginalTextAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.ValueAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.IsNewlineAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.SentencesAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.SentenceIndexAnnotation.class));
  }
}
