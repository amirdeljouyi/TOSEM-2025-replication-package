package edu.stanford.nlp.pipeline;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.process.*;
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.PropertiesUtils;
import junit.framework.TestCase;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import org.junit.Test;

import static org.junit.Assert.*;

public class TokensRegexNERAnnotator_5_GPTLLMTest {

 @Test
  public void testSimpleExactMatchAnnotation() throws Exception {
    File mappingFile = File.createTempFile("simple", ".tab");
    mappingFile.deleteOnExit();

    PrintWriter writer = new PrintWriter(mappingFile);
    writer.println("Stanford\tUNIVERSITY");
    writer.close();

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(mappingFile.getAbsolutePath());

    CoreLabel token = new CoreLabel();
    token.setWord("Stanford");
    token.set(CoreAnnotations.TextAnnotation.class, "Stanford");
    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    tokens.add(token);

    Annotation annotation = new Annotation("Stanford");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    String result = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
    assertEquals("UNIVERSITY", result);
  }
@Test
  public void testTokensRegexPatternMatch() throws Exception {
    File mappingFile = File.createTempFile("regex", ".tab");
    mappingFile.deleteOnExit();

    PrintWriter writer = new PrintWriter(mappingFile);
    writer.println("( /University/ /of/ /California/ )\tSCHOOL");
    writer.close();

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(mappingFile.getAbsolutePath());

    CoreLabel token1 = new CoreLabel();
    token1.setWord("University");
    token1.set(CoreAnnotations.TextAnnotation.class, "University");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("of");
    token2.set(CoreAnnotations.TextAnnotation.class, "of");

    CoreLabel token3 = new CoreLabel();
    token3.setWord("California");
    token3.set(CoreAnnotations.TextAnnotation.class, "California");

    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    tokens.add(token1);
    tokens.add(token2);
    tokens.add(token3);

    Annotation annotation = new Annotation("University of California");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    String ner1 = token1.get(CoreAnnotations.NamedEntityTagAnnotation.class);
    String ner2 = token2.get(CoreAnnotations.NamedEntityTagAnnotation.class);
    String ner3 = token3.get(CoreAnnotations.NamedEntityTagAnnotation.class);

    assertEquals("SCHOOL", ner1);
    assertEquals("SCHOOL", ner2);
    assertEquals("SCHOOL", ner3);
  }
@Test
  public void testNoMatch() throws Exception {
    File mappingFile = File.createTempFile("nomatch", ".tab");
    mappingFile.deleteOnExit();

    PrintWriter writer = new PrintWriter(mappingFile);
    writer.println("Harvard\tUNIVERSITY");
    writer.close();

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(mappingFile.getAbsolutePath());

    CoreLabel token = new CoreLabel();
    token.setWord("MIT");
    token.set(CoreAnnotations.TextAnnotation.class, "MIT");

    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    tokens.add(token);

    Annotation annotation = new Annotation("MIT");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    assertNull(token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }
@Test
  public void testIgnoreCaseMatching() throws Exception {
    File mappingFile = File.createTempFile("ignorecase", ".tab");
    mappingFile.deleteOnExit();

    PrintWriter writer = new PrintWriter(mappingFile);
    writer.println("harvard\tUNIVERSITY");
    writer.close();

    Properties props = new Properties();
    props.setProperty("mapping", mappingFile.getAbsolutePath());
    props.setProperty("ignorecase", "true");

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);

    CoreLabel token = new CoreLabel();
    token.setWord("Harvard");
    token.set(CoreAnnotations.TextAnnotation.class, "Harvard");

    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    tokens.add(token);

    Annotation annotation = new Annotation("Harvard");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    String ner = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
    assertEquals("UNIVERSITY", ner);
  }
@Test
  public void testOverwritableTypeMatch() throws Exception {
    File mappingFile = File.createTempFile("overwrite", ".tab");
    mappingFile.deleteOnExit();

    PrintWriter writer = new PrintWriter(mappingFile);
    writer.println("IBM\tCOMPANY\tMISC");
    writer.close();

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(mappingFile.getAbsolutePath());

    CoreLabel token = new CoreLabel();
    token.setWord("IBM");
    token.set(CoreAnnotations.TextAnnotation.class, "IBM");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "MISC");

    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    tokens.add(token);

    Annotation annotation = new Annotation("IBM");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    String ner = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
    assertEquals("COMPANY", ner);
  }
@Test
  public void testNoOverwriteDueToExistingNER() throws Exception {
    File mappingFile = File.createTempFile("nooverwrite", ".tab");
    mappingFile.deleteOnExit();

    PrintWriter writer = new PrintWriter(mappingFile);
    writer.println("Google\tCORP");
    writer.close();

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(mappingFile.getAbsolutePath());

    CoreLabel token = new CoreLabel();
    token.setWord("Google");
    token.set(CoreAnnotations.TextAnnotation.class, "Google");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORG");

    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    tokens.add(token);

    Annotation annotation = new Annotation("Google");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    String ner = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
    assertEquals("ORG", ner);
  }
@Test
  public void testInvalidPriorityThrowsException() throws Exception {
    File mappingFile = File.createTempFile("badpriority", ".tab");
    mappingFile.deleteOnExit();

    PrintWriter writer = new PrintWriter(mappingFile);
    writer.println("Stanford\tUNIVERSITY\toverwrite\tINVALID_PRIORITY");
    writer.close();

    boolean exceptionThrown = false;
    try {
      TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(mappingFile.getAbsolutePath());
    } catch (IllegalArgumentException e) {
      exceptionThrown = true;
      assertTrue(e.getMessage().contains("Invalid priority"));
    }
    assertTrue(exceptionThrown);
  }
@Test
  public void testCustomHeaderAndCustomAnnotationField() throws Exception {
    File mappingFile = File.createTempFile("custom", ".tab");
    mappingFile.deleteOnExit();

    PrintWriter writer = new PrintWriter(mappingFile);
    writer.println("pattern\torgtype");
    writer.println("Oracle\tSOFTWARECOMPANY");
    writer.close();

    Properties props = new Properties();
    props.setProperty("tokenregexner.mapping", mappingFile.getAbsolutePath());
    props.setProperty("tokenregexner.mapping.header", "pattern,orgtype");
    props.setProperty("tokenregexner.mapping.field.orgtype",
        CoreAnnotations.NamedEntityTagAnnotation.class.getName());

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);

    CoreLabel token = new CoreLabel();
    token.setWord("Oracle");
    token.set(CoreAnnotations.TextAnnotation.class, "Oracle");

    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    tokens.add(token);

    Annotation annotation = new Annotation("Oracle");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    String ner = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
    assertEquals("SOFTWARECOMPANY", ner);
  }
@Test
public void testMultipleTokensSameRegexDifferentCaseWithoutIgnoreCase() throws Exception {
  File mappingFile = File.createTempFile("multiCase", ".tab");
  mappingFile.deleteOnExit();

  PrintWriter writer = new PrintWriter(mappingFile);
  writer.println("UnItEd\tCOUNTRY");
  writer.close();

  Properties props = new Properties();
  props.setProperty("mapping", mappingFile.getAbsolutePath());
  props.setProperty("ignorecase", "false");

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);

  CoreLabel token = new CoreLabel();
  token.setWord("united");
  token.set(CoreAnnotations.TextAnnotation.class, "united");

  List<CoreLabel> tokens = new ArrayList<CoreLabel>();
  tokens.add(token);

  Annotation annotation = new Annotation("united");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  annotator.annotate(annotation);

  assertNull(token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
}
@Test
public void testWhitespaceInPatternNotSplittingRegex() throws Exception {
  File mappingFile = File.createTempFile("whitespace", ".tab");
  mappingFile.deleteOnExit();

  PrintWriter writer = new PrintWriter(mappingFile);
  writer.println("New\\ York\tCITY");
  writer.close();

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(mappingFile.getAbsolutePath());

  CoreLabel token1 = new CoreLabel();
  token1.setWord("New");
  token1.set(CoreAnnotations.TextAnnotation.class, "New");

  CoreLabel token2 = new CoreLabel();
  token2.setWord("York");
  token2.set(CoreAnnotations.TextAnnotation.class, "York");

  List<CoreLabel> tokens = new ArrayList<CoreLabel>();
  tokens.add(token1);
  tokens.add(token2);

  Annotation annotation = new Annotation("New York");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  annotator.annotate(annotation);

  assertNull(token1.get(CoreAnnotations.NamedEntityTagAnnotation.class));
  assertNull(token2.get(CoreAnnotations.NamedEntityTagAnnotation.class));
}
@Test
public void testPatternWithPriorityResolution() throws Exception {
  File mappingFile = File.createTempFile("priority", ".tab");
  mappingFile.deleteOnExit();

  PrintWriter writer = new PrintWriter(mappingFile);
  writer.println("Apple\tFRUIT\t\t1.0");
  writer.println("Apple\tCOMPANY\t\t5.0");
  writer.close();

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(mappingFile.getAbsolutePath());

  CoreLabel token = new CoreLabel();
  token.setWord("Apple");
  token.set(CoreAnnotations.TextAnnotation.class, "Apple");

  List<CoreLabel> tokens = new ArrayList<CoreLabel>();
  tokens.add(token);

  Annotation annotation = new Annotation("Apple");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  annotator.annotate(annotation);

  assertEquals("COMPANY", token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
}
@Test
public void testMatchingEmptyNERTagToken() throws Exception {
  File mappingFile = File.createTempFile("nullner", ".tab");
  mappingFile.deleteOnExit();

  PrintWriter writer = new PrintWriter(mappingFile);
  writer.println("DeepMind\tORG");
  writer.close();

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(mappingFile.getAbsolutePath());

  CoreLabel token = new CoreLabel();
  token.setWord("DeepMind");
  token.set(CoreAnnotations.TextAnnotation.class, "DeepMind");
  token.set(CoreAnnotations.NamedEntityTagAnnotation.class, null);  

  List<CoreLabel> tokens = new ArrayList<CoreLabel>();
  tokens.add(token);

  Annotation annotation = new Annotation("DeepMind");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  annotator.annotate(annotation);

  assertEquals("ORG", token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
}
@Test
public void testInvalidWeightThrowsException() throws Exception {
  File mappingFile = File.createTempFile("badweight", ".tab");
  mappingFile.deleteOnExit();

  PrintWriter writer = new PrintWriter(mappingFile);
  writer.println("Stanford\tUNIVERSITY\t\t1.0\tBAD_WEIGHT");
  writer.close();

  boolean exceptionThrown = false;
  try {
    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(mappingFile.getAbsolutePath());
  } catch (IllegalArgumentException e) {
    exceptionThrown = true;
    assertTrue(e.getMessage().contains("Invalid group"));
  }
  assertTrue(exceptionThrown);
}
@Test
public void testDefaultOverwriteBackgroundSymbol() throws Exception {
  File mappingFile = File.createTempFile("background", ".tab");
  mappingFile.deleteOnExit();

  PrintWriter writer = new PrintWriter(mappingFile);
  writer.println("Tokyo\tCITY");
  writer.close();

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(mappingFile.getAbsolutePath());

  CoreLabel token = new CoreLabel();
  token.setWord("Tokyo");
  token.set(CoreAnnotations.TextAnnotation.class, "Tokyo");
  token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O"); 

  List<CoreLabel> tokens = new ArrayList<CoreLabel>();
  tokens.add(token);

  Annotation annotation = new Annotation("Tokyo");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  annotator.annotate(annotation);

  assertEquals("CITY", token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
}
@Test
public void testHeaderMismatchThrowsException() throws Exception {
  File mappingFile = File.createTempFile("badheader", ".tab");
  mappingFile.deleteOnExit();

  PrintWriter writer = new PrintWriter(mappingFile);
  writer.println("pattern\tentity");
  writer.println("Tesla\tORG");
  writer.close();

  Properties props = new Properties();
  props.setProperty("tokenregexner.mapping", mappingFile.getAbsolutePath());
  props.setProperty("tokenregexner.mapping.header", "pattern,type");

  boolean exceptionThrown = false;
  try {
    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);
  } catch (IllegalArgumentException e) {
    exceptionThrown = true;
    assertTrue(e.getMessage().contains("Header does not contain annotation field"));
  }

  assertTrue(exceptionThrown);
}
@Test
public void testEmptyMappingFileShouldNotFail() throws Exception {
  File mappingFile = File.createTempFile("empty", ".tab");
  mappingFile.deleteOnExit();

  PrintWriter writer = new PrintWriter(mappingFile);
  writer.close(); 

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(mappingFile.getAbsolutePath());

  CoreLabel token = new CoreLabel();
  token.setWord("Nothing");
  token.set(CoreAnnotations.TextAnnotation.class, "Nothing");

  List<CoreLabel> tokens = new ArrayList<CoreLabel>();
  tokens.add(token);

  Annotation annotation = new Annotation("Nothing");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  annotator.annotate(annotation);

  assertNull(token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
}
@Test
public void testGroupOutOfBoundsThrowsException() throws Exception {
  File mappingFile = File.createTempFile("groupOutOfBounds", ".tab");
  mappingFile.deleteOnExit();

  PrintWriter writer = new PrintWriter(mappingFile);
  
  writer.println("Apple\tORG\t\t1.0\t3");
  writer.close();

  boolean exceptionThrown = false;
  try {
    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(mappingFile.getAbsolutePath());
  } catch (RuntimeException e) {
    exceptionThrown = true;
    assertTrue(e.getMessage().contains("Invalid match group"));
  }

  assertTrue(exceptionThrown);
}
@Test
public void testRepeatedIdenticalRegexWithSamePriorityShouldNotDuplicate() throws Exception {
  File mappingFile = File.createTempFile("duplicates", ".tab");
  mappingFile.deleteOnExit();

  PrintWriter writer = new PrintWriter(mappingFile);
  writer.println("Amazon\tORG\t\t1.0");
  writer.println("Amazon\tORG\t\t1.0");
  writer.close();

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(mappingFile.getAbsolutePath());

  CoreLabel token = new CoreLabel();
  token.setWord("Amazon");
  token.set(CoreAnnotations.TextAnnotation.class, "Amazon");

  List<CoreLabel> tokens = new ArrayList<CoreLabel>();
  tokens.add(token);

  Annotation annotation = new Annotation("Amazon");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  annotator.annotate(annotation);

  assertEquals("ORG", token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
}
@Test
public void testCommaDelimitedTypesWarningStripsToFirst() throws Exception {
  File mappingFile = File.createTempFile("commatypes", ".tab");
  mappingFile.deleteOnExit();

  PrintWriter writer = new PrintWriter(mappingFile);
  writer.println("Tesla\tCAR,ORG");
  writer.close();

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(mappingFile.getAbsolutePath());

  CoreLabel token = new CoreLabel();
  token.setWord("Tesla");
  token.set(CoreAnnotations.TextAnnotation.class, "Tesla");

  List<CoreLabel> tokens = new ArrayList<CoreLabel>();
  tokens.add(token);

  Annotation annotation = new Annotation("Tesla");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  annotator.annotate(annotation);

  assertEquals("CAR", token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
}
@Test
public void testMixedCaseOverrideWithBackgroundMISCMatches() throws Exception {
  File mappingFile = File.createTempFile("miscOverride", ".tab");
  mappingFile.deleteOnExit();

  PrintWriter writer = new PrintWriter(mappingFile);
  writer.println("OpenAI\tLAB");
  writer.close();

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(mappingFile.getAbsolutePath());

  CoreLabel token = new CoreLabel();
  token.setWord("OpenAI");
  token.set(CoreAnnotations.TextAnnotation.class, "OpenAI");
  token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "MISC");

  List<CoreLabel> tokens = new ArrayList<CoreLabel>();
  tokens.add(token);

  Annotation annotation = new Annotation("OpenAI");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  annotator.annotate(annotation);

  assertEquals("LAB", token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
}
@Test
public void testTokensRegexAnnotationWithPOSValidMatch_AllTokens() throws Exception {
  File mappingFile = File.createTempFile("posmatch_all", ".tab");
  mappingFile.deleteOnExit();

  PrintWriter writer = new PrintWriter(mappingFile);
  writer.println("( /machine/ /learning/ )\tFIELD");
  writer.close();

  Properties props = new Properties();
  props.setProperty("mapping", mappingFile.getAbsolutePath());
  props.setProperty("validpospattern", "NN|VBG");
  props.setProperty("posmatchtype", "MATCH_ALL_TOKENS");

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);

  CoreLabel token1 = new CoreLabel();
  token1.setWord("machine");
  token1.set(CoreAnnotations.TextAnnotation.class, "machine");
  token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

  CoreLabel token2 = new CoreLabel();
  token2.setWord("learning");
  token2.set(CoreAnnotations.TextAnnotation.class, "learning");
  token2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBG");

  List<CoreLabel> tokens = new ArrayList<CoreLabel>();
  tokens.add(token1);
  tokens.add(token2);

  Annotation annotation = new Annotation("machine learning");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  annotator.annotate(annotation);

  assertEquals("FIELD", token1.get(CoreAnnotations.NamedEntityTagAnnotation.class));
  assertEquals("FIELD", token2.get(CoreAnnotations.NamedEntityTagAnnotation.class));
}
@Test
public void testOverlappingNERBlockIsNotOverwritten() throws Exception {
  File mappingFile = File.createTempFile("overlap", ".tab");
  mappingFile.deleteOnExit();

  PrintWriter writer = new PrintWriter(mappingFile);
  writer.println("( /ABC/ )\tORG");
  writer.close();

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(mappingFile.getAbsolutePath());

  CoreLabel token1 = new CoreLabel();
  token1.setWord("The");
  token1.set(CoreAnnotations.TextAnnotation.class, "The");
  token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORG");

  CoreLabel token2 = new CoreLabel();
  token2.setWord("ABC");
  token2.set(CoreAnnotations.TextAnnotation.class, "ABC");
  token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORG");

  CoreLabel token3 = new CoreLabel();
  token3.setWord("Corp");
  token3.set(CoreAnnotations.TextAnnotation.class, "Corp");
  token3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORG");

  List<CoreLabel> tokens = new ArrayList<CoreLabel>();
  tokens.add(token1);
  tokens.add(token2);
  tokens.add(token3);

  Annotation annotation = new Annotation("The ABC Corp");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  annotator.annotate(annotation);

  assertEquals("ORG", token2.get(CoreAnnotations.NamedEntityTagAnnotation.class)); 
}
@Test
public void testInvalidHeaderDuplicateFieldsThrowsException() throws Exception {
  File mappingFile = File.createTempFile("duplicateHeader", ".tab");
  mappingFile.deleteOnExit();

  PrintWriter writer = new PrintWriter(mappingFile);
  writer.println("pattern\tner\tpattern");
  writer.println("IBM\tCOMPANY\tOther");
  writer.close();

  Properties props = new Properties();
  props.setProperty("tokenregexner.mapping", mappingFile.getAbsolutePath());
  props.setProperty("tokenregexner.mapping.header", "pattern,ner,pattern");

  boolean exceptionThrown = false;
  try {
    new TokensRegexNERAnnotator("tokenregexner", props);
  } catch (IllegalArgumentException e) {
    exceptionThrown = true;
    assertTrue(e.getMessage().contains("Duplicate header field"));
  }

  assertTrue(exceptionThrown);
}
@Test
public void testOverwriteNullNERValueSucceeds() throws Exception {
  File mappingFile = File.createTempFile("nullNER", ".tab");
  mappingFile.deleteOnExit();

  PrintWriter writer = new PrintWriter(mappingFile);
  writer.println("DeepMind\tORG");
  writer.close();

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(mappingFile.getAbsolutePath());

  CoreLabel token = new CoreLabel();
  token.setWord("DeepMind");
  token.set(CoreAnnotations.TextAnnotation.class, "DeepMind");
  token.set(CoreAnnotations.NamedEntityTagAnnotation.class, null);

  List<CoreLabel> tokens = new ArrayList<CoreLabel>();
  tokens.add(token);

  Annotation annotation = new Annotation("DeepMind");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  annotator.annotate(annotation);

  assertEquals("ORG", token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
}
@Test
public void testMatchOneTokenWithPosMismatchShouldNotTag() throws Exception {
  File mappingFile = File.createTempFile("posMismatch", ".tab");
  mappingFile.deleteOnExit();

  PrintWriter writer = new PrintWriter(mappingFile);
  writer.println("( /help/ )\tACTION");
  writer.close();

  Properties props = new Properties();
  props.setProperty("mapping", mappingFile.getAbsolutePath());
  props.setProperty("validpospattern", "NNP");
  props.setProperty("posmatchtype", "MATCH_ONE_TOKEN_PHRASE_ONLY");

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("ner", props);

  CoreLabel token = new CoreLabel();
  token.setWord("help");
  token.set(CoreAnnotations.TextAnnotation.class, "help");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");

  List<CoreLabel> tokens = new ArrayList<CoreLabel>();
  tokens.add(token);

  Annotation annotation = new Annotation("help");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  annotator.annotate(annotation);

  assertNull(token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
}
@Test
public void testCommonWordsNotAnnotated() throws Exception {
  File mappingFile = File.createTempFile("recognition", ".tab");
  mappingFile.deleteOnExit();

  File commonWordsFile = File.createTempFile("commonwords", ".txt");
  commonWordsFile.deleteOnExit();

  PrintWriter writer = new PrintWriter(mappingFile);
  writer.println("Stanford\tUNIVERSITY");
  writer.close();

  PrintWriter commonWriter = new PrintWriter(commonWordsFile);
  commonWriter.println("Stanford");
  commonWriter.close();

  Properties props = new Properties();
  props.setProperty("mapping", mappingFile.getAbsolutePath());
  props.setProperty("commonWords", commonWordsFile.getAbsolutePath());
  props.setProperty("verbose", "true");

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("ner", props);

  CoreLabel token = new CoreLabel();
  token.setWord("Stanford");
  token.set(CoreAnnotations.TextAnnotation.class, "Stanford");

  List<CoreLabel> tokens = new ArrayList<CoreLabel>();
  tokens.add(token);

  Annotation annotation = new Annotation("Stanford");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  annotator.annotate(annotation);

  assertNull(token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
}
@Test
public void testNERAnnotationSkippedWhenCuttingAcrossNERBoundaries() throws Exception {
  File mappingFile = File.createTempFile("skipBoundary", ".tab");
  mappingFile.deleteOnExit();

  PrintWriter writer = new PrintWriter(mappingFile);
  writer.println("( /OpenAI/ /GPT/ )\tORG");
  writer.close();

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(mappingFile.getAbsolutePath());

  CoreLabel token1 = new CoreLabel();
  token1.setWord("OpenAI");
  token1.set(CoreAnnotations.TextAnnotation.class, "OpenAI");
  token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORG");

  CoreLabel token2 = new CoreLabel();
  token2.setWord("GPT");
  token2.set(CoreAnnotations.TextAnnotation.class, "GPT");
  token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "MISC");

  List<CoreLabel> tokens = new ArrayList<CoreLabel>();
  tokens.add(token1);
  tokens.add(token2);

  Annotation annotation = new Annotation("OpenAI GPT");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  annotator.annotate(annotation);

  assertEquals("ORG", token1.get(CoreAnnotations.NamedEntityTagAnnotation.class));
  assertEquals("MISC", token2.get(CoreAnnotations.NamedEntityTagAnnotation.class)); 
}
@Test
public void testNoDefaultOverwriteLabelsOptionalOverwriteFailure() throws Exception {
  File mappingFile = File.createTempFile("noOverwriteTypes", ".tab");
  mappingFile.deleteOnExit();

  PrintWriter writer = new PrintWriter(mappingFile);
  writer.println("Paris\tCITY");
  writer.close();

  Properties props = new Properties();
  props.setProperty("mapping", mappingFile.getAbsolutePath());
  props.setProperty("noDefaultOverwriteLabels", "CITY");

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("ner", props);

  CoreLabel token = new CoreLabel();
  token.setWord("Paris");
  token.set(CoreAnnotations.TextAnnotation.class, "Paris");
  token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "GPE");

  List<CoreLabel> tokens = new ArrayList<CoreLabel>();
  tokens.add(token);

  Annotation annotation = new Annotation("Paris");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  annotator.annotate(annotation);

  assertEquals("GPE", token.get(CoreAnnotations.NamedEntityTagAnnotation.class)); 
}
@Test
public void testPerFileIgnoreCaseOverridesGlobalFalse() throws Exception {
  File mappingFile = File.createTempFile("splitconfig", ".tab");
  mappingFile.deleteOnExit();

  PrintWriter writer = new PrintWriter(mappingFile);
  writer.println("tesla\tAUTO");
  writer.close();

  String mappingValue = "ignorecase=true, " + mappingFile.getAbsolutePath();

  Properties props = new Properties();
  props.setProperty("mapping", mappingValue);
  props.setProperty("ignorecase", "false");

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("ner", props);

  CoreLabel token = new CoreLabel();
  token.setWord("Tesla");
  token.set(CoreAnnotations.TextAnnotation.class, "Tesla");

  List<CoreLabel> tokens = new ArrayList<CoreLabel>();
  tokens.add(token);

  Annotation annotation = new Annotation("Tesla");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  annotator.annotate(annotation);

  assertEquals("AUTO", token.get(CoreAnnotations.NamedEntityTagAnnotation.class)); 
}
@Test
public void testTokensRegexWithWeightParsing() throws Exception {
  File ruleFile = File.createTempFile("testWeight", ".tab");
  ruleFile.deleteOnExit();

  PrintWriter pw = new PrintWriter(ruleFile);
  pw.println("( /Google/ )\tORG\t\t1.0\t0.75");
  pw.close();

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(ruleFile.getAbsolutePath());

  CoreLabel token = new CoreLabel();
  token.setWord("Google");
  token.set(CoreAnnotations.TextAnnotation.class, "Google");

  List<CoreLabel> tokens = new ArrayList<CoreLabel>();
  tokens.add(token);

  Annotation annotation = new Annotation("Google");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  annotator.annotate(annotation);

  assertEquals("ORG", token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
}
@Test
public void testPatternHeaderMissingThrowsException() throws Exception {
  File ruleFile = File.createTempFile("badHeader", ".tab");
  ruleFile.deleteOnExit();

  PrintWriter pw = new PrintWriter(ruleFile);
  pw.println("name\ttype");
  pw.println("Amazon\tORG");
  pw.close();

  Properties props = new Properties();
  props.setProperty("mapping", ruleFile.getAbsolutePath());
  props.setProperty("mapping.header", "name,type");

  boolean failed = false;
  try {
    new TokensRegexNERAnnotator("ner", props);
  } catch (IllegalArgumentException e) {
    failed = true;
    assertTrue(e.getMessage().contains("Header does not contain 'pattern'"));
  }

  assertTrue(failed);
}
@Test
public void testNullPosAnnotationSkipsMatchWithPosPattern() throws Exception {
  File ruleFile = File.createTempFile("posNull", ".tab");
  ruleFile.deleteOnExit();

  PrintWriter pw = new PrintWriter(ruleFile);
  pw.println("( /AI/ )\tTECH");
  pw.close();

  Properties props = new Properties();
  props.setProperty("mapping", ruleFile.getAbsolutePath());
  props.setProperty("validpospattern", "NN|NNS");
  props.setProperty("posmatchtype", "MATCH_AT_LEAST_ONE_TOKEN");

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("ner", props);

  CoreLabel token = new CoreLabel();
  token.setWord("AI");
  token.set(CoreAnnotations.TextAnnotation.class, "AI");
  

  List<CoreLabel> tokens = new ArrayList<CoreLabel>();
  tokens.add(token);

  Annotation annotation = new Annotation("AI");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  annotator.annotate(annotation);

  assertNull(token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
}
@Test
public void testUnknownAnnotationClassFailsEarly() throws Exception {
  File ruleFile = File.createTempFile("badAnnotationClass", ".tab");
  ruleFile.deleteOnExit();

  PrintWriter pw = new PrintWriter(ruleFile);
  pw.println("pattern\tzzztype");
  pw.println("Apple\tFRUIT");
  pw.close();

  Properties props = new Properties();
  props.setProperty("mapping", ruleFile.getAbsolutePath());
  props.setProperty("mapping.header", "pattern,zzztype");
  props.setProperty("mapping.field.zzztype", "some.unknown.Type");

  boolean exceptionThrown = false;
  try {
    new TokensRegexNERAnnotator("ner", props);
  } catch (RuntimeException e) {
    exceptionThrown = true;
    assertTrue(e.getMessage().contains("Not recognized annotation class field"));
  }

  assertTrue(exceptionThrown);
}
@Test
public void testSemicolonDelimitedMultiMapping() throws Exception {
  File file1 = File.createTempFile("multiMap1", ".tab");
  file1.deleteOnExit();
  PrintWriter pw1 = new PrintWriter(file1);
  pw1.println("OpenAI\tORG");
  pw1.close();

  File file2 = File.createTempFile("multiMap2", ".tab");
  file2.deleteOnExit();
  PrintWriter pw2 = new PrintWriter(file2);
  pw2.println("Stanford\tUNIVERSITY");
  pw2.close();

  String combinedMapping = file1.getAbsolutePath() + ";" + file2.getAbsolutePath();
  Properties props = new Properties();
  props.setProperty("mapping", combinedMapping);

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("ner", props);

  CoreLabel token1 = new CoreLabel();
  token1.setWord("Stanford");
  token1.set(CoreAnnotations.TextAnnotation.class, "Stanford");

  List<CoreLabel> tokens = new ArrayList<CoreLabel>();
  tokens.add(token1);

  Annotation annotation = new Annotation("Stanford");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  annotator.annotate(annotation);

  assertEquals("UNIVERSITY", token1.get(CoreAnnotations.NamedEntityTagAnnotation.class));
}
@Test
public void testReadHeaderTrueAutoloadFromFile() throws Exception {
  File ruleFile = File.createTempFile("headerTrue", ".tab");
  ruleFile.deleteOnExit();

  PrintWriter writer = new PrintWriter(ruleFile);
  writer.println("pattern	type");
  writer.println("NVIDIA	COMPANY");
  writer.close();

  Properties props = new Properties();
  props.setProperty("mapping", "header=true, " + ruleFile.getAbsolutePath());

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);

  CoreLabel token = new CoreLabel();
  token.setWord("NVIDIA");
  token.set(CoreAnnotations.TextAnnotation.class, "NVIDIA");

  List<CoreLabel> tokens = new ArrayList<CoreLabel>();
  tokens.add(token);

  Annotation annotation = new Annotation("NVIDIA");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  annotator.annotate(annotation);
  assertEquals("COMPANY", token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
}
@Test
public void testInvalidOverwriteTypeConfigurationIgnored() throws Exception {
  File ruleFile = File.createTempFile("badOverwrite", ".tab");
  ruleFile.deleteOnExit();

  PrintWriter pw = new PrintWriter(ruleFile);
  pw.println("Meta\tORG\tUNKNOWN_LABEL");
  pw.close();

  Properties props = new Properties();
  props.setProperty("mapping", ruleFile.getAbsolutePath());

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("ner", props);

  CoreLabel token = new CoreLabel();
  token.setWord("Meta");
  token.set(CoreAnnotations.TextAnnotation.class, "Meta");
  token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

  List<CoreLabel> tokens = new ArrayList<CoreLabel>();
  tokens.add(token);

  Annotation annotation = new Annotation("Meta");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  annotator.annotate(annotation);

  assertEquals("PERSON", token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
}
@Test
public void testOverlappingDifferentNERTagsTriggersOverwrite() throws Exception {
  File ruleFile = File.createTempFile("overwriteInconsistent", ".tab");
  ruleFile.deleteOnExit();

  PrintWriter pw = new PrintWriter(ruleFile);
  pw.println("( /Apple/ /Inc/ )\tCORP");
  pw.close();

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(ruleFile.getAbsolutePath());

  CoreLabel token1 = new CoreLabel();
  token1.setWord("Apple");
  token1.set(CoreAnnotations.TextAnnotation.class, "Apple");
  token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORG");

  CoreLabel token2 = new CoreLabel();
  token2.setWord("Inc");
  token2.set(CoreAnnotations.TextAnnotation.class, "Inc");
  token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "MISC");

  List<CoreLabel> tokens = new ArrayList<CoreLabel>();
  tokens.add(token1);
  tokens.add(token2);

  Annotation annotation = new Annotation("Apple Inc");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  annotator.annotate(annotation);

  assertEquals("CORP", token1.get(CoreAnnotations.NamedEntityTagAnnotation.class));
  assertEquals("CORP", token2.get(CoreAnnotations.NamedEntityTagAnnotation.class));
}
@Test
public void testUnknownMappingFieldWithoutClassDefinedWarningSuppressed() throws Exception {
  File file = File.createTempFile("unknownFieldNoClass", ".tab");
  file.deleteOnExit();

  PrintWriter writer = new PrintWriter(file);
  writer.println("pattern\tMYTYPE");
  writer.println("Alpha\tFOO");
  writer.close();

  Properties props = new Properties();
  props.setProperty("mapping", file.getAbsolutePath());
  props.setProperty("mapping.header", "pattern,MYTYPE");

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("ner", props);

  CoreLabel token = new CoreLabel();
  token.setWord("Alpha");
  token.set(CoreAnnotations.TextAnnotation.class, "Alpha");

  List<CoreLabel> tokens = new ArrayList<CoreLabel>();
  tokens.add(token);

  Annotation annotation = new Annotation("Alpha");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  annotator.annotate(annotation);

  assertNull(token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
}
@Test
public void testOverwriteFailsWhenOnlyNonOverwritableAndNoMatchToMyLabels() throws Exception {
  File ruleFile = File.createTempFile("strict-non-overwrite", ".tab");
  ruleFile.deleteOnExit();

  PrintWriter pw = new PrintWriter(ruleFile);
  pw.println("Beijing\tCITY");
  pw.close();

  Properties props = new Properties();
  props.setProperty("mapping", ruleFile.getAbsolutePath());
  props.setProperty("noDefaultOverwriteLabels", "CITY");

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("ner", props);

  CoreLabel token = new CoreLabel();
  token.setWord("Beijing");
  token.set(CoreAnnotations.TextAnnotation.class, "Beijing");
  token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "COUNTRY"); 

  List<CoreLabel> tokens = new ArrayList<CoreLabel>();
  tokens.add(token);

  Annotation annotation = new Annotation("Beijing");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  annotator.annotate(annotation);

  assertEquals("COUNTRY", token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
}
@Test
public void testNullRegexFailsParsingGracefully() throws Exception {
  File file = File.createTempFile("null-regex", ".tab");
  file.deleteOnExit();

  PrintWriter pw = new PrintWriter(file);
  pw.println("\tORG");
  pw.close();

  boolean thrown = false;
  try {
    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(file.getAbsolutePath());
  } catch (IllegalArgumentException e) {
    thrown = true;
    assertTrue(e.getMessage().contains("tab-separated columns"));
  }

  assertTrue(thrown);
}
@Test
public void testMatchGroupZeroUsedWhenGroupFieldAbsent() throws Exception {
  File ruleFile = File.createTempFile("group-default", ".tab");
  ruleFile.deleteOnExit();

  PrintWriter writer = new PrintWriter(ruleFile);
  writer.println("Tesla\tCAR");
  writer.close();

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(ruleFile.getAbsolutePath());

  CoreLabel token = new CoreLabel();
  token.setWord("Tesla");
  token.set(CoreAnnotations.TextAnnotation.class, "Tesla");

  List<CoreLabel> tokens = new ArrayList<CoreLabel>();
  tokens.add(token);

  Annotation annotation = new Annotation("Tesla");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  annotator.annotate(annotation);

  assertEquals("CAR", token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
}
@Test
public void testMultipleRegexWithOnlyOneMatchingLongestChosen() throws Exception {
  File ruleFile = File.createTempFile("longest-match", ".tab");
  ruleFile.deleteOnExit();

  PrintWriter writer = new PrintWriter(ruleFile);
  writer.println("Stanford\tPLACE\t\t1.0");
  writer.println("Stanford University\tSCHOOL\t\t1.0");
  writer.close();

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(ruleFile.getAbsolutePath());

  CoreLabel token1 = new CoreLabel();
  token1.setWord("Stanford");
  token1.set(CoreAnnotations.TextAnnotation.class, "Stanford");

  CoreLabel token2 = new CoreLabel();
  token2.setWord("University");
  token2.set(CoreAnnotations.TextAnnotation.class, "University");

  List<CoreLabel> tokens = new ArrayList<CoreLabel>();
  tokens.add(token1);
  tokens.add(token2);

  Annotation annotation = new Annotation("Stanford University");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  annotator.annotate(annotation);

  assertEquals("SCHOOL", token1.get(CoreAnnotations.NamedEntityTagAnnotation.class));
  assertEquals("SCHOOL", token2.get(CoreAnnotations.NamedEntityTagAnnotation.class));
}
@Test
public void testRegexIsIgnoredIfItsAnExactDuplicateWithLowerPriority() throws Exception {
  File file = File.createTempFile("duplicateLowerPriority", ".tab");
  file.deleteOnExit();

  PrintWriter pw = new PrintWriter(file);
  pw.println("IBM\tORG\t\t2.0");
  pw.println("IBM\tORG\t\t1.0"); 
  pw.close();

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(file.getAbsolutePath());

  CoreLabel token = new CoreLabel();
  token.setWord("IBM");
  token.set(CoreAnnotations.TextAnnotation.class, "IBM");

  List<CoreLabel> tokens = new ArrayList<CoreLabel>();
  tokens.add(token);

  Annotation annotation = new Annotation("IBM");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  annotator.annotate(annotation);

  assertEquals("ORG", token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
}
@Test
public void testCommonWordMatchIsBlockedEvenWithHighPriority() throws Exception {
  File mappingFile = File.createTempFile("common-block", ".tab");
  mappingFile.deleteOnExit();

  File commonWords = File.createTempFile("mycommontxt", ".txt");
  commonWords.deleteOnExit();

  PrintWriter matcher = new PrintWriter(mappingFile);
  matcher.println("AI\tTOPIC\t\t99.0");
  matcher.close();

  PrintWriter comm = new PrintWriter(commonWords);
  comm.println("AI");
  comm.close();

  Properties props = new Properties();
  props.setProperty("mapping", mappingFile.getAbsolutePath());
  props.setProperty("commonWords", commonWords.getAbsolutePath());

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("ner", props);

  CoreLabel token = new CoreLabel();
  token.setWord("AI");
  token.set(CoreAnnotations.TextAnnotation.class, "AI");

  List<CoreLabel> tokens = new ArrayList<CoreLabel>();
  tokens.add(token);

  Annotation annotation = new Annotation("AI");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  annotator.annotate(annotation);

  assertNull(token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
}
@Test
public void testTokensRegexWithNegativePriorityStillAppliesIfBestMatch() throws Exception {
  File mappingFile = File.createTempFile("negPriority", ".tab");
  mappingFile.deleteOnExit();

  PrintWriter writer = new PrintWriter(mappingFile);
  writer.println("Google\tORG\t\t-1.0");

  writer.close();

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(mappingFile.getAbsolutePath());

  CoreLabel token = new CoreLabel();
  token.setWord("Google");
  token.set(CoreAnnotations.TextAnnotation.class, "Google");

  List<CoreLabel> tokens = new ArrayList<CoreLabel>();
  tokens.add(token);

  Annotation annotation = new Annotation("Google");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  annotator.annotate(annotation);

  assertEquals("ORG", token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
}
@Test
public void testInvalidGroupThrowsExceptionDuringAnnotationSetup() throws Exception {
  File mappingFile = File.createTempFile("badGroup", ".tab");
  mappingFile.deleteOnExit();

  PrintWriter writer = new PrintWriter(mappingFile);
  
  writer.println("( /Tesla/ )\tCAR\t\t1.0\t1.0\t99");
  writer.close();

  boolean exceptionThrown = false;

  try {
    new TokensRegexNERAnnotator(mappingFile.getAbsolutePath());
  } catch (RuntimeException e) {
    exceptionThrown = true;
    assertTrue(e.getMessage().contains("Invalid match group"));
  }

  assertTrue(exceptionThrown);
}
@Test
public void testEmptyOverwriteColumnIsIgnoredProperly() throws Exception {
  File mappingFile = File.createTempFile("emptyOverwrite", ".tab");
  mappingFile.deleteOnExit();

  PrintWriter writer = new PrintWriter(mappingFile);
  writer.println("Apple\tCOMPANY\t\t0.5");
  writer.close();

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(mappingFile.getAbsolutePath());

  CoreLabel token = new CoreLabel();
  token.setWord("Apple");
  token.set(CoreAnnotations.TextAnnotation.class, "Apple");

  List<CoreLabel> tokens = new ArrayList<CoreLabel>();
  tokens.add(token);

  Annotation annotation = new Annotation("Apple");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  annotator.annotate(annotation);

  assertEquals("COMPANY", token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
}
@Test
public void testMultipleEntriesWithDifferentFieldsSamePatternUsesLastAdded() throws Exception {
  File mappingFile = File.createTempFile("samePatternDiffFields", ".tab");
  mappingFile.deleteOnExit();

  PrintWriter writer = new PrintWriter(mappingFile);
  writer.println("Tesla\tCOMPANY\t\t2.0");
  writer.println("Tesla\tCAR\t\t5.0");
  writer.close();

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(mappingFile.getAbsolutePath());

  CoreLabel token = new CoreLabel();
  token.setWord("Tesla");
  token.set(CoreAnnotations.TextAnnotation.class, "Tesla");

  List<CoreLabel> tokens = new ArrayList<CoreLabel>();
  tokens.add(token);

  Annotation annotation = new Annotation("Tesla");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  annotator.annotate(annotation);

  assertEquals("CAR", token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
}
@Test
public void testEmptyLineInMappingFileIsIgnored() throws Exception {
  File mappingFile = File.createTempFile("emptylines", ".tab");
  mappingFile.deleteOnExit();

  PrintWriter writer = new PrintWriter(mappingFile);
  writer.println("Tesla\tCOMPANY");
  writer.println("");
  writer.println("\t");
  writer.close();

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(mappingFile.getAbsolutePath());

  CoreLabel token = new CoreLabel();
  token.setWord("Tesla");
  token.set(CoreAnnotations.TextAnnotation.class, "Tesla");

  List<CoreLabel> tokens = new ArrayList<CoreLabel>();
  tokens.add(token);

  Annotation annotation = new Annotation("Tesla");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  annotator.annotate(annotation);

  assertEquals("COMPANY", token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
}
@Test
public void testPosMatchTypeMatchOneTokenPhraseOnly_WithNonOneTokenPhraseShouldSkipPOSCheck() throws Exception {
  File mappingFile = File.createTempFile("matchPOSOneTokenPhraseOnly", ".tab");
  mappingFile.deleteOnExit();

  PrintWriter writer = new PrintWriter(mappingFile);
  writer.println("New York\tCITY");
  writer.close();

  Properties props = new Properties();
  props.setProperty("mapping", mappingFile.getAbsolutePath());
  props.setProperty("validpospattern", "NNP");
  props.setProperty("posmatchtype", "MATCH_ONE_TOKEN_PHRASE_ONLY");

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("ner", props);

  CoreLabel token1 = new CoreLabel();
  token1.setWord("New");
  token1.set(CoreAnnotations.TextAnnotation.class, "New");
  token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "JJ");

  CoreLabel token2 = new CoreLabel();
  token2.setWord("York");
  token2.set(CoreAnnotations.TextAnnotation.class, "York");
  token2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

  List<CoreLabel> tokens = new ArrayList<CoreLabel>();
  tokens.add(token1);
  tokens.add(token2);

  Annotation annotation = new Annotation("New York");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  annotator.annotate(annotation);

  assertEquals("CITY", token1.get(CoreAnnotations.NamedEntityTagAnnotation.class));
  assertEquals("CITY", token2.get(CoreAnnotations.NamedEntityTagAnnotation.class));
}
@Test
public void testTokenWithoutNERTagAndWithoutPOSTagIsStillAnnotatedWithoutChecks() throws Exception {
  File mappingFile = File.createTempFile("bareToken", ".tab");
  mappingFile.deleteOnExit();

  PrintWriter writer = new PrintWriter(mappingFile);
  writer.println("Saturn\tPLANET");
  writer.close();

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(mappingFile.getAbsolutePath());

  CoreLabel token = new CoreLabel();
  token.setWord("Saturn");
  token.set(CoreAnnotations.TextAnnotation.class, "Saturn");
  

  List<CoreLabel> tokens = new ArrayList<CoreLabel>();
  tokens.add(token);

  Annotation annotation = new Annotation("Saturn");
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  annotator.annotate(annotation);

  assertEquals("PLANET", token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
}
@Test
public void testInvalidNumberOfColumnsTooFewThrowsException() throws Exception {
  File mappingFile = File.createTempFile("incomplete", ".tab");
  mappingFile.deleteOnExit();

  PrintWriter writer = new PrintWriter(mappingFile);
  writer.println("OpenAI"); 
  writer.close();

  boolean thrown = false;
  try {
    new TokensRegexNERAnnotator(mappingFile.getAbsolutePath());
  } catch (IllegalArgumentException e) {
    thrown = true;
    assertTrue(e.getMessage().contains("too few tab-separated columns"));
  }

  assertTrue(thrown);
} 
}