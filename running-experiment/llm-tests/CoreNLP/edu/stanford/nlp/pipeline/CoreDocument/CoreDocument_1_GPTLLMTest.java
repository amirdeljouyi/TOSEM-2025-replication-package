package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;
import org.junit.Test;

import java.util.*;
import java.util.regex.Pattern;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CoreDocument_1_GPTLLMTest {

 @Test
    public void testConstructorWithText() {
        String documentText = "Alice lives in Wonderland.";
        CoreDocument coreDocument = new CoreDocument(documentText);
        
        assertNotNull(coreDocument.annotation());
        assertEquals(documentText, coreDocument.text());
    }
@Test
    public void testConstructorWithAnnotation() {
        String documentText = "Bob went to the market.";
        Annotation mockAnnotation = mock(Annotation.class);
        when(mockAnnotation.get(CoreAnnotations.TextAnnotation.class)).thenReturn(documentText);

        CoreDocument coreDocument = new CoreDocument(mockAnnotation);

        assertNotNull(coreDocument.annotation());
        assertEquals(documentText, coreDocument.text());
    }
@Test
    public void testWrapAnnotationsWithoutSentences() {
        Annotation mockAnnotation = mock(Annotation.class);
        when(mockAnnotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(null);
        
        CoreDocument coreDocument = new CoreDocument(mockAnnotation);
        coreDocument.wrapAnnotations();

        assertNull(coreDocument.sentences());
        assertNull(coreDocument.entityMentions());
        assertNull(coreDocument.quotes());
    }
@Test
    public void testWrapAnnotationsWithEmptySentences() {
        Annotation mockAnnotation = mock(Annotation.class);
        when(mockAnnotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Collections.emptyList());
        
        CoreDocument coreDocument = new CoreDocument(mockAnnotation);
        coreDocument.wrapAnnotations();

        assertNotNull(coreDocument.sentences());
        assertEquals(0, coreDocument.sentences().size());
    }
@Test
    public void testEmptyEntityMentionsWhenNoneArePresent() {
        Annotation mockAnnotation = mock(Annotation.class);
        when(mockAnnotation.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(null);
        
        CoreDocument coreDocument = new CoreDocument(mockAnnotation);
        coreDocument.wrapAnnotations();

        assertNull(coreDocument.entityMentions());
    }
@Test
    public void testDocIDRetrieval() {
        Annotation mockAnnotation = mock(Annotation.class);
        when(mockAnnotation.get(CoreAnnotations.DocIDAnnotation.class)).thenReturn("DOC1001");
        
        CoreDocument coreDocument = new CoreDocument(mockAnnotation);

        assertEquals("DOC1001", coreDocument.docID());
    }
@Test
    public void testDocDateRetrieval() {
        Annotation mockAnnotation = mock(Annotation.class);
        when(mockAnnotation.get(CoreAnnotations.DocDateAnnotation.class)).thenReturn("2024-06-12");
        
        CoreDocument coreDocument = new CoreDocument(mockAnnotation);

        assertEquals("2024-06-12", coreDocument.docDate());
    }
@Test
    public void testCorefChainsRetrieval() {
        Annotation mockAnnotation = mock(Annotation.class);
        Map<Integer, CorefChain> mockCorefChains = mock(Map.class);
        when(mockAnnotation.get(CorefCoreAnnotations.CorefChainAnnotation.class)).thenReturn(mockCorefChains);
        
        CoreDocument coreDocument = new CoreDocument(mockAnnotation);

        assertNotNull(coreDocument.corefChains());
        assertEquals(mockCorefChains, coreDocument.corefChains());
    }
@Test
    public void testTokensRetrieval() {
        Annotation mockAnnotation = mock(Annotation.class);
        List<CoreLabel> mockTokens = mock(List.class);
        when(mockAnnotation.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(mockTokens);
        
        CoreDocument coreDocument = new CoreDocument(mockAnnotation);

        assertNotNull(coreDocument.tokens());
        assertEquals(mockTokens, coreDocument.tokens());
    }
@Test
    public void testToStringMethod() {
        Annotation mockAnnotation = mock(Annotation.class);
        when(mockAnnotation.toString()).thenReturn("MockAnnotationContent");
        
        CoreDocument coreDocument = new CoreDocument(mockAnnotation);

        assertEquals("MockAnnotationContent", coreDocument.toString());
    }
@Test
    public void testConstructorWithNullText() {
        CoreDocument coreDocument = new CoreDocument((String) null);
        assertNotNull(coreDocument.annotation());
        assertNull(coreDocument.text());
    }
@Test
    public void testConstructorWithEmptyText() {
        CoreDocument coreDocument = new CoreDocument("");
        assertNotNull(coreDocument.annotation());
        assertEquals("", coreDocument.text());
    }
@Test
    public void testConstructorWithNullAnnotation() {
        CoreDocument coreDocument = new CoreDocument((Annotation) null);
        assertNotNull(coreDocument.annotation());
        assertNull(coreDocument.text());
    }
@Test
    public void testWrapAnnotationsWithNullAnnotationDocument() {
        CoreDocument coreDocument = new CoreDocument((Annotation) null);
        coreDocument.wrapAnnotations();
        assertNull(coreDocument.sentences());
        assertNull(coreDocument.entityMentions());
        assertNull(coreDocument.quotes());
    }
@Test
    public void testWrapAnnotationsWithAnnotationThatLacksSentenceAnnotation() {
        Annotation mockAnnotation = mock(Annotation.class);
        when(mockAnnotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(null);

        CoreDocument coreDocument = new CoreDocument(mockAnnotation);
        coreDocument.wrapAnnotations();

        assertNull(coreDocument.sentences());
        assertNull(coreDocument.entityMentions());
        assertNull(coreDocument.quotes());
    }
@Test
    public void testWrapAnnotationsWithAnnotationThatHasEmptyMentions() {
        Annotation mockAnnotation = mock(Annotation.class);
        when(mockAnnotation.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.emptyList());

        CoreDocument coreDocument = new CoreDocument(mockAnnotation);
        coreDocument.wrapAnnotations();

        assertNotNull(coreDocument.entityMentions());
        assertTrue(coreDocument.entityMentions().isEmpty());
    }
@Test
    public void testCorefChainsWhenNoneArePresent() {
        Annotation mockAnnotation = mock(Annotation.class);
        when(mockAnnotation.get(CorefCoreAnnotations.CorefChainAnnotation.class)).thenReturn(null);

        CoreDocument coreDocument = new CoreDocument(mockAnnotation);
        assertNull(coreDocument.corefChains());
    }

@Test
    public void testTokensWhenNoneArePresent() {
        Annotation mockAnnotation = mock(Annotation.class);
        when(mockAnnotation.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(null);

        CoreDocument coreDocument = new CoreDocument(mockAnnotation);
        assertNull(coreDocument.tokens());
    }
@Test
    public void testToStringWithNullAnnotation() {
        CoreDocument coreDocument = new CoreDocument((Annotation) null);
        assertNotNull(coreDocument.toString());
        assertEquals("null", coreDocument.toString());
    }
@Test
    public void testConstructorWithWhitespacesOnlyText() {
        CoreDocument coreDocument = new CoreDocument("   ");
        assertNotNull(coreDocument.annotation());
        assertEquals("   ", coreDocument.text());
    }
@Test
    public void testWrapAnnotationsWithNullEntityMentions() {
        Annotation mockAnnotation = mock(Annotation.class);
        when(mockAnnotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Collections.emptyList());
        when(mockAnnotation.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(null);

        CoreDocument coreDocument = new CoreDocument(mockAnnotation);
        coreDocument.wrapAnnotations();

        assertNull(coreDocument.entityMentions());
    }
@Test
    public void testWrapAnnotationsWithNonNullEntityMentionsButNoSentences() {
        Annotation mockAnnotation = mock(Annotation.class);
        List<CoreEntityMention> mockEntityMentions = mock(List.class);
//        when(mockAnnotation.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(mockEntityMentions);

        CoreDocument coreDocument = new CoreDocument(mockAnnotation);
        coreDocument.wrapAnnotations();

        assertNotNull(coreDocument.entityMentions());
        assertEquals(mockEntityMentions, coreDocument.entityMentions());
    }
@Test
    public void testWrapAnnotationsWithEmptyQuotes() {
        Annotation mockAnnotation = mock(Annotation.class);
        when(mockAnnotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Collections.emptyList());
        when(QuoteAnnotator.gatherQuotes(mockAnnotation)).thenReturn(Collections.emptyList());

        CoreDocument coreDocument = new CoreDocument(mockAnnotation);
        coreDocument.wrapAnnotations();

        assertNotNull(coreDocument.quotes());
        assertTrue(coreDocument.quotes().isEmpty());
    }
@Test
    public void testCorefChainsWithEmptyCorefMap() {
        Annotation mockAnnotation = mock(Annotation.class);
        Map<Integer, CorefChain> mockCorefChains = Collections.emptyMap();
        when(mockAnnotation.get(CorefCoreAnnotations.CorefChainAnnotation.class)).thenReturn(mockCorefChains);

        CoreDocument coreDocument = new CoreDocument(mockAnnotation);
        assertNotNull(coreDocument.corefChains());
        assertTrue(coreDocument.corefChains().isEmpty());
    }
@Test
    public void testTokensWithEmptyTokenList() {
        Annotation mockAnnotation = mock(Annotation.class);
        when(mockAnnotation.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.emptyList());

        CoreDocument coreDocument = new CoreDocument(mockAnnotation);

        assertNotNull(coreDocument.tokens());
        assertTrue(coreDocument.tokens().isEmpty());
    }
@Test
    public void testToStringWithEmptyAnnotation() {
        Annotation mockAnnotation = mock(Annotation.class);
        when(mockAnnotation.toString()).thenReturn("");

        CoreDocument coreDocument = new CoreDocument(mockAnnotation);

        assertEquals("", coreDocument.toString());
    }
@Test
    public void testConstructorWithLongText() {
        String longText = new String(new char[10000]).replace("\0", "A");
        CoreDocument coreDocument = new CoreDocument(longText);

        assertNotNull(coreDocument.annotation());
        assertEquals(longText, coreDocument.text());
    }
@Test
    public void testWrapAnnotationsWithNullSentencesAnnotation() {
        Annotation mockAnnotation = mock(Annotation.class);
        when(mockAnnotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(null);

        CoreDocument coreDocument = new CoreDocument(mockAnnotation);
        coreDocument.wrapAnnotations();

        assertNull(coreDocument.sentences());
    }
@Test
    public void testWrapAnnotationsWithNonEmptySentencesButNullEntityMentions() {
        Annotation mockAnnotation = mock(Annotation.class);
        List<CoreSentence> mockSentences = mock(List.class);
//        when(mockAnnotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(mockSentences);
        when(mockSentences.size()).thenReturn(2);
        when(mockSentences.get(0).entityMentions()).thenReturn(null);

        CoreDocument coreDocument = new CoreDocument(mockAnnotation);
        coreDocument.wrapAnnotations();

        assertNotNull(coreDocument.sentences());
        assertEquals(mockSentences, coreDocument.sentences());
        assertNull(coreDocument.entityMentions());
    }
@Test
    public void testWrapAnnotationsWithEmptyEntityMentionsInFirstSentence() {
        Annotation mockAnnotation = mock(Annotation.class);
        List<CoreSentence> mockSentences = mock(List.class);
//        when(mockAnnotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(mockSentences);
        when(mockSentences.size()).thenReturn(1);
        when(mockSentences.get(0).entityMentions()).thenReturn(Collections.emptyList());

        CoreDocument coreDocument = new CoreDocument(mockAnnotation);
        coreDocument.wrapAnnotations();

        assertNotNull(coreDocument.sentences());
        assertNotNull(coreDocument.entityMentions());
        assertTrue(coreDocument.entityMentions().isEmpty());
    }
@Test
    public void testWrapAnnotationsWithNullQuotes() {
        Annotation mockAnnotation = mock(Annotation.class);
        when(mockAnnotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Collections.emptyList());
        when(QuoteAnnotator.gatherQuotes(mockAnnotation)).thenReturn(null);

        CoreDocument coreDocument = new CoreDocument(mockAnnotation);
        coreDocument.wrapAnnotations();

        assertNull(coreDocument.quotes());
    }
@Test
    public void testWrapAnnotationsWithEmptyQuoteList() {
        Annotation mockAnnotation = mock(Annotation.class);
        when(mockAnnotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Collections.emptyList());
        when(QuoteAnnotator.gatherQuotes(mockAnnotation)).thenReturn(Collections.emptyList());

        CoreDocument coreDocument = new CoreDocument(mockAnnotation);
        coreDocument.wrapAnnotations();

        assertNotNull(coreDocument.quotes());
        assertTrue(coreDocument.quotes().isEmpty());
    }
@Test
    public void testWrapAnnotationsWithQuotesButNullCoreMapQuote() {
        Annotation mockAnnotation = mock(Annotation.class);
        List<CoreMap> mockQuotes = mock(List.class);
        when(mockAnnotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Collections.emptyList());
        when(QuoteAnnotator.gatherQuotes(mockAnnotation)).thenReturn(mockQuotes);
        when(mockQuotes.stream()).thenReturn(null);

        CoreDocument coreDocument = new CoreDocument(mockAnnotation);
        coreDocument.wrapAnnotations();

        assertNull(coreDocument.quotes());
    }
@Test
    public void testCorefChainsWithMultipleEntries() {
        Annotation mockAnnotation = mock(Annotation.class);
        Map<Integer, CorefChain> mockCorefChains = mock(Map.class);
        when(mockCorefChains.isEmpty()).thenReturn(false);
        when(mockCorefChains.size()).thenReturn(3);
        when(mockAnnotation.get(CorefCoreAnnotations.CorefChainAnnotation.class)).thenReturn(mockCorefChains);

        CoreDocument coreDocument = new CoreDocument(mockAnnotation);
        assertNotNull(coreDocument.corefChains());
        assertEquals(3, coreDocument.corefChains().size());
    }
@Test
    public void testToStringWithNullAnnotationReference() {
        CoreDocument coreDocument = new CoreDocument((Annotation) null);
        assertNotNull(coreDocument.toString());
        assertEquals("null", coreDocument.toString());
    }
@Test
    public void testConstructorWithMixedWhitespaceText() {
        CoreDocument coreDocument = new CoreDocument("\t  \nAlice went to the park.  \n");
        assertNotNull(coreDocument.annotation());
        assertEquals("\t  \nAlice went to the park.  \n", coreDocument.text());
    }
@Test
    public void testWrapAnnotationsWhenSentencesAreNull() {
        Annotation mockAnnotation = mock(Annotation.class);
        when(mockAnnotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(null);

        CoreDocument coreDocument = new CoreDocument(mockAnnotation);
        coreDocument.wrapAnnotations();

        assertNull(coreDocument.sentences());
        assertNull(coreDocument.entityMentions());
        assertNull(coreDocument.quotes());
    }
@Test
    public void testWrapAnnotationsWithEmptySentencesAndNonNullMentions() {
        Annotation mockAnnotation = mock(Annotation.class);
        when(mockAnnotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Collections.emptyList());
        when(mockAnnotation.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(mock(List.class));

        CoreDocument coreDocument = new CoreDocument(mockAnnotation);
        coreDocument.wrapAnnotations();

        assertNotNull(coreDocument.entityMentions());
        assertTrue(coreDocument.entityMentions().isEmpty());
    }
@Test
    public void testWrapAnnotationsWithNullMentionsList() {
        Annotation mockAnnotation = mock(Annotation.class);
        when(mockAnnotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Collections.emptyList());
        when(mockAnnotation.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(null);

        CoreDocument coreDocument = new CoreDocument(mockAnnotation);
        coreDocument.wrapAnnotations();

        assertNull(coreDocument.entityMentions());
    }
@Test
    public void testWrapAnnotationsWithNullQuotesList() {
        Annotation mockAnnotation = mock(Annotation.class);
        when(mockAnnotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Collections.emptyList());
        when(QuoteAnnotator.gatherQuotes(mockAnnotation)).thenReturn(null);

        CoreDocument coreDocument = new CoreDocument(mockAnnotation);
        coreDocument.wrapAnnotations();

        assertNull(coreDocument.quotes());
    }
@Test
    public void testTokensReturnsEmptyListWhenNonePresent() {
        Annotation mockAnnotation = mock(Annotation.class);
        when(mockAnnotation.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.emptyList());

        CoreDocument coreDocument = new CoreDocument(mockAnnotation);

        assertNotNull(coreDocument.tokens());
        assertTrue(coreDocument.tokens().isEmpty());
    }
@Test
    public void testCorefChainsHandlesNullGracefully() {
        Annotation mockAnnotation = mock(Annotation.class);
        when(mockAnnotation.get(CorefCoreAnnotations.CorefChainAnnotation.class)).thenReturn(null);

        CoreDocument coreDocument = new CoreDocument(mockAnnotation);

        assertNull(coreDocument.corefChains());
    }
@Test
    public void testCorefChainsWithEmptyMap() {
        Annotation mockAnnotation = mock(Annotation.class);
        Map<Integer, CorefChain> mockCorefChains = Collections.emptyMap();
        when(mockAnnotation.get(CorefCoreAnnotations.CorefChainAnnotation.class)).thenReturn(mockCorefChains);

        CoreDocument coreDocument = new CoreDocument(mockAnnotation);

        assertNotNull(coreDocument.corefChains());
        assertTrue(coreDocument.corefChains().isEmpty());
    }
@Test
    public void testToStringReturnsExpectedValueForEmptyAnnotation() {
        Annotation mockAnnotation = mock(Annotation.class);
        when(mockAnnotation.toString()).thenReturn("");

        CoreDocument coreDocument = new CoreDocument(mockAnnotation);

        assertEquals("", coreDocument.toString());
    }
@Test
    public void testToStringReturnsExpectedValueForNonEmptyAnnotation() {
        Annotation mockAnnotation = mock(Annotation.class);
        when(mockAnnotation.toString()).thenReturn("SampleAnnotation");

        CoreDocument coreDocument = new CoreDocument(mockAnnotation);

        assertEquals("SampleAnnotation", coreDocument.toString());
    }
@Test
    public void testConstructorWithSpecialCharactersText() {
        CoreDocument coreDocument = new CoreDocument("Hello, world! 💡🚀 #NLP");
        assertNotNull(coreDocument.annotation());
        assertEquals("Hello, world! 💡🚀 #NLP", coreDocument.text());
    }
@Test
    public void testWrapAnnotationsWithNullSentencesButNonNullEntityMentions() {
        Annotation mockAnnotation = mock(Annotation.class);
        when(mockAnnotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(null);
        when(mockAnnotation.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(mock(List.class));

        CoreDocument coreDocument = new CoreDocument(mockAnnotation);
        coreDocument.wrapAnnotations();

        assertNull(coreDocument.sentences());
        assertNull(coreDocument.entityMentions());
    }
@Test
    public void testWrapAnnotationsWithSentencesButNoEntityMentionsInAnySentence() {
        Annotation mockAnnotation = mock(Annotation.class);
        List<CoreSentence> mockSentences = mock(List.class);
//        when(mockAnnotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(mockSentences);
        when(mockSentences.size()).thenReturn(1);
        when(mockSentences.get(0).entityMentions()).thenReturn(Collections.emptyList());

        CoreDocument coreDocument = new CoreDocument(mockAnnotation);
        coreDocument.wrapAnnotations();

        assertNotNull(coreDocument.sentences());
        assertNotNull(coreDocument.entityMentions());
        assertTrue(coreDocument.entityMentions().isEmpty());
    }
@Test
    public void testCorefChainsWithLargeCorefMap() {
        Annotation mockAnnotation = mock(Annotation.class);
        Map<Integer, CorefChain> mockCorefChains = mock(Map.class);
        when(mockCorefChains.isEmpty()).thenReturn(false);
        when(mockCorefChains.size()).thenReturn(100);

        when(mockAnnotation.get(CorefCoreAnnotations.CorefChainAnnotation.class)).thenReturn(mockCorefChains);

        CoreDocument coreDocument = new CoreDocument(mockAnnotation);
        assertNotNull(coreDocument.corefChains());
        assertEquals(100, coreDocument.corefChains().size());
    }
@Test
    public void testTokensRetrievalWhenTextContainsPunctuation() {
        Annotation mockAnnotation = mock(Annotation.class);
        List<CoreLabel> mockTokens = mock(List.class);
        when(mockAnnotation.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(mockTokens);

        CoreDocument coreDocument = new CoreDocument(mockAnnotation);

        assertNotNull(coreDocument.tokens());
        assertEquals(mockTokens, coreDocument.tokens());
    }
@Test
    public void testQuotesWithSingleQuoteDetected() {
        Annotation mockAnnotation = mock(Annotation.class);
        List<CoreQuote> mockQuotes = mock(List.class);
//        when(QuoteAnnotator.gatherQuotes(mockAnnotation)).thenReturn(mockQuotes);

        CoreDocument coreDocument = new CoreDocument(mockAnnotation);
        coreDocument.wrapAnnotations();

        assertNotNull(coreDocument.quotes());
        assertEquals(mockQuotes, coreDocument.quotes());
    }
@Test
    public void testToStringForLargeText() {
        String largeText = new String(new char[5000]).replace("\0", "A");
        Annotation mockAnnotation = mock(Annotation.class);
        when(mockAnnotation.toString()).thenReturn(largeText);

        CoreDocument coreDocument = new CoreDocument(mockAnnotation);

        assertEquals(largeText, coreDocument.toString());
    }
@Test
    public void testConstructorWithSingleCharacterText() {
        CoreDocument coreDocument = new CoreDocument("A");
        assertNotNull(coreDocument.annotation());
        assertEquals("A", coreDocument.text());
    }
@Test
    public void testWrapAnnotationsWithNullAnnotation() {
        CoreDocument coreDocument = new CoreDocument((Annotation) null);
        coreDocument.wrapAnnotations();

        assertNull(coreDocument.sentences());
        assertNull(coreDocument.entityMentions());
        assertNull(coreDocument.quotes());
    }
@Test
    public void testWrapAnnotationsWithMultipleSentencesButNoEntityMentions() {
        Annotation mockAnnotation = mock(Annotation.class);
        List<CoreSentence> mockSentences = mock(List.class);
//        when(mockAnnotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(mockSentences);

        when(mockSentences.size()).thenReturn(2);
        when(mockSentences.get(0).entityMentions()).thenReturn(null);
        when(mockSentences.get(1).entityMentions()).thenReturn(null);

        CoreDocument coreDocument = new CoreDocument(mockAnnotation);
        coreDocument.wrapAnnotations();

        assertNotNull(coreDocument.sentences());
        assertEquals(mockSentences, coreDocument.sentences());
        assertNull(coreDocument.entityMentions());
    }
@Test
    public void testWrapAnnotationsWithSentencesButEmptyEntityMentions() {
        Annotation mockAnnotation = mock(Annotation.class);
        List<CoreSentence> mockSentences = mock(List.class);
//        when(mockAnnotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(mockSentences);

        when(mockSentences.size()).thenReturn(1);
        when(mockSentences.get(0).entityMentions()).thenReturn(Collections.emptyList());

        CoreDocument coreDocument = new CoreDocument(mockAnnotation);
        coreDocument.wrapAnnotations();

        assertNotNull(coreDocument.sentences());
        assertNotNull(coreDocument.entityMentions());
        assertTrue(coreDocument.entityMentions().isEmpty());
    }
@Test
    public void testQuotesWithEmptyList() {
        Annotation mockAnnotation = mock(Annotation.class);
        when(mockAnnotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Collections.emptyList());
        when(QuoteAnnotator.gatherQuotes(mockAnnotation)).thenReturn(Collections.emptyList());

        CoreDocument coreDocument = new CoreDocument(mockAnnotation);
        coreDocument.wrapAnnotations();

        assertNotNull(coreDocument.quotes());
        assertTrue(coreDocument.quotes().isEmpty());
    }
@Test
    public void testToStringWhenAnnotationReturnsNull() {
        Annotation mockAnnotation = mock(Annotation.class);
        when(mockAnnotation.toString()).thenReturn(null);

        CoreDocument coreDocument = new CoreDocument(mockAnnotation);
        assertNotNull(coreDocument.toString());
        assertNull(coreDocument.toString());
    }
@Test
    public void testToStringForUnknownAnnotationType() {
        Annotation mockAnnotation = mock(Annotation.class);
        when(mockAnnotation.toString()).thenReturn("UnknownAnnotationType");

        CoreDocument coreDocument = new CoreDocument(mockAnnotation);
        assertEquals("UnknownAnnotationType", coreDocument.toString());
    } 
}