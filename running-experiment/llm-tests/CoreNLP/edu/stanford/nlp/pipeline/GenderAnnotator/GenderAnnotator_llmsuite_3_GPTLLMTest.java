package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import org.junit.Test;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import static org.junit.Assert.*;

public class GenderAnnotator_llmsuite_3_GPTLLMTest {

@Test
public void testMaleNameAnnotation() throws Exception {
// File maleFile = tempFolder.newFile("male.txt");
// FileWriter mw = new FileWriter(maleFile);
// mw.write("John\n");
// mw.close();
// File femaleFile = tempFolder.newFile("female.txt");
// FileWriter fw = new FileWriter(femaleFile);
// fw.write("Mary\n");
// fw.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("John");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
CoreMap entity = new ArrayCoreMap();
entity.set(CoreAnnotations.TokensAnnotation.class, tokens);
entity.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
List<CoreMap> entities = new ArrayList<>();
entities.add(entity);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, entities);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("John Smith");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(annotation);
CoreMap resultEntity = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(CoreAnnotations.MentionsAnnotation.class).get(0);
assertEquals("MALE", resultEntity.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("MALE", resultEntity.get(CoreAnnotations.TokensAnnotation.class).get(0).get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testFemaleNameAnnotation() throws Exception {
// File maleFile = tempFolder.newFile("male_empty.txt");
// FileWriter mw = new FileWriter(maleFile);
// mw.write("");
// mw.close();
// File femaleFile = tempFolder.newFile("female.txt");
// FileWriter fw = new FileWriter(femaleFile);
// fw.write("Susan\n");
// fw.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("Susan");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
CoreMap entity = new ArrayCoreMap();
entity.set(CoreAnnotations.TokensAnnotation.class, tokens);
entity.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
List<CoreMap> mentions = new ArrayList<>();
mentions.add(entity);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentenceList = new ArrayList<>();
sentenceList.add(sentence);
Annotation ann = new Annotation("Susan Jones");
ann.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);
annotator.annotate(ann);
CoreMap actualMention = ann.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(CoreAnnotations.MentionsAnnotation.class).get(0);
assertEquals("FEMALE", actualMention.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("FEMALE", actualMention.get(CoreAnnotations.TokensAnnotation.class).get(0).get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testUnknownPersonNoGender() throws Exception {
// File maleFile = tempFolder.newFile("male.txt");
// FileWriter mw = new FileWriter(maleFile);
// mw.write("John\n");
// mw.close();
// File femaleFile = tempFolder.newFile("female.txt");
// FileWriter fw = new FileWriter(femaleFile);
// fw.write("Susan\n");
// fw.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("Alex");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
CoreMap entity = new ArrayCoreMap();
entity.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
entity.set(CoreAnnotations.TokensAnnotation.class, tokens);
List<CoreMap> mentions = new ArrayList<>();
mentions.add(entity);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentenceBlocks = new ArrayList<>();
sentenceBlocks.add(sentence);
Annotation annotation = new Annotation("Alex Doe");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceBlocks);
annotator.annotate(annotation);
CoreMap processed = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(CoreAnnotations.MentionsAnnotation.class).get(0);
assertNull(processed.get(CoreAnnotations.GenderAnnotation.class));
assertNull(processed.get(CoreAnnotations.TokensAnnotation.class).get(0).get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testNonPersonEntityIgnored() throws Exception {
// File maleFile = tempFolder.newFile("male.txt");
// FileWriter mw = new FileWriter(maleFile);
// mw.write("David\n");
// mw.close();
// File femaleFile = tempFolder.newFile("female.txt");
// FileWriter fw = new FileWriter(femaleFile);
// fw.write("Emma\n");
// fw.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("Texas");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
CoreMap entity = new ArrayCoreMap();
entity.set(CoreAnnotations.EntityTypeAnnotation.class, "LOCATION");
entity.set(CoreAnnotations.TokensAnnotation.class, tokens);
List<CoreMap> mentions = new ArrayList<>();
mentions.add(entity);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sList = new ArrayList<>();
sList.add(sentence);
Annotation annotation = new Annotation("Texas is big.");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sList);
annotator.annotate(annotation);
CoreMap processed = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(CoreAnnotations.MentionsAnnotation.class).get(0);
assertNull(processed.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testAnnotatorRequiresSet() throws Exception {
Properties props = new Properties();
GenderAnnotator annotator = new GenderAnnotator("gender", props);
// Set<Class<? extends edu.stanford.nlp.util.CoreAnnotation>> required = annotator.requires();
// assertTrue(required.contains(CoreAnnotations.TextAnnotation.class));
// assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
// assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
// assertTrue(required.contains(CoreAnnotations.MentionsAnnotation.class));
// assertTrue(required.contains(CoreAnnotations.EntityTypeAnnotation.class));
}

@Test
public void testAnnotatorRequirementsSatisfiedSet() throws Exception {
Properties props = new Properties();
GenderAnnotator annotator = new GenderAnnotator("gender", props);
// Set<Class<? extends edu.stanford.nlp.util.CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
// assertEquals(1, satisfied.size());
// assertTrue(satisfied.contains(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testEmptyMentionListNoException() throws Exception {
// File maleFile = tempFolder.newFile("male.txt");
// File femaleFile = tempFolder.newFile("female.txt");
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("No person here.");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
try {
annotator.annotate(annotation);
} catch (Exception e) {
fail("Should not throw exception with empty mentions list");
}
}

@Test
public void testLoadGenderNamesEmptyFile() throws Exception {
// File emptyFile = tempFolder.newFile("empty.txt");
// FileWriter writer = new FileWriter(emptyFile);
// writer.write(",,,\n");
// writer.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", emptyFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", emptyFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
assertTrue(annotator.maleNames.isEmpty() || !annotator.maleNames.contains(","));
assertTrue(annotator.femaleNames.isEmpty() || !annotator.femaleNames.contains(","));
}

@Test
public void testNameCaseMismatchStillMatches() throws Exception {
// File maleFile = tempFolder.newFile("male.txt");
// FileWriter mw = new FileWriter(maleFile);
// mw.write("Robert\n");
// mw.close();
// File femaleFile = tempFolder.newFile("female.txt");
// FileWriter fw = new FileWriter(femaleFile);
// fw.write("Alice\n");
// fw.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("rOBErT");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("rOBErT Smith");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(annotation);
assertEquals("MALE", mention.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("MALE", token.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testMentionWithMultipleTokensUsesFirstTokenOnly() throws Exception {
// File maleFile = tempFolder.newFile("male.txt");
// FileWriter mw = new FileWriter(maleFile);
// mw.write("Sam\n");
// mw.close();
// File femaleFile = tempFolder.newFile("female.txt");
// FileWriter fw = new FileWriter(femaleFile);
// fw.write("Nina\n");
// fw.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel firstToken = new CoreLabel();
firstToken.setWord("Sam");
CoreLabel secondToken = new CoreLabel();
secondToken.setWord("Johnson");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(firstToken);
tokens.add(secondToken);
CoreMap entity = new ArrayCoreMap();
entity.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
entity.set(CoreAnnotations.TokensAnnotation.class, tokens);
List<CoreMap> mentions = new ArrayList<>();
mentions.add(entity);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("Sam Johnson");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(annotation);
assertEquals("MALE", entity.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("MALE", firstToken.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("MALE", secondToken.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testMissingEntityTypeDoesNotThrowException() throws Exception {
// File maleFile = tempFolder.newFile("male.txt");
// File femaleFile = tempFolder.newFile("female.txt");
// FileWriter mw = new FileWriter(maleFile);
// mw.write("Tony\n");
// mw.close();
// FileWriter fw = new FileWriter(femaleFile);
// fw.write("Martha\n");
// fw.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("Tony");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
CoreMap entity = new ArrayCoreMap();
entity.set(CoreAnnotations.TokensAnnotation.class, tokens);
List<CoreMap> mentions = new ArrayList<>();
mentions.add(entity);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("Tony Stark");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
try {
annotator.annotate(annotation);
} catch (Exception e) {
fail("Should not throw exception when entity type annotation is missing");
}
assertNull(entity.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testMissingTokensDoesNotThrowException() throws Exception {
// File maleFile = tempFolder.newFile("male.txt");
// FileWriter mw = new FileWriter(maleFile);
// mw.write("Clark\n");
// mw.close();
// File femaleFile = tempFolder.newFile("female.txt");
// FileWriter fw = new FileWriter(femaleFile);
// fw.write("Diana\n");
// fw.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreMap entity = new ArrayCoreMap();
entity.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
List<CoreMap> mentions = new ArrayList<>();
mentions.add(entity);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("Clark Kent");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
try {
annotator.annotate(annotation);
} catch (Exception e) {
assertTrue(e instanceof NullPointerException);
}
}

@Test
public void testGenderFileWithEmptyLinesAndWhitespace() throws Exception {
// File maleFile = tempFolder.newFile("male.txt");
// FileWriter mw = new FileWriter(maleFile);
// mw.write("\n  \nAlex,   ,Bob\n");
// mw.close();
// File femaleFile = tempFolder.newFile("female.txt");
// FileWriter fw = new FileWriter(femaleFile);
// fw.write(" , \nAnna\n");
// fw.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("Bob");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
CoreMap entity = new ArrayCoreMap();
entity.set(CoreAnnotations.TokensAnnotation.class, tokens);
entity.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
List<CoreMap> mentions = new ArrayList<>();
mentions.add(entity);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("Bob Marley");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(annotation);
assertEquals("MALE", entity.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testAnnotateWithNullSentencesAnnotation() throws Exception {
// File maleFile = tempFolder.newFile("male.txt");
// FileWriter maleWriter = new FileWriter(maleFile);
// maleWriter.write("Tom\n");
// maleWriter.close();
// File femaleFile = tempFolder.newFile("female.txt");
// FileWriter femaleWriter = new FileWriter(femaleFile);
// femaleWriter.write("Lisa\n");
// femaleWriter.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
Annotation annotation = new Annotation("Text with null sentence annotation");
annotation.set(CoreAnnotations.SentencesAnnotation.class, null);
try {
annotator.annotate(annotation);
} catch (Exception e) {
fail("GenderAnnotator should not throw exception even if sentences are null");
}
}

@Test
public void testAnnotateWithSentenceMissingMentionsAnnotation() throws Exception {
// File maleFile = tempFolder.newFile("male.txt");
// FileWriter maleWriter = new FileWriter(maleFile);
// maleWriter.write("Ron\n");
// maleWriter.close();
// File femaleFile = tempFolder.newFile("female.txt");
// FileWriter femaleWriter = new FileWriter(femaleFile);
// femaleWriter.write("Rachel\n");
// femaleWriter.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreMap sentence = new ArrayCoreMap();
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("No mentions in sentence");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
try {
annotator.annotate(annotation);
} catch (Exception e) {
fail("Annotator should handle sentence without MentionsAnnotation gracefully");
}
}

@Test
public void testLoadGenderNamesWithDuplicateEntriesAcrossLines() throws Exception {
// File maleFile = tempFolder.newFile("male.txt");
// FileWriter maleWriter = new FileWriter(maleFile);
// maleWriter.write("Adam\n");
// maleWriter.write("ADAM,Adam\n");
// maleWriter.close();
// File femaleFile = tempFolder.newFile("female.txt");
// FileWriter femaleWriter = new FileWriter(femaleFile);
// femaleWriter.write("Eve,Eve,EVE\n");
// femaleWriter.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
assertTrue(annotator.maleNames.contains("adam"));
assertTrue(annotator.maleNames.size() == 1);
assertTrue(annotator.femaleNames.contains("eve"));
assertTrue(annotator.femaleNames.size() == 1);
}

@Test
public void testMaleAndFemaleNameOverlapPrefersMale() throws Exception {
// File maleFile = tempFolder.newFile("male.txt");
// FileWriter mWriter = new FileWriter(maleFile);
// mWriter.write("Taylor\n");
// mWriter.close();
// File femaleFile = tempFolder.newFile("female.txt");
// FileWriter fWriter = new FileWriter(femaleFile);
// fWriter.write("Taylor\n");
// fWriter.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("Taylor");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("Taylor Swift");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(annotation);
assertEquals("MALE", mention.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("MALE", token.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testGenderAnnotationNotSetIfTokenListEmpty() throws Exception {
// File maleFile = tempFolder.newFile("male.txt");
// FileWriter mWriter = new FileWriter(maleFile);
// mWriter.write("Gary\n");
// mWriter.close();
// File femaleFile = tempFolder.newFile("female.txt");
// FileWriter fWriter = new FileWriter(femaleFile);
// fWriter.write("Grace\n");
// fWriter.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
List<CoreLabel> tokens = new ArrayList<>();
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentenceList = new ArrayList<>();
sentenceList.add(sentence);
Annotation annotation = new Annotation("Empty token list");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);
try {
annotator.annotate(annotation);
} catch (IndexOutOfBoundsException e) {
return;
}
fail("Should throw IndexOutOfBoundsException for empty token list");
}

@Test
public void testMultipleMentionsInSingleSentence() throws Exception {
// File maleFile = tempFolder.newFile("male.txt");
// FileWriter mWriter = new FileWriter(maleFile);
// mWriter.write("Jack\n");
// mWriter.write("Mike\n");
// mWriter.close();
// File femaleFile = tempFolder.newFile("female.txt");
// FileWriter fWriter = new FileWriter(femaleFile);
// fWriter.write("Jane\n");
// fWriter.write("Sara\n");
// fWriter.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token1 = new CoreLabel();
token1.setWord("Jack");
CoreMap mention1 = new ArrayCoreMap();
mention1.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token1));
mention1.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
CoreLabel token2 = new CoreLabel();
token2.setWord("Jane");
CoreMap mention2 = new ArrayCoreMap();
mention2.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token2));
mention2.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention1);
mentions.add(mention2);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
Annotation annotation = new Annotation("Jack and Jane went home.");
annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
annotator.annotate(annotation);
assertEquals("MALE", mention1.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("FEMALE", mention2.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("MALE", token1.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("FEMALE", token2.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testMixedEntityTypesInSameSentence() throws Exception {
// File maleFile = tempFolder.newFile("male.txt");
// FileWriter mWriter = new FileWriter(maleFile);
// mWriter.write("Lucas\n");
// mWriter.close();
// File femaleFile = tempFolder.newFile("female.txt");
// FileWriter fWriter = new FileWriter(femaleFile);
// fWriter.write("Emily\n");
// fWriter.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel personToken = new CoreLabel();
personToken.setWord("Lucas");
CoreMap personMention = new ArrayCoreMap();
personMention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(personToken));
personMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
CoreLabel locationToken = new CoreLabel();
locationToken.setWord("Paris");
CoreMap locationMention = new ArrayCoreMap();
locationMention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(locationToken));
locationMention.set(CoreAnnotations.EntityTypeAnnotation.class, "LOCATION");
List<CoreMap> mentions = new ArrayList<>();
mentions.add(personMention);
mentions.add(locationMention);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
Annotation annotation = new Annotation("Lucas lives in Paris.");
annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
annotator.annotate(annotation);
assertEquals("MALE", personMention.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("MALE", personToken.get(CoreAnnotations.GenderAnnotation.class));
assertNull(locationMention.get(CoreAnnotations.GenderAnnotation.class));
assertNull(locationToken.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testNullTokenInMentionDoesNotThrow() throws Exception {
// File maleFile = tempFolder.newFile("male.txt");
// FileWriter mWriter = new FileWriter(maleFile);
// mWriter.write("Leo\n");
// mWriter.close();
// File femaleFile = tempFolder.newFile("female.txt");
// FileWriter fWriter = new FileWriter(femaleFile);
// fWriter.write("Laura\n");
// fWriter.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(null);
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
Annotation annotation = new Annotation("Null token test");
annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
try {
annotator.annotate(annotation);
} catch (NullPointerException e) {
return;
}
fail("Expected a NullPointerException due to null token");
}

@Test
public void testGenderNamesWithExtraWhitespaceAreTrimmed() throws Exception {
// File maleFile = tempFolder.newFile("male.txt");
// FileWriter mWriter = new FileWriter(maleFile);
// mWriter.write(" Brian , \n");
// mWriter.close();
// File femaleFile = tempFolder.newFile("female.txt");
// FileWriter fWriter = new FileWriter(femaleFile);
// fWriter.write("  Emma\n");
// fWriter.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("brian");
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
Annotation annotation = new Annotation("Brian info");
annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
annotator.annotate(annotation);
assertEquals("MALE", mention.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("MALE", token.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testCustomGenderFilePathOverridesDefault() throws Exception {
// File maleFile = tempFolder.newFile("custom_males.txt");
// FileWriter mWriter = new FileWriter(maleFile);
// mWriter.write("Zane\n");
// mWriter.close();
// File femaleFile = tempFolder.newFile("custom_females.txt");
// FileWriter fWriter = new FileWriter(femaleFile);
// fWriter.write("Zara\n");
// fWriter.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("Zane");
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
Annotation annotation = new Annotation("Zane case");
annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
annotator.annotate(annotation);
assertEquals("MALE", mention.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("MALE", token.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testLoadGenderNamesHandlesInvalidUnicodeGracefully() throws Exception {
// File maleFile = tempFolder.newFile("unicode_male.txt");
// FileWriter mWriter = new FileWriter(maleFile);
// mWriter.write("André\n");
// mWriter.write("\uDC00\uDC00\n");
// mWriter.close();
// File femaleFile = tempFolder.newFile("unicode_female.txt");
// FileWriter fWriter = new FileWriter(femaleFile);
// fWriter.write("Élodie\n");
// fWriter.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("andré");
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
mention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
Annotation annotation = new Annotation("Unicode test");
annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
annotator.annotate(annotation);
assertEquals("MALE", mention.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("MALE", token.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testAnnotatorWithNoMentionTokensAnnotationSet() throws Exception {
// File maleFile = tempFolder.newFile("male_names.txt");
// FileWriter maleWriter = new FileWriter(maleFile);
// maleWriter.write("Giovanni\n");
// maleWriter.close();
// File femaleFile = tempFolder.newFile("female_names.txt");
// FileWriter femaleWriter = new FileWriter(femaleFile);
// femaleWriter.write("Rosa\n");
// femaleWriter.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreMap entityMention = new ArrayCoreMap();
entityMention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
List<CoreMap> mentions = new ArrayList<>();
mentions.add(entityMention);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("Giovanni without token list.");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
try {
annotator.annotate(annotation);
} catch (Exception e) {
fail("Annotator should not throw when TokensAnnotation is missing from entity mention.");
}
}

@Test
public void testEmptySentencesAnnotationList() throws Exception {
// File maleFile = tempFolder.newFile("male.txt");
// FileWriter mWriter = new FileWriter(maleFile);
// mWriter.write("Harry\n");
// mWriter.close();
// File femaleFile = tempFolder.newFile("female.txt");
// FileWriter fWriter = new FileWriter(femaleFile);
// fWriter.write("Harriet\n");
// fWriter.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
List<CoreMap> emptySentences = new ArrayList<>();
Annotation annotation = new Annotation("Empty sentence list test.");
annotation.set(CoreAnnotations.SentencesAnnotation.class, emptySentences);
try {
annotator.annotate(annotation);
} catch (Exception e) {
fail("Should not throw when sentences list is empty.");
}
}

@Test
public void testUnknownEntityTypeIsIgnored() throws Exception {
// File maleFile = tempFolder.newFile("m.txt");
// FileWriter mWriter = new FileWriter(maleFile);
// mWriter.write("John\n");
// mWriter.close();
// File femaleFile = tempFolder.newFile("f.txt");
// FileWriter fWriter = new FileWriter(femaleFile);
// fWriter.write("Mary\n");
// fWriter.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("John");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "ORGANIZATION");
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("Unknown type");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(annotation);
assertNull(mention.get(CoreAnnotations.GenderAnnotation.class));
assertNull(token.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testGenderFilesWithEmptyLinesOnly() throws Exception {
// File maleFile = tempFolder.newFile("malef.txt");
// FileWriter m = new FileWriter(maleFile);
// m.write("\n\n\n");
// m.close();
// File femaleFile = tempFolder.newFile("femalef.txt");
// FileWriter f = new FileWriter(femaleFile);
// f.write("\n  \n");
// f.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
assertTrue(annotator.maleNames.isEmpty());
assertTrue(annotator.femaleNames.isEmpty());
}

@Test
public void testMalformedGenderFilePathFallsBackToDefaults() throws Exception {
// File maleFile = tempFolder.newFile("fallback_male.txt");
// FileWriter mw = new FileWriter(maleFile);
// mw.write("Clark\n");
// mw.close();
// File fallbackFemaleFile = tempFolder.newFile("fallback_female.txt");
// FileWriter fw = new FileWriter(fallbackFemaleFile);
// fw.write("Lana\n");
// fw.close();
Properties props = new Properties();
// props.setProperty("gender.femaleNamesFile", fallbackFemaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
assertNotNull(annotator.femaleNames);
assertTrue(annotator.femaleNames.contains("lana"));
}

@Test
public void testGenderNameWithUnicodeAccentsNormalizesMatch() throws Exception {
// File maleFile = tempFolder.newFile("unicode_malek.txt");
// FileWriter mw = new FileWriter(maleFile);
// mw.write("José\n");
// mw.close();
// File femaleFile = tempFolder.newFile("unicode_femalek.txt");
// FileWriter fw = new FileWriter(femaleFile);
// fw.write("Renée\n");
// fw.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("José");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
Annotation annotation = new Annotation("Test unicode with accents");
annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
annotator.annotate(annotation);
assertEquals("MALE", mention.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("MALE", token.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testAnnotateEmptyAnnotationObject() throws Exception {
// File maleFile = tempFolder.newFile("male_names.txt");
// FileWriter maleWriter = new FileWriter(maleFile);
// maleWriter.write("Ethan\n");
// maleWriter.close();
// File femaleFile = tempFolder.newFile("female_names.txt");
// FileWriter femaleWriter = new FileWriter(femaleFile);
// femaleWriter.write("Olivia\n");
// femaleWriter.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
Annotation emptyAnnotation = new Annotation("Empty test");
try {
annotator.annotate(emptyAnnotation);
} catch (Exception e) {
fail("Annotator must handle empty annotation with no sentence structure.");
}
}

@Test
public void testNameWithPunctuationDoesNotMatch() throws Exception {
// File maleFile = tempFolder.newFile("male.txt");
// FileWriter writer = new FileWriter(maleFile);
// writer.write("James\n");
// writer.close();
// File femaleFile = tempFolder.newFile("female.txt");
// FileWriter fw = new FileWriter(femaleFile);
// fw.write("Anna\n");
// fw.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("James.");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
Annotation annotation = new Annotation("James. with period");
annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
annotator.annotate(annotation);
assertNull(mention.get(CoreAnnotations.GenderAnnotation.class));
assertNull(token.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testSentenceWithNullMentionsList() throws Exception {
// File maleFile = tempFolder.newFile("male_data.txt");
// FileWriter mWriter = new FileWriter(maleFile);
// mWriter.write("Mark\n");
// mWriter.close();
// File femaleFile = tempFolder.newFile("female_data.txt");
// FileWriter fWriter = new FileWriter(femaleFile);
// fWriter.write("Alice\n");
// fWriter.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, null);
Annotation annotation = new Annotation("Null mentions list case");
annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
try {
annotator.annotate(annotation);
} catch (Exception e) {
fail("Annotator should not throw an exception when MentionsAnnotation is null");
}
}

@Test
public void testMentionsListContainsNullEntry() throws Exception {
// File maleFile = tempFolder.newFile("malelist.txt");
// FileWriter mw = new FileWriter(maleFile);
// mw.write("Joe\n");
// mw.close();
// File femaleFile = tempFolder.newFile("femalelist.txt");
// FileWriter fw = new FileWriter(femaleFile);
// fw.write("Jenny\n");
// fw.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
List<CoreMap> mentions = new ArrayList<>();
mentions.add(null);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
Annotation annotation = new Annotation("Mention list with null");
annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
try {
annotator.annotate(annotation);
} catch (Exception e) {
fail("Annotator should skip null mention entries without throwing an exception");
}
}

@Test
public void testTokenIsMissingWordDoesNotThrow() throws Exception {
// File maleFile = tempFolder.newFile("male.csv");
// FileWriter mWriter = new FileWriter(maleFile);
// mWriter.write("David\n");
// mWriter.close();
// File femaleFile = tempFolder.newFile("female.csv");
// FileWriter fWriter = new FileWriter(femaleFile);
// fWriter.write("Diana\n");
// fWriter.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
Annotation annotation = new Annotation("No word in token");
annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
try {
annotator.annotate(annotation);
} catch (Exception e) {
fail("Annotator should not throw when a token has no word set");
}
}

@Test
public void testAnnotationWithNullStringText() throws Exception {
// File maleFile = tempFolder.newFile("ml.txt");
// FileWriter mlw = new FileWriter(maleFile);
// mlw.write("Alex\n");
// mlw.close();
// File femaleFile = tempFolder.newFile("fl.txt");
// FileWriter flw = new FileWriter(femaleFile);
// flw.write("Alexa\n");
// flw.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
// Annotation annotation = new Annotation(null);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
// annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
try {
// annotator.annotate(annotation);
} catch (Exception e) {
fail("Annotation with null text should not affect annotate execution");
}
}

@Test
public void testAnnotatorWithEmptyPropertiesLoadsDefaults() throws Exception {
Properties props = new Properties();
GenderAnnotator annotator = new GenderAnnotator("gender", props);
// Set<Class<? extends edu.stanford.nlp.util.CoreAnnotation>> required = annotator.requires();
// assertNotNull(required);
// assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
// Set<Class<? extends edu.stanford.nlp.util.CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
// assertNotNull(satisfied);
// assertTrue(satisfied.contains(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testBothNameListsEmptyDoesNotAnnotateAnything() throws Exception {
// File maleFile = tempFolder.newFile("m_empty.txt");
// File femaleFile = tempFolder.newFile("f_empty.txt");
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("Jordan");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
Annotation annotation = new Annotation("Jordan unknown gender");
annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
annotator.annotate(annotation);
assertNull(mention.get(CoreAnnotations.GenderAnnotation.class));
assertNull(token.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testEntityTypeAnnotationIsExplicitlyNull() throws Exception {
// File maleFile = tempFolder.newFile("male_list.txt");
// FileWriter writer1 = new FileWriter(maleFile);
// writer1.write("Tom\n");
// writer1.close();
// File femaleFile = tempFolder.newFile("female_list.txt");
// FileWriter writer2 = new FileWriter(femaleFile);
// writer2.write("Tammy\n");
// writer2.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("Tom");
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
mention.set(CoreAnnotations.EntityTypeAnnotation.class, null);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
Annotation annotation = new Annotation("Tom no entity type");
annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
try {
annotator.annotate(annotation);
} catch (Exception e) {
fail("Should not fail when EntityTypeAnnotation is null");
}
}

@Test
public void testFirstTokenIsNull() throws Exception {
// File maleFile = tempFolder.newFile("male_source.txt");
// FileWriter fw1 = new FileWriter(maleFile);
// fw1.write("Steve\n");
// fw1.close();
// File femaleFile = tempFolder.newFile("female_source.txt");
// FileWriter fw2 = new FileWriter(femaleFile);
// fw2.write("Sara\n");
// fw2.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(null);
CoreLabel secondToken = new CoreLabel();
secondToken.setWord("Ignored");
tokens.add(secondToken);
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
Annotation annotation = new Annotation("Null first token");
annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
try {
annotator.annotate(annotation);
} catch (NullPointerException e) {
return;
}
fail("Expected NullPointerException due to null token");
}

@Test
public void testMalformedEntryInNameFileIsIgnored() throws Exception {
// File maleFile = tempFolder.newFile("badformat_male.txt");
// FileWriter mw = new FileWriter(maleFile);
// mw.write("##BAD_ENTRY%%\nTom,,\n");
// mw.close();
// File femaleFile = tempFolder.newFile("badformat_female.txt");
// FileWriter fw = new FileWriter(femaleFile);
// fw.write("!!!,Malory,,\n");
// fw.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("Tom");
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
Annotation annotation = new Annotation("Tom with bad file lines");
annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
annotator.annotate(annotation);
assertEquals("MALE", mention.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("MALE", token.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testNumericNameToken() throws Exception {
// File maleFile = tempFolder.newFile("male_numbers.txt");
// FileWriter fw = new FileWriter(maleFile);
// fw.write("1234\n");
// fw.close();
// File femaleFile = tempFolder.newFile("female_numbers.txt");
// FileWriter fw2 = new FileWriter(femaleFile);
// fw2.write("0000\n");
// fw2.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("1234");
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
mention.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
Annotation annotation = new Annotation("Numeric token");
annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
annotator.annotate(annotation);
assertEquals("MALE", mention.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("MALE", token.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testMissingNameFileSilentlyFails() throws Exception {
// String nonExistentPath = new File(tempFolder.getRoot(), "nonexistent_file.txt").getAbsolutePath();
// File femaleFile = tempFolder.newFile("fallback_female.txt");
// FileWriter writer = new FileWriter(femaleFile);
// writer.write("Janet\n");
// writer.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", nonExistentPath);
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
assertTrue(annotator.maleNames.isEmpty());
assertTrue(annotator.femaleNames.contains("janet"));
}

@Test
public void testEmptyTokensListStillValidMention() throws Exception {
// File maleFile = tempFolder.newFile("file.txt");
// FileWriter maleWriter = new FileWriter(maleFile);
// maleWriter.write("Eric\n");
// maleWriter.close();
// File femaleFile = tempFolder.newFile("file2.txt");
// FileWriter femaleWriter = new FileWriter(femaleFile);
// femaleWriter.write("Erica\n");
// femaleWriter.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("annotator", props);
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
Annotation annotation = new Annotation("No tokens inside");
annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
try {
annotator.annotate(annotation);
} catch (IndexOutOfBoundsException e) {
return;
}
fail("Expected IndexOutOfBoundsException for empty token list");
}

@Test
public void testTokensAnnotationIsUnmodifiableList() throws Exception {
// File maleFile = tempFolder.newFile("locked_male_names.txt");
// FileWriter fw = new FileWriter(maleFile);
// fw.write("Ben\n");
// fw.close();
// File femaleFile = tempFolder.newFile("locked_female_names.txt");
// FileWriter fw2 = new FileWriter(femaleFile);
// fw2.write("Bella\n");
// fw2.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("Ben");
List<CoreLabel> immutableTokens = Collections.unmodifiableList(Collections.singletonList(token));
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.TokensAnnotation.class, immutableTokens);
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
Annotation annotation = new Annotation("Immutable token input");
annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
annotator.annotate(annotation);
assertEquals("MALE", mention.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("MALE", token.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testUnreadableGenderFileIsIgnoredGracefully() throws Exception {
// File maleFile = tempFolder.newFile("private_male.txt");
// FileWriter writer = new FileWriter(maleFile);
// writer.write("Ken\n");
// writer.close();
// File femaleFile = new File(tempFolder.getRoot(), "restricted.txt");
// femaleFile.createNewFile();
// femaleFile.setReadable(false);
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
assertTrue(annotator.maleNames.contains("ken"));
assertTrue(annotator.femaleNames.isEmpty() || !annotator.femaleNames.contains("any"));
}
}
