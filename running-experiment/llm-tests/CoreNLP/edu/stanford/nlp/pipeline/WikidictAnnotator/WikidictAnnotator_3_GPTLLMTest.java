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

public class WikidictAnnotator_3_GPTLLMTest {

 @Test
  public void testNormalizeTimexWithTimeComponent() {
    String input = "2025-12-31T15:45";
    String result = WikidictAnnotator.normalizeTimex(input);
    assertEquals("2025-12-31", result);
  }
@Test
  public void testNormalizeTimexWithoutTimeComponent() {
    String input = "2025-12-31";
    String result = WikidictAnnotator.normalizeTimex(input);
    assertEquals("2025-12-31", result);
  }
@Test
  public void testNormalizeTimexWithPresentValue() {
    String result = WikidictAnnotator.normalizeTimex("PRESENT");
    assertEquals("PRESENT", result);
  }
@Test
  public void testLinkWithExactMatchDictionaryEntry() {
    Properties props = new Properties();
    props.setProperty("wikidict", "ignored.tsv");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);
//     annotator.dictionary.put("Stanford", "Stanford_University");

    CoreMap mention = mock(CoreMap.class);
    when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
    when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Stanford");
    when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORGANIZATION");

    Optional<String> result = annotator.link(mention);
    assertTrue(result.isPresent());
    assertEquals("Stanford_University", result.get());
  }
@Test
  public void testLinkReturnsNormalizedTimex() {
    Properties props = new Properties();
    props.setProperty("wikidict", "ignored.tsv");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

    CoreMap mention = mock(CoreMap.class);
    Timex timex = mock(Timex.class);
    when(timex.value()).thenReturn("2022-01-01T00:00");
    when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn("2022-01-01T00:00");
    when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("DATE");
    when(mention.get(TimeAnnotations.TimexAnnotation.class)).thenReturn(timex);

    Optional<String> result = annotator.link(mention);
    assertTrue(result.isPresent());
    assertEquals("2022-01-01", result.get());
  }
@Test
  public void testLinkTimexWithPresentReturnsEmpty() {
    Properties props = new Properties();
    props.setProperty("wikidict", "ignored.tsv");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

    CoreMap mention = mock(CoreMap.class);
    Timex timex = mock(Timex.class);
    when(timex.value()).thenReturn("PRESENT");
    when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn("PRESENT");
    when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("DATE");
    when(mention.get(TimeAnnotations.TimexAnnotation.class)).thenReturn(timex);

    Optional<String> result = annotator.link(mention);
    assertFalse(result.isPresent());
  }
@Test
  public void testLinkOrdinalNumber() {
    Properties props = new Properties();
    props.setProperty("wikidict", "ignored.tsv");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

    CoreMap mention = mock(CoreMap.class);
    when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn("1st");
    when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORDINAL");
    when(mention.get(CoreAnnotations.NumericValueAnnotation.class)).thenReturn(1);

    Optional<String> result = annotator.link(mention);
    assertTrue(result.isPresent());
    assertEquals("1", result.get());
  }
@Test
  public void testLinkPureNumber() {
    Properties props = new Properties();
    props.setProperty("wikidict", "ignored.tsv");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

    CoreMap mention = mock(CoreMap.class);
    when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn("123.45");
    when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("NUMBER");

    Optional<String> result = annotator.link(mention);
    assertTrue(result.isPresent());
    assertEquals("123.45", result.get());
  }
@Test
  public void testLinkWithNoMatchReturnsEmpty() {
    Properties props = new Properties();
    props.setProperty("wikidict", "ignored.tsv");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

    CoreMap mention = mock(CoreMap.class);
    when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn("UnknownEntity");
    when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("LOCATION");

    Optional<String> result = annotator.link(mention);
    assertFalse(result.isPresent());
  }
@Test
  public void testDoOneSentenceSetsWikipediaEntityAnnotation() {
    Properties props = new Properties();
    props.setProperty("wikidict", "ignored.tsv");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);
//     annotator.dictionary.put("Obama", "Barack_Obama");

    Annotation annotation = new Annotation("Dummy");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Obama");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap mention = mock(CoreMap.class);
    when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn("Obama");
    when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");
    when(mention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(mention);
    when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(mentions);

    annotator.doOneSentence(annotation, sentence);

    assertEquals("Barack_Obama", token.get(CoreAnnotations.WikipediaEntityAnnotation.class));
    verify(mention).set(CoreAnnotations.WikipediaEntityAnnotation.class, "Barack_Obama");
  }
@Test
  public void testMaxTimeAlwaysReturnsMinusOne() {
    Properties props = new Properties();
    props.setProperty("wikidict", "ignored.tsv");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);
    assertEquals(-1L, annotator.maxTime());
  }
@Test
  public void testRequirementsSatisfiedReturnsWikipediaAnnotation() {
    Properties props = new Properties();
    props.setProperty("wikidict", "ignored.tsv");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);
    Set<Class<? extends CoreAnnotation>> result = annotator.requirementsSatisfied();
    assertTrue(result.contains(CoreAnnotations.WikipediaEntityAnnotation.class));
  }
@Test
  public void testRequiresReturnsAllRequiredAnnotations() {
    Properties props = new Properties();
    props.setProperty("wikidict", "ignored.tsv");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);
    Set<Class<? extends CoreAnnotation>> required = annotator.requires();

    assertTrue(required.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.OriginalTextAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.MentionsAnnotation.class));
  }
@Test
  public void testDoOneFailedSentenceDoesNothing() {
    Properties props = new Properties();
    props.setProperty("wikidict", "ignored.tsv");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);
    Annotation annotation = new Annotation("Dummy");
    CoreMap sentence = mock(CoreMap.class);
    
    annotator.doOneFailedSentence(annotation, sentence);
  }
@Test
  public void testNThreadsReturnsDefaultOne() {
    Properties props = new Properties();
    props.setProperty("wikidict", "ignored.tsv");
    props.setProperty("threads", "1");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);
    assertEquals(1, annotator.nThreads());
  }
@Test
public void testLinkWithTextAnnotationFallbackWhenOriginalTextIsNull() {
  Properties props = new Properties();
  props.setProperty("wikidict", "ignored.tsv");
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);
//   annotator.dictionary.put("California", "California_(state)");

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("California");
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("STATE_OR_PROVINCE");

  Optional<String> result = annotator.link(mention);
  assertTrue(result.isPresent());
  assertEquals("California_(state)", result.get());
}
@Test
public void testLinkWithEmptyTimexValueReturnsEmpty() {
  Properties props = new Properties();
  props.setProperty("wikidict", "ignored.tsv");
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  CoreMap mention = mock(CoreMap.class);
  Timex timex = mock(Timex.class);
  when(timex.value()).thenReturn("");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn("SomeTime");
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("DATE");
  when(mention.get(TimeAnnotations.TimexAnnotation.class)).thenReturn(timex);

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkWithNullNERReturnsEmpty() {
  Properties props = new Properties();
  props.setProperty("wikidict", "ignored.tsv");
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn("Stanford");
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn(null);

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkWithNERTagOfOReturnsEmpty() {
  Properties props = new Properties();
  props.setProperty("wikidict", "ignored.tsv");
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);
//   annotator.dictionary.put("Dog", "Canine");

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn("Dog");
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("O");

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkWithSurfaceFormThatDoesNotMatchNumberPattern() {
  Properties props = new Properties();
  props.setProperty("wikidict", "ignored.tsv");
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn("12a3");
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("NUMBER");

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testPronounEntityNotLinkableReturnsEmpty() {
  Properties props = new Properties();
  props.setProperty("wikidict", "ignored.tsv");
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn("He");
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testDictionaryThresholdFilteringSkipsLowScoreLines() {
  Properties props = new Properties();
  props.setProperty("wikidict", "test.tsv");
  props.setProperty("threshold", "0.5");

  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  
  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn("Whatever");
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testDoOneSentenceWithEmptyMentionsAnnotation() {
  Properties props = new Properties();
  props.setProperty("wikidict", "dummy.tsv");
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  Annotation annotation = new Annotation("Test");
  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "Hello");
  token.set(CoreAnnotations.OriginalTextAnnotation.class, "Hello");

  CoreMap sentence = mock(CoreMap.class);
  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);

  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
  when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.emptyList());

  annotator.doOneSentence(annotation, sentence);
  assertEquals("O", token.get(CoreAnnotations.WikipediaEntityAnnotation.class));
}
@Test
public void testLinkWithNullOriginalTextAndNullTextAnnotationReturnsEmpty() {
  Properties props = new Properties();
  props.setProperty("wikidict", "ignored.tsv");
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkTimexWithNullTimeAnnotationReturnsEmpty() {
  Properties props = new Properties();
  props.setProperty("wikidict", "any.tsv");
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn("2022-05-30T00:00");
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("DATE");
  when(mention.get(TimeAnnotations.TimexAnnotation.class)).thenReturn(null);

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkTimexWithNullValueReturnsEmpty() {
  Properties props = new Properties();
  props.setProperty("wikidict", "fake.tsv");
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  Timex timex = mock(Timex.class);
  when(timex.value()).thenReturn(null);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn("unknown");
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("TIME");
  when(mention.get(TimeAnnotations.TimexAnnotation.class)).thenReturn(timex);

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkOrdinalWithNullNumericValueReturnsEmpty() {
  Properties props = new Properties();
  props.setProperty("wikidict", "none.tsv");
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn("1st");
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORDINAL");
  when(mention.get(CoreAnnotations.NumericValueAnnotation.class)).thenReturn(null);

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testDoOneSentenceWithMentionMissingNER() {
  Properties props = new Properties();
  props.setProperty("wikidict", "ignore.tsv");
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);
//   annotator.dictionary.put("John", "John_(name)");

  Annotation annotation = new Annotation("test");

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "John");

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn("John");
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Arrays.asList(token));

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token));
  when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.singletonList(mention));

  annotator.doOneSentence(annotation, sentence);

  assertEquals("O", token.get(CoreAnnotations.WikipediaEntityAnnotation.class));
}
@Test
public void testLinkNumberPatternWithLettersFails() {
  Properties props = new Properties();
  props.setProperty("wikidict", "irrelevant.tsv");
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn("123A");
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("NUMBER");

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkWithCaselessEnabledMatchesLowercaseDictionary() {
  Properties props = new Properties();
  props.setProperty("wikidict", "none.tsv");
  props.setProperty("caseless", "true");
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

//   annotator.dictionary.put("new york", "New_York_City");

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn("New York");
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("CITY");

  Optional<String> result = annotator.link(mention);
  assertTrue(result.isPresent());
  assertEquals("New_York_City", result.get());
}
@Test
public void testLinkIgnoresIncompleteMentionsWithNoSurfaceText() {
  Properties props = new Properties();
  props.setProperty("wikidict", "any.tsv");
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn(null);

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testDoOneSentenceAssignsOWikidataEntityToTokensByDefault() {
  Properties props = new Properties();
  props.setProperty("wikidict", "sample.tsv");
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  CoreLabel token1 = new CoreLabel();
  token1.set(CoreAnnotations.TextAnnotation.class, "The");
  CoreLabel token2 = new CoreLabel();
  token2.set(CoreAnnotations.TextAnnotation.class, "capybara");

  List<CoreLabel> tokens = Arrays.asList(token1, token2);

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
  when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.emptyList());

  Annotation annotation = new Annotation("Test doc");
  annotator.doOneSentence(annotation, sentence);

  assertEquals("O", token1.get(CoreAnnotations.WikipediaEntityAnnotation.class));
  assertEquals("O", token2.get(CoreAnnotations.WikipediaEntityAnnotation.class));
}
@Test
public void testLinkWithMixedCaseKeyAndCaselessDictionaryDisabled() {
  Properties props = new Properties();
  props.setProperty("wikidict", "mock.tsv");
  props.setProperty("caseless", "false");
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

//   annotator.dictionary.put("john", "John_(name)");

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn("John");
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkWithMixedCaseKeyAndCaselessDictionaryEnabled() {
  Properties props = new Properties();
  props.setProperty("wikidict", "mock.tsv");
  props.setProperty("caseless", "true");
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

//   annotator.dictionary.put("john", "John_(name)");

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn("John");
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");

  Optional<String> result = annotator.link(mention);
  assertTrue(result.isPresent());
  assertEquals("John_(name)", result.get());
}
@Test
public void testLinkWithSurfaceFormPureDots() {
  Properties props = new Properties();
  props.setProperty("wikidict", "fake.tsv");
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn("...");
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("NUMBER");

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkWithNumberContainingMultipleDots() {
  Properties props = new Properties();
  props.setProperty("wikidict", "xyz.tsv");
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn("12.3.4");
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("NUMBER");

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkWithNumericHavingLeadingZeros() {
  Properties props = new Properties();
  props.setProperty("wikidict", "abc.tsv");
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn("000123");
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("NUMBER");

  Optional<String> result = annotator.link(mention);
  assertTrue(result.isPresent());
  assertEquals("000123", result.get());
}
@Test
public void testLinkWithTimexSpecialRefValueReturnsEmpty() {
  Properties props = new Properties();
  props.setProperty("wikidict", "none.tsv");
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  Timex timex = mock(Timex.class);
  when(timex.value()).thenReturn("FUTURE_REF");

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn("somedate");
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("DATE");
  when(mention.get(TimeAnnotations.TimexAnnotation.class)).thenReturn(timex);

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testDoOneSentenceWithMultipleMentionsAndPartialMatches() {
  Properties props = new Properties();
  props.setProperty("wikidict", "source.tsv");
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

//   annotator.dictionary.put("Lincoln", "Abraham_Lincoln");

  CoreLabel token1 = new CoreLabel();
  token1.set(CoreAnnotations.TextAnnotation.class, "Lincoln");

  CoreLabel token2 = new CoreLabel();
  token2.set(CoreAnnotations.TextAnnotation.class, "Memorial");

  CoreMap mention1 = mock(CoreMap.class);
  when(mention1.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn("Lincoln");
  when(mention1.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");
  when(mention1.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token1));

  CoreMap mention2 = mock(CoreMap.class);
  when(mention2.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn("Memorial");
  when(mention2.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("LOCATION");
  when(mention2.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token2));

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Arrays.asList(token1, token2));
  when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Arrays.asList(mention1, mention2));

  Annotation annotation = new Annotation("doc");
  annotator.doOneSentence(annotation, sentence);

  assertEquals("Abraham_Lincoln", token1.get(CoreAnnotations.WikipediaEntityAnnotation.class));
  assertNull(token2.get(CoreAnnotations.WikipediaEntityAnnotation.class));
}
@Test
public void testLinkWithEmptySurfaceFormReturnsEmpty() {
  Properties props = new Properties();
  props.setProperty("wikidict", "empty.tsv");
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn("");
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkIgnoresDictionaryWhenNERisNullAndTextMatches() {
  Properties props = new Properties();
  props.setProperty("wikidict", "x.tsv");
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

//   annotator.dictionary.put("Apple", "Apple_Inc.");

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn("Apple");
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn(null);

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkWithWhitespaceSurfaceFormReturnsEmpty() {
  Properties props = new Properties();
  props.setProperty("wikidict", "ignored.tsv");
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn("   ");
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkTimexValuePresentRefReturnsEmpty() {
  Properties props = new Properties();
  props.setProperty("wikidict", "test.tsv");
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  Timex timex = mock(Timex.class);
  when(timex.value()).thenReturn("PRESENT_REF");

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn("2024-01-01");
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("DATE");
  when(mention.get(TimeAnnotations.TimexAnnotation.class)).thenReturn(timex);

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkWithNullMentionReturnsEmpty() {
  Properties props = new Properties();
  props.setProperty("wikidict", "config.tsv");
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  Optional<String> result = annotator.link(null);
  assertFalse(result.isPresent());
}
@Test
public void testLinkWithValidNERButNoDictionaryMatchReturnsEmpty() {
  Properties props = new Properties();
  props.setProperty("wikidict", "dict.tsv");
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn("Atlantis");
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("LOCATION");

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkWithSymbolOnlySurfaceFormReturnsEmpty() {
  Properties props = new Properties();
  props.setProperty("wikidict", "file.tsv");
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn("$$$");
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("MONEY");

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkWithNERInLowerCaseReturnsValidMatch() {
  Properties props = new Properties();
  props.setProperty("wikidict", "custom.tsv");
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

//   annotator.dictionary.put("venus", "Venus_(planet)");

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn("venus");
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("location");

  Optional<String> result = annotator.link(mention);
  assertTrue(result.isPresent());
  assertEquals("Venus_(planet)", result.get());
}
@Test
public void testLinkNERDateWithoutTimexAnnotationReturnsEmpty() {
  Properties props = new Properties();
  props.setProperty("wikidict", "anyfile.tsv");
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn("September");
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("DATE");
  when(mention.get(TimeAnnotations.TimexAnnotation.class)).thenReturn(null);

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkWithNERDateAndNullTimexValueReturnsEmpty() {
  Properties props = new Properties();
  props.setProperty("wikidict", "temporal.tsv");
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  Timex timex = mock(Timex.class);
  when(timex.value()).thenReturn(null);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn("2024");
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("DATE");
  when(mention.get(TimeAnnotations.TimexAnnotation.class)).thenReturn(timex);

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkWithValidTimexFieldButNERNotRecognizedReturnsEmpty() {
  Properties props = new Properties();
  props.setProperty("wikidict", "time.tsv");
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  Timex timex = mock(Timex.class);
  when(timex.value()).thenReturn("2023-01-01");

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn("2023-01-01");
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("DURATION");
  when(mention.get(TimeAnnotations.TimexAnnotation.class)).thenReturn(timex);

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkWithNERSetButSurfaceFormNotInDictionaryReturnsEmpty() {
  Properties props = new Properties();
  props.setProperty("wikidict", "nodict.tsv");
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn("Xyznonexistent");
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkWithNERTagSetToNullAndSurfaceFormPresentInDictionary() {
  Properties props = new Properties();
  props.setProperty("wikidict", "test.tsv");
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);
//   annotator.dictionary.put("Amazon", "Amazon_(company)");

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn("Amazon");
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn(null);

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkWithTextAnnotationUsedInsteadOfOriginal() {
  Properties props = new Properties();
  props.setProperty("wikidict", "mock.tsv");
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);
//   annotator.dictionary.put("Berlin", "Berlin");

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Berlin");
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("LOCATION");

  Optional<String> result = annotator.link(mention);
  assertTrue(result.isPresent());
  assertEquals("Berlin", result.get());
}
@Test
public void testLinkWithScoreCutoffAboveThresholdSkipsLowScoreEntry() {
  Properties props = new Properties();
  props.setProperty("wikidict", "dummy.tsv");
  props.setProperty("threshold", "0.8"); 
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  
  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn("LowScoreName");
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkWhenMentionHasNoNERAndTimexAnnotationIsPresentButInvalid() {
  Properties props = new Properties();
  props.setProperty("wikidict", "any.tsv");
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  Timex timex = mock(Timex.class);
  when(timex.value()).thenReturn("JUNK");

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn("JUNK");
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn(null);
  when(mention.get(TimeAnnotations.TimexAnnotation.class)).thenReturn(timex);

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testDoOneSentenceWithMentionMissingWikiMatchDoesNotSetWikipediaAnnotation() {
  Properties props = new Properties();
  props.setProperty("wikidict", "temp.tsv");
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "Mars");

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn("Mars");
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("LOCATION");
  when(mention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token));

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token));
  when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.singletonList(mention));

  Annotation annotation = new Annotation("Test");
  annotator.doOneSentence(annotation, sentence);

  assertNull(token.get(CoreAnnotations.WikipediaEntityAnnotation.class));
}
@Test
public void testDoOneSentenceWithMentionAndEmptyTokensAnnotation() {
  Properties props = new Properties();
  props.setProperty("wikidict", "abc.tsv");
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);
//   annotator.dictionary.put("Einstein", "Albert_Einstein");

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn("Einstein");
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");
  when(mention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.emptyList());

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "Einstein");

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token));
  when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.singletonList(mention));

  Annotation annotation = new Annotation("Sentence");
  annotator.doOneSentence(annotation, sentence);

  assertEquals("O", token.get(CoreAnnotations.WikipediaEntityAnnotation.class));
}
@Test
public void testLinkWhenNERIsTimeButTimexIsMissingReturnsEmpty() {
  Properties props = new Properties();
  props.setProperty("wikidict", "ignored.tsv");
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn("midnight");
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("TIME");
  when(mention.get(TimeAnnotations.TimexAnnotation.class)).thenReturn(null);

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkWithMissingTextAndOriginalTextReturnsEmpty() {
  Properties props = new Properties();
  props.setProperty("wikidict", "empty.tsv");
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORGANIZATION");

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkReturnsTimexDateUnchangedWhenNoTCharacterPresent() {
  Properties props = new Properties();
  props.setProperty("wikidict", "time.tsv");
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  Timex timex = mock(Timex.class);
  when(timex.value()).thenReturn("1995-06-30");

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn("1995-06-30");
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("DATE");
  when(mention.get(TimeAnnotations.TimexAnnotation.class)).thenReturn(timex);

  Optional<String> result = annotator.link(mention);
  assertTrue(result.isPresent());
  assertEquals("1995-06-30", result.get());
}
@Test
public void testLinkWhenNERIsSETAndValidTimexValueWithTimeReturnsNormalized() {
  Properties props = new Properties();
  props.setProperty("wikidict", "date.tsv");
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  Timex timex = mock(Timex.class);
  when(timex.value()).thenReturn("2001-09-11T00:00");

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn("September 11, 2001");
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("SET");
  when(mention.get(TimeAnnotations.TimexAnnotation.class)).thenReturn(timex);

  Optional<String> result = annotator.link(mention);
  assertTrue(result.isPresent());
  assertEquals("2001-09-11", result.get());
}
@Test
public void testLinkReturnsEmptyWhenNERIsORDINALAndNumericValueIsNonNumericType() {
  Properties props = new Properties();
  props.setProperty("wikidict", "ordinal.tsv");
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn("third");
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORDINAL");
  when(mention.get(CoreAnnotations.NumericValueAnnotation.class)).thenReturn(null); 

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testDoOneSentenceWithMentionButEmptyTokenListInMention() {
  Properties props = new Properties();
  props.setProperty("wikidict", "wikifile.tsv");
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);
//   annotator.dictionary.put("Tesla", "Nikola_Tesla");

  CoreLabel sentenceToken = new CoreLabel();
  sentenceToken.set(CoreAnnotations.TextAnnotation.class, "Tesla");

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn("Tesla");
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");
  when(mention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.emptyList());

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(sentenceToken));
  when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.singletonList(mention));

  Annotation annotation = new Annotation("Test");
  annotator.doOneSentence(annotation, sentence);

  assertEquals("O", sentenceToken.get(CoreAnnotations.WikipediaEntityAnnotation.class));
}
@Test
public void testDoOneSentenceWithTokensOnlyNoMentionsStillAssignsAllO() {
  Properties props = new Properties();
  props.setProperty("wikidict", "wikidata.tsv");
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  CoreLabel token1 = new CoreLabel();
  token1.set(CoreAnnotations.TextAnnotation.class, "Alice");
  CoreLabel token2 = new CoreLabel();
  token2.set(CoreAnnotations.TextAnnotation.class, "and");

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Arrays.asList(token1, token2));
  when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.emptyList());

  Annotation annotation = new Annotation("Basic");
  annotator.doOneSentence(annotation, sentence);

  assertEquals("O", token1.get(CoreAnnotations.WikipediaEntityAnnotation.class));
  assertEquals("O", token2.get(CoreAnnotations.WikipediaEntityAnnotation.class));
}
@Test
public void testLinkWithMultipleDotsAndValidNERStillReturnsEmpty() {
  Properties props = new Properties();
  props.setProperty("wikidict", "precision.tsv");
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn("3.14.159");
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("NUMBER");

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkWithSingleCharacterSurfaceFormDoesNotMatchDictionaryReturnsEmpty() {
  Properties props = new Properties();
  props.setProperty("wikidict", "symbols.tsv");
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn("x");
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("VARIABLE");

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
} 
}
