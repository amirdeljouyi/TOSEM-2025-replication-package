package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.*;

public class CleanXmlAnnotator_1_GPTLLMTest {

 @Test
    public void testDefaultConstructor() {
        CleanXmlAnnotator annotator = new CleanXmlAnnotator();
        assertNotNull(annotator);
        assertTrue(annotator.requires().contains(CoreAnnotations.TokensAnnotation.class));
    }
@Test
    public void testConstructorWithProperties() {
        Properties properties = new Properties();
        properties.setProperty("clean.xmltags", "p|div");
        properties.setProperty("clean.sentenceendingtags", "p");
        properties.setProperty("clean.datetags", "date");
        properties.setProperty("clean.allowflawedxml", "false");

        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);
        assertNotNull(annotator);
    }
@Test
    public void testAnnotateWithSimpleXml() {
        CleanXmlAnnotator annotator = new CleanXmlAnnotator();

        Annotation annotation = new Annotation("<p>Hello world!</p>");
        List<CoreLabel> tokens = new ArrayList<>();
        
        CoreLabel token1 = new CoreLabel();
        token1.setWord("<p>");
        
        CoreLabel token2 = new CoreLabel();
        token2.setWord("Hello");
        
        CoreLabel token3 = new CoreLabel();
        token3.setWord("world");
        
        CoreLabel token4 = new CoreLabel();
        token4.setWord("!");

        CoreLabel token5 = new CoreLabel();
        token5.setWord("</p>");

        tokens.add(token1);
        tokens.add(token2);
        tokens.add(token3);
        tokens.add(token4);
        tokens.add(token5);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        List<CoreLabel> cleanedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

        assertEquals(3, cleanedTokens.size());
        assertEquals("Hello", cleanedTokens.get(0).word());
        assertEquals("world", cleanedTokens.get(1).word());
        assertEquals("!", cleanedTokens.get(2).word());
    }
@Test
    public void testHandlingFlawedXml() {
        Properties properties = new Properties();
        properties.setProperty("clean.allowflawedxml", "true");

        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("<div><p>Hello <b>world!</p></div>");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<div>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("<p>");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("Hello");

        CoreLabel token4 = new CoreLabel();
        token4.setWord("<b>");

        CoreLabel token5 = new CoreLabel();
        token5.setWord("world!");

        CoreLabel token6 = new CoreLabel();
        token6.setWord("</p>");

        CoreLabel token7 = new CoreLabel();
        token7.setWord("</div>");

        tokens.add(token1);
        tokens.add(token2);
        tokens.add(token3);
        tokens.add(token4);
        tokens.add(token5);
        tokens.add(token6);
        tokens.add(token7);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        List<CoreLabel> cleanedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

        assertEquals(2, cleanedTokens.size());
        assertEquals("Hello", cleanedTokens.get(0).word());
        assertEquals("world!", cleanedTokens.get(1).word());
    }
@Test
    public void testSentenceEndingTags() {
        Properties properties = new Properties();
        properties.setProperty("clean.sentenceendingtags", "p|br");

        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("<p>Hello.</p> <p>New sentence.</p>");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel t1 = new CoreLabel();
        t1.setWord("<p>");
        
        CoreLabel t2 = new CoreLabel();
        t2.setWord("Hello");
        
        CoreLabel t3 = new CoreLabel();
        t3.setWord(".");
        
        CoreLabel t4 = new CoreLabel();
        t4.setWord("</p>");
        
        CoreLabel t5 = new CoreLabel();
        t5.setWord("<p>");
        
        CoreLabel t6 = new CoreLabel();
        t6.setWord("New");
        
        CoreLabel t7 = new CoreLabel();
        t7.setWord("sentence");
        
        CoreLabel t8 = new CoreLabel();
        t8.setWord(".");
        
        CoreLabel t9 = new CoreLabel();
        t9.setWord("</p>");

        tokens.add(t1);
        tokens.add(t2);
        tokens.add(t3);
        tokens.add(t4);
        tokens.add(t5);
        tokens.add(t6);
        tokens.add(t7);
        tokens.add(t8);
        tokens.add(t9);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        List<CoreLabel> cleanedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

        assertTrue(cleanedTokens.get(2).get(CoreAnnotations.ForcedSentenceEndAnnotation.class));
        assertTrue(cleanedTokens.get(7).get(CoreAnnotations.ForcedSentenceEndAnnotation.class));
    }
@Test
    public void testRetainingAttributes() {
        Properties properties = new Properties();
        properties.setProperty("clean.docAnnotations", "docID=doc[id]");

        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("<doc id='1234'>Text content</doc>");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<doc id='1234'>");
        
        CoreLabel token2 = new CoreLabel();
        token2.setWord("Text");
        
        CoreLabel token3 = new CoreLabel();
        token3.setWord("content");
        
        CoreLabel token4 = new CoreLabel();
        token4.setWord("</doc>");

        tokens.add(token1);
        tokens.add(token2);
        tokens.add(token3);
        tokens.add(token4);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        assertEquals("1234", annotation.get(CoreAnnotations.DocIDAnnotation.class));
    }
@Test(expected = IllegalArgumentException.class)
    public void testInvalidDocAnnotationPattern() {
        Properties properties = new Properties();
        properties.setProperty("clean.docAnnotations", "invalidPattern");

        new CleanXmlAnnotator(properties);
    }
@Test
    public void testEmptyInput() {
        CleanXmlAnnotator annotator = new CleanXmlAnnotator();
        
        Annotation annotation = new Annotation("");
        List<CoreLabel> tokens = new ArrayList<>();

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        List<CoreLabel> cleanedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertTrue(cleanedTokens.isEmpty());
    }
@Test
    public void testOnlyXmlTagsInput() {
        CleanXmlAnnotator annotator = new CleanXmlAnnotator();
        
        Annotation annotation = new Annotation("<p></p>");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<p>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("</p>");

        tokens.add(token1);
        tokens.add(token2);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);
        
        List<CoreLabel> cleanedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertTrue(cleanedTokens.isEmpty());
    }
@Test
    public void testMismatchedXmlTags() {
        Properties properties = new Properties();
        properties.setProperty("clean.allowflawedxml", "false");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);
        
        Annotation annotation = new Annotation("<p>Hello <b>world!</p>");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<p>");
        
        CoreLabel token2 = new CoreLabel();
        token2.setWord("Hello");
        
        CoreLabel token3 = new CoreLabel();
        token3.setWord("<b>");
        
        CoreLabel token4 = new CoreLabel();
        token4.setWord("world!");
        
        CoreLabel token5 = new CoreLabel();
        token5.setWord("</p>");

        tokens.add(token1);
        tokens.add(token2);
        tokens.add(token3);
        tokens.add(token4);
        tokens.add(token5);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        
        try {
            annotator.annotate(annotation);
            fail("Expected IllegalArgumentException due to mismatched XML tags.");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }
@Test
    public void testSelfClosingTags() {
        CleanXmlAnnotator annotator = new CleanXmlAnnotator();
        
        Annotation annotation = new Annotation("<meta /><p>Hello world!</p>");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<meta />");
        
        CoreLabel token2 = new CoreLabel();
        token2.setWord("<p>");
        
        CoreLabel token3 = new CoreLabel();
        token3.setWord("Hello");
        
        CoreLabel token4 = new CoreLabel();
        token4.setWord("world!");
        
        CoreLabel token5 = new CoreLabel();
        token5.setWord("</p>");

        tokens.add(token1);
        tokens.add(token2);
        tokens.add(token3);
        tokens.add(token4);
        tokens.add(token5);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        List<CoreLabel> cleanedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

        assertEquals(2, cleanedTokens.size());
        assertEquals("Hello", cleanedTokens.get(0).word());
        assertEquals("world!", cleanedTokens.get(1).word());
    }
@Test
    public void testMultipleNestedTags() {
        CleanXmlAnnotator annotator = new CleanXmlAnnotator();
        
        Annotation annotation = new Annotation("<div><p><b>Hello</b> world!</p></div>");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<div>");
        
        CoreLabel token2 = new CoreLabel();
        token2.setWord("<p>");
        
        CoreLabel token3 = new CoreLabel();
        token3.setWord("<b>");
        
        CoreLabel token4 = new CoreLabel();
        token4.setWord("Hello");
        
        CoreLabel token5 = new CoreLabel();
        token5.setWord("</b>");
        
        CoreLabel token6 = new CoreLabel();
        token6.setWord("world!");
        
        CoreLabel token7 = new CoreLabel();
        token7.setWord("</p>");
        
        CoreLabel token8 = new CoreLabel();
        token8.setWord("</div>");

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

        List<CoreLabel> cleanedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

        assertEquals(2, cleanedTokens.size());
        assertEquals("Hello", cleanedTokens.get(0).word());
        assertEquals("world!", cleanedTokens.get(1).word());
    }
@Test
    public void testTextOutsideAndInsideXmlTags() {
        CleanXmlAnnotator annotator = new CleanXmlAnnotator();
        
        Annotation annotation = new Annotation("Raw text. <p>Hello world!</p> More raw text.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("Raw");
        
        CoreLabel token2 = new CoreLabel();
        token2.setWord("text.");
        
        CoreLabel token3 = new CoreLabel();
        token3.setWord("<p>");
        
        CoreLabel token4 = new CoreLabel();
        token4.setWord("Hello");
        
        CoreLabel token5 = new CoreLabel();
        token5.setWord("world!");
        
        CoreLabel token6 = new CoreLabel();
        token6.setWord("</p>");
        
        CoreLabel token7 = new CoreLabel();
        token7.setWord("More");
        
        CoreLabel token8 = new CoreLabel();
        token8.setWord("raw");
        
        CoreLabel token9 = new CoreLabel();
        token9.setWord("text.");

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

        List<CoreLabel> cleanedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

        assertEquals(5, cleanedTokens.size());
        assertEquals("Raw", cleanedTokens.get(0).word());
        assertEquals("text.", cleanedTokens.get(1).word());
        assertEquals("Hello", cleanedTokens.get(2).word());
        assertEquals("world!", cleanedTokens.get(3).word());
        assertEquals("More", cleanedTokens.get(4).word());
    }
@Test
    public void testMissingClosingTagsWithAllowFlawedXmlTrue() {
        Properties properties = new Properties();
        properties.setProperty("clean.allowflawedxml", "true");

        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("<p>Hello <b>world!");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<p>");
        
        CoreLabel token2 = new CoreLabel();
        token2.setWord("Hello");
        
        CoreLabel token3 = new CoreLabel();
        token3.setWord("<b>");
        
        CoreLabel token4 = new CoreLabel();
        token4.setWord("world!");

        tokens.add(token1);
        tokens.add(token2);
        tokens.add(token3);
        tokens.add(token4);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        
        annotator.annotate(annotation);
        
        List<CoreLabel> cleanedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertEquals(2, cleanedTokens.size());
        assertEquals("Hello", cleanedTokens.get(0).word());
        assertEquals("world!", cleanedTokens.get(1).word());
    }
@Test
    public void testMultipleConsecutiveTags() {
        CleanXmlAnnotator annotator = new CleanXmlAnnotator();

        Annotation annotation = new Annotation("<p><b></b><i></i><u></u></p>");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<p>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("<b>");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("</b>");

        CoreLabel token4 = new CoreLabel();
        token4.setWord("<i>");

        CoreLabel token5 = new CoreLabel();
        token5.setWord("</i>");

        CoreLabel token6 = new CoreLabel();
        token6.setWord("<u>");

        CoreLabel token7 = new CoreLabel();
        token7.setWord("</u>");

        CoreLabel token8 = new CoreLabel();
        token8.setWord("</p>");

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

        List<CoreLabel> cleanedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

        assertTrue(cleanedTokens.isEmpty());
    }
@Test
    public void testUnclosedSelfClosingTags() {
        CleanXmlAnnotator annotator = new CleanXmlAnnotator();

        Annotation annotation = new Annotation("<img src='image.png'> Some text.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<img src='image.png'>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Some");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("text.");

        tokens.add(token1);
        tokens.add(token2);
        tokens.add(token3);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        List<CoreLabel> cleanedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

        assertEquals(2, cleanedTokens.size());
        assertEquals("Some", cleanedTokens.get(0).word());
        assertEquals("text.", cleanedTokens.get(1).word());
    }
@Test
    public void testUnclosedNestedTags() {
        CleanXmlAnnotator annotator = new CleanXmlAnnotator();

        Annotation annotation = new Annotation("<div><p>Hello <b>world! </div>");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<div>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("<p>");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("Hello");

        CoreLabel token4 = new CoreLabel();
        token4.setWord("<b>");

        CoreLabel token5 = new CoreLabel();
        token5.setWord("world!");

        CoreLabel token6 = new CoreLabel();
        token6.setWord("</div>");

        tokens.add(token1);
        tokens.add(token2);
        tokens.add(token3);
        tokens.add(token4);
        tokens.add(token5);
        tokens.add(token6);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

        try {
            annotator.annotate(annotation);
            fail("Expected IllegalArgumentException due to unclosed nested tags.");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }
@Test
    public void testTagsWithAttributesPreserveText() {
        CleanXmlAnnotator annotator = new CleanXmlAnnotator();

        Annotation annotation = new Annotation("<p class='important'>Hello world!</p>");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<p class='important'>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Hello");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("world!");

        CoreLabel token4 = new CoreLabel();
        token4.setWord("</p>");

        tokens.add(token1);
        tokens.add(token2);
        tokens.add(token3);
        tokens.add(token4);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        List<CoreLabel> cleanedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

        assertEquals(2, cleanedTokens.size());
        assertEquals("Hello", cleanedTokens.get(0).word());
        assertEquals("world!", cleanedTokens.get(1).word());
    }
@Test
    public void testMultipleDocumentAnnotationsProcessing() {
        Properties properties = new Properties();
        properties.setProperty("clean.docAnnotations", "docID=doc[id],docType=doc[type]");

        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("<doc id='1234' type='news'>Text Content</doc>");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<doc id='1234' type='news'>");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Text");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("Content");

        CoreLabel token4 = new CoreLabel();
        token4.setWord("</doc>");

        tokens.add(token1);
        tokens.add(token2);
        tokens.add(token3);
        tokens.add(token4);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        assertEquals("1234", annotation.get(CoreAnnotations.DocIDAnnotation.class));
        assertEquals("news", annotation.get(CoreAnnotations.DocTypeAnnotation.class));
    }
@Test
    public void testUnexpectedSpecialCharacterHandling() {
        CleanXmlAnnotator annotator = new CleanXmlAnnotator();

        Annotation annotation = new Annotation("&lt;p&gt;Hello &amp; world!&lt;/p&gt;");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("&lt;p&gt;");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Hello");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("&amp;");

        CoreLabel token4 = new CoreLabel();
        token4.setWord("world!");

        CoreLabel token5 = new CoreLabel();
        token5.setWord("&lt;/p&gt;");

        tokens.add(token1);
        tokens.add(token2);
        tokens.add(token3);
        tokens.add(token4);
        tokens.add(token5);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        List<CoreLabel> cleanedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

        assertEquals(3, cleanedTokens.size());
        assertEquals("Hello", cleanedTokens.get(0).word());
        assertEquals("&", cleanedTokens.get(1).word());
        assertEquals("world!", cleanedTokens.get(2).word());
    }
@Test
    public void testInvalidXmlTagStructure() {
        Properties properties = new Properties();
        properties.setProperty("clean.allowflawedxml", "false");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("<p><b>Hello</p></b>");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel t1 = new CoreLabel();
        t1.setWord("<p>");

        CoreLabel t2 = new CoreLabel();
        t2.setWord("<b>");

        CoreLabel t3 = new CoreLabel();
        t3.setWord("Hello");

        CoreLabel t4 = new CoreLabel();
        t4.setWord("</p>");

        CoreLabel t5 = new CoreLabel();
        t5.setWord("</b>");

        tokens.add(t1);
        tokens.add(t2);
        tokens.add(t3);
        tokens.add(t4);
        tokens.add(t5);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

        try {
            annotator.annotate(annotation);
            fail("Expected IllegalArgumentException due to mismatched tags.");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }
@Test
    public void testProcessingEmptyString() {
        CleanXmlAnnotator annotator = new CleanXmlAnnotator();

        Annotation annotation = new Annotation("");
        List<CoreLabel> tokens = new ArrayList<>();

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        List<CoreLabel> cleanedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertTrue(cleanedTokens.isEmpty());
    }
@Test
    public void testProcessingOnlyTagsNoText() {
        CleanXmlAnnotator annotator = new CleanXmlAnnotator();

        Annotation annotation = new Annotation("<html><body><p></p></body></html>");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel t1 = new CoreLabel();
        t1.setWord("<html>");

        CoreLabel t2 = new CoreLabel();
        t2.setWord("<body>");

        CoreLabel t3 = new CoreLabel();
        t3.setWord("<p>");

        CoreLabel t4 = new CoreLabel();
        t4.setWord("</p>");

        CoreLabel t5 = new CoreLabel();
        t5.setWord("</body>");

        CoreLabel t6 = new CoreLabel();
        t6.setWord("</html>");

        tokens.add(t1);
        tokens.add(t2);
        tokens.add(t3);
        tokens.add(t4);
        tokens.add(t5);
        tokens.add(t6);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        List<CoreLabel> cleanedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertTrue(cleanedTokens.isEmpty());
    }
@Test
    public void testProcessingWithEscapedHtmlCharacters() {
        CleanXmlAnnotator annotator = new CleanXmlAnnotator();

        Annotation annotation = new Annotation("&lt;p&gt;Hello &amp; world!&lt;/p&gt;");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel t1 = new CoreLabel();
        t1.setWord("&lt;p&gt;");

        CoreLabel t2 = new CoreLabel();
        t2.setWord("Hello");

        CoreLabel t3 = new CoreLabel();
        t3.setWord("&amp;");

        CoreLabel t4 = new CoreLabel();
        t4.setWord("world!");

        CoreLabel t5 = new CoreLabel();
        t5.setWord("&lt;/p&gt;");

        tokens.add(t1);
        tokens.add(t2);
        tokens.add(t3);
        tokens.add(t4);
        tokens.add(t5);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        List<CoreLabel> cleanedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

        assertEquals(3, cleanedTokens.size());
        assertEquals("Hello", cleanedTokens.get(0).word());
        assertEquals("&", cleanedTokens.get(1).word());
        assertEquals("world!", cleanedTokens.get(2).word());
    }
@Test
    public void testProcessingMultipleSentenceEndingTags() {
        Properties properties = new Properties();
        properties.setProperty("clean.sentenceendingtags", "p|br");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("<p>First sentence.</p><p>Second sentence.</p>");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel t1 = new CoreLabel();
        t1.setWord("<p>");

        CoreLabel t2 = new CoreLabel();
        t2.setWord("First");

        CoreLabel t3 = new CoreLabel();
        t3.setWord("sentence.");

        CoreLabel t4 = new CoreLabel();
        t4.setWord("</p>");

        CoreLabel t5 = new CoreLabel();
        t5.setWord("<p>");

        CoreLabel t6 = new CoreLabel();
        t6.setWord("Second");

        CoreLabel t7 = new CoreLabel();
        t7.setWord("sentence.");

        CoreLabel t8 = new CoreLabel();
        t8.setWord("</p>");

        tokens.add(t1);
        tokens.add(t2);
        tokens.add(t3);
        tokens.add(t4);
        tokens.add(t5);
        tokens.add(t6);
        tokens.add(t7);
        tokens.add(t8);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        List<CoreLabel> cleanedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

        assertTrue(cleanedTokens.get(2).get(CoreAnnotations.ForcedSentenceEndAnnotation.class));
        assertTrue(cleanedTokens.get(6).get(CoreAnnotations.ForcedSentenceEndAnnotation.class));
    }
@Test
    public void testMultipleNestedAndSelfClosingTags() {
        CleanXmlAnnotator annotator = new CleanXmlAnnotator();

        Annotation annotation = new Annotation("<div><span><br /></span>Hello <b>World!</b></div>");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel t1 = new CoreLabel();
        t1.setWord("<div>");

        CoreLabel t2 = new CoreLabel();
        t2.setWord("<span>");

        CoreLabel t3 = new CoreLabel();
        t3.setWord("<br />");

        CoreLabel t4 = new CoreLabel();
        t4.setWord("</span>");

        CoreLabel t5 = new CoreLabel();
        t5.setWord("Hello");

        CoreLabel t6 = new CoreLabel();
        t6.setWord("<b>");

        CoreLabel t7 = new CoreLabel();
        t7.setWord("World!");

        CoreLabel t8 = new CoreLabel();
        t8.setWord("</b>");

        CoreLabel t9 = new CoreLabel();
        t9.setWord("</div>");

        tokens.add(t1);
        tokens.add(t2);
        tokens.add(t3);
        tokens.add(t4);
        tokens.add(t5);
        tokens.add(t6);
        tokens.add(t7);
        tokens.add(t8);
        tokens.add(t9);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        List<CoreLabel> cleanedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

        assertEquals(2, cleanedTokens.size());
        assertEquals("Hello", cleanedTokens.get(0).word());
        assertEquals("World!", cleanedTokens.get(1).word());
    }
@Test
    public void testXmlWithMixedContent() {
        CleanXmlAnnotator annotator = new CleanXmlAnnotator();

        Annotation annotation = new Annotation("Normal text <p>XML paragraph</p> more text.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel t1 = new CoreLabel();
        t1.setWord("Normal");

        CoreLabel t2 = new CoreLabel();
        t2.setWord("text");

        CoreLabel t3 = new CoreLabel();
        t3.setWord("<p>");

        CoreLabel t4 = new CoreLabel();
        t4.setWord("XML");

        CoreLabel t5 = new CoreLabel();
        t5.setWord("paragraph");

        CoreLabel t6 = new CoreLabel();
        t6.setWord("</p>");

        CoreLabel t7 = new CoreLabel();
        t7.setWord("more");

        CoreLabel t8 = new CoreLabel();
        t8.setWord("text.");

        tokens.add(t1);
        tokens.add(t2);
        tokens.add(t3);
        tokens.add(t4);
        tokens.add(t5);
        tokens.add(t6);
        tokens.add(t7);
        tokens.add(t8);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        List<CoreLabel> cleanedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertEquals(5, cleanedTokens.size());
        assertEquals("Normal", cleanedTokens.get(0).word());
        assertEquals("text", cleanedTokens.get(1).word());
        assertEquals("XML", cleanedTokens.get(2).word());
        assertEquals("paragraph", cleanedTokens.get(3).word());
        assertEquals("more", cleanedTokens.get(4).word());
    }
@Test
    public void testUnclosedRootTag() {
        Properties properties = new Properties();
        properties.setProperty("clean.allowflawedxml", "false");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("<article><p>Hello, world!");

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel t1 = new CoreLabel();
        t1.setWord("<article>");

        CoreLabel t2 = new CoreLabel();
        t2.setWord("<p>");

        CoreLabel t3 = new CoreLabel();
        t3.setWord("Hello,");

        CoreLabel t4 = new CoreLabel();
        t4.setWord("world!");

        tokens.add(t1);
        tokens.add(t2);
        tokens.add(t3);
        tokens.add(t4);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

        try {
            annotator.annotate(annotation);
            fail("Expected IllegalArgumentException due to unclosed root XML tag.");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }
@Test
    public void testProcessingWithNoXmlTags() {
        CleanXmlAnnotator annotator = new CleanXmlAnnotator();

        Annotation annotation = new Annotation("This is just normal text.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel t1 = new CoreLabel();
        t1.setWord("This");

        CoreLabel t2 = new CoreLabel();
        t2.setWord("is");

        CoreLabel t3 = new CoreLabel();
        t3.setWord("just");

        CoreLabel t4 = new CoreLabel();
        t4.setWord("normal");

        CoreLabel t5 = new CoreLabel();
        t5.setWord("text.");

        tokens.add(t1);
        tokens.add(t2);
        tokens.add(t3);
        tokens.add(t4);
        tokens.add(t5);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        List<CoreLabel> cleanedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertEquals(5, cleanedTokens.size());
        assertEquals("This", cleanedTokens.get(0).word());
        assertEquals("is", cleanedTokens.get(1).word());
        assertEquals("just", cleanedTokens.get(2).word());
        assertEquals("normal", cleanedTokens.get(3).word());
        assertEquals("text.", cleanedTokens.get(4).word());
    }
@Test
    public void testProcessingWithOnlyOpeningTags() {
        Properties properties = new Properties();
        properties.setProperty("clean.allowflawedxml", "true");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("<div><p>Hello world!");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel t1 = new CoreLabel();
        t1.setWord("<div>");

        CoreLabel t2 = new CoreLabel();
        t2.setWord("<p>");

        CoreLabel t3 = new CoreLabel();
        t3.setWord("Hello");

        CoreLabel t4 = new CoreLabel();
        t4.setWord("world!");

        tokens.add(t1);
        tokens.add(t2);
        tokens.add(t3);
        tokens.add(t4);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        List<CoreLabel> cleanedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertEquals(2, cleanedTokens.size());
        assertEquals("Hello", cleanedTokens.get(0).word());
        assertEquals("world!", cleanedTokens.get(1).word());
    }
@Test
    public void testSentenceBeyondNestingBoundary() {
        Properties properties = new Properties();
        properties.setProperty("clean.sentenceendingtags", "p|h1");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("<h1>Title</h1> Text outside.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel t1 = new CoreLabel();
        t1.setWord("<h1>");

        CoreLabel t2 = new CoreLabel();
        t2.setWord("Title");

        CoreLabel t3 = new CoreLabel();
        t3.setWord("</h1>");

        CoreLabel t4 = new CoreLabel();
        t4.setWord("Text");

        CoreLabel t5 = new CoreLabel();
        t5.setWord("outside.");

        tokens.add(t1);
        tokens.add(t2);
        tokens.add(t3);
        tokens.add(t4);
        tokens.add(t5);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        List<CoreLabel> cleanedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

        assertEquals(3, cleanedTokens.size());
        assertEquals("Title", cleanedTokens.get(0).word());
        assertTrue(cleanedTokens.get(0).get(CoreAnnotations.ForcedSentenceEndAnnotation.class));
        assertEquals("Text", cleanedTokens.get(1).word());
        assertEquals("outside.", cleanedTokens.get(2).word());
    }
@Test
    public void testMultipleInlineTagsPreserveSentence() {
        CleanXmlAnnotator annotator = new CleanXmlAnnotator();

        Annotation annotation = new Annotation("This is <b>bold</b> and <i>italic</i> text.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel t1 = new CoreLabel();
        t1.setWord("This");

        CoreLabel t2 = new CoreLabel();
        t2.setWord("is");

        CoreLabel t3 = new CoreLabel();
        t3.setWord("<b>");

        CoreLabel t4 = new CoreLabel();
        t4.setWord("bold");

        CoreLabel t5 = new CoreLabel();
        t5.setWord("</b>");

        CoreLabel t6 = new CoreLabel();
        t6.setWord("and");

        CoreLabel t7 = new CoreLabel();
        t7.setWord("<i>");

        CoreLabel t8 = new CoreLabel();
        t8.setWord("italic");

        CoreLabel t9 = new CoreLabel();
        t9.setWord("</i>");

        CoreLabel t10 = new CoreLabel();
        t10.setWord("text.");

        tokens.add(t1);
        tokens.add(t2);
        tokens.add(t3);
        tokens.add(t4);
        tokens.add(t5);
        tokens.add(t6);
        tokens.add(t7);
        tokens.add(t8);
        tokens.add(t9);
        tokens.add(t10);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        List<CoreLabel> cleanedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertEquals(6, cleanedTokens.size());
        assertEquals("This", cleanedTokens.get(0).word());
        assertEquals("is", cleanedTokens.get(1).word());
        assertEquals("bold", cleanedTokens.get(2).word());
        assertEquals("and", cleanedTokens.get(3).word());
        assertEquals("italic", cleanedTokens.get(4).word());
        assertEquals("text.", cleanedTokens.get(5).word());
    }
@Test
    public void testProcessingWithMultipleDifferentTags() {
        CleanXmlAnnotator annotator = new CleanXmlAnnotator();

        Annotation annotation = new Annotation("<article><title>Headline</title><body>Some content inside XML.</body></article>");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel t1 = new CoreLabel();
        t1.setWord("<article>");

        CoreLabel t2 = new CoreLabel();
        t2.setWord("<title>");

        CoreLabel t3 = new CoreLabel();
        t3.setWord("Headline");

        CoreLabel t4 = new CoreLabel();
        t4.setWord("</title>");

        CoreLabel t5 = new CoreLabel();
        t5.setWord("<body>");

        CoreLabel t6 = new CoreLabel();
        t6.setWord("Some");

        CoreLabel t7 = new CoreLabel();
        t7.setWord("content");

        CoreLabel t8 = new CoreLabel();
        t8.setWord("inside");

        CoreLabel t9 = new CoreLabel();
        t9.setWord("XML.");

        CoreLabel t10 = new CoreLabel();
        t10.setWord("</body>");

        CoreLabel t11 = new CoreLabel();
        t11.setWord("</article>");

        tokens.add(t1);
        tokens.add(t2);
        tokens.add(t3);
        tokens.add(t4);
        tokens.add(t5);
        tokens.add(t6);
        tokens.add(t7);
        tokens.add(t8);
        tokens.add(t9);
        tokens.add(t10);
        tokens.add(t11);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        List<CoreLabel> cleanedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

        assertEquals(5, cleanedTokens.size());
        assertEquals("Headline", cleanedTokens.get(0).word());
        assertEquals("Some", cleanedTokens.get(1).word());
        assertEquals("content", cleanedTokens.get(2).word());
        assertEquals("inside", cleanedTokens.get(3).word());
        assertEquals("XML.", cleanedTokens.get(4).word());
    }
@Test
    public void testSingleSelfClosingTag() {
        CleanXmlAnnotator annotator = new CleanXmlAnnotator();

        Annotation annotation = new Annotation("<image />");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel t1 = new CoreLabel();
        t1.setWord("<image />");

        tokens.add(t1);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        List<CoreLabel> cleanedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertTrue(cleanedTokens.isEmpty());
    }
@Test
    public void testSentenceSplittingWithNestedTags() {
        Properties properties = new Properties();
        properties.setProperty("clean.sentenceendingtags", "div|p");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("<div>Sentence one. <p>Sentence two.</p></div>");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel t1 = new CoreLabel();
        t1.setWord("<div>");

        CoreLabel t2 = new CoreLabel();
        t2.setWord("Sentence");

        CoreLabel t3 = new CoreLabel();
        t3.setWord("one.");

        CoreLabel t4 = new CoreLabel();
        t4.setWord("<p>");

        CoreLabel t5 = new CoreLabel();
        t5.setWord("Sentence");

        CoreLabel t6 = new CoreLabel();
        t6.setWord("two.");

        CoreLabel t7 = new CoreLabel();
        t7.setWord("</p>");

        CoreLabel t8 = new CoreLabel();
        t8.setWord("</div>");

        tokens.add(t1);
        tokens.add(t2);
        tokens.add(t3);
        tokens.add(t4);
        tokens.add(t5);
        tokens.add(t6);
        tokens.add(t7);
        tokens.add(t8);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        List<CoreLabel> cleanedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

        assertEquals(4, cleanedTokens.size());
        assertTrue(cleanedTokens.get(2).get(CoreAnnotations.ForcedSentenceEndAnnotation.class));
        assertTrue(cleanedTokens.get(3).get(CoreAnnotations.ForcedSentenceEndAnnotation.class));
    }
@Test
    public void testNestedStructureWithAttributes() {
        CleanXmlAnnotator annotator = new CleanXmlAnnotator();

        Annotation annotation = new Annotation("<note type='reminder'><message>Don't forget!</message></note>");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel t1 = new CoreLabel();
        t1.setWord("<note type='reminder'>");

        CoreLabel t2 = new CoreLabel();
        t2.setWord("<message>");

        CoreLabel t3 = new CoreLabel();
        t3.setWord("Don't");

        CoreLabel t4 = new CoreLabel();
        t4.setWord("forget!");

        CoreLabel t5 = new CoreLabel();
        t5.setWord("</message>");

        CoreLabel t6 = new CoreLabel();
        t6.setWord("</note>");

        tokens.add(t1);
        tokens.add(t2);
        tokens.add(t3);
        tokens.add(t4);
        tokens.add(t5);
        tokens.add(t6);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        List<CoreLabel> cleanedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertEquals(2, cleanedTokens.size());
        assertEquals("Don't", cleanedTokens.get(0).word());
        assertEquals("forget!", cleanedTokens.get(1).word());
    }
@Test
    public void testUnusualTagFormats() {
        CleanXmlAnnotator annotator = new CleanXmlAnnotator();

        Annotation annotation = new Annotation("<customTag></customTag>");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel t1 = new CoreLabel();
        t1.setWord("<customTag>");

        CoreLabel t2 = new CoreLabel();
        t2.setWord("</customTag>");

        tokens.add(t1);
        tokens.add(t2);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        List<CoreLabel> cleanedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertTrue(cleanedTokens.isEmpty());
    }
@Test
    public void testIgnoringCustomTagsWithText() {
        Properties properties = new Properties();
        properties.setProperty("clean.xmltags", "customTag");
        CleanXmlAnnotator annotator = new CleanXmlAnnotator(properties);

        Annotation annotation = new Annotation("<customTag>Hello World!</customTag>");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel t1 = new CoreLabel();
        t1.setWord("<customTag>");

        CoreLabel t2 = new CoreLabel();
        t2.setWord("Hello");

        CoreLabel t3 = new CoreLabel();
        t3.setWord("World!");

        CoreLabel t4 = new CoreLabel();
        t4.setWord("</customTag>");

        tokens.add(t1);
        tokens.add(t2);
        tokens.add(t3);
        tokens.add(t4);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        List<CoreLabel> cleanedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        assertEquals(2, cleanedTokens.size());
        assertEquals("Hello", cleanedTokens.get(0).word());
        assertEquals("World!", cleanedTokens.get(1).word());
    } 
}