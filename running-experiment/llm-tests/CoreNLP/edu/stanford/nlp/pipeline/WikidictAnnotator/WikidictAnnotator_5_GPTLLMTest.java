package edu.stanford.nlp.pipeline;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.process.*;
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.Timex;
import edu.stanford.nlp.util.*;
import junit.framework.TestCase;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class WikidictAnnotator_5_GPTLLMTest {

 @Test
  public void testLinkSimpleWikidictMatch() throws Exception {
    File dictFile = File.createTempFile("wikidict", ".tsv");
    PrintWriter writer = new PrintWriter(dictFile, "UTF-8");
    writer.println("Barack Obama\tBarack_Obama\t1.0");
    writer.close();

    Properties props = new Properties();
    props.setProperty("wikidict", dictFile.getAbsolutePath());

    WikidictAnnotator annotator = new WikidictAnnotator("test", props);
    Annotation mention = new Annotation("Barack Obama");
    mention.set(CoreAnnotations.TextAnnotation.class, "Barack Obama");
    mention.set(CoreAnnotations.OriginalTextAnnotation.class, "Barack Obama");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    Optional<String> result = annotator.link(mention);
    assertTrue(result.isPresent());
    assertEquals("Barack_Obama", result.get());
  }
@Test
  public void testLinkWithCaselessMatch() throws Exception {
    File dictFile = File.createTempFile("wikidict", ".tsv");
    PrintWriter writer = new PrintWriter(dictFile, "UTF-8");
    writer.println("Barack Obama\tBarack_Obama\t1.0");
    writer.close();

    Properties props = new Properties();
    props.setProperty("wikidict", dictFile.getAbsolutePath());
    props.setProperty("caseless", "true");

    WikidictAnnotator annotator = new WikidictAnnotator("test", props);
    Annotation mention = new Annotation("barack obama");
    mention.set(CoreAnnotations.TextAnnotation.class, "barack obama");
    mention.set(CoreAnnotations.OriginalTextAnnotation.class, "barack obama");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    Optional<String> result = annotator.link(mention);
    assertTrue(result.isPresent());
    assertEquals("Barack_Obama", result.get());
  }
@Test
  public void testThresholdFilteringExcludesEntry() throws Exception {
    File dictFile = File.createTempFile("wikidict", ".tsv");
    PrintWriter writer = new PrintWriter(dictFile, "UTF-8");
    writer.println("USA\tUnited_States\t0.5"); 
    writer.close();

    Properties props = new Properties();
    props.setProperty("wikidict", dictFile.getAbsolutePath());
    props.setProperty("threshold", "0.8");

    WikidictAnnotator annotator = new WikidictAnnotator("test", props);
    Annotation mention = new Annotation("USA");
    mention.set(CoreAnnotations.TextAnnotation.class, "USA");
    mention.set(CoreAnnotations.OriginalTextAnnotation.class, "USA");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");

    Optional<String> result = annotator.link(mention);
    assertFalse(result.isPresent());
  }
@Test
  public void testNormalizeTimexWithTime() {
    String value = "2023-03-01T12:00:00";
    String result = WikidictAnnotator.normalizeTimex(value);
    assertEquals("2023-03-01", result);
  }
@Test
  public void testNormalizeTimexWithPresent() {
    String value = "PRESENT";
    String result = WikidictAnnotator.normalizeTimex(value);
    assertEquals("PRESENT", result);
  }
@Test
  public void testLinkOrdinalWithNumericValue() throws Exception {
    File dictFile = File.createTempFile("wikidict", ".tsv");
    PrintWriter writer = new PrintWriter(dictFile, "UTF-8");
    writer.println("3rd\tOrdinal_Three\t1.0");
    writer.close();

    Properties props = new Properties();
    props.setProperty("wikidict", dictFile.getAbsolutePath());

    WikidictAnnotator annotator = new WikidictAnnotator("test", props);
    Annotation mention = new Annotation("3rd");
    mention.set(CoreAnnotations.TextAnnotation.class, "3rd");
    mention.set(CoreAnnotations.OriginalTextAnnotation.class, "3rd");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORDINAL");
    mention.set(CoreAnnotations.NumericValueAnnotation.class, 3.0);

    Optional<String> result = annotator.link(mention);
    assertTrue(result.isPresent());
    assertEquals("3.0", result.get());
  }
@Test
  public void testLinkDateWithValidTimex() throws Exception {
    File dictFile = File.createTempFile("wikidict", ".tsv");
    PrintWriter writer = new PrintWriter(dictFile, "UTF-8");
    writer.close();

    Properties props = new Properties();
    props.setProperty("wikidict", dictFile.getAbsolutePath());

    WikidictAnnotator annotator = new WikidictAnnotator("test", props);
    Annotation mention = new Annotation("March 3rd");
    mention.set(CoreAnnotations.TextAnnotation.class, "March 3rd");
    mention.set(CoreAnnotations.OriginalTextAnnotation.class, "March 3rd");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");

//     Timex timex = new Timex(0);
//     timex.setValue("2023-03-03T00:00:00");
//     mention.set(TimeAnnotations.TimexAnnotation.class, timex);

    Optional<String> result = annotator.link(mention);
    assertTrue(result.isPresent());
    assertEquals("2023-03-03", result.get());
  }
@Test
  public void testLinkPureNumberAsString() throws Exception {
    File dictFile = File.createTempFile("wikidict", ".tsv");
    PrintWriter writer = new PrintWriter(dictFile, "UTF-8");
    writer.close();

    Properties props = new Properties();
    props.setProperty("wikidict", dictFile.getAbsolutePath());

    WikidictAnnotator annotator = new WikidictAnnotator("test", props);
    Annotation mention = new Annotation("123.45");
    mention.set(CoreAnnotations.TextAnnotation.class, "123.45");
    mention.set(CoreAnnotations.OriginalTextAnnotation.class, "123.45");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "NUMBER");

    Optional<String> result = annotator.link(mention);
    assertTrue(result.isPresent());
    assertEquals("123.45", result.get());
  }
@Test
  public void testLinkReturnsEmptyOnUnknownNER() throws Exception {
    File dictFile = File.createTempFile("wikidict", ".tsv");
    PrintWriter writer = new PrintWriter(dictFile, "UTF-8");
    writer.close();

    Properties props = new Properties();
    props.setProperty("wikidict", dictFile.getAbsolutePath());

    WikidictAnnotator annotator = new WikidictAnnotator("test", props);
    Annotation mention = new Annotation("UnknownEntity");
    mention.set(CoreAnnotations.TextAnnotation.class, "UnknownEntity");
    mention.set(CoreAnnotations.OriginalTextAnnotation.class, "UnknownEntity");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORG");

    Optional<String> result = annotator.link(mention);
    assertFalse(result.isPresent());
  }
@Test
  public void testRequirementsSatisfiedIncludesWikipediaAnnotation() throws Exception {
    File dictFile = File.createTempFile("wikidict", ".tsv");
    PrintWriter writer = new PrintWriter(dictFile, "UTF-8");
    writer.close();

    Properties props = new Properties();
    props.setProperty("wikidict", dictFile.getAbsolutePath());

    WikidictAnnotator annotator = new WikidictAnnotator("test", props);
    Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> requirements = annotator.requirementsSatisfied();
    assertTrue(requirements.contains(CoreAnnotations.WikipediaEntityAnnotation.class));
  }
@Test
  public void testRequiresAnnotationsAreReturned() throws Exception {
    File dictFile = File.createTempFile("wikidict", ".tsv");
    PrintWriter writer = new PrintWriter(dictFile, "UTF-8");
    writer.close();

    Properties props = new Properties();
    props.setProperty("wikidict", dictFile.getAbsolutePath());

    WikidictAnnotator annotator = new WikidictAnnotator("test", props);
    Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> requires = annotator.requires();

    assertTrue(requires.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(requires.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(requires.contains(CoreAnnotations.SentencesAnnotation.class));
    assertTrue(requires.contains(CoreAnnotations.OriginalTextAnnotation.class));
    assertTrue(requires.contains(CoreAnnotations.MentionsAnnotation.class));
  }
@Test
  public void testDoOneFailedSentenceDoesNothing() throws Exception {
    File dictFile = File.createTempFile("wikidict", ".tsv");
    PrintWriter writer = new PrintWriter(dictFile, "UTF-8");
    writer.close();

    Properties props = new Properties();
    props.setProperty("wikidict", dictFile.getAbsolutePath());

    WikidictAnnotator annotator = new WikidictAnnotator("test", props);

    CoreMap sentence = new Annotation("This is a test sentence.");
    Annotation annotation = new Annotation("Document");
    annotator.doOneFailedSentence(annotation, sentence);
    
  }
@Test
  public void testNThreadsReturnsConfiguredValue() throws Exception {
    File dictFile = File.createTempFile("wikidict", ".tsv");
    PrintWriter writer = new PrintWriter(dictFile, "UTF-8");
    writer.close();

    Properties props = new Properties();
    props.setProperty("wikidict", dictFile.getAbsolutePath());
    props.setProperty("threads", "3");

    WikidictAnnotator annotator = new WikidictAnnotator("test", props);
    assertEquals(3, annotator.nThreads());
  }
@Test
  public void testMaxTimeReturnsMinusOne() throws Exception {
    File dictFile = File.createTempFile("wikidict", ".tsv");
    PrintWriter writer = new PrintWriter(dictFile, "UTF-8");
    writer.close();

    Properties props = new Properties();
    props.setProperty("wikidict", dictFile.getAbsolutePath());

    WikidictAnnotator annotator = new WikidictAnnotator("test", props);
    assertEquals(-1L, annotator.maxTime());
  }
@Test
public void testLinkMissingNERTag() throws Exception {
  File dictFile = File.createTempFile("wikidict_missingNER", ".tsv");
  PrintWriter writer = new PrintWriter(dictFile, "UTF-8");
  writer.println("EntityX\tPageX\t0.9");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());

  WikidictAnnotator annotator = new WikidictAnnotator("test", props);

  Annotation mention = new Annotation("EntityX");
  mention.set(CoreAnnotations.TextAnnotation.class, "EntityX");
  mention.set(CoreAnnotations.OriginalTextAnnotation.class, "EntityX");
  

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkMissingTextAnnotationFallsBackToOriginal() throws Exception {
  File dictFile = File.createTempFile("wikidict_fallback", ".tsv");
  PrintWriter writer = new PrintWriter(dictFile, "UTF-8");
  writer.println("Foo\tFoo_Link\t0.6");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());

  WikidictAnnotator annotator = new WikidictAnnotator("test", props);

  Annotation mention = new Annotation("Foo");
  
  mention.set(CoreAnnotations.OriginalTextAnnotation.class, "Foo");
  mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

  Optional<String> result = annotator.link(mention);
  assertTrue(result.isPresent());
  assertEquals("Foo_Link", result.get());
}
@Test
public void testLinkWithTimexButNullValue() throws Exception {
  File dictFile = File.createTempFile("wikidict_nulltimex", ".tsv");
  PrintWriter writer = new PrintWriter(dictFile, "UTF-8");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());

  WikidictAnnotator annotator = new WikidictAnnotator("test", props);

  Annotation mention = new Annotation("today");
  mention.set(CoreAnnotations.TextAnnotation.class, "today");
  mention.set(CoreAnnotations.OriginalTextAnnotation.class, "today");
  mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");

//   Timex timex = new Timex(0);
//   timex.setValue(null);
//   mention.set(TimeAnnotations.TimexAnnotation.class, timex);

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkWithScoreExactlyAtThreshold() throws Exception {
  File dictFile = File.createTempFile("wikidict_threshold", ".tsv");
  PrintWriter writer = new PrintWriter(dictFile, "UTF-8");
  writer.println("EntityZ\tZ_Link\t0.7");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());
  props.setProperty("threshold", "0.7");

  WikidictAnnotator annotator = new WikidictAnnotator("test", props);

  Annotation mention = new Annotation("EntityZ");
  mention.set(CoreAnnotations.TextAnnotation.class, "EntityZ");
  mention.set(CoreAnnotations.OriginalTextAnnotation.class, "EntityZ");
  mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

  Optional<String> result = annotator.link(mention);
  assertTrue(result.isPresent());
  assertEquals("Z_Link", result.get());
}
@Test
public void testLinkWithNumberDoesNotMatchNumberPattern() throws Exception {
  File dictFile = File.createTempFile("wikidict_numfail", ".tsv");
  PrintWriter writer = new PrintWriter(dictFile, "UTF-8");
  writer.println("12,000\tBig_Number\t1.0");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());

  WikidictAnnotator annotator = new WikidictAnnotator("test", props);

  Annotation mention = new Annotation("12,000");
  mention.set(CoreAnnotations.TextAnnotation.class, "12,000");
  mention.set(CoreAnnotations.OriginalTextAnnotation.class, "12,000");
  mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "NUMBER");

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkWithUnsupportedNERTag() throws Exception {
  File dictFile = File.createTempFile("wikidict_unsup", ".tsv");
  PrintWriter writer = new PrintWriter(dictFile, "UTF-8");
  writer.println("WeirdEntity\tUnknown_Link\t1.0");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());

  WikidictAnnotator annotator = new WikidictAnnotator("test", props);

  Annotation mention = new Annotation("WeirdEntity");
  mention.set(CoreAnnotations.TextAnnotation.class, "WeirdEntity");
  mention.set(CoreAnnotations.OriginalTextAnnotation.class, "WeirdEntity");
  mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "QUANTITY"); 

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testDictionaryReusesStringLinkOptimization() throws Exception {
  File dictFile = File.createTempFile("wikidict_reuse", ".tsv");
  PrintWriter writer = new PrintWriter(dictFile, "UTF-8");
  writer.println("A1\tShared_Link\t1.0");
  writer.println("A2\tShared_Link\t0.8");
  writer.println("A3\tShared_Link\t0.8");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());

  WikidictAnnotator annotator = new WikidictAnnotator("test", props);

  Annotation mention = new Annotation("A2");
  mention.set(CoreAnnotations.TextAnnotation.class, "A2");
  mention.set(CoreAnnotations.OriginalTextAnnotation.class, "A2");
  mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORG");

  Optional<String> result = annotator.link(mention);
  assertTrue(result.isPresent());
  assertEquals("Shared_Link", result.get());
}
@Test
public void testLinkReturnsEmptyWhenSurfaceFormIsMissing() throws Exception {
  File dictFile = File.createTempFile("wikidict_missing_surfaceform", ".tsv");
  PrintWriter writer = new PrintWriter(dictFile, "UTF-8");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());

  WikidictAnnotator annotator = new WikidictAnnotator("test", props);

  Annotation mention = new Annotation("missing");
  
  mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testNormalizeTimexFutureIsIgnored() {
  String value = "FUTURE";
  String result = WikidictAnnotator.normalizeTimex(value);
  assertEquals("FUTURE", result);
}
@Test
public void testLinkWithCaselessFalseAndMismatchedCase() throws Exception {
  File dictFile = File.createTempFile("wikidict_case", ".tsv");
  PrintWriter writer = new PrintWriter(dictFile, "UTF-8");
  writer.println("EntityName\tCapital_Link\t1.0");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());
  props.setProperty("caseless", "false");

  WikidictAnnotator annotator = new WikidictAnnotator("test", props);

  Annotation mention = new Annotation("entityname");
  mention.set(CoreAnnotations.TextAnnotation.class, "entityname");
  mention.set(CoreAnnotations.OriginalTextAnnotation.class, "entityname");
  mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkCaselessTrueMatchesLowerCaseDictionary() throws Exception {
  File dictFile = File.createTempFile("wikidict_lower", ".tsv");
  PrintWriter writer = new PrintWriter(dictFile, "UTF-8");
  writer.println("entityname\tLower_Link\t1.0");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());
  props.setProperty("caseless", "true");

  WikidictAnnotator annotator = new WikidictAnnotator("test", props);

  Annotation mention = new Annotation("ENTITYNAME");
  mention.set(CoreAnnotations.TextAnnotation.class, "ENTITYNAME");
  mention.set(CoreAnnotations.OriginalTextAnnotation.class, "ENTITYNAME");
  mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");

  Optional<String> result = annotator.link(mention);
  assertTrue(result.isPresent());
  assertEquals("Lower_Link", result.get());
}
@Test
public void testLinkWithAlphaNumericFailsNumericPatternMatch() throws Exception {
  File dictFile = File.createTempFile("wikidict_alpha", ".tsv");
  PrintWriter writer = new PrintWriter(dictFile, "UTF-8");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());

  WikidictAnnotator annotator = new WikidictAnnotator("test", props);

  Annotation mention = new Annotation("12abc");
  mention.set(CoreAnnotations.TextAnnotation.class, "12abc");
  mention.set(CoreAnnotations.OriginalTextAnnotation.class, "12abc");
  mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "NUMBER");

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkWithHighThresholdFiltersAll() throws Exception {
  File dictFile = File.createTempFile("wikidict_highthreshold", ".tsv");
  PrintWriter writer = new PrintWriter(dictFile, "UTF-8");
  writer.println("Apple\tApple_Inc\t0.95");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());
  props.setProperty("threshold", "1.0");

  WikidictAnnotator annotator = new WikidictAnnotator("test", props);

  Annotation mention = new Annotation("Apple");
  mention.set(CoreAnnotations.TextAnnotation.class, "Apple");
  mention.set(CoreAnnotations.OriginalTextAnnotation.class, "Apple");
  mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testDictionaryLineMissingScoreFieldIgnored() throws Exception {
  File dictFile = File.createTempFile("wikidict_badline", ".tsv");
  PrintWriter writer = new PrintWriter(dictFile, "UTF-8");
  writer.println("BadEntry\tMissingScore"); 
  writer.println("GoodEntry\tGood_Link\t0.9");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());
  props.setProperty("threshold", "0.1");

  WikidictAnnotator annotator = new WikidictAnnotator("test", props);

  Annotation mention = new Annotation("GoodEntry");
  mention.set(CoreAnnotations.TextAnnotation.class, "GoodEntry");
  mention.set(CoreAnnotations.OriginalTextAnnotation.class, "GoodEntry");
  mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");

  Optional<String> result = annotator.link(mention);
  assertTrue(result.isPresent());
  assertEquals("Good_Link", result.get());
}
@Test
public void testLinkMentionWithNullTextReturnsEmpty() throws Exception {
  File dictFile = File.createTempFile("wikidict_null", ".tsv");
  PrintWriter writer = new PrintWriter(dictFile, "UTF-8");
  writer.println("John\tJohn_Page\t1.0");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());

  WikidictAnnotator annotator = new WikidictAnnotator("test", props);

  Annotation mention = new Annotation("missing");
  mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test(expected = RuntimeException.class)
public void testDictionaryFileNotFoundThrowsException() {
  Properties props = new Properties();
  props.setProperty("wikidict", "nonexistent_file.tsv");

  WikidictAnnotator annotator = new WikidictAnnotator("test", props);
}
@Test
public void testTabPrefixedLinesAreIgnored() throws Exception {
  File file = File.createTempFile("wikidict_tabs", ".tsv");
  PrintWriter writer = new PrintWriter(file, "UTF-8");
  writer.println("\tThis line should be ignored");
  writer.println("ValidEntity\tLinky\t1.0");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", file.getAbsolutePath());

  WikidictAnnotator annotator = new WikidictAnnotator("test", props);

  Annotation mention = new Annotation("ValidEntity");
  mention.set(CoreAnnotations.TextAnnotation.class, "ValidEntity");
  mention.set(CoreAnnotations.OriginalTextAnnotation.class, "ValidEntity");
  mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

  Optional<String> result = annotator.link(mention);
  assertTrue(result.isPresent());
  assertEquals("Linky", result.get());
}
@Test
public void testLinkWithNERTagOIgnoreEvenIfInDict() throws Exception {
  File file = File.createTempFile("wikidict_o_tag", ".tsv");
  PrintWriter writer = new PrintWriter(file, "UTF-8");
  writer.println("Untagged\tLinkPage\t0.9");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", file.getAbsolutePath());

  WikidictAnnotator annotator = new WikidictAnnotator("test", props);

  Annotation mention = new Annotation("Untagged");
  mention.set(CoreAnnotations.TextAnnotation.class, "Untagged");
  mention.set(CoreAnnotations.OriginalTextAnnotation.class, "Untagged");
  mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkWithMultiplePeriodsStillParsesAsNumber() throws Exception {
  File file = File.createTempFile("wikidict_numbers", ".tsv");
  PrintWriter writer = new PrintWriter(file, "UTF-8");
  writer.println("1.2.3\tVersion_Link\t1.0"); 
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", file.getAbsolutePath());

  WikidictAnnotator annotator = new WikidictAnnotator("test", props);

  Annotation mention = new Annotation("1.2.3");
  mention.set(CoreAnnotations.TextAnnotation.class, "1.2.3");
  mention.set(CoreAnnotations.OriginalTextAnnotation.class, "1.2.3");
  mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "NUMBER");

  Optional<String> result = annotator.link(mention);
  assertTrue(result.isPresent());
  assertEquals("1.2.3", result.get()); 
}
@Test
public void testDateTimexWithSpecialValueIgnored() throws Exception {
  File file = File.createTempFile("wikidict_nodate", ".tsv");
  PrintWriter writer = new PrintWriter(file, "UTF-8");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", file.getAbsolutePath());

  WikidictAnnotator annotator = new WikidictAnnotator("test", props);

  Annotation mention = new Annotation("Yesterday");
  mention.set(CoreAnnotations.TextAnnotation.class, "Yesterday");
  mention.set(CoreAnnotations.OriginalTextAnnotation.class, "Yesterday");
  mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");

//   Timex timex = new Timex(0);
//   timex.setValue("PAST_REF");
//   mention.set(TimeAnnotations.TimexAnnotation.class, timex);

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testDoOneSentenceMentionWithoutTokens() throws Exception {
  File file = File.createTempFile("wikidict_sent", ".tsv");
  PrintWriter writer = new PrintWriter(file, "UTF-8");
  writer.println("Stanford\tStanford_University\t1.0");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", file.getAbsolutePath());

  WikidictAnnotator annotator = new WikidictAnnotator("test", props);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "Stanford");

  Annotation mention = new Annotation("Stanford");
  mention.set(CoreAnnotations.TextAnnotation.class, "Stanford");
  mention.set(CoreAnnotations.OriginalTextAnnotation.class, "Stanford");
  mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");

  List<CoreLabel> sentenceTokens = new ArrayList<>();
  sentenceTokens.add(token);

  CoreMap sentence = new Annotation("sentence");
  sentence.set(CoreAnnotations.TokensAnnotation.class, sentenceTokens);
  sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));

  Annotation annotation = new Annotation("doc");
  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  annotator.doOneSentence(annotation, sentence);

  for (CoreLabel t : sentenceTokens) {
    String wikiLink = t.get(CoreAnnotations.WikipediaEntityAnnotation.class);
    assertEquals("O", wikiLink);
  }
}
@Test
public void testMentionWithNullTimexAnnotationReturnsEmpty() throws Exception {
  File dictFile = File.createTempFile("wikidict_null_timex", ".tsv");
  PrintWriter writer = new PrintWriter(dictFile, "UTF-8");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());

  WikidictAnnotator annotator = new WikidictAnnotator("test", props);

  Annotation mention = new Annotation("Tomorrow");
  mention.set(CoreAnnotations.TextAnnotation.class, "Tomorrow");
  mention.set(CoreAnnotations.OriginalTextAnnotation.class, "Tomorrow");
  mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");

  mention.set(TimeAnnotations.TimexAnnotation.class, null); 

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testMentionWithTimexHasValueButNERIsNotTimeLike() throws Exception {
  File dictFile = File.createTempFile("wikidict_non_date_ner", ".tsv");
  PrintWriter writer = new PrintWriter(dictFile, "UTF-8");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());

  WikidictAnnotator annotator = new WikidictAnnotator("test", props);

  Annotation mention = new Annotation("2023-05-10T08:00:00");
  mention.set(CoreAnnotations.TextAnnotation.class, "2023-05-10T08:00:00");
  mention.set(CoreAnnotations.OriginalTextAnnotation.class, "2023-05-10T08:00:00");
  mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");

//   Timex timex = new Timex(0);
//   timex.setValue("2023-05-10T08:00:00");
//   mention.set(TimeAnnotations.TimexAnnotation.class, timex);

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testMentionWithNullNumericValueReturnsEmpty() throws Exception {
  File dictFile = File.createTempFile("wikidict_null_numeric", ".tsv");
  PrintWriter writer = new PrintWriter(dictFile, "UTF-8");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());

  WikidictAnnotator annotator = new WikidictAnnotator("test", props);

  Annotation mention = new Annotation("3rd");
  mention.set(CoreAnnotations.TextAnnotation.class, "3rd");
  mention.set(CoreAnnotations.OriginalTextAnnotation.class, "3rd");
  mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORDINAL");
  mention.set(CoreAnnotations.NumericValueAnnotation.class, null);

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testEmptyStringSurfaceFormReturnsEmpty() throws Exception {
  File dictFile = File.createTempFile("wikidict_empty_string", ".tsv");
  PrintWriter writer = new PrintWriter(dictFile, "UTF-8");
  writer.println(" \tWhitespace_Link\t1.0");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());
  props.setProperty("caseless", "true");

  WikidictAnnotator annotator = new WikidictAnnotator("test", props);

  Annotation mention = new Annotation(" ");
  mention.set(CoreAnnotations.TextAnnotation.class, " ");
  mention.set(CoreAnnotations.OriginalTextAnnotation.class, " ");
  mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");

  Optional<String> result = annotator.link(mention);
  assertTrue(result.isPresent());
  assertEquals("Whitespace_Link", result.get());
}
@Test
public void testDictionaryWithLineTooShortSkipsParsingGracefully() throws Exception {
  File dictFile = File.createTempFile("wikidict_short_line", ".tsv");
  PrintWriter writer = new PrintWriter(dictFile, "UTF-8");
  writer.println("onlyonefield");
  writer.println("Entity\tValid_Link\t1.0");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());
  props.setProperty("threshold", "0.0");

  WikidictAnnotator annotator = new WikidictAnnotator("test", props);

  Annotation mention = new Annotation("Entity");
  mention.set(CoreAnnotations.TextAnnotation.class, "Entity");
  mention.set(CoreAnnotations.OriginalTextAnnotation.class, "Entity");
  mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");

  Optional<String> result = annotator.link(mention);
  assertTrue(result.isPresent());
  assertEquals("Valid_Link", result.get());
}
@Test
public void testLinkWithTextOnlyNoNERReturnsEmpty() throws Exception {
  File dictFile = File.createTempFile("wikidict_no_tag", ".tsv");
  PrintWriter writer = new PrintWriter(dictFile, "UTF-8");
  writer.println("NoTag\tSome_Link\t1.0");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());

  WikidictAnnotator annotator = new WikidictAnnotator("test", props);

  Annotation mention = new Annotation("NoTag");
  mention.set(CoreAnnotations.TextAnnotation.class, "NoTag");
  mention.set(CoreAnnotations.OriginalTextAnnotation.class, "NoTag");
  

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testMentionTextNotInDictReturnsEmpty() throws Exception {
  File dictFile = File.createTempFile("wikidict_dictmiss", ".tsv");
  PrintWriter writer = new PrintWriter(dictFile, "UTF-8");
  writer.println("Apple\tApple_Inc\t1.0");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());

  WikidictAnnotator annotator = new WikidictAnnotator("test", props);

  Annotation mention = new Annotation("Orange");
  mention.set(CoreAnnotations.TextAnnotation.class, "Orange");
  mention.set(CoreAnnotations.OriginalTextAnnotation.class, "Orange");
  mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testSurfaceFormWithTabIsNotIgnored() throws Exception {
  File dictFile = File.createTempFile("wikidict_surface_tab", ".tsv");
  PrintWriter writer = new PrintWriter(dictFile, "UTF-8");
  writer.println("A\tTabbed\t1.0");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());
  props.setProperty("caseless", "false");

  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  Annotation mention = new Annotation("A");
  mention.set(CoreAnnotations.TextAnnotation.class, "A");
  mention.set(CoreAnnotations.OriginalTextAnnotation.class, "A");
  mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "MISC");

  Optional<String> result = annotator.link(mention);
  assertTrue(result.isPresent());
  assertEquals("Tabbed", result.get());
}
@Test
public void testScoreJustBelowThresholdIsDiscarded() throws Exception {
  File dictFile = File.createTempFile("wikidict_threshold_edge", ".tsv");
  PrintWriter writer = new PrintWriter(dictFile, "UTF-8");
  writer.println("LowScorer\tDiscarded_Link\t0.099");
  writer.println("Another\tSaved_Link\t0.100");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());
  props.setProperty("threshold", "0.1");

  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  Annotation mention = new Annotation("LowScorer");
  mention.set(CoreAnnotations.TextAnnotation.class, "LowScorer");
  mention.set(CoreAnnotations.OriginalTextAnnotation.class, "LowScorer");
  mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());

  Annotation mention2 = new Annotation("Another");
  mention2.set(CoreAnnotations.TextAnnotation.class, "Another");
  mention2.set(CoreAnnotations.OriginalTextAnnotation.class, "Another");
  mention2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

  Optional<String> result2 = annotator.link(mention2);
  assertTrue(result2.isPresent());
  assertEquals("Saved_Link", result2.get());
}
@Test
public void testLinkWithTimexValueAsEmptyString() throws Exception {
  File dictFile = File.createTempFile("wikidict_timex_empty", ".tsv");
  PrintWriter writer = new PrintWriter(dictFile, "UTF-8");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());

  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  Annotation mention = new Annotation("now");
  mention.set(CoreAnnotations.TextAnnotation.class, "now");
  mention.set(CoreAnnotations.OriginalTextAnnotation.class, "now");
  mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");

//   Timex timex = new Timex(0);
//   timex.setValue("");
//   mention.set(TimeAnnotations.TimexAnnotation.class, timex);

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkMentionWithUppercaseNERTagMatchesCaseSensitiveDictionary() throws Exception {
  File dictFile = File.createTempFile("wikidict_upper_ner", ".tsv");
  PrintWriter writer = new PrintWriter(dictFile, "UTF-8");
  writer.println("ALICE\tPage_ALICE\t1.0");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());
  props.setProperty("caseless", "false");

  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  Annotation mention = new Annotation("ALICE");
  mention.set(CoreAnnotations.TextAnnotation.class, "ALICE");
  mention.set(CoreAnnotations.OriginalTextAnnotation.class, "ALICE");
  mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

  Optional<String> result = annotator.link(mention);
  assertTrue(result.isPresent());
  assertEquals("Page_ALICE", result.get());
}
@Test
public void testDictionaryEntryLinkReusePathWithManyDuplicates() throws Exception {
  File dictFile = File.createTempFile("wikidict_reuse_ratio", ".tsv");
  PrintWriter writer = new PrintWriter(dictFile, "UTF-8");
  writer.println("A\tLink1\t1.0");
  writer.println("B\tLink1\t1.0");
  writer.println("C\tLink1\t1.0");
  writer.println("D\tLink1\t1.0");
  writer.println("E\tLink1\t1.0");
  writer.println("F\tLink1\t1.0");
  writer.println("G\tLink1\t1.0");
  writer.println("H\tLink2\t1.0");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());

  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  Annotation mention = new Annotation("H");
  mention.set(CoreAnnotations.TextAnnotation.class, "H");
  mention.set(CoreAnnotations.OriginalTextAnnotation.class, "H");
  mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORG");

  Optional<String> result = annotator.link(mention);
  assertTrue(result.isPresent());
  assertEquals("Link2", result.get());
}
@Test
public void testNormalizeTimexWithTButEndsAtTPosition() {
  String input = "2023-04-01T";
  String expected = "2023-04-01";
  String result = WikidictAnnotator.normalizeTimex(input);
  assertEquals(expected, result);
}
@Test
public void testLinkWithNonTemporalTagAndNoDictionaryEntryReturnsEmpty() throws Exception {
  File dictFile = File.createTempFile("wikidict_no_entry_org", ".tsv");
  PrintWriter writer = new PrintWriter(dictFile, "UTF-8");
  writer.println("CompanyX\tPageX\t1.0");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());
  props.setProperty("caseless", "false");

  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  Annotation mention = new Annotation("MissingCompany");
  mention.set(CoreAnnotations.TextAnnotation.class, "MissingCompany");
  mention.set(CoreAnnotations.OriginalTextAnnotation.class, "MissingCompany");
  mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkWhenOriginalTextAnnotationIsNullAndTextAnnotationExists() throws Exception {
  File dictFile = File.createTempFile("wikidict_original_null", ".tsv");
  PrintWriter writer = new PrintWriter(dictFile, "UTF-8");
  writer.println("Hello\tGreeting_Page\t1.0");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());
  props.setProperty("caseless", "false");

  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  Annotation mention = new Annotation("Hello");
  mention.set(CoreAnnotations.TextAnnotation.class, "Hello");
  mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "MISC");

  Optional<String> result = annotator.link(mention);
  assertTrue(result.isPresent());
  assertEquals("Greeting_Page", result.get());
}
@Test
public void testLinkWithSurfaceFormExactlyMatchesNumberRegexAndHasDictionaryEntry() throws Exception {
  File dictFile = File.createTempFile("wikidict_numeric_then_dict", ".tsv");
  PrintWriter writer = new PrintWriter(dictFile, "UTF-8");
  writer.println("99\tNinetyNine_Page\t1.0");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());
  props.setProperty("caseless", "false");

  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  Annotation mention = new Annotation("99");
  mention.set(CoreAnnotations.TextAnnotation.class, "99");
  mention.set(CoreAnnotations.OriginalTextAnnotation.class, "99");
  mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "CARDINAL");

  Optional<String> result = annotator.link(mention);
  assertTrue(result.isPresent());
  assertEquals("99", result.get()); 
}
@Test
public void testLinkOrdinalTagWithNonNumericValueReturnsEmpty() throws Exception {
  File dictFile = File.createTempFile("wikidict_ordinal_non_numeric", ".tsv");
  PrintWriter writer = new PrintWriter(dictFile, "UTF-8");
  writer.println("First\tFirst_Link\t1.0");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());
  props.setProperty("caseless", "true");

  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  Annotation mention = new Annotation("First");
  mention.set(CoreAnnotations.TextAnnotation.class, "First");
  mention.set(CoreAnnotations.OriginalTextAnnotation.class, "First");
  mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORDINAL");
  

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkWhenDictionaryLinkIsReusedMultipleTimes() throws Exception {
  File dictFile = File.createTempFile("wikidict_duplicate_links", ".tsv");
  PrintWriter writer = new PrintWriter(dictFile, "UTF-8");
  writer.println("A\tLink123\t1.0");
  writer.println("B\tLink123\t0.9");
  writer.println("C\tLink123\t0.8");
  writer.println("D\tLink456\t0.8");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());
  props.setProperty("caseless", "true");

  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  Annotation mention = new Annotation("C");
  mention.set(CoreAnnotations.TextAnnotation.class, "C");
  mention.set(CoreAnnotations.OriginalTextAnnotation.class, "C");
  mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "MISC");

  Optional<String> result = annotator.link(mention);
  assertTrue(result.isPresent());
  assertEquals("Link123", result.get());
}
@Test
public void testLinkWhenTextIsOnlySignificantDigitsFloatPattern() throws Exception {
  File dictFile = File.createTempFile("wikidict_float_significant", ".tsv");
  PrintWriter writer = new PrintWriter(dictFile, "UTF-8");
  writer.println("0.001\tScientific_Link\t1.0");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());

  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  Annotation mention = new Annotation("0.001");
  mention.set(CoreAnnotations.TextAnnotation.class, "0.001");
  mention.set(CoreAnnotations.OriginalTextAnnotation.class, "0.001");
  mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "NUMBER");

  Optional<String> result = annotator.link(mention);
  assertTrue(result.isPresent());
  assertEquals("0.001", result.get());
}
@Test
public void testNormalizeTimexWithSingleTCharacterOnly() {
  String input = "T";
  String result = WikidictAnnotator.normalizeTimex(input);
  assertEquals("", result);
}
@Test
public void testLinkWithSentenceAnnotationButNoMentionList() throws Exception {
  File dictFile = File.createTempFile("wikidict_empty_mentions", ".tsv");
  PrintWriter writer = new PrintWriter(dictFile, "UTF-8");
  writer.println("Foo\tFoo_Page\t1.0");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());

  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "Foo");
  token.set(CoreAnnotations.OriginalTextAnnotation.class, "Foo");

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);

  CoreMap sentence = new Annotation("sentence");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
  sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>()); 

  Annotation annotation = new Annotation("doc");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.doOneSentence(annotation, sentence);

  String wikiAnnotation = token.get(CoreAnnotations.WikipediaEntityAnnotation.class);
  assertEquals("O", wikiAnnotation);
}
@Test
public void testDictionaryWithExtraTabsInLineStillParsesFirstThreeColumns() throws Exception {
  File dictFile = File.createTempFile("wikidict_extracolumns", ".tsv");
  PrintWriter writer = new PrintWriter(dictFile, "UTF-8");
  writer.println("Extra\tLinkExtra\t0.9\tignored1\tignored2");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());

  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  Annotation mention = new Annotation("Extra");
  mention.set(CoreAnnotations.TextAnnotation.class, "Extra");
  mention.set(CoreAnnotations.OriginalTextAnnotation.class, "Extra");
  mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

  Optional<String> result = annotator.link(mention);
  assertTrue(result.isPresent());
  assertEquals("LinkExtra", result.get());
} 
}
