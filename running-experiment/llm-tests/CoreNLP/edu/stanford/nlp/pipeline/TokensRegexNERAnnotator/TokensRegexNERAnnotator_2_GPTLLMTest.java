package edu.stanford.nlp.pipeline;

import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.process.*;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.PropertiesUtils;
import junit.framework.TestCase;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import org.junit.Test;

import static org.junit.Assert.*;

public class TokensRegexNERAnnotator_2_GPTLLMTest {

 @Test
  public void testSimpleAnnotation() throws Exception {
    Path file = Files.createTempFile("regexner", ".tab");
    Files.write(file, Arrays.asList(
      "pattern\tner\toverwrite\tpriority\tgroup",
      "Stanford\tSCHOOL\tO\t1.0\t0"
    ));

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(file.toAbsolutePath().toString());

    CoreLabel token = new CoreLabel();
    token.setWord("Stanford");
    token.setValue("Stanford");
    token.setOriginalText("Stanford");
    token.set(CoreAnnotations.TextAnnotation.class, "Stanford");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    Annotation annotation = new Annotation("Stanford");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    assertEquals("SCHOOL", token.ner());

    Files.deleteIfExists(file);
  }
@Test
  public void testCaseInsensitiveMatch() throws Exception {
    Path file = Files.createTempFile("regexner", ".tab");
    Files.write(file, Arrays.asList(
      "pattern\tner\toverwrite\tpriority\tgroup",
      "stanford\tSCHOOL\tO\t1.0\t0"
    ));

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(file.toAbsolutePath().toString(), true);

    CoreLabel token = new CoreLabel();
    token.setWord("Stanford");
    token.setValue("Stanford");
    token.setOriginalText("Stanford");
    token.set(CoreAnnotations.TextAnnotation.class, "Stanford");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    Annotation annotation = new Annotation("Stanford");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    assertEquals("SCHOOL", token.ner());

    Files.deleteIfExists(file);
  }
@Test
  public void testNoOverwriteDueToExistingNER() throws Exception {
    Path file = Files.createTempFile("regexner", ".tab");
    Files.write(file, Arrays.asList(
      "pattern\tner\toverwrite\tpriority\tgroup",
      "Stanford\tSCHOOL\tLOCATION\t1.0\t0"
    ));

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(file.toAbsolutePath().toString());

    CoreLabel token = new CoreLabel();
    token.setWord("Stanford");
    token.setValue("Stanford");
    token.setOriginalText("Stanford");
    token.set(CoreAnnotations.TextAnnotation.class, "Stanford");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    Annotation annotation = new Annotation("Stanford");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    assertEquals("LOCATION", token.ner());

    Files.deleteIfExists(file);
  }
@Test
  public void testMultiTokenMatch() throws Exception {
    Path file = Files.createTempFile("regexner", ".tab");
    Files.write(file, Arrays.asList(
      "pattern\tner\toverwrite\tpriority\tgroup",
      "New\\s+York\tCITY\tO\t1.0\t0"
    ));

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(file.toAbsolutePath().toString());

    CoreLabel token1 = new CoreLabel();
    token1.setWord("New");
    token1.set(CoreAnnotations.TextAnnotation.class, "New");
    token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("York");
    token2.set(CoreAnnotations.TextAnnotation.class, "York");
    token2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);

    Annotation annotation = new Annotation("New York");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    assertEquals("CITY", token1.ner());
    assertEquals("CITY", token2.ner());

    Files.deleteIfExists(file);
  }
@Test
  public void testPOSFilteringNoMatch() throws Exception {
    Path file = Files.createTempFile("regexner", ".tab");
    Files.write(file, Arrays.asList(
      "pattern\tner\toverwrite\tpriority\tgroup",
      "Doctor\tTITLE\tO\t1.0\t0"
    ));

    Properties props = new Properties();
    props.setProperty("tokenregexner.mapping", file.toAbsolutePath().toString());
    props.setProperty("tokenregexner.validpospattern", "VB.*");
    props.setProperty("tokenregexner.posmatchtype", "MATCH_ALL_TOKENS");

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);

    CoreLabel token = new CoreLabel();
    token.setWord("Doctor");
    token.set(CoreAnnotations.TextAnnotation.class, "Doctor");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    Annotation annotation = new Annotation("Doctor");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    assertEquals("O", token.ner());

    Files.deleteIfExists(file);
  }
@Test(expected = RuntimeException.class)
  public void testMissingTokensThrowsException() throws Exception {
    Path file = Files.createTempFile("regexner", ".tab");
    Files.write(file, Arrays.asList(
      "pattern\tner\toverwrite\tpriority\tgroup",
      "Stanford\tSCHOOL\tO\t1.0\t0"
    ));

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(file.toAbsolutePath().toString());

    Annotation annotation = new Annotation("No tokens here");

    annotator.annotate(annotation);

    Files.deleteIfExists(file);
  }
@Test
  public void testIncludeMultiFieldHeader() throws Exception {
    Path file = Files.createTempFile("regexner", ".tab");
    Files.write(file, Arrays.asList(
      "pattern\tner\toverwrite\tpriority\tgroup\tSpeaker",
      "John\tPERSON\tO\t1.0\t0\tSPEAKER_LABEL"
    ));

    Properties props = new Properties();
    props.setProperty("tokenregexner.mapping", file.toAbsolutePath().toString());
    props.setProperty("tokenregexner.mapping.field.Speaker", "edu.stanford.nlp.ling.CoreAnnotations$SpeakerAnnotation");

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);

    CoreLabel token = new CoreLabel();
    token.setWord("John");
    token.set(CoreAnnotations.TextAnnotation.class, "John");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    Annotation annotation = new Annotation("John");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    assertEquals("PERSON", token.ner());
    assertEquals("SPEAKER_LABEL", token.get(CoreAnnotations.SpeakerAnnotation.class));

    Files.deleteIfExists(file);
  }
@Test
  public void testOverwriteDisabledForOverlappingNERSpan() throws Exception {
    Path file = Files.createTempFile("regexner_overlap", ".tab");
    Files.write(file, Arrays.asList(
      "pattern\tner\toverwrite\tpriority\tgroup",
      "ABC\tORG\tO\t1.0\t0"
    ));

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(file.toAbsolutePath().toString());

    CoreLabel token1 = new CoreLabel();
    token1.setWord("The");
    token1.set(CoreAnnotations.TextAnnotation.class, "The");
    token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "DT");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORG");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("ABC");
    token2.set(CoreAnnotations.TextAnnotation.class, "ABC");
    token2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORG");

    CoreLabel token3 = new CoreLabel();
    token3.setWord("Company");
    token3.set(CoreAnnotations.TextAnnotation.class, "Company");
    token3.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    token3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORG");

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);

    Annotation annotation = new Annotation("The ABC Company");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    assertEquals("ORG", token2.ner());

    Files.deleteIfExists(file);
  }
@Test
  public void testInvalidAnnotateGroupThrows() throws Exception {
    Path file = Files.createTempFile("regexner_badgroup", ".tab");
    Files.write(file, Arrays.asList(
      "pattern\tner\toverwrite\tpriority\tgroup",
      "Stanford\tSCHOOL\tO\t1.0\t10"
    ));

    try {
      new TokensRegexNERAnnotator(file.toAbsolutePath().toString());
      fail("Expected RuntimeException due to invalid annotateGroup index");
    } catch (RuntimeException expected) {
      assertTrue(expected.getMessage().contains("Invalid match group"));
    }

    Files.deleteIfExists(file);
  }
@Test
  public void testEmptyMappingFileHandledGracefully() throws Exception {
    Path file = Files.createTempFile("regexner_empty", ".tab");
    Files.write(file, Collections.singletonList("pattern\tner\toverwrite\tpriority\tgroup"));

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(file.toAbsolutePath().toString());

    CoreLabel token = new CoreLabel();
    token.setWord("Nothing");
    token.set(CoreAnnotations.TextAnnotation.class, "Nothing");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> tokens = Collections.singletonList(token);

    Annotation annotation = new Annotation("Nothing");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    assertEquals("O", token.ner()); 

    Files.deleteIfExists(file);
  }
@Test
  public void testHeaderMissingRequiredFieldThrows() throws Exception {
    Path file = Files.createTempFile("regexner_badheader", ".tab");
    Files.write(file, Arrays.asList(
      "ner\toverwrite\tpriority\tgroup", 
      "Stanford\tSCHOOL\tO\t1.0"
    ));

    try {
      new TokensRegexNERAnnotator(file.toAbsolutePath().toString());
      fail("Expected IllegalArgumentException for missing pattern field");
    } catch (IllegalArgumentException expected) {
      assertTrue(expected.getMessage().contains("does not contain 'pattern'"));
    }

    Files.deleteIfExists(file);
  }
@Test
  public void testZeroPriorityDuplicatePatternsKeepsFirst() throws Exception {
    Path file = Files.createTempFile("regexner_duplicate", ".tab");
    Files.write(file, Arrays.asList(
      "pattern\tner\toverwrite\tpriority\tgroup",
      "Stanford\tUNIV\tO\t0.0\t0",
      "Stanford\tSCHOOL\tO\t0.0\t0"
    ));

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(file.toAbsolutePath().toString());

    CoreLabel token = new CoreLabel();
    token.setWord("Stanford");
    token.set(CoreAnnotations.TextAnnotation.class, "Stanford");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> tokens = Collections.singletonList(token);

    Annotation annotation = new Annotation("Stanford");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    assertEquals("UNIV", token.ner()); 

    Files.deleteIfExists(file);
  }
@Test
  public void testTokensRegexSyntaxPatternIsHandled() throws Exception {
    Path file = Files.createTempFile("regexner_tokensregex", ".tab");
    Files.write(file, Arrays.asList(
      "pattern\tner\toverwrite\tpriority\tgroup",
      "( /Dr\\./ /Smith/ )\tPERSON\tO\t1.0\t0"
    ));

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(file.toAbsolutePath().toString());

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Dr.");
    token1.set(CoreAnnotations.TextAnnotation.class, "Dr.");
    token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Smith");
    token2.set(CoreAnnotations.TextAnnotation.class, "Smith");
    token2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> tokens = Arrays.asList(token1, token2);

    Annotation annotation = new Annotation("Dr. Smith");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    assertEquals("PERSON", token1.ner());
    assertEquals("PERSON", token2.ner());

    Files.deleteIfExists(file);
  }
@Test
  public void testOverwritingWithNullOriginalNER() throws Exception {
    Path file = Files.createTempFile("regexner_nullner", ".tab");
    Files.write(file, Arrays.asList(
      "pattern\tner\toverwrite\tpriority\tgroup",
      "London\tCITY\tO\t1.0\t0"
    ));

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(file.toAbsolutePath().toString());

    CoreLabel token = new CoreLabel();
    token.setWord("London");
    token.set(CoreAnnotations.TextAnnotation.class, "London");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    

    List<CoreLabel> tokens = Collections.singletonList(token);

    Annotation annotation = new Annotation("London");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    assertEquals("CITY", token.ner());

    Files.deleteIfExists(file);
  }
@Test
  public void testPosMatchTypeOneTokenPhraseOnly() throws Exception {
    Path file = Files.createTempFile("regexner_pos_one", ".tab");
    Files.write(file, Arrays.asList(
      "pattern\tner\toverwrite\tpriority\tgroup",
      "Apple\tCOMPANY\tO\t1.0\t0"
    ));

    Properties props = new Properties();
    props.setProperty("tokenregexner.mapping", file.toAbsolutePath().toString());
    props.setProperty("tokenregexner.validpospattern", "NN.*");
    props.setProperty("tokenregexner.posmatchtype", "MATCH_ONE_TOKEN_PHRASE_ONLY");

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);

    CoreLabel token = new CoreLabel();
    token.setWord("Apple");
    token.set(CoreAnnotations.TextAnnotation.class, "Apple");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");  
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> tokens = Collections.singletonList(token);

    Annotation ann = new Annotation("Apple");
    ann.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(ann);

    assertEquals("O", token.ner());  

    Files.deleteIfExists(file);
  }
@Test
  public void testOverwriteWhenOriginalNerNull() throws Exception {
    Path file = Files.createTempFile("regexner_overwrite_null", ".tab");
    Files.write(file, Arrays.asList(
      "pattern\tner\toverwrite\tpriority\tgroup",
      "China\tCOUNTRY\t\t1.0\t0"
    ));

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(file.toAbsolutePath().toString());

    CoreLabel token = new CoreLabel();
    token.setWord("China");
    token.set(CoreAnnotations.TextAnnotation.class, "China");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    

    List<CoreLabel> tokens = Collections.singletonList(token);

    Annotation annotation = new Annotation("China");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    assertEquals("COUNTRY", token.ner());

    Files.deleteIfExists(file);
  }
@Test
  public void testNoOverwriteDueToNotInMyLabelsOrOverwriteableTypes() throws Exception {
    Path file = Files.createTempFile("regexner_nochange_label", ".tab");
    Files.write(file, Arrays.asList(
      "pattern\tner\toverwrite\tpriority\tgroup",
      "Berlin\tTOURISM\t\t1.0\t0"
    ));

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(file.toAbsolutePath().toString());

    CoreLabel token = new CoreLabel();
    token.setWord("Berlin");
    token.set(CoreAnnotations.TextAnnotation.class, "Berlin");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON"); 

    List<CoreLabel> tokens = Collections.singletonList(token);

    Annotation annotation = new Annotation("Berlin");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    assertEquals("PERSON", token.ner());  

    Files.deleteIfExists(file);
  }
@Test
  public void testDuplicatePatternWithHigherPriorityWins() throws Exception {
    Path file = Files.createTempFile("regexner_dup_priority", ".tab");
    Files.write(file, Arrays.asList(
      "pattern\tner\toverwrite\tpriority\tgroup",
      "Google\tCOMPANY\t\t0.5\t0",
      "Google\tORG\t\t1.5\t0"
    ));

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(file.toAbsolutePath().toString());

    CoreLabel token = new CoreLabel();
    token.setWord("Google");
    token.set(CoreAnnotations.TextAnnotation.class, "Google");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> tokens = Collections.singletonList(token);

    Annotation annotation = new Annotation("Google");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    assertEquals("ORG", token.ner());

    Files.deleteIfExists(file);
  }
@Test
  public void testPosIsNullWithValidPosPattern() throws Exception {
    Path file = Files.createTempFile("regexner_posnull", ".tab");
    Files.write(file, Arrays.asList(
      "pattern\tner\toverwrite\tpriority\tgroup",
      "Paris\tPLACE\t\t1.0\t0"
    ));

    Properties props = new Properties();
    props.setProperty("tokenregexner.mapping", file.toAbsolutePath().toString());
    props.setProperty("tokenregexner.validpospattern", "NN.*");

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);

    CoreLabel token = new CoreLabel();
    token.setWord("Paris");
    token.set(CoreAnnotations.TextAnnotation.class, "Paris");
    
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> tokens = Collections.singletonList(token);

    Annotation annotation = new Annotation("Paris");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    assertEquals("PLACE", token.ner());  

    Files.deleteIfExists(file);
  }
@Test
  public void testNonNumericPriorityThrows() throws Exception {
    Path file = Files.createTempFile("regexner_badprio", ".tab");
    Files.write(file, Arrays.asList(
      "pattern\tner\toverwrite\tpriority\tgroup",
      "Pizza\tFOOD\t\tABC\t0"
    ));

    try {
      new TokensRegexNERAnnotator(file.toAbsolutePath().toString());
      fail("Expected exception due to non-numeric priority");
    } catch (IllegalArgumentException expected) {
      assertTrue(expected.getMessage().contains("Invalid priority"));
    }

    Files.deleteIfExists(file);
  }
@Test
  public void testCommaInsideTypeFieldStripsToFirst() throws Exception {
    Path file = Files.createTempFile("regexner_commatype", ".tab");
    Files.write(file, Arrays.asList(
      "pattern\tner\toverwrite\tpriority\tgroup",
      "Moon\tASTRO,OBJECT\t\t1.0\t0"
    ));

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(file.toAbsolutePath().toString());

    CoreLabel token = new CoreLabel();
    token.setWord("Moon");
    token.set(CoreAnnotations.TextAnnotation.class, "Moon");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> tokens = Collections.singletonList(token);

    Annotation ann = new Annotation("Moon");
    ann.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(ann);

    assertEquals("ASTRO", token.ner()); 

    Files.deleteIfExists(file);
  }
@Test
  public void testNonTokensRegexParenthesizedPatternParsedAsPlainRegex() throws Exception {
    Path file = Files.createTempFile("regexner_fakeparen", ".tab");
    Files.write(file, Arrays.asList(
      "pattern\tner\toverwrite\tpriority\tgroup",
      "(Apple)\tFRUIT\t\t1.0\t0"
    ));

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(file.toAbsolutePath().toString());

    CoreLabel token = new CoreLabel();
    token.setWord("(Apple)");
    token.set(CoreAnnotations.TextAnnotation.class, "(Apple)");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> tokens = Collections.singletonList(token);

    Annotation ann = new Annotation("(Apple)");
    ann.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(ann);

    assertEquals("O", token.ner()); 

    Files.deleteIfExists(file);
  }
@Test
  public void testHeaderOnlyPatternAndNERFields() throws Exception {
    Path file = Files.createTempFile("regexner_simpleheader", ".tab");
    Files.write(file, Arrays.asList(
      "pattern\tner",    
      "San\tCITY"        
    ));

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(file.toAbsolutePath().toString());

    CoreLabel tok = new CoreLabel();
    tok.setWord("San");
    tok.set(CoreAnnotations.TextAnnotation.class, "San");
    tok.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    tok.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(tok);

    Annotation ann = new Annotation("San");
    ann.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(ann);

    assertEquals("CITY", tok.ner());

    Files.deleteIfExists(file);
  }
@Test
  public void testCaseSensitiveFailsWhenPatternIsLowercase() throws Exception {
    Path file = Files.createTempFile("regexner_sensitive", ".tab");
    Files.write(file, Arrays.asList(
      "pattern\tner\toverwrite\tpriority\tgroup",
      "san\tCITY\tO\t1.0\t0"
    ));

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(file.toAbsolutePath().toString(), false);

    CoreLabel tok = new CoreLabel();
    tok.setWord("San");
    tok.set(CoreAnnotations.TextAnnotation.class, "San");
    tok.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    tok.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(tok);

    Annotation ann = new Annotation("San");
    ann.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(ann);

    assertEquals("O", tok.ner());

    Files.deleteIfExists(file);
  }
@Test
  public void testMatchAtLeastOneTokenPOSPatternSucceeds() throws Exception {
    Path file = Files.createTempFile("regexner_pos_some", ".tab");
    Files.write(file, Arrays.asList(
      "pattern\tner\toverwrite\tpriority\tgroup",
      "New\\s+Orleans\tCITY\tO\t1.0\t0"
    ));

    Properties props = new Properties();
    props.setProperty("tokenregexner.mapping", file.toAbsolutePath().toString());
    props.setProperty("tokenregexner.posmatchtype", "MATCH_AT_LEAST_ONE_TOKEN");
    props.setProperty("tokenregexner.validpospattern", "NNP");

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);

    CoreLabel t1 = new CoreLabel();
    t1.setWord("New");
    t1.set(CoreAnnotations.TextAnnotation.class, "New");
    t1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    t1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    CoreLabel t2 = new CoreLabel();
    t2.setWord("Orleans");
    t2.set(CoreAnnotations.TextAnnotation.class, "Orleans");
    t2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");  

    List<CoreLabel> tokens = Arrays.asList(t1, t2);
    for (CoreLabel t : tokens) {
      t.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    }

    Annotation ann = new Annotation("New Orleans");
    ann.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(ann);

    assertEquals("CITY", t1.ner());
    assertEquals("CITY", t2.ner());

    Files.deleteIfExists(file);
  }
@Test
  public void testSameLengthPriorityResolution() throws Exception {
    Path file = Files.createTempFile("regexner_multiresolve", ".tab");
    Files.write(file, Arrays.asList(
      "pattern\tner\toverwrite\tpriority\tgroup",
      "Silicon\\s+Valley\tTECH\tO\t1.0\t0",
      "Silicon\\s+Valley\tPLACE\tO\t2.0\t0"
    ));

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(file.toAbsolutePath().toString());

    CoreLabel t1 = new CoreLabel();
    t1.setWord("Silicon");
    t1.set(CoreAnnotations.TextAnnotation.class, "Silicon");
    t1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    t1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    CoreLabel t2 = new CoreLabel();
    t2.setWord("Valley");
    t2.set(CoreAnnotations.TextAnnotation.class, "Valley");
    t2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    t2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> tokens = Arrays.asList(t1, t2);
    Annotation ann = new Annotation("Silicon Valley");
    ann.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(ann);

    assertEquals("PLACE", t1.ner());
    assertEquals("PLACE", t2.ner());

    Files.deleteIfExists(file);
  }
@Test
  public void testMultipleValidPosPatternsUsedInDifferentFiles() throws Exception {
    Path f1 = Files.createTempFile("f1", ".tab");
    Files.write(f1, Arrays.asList("pattern\tner\toverwrite\tpriority\tgroup", "Jazz\tMUSIC\tO\t1.0\t0"));

    Path f2 = Files.createTempFile("f2", ".tab");
    Files.write(f2, Arrays.asList("pattern\tner\toverwrite\tpriority\tgroup", "Blues\tGENRE\tO\t1.0\t0"));

    Properties props = new Properties();
    props.setProperty("tokenregexner.mapping",
      "header=pattern ner overwrite priority group, validpospattern=JJ, " + f1.toAbsolutePath() + ";" +
      "header=pattern ner overwrite priority group, validpospattern=NN, " + f2.toAbsolutePath());
    props.setProperty("tokenregexner.posmatchtype", "MATCH_AT_LEAST_ONE_TOKEN");

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);

    CoreLabel t1 = new CoreLabel();
    t1.setWord("Jazz");
    t1.set(CoreAnnotations.TextAnnotation.class, "Jazz");
    t1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "JJ");
    t1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    CoreLabel t2 = new CoreLabel();
    t2.setWord("Blues");
    t2.set(CoreAnnotations.TextAnnotation.class, "Blues");
    t2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    t2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> tokens = Arrays.asList(t1, t2);
    Annotation annotation = new Annotation("Jazz Blues");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    assertEquals("MUSIC", t1.ner());
    assertEquals("GENRE", t2.ner());

    Files.deleteIfExists(f1);
    Files.deleteIfExists(f2);
  }
@Test
  public void testHeaderTrueExtractsHeaderAndParses() throws Exception {
    Path file = Files.createTempFile("true_header", ".tab");
    Files.write(file, Arrays.asList(
      "pattern\tner\toverwrite\tpriority\tgroup",
      "England\tCOUNTRY\tO\t1.0\t0"
    ));

    Properties props = new Properties();
    props.setProperty("tokenregexner.mapping", "ignorecase=true, header=true, " + file.toAbsolutePath());

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);

    CoreLabel tok = new CoreLabel();
    tok.setWord("England");
    tok.set(CoreAnnotations.TextAnnotation.class, "England");
    tok.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    tok.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> tokens = Collections.singletonList(tok);

    Annotation ann = new Annotation("England");
    ann.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(ann);

    assertEquals("COUNTRY", tok.ner());

    Files.deleteIfExists(file);
  }
@Test(expected = RuntimeException.class)
  public void testAnnotateGroupOutOfBoundsTriggersException() throws Exception {
    Path file = Files.createTempFile("regexner_badgroup", ".tab");
    Files.write(file, Arrays.asList(
      "pattern\tner\toverwrite\tpriority\tgroup",
      "Red\tCOLOR\tO\t1.0\t6"
    ));

    new TokensRegexNERAnnotator(file.toAbsolutePath().toString());

    Files.deleteIfExists(file);
  }
@Test
  public void testMultipleAnnotationsForSamePatternDifferentFields() throws Exception {
    Path file = Files.createTempFile("regexner_multiannotate", ".tab");
    Files.write(file, Arrays.asList(
      "pattern\tner\tSpeaker\tpriority\tgroup",
      "Obama\tPERSON\tOBAMA_SPEAKER\t1.0\t0"
    ));

    Properties props = new Properties();
    props.setProperty("tokenregexner.mapping", file.toAbsolutePath().toString());
    props.setProperty("tokenregexner.mapping.field.Speaker",
      "edu.stanford.nlp.ling.CoreAnnotations$SpeakerAnnotation");

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);

    CoreLabel tok = new CoreLabel();
    tok.setWord("Obama");
    tok.set(CoreAnnotations.TextAnnotation.class, "Obama");
    tok.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    tok.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> tokens = Collections.singletonList(tok);

    Annotation ann = new Annotation("Obama");
    ann.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(ann);

    assertEquals("PERSON", tok.ner());
    assertEquals("OBAMA_SPEAKER", tok.get(CoreAnnotations.SpeakerAnnotation.class));

    Files.deleteIfExists(file);
  }
@Test
  public void testExtraColumnsInMappingLineAreIgnored() throws Exception {
    Path file = Files.createTempFile("regexner_extracol", ".tab");
    Files.write(file, Arrays.asList(
      "pattern\tner\toverwrite\tpriority\tgroup",
      "Amazon\tORG\tMISC\t1.0\t0\tEXTRA\tFIELD\tSHOULD\tBE\tIGNORED"
    ));

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(file.toAbsolutePath().toString());

    CoreLabel tok = new CoreLabel();
    tok.setWord("Amazon");
    tok.set(CoreAnnotations.TextAnnotation.class, "Amazon");
    tok.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    tok.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> tokens = Collections.singletonList(tok);

    Annotation ann = new Annotation("Amazon");
    ann.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(ann);

    assertEquals("ORG", tok.ner());

    Files.deleteIfExists(file);
  }
@Test
  public void testDefaultBackgroundSymbolOandMISC() throws Exception {
    Path file = Files.createTempFile("regexner_bgdefault", ".tab");
    Files.write(file, Arrays.asList(
      "pattern\tner\toverwrite\tpriority\tgroup",
      "Earth\tPLANET\t\t1.0\t0"
    ));

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(file.toAbsolutePath().toString());

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Earth");
    token.setWord("Earth");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "MISC");

    List<CoreLabel> tokens = Collections.singletonList(token);
    Annotation annotation = new Annotation("Earth");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    assertEquals("PLANET", token.ner());

    Files.deleteIfExists(file);
  }
@Test
  public void testMatchFailsWhenPOSNull() throws Exception {
    Path file = Files.createTempFile("regexner_nullpos", ".tab");
    Files.write(file, Arrays.asList(
      "pattern\tner\toverwrite\tpriority\tgroup",
      "Galaxy\tASTRO\t\t1.0\t0"
    ));

    Properties props = new Properties();
    props.setProperty("tokenregexner.mapping", file.toAbsolutePath().toString());
    props.setProperty("tokenregexner.validpospattern", "NN.*");
    props.setProperty("tokenregexner.posmatchtype", "MATCH_AT_LEAST_ONE_TOKEN");

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Galaxy");
    token.setWord("Galaxy");
    
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> tokens = Collections.singletonList(token);
    Annotation annotation = new Annotation("Galaxy");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    assertEquals("O", token.ner());

    Files.deleteIfExists(file);
  }
@Test
  public void testUnicodeRegexPatternMatch() throws Exception {
    Path file = Files.createTempFile("regexner_unicode", ".tab");
    Files.write(file, Arrays.asList(
      "pattern\tner\toverwrite\tpriority\tgroup",
      "😀\tEMOJI\t\t1.0\t0"
    ));

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(file.toAbsolutePath().toString());

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "😀");
    token.setWord("😀");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> tokens = Collections.singletonList(token);
    Annotation annotation = new Annotation("😀");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    assertEquals("EMOJI", token.ner());

    Files.deleteIfExists(file);
  }
@Test
  public void testMatchAllTokensPosPatternBlocksMatch() throws Exception {
    Path file = Files.createTempFile("regexner_matchall", ".tab");
    Files.write(file, Arrays.asList(
      "pattern\tner\toverwrite\tpriority\tgroup",
      "Ice\\s+Cream\tFOOD\t\t1.0\t0"
    ));

    Properties props = new Properties();
    props.setProperty("tokenregexner.mapping", file.toAbsolutePath().toString());
    props.setProperty("tokenregexner.validpospattern", "JJ");
    props.setProperty("tokenregexner.posmatchtype", "MATCH_ALL_TOKENS");

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);

    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "Ice");
    t1.setWord("Ice");
    t1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "JJ");

    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "Cream");
    t2.setWord("Cream");
    t2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

    t1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    t2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> tokens = Arrays.asList(t1, t2);
    Annotation annotation = new Annotation("Ice Cream");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    assertEquals("O", t1.ner());
    assertEquals("O", t2.ner());

    Files.deleteIfExists(file);
  }
@Test
  public void testSentenceAnnotationWithCoreAnnotationsSentencesAnnotation() throws Exception {
    Path file = Files.createTempFile("regexner_sentences", ".tab");
    Files.write(file, Arrays.asList(
      "pattern\tner\toverwrite\tpriority\tgroup",
      "Tesla\tPERSON\t\t1.0\t0"
    ));

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(file.toAbsolutePath().toString());

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Tesla");
    token.setWord("Tesla");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> sentenceTokens = Collections.singletonList(token);
    CoreMap sentence = new Annotation("Tesla");
    sentence.set(CoreAnnotations.TokensAnnotation.class, sentenceTokens);

    List<CoreMap> sentences = Collections.singletonList(sentence);

    Annotation annotation = new Annotation("Tesla sentence.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    assertEquals("PERSON", token.ner());

    Files.deleteIfExists(file);
  }
@Test
  public void testRegexWithAlternationMatchesCorrectly() throws Exception {
    Path file = Files.createTempFile("regexner_alternation", ".tab");
    Files.write(file, Arrays.asList(
      "pattern\tner\toverwrite\tpriority\tgroup",
      "(Foo|Bar)\tKEYWORD\t\t1.0\t0"
    ));

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(file.toAbsolutePath().toString());

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Bar");
    token.setWord("Bar");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> tokens = Collections.singletonList(token);
    Annotation annotation = new Annotation("Bar");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    assertEquals("KEYWORD", token.ner());

    Files.deleteIfExists(file);
  }
@Test
  public void testNonMatchingPatternDoesNotAnnotateAnything() throws Exception {
    Path file = Files.createTempFile("regexner_nomatch", ".tab");
    Files.write(file, Arrays.asList(
      "pattern\tner\toverwrite\tpriority\tgroup",
      "Moon\tCELESTIAL\t\t1.0\t0"
    ));

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(file.toAbsolutePath().toString());

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Mars");
    token.setWord("Mars");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> tokens = Collections.singletonList(token);
    Annotation annotation = new Annotation("Mars");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    assertEquals("O", token.ner());

    Files.deleteIfExists(file);
  }
@Test
  public void testAnnotationOnNonNERFieldSuchAsLemma() throws Exception {
    Path file = Files.createTempFile("regexner_lemma", ".tab");
    Files.write(file, Arrays.asList(
      "pattern\tner\tLemmaAnnotation\tpriority\tgroup",
      "Running\tACTIVITY\tverb_run\t1.0\t0"
    ));

    Properties props = new Properties();
    props.setProperty("tokenregexner.mapping", file.toAbsolutePath().toString());
    props.setProperty("tokenregexner.mapping.field.LemmaAnnotation", "edu.stanford.nlp.ling.CoreAnnotations$LemmaAnnotation");

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Running");
    token.setWord("Running");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBG");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> tokens = Collections.singletonList(token);
    Annotation annotation = new Annotation("Running");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    assertEquals("verb_run", token.get(CoreAnnotations.LemmaAnnotation.class));
    assertEquals("ACTIVITY", token.ner());

    Files.deleteIfExists(file);
  }
@Test
  public void testPartialMatchAmongMultipleTokensDoesNotAnnotate() throws Exception {
    Path file = Files.createTempFile("regexner_partialspan", ".tab");
    Files.write(file, Arrays.asList(
      "pattern\tner\toverwrite\tpriority\tgroup",
      "United\\s+Kingdom\tCOUNTRY\t\t1.0\t0"
    ));

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(file.toAbsolutePath().toString());

    CoreLabel t1 = new CoreLabel();
    t1.setWord("United");
    t1.set(CoreAnnotations.TextAnnotation.class, "United");
    t1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");

    CoreLabel t2 = new CoreLabel();
    t2.setWord("States");
    t2.set(CoreAnnotations.TextAnnotation.class, "States");
    t2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");

    for (CoreLabel t : Arrays.asList(t1, t2)) {
      t.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    }

    List<CoreLabel> tokens = Arrays.asList(t1, t2);
    Annotation annotation = new Annotation("United States");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    assertEquals("O", t1.ner());
    assertEquals("O", t2.ner());

    Files.deleteIfExists(file);
  }
@Test(expected = IllegalArgumentException.class)
  public void testDuplicateHeaderFieldThrows() throws Exception {
    Path file = Files.createTempFile("regexner_dupehead", ".tab");
    Files.write(file, Arrays.asList(
      "pattern\tner\tner\tpriority\tgroup",
      "Stanford\tSCHOOL\tO\t1.0\t0"
    ));

    new TokensRegexNERAnnotator(file.toAbsolutePath().toString());

    Files.deleteIfExists(file);
  }
@Test
  public void testEmptyLineInMappingFileIsIgnored() throws Exception {
    Path file = Files.createTempFile("regexner_emptyline", ".tab");
    Files.write(file, Arrays.asList(
      "pattern\tner\toverwrite\tpriority\tgroup",
      "",
      "Cloud\tNATURE\tO\t1.0\t0"
    ));

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(file.toAbsolutePath().toString());

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Cloud");
    token.setWord("Cloud");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> tokens = Collections.singletonList(token);

    Annotation ann = new Annotation("Cloud");
    ann.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(ann);

    assertEquals("NATURE", token.ner());

    Files.deleteIfExists(file);
  }
@Test(expected = RuntimeException.class)
  public void testInvalidRegexSyntax() throws Exception {
    Path file = Files.createTempFile("regexner_badregex", ".tab");
    Files.write(file, Arrays.asList(
      "pattern\tner\toverwrite\tpriority\tgroup",
      "( [abc\tERROR\tO\t1.0\t0"
    ));

    new TokensRegexNERAnnotator(file.toAbsolutePath().toString());

    Files.deleteIfExists(file);
  }
@Test(expected = RuntimeException.class)
  public void testNegativeAnnotateGroupThrowsAtRuntime() throws Exception {
    Path file = Files.createTempFile("regexner_neggroup", ".tab");
    Files.write(file, Arrays.asList(
      "pattern\tner\toverwrite\tpriority\tgroup",
      "Elon\tPERSON\tO\t1.0\t-1"
    ));

    new TokensRegexNERAnnotator(file.toAbsolutePath().toString());

    Files.deleteIfExists(file);
  }
@Test(expected = RuntimeException.class)
  public void testAnnotateGroupLargerThanPatternGroupsThrows() throws Exception {
    Path file = Files.createTempFile("regexner_groupoutofbound", ".tab");
    Files.write(file, Arrays.asList(
      "pattern\tner\toverwrite\tpriority\tgroup",
      "( /Blue/ /Sky/ )\tCOLOR\tO\t1.0\t5"
    ));

    new TokensRegexNERAnnotator(file.toAbsolutePath().toString());

    Files.deleteIfExists(file);
  }
@Test
  public void testPatternWithExtraAnnotationFieldIgnoredIfUnset() throws Exception {
    Path file = Files.createTempFile("regexner_unsetfield", ".tab");
    Files.write(file, Arrays.asList(
      "pattern\tner\tFakeField\tpriority\tgroup",
      "Python\tLANGUAGE\tSNAKE\t1.0\t0"
    ));

    Properties props = new Properties();
    props.setProperty("tokenregexner.mapping", file.toAbsolutePath().toString());
    

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Python");
    token.setWord("Python");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> tokens = Collections.singletonList(token);

    Annotation ann = new Annotation("Python");
    ann.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(ann);

    assertEquals("LANGUAGE", token.ner());

    Files.deleteIfExists(file);
  }
@Test
  public void testMultiTokenPatternWhitespaceMismatchNoMatch() throws Exception {
    Path file = Files.createTempFile("regexner_whitespacefail", ".tab");
    Files.write(file, Arrays.asList(
      "pattern\tner\toverwrite\tpriority\tgroup",
      "New York\tCITY\tO\t1.0\t0"
    ));

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(file.toAbsolutePath().toString());

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "New_York"); 
    token.setWord("New_York");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> tokens = Collections.singletonList(token);

    Annotation ann = new Annotation("New_York");
    ann.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(ann);

    assertEquals("O", token.ner());

    Files.deleteIfExists(file);
  }
@Test
  public void testTokenWithoutTextAnnotationMatchesByWordField() throws Exception {
    Path file = Files.createTempFile("regexner_notext", ".tab");
    Files.write(file, Arrays.asList(
      "pattern\tner\toverwrite\tpriority\tgroup",
      "River\tNATURE\t\t1.0\t0"
    ));

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(file.toAbsolutePath().toString());

    CoreLabel token = new CoreLabel();
    token.setWord("River");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    

    List<CoreLabel> tokens = Collections.singletonList(token);

    Annotation ann = new Annotation("River");
    ann.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(ann);

    assertEquals("NATURE", token.ner());

    Files.deleteIfExists(file);
  }
@Test
  public void testTokenWithNoNERFieldDefaultsToNullOverwrite() throws Exception {
    Path file = Files.createTempFile("regexner_noner", ".tab");
    Files.write(file, Arrays.asList(
      "pattern\tner\tpriority\tgroup",
      "Rain\tEVENT\t1.0\t0"
    ));

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(file.toAbsolutePath().toString());

    CoreLabel token = new CoreLabel();
    token.setWord("Rain");
    token.set(CoreAnnotations.TextAnnotation.class, "Rain");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

    

    List<CoreLabel> tokens = Collections.singletonList(token);

    Annotation ann = new Annotation("Rain");
    ann.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(ann);

    assertEquals("EVENT", token.ner());

    Files.deleteIfExists(file);
  }
@Test
  public void testPriorityAndWeightAffectPatternCreation() throws Exception {
    Path file = Files.createTempFile("regexner_priority_weight", ".tab");
    Files.write(file, Arrays.asList(
      "pattern\tner\toverwrite\tpriority\tweight\tgroup",
      "Starbucks\tCAFÉ\tO\t5.0\t99.0\t0"
    ));

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(file.toAbsolutePath().toString());

    CoreLabel t = new CoreLabel();
    t.setWord("Starbucks");
    t.set(CoreAnnotations.TextAnnotation.class, "Starbucks");
    t.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    t.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> tokens = Collections.singletonList(t);

    Annotation ann = new Annotation("Starbucks store");
    ann.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(ann);

    assertEquals("CAFÉ", t.ner());

    Files.deleteIfExists(file);
  }
@Test
  public void testNoDefaultOverwriteBlockedIfNoOverwritableTypesAndLabelPresent() throws Exception {
    Path file = Files.createTempFile("regexner_nodefault_blocked", ".tab");
    Files.write(file, Arrays.asList(
      "pattern\tner\toverwrite\tpriority\tgroup",
      "Rome\tCITY\t\t1.0\t0"
    ));

    Properties props = new Properties();
    props.setProperty("tokenregexner.mapping", file.toAbsolutePath().toString());
    props.setProperty("tokenregexner.noDefaultOverwriteLabels", "CITY");

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);

    CoreLabel t = new CoreLabel();
    t.setWord("Rome");
    t.set(CoreAnnotations.TextAnnotation.class, "Rome");
    t.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    t.set(CoreAnnotations.NamedEntityTagAnnotation.class, "CITY");

    List<CoreLabel> tokens = Collections.singletonList(t);

    Annotation ann = new Annotation("Rome");
    ann.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(ann);

    assertEquals("CITY", t.ner());  

    Files.deleteIfExists(file);
  }
@Test
  public void testEmptyOverwritableTypesWithLabelThatIsWhitelisted() throws Exception {
    Path file = Files.createTempFile("regexner_empty_overwritable", ".tab");
    Files.write(file, Arrays.asList(
      "pattern\tner\toverwrite\tpriority\tgroup",
      "Venus\tPLANET\t\t1.0\t0"
    ));

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(file.toAbsolutePath().toString());

    CoreLabel t = new CoreLabel();
    t.setWord("Venus");
    t.set(CoreAnnotations.TextAnnotation.class, "Venus");
    t.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    t.set(CoreAnnotations.NamedEntityTagAnnotation.class, "MISC");

    List<CoreLabel> tokens = Collections.singletonList(t);

    Annotation ann = new Annotation("Venus");
    ann.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(ann);

    assertEquals("PLANET", t.ner());  

    Files.deleteIfExists(file);
  }
@Test(expected = IllegalArgumentException.class)
  public void testInvalidTooFewColumnsThrows() throws Exception {
    Path file = Files.createTempFile("regexner_toofewcolumns", ".tab");
    Files.write(file, Arrays.asList(
      "pattern\tner\toverwrite\tpriority\tgroup",
      "OnlyPatternField"
    ));

    new TokensRegexNERAnnotator(file.toAbsolutePath().toString());

    Files.deleteIfExists(file);
  }
@Test
  public void testPatternWithCommaButNoQuotesLoadsAsSinglePattern() throws Exception {
    Path file = Files.createTempFile("regexner_commastring", ".tab");
    Files.write(file, Arrays.asList(
      "pattern\tner\toverwrite\tpriority\tgroup",
      "Red,Blue\tCOLOR\tO\t1.0\t0"
    ));

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(file.toAbsolutePath().toString());

    CoreLabel t = new CoreLabel();
    t.setWord("Red,Blue");
    t.set(CoreAnnotations.TextAnnotation.class, "Red,Blue");
    t.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    t.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> tokens = Collections.singletonList(t);

    Annotation ann = new Annotation("Red,Blue");
    ann.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(ann);

    assertEquals("COLOR", t.ner());

    Files.deleteIfExists(file);
  }
@Test
  public void testMissingMappingPropertyFallsBackToDefault() throws Exception {
    Properties props = new Properties();
    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);
    assertNotNull(annotator);  
  }
@Test
  public void testEmptyValidPosPattern() throws Exception {
    Path file = Files.createTempFile("regexner_pos_empty", ".tab");
    Files.write(file, Arrays.asList(
      "pattern\tner\toverwrite\tpriority\tgroup",
      "Leaf\tPLANT\t\t1.0\t0"
    ));

    Properties props = new Properties();
    props.setProperty("tokenregexner.mapping", file.toAbsolutePath().toString());
    props.setProperty("tokenregexner.validpospattern", "");  

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);

    CoreLabel t = new CoreLabel();
    t.setWord("Leaf");
    t.set(CoreAnnotations.TextAnnotation.class, "Leaf");
    t.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    t.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> tokens = Collections.singletonList(t);
    Annotation ann = new Annotation("Leaf");
    ann.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(ann);

    assertEquals("PLANT", t.ner());

    Files.deleteIfExists(file);
  }
@Test
  public void testEntryWithNullTypeAllowed() throws Exception {
    Path file = Files.createTempFile("regexner_nulltype", ".tab");
    Files.write(file, Arrays.asList(
      "pattern\tner\toverwrite\tpriority\tgroup",
      "Ghost\t\t\t1.0\t0"
    ));

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(file.toAbsolutePath().toString());

    CoreLabel t = new CoreLabel();
    t.setWord("Ghost");
    t.set(CoreAnnotations.TextAnnotation.class, "Ghost");
    t.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    t.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> tokens = Collections.singletonList(t);
    Annotation ann = new Annotation("Ghost");
    ann.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(ann);

    assertNull(t.ner());  

    Files.deleteIfExists(file);
  } 
}