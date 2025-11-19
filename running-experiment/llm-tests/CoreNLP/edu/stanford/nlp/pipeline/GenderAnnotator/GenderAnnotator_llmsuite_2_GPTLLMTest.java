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

public class GenderAnnotator_llmsuite_2_GPTLLMTest {

@Test
public void testConstructorWithCustomFiles() throws IOException {
File maleFile = File.createTempFile("male_names", ".txt");
File femaleFile = File.createTempFile("female_names", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
BufferedWriter maleWriter = new BufferedWriter(new FileWriter(maleFile));
maleWriter.write("John,Michael");
maleWriter.close();
BufferedWriter femaleWriter = new BufferedWriter(new FileWriter(femaleFile));
femaleWriter.write("Mary,Linda");
femaleWriter.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
assertTrue(annotator.maleNames.contains("john"));
assertTrue(annotator.maleNames.contains("michael"));
assertTrue(annotator.femaleNames.contains("mary"));
assertTrue(annotator.femaleNames.contains("linda"));
}

@Test
public void testAnnotateWithMaleName() throws IOException {
File maleFile = File.createTempFile("male_names", ".txt");
File femaleFile = File.createTempFile("female_names", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
BufferedWriter maleWriter = new BufferedWriter(new FileWriter(maleFile));
maleWriter.write("John");
maleWriter.close();
BufferedWriter femaleWriter = new BufferedWriter(new FileWriter(femaleFile));
femaleWriter.write("");
femaleWriter.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("John");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
Annotation mention = new Annotation("John");
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new Annotation("John went home.");
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation document = new Annotation("John went home.");
document.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(document);
CoreMap resultMention = document.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(CoreAnnotations.MentionsAnnotation.class).get(0);
assertEquals("MALE", resultMention.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("MALE", token.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testAnnotateWithFemaleName() throws IOException {
File maleFile = File.createTempFile("male_names", ".txt");
File femaleFile = File.createTempFile("female_names", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
BufferedWriter maleWriter = new BufferedWriter(new FileWriter(maleFile));
maleWriter.write("");
maleWriter.close();
BufferedWriter femaleWriter = new BufferedWriter(new FileWriter(femaleFile));
femaleWriter.write("Mary");
femaleWriter.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("Mary");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
Annotation mention = new Annotation("Mary");
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new Annotation("Mary is here.");
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation document = new Annotation("Mary is here.");
document.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(document);
CoreMap resultMention = document.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(CoreAnnotations.MentionsAnnotation.class).get(0);
assertEquals("FEMALE", resultMention.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("FEMALE", token.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testAnnotateWithUnknownName() throws IOException {
File maleFile = File.createTempFile("male_names", ".txt");
File femaleFile = File.createTempFile("female_names", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
BufferedWriter maleWriter = new BufferedWriter(new FileWriter(maleFile));
maleWriter.write("John");
maleWriter.close();
BufferedWriter femaleWriter = new BufferedWriter(new FileWriter(femaleFile));
femaleWriter.write("Mary");
femaleWriter.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("Alex");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
Annotation mention = new Annotation("Alex");
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new Annotation("Alex is unknown.");
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation document = new Annotation("Alex is unknown.");
document.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(document);
CoreMap resultMention = document.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(CoreAnnotations.MentionsAnnotation.class).get(0);
assertNull(resultMention.get(CoreAnnotations.GenderAnnotation.class));
assertNull(token.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testAnnotateNonPersonEntity() throws IOException {
File maleFile = File.createTempFile("male_names", ".txt");
File femaleFile = File.createTempFile("female_names", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
BufferedWriter maleWriter = new BufferedWriter(new FileWriter(maleFile));
maleWriter.write("John");
maleWriter.close();
BufferedWriter femaleWriter = new BufferedWriter(new FileWriter(femaleFile));
femaleWriter.write("Mary");
femaleWriter.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("Seattle");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
Annotation mention = new Annotation("Seattle");
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "LOCATION");
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new Annotation("Seattle is a city.");
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation document = new Annotation("Seattle is a city.");
document.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(document);
CoreMap resultMention = document.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(CoreAnnotations.MentionsAnnotation.class).get(0);
assertNull(resultMention.get(CoreAnnotations.GenderAnnotation.class));
assertNull(token.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testRequirements() throws IOException {
File maleFile = File.createTempFile("males", ".txt");
File femaleFile = File.createTempFile("females", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
BufferedWriter out1 = new BufferedWriter(new FileWriter(maleFile));
out1.write("John");
out1.close();
BufferedWriter out2 = new BufferedWriter(new FileWriter(femaleFile));
out2.write("Mary");
out2.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
Set<Class<? extends CoreAnnotation>> required = annotator.requires();
assertTrue(required.contains(CoreAnnotations.TextAnnotation.class));
assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
assertTrue(required.contains(CoreAnnotations.MentionsAnnotation.class));
assertTrue(required.contains(CoreAnnotations.EntityTypeAnnotation.class));
}

@Test
public void testRequirementsSatisfied() throws IOException {
File maleFile = File.createTempFile("males", ".txt");
File femaleFile = File.createTempFile("females", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
BufferedWriter out1 = new BufferedWriter(new FileWriter(maleFile));
out1.write("John");
out1.close();
BufferedWriter out2 = new BufferedWriter(new FileWriter(femaleFile));
out2.write("Mary");
out2.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
assertEquals(1, satisfied.size());
assertTrue(satisfied.contains(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testAnnotateWithNullSentencesAnnotation() throws IOException {
File maleFile = File.createTempFile("males", ".txt");
File femaleFile = File.createTempFile("females", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
BufferedWriter maleWriter = new BufferedWriter(new FileWriter(maleFile));
maleWriter.write("John");
maleWriter.close();
BufferedWriter femaleWriter = new BufferedWriter(new FileWriter(femaleFile));
femaleWriter.write("Mary");
femaleWriter.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
Annotation annotation = new Annotation("This is a test.");
annotation.set(CoreAnnotations.SentencesAnnotation.class, null);
annotator.annotate(annotation);
}

@Test
public void testAnnotateWithEmptyMentionTokens() throws IOException {
File maleFile = File.createTempFile("males", ".txt");
File femaleFile = File.createTempFile("females", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
BufferedWriter maleWriter = new BufferedWriter(new FileWriter(maleFile));
maleWriter.write("John");
maleWriter.close();
BufferedWriter femaleWriter = new BufferedWriter(new FileWriter(femaleFile));
femaleWriter.write("Mary");
femaleWriter.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
Annotation mention = new Annotation("");
mention.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new Annotation("");
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation document = new Annotation("");
document.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(document);
assertNull(mention.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testLoadGenderNamesWithEmptyFile() throws IOException {
File emptyFile = File.createTempFile("empty", ".txt");
emptyFile.deleteOnExit();
BufferedWriter writer = new BufferedWriter(new FileWriter(emptyFile));
writer.write("");
writer.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", emptyFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", emptyFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
assertTrue(annotator.maleNames.isEmpty());
assertTrue(annotator.femaleNames.isEmpty());
}

@Test
public void testAnnotateWithMixedCaseName() throws IOException {
File maleFile = File.createTempFile("males", ".txt");
File femaleFile = File.createTempFile("females", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
BufferedWriter maleWriter = new BufferedWriter(new FileWriter(maleFile));
maleWriter.write("JoHn");
maleWriter.close();
BufferedWriter femaleWriter = new BufferedWriter(new FileWriter(femaleFile));
femaleWriter.write("");
femaleWriter.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("JOHN");
List<CoreLabel> tokens = new ArrayList<CoreLabel>();
tokens.add(token);
Annotation mention = new Annotation("JOHN");
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
List<CoreMap> mentions = new ArrayList<CoreMap>();
mentions.add(mention);
CoreMap sentence = new Annotation("JOHN is here.");
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
List<CoreMap> sentences = new ArrayList<CoreMap>();
sentences.add(sentence);
Annotation document = new Annotation("JOHN is here.");
document.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(document);
assertEquals("MALE", mention.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("MALE", token.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testAnnotateWithMultipleNamesInSameMention() throws IOException {
File maleFile = File.createTempFile("males", ".txt");
File femaleFile = File.createTempFile("females", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
BufferedWriter maleWriter = new BufferedWriter(new FileWriter(maleFile));
maleWriter.write("John");
maleWriter.close();
BufferedWriter femaleWriter = new BufferedWriter(new FileWriter(femaleFile));
femaleWriter.write("Mary");
femaleWriter.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token1 = new CoreLabel();
token1.setWord("John");
CoreLabel token2 = new CoreLabel();
token2.setWord("Smith");
List<CoreLabel> tokens = new ArrayList<CoreLabel>();
tokens.add(token1);
tokens.add(token2);
Annotation mention = new Annotation("John Smith");
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
List<CoreMap> mentions = new ArrayList<CoreMap>();
mentions.add(mention);
CoreMap sentence = new Annotation("John Smith arrived.");
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
List<CoreMap> sentences = new ArrayList<CoreMap>();
sentences.add(sentence);
Annotation document = new Annotation("John Smith arrived.");
document.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(document);
assertEquals("MALE", mention.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("MALE", token1.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("MALE", token2.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testAnnotateWithMalformedNameListCsv() throws IOException {
File maleFile = File.createTempFile("malformed_males", ".txt");
File femaleFile = File.createTempFile("malformed_females", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
BufferedWriter maleWriter = new BufferedWriter(new FileWriter(maleFile));
maleWriter.write("John,,Robert,,");
maleWriter.close();
BufferedWriter femaleWriter = new BufferedWriter(new FileWriter(femaleFile));
femaleWriter.write("Mary,,Linda,");
femaleWriter.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
assertTrue(annotator.maleNames.contains("john"));
assertTrue(annotator.maleNames.contains("robert"));
assertTrue(annotator.femaleNames.contains("mary"));
assertTrue(annotator.femaleNames.contains("linda"));
assertTrue(annotator.maleNames.contains(""));
assertTrue(annotator.femaleNames.contains(""));
}

@Test
public void testAnnotateWithNullMentionList() throws IOException {
File maleFile = File.createTempFile("male_names", ".txt");
File femaleFile = File.createTempFile("female_names", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
BufferedWriter maleWriter = new BufferedWriter(new FileWriter(maleFile));
maleWriter.write("John");
maleWriter.close();
BufferedWriter femaleWriter = new BufferedWriter(new FileWriter(femaleFile));
femaleWriter.write("Mary");
femaleWriter.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
Annotation document = new Annotation("John is from Seattle.");
CoreMap sentence = new Annotation("John is from Seattle.");
sentence.set(CoreAnnotations.MentionsAnnotation.class, null);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
document.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(document);
assertEquals(1, document.get(CoreAnnotations.SentencesAnnotation.class).size());
}

@Test
public void testAnnotateWithMentionNoEntityType() throws IOException {
File maleFile = File.createTempFile("male_names", ".txt");
File femaleFile = File.createTempFile("female_names", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
BufferedWriter maleWriter = new BufferedWriter(new FileWriter(maleFile));
maleWriter.write("John");
maleWriter.close();
BufferedWriter femaleWriter = new BufferedWriter(new FileWriter(femaleFile));
femaleWriter.write("Mary");
femaleWriter.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("John");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
Annotation mention = new Annotation("John");
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new Annotation("John walks.");
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation document = new Annotation("John walks.");
document.set(CoreAnnotations.SentencesAnnotation.class, sentences);
try {
annotator.annotate(document);
fail("Expected NullPointerException due to missing EntityTypeAnnotation");
} catch (NullPointerException expected) {
}
}

@Test
public void testLoadGenderNamesWithEmptyLines() throws IOException {
File maleFile = File.createTempFile("male_emptylines", ".txt");
File femaleFile = File.createTempFile("female_emptylines", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
BufferedWriter maleWriter = new BufferedWriter(new FileWriter(maleFile));
maleWriter.write("\n\nJohn\n\n");
maleWriter.close();
BufferedWriter femaleWriter = new BufferedWriter(new FileWriter(femaleFile));
femaleWriter.write("\nMary\n");
femaleWriter.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
assertTrue(annotator.maleNames.contains("john"));
assertTrue(annotator.femaleNames.contains("mary"));
assertFalse(annotator.maleNames.contains(""));
assertFalse(annotator.femaleNames.contains(""));
}

@Test
public void testNameFileWithWhitespaceOnlyLines() throws IOException {
File maleFile = File.createTempFile("whitespace_male", ".txt");
File femaleFile = File.createTempFile("whitespace_female", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
BufferedWriter maleWriter = new BufferedWriter(new FileWriter(maleFile));
maleWriter.write("  \nJohn \n  ");
maleWriter.close();
BufferedWriter femaleWriter = new BufferedWriter(new FileWriter(femaleFile));
femaleWriter.write("   \nMary\n \t ");
femaleWriter.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
assertTrue(annotator.maleNames.contains("john"));
assertTrue(annotator.femaleNames.contains("mary"));
}

@Test
public void testTokenWordIsNull() throws IOException {
File maleFile = File.createTempFile("male_names", ".txt");
File femaleFile = File.createTempFile("female_names", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
BufferedWriter maleWriter = new BufferedWriter(new FileWriter(maleFile));
maleWriter.write("Michael");
maleWriter.close();
BufferedWriter femaleWriter = new BufferedWriter(new FileWriter(femaleFile));
femaleWriter.write("Sarah");
femaleWriter.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
Annotation mention = new Annotation("");
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new Annotation("Unknown person.");
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
List<CoreMap> sentenceList = new ArrayList<>();
sentenceList.add(sentence);
Annotation document = new Annotation("Unknown person.");
document.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);
try {
annotator.annotate(document);
fail("Expected NullPointerException when first token's word is null");
} catch (NullPointerException expected) {
}
}

@Test
public void testFirstNameNotInListButExactMatchInSentenceTokens() throws IOException {
File maleFile = File.createTempFile("males_custom", ".txt");
File femaleFile = File.createTempFile("females_custom", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
BufferedWriter maleWriter = new BufferedWriter(new FileWriter(maleFile));
maleWriter.write("john");
maleWriter.close();
BufferedWriter femaleWriter = new BufferedWriter(new FileWriter(femaleFile));
femaleWriter.write("mary");
femaleWriter.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token1 = new CoreLabel();
token1.setWord("NotAName");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token1);
Annotation mention = new Annotation("NotAName");
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new Annotation("NotAName threw a ball.");
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation document = new Annotation("NotAName threw a ball.");
document.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(document);
assertNull(mention.get(CoreAnnotations.GenderAnnotation.class));
assertNull(token1.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testEmptySentenceListDoesNotThrowException() throws IOException {
File maleFile = File.createTempFile("males_empty_sentence", ".txt");
File femaleFile = File.createTempFile("females_empty_sentence", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
BufferedWriter maleWriter = new BufferedWriter(new FileWriter(maleFile));
maleWriter.write("Kevin");
maleWriter.close();
BufferedWriter femaleWriter = new BufferedWriter(new FileWriter(femaleFile));
femaleWriter.write("Nancy");
femaleWriter.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
Annotation document = new Annotation("Test input with no sentences.");
document.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());
annotator.annotate(document);
assertTrue(document.get(CoreAnnotations.SentencesAnnotation.class).isEmpty());
}

@Test
public void testMentionHasNullTokensList() throws IOException {
File maleFile = File.createTempFile("males_null_tokens", ".txt");
File femaleFile = File.createTempFile("females_null_tokens", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
BufferedWriter maleWriter = new BufferedWriter(new FileWriter(maleFile));
maleWriter.write("David");
maleWriter.close();
BufferedWriter femaleWriter = new BufferedWriter(new FileWriter(femaleFile));
femaleWriter.write("Rachel");
femaleWriter.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
Annotation mention = new Annotation("David");
mention.set(CoreAnnotations.TokensAnnotation.class, null);
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
List<CoreMap> mentions = new ArrayList<CoreMap>();
mentions.add(mention);
CoreMap sentence = new Annotation("David went shopping.");
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
List<CoreMap> sentences = new ArrayList<CoreMap>();
sentences.add(sentence);
Annotation document = new Annotation("David went shopping.");
document.set(CoreAnnotations.SentencesAnnotation.class, sentences);
try {
annotator.annotate(document);
fail("Expected NullPointerException due to null TokensAnnotation in mention");
} catch (NullPointerException expected) {
}
}

@Test
public void testSingleTokenMentionWithEmptyWord() throws IOException {
File maleFile = File.createTempFile("males_empty_word", ".txt");
File femaleFile = File.createTempFile("females_empty_word", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
BufferedWriter maleWriter = new BufferedWriter(new FileWriter(maleFile));
maleWriter.write("Ethan");
maleWriter.close();
BufferedWriter femaleWriter = new BufferedWriter(new FileWriter(femaleFile));
femaleWriter.write("Olivia");
femaleWriter.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("");
List<CoreLabel> tokens = new ArrayList<CoreLabel>();
tokens.add(token);
Annotation mention = new Annotation("");
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
List<CoreMap> mentions = new ArrayList<CoreMap>();
mentions.add(mention);
CoreMap sentence = new Annotation(" ");
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
List<CoreMap> sentences = new ArrayList<CoreMap>();
sentences.add(sentence);
Annotation document = new Annotation(" ");
document.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(document);
assertNull(mention.get(CoreAnnotations.GenderAnnotation.class));
assertNull(token.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testMentionWithEntityTypeCaseInsensitive() throws IOException {
File maleFile = File.createTempFile("males_case_entity", ".txt");
File femaleFile = File.createTempFile("females_case_entity", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
BufferedWriter maleWriter = new BufferedWriter(new FileWriter(maleFile));
maleWriter.write("Oscar");
maleWriter.close();
BufferedWriter femaleWriter = new BufferedWriter(new FileWriter(femaleFile));
femaleWriter.write("Emily");
femaleWriter.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("Oscar");
List<CoreLabel> tokens = new ArrayList<CoreLabel>();
tokens.add(token);
Annotation mention = new Annotation("Oscar");
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "person");
List<CoreMap> mentions = new ArrayList<CoreMap>();
mentions.add(mention);
CoreMap sentence = new Annotation("Oscar enters stage left.");
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
List<CoreMap> sentences = new ArrayList<CoreMap>();
sentences.add(sentence);
Annotation document = new Annotation("Oscar enters stage left.");
document.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(document);
assertNull(mention.get(CoreAnnotations.GenderAnnotation.class));
assertNull(token.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testCoreLabelWithNullAndBlankWord() throws IOException {
File maleFile = File.createTempFile("males_null_word", ".txt");
File femaleFile = File.createTempFile("females_null_word", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
BufferedWriter maleWriter = new BufferedWriter(new FileWriter(maleFile));
maleWriter.write("Aaron");
maleWriter.close();
BufferedWriter femaleWriter = new BufferedWriter(new FileWriter(femaleFile));
femaleWriter.write("Anna");
femaleWriter.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token1 = new CoreLabel();
token1.setWord(null);
CoreLabel token2 = new CoreLabel();
token2.setWord(" ");
List<CoreLabel> tokens = new ArrayList<CoreLabel>();
tokens.add(token1);
tokens.add(token2);
Annotation mention = new Annotation("");
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
List<CoreMap> mentions = new ArrayList<CoreMap>();
mentions.add(mention);
CoreMap sentence = new Annotation("");
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
List<CoreMap> sentences = new ArrayList<CoreMap>();
sentences.add(sentence);
Annotation document = new Annotation("");
document.set(CoreAnnotations.SentencesAnnotation.class, sentences);
try {
annotator.annotate(document);
fail("Expected NullPointerException due to token with null word");
} catch (NullPointerException expected) {
}
}

@Test
public void testLoadGenderNamesWithMultipleLinesAndDuplicateNames() throws IOException {
File maleFile = File.createTempFile("male_duplicates", ".txt");
File femaleFile = File.createTempFile("female_duplicates", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
BufferedWriter maleWriter = new BufferedWriter(new FileWriter(maleFile));
maleWriter.write("John,Robert\nMichael,John\n");
maleWriter.close();
BufferedWriter femaleWriter = new BufferedWriter(new FileWriter(femaleFile));
femaleWriter.write("Mary,Susan\nLinda,Mary\n");
femaleWriter.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
assertEquals(3, annotator.maleNames.size());
assertTrue(annotator.maleNames.contains("john"));
assertTrue(annotator.maleNames.contains("robert"));
assertTrue(annotator.maleNames.contains("michael"));
assertEquals(3, annotator.femaleNames.size());
assertTrue(annotator.femaleNames.contains("mary"));
assertTrue(annotator.femaleNames.contains("susan"));
assertTrue(annotator.femaleNames.contains("linda"));
}

@Test
public void testMentionEntityTypeNotPersonWithPersonLikeName() throws IOException {
File maleFile = File.createTempFile("male_nonperson", ".txt");
File femaleFile = File.createTempFile("female_nonperson", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
BufferedWriter maleWriter = new BufferedWriter(new FileWriter(maleFile));
maleWriter.write("James");
maleWriter.close();
BufferedWriter femaleWriter = new BufferedWriter(new FileWriter(femaleFile));
femaleWriter.write("");
femaleWriter.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("James");
List<CoreLabel> tokens = new ArrayList<CoreLabel>();
tokens.add(token);
Annotation mention = new Annotation("James");
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "ORGANIZATION");
List<CoreMap> mentions = new ArrayList<CoreMap>();
mentions.add(mention);
CoreMap sentence = new Annotation("James launched a product.");
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
List<CoreMap> sentences = new ArrayList<CoreMap>();
sentences.add(sentence);
Annotation document = new Annotation("James launched a product.");
document.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(document);
assertNull(mention.get(CoreAnnotations.GenderAnnotation.class));
assertNull(token.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testAnnotateWithNameAppearingInBothMaleAndFemaleLists() throws IOException {
File maleFile = File.createTempFile("ambiguous_male", ".txt");
File femaleFile = File.createTempFile("ambiguous_female", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
BufferedWriter maleWriter = new BufferedWriter(new FileWriter(maleFile));
maleWriter.write("Taylor");
maleWriter.close();
BufferedWriter femaleWriter = new BufferedWriter(new FileWriter(femaleFile));
femaleWriter.write("Taylor");
femaleWriter.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("Taylor");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
Annotation mention = new Annotation("Taylor");
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new Annotation("Taylor is present.");
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation document = new Annotation("Taylor is present.");
document.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(document);
assertEquals("MALE", mention.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("MALE", token.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testAnnotateMultipleSentencesWithOneEmptyMentionList() throws IOException {
File maleFile = File.createTempFile("multi_sent_male", ".txt");
File femaleFile = File.createTempFile("multi_sent_female", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
BufferedWriter maleWriter = new BufferedWriter(new FileWriter(maleFile));
maleWriter.write("Steve");
maleWriter.close();
BufferedWriter femaleWriter = new BufferedWriter(new FileWriter(femaleFile));
femaleWriter.write("Jane");
femaleWriter.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token1 = new CoreLabel();
token1.setWord("Steve");
List<CoreLabel> tokens1 = new ArrayList<>();
tokens1.add(token1);
Annotation mention1 = new Annotation("Steve");
mention1.set(CoreAnnotations.TokensAnnotation.class, tokens1);
mention1.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
List<CoreMap> mentions1 = new ArrayList<>();
mentions1.add(mention1);
CoreMap sentence1 = new Annotation("Steve works.");
sentence1.set(CoreAnnotations.MentionsAnnotation.class, mentions1);
sentence1.set(CoreAnnotations.TokensAnnotation.class, tokens1);
CoreMap sentence2 = new Annotation("Something else.");
sentence2.set(CoreAnnotations.MentionsAnnotation.class, null);
sentence2.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence1);
sentences.add(sentence2);
Annotation document = new Annotation("Two sentences.");
document.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(document);
assertEquals("MALE", mention1.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("MALE", token1.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testSingleMentionWithSingleCharacterName() throws IOException {
File maleFile = File.createTempFile("male_single_char", ".txt");
File femaleFile = File.createTempFile("female_single_char", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
BufferedWriter maleWriter = new BufferedWriter(new FileWriter(maleFile));
maleWriter.write("A");
maleWriter.close();
BufferedWriter femaleWriter = new BufferedWriter(new FileWriter(femaleFile));
femaleWriter.write("B");
femaleWriter.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("A");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
Annotation mention = new Annotation("A");
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new Annotation("A walked.");
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation document = new Annotation("A walked.");
document.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(document);
assertEquals("MALE", mention.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("MALE", token.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testEmptyEntityTypeAnnotationValue() throws IOException {
File maleFile = File.createTempFile("male_empty_entity", ".txt");
File femaleFile = File.createTempFile("female_empty_entity", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
BufferedWriter maleWriter = new BufferedWriter(new FileWriter(maleFile));
maleWriter.write("Chris");
maleWriter.close();
BufferedWriter femaleWriter = new BufferedWriter(new FileWriter(femaleFile));
femaleWriter.write("Sophia");
femaleWriter.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("Chris");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
Annotation mention = new Annotation("Chris");
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "");
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new Annotation("Chris runs.");
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation document = new Annotation("Chris runs.");
document.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(document);
assertNull(mention.get(CoreAnnotations.GenderAnnotation.class));
assertNull(token.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testTokensListWithMultipleTokensButFirstTokenNotInNameList() throws IOException {
File maleFile = File.createTempFile("male_multi_tokens", ".txt");
File femaleFile = File.createTempFile("female_multi_tokens", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
BufferedWriter maleWriter = new BufferedWriter(new FileWriter(maleFile));
maleWriter.write("Daniel");
maleWriter.close();
BufferedWriter femaleWriter = new BufferedWriter(new FileWriter(femaleFile));
femaleWriter.write("Anna");
femaleWriter.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token1 = new CoreLabel();
token1.setWord("Xyz");
CoreLabel token2 = new CoreLabel();
token2.setWord("Daniel");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token1);
tokens.add(token2);
Annotation mention = new Annotation("Xyz Daniel");
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new Annotation("Xyz Daniel is running.");
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation document = new Annotation("Xyz Daniel is running.");
document.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(document);
assertNull(mention.get(CoreAnnotations.GenderAnnotation.class));
assertNull(token1.get(CoreAnnotations.GenderAnnotation.class));
assertNull(token2.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testNameWithTrailingWhitespaceInCSVAndMention() throws IOException {
File maleFile = File.createTempFile("male_trailing_whitespace", ".txt");
File femaleFile = File.createTempFile("female_trailing_whitespace", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
BufferedWriter maleWriter = new BufferedWriter(new FileWriter(maleFile));
maleWriter.write("Liam ");
maleWriter.close();
BufferedWriter femaleWriter = new BufferedWriter(new FileWriter(femaleFile));
femaleWriter.write("Emma");
femaleWriter.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("Liam");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
Annotation mention = new Annotation("Liam");
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new Annotation("Liam arrived.");
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation document = new Annotation("Liam arrived.");
document.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(document);
assertEquals("MALE", mention.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("MALE", token.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testGenderListsPopulatedButMentionTokenIsNull() throws IOException {
File maleFile = File.createTempFile("male_null_token", ".txt");
File femaleFile = File.createTempFile("female_null_token", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
BufferedWriter maleWriter = new BufferedWriter(new FileWriter(maleFile));
maleWriter.write("Henry");
maleWriter.close();
BufferedWriter femaleWriter = new BufferedWriter(new FileWriter(femaleFile));
femaleWriter.write("Isabel");
femaleWriter.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(null);
Annotation mention = new Annotation("Henry");
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new Annotation("Henry speaks.");
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation document = new Annotation("Henry speaks.");
document.set(CoreAnnotations.SentencesAnnotation.class, sentences);
try {
annotator.annotate(document);
fail("Expected NullPointerException due to null token in mention");
} catch (NullPointerException expected) {
}
}

@Test
public void testAnnotatorWithMissingMaleNameFile() throws IOException {
File femaleFile = File.createTempFile("female_only", ".txt");
femaleFile.deleteOnExit();
BufferedWriter femaleWriter = new BufferedWriter(new FileWriter(femaleFile));
femaleWriter.write("Alice\n");
femaleWriter.close();
Properties props = new Properties();
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
assertFalse(annotator.femaleNames.isEmpty());
assertTrue(annotator.maleNames.isEmpty());
}

@Test
public void testAnnotatorWithNonExistentFilePath() {
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", "non/existent/male_names.txt");
props.setProperty("gender.femaleNamesFile", "non/existent/female_names.txt");
try {
new GenderAnnotator("gender", props);
fail("Expected RuntimeException or IOException for invalid file path");
} catch (RuntimeException e) {
}
}

@Test
public void testGenderListWithNonCsvLineFormat() throws IOException {
File maleFile = File.createTempFile("malformed_male", ".txt");
File femaleFile = File.createTempFile("malformed_female", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
BufferedWriter maleWriter = new BufferedWriter(new FileWriter(maleFile));
maleWriter.write("John Robert Thomas\nMichael\n");
maleWriter.close();
BufferedWriter femaleWriter = new BufferedWriter(new FileWriter(femaleFile));
femaleWriter.write("Mary Linda Jane");
femaleWriter.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
assertTrue(annotator.maleNames.contains("michael"));
assertTrue(annotator.maleNames.contains("john robert thomas"));
assertTrue(annotator.femaleNames.contains("mary linda jane"));
}

@Test
public void testSentenceWithNullTokensAnnotation() throws IOException {
File maleFile = File.createTempFile("male_null_tokensent", ".txt");
File femaleFile = File.createTempFile("female_null_tokensent", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
BufferedWriter maleWriter = new BufferedWriter(new FileWriter(maleFile));
maleWriter.write("Ethan");
maleWriter.close();
BufferedWriter femaleWriter = new BufferedWriter(new FileWriter(femaleFile));
femaleWriter.write("Anna");
femaleWriter.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("Ethan");
List<CoreLabel> mentionTokens = new ArrayList<>();
mentionTokens.add(token);
Annotation mention = new Annotation("Ethan");
mention.set(CoreAnnotations.TokensAnnotation.class, mentionTokens);
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new Annotation("Ethan jumped.");
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
sentence.set(CoreAnnotations.TokensAnnotation.class, null);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation document = new Annotation("Ethan jumped.");
document.set(CoreAnnotations.SentencesAnnotation.class, sentences);
try {
annotator.annotate(document);
fail("Expected NullPointerException due to TokensAnnotation being null");
} catch (NullPointerException expected) {
}
}

@Test
public void testReAnnotateSameDocument() throws IOException {
File maleFile = File.createTempFile("male_repeat", ".txt");
File femaleFile = File.createTempFile("female_repeat", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
BufferedWriter maleWriter = new BufferedWriter(new FileWriter(maleFile));
maleWriter.write("Leo");
maleWriter.close();
BufferedWriter femaleWriter = new BufferedWriter(new FileWriter(femaleFile));
femaleWriter.write("Emma");
femaleWriter.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("Leo");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
Annotation mention = new Annotation("Leo");
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new Annotation("Leo sits.");
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation document = new Annotation("Leo sits.");
document.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(document);
assertEquals("MALE", mention.get(CoreAnnotations.GenderAnnotation.class));
annotator.annotate(document);
assertEquals("MALE", mention.get(CoreAnnotations.GenderAnnotation.class));
}
}
