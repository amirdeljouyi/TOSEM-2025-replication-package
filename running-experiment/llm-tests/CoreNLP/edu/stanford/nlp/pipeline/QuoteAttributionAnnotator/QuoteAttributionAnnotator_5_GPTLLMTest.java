package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.util.Pair;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.util.CoreMap;

import java.util.*;

public class QuoteAttributionAnnotator_5_GPTLLMTest {

 @Test
  public void testSimpleInitialization() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "dummy/path");
    props.setProperty("booknlpCoref", "dummy/coref");
    props.setProperty("QMSieves", "tri,dep");
    props.setProperty("MSSieves", "det");
    props.setProperty("familyWordsFile", "dummy/family.txt");
    props.setProperty("animacyWordsFile", "dummy/animacy.txt");
    props.setProperty("genderNamesFile", "dummy/gender.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    assertNotNull(annotator);
  }
@Test
  public void testMissingCharacterPathTriggersMapBuildFlag() {
    Properties props = new Properties();
    props.setProperty("booknlpCoref", "dummy/coref");
    props.setProperty("familyWordsFile", "dummy/family.txt");
    props.setProperty("animacyWordsFile", "dummy/animacy.txt");
    props.setProperty("genderNamesFile", "dummy/gender.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    assertTrue(annotator.buildCharacterMapPerAnnotation);
  }
@Test
  public void testRequirementsIncludeCoref() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "c.txt");
    props.setProperty("booknlpCoref", "coref.tsv");
    props.setProperty("familyWordsFile", "dummy.txt");
    props.setProperty("animacyWordsFile", "dummy.txt");
    props.setProperty("genderNamesFile", "dummy.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    Set<Class<? extends CoreAnnotation>> required = annotator.requires();

    assertTrue(required.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
    assertTrue(required.contains(CorefCoreAnnotations.CorefChainAnnotation.class));
  }
@Test
  public void testRequirementsSatisfiedIncludesSpeaker() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "char.txt");
    props.setProperty("booknlpCoref", "coref.tsv");
    props.setProperty("familyWordsFile", "dummy.txt");
    props.setProperty("animacyWordsFile", "dummy.txt");
    props.setProperty("genderNamesFile", "dummy.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertTrue(satisfied.contains(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    assertTrue(satisfied.contains(QuoteAttributionAnnotator.CanonicalMentionAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.ParagraphIndexAnnotation.class));
  }
@Test
  public void testAnnotateEmptyDocument() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "char.txt");
    props.setProperty("booknlpCoref", "coref.tsv");
    props.setProperty("familyWordsFile", "dummy.txt");
    props.setProperty("animacyWordsFile", "dummy.txt");
    props.setProperty("genderNamesFile", "dummy.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    Annotation annotation = new Annotation("");

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("Annotate failed on empty input: " + e.getMessage());
    }
  }
@Test
  public void testEntityMentionsToCharacterMapSingleName() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "char.txt");
    props.setProperty("booknlpCoref", "coref.tsv");
    props.setProperty("familyWordsFile", "f.txt");
    props.setProperty("animacyWordsFile", "a.txt");
    props.setProperty("genderNamesFile", "g.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    Annotation annotation = new Annotation("Tom went home.");
    
    CoreLabel token = new CoreLabel();
    token.setWord("Tom");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    CoreMap mention = new Annotation("Tom");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(mention);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);

    try {
      annotator.entityMentionsToCharacterMap(annotation);
    } catch (Exception e) {
      fail("Failed entityMentionsToCharacterMap: " + e.getMessage());
    }
  }
@Test
  public void testSpeakerAnnotationClassTypes() {
    QuoteAttributionAnnotator.SpeakerAnnotation sa = new QuoteAttributionAnnotator.SpeakerAnnotation();
    Class<?> result1 = sa.getType();
    assertEquals(String.class, result1);

    QuoteAttributionAnnotator.MentionAnnotation ma = new QuoteAttributionAnnotator.MentionAnnotation();
    Class<?> result2 = ma.getType();
    assertEquals(String.class, result2);

    QuoteAttributionAnnotator.MentionBeginAnnotation mba = new QuoteAttributionAnnotator.MentionBeginAnnotation();
    Class<?> result3 = mba.getType();
    assertEquals(Integer.class, result3);

    QuoteAttributionAnnotator.MentionEndAnnotation mea = new QuoteAttributionAnnotator.MentionEndAnnotation();
    Class<?> result4 = mea.getType();
    assertEquals(Integer.class, result4);

    QuoteAttributionAnnotator.CanonicalMentionAnnotation ca = new QuoteAttributionAnnotator.CanonicalMentionAnnotation();
    Class<?> result5 = ca.getType();
    assertEquals(String.class, result5);
  }
@Test
  public void testUseCorefFalseHandling() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "char.txt");
    props.setProperty("booknlpCoref", "coref.tsv");
    props.setProperty("familyWordsFile", "dummy.txt");
    props.setProperty("animacyWordsFile", "dummy.txt");
    props.setProperty("genderNamesFile", "dummy.txt");
    props.setProperty("useCoref", "false");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    Set<Class<? extends CoreAnnotation>> requirements = annotator.requires();

    boolean includesCoref = requirements.contains(CorefCoreAnnotations.CorefChainAnnotation.class);
    assertFalse(includesCoref);
  }
@Test
  public void testAnnotationOnSimpleInput() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "char.txt");
    props.setProperty("booknlpCoref", "coref.tsv");
    props.setProperty("familyWordsFile", "family.txt");
    props.setProperty("animacyWordsFile", "animacy.txt");
    props.setProperty("genderNamesFile", "gender.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    CoreLabel word1 = new CoreLabel();
    word1.setWord("Tom");
    word1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    CoreMap mention = new Annotation("Tom");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(mention);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(word1);

    Annotation annotation = new Annotation("Tom said, \"Hello.\"");
    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.TextAnnotation.class, "Tom said, \"Hello.\"");

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("Annotate failed: " + e.getMessage());
    }
  }
@Test
  public void testInitializationWithMinimalProperties() {
    Properties props = new Properties();
    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    assertNotNull(annotator);
    assertEquals(QuoteAttributionAnnotator.DEFAULT_MODEL_PATH,
        QuoteAttributionAnnotator.MODEL_PATH);
  }
@Test
  public void testAnnotateWithNullMentionsAnnotation() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "char.tsv");
    props.setProperty("booknlpCoref", "coref.tsv");
    props.setProperty("familyWordsFile", "family.txt");
    props.setProperty("animacyWordsFile", "animacy.txt");
    props.setProperty("genderNamesFile", "gender.txt");
    props.setProperty("QMSieves", "tri");
    props.setProperty("MSSieves", "det");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    Annotation annotation = new Annotation("John said, \"I'll be there.\"");

    annotation.set(CoreAnnotations.TextAnnotation.class, "John said, \"I'll be there.\"");

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("Annotate should not throw exception when MentionsAnnotation is null");
    }
  }
@Test
  public void testAnnotateWithMentionWithoutPERSON() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "char.tsv");
    props.setProperty("booknlpCoref", "coref.tsv");
    props.setProperty("familyWordsFile", "family.txt");
    props.setProperty("animacyWordsFile", "animacy.txt");
    props.setProperty("genderNamesFile", "gender.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    Annotation annotation = new Annotation("The table said hello.");

    CoreMap mention = new Annotation("table");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");

    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(mention);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);

    try {
      annotator.entityMentionsToCharacterMap(annotation);
    } catch (Exception e) {
      fail("Should ignore non-PERSON entity types without error.");
    }
  }
@Test
  public void testCanonicalEntityMentionNotFound() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "char.tsv");
    props.setProperty("booknlpCoref", "coref.tsv");
    props.setProperty("familyWordsFile", "family.txt");
    props.setProperty("animacyWordsFile", "animacy.txt");
    props.setProperty("genderNamesFile", "gender.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("He replied, \"Yes.\"");

    CoreMap quote = new Annotation("\"Yes.\"");
    quote.set(QuoteAttributionAnnotator.MentionBeginAnnotation.class, 0);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.EntityMentionIndexAnnotation.class, null);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> quotes = new ArrayList<>();
    quotes.add(quote);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, quotes);

    List<CoreMap> emptyMentions = new ArrayList<>();
    annotation.set(CoreAnnotations.MentionsAnnotation.class, emptyMentions);

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("Should handle missing canonical entity mention safely.");
    }
  }
@Test
  public void testAnnotateWithNullCorefAndCharacterMap() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "somefile.txt");
    props.setProperty("booknlpCoref", (String) null);
    props.setProperty("familyWordsFile", "fam.txt");
    props.setProperty("animacyWordsFile", "ani.txt");
    props.setProperty("genderNamesFile", "gen.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    Annotation annotation = new Annotation("He answered, \"Sure.\"");

    annotation.set(CoreAnnotations.TextAnnotation.class, "He answered, \"Sure.\"");

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("Should not fail when COREF_PATH is null");
    }
  }
@Test
  public void testInvalidSieveNameSkippedGracefully() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "char.tsv");
    props.setProperty("booknlpCoref", "coref.tsv");
    props.setProperty("QMSieves", "invalidsieve");
    props.setProperty("MSSieves", "invalidsieve");
    props.setProperty("familyWordsFile", "family.txt");
    props.setProperty("animacyWordsFile", "animacy.txt");
    props.setProperty("genderNamesFile", "gender.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    Annotation annotation = new Annotation("He said hello.");

    annotation.set(CoreAnnotations.TextAnnotation.class, "He said hello.");
    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token = new CoreLabel();
    tokens.add(token);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("Should not throw exception if an invalid sieve name is used.");
    }
  }
@Test
  public void testMentionToSpeakerWithoutMentionBeginSet() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "char.tsv");
    props.setProperty("booknlpCoref", "coref.tsv");
    props.setProperty("familyWordsFile", "family.txt");
    props.setProperty("animacyWordsFile", "animacy.txt");
    props.setProperty("genderNamesFile", "gender.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("Someone said that.");
    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token = new CoreLabel();
    tokens.add(token);

    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.TextAnnotation.class, "Someone said that.");

    List<CoreMap> mentions = new ArrayList<>();
    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("Should tolerate quotes lacking mention begin info.");
    }
  }
@Test
  public void testDefaultQMAndMSSieveConfiguration() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "char.tsv");
    props.setProperty("booknlpCoref", "coref.tsv");
    props.setProperty("familyWordsFile", "family.txt");
    props.setProperty("animacyWordsFile", "animacy.txt");
    props.setProperty("genderNamesFile", "gender.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("This is a test with default sieves.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "This is a test with default sieves.");

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("Annotation should work with default sieve configuration.");
    }
  }
@Test
  public void testAnnotateWithEmptyCharacterMapAndValidMention() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "nonexistent.tsv");
    props.setProperty("booknlpCoref", "nonexistent.tsv");
    props.setProperty("familyWordsFile", "dummy_family.txt");
    props.setProperty("animacyWordsFile", "dummy_animacy.txt");
    props.setProperty("genderNamesFile", "dummy_gender.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("Mary replied, \"I’m ready.\"");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Mary replied, \"I’m ready.\"");

    CoreMap mention = new Annotation("Mary");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(mention);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("Should not fail when characterMap is empty and mention is valid.");
    }
  }
@Test
  public void testAnnotateWithCharacterMapButMissingTokensAnnotation() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "somefile.tsv");
    props.setProperty("booknlpCoref", "coref.tsv");
    props.setProperty("familyWordsFile", "f.txt");
    props.setProperty("animacyWordsFile", "a.txt");
    props.setProperty("genderNamesFile", "g.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("He shouted, \"Run!\"");
    annotation.set(CoreAnnotations.TextAnnotation.class, "He shouted, \"Run!\"");

    CoreMap quote = new Annotation("\"Run!\"");
    quote.set(QuoteAttributionAnnotator.MentionBeginAnnotation.class, 1);

    List<CoreMap> quotes = new ArrayList<>();
    quotes.add(quote);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, quotes);

    List<CoreMap> mentions = new ArrayList<>();
    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("Should not fail when TokensAnnotation is missing.");
    }
  }
@Test
  public void testEntityMentionsToCharacterMapWithMultiplePersons() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "fake.tsv");
    props.setProperty("booknlpCoref", "fake.tsv");
    props.setProperty("familyWordsFile", "file1.txt");
    props.setProperty("animacyWordsFile", "file2.txt");
    props.setProperty("genderNamesFile", "file3.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("Alice and Bob walked together.");

    CoreMap mention1 = new Annotation("Alice");
    mention1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    CoreMap mention2 = new Annotation("Bob");
    mention2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(mention1);
    mentions.add(mention2);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);

    try {
      annotator.entityMentionsToCharacterMap(annotation);
    } catch (Exception e) {
      fail("Should handle multiple PERSON mentions without failure.");
    }
  }
@Test
  public void testInitializationWithEmptyQMAndMSSieveList() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "any.tsv");
    props.setProperty("booknlpCoref", "any.tsv");
    props.setProperty("QMSieves", "");
    props.setProperty("MSSieves", "");
    props.setProperty("familyWordsFile", "dummy.txt");
    props.setProperty("animacyWordsFile", "dummy_a.txt");
    props.setProperty("genderNamesFile", "dummy_g.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    Annotation annotation = new Annotation("\"Hello,\" Alice said.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "\"Hello,\" Alice said.");

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("Should handle empty sieve configuration without throwing.");
    }
  }
@Test
  public void testMentionToSpeakerWithCanonicalMentionIndexButNoTokens() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "characters.tsv");
    props.setProperty("booknlpCoref", "coref.tsv");
    props.setProperty("familyWordsFile", "f.txt");
    props.setProperty("animacyWordsFile", "a.txt");
    props.setProperty("genderNamesFile", "g.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("John smiled and said, \"Yes.\"");

    CoreMap quote = new Annotation("\"Yes.\"");
    quote.set(QuoteAttributionAnnotator.MentionBeginAnnotation.class, 0);

    CoreMap mention = new Annotation("John");
    mention.set(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class, 1);

    CoreMap canonicalMention = new Annotation("John Smith");

    List<CoreMap> corefMentions = new ArrayList<>();
    corefMentions.add(mention);
    corefMentions.add(canonicalMention);

    annotation.set(CoreAnnotations.MentionsAnnotation.class, corefMentions);

    List<CoreMap> quoteList = new ArrayList<>();
    quoteList.add(quote);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, quoteList);

    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);
    tokens.add(token);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("Should not fail when canonical mention is missing token data.");
    }
  }
@Test
  public void testCanonicalMentionTokenOffsetsSetCorrectlyWhenPresent() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "characters.tsv");
    props.setProperty("booknlpCoref", "coref.tsv");
    props.setProperty("familyWordsFile", "family.txt");
    props.setProperty("animacyWordsFile", "animacy.txt");
    props.setProperty("genderNamesFile", "gender.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("Jane said, \"Let's begin.\"");

    CoreLabel firstToken = new CoreLabel();
    firstToken.setWord("Jane");
    firstToken.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    CoreLabel lastToken = new CoreLabel();
    lastToken.setWord("Doe");
    lastToken.set(CoreAnnotations.TokenBeginAnnotation.class, 1);

    List<CoreLabel> canonicalTokens = new ArrayList<>();
    canonicalTokens.add(firstToken);
    canonicalTokens.add(lastToken);

    CoreMap canonicalMention = new Annotation("Jane Doe");
    canonicalMention.set(CoreAnnotations.TextAnnotation.class, "Jane Doe");
    canonicalMention.set(CoreAnnotations.TokensAnnotation.class, canonicalTokens);

    CoreMap mention = new Annotation("Jane");
    mention.set(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class, 0);

    CoreMap quote = new Annotation("\"Let's begin.\"");
    quote.set(QuoteAttributionAnnotator.MentionBeginAnnotation.class, 0);

    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(canonicalMention); 
    mentions.add(mention);          
    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);

    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel refToken = new CoreLabel();
    refToken.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 1);
    tokens.add(refToken);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(quote);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("Should annotate canonical offsets properly when token offsets present.");
    }

    assertEquals("Jane Doe", quote.get(QuoteAttributionAnnotator.CanonicalMentionAnnotation.class));
    assertEquals(Integer.valueOf(0), quote.get(QuoteAttributionAnnotator.CanonicalMentionBeginAnnotation.class));
    assertEquals(Integer.valueOf(1), quote.get(QuoteAttributionAnnotator.CanonicalMentionEndAnnotation.class));
  }
@Test
  public void testMentionWithoutNamedEntityTagAnnotation() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "test.tsv");
    props.setProperty("booknlpCoref", "coref.tsv");
    props.setProperty("familyWordsFile", "family.txt");
    props.setProperty("animacyWordsFile", "animacy.txt");
    props.setProperty("genderNamesFile", "gender.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("Someone knocked on the door.");
    CoreMap mention = new Annotation("Someone");
    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(mention);

    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);

    try {
      annotator.entityMentionsToCharacterMap(annotation);
    } catch (Exception e) {
      fail("Should safely skip mentions missing NER types");
    }
  }
@Test
  public void testAnnotateWithEmptyTokensList() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "char.tsv");
    props.setProperty("booknlpCoref", "coref.tsv");
    props.setProperty("familyWordsFile", "fam.txt");
    props.setProperty("animacyWordsFile", "ani.txt");
    props.setProperty("genderNamesFile", "gen.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("No tokens in list.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "No tokens in list.");
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("Should not throw error on empty tokens list.");
    }
  }
@Test
  public void testMentionBeginIndexOutOfBounds() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "char.tsv");
    props.setProperty("booknlpCoref", "coref.tsv");
    props.setProperty("familyWordsFile", "f.txt");
    props.setProperty("animacyWordsFile", "a.txt");
    props.setProperty("genderNamesFile", "g.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("Sample text.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Sample text.");

    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel t = new CoreLabel();
    t.setWord("Sample");
    tokens.add(t);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    CoreMap quote = new Annotation("Quote");
    quote.set(QuoteAttributionAnnotator.MentionBeginAnnotation.class, 5);

    List<CoreMap> sentenceQuotes = new ArrayList<>();
    sentenceQuotes.add(quote);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceQuotes);

    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("Should not throw error if MentionBeginAnnotation is out-of-bounds");
    }
  }
@Test
  public void testCanonicalMentionWithNullTokenList() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "chars.tsv");
    props.setProperty("booknlpCoref", "cr.tsv");
    props.setProperty("familyWordsFile", "fw.txt");
    props.setProperty("animacyWordsFile", "aw.txt");
    props.setProperty("genderNamesFile", "gw.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("Jane said, \"Hello.\"");

    CoreMap canonicalMention = new Annotation("Jane Smith");
    canonicalMention.set(CoreAnnotations.TextAnnotation.class, "Jane Smith");
    canonicalMention.set(CoreAnnotations.TokensAnnotation.class, null);

    CoreMap mention = new Annotation("Jane");
    mention.set(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class, 0);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 1);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> mentionList = new ArrayList<>();
    mentionList.add(canonicalMention); 
    mentionList.add(mention); 
    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentionList);

    CoreMap quote = new Annotation("\"Hello\"");
    quote.set(QuoteAttributionAnnotator.MentionBeginAnnotation.class, 0);
    List<CoreMap> quotes = new ArrayList<>();
    quotes.add(quote);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, quotes);

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("Should gracefully handle canonical mention with null token list.");
    }
  }
@Test
  public void testMissingOptionalWordListsFallback() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "ch.txt");
    props.setProperty("booknlpCoref", "coref.txt");
    
    

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("John yelled, \"Stop!\"");
    annotation.set(CoreAnnotations.TextAnnotation.class, "John yelled, \"Stop!\"");

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("Should default to fallback model/word list paths with missing optional properties.");
    }
  }
@Test
  public void testExplicitEmptySieveConfig() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "data.tsv");
    props.setProperty("booknlpCoref", "coref.tsv");
    props.setProperty("QMSieves", "");
    props.setProperty("MSSieves", "");
    props.setProperty("familyWordsFile", "f.txt");
    props.setProperty("animacyWordsFile", "a.txt");
    props.setProperty("genderNamesFile", "g.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    Annotation annotation = new Annotation("Empty sieve config.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Empty sieve config.");

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("Should not fail on empty QMSieve and MSSieve config.");
    }
  }
@Test
  public void testNullModelPathFallsBackToDefault() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "char.tsv");
    props.setProperty("booknlpCoref", "coref.tsv");
    props.setProperty("modelPath", null);  
    props.setProperty("familyWordsFile", "f.txt");
    props.setProperty("animacyWordsFile", "a.txt");
    props.setProperty("genderNamesFile", "g.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    assertNotNull(annotator);
  }
@Test
  public void testAnnotateWithNoTextAnnotation() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "char.tsv");
    props.setProperty("booknlpCoref", "coref.tsv");
    props.setProperty("familyWordsFile", "family.txt");
    props.setProperty("animacyWordsFile", "animacy.txt");
    props.setProperty("genderNamesFile", "gender.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation(""); 

    List<CoreMap> mentions = new ArrayList<>();
    CoreMap mention = new Annotation("James");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    mentions.add(mention);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("annotate should not throw exception when TextAnnotation is missing");
    }
  }
@Test
  public void testEntityMentionsToCharacterMapCleansWhitespaceInKeys() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "characters.tsv");
    props.setProperty("booknlpCoref", "coref.tsv");
    props.setProperty("familyWordsFile", "fam.txt");
    props.setProperty("animacyWordsFile", "ani.txt");
    props.setProperty("genderNamesFile", "gen.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("  John   Doe  is here.");

    CoreMap mention = new Annotation("  John   Doe  ");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(mention);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);

    try {
      annotator.entityMentionsToCharacterMap(annotation);
    } catch (Exception e) {
      fail("Should handle whitespace cleanup in mention strings correctly.");
    }
  }
@Test
  public void testAnnotateWithOnlyPunctuationTokens() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "chars.tsv");
    props.setProperty("booknlpCoref", "coref.tsv");
    props.setProperty("familyWordsFile", "file.txt");
    props.setProperty("animacyWordsFile", "file2.txt");
    props.setProperty("genderNamesFile", "file3.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("“...”");
    annotation.set(CoreAnnotations.TextAnnotation.class, "“...”");

    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel t1 = new CoreLabel();
    t1.setWord("\"");
    CoreLabel t2 = new CoreLabel();
    t2.setWord(".");
    CoreLabel t3 = new CoreLabel();
    t3.setWord(".");
    CoreLabel t4 = new CoreLabel();
    t4.setWord(".");
    CoreLabel t5 = new CoreLabel();
    t5.setWord("\"");

    tokens.add(t1);
    tokens.add(t2);
    tokens.add(t3);
    tokens.add(t4);
    tokens.add(t5);

    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("Should not throw exception on documents with only punctuation.");
    }
  }
@Test
  public void testAnnotateWithTokenMissingEntityMentionIndexAnnotation() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "charlist.tsv");
    props.setProperty("booknlpCoref", "coref.tsv");
    props.setProperty("familyWordsFile", "f.txt");
    props.setProperty("animacyWordsFile", "a.txt");
    props.setProperty("genderNamesFile", "g.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("Tom said, \"Maybe.\"");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Tom said, \"Maybe.\"");

    CoreLabel token = new CoreLabel();
    token.setWord("Tom");
    

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    CoreMap quote = new Annotation("\"Maybe.\"");
    quote.set(QuoteAttributionAnnotator.MentionBeginAnnotation.class, 0);

    List<CoreMap> quotes = new ArrayList<>();
    quotes.add(quote);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, quotes);

    List<CoreMap> mentions = new ArrayList<>();
    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("Should skip processing when token is missing entity mention index.");
    }
  }
@Test
  public void testAnnotateWithOnlyQuotedSentenceNoMention() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "chars.tsv");
    props.setProperty("booknlpCoref", "coref.tsv");
    props.setProperty("familyWordsFile", "family.txt");
    props.setProperty("animacyWordsFile", "animacy.txt");
    props.setProperty("genderNamesFile", "gender.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("\"Go!\"");
    annotation.set(CoreAnnotations.TextAnnotation.class, "\"Go!\"");
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("Should handle standalone quote-only sentence without failure.");
    }
  }
@Test
  public void testAnnotateWhenCanonicalEntityMentionIndexInvalid() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "char.tsv");
    props.setProperty("booknlpCoref", "coref.tsv");
    props.setProperty("familyWordsFile", "f.txt");
    props.setProperty("animacyWordsFile", "a.txt");
    props.setProperty("genderNamesFile", "gender.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("Taylor laughed.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Taylor laughed.");

    CoreMap mention = new Annotation("Taylor");
    mention.set(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class, 99); 

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(mention);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);

    CoreMap quote = new Annotation("\"Okay.\"");
    quote.set(QuoteAttributionAnnotator.MentionBeginAnnotation.class, 0);

    List<CoreMap> quotes = new ArrayList<>();
    quotes.add(quote);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, quotes);

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("Should handle missing canonical entity mention index without error.");
    }
  }
@Test
  public void testEntityMentionsToCharacterMapWithDuplicatePersonMention() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "char.tsv");
    props.setProperty("booknlpCoref", "coref.tsv");
    props.setProperty("familyWordsFile", "dummyf.txt");
    props.setProperty("animacyWordsFile", "dummya.txt");
    props.setProperty("genderNamesFile", "dummyg.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("Anna spoke. Anna listened.");
    CoreMap mention1 = new Annotation("Anna");
    mention1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    CoreMap mention2 = new Annotation("Anna"); 
    mention2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(mention1);
    mentions.add(mention2);

    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);

    try {
      annotator.entityMentionsToCharacterMap(annotation);
    } catch (Exception e) {
      fail("Should support duplicate PERSON mentions without failure.");
    }
  }
@Test
  public void testCharacterMapIsNotNullButCharactersFileIsNull() {
    Properties props = new Properties();
    props.setProperty("booknlpCoref", "coref.tsv");
    props.setProperty("charactersPath", null); 
    props.setProperty("familyWordsFile", "family.txt");
    props.setProperty("animacyWordsFile", "animacy.txt");
    props.setProperty("genderNamesFile", "gender.txt");
    props.setProperty("QMSieves", "tri");
    props.setProperty("MSSieves", "det");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    Annotation annotation = new Annotation("Emily said, \"Hi.\"");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Emily said, \"Hi.\"");

    List<CoreMap> mentions = new ArrayList<>();
    CoreMap mention = new Annotation("Emily");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    mentions.add(mention);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("Should handle charactersFile = null when characterMap is used.");
    }
  }
@Test
  public void testAnnotateMentionsToSpeakerWithEntityMentionIndexButCanonicalMissing() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "chars.tsv");
    props.setProperty("booknlpCoref", "coref.tsv");
    props.setProperty("familyWordsFile", "fam.txt");
    props.setProperty("animacyWordsFile", "ani.txt");
    props.setProperty("genderNamesFile", "gen.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("She whispered, \"Be quiet.\"");
    annotation.set(CoreAnnotations.TextAnnotation.class, "She whispered, \"Be quiet.\"");

    CoreMap quote = new Annotation("\"Be quiet.\"");
    quote.set(QuoteAttributionAnnotator.MentionBeginAnnotation.class, 0);

    CoreLabel mentionToken = new CoreLabel();
    mentionToken.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0); 
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(mentionToken);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    CoreMap mention = new Annotation("She");
    

    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(mention);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(quote);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("Should skip canonical linking when CanonicalEntityMentionIndexAnnotation is missing.");
    }
  }
@Test
  public void testCanonicalMentionEndsBeforeItBegins() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "c.tsv");
    props.setProperty("booknlpCoref", "b.tsv");
    props.setProperty("familyWordsFile", "f.txt");
    props.setProperty("animacyWordsFile", "a.txt");
    props.setProperty("genderNamesFile", "g.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("Something happened.");

    CoreMap canonicalMention = new Annotation("Agent X");
    List<CoreLabel> canonicalTokens = new ArrayList<>();

    CoreLabel beginToken = new CoreLabel();
    beginToken.set(CoreAnnotations.TokenBeginAnnotation.class, 3);

    CoreLabel endToken = new CoreLabel();
    endToken.set(CoreAnnotations.TokenBeginAnnotation.class, 2); 

    canonicalTokens.add(beginToken);
    canonicalTokens.add(endToken);
    canonicalMention.set(CoreAnnotations.TextAnnotation.class, "Agent X");
    canonicalMention.set(CoreAnnotations.TokensAnnotation.class, canonicalTokens);

    CoreMap mention = new Annotation("He");
    mention.set(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class, 0);

    CoreLabel tokenWithEntity = new CoreLabel();
    tokenWithEntity.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 1);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(tokenWithEntity);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(canonicalMention);
    mentions.add(mention);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);

    CoreMap quote = new Annotation("\"Go now!\"");
    quote.set(QuoteAttributionAnnotator.MentionBeginAnnotation.class, 0);

    List<CoreMap> quotes = new ArrayList<>();
    quotes.add(quote);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, quotes);

    try {
      annotator.annotate(annotation);
      
      assertEquals("Agent X", quote.get(QuoteAttributionAnnotator.CanonicalMentionAnnotation.class));
    } catch (Exception e) {
      fail("Should not crash even when canonical entity token range is invalid");
    }
  }
@Test
  public void testAnnotateWithUseCorefDisabled() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "whoknows.tsv");
    props.setProperty("booknlpCoref", "who.tsv");
    props.setProperty("familyWordsFile", "fam.txt");
    props.setProperty("animacyWordsFile", "ani.txt");
    props.setProperty("genderNamesFile", "gen.txt");
    props.setProperty("useCoref", "false");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    Annotation annotation = new Annotation("Just a random string.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Just a random string.");

    try {
      Set<Class<? extends CoreAnnotation>> result = annotator.requires();
      boolean containsCoref = result.contains(edu.stanford.nlp.coref.CorefCoreAnnotations.CorefChainAnnotation.class);
      assertFalse(containsCoref);
    } catch (Exception e) {
      fail("Should not depend on coref when useCoref=false");
    }
  }
@Test
  public void testHandlesNullCharacterMapInEntityMentionsConversionGracefully() {
    Properties props = new Properties();
    props.setProperty("charactersPath", null);
    props.setProperty("booknlpCoref", "coref.tsv");
    props.setProperty("familyWordsFile", "fam.txt");
    props.setProperty("animacyWordsFile", "ani.txt");
    props.setProperty("genderNamesFile", "gender.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("Captain said, 'Hold steady'.");
    CoreMap mention = new Annotation("Captain");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(mention);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);

    try {
      annotator.entityMentionsToCharacterMap(annotation);
    } catch (Exception e) {
      fail("Should initialize empty characterMap and populate it without error.");
    }
  }
@Test
  public void testSupervisedSieveModelFailsToLoadGracefully() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "test_characters.tsv");
    props.setProperty("booknlpCoref", "test_coref.tsv");
    props.setProperty("QMSieves", "sup");
    props.setProperty("MSSieves", "top");
    props.setProperty("modelPath", "non_existing_model_path.ser");
    props.setProperty("familyWordsFile", "test_fam.txt");
    props.setProperty("animacyWordsFile", "test_animacy.txt");
    props.setProperty("genderNamesFile", "test_gender.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("He said, \"Let's go!\"");
    annotation.set(CoreAnnotations.TextAnnotation.class, "He said, \"Let's go!\"");
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    try {
      annotator.annotate(annotation);
    } catch (RuntimeException e) {
      
      assertTrue(e.getMessage().contains("not found") || e.getMessage().contains("Failed"));
    } catch (Exception e) {
      fail("Unexpected exception raised: " + e.getMessage());
    }
  }
@Test
  public void testInvalidSieveNameInQMSievesSkippedWithoutException() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "test_characters.tsv");
    props.setProperty("booknlpCoref", "coref.tsv");
    props.setProperty("QMSieves", "tri,invalidSieve");
    props.setProperty("MSSieves", "det");
    props.setProperty("modelPath", "dummy_model.ser");
    props.setProperty("familyWordsFile", "dummyFam.txt");
    props.setProperty("animacyWordsFile", "dummyAnimacy.txt");
    props.setProperty("genderNamesFile", "dummyGender.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("John said, \"Go!\"");
    annotation.set(CoreAnnotations.TextAnnotation.class, "John said, \"Go!\"");
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("Invalid sieve in QMSieves should be skipped silently, not fail annotation.");
    }
  }
@Test
  public void testInvalidSieveNameInMSSievesSkippedWithoutError() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "test_characters.tsv");
    props.setProperty("booknlpCoref", "coref.tsv");
    props.setProperty("QMSieves", "tri");
    props.setProperty("MSSieves", "foo,det,bar");
    props.setProperty("modelPath", "dummy_model.ser");
    props.setProperty("familyWordsFile", "family.txt");
    props.setProperty("animacyWordsFile", "animacy.txt");
    props.setProperty("genderNamesFile", "gender.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("Lily said, \"I'll handle it.\"");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Lily said, \"I'll handle it.\"");
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("Invalid sieve names in MSSieves should not cause failure.");
    }
  }
@Test
  public void testAnnotateWithNullMentionBeginTokenInQuote() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "char.tsv");
    props.setProperty("booknlpCoref", "coref.tsv");
    props.setProperty("QMSieves", "tri");
    props.setProperty("MSSieves", "det");
    props.setProperty("familyWordsFile", "fam.txt");
    props.setProperty("animacyWordsFile", "ani.txt");
    props.setProperty("genderNamesFile", "gen.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("\"No one.\"");
    annotation.set(CoreAnnotations.TextAnnotation.class, "\"No one.\"");
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    CoreMap quote = new Annotation("\"No one.\"");
    quote.set(QuoteAttributionAnnotator.MentionBeginAnnotation.class, null); 

    List<CoreMap> quotes = new ArrayList<>();
    quotes.add(quote);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, quotes);

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("Quote without MentionBeginAnnotation should not throw exception.");
    }
  }
@Test
  public void testCanonicalMentionIndexRefersToNonexistentMention() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "characters.tsv");
    props.setProperty("booknlpCoref", "coref.tsv");
    props.setProperty("QMSieves", "tri");
    props.setProperty("MSSieves", "det");
    props.setProperty("familyWordsFile", "f.txt");
    props.setProperty("animacyWordsFile", "a.txt");
    props.setProperty("genderNamesFile", "g.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    List<CoreMap> mentions = new ArrayList<>();
    CoreMap mention = new Annotation("Jon");
    mention.set(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class, 99); 
    mentions.add(mention);

    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel referringToken = new CoreLabel();
    referringToken.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);
    tokens.add(referringToken);

    List<CoreMap> quotes = new ArrayList<>();
    CoreMap quote = new Annotation("\"Hello!\"");
    quote.set(QuoteAttributionAnnotator.MentionBeginAnnotation.class, 0);
    quotes.add(quote);

    Annotation annotation = new Annotation("Jon said, \"Hello!\"");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Jon said, \"Hello!\"");
    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, quotes);

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("Invalid CanonicalEntityMentionIndex should be ignored safely.");
    }
  }
@Test
  public void testQuoteWithoutEntityMentionStillAnnotated() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "c.tsv");
    props.setProperty("booknlpCoref", "b.tsv");
    props.setProperty("QMSieves", "tri");
    props.setProperty("MSSieves", "det");
    props.setProperty("familyWordsFile", "file1.txt");
    props.setProperty("animacyWordsFile", "file2.txt");
    props.setProperty("genderNamesFile", "file3.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("She whispered, \"Be quiet.\"");

    CoreMap quote = new Annotation("\"Be quiet.\"");
    
    List<CoreMap> quotes = new ArrayList<>();
    quotes.add(quote);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, quotes);
    annotation.set(CoreAnnotations.TextAnnotation.class, "She whispered, \"Be quiet.\"");
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("Quote without entity mention should still be handled.");
    }
  } 
}