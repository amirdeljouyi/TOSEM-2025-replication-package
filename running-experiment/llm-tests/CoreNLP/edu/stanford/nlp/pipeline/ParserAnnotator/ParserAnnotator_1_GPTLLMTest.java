package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.parser.common.ParserGrammar;
import edu.stanford.nlp.parser.common.ParserQuery;
import edu.stanford.nlp.parser.lexparser.TreebankLangParserParams;
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.TimeExpression;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.*;

import edu.stanford.nlp.ie.NERClassifierCombiner;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.time.Timex;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import org.junit.Test;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ParserAnnotator_1_GPTLLMTest {

 @Test
    public void testConstructorWithProperties() {
        Properties testProperties = new Properties();
        testProperties.setProperty("test.model", "edu/stanford/nlp/models/parser");
        testProperties.setProperty("test.debug", "true");
        testProperties.setProperty("test.keepPunct", "false");

        ParserAnnotator parserAnnotator = new ParserAnnotator("test", testProperties);

        assertNotNull(parserAnnotator);
    }
@Test(expected = IllegalArgumentException.class)
    public void testConstructorWithMissingModel() {
        Properties testProperties = new Properties();
        new ParserAnnotator("test", testProperties);
    }
@Test
    public void testSignatureStringGeneration() {
        Properties testProperties = new Properties();
        testProperties.setProperty("test.model", "custom_model");
        testProperties.setProperty("test.debug", "false");

        String signature = ParserAnnotator.signature("test", testProperties);

        assertTrue(signature.contains("test.model:custom_model"));
        assertTrue(signature.contains("test.debug:false"));
    }
@Test
    public void testDoOneSentenceWithValidData() {
        ParserGrammar mockParser = mock(ParserGrammar.class);
        ParserAnnotator parserAnnotator = new ParserAnnotator(mockParser, true, 50);

        CoreMap mockSentence = mock(CoreMap.class);
        CoreLabel token1 = new CoreLabel();
        token1.setWord("Hello");
        token1.setTag("UH");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("world");
        token2.setTag("NN");

        List<CoreLabel> tokens = Arrays.asList(token1, token2);

        when(mockSentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
        when(mockSentence.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(null);

        Annotation mockAnnotation = mock(Annotation.class);
        parserAnnotator.doOneSentence(mockAnnotation, mockSentence);
  }
@Test
    public void testDoOneSentenceWithExceedingLength() {
        ParserGrammar mockParser = mock(ParserGrammar.class);
        ParserAnnotator parserAnnotator = new ParserAnnotator(mockParser, true, 10);

        CoreMap mockSentence = mock(CoreMap.class);
        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(new CoreLabel());
        tokens.add(new CoreLabel());
        tokens.add(new CoreLabel());
        tokens.add(new CoreLabel());
        tokens.add(new CoreLabel());
        tokens.add(new CoreLabel());
        tokens.add(new CoreLabel());
        tokens.add(new CoreLabel());
        tokens.add(new CoreLabel());
        tokens.add(new CoreLabel());
        tokens.add(new CoreLabel());  

        when(mockSentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

        Annotation mockAnnotation = mock(Annotation.class);
        parserAnnotator.doOneSentence(mockAnnotation, mockSentence);
  }
@Test
    public void testDoOneFailedSentence() {
        ParserGrammar mockParser = mock(ParserGrammar.class);
        ParserAnnotator parserAnnotator = new ParserAnnotator(mockParser, true, 50);

        CoreMap mockSentence = mock(CoreMap.class);
        CoreLabel token1 = new CoreLabel();
        token1.setWord("This");
        token1.setTag(null);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("fails");
        token2.setTag(null);

        List<CoreLabel> tokens = Arrays.asList(token1, token2);

        when(mockSentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

        Annotation mockAnnotation = mock(Annotation.class);
        parserAnnotator.doOneFailedSentence(mockAnnotation, mockSentence);

        assertEquals("XX", token1.tag());
        assertEquals("XX", token2.tag());
        verify(mockSentence, times(1)).set(eq(TreeCoreAnnotations.TreeAnnotation.class), any(Tree.class));
    }
@Test
    public void testRequires() {
        ParserGrammar mockParser = mock(ParserGrammar.class);
        when(mockParser.getTLPParams()).thenReturn(mock(TreebankLangParserParams.class));
        ParserAnnotator parserAnnotator = new ParserAnnotator(mockParser, true, 50);

        Set<Class<? extends CoreAnnotation>> requiredAnnotations = parserAnnotator.requires();

        assertTrue(requiredAnnotations.contains(CoreAnnotations.TextAnnotation.class));
        assertTrue(requiredAnnotations.contains(CoreAnnotations.TokensAnnotation.class));
    }
@Test
    public void testRequirementsSatisfied() {
        ParserGrammar mockParser = mock(ParserGrammar.class);
        when(mockParser.getTLPParams()).thenReturn(mock(TreebankLangParserParams.class));
        ParserAnnotator parserAnnotator = new ParserAnnotator(mockParser, true, 50);

        Set<Class<? extends CoreAnnotation>> satisfiedAnnotations = parserAnnotator.requirementsSatisfied();

        assertTrue(satisfiedAnnotations.contains(TreeCoreAnnotations.TreeAnnotation.class));
    }
@Test
    public void testMaxTime() {
        ParserGrammar mockParser = mock(ParserGrammar.class);
        when(mockParser.getTLPParams()).thenReturn(mock(TreebankLangParserParams.class));
        ParserAnnotator parserAnnotator = new ParserAnnotator(mockParser, true, 50);

        assertEquals(0, parserAnnotator.maxTime());
    }
@Test
    public void testNThreads() {
        ParserGrammar mockParser = mock(ParserGrammar.class);
        when(mockParser.getTLPParams()).thenReturn(mock(TreebankLangParserParams.class));
        ParserAnnotator parserAnnotator = new ParserAnnotator(mockParser, true, 50);

        assertEquals(1, parserAnnotator.nThreads());
    }
@Test
    public void testConstructorWithNullProperties() {
        try {
            new ParserAnnotator("test", null);
            fail("Expected NullPointerException or IllegalArgumentException for null properties");
        } catch (NullPointerException | IllegalArgumentException e) {
            
        }
    }
@Test
    public void testConstructorWithNegativeMaxSentLength() {
        ParserGrammar mockParser = mock(ParserGrammar.class);
        when(mockParser.getTLPParams()).thenReturn(mock(TreebankLangParserParams.class));

        ParserAnnotator parserAnnotator = new ParserAnnotator(mockParser, true, -5);

        assertNotNull(parserAnnotator);
    }
@Test
    public void testDoOneSentenceWithEmptyTokenList() {
        ParserGrammar mockParser = mock(ParserGrammar.class);
        when(mockParser.getTLPParams()).thenReturn(mock(TreebankLangParserParams.class));

        ParserAnnotator parserAnnotator = new ParserAnnotator(mockParser, true, 50);
        CoreMap mockSentence = mock(CoreMap.class);

        List<CoreLabel> emptyTokens = Collections.emptyList();
        when(mockSentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(emptyTokens);

        Annotation mockAnnotation = mock(Annotation.class);
        parserAnnotator.doOneSentence(mockAnnotation, mockSentence);

        verify(mockSentence, times(1)).set(eq(TreeCoreAnnotations.TreeAnnotation.class), any(Tree.class));
    }
@Test
    public void testDoOneSentenceWithNullTokenList() {
        ParserGrammar mockParser = mock(ParserGrammar.class);
        when(mockParser.getTLPParams()).thenReturn(mock(TreebankLangParserParams.class));

        ParserAnnotator parserAnnotator = new ParserAnnotator(mockParser, true, 50);
        CoreMap mockSentence = mock(CoreMap.class);

        when(mockSentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(null);

        Annotation mockAnnotation = mock(Annotation.class);
        parserAnnotator.doOneSentence(mockAnnotation, mockSentence);

    }
@Test
    public void testDoOneFailedSentenceWithNullTokens() {
        ParserGrammar mockParser = mock(ParserGrammar.class);
        when(mockParser.getTLPParams()).thenReturn(mock(TreebankLangParserParams.class));

        ParserAnnotator parserAnnotator = new ParserAnnotator(mockParser, true, 50);
        CoreMap mockSentence = mock(CoreMap.class);

        when(mockSentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(null);

        Annotation mockAnnotation = mock(Annotation.class);
        parserAnnotator.doOneFailedSentence(mockAnnotation, mockSentence);

    }
@Test
    public void testDoOneSentenceWithExistingTreeAnnotation() {
        ParserGrammar mockParser = mock(ParserGrammar.class);
        when(mockParser.getTLPParams()).thenReturn(mock(TreebankLangParserParams.class));

        ParserAnnotator parserAnnotator = new ParserAnnotator(mockParser, true, 50);
        CoreMap mockSentence = mock(CoreMap.class);

        Tree existingTree = mock(Tree.class);
        when(existingTree.label()).thenReturn(mock(Label.class));
        when(existingTree.label().value()).thenReturn("S");
        when(mockSentence.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(existingTree);

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(new CoreLabel());
        tokens.add(new CoreLabel());
        when(mockSentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

        Annotation mockAnnotation = mock(Annotation.class);
        parserAnnotator.doOneSentence(mockAnnotation, mockSentence);

    }
@Test
    public void testDoOneSentenceWithNoSquashSet() {
        ParserGrammar mockParser = mock(ParserGrammar.class);
        when(mockParser.getTLPParams()).thenReturn(mock(TreebankLangParserParams.class));

        ParserAnnotator parserAnnotator = new ParserAnnotator(mockParser, true, 50);
        
        CoreMap mockSentence = mock(CoreMap.class);
        Tree mockTree = mock(Tree.class);
        when(mockTree.label()).thenReturn(mock(Label.class));
        when(mockTree.label().value()).thenReturn("X");
        when(mockSentence.get(TreeCoreAnnotations.TreeAnnotation.class)).thenReturn(mockTree);

        List<CoreLabel> tokens = Arrays.asList(new CoreLabel(), new CoreLabel());
        when(mockSentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

        Annotation mockAnnotation = mock(Annotation.class);
        parserAnnotator.doOneSentence(mockAnnotation, mockSentence);

        verify(mockSentence, times(1)).set(eq(TreeCoreAnnotations.TreeAnnotation.class), any(Tree.class));
    }
@Test
    public void testSignatureWithEmptyProperties() {
        Properties testProperties = new Properties();

        String signature = ParserAnnotator.signature("test", testProperties);

        assertNotNull(signature);
        assertTrue(signature.contains("test.model"));
    }
@Test
    public void testSignatureWithDifferentAnnotatorNames() {
        Properties testProperties = new Properties();
        testProperties.setProperty("annotatorA.model", "modelA");
        testProperties.setProperty("annotatorB.model", "modelB");

        String signatureA = ParserAnnotator.signature("annotatorA", testProperties);
        String signatureB = ParserAnnotator.signature("annotatorB", testProperties);

        assertTrue(signatureA.contains("annotatorA.model:modelA"));
        assertTrue(signatureB.contains("annotatorB.model:modelB"));
        assertNotEquals(signatureA, signatureB);
    }
@Test
    public void testDoOneSentenceWithOutOfMemoryException() {
        ParserGrammar mockParser = mock(ParserGrammar.class);
        when(mockParser.getTLPParams()).thenReturn(mock(TreebankLangParserParams.class));

        ParserAnnotator parserAnnotator = new ParserAnnotator(mockParser, true, 50);
        CoreMap mockSentence = mock(CoreMap.class);

        List<CoreLabel> tokens = Arrays.asList(new CoreLabel(), new CoreLabel());
        when(mockSentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

        ParserQuery mockParserQuery = mock(ParserQuery.class);
        when(mockParser.parserQuery()).thenReturn(mockParserQuery);
        when(mockParserQuery.getBestParse()).thenThrow(new OutOfMemoryError());

        Annotation mockAnnotation = mock(Annotation.class);
        parserAnnotator.doOneSentence(mockAnnotation, mockSentence);
  }
@Test
    public void testConstructorWithInvalidParserLocation() {
        try {
            new ParserAnnotator("invalid_path", true, 50, new String[]{"-testFlag"});
            fail("Expected an exception for an invalid parser location");
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException || e instanceof RuntimeException);
        }
    }
@Test
    public void testConstructorWithNullParserGrammar() {
        try {
            new ParserAnnotator(null, true, 50);
            fail("Expected NullPointerException or IllegalArgumentException");
        } catch (NullPointerException | IllegalArgumentException e) {
            
        }
    }
@Test
    public void testDoOneSentenceWithMaxSentenceLengthZero() {
        ParserGrammar mockParser = mock(ParserGrammar.class);
        when(mockParser.getTLPParams()).thenReturn(mock(TreebankLangParserParams.class));

        ParserAnnotator parserAnnotator = new ParserAnnotator(mockParser, true, 0);
        CoreMap mockSentence = mock(CoreMap.class);

        List<CoreLabel> tokens = Arrays.asList(new CoreLabel(), new CoreLabel());
        when(mockSentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

        Annotation mockAnnotation = mock(Annotation.class);
        parserAnnotator.doOneSentence(mockAnnotation, mockSentence);

    }
@Test
    public void testDoOneSentenceWithNullTreeResult() {
        ParserGrammar mockParser = mock(ParserGrammar.class);
        when(mockParser.getTLPParams()).thenReturn(mock(TreebankLangParserParams.class));

        ParserAnnotator parserAnnotator = new ParserAnnotator(mockParser, true, 50);
        CoreMap mockSentence = mock(CoreMap.class);

        List<CoreLabel> tokens = Arrays.asList(new CoreLabel(), new CoreLabel());
        when(mockSentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

        ParserQuery mockParserQuery = mock(ParserQuery.class);
        when(mockParser.parserQuery()).thenReturn(mockParserQuery);
        when(mockParserQuery.getBestParse()).thenReturn(null);
        
        Annotation mockAnnotation = mock(Annotation.class);
        parserAnnotator.doOneSentence(mockAnnotation, mockSentence);

    }
@Test
    public void testDoOneSentenceWithMultipleParses() {
        ParserGrammar mockParser = mock(ParserGrammar.class);
        when(mockParser.getTLPParams()).thenReturn(mock(TreebankLangParserParams.class));

        ParserAnnotator parserAnnotator = new ParserAnnotator(mockParser, true, 50);
        CoreMap mockSentence = mock(CoreMap.class);

        List<CoreLabel> tokens = Arrays.asList(new CoreLabel(), new CoreLabel());
        when(mockSentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

        Tree tree1 = mock(Tree.class);
        Tree tree2 = mock(Tree.class);

        List<Tree> trees = Arrays.asList(tree1, tree2);
        ParserQuery mockParserQuery = mock(ParserQuery.class);
        when(mockParser.parserQuery()).thenReturn(mockParserQuery);
        when(mockParserQuery.getKBestParses(2)).thenReturn(Arrays.asList(new ScoredObject<>(tree1, -10.0), new ScoredObject<>(tree2, -9.0)));

        Annotation mockAnnotation = mock(Annotation.class);
        parserAnnotator.doOneSentence(mockAnnotation, mockSentence);

    }
@Test
    public void testSignatureWithNullAnnotatorName() {
        Properties testProperties = new Properties();
        
        try {
            ParserAnnotator.signature(null, testProperties);
            fail("Expected NullPointerException to be thrown");
        } catch (NullPointerException e) {
            
        }
    }
@Test
    public void testSignatureWithNullProperties() {
        try {
            ParserAnnotator.signature("test", null);
            fail("Expected NullPointerException to be thrown");
        } catch (NullPointerException e) {
            
        }
    }
@Test
    public void testDoOneFailedSentenceWithEmptyTokenList() {
        ParserGrammar mockParser = mock(ParserGrammar.class);
        when(mockParser.getTLPParams()).thenReturn(mock(TreebankLangParserParams.class));

        ParserAnnotator parserAnnotator = new ParserAnnotator(mockParser, true, 50);
        CoreMap mockSentence = mock(CoreMap.class);

        List<CoreLabel> tokens = Collections.emptyList();
        when(mockSentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

        Annotation mockAnnotation = mock(Annotation.class);
        parserAnnotator.doOneFailedSentence(mockAnnotation, mockSentence);

    }
@Test
    public void testMaxHeightHandlingInFinishSentence() {
        ParserGrammar mockParser = mock(ParserGrammar.class);
        when(mockParser.getTLPParams()).thenReturn(mock(TreebankLangParserParams.class));

        ParserAnnotator parserAnnotator = new ParserAnnotator(mockParser, true, 50);

        CoreMap mockSentence = mock(CoreMap.class);
        Tree mockTree = mock(Tree.class);

        List<Tree> trees = Collections.singletonList(mockTree);

    }
@Test
    public void testRequirementsForTaggingEnabled() {
        ParserGrammar mockParser = mock(ParserGrammar.class);
        when(mockParser.getTLPParams()).thenReturn(mock(TreebankLangParserParams.class));
        when(mockParser.requiresTags()).thenReturn(true);

        ParserAnnotator parserAnnotator = new ParserAnnotator(mockParser, true, 50);
        Set<Class<? extends CoreAnnotation>> requiredAnnotations = parserAnnotator.requires();

        assertTrue(requiredAnnotations.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
    }
@Test
    public void testRequirementsForTaggingDisabled() {
        ParserGrammar mockParser = mock(ParserGrammar.class);
        when(mockParser.getTLPParams()).thenReturn(mock(TreebankLangParserParams.class));
        when(mockParser.requiresTags()).thenReturn(false);

        ParserAnnotator parserAnnotator = new ParserAnnotator(mockParser, true, 50);
        Set<Class<? extends CoreAnnotation>> requiredAnnotations = parserAnnotator.requires();

        assertFalse(requiredAnnotations.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
    }
@Test
    public void testConstructorWithEmptyFlagsArray() {
        ParserAnnotator parserAnnotator = new ParserAnnotator("edu/stanford/nlp/models/parser", true, 50, new String[]{});
        assertNotNull(parserAnnotator);
    }
@Test
    public void testConstructorWithNullFlagsArray() {
        ParserAnnotator parserAnnotator = new ParserAnnotator("edu/stanford/nlp/models/parser", true, 50, null);
        assertNotNull(parserAnnotator);
    }
@Test
    public void testDoOneSentenceWithOnlyPunctuation() {
        ParserGrammar mockParser = mock(ParserGrammar.class);
        when(mockParser.getTLPParams()).thenReturn(mock(TreebankLangParserParams.class));
        
        ParserAnnotator parserAnnotator = new ParserAnnotator(mockParser, true, 50);
        CoreMap mockSentence = mock(CoreMap.class);

        CoreLabel token1 = new CoreLabel();
        token1.setWord(",");
        token1.setTag(",");

        CoreLabel token2 = new CoreLabel();
        token2.setWord(".");
        token2.setTag(".");

        List<CoreLabel> tokens = Arrays.asList(token1, token2);
        when(mockSentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

        Annotation mockAnnotation = mock(Annotation.class);
        parserAnnotator.doOneSentence(mockAnnotation, mockSentence);

    }
@Test
    public void testDoOneSentenceWithSingleWord() {
        ParserGrammar mockParser = mock(ParserGrammar.class);
        when(mockParser.getTLPParams()).thenReturn(mock(TreebankLangParserParams.class));
        
        ParserAnnotator parserAnnotator = new ParserAnnotator(mockParser, true, 50);
        CoreMap mockSentence = mock(CoreMap.class);

        CoreLabel token1 = new CoreLabel();
        token1.setWord("Hello");
        token1.setTag("UH");

        List<CoreLabel> tokens = Collections.singletonList(token1);
        when(mockSentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

        Annotation mockAnnotation = mock(Annotation.class);
        parserAnnotator.doOneSentence(mockAnnotation, mockSentence);

    }
@Test
    public void testDoOneSentenceWithInvalidCharacters() {
        ParserGrammar mockParser = mock(ParserGrammar.class);
        when(mockParser.getTLPParams()).thenReturn(mock(TreebankLangParserParams.class));
        
        ParserAnnotator parserAnnotator = new ParserAnnotator(mockParser, true, 50);
        CoreMap mockSentence = mock(CoreMap.class);

        CoreLabel token1 = new CoreLabel();
        token1.setWord("@#%^&");
        token1.setTag("SYM");

        List<CoreLabel> tokens = Collections.singletonList(token1);
        when(mockSentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

        Annotation mockAnnotation = mock(Annotation.class);
        parserAnnotator.doOneSentence(mockAnnotation, mockSentence);

    }
@Test
    public void testDoOneSentenceWithDifferentPOS() {
        ParserGrammar mockParser = mock(ParserGrammar.class);
        when(mockParser.getTLPParams()).thenReturn(mock(TreebankLangParserParams.class));

        ParserAnnotator parserAnnotator = new ParserAnnotator(mockParser, true, 50);
        CoreMap mockSentence = mock(CoreMap.class);

        CoreLabel token1 = new CoreLabel();
        token1.setWord("Can");
        token1.setTag("MD");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("you");
        token2.setTag("PRP");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("parse");
        token3.setTag("VB");

        List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);
        when(mockSentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

        Annotation mockAnnotation = mock(Annotation.class);
        parserAnnotator.doOneSentence(mockAnnotation, mockSentence);

        verify(mockSentence, times(1)).set(eq(TreeCoreAnnotations.TreeAnnotation.class), any(Tree.class));
    }
@Test
    public void testDoOneSentenceWithLongSentenceWithoutMaxLengthRestriction() {
        ParserGrammar mockParser = mock(ParserGrammar.class);
        when(mockParser.getTLPParams()).thenReturn(mock(TreebankLangParserParams.class));

        ParserAnnotator parserAnnotator = new ParserAnnotator(mockParser, true, -1);
        CoreMap mockSentence = mock(CoreMap.class);

        List<CoreLabel> longSentence = new ArrayList<>();
        longSentence.add(new CoreLabel());
        longSentence.add(new CoreLabel());
        longSentence.add(new CoreLabel());
        longSentence.add(new CoreLabel());
        longSentence.add(new CoreLabel());
        longSentence.add(new CoreLabel());
        longSentence.add(new CoreLabel());
        longSentence.add(new CoreLabel());
        longSentence.add(new CoreLabel());
        longSentence.add(new CoreLabel());

        when(mockSentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(longSentence);

        Annotation mockAnnotation = mock(Annotation.class);
        parserAnnotator.doOneSentence(mockAnnotation, mockSentence);

        verify(mockSentence, times(1)).set(eq(TreeCoreAnnotations.TreeAnnotation.class), any(Tree.class));
    }
@Test
    public void testDoOneFailedSentenceWithMalformedTokens() {
        ParserGrammar mockParser = mock(ParserGrammar.class);
        when(mockParser.getTLPParams()).thenReturn(mock(TreebankLangParserParams.class));
        
        ParserAnnotator parserAnnotator = new ParserAnnotator(mockParser, true, 50);
        CoreMap mockSentence = mock(CoreMap.class);

        CoreLabel token1 = new CoreLabel();
        token1.setWord("###");
        token1.setTag(null);

        List<CoreLabel> tokens = Collections.singletonList(token1);
        when(mockSentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

        Annotation mockAnnotation = mock(Annotation.class);
        parserAnnotator.doOneFailedSentence(mockAnnotation, mockSentence);

        assertEquals("XX", token1.tag());
        verify(mockSentence, times(1)).set(eq(TreeCoreAnnotations.TreeAnnotation.class), any(Tree.class));
    }
@Test
    public void testParserAnnotatorWithEmptyModelPath() {
        try {
            new ParserAnnotator("", true, 50, new String[]{"-flag1"});
            fail("Expected an exception for an empty model path");
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException || e instanceof RuntimeException);
        }
    }
@Test
    public void testParserAnnotatorWithWhitespaceModelPath() {
        try {
            new ParserAnnotator("   ", true, 50, new String[]{"-flag1"});
            fail("Expected an exception for a model path with only whitespaces");
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException || e instanceof RuntimeException);
        }
    }
@Test
    public void testDoOneSentenceWithNoTokensAnnotation() {
        ParserGrammar mockParser = mock(ParserGrammar.class);
        when(mockParser.getTLPParams()).thenReturn(mock(TreebankLangParserParams.class));

        ParserAnnotator parserAnnotator = new ParserAnnotator(mockParser, true, 50);
        
        CoreMap mockSentence = mock(CoreMap.class);
        when(mockSentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(null);

        Annotation mockAnnotation = mock(Annotation.class);
        parserAnnotator.doOneSentence(mockAnnotation, mockSentence);

        verify(mockSentence, times(1)).set(eq(TreeCoreAnnotations.TreeAnnotation.class), any(Tree.class));
    }
@Test
    public void testDoOneSentenceWithVaryingSentenceLengths() {
        ParserGrammar mockParser = mock(ParserGrammar.class);
        when(mockParser.getTLPParams()).thenReturn(mock(TreebankLangParserParams.class));
        
        ParserAnnotator parserAnnotator = new ParserAnnotator(mockParser, true, 50);
        CoreMap mockSentence = mock(CoreMap.class);

        List<CoreLabel> tokens1 = Collections.singletonList(new CoreLabel()); 
        List<CoreLabel> tokens2 = Arrays.asList(new CoreLabel(), new CoreLabel(), new CoreLabel()); 
        List<CoreLabel> longSentence = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            longSentence.add(new CoreLabel()); 
        }

        when(mockSentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens1);
        parserAnnotator.doOneSentence(mock(Annotation.class), mockSentence);
        verify(mockSentence, times(1)).set(eq(TreeCoreAnnotations.TreeAnnotation.class), any(Tree.class));

        when(mockSentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens2);
        parserAnnotator.doOneSentence(mock(Annotation.class), mockSentence);
        verify(mockSentence, times(2)).set(eq(TreeCoreAnnotations.TreeAnnotation.class), any(Tree.class));

        when(mockSentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(longSentence);
        parserAnnotator.doOneSentence(mock(Annotation.class), mockSentence);
        verify(mockSentence, times(3)).set(eq(TreeCoreAnnotations.TreeAnnotation.class), any(Tree.class));
    }

@Test
    public void testSignatureWithDifferentConfigurations() {
        Properties properties = new Properties();
        properties.setProperty("parserA.model", "modelA");
        properties.setProperty("parserB.model", "modelB");

        String signatureA = ParserAnnotator.signature("parserA", properties);
        String signatureB = ParserAnnotator.signature("parserB", properties);

        assertTrue(signatureA.contains("parserA.model:modelA"));
        assertTrue(signatureB.contains("parserB.model:modelB"));
        assertNotEquals(signatureA, signatureB);
    }
@Test
    public void testDoOneFailedSentenceWithMissingTags() {
        ParserGrammar mockParser = mock(ParserGrammar.class);
        
        ParserAnnotator parserAnnotator = new ParserAnnotator(mockParser, true, 50);
        CoreMap mockSentence = mock(CoreMap.class);

        CoreLabel token1 = new CoreLabel();
        token1.setWord("UnknownWord");
        token1.setTag(null);

        List<CoreLabel> tokens = Collections.singletonList(token1);
        when(mockSentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

        Annotation mockAnnotation = mock(Annotation.class);
        parserAnnotator.doOneFailedSentence(mockAnnotation, mockSentence);

        assertEquals("XX", token1.tag());
        verify(mockSentence, times(1)).set(eq(TreeCoreAnnotations.TreeAnnotation.class), any(Tree.class));
    }
@Test
    public void testParsingTreeWithUnsupportedParserFeature() {
        ParserGrammar mockParser = mock(ParserGrammar.class);
        when(mockParser.getTLPParams()).thenReturn(mock(TreebankLangParserParams.class));
        when(mockParser.getTLPParams().supportsBasicDependencies()).thenReturn(false);

        ParserAnnotator parserAnnotator = new ParserAnnotator(mockParser, true, 50);
        CoreMap mockSentence = mock(CoreMap.class);
        List<CoreLabel> tokens = Arrays.asList(new CoreLabel(), new CoreLabel());

        when(mockSentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
        Annotation mockAnnotation = mock(Annotation.class);

        parserAnnotator.doOneSentence(mockAnnotation, mockSentence);
    }
@Test
    public void testDoOneSentenceWithMaxSentenceBoundary() {
        ParserGrammar mockParser = mock(ParserGrammar.class);
        when(mockParser.getTLPParams()).thenReturn(mock(TreebankLangParserParams.class));

        ParserAnnotator parserAnnotator = new ParserAnnotator(mockParser, true, 5);
        CoreMap mockSentence = mock(CoreMap.class);
    
        List<CoreLabel> tokens = Arrays.asList(new CoreLabel(), new CoreLabel(), new CoreLabel(), new CoreLabel(), new CoreLabel());

        when(mockSentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
        Annotation mockAnnotation = mock(Annotation.class);

        parserAnnotator.doOneSentence(mockAnnotation, mockSentence);
        verify(mockSentence, times(1)).set(eq(TreeCoreAnnotations.TreeAnnotation.class), any(Tree.class));
    }
@Test
    public void testDoOneSentenceExceedingMaxSentenceLength() {
        ParserGrammar mockParser = mock(ParserGrammar.class);
        when(mockParser.getTLPParams()).thenReturn(mock(TreebankLangParserParams.class));

        ParserAnnotator parserAnnotator = new ParserAnnotator(mockParser, true, 5);
        CoreMap mockSentence = mock(CoreMap.class);
    
        List<CoreLabel> tokens = Arrays.asList(new CoreLabel(), new CoreLabel(), new CoreLabel(), new CoreLabel(), new CoreLabel(), new CoreLabel());

        when(mockSentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
        Annotation mockAnnotation = mock(Annotation.class);

        parserAnnotator.doOneSentence(mockAnnotation, mockSentence);
        verify(mockSentence, never()).set(eq(TreeCoreAnnotations.TreeAnnotation.class), any(Tree.class));
    }
@Test
    public void testParserAnnotatorWithNullParserLocation() {
        try {
            new ParserAnnotator(null, true, 50, new String[]{"-testFlag"});
            fail("Expected an exception for a null parser location");
        } catch (NullPointerException | IllegalArgumentException e) {
            
        }
    }
@Test
    public void testDoOneSentenceWithEmptyTokenWords() {
        ParserGrammar mockParser = mock(ParserGrammar.class);
        when(mockParser.getTLPParams()).thenReturn(mock(TreebankLangParserParams.class));
        
        ParserAnnotator parserAnnotator = new ParserAnnotator(mockParser, true, 50);
        CoreMap mockSentence = mock(CoreMap.class);

        CoreLabel token = new CoreLabel();
        token.setWord("");
        token.setTag("NN");

        List<CoreLabel> tokens = Collections.singletonList(token);
        when(mockSentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

        Annotation mockAnnotation = mock(Annotation.class);
        parserAnnotator.doOneSentence(mockAnnotation, mockSentence);

        verify(mockSentence, times(1)).set(eq(TreeCoreAnnotations.TreeAnnotation.class), any(Tree.class));
    }
@Test
    public void testDoOneSentenceWithNullPartOfSpeechAnnotation() {
        ParserGrammar mockParser = mock(ParserGrammar.class);
        when(mockParser.getTLPParams()).thenReturn(mock(TreebankLangParserParams.class));

        ParserAnnotator parserAnnotator = new ParserAnnotator(mockParser, true, 50);
        CoreMap mockSentence = mock(CoreMap.class);

        CoreLabel token = new CoreLabel();
        token.setWord("Hello");
        token.setTag(null);  

        List<CoreLabel> tokens = Collections.singletonList(token);
        when(mockSentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
        
        Annotation mockAnnotation = mock(Annotation.class);
        parserAnnotator.doOneSentence(mockAnnotation, mockSentence);

        verify(mockSentence, times(1)).set(eq(TreeCoreAnnotations.TreeAnnotation.class), any(Tree.class));
    }
@Test
    public void testDoOneSentenceWithWhitespaceTokens() {
        ParserGrammar mockParser = mock(ParserGrammar.class);
        when(mockParser.getTLPParams()).thenReturn(mock(TreebankLangParserParams.class));

        ParserAnnotator parserAnnotator = new ParserAnnotator(mockParser, true, 50);
        CoreMap mockSentence = mock(CoreMap.class);

        CoreLabel token = new CoreLabel();
        token.setWord("   "); 
        token.setTag("NN");

        List<CoreLabel> tokens = Collections.singletonList(token);
        when(mockSentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
        
        Annotation mockAnnotation = mock(Annotation.class);
        parserAnnotator.doOneSentence(mockAnnotation, mockSentence);

        verify(mockSentence, times(1)).set(eq(TreeCoreAnnotations.TreeAnnotation.class), any(Tree.class));
    }
@Test
    public void testDoOneSentenceWithDuplicateTokens() {
        ParserGrammar mockParser = mock(ParserGrammar.class);
        when(mockParser.getTLPParams()).thenReturn(mock(TreebankLangParserParams.class));

        ParserAnnotator parserAnnotator = new ParserAnnotator(mockParser, true, 50);
        CoreMap mockSentence = mock(CoreMap.class);

        CoreLabel token1 = new CoreLabel();
        token1.setWord("word");
        token1.setTag("NN");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("word");
        token2.setTag("NN");

        List<CoreLabel> tokens = Arrays.asList(token1, token2);
        when(mockSentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

        Annotation mockAnnotation = mock(Annotation.class);
        parserAnnotator.doOneSentence(mockAnnotation, mockSentence);

        verify(mockSentence, times(1)).set(eq(TreeCoreAnnotations.TreeAnnotation.class), any(Tree.class));
    }
@Test
    public void testParserAnnotatorWithNonExistentModelPath() {
        try {
            new ParserAnnotator("non_existent_path", true, 50, new String[]{"-testFlag"});
            fail("Expected an exception for a nonexistent model path");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Error loading parser model"));
        }
    }
@Test
    public void testDoOneFailedSentenceWithWhitespaceTokens() {
        ParserGrammar mockParser = mock(ParserGrammar.class);
        when(mockParser.getTLPParams()).thenReturn(mock(TreebankLangParserParams.class));

        ParserAnnotator parserAnnotator = new ParserAnnotator(mockParser, true, 50);
        CoreMap mockSentence = mock(CoreMap.class);

        CoreLabel token = new CoreLabel();
        token.setWord("   "); 
        token.setTag(null);

        List<CoreLabel> tokens = Collections.singletonList(token);
        when(mockSentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
        
        Annotation mockAnnotation = mock(Annotation.class);
        parserAnnotator.doOneFailedSentence(mockAnnotation, mockSentence);

        assertEquals("XX", token.tag());
        verify(mockSentence, times(1)).set(eq(TreeCoreAnnotations.TreeAnnotation.class), any(Tree.class));
    }
@Test
    public void testSignatureWithMinimalProperties() {
        Properties testProperties = new Properties();
        testProperties.setProperty("test.model", "minimal");

        String signature = ParserAnnotator.signature("test", testProperties);

        assertTrue(signature.contains("test.model:minimal"));
    }
@Test
    public void testSignatureWithComplexProperties() {
        Properties testProperties = new Properties();
        testProperties.setProperty("annotator.model", "advanced_model");
        testProperties.setProperty("annotator.debug", "true");
        testProperties.setProperty("annotator.extradependencies", "FULL");

        String signature = ParserAnnotator.signature("annotator", testProperties);

        assertTrue(signature.contains("annotator.model:advanced_model"));
        assertTrue(signature.contains("annotator.debug:true"));
        assertTrue(signature.contains("annotator.extradependencies:full"));
    }
@Test
    public void testConstructorWithLongModelPath() {
        try {
            String longModelPath = "edu/stanford/nlp/models/parser";
            new ParserAnnotator(longModelPath, true, 50, new String[]{"-flag1"});
            fail("Expected an exception for an excessively long model path.");
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException || e instanceof RuntimeException);
        }
    }
@Test
    public void testDoOneSentenceWithNullWord() {
        ParserGrammar mockParser = mock(ParserGrammar.class);
        when(mockParser.getTLPParams()).thenReturn(mock(TreebankLangParserParams.class));
        
        ParserAnnotator parserAnnotator = new ParserAnnotator(mockParser, true, 50);
        CoreMap mockSentence = mock(CoreMap.class);

        CoreLabel token = new CoreLabel();
        token.setWord(null);  
        token.setTag("NN");

        List<CoreLabel> tokens = Collections.singletonList(token);
        when(mockSentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

        Annotation mockAnnotation = mock(Annotation.class);
        parserAnnotator.doOneSentence(mockAnnotation, mockSentence);
  }
@Test
    public void testDoOneSentenceWithRepeatedPunctuation() {
        ParserGrammar mockParser = mock(ParserGrammar.class);
        when(mockParser.getTLPParams()).thenReturn(mock(TreebankLangParserParams.class));

        ParserAnnotator parserAnnotator = new ParserAnnotator(mockParser, true, 50);
        CoreMap mockSentence = mock(CoreMap.class);

        CoreLabel token1 = new CoreLabel();
        token1.setWord("...");
        token1.setTag("PUNC");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("!!!");
        token2.setTag("PUNC");

        List<CoreLabel> tokens = Arrays.asList(token1, token2);
        when(mockSentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
        
        Annotation mockAnnotation = mock(Annotation.class);
        parserAnnotator.doOneSentence(mockAnnotation, mockSentence);

        verify(mockSentence, times(1)).set(eq(TreeCoreAnnotations.TreeAnnotation.class), any(Tree.class));
    }
@Test
    public void testDoOneSentenceWithExcessivelyLongWord() {
        ParserGrammar mockParser = mock(ParserGrammar.class);
        when(mockParser.getTLPParams()).thenReturn(mock(TreebankLangParserParams.class));

        ParserAnnotator parserAnnotator = new ParserAnnotator(mockParser, true, 50);
        CoreMap mockSentence = mock(CoreMap.class);

        String longWord = "word";
        CoreLabel token = new CoreLabel();
        token.setWord(longWord);
        token.setTag("NN");

        List<CoreLabel> tokens = Collections.singletonList(token);
        when(mockSentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
        
        Annotation mockAnnotation = mock(Annotation.class);
        parserAnnotator.doOneSentence(mockAnnotation, mockSentence);
  }
@Test
    public void testDoOneSentenceWithAdjacentIdenticalWords() {
        ParserGrammar mockParser = mock(ParserGrammar.class);
        when(mockParser.getTLPParams()).thenReturn(mock(TreebankLangParserParams.class));

        ParserAnnotator parserAnnotator = new ParserAnnotator(mockParser, true, 50);
        CoreMap mockSentence = mock(CoreMap.class);

        CoreLabel token1 = new CoreLabel();
        token1.setWord("repeat");
        token1.setTag("VB");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("repeat");
        token2.setTag("VB");

        List<CoreLabel> tokens = Arrays.asList(token1, token2);
        when(mockSentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

        Annotation mockAnnotation = mock(Annotation.class);
        parserAnnotator.doOneSentence(mockAnnotation, mockSentence);
  }
@Test
    public void testDoOneFailedSentenceWithNullTokensList() {
        ParserGrammar mockParser = mock(ParserGrammar.class);
        
        ParserAnnotator parserAnnotator = new ParserAnnotator(mockParser, true, 50);
        CoreMap mockSentence = mock(CoreMap.class);
        
        when(mockSentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(null);

        Annotation mockAnnotation = mock(Annotation.class);
        parserAnnotator.doOneFailedSentence(mockAnnotation, mockSentence);
  }

@Test
    public void testSignatureWithEmptyStringProperty() {
        Properties testProperties = new Properties();
        testProperties.setProperty("test.model", "");

        String signature = ParserAnnotator.signature("test", testProperties);

        assertTrue(signature.contains("test.model:"));
    }
@Test
    public void testSignatureWithWhitespaceInProperties() {
        Properties testProperties = new Properties();
        testProperties.setProperty("annotator.model", "  spaces_model  ");
        testProperties.setProperty("annotator.debug", "   true   ");
        testProperties.setProperty("annotator.extradependencies", "  FULL  ");

        String signature = ParserAnnotator.signature("annotator", testProperties);

        assertTrue(signature.contains("annotator.model:  spaces_model  "));
        assertTrue(signature.contains("annotator.debug:   true   "));
        assertTrue(signature.contains("annotator.extradependencies:  full  "));
    } 
}