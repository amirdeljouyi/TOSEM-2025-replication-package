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

public class TokensRegexNERAnnotator_4_GPTLLMTest {

 @Test
  public void testBasicNonMatchingAnnotation() throws Exception {
    String mappingPath = "target/test_mapping_1.tab";
    PrintWriter writer = new PrintWriter(new FileWriter(mappingPath));
    writer.println("pattern\tner\toverwrite\tpriority\tgroup");
    writer.println("Stanford University\tSCHOOL\tO,MISC\t1.0\t0");
    writer.close();

    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token0 = new CoreLabel();
    token0.setWord("I");
    token0.setValue("I");
    token0.set(CoreAnnotations.TextAnnotation.class, "I");
    token0.set(CoreAnnotations.PartOfSpeechAnnotation.class, "PRP");
    token0.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    tokens.add(token0);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("went");
    token1.setValue("went");
    token1.set(CoreAnnotations.TextAnnotation.class, "went");
    token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBD");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    tokens.add(token1);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("home");
    token2.setValue("home");
    token2.set(CoreAnnotations.TextAnnotation.class, "home");
    token2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    tokens.add(token2);

    CoreMap sentence = new Annotation("I went home.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation ann = new Annotation("I went home.");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    ann.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Properties props = new Properties();
    props.setProperty("tokenregexner.mapping", mappingPath);
    props.setProperty("tokenregexner.ignorecase", "false");

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);
    annotator.annotate(ann);

    assertEquals("O", tokens.get(0).ner());
    assertEquals("O", tokens.get(1).ner());
    assertEquals("O", tokens.get(2).ner());
  }
@Test
  public void testSimpleMatchAnnotation() throws Exception {
    String mappingPath = "target/test_mapping_2.tab";
    PrintWriter writer = new PrintWriter(new FileWriter(mappingPath));
    writer.println("pattern\tner\toverwrite\tpriority\tgroup");
    writer.println("Stanford University\tSCHOOL\tO,MISC\t1.0\t0");
    writer.close();

    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel token0 = new CoreLabel();
    token0.setWord("I");
    token0.setValue("I");
    token0.set(CoreAnnotations.TextAnnotation.class, "I");
    token0.set(CoreAnnotations.PartOfSpeechAnnotation.class, "PRP");
    token0.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    tokens.add(token0);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("attended");
    token1.setValue("attended");
    token1.set(CoreAnnotations.TextAnnotation.class, "attended");
    token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBD");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    tokens.add(token1);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Stanford");
    token2.setValue("Stanford");
    token2.set(CoreAnnotations.TextAnnotation.class, "Stanford");
    token2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    tokens.add(token2);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("University");
    token3.setValue("University");
    token3.set(CoreAnnotations.TextAnnotation.class, "University");
    token3.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    token3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    tokens.add(token3);

    CoreMap sentence = new Annotation("I attended Stanford University.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation ann = new Annotation("I attended Stanford University.");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    ann.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Properties props = new Properties();
    props.setProperty("tokenregexner.mapping", mappingPath);
    props.setProperty("tokenregexner.ignorecase", "false");

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);
    annotator.annotate(ann);

    assertEquals("O", tokens.get(0).ner());
    assertEquals("O", tokens.get(1).ner());
    assertEquals("SCHOOL", tokens.get(2).ner());
    assertEquals("SCHOOL", tokens.get(3).ner());
  }
@Test
  public void testCaseInsensitiveMatch() throws Exception {
    String mappingPath = "target/test_mapping_3.tab";
    PrintWriter writer = new PrintWriter(new FileWriter(mappingPath));
    writer.println("pattern\tner\toverwrite\tpriority\tgroup");
    writer.println("stanford university\tSCHOOL\tO,MISC\t1.0\t0");
    writer.close();

    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel token0 = new CoreLabel();
    token0.setWord("He");
    token0.setValue("He");
    token0.set(CoreAnnotations.TextAnnotation.class, "He");
    token0.set(CoreAnnotations.PartOfSpeechAnnotation.class, "PRP");
    token0.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    tokens.add(token0);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("studies");
    token1.setValue("studies");
    token1.set(CoreAnnotations.TextAnnotation.class, "studies");
    token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBZ");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    tokens.add(token1);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Stanford");
    token2.setValue("Stanford");
    token2.set(CoreAnnotations.TextAnnotation.class, "Stanford");
    token2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    tokens.add(token2);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("University");
    token3.setValue("University");
    token3.set(CoreAnnotations.TextAnnotation.class, "University");
    token3.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    token3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    tokens.add(token3);

    CoreMap sentence = new Annotation("He studies Stanford University.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation ann = new Annotation("He studies Stanford University.");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    ann.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Properties props = new Properties();
    props.setProperty("tokenregexner.mapping", mappingPath);
    props.setProperty("tokenregexner.ignorecase", "true");

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);
    annotator.annotate(ann);

    assertEquals("O", tokens.get(0).ner());
    assertEquals("O", tokens.get(1).ner());
    assertEquals("SCHOOL", tokens.get(2).ner());
    assertEquals("SCHOOL", tokens.get(3).ner());
  }
@Test(expected = RuntimeException.class)
  public void testMissingTokensThrows() {
    String mappingPath = "target/test_mapping_4.tab";
    try {
      PrintWriter writer = new PrintWriter(new FileWriter(mappingPath));
      writer.println("pattern\tner\toverwrite\tpriority\tgroup");
      writer.println("UC Berkeley\tORG\tO\t1.0\t0");
      writer.close();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    Annotation ann = new Annotation("Incomplete input");

    Properties props = new Properties();
    props.setProperty("tokenregexner.mapping", mappingPath);

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);
    annotator.annotate(ann); 
  }
@Test
public void testOverlappingEntitySpanIsNotOverwritten() throws Exception {
  String mappingPath = "target/test_overlap_rule.tab";
  PrintWriter writer = new PrintWriter(new FileWriter(mappingPath));
  writer.println("pattern\tner\toverwrite\tpriority\tgroup");
  writer.println("New York\tCITY\tO\t1.0\t0");
  writer.close();

  List<CoreLabel> tokens = new ArrayList<>();

  CoreLabel token0 = new CoreLabel();
  token0.setWord("The");
  token0.set(CoreAnnotations.TextAnnotation.class, "The");
  token0.set(CoreAnnotations.PartOfSpeechAnnotation.class, "DT");
  token0.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORG");
  tokens.add(token0);

  CoreLabel token1 = new CoreLabel();
  token1.setWord("New");
  token1.set(CoreAnnotations.TextAnnotation.class, "New");
  token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORG");
  tokens.add(token1);

  CoreLabel token2 = new CoreLabel();
  token2.setWord("York");
  token2.set(CoreAnnotations.TextAnnotation.class, "York");
  token2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORG");
  tokens.add(token2);

  CoreMap sentence = new Annotation("The New York office");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  Annotation ann = new Annotation("The New York office");
  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
  ann.set(CoreAnnotations.TokensAnnotation.class, tokens);

  Properties props = new Properties();
  props.setProperty("tokenregexner.mapping", mappingPath);

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);
  annotator.annotate(ann);

  assertEquals("ORG", tokens.get(0).ner());
  assertEquals("ORG", tokens.get(1).ner());
  assertEquals("ORG", tokens.get(2).ner());
}
@Test
public void testTypeWithCommaIsSplitAndStripped() throws Exception {
  String mappingPath = "target/test_comma_in_type.tab";
  PrintWriter writer = new PrintWriter(new FileWriter(mappingPath));
  writer.println("pattern\tner\toverwrite\tpriority\tgroup");
  writer.println("Big Apple\tCITY,EXTRA\tO\t1.0\t0");
  writer.close();

  List<CoreLabel> tokens = new ArrayList<>();

  CoreLabel token0 = new CoreLabel();
  token0.setWord("Big");
  token0.set(CoreAnnotations.TextAnnotation.class, "Big");
  token0.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  token0.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
  tokens.add(token0);

  CoreLabel token1 = new CoreLabel();
  token1.setWord("Apple");
  token1.set(CoreAnnotations.TextAnnotation.class, "Apple");
  token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
  tokens.add(token1);

  CoreMap sentence = new Annotation("Big Apple");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  Annotation ann = new Annotation("Big Apple");
  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
  ann.set(CoreAnnotations.TokensAnnotation.class, tokens);

  Properties props = new Properties();
  props.setProperty("tokenregexner.mapping", mappingPath);

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);
  annotator.annotate(ann);

  assertEquals("CITY", tokens.get(0).ner());
  assertEquals("CITY", tokens.get(1).ner());
}
@Test
public void testInvalidGroupNumberThrowsException() throws Exception {
  String mappingPath = "target/test_invalid_group.tab";
  PrintWriter writer = new PrintWriter(new FileWriter(mappingPath));
  writer.println("pattern\tner\toverwrite\tpriority\tgroup");
  writer.println("( /John/ /Smith/ )\tPERSON\tO\t1.0\t100"); 
  writer.close();

  try {
    Properties props = new Properties();
    props.setProperty("tokenregexner.mapping", mappingPath);

    new TokensRegexNERAnnotator("tokenregexner", props);
  } catch (RuntimeException e) {
    assertTrue(e.getMessage().contains("Invalid match group"));
  }
}
@Test
public void testPOSFilteringBlocksAnnotation() throws Exception {
  String mappingPath = "target/test_pos_match.tab";
  PrintWriter writer = new PrintWriter(new FileWriter(mappingPath));
  writer.println("pattern\tner\toverwrite\tpriority\tgroup");
  writer.println("Stanford University\tSCHOOL\tO\t1.0\t0");
  writer.close();

  List<CoreLabel> tokens = new ArrayList<>();

  CoreLabel token0 = new CoreLabel();
  token0.setWord("Stanford");
  token0.set(CoreAnnotations.TextAnnotation.class, "Stanford");
  token0.set(CoreAnnotations.PartOfSpeechAnnotation.class, "JJ");
  token0.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
  tokens.add(token0);

  CoreLabel token1 = new CoreLabel();
  token1.setWord("University");
  token1.set(CoreAnnotations.TextAnnotation.class, "University");
  token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "JJ"); 
  token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
  tokens.add(token1);

  CoreMap sentence = new Annotation("Stanford University");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  Annotation ann = new Annotation("Stanford University");
  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
  ann.set(CoreAnnotations.TokensAnnotation.class, tokens);

  Properties props = new Properties();
  props.setProperty("tokenregexner.mapping", mappingPath);
  props.setProperty("tokenregexner.validpospattern", "^NNP$");
  props.setProperty("tokenregexner.posmatchtype", "MATCH_ALL_TOKENS");

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);
  annotator.annotate(ann);

  assertEquals("O", tokens.get(0).ner());
  assertEquals("O", tokens.get(1).ner());
}
@Test
public void testEmptyOverwriteFieldIsHandled() throws Exception {
  String mappingPath = "target/test_empty_overwrite.tab";
  PrintWriter writer = new PrintWriter(new FileWriter(mappingPath));
  writer.println("pattern\tner\toverwrite\tpriority\tgroup");
  writer.println("Gotham\tCITY\t\t1.0\t0");
  writer.close();

  List<CoreLabel> tokens = new ArrayList<>();

  CoreLabel token0 = new CoreLabel();
  token0.setWord("Gotham");
  token0.set(CoreAnnotations.TextAnnotation.class, "Gotham");
  token0.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  token0.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
  tokens.add(token0);

  CoreMap sentence = new Annotation("Gotham");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  Annotation ann = new Annotation("Gotham");
  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
  ann.set(CoreAnnotations.TokensAnnotation.class, tokens);

  Properties props = new Properties();
  props.setProperty("tokenregexner.mapping", mappingPath);

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);
  annotator.annotate(ann);

  assertEquals("CITY", tokens.get(0).ner());
}
@Test
public void testNoSentenceAnnotationTriggersTokenFallback() throws Exception {
  String mappingPath = "target/test_only_tokens.tab";
  PrintWriter writer = new PrintWriter(new FileWriter(mappingPath));
  writer.println("pattern\tner\toverwrite\tpriority\tgroup");
  writer.println("Harvard University\tSCHOOL\tO\t1.0\t0");
  writer.close();

  List<CoreLabel> tokens = new ArrayList<>();

  CoreLabel token0 = new CoreLabel();
  token0.setWord("Harvard");
  token0.set(CoreAnnotations.TextAnnotation.class, "Harvard");
  token0.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  token0.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
  tokens.add(token0);

  CoreLabel token1 = new CoreLabel();
  token1.setWord("University");
  token1.set(CoreAnnotations.TextAnnotation.class, "University");
  token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
  tokens.add(token1);

  Annotation ann = new Annotation("Harvard University");
  ann.set(CoreAnnotations.TokensAnnotation.class, tokens);

  Properties props = new Properties();
  props.setProperty("tokenregexner.mapping", mappingPath);

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);
  annotator.annotate(ann);

  assertEquals("SCHOOL", tokens.get(0).ner());
  assertEquals("SCHOOL", tokens.get(1).ner());
}
@Test
public void testPerFileIgnoreCaseOptionOverridesGlobalValue() throws Exception {
  String mappingPath = "target/test_case_override_1.tab";
  PrintWriter writer = new PrintWriter(new FileWriter(mappingPath));
  writer.println("pattern\tner\toverwrite\tpriority\tgroup");
  writer.println("foobar university\tSCHOOL\tO\t1.0\t0");
  writer.close();

  String perFile = "ignorecase=true, " + mappingPath;

  List<CoreLabel> tokens = new ArrayList<>();

  CoreLabel token0 = new CoreLabel();
  token0.setWord("Foobar");
  token0.set(CoreAnnotations.TextAnnotation.class, "Foobar");
  token0.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
  token0.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  tokens.add(token0);

  CoreLabel token1 = new CoreLabel();
  token1.setWord("University");
  token1.set(CoreAnnotations.TextAnnotation.class, "University");
  token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
  token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  tokens.add(token1);

  Annotation ann = new Annotation("Foobar University");
  CoreMap sent = new Annotation("Foobar University");
  sent.set(CoreAnnotations.TokensAnnotation.class, tokens);
  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sent));
  ann.set(CoreAnnotations.TokensAnnotation.class, tokens);

  Properties props = new Properties();
  props.setProperty("tokenregexner.mapping", perFile);
  props.setProperty("tokenregexner.ignorecase", "false");

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);
  annotator.annotate(ann);

  assertEquals("SCHOOL", tokens.get(0).ner());
  assertEquals("SCHOOL", tokens.get(1).ner());
}
@Test
public void testPriorityResolvesConflictBetweenPatterns() throws Exception {
  String mappingPath = "target/test_priority_conflict.tab";
  PrintWriter writer = new PrintWriter(new FileWriter(mappingPath));
  writer.println("pattern\tner\toverwrite\tpriority\tgroup");
  writer.println("Data Science\tFIELD\tO\t2.0\t0");
  writer.println("Data\tNOUN\tO\t1.0\t0");
  writer.close();

  List<CoreLabel> tokens = new ArrayList<>();

  CoreLabel token0 = new CoreLabel();
  token0.setWord("Data");
  token0.set(CoreAnnotations.TextAnnotation.class, "Data");
  token0.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
  token0.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
  tokens.add(token0);

  CoreLabel token1 = new CoreLabel();
  token1.setWord("Science");
  token1.set(CoreAnnotations.TextAnnotation.class, "Science");
  token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
  token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
  tokens.add(token1);

  Annotation ann = new Annotation("Data Science");
  CoreMap sent = new Annotation("Data Science");
  sent.set(CoreAnnotations.TokensAnnotation.class, tokens);
  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sent));
  ann.set(CoreAnnotations.TokensAnnotation.class, tokens);

  Properties props = new Properties();
  props.setProperty("tokenregexner.mapping", mappingPath);

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);
  annotator.annotate(ann);

  assertEquals("FIELD", tokens.get(0).ner());
  assertEquals("FIELD", tokens.get(1).ner());
}
@Test(expected = IllegalArgumentException.class)
public void testInvalidHeaderFieldThrowsException() throws Exception {
  String mappingPath = "target/test_invalid_header.tab";
  PrintWriter writer = new PrintWriter(new FileWriter(mappingPath));
  writer.println("wrong\tner\toverwrite\tpriority\tgroup");
  writer.println("Foo Bar\tSOMETHING\tO\t1.0\t0");
  writer.close();

  Properties props = new Properties();
  props.setProperty("tokenregexner.mapping", mappingPath);
  props.setProperty("tokenregexner.mapping.header", "wrong,ner,overwrite,priority,group");

  new TokensRegexNERAnnotator("tokenregexner", props);
}
@Test
public void testValidHeaderWithDynamicAnnotationField() throws Exception {
  String mappingPath = "target/test_custom_annotation_field.tab";
  PrintWriter writer = new PrintWriter(new FileWriter(mappingPath));
  writer.println("pattern\tner\tcustomField\toverwrite\tpriority\tgroup");
  writer.println("AI\tTECH\tCustomType\tO\t1.0\t0");
  writer.close();

  List<CoreLabel> tokens = new ArrayList<>();

  CoreLabel token0 = new CoreLabel();
  token0.setWord("AI");
  token0.set(CoreAnnotations.TextAnnotation.class, "AI");
  token0.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
  token0.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
  tokens.add(token0);

  Annotation ann = new Annotation("AI");
  CoreMap sent = new Annotation("AI");
  sent.set(CoreAnnotations.TokensAnnotation.class, tokens);
  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sent));
  ann.set(CoreAnnotations.TokensAnnotation.class, tokens);

  Properties props = new Properties();
  props.setProperty("tokenregexner.mapping", mappingPath);
  props.setProperty("tokenregexner.mapping.header", "pattern,ner,customField,overwrite,priority,group");
  props.setProperty("tokenregexner.mapping.field.customField", "edu.stanford.nlp.ling.CoreAnnotations$CustomAnnotation");

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);
  annotator.annotate(ann);

  assertEquals("TECH", tokens.get(0).ner());
}
@Test
public void testAllowOverwriteByDefaultBackgroundSymbol() throws Exception {
  String mappingPath = "target/test_backgroundSymbols.tab";
  PrintWriter writer = new PrintWriter(new FileWriter(mappingPath));
  writer.println("pattern\tner\toverwrite\tpriority\tgroup");
  writer.println("Quantum Computing\tTECH\t\t1.0\t0");
  writer.close();

  List<CoreLabel> tokens = new ArrayList<>();

  CoreLabel token0 = new CoreLabel();
  token0.setWord("Quantum");
  token0.set(CoreAnnotations.TextAnnotation.class, "Quantum");
  token0.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  token0.set(CoreAnnotations.NamedEntityTagAnnotation.class, "MISC");
  tokens.add(token0);

  CoreLabel token1 = new CoreLabel();
  token1.setWord("Computing");
  token1.set(CoreAnnotations.TextAnnotation.class, "Computing");
  token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "MISC");
  tokens.add(token1);

  Annotation ann = new Annotation("Quantum Computing");
  CoreMap sent = new Annotation("Quantum Computing");
  sent.set(CoreAnnotations.TokensAnnotation.class, tokens);
  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sent));
  ann.set(CoreAnnotations.TokensAnnotation.class, tokens);

  Properties props = new Properties();
  props.setProperty("tokenregexner.mapping", mappingPath);

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);
  annotator.annotate(ann);

  assertEquals("TECH", tokens.get(0).ner());
  assertEquals("TECH", tokens.get(1).ner());
}
@Test
public void testCustomNoDefaultOverwriteLabelsPreventsOverwrite() throws Exception {
  String mappingPath = "target/test_no_default_labels.tab";
  PrintWriter writer = new PrintWriter(new FileWriter(mappingPath));
  writer.println("pattern\tner\toverwrite\tpriority\tgroup");
  writer.println("OpenAI\tORG\tO\t1.0\t0");
  writer.close();

  List<CoreLabel> tokens = new ArrayList<>();

  CoreLabel token0 = new CoreLabel();
  token0.setWord("OpenAI");
  token0.set(CoreAnnotations.TextAnnotation.class, "OpenAI");
  token0.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  token0.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORG");
  tokens.add(token0);

  Annotation ann = new Annotation("OpenAI");
  CoreMap sent = new Annotation("OpenAI");
  sent.set(CoreAnnotations.TokensAnnotation.class, tokens);
  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sent));
  ann.set(CoreAnnotations.TokensAnnotation.class, tokens);

  Properties props = new Properties();
  props.setProperty("tokenregexner.mapping", mappingPath);
  props.setProperty("tokenregexner.noDefaultOverwriteLabels", "ORG");

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);
  annotator.annotate(ann);

  assertEquals("ORG", tokens.get(0).ner());
}
@Test
public void testReadHeaderFromFirstLineOfFile() throws Exception {
  String mappingPath = "target/test_header_first_line.tab";
  PrintWriter writer = new PrintWriter(new FileWriter(mappingPath));
  writer.println("pattern\toverwrite\tpriority\tner\tgroup");
  writer.println("Tesla Motors\tO\t1.0\tORG\t0");
  writer.close();

  String config = "header=true, " + mappingPath;

  List<CoreLabel> tokens = new ArrayList<>();

  CoreLabel token0 = new CoreLabel();
  token0.setWord("Tesla");
  token0.set(CoreAnnotations.TextAnnotation.class, "Tesla");
  token0.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
  token0.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  tokens.add(token0);

  CoreLabel token1 = new CoreLabel();
  token1.setWord("Motors");
  token1.set(CoreAnnotations.TextAnnotation.class, "Motors");
  token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
  token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  tokens.add(token1);

  Annotation ann = new Annotation("Tesla Motors");
  CoreMap sentence = new Annotation("Tesla Motors");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
  ann.set(CoreAnnotations.TokensAnnotation.class, tokens);
  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  Properties props = new Properties();
  props.setProperty("tokenregexner.mapping", config);
  props.setProperty("tokenregexner.mapping.header", "true");

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);
  annotator.annotate(ann);

  assertEquals("ORG", tokens.get(0).ner());
  assertEquals("ORG", tokens.get(1).ner());
}
@Test
public void testGroupMatchOutsideTokenRangeIgnored() throws Exception {
  String mappingPath = "target/test_invalid_group_match.tab";
  PrintWriter writer = new PrintWriter(new FileWriter(mappingPath));
  writer.println("pattern\tner\toverwrite\tpriority\tgroup");
  writer.println("( /San/ /Francisco/ )\tCITY\tO\t1.0\t2"); 
  writer.close();

  boolean thrown = false;
  try {
    Properties props = new Properties();
    props.setProperty("tokenregexner.mapping", mappingPath);
    new TokensRegexNERAnnotator("tokenregexner", props);
  } catch (RuntimeException e) {
    thrown = true;
    assertTrue(e.getMessage().contains("Invalid match group"));
  }

  assertTrue(thrown);
}
@Test
public void testMultipleMappingsMaintainSeparateIgnoreCaseFlags() throws Exception {
  String mapping1 = "target/test_multimap_1.tab";
  String mapping2 = "target/test_multimap_2.tab";

  PrintWriter w1 = new PrintWriter(new FileWriter(mapping1));
  w1.println("pattern\tner\toverwrite\tpriority\tgroup");
  w1.println("facebook\tORG\tO\t1.0\t0"); 
  w1.close();

  PrintWriter w2 = new PrintWriter(new FileWriter(mapping2));
  w2.println("pattern\tner\toverwrite\tpriority\tgroup");
  w2.println("Google\tORG\tO\t1.0\t0"); 
  w2.close();

  String mappingConfig = "ignorecase=true, " + mapping1 + "; ignorecase=false, " + mapping2;

  List<CoreLabel> tokens = new ArrayList<>();

  CoreLabel token0 = new CoreLabel();
  token0.setWord("Facebook");
  token0.set(CoreAnnotations.TextAnnotation.class, "Facebook");
  token0.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
  token0.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  tokens.add(token0);

  CoreLabel token1 = new CoreLabel();
  token1.setWord("google");
  token1.set(CoreAnnotations.TextAnnotation.class, "google");
  token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
  token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  tokens.add(token1);

  Annotation ann = new Annotation("Facebook google");
  CoreMap sentence = new Annotation("Facebook google");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
  ann.set(CoreAnnotations.TokensAnnotation.class, tokens);
  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  Properties props = new Properties();
  props.setProperty("tokenregexner.mapping", mappingConfig);

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);
  annotator.annotate(ann);

  assertEquals("ORG", tokens.get(0).ner());     
  assertEquals("O", tokens.get(1).ner());       
}
@Test
public void testEmptyValidPosPatternBehavesGracefully() throws Exception {
  String mappingPath = "target/test_pospattern_empty.tab";
  PrintWriter writer = new PrintWriter(new FileWriter(mappingPath));
  writer.println("pattern\tner\toverwrite\tpriority\tgroup");
  writer.println("Berkeley\tCITY\tO\t1.0\t0");
  writer.close();

  List<CoreLabel> tokens = new ArrayList<>();
  CoreLabel token = new CoreLabel();
  token.setWord("Berkeley");
  token.set(CoreAnnotations.TextAnnotation.class, "Berkeley");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
  tokens.add(token);

  CoreMap sentence = new Annotation("Berkeley");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
  Annotation ann = new Annotation("Berkeley");
  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
  ann.set(CoreAnnotations.TokensAnnotation.class, tokens);

  Properties props = new Properties();
  props.setProperty("tokenregexner.mapping", mappingPath);
  props.setProperty("tokenregexner.validpospattern", "");

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);
  annotator.annotate(ann);

  assertEquals("CITY", tokens.get(0).ner());
}
@Test
public void testLabelIsNullEntryIsStillApplied() throws Exception {
  String mappingPath = "target/test_null_label.tab";
  PrintWriter writer = new PrintWriter(new FileWriter(mappingPath));
  writer.println("pattern\tner\toverwrite\tpriority\tgroup");
  writer.println("Unknown\tOTHER\t\t1.0\t0");
  writer.close();

  List<CoreLabel> tokens = new ArrayList<>();
  CoreLabel token = new CoreLabel();
  token.setWord("Unknown");
  token.set(CoreAnnotations.TextAnnotation.class, "Unknown");
  token.set(CoreAnnotations.NamedEntityTagAnnotation.class, null);  
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
  tokens.add(token);

  CoreMap sentence = new Annotation("Unknown");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
  Annotation ann = new Annotation("Unknown");
  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
  ann.set(CoreAnnotations.TokensAnnotation.class, tokens);

  Properties props = new Properties();
  props.setProperty("tokenregexner.mapping", mappingPath);

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);
  annotator.annotate(ann);

  assertEquals("OTHER", tokens.get(0).ner());
}
@Test
public void testRegexMatchIsNotAppliedToNonTokenSequencePattern() throws Exception {
  String mappingPath = "target/test_non_tokensregex.tab";
  PrintWriter writer = new PrintWriter(new FileWriter(mappingPath));
  writer.println("pattern\tner\toverwrite\tpriority\tgroup");
  writer.println("The\\s+Rock\tPERSON\tO\t1.0\t0");
  writer.close();

  List<CoreLabel> tokens = new ArrayList<>();
  CoreLabel token0 = new CoreLabel();
  token0.setWord("The");
  token0.set(CoreAnnotations.TextAnnotation.class, "The");
  token0.set(CoreAnnotations.PartOfSpeechAnnotation.class, "DT");
  token0.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
  tokens.add(token0);

  CoreLabel token1 = new CoreLabel();
  token1.setWord("Rock");
  token1.set(CoreAnnotations.TextAnnotation.class, "Rock");
  token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
  tokens.add(token1);

  Annotation ann = new Annotation("The Rock");
  CoreMap sentence = new Annotation("The Rock");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
  ann.set(CoreAnnotations.TokensAnnotation.class, tokens);

  Properties props = new Properties();
  props.setProperty("tokenregexner.mapping", mappingPath);

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);
  annotator.annotate(ann);

  assertEquals("O", tokens.get(0).ner());
  assertEquals("O", tokens.get(1).ner());
}
@Test
public void testNoOverwriteDueToConsistentOldNERTagsAndNonOverwritableType() throws Exception {
  String mappingPath = "target/test_consistent_tag_not_overwritten.tab";
  PrintWriter writer = new PrintWriter(new FileWriter(mappingPath));
  writer.println("pattern\tner\toverwrite\tpriority\tgroup");
  writer.println("Apple Inc\tORG\tLOCATION\t1.0\t0");
  writer.close();

  List<CoreLabel> tokens = new ArrayList<>();

  CoreLabel token0 = new CoreLabel();
  token0.setWord("Apple");
  token0.set(CoreAnnotations.TextAnnotation.class, "Apple");
  token0.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  token0.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORG");
  tokens.add(token0);

  CoreLabel token1 = new CoreLabel();
  token1.setWord("Inc");
  token1.set(CoreAnnotations.TextAnnotation.class, "Inc");
  token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORG");
  tokens.add(token1);

  Annotation ann = new Annotation("Apple Inc");
  CoreMap sentence = new Annotation("Apple Inc");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
  ann.set(CoreAnnotations.TokensAnnotation.class, tokens);
  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  Properties props = new Properties();
  props.setProperty("tokenregexner.mapping", mappingPath);
  props.setProperty("tokenregexner.noDefaultOverwriteLabels", "ORG");

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);
  annotator.annotate(ann);

  assertEquals("ORG", tokens.get(0).ner());
  assertEquals("ORG", tokens.get(1).ner());
}
@Test
public void testOverwriteAppliesWhenOldNERInOverwriteableTypes() throws Exception {
  String mappingPath = "target/test_overwrite_by_explicit_type.tab";
  PrintWriter writer = new PrintWriter(new FileWriter(mappingPath));
  writer.println("pattern\tner\toverwrite\tpriority\tgroup");
  writer.println("Amazon\tCOMPANY\tORG\t1.0\t0");
  writer.close();

  List<CoreLabel> tokens = new ArrayList<>();
  CoreLabel token = new CoreLabel();
  token.setWord("Amazon");
  token.set(CoreAnnotations.TextAnnotation.class, "Amazon");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORG");
  tokens.add(token);

  Annotation ann = new Annotation("Amazon");
  CoreMap sentence = new Annotation("Amazon");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
  ann.set(CoreAnnotations.TokensAnnotation.class, tokens);
  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  Properties props = new Properties();
  props.setProperty("tokenregexner.mapping", mappingPath);
  props.setProperty("tokenregexner.noDefaultOverwriteLabels", "NONE");

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);
  annotator.annotate(ann);

  assertEquals("COMPANY", tokens.get(0).ner());
}
@Test
public void testPriorityTieChoosesLongerMatch() throws Exception {
  String mappingPath = "target/test_tie_priority_longer_match.tab";
  PrintWriter writer = new PrintWriter(new FileWriter(mappingPath));
  writer.println("pattern\tner\toverwrite\tpriority\tgroup");
  writer.println("Machine\tSINGLE\tO\t1.0\t0");
  writer.println("Machine Learning\tPHRASE\tO\t1.0\t0");
  writer.close();

  List<CoreLabel> tokens = new ArrayList<>();
  CoreLabel token0 = new CoreLabel();
  token0.setWord("Machine");
  token0.set(CoreAnnotations.TextAnnotation.class, "Machine");
  token0.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
  token0.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
  tokens.add(token0);

  CoreLabel token1 = new CoreLabel();
  token1.setWord("Learning");
  token1.set(CoreAnnotations.TextAnnotation.class, "Learning");
  token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
  token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
  tokens.add(token1);

  Annotation ann = new Annotation("Machine Learning");
  CoreMap sentence = new Annotation("Machine Learning");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
  ann.set(CoreAnnotations.TokensAnnotation.class, tokens);
  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  Properties props = new Properties();
  props.setProperty("tokenregexner.mapping", mappingPath);

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);
  annotator.annotate(ann);

  assertEquals("PHRASE", tokens.get(0).ner());
  assertEquals("PHRASE", tokens.get(1).ner());
}
@Test
public void testOverlappingExistingEntityBeforeAndAfterMatchShouldBlockOverwrite() throws Exception {
  String mappingPath = "target/test_overlap_block.tab";
  PrintWriter writer = new PrintWriter(new FileWriter(mappingPath));
  writer.println("pattern\tner\toverwrite\tpriority\tgroup");
  writer.println("New York\tCITY\tO\t1.0\t0");
  writer.close();

  List<CoreLabel> tokens = new ArrayList<>();

  CoreLabel token0 = new CoreLabel();
  token0.setWord("I");
  token0.set(CoreAnnotations.TextAnnotation.class, "I");
  token0.set(CoreAnnotations.PartOfSpeechAnnotation.class, "PRP");
  token0.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
  tokens.add(token0);

  CoreLabel token1 = new CoreLabel();
  token1.setWord("visited");
  token1.set(CoreAnnotations.TextAnnotation.class, "visited");
  token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBD");
  token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
  tokens.add(token1);

  CoreLabel token2 = new CoreLabel();
  token2.setWord("New");
  token2.set(CoreAnnotations.TextAnnotation.class, "New");
  token2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PLACE");
  tokens.add(token2);

  CoreLabel token3 = new CoreLabel();
  token3.setWord("York");
  token3.set(CoreAnnotations.TextAnnotation.class, "York");
  token3.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  token3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PLACE");
  tokens.add(token3);

  CoreLabel token4 = new CoreLabel();
  token4.setWord("City");
  token4.set(CoreAnnotations.TextAnnotation.class, "City");
  token4.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  token4.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PLACE");
  tokens.add(token4);

  Annotation ann = new Annotation("I visited New York City");
  CoreMap sentence = new Annotation("I visited New York City");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
  ann.set(CoreAnnotations.TokensAnnotation.class, tokens);
  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  Properties props = new Properties();
  props.setProperty("tokenregexner.mapping", mappingPath);
  props.setProperty("tokenregexner.noDefaultOverwriteLabels", "CITY");

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);
  annotator.annotate(ann);

  assertEquals("PLACE", tokens.get(2).ner());
  assertEquals("PLACE", tokens.get(3).ner());
  assertEquals("PLACE", tokens.get(4).ner());
}
@Test(expected = IllegalArgumentException.class)
public void testTooFewColumnsInMappingFileThrowsException() throws Exception {
  String mappingPath = "target/test_mapping_missing_fields.tab";
  PrintWriter writer = new PrintWriter(new FileWriter(mappingPath));
  writer.println("pattern\tner\tpriority");
  writer.println("Stanford University\tSCHOOL");
  writer.close();

  Properties props = new Properties();
  props.setProperty("tokenregexner.mapping", mappingPath);
  new TokensRegexNERAnnotator("tokenregexner", props);
}
@Test(expected = IllegalArgumentException.class)
public void testDuplicateHeaderFieldThrowsException() throws Exception {
  String mappingPath = "target/test_duplicate_header.tab";
  PrintWriter writer = new PrintWriter(new FileWriter(mappingPath));
  writer.println("pattern\tner\toverwrite\toverwrite\tpriority\tgroup");
  writer.println("MIT\tORG\tO,O\t1.0\t0");
  writer.close();

  Properties props = new Properties();
  props.setProperty("tokenregexner.mapping", mappingPath);
  props.setProperty("tokenregexner.mapping.header", "pattern,ner,overwrite,overwrite,priority,group");

  new TokensRegexNERAnnotator("tokenregexner", props);
}
@Test
public void testPatternWithWhitespaceDelimitersIsParsedCorrectly() throws Exception {
  String mappingPath = "target/test_tab_or_space_parser.tab";
  PrintWriter writer = new PrintWriter(new FileWriter(mappingPath));
  writer.println("pattern\tner\toverwrite\tpriority\tgroup");
  writer.println("University\\s+of\\s+Texas\tSCHOOL\tO\t1\t0");
  writer.close();

  List<CoreLabel> tokens = new ArrayList<>();

  CoreLabel token0 = new CoreLabel();
  token0.setWord("University");
  token0.set(CoreAnnotations.TextAnnotation.class, "University");
  token0.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  token0.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
  tokens.add(token0);

  CoreLabel token1 = new CoreLabel();
  token1.setWord("of");
  token1.set(CoreAnnotations.TextAnnotation.class, "of");
  token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "IN");
  token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
  tokens.add(token1);

  CoreLabel token2 = new CoreLabel();
  token2.setWord("Texas");
  token2.set(CoreAnnotations.TextAnnotation.class, "Texas");
  token2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
  tokens.add(token2);

  Annotation ann = new Annotation("University of Texas");
  CoreMap sentence = new Annotation("University of Texas");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
  ann.set(CoreAnnotations.TokensAnnotation.class, tokens);
  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  Properties props = new Properties();
  props.setProperty("tokenregexner.mapping", mappingPath);

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);
  annotator.annotate(ann);

  assertEquals("SCHOOL", tokens.get(0).ner());
  assertEquals("SCHOOL", tokens.get(1).ner());
  assertEquals("SCHOOL", tokens.get(2).ner());
}
@Test
public void testEmptyWeightFieldHandledGracefully() throws Exception {
  String mappingPath = "target/test_empty_weight.tab";
  PrintWriter writer = new PrintWriter(new FileWriter(mappingPath));
  writer.println("pattern\tner\toverwrite\tpriority\tweight\tgroup");
  writer.println("Caltech\tSCHOOL\tO\t2.0\t\t0");
  writer.close();

  List<CoreLabel> tokens = new ArrayList<>();
  CoreLabel token = new CoreLabel();
  token.setWord("Caltech");
  token.set(CoreAnnotations.TextAnnotation.class, "Caltech");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
  tokens.add(token);

  CoreMap sentence = new Annotation("Caltech");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
  Annotation ann = new Annotation("Caltech");
  ann.set(CoreAnnotations.TokensAnnotation.class, tokens);
  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  Properties props = new Properties();
  props.setProperty("tokenregexner.mapping", mappingPath);

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);
  annotator.annotate(ann);

  assertEquals("SCHOOL", tokens.get(0).ner());
}
@Test
public void testEmptyGroupFieldDefaultsToZero() throws Exception {
  String mappingPath = "target/test_missing_group_field.tab";
  PrintWriter writer = new PrintWriter(new FileWriter(mappingPath));
  writer.println("pattern\tner\toverwrite\tpriority");
  writer.println("MIT\tSCHOOL\tO\t9.0");
  writer.close();

  List<CoreLabel> tokens = new ArrayList<>();
  CoreLabel token = new CoreLabel();
  token.setWord("MIT");
  token.set(CoreAnnotations.TextAnnotation.class, "MIT");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
  token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
  tokens.add(token);

  CoreMap sentence = new Annotation("MIT");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
  Annotation ann = new Annotation("MIT");
  ann.set(CoreAnnotations.TokensAnnotation.class, tokens);
  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  Properties props = new Properties();
  props.setProperty("tokenregexner.mapping", mappingPath);
  props.setProperty("tokenregexner.mapping.header", "pattern,ner,overwrite,priority");

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);
  annotator.annotate(ann);

  assertEquals("SCHOOL", tokens.get(0).ner());
}
@Test
public void testMatchOneTokenPosMatchTypeAppliedProperly() throws Exception {
  String mappingPath = "target/test_match_one_token_pos.tab";
  PrintWriter writer = new PrintWriter(new FileWriter(mappingPath));
  writer.println("pattern\tner\toverwrite\tpriority\tgroup");
  writer.println("AI\tTECH\tO\t1.0\t0");
  writer.close();

  List<CoreLabel> tokens = new ArrayList<>();
  CoreLabel token = new CoreLabel();
  token.setWord("AI");
  token.set(CoreAnnotations.TextAnnotation.class, "AI");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "JJ"); 
  token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
  tokens.add(token);

  CoreMap sentence = new Annotation("AI");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
  Annotation ann = new Annotation("AI");
  ann.set(CoreAnnotations.TokensAnnotation.class, tokens);
  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  Properties props = new Properties();
  props.setProperty("tokenregexner.mapping", mappingPath);
  props.setProperty("tokenregexner.validpospattern", "^NNP$");
  props.setProperty("tokenregexner.posmatchtype", "MATCH_ONE_TOKEN_PHRASE_ONLY");

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);
  annotator.annotate(ann);

  assertEquals("O", tokens.get(0).ner());  
}
@Test(expected = IllegalArgumentException.class)
public void testInvalidPriorityValueThrowsException() throws Exception {
  String mappingPath = "target/test_invalid_priority.tab";
  PrintWriter writer = new PrintWriter(new FileWriter(mappingPath));
  writer.println("pattern\tner\toverwrite\tpriority\tgroup");
  writer.println("FooBar\tCOMPANY\tO\tinvalid_value\t0");
  writer.close();

  Properties props = new Properties();
  props.setProperty("tokenregexner.mapping", mappingPath);
  new TokensRegexNERAnnotator("tokenregexner", props);
}
@Test(expected = IllegalArgumentException.class)
public void testInvalidWeightValueThrowsException() throws Exception {
  String mappingPath = "target/test_invalid_weight.tab";
  PrintWriter writer = new PrintWriter(new FileWriter(mappingPath));
  writer.println("pattern\tner\toverwrite\tpriority\tweight\tgroup");
  writer.println("FooBar\tCOMPANY\tO\t1.0\tnot_a_number\t0");
  writer.close();

  Properties props = new Properties();
  props.setProperty("tokenregexner.mapping", mappingPath);
  new TokensRegexNERAnnotator("tokenregexner", props);
}
@Test(expected = IllegalArgumentException.class)
public void testInvalidGroupFieldThrowsException() throws Exception {
  String mappingPath = "target/test_invalid_group.tab";
  PrintWriter writer = new PrintWriter(new FileWriter(mappingPath));
  writer.println("pattern\tner\toverwrite\tpriority\tgroup");
  writer.println("( /Deep/ /Learning/ )\tFIELD\tO\t1.0\tabc");
  writer.close();

  Properties props = new Properties();
  props.setProperty("tokenregexner.mapping", mappingPath);
  new TokensRegexNERAnnotator("tokenregexner", props);
}
@Test
public void testEmptyNERTagCanBeOverwrittenByNull() throws Exception {
  String mappingPath = "target/test_overwrite_null.tab";
  PrintWriter writer = new PrintWriter(new FileWriter(mappingPath));
  writer.println("pattern\tner\toverwrite\tpriority\tgroup");
  writer.println("Uber\tCOMPANY\t\t1.0\t0");
  writer.close();

  List<CoreLabel> tokens = new ArrayList<>();
  CoreLabel token = new CoreLabel();
  token.setWord("Uber");
  token.set(CoreAnnotations.TextAnnotation.class, "Uber");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  token.set(CoreAnnotations.NamedEntityTagAnnotation.class, null);
  tokens.add(token);

  CoreMap sentence = new Annotation("Uber");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
  Annotation ann = new Annotation("Uber");
  ann.set(CoreAnnotations.TokensAnnotation.class, tokens);
  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  Properties props = new Properties();
  props.setProperty("tokenregexner.mapping", mappingPath);

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);
  annotator.annotate(ann);

  assertEquals("COMPANY", tokens.get(0).ner());
}
@Test
public void testMultipleMatchesSameSpanLowerPriorityDropped() throws Exception {
  String mappingPath = "target/test_priority_conflict_dedup.tab";
  PrintWriter writer = new PrintWriter(new FileWriter(mappingPath));
  writer.println("pattern\tner\toverwrite\tpriority\tgroup");
  writer.println("Deep Learning\tFIELD1\tO\t1.0\t0");
  writer.println("Deep Learning\tFIELD2\tO\t2.0\t0");
  writer.close();

  List<CoreLabel> tokens = new ArrayList<>();

  CoreLabel token0 = new CoreLabel();
  token0.setWord("Deep");
  token0.set(CoreAnnotations.TextAnnotation.class, "Deep");
  token0.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
  token0.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
  tokens.add(token0);

  CoreLabel token1 = new CoreLabel();
  token1.setWord("Learning");
  token1.set(CoreAnnotations.TextAnnotation.class, "Learning");
  token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
  token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
  tokens.add(token1);

  CoreMap sentence = new Annotation("Deep Learning");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
  Annotation ann = new Annotation("Deep Learning");
  ann.set(CoreAnnotations.TokensAnnotation.class, tokens);
  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  Properties props = new Properties();
  props.setProperty("tokenregexner.mapping", mappingPath);

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);
  annotator.annotate(ann);

  assertEquals("FIELD2", tokens.get(0).ner());
  assertEquals("FIELD2", tokens.get(1).ner());
}
@Test
public void testTokensRegexPatternMatchOverride_WhenSingleGroupUsed() throws Exception {
  String mappingPath = "target/test_tregex_one_group.tab";
  PrintWriter writer = new PrintWriter(new FileWriter(mappingPath));
  writer.println("pattern\tner\toverwrite\tpriority\tgroup");
  writer.println("( /OpenAI/ )\tORG\tO\t1.0\t0");
  writer.close();

  List<CoreLabel> tokens = new ArrayList<>();
  CoreLabel token = new CoreLabel();
  token.setWord("OpenAI");
  token.set(CoreAnnotations.TextAnnotation.class, "OpenAI");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
  tokens.add(token);

  CoreMap sentence = new Annotation("OpenAI");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
  Annotation ann = new Annotation("OpenAI");
  ann.set(CoreAnnotations.TokensAnnotation.class, tokens);
  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  Properties props = new Properties();
  props.setProperty("tokenregexner.mapping", mappingPath);

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);
  annotator.annotate(ann);

  assertEquals("ORG", tokens.get(0).ner());
}
@Test
public void testBackgroundSymbolWithMultipleLabels() throws Exception {
  String mappingPath = "target/test_override_misc_custom_background.tab";
  PrintWriter writer = new PrintWriter(new FileWriter(mappingPath));
  writer.println("pattern\tner\toverwrite\tpriority\tgroup");
  writer.println("Baidu\tCOMPANY\t\t1.0\t0");
  writer.close();

  List<CoreLabel> tokens = new ArrayList<>();
  CoreLabel token = new CoreLabel();
  token.setWord("Baidu");
  token.set(CoreAnnotations.TextAnnotation.class, "Baidu");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "MISC");
  tokens.add(token);

  CoreMap sentence = new Annotation("Baidu");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
  Annotation ann = new Annotation("Baidu");
  ann.set(CoreAnnotations.TokensAnnotation.class, tokens);
  ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  Properties props = new Properties();
  props.setProperty("tokenregexner.mapping", mappingPath);
  props.setProperty("tokenregexner.backgroundSymbol", "O,MISC,LOC");

  TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("tokenregexner", props);
  annotator.annotate(ann);

  assertEquals("COMPANY", tokens.get(0).ner());
} 
}