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

public class TokensRegexNERAnnotator_1_GPTLLMTest {

 @Test
    public void testAnnotationWithSimplePattern() {
        Properties props = new Properties();
        props.setProperty("mapping", "test_regexner.tab");
        props.setProperty("ignorecase", "true");
        props.setProperty("validpospattern", "NN|NNS|NNP|NNPS");
        props.setProperty("backgroundSymbol", "O,MISC");

        TokensRegexNERAnnotator nerAnnotator = new TokensRegexNERAnnotator("regexner", props);

        List<CoreLabel> tokens = new ArrayList<>();
        CoreLabel token1 = new CoreLabel();
        token1.set(CoreAnnotations.TextAnnotation.class, "Stanford");
        token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.set(CoreAnnotations.TextAnnotation.class, "University");
        token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.set(CoreAnnotations.TextAnnotation.class, "is");
        token3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token3);

        

        assertEquals("ORGANIZATION", tokens.get(0).ner());
        assertEquals("ORGANIZATION", tokens.get(1).ner());
        assertEquals("O", tokens.get(2).ner());
    }
@Test
    public void testAnnotationIsCaseInsensitive() {
        Properties props = new Properties();
        props.setProperty("mapping", "test_regexner.tab");
        props.setProperty("ignorecase", "true");

        TokensRegexNERAnnotator nerAnnotator = new TokensRegexNERAnnotator("regexner", props);

        List<CoreLabel> tokens = new ArrayList<>();
        CoreLabel token1 = new CoreLabel();
        token1.set(CoreAnnotations.TextAnnotation.class, "stanford");
        token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.set(CoreAnnotations.TextAnnotation.class, "university");
        token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token2);
        
        assertEquals("ORGANIZATION", tokens.get(0).ner());
        assertEquals("ORGANIZATION", tokens.get(1).ner());
    }
@Test
    public void testOverwritesValidNERTokens() {
        Properties props = new Properties();
        props.setProperty("mapping", "test_regexner.tab");

        TokensRegexNERAnnotator nerAnnotator = new TokensRegexNERAnnotator("regexner", props);

        List<CoreLabel> tokens = new ArrayList<>();
        CoreLabel token1 = new CoreLabel();
        token1.set(CoreAnnotations.TextAnnotation.class, "The");
        token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.set(CoreAnnotations.TextAnnotation.class, "ABC");
        token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "MISC");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.set(CoreAnnotations.TextAnnotation.class, "company");
        token3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token3);
        
        assertEquals("O", tokens.get(0).ner());
        assertEquals("ORGANIZATION", tokens.get(1).ner());
        assertEquals("O", tokens.get(2).ner());
    }
@Test
    public void testDoesNotOverwriteOtherNERLabels() {
        Properties props = new Properties();
        props.setProperty("mapping", "test_regexner.tab");

        TokensRegexNERAnnotator nerAnnotator = new TokensRegexNERAnnotator("regexner", props);

        List<CoreLabel> tokens = new ArrayList<>();
        CoreLabel token1 = new CoreLabel();
        token1.set(CoreAnnotations.TextAnnotation.class, "The");
        token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.set(CoreAnnotations.TextAnnotation.class, "ABC");
        token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.set(CoreAnnotations.TextAnnotation.class, "company");
        token3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token3);

        assertEquals("O", tokens.get(0).ner());
        assertEquals("PERSON", tokens.get(1).ner()); 
        assertEquals("O", tokens.get(2).ner());
    }
@Test
    public void testHandlesMultipleMatchesCorrectly() {
        Properties props = new Properties();
        props.setProperty("mapping", "test_regexner.tab");

        TokensRegexNERAnnotator nerAnnotator = new TokensRegexNERAnnotator("regexner", props);

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.set(CoreAnnotations.TextAnnotation.class, "Stanford");
        token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.set(CoreAnnotations.TextAnnotation.class, ",");
        token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.set(CoreAnnotations.TextAnnotation.class, "Google");
        token3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token3);

        CoreLabel token4 = new CoreLabel();
        token4.set(CoreAnnotations.TextAnnotation.class, ",");
        token4.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token4);

        CoreLabel token5 = new CoreLabel();
        token5.set(CoreAnnotations.TextAnnotation.class, "Apple");
        token5.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token5);

        CoreLabel token6 = new CoreLabel();
        token6.set(CoreAnnotations.TextAnnotation.class, "are");
        token6.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token6);

        

        assertEquals("ORGANIZATION", tokens.get(0).ner()); 
        assertEquals("O", tokens.get(1).ner());
        assertEquals("ORGANIZATION", tokens.get(2).ner()); 
        assertEquals("O", tokens.get(3).ner());
        assertEquals("ORGANIZATION", tokens.get(4).ner()); 
        assertEquals("O", tokens.get(5).ner());
    }
@Test
    public void testHandlesSingleTokenWithoutMatch() {
        Properties props = new Properties();
        props.setProperty("mapping", "test_regexner.tab");

        TokensRegexNERAnnotator nerAnnotator = new TokensRegexNERAnnotator("regexner", props);

        List<CoreLabel> tokens = new ArrayList<>();
        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, "Unknown");
        token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token);

        

        assertEquals("O", tokens.get(0).ner());  
    }
@Test
    public void testPartialMatchDoesNotAffectOtherTokens() {
        Properties props = new Properties();
        props.setProperty("mapping", "test_regexner.tab");

        TokensRegexNERAnnotator nerAnnotator = new TokensRegexNERAnnotator("regexner", props);

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.set(CoreAnnotations.TextAnnotation.class, "Stanford");
        token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.set(CoreAnnotations.TextAnnotation.class, "randomword");
        token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.set(CoreAnnotations.TextAnnotation.class, "University");
        token3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token3);

        

        assertEquals("O", tokens.get(1).ner());  
    }
@Test
    public void testEmptyTokenListDoesNotThrowErrors() {
        Properties props = new Properties();
        props.setProperty("mapping", "test_regexner.tab");

        TokensRegexNERAnnotator nerAnnotator = new TokensRegexNERAnnotator("regexner", props);

        List<CoreLabel> tokens = new ArrayList<>();
        
        

        assertTrue(tokens.isEmpty()); 
    }
@Test
    public void testWhitespaceOnlyTokenDoesNotCauseErrors() {
        Properties props = new Properties();
        props.setProperty("mapping", "test_regexner.tab");

        TokensRegexNERAnnotator nerAnnotator = new TokensRegexNERAnnotator("regexner", props);

        List<CoreLabel> tokens = new ArrayList<>();
        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, "   ");
        token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token);

        

        assertEquals("O", tokens.get(0).ner());  
    }
@Test
    public void testAnnotationsWithMultipleOverlappingMatches() {
        Properties props = new Properties();
        props.setProperty("mapping", "test_regexner_complex.tab");

        TokensRegexNERAnnotator nerAnnotator = new TokensRegexNERAnnotator("regexner", props);

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.set(CoreAnnotations.TextAnnotation.class, "New");
        token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.set(CoreAnnotations.TextAnnotation.class, "York");
        token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.set(CoreAnnotations.TextAnnotation.class, "City");
        token3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token3);

        

        assertEquals("LOCATION", tokens.get(0).ner());  
        assertEquals("LOCATION", tokens.get(1).ner());  
        assertEquals("LOCATION", tokens.get(2).ner());  
    }
@Test
    public void testDoesNotOverwritePredefinedNonOverwritableLabels() {
        Properties props = new Properties();
        props.setProperty("mapping", "test_regexner.tab");
        props.setProperty("noDefaultOverwriteLabels", "PERSON");

        TokensRegexNERAnnotator nerAnnotator = new TokensRegexNERAnnotator("regexner", props);

        List<CoreLabel> tokens = new ArrayList<>();
        
        CoreLabel token1 = new CoreLabel();
        token1.set(CoreAnnotations.TextAnnotation.class, "Obama");
        token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
        tokens.add(token1);

        

        assertEquals("PERSON", tokens.get(0).ner());  
    }
@Test
    public void testSpecialCharactersInTokensDoNotCauseIssues() {
        Properties props = new Properties();
        props.setProperty("mapping", "test_regexner_special_characters.tab");

        TokensRegexNERAnnotator nerAnnotator = new TokensRegexNERAnnotator("regexner", props);

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.set(CoreAnnotations.TextAnnotation.class, "$100");
        token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.set(CoreAnnotations.TextAnnotation.class, "%");
        token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.set(CoreAnnotations.TextAnnotation.class, "&");
        token3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token3);

        

        assertEquals("MONEY", tokens.get(0).ner()); 
        assertEquals("O", tokens.get(1).ner()); 
        assertEquals("O", tokens.get(2).ner()); 
    }
@Test
    public void testPositionalAnnotationDoesNotMisalignTokens() {
        Properties props = new Properties();
        props.setProperty("mapping", "test_regexner.tab");
        props.setProperty("validpospattern", "NNP");

        TokensRegexNERAnnotator nerAnnotator = new TokensRegexNERAnnotator("regexner", props);

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.set(CoreAnnotations.TextAnnotation.class, "Stanford");
        token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
        token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.set(CoreAnnotations.TextAnnotation.class, "University");
        token2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
        token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token2);

        

        assertEquals("O", tokens.get(1).ner());  
    }
@Test
    public void testAnnotationWithSingleCharacterTokens() {
        Properties props = new Properties();
        props.setProperty("mapping", "test_regexner.tab");

        TokensRegexNERAnnotator nerAnnotator = new TokensRegexNERAnnotator("regexner", props);

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.set(CoreAnnotations.TextAnnotation.class, "A");
        token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.set(CoreAnnotations.TextAnnotation.class, "B");
        token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.set(CoreAnnotations.TextAnnotation.class, "C");
        token3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token3);

        

        assertEquals("O", tokens.get(0).ner()); 
        assertEquals("O", tokens.get(1).ner());
        assertEquals("O", tokens.get(2).ner());
    }
@Test
    public void testAnnotationWithLongSentence() {
        Properties props = new Properties();
        props.setProperty("mapping", "test_regexner.tab");

        TokensRegexNERAnnotator nerAnnotator = new TokensRegexNERAnnotator("regexner", props);

        List<CoreLabel> tokens = new ArrayList<>();
        
        CoreLabel token1 = new CoreLabel();
        token1.set(CoreAnnotations.TextAnnotation.class, "This");
        token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token1);
        
        CoreLabel token2 = new CoreLabel();
        token2.set(CoreAnnotations.TextAnnotation.class, "is");
        token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token2);
        
        CoreLabel token3 = new CoreLabel();
        token3.set(CoreAnnotations.TextAnnotation.class, "a");
        token3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token3);
        
        CoreLabel token4 = new CoreLabel();
        token4.set(CoreAnnotations.TextAnnotation.class, "very");
        token4.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token4);

        CoreLabel token5 = new CoreLabel();
        token5.set(CoreAnnotations.TextAnnotation.class, "long");
        token5.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token5);

        CoreLabel token6 = new CoreLabel();
        token6.set(CoreAnnotations.TextAnnotation.class, "sentence");
        token6.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token6);

        

        assertEquals("O", tokens.get(0).ner());
        assertEquals("O", tokens.get(1).ner());
        assertEquals("O", tokens.get(2).ner());
        assertEquals("O", tokens.get(3).ner());
        assertEquals("O", tokens.get(4).ner());
        assertEquals("O", tokens.get(5).ner());
    }
@Test
    public void testAnnotationDoesNotOverwriteSpecificLabels() {
        Properties props = new Properties();
        props.setProperty("mapping", "test_regexner.tab");
        props.setProperty("noDefaultOverwriteLabels", "LOCATION");

        TokensRegexNERAnnotator nerAnnotator = new TokensRegexNERAnnotator("regexner", props);

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.set(CoreAnnotations.TextAnnotation.class, "Paris");
        token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");
        tokens.add(token1);

        

        assertEquals("LOCATION", tokens.get(0).ner()); 
    }
@Test
    public void testAnnotationHandlesRepeatedEntitiesCorrectly() {
        Properties props = new Properties();
        props.setProperty("mapping", "test_regexner.tab");

        TokensRegexNERAnnotator nerAnnotator = new TokensRegexNERAnnotator("regexner", props);

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.set(CoreAnnotations.TextAnnotation.class, "Google");
        token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.set(CoreAnnotations.TextAnnotation.class, "Google");
        token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.set(CoreAnnotations.TextAnnotation.class, "Google");
        token3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token3);

        

        assertEquals("ORGANIZATION", tokens.get(0).ner()); 
        assertEquals("ORGANIZATION", tokens.get(1).ner());
        assertEquals("ORGANIZATION", tokens.get(2).ner());
    }
@Test
    public void testAnnotationWithNumbersAndSpecialSymbols() {
        Properties props = new Properties();
        props.setProperty("mapping", "test_regexner.tab");

        TokensRegexNERAnnotator nerAnnotator = new TokensRegexNERAnnotator("regexner", props);

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.set(CoreAnnotations.TextAnnotation.class, "1234");
        token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.set(CoreAnnotations.TextAnnotation.class, "@");
        token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.set(CoreAnnotations.TextAnnotation.class, "#");
        token3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token3);

        

        assertEquals("NUMBER", tokens.get(0).ner());
        assertEquals("O", tokens.get(1).ner());
        assertEquals("O", tokens.get(2).ner());
    }
@Test
    public void testAnnotationHandlesSentenceWithMultipleTypes() {
        Properties props = new Properties();
        props.setProperty("mapping", "test_regexner.tab");

        TokensRegexNERAnnotator nerAnnotator = new TokensRegexNERAnnotator("regexner", props);

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.set(CoreAnnotations.TextAnnotation.class, "Apple");
        token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.set(CoreAnnotations.TextAnnotation.class, "1234");
        token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.set(CoreAnnotations.TextAnnotation.class, "San");
        token3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token3);

        CoreLabel token4 = new CoreLabel();
        token4.set(CoreAnnotations.TextAnnotation.class, "Francisco");
        token4.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token4);

        

        assertEquals("ORGANIZATION", tokens.get(0).ner()); 
        assertEquals("NUMBER", tokens.get(1).ner()); 
        assertEquals("LOCATION", tokens.get(2).ner()); 
        assertEquals("LOCATION", tokens.get(3).ner()); 
    }
@Test
    public void testAnnotationWithSingleTokenMatch() {
        Properties props = new Properties();
        props.setProperty("mapping", "test_regexner.tab");

        TokensRegexNERAnnotator nerAnnotator = new TokensRegexNERAnnotator("regexner", props);

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, "Stanford");
        token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token);

        

        assertEquals("ORGANIZATION", tokens.get(0).ner());
    }
@Test
    public void testAnnotationWithSymbolsBetweenEntities() {
        Properties props = new Properties();
        props.setProperty("mapping", "test_regexner.tab");

        TokensRegexNERAnnotator nerAnnotator = new TokensRegexNERAnnotator("regexner", props);

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.set(CoreAnnotations.TextAnnotation.class, "Stanford");
        token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.set(CoreAnnotations.TextAnnotation.class, "-");
        token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.set(CoreAnnotations.TextAnnotation.class, "University");
        token3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token3);

        

        assertEquals("ORGANIZATION", tokens.get(0).ner());
        assertEquals("O", tokens.get(1).ner());
        assertEquals("ORGANIZATION", tokens.get(2).ner());
    }
@Test
    public void testAnnotationWithLongUnmatchedSentence() {
        Properties props = new Properties();
        props.setProperty("mapping", "test_regexner.tab");

        TokensRegexNERAnnotator nerAnnotator = new TokensRegexNERAnnotator("regexner", props);

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.set(CoreAnnotations.TextAnnotation.class, "This");
        token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.set(CoreAnnotations.TextAnnotation.class, "is");
        token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.set(CoreAnnotations.TextAnnotation.class, "a");
        token3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token3);

        CoreLabel token4 = new CoreLabel();
        token4.set(CoreAnnotations.TextAnnotation.class, "plain");
        token4.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token4);

        CoreLabel token5 = new CoreLabel();
        token5.set(CoreAnnotations.TextAnnotation.class, "sentence");
        token5.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token5);

        

        assertEquals("O", tokens.get(0).ner());
        assertEquals("O", tokens.get(1).ner());
        assertEquals("O", tokens.get(2).ner());
        assertEquals("O", tokens.get(3).ner());
        assertEquals("O", tokens.get(4).ner());
    }
@Test
    public void testAnnotationWithNumbersRecognizedAsEntities() {
        Properties props = new Properties();
        props.setProperty("mapping", "test_regexner.tab");

        TokensRegexNERAnnotator nerAnnotator = new TokensRegexNERAnnotator("regexner", props);

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, "12345");
        token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token);

        

        assertEquals("NUMBER", tokens.get(0).ner());
    }
@Test
    public void testAnnotationWithMixedEntityTypes() {
        Properties props = new Properties();
        props.setProperty("mapping", "test_regexner.tab");

        TokensRegexNERAnnotator nerAnnotator = new TokensRegexNERAnnotator("regexner", props);

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.set(CoreAnnotations.TextAnnotation.class, "Apple");
        token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.set(CoreAnnotations.TextAnnotation.class, "San");
        token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.set(CoreAnnotations.TextAnnotation.class, "Francisco");
        token3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token3);

        

        assertEquals("ORGANIZATION", tokens.get(0).ner());  
        assertEquals("LOCATION", tokens.get(1).ner());  
        assertEquals("LOCATION", tokens.get(2).ner());  
    }
@Test
    public void testAnnotationWithOverlappingMatches() {
        Properties props = new Properties();
        props.setProperty("mapping", "test_regexner_complex.tab");

        TokensRegexNERAnnotator nerAnnotator = new TokensRegexNERAnnotator("regexner", props);

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.set(CoreAnnotations.TextAnnotation.class, "New");
        token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.set(CoreAnnotations.TextAnnotation.class, "York");
        token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.set(CoreAnnotations.TextAnnotation.class, "City");
        token3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token3);

        

        assertEquals("LOCATION", tokens.get(0).ner());  
        assertEquals("LOCATION", tokens.get(1).ner());  
        assertEquals("LOCATION", tokens.get(2).ner());  
    }
@Test
    public void testAnnotationWithUnrecognizedSpecialCharacters() {
        Properties props = new Properties();
        props.setProperty("mapping", "test_regexner_special_characters.tab");

        TokensRegexNERAnnotator nerAnnotator = new TokensRegexNERAnnotator("regexner", props);

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.set(CoreAnnotations.TextAnnotation.class, "*&^%");
        token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token1);

        

        assertEquals("O", tokens.get(0).ner()); 
    }
@Test
    public void testAnnotationWithEmptyTextTokens() {
        Properties props = new Properties();
        props.setProperty("mapping", "test_regexner.tab");

        TokensRegexNERAnnotator nerAnnotator = new TokensRegexNERAnnotator("regexner", props);

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.set(CoreAnnotations.TextAnnotation.class, "");
        token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token1);

        

        assertEquals("O", tokens.get(0).ner()); 
    }
@Test
    public void testAnnotationWithMixedCaseEntities() {
        Properties props = new Properties();
        props.setProperty("mapping", "test_regexner.tab");
        props.setProperty("ignorecase", "true");

        TokensRegexNERAnnotator nerAnnotator = new TokensRegexNERAnnotator("regexner", props);

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.set(CoreAnnotations.TextAnnotation.class, "GOOGLE");
        token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.set(CoreAnnotations.TextAnnotation.class, "Google");
        token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.set(CoreAnnotations.TextAnnotation.class, "google");
        token3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token3);

        

        assertEquals("ORGANIZATION", tokens.get(0).ner());
        assertEquals("ORGANIZATION", tokens.get(1).ner());
        assertEquals("ORGANIZATION", tokens.get(2).ner());
    }
@Test
    public void testAnnotationWithMultiWordEntityAtSentenceStart() {
        Properties props = new Properties();
        props.setProperty("mapping", "test_regexner.tab");

        TokensRegexNERAnnotator nerAnnotator = new TokensRegexNERAnnotator("regexner", props);

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.set(CoreAnnotations.TextAnnotation.class, "New");
        token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.set(CoreAnnotations.TextAnnotation.class, "York");
        token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.set(CoreAnnotations.TextAnnotation.class, "is");
        token3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token3);

        

        assertEquals("LOCATION", tokens.get(0).ner());
        assertEquals("LOCATION", tokens.get(1).ner());
        assertEquals("O", tokens.get(2).ner()); 
    }
@Test
    public void testAnnotationWithRepeatedNonEntityWord() {
        Properties props = new Properties();
        props.setProperty("mapping", "test_regexner.tab");

        TokensRegexNERAnnotator nerAnnotator = new TokensRegexNERAnnotator("regexner", props);

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.set(CoreAnnotations.TextAnnotation.class, "random");
        token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.set(CoreAnnotations.TextAnnotation.class, "random");
        token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.set(CoreAnnotations.TextAnnotation.class, "random");
        token3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token3);

        

        assertEquals("O", tokens.get(0).ner());
        assertEquals("O", tokens.get(1).ner());
        assertEquals("O", tokens.get(2).ner());
    }
@Test
    public void testAnnotationWithUnicodeCharacters() {
        Properties props = new Properties();
        props.setProperty("mapping", "test_regexner_unicode.tab");

        TokensRegexNERAnnotator nerAnnotator = new TokensRegexNERAnnotator("regexner", props);

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, "北京");
        token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token);

        

        assertEquals("LOCATION", tokens.get(0).ner()); 
    }
@Test
    public void testAnnotationWithNonMatchingSentence() {
        Properties props = new Properties();
        props.setProperty("mapping", "test_regexner.tab");

        TokensRegexNERAnnotator nerAnnotator = new TokensRegexNERAnnotator("regexner", props);

        List<CoreLabel> tokens = new ArrayList<>();
        
        CoreLabel token1 = new CoreLabel();
        token1.set(CoreAnnotations.TextAnnotation.class, "No");
        token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token1);
        
        CoreLabel token2 = new CoreLabel();
        token2.set(CoreAnnotations.TextAnnotation.class, "entities");
        token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token2);
        
        CoreLabel token3 = new CoreLabel();
        token3.set(CoreAnnotations.TextAnnotation.class, "here");
        token3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token3);

        

        assertEquals("O", tokens.get(0).ner());
        assertEquals("O", tokens.get(1).ner());
        assertEquals("O", tokens.get(2).ner());
    }
@Test
    public void testAnnotationWithSentenceContainingOnlyStopWords() {
        Properties props = new Properties();
        props.setProperty("mapping", "test_regexner.tab");
        props.setProperty("commonWords", "stopwords.txt");

        TokensRegexNERAnnotator nerAnnotator = new TokensRegexNERAnnotator("regexner", props);

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.set(CoreAnnotations.TextAnnotation.class, "the");
        token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.set(CoreAnnotations.TextAnnotation.class, "and");
        token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.set(CoreAnnotations.TextAnnotation.class, "of");
        token3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token3);

        

        assertEquals("O", tokens.get(0).ner());
        assertEquals("O", tokens.get(1).ner());
        assertEquals("O", tokens.get(2).ner());
    }
@Test
    public void testAnnotationWithWhitespaceOnlyTokens() {
        Properties props = new Properties();
        props.setProperty("mapping", "test_regexner.tab");
        
        TokensRegexNERAnnotator nerAnnotator = new TokensRegexNERAnnotator("regexner", props);
        
        List<CoreLabel> tokens = new ArrayList<>();
        
        CoreLabel token1 = new CoreLabel();
        token1.set(CoreAnnotations.TextAnnotation.class, "   ");
        token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token1);
        
        

        assertEquals("O", tokens.get(0).ner()); 
    }
@Test
    public void testAnnotationWithMultipleSpacesBetweenEntities() {
        Properties props = new Properties();
        props.setProperty("mapping", "test_regexner.tab");

        TokensRegexNERAnnotator nerAnnotator = new TokensRegexNERAnnotator("regexner", props);

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.set(CoreAnnotations.TextAnnotation.class, "New");
        token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.set(CoreAnnotations.TextAnnotation.class, "    "); 
        token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.set(CoreAnnotations.TextAnnotation.class, "York");
        token3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token3);

        

        assertEquals("LOCATION", tokens.get(0).ner());
        assertEquals("O", tokens.get(1).ner());
        assertEquals("LOCATION", tokens.get(2).ner());
    }
@Test
    public void testAnnotationWithUnusualCharacters() {
        Properties props = new Properties();
        props.setProperty("mapping", "test_regexner_special_chars.tab");

        TokensRegexNERAnnotator nerAnnotator = new TokensRegexNERAnnotator("regexner", props);

        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token = new CoreLabel();
        token.set(CoreAnnotations.TextAnnotation.class, "@Stanford#");
        token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token);

        

        assertEquals("O", tokens.get(0).ner()); 
    }
@Test
    public void testAnnotationWithSentenceContainingMixedUnrecognizedEntities() {
        Properties props = new Properties();
        props.setProperty("mapping", "test_regexner.tab");

        TokensRegexNERAnnotator nerAnnotator = new TokensRegexNERAnnotator("regexner", props);

        List<CoreLabel> tokens = new ArrayList<>();
        
        CoreLabel token1 = new CoreLabel();
        token1.set(CoreAnnotations.TextAnnotation.class, "Apple");
        token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.set(CoreAnnotations.TextAnnotation.class, "Computer");
        token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.set(CoreAnnotations.TextAnnotation.class, "Software");
        token3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token3);
        
        

        assertEquals("ORGANIZATION", tokens.get(0).ner());  
        assertEquals("O", tokens.get(1).ner());  
        assertEquals("O", tokens.get(2).ner());  
    }
@Test
    public void testAnnotationWithEntitiesContainingPunctuation() {
        Properties props = new Properties();
        props.setProperty("mapping", "test_regexner_punctuation.tab");

        TokensRegexNERAnnotator nerAnnotator = new TokensRegexNERAnnotator("regexner", props);

        List<CoreLabel> tokens = new ArrayList<>();
        
        CoreLabel token1 = new CoreLabel();
        token1.set(CoreAnnotations.TextAnnotation.class, "U.S.");
        token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.set(CoreAnnotations.TextAnnotation.class, "Government");
        token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token2);
        
        

        assertEquals("LOCATION", tokens.get(0).ner());  
        assertEquals("O", tokens.get(1).ner()); 
    }
@Test
    public void testAnnotationWithEntitiesMatchingPartialWords() {
        Properties props = new Properties();
        props.setProperty("mapping", "test_regexner_partial_words.tab");

        TokensRegexNERAnnotator nerAnnotator = new TokensRegexNERAnnotator("regexner", props);

        List<CoreLabel> tokens = new ArrayList<>();
        
        CoreLabel token1 = new CoreLabel();
        token1.set(CoreAnnotations.TextAnnotation.class, "Stanfordian");
        token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token1);
        
        

        assertEquals("O", tokens.get(0).ner());  
    }
@Test
    public void testAnnotationWithEntitiesContainingNumbersAndLetters() {
        Properties props = new Properties();
        props.setProperty("mapping", "test_regexner_alphanumeric.tab");

        TokensRegexNERAnnotator nerAnnotator = new TokensRegexNERAnnotator("regexner", props);

        List<CoreLabel> tokens = new ArrayList<>();
        
        CoreLabel token1 = new CoreLabel();
        token1.set(CoreAnnotations.TextAnnotation.class, "X123");
        token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.set(CoreAnnotations.TextAnnotation.class, "B5000");
        token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token2);

        

        assertEquals("PRODUCT", tokens.get(0).ner()); 
        assertEquals("PRODUCT", tokens.get(1).ner()); 
    }
@Test
    public void testAnnotationWithSpecialCaseAcronyms() {
        Properties props = new Properties();
        props.setProperty("mapping", "test_regexner_acronyms.tab");

        TokensRegexNERAnnotator nerAnnotator = new TokensRegexNERAnnotator("regexner", props);
        
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.set(CoreAnnotations.TextAnnotation.class, "NASA");
        token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.set(CoreAnnotations.TextAnnotation.class, "UNESCO");
        token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token2);

        

        assertEquals("ORGANIZATION", tokens.get(0).ner()); 
        assertEquals("ORGANIZATION", tokens.get(1).ner()); 
    }
@Test
    public void testAnnotationWithMultiWordEntityAtEndOfSentence() {
        Properties props = new Properties();
        props.setProperty("mapping", "test_regexner.tab");

        TokensRegexNERAnnotator nerAnnotator = new TokensRegexNERAnnotator("regexner", props);
        
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.set(CoreAnnotations.TextAnnotation.class, "I");
        token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.set(CoreAnnotations.TextAnnotation.class, "study");
        token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.set(CoreAnnotations.TextAnnotation.class, "at");
        token3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token3);

        CoreLabel token4 = new CoreLabel();
        token4.set(CoreAnnotations.TextAnnotation.class, "Harvard");
        token4.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token4);

        CoreLabel token5 = new CoreLabel();
        token5.set(CoreAnnotations.TextAnnotation.class, "University");
        token5.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token5);

        

        assertEquals("O", tokens.get(0).ner());
        assertEquals("O", tokens.get(1).ner());
        assertEquals("O", tokens.get(2).ner());
        assertEquals("ORGANIZATION", tokens.get(3).ner());
        assertEquals("ORGANIZATION", tokens.get(4).ner());
    }
@Test
    public void testAnnotationWithSpecialCharactersInEntity() {
        Properties props = new Properties();
        props.setProperty("mapping", "test_regexner_special.tab");

        TokensRegexNERAnnotator nerAnnotator = new TokensRegexNERAnnotator("regexner", props);
        
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.set(CoreAnnotations.TextAnnotation.class, "$Google");
        token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token1);

        

        assertEquals("ORGANIZATION", tokens.get(0).ner());
    }
@Test
    public void testAnnotationWithAcronymsMixedWithRegularWords() {
        Properties props = new Properties();
        props.setProperty("mapping", "test_regexner_acronyms.tab");
        
        TokensRegexNERAnnotator nerAnnotator = new TokensRegexNERAnnotator("regexner", props);
        
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.set(CoreAnnotations.TextAnnotation.class, "NASA");
        token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.set(CoreAnnotations.TextAnnotation.class, "Research");
        token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.set(CoreAnnotations.TextAnnotation.class, "Center");
        token3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token3);

        

        assertEquals("ORGANIZATION", tokens.get(0).ner());
        assertEquals("O", tokens.get(1).ner());
        assertEquals("O", tokens.get(2).ner());
    }
@Test
    public void testAnnotationWithHyphenatedNamedEntity() {
        Properties props = new Properties();
        props.setProperty("mapping", "test_regexner_hyphens.tab");

        TokensRegexNERAnnotator nerAnnotator = new TokensRegexNERAnnotator("regexner", props);
        
        List<CoreLabel> tokens = new ArrayList<>();
        
        CoreLabel token1 = new CoreLabel();
        token1.set(CoreAnnotations.TextAnnotation.class, "Smith-Johnson");
        token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token1);

        

        assertEquals("PERSON", tokens.get(0).ner());
    }
@Test
    public void testAnnotationWithHumanNamesHavingMiddleInitials() {
        Properties props = new Properties();
        props.setProperty("mapping", "test_regexner_names.tab");

        TokensRegexNERAnnotator nerAnnotator = new TokensRegexNERAnnotator("regexner", props);
        
        List<CoreLabel> tokens = new ArrayList<>();
        
        CoreLabel token1 = new CoreLabel();
        token1.set(CoreAnnotations.TextAnnotation.class, "John");
        token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.set(CoreAnnotations.TextAnnotation.class, "F.");
        token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.set(CoreAnnotations.TextAnnotation.class, "Kennedy");
        token3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token3);

        

        assertEquals("PERSON", tokens.get(0).ner());
        assertEquals("PERSON", tokens.get(1).ner());
        assertEquals("PERSON", tokens.get(2).ner());
    }
@Test
    public void testAnnotationWithLongNumbersThatShouldNotBeEntities() {
        Properties props = new Properties();
        props.setProperty("mapping", "test_regexner.tab");
        
        TokensRegexNERAnnotator nerAnnotator = new TokensRegexNERAnnotator("regexner", props);
        
        List<CoreLabel> tokens = new ArrayList<>();
        
        CoreLabel token1 = new CoreLabel();
        token1.set(CoreAnnotations.TextAnnotation.class, "1234567890");
        token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token1);

        

        assertEquals("O", tokens.get(0).ner()); 
    }
@Test
    public void testAnnotationWithSymbolSeparatedEntityNames() {
        Properties props = new Properties();
        props.setProperty("mapping", "test_regexner_symbols.tab");

        TokensRegexNERAnnotator nerAnnotator = new TokensRegexNERAnnotator("regexner", props);
        
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.set(CoreAnnotations.TextAnnotation.class, "New");
        token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.set(CoreAnnotations.TextAnnotation.class, "/");
        token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.set(CoreAnnotations.TextAnnotation.class, "York");
        token3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
        tokens.add(token3);

        

        assertEquals("LOCATION", tokens.get(0).ner());
        assertEquals("O", tokens.get(1).ner()); 
        assertEquals("LOCATION", tokens.get(2).ner());
    } 
}