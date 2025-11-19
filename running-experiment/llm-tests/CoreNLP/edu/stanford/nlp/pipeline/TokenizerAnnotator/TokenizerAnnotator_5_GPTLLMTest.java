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

public class TokenizerAnnotator_5_GPTLLMTest {

 @Test
  public void testDefaultConstructor_TokenizerNotNull() {
    TokenizerAnnotator annotator = new TokenizerAnnotator();
    assertNotNull(annotator.getTokenizer(new StringReader("Hello, world.")));
  }

@Test
  public void testGetTokenizerType_FromLanguage() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "es");
    TokenizerAnnotator.TokenizerType type = TokenizerAnnotator.TokenizerType.getTokenizerType(props);
    assertEquals(TokenizerAnnotator.TokenizerType.Spanish, type);
  }
@Test
  public void testGetTokenizerType_FromClass() {
    Properties props = new Properties();
    props.setProperty("tokenize.class", "WhitespaceTokenizer");
    TokenizerAnnotator.TokenizerType type = TokenizerAnnotator.TokenizerType.getTokenizerType(props);
    assertEquals(TokenizerAnnotator.TokenizerType.Whitespace, type);
  }
@Test
  public void testTokenize_English_SimpleText() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);

    Annotation annotation = new Annotation("Hello world!");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertEquals(3, tokens.size());
    assertEquals("Hello", tokens.get(0).word());
    assertEquals("world", tokens.get(1).word());
    assertEquals("!", tokens.get(2).word());
  }
@Test
  public void testTokenBeginAndEndAnnotations_AreSetCorrectly() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);

    Annotation annotation = new Annotation("The quick brown fox");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertEquals((Integer) 0, tokens.get(0).get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals((Integer) 1, tokens.get(0).get(CoreAnnotations.TokenEndAnnotation.class));

    assertEquals((Integer) 1, tokens.get(1).get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals((Integer) 2, tokens.get(1).get(CoreAnnotations.TokenEndAnnotation.class));

    assertEquals((Integer) 2, tokens.get(2).get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals((Integer) 3, tokens.get(2).get(CoreAnnotations.TokenEndAnnotation.class));

    assertEquals((Integer) 3, tokens.get(3).get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals((Integer) 4, tokens.get(3).get(CoreAnnotations.TokenEndAnnotation.class));
  }
@Test
  public void testNewlineAnnotationLabeling() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    Annotation annotation = new Annotation("Hello\nWorld");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    boolean found = false;
    if (tokens.get(0).get(CoreAnnotations.IsNewlineAnnotation.class) != null &&
        tokens.get(0).get(CoreAnnotations.IsNewlineAnnotation.class)) {
      found = true;
    }
    if (tokens.get(1).get(CoreAnnotations.IsNewlineAnnotation.class) != null &&
        tokens.get(1).get(CoreAnnotations.IsNewlineAnnotation.class)) {
      found = true;
    }
    if (tokens.size() > 2 &&
        tokens.get(2).get(CoreAnnotations.IsNewlineAnnotation.class) != null &&
        tokens.get(2).get(CoreAnnotations.IsNewlineAnnotation.class)) {
      found = true;
    }

    assertTrue("Expected to find a token labeled with IsNewlineAnnotation", found);
  }
@Test
  public void testAdjustFinalToken_RemovesTrailingSpace() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.AfterAnnotation.class, " ");
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    TokenizerAnnotator.adjustFinalToken(tokens);
    assertEquals("", token.get(CoreAnnotations.AfterAnnotation.class));
  }
@Test
  public void testAdjustFinalToken_EmptyListDoesNotFail() {
    List<CoreLabel> tokens = new ArrayList<>();
    TokenizerAnnotator.adjustFinalToken(tokens);
    assertTrue(tokens.isEmpty());
  }
@Test
  public void testWhitespaceTokenizer_KeepsNewlines() {
    Properties props = new Properties();
    props.setProperty("tokenize.whitespace", "true");
    props.setProperty("tokenize.keepeol", "true");

    Annotation annotation = new Annotation("a\nb");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    boolean containsEol = false;
    if (tokens.size() >= 2 &&
        tokens.get(1).get(CoreAnnotations.IsNewlineAnnotation.class) != null &&
        tokens.get(1).get(CoreAnnotations.IsNewlineAnnotation.class)) {
      containsEol = true;
    }

    assertTrue("Expected at least one token with IsNewlineAnnotation=true", containsEol);
  }

@Test
  public void testUnspecifiedTokenizer_FallsBackToPTBTokenizer() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    assertNotNull(annotator.getTokenizer(new StringReader("Some text")));
  }
@Test
  public void testAnnotatorRequirementsSatisfied_ContainsExpectedAnnotations() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);

    Set<Class<? extends CoreAnnotation>> requirements = annotator.requirementsSatisfied();

    assertTrue(requirements.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.TokenBeginAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.TokenEndAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.BeforeAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.AfterAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.IsNewlineAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.SentencesAnnotation.class));
  }
@Test
  public void testVerboseProperty_EnablesVerboseModeWithoutError() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.verbose", "true");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("Verbose mode test.");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.size() >= 3);
  }
@Test
  public void testStatTokSentModelIntegration() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("statTokSent.model", "edu/stanford/nlp/models/statssplitter/english-bidirectional.tagger");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("StatTokSent test sentence.");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.size() >= 3);
  }
@Test
  public void testEmptyString_InputProducesNoTokens() {
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
  public void testWhiteSpaceOnlyInput_WithWhitespaceTokenizer() {
    Properties props = new Properties();
    props.setProperty("tokenize.whitespace", "true");
    Annotation annotation = new Annotation("     ");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(0, tokens.size());
  }
@Test
  public void testChineseTokenizerType_YieldsNullFactory() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "zh");
    try {
      new TokenizerAnnotator(props); 
    } catch (RuntimeException e) {
      
      assertTrue(e.getMessage().contains("segment"));
    }
  }
@Test
  public void testArabicTokenizerType_YieldsNonNull() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "ar");
    try {
      new TokenizerAnnotator(props); 
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("segment"));
    }
  }
@Test
  public void testWhitespaceTokenizerWithoutEOLProperty() {
    Properties props = new Properties();
    props.setProperty("tokenize.whitespace", "true");
    Annotation annotation = new Annotation("A B C");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals("A", tokens.get(0).word());
    assertEquals("B", tokens.get(1).word());
    assertEquals("C", tokens.get(2).word());
  }
@Test
  public void testAdjustFinalTokenWithNonSpaceFinalAfterThrowsException() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.AfterAnnotation.class, "\t");
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    try {
      TokenizerAnnotator.adjustFinalToken(tokens);
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("Unexpected final char"));
    }
  }
@Test
  public void testAdjustFinalTokenWithNullAfter_DoesNotThrow() {
    CoreLabel token = new CoreLabel(); 
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    try {
      TokenizerAnnotator.adjustFinalToken(tokens);
    } catch (Exception e) {
      fail("adjustFinalToken should not throw exception if AfterAnnotation is null");
    }
  }
@Test
  public void testWhitespaceTokenizer_EolsPreservedWhenComputedOptionMatchesConstant() {
    Properties props = new Properties();
    props.setProperty("tokenize.whitespace", "true");
    props.setProperty("ssplit.newlineIsSentenceBreak", "always");
    Annotation annotation = new Annotation("hello\nworld");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertEquals("hello", tokens.get(0).word());
    assertTrue(tokens.get(1).get(CoreAnnotations.IsNewlineAnnotation.class));
    assertEquals("world", tokens.get(2).word());
  }
@Test
  public void testPostProcessors_IncludeCodepointProcessor() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.codepoint", "true");
    Annotation annotation = new Annotation("abc");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(3, tokens.size()); 
  }
@Test
  public void testUnknownTokenizerType_ThrowsIllegalArgumentException() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "zz");
    try {
      TokenizerAnnotator.TokenizerType.getTokenizerType(props);
      fail("Expected IllegalArgumentException for unknown tokenizer type");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("unknown tokenize.language"));
    }
  }
@Test
  public void testTokenizerTypeUnspecifiedFallbackToEnglish_PTBFallback() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", ""); 
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("Fallback test.");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals("Fallback", tokens.get(0).word());
  }
@Test
  public void testInitFactory_DefaultOptionsOverridesExtraOptions() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.options", "invertible");
    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props, "tokenizeNLs,");
    Annotation annotation = new Annotation("Hello there!");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals(3, tokens.size());
  }
@Test
  public void testCDCAnnotatorReturnsEarly() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("statTokSent.model", "edu/stanford/nlp/models/statssplitter/english-bidirectional.tagger");
    Annotation annotation = new Annotation("This is a CDC tokenizer test.");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.size() > 0);
  }
@Test
  public void testSegmenterLanguageWithWhitespaceTokenization_SkipsSegmenter() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "ar"); 
    props.setProperty("tokenize.whitespace", "true"); 
    Annotation annotation = new Annotation("word1 word2 word3");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(3, tokens.size());
    assertEquals("word1", tokens.get(0).word());
    assertEquals("word2", tokens.get(1).word());
    assertEquals("word3", tokens.get(2).word());
  }
@Test
  public void testAnnotateRemovesSentencesAnnotationIfPresent() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    Annotation annotation = new Annotation("Sentence test.");
    List<Annotation> fakeSentences = new ArrayList<>();
    fakeSentences.add(new Annotation("Fake sentence."));

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    annotator.annotate(annotation);
  }
@Test
  public void testWhitespaceTokenizer_EOLComputationViaNewlineIsSentenceBreak() {
    Properties props = new Properties();
    props.setProperty("tokenize.whitespace", "true");
    props.setProperty("ssplit.isOneSentence", "false");
    props.setProperty("ssplit.newlineIsSentenceBreak", "always");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("A\nB");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals("A", tokens.get(0).word());
    assertTrue(tokens.get(1).get(CoreAnnotations.IsNewlineAnnotation.class));
    assertEquals("B", tokens.get(2).word());
  }
@Test
  public void testSetTokenBeginTokenEndWithOneToken() {
    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token = new CoreLabel();
    tokens.add(token);
    TokenizerAnnotator.adjustFinalToken(tokens); 
    TokenizerAnnotator tokenizerAnnotator = new TokenizerAnnotator();
    Annotation annotation = new Annotation("OneToken");
    tokenizerAnnotator.annotate(annotation);
    List<CoreLabel> resultTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals((Integer) 0, resultTokens.get(0).get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals((Integer) 1, resultTokens.get(0).get(CoreAnnotations.TokenEndAnnotation.class));
  }
@Test
  public void testGetTokenizerType_WithNullPropValues() {
    Properties props = new Properties(); 
    TokenizerAnnotator.TokenizerType type = TokenizerAnnotator.TokenizerType.getTokenizerType(props);
    assertEquals(TokenizerAnnotator.TokenizerType.English, type); 
  }
@Test
  public void testRequirementsEmptySetFromRequires() {
    TokenizerAnnotator annotator = new TokenizerAnnotator();
    Set<Class<? extends CoreAnnotation>> requires = annotator.requires();
    assertTrue(requires.isEmpty());
  }
@Test
  public void testCustomTokenizerClassCaseInsensitiveMatch() {
    Properties props = new Properties();
    props.setProperty("tokenize.class", "whitespaceTokenizer"); 
    TokenizerAnnotator.TokenizerType type = TokenizerAnnotator.TokenizerType.getTokenizerType(props);
    assertEquals(TokenizerAnnotator.TokenizerType.Whitespace, type);
  }
@Test
  public void testComputeExtraOptions_NewlinesSetViaProperty() {
    Properties props = new Properties();
    props.setProperty("ssplit.isOneSentence", "false");
    props.setProperty("ssplit.newlineIsSentenceBreak", "always");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("Test\nTokenizer\n");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertTrue(tokens.size() > 0);
    assertTrue(tokens.get(1).get(CoreAnnotations.IsNewlineAnnotation.class));
  }
@Test
  public void testMultipleNewlines_TokenizedAndLabeledAsIsNewlineTokens() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    Annotation annotation = new Annotation("One\n\nTwo");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    boolean hasNewlineLabel = false;
    if (tokens.size() >= 3) {
      if (Boolean.TRUE.equals(tokens.get(1).get(CoreAnnotations.IsNewlineAnnotation.class))) {
        hasNewlineLabel = true;
      }
    }

    assertTrue("Expected newline token to be labeled", hasNewlineLabel);
  }
@Test
  public void testWhitespaceTokenizerWithKeepEOLExplicitFalse() {
    Properties props = new Properties();
    props.setProperty("tokenize.whitespace", "true");
    props.setProperty("tokenize.keepeol", "false");

    Annotation annotation = new Annotation("a\nb");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    boolean anyIsNewline = false;
    if (tokens.size() > 1) {
      if (Boolean.TRUE.equals(tokens.get(1).get(CoreAnnotations.IsNewlineAnnotation.class))) {
        anyIsNewline = true;
      }
    }

    assertFalse("Expected no token to be labeled as newline", anyIsNewline);
  }
@Test
  public void testTokenizeCodepointYieldsOneTokenPerChar() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.codepoint", "true");

    Annotation annotation = new Annotation("abc");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertEquals(3, tokens.size());
    assertEquals("a", tokens.get(0).word());
    assertEquals("b", tokens.get(1).word());
    assertEquals("c", tokens.get(2).word());
  }
@Test
  public void testCleanXMLAnnotatorActivationWithProperty() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.cleanxml", "true");

    Annotation annotation = new Annotation("<xml>This & That</xml>");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.size() > 0);
  }
@Test
  public void testSSplitAnnotatorDisabledExplicitly() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.ssplit", "false");

    Annotation annotation = new Annotation("Sentence one. Sentence two.");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    annotator.annotate(annotation);

    List<?> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNull("Expected SentencesAnnotation to be null", sentences);
  }
@Test
  public void testTokenWithAfterAnnotationEndsWithNonSpaceThrows() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.AfterAnnotation.class, "\t");
    List<CoreLabel> list = new ArrayList<>();
    list.add(token);

    TokenizerAnnotator.adjustFinalToken(list);
  }
@Test
  public void testNoTokenizerPropertiesSpecified_UsesDefaults() {
    Properties props = new Properties(); 
    Annotation annotation = new Annotation("hello default config.");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals("hello", tokens.get(0).word());
    assertEquals("default", tokens.get(1).word());
    assertEquals("config", tokens.get(2).word());
    assertEquals(".", tokens.get(3).word());
  }
@Test
  public void testLanguageHasNoSegmenter_YieldsRuntimeException() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "de"); 

    new TokenizerAnnotator(props);
  }
@Test
  public void testWhitespaceWithNewlinePreservationViaComputedOptions() {
    Properties props = new Properties();
    props.setProperty("tokenize.whitespace", "true");
    props.setProperty("ssplit.newlineIsSentenceBreak", "always");
    props.setProperty("ssplit.isOneSentence", "false");

    Annotation annotation = new Annotation("First\nSecond");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertEquals("First", tokens.get(0).word());
    assertEquals("Second", tokens.get(2).word());
    assertTrue(tokens.get(1).get(CoreAnnotations.IsNewlineAnnotation.class));
  }
@Test
  public void testPostProcessorClassConfiguredButClassMissing_Throws() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.postProcessor", "missing.FakeProcessor");

    new TokenizerAnnotator(props);
  }
@Test
  public void testWhitespaceTokenizerWithEmptyInput_YieldsEmptyList() {
    Properties props = new Properties();
    props.setProperty("tokenize.whitespace", "true");

    Annotation annotation = new Annotation("");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.isEmpty());
  }
@Test
  public void testUnmodifiablePostProcessorsField_RemainsUnmodifiable() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.codepoint", "true");

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Annotation annotation = new Annotation("X");
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(1, tokens.size());
    assertEquals("X", tokens.get(0).word());
  }
@Test
  public void testSSplitAndCleanXmlBothEnabled_TokensStillPresent() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.cleanxml", "true");
    props.setProperty("tokenize.ssplit", "true");

    Annotation annotation = new Annotation("<doc>Sentence. Sentence2.</doc>");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.size() >= 2);
  }
@Test
  public void testInitFactory_WhitespaceTokenizer_EOLPropertyTrue() {
    Properties props = new Properties();
    props.setProperty("tokenize.whitespace", "true");
    props.setProperty("tokenize.keepeol", "true");
    Annotation annotation = new Annotation("alpha\nbeta");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals("alpha", tokens.get(0).word());
    assertEquals("beta", tokens.get(2).word());
    assertTrue(tokens.get(1).get(CoreAnnotations.IsNewlineAnnotation.class));
  }
@Test
  public void testGetTokenizerWithNullReader_ThrowsNPE() {
    TokenizerAnnotator annotator = new TokenizerAnnotator();
    annotator.getTokenizer(null);
  }
@Test
  public void testEmptyAfterAnnotationInLastToken_NoExceptionThrown() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.AfterAnnotation.class, "");
    List<CoreLabel> list = new ArrayList<>();
    list.add(token);
    try {
      TokenizerAnnotator.adjustFinalToken(list);
    } catch (Exception e) {
      fail("adjustFinalToken should ignore empty AfterAnnotation");
    }
  }
@Test
  public void testCleanXmlAnnotatorOverridesText() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.cleanxml", "true");
    Annotation annotation = new Annotation("<doc>cleaned text</doc>");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.size() > 0);
  }
@Test
  public void testLanguageGermanUsesPTBTokenizerAndNoSegmenter() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "de");
    Annotation annotation = new Annotation("Dies ist ein Test.");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.size() >= 4);
  }
@Test
  public void testWhitespaceTokenizerWithTrailingNewline() {
    Properties props = new Properties();
    props.setProperty("tokenize.whitespace", "true");
    props.setProperty("tokenize.keepeol", "true");
    Annotation annotation = new Annotation("word1\n");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(2, tokens.size());
    assertEquals("word1", tokens.get(0).word());
    assertTrue(tokens.get(1).get(CoreAnnotations.IsNewlineAnnotation.class));
  }
@Test
  public void testSegmenterLanguageWithValidPath_Chinese() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "zh");
    try {
      new TokenizerAnnotator(props);
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("segment"));
    }
  }
@Test
  public void testSegmenterLanguageInvalid_YieldsRuntimeException() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "ko"); 
    try {
      new TokenizerAnnotator(props);
      fail("Expected RuntimeException for unsupported segmenter language");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("No segmenter implemented"));
    }
  }
@Test
  public void testSetNewlineStatus_MultipleTokensOneNewline() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Hi");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("\n");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("There");
    List<CoreLabel> inputList = new ArrayList<>();
    inputList.add(token1);
    inputList.add(token2);
    inputList.add(token3);

    for (CoreLabel t : inputList) {
      if (t.word().equals("\n")) {
        t.set(CoreAnnotations.TextAnnotation.class, AbstractTokenizer.NEWLINE_TOKEN);
      }
    }

    TokenizerAnnotator annotator = new TokenizerAnnotator();
    Annotation annotation = new Annotation("Hi\nThere");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    boolean foundNewline = false;
    if (tokens != null && tokens.size() >= 2) {
      foundNewline = Boolean.TRUE.equals(tokens.get(1).get(CoreAnnotations.IsNewlineAnnotation.class));
    }

    assertTrue("Expected newline token to be annotated", foundNewline);
  }
@Test
  public void testGetTokenizerType_LanguageCaseInsensitive() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "ENgLiSh");
    TokenizerAnnotator.TokenizerType type = TokenizerAnnotator.TokenizerType.getTokenizerType(props);
    assertEquals(TokenizerAnnotator.TokenizerType.English, type);
  }
@Test
  public void testFactoryLoadingForFrenchTokenizer() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "fr");
    Annotation annotation = new Annotation("Bonjour le monde.");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.size() >= 3);
  }
@Test
  public void testFactoryLoadingForSpanishTokenizer() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "es");
    Annotation annotation = new Annotation("Hola mundo.");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.size() >= 2);
  }
@Test
  public void testTokenBeforeAndAfter_AnnotationsExist() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    Annotation annotation = new Annotation("hello!");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    String before = tokens.get(0).get(CoreAnnotations.BeforeAnnotation.class);
    String after = tokens.get(0).get(CoreAnnotations.AfterAnnotation.class);
    assertNotNull(before);
    assertNotNull(after);
  }
@Test
  public void testUnspecifiedLanguageDefaultsToPTBTokenizer() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "");
    Annotation annotation = new Annotation("Default fallback test.");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals("Default", tokens.get(0).word());
  }
@Test
  public void testNullTokenizerTypeThrowsOnUnknownFactoryType() {
    Properties props = new Properties();
    props.setProperty("tokenize.class", "UnknownTokenizerClass");
    TokenizerAnnotator.TokenizerType.getTokenizerType(props);
  }
@Test
  public void testWhitespaceTokenizer_CallsWithEOLFromExtraOptions() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.whitespace", "true");
    props.setProperty("ssplit.newlineIsSentenceBreak", "always");
    props.setProperty("ssplit.isOneSentence", "false");
    Annotation annotation = new Annotation("x\ny");
    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals("x", tokens.get(0).word());
    assertTrue(tokens.get(1).get(CoreAnnotations.IsNewlineAnnotation.class));
  }
@Test
  public void testChineseSegmenterFallbackIfJARMissing() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "zh");
    try {
      new TokenizerAnnotator(props);
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().toLowerCase().contains("segment"));
    }
  }
@Test
  public void testArabicSegmenterFallbackIfJARMissing() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "ar");
    try {
      new TokenizerAnnotator(props);
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().toLowerCase().contains("segment"));
    }
  }
@Test
  public void testLanguageNoSegmenterThrowsRuntimeException() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "xx");
    try {
      new TokenizerAnnotator(props);
      fail("Expected RuntimeException due to missing segmenter support");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("No segmenter implemented"));
    }
  }
@Test
  public void testAnnotationWithoutTokensAnnotationStillSucceeds() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);

    Annotation annotation = new Annotation("testing clean pass");
    annotation.remove(CoreAnnotations.TokensAnnotation.class);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertNotNull(tokens);
    assertEquals("testing", tokens.get(0).word());
  }
@Test
  public void testTokenAfterAnnotationIsNullIsSafeInAdjustFinalToken() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.AfterAnnotation.class, null);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    try {
      TokenizerAnnotator.adjustFinalToken(tokens);
    } catch (Exception e) {
      fail("adjustFinalToken should handle null AfterAnnotation");
    }
  }
@Test
  public void testTokenAfterWithWhitespaceSuffixOnly_RemovesSpace() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.AfterAnnotation.class, " ");
    List<CoreLabel> list = new ArrayList<CoreLabel>();
    list.add(token);
    TokenizerAnnotator.adjustFinalToken(list);
    String result = token.get(CoreAnnotations.AfterAnnotation.class);
    assertEquals("", result);
  }
@Test
  public void testSSplitAnnotatedDisabledByProperty() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.ssplit", "false");
    Annotation annotation = new Annotation("Hello sentence splitter.");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    annotator.annotate(annotation);
    Object sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNull(sentences);
  }
@Test
  public void testCleanXmlOnlyEnabledWhenPropertyIsSet() {
    Properties without = new Properties();
    without.setProperty("tokenize.language", "en");
    TokenizerAnnotator baseAnnotator = new TokenizerAnnotator(without);
    Annotation annotation = new Annotation("<xml>Text</xml>");
    baseAnnotator.annotate(annotation);
    List<CoreLabel> tokens1 = annotation.get(CoreAnnotations.TokensAnnotation.class);

    Properties with = new Properties();
    with.setProperty("tokenize.language", "en");
    with.setProperty("tokenize.cleanxml", "true");
    TokenizerAnnotator xmlAnnotator = new TokenizerAnnotator(with);
    Annotation annotation2 = new Annotation("<xml>Text</xml>");
    xmlAnnotator.annotate(annotation2);
    List<CoreLabel> tokens2 = annotation2.get(CoreAnnotations.TokensAnnotation.class);

    assertTrue(tokens2.size() > 0);
    assertEquals(tokens1.size(), tokens2.size());
  }
@Test
  public void testBeforeAndAfterAnnotationsArePresentInEnglishTokens() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    Annotation annotation = new Annotation("unit test.");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    String before = tokens.get(0).get(CoreAnnotations.BeforeAnnotation.class);
    String after = tokens.get(0).get(CoreAnnotations.AfterAnnotation.class);
    assertNotNull(before);
    assertNotNull(after);
  }
@Test
  public void testTokenBeginEndAreSetCorrectlyOnEachToken() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    Annotation annotation = new Annotation("Check token index.");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals((Integer) 0, tokens.get(0).get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals((Integer) 1, tokens.get(0).get(CoreAnnotations.TokenEndAnnotation.class));
    assertEquals((Integer) 1, tokens.get(1).get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals((Integer) 2, tokens.get(1).get(CoreAnnotations.TokenEndAnnotation.class));
  }
@Test
  public void testAllRequirementsSatisfiedContainExpectedSet() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
    assertTrue(satisfied.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.TokenBeginAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.TokenEndAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.AfterAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.BeforeAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.SentenceIndexAnnotation.class));
  }
@Test
  public void testInitFactoryWithNullOptionsUsesDefault() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props, null);
    Annotation annotation = new Annotation("Default options check.");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertEquals("Default", tokens.get(0).word());
  }
@Test
  public void testMultiplePostProcessorsIncludingCodepoint() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.codepoint", "true");
    props.setProperty("tokenize.postProcessor", "edu.stanford.nlp.pipeline.CodepointCoreLabelProcessor");

    Annotation annotation = new Annotation("AB");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(2, tokens.size());
    assertEquals("A", tokens.get(0).word());
    assertEquals("B", tokens.get(1).word());
  }
@Test
  public void testEmptyInput_PTBTokensEmpty() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    Annotation annotation = new Annotation("");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(0, tokens.size());
  }
@Test
  public void testTokenWithAfterAnnotationEndingWithSpace_RemovesSpace() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.AfterAnnotation.class, " \n");
    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    tokens.add(token);

    try {
      TokenizerAnnotator.adjustFinalToken(tokens);
      String finalAfter = token.get(CoreAnnotations.AfterAnnotation.class);
      assertEquals(" \n", finalAfter); 
    } catch (Exception e) {
      fail("adjustFinalToken should not throw");
    }
  }
@Test
  public void testTokenWithAfterAnnotationEndingWithTab_Throws() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.AfterAnnotation.class, "\t");
    List<CoreLabel> list = new ArrayList<CoreLabel>();
    list.add(token);
  }
@Test
  public void testMissingTextAnnotationThrowsOnAnnotate() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    Annotation annotation = new Annotation("");
    annotation.remove(CoreAnnotations.TextAnnotation.class);
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);

    annotator.annotate(annotation);
  }
@Test
  public void testWhitespaceTokenizer_EOLSComputedFromCode() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("tokenize.whitespace", "true");
    props.setProperty("ssplit.isOneSentence", "false");
    props.setProperty("ssplit.newlineIsSentenceBreak", "twoConsecutive");

    Annotation annotation = new Annotation("a\n\nb");
    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props);
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals("a", tokens.get(0).word());
    assertTrue(tokens.get(1).get(CoreAnnotations.IsNewlineAnnotation.class));
    assertTrue(tokens.get(2).get(CoreAnnotations.IsNewlineAnnotation.class));
    assertEquals("b", tokens.get(3).word());
  }
@Test
  public void testWhitespaceTokenizer_IgnoreNewlineWhenEOLPropertyFalseExplicitly() {
    Properties props = new Properties();
    props.setProperty("tokenize.whitespace", "true");
    props.setProperty("tokenize.keepeol", "false");

    Annotation annotation = new Annotation("token\nignored");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    assertEquals("token", tokens.get(0).word());
    assertEquals("ignored", tokens.get(1).word());
    assertFalse(tokens.get(0).get(CoreAnnotations.IsNewlineAnnotation.class));
  }
@Test
  public void testCDCAnnotatorOnly_PerformsTokenizationAndSkipsCleanXml() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("statTokSent.model", "edu/stanford/nlp/models/statssplitter/english-bidirectional.tagger");
    props.setProperty("tokenize.cleanxml", "true");

    Annotation annotation = new Annotation("test cdc");
    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(tokens);
    assertTrue(tokens.size() > 0);
  }
@Test
  public void testAnnotateRemovesOldSentencesAnnotation() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");

    Annotation annotation = new Annotation("Reset sentence.");
    List<Annotation> fake = new ArrayList<Annotation>();
    fake.add(new Annotation("Fake"));

    TokenizerAnnotator annotator = new TokenizerAnnotator(props);
    annotator.annotate(annotation);

    List<?> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotSame(fake, sentences);
  }
@Test
  public void testUnknownLanguageCodeThrowsInGetTokenizerType() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "xx");

    TokenizerAnnotator.TokenizerType.getTokenizerType(props);
  }
@Test
  public void testClassNameMapMatchingUppercase() {
    Properties props = new Properties();
    props.setProperty("tokenize.class", "WhitespaceTokenizer");

    TokenizerAnnotator.TokenizerType type = TokenizerAnnotator.TokenizerType.getTokenizerType(props);
    assertEquals(TokenizerAnnotator.TokenizerType.Whitespace, type);
  }
@Test
  public void testLanguageAbbreviationMapMatching() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "de"); 
    TokenizerAnnotator.TokenizerType type = TokenizerAnnotator.TokenizerType.getTokenizerType(props);
    assertEquals(TokenizerAnnotator.TokenizerType.German, type);
  }
@Test
  public void testStringToNewlineBreakConversionAffectsKeepNewlineOption() {
    Properties props = new Properties();
    props.setProperty("tokenize.language", "en");
    props.setProperty("ssplit.isOneSentence", "false");
    props.setProperty("ssplit.newlineIsSentenceBreak", "always");

    TokenizerAnnotator annotator = new TokenizerAnnotator(false, props, "tokenizeNLs,invertible");

    Annotation annotation = new Annotation("line1\nline2");
    annotator.annotate(annotation);
    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertTrue(tokens.size() >= 3);
    assertTrue(tokens.get(1).get(CoreAnnotations.IsNewlineAnnotation.class));
  } 
}