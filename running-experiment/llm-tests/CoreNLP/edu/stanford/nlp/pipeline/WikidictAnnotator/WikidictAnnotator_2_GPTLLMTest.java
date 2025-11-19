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

public class WikidictAnnotator_2_GPTLLMTest {

 @Test
  public void testLinkWithExactDictionaryMatch() {
    Properties props = new Properties();
    props.setProperty("wikidict", "mock/path.tsv");

    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);
    WikidictAnnotator spyAnnotator = spy(annotator);

    Map<String, String> testDictionary = new HashMap<>();
    testDictionary.put("Barack Obama", "Barack_Obama");
    doReturn(testDictionary).when(spyAnnotator).link(any(CoreMap.class));
//     CoreMap mention = new TypesafeMap();
//     mention.set(CoreAnnotations.TextAnnotation.class, "Barack Obama");
//     mention.set(CoreAnnotations.OriginalTextAnnotation.class, "Barack Obama");
//     mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

//     Optional<String> link = spyAnnotator.link(mention);

//     assertTrue(link.isPresent());
//     assertEquals("Barack_Obama", link.get());
  }
@Test
  public void testLinkCaseInsensitiveDictionaryMatch() {
    Properties props = new Properties();
    props.setProperty("wikidict", "mock/path.tsv");
    props.setProperty("caseless", "true");

    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);
    WikidictAnnotator spyAnnotator = spy(annotator);

    Map<String, String> testDictionary = new HashMap<>();
    testDictionary.put("apple", "Apple_Inc.");
    doReturn(testDictionary).when(spyAnnotator).link(any(CoreMap.class));

//     CoreMap mention = new TypesafeMap();
//     mention.set(CoreAnnotations.TextAnnotation.class, "Apple");
//     mention.set(CoreAnnotations.OriginalTextAnnotation.class, "Apple");
//     mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");

//     Optional<String> link = spyAnnotator.link(mention);

//     assertTrue(link.isPresent());
//     assertEquals("Apple_Inc.", link.get());
  }
@Test
  public void testLinkDateTimexReturnsNormalized() {
    Properties props = new Properties();
    props.setProperty("wikidict", "mock");

    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);
    Timex mockTimex = mock(Timex.class);
    when(mockTimex.value()).thenReturn("2022-03-15T00:00");

//     CoreMap mention = new TypesafeMap();
//     mention.set(CoreAnnotations.TextAnnotation.class, "March 15, 2022");
//     mention.set(CoreAnnotations.OriginalTextAnnotation.class, "March 15, 2022");
//     mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");
//     mention.set(TimeAnnotations.TimexAnnotation.class, mockTimex);

//     Optional<String> result = annotator.link(mention);

//     assertTrue(result.isPresent());
//     assertEquals("2022-03-15", result.get());
  }
@Test
  public void testLinkTimexWithPRESENTReturnsEmpty() {
    Properties props = new Properties();
    props.setProperty("wikidict", "mock");

    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);
    Timex mockTimex = mock(Timex.class);
    when(mockTimex.value()).thenReturn("PRESENT");

//     CoreMap mention = new TypesafeMap();
//     mention.set(CoreAnnotations.TextAnnotation.class, "now");
//     mention.set(CoreAnnotations.OriginalTextAnnotation.class, "now");
//     mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");
//     mention.set(TimeAnnotations.TimexAnnotation.class, mockTimex);

//     Optional<String> result = annotator.link(mention);

//     assertFalse(result.isPresent());
  }
@Test
  public void testLinkOrdinalReturnsNumericString() {
    Properties props = new Properties();
    props.setProperty("wikidict", "mock");

    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

//     CoreMap mention = new TypesafeMap();
//     mention.set(CoreAnnotations.TextAnnotation.class, "third");
//     mention.set(CoreAnnotations.OriginalTextAnnotation.class, "third");
//     mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORDINAL");
//     mention.set(CoreAnnotations.NumericValueAnnotation.class, 3);

//     Optional<String> result = annotator.link(mention);

//     assertTrue(result.isPresent());
//     assertEquals("3", result.get());
  }
@Test
  public void testLinkNumberFormatReturnsSameValue() {
    Properties props = new Properties();
    props.setProperty("wikidict", "mock");

    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

//     CoreMap mention = new TypesafeMap();
//     mention.set(CoreAnnotations.TextAnnotation.class, "42");
//     mention.set(CoreAnnotations.OriginalTextAnnotation.class, "42");
//     mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "NUMBER");

//     Optional<String> result = annotator.link(mention);

//     assertTrue(result.isPresent());
//     assertEquals("42", result.get());
  }
@Test
  public void testLinkUnmatchedReturnsEmpty() {
    Properties props = new Properties();
    props.setProperty("wikidict", "mock");

    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

//     CoreMap mention = new TypesafeMap();
//     mention.set(CoreAnnotations.TextAnnotation.class, "Qwerty");
//     mention.set(CoreAnnotations.OriginalTextAnnotation.class, "Qwerty");
//     mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "MISC");

//     Optional<String> result = annotator.link(mention);

//     assertFalse(result.isPresent());
  }
@Test
  public void testNormalizeTimexWithTimeComponent() {
    String input = "2023-09-12T21:45";
    String output = WikidictAnnotator.normalizeTimex(input);
    assertEquals("2023-09-12", output);
  }
@Test
  public void testNormalizeTimexWithoutTimeComponent() {
    String input = "1990-01-01";
    String output = WikidictAnnotator.normalizeTimex(input);
    assertEquals("1990-01-01", output);
  }
@Test
  public void testNormalizeTimexIsPresentPassThrough() {
    String input = "PRESENT";
    String output = WikidictAnnotator.normalizeTimex(input);
    assertEquals("PRESENT", output);
  }
@Test
  public void testRequirementsSatisfiedIncludesWikipediaAnnotation() {
    Properties props = new Properties();
    props.setProperty("wikidict", "mock");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

    Set<Class<? extends CoreAnnotation>> sats = annotator.requirementsSatisfied();

    assertTrue(sats.contains(CoreAnnotations.WikipediaEntityAnnotation.class));
  }
@Test
  public void testRequiresSetIncludesMentionDependencies() {
    Properties props = new Properties();
    props.setProperty("wikidict", "mock");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

    Set<Class<? extends CoreAnnotation>> requires = annotator.requires();

    assertTrue(requires.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(requires.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(requires.contains(CoreAnnotations.SentencesAnnotation.class));
    assertTrue(requires.contains(CoreAnnotations.OriginalTextAnnotation.class));
    assertTrue(requires.contains(CoreAnnotations.MentionsAnnotation.class));
  }
@Test
  public void testDoOneSentenceAddsWikipediaLinkToAllMentionTokens() {
    Properties props = new Properties();
    props.setProperty("wikidict", "mock");

    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);
    WikidictAnnotator spyAnnotator = spy(annotator);
    doReturn(Optional.of("Barack_Obama")).when(spyAnnotator).link(any(CoreMap.class));

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.WikipediaEntityAnnotation.class, "O");

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.WikipediaEntityAnnotation.class, "O");

    List<CoreLabel> tokens = Arrays.asList(token1, token2);

//     CoreMap mention = new TypesafeMap();
//     mention.set(CoreAnnotations.TextAnnotation.class, "Barack Obama");
//     mention.set(CoreAnnotations.OriginalTextAnnotation.class, "Barack Obama");
//     mention.set(CoreAnnotations.TokensAnnotation.class, tokens);

//     List<CoreMap> mentions = Collections.singletonList(mention);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
//     when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(mentions);

    Annotation annotation = new Annotation("Any sentence");

    spyAnnotator.doOneSentence(annotation, sentence);

    String val1 = token1.get(CoreAnnotations.WikipediaEntityAnnotation.class);
    String val2 = token2.get(CoreAnnotations.WikipediaEntityAnnotation.class);

//     assertEquals("Barack_Obama", mention.get(CoreAnnotations.WikipediaEntityAnnotation.class));
    assertEquals("Barack_Obama", val1);
    assertEquals("Barack_Obama", val2);
  }
@Test
  public void testDoOneSentenceNoMatchPreservesO() {
    Properties props = new Properties();
    props.setProperty("wikidict", "mock");

    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);
    WikidictAnnotator spyAnnotator = spy(annotator);
    doReturn(Optional.empty()).when(spyAnnotator).link(any(CoreMap.class));

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.WikipediaEntityAnnotation.class, "O");

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.WikipediaEntityAnnotation.class, "O");

    List<CoreLabel> tokens = Arrays.asList(token1, token2);

//     CoreMap mention = new TypesafeMap();
//     mention.set(CoreAnnotations.TokensAnnotation.class, tokens);

//     List<CoreMap> mentions = Collections.singletonList(mention);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
//     when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(mentions);

    Annotation annotation = new Annotation("Some sentence");

    spyAnnotator.doOneSentence(annotation, sentence);

    assertEquals("O", token1.get(CoreAnnotations.WikipediaEntityAnnotation.class));
    assertEquals("O", token2.get(CoreAnnotations.WikipediaEntityAnnotation.class));
//     assertNull(mention.get(CoreAnnotations.WikipediaEntityAnnotation.class));
  }
@Test
  public void testLinkWithNullOriginalTextFallsBackToText() {
    Properties props = new Properties();
    props.setProperty("wikidict", "mock");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);
    WikidictAnnotator spyAnnotator = spy(annotator);

    Map<String, String> dictionary = new HashMap<>();
    dictionary.put("SpaceX", "SpaceX_Link");
//     doReturn(dictionary).when(spyAnnotator).dictionary;

//     CoreMap mention = new TypesafeMap();
//     mention.set(CoreAnnotations.TextAnnotation.class, "SpaceX");
//     mention.set(CoreAnnotations.OriginalTextAnnotation.class, null);
//     mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");

//     Optional<String> link = spyAnnotator.link(mention);
//     assertTrue(link.isPresent());
//     assertEquals("SpaceX_Link", link.get());
  }
@Test
  public void testLinkWithWhitespaceInSurfaceForm() {
    Properties props = new Properties();
    props.setProperty("wikidict", "mock");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);
    WikidictAnnotator spyAnnotator = spy(annotator);

    Map<String, String> dictionary = new HashMap<>();
    dictionary.put("New York", "New_York");
//     doReturn(dictionary).when(spyAnnotator).dictionary;

//     CoreMap mention = new TypesafeMap();
//     mention.set(CoreAnnotations.TextAnnotation.class, " New York ");
//     mention.set(CoreAnnotations.OriginalTextAnnotation.class, " New York ");
//     mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");

//     Optional<String> link = spyAnnotator.link(mention);
//     assertFalse(link.isPresent()); 
  }
@Test
  public void testLinkWithCaselessTrueAndMixedCaseMatch() {
    Properties props = new Properties();
    props.setProperty("wikidict", "mock");
    props.setProperty("caseless", "true");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);
    WikidictAnnotator spyAnnotator = spy(annotator);

    Map<String, String> dictionary = new HashMap<>();
    dictionary.put("google", "Google_Inc");
//     doReturn(dictionary).when(spyAnnotator).dictionary;

//     CoreMap mention = new TypesafeMap();
//     mention.set(CoreAnnotations.TextAnnotation.class, "Google");
//     mention.set(CoreAnnotations.OriginalTextAnnotation.class, "Google");
//     mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");

//     Optional<String> link = spyAnnotator.link(mention);
//     assertTrue(link.isPresent());
//     assertEquals("Google_Inc", link.get());
  }
@Test
  public void testLinkWithNER_Tag_O_ReturnsEmpty() {
    Properties props = new Properties();
    props.setProperty("wikidict", "mock");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);
    WikidictAnnotator spyAnnotator = spy(annotator);

    Map<String, String> dictionary = new HashMap<>();
    dictionary.put("Obama", "Barack_Obama");
//     doReturn(dictionary).when(spyAnnotator).dictionary;

//     CoreMap mention = new TypesafeMap();
//     mention.set(CoreAnnotations.TextAnnotation.class, "Obama");
//     mention.set(CoreAnnotations.OriginalTextAnnotation.class, "Obama");
//     mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

//     Optional<String> link = spyAnnotator.link(mention);
//     assertFalse(link.isPresent());
  }
@Test
  public void testLinkWithNullNER_ReturnsEmpty() {
    Properties props = new Properties();
    props.setProperty("wikidict", "mock");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);
    WikidictAnnotator spyAnnotator = spy(annotator);

    Map<String, String> dictionary = new HashMap<>();
    dictionary.put("Something", "Something_Link");
//     doReturn(dictionary).when(spyAnnotator).dictionary;

//     CoreMap mention = new TypesafeMap();
//     mention.set(CoreAnnotations.TextAnnotation.class, "Something");
//     mention.set(CoreAnnotations.OriginalTextAnnotation.class, "Something");
    

//     Optional<String> link = spyAnnotator.link(mention);
//     assertFalse(link.isPresent());
  }
@Test
  public void testLinkWhenTimexObjectExistsButValueIsNull() {
    Properties props = new Properties();
    props.setProperty("wikidict", "mock");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);
    Timex mockTimex = mock(Timex.class);
    when(mockTimex.value()).thenReturn(null);

//     CoreMap mention = new TypesafeMap();
//     mention.set(CoreAnnotations.TextAnnotation.class, "yesterday");
//     mention.set(CoreAnnotations.OriginalTextAnnotation.class, "yesterday");
//     mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");
//     mention.set(TimeAnnotations.TimexAnnotation.class, mockTimex);

//     Optional<String> result = annotator.link(mention);

//     assertFalse(result.isPresent());
  }
@Test
  public void testLinkWithSurfaceFormMatchingNumberPatternButNoNER() {
    Properties props = new Properties();
    props.setProperty("wikidict", "mock");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

//     CoreMap mention = new TypesafeMap();
//     mention.set(CoreAnnotations.TextAnnotation.class, "123.456");
//     mention.set(CoreAnnotations.OriginalTextAnnotation.class, "123.456");
    

//     Optional<String> result = annotator.link(mention);
//     assertTrue(result.isPresent());
//     assertEquals("123.456", result.get());
  }
@Test
  public void testLinkWithDictionaryMatchAndNoNER() {
    Properties props = new Properties();
    props.setProperty("wikidict", "mock");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);
    WikidictAnnotator spyAnnotator = spy(annotator);

    Map<String, String> dictionary = new HashMap<>();
    dictionary.put("Tesla", "Tesla_Inc");
//     doReturn(dictionary).when(spyAnnotator).dictionary;

//     CoreMap mention = new TypesafeMap();
//     mention.set(CoreAnnotations.TextAnnotation.class, "Tesla");
//     mention.set(CoreAnnotations.OriginalTextAnnotation.class, "Tesla");
    

//     Optional<String> result = spyAnnotator.link(mention);
//     assertFalse(result.isPresent()); 
  }
@Test
  public void testTimexWithSpecialReferenceValueReturnsEmpty() {
    Properties props = new Properties();
    props.setProperty("wikidict", "mock");

    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);
    String[] specialVals = { "PRESENT_REF", "PAST", "FUTURE_REF" };

    Timex mockTimex1 = mock(Timex.class);
    when(mockTimex1.value()).thenReturn(specialVals[0]);
//     CoreMap mention1 = new TypesafeMap();
//     mention1.set(CoreAnnotations.TextAnnotation.class, "TimeRef");
//     mention1.set(CoreAnnotations.OriginalTextAnnotation.class, "TimeRef");
//     mention1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");
//     mention1.set(TimeAnnotations.TimexAnnotation.class, mockTimex1);
//     Optional<String> link1 = annotator.link(mention1);
//     assertFalse(link1.isPresent());

    Timex mockTimex2 = mock(Timex.class);
    when(mockTimex2.value()).thenReturn(specialVals[1]);
//     CoreMap mention2 = new TypesafeMap();
//     mention2.set(CoreAnnotations.TextAnnotation.class, "Past");
//     mention2.set(CoreAnnotations.OriginalTextAnnotation.class, "Past");
//     mention2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");
//     mention2.set(TimeAnnotations.TimexAnnotation.class, mockTimex2);
//     Optional<String> link2 = annotator.link(mention2);
//     assertFalse(link2.isPresent());

    Timex mockTimex3 = mock(Timex.class);
    when(mockTimex3.value()).thenReturn(specialVals[2]);
//     CoreMap mention3 = new TypesafeMap();
//     mention3.set(CoreAnnotations.TextAnnotation.class, "Future");
//     mention3.set(CoreAnnotations.OriginalTextAnnotation.class, "Future");
//     mention3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "SET");
//     mention3.set(TimeAnnotations.TimexAnnotation.class, mockTimex3);
//     Optional<String> link3 = annotator.link(mention3);
//     assertFalse(link3.isPresent());
  }
@Test
  public void testLinkWithEmptySurfaceFormReturnsEmpty() {
    Properties props = new Properties();
    props.setProperty("wikidict", "test");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

//     CoreMap mention = new TypesafeMap();
//     mention.set(CoreAnnotations.TextAnnotation.class, "");
//     mention.set(CoreAnnotations.OriginalTextAnnotation.class, "");
//     mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

//     Optional<String> link = annotator.link(mention);
//     assertFalse(link.isPresent());
  }
@Test
  public void testLinkWithSurfaceFormNullReturnsEmpty() {
    Properties props = new Properties();
    props.setProperty("wikidict", "test");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

//     CoreMap mention = new TypesafeMap();
//     mention.set(CoreAnnotations.TextAnnotation.class, null);
//     mention.set(CoreAnnotations.OriginalTextAnnotation.class, null);
//     mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");

//     Optional<String> result = annotator.link(mention);
//     assertFalse(result.isPresent());
  }
@Test
  public void testLinkWithOnlyTextAnnotationAndNotOriginalText() {
    Properties props = new Properties();
    props.setProperty("wikidict", "test");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);
    WikidictAnnotator spyAnnotator = spy(annotator);

    Map<String, String> map = new HashMap<>();
    map.put("eiffel tower", "Eiffel_Tower");
//     doReturn(map).when(spyAnnotator).dictionary;

//     CoreMap mention = new TypesafeMap();
//     mention.set(CoreAnnotations.TextAnnotation.class, "eiffel tower");
//     mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");

//     Optional<String> result = spyAnnotator.link(mention);
//     assertTrue(result.isPresent());
//     assertEquals("Eiffel_Tower", result.get());
  }
@Test
  public void testLinkWithNullMentionReturnsEmpty() {
    Properties props = new Properties();
    props.setProperty("wikidict", "test");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);
    Optional<String> link = annotator.link(null);
    assertFalse(link.isPresent());
  }
@Test
  public void testDoOneFailedSentenceHasNoEffect() {
    Properties props = new Properties();
    props.setProperty("wikidict", "test");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

    CoreMap sentence = mock(CoreMap.class);
    Annotation annotation = new Annotation("Example");
    annotator.doOneFailedSentence(annotation, sentence);

    
  }
@Test
  public void testDoOneSentenceWithNoMentionsAnnotation() {
    Properties props = new Properties();
    props.setProperty("wikidict", "test");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.WikipediaEntityAnnotation.class, "O");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.WikipediaEntityAnnotation.class, "O");
    List<CoreLabel> tokenList = Arrays.asList(token1, token2);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokenList);
    when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(null);

    Annotation annotation = new Annotation("Sentence without mentions");
    annotator.doOneSentence(annotation, sentence);

    assertEquals("O", token1.get(CoreAnnotations.WikipediaEntityAnnotation.class));
    assertEquals("O", token2.get(CoreAnnotations.WikipediaEntityAnnotation.class));
  }
@Test
  public void testLinkWithDictionaryValueOverriddenByReuseCheck() {
    Properties props = new Properties();
    props.setProperty("wikidict", "test");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);
    WikidictAnnotator spyAnnotator = spy(annotator);

    Map<String, String> dict = new HashMap<>();
    dict.put("Mars", "Mars_Planet");
    dict.put("mars", "Mars_Chocolate");
//     doReturn(dict).when(spyAnnotator).dictionary;

//     CoreMap mention = new TypesafeMap();
//     mention.set(CoreAnnotations.TextAnnotation.class, "mars");
//     mention.set(CoreAnnotations.OriginalTextAnnotation.class, "mars");
//     mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");

//     Optional<String> result = spyAnnotator.link(mention);
//     assertTrue(result.isPresent());
//     assertEquals("Mars_Chocolate", result.get());
  }
@Test
  public void testTimexLinkWithUnsupportedNERTag() {
    Properties props = new Properties();
    props.setProperty("wikidict", "test");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

    Timex timex = mock(Timex.class);
    when(timex.value()).thenReturn("2042-01-01T12:00");

//     CoreMap mention = new TypesafeMap();
//     mention.set(CoreAnnotations.TextAnnotation.class, "2042");
//     mention.set(CoreAnnotations.OriginalTextAnnotation.class, "2042");
//     mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "NUMBER"); 
//     mention.set(TimeAnnotations.TimexAnnotation.class, timex);

//     Optional<String> result = annotator.link(mention);
//     assertFalse(result.isPresent());
  }
@Test
  public void testLinkWithThresholdFilteringBehavior() {
    Properties props = new Properties();
    props.setProperty("wikidict", "mock");
    props.setProperty("threshold", "0.9");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);
    
    
//     CoreMap mention = new TypesafeMap();
//     mention.set(CoreAnnotations.TextAnnotation.class, "Obama");
//     mention.set(CoreAnnotations.OriginalTextAnnotation.class, "Obama");
//     mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

//     Optional<String> result = annotator.link(mention);
//     assertFalse(result.isPresent()); 
  }
@Test
  public void testLinkWithTimexNullReturnsEmpty() {
    Properties props = new Properties();
    props.setProperty("wikidict", "x");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

//     CoreMap mention = new TypesafeMap();
//     mention.set(CoreAnnotations.TextAnnotation.class, "Tomorrow");
//     mention.set(CoreAnnotations.OriginalTextAnnotation.class, "Tomorrow");
//     mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");
    

//     Optional<String> result = annotator.link(mention);
//     assertFalse(result.isPresent());
  }
@Test
  public void testLinkWithTimexPresentValueAndNullType() {
    Properties props = new Properties();
    props.setProperty("wikidict", "x");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

    Timex timex = mock(Timex.class);
    when(timex.value()).thenReturn("PRESENT");

//     CoreMap mention = new TypesafeMap();
//     mention.set(CoreAnnotations.TextAnnotation.class, "now");
//     mention.set(CoreAnnotations.OriginalTextAnnotation.class, "now");
//     mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");
//     mention.set(TimeAnnotations.TimexAnnotation.class, timex);

//     Optional<String> result = annotator.link(mention);
//     assertFalse(result.isPresent());
  }
@Test
  public void testOriginalTextPresentAndTextNullResolvedCorrectly() {
    Properties props = new Properties();
    props.setProperty("wikidict", "x");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);
    WikidictAnnotator spy = spy(annotator);

    Map<String, String> dict = new HashMap<>();
    dict.put("spacex", "SpaceX_Page");
//     doReturn(dict).when(spy).dictionary;

//     CoreMap mention = new TypesafeMap();
//     mention.set(CoreAnnotations.OriginalTextAnnotation.class, "spacex");
    
//     mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");

//     Optional<String> result = spy.link(mention);
//     assertTrue(result.isPresent());
//     assertEquals("SpaceX_Page", result.get());
  }
@Test
  public void testMentionHasNoTokensAnnotationDoOneSentenceSkipsGracefully() {
    Properties props = new Properties();
    props.setProperty("wikidict", "x");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);
    WikidictAnnotator spy = spy(annotator);
    doReturn(Optional.of("Something")).when(spy).link(any(CoreMap.class));

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.WikipediaEntityAnnotation.class, "O");
    List<CoreLabel> tokens = Arrays.asList(token);

//     CoreMap mention = new TypesafeMap();
    

//     List<CoreMap> mentions = Arrays.asList(mention);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
//     when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(mentions);

    Annotation ann = new Annotation("abc");
    spy.doOneSentence(ann, sentence);

    assertEquals("O", token.get(CoreAnnotations.WikipediaEntityAnnotation.class));
  }
@Test
  public void testDoOneSentenceWithEmptyMentionsListRunsSuccessfully() {
    Properties props = new Properties();
    props.setProperty("wikidict", "x");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

    List<CoreLabel> tokens = Arrays.asList(new CoreLabel());

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
    when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(Collections.emptyList());

    Annotation ann = new Annotation("abc");
    annotator.doOneSentence(ann, sentence);

    String val = tokens.get(0).get(CoreAnnotations.WikipediaEntityAnnotation.class);
    assertEquals("O", val);
  }
@Test
  public void testDoOneSentenceWithNullTokensListSkipsGracefully() {
    Properties props = new Properties();
    props.setProperty("wikidict", "x");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(null);
    when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(null);

    Annotation ann = new Annotation("abc");
    annotator.doOneSentence(ann, sentence);
    
  }
@Test
  public void testLinkWithSetNERNormalizesTimex() {
    Properties props = new Properties();
    props.setProperty("wikidict", "x");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

    Timex timex = mock(Timex.class);
    when(timex.value()).thenReturn("2022-01-01T13:00");

//     CoreMap mention = new TypesafeMap();
//     mention.set(CoreAnnotations.TextAnnotation.class, "event date");
//     mention.set(CoreAnnotations.OriginalTextAnnotation.class, "event date");
//     mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "SET");
//     mention.set(TimeAnnotations.TimexAnnotation.class, timex);

//     Optional<String> result = annotator.link(mention);
//     assertTrue(result.isPresent());
//     assertEquals("2022-01-01", result.get());
  }
@Test
  public void testLinkWithNonNumericValueOfOrdinal() {
    Properties props = new Properties();
    props.setProperty("wikidict", "x");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

//     CoreMap mention = new TypesafeMap();
//     mention.set(CoreAnnotations.TextAnnotation.class, "first");
//     mention.set(CoreAnnotations.OriginalTextAnnotation.class, "first");
//     mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORDINAL");
    

//     Optional<String> result = annotator.link(mention);
//     assertFalse(result.isPresent());
  }
@Test
  public void testLinkWithCaselessTrueMixedCasing() {
    Properties props = new Properties();
    props.setProperty("wikidict", "x");
    props.setProperty("caseless", "true");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);
    WikidictAnnotator spy = spy(annotator);

    Map<String, String> dict = new HashMap<>();
    dict.put("google", "GoogleInc");
//     doReturn(dict).when(spy).dictionary;

//     CoreMap mention = new TypesafeMap();
//     mention.set(CoreAnnotations.TextAnnotation.class, "GoOgLe");
//     mention.set(CoreAnnotations.OriginalTextAnnotation.class, "GoOgLe");
//     mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");

//     Optional<String> result = spy.link(mention);
//     assertTrue(result.isPresent());
//     assertEquals("GoogleInc", result.get());
  }
@Test
  public void testDoOneSentenceMultipleMentionsEachTagged() {
    Properties props = new Properties();
    props.setProperty("wikidict", "x");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);
    WikidictAnnotator spy = spy(annotator);

    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel t1 = new CoreLabel();
    CoreLabel t2 = new CoreLabel();
    t1.set(CoreAnnotations.WikipediaEntityAnnotation.class, "O");
    t2.set(CoreAnnotations.WikipediaEntityAnnotation.class, "O");
    tokens.add(t1);
    tokens.add(t2);

//     CoreMap m1 = new TypesafeMap();
//     CoreMap m2 = new TypesafeMap();
//     m1.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(t1));
//     m2.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(t2));

//     doReturn(Optional.of("Entity1")).when(spy).link(m1);
//     doReturn(Optional.of("Entity2")).when(spy).link(m2);

    List<CoreMap> mentions = new ArrayList<>();
//     mentions.add(m1);
//     mentions.add(m2);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
    when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(mentions);

    Annotation ann = new Annotation("test");
    spy.doOneSentence(ann, sentence);

    assertEquals("Entity1", t1.get(CoreAnnotations.WikipediaEntityAnnotation.class));
    assertEquals("Entity2", t2.get(CoreAnnotations.WikipediaEntityAnnotation.class));
//     assertEquals("Entity1", m1.get(CoreAnnotations.WikipediaEntityAnnotation.class));
//     assertEquals("Entity2", m2.get(CoreAnnotations.WikipediaEntityAnnotation.class));
  }
@Test
  public void testLinkWithTimexHavingValuePRESENT_REFReturnsEmpty() {
    Properties props = new Properties();
    props.setProperty("wikidict", "dummy");

    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

    Timex timex = mock(Timex.class);
    when(timex.value()).thenReturn("PRESENT_REF");

//     CoreMap mention = new TypesafeMap();
//     mention.set(CoreAnnotations.TextAnnotation.class, "today");
//     mention.set(CoreAnnotations.OriginalTextAnnotation.class, "today");
//     mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");
//     mention.set(TimeAnnotations.TimexAnnotation.class, timex);

//     Optional<String> result = annotator.link(mention);
//     assertFalse(result.isPresent());
  }
@Test
  public void testLinkWithTimexHavingValueFUTURE_REFReturnsEmpty() {
    Properties props = new Properties();
    props.setProperty("wikidict", "dummy");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

    Timex timex = mock(Timex.class);
    when(timex.value()).thenReturn("FUTURE_REF");

//     CoreMap mention = new TypesafeMap();
//     mention.set(CoreAnnotations.TextAnnotation.class, "tomorrow");
//     mention.set(CoreAnnotations.OriginalTextAnnotation.class, "tomorrow");
//     mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");
//     mention.set(TimeAnnotations.TimexAnnotation.class, timex);

//     Optional<String> result = annotator.link(mention);
//     assertFalse(result.isPresent());
  }
@Test
  public void testLinkWithEmptyDictionaryReturnsEmpty() {
    Properties props = new Properties();
    props.setProperty("wikidict", "dummy");

    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);
    WikidictAnnotator spy = spy(annotator);

    Map<String, String> emptyDict = new HashMap<>();
//     doReturn(emptyDict).when(spy).dictionary;

//     CoreMap mention = new TypesafeMap();
//     mention.set(CoreAnnotations.TextAnnotation.class, "Amazon");
//     mention.set(CoreAnnotations.OriginalTextAnnotation.class, "Amazon");
//     mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");

//     Optional<String> result = spy.link(mention);
//     assertFalse(result.isPresent());
  }
@Test
  public void testLinkWithMalformedNumericNERValueReturnsAsIs() {
    Properties props = new Properties();
    props.setProperty("wikidict", "test");

    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

//     CoreMap mention = new TypesafeMap();
//     mention.set(CoreAnnotations.TextAnnotation.class, "98.6");
//     mention.set(CoreAnnotations.OriginalTextAnnotation.class, "98.6");
//     mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "TEMP"); 

//     Optional<String> result = annotator.link(mention);
//     assertTrue(result.isPresent());
//     assertEquals("98.6", result.get()); 
  }
@Test
  public void testLinkWithNullNumericValueOrdinalReturnsEmpty() {
    Properties props = new Properties();
    props.setProperty("wikidict", "dummy");

    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

//     CoreMap mention = new TypesafeMap();
//     mention.set(CoreAnnotations.TextAnnotation.class, "Fourth");
//     mention.set(CoreAnnotations.OriginalTextAnnotation.class, "Fourth");
//     mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORDINAL");

//     Optional<String> result = annotator.link(mention);
//     assertFalse(result.isPresent());
  }
@Test
  public void testLinkWithNumberPatternButNER_OFailsDictionary() {
    Properties props = new Properties();
    props.setProperty("wikidict", "fake");

    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

//     CoreMap mention = new TypesafeMap();
//     mention.set(CoreAnnotations.TextAnnotation.class, "007");
//     mention.set(CoreAnnotations.OriginalTextAnnotation.class, "007");
//     mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

//     Optional<String> result = annotator.link(mention);
//     assertTrue(result.isPresent());
//     assertEquals("007", result.get());
  }
@Test
  public void testDoOneSentenceMentionPresentButLinkReturnsEmpty() {
    Properties props = new Properties();
    props.setProperty("wikidict", "mock");

    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);
    WikidictAnnotator spy = spy(annotator);
    doReturn(Optional.empty()).when(spy).link(any(CoreMap.class));

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.WikipediaEntityAnnotation.class, "O");

    List<CoreLabel> tokens = Arrays.asList(token);

//     CoreMap mention = new TypesafeMap();
//     mention.set(CoreAnnotations.TokensAnnotation.class, tokens);

//     List<CoreMap> mentions = Arrays.asList(mention);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
//     when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(mentions);

    Annotation annotation = new Annotation("test");
    spy.doOneSentence(annotation, sentence);

    String result = token.get(CoreAnnotations.WikipediaEntityAnnotation.class);
    assertEquals("O", result);
//     assertNull(mention.get(CoreAnnotations.WikipediaEntityAnnotation.class));
  }
@Test
  public void testLinkWithNonMatchingNERReturnsEmpty() {
    Properties props = new Properties();
    props.setProperty("wikidict", "mock");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);
    WikidictAnnotator spy = spy(annotator);

    Map<String, String> dictionary = new HashMap<>();
    dictionary.put("Venus", "Venus_Planet");
//     doReturn(dictionary).when(spy).dictionary;

//     CoreMap mention = new TypesafeMap();
//     mention.set(CoreAnnotations.TextAnnotation.class, "Venus");
//     mention.set(CoreAnnotations.OriginalTextAnnotation.class, "Venus");
//     mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "UNKNOWN");

//     Optional<String> result = spy.link(mention);
//     assertFalse(result.isPresent());
  }
@Test
  public void testDictionaryMatchSkipsDueToNERBeingO() {
    Properties props = new Properties();
    props.setProperty("wikidict", "dummy");

    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);
    WikidictAnnotator spy = spy(annotator);

    Map<String, String> dictionary = new HashMap<>();
    dictionary.put("Nikon", "Nikon_Corp");
//     doReturn(dictionary).when(spy).dictionary;

//     CoreMap mention = new TypesafeMap();
//     mention.set(CoreAnnotations.TextAnnotation.class, "Nikon");
//     mention.set(CoreAnnotations.OriginalTextAnnotation.class, "Nikon");
//     mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

//     Optional<String> result = spy.link(mention);
//     assertFalse(result.isPresent());
  }
@Test
  public void testNormalizeTimexPreservesPartialValue() {
    String value = "2001-01";
    String result = WikidictAnnotator.normalizeTimex(value);
    assertEquals("2001-01", result);
  }
@Test
  public void testNormalizeTimexWithTrailingTimingTReturnsTrimmed() {
    String value = "2020-02-02T00:00:00";
    String result = WikidictAnnotator.normalizeTimex(value);
    assertEquals("2020-02-02", result);
  }
@Test
  public void testLinkWithDictionaryMatchButNullNERReturnsEmpty() {
    Properties props = new Properties();
    props.setProperty("wikidict", "fake");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);
    WikidictAnnotator spy = spy(annotator);

    Map<String, String> dict = new HashMap<>();
    dict.put("OpenAI", "OpenAI_Article");
//     doReturn(dict).when(spy).dictionary;

//     CoreMap mention = new TypesafeMap();
//     mention.set(CoreAnnotations.TextAnnotation.class, "OpenAI");
//     mention.set(CoreAnnotations.OriginalTextAnnotation.class, "OpenAI");
    

//     Optional<String> result = spy.link(mention);
//     assertFalse(result.isPresent());
  }
@Test
  public void testLinkOrdinalWithNonParsableNumericValueReturnsEmpty() {
    Properties props = new Properties();
    props.setProperty("wikidict", "path");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

//     CoreMap mention = new TypesafeMap();
//     mention.set(CoreAnnotations.TextAnnotation.class, "twelfth");
//     mention.set(CoreAnnotations.OriginalTextAnnotation.class, "twelfth");
//     mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORDINAL");
//     mention.set(CoreAnnotations.NumericValueAnnotation.class, null); 

//     Optional<String> result = annotator.link(mention);
//     assertFalse(result.isPresent());
  }
@Test
  public void testDoOneSentenceWithMentionButEmptyTokensList() {
    Properties props = new Properties();
    props.setProperty("wikidict", "dummy");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);
    WikidictAnnotator spy = spy(annotator);

//     CoreMap mention = new TypesafeMap();
//     mention.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
//     doReturn(Optional.of("Entity_X")).when(spy).link(mention);

//     List<CoreMap> mentions = Arrays.asList(mention);

    List<CoreLabel> sentenceTokens = Arrays.asList(new CoreLabel());
    sentenceTokens.get(0).set(CoreAnnotations.WikipediaEntityAnnotation.class, "O");

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(sentenceTokens);
//     when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(mentions);

    Annotation annotation = new Annotation("x");
    spy.doOneSentence(annotation, sentence);

    String tag = sentenceTokens.get(0).get(CoreAnnotations.WikipediaEntityAnnotation.class);
    assertEquals("O", tag);
  }
@Test
  public void testDoOneSentenceWithNullMentionsAndNullTokens() {
    Properties props = new Properties();
    props.setProperty("wikidict", "x");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(null);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(null);

    Annotation ann = new Annotation("dummy");
    annotator.doOneSentence(ann, sentence);
    
  }
@Test
  public void testLinkWithSurfaceFormThatHasTrailingWhitespace() {
    Properties props = new Properties();
    props.setProperty("wikidict", "mock");

    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);
    WikidictAnnotator spy = spy(annotator);

    Map<String, String> dict = new HashMap<>();
    dict.put("Google", "Google_Inc");
    dict.put("Google ", "Google_Bad"); 
//     doReturn(dict).when(spy).dictionary;

//     CoreMap mention = new TypesafeMap();
//     mention.set(CoreAnnotations.TextAnnotation.class, "Google ");
//     mention.set(CoreAnnotations.OriginalTextAnnotation.class, "Google ");
//     mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");

//     Optional<String> result = spy.link(mention);
//     assertTrue(result.isPresent());
//     assertEquals("Google_Bad", result.get());
  }
@Test
  public void testLinkWithEmptyTextAnnotationFallsBackAndReturnsEmpty() {
    Properties props = new Properties();
    props.setProperty("wikidict", "default");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

//     CoreMap mention = new TypesafeMap();
//     mention.set(CoreAnnotations.TextAnnotation.class, "");
//     mention.set(CoreAnnotations.OriginalTextAnnotation.class, "");
//     mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");

//     Optional<String> result = annotator.link(mention);
//     assertFalse(result.isPresent());
  }
@Test
  public void testLinkHandlesSpecialTimexTypeTIME() {
    Properties props = new Properties();
    props.setProperty("wikidict", "dummy");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

    Timex timex = mock(Timex.class);
    when(timex.value()).thenReturn("1979-09-21T12:20");

//     CoreMap mention = new TypesafeMap();
//     mention.set(CoreAnnotations.TextAnnotation.class, "noon");
//     mention.set(CoreAnnotations.OriginalTextAnnotation.class, "noon");
//     mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "TIME");
//     mention.set(TimeAnnotations.TimexAnnotation.class, timex);

//     Optional<String> result = annotator.link(mention);
//     assertTrue(result.isPresent());
//     assertEquals("1979-09-21", result.get());
  }
@Test
  public void testLinkHandlesSpecialTimexTypeSET() {
    Properties props = new Properties();
    props.setProperty("wikidict", "dummy");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

    Timex timex = mock(Timex.class);
    when(timex.value()).thenReturn("2022-12-12T00:00");

//     CoreMap mention = new TypesafeMap();
//     mention.set(CoreAnnotations.TextAnnotation.class, "Christmas");
//     mention.set(CoreAnnotations.OriginalTextAnnotation.class, "Christmas");
//     mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "SET");
//     mention.set(TimeAnnotations.TimexAnnotation.class, timex);

//     Optional<String> result = annotator.link(mention);
//     assertTrue(result.isPresent());
//     assertEquals("2022-12-12", result.get());
  }
@Test
  public void testLinkWithMinimalValidNumericSurfaceFormOnly() {
    Properties props = new Properties();
    props.setProperty("wikidict", "none");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

//     CoreMap mention = new TypesafeMap();
//     mention.set(CoreAnnotations.TextAnnotation.class, "17");
//     mention.set(CoreAnnotations.OriginalTextAnnotation.class, "17");
//     mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

//     Optional<String> result = annotator.link(mention);
//     assertTrue(result.isPresent());
//     assertEquals("17", result.get());
  }
@Test
  public void testNormalizeTimexHandlesExactPRESENTCase() {
    String input = "PRESENT";
    String output = WikidictAnnotator.normalizeTimex(input);
    assertEquals("PRESENT", output);
  }
@Test
  public void testLinkWithNullTimexObjectReturnsEmpty() {
    Properties props = new Properties();
    props.setProperty("wikidict", "source");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

//     CoreMap mention = new TypesafeMap();
//     mention.set(CoreAnnotations.TextAnnotation.class, "Tomorrow");
//     mention.set(CoreAnnotations.OriginalTextAnnotation.class, "Tomorrow");
//     mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");
//     mention.set(TimeAnnotations.TimexAnnotation.class, null); 

//     Optional<String> result = annotator.link(mention);
//     assertFalse(result.isPresent());
  }
@Test
  public void testLinkWithTimeNERAndValidTimexWithoutTReturnsFullDate() {
    Properties props = new Properties();
    props.setProperty("wikidict", "source");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

    Timex timex = mock(Timex.class);
    when(timex.value()).thenReturn("2023-07-19");

//     CoreMap mention = new TypesafeMap();
//     mention.set(CoreAnnotations.TextAnnotation.class, "7 PM");
//     mention.set(CoreAnnotations.OriginalTextAnnotation.class, "7 PM");
//     mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "TIME");
//     mention.set(TimeAnnotations.TimexAnnotation.class, timex);

//     Optional<String> result = annotator.link(mention);
//     assertTrue(result.isPresent());
//     assertEquals("2023-07-19", result.get());
  }
@Test
  public void testLinkWithTimexNullValueReturnsEmpty() {
    Properties props = new Properties();
    props.setProperty("wikidict", "source");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

    Timex timex = mock(Timex.class);
    when(timex.value()).thenReturn(null);

//     CoreMap mention = new TypesafeMap();
//     mention.set(CoreAnnotations.TextAnnotation.class, "Jan 5");
//     mention.set(CoreAnnotations.OriginalTextAnnotation.class, "Jan 5");
//     mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");
//     mention.set(TimeAnnotations.TimexAnnotation.class, timex);

//     Optional<String> result = annotator.link(mention);
//     assertFalse(result.isPresent());
  }
@Test
  public void testLinkWithEmptyTextReturnsEmptyResult() {
    Properties props = new Properties();
    props.setProperty("wikidict", "data");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

//     CoreMap mention = new TypesafeMap();
//     mention.set(CoreAnnotations.TextAnnotation.class, "");
//     mention.set(CoreAnnotations.OriginalTextAnnotation.class, "");
//     mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

//     Optional<String> result = annotator.link(mention);
//     assertFalse(result.isPresent());
  }
@Test
  public void testDoOneSentenceWithNullMentionsListSkipsGracefully() {
    Properties props = new Properties();
    props.setProperty("wikidict", "test");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

    CoreLabel t0 = new CoreLabel();
    t0.set(CoreAnnotations.WikipediaEntityAnnotation.class, "O");
    List<CoreLabel> tokens = Arrays.asList(t0);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(tokens);
    when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(null);

    Annotation annotation = new Annotation("sample text");
    annotator.doOneSentence(annotation, sentence);

    String out = t0.get(CoreAnnotations.WikipediaEntityAnnotation.class);
    assertEquals("O", out);
  }
@Test
  public void testDoOneSentenceWithMentionAndEmptyTokensUsesCanonicalNameOnlyOnMention() {
    Properties props = new Properties();
    props.setProperty("wikidict", "xyz");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);
    WikidictAnnotator spy = spy(annotator);

    List<CoreLabel> sentenceTokens = Arrays.asList(new CoreLabel());

//     CoreMap mention = new TypesafeMap();
//     mention.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());

//     List<CoreMap> mentions = Arrays.asList(mention);

    CoreMap sentence = mock(CoreMap.class);
    when(sentence.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(sentenceTokens);
//     when(sentence.get(CoreAnnotations.MentionsAnnotation.class)).thenReturn(mentions);

//     doReturn(Optional.of("LinkedEntity")).when(spy).link(mention);

    Annotation annotation = new Annotation("text");
    spy.doOneSentence(annotation, sentence);

//     String tag = mention.get(CoreAnnotations.WikipediaEntityAnnotation.class);
//     assertEquals("LinkedEntity", tag);
  }
@Test
  public void testLinkWithCaselessEnabledAndUpperCaseMentionMatchesLowercaseDictionaryKey() {
    Properties props = new Properties();
    props.setProperty("wikidict", "stub");
    props.setProperty("caseless", "true");

    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);
    WikidictAnnotator spy = spy(annotator);

    Map<String, String> dict = new HashMap<>();
    dict.put("tesla", "Tesla_Motors");
//     doReturn(dict).when(spy).dictionary;

//     CoreMap mention = new TypesafeMap();
//     mention.set(CoreAnnotations.TextAnnotation.class, "TESLA");
//     mention.set(CoreAnnotations.OriginalTextAnnotation.class, "TESLA");
//     mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");

//     Optional<String> result = spy.link(mention);
//     assertTrue(result.isPresent());
//     assertEquals("Tesla_Motors", result.get());
  }
@Test
  public void testConstructorHandlesFileReadFailureThrowsRuntimeException() {
    Properties props = new Properties();
    props.setProperty("wikidict", "non_existent_fake_path.tsv");

    RuntimeException thrown = null;
    try {
      WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);
    } catch (RuntimeException e) {
      thrown = e;
    }

    assertNotNull(thrown);
    assertTrue(thrown.getCause() instanceof java.io.FileNotFoundException);
  } 
}
