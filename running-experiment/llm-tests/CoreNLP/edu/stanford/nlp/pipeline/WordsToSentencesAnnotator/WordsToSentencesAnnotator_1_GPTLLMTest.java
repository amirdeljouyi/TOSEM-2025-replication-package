package edu.stanford.nlp.pipeline;

import java.util.*;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.util.CoreMap;

import edu.stanford.nlp.util.logging.Redwood;
import org.junit.Test;

import edu.stanford.nlp.util.PropertiesUtils;

import static org.junit.Assert.*;

public class WordsToSentencesAnnotator_1_GPTLLMTest {

 @Test
    public void testAnnotateBasicSentenceSplitting() {
        Properties props = new Properties();
        WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator(props);

        Annotation annotation = new Annotation("This is a sentence. This is another one.");
        List<CoreLabel> tokens = new ArrayList<>();
        
        CoreLabel token1 = new CoreLabel();
        token1.setWord("This");
        token1.set(CoreAnnotations.TextAnnotation.class, "This");
        token1.setIndex(0);
        
        CoreLabel token2 = new CoreLabel();
        token2.setWord("is");
        token2.set(CoreAnnotations.TextAnnotation.class, "is");
        token2.setIndex(1);
        
        CoreLabel token3 = new CoreLabel();
        token3.setWord("a");
        token3.set(CoreAnnotations.TextAnnotation.class, "a");
        token3.setIndex(2);
        
        CoreLabel token4 = new CoreLabel();
        token4.setWord("sentence.");
        token4.set(CoreAnnotations.TextAnnotation.class, "sentence.");
        token4.setIndex(3);
        
        CoreLabel token5 = new CoreLabel();
        token5.setWord("This");
        token5.set(CoreAnnotations.TextAnnotation.class, "This");
        token5.setIndex(4);
        
        CoreLabel token6 = new CoreLabel();
        token6.setWord("is");
        token6.set(CoreAnnotations.TextAnnotation.class, "is");
        token6.setIndex(5);
        
        CoreLabel token7 = new CoreLabel();
        token7.setWord("another");
        token7.set(CoreAnnotations.TextAnnotation.class, "another");
        token7.setIndex(6);
        
        CoreLabel token8 = new CoreLabel();
        token8.setWord("one.");
        token8.set(CoreAnnotations.TextAnnotation.class, "one.");
        token8.setIndex(7);
        
        tokens.add(token1);
        tokens.add(token2);
        tokens.add(token3);
        tokens.add(token4);
        tokens.add(token5);
        tokens.add(token6);
        tokens.add(token7);
        tokens.add(token8);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        
        annotator.annotate(annotation);

        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        assertNotNull(sentences);
        assertEquals(2, sentences.size());
        assertEquals("This is a sentence.", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
        assertEquals("This is another one.", sentences.get(1).get(CoreAnnotations.TextAnnotation.class));
    }
@Test
    public void testNewlineSplitter() {
        WordsToSentencesAnnotator newlineSplitterAnnotator = WordsToSentencesAnnotator.newlineSplitter("\n");

        Annotation annotation = new Annotation("First line\nSecond line\n\nThird line");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("First");
        token1.set(CoreAnnotations.TextAnnotation.class, "First");
        token1.setIndex(0);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("line");
        token2.set(CoreAnnotations.TextAnnotation.class, "line");
        token2.setIndex(1);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("\n");
        token3.set(CoreAnnotations.TextAnnotation.class, "\n");
        token3.setIndex(2);
        token3.set(CoreAnnotations.IsNewlineAnnotation.class, true);

        CoreLabel token4 = new CoreLabel();
        token4.setWord("Second");
        token4.set(CoreAnnotations.TextAnnotation.class, "Second");
        token4.setIndex(3);

        CoreLabel token5 = new CoreLabel();
        token5.setWord("line");
        token5.set(CoreAnnotations.TextAnnotation.class, "line");
        token5.setIndex(4);

        CoreLabel token6 = new CoreLabel();
        token6.setWord("\n");
        token6.set(CoreAnnotations.TextAnnotation.class, "\n");
        token6.setIndex(5);
        token6.set(CoreAnnotations.IsNewlineAnnotation.class, true);

        CoreLabel token7 = new CoreLabel();
        token7.setWord("Third");
        token7.set(CoreAnnotations.TextAnnotation.class, "Third");
        token7.setIndex(6);

        CoreLabel token8 = new CoreLabel();
        token8.setWord("line");
        token8.set(CoreAnnotations.TextAnnotation.class, "line");
        token8.setIndex(7);

        tokens.add(token1);
        tokens.add(token2);
        tokens.add(token3);
        tokens.add(token4);
        tokens.add(token5);
        tokens.add(token6);
        tokens.add(token7);
        tokens.add(token8);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

        newlineSplitterAnnotator.annotate(annotation);

        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        assertNotNull(sentences);
        assertEquals(3, sentences.size());
        assertEquals("First line", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
        assertEquals("Second line", sentences.get(1).get(CoreAnnotations.TextAnnotation.class));
        assertEquals("Third line", sentences.get(2).get(CoreAnnotations.TextAnnotation.class));
    }
@Test(expected = IllegalArgumentException.class)
    public void testAnnotateWithoutTokens() {
        Properties props = new Properties();
        WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator(props);

        Annotation annotation = new Annotation(""); 
        annotator.annotate(annotation);
    }
@Test
    public void testVerboseMode() {
        WordsToSentencesAnnotator verboseAnnotator = new WordsToSentencesAnnotator(true);
        Annotation annotation = new Annotation("Verbose sentence.");
        
        List<CoreLabel> tokens = new ArrayList<>();
        
        CoreLabel token = new CoreLabel();
        token.setWord("Verbose");
        token.set(CoreAnnotations.TextAnnotation.class, "Verbose");
        token.setIndex(0);
        
        tokens.add(token);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        
        verboseAnnotator.annotate(annotation);
    }
@Test
    public void testSingleWordText() {
        Properties props = new Properties();
        WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator(props);

        Annotation annotation = new Annotation("Word");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token = new CoreLabel();
        token.setWord("Word");
        token.set(CoreAnnotations.TextAnnotation.class, "Word");
        token.setIndex(0);
        tokens.add(token);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        
        annotator.annotate(annotation);

        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        assertNotNull(sentences);
        assertEquals(1, sentences.size());
        assertEquals("Word", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
    }
@Test
    public void testEmptyText() {
        Properties props = new Properties();
        WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator(props);

        Annotation annotation = new Annotation("");
        annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

        annotator.annotate(annotation);

        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        assertNotNull(sentences);
        assertTrue(sentences.isEmpty());
    }
@Test
    public void testMultiplePeriodsWithoutSpaces() {
        Properties props = new Properties();
        WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator(props);

        Annotation annotation = new Annotation("Hello.World!Another.Sentence.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("Hello.");
        token1.set(CoreAnnotations.TextAnnotation.class, "Hello.");
        token1.setIndex(0);
        
        CoreLabel token2 = new CoreLabel();
        token2.setWord("World!");
        token2.set(CoreAnnotations.TextAnnotation.class, "World!");
        token2.setIndex(1);
        
        CoreLabel token3 = new CoreLabel();
        token3.setWord("Another.");
        token3.set(CoreAnnotations.TextAnnotation.class, "Another.");
        token3.setIndex(2);
        
        CoreLabel token4 = new CoreLabel();
        token4.setWord("Sentence.");
        token4.set(CoreAnnotations.TextAnnotation.class, "Sentence.");
        token4.setIndex(3);
        
        tokens.add(token1);
        tokens.add(token2);
        tokens.add(token3);
        tokens.add(token4);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        
        annotator.annotate(annotation);

        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        assertNotNull(sentences);
        assertEquals(2, sentences.size());
        assertEquals("Hello.World!", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
        assertEquals("Another.Sentence.", sentences.get(1).get(CoreAnnotations.TextAnnotation.class));
    }
@Test
    public void testSentenceWithUnexpectedEmptyTokens() {
        Properties props = new Properties();
        WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator(props);

        Annotation annotation = new Annotation("This is a valid sentence.");

        List<CoreLabel> tokens = new ArrayList<>();
        
        CoreLabel token1 = new CoreLabel();
        token1.setWord("This");
        token1.set(CoreAnnotations.TextAnnotation.class, "This");
        token1.setIndex(0);
        
        CoreLabel token2 = new CoreLabel();
        token2.setWord("is");
        token2.set(CoreAnnotations.TextAnnotation.class, "is");
        token2.setIndex(1);
        
        CoreLabel token3 = new CoreLabel();
        token3.setWord("");
        token3.set(CoreAnnotations.TextAnnotation.class, "");
        token3.setIndex(2);
        
        CoreLabel token4 = new CoreLabel();
        token4.setWord("a");
        token4.set(CoreAnnotations.TextAnnotation.class, "a");
        token4.setIndex(3);
        
        CoreLabel token5 = new CoreLabel();
        token5.setWord("valid");
        token5.set(CoreAnnotations.TextAnnotation.class, "valid");
        token5.setIndex(4);
        
        CoreLabel token6 = new CoreLabel();
        token6.setWord("sentence.");
        token6.set(CoreAnnotations.TextAnnotation.class, "sentence.");
        token6.setIndex(5);
        
        tokens.add(token1);
        tokens.add(token2);
        tokens.add(token3);
        tokens.add(token4);
        tokens.add(token5);
        tokens.add(token6);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        
        annotator.annotate(annotation);
        
        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        assertNotNull(sentences);
        assertEquals(1, sentences.size());
        assertEquals("This is a valid sentence.", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
    }
@Test
    public void testTextWithExcessiveSpaces() {
        Properties props = new Properties();
        WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator(props);

        Annotation annotation = new Annotation("This    is    a    sentence.    Another       one.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("This");
        token1.set(CoreAnnotations.TextAnnotation.class, "This");
        token1.setIndex(0);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("is");
        token2.set(CoreAnnotations.TextAnnotation.class, "is");
        token2.setIndex(1);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("a");
        token3.set(CoreAnnotations.TextAnnotation.class, "a");
        token3.setIndex(2);

        CoreLabel token4 = new CoreLabel();
        token4.setWord("sentence.");
        token4.set(CoreAnnotations.TextAnnotation.class, "sentence.");
        token4.setIndex(3);

        CoreLabel token5 = new CoreLabel();
        token5.setWord("Another");
        token5.set(CoreAnnotations.TextAnnotation.class, "Another");
        token5.setIndex(4);

        CoreLabel token6 = new CoreLabel();
        token6.setWord("one.");
        token6.set(CoreAnnotations.TextAnnotation.class, "one.");
        token6.setIndex(5);

        tokens.add(token1);
        tokens.add(token2);
        tokens.add(token3);
        tokens.add(token4);
        tokens.add(token5);
        tokens.add(token6);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotator.annotate(annotation);

        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        assertNotNull(sentences);
        assertEquals(2, sentences.size());
        assertEquals("This    is    a    sentence.", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
        assertEquals("Another       one.", sentences.get(1).get(CoreAnnotations.TextAnnotation.class));
    }
@Test
    public void testOnlyPunctuationInput() {
        Properties props = new Properties();
        WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator(props);

        Annotation annotation = new Annotation("!!! ??? ...");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("!!!");
        token1.set(CoreAnnotations.TextAnnotation.class, "!!!");
        token1.setIndex(0);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("???");
        token2.set(CoreAnnotations.TextAnnotation.class, "???");
        token2.setIndex(1);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("...");
        token3.set(CoreAnnotations.TextAnnotation.class, "...");
        token3.setIndex(2);

        tokens.add(token1);
        tokens.add(token2);
        tokens.add(token3);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotator.annotate(annotation);

        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        assertNotNull(sentences);
        assertEquals(3, sentences.size());
        assertEquals("!!!", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
        assertEquals("???", sentences.get(1).get(CoreAnnotations.TextAnnotation.class));
        assertEquals("...", sentences.get(2).get(CoreAnnotations.TextAnnotation.class));
    }
@Test
    public void testSentenceWithQuotesAndPunctuation() {
        Properties props = new Properties();
        WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator(props);

        Annotation annotation = new Annotation("\"Hello!\" said John. \"How are you?\".");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("\"Hello!\"");
        token1.set(CoreAnnotations.TextAnnotation.class, "\"Hello!\"");
        token1.setIndex(0);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("said");
        token2.set(CoreAnnotations.TextAnnotation.class, "said");
        token2.setIndex(1);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("John.");
        token3.set(CoreAnnotations.TextAnnotation.class, "John.");
        token3.setIndex(2);

        CoreLabel token4 = new CoreLabel();
        token4.setWord("\"How");
        token4.set(CoreAnnotations.TextAnnotation.class, "\"How");
        token4.setIndex(3);

        CoreLabel token5 = new CoreLabel();
        token5.setWord("are");
        token5.set(CoreAnnotations.TextAnnotation.class, "are");
        token5.setIndex(4);

        CoreLabel token6 = new CoreLabel();
        token6.setWord("you?\".");
        token6.set(CoreAnnotations.TextAnnotation.class, "you?\".");
        token6.setIndex(5);

        tokens.add(token1);
        tokens.add(token2);
        tokens.add(token3);
        tokens.add(token4);
        tokens.add(token5);
        tokens.add(token6);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotator.annotate(annotation);

        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        assertNotNull(sentences);
        assertEquals(2, sentences.size());
        assertEquals("\"Hello!\" said John.", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
        assertEquals("\"How are you?\".", sentences.get(1).get(CoreAnnotations.TextAnnotation.class));
    }
@Test
    public void testMultipleNewlinesAndParagraphBreaks() {
        WordsToSentencesAnnotator newlineSplitterAnnotator = WordsToSentencesAnnotator.newlineSplitter("\n");

        Annotation annotation = new Annotation("This is a paragraph.\n\nThis is another paragraph.\n\n\nEnd.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("This");
        token1.set(CoreAnnotations.TextAnnotation.class, "This");
        token1.setIndex(0);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("is");
        token2.set(CoreAnnotations.TextAnnotation.class, "is");
        token2.setIndex(1);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("a");
        token3.set(CoreAnnotations.TextAnnotation.class, "a");
        token3.setIndex(2);

        CoreLabel token4 = new CoreLabel();
        token4.setWord("paragraph.");
        token4.set(CoreAnnotations.TextAnnotation.class, "paragraph.");
        token4.setIndex(3);

        CoreLabel token5 = new CoreLabel();
        token5.setWord("\n");
        token5.set(CoreAnnotations.IsNewlineAnnotation.class, true);
        token5.setIndex(4);

        CoreLabel token6 = new CoreLabel();
        token6.setWord("\n");
        token6.set(CoreAnnotations.IsNewlineAnnotation.class, true);
        token6.setIndex(5);

        CoreLabel token7 = new CoreLabel();
        token7.setWord("This");
        token7.set(CoreAnnotations.TextAnnotation.class, "This");
        token7.setIndex(6);

        CoreLabel token8 = new CoreLabel();
        token8.setWord("is");
        token8.set(CoreAnnotations.TextAnnotation.class, "is");
        token8.setIndex(7);

        CoreLabel token9 = new CoreLabel();
        token9.setWord("another");
        token9.set(CoreAnnotations.TextAnnotation.class, "another");
        token9.setIndex(8);

        CoreLabel token10 = new CoreLabel();
        token10.setWord("paragraph.");
        token10.set(CoreAnnotations.TextAnnotation.class, "paragraph.");
        token10.setIndex(9);

        CoreLabel token11 = new CoreLabel();
        token11.setWord("\n");
        token11.set(CoreAnnotations.IsNewlineAnnotation.class, true);
        token11.setIndex(10);

        CoreLabel token12 = new CoreLabel();
        token12.setWord("\n");
        token12.set(CoreAnnotations.IsNewlineAnnotation.class, true);
        token12.setIndex(11);

        CoreLabel token13 = new CoreLabel();
        token13.setWord("\n");
        token13.set(CoreAnnotations.IsNewlineAnnotation.class, true);
        token13.setIndex(12);

        CoreLabel token14 = new CoreLabel();
        token14.setWord("End.");
        token14.set(CoreAnnotations.TextAnnotation.class, "End.");
        token14.setIndex(13);

        tokens.add(token1);
        tokens.add(token2);
        tokens.add(token3);
        tokens.add(token4);
        tokens.add(token5);
        tokens.add(token6);
        tokens.add(token7);
        tokens.add(token8);
        tokens.add(token9);
        tokens.add(token10);
        tokens.add(token11);
        tokens.add(token12);
        tokens.add(token13);
        tokens.add(token14);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

        newlineSplitterAnnotator.annotate(annotation);

        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        assertNotNull(sentences);
        assertEquals(3, sentences.size());
        assertEquals("This is a paragraph.", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
        assertEquals("This is another paragraph.", sentences.get(1).get(CoreAnnotations.TextAnnotation.class));
        assertEquals("End.", sentences.get(2).get(CoreAnnotations.TextAnnotation.class));
    }
@Test
    public void testSentenceEndingWithEllipsis() {
        Properties props = new Properties();
        WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator(props);

        Annotation annotation = new Annotation("This is a sentence... And another one.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("This");
        token1.set(CoreAnnotations.TextAnnotation.class, "This");
        token1.setIndex(0);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("is");
        token2.set(CoreAnnotations.TextAnnotation.class, "is");
        token2.setIndex(1);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("a");
        token3.set(CoreAnnotations.TextAnnotation.class, "a");
        token3.setIndex(2);

        CoreLabel token4 = new CoreLabel();
        token4.setWord("sentence...");
        token4.set(CoreAnnotations.TextAnnotation.class, "sentence...");
        token4.setIndex(3);

        CoreLabel token5 = new CoreLabel();
        token5.setWord("And");
        token5.set(CoreAnnotations.TextAnnotation.class, "And");
        token5.setIndex(4);

        CoreLabel token6 = new CoreLabel();
        token6.setWord("another");
        token6.set(CoreAnnotations.TextAnnotation.class, "another");
        token6.setIndex(5);

        CoreLabel token7 = new CoreLabel();
        token7.setWord("one.");
        token7.set(CoreAnnotations.TextAnnotation.class, "one.");
        token7.setIndex(6);

        tokens.add(token1);
        tokens.add(token2);
        tokens.add(token3);
        tokens.add(token4);
        tokens.add(token5);
        tokens.add(token6);
        tokens.add(token7);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotator.annotate(annotation);

        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        assertNotNull(sentences);
        assertEquals(2, sentences.size());
        assertEquals("This is a sentence...", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
        assertEquals("And another one.", sentences.get(1).get(CoreAnnotations.TextAnnotation.class));
    }
@Test
    public void testSentenceWithMultipleExclamationMarks() {
        Properties props = new Properties();
        WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator(props);

        Annotation annotation = new Annotation("Wow!!! This is amazing!! What???");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("Wow!!!");
        token1.set(CoreAnnotations.TextAnnotation.class, "Wow!!!");
        token1.setIndex(0);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("This");
        token2.set(CoreAnnotations.TextAnnotation.class, "This");
        token2.setIndex(1);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("is");
        token3.set(CoreAnnotations.TextAnnotation.class, "is");
        token3.setIndex(2);

        CoreLabel token4 = new CoreLabel();
        token4.setWord("amazing!!");
        token4.set(CoreAnnotations.TextAnnotation.class, "amazing!!");
        token4.setIndex(3);

        CoreLabel token5 = new CoreLabel();
        token5.setWord("What???");
        token5.set(CoreAnnotations.TextAnnotation.class, "What???");
        token5.setIndex(4);

        tokens.add(token1);
        tokens.add(token2);
        tokens.add(token3);
        tokens.add(token4);
        tokens.add(token5);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotator.annotate(annotation);

        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        assertNotNull(sentences);
        assertEquals(3, sentences.size());
        assertEquals("Wow!!!", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
        assertEquals("This is amazing!!", sentences.get(1).get(CoreAnnotations.TextAnnotation.class));
        assertEquals("What???", sentences.get(2).get(CoreAnnotations.TextAnnotation.class));
    }
@Test
    public void testSentenceWithParentheses() {
        Properties props = new Properties();
        WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator(props);

        Annotation annotation = new Annotation("This is a sentence (with a note). Another sentence follows.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("This");
        token1.set(CoreAnnotations.TextAnnotation.class, "This");
        token1.setIndex(0);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("is");
        token2.set(CoreAnnotations.TextAnnotation.class, "is");
        token2.setIndex(1);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("a");
        token3.set(CoreAnnotations.TextAnnotation.class, "a");
        token3.setIndex(2);

        CoreLabel token4 = new CoreLabel();
        token4.setWord("sentence");
        token4.set(CoreAnnotations.TextAnnotation.class, "sentence");
        token4.setIndex(3);

        CoreLabel token5 = new CoreLabel();
        token5.setWord("(with");
        token5.set(CoreAnnotations.TextAnnotation.class, "(with");
        token5.setIndex(4);

        CoreLabel token6 = new CoreLabel();
        token6.setWord("a");
        token6.set(CoreAnnotations.TextAnnotation.class, "a");
        token6.setIndex(5);

        CoreLabel token7 = new CoreLabel();
        token7.setWord("note).");
        token7.set(CoreAnnotations.TextAnnotation.class, "note).");
        token7.setIndex(6);

        CoreLabel token8 = new CoreLabel();
        token8.setWord("Another");
        token8.set(CoreAnnotations.TextAnnotation.class, "Another");
        token8.setIndex(7);

        CoreLabel token9 = new CoreLabel();
        token9.setWord("sentence");
        token9.set(CoreAnnotations.TextAnnotation.class, "sentence");
        token9.setIndex(8);

        CoreLabel token10 = new CoreLabel();
        token10.setWord("follows.");
        token10.set(CoreAnnotations.TextAnnotation.class, "follows.");
        token10.setIndex(9);

        tokens.add(token1);
        tokens.add(token2);
        tokens.add(token3);
        tokens.add(token4);
        tokens.add(token5);
        tokens.add(token6);
        tokens.add(token7);
        tokens.add(token8);
        tokens.add(token9);
        tokens.add(token10);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotator.annotate(annotation);

        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        assertNotNull(sentences);
        assertEquals(2, sentences.size());
        assertEquals("This is a sentence (with a note).", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
        assertEquals("Another sentence follows.", sentences.get(1).get(CoreAnnotations.TextAnnotation.class));
    }
@Test
    public void testConsecutiveSentenceSeparators() {
        Properties props = new Properties();
        WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator(props);

        Annotation annotation = new Annotation("Hello!!?? This is a test... Right?");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("Hello!!??");
        token1.set(CoreAnnotations.TextAnnotation.class, "Hello!!??");
        token1.setIndex(0);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("This");
        token2.set(CoreAnnotations.TextAnnotation.class, "This");
        token2.setIndex(1);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("is");
        token3.set(CoreAnnotations.TextAnnotation.class, "is");
        token3.setIndex(2);

        CoreLabel token4 = new CoreLabel();
        token4.setWord("a");
        token4.set(CoreAnnotations.TextAnnotation.class, "a");
        token4.setIndex(3);

        CoreLabel token5 = new CoreLabel();
        token5.setWord("test...");
        token5.set(CoreAnnotations.TextAnnotation.class, "test...");
        token5.setIndex(4);

        CoreLabel token6 = new CoreLabel();
        token6.setWord("Right?");
        token6.set(CoreAnnotations.TextAnnotation.class, "Right?");
        token6.setIndex(5);

        tokens.add(token1);
        tokens.add(token2);
        tokens.add(token3);
        tokens.add(token4);
        tokens.add(token5);
        tokens.add(token6);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotator.annotate(annotation);

        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        assertNotNull(sentences);
        assertEquals(3, sentences.size());
        assertEquals("Hello!!??", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
        assertEquals("This is a test...", sentences.get(1).get(CoreAnnotations.TextAnnotation.class));
        assertEquals("Right?", sentences.get(2).get(CoreAnnotations.TextAnnotation.class));
    }
@Test
    public void testSentenceWithMixedAlphanumericTokens() {
        Properties props = new Properties();
        WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator(props);

        Annotation annotation = new Annotation("This is version 1.2 of NLP v3.0.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("This");
        token1.set(CoreAnnotations.TextAnnotation.class, "This");
        token1.setIndex(0);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("is");
        token2.set(CoreAnnotations.TextAnnotation.class, "is");
        token2.setIndex(1);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("version");
        token3.set(CoreAnnotations.TextAnnotation.class, "version");
        token3.setIndex(2);

        CoreLabel token4 = new CoreLabel();
        token4.setWord("1.2");
        token4.set(CoreAnnotations.TextAnnotation.class, "1.2");
        token4.setIndex(3);

        CoreLabel token5 = new CoreLabel();
        token5.setWord("of");
        token5.set(CoreAnnotations.TextAnnotation.class, "of");
        token5.setIndex(4);

        CoreLabel token6 = new CoreLabel();
        token6.setWord("NLP");
        token6.set(CoreAnnotations.TextAnnotation.class, "NLP");
        token6.setIndex(5);

        CoreLabel token7 = new CoreLabel();
        token7.setWord("v3.0.");
        token7.set(CoreAnnotations.TextAnnotation.class, "v3.0.");
        token7.setIndex(6);

        tokens.add(token1);
        tokens.add(token2);
        tokens.add(token3);
        tokens.add(token4);
        tokens.add(token5);
        tokens.add(token6);
        tokens.add(token7);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotator.annotate(annotation);

        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        assertNotNull(sentences);
        assertEquals(1, sentences.size());
        assertEquals("This is version 1.2 of NLP v3.0.", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
    }
@Test
    public void testTextWithSingleNewline() {
        WordsToSentencesAnnotator newlineSplitterAnnotator = WordsToSentencesAnnotator.newlineSplitter("\n");

        Annotation annotation = new Annotation("This is a sentence.\nHere is another.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("This");
        token1.set(CoreAnnotations.TextAnnotation.class, "This");
        token1.setIndex(0);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("is");
        token2.set(CoreAnnotations.TextAnnotation.class, "is");
        token2.setIndex(1);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("a");
        token3.set(CoreAnnotations.TextAnnotation.class, "a");
        token3.setIndex(2);

        CoreLabel token4 = new CoreLabel();
        token4.setWord("sentence.");
        token4.set(CoreAnnotations.TextAnnotation.class, "sentence.");
        token4.setIndex(3);

        CoreLabel token5 = new CoreLabel();
        token5.setWord("\n");
        token5.set(CoreAnnotations.IsNewlineAnnotation.class, true);
        token5.setIndex(4);

        CoreLabel token6 = new CoreLabel();
        token6.setWord("Here");
        token6.set(CoreAnnotations.TextAnnotation.class, "Here");
        token6.setIndex(5);

        CoreLabel token7 = new CoreLabel();
        token7.setWord("is");
        token7.set(CoreAnnotations.TextAnnotation.class, "is");
        token7.setIndex(6);

        CoreLabel token8 = new CoreLabel();
        token8.setWord("another.");
        token8.set(CoreAnnotations.TextAnnotation.class, "another.");
        token8.setIndex(7);

        tokens.add(token1);
        tokens.add(token2);
        tokens.add(token3);
        tokens.add(token4);
        tokens.add(token5);
        tokens.add(token6);
        tokens.add(token7);
        tokens.add(token8);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

        newlineSplitterAnnotator.annotate(annotation);

        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        assertNotNull(sentences);
        assertEquals(2, sentences.size());
        assertEquals("This is a sentence.", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
        assertEquals("Here is another.", sentences.get(1).get(CoreAnnotations.TextAnnotation.class));
    }
@Test
    public void testSentenceWithUnusualWhitespace() {
        Properties props = new Properties();
        WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator(props);

        Annotation annotation = new Annotation("This  is a   sentence .   Another    one.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("This");
        token1.set(CoreAnnotations.TextAnnotation.class, "This");
        token1.setIndex(0);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("is");
        token2.set(CoreAnnotations.TextAnnotation.class, "is");
        token2.setIndex(1);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("a");
        token3.set(CoreAnnotations.TextAnnotation.class, "a");
        token3.setIndex(2);

        CoreLabel token4 = new CoreLabel();
        token4.setWord("sentence");
        token4.set(CoreAnnotations.TextAnnotation.class, "sentence");
        token4.setIndex(3);

        CoreLabel token5 = new CoreLabel();
        token5.setWord(".");
        token5.set(CoreAnnotations.TextAnnotation.class, ".");
        token5.setIndex(4);

        CoreLabel token6 = new CoreLabel();
        token6.setWord("Another");
        token6.set(CoreAnnotations.TextAnnotation.class, "Another");
        token6.setIndex(5);

        CoreLabel token7 = new CoreLabel();
        token7.setWord("one.");
        token7.set(CoreAnnotations.TextAnnotation.class, "one.");
        token7.setIndex(6);

        tokens.add(token1);
        tokens.add(token2);
        tokens.add(token3);
        tokens.add(token4);
        tokens.add(token5);
        tokens.add(token6);
        tokens.add(token7);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotator.annotate(annotation);

        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        assertNotNull(sentences);
        assertEquals(2, sentences.size());
        assertEquals("This  is a   sentence .", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
        assertEquals("Another    one.", sentences.get(1).get(CoreAnnotations.TextAnnotation.class));
    }
@Test
    public void testAbbreviationsHandling() {
        Properties props = new Properties();
        WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator(props);

        Annotation annotation = new Annotation("Dr. Smith arrived at 10 a.m. He said hello.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("Dr.");
        token1.set(CoreAnnotations.TextAnnotation.class, "Dr.");
        token1.setIndex(0);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Smith");
        token2.set(CoreAnnotations.TextAnnotation.class, "Smith");
        token2.setIndex(1);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("arrived");
        token3.set(CoreAnnotations.TextAnnotation.class, "arrived");
        token3.setIndex(2);

        CoreLabel token4 = new CoreLabel();
        token4.setWord("at");
        token4.set(CoreAnnotations.TextAnnotation.class, "at");
        token4.setIndex(3);

        CoreLabel token5 = new CoreLabel();
        token5.setWord("10");
        token5.set(CoreAnnotations.TextAnnotation.class, "10");
        token5.setIndex(4);

        CoreLabel token6 = new CoreLabel();
        token6.setWord("a.m.");
        token6.set(CoreAnnotations.TextAnnotation.class, "a.m.");
        token6.setIndex(5);

        CoreLabel token7 = new CoreLabel();
        token7.setWord("He");
        token7.set(CoreAnnotations.TextAnnotation.class, "He");
        token7.setIndex(6);

        CoreLabel token8 = new CoreLabel();
        token8.setWord("said");
        token8.set(CoreAnnotations.TextAnnotation.class, "said");
        token8.setIndex(7);

        CoreLabel token9 = new CoreLabel();
        token9.setWord("hello.");
        token9.set(CoreAnnotations.TextAnnotation.class, "hello.");
        token9.setIndex(8);

        tokens.add(token1);
        tokens.add(token2);
        tokens.add(token3);
        tokens.add(token4);
        tokens.add(token5);
        tokens.add(token6);
        tokens.add(token7);
        tokens.add(token8);
        tokens.add(token9);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotator.annotate(annotation);

        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        assertNotNull(sentences);
        assertEquals(2, sentences.size());
        assertEquals("Dr. Smith arrived at 10 a.m.", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
        assertEquals("He said hello.", sentences.get(1).get(CoreAnnotations.TextAnnotation.class));
    }
@Test
    public void testSentenceSplitterWithEmojiSymbols() {
        Properties props = new Properties();
        WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator(props);

        Annotation annotation = new Annotation("I am happy 😊. Are you?");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("I");
        token1.set(CoreAnnotations.TextAnnotation.class, "I");
        token1.setIndex(0);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("am");
        token2.set(CoreAnnotations.TextAnnotation.class, "am");
        token2.setIndex(1);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("happy");
        token3.set(CoreAnnotations.TextAnnotation.class, "happy");
        token3.setIndex(2);

        CoreLabel token4 = new CoreLabel();
        token4.setWord("😊.");
        token4.set(CoreAnnotations.TextAnnotation.class, "😊.");
        token4.setIndex(3);

        CoreLabel token5 = new CoreLabel();
        token5.setWord("Are");
        token5.set(CoreAnnotations.TextAnnotation.class, "Are");
        token5.setIndex(4);

        CoreLabel token6 = new CoreLabel();
        token6.setWord("you?");
        token6.set(CoreAnnotations.TextAnnotation.class, "you?");
        token6.setIndex(5);

        tokens.add(token1);
        tokens.add(token2);
        tokens.add(token3);
        tokens.add(token4);
        tokens.add(token5);
        tokens.add(token6);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotator.annotate(annotation);

        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        assertNotNull(sentences);
        assertEquals(2, sentences.size());
        assertEquals("I am happy 😊.", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
        assertEquals("Are you?", sentences.get(1).get(CoreAnnotations.TextAnnotation.class));
    }
@Test
    public void testMultipleNewlinesWithSpacesBetween() {
        WordsToSentencesAnnotator newlineSplitterAnnotator = WordsToSentencesAnnotator.newlineSplitter("\n");

        Annotation annotation = new Annotation("Line 1.\n  \nLine 2.\n \n  \nLine 3.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("Line");
        token1.set(CoreAnnotations.TextAnnotation.class, "Line");
        token1.setIndex(0);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("1.");
        token2.set(CoreAnnotations.TextAnnotation.class, "1.");
        token2.setIndex(1);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("\n");
        token3.set(CoreAnnotations.IsNewlineAnnotation.class, true);
        token3.setIndex(2);

        CoreLabel token4 = new CoreLabel();
        token4.setWord("Line");
        token4.set(CoreAnnotations.TextAnnotation.class, "Line");
        token4.setIndex(3);

        CoreLabel token5 = new CoreLabel();
        token5.setWord("2.");
        token5.set(CoreAnnotations.TextAnnotation.class, "2.");
        token5.setIndex(4);

        CoreLabel token6 = new CoreLabel();
        token6.setWord("\n");
        token6.set(CoreAnnotations.IsNewlineAnnotation.class, true);
        token6.setIndex(5);

        CoreLabel token7 = new CoreLabel();
        token7.setWord("Line");
        token7.set(CoreAnnotations.TextAnnotation.class, "Line");
        token7.setIndex(6);

        CoreLabel token8 = new CoreLabel();
        token8.setWord("3.");
        token8.set(CoreAnnotations.TextAnnotation.class, "3.");
        token8.setIndex(7);

        tokens.add(token1);
        tokens.add(token2);
        tokens.add(token3);
        tokens.add(token4);
        tokens.add(token5);
        tokens.add(token6);
        tokens.add(token7);
        tokens.add(token8);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

        newlineSplitterAnnotator.annotate(annotation);

        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        assertNotNull(sentences);
        assertEquals(3, sentences.size());
        assertEquals("Line 1.", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
        assertEquals("Line 2.", sentences.get(1).get(CoreAnnotations.TextAnnotation.class));
        assertEquals("Line 3.", sentences.get(2).get(CoreAnnotations.TextAnnotation.class));
    }
@Test
    public void testSpecialCharactersInText() {
        Properties props = new Properties();
        WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator(props);

        Annotation annotation = new Annotation("Hello @JohnDoe, how are you? #excited!");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("Hello");
        token1.set(CoreAnnotations.TextAnnotation.class, "Hello");
        token1.setIndex(0);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("@JohnDoe,");
        token2.set(CoreAnnotations.TextAnnotation.class, "@JohnDoe,");
        token2.setIndex(1);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("how");
        token3.set(CoreAnnotations.TextAnnotation.class, "how");
        token3.setIndex(2);

        CoreLabel token4 = new CoreLabel();
        token4.setWord("are");
        token4.set(CoreAnnotations.TextAnnotation.class, "are");
        token4.setIndex(3);

        CoreLabel token5 = new CoreLabel();
        token5.setWord("you?");
        token5.set(CoreAnnotations.TextAnnotation.class, "you?");
        token5.setIndex(4);

        CoreLabel token6 = new CoreLabel();
        token6.setWord("#excited!");
        token6.set(CoreAnnotations.TextAnnotation.class, "#excited!");
        token6.setIndex(5);

        tokens.add(token1);
        tokens.add(token2);
        tokens.add(token3);
        tokens.add(token4);
        tokens.add(token5);
        tokens.add(token6);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotator.annotate(annotation);

        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        assertNotNull(sentences);
        assertEquals(2, sentences.size());
        assertEquals("Hello @JohnDoe, how are you?", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
        assertEquals("#excited!", sentences.get(1).get(CoreAnnotations.TextAnnotation.class));
    }
@Test
    public void testEdgeCaseWithNoSpacesBetweenSentences() {
        Properties props = new Properties();
        WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator(props);

        Annotation annotation = new Annotation("This is one.The second is here.And the third.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("This");
        token1.set(CoreAnnotations.TextAnnotation.class, "This");
        token1.setIndex(0);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("is");
        token2.set(CoreAnnotations.TextAnnotation.class, "is");
        token2.setIndex(1);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("one.");
        token3.set(CoreAnnotations.TextAnnotation.class, "one.");
        token3.setIndex(2);

        CoreLabel token4 = new CoreLabel();
        token4.setWord("The");
        token4.set(CoreAnnotations.TextAnnotation.class, "The");
        token4.setIndex(3);

        CoreLabel token5 = new CoreLabel();
        token5.setWord("second");
        token5.set(CoreAnnotations.TextAnnotation.class, "second");
        token5.setIndex(4);

        CoreLabel token6 = new CoreLabel();
        token6.setWord("is");
        token6.set(CoreAnnotations.TextAnnotation.class, "is");
        token6.setIndex(5);

        CoreLabel token7 = new CoreLabel();
        token7.setWord("here.");
        token7.set(CoreAnnotations.TextAnnotation.class, "here.");
        token7.setIndex(6);

        CoreLabel token8 = new CoreLabel();
        token8.setWord("And");
        token8.set(CoreAnnotations.TextAnnotation.class, "And");
        token8.setIndex(7);

        CoreLabel token9 = new CoreLabel();
        token9.setWord("the");
        token9.set(CoreAnnotations.TextAnnotation.class, "the");
        token9.setIndex(8);

        CoreLabel token10 = new CoreLabel();
        token10.setWord("third.");
        token10.set(CoreAnnotations.TextAnnotation.class, "third.");
        token10.setIndex(9);

        tokens.add(token1);
        tokens.add(token2);
        tokens.add(token3);
        tokens.add(token4);
        tokens.add(token5);
        tokens.add(token6);
        tokens.add(token7);
        tokens.add(token8);
        tokens.add(token9);
        tokens.add(token10);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        
        annotator.annotate(annotation);
        
        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        assertNotNull(sentences);
        assertEquals(3, sentences.size());
        assertEquals("This is one.", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
        assertEquals("The second is here.", sentences.get(1).get(CoreAnnotations.TextAnnotation.class));
        assertEquals("And the third.", sentences.get(2).get(CoreAnnotations.TextAnnotation.class));
    } 
}