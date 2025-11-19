package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class CleanXmlAnnotator_5_GPTLLMTest {

 @Test
    public void testDefaultConstructor() {
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();
        assertNotNull("CleanXmlAnnotator should be instantiated", cleanXmlAnnotator);
    }
@Test
    public void testProcessesValidXmlTokens() {
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<p>");
        CoreLabel token2 = new CoreLabel();
        token2.setWord("Hello");
        CoreLabel token3 = new CoreLabel();
        token3.setWord("world");
        CoreLabel token4 = new CoreLabel();
        token4.setWord("</p>");

        List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4);
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

        assertEquals(2, processedTokens.size());
        assertEquals("Hello", processedTokens.get(0).word());
        assertEquals("world", processedTokens.get(1).word());
    }
@Test
    public void testHandlesUnclosedTagsGracefully() {
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<p>");
        CoreLabel token2 = new CoreLabel();
        token2.setWord("Test");
        CoreLabel token3 = new CoreLabel();
        token3.setWord("<b>");
        CoreLabel token4 = new CoreLabel();
        token4.setWord("data");

        List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4);

        try {
            cleanXmlAnnotator.process(tokens);
            assertTrue(true);
        } catch (IllegalArgumentException e) {
            fail("Should handle unclosed tags gracefully");
        }
    }
@Test
    public void testAnnotatorProcessesDocumentMetaData() {
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();
        Annotation annotation = new Annotation("<docid>12345</docid>");

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<docid>");
        CoreLabel token2 = new CoreLabel();
        token2.setWord("12345");
        CoreLabel token3 = new CoreLabel();
        token3.setWord("</docid>");

        List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);
        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

        cleanXmlAnnotator.annotate(annotation);

        assertEquals("12345", annotation.get(CoreAnnotations.DocIDAnnotation.class));
    }
@Test
    public void testHandlesFlawedXmlWithAllowFlawedXmlEnabled() {
        Properties props = new Properties();
        props.setProperty("clean.allowflawedxml", "true");
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(props);

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<text>");
        CoreLabel token2 = new CoreLabel();
        token2.setWord("<b>");
        CoreLabel token3 = new CoreLabel();
        token3.setWord("Flawed");
        CoreLabel token4 = new CoreLabel();
        token4.setWord("XML");
        CoreLabel token5 = new CoreLabel();
        token5.setWord("</text>");

        List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5);

        try {
            List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);
            assertEquals(2, processedTokens.size());
        } catch (IllegalArgumentException e) {
            fail("Should process flawed XML without throwing exception");
        }
    }
@Test
    public void testSentenceEndingTagForcesSentenceSplit() {
        Properties props = new Properties();
        props.setProperty("clean.sentenceendingtags", "p");
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(props);

        CoreLabel token1 = new CoreLabel();
        token1.setWord("First");
        CoreLabel token2 = new CoreLabel();
        token2.setWord("sentence");
        CoreLabel token3 = new CoreLabel();
        token3.setWord("<p>");
        CoreLabel token4 = new CoreLabel();
        token4.setWord("Second");
        CoreLabel token5 = new CoreLabel();
        token5.setWord("sentence");

        List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5);
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

        assertTrue(processedTokens.get(1).containsKey(CoreAnnotations.ForcedSentenceEndAnnotation.class));
    }
@Test
    public void testProcessesMultipleNestedTags() {
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<p>");
        CoreLabel token2 = new CoreLabel();
        token2.setWord("<b>");
        CoreLabel token3 = new CoreLabel();
        token3.setWord("Nested");
        CoreLabel token4 = new CoreLabel();
        token4.setWord("text");
        CoreLabel token5 = new CoreLabel();
        token5.setWord("</b>");
        CoreLabel token6 = new CoreLabel();
        token6.setWord("</p>");

        List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5, token6);
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

        assertEquals(2, processedTokens.size());
        assertEquals("Nested", processedTokens.get(0).word());
    }
@Test
    public void testProcessesUtteranceTagCorrectly() {
        Properties props = new Properties();
        props.setProperty("clean.turntags", "turn");
        CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(props);

        CoreLabel token1 = new CoreLabel();
        token1.setWord("<turn>");
        CoreLabel token2 = new CoreLabel();
        token2.setWord("This");
        CoreLabel token3 = new CoreLabel();
        token3.setWord("is");
        CoreLabel token4 = new CoreLabel();
        token4.setWord("a");
        CoreLabel token5 = new CoreLabel();
        token5.setWord("turn");
        CoreLabel token6 = new CoreLabel();
        token6.setWord("</turn>");

        List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5, token6);
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);
        
        assertEquals(Integer.valueOf(1), processedTokens.get(0).get(CoreAnnotations.UtteranceAnnotation.class));
    }
@Test
public void testHandlesSelfClosingTags() {
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("<br/>");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("Hello");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("world");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);
    List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

    assertEquals(2, processedTokens.size());
    assertEquals("Hello", processedTokens.get(0).word());
    assertEquals("world", processedTokens.get(1).word());
}
@Test
public void testPreservesTextOutsideXmlTags() {
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Introduction:");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("<p>");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("Content");
    CoreLabel token4 = new CoreLabel();
    token4.setWord("</p>");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4);
    List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

    assertEquals(2, processedTokens.size());
    assertEquals("Introduction:", processedTokens.get(0).word());
    assertEquals("Content", processedTokens.get(1).word());
}
@Test
public void testHandlesSequentialEmptyXmlTags() {
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();
    
    CoreLabel token1 = new CoreLabel();
    token1.setWord("<tag1>");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("<tag2>");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("Text");
    CoreLabel token4 = new CoreLabel();
    token4.setWord("</tag2>");
    CoreLabel token5 = new CoreLabel();
    token5.setWord("</tag1>");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5);
    List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

    assertEquals(1, processedTokens.size());
    assertEquals("Text", processedTokens.get(0).word());
}
@Test
public void testHandlesMultipleDifferentSentenceEndTags() {
    Properties props = new Properties();
    props.setProperty("clean.sentenceendingtags", "p,div");
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(props);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Sentence");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("one");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("<p>");
    CoreLabel token4 = new CoreLabel();
    token4.setWord("Sentence");
    CoreLabel token5 = new CoreLabel();
    token5.setWord("two");
    CoreLabel token6 = new CoreLabel();
    token6.setWord("<div>");
    CoreLabel token7 = new CoreLabel();
    token7.setWord("Final");
    CoreLabel token8 = new CoreLabel();
    token8.setWord("sentence");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5, token6, token7, token8);
    List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

    assertTrue(processedTokens.get(1).containsKey(CoreAnnotations.ForcedSentenceEndAnnotation.class));
    assertTrue(processedTokens.get(4).containsKey(CoreAnnotations.ForcedSentenceEndAnnotation.class));
}
@Test
public void testIgnoresUnknownXmlTags() {
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("<unknownTag>");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("Some");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("text");
    CoreLabel token4 = new CoreLabel();
    token4.setWord("</unknownTag>");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4);
    List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

    assertEquals(2, processedTokens.size());
    assertEquals("Some", processedTokens.get(0).word());
    assertEquals("text", processedTokens.get(1).word());
}
@Test
public void testProcessesHighNestedXmlTags() {
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("<outer>");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("<middle>");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("<inner>");
    CoreLabel token4 = new CoreLabel();
    token4.setWord("Data");
    CoreLabel token5 = new CoreLabel();
    token5.setWord("</inner>");
    CoreLabel token6 = new CoreLabel();
    token6.setWord("</middle>");
    CoreLabel token7 = new CoreLabel();
    token7.setWord("</outer>");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5, token6, token7);
    List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

    assertEquals(1, processedTokens.size());
    assertEquals("Data", processedTokens.get(0).word());
}
@Test
public void testRemovesMalformedXmlTags() {
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("<unclosed");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("validToken");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("</another>");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);
    
    try {
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);
        assertEquals(1, processedTokens.size());
        assertEquals("validToken", processedTokens.get(0).word());
    } catch (IllegalArgumentException e) {
        fail("Malformed tags should be ignored, not cause an exception.");
    }
}
@Test
public void testHandlesMixedContentXml() {
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Intro");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("<p>");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("Only");
    CoreLabel token4 = new CoreLabel();
    token4.setWord("this");
    CoreLabel token5 = new CoreLabel();
    token5.setWord("part");
    CoreLabel token6 = new CoreLabel();
    token6.setWord("</p>");
    CoreLabel token7 = new CoreLabel();
    token7.setWord("Outro");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5, token6, token7);
    List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

    assertEquals(5, processedTokens.size());
    assertEquals("Intro", processedTokens.get(0).word());
    assertEquals("Only", processedTokens.get(1).word());
    assertEquals("this", processedTokens.get(2).word());
    assertEquals("part", processedTokens.get(3).word());
    assertEquals("Outro", processedTokens.get(4).word());
}
@Test
public void testHandlesMisplacedSentenceEndTags() {
    Properties props = new Properties();
    props.setProperty("clean.sentenceendingtags", "p");
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(props);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("<p>");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("First");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("sentence");
    CoreLabel token4 = new CoreLabel();
    token4.setWord("<p>");
    CoreLabel token5 = new CoreLabel();
    token5.setWord("Invalid");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5);
    List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

    assertEquals(3, processedTokens.size());
    assertTrue(processedTokens.get(1).containsKey(CoreAnnotations.ForcedSentenceEndAnnotation.class));
}
@Test
public void testHandlesEmptyInputTokens() {
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();
    
    List<CoreLabel> tokens = Collections.emptyList();
    List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

    assertTrue("Processed tokens should be empty for an empty input list", processedTokens.isEmpty());
}
@Test
public void testHandlesNullInputTokens() {
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();

    try {
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(null);
        assertNotNull("Processed tokens should not be null even when given null", processedTokens);
        assertTrue("Processed tokens should be empty when given null", processedTokens.isEmpty());
    } catch (Exception e) {
        fail("Processing null tokens should not throw an exception.");
    }
}
@Test
public void testHandlesInlineXmlWithNoWhitespace() {
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("<b>Bold</b>");

    List<CoreLabel> tokens = Arrays.asList(token1);
    List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

    assertEquals(1, processedTokens.size());
    assertEquals("Bold", processedTokens.get(0).word());
}
@Test
public void testHandlesMalformedXmlEntitiesGracefully() {
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("&invalid;");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("<p>");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("ValidText");
    CoreLabel token4 = new CoreLabel();
    token4.setWord("</p>");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4);
    List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

    assertEquals(1, processedTokens.size());
    assertEquals("ValidText", processedTokens.get(0).word());
}
@Test
public void testHandlesMultipleConsecutiveTags() {
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("<b>");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("<i>");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("Text");
    CoreLabel token4 = new CoreLabel();
    token4.setWord("</i>");
    CoreLabel token5 = new CoreLabel();
    token5.setWord("</b>");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5);
    List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

    assertEquals(1, processedTokens.size());
    assertEquals("Text", processedTokens.get(0).word());
}
@Test
public void testHandlesUnmatchedEndTags() {
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();
    
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Content");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("</invalid>");

    List<CoreLabel> tokens = Arrays.asList(token1, token2);

    try {
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);
        assertEquals(1, processedTokens.size());
        assertEquals("Content", processedTokens.get(0).word());
    } catch (Exception e) {
        fail("Unmatched end tags should not cause exceptions.");
    }
}
@Test
public void testHandlesXmlAttributesCorrectly() {
    Properties props = new Properties();
    props.setProperty("clean.tokenAnnotations", "link=a[href]");
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(props);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("<a href=\"https://example.com\">");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("Click");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("</a>");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);
    List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

    assertEquals(1, processedTokens.size());
    assertEquals("Click", processedTokens.get(0).word());
    assertNotNull("Token should have a link annotation", processedTokens.get(0).get(CoreAnnotations.LinkAnnotation.class));
}
@Test
public void testHandlesMixedNestedInlineTags() {
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("<p>");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("<b>");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("Nested");
    CoreLabel token4 = new CoreLabel();
    token4.setWord("</b>");
    CoreLabel token5 = new CoreLabel();
    token5.setWord("<i>");
    CoreLabel token6 = new CoreLabel();
    token6.setWord("text");
    CoreLabel token7 = new CoreLabel();
    token7.setWord("</i>");
    CoreLabel token8 = new CoreLabel();
    token8.setWord("</p>");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5, token6, token7, token8);
    List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

    assertEquals(2, processedTokens.size());
    assertEquals("Nested", processedTokens.get(0).word());
    assertEquals("text", processedTokens.get(1).word());
}
@Test
public void testHandlesContentWithSpecialCharacters() {
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Content:");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("<p>");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("<em>");
    CoreLabel token4 = new CoreLabel();
    token4.setWord("&amp;SpecialChar&copy;");
    CoreLabel token5 = new CoreLabel();
    token5.setWord("</em>");
    CoreLabel token6 = new CoreLabel();
    token6.setWord("</p>");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5, token6);
    List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

    assertEquals(2, processedTokens.size());
    assertEquals("Content:", processedTokens.get(0).word());
    assertEquals("&SpecialChar©", processedTokens.get(1).word());
}
@Test
public void testHandlesMultipleSequentialXmlBlocks() {
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("<p>");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("First");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("block");
    CoreLabel token4 = new CoreLabel();
    token4.setWord("</p>");
    CoreLabel token5 = new CoreLabel();
    token5.setWord("<p>");
    CoreLabel token6 = new CoreLabel();
    token6.setWord("Second");
    CoreLabel token7 = new CoreLabel();
    token7.setWord("block");
    CoreLabel token8 = new CoreLabel();
    token8.setWord("</p>");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5, token6, token7, token8);
    List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

    assertEquals(4, processedTokens.size());
    assertEquals("First", processedTokens.get(0).word());
    assertEquals("block", processedTokens.get(1).word());
    assertEquals("Second", processedTokens.get(2).word());
    assertEquals("block", processedTokens.get(3).word());
}
@Test
public void testHandlesXmlWithNoTags() {
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Plain");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("text.");
    
    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

    assertEquals(2, processedTokens.size());
    assertEquals("Plain", processedTokens.get(0).word());
    assertEquals("text.", processedTokens.get(1).word());
}
@Test
public void testHandlesWhitespaceBetweenTags() {
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("<p>");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("  ");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("Hello");
    CoreLabel token4 = new CoreLabel();
    token4.setWord("World");
    CoreLabel token5 = new CoreLabel();
    token5.setWord("</p>");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5);
    List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

    assertEquals(2, processedTokens.size());
    assertEquals("Hello", processedTokens.get(0).word());
    assertEquals("World", processedTokens.get(1).word());
}
@Test
public void testHandlesMultipleSequentialTagsWithoutContent() {
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("<p>");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("<b>");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("</b>");
    CoreLabel token4 = new CoreLabel();
    token4.setWord("</p>");
    CoreLabel token5 = new CoreLabel();
    token5.setWord("Content");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5);
    List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

    assertEquals(1, processedTokens.size());
    assertEquals("Content", processedTokens.get(0).word());
}
@Test
public void testHandlesNestedTagsWithTextAtDifferentLevels() {
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("<p>");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("Outer");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("<b>");
    CoreLabel token4 = new CoreLabel();
    token4.setWord("Inner");
    CoreLabel token5 = new CoreLabel();
    token5.setWord("</b>");
    CoreLabel token6 = new CoreLabel();
    token6.setWord("</p>");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5, token6);
    List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

    assertEquals(2, processedTokens.size());
    assertEquals("Outer", processedTokens.get(0).word());
    assertEquals("Inner", processedTokens.get(1).word());
}
@Test
public void testHandlesMixedTextAndXmlTags() {
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Start");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("<p>");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("Middle");
    CoreLabel token4 = new CoreLabel();
    token4.setWord("</p>");
    CoreLabel token5 = new CoreLabel();
    token5.setWord("End");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5);
    List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

    assertEquals(3, processedTokens.size());
    assertEquals("Start", processedTokens.get(0).word());
    assertEquals("Middle", processedTokens.get(1).word());
    assertEquals("End", processedTokens.get(2).word());
}
@Test
public void testHandlesXmlWithOnlyTagsAndNoText() {
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("<p>");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("<b>");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("</b>");
    CoreLabel token4 = new CoreLabel();
    token4.setWord("</p>");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4);
    List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

    assertEquals(0, processedTokens.size());
}
@Test
public void testProcessesSingleSentenceTagCorrectly() {
    Properties props = new Properties();
    props.setProperty("clean.singlesentencetags", "title");
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(props);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("<title>");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("Single");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("sentence");
    CoreLabel token4 = new CoreLabel();
    token4.setWord("</title>");
    CoreLabel token5 = new CoreLabel();
    token5.setWord("Second");
    CoreLabel token6 = new CoreLabel();
    token6.setWord("sentence");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5, token6);
    List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

    assertEquals(4, processedTokens.size());
    assertTrue(processedTokens.get(1).containsKey(CoreAnnotations.ForcedSentenceUntilEndAnnotation.class));
}
@Test
public void testProcessesSpeakerAnnotationCorrectly() {
    Properties props = new Properties();
    props.setProperty("clean.speakertags", "speaker");
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(props);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("<speaker>");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("John");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("Doe");
    CoreLabel token4 = new CoreLabel();
    token4.setWord("</speaker>");
    CoreLabel token5 = new CoreLabel();
    token5.setWord("says");
    CoreLabel token6 = new CoreLabel();
    token6.setWord("hello");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5, token6);
    List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

    assertEquals(4, processedTokens.size());
    assertEquals("says", processedTokens.get(2).word());
    assertEquals("hello", processedTokens.get(3).word());
    assertEquals("John Doe", processedTokens.get(1).get(CoreAnnotations.SpeakerAnnotation.class));
}
@Test
public void testProcessesMalformedXmlGracefully() {
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("<p");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("Malformed");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("</p>");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);

    try {
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);
        assertEquals(1, processedTokens.size());
        assertEquals("Malformed", processedTokens.get(0).word());
    } catch (Exception e) {
        fail("Malformed XML structures should be handled gracefully.");
    }
}
@Test
public void testHandlesMultipleNestedTagsWithSameName() {
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("<div>");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("<div>");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("Nested");
    CoreLabel token4 = new CoreLabel();
    token4.setWord("</div>");
    CoreLabel token5 = new CoreLabel();
    token5.setWord("</div>");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5);
    List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

    assertEquals(1, processedTokens.size());
    assertEquals("Nested", processedTokens.get(0).word());
}
@Test
public void testHandlesSingleCharacterXmlTags() {
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("<x>");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("Text");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("</x>");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);
    List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

    assertEquals(1, processedTokens.size());
    assertEquals("Text", processedTokens.get(0).word());
}
@Test
public void testHandlesAttributesInsideTagsButIgnoresThemInProcessing() {
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("<p class=\"paragraph\">");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("Content");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("</p>");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);
    List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

    assertEquals(1, processedTokens.size());
    assertEquals("Content", processedTokens.get(0).word());
}
@Test
public void testProcessesXmlWithUnknownTags() {
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("<unknown>");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("Text inside unknown tag");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("</unknown>");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);
    List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

    assertEquals(1, processedTokens.size());
    assertEquals("Text inside unknown tag", processedTokens.get(0).word());
}
@Test
public void testHandlesTagsWithNoEndingPair() {
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();
    
    CoreLabel token1 = new CoreLabel();
    token1.setWord("BeforeTag");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("<unclosed>");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("AfterTag");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);
    List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

    assertEquals(2, processedTokens.size());
    assertEquals("BeforeTag", processedTokens.get(0).word());
    assertEquals("AfterTag", processedTokens.get(1).word());
}
@Test
public void testHandlesIncorrectlyNestedTags() {
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("<a>");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("<b>");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("Incorrect nesting");
    CoreLabel token4 = new CoreLabel();
    token4.setWord("</a>");
    CoreLabel token5 = new CoreLabel();
    token5.setWord("</b>");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5);

    try {
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);
        assertEquals(1, processedTokens.size());
        assertEquals("Incorrect nesting", processedTokens.get(0).word());
    } catch (Exception e) {
        fail("Incorrectly nested tags should be handled gracefully.");
    }
}
@Test
public void testHandlesSelfClosingTagsWithTextFollowing() {
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();
    
    CoreLabel token1 = new CoreLabel();
    token1.setWord("<br/>");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("Next Sentence");

    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

    assertEquals(1, processedTokens.size());
    assertEquals("Next Sentence", processedTokens.get(0).word());
}
@Test
public void testHandlesMultipleDifferentSentenceEndingTags() {
    Properties props = new Properties();
    props.setProperty("clean.sentenceendingtags", "p,div");
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(props);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Sentence");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("<p>");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("Another");
    CoreLabel token4 = new CoreLabel();
    token4.setWord("<div>");
    CoreLabel token5 = new CoreLabel();
    token5.setWord("Final");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5);
    List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

    assertTrue(processedTokens.get(0).containsKey(CoreAnnotations.ForcedSentenceEndAnnotation.class));
    assertTrue(processedTokens.get(2).containsKey(CoreAnnotations.ForcedSentenceEndAnnotation.class));
}
@Test
public void testHandlesMultipleConsecutiveSelfClosingTags() {
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("<br/>");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("<hr/>");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("Text after");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);
    List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

    assertEquals(1, processedTokens.size());
    assertEquals("Text after", processedTokens.get(0).word());
}
@Test
public void testHandlesDifferentNamespaceXmlTags() {
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("<ns:tag>");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("Namespaced Content");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("</ns:tag>");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);
    List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

    assertEquals(1, processedTokens.size());
    assertEquals("Namespaced Content", processedTokens.get(0).word());
}
@Test
public void testHandlesTagsWithNewlineCharactersInside() {
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("<p>\n");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("Content");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("\n</p>");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);
    List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

    assertEquals(1, processedTokens.size());
    assertEquals("Content", processedTokens.get(0).word());
}
@Test
public void testHandlesSelfClosingXmlTagWithAttributes() {
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("<img src='image.png'/>");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("Caption");

    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

    assertEquals(1, processedTokens.size());
    assertEquals("Caption", processedTokens.get(0).word());
}
@Test
public void testHandlesUnmatchedOpeningAndClosingTags() {
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("<wrong>");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("Content");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("</mismatched>");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);

    try {
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);
        assertEquals(1, processedTokens.size());
        assertEquals("Content", processedTokens.get(0).word());
    } catch (Exception e) {
        fail("Mismatched opening/closing tags should not throw exceptions.");
    }
}
@Test
public void testRemovesSequentialXmlTagsWithoutText() {
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("<title>");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("<meta>");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("</meta>");
    CoreLabel token4 = new CoreLabel();
    token4.setWord("</title>");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4);
    List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

    assertTrue(processedTokens.isEmpty());
}
@Test
public void testHandlesMalformedXmlWithMissingAngles() {
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Missing <tag text");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("Valid content");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("</tag>");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);

    List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);
    assertEquals(1, processedTokens.size());
    assertEquals("Valid content", processedTokens.get(0).word());
}
@Test
public void testHandlesMultipleAttributesInTag() {
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("<a href='link.html' target='_blank'>");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("Anchor Text");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("</a>");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);
    List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

    assertEquals(1, processedTokens.size());
    assertEquals("Anchor Text", processedTokens.get(0).word());
}
@Test
public void testHandlesTagsWithSpecialCharactersInAttributes() {
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("<meta content='Some \"quoted\" text'/>");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("Following text");

    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

    assertEquals(1, processedTokens.size());
    assertEquals("Following text", processedTokens.get(0).word());
}
@Test
public void testHandlesMultipleIntersectingTags() {
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("<p>");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("<b>");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("Bolded text");
    CoreLabel token4 = new CoreLabel();
    token4.setWord("</p>");
    CoreLabel token5 = new CoreLabel();
    token5.setWord("</b>");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5);
    List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

    assertEquals(1, processedTokens.size());
    assertEquals("Bolded text", processedTokens.get(0).word());
}
@Test
public void testHandlesXmlTagsWithinWords() {
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Hello<p>world</p>!");

    List<CoreLabel> tokens = Arrays.asList(token1);
    List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

    assertEquals(1, processedTokens.size());
    assertEquals("Hello world!", processedTokens.get(0).word());
}
@Test
public void testHandlesTextWithNoXmlTags() {
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Plain");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("text.");
    
    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

    assertEquals(2, processedTokens.size());
    assertEquals("Plain", processedTokens.get(0).word());
    assertEquals("text.", processedTokens.get(1).word());
}
@Test
public void testHandlesDuplicatedNestedTags() {
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("<b>");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("<b>");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("Bold Text");
    CoreLabel token4 = new CoreLabel();
    token4.setWord("</b>");
    CoreLabel token5 = new CoreLabel();
    token5.setWord("</b>");
    
    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5);
    List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

    assertEquals(1, processedTokens.size());
    assertEquals("Bold Text", processedTokens.get(0).word());
}
@Test
public void testHandlesTagsWithSpecialCharacters() {
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();
    
    CoreLabel token1 = new CoreLabel();
    token1.setWord("<meta data=\"test&amp;value\"/>");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("Following text");

    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);
    
    assertEquals(1, processedTokens.size());
    assertEquals("Following text", processedTokens.get(0).word());
}
@Test
public void testHandlesXmlTagsInsideWords() {
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();
    
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Hello<p>World!</p>");

    List<CoreLabel> tokens = Arrays.asList(token1);
    List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);
    
    assertEquals(1, processedTokens.size());
    assertEquals("Hello World!", processedTokens.get(0).word());
}
@Test
public void testHandlesTagsSpanningMultipleLines() {
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();
    
    CoreLabel token1 = new CoreLabel();
    token1.setWord("<p>");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("First line");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("Second line");
    CoreLabel token4 = new CoreLabel();
    token4.setWord("</p>");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4);
    List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);
    
    assertEquals(2, processedTokens.size());
    assertEquals("First line", processedTokens.get(0).word());
    assertEquals("Second line", processedTokens.get(1).word());
}
@Test
public void testHandlesClosingTagWithoutOpeningTag() {
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();
    
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Text before");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("</unexpected>");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("Text after");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);

    try {
        List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);
        assertEquals(2, processedTokens.size());
        assertEquals("Text before", processedTokens.get(0).word());
        assertEquals("Text after", processedTokens.get(1).word());
    } catch (Exception e) {
        fail("Unexpected closing tag should not cause an exception.");
    }
}
@Test
public void testRemovesMultipleXmlTagsWithNoTextBetween() {
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();
    
    CoreLabel token1 = new CoreLabel();
    token1.setWord("<header>");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("<meta>");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("</meta>");
    CoreLabel token4 = new CoreLabel();
    token4.setWord("</header>");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4);
    List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

    assertTrue(processedTokens.isEmpty());
}
@Test
public void testHandlesSelfClosingTagsInsideContent() {
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();
    
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Before image");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("<img src=\"image.png\"/>");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("After image");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);
    List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

    assertEquals(2, processedTokens.size());
    assertEquals("Before image", processedTokens.get(0).word());
    assertEquals("After image", processedTokens.get(1).word());
}
@Test
public void testHandlesXmlInsideAttributeValues() {
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator();
    
    CoreLabel token1 = new CoreLabel();
    token1.setWord("<tag value='<nested>'>");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("Valid text");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("</tag>");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);
    List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

    assertEquals(1, processedTokens.size());
    assertEquals("Valid text", processedTokens.get(0).word());
}
@Test
public void testEnsuresCorrectSentenceSplittingWithMultipleEndingTags() {
    Properties props = new Properties();
    props.setProperty("clean.sentenceendingtags", "p,div");
    CleanXmlAnnotator cleanXmlAnnotator = new CleanXmlAnnotator(props);
    
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Sentence");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("<p>");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("Another");
    CoreLabel token4 = new CoreLabel();
    token4.setWord("<div>");
    CoreLabel token5 = new CoreLabel();
    token5.setWord("Final");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5);
    List<CoreLabel> processedTokens = cleanXmlAnnotator.process(tokens);

    assertTrue(processedTokens.get(0).containsKey(CoreAnnotations.ForcedSentenceEndAnnotation.class));
    assertTrue(processedTokens.get(2).containsKey(CoreAnnotations.ForcedSentenceEndAnnotation.class));
} 
}