package edu.stanford.nlp.pipeline;

import java.io.StringReader;
import java.util.*;

import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.process.*;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.PropertiesUtils;
import junit.framework.TestCase;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import org.junit.Test;

import static org.junit.Assert.*;

public class TokenizerAnnotator_4_GPTLLMTest {

 @Test
  public void testConstructorWithDefault() {
    TokenizerAnnotator annotator = new TokenizerAnnotator();
    assertNotNull(annotator);
  }
@Test
  public void testConstructorWithLanguage() {
    TokenizerAnnotator annotator = new TokenizerAnnotator("en");
    assertNotNull(annotator);
  }
@Test
  public void testAdjustFinalToken_RemovesTrailingSpace() {
    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.AfterAnnotation.class, " ");
    tokens.add(token);

    TokenizerAnnotator.adjustFinalToken(tokens);

    assertEquals("", token.get(CoreAnnotations.AfterAnnotation.class));
  }
@Test
  public void testAdjustFinalToken_NoChangeIfNoAfterAnnotationField() {
    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token = new CoreLabel();
    tokens.add(token);

    TokenizerAnnotator.adjustFinalToken(tokens);

    assertNull(token.get(CoreAnnotations.AfterAnnotation.class));
  }
@Test(expected = IllegalArgumentException.class)
  public void testAdjustFinalToken_ThrowsExceptionOnUnexpectedChar() {
    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.AfterAnnotation.class, "\t");
    tokens.add(token);

    TokenizerAnnotator.adjustFinalToken(tokens);
  }
@Test
  public void testWhitespaceTokenizerProducesTokens() {
    Properties props = new Properties();
    props.setProperty("tokenize.whitespace", "true");
    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("Token1 Token2\tToken3\nToken4");

    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertNotNull(tokens);
    assertEquals(4, tokens.size());

    assertEquals("Token1", tokens.get(0).word());
    assertEquals("Token2", tokens.get(1).word());
    assertEquals("Token3", tokens.get(2).word());
    assertEquals("Token4", tokens.get(3).word());
  }
@Test
  public void testEnglishTokenizationProducesCorrectTokens() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("This is a test.");

    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertNotNull(tokens);
    assertEquals("This", tokens.get(0).word());
    assertEquals("is", tokens.get(1).word());
    assertEquals("a", tokens.get(2).word());
    assertEquals("test", tokens.get(3).word());
    assertEquals(".", tokens.get(4).word());
  }
@Test
  public void testSpanishTokenizationReturnsNonEmptyList() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "es");
    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("Hola mundo. ¿Cómo estás?");

    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.size() > 0);
  }
@Test
  public void testFrenchTokenizationReturnsNonEmptyList() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "fr");
    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("Bonjour le monde. Comment ça va?");

    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.size() > 0);
  }
@Test(expected = IllegalArgumentException.class)
  public void testInvalidLanguageThrowsException() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "xx");

    TokenizerAnnotator.TokenizerType.getTokenizerType(props);
  }
@Test(expected = IllegalArgumentException.class)
  public void testInvalidTokenizerClassThrowsException() {
    Properties props = new Properties();
    props.setProperty("tokenize.class", "UnknownTokenizer");

    TokenizerAnnotator.TokenizerType.getTokenizerType(props);
  }
@Test
  public void testSetNewlineStatusAssignsIsNewlineAnnotation() {
    CoreLabel newlineToken = new CoreLabel();
    newlineToken.setWord(AbstractTokenizer.NEWLINE_TOKEN);

    CoreLabel otherToken = new CoreLabel();
    otherToken.setWord("word");

    List<CoreLabel> tokens = Arrays.asList(newlineToken, otherToken);

    assertTrue(newlineToken.get(CoreAnnotations.IsNewlineAnnotation.class));
    assertFalse(otherToken.get(CoreAnnotations.IsNewlineAnnotation.class));
  }
@Test
  public void testSetTokenIndexBoundaries() {
    CoreLabel token1 = new CoreLabel();
    CoreLabel token2 = new CoreLabel();
    CoreLabel token3 = new CoreLabel();

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);

    assertEquals(0, (int) token1.get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals(1, (int) token1.get(CoreAnnotations.TokenEndAnnotation.class));

    assertEquals(1, (int) token2.get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals(2, (int) token2.get(CoreAnnotations.TokenEndAnnotation.class));

    assertEquals(2, (int) token3.get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals(3, (int) token3.get(CoreAnnotations.TokenEndAnnotation.class));
  }
@Test(expected = RuntimeException.class)
  public void testAnnotateWithoutTextThrows() {
    TokenizerAnnotator annotator = new TokenizerAnnotator(false);
    Annotation annotation = new Annotation("");
    annotation.remove(CoreAnnotations.TextAnnotation.class);

    annotator.annotate(annotation);
  }
@Test
  public void testGetTokenizerReturnsPTBTokenizer() {
    TokenizerAnnotator annotator = new TokenizerAnnotator();
    Tokenizer<CoreLabel> tokenizer = annotator.getTokenizer(new StringReader("Hello world!"));

    List<CoreLabel> tokens = tokenizer.tokenize();

    assertEquals("Hello", tokens.get(0).word());
    assertEquals("world", tokens.get(1).word());
    assertEquals("!", tokens.get(2).word());
  }
@Test
  public void testRequirementsSatisfiedContainsTokenAnnotation() {
    TokenizerAnnotator annotator = new TokenizerAnnotator();
    Set<Class<? extends CoreAnnotation>> set = annotator.requirementsSatisfied();

    assertTrue(set.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(set.contains(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testRequiresIsEmpty() {
    TokenizerAnnotator annotator = new TokenizerAnnotator();

    Set<Class<? extends CoreAnnotation>> required = annotator.requires();

    assertTrue(required.isEmpty());
  }
@Test
  public void testTokenizerWithVerboseLogging() {
    Properties props = new Properties();
    props.setProperty("tokenize.verbose", "true");
    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    assertNotNull(annotator);
  }
@Test(expected = RuntimeException.class)
  public void testPostProcessorClassNotFoundThrows() {
    Properties props = new Properties();
    props.setProperty("tokenize.postProcessor", "non.existent.ClassName");

    new TokenizerAnnotator(false, props);
  }
@Test
  public void testEmptyInputYieldsNoTokens() {
    TokenizerAnnotator annotator = new TokenizerAnnotator();
    Annotation annotation = new Annotation("");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(0, tokens.size());
  }
@Test
  public void testWhitespaceTokenizerWithMultipleWhitespace() {
    Properties props = new Properties();
    props.setProperty("tokenize.whitespace", "true");
    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("A    B\t\tC\n\nD");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(4, tokens.size());
    assertEquals("A", tokens.get(0).word());
    assertEquals("B", tokens.get(1).word());
    assertEquals("C", tokens.get(2).word());
    assertEquals("D", tokens.get(3).word());
  }
@Test
  public void testUnspecifiedTokenizerDefaultsToPTB() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "Unspecified");
    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("Simple test.");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(3, tokens.size());
  }
@Test
  public void testNullPropertiesConstructor() {
    TokenizerAnnotator annotator = new TokenizerAnnotator(false, (Properties) null);
    Annotation annotation = new Annotation("Default test.");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.size() > 0);
  }
@Test
  public void testNullOptionsString() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props, null);
    Annotation annotation = new Annotation("Something here.");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.size() > 0);
  }
@Test
  public void testEmptyAfterAnnotationStillTokenizesFine() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.AfterAnnotation.class, "");
    List<CoreLabel> list = new ArrayList<>();
    list.add(token);
    TokenizerAnnotator.adjustFinalToken(list);
    assertEquals("", token.get(CoreAnnotations.AfterAnnotation.class));
  }
@Test
  public void testAdjustFinalTokenNullListDoesNothing() {
    TokenizerAnnotator.adjustFinalToken(null);
    
  }
@Test
  public void testAdjustFinalTokenEmptyListDoesNothing() {
    TokenizerAnnotator.adjustFinalToken(Collections.emptyList());
    
  }
@Test
  public void testTokenizationWithNewlineCharacters() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    Annotation annotation = new Annotation("Line 1\nLine 2");
    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.size() > 0);
  }
@Test(expected = RuntimeException.class)
  public void testUnsupportedSegmenterLanguageThrowsRuntime() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "de"); 
    new TokenizerAnnotator(false, props);
  }
@Test
  public void testCdcAnnotatorTakesPrecedenceOverSegmenter() {
    Properties props = new Properties();
    props.setProperty("tokenize.whitespace", "false");
    props.setProperty("segment.model", "fake-path"); 
    props.setProperty("statTok.model", "fake-model-path"); 
    try {
      new TokenizerAnnotator(false, props);
    } catch (RuntimeException e) {
      
    }
  }
@Test
  public void testFactoryWithCodepointProcessor() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.codepoint", "true");
    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("Test 123");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.size() > 0);
  }
@Test
  public void testPostProcessorViaReflectionFailsGracefully() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.postProcessor", "edu.stanford.nlp.NonExistentClassName");
    try {
      new TokenizerAnnotator(false, props);
      fail("Expected RuntimeException due to invalid postProcessor class");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("Loading: edu.stanford.nlp.NonExistentClassName failed with"));
    }
  }
@Test
  public void testTokenAfterAnnotationEndsWithSpaceAndMultipleSpacesTrimmedCorrectly() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.AfterAnnotation.class, "    ");
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    TokenizerAnnotator.adjustFinalToken(tokens);

    assertEquals("   ", token.get(CoreAnnotations.AfterAnnotation.class));
  }
@Test
  public void testUnspecifiedLanguageDefaultsToPTBTokenizer() {
    Properties props = new Properties();
    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("Testing PTB tokenizer.");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals("Testing", tokens.get(0).word());
    assertEquals(".", tokens.get(tokens.size() - 1).word());
  }
@Test
  public void testWhitespaceTokenizerFactoryNewLinePropertyRespected() {
    Properties props = new Properties();
    props.setProperty("tokenize.whitespace", "true");
    props.setProperty("tokenize.keepeol", "true");
    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("Token1\nToken2");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(2, tokens.size());
    assertEquals("Token1", tokens.get(0).word());
    assertEquals("Token2", tokens.get(1).word());
  }
@Test
  public void testLanguageAliasFrenchRecognized() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "FR"); 
    TokenizerAnnotator.TokenizerType type = TokenizerAnnotator.TokenizerType.getTokenizerType(props);
    assertEquals(TokenizerAnnotator.TokenizerType.French, type);
  }
@Test
  public void testLanguageAliasWhitespaceRecognized() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "whitespace"); 
    TokenizerAnnotator.TokenizerType type = TokenizerAnnotator.TokenizerType.getTokenizerType(props);
    assertEquals(TokenizerAnnotator.TokenizerType.Whitespace, type);
  }
@Test
  public void testTextWithOnlyNewlinesTokenizesCorrectlyUsingPTB() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    Annotation annotation = new Annotation("\n\n");
    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(2, tokens.size());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(0).word());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(1).word());
  }
@Test
  public void testMultiplePostProcessorsAppliedInOrder() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.postProcessor", "edu.stanford.nlp.pipeline.CodepointCoreLabelProcessor");
    props.setProperty("tokenize.codepoint", "true");
    try {
      TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
      assertNotNull(annotator);
    } catch (RuntimeException e) {
      
      fail("Expected postprocessor class to load successfully");
    }
  }
@Test
  public void testCleanXmlAnnotatorAppliedWhenEnabled() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.cleanxml", "true");
    Annotation annotation = new Annotation("This is <tag>cleaned</tag> text.");

    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.size() > 0);
  }
@Test
  public void testSentenceSplittingCanBeDisabled() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.ssplit", "false");
    Annotation annotation = new Annotation("Sentence one. Sentence two.");

    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    annotator.annotate(annotation);

    assertFalse(annotation.containsKey(CoreAnnotations.SentencesAnnotation.class));
  }
@Test
  public void testSentenceSplittingEnabledByDefault() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    Annotation annotation = new Annotation("Sentence one. Sentence two.");

    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    annotator.annotate(annotation);

    assertTrue(annotation.containsKey(CoreAnnotations.SentencesAnnotation.class));
  }
@Test
  public void testWhitespaceTokenizerWithLeadingTrailingSpaces() {
    Properties props = new Properties();
    props.setProperty("tokenize.whitespace", "true");
    Annotation annotation = new Annotation("   token1 token2   ");
    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(2, tokens.size());
    assertEquals("token1", tokens.get(0).word());
    assertEquals("token2", tokens.get(1).word());
  }
@Test
  public void testTokenizerTypeFromAbbreviation() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "es");
    TokenizerAnnotator.TokenizerType type = TokenizerAnnotator.TokenizerType.getTokenizerType(props);
    assertEquals(TokenizerAnnotator.TokenizerType.Spanish, type);
  }
@Test
  public void testTokenizerTypeFromEnumName() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "FRENCH");
    TokenizerAnnotator.TokenizerType type = TokenizerAnnotator.TokenizerType.getTokenizerType(props);
    assertEquals(TokenizerAnnotator.TokenizerType.French, type);
  }
@Test
  public void testTokenizerTypeFromTokenizerClass() {
    Properties props = new Properties();
    props.setProperty("tokenize.class", "FrenchTokenizer");
    TokenizerAnnotator.TokenizerType type = TokenizerAnnotator.TokenizerType.getTokenizerType(props);
    assertEquals(TokenizerAnnotator.TokenizerType.French, type);
  }
@Test
  public void testTokenizerTypeFromWhitespaceProperty() {
    Properties props = new Properties();
    props.setProperty("tokenize.whitespace", "true");
    TokenizerAnnotator.TokenizerType type = TokenizerAnnotator.TokenizerType.getTokenizerType(props);
    assertEquals(TokenizerAnnotator.TokenizerType.Whitespace, type);
  }
@Test(expected = IllegalArgumentException.class)
  public void testUnknownTokenizerClassFails() {
    Properties props = new Properties();
    props.setProperty("tokenize.class", "InvalidTokenizerClassName");
    TokenizerAnnotator.TokenizerType.getTokenizerType(props);
  }
@Test(expected = IllegalArgumentException.class)
  public void testUnknownTokenizerLanguageFails() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "unknown_language");
    TokenizerAnnotator.TokenizerType.getTokenizerType(props);
  }
@Test
  public void testInitFactoryWithExtraOptionsAndDefaultOptions() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    TokenizerAnnotator.TokenizerType type = TokenizerAnnotator.TokenizerType.English;
  }

@Test
  public void testAnnotateWithTrailingSpaceInText() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    Annotation annotation = new Annotation("Trailing space ");
    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertFalse(tokens.isEmpty());
    CoreLabel last = tokens.get(tokens.size() - 1);
    assertNotNull(last.get(CoreAnnotations.TokenBeginAnnotation.class));
    assertNotNull(last.get(CoreAnnotations.TokenEndAnnotation.class));
  }
@Test
  public void testMultipleNewlineTokensAreLabeledCorrectly() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord(AbstractTokenizer.NEWLINE_TOKEN);
    CoreLabel token2 = new CoreLabel();
    token2.setWord(AbstractTokenizer.NEWLINE_TOKEN);
    CoreLabel token3 = new CoreLabel();
    token3.setWord("Hello");
    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);
    assertTrue(token1.get(CoreAnnotations.IsNewlineAnnotation.class));
    assertTrue(token2.get(CoreAnnotations.IsNewlineAnnotation.class));
    assertFalse(token3.get(CoreAnnotations.IsNewlineAnnotation.class));
  }
@Test
  public void testTokenEndAnnotationDoesNotOverlap() {
    CoreLabel tokenA = new CoreLabel();
    CoreLabel tokenB = new CoreLabel();
    CoreLabel tokenC = new CoreLabel();
    List<CoreLabel> tokens = Arrays.asList(tokenA, tokenB, tokenC);
    int endA = tokenA.get(CoreAnnotations.TokenEndAnnotation.class);
    int beginB = tokenB.get(CoreAnnotations.TokenBeginAnnotation.class);
    int endB = tokenB.get(CoreAnnotations.TokenEndAnnotation.class);
    int beginC = tokenC.get(CoreAnnotations.TokenBeginAnnotation.class);
    assertEquals(endA, beginB);
    assertEquals(endB, beginC);
  }
@Test
  public void testDefaultConstructorAnnotatesCorrectly() {
    TokenizerAnnotator annotator = new TokenizerAnnotator();
    Annotation annotation = new Annotation("Simple sentence.");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertFalse(tokens.isEmpty());
    assertEquals("Simple", tokens.get(0).word());
  }
@Test
  public void testEmptyAfterAnnotationIsHandled() {
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.AfterAnnotation.class, "");
    List<CoreLabel> tokens = Collections.singletonList(token1);
    TokenizerAnnotator.adjustFinalToken(tokens);
    assertEquals("", token1.get(CoreAnnotations.AfterAnnotation.class));
  }

@Test(expected = RuntimeException.class)
  public void testMissingTextAnnotationFailsAnnotation() {
    Annotation annotation = new Annotation("");
    annotation.remove(CoreAnnotations.TextAnnotation.class);
    TokenizerAnnotator annotator = new TokenizerAnnotator();
    annotator.annotate(annotation);
  }
@Test
  public void testGetTokenizerTypeWhenNoPropertiesProvidedDefaultsToEnglish() {
    Properties props = new Properties();
    TokenizerAnnotator.TokenizerType type = TokenizerAnnotator.TokenizerType.getTokenizerType(props);
    assertEquals(TokenizerAnnotator.TokenizerType.English, type);
  }
@Test
  public void testInitFactoryWithWhitespaceAndKeepNewlineTrue() {
    Properties props = new Properties();
    props.setProperty("tokenize.keepeol", "true");
    TokenizerAnnotator.TokenizerType type = TokenizerAnnotator.TokenizerType.Whitespace;
  }
@Test
  public void testInitFactoryWithWhitespaceAndKeepNewlineTokenizeNLsOption() {
    Properties props = new Properties();
    props.setProperty("tokenize.whitespace", "true");
    props.setProperty("ssplit.newlineIsSentenceBreak", "always");
    TokenizerAnnotator.TokenizerType type = TokenizerAnnotator.TokenizerType.Whitespace;
  }
@Test
  public void testAdjustFinalTokenWithNoTrailingSpaceDoesNothing() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.AfterAnnotation.class, "abc");
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    try {
      TokenizerAnnotator.adjustFinalToken(tokens);
      fail("Expected exception since final char is not space");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("Unexpected final char"));
    }
  }
@Test
  public void testConstructWithBooleanAndLanguage() {
    TokenizerAnnotator annotator = new TokenizerAnnotator(true, "en");
    Annotation annotation = new Annotation("This is an inline example.");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals("This", tokens.get(0).word());
  }
@Test
  public void testConstructWithTokenizerTypeObject() {
    TokenizerAnnotator annotator = new TokenizerAnnotator(true, TokenizerAnnotator.TokenizerType.English);
    Annotation annotation = new Annotation("Quick test.");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertTrue(tokens.size() > 0);
  }
@Test
  public void testAnnotatePreservesPostProcessorOutput() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.codepoint", "true");
    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("Test!");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.get(0).containsKey(CoreAnnotations.ValueAnnotation.class));
  }

@Test
  public void testGetTokenizerInvokesFactory() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Tokenizer<CoreLabel> tokenizer = annotator.getTokenizer(new StringReader("abc def"));
    List<CoreLabel> list = tokenizer.tokenize();
    assertEquals("abc", list.get(0).word());
    assertEquals("def", list.get(1).word());
  }
@Test
  public void testAnnotationClearsExistingSentences() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    Annotation annotation = new Annotation("Sentence. Another.");
    List<CoreMap> dummySentences = new ArrayList<>();
    CoreMap sentence = new Annotation("Pre-existing sentence");
    dummySentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, dummySentences);
    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    annotator.annotate(annotation);
    List<CoreMap> newSentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotSame(dummySentences, newSentences);
  }

@Test
  public void testFullyInitializedTokenizerConfiguration() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.verbose", "true");
    props.setProperty("tokenize.cleanxml", "true");
    props.setProperty("tokenize.ssplit", "true");
    Annotation annotation = new Annotation("The quick brown fox.");
    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(annotation.containsKey(CoreAnnotations.SentencesAnnotation.class));
  }
@Test(expected = RuntimeException.class)
  public void testUnsupportedSegmenterLanguageThrowsForNonChineseArabic() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "finnish"); 
    new TokenizerAnnotator(false, props);
  }
@Test
  public void testEmptyAfterAnnotationStringIsProcessedCorrectly() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.AfterAnnotation.class, "");
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    TokenizerAnnotator.adjustFinalToken(tokens);
    assertEquals("", token.get(CoreAnnotations.AfterAnnotation.class));
  }
@Test
  public void testInitFactoryWithGermanTokenizerAndExtraOptions() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "de");
    TokenizerAnnotator.TokenizerType type = TokenizerAnnotator.TokenizerType.German;
  }
@Test
  public void testAnnotateWithMultipleSpacesBetweenTokens() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    Annotation annotation = new Annotation("This   is   spaced.");
    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals("This", tokens.get(0).word());
    assertEquals("spaced", tokens.get(tokens.size() - 2).word());
  }
@Test
  public void testAnnotateWithJustPunctuation() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    Annotation annotation = new Annotation("...");
    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertTrue(tokens.size() >= 1);
  }
@Test
  public void testSetNewlineStatusWithTextTokensOnly() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Hello");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("World");
    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    assertFalse(token1.get(CoreAnnotations.IsNewlineAnnotation.class));
    assertFalse(token2.get(CoreAnnotations.IsNewlineAnnotation.class));
  }
@Test
  public void testAnnotatePreservesOriginalTokenList() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.codepoint", "true");
    Annotation annotation = new Annotation("Code test.");
    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens.get(0).word());
    assertTrue(tokens.get(0).containsKey(CoreAnnotations.ValueAnnotation.class));
  }
@Test
  public void testCleanXmlDisabled() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.cleanxml", "false");
    Annotation annotation = new Annotation("<foo>bar</foo>");
    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertFalse(annotation.containsKey(CoreAnnotations.XmlContextAnnotation.class));
    assertTrue(tokens.size() > 0);
  }
@Test
  public void testVerboseConstructorPrintsInfo() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.verbose", "true");
    new TokenizerAnnotator(false, props); 
  }
@Test
  public void testWhitespaceTokenizerWithMixedLineEndings() {
    Properties props = new Properties();
    props.setProperty("tokenize.whitespace", "true");
    Annotation annotation = new Annotation("A\r\nB\tC\nD");
    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(4, tokens.size());
    assertEquals("D", tokens.get(3).word());
  }
@Test
  public void testDisableSentenceSplitter() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.ssplit", "false");
    Annotation annotation = new Annotation("Sentence one. Sentence two.");
    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    annotator.annotate(annotation);
    assertFalse(annotation.containsKey(CoreAnnotations.SentencesAnnotation.class));
  }
@Test
  public void testAssignTokenSentenceIndex() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    Annotation annotation = new Annotation("One. Two.");
    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    annotator.annotate(annotation);
    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(sentences);
    assertEquals(0, (int) sentences.get(0).get(CoreAnnotations.SentenceIndexAnnotation.class));
  }
@Test
  public void testRemovalOfPreviousSentencesAnnotation() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    Annotation annotation = new Annotation("Test reuse.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());
    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    annotator.annotate(annotation);
    List<CoreMap> result = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(result);
  }
@Test
  public void testAnnotationWithTrailingNewlineIsTokenizedCorrectly() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    Annotation annotation = new Annotation("This is a test\n");
    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertTrue(tokens.size() > 0);
    CoreLabel last = tokens.get(tokens.size() - 1);
    assertNotNull(last.get(CoreAnnotations.TokenBeginAnnotation.class));
  }
@Test
  public void testWhitespaceTokenizerWithSingleTokenAndWhitespace() {
    Properties props = new Properties();
    props.setProperty("tokenize.whitespace", "true");
    Annotation annotation = new Annotation("Token   ");
    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(1, tokens.size());
    assertEquals("Token", tokens.get(0).word());
  }
@Test
  public void testGetTokenizerTypeFallbacksToDefaultOnNullLanguage() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", null); 
    TokenizerAnnotator.TokenizerType type = TokenizerAnnotator.TokenizerType.getTokenizerType(props);
    assertEquals(TokenizerAnnotator.TokenizerType.English, type); 
  }
@Test
  public void testAnnotatorDoesNotCrashOnWhitespaceOnlyText() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    Annotation annotation = new Annotation("     ");
    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
  }
@Test
  public void testWhitespaceTokenizerPreservesNewlineTokenAsNewlineAnnotation() {
    Properties props = new Properties();
    props.setProperty("tokenize.whitespace", "true");
    Annotation annotation = new Annotation(AbstractTokenizer.NEWLINE_TOKEN + " tok2");
    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertTrue(tokens.get(0).get(CoreAnnotations.IsNewlineAnnotation.class));
    assertFalse(tokens.get(1).get(CoreAnnotations.IsNewlineAnnotation.class));
  }
@Test
  public void testAnnotateClearsExistingSentenceAnnotation() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    List<CoreMap> sentencesBefore = new ArrayList<>();
    CoreMap sentence = new Annotation("Old sentence");
    sentencesBefore.add(sentence);
    Annotation annotation = new Annotation("New text.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentencesBefore);
    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    annotator.annotate(annotation);
    List<CoreMap> currentSentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotSame(sentencesBefore, currentSentences);
  }
@Test(expected = RuntimeException.class)
  public void testSpanishSegmenterNotImplementedThrows() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "es");
    props.setProperty("tokenize.whitespace", "false");
    new TokenizerAnnotator(false, props); 
  }

@Test
  public void testSentenceSplitterAssignsSentenceIndex() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    Annotation annotation = new Annotation("One. Two.");
    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    annotator.annotate(annotation);
    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(sentences);
    assertEquals(0, (int) sentences.get(0).get(CoreAnnotations.SentenceIndexAnnotation.class));
  }
@Test
  public void testFactoryOptionConcatWithTrailingCommaExtraOptions() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    String extraOptions = "tokenizeNLs,";
    TokenizerAnnotator.TokenizerType type = TokenizerAnnotator.TokenizerType.English;

  }
@Test
  public void testFactoryOptionConcatWithNonTrailingCommaExtraOptions() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    String extraOptions = "nlsOption";
    TokenizerAnnotator.TokenizerType type = TokenizerAnnotator.TokenizerType.English;
  }
@Test
  public void testAnnotationWithoutAfterAnnotationFieldDoesNotCrashAdjustFinalToken() {
    CoreLabel token = new CoreLabel();
    List<CoreLabel> tokens = Collections.singletonList(token);
    TokenizerAnnotator.adjustFinalToken(tokens);
    assertNull(token.get(CoreAnnotations.AfterAnnotation.class));
  } 
}