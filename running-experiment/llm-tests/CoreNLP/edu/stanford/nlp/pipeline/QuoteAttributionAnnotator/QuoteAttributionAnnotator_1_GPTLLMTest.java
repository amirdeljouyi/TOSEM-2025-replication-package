package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.util.Pair;
import org.junit.Test;
import static org.junit.Assert.*;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.util.CoreMap;

import java.util.*;

public class QuoteAttributionAnnotator_1_GPTLLMTest {

 @Test
    public void testAnnotatorInitialization() {
        Properties props = new Properties();
        props.setProperty("charactersPath", "test-data/characters.txt");
        props.setProperty("booknlpCoref", "test-data/booknlp_coref.txt");
        props.setProperty("QMSieves", "tri,dep");
        props.setProperty("MSSieves", "det,top");

        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

        assertNotNull(annotator);
        assertEquals("test-data/characters.txt", QuoteAttributionAnnotator.CHARACTERS_FILE);
        assertEquals("test-data/booknlp_coref.txt", QuoteAttributionAnnotator.COREF_PATH);
    }
@Test
    public void testAnnotateWithQuotes() {
        Properties props = new Properties();
        props.setProperty("charactersPath", "test-data/characters.txt");
        props.setProperty("booknlpCoref", "test-data/booknlp_coref.txt");

        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

        Annotation doc = new Annotation("He said, \"This is a test.\"");
        annotator.annotate(doc);

        assertNotNull(doc.get(CoreAnnotations.ParagraphIndexAnnotation.class));
        assertNotNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testEntityMentionsToCharacterMap() {
        Properties props = new Properties();

        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

        Annotation annotation = new Annotation("John spoke to Mary.");
        annotator.entityMentionsToCharacterMap(annotation);

    }
@Test
    public void testAnnotateWithoutCharactersFile() {
        Properties props = new Properties();
        props.setProperty("booknlpCoref", "test-data/booknlp_coref.txt");

        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

        Annotation doc = new Annotation("The professor said, \"Hello students.\"");
        annotator.annotate(doc);
    }
@Test
    public void testRequirementsSatisfied() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

        Set<Class<? extends CoreAnnotation>> requirements = annotator.requirementsSatisfied();

        assertTrue(requirements.contains(QuoteAttributionAnnotator.MentionAnnotation.class));
        assertTrue(requirements.contains(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testRequires() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

        Set<Class<? extends CoreAnnotation>> requires = annotator.requires();

        assertTrue(requires.contains(CoreAnnotations.TokensAnnotation.class));
        assertTrue(requires.contains(CoreAnnotations.SentencesAnnotation.class));
        assertFalse(requires.isEmpty());
    }
@Test
    public void testAnnotatorWithLongText() {
        Properties props = new Properties();
        props.setProperty("charactersPath", "test-data/characters.txt");

        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

        Annotation longText = new Annotation("Alice whispered, \"I think something is wrong.\" Then Bob replied, \"What do you mean?\"");
        annotator.annotate(longText);

        assertNotNull(longText.get(CoreAnnotations.TokensAnnotation.class));
        assertNotNull(longText.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testQuoteWithoutSpeaker() {
        Properties props = new Properties();

        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

        Annotation noSpeaker = new Annotation("\"This is an orphaned quote.\"");
        annotator.annotate(noSpeaker);

        assertNull(noSpeaker.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testQuoteAttributionWithIncorrectModelPath() {
        Properties props = new Properties();
        props.setProperty("modelPath", "incorrect/path/to/model.ser");

        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

        Annotation doc = new Annotation("Someone said, \"I am lost.\"");
        annotator.annotate(doc);

        assertNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testCoreferenceWithoutBookNLPFile() {
        Properties props = new Properties();
        props.setProperty("charactersPath", "test-data/characters.txt");

        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

        Annotation doc = new Annotation("James went home. Later, he said, \"I'm back.\"");
        annotator.annotate(doc);

        assertNotNull(doc.get(CoreAnnotations.TokensAnnotation.class));
    }
@Test
    public void testAnnotationWithEmptyText() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
        
        Annotation doc = new Annotation("");
        annotator.annotate(doc);
        
        assertNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithOnlyWhitespace() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
        
        Annotation doc = new Annotation("    ");
        annotator.annotate(doc);
        
        assertNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithMultipleQuotesSameSpeaker() {
        Properties props = new Properties();
        props.setProperty("charactersPath", "test-data/characters.txt");

        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

        Annotation doc = new Annotation("John said, \"Hello!\" Then he added, \"How are you?\"");
        annotator.annotate(doc);

        assertNotNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithNestedQuotes() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

        Annotation doc = new Annotation("John said, \"Mary told me, 'See you soon.'\"");
        annotator.annotate(doc);

        assertNotNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithAmbiguousSpeaker() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

        Annotation doc = new Annotation("\"This is unclear,\" said someone.");
        annotator.annotate(doc);

        assertNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testQuoteAttributionWithNonDialogText() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

        Annotation doc = new Annotation("The sky is blue. Birds are flying.");
        annotator.annotate(doc);

        assertNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithoutVerballyIndicatedSpeaker() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

        Annotation doc = new Annotation("\"A great day for a walk.\" He looked at the horizon.");
        annotator.annotate(doc);

        assertNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithQuotesWithoutSpeakers() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

        Annotation doc = new Annotation("\"Just some text.\" \"Another quote.\" \"More.\"");
        annotator.annotate(doc);

        assertNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testCharacterMapWithInvalidMentions() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

        Annotation annotation = new Annotation("Random objects without names: chair, book, tree.");
        annotator.entityMentionsToCharacterMap(annotation);
  }
@Test
    public void testAnnotationWithMultipleParagraphs() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

        Annotation doc = new Annotation("John said, \"This is great.\"\n\nMary responded, \"I agree.\"");
        annotator.annotate(doc);

        assertNotNull(doc.get(CoreAnnotations.ParagraphIndexAnnotation.class));
        assertNotNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithSingleWordQuote() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

        Annotation doc = new Annotation("She said, \"Hi.\"");
        annotator.annotate(doc);

        assertNotNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithQuoteAtEndOfSentenceWithoutSpeaker() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

        Annotation doc = new Annotation("\"This is a mysterious statement.\"");
        annotator.annotate(doc);

        assertNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithSpeakerAfterQuote() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

        Annotation doc = new Annotation("\"I love programming,\" John exclaimed.");
        annotator.annotate(doc);

        assertNotNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithInterruptedQuote() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

        Annotation doc = new Annotation("\"I think,\" she paused, \"this works.\"");
        annotator.annotate(doc);

        assertNotNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithMisformattedQuote() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

        Annotation doc = new Annotation("He said, \"Something went wrong.");
        annotator.annotate(doc);

        assertNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithMultipleSpeakersInOneSentence() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

        Annotation doc = new Annotation("Alice said, \"I will go.\" Bob replied, \"I'll stay.\"");
        annotator.annotate(doc);

        assertNotNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithRepeatedMentions() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

        Annotation doc = new Annotation("John said, \"I agree.\" Later, John said again, \"I still agree.\"");
        annotator.annotate(doc);

        assertNotNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithIdenticalQuotesDifferentSpeakers() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

        Annotation doc = new Annotation("Jane said, \"Hello there.\" Later, Mark also said, \"Hello there.\"");
        annotator.annotate(doc);

        assertNotNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithSpeakerWithoutQuote() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

        Annotation doc = new Annotation("James sat calmly. Then he walked away.");
        annotator.annotate(doc);

        assertNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithPronounAsMention() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
        
        Annotation doc = new Annotation("John was excited. He said, \"Let's go!\"");
        annotator.annotate(doc);

        assertNotNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithUnusualPunctuationInQuote() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
        
        Annotation doc = new Annotation("She shouted, \"Wait... what?!\"");
        annotator.annotate(doc);

        assertNotNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithSpeakerHavingNoDialog() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
        
        Annotation doc = new Annotation("Michael thought for a while. Then he nodded.");
        annotator.annotate(doc);

        assertNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithQuoteAtParagraphStart() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

        Annotation doc = new Annotation("\"This starts the paragraph,\" said Alice.");
        annotator.annotate(doc);

        assertNotNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithMultipleQuotesSameSentence() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

        Annotation doc = new Annotation("Bob said, \"I'll go,\" and then added, \"Wait, maybe not.\"");
        annotator.annotate(doc);

        assertNotNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithMultipleSpeakersDifferentParagraphs() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

        Annotation doc = new Annotation("Alice said, \"Let's begin.\"\n\nBob responded, \"I'm ready.\"");
        annotator.annotate(doc);

        assertNotNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
        assertNotNull(doc.get(CoreAnnotations.ParagraphIndexAnnotation.class));
    }
@Test
    public void testAnnotationWithQuoteContainingCommaWithin() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
        
        Annotation doc = new Annotation("Sam said, \"Well, I think this is correct.\"");
        annotator.annotate(doc);

        assertNotNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithNoNamedEntities() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

        Annotation doc = new Annotation("\"Yes,\" she said, looking at the sunset.");
        annotator.annotate(doc);

        assertNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithSpeakerMentionedAfterQuote() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
        
        Annotation doc = new Annotation("\"I disagree,\" said Emma.");
        annotator.annotate(doc);

        assertNotNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithQuoteContainingUnusualSymbols() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
        
        Annotation doc = new Annotation("Liam muttered, \"Are you sure? *sigh* Alright.\"");
        annotator.annotate(doc);

        assertNotNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testQuoteAttributionWithoutStandardDialogVerbs() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

        Annotation doc = new Annotation("\"This is important.\" Jake turned away.");
        annotator.annotate(doc);

        assertNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithFullyQuotedConversation() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

        Annotation doc = new Annotation("\"Where are we going?\" \"To the park.\" \"Sounds good.\"");
        annotator.annotate(doc);

        assertNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithQuotationInsideQuotation() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
        
        Annotation doc = new Annotation("Mark said, \"I heard Anne say, 'Let's leave now.'\"");
        annotator.annotate(doc);

        assertNotNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithQuoteAtEndOfDocument() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
        
        Annotation doc = new Annotation("She turned and whispered, \"Goodbye.\"");
        annotator.annotate(doc);

        assertNotNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithSingleCharacterQuote() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

        Annotation doc = new Annotation("Tom said, \"A.\"");
        annotator.annotate(doc);

        assertNotNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithQuoteButNoExplicitSpeaker() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

        Annotation doc = new Annotation("\"A mysterious voice whispered in the dark.\"");
        annotator.annotate(doc);

        assertNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithSpeakerMentionedLater() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

        Annotation doc = new Annotation("\"Follow me.\" The guide instructed firmly.");
        annotator.annotate(doc);

        assertNotNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithSpeakerUsingIndirectSpeech() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

        Annotation doc = new Annotation("Andrew said that he would join later.");
        annotator.annotate(doc);

        assertNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithMultiSentenceQuote() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

        Annotation doc = new Annotation("Lisa said, \"I can't believe this happened. This changes everything!\"");
        annotator.annotate(doc);

        assertNotNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithSpeakerNameEmbeddedInQuote() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

        Annotation doc = new Annotation("\"Well, Bob, I think we should go now,\" said Alice.");
        annotator.annotate(doc);

        assertNotNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithUnconventionalPunctuation() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

        Annotation doc = new Annotation("\"Wow... That was unexpected?!\" exclaimed Sarah.");
        annotator.annotate(doc);

        assertNotNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithNonDialogueTextContainingQuotes() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

        Annotation doc = new Annotation("The title of the book is \"The Catcher in the Rye\".");
        annotator.annotate(doc);

        assertNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithMultipleSpeakersAlternatingQuotes() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

        Annotation doc = new Annotation("Alice said, \"Let's go.\" Bob replied, \"I'm not ready yet.\"");
        annotator.annotate(doc);

        assertNotNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithParentheticalInterruptionInQuote() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

        Annotation doc = new Annotation("\"I can't believe it,\" she said, \"that this actually worked!\"");
        annotator.annotate(doc);

        assertNotNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithAmbiguousPronounReferences() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

        Annotation doc = new Annotation("John met with Peter. He said, \"This is important.\"");
        annotator.annotate(doc);

        assertNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithDialogueWithoutExplicitQuotationMarks() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

        Annotation doc = new Annotation("John muttered something about leaving soon.");
        annotator.annotate(doc);

        assertNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithQuoteContainingMultipleNames() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

        Annotation doc = new Annotation("\"I think James and Sarah both agree,\" said Michael.");
        annotator.annotate(doc);

        assertNotNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithUnfinishedQuote() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
        
        Annotation doc = new Annotation("She said, \"This is just the beginning");
        annotator.annotate(doc);

        assertNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithStandaloneInterjectionBeforeQuote() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
        
        Annotation doc = new Annotation("Wow! He said, \"This is amazing!\"");
        annotator.annotate(doc);

        assertNotNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithCompoundSentenceQuote() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
        
        Annotation doc = new Annotation("John said, \"I will go, but I might be late.\"");
        annotator.annotate(doc);

        assertNotNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithSpeakerAfterMultipleQuotes() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
        
        Annotation doc = new Annotation("\"Look out!\" \"Be careful!\" warned Alice.");
        annotator.annotate(doc);

        assertNotNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithSelfReferentialQuote() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
        
        Annotation doc = new Annotation("He muttered, \"I always say, 'Never give up.'\"");
        annotator.annotate(doc);

        assertNotNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithRedundantSpeakerQuote() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
        
        Annotation doc = new Annotation("Peter said, \"Peter will handle it.\"");
        annotator.annotate(doc);

        assertNotNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithNumericalQuote() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
        
        Annotation doc = new Annotation("He responded, \"42 is the answer.\"");
        annotator.annotate(doc);

        assertNotNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithMultipleSpeakersAlternatingSentences() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
        
        Annotation doc = new Annotation("Alice said, \"Let's leave.\" Then Bob said, \"Not yet.\"");
        annotator.annotate(doc);

        assertNotNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithSpeakerAfterLongNarration() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
        
        Annotation doc = new Annotation("The wind howled through the trees as the storm worsened. \"We need to move now,\" warned Jake.");
        annotator.annotate(doc);
        
        assertNotNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithSpeakerOmittedButImpliedContextually() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
        
        Annotation doc = new Annotation("\"Hurry!\" The footsteps grew louder.");
        annotator.annotate(doc);

        assertNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithQuoteContainingSpeakerNameNotInAttribution() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
        
        Annotation doc = new Annotation("\"Well, John, I don't agree with you,\" said Mark.");
        annotator.annotate(doc);

        assertNotNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithSentenceEndingInQuoteWithoutSpeaker() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
        
        Annotation doc = new Annotation("They all heard it: \"The storm is coming.\"");
        annotator.annotate(doc);

        assertNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithMultipleSpeakersSameQuote() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
        
        Annotation doc = new Annotation("Alice and Bob said together, \"We agree.\"");
        annotator.annotate(doc);

        assertNotNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithSpeakerUsingSelfReference() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
        
        Annotation doc = new Annotation("John declared, \"John always finishes his work on time.\"");
        annotator.annotate(doc);

        assertNotNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithQuoteHavingMultipleEmbeddedClauses() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
        
        Annotation doc = new Annotation("Kate said, \"If I recall, she told me, 'John said, \"We should leave now.\"'\"");
        annotator.annotate(doc);

        assertNotNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithQuoteContainingListOfItems() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
        
        Annotation doc = new Annotation("She listed, \"Apples, oranges, and bananas are available.\"");
        annotator.annotate(doc);

        assertNotNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithSpeakerAtParagraphEndAfterLongDescription() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
        
        Annotation doc = new Annotation("The night was cold, the wind howled, and silence filled the air.\n\nShe finally whispered, \"I'm scared.\"");
        annotator.annotate(doc);

        assertNotNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
        assertNotNull(doc.get(CoreAnnotations.ParagraphIndexAnnotation.class));
    }
@Test
    public void testAnnotationWithQuoteContainingMathExpression() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
        
        Annotation doc = new Annotation("He said, \"The result of 5 + 3 is 8.\"");
        annotator.annotate(doc);

        assertNotNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithQuoteContainingCodeSnippet() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
        
        Annotation doc = new Annotation("She explained, \"In Java, use System.out.println(\"Hello\"); to print.\"");
        annotator.annotate(doc);

        assertNotNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithQuoteSplitByNarration() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
        
        Annotation doc = new Annotation("\"This,\" he said, \"is unexpected.\"");
        annotator.annotate(doc);

        assertNotNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithMultipleConsecutiveQuotesByDifferentSpeakers() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

        Annotation doc = new Annotation("\"We should go now,\" said Tom. \"No, let's wait,\" replied Anna.");
        annotator.annotate(doc);

        assertNotNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    }
@Test
    public void testAnnotationWithQuoteContainingForeignLanguagePhrase() {
        Properties props = new Properties();
        QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

        Annotation doc = new Annotation("She greeted, \"Bonjour! How are you?\"");
        annotator.annotate(doc);

        assertNotNull(doc.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    } 
}