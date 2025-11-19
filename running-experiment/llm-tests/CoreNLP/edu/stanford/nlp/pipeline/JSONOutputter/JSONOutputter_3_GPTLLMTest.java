package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.ie.machinereading.structure.Span;
import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
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
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class JSONOutputter_3_GPTLLMTest {

 @Test
  public void testJsonPrintIncludesDocumentMetadata() throws Exception {
    Annotation annotation = new Annotation("Text");
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "doc001");
    annotation.set(CoreAnnotations.DocDateAnnotation.class, "2024-01-01");
    annotation.set(CoreAnnotations.TextAnnotation.class, "This is a test.");

    String json = JSONOutputter.jsonPrint(annotation);

    assertNotNull(json);
    assertTrue(json.contains("\"docId\""));
    assertTrue(json.contains("doc001"));
    assertTrue(json.contains("\"docDate\""));
    assertTrue(json.contains("2024-01-01"));
    assertTrue(json.contains("This is a test"));
  }
@Test
  public void testJsonPrintIncludesSingleToken() throws Exception {
    Annotation annotation = new Annotation("Hi");
    CoreLabel token = new CoreLabel();
    token.setWord("Hello");
    token.setOriginalText("Hello");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "UH");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "GREETING");
    token.setIndex(1);
    token.setBeginPosition(0);
    token.setEndPosition(5);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    String json = JSONOutputter.jsonPrint(annotation);

    assertTrue(json.contains("Hello"));
    assertTrue(json.contains("GREETING"));
    assertTrue(json.contains("UH"));
    assertTrue(json.contains("tokens"));
  }
@Test
  public void testJsonPrintIncludesParseTree() throws Exception {
    Annotation annotation = new Annotation("A.");
    CoreLabel token = new CoreLabel();
    token.setIndex(1);
    token.setWord("A");
    token.setOriginalText("A");
    token.setBeginPosition(0);
    token.setEndPosition(1);

    Annotation sentence = new Annotation("A.");
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Tree tree = Tree.valueOf("(ROOT (NP (DT A)))");
    sentence.set(CoreAnnotations.TreeAnnotation.class, tree);

    List<Annotation> sentences = new ArrayList<>();
    sentences.add(sentence);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    String json = JSONOutputter.jsonPrint(annotation);

    assertTrue(json.contains("ROOT"));
    assertTrue(json.contains("NP"));
    assertTrue(json.contains("DT"));
    assertTrue(json.contains("A"));
  }
@Test
  public void testJsonWriterNestedStructure() {
    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set("outer", (JSONOutputter.Writer) (key, value) -> {
        if ("inner".equals(key)) {
          assertEquals("inside", value);
        }
      });
      writer.set("inner", "inside");
    });

    assertTrue(json.contains("outer"));
    assertTrue(json.contains("inner"));
    assertTrue(json.contains("inside"));
  }
@Test
  public void testJsonWriterPrimitiveTypes() {
    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set("int", 42);
      writer.set("bool", true);
      writer.set("float", 3.14f);
      writer.set("double", 2.71828d);
    });

    assertTrue(json.contains("42"));
    assertTrue(json.contains("true"));
    assertTrue(json.contains("3.14"));
    assertTrue(json.contains("2.71828"));
  }
@Test
  public void testJsonPrintHandlesCorefChains() throws Exception {
    Annotation annotation = new Annotation("Obama was president.");

    CorefMention mention = new CorefMention(1, 0, 0, 1, 1, "Obama", "NOMINATIVE", "SINGULAR", "MALE", "ANIMATE", new int[]{0});
    CorefChain chain = new CorefChain(1, Collections.singletonList(mention));

    Map<Integer, CorefChain> corefMap = new HashMap<>();
    corefMap.put(1, chain);

    annotation.set(CoreAnnotations.TextAnnotation.class, "Obama was president.");
    annotation.set(edu.stanford.nlp.coref.CorefCoreAnnotations.CorefChainAnnotation.class, corefMap);

    String json = JSONOutputter.jsonPrint(annotation);

    assertTrue(json.contains("corefs"));
    assertTrue(json.contains("Obama"));
  }
@Test
  public void testJsonPrintSentimentAnnotatedTree() throws Exception {
    Annotation annotation = new Annotation("Great movie!");

    Annotation sentence = new Annotation("Great movie!");
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Tree sentimentTree = Tree.valueOf("(3 (2 Great) (4 movie!))");

    sentence.set(SentimentCoreAnnotations.SentimentAnnotatedTree.class, sentimentTree);
    sentence.set(SentimentCoreAnnotations.SentimentClass.class, "Positive");

    List<Annotation> sentences = new ArrayList<>();
    sentences.add(sentence);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    String json = JSONOutputter.jsonPrint(annotation);

    assertTrue(json.contains("sentimentValue"));
    assertTrue(json.contains("Positive"));
    assertTrue(json.contains("sentimentTree"));
  }
@Test
  public void testJsonPrintEntityWithTimex() throws Exception {
    Annotation annotation = new Annotation("Yesterday was cold.");

    CoreLabel token = new CoreLabel();
    token.setWord("Yesterday");
    token.setIndex(1);
    token.setBeginPosition(0);
    token.setEndPosition(9);

    Timex timex = new Timex("t1", "DATE", "2024-05-12", null);

    token.set(TimeAnnotations.TimexAnnotation.class, timex);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    Annotation sentence = new Annotation("Yesterday was cold.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    List<Annotation> sentences = new ArrayList<>();
    sentences.add(sentence);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    String json = JSONOutputter.jsonPrint(annotation);
    assertTrue(json.contains("timex"));
    assertTrue(json.contains("2024-05-12"));
  }
@Test
  public void testJsonPrintToStreamPrettyEnabled() throws Exception {
    Annotation annotation = new Annotation("Hello World!");
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "docABC");

    JSONOutputter.Options options = new JSONOutputter.Options();
    options.pretty = true;

    OutputStream out = new ByteArrayOutputStream();

    JSONOutputter.jsonPrint(annotation, out, options);

    String output = out.toString();
    assertTrue(output.contains("docABC"));
    assertTrue(output.contains("\n")); 
  }
@Test(expected = RuntimeException.class)
  public void testJsonWriterFailsOnUnsupportedType() {
    JSONOutputter.JSONWriter.objectToJSON(writer -> writer.set("badKey", new Object()));
  }
@Test
  public void testJsonWriterNullSuppressed() {
    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set("nullValue", null);
      writer.set(null, "test");
    });

    assertFalse(json.contains("nullValue"));
    assertFalse(json.contains("test"));
  }
@Test
  public void testEmptyAnnotationYieldsValidJson() throws Exception {
    Annotation annotation = new Annotation("");
    String json = JSONOutputter.jsonPrint(annotation);
    assertNotNull(json);
    assertTrue(json.startsWith("{"));
    assertTrue(json.endsWith("}"));
  }
@Test
  public void testMalformedTreeSkippedInOutput() throws Exception {
    Annotation annotation = new Annotation("Skipped.");
    CoreLabel token = new CoreLabel();
    token.setWord("Skipped");
    token.setIndex(1);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    Annotation sentence = new Annotation("Skipped.");
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TreeAnnotation.class, Tree.valueOf("SENTENCE_SKIPPED_OR_UNPARSABLE"));
    List<Annotation> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    String json = JSONOutputter.jsonPrint(annotation);
    assertFalse(json.contains("parse"));
  }
@Test
  public void testEmptyDependencyGraphHandledAsNull() throws Exception {
    Annotation annotation = new Annotation("Fake.");
    Annotation sentence = new Annotation("Fake.");
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class, null);
    List<Annotation> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    String json = JSONOutputter.jsonPrint(annotation);
    assertTrue(json.contains("basicDependencies"));
    assertTrue(json.contains("null"));
  }
@Test
  public void testMultipleOpenIETriplesWritten() throws Exception {
    Annotation annotation = new Annotation("Obama was president.");
    CoreLabel token = new CoreLabel();
    token.setWord("Obama");
    token.setIndex(1);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    RelationTriple triple1 = new RelationTriple(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), 0.9, 0.5, 0.7) {
      @Override public String subjectGloss() { return "Obama"; }
      @Override public String relationGloss() { return "was"; }
      @Override public String objectGloss() { return "president"; }
      @Override public edu.stanford.nlp.util.Pair<Integer, Integer> subjectTokenSpan() { return new edu.stanford.nlp.util.Pair<>(0, 1); }
      @Override public edu.stanford.nlp.util.Pair<Integer, Integer> relationTokenSpan() { return new edu.stanford.nlp.util.Pair<>(1, 2); }
      @Override public edu.stanford.nlp.util.Pair<Integer, Integer> objectTokenSpan() { return new edu.stanford.nlp.util.Pair<>(2, 3); }
    };

    List<RelationTriple> triples = new ArrayList<>();
    triples.add(triple1);

    Annotation sentence = new Annotation("Obama was president.");
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(NaturalLogicAnnotations.RelationTriplesAnnotation.class, triples);

    List<Annotation> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    String json = JSONOutputter.jsonPrint(annotation);
    assertTrue(json.contains("Obama"));
    assertTrue(json.contains("president"));
    assertTrue(json.contains("openie"));
  }
@Test
  public void testEmptyTokenListHandled() throws Exception {
    Annotation annotation = new Annotation("Empty sentence");
    Annotation sentence = new Annotation("Empty sentence");
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    List<Annotation> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    String json = JSONOutputter.jsonPrint(annotation);
    assertTrue(json.contains("tokens"));
  }
@Test
  public void testQuoteWithMinimalAttributes() throws Exception {
    Annotation annotation = new Annotation("\"Quoted\" word.");
    Annotation quote = new Annotation("Quoted");
    quote.set(CoreAnnotations.QuotationIndexAnnotation.class, 1);
    quote.set(CoreAnnotations.TextAnnotation.class, "Quoted");
    quote.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    quote.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
    List<Annotation> quotes = new ArrayList<>();
    quotes.add(quote);
    annotation.set(CoreAnnotations.QuotationsAnnotation.class, quotes);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>()); 
    String json = JSONOutputter.jsonPrint(annotation);
    assertTrue(json.contains("quotes"));
    assertTrue(json.contains("Quoted"));
  }
@Test
  public void testSectionsFieldWithMinimalData() throws Exception {
    Annotation annotation = new Annotation("Sectioned text.");
    Annotation section = new Annotation("Section");
    section.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
    section.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 100);
    List<Annotation> sentences = new ArrayList<>();
    Annotation sentence = new Annotation("Sentence");
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentences.add(sentence);
    section.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    List<Annotation> sections = new ArrayList<>();
    sections.add(section);
    annotation.set(CoreAnnotations.SectionsAnnotation.class, sections);
    String json = JSONOutputter.jsonPrint(annotation);
    assertTrue(json.contains("sections"));
    assertTrue(json.contains("charBegin"));
    assertTrue(json.contains("sentenceIndexes"));
  }
@Test
  public void testArrayOfPrimitivesSerializedCorrectly() {
    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set("intArray", new int[]{1, 2, 3});
      writer.set("boolArray", new boolean[]{true, false});
      writer.set("floatArray", new float[]{1.1f, 2.2f});
    });
    assertTrue(json.contains("["));
    assertTrue(json.contains("1"));
    assertTrue(json.contains("true"));
    assertTrue(json.contains("2.2"));
  }
@Test
  public void testInvalidArrayTypeThrows() {
    try {
      JSONOutputter.JSONWriter.objectToJSON(writer -> {
        writer.set("badArray", new Object[]{
            new Object(), new Object()
        });
      });
      fail("Expected RuntimeException");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("Unknown object"));
    }
  }
@Test
  public void testSentimentClassSpaceStripping() throws Exception {
    Annotation annotation = new Annotation("Test.");
    Annotation sentence = new Annotation("Test.");
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    Tree tree = Tree.valueOf("(3 (2 Test))");
    sentence.set(SentimentCoreAnnotations.SentimentAnnotatedTree.class, tree);
    sentence.set(SentimentCoreAnnotations.SentimentClass.class, "Neutral Class");
    List<Annotation> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    String json = JSONOutputter.jsonPrint(annotation);
    assertTrue(json.contains("NeutralClass")); 
  }
@Test
  public void testJsonWriterStreamValue() {
    String json = JSONWriter.objectToJSON(writer -> {
      writer.set("stream", Arrays.asList("a", "b", "c").stream());
    });
    assertTrue(json.contains("a"));
    assertTrue(json.contains("b"));
    assertTrue(json.contains("c"));
  }
@Test
  public void testRouteObjectHandlesEnum() {
    String json = JSONWriter.objectToJSON(writer -> {
      writer.set("enum", Thread.State.NEW);
    });
    assertTrue(json.contains("\"NEW\""));
  }
@Test
  public void testRouteObjectHandlesNullValue() {
    String json = JSONWriter.objectToJSON(writer -> {
      writer.set("field", null);
    });
    assertFalse(json.contains("field"));
  }
@Test
  public void testArrayTypedValuesIncludingPrimitive() {
    String json = JSONWriter.objectToJSON(writer -> {
      writer.set("ints", new int[]{10, 20});
      writer.set("floats", new float[]{1.1f, 2.2f});
      writer.set("bools", new boolean[]{true});
    });
    assertTrue(json.contains("10"));
    assertTrue(json.contains("2.2"));
    assertTrue(json.contains("true"));
  }
@Test
  public void testObjectArraySerializedAsList() {
    String json = JSONWriter.objectToJSON(writer -> {
      Object[] objects = new Object[2];
      objects[0] = "first";
      objects[1] = "second";
      writer.set("objects", objects);
    });
    assertTrue(json.contains("first"));
    assertTrue(json.contains("second"));
  }
@Test
  public void testPairObjectSerializedAsArray() {
    String json = JSONWriter.objectToJSON(writer -> {
      writer.set("pair", new Pair<>("x", "y"));
    });
    assertTrue(json.contains("x"));
    assertTrue(json.contains("y"));
    assertTrue(json.contains("["));
  }
@Test
  public void testSpanSerializationShortcut() {
    String json = JSONWriter.objectToJSON(writer -> {
      writer.set("span", edu.stanford.nlp.ie.machinereading.structure.Span.fromValues(3, 7));
    });
    assertTrue(json.contains("3"));
    assertTrue(json.contains("7"));
    assertTrue(json.contains("["));
  }
@Test
  public void testTimexFullSerialization() throws Exception {
    Annotation annotation = new Annotation("Timex-x");
    CoreLabel token = new CoreLabel();
    token.setWord("yesterday");
    token.setIndex(1);
    Timex.Range range = new Timex.Range(100, 200, "1 day");
    Timex timex = new Timex("t5", "DATE", "2023-01-01", null);
    timex.setRange(range);
    token.set(TimeAnnotations.TimexAnnotation.class, timex);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    Annotation sentence = new Annotation("yesterday");
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    List<Annotation> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    String output = JSONOutputter.jsonPrint(annotation);
    assertTrue(output.contains("timex"));
    assertTrue(output.contains("t5"));
    assertTrue(output.contains("DATE"));
    assertTrue(output.contains("2023-01-01"));
    assertTrue(output.contains("range"));
    assertTrue(output.contains("1 day"));
  }
@Test
  public void testJSONPrintWithNoSentencesButTokens() throws Exception {
    Annotation annotation = new Annotation("Text tokenized no sentence.");
    CoreLabel token = new CoreLabel();
    token.setWord("onlyToken");
    token.setOriginalText("onlyToken");
    token.setBeginPosition(0);
    token.setEndPosition(9);
    token.setIndex(1);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    String output = JSONOutputter.jsonPrint(annotation);
    assertTrue(output.contains("tokens"));
    assertTrue(output.contains("onlyToken"));
  }
@Test
  public void testJSONPrintHandlesNoOutputStreamEncoding() throws Exception {
    Annotation annotation = new Annotation("Something.");
    OutputStream os = new StringOutputStream();
    JSONOutputter.jsonPrint(annotation, os);
    String result = os.toString();
    assertTrue(result.contains("{"));
  }

  @Test
  public void testTreePrinterDefaultOnelineFallback() throws Exception {
    Annotation annotation = new Annotation("Hi.");
    CoreLabel token = new CoreLabel();
    token.setWord("Hi");
    token.setOriginalText("Hi");
    token.setBeginPosition(0);
    token.setEndPosition(2);
    token.setIndex(1);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    Annotation sentence = new Annotation("Hi.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    Tree tree = Tree.valueOf("(ROOT (INTJ Hi))");
    sentence.set(CoreAnnotations.TreeAnnotation.class, tree);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    List<Annotation> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    String json = JSONOutputter.jsonPrint(annotation);
    assertTrue(json.contains("ROOT"));
    assertTrue(json.contains("INTJ"));
    assertTrue(json.contains("Hi"));
  }

  @Test
  public void testWriteTimeSkipsWithoutRange() throws Exception {
    Annotation annotation = new Annotation("This morning");
    CoreLabel token = new CoreLabel();
    Timex timex = new Timex("T1", "TIME", "06:00", null);
    token.set(TimeAnnotations.TimexAnnotation.class, timex);
    token.setWord("morning");
    token.setIndex(1);
    token.setBeginPosition(5);
    token.setEndPosition(12);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    Annotation sentence = new Annotation("This morning");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    List<Annotation> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    String json = JSONOutputter.jsonPrint(annotation);
    assertTrue(json.contains("timex"));
    assertTrue(json.contains("T1"));
    assertTrue(json.contains("06:00"));
    assertFalse(json.contains("range")); 
  }

  @Test
  public void testJSONWriterObjectWithConsumerValue() {
    String json = JSONWriter.objectToJSON(writer -> {
      writer.set("metadata", (JSONOutputter.Writer) (key, value) -> {
        if ("version".equals(key)) {
          assertEquals("1.0", value);
        }
      });
      writer.set("version", "1.0");
    });
    assertTrue(json.contains("version"));
    assertTrue(json.contains("1.0"));
  }
}
@Test
  public void testTreePrinterDefaultOnelineFallback() throws Exception {
    Annotation annotation = new Annotation("Hi.");
    CoreLabel token = new CoreLabel();
    token.setWord("Hi");
    token.setOriginalText("Hi");
    token.setBeginPosition(0);
    token.setEndPosition(2);
    token.setIndex(1);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    Annotation sentence = new Annotation("Hi.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    Tree tree = Tree.valueOf("(ROOT (INTJ Hi))");
    sentence.set(CoreAnnotations.TreeAnnotation.class, tree);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    List<Annotation> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    String json = JSONOutputter.jsonPrint(annotation);
    assertTrue(json.contains("ROOT"));
    assertTrue(json.contains("INTJ"));
    assertTrue(json.contains("Hi"));
  }
@Test
  public void testWriteTimeSkipsWithoutRange() throws Exception {
    Annotation annotation = new Annotation("This morning");
    CoreLabel token = new CoreLabel();
    Timex timex = new Timex("T1", "TIME", "06:00", null);
    token.set(TimeAnnotations.TimexAnnotation.class, timex);
    token.setWord("morning");
    token.setIndex(1);
    token.setBeginPosition(5);
    token.setEndPosition(12);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    Annotation sentence = new Annotation("This morning");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    List<Annotation> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    String json = JSONOutputter.jsonPrint(annotation);
    assertTrue(json.contains("timex"));
    assertTrue(json.contains("T1"));
    assertTrue(json.contains("06:00"));
    assertFalse(json.contains("range")); 
  }
@Test
  public void testJSONWriterObjectWithConsumerValue() {
    String json = JSONWriter.objectToJSON(writer -> {
      writer.set("metadata", (JSONOutputter.Writer) (key, value) -> {
        if ("version".equals(key)) {
          assertEquals("1.0", value);
        }
      });
      writer.set("version", "1.0");
    });
    assertTrue(json.contains("version"));
    assertTrue(json.contains("1.0"));
  }
@Test
  public void testEntityMentionWithoutTokenOffsets() throws Exception {
    Annotation annotation = new Annotation("Apple was founded.");
    CoreLabel mention = new CoreLabel();
    mention.set(CoreAnnotations.TextAnnotation.class, "Apple");
    mention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    mention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(mention);
    Annotation sentence = new Annotation("Apple was founded.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    String json = JSONOutputter.jsonPrint(annotation);
    assertTrue(json.contains("Apple"));
    assertTrue(json.contains("ner"));
    assertTrue(json.contains("ORGANIZATION"));
  }
@Test
  public void testEmptyKbpTriplesHandledCorrectly() throws Exception {
    Annotation annotation = new Annotation("Bill Gates was CEO of Microsoft.");
    Annotation sentence = new Annotation("Sentence");
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.KBPTriplesAnnotation.class, new ArrayList<RelationTriple>());
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    String json = JSONOutputter.jsonPrint(annotation);
    assertTrue(json.contains("kbp")); 
  }
@Test
  public void testEntityMentionWithoutNERConfidence() throws Exception {
    Annotation annotation = new Annotation("Google was created.");
    CoreLabel mention = new CoreLabel();
    mention.set(CoreAnnotations.TextAnnotation.class, "Google");
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORGANIZATION");
    List<CoreMap> mentions = new ArrayList<>();
    mentions.add(mention);
    Annotation sentence = new Annotation("Sentence");
    sentence.set(CoreAnnotations.MentionsAnnotation.class, mentions);
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    List<CoreMap> sents = new ArrayList<>();
    sents.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sents);
    String json = JSONOutputter.jsonPrint(annotation);
    assertTrue(json.contains("Google"));
    assertFalse(json.contains("nerConfidences"));
  }
@Test
  public void testCorefChainWithNullRepresentativeMentionHandled() throws Exception {
    Annotation annotation = new Annotation("Sentence");
    CorefMention mention = new CorefMention(0, 0, 0, 1, 1, "Entity", "NOMINATIVE", "SINGULAR", "NEUTRAL", "INANIMATE", new int[]{0});
    CorefChain chain = new CorefChain(5, Arrays.asList(mention)) {
      @Override
      public CorefMention getRepresentativeMention() {
        return null; 
      }
    };
    Map<Integer, CorefChain> chains = new HashMap<>();
    chains.put(5, chain);
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, chains);
    String json = JSONOutputter.jsonPrint(annotation);
    assertTrue(json.contains("corefs"));
    assertTrue(json.contains("Entity"));
  }
@Test
  public void testQuoteAttributionFallbackToUnknownSpeaker() throws Exception {
    Annotation quote = new Annotation("Quote");
    quote.set(CoreAnnotations.QuotationIndexAnnotation.class, 1);
    quote.set(CoreAnnotations.TextAnnotation.class, "I am a quote.");
    quote.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    quote.set(CoreAnnotations.TokenEndAnnotation.class, 3);
    quote.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    quote.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 14);
    quote.set(CoreAnnotations.SentenceBeginAnnotation.class, 0);
    quote.set(CoreAnnotations.SentenceEndAnnotation.class, 0);
    List<CoreMap> quoteList = new ArrayList<>();
    quoteList.add(quote);
    Annotation annotation = new Annotation("I am a quote.");
    annotation.set(CoreAnnotations.QuotationsAnnotation.class, quoteList);
    List<CoreMap> sentenceList = new ArrayList<>();
    Annotation sentence = new Annotation("I am a quote.");
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentenceList.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);
    String output = JSONOutputter.jsonPrint(annotation);
    assertTrue(output.contains("I am a quote."));
    assertTrue(output.contains("Unknown"));
    assertTrue(output.contains("speaker"));
  }
@Test
  public void testSentenceWithOnlyEnhancedDependencies() throws Exception {
    Annotation annotation = new Annotation("Sentence");
    Annotation sentence = new Annotation("Sentence");
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(SemanticGraphCoreAnnotations.EnhancedDependenciesAnnotation.class, null); 
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    String output = JSONOutputter.jsonPrint(annotation);
    assertTrue(output.contains("enhancedDependencies"));
    assertTrue(output.contains("null"));
  }
@Test
  public void testTokensWithCodepointOffsets() throws Exception {
    Annotation annotation = new Annotation("Emoji 😊");
    CoreLabel token = new CoreLabel();
    token.setWord("Emoji");
    token.setOriginalText("Emoji");
    token.setIndex(1);
    token.setBeginPosition(0);
    token.setEndPosition(5);
    token.set(CoreAnnotations.CodepointOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CodepointOffsetEndAnnotation.class, 5);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    Annotation sentence = new Annotation("Emoji sentence");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    String json = JSONOutputter.jsonPrint(annotation);
    assertTrue(json.contains("codepointOffsetBegin"));
    assertTrue(json.contains("0"));
    assertTrue(json.contains("5"));
  }
@Test
  public void testBinaryParsePresentWithTree() throws Exception {
    Annotation annotation = new Annotation("Parse this.");
    CoreLabel token = new CoreLabel();
    token.setWord("Hello");
    token.setIndex(1);
    token.setBeginPosition(0);
    token.setEndPosition(5);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    Tree tree = Tree.valueOf("(ROOT (NP Hello))");
    Tree binarizedTree = Tree.valueOf("(ROOT (LST Hello))");

    Annotation sentence = new Annotation("Parse this.");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TreeAnnotation.class, tree);
    sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binarizedTree);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    String json = JSONOutputter.jsonPrint(annotation);
    assertTrue(json.contains("parse"));
    assertTrue(json.contains("binaryParse"));
    assertTrue(json.contains("Hello"));
    assertTrue(json.contains("ROOT"));
  }
@Test
  public void testCorefChainWithMultipleMentions() throws Exception {
    Annotation annotation = new Annotation("Steve said he is happy.");
    
    CorefMention mention1 = new CorefMention(1, 0, 0, 1, 1, "Steve", "NOMINATIVE", "SINGULAR", "MALE", "ANIMATE", new int[]{0});
    CorefMention mention2 = new CorefMention(2, 0, 2, 3, 3, "he", "NOMINATIVE", "SINGULAR", "MALE", "ANIMATE", new int[]{0});
    
    List<CorefMention> mentions = new ArrayList<>();
    mentions.add(mention1);
    mentions.add(mention2);
    
    CorefChain chain = new CorefChain(1, mentions);
    Map<Integer, CorefChain> map = new HashMap<>();
    map.put(1, chain);
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, map);
    
    String json = JSONOutputter.jsonPrint(annotation);
    
    assertTrue(json.contains("Steve"));
    assertTrue(json.contains("he"));
    assertTrue(json.contains("isRepresentativeMention"));
  }
@Test
  public void testJSONWriterWithBooleanArray() {
    String json = JSONWriter.objectToJSON(writer -> {
      boolean[] flags = new boolean[2];
      flags[0] = true;
      flags[1] = false;
      writer.set("flags", flags);
    });

    assertTrue(json.contains("true"));
    assertTrue(json.contains("false"));
    assertTrue(json.contains("flags"));
  }
@Test
  public void testJSONWriterWithDoubleArray() {
    String json = JSONWriter.objectToJSON(writer -> {
      double[] values = new double[2];
      values[0] = 1.234567;
      values[1] = 9.87654321;
      writer.set("values", values);
    });

    assertTrue(json.contains("1.234567"));
    assertTrue(json.contains("9.87654321"));
    assertTrue(json.contains("values"));
  }
@Test
  public void testSentenceWithNullTreeIsOmittedFromOutput() throws Exception {
    Annotation annotation = new Annotation("Sentence");

    CoreLabel token = new CoreLabel();
    token.setWord("hello");
    token.setIndex(1);
    token.setBeginPosition(0);
    token.setEndPosition(5);

    Annotation sentence = new Annotation("Sentence");
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, null);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    String output = JSONOutputter.jsonPrint(annotation);

    assertTrue(output.contains("tokens"));
    assertFalse(output.contains("parse"));
  }
@Test
  public void testSentenceWithEmptyEntityMentionList() throws Exception {
    Annotation annotation = new Annotation("Empty Entity Mention List");

    Annotation sentence = new Annotation("Sentence");
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<>());
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    String json = JSONOutputter.jsonPrint(annotation);

    assertTrue(json.contains("entitymentions"));
    assertTrue(json.contains("[]"));
  }
@Test
  public void testCorefMentionWithEmptyPositionArray() throws Exception {
    Annotation annotation = new Annotation("Body text");

    CorefMention mention = new CorefMention(9, 0, 1, 2, 2, "someone", "NOMINATIVE", "PLURAL", "UNKNOWN", "INANIMATE", new int[]{});

    List<CorefMention> mentions = new ArrayList<>();
    mentions.add(mention);

    CorefChain chain = new CorefChain(99, mentions);

    Map<Integer, CorefChain> map = new HashMap<>();
    map.put(99, chain);

    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, map);

    String output = JSONOutputter.jsonPrint(annotation);
    
    assertTrue(output.contains("someone"));
    assertTrue(output.contains("position"));
    assertTrue(output.contains("[]"));
  }
@Test
  public void testTokenWithAllOptionalFieldsSetToNull() throws Exception {
    Annotation annotation = new Annotation("Long word");

    CoreLabel token = new CoreLabel();
    token.setWord("token");
    token.setOriginalText(null);
    token.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, null);
    token.set(CoreAnnotations.SpeakerAnnotation.class, null);
    token.set(CoreAnnotations.SpeakerTypeAnnotation.class, null);
    token.set(CoreAnnotations.TrueCaseAnnotation.class, null);
    token.set(CoreAnnotations.TrueCaseTextAnnotation.class, null);
    token.set(CoreAnnotations.BeforeAnnotation.class, null);
    token.set(CoreAnnotations.AfterAnnotation.class, null);
    token.set(CoreAnnotations.WikipediaEntityAnnotation.class, null);
    token.setIndex(1);
    token.setBeginPosition(0);
    token.setEndPosition(5);

    Annotation sentence = new Annotation("token");
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    String output = JSONOutputter.jsonPrint(annotation);

    assertTrue(output.contains("token"));
    assertTrue(output.contains("index"));
    assertTrue(output.contains("characterOffsetBegin"));
    assertTrue(output.contains("characterOffsetEnd"));
    assertFalse(output.contains("normalizedNER"));
    assertFalse(output.contains("speaker"));
  }
@Test
  public void testTimexWithNullAltValAndRange() throws Exception {
    Annotation annotation = new Annotation("Tomorrow at noon.");

    CoreLabel token = new CoreLabel();
    token.setWord("tomorrow");
    token.setIndex(1);
    token.setBeginPosition(0);
    token.setEndPosition(8);

    Timex timex = new Timex("T2", "TIME", "2024-03-28", null);
    token.set(TimeAnnotations.TimexAnnotation.class, timex);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    Annotation sentence = new Annotation("tomorrow");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    String output = JSONOutputter.jsonPrint(annotation);

    assertTrue(output.contains("T2"));
    assertTrue(output.contains("2024-03-28"));
    assertTrue(output.contains("timex"));
    assertFalse(output.contains("range"));
    assertFalse(output.contains("altValue"));
  }
@Test
  public void testWriterSetNullKey() {
    String json = JSONWriter.objectToJSON(writer -> {
      writer.set(null, "invalid");
      writer.set("valid", "data");
    });

    assertTrue(json.contains("valid"));
    assertTrue(json.contains("data"));
    assertFalse(json.contains("invalid"));
  }
@Test
  public void testWriterSetNullValue() {
    String json = JSONWriter.objectToJSON(writer -> {
      writer.set("nullable", null);
      writer.set("notNull", 123);
    });

    assertTrue(json.contains("notNull"));
    assertTrue(json.contains("123"));
    assertFalse(json.contains("nullable"));
  }
@Test
  public void testWriterHandlesEmptyCollection() {
    String json = JSONWriter.objectToJSON(writer -> {
      writer.set("emptyList", new ArrayList<>());
    });
    assertTrue(json.contains("emptyList"));
    assertTrue(json.contains("["));
    assertTrue(json.contains("]"));
  }
@Test
  public void testWriterHandlesNullStream() {
    String json = JSONWriter.objectToJSON(writer -> {
      writer.set("streamVal", ((java.util.stream.Stream<Object>) null));
    });
    assertFalse(json.contains("streamVal"));
  }
@Test
  public void testWriterHandlesEmptyArray() {
    String json = JSONWriter.objectToJSON(writer -> {
      writer.set("emptyArray", new String[0]);
    });
    assertTrue(json.contains("emptyArray"));
    assertTrue(json.contains("["));
    assertTrue(json.contains("]"));
  }
@Test
  public void testWriterHandlesCharArray() {
    char[] chars = new char[]{'a', 'b'};
    String json = JSONWriter.objectToJSON(writer -> {
      writer.set("letters", chars);
    });
    assertTrue(json.contains("a"));
    assertTrue(json.contains("b"));
  }
@Test(expected = RuntimeException.class)
  public void testWriterThrowsOnUnknownObject() {
    Object unknownType = new java.math.BigDecimal("123.45");
    JSONWriter.objectToJSON(writer -> {
      writer.set("bad", unknownType);
    });
  }
@Test
  public void testWriterHandlesEnumInCollection() {
    List<Thread.State> list = Arrays.asList(Thread.State.NEW, Thread.State.RUNNABLE);
    String json = JSONWriter.objectToJSON(writer -> {
      writer.set("states", list);
    });
    assertTrue(json.contains("NEW"));
    assertTrue(json.contains("RUNNABLE"));
  }
@Test
  public void testWriterHandlesNestedConsumers() {
    String json = JSONWriter.objectToJSON(writer -> {
      writer.set("level1", (JSONOutputter.Writer) (k2, v2) -> {
        if ("level2".equals(k2)) {
          assertTrue(((String) v2).equals("deep"));
        }
      });
      writer.set("level2", "deep");
    });
    assertTrue(json.contains("deep"));
  }
@Test
  public void testJSONWriterWithShortPrimitiveArray() {
    String json = JSONWriter.objectToJSON(writer -> {
      short[] shorts = new short[]{1, 2, 3};
      writer.set("shorts", shorts);
    });
    assertTrue(json.contains("1"));
    assertTrue(json.contains("2"));
    assertTrue(json.contains("3"));
  }
@Test
  public void testNoSentencesButQuotationsExist() throws Exception {
    Annotation annotation = new Annotation("Quoted text");
    Annotation quote = new Annotation("Quoted");
    quote.set(CoreAnnotations.QuotationIndexAnnotation.class, 1);
    quote.set(CoreAnnotations.TextAnnotation.class, "Sample");
    quote.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    quote.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
    annotation.set(CoreAnnotations.QuotationsAnnotation.class, Collections.singletonList(quote));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, null); 
    
    String json = JSONOutputter.jsonPrint(annotation);
    assertTrue(json.contains("quotes"));
    assertTrue(json.contains("Sample"));
  }
@Test
  public void testSentenceWithoutTokensOrTree() throws Exception {
    Annotation annotation = new Annotation("Test sentence.");
    Annotation sentence = new Annotation("Test sentence.");
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokensAnnotation.class, null);
    sentence.set(CoreAnnotations.TreeAnnotation.class, null);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    
    String json = JSONOutputter.jsonPrint(annotation);
    assertTrue(json.contains("sentences"));
    assertTrue(json.contains("id") || json.contains("index")); 
  }
@Test
  public void testMultiSentenceOutputIncludesEachTree() throws Exception {
    Annotation annotation = new Annotation("Sentence one. Sentence two.");
    
    Annotation sentence1 = new Annotation("One.");
    sentence1.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    Tree tree1 = Tree.valueOf("(ROOT (S (NP (NNP One))))");
    sentence1.set(CoreAnnotations.TreeAnnotation.class, tree1);

    Annotation sentence2 = new Annotation("Two.");
    sentence2.set(CoreAnnotations.SentenceIndexAnnotation.class, 1);
    Tree tree2 = Tree.valueOf("(ROOT (S (NP (NNP Two))))");
    sentence2.set(CoreAnnotations.TreeAnnotation.class, tree2);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence1, sentence2));
    
    String json = JSONOutputter.jsonPrint(annotation);
    assertTrue(json.contains("One"));
    assertTrue(json.contains("Two"));
    assertEquals(2, json.split("parse").length - 1); 
  }
@Test
  public void testWriterHandlesMapSetInsideObject() {
    String json = JSONWriter.objectToJSON(writer -> {
      writer.set("map", (JSONOutputter.Writer) (k, v) -> {
        if ("key1".equals(k)) {
          assertEquals("val1", v);
        }
      });
      writer.set("key1", "val1");
    });
    assertTrue(json.contains("key1"));
    assertTrue(json.contains("val1"));
  }
@Test
  public void testCorefMentionHasRepresentativeFlagAsTrue() throws Exception {
    Annotation annotation = new Annotation("Barack Obama was the president.");
    
    CorefMention mention1 = new CorefMention(1, 0, 0, 1, 1, "Barack Obama", "NOMINATIVE", "SINGULAR", "MALE", "ANIMATE", new int[]{0});
    CorefMention mention2 = new CorefMention(2, 0, 5, 6, 6, "the president", "NOMINATIVE", "SINGULAR", "MALE", "ANIMATE", new int[]{0});
    
    List<CorefMention> mentions = Arrays.asList(mention1, mention2);

    CorefChain chain = new CorefChain(101, mentions) {
      @Override
      public CorefMention getRepresentativeMention() {
        return mention1;
      }
    };

    Map<Integer, CorefChain> map = new HashMap<>();
    map.put(101, chain);
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, map);

    String json = JSONOutputter.jsonPrint(annotation);
    assertTrue(json.contains("Barack Obama"));
    assertTrue(json.contains("isRepresentativeMention"));
    assertTrue(json.contains("true"));
  }
@Test
  public void testWriterHandlesBooleanFalse() {
    String json = JSONWriter.objectToJSON(writer -> {
      writer.set("flag", false);
    });
    assertTrue(json.contains("false"));
    assertTrue(json.contains("flag"));
  }
@Test
  public void testWriterHandlesCharacterInput() {
    String json = JSONWriter.objectToJSON(writer -> {
      writer.set("char", 'A');
    });
    assertTrue(json.contains("A"));
    assertTrue(json.contains("char"));
  }
@Test
  public void testWriterHandlesFloatPrecision() {
    String json = JSONWriter.objectToJSON(writer -> {
      writer.set("rate", 3.1415927f);
    });
    assertTrue(json.contains("3.1415927") || json.contains("3.141"));
  }
@Test
  public void testWriterHandlesDoublePrecision() {
    String json = JSONWriter.objectToJSON(writer -> {
      writer.set("value", 2.718281828459d);
    });
    assertTrue(json.contains("2.7182818") || json.contains("2.718"));
  }
@Test
  public void testJSONWriterWithEscapedCharacters() {
    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set("escaped", "He said, \"Hello\\World\"");
    });
    assertTrue(json.contains("\\\"Hello\\\\World\\\""));
    assertTrue(json.contains("escaped"));
  }
@Test
  public void testJSONWriterWithConsumerInsideArray() {
    Consumer<JSONOutputter.Writer> nestedObject = (w) -> w.set("val", 123);
    List<Object> list = new ArrayList<>();
    list.add(nestedObject);
    list.add("extra");

    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set("data", list);
    });

    assertTrue(json.contains("val"));
    assertTrue(json.contains("123"));
    assertTrue(json.contains("extra"));
  }
@Test
  public void testJSONWriterSetWithEnumArray() {
    Enum[] array = new Thread.State[]{Thread.State.NEW, Thread.State.RUNNABLE};
    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set("enumArray", array);
    });
    assertTrue(json.contains("NEW"));
    assertTrue(json.contains("RUNNABLE"));
  }
@Test
  public void testJSONWriterSetWithNestedPair() {
    Pair<String, Pair<Integer, String>> nestedPair = new Pair<>("outer", new Pair<>(1, "inner"));
    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set("nested", nestedPair);
    });
    assertTrue(json.contains("outer"));
    assertTrue(json.contains("1"));
    assertTrue(json.contains("inner"));
  }
@Test
  public void testJSONWriterSetWithRawArrayTypeFallback() {
    String[] texts = new String[]{"x", "y"};
    Object value = texts; 
    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set("words", value);
    });
    assertTrue(json.contains("x"));
    assertTrue(json.contains("y"));
  }
@Test
  public void testWriterSetWithVariousPrimitiveWrappers() {
    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set("byteVal", Byte.valueOf((byte) 5));
      writer.set("shortVal", Short.valueOf((short) 100));
      writer.set("intVal", Integer.valueOf(999));
      writer.set("longVal", Long.valueOf(100000L));
      writer.set("floatVal", Float.valueOf(1.5f));
      writer.set("doubleVal", Double.valueOf(2.25));
      writer.set("boolVal", Boolean.TRUE);
    });
    assertTrue(json.contains("5"));
    assertTrue(json.contains("100"));
    assertTrue(json.contains("999"));
    assertTrue(json.contains("100000"));
    assertTrue(json.contains("1.5"));
    assertTrue(json.contains("2.25"));
    assertTrue(json.contains("true"));
  }
@Test
  public void testWriterHandlesUnsupportedPrimitiveComponentType() {
    try {
      Object value = java.lang.reflect.Array.newInstance(java.sql.Date.class, 1);
      ((Object[]) value)[0] = java.sql.Date.valueOf("2020-01-01");
      JSONOutputter.JSONWriter.objectToJSON(writer -> {
        writer.set("unsupported", value);
      });
      fail("Expected Unsupported type exception");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("Unhandled"));
    }
  }
@Test
  public void testCorefWithNullChainListHandledGracefully() throws Exception {
    Annotation annotation = new Annotation("Missing chain list");
    Map<Integer, CorefChain> corefMap = new HashMap<>();
    corefMap.put(999, new CorefChain(999, null));
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, corefMap);

    String json = JSONOutputter.jsonPrint(annotation);
    assertTrue(json.contains("corefs"));
    assertTrue(json.contains("999")); 
  }
@Test
  public void testJSONPrintDocumentFieldsIncludeOnlyTextWhenConfigured() throws Exception {
    Annotation annotation = new Annotation("Hello there JSON");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Hello there JSON");
    Options opts = new Options();
    opts.includeText = true;
    opts.pretty = false;

    StringOutputStream os = new StringOutputStream();
    new JSONOutputter().print(annotation, os, opts);
    String json = os.toString();

    assertTrue(json.contains("text"));
    assertTrue(json.contains("Hello there JSON"));
  }
@Test
  public void testWriterHandlesNullKeyAndNullValueNoWrite() {
    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set(null, null);
      writer.set("keep", "yes");
    });
    assertFalse(json.contains("null"));
    assertFalse(json.contains(": null"));
    assertTrue(json.contains("keep"));
    assertTrue(json.contains("yes"));
  }
@Test
  public void testWriterConsumerObjectGeneratedInsideArray() {
    Consumer<JSONOutputter.Writer> consumer = w -> w.set("inner", "object");
    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set("root", Arrays.asList(consumer));
    });
    assertTrue(json.contains("object"));
    assertTrue(json.contains("inner"));
  }
@Test
  public void testJsonEscapeStringHandlesControlChars() {
    String text = "Newline:\nTab:\tQuote:\"Backslash:\\";
    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set("text", text);
    });
    assertTrue(json.contains("\\n"));
    assertTrue(json.contains("\\t"));
    assertTrue(json.contains("\\\""));
    assertTrue(json.contains("\\\\"));
  }
@Test
  public void testWriterHandlesEmptyPair() {
    Pair<String, Integer> pair = new Pair<>(null, null);
    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set("pair", pair);
    });
    assertTrue(json.contains("null")); 
  }
@Test
  public void testWriterHandlesIntPrimitiveInputViaClassTypePath() {
    int primitiveInt = 3;
    Object boxed = primitiveInt;  
    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set("value", boxed);
    });
    assertTrue(json.contains("3"));
  }
@Test
  public void testWriterHandlesBooleanClassObjectTrue() {
    Object value = Boolean.TRUE;
    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set("enabled", value);
    });
    assertTrue(json.contains("true"));
    assertTrue(json.contains("enabled"));
  } 
}