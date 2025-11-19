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
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.PropertiesUtils;
import edu.stanford.nlp.util.StringUtils;
import junit.framework.TestCase;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class WikidictAnnotator_1_GPTLLMTest { 

 @Test
  public void testLinkExactMatch() throws Exception {
    File dictFile = File.createTempFile("wikidict", ".tsv");
    dictFile.deleteOnExit();
    BufferedWriter writer = new BufferedWriter(new FileWriter(dictFile));
    writer.write("Barack Obama\tBarack_Obama\t0.9\n");
    writer.close();

    Properties props = new Properties();
    props.setProperty("wikidict", dictFile.getAbsolutePath());

    WikidictAnnotator annotator = new WikidictAnnotator(props);

    CoreMap mention = mock(CoreMap.class);
    when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Barack Obama");
    when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
    when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");

    Optional<String> result = annotator.link(mention);
    assertTrue(result.isPresent());
    assertEquals("Barack_Obama", result.get());
  }
@Test
  public void testLinkCaseInsensitiveMatch() throws Exception {
    File dictFile = File.createTempFile("wikidict", ".tsv");
    dictFile.deleteOnExit();
    BufferedWriter writer = new BufferedWriter(new FileWriter(dictFile));
    writer.write("Google\tGoogle_Page\t0.8\n");
    writer.close();

    Properties props = new Properties();
    props.setProperty("wikidict", dictFile.getAbsolutePath());
    props.setProperty("caseless", "true");

    WikidictAnnotator annotator = new WikidictAnnotator(props);

    CoreMap mention = mock(CoreMap.class);
    when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("google");
    when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
    when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORGANIZATION");

    Optional<String> result = annotator.link(mention);
    assertTrue(result.isPresent());
    assertEquals("Google_Page", result.get());
  }
@Test
  public void testLinkOrdinalNumericValue() throws Exception {
    Properties props = new Properties();
    WikidictAnnotator annotator = new WikidictAnnotator(props);

    CoreMap mention = mock(CoreMap.class);
    when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("3rd");
    when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
    when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORDINAL");
    when(mention.get(CoreAnnotations.NumericValueAnnotation.class)).thenReturn(3);

    Optional<String> result = annotator.link(mention);
    assertTrue(result.isPresent());
    assertEquals("3", result.get());
  }
@Test
  public void testLinkTimexWithTimePart() throws Exception {
    Properties props = new Properties();
    WikidictAnnotator annotator = new WikidictAnnotator(props);

    Timex timex = mock(Timex.class);
    when(timex.value()).thenReturn("2023-07-21T00:00");

    CoreMap mention = mock(CoreMap.class);
    when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("July 21st");
    when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
    when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("DATE");
    when(mention.get(TimeAnnotations.TimexAnnotation.class)).thenReturn(timex);

    Optional<String> result = annotator.link(mention);
    assertTrue(result.isPresent());
    assertEquals("2023-07-21", result.get());
  }
@Test
  public void testLinkTimexSpecialPresentValue() throws Exception {
    Properties props = new Properties();
    WikidictAnnotator annotator = new WikidictAnnotator(props);

    Timex timex = mock(Timex.class);
    when(timex.value()).thenReturn("PRESENT");

    CoreMap mention = mock(CoreMap.class);
    when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Now");
    when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
    when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("DATE");
    when(mention.get(TimeAnnotations.TimexAnnotation.class)).thenReturn(timex);

    Optional<String> result = annotator.link(mention);
    assertFalse(result.isPresent());
  }
@Test
  public void testLinkNumberPatternDirectMatch() throws Exception {
    Properties props = new Properties();
    WikidictAnnotator annotator = new WikidictAnnotator(props);

    CoreMap mention = mock(CoreMap.class);
    when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("42.0");
    when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
    when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("NUMBER");

    Optional<String> result = annotator.link(mention);
    assertTrue(result.isPresent());
    assertEquals("42.0", result.get());
  }
@Test
  public void testLinkSurfaceFormNotFoundReturnsEmpty() throws Exception {
    File dictFile = File.createTempFile("wikidict", ".tsv");
    dictFile.deleteOnExit();
    BufferedWriter writer = new BufferedWriter(new FileWriter(dictFile));
    writer.write("Known\tKnown_Link\t0.99\n");
    writer.close();

    Properties props = new Properties();
    props.setProperty("wikidict", dictFile.getAbsolutePath());

    WikidictAnnotator annotator = new WikidictAnnotator(props);

    CoreMap mention = mock(CoreMap.class);
    when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Unknown");
    when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
    when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORGANIZATION");

    Optional<String> result = annotator.link(mention);
    assertFalse(result.isPresent());
  }
@Test
  public void testNormalizeTimexWithTReturnsShortenedValue() {
    String result = WikidictAnnotator.normalizeTimex("2001-09-11T00:00");
    assertEquals("2001-09-11", result);
  }
@Test
  public void testNormalizeTimexWithoutTReturnsOriginal() {
    String input = "2001-09-11";
    String result = WikidictAnnotator.normalizeTimex(input);
    assertEquals("2001-09-11", result);
  }
@Test
  public void testNormalizeTimexPresentReturnsUnchanged() {
    String input = "PRESENT";
    String result = WikidictAnnotator.normalizeTimex(input);
    assertEquals("PRESENT", result);
  }
@Test
  public void testDoOneSentenceWithMatchedEntity() throws Exception {
    File dictFile = File.createTempFile("wikidict", ".tsv");
    dictFile.deleteOnExit();
    BufferedWriter writer = new BufferedWriter(new FileWriter(dictFile));
    writer.write("Tesla\tTesla_Motors\t0.95\n");
    writer.close();

    Properties props = new Properties();
    props.setProperty("wikidict", dictFile.getAbsolutePath());

    WikidictAnnotator annotator = new WikidictAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Tesla");
    List<CoreLabel> tokenList = new ArrayList<>();
    tokenList.add(token);

    CoreMap mention = mock(CoreMap.class);
    when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Tesla");
    when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
    when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORGANIZATION");
    when(mention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokenList);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokenList);
    when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.singletonList(mention));

    Annotation annotation = new Annotation("Tesla");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.doOneSentence(annotation, sentence);

    assertEquals("Tesla_Motors", token.get(CoreAnnotations.WikipediaEntityAnnotation.class));
  }
@Test
  public void testDoOneSentenceWithNoLinkSetsO() throws Exception {
    Properties props = new Properties();
    WikidictAnnotator annotator = new WikidictAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Alienware");
    List<CoreLabel> tokenList = new ArrayList<>();
    tokenList.add(token);

    CoreMap mention = mock(CoreMap.class);
    when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Alienware");
    when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
    when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("COMPANY");
    when(mention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokenList);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokenList);
    when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.singletonList(mention));

    Annotation annotation = new Annotation("Alienware");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.doOneSentence(annotation, sentence);

    assertEquals("O", token.get(CoreAnnotations.WikipediaEntityAnnotation.class));
  }
@Test
  public void testRequirementsSatisfiedIncludesWikipediaAnnotation() throws Exception {
    Properties props = new Properties();
    WikidictAnnotator annotator = new WikidictAnnotator(props);

    Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
    assertTrue(satisfied.contains(CoreAnnotations.WikipediaEntityAnnotation.class));
  }
@Test
  public void testRequiresIncludesMentionsAndTextAnnotations() throws Exception {
    Properties props = new Properties();
    WikidictAnnotator annotator = new WikidictAnnotator(props);

    Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> requires = annotator.requires();

    assertTrue(requires.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(requires.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(requires.contains(CoreAnnotations.SentencesAnnotation.class));
    assertTrue(requires.contains(CoreAnnotations.MentionsAnnotation.class));
    assertTrue(requires.contains(CoreAnnotations.OriginalTextAnnotation.class));
  }
@Test
  public void testNThreadsRespectsProperty() throws Exception {
    Properties props = new Properties();
    props.setProperty("threads", "4");
    WikidictAnnotator annotator = new WikidictAnnotator(props);
    assertEquals(4, annotator.nThreads());
  }
@Test
  public void testMaxTimeAlwaysReturnsMinusOne() throws Exception {
    Properties props = new Properties();
    WikidictAnnotator annotator = new WikidictAnnotator(props);
    assertEquals(-1L, annotator.maxTime());
  }
@Test
public void testLinkWithOriginalTextOverridesTextAnnotation() throws Exception {
  File dictFile = File.createTempFile("wikidict", ".tsv");
  dictFile.deleteOnExit();
  BufferedWriter writer = new BufferedWriter(new FileWriter(dictFile));
  writer.write("Microsoft Corp\tMicrosoft\t0.95\n");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());

  WikidictAnnotator annotator = new WikidictAnnotator(props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("wrong");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn("Microsoft Corp");
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORGANIZATION");

  Optional<String> result = annotator.link(mention);
  assertTrue(result.isPresent());
  assertEquals("Microsoft", result.get());
}
@Test
public void testLinkWithNullNerReturnsEmptyOptional() throws Exception {
  File dictFile = File.createTempFile("wikidict", ".tsv");
  dictFile.deleteOnExit();
  BufferedWriter writer = new BufferedWriter(new FileWriter(dictFile));
  writer.write("Amazon\tAmazon_(company)\t0.9\n");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());

  WikidictAnnotator annotator = new WikidictAnnotator(props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Amazon");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn(null);

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkTimexWithNullTimexReturnsEmpty() throws Exception {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator(props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("yesterday");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("DATE");
  when(mention.get(TimeAnnotations.TimexAnnotation.class)).thenReturn(null);

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkTimexWithNullTimexValueReturnsEmpty() throws Exception {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator(props);

  Timex mockTimex = mock(Timex.class);
  when(mockTimex.value()).thenReturn(null);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Tomorrow");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("DATE");
  when(mention.get(TimeAnnotations.TimexAnnotation.class)).thenReturn(mockTimex);

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkWithOEntityAndMatchingSurfaceFormReturnsEmpty() throws Exception {
  File dictFile = File.createTempFile("wikidict", ".tsv");
  dictFile.deleteOnExit();
  BufferedWriter writer = new BufferedWriter(new FileWriter(dictFile));
  writer.write("Paris\tParis_France\t0.9\n");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());

  WikidictAnnotator annotator = new WikidictAnnotator(props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Paris");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("O");

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkEmptySurfaceFormWithNoNERReturnsEmpty() throws Exception {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator(props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn(null);

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testDoOneSentenceWithNoMentionsDoesNotThrow() throws Exception {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator(props);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "Hello");
  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
  when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.emptyList());

  Annotation annotation = new Annotation("Hello");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.doOneSentence(annotation, sentence);

  assertEquals("O", token.get(CoreAnnotations.WikipediaEntityAnnotation.class));
}
@Test
public void testThresholdExcludesLowScoreEntry() throws Exception {
  File file = File.createTempFile("wikidict", ".tsv");
  file.deleteOnExit();
  BufferedWriter writer = new BufferedWriter(new FileWriter(file));
  writer.write("LowEntry\tLink1\t0.1\n");
  writer.write("HighEntry\tLink2\t0.9\n");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", file.getAbsolutePath());
  props.setProperty("threshold", "0.5");

  WikidictAnnotator annotator = new WikidictAnnotator(props);

  CoreMap mentionLow = mock(CoreMap.class);
  when(mentionLow.get(CoreAnnotations.TextAnnotation.class)).thenReturn("LowEntry");
  when(mentionLow.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mentionLow.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORG");

  Optional<String> low = annotator.link(mentionLow);
  assertFalse(low.isPresent());

  CoreMap mentionHigh = mock(CoreMap.class);
  when(mentionHigh.get(CoreAnnotations.TextAnnotation.class)).thenReturn("HighEntry");
  when(mentionHigh.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mentionHigh.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORG");

  Optional<String> high = annotator.link(mentionHigh);
  assertTrue(high.isPresent());
  assertEquals("Link2", high.get());
}
@Test
public void testLinkWithNullTextAnnotationAndNonNullOriginalText() throws Exception {
  File dictFile = File.createTempFile("wikidict", ".tsv");
  dictFile.deleteOnExit();
  BufferedWriter writer = new BufferedWriter(new FileWriter(dictFile));
  writer.write("Elon Musk\tElon_Musk\t0.99\n");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());

  WikidictAnnotator annotator = new WikidictAnnotator(props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn("Elon Musk");
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");

  Optional<String> result = annotator.link(mention);
  assertTrue(result.isPresent());
  assertEquals("Elon_Musk", result.get());
}
@Test
public void testLinkWithNullTextAndOriginalTextReturnsEmpty() throws Exception {
  File dictFile = File.createTempFile("wikidict", ".tsv");
  dictFile.deleteOnExit();
  BufferedWriter writer = new BufferedWriter(new FileWriter(dictFile));
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());

  WikidictAnnotator annotator = new WikidictAnnotator(props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn(null);

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkWithCaselessDictionaryAndUpperCaseInput() throws Exception {
  File dictFile = File.createTempFile("wikidict_upper", ".tsv");
  dictFile.deleteOnExit();
  BufferedWriter writer = new BufferedWriter(new FileWriter(dictFile));
  writer.write("apple\tApple_Inc\t0.99\n");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());
  props.setProperty("caseless", "true");

  WikidictAnnotator annotator = new WikidictAnnotator(props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("APPLE");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORGANIZATION");

  Optional<String> result = annotator.link(mention);
  assertTrue(result.isPresent());
  assertEquals("Apple_Inc", result.get());
}
@Test
public void testLinkWithMultipleEntriesOnlyFirstAboveThreshold() throws Exception {
  File dictFile = File.createTempFile("wikidict_multi", ".tsv");
  dictFile.deleteOnExit();
  BufferedWriter writer = new BufferedWriter(new FileWriter(dictFile));
  writer.write("EntryA\tLinkA\t0.6\n");
  writer.write("EntryA\tLinkB\t0.3\n");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());
  props.setProperty("threshold", "0.5");

  WikidictAnnotator annotator = new WikidictAnnotator(props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("EntryA");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("MISC");

  Optional<String> result = annotator.link(mention);
  assertTrue(result.isPresent());
  assertEquals("LinkA", result.get());
}
@Test
public void testLinkTimexWithPastRefExcluded() throws Exception {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator(props);

  Timex timex = mock(Timex.class);
  when(timex.value()).thenReturn("PAST_REF");

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("yesterday");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("DATE");
  when(mention.get(TimeAnnotations.TimexAnnotation.class)).thenReturn(timex);

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkWithNonAlphabeticNumericSurfaceForm() throws Exception {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator(props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("12345678");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("NUMBER");

  Optional<String> result = annotator.link(mention);
  assertTrue(result.isPresent());
  assertEquals("12345678", result.get());
}
@Test
public void testDoOneSentenceAssignsWikipediaEntityOnlyToMentionTokens() throws Exception {
  File dictFile = File.createTempFile("wikidict_do_one", ".tsv");
  dictFile.deleteOnExit();
  BufferedWriter writer = new BufferedWriter(new FileWriter(dictFile));
  writer.write("Intel\tIntel_Corp\t0.9\n");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());

  WikidictAnnotator annotator = new WikidictAnnotator(props);

  CoreLabel token1 = new CoreLabel();
  token1.set(CoreAnnotations.TextAnnotation.class, "Intel");
  CoreLabel token2 = new CoreLabel();
  token2.set(CoreAnnotations.TextAnnotation.class, "processors");

  List<CoreLabel> tokenList = Arrays.asList(token1, token2);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Intel");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORGANIZATION");
  when(mention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token1));

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokenList);
  when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.singletonList(mention));

  Annotation annotation = new Annotation("Intel processors");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.doOneSentence(annotation, sentence);

  assertEquals("Intel_Corp", token1.get(CoreAnnotations.WikipediaEntityAnnotation.class));
  assertEquals("O", token2.get(CoreAnnotations.WikipediaEntityAnnotation.class));
}
@Test
public void testLinkWithNumericNERButNullNumericValueReturnsEmpty() throws Exception {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator(props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("5th");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORDINAL");
  when(mention.get(CoreAnnotations.NumericValueAnnotation.class)).thenReturn(null);

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkWithNERButSurfaceFormMissingInDictionaryReturnsEmpty() throws Exception {
  File dictFile = File.createTempFile("wikidict_missing_entry", ".tsv");
  dictFile.deleteOnExit();
  BufferedWriter writer = new BufferedWriter(new FileWriter(dictFile));
  writer.write("KnownEntity\tLink_Known\t0.9\n");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());

  WikidictAnnotator annotator = new WikidictAnnotator(props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("UnknownEntity");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORGANIZATION");

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkNumberPatternRejectsNonMatchingSurfaceFormReturnsEmpty() throws Exception {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator(props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("abc123");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("NUMBER");

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testDictionaryWithDuplicateLinksReusesStringInstances() throws Exception {
  File dictFile = File.createTempFile("wikidict_duplicates", ".tsv");
  dictFile.deleteOnExit();
  BufferedWriter writer = new BufferedWriter(new FileWriter(dictFile));
  writer.write("Foo\tCommonLink\t0.9\n");
  writer.write("Bar\tCommonLink\t0.9\n");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());

  WikidictAnnotator annotator = new WikidictAnnotator(props);

  CoreMap mention1 = mock(CoreMap.class);
  when(mention1.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Foo");
  when(mention1.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention1.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ENTITY");

  CoreMap mention2 = mock(CoreMap.class);
  when(mention2.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Bar");
  when(mention2.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention2.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ENTITY");

  Optional<String> result1 = annotator.link(mention1);
  Optional<String> result2 = annotator.link(mention2);

  assertTrue(result1.isPresent());
  assertTrue(result2.isPresent());
  assertEquals("CommonLink", result1.get());
  assertEquals("CommonLink", result2.get());
  assertTrue(result1.get() == result2.get()); 
}
@Test
public void testLinkWithCaselessDictionarySkipsCaseWhenNotEnabled() throws Exception {
  File dictFile = File.createTempFile("wikidict_case_sensitive", ".tsv");
  dictFile.deleteOnExit();
  BufferedWriter writer = new BufferedWriter(new FileWriter(dictFile));
  writer.write("lowercase entry\tEntity_Page\t0.9\n");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());
  

  WikidictAnnotator annotator = new WikidictAnnotator(props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("LOWERCASE ENTRY");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("MISC");

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkHandlesNumericNERThatAlsoMatchesNumberPattern() throws Exception {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator(props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("2024");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("NUMBER");

  Optional<String> result = annotator.link(mention);
  assertTrue(result.isPresent());
  assertEquals("2024", result.get());
}
@Test
public void testDoOneSentenceAssignsNothingWhenMentionLinkReturnsEmpty() throws Exception {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator(props);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "Unlinkable");

  List<CoreLabel> tokenList = Arrays.asList(token);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Unlinkable");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORG");
  when(mention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokenList);

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokenList);
  when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.singletonList(mention));

  Annotation annotation = new Annotation("Unlinkable");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.doOneSentence(annotation, sentence);

  assertEquals("O", token.get(CoreAnnotations.WikipediaEntityAnnotation.class));
}
@Test
public void testEmptyDictionaryResultsInNoLinks() throws Exception {
  File dictFile = File.createTempFile("wikidict_empty", ".tsv");
  dictFile.deleteOnExit();
  BufferedWriter writer = new BufferedWriter(new FileWriter(dictFile));
  writer.close(); 

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());

  WikidictAnnotator annotator = new WikidictAnnotator(props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Nothing");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("UNKNOWN");

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testThresholdZeroAcceptsAllEntries() throws Exception {
  File dictFile = File.createTempFile("wikidict_threshold0", ".tsv");
  dictFile.deleteOnExit();
  BufferedWriter writer = new BufferedWriter(new FileWriter(dictFile));
  writer.write("LowScore\tLow_Link\t0.01\n");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());
  props.setProperty("threshold", "0.0");

  WikidictAnnotator annotator = new WikidictAnnotator(props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("LowScore");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORG");

  Optional<String> result = annotator.link(mention);
  assertTrue(result.isPresent());
  assertEquals("Low_Link", result.get());
}
@Test
public void testLinkWithWhitespaceSurfaceForm() throws Exception {
  File dictFile = File.createTempFile("wikidict_whitespace", ".tsv");
  dictFile.deleteOnExit();
  BufferedWriter writer = new BufferedWriter(new FileWriter(dictFile));
  writer.write("   \tIgnored_Link\t0.9\n");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());

  WikidictAnnotator annotator = new WikidictAnnotator(props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("   ");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORGANIZATION");

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkWithMalformedDictionaryLine() throws Exception {
  File dictFile = File.createTempFile("wikidict_malformed", ".tsv");
  dictFile.deleteOnExit();
  BufferedWriter writer = new BufferedWriter(new FileWriter(dictFile));
  writer.write("OnlyOneField\n"); 
  writer.write("NormalEntity\tGood_Link\t0.9\n");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());

  WikidictAnnotator annotator = new WikidictAnnotator(props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("NormalEntity");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");

  Optional<String> result = annotator.link(mention);
  assertTrue(result.isPresent());
  assertEquals("Good_Link", result.get());
}
@Test
public void testLinkWithDuplicateSurfaceFormsKeepsLastMatch() throws Exception {
  File dictFile = File.createTempFile("wikidict_duplicate_key", ".tsv");
  dictFile.deleteOnExit();
  BufferedWriter writer = new BufferedWriter(new FileWriter(dictFile));
  writer.write("Duplicate\tFirst_Link\t0.6\n");
  writer.write("Duplicate\tSecond_Link\t0.9\n");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());
  props.setProperty("threshold", "0.0");

  WikidictAnnotator annotator = new WikidictAnnotator(props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Duplicate");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("LOCATION");

  Optional<String> result = annotator.link(mention);
  assertTrue(result.isPresent());
  assertEquals("Second_Link", result.get());
}
@Test
public void testLinkWithThresholdEdgeCaseEqualToScore() throws Exception {
  File dictFile = File.createTempFile("wikidict_threshold_edge", ".tsv");
  dictFile.deleteOnExit();
  BufferedWriter writer = new BufferedWriter(new FileWriter(dictFile));
  writer.write("ExactThreshold\tEdge_Link\t0.5\n");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());
  props.setProperty("threshold", "0.5");

  WikidictAnnotator annotator = new WikidictAnnotator(props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("ExactThreshold");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("MISC");

  Optional<String> result = annotator.link(mention);
  assertTrue(result.isPresent());
  assertEquals("Edge_Link", result.get());
}
@Test
public void testDoOneFailedSentenceDoesNothing() throws Exception {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator(props);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "Failed");

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token));

  Annotation annotation = new Annotation("Failed stuff");

  annotator.doOneFailedSentence(annotation, sentence);
  
  assertNull(token.get(CoreAnnotations.WikipediaEntityAnnotation.class));
}
@Test
public void testLinkWithTimexNERButTimexObjectIsMissingReturnsEmpty() throws Exception {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator(props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Tomorrow");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("DATE");
  when(mention.get(TimeAnnotations.TimexAnnotation.class)).thenReturn(null);

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testTimexWithUpperCasePresentIsNotNormalized() {
  String result = WikidictAnnotator.normalizeTimex("PRESENT");
  assertEquals("PRESENT", result);
}
@Test
public void testTimexWithMixedCaseLabelIsNormalized() {
  String result = WikidictAnnotator.normalizeTimex("2023-08-17t12:00");
  assertEquals("2023-08-17t12:00", result);
}
@Test
public void testLinkWithMalformedScoreThrowsGracefulError() throws Exception {
  File dictFile = File.createTempFile("wikidict_invalid_score", ".tsv");
  dictFile.deleteOnExit();
  BufferedWriter writer = new BufferedWriter(new FileWriter(dictFile));
  writer.write("Entity\tLink\tNaN\n");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());
  props.setProperty("threshold", "0.0");

  try {
    WikidictAnnotator annotator = new WikidictAnnotator(props);
    fail("Expected NumberFormatException to be wrapped in RuntimeException");
  } catch (RuntimeException e) {
    assertTrue(e.getCause() instanceof NumberFormatException);
  }
}
@Test
public void testMainMethodDoesNotThrowWhenParsingEmptyArgs() throws Exception {
  
  Properties props = StringUtils.argsToProperties(new String[] {});
  props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,entitymentions,entitylink");
  StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
  assertNotNull(pipeline);
}
@Test
public void testLinkWithNullSurfaceFormAndNonNullNERReturnsEmpty() throws Exception {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator(props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkWithEmptyLineIgnoredInDictionary() throws Exception {
  File dictFile = File.createTempFile("wikidict_emptyline", ".tsv");
  dictFile.deleteOnExit();
  BufferedWriter writer = new BufferedWriter(new FileWriter(dictFile));
  writer.write("\n");
  writer.write("EntryX\tLinkX\t0.9\n");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());

  WikidictAnnotator annotator = new WikidictAnnotator(props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("EntryX");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORG");

  Optional<String> result = annotator.link(mention);
  assertTrue(result.isPresent());
  assertEquals("LinkX", result.get());
}
@Test
public void testDictionaryLineWithLeadingTabIsSkipped() throws Exception {
  File dictFile = File.createTempFile("wikidict_leadingtab", ".tsv");
  dictFile.deleteOnExit();
  BufferedWriter writer = new BufferedWriter(new FileWriter(dictFile));
  writer.write("\tSkipped\tSkippedLink\t0.9\n");
  writer.write("EntityY\tLinkedY\t0.8\n");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());
  props.setProperty("threshold", "0.0");

  WikidictAnnotator annotator = new WikidictAnnotator(props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Skipped");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORG");

  Optional<String> skippedResult = annotator.link(mention);
  assertFalse(skippedResult.isPresent());

  CoreMap mention2 = mock(CoreMap.class);
  when(mention2.get(CoreAnnotations.TextAnnotation.class)).thenReturn("EntityY");
  when(mention2.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention2.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORG");

  Optional<String> result2 = annotator.link(mention2);
  assertTrue(result2.isPresent());
  assertEquals("LinkedY", result2.get());
}
@Test
public void testTimexValueIsFutureReturnsEmpty() throws Exception {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator(props);

  Timex timex = mock(Timex.class);
  when(timex.value()).thenReturn("FUTURE");

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("next month");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("DATE");
  when(mention.get(TimeAnnotations.TimexAnnotation.class)).thenReturn(timex);

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkWithNERTagMatchingSetReturnsNormalizedTimex() throws Exception {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator(props);

  Timex timex = mock(Timex.class);
  when(timex.value()).thenReturn("2023-12-31T00:00");

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("every December 31");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("SET");
  when(mention.get(TimeAnnotations.TimexAnnotation.class)).thenReturn(timex);

  Optional<String> result = annotator.link(mention);
  assertTrue(result.isPresent());
  assertEquals("2023-12-31", result.get());
}
@Test
public void testLinkIgnoresOnerTagEvenIfDictionaryContainsForm() throws Exception {
  File dictFile = File.createTempFile("wikidict_oner", ".tsv");
  dictFile.deleteOnExit();
  BufferedWriter writer = new BufferedWriter(new FileWriter(dictFile));
  writer.write("IgnoredText\tValidLink\t0.9\n");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());

  WikidictAnnotator annotator = new WikidictAnnotator(props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("IgnoredText");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("O");

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testEntityLinkedHasCorrectValueInMentionAndToken() throws Exception {
  File dictFile = File.createTempFile("wikidict_link", ".tsv");
  dictFile.deleteOnExit();
  BufferedWriter writer = new BufferedWriter(new FileWriter(dictFile));
  writer.write("MyEntity\tWikipedia_MyEntity\t0.95\n");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());

  WikidictAnnotator annotator = new WikidictAnnotator(props);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "MyEntity");

  List<CoreLabel> mentionTokens = new ArrayList<>();
  mentionTokens.add(token);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("MyEntity");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORG");
  when(mention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(mentionTokens);

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(mentionTokens);
  when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.singletonList(mention));

  Annotation annotation = new Annotation("MyEntity");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.doOneSentence(annotation, sentence);

  assertEquals("Wikipedia_MyEntity", token.get(CoreAnnotations.WikipediaEntityAnnotation.class));
  verify(mention).set(CoreAnnotations.WikipediaEntityAnnotation.class, "Wikipedia_MyEntity");
}
@Test
public void testLinkWithScoreBelowThresholdIsSkipped() throws Exception {
  File dictFile = File.createTempFile("wikidict_score_filter", ".tsv");
  dictFile.deleteOnExit();
  BufferedWriter writer = new BufferedWriter(new FileWriter(dictFile));
  writer.write("LowScoreEntity\tFilteredLink\t0.4\n");
  writer.write("HighScoreEntity\tAcceptedLink\t0.8\n");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());
  props.setProperty("threshold", "0.5");

  WikidictAnnotator annotator = new WikidictAnnotator(props);

  CoreMap mention1 = mock(CoreMap.class);
  when(mention1.get(CoreAnnotations.TextAnnotation.class)).thenReturn("LowScoreEntity");
  when(mention1.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention1.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORG");

  CoreMap mention2 = mock(CoreMap.class);
  when(mention2.get(CoreAnnotations.TextAnnotation.class)).thenReturn("HighScoreEntity");
  when(mention2.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention2.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORG");

  Optional<String> result1 = annotator.link(mention1);
  Optional<String> result2 = annotator.link(mention2);

  assertFalse(result1.isPresent());
  assertTrue(result2.isPresent());
  assertEquals("AcceptedLink", result2.get());
}
@Test
public void testLinkWithNumericValueZeroIsHandled() throws Exception {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator(props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("0th");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORDINAL");
  when(mention.get(CoreAnnotations.NumericValueAnnotation.class)).thenReturn(0);

  Optional<String> result = annotator.link(mention);
  assertTrue(result.isPresent());
  assertEquals("0", result.get());
}
@Test
public void testLinkWithTimexValueWithOnlyDateNoTimeKeepsValue() throws Exception {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator(props);

  Timex timex = mock(Timex.class);
  when(timex.value()).thenReturn("2022-03-15");

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("March 15");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("DATE");
  when(mention.get(TimeAnnotations.TimexAnnotation.class)).thenReturn(timex);

  Optional<String> result = annotator.link(mention);
  assertTrue(result.isPresent());
  assertEquals("2022-03-15", result.get());
}
@Test
public void testLinkWithNonStandardNERButDictionaryMatchReturnsLink() throws Exception {
  File dictFile = File.createTempFile("wikidict_nonstandard_ner", ".tsv");
  dictFile.deleteOnExit();
  BufferedWriter writer = new BufferedWriter(new FileWriter(dictFile));
  writer.write("QuantumAI\tQuantumArtificialIntelligence\t0.95\n");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());

  WikidictAnnotator annotator = new WikidictAnnotator(props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("QuantumAI");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("TECH");

  Optional<String> result = annotator.link(mention);
  assertTrue(result.isPresent());
  assertEquals("QuantumArtificialIntelligence", result.get());
}
@Test
public void testLinkWithNERNullAndNumberPatternMatchReturnsLink() throws Exception {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator(props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("123.4");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn(null);

  Optional<String> result = annotator.link(mention);
  assertTrue(result.isPresent());
  assertEquals("123.4", result.get());
}
@Test
public void testDoOneSentenceWithMultipleMentionsAssignsCorrectWikipediaTags() throws Exception {
  File dictFile = File.createTempFile("wikidict_multiple_mentions", ".tsv");
  dictFile.deleteOnExit();
  BufferedWriter writer = new BufferedWriter(new FileWriter(dictFile));
  writer.write("Apple\tApple_Inc\t0.9\n");
  writer.write("Amazon\tAmazon_com\t0.95\n");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());

  WikidictAnnotator annotator = new WikidictAnnotator(props);

  CoreLabel token1 = new CoreLabel();
  token1.set(CoreAnnotations.TextAnnotation.class, "Apple");
  CoreLabel token2 = new CoreLabel();
  token2.set(CoreAnnotations.TextAnnotation.class, "Amazon");

  List<CoreLabel> tokens = Arrays.asList(token1, token2);

  CoreMap mention1 = mock(CoreMap.class);
  when(mention1.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Apple");
  when(mention1.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention1.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORG");
  when(mention1.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token1));

  CoreMap mention2 = mock(CoreMap.class);
  when(mention2.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Amazon");
  when(mention2.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention2.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORG");
  when(mention2.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token2));

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
  when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Arrays.asList(mention1, mention2));

  Annotation annotation = new Annotation("Apple Amazon");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.doOneSentence(annotation, sentence);

  assertEquals("Apple_Inc", token1.get(CoreAnnotations.WikipediaEntityAnnotation.class));
  assertEquals("Amazon_com", token2.get(CoreAnnotations.WikipediaEntityAnnotation.class));
}
@Test
public void testDoOneSentenceWithOverlappingMentionsAssignsCorrectTokenLinks() throws Exception {
  File dictFile = File.createTempFile("wikidict_overlap_mentions", ".tsv");
  dictFile.deleteOnExit();
  BufferedWriter writer = new BufferedWriter(new FileWriter(dictFile));
  writer.write("New York\tNew_York_City\t0.95\n");
  writer.close();

  Properties props = new Properties();
  props.setProperty("wikidict", dictFile.getAbsolutePath());

  WikidictAnnotator annotator = new WikidictAnnotator(props);

  CoreLabel token1 = new CoreLabel();
  token1.set(CoreAnnotations.TextAnnotation.class, "New");
  CoreLabel token2 = new CoreLabel();
  token2.set(CoreAnnotations.TextAnnotation.class, "York");

  List<CoreLabel> tokens = Arrays.asList(token1, token2);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("New York");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("LOCATION");
  when(mention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
  when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.singletonList(mention));

  Annotation annotation = new Annotation("New York");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  annotator.doOneSentence(annotation, sentence);

  assertEquals("New_York_City", token1.get(CoreAnnotations.WikipediaEntityAnnotation.class));
  assertEquals("New_York_City", token2.get(CoreAnnotations.WikipediaEntityAnnotation.class));
} 
}