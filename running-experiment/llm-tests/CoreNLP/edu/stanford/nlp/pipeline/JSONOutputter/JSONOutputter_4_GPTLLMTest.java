package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.ie.machinereading.structure.Span;
import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.parser.lexparser.Options;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.Timex;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.trees.LabeledScoredTreeFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.IntPair;
import edu.stanford.nlp.util.Pair;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class JSONOutputter_4_GPTLLMTest {

 @Test
  public void testJsonPrintEmptyAnnotation() throws IOException {
    Annotation annotation = new Annotation("");
    String json = JSONOutputter.jsonPrint(annotation);
    assertNotNull(json);
    assertTrue(json.contains("{"));
    assertTrue(json.contains("}"));
  }
@Test
  public void testJsonIncludesTextWhenOptionEnabled() throws IOException {
    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.TextAnnotation.class, "This is test text.");
    JSONOutputter.Options options = new JSONOutputter.Options();
//    options.includeText = true;

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    JSONOutputter.jsonPrint(annotation, os, options);
    String output = os.toString("UTF-8");

    assertTrue(output.contains("\"text\""));
    assertTrue(output.contains("This is test text."));
  }
@Test
  public void testSentenceWithParseTree() throws IOException {
    Annotation annotation = new Annotation("");
    Tree tree = new LabeledScoredTreeFactory().newLeaf("ROOT");
    CoreMap sentence = new Annotation("");
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    String json = JSONOutputter.jsonPrint(annotation);
    assertTrue(json.contains("\"sentences\""));
    assertTrue(json.contains("\"parse\""));
    assertTrue(json.contains("ROOT"));
  }
@Test
  public void testDependencyRootSerialization() throws IOException {
    Annotation annotation = new Annotation("");
    IndexedWord root = new IndexedWord(new Word("root"));
    root.setIndex(1);

    SemanticGraph graph = new SemanticGraph();
    graph.addRoot(root);

    CoreMap sentence = new Annotation("");
    sentence.set(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class, graph);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    String json = JSONOutputter.jsonPrint(annotation);
    assertTrue(json.contains("\"basicDependencies\""));
    assertTrue(json.contains("\"dependentGloss\": \"root\""));
    assertTrue(json.contains("\"dep\": \"ROOT\""));
  }
@Test
  public void testEntityMentionsMinimal() throws IOException {
    Annotation annotation = new Annotation("");

    CoreLabel mention = new CoreLabel();
    mention.set(CoreAnnotations.TextAnnotation.class, "Stanford");
    mention.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    mention.set(CoreAnnotations.TokenEndAnnotation.class, 1);
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORG");

    List<CoreLabel> mentions = new ArrayList<>();
    mentions.add(mention);

    CoreMap sentence = new Annotation("");
//    sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    String json = JSONOutputter.jsonPrint(annotation);
    assertTrue(json.contains("\"entitymentions\""));
    assertTrue(json.contains("Stanford"));
    assertTrue(json.contains("\"ner\": \"ORG\""));
  }
@Test
  public void testTokenSerialization() throws IOException {
    Annotation annotation = new Annotation("");

    CoreLabel token = new CoreLabel();
    token.setWord("Stanford");
    token.setOriginalText("Stanford");
    token.setBeginPosition(0);
    token.setEndPosition(8);
    token.setTag("NNP");
    token.setNER("ORG");
    token.set(CoreAnnotations.SpeakerAnnotation.class, "Dr. Smith");

    List<CoreLabel> tokenList = new ArrayList<>();
    tokenList.add(token);

    CoreMap sentence = new Annotation("");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokenList);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    String json = JSONOutputter.jsonPrint(annotation);

    assertTrue(json.contains("\"Stanford\""));
    assertTrue(json.contains("\"pos\": \"NNP\""));
    assertTrue(json.contains("\"ner\": \"ORG\""));
    assertTrue(json.contains("\"speaker\": \"Dr. Smith\""));
  }
@Test
  public void testRelationTripleSerialization() throws IOException {
    Annotation annotation = new Annotation("");

    RelationTriple triple = mock(RelationTriple.class);
    when(triple.subjectGloss()).thenReturn("Alice");
    when(triple.relationGloss()).thenReturn("loves");
    when(triple.objectGloss()).thenReturn("Bob");
    when(triple.subjectTokenSpan()).thenReturn(new Pair<>(0, 1));
    when(triple.relationTokenSpan()).thenReturn(new Pair<>(1, 2));
    when(triple.objectTokenSpan()).thenReturn(new Pair<>(2, 3));

    List<RelationTriple> list = new ArrayList<>();
    list.add(triple);

    CoreMap sentence = new Annotation("");
    sentence.set(NaturalLogicAnnotations.RelationTriplesAnnotation.class, list);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    String json = JSONOutputter.jsonPrint(annotation);
    assertTrue(json.contains("\"openie\""));
    assertTrue(json.contains("Alice"));
    assertTrue(json.contains("loves"));
    assertTrue(json.contains("Bob"));
  }
@Test
  public void testSentimentSerialization() throws IOException {
    Annotation annotation = new Annotation("");

    Tree sentimentTree = new LabeledScoredTreeFactory().newLeaf("positive");
    CoreMap sentence = new Annotation("positive");
    sentence.set(SentimentCoreAnnotations.SentimentAnnotatedTree.class, sentimentTree);
    sentence.set(SentimentCoreAnnotations.SentimentClass.class, "Positive");
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    String json = JSONOutputter.jsonPrint(annotation);
    assertTrue(json.contains("\"sentiment\""));
    assertTrue(json.contains("Positive"));
  }
@Test
  public void testCoreferenceChainSerialization() throws IOException {
    Annotation annotation = new Annotation("");

//    CorefChain.CorefMention mention = new CorefChain.CorefMention(1, 1, 0, 1, 0, 0,
//        "Alice", "PROPER", "SINGULAR", "FEMALE", "ANIMATE", false);

    List<CorefChain.CorefMention> mentions = new ArrayList<>();
//    mentions.add(mention);

//    CorefChain chain = new CorefChain(100, mentions);
    Map<Integer, CorefChain> map = new HashMap<>();
//    map.put(100, chain);

    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, map);

    String json = JSONOutputter.jsonPrint(annotation);
    assertTrue(json.contains("\"corefs\""));
    assertTrue(json.contains("\"text\": \"Alice\""));
    assertTrue(json.contains("\"type\": \"PROPER\""));
  }
@Test
  public void testTimexFieldsSerialization() throws IOException {
    Annotation annotation = new Annotation("");

    CoreLabel token = new CoreLabel();
//    Timex timex = new Timex("t1", Timex.Type.DATE);
//    timex.setValue("2024-01-01");
//    token.set(TimeAnnotations.TimexAnnotation.class, timex);

    List<CoreLabel> tokenList = new ArrayList<>();
    tokenList.add(token);

    CoreMap sentence = new Annotation("");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokenList);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    String json = JSONOutputter.jsonPrint(annotation);
    assertTrue(json.contains("\"timex\""));
    assertTrue(json.contains("2024-01-01"));
  }
@Test(expected = IOException.class)
  public void testJsonThrowsIOException() throws IOException {
    Annotation annotation = new Annotation("Will throw");
    OutputStream badOutputStream = mock(OutputStream.class);
    doThrow(new IOException()).when(badOutputStream).write(any(byte[].class), anyInt(), anyInt());
    JSONOutputter.jsonPrint(annotation, badOutputStream);
  }
@Test
public void testSentenceWithoutTreeAnnotations() throws IOException {
  Annotation annotation = new Annotation("Test sentence.");
  CoreMap sentence = new Annotation("Test sentence.");
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"sentences\""));
}
@Test
public void testSentenceWithBinarizedTree() throws IOException {
  Annotation annotation = new Annotation("");

  Tree binTree = new LabeledScoredTreeFactory().newLeaf("BINARY_ROOT");

  CoreMap sentence = new Annotation("");
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binTree);
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"binaryParse\""));
  assertTrue(json.contains("BINARY_ROOT"));
}
@Test
public void testEmptyDependencyGraph() throws IOException {
  Annotation annotation = new Annotation("");
  SemanticGraph graph = new SemanticGraph(); 

  CoreMap sentence = new Annotation("");
  sentence.set(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class, graph);
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"basicDependencies\""));
  assertFalse(json.contains("\"dep\":"));
}
@Test
public void testEmptyEntityMentions() throws IOException {
  Annotation annotation = new Annotation("");

  CoreMap sentence = new Annotation("");
  sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
  sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"entitymentions\": []"));
}
@Test
public void testTokenWithCodepointOffsets() throws IOException {
  Annotation annotation = new Annotation("");

  CoreLabel token = new CoreLabel();
  token.setWord("Umlaut");
  token.setOriginalText("Ümlaut");
  token.setBeginPosition(0);
  token.setEndPosition(6);
  token.setIndex(1);
  token.set(CoreAnnotations.CodepointOffsetBeginAnnotation.class, 0);
  token.set(CoreAnnotations.CodepointOffsetEndAnnotation.class, 6);

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);

  CoreMap sentence = new Annotation("");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"codepointOffsetBegin\": 0"));
  assertTrue(json.contains("\"codepointOffsetEnd\": 6"));
}
@Test
public void testQuoteWithAllFieldsNull() throws IOException {
  Annotation annotation = new Annotation("");

  CoreMap quote = new Annotation("");
  quote.set(CoreAnnotations.QuotationIndexAnnotation.class, 0);
  quote.set(CoreAnnotations.TextAnnotation.class, "Quote body");
  quote.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
  quote.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 20);
  quote.set(CoreAnnotations.TokenBeginAnnotation.class, 1);
  quote.set(CoreAnnotations.TokenEndAnnotation.class, 2);
  quote.set(CoreAnnotations.SentenceBeginAnnotation.class, 0);
  quote.set(CoreAnnotations.SentenceEndAnnotation.class, 0);

  List<CoreMap> quotes = new ArrayList<>();
  quotes.add(quote);
  annotation.set(CoreAnnotations.QuotationsAnnotation.class, quotes);

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"quotes\""));
  assertTrue(json.contains("\"text\": \"Quote body\""));
  assertTrue(json.contains("\"speaker\": \"Unknown\""));
  assertTrue(json.contains("\"canonicalSpeaker\": \"Unknown\""));
}
@Test
public void testCorefChainWithNullValues() throws IOException {
  Annotation annotation = new Annotation("");

//  CorefChain.CorefMention mention = new CorefChain.CorefMention(1, 1, 1, 1, 1, 1,
//      null, null, null, null, null, false);

//  List<CorefMention> mentions = new ArrayList<>();
//  mentions.add(mention);
//  CorefChain chain = new CorefChain(1234, mentions);

  Map<Integer, CorefChain> corefMap = new HashMap<>();
//  corefMap.put(1234, chain);
  annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, corefMap);

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"corefs\""));
  assertTrue(json.contains("\"id\""));
  assertTrue(json.contains("\"isRepresentativeMention\""));
}
@Test
public void testSectionWithNullDatesAndAuthor() throws IOException {
  Annotation annotation = new Annotation("");

  CoreMap section = new Annotation("");
  section.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
  section.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);

  CoreMap sentence = new Annotation("Section sentence.");
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  List<CoreMap> sectionSentences = new ArrayList<>();
  sectionSentences.add(sentence);

  section.set(CoreAnnotations.SentencesAnnotation.class, sectionSentences);
  List<CoreMap> sections = new ArrayList<>();
  sections.add(section);
  annotation.set(CoreAnnotations.SectionsAnnotation.class, sections);

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"sections\""));
  assertTrue(json.contains("\"charBegin\": 5"));
  assertTrue(json.contains("\"charEnd\": 10"));
  assertTrue(json.contains("\"sentenceIndexes\""));
}
@Test
public void testSentimentTreeWithNullValues() throws IOException {
  Annotation annotation = new Annotation("");

//  Tree sentimentTree = new LabeledScoredTreeFactory().newLeaf(null);
  CoreMap sentence = new Annotation("Sentiment tree with null label");
//  sentence.set(SentimentCoreAnnotations.SentimentAnnotatedTree.class, sentimentTree);
  sentence.set(SentimentCoreAnnotations.SentimentClass.class, "Very Positive");
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"sentiment\""));
  assertTrue(json.contains("VeryPositive")); 
}
@Test
public void testSingleTokenGlobalLevel() throws IOException {
  Annotation annotation = new Annotation("token level test");

  CoreLabel token = new CoreLabel();
  token.setWord("Hello");
  token.setOriginalText("Hello");
  token.setBeginPosition(0);
  token.setEndPosition(5);
  token.setIndex(1);

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);

  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"tokens\""));
  assertTrue(json.contains("\"word\": \"Hello\""));
  assertTrue(json.contains("\"index\": 1"));
}
@Test
public void testOpenIETriplesNullSpan() throws IOException {
  Annotation annotation = new Annotation("");

  RelationTriple triple = mock(RelationTriple.class);
  when(triple.subjectGloss()).thenReturn("X");
  when(triple.relationGloss()).thenReturn("is");
  when(triple.objectGloss()).thenReturn("Y");
  when(triple.subjectTokenSpan()).thenReturn(null);
  when(triple.relationTokenSpan()).thenReturn(null);
  when(triple.objectTokenSpan()).thenReturn(null);

  CoreMap sentence = new Annotation("");
  List<RelationTriple> triples = new ArrayList<>();
  triples.add(triple);
  sentence.set(NaturalLogicAnnotations.RelationTriplesAnnotation.class, triples);
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"openie\""));
  assertTrue(json.contains("\"subject\": \"X\""));
  assertTrue(json.contains("\"relation\": \"is\""));
  assertTrue(json.contains("\"object\": \"Y\""));
}
@Test
public void testNullSentenceInSentencesAnnotation() throws IOException {
  Annotation annotation = new Annotation("");

  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(null); 
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  String json = JSONOutputter.jsonPrint(annotation);
  assertNotNull(json);
  assertTrue(json.contains("\"sentences\""));
}
@Test
public void testEmptyStructuredTriples() throws IOException {
  Annotation annotation = new Annotation("");
  CoreMap sentence = new Annotation("");
  sentence.set(NaturalLogicAnnotations.RelationTriplesAnnotation.class, new ArrayList<>());
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  List<CoreMap> sentenceList = new ArrayList<>();
  sentenceList.add(sentence);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"openie\": []"));
}
@Test
public void testEmptyKBPTriples() throws IOException {
  Annotation annotation = new Annotation("");
  CoreMap sentence = new Annotation("");
  sentence.set(CoreAnnotations.KBPTriplesAnnotation.class, new ArrayList<>());
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  List<CoreMap> sentenceList = new ArrayList<>();
  sentenceList.add(sentence);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"kbp\": []"));
}
@Test
public void testTokenWithOnlyIndexAndWord() throws IOException {
  Annotation annotation = new Annotation("");

  CoreLabel token = new CoreLabel();
  token.setIndex(3);
  token.setWord("Minimal");

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);

  CoreMap sentence = new Annotation("");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  List<CoreMap> sentenceList = new ArrayList<>();
  sentenceList.add(sentence);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"word\": \"Minimal\""));
  assertTrue(json.contains("\"index\": 3"));
}
@Test
public void testTokenWithMissingOffsets() throws IOException {
  Annotation annotation = new Annotation("");

  CoreLabel token = new CoreLabel();
  token.setIndex(1);
  token.setWord("Test");
  token.setOriginalText("Test");

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);

  CoreMap sentence = new Annotation("");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  List<CoreMap> sentenceList = new ArrayList<>();
  sentenceList.add(sentence);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"word\": \"Test\""));
  assertTrue(json.contains("\"originalText\": \"Test\""));
}
@Test
public void testSentenceWithNullParseTreeString() throws IOException {
  Annotation annotation = new Annotation("");

  Tree tree = new LabeledScoredTreeFactory().newLeaf("");
  CoreMap sentence = new Annotation("");
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"parse\""));
}
@Test
public void testOnlyMentionsAnnotationWithoutTokenBegin() throws IOException {
  Annotation annotation = new Annotation("");

  CoreLabel mention = new CoreLabel();
  mention.set(CoreAnnotations.TextAnnotation.class, "EntityOnly");

  List<CoreLabel> mentions = new ArrayList<>();
  mentions.add(mention);

  CoreMap sentence = new Annotation("");
//  sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);

  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("EntityOnly"));
}
@Test
public void testCorefChainWithEmptyMentionList() throws IOException {
  Annotation annotation = new Annotation("");
//  CorefChain chain = new CorefChain(101, new ArrayList<CorefChain.CorefMention>());
  Map<Integer, CorefChain> corefs = new HashMap<>();
//  corefs.put(101, chain);
  annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, corefs);

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"corefs\""));
}
@Test
public void testNoSentencesButOnlyTokens() throws IOException {
  Annotation annotation = new Annotation("");

  CoreLabel token = new CoreLabel();
  token.setIndex(1);
  token.setWord("Orphan");
  token.setOriginalText("Orphan");
  token.setBeginPosition(0);
  token.setEndPosition(6);

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"tokens\""));
  assertTrue(json.contains("Orphan"));
}
@Test
public void testTimeWithNullRange() throws IOException {
  Annotation annotation = new Annotation("");

//  Timex time = new Timex("t0", Timex.Type.DATE);
//  time.setValue("2025-12-25");
//
  CoreLabel mention = new CoreLabel();
//  mention.set(TokenAnnotation.class, "Christmas");
//  mention.set(TimeAnnotations.TimexAnnotation.class, time);

  List<CoreLabel> mentions = new ArrayList<>();
  mentions.add(mention);
  CoreMap sentence = new Annotation("");
//  sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
  sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"value\": \"2025-12-25\""));
}
@Test
public void testJsonWriterWithPrimitiveArrayInt() {
  ByteArrayOutputStream os = new ByteArrayOutputStream();
  PrintWriter pw = new PrintWriter(os);
  JSONOutputter.Options options = new JSONOutputter.Options();
  JSONOutputter.JSONWriter writer = new JSONOutputter.JSONWriter(pw, options);

  int[] nums = new int[]{1, 2, 3};
  writer.object(out -> {
    out.set("array", nums);
  });
  writer.flush();
  String json = os.toString();
  assertTrue(json.contains("[1, 2, 3]"));
}
@Test
public void testJsonWriterWithEnum() {
  ByteArrayOutputStream os = new ByteArrayOutputStream();
  PrintWriter pw = new PrintWriter(os);
  JSONOutputter.Options options = new JSONOutputter.Options();
  JSONOutputter.JSONWriter writer = new JSONOutputter.JSONWriter(pw, options);

//  enum SampleEnum {FOO, BAR}

//  writer.object(out -> {
//    out.set("enumValue", SampleEnum.BAR);
//  });
  writer.flush();

  String output = os.toString();
  assertTrue(output.contains("\"enumValue\": \"BAR\""));
}
@Test
public void testJsonWriterWithPair() {
  ByteArrayOutputStream os = new ByteArrayOutputStream();
  PrintWriter pw = new PrintWriter(os);
  JSONOutputter.Options options = new JSONOutputter.Options();
  JSONOutputter.JSONWriter writer = new JSONOutputter.JSONWriter(pw, options);

  writer.object(out -> {
    out.set("pair", new Pair<>("left", "right"));
  });
  writer.flush();

  String output = os.toString();
  assertTrue(output.contains("[\"left\", \"right\"]"));
}
//@Test
//public void testBuildDependencyTreeWithNullGraph() throws IOException {
//  Object result = invokeBuildDependencyTreeWithNull();
//  assertNull(result);
//}
@Test
public void testWriteTimeWithTimexRangeNull() {
  ByteArrayOutputStream os = new ByteArrayOutputStream();
  PrintWriter pw = new PrintWriter(os);
  JSONOutputter.Options options = new JSONOutputter.Options();
  JSONOutputter.JSONWriter writer = new JSONOutputter.JSONWriter(pw, options);

//  Timex timex = new Timex("tX", Timex.Type.TIME);
//  timex.setValue("2020-02-02");
//  timex.setAltVal("alt-2020");
//
//  writer.object(out -> {
//    JSONOutputter.Writer w = out::set;
//    JSONOutputter.writeTime(w, timex);
//  });

  writer.flush();
  String json = os.toString();
  assertTrue(json.contains("\"value\": \"2020-02-02\""));
  assertTrue(json.contains("\"altValue\": \"alt-2020\""));
}
@Test
public void testQuoteWithMentionAndSpeakerSieve() throws IOException {
  Annotation annotation = new Annotation("");

  CoreMap quote = new Annotation("");
  quote.set(CoreAnnotations.QuotationIndexAnnotation.class, 1);
  quote.set(CoreAnnotations.TextAnnotation.class, "He said something.");
  quote.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
  quote.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 25);
  quote.set(CoreAnnotations.TokenBeginAnnotation.class, 1);
  quote.set(CoreAnnotations.TokenEndAnnotation.class, 5);
  quote.set(CoreAnnotations.SentenceBeginAnnotation.class, 0);
  quote.set(CoreAnnotations.SentenceEndAnnotation.class, 0);
  quote.set(QuoteAttributionAnnotator.SpeakerAnnotation.class, "John");
  quote.set(QuoteAttributionAnnotator.SpeakerSieveAnnotation.class, "RuleBasedSieve");
  quote.set(QuoteAttributionAnnotator.MentionAnnotation.class, "MentionX");
  quote.set(QuoteAttributionAnnotator.MentionBeginAnnotation.class, 5);
  quote.set(QuoteAttributionAnnotator.MentionEndAnnotation.class, 6);
  quote.set(QuoteAttributionAnnotator.MentionTypeAnnotation.class, "Pronoun");
  quote.set(QuoteAttributionAnnotator.MentionSieveAnnotation.class, "Sieve42");
  quote.set(QuoteAttributionAnnotator.CanonicalMentionAnnotation.class, "John Doe");
  quote.set(QuoteAttributionAnnotator.CanonicalMentionBeginAnnotation.class, 1);
  quote.set(QuoteAttributionAnnotator.CanonicalMentionEndAnnotation.class, 3);

  List<CoreMap> list = new ArrayList<>();
  list.add(quote);
  annotation.set(CoreAnnotations.QuotationsAnnotation.class, list);

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"speaker\": \"John\""));
  assertTrue(json.contains("\"mention\": \"MentionX\""));
  assertTrue(json.contains("\"canonicalSpeaker\": \"John Doe\""));
  assertTrue(json.contains("\"speakerSieve\": \"RuleBasedSieve\""));
}
@Test
public void testSentimentTreeWithPrediction() throws IOException {
  Annotation annotation = new Annotation("");

  Tree tree = new LabeledScoredTreeFactory().newLeaf("positive");
  tree.label().setValue("POS_LABEL");

  CoreMap sentence = new Annotation("");
  sentence.set(SentimentCoreAnnotations.SentimentAnnotatedTree.class, tree);
  sentence.set(SentimentCoreAnnotations.SentimentClass.class, "Positive");
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  List<CoreMap> sentenceList = new ArrayList<>();
  sentenceList.add(sentence);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"sentimentTree\""));
  assertTrue(json.contains("POS_LABEL"));
}
@Test
public void testTokenWithNullNERAndNullNormalizedNER() throws IOException {
  Annotation annotation = new Annotation("");

  CoreLabel token = new CoreLabel();
  token.setIndex(1);
  token.setWord("TestToken");
  token.setOriginalText("TestToken");
  token.setBeginPosition(0);
  token.setEndPosition(9);
  token.setNER(null);
  token.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, null);

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);
  CoreMap sentence = new Annotation("");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"word\": \"TestToken\""));
  assertTrue(json.contains("\"index\": 1"));
}
@Test
public void testEntityMentionWithNERConfidenceButOnlyO() throws IOException {
  Annotation annotation = new Annotation("");
  CoreLabel mention = new CoreLabel();
  mention.set(CoreAnnotations.TextAnnotation.class, "ExampleEntity");
  mention.set(CoreAnnotations.TokenBeginAnnotation.class, 5);
  mention.set(CoreAnnotations.TokenEndAnnotation.class, 6);
  mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORG");
  mention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
  mention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 20);
  Map<String, Double> confidences = new HashMap<>();
  confidences.put("O", 1.0);
  mention.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, confidences);

  CoreMap sentence = new Annotation("");
  sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
  sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 5);
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"entitymentions\""));
  assertFalse(json.contains("\"nerConfidences\""));
}
@Test
public void testTokenWithTimexFullRange() throws IOException {
  Annotation annotation = new Annotation("");
//  Timex timex = new Timex("t123", Timex.Type.DATE);
//  timex.setValue("2024-12-31");
//  Timex.Range range = new Timex.Range();
//  range.begin = 1;
//  range.end = 2;
//  range.duration = "P1D";
//  timex.setRange(range);

  CoreLabel token = new CoreLabel();
  token.setWord("NewYearEve");
  token.setBeginPosition(0);
  token.setEndPosition(11);
//  token.set(TimeAnnotations.TimexAnnotation.class, timex);

  CoreMap sentence = new Annotation("");
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"value\": \"2024-12-31\""));
  assertTrue(json.contains("\"begin\": 1"));
  assertTrue(json.contains("\"duration\": \"P1D\""));
}
@Test
public void testQuoteWithNullSpeakerAndCanonicalSpeaker() throws IOException {
  Annotation annotation = new Annotation("");

  CoreMap quote = new Annotation("");
  quote.set(CoreAnnotations.QuotationIndexAnnotation.class, 0);
  quote.set(CoreAnnotations.TextAnnotation.class, "Quote goes here");
  quote.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  quote.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 16);
  quote.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
  quote.set(CoreAnnotations.TokenEndAnnotation.class, 4);
  quote.set(CoreAnnotations.SentenceBeginAnnotation.class, 0);
  quote.set(CoreAnnotations.SentenceEndAnnotation.class, 0);

  List<CoreMap> quotes = new ArrayList<>();
  quotes.add(quote);
  annotation.set(CoreAnnotations.QuotationsAnnotation.class, quotes);

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"speaker\": \"Unknown\""));
  assertTrue(json.contains("\"canonicalSpeaker\": \"Unknown\""));
}
@Test
public void testEmptyCorefsMap() throws IOException {
  Annotation annotation = new Annotation("");
  Map<Integer, CorefChain> emptyMap = new HashMap<>();
  annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, emptyMap);
  
  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"corefs\": {}"));
}
@Test
public void testStreamInsideJsonWriterObject() {
  ByteArrayOutputStream os = new ByteArrayOutputStream();
  PrintWriter writer = new PrintWriter(os);
  JSONOutputter.Options options = new JSONOutputter.Options();
  JSONOutputter.JSONWriter jsonWriter = new JSONOutputter.JSONWriter(writer, options);

  jsonWriter.object(out -> {
    List<String> list = Arrays.asList("a", "b", "c");
    out.set("letters", list.stream());
  });
  jsonWriter.flush();
  String json = os.toString();
  assertTrue(json.contains("[\"a\", \"b\", \"c\"]"));
}
@Test
public void testWriterWithBooleanValues() {
  ByteArrayOutputStream os = new ByteArrayOutputStream();
  PrintWriter writer = new PrintWriter(os);
  JSONOutputter.Options options = new JSONOutputter.Options();
  JSONOutputter.JSONWriter jsonWriter = new JSONOutputter.JSONWriter(writer, options);

  jsonWriter.object(out -> {
    out.set("isTrue", true);
    out.set("isFalse", false);
  });
  jsonWriter.flush();
  String json = os.toString();
  assertTrue(json.contains("\"isTrue\": true"));
  assertTrue(json.contains("\"isFalse\": false"));
}
@Test
public void testJsonWriterWithNullKeyValue() {
  ByteArrayOutputStream os = new ByteArrayOutputStream();
  PrintWriter writer = new PrintWriter(os);
  JSONOutputter.Options options = new JSONOutputter.Options();
  JSONOutputter.JSONWriter jsonWriter = new JSONOutputter.JSONWriter(writer, options);

  jsonWriter.object(out -> {
    out.set(null, "value");
    out.set("key", null);
  });
  jsonWriter.flush();
  String json = os.toString();
  assertTrue(json.contains("{"));
  assertTrue(json.contains("}"));
  assertFalse(json.contains("value"));
  assertFalse(json.contains("key"));
}
@Test
public void testJsonWriterWithCharArraySerialization() {
  ByteArrayOutputStream os = new ByteArrayOutputStream();
  PrintWriter pw = new PrintWriter(os);
  JSONOutputter.Options options = new JSONOutputter.Options();
  JSONOutputter.JSONWriter writer = new JSONOutputter.JSONWriter(pw, options);

  char[] charArray = new char[]{'x', 'y', 'z'};
  writer.object(out -> out.set("chars", charArray));
  writer.flush();
  String json = os.toString();
  assertTrue(json.contains("[\"x\", \"y\", \"z\"]") || json.contains("[x, y, z]")); 
}
@Test
public void testJsonWriterWithFloatPrecisionBoundaries() {
  ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
  PrintWriter writer = new PrintWriter(outputStream);
  JSONOutputter.Options options = new JSONOutputter.Options();
  JSONOutputter.JSONWriter jsonWriter = new JSONOutputter.JSONWriter(writer, options);

  jsonWriter.object(out -> {
    out.set("floatMaxPrecision", 1234567.1234567f);
  });
  jsonWriter.flush();

  String json = outputStream.toString();
  assertTrue(json.contains("1234567.1234567") || json.contains("1234567.123")); 
}
@Test
public void testJsonWriterWithDoublePrecisionBoundary() {
  ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
  PrintWriter writer = new PrintWriter(outputStream);
  JSONOutputter.Options options = new JSONOutputter.Options();
  JSONOutputter.JSONWriter jsonWriter = new JSONOutputter.JSONWriter(writer, options);

  jsonWriter.object(out -> {
    out.set("doubleValue", 123456789.12345678901234d);
  });
  jsonWriter.flush();

  String json = outputStream.toString();
  assertTrue(json.contains("123456789.1234") || json.contains("123456789.123")); 
}
@Test
public void testAnnotationWithSectionWithoutSentences() throws IOException {
  Annotation annotation = new Annotation("");

  CoreMap section = new Annotation("");
  section.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  section.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 50);
  section.set(CoreAnnotations.AuthorAnnotation.class, "Tester");
  section.set(CoreAnnotations.SectionDateAnnotation.class, "2023-11-11");

  List<CoreMap> sections = new ArrayList<>();
  sections.add(section);
  annotation.set(CoreAnnotations.SectionsAnnotation.class, sections);

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"charBegin\": 0"));
  assertTrue(json.contains("\"author\": \"Tester\""));
  assertTrue(json.contains("\"dateTime\": \"2023-11-11\""));
}
@Test
public void testConstituencyTreeSkippedMarkerIsExcluded() throws IOException {
  Annotation annotation = new Annotation("");

  Tree dummyTree = new LabeledScoredTreeFactory().newLeaf("SENTENCE_SKIPPED_OR_UNPARSABLE");

  CoreMap sentence = new Annotation("");
  sentence.set(TreeCoreAnnotations.TreeAnnotation.class, dummyTree);
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  List<CoreMap> sentences = new ArrayList<>();
  sentences.add(sentence);

  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
  String json = JSONOutputter.jsonPrint(annotation);
  assertFalse(json.contains("\"parse\""));
}
@Test
public void testDependencyTreeWithEdgeOnly() throws IOException {
  Annotation annotation = new Annotation("");

  IndexedWord governor = new IndexedWord(new Word("likes"));
  governor.setIndex(1);

  IndexedWord dependent = new IndexedWord(new Word("pizza"));
  dependent.setIndex(2);

  SemanticGraph graph = new SemanticGraph();
  graph.addVertex(governor);
  graph.addVertex(dependent);
  graph.addEdge(governor, dependent, GrammaticalRelation.valueOf("dobj"), 1.0, false);

  CoreMap sentence = new Annotation("");
  sentence.set(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class, graph);
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"dep\": \"dobj\""));
  assertTrue(json.contains("\"governorGloss\": \"likes\""));
  assertTrue(json.contains("\"dependentGloss\": \"pizza\""));
}
@Test
public void testEntityMentionWithNullTokenOffsets() throws IOException {
  Annotation annotation = new Annotation("");

  CoreLabel mention = new CoreLabel();
  mention.set(CoreAnnotations.TextAnnotation.class, "OrganizationX");
  mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORG");
  mention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
  mention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 20);

  List<CoreLabel> mentions = new ArrayList<>();
  mentions.add(mention);

  CoreMap sentence = new Annotation("");
//  sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
  sentence.set(CoreAnnotations.TokenBeginAnnotation.class, null);
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"text\": \"OrganizationX\""));
  assertFalse(json.contains("\"tokenBegin\"")); 
}
@Test
public void testJsonWriterWithArrayOfMixedTypes() {
  ByteArrayOutputStream os = new ByteArrayOutputStream();
  PrintWriter writer = new PrintWriter(os);
  JSONOutputter.Options options = new JSONOutputter.Options();
  JSONOutputter.JSONWriter jsonWriter = new JSONOutputter.JSONWriter(writer, options);

  Object[] mixed = new Object[]{1, "text", true, 3.14, null};

  jsonWriter.object(out -> {
    out.set("mixedArray", mixed);
  });
  jsonWriter.flush();
  String json = os.toString();
  assertTrue(json.contains("1"));
  assertTrue(json.contains("\"text\""));
  assertTrue(json.contains("true"));
  assertTrue(json.contains("3.14") || json.matches(".*3\\.\\d+.*"));
}
@Test
public void testJsonWriterWithBooleanArray() {
  ByteArrayOutputStream os = new ByteArrayOutputStream();
  PrintWriter writer = new PrintWriter(os);
  JSONOutputter.Options options = new JSONOutputter.Options();
  JSONOutputter.JSONWriter jsonWriter = new JSONOutputter.JSONWriter(writer, options);

  boolean[] flags = new boolean[]{true, false, true};

  jsonWriter.object(out -> {
    out.set("flags", flags);
  });
  jsonWriter.flush();
  String json = os.toString();
  assertTrue(json.contains("[true, false, true]") || json.replaceAll("\\s", "").contains("[true,false,true]"));
}
@Test
public void testEmptyTokensAnnotationWithoutSentences() throws IOException {
  Annotation annotation = new Annotation("Just text");
  annotation.set(CoreAnnotations.TextAnnotation.class, "Just text");
  annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"tokens\": []"));
}
@Test
public void testTokenWithUnicodeAndJsonEscapingCharacters() throws IOException {
  Annotation annotation = new Annotation("Text with \"quotes\" and \\slashes\\");
  CoreLabel token = new CoreLabel();
  token.setIndex(1);
  token.setWord("\"quoted\\\"");
  token.setOriginalText("\"quoted\\\"");
  token.setBeginPosition(0);
  token.setEndPosition(10);

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);
  CoreMap sentence = new Annotation("");
  sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\\\"quoted\\\\\\\"")); 
}
@Test
public void testMultipleSentencesDifferentAnnotationsPresence() throws IOException {
  Annotation annotation = new Annotation("");

  CoreMap sentence1 = new Annotation("First");
  sentence1.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  CoreMap sentence2 = new Annotation("Second");
  sentence2.set(CoreAnnotations.SentenceIndexAnnotation.class, 1);
  Tree tree = new LabeledScoredTreeFactory().newLeaf("ROOT");
  sentence2.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
  SemanticGraph depGraph = new SemanticGraph();
  sentence2.set(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class, depGraph);

  List<CoreMap> sentenceList = Arrays.asList(sentence1, sentence2);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"sentences\""));
  assertTrue(json.contains("ROOT") || json.contains("\"parse\""));
}
@Test
public void testEmptyRelationTripleSpans() throws IOException {
  Annotation annotation = new Annotation("");

  RelationTriple triple = mock(RelationTriple.class);
  when(triple.subjectGloss()).thenReturn("A");
  when(triple.objectGloss()).thenReturn("B");
  when(triple.relationGloss()).thenReturn("likes");
  when(triple.subjectTokenSpan()).thenReturn(null);
  when(triple.relationTokenSpan()).thenReturn(null);
  when(triple.objectTokenSpan()).thenReturn(null);

  CoreMap sentence = new Annotation("");
  sentence.set(NaturalLogicAnnotations.RelationTriplesAnnotation.class, Collections.singletonList(triple));
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"subject\": \"A\""));
  assertTrue(json.contains("\"relation\": \"likes\""));
  assertTrue(json.contains("\"object\": \"B\""));
}
@Test
public void testDocumentLevelCodepointOffsetsOnly() throws IOException {
  Annotation annotation = new Annotation("Unicode 🎉");

  CoreLabel token = new CoreLabel();
  token.setIndex(1);
  token.setWord("🎉");
  token.setOriginalText("🎉");
  token.setBeginPosition(0);
  token.setEndPosition(2);
  token.set(CoreAnnotations.CodepointOffsetBeginAnnotation.class, 0);
  token.set(CoreAnnotations.CodepointOffsetEndAnnotation.class, 1);

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"codepointOffsetBegin\": 0"));
  assertTrue(json.contains("\"codepointOffsetEnd\": 1"));
}
@Test
public void testEmptyQuotesList() throws IOException {
  Annotation annotation = new Annotation("Text with no quotations.");
  annotation.set(CoreAnnotations.QuotationsAnnotation.class, new ArrayList<>());

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"quotes\": []"));
}
@Test
public void testTimexWithNullTypeAndTid() throws IOException {
  Annotation annotation = new Annotation("Date test");

  Timex timex = new Timex(null, null);
//  timex.setValue("2024-05-20");

  CoreLabel token = new CoreLabel();
  token.setWord("May 20");
  token.set(TimeAnnotations.TimexAnnotation.class, timex);

  CoreMap sentence = new Annotation("");
  sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"value\": \"2024-05-20\""));
}
@Test
public void testDependencyTreeWithNoGovernorDependentIndices() throws IOException {
  Annotation annotation = new Annotation("");

  IndexedWord governor = new IndexedWord();
  governor.setWord("walks");

  IndexedWord dependent = new IndexedWord();
  dependent.setWord("John");

  SemanticGraph graph = new SemanticGraph();
  graph.addVertex(governor);
  graph.addVertex(dependent);
  graph.addEdge(governor, dependent, GrammaticalRelation.valueOf("nsubj"), 1.0, false);

  CoreMap sentence = new Annotation("");
  sentence.set(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class, graph);
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"dep\": \"nsubj\""));
  assertTrue(json.contains("\"dependentGloss\": \"John\""));
  assertTrue(json.contains("\"governorGloss\": \"walks\""));
}
@Test
public void testSentenceWithOnlyBinaryParseTree() throws IOException {
  Annotation annotation = new Annotation("");

  Tree binTree = new LabeledScoredTreeFactory().newLeaf("BIN_TREE");

  CoreMap sentence = new Annotation("");
  sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binTree);
  sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
  List<CoreMap> sentenceList = new ArrayList<>();
  sentenceList.add(sentence);

  annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);
  String json = JSONOutputter.jsonPrint(annotation);
  assertTrue(json.contains("\"binaryParse\""));
} 
}