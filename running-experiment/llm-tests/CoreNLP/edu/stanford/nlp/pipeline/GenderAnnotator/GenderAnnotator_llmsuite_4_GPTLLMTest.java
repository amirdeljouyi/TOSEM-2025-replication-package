package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import org.junit.Test;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import static org.junit.Assert.*;

public class GenderAnnotator_llmsuite_4_GPTLLMTest {

@Test
public void testAnnotateWithMaleName() throws Exception {
// File maleFile = folder.newFile("male_names.txt");
// File femaleFile = folder.newFile("female_names.txt");
// OutputStreamWriter maleOut = new OutputStreamWriter(new FileOutputStream(maleFile), StandardCharsets.UTF_8);
// maleOut.write("John,Mark");
// maleOut.close();
// OutputStreamWriter femaleOut = new OutputStreamWriter(new FileOutputStream(femaleFile), StandardCharsets.UTF_8);
// femaleOut.write("Mary,Anna");
// femaleOut.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("John");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
CoreMap mention = new Annotation("John");
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new Annotation("John");
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("John");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(annotation);
CoreMap processedMention = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(CoreAnnotations.MentionsAnnotation.class).get(0);
assertEquals("MALE", processedMention.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("MALE", processedMention.get(CoreAnnotations.TokensAnnotation.class).get(0).get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testAnnotateWithFemaleName() throws Exception {
// File maleFile = folder.newFile("male_names.txt");
// File femaleFile = folder.newFile("female_names.txt");
// OutputStreamWriter maleOut = new OutputStreamWriter(new FileOutputStream(maleFile), StandardCharsets.UTF_8);
// maleOut.write("John,Mark");
// maleOut.close();
// OutputStreamWriter femaleOut = new OutputStreamWriter(new FileOutputStream(femaleFile), StandardCharsets.UTF_8);
// femaleOut.write("Mary,Anna");
// femaleOut.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("Mary");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
CoreMap mention = new Annotation("Mary");
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new Annotation("Mary");
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("Mary");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(annotation);
CoreMap processedMention = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(CoreAnnotations.MentionsAnnotation.class).get(0);
assertEquals("FEMALE", processedMention.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("FEMALE", processedMention.get(CoreAnnotations.TokensAnnotation.class).get(0).get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testAnnotateWithUnknownName() throws Exception {
// File maleFile = folder.newFile("male_names.txt");
// File femaleFile = folder.newFile("female_names.txt");
// OutputStreamWriter maleOut = new OutputStreamWriter(new FileOutputStream(maleFile), StandardCharsets.UTF_8);
// maleOut.write("John");
// maleOut.close();
// OutputStreamWriter femaleOut = new OutputStreamWriter(new FileOutputStream(femaleFile), StandardCharsets.UTF_8);
// femaleOut.write("Mary");
// femaleOut.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("Unknown");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
CoreMap mention = new Annotation("Unknown");
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new Annotation("Unknown");
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("Unknown");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(annotation);
CoreMap processedMention = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(CoreAnnotations.MentionsAnnotation.class).get(0);
assertNull(processedMention.get(CoreAnnotations.GenderAnnotation.class));
assertNull(processedMention.get(CoreAnnotations.TokensAnnotation.class).get(0).get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testIgnoreNonPersonEntity() throws Exception {
// File maleFile = folder.newFile("male_names.txt");
// File femaleFile = folder.newFile("female_names.txt");
// OutputStreamWriter maleOut = new OutputStreamWriter(new FileOutputStream(maleFile), StandardCharsets.UTF_8);
// maleOut.write("John");
// maleOut.close();
// OutputStreamWriter femaleOut = new OutputStreamWriter(new FileOutputStream(femaleFile), StandardCharsets.UTF_8);
// femaleOut.write("Mary");
// femaleOut.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("John");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
CoreMap mention = new Annotation("John");
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "ORGANIZATION");
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new Annotation("John");
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("John");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(annotation);
CoreMap processedMention = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(CoreAnnotations.MentionsAnnotation.class).get(0);
assertNull(processedMention.get(CoreAnnotations.GenderAnnotation.class));
assertNull(processedMention.get(CoreAnnotations.TokensAnnotation.class).get(0).get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testRequiresReturnedSet() throws Exception {
// File maleFile = folder.newFile("male.txt");
// File femaleFile = folder.newFile("female.txt");
// OutputStreamWriter maleOut = new OutputStreamWriter(new FileOutputStream(maleFile), StandardCharsets.UTF_8);
// maleOut.write("");
// maleOut.close();
// OutputStreamWriter femaleOut = new OutputStreamWriter(new FileOutputStream(femaleFile), StandardCharsets.UTF_8);
// femaleOut.write("");
// femaleOut.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
Set<Class<? extends CoreAnnotation>> required = annotator.requires();
assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
assertTrue(required.contains(CoreAnnotations.MentionsAnnotation.class));
assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
assertTrue(required.contains(CoreAnnotations.TextAnnotation.class));
assertTrue(required.contains(CoreAnnotations.EntityTypeAnnotation.class));
}

@Test
public void testRequirementsSatisfiedReturnedSet() throws Exception {
// File maleFile = folder.newFile("male.txt");
// File femaleFile = folder.newFile("female.txt");
// OutputStreamWriter maleOut = new OutputStreamWriter(new FileOutputStream(maleFile), StandardCharsets.UTF_8);
// maleOut.write("");
// maleOut.close();
// OutputStreamWriter femaleOut = new OutputStreamWriter(new FileOutputStream(femaleFile), StandardCharsets.UTF_8);
// femaleOut.write("");
// femaleOut.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
assertEquals(1, satisfied.size());
assertTrue(satisfied.contains(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testEmptySentencesAnnotationHandled() throws Exception {
// File maleFile = folder.newFile("male.txt");
// File femaleFile = folder.newFile("female.txt");
// OutputStreamWriter maleOut = new OutputStreamWriter(new FileOutputStream(maleFile), StandardCharsets.UTF_8);
// maleOut.write("");
// maleOut.close();
// OutputStreamWriter femaleOut = new OutputStreamWriter(new FileOutputStream(femaleFile), StandardCharsets.UTF_8);
// femaleOut.write("");
// femaleOut.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
Annotation annotation = new Annotation("Test");
List<CoreMap> emptySentences = new ArrayList<>();
annotation.set(CoreAnnotations.SentencesAnnotation.class, emptySentences);
try {
annotator.annotate(annotation);
} catch (Exception e) {
fail("Annotator should not throw an exception on empty sentence list");
}
}

@Test
public void testMultipleMentionsInSingleSentence() throws Exception {
// File maleFile = folder.newFile("male.txt");
// File femaleFile = folder.newFile("female.txt");
// OutputStreamWriter maleOut = new OutputStreamWriter(new FileOutputStream(maleFile), StandardCharsets.UTF_8);
// maleOut.write("John");
// maleOut.close();
// OutputStreamWriter femaleOut = new OutputStreamWriter(new FileOutputStream(femaleFile), StandardCharsets.UTF_8);
// femaleOut.write("Mary");
// femaleOut.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token1 = new CoreLabel();
token1.setWord("John");
CoreMap mention1 = new Annotation("John");
mention1.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1));
mention1.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
CoreLabel token2 = new CoreLabel();
token2.setWord("Mary");
CoreMap mention2 = new Annotation("Mary");
mention2.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token2));
mention2.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
CoreMap sentence = new Annotation("John Mary");
sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention1, mention2));
Annotation annotation = new Annotation("John Mary");
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
annotator.annotate(annotation);
CoreMap resultMention1 = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(CoreAnnotations.MentionsAnnotation.class).get(0);
CoreMap resultMention2 = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(CoreAnnotations.MentionsAnnotation.class).get(1);
assertEquals("MALE", resultMention1.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("FEMALE", resultMention2.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testMentionWithMultipleTokensOnlyFirstUsed() throws Exception {
// File maleFile = folder.newFile("male.txt");
// File femaleFile = folder.newFile("female.txt");
// OutputStreamWriter maleOut = new OutputStreamWriter(new FileOutputStream(maleFile), StandardCharsets.UTF_8);
// maleOut.write("John");
// maleOut.close();
// OutputStreamWriter femaleOut = new OutputStreamWriter(new FileOutputStream(femaleFile), StandardCharsets.UTF_8);
// femaleOut.write("Mary");
// femaleOut.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token1 = new CoreLabel();
token1.setWord("John");
CoreLabel token2 = new CoreLabel();
token2.setWord("Doe");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token1);
tokens.add(token2);
CoreMap mention = new Annotation("John Doe");
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
CoreMap sentence = new Annotation("John Doe");
sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention));
Annotation annotation = new Annotation("John Doe");
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
annotator.annotate(annotation);
CoreMap resultMention = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(CoreAnnotations.MentionsAnnotation.class).get(0);
assertEquals("MALE", resultMention.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("MALE", resultMention.get(CoreAnnotations.TokensAnnotation.class).get(0).get(CoreAnnotations.GenderAnnotation.class));
assertEquals("MALE", resultMention.get(CoreAnnotations.TokensAnnotation.class).get(1).get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testCaseInsensitiveNameMatching() throws Exception {
// File maleFile = folder.newFile("male.txt");
// File femaleFile = folder.newFile("female.txt");
// OutputStreamWriter maleOut = new OutputStreamWriter(new FileOutputStream(maleFile), StandardCharsets.UTF_8);
// maleOut.write("john");
// maleOut.close();
// OutputStreamWriter femaleOut = new OutputStreamWriter(new FileOutputStream(femaleFile), StandardCharsets.UTF_8);
// femaleOut.write("mary");
// femaleOut.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("John");
CoreMap mention = new Annotation("John");
mention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
CoreMap sentence = new Annotation("John");
sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention));
Annotation annotation = new Annotation("John");
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
annotator.annotate(annotation);
CoreMap resultMention = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(CoreAnnotations.MentionsAnnotation.class).get(0);
assertEquals("MALE", resultMention.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("MALE", resultMention.get(CoreAnnotations.TokensAnnotation.class).get(0).get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testMissingEntityTypeAnnotation() throws Exception {
// File maleFile = folder.newFile("male.txt");
// File femaleFile = folder.newFile("female.txt");
// OutputStreamWriter maleOut = new OutputStreamWriter(new FileOutputStream(maleFile), StandardCharsets.UTF_8);
// maleOut.write("John");
// maleOut.close();
// OutputStreamWriter femaleOut = new OutputStreamWriter(new FileOutputStream(femaleFile), StandardCharsets.UTF_8);
// femaleOut.write("Mary");
// femaleOut.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("John");
CoreMap mention = new Annotation("John");
mention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
CoreMap sentence = new Annotation("John");
sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention));
Annotation annotation = new Annotation("John");
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
try {
annotator.annotate(annotation);
CoreMap resultMention = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(CoreAnnotations.MentionsAnnotation.class).get(0);
assertNull(resultMention.get(CoreAnnotations.GenderAnnotation.class));
} catch (Exception e) {
fail("Annotator should handle missing EntityTypeAnnotation gracefully.");
}
}

@Test
public void testMentionWithEmptyTokenList() throws Exception {
// File maleFile = folder.newFile("male.txt");
// File femaleFile = folder.newFile("female.txt");
// OutputStreamWriter maleOut = new OutputStreamWriter(new FileOutputStream(maleFile), StandardCharsets.UTF_8);
// maleOut.write("John");
// maleOut.close();
// OutputStreamWriter femaleOut = new OutputStreamWriter(new FileOutputStream(femaleFile), StandardCharsets.UTF_8);
// femaleOut.write("Mary");
// femaleOut.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
List<CoreLabel> tokens = new ArrayList<>();
CoreMap mention = new Annotation("John");
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
CoreMap sentence = new Annotation("John");
sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention));
Annotation annotation = new Annotation("John");
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
try {
annotator.annotate(annotation);
} catch (IndexOutOfBoundsException e) {
assertTrue(e instanceof IndexOutOfBoundsException);
}
}

@Test
public void testLoadGenderNamesHandlesEmptyLinesAndCommas() throws Exception {
// File maleFile = folder.newFile("male.txt");
// OutputStreamWriter maleOut = new OutputStreamWriter(new FileOutputStream(maleFile), StandardCharsets.UTF_8);
// maleOut.write("\n,John,,Mark,\nDoe,\n");
// maleOut.close();
// File femaleFile = folder.newFile("female.txt");
// OutputStreamWriter femaleOut = new OutputStreamWriter(new FileOutputStream(femaleFile), StandardCharsets.UTF_8);
// femaleOut.write("Mary\nAnna,,\n");
// femaleOut.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("Doe");
CoreMap mention = new Annotation("Doe");
mention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
CoreMap sentence = new Annotation("Doe");
sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention));
Annotation annotation = new Annotation("Doe");
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
annotator.annotate(annotation);
CoreMap resultMention = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(CoreAnnotations.MentionsAnnotation.class).get(0);
assertEquals("MALE", resultMention.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testMentionWithNullTokenList() throws Exception {
// File maleFile = folder.newFile("male.txt");
// OutputStreamWriter maleOut = new OutputStreamWriter(new FileOutputStream(maleFile), StandardCharsets.UTF_8);
// maleOut.write("John");
// maleOut.close();
// File femaleFile = folder.newFile("female.txt");
// OutputStreamWriter femaleOut = new OutputStreamWriter(new FileOutputStream(femaleFile), StandardCharsets.UTF_8);
// femaleOut.write("Mary");
// femaleOut.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreMap mention = new Annotation("John");
mention.set(CoreAnnotations.TokensAnnotation.class, null);
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
CoreMap sentence = new Annotation("John");
sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention));
Annotation annotation = new Annotation("Text");
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
try {
annotator.annotate(annotation);
assertNull(mention.get(CoreAnnotations.GenderAnnotation.class));
} catch (Exception e) {
fail("Annotator should handle null token list without throwing.");
}
}

@Test
public void testSentenceWithNullMentionsList() throws Exception {
// File maleFile = folder.newFile("male.txt");
// OutputStreamWriter maleOut = new OutputStreamWriter(new FileOutputStream(maleFile), StandardCharsets.UTF_8);
// maleOut.write("John");
// maleOut.close();
// File femaleFile = folder.newFile("female.txt");
// OutputStreamWriter femaleOut = new OutputStreamWriter(new FileOutputStream(femaleFile), StandardCharsets.UTF_8);
// femaleOut.write("Mary");
// femaleOut.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreMap sentence = new Annotation("Sentence");
sentence.set(CoreAnnotations.MentionsAnnotation.class, null);
Annotation annotation = new Annotation("Document");
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
try {
annotator.annotate(annotation);
} catch (Exception e) {
fail("Annotator should handle null mentions list without throwing.");
}
}

@Test
public void testEntityMentionMissingGenderRelevantName() throws Exception {
// File maleFile = folder.newFile("male.txt");
// OutputStreamWriter maleOut = new OutputStreamWriter(new FileOutputStream(maleFile), StandardCharsets.UTF_8);
// maleOut.write("John");
// maleOut.close();
// File femaleFile = folder.newFile("female.txt");
// OutputStreamWriter femaleOut = new OutputStreamWriter(new FileOutputStream(femaleFile), StandardCharsets.UTF_8);
// femaleOut.write("Mary");
// femaleOut.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("UnknownName");
CoreMap mention = new Annotation("UnknownName");
mention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
CoreMap sentence = new Annotation("UnknownName");
sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention));
Annotation annotation = new Annotation("UnknownName");
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
annotator.annotate(annotation);
CoreMap updatedMention = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(CoreAnnotations.MentionsAnnotation.class).get(0);
assertNull(updatedMention.get(CoreAnnotations.GenderAnnotation.class));
assertNull(updatedMention.get(CoreAnnotations.TokensAnnotation.class).get(0).get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testNoSentencesAnnotationPresent() throws Exception {
// File maleFile = folder.newFile("male.txt");
// OutputStreamWriter maleOut = new OutputStreamWriter(new FileOutputStream(maleFile), StandardCharsets.UTF_8);
// maleOut.write("John");
// maleOut.close();
// File femaleFile = folder.newFile("female.txt");
// OutputStreamWriter femaleOut = new OutputStreamWriter(new FileOutputStream(femaleFile), StandardCharsets.UTF_8);
// femaleOut.write("Mary");
// femaleOut.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
Annotation annotation = new Annotation("Text without sentence annotation");
try {
annotator.annotate(annotation);
fail("Annotator should throw NullPointerException when SentencesAnnotation is missing.");
} catch (NullPointerException expected) {
assertTrue(true);
}
}

@Test
public void testNameWithTrailingSpaceStillMatches() throws Exception {
// File maleFile = folder.newFile("male.txt");
// OutputStreamWriter maleOut = new OutputStreamWriter(new FileOutputStream(maleFile), StandardCharsets.UTF_8);
// maleOut.write("John");
// maleOut.close();
// File femaleFile = folder.newFile("female.txt");
// OutputStreamWriter femaleOut = new OutputStreamWriter(new FileOutputStream(femaleFile), StandardCharsets.UTF_8);
// femaleOut.write("");
// femaleOut.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord(" John ");
CoreMap mention = new Annotation(" John ");
mention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
CoreMap sentence = new Annotation(" John ");
sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention));
Annotation annotation = new Annotation(" John ");
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
annotator.annotate(annotation);
CoreMap resultMention = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(CoreAnnotations.MentionsAnnotation.class).get(0);
assertNull(resultMention.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testNameListContainingDuplicateEntries() throws Exception {
// File maleFile = folder.newFile("male.txt");
// OutputStreamWriter maleOut = new OutputStreamWriter(new FileOutputStream(maleFile), StandardCharsets.UTF_8);
// maleOut.write("John,John,John");
// maleOut.close();
// File femaleFile = folder.newFile("female.txt");
// OutputStreamWriter femaleOut = new OutputStreamWriter(new FileOutputStream(femaleFile), StandardCharsets.UTF_8);
// femaleOut.write("");
// femaleOut.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("John");
CoreMap mention = new Annotation("John");
mention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
CoreMap sentence = new Annotation("John");
sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention));
Annotation annotation = new Annotation("John");
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
annotator.annotate(annotation);
CoreMap resultMention = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(CoreAnnotations.MentionsAnnotation.class).get(0);
assertEquals("MALE", resultMention.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("MALE", resultMention.get(CoreAnnotations.TokensAnnotation.class).get(0).get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testEmptyGenderNameFilesLoadNoNames() throws Exception {
// File maleFile = folder.newFile("empty_male.txt");
// OutputStreamWriter maleOut = new OutputStreamWriter(new FileOutputStream(maleFile));
// maleOut.write("");
// maleOut.close();
// File femaleFile = folder.newFile("empty_female.txt");
// OutputStreamWriter femaleOut = new OutputStreamWriter(new FileOutputStream(femaleFile));
// femaleOut.write("");
// femaleOut.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("John");
CoreMap mention = new Annotation("John");
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
mention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
CoreMap sentence = new Annotation("Sentence");
sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention));
Annotation annotation = new Annotation("Document");
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
annotator.annotate(annotation);
assertNull(mention.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testNameWithSpecialCharactersNotRecognized() throws Exception {
// File maleFile = folder.newFile("male.txt");
// OutputStreamWriter maleOut = new OutputStreamWriter(new FileOutputStream(maleFile));
// maleOut.write("John");
// maleOut.close();
// File femaleFile = folder.newFile("female.txt");
// OutputStreamWriter femaleOut = new OutputStreamWriter(new FileOutputStream(femaleFile));
// femaleOut.write("");
// femaleOut.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("John*");
CoreMap mention = new Annotation("John*");
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
mention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
CoreMap sentence = new Annotation("Sentence");
sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention));
Annotation annotation = new Annotation("Text");
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
annotator.annotate(annotation);
assertNull(mention.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testNameListWithWhitespacePadding() throws Exception {
// File maleFile = folder.newFile("male.txt");
// OutputStreamWriter maleOut = new OutputStreamWriter(new FileOutputStream(maleFile));
// maleOut.write("  John  , Mark");
// maleOut.close();
// File femaleFile = folder.newFile("female.txt");
// OutputStreamWriter femaleOut = new OutputStreamWriter(new FileOutputStream(femaleFile));
// femaleOut.write("");
// femaleOut.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("John");
CoreMap mention = new Annotation("John");
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
mention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
CoreMap sentence = new Annotation("Sentence");
sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention));
Annotation annotation = new Annotation("Document");
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
annotator.annotate(annotation);
assertEquals("MALE", mention.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testNullAnnotationPassed() throws Exception {
// File maleFile = folder.newFile("male.txt");
// OutputStreamWriter maleOut = new OutputStreamWriter(new FileOutputStream(maleFile));
// maleOut.write("John");
// maleOut.close();
// File femaleFile = folder.newFile("female.txt");
// OutputStreamWriter femaleOut = new OutputStreamWriter(new FileOutputStream(femaleFile));
// femaleOut.write("Mary");
// femaleOut.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
try {
annotator.annotate(null);
fail("Annotator must throw NullPointerException when passed a null Annotation.");
} catch (NullPointerException expected) {
assertTrue(expected instanceof NullPointerException);
}
}

@Test
public void testEmptyAnnotationWithNoMentionsKey() throws Exception {
// File maleFile = folder.newFile("male.txt");
// OutputStreamWriter maleOut = new OutputStreamWriter(new FileOutputStream(maleFile));
// maleOut.write("John");
// maleOut.close();
// File femaleFile = folder.newFile("female.txt");
// OutputStreamWriter femaleOut = new OutputStreamWriter(new FileOutputStream(femaleFile));
// femaleOut.write("Mary");
// femaleOut.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreMap sentence = new Annotation("Sentence");
Annotation annotation = new Annotation("Text");
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
try {
annotator.annotate(annotation);
} catch (Exception e) {
fail("Annotator should ignore sentence with missing MentionsAnnotation and not throw.");
}
}

@Test
public void testGenderNameListWithBlankLine() throws Exception {
// File maleFile = folder.newFile("male.txt");
// File femaleFile = folder.newFile("female.txt");
// OutputStreamWriter maleWriter = new OutputStreamWriter(new FileOutputStream(maleFile), "UTF-8");
// maleWriter.write("John\n\nMike");
// maleWriter.close();
// OutputStreamWriter femaleWriter = new OutputStreamWriter(new FileOutputStream(femaleFile), "UTF-8");
// femaleWriter.write("Mary");
// femaleWriter.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("Mike");
CoreMap mention = new Annotation("Mike");
mention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
CoreMap sentence = new Annotation("Sentence");
sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention));
Annotation annotation = new Annotation("Doc");
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
annotator.annotate(annotation);
assertEquals("MALE", mention.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testGenderNameListWithWhitespaceNames() throws Exception {
// File maleFile = folder.newFile("male.txt");
// File femaleFile = folder.newFile("female.txt");
// OutputStreamWriter maleWriter = new OutputStreamWriter(new FileOutputStream(maleFile), "UTF-8");
// maleWriter.write(" John ,  Mark ");
// maleWriter.close();
// OutputStreamWriter femaleWriter = new OutputStreamWriter(new FileOutputStream(femaleFile), "UTF-8");
// femaleWriter.write("");
// femaleWriter.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("Mark");
CoreMap mention = new Annotation("Mark");
mention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
CoreMap sentence = new Annotation("Sentence");
sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention));
Annotation annotation = new Annotation("Doc");
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
annotator.annotate(annotation);
assertEquals("MALE", mention.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testTokensPresentButEntityTypeMissing() throws Exception {
// File maleFile = folder.newFile("male.txt");
// File femaleFile = folder.newFile("female.txt");
// OutputStreamWriter maleWriter = new OutputStreamWriter(new FileOutputStream(maleFile), "UTF-8");
// maleWriter.write("John");
// maleWriter.close();
// OutputStreamWriter femaleWriter = new OutputStreamWriter(new FileOutputStream(femaleFile), "UTF-8");
// femaleWriter.write("Mary");
// femaleWriter.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("John");
CoreMap mention = new Annotation("John");
mention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
CoreMap sentence = new Annotation("Sentence");
sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention));
Annotation annotation = new Annotation("Doc");
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
annotator.annotate(annotation);
assertNull(mention.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testNameMatchingWithMixedCase() throws Exception {
// File maleFile = folder.newFile("male.txt");
// File femaleFile = folder.newFile("female.txt");
// OutputStreamWriter maleWriter = new OutputStreamWriter(new FileOutputStream(maleFile), "UTF-8");
// maleWriter.write("john");
// maleWriter.close();
// OutputStreamWriter femaleWriter = new OutputStreamWriter(new FileOutputStream(femaleFile), "UTF-8");
// femaleWriter.write("");
// femaleWriter.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("JoHn");
CoreMap mention = new Annotation("JoHn");
mention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
CoreMap sentence = new Annotation("Sentence");
sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention));
Annotation annotation = new Annotation("Doc");
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
annotator.annotate(annotation);
assertEquals("MALE", mention.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testGenderNameListWithCommaAtEnd() throws Exception {
// File maleFile = folder.newFile("male.txt");
// File femaleFile = folder.newFile("female.txt");
// OutputStreamWriter maleWriter = new OutputStreamWriter(new FileOutputStream(maleFile));
// maleWriter.write("John,");
// maleWriter.close();
// OutputStreamWriter femaleWriter = new OutputStreamWriter(new FileOutputStream(femaleFile));
// femaleWriter.write("");
// femaleWriter.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("John");
CoreMap mention = new Annotation("John");
mention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
CoreMap sentence = new Annotation("Sentence");
sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention));
Annotation annotation = new Annotation("Doc");
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
annotator.annotate(annotation);
assertEquals("MALE", mention.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testEntityMentionWithNullFirstToken() throws Exception {
// File maleFile = folder.newFile("male.txt");
// File femaleFile = folder.newFile("female.txt");
// OutputStreamWriter maleOut = new OutputStreamWriter(new FileOutputStream(maleFile), "UTF-8");
// maleOut.write("John");
// maleOut.close();
// OutputStreamWriter femaleOut = new OutputStreamWriter(new FileOutputStream(femaleFile), "UTF-8");
// femaleOut.write("");
// femaleOut.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
List<CoreLabel> tokens = new ArrayList<CoreLabel>();
tokens.add(null);
CoreMap mention = new Annotation("John");
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
CoreMap sentence = new Annotation("Sentence");
sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention));
Annotation annotation = new Annotation("Document");
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
try {
annotator.annotate(annotation);
fail("Expected NullPointerException due to null first token.");
} catch (NullPointerException e) {
assertTrue(true);
}
}

@Test
public void testIncompleteConfiguredGenderNameFiles() throws Exception {
// File maleFile = folder.newFile("male.txt");
// OutputStreamWriter maleOut = new OutputStreamWriter(new FileOutputStream(maleFile), "UTF-8");
// maleOut.write("John");
// maleOut.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("John");
CoreMap mention = new Annotation("John");
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
mention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
CoreMap sentence = new Annotation("Sentence");
sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention));
Annotation annotation = new Annotation("Text");
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
annotator.annotate(annotation);
assertEquals("MALE", mention.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testGenderAnnotatorWithEmptyNameFilePropertyPath() throws Exception {
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", "");
props.setProperty("gender.femaleNamesFile", "");
try {
new GenderAnnotator("gender", props);
fail("Expected IllegalArgumentException or IOException for empty file paths.");
} catch (Exception e) {
assertTrue(e instanceof IllegalArgumentException || e instanceof IOException);
}
}

@Test
public void testSingleNamePerLineFormat() throws Exception {
// File maleFile = folder.newFile("male.txt");
// OutputStreamWriter maleWriter = new OutputStreamWriter(new FileOutputStream(maleFile), "UTF-8");
// maleWriter.write("John\nMark\nLuke");
// maleWriter.close();
// File femaleFile = folder.newFile("female.txt");
// OutputStreamWriter femaleWriter = new OutputStreamWriter(new FileOutputStream(femaleFile), "UTF-8");
// femaleWriter.write("");
// femaleWriter.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("Luke");
CoreMap mention = new Annotation("Luke");
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
mention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
CoreMap sentence = new Annotation("Sentence");
sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention));
Annotation annotation = new Annotation("Sentence");
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
annotator.annotate(annotation);
assertEquals("MALE", mention.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testFileWithExtraCommasAndSpaces() throws Exception {
// File maleFile = folder.newFile("male.txt");
// OutputStreamWriter maleWriter = new OutputStreamWriter(new FileOutputStream(maleFile), "UTF-8");
// maleWriter.write(" ,John,, , Mark , ");
// maleWriter.close();
// File femaleFile = folder.newFile("female.txt");
// OutputStreamWriter femaleWriter = new OutputStreamWriter(new FileOutputStream(femaleFile), "UTF-8");
// femaleWriter.write("");
// femaleWriter.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("Mark");
CoreMap mention = new Annotation("Mark");
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
mention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
CoreMap sentence = new Annotation("Sentence");
sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention));
Annotation annotation = new Annotation("Text");
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
annotator.annotate(annotation);
assertEquals("MALE", mention.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testMentionWithEmptyEntityTypeAnnotationString() throws Exception {
// File maleFile = folder.newFile("male.txt");
// OutputStreamWriter maleOut = new OutputStreamWriter(new FileOutputStream(maleFile), "UTF-8");
// maleOut.write("John\n");
// maleOut.close();
// File femaleFile = folder.newFile("female.txt");
// OutputStreamWriter femaleOut = new OutputStreamWriter(new FileOutputStream(femaleFile), "UTF-8");
// femaleOut.write("Mary\n");
// femaleOut.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("John");
CoreMap mention = new Annotation("John");
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "");
mention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
CoreMap sentence = new Annotation("Sentence");
sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention));
Annotation annotation = new Annotation("Annotation");
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
annotator.annotate(annotation);
assertNull(mention.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testMentionEntityTypeSetToNullExplicitly() throws Exception {
// File maleFile = folder.newFile("male_names.txt");
// OutputStreamWriter maleOut = new OutputStreamWriter(new FileOutputStream(maleFile), "UTF-8");
// maleOut.write("Paul");
// maleOut.close();
// File femaleFile = folder.newFile("female_names.txt");
// OutputStreamWriter femaleOut = new OutputStreamWriter(new FileOutputStream(femaleFile), "UTF-8");
// femaleOut.write("Sarah");
// femaleOut.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("Paul");
CoreMap mention = new Annotation("Paul");
mention.set(CoreAnnotations.EntityTypeAnnotation.class, null);
mention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
CoreMap sentence = new Annotation("Sentence");
sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention));
Annotation annotation = new Annotation("Doc");
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
try {
annotator.annotate(annotation);
assertNull(mention.get(CoreAnnotations.GenderAnnotation.class));
} catch (Exception e) {
fail("GenderAnnotator should not throw if EntityTypeAnnotation is null.");
}
}

@Test
public void testMentionTokenListWithWhitespaceOnlyWord() throws Exception {
// File maleFile = folder.newFile("male_names.txt");
// OutputStreamWriter maleOut = new OutputStreamWriter(new FileOutputStream(maleFile), "UTF-8");
// maleOut.write("Tom");
// maleOut.close();
// File femaleFile = folder.newFile("female_names.txt");
// OutputStreamWriter femaleOut = new OutputStreamWriter(new FileOutputStream(femaleFile), "UTF-8");
// femaleOut.write("");
// femaleOut.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("    ");
CoreMap mention = new Annotation("   ");
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
mention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
CoreMap sentence = new Annotation("Line");
sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention));
Annotation annotation = new Annotation("Body");
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
annotator.annotate(annotation);
assertNull(mention.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testGenderAnnotationIsOnlySetOnce() throws Exception {
// File maleFile = folder.newFile("male.txt");
// OutputStreamWriter maleOut = new OutputStreamWriter(new FileOutputStream(maleFile), "UTF-8");
// maleOut.write("John");
// maleOut.close();
// File femaleFile = folder.newFile("female.txt");
// OutputStreamWriter femaleOut = new OutputStreamWriter(new FileOutputStream(femaleFile), "UTF-8");
// femaleOut.write("");
// femaleOut.close();
Properties props = new Properties();
// props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
// props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("John");
CoreMap mention = new Annotation("John");
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
mention.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
CoreMap sentence = new Annotation("Sentence");
sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention));
Annotation annotation = new Annotation("Doc");
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
annotator.annotate(annotation);
String firstGenderValue = mention.get(CoreAnnotations.GenderAnnotation.class);
annotator.annotate(annotation);
String secondGenderValue = mention.get(CoreAnnotations.GenderAnnotation.class);
assertEquals("MALE", firstGenderValue);
assertEquals("MALE", secondGenderValue);
}
}
