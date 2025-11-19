package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.Timex;
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.Pair;
import org.junit.Test;

import java.util.*;
import java.util.function.Predicate;

import static org.junit.Assert.*;

public class MorphaAnnotator_1_GPTLLMTest {

 @Test
    public void testLemmaAnnotationSimpleVerb() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();

        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, "running");
        token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBG");

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token);

        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);

        assertEquals("run", token.get(CoreAnnotations.LemmaAnnotation.class));
    }
@Test
    public void testLemmaAnnotationNoun() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();

        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, "cats");
        token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNS");

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token);

        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);

        assertEquals("cat", token.get(CoreAnnotations.LemmaAnnotation.class));
    }
@Test
    public void testLemmaAnnotationPhrasalVerb() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();

        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, "take_off");
        token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token);

        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);

        assertEquals("take_off", token.get(CoreAnnotations.LemmaAnnotation.class));
    }
@Test
    public void testLemmaAnnotationUnknownPOS() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();

        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, "xenon");
        token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "ZZZ");

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token);

        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);

        assertNotNull(token.get(CoreAnnotations.LemmaAnnotation.class));
    }
@Test
    public void testHandlingEmptyAnnotation() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();

        try {
            annotator.annotate(annotation);
            fail("Expected RuntimeException due to missing SentencesAnnotation");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Unable to find words/tokens"));
        }
    }
@Test
    public void testHandlingMissingTokenKey() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();
        CoreMap sentence = new CoreLabel();

        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        try {
            annotator.annotate(annotation);
            fail("Expected NullPointerException due to missing TokensAnnotation");
        } catch (NullPointerException e) {
            
        }
    }
@Test
    public void testMultipleTokensAnnotation() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();

        CoreLabel token1 = new CoreLabel();
        token1.set(CoreAnnotations.TextAnnotation.class, "running");
        token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBG");

        CoreLabel token2 = new CoreLabel();
        token2.set(CoreAnnotations.TextAnnotation.class, "dogs");
        token2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNS");

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token1);
        tokens.add(token2);

        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);

        assertEquals("run", token1.get(CoreAnnotations.LemmaAnnotation.class));
        assertEquals("dog", token2.get(CoreAnnotations.LemmaAnnotation.class));
    }
@Test
    public void testVerboseModeLogging() {
        MorphaAnnotator annotator = new MorphaAnnotator(true);
        Annotation annotation = new Annotation();

        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, "testing");
        token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBG");

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token);

        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);
    }
@Test
    public void testPhrasalVerbLemmaWithInvalidParticle() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();

        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, "get_awayz");
        token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token);

        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);

        assertNull(token.get(CoreAnnotations.LemmaAnnotation.class));
    }
@Test
    public void testLemmaAnnotationSingleCharacterWord() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();

        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, "a");
        token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token);

        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);
        
        assertEquals("a", token.get(CoreAnnotations.LemmaAnnotation.class));
    }
@Test
    public void testLemmaAnnotationUpperCaseWord() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();

        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, "RUNNING");
        token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBG");

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token);

        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);
        
        assertEquals("RUNNING", token.get(CoreAnnotations.LemmaAnnotation.class));  
    }
@Test
    public void testLemmaAnnotationWithEmptyString() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();

        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, "");
        token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token);

        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);

        assertNull(token.get(CoreAnnotations.LemmaAnnotation.class));  
    }
@Test
    public void testLemmaAnnotationOnlyPOSWithoutText() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();

        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");  

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token);

        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
        
        annotator.annotate(annotation);

        assertNull(token.get(CoreAnnotations.LemmaAnnotation.class));  
    }
@Test
    public void testLemmaAnnotationWithMultipleSpacesInText() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();

        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, "  walking  ");
        token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBG");

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token);

        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);

        assertEquals("walk", token.get(CoreAnnotations.LemmaAnnotation.class));
    }
@Test
    public void testLemmaAnnotationWithoutPOS() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();

        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, "running");  

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token);

        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);

        assertNotNull(token.get(CoreAnnotations.LemmaAnnotation.class));  
    }
@Test
    public void testLemmaAnnotationWithPhrasalVerbMixedCase() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();

        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, "Give_Up");
        token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token);

        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);

        assertEquals("Give_up", token.get(CoreAnnotations.LemmaAnnotation.class));
    }
@Test
    public void testLemmaAnnotationForNonExistentPhrasalVerb() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();

        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, "run_downhill");
        token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token);

        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);

        assertNull(token.get(CoreAnnotations.LemmaAnnotation.class)); 
    }
@Test
    public void testLemmaAnnotationForeignCharacterInput() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();

        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, "ありがとう");
        token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token);

        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);

        assertEquals("ありがとう", token.get(CoreAnnotations.LemmaAnnotation.class));  
    }
@Test
    public void testLemmaAnnotationWithNullTextField() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();

        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, null);
        token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token);

        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        try {
            annotator.annotate(annotation);
            fail("Expected NullPointerException due to null text field");
        } catch (NullPointerException e) {
            assertTrue(true);
        }
    }
@Test
    public void testLemmaAnnotationWithOnlySpaces() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();

        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, "   ");  
        token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token);
        
        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
        
        annotator.annotate(annotation);

        assertNull(token.get(CoreAnnotations.LemmaAnnotation.class));  
    }
@Test
    public void testEmptyTokenListHandling() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();

        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());

        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);

        assertTrue(sentence.get(CoreAnnotations.TokensAnnotation.class).isEmpty());
    }
@Test
    public void testSingleTokenWithoutAnyAnnotationFields() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();
        
        CoreLabel token = new CoreLabel();  

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token);

        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
        
        annotator.annotate(annotation);

        assertNull(token.get(CoreAnnotations.LemmaAnnotation.class));  
    }
@Test
    public void testPhrasalVerbWithInvalidSeparator() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();
        
        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, "break-up");  
        token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token);
        
        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
        
        annotator.annotate(annotation);
        
        assertNotEquals("break_up", token.get(CoreAnnotations.LemmaAnnotation.class));
    }
@Test
    public void testTokenWithWhitespaceOnlyPOSAnnotation() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();
        
        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, "running");  
        token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "   ");

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token);
        
        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
        
        annotator.annotate(annotation);

        assertEquals("running", token.get(CoreAnnotations.LemmaAnnotation.class));  
    }
@Test
    public void testMultiplePhrasalVerbsHandling() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();
        
        CoreLabel token1 = new CoreLabel();
        token1.set(CoreAnnotations.TextAnnotation.class, "look_up");
        token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

        CoreLabel token2 = new CoreLabel();
        token2.set(CoreAnnotations.TextAnnotation.class, "give_in");
        token2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token1);
        tokens.add(token2);

        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);

        assertEquals("look_up", token1.get(CoreAnnotations.LemmaAnnotation.class));
        assertEquals("give_in", token2.get(CoreAnnotations.LemmaAnnotation.class));
    }
@Test
    public void testHandlingOfNumbersAsTokens() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();
        
        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, "12345");  
        token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "CD");

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token);
        
        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
        
        annotator.annotate(annotation);

        assertEquals("12345", token.get(CoreAnnotations.LemmaAnnotation.class));  
    }
@Test
    public void testHandlingOfSpecialCharactersAsTokens() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();
        
        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, "&^%$#@!");  
        token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "SYM");

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token);
        
        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
        
        annotator.annotate(annotation);

        assertEquals("&^%$#@!", token.get(CoreAnnotations.LemmaAnnotation.class));  
    }
@Test
    public void testLemmaAnnotationWithNullPOSField() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();

        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, "running");
        token.set(CoreAnnotations.PartOfSpeechAnnotation.class, null); 

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token);

        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);

        assertNotNull(token.get(CoreAnnotations.LemmaAnnotation.class)); 
    }
@Test
    public void testLemmaAnnotationWithLongWord() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();

        String longWord = "supercalifragilisticexpialidocious";  
        
        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, longWord);
        token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token);

        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);
        
        assertEquals(longWord, token.get(CoreAnnotations.LemmaAnnotation.class));  
    }
@Test
    public void testHandlingOfTokenWithOnlyPunctuation() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();

        String punctuationToken = "!!!"; 

        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, punctuationToken);
        token.set(CoreAnnotations.PartOfSpeechAnnotation.class, ".");

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token);
        
        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);
        
        assertEquals(punctuationToken, token.get(CoreAnnotations.LemmaAnnotation.class));  
    }
@Test
    public void testLemmaAnnotationWithMixedCasePhrasalVerb() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();

        String mixedCasePhrasalVerb = "Look_Up";

        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, mixedCasePhrasalVerb);
        token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token);
        
        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
        
        annotator.annotate(annotation);

        assertEquals("Look_up", token.get(CoreAnnotations.LemmaAnnotation.class));  
    }
@Test
    public void testLemmaAnnotationHandlingOfSymbolsMixedWithLetters() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();

        String symbolText = "hello@world";

        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, symbolText);
        token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token);
        
        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
        
        annotator.annotate(annotation);

        assertEquals(symbolText, token.get(CoreAnnotations.LemmaAnnotation.class));  
    }
@Test
    public void testLemmaAnnotationForTokenContainingDigits() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();
        
        String wordWithDigits = "run123";

        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, wordWithDigits);
        token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token);

        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);

        assertEquals(wordWithDigits, token.get(CoreAnnotations.LemmaAnnotation.class));  
    }
@Test
    public void testEmptySentenceAnnotation() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();
        
        annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());
        
        try {
            annotator.annotate(annotation);
            fail("Expected RuntimeException due to empty sentence annotation");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Unable to find words/tokens"));
        }
    }
@Test
    public void testPhrasalVerbWithUnexpectedTokenStructure() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();
        
        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, "check_this_out");
        token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token);
        
        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);

        assertNull(token.get(CoreAnnotations.LemmaAnnotation.class));  
    }
@Test
    public void testWhitespaceOnlyText() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();

        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, "   "); 
        token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token);
        
        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
        
        annotator.annotate(annotation);

        assertNull(token.get(CoreAnnotations.LemmaAnnotation.class));  
    }
@Test
    public void testTokenWithOnlyNumbers() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();

        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, "12345");
        token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "CD");

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token);
        
        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
        
        annotator.annotate(annotation);

        assertEquals("12345", token.get(CoreAnnotations.LemmaAnnotation.class));  
    }
@Test
    public void testTokenWithSpecialCharacters() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();
        
        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, "@#$%");
        token.set(CoreAnnotations.PartOfSpeechAnnotation.class, ".");

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token);
        
        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
        
        annotator.annotate(annotation);

        assertEquals("@#$%", token.get(CoreAnnotations.LemmaAnnotation.class));  
    }
@Test
    public void testTokenWithoutTextButWithPOS() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();
        
        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");  

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token);
        
        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
        
        annotator.annotate(annotation);

        assertNull(token.get(CoreAnnotations.LemmaAnnotation.class));  
    }
@Test
    public void testEmptyTokenList() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();
        
        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
        
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);

        assertTrue(sentence.get(CoreAnnotations.TokensAnnotation.class).isEmpty());
    }
@Test
    public void testTokenWithMixedLettersAndNumbers() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();
        
        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, "run123");
        token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token);

        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);

        assertEquals("run123", token.get(CoreAnnotations.LemmaAnnotation.class));  
    }
@Test
    public void testLongToken() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();
        
        String longWord = "supercalifragilisticexpialidocious";  

        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, longWord);
        token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token);

        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);
        
        assertEquals(longWord, token.get(CoreAnnotations.LemmaAnnotation.class));  
    }
@Test
    public void testEmptySentencesAnnotation() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();
        
        annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

        try {
            annotator.annotate(annotation);
            fail("Expected RuntimeException due to empty SentencesAnnotation");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Unable to find words/tokens"));
        }
    }
@Test
    public void testMultiWordTokenWithUnderscoresButNotAPhrasalVerb() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();
        
        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, "data_science");
        token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token);

        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);

        assertEquals("data_science", token.get(CoreAnnotations.LemmaAnnotation.class));  
    }
@Test
    public void testTokenWithOnlySingleSpaceCharacter() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();
        
        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, " ");
        token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token);
        
        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
        
        annotator.annotate(annotation);
        
        assertNull(token.get(CoreAnnotations.LemmaAnnotation.class));  
    }
@Test
    public void testHandlingOfEmptyStringToken() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();
        
        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, "");
        token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token);
        
        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
        
        annotator.annotate(annotation);
        
        assertNull(token.get(CoreAnnotations.LemmaAnnotation.class));  
    }
@Test
    public void testTokenWithMixedLettersNumbersAndSymbols() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();
        
        String mixedToken = "run123!";
        
        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, mixedToken);
        token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token);
        
        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
        
        annotator.annotate(annotation);

        assertEquals(mixedToken, token.get(CoreAnnotations.LemmaAnnotation.class));  
    }
@Test
    public void testHandlingOfNullPOSField() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();
        
        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, "running");
        token.set(CoreAnnotations.PartOfSpeechAnnotation.class, null);  

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token);
        
        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
        
        annotator.annotate(annotation);
        
        assertNotNull(token.get(CoreAnnotations.LemmaAnnotation.class));  
    }
@Test
    public void testHandlingOfTokenWithOnlyDigits() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();
        
        String numberToken = "98765";

        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, numberToken);
        token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "CD");

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token);
        
        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);
        
        assertEquals(numberToken, token.get(CoreAnnotations.LemmaAnnotation.class));  
    }
@Test
    public void testTokenWithUncommonUnicodeCharacters() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();
        
        String unicodeToken = "こんにちは";  

        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, unicodeToken);
        token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token);

        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);

        assertEquals(unicodeToken, token.get(CoreAnnotations.LemmaAnnotation.class));  
    }
@Test
    public void testEmptySentencesAnnotationHandling() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();
        
        annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

        try {
            annotator.annotate(annotation);
            fail("Expected RuntimeException due to empty SentencesAnnotation");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Unable to find words/tokens"));
        }
    }
@Test
    public void testLongTokenProcessing() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();
        
        String longWord = "pneumonoultramicroscopicsilicovolcanoconiosis";  

        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, longWord);
        token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token);

        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);

        assertEquals(longWord, token.get(CoreAnnotations.LemmaAnnotation.class));  
    }
@Test
    public void testMalformedPhrasalVerbStructure() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();
        
        String malformedPhrasalVerb = "break--up";  

        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, malformedPhrasalVerb);
        token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token);

        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);

        assertNotEquals("break_up", token.get(CoreAnnotations.LemmaAnnotation.class));
    }
@Test
    public void testTokenWithLeadingAndTrailingSpaces() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();

        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, "  running  ");
        token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBG");

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token);

        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
        annotator.annotate(annotation);

        assertEquals("run", token.get(CoreAnnotations.LemmaAnnotation.class));
    }
@Test
    public void testTokenWithMixedCaseInput() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();

        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, "LoOkInG");
        token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBG");

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token);

        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
        annotator.annotate(annotation);

        assertNotEquals("looking", token.get(CoreAnnotations.LemmaAnnotation.class)); 
    }
@Test
    public void testTokenWithNumericPOS() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();

        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, "5km");
        token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "CD");

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token);

        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
        annotator.annotate(annotation);

        assertEquals("5km", token.get(CoreAnnotations.LemmaAnnotation.class)); 
    }
@Test
    public void testPhrasalVerbWithMisspelledParticle() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();

        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, "give_upz");
        token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token);

        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
        annotator.annotate(annotation);

        assertNull(token.get(CoreAnnotations.LemmaAnnotation.class)); 
    }
@Test
    public void testTokenContainingHyphen() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();

        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, "self-driving");
        token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "JJ");

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token);

        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
        annotator.annotate(annotation);

        assertEquals("self-driving", token.get(CoreAnnotations.LemmaAnnotation.class)); 
    }
@Test
    public void testPhrasalVerbWithThreeParts() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();

        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, "check_this_out");
        token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token);

        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
        annotator.annotate(annotation);

        assertNull(token.get(CoreAnnotations.LemmaAnnotation.class)); 
    }
@Test
    public void testNullTextFieldForToken() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();

        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, null);
        token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token);

        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        try {
            annotator.annotate(annotation);
            fail("Expected NullPointerException due to null text field");
        } catch (NullPointerException e) {
            assertTrue(true);
        }
    }
@Test
    public void testTokenWithOnlyPunctuation() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();

        String punctuation = "?!...";

        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, punctuation);
        token.set(CoreAnnotations.PartOfSpeechAnnotation.class, ".");

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token);
        
        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);

        assertEquals(punctuation, token.get(CoreAnnotations.LemmaAnnotation.class)); 
    }
@Test
    public void testHandlingOfForeignCharacters() {
        MorphaAnnotator annotator = new MorphaAnnotator();
        Annotation annotation = new Annotation();

        String foreignText = "你好";

        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, foreignText);
        token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(token);
        
        CoreMap sentence = new CoreLabel();
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);

        assertEquals(foreignText, token.get(CoreAnnotations.LemmaAnnotation.class)); 
    } 
}