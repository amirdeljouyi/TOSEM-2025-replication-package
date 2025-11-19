package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.*;

public class CleanXmlAnnotator_4_GPTLLMTest {

 @Test
    public void testTokenCleaning() {
        Properties properties = new Properties();
        properties.setProperty("clean.xmltags", ".*");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");
        CoreLabel token1 = new CoreLabel();
        token1.setWord("<xml>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("This");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("is");

        CoreLabel token4 = new CoreLabel();
        token4.setWord("text.");

        CoreLabel token5 = new CoreLabel();
        token5.setWord("</xml>");

        annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2, token3, token4, token5));

        annotator.annotate(annotation);

        List<CoreLabel> processedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertEquals(3, processedTokens.size());
        assertEquals("This", processedTokens.get(0).word());
        assertEquals("is", processedTokens.get(1).word());
        assertEquals("text.", processedTokens.get(2).word());
    }
@Test
    public void testSentenceEndingTags() {
        Properties properties = new Properties();
        properties.setProperty("clean.sentenceendingtags", "p|br");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");
        CoreLabel token1 = new CoreLabel();
        token1.setWord("Hello");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("world");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("<p>");

        CoreLabel token4 = new CoreLabel();
        token4.setWord("New");

        CoreLabel token5 = new CoreLabel();
        token5.setWord("sentence");

        CoreLabel token6 = new CoreLabel();
        token6.setWord("</p>");

        annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2, token3, token4, token5, token6));
        
        annotator.annotate(annotation);

        List<CoreLabel> processedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertEquals(4, processedTokens.size());
        assertTrue(processedTokens.get(1).containsKey(CoreAnnotations.ForcedSentenceEndAnnotation.class));
    }
@Test
    public void testFlawedXmlHandling() {
        Properties properties = new Properties();
        properties.setProperty("clean.allowflawedxml", "true");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");
        CoreLabel token1 = new CoreLabel();
        token1.setWord("<open>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Unclosed");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("tag");

        annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2, token3));

        annotator.annotate(annotation);

        List<CoreLabel> processedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertEquals(2, processedTokens.size());
        assertEquals("Unclosed", processedTokens.get(0).word());
        assertEquals("tag", processedTokens.get(1).word());
    }
@Test
    public void testDocumentLevelAnnotations() {
        Properties properties = new Properties();
        properties.setProperty("clean.datetags", "datetime|date");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");
        CoreLabel token1 = new CoreLabel();
        token1.setWord("<date>2024-06-12</date>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Some");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("content");

        annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2, token3));

        annotator.annotate(annotation);

        assertEquals("2024-06-12", annotation.get(CoreAnnotations.DocDateAnnotation.class));
    }
@Test
    public void testTokenAnnotationPatterns() {
        Properties properties = new Properties();
        properties.setProperty("clean.tokenAnnotations", "speaker=[name]");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");
        CoreLabel token1 = new CoreLabel();
        token1.setWord("<speaker name='John'>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Hello");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("there");

        CoreLabel token4 = new CoreLabel();
        token4.setWord("</speaker>");

        annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2, token3, token4));

        annotator.annotate(annotation);

        List<CoreLabel> processedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertEquals(2, processedTokens.size());
        assertEquals("John", processedTokens.get(0).get(CoreAnnotations.SpeakerAnnotation.class));
    }
@Test
    public void testProcessingOfNestedTags() {
        Properties properties = new Properties();
        properties.setProperty("clean.xmltags", ".*");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");
        CoreLabel token1 = new CoreLabel();
        token1.setWord("<outer>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Hello");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("<inner>");

        CoreLabel token4 = new CoreLabel();
        token4.setWord("Nested");

        CoreLabel token5 = new CoreLabel();
        token5.setWord("</inner>");

        CoreLabel token6 = new CoreLabel();
        token6.setWord("</outer>");

        CoreLabel token7 = new CoreLabel();
        token7.setWord("World");

        annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2, token3, token4, token5, token6, token7));

        annotator.annotate(annotation);

        List<CoreLabel> processedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertEquals(3, processedTokens.size());
        assertEquals("Hello", processedTokens.get(0).word());
        assertEquals("Nested", processedTokens.get(1).word());
        assertEquals("World", processedTokens.get(2).word());
    }
@Test
    public void testEmptyAnnotation() {
        Properties properties = new Properties();
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");
        annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList());

        annotator.annotate(annotation);

        List<CoreLabel> processedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertNotNull(processedTokens);
        assertTrue(processedTokens.isEmpty());
    }
@Test
    public void testAnnotationWithoutXmlTags() {
        Properties properties = new Properties();
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");
        CoreLabel token1 = new CoreLabel();
        token1.setWord("Hello");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("world!");

        annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));

        annotator.annotate(annotation);

        List<CoreLabel> processedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertEquals(2, processedTokens.size());
        assertEquals("Hello", processedTokens.get(0).word());
        assertEquals("world!", processedTokens.get(1).word());
    }
@Test
    public void testNestedUnmatchedTags() {
        Properties properties = new Properties();
        properties.setProperty("clean.allowflawedxml", "true");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");
        CoreLabel token1 = new CoreLabel();
        token1.setWord("<outer>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("text inside outer");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("<inner>");

        CoreLabel token4 = new CoreLabel();
        token4.setWord("Nested text");

        annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2, token3, token4));

        annotator.annotate(annotation);

        List<CoreLabel> processedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertEquals(2, processedTokens.size());
        assertEquals("text inside outer", processedTokens.get(0).word());
        assertEquals("Nested text", processedTokens.get(1).word());
    }
@Test
    public void testProcessingOnlyXmlTags() {
        Properties properties = new Properties();
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");
        CoreLabel token1 = new CoreLabel();
        token1.setWord("<html>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("<body>");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("</body>");

        CoreLabel token4 = new CoreLabel();
        token4.setWord("</html>");

        annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2, token3, token4));

        annotator.annotate(annotation);

        List<CoreLabel> processedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertTrue(processedTokens.isEmpty());
    }
@Test
    public void testMixedContentWithAttributes() {
        Properties properties = new Properties();
        properties.setProperty("clean.tokenAnnotations", "author=[creator]");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");
        CoreLabel token1 = new CoreLabel();
        token1.setWord("<meta creator='John Doe'>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Meaningful");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("content");

        CoreLabel token4 = new CoreLabel();
        token4.setWord("</meta>");

        annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2, token3, token4));

        annotator.annotate(annotation);

        List<CoreLabel> processedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertEquals(2, processedTokens.size());
        assertEquals("John Doe", processedTokens.get(0).get(CoreAnnotations.AuthorAnnotation.class));
        assertEquals("Meaningful", processedTokens.get(0).word());
        assertEquals("content", processedTokens.get(1).word());
    }
@Test
    public void testSingleTagProcessing() {
        Properties properties = new Properties();
        properties.setProperty("clean.xmltags", ".*");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");
        CoreLabel token1 = new CoreLabel();
        token1.setWord("Before");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("<br>");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("After");

        annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2, token3));

        annotator.annotate(annotation);

        List<CoreLabel> processedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertEquals(2, processedTokens.size());
        assertEquals("Before", processedTokens.get(0).word());
        assertEquals("After", processedTokens.get(1).word());
    }
@Test
    public void testMultipleSentenceBreaks() {
        Properties properties = new Properties();
        properties.setProperty("clean.sentenceendingtags", "div|p");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");
        CoreLabel token1 = new CoreLabel();
        token1.setWord("Text1");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("<div>");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("Text2");

        CoreLabel token4 = new CoreLabel();
        token4.setWord("</div>");

        CoreLabel token5 = new CoreLabel();
        token5.setWord("<p>");

        CoreLabel token6 = new CoreLabel();
        token6.setWord("Text3");

        CoreLabel token7 = new CoreLabel();
        token7.setWord("</p>");

        annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2, token3, token4, token5, token6, token7));

        annotator.annotate(annotation);

        List<CoreLabel> processedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertEquals(3, processedTokens.size());
        assertTrue(processedTokens.get(0).containsKey(CoreAnnotations.ForcedSentenceEndAnnotation.class));
        assertTrue(processedTokens.get(1).containsKey(CoreAnnotations.ForcedSentenceEndAnnotation.class));
    }
@Test
    public void testLongXmlContentRemovesTokensProperly() {
        Properties properties = new Properties();
        properties.setProperty("clean.xmltags", ".*");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");
        CoreLabel token1 = new CoreLabel();
        token1.setWord("<article>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("<header>Title</header>");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("Main");

        CoreLabel token4 = new CoreLabel();
        token4.setWord("text");

        CoreLabel token5 = new CoreLabel();
        token5.setWord("</article>");

        annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2, token3, token4, token5));

        annotator.annotate(annotation);

        List<CoreLabel> processedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertEquals(2, processedTokens.size());
        assertEquals("Main", processedTokens.get(0).word());
        assertEquals("text", processedTokens.get(1).word());
    }
@Test
    public void testProcessingWithOnlySelfClosingTags() {
        Properties properties = new Properties();
        properties.setProperty("clean.xmltags", ".*");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");
        CoreLabel token1 = new CoreLabel();
        token1.setWord("<br/>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("<img src='logo.png'/>");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("<meta charset='UTF-8'/>");

        annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2, token3));

        annotator.annotate(annotation);

        List<CoreLabel> processedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertTrue(processedTokens.isEmpty());
    }
@Test
    public void testInvalidXmlStructureHandling() {
        Properties properties = new Properties();
        properties.setProperty("clean.allowflawedxml", "false");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");
        CoreLabel token1 = new CoreLabel();
        token1.setWord("<b>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Bold text");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("</i>");

        annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2, token3));

        Exception thrownException = null;
        try {
            annotator.annotate(annotation);
        } catch (Exception e) {
            thrownException = e;
        }

        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalArgumentException);
    }
@Test
    public void testMultipleLevelsOfNesting() {
        Properties properties = new Properties();
        properties.setProperty("clean.xmltags", ".*");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");
        CoreLabel token1 = new CoreLabel();
        token1.setWord("<root>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("<level1>");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("<level2>");

        CoreLabel token4 = new CoreLabel();
        token4.setWord("Deep content");

        CoreLabel token5 = new CoreLabel();
        token5.setWord("</level2>");

        CoreLabel token6 = new CoreLabel();
        token6.setWord("</level1>");

        CoreLabel token7 = new CoreLabel();
        token7.setWord("</root>");

        annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2, token3, token4, token5, token6, token7));

        annotator.annotate(annotation);

        List<CoreLabel> processedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertEquals(1, processedTokens.size());
        assertEquals("Deep content", processedTokens.get(0).word());
    }
@Test
    public void testTagEnclosedTextExtraction() {
        Properties properties = new Properties();
        properties.setProperty("clean.xmltags", "p");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");
        CoreLabel token1 = new CoreLabel();
        token1.setWord("<p>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Extracted text");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("</p>");

        annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2, token3));

        annotator.annotate(annotation);

        List<CoreLabel> processedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertEquals(1, processedTokens.size());
        assertEquals("Extracted text", processedTokens.get(0).word());
    }
@Test
    public void testDocumentAnnotationExtractionWithMultipleAttributes() {
        Properties properties = new Properties();
        properties.setProperty("clean.docAnnotations", "docID=doc[id],doctype=doc[type]");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");
        CoreLabel token1 = new CoreLabel();
        token1.setWord("<doc id='12345' type='article'>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Relevant");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("text");

        CoreLabel token4 = new CoreLabel();
        token4.setWord("</doc>");

        annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2, token3, token4));

        annotator.annotate(annotation);

        assertEquals("12345", annotation.get(CoreAnnotations.DocIDAnnotation.class));
        assertEquals("article", annotation.get(CoreAnnotations.DocTypeAnnotation.class));
    }
@Test
    public void testSentenceSplittingWithSingleSentenceTags() {
        Properties properties = new Properties();
        properties.setProperty("clean.singlesentencetags", "quote");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");
        CoreLabel token1 = new CoreLabel();
        token1.setWord("This");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("is");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("<quote>");

        CoreLabel token4 = new CoreLabel();
        token4.setWord("a single sentence");

        CoreLabel token5 = new CoreLabel();
        token5.setWord("</quote>");

        CoreLabel token6 = new CoreLabel();
        token6.setWord("outside quote");

        annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2, token3, token4, token5, token6));

        annotator.annotate(annotation);

        List<CoreLabel> processedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertEquals(4, processedTokens.size());
        assertFalse(processedTokens.get(2).containsKey(CoreAnnotations.ForcedSentenceEndAnnotation.class));
        assertTrue(processedTokens.get(3).containsKey(CoreAnnotations.ForcedSentenceEndAnnotation.class));
    }
@Test
    public void testInvalidTagPatternHandling() {
        Properties properties = new Properties();
        properties.setProperty("clean.docAnnotations", "invalid=");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Exception thrownException = null;
        try {
            new CleanXmlAnnotator(properties);
        } catch (Exception e) {
            thrownException = e;
        }

        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalArgumentException);
    }
@Test
    public void testHandlingEmptyXmlTagsWithContent() {
        Properties properties = new Properties();
        properties.setProperty("clean.xmltags", ".*");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");
        CoreLabel token1 = new CoreLabel();
        token1.setWord("<empty></empty>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Valid content");

        annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));

        annotator.annotate(annotation);

        List<CoreLabel> processedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertEquals(1, processedTokens.size());
        assertEquals("Valid content", processedTokens.get(0).word());
    }
@Test
    public void testMalformedTagHandling() {
        Properties properties = new Properties();
        properties.setProperty("clean.allowflawedxml", "true");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");
        CoreLabel token1 = new CoreLabel();
        token1.setWord("<tag");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("incomplete");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("content");

        annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2, token3));

        annotator.annotate(annotation);

        List<CoreLabel> processedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertEquals(2, processedTokens.size());
        assertEquals("incomplete", processedTokens.get(0).word());
        assertEquals("content", processedTokens.get(1).word());
    }
@Test
    public void testWhitespaceOnlyTagProcessing() {
        Properties properties = new Properties();
        properties.setProperty("clean.xmltags", ".*");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");
        CoreLabel token1 = new CoreLabel();
        token1.setWord("<p>   </p>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Valid");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("Text");

        annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2, token3));

        annotator.annotate(annotation);

        List<CoreLabel> processedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertEquals(2, processedTokens.size());
        assertEquals("Valid", processedTokens.get(0).word());
        assertEquals("Text", processedTokens.get(1).word());
    }
@Test
    public void testNestedTagsWithSameName() {
        Properties properties = new Properties();
        properties.setProperty("clean.xmltags", ".*");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");
        CoreLabel token1 = new CoreLabel();
        token1.setWord("<div>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("<div>");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("Text inside inner div");

        CoreLabel token4 = new CoreLabel();
        token4.setWord("</div>");

        CoreLabel token5 = new CoreLabel();
        token5.setWord("</div>");

        annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2, token3, token4, token5));

        annotator.annotate(annotation);

        List<CoreLabel> processedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertEquals(1, processedTokens.size());
        assertEquals("Text inside inner div", processedTokens.get(0).word());
    }
@Test
    public void testCaseInsensitiveTagMatching() {
        Properties properties = new Properties();
        properties.setProperty("clean.xmltags", "TITLE");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");
        CoreLabel token1 = new CoreLabel();
        token1.setWord("<Title>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Upper and lower case support");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("</TITLE>");

        annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2, token3));

        annotator.annotate(annotation);

        List<CoreLabel> processedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertEquals(1, processedTokens.size());
        assertEquals("Upper and lower case support", processedTokens.get(0).word());
    }
@Test
    public void testProcessingOfSingleCharacterTags() {
        Properties properties = new Properties();
        properties.setProperty("clean.xmltags", "x");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");
        CoreLabel token1 = new CoreLabel();
        token1.setWord("<x>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Single-char");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("tag test");

        CoreLabel token4 = new CoreLabel();
        token4.setWord("</x>");

        annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2, token3, token4));

        annotator.annotate(annotation);

        List<CoreLabel> processedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertEquals(2, processedTokens.size());
        assertEquals("Single-char", processedTokens.get(0).word());
        assertEquals("tag test", processedTokens.get(1).word());
    }
@Test
    public void testPartialAttributeExtraction() {
        Properties properties = new Properties();
        properties.setProperty("clean.docAnnotations", "source=meta[src]");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");
        CoreLabel token1 = new CoreLabel();
        token1.setWord("<meta name='author'>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Ignored Content");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("</meta>");

        annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2, token3));

        annotator.annotate(annotation);

    }
@Test
    public void testHandlingOfMisplacedClosingTag() {
        Properties properties = new Properties();
        properties.setProperty("clean.allowflawedxml", "true");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");
        CoreLabel token1 = new CoreLabel();
        token1.setWord("</closed>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Valid");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("Text");

        annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2, token3));

        annotator.annotate(annotation);

        List<CoreLabel> processedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertEquals(2, processedTokens.size());
        assertEquals("Valid", processedTokens.get(0).word());
        assertEquals("Text", processedTokens.get(1).word());
    }
@Test
    public void testTagContainingOnlySymbols() {
        Properties properties = new Properties();
        properties.setProperty("clean.xmltags", ".*");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");
        CoreLabel token1 = new CoreLabel();
        token1.setWord("<tag>@#$%</tag>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Normal content");

        annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));

        annotator.annotate(annotation);

        List<CoreLabel> processedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertEquals(1, processedTokens.size());
        assertEquals("Normal content", processedTokens.get(0).word());
    }
@Test
    public void testUnclosedTagWithValidText() {
        Properties properties = new Properties();
        properties.setProperty("clean.allowflawedxml", "true");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");
        CoreLabel token1 = new CoreLabel();
        token1.setWord("<b>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Bold text");

        annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));

        annotator.annotate(annotation);

        List<CoreLabel> processedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertEquals(1, processedTokens.size());
        assertEquals("Bold text", processedTokens.get(0).word());
    }
@Test
    public void testDiscardUnnecessaryTokens() {
        Properties properties = new Properties();
        properties.setProperty("clean.ssplitDiscardTokens", "token");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");
        CoreLabel token1 = new CoreLabel();
        token1.setWord("Keep");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("token");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("this");

        annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2, token3));

        annotator.annotate(annotation);

        List<CoreLabel> processedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertEquals(2, processedTokens.size());
        assertEquals("Keep", processedTokens.get(0).word());
        assertEquals("this", processedTokens.get(1).word());
    }
@Test
    public void testXmlWithSpecialCharacters() {
        Properties properties = new Properties();
        properties.setProperty("clean.xmltags", ".*");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");
        
        CoreLabel token1 = new CoreLabel();
        token1.setWord("<p>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("&lt;escaped&gt;");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("text!");

        CoreLabel token4 = new CoreLabel();
        token4.setWord("</p>");
        
        annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2, token3, token4));

        annotator.annotate(annotation);

        List<CoreLabel> processedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertEquals(2, processedTokens.size());
        assertEquals("<escaped>", processedTokens.get(0).word());
        assertEquals("text!", processedTokens.get(1).word());
    }
@Test
    public void testUnmatchedClosingTagsIgnored() {
        Properties properties = new Properties();
        properties.setProperty("clean.allowflawedxml", "true");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");
        
        CoreLabel token1 = new CoreLabel();
        token1.setWord("</div>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Valid");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("content");

        annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2, token3));

        annotator.annotate(annotation);

        List<CoreLabel> processedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertEquals(2, processedTokens.size());
        assertEquals("Valid", processedTokens.get(0).word());
        assertEquals("content", processedTokens.get(1).word());
    }
@Test
    public void testMultipleNestedLevelsProcessedCorrectly() {
        Properties properties = new Properties();
        properties.setProperty("clean.xmltags", ".*");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");
        
        CoreLabel token1 = new CoreLabel();
        token1.setWord("<outer>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("<inner>");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("<deepest>");

        CoreLabel token4 = new CoreLabel();
        token4.setWord("Important text");

        CoreLabel token5 = new CoreLabel();
        token5.setWord("</deepest>");

        CoreLabel token6 = new CoreLabel();
        token6.setWord("</inner>");

        CoreLabel token7 = new CoreLabel();
        token7.setWord("</outer>");

        annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2, token3, token4, token5, token6, token7));

        annotator.annotate(annotation);

        List<CoreLabel> processedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertEquals(1, processedTokens.size());
        assertEquals("Important text", processedTokens.get(0).word());
    }
@Test
    public void testMissingClosingTagsWithMultipleOpenTags() {
        Properties properties = new Properties();
        properties.setProperty("clean.allowflawedxml", "true");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");
        
        CoreLabel token1 = new CoreLabel();
        token1.setWord("<start>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("<inner>");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("Kept text");

        annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2, token3));

        annotator.annotate(annotation);

        List<CoreLabel> processedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertEquals(1, processedTokens.size());
        assertEquals("Kept text", processedTokens.get(0).word());
    }
@Test
    public void testHandlingInvalidDocumentAnnotationKeys() {
        Properties properties = new Properties();
        properties.setProperty("clean.docAnnotations", "invalidTag=");
        Exception expectedException = null;
        
        try {
            new CleanXmlAnnotator(properties);
        } catch (Exception e) {
            expectedException = e;
        }

        assertNotNull(expectedException);
        assertTrue(expectedException instanceof IllegalArgumentException);
    }
@Test
    public void testTagWithinSentenceMaintainsIntegrity() {
        Properties properties = new Properties();
        properties.setProperty("clean.xmltags", "bold");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");
        
        CoreLabel token1 = new CoreLabel();
        token1.setWord("This");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("is");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("<bold>");

        CoreLabel token4 = new CoreLabel();
        token4.setWord("highlighted");

        CoreLabel token5 = new CoreLabel();
        token5.setWord("</bold>");

        CoreLabel token6 = new CoreLabel();
        token6.setWord("text");

        annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2, token3, token4, token5, token6));

        annotator.annotate(annotation);

        List<CoreLabel> processedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertEquals(4, processedTokens.size());
        assertEquals("This", processedTokens.get(0).word());
        assertEquals("is", processedTokens.get(1).word());
        assertEquals("highlighted", processedTokens.get(2).word());
        assertEquals("text", processedTokens.get(3).word());
    }
@Test
    public void testUnclosedXmlTagsWithText() {
        Properties properties = new Properties();
        properties.setProperty("clean.allowflawedxml", "true");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<b>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Bold text");

        annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));

        annotator.annotate(annotation);

        List<CoreLabel> processedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertEquals(1, processedTokens.size());
        assertEquals("Bold text", processedTokens.get(0).word());
    }
@Test
    public void testProcessingWithOnlyXmlTags() {
        Properties properties = new Properties();
        properties.setProperty("clean.xmltags", ".*");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<xml>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("</xml>");

        annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));

        annotator.annotate(annotation);

        List<CoreLabel> processedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertTrue(processedTokens.isEmpty());
    }
@Test
    public void testProcessingTagsThatOnlyContainSymbols() {
        Properties properties = new Properties();
        properties.setProperty("clean.xmltags", ".*");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<tag>!@#$%</tag>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Valid content");

        annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));

        annotator.annotate(annotation);

        List<CoreLabel> processedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertEquals(1, processedTokens.size());
        assertEquals("Valid content", processedTokens.get(0).word());
    }
@Test
    public void testProcessingOfQuotationTags() {
        Properties properties = new Properties();
        properties.setProperty("clean.quotetags", "quote");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<quote>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Quoted text");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("</quote>");

        annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2, token3));

        annotator.annotate(annotation);

        List<CoreLabel> processedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertEquals(1, processedTokens.size());
        assertEquals("Quoted text", processedTokens.get(0).word());
    }
@Test
    public void testProcessingWithNestedXmlTags() {
        Properties properties = new Properties();
        properties.setProperty("clean.xmltags", ".*");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<outer>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("<inner>");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("Nested text");

        CoreLabel token4 = new CoreLabel();
        token4.setWord("</inner>");

        CoreLabel token5 = new CoreLabel();
        token5.setWord("</outer>");

        annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2, token3, token4, token5));

        annotator.annotate(annotation);

        List<CoreLabel> processedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertEquals(1, processedTokens.size());
        assertEquals("Nested text", processedTokens.get(0).word());
    }
@Test
    public void testHandlingOfMalformedClosingTags() {
        Properties properties = new Properties();
        properties.setProperty("clean.allowflawedxml", "true");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<div>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Div content");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("</span>");

        annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2, token3));

        annotator.annotate(annotation);

        List<CoreLabel> processedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertEquals(1, processedTokens.size());
        assertEquals("Div content", processedTokens.get(0).word());
    }
@Test
    public void testHandlingOfInvalidAnnotationPatterns() {
        Properties properties = new Properties();
        properties.setProperty("clean.docAnnotations", "invalid=");
        Exception exceptionThrown = null;

        try {
            new CleanXmlAnnotator(properties);
        } catch (Exception e) {
            exceptionThrown = e;
        }

        assertNotNull(exceptionThrown);
        assertTrue(exceptionThrown instanceof IllegalArgumentException);
    }
@Test
    public void testProcessingXmlTagsWithAttributes() {
        Properties properties = new Properties();
        properties.setProperty("clean.tokenAnnotations", "author=[name]");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<meta name='John Doe'>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Content here");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("</meta>");

        annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2, token3));

        annotator.annotate(annotation);

        List<CoreLabel> processedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertEquals(1, processedTokens.size());
        assertEquals("John Doe", processedTokens.get(0).get(CoreAnnotations.AuthorAnnotation.class));
        assertEquals("Content here", processedTokens.get(0).word());
    }
@Test
    public void testProcessingWithIncompleteXmlTag() {
        Properties properties = new Properties();
        properties.setProperty("clean.allowflawedxml", "true");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<incomplete");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Content");

        annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));

        annotator.annotate(annotation);

        List<CoreLabel> processedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertEquals(1, processedTokens.size());
        assertEquals("Content", processedTokens.get(0).word());
    }
@Test
    public void testProcessingWithNoTextOutsideTags() {
        Properties properties = new Properties();
        properties.setProperty("clean.xmltags", ".*");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<tag>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("</tag>");

        annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));

        annotator.annotate(annotation);

        List<CoreLabel> processedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertTrue(processedTokens.isEmpty());
    }
@Test
    public void testMismatchedNestedTagsHandling() {
        Properties properties = new Properties();
        properties.setProperty("clean.allowflawedxml", "true");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<outer>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("<inner>");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("Valid content");

        CoreLabel token4 = new CoreLabel();
        token4.setWord("</outer>");

        annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2, token3, token4));

        annotator.annotate(annotation);

        List<CoreLabel> processedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertEquals(1, processedTokens.size());
        assertEquals("Valid content", processedTokens.get(0).word());
    }
@Test
    public void testTagWithOnlyWhitespaceContent() {
        Properties properties = new Properties();
        properties.setProperty("clean.xmltags", ".*");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<p>      </p>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Valid");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("Content");

        annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2, token3));

        annotator.annotate(annotation);

        List<CoreLabel> processedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertEquals(2, processedTokens.size());
        assertEquals("Valid", processedTokens.get(0).word());
        assertEquals("Content", processedTokens.get(1).word());
    }
@Test
    public void testExtractionOfDocumentId() {
        Properties properties = new Properties();
        properties.setProperty("clean.docAnnotations", "docID=doc[id]");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<doc id='12345'>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Body content");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("</doc>");

        annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2, token3));

        annotator.annotate(annotation);

        assertEquals("12345", annotation.get(CoreAnnotations.DocIDAnnotation.class));
    }
@Test
    public void testProcessingWithSelfClosingTag() {
        Properties properties = new Properties();
        properties.setProperty("clean.xmltags", ".*");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");

        CoreLabel token1 = new CoreLabel();
        token1.setWord("Text");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("<br/>");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("Next");

        annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2, token3));

        annotator.annotate(annotation);

        List<CoreLabel> processedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertEquals(2, processedTokens.size());
        assertEquals("Text", processedTokens.get(0).word());
        assertEquals("Next", processedTokens.get(1).word());
    }
@Test
    public void testMultipleSentenceEndingTags() {
        Properties properties = new Properties();
        properties.setProperty("clean.sentenceendingtags", "p|br");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("");

        CoreLabel token1 = new CoreLabel();
        token1.setWord("Sentence1");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("<p>");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("Sentence2");

        CoreLabel token4 = new CoreLabel();
        token4.setWord("</p>");

        CoreLabel token5 = new CoreLabel();
        token5.setWord("<br>");

        CoreLabel token6 = new CoreLabel();
        token6.setWord("Sentence3");

        annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2, token3, token4, token5, token6));

        annotator.annotate(annotation);

        List<CoreLabel> processedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertEquals(3, processedTokens.size());
        assertTrue(processedTokens.get(0).containsKey(CoreAnnotations.ForcedSentenceEndAnnotation.class));
        assertTrue(processedTokens.get(1).containsKey(CoreAnnotations.ForcedSentenceEndAnnotation.class));
    } 
}