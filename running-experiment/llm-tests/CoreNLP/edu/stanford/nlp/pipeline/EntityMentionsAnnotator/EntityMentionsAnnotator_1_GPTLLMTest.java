package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.Timex;
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class EntityMentionsAnnotator_1_GPTLLMTest {

 @Test
    public void testDefaultConstructor() {
        EntityMentionsAnnotator defaultAnnotator = new EntityMentionsAnnotator();
        assertNotNull(defaultAnnotator);
    }
@Test
    public void testCustomPropertiesConstructor() {
        Properties properties = new Properties();
        properties.setProperty("entityMentions.nerCoreAnnotation", "edu.stanford.nlp.ling.CoreAnnotations$NamedEntityTagAnnotation");
        properties.setProperty("entityMentions.acronyms", "true");

        EntityMentionsAnnotator configuredAnnotator = new EntityMentionsAnnotator("entityMentions", properties);
        assertNotNull(configuredAnnotator);
    }
@Test
    public void testAnnotateWithBasicEntities() {
        EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

        Annotation testAnnotation = new Annotation("Barack Obama was the 44th President of the United States.");
        
        CoreMap sentence = new Annotation("Barack Obama was the 44th President.");

        List<CoreLabel> tokens = new ArrayList<>();
        CoreLabel token1 = new CoreLabel();
        token1.setWord("Barack");
        token1.setNER("PERSON");
        CoreLabel token2 = new CoreLabel();
        token2.setWord("Obama");
        token2.setNER("PERSON");
        tokens.add(token1);
        tokens.add(token2);

        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        testAnnotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(testAnnotation);

        List<CoreMap> mentions = sentence.get(CoreAnnotations.MentionsAnnotation.class);
        assertNotNull(mentions);
        assertEquals(1, mentions.size());
    }
@Test
    public void testAnnotateWithNoEntities() {
        EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();

        Annotation testAnnotation = new Annotation("This is a simple sentence with no named entities.");
        CoreMap sentence = new Annotation("This is a simple sentence with no named entities.");
        sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
        testAnnotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(testAnnotation);

        List<CoreMap> mentions = sentence.get(CoreAnnotations.MentionsAnnotation.class);
        assertNull(mentions);
    }
@Test
    public void testAnnotatePronominalMentions() {
        Annotation annotation = new Annotation("She lives in New York.");
        CoreMap sentence = new Annotation("She lives in New York.");
        
        List<CoreLabel> tokens = new ArrayList<>();
        CoreLabel token1 = new CoreLabel();
        token1.setWord("She");
        tokens.add(token1);

        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
  }
@Test
    public void testDetermineEntityMentionConfidences() {
        CoreMap entityMention = new Annotation("Stanford University");

        List<CoreLabel> mentionTokens = new ArrayList<>();
        CoreLabel token1 = new CoreLabel();
        token1.setWord("Stanford");
        token1.setNER("ORGANIZATION");
        CoreLabel token2 = new CoreLabel();
        token2.setWord("University");
        token2.setNER("ORGANIZATION");

        mentionTokens.add(token1);
        mentionTokens.add(token2);

        entityMention.set(CoreAnnotations.TokensAnnotation.class, mentionTokens);

        HashMap<String, Double> confidenceScores = EntityMentionsAnnotator.determineEntityMentionConfidences(entityMention);

        assertNotNull(confidenceScores);
        assertTrue(confidenceScores.containsKey("ORGANIZATION"));
    }
@Test
    public void testTokensForCharacters() {
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("Stanford");
        token1.setBeginPosition(0);
        token1.setEndPosition(8);
        
        CoreLabel token2 = new CoreLabel();
        token2.setWord("University");
        token2.setBeginPosition(9);
        token2.setEndPosition(18);

        tokens.add(token1);
        tokens.add(token2);
    }
@Test
    public void testOverlapsWithMention() {
        CoreMap mention1 = new Annotation("Stanford University");
        
        List<CoreLabel> tokens1 = new ArrayList<>();
        CoreLabel tokenA = new CoreLabel();
        tokenA.setWord("Stanford");
        tokenA.setBeginPosition(0);
        tokenA.setEndPosition(8);
        tokens1.add(tokenA);
        mention1.set(CoreAnnotations.TokensAnnotation.class, tokens1);

        CoreMap mention2 = new Annotation("University");
        
        List<CoreLabel> tokens2 = new ArrayList<>();
        CoreLabel tokenB = new CoreLabel();
        tokenB.setWord("University");
        tokenB.setBeginPosition(9);
        tokenB.setEndPosition(18);
        tokens2.add(tokenB);
        mention2.set(CoreAnnotations.TokensAnnotation.class, tokens2);

        List<CoreMap> mentionsList = new ArrayList<>();
        mentionsList.add(mention1);
    }
@Test
    public void testRequirementsSatisfied() {
        EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
        Set<Class<? extends CoreAnnotation>> satisfiedRequirements = annotator.requirementsSatisfied();
        
        assertNotNull(satisfiedRequirements);
        assertTrue(satisfiedRequirements.contains(CoreAnnotations.MentionsAnnotation.class));
    }
@Test
    public void testEmptyAnnotation() {
        EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
        Annotation emptyAnnotation = new Annotation("");

        annotator.annotate(emptyAnnotation);

        List<CoreMap> mentions = emptyAnnotation.get(CoreAnnotations.MentionsAnnotation.class);
        assertNull(mentions);
    }
@Test
    public void testSingleCharacterEntity() {
        EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
        Annotation annotation = new Annotation("A is a letter.");
        
        CoreMap sentence = new Annotation("A is a letter.");
        List<CoreLabel> tokens = new ArrayList<>();
        
        CoreLabel token = new CoreLabel();
        token.setWord("A");
        token.setNER("ENTITY");
        tokens.add(token);
        
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);

        List<CoreMap> mentions = sentence.get(CoreAnnotations.MentionsAnnotation.class);
        assertNotNull(mentions);
        assertEquals(1, mentions.size());
    }
@Test
    public void testEntitiesWithSameBeginPosition() {
        EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
        Annotation annotation = new Annotation("United States of America is a country.");

        CoreMap sentence = new Annotation("United States of America is a country.");
        List<CoreLabel> tokens = new ArrayList<>();
        
        CoreLabel token1 = new CoreLabel();
        token1.setWord("United");
        token1.setNER("LOCATION");
        token1.setBeginPosition(0);
        token1.setEndPosition(6);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("States");
        token2.setNER("LOCATION");
        token2.setBeginPosition(0);
        token2.setEndPosition(13);

        tokens.add(token1);
        tokens.add(token2);

        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);

        List<CoreMap> mentions = sentence.get(CoreAnnotations.MentionsAnnotation.class);
        assertNotNull(mentions);
        assertEquals(1, mentions.size());
    }
@Test
    public void testOverlappingEntities() {
        EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
        Annotation annotation = new Annotation("Barack Obama, the President, won an award.");

        CoreMap sentence = new Annotation("Barack Obama, the President, won an award.");
        List<CoreLabel> tokens = new ArrayList<>();
        
        CoreLabel token1 = new CoreLabel();
        token1.setWord("Barack");
        token1.setNER("PERSON");
        token1.setBeginPosition(0);
        token1.setEndPosition(6);
        
        CoreLabel token2 = new CoreLabel();
        token2.setWord("Obama");
        token2.setNER("PERSON");
        token2.setBeginPosition(7);
        token2.setEndPosition(12);
        
        CoreLabel token3 = new CoreLabel();
        token3.setWord("President");
        token3.setNER("TITLE");
        token3.setBeginPosition(14);
        token3.setEndPosition(23);
        
        tokens.add(token1);
        tokens.add(token2);
        tokens.add(token3);

        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);
        
        List<CoreMap> mentions = sentence.get(CoreAnnotations.MentionsAnnotation.class);
        assertNotNull(mentions);
        assertEquals(2, mentions.size());
    }
@Test
    public void testSentenceWithoutNERAnnotations() {
        EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
        Annotation annotation = new Annotation("This is an unknown phrase.");

        CoreMap sentence = new Annotation("This is an unknown phrase.");
        List<CoreLabel> tokens = new ArrayList<>();
        
        CoreLabel token = new CoreLabel();
        token.setWord("undefined");
        token.setNER(null);  

        tokens.add(token);
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);

        List<CoreMap> mentions = sentence.get(CoreAnnotations.MentionsAnnotation.class);
        assertNull(mentions);
    }
@Test
    public void testPronominalMentionsWithGender() {
        Annotation annotation = new Annotation("He and she were present.");
        CoreMap sentence = new Annotation("He and she were present.");

        List<CoreLabel> tokens = new ArrayList<>();
        CoreLabel token1 = new CoreLabel();
        token1.setWord("He");
        tokens.add(token1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("she");
        tokens.add(token2);

        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

       }
@Test
    public void testDetermineEntityMentionConfidencesWithoutNER() {
        CoreMap entityMention = new Annotation("Unknown Entity");

        List<CoreLabel> mentionTokens = new ArrayList<>();
        CoreLabel token = new CoreLabel();
        token.setWord("Unknown");
        token.setNER(null);

        mentionTokens.add(token);
        entityMention.set(CoreAnnotations.TokensAnnotation.class, mentionTokens);

        HashMap<String, Double> confidenceScores = EntityMentionsAnnotator.determineEntityMentionConfidences(entityMention);

        assertNull(confidenceScores);
    }
@Test
    public void testAcronymDetection() {
        EntityMentionsAnnotator annotator = new EntityMentionsAnnotator("entityMentions", new Properties());

        Annotation annotation = new Annotation("IBM is an organization. International Business Machines is its full name.");
        CoreMap sentence = new Annotation("IBM is an organization. International Business Machines is its full name.");

        List<CoreLabel> tokens = new ArrayList<>();
        
        CoreLabel token1 = new CoreLabel();
        token1.setWord("IBM");
        token1.setNER("O");
        tokens.add(token1);
        
        CoreLabel token2 = new CoreLabel();
        token2.setWord("International");
        token2.setNER("ORGANIZATION");
        tokens.add(token2);
        
        CoreLabel token3 = new CoreLabel();
        token3.setWord("Business");
        token3.setNER("ORGANIZATION");
        tokens.add(token3);
        
        CoreLabel token4 = new CoreLabel();
        token4.setWord("Machines");
        token4.setNER("ORGANIZATION");
        tokens.add(token4);

        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);

        List<CoreMap> mentions = sentence.get(CoreAnnotations.MentionsAnnotation.class);
        assertNotNull(mentions);
        assertTrue(mentions.stream().anyMatch(m -> "ORGANIZATION".equals(m.get(CoreAnnotations.NamedEntityTagAnnotation.class))));
    }
@Test
    public void testNullAnnotation() {
        EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
        
        try {
            annotator.annotate(null);
            fail("Expected NullPointerException when annotating null");
        } catch (NullPointerException e) {
            
        }
    }
@Test
    public void testEmptySentenceList() {
        EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
        Annotation annotation = new Annotation("This document has no sentences.");
        annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

        annotator.annotate(annotation);

        List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
        assertNull(mentions);
    }
@Test
    public void testSentenceWithNoTokens() {
        EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
        Annotation annotation = new Annotation("Empty sentence.");
        
        CoreMap sentence = new Annotation("Empty sentence.");
        sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);
        
        List<CoreMap> mentions = sentence.get(CoreAnnotations.MentionsAnnotation.class);
        assertNull(mentions);
    }
@Test
    public void testEntitiesWithDifferentNERValues() {
        EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
        Annotation annotation = new Annotation("Michael Jordan and Paris are different entities.");
        
        CoreMap sentence = new Annotation("Michael Jordan and Paris are different entities.");
        
        List<CoreLabel> tokens = new ArrayList<>();
        
        CoreLabel token1 = new CoreLabel();
        token1.setWord("Michael");
        token1.setNER("PERSON");
        
        CoreLabel token2 = new CoreLabel();
        token2.setWord("Jordan");
        token2.setNER("PERSON");
        
        CoreLabel token3 = new CoreLabel();
        token3.setWord("Paris");
        token3.setNER("LOCATION");
        
        tokens.add(token1);
        tokens.add(token2);
        tokens.add(token3);

        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);

        List<CoreMap> mentions = sentence.get(CoreAnnotations.MentionsAnnotation.class);
        assertNotNull(mentions);
        assertEquals(2, mentions.size());
    }
@Test
    public void testTokenCompatibilityWithNumericEntity() {
        EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
        
        CoreLabel token1 = new CoreLabel();
        token1.setWord("100");
        token1.setNER("NUMBER");
        token1.set(CoreAnnotations.NumericCompositeValueAnnotation.class, 100);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("100");
        token2.setNER("NUMBER");
        token2.set(CoreAnnotations.NumericCompositeValueAnnotation.class, 100);

 }
@Test
    public void testTokenIncompatibilityWithNumericEntity() {
        EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
        
        CoreLabel token1 = new CoreLabel();
        token1.setWord("100");
        token1.setNER("NUMBER");
        token1.set(CoreAnnotations.NumericCompositeValueAnnotation.class, 100);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("200");
        token2.setNER("NUMBER");
        token2.set(CoreAnnotations.NumericCompositeValueAnnotation.class, 200);

    }
@Test
    public void testCompatibleTimexEntities() {
        EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
        
        CoreLabel token1 = new CoreLabel();
        token1.setWord("Monday");
        token1.setNER("DATE");
        Timex timex1 = new Timex("T1");
        token1.set(TimeAnnotations.TimexAnnotation.class, timex1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Tuesday");
        token2.setNER("DATE");
        Timex timex2 = new Timex("T1");
        token2.set(TimeAnnotations.TimexAnnotation.class, timex2);

    }
@Test
    public void testIncompatibleTimexEntities() {
        EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
        
        CoreLabel token1 = new CoreLabel();
        token1.setWord("Monday");
        token1.setNER("DATE");
        Timex timex1 = new Timex("T1");
        token1.set(TimeAnnotations.TimexAnnotation.class, timex1);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Tuesday");
        token2.setNER("DATE");
        Timex timex2 = new Timex("T2");
        token2.set(TimeAnnotations.TimexAnnotation.class, timex2);

    }
@Test
    public void testAcronymsWithoutNERAnnotations() {
        EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
        
        Annotation annotation = new Annotation("NASA is based in the US. National Aeronautics and Space Administration is the full name.");
        CoreMap sentence = new Annotation("NASA is based in the US. National Aeronautics and Space Administration is the full name.");
        
        List<CoreLabel> tokens = new ArrayList<>();
        
        CoreLabel token1 = new CoreLabel();
        token1.setWord("NASA");
        token1.setNER("O");
        
        CoreLabel token2 = new CoreLabel();
        token2.setWord("National");
        token2.setNER("O");
        
        CoreLabel token3 = new CoreLabel();
        token3.setWord("Aeronautics");
        token3.setNER("O");

        CoreLabel token4 = new CoreLabel();
        token4.setWord("Space");
        token4.setNER("O");

        CoreLabel token5 = new CoreLabel();
        token5.setWord("Administration");
        token5.setNER("O");

        tokens.add(token1);
        tokens.add(token2);
        tokens.add(token3);
        tokens.add(token4);
        tokens.add(token5);

        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);

        List<CoreMap> mentions = sentence.get(CoreAnnotations.MentionsAnnotation.class);
        assertNotNull(mentions);
        assertTrue(mentions.stream().anyMatch(m -> "O".equals(m.get(CoreAnnotations.NamedEntityTagAnnotation.class))));
    }
@Test
    public void testMultipleSentencesWithEntities() {
        EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
        Annotation annotation = new Annotation("Barack Obama was the President. Microsoft is a company.");

        CoreMap sentence1 = new Annotation("Barack Obama was the President.");
        CoreMap sentence2 = new Annotation("Microsoft is a company.");

        List<CoreLabel> tokens1 = new ArrayList<>();
        CoreLabel token1 = new CoreLabel();
        token1.setWord("Barack");
        token1.setNER("PERSON");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Obama");
        token2.setNER("PERSON");

        tokens1.add(token1);
        tokens1.add(token2);
        sentence1.set(CoreAnnotations.TokensAnnotation.class, tokens1);

        List<CoreLabel> tokens2 = new ArrayList<>();
        CoreLabel token3 = new CoreLabel();
        token3.setWord("Microsoft");
        token3.setNER("ORGANIZATION");

        tokens2.add(token3);
        sentence2.set(CoreAnnotations.TokensAnnotation.class, tokens2);

        annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence1, sentence2));

        annotator.annotate(annotation);

        List<CoreMap> mentions1 = sentence1.get(CoreAnnotations.MentionsAnnotation.class);
        List<CoreMap> mentions2 = sentence2.get(CoreAnnotations.MentionsAnnotation.class);

        assertNotNull(mentions1);
        assertNotNull(mentions2);
        assertEquals(1, mentions1.size());
        assertEquals(1, mentions2.size());
    }
@Test
    public void testMentionsWithOverlappingCharacterOffsets() {
        EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
        Annotation annotation = new Annotation("United Nations, known as UN, is an organization.");

        CoreMap sentence = new Annotation("United Nations, known as UN, is an organization.");
        
        List<CoreLabel> tokens = new ArrayList<>();
        CoreLabel token1 = new CoreLabel();
        token1.setWord("United");
        token1.setNER("ORGANIZATION");
        token1.setBeginPosition(0);
        token1.setEndPosition(6);

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Nations");
        token2.setNER("ORGANIZATION");
        token2.setBeginPosition(7);
        token2.setEndPosition(14);

        CoreLabel token3 = new CoreLabel();
        token3.setWord("UN");
        token3.setNER("ORGANIZATION");
        token3.setBeginPosition(22);
        token3.setEndPosition(24);

        tokens.add(token1);
        tokens.add(token2);
        tokens.add(token3);

        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);

        List<CoreMap> mentions = sentence.get(CoreAnnotations.MentionsAnnotation.class);
        assertNotNull(mentions);
        assertEquals(2, mentions.size());
    }
@Test
    public void testEntityMentionIndexAssignment() {
        EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
        Annotation annotation = new Annotation("Google and Apple are tech giants.");

        CoreMap sentence = new Annotation("Google and Apple are tech giants.");

        List<CoreLabel> tokens = new ArrayList<>();
        CoreLabel token1 = new CoreLabel();
        token1.setWord("Google");
        token1.setNER("ORGANIZATION");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Apple");
        token2.setNER("ORGANIZATION");

        tokens.add(token1);
        tokens.add(token2);

        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);

        List<CoreMap> mentions = sentence.get(CoreAnnotations.MentionsAnnotation.class);
        assertNotNull(mentions);
        assertEquals(2, mentions.size());

        assertEquals(Integer.valueOf(0), mentions.get(0).get(CoreAnnotations.EntityMentionIndexAnnotation.class));
        assertEquals(Integer.valueOf(1), mentions.get(1).get(CoreAnnotations.EntityMentionIndexAnnotation.class));
    }
@Test
    public void testNilNERAssignment() {
        EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
        Annotation annotation = new Annotation("Random words without named entities.");

        CoreMap sentence = new Annotation("Random words without named entities.");
        List<CoreLabel> tokens = new ArrayList<>();
        
        CoreLabel token1 = new CoreLabel();
        token1.setWord("Random");
        token1.setNER("O");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("words");
        token2.setNER("O");

        tokens.add(token1);
        tokens.add(token2);
        
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);
        
        List<CoreMap> mentions = sentence.get(CoreAnnotations.MentionsAnnotation.class);
        assertNull(mentions);
    }
@Test
    public void testPartialNEROverlap() {
        EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
        Annotation annotation = new Annotation("Stanford University in California.");

        CoreMap sentence = new Annotation("Stanford University in California.");

        List<CoreLabel> tokens = new ArrayList<>();
        CoreLabel token1 = new CoreLabel();
        token1.setWord("Stanford");
        token1.setNER("ORGANIZATION");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("University");
        token2.setNER("O");  

        CoreLabel token3 = new CoreLabel();
        token3.setWord("California");
        token3.setNER("LOCATION");

        tokens.add(token1);
        tokens.add(token2);
        tokens.add(token3);

        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);

        List<CoreMap> mentions = sentence.get(CoreAnnotations.MentionsAnnotation.class);
        assertNotNull(mentions);
        assertEquals(2, mentions.size());
    }
@Test
    public void testPronounWithoutGender() {
        Annotation annotation = new Annotation("They were at the party.");
        CoreMap sentence = new Annotation("They were at the party.");

        List<CoreLabel> tokens = new ArrayList<>();
        CoreLabel token1 = new CoreLabel();
        token1.setWord("They");
        tokens.add(token1);

        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    }
@Test
    public void testSingleTokenWithoutNER() {
        EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
        Annotation annotation = new Annotation("Hello.");
        
        CoreMap sentence = new Annotation("Hello.");
        List<CoreLabel> tokens = new ArrayList<>();
        
        CoreLabel token = new CoreLabel();
        token.setWord("Hello");
        token.setNER("O"); 

        tokens.add(token);
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);

        List<CoreMap> mentions = sentence.get(CoreAnnotations.MentionsAnnotation.class);
        assertNull(mentions);
    }
@Test
    public void testProperNounAsNonEntity() {
        EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
        Annotation annotation = new Annotation("Coffee is a drink.");

        CoreMap sentence = new Annotation("Coffee is a drink.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token = new CoreLabel();
        token.setWord("Coffee");
        token.setNER("O");  

        tokens.add(token);
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);

        List<CoreMap> mentions = sentence.get(CoreAnnotations.MentionsAnnotation.class);
        assertNull(mentions);
    }
@Test
    public void testFailedNERClassification() {
        EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
        Annotation annotation = new Annotation("Unrecognizedword should not be an entity.");
        
        CoreMap sentence = new Annotation("Unrecognizedword should not be an entity.");
        List<CoreLabel> tokens = new ArrayList<>();
        
        CoreLabel token = new CoreLabel();
        token.setWord("Unrecognizedword");
        token.setNER(null);

        tokens.add(token);
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);

        List<CoreMap> mentions = sentence.get(CoreAnnotations.MentionsAnnotation.class);
        assertNull(mentions);
    }
@Test
    public void testEntitiesWithSameNameDifferentTypes() {
        EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
        Annotation annotation = new Annotation("Amazon is a river. Amazon is also a company.");
        
        CoreMap sentence1 = new Annotation("Amazon is a river.");
        CoreMap sentence2 = new Annotation("Amazon is also a company.");

        List<CoreLabel> tokens1 = new ArrayList<>();
        CoreLabel token1 = new CoreLabel();
        token1.setWord("Amazon");
        token1.setNER("LOCATION");

        tokens1.add(token1);
        sentence1.set(CoreAnnotations.TokensAnnotation.class, tokens1);

        List<CoreLabel> tokens2 = new ArrayList<>();
        CoreLabel token2 = new CoreLabel();
        token2.setWord("Amazon");
        token2.setNER("ORGANIZATION");

        tokens2.add(token2);
        sentence2.set(CoreAnnotations.TokensAnnotation.class, tokens2);

        annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence1, sentence2));

        annotator.annotate(annotation);

        List<CoreMap> mentions1 = sentence1.get(CoreAnnotations.MentionsAnnotation.class);
        List<CoreMap> mentions2 = sentence2.get(CoreAnnotations.MentionsAnnotation.class);

        assertNotNull(mentions1);
        assertNotNull(mentions2);
        assertEquals(1, mentions1.size());
        assertEquals(1, mentions2.size());
        assertNotEquals(mentions1.get(0).get(CoreAnnotations.NamedEntityTagAnnotation.class),
                        mentions2.get(0).get(CoreAnnotations.NamedEntityTagAnnotation.class));
    }
@Test
    public void testTokensWithSameNERDifferentNormalizedForms() {
        EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
        Annotation annotation = new Annotation("The USA and United States are the same entity.");

        CoreMap sentence = new Annotation("The USA and United States are the same entity.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("USA");
        token1.setNER("LOCATION");
        token1.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "United States");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("United");
        token2.setNER("LOCATION");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("States");
        token3.setNER("LOCATION");

        tokens.add(token1);
        tokens.add(token2);
        tokens.add(token3);

        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);

        List<CoreMap> mentions = sentence.get(CoreAnnotations.MentionsAnnotation.class);
        assertNotNull(mentions);
        assertEquals(1, mentions.size());
        assertEquals("United States", mentions.get(0).get(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class));
    }
@Test
    public void testEmptyTextAnnotation() {
        EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
        Annotation annotation = new Annotation("");

        annotator.annotate(annotation);
        
        List<CoreMap> mentions = annotation.get(CoreAnnotations.MentionsAnnotation.class);
        assertNull(mentions);
    }
@Test
    public void testAcronymsWithDifferentNERClasses() {
        EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
        Annotation annotation = new Annotation("NASA is an organization. NASA pertains to space technology.");
        
        CoreMap sentence = new Annotation("NASA is an organization. NASA pertains to space technology.");
        List<CoreLabel> tokens = new ArrayList<>();
        
        CoreLabel token1 = new CoreLabel();
        token1.setWord("NASA");
        token1.setNER("ORGANIZATION");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("NASA");
        token2.setNER("TECHNOLOGY");

        tokens.add(token1);
        tokens.add(token2);

        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);
        
        List<CoreMap> mentions = sentence.get(CoreAnnotations.MentionsAnnotation.class);
        assertNotNull(mentions);
        assertEquals(2, mentions.size());
        assertNotEquals(mentions.get(0).get(CoreAnnotations.NamedEntityTagAnnotation.class), 
                        mentions.get(1).get(CoreAnnotations.NamedEntityTagAnnotation.class));
    }
@Test
    public void testTimexAnnotationOnTemporalEntities() {
        EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
        Annotation annotation = new Annotation("Next Monday is a holiday.");

        CoreMap sentence = new Annotation("Next Monday is a holiday.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("Monday");
        token1.setNER("DATE");
        Timex timex = new Timex("T1");
        token1.set(TimeAnnotations.TimexAnnotation.class, timex);

        tokens.add(token1);
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);

        List<CoreMap> mentions = sentence.get(CoreAnnotations.MentionsAnnotation.class);
        assertNotNull(mentions);
        assertEquals(1, mentions.size());
        assertEquals("T1", mentions.get(0).get(TimeAnnotations.TimexAnnotation.class).tid());
    }
@Test
    public void testDuplicateEntityMentionsInSentence() {
        EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
        Annotation annotation = new Annotation("Google is a company. Google was founded in 1998.");

        CoreMap sentence = new Annotation("Google is a company. Google was founded in 1998.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("Google");
        token1.setNER("ORGANIZATION");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Google");
        token2.setNER("ORGANIZATION");

        tokens.add(token1);
        tokens.add(token2);

        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);

        List<CoreMap> mentions = sentence.get(CoreAnnotations.MentionsAnnotation.class);
        assertNotNull(mentions);
        assertEquals(2, mentions.size());
        assertEquals("ORGANIZATION", mentions.get(0).get(CoreAnnotations.NamedEntityTagAnnotation.class));
        assertEquals("ORGANIZATION", mentions.get(1).get(CoreAnnotations.NamedEntityTagAnnotation.class));
    }
@Test
    public void testPronounWithoutNamedEntityType() {
        Annotation annotation = new Annotation("They arrived early.");
        CoreMap sentence = new Annotation("They arrived early.");

        List<CoreLabel> tokens = new ArrayList<>();
        CoreLabel token = new CoreLabel();
        token.setWord("They");
        tokens.add(token);

        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));


    }
@Test
    public void testEntityWithSpecialCharacters() {
        EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
        Annotation annotation = new Annotation("Apple Inc. is a technology giant.");

        CoreMap sentence = new Annotation("Apple Inc. is a technology giant.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("Apple");
        token1.setNER("ORGANIZATION");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Inc.");
        token2.setNER("O");

        tokens.add(token1);
        tokens.add(token2);

        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);

        List<CoreMap> mentions = sentence.get(CoreAnnotations.MentionsAnnotation.class);
        assertNotNull(mentions);
        assertEquals(1, mentions.size());
    }
@Test
    public void testTokenWithMultipleNERTags() {
        EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
        Annotation annotation = new Annotation("Spring can refer to a season or a mechanism.");

        CoreMap sentence = new Annotation("Spring can refer to a season or a mechanism.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("Spring");
        token1.setNER("SEASON");  
        tokens.add(token1);

        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);

        List<CoreMap> mentions = sentence.get(CoreAnnotations.MentionsAnnotation.class);
        assertNotNull(mentions);
        assertEquals(1, mentions.size());
        assertEquals("SEASON", mentions.get(0).get(CoreAnnotations.NamedEntityTagAnnotation.class));
    }
@Test
    public void testAcronymWithoutNERTag() {
        EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
        Annotation annotation = new Annotation("NASA launched a satellite.");

        CoreMap sentence = new Annotation("NASA launched a satellite.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token = new CoreLabel();
        token.setWord("NASA");
        token.setNER("O");  

        tokens.add(token);
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);

        List<CoreMap> mentions = sentence.get(CoreAnnotations.MentionsAnnotation.class);
        assertNull(mentions);
    }
@Test
    public void testEntityWithSingleLetterWord() {
        EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
        Annotation annotation = new Annotation("The movie E.T. was a hit.");

        CoreMap sentence = new Annotation("The movie E.T. was a hit.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token = new CoreLabel();
        token.setWord("E.T.");
        token.setNER("MOVIE");

        tokens.add(token);
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);

        List<CoreMap> mentions = sentence.get(CoreAnnotations.MentionsAnnotation.class);
        assertNotNull(mentions);
        assertEquals(1, mentions.size());
    }
@Test
    public void testLongNamedEntityWithoutSpaces() {
        EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
        Annotation annotation = new Annotation("PaloAltoNetworks is a cybersecurity company.");

        CoreMap sentence = new Annotation("PaloAltoNetworks is a cybersecurity company.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token = new CoreLabel();
        token.setWord("PaloAltoNetworks");
        token.setNER("ORGANIZATION");

        tokens.add(token);
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);

        List<CoreMap> mentions = sentence.get(CoreAnnotations.MentionsAnnotation.class);
        assertNotNull(mentions);
        assertEquals(1, mentions.size());
    }
@Test
    public void testEntityWithLeadingAndTrailingPunctuation() {
        EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
        Annotation annotation = new Annotation("\"Google\" is a major tech company.");

        CoreMap sentence = new Annotation("\"Google\" is a major tech company.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token = new CoreLabel();
        token.setWord("Google");
        token.setNER("ORGANIZATION");

        tokens.add(token);
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);

        List<CoreMap> mentions = sentence.get(CoreAnnotations.MentionsAnnotation.class);
        assertNotNull(mentions);
        assertEquals(1, mentions.size());
    }
@Test
    public void testWordWithMisclassifiedNER() {
        EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
        Annotation annotation = new Annotation("Apple is a fruit.");

        CoreMap sentence = new Annotation("Apple is a fruit.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token = new CoreLabel();
        token.setWord("Apple");
        token.setNER("ORGANIZATION");  

        tokens.add(token);
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);

        List<CoreMap> mentions = sentence.get(CoreAnnotations.MentionsAnnotation.class);
        assertNotNull(mentions);
        assertEquals(1, mentions.size());
        assertEquals("ORGANIZATION", mentions.get(0).get(CoreAnnotations.NamedEntityTagAnnotation.class));
    }
@Test
    public void testMultipleEntitiesInSameSentence() {
        EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
        Annotation annotation = new Annotation("Amazon and Google are tech giants.");

        CoreMap sentence = new Annotation("Amazon and Google are tech giants.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("Amazon");
        token1.setNER("ORGANIZATION");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Google");
        token2.setNER("ORGANIZATION");

        tokens.add(token1);
        tokens.add(token2);

        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);

        List<CoreMap> mentions = sentence.get(CoreAnnotations.MentionsAnnotation.class);
        assertNotNull(mentions);
        assertEquals(2, mentions.size());
    }
@Test
    public void testEntityWithNumericValue() {
        EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
        Annotation annotation = new Annotation("Tesla Model 3 was released in 2017.");

        CoreMap sentence = new Annotation("Tesla Model 3 was released in 2017.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("Tesla");
        token1.setNER("ORGANIZATION");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Model");
        token2.setNER("O");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("3");
        token3.setNER("NUMBER");

        CoreLabel token4 = new CoreLabel();
        token4.setWord("2017");
        token4.setNER("DATE");

        tokens.add(token1);
        tokens.add(token2);
        tokens.add(token3);
        tokens.add(token4);

        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);

        List<CoreMap> mentions = sentence.get(CoreAnnotations.MentionsAnnotation.class);
        assertNotNull(mentions);
        assertEquals(2, mentions.size());
    }
@Test
    public void testEntityWithDifferentCaseForms() {
        EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
        Annotation annotation = new Annotation("Google and google are the same.");

        CoreMap sentence = new Annotation("Google and google are the same.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("Google");
        token1.setNER("ORGANIZATION");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("google");
        token2.setNER("O");

        tokens.add(token1);
        tokens.add(token2);

        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);

        List<CoreMap> mentions = sentence.get(CoreAnnotations.MentionsAnnotation.class);
        assertNotNull(mentions);
        assertEquals(1, mentions.size());
    }
@Test
    public void testEntitySpanningMultipleWords() {
        EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
        Annotation annotation = new Annotation("United Nations is an international organization.");

        CoreMap sentence = new Annotation("United Nations is an international organization.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("United");
        token1.setNER("ORGANIZATION");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Nations");
        token2.setNER("ORGANIZATION");

        tokens.add(token1);
        tokens.add(token2);

        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);

        List<CoreMap> mentions = sentence.get(CoreAnnotations.MentionsAnnotation.class);
        assertNotNull(mentions);
        assertEquals(1, mentions.size());
    }
@Test
    public void testHyphenatedNamedEntity() {
        EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
        Annotation annotation = new Annotation("Jean-Claude Van Damme is an actor.");

        CoreMap sentence = new Annotation("Jean-Claude Van Damme is an actor.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token1 = new CoreLabel();
        token1.setWord("Jean-Claude");
        token1.setNER("PERSON");

        CoreLabel token2 = new CoreLabel();
        token2.setWord("Van");
        token2.setNER("PERSON");

        CoreLabel token3 = new CoreLabel();
        token3.setWord("Damme");
        token3.setNER("PERSON");

        tokens.add(token1);
        tokens.add(token2);
        tokens.add(token3);

        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);

        List<CoreMap> mentions = sentence.get(CoreAnnotations.MentionsAnnotation.class);
        assertNotNull(mentions);
        assertEquals(1, mentions.size());
    }
@Test
    public void testNamedEntityWithLeadingSymbol() {
        EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
        Annotation annotation = new Annotation("@Microsoft is a company.");

        CoreMap sentence = new Annotation("@Microsoft is a company.");
        List<CoreLabel> tokens = new ArrayList<>();

        CoreLabel token = new CoreLabel();
        token.setWord("@Microsoft");
        token.setNER("ORGANIZATION");

        tokens.add(token);
        sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
        annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

        annotator.annotate(annotation);

        List<CoreMap> mentions = sentence.get(CoreAnnotations.MentionsAnnotation.class);
        assertNotNull(mentions);
        assertEquals(1, mentions.size());
    }
@Test
    public void testNamedEntityAtSentenceBoundary() {
        EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
        Annotation annotation = new Annotation("Tesla is an electric vehicle manufacturer. Ford is also one.");

        CoreMap sentence1 = new Annotation("Tesla is an electric vehicle manufacturer.");
        CoreMap sentence2 = new Annotation("Ford is also one.");

        List<CoreLabel> tokens1 = new ArrayList<>();
        CoreLabel token1 = new CoreLabel();
        token1.setWord("Tesla");
        token1.setNER("ORGANIZATION");

        tokens1.add(token1);
        sentence1.set(CoreAnnotations.TokensAnnotation.class, tokens1);

        List<CoreLabel> tokens2 = new ArrayList<>();
        CoreLabel token2 = new CoreLabel();
        token2.setWord("Ford");
        token2.setNER("ORGANIZATION");

        tokens2.add(token2);
        sentence2.set(CoreAnnotations.TokensAnnotation.class, tokens2);

        annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence1, sentence2));

        annotator.annotate(annotation);

        List<CoreMap> mentions1 = sentence1.get(CoreAnnotations.MentionsAnnotation.class);
        List<CoreMap> mentions2 = sentence2.get(CoreAnnotations.MentionsAnnotation.class);

        assertNotNull(mentions1);
        assertNotNull(mentions2);
        assertEquals(1, mentions1.size());
        assertEquals(1, mentions2.size());
    }
@Test
    public void testNamedEntityWithDifferentNERClassifications() {
        EntityMentionsAnnotator annotator = new EntityMentionsAnnotator();
        Annotation annotation = new Annotation("Amazon is a company. Amazon is also a river.");

        CoreMap sentence1 = new Annotation("Amazon is a company.");
        CoreMap sentence2 = new Annotation("Amazon is also a river.");

        List<CoreLabel> tokens1 = new ArrayList<>();
        CoreLabel token1 = new CoreLabel();
        token1.setWord("Amazon");
        token1.setNER("ORGANIZATION");

        tokens1.add(token1);
        sentence1.set(CoreAnnotations.TokensAnnotation.class, tokens1);

        List<CoreLabel> tokens2 = new ArrayList<>();
        CoreLabel token2 = new CoreLabel();
        token2.setWord("Amazon");
        token2.setNER("LOCATION");

        tokens2.add(token2);
        sentence2.set(CoreAnnotations.TokensAnnotation.class, tokens2);

        annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence1, sentence2));

        annotator.annotate(annotation);

        List<CoreMap> mentions1 = sentence1.get(CoreAnnotations.MentionsAnnotation.class);
        List<CoreMap> mentions2 = sentence2.get(CoreAnnotations.MentionsAnnotation.class);

        assertNotNull(mentions1);
        assertNotNull(mentions2);
        assertEquals(1, mentions1.size());
        assertEquals(1, mentions2.size());
        assertNotEquals(mentions1.get(0).get(CoreAnnotations.NamedEntityTagAnnotation.class),
                        mentions2.get(0).get(CoreAnnotations.NamedEntityTagAnnotation.class));
    } 
}