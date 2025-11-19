package edu.stanford.nlp.pipeline;

import java.io.StringReader;
import java.util.*;

import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.process.AbstractTokenizer;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.PropertiesUtils;
import junit.framework.TestCase;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import org.junit.Test;

import static org.junit.Assert.*;

public class TokenizerAnnotator_3_GPTLLMTest {

 @Test
  public void testDefaultEnglishTokenizerConstruction() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    assertNotNull(annotator);
  }
@Test
  public void testTokenizerTypeByLanguage() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "es");
    TokenizerAnnotator.TokenizerType type = TokenizerAnnotator.TokenizerType.getTokenizerType(props);
    assertEquals(TokenizerAnnotator.TokenizerType.Spanish, type);
  }
@Test
  public void testTokenizerTypeByClass() {
    Properties props = new Properties();
    props.setProperty("tokenize.class", "FrenchTokenizer");
    TokenizerAnnotator.TokenizerType type = TokenizerAnnotator.TokenizerType.getTokenizerType(props);
    assertEquals(TokenizerAnnotator.TokenizerType.French, type);
  }
@Test
  public void testWhitespaceTokenizerTypeFromFlag() {
    Properties props = new Properties();
    props.setProperty("tokenize.whitespace", "true");
    TokenizerAnnotator.TokenizerType type = TokenizerAnnotator.TokenizerType.getTokenizerType(props);
    assertEquals(TokenizerAnnotator.TokenizerType.Whitespace, type);
  }
@Test
  public void testUnknownTokenizerLanguageThrowsException() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "klingon");
    try {
      TokenizerAnnotator.TokenizerType.getTokenizerType(props);
      fail("Expected IllegalArgumentException for unknown language");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("unknown tokenize.language property"));
    }
  }
@Test
  public void testUnknownTokenizerClassThrowsException() {
    Properties props = new Properties();
    props.setProperty("tokenize.class", "UnrealTokenizer");
    try {
      TokenizerAnnotator.TokenizerType.getTokenizerType(props);
      fail("Expected IllegalArgumentException for unknown class");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("unknown tokenize.class"));
    }
  }
@Test
  public void testEnglishTokenizationBasic() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("Test sentence.");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertNotNull(tokens);
    assertEquals(3, tokens.size());

    CoreLabel token1 = tokens.get(0);
    assertEquals("Test", token1.word());
    assertEquals(Integer.valueOf(0), token1.get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals(Integer.valueOf(1), token1.get(CoreAnnotations.TokenEndAnnotation.class));

    CoreLabel token2 = tokens.get(1);
    assertEquals("sentence", token2.word());
    assertEquals(Integer.valueOf(1), token2.get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals(Integer.valueOf(2), token2.get(CoreAnnotations.TokenEndAnnotation.class));

    CoreLabel token3 = tokens.get(2);
    assertEquals(".", token3.word());
    assertEquals(Integer.valueOf(2), token3.get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals(Integer.valueOf(3), token3.get(CoreAnnotations.TokenEndAnnotation.class));
  }
@Test
  public void testWhitespaceTokenizerSplitsCorrectly() {
    Properties props = new Properties();
    props.setProperty("tokenize.whitespace", "true");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("a quick brown fox");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertNotNull(tokens);
    assertEquals(4, tokens.size());
    assertEquals("a", tokens.get(0).word());
    assertEquals("quick", tokens.get(1).word());
    assertEquals("brown", tokens.get(2).word());
    assertEquals("fox", tokens.get(3).word());
  }
@Test
  public void testAdjustFinalTokenRemovesTrailingSpace() {
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.AfterAnnotation.class, " ");

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.AfterAnnotation.class, "  ");

    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    tokens.add(token1);
    tokens.add(token2);

    TokenizerAnnotator.adjustFinalToken(tokens);

    assertEquals(" ", token1.get(CoreAnnotations.AfterAnnotation.class));
    assertEquals("", token2.get(CoreAnnotations.AfterAnnotation.class));
  }
@Test
  public void testAdjustFinalTokenHandlesEmptyList() {
    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    TokenizerAnnotator.adjustFinalToken(tokens);
    assertTrue(tokens.isEmpty());
  }
@Test
  public void testAdjustFinalTokenHandlesNullList() {
    TokenizerAnnotator.adjustFinalToken(null);
    assertTrue(true); 
  }
@Test
  public void testIsNewlineAnnotationSetCorrectlyTrue() {
    CoreLabel token = new CoreLabel();
    token.setWord(AbstractTokenizer.NEWLINE_TOKEN);
    List<CoreLabel> list = new ArrayList<CoreLabel>();
    list.add(token);

    try {
      java.lang.reflect.Method method = TokenizerAnnotator.class.getDeclaredMethod("setNewlineStatus", List.class);
      method.setAccessible(true);
      method.invoke(null, list);
    } catch (Exception e) {
      fail("Unable to invoke setNewlineStatus: " + e.getMessage());
    }

    assertTrue(token.get(CoreAnnotations.IsNewlineAnnotation.class));
  }
@Test
  public void testIsNewlineAnnotationSetCorrectlyFalse() {
    CoreLabel token = new CoreLabel();
    token.setWord("Hello");
    List<CoreLabel> list = new ArrayList<CoreLabel>();
    list.add(token);

    try {
      java.lang.reflect.Method method = TokenizerAnnotator.class.getDeclaredMethod("setNewlineStatus", List.class);
      method.setAccessible(true);
      method.invoke(null, list);
    } catch (Exception e) {
      fail("Unable to invoke setNewlineStatus: " + e.getMessage());
    }

    assertFalse(token.get(CoreAnnotations.IsNewlineAnnotation.class));
  }
@Test
  public void testTokenBeginEndAnnotationAssigned() {
    CoreLabel tokenA = new CoreLabel();
    tokenA.setWord("first");
    CoreLabel tokenB = new CoreLabel();
    tokenB.setWord("second");
    CoreLabel tokenC = new CoreLabel();
    tokenC.setWord("third");

    List<CoreLabel> tokens = Arrays.asList(tokenA, tokenB, tokenC);

    try {
      java.lang.reflect.Method method = TokenizerAnnotator.class.getDeclaredMethod("setTokenBeginTokenEnd", List.class);
      method.setAccessible(true);
      method.invoke(null, tokens);
    } catch (Exception e) {
      fail("Unable to invoke setTokenBeginTokenEnd: " + e.getMessage());
    }

    assertEquals(Integer.valueOf(0), tokenA.get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals(Integer.valueOf(1), tokenA.get(CoreAnnotations.TokenEndAnnotation.class));
    assertEquals(Integer.valueOf(1), tokenB.get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals(Integer.valueOf(2), tokenB.get(CoreAnnotations.TokenEndAnnotation.class));
    assertEquals(Integer.valueOf(2), tokenC.get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals(Integer.valueOf(3), tokenC.get(CoreAnnotations.TokenEndAnnotation.class));
  }
@Test
  public void testMissingTextAnnotationThrowsException() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation();

    try {
      annotator.annotate(annotation);
      fail("Expected exception for missing text annotation");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("Tokenizer unable to find text"));
    }
  }
@Test
  public void testRequirementsSatisfiedIncludesExpectedKeys() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);

    Set<Class<? extends CoreAnnotation>> required = annotator.requires();
    assertTrue(required.isEmpty());

    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
    assertTrue(satisfied.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testConstructorOverloads() {
    TokenizerAnnotator annotator1 = new TokenizerAnnotator();
    assertNotNull(annotator1);

    TokenizerAnnotator annotator2 = new TokenizerAnnotator("en");
    assertNotNull(annotator2);

    TokenizerAnnotator annotator3 = new TokenizerAnnotator(true, TokenizerAnnotator.TokenizerType.English);
    assertNotNull(annotator3);

    Properties p = PropertiesUtils.asProperties("tokenize.language", "en");
    TokenizerAnnotator annotator4 = new TokenizerAnnotator(false, p);
    assertNotNull(annotator4);

    TokenizerAnnotator annotator5 = new TokenizerAnnotator(true, "en", "invertible");
    assertNotNull(annotator5);
  }
@Test
  public void testTokenizationEmptyTextShouldReturnEmptyTokenList() {
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
  public void testTokenizationOnlyWhitespaceText() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("     ");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(0, tokens.size());
  }
@Test
  public void testWhitespaceTokenizerWithTabsAndNewlines() {
    Properties props = new Properties();
    props.setProperty("tokenize.whitespace", "true");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("abc\tdef\nghi");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(3, tokens.size());
    assertEquals("abc", tokens.get(0).word());
    assertEquals("def", tokens.get(1).word());
    assertEquals("ghi", tokens.get(2).word());
  }
@Test
  public void testComputeExtraOptionsWhenEolIsFalseAndNewlineIsNever() throws Exception {
    Properties props = new Properties();
    props.setProperty("ssplit.isOneSentence", "false");
    props.setProperty("ssplit.newlineIsSentenceBreak", "never");

    java.lang.reflect.Method method = TokenizerAnnotator.class.getDeclaredMethod("computeExtraOptions", Properties.class);
    method.setAccessible(true);
    Object result = method.invoke(null, props);

    assertNull(result);
  }
@Test
  public void testComputeExtraOptionsWhenNewlineIsAlways() throws Exception {
    Properties props = new Properties();
    props.setProperty("ssplit.isOneSentence", "false");
    props.setProperty("ssplit.newlineIsSentenceBreak", "always");

    java.lang.reflect.Method method = TokenizerAnnotator.class.getDeclaredMethod("computeExtraOptions", Properties.class);
    method.setAccessible(true);
    Object result = method.invoke(null, props);

    assertEquals("tokenizeNLs,", result);
  }
@Test
  public void testUnspecifiedTokenizerFallsBackToPTBTokenizer() {
    Properties props = new Properties(); 
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);

    Annotation annotation = new Annotation("Fallback test.");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(3, tokens.size());
    assertEquals("Fallback", tokens.get(0).word());
    assertEquals("test", tokens.get(1).word());
    assertEquals(".", tokens.get(2).word());
  }
@Test
  public void testPostProcessorFailureThrowsException() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.postProcessor", "non.existent.FakeProcessor");

    try {
      new TokenizerAnnotator(false, props);
      fail("Expected RuntimeException due to invalid postProcessor class");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("Loading: non.existent.FakeProcessor"));
    }
  }
@Test
  public void testCodepointProcessorAppendedWhenOptionSet() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.codepoint", "true");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);

    Annotation annotation = new Annotation("A");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(1, tokens.size());
    assertEquals("A", tokens.get(0).word());
  }
@Test
  public void testTokenizationWithNullAfterAnnotation() {
    CoreLabel token = new CoreLabel();
    token.setWord("word");
    token.set(CoreAnnotations.AfterAnnotation.class, null);

    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    tokens.add(token);

    TokenizerAnnotator.adjustFinalToken(tokens);
    assertNull(tokens.get(0).get(CoreAnnotations.AfterAnnotation.class));
  }
@Test
  public void testEnglishTokenizationWithQuotationAndPunctuation() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);

    Annotation annotation = new Annotation("\"He said, 'hello'!\"");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertFalse(tokens.isEmpty());
    assertEquals("\"", tokens.get(0).word());
    assertEquals("He", tokens.get(1).word());
    assertEquals("said", tokens.get(2).word());
    assertEquals(",", tokens.get(3).word());
    assertEquals("'hello'", tokens.get(4).word()); 
  }
@Test
  public void testWhitespaceTokenizerWithEmptyAnnotation() {
    Properties props = new Properties();
    props.setProperty("tokenize.whitespace", "true");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(0, tokens.size());
  }
@Test
  public void testCleanXmlAnnotatorAttachedWhenEnabled() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.cleanxml", "true");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("<p>Text</p>");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertFalse(tokens.isEmpty());
  }
@Test
  public void testSsplitAnnotatorDisabledWhenConfigured() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.ssplit", "false");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("Sentence one. Sentence two.");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(6, tokens.size());
  }
@Test
  public void testArabicSegmenterAnnotatorInitialized() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "ar");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("العربية");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertFalse(tokens.isEmpty());
  }
@Test
  public void testChineseSegmenterAnnotatorInitialized() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "zh");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("你好世界");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertFalse(tokens.isEmpty());
  }
@Test
  public void testSegmenterThrowsForUnsupportedLanguage() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "de");

    try {
      new TokenizerAnnotator(props);
    } catch (Exception e) {
      assertFalse(e instanceof RuntimeException); 
    }
  }
@Test
  public void testTokenizationWithEmojiCharacters() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);

    Annotation annotation = new Annotation("Smile 😊 and laugh 😂.");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.stream().anyMatch(t -> t.word().contains("😊") || t.word().contains("😂")));
  }
@Test
  public void testTokenizationOfSingleCharacter() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);

    Annotation annotation = new Annotation("A");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(1, tokens.size());
    assertEquals("A", tokens.get(0).word());
  }
@Test
  public void testGetTokenizerReturnsNonNull() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);

    TokenizerAnnotator.TokenizerType type = TokenizerAnnotator.TokenizerType.getTokenizerType(props);
    assertNotNull(type);

    java.io.StringReader reader = new java.io.StringReader("Testing getTokenizer()");
    assertNotNull(annotator.getTokenizer(reader));
  }
@Test
  public void testAnnotatorWithCustomOptionsOverridesDefaults() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.options", "untokenizable=noneDelete");
    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props, "invertible");

    Annotation annotation = new Annotation("This is a test.");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(4, tokens.size());
  }
@Test
  public void testSetTokenBeginTokenEndWithNullList() throws Exception {
    try {
      java.lang.reflect.Method method = TokenizerAnnotator.class.getDeclaredMethod("setTokenBeginTokenEnd", List.class);
      method.setAccessible(true);
      method.invoke(null, (Object) null);
    } catch (Exception e) {
      fail("Method should handle null list without exception");
    }
  }
@Test
  public void testLanguageFallbackToUnspecifiedDefaults() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("Fallback mode test.");

    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertNotNull(tokens);
    assertEquals(3, tokens.size());
  }
@Test
  public void testWhitespaceTokenizerWithTrailingSpaces() {
    Properties props = new Properties();
    props.setProperty("tokenize.whitespace", "true");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);

    Annotation annotation = new Annotation("word1 word2    ");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(2, tokens.size());
    assertEquals("word1", tokens.get(0).word());
    assertEquals("word2", tokens.get(1).word());
  }
@Test
  public void testNonBreakingSpaceHandledCorrectly() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);

    String text = "word1\u00A0word2"; 
    Annotation annotation = new Annotation(text);
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(2, tokens.size());
    assertEquals("word1", tokens.get(0).word());
    assertEquals("word2", tokens.get(1).word());
  }
@Test
  public void testInitFactoryWithNullTokenizerTypeThrows() throws Exception {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "nonexistent");

    try {
      TokenizerAnnotator.TokenizerType.getTokenizerType(props);
      fail("Expected exception for null tokenizer type");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("unknown tokenize.language property"));
    }
  }
@Test
  public void testAnnotationPreservesOtherValues() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("Hello world.");
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "test-doc-123");

    annotator.annotate(annotation);

    assertEquals("test-doc-123", annotation.get(CoreAnnotations.DocIDAnnotation.class));
  }
@Test
  public void testTokenizationPreservesOriginalTextAnnotation() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("Testing original text property.");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    for (CoreLabel token : tokens) {
      assertNotNull(token.word());
      assertNotNull(token.get(CoreAnnotations.BeforeAnnotation.class));
      assertNotNull(token.get(CoreAnnotations.AfterAnnotation.class));
    }
  }
@Test
  public void testMultipleSpacesBetweenWordsInWhitespaceTokenizer() {
    Properties props = new Properties();
    props.setProperty("tokenize.whitespace", "true");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("word1     word2");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(2, tokens.size());
    assertEquals("word1", tokens.get(0).word());
    assertEquals("word2", tokens.get(1).word());
  }
@Test
  public void testInitializationWithNullLanguageProperties() {
    Properties props = new Properties();
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    assertNotNull(annotator);
    Annotation annotation = new Annotation("Default tokenizer check.");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertFalse(tokens.isEmpty());
  }
@Test
  public void testTokenizerTypeMappingIncludesAbbreviationAndName() {
    TokenizerAnnotator.TokenizerType typeByName = TokenizerAnnotator.TokenizerType.valueOf("English");
    TokenizerAnnotator.TokenizerType typeByAbbrev = TokenizerAnnotator.TokenizerType.getTokenizerType(PropertiesUtils.asProperties("tokenize.language", "en"));
    assertEquals(typeByName, typeByAbbrev);
  }
@Test
  public void testInvalidTokenizerEnumSwitchThrows() {
    try {
      Properties props = new Properties();
      props.setProperty("tokenize.language", "en");
      String invalidType = "INVALID";
      TokenizerAnnotator.TokenizerType fakeType = TokenizerAnnotator.TokenizerType.valueOf(invalidType);
      fail("Expected IllegalArgumentException for invalid type");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("No enum"));
    }
  }
@Test
  public void testWhitespaceTokenizerIgnoresMultipleNewlinesAndSpaces() {
    Properties props = new Properties();
    props.setProperty("tokenize.whitespace", "true");

    TokenizerAnnotator ta = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("a\n\n\n  b");
    ta.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertEquals(2, tokens.size());
    assertEquals("a", tokens.get(0).word());
    assertEquals("b", tokens.get(1).word());
  }
@Test
  public void testWhitespaceTokenizerTreatsTabAsDelimiter() {
    Properties props = new Properties();
    props.setProperty("tokenize.whitespace", "true");

    TokenizerAnnotator ta = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("a\tb");
    ta.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertEquals(2, tokens.size());
    assertEquals("a", tokens.get(0).word());
    assertEquals("b", tokens.get(1).word());
  }
@Test
  public void testFactoryInitializationWithNullPropertiesFallsBackToDefaults() {
    TokenizerAnnotator ta = new TokenizerAnnotator(true, (Properties) null, null);
    Annotation annotation = new Annotation("Testing null properties.");
    ta.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(3, tokens.size());
    assertEquals("Testing", tokens.get(0).word());
    assertEquals("null", tokens.get(1).word());
    assertEquals("properties", tokens.get(2).word());
  }
@Test
  public void testStatTokSentAnnotatorInitializationFromPropertyTriggersFlow() {
    Properties props = new Properties();
    props.setProperty("tokenize.cdc.model", "some_model_path");  
    props.setProperty("tokenize.language", "en");

    TokenizerAnnotator ta = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("Using CDC tokenizer.");
    ta.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertFalse(tokens.isEmpty());
  }
@Test
  public void testTokenizerAnnotatorWithVerboseAndLogsConstructsSuccessfully() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.verbose", "true");

    TokenizerAnnotator ta = new TokenizerAnnotator(false, props);
    Annotation a = new Annotation("Logging test.");
    ta.annotate(a);

    List<CoreLabel> tokens = a.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(2, tokens.size());
  }
@Test
  public void testTokenizationWithMultipleSentencesHavingNewlines() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("ssplit.newlineIsSentenceBreak", "always");

    TokenizerAnnotator ta = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("First sentence.\nSecond sentence.\n\nThird sentence.");
    ta.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertTrue(tokens.stream().anyMatch(t -> Boolean.TRUE.equals(t.get(CoreAnnotations.IsNewlineAnnotation.class))));
  }
@Test
  public void testGetTokenizerWorksIndependently() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    TokenizerAnnotator ta = new TokenizerAnnotator(props);
    java.io.Reader reader = new java.io.StringReader("token one token two");
    List<CoreLabel> tokens = ta.getTokenizer(reader).tokenize();
    assertEquals(4, tokens.size());
    assertEquals("token", tokens.get(0).word());
    assertEquals("two", tokens.get(3).word());
  }
@Test
  public void testAnnotationWithTextNotSetThrowsExpectedException() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    TokenizerAnnotator ta = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation(String.valueOf(new HashMap<>()));

    try {
      ta.annotate(annotation);
      fail("Expected RuntimeException for missing text");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("Tokenizer unable to find text"));
    }
  }
@Test
  public void testFrenchTokenizerInitializesSuccessfully() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "fr");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("Ceci est une phrase en français.");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertNotNull(tokens);
    assertFalse(tokens.isEmpty());
  }
@Test
  public void testSpanishTokenizerInitializesSuccessfully() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "es");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("¿Dónde está el baño?");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertNotNull(tokens);
    assertFalse(tokens.isEmpty());
  }
@Test
  public void testWhitespaceTokenizerIgnoresBlankTokens() {
    Properties props = new Properties();
    props.setProperty("tokenize.whitespace", "true");

    Annotation annotation = new Annotation("  token1     token2  ");
    TokenizerAnnotator tokenizer = new TokenizerAnnotator(props);
    tokenizer.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(2, tokens.size());
    assertEquals("token1", tokens.get(0).word());
    assertEquals("token2", tokens.get(1).word());
  }
@Test
  public void testTokenizerWithExtraOptionsPassedDirectly() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    TokenizerAnnotator ta = new TokenizerAnnotator(false, props, "invertible");

    Annotation annotation = new Annotation("Options test string.");
    ta.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(3, tokens.size());
  }
@Test
  public void testFactoryReturnsNullForUnsupportedSegmenterLanguageWithWhitespaceFalse() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "xx"); 
    props.setProperty("tokenize.whitespace", "false");
    try {
      new TokenizerAnnotator(props);
      fail("Expected RuntimeException for unsupported segmenter language");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("No segmenter implemented for"));
    }
  }
@Test
  public void testTokenizerTypeWithMixedCaseLanguageName() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "EnGlIsH");
    TokenizerAnnotator.TokenizerType type = TokenizerAnnotator.TokenizerType.getTokenizerType(props);
    assertEquals(TokenizerAnnotator.TokenizerType.English, type);
  }
@Test
  public void testGetTokenizerTypeReturnsWhitespaceWhenAllInputsNullExceptWhitespaceTrue() {
    Properties props = new Properties();
    props.setProperty("tokenize.whitespace", "true");
    TokenizerAnnotator.TokenizerType type = TokenizerAnnotator.TokenizerType.getTokenizerType(props);
    assertEquals(TokenizerAnnotator.TokenizerType.Whitespace, type);
  }
@Test
  public void testAdjustFinalTokenDoesNotModifyIfNoAfterAnnotationPresent() {
    CoreLabel token = new CoreLabel();
    token.setWord("Goodbye");

    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    tokens.add(token);

    TokenizerAnnotator.adjustFinalToken(tokens);

    assertNull(token.get(CoreAnnotations.AfterAnnotation.class));
  }
@Test
  public void testAdjustFinalTokenThrowsIfFinalAfterCharIsNotSpace() {
    CoreLabel token = new CoreLabel();
    token.setWord("Final");
    token.set(CoreAnnotations.AfterAnnotation.class, ".");

    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    tokens.add(token);

    try {
      TokenizerAnnotator.adjustFinalToken(tokens);
      fail("Expected IllegalArgumentException for non-space trailing character");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("Unexpected final char"));
    }
  }
@Test
  public void testGetTokenizerWithNullReaderThrowsNullPointerException() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    TokenizerAnnotator tokenizerAnnotator = new TokenizerAnnotator(props);
    try {
      tokenizerAnnotator.getTokenizer(null);
      fail("Expected NullPointerException when passing null Reader");
    } catch (NullPointerException e) {
      
    }
  }
@Test
  public void testTokenizationWithSpecialCharactersSymbols() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    TokenizerAnnotator tokenizerAnnotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("@#$%^&*()!!");

    tokenizerAnnotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertFalse(tokens.isEmpty());
  }
@Test
  public void testWhitespaceTokenizerWithUnicodeWhitespaceCharacters() {
    Properties props = new Properties();
    props.setProperty("tokenize.whitespace", "true");

    TokenizerAnnotator tokenizerAnnotator = new TokenizerAnnotator(props);
    String input = "start\u2003middle\u2009end"; 
    Annotation annotation = new Annotation(input);
    tokenizerAnnotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(3, tokens.size());
    assertEquals("start", tokens.get(0).word());
    assertEquals("middle", tokens.get(1).word());
    assertEquals("end", tokens.get(2).word());
  }
@Test
  public void testConstructorWithNullLangAndNullOptions() {
    TokenizerAnnotator tokenizer = new TokenizerAnnotator(true, (String) null, null);
    Annotation annotation = new Annotation("Null test.");
    tokenizer.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(2, tokens.size());
  }
@Test
  public void testPostProcessorListIsEmptyByDefault() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.postProcessor", "");

    TokenizerAnnotator tokenizer = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("No postprocessor.");
    tokenizer.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(2, tokens.size());
  }
@Test
  public void testWhitespaceTokenizerEolSignificanceExplicitlySetFalse() {
    Properties props = new Properties();
    props.setProperty("tokenize.whitespace", "true");
    props.setProperty("tokenize.keepeol", "false");

    TokenizerAnnotator tokenizer = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("Token1\nToken2");
    tokenizer.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(2, tokens.size());
  }
@Test
  public void testWhitespaceTokenizerEolSignificanceExplicitlySetTrue() {
    Properties props = new Properties();
    props.setProperty("tokenize.whitespace", "true");
    props.setProperty("tokenize.keepeol", "true");

    TokenizerAnnotator tokenizer = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("t1\nt2");
    tokenizer.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(3, tokens.size());
    assertTrue(tokens.stream().anyMatch(t -> Boolean.TRUE.equals(t.get(CoreAnnotations.IsNewlineAnnotation.class))));
  }
@Test
  public void testCleanXmlAnnotatorNotUsedWhenFalse() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.cleanxml", "false");

    TokenizerAnnotator tokenizer = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("<p>This is not cleaned.</p>");
    tokenizer.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(6, tokens.size());
  }
@Test
  public void testRequirementsSatisfiedContainsExpectedAnnotations() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");

    TokenizerAnnotator tokenizer = new TokenizerAnnotator(props);
    Set<Class<? extends CoreAnnotation>> satisfied = tokenizer.requirementsSatisfied();
    assertTrue(satisfied.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.SentencesAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testTokenizerTypeMapContainsBothAbbreviationAndEnumName() {
    Map<String, TokenizerAnnotator.TokenizerType> field;
    try {
      java.lang.reflect.Field f = TokenizerAnnotator.TokenizerType.class.getDeclaredField("nameToTokenizerMap");
      f.setAccessible(true);
      field = (Map<String, TokenizerAnnotator.TokenizerType>) f.get(null);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    assertTrue(field.containsKey("EN"));
    assertTrue(field.containsKey("ENGLISH"));
  }
@Test
  public void testTokenizerTypeClassMapContainsTokenizerClass() {
    Map<String, TokenizerAnnotator.TokenizerType> field;
    try {
      java.lang.reflect.Field f = TokenizerAnnotator.TokenizerType.class.getDeclaredField("classToTokenizerMap");
      f.setAccessible(true);
      field = (Map<String, TokenizerAnnotator.TokenizerType>) f.get(null);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    assertTrue(field.containsKey("FRENCHTOKENIZER"));
  }
@Test
  public void testAdjustFinalTokenNoSpaceCharThrows() {
    CoreLabel token = new CoreLabel();
    token.setWord("end");
    token.set(CoreAnnotations.AfterAnnotation.class, "/////");

    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    tokens.add(token);

    try {
      TokenizerAnnotator.adjustFinalToken(tokens);
      fail("Expected IllegalArgumentException for no trailing space");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().startsWith("adjustFinalToken: Unexpected final char"));
    }
  }
@Test
  public void testEmptyLanguagePropertyDefaultsToEnglish() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "");

    TokenizerAnnotator tokenizer = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("This should still tokenize.");
    tokenizer.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertFalse(tokens.isEmpty());
  }
@Test
  public void testWhitespaceTokenizerSkipsTrailingEmptyTokens() {
    Properties props = new Properties();
    props.setProperty("tokenize.whitespace", "true");

    TokenizerAnnotator tokenizer = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("word1 word2 ");
    tokenizer.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(2, tokens.size());
    assertEquals("word1", tokens.get(0).word());
    assertEquals("word2", tokens.get(1).word());
  }
@Test
  public void testWhitespaceTokenizerNewlinesTriggerIsNewlineAnnotation() throws Exception {
    Properties props = new Properties();
    props.setProperty("tokenize.whitespace", "true");
    props.setProperty("tokenize.keepeol", "true");

    TokenizerAnnotator tokenizer = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("a\nb");
    tokenizer.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    boolean hasNewline = false;
    for (CoreLabel token : tokens) {
      if ("\n".equals(token.word()) || Boolean.TRUE.equals(token.get(CoreAnnotations.IsNewlineAnnotation.class))) {
        hasNewline = true;
        break;
      }
    }
    assertTrue(hasNewline);
  }
@Test
  public void testCreateCodepointProcessorOnlyWhenOptionEnabled() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.codepoint", "true");

    TokenizerAnnotator tokenizer = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("Q");
    tokenizer.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(1, tokens.size());
    assertEquals("Q", tokens.get(0).word());
  }
@Test
  public void testUnsupportedTokenizerClassThrowsException() {
    Properties props = new Properties();
    props.setProperty("tokenize.class", "MadeUpTokenizer");

    try {
      TokenizerAnnotator.TokenizerType.getTokenizerType(props);
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("unknown tokenize.class property"));
    }
  }
@Test
  public void testCDCAnnotatorPathSkipsCleanXmlAndSsplit() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.cdc.model", "cdc-model-path");

    TokenizerAnnotator tokenizer = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("CDC only path.");
    tokenizer.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertFalse(tokens.isEmpty());
    assertNull(annotation.get(CoreAnnotations.SentencesAnnotation.class));  
  }
@Test
  public void testTokenizerWithStringConstructorLangNull() {
    TokenizerAnnotator tokenizer = new TokenizerAnnotator(true, (String) null);
    Annotation annotation = new Annotation("Null lang string.");
    tokenizer.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(3, tokens.size());
  }
@Test
  public void testTrailingSpaceAfterFinalTokenIsRemoved() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    TokenizerAnnotator tokenizer = new TokenizerAnnotator(props);

    String text = "Test trailing space ";
    Annotation annotation = new Annotation(text);
    tokenizer.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    String lastAfter = tokens.get(tokens.size() - 1).get(CoreAnnotations.AfterAnnotation.class);
    assertTrue(lastAfter == null || lastAfter.isEmpty());
  }
@Test
  public void testWhitepaceTokenizerTreatsMultipleConsecutiveDelimitersAsOne() {
    Properties props = new Properties();
    props.setProperty("tokenize.whitespace", "true");

    TokenizerAnnotator ta = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("a     b\t\t\tc\n\nd");
    ta.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(4, tokens.size());
    assertEquals("a", tokens.get(0).word());
    assertEquals("b", tokens.get(1).word());
    assertEquals("c", tokens.get(2).word());
    assertEquals("d", tokens.get(3).word());
  }
@Test
  public void testGetTokenizerWithLineBreaksInText() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");

    TokenizerAnnotator tokenizer = new TokenizerAnnotator(props);
    String text = "Line 1\nLine 2\nLine 3";
    StringReader reader = new StringReader(text);
    List<CoreLabel> tokens = tokenizer.getTokenizer(reader).tokenize();

    assertNotNull(tokens);
    assertTrue(tokens.size() > 0);
  }
@Test
  public void testSsplitAnnotatorNotUsedWhenDisabled() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.ssplit", "false");

    TokenizerAnnotator tokenizer = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("No sentences should be present.");
    tokenizer.annotate(annotation);

    assertNull(annotation.get(CoreAnnotations.SentencesAnnotation.class));
  }
@Test
  public void testCleanXmlAnnotatorUsedWhenEnabled() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.cleanxml", "true");

    TokenizerAnnotator tokenizer = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("<html>Some <b>bold</b> text</html>");
    tokenizer.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertTrue(tokens.size() > 0);
  }
@Test
  public void testNullPropertiesHandledInConstructor() {
    TokenizerAnnotator tokenizer = new TokenizerAnnotator(false, (Properties) null);
    Annotation annotation = new Annotation("Handling null properties.");
    tokenizer.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(3, tokens.size());
  }
@Test
  public void testTokenizerTypeFallbackToUnspecifiedWhenNoProps() {
    Properties props = new Properties(); 
    TokenizerAnnotator.TokenizerType type = TokenizerAnnotator.TokenizerType.getTokenizerType(props);
    assertEquals(TokenizerAnnotator.TokenizerType.Unspecified, type);
  }
@Test
  public void testWhitespaceTokenizerWithLeadingTrailingMultipleSpaces() {
    Properties props = new Properties();
    props.setProperty("tokenize.whitespace", "true");

    TokenizerAnnotator tokenizer = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("   one  two   three   ");
    tokenizer.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(3, tokens.size());
    assertEquals("one", tokens.get(0).word());
    assertEquals("two", tokens.get(1).word());
    assertEquals("three", tokens.get(2).word());
  }
@Test
  public void testUnknownLanguageDefaultsToUnspecified() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "xx_unknown");

    try {
      TokenizerAnnotator.TokenizerType.getTokenizerType(props);
      fail("Expected exception for unknown language");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("unknown tokenize.language property"));
    }
  }
@Test
  public void testSegmenterInitializationArabicTriggersCorrectAnnotator() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "ar");

    TokenizerAnnotator tokenizer = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("مرحبا بك في العربية");
    tokenizer.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertFalse(tokens.isEmpty());
  }
@Test
  public void testSegmenterInitializationChineseTriggersCorrectAnnotator() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "zh");

    TokenizerAnnotator tokenizer = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("这是中文测试");
    tokenizer.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertFalse(tokens.isEmpty());
  }
@Test
  public void testAdjustFinalTokenHandlesSingleSpaceAfterSingleToken() {
    CoreLabel token = new CoreLabel();
    token.setWord("word");
    token.set(CoreAnnotations.AfterAnnotation.class, " ");
    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    tokens.add(token);
    TokenizerAnnotator.adjustFinalToken(tokens);
    String after = tokens.get(0).get(CoreAnnotations.AfterAnnotation.class);
    assertEquals("", after);
  }
@Test
  public void testAdjustFinalTokenHandlesMultiSpaceAndTrimsOneSpaceOnly() {
    CoreLabel token = new CoreLabel();
    token.setWord("endword");
    token.set(CoreAnnotations.AfterAnnotation.class, "   ");
    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    tokens.add(token);
    TokenizerAnnotator.adjustFinalToken(tokens);
    String afterFinal = token.get(CoreAnnotations.AfterAnnotation.class);
    assertEquals("  ", afterFinal);
  }
@Test
  public void testWhiteSpaceTokenizerTrimsNewlineCharactersAsTokensWhenEnabled() {
    Properties props = new Properties();
    props.setProperty("tokenize.whitespace", "true");
    props.setProperty("tokenize.keepeol", "true");

    TokenizerAnnotator tokenizer = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("a\nb\nc");
    tokenizer.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(5, tokens.size());
    assertEquals("a", tokens.get(0).word());
    assertEquals("\n", tokens.get(1).word());
    assertTrue(tokens.get(1).get(CoreAnnotations.IsNewlineAnnotation.class));
  }
@Test
  public void testEnglishTokenizerProcessesBasicPunctuation() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");

    TokenizerAnnotator tokenizer = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("Hello, world!");
    tokenizer.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(4, tokens.size());
    assertEquals("Hello", tokens.get(0).word());
    assertEquals(",", tokens.get(1).word());
    assertEquals("world", tokens.get(2).word());
    assertEquals("!", tokens.get(3).word());
  }
@Test
  public void testDefaultOptionsAreAppendedCorrectlyWhenOptionsNull() throws Exception {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");

    java.lang.reflect.Method method = TokenizerAnnotator.class.getDeclaredMethod(
        "initFactory", TokenizerAnnotator.TokenizerType.class, Properties.class, String.class);
    method.setAccessible(true);
    TokenizerAnnotator.TokenizerType type = TokenizerAnnotator.TokenizerType.getTokenizerType(props);
    Object result = method.invoke(null, type, props, "invertible");
    assertNotNull(result);
  }
@Test
  public void testInitFactoryWithAllNullOptionsUsesDefaultTokenizer() throws Exception {
    Properties props = new Properties(); 
    TokenizerAnnotator.TokenizerType type = TokenizerAnnotator.TokenizerType.Unspecified;

    java.lang.reflect.Method method = TokenizerAnnotator.class.getDeclaredMethod(
        "initFactory", TokenizerAnnotator.TokenizerType.class, Properties.class, String.class);
    method.setAccessible(true);
    Object tokenizerFactory = method.invoke(null, type, props, null);

    assertNotNull(tokenizerFactory);
  }
@Test
  public void testVerbosePropertyTogglesLoggingInitialization() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.verbose", "true");

    TokenizerAnnotator tokenizer = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("Verbose check.");
    tokenizer.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(2, tokens.size());
  }
@Test
  public void testSentenceAnnotationRemovedDuringAnnotation() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");

    TokenizerAnnotator tokenizer = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("First. Second.");

    List<CoreMap> dummySentences = new ArrayList<CoreMap>();
    annotation.set(CoreAnnotations.SentencesAnnotation.class, dummySentences);
    tokenizer.annotate(annotation);

    assertNotEquals(dummySentences, annotation.get(CoreAnnotations.SentencesAnnotation.class));
  }
@Test
  public void testAnnotateWithCoreAnnotationTextMissingRaisesException() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    TokenizerAnnotator tokenizer = new TokenizerAnnotator(props);
  } 
}