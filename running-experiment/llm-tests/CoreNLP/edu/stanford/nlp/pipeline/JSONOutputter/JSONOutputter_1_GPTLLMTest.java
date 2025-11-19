package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.ie.machinereading.structure.Span;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.time.Timex;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class JSONOutputter_1_GPTLLMTest {

 @Test
  public void testJsonPrintMinimalDocument() throws Exception {
    Annotation annotation = new Annotation("Hello world.");
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "0001");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Hello world.");

    String json = JSONOutputter.jsonPrint(annotation);
    assertTrue(json.contains("\"docId\":\"0001\""));
    assertTrue(json.contains("\"text\":\"Hello world.\""));
  }
@Test
  public void testJsonPrintTokenOnly() throws Exception {
    Annotation annotation = new Annotation("Stanford");

    CoreLabel token = new CoreLabel();
    token.setWord("Stanford");
    token.setOriginalText("Stanford");
    token.setIndex(1);
    token.setBeginPosition(0);
    token.setEndPosition(8);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    String json = JSONOutputter.jsonPrint(annotation);
    assertTrue(json.contains("\"word\":\"Stanford\""));
    assertTrue(json.contains("\"index\":1"));
  }
@Test
  public void testJsonIncludesSentenceLevelData() throws Exception {
    Annotation annotation = new Annotation("This is a test.");

    CoreMap sentence = new Annotation("This is a test.");
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TextAnnotation.class, "This is a test.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    String json = JSONOutputter.jsonPrint(annotation);
    assertTrue(json.contains("\"sentences\""));
    assertTrue(json.contains("\"index\":0"));
  }
@Test
  public void testHandlesParseTree() throws Exception {
    Annotation annotation = new Annotation("Word");

    CoreMap sentence = new Annotation("Word");
    Tree tree = Tree.valueOf("(ROOT (NN Word))");
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    JSONOutputter.Options options = new JSONOutputter.Options();
//    options.pretty = false;
//    options.includeText = true;

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    JSONOutputter outputter = new JSONOutputter();
    outputter.print(annotation, os, options);

    String result = os.toString();
    assertTrue(result.contains("\"parse\":\"(ROOT (NN Word))\""));
  }
@Test
  public void testPrintIncludesSentimentTree() throws Exception {
    Annotation annotation = new Annotation("Example");

    CoreMap sentence = new Annotation("Example");
    Tree sentimentTree = Tree.valueOf("(3 (2 bad) (4 good))");
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(SentimentCoreAnnotations.SentimentAnnotatedTree.class, sentimentTree);
    sentence.set(SentimentCoreAnnotations.SentimentClass.class, "Positive");

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    String json = JSONOutputter.jsonPrint(annotation);
    assertTrue(json.contains("\"sentiment\":\"Positive\"") || json.contains("\"sentiment\":\"Positive\""));
  }
@Test
  public void testHandlesEmptyAnnotation() throws Exception {
    Annotation annotation = new Annotation("");

    JSONOutputter.Options options = new JSONOutputter.Options();
//    options.pretty = false;
//    options.includeText = true;

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    JSONOutputter outputter = new JSONOutputter();
    outputter.print(annotation, os, options);

    String result = os.toString();
    assertNotNull(result);
    assertTrue(result.startsWith("{"));
    assertTrue(result.endsWith("}"));
  }
@Test
  public void testDependencyTreeSerialization() throws Exception {
    Annotation annotation = new Annotation("Text");

    CoreMap sentence = new Annotation("Text");
    SemanticGraph graph = new SemanticGraph();
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class, graph);
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    String json = JSONOutputter.jsonPrint(annotation);
    assertTrue(json.contains("\"basicDependencies\""));
  }
@Test
  public void testNullValuesAreIgnored() throws Exception {
    Annotation annotation = new Annotation("Some text");
    annotation.set(CoreAnnotations.AuthorAnnotation.class, null);

    String json = JSONOutputter.jsonPrint(annotation);
    assertFalse(json.contains("author"));
  }
@Test
  public void testQuoteAnnotationsEmpty() throws Exception {
    Annotation annotation = new Annotation("He said something.");
    annotation.set(CoreAnnotations.QuotationsAnnotation.class, new ArrayList<>());

    String json = JSONOutputter.jsonPrint(annotation);
    assertTrue(json.contains("\"quotes\":[]"));
  }
@Test
  public void testCorefChainEmptyMap() throws Exception {
    Annotation annotation = new Annotation("Sample text.");
    Map<Integer, CorefChain> corefMap = new HashMap<>();
    annotation.set(edu.stanford.nlp.coref.CorefCoreAnnotations.CorefChainAnnotation.class, corefMap);

    String json = JSONOutputter.jsonPrint(annotation);
    assertTrue(json.contains("\"corefs\":{}"));
  }
@Test
  public void testPrintWithCustomOutputStream() throws Exception {
    Annotation annotation = new Annotation("Custom OS");
    ByteArrayOutputStream os = new ByteArrayOutputStream();

    JSONOutputter.jsonPrint(annotation, os);
    String output = os.toString();
    assertTrue(output.contains("Custom OS"));
  }
@Test
  public void testJSONWriterObjectToJsonAPI() throws Exception {
    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set("name", "testName");
      List<Integer> numbers = new ArrayList<>();
      numbers.add(1);
      numbers.add(2);
      numbers.add(3);
      writer.set("values", numbers);
    });

    assertTrue(json.contains("\"name\":\"testName\""));
    assertTrue(json.contains("[1,2,3]") || json.contains("[ 1, 2, 3 ]"));
  }
@Test
  public void testWriteNullValueDoesNotCrash() throws Exception {
    Annotation annotation = new Annotation("test");

    CoreMap sentence = new Annotation();
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokensAnnotation.class, null);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    JSONOutputter.Options options = new JSONOutputter.Options();
//    options.includeText = true;

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    JSONOutputter outputter = new JSONOutputter();
    outputter.print(annotation, os, options);

    String json = os.toString();
    assertTrue(json.contains("\"sentences\""));
  }
@Test
  public void testMultipleSentencePrint() throws Exception {
    Annotation annotation = new Annotation("Hello World");

    CoreMap first = new Annotation("Hello");
    first.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    CoreMap second = new Annotation("World");
    second.set(CoreAnnotations.SentenceIndexAnnotation.class, 1);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(first);
    sentences.add(second);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    String json = JSONOutputter.jsonPrint(annotation);
    assertTrue(json.contains("\"index\":0"));
    assertTrue(json.contains("\"index\":1"));
  }
@Test
public void testPrintAnnotationWithEntityMentionIncludingNERConfidence() throws Exception {
  Annotation annotation = new Annotation("John went to Paris.");

  CoreMap sentence = new Annotation("John went to Paris.");
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

  CoreLabel entityMention = new CoreLabel();
  entityMention.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
  entityMention.set(CoreAnnotations.TokenEndAnnotation.class, 1);
  entityMention.set(CoreAnnotations.TextAnnotation.class, "John");
  entityMention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  entityMention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
  entityMention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
  entityMention.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "John");
  entityMention.set(CoreAnnotations.WikipediaEntityAnnotation.class, "John_(person)");

  Map<String, Double> nerProbs = new HashMap<>();
  nerProbs.put("PERSON", 0.92);
  nerProbs.put("O", 0.08); 
  entityMention.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, nerProbs);

  List<CoreMap> mentions = new ArrayList<>();
  mentions.add(entityMention);

  sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
  sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"entitymentions\""));
  assertTrue(json.contains("\"nerConfidences\""));
  assertTrue(json.contains("\"PERSON\":0.92"));
  assertFalse(json.contains("\"O\""));
}
@Test
public void testPrintTimexInToken() throws Exception {
  Annotation annotation = new Annotation("March 15, 2020");

  CoreLabel token = new CoreLabel();
  token.setWord("March");
  token.setOriginalText("March");
  token.setIndex(1);
  token.setBeginPosition(0);
  token.setEndPosition(5);
  token.set(CoreAnnotations.TokensAnnotation.class, null);
  token.set(CoreAnnotations.SpeakerAnnotation.class, "Speaker1");

//  Timex timex = new Timex("t1", "DATE", "2020-03-15");
//  token.set(edu.stanford.nlp.time.TimeAnnotations.TimexAnnotation.class, timex);

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);

  CoreMap sentence = new Annotation("March 15, 2020");
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"timex\""));
  assertTrue(json.contains("\"type\":\"DATE\""));
  assertTrue(json.contains("\"value\":\"2020-03-15\""));
}
@Test
public void testPrintWithInvalidParseTreeString() throws Exception {
  Annotation annotation = new Annotation("Invalid tree");

  Tree tree = Tree.valueOf("(ROOT (NN SENTENCE_SKIPPED_OR_UNPARSABLE))");

  CoreMap sentence = new Annotation("Invalid tree");
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
  sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  String json = JSONOutputter.jsonPrint(annotation);
  assertFalse(json.contains("\"parse\""));
}
@Test
public void testTokenWithCodepointOffsets() throws Exception {
  Annotation annotation = new Annotation("👋 Hello");

  CoreLabel token = new CoreLabel();
  token.setWord("👋");
  token.setOriginalText("👋");
  token.setIndex(1);
  token.setBeginPosition(0);
  token.setEndPosition(2);
  token.set(CoreAnnotations.CodepointOffsetBeginAnnotation.class, 0);
  token.set(CoreAnnotations.CodepointOffsetEndAnnotation.class, 1);

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);

  CoreMap sentence = new Annotation("👋 Hello");
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"codepointOffsetBegin\":0"));
  assertTrue(json.contains("\"codepointOffsetEnd\":1"));
}
@Test
public void testDefaultTreePrinterReplacement() throws Exception {
  Annotation annotation = new Annotation("Tree test");

  Tree tree = Tree.valueOf("(ROOT (NN Test))");

  CoreMap sentence = new Annotation("Tree test");
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
  sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  JSONOutputter.Options options = new JSONOutputter.Options();
//  options.constituencyTreePrinter = AnnotationOutputter.DEFAULT_CONSTITUENCY_TREE_PRINTER;

  ByteArrayOutputStream os = new ByteArrayOutputStream();
  new JSONOutputter().print(annotation, os, options);
  String output = os.toString();

  assertTrue(output.contains("\"parse\":\"(ROOT (NN Test))\""));
}
@Test
public void testMissingSentenceTokensAndMentions() throws Exception {
  Annotation annotation = new Annotation("Text");

  CoreMap sentence = new Annotation("Text");
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  sentence.set(CoreAnnotations.TokensAnnotation.class, null);
  sentence.set(CoreAnnotations.MentionsAnnotation.class, null);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  ByteArrayOutputStream os = new ByteArrayOutputStream();
  JSONOutputter.Options options = new JSONOutputter.Options();
  new JSONOutputter().print(annotation, os, options);

  String json = os.toString();
  assertTrue(json.contains("\"sentences\""));
}
@Test
public void testCorefChainWithTwoMentionsWithDifferentTypes() throws Exception {
  Annotation annotation = new Annotation("Example");

//  CorefChain.CorefMention mention1 = new CorefChain.CorefMention(1, 1, 1, 2, 1, "John", "PROPER", "SINGULAR", "MALE", "ANIMATE", new int[]{1, 2});
//  CorefChain.CorefMention mention2 = new CorefChain.CorefMention(2, 2, 3, 4, 3, "he", "PRONOMINAL", "SINGULAR", "MALE", "ANIMATE", new int[]{3, 4});
//  List<CorefChain.CorefMention> mentions = Arrays.asList(mention1, mention2);
//  CorefChain chain = new CorefChain(1, mentions);

  Map<Integer, CorefChain> corefs = new HashMap<>();
//  corefs.put(1, chain);
  annotation.set(edu.stanford.nlp.coref.CorefCoreAnnotations.CorefChainAnnotation.class, corefs);

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"type\":\"PROPER\""));
  assertTrue(json.contains("\"type\":\"PRONOMINAL\""));
  assertTrue(json.contains("\"isRepresentativeMention\":true") || json.contains("\"isRepresentativeMention\":false"));
}
@Test
public void testQuotesWithAttributionFields() throws Exception {
  Annotation annotation = new Annotation("“I’ll go,” said Alice.");

  CoreMap quote = new Annotation("I’ll go");
  quote.set(CoreAnnotations.QuotationIndexAnnotation.class, 1);
  quote.set(CoreAnnotations.TextAnnotation.class, "I’ll go");
  quote.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  quote.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);
  quote.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
  quote.set(CoreAnnotations.TokenEndAnnotation.class, 3);
  quote.set(CoreAnnotations.SentenceBeginAnnotation.class, 0);
  quote.set(CoreAnnotations.SentenceEndAnnotation.class, 0);
  quote.set(QuoteAttributionAnnotator.MentionAnnotation.class, "Alice");
  quote.set(QuoteAttributionAnnotator.MentionBeginAnnotation.class, 4);
  quote.set(QuoteAttributionAnnotator.MentionEndAnnotation.class, 5);
  quote.set(QuoteAttributionAnnotator.MentionTypeAnnotation.class, "NAMED");
  quote.set(QuoteAttributionAnnotator.MentionSieveAnnotation.class, "RULE");
  quote.set(QuoteAttributionAnnotator.SpeakerAnnotation.class, "Alice");
  quote.set(QuoteAttributionAnnotator.SpeakerSieveAnnotation.class, "RULE");
  quote.set(QuoteAttributionAnnotator.CanonicalMentionAnnotation.class, "Alice");
  quote.set(QuoteAttributionAnnotator.CanonicalMentionBeginAnnotation.class, 4);
  quote.set(QuoteAttributionAnnotator.CanonicalMentionEndAnnotation.class, 5);

  List<CoreMap> quotes = new ArrayList<>();
  quotes.add(quote);
  annotation.set(CoreAnnotations.QuotationsAnnotation.class, quotes);

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"mention\":\"Alice\""));
  assertTrue(json.contains("\"canonicalSpeaker\":\"Alice\""));
  assertTrue(json.contains("\"mentionSieve\":\"RULE\""));
}
@Test
public void testSectionsAnnotationWithOptionalFields() throws Exception {
  Annotation annotation = new Annotation("Some text.");

  CoreMap sentence = new Annotation("text");
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  List<CoreMap> sentenceList = new ArrayList<>();
  sentenceList.add(sentence);

  CoreMap section = new Annotation("section");
  section.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  section.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);
  section.set(CoreAnnotations.AuthorAnnotation.class, "AuthorX");
  section.set(CoreAnnotations.SectionDateAnnotation.class, "2024-06-04");
  section.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

  List<CoreMap> sections = new ArrayList<>();
  sections.add(section);
  annotation.set(CoreAnnotations.SectionsAnnotation.class, sections);

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"author\":\"AuthorX\""));
  assertTrue(json.contains("\"dateTime\":\"2024-06-04\""));
  assertTrue(json.contains("\"sentenceIndexes\""));
}
@Test
public void testSentenceWithBinarizedTree() throws Exception {
  Annotation annotation = new Annotation("Binary tree test");

  Tree origTree = Tree.valueOf("(S (NP This) (VP works))");
  Tree binTree = Tree.valueOf("(X (Y This) (Z works))");

  CoreMap sentence = new Annotation("Binary tree test");
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, origTree);
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binTree);
  sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"binaryParse\":\"(X (Y This) (Z works))\""));
}
@Test
public void testDependencyGraphWithRootsAndEdges() throws Exception {
  Annotation annotation = new Annotation("Foo bar.");

  CoreLabel governor = new CoreLabel();
  governor.setWord("root");
  governor.setIndex(1);
  CoreLabel dependent = new CoreLabel();
  dependent.setWord("bar");
  dependent.setIndex(2);

  SemanticGraph graph = new SemanticGraph();
//  graph.addRoot(governor);
//  graph.addVertex(dependent);
//  graph.addEdge(governor, dependent, edu.stanford.nlp.semgraph.SemanticGraphEdge.makeEdge(governor, dependent, "obj", 1.0, false));

  CoreMap sentence = new Annotation("Foo bar");
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  sentence.set(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class, graph);
  sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

  List<CoreMap> list = new ArrayList<>();
  list.add(sentence);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, list);

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"dep\":\"ROOT\""));
  assertTrue(json.contains("\"dep\":\"obj\""));
}
@Test
public void testOpenIEAndKBPTriples() throws Exception {
  Annotation annotation = new Annotation("Test triples");

//  edu.stanford.nlp.ie.util.RelationTriple triple = new edu.stanford.nlp.ie.util.RelationTriple(
//    new CoreLabel(), new CoreLabel(), new CoreLabel(),
//    Collections.emptyList(), Collections.emptyList(), Collections.emptyList()
//  );

  List<edu.stanford.nlp.ie.util.RelationTriple> openieTriples = new ArrayList<>();
//  openieTriples.add(triple);

  List<edu.stanford.nlp.ie.util.RelationTriple> kbpTriples = new ArrayList<>();
//  kbpTriples.add(triple);

  CoreMap sentence = new Annotation("Triple sentence");
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  sentence.set(NaturalLogicAnnotations.RelationTriplesAnnotation.class, openieTriples);
  sentence.set(CoreAnnotations.KBPTriplesAnnotation.class, kbpTriples);
  sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

  List<CoreMap> list = new ArrayList<>();
  list.add(sentence);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, list);

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"openie\""));
  assertTrue(json.contains("\"kbp\""));
}
@Test
public void testTokenWithOnlyRequiredFields() throws Exception {
  Annotation annotation = new Annotation("x");

  CoreLabel token = new CoreLabel();
  token.setWord("x");
  token.setIndex(1);
  token.setBeginPosition(0);
  token.setEndPosition(1);

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);

  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"index\":1"));
  assertTrue(json.contains("\"word\":\"x\""));
}
@Test
public void testSpanWithNullSpan() throws Exception {
  String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
    writer.set("key", Span.fromValues(null, null));
  });
  assertTrue(json.contains("[0,0]"));  
}
@Test
public void testPairSerializedInArray() throws Exception {
  edu.stanford.nlp.util.Pair<String, String> pair = new edu.stanford.nlp.util.Pair<>("A", "B");
  String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
    writer.set("pair", pair);
  });
  assertTrue(json.contains("\"pair\":[\"A\",\"B\"]"));
}
@Test
public void testPrimitiveArraySerialization() throws Exception {
  int[] values = new int[]{1, 2, 3};
  String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
    writer.set("array", values);
  });
  assertTrue(json.contains("[1,2,3]"));
}
@Test
public void testEnumSerialization() throws Exception {
  JSONOutputter.Options options = new JSONOutputter.Options();
  String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
    writer.set("enumValue", Thread.State.NEW);
  });
  assertTrue(json.contains("\"NEW\""));
}
@Test
public void testBooleanSerializationInWriter() throws Exception {
  String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
    writer.set("flagTrue", true);
    writer.set("flagFalse", false);
  });
  assertTrue(json.contains("\"flagTrue\":true"));
  assertTrue(json.contains("\"flagFalse\":false"));
}
@Test
public void testStreamSerializationInWriter() throws Exception {
  Stream<String> stream = Stream.of("x", "y", "z");
  String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
    writer.set("letters", stream);
  });
  assertTrue(json.contains("[\"x\",\"y\",\"z\"]"));
}
@Test
public void testWriterSetNullKeyAndNullValue() {
  String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
    writer.set(null, "value");
    writer.set("key", null);
    writer.set(null, null);
    writer.set("realKey", "realValue");
  });
  assertTrue(json.contains("\"realKey\":\"realValue\""));
  assertFalse(json.contains("null"));
}
@Test
public void testWriterRejectsUnsupportedTypeThrowsRuntime() {
  try {
    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set("invalid", new Object()); 
    });
    fail("Expected RuntimeException for unsupported object type");
  } catch (RuntimeException expected) {
    assertTrue(expected.getMessage().contains("Unknown object to serialize"));
  }
}
@Test
public void testTimexWithoutRange() throws Exception {
  Annotation annotation = new Annotation("Time test");

  CoreLabel token = new CoreLabel();
  token.setWord("today");
  token.setIndex(1);
  token.setBeginPosition(0);
  token.setEndPosition(5);

//  edu.stanford.nlp.time.Timex timex = new edu.stanford.nlp.time.Timex("T1", "DATE", "2023-01-01");
//  token.set(edu.stanford.nlp.time.TimeAnnotations.TimexAnnotation.class, timex);

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);

  CoreMap sentence = new Annotation("Time test");
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"value\":\"2023-01-01\""));
  assertTrue(json.contains("\"type\":\"DATE\""));
}
@Test
public void testSpanWithSameStartAndEnd() {
  edu.stanford.nlp.ie.machinereading.structure.Span span = new edu.stanford.nlp.ie.machinereading.structure.Span(2,2);
  String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
    writer.set("range", span);
  });
  assertTrue(json.contains("[2,2]"));
}
@Test
public void testObjectWithConsumerValue() {
  String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
    writer.set("nested", (JSONOutputter.Writer) (key, value) -> {
      if (key != null && value != null) {
        writer.set("subkey", "subval");
      }
    });
  });
  assertTrue(json.contains("\"subkey\":\"subval\""));
}
@Test
public void testStreamWithNullElements() {
  Stream<String> stream = Stream.of("a", null, "b");
  String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
    writer.set("letters", stream);
  });
  assertTrue(json.contains("\"a\""));
  assertTrue(json.contains("\"b\""));
}
@Test
public void testTokenWithMissingAllOptionalFields() throws Exception {
  Annotation annotation = new Annotation("minimal");
  CoreLabel token = new CoreLabel();
  token.setWord("hi");
  token.setIndex(1);
  token.setBeginPosition(0);
  token.setEndPosition(2);

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"word\":\"hi\""));
  assertFalse(json.contains("\"lemma\""));
  assertFalse(json.contains("\"ner\""));
}
@Test
public void testSentenceSpeakerAndSpeakerType() throws Exception {
  Annotation annotation = new Annotation("Hello!");
  CoreMap sentence = new Annotation("Hello!");
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  sentence.set(CoreAnnotations.SpeakerAnnotation.class, "John");
  sentence.set(CoreAnnotations.SpeakerTypeAnnotation.class, "PERSON");
  sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"speaker\":\"John\""));
  assertTrue(json.contains("\"speakerType\":\"PERSON\""));
}
@Test
public void testNullDocLevelMetadataFields() throws Exception {
  Annotation annotation = new Annotation("Document text.");
  annotation.set(CoreAnnotations.DocIDAnnotation.class, null);
  annotation.set(CoreAnnotations.DocDateAnnotation.class, null);
  annotation.set(CoreAnnotations.DocTypeAnnotation.class, null);
  annotation.set(CoreAnnotations.DocSourceTypeAnnotation.class, null);
  annotation.set(CoreAnnotations.AuthorAnnotation.class, null);
  annotation.set(CoreAnnotations.TextAnnotation.class, "Document text.");

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"text\":\"Document text.\""));
  assertFalse(json.contains("\"docId\""));
  assertFalse(json.contains("\"docDate\""));
  assertFalse(json.contains("\"docType\""));
  assertFalse(json.contains("\"docSourceType\""));
  assertFalse(json.contains("\"author\""));
}
@Test
public void testMultiSentenceWithNullTrees() throws Exception {
  Annotation annotation = new Annotation("Sentence 1. Sentence 2.");
  CoreMap sentence1 = new Annotation("Sentence 1.");
  sentence1.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  sentence1.set(TreeCoreAnnotations.TreeAnnotation.class, null);
  sentence1.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, null);
  sentence1.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

  CoreMap sentence2 = new Annotation("Sentence 2.");
  sentence2.set(CoreAnnotations.SentenceIndexAnnotation.class, 1);
  sentence2.set(TreeCoreAnnotations.TreeAnnotation.class, null);
  sentence2.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, null);
  sentence2.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

  List<CoreMap> list = new ArrayList<>();
  list.add(sentence1);
  list.add(sentence2);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, list);

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"index\":0"));
  assertTrue(json.contains("\"index\":1"));
}
@Test
public void testEmptyCollectionInWriter() {
  String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
    List<String> emptyList = new ArrayList<>();
    writer.set("emptyArray", emptyList);
  });
  assertTrue(json.contains("\"emptyArray\":[]"));
}
@Test
public void testEmptyMapInWriterAsObject() {
  String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
    Map<String, Object> map = new HashMap<>();
    writer.set("emptyObject", (JSONOutputter.Writer) (key, value) -> {
      for (String k : map.keySet()) {
        writer.set(k, map.get(k));
      }
    });
  });
  assertTrue(json.contains("\"emptyObject\":{}"));
}
@Test
public void testNullStreamInWriter() {
  Stream<String> stream = null;
  String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
    writer.set("nullStream", stream);
  });
  assertTrue(json.contains("\"nullStream\":null") || json.contains("nullStream"));
}
@Test
public void testUnicodeCharactersInTokens() throws Exception {
  Annotation annotation = new Annotation("Test");

  CoreLabel unicodeToken = new CoreLabel();
  unicodeToken.setWord("😀");
  unicodeToken.setOriginalText("😀");
  unicodeToken.setIndex(1);
  unicodeToken.setBeginPosition(0);
  unicodeToken.setEndPosition(2);

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(unicodeToken);
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("😀"));
}
@Test
public void testFloatAndDoubleEdgeValues() {
  String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
    writer.set("floatMax", Float.MAX_VALUE);
    writer.set("floatMin", Float.MIN_VALUE);
    writer.set("doubleNaN", Double.NaN);
    writer.set("doubleInf", Double.POSITIVE_INFINITY);
  });
  assertTrue(json.contains("\"floatMax\""));
  assertTrue(json.contains("\"floatMin\""));
  assertTrue(json.contains("NaN") || json.contains("null"));
  assertTrue(json.contains("Infinity") || json.contains("null"));
}
@Test
public void testConsumerWriterDoesNothing() {
  String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
    writer.set("data", (JSONOutputter.Writer) (ignoredKey, ignoredValue) -> {});
  });
  assertTrue(json.contains("\"data\":{}"));
}
@Test
public void testMalformedTreeAnnotation() throws Exception {
  Annotation annotation = new Annotation("Malformed");

  Tree tree = Tree.valueOf("(ROOT (SENTENCE_SKIPPED_OR_UNPARSABLE))");
  CoreMap sentence = new Annotation("test");
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
  sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  String json = JSONOutputter.jsonPrint(annotation);
  assertFalse(json.contains("\"parse\""));
}
@Test
public void testQuoteWithNullSpeakerFields() throws Exception {
  Annotation annotation = new Annotation("“Test quote.”");

  CoreMap quote = new Annotation("“Test quote.”");
  quote.set(CoreAnnotations.QuotationIndexAnnotation.class, 1);
  quote.set(CoreAnnotations.TextAnnotation.class, "Test quote");
  quote.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
  quote.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  quote.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);

  List<CoreMap> quotes = new ArrayList<>();
  quotes.add(quote);
  annotation.set(CoreAnnotations.QuotationsAnnotation.class, quotes);

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"speaker\":\"Unknown\""));
  assertTrue(json.contains("\"canonicalSpeaker\":\"Unknown\""));
}
@Test
public void testCorefChainWithNullRepresentativeMention() throws Exception {
  Annotation annotation = new Annotation("test");

//  CorefChain.CorefMention m1 = new CorefChain.CorefMention(1, 1, 1, 2, 1, "Test", "NOMINAL", "SINGULAR", "UNKNOWN", "INANIMATE", new int[]{1});
  List<CorefChain.CorefMention> mentions = new ArrayList<>();
//  mentions.add(m1);

//  CorefChain chain = new CorefChain(1, mentions) {
//    @Override
//    public CorefMention getRepresentativeMention() {
//      return null;
//    }
//  };

  Map<Integer, CorefChain> corefMap = new HashMap<>();
//  corefMap.put(1, chain);
  annotation.set(edu.stanford.nlp.coref.CorefCoreAnnotations.CorefChainAnnotation.class, corefMap);

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"id\":1"));
}
@Test
public void testSentenceWithoutIndex() throws Exception {
  Annotation annotation = new Annotation("Sentence without index");

  CoreMap sentence = new Annotation("Sentence without index");
  sentence.set(CoreAnnotations.TextAnnotation.class, "Hello world");
  sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

  List<CoreMap> list = new ArrayList<>();
  list.add(sentence);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, list);

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("sentences"));
  assertFalse(json.contains("\"index\"")); 
}
@Test
public void testPairWithNulls() {
  edu.stanford.nlp.util.Pair<String, String> pair = new edu.stanford.nlp.util.Pair<>(null, null);
  String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
    writer.set("nullPair", pair);
  });
  assertTrue(json.contains("\"nullPair\":[null,null]") || json.contains("[ null, null ]"));
}
@Test
public void testNullSpanSerialization() {
  edu.stanford.nlp.ie.machinereading.structure.Span span = null;
  String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
    writer.set("mySpan", span);
  });
  assertTrue(json.contains("\"mySpan\":null"));
}
//@Test
//public void testMultiLevelNestedConsumerObjects() {
//  String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
//    writer.set("outer", (JSONOutputter.Writer) (k1, v1) -> {
//      ((JSONOutputter.Writer) writer1 -> writer1.set("innerKey", "innerValue")).set("innerObj", v1);
//      writer.set("deep", (JSONOutputter.Writer) (k2, v2) -> {
//        writer.set("deepKey", "deepVal");
//      });
//    });
//  });
//  assertTrue(json.contains("\"deepKey\":\"deepVal\""));
//}
@Test
public void testCorefChainWithEmptyMentionsList() throws Exception {
  Annotation annotation = new Annotation("Test");

//  CorefChain emptyChain = new CorefChain(1, new ArrayList<>()) {
//    @Override
//    public CorefMention getRepresentativeMention() {
//      return null;
//    }
//  };

  Map<Integer, CorefChain> corefs = new HashMap<>();
//  corefs.put(1, emptyChain);
  annotation.set(edu.stanford.nlp.coref.CorefCoreAnnotations.CorefChainAnnotation.class, corefs);

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"corefs\""));
}
@Test
public void testDependencyGraphNullIsSerializedAsNull() throws Exception {
  Annotation annotation = new Annotation("Dependencies");

  CoreMap sentence = new Annotation("Dependencies");
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  sentence.set(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class, null);
  sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

  List<CoreMap> list = new ArrayList<>();
  list.add(sentence);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, list);

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"basicDependencies\":null"));
}
@Test
public void testSectionWithNullSentencesArray() throws Exception {
  Annotation annotation = new Annotation("Section");

  CoreMap section = new Annotation("meta");
  section.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  section.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);
  section.set(CoreAnnotations.SentencesAnnotation.class, null);

  List<CoreMap> list = new ArrayList<>();
  list.add(section);
  annotation.set(CoreAnnotations.SectionsAnnotation.class, list);

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"charBegin\":0"));
  assertTrue(json.contains("\"charEnd\":10"));
}
@Test
public void testTimexWithFullRangeWritten() throws Exception {
  Annotation annotation = new Annotation("Time");

  CoreLabel token = new CoreLabel();
  token.setWord("today");
  token.setIndex(1);
  token.setBeginPosition(0);
  token.setEndPosition(5);

//  edu.stanford.nlp.time.Timex.Range range = new edu.stanford.nlp.time.Timex.Range(0, 1, "PT1D");
//  edu.stanford.nlp.time.Timex timex = new edu.stanford.nlp.time.Timex("t1", "DATE", "2020-01-01");
//  timex.setRange(range);
//  token.set(edu.stanford.nlp.time.TimeAnnotations.TimexAnnotation.class, timex);

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);

  CoreMap sentence = new Annotation("time");
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"duration\":\"PT1D\""));
}
@Test
public void testTokenWithExtendedOptionalFields() throws Exception {
  Annotation annotation = new Annotation("Hello");

  CoreLabel token = new CoreLabel();
  token.setWord("Hello");
  token.setOriginalText("Hello");
  token.setIndex(1);
  token.setBeginPosition(0);
  token.setEndPosition(5);
  token.setTag("UH");
  token.setNER("GREETING");
  token.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "Hi");
  token.set(CoreAnnotations.SpeakerAnnotation.class, "Narrator");
  token.set(CoreAnnotations.SpeakerTypeAnnotation.class, "AGENT");
  token.set(CoreAnnotations.TrueCaseAnnotation.class, "UPPER");
  token.set(CoreAnnotations.TrueCaseTextAnnotation.class, "HELLO");
  token.set(CoreAnnotations.BeforeAnnotation.class, "^");
  token.set(CoreAnnotations.AfterAnnotation.class, "$");

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);

  CoreMap sentence = new Annotation("Hello");
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"truecase\":\"UPPER\""));
  assertTrue(json.contains("\"speakerType\":\"AGENT\""));
  assertTrue(json.contains("\"before\":\"^\""));
  assertTrue(json.contains("\"after\":\"$\""));
}
@Test
public void testSentimentAnnotatedWithDistributionAndTree() throws Exception {
  Annotation annotation = new Annotation("Sentiment");

  Tree sentimentTree = Tree.valueOf("(3 (2 bad) (4 good))");
  CoreMap sentence = new Annotation("sentiment");
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  sentence.set(SentimentCoreAnnotations.SentimentAnnotatedTree.class, sentimentTree);
  sentence.set(SentimentCoreAnnotations.SentimentClass.class, "Positive");
  sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

  List<CoreMap> list = new ArrayList<>();
  list.add(sentence);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, list);

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"sentiment\":\"Positive\""));
  assertTrue(json.contains("\"sentimentValue\""));
  assertTrue(json.contains("\"sentimentDistribution\""));
  assertTrue(json.contains("\"sentimentTree\""));
}
@Test
public void testWriterWithObjectArraySerialization() {
  Object[] values = new Object[]{"a", "b", 7};
  String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
    writer.set("values", values);
  });
  assertTrue(json.contains("\"values\":[\"a\",\"b\",7]"));
}
@Test
public void testWriterWithPrimitiveCharSerialization() {
  String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
    writer.set("charField", 'Z');
  });
  assertTrue(json.contains("\"charField\":\"Z\""));
}
@Test
public void testWriterWithBooleanPrimitiveSerialization() {
  String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
    writer.set("truth", true);
    writer.set("lie", false);
  });
  assertTrue(json.contains("\"truth\":true"));
  assertTrue(json.contains("\"lie\":false"));
}
@Test
public void testWriterWithNullInsideStream() {
  Stream<Object> data = Stream.of("A", null, 5);
  String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
    writer.set("mixed", data);
  });
  assertTrue(json.contains("\"mixed\""));
  assertTrue(json.contains("A"));
  assertTrue(json.contains("5"));
}
@Test
public void testVeryLargeAndSmallFloatValues() {
  String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
    writer.set("floatMax", Float.MAX_VALUE);
    writer.set("floatMin", Float.MIN_VALUE);
    writer.set("negativeFloat", -3.1415f);
  });
  assertTrue(json.contains("\"floatMax\""));
  assertTrue(json.contains("\"negativeFloat\":"));
}
@Test
public void testEscapeControlCharactersInKeysAndValues() {
  String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
    writer.set("key\n\t\r", "val\n\t\r");
  });
  assertTrue(json.contains("\\n"));
  assertTrue(json.contains("\\r"));
  assertTrue(json.contains("\\t"));
}
@Test
public void testSentenceWithOnlyBinarizedTree() throws Exception {
  Tree binTree = Tree.valueOf("(X (A a) (B b))");
  CoreMap sentence = new Annotation("BinaryOnly");
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binTree);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);

  Annotation annotation = new Annotation("test");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"binaryParse\""));
}
@Test
public void testObjectArrayWithPrimitiveAndWrapperMix() {
  Object[] array = new Object[]{1, "x", true, null};
  String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
    writer.set("mixedArray", array);
  });
  assertTrue(json.contains("[1,\"x\",true,null]"));
}
@Test
public void testTokenWithCodepointOnlyNoOffsets() throws Exception {
  CoreLabel token = new CoreLabel();
  token.setWord("😊");
  token.setIndex(1);
  token.set(CoreAnnotations.CodepointOffsetBeginAnnotation.class, 10);
  token.set(CoreAnnotations.CodepointOffsetEndAnnotation.class, 11);

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);

  CoreMap sentence = new Annotation("Codepoints");
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

  List<CoreMap> sents = new ArrayList<>();
  sents.add(sentence);

  Annotation annotation = new Annotation("Codepoints");
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sents);

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("codepointOffset"));
}
@Test
public void testMinimalSentenceWithManyMissingAnnotationTypes() throws Exception {
  CoreMap sentence = new Annotation("empty");
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0); 

  Annotation annotation = new Annotation("input");
  List<CoreMap> list = new ArrayList<>();
  list.add(sentence);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, list);

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"index\":0"));
}
@Test
public void testJSONStreamWithDuplicates() {
  Stream<String> dupStream = Stream.of("a", "b", "a", "c");
  String output = JSONOutputter.JSONWriter.objectToJSON(writer -> writer.set("dups", dupStream));
  assertTrue(output.contains("\"dups\":[\"a\",\"b\",\"a\",\"c\"]"));
}
@Test
public void testWriterWithEmptyStringKey() {
  String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
    writer.set("", "nonEmptyValue");
  });
  assertTrue(json.contains("\"\":\"nonEmptyValue\""));
}
@Test
public void testPrettyPrintAddsIndentation() {
  try {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    PrintWriter out = new PrintWriter(os, true, java.nio.charset.StandardCharsets.UTF_8);
    JSONOutputter.JSONWriter writer = new JSONOutputter.JSONWriter(out, new JSONOutputter.Options(true));

    writer.object(w -> {
      w.set("a", "b"); 
    });
    out.flush();
    String result = os.toString("UTF-8");
    assertTrue(result.contains("\n"));
    assertTrue(result.contains("  \"a\""));
  } catch (Exception e) {
    fail("Should not fail on pretty output: " + e.getMessage());
  }
} 
}