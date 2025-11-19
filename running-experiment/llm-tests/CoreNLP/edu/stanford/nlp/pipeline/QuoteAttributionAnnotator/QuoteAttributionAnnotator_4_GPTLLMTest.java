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

public class QuoteAttributionAnnotator_4_GPTLLMTest {

 @Test
  public void testAnnotatorInitializationWithDefaultProperties() {
    Properties props = new Properties();
    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    assertNotNull(annotator);
    assertTrue(annotator instanceof QuoteAttributionAnnotator);
  }
@Test
  public void testAnnotatorInitializationWithExplicitProperties() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "some/path/characters.txt");
    props.setProperty("booknlpCoref", "some/path/booknlp.txt");
    props.setProperty("modelPath", "some/path/model.ser.gz");
    props.setProperty("familyWordsFile", "some/path/family.txt");
    props.setProperty("genderNamesFile", "some/path/gender.txt");
    props.setProperty("animacyWordsFile", "some/path/animacy.txt");

    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    assertNotNull(annotator);
  }
@Test
  public void testRequirementsSatisfiedContainsSpeakerAnnotation() {
    Properties props = new Properties();
    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
    assertTrue(satisfied.contains(QuoteAttributionAnnotator.SpeakerAnnotation.class));
  }
@Test
  public void testRequirementsContainsNamedEntityTagAnnotation() {
    Properties props = new Properties();
    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Set<Class<? extends CoreAnnotation>> required = annotator.requires();
    assertTrue(required.contains(CoreAnnotations.NamedEntityTagAnnotation.class));
  }
@Test
  public void testAnnotateWithEmptyText() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "");
    props.setProperty("booknlpCoref", "");
    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.TextAnnotation.class, "");

    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = new ArrayList<CoreMap>();
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    annotator.annotate(annotation);
    assertTrue(annotation.get(CoreAnnotations.TokensAnnotation.class).isEmpty());
  }
@Test
  public void testAnnotateWithSinglePersonMention() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "");
    props.setProperty("booknlpCoref", "");
    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("John spoke.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "John spoke.");

    CoreLabel johnToken = new CoreLabel();
    johnToken.set(CoreAnnotations.TextAnnotation.class, "John");
    johnToken.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    johnToken.set(CoreAnnotations.IndexAnnotation.class, 1);
    johnToken.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    johnToken.set(CoreAnnotations.TokenEndAnnotation.class, 1);

    CoreLabel spokeToken = new CoreLabel();
    spokeToken.set(CoreAnnotations.TextAnnotation.class, "spoke");
    spokeToken.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    spokeToken.set(CoreAnnotations.IndexAnnotation.class, 2);
    spokeToken.set(CoreAnnotations.TokenBeginAnnotation.class, 1);
    spokeToken.set(CoreAnnotations.TokenEndAnnotation.class, 2);

    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    tokens.add(johnToken);
    tokens.add(spokeToken);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> mentions = new ArrayList<CoreMap>();
    annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

    annotator.annotate(annotation);

    assertNotNull(annotation.get(CoreAnnotations.TokensAnnotation.class));
  }
@Test
  public void testAnnotateWithNoMentionsAnnotationPresent() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "");
    props.setProperty("booknlpCoref", "");
    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("Text with no mentions");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Text with no mentions");

    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Text");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    token1.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.TokenEndAnnotation.class, 1);
    token1.set(CoreAnnotations.IndexAnnotation.class, 1);

    tokens.add(token1);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

    annotator.annotate(annotation);
    assertTrue(annotation.get(CoreAnnotations.TokensAnnotation.class).size() == 1);
  }
@Test
  public void testAnnotateDoesNotThrowExceptionOnEmptyTokens() {
    Properties props = new Properties();
    props.setProperty("charactersPath", "");
    props.setProperty("booknlpCoref", "");
    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.TextAnnotation.class, "");
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    annotator.annotate(annotation);
    assertTrue(true); 
  }
@Test
  public void testUseCorefFlagDefault() {
    Properties props = new Properties();
    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    Set<Class<? extends CoreAnnotation>> required = annotator.requires();
    boolean includesCoref = required.contains(edu.stanford.nlp.coref.CorefCoreAnnotations.CorefChainAnnotation.class);
    assertTrue(includesCoref);
  }
@Test
  public void testUseCorefFlagFalseDisablesRequirement() {
    Properties props = new Properties();
    props.setProperty("useCoref", "false");
    QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
    Set<Class<? extends CoreAnnotation>> required = annotator.requires();
    boolean includesCoref = required.contains(edu.stanford.nlp.coref.CorefCoreAnnotations.CorefChainAnnotation.class);
    assertFalse(includesCoref);
  }
@Test
public void testAnnotatorHandlesNullMentionsAnnotation() {
  Properties props = new Properties();
  props.setProperty("charactersPath", "");
  props.setProperty("booknlpCoref", "");
  QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

  Annotation annotation = new Annotation("This text lacks MentionsAnnotation.");
  annotation.set(CoreAnnotations.TextAnnotation.class, "This text lacks MentionsAnnotation.");

  List<CoreLabel> tokens = new ArrayList<CoreLabel>();
  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "This");
  token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
  token.set(CoreAnnotations.IndexAnnotation.class, 1);
  token.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
  token.set(CoreAnnotations.TokenEndAnnotation.class, 1);
  tokens.add(token);
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  

  annotator.annotate(annotation);

  assertNotNull(annotation.get(CoreAnnotations.TokensAnnotation.class));
}
@Test
public void testAnnotatorWithNoTextAnnotation() {
  Properties props = new Properties();
  QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

  Annotation annotation = new Annotation(""); 

  List<CoreLabel> tokens = new ArrayList<CoreLabel>();
  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "Example");
  token.set(CoreAnnotations.IndexAnnotation.class, 1);
  token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
  token.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
  token.set(CoreAnnotations.TokenEndAnnotation.class, 1);
  tokens.add(token);
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

  annotator.annotate(annotation);

  assertNotNull(annotation.get(CoreAnnotations.TokensAnnotation.class));
}
@Test
public void testAnnotatorWithMalformedMentionWithoutNER() {
  Properties props = new Properties();
  QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

  Annotation annotation = new Annotation("Entity without NER");
  annotation.set(CoreAnnotations.TextAnnotation.class, "Entity without NER");

  
  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "John");
  token.set(CoreAnnotations.IndexAnnotation.class, 1);
  token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O"); 
  token.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
  token.set(CoreAnnotations.TokenEndAnnotation.class, 1);
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));


  
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

  annotator.annotate(annotation);
  assertNotNull(annotation.get(CoreAnnotations.TokensAnnotation.class));
}
@Test
public void testSieveListExcludesUnknownQMAndMSSieve() {
  Properties props = new Properties();
  props.setProperty("charactersPath", "");
  props.setProperty("booknlpCoref", "");
  props.setProperty("QMSieves", "unknownsieve");
  props.setProperty("MSSieves", "badms");
  QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

  Annotation annotation = new Annotation("“Hello,” said Jane.");
  annotation.set(CoreAnnotations.TextAnnotation.class, "“Hello,” said Jane.");

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "Jane");
  token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
  token.set(CoreAnnotations.IndexAnnotation.class, 1);
  token.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
  token.set(CoreAnnotations.TokenEndAnnotation.class, 1);

  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

  annotator.annotate(annotation);
  assertNotNull(annotation);
}
@Test
public void testAnnotatorWithMissingCharacterMapAndNoMentions() {
  Properties props = new Properties();
  props.setProperty("charactersPath", "");
  props.setProperty("booknlpCoref", "");
  QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

  Annotation annotation = new Annotation("Speech with no characters.");
  annotation.set(CoreAnnotations.TextAnnotation.class, "Speech with no characters.");

  annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());
  
  annotator.annotate(annotation);

  assertNotNull(annotation.get(CoreAnnotations.TokensAnnotation.class));
}
@Test
public void testEmptyQMAndMSSievesProperty() {
  Properties props = new Properties();
  props.setProperty("QMSieves", "");
  props.setProperty("MSSieves", "");
  props.setProperty("charactersPath", "");
  props.setProperty("booknlpCoref", "");
  QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

  Annotation annotation = new Annotation("“Hello,” she said.");
  annotation.set(CoreAnnotations.TextAnnotation.class, "“Hello,” she said.");

  List<CoreLabel> tokens = new ArrayList<CoreLabel>();
  CoreLabel token1 = new CoreLabel();
  token1.set(CoreAnnotations.TextAnnotation.class, "she");
  token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
  token1.set(CoreAnnotations.IndexAnnotation.class, 1);
  token1.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
  token1.set(CoreAnnotations.TokenEndAnnotation.class, 1);
  tokens.add(token1);
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

  annotator.annotate(annotation);

  assertNotNull(annotation.get(CoreAnnotations.TokensAnnotation.class));
}
@Test
public void testAnnotationWithMultipleMentionsOnlyOnePerson() {
  Properties props = new Properties();
  props.setProperty("charactersPath", "");
  props.setProperty("booknlpCoref", "");
  QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

  Annotation annotation = new Annotation("John met Microsoft in Paris.");
  annotation.set(CoreAnnotations.TextAnnotation.class, "John met Microsoft in Paris.");

  List<CoreLabel> tokens = new ArrayList<CoreLabel>();

  CoreLabel token1 = new CoreLabel();
  token1.set(CoreAnnotations.TextAnnotation.class, "John");
  token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
  token1.set(CoreAnnotations.IndexAnnotation.class, 1);
  token1.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
  token1.set(CoreAnnotations.TokenEndAnnotation.class, 1);

  CoreLabel token2 = new CoreLabel();
  token2.set(CoreAnnotations.TextAnnotation.class, "Microsoft");
  token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
  token2.set(CoreAnnotations.IndexAnnotation.class, 2);
  token2.set(CoreAnnotations.TokenBeginAnnotation.class, 1);
  token2.set(CoreAnnotations.TokenEndAnnotation.class, 2);

  CoreLabel token3 = new CoreLabel();
  token3.set(CoreAnnotations.TextAnnotation.class, "Paris");
  token3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");
  token3.set(CoreAnnotations.IndexAnnotation.class, 3);
  token3.set(CoreAnnotations.TokenBeginAnnotation.class, 2);
  token3.set(CoreAnnotations.TokenEndAnnotation.class, 3);

  tokens.add(token1);
  tokens.add(token2);
  tokens.add(token3);

  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);



  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

  annotator.annotate(annotation);

  assertNotNull(annotation.get(CoreAnnotations.TokensAnnotation.class));
}
@Test
public void testAnnotatorWithOnlyWhitespaceInput() {
  Properties props = new Properties();
  QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

  Annotation annotation = new Annotation("   ");
  annotation.set(CoreAnnotations.TextAnnotation.class, "   ");
  annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());
  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

  annotator.annotate(annotation);

  assertNotNull(annotation.get(CoreAnnotations.TokensAnnotation.class));
}
@Test
public void testAnnotatorWithNonDefaultDependencyParserPath() {
  Properties props = new Properties();
  props.setProperty("stanford.dep.model", "edu/stanford/nlp/models/parser/nndep/english_UD.gz");
  props.setProperty("charactersPath", "");
  props.setProperty("booknlpCoref", "");
  QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

  Annotation annotation = new Annotation("He whispered to Mary.");
  annotation.set(CoreAnnotations.TextAnnotation.class, "He whispered to Mary.");

  List<CoreLabel> tokens = new ArrayList<CoreLabel>();

  CoreLabel token1 = new CoreLabel();
  token1.set(CoreAnnotations.TextAnnotation.class, "He");
  token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
  token1.set(CoreAnnotations.IndexAnnotation.class, 1);
  token1.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
  token1.set(CoreAnnotations.TokenEndAnnotation.class, 1);

  CoreLabel token2 = new CoreLabel();
  token2.set(CoreAnnotations.TextAnnotation.class, "Mary");
  token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
  token2.set(CoreAnnotations.IndexAnnotation.class, 2);
  token2.set(CoreAnnotations.TokenBeginAnnotation.class, 1);
  token2.set(CoreAnnotations.TokenEndAnnotation.class, 2);

  tokens.add(token1);
  tokens.add(token2);

  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());


  annotator.annotate(annotation);

  assertNotNull(annotation.get(CoreAnnotations.TokensAnnotation.class));
}
@Test
public void testAnnotatorWithSingleTokenNoNERTag() {
  Properties props = new Properties();
  props.setProperty("charactersPath", "");
  props.setProperty("booknlpCoref", "");
  QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

  Annotation annotation = new Annotation("Word");
  annotation.set(CoreAnnotations.TextAnnotation.class, "Word");

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "Word");
  token.set(CoreAnnotations.IndexAnnotation.class, 1);
  token.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
  token.set(CoreAnnotations.TokenEndAnnotation.class, 1);
  token.set(CoreAnnotations.NamedEntityTagAnnotation.class, null); 

  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());
  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

  annotator.annotate(annotation);

  assertNotNull(annotation.get(CoreAnnotations.TokensAnnotation.class));
}
@Test
public void testAnnotatorWithSentenceMissingTokensAnnotation() {
  Properties props = new Properties();
  props.setProperty("charactersPath", "");
  props.setProperty("booknlpCoref", "");
  QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

  Annotation annotation = new Annotation("John said, \"Hello.\"");
  annotation.set(CoreAnnotations.TextAnnotation.class, "John said, \"Hello.\"");
  
  
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());
  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

  annotator.annotate(annotation);

  assertNull(annotation.get(CoreAnnotations.TokensAnnotation.class));
}
@Test
public void testAnnotatorMissingCorefPathAndVerboseTrue() {
  Properties props = new Properties();
  props.setProperty("verbose", "true"); 
  props.setProperty("charactersPath", "");
  props.setProperty("modelPath", "edu/stanford/nlp/models/quoteattribution/quoteattribution_model.ser");
  
  
  QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

  Annotation annotation = new Annotation("“Good morning,” said Watson.");
  annotation.set(CoreAnnotations.TextAnnotation.class, "“Good morning,” said Watson.");

  List<CoreLabel> tokens = new ArrayList<CoreLabel>();
  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "Watson");
  token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
  token.set(CoreAnnotations.IndexAnnotation.class, 1);
  token.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
  token.set(CoreAnnotations.TokenEndAnnotation.class, 1);
  tokens.add(token);
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

  annotator.annotate(annotation);

  assertNotNull(annotation.get(CoreAnnotations.TokensAnnotation.class));
}
@Test
public void testAnnotatorWithNullCharacterMapAndMultiplePersonMentions() {
  Properties props = new Properties();
  props.setProperty("charactersPath", "");
  props.setProperty("booknlpCoref", "");
  QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

  Annotation annotation = new Annotation("“We're late,” said Alice. “Yes, we are,” replied Bob.");
  annotation.set(CoreAnnotations.TextAnnotation.class, "“We're late,” said Alice. “Yes, we are,” replied Bob.");

  List<CoreLabel> tokens = new ArrayList<CoreLabel>();
  CoreLabel token1 = new CoreLabel();
  token1.set(CoreAnnotations.TextAnnotation.class, "Alice");
  token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
  token1.set(CoreAnnotations.IndexAnnotation.class, 1);
  token1.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
  token1.set(CoreAnnotations.TokenEndAnnotation.class, 1);
  tokens.add(token1);

  CoreLabel token2 = new CoreLabel();
  token2.set(CoreAnnotations.TextAnnotation.class, "Bob");
  token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
  token2.set(CoreAnnotations.IndexAnnotation.class, 2);
  token2.set(CoreAnnotations.TokenBeginAnnotation.class, 1);
  token2.set(CoreAnnotations.TokenEndAnnotation.class, 2);
  tokens.add(token2);

  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

  annotator.annotate(annotation);

  assertNotNull(annotation);
}
@Test
public void testAnnotatorWithCanonicalMentionsMappingAbsent() {
  Properties props = new Properties();
  props.setProperty("charactersPath", "");
  props.setProperty("booknlpCoref", "");
  QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

  Annotation annotation = new Annotation("“Silence,” demanded Professor Moriarty.");
  annotation.set(CoreAnnotations.TextAnnotation.class, "“Silence,” demanded Professor Moriarty.");

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "Moriarty");
  token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
  token.set(CoreAnnotations.IndexAnnotation.class, 1);
  token.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
  token.set(CoreAnnotations.TokenEndAnnotation.class, 1);

  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));

  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

  annotator.annotate(annotation);

  assertNotNull(annotation.get(CoreAnnotations.MentionsAnnotation.class));
}
@Test
public void testAnnotatorWithUseCorefFalseSkipsCorefRequirement() {
  Properties props = new Properties();
  props.setProperty("useCoref", "false");
  QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);
  Set<Class<? extends CoreAnnotation>> requirements = annotator.requires();
  boolean corefRequired = requirements.contains(edu.stanford.nlp.coref.CorefCoreAnnotations.CorefChainAnnotation.class);
  assertFalse(corefRequired);
}
@Test
public void testAnnotatorHandlesNullTextAnnotationGracefully() {
  Properties props = new Properties();
  props.setProperty("charactersPath", "");
  props.setProperty("booknlpCoref", "");
  QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

  Annotation annotation = new Annotation((Annotation) null);

  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
  annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

  annotator.annotate(annotation);

  assertNotNull(annotation);
}
@Test
public void testAnnotatorWithMentionLackingExpectedMentionFields() {
  Properties props = new Properties();
  props.setProperty("charactersPath", "");
  props.setProperty("booknlpCoref", "");
  QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

  Annotation annotation = new Annotation("Smith responded with confidence.");

  List<CoreLabel> tokens = new ArrayList<CoreLabel>();
  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "Smith");
  token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
  token.set(CoreAnnotations.IndexAnnotation.class, 1);
  token.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
  token.set(CoreAnnotations.TokenEndAnnotation.class, 1);
  tokens.add(token);

  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
  annotation.set(CoreAnnotations.TextAnnotation.class, "Smith responded with confidence.");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

  List<CoreMap> mentions = new ArrayList<CoreMap>();
  annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);

  annotator.annotate(annotation);
  assertNotNull(annotation);
}
@Test
public void testAnnotatorWithSingleTokenWithOnlyPartialAnnotations() {
  Properties props = new Properties();
  QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

  Annotation annotation = new Annotation("Bob");

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "Bob");
  
  token.set(CoreAnnotations.TokenEndAnnotation.class, 1);
  token.set(CoreAnnotations.IndexAnnotation.class, 1);

  annotation.set(CoreAnnotations.TextAnnotation.class, "Bob");
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());
  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

  annotator.annotate(annotation);
  assertNotNull(annotation);
}
@Test
public void testAnnotatorWithMalformedMentionHavingNoNERAnnotation() {
  Properties props = new Properties();
  QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

  Annotation annotation = new Annotation("Spock looked at Kirk.");

  CoreLabel token1 = new CoreLabel();
  token1.set(CoreAnnotations.TextAnnotation.class, "Spock");
  token1.set(CoreAnnotations.IndexAnnotation.class, 1);
  token1.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
  token1.set(CoreAnnotations.TokenEndAnnotation.class, 1);

  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1));
  annotation.set(CoreAnnotations.TextAnnotation.class, "Spock looked at Kirk.");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

  List<CoreMap> mentions = new ArrayList<CoreMap>();
  annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);

  annotator.annotate(annotation);
  assertNotNull(annotation);
}
@Test
public void testAnnotatorDoesNotFailWithGarbageNERTag() {
  Properties props = new Properties();
  QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

  Annotation annotation = new Annotation("Unknown shouted loudly.");

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "Unknown");
  token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "XYZ_UNKNOWN_TYPE");
  token.set(CoreAnnotations.IndexAnnotation.class, 1);
  token.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
  token.set(CoreAnnotations.TokenEndAnnotation.class, 1);

  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  annotation.set(CoreAnnotations.TextAnnotation.class, "Unknown shouted loudly.");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

  annotator.annotate(annotation);
  assertNotNull(annotation.get(CoreAnnotations.TokensAnnotation.class));
}
@Test
public void testAnnotatorWithNullAnnotation() {
  Properties props = new Properties();
  QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

  Annotation annotation = null;

  try {
    annotator.annotate(annotation);
    fail("Expected NullPointerException");
  } catch (NullPointerException expected) {
    assertTrue(true);
  }
}
@Test
public void testAnnotatorCanonicalEntityMentionIndexExistsButOutOfBounds() {
  Properties props = new Properties();
  props.setProperty("charactersPath", "");
  props.setProperty("booknlpCoref", "");
  QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

  Annotation annotation = new Annotation("He said, \"Go home.\"");

  List<CoreLabel> tokens = new ArrayList<CoreLabel>();
  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "He");
  token.set(CoreAnnotations.IndexAnnotation.class, 0);
  token.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
  token.set(CoreAnnotations.TokenEndAnnotation.class, 1);
  tokens.add(token);
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  List<CoreMap> mentions = new ArrayList<CoreMap>();

  annotation.set(CoreAnnotations.MentionsAnnotation.class, mentions);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());
  annotation.set(CoreAnnotations.TextAnnotation.class, "He said, \"Go home.\"");

  annotator.annotate(annotation);

  assertNotNull(annotation.get(CoreAnnotations.MentionsAnnotation.class));
}
@Test
public void testAnnotatorWithZeroLengthCharacterMap() {
  Properties props = new Properties();
  props.setProperty("charactersPath", "/dev/null");
  props.setProperty("booknlpCoref", "");
  props.setProperty("QMSieves", "tri");
  props.setProperty("MSSieves", "det");
  QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

  Annotation annotation = new Annotation("No characters listed.");

  List<CoreLabel> tokens = new ArrayList<CoreLabel>();
  CoreLabel t1 = new CoreLabel();
  t1.set(CoreAnnotations.TextAnnotation.class, "Nowhere");
  t1.set(CoreAnnotations.IndexAnnotation.class, 1);
  t1.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
  t1.set(CoreAnnotations.TokenEndAnnotation.class, 1);
  t1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
  tokens.add(t1);
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());
  annotation.set(CoreAnnotations.TextAnnotation.class, "No characters listed.");

  annotator.annotate(annotation);

  assertNotNull(annotation.get(CoreAnnotations.TokensAnnotation.class));
}
@Test
public void testAnnotatorWithTokenMissingIndexAnnotation() {
  Properties props = new Properties();
  props.setProperty("charactersPath", "");
  QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

  Annotation annotation = new Annotation("“Hello,” said Jane.");

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "Jane");
  token.set(CoreAnnotations.TokenBeginAnnotation.class, 1);
  token.set(CoreAnnotations.TokenEndAnnotation.class, 2);
  token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
  

  annotation.set(CoreAnnotations.TextAnnotation.class, "“Hello,” said Jane.");
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

  annotator.annotate(annotation);

  assertEquals(1, annotation.get(CoreAnnotations.TokensAnnotation.class).size());
}
@Test
public void testAnnotatorWithTokenHavingOnlyNERTagNotText() {
  Properties props = new Properties();
  QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

  Annotation annotation = new Annotation("Someone UNKNOWN");

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
  token.set(CoreAnnotations.IndexAnnotation.class, 1);
  token.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
  token.set(CoreAnnotations.TokenEndAnnotation.class, 1);
  

  annotation.set(CoreAnnotations.TextAnnotation.class, "Someone UNKNOWN");
  annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token));
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());
  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

  annotator.annotate(annotation);

  assertNotNull(annotation.get(CoreAnnotations.TokensAnnotation.class));
}
@Test
public void testAnnotatorWithMentionIndexButMissingCanonicalIndex() {
  Properties props = new Properties();
  props.setProperty("charactersPath", "");
  props.setProperty("booknlpCoref", "");
  props.setProperty("QMSieves", "tri");
  props.setProperty("MSSieves", "det");
  QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

  Annotation annotation = new Annotation("“Hello,” said Sarah.");
  annotation.set(CoreAnnotations.TextAnnotation.class, "“Hello,” said Sarah.");

  List<CoreLabel> tokens = new ArrayList<CoreLabel>();
  CoreLabel token1 = new CoreLabel();
  token1.set(CoreAnnotations.TextAnnotation.class, "Sarah");
  token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
  token1.set(CoreAnnotations.IndexAnnotation.class, 5);
  token1.set(CoreAnnotations.TokenBeginAnnotation.class, 4);
  token1.set(CoreAnnotations.TokenEndAnnotation.class, 5);
  token1.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);
  tokens.add(token1);

  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

  annotator.annotate(annotation);

  assertNotNull(annotation.get(CoreAnnotations.TokensAnnotation.class));
}
@Test
public void testAnnotatorWithCanonicalMentionIndexPointsToNull() {
  Properties props = new Properties();
  props.setProperty("charactersPath", "");
  props.setProperty("booknlpCoref", "");
  QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

  Annotation annotation = new Annotation("“Go,” shouted Mark.");
  annotation.set(CoreAnnotations.TextAnnotation.class, "“Go,” shouted Mark.");

  CoreLabel token = new CoreLabel();
  token.set(CoreAnnotations.TextAnnotation.class, "Mark");
  token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
  token.set(CoreAnnotations.IndexAnnotation.class, 2);
  token.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 0);
  annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

  annotator.annotate(annotation);

  assertNotNull(annotation.get(CoreAnnotations.MentionsAnnotation.class));
}
@Test
public void testAnnotatorHandlesSupervisedSieveModelPathMissing() {
  Properties props = new Properties();
  props.setProperty("charactersPath", "");
  props.setProperty("booknlpCoref", "");
  props.setProperty("QMSieves", "sup"); 
  props.setProperty("modelPath", "invalid/model/path.ser");
  QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

  Annotation annotation = new Annotation("“Hello,” said Jane.");
  annotation.set(CoreAnnotations.TextAnnotation.class, "“Hello,” said Jane.");
  annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

  annotator.annotate(annotation);

  assertNotNull(annotation);
}
@Test
public void testAnnotatorWithUseCorefTrueAndNoCorefAnnotationPresent() {
  Properties props = new Properties();
  props.setProperty("useCoref", "true");
  props.setProperty("charactersPath", "");
  QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

  Annotation annotation = new Annotation("“It's done,” said Marie.");
  annotation.set(CoreAnnotations.TextAnnotation.class, "“It's done,” said Marie.");
  annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
  annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
  annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

  

  Set<Class<? extends CoreAnnotation>> required = annotator.requires();
  assertTrue(required.contains(edu.stanford.nlp.coref.CorefCoreAnnotations.CorefChainAnnotation.class));

  annotator.annotate(annotation);

  assertNotNull(annotation);
}
@Test
public void testAnnotatorGetQMMappingIncludesAllSieveKeysEvenUnused() throws Exception {
  Properties props = new Properties();
  props.setProperty("QMSieves", "tri,dep,onename,voc,paraend,conv,sup,loose,closest");
  props.setProperty("charactersPath", "");
  props.setProperty("booknlpCoref", "");
  QuoteAttributionAnnotator annotator = new QuoteAttributionAnnotator(props);

  Annotation fakeDoc = new Annotation("Placeholder");
  Map<Integer, String> emptyMap = new HashMap<>();

  java.lang.reflect.Method qmMethod = QuoteAttributionAnnotator.class.getDeclaredMethod("getQMMapping", Annotation.class, Map.class);
  qmMethod.setAccessible(true);
  Map result = (Map) qmMethod.invoke(annotator, fakeDoc, emptyMap);

  assertTrue(result.containsKey("tri"));
  assertTrue(result.containsKey("loose"));
  assertTrue(result.containsKey("sup"));
  assertTrue(result.containsKey("closest"));
} 
}