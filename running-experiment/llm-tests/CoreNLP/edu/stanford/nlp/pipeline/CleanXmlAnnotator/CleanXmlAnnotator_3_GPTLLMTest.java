package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import static org.junit.Assert.*;


public class CleanXmlAnnotator_3_GPTLLMTest {

 @Test
    public void testConstructorWithProperties() {
        Properties properties = new Properties();
        properties.setProperty("clean.xmltags", "p|div");
        properties.setProperty("clean.sentenceendingtags", "p");

        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Pattern expectedXmlTagMatcher = Pattern.compile("p|div", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        Pattern expectedSentenceEndingTagMatcher = Pattern.compile("p", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    }
@Test
    public void testProcessRemovesXmlTags() {
        CleanXmlAnnotator annotator = new CleanXmlAnnotator();
        
        List<CoreLabel> tokens = new ArrayList<>();
        CoreLabel token1 = new CoreLabel();
        token1.setWord("<p>");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Hello");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("world");
        tokens.add(token3);

        CoreLabel token4 = new CoreLabel();
        token4.setWord("</p>");
        tokens.add(token4);

        List<CoreLabel> processedTokens = annotator.process(tokens);

        assertEquals(2, processedTokens.size());
        assertEquals("Hello", processedTokens.get(0).word());
        assertEquals("world", processedTokens.get(1).word());
    }
@Test(expected = IllegalArgumentException.class)
    public void testProcessThrowsExceptionOnMismatchedTags() {
        Properties properties = new Properties();
        properties.setProperty("clean.allowflawedxml", "false");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        List<CoreLabel> tokens = new ArrayList<>();
        
        CoreLabel token1 = new CoreLabel();
        token1.setWord("<p>");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Mismatch test");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("</div>");
        tokens.add(token3);

        annotator.process(tokens);
    }
@Test
    public void testProcessHandlesFlawedXmlGracefully() {
        Properties properties = new Properties();
        properties.setProperty("clean.allowflawedxml", "true");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<p>");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Mismatch test");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("</div>");
        tokens.add(token3);

        List<CoreLabel> processedTokens = annotator.process(tokens);

        assertEquals(1, processedTokens.size());
        assertEquals("Mismatch test", processedTokens.get(0).word());
    }
@Test
    public void testSentenceEndingTagSplitting() {
        Properties properties = new Properties();
        properties.setProperty("clean.sentenceendingtags", "p");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("First");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("<p>");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("Second");
        tokens.add(token3);

        List<CoreLabel> processedTokens = annotator.process(tokens);

        assertTrue(processedTokens.get(0).containsKey(CoreAnnotations.ForcedSentenceEndAnnotation.class));
    }
@Test
    public void testAnnotateHandlesTokenAnnotations() {
        CleanXmlAnnotator annotator = new CleanXmlAnnotator();
        Annotation annotation = new Annotation("");

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<speaker>");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Alice");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("</speaker>");
        tokens.add(token3);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        List<CoreLabel> processedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertEquals(1, processedTokens.size());
        assertEquals("Alice", processedTokens.get(0).word());
        assertNotNull(processedTokens.get(0).get(CoreAnnotations.SpeakerAnnotation.class));
        assertEquals("Alice", processedTokens.get(0).get(CoreAnnotations.SpeakerAnnotation.class));
    }
@Test
    public void testHandleQuoteTagWithAnnotations() {
        Properties properties = new Properties();
        properties.setProperty("clean.quotetags", "quote");
        properties.setProperty("clean.quoteauthorattributes", "author");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<quote author='John Doe'>");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Quoted text here");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("</quote>");
        tokens.add(token3);

        List<CoreLabel> processedTokens = annotator.process(tokens);
        
        assertEquals(1, processedTokens.size());
        assertEquals("Quoted text here", processedTokens.get(0).word());
    }
@Test
    public void testProcessWithEmptyTokensList() {
        CleanXmlAnnotator annotator = new CleanXmlAnnotator();
        List<CoreLabel> tokens = new ArrayList<>(); 

        List<CoreLabel> processedTokens = annotator.process(tokens);

        assertTrue(processedTokens.isEmpty());
    }
@Test
    public void testProcessWithOnlyXmlTags() {
        CleanXmlAnnotator annotator = new CleanXmlAnnotator();
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<doc>");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("</doc>");
        tokens.add(token2);

        List<CoreLabel> processedTokens = annotator.process(tokens);

        assertTrue(processedTokens.isEmpty());
    }
@Test
    public void testProcessHandlesMalformedXmlTagGracefully() {
        Properties properties = new Properties();
        properties.setProperty("clean.allowflawedxml", "true");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<unclosed");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("text");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("</closed>");
        tokens.add(token3);

        List<CoreLabel> processedTokens = annotator.process(tokens);

        assertEquals(1, processedTokens.size());
        assertEquals("text", processedTokens.get(0).word());
    }
@Test(expected = IllegalArgumentException.class)
    public void testProcessFailsOnMalformedXmlWhenStrictModeIsEnabled() {
        Properties properties = new Properties();
        properties.setProperty("clean.allowflawedxml", "false");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<unclosed");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("text");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("</closed>");
        tokens.add(token3);

        annotator.process(tokens);
    }
@Test
    public void testSingleSentenceTagPreventsSplitting() {
        Properties properties = new Properties();
        properties.setProperty("clean.singlesentencetags", "post");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<post>");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("This is a sentence.");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("Another sentence.");
        tokens.add(token3);

        CoreLabel token4 = new CoreLabel();
        token4.setWord("</post>");
        tokens.add(token4);

        List<CoreLabel> processedTokens = annotator.process(tokens);

        assertEquals(2, processedTokens.size());
        assertTrue(processedTokens.get(0).containsKey(CoreAnnotations.ForcedSentenceUntilEndAnnotation.class));
    }
@Test
    public void testProcessWithMultipleAttributesInTag() {
        Properties properties = new Properties();
        properties.setProperty("clean.docAnnotations", "author=name,source=doc[source]");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<doc name='Alice' source='Book'>");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Content inside.");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("</doc>");
        tokens.add(token3);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        assertEquals("Alice", annotation.get(CoreAnnotations.AuthorAnnotation.class));
    }
@Test
    public void testProcessRetainsTextWithUnmatchedTag() {
        CleanXmlAnnotator annotator = new CleanXmlAnnotator();

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("Outside text.");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("<tag>");
        tokens.add(token2);
        
        CoreLabel token3 = new CoreLabel();
        token3.setWord("Inside tag.");
        tokens.add(token3);

        List<CoreLabel> processedTokens = annotator.process(tokens);

        assertEquals(2, processedTokens.size());
        assertEquals("Outside text.", processedTokens.get(0).word());
        assertEquals("Inside tag.", processedTokens.get(1).word());
    }
@Test
    public void testProcessHandlesSequentialTagsCorrectly() {
        CleanXmlAnnotator annotator = new CleanXmlAnnotator();

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<p>");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("</p>");
        tokens.add(token2);
        
        CoreLabel token3 = new CoreLabel();
        token3.setWord("<div>");
        tokens.add(token3);

        CoreLabel token4 = new CoreLabel();
        token4.setWord("</div>");
        tokens.add(token4);

        CoreLabel token5 = new CoreLabel();
        token5.setWord("Valid text.");
        tokens.add(token5);

        List<CoreLabel> processedTokens = annotator.process(tokens);

        assertEquals(1, processedTokens.size());
        assertEquals("Valid text.", processedTokens.get(0).word());
    }
@Test
    public void testProcessHandlesMultipleNestedSections() {
        Properties properties = new Properties();
        properties.setProperty("clean.sectiontags", "section");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<section>");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("First section text.");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("<section>");
        tokens.add(token3);

        CoreLabel token4 = new CoreLabel();
        token4.setWord("Nested section text.");
        tokens.add(token4);

        CoreLabel token5 = new CoreLabel();
        token5.setWord("</section>");
        tokens.add(token5);

        CoreLabel token6 = new CoreLabel();
        token6.setWord("</section>");
        tokens.add(token6);

        List<CoreLabel> processedTokens = annotator.process(tokens);

        assertEquals(2, processedTokens.size());
        assertEquals("First section text.", processedTokens.get(0).word());
        assertEquals("Nested section text.", processedTokens.get(1).word());
    }
@Test
    public void testProcessHandlesSelfClosingTags() {
        CleanXmlAnnotator annotator = new CleanXmlAnnotator();

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("Text before.");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("<br/>");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("Text after.");
        tokens.add(token3);

        List<CoreLabel> processedTokens = annotator.process(tokens);

        assertEquals(2, processedTokens.size());
        assertEquals("Text before.", processedTokens.get(0).word());
        assertEquals("Text after.", processedTokens.get(1).word());
    }
@Test
    public void testProcessHandlesMultipleSentenceEndingTags() {
        Properties properties = new Properties();
        properties.setProperty("clean.sentenceendingtags", "p|div");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("Sentence one");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("<p>");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("Sentence two");
        tokens.add(token3);

        CoreLabel token4 = new CoreLabel();
        token4.setWord("<div>");
        tokens.add(token4);

        CoreLabel token5 = new CoreLabel();
        token5.setWord("Sentence three");
        tokens.add(token5);

        List<CoreLabel> processedTokens = annotator.process(tokens);

        assertEquals(3, processedTokens.size());
        assertTrue(processedTokens.get(0).containsKey(CoreAnnotations.ForcedSentenceEndAnnotation.class));
        assertTrue(processedTokens.get(1).containsKey(CoreAnnotations.ForcedSentenceEndAnnotation.class));
    }
@Test
    public void testProcessWithNestedSingleSentenceTags() {
        Properties properties = new Properties();
        properties.setProperty("clean.singlesentencetags", "post|comment");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<post>");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Post text.");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("<comment>");
        tokens.add(token3);

        CoreLabel token4 = new CoreLabel();
        token4.setWord("Comment text.");
        tokens.add(token4);

        CoreLabel token5 = new CoreLabel();
        token5.setWord("</comment>");
        tokens.add(token5);

        CoreLabel token6 = new CoreLabel();
        token6.setWord("</post>");
        tokens.add(token6);

        List<CoreLabel> processedTokens = annotator.process(tokens);

        assertEquals(2, processedTokens.size());
        assertTrue(processedTokens.get(0).containsKey(CoreAnnotations.ForcedSentenceUntilEndAnnotation.class));
        assertTrue(processedTokens.get(1).containsKey(CoreAnnotations.ForcedSentenceUntilEndAnnotation.class));
    }
@Test
    public void testProcessWithNestedQuoteTags() {
        Properties properties = new Properties();
        properties.setProperty("clean.quotetags", "quote");
        properties.setProperty("clean.quoteauthorattributes", "author");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<quote author='Alice'>");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("First level quote.");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("<quote author='Bob'>");
        tokens.add(token3);

        CoreLabel token4 = new CoreLabel();
        token4.setWord("Nested quote text.");
        tokens.add(token4);

        CoreLabel token5 = new CoreLabel();
        token5.setWord("</quote>");
        tokens.add(token5);

        CoreLabel token6 = new CoreLabel();
        token6.setWord("</quote>");
        tokens.add(token6);

        List<CoreLabel> processedTokens = annotator.process(tokens);

        assertEquals(2, processedTokens.size());
        assertEquals("First level quote.", processedTokens.get(0).word());
        assertEquals("Nested quote text.", processedTokens.get(1).word());
    }
@Test
    public void testProcessHandlesSectionAnnotations() {
        Properties properties = new Properties();
        properties.setProperty("clean.sectiontags", "post");
        properties.setProperty("clean.sectionannotations", "author=post[author]");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<post author='John'>");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Post content.");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("</post>");
        tokens.add(token3);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        List<CoreMap> sections = annotation.get(CoreAnnotations.SectionsAnnotation.class);
        assertNotNull(sections);
        assertEquals(1, sections.size());
        assertEquals("John", sections.get(0).get(CoreAnnotations.AuthorAnnotation.class));
    }
@Test
    public void testProcessHandlesNoTagsAtAll() {
        CleanXmlAnnotator annotator = new CleanXmlAnnotator();

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("This");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("is");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("a");
        tokens.add(token3);

        CoreLabel token4 = new CoreLabel();
        token4.setWord("normal");
        tokens.add(token4);

        CoreLabel token5 = new CoreLabel();
        token5.setWord("sentence.");
        tokens.add(token5);

        List<CoreLabel> processedTokens = annotator.process(tokens);

        assertEquals(5, processedTokens.size());
        assertEquals("This", processedTokens.get(0).word());
        assertEquals("sentence.", processedTokens.get(4).word());
    }
@Test
    public void testProcessHandlesIncorrectlyNestedTags() {
        Properties properties = new Properties();
        properties.setProperty("clean.allowflawedxml", "true");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<b>");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("<i>");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("text");
        tokens.add(token3);

        CoreLabel token4 = new CoreLabel();
        token4.setWord("</b>");
        tokens.add(token4);

        CoreLabel token5 = new CoreLabel();
        token5.setWord("</i>");
        tokens.add(token5);

        List<CoreLabel> processedTokens = annotator.process(tokens);

        assertEquals(1, processedTokens.size());
        assertEquals("text", processedTokens.get(0).word());
    }
@Test
    public void testProcessHandlesTokensWithLeadingXmlTags() {
        CleanXmlAnnotator annotator = new CleanXmlAnnotator();

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<doc>");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Important information.");
        tokens.add(token2);

        List<CoreLabel> processedTokens = annotator.process(tokens);

        assertEquals(1, processedTokens.size());
        assertEquals("Important information.", processedTokens.get(0).word());
    }
@Test
    public void testProcessHandlesTokensWithTrailingXmlTags() {
        CleanXmlAnnotator annotator = new CleanXmlAnnotator();

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("Final thoughts.");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("</doc>");
        tokens.add(token2);

        List<CoreLabel> processedTokens = annotator.process(tokens);

        assertEquals(1, processedTokens.size());
        assertEquals("Final thoughts.", processedTokens.get(0).word());
    }
@Test
    public void testProcessHandlesTagsWithMultipleAttributes() {
        Properties properties = new Properties();
        properties.setProperty("clean.docAnnotations", "author=post[author],date=post[date]");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<post author='Alice' date='2023-06-10'>");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Content inside the post.");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("</post>");
        tokens.add(token3);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        assertEquals("Alice", annotation.get(CoreAnnotations.AuthorAnnotation.class));
        assertEquals("2023-06-10", annotation.get(CoreAnnotations.DocDateAnnotation.class));
    }
@Test
    public void testSentenceEndingTagWithoutContent() {
        Properties properties = new Properties();
        properties.setProperty("clean.sentenceendingtags", "p");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<p>");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("</p>");
        tokens.add(token2);

        List<CoreLabel> processedTokens = annotator.process(tokens);

        assertTrue(processedTokens.isEmpty());
    }
@Test
    public void testProcessHandlesMalformedAttributeSyntax() {
        Properties properties = new Properties();
        properties.setProperty("clean.allowflawedxml", "true");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<post author='Alice date='2023-06-10'>");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Content here.");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("</post>");
        tokens.add(token3);

        List<CoreLabel> processedTokens = annotator.process(tokens);

        assertEquals(1, processedTokens.size());
        assertEquals("Content here.", processedTokens.get(0).word());
    }
@Test
    public void testProcessHandlesUnescapedXmlCharacters() {
        CleanXmlAnnotator annotator = new CleanXmlAnnotator();

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("&lt;tag&gt;");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("text");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("&amp; symbol");
        tokens.add(token3);

        List<CoreLabel> processedTokens = annotator.process(tokens);

        assertEquals(2, processedTokens.size());
        assertEquals("text", processedTokens.get(0).word());
        assertEquals("& symbol", processedTokens.get(1).word());
    }
@Test
    public void testProcessHandlesMixedTextAndTags() {
        CleanXmlAnnotator annotator = new CleanXmlAnnotator();

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("Start text");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("<p>");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("inside tag");
        tokens.add(token3);

        CoreLabel token4 = new CoreLabel();
        token4.setWord("</p>");
        tokens.add(token4);

        CoreLabel token5 = new CoreLabel();
        token5.setWord("End text");
        tokens.add(token5);

        List<CoreLabel> processedTokens = annotator.process(tokens);

        assertEquals(3, processedTokens.size());
        assertEquals("Start text", processedTokens.get(0).word());
        assertEquals("inside tag", processedTokens.get(1).word());
        assertEquals("End text", processedTokens.get(2).word());
    }
@Test
    public void testProcessHandlesMultipleNestedDifferentTags() {
        CleanXmlAnnotator annotator = new CleanXmlAnnotator();

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<section>");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("<subsection>");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("Nested content");
        tokens.add(token3);

        CoreLabel token4 = new CoreLabel();
        token4.setWord("</subsection>");
        tokens.add(token4);

        CoreLabel token5 = new CoreLabel();
        token5.setWord("</section>");
        tokens.add(token5);

        List<CoreLabel> processedTokens = annotator.process(tokens);

        assertEquals(1, processedTokens.size());
        assertEquals("Nested content", processedTokens.get(0).word());
    }
@Test
    public void testProcessHandlesMultipleSequentialTagsWithContent() {
        CleanXmlAnnotator annotator = new CleanXmlAnnotator();

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<p>");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("First paragraph.");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("</p>");
        tokens.add(token3);

        CoreLabel token4 = new CoreLabel();
        token4.setWord("<p>");
        tokens.add(token4);

        CoreLabel token5 = new CoreLabel();
        token5.setWord("Second paragraph.");
        tokens.add(token5);

        CoreLabel token6 = new CoreLabel();
        token6.setWord("</p>");
        tokens.add(token6);

        List<CoreLabel> processedTokens = annotator.process(tokens);

        assertEquals(2, processedTokens.size());
        assertEquals("First paragraph.", processedTokens.get(0).word());
        assertEquals("Second paragraph.", processedTokens.get(1).word());
    }
@Test
    public void testProcessHandlesDocIDExtraction() {
        Properties properties = new Properties();
        properties.setProperty("clean.docIdtags", "docid");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<docid>");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("12345");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("</docid>");
        tokens.add(token3);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        assertEquals("12345", annotation.get(CoreAnnotations.DocIDAnnotation.class));
    }
@Test
    public void testProcessHandlesSentenceSplittingWithMixedTags() {
        Properties properties = new Properties();
        properties.setProperty("clean.sentenceendingtags", "p|div");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("First sentence.");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("<p>");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("Second sentence.");
        tokens.add(token3);

        CoreLabel token4 = new CoreLabel();
        token4.setWord("</p>");
        tokens.add(token4);

        CoreLabel token5 = new CoreLabel();
        token5.setWord("<div>");
        tokens.add(token5);

        CoreLabel token6 = new CoreLabel();
        token6.setWord("Third sentence.");
        tokens.add(token6);

        CoreLabel token7 = new CoreLabel();
        token7.setWord("</div>");
        tokens.add(token7);

        List<CoreLabel> processedTokens = annotator.process(tokens);

        assertEquals(3, processedTokens.size());
        assertTrue(processedTokens.get(0).containsKey(CoreAnnotations.ForcedSentenceEndAnnotation.class));
        assertTrue(processedTokens.get(1).containsKey(CoreAnnotations.ForcedSentenceEndAnnotation.class));
    }
@Test
    public void testProcessHandlesMalformedXmlWithoutFlawedXmlAllowed() {
        Properties properties = new Properties();
        properties.setProperty("clean.allowflawedxml", "false");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<p>");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Some content.");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("</div>");
        tokens.add(token3);

        try {
            annotator.process(tokens);
            fail("Expected IllegalArgumentException for mismatched tags.");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Mismatched tags"));
        }
    }
@Test
    public void testProcessHandlesSpeakerAnnotations() {
        Properties properties = new Properties();
        properties.setProperty("clean.speakertags", "speaker");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<speaker>");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Alice");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("</speaker>");
        tokens.add(token3);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        assertEquals(1, annotation.get(CoreAnnotations.TokensAnnotation.class).size());
        assertEquals("Alice", annotation.get(CoreAnnotations.TokensAnnotation.class).get(0).word());
        assertEquals("Alice", annotation.get(CoreAnnotations.TokensAnnotation.class).get(0).get(CoreAnnotations.SpeakerAnnotation.class));
    }
@Test
    public void testProcessHandlesMultipleLevelsOfQuotes() {
        Properties properties = new Properties();
        properties.setProperty("clean.quotetags", "quote");
        properties.setProperty("clean.quoteauthorattributes", "author");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<quote author='John'>");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("First quote level.");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("<quote author='Alice'>");
        tokens.add(token3);

        CoreLabel token4 = new CoreLabel();
        token4.setWord("Nested quote.");
        tokens.add(token4);

        CoreLabel token5 = new CoreLabel();
        token5.setWord("</quote>");
        tokens.add(token5);

        CoreLabel token6 = new CoreLabel();
        token6.setWord("</quote>");
        tokens.add(token6);

        List<CoreLabel> processedTokens = annotator.process(tokens);

        assertEquals(2, processedTokens.size());
        assertEquals("First quote level.", processedTokens.get(0).word());
        assertEquals("Nested quote.", processedTokens.get(1).word());
    }
@Test
    public void testProcessHandlesEmptyTagContent() {
        CleanXmlAnnotator annotator = new CleanXmlAnnotator();
        
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<p>");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("</p>");
        tokens.add(token2);

        List<CoreLabel> processedTokens = annotator.process(tokens);
        
        assertTrue(processedTokens.isEmpty());
    }
@Test
    public void testProcessHandlesMixedValidAndInvalidTags() {
        Properties properties = new Properties();
        properties.setProperty("clean.allowflawedxml", "true");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<valid>");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Content inside valid tag.");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("</invalid>");
        tokens.add(token3);

        List<CoreLabel> processedTokens = annotator.process(tokens);

        assertEquals(1, processedTokens.size());
        assertEquals("Content inside valid tag.", processedTokens.get(0).word());
    }
@Test
    public void testProcessHandlesSelfClosingTagBetweenText() {
        CleanXmlAnnotator annotator = new CleanXmlAnnotator();
        
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("First part.");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("<br/>");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("Second part.");
        tokens.add(token3);

        List<CoreLabel> processedTokens = annotator.process(tokens);
        
        assertEquals(2, processedTokens.size());
        assertEquals("First part.", processedTokens.get(0).word());
        assertEquals("Second part.", processedTokens.get(1).word());
    }
@Test
    public void testProcessHandlesGeneratedSectionAnnotations() {
        Properties properties = new Properties();
        properties.setProperty("clean.sectiontags", "section");
        properties.setProperty("clean.sectionannotations", "author=section[author]");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<section author='Bob'>");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Section content.");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("</section>");
        tokens.add(token3);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        List<CoreMap> sections = annotation.get(CoreAnnotations.SectionsAnnotation.class);
        assertNotNull(sections);
        assertEquals(1, sections.size());
        assertEquals("Bob", sections.get(0).get(CoreAnnotations.AuthorAnnotation.class));
    }
@Test
    public void testProcessHandlesQuoteTagWithoutAuthor() {
        Properties properties = new Properties();
        properties.setProperty("clean.quotetags", "quote");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<quote>");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Quoted text.");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("</quote>");
        tokens.add(token3);

        List<CoreLabel> processedTokens = annotator.process(tokens);

        assertEquals(1, processedTokens.size());
        assertEquals("Quoted text.", processedTokens.get(0).word());
    }
@Test
    public void testProcessHandlesDocumentDateExtraction() {
        Properties properties = new Properties();
        properties.setProperty("clean.datetags", "date");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<date>");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("2024-06-01");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("</date>");
        tokens.add(token3);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        assertEquals("2024-06-01", annotation.get(CoreAnnotations.DocDateAnnotation.class));
    }
@Test
    public void testProcessHandlesMalformedTagName() {
        Properties properties = new Properties();
        properties.setProperty("clean.allowflawedxml", "true");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<op3n>");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Malformed tag text.");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("</op3n>");
        tokens.add(token3);

        List<CoreLabel> processedTokens = annotator.process(tokens);

        assertEquals(1, processedTokens.size());
        assertEquals("Malformed tag text.", processedTokens.get(0).word());
    }
@Test
    public void testProcessHandlesEscapedHtmlEntities() {
        CleanXmlAnnotator annotator = new CleanXmlAnnotator();
        
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("This &amp; that.");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("5 &lt; 10.");
        tokens.add(token2);

        List<CoreLabel> processedTokens = annotator.process(tokens);

        assertEquals(2, processedTokens.size());
        assertEquals("This & that.", processedTokens.get(0).word());
        assertEquals("5 < 10.", processedTokens.get(1).word());
    }
@Test
    public void testProcessHandlesUnmatchedClosingTagGracefully() {
        Properties properties = new Properties();
        properties.setProperty("clean.allowflawedxml", "true");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("Valid text.");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("</unmatched>");
        tokens.add(token2);

        List<CoreLabel> processedTokens = annotator.process(tokens);

        assertEquals(1, processedTokens.size());
        assertEquals("Valid text.", processedTokens.get(0).word());
    }
@Test
    public void testProcessHandlesMixedSentenceEndingAndSingleSentenceTags() {
        Properties properties = new Properties();
        properties.setProperty("clean.sentenceendingtags", "p");
        properties.setProperty("clean.singlesentencetags", "post");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<post>");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Post content.");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("<p>");
        tokens.add(token3);

        CoreLabel token4 = new CoreLabel();
        token4.setWord("This should be sentence-ending.");
        tokens.add(token4);

        CoreLabel token5 = new CoreLabel();
        token5.setWord("</p>");
        tokens.add(token5);

        CoreLabel token6 = new CoreLabel();
        token6.setWord("</post>");
        tokens.add(token6);

        List<CoreLabel> processedTokens = annotator.process(tokens);

        assertEquals(2, processedTokens.size());
        assertTrue(processedTokens.get(0).containsKey(CoreAnnotations.ForcedSentenceUntilEndAnnotation.class));
        assertTrue(processedTokens.get(1).containsKey(CoreAnnotations.ForcedSentenceEndAnnotation.class));
    }
@Test
    public void testProcessHandlesMultipleDiscontinuousTags() {
        CleanXmlAnnotator annotator = new CleanXmlAnnotator();
        
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<tag1>");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Content 1.");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("</tag1>");
        tokens.add(token3);

        CoreLabel token4 = new CoreLabel();
        token4.setWord("<tag2>");
        tokens.add(token4);

        CoreLabel token5 = new CoreLabel();
        token5.setWord("Content 2.");
        tokens.add(token5);

        CoreLabel token6 = new CoreLabel();
        token6.setWord("</tag2>");
        tokens.add(token6);

        List<CoreLabel> processedTokens = annotator.process(tokens);
        
        assertEquals(2, processedTokens.size());
        assertEquals("Content 1.", processedTokens.get(0).word());
        assertEquals("Content 2.", processedTokens.get(1).word());
    }
@Test
    public void testProcessHandlesImproperlyNestedTagsGracefully() {
        Properties properties = new Properties();
        properties.setProperty("clean.allowflawedxml", "true");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        List<CoreLabel> tokens = new ArrayList<>();
        
        CoreLabel token1 = new CoreLabel();
        token1.setWord("<outer>");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("<inner>");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("Text inside.");
        tokens.add(token3);

        CoreLabel token4 = new CoreLabel();
        token4.setWord("</outer>");
        tokens.add(token4);

        CoreLabel token5 = new CoreLabel();
        token5.setWord("</inner>");
        tokens.add(token5);

        List<CoreLabel> processedTokens = annotator.process(tokens);
        
        assertEquals(1, processedTokens.size());
        assertEquals("Text inside.", processedTokens.get(0).word());
    }
@Test
    public void testProcessHandlesSingleTokenInsideTag() {
        CleanXmlAnnotator annotator = new CleanXmlAnnotator();
        
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<tag>");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Word");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("</tag>");
        tokens.add(token3);

        List<CoreLabel> processedTokens = annotator.process(tokens);
        
        assertEquals(1, processedTokens.size());
        assertEquals("Word", processedTokens.get(0).word());
    }
@Test
    public void testProcessHandlesConsecutiveTagsWithoutContent() {
        CleanXmlAnnotator annotator = new CleanXmlAnnotator();
        
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<tag1>");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("</tag1>");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("<tag2>");
        tokens.add(token3);

        CoreLabel token4 = new CoreLabel();
        token4.setWord("</tag2>");
        tokens.add(token4);

        List<CoreLabel> processedTokens = annotator.process(tokens);
        
        assertTrue(processedTokens.isEmpty());
    }
@Test
    public void testProcessHandlesQuoteTagContainingPunctuation() {
        Properties properties = new Properties();
        properties.setProperty("clean.quotetags", "quote");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<quote>");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("\"Quoted text with punctuation.\"");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("</quote>");
        tokens.add(token3);

        List<CoreLabel> processedTokens = annotator.process(tokens);
        
        assertEquals(1, processedTokens.size());
        assertEquals("\"Quoted text with punctuation.\"", processedTokens.get(0).word());
    }
@Test
    public void testProcessHandlesUnmatchedOpeningTagGracefully() {
        Properties properties = new Properties();
        properties.setProperty("clean.allowflawedxml", "true");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<unmatched>");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Valid content.");
        tokens.add(token2);

        List<CoreLabel> processedTokens = annotator.process(tokens);
        
        assertEquals(1, processedTokens.size());
        assertEquals("Valid content.", processedTokens.get(0).word());
    }
@Test
    public void testProcessHandlesMixOfTextAndStandaloneTags() {
        CleanXmlAnnotator annotator = new CleanXmlAnnotator();
        
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("Introduction.");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("<section>");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("Inside section.");
        tokens.add(token3);

        CoreLabel token4 = new CoreLabel();
        token4.setWord("<br/>");
        tokens.add(token4);

        CoreLabel token5 = new CoreLabel();
        token5.setWord("More content.");
        tokens.add(token5);

        CoreLabel token6 = new CoreLabel();
        token6.setWord("</section>");
        tokens.add(token6);

        List<CoreLabel> processedTokens = annotator.process(tokens);
        
        assertEquals(3, processedTokens.size());
        assertEquals("Introduction.", processedTokens.get(0).word());
        assertEquals("Inside section.", processedTokens.get(1).word());
        assertEquals("More content.", processedTokens.get(2).word());
    }
@Test
    public void testProcessHandlesMultipleLevelsOfSectionTags() {
        Properties properties = new Properties();
        properties.setProperty("clean.sectiontags", "post|comment");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<post>");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Post content.");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("<comment>");
        tokens.add(token3);

        CoreLabel token4 = new CoreLabel();
        token4.setWord("Comment content.");
        tokens.add(token4);

        CoreLabel token5 = new CoreLabel();
        token5.setWord("</comment>");
        tokens.add(token5);

        CoreLabel token6 = new CoreLabel();
        token6.setWord("</post>");
        tokens.add(token6);

        List<CoreLabel> processedTokens = annotator.process(tokens);
        
        assertEquals(2, processedTokens.size());
        assertEquals("Post content.", processedTokens.get(0).word());
        assertEquals("Comment content.", processedTokens.get(1).word());
    } 
}