package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class GenderAnnotator_llmsuite_1_GPTLLMTest {

@Test
public void testAnnotateMalePerson() {
Properties props = new Properties();
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("John");
ArrayList<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
ArrayList<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
ArrayList<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("Test text");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.maleNames.clear();
annotator.femaleNames.clear();
annotator.maleNames.add("john");
annotator.annotate(annotation);
assertEquals("MALE", mention.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("MALE", token.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testAnnotateFemalePerson() {
Properties props = new Properties();
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("Mary");
ArrayList<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
ArrayList<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
ArrayList<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("Test text");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.maleNames.clear();
annotator.femaleNames.clear();
annotator.femaleNames.add("mary");
annotator.annotate(annotation);
assertEquals("FEMALE", mention.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("FEMALE", token.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testAnnotateUnknownPersonName() {
Properties props = new Properties();
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("Taylor");
ArrayList<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
ArrayList<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
ArrayList<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("Sample");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.maleNames.clear();
annotator.femaleNames.clear();
annotator.annotate(annotation);
assertNull(mention.get(CoreAnnotations.GenderAnnotation.class));
assertNull(token.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testNonPersonEntityType() {
Properties props = new Properties();
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("Stanford");
ArrayList<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "LOCATION");
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
ArrayList<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
ArrayList<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("Place Test");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.maleNames.clear();
annotator.femaleNames.clear();
annotator.maleNames.add("stanford");
annotator.annotate(annotation);
assertNull(mention.get(CoreAnnotations.GenderAnnotation.class));
assertNull(token.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testCaseInsensitiveNameMatch() {
Properties props = new Properties();
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("mArY");
ArrayList<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
ArrayList<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
ArrayList<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("Case Test");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.femaleNames.clear();
annotator.femaleNames.add("mary");
annotator.annotate(annotation);
assertEquals("FEMALE", mention.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("FEMALE", token.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testRequirementsSatisfied() {
Properties props = new Properties();
GenderAnnotator annotator = new GenderAnnotator("gender", props);
Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
assertEquals(1, satisfied.size());
assertTrue(satisfied.contains(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testRequiredAnnotations() {
Properties props = new Properties();
GenderAnnotator annotator = new GenderAnnotator("gender", props);
Set<Class<? extends CoreAnnotation>> required = annotator.requires();
assertNotNull(required);
assertTrue(required.contains(CoreAnnotations.TextAnnotation.class));
assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
assertTrue(required.contains(CoreAnnotations.MentionsAnnotation.class));
assertTrue(required.contains(CoreAnnotations.EntityTypeAnnotation.class));
assertEquals(5, required.size());
}

@Test
public void testEmptySentencesAnnotation() {
Properties props = new Properties();
GenderAnnotator annotator = new GenderAnnotator("gender", props);
ArrayList<CoreMap> sentences = new ArrayList<>();
Annotation annotation = new Annotation("Empty test");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(annotation);
assertTrue(annotation.get(CoreAnnotations.SentencesAnnotation.class).isEmpty());
}

@Test
public void testAnnotateEntityMentionSetsGender() {
Properties props = new Properties();
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("Name");
ArrayList<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
annotator.annotateEntityMention(mention, "MALE");
assertEquals("MALE", mention.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("MALE", token.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testNullEntityTypeDoesNotThrow() {
Properties props = new Properties();
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("Unknown");
ArrayList<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
ArrayList<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
ArrayList<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("Null entity type");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
try {
annotator.annotate(annotation);
} catch (Exception e) {
fail("Exception was thrown for null entity type: " + e.getMessage());
}
}

@Test
public void testLowerCaseNameInTokenStillMatchesMaleList() {
Properties props = new Properties();
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("JOHN");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
annotator.maleNames.clear();
annotator.maleNames.add("john");
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("Caps test");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(annotation);
assertEquals("MALE", mention.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("MALE", token.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testEntityMentionWithEmptyTokensList() {
Properties props = new Properties();
GenderAnnotator annotator = new GenderAnnotator("gender", props);
List<CoreLabel> emptyTokens = new ArrayList<>();
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
mention.set(CoreAnnotations.TokensAnnotation.class, emptyTokens);
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("Empty tokens");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
try {
annotator.annotate(annotation);
} catch (IndexOutOfBoundsException e) {
return;
}
fail("Expected IndexOutOfBoundsException for empty token list");
}

@Test
public void testEntityMentionWithNullTokensAnnotation() {
Properties props = new Properties();
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("Null tokens");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
try {
annotator.annotate(annotation);
} catch (NullPointerException e) {
return;
}
fail("Expected NullPointerException due to missing TokensAnnotation");
}

@Test
public void testEntityMentionWithMultipleTokensUsesOnlyFirstForGender() {
Properties props = new Properties();
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token1 = new CoreLabel();
token1.setWord("Mary");
CoreLabel token2 = new CoreLabel();
token2.setWord("Smith");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token1);
tokens.add(token2);
annotator.femaleNames.clear();
annotator.femaleNames.add("mary");
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("Multiple tokens");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(annotation);
assertEquals("FEMALE", mention.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("FEMALE", token1.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("FEMALE", token2.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testMultipleSentencesAreProcessed() {
Properties props = new Properties();
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token1 = new CoreLabel();
token1.setWord("John");
CoreMap mention1 = new ArrayCoreMap();
List<CoreLabel> tokens1 = new ArrayList<>();
tokens1.add(token1);
mention1.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
mention1.set(CoreAnnotations.TokensAnnotation.class, tokens1);
CoreMap sentence1 = new ArrayCoreMap();
List<CoreMap> mentions1 = new ArrayList<>();
mentions1.add(mention1);
sentence1.set(CoreAnnotations.MentionsAnnotation.class, mentions1);
CoreLabel token2 = new CoreLabel();
token2.setWord("Mary");
CoreMap mention2 = new ArrayCoreMap();
List<CoreLabel> tokens2 = new ArrayList<>();
tokens2.add(token2);
mention2.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
mention2.set(CoreAnnotations.TokensAnnotation.class, tokens2);
CoreMap sentence2 = new ArrayCoreMap();
List<CoreMap> mentions2 = new ArrayList<>();
mentions2.add(mention2);
sentence2.set(CoreAnnotations.MentionsAnnotation.class, mentions2);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence1);
sentences.add(sentence2);
Annotation annotation = new Annotation("Multi-sentence text");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.maleNames.clear();
annotator.femaleNames.clear();
annotator.maleNames.add("john");
annotator.femaleNames.add("mary");
annotator.annotate(annotation);
assertEquals("MALE", mention1.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("FEMALE", mention2.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testNameInBothMaleAndFemaleListsPrefersMale() {
Properties props = new Properties();
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("Sam");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
annotator.maleNames.clear();
annotator.femaleNames.clear();
annotator.maleNames.add("sam");
annotator.femaleNames.add("sam");
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("Ambiguous name Sam");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(annotation);
assertEquals("MALE", mention.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testMentionWithoutEntityTypeAnnotation() {
Properties props = new Properties();
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("John");
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
Annotation annotation = new Annotation("No entity type");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
try {
annotator.annotate(annotation);
} catch (Exception e) {
return;
}
fail("Expected exception due to null EntityTypeAnnotation");
}

@Test
public void testMentionWithNonPersonEntityTypeLowercase() {
Properties props = new Properties();
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("Mary");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "location");
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("Lowercase entity type");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.femaleNames.clear();
annotator.femaleNames.add("mary");
annotator.annotate(annotation);
assertNull(mention.get(CoreAnnotations.GenderAnnotation.class));
assertNull(token.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testEmptyMentionsListInSentence() {
Properties props = new Properties();
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreMap sentence = new ArrayCoreMap();
List<CoreMap> emptyMentions = new ArrayList<>();
sentence.set(CoreAnnotations.MentionsAnnotation.class, emptyMentions);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("No mentions test");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(annotation);
assertEquals(0, annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(CoreAnnotations.MentionsAnnotation.class).size());
}

@Test
public void testSentenceWithNullMentionsAnnotation() {
Properties props = new Properties();
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreMap sentence = new ArrayCoreMap();
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("Missing Mentions");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
try {
annotator.annotate(annotation);
} catch (NullPointerException e) {
return;
}
fail("Expected NullPointerException due to missing MentionsAnnotation");
}

@Test
public void testAnnotationWithoutSentencesAnnotation() {
Properties props = new Properties();
GenderAnnotator annotator = new GenderAnnotator("gender", props);
Annotation annotation = new Annotation("Missing sentence annotation");
try {
annotator.annotate(annotation);
} catch (NullPointerException e) {
return;
}
fail("Expected NullPointerException due to missing SentencesAnnotation");
}

@Test
public void testTokensWithNullTokenElement() {
Properties props = new Properties();
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
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("Null token");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
try {
annotator.annotate(annotation);
} catch (NullPointerException e) {
return;
}
fail("Expected exception due to null token in token list");
}

@Test
public void testFirstTokenHasNullWord() {
Properties props = new Properties();
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord(null);
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
Annotation annotation = new Annotation("Null word in token");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
try {
annotator.annotate(annotation);
} catch (NullPointerException e) {
return;
}
fail("Expected exception due to null word in first token");
}

@Test
public void testMentionWithUnknownTypeStillSkipsAnnotation() {
Properties props = new Properties();
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("Alex");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "OTHER");
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("Unknown entity type");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(annotation);
assertNull(mention.get(CoreAnnotations.GenderAnnotation.class));
assertNull(token.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testFirstTokenWordIsEmptyString() {
Properties props = new Properties();
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
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
public void testFirstTokenWordIsWhitespace() {
Properties props = new Properties();
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("   ");
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
Annotation annotation = new Annotation("Whitespace name");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(annotation);
assertNull(mention.get(CoreAnnotations.GenderAnnotation.class));
assertNull(token.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testSameNameDifferentCasesStoredInGenderLists() {
Properties props = new Properties();
GenderAnnotator annotator = new GenderAnnotator("gender", props);
annotator.maleNames.clear();
annotator.maleNames.add("john");
annotator.maleNames.add("JOHN");
CoreLabel token = new CoreLabel();
token.setWord("JoHn");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("Name case variants");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(annotation);
assertEquals("MALE", mention.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("MALE", token.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testNullAnnotationPassedToAnnotateMethod() {
Properties props = new Properties();
GenderAnnotator annotator = new GenderAnnotator("gender", props);
try {
annotator.annotate(null);
} catch (NullPointerException e) {
return;
}
fail("Expected NullPointerException due to null Annotation argument");
}

@Test
public void testSentenceWithoutMentionAnnotationKeyReturnsNull() {
Properties props = new Properties();
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreMap sentence = new ArrayCoreMap();
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("No mentions key");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
try {
annotator.annotate(annotation);
} catch (NullPointerException e) {
return;
}
fail("Expected NullPointerException due to missing MentionsAnnotation in sentence");
}

@Test
public void testSentenceWithNullMentionInMentionsList() {
Properties props = new Properties();
GenderAnnotator annotator = new GenderAnnotator("gender", props);
List<CoreMap> mentions = new ArrayList<>();
mentions.add(null);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("Null mention in sentence");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
try {
annotator.annotate(annotation);
} catch (NullPointerException e) {
return;
}
fail("Expected NullPointerException on null mention in list");
}

@Test
public void testAnnotationWithEmptyStringTokenWord() {
Properties props = new Properties();
GenderAnnotator annotator = new GenderAnnotator("gender", props);
annotator.femaleNames.clear();
annotator.femaleNames.add("mary");
CoreLabel token = new CoreLabel();
token.setWord("");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
Annotation annotation = new Annotation("Empty word token");
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(annotation);
assertNull(mention.get(CoreAnnotations.GenderAnnotation.class));
assertNull(token.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testTokensAnnotationSetToNullExplicitly() {
Properties props = new Properties();
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
Annotation annotation = new Annotation("Explicit null tokens");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
try {
annotator.annotate(annotation);
} catch (NullPointerException e) {
return;
}
fail("Expected NullPointerException due to explicitly null TokensAnnotation");
}

@Test
public void testMentionTokensListWithMultipleNullTokens() {
Properties props = new Properties();
GenderAnnotator annotator = new GenderAnnotator("gender", props);
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(null);
tokens.add(null);
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("Nulls in tokens");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
try {
annotator.annotate(annotation);
} catch (NullPointerException e) {
return;
}
fail("Expected NullPointerException due to null token elements");
}

@Test
public void testMultipleMentionsShareSameTokenInstance() {
Properties props = new Properties();
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("Mary");
List<CoreLabel> tokens1 = new ArrayList<>();
tokens1.add(token);
List<CoreLabel> tokens2 = new ArrayList<>();
tokens2.add(token);
annotator.femaleNames.clear();
annotator.femaleNames.add("mary");
CoreMap mention1 = new ArrayCoreMap();
mention1.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
mention1.set(CoreAnnotations.TokensAnnotation.class, tokens1);
CoreMap mention2 = new ArrayCoreMap();
mention2.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
mention2.set(CoreAnnotations.TokensAnnotation.class, tokens2);
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention1);
mentions.add(mention2);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("Shared token");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(annotation);
assertEquals("FEMALE", mention1.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("FEMALE", mention2.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("FEMALE", token.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testNameWithLeadingAndTrailingWhitespace() {
Properties props = new Properties();
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("  John  ");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
annotator.maleNames.clear();
annotator.maleNames.add("john");
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("Whitespace name");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(annotation);
assertNull(mention.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testDuplicateMentionReferencesInSentence() {
Properties props = new Properties();
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("Mary");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
annotator.femaleNames.clear();
annotator.femaleNames.add("mary");
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
mentions.add(mention);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("Repeated mentions");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(annotation);
assertEquals("FEMALE", mention.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("FEMALE", token.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testRequiresSetIsImmutable() {
Properties props = new Properties();
GenderAnnotator annotator = new GenderAnnotator("gender", props);
Set<Class<? extends CoreAnnotation>> requirements = annotator.requires();
boolean thrown = false;
try {
requirements.clear();
} catch (UnsupportedOperationException e) {
thrown = true;
}
assertTrue("Expected unmodifiable set", thrown);
}

@Test
public void testRequirementsSatisfiedContainsOnlyGenderAnnotation() {
Properties props = new Properties();
GenderAnnotator annotator = new GenderAnnotator("gender", props);
Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
assertEquals(1, satisfied.size());
assertTrue(satisfied.contains(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testEmptyAnnotationWithoutAnyKeysSet() {
Properties props = new Properties();
GenderAnnotator annotator = new GenderAnnotator("gender", props);
Annotation annotation = new Annotation("Empty structure");
try {
annotator.annotate(annotation);
} catch (NullPointerException e) {
return;
}
fail("Expected NullPointerException due to missing SentencesAnnotation");
}

@Test
public void testEntityMentionWithMixedCasePersonLabel() {
Properties props = new Properties();
GenderAnnotator annotator = new GenderAnnotator("gender", props);
annotator.maleNames.clear();
annotator.maleNames.add("john");
CoreLabel token = new CoreLabel();
token.setWord("John");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "pErSoN");
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("Mixed case PERSON type");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(annotation);
assertNull(mention.get(CoreAnnotations.GenderAnnotation.class));
assertNull(token.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testWordMatchesNeitherGenderList() {
Properties props = new Properties();
GenderAnnotator annotator = new GenderAnnotator("gender", props);
annotator.maleNames.clear();
annotator.femaleNames.clear();
annotator.maleNames.add("john");
annotator.femaleNames.add("mary");
CoreLabel token = new CoreLabel();
token.setWord("Qwerty");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("Unknown name");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(annotation);
assertNull(mention.get(CoreAnnotations.GenderAnnotation.class));
assertNull(token.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testOneSentenceWithNullMentionsFollowedByValidSentence() {
Properties props = new Properties();
GenderAnnotator annotator = new GenderAnnotator("gender", props);
annotator.maleNames.clear();
annotator.maleNames.add("john");
CoreMap invalidSentence = new ArrayCoreMap();
CoreLabel token = new CoreLabel();
token.setWord("John");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap validSentence = new ArrayCoreMap();
validSentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(invalidSentence);
sentences.add(validSentence);
Annotation annotation = new Annotation("Mixed sentence quality");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
try {
annotator.annotate(annotation);
assertEquals("MALE", mention.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("MALE", token.get(CoreAnnotations.GenderAnnotation.class));
} catch (NullPointerException e) {
fail("Annotation failed when a valid sentence followed a null-mentions one");
}
}

@Test
public void testValidFirstNameButNonPersonEntityType() {
Properties props = new Properties();
GenderAnnotator annotator = new GenderAnnotator("gender", props);
annotator.femaleNames.clear();
annotator.femaleNames.add("mary");
CoreLabel token = new CoreLabel();
token.setWord("Mary");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "LOCATION");
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("Gendered name in non-person type");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(annotation);
assertNull(mention.get(CoreAnnotations.GenderAnnotation.class));
assertNull(token.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testEntityMentionTokensListIsMissingAndNoException() {
Properties props = new Properties();
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("Missing token list");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
try {
annotator.annotate(annotation);
} catch (Exception e) {
return;
}
fail("Expected failure due to missing TokensAnnotation in mention");
}

@Test
public void testNameContainsUnicodeAccentsNotMatched() {
Properties props = new Properties();
GenderAnnotator annotator = new GenderAnnotator("gender", props);
annotator.femaleNames.clear();
annotator.femaleNames.add("josephine");
CoreLabel token = new CoreLabel();
token.setWord("Joséphine");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("Unicode name variant");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(annotation);
assertNull(mention.get(CoreAnnotations.GenderAnnotation.class));
assertNull(token.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testMentionWithoutGenderListNameButEntityTypeIsPerson() {
Properties props = new Properties();
GenderAnnotator annotator = new GenderAnnotator("gender", props);
annotator.maleNames.clear();
annotator.femaleNames.clear();
CoreLabel token = new CoreLabel();
token.setWord("Lucas");
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
Annotation annotation = new Annotation("Name not in lists");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(annotation);
assertNull(mention.get(CoreAnnotations.GenderAnnotation.class));
assertNull(token.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testSentenceWithEmptyMentionsAnnotationValueIsNull() {
Properties props = new Properties();
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, null);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("Null mention list");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
try {
annotator.annotate(annotation);
} catch (NullPointerException e) {
return;
}
fail("Expected NullPointerException due to null MentionsAnnotation list");
}

@Test
public void testNameWithMixedCaseFailsIfOnlyLowercasePresent() {
Properties props = new Properties();
GenderAnnotator annotator = new GenderAnnotator("gender", props);
annotator.maleNames.clear();
annotator.femaleNames.clear();
annotator.maleNames.add("john");
CoreLabel token = new CoreLabel();
token.setWord("JOHN");
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
Annotation annotation = new Annotation("Name in caps");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(annotation);
assertEquals("MALE", mention.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("MALE", token.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testEntityMentionWithSecondTokenGenderPropagation() {
Properties props = new Properties();
GenderAnnotator annotator = new GenderAnnotator("gender", props);
annotator.femaleNames.clear();
annotator.femaleNames.add("alice");
CoreLabel token1 = new CoreLabel();
token1.setWord("Alice");
CoreLabel token2 = new CoreLabel();
token2.setWord("Johnson");
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
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("Multi-token mention");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(annotation);
assertEquals("FEMALE", mention.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("FEMALE", token1.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("FEMALE", token2.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testEntityTypeAnnotationExplicitlySetToNull() {
Properties props = new Properties();
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("John");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.EntityTypeAnnotation.class, null);
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("Null EntityTypeAnnotation");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
try {
annotator.annotate(annotation);
} catch (Exception e) {
return;
}
fail("Expected exception due to null EntityTypeAnnotation");
}

@Test
public void testNewGenderAnnotatorWithInvalidFilePathsStillFunctionsIfListsInjected() {
Properties props = new Properties();
props.setProperty("gender.maleNamesFile", "invalid/path/male.txt");
props.setProperty("gender.femaleNamesFile", "invalid/path/female.txt");
GenderAnnotator annotator = new GenderAnnotator("gender", props);
CoreLabel token = new CoreLabel();
token.setWord("Mary");
List<CoreLabel> tokens = new ArrayList<>();
tokens.add(token);
annotator.femaleNames.clear();
annotator.femaleNames.add("mary");
CoreMap mention = new ArrayCoreMap();
mention.set(CoreAnnotations.TokensAnnotation.class, tokens);
mention.set(CoreAnnotations.EntityTypeAnnotation.class, "PERSON");
List<CoreMap> mentions = new ArrayList<>();
mentions.add(mention);
CoreMap sentence = new ArrayCoreMap();
sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
List<CoreMap> sentences = new ArrayList<>();
sentences.add(sentence);
Annotation annotation = new Annotation("Overrides after bad paths");
annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
annotator.annotate(annotation);
assertEquals("FEMALE", mention.get(CoreAnnotations.GenderAnnotation.class));
assertEquals("FEMALE", token.get(CoreAnnotations.GenderAnnotation.class));
}

@Test
public void testLoadGenderNamesHandlesEmptyFileContentGracefully() {
Properties props = new Properties();
GenderAnnotator annotator = new GenderAnnotator("gender", props);
Set<String> genderSet = new HashSet<>();
List<String> linesFromFile = new ArrayList<>();
for (String line : linesFromFile) {
String[] names = line.split(",");
for (String name : names) {
genderSet.add(name.toLowerCase());
}
}
assertTrue(genderSet.isEmpty());
}
}
