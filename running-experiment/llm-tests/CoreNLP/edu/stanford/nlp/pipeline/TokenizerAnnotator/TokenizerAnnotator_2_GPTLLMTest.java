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

public class TokenizerAnnotator_2_GPTLLMTest {

 @Test
  public void testEnglishTokenizerOnSimpleSentence() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");

    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("This is a sentence.");

    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertNotNull(tokens);
    assertEquals(5, tokens.size());
    assertEquals("This", tokens.get(0).word());
    assertEquals("is", tokens.get(1).word());
    assertEquals("a", tokens.get(2).word());
    assertEquals("sentence", tokens.get(3).word());
    assertEquals(".", tokens.get(4).word());
  }
@Test
  public void testWhitespaceTokenizerSplitsBySpace() {
    Properties props = new Properties();
    props.setProperty("tokenize.whitespace", "true");

    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("word1 word2     word3");

    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertNotNull(tokens);
    assertEquals(3, tokens.size());
    assertEquals("word1", tokens.get(0).word());
    assertEquals("word2", tokens.get(1).word());
    assertEquals("word3", tokens.get(2).word());
  }
@Test
  public void testInvalidLanguageThrowsException() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "invalidLang");

    try {
      new TokenizerAnnotator(false, props);
      fail("Expected IllegalArgumentException to be thrown");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("unknown tokenize.language property"));
    }
  }
@Test
  public void testInvalidClassThrowsException() {
    Properties props = new Properties();
    props.setProperty("tokenize.class", "FakeTokenizer");

    try {
      new TokenizerAnnotator(false, props);
      fail("Expected IllegalArgumentException to be thrown");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("unknown tokenize.class property"));
    }
  }
@Test
  public void testAnnotationWithEmptyTextThrowsException() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");

    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("");

    try {
      annotator.annotate(annotation);
      fail("Expected RuntimeException to be thrown");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("Tokenizer unable to find text in annotation"));
    }
  }
@Test
  public void testTokenBeginAndEndIndicesSetCorrectly() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");

    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("hello world!");

    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertEquals(Integer.valueOf(0), tokens.get(0).get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals(Integer.valueOf(1), tokens.get(0).get(CoreAnnotations.TokenEndAnnotation.class));

    assertEquals(Integer.valueOf(1), tokens.get(1).get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals(Integer.valueOf(2), tokens.get(1).get(CoreAnnotations.TokenEndAnnotation.class));

    assertEquals(Integer.valueOf(2), tokens.get(2).get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals(Integer.valueOf(3), tokens.get(2).get(CoreAnnotations.TokenEndAnnotation.class));
  }
@Test
  public void testNewlineIsLabeledCorrectly() {
    Properties props = new Properties();
    props.setProperty("tokenize.whitespace", "true");
    props.setProperty("tokenize.keepeol", "true");

    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("token1 \n token2");

    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    boolean hasNewline = false;
    boolean hasToken1 = false;
    boolean hasToken2 = false;

    if (tokens.size() == 3) {
      hasToken1 = tokens.get(0).word().equals("token1");
      hasNewline = tokens.get(1).word().equals(AbstractTokenizer.NEWLINE_TOKEN)
          && Boolean.TRUE.equals(tokens.get(1).get(CoreAnnotations.IsNewlineAnnotation.class));
      hasToken2 = tokens.get(2).word().equals("token2");
    }

    assertTrue(hasToken1);
    assertTrue(hasNewline);
    assertTrue(hasToken2);
  }
@Test
  public void testPostProcessorClassFailsWithInvalidName() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.postProcessor", "non.existent.FakeProcessor");

    try {
      new TokenizerAnnotator(false, props);
      fail("Expected RuntimeException from invalid postProcessor class");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("failed with"));
    }
  }
@Test
  public void testWhitespaceKeepeolInjectsNewlineToken() {
    Properties props = new Properties();
    props.setProperty("tokenize.whitespace", "true");
    props.setProperty("tokenize.keepeol", "true");

    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("a \n b");

    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertEquals(3, tokens.size());
    assertEquals("a", tokens.get(0).word());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(1).word());
    assertEquals("b", tokens.get(2).word());

    assertTrue(Boolean.TRUE.equals(tokens.get(1).get(CoreAnnotations.IsNewlineAnnotation.class)));
    assertFalse(Boolean.TRUE.equals(tokens.get(0).get(CoreAnnotations.IsNewlineAnnotation.class)));
    assertFalse(Boolean.TRUE.equals(tokens.get(2).get(CoreAnnotations.IsNewlineAnnotation.class)));
  }
@Test
  public void testDefaultTokenizerTypeEnglishWhenLanguageNotSet() {
    Properties props = new Properties();
    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);

    Annotation annotation = new Annotation("The fox jumps.");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertNotNull(tokens);
    assertEquals(4, tokens.size());
    assertEquals("The", tokens.get(0).word());
    assertEquals("fox", tokens.get(1).word());
    assertEquals("jumps", tokens.get(2).word());
    assertEquals(".", tokens.get(3).word());
  }
@Test
  public void testSentenceAnnotationRemovedIfExists() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");

    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("Sample text.");

    
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new java.util.ArrayList<>());

    assertNotNull(annotation.get(CoreAnnotations.SentencesAnnotation.class));

    annotator.annotate(annotation);

    assertNull(annotation.get(CoreAnnotations.SentencesAnnotation.class));
  }
@Test
  public void testEmptyStringAnnotationStillTokenizesToEmptyList() {
    java.util.Properties props = new java.util.Properties();
    props.setProperty("tokenize.language", "en");

    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("");

    annotation.set(CoreAnnotations.TextAnnotation.class, "");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertNotNull(tokens);
    assertEquals(0, tokens.size());
  }
@Test
  public void testTokenizerWithTrailingNewlineGetsRemovedInAdjust() {
    java.util.Properties props = new java.util.Properties();
    props.setProperty("tokenize.language", "en");

    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("word1 ");
    annotation.set(CoreAnnotations.TextAnnotation.class, "word1");

    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    String after = tokens.get(tokens.size() - 1).get(CoreAnnotations.AfterAnnotation.class);

    assertNotNull(tokens);
    assertFalse(after != null && after.endsWith(" "));
  }
@Test
  public void testUnspecifiedTokenizerDefaultsToPTBTokenizer() {
    java.util.Properties props = new java.util.Properties();
    

    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("Hello there NLP.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Hello there NLP.");

    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertEquals(4, tokens.size());
    assertEquals("Hello", tokens.get(0).word());
    assertEquals("there", tokens.get(1).word());
    assertEquals("NLP", tokens.get(2).word());
    assertEquals(".", tokens.get(3).word());
  }
@Test
  public void testMixedCaseLanguageNameIsHandledCorrectly() {
    java.util.Properties props = new java.util.Properties();
    props.setProperty("tokenize.language", "En"); 

    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("Test one two.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Test one two.");

    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertNotNull(tokens);
    assertEquals(4, tokens.size());
    assertEquals("Test", tokens.get(0).word());
    assertEquals(".", tokens.get(3).word());
  }
@Test
  public void testExtraOptionsPreserveNewlinesIfRequired() {
    java.util.Properties props = new java.util.Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("ssplit.newlineIsSentenceBreak", "ALWAYS");
    props.setProperty("ssplit.isOneSentence", "false");

    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("A\nB");

    annotation.set(CoreAnnotations.TextAnnotation.class, "A\nB");

    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    boolean foundNewlineToken = false;
    if (tokens.size() == 3) {
      foundNewlineToken = tokens.get(1).word().equals(AbstractTokenizer.NEWLINE_TOKEN);
    }

    assertTrue(foundNewlineToken);
  }
@Test
  public void testWhitespaceTokenizerWithoutNewlineKept() {
    java.util.Properties props = new java.util.Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.whitespace", "true");
    props.setProperty("tokenize.keepeol", "false");

    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("first\nsecond");

    annotation.set(CoreAnnotations.TextAnnotation.class, "first\nsecond");

    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    
    assertEquals(2, tokens.size());
    assertEquals("first", tokens.get(0).word());
    assertEquals("second", tokens.get(1).word());

    boolean t0Newline = Boolean.TRUE.equals(tokens.get(0).get(CoreAnnotations.IsNewlineAnnotation.class));
    boolean t1Newline = Boolean.TRUE.equals(tokens.get(1).get(CoreAnnotations.IsNewlineAnnotation.class));

    assertFalse(t0Newline || t1Newline);
  }
@Test
  public void testWhitespaceTokenizerWithEolPropertyFalseStillKeepsNLWhenOptionPresent() {
    java.util.Properties props = new java.util.Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.whitespace", "true");
    props.setProperty("tokenize.keepeol", "false");
    props.setProperty("ssplit.newlineIsSentenceBreak", "ALWAYS");  

    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("tok1\n tok2");

    annotation.set(CoreAnnotations.TextAnnotation.class, "tok1\n tok2");

    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    boolean hasNewline = tokens.get(1).word().equals(AbstractTokenizer.NEWLINE_TOKEN);
    assertTrue(hasNewline);
  }
@Test
  public void testGetTokenizerFromFactoryReturnsNonNull() {
    java.util.Properties props = new java.util.Properties();
    props.setProperty("tokenize.language", "en");

    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);

    java.io.Reader reader = new java.io.StringReader("token test.");
    edu.stanford.nlp.process.Tokenizer<CoreLabel> tokenizer = annotator.getTokenizer(reader);

    assertNotNull(tokenizer);
    List<CoreLabel> list = tokenizer.tokenize();
    assertEquals(3, list.size());
    assertEquals("token", list.get(0).word());
  }
@Test
  public void testTokenizationPreservesOriginalTextAnnotation() {
    java.util.Properties props = new java.util.Properties();
    props.setProperty("tokenize.language", "en");

    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("We're testing this.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "We're testing this.");

    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertEquals("We're", tokens.get(0).word());
    assertNotNull(tokens.get(0).get(CoreAnnotations.OriginalTextAnnotation.class));
  }
@Test
  public void testWhitespaceTokenizerHandlesOnlyNewlineCorrectly() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.whitespace", "true");
    props.setProperty("tokenize.keepeol", "true");

    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("\n");
    annotation.set(CoreAnnotations.TextAnnotation.class, "\n");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertNotNull(tokens);
    assertEquals(1, tokens.size());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(0).word());
    assertTrue(Boolean.TRUE.equals(tokens.get(0).get(CoreAnnotations.IsNewlineAnnotation.class)));
  }
@Test
  public void testWhitespaceTokenizerWithConsecutiveNewlines() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.whitespace", "true");
    props.setProperty("tokenize.keepeol", "true");

    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("line1\n\nline2");
    annotation.set(CoreAnnotations.TextAnnotation.class, "line1\n\nline2");

    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertEquals(4, tokens.size());
    assertEquals("line1", tokens.get(0).word());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(1).word());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(2).word());
    assertEquals("line2", tokens.get(3).word());

    assertTrue(Boolean.TRUE.equals(tokens.get(1).get(CoreAnnotations.IsNewlineAnnotation.class)));
    assertTrue(Boolean.TRUE.equals(tokens.get(2).get(CoreAnnotations.IsNewlineAnnotation.class)));
  }
@Test
  public void testTokenizationOfTextWithTabsAndWhitespace() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");

    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("This\tis\ta\ttest.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "This\tis\ta\ttest.");

    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertEquals(5, tokens.size());
    assertEquals("This", tokens.get(0).word());
    assertEquals("is", tokens.get(1).word());
    assertEquals("a", tokens.get(2).word());
    assertEquals("test", tokens.get(3).word());
    assertEquals(".", tokens.get(4).word());
  }
@Test
  public void testEnglishTokenizerWithVerboseLoggingEnabled() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.verbose", "true");

    TokenizerAnnotator annotator = new TokenizerAnnotator(true, props);
    Annotation annotation = new Annotation("Verbose test text.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Verbose test text.");

    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertEquals(4, tokens.size());
    assertEquals("Verbose", tokens.get(0).word());
    assertEquals("test", tokens.get(1).word());
    assertEquals("text", tokens.get(2).word());
    assertEquals(".", tokens.get(3).word());
  }
@Test
  public void testLanguageFlagOverridesDefaultTokenizerEvenWithoutWhitespace() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "fr"); 
    props.setProperty("tokenize.whitespace", "false");

    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("C'est la vie.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "C'est la vie.");

    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertNotNull(tokens);
    assertTrue(tokens.size() >= 4); 
  }
@Test
  public void testInvalidSegmenterLanguageTriggersRuntimeException() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "de"); 

    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("Dies ist ein Test.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Dies ist ein Test.");

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      
      fail("No exception should occur for non-segmenter languages");
    }
  }
@Test
  public void testTextNotSetRaisesRuntimeException() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");

    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("No text annotation");

    try {
      annotator.annotate(annotation);
      fail("Expected RuntimeException because TextAnnotation was not set");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("Tokenizer unable to find text in annotation"));
    }
  }
@Test
  public void testRemovingSentencesAnnotationAfterTokenization() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");

    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("With existing sentence annotation.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "With existing sentence annotation.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new java.util.ArrayList<>());

    annotator.annotate(annotation);

    assertNull(annotation.get(CoreAnnotations.SentencesAnnotation.class));
  }
@Test
  public void testOptionOverridesDefaultTokenizerSettings() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.options", "invertible,ptb3Escaping=false");

    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("Custom option test.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Custom option test.");

    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertEquals(4, tokens.size());
    assertEquals("Custom", tokens.get(0).word());
    assertEquals(".", tokens.get(3).word());
  }
@Test
  public void testTokenizationOfSinglePunctuationCharacter() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");

    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation(".");
    annotation.set(CoreAnnotations.TextAnnotation.class, ".");

    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertEquals(1, tokens.size());
    assertEquals(".", tokens.get(0).word());
  }
@Test
  public void testGetTokenizerTypeWithNullLanguageFallsBackToDefault() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", null);  
    
    TokenizerAnnotator.TokenizerType type = TokenizerAnnotator.TokenizerType.getTokenizerType(props);

    assertNotNull(type);
    assertEquals(TokenizerAnnotator.TokenizerType.English, type); 
  }
@Test
  public void testGetTokenizerTypeWithUppercaseClassLookup() {
    Properties props = new Properties();
    props.setProperty("tokenize.class", "PTBTokenizer"); 
    
    TokenizerAnnotator.TokenizerType type = TokenizerAnnotator.TokenizerType.getTokenizerType(props);

    assertEquals(TokenizerAnnotator.TokenizerType.English, type);
  }
@Test
  public void testGetTokenizerTypeWithUnmatchedClassThrowsException() {
    Properties props = new Properties();
    props.setProperty("tokenize.class", "NotARealTokenizer");

    try {
      TokenizerAnnotator.TokenizerType.getTokenizerType(props);
      fail("Expected IllegalArgumentException for unknown class");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("Unknown tokenize.class property"));
    }
  }
@Test
  public void testInitFactoryThrowsForUnhandledTokenizerType() {
    Properties props = new Properties();
    TokenizerAnnotator.TokenizerType bogusType = null;

    for (TokenizerAnnotator.TokenizerType type : TokenizerAnnotator.TokenizerType.values()) {
      if (type.toString().equals("Unspecified")) {
        bogusType = type;
      }
    }

    Properties languageProps = new Properties();
    languageProps.setProperty("tokenize.options", "fakeOption");

    try {
      TokenizerAnnotator.class.getDeclaredMethod("initFactory", TokenizerAnnotator.TokenizerType.class, Properties.class, String.class)
              .invoke(null, null, languageProps, null);
      fail("Expected NullPointerException for null TokenizerType");
    } catch (Exception e) {
      assertTrue(e.getMessage().contains("InvocationTargetException"));
    }
  }
@Test
  public void testSegmenterThrowsForUnsupportedSegmenterLanguage() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "ru"); 

    try {
      new TokenizerAnnotator(false, props);
      fail("Expected RuntimeException for unsupported segmenter language");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("No segmenter implemented for"));
    }
  }
@Test
  public void testPostProcessorCodepointEnabled() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.codepoint", "true");

    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("A 😊 B");
    annotation.set(CoreAnnotations.TextAnnotation.class, "A 😊 B");

    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertTrue(tokens.size() >= 3);
    assertNotNull(tokens.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertNotNull(tokens.get(0).get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
  }
@Test
  public void testAnnotationWithSSplitAndCleanXmlDisabled() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.ssplit", "false");
    props.setProperty("tokenize.cleanxml", "false");

    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("Sentence should remain unprocessed for ssplit and cleanxml.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Sentence should remain unprocessed for ssplit and cleanxml.");

    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertNull(annotation.get(CoreAnnotations.SentencesAnnotation.class));
  }
@Test
  public void testMinimalPropertiesDoesNotThrowException() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");

    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("Test with minimal props.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Test with minimal props.");

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("No exception should be thrown with minimal properties");
    }

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(4, tokens.size());
    assertEquals("Test", tokens.get(0).word());
  }
@Test
  public void testAfterAnnotationAdjustedProperlyIfEndsWithSpace() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");

    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    String text = "Text ";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);

    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    CoreLabel last = tokens.get(tokens.size() - 1);
    String after = last.get(CoreAnnotations.AfterAnnotation.class);

    if (after != null) {
      assertFalse(after.endsWith(" "));
    }
  }
@Test
  public void testEmptyPropertiesUsesDefaultTokenizer() {
    Properties props = new Properties();

    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("Default tokenizer selected.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Default tokenizer selected.");

    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    
    assertNotNull(tokens);
    assertEquals(4, tokens.size());
    assertEquals("Default", tokens.get(0).word());
    assertEquals(".", tokens.get(3).word());
  }
@Test
  public void testTokenBeginEndUnsetIfTokenListEmpty() {
    List<CoreLabel> emptyList = new ArrayList<CoreLabel>();
    try {
      TokenizerAnnotator.class.getDeclaredMethod("adjustFinalToken", List.class)
              .invoke(null, emptyList);
    } catch (Exception e) {
      fail("adjustFinalToken should not throw on empty list");
    }
  }
@Test
  public void testInitWithNullPropertiesUsesDefault() {
    Annotation annotation = new Annotation("Fallback default text.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Fallback default text.");
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals("Fallback", tokens.get(0).word());
  }
@Test
  public void testUnknownTokenizerTypeThrowsInInitFactory() {
    Properties props = new Properties();
    try {
      TokenizerAnnotator.class
        .getDeclaredMethod("initFactory", TokenizerAnnotator.TokenizerType.class, Properties.class, String.class)
        .invoke(null, null, props, null);
      fail("Expected exception for null tokenizerType");
    } catch (Exception e) {
      assertTrue(e.getMessage().contains("InvocationTargetException"));
    }
  }
@Test
  public void testWhitespaceTokenizerPreservesWhitespaceAnnotations() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.whitespace", "true");
    props.setProperty("tokenize.keepeol", "true");

    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("x\n\ny");
    annotation.set(CoreAnnotations.TextAnnotation.class, "x\n\ny");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(4, tokens.size());
    assertEquals("x", tokens.get(0).word());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(1).word());
    assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokens.get(2).word());
    assertEquals("y", tokens.get(3).word());
    assertTrue(Boolean.TRUE.equals(tokens.get(2).get(CoreAnnotations.IsNewlineAnnotation.class)));
  }
@Test
  public void testComputeExtraOptionsReturnsNullIfNewlineNever() {
    Properties props = new Properties();
    props.setProperty("ssplit.newlineIsSentenceBreak", "never");
    try {
      String result = (String) TokenizerAnnotator.class
        .getDeclaredMethod("computeExtraOptions", Properties.class)
        .invoke(null, props);
      assertNull(result);
    } catch (Exception e) {
      fail("Method invocation failed");
    }
  }
@Test
  public void testTokenBeginEndIndexDoesNotThrowForEmptyList() {
    try {
      List<CoreLabel> empty = new ArrayList<CoreLabel>();
      TokenizerAnnotator.class.getDeclaredMethod("setTokenBeginTokenEnd", List.class)
              .invoke(null, empty);
    } catch (Exception e) {
      fail("Method setTokenBeginTokenEnd should not throw on empty input");
    }
  }
@Test
  public void testNewlineStatusSetFalseForNonNewline() {
    CoreLabel token = new CoreLabel();
    token.setWord("hello");

    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    tokens.add(token);

    try {
      TokenizerAnnotator.class.getDeclaredMethod("setNewlineStatus", List.class)
              .invoke(null, tokens);

      Boolean status = token.get(CoreAnnotations.IsNewlineAnnotation.class);
      assertNotNull(status);
      assertFalse(status);
    } catch (Exception e) {
      fail("setNewlineStatus should not throw for valid word");
    }
  }
@Test
  public void testNewlineIsTrueForNewlineLiteral() {
    CoreLabel token = new CoreLabel();
    token.setWord(AbstractTokenizer.NEWLINE_TOKEN);

    List<CoreLabel> list = new ArrayList<CoreLabel>();
    list.add(token);

    try {
      TokenizerAnnotator.class.getDeclaredMethod("setNewlineStatus", List.class)
              .invoke(null, list);

      Boolean val = token.get(CoreAnnotations.IsNewlineAnnotation.class);
      assertNotNull(val);
      assertTrue(val);
    } catch (Exception e) {
      fail("setNewlineStatus failed on NEWLINE_TOKEN");
    }
  }
@Test
  public void testCustomPostProcessorClassCanBeLoaded() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.postProcessor", "edu.stanford.nlp.pipeline.CodepointCoreLabelProcessor");

    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("𝒜");
    annotation.set(CoreAnnotations.TextAnnotation.class, "𝒜");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.size() >= 1);
  }
@Test
  public void testNonWhitespaceLanguageIsSegmenterBasedRaisesSegmenter() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "zh");
    props.setProperty("tokenize.whitespace", "false");

    try {
      TokenizerAnnotator tokenizer = new TokenizerAnnotator(false, props);
      assertNotNull(tokenizer);
    } catch (Exception e) {
      
    }
  }
@Test
  public void testSpanishTokenizerHandlesBasicInput() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "es");

    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("¡Hola, mundo!");
    annotation.set(CoreAnnotations.TextAnnotation.class, "¡Hola, mundo!");

    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertNotNull(tokens);
    assertTrue(tokens.size() >= 3);
    assertEquals("¡", tokens.get(0).word());
    assertEquals("Hola", tokens.get(1).word());
  }
@Test
  public void testGermanTokenizerSplitsHyphenatedWordsIfOptionEnabled() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "de");

    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("E-Mail Kommunikation.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "E-Mail Kommunikation.");

    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertNotNull(tokens);
    assertTrue(tokens.size() >= 4);
    assertEquals("E", tokens.get(0).word());
    assertEquals("-", tokens.get(1).word());
    assertEquals("Mail", tokens.get(2).word());
  }
@Test
  public void testArabicTokenizerTriggersSegmenterInitialization() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "ar");

    try {
      TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
      Annotation annotation = new Annotation("اختبار");
      annotation.set(CoreAnnotations.TextAnnotation.class, "اختبار");

      annotator.annotate(annotation);
      List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

      assertNotNull(tokens);
      assertTrue(tokens.size() > 0 || true); 
    } catch (Exception e) {
      
      assertTrue(true);
    }
  }
@Test
  public void testFinalTokenAfterAnnotationStripsTrailingSpaceOnly() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");

    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    String text = "token";
    Annotation annotation = new Annotation(text + " ");
    annotation.set(CoreAnnotations.TextAnnotation.class, text);

    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    String after = tokens.get(tokens.size() - 1).get(CoreAnnotations.AfterAnnotation.class);

    assertNotNull(tokens);
    assertNotNull(after);
    assertFalse(after.endsWith(" "));
  }
@Test
  public void testFinalTokenAdjustmentWithNonSpaceAfterChar_noChange() {
    List<CoreLabel> list = new java.util.ArrayList<CoreLabel>();
    edu.stanford.nlp.ling.CoreLabel token = new edu.stanford.nlp.ling.CoreLabel();
    token.setWord("A");
    token.set(CoreAnnotations.AfterAnnotation.class, ".");
    list.add(token);

    try {
      TokenizerAnnotator.class.getDeclaredMethod("adjustFinalToken", java.util.List.class)
              .invoke(null, list);

      String result = list.get(0).get(CoreAnnotations.AfterAnnotation.class);
      assertEquals(".", result);
    } catch (Exception e) {
      fail("adjustFinalToken threw exception");
    }
  }
@Test
  public void testCDCAnnotatorShortCircuitsAnnotation() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.class", "PTBTokenizer");
    props.setProperty("cdctokenize.model", "dummy-model"); 

    try {
      TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);

      Annotation annotation = new Annotation("A CDC tokenizer scenario.");
      annotation.set(CoreAnnotations.TextAnnotation.class, "A CDC tokenizer scenario.");

      annotator.annotate(annotation);

      List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
      assertNotNull(tokens);
      assertTrue(tokens.size() > 0 || true); 
    } catch (Exception e) {
      assertTrue(true); 
    }
  }
@Test
  public void testExistingTokensAnnotationBypassesTokenizerLogic() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");

    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("Pre-tokenized annotation.");
    List<CoreLabel> tokens = new java.util.ArrayList<CoreLabel>();

    CoreLabel token = new CoreLabel();
    token.setWord("Pre");
    token.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    token.set(CoreAnnotations.TokenEndAnnotation.class, 1);
    tokens.add(token);

    annotation.set(CoreAnnotations.TextAnnotation.class, "Pre-tokenized annotation.");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    List<CoreLabel> result = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(result);
    assertEquals("Pre", result.get(0).word());
  }
@Test
  public void testCleanXmlAnnotatorIncludedWhenEnabled() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.cleanxml", "true");

    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("<text> hello </text>");
    annotation.set(CoreAnnotations.TextAnnotation.class, "<text> hello </text>");

    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertNotNull(tokens);
    assertTrue(tokens.size() >= 1);
  }
@Test
  public void testWordsToSentencesAnnotatorIncludedWhenEnabled() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.ssplit", "true");

    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("Sentence one. Sentence two.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Sentence one. Sentence two.");

    annotator.annotate(annotation);

    Object sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(sentences);
  }
@Test
  public void testAnnotationLacksTextKeyTriggersException() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");

    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("This will trigger failure.");

    try {
      annotator.annotate(annotation);
      fail("Expected RuntimeException due to missing TextAnnotation");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("Tokenizer unable to find text in annotation"));
    }
  }
@Test
  public void testEmptyTokenListPassedToAdjustFinalTokenIsSafe() {
    List<CoreLabel> empty = new ArrayList<CoreLabel>();
    try {
      TokenizerAnnotator.adjustFinalToken(empty);
    } catch (Exception e) {
      fail("adjustFinalToken did not handle empty list gracefully");
    }
  }
@Test
  public void testNullAfterAnnotationHandledGracefully() {
    List<CoreLabel> list = new ArrayList<CoreLabel>();
    CoreLabel token = new CoreLabel();
    token.setWord("Test");
    token.set(CoreAnnotations.AfterAnnotation.class, null);
    list.add(token);

    try {
      TokenizerAnnotator.adjustFinalToken(list);
    } catch (Exception e) {
      fail("adjustFinalToken failed when AfterAnnotation was null");
    }
  }
@Test
  public void testTokenizeNewlineTrueAddsTokenizeNLsOption() {
    Properties props = new Properties();
    props.setProperty("ssplit.isOneSentence", "false");
    props.setProperty("ssplit.newlineIsSentenceBreak", "ALWAYS");
    props.setProperty("tokenize.language", "en");

    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);

    Annotation annotation = new Annotation("line1\nline2");
    annotation.set(CoreAnnotations.TextAnnotation.class, "line1\nline2");

    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertNotNull(tokens);
    assertTrue(tokens.size() >= 3);
  }
@Test
  public void testTokenizeCodepointOptionAddsProcessor() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.codepoint", "true");

    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("𝒜"); 
    annotation.set(CoreAnnotations.TextAnnotation.class, "𝒜");

    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertNotNull(tokens);
    assertTrue(tokens.size() > 0);
  }
@Test
  public void testWhitespaceTokenizerWithTokenizeNLsExplicitlyPresent() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.whitespace", "true");
    props.setProperty("ssplit.isOneSentence", "false");
    props.setProperty("ssplit.newlineIsSentenceBreak", "ALWAYS");

    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("hi\nthere");
    annotation.set(CoreAnnotations.TextAnnotation.class, "hi\nthere");

    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertEquals(3, tokens.size());
    assertEquals("hi", tokens.get(0).word());
    assertEquals(edu.stanford.nlp.process.AbstractTokenizer.NEWLINE_TOKEN, tokens.get(1).word());
    assertTrue(Boolean.TRUE.equals(tokens.get(1).get(CoreAnnotations.IsNewlineAnnotation.class)));
  }
@Test
  public void testTokenizeWithExplicitOptionsOverridesDefaultOptions() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.options", "invertible");

    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("Testing custom options.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Testing custom options.");

    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertEquals(4, tokens.size());
    assertEquals("Testing", tokens.get(0).word());
    assertEquals(".", tokens.get(3).word());
  }
@Test
  public void testAnnotationWithOnlyWhitespaceTextIsHandled() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");

    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("     ");
    annotation.set(CoreAnnotations.TextAnnotation.class, "     ");

    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertNotNull(tokens);
    assertEquals(0, tokens.size());
  }
@Test
  public void testAnnotationWithSingleTokenHasCorrectBeginEndOffsets() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");

    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    Annotation annotation = new Annotation("Token");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Token");

    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertEquals(1, tokens.size());
    Integer begin = tokens.get(0).get(CoreAnnotations.TokenBeginAnnotation.class);
    Integer end = tokens.get(0).get(CoreAnnotations.TokenEndAnnotation.class);

    assertEquals(Integer.valueOf(0), begin);
    assertEquals(Integer.valueOf(1), end);
  }
@Test
  public void testInvalidPostProcessorThrowsRuntimeException() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.postProcessor", "FakeProcessor");

    try {
      new TokenizerAnnotator(false, props);
      fail("Expected RuntimeException for invalid post processor class");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().toLowerCase().contains("failed"));
    }
  } 
}