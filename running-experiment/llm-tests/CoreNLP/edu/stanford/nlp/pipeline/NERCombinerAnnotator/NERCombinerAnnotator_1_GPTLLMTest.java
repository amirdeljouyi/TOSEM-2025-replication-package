package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.Timex;
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.Pair;
import org.junit.Test;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;

import static org.junit.Assert.*;

public class NERCombinerAnnotator_1_GPTLLMTest {

 @Test
    public void testConstructorWithProperties() throws IOException {
        Properties properties = new Properties();
        properties.setProperty("ner.model", "edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz");
        properties.setProperty("ner.applyFineGrained", "true");
        properties.setProperty("ner.buildEntityMentions", "true");

        NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

        assertNotNull(annotator);
    }
@Test
    public void testAnnotateBasicSentence() throws IOException {
        Properties properties = new Properties();
        NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

        Annotation annotation = new Annotation("Barack Obama was the 44th President of the United States.");
        List<CoreLabel> tokens = new ArrayList<>();
        CoreLabel token1 = new CoreLabel();
        token1.setWord("Barack");
        token1.set(CoreAnnotations.IndexAnnotation.class, 0);
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Obama");
        token2.set(CoreAnnotations.IndexAnnotation.class, 1);
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("was");
        token3.set(CoreAnnotations.IndexAnnotation.class, 2);
        tokens.add(token3);

        CoreLabel token4 = new CoreLabel();
        token4.setWord("the");
        token4.set(CoreAnnotations.IndexAnnotation.class, 3);
        tokens.add(token4);

        CoreLabel token5 = new CoreLabel();
        token5.setWord("44th");
        token5.set(CoreAnnotations.IndexAnnotation.class, 4);
        tokens.add(token5);

        CoreLabel token6 = new CoreLabel();
        token6.setWord("President");
        token6.set(CoreAnnotations.IndexAnnotation.class, 5);
        tokens.add(token6);

        CoreLabel token7 = new CoreLabel();
        token7.setWord("of");
        token7.set(CoreAnnotations.IndexAnnotation.class, 6);
        tokens.add(token7);

        CoreLabel token8 = new CoreLabel();
        token8.setWord("the");
        token8.set(CoreAnnotations.IndexAnnotation.class, 7);
        tokens.add(token8);

        CoreLabel token9 = new CoreLabel();
        token9.setWord("United");
        token9.set(CoreAnnotations.IndexAnnotation.class, 8);
        tokens.add(token9);

        CoreLabel token10 = new CoreLabel();
        token10.setWord("States");
        token10.set(CoreAnnotations.IndexAnnotation.class, 9);
        tokens.add(token10);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotator.annotate(annotation);

        assertEquals("PERSON", token1.ner());
        assertEquals("PERSON", token2.ner());
        assertEquals("ORDINAL", token5.ner());
        assertEquals("LOCATION", token9.ner());
        assertEquals("LOCATION", token10.ner());
    }
@Test
    public void testAnnotateWithoutEntities() throws IOException {
        Properties properties = new Properties();
        NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

        Annotation annotation = new Annotation("This is a simple test.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("This");
        token1.set(CoreAnnotations.IndexAnnotation.class, 0);
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("is");
        token2.set(CoreAnnotations.IndexAnnotation.class, 1);
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("a");
        token3.set(CoreAnnotations.IndexAnnotation.class, 2);
        tokens.add(token3);

        CoreLabel token4 = new CoreLabel();
        token4.setWord("simple");
        token4.set(CoreAnnotations.IndexAnnotation.class, 3);
        tokens.add(token4);

        CoreLabel token5 = new CoreLabel();
        token5.setWord("test");
        token5.set(CoreAnnotations.IndexAnnotation.class, 4);
        tokens.add(token5);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        assertNull(token1.ner());
        assertNull(token2.ner());
        assertNull(token3.ner());
        assertNull(token4.ner());
        assertNull(token5.ner());
    }
@Test
    public void testDoOneFailedSentence() throws IOException {
        Properties properties = new Properties();
        NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

        Annotation annotation = new Annotation("Test failure handling.");
        CoreMap sentence = new Annotation("Test failure handling.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("Test");
        token1.set(CoreAnnotations.IndexAnnotation.class, 0);
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("failure");
        token2.set(CoreAnnotations.IndexAnnotation.class, 1);
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("handling");
        token3.set(CoreAnnotations.IndexAnnotation.class, 2);
        tokens.add(token3);

        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.doOneFailedSentence(annotation, sentence);

        assertNotNull(token1.ner());
        assertNotNull(token2.ner());
        assertNotNull(token3.ner());
    }
@Test
    public void testMergeTokens() {
        CoreLabel first = new CoreLabel();
        first.setWord("high-");
        first.set(CoreAnnotations.IndexAnnotation.class, 0);

        CoreLabel second = new CoreLabel();
        second.setWord("quality");
        second.set(CoreAnnotations.IndexAnnotation.class, 1);

        NERCombinerAnnotator.mergeTokens(first, second);

        assertEquals("high-quality", first.word());
    }
@Test
    public void testEmptyTextAnnotation() throws IOException {
        Properties properties = new Properties();
        NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

        Annotation annotation = new Annotation("");
        annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

        annotator.annotate(annotation);

        assertTrue(annotation.get(CoreAnnotations.TokensAnnotation.class).isEmpty());
    }
@Test
    public void testSingleWordAnnotation() throws IOException {
        Properties properties = new Properties();
        NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

        Annotation annotation = new Annotation("Apple");
        List<CoreLabel> tokens = new ArrayList<>();
        CoreLabel token = new CoreLabel();
        token.setWord("Apple");
        token.set(CoreAnnotations.IndexAnnotation.class, 0);
        tokens.add(token);
        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotator.annotate(annotation);

        assertEquals("ORGANIZATION", token.ner());
    }
@Test
    public void testPunctuationOnlyAnnotation() throws IOException {
        Properties properties = new Properties();
        NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

        Annotation annotation = new Annotation("!!!");
        List<CoreLabel> tokens = new ArrayList<>();
        CoreLabel token = new CoreLabel();
        token.setWord("!!!");
        token.set(CoreAnnotations.IndexAnnotation.class, 0);
        tokens.add(token);
        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotator.annotate(annotation);

        assertNull(token.ner()); 
    }
@Test
    public void testMultipleSentencesAnnotation() throws IOException {
        Properties properties = new Properties();
        NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

        String text = "Elon Musk founded SpaceX. He also owns Tesla.";
        Annotation annotation = new Annotation(text);
        List<CoreLabel> tokens1 = new ArrayList<>();
        CoreLabel token1 = new CoreLabel();
        token1.setWord("Elon");
        token1.set(CoreAnnotations.IndexAnnotation.class, 0);
        tokens1.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Musk");
        token2.set(CoreAnnotations.IndexAnnotation.class, 1);
        tokens1.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("founded");
        token3.set(CoreAnnotations.IndexAnnotation.class, 2);
        tokens1.add(token3);

        List<CoreLabel> tokens2 = new ArrayList<>();
        CoreLabel token4 = new CoreLabel();
        token4.setWord("He");
        token4.set(CoreAnnotations.IndexAnnotation.class, 3);
        tokens2.add(token4);

        CoreLabel token5 = new CoreLabel();
        token5.setWord("Tesla");
        token5.set(CoreAnnotations.IndexAnnotation.class, 4);
        tokens2.add(token5);

        List<CoreMap> sentences = new ArrayList<>();
        Annotation sentence1 = new Annotation("Elon Musk founded SpaceX.");
        sentence1.set(CoreAnnotations.TokensAnnotation.class, tokens1);
        sentences.add(sentence1);

        Annotation sentence2 = new Annotation("He also owns Tesla.");
        sentence2.set(CoreAnnotations.TokensAnnotation.class, tokens2);
        sentences.add(sentence2);

        annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

        annotator.annotate(annotation);

        assertEquals("PERSON", token1.ner());
        assertEquals("PERSON", token2.ner());
        assertEquals("ORGANIZATION", token5.ner());
    }
@Test
    public void testNumberEntityRecognition() throws IOException {
        Properties properties = new Properties();
        NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

        Annotation annotation = new Annotation("John bought 100 shares.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("John");
        token1.set(CoreAnnotations.IndexAnnotation.class, 0);
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("bought");
        token2.set(CoreAnnotations.IndexAnnotation.class, 1);
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("100");
        token3.set(CoreAnnotations.IndexAnnotation.class, 2);
        tokens.add(token3);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotator.annotate(annotation);

        assertEquals("PERSON", token1.ner());
        assertEquals("NUMBER", token3.ner());
    }
@Test
    public void testUnknownWordEntityHandling() throws IOException {
        Properties properties = new Properties();
        NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

        Annotation annotation = new Annotation("Blorf is a new startup.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("Blorf");
        token1.set(CoreAnnotations.IndexAnnotation.class, 0);
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("is");
        token2.set(CoreAnnotations.IndexAnnotation.class, 1);
        tokens.add(token2);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotator.annotate(annotation);

        assertNull(token1.ner()); 
    }
@Test
    public void testSpanishTextAnnotation() throws IOException {
        Properties properties = new Properties();
        properties.setProperty("ner.language", "es");
        NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

        Annotation annotation = new Annotation("Madrid es la capital de España.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("Madrid");
        token1.set(CoreAnnotations.IndexAnnotation.class, 0);
        tokens.add(token1);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotator.annotate(annotation);

        assertEquals("LOCATION", token1.ner());
    }
@Test
    public void testLongSentenceAnnotation() throws IOException {
        Properties properties = new Properties();
        properties.setProperty("ner.maxlen", "5"); 
        NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

        Annotation annotation = new Annotation("This is a very long sentence that should exceed the max length.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("This");
        token1.set(CoreAnnotations.IndexAnnotation.class, 0);
        tokens.add(token1);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotator.annotate(annotation);

        assertNull(token1.ner()); 
    }
@Test
    public void testMixedCaseEntityRecognition() throws IOException {
        Properties properties = new Properties();
        NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

        Annotation annotation = new Annotation("apple is different from Apple Inc.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("apple");
        token1.set(CoreAnnotations.IndexAnnotation.class, 0);
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("is");
        token2.set(CoreAnnotations.IndexAnnotation.class, 1);
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("different");
        token3.set(CoreAnnotations.IndexAnnotation.class, 2);
        tokens.add(token3);

        CoreLabel token4 = new CoreLabel();
        token4.setWord("from");
        token4.set(CoreAnnotations.IndexAnnotation.class, 3);
        tokens.add(token4);

        CoreLabel token5 = new CoreLabel();
        token5.setWord("Apple");
        token5.set(CoreAnnotations.IndexAnnotation.class, 4);
        tokens.add(token5);

        CoreLabel token6 = new CoreLabel();
        token6.setWord("Inc.");
        token6.set(CoreAnnotations.IndexAnnotation.class, 5);
        tokens.add(token6);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotator.annotate(annotation);

        assertNull(token1.ner());
        assertEquals("ORGANIZATION", token5.ner());
        assertEquals("ORGANIZATION", token6.ner());
    }
@Test
    public void testAmbiguousNamedEntities() throws IOException {
        Properties properties = new Properties();
        NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

        Annotation annotation = new Annotation("Paris is a city but also a name.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("Paris");
        token1.set(CoreAnnotations.IndexAnnotation.class, 0);
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("is");
        token2.set(CoreAnnotations.IndexAnnotation.class, 1);
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("a");
        token3.set(CoreAnnotations.IndexAnnotation.class, 2);
        tokens.add(token3);

        CoreLabel token4 = new CoreLabel();
        token4.setWord("city");
        token4.set(CoreAnnotations.IndexAnnotation.class, 3);
        tokens.add(token4);

        CoreLabel token5 = new CoreLabel();
        token5.setWord("but");
        token5.set(CoreAnnotations.IndexAnnotation.class, 4);
        tokens.add(token5);

        CoreLabel token6 = new CoreLabel();
        token6.setWord("also");
        token6.set(CoreAnnotations.IndexAnnotation.class, 5);
        tokens.add(token6);

        CoreLabel token7 = new CoreLabel();
        token7.setWord("a");
        token7.set(CoreAnnotations.IndexAnnotation.class, 6);
        tokens.add(token7);

        CoreLabel token8 = new CoreLabel();
        token8.setWord("name");
        token8.set(CoreAnnotations.IndexAnnotation.class, 7);
        tokens.add(token8);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotator.annotate(annotation);

        assertEquals("LOCATION", token1.ner());
    }
@Test
    public void testMultiWordEntityRecognition() throws IOException {
        Properties properties = new Properties();
        NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

        Annotation annotation = new Annotation("The United Nations is meeting today.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("The");
        token1.set(CoreAnnotations.IndexAnnotation.class, 0);
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("United");
        token2.set(CoreAnnotations.IndexAnnotation.class, 1);
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("Nations");
        token3.set(CoreAnnotations.IndexAnnotation.class, 2);
        tokens.add(token3);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotator.annotate(annotation);

        assertEquals("ORGANIZATION", token2.ner());
        assertEquals("ORGANIZATION", token3.ner());
    }
@Test
    public void testNumericEntityRecognitionWithUnits() throws IOException {
        Properties properties = new Properties();
        NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

        Annotation annotation = new Annotation("The box weighs 10 kg.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("The");
        token1.set(CoreAnnotations.IndexAnnotation.class, 0);
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("box");
        token2.set(CoreAnnotations.IndexAnnotation.class, 1);
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("weighs");
        token3.set(CoreAnnotations.IndexAnnotation.class, 2);
        tokens.add(token3);

        CoreLabel token4 = new CoreLabel();
        token4.setWord("10");
        token4.set(CoreAnnotations.IndexAnnotation.class, 3);
        tokens.add(token4);

        CoreLabel token5 = new CoreLabel();
        token5.setWord("kg");
        token5.set(CoreAnnotations.IndexAnnotation.class, 4);
        tokens.add(token5);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotator.annotate(annotation);

        assertEquals("NUMBER", token4.ner());
        assertNull(token5.ner());
    }
@Test
    public void testHyphenSeparatedNames() throws IOException {
        Properties properties = new Properties();
        NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

        Annotation annotation = new Annotation("Jean-Claude Van Damme is an actor.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("Jean-Claude");
        token1.set(CoreAnnotations.IndexAnnotation.class, 0);
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Van");
        token2.set(CoreAnnotations.IndexAnnotation.class, 1);
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("Damme");
        token3.set(CoreAnnotations.IndexAnnotation.class, 2);
        tokens.add(token3);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotator.annotate(annotation);

        assertEquals("PERSON", token1.ner());
        assertEquals("PERSON", token2.ner());
        assertEquals("PERSON", token3.ner());
    }
@Test
    public void testSpecialCharactersHandling() throws IOException {
        Properties properties = new Properties();
        NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

        Annotation annotation = new Annotation("@Google is a famous company.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("@Google");
        token1.set(CoreAnnotations.IndexAnnotation.class, 0);
        tokens.add(token1);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

        annotator.annotate(annotation);

        assertEquals("ORGANIZATION", token1.ner());
    }
@Test
    public void testAnnotationWithSingleCharacterEntities() throws IOException {
        Properties properties = new Properties();
        NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

        Annotation annotation = new Annotation("A is a letter, but B is a grade.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("A");
        token1.set(CoreAnnotations.IndexAnnotation.class, 0);
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("B");
        token2.set(CoreAnnotations.IndexAnnotation.class, 1);
        tokens.add(token2);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        assertNull(token1.ner()); 
        assertNull(token2.ner()); 
    }
@Test
    public void testNonStandardWordSeparators() throws IOException {
        Properties properties = new Properties();
        NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

        Annotation annotation = new Annotation("New York-based analyst reported.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("New");
        token1.set(CoreAnnotations.IndexAnnotation.class, 0);
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("York");
        token2.set(CoreAnnotations.IndexAnnotation.class, 1);
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("-");
        token3.set(CoreAnnotations.IndexAnnotation.class, 2);
        tokens.add(token3);

        CoreLabel token4 = new CoreLabel();
        token4.setWord("based");
        token4.set(CoreAnnotations.IndexAnnotation.class, 3);
        tokens.add(token4);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        assertEquals("LOCATION", token1.ner());
        assertEquals("LOCATION", token2.ner());
        assertNull(token3.ner()); 
        assertNull(token4.ner()); 
    }
@Test
    public void testAnnotationWithUncommonCurrency() throws IOException {
        Properties properties = new Properties();
        NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

        Annotation annotation = new Annotation("He paid 500 yen at the store.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("500");
        token1.set(CoreAnnotations.IndexAnnotation.class, 0);
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("yen");
        token2.set(CoreAnnotations.IndexAnnotation.class, 1);
        tokens.add(token2);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        assertEquals("NUMBER", token1.ner()); 
        assertEquals("MONEY", token2.ner()); 
    }
@Test
    public void testAnnotationWithMultipleTimeExpressions() throws IOException {
        Properties properties = new Properties();
        NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

        Annotation annotation = new Annotation("She left on January 1st at 3:00 PM.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("January");
        token1.set(CoreAnnotations.IndexAnnotation.class, 0);
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("1st");
        token2.set(CoreAnnotations.IndexAnnotation.class, 1);
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("3:00");
        token3.set(CoreAnnotations.IndexAnnotation.class, 2);
        tokens.add(token3);

        CoreLabel token4 = new CoreLabel();
        token4.setWord("PM");
        token4.set(CoreAnnotations.IndexAnnotation.class, 3);
        tokens.add(token4);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        assertEquals("DATE", token1.ner());
        assertEquals("DATE", token2.ner());
        assertEquals("TIME", token3.ner());
        assertEquals("TIME", token4.ner());
    }
@Test
    public void testAnnotationWithAbbreviations() throws IOException {
        Properties properties = new Properties();
        NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

        Annotation annotation = new Annotation("Dr. Smith went to NASA HQ.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("Dr.");
        token1.set(CoreAnnotations.IndexAnnotation.class, 0);
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Smith");
        token2.set(CoreAnnotations.IndexAnnotation.class, 1);
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("NASA");
        token3.set(CoreAnnotations.IndexAnnotation.class, 2);
        tokens.add(token3);

        CoreLabel token4 = new CoreLabel();
        token4.setWord("HQ");
        token4.set(CoreAnnotations.IndexAnnotation.class, 3);
        tokens.add(token4);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        assertEquals("TITLE", token1.ner()); 
        assertEquals("PERSON", token2.ner());
        assertEquals("ORGANIZATION", token3.ner()); 
        assertEquals("ORGANIZATION", token4.ner()); 
    }
@Test
    public void testAnnotationWithMixedNumericFormats() throws IOException {
        Properties properties = new Properties();
        NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

        Annotation annotation = new Annotation("The company made $1.5 million in Q3 2023.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("$");
        token1.set(CoreAnnotations.IndexAnnotation.class, 0);
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("1.5");
        token2.set(CoreAnnotations.IndexAnnotation.class, 1);
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("million");
        token3.set(CoreAnnotations.IndexAnnotation.class, 2);
        tokens.add(token3);

        CoreLabel token4 = new CoreLabel();
        token4.setWord("Q3");
        token4.set(CoreAnnotations.IndexAnnotation.class, 3);
        tokens.add(token4);

        CoreLabel token5 = new CoreLabel();
        token5.setWord("2023");
        token5.set(CoreAnnotations.IndexAnnotation.class, 4);
        tokens.add(token5);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        assertEquals("MONEY", token2.ner());
        assertEquals("MONEY", token3.ner());
        assertEquals("DATE", token5.ner());
    }
@Test
    public void testAnnotationWithForeignLanguageTextMixedWithEnglish() throws IOException {
        Properties properties = new Properties();
        properties.setProperty("ner.language", "en");
        NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

        Annotation annotation = new Annotation("Bonjour! I visited Paris last year.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("Bonjour");
        token1.set(CoreAnnotations.IndexAnnotation.class, 0);
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Paris");
        token2.set(CoreAnnotations.IndexAnnotation.class, 1);
        tokens.add(token2);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        assertNull(token1.ner()); 
        assertEquals("LOCATION", token2.ner()); 
    }
@Test
    public void testAnnotationOnCompletelyNumericSentence() throws IOException {
        Properties properties = new Properties();
        NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

        Annotation annotation = new Annotation("2024 1000 23.5");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("2024");
        token1.set(CoreAnnotations.IndexAnnotation.class, 0);
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("1000");
        token2.set(CoreAnnotations.IndexAnnotation.class, 1);
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("23.5");
        token3.set(CoreAnnotations.IndexAnnotation.class, 2);
        tokens.add(token3);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        assertEquals("DATE", token1.ner());
        assertEquals("NUMBER", token2.ner());
        assertEquals("NUMBER", token3.ner());
    }
@Test
    public void testAnnotationWithRepeatedNamedEntities() throws IOException {
        Properties properties = new Properties();
        NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

        Annotation annotation = new Annotation("Google announced Google Pixel.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("Google");
        token1.set(CoreAnnotations.IndexAnnotation.class, 0);
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("announced");
        token2.set(CoreAnnotations.IndexAnnotation.class, 1);
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("Google");
        token3.set(CoreAnnotations.IndexAnnotation.class, 2);
        tokens.add(token3);

        CoreLabel token4 = new CoreLabel();
        token4.setWord("Pixel");
        token4.set(CoreAnnotations.IndexAnnotation.class, 3);
        tokens.add(token4);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        assertEquals("ORGANIZATION", token1.ner());
        assertEquals("ORGANIZATION", token3.ner());
        assertEquals("PRODUCT", token4.ner());
    }
@Test
    public void testAnnotationWithEmailAddress() throws IOException {
        Properties properties = new Properties();
        NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

        Annotation annotation = new Annotation("Contact support at help@example.com.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("help@example.com");
        token1.set(CoreAnnotations.IndexAnnotation.class, 0);
        tokens.add(token1);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        assertNull(token1.ner()); 
    }
@Test
    public void testAnnotationOnSentenceWithOnlySymbols() throws IOException {
        Properties properties = new Properties();
        NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

        Annotation annotation = new Annotation("# $ % & * @ ??");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("#");
        token1.set(CoreAnnotations.IndexAnnotation.class, 0);
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("$");
        token2.set(CoreAnnotations.IndexAnnotation.class, 1);
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("@");
        token3.set(CoreAnnotations.IndexAnnotation.class, 2);
        tokens.add(token3);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        assertNull(token1.ner());
        assertNull(token2.ner());
        assertNull(token3.ner());
    }
@Test
    public void testAnnotationWithMisspelledNamedEntities() throws IOException {
        Properties properties = new Properties();
        NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

        Annotation annotation = new Annotation("Gooogle is a famous tech company.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("Gooogle");
        token1.set(CoreAnnotations.IndexAnnotation.class, 0);
        tokens.add(token1);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        assertNull(token1.ner()); 
    }
@Test
    public void testAnnotationWithAcronym() throws IOException {
        Properties properties = new Properties();
        NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

        Annotation annotation = new Annotation("NASA launched Artemis 1.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("NASA");
        token1.set(CoreAnnotations.IndexAnnotation.class, 0);
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Artemis");
        token2.set(CoreAnnotations.IndexAnnotation.class, 1);
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("1");
        token3.set(CoreAnnotations.IndexAnnotation.class, 2);
        tokens.add(token3);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        assertEquals("ORGANIZATION", token1.ner());
        assertEquals("PRODUCT", token2.ner()); 
        assertEquals("NUMBER", token3.ner());
    }
@Test
    public void testAnnotationWithDateRange() throws IOException {
        Properties properties = new Properties();
        NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

        Annotation annotation = new Annotation("The event runs from June 1 to June 10.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("June");
        token1.set(CoreAnnotations.IndexAnnotation.class, 0);
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("1");
        token2.set(CoreAnnotations.IndexAnnotation.class, 1);
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("to");
        token3.set(CoreAnnotations.IndexAnnotation.class, 2);
        tokens.add(token3);

        CoreLabel token4 = new CoreLabel();
        token4.setWord("June");
        token4.set(CoreAnnotations.IndexAnnotation.class, 3);
        tokens.add(token4);

        CoreLabel token5 = new CoreLabel();
        token5.setWord("10");
        token5.set(CoreAnnotations.IndexAnnotation.class, 4);
        tokens.add(token5);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        assertEquals("DATE", token1.ner());
        assertEquals("DATE", token2.ner());
        assertEquals("DATE", token4.ner());
        assertEquals("DATE", token5.ner());
    }
@Test
    public void testAnnotationWithReversedFullNames() throws IOException {
        Properties properties = new Properties();
        NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

        Annotation annotation = new Annotation("Doe, John attended the conference.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("Doe,");
        token1.set(CoreAnnotations.IndexAnnotation.class, 0);
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("John");
        token2.set(CoreAnnotations.IndexAnnotation.class, 1);
        tokens.add(token2);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        assertEquals("PERSON", token2.ner()); 
    }
@Test
    public void testAnnotationWithVeryLongEntityName() throws IOException {
        Properties properties = new Properties();
        NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

        Annotation annotation = new Annotation("His full name is Jonathan Alexander Christopher Vincent Williams III.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("Jonathan");
        token1.set(CoreAnnotations.IndexAnnotation.class, 0);
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Alexander");
        token2.set(CoreAnnotations.IndexAnnotation.class, 1);
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("Christopher");
        token3.set(CoreAnnotations.IndexAnnotation.class, 2);
        tokens.add(token3);

        CoreLabel token4 = new CoreLabel();
        token4.setWord("Vincent");
        token4.set(CoreAnnotations.IndexAnnotation.class, 3);
        tokens.add(token4);

        CoreLabel token5 = new CoreLabel();
        token5.setWord("Williams");
        token5.set(CoreAnnotations.IndexAnnotation.class, 4);
        tokens.add(token5);

        CoreLabel token6 = new CoreLabel();
        token6.setWord("III");
        token6.set(CoreAnnotations.IndexAnnotation.class, 5);
        tokens.add(token6);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        assertEquals("PERSON", token1.ner());
        assertEquals("PERSON", token2.ner());
        assertEquals("PERSON", token3.ner());
        assertEquals("PERSON", token4.ner());
        assertEquals("PERSON", token5.ner());
        assertEquals("ORDINAL", token6.ner()); 
    }
@Test
    public void testAnnotationWithURLs() throws IOException {
        Properties properties = new Properties();
        NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

        Annotation annotation = new Annotation("Visit https://www.example.com for more details.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("https://www.example.com");
        token1.set(CoreAnnotations.IndexAnnotation.class, 0);
        tokens.add(token1);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        assertNull(token1.ner()); 
    }
@Test
    public void testAnnotationWithPhoneNumber() throws IOException {
        Properties properties = new Properties();
        NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

        Annotation annotation = new Annotation("Call me at +1-234-567-8900.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("+1-234-567-8900");
        token1.set(CoreAnnotations.IndexAnnotation.class, 0);
        tokens.add(token1);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        assertNull(token1.ner()); 
    }
@Test
    public void testAnnotationOnSentenceWithMultipleNEROverlaps() throws IOException {
        Properties properties = new Properties();
        NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

        Annotation annotation = new Annotation("Microsoft CEO Satya Nadella visited Berlin.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("Microsoft");
        token1.set(CoreAnnotations.IndexAnnotation.class, 0);
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("CEO");
        token2.set(CoreAnnotations.IndexAnnotation.class, 1);
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("Satya");
        token3.set(CoreAnnotations.IndexAnnotation.class, 2);
        tokens.add(token3);

        CoreLabel token4 = new CoreLabel();
        token4.setWord("Nadella");
        token4.set(CoreAnnotations.IndexAnnotation.class, 3);
        tokens.add(token4);

        CoreLabel token5 = new CoreLabel();
        token5.setWord("Berlin");
        token5.set(CoreAnnotations.IndexAnnotation.class, 4);
        tokens.add(token5);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        assertEquals("ORGANIZATION", token1.ner());
        assertNull(token2.ner()); 
        assertEquals("PERSON", token3.ner());
        assertEquals("PERSON", token4.ner());
        assertEquals("LOCATION", token5.ner());
    }
@Test
    public void testAnnotationWithAmbiguousNoun() throws IOException {
        Properties properties = new Properties();
        NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

        Annotation annotation = new Annotation("Amazon is a rainforest and a company.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("Amazon");
        token1.set(CoreAnnotations.IndexAnnotation.class, 0);
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("rainforest");
        token2.set(CoreAnnotations.IndexAnnotation.class, 1);
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("company");
        token3.set(CoreAnnotations.IndexAnnotation.class, 2);
        tokens.add(token3);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        assertEquals("ORGANIZATION", token1.ner()); 
        assertNull(token2.ner()); 
        assertNull(token3.ner());
    }
@Test
    public void testAnnotationOnTextWithExtraWhitespaces() throws IOException {
        Properties properties = new Properties();
        NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

        Annotation annotation = new Annotation("   New   York   City   ");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("New");
        token1.set(CoreAnnotations.IndexAnnotation.class, 0);
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("York");
        token2.set(CoreAnnotations.IndexAnnotation.class, 1);
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("City");
        token3.set(CoreAnnotations.IndexAnnotation.class, 2);
        tokens.add(token3);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        assertEquals("LOCATION", token1.ner());
        assertEquals("LOCATION", token2.ner());
        assertEquals("LOCATION", token3.ner());
    }
@Test
    public void testAnnotationWithSingleLetterEntity() throws IOException {
        Properties properties = new Properties();
        NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

        Annotation annotation = new Annotation("John F. Kennedy was the 35th president.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("John");
        token1.set(CoreAnnotations.IndexAnnotation.class, 0);
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("F.");
        token2.set(CoreAnnotations.IndexAnnotation.class, 1);
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("Kennedy");
        token3.set(CoreAnnotations.IndexAnnotation.class, 2);
        tokens.add(token3);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        assertEquals("PERSON", token1.ner());
        assertEquals("PERSON", token2.ner()); 
        assertEquals("PERSON", token3.ner());
    }
@Test
    public void testAnnotationWithHyphenatedNames() throws IOException {
        Properties properties = new Properties();
        NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

        Annotation annotation = new Annotation("Jean-Paul Sartre was a philosopher.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("Jean-Paul");
        token1.set(CoreAnnotations.IndexAnnotation.class, 0);
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Sartre");
        token2.set(CoreAnnotations.IndexAnnotation.class, 1);
        tokens.add(token2);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        assertEquals("PERSON", token1.ner());
        assertEquals("PERSON", token2.ner());
    }
@Test
    public void testAnnotationWithNonStandardCapitalization() throws IOException {
        Properties properties = new Properties();
        NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

        Annotation annotation = new Annotation("elizabeth II met with Prime Minister JUSTIN TRUDEAU.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("elizabeth");
        token1.set(CoreAnnotations.IndexAnnotation.class, 0);
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("II");
        token2.set(CoreAnnotations.IndexAnnotation.class, 1);
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("JUSTIN");
        token3.set(CoreAnnotations.IndexAnnotation.class, 2);
        tokens.add(token3);

        CoreLabel token4 = new CoreLabel();
        token4.setWord("TRUDEAU");
        token4.set(CoreAnnotations.IndexAnnotation.class, 3);
        tokens.add(token4);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        assertEquals("PERSON", token1.ner());
        assertEquals("ORDINAL", token2.ner());
        assertEquals("PERSON", token3.ner());
        assertEquals("PERSON", token4.ner());
    }
@Test
    public void testAnnotationWithRepeatedEntityTypes() throws IOException {
        Properties properties = new Properties();
        NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

        Annotation annotation = new Annotation("Amazon and Google are tech giants. Amazon also sells books.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("Amazon");
        token1.set(CoreAnnotations.IndexAnnotation.class, 0);
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Google");
        token2.set(CoreAnnotations.IndexAnnotation.class, 1);
        tokens.add(token2);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("Amazon");
        token3.set(CoreAnnotations.IndexAnnotation.class, 2);
        tokens.add(token3);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        assertEquals("ORGANIZATION", token1.ner());
        assertEquals("ORGANIZATION", token2.ner());
        assertEquals("ORGANIZATION", token3.ner());
    }
@Test
    public void testAnnotationWithAmbiguousAcronyms() throws IOException {
        Properties properties = new Properties();
        NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

        Annotation annotation = new Annotation("The UN and Apple are in talks.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("UN");
        token1.set(CoreAnnotations.IndexAnnotation.class, 0);
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Apple");
        token2.set(CoreAnnotations.IndexAnnotation.class, 1);
        tokens.add(token2);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        assertEquals("ORGANIZATION", token1.ner());
        assertEquals("ORGANIZATION", token2.ner());
    }
@Test
    public void testAnnotationWithDateInDifferentFormats() throws IOException {
        Properties properties = new Properties();
        NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

        Annotation annotation = new Annotation("He was born on 12/25/1990 and moved in June-1995.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("12/25/1990");
        token1.set(CoreAnnotations.IndexAnnotation.class, 0);
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("June-1995");
        token2.set(CoreAnnotations.IndexAnnotation.class, 1);
        tokens.add(token2);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        assertEquals("DATE", token1.ner());
        assertEquals("DATE", token2.ner());
    }
@Test
    public void testAnnotationWithMixedLanguages() throws IOException {
        Properties properties = new Properties();
        properties.setProperty("ner.language", "en");
        NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

        Annotation annotation = new Annotation("Bonjour! I visited Madrid last summer.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("Bonjour");
        token1.set(CoreAnnotations.IndexAnnotation.class, 0);
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Madrid");
        token2.set(CoreAnnotations.IndexAnnotation.class, 1);
        tokens.add(token2);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        assertNull(token1.ner()); 
        assertEquals("LOCATION", token2.ner());
    }
@Test
    public void testAnnotationWithNoNamedEntities() throws IOException {
        Properties properties = new Properties();
        NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

        Annotation annotation = new Annotation("The cat sleeps under the table.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("cat");
        token1.set(CoreAnnotations.IndexAnnotation.class, 0);
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("table");
        token2.set(CoreAnnotations.IndexAnnotation.class, 1);
        tokens.add(token2);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        assertNull(token1.ner());
        assertNull(token2.ner());
    }
@Test
    public void testAnnotationWithSingleNamedEntityWord() throws IOException {
        Properties properties = new Properties();
        NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

        Annotation annotation = new Annotation("Tesla.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("Tesla");
        token1.set(CoreAnnotations.IndexAnnotation.class, 0);
        tokens.add(token1);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        assertEquals("ORGANIZATION", token1.ner());
    }
@Test
    public void testAnnotationWithLongNumberString() throws IOException {
        Properties properties = new Properties();
        NERCombinerAnnotator annotator = new NERCombinerAnnotator(properties);

        Annotation annotation = new Annotation("9876543210 is a large number.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("9876543210");
        token1.set(CoreAnnotations.IndexAnnotation.class, 0);
        tokens.add(token1);

        annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotator.annotate(annotation);

        assertEquals("NUMBER", token1.ner());
    } 
}