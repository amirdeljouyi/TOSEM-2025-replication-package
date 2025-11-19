package edu.stanford.nlp.pipeline;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.StringReader;
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

public class TokensRegexNERAnnotator_3_GPTLLMTest {

 @Test
  public void testSimpleRegexTokenMatch() throws Exception {
    
    String content = "pattern\tnetype\toverwrite\tpriority\tgroup\n"
                   + "Stanford\tORG\tO,MISC\t1.0\t0\n";
    File mappingFile = File.createTempFile("regexner", ".tab");
    mappingFile.deleteOnExit();
    BufferedWriter writer = new BufferedWriter(new FileWriter(mappingFile));
    writer.write(content);
    writer.close();

    Properties props = new Properties();
    props.setProperty("mapping", mappingFile.getAbsolutePath());
    props.setProperty("ignorecase", "false");

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("ner", props);

    
    CoreLabel token0 = new CoreLabel();
    token0.setWord("I");
    token0.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    CoreLabel token1 = new CoreLabel();
    token1.setWord("work");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("at");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    CoreLabel token3 = new CoreLabel();
    token3.setWord("Stanford");
    token3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> tokens = Arrays.asList(token0, token1, token2, token3);

    
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = Arrays.asList(sentence);

    Annotation document = new Annotation("Sample text");
    document.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    document.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(document);

    assertEquals("O", token0.ner());
    assertEquals("O", token1.ner());
    assertEquals("O", token2.ner());
    assertEquals("ORG", token3.ner());
  }
@Test
  public void testOverwriteWithCityWhenOriginalIsO() throws Exception {
    String content = "pattern\tnetype\toverwrite\tpriority\tgroup\n"
                   + "San Francisco\tCITY\tO\t1.0\t0\n";
    File mappingFile = File.createTempFile("regexner", ".tab");
    mappingFile.deleteOnExit();
    BufferedWriter writer = new BufferedWriter(new FileWriter(mappingFile));
    writer.write(content);
    writer.close();

    Properties props = new Properties();
    props.setProperty("mapping", mappingFile.getAbsolutePath());

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("ner", props);

    
    CoreLabel token0 = new CoreLabel();
    token0.setWord("San");
    token0.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Francisco");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> tokens = Arrays.asList(token0, token1);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    List<CoreMap> sentences = Arrays.asList(sentence);

    Annotation document = new Annotation("San Francisco");
    document.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    document.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(document);

    assertEquals("CITY", token0.ner());
    assertEquals("CITY", token1.ner());
  }
@Test
  public void testNoOverwriteIfNERIsNotOverwritable() throws Exception {
    String content = "pattern\tnetype\toverwrite\tpriority\tgroup\n"
                   + "San Francisco\tCITY\tO\t1.0\t0\n";
    File mappingFile = File.createTempFile("regexner", ".tab");
    mappingFile.deleteOnExit();
    BufferedWriter writer = new BufferedWriter(new FileWriter(mappingFile));
    writer.write(content);
    writer.close();

    Properties props = new Properties();
    props.setProperty("mapping", mappingFile.getAbsolutePath());

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("ner", props);

    CoreLabel token0 = new CoreLabel();
    token0.setWord("San");
    token0.set(CoreAnnotations.NamedEntityTagAnnotation.class, "GPE");

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Francisco");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "GPE");

    List<CoreLabel> tokens = Arrays.asList(token0, token1);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    List<CoreMap> sentences = Arrays.asList(sentence);

    Annotation document = new Annotation("San Francisco");
    document.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    document.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(document);

    assertEquals("GPE", token0.ner());
    assertEquals("GPE", token1.ner());
  }
@Test
  public void testCaseInsensitiveMatch() throws Exception {
    String content = "pattern\tnetype\toverwrite\tpriority\tgroup\n"
                   + "Stanford\tORG\tO\t1.0\t0\n";
    File mappingFile = File.createTempFile("regexner", ".tab");
    mappingFile.deleteOnExit();
    BufferedWriter writer = new BufferedWriter(new FileWriter(mappingFile));
    writer.write(content);
    writer.close();

    Properties props = new Properties();
    props.setProperty("mapping", mappingFile.getAbsolutePath());
    props.setProperty("ignorecase", "true");

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("ner", props);

    CoreLabel token = new CoreLabel();
    token.setWord("stanford");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> tokens = Arrays.asList(token);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    List<CoreMap> sentences = Arrays.asList(sentence);

    Annotation doc = new Annotation("stanford");
    doc.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(doc);

    assertEquals("ORG", token.ner());
  }
@Test(expected = RuntimeException.class)
  public void testThrowsWhenNoSentencesOrTokens() {
    Annotation doc = new Annotation("text");
    Properties props = new Properties();
    props.setProperty("mapping", "fakefile.tab");

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("ner", props);

    annotator.annotate(doc);
  }
@Test
  public void testCommonWordsNotAnnotated() throws Exception {
    File mappingFile = File.createTempFile("regexner", ".tab");
    mappingFile.deleteOnExit();
    BufferedWriter ruleWriter = new BufferedWriter(new FileWriter(mappingFile));
    ruleWriter.write("pattern\tnetype\toverwrite\tpriority\tgroup\n");
    ruleWriter.write("Stanford\tORG\tO\t1.0\t0\n");
    ruleWriter.close();

    File commonFile = File.createTempFile("common", ".txt");
    commonFile.deleteOnExit();
    BufferedWriter commonWriter = new BufferedWriter(new FileWriter(commonFile));
    commonWriter.write("Stanford\n");
    commonWriter.close();

    Properties props = new Properties();
    props.setProperty("mapping", mappingFile.getAbsolutePath());
    props.setProperty("commonWords", commonFile.getAbsolutePath());

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("ner", props);

    CoreLabel token = new CoreLabel();
    token.setWord("Stanford");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> tokens = Arrays.asList(token);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    List<CoreMap> sentences = Arrays.asList(sentence);

    Annotation doc = new Annotation("Stanford");
    doc.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(doc);

    assertEquals("O", token.ner());
  }
@Test(expected = IllegalArgumentException.class)
  public void testInvalidHeaderThrows() throws Exception {
    File mappingFile = File.createTempFile("regexner", ".tab");
    mappingFile.deleteOnExit();
    BufferedWriter ruleWriter = new BufferedWriter(new FileWriter(mappingFile));
    ruleWriter.write("pattern\tinvalidfield\toverwrite\tpriority\tgroup\n");
    ruleWriter.write("Stanford\tORG\tO\t1.0\t0\n");
    ruleWriter.close();

    Properties props = new Properties();
    props.setProperty("mapping", mappingFile.getAbsolutePath());

    new TokensRegexNERAnnotator("ner", props);
  }
@Test
  public void testEmptyMappingFileDoesNotThrow() throws Exception {
    File mappingFile = File.createTempFile("empty_mapping", ".tab");
    mappingFile.deleteOnExit();
    BufferedWriter writer = new BufferedWriter(new FileWriter(mappingFile));
    writer.write(""); 
    writer.close();

    Properties props = new Properties();
    props.setProperty("mapping", mappingFile.getAbsolutePath());

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("ner", props);

    CoreLabel token = new CoreLabel();
    token.setWord("Hello");
    token.setNER("O");

    List<CoreLabel> tokens = Arrays.asList(token);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    List<CoreMap> sentences = Arrays.asList(sentence);

    Annotation doc = new Annotation("Hello");
    doc.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(doc);

    assertEquals("O", token.ner());
  }
@Test
  public void testDuplicatePatternWithHigherPriorityWins() throws Exception {
    File mappingFile = File.createTempFile("dup_priority", ".tab");
    mappingFile.deleteOnExit();
    BufferedWriter writer = new BufferedWriter(new FileWriter(mappingFile));
    writer.write("pattern\tnetype\toverwrite\tpriority\tgroup\n");
    writer.write("Stanford\tMISC\tO\t1.0\t0\n");
    writer.write("Stanford\tORG\tO\t5.0\t0\n");
    writer.close();

    Properties props = new Properties();
    props.setProperty("mapping", mappingFile.getAbsolutePath());

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("ner", props);

    CoreLabel token = new CoreLabel();
    token.setWord("Stanford");
    token.setNER("O");

    List<CoreLabel> tokens = Arrays.asList(token);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    List<CoreMap> sentences = Arrays.asList(sentence);

    Annotation doc = new Annotation("Stanford");
    doc.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(doc);

    assertEquals("ORG", token.ner());
  }
@Test
  public void testOverwriteLabelSpecifiedButDoesNotMatchExistingNER() throws Exception {
    File mappingFile = File.createTempFile("overwrite_fail", ".tab");
    mappingFile.deleteOnExit();
    BufferedWriter writer = new BufferedWriter(new FileWriter(mappingFile));
    writer.write("pattern\tnetype\toverwrite\tpriority\tgroup\n");
    writer.write("Stanford\tORG\tPERSON\t1.0\t0\n");
    writer.close();

    Properties props = new Properties();
    props.setProperty("mapping", mappingFile.getAbsolutePath());

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("ner", props);

    CoreLabel token = new CoreLabel();
    token.setWord("Stanford");
    token.setNER("LOCATION");

    List<CoreLabel> tokens = Arrays.asList(token);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    List<CoreMap> sentences = Arrays.asList(sentence);

    Annotation doc = new Annotation("Stanford");
    doc.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(doc);

    assertEquals("LOCATION", token.ner());
  }
@Test(expected = RuntimeException.class)
  public void testInvalidGroupNumberThrows() throws Exception {
    File mappingFile = File.createTempFile("invalid_gid", ".tab");
    mappingFile.deleteOnExit();
    BufferedWriter writer = new BufferedWriter(new FileWriter(mappingFile));
    writer.write("pattern\tnetype\toverwrite\tpriority\tgroup\n");
    writer.write("( /University/ /of/ /California/ )\tUNI\tO\t1.0\t2\n"); 
    writer.close();

    Properties props = new Properties();
    props.setProperty("mapping", mappingFile.getAbsolutePath());

    new TokensRegexNERAnnotator("ner", props); 
  }
@Test(expected = IllegalArgumentException.class)
  public void testInvalidPriorityNumberThrows() throws Exception {
    File mappingFile = File.createTempFile("bad_priority", ".tab");
    mappingFile.deleteOnExit();
    BufferedWriter writer = new BufferedWriter(new FileWriter(mappingFile));
    writer.write("pattern\tnetype\toverwrite\tpriority\tgroup\n");
    writer.write("Stanford\tORG\tO\t$badpriority\t0\n"); 
    writer.close();

    Properties props = new Properties();
    props.setProperty("mapping", mappingFile.getAbsolutePath());

    new TokensRegexNERAnnotator("ner", props);
  }
@Test(expected = IllegalArgumentException.class)
  public void testInvalidWeightThrows() throws Exception {
    File mappingFile = File.createTempFile("bad_weight", ".tab");
    mappingFile.deleteOnExit();
    BufferedWriter writer = new BufferedWriter(new FileWriter(mappingFile));
    writer.write("pattern\tnetype\toverwrite\tpriority\tweight\tgroup\n");
    writer.write("Stanford\tORG\tO\t1.0\tbadWeight\t0\n");
    writer.close();

    Properties props = new Properties();
    props.setProperty("mapping", mappingFile.getAbsolutePath());
    props.setProperty("mapping.header", "pattern,netype,overwrite,priority,weight,group");

    new TokensRegexNERAnnotator("ner", props);
  }
@Test(expected = IllegalStateException.class)
  public void testHeaderSetToTrueButFileHeaderIsMissingThrows() throws Exception {
    File mappingFile = File.createTempFile("no_header_line", ".tab");
    mappingFile.deleteOnExit();
    BufferedWriter writer = new BufferedWriter(new FileWriter(mappingFile));
    
    writer.write("Stanford\tORG\tO\t1.0\t0\n");
    writer.close();

    Properties props = new Properties();
    props.setProperty("mapping", "header=true, " + mappingFile.getAbsolutePath());

    new TokensRegexNERAnnotator("ner", props);
  }
@Test
  public void testOverlappingNERIsNotReplaced() throws Exception {
    File mappingFile = File.createTempFile("overlapping", ".tab");
    mappingFile.deleteOnExit();
    BufferedWriter writer = new BufferedWriter(new FileWriter(mappingFile));
    writer.write("pattern\tnetype\toverwrite\tpriority\tgroup\n");
    writer.write("ABC\tORG\tO,MISC\t1.0\t0\n");
    writer.close();

    Properties props = new Properties();
    props.setProperty("mapping", mappingFile.getAbsolutePath());

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("ner", props);

    CoreLabel token0 = new CoreLabel();
    token0.setWord("The");
    token0.setNER("ORG");

    CoreLabel token1 = new CoreLabel();
    token1.setWord("ABC");
    token1.setNER("ORG");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Company");
    token2.setNER("ORG");

    List<CoreLabel> tokens = Arrays.asList(token0, token1, token2);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    List<CoreMap> sentences = Arrays.asList(sentence);

    Annotation doc = new Annotation("The ABC Company");
    doc.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(doc);

    assertEquals("ORG", token1.ner()); 
  }
@Test
  public void testMultipleAnnotationFields() throws Exception {
    File mappingFile = File.createTempFile("multi_field", ".tab");
    mappingFile.deleteOnExit();
    BufferedWriter writer = new BufferedWriter(new FileWriter(mappingFile));
    writer.write("pattern\tner\tcustom\tpriority\tgroup\n");
    writer.write("Stanford\tORG\tUniversity\t1.0\t0\n");
    writer.close();

    Properties props = new Properties();
    props.setProperty("mapping", mappingFile.getAbsolutePath());
    props.setProperty("mapping.header", "pattern,ner,custom,priority,group");
    props.setProperty("mapping.field.custom", "edu.stanford.nlp.ling.CoreAnnotations$AnswerAnnotation");

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("ner", props);

    CoreLabel token = new CoreLabel();
    token.setWord("Stanford");
    token.setNER("O");

    List<CoreLabel> tokens = Arrays.asList(token);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    List<CoreMap> sentences = Arrays.asList(sentence);

    Annotation doc = new Annotation("Stanford");
    doc.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(doc);

    assertEquals("ORG", token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
    assertEquals("University", token.get(CoreAnnotations.AnswerAnnotation.class));
  }
@Test
  public void testMatchAllTokensPOSPatternMatchFails() throws Exception {
    File ruleFile = File.createTempFile("pos_match_all", ".tab");
    ruleFile.deleteOnExit();
    BufferedWriter bw = new BufferedWriter(new FileWriter(ruleFile));
    bw.write("pattern\tnetype\toverwrite\tpriority\tgroup\n");
    bw.write("Java\tLANGUAGE\tO\t1.0\t0\n");
    bw.close();

    Properties props = new Properties();
    props.setProperty("mapping", ruleFile.getAbsolutePath());
    props.setProperty("validpospattern", "NNP");
    props.setProperty("posmatchtype", "MATCH_ALL_TOKENS");

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("ner", props);

    CoreLabel c1 = new CoreLabel();
    c1.setWord("Java");
    c1.setNER("O");
    c1.setTag("JJ");  

    List<CoreLabel> tokens = Arrays.asList(c1);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation doc = new Annotation("");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(doc);

    assertEquals("O", c1.ner());  
  }
@Test
  public void testNoDefaultOverwriteDisablesOverwriteUnlessExplicit() throws Exception {
    File ruleFile = File.createTempFile("no_overwrite_test", ".tab");
    ruleFile.deleteOnExit();
    BufferedWriter bw = new BufferedWriter(new FileWriter(ruleFile));
    bw.write("pattern\tnetype\toverwrite\tpriority\tgroup\n");
    bw.write("Toronto\tCITY\t\t1.0\t0\n");
    bw.close();

    Properties props = new Properties();
    props.setProperty("mapping", ruleFile.getAbsolutePath());
    props.setProperty("noDefaultOverwriteLabels", "CITY");

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("ner", props);

    CoreLabel c1 = new CoreLabel();
    c1.setWord("Toronto");
    c1.setNER("PERSON");  

    List<CoreLabel> tokens = Arrays.asList(c1);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation doc = new Annotation("");
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);
    doc.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(doc);

    assertEquals("PERSON", c1.ner());  
  }
@Test
  public void testEmptyOverwriteColumnMaintainsDefaults() throws Exception {
    File ruleFile = File.createTempFile("empty_overwrite", ".tab");
    ruleFile.deleteOnExit();
    BufferedWriter bw = new BufferedWriter(new FileWriter(ruleFile));
    bw.write("pattern\tnetype\toverwrite\tpriority\tgroup\n");
    bw.write("Toronto\tCITY\t\t1.0\t0\n");
    bw.close();

    Properties props = new Properties();
    props.setProperty("mapping", ruleFile.getAbsolutePath());

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("ner", props);

    CoreLabel c1 = new CoreLabel();
    c1.setWord("Toronto");
    c1.setNER("O");

    List<CoreLabel> tokens = Arrays.asList(c1);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation doc = new Annotation("");
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);
    doc.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(doc);

    assertEquals("CITY", c1.ner());
  }
@Test
  public void testPatternWithHigherPriorityAndSameSpanIsSelected() throws Exception {
    File ruleFile = File.createTempFile("longest_priority", ".tab");
    ruleFile.deleteOnExit();
    BufferedWriter bw = new BufferedWriter(new FileWriter(ruleFile));
    bw.write("pattern\tnetype\toverwrite\tpriority\tgroup\n");
    bw.write("New York\tCITY\tO\t1.0\t0\n");
    bw.write("New\tMISC\tO\t0.5\t0\n");
    bw.close();

    Properties props = new Properties();
    props.setProperty("mapping", ruleFile.getAbsolutePath());

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("ner", props);

    CoreLabel c1 = new CoreLabel(); c1.setWord("New"); c1.setNER("O");
    CoreLabel c2 = new CoreLabel(); c2.setWord("York"); c2.setNER("O");

    List<CoreLabel> tokens = Arrays.asList(c1, c2);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation annotation = new Annotation("New York");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    assertEquals("CITY", c1.ner());
    assertEquals("CITY", c2.ner());
  }
@Test
  public void testTokenWithNullNERGetsOverwritten() throws Exception {
    File ruleFile = File.createTempFile("null_ner", ".tab");
    ruleFile.deleteOnExit();
    BufferedWriter bw = new BufferedWriter(new FileWriter(ruleFile));
    bw.write("pattern\tnetype\toverwrite\tpriority\tgroup\n");
    bw.write("London\tCITY\t\t1.0\t0\n");
    bw.close();

    Properties props = new Properties();
    props.setProperty("mapping", ruleFile.getAbsolutePath());

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("ner", props);

    CoreLabel c1 = new CoreLabel();
    c1.setWord("London");
    c1.setNER(null);

    List<CoreLabel> tokens = Arrays.asList(c1);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation doc = new Annotation("");
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);
    doc.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(doc);

    assertEquals("CITY", c1.ner());
  }
@Test
  public void testSlashInTokenPatternIsEscapedProperly() throws Exception {
    File ruleFile = File.createTempFile("slash_pattern", ".tab");
    ruleFile.deleteOnExit();
    BufferedWriter bw = new BufferedWriter(new FileWriter(ruleFile));
    bw.write("pattern\tnetype\toverwrite\tpriority\tgroup\n");
    bw.write("U.S.\\/A\tCOUNTRY\tO\t1.0\t0\n");  
    bw.close();

    Properties props = new Properties();
    props.setProperty("mapping", ruleFile.getAbsolutePath());

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("ner", props);

    CoreLabel c1 = new CoreLabel(); c1.setWord("U.S./A"); c1.setNER("O");

    List<CoreLabel> tokens = Arrays.asList(c1);
    CoreMap sentence = new ArrayCoreMap(); sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation doc = new Annotation("U.S./A");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(doc);

    assertEquals("COUNTRY", c1.ner());
  }
@Test
  public void testPatternWithNonZeroGroupIndex() throws Exception {
    File ruleFile = File.createTempFile("group_index_test", ".tab");
    ruleFile.deleteOnExit();
    BufferedWriter bw = new BufferedWriter(new FileWriter(ruleFile));
    bw.write("pattern\tnetype\toverwrite\tpriority\tgroup\n");
    bw.write("( /Hello/ (/World/) )\tGREETING\tO\t1.0\t1\n");
    bw.close();

    Properties props = new Properties();
    props.setProperty("mapping", ruleFile.getAbsolutePath());

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("ner", props);

    CoreLabel t0 = new CoreLabel(); t0.setWord("Hello"); t0.setNER("O");
    CoreLabel t1 = new CoreLabel(); t1.setWord("World"); t1.setNER("O");

    List<CoreLabel> tokens = Arrays.asList(t0, t1);
    CoreMap sentence = new ArrayCoreMap(); sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation doc = new Annotation("Hello World");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(doc);

    assertEquals("GREETING", t1.ner());
    assertEquals("O", t0.ner());  
  }
@Test
  public void testMultipleFilesWithDifferingHeaders() throws Exception {
    File f1 = File.createTempFile("file1", ".tab");
    File f2 = File.createTempFile("file2", ".tab");
    f1.deleteOnExit(); f2.deleteOnExit();

    BufferedWriter w1 = new BufferedWriter(new FileWriter(f1));
    w1.write("pattern\tner\tpriority\tgroup\n");
    w1.write("Apple\tORG\t0.8\t0\n");
    w1.close();

    BufferedWriter w2 = new BufferedWriter(new FileWriter(f2));
    w2.write("pattern\tnetype\toverwrite\tpriority\tgroup\n");
    w2.write("Google\tORG\tO\t1.0\t0\n");
    w2.close();

    String mapping = "header=pattern ner priority group," + f1.getAbsolutePath() + ";" +
                     "header=pattern netype overwrite priority group," + f2.getAbsolutePath();

    Properties props = new Properties();
    props.setProperty("mapping", mapping);

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("ner", props);

    CoreLabel a = new CoreLabel(); a.setWord("Apple"); a.setNER("O");
    CoreLabel g = new CoreLabel(); g.setWord("Google"); g.setNER("O");

    List<CoreLabel> tokens = Arrays.asList(a, g);
    CoreMap sentence = new ArrayCoreMap(); sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation doc = new Annotation("Apple Google");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(doc);

    assertEquals("ORG", a.ner());
    assertEquals("ORG", g.ner());
  }
@Test(expected = RuntimeException.class)
  public void testInvalidAnnotationFieldClassFails() throws Exception {
    File ruleFile = File.createTempFile("bad_field", ".tab");
    ruleFile.deleteOnExit();
    BufferedWriter bw = new BufferedWriter(new FileWriter(ruleFile));
    bw.write("pattern\tner\twrongField\tpriority\tgroup\n");
    bw.write("Test\tTYPE\tABC\t1.0\t0\n");
    bw.close();

    Properties props = new Properties();
    props.setProperty("mapping", ruleFile.getAbsolutePath());
    props.setProperty("mapping.header", "pattern,ner,wrongField,priority,group");

    new TokensRegexNERAnnotator("ner", props);
  }
@Test(expected = IllegalArgumentException.class)
  public void testHeaderWithDuplicateFieldThrows() throws Exception {
    File ruleFile = File.createTempFile("duplicate_header", ".tab");
    ruleFile.deleteOnExit();
    BufferedWriter bw = new BufferedWriter(new FileWriter(ruleFile));
    bw.write("pattern\tner\tpattern\tpriority\tgroup\n");
    bw.close();

    Properties props = new Properties();
    props.setProperty("mapping", ruleFile.getAbsolutePath());
    props.setProperty("mapping.header", "pattern,ner,pattern,priority,group");

    new TokensRegexNERAnnotator("ner", props);
  }
@Test(expected = IllegalArgumentException.class)
  public void testLineWithExtraColumnsThrows() throws Exception {
    File ruleFile = File.createTempFile("too_many_cols", ".tab");
    ruleFile.deleteOnExit();
    BufferedWriter bw = new BufferedWriter(new FileWriter(ruleFile));
    bw.write("pattern\tnetype\toverwrite\tpriority\tgroup\n");
    bw.write("Stanford\tORG\tO\t1.0\t0\tEXTRA\n");
    bw.close();

    Properties props = new Properties();
    props.setProperty("mapping", ruleFile.getAbsolutePath());

    new TokensRegexNERAnnotator("ner", props);
  }
@Test(expected = IllegalArgumentException.class)
  public void testLineWithTooFewColumnsThrows() throws Exception {
    File ruleFile = File.createTempFile("too_few_cols", ".tab");
    ruleFile.deleteOnExit();
    BufferedWriter bw = new BufferedWriter(new FileWriter(ruleFile));
    bw.write("pattern\tnetype\toverwrite\tpriority\tgroup\n");
    bw.write("OnlyOneColumn\n"); 
    bw.close();

    Properties props = new Properties();
    props.setProperty("mapping", ruleFile.getAbsolutePath());

    new TokensRegexNERAnnotator("ner", props);
  }
@Test(expected = IllegalArgumentException.class)
  public void testInvalidPosMatchTypeFails() throws Exception {
    File ruleFile = File.createTempFile("posmatchtype", ".tab");
    ruleFile.deleteOnExit();
    BufferedWriter bw = new BufferedWriter(new FileWriter(ruleFile));
    bw.write("pattern\tnetype\toverwrite\tpriority\tgroup\n");
    bw.write("Stanford\tORG\tO\t1.0\t0\n");
    bw.close();

    Properties props = new Properties();
    props.setProperty("mapping", ruleFile.getAbsolutePath());
    props.setProperty("posmatchtype", "NON_EXISTENT_MATCH");

    new TokensRegexNERAnnotator("ner", props);
  }
@Test
  public void testPatternGroupProducesNullGroupIsSkipped() throws Exception {
    File ruleFile = File.createTempFile("null_group_pattern", ".tab");
    ruleFile.deleteOnExit();
    BufferedWriter bw = new BufferedWriter(new FileWriter(ruleFile));
    bw.write("pattern\tnetype\toverwrite\tpriority\tgroup\n");
    bw.write("( /A/ /B/ )\tTYPE\tO\t1.0\t5\n"); 
    bw.close();

    boolean exceptionThrown = false;
    try {
      Properties props = new Properties();
      props.setProperty("mapping", ruleFile.getAbsolutePath());
      new TokensRegexNERAnnotator("ner", props);
    } catch (RuntimeException ex) {
      exceptionThrown = true;
    }
    assertTrue(exceptionThrown);
  }
@Test
  public void testTokenHasNERTypeThatCannotBeTriviallyOverwritten() throws Exception {
    File ruleFile = File.createTempFile("ner_overlap_fail", ".tab");
    ruleFile.deleteOnExit();
    BufferedWriter bw = new BufferedWriter(new FileWriter(ruleFile));
    bw.write("pattern\tnetype\toverwrite\tpriority\tgroup\n");
    bw.write("ABC\tORG\t\t1.0\t0\n");
    bw.close();

    Properties props = new Properties();
    props.setProperty("mapping", ruleFile.getAbsolutePath());

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("ner", props);

    CoreLabel c1 = new CoreLabel(); c1.setWord("ABC"); c1.setNER("PERSON");
    List<CoreLabel> tokens = Arrays.asList(c1);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation doc = new Annotation("ABC");
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);
    doc.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(doc);

    assertEquals("PERSON", c1.ner()); 
  }
@Test
  public void testMatchOneTokenPhraseOnlyRespectsSizeCondition() throws Exception {
    File ruleFile = File.createTempFile("match_one_token_phrase_only", ".tab");
    ruleFile.deleteOnExit();
    BufferedWriter bw = new BufferedWriter(new FileWriter(ruleFile));
    bw.write("pattern\tnetype\toverwrite\tpriority\tgroup\n");
    bw.write("San Jose\tCITY\tO\t1.0\t0\n");
    bw.close();

    Properties props = new Properties();
    props.setProperty("mapping", ruleFile.getAbsolutePath());
    props.setProperty("posmatchtype", "MATCH_ONE_TOKEN_PHRASE_ONLY");
    props.setProperty("validpospattern", "NNP");

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("ner", props);

    CoreLabel c1 = new CoreLabel(); c1.setWord("San"); c1.setNER("O"); c1.setTag("DT");
    CoreLabel c2 = new CoreLabel(); c2.setWord("Jose"); c2.setNER("O"); c2.setTag("NNP");

    List<CoreLabel> tokens = Arrays.asList(c1, c2);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation doc = new Annotation("San Jose");
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);
    doc.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(doc);

    assertEquals("O", c1.ner());
    assertEquals("O", c2.ner());
  }
@Test
  public void testOldNERPhraseCompletelyCoversPatternAndSkipsMatch() throws Exception {
    File ruleFile = File.createTempFile("cut_through_phrase", ".tab");
    ruleFile.deleteOnExit();
    BufferedWriter bw = new BufferedWriter(new FileWriter(ruleFile));
    bw.write("pattern\tnetype\toverwrite\tpriority\tgroup\n");
    bw.write("Barack Obama\tPERSON\t\t1.0\t0\n");
    bw.close();

    Properties props = new Properties();
    props.setProperty("mapping", ruleFile.getAbsolutePath());

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("ner", props);

    CoreLabel t1 = new CoreLabel(); t1.setWord("Barack"); t1.setNER("PERSON");
    CoreLabel t2 = new CoreLabel(); t2.setWord("Obama"); t2.setNER("PERSON");
    CoreLabel t3 = new CoreLabel(); t3.setWord("visited"); t3.setNER("O");

    List<CoreLabel> tokens = Arrays.asList(t1, t2, t3);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation doc = new Annotation("Barack Obama visited");
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);
    doc.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(doc);

    assertEquals("PERSON", t1.ner());
    assertEquals("PERSON", t2.ner());
  }
@Test
  public void testNullBackgroundSymbolStillOverwritesTokenWithNull() throws Exception {
    File ruleFile = File.createTempFile("null_background", ".tab");
    ruleFile.deleteOnExit();
    BufferedWriter bw = new BufferedWriter(new FileWriter(ruleFile));
    bw.write("pattern\tnetype\toverwrite\tpriority\tgroup\n");
    bw.write("Laptop\tDEVICE\t\t1.0\t0\n");
    bw.close();

    Properties props = new Properties();
    props.setProperty("mapping", ruleFile.getAbsolutePath());
    props.setProperty("backgroundSymbol", "");

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("ner", props);

    CoreLabel token = new CoreLabel();
    token.setWord("Laptop");
    token.setNER(null); 

    List<CoreLabel> tokens = Arrays.asList(token);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation doc = new Annotation("Laptop");
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);
    doc.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(doc);

    assertEquals("DEVICE", token.ner()); 
  }
@Test
  public void testAnnotationWithEmptyTokenListDoesNotThrow() throws Exception {
    Properties props = new Properties();
    File ruleFile = File.createTempFile("empty_tokens_test", ".tab");
    ruleFile.deleteOnExit();
    BufferedWriter writer = new BufferedWriter(new FileWriter(ruleFile));
    writer.write("pattern\tnetype\toverwrite\tpriority\tgroup\n");
    writer.write("Stanford\tORG\tO\t1.0\t0\n");
    writer.close();

    props.setProperty("mapping", ruleFile.getAbsolutePath());

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("ner", props);

    Annotation doc = new Annotation("");
    List<CoreLabel> tokens = new ArrayList<>();
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);
    doc.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

    annotator.annotate(doc);
    
    assertTrue(doc.containsKey(CoreAnnotations.TokensAnnotation.class));
  }
@Test
  public void testInvalidPerFileOptionIgnoredSilently() throws Exception {
    File ruleFile = File.createTempFile("invalid_option", ".tab");
    ruleFile.deleteOnExit();
    BufferedWriter bw = new BufferedWriter(new FileWriter(ruleFile));
    bw.write("pattern\tnetype\toverwrite\tpriority\tgroup\n");
    bw.write("Test\tTYPE\tO\t1.0\t0\n");
    bw.close();

    String mapping = "invalidkey=true, " + ruleFile.getAbsolutePath();
    Properties props = new Properties();
    props.setProperty("mapping", mapping);

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("ner", props);

    CoreLabel t = new CoreLabel(); t.setWord("Test"); t.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreLabel> tokens = Arrays.asList(t);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation doc = new Annotation("Test");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(doc);

    assertEquals("TYPE", t.ner());
  }
@Test
  public void testPatternWithEscapedTabOrSpaces() throws Exception {
    File ruleFile = File.createTempFile("escaped_chars", ".tab");
    ruleFile.deleteOnExit();
    BufferedWriter bw = new BufferedWriter(new FileWriter(ruleFile));
    bw.write("pattern\tnetype\toverwrite\tpriority\tgroup\n");
    bw.write("New\\sYork\tCITY\tO\t1.0\t0\n");
    bw.close();

    Properties props = new Properties();
    props.setProperty("mapping", ruleFile.getAbsolutePath());

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("ner", props);

    CoreLabel c1 = new CoreLabel(); c1.setWord("New"); c1.setNER("O");
    CoreLabel c2 = new CoreLabel(); c2.setWord("York"); c2.setNER("O");

    List<CoreLabel> tokens = Arrays.asList(c1, c2);
    CoreMap sentence = new ArrayCoreMap(); sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation doc = new Annotation("New York");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(doc);

    assertEquals("CITY", c1.ner());
    assertEquals("CITY", c2.ner());
  }
@Test
  public void testDuplicateEntryWithSameTypeSkippedQuietly() throws Exception {
    File ruleFile = File.createTempFile("dup_same_type", ".tab");
    ruleFile.deleteOnExit();
    BufferedWriter writer = new BufferedWriter(new FileWriter(ruleFile));
    writer.write("pattern\tnetype\toverwrite\tpriority\tgroup\n");
    writer.write("Stanford\tORG\tO\t1.0\t0\n");
    writer.write("Stanford\tORG\tO\t1.0\t0\n");  
    writer.close();

    Properties props = new Properties();
    props.setProperty("mapping", ruleFile.getAbsolutePath());

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("ner", props);

    CoreLabel token = new CoreLabel();
    token.setWord("Stanford");
    token.setNER("O");

    List<CoreLabel> tokens = Arrays.asList(token);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation doc = new Annotation("Stanford");
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);
    doc.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));

    annotator.annotate(doc);

    assertEquals("ORG", token.ner());
  }
@Test
  public void testNoGroupAnnotationWhenGroupIsNegative() throws Exception {
    File ruleFile = File.createTempFile("negative_group", ".tab");
    ruleFile.deleteOnExit();

    BufferedWriter writer = new BufferedWriter(new FileWriter(ruleFile));
    writer.write("pattern\tnetype\toverwrite\tpriority\tgroup\n");
    writer.write("( /A/ /B/ )\tTYPE\tO\t1.0\t-1\n");
    writer.close();

    boolean failedAsExpected = false;
    try {
      Properties props = new Properties();
      props.setProperty("mapping", ruleFile.getAbsolutePath());
      new TokensRegexNERAnnotator("ner", props);
    } catch (RuntimeException e) {
      failedAsExpected = true;
    }

    assertTrue(failedAsExpected);
  }
@Test
  public void testMissingTokensFieldThrows() throws Exception {
    File ruleFile = File.createTempFile("missing_tokens_key", ".tab");
    ruleFile.deleteOnExit();
    BufferedWriter bw = new BufferedWriter(new FileWriter(ruleFile));
    bw.write("pattern\tnetype\toverwrite\tpriority\tgroup\n");
    bw.write("Missing\tTAG\tO\t1.0\t0\n");
    bw.close();

    Properties props = new Properties();
    props.setProperty("mapping", ruleFile.getAbsolutePath());

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("ner", props);

    Annotation doc = new Annotation("Missing");
    

    boolean threw = false;
    try {
      annotator.annotate(doc);
    } catch (RuntimeException ex) {
      threw = true;
    }
    assertTrue(threw);
  }
@Test
  public void testWeightFieldParsedAndAccepted() throws Exception {
    File ruleFile = File.createTempFile("with_weight", ".tab");
    ruleFile.deleteOnExit();
    BufferedWriter writer = new BufferedWriter(new FileWriter(ruleFile));
    writer.write("pattern\tnetype\toverwrite\tpriority\tweight\tgroup\n");
    writer.write("Google\tORG\tO\t1.0\t5.0\t0\n");
    writer.close();

    Properties props = new Properties();
    props.setProperty("mapping", ruleFile.getAbsolutePath());
    props.setProperty("mapping.header", "pattern,netype,overwrite,priority,weight,group");

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("ner", props);

    CoreLabel token = new CoreLabel(); token.setWord("Google"); token.setNER("O");

    List<CoreLabel> tokens = Arrays.asList(token);
    CoreMap sentence = new ArrayCoreMap(); sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation doc = new Annotation("Google");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(doc);

    assertEquals("ORG", token.ner());
  }
@Test
  public void testMultipleAnnotationFieldsPopulateCorrectly() throws Exception {
    File ruleFile = File.createTempFile("multi_annot", ".tab");
    ruleFile.deleteOnExit();

    BufferedWriter writer = new BufferedWriter(new FileWriter(ruleFile));
    writer.write("pattern\tner\tcategory\tpriority\tgroup\n");
    writer.write("Tesla\tORG\tCarMaker\t1.0\t0\n");
    writer.close();

    Properties props = new Properties();
    props.setProperty("mapping", ruleFile.getAbsolutePath());
    props.setProperty("mapping.header", "pattern,ner,category,priority,group");
    props.setProperty("mapping.field.category", "edu.stanford.nlp.ling.CoreAnnotations$NormalizedNamedEntityTagAnnotation");

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("ner", props);

    CoreLabel token = new CoreLabel(); token.setWord("Tesla"); token.setNER("O");

    List<CoreLabel> tokens = Arrays.asList(token);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation doc = new Annotation("Tesla");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(doc);

    assertEquals("ORG", token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
    assertEquals("CarMaker", token.get(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class));
  }
@Test
  public void testTokensRegexWithQuotedPattern() throws Exception {
    File ruleFile = File.createTempFile("quoted_pattern", ".tab");
    ruleFile.deleteOnExit();

    BufferedWriter writer = new BufferedWriter(new FileWriter(ruleFile));
    writer.write("pattern\tnetype\toverwrite\tpriority\tgroup\n");
    writer.write("( /\"The/ /Stanford/ /Group\"/ )\tORG\tO\t1.0\t0\n");
    writer.close();

    Properties props = new Properties();
    props.setProperty("mapping", ruleFile.getAbsolutePath());

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("ner", props);

    CoreLabel t0 = new CoreLabel(); t0.setWord("\"The"); t0.setNER("O");
    CoreLabel t1 = new CoreLabel(); t1.setWord("Stanford"); t1.setNER("O");
    CoreLabel t2 = new CoreLabel(); t2.setWord("Group\""); t2.setNER("O");

    List<CoreLabel> tokens = Arrays.asList(t0, t1, t2);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation doc = new Annotation("quoted");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(doc);

    assertEquals("ORG", t0.ner());
    assertEquals("ORG", t1.ner());
    assertEquals("ORG", t2.ner());
  }
@Test(expected = RuntimeException.class)
  public void testMappingFileWithInvalidGroupIndexTooLarge() throws Exception {
    File ruleFile = File.createTempFile("bad_group", ".tab");
    ruleFile.deleteOnExit();

    BufferedWriter writer = new BufferedWriter(new FileWriter(ruleFile));
    writer.write("pattern\tnetype\toverwrite\tpriority\tgroup\n");
    writer.write("( /Hello/ /World/ )\tGREETING\tO\t1.0\t4\n"); 
    writer.close();

    Properties props = new Properties();
    props.setProperty("mapping", ruleFile.getAbsolutePath());

    new TokensRegexNERAnnotator("ner", props); 
  }
@Test
  public void testMultipleBackgroundSymbolsIncluded() throws Exception {
    File ruleFile = File.createTempFile("multi_background", ".tab");
    ruleFile.deleteOnExit();

    BufferedWriter writer = new BufferedWriter(new FileWriter(ruleFile));
    writer.write("pattern\tnetype\toverwrite\tpriority\tgroup\n");
    writer.write("Apple\tORG\t\t1.0\t0\n");
    writer.close();

    Properties props = new Properties();
    props.setProperty("mapping", ruleFile.getAbsolutePath());
    props.setProperty("backgroundSymbol", "O,MISC,NONE");

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("ner", props);

    CoreLabel t = new CoreLabel(); t.setWord("Apple"); t.setNER("NONE");

    List<CoreLabel> tokens = Arrays.asList(t);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation doc = new Annotation("Apple");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(doc);

    assertEquals("ORG", t.ner());
  }
@Test
  public void testRegexWithHigherPriorityOverridesLowerPriorityMatch() throws Exception {
    File ruleFile = File.createTempFile("priority_conflict", ".tab");
    ruleFile.deleteOnExit();

    BufferedWriter writer = new BufferedWriter(new FileWriter(ruleFile));
    writer.write("pattern\tnetype\toverwrite\tpriority\tgroup\n");
    writer.write("Tesla\tCAR_BRAND\tO\t0.5\t0\n");
    writer.write("Tesla\tCOMPANY\tO\t5.0\t0\n"); 
    writer.close();

    Properties props = new Properties();
    props.setProperty("mapping", ruleFile.getAbsolutePath());

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("ner", props);

    CoreLabel t = new CoreLabel(); t.setWord("Tesla"); t.setNER("O");

    List<CoreLabel> tokens = Arrays.asList(t);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation doc = new Annotation("Tesla");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(doc);

    assertEquals("COMPANY", t.ner());
  }
@Test
  public void testPatternWithAnnotationGroupOne() throws Exception {
    File ruleFile = File.createTempFile("group_1_pattern", ".tab");
    ruleFile.deleteOnExit();

    BufferedWriter writer = new BufferedWriter(new FileWriter(ruleFile));
    writer.write("pattern\tnetype\toverwrite\tpriority\tgroup\n");
    writer.write("( /The/ (/Company/) )\tORG\tO\t1.0\t1\n"); 
    writer.close();

    Properties props = new Properties();
    props.setProperty("mapping", ruleFile.getAbsolutePath());

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("ner", props);

    CoreLabel t0 = new CoreLabel(); t0.setWord("The"); t0.setNER("O");
    CoreLabel t1 = new CoreLabel(); t1.setWord("Company"); t1.setNER("O");

    List<CoreLabel> tokens = Arrays.asList(t0, t1);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation doc = new Annotation("The Company");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(doc);

    assertEquals("O", t0.ner());
    assertEquals("ORG", t1.ner());
  }
@Test
  public void testValidPosPatternCombinedWithIgnoreCase() throws Exception {
    File ruleFile = File.createTempFile("pos_ignorecase", ".tab");
    ruleFile.deleteOnExit();

    BufferedWriter writer = new BufferedWriter(new FileWriter(ruleFile));
    writer.write("pattern\tnetype\toverwrite\tpriority\tgroup\n");
    writer.write("UN\tORG\tO\t1.0\t0\n");
    writer.close();

    Properties props = new Properties();
    props.setProperty("mapping", ruleFile.getAbsolutePath());
    props.setProperty("ignorecase", "true");
    props.setProperty("validpospattern", "NNP");

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("ner", props);

    CoreLabel token = new CoreLabel();
    token.setWord("un"); 
    token.setTag("NNP");
    token.setNER("O");

    List<CoreLabel> tokens = Arrays.asList(token);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation annotation = new Annotation("un");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(annotation);

    assertEquals("ORG", token.ner());
  }
@Test
  public void testTokenWithMismatchedPOSAndNullNERNotAnnotated() throws Exception {
    File ruleFile = File.createTempFile("pos_fail", ".tab");
    ruleFile.deleteOnExit();

    BufferedWriter writer = new BufferedWriter(new FileWriter(ruleFile));
    writer.write("pattern\tnetype\toverwrite\tpriority\tgroup\n");
    writer.write("NASA\tORG\tO\t1.0\t0\n");
    writer.close();

    Properties props = new Properties();
    props.setProperty("mapping", ruleFile.getAbsolutePath());
    props.setProperty("validpospattern", "NNP");
    props.setProperty("posmatchtype", "MATCH_ALL_TOKENS");

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("ner", props);

    CoreLabel c = new CoreLabel();
    c.setWord("NASA");
    c.setTag("VBZ");
    c.setNER(null);

    List<CoreLabel> tokens = Arrays.asList(c);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation doc = new Annotation("NASA");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(doc);

    assertNull(c.ner());
  }
@Test
  public void testMappingEntryUsesDefaultHeaderWhenNoneDefined() throws Exception {
    File mappingFile = File.createTempFile("default_header_case", ".tab");
    mappingFile.deleteOnExit();
    BufferedWriter writer = new BufferedWriter(new FileWriter(mappingFile));
    writer.write("Google\tORG\n");
    writer.close();

    Properties props = new Properties();
    props.setProperty("mapping", mappingFile.getAbsolutePath());

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("regex", props);

    CoreLabel token = new CoreLabel();
    token.setWord("Google");
    token.setNER("O");

    List<CoreLabel> tokens = Collections.singletonList(token);
    CoreMap sent = new ArrayCoreMap();
    sent.set(CoreAnnotations.TokensAnnotation.class, tokens);
    Annotation doc = new Annotation("Google");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sent));
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(doc);

    assertEquals("ORG", token.ner());
  }
@Test
  public void testMultipleAnnotationFieldsProcessedFromHeader() throws Exception {
    File mappingFile = File.createTempFile("multi_field_ann", ".tab");
    mappingFile.deleteOnExit();
    BufferedWriter writer = new BufferedWriter(new FileWriter(mappingFile));
    writer.write("pattern\tner\tcustom\tpriority\tgroup\n");
    writer.write("Microsoft\tORG\tTECH\t1.0\t0\n");
    writer.close();

    Properties props = new Properties();
    props.setProperty("mapping", mappingFile.getAbsolutePath());
    props.setProperty("mapping.header", "pattern,ner,custom,priority,group");
    props.setProperty("mapping.field.custom", "edu.stanford.nlp.ling.CoreAnnotations$NormalizedNamedEntityTagAnnotation");

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("regex", props);

    CoreLabel token = new CoreLabel();
    token.setWord("Microsoft");
    token.setNER("O");

    List<CoreLabel> tokens = Collections.singletonList(token);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    Annotation doc = new Annotation("Microsoft");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(doc);

    assertEquals("ORG", token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
    assertEquals("TECH", token.get(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class));
  }
@Test
  public void testOverwriteAllowedWithConsistentNERAndOverwriteDefined() throws Exception {
    File ruleFile = File.createTempFile("overwrite_match", ".tab");
    ruleFile.deleteOnExit();
    BufferedWriter writer = new BufferedWriter(new FileWriter(ruleFile));
    writer.write("pattern\tnetype\toverwrite\tpriority\tgroup\n");
    writer.write("IBM\tCOMPANY\tORG,LOCATION,MISC\t1.0\t0\n");
    writer.close();

    Properties props = new Properties();
    props.setProperty("mapping", ruleFile.getAbsolutePath());

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("regex", props);

    CoreLabel token = new CoreLabel(); token.setWord("IBM"); token.setNER("ORG");

    List<CoreLabel> tokens = Collections.singletonList(token);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    Annotation doc = new Annotation("IBM");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotator.annotate(doc);
    assertEquals("COMPANY", token.ner());
  }
@Test
  public void testValidPosPatternDoesNotBlockAnnotationIfNoPOSAvailable() throws Exception {
    File ruleFile = File.createTempFile("no_pos_tag", ".tab");
    ruleFile.deleteOnExit();
    BufferedWriter w = new BufferedWriter(new FileWriter(ruleFile));
    w.write("pattern\tnetype\toverwrite\tpriority\tgroup\n");
    w.write("Python\tLANG\tO\t1.0\t0\n");
    w.close();

    Properties props = new Properties();
    props.setProperty("mapping", ruleFile.getAbsolutePath());
    props.setProperty("posmatchtype", "MATCH_AT_LEAST_ONE_TOKEN");
    props.setProperty("validpospattern", "NNP");

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("regex", props);

    CoreLabel token = new CoreLabel();
    token.setWord("Python");
    token.setNER("O"); 

    List<CoreLabel> list = Collections.singletonList(token);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, list);

    Annotation doc = new Annotation("Python");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    doc.set(CoreAnnotations.TokensAnnotation.class, list);

    annotator.annotate(doc);
    assertEquals("LANG", token.ner());
  }
@Test
  public void testRegexTokenSplitWithMultipleWhitespaces() throws Exception {
    File ruleFile = File.createTempFile("whitespace_regex", ".tab");
    ruleFile.deleteOnExit();
    BufferedWriter w = new BufferedWriter(new FileWriter(ruleFile));
    w.write("pattern\tnetype\toverwrite\tpriority\tgroup\n");
    w.write("New     York\tCITY\tO\t1.0\t0\n");  
    w.close();

    Properties props = new Properties();
    props.setProperty("mapping", ruleFile.getAbsolutePath());

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("regex", props);

    CoreLabel t1 = new CoreLabel(); t1.setWord("New"); t1.setNER("O");
    CoreLabel t2 = new CoreLabel(); t2.setWord("York"); t2.setNER("O");

    List<CoreLabel> tokens = Arrays.asList(t1, t2);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation doc = new Annotation("New York");
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);
    doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(doc);

    assertEquals("CITY", t1.ner());
    assertEquals("CITY", t2.ner());
  }
@Test
  public void testPatternWithTrailingWhitespaceIsTrimmedDuringParsing() throws Exception {
    File ruleFile = File.createTempFile("trailing_space", ".tab");
    ruleFile.deleteOnExit();
    BufferedWriter w = new BufferedWriter(new FileWriter(ruleFile));
    w.write("pattern\tnetype\toverwrite\tpriority\tgroup\n");
    w.write("London\tCITY\tO\t1.0\t0   \n"); 
    w.close();

    Properties props = new Properties();
    props.setProperty("mapping", ruleFile.getAbsolutePath());

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("regex", props);

    CoreLabel token = new CoreLabel(); token.setWord("London"); token.setNER("O");

    List<CoreLabel> tokens = Collections.singletonList(token);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Annotation doc = new Annotation("London");
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);
    doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(doc);
    assertEquals("CITY", token.ner());
  }
@Test
  public void testRepeatedPatternWithDifferentWeightAccepted() throws Exception {
    File ruleFile = File.createTempFile("weight_diff", ".tab");
    ruleFile.deleteOnExit();
    BufferedWriter w = new BufferedWriter(new FileWriter(ruleFile));
    w.write("pattern\tnetype\toverwrite\tpriority\tweight\tgroup\n");
    w.write("Tesla\tCAR\tO\t5.0\t1.0\t0\n");
    w.write("Tesla\tCAR\tO\t5.0\t2.0\t0\n");  
    w.close();

    Properties props = new Properties();
    props.setProperty("mapping", ruleFile.getAbsolutePath());
    props.setProperty("mapping.header", "pattern,netype,overwrite,priority,weight,group");

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("regex", props);

    CoreLabel token = new CoreLabel(); token.setWord("Tesla"); token.setNER("O");
    List<CoreLabel> list = Collections.singletonList(token);
    CoreMap sentence = new ArrayCoreMap(); sentence.set(CoreAnnotations.TokensAnnotation.class, list);

    Annotation doc = new Annotation("Tesla");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    doc.set(CoreAnnotations.TokensAnnotation.class, list);

    annotator.annotate(doc);
    assertEquals("CAR", token.ner());
  }
@Test
  public void testEmptyOverwriteColumnAllowsDefaultTypeOverwrite() throws Exception {
    File ruleFile = File.createTempFile("empty_overwrite", ".tab");
    ruleFile.deleteOnExit();
    BufferedWriter w = new BufferedWriter(new FileWriter(ruleFile));
    w.write("pattern\tnetype\toverwrite\tpriority\tgroup\n");
    w.write("Paris\tCITY\t\t1.0\t0\n");  
    w.close();

    Properties props = new Properties();
    props.setProperty("mapping", ruleFile.getAbsolutePath());

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("regex", props);

    CoreLabel token = new CoreLabel(); token.setWord("Paris"); token.setNER("O");

    List<CoreLabel> t = Collections.singletonList(token);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, t);
    Annotation doc = new Annotation("Paris");
    doc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    doc.set(CoreAnnotations.TokensAnnotation.class, t);

    annotator.annotate(doc);
    assertEquals("CITY", token.ner());
  }
@Test
  public void testAnnotationOnTokenOnlyWithoutSentences() throws Exception {
    File ruleFile = File.createTempFile("no_sent", ".tab");
    ruleFile.deleteOnExit();
    BufferedWriter writer = new BufferedWriter(new FileWriter(ruleFile));
    writer.write("pattern\tnetype\toverwrite\tpriority\tgroup\n");
    writer.write("Elon\tPERSON\tO\t1.0\t0\n");
    writer.close();

    Properties props = new Properties();
    props.setProperty("mapping", ruleFile.getAbsolutePath());

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("regex", props);

    CoreLabel token = new CoreLabel();
    token.setWord("Elon");
    token.setNER("O");

    List<CoreLabel> tokens = Collections.singletonList(token);
    Annotation doc = new Annotation("Elon");
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);
    

    annotator.annotate(doc);

    assertEquals("PERSON", token.ner());
  } 
}