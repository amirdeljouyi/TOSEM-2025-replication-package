package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;
import org.junit.Test;

import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class CleanXmlAnnotator_2_GPTLLMTest {

 @Test
    public void testConstructorWithDefaultValues() {
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();
        assertNotNull(cleanXmlAnnotator);
    }
@Test
    public void testToCaseInsensitivePattern() {
        Pattern pattern = Pattern.compile("test", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        assertTrue(pattern.matcher("TeSt").matches());
    }
@Test
    public void testSetSingleSentenceTagMatcher() {
        Properties properties = new Properties();
        properties.setProperty("clean.singlesentencetags", "strong");
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);
        assertNotNull(cleanXmlAnnotator);
    }
@Test
    public void testSetDocIdTagMatcher() {
        Properties properties = new Properties();
        properties.setProperty("clean.docIdtags", "docid");
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);
        assertNotNull(cleanXmlAnnotator);
    }
@Test
    public void testSetSectionTagMatcher() {
        Properties properties = new Properties();
        properties.setProperty("clean.sectiontags", "section");
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);
        assertNotNull(cleanXmlAnnotator);
    }
@Test
    public void testAnnotate() {
        Properties properties = new Properties();
        properties.setProperty("clean.xmltags", ".*");
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("<p>This is a test</p>");
        cleanXmlAnnotator.annotate(annotation);

        List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertNotNull(tokens);
        assertFalse(tokens.isEmpty());
    }
@Test
    public void testProcessXmlRemoval() {
        Properties properties = new Properties();
        properties.setProperty("clean.xmltags", ".*");
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<title>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Important");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("News");

        CoreLabel token4 = new CoreLabel();
        token4.setWord("</title>");

        List<CoreLabel> tokens = List.of(token1, token2, token3, token4);
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

        assertEquals(2, processedTokens.size());
        assertEquals("Important", processedTokens.get(0).word());
        assertEquals("News", processedTokens.get(1).word());
    }
@Test
    public void testProcessSentenceEndingTag() {
        Properties properties = new Properties();
        properties.setProperty("clean.sentenceendingtags", "p");
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);

        CoreLabel token1 = new CoreLabel();
        token1.setWord("Hello");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("<p>");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("world");

        CoreLabel token4 = new CoreLabel();
        token4.setWord("</p>");

        List<CoreLabel> tokens = List.of(token1, token2, token3, token4);
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

        assertEquals(2, processedTokens.size());
        assertTrue(processedTokens.get(0).get(CoreAnnotations.ForcedSentenceEndAnnotation.class));
    }
@Test(expected = IllegalArgumentException.class)
    public void testInvalidAnnotationPatterns() {
        Properties properties = new Properties();
        properties.setProperty("clean.docAnnotations", "invalidPattern");
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);
        cleanXmlAnnotator.setDocAnnotationPatterns("invalidPattern");
    }
@Test
    public void testAllowFlawedXml() {
        Properties properties = new Properties();
        properties.setProperty("clean.allowflawedxml", "true");
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);
        assertNotNull(cleanXmlAnnotator);
    }
@Test
    public void testEmptyInputShouldReturnEmptyList() {
        Properties properties = new Properties();
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);
        
        List<CoreLabel> tokens = List.of();
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);
        
        assertTrue(processedTokens.isEmpty());
    }
@Test
    public void testOnlyXmlTagsShouldReturnEmptyList() {
        Properties properties = new Properties();
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);
        
        CoreLabel token1 = new CoreLabel();
        token1.setWord("<tag1>");
        
        CoreLabel token2 = new CoreLabel();
        token2.setWord("</tag1>");
        
        List<CoreLabel> tokens = List.of(token1, token2);
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);
        
        assertTrue(processedTokens.isEmpty());
    }
@Test
    public void testMixedContentShouldKeepNonXmlTokens() {
        Properties properties = new Properties();
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<title>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Hello");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("</title>");

        CoreLabel token4 = new CoreLabel();
        token4.setWord("World");

        List<CoreLabel> tokens = List.of(token1, token2, token3, token4);
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

        assertEquals(2, processedTokens.size());
        assertEquals("Hello", processedTokens.get(0).word());
        assertEquals("World", processedTokens.get(1).word());
    }
@Test
    public void testNestedXmlTagsShouldRemoveTagsButKeepText() {
        Properties properties = new Properties();
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<outer>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("<inner>");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("content");

        CoreLabel token4 = new CoreLabel();
        token4.setWord("</inner>");

        CoreLabel token5 = new CoreLabel();
        token5.setWord("</outer>");

        List<CoreLabel> tokens = List.of(token1, token2, token3, token4, token5);
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

        assertEquals(1, processedTokens.size());
        assertEquals("content", processedTokens.get(0).word());
    }
@Test(expected = IllegalArgumentException.class)
    public void testMismatchedClosingTagsShouldThrowExceptionIfFlawedXmlNotAllowed() {
        Properties properties = new Properties();
        properties.setProperty("clean.allowflawedxml", "false");
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<tag1>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("</wrongTag>");

        List<CoreLabel> tokens = List.of(token1, token2);
        cleanXmlAnnotator.process(tokens);
    }
@Test
    public void testUnclosedTagsShouldProceedWithFlawedXmlAllowed() {
        Properties properties = new Properties();
        properties.setProperty("clean.allowflawedxml", "true");
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<tag1>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("content");

        List<CoreLabel> tokens = List.of(token1, token2);
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

        assertEquals(1, processedTokens.size());
        assertEquals("content", processedTokens.get(0).word());
    }
@Test
    public void testQuoteAnnotationShouldCaptureCorrectly() {
        Properties properties = new Properties();
        properties.setProperty("clean.quotetags", "quote");
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<quote>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("This");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("is");

        CoreLabel token4 = new CoreLabel();
        token4.setWord("quoted");

        CoreLabel token5 = new CoreLabel();
        token5.setWord("</quote>");

        List<CoreLabel> tokens = List.of(token1, token2, token3, token4, token5);
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

        assertEquals(3, processedTokens.size());
        assertEquals("This", processedTokens.get(0).word());
        assertEquals("is", processedTokens.get(1).word());
        assertEquals("quoted", processedTokens.get(2).word());
    }
@Test
    public void testEmptyPropertiesDefaultBehavior() {
        Properties properties = new Properties();
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);
        
        assertNotNull(cleanXmlAnnotator);
    }
@Test
    public void testSentenceEndingTagShouldMarkLastToken() {
        Properties properties = new Properties();
        properties.setProperty("clean.sentenceendingtags", "div");
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);

        CoreLabel token1 = new CoreLabel();
        token1.setWord("Hello");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("<div>");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("World");

        CoreLabel token4 = new CoreLabel();
        token4.setWord("</div>");

        List<CoreLabel> tokens = List.of(token1, token2, token3, token4);
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

        assertEquals(2, processedTokens.size());
        assertTrue(processedTokens.get(0).get(CoreAnnotations.ForcedSentenceEndAnnotation.class));
    }
@Test
    public void testProcessingAttributesInsideXmlTagsShouldIgnoreThem() {
        Properties properties = new Properties();
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<tag attr='value'>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Hello");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("</tag>");

        List<CoreLabel> tokens = List.of(token1, token2, token3);
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

        assertEquals(1, processedTokens.size());
        assertEquals("Hello", processedTokens.get(0).word());
    }
@Test
    public void testMultipleXmlTagsWithInterleavedText() {
        Properties properties = new Properties();
        properties.setProperty("clean.xmltags", ".*");
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);

        CoreLabel token1 = new CoreLabel();
        token1.setWord("Hello");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("<tag1>");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("World");

        CoreLabel token4 = new CoreLabel();
        token4.setWord("</tag1>");

        CoreLabel token5 = new CoreLabel();
        token5.setWord("<p>");

        CoreLabel token6 = new CoreLabel();
        token6.setWord("!");

        CoreLabel token7 = new CoreLabel();
        token7.setWord("</p>");

        List<CoreLabel> tokens = List.of(token1, token2, token3, token4, token5, token6, token7);
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

        assertEquals(3, processedTokens.size());
        assertEquals("Hello", processedTokens.get(0).word());
        assertEquals("World", processedTokens.get(1).word());
        assertEquals("!", processedTokens.get(2).word());
    }
@Test
    public void testEmptyTagShouldBeIgnored() {
        Properties properties = new Properties();
        properties.setProperty("clean.xmltags", ".*");
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<emptyTag/>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Hello");

        List<CoreLabel> tokens = List.of(token1, token2);
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

        assertEquals(1, processedTokens.size());
        assertEquals("Hello", processedTokens.get(0).word());
    }
@Test
    public void testMultipleLevelsOfNestingXml() {
        Properties properties = new Properties();
        properties.setProperty("clean.xmltags", ".*");
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<outer>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("<middle>");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("<inner>");

        CoreLabel token4 = new CoreLabel();
        token4.setWord("Text");

        CoreLabel token5 = new CoreLabel();
        token5.setWord("</inner>");

        CoreLabel token6 = new CoreLabel();
        token6.setWord("</middle>");

        CoreLabel token7 = new CoreLabel();
        token7.setWord("</outer>");

        List<CoreLabel> tokens = List.of(token1, token2, token3, token4, token5, token6, token7);
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

        assertEquals(1, processedTokens.size());
        assertEquals("Text", processedTokens.get(0).word());
    }
@Test
    public void testSentenceEndingTagAtStartShouldNotAffectSentence() {
        Properties properties = new Properties();
        properties.setProperty("clean.sentenceendingtags", "p");
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<p>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Hello");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("World");

        List<CoreLabel> tokens = List.of(token1, token2, token3);
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

        assertEquals(2, processedTokens.size());
        assertEquals("Hello", processedTokens.get(0).word());
        assertEquals("World", processedTokens.get(1).word());
        assertNull(processedTokens.get(0).get(CoreAnnotations.ForcedSentenceEndAnnotation.class));
    }
@Test
    public void testCustomQuoteParsingShouldCaptureProperly() {
        Properties properties = new Properties();
        properties.setProperty("clean.quotetags", "quote");
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<quote>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("This");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("is");

        CoreLabel token4 = new CoreLabel();
        token4.setWord("quoted");

        CoreLabel token5 = new CoreLabel();
        token5.setWord("</quote>");

        List<CoreLabel> tokens = List.of(token1, token2, token3, token4, token5);
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

        assertEquals(3, processedTokens.size());
        assertEquals("This", processedTokens.get(0).word());
        assertEquals("is", processedTokens.get(1).word());
        assertEquals("quoted", processedTokens.get(2).word());
    }
@Test(expected = IllegalArgumentException.class)
    public void testInvalidAnnotationPatternThrowsException() {
        Properties properties = new Properties();
        properties.setProperty("clean.docAnnotations", "invalidPattern");
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);
        cleanXmlAnnotator.setDocAnnotationPatterns("invalidPattern");
    }
@Test
    public void testTextOutsideTagsShouldBeUnchanged() {
        Properties properties = new Properties();
        properties.setProperty("clean.xmltags", ".*");
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);

        CoreLabel token1 = new CoreLabel();
        token1.setWord("Text1");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("<tag>");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("Text2");

        CoreLabel token4 = new CoreLabel();
        token4.setWord("</tag>");

        CoreLabel token5 = new CoreLabel();
        token5.setWord("Text3");

        List<CoreLabel> tokens = List.of(token1, token2, token3, token4, token5);
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

        assertEquals(3, processedTokens.size());
        assertEquals("Text1", processedTokens.get(0).word());
        assertEquals("Text2", processedTokens.get(1).word());
        assertEquals("Text3", processedTokens.get(2).word());
    }
@Test
    public void testSectionAnnotationPatternsAppliedCorrectly() {
        Properties properties = new Properties();
        properties.setProperty("clean.sectiontags", "section");
        properties.setProperty("clean.sectionAnnotations", "author=authorName");
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<section>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("<authorName>");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("John Doe");

        CoreLabel token4 = new CoreLabel();
        token4.setWord("</authorName>");

        CoreLabel token5 = new CoreLabel();
        token5.setWord("Text in section");

        CoreLabel token6 = new CoreLabel();
        token6.setWord("</section>");

        List<CoreLabel> tokens = List.of(token1, token2, token3, token4, token5, token6);
        Annotation annotation = new Annotation("");
        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

        cleanXmlAnnotator.annotate(annotation);
        List<CoreMap> sections = annotation.get(CoreAnnotations.SectionsAnnotation.class);

        assertNotNull(sections);
        assertEquals(1, sections.size());
        CoreMap section = sections.get(0);
        assertEquals("John Doe", section.get(CoreAnnotations.AuthorAnnotation.class));
    }
@Test
    public void testSelfClosingTagShouldBeIgnored() {
        Properties properties = new Properties();
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<br/>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Hello");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("World");

        List<CoreLabel> tokens = List.of(token1, token2, token3);
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

        assertEquals(2, processedTokens.size());
        assertEquals("Hello", processedTokens.get(0).word());
        assertEquals("World", processedTokens.get(1).word());
    }
@Test
    public void testMalformedXmlWithMissingClosingTagShouldBeHandledGracefully() {
        Properties properties = new Properties();
        properties.setProperty("clean.allowflawedxml", "true");
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<p>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Unclosed");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("Tag");

        List<CoreLabel> tokens = List.of(token1, token2, token3);
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

        assertEquals(2, processedTokens.size());
        assertEquals("Unclosed", processedTokens.get(0).word());
        assertEquals("Tag", processedTokens.get(1).word());
    }
@Test(expected = IllegalArgumentException.class)
    public void testMismatchedTagsWithStrictXmlShouldThrowException() {
        Properties properties = new Properties();
        properties.setProperty("clean.allowflawedxml", "false");
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<div>");
        
        CoreLabel token2 = new CoreLabel();
        token2.setWord("Some text");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("</span>"); 

        List<CoreLabel> tokens = List.of(token1, token2, token3);
        cleanXmlAnnotator.process(tokens);
    }
@Test
    public void testSentenceEndingTagForcesSentenceEnd() {
        Properties properties = new Properties();
        properties.setProperty("clean.sentenceendingtags", "p");
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);

        CoreLabel token1 = new CoreLabel();
        token1.setWord("Sentence");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("<p>");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("Another");

        CoreLabel token4 = new CoreLabel();
        token4.setWord("sentence");

        CoreLabel token5 = new CoreLabel();
        token5.setWord("</p>");

        List<CoreLabel> tokens = List.of(token1, token2, token3, token4, token5);
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

        assertEquals(3, processedTokens.size());
        assertTrue(processedTokens.get(0).get(CoreAnnotations.ForcedSentenceEndAnnotation.class));
    }
@Test
    public void testTokenAnnotationsAreSetCorrectly() {
        Properties properties = new Properties();
        properties.setProperty("clean.tokenAnnotations", "topic=section");
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<section>");
        CoreLabel token2 = new CoreLabel();
        token2.setWord("Machine");
        CoreLabel token3 = new CoreLabel();
        token3.setWord("Learning");
        CoreLabel token4 = new CoreLabel();
        token4.setWord("</section>");

        List<CoreLabel> tokens = List.of(token1, token2, token3, token4);

        Annotation annotation = new Annotation("");
        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

        cleanXmlAnnotator.annotate(annotation);

        List<CoreLabel> processedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

        assertEquals(2, processedTokens.size());
        assertEquals("Machine", processedTokens.get(0).word());
        assertEquals("Learning", processedTokens.get(1).word());
    }
@Test
    public void testUnmatchedSingleTagsShouldBeIgnored() {
        Properties properties = new Properties();
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<img>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Text");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("Here");

        List<CoreLabel> tokens = List.of(token1, token2, token3);
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

        assertEquals(2, processedTokens.size());
        assertEquals("Text", processedTokens.get(0).word());
        assertEquals("Here", processedTokens.get(1).word());
    }
@Test
    public void testRemovingTagsWithNestedContent() {
        Properties properties = new Properties();
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<outer>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("<inner>");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("Content");

        CoreLabel token4 = new CoreLabel();
        token4.setWord("</inner>");

        CoreLabel token5 = new CoreLabel();
        token5.setWord("</outer>");

        List<CoreLabel> tokens = List.of(token1, token2, token3, token4, token5);
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

        assertEquals(1, processedTokens.size());
        assertEquals("Content", processedTokens.get(0).word());
    }
@Test
    public void testProperHandlingOfAttributesInsideTags() {
        Properties properties = new Properties();
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<meta name='author'>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("John");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("Doe");

        CoreLabel token4 = new CoreLabel();
        token4.setWord("</meta>");

        List<CoreLabel> tokens = List.of(token1, token2, token3, token4);
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

        assertEquals(2, processedTokens.size());
        assertEquals("John", processedTokens.get(0).word());
        assertEquals("Doe", processedTokens.get(1).word());
    }
@Test
    public void testProcessingTextWithoutTagsShouldRemainUnchanged() {
        Properties properties = new Properties();
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);
        
        CoreLabel token1 = new CoreLabel();
        token1.setWord("Hello");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("World");

        List<CoreLabel> tokens = List.of(token1, token2);
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

        assertEquals(2, processedTokens.size());
        assertEquals("Hello", processedTokens.get(0).word());
        assertEquals("World", processedTokens.get(1).word());
    }
@Test
    public void testXmlTagAtStartShouldBeIgnoredAndKeepText() {
        Properties properties = new Properties();
        properties.setProperty("clean.xmltags", ".*");
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);
        
        CoreLabel token1 = new CoreLabel();
        token1.setWord("<header>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Important");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("</header>");

        CoreLabel token4 = new CoreLabel();
        token4.setWord("Message");

        List<CoreLabel> tokens = List.of(token1, token2, token3, token4);
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

        assertEquals(2, processedTokens.size());
        assertEquals("Important", processedTokens.get(0).word());
        assertEquals("Message", processedTokens.get(1).word());
    }
@Test
    public void testMixedValidAndInvalidXmlTagsShouldRetainValidText() {
        Properties properties = new Properties();
        properties.setProperty("clean.allowflawedxml", "true");
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);
        
        CoreLabel token1 = new CoreLabel();
        token1.setWord("<valid>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("ValidText");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("</invalid>"); 

        CoreLabel token4 = new CoreLabel();
        token4.setWord("RemainingText");

        List<CoreLabel> tokens = List.of(token1, token2, token3, token4);
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

        assertEquals(2, processedTokens.size());
        assertEquals("ValidText", processedTokens.get(0).word());
        assertEquals("RemainingText", processedTokens.get(1).word());
    }
@Test
    public void testMalformedXmlTagShouldNotCrashProcessing() {
        Properties properties = new Properties();
        properties.setProperty("clean.allowflawedxml", "true");
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);
        
        CoreLabel token1 = new CoreLabel();
        token1.setWord("<open");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Text");

        CoreLabel token3 = new CoreLabel();
        token3.setWord(">");

        CoreLabel token4 = new CoreLabel();
        token4.setWord("MoreText");

        List<CoreLabel> tokens = List.of(token1, token2, token3, token4);
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

        assertEquals(2, processedTokens.size());
        assertEquals("Text", processedTokens.get(0).word());
        assertEquals("MoreText", processedTokens.get(1).word());
    }
@Test
    public void testXmlTagsInsideSentenceShouldOnlyRemoveTags() {
        Properties properties = new Properties();
        properties.setProperty("clean.xmltags", "strong");
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);
        
        CoreLabel token1 = new CoreLabel();
        token1.setWord("This");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("<strong>");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("is");

        CoreLabel token4 = new CoreLabel();
        token4.setWord("</strong>");

        CoreLabel token5 = new CoreLabel();
        token5.setWord("bold");

        List<CoreLabel> tokens = List.of(token1, token2, token3, token4, token5);
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

        assertEquals(3, processedTokens.size());
        assertEquals("This", processedTokens.get(0).word());
        assertEquals("is", processedTokens.get(1).word());
        assertEquals("bold", processedTokens.get(2).word());
    }
@Test
    public void testMultipleNestedXmlTagsShouldOnlyExtractText() {
        Properties properties = new Properties();
        properties.setProperty("clean.xmltags", ".*");
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);
        
        CoreLabel token1 = new CoreLabel();
        token1.setWord("<outer>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("<inner>");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("TextContent");

        CoreLabel token4 = new CoreLabel();
        token4.setWord("</inner>");

        CoreLabel token5 = new CoreLabel();
        token5.setWord("</outer>");

        List<CoreLabel> tokens = List.of(token1, token2, token3, token4, token5);
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

        assertEquals(1, processedTokens.size());
        assertEquals("TextContent", processedTokens.get(0).word());
    }
@Test
    public void testSingleTagWithNoClosingTagShouldIgnoreTag() {
        Properties properties = new Properties();
        properties.setProperty("clean.allowflawedxml", "true");
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);
        
        CoreLabel token1 = new CoreLabel();
        token1.setWord("<img>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Some text");

        List<CoreLabel> tokens = List.of(token1, token2);
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

        assertEquals(1, processedTokens.size());
        assertEquals("Some text", processedTokens.get(0).word());
    }
@Test
    public void testValidXmlWithNoMatchingCloseShouldStillProcessText() {
        Properties properties = new Properties();
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);
        
        CoreLabel token1 = new CoreLabel();
        token1.setWord("<note>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Reminder");

        List<CoreLabel> tokens = List.of(token1, token2);
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

        assertEquals(1, processedTokens.size());
        assertEquals("Reminder", processedTokens.get(0).word());
    }
@Test
    public void testEmptyStringShouldReturnEmptyList() {
        Properties properties = new Properties();
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);

        List<CoreLabel> tokens = List.of();
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

        assertTrue(processedTokens.isEmpty());
    }
@Test
    public void testOnlyOpeningTagWithoutClosingTagShouldIgnoreTag() {
        Properties properties = new Properties();
        properties.setProperty("clean.allowflawedxml", "true");
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<title>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Sample");

        List<CoreLabel> tokens = List.of(token1, token2);
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

        assertEquals(1, processedTokens.size());
        assertEquals("Sample", processedTokens.get(0).word());
    }
@Test(expected = IllegalArgumentException.class)
    public void testMismatchedOpeningAndClosingXmlTagsShouldThrowException() {
        Properties properties = new Properties();
        properties.setProperty("clean.allowflawedxml", "false");
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<div>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Text");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("</span>"); 

        List<CoreLabel> tokens = List.of(token1, token2, token3);
        cleanXmlAnnotator.process(tokens);
    }
@Test
    public void testSelfClosingXmlTagsShouldBeIgnored() {
        Properties properties = new Properties();
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<br/>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Hello");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("World");

        List<CoreLabel> tokens = List.of(token1, token2, token3);
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

        assertEquals(2, processedTokens.size());
        assertEquals("Hello", processedTokens.get(0).word());
        assertEquals("World", processedTokens.get(1).word());
    }
@Test
    public void testTagsWithAttributesShouldOnlyRemoveTags() {
        Properties properties = new Properties();
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<meta name='author'>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Jane");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("Doe");

        CoreLabel token4 = new CoreLabel();
        token4.setWord("</meta>");

        List<CoreLabel> tokens = List.of(token1, token2, token3, token4);
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

        assertEquals(2, processedTokens.size());
        assertEquals("Jane", processedTokens.get(0).word());
        assertEquals("Doe", processedTokens.get(1).word());
    }
@Test
    public void testDeeplyNestedXmlShouldOnlyKeepText() {
        Properties properties = new Properties();
        properties.setProperty("clean.xmltags", ".*");
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<outer>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("<middle>");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("<inner>");

        CoreLabel token4 = new CoreLabel();
        token4.setWord("Useful Content");

        CoreLabel token5 = new CoreLabel();
        token5.setWord("</inner>");

        CoreLabel token6 = new CoreLabel();
        token6.setWord("</middle>");

        CoreLabel token7 = new CoreLabel();
        token7.setWord("</outer>");

        List<CoreLabel> tokens = List.of(token1, token2, token3, token4, token5, token6, token7);
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

        assertEquals(1, processedTokens.size());
        assertEquals("Useful Content", processedTokens.get(0).word());
    }
@Test
    public void testXmlTagAroundSentenceShouldOnlyRemoveTags() {
        Properties properties = new Properties();
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<strong>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Important");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("Message");

        CoreLabel token4 = new CoreLabel();
        token4.setWord("</strong>");

        List<CoreLabel> tokens = List.of(token1, token2, token3, token4);
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

        assertEquals(2, processedTokens.size());
        assertEquals("Important", processedTokens.get(0).word());
        assertEquals("Message", processedTokens.get(1).word());
    }
@Test
    public void testValidXmlWithoutEndingTagShouldStillProcessText() {
        Properties properties = new Properties();
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<note>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Reminder");

        List<CoreLabel> tokens = List.of(token1, token2);
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

        assertEquals(1, processedTokens.size());
        assertEquals("Reminder", processedTokens.get(0).word());
    }
@Test
    public void testRedundantTagsShouldBeFullyRemoved() {
        Properties properties = new Properties();
        properties.setProperty("clean.xmltags", ".*");
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<a>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("<b>");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("Content");

        CoreLabel token4 = new CoreLabel();
        token4.setWord("</b>");

        CoreLabel token5 = new CoreLabel();
        token5.setWord("</a>");

        List<CoreLabel> tokens = List.of(token1, token2, token3, token4, token5);
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

        assertEquals(1, processedTokens.size());
        assertEquals("Content", processedTokens.get(0).word());
    }
@Test
    public void testXmlTagsAroundSentenceShouldRemoveTagsOnly() {
        Properties properties = new Properties();
        properties.setProperty("clean.xmltags", "p");
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<p>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Hello");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("World");

        CoreLabel token4 = new CoreLabel();
        token4.setWord("</p>");

        List<CoreLabel> tokens = List.of(token1, token2, token3, token4);
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

        assertEquals(2, processedTokens.size());
        assertEquals("Hello", processedTokens.get(0).word());
        assertEquals("World", processedTokens.get(1).word());
    }
@Test
    public void testSentenceEndingTagMarksEndCorrectly() {
        Properties properties = new Properties();
        properties.setProperty("clean.sentenceendingtags", "p");
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);

        CoreLabel token1 = new CoreLabel();
        token1.setWord("Hello");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("<p>");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("World");

        CoreLabel token4 = new CoreLabel();
        token4.setWord("</p>");

        List<CoreLabel> tokens = List.of(token1, token2, token3, token4);
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

        assertEquals(2, processedTokens.size());
        assertTrue(processedTokens.get(0).get(CoreAnnotations.ForcedSentenceEndAnnotation.class));
    }
@Test
    public void testMalformedXmlTagDoesNotCrashProcessing() {
        Properties properties = new Properties();
        properties.setProperty("clean.allowflawedxml", "true");
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<unclosed");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("ValidContent");

        List<CoreLabel> tokens = List.of(token1, token2);
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

        assertEquals(1, processedTokens.size());
        assertEquals("ValidContent", processedTokens.get(0).word());
    }
@Test
    public void testSelfClosingTagsAreIgnored() {
        Properties properties = new Properties();
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<br/>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Text");

        List<CoreLabel> tokens = List.of(token1, token2);
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

        assertEquals(1, processedTokens.size());
        assertEquals("Text", processedTokens.get(0).word());
    }
@Test
    public void testMultipleNestedXmlTagsPreserveInnerContentOnly() {
        Properties properties = new Properties();
        properties.setProperty("clean.xmltags", ".*");
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<outer>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("<inner>");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("CoreText");

        CoreLabel token4 = new CoreLabel();
        token4.setWord("</inner>");

        CoreLabel token5 = new CoreLabel();
        token5.setWord("</outer>");

        List<CoreLabel> tokens = List.of(token1, token2, token3, token4, token5);
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

        assertEquals(1, processedTokens.size());
        assertEquals("CoreText", processedTokens.get(0).word());
    }
@Test
    public void testUnmatchedClosingTagHandledGracefully() {
        Properties properties = new Properties();
        properties.setProperty("clean.allowflawedxml", "true");
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);

        CoreLabel token1 = new CoreLabel();
        token1.setWord("</div>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("SurvivingText");

        List<CoreLabel> tokens = List.of(token1, token2);
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

        assertEquals(1, processedTokens.size());
        assertEquals("SurvivingText", processedTokens.get(0).word());
    }
@Test
    public void testUnclosedXmlTagStillPreservesText() {
        Properties properties = new Properties();
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<header>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("HeaderContent");

        List<CoreLabel> tokens = List.of(token1, token2);
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

        assertEquals(1, processedTokens.size());
        assertEquals("HeaderContent", processedTokens.get(0).word());
    }
@Test
    public void testUnmatchedOpeningTagIgnoredWithFlawedXml() {
        Properties properties = new Properties();
        properties.setProperty("clean.allowflawedxml", "true");
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(properties);

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<unmatched>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("ImportantText");

        List<CoreLabel> tokens = List.of(token1, token2);
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

        assertEquals(1, processedTokens.size());
        assertEquals("ImportantText", processedTokens.get(0).word());
    } 
}