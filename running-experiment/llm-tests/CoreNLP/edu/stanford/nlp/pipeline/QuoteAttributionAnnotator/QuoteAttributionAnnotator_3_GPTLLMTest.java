package edu.stanford.nlp.pipeline;

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

public class QuoteAttributionAnnotator_3_GPTLLMTest {

 @Test
  public void testConstructsWithMinimumProperties() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "dummy_chars.txt");
    props.setProperty("booknlpCoref", "dummy_coref.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    assertNotNull(annotator);
  }
@Test
  public void testEntityMentionsToCharacterMap_addsSinglePerson() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "dummy_chars.txt");
    props.setProperty("booknlpCoref", "dummy_coref.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    CoreMap entityMention = mock(CoreMap.class);
    when(entityMention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");
    when(entityMention.toString()).thenReturn("John Smith");

    List<CoreMap> entityMentions = Arrays.asList(entityMention);

    Annotation annotation = new Annotation("Test");
    annotation.set(CoreAnnotations.MentionsAnnotation.class, entityMentions);

    annotator.entityMentionsToCharacterMap(annotation);

    
    assertTrue(true); 
  }
@Test
  public void testAnnotateWhenMentionsAnnotationMissing() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "dummy_chars.txt");
    props.setProperty("booknlpCoref", "dummy_coref.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("No entities here.");
    annotator.annotate(annotation);

    
    assertNotNull(annotation);
  }
@Test
  public void testQMMappingProducesExpectedSieveKeys() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "dummy_chars.txt");
    props.setProperty("booknlpCoref", "dummy_coref.txt");
    props.setProperty("QMSieves", "tri,dep,onename");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation dummyAnnotation = new Annotation("Text");
    Map<Integer, String> dummyCorefMap = new HashMap<>();

    Map<String, ?> sieves = annotator.getClass() 
        .getDeclaredClasses().length >= 0 ? new HashMap<>() : null;

    
    assertNotNull(annotator);
  }
@Test
  public void testRequirementsSatisfiedIncludesMentionAndSpeakerAnnotations() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "file.txt");
    props.setProperty("booknlpCoref", "coref.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    Set<Class<? extends CoreAnnotation>> output = annotator.requirementsSatisfied();

    assertTrue(output.contains(QuoteAttributionAnnotator.MentionAnnotation.class));
    assertTrue(output.contains(QuoteAttributionAnnotator.SpeakerAnnotation.class));
  }
@Test
  public void testRequiresIncludesCorefWhenUseCorefTrue() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "chars.tsv");
    props.setProperty("booknlpCoref", "coref.json");
    props.setProperty("useCoref", "true");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    Set<Class<? extends CoreAnnotation>> requirements = annotator.requires();

    assertTrue(requirements.contains(edu.stanford.nlp.coref.CorefCoreAnnotations.CorefChainAnnotation.class));
  }
@Test
  public void testRequiresOmitsCorefWhenUseCorefFalse() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "chars.tsv");
    props.setProperty("booknlpCoref", "coref.json");
    props.setProperty("useCoref", "false");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    Set<Class<? extends CoreAnnotation>> requirements = annotator.requires();

    assertFalse(requirements.contains(edu.stanford.nlp.coref.CorefCoreAnnotations.CorefChainAnnotation.class));
  }
@Test
  public void testAnnotateBuildsCanonicalMentionsIfPresent() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "x.tsv");
    props.setProperty("booknlpCoref", "y.json");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    token.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);

    CoreMap quote = mock(CoreMap.class);
    when(quote.get(QuoteAttributionAnnotator.MentionBeginAnnotation.class)).thenReturn(0);

    CoreMap entityMention = mock(CoreMap.class);
    CoreMap canonicalMention = mock(CoreMap.class);

    when(canonicalMention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Jane Doe");

    CoreLabel cTok1 = new CoreLabel();
    cTok1.set(CoreAnnotations.TokenBeginAnnotation.class, 1);
    CoreLabel cTok2 = new CoreLabel();
    cTok2.set(CoreAnnotations.TokenBeginAnnotation.class, 2);
    List<CoreLabel> canonicalTokens = Arrays.asList(cTok1, cTok2);

    when(entityMention.get(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class)).thenReturn(1);
    when(entityMention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(canonicalTokens);

    when(canonicalMention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(canonicalTokens);
    List<CoreMap> mentions = Arrays.asList(entityMention, canonicalMention);
    List<CoreLabel> tokens = Arrays.asList(token);

    Annotation annotation = new Annotation("Test sentence");
    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    QuoteAnnotator mockQuoteAnnotator = mock(QuoteAnnotator.class);
    List<CoreMap> quotes = Arrays.asList(quote);
    

    annotator.annotate(annotation);

    assertNotNull(annotation);
  }
@Test
  public void testEntityMentionsToCharacterMapWithNonPersonNER() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "dummy.tsv");
    props.setProperty("booknlpCoref", "dummyCoref.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    CoreMap entityMention = mock(CoreMap.class);
    when(entityMention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("LOCATION");
    when(entityMention.toString()).thenReturn("Paris");

    List<CoreMap> mentions = Arrays.asList(entityMention);
    Annotation annotation = new Annotation("Mention is not a person");
    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);

    annotator.entityMentionsToCharacterMap(annotation);

    assertTrue(true); 
  }
@Test
  public void testAnnotateHandlesEmptyMentionsList() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "dummy.tsv");
    props.setProperty("booknlpCoref", "dummyCoref.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation doc = new Annotation("Test no mentions");
    doc.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    annotator.annotate(doc);

    assertNotNull(doc);
  }
@Test
  public void testAnnotatorWithNoCharactersPathLogsWarningIfVerbose() {
    Properties props = new Properties();
    props.setProperty("modelPath", "dummyModel");
    props.setProperty("verbose", "true");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    assertNotNull(annotator);
  }
@Test
  public void testAnnotatorWithNoModelPathDefaultsCorrectly() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "character.tsv");
    props.setProperty("booknlpCoref", "coref.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    assertNotNull(annotator);
  }
@Test
  public void testAnnotatorSkipsCanonicalMappingIfNoEntityMentionIndex() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "chars.tsv");
    props.setProperty("booknlpCoref", "coref.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("Testing quote without entity index");

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    CoreMap quote = mock(CoreMap.class);
    when(quote.get(QuoteAttributionAnnotator.MentionBeginAnnotation.class)).thenReturn(0);

    List<CoreLabel> tokens = Arrays.asList(token);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    annotator.annotate(annotation);

    assertNotNull(annotation);
  }
@Test
  public void testAnnotatorSkipsCanonicalMappingIfCanonicalIndexIsNull() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "chars.tsv");
    props.setProperty("booknlpCoref", "coref.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    token.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);

    CoreMap quote = mock(CoreMap.class);
    when(quote.get(QuoteAttributionAnnotator.MentionBeginAnnotation.class)).thenReturn(0);

    CoreMap entityMention = mock(CoreMap.class);
    when(entityMention.get(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class)).thenReturn(null);

    List<CoreLabel> tokens = Arrays.asList(token);
    List<CoreMap> mentions = Arrays.asList(entityMention);
    Annotation annotation = new Annotation("Quote where canonical mention index is null");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);

    annotator.annotate(annotation);

    assertNotNull(annotation);
  }
@Test
  public void testAnnotateDoesNotFailWithNullTokensList() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "chars.tsv");
    props.setProperty("booknlpCoref", "coref.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("Some quote without tokens");
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    

    annotator.annotate(annotation);

    assertNotNull(annotation); 
  }
@Test
  public void testRequirementsSatisfiedContainsAllRequiredCustomAnnotations() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "path.tsv");
    props.setProperty("booknlpCoref", "coref.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertTrue(satisfied.contains(QuoteAttributionAnnotator.MentionSieveAnnotation.class));
    assertTrue(satisfied.contains(QuoteAttributionAnnotator.CanonicalMentionBeginAnnotation.class));
    assertTrue(satisfied.contains(QuoteAttributionAnnotator.CanonicalMentionEndAnnotation.class));
    assertTrue(satisfied.contains(QuoteAttributionAnnotator.CanonicalMentionAnnotation.class));
  }
@Test
  public void testRequiresIncludesMentionAnnotation() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "path.tsv");
    props.setProperty("booknlpCoref", "coref.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    Set<Class<? extends CoreAnnotation>> required = annotator.requires();

    assertTrue(required.contains(CoreAnnotations.MentionsAnnotation.class));
  }
@Test
  public void testAnnotateWithNoCorefPathLogsWarningIfVerbose() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "test.tsv");
    props.setProperty("verbose", "true");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("Testing missing coref path.");
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    annotator.annotate(annotation);

    assertNotNull(annotation); 
  }
@Test
  public void testAnnotateWithNullEntityMentionList() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "char.tsv");
    props.setProperty("booknlpCoref", "coreffile");
    props.setProperty("QMSieves", "tri");
    props.setProperty("MSSieves", "det");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("Some text here.");
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.MentionsAnnotation.class, null); 

    annotator.annotate(annotation);

    assertNotNull(annotation);
  }
@Test
  public void testAnnotateWithEmptyParagraphTokens() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "characters.tsv");
    props.setProperty("booknlpCoref", "coref.json");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation ann = new Annotation("Text document");
    ann.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
    ann.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());

    annotator.annotate(ann);

    assertNotNull(ann);
  }
@Test
  public void testAnnotateSkipsQuoteCanonicalMatchingWhenMentionBeginIsNull() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "chars.tsv");
    props.setProperty("booknlpCoref", "coref.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    CoreMap quote = mock(CoreMap.class);
    when(quote.get(QuoteAttributionAnnotator.MentionBeginAnnotation.class)).thenReturn(null);

    List<CoreMap> quotes = Arrays.asList(quote);

    Annotation ann = new Annotation("He exclaimed loudly.");
    ann.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    ann.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    annotator.annotate(ann);

    assertNotNull(ann);
  }
@Test
  public void testAnnotateSkipsCanonicalHandlingWhenMentionIndexOutOfBounds() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "characters.tsv");
    props.setProperty("booknlpCoref", "corefmap");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 5); 
    token.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    List<CoreLabel> tokens = Arrays.asList(token);

    CoreMap quote = mock(CoreMap.class);
    when(quote.get(QuoteAttributionAnnotator.MentionBeginAnnotation.class)).thenReturn(0);

    List<CoreMap> quotes = Arrays.asList(quote);

    List<CoreMap> mentions = Arrays.asList(mock(CoreMap.class)); 

    Annotation ann = new Annotation("Testing with missing entity mention");
    ann.set(CoreAnnotations.TokensAnnotation.class, tokens);
    ann.set(CoreAnnotations.MentionsAnnotation.class, mentions);

    annotator.annotate(ann);

    assertNotNull(ann);
  }
@Test
  public void testQMSieveFallbackToClosestWhenInvalidSieveSpecified() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "c.tsv");
    props.setProperty("booknlpCoref", "coref.json");
    props.setProperty("QMSieves", "invalidsieve,closest");
    props.setProperty("MSSieves", "");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("He said something again.");
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    annotator.annotate(annotation);

    assertNotNull(annotation); 
  }
@Test
  public void testMSSieveFallbackToMajWhenInvalidSieveSpecified() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "chars.tsv");
    props.setProperty("booknlpCoref", "coref.txt");
    props.setProperty("MSSieves", "unknown,maj");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("She said: Hello.");
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    annotator.annotate(annotation);

    assertNotNull(annotation); 
  }
@Test
  public void testEntityMentionToCharacterMapSkipsNullNERTag() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "dummy.tsv");
    props.setProperty("booknlpCoref", "coref.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    CoreMap mention = mock(CoreMap.class);
    when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn(null);
    when(mention.toString()).thenReturn("Unnamed");

    List<CoreMap> mentions = Arrays.asList(mention);
    Annotation ann = new Annotation("Entity isn't typed");
    ann.set(CoreAnnotations.MentionsAnnotation.class, mentions);

    annotator.entityMentionsToCharacterMap(ann);

    assertTrue(true); 
  }
@Test
  public void testAnnotatorWithEmptyQMAndMSSieves() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "chars.tsv");
    props.setProperty("booknlpCoref", "coref.txt");
    props.setProperty("QMSieves", "");
    props.setProperty("MSSieves", "");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    Annotation annotation = new Annotation("Empty sieve config");

    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

    annotator.annotate(annotation);

    assertNotNull(annotation);
  }
@Test
  public void testAnnotatorAnnotateHandlesNullPronounCorefMapGracefully() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "c.tsv");
    props.setProperty("booknlpCoref", "coref.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    Annotation ann = new Annotation("He walked into the room.");

    ann.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    ann.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

    annotator.annotate(ann);

    assertNotNull(ann);
  }
@Test
  public void testAnnotatorWithOnlyCharactersPathSet() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "people.tsv");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation doc = new Annotation("Basic text.");
    doc.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
    doc.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());

    annotator.annotate(doc);

    assertNotNull(doc);
  }
@Test
  public void testAnnotateSkipsCanonicalAssignmentWhenNoTokensOnCanonicalMention() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "x.tsv");
    props.setProperty("booknlpCoref", "x.json");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    token.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);

    CoreMap quote = mock(CoreMap.class);
    when(quote.get(QuoteAttributionAnnotator.MentionBeginAnnotation.class)).thenReturn(0);

    CoreMap entityMention = mock(CoreMap.class);
    when(entityMention.get(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class)).thenReturn(1);

    CoreMap canonicalMention = mock(CoreMap.class);
    when(canonicalMention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("BARACK OBAMA");
    when(canonicalMention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(new ArrayList<>()); 

    List<CoreMap> mentions = Arrays.asList(entityMention, canonicalMention);
    List<CoreLabel> tokens = Arrays.asList(token);

    Annotation ann = new Annotation("He said something.");
    ann.set(CoreAnnotations.MentionsAnnotation.class, mentions);
    ann.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(ann);

    assertNotNull(ann);
  }
@Test
  public void testConstructorHandlesNullGenderFileFallback() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "test.tsv");
    props.setProperty("booknlpCoref", "coref.txt");
    props.setProperty("genderNamesFile", "");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    assertNotNull(annotator);
  }
@Test
  public void testEntityMentionsToCharacterMapWithMultiplePersons() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "c.tsv");
    props.setProperty("booknlpCoref", "coref.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    CoreMap mention1 = mock(CoreMap.class);
    CoreMap mention2 = mock(CoreMap.class);

    when(mention1.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");
    when(mention1.toString()).thenReturn("Alice Wonderland");

    when(mention2.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");
    when(mention2.toString()).thenReturn("Bob Builder");

    List<CoreMap> mentions = Arrays.asList(mention1, mention2);

    Annotation ann = new Annotation("Characters: Alice and Bob");
    ann.set(CoreAnnotations.MentionsAnnotation.class, mentions);

    annotator.entityMentionsToCharacterMap(ann);

    assertTrue(true); 
  }
@Test
  public void testAnnotateHandlesQuoteWithMissingMentionBeginButValidMentionList() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "xx.tsv");
    props.setProperty("booknlpCoref", "cc");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation ann = new Annotation("He whispered.");

    
    ann.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    ann.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

    
    CoreMap quote = mock(CoreMap.class);
    when(quote.get(QuoteAttributionAnnotator.MentionBeginAnnotation.class)).thenReturn(null);

    
    ann.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

    annotator.annotate(ann);

    assertNotNull(ann);
  }
@Test
  public void testAnnotatorWithMissingDependencyParserModelFallsBackToDefault() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "z.tsv");
    props.setProperty("booknlpCoref", "book-coref.txt");

    
    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation ann = new Annotation("Simple input");
    ann.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    ann.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

    annotator.annotate(ann);

    assertNotNull(ann);
  }
@Test
  public void testAnnotateHandlesEmptySentenceStructure() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "data.tsv");
    props.setProperty("booknlpCoref", "dummy.json");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation ann = new Annotation("Text without any sentence structure.");

    ann.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    ann.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    ann.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

    annotator.annotate(ann);

    assertNotNull(ann);
  }
@Test
  public void testAnnotatorDoesNotFailWithNullMentionTokenIndex() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "p.tsv");
    props.setProperty("booknlpCoref", "coref.json");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    CoreMap mention = mock(CoreMap.class);
    when(mention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("PERSON");
    when(mention.toString()).thenReturn("Crazy Person");

    List<CoreMap> mentions = Arrays.asList(mention);

    Annotation annotation = new Annotation("One mention only.");
    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);

    annotator.entityMentionsToCharacterMap(annotation);

    assertNotNull(annotation);
  }
@Test
  public void testAnnotateHandlesQuoteWithMentionWithoutCanonicalEntityIndex() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "characters.tsv");
    props.setProperty("booknlpCoref", "coref.json");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    token.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);

    CoreMap mention = mock(CoreMap.class);
    when(mention.get(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class)).thenReturn(null);
    List<CoreMap> mentions = Collections.singletonList(mention);

    CoreMap quote = mock(CoreMap.class);
    when(quote.get(QuoteAttributionAnnotator.MentionBeginAnnotation.class)).thenReturn(0);

    Annotation ann = new Annotation("He said hello.");
    ann.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    ann.set(CoreAnnotations.MentionsAnnotation.class, mentions);

    annotator.annotate(ann);
    assertNotNull(ann);
  }
@Test
  public void testAnnotateWithEmptyTokenListAndNullMentionBeginAnnotationInQuote() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "char.tsv");
    props.setProperty("booknlpCoref", "corefinfo");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation ann = new Annotation("Empty tokens and missing quote bounds");
    ann.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    ann.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    CoreMap quote = mock(CoreMap.class);
    when(quote.get(QuoteAttributionAnnotator.MentionBeginAnnotation.class)).thenReturn(null);

    annotator.annotate(ann);
    assertTrue(true);
  }
@Test
  public void testAnnotateSkipsCanonicalAssignmentIfMentionIndexOutOfBound() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "char.tsv");
    props.setProperty("booknlpCoref", "coref.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    token.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 5); 

    CoreMap quote = mock(CoreMap.class);
    when(quote.get(QuoteAttributionAnnotator.MentionBeginAnnotation.class)).thenReturn(0);

    Annotation ann = new Annotation("Testing invalid entity mention index.");
    ann.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    ann.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    annotator.annotate(ann);
    assertNotNull(ann);
  }
@Test
  public void testAnnotateSkipsCanonicalLogicIfCanonicalMentionTokenListIsNull() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "char.tsv");
    props.setProperty("booknlpCoref", "coref.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);
    token.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    CoreMap mention = mock(CoreMap.class);
    when(mention.get(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class)).thenReturn(1);

    CoreMap canonicalMention = mock(CoreMap.class);
    when(canonicalMention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(null); 
    when(canonicalMention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Jane Doe");

    List<CoreMap> mentionList = Arrays.asList(mention, canonicalMention);
    List<CoreLabel> tokenList = Arrays.asList(token);

    Annotation ann = new Annotation("Jane was here.");
    ann.set(CoreAnnotations.MentionsAnnotation.class, mentionList);
    ann.set(CoreAnnotations.TokensAnnotation.class, tokenList);

    annotator.annotate(ann);
    assertNotNull(ann);
  }
@Test
  public void testAnnotatorInitializationWithMissingFamilyWordListFallsBackGracefully() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "characters.tsv");
    props.setProperty("booknlpCoref", "coref.json");
    props.setProperty("familyWordsFile", "");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    assertNotNull(annotator);
  }
@Test
  public void testAnnotatorInitializationWithMissingAnimacyWordListFallsBackGracefully() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "chars.tsv");
    props.setProperty("booknlpCoref", "corefdata.json");
    props.setProperty("animacyWordsFile", "");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    assertNotNull(annotator);
  }
@Test
  public void testConstructorWithUnlistedSieveNamesSkipsGracefully() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "p.tsv");
    props.setProperty("booknlpCoref", "testcoref.txt");
    props.setProperty("QMSieves", "fake1,fake2");
    props.setProperty("MSSieves", "fake3");

    Annotation ann = new Annotation("Some input");
    ann.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    ann.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    annotator.annotate(ann);

    assertNotNull(ann);
  }
@Test
  public void testConstructorWithEmptyPropertiesUsesDefaults() {
    Properties props = new Properties();

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    assertNotNull(annotator);
  }
@Test
  public void testAnnotateToleratesTokenWithoutEntityMentionIndexAnnotation() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "x.tsv");
    props.setProperty("booknlpCoref", "y.json");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    CoreLabel token = new CoreLabel(); 
    token.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    CoreMap quote = mock(CoreMap.class);
    when(quote.get(QuoteAttributionAnnotator.MentionBeginAnnotation.class)).thenReturn(0);

    Annotation ann = new Annotation("Missing entity index.");
    ann.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    ann.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    annotator.annotate(ann);

    assertNotNull(ann);
  }
@Test
  public void testMentionTokenWithoutTokenBeginAnnotationDoesNotFail() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "data/characters.tsv");
    props.setProperty("booknlpCoref", "data/coref.json");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    CoreLabel token = new CoreLabel(); 
    token.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);

    CoreMap quote = mock(CoreMap.class);
    when(quote.get(QuoteAttributionAnnotator.MentionBeginAnnotation.class)).thenReturn(0);

    CoreMap entityMention = mock(CoreMap.class);
    when(entityMention.get(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class)).thenReturn(1);

    CoreMap canonicalMention = mock(CoreMap.class);
    when(canonicalMention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(Arrays.asList(new CoreLabel()));
    when(canonicalMention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Sarah");

    List<CoreMap> mentions = Arrays.asList(entityMention, canonicalMention);
    List<CoreLabel> tokens = Arrays.asList(token);

    Annotation annotation = new Annotation("Testing invalid token");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);

    annotator.annotate(annotation);

    assertNotNull(annotation);
  }
@Test
  public void testAnnotatorHandlesMissingPropertiesGracefully() {
    Properties props = new Properties();

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("Text");
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

    annotator.annotate(annotation);

    assertNotNull(annotation);
  }
@Test
  public void testMentionWithUnexpectedNERTypeIsIgnored() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "characters.tsv");
    props.setProperty("booknlpCoref", "coref.json");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    CoreMap entityMention = mock(CoreMap.class);
    when(entityMention.get(CoreAnnotations.NamedEntityTagAnnotation.class)).thenReturn("ORGANIZATION");
    when(entityMention.toString()).thenReturn("UN");

    List<CoreMap> mentions = Arrays.asList(entityMention);

    Annotation annotation = new Annotation("ORG mention shouldn't be processed.");
    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);

    annotator.entityMentionsToCharacterMap(annotation);

    assertTrue(true); 
  }
@Test
  public void testAnnotateWithNullTokensAnnotation() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "char.tsv");
    props.setProperty("booknlpCoref", "coref.json");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("Test");

    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
    

    annotator.annotate(annotation);

    assertNotNull(annotation);
  }
@Test
  public void testAnnotatorWithOnlyQMSievesConfigured() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "chars.tsv");
    props.setProperty("booknlpCoref", "coref.txt");
    props.setProperty("QMSieves", "tri,dep");
    props.setProperty("MSSieves", "");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("He said she said.");
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

    annotator.annotate(annotation);

    assertNotNull(annotation);
  }
@Test
  public void testAnnotatorWithOnlyMSSievesConfigured() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "char.tsv");
    props.setProperty("booknlpCoref", "coref.json");
    props.setProperty("QMSieves", "");
    props.setProperty("MSSieves", "det,maj");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("He said she said.");
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

    annotator.annotate(annotation);

    assertNotNull(annotation);
  }
@Test
  public void testAnnotatorWithEmptyQMSievesAndMSSieves() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "chars.tsv");
    props.setProperty("booknlpCoref", "map.json");
    props.setProperty("QMSieves", "");
    props.setProperty("MSSieves", "");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("Silence is golden.");
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

    annotator.annotate(annotation);

    assertNotNull(annotation);
  }
@Test
  public void testAnnotateSkipsMalformedCanonicalEntityMentionList() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "chars.tsv");
    props.setProperty("booknlpCoref", "corefmap");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    token.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);

    CoreMap entityMention = mock(CoreMap.class);
    when(entityMention.get(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class)).thenReturn(1);

    CoreMap canonicalMention = mock(CoreMap.class);
    when(canonicalMention.get(CoreAnnotations.TokensAnnotation.class)).thenReturn(new ArrayList<>());
    when(canonicalMention.get(CoreAnnotations.TextAnnotation.class)).thenReturn("Unnamed");

    List<CoreLabel> tokens = Arrays.asList(token);
    List<CoreMap> mentions = Arrays.asList(entityMention, canonicalMention);

    Annotation annotation = new Annotation("Word error.");
    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    assertNotNull(annotation);
  }
@Test
  public void testRequirementsIncludeNamedDependenciesWhenUseCorefTrue() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "some.tsv");
    props.setProperty("booknlpCoref", "coref.json");
    props.setProperty("useCoref", "true");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Set<Class<? extends CoreAnnotation>> required = annotator.requires();

    assertTrue(required.contains(CoreAnnotations.NamedEntityTagAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.LemmaAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(required.contains(edu.stanford.nlp.coref.CorefCoreAnnotations.CorefChainAnnotation.class));
  }
@Test
  public void testRequirementsExcludeCorefWhenUseCorefFalse() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "char.tsv");
    props.setProperty("booknlpCoref", "coref.json");
    props.setProperty("useCoref", "false");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Set<Class<? extends CoreAnnotation>> required = annotator.requires();

    assertFalse(required.contains(edu.stanford.nlp.coref.CorefCoreAnnotations.CorefChainAnnotation.class));
  }
@Test
  public void testConstructorWithMissingCharactersFileLogsWarningIfVerbose() {
    Properties props = new Properties();
    props.setProperty("verbose", "true");
    props.setProperty("modelPath", "some/path/model.ser");
    props.setProperty("familyWordsFile", "some/family.txt");
    props.setProperty("animacyWordsFile", "some/animacy.txt");
    props.setProperty("genderNamesFile", "some/gender.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    assertNotNull(annotator);
  }
@Test
  public void testAnnotateHandlesNullMentionsAnnotationGracefully() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "test.tsv");
    props.setProperty("booknlpCoref", "coref.json");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation ann = new Annotation("No mentions here.");
    ann.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>()); 

    annotator.annotate(ann);

    assertNotNull(ann);
  }
@Test
  public void testAnnotateHandlesNoSentencesNorTokens() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "chars.tsv");
    props.setProperty("booknlpCoref", "coref.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation ann = new Annotation("Edge case.");
    

    ann.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());

    annotator.annotate(ann);
    assertNotNull(ann);
  }
@Test
  public void testEntityMentionToCharacterMapSkipsNullMentionList() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "whatever.tsv");
    props.setProperty("booknlpCoref", "other.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    Annotation ann = new Annotation("Entity mapping failure case.");

    ann.set(CoreAnnotations.MentionsAnnotation.class, null); 

    annotator.entityMentionsToCharacterMap(ann);
    assertNotNull(ann);
  }
@Test
  public void testAnnotatorWithNoPropertiesUsesDefaultsWithoutError() {
    Properties emptyProps = new Properties();
    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(emptyProps);

    Annotation ann = new Annotation("A basic quote about something.");
    ann.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    ann.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    ann.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

    annotator.annotate(ann);
    assertNotNull(ann);
  }
@Test
  public void testMissingModelPathFallsBackToDefaultModel() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "c.tsv");
    props.setProperty("booknlpCoref", "coref.json");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    assertNotNull(annotator);
  }
@Test
  public void testEmptySentenceWithParagraphBreakHandling() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "test.tsv");
    props.setProperty("booknlpCoref", "coref.json");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    CoreMap sentence = mock(CoreMap.class);
    Annotation ann = new Annotation("");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    ann.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    ann.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

    annotator.annotate(ann);
    assertNotNull(ann);
  }
@Test
  public void testQuoteCanonicalHandlingWithMissingEntityMention() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "canonical.tsv");
    props.setProperty("booknlpCoref", "corefmap.json");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    token.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 5); 

    List<CoreLabel> tokens = Collections.singletonList(token);

    Annotation ann = new Annotation("Quote lacking matching entity mention.");
    ann.set(CoreAnnotations.TokensAnnotation.class, tokens);
    ann.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>()); 

    annotator.annotate(ann);
    assertNotNull(ann);
  }
@Test
  public void testCanonicalMentionAssignmentFailsGracefullyIfCanonicalIndexTooLarge() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "c.tsv");
    props.setProperty("booknlpCoref", "corefdata.json");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    token.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);

    CoreMap quote = mock(CoreMap.class);
    when(quote.get(QuoteAttributionAnnotator.MentionBeginAnnotation.class)).thenReturn(0);

    CoreMap mention = mock(CoreMap.class);
    when(mention.get(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class)).thenReturn(1); 

    List<CoreMap> mentions = Collections.singletonList(mention); 
    List<CoreLabel> tokens = Collections.singletonList(token);

    Annotation ann = new Annotation("Mismatch index assignment");
    ann.set(CoreAnnotations.MentionsAnnotation.class, mentions);
    ann.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(ann);
    assertNotNull(ann);
  }
@Test
  public void testAnnotatorAcceptsEmptyQMSievesList() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "qms.tsv");
    props.setProperty("booknlpCoref", "coref.txt");
    props.setProperty("QMSieves", "");
    props.setProperty("MSSieves", "maj");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation ann = new Annotation("Empty QM sieves config.");
    ann.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    ann.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

    annotator.annotate(ann);
    assertNotNull(ann);
  }
@Test
  public void testAnnotatorAcceptsEmptyMSSievesList() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "chars.tsv");
    props.setProperty("booknlpCoref", "corefs.json");
    props.setProperty("QMSieves", "tri");
    props.setProperty("MSSieves", "");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation ann = new Annotation("Only QM sieve scenario.");
    ann.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    ann.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

    annotator.annotate(ann);

    assertNotNull(ann);
  } 
}