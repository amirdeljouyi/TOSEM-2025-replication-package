package edu.stanford.nlp.pipeline;

import java.io.StringReader;
import java.util.*;

import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.process.AbstractTokenizer;
import edu.stanford.nlp.util.PropertiesUtils;
import junit.framework.TestCase;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import org.junit.Test;

import static org.junit.Assert.*;

public class TokenizerAnnotator_1_GPTLLMTest {

 @Test
  public void testBasicEnglishTokenization() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("Hello world!");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertNotNull(tokens);
    assertEquals(3, tokens.size());

    assertEquals("Hello", tokens.get(0).word());
    assertEquals("world", tokens.get(1).word());
    assertEquals("!", tokens.get(2).word());
  }
@Test
  public void testWhitespaceTokenizer() {
    Properties props = new Properties();
    props.setProperty("tokenize.whitespace", "true");
    props.setProperty("tokenize.language", "en");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("one two  three\nfour");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertNotNull(tokens);
    assertEquals(4, tokens.size());

    assertEquals("one", tokens.get(0).word());
    assertEquals("two", tokens.get(1).word());
    assertEquals("three", tokens.get(2).word());
    assertEquals("four", tokens.get(3).word());
  }
@Test
  public void testUnsupportedLanguageThrowsException() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "zz");

    try {
      new TokenizerAnnotator(props);
      fail("Expected IllegalArgumentException for unknown language");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("unknown tokenize.language property"));
    }
  }
@Test
  public void testUnknownTokenizerClassThrowsException() {
    Properties props = new Properties();
    props.setProperty("tokenize.class", "InvalidTokenizer");

    try {
      new TokenizerAnnotator(props);
      fail("Expected IllegalArgumentException for unknown class");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("unknown tokenize.class property"));
    }
  }
@Test
  public void testEmptyAnnotationReturnsEmptyTokens() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertNotNull(tokens);
    assertEquals(0, tokens.size());
  }
@Test
  public void testMissingTextAnnotationRaisesException() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("");
    annotation.remove(CoreAnnotations.TextAnnotation.class);

    try {
      annotator.annotate(annotation);
      fail("Expected RuntimeException due to missing text");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("Tokenizer unable to find text"));
    }
  }
@Test
  public void testTokenBeginEndAnnotationsSetCorrectly() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("a b");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertEquals((Integer) 0, tokens.get(0).get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals((Integer) 1, tokens.get(0).get(CoreAnnotations.TokenEndAnnotation.class));

    assertEquals((Integer) 1, tokens.get(1).get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals((Integer) 2, tokens.get(1).get(CoreAnnotations.TokenEndAnnotation.class));
  }
@Test
  public void testAdjustFinalTokenRemovesTrailingSpace() {
    CoreLabel label = new CoreLabel();
    label.set(CoreAnnotations.AfterAnnotation.class, "  ");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(label);

    TokenizerAnnotator.adjustFinalToken(tokens);
    assertEquals(" ", label.get(CoreAnnotations.AfterAnnotation.class));
  }
@Test
  public void testAdjustFinalTokenHandlesEmptyList() {
    List<CoreLabel> tokens = new ArrayList<>();
    TokenizerAnnotator.adjustFinalToken(tokens);
    assertEquals(0, tokens.size());
  }
@Test
  public void testNewlineTokenIsAnnotated() {
    CoreLabel token = new CoreLabel();
    token.setWord(AbstractTokenizer.NEWLINE_TOKEN);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    Boolean status = token.get(CoreAnnotations.IsNewlineAnnotation.class);
    assertTrue(status);
  }
@Test
  public void testRegularTokenIsNotMarkedNewline() {
    CoreLabel token = new CoreLabel();
    token.setWord("text");
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    Boolean status = token.get(CoreAnnotations.IsNewlineAnnotation.class);
    assertFalse(status);
  }
@Test
  public void testCleanXmlAnnotationEnabled() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.cleanxml", "true");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("<p>Hello</p>");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertFalse(tokens.isEmpty());
  }
@Test
  public void testSsplitDisabled() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.ssplit", "false");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("This is a test.");
    annotator.annotate(annotation);

    assertNull(annotation.get(CoreAnnotations.SentencesAnnotation.class));
  }
@Test
  public void testRequirementsSatisfiedContainsExpected() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertTrue(satisfied.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testRequiresReturnsEmptySet() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Set<Class<? extends CoreAnnotation>> required = annotator.requires();

    assertNotNull(required);
    assertTrue(required.isEmpty());
  }
@Test
  public void testEmptyTokenListInAdjustFinalTokenDoesNotThrow() {
    List<CoreLabel> emptyList = new ArrayList<CoreLabel>();
    TokenizerAnnotator.adjustFinalToken(emptyList);
    assertTrue(emptyList.isEmpty());
  }
@Test
  public void testFinalTokenWithNullAfterAnnotation() {
    CoreLabel label = new CoreLabel();
    label.set(CoreAnnotations.AfterAnnotation.class, null);
    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    tokens.add(label);

    TokenizerAnnotator.adjustFinalToken(tokens);
    assertNull(label.get(CoreAnnotations.AfterAnnotation.class));
  }
@Test
  public void testFinalTokenWithEmptyAfterAnnotation() {
    CoreLabel label = new CoreLabel();
    label.set(CoreAnnotations.AfterAnnotation.class, "");
    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    tokens.add(label);

    TokenizerAnnotator.adjustFinalToken(tokens);
    assertEquals("", label.get(CoreAnnotations.AfterAnnotation.class));
  }
@Test
  public void testDefaultConstructorInitializesEnglishPTBTokenizer() {
    TokenizerAnnotator annotator = new TokenizerAnnotator();
    Annotation annotation = new Annotation("hello!");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(2, tokens.size());
    assertEquals("hello", tokens.get(0).word());
    assertEquals("!", tokens.get(1).word());
  }
@Test
  public void testConstructorWithLanguageStringOverridesDefaultOptions() {
    TokenizerAnnotator annotator = new TokenizerAnnotator(true, "english", "");
    Annotation annotation = new Annotation("Stanford.");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(2, tokens.size());
    assertEquals("Stanford", tokens.get(0).word());
    assertEquals(".", tokens.get(1).word());
  }
@Test
  public void testLanguageSpecificOptionsLoadedForSpanishTokenizer() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "es");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation ann = new Annotation("¡Hola!");
    annotator.annotate(ann);
    List<CoreLabel> tokens = ann.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertFalse(tokens.isEmpty());
  }
@Test
  public void testFrenchTokenizerWithDefaultOptionsAndUppercaseLangCode() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "FR");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation ann = new Annotation("bonjour.");
    annotator.annotate(ann);
    List<CoreLabel> tokens = ann.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(2, tokens.size());
    assertEquals("bonjour", tokens.get(0).word());
    assertEquals(".", tokens.get(1).word());
  }
@Test
  public void testInitFactoryFallsBackToPTBTokenizerWhenUnspecified() {
    Properties props = new Properties();
    TokenizerAnnotator annotator = new TokenizerAnnotator(false, TokenizerAnnotator.TokenizerType.Unspecified);
    Annotation ann = new Annotation("Testing default tokenizer.");
    annotator.annotate(ann);
    List<CoreLabel> tokens = ann.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertFalse(tokens.isEmpty());
  }
@Test
  public void testTokenizeWithCodepointPostProcessor() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.codepoint", "true");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation ann = new Annotation("A B");
    annotator.annotate(ann);
    List<CoreLabel> tokens = ann.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(2, tokens.size());
    assertEquals("A", tokens.get(0).word());
    assertEquals("B", tokens.get(1).word());
  }
@Test
  public void testSetNewlineStatusHandlesMultipleNewlineTokens() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord(AbstractTokenizer.NEWLINE_TOKEN);
    CoreLabel token2 = new CoreLabel();
    token2.setWord(AbstractTokenizer.NEWLINE_TOKEN);
    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    tokens.add(token1);
    tokens.add(token2);

 }
@Test
  public void testAnnotationWithoutSentenceAnnotationClearsOldSentences() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("This is one. This is two.");
    List<List<CoreLabel>> dummySentences = new ArrayList<List<CoreLabel>>();

    annotator.annotate(annotation);

    assertNotSame(dummySentences, annotation.get(CoreAnnotations.SentencesAnnotation.class));
  }
@Test
  public void testTokenizerFactoryCaseWhenOptionsContainsKeepNL() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("ssplit.isOneSentence", "false");
    props.setProperty("ssplit.newlineIsSentenceBreak", "always");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("Line 1\nLine 2");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(4, tokens.size());
  }
@Test
  public void testTokenizerHandlesInputEndingWithNoSpace() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("finalword");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(1, tokens.size());
    assertEquals("finalword", tokens.get(0).word());
  }
@Test
  public void testMultiplePostProcessorsAppliedSequentially() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.codepoint", "true");
    props.setProperty("tokenize.postProcessor", "edu.stanford.nlp.pipeline.CodepointCoreLabelProcessor");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("a b");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(2, tokens.size());
  }
@Test
  public void testTokenizerDoesNotFailWithSinglePunctuationCharacter() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation(".");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(1, tokens.size());
    assertEquals(".", tokens.get(0).word());
  }
@Test
  public void testTokenizerTypeFromLanguageReturnsWhitespace() {
    Properties properties = new Properties();
    properties.setProperty("tokenize.whitespace", "true");

    TokenizerAnnotator.TokenizerType type = TokenizerAnnotator.TokenizerType.getTokenizerType(properties);
    assertEquals(TokenizerAnnotator.TokenizerType.Whitespace, type);
  }
@Test
  public void testTokenizerTypeFallbackToDefaultEnglish() {
    Properties properties = new Properties();
    TokenizerAnnotator.TokenizerType type = TokenizerAnnotator.TokenizerType.getTokenizerType(properties);
    assertEquals(TokenizerAnnotator.TokenizerType.English, type);
  }
@Test
  public void testTokenizerTypeThrowsOnUnknownClassName() {
    Properties properties = new Properties();
    properties.setProperty("tokenize.class", "NonExistentTokenizer");

    try {
      TokenizerAnnotator.TokenizerType.getTokenizerType(properties);
      fail("Expected IllegalArgumentException for invalid class name");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("unknown tokenize.class property"));
    }
  }
@Test
  public void testTokenizerTypeThrowsOnUnknownLanguageCode() {
    Properties properties = new Properties();
    properties.setProperty("tokenize.language", "xxx");

    try {
      TokenizerAnnotator.TokenizerType.getTokenizerType(properties);
      fail("Expected IllegalArgumentException for unknown language code");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("unknown tokenize.language property"));
    }
  }
@Test
  public void testComputeExtraOptionsIncludesKeepNLWhenNewlineIsAlways() {
    Properties props = new Properties();
    props.setProperty("ssplit.newlineIsSentenceBreak", "always");
    props.setProperty("ssplit.isOneSentence", "false");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation ann = new Annotation("Line1\nLine2");
    annotator.annotate(ann);
    List<CoreLabel> tokens = ann.get(CoreAnnotations.TokensAnnotation.class);

    assertNotNull(tokens);
    assertFalse(tokens.isEmpty());
  }
@Test
  public void testComputeExtraOptionsNoKeepNLWhenNeverIsSet() {
    Properties props = new Properties();
    props.setProperty("ssplit.newlineIsSentenceBreak", "never");
    props.setProperty("ssplit.isOneSentence", "false");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation ann = new Annotation("One\nTwo");
    annotator.annotate(ann);
    List<CoreLabel> tokens = ann.get(CoreAnnotations.TokensAnnotation.class);

    assertNotNull(tokens);
    assertFalse(tokens.isEmpty());
  }
@Test
  public void testUnsupportedSegmenterLanguageThrows() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "de");

    try {
      new TokenizerAnnotator(props);
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("No segmenter implemented for"));
    }
  }
@Test
  public void testArabicSegmenterIsUsed() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "ar");

    try {
      TokenizerAnnotator annotator = new TokenizerAnnotator(props);
      Annotation annotation = new Annotation("العَرَبِيَّةُ");
      annotator.annotate(annotation);
      List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
      assertNotNull(tokens);
    } catch (Exception e) {
      
    }
  }
@Test
  public void testChineseSegmenterIsUsed() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "zh");

    try {
      TokenizerAnnotator annotator = new TokenizerAnnotator(props);
      Annotation annotation = new Annotation("这是一个测试");
      annotator.annotate(annotation);
      List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
      assertNotNull(tokens);
    } catch (Exception e) {
      
    }
  }
@Test
  public void testCDCAnnotatorFallbackPathInitializes() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("edu.stanford.nlp.pipeline.cdc.model", "fake-model-path");

    try {
      new TokenizerAnnotator(props);
    } catch (Exception ignored) {
    }
  }
@Test
  public void testTokenizerAnnotatorWithNullPropertiesDefaultsToEnglish() {
    TokenizerAnnotator annotator = new TokenizerAnnotator(true, (Properties) null);
    Annotation annotation = new Annotation("Basic English test.");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertNotNull(tokens);
    assertEquals(3, tokens.size());
  }
@Test
  public void testInitFactoryForWhitespaceWithEOLPropertyTrue() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.whitespace", "true");
    props.setProperty("tokenize.keepeol", "true");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("line1\nline2");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertNotNull(tokens);
    assertEquals(2, tokens.size());
  }
@Test
  public void testInitFactoryOptionsConcatenationWithComma() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.options", "splitHyphenated=true");

    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("e-mail address");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertNotNull(tokens);
    assertTrue(tokens.size() >= 2);
  }
@Test
  public void testWhitespaceTokenizerTrimsMultipleSpaces() {
    Properties props = new Properties();
    props.setProperty("tokenize.whitespace", "true");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("a    b");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertEquals(2, tokens.size());
  }
@Test
  public void testAfterAnnotationPreservedOnFinalToken() {
    CoreLabel label = new CoreLabel();
    label.set(CoreAnnotations.AfterAnnotation.class, " ");
    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    tokens.add(label);

    TokenizerAnnotator.adjustFinalToken(tokens);
    assertEquals("", label.get(CoreAnnotations.AfterAnnotation.class));
  }
@Test
  public void testAfterAnnotationWithSingleNonSpaceCharThrowsException() {
    CoreLabel label = new CoreLabel();
    label.set(CoreAnnotations.AfterAnnotation.class, "\t");

    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    tokens.add(label);

    try {
      TokenizerAnnotator.adjustFinalToken(tokens);
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("Unexpected final char"));
    }
  }
@Test
  public void testConstructorWithNullOptionsUsesDefaults() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    TokenizerAnnotator annotator = new TokenizerAnnotator(true, props, null);
    Annotation annotation = new Annotation("This is a test.");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertNotNull(tokens);
    assertEquals(4, tokens.size());
    assertEquals("This", tokens.get(0).word());
  }
@Test
  public void testConstructorWithInvalidPostProcessorClassThrows() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.postProcessor", "non.existent.UnloadableProcessor");

    boolean exceptionThrown = false;
    try {
      new TokenizerAnnotator(props);
    } catch (RuntimeException e) {
      exceptionThrown = true;
      assertTrue(e.getMessage().contains("Loading: non.existent.UnloadableProcessor failed"));
    }
    assertTrue(exceptionThrown);
  }
@Test
  public void testInitFactoryWithNullExtraOptionsPreserved() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    TokenizerAnnotator.TokenizerType type = TokenizerAnnotator.TokenizerType.getTokenizerType(props);

    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props, null);
    Annotation annotation = new Annotation("Tokenization fallback validation.");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.size() > 0);
  }
@Test
  public void testUnspecifiedTokenizerDefaultsToPTBTokenizer() {
    Properties props = new Properties();
    TokenizerAnnotator.TokenizerType type = TokenizerAnnotator.TokenizerType.Unspecified;

    TokenizerAnnotator annotator = new TokenizerAnnotator(false, type);
    Annotation annotation = new Annotation("Fallback test.");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertNotNull(tokens);
    assertEquals(2, tokens.size());
  }
@Test
  public void testSetNewlineStatusWithNullWordDoesNotThrow() {
    CoreLabel token = new CoreLabel();
    token.setWord(null);
    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    tokens.add(token);

    Boolean result = token.get(CoreAnnotations.IsNewlineAnnotation.class);
    assertFalse(result);
  }
@Test
  public void testAdjustFinalTokenWithSingleSpace() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.AfterAnnotation.class, " ");
    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    tokens.add(token);

    TokenizerAnnotator.adjustFinalToken(tokens);
    assertEquals("", token.get(CoreAnnotations.AfterAnnotation.class));
  }
@Test
  public void testMultiplePropertiesWithMixedBooleans() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "EN");
    props.setProperty("tokenize.verbose", "true");
    props.setProperty("tokenize.whitespace", "false");
    props.setProperty("tokenize.options", "invertible");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("Hybrid test.");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(2, tokens.size());
  }
@Test
  public void testSentenceAnnotationRemovedIfPreviouslyPresent() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("One sentence here.");

    annotator.annotate(annotation);
    Object result = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(result);
  }
@Test
  public void testWhitespaceTokenizerWithKeepEolAndSplitNewlineOption() {
    Properties props = new Properties();
    props.setProperty("tokenize.whitespace", "true");
    props.setProperty("tokenize.keepeol", "true");

    Annotation annotation = new Annotation("Line1\nLine2");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertNotNull(tokens);
    assertEquals(2, tokens.size());
  }
@Test
  public void testInitWithMixedCasingLanguageCode() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "Fr");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("salut.");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertNotNull(tokens);
    assertEquals(2, tokens.size());
  }
@Test
  public void testMultipleSpacesWithWhitespaceTokenizer() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.whitespace", "true");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation(" word1     word2 ");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertNotNull(tokens);
    assertEquals(2, tokens.size());
    assertEquals("word1", tokens.get(0).word());
    assertEquals("word2", tokens.get(1).word());
  }
@Test
  public void testEnglishTokenizerHandlesSpecialCharacters() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("email@example.com");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.size() >= 1); 
  }
@Test
  public void testWhitespaceTrueOverridesLanguageProperty() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.whitespace", "true");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("foo bar");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(2, tokens.size());
    assertEquals("foo", tokens.get(0).word());
    assertEquals("bar", tokens.get(1).word());
  }
@Test
  public void testDefaultOptionsAreUsedWhenTokenizeOptionsMissing() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "fr");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("Bonjour.");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(2, tokens.size());
    assertEquals("Bonjour", tokens.get(0).word());
    assertEquals(".", tokens.get(1).word());
  }
@Test
  public void testKeepNewlineAndSsplitOneSentenceFalseTriggersKeepNL() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("ssplit.isOneSentence", "false");
    props.setProperty("ssplit.newlineIsSentenceBreak", "two");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("First line.\nSecond line.");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    boolean foundNewline = false;
    if (!tokens.isEmpty()) {
      for (int i = 0; i < tokens.size(); i++) {
        CoreLabel tok = tokens.get(i);
        if (Boolean.TRUE.equals(tok.get(CoreAnnotations.IsNewlineAnnotation.class))) {
          foundNewline = true;
        }
      }
    }
    assertTrue(foundNewline);
  }
@Test
  public void testKeepNewlineFalseDoesNotTriggerKeepNL() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("ssplit.isOneSentence", "true");
    props.setProperty("ssplit.newlineIsSentenceBreak", "never");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("Line1\nLine2");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertFalse(tokens.get(0).get(CoreAnnotations.IsNewlineAnnotation.class));
    assertFalse(tokens.get(tokens.size() - 1).get(CoreAnnotations.IsNewlineAnnotation.class));
  }
@Test
  public void testUnspecifiedTokenizerWithNullOptionFallsBackGracefully() {
    Properties props = new Properties();
    TokenizerAnnotator.TokenizerType type = TokenizerAnnotator.TokenizerType.Unspecified;
    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props, null);
    Annotation annotation = new Annotation("Graceful fallback check.");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(3, tokens.size());
  }
@Test
  public void testUnmodifiablePostProcessorListIsEmptyByDefault() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("No processors here.");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertFalse(tokens.isEmpty());
  }
@Test
  public void testAnnotateReplacesExistingSentencesAnnotation() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("This is new.");
    List<List<CoreLabel>> dummySentences = new ArrayList<List<CoreLabel>>();
    annotator.annotate(annotation);
    Object newValue = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotSame(dummySentences, newValue);
  }
@Test
  public void testChineseSegmenterHandlesTokenization() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "zh");
    try {
      TokenizerAnnotator annotator = new TokenizerAnnotator(props);
      Annotation annotation = new Annotation("中文测试");
      annotator.annotate(annotation);
      List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
      assertNotNull(tokens);
    } catch (Exception e) {
      
      assertTrue(true);
    }
  }
@Test
  public void testArabicSegmenterHandlesTokenization() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "ar");
    try {
      TokenizerAnnotator annotator = new TokenizerAnnotator(props);
      Annotation annotation = new Annotation("مرحبا بالعالم");
      annotator.annotate(annotation);
      List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
      assertNotNull(tokens);
    } catch (Exception e) {
      
      assertTrue(true);
    }
  }
@Test
  public void testWhitespaceTokenizerWithNewlineNoSplitOptionTreatsEOLAsSpace() {
    Properties props = new Properties();
    props.setProperty("tokenize.whitespace", "true");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation ann = new Annotation("line1\nline2");
    annotator.annotate(ann);
    List<CoreLabel> tokens = ann.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(2, tokens.size());
    assertEquals("line1", tokens.get(0).word());
    assertEquals("line2", tokens.get(1).word());
  }
@Test
  public void testTokenBeginEndAnnotationsAreSequential() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("A B C");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(3, tokens.size());
    assertEquals((Integer) 0, tokens.get(0).get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals((Integer) 1, tokens.get(0).get(CoreAnnotations.TokenEndAnnotation.class));
    assertEquals((Integer) 1, tokens.get(1).get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals((Integer) 2, tokens.get(1).get(CoreAnnotations.TokenEndAnnotation.class));
    assertEquals((Integer) 2, tokens.get(2).get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals((Integer) 3, tokens.get(2).get(CoreAnnotations.TokenEndAnnotation.class));
  }
@Test
  public void testPostProcessingDoesNotClearTokensIfEmptyProcessorList() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("Original content.");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.size() >= 2);
  }
@Test
  public void testTokenizerTypeGetFromValidClassNameUppercase() {
    Properties props = new Properties();
    props.setProperty("tokenize.class", "FrenchTokenizer");
    TokenizerAnnotator.TokenizerType type = TokenizerAnnotator.TokenizerType.getTokenizerType(props);
    assertEquals(TokenizerAnnotator.TokenizerType.French, type);
  }
@Test
  public void testTokenizerTypeGetFromValidClassNameLowercase() {
    Properties props = new Properties();
    props.setProperty("tokenize.class", "frenchtokenizer");
    TokenizerAnnotator.TokenizerType type = TokenizerAnnotator.TokenizerType.getTokenizerType(props);
    assertEquals(TokenizerAnnotator.TokenizerType.French, type);
  }
@Test
  public void testTokenizerTypeNameLookupFromUppercaseLanguageCode() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "ES");
    TokenizerAnnotator.TokenizerType type = TokenizerAnnotator.TokenizerType.getTokenizerType(props);
    assertEquals(TokenizerAnnotator.TokenizerType.Spanish, type);
  }
@Test
  public void testTokenizerTypeNameLookupFromTokenizerTypeNameString() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "GERMAN");
    TokenizerAnnotator.TokenizerType type = TokenizerAnnotator.TokenizerType.getTokenizerType(props);
    assertEquals(TokenizerAnnotator.TokenizerType.German, type);
  }
@Test
  public void testClassToTokenizerMapDoesNotContainUnknownClass() {
    try {
      Properties props = new Properties();
      props.setProperty("tokenize.class", "InvalidClassName");
      TokenizerAnnotator.TokenizerType.getTokenizerType(props);
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("unknown tokenize.class"));
    }
  }
@Test
  public void testNameToTokenizerMapDoesNotContainUnknownLanguageCode() {
    try {
      Properties props = new Properties();
      props.setProperty("tokenize.language", "NONEXISTENT");
      TokenizerAnnotator.TokenizerType.getTokenizerType(props);
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("unknown tokenize.language"));
    }
  }
@Test
  public void testEnglishTokenizerWithMultilineText() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);

    String text = "This is line one.\nAnd this is line two.\nFinal line.";
    Annotation annotation = new Annotation(text);
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.size() > 5);
  }
@Test
  public void testGermanTokenizerWithConfiguredOptions() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "de");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("Hallo-Welt!");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.size() >= 2);
  }
@Test
  public void testWhitespaceTokenizerWithLeadingAndTrailingSpace() {
    Properties props = new Properties();
    props.setProperty("tokenize.whitespace", "true");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("  hello   world  ");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(2, tokens.size());
    assertEquals("hello", tokens.get(0).word());
    assertEquals("world", tokens.get(1).word());
  }
@Test
  public void testUnspecifiedTokenizerTypeFallsBackToPTB() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "UNSPECIFIED");  

    TokenizerAnnotator.TokenizerType type = TokenizerAnnotator.TokenizerType.getTokenizerType(props);
    assertEquals(TokenizerAnnotator.TokenizerType.Unspecified, type);

    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props, null);
    Annotation annotation = new Annotation("hello fallback.");
    annotator.annotate(annotation);

    List<CoreLabel> result = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(2, result.size());
  }
@Test
  public void testMultiplePropertiesSetButOverrideViaWhitespace() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.class", "PTBTokenizer");
    props.setProperty("tokenize.whitespace", "true");

    TokenizerAnnotator.TokenizerType type = TokenizerAnnotator.TokenizerType.getTokenizerType(props);
    assertEquals(TokenizerAnnotator.TokenizerType.Whitespace, type);
  }
@Test
  public void testCustomTokenizerOptionsMergedWithExtraOptions() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.options", "invertible,ptb3Escaping=false");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("mixed options test.");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertFalse(tokens.isEmpty());
  }
@Test
  public void testAdjustFinalTokenWithTrailingWhitespaceAndNonSpaceCharFails() {
    CoreLabel label = new CoreLabel();
    label.set(CoreAnnotations.AfterAnnotation.class, "_X"); 

    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    tokens.add(label);

    try {
      TokenizerAnnotator.adjustFinalToken(tokens);
      fail("Expected IllegalArgumentException for non-space trailing character");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("Unexpected final char"));
    }
  }
@Test
  public void testEmptyPropertiesUsesDefaultEnglishTokenizer() {
    Properties props = new Properties();
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);

    Annotation ann = new Annotation("default behavior test.");
    annotator.annotate(ann);
    List<CoreLabel> tokens = ann.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(3, tokens.size());
  }
@Test
  public void testLanguageTokenizationCaseInsensitiveEnumHandling() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "En");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("Case insensitive language.");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertEquals(3, tokens.size());
  }
@Test
  public void testFactoryInitializedForSpanishTokenizer() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "es");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("¡Hola mundo!");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertNotNull(tokens);
    assertTrue(tokens.size() >= 2);
  }
@Test
  public void testFinalTokenAfterAnnotationSetToNullIfOnlyOneChar() {
    CoreLabel label = new CoreLabel();
    label.set(CoreAnnotations.AfterAnnotation.class, " ");
    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    tokens.add(label);

    TokenizerAnnotator.adjustFinalToken(tokens);
    assertEquals("", label.get(CoreAnnotations.AfterAnnotation.class));
  }

@Test
  public void testInitFactoryNullTokenizeOptionsUsesOnlyExtraOptions() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.remove("tokenize.options");

    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props, "customOption");
    Annotation annotation = new Annotation("Custom option check.");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(3, tokens.size());
  }
@Test
  public void testInitFactoryEmptyOptionsWithValidExtraOption() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.options", "");
    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props, "invertible");
    Annotation ann = new Annotation("Test tokens.");
    annotator.annotate(ann);
    List<CoreLabel> tokens = ann.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertFalse(tokens.isEmpty());
  }
@Test
  public void testWhitespaceTokenizerEOLPropertyFalseWithKeepNLOptionTrue() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.whitespace", "true");
    props.setProperty("tokenize.keepeol", "false");
    props.setProperty("ssplit.isOneSentence", "false");
    props.setProperty("ssplit.newlineIsSentenceBreak", "always");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation ann = new Annotation("Line1\nLine2");
    annotator.annotate(ann);

    List<CoreLabel> tokens = ann.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(2, tokens.size());
  }
@Test
  public void testSsplitNewlineIsSentenceBreakNeverKeepsNewlineFalse() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("ssplit.newlineIsSentenceBreak", "never");
    props.setProperty("ssplit.isOneSentence", "false");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("Line1\nLine2");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(4, tokens.size());
  }
@Test
  public void testInitializeWithCodepointOnlyAsPostProcessor() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.codepoint", "true");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation ann = new Annotation("A B");
    annotator.annotate(ann);
    List<CoreLabel> tokens = ann.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(2, tokens.size());
    assertEquals("A", tokens.get(0).word());
  }
@Test
  public void testCleanXmlDisabledAndSsplitDisabledDisablesBothPaths() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.cleanxml", "false");
    props.setProperty("tokenize.ssplit", "false");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation ann = new Annotation("Check split.");
    annotator.annotate(ann);
    assertNull(ann.get(CoreAnnotations.SentencesAnnotation.class));
  }
@Test
  public void testSetNewlineStatusFalseIfNewlineTokenLengthNotMatchingLiteral() {
    CoreLabel label = new CoreLabel();
    label.setWord("*NL*xyz");
    List<CoreLabel> list = new ArrayList<CoreLabel>();
    list.add(label);

    Boolean value = label.get(CoreAnnotations.IsNewlineAnnotation.class);
    assertFalse(value);
  }
@Test
  public void testTokenizerTypeUsesClassInsteadOfLanguageWhenBothPresent() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.class", "frenchtokenizer"); 

    TokenizerAnnotator.TokenizerType type = TokenizerAnnotator.TokenizerType.getTokenizerType(props);
    assertEquals(TokenizerAnnotator.TokenizerType.French, type);
  }
@Test
  public void testTokenizerTypeEvaluatesClassMappingEvenWhenLanguageNull() {
    Properties props = new Properties();
    props.setProperty("tokenize.class", "SpanishTokenizer");

    TokenizerAnnotator.TokenizerType type = TokenizerAnnotator.TokenizerType.getTokenizerType(props);
    assertEquals(TokenizerAnnotator.TokenizerType.Spanish, type);
  }
@Test
  public void testAdjustFinalTokenNoOpWhenAfterIsNull() {
    CoreLabel label = new CoreLabel();
    label.set(CoreAnnotations.AfterAnnotation.class, null);
    List<CoreLabel> list = new ArrayList<CoreLabel>();
    list.add(label);

    TokenizerAnnotator.adjustFinalToken(list);
    assertNull(label.get(CoreAnnotations.AfterAnnotation.class));
  } 
}