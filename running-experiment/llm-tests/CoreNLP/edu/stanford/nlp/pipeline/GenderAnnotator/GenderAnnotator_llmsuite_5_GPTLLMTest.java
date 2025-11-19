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
import java.util.*;
import static org.junit.Assert.*;

public class GenderAnnotator_llmsuite_5_GPTLLMTest {

@Test
public void testAnnotateWithMaleName() throws Exception {
File maleFile = File.createTempFile("male", ".txt");
File femaleFile = File.createTempFile("female", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
PrintWriter maleWriter = new PrintWriter(new FileWriter(maleFile));
maleWriter.println("John");
maleWriter.close();
PrintWriter femaleWriter = new PrintWriter(new FileWriter(femaleFile));
femaleWriter.println("Mary");
femaleWriter.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator genderAnnotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("John");
token.set(CoreAnnotations.ValueAnnotation.class, "John");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentenceList = new ArrayList<>();
sentenceList.add(sentence);
Annotation annotation = new Annotation("Sample text");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);
genderAnnotator.annotate(annotation);
CoreMap resultMention = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(CoreAnnotations.MentionsAnnotation.class).get(0);
assertEquals("MALE", resultMention.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("MALE", resultMention.get(CoreAnnotations.TokensAnnotation.class).get(0).get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testAnnotateWithFemaleName() throws Exception {
File maleFile = File.createTempFile("male", ".txt");
File femaleFile = File.createTempFile("female", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
PrintWriter maleWriter = new PrintWriter(new FileWriter(maleFile));
maleWriter.println("John");
maleWriter.close();
PrintWriter femaleWriter = new PrintWriter(new FileWriter(femaleFile));
femaleWriter.println("Alice");
femaleWriter.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator genderAnnotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("Alice");
token.set(CoreAnnotations.ValueAnnotation.class, "Alice");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentenceList = new ArrayList<>();
sentenceList.add(sentence);
Annotation annotation = new Annotation("Sample text");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);
genderAnnotator.annotate(annotation);
CoreMap resultMention = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(CoreAnnotations.MentionsAnnotation.class).get(0);
assertEquals("FEMALE", resultMention.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("FEMALE", resultMention.get(CoreAnnotations.TokensAnnotation.class).get(0).get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testAnnotateWithUnknownName() throws Exception {
File maleFile = File.createTempFile("male", ".txt");
File femaleFile = File.createTempFile("female", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
PrintWriter maleWriter = new PrintWriter(new FileWriter(maleFile));
maleWriter.println("John");
maleWriter.close();
PrintWriter femaleWriter = new PrintWriter(new FileWriter(femaleFile));
femaleWriter.println("Mary");
femaleWriter.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator genderAnnotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("Zorblax");
token.set(CoreAnnotations.ValueAnnotation.class, "Zorblax");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentenceList = new ArrayList<>();
sentenceList.add(sentence);
Annotation annotation = new Annotation("Input text");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);
genderAnnotator.annotate(annotation);
CoreMap resultMention = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(CoreAnnotations.MentionsAnnotation.class).get(0);
assertNull(resultMention.get(CoreAnnotations.GenderAnnotation.class));
assertNull(resultMention.get(CoreAnnotations.TokensAnnotation.class).get(0).get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testAnnotateWithNonPersonEntity() throws Exception {
File maleFile = File.createTempFile("male", ".txt");
File femaleFile = File.createTempFile("female", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
PrintWriter maleWriter = new PrintWriter(new FileWriter(maleFile));
maleWriter.println("John");
maleWriter.close();
PrintWriter femaleWriter = new PrintWriter(new FileWriter(femaleFile));
femaleWriter.println("Mary");
femaleWriter.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator genderAnnotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("Tesla");
token.set(CoreAnnotations.ValueAnnotation.class, "Tesla");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "ORGANIZATION");
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentenceList = new ArrayList<>();
sentenceList.add(sentence);
Annotation annotation = new Annotation("Org text");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);
genderAnnotator.annotate(annotation);
CoreMap resultMention = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(CoreAnnotations.MentionsAnnotation.class).get(0);
assertNull(resultMention.get(CoreAnnotations.GenderAnnotation.class));
assertNull(resultMention.get(CoreAnnotations.TokensAnnotation.class).get(0).get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testAnnotateWithEmptyTokenList() throws Exception {
File maleFile = File.createTempFile("male", ".txt");
File femaleFile = File.createTempFile("female", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
PrintWriter maleWriter = new PrintWriter(new FileWriter(maleFile));
maleWriter.println("Bob");
maleWriter.close();
PrintWriter femaleWriter = new PrintWriter(new FileWriter(femaleFile));
femaleWriter.println("Alice");
femaleWriter.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator genderAnnotator = new GenderAnnotator("gender", props);
List<CoreLabel> tokens = new ArrayList<>();
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentenceList = new ArrayList<>();
sentenceList.add(sentence);
Annotation annotation = new Annotation("Empty token test");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);
try {
genderAnnotator.annotate(annotation);
} catch (IndexOutOfBoundsException e) {
fail("Annotator should handle empty token list safely");
}
}

@Test
public void testAnnotateWithNullEntityType() throws Exception {
File maleFile = File.createTempFile("male", ".txt");
File femaleFile = File.createTempFile("female", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
PrintWriter maleWriter = new PrintWriter(new FileWriter(maleFile));
maleWriter.println("John");
maleWriter.close();
PrintWriter femaleWriter = new PrintWriter(new FileWriter(femaleFile));
femaleWriter.println("Jane");
femaleWriter.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator genderAnnotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("John");
token.set(CoreAnnotations.ValueAnnotation.class, "John");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("Some text");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
try {
genderAnnotator.annotate(annotation);
fail("Expected NullPointerException due to missing EntityTypeAnnotation");
} catch (NullPointerException e) {
}
}

@Test
public void testAnnotateWithMultipleTokensInMention() throws Exception {
File maleFile = File.createTempFile("male", ".txt");
File femaleFile = File.createTempFile("female", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
PrintWriter maleWriter = new PrintWriter(new FileWriter(maleFile));
maleWriter.println("Robert");
maleWriter.close();
PrintWriter femaleWriter = new PrintWriter(new FileWriter(femaleFile));
femaleWriter.println("Alice");
femaleWriter.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator genderAnnotator = new GenderAnnotator("gender", props);
CoreLabel firstToken = new CoreLabel();
firstToken.setWord("Robert");
firstToken.set(CoreAnnotations.ValueAnnotation.class, "Robert");
CoreLabel secondToken = new CoreLabel();
secondToken.setWord("Smith");
secondToken.set(CoreAnnotations.ValueAnnotation.class, "Smith");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(firstToken);
tokens.add(secondToken);
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentenceList = new ArrayList<>();
sentenceList.add(sentence);
Annotation annotation = new Annotation("Full name test");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);
genderAnnotator.annotate(annotation);
assertEquals("MALE", mention.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("MALE", tokens.get(0).get(CoreAnnotations.GenderAnnotation.class));
assertEquals("MALE", tokens.get(1).get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testAnnotateWithEmptyMentionListInSentence() throws Exception {
File maleFile = File.createTempFile("male", ".txt");
File femaleFile = File.createTempFile("female", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
new PrintWriter(maleFile).close();
new PrintWriter(femaleFile).close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator genderAnnotator = new GenderAnnotator("gender", props);
List<CoreMap> mentions = new ArrayList<>();
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("Sentence with no mentions");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
try {
genderAnnotator.annotate(annotation);
} catch (Exception e) {
fail("Annotator should handle empty mention list without exception");
}
}

@Test
public void testAnnotateWithNoSentencesAnnotation() throws Exception {
File maleFile = File.createTempFile("male", ".txt");
File femaleFile = File.createTempFile("female", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
PrintWriter maleWriter = new PrintWriter(new FileWriter(maleFile));
maleWriter.println("John");
maleWriter.close();
PrintWriter femaleWriter = new PrintWriter(new FileWriter(femaleFile));
femaleWriter.println("Mary");
femaleWriter.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator genderAnnotator = new GenderAnnotator("gender", props);
Annotation annotation = new Annotation("Text without sentences");
try {
genderAnnotator.annotate(annotation);
fail("Expected NullPointerException due to missing SentencesAnnotation");
} catch (NullPointerException e) {
}
}

@Test
public void testLoadGenderNamesWithMalformedCSVLine() throws Exception {
File maleFile = File.createTempFile("male", ".txt");
File femaleFile = File.createTempFile("female", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
PrintWriter maleWriter = new PrintWriter(new FileWriter(maleFile));
maleWriter.println("Bob");
maleWriter.println(",,Smith");
maleWriter.close();
PrintWriter femaleWriter = new PrintWriter(new FileWriter(femaleFile));
femaleWriter.println(",");
femaleWriter.println("Sally");
femaleWriter.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator genderAnnotator = new GenderAnnotator("gender", props);
assertTrue(genderAnnotator.maleNames.contains("bob"));
assertTrue(genderAnnotator.maleNames.contains("smith"));
assertTrue(genderAnnotator.femaleNames.contains("sally"));
assertTrue(genderAnnotator.femaleNames.contains(""));
}

@Test
public void testRequirementAPIs() throws Exception {
File maleFile = File.createTempFile("male", ".txt");
File femaleFile = File.createTempFile("female", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
new PrintWriter(maleFile).close();
new PrintWriter(femaleFile).close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator genderAnnotator = new GenderAnnotator("gender", props);
Set<Class<? extends CoreAnnotation>> required = genderAnnotator.requires();
assertNotNull(required);
assertTrue(required.contains(CoreAnnotations.TextAnnotation.class));
assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
assertTrue(required.contains(CoreAnnotations.MentionsAnnotation.class));
assertTrue(required.contains(CoreAnnotations.EntityTypeAnnotation.class));
Set<Class<? extends CoreAnnotation>> satisfied = genderAnnotator.requirementsSatisfied();
assertNotNull(satisfied);
assertTrue(satisfied.contains(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testAnnotateWithWhitespaceNameInFile() throws Exception {
File maleFile = File.createTempFile("maleNames", ".txt");
File femaleFile = File.createTempFile("femaleNames", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
PrintWriter maleWriter = new PrintWriter(new FileWriter(maleFile));
maleWriter.println("   ");
maleWriter.println("Bob");
maleWriter.close();
PrintWriter femaleWriter = new PrintWriter(new FileWriter(femaleFile));
femaleWriter.println("Alice");
femaleWriter.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator genderAnnotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord(" ");
token.set(CoreAnnotations.ValueAnnotation.class, " ");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentenceList = new ArrayList<>();
sentenceList.add(sentence);
Annotation annotation = new Annotation("Whitespace token test");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);
genderAnnotator.annotate(annotation);
assertNull(mention.get(CoreAnnotations.GenderAnnotation.class));
assertNull(tokens.get(0).get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testCaseSensitivityOnMixedCaseName() throws Exception {
File maleFile = File.createTempFile("male", ".txt");
File femaleFile = File.createTempFile("female", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
PrintWriter maleWriter = new PrintWriter(new FileWriter(maleFile));
maleWriter.println("bob");
maleWriter.close();
PrintWriter femaleWriter = new PrintWriter(new FileWriter(femaleFile));
femaleWriter.println("ALICE");
femaleWriter.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("ALICE");
token.set(CoreAnnotations.ValueAnnotation.class, "ALICE");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentenceList = new ArrayList<>();
sentenceList.add(sentence);
Annotation annotation = new Annotation("Case sensitivity test");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);
annotator.annotate(annotation);
assertEquals("FEMALE", mention.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("FEMALE", token.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testEmptyGenderFilesDoNotCrash() throws Exception {
File maleFile = File.createTempFile("mfile", ".txt");
File femaleFile = File.createTempFile("ffile", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
new PrintWriter(maleFile).close();
new PrintWriter(femaleFile).close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("UnknownName");
token.set(CoreAnnotations.ValueAnnotation.class, "UnknownName");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentenceList = new ArrayList<>();
sentenceList.add(sentence);
Annotation annotation = new Annotation("Empty file test");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);
annotator.annotate(annotation);
assertNull(mention.get(CoreAnnotations.GenderAnnotation.class));
assertNull(token.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testFirstTokenIsNullInMention() throws Exception {
File maleFile = File.createTempFile("maleSet", ".txt");
File femaleFile = File.createTempFile("femaleSet", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
PrintWriter maleWriter = new PrintWriter(new FileWriter(maleFile));
maleWriter.println("Bob");
maleWriter.close();
PrintWriter femaleWriter = new PrintWriter(new FileWriter(femaleFile));
femaleWriter.println("Alice");
femaleWriter.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(null);
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentenceList = new ArrayList<>();
sentenceList.add(sentence);
Annotation annotation = new Annotation("Null token test");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);
try {
annotator.annotate(annotation);
fail("Expected NullPointerException due to null first token");
} catch (NullPointerException e) {
}
}

@Test
public void testMentionWithoutTokensAnnotation() throws Exception {
File maleFile = File.createTempFile("mset", ".txt");
File femaleFile = File.createTempFile("fset", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
PrintWriter writer = new PrintWriter(new FileWriter(maleFile));
writer.println("Jim");
writer.close();
writer = new PrintWriter(new FileWriter(femaleFile));
writer.println("Emily");
writer.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentenceList = new ArrayList<>();
sentenceList.add(sentence);
Annotation annotation = new Annotation("Missing tokens test");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);
try {
annotator.annotate(annotation);
fail("Expected NullPointerException due to missing TokensAnnotation");
} catch (NullPointerException e) {
}
}

@Test
public void testNameWithLeadingAndTrailingWhitespaceInGenderFile() throws Exception {
File maleFile = File.createTempFile("male", ".txt");
File femaleFile = File.createTempFile("female", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
PrintWriter maleWriter = new PrintWriter(maleFile);
maleWriter.println("   Charles   ");
maleWriter.close();
PrintWriter femaleWriter = new PrintWriter(femaleFile);
femaleWriter.println("Elizabeth");
femaleWriter.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("Charles");
token.set(CoreAnnotations.ValueAnnotation.class, "Charles");
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
Annotation annotation = new Annotation("Name with extra whitespace");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(annotation);
assertEquals("MALE", mention.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("MALE", tokens.get(0).get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testAnnotatorWithInvalidCSVLineContainingOnlyCommas() throws Exception {
File maleFile = File.createTempFile("mfile", ".txt");
File femaleFile = File.createTempFile("ffile", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
PrintWriter maleWriter = new PrintWriter(maleFile);
maleWriter.println("Bob");
maleWriter.println(",,,");
maleWriter.close();
PrintWriter femaleWriter = new PrintWriter(femaleFile);
femaleWriter.println("Alice");
femaleWriter.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
assertTrue(annotator.maleNames.contains("bob"));
assertTrue(annotator.maleNames.contains(""));
}

@Test
public void testMultipleMentionsWithMixedEntityTypes() throws Exception {
File maleFile = File.createTempFile("m1", ".txt");
File femaleFile = File.createTempFile("f1", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
PrintWriter maleWriter = new PrintWriter(maleFile);
maleWriter.println("Tom");
maleWriter.close();
PrintWriter femaleWriter = new PrintWriter(femaleFile);
femaleWriter.println("Emma");
femaleWriter.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token1 = new CoreLabel();
token1.setWord("Tom");
token1.set(CoreAnnotations.ValueAnnotation.class, "Tom");
CoreMap mention1 = new ArrayCoreMap();
mention1.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
mention1.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token1));
CoreLabel token2 = new CoreLabel();
token2.setWord("Emma");
token2.set(CoreAnnotations.ValueAnnotation.class, "Emma");
CoreMap mention2 = new ArrayCoreMap();
mention2.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
mention2.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token2));
CoreLabel token3 = new CoreLabel();
token3.setWord("Google");
token3.set(CoreAnnotations.ValueAnnotation.class, "Google");
CoreMap mention3 = new ArrayCoreMap();
mention3.set(CoreAnnotations.EntityTypeAnnotation.class, "ORGANIZATION");
mention3.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token3));
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention1);
mentions.add(mention2);
mentions.add(mention3);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("Tom Emma Google test");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(annotation);
assertEquals("MALE", mention1.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("FEMALE", mention2.get(CoreAnnotations.GenderAnnotation.class));
assertNull(mention3.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testAnnotateWithOnlyCommasInNameLine() throws Exception {
File maleFile = File.createTempFile("mx", ".txt");
File femaleFile = File.createTempFile("fx", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
PrintWriter writer = new PrintWriter(new FileWriter(maleFile));
writer.println(",,");
writer.println("Jim");
writer.close();
writer = new PrintWriter(new FileWriter(femaleFile));
writer.println("Sue");
writer.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
assertTrue(annotator.maleNames.contains("jim"));
assertTrue(annotator.maleNames.contains(""));
}

@Test
public void testAnnotatorWithNullTokensAnnotationKey() throws Exception {
File maleFile = File.createTempFile("mNull", ".txt");
File femaleFile = File.createTempFile("fNull", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
PrintWriter writer = new PrintWriter(new FileWriter(maleFile));
writer.println("Mark");
writer.close();
writer = new PrintWriter(new FileWriter(femaleFile));
writer.println("Grace");
writer.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("Missing tokens test");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
try {
annotator.annotate(annotation);
fail("Expected NullPointerException due to missing TokensAnnotation");
} catch (NullPointerException e) {
}
}

@Test
public void testMentionsAnnotationKeyIsMissingFromSentence() throws Exception {
File maleFile = File.createTempFile("mNullMentions", ".txt");
File femaleFile = File.createTempFile("fNullMentions", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
new PrintWriter(maleFile).close();
new PrintWriter(femaleFile).close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreMap sentence = new ArrayCoreMap();
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("Missing mentions key");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
try {
annotator.annotate(annotation);
fail("Expected NullPointerException due to missing MentionsAnnotation in sentence");
} catch (NullPointerException e) {
}
}

@Test
public void testDuplicateNamesInGenderFile() throws Exception {
File maleFile = File.createTempFile("dupMale", ".txt");
File femaleFile = File.createTempFile("dupFemale", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
PrintWriter maleWriter = new PrintWriter(maleFile);
maleWriter.println("Leo,leo,LEO");
maleWriter.close();
PrintWriter femaleWriter = new PrintWriter(femaleFile);
femaleWriter.println("Anna");
femaleWriter.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
assertEquals(1, annotator.maleNames.size());
assertTrue(annotator.maleNames.contains("leo"));
}

@Test
public void testAnnotatedNameWrappedInQuotes() throws Exception {
File maleFile = File.createTempFile("quoteTestMale", ".txt");
File femaleFile = File.createTempFile("quoteTestFemale", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
PrintWriter writer = new PrintWriter(maleFile);
writer.println("\"George\"");
writer.close();
new PrintWriter(femaleFile).close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("\"George\"");
token.set(CoreAnnotations.ValueAnnotation.class, "\"George\"");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentenceList = new ArrayList<>();
sentenceList.add(sentence);
Annotation annotation = new Annotation("Quote test");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);
annotator.annotate(annotation);
assertEquals("MALE", mention.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("MALE", tokens.get(0).get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testMentionWithNullTokensValue() throws Exception {
File maleFile = File.createTempFile("maleNullTokens", ".txt");
File femaleFile = File.createTempFile("femaleNullTokens", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
new PrintWriter(maleFile).close();
new PrintWriter(femaleFile).close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
mention.set(CoreAnnotations.TokensAnnotation.class, null);
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("Null tokens value");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
try {
annotator.annotate(annotation);
fail("Expected NullPointerException due to null TokensAnnotation value in mention");
} catch (NullPointerException e) {
}
}

@Test
public void testEmptyAnnotationObjectHasNoEffect() throws Exception {
File maleFile = File.createTempFile("emptyMale", ".txt");
File femaleFile = File.createTempFile("emptyFemale", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
new PrintWriter(maleFile).close();
new PrintWriter(femaleFile).close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
Annotation annotation = new Annotation("");
try {
annotator.annotate(annotation);
fail("Expected NullPointerException for missing SentencesAnnotation");
} catch (NullPointerException e) {
}
}

@Test
public void testSentenceWithNullMentionObject() throws Exception {
File maleFile = File.createTempFile("maleNullMention", ".txt");
File femaleFile = File.createTempFile("femaleNullMention", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
new PrintWriter(maleFile).close();
new PrintWriter(femaleFile).close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
List<CoreMap> mentions = new ArrayList<>();
mentions.add(null);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("Sentence with null mention");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
try {
annotator.annotate(annotation);
fail("Expected NullPointerException due to null entity mention");
} catch (NullPointerException e) {
}
}

@Test
public void testTokenWithNullWordField() throws Exception {
File maleFile = File.createTempFile("nullWordMale", ".txt");
File femaleFile = File.createTempFile("nullWordFemale", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
PrintWriter mw = new PrintWriter(maleFile);
mw.println("David");
mw.close();
PrintWriter fw = new PrintWriter(femaleFile);
fw.println("Rose");
fw.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord(null);
token.set(CoreAnnotations.ValueAnnotation.class, null);
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
Annotation annotation = new Annotation("Token with null word");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
try {
annotator.annotate(annotation);
fail("Expected NullPointerException due to null token word");
} catch (NullPointerException e) {
}
}

@Test
public void testTokenWordIsEmptyString() throws Exception {
File maleFile = File.createTempFile("maleEmptyToken", ".txt");
File femaleFile = File.createTempFile("femaleEmptyToken", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
PrintWriter maleOut = new PrintWriter(new FileWriter(maleFile));
maleOut.println("Tom");
maleOut.close();
PrintWriter femaleOut = new PrintWriter(new FileWriter(femaleFile));
femaleOut.println("Diana");
femaleOut.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("");
token.set(CoreAnnotations.ValueAnnotation.class, "");
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
Annotation annotation = new Annotation("Empty token word");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(annotation);
assertNull(mention.get(CoreAnnotations.GenderAnnotation.class));
assertNull(token.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testGenderFileContainsOnlyCommaSymbols() throws Exception {
File maleFile = File.createTempFile("onlyCommasMale", ".txt");
File femaleFile = File.createTempFile("onlyCommasFemale", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
PrintWriter mw = new PrintWriter(new FileWriter(maleFile));
mw.println(",,,");
mw.println("Greg");
mw.close();
PrintWriter fw = new PrintWriter(new FileWriter(femaleFile));
fw.println("Lisa,,");
fw.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
assertTrue(annotator.maleNames.contains(""));
assertTrue(annotator.femaleNames.contains(""));
assertTrue(annotator.maleNames.contains("greg"));
assertTrue(annotator.femaleNames.contains("lisa"));
}

@Test
public void testMultipleEmptyLinesInGenderNameFiles() throws Exception {
File maleFile = File.createTempFile("emptyLinesMale", ".txt");
File femaleFile = File.createTempFile("emptyLinesFemale", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
PrintWriter mw = new PrintWriter(maleFile);
mw.println();
mw.println("John");
mw.println();
mw.close();
PrintWriter fw = new PrintWriter(femaleFile);
fw.println("Mary");
fw.println("");
fw.println();
fw.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
assertTrue(annotator.maleNames.contains("john"));
assertTrue(annotator.maleNames.contains(""));
assertTrue(annotator.femaleNames.contains("mary"));
assertTrue(annotator.femaleNames.contains(""));
}

@Test
public void testSentenceListIsEmpty() throws Exception {
File maleFile = File.createTempFile("maleEmptySent", ".txt");
File femaleFile = File.createTempFile("femaleEmptySent", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
new PrintWriter(maleFile).close();
new PrintWriter(femaleFile).close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
Annotation annotation = new Annotation("Text");
List<CoreMap> sentences = new ArrayList<>();
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(annotation);
}

@Test
public void testMentionWithEmptyTokenList() throws Exception {
File maleFile = File.createTempFile("maleEmptyTokens", ".txt");
File femaleFile = File.createTempFile("femaleEmptyTokens", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
new PrintWriter(maleFile).close();
new PrintWriter(femaleFile).close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
List<CoreLabel> tokens = new ArrayList<>();
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("Empty token mention test");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
try {
annotator.annotate(annotation);
fail("Expected IndexOutOfBoundsException for empty token list");
} catch (IndexOutOfBoundsException e) {
}
}

@Test
public void testMentionWithWhitespaceTokenName() throws Exception {
File maleFile = File.createTempFile("whitespaceMale", ".txt");
File femaleFile = File.createTempFile("whitespaceFemale", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
PrintWriter mw = new PrintWriter(maleFile);
mw.println("Bob");
mw.close();
PrintWriter fw = new PrintWriter(femaleFile);
fw.println("Alice");
fw.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("   ");
token.set(CoreAnnotations.ValueAnnotation.class, "   ");
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
Annotation annotation = new Annotation("Whitespace name test");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(annotation);
assertNull(mention.get(CoreAnnotations.GenderAnnotation.class));
assertNull(token.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testMultipleValidNamesInSingleCSVLine() throws Exception {
File maleFile = File.createTempFile("multiMale", ".txt");
File femaleFile = File.createTempFile("multiFemale", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
PrintWriter mw = new PrintWriter(maleFile);
mw.println("Sam,Andrew,Jack");
mw.close();
new PrintWriter(femaleFile).close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
assertEquals(3, annotator.maleNames.size());
assertTrue(annotator.maleNames.contains("sam"));
assertTrue(annotator.maleNames.contains("andrew"));
assertTrue(annotator.maleNames.contains("jack"));
}

@Test
public void testSingleNameCommaThenEmptyAfterSplit() throws Exception {
File maleFile = File.createTempFile("maleCommaEmpty", ".txt");
File femaleFile = File.createTempFile("femaleCommaEmpty", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
PrintWriter pw = new PrintWriter(maleFile);
pw.println("Jake,");
pw.close();
new PrintWriter(femaleFile).close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
assertTrue(annotator.maleNames.contains("jake"));
assertTrue(annotator.maleNames.contains(""));
}

@Test
public void testMentionWithNullEntityTypeFailsSafely() throws Exception {
File maleFile = File.createTempFile("mnullentity", ".txt");
File femaleFile = File.createTempFile("fnullentity", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
new PrintWriter(maleFile).close();
new PrintWriter(femaleFile).close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("John");
token.set(CoreAnnotations.ValueAnnotation.class, "John");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("Null entity type");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
try {
annotator.annotate(annotation);
fail("Expected NullPointerException for missing EntityTypeAnnotation");
} catch (NullPointerException e) {
}
}

@Test
public void testFirstNameNotAtFirstToken() throws Exception {
File maleFile = File.createTempFile("mpos", ".txt");
File femaleFile = File.createTempFile("fpos", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
PrintWriter mw = new PrintWriter(maleFile);
mw.println("Robert");
mw.close();
PrintWriter fw = new PrintWriter(femaleFile);
fw.println("Anna");
fw.close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token1 = new CoreLabel();
token1.setWord("Dr.");
token1.set(CoreAnnotations.ValueAnnotation.class, "Dr.");
CoreLabel token2 = new CoreLabel();
token2.setWord("Robert");
token2.set(CoreAnnotations.ValueAnnotation.class, "Robert");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token1);
tokens.add(token2);
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentenceList = new ArrayList<>();
sentenceList.add(sentence);
Annotation annotation = new Annotation("First name not first token test");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);
annotator.annotate(annotation);
assertNull(mention.get(CoreAnnotations.GenderAnnotation.class));
assertNull(token1.get(CoreAnnotations.GenderAnnotation.class));
assertNull(token2.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testNoEntityMentionsInSentence() throws Exception {
File maleFile = File.createTempFile("mnomentions", ".txt");
File femaleFile = File.createTempFile("fnomentions", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
new PrintWriter(maleFile).close();
new PrintWriter(femaleFile).close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("Sentence with no entity mentions");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(annotation);
}

@Test
public void testMentionListContainsOnlyNullEntries() throws Exception {
File maleFile = File.createTempFile("nullmentions", ".txt");
File femaleFile = File.createTempFile("nullmentionsf", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
new PrintWriter(maleFile).close();
new PrintWriter(femaleFile).close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
List<CoreMap> mentions = new ArrayList<>();
mentions.add(null);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("Null mentions test");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
try {
annotator.annotate(annotation);
fail("Expected NullPointerException when trying to process null mention");
} catch (NullPointerException e) {
}
}

@Test
public void testTokensAnnotationPresentAsEmptyList() throws Exception {
File maleFile = File.createTempFile("emptytoklistmale", ".txt");
File femaleFile = File.createTempFile("emptytoklistfem", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
new PrintWriter(maleFile).close();
new PrintWriter(femaleFile).close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
mention.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
Annotation annotation = new Annotation("Empty TokensAnnotation List");
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
try {
annotator.annotate(annotation);
fail("Expected IndexOutOfBoundsException accessing first token");
} catch (IndexOutOfBoundsException e) {
}
}

@Test
public void testNullTokenObjectInTokenList() throws Exception {
File maleFile = File.createTempFile("nulltokmale", ".txt");
File femaleFile = File.createTempFile("nulltokfemale", ".txt");
maleFile.deleteOnExit();
femaleFile.deleteOnExit();
PrintWriter pw = new PrintWriter(maleFile);
pw.println("Lucas");
pw.close();
new PrintWriter(femaleFile).close();
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", maleFile.getAbsolutePath());
props.setProperty("gender.femaleNamesFile", femaleFile.getAbsolutePath());
GenderAnnotator annotator = new GenderAnnotator("gender", props);
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(null);
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, Arrays.asList(mention));
Annotation annotation = new Annotation("Null token inside list");
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
try {
annotator.annotate(annotation);
fail("Expected NullPointerException from null token access");
} catch (NullPointerException e) {
}
}
}
