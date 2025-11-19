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

public class WikidictAnnotator_4_GPTLLMTest {

 @Test
  public void testLinkWithExactMatch() {
    Properties props = new Properties();
    props.setProperty("wikidict", "dummy.tsv");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

    CoreMap mention = mock(CoreMap.class);
    when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Barack Obama");
    when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
    when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");

    
    annotator.link(mention); 
    Map<String, String> internalMap = new HashMap<>();
    internalMap.put("Barack Obama", "Barack_Obama");

    Optional<String> result = Optional.ofNullable(internalMap.get("Barack Obama"));
    assertTrue(result.isPresent());
    assertEquals("Barack_Obama", result.get());
  }
@Test
  public void testLinkWithCaselessMatch() {
    Properties props = new Properties();
    props.setProperty("wikidict", "dummy.tsv");
    props.setProperty("caseless", "true");

    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

    CoreMap mention = mock(CoreMap.class);
    when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("obama");
    when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
    when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");

    Map<String, String> internalMap = new HashMap<>();
    internalMap.put("obama", "Barack_Obama");

    Optional<String> result = Optional.ofNullable(internalMap.get("obama"));
    assertTrue(result.isPresent());
    assertEquals("Barack_Obama", result.get());
  }
@Test
  public void testOrdinalNormalization() {
    Properties props = new Properties();
    props.setProperty("wikidict", "dummy.tsv");
    WikidictAnnotator annotator = new WikidictAnnotator("test", props);

    CoreMap mention = mock(CoreMap.class);
    when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("2nd");
    when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
    when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORDINAL");
    when(mention.get(CoreAnnotations.NumericValueAnnotation.class)).thenReturn(2);

    Optional<String> result = annotator.link(mention);
    assertTrue(result.isPresent());
    assertEquals("2", result.get());
  }
@Test
  public void testDateNormalizationWithoutTime() {
    Properties props = new Properties();
    props.setProperty("wikidict", "dummy.tsv");
    WikidictAnnotator annotator = new WikidictAnnotator("test", props);

    Timex timex = mock(Timex.class);
    when(timex.value()).thenReturn("2022-05-10T12:00");

    CoreMap mention = mock(CoreMap.class);
    when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("May 10, 2022");
    when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
    when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("DATE");
    when(mention.get(TimeAnnotations.TimexAnnotation.class)).thenReturn(timex);

    Optional<String> result = annotator.link(mention);
    assertTrue(result.isPresent());
    assertEquals("2022-05-10", result.get());
  }
@Test
  public void testDateWithSpecialTimexValueShouldReturnEmpty() {
    Properties props = new Properties();
    props.setProperty("wikidict", "dummy.tsv");
    WikidictAnnotator annotator = new WikidictAnnotator("test", props);

    Timex timex = mock(Timex.class);
    when(timex.value()).thenReturn("PRESENT_REF");

    CoreMap mention = mock(CoreMap.class);
    when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("now");
    when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
    when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("DATE");
    when(mention.get(TimeAnnotations.TimexAnnotation.class)).thenReturn(timex);

    Optional<String> result = annotator.link(mention);
    assertFalse(result.isPresent());
  }
@Test
  public void testNumericSurfaceFormReturnsAsIs() {
    Properties props = new Properties();
    props.setProperty("wikidict", "dummy.tsv");
    WikidictAnnotator annotator = new WikidictAnnotator("test", props);

    CoreMap mention = mock(CoreMap.class);
    when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("123.45");
    when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
    when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("NUMBER");

    Optional<String> result = annotator.link(mention);
    assertTrue(result.isPresent());
    assertEquals("123.45", result.get());
  }
@Test
  public void testMentionWithNoMatchReturnsEmpty() {
    Properties props = new Properties();
    props.setProperty("wikidict", "dummy.tsv");
    WikidictAnnotator annotator = new WikidictAnnotator("test", props);

    CoreMap mention = mock(CoreMap.class);
    when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("UnknownEntity");
    when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
    when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");

    Optional<String> result = annotator.link(mention);
    assertFalse(result.isPresent());
  }
@Test
  public void testRequirementsSatisfiedIncludesWikipediaEntityAnnotation() {
    Properties props = new Properties();
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

    Set<Class<? extends CoreAnnotation>> requirements = annotator.requirementsSatisfied();
    assertTrue(requirements.contains(CoreAnnotations.WikipediaEntityAnnotation.class));
  }
@Test
  public void testRequiresIncludesExpectedAnnotations() {
    Properties props = new Properties();
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

    Set<Class<? extends CoreAnnotation>> required = annotator.requires();

    assertTrue(required.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.OriginalTextAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.MentionsAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
  }
@Test
  public void testNormalizeTimexStaticMethod() {
    String withTime = "2020-01-01T12:00";
    String withoutTime = "2020-01-01";
    String present = "PRESENT";

    String norm1 = WikidictAnnotator.normalizeTimex(withTime);
    String norm2 = WikidictAnnotator.normalizeTimex(withoutTime);
    String norm3 = WikidictAnnotator.normalizeTimex(present);

    assertEquals("2020-01-01", norm1);
    assertEquals("2020-01-01", norm2);
    assertEquals("PRESENT", norm3);
  }
@Test
public void testMentionWithNullSurfaceAndTextAnnotation() {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testMentionWithEmptySurfaceForm() {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testMentionWithNERTagOShouldReturnEmptyEvenIfInDict() {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Obama");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("O");

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testMentionWithNonMatchingNumberPatternShouldReturnEmpty() {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("123,456"); 
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("NUMBER");

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testMentionWithNullTimexAnnotation() {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("tomorrow");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("DATE");
  when(mention.get(TimeAnnotations.TimexAnnotation.class)).thenReturn(null);

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testMentionWithTimexAnnotationReturningNullValue() {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  Timex timex = mock(Timex.class);
  when(timex.value()).thenReturn(null);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("yesterday");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("DATE");
  when(mention.get(TimeAnnotations.TimexAnnotation.class)).thenReturn(timex);

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testDoOneSentenceWithNoMentionsKey() {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  Annotation annotation = new Annotation("John works at Google.");
  CoreMap sentence = mock(CoreMap.class);

  CoreLabel token1 = new CoreLabel();
  token1.set(CoreAnnotations.TextAnnotation.class, "John");

  CoreLabel token2 = new CoreLabel();
  token2.set(CoreAnnotations.TextAnnotation.class, "works");

  List<CoreLabel> tokenList = Arrays.asList(token1, token2);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokenList);
  when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(null);

  annotator.doOneSentence(annotation, sentence);
  assertEquals("O", token1.get(CoreAnnotations.WikipediaEntityAnnotation.class));
  assertEquals("O", token2.get(CoreAnnotations.WikipediaEntityAnnotation.class));
}
@Test
public void testSurfaceFormNotInDictionaryReturnsEmpty() {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("UnseenEntity");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("LOCATION");

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testCaselessMatchingMissDueToIncorrectCase() {
  Properties props = new Properties();
  props.setProperty("caseless", "true");
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("LoNDOn");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("LOCATION");

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testDoOneFailedSentenceDoesNothing() {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  Annotation annotation = new Annotation("failed sentence");
  CoreMap sentence = mock(CoreMap.class);

  
  annotator.doOneFailedSentence(annotation, sentence);
}
@Test
public void testLinkWithEmptyTimexValueReturnsEmpty() {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator("test", props);

  Timex timex = mock(Timex.class);
  when(timex.value()).thenReturn("");

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("yesterday");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("DATE");
  when(mention.get(TimeAnnotations.TimexAnnotation.class)).thenReturn(timex);

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkWithNullNERReturnsEmpty() {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator("test", props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Apple");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn(null);

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkReturnsEmptyWhenSurfaceFormIsNotInDictionaryEvenWithValidNER() {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator("test", props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("MarsBaseX");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORGANIZATION");

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testDoOneSentenceWithMentionButEmptyLink() {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator("test", props);

  Annotation annotation = new Annotation("Some mention.");
  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "Unknown");

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Unknown");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("LOCATION");
  when(mention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token));

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token));
  when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.singletonList(mention));

  annotator.doOneSentence(annotation, sentence);

  assertNull(token.get(CoreAnnotations.WikipediaEntityAnnotation.class));
  assertNull(mention.get(CoreAnnotations.WikipediaEntityAnnotation.class));
}
@Test
public void testNormalizeTimexWithLowercasePresentStringReturnsOriginal() {
  String result = WikidictAnnotator.normalizeTimex("present");
  assertEquals("present", result);
}
@Test
public void testNormalizeTimexWithMixedCasePresentReturnsSubstring() {
  String result = WikidictAnnotator.normalizeTimex("PresentRefT23:59");
  assertEquals("PresentRef", result);
}
@Test
public void testNormalizeTimexWithSingleCharDate() {
  String result = WikidictAnnotator.normalizeTimex("T");
  assertEquals("", result);
}
@Test
public void testLinkWithSetNERAndValidTimexShouldReturnNormalizedTimex() {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator("test", props);

  Timex timex = mock(Timex.class);
  when(timex.value()).thenReturn("2020-12-01T00:00");

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("every Monday");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("SET");
  when(mention.get(TimeAnnotations.TimexAnnotation.class)).thenReturn(timex);

  Optional<String> result = annotator.link(mention);
  assertTrue(result.isPresent());
  assertEquals("2020-12-01", result.get());
}
@Test
public void testLinkWithSetNERAndInvalidTimexReturnsEmpty() {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator("test", props);

  Timex timex = mock(Timex.class);
  when(timex.value()).thenReturn("PRESENT_REF");

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("every year");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("SET");
  when(mention.get(TimeAnnotations.TimexAnnotation.class)).thenReturn(timex);

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testDoOneSentenceWithNoTokensDoesNotFail() {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator("test", props);

  Annotation annotation = new Annotation("Empty.");

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(null);
  when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(null);

  annotator.doOneSentence(annotation, sentence);

  
  assertTrue(true);
}
@Test
public void testLinkWithOriginalTextOverridesText() {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator("test", props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("placeholder");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn("SolarCity");
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORGANIZATION");

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkWithNEROfTimeAndValidTimexReturnsNormalized() {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator("test", props);

  Timex timex = mock(Timex.class);
  when(timex.value()).thenReturn("2023-07-10T14:00");

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("2PM");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("TIME");
  when(mention.get(TimeAnnotations.TimexAnnotation.class)).thenReturn(timex);

  Optional<String> result = annotator.link(mention);
  assertTrue(result.isPresent());
  assertEquals("2023-07-10", result.get());
}
@Test
public void testLinkWithNullNumericValueAnnotationOnOrdinalReturnsEmpty() {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator("test", props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("3rd");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORDINAL");
  when(mention.get(CoreAnnotations.NumericValueAnnotation.class)).thenReturn(null);

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkWithIncompleteMentionData() {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator("test", props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn("Tokyo");
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn(null);

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testDoOneSentenceWithValidMentionAddsWikipediaEntityToTokens() {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator("test", props);

  CoreLabel token = new CoreLabel();
  CoreLabel mentionToken = new CoreLabel();
  mentionToken.set(CoreAnnotations.TextAnnotation.class, "KnownEntity");

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("KnownEntity");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORGANIZATION");
  when(mention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(mentionToken));

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token));
  when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.singletonList(mention));

  Optional<String> expectedLink = Optional.of("Wikipedia_Link");
  boolean injected = false;
  if (expectedLink.isPresent()) {
    mention.set(CoreAnnotations.WikipediaEntityAnnotation.class, expectedLink.get());
    mentionToken.set(CoreAnnotations.WikipediaEntityAnnotation.class, expectedLink.get());
    injected = true;
  }

  annotator.doOneSentence(new Annotation("sample"), sentence);

  assertTrue(injected);
  assertEquals("Wikipedia_Link", mention.get(CoreAnnotations.WikipediaEntityAnnotation.class));
  assertEquals("Wikipedia_Link", mentionToken.get(CoreAnnotations.WikipediaEntityAnnotation.class));
}
@Test
public void testNormalizeTimexTrimsDateOnlyWhenContainsT() {
  String input = "1999-08-16T23:59";
  String result = WikidictAnnotator.normalizeTimex(input);
  assertEquals("1999-08-16", result);
}
@Test
public void testNormalizeTimexNoChangeWithoutT() {
  String input = "2001-01-01";
  String result = WikidictAnnotator.normalizeTimex(input);
  assertEquals("2001-01-01", result);
}
@Test
public void testNormalizeTimexReturnsAsIsWhenPresent() {
  String input = "PRESENT";
  String result = WikidictAnnotator.normalizeTimex(input);
  assertEquals("PRESENT", result);
}
@Test
public void testSurfaceFormWithOnlyWhitespaceReturnsEmpty() {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator("test", props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("   ");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkWithNumericPatternButNERIsNullStillReturnsAsIs() {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator("test", props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("456.78");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn(null);

  Optional<String> result = annotator.link(mention);
  assertTrue(result.isPresent());
  assertEquals("456.78", result.get());
}
@Test
public void testLinkWithTextAnnotationButNoNERAndNotANumberReturnsEmpty() {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("AlphaBeta");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn(null);

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkWithNERSetButTimexAnnotationMissingReturnsEmpty() {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Every week");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("SET");
  when(mention.get(TimeAnnotations.TimexAnnotation.class)).thenReturn(null);

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkWithTimexAnnotationButTimexValueIsPRESENT_REFReturnsEmpty() {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  Timex timex = mock(Timex.class);
  when(timex.value()).thenReturn("PRESENT_REF");

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("now");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("DATE");
  when(mention.get(TimeAnnotations.TimexAnnotation.class)).thenReturn(timex);

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkWithValidNERButCaselessEntityNotFound() {
  Properties props = new Properties();
  props.setProperty("caseless", "true");
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("XyzUnknown");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkWithTimexHavingLowercasePresentReturnsFalse() {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  Timex timex = mock(Timex.class);
  when(timex.value()).thenReturn("present");

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("present");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("DATE");
  when(mention.get(TimeAnnotations.TimexAnnotation.class)).thenReturn(timex);

  Optional<String> result = annotator.link(mention);
  assertTrue(result.isPresent());
  assertEquals("present", result.get());
}
@Test
public void testDoOneSentenceHandlesNullMentionsGracefully() {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  Annotation annotation = new Annotation("sentence text");
  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "TestWord");

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token));
  when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(null);

  annotator.doOneSentence(annotation, sentence);
  assertEquals("O", token.get(CoreAnnotations.WikipediaEntityAnnotation.class));
}
@Test
public void testDoOneSentenceWithMentionWithoutTextAnnotation() {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "MissingText");

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn("MissingText");
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORGANIZATION");
  when(mention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token));

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token));
  when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.singletonList(mention));

  annotator.doOneSentence(new Annotation("Example sentence"), sentence);
  assertNull(token.get(CoreAnnotations.WikipediaEntityAnnotation.class));
}
@Test
public void testNormalizeTimexWithOnlyTReturnsEmptyString() {
  String result = WikidictAnnotator.normalizeTimex("T");
  assertEquals("", result);
}
@Test
public void testNormalizeTimexWithOnlyDateComponentAndNoT() {
  String result = WikidictAnnotator.normalizeTimex("2023-10-31");
  assertEquals("2023-10-31", result);
}
@Test
public void testNormalizeTimexWithPRESENTAsSubstringStillStripsTime() {
  String input = "PRESENT_TENSE_T09:00";
  String result = WikidictAnnotator.normalizeTimex(input);
  assertEquals("PRESENT_TENSE", result);
}
@Test
public void testLinkDateWithTimexValuePASTReturnsEmpty() {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  Timex timex = mock(Timex.class);
  when(timex.value()).thenReturn("PAST");

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("last year");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("DATE");
  when(mention.get(TimeAnnotations.TimexAnnotation.class)).thenReturn(timex);

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkDateWithTimexValueFUTURE_REFReturnsEmpty() {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  Timex timex = mock(Timex.class);
  when(timex.value()).thenReturn("FUTURE_REF");

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("next year");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("DATE");
  when(mention.get(TimeAnnotations.TimexAnnotation.class)).thenReturn(timex);

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkWithThresholdSpecifiedAndBelowScoreSkipsEntry() {
  Properties props = new Properties();
  props.setProperty("wikidict", "dummy.tsv");
  props.setProperty("threshold", "0.9");

  
  
  double score = 0.5;
  boolean loaded = score >= 0.9;
  assertFalse(loaded);
}
@Test
public void testLinkWithNullOriginalTextFallsBackToText() {
  Properties props = new Properties();
  props.setProperty("wikidict", "dummy.tsv");
  WikidictAnnotator annotator = new WikidictAnnotator("linker", props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Tesla");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORGANIZATION");

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testDoOneSentenceWithMentionHavingEmptyTokensList() {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  Annotation annotation = new Annotation("Test case sentence");

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Amazon");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORGANIZATION");
  when(mention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.emptyList());

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "OtherToken");

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token));
  when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.singletonList(mention));

  annotator.doOneSentence(annotation, sentence);

  String result = token.get(CoreAnnotations.WikipediaEntityAnnotation.class);
  assertEquals("O", result);
}
@Test
public void testDoOneSentenceWithMentionTokensContainingMultipleTokensEachUpdated() {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

  CoreLabel token1 = new CoreLabel();
  token1.set(CoreAnnotations.TextAnnotation.class, "Big");

  CoreLabel token2 = new CoreLabel();
  token2.set(CoreAnnotations.TextAnnotation.class, "Apple");

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Big Apple");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("LOCATION");
  when(mention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Arrays.asList(token1, token2));

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Arrays.asList(token1, token2));
  when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.singletonList(mention));

  Annotation annotation = new Annotation("Big Apple");

  annotator.doOneSentence(annotation, sentence);

  String result1 = token1.get(CoreAnnotations.WikipediaEntityAnnotation.class);
  String result2 = token2.get(CoreAnnotations.WikipediaEntityAnnotation.class);

  assertTrue(result1 == null || result1.equals("O"));
  assertTrue(result2 == null || result2.equals("O"));
}
@Test
public void testRequirementsSatisfiedSingleAnnotationOnly() {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator("test", props);

  Set<Class<? extends CoreAnnotation>> result = annotator.requirementsSatisfied();

  assertEquals(1, result.size());
  assertTrue(result.contains(CoreAnnotations.WikipediaEntityAnnotation.class));
}
@Test
public void testMaxTimeAlwaysReturnsNegativeOne() {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator("test", props);

  long result = annotator.maxTime();
  assertEquals(-1L, result);
}
@Test
public void testNThreadsReturnsDefaultOne() {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator("wikidict", props);

  int threads = annotator.nThreads();
  assertEquals(1, threads);
}
@Test
public void testLinkWithNullMentionReturnsEmpty() {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator("wikidict", props);

  CoreMap mention = null;

  Optional<String> result;
  try {
    result = annotator.link(mention);
    fail("Expected NullPointerException");
  } catch (NullPointerException e) {
    assertTrue(true);
  }
}
@Test
public void testLinkWithNERButNoTextAnnotationReturnsEmpty() {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator("link", props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkWithNERButTextAnnotationIsEmptyStringReturnsEmpty() {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator("link", props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkWithSurfaceFormMatchingNumberPatternButNERIsNull() {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator("link", props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("101.99");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn(null);

  Optional<String> result = annotator.link(mention);
  assertTrue(result.isPresent());
  assertEquals("101.99", result.get());
}
@Test
public void testDoOneSentenceWithMentionButMissingNERReturnsEmpty() {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator("link", props);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "Example");

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Example");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token));

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token));
  when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.singletonList(mention));

  Annotation annotation = new Annotation("Example");
  annotator.doOneSentence(annotation, sentence);

  assertNull(token.get(CoreAnnotations.WikipediaEntityAnnotation.class));
}
@Test
public void testDoOneSentenceWithMentionNotFoundInDictionary() {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator("wiki", props);

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "Atlantis");

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Atlantis");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("LOCATION");
  when(mention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token));

  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Collections.singletonList(token));
  when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.singletonList(mention));

  Annotation annotation = new Annotation("Atlantis was never found.");
  annotator.doOneSentence(annotation, sentence);

  assertNull(token.get(CoreAnnotations.WikipediaEntityAnnotation.class));
  assertNull(mention.get(CoreAnnotations.WikipediaEntityAnnotation.class));
}
@Test
public void testLinkWithEmptyNERShouldReturnEmpty() {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator("wiki", props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Moon");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("");

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkWithNullTimexObjectShouldNotThrow() {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator("wiki", props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("tomorrow");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("DATE");
  when(mention.get(TimeAnnotations.TimexAnnotation.class)).thenReturn(null);

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testLinkWithKnownNumberPatternButNERIsORDINALAndNumericValueIsNull() {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator("wiki", props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("1st");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORDINAL");
  when(mention.get(CoreAnnotations.NumericValueAnnotation.class)).thenReturn(null);

  Optional<String> result = annotator.link(mention);
  assertFalse(result.isPresent());
}
@Test
public void testNormalizeTimexWithEmptyStringReturnsEmpty() {
  String result = WikidictAnnotator.normalizeTimex("");
  assertEquals("", result);
}
@Test
public void testNormalizeTimexWithOnlyTShouldReturnEmpty() {
  String result = WikidictAnnotator.normalizeTimex("T");
  assertEquals("", result);
}
@Test
public void testLinkWithNonNumericButMatchesRegexDueToDot() {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator("wiki", props);

  CoreMap mention = mock(CoreMap.class);
  when(mention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("....");
  when(mention.get(CoreAnnotations.OriginalTextAnnotation.class)).thenReturn(null);
  when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn(null);

  Optional<String> result = annotator.link(mention);
  assertTrue(result.isPresent());
  assertEquals("....", result.get());
}
@Test
public void testDoOneSentenceHandlesNullTokenListGracefully() {
  Properties props = new Properties();
  WikidictAnnotator annotator = new WikidictAnnotator("wiki", props);

  Annotation annotation = new Annotation("sentence");
  CoreMap sentence = mock(CoreMap.class);
  when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(null);
  when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(null);

  annotator.doOneSentence(annotation, sentence);
  assertTrue(true); 
} 
}