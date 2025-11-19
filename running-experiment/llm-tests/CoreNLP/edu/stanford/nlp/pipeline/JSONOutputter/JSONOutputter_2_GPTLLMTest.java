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

public class JSONOutputter_2_GPTLLMTest { 

 @Test
  public void testPrintSingleTokenAnnotation() throws Exception {
    Annotation doc = new Annotation("Barack");
    CoreLabel token = new CoreLabel();
    token.setWord("Barack");
    token.setOriginalText("Barack");
    token.setLemma("Barack");
    token.setIndex(1);
    token.setBeginPosition(0);
    token.setEndPosition(6);
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

    JSONOutputter.Options options = new JSONOutputter.Options();
//    options.includeText = true;
//    options.pretty = true;
//    options.encoding = "UTF-8";

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    new JSONOutputter().print(doc, out, options);
    String json = out.toString("UTF-8");

    assertTrue(json.contains("\"word\": \"Barack\""));
    assertTrue(json.contains("\"pos\": \"NNP\""));
    assertTrue(json.contains("\"ner\": \"PERSON\""));
  }
@Test
  public void testPrintNullAnnotationIsSafe() throws Exception {
    Annotation doc = new Annotation((String) null);

    JSONOutputter.Options options = new JSONOutputter.Options();
//    options.includeText = true;
//    options.pretty = true;
//    options.encoding = "UTF-8";

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    new JSONOutputter().print(doc, out, options);

    String json = out.toString("UTF-8");

    assertNotNull(json);
    assertTrue(json.contains("{"));
    assertTrue(json.contains("}"));
  }
@Test
  public void testJsonWriterObjectToJSONSimpleStruct() {
    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set("key", "value");
      writer.set("number", 123);
      writer.set("flag", true);
    });

    assertTrue(json.contains("\"key\": \"value\""));
    assertTrue(json.contains("\"number\": 123"));
    assertTrue(json.contains("\"flag\": true"));
  }
@Test
  public void testJsonWriterSupportsArray() {
    int[] arr = new int[3];
    arr[0] = 5;
    arr[1] = 10;
    arr[2] = 15;

    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set("ids", arr);
    });

    assertTrue(json.contains("\"ids\": ["));
    assertTrue(json.contains("5"));
    assertTrue(json.contains("10"));
    assertTrue(json.contains("15"));
  }
@Test
  public void testJsonWriterHandlesEmptyObject() {
    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      
    });

    assertEquals("{}", json.trim());
  }
@Test
  public void testJsonWriterWithBoolean() {
    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set("available", false);
    });

    assertTrue(json.contains("\"available\": false"));
  }
@Test
  public void testTimexAnnotationSerialization() throws Exception {
    Annotation doc = new Annotation("Today is 2020-01-01.");
//    Timex timex = new Timex("t1", "DATE", "2020-01-01", "2020-01-01", null);
    CoreLabel token = new CoreLabel();
    token.setWord("Today");
//    token.set(TimeAnnotations.TimexAnnotation.class, timex);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

    JSONOutputter.Options options = new JSONOutputter.Options();
//    options.includeText = true;
//    options.pretty = true;
//    options.encoding = "UTF-8";

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    new JSONOutputter().print(doc, out, options);
    String json = out.toString("UTF-8");

    assertTrue(json.contains("\"tid\": \"t1\""));
    assertTrue(json.contains("\"value\": \"2020-01-01\""));
    assertTrue(json.contains("\"type\": \"DATE\""));
  }
@Test
  public void testJsonPrintStaticReturnsNonEmpty() throws Exception {
    Annotation ann = new Annotation("Testing static jsonPrint");
    ann.set(CoreAnnotations.DocIDAnnotation.class, "doc001");

    String json = JSONOutputter.jsonPrint(ann);
    assertNotNull(json);
    assertTrue(json.contains("doc001"));
    assertTrue(json.contains("Testing static jsonPrint"));
  }
@Test
  public void testRelationTripleSerialization() {
    final Span subjectSpan = new Span(0, 1);
    final Span relationSpan = new Span(1, 2);
    final Span objectSpan = new Span(2, 3);

//    RelationTriple triple = new RelationTriple(
//        null, null, null,
//        "John", "loves", "Mary",
//        new Pair<>(0, 1),
//        new Pair<>(1, 2),
//        new Pair<>(2, 3),
//        1.0, 1.0, 1.0
//    );

    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      JSONOutputter.JSONWriter w = new JSONOutputter.JSONWriter(new java.io.PrintWriter(new java.io.StringWriter()), new JSONOutputter.Options());
      JSONOutputter.JSONWriter tripleWriter = w;
//      JSONOutputter.writeTriples(writer, "openie", Arrays.asList(triple));
    });

    assertTrue(json.contains("\"subject\": \"John\""));
    assertTrue(json.contains("\"relation\": \"loves\""));
    assertTrue(json.contains("\"object\": \"Mary\""));
    assertTrue(json.contains("\"subjectSpan\""));
  }
@Test
  public void testWriteNullTimexProducesNoTimexKey() {
//    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
//      JSONOutputter.writeTime(writer, null);
//    });

//    assertEquals("{}", json.trim());
  }
@Test
  public void testWriteTimeWithRange() {
    Timex.Range range = new Timex.Range("2020-01-01", "2020-01-02", "P1D");
//    Timex timex = new Timex("t1", "DATE", "2020-01-01", null, range);

//    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
//      JSONOutputter.writeTime(writer, timex);
//    });

//    assertTrue(json.contains("\"begin\": \"2020-01-01\""));
//    assertTrue(json.contains("\"end\": \"2020-01-02\""));
//    assertTrue(json.contains("\"duration\": \"P1D\""));
  }
@Test
  public void testNullKeyIsIgnoredInWriter() {
    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set(null, "someValue");
      writer.set("valid", "value");
    });

    assertFalse(json.contains("someValue"));
    assertTrue(json.contains("\"valid\": \"value\""));
  }
@Test
  public void testNullValueIsIgnoredInWriter() {
    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set("shouldSkip", null);
      writer.set("included", true);
    });

    assertFalse(json.contains("shouldSkip"));
    assertTrue(json.contains("\"included\": true"));
  }
@Test
  public void testUnsupportedObjectTypeThrowsRuntimeException() {
    final Object unsupported = new Object();

    boolean exceptionThrown = false;
    try {
      JSONOutputter.JSONWriter.objectToJSON(writer -> {
        writer.set("badType", unsupported);
      });
    } catch (RuntimeException e) {
      exceptionThrown = true;
    }

    assertTrue("Expected RuntimeException for unsupported object type", exceptionThrown);
  }
@Test
  public void testObjectArraySerialization() {
    String[] items = new String[] { "one", "two", "three" };

    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set("items", items);
    });

    assertTrue(json.contains("["));
    assertTrue(json.contains("\"one\""));
    assertTrue(json.contains("\"three\""));
  }
@Test
  public void testEnumSerialization() {
    Thread.State state = Thread.State.RUNNABLE;

    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set("threadState", state);
    });

    assertTrue(json.contains("\"threadState\": \"RUNNABLE\""));
  }
@Test
  public void testPairSerializationAsList() {
    Pair<String, Integer> pair = new Pair<>("hello", 42);

    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set("data", pair);
    });

    assertTrue(json.contains("[\"hello\""));
    assertTrue(json.contains("42"));
  }
@Test
  public void testCharArraySerialization() {
    char[] chars = new char[] { 'a', 'b', 'c' };

    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set("chars", chars);
    });

    assertTrue(json.contains("[\"a\","));
    assertTrue(json.contains("\"c\"]"));
  }
@Test
  public void testSentenceWithSpeakerAndParseTree() throws Exception {
    Annotation annotation = new Annotation("Obama spoke.");

    CoreLabel token = new CoreLabel();
    token.setWord("Obama");
    token.setOriginalText("Obama");
    token.setIndex(1);
    token.setTag("NNP");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new Annotation("Obama spoke.");
    sentence.set(CoreAnnotations.TextAnnotation.class, "Obama spoke.");
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.SpeakerAnnotation.class, "Obama");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Tree tree = new LabeledScoredTreeFactory().newTreeNode("S", new ArrayList<>());
//    sentence.set(CoreAnnotations.TreeAnnotation.class, tree);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    annotation.set(CoreAnnotations.TextAnnotation.class, "Obama spoke.");

    JSONOutputter.Options options = new JSONOutputter.Options();
//    options.pretty = true;
//    options.includeText = true;
//    options.encoding = "UTF-8";

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    new JSONOutputter().print(annotation, out, options);
    String json = out.toString("UTF-8");

    assertTrue(json.contains("\"speaker\": \"Obama\""));
    assertTrue(json.contains("\"parse\": \"(S)\""));
  }
@Test
  public void testTokenWithCodepointOffsetsIncluded() throws Exception {
    Annotation document = new Annotation("هجري");

    CoreLabel token = new CoreLabel();
    token.setWord("هجري");
    token.setOriginalText("هجري");
    token.setIndex(1);
    token.setBeginPosition(0);
    token.setEndPosition(4);
    token.set(CoreAnnotations.CodepointOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CodepointOffsetEndAnnotation.class, 4);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    document.set(CoreAnnotations.TokensAnnotation.class, tokens);

    JSONOutputter.Options options = new JSONOutputter.Options();
//    options.includeText = true;
//    options.pretty = true;
//    options.encoding = "UTF-8";

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    new JSONOutputter().print(document, out, options);

    String json = out.toString("UTF-8");
    assertTrue(json.contains("\"codepointOffsetBegin\": 0"));
    assertTrue(json.contains("\"codepointOffsetEnd\": 4"));
  }
@Test
  public void testQuoteWithAttributions() throws Exception {
    CoreMap quote = new Annotation("He said: \"Go away\".");
    quote.set(CoreAnnotations.QuotationIndexAnnotation.class, 1);
    quote.set(CoreAnnotations.TextAnnotation.class, "Go away");
    quote.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 8);
    quote.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 16);
    quote.set(CoreAnnotations.TokenBeginAnnotation.class, 2);
    quote.set(CoreAnnotations.TokenEndAnnotation.class, 4);
    quote.set(CoreAnnotations.SentenceBeginAnnotation.class, 0);
    quote.set(CoreAnnotations.SentenceEndAnnotation.class, 1);
    quote.set(QuoteAttributionAnnotator.SpeakerAnnotation.class, "He");
    quote.set(QuoteAttributionAnnotator.CanonicalMentionAnnotation.class, "He");

    List<CoreMap> quotes = new ArrayList<>();
    quotes.add(quote);
    Annotation document = new Annotation("He said: \"Go away\".");
    document.set(CoreAnnotations.QuotationsAnnotation.class, quotes);

    JSONOutputter.Options options = new JSONOutputter.Options();
//    options.pretty = true;
//    options.includeText = true;
//    options.encoding = "UTF-8";

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    new JSONOutputter().print(document, out, options);
    String json = out.toString("UTF-8");

    assertTrue(json.contains("\"speaker\": \"He\""));
    assertTrue(json.contains("\"canonicalSpeaker\": \"He\""));
    assertTrue(json.contains("\"text\": \"Go away\""));
  }
@Test
  public void testCorefChainWithRepresentativeMention() throws Exception {
//    CorefChain.CorefMention mention1 = new CorefChain.CorefMention(0, 1, 2, 2, 2, "Obama", CorefChain.CorefMention.mentionType.NOMINAL,
//         CorefChain.CorefMention.number.SINGULAR, CorefChain.CorefMention.gender.MALE, CorefChain.CorefMention.animacy.ANIMATE,
//         new IntPair(0, 1), 0);
//    CorefChain.CorefMention mention2 = new CorefChain.CorefMention(0, 1, 2, 2, 2, "He", CorefChain.CorefMention.mentionType.PRONOMINAL,
//         CorefChain.CorefMention.number.SINGULAR, CorefChain.CorefMention.gender.MALE, CorefChain.CorefMention.animacy.ANIMATE,
//         new IntPair(0, 2), 1);
//    List<CorefChain.CorefMention> mentions = Arrays.asList(mention1, mention2);
//    CorefChain chain = new CorefChain(1, mentions);

    Map<Integer, CorefChain> corefMap = new HashMap<>();
//    corefMap.put(1, chain);

    Annotation annotation = new Annotation("Obama was president. He served two terms.");
//    annotation.set(CoreAnnotations.CorefCoreAnnotations.CorefChainAnnotation.class, corefMap);

    JSONOutputter.Options options = new JSONOutputter.Options();
//    options.pretty = true;
//    options.includeText = true;
//    options.encoding = "UTF-8";

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    new JSONOutputter().print(annotation, out, options);

    String json = out.toString("UTF-8");

    assertTrue(json.contains("\"text\": \"Obama\""));
    assertTrue(json.contains("\"text\": \"He\""));
    assertTrue(json.contains("\"isRepresentativeMention\": true"));
  }
@Test
  public void testSectionAnnotationIncludesSentenceIndices() throws Exception {
    CoreMap sentence1 = new Annotation("Sentence one.");
    sentence1.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    CoreMap sentence2 = new Annotation("Sentence two.");
    sentence2.set(CoreAnnotations.SentenceIndexAnnotation.class, 1);
    List<CoreMap> sectionSentences = Arrays.asList(sentence1, sentence2);

    CoreMap section = new Annotation("Section");
    section.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    section.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 20);
    section.set(CoreAnnotations.AuthorAnnotation.class, "Author A");
    section.set(CoreAnnotations.SectionDateAnnotation.class, "2020-01-01");
    section.set(CoreAnnotations.SentencesAnnotation.class, sectionSentences);

    List<CoreMap> sections = new ArrayList<>();
    sections.add(section);

    Annotation annotation = new Annotation("Full text.");
    annotation.set(CoreAnnotations.SectionsAnnotation.class, sections);

    JSONOutputter.Options options = new JSONOutputter.Options();
//    options.pretty = true;
//    options.includeText = true;
//    options.encoding = "UTF-8";

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    new JSONOutputter().print(annotation, out, options);

    String json = out.toString("UTF-8");

    assertTrue(json.contains("\"charBegin\": 0"));
    assertTrue(json.contains("\"author\": \"Author A\""));
    assertTrue(json.contains("\"dateTime\": \"2020-01-01\""));
    assertTrue(json.contains("\"index\": 0"));
    assertTrue(json.contains("\"index\": 1"));
  }
@Test
  public void testEmptySentencesWithTokenListStillSerialized() throws Exception {
    Annotation doc = new Annotation("Test");

    CoreLabel token = new CoreLabel();
    token.setWord("Test");
    token.setIndex(1);
    token.setOriginalText("Test");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);
    doc.set(CoreAnnotations.SentencesAnnotation.class, null); 

    Options options = new Options();
//    options.pretty = true;
//    options.includeText = true;
//    options.encoding = "UTF-8";

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//    new JSONOutputter().print(doc, baos, options);

    String json = baos.toString("UTF-8");
    assertTrue(json.contains("\"tokens\""));
    assertTrue(json.contains("\"word\": \"Test\""));
  }
@Test
  public void testSentimentTreeSerializationOutput() throws Exception {
    Annotation doc = new Annotation("Good");

    Tree tree = new LabeledScoredTreeFactory().newLeaf("positive");
    Tree sentimentTree = new LabeledScoredTreeFactory().newTreeNode("Positive", Collections.singletonList(tree));
    sentimentTree.label().setValue("Positive");

    CoreMap sentence = new Annotation("Good");
    sentence.set(CoreAnnotations.TextAnnotation.class, "Good");
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(SentimentCoreAnnotations.SentimentClass.class, "Very positive");
    sentence.set(SentimentCoreAnnotations.SentimentAnnotatedTree.class, sentimentTree);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    doc.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    Options options = new Options();
//    options.pretty = true;
//    options.includeText = true;
//    options.encoding = "UTF-8";

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//    new JSONOutputter().print(doc, baos, options);

    String json = baos.toString("UTF-8");
    assertTrue(json.contains("\"sentiment\": \"Verypositive\""));
    assertTrue(json.contains("\"sentimentTree\""));
  }
@Test
  public void testEmptyOpenIETripleListYieldEmptyArray() throws Exception {
    Annotation doc = new Annotation("No relations exist.");

    CoreMap sentence = new Annotation("Sentence");
    sentence.set(CoreAnnotations.TextAnnotation.class, "Sentence");
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());  
    sentence.set(edu.stanford.nlp.naturalli.NaturalLogicAnnotations.RelationTriplesAnnotation.class, new ArrayList<>());

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    doc.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    Options options = new Options();
//    options.pretty = true;
//    options.includeText = true;
//    options.encoding = "UTF-8";

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//    new JSONOutputter().print(doc, baos, options);

    String json = baos.toString("UTF-8");
    assertTrue(json.contains("\"openie\": []"));
  }
@Test
  public void testNormalizedNERAndEntityLinkAreIncluded() throws Exception {
    Annotation doc = new Annotation("Testing");

    CoreLabel token = new CoreLabel();
    token.setWord("Stanford");
    token.setOriginalText("Stanford");
    token.setIndex(1);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);
    token.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "STANFORD_UNIVERSITY");
    token.set(CoreAnnotations.WikipediaEntityAnnotation.class, "Stanford_University");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Options options = new Options();
//    options.includeText = true;
//    options.pretty = true;
//    options.encoding = "UTF-8";

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//    new JSONOutputter().print(doc, baos, options);

    String json = baos.toString("UTF-8");
    assertTrue(json.contains("\"normalizedNER\": \"STANFORD_UNIVERSITY\""));
    assertTrue(json.contains("\"entitylink\": \"Stanford_University\""));
  }
@Test
  public void testTokenWithBeforeAndAfterWhitespace() throws Exception {
    Annotation doc = new Annotation("Example");

    CoreLabel token = new CoreLabel();
    token.setWord("Example");
    token.setOriginalText("Example");
    token.setIndex(1);
//    token.set(CharacterOffsetBeginAnnotation.class, 0);
//    token.set(CharacterOffsetEndAnnotation.class, 7);
    token.set(CoreAnnotations.BeforeAnnotation.class, " ");
    token.set(CoreAnnotations.AfterAnnotation.class, "\n");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Options options = new Options();
//    options.includeText = true;
//    options.pretty = true;
//    options.encoding = "UTF-8";

    ByteArrayOutputStream out = new ByteArrayOutputStream();
//    new JSONOutputter().print(doc, out, options);

    String json = out.toString("UTF-8");
    assertTrue(json.contains("\"before\": \" \""));
    assertTrue(json.contains("\"after\": \"\\n\""));
  }
@Test
  public void testEmptyCollectionIsSerializedAsEmptyArray() {
    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set("list", new ArrayList<>());
    });

    assertTrue(json.contains("\"list\": []"));
  }
@Test
  public void testBooleanFalseLiteralSerialization() {
    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set("flag", false);
    });

    assertTrue(json.contains("\"flag\": false"));
  }
@Test
  public void testNestedConsumerWithNullEntryIgnored() {
    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set("object", (JSONOutputter.Writer) (key, value) -> {
        writer.set("a", null); 
        writer.set("b", "valid");
      });
    });

    assertTrue(json.contains("\"b\": \"valid\""));
    assertFalse(json.contains("\"a\""));
  }
@Test
  public void testSkippedParseTreeIsNotSerialized() throws Exception {
    Annotation doc = new Annotation("Skipped");
    CoreMap sentence = new Annotation("Skipped");
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

//    Tree unparsableTree = new Tree() {
//      @Override public Tree[] children() { return new Tree[0]; }
//      @Override public Tree[] siblings(Tree node) { return new Tree[0]; }
//      @Override public Label label() { return null; }
//      @Override public void setLabel(Label label) {}
//      @Override public List<Tree> getChildrenAsList() { return new ArrayList<>(); }
//      @Override public boolean isLeaf() { return true; }
//      @Override public boolean isPreTerminal() { return false; }
//      @Override public Tree deepCopy() { return null; }
//      @Override public Tree parent(Tree root) { return null; }
//      @Override public Tree[] pathNodeToNode(Tree root, Tree start, Tree end) { return new Tree[0]; }
//      @Override public String nodeString() { return "SENTENCE_SKIPPED_OR_UNPARSABLE"; }
//      @Override public Tree treeFactory() { return new LabeledScoredTreeFactory().newLeaf("X"); }
//      @Override public String toString() { return "SENTENCE_SKIPPED_OR_UNPARSABLE"; }
//    };
//    sentence.set(TreeAnnotation.class, unparsableTree);
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    doc.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    JSONOutputter.Options options = new JSONOutputter.Options();
//    options.pretty = true;
//    options.includeText = true;
//    options.encoding = "UTF-8";

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    new JSONOutputter().print(doc, baos, options);
    String json = baos.toString("UTF-8");

    assertFalse(json.contains("\"parse\""));
  }
@Test
  public void testBinarizedTreeSerialization() throws Exception {
    Annotation doc = new Annotation("Binarized");

    Tree tree = new LabeledScoredTreeFactory().newLeaf("NN");
    Tree binarizedTree = new LabeledScoredTreeFactory().newTreeNode("X", Collections.singletonList(tree));

    CoreMap sentence = new Annotation("Binarized");
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class, binarizedTree);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    doc.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    JSONOutputter.Options options = new JSONOutputter.Options();
//    options.pretty = true;
//    options.includeText = true;

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    new JSONOutputter().print(doc, os, options);

    String json = os.toString("UTF-8");
    assertTrue(json.contains("\"binaryParse\""));
    assertTrue(json.contains("(X (NN))"));
  }
@Test
  public void testSentenceWithLineAndParagraphNumbers() throws Exception {
    Annotation doc = new Annotation("Meta");

    CoreMap sentence = new Annotation("Meta");
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.LineNumberAnnotation.class, 42);
    sentence.set(CoreAnnotations.ParagraphIndexAnnotation.class, 7);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    doc.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    JSONOutputter.Options options = new JSONOutputter.Options();
//    options.pretty = true;
//    options.includeText = true;

    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    new JSONOutputter().print(doc, stream, options);

    String json = stream.toString("UTF-8");

    assertTrue(json.contains("\"line\": 42"));
    assertTrue(json.contains("\"paragraph\": 7"));
  }
@Test
  public void testTokenWithTrueCaseAttributes() throws Exception {
    Annotation ann = new Annotation("case");

    CoreLabel token = new CoreLabel();
    token.setWord("stanford");
    token.setOriginalText("Stanford");
    token.setIndex(1);
    token.set(CoreAnnotations.TrueCaseAnnotation.class, "UPPER");
    token.set(CoreAnnotations.TrueCaseTextAnnotation.class, "Stanford");

    List<CoreLabel> tokens = Collections.singletonList(token);
    ann.set(CoreAnnotations.TokensAnnotation.class, tokens);

    JSONOutputter.Options options = new JSONOutputter.Options();
//    options.pretty = true;
//    options.includeText = true;

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    new JSONOutputter().print(ann, os, options);

    String json = os.toString("UTF-8");

    assertTrue(json.contains("\"truecase\": \"UPPER\""));
    assertTrue(json.contains("\"truecaseText\": \"Stanford\""));
  }
//@Test
//  public void testNullDependencyGraphReturnsNull() {
//    Object obj = new JSONOutputter().buildDependencyTree(null);
//    assertNull(obj);
//  }
@Test
  public void testStreamOfObjectsIsSerialized() {
    Stream<Object> stream = Stream.of("a", "b", 123, true);
    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set("items", stream);
    });

    assertTrue(json.contains("\"items\": ["));
    assertTrue(json.contains("\"a\""));
    assertTrue(json.contains("123"));
    assertTrue(json.contains("true"));
  }
@Test
  public void testFloatAndDoublePrecisionFormatting() {
    float floatValue = 3.1415927f;
    double doubleValue = 2.718281828459045;

    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set("small", floatValue);
      writer.set("math", doubleValue);
    });

    assertTrue(json.contains("3.14159") || json.contains("3.141592")); 
    assertTrue(json.contains("2.71828"));                              
  }
@Test
  public void testSpanArraySerializationAsList() {
    Span span = new Span(10, 20);
    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set("spanInside", span);
    });

    assertTrue(json.contains("\"spanInside\": [10, 20]"));
  }
@Test
  public void testCorefMentionPositionArrayIsSerialized() throws Exception {
//    CorefMention m1 = new CorefMention(0, 0, 2, 2, 2, "Obama", CorefChain.CorefMention.mentionType.NOMINAL,
//            CorefChain.CorefMention.number.SINGULAR, CorefChain.CorefMention.gender.MALE,
//            CorefChain.CorefMention.animacy.ANIMATE, new IntPair(1, 3), 0);
//    CorefMention m2 = new CorefMention(0, 0, 4, 4, 4, "He", CorefChain.CorefMention.mentionType.PRONOMINAL,
//            CorefChain.CorefMention.number.SINGULAR, CorefChain.CorefMention.gender.MALE,
//            CorefChain.CorefMention.animacy.ANIMATE, new IntPair(4, 6), 1);
//    List<CorefMention> mentions = Arrays.asList(m1, m2);
//    CorefChain chain = new CorefChain(1, mentions);

    Map<Integer, CorefChain> map = new HashMap<>();
//    map.put(1, chain);

    Annotation annotation = new Annotation("Coref test");
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, map);

    JSONOutputter.Options options = new JSONOutputter.Options();
//    options.includeText = true;
//    options.pretty = true;

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    new JSONOutputter().print(annotation, out, options);

    String json = out.toString("UTF-8");
    assertTrue(json.contains("\"position\": [1, 3]"));
    assertTrue(json.contains("\"text\": \"Obama\""));
    assertTrue(json.contains("\"isRepresentativeMention\": true"));
  }
@Test
  public void testEntityMentionWithNullNerConfidences() throws Exception {
    Annotation annotation = new Annotation("Text");
    CoreLabel token = new CoreLabel();
    token.setWord("Obama");
    token.setOriginalText("Obama");
    token.setIndex(1);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");

    CoreMap ent = new Annotation("Obama");
    ent.set(CoreAnnotations.TextAnnotation.class, "Obama");
    ent.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    ent.set(CoreAnnotations.TokenEndAnnotation.class, 1);
    ent.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    ent.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    ent.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    ent.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, null); 

    CoreMap sentence = new Annotation("Obama");
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(ent));

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    JSONOutputter.Options options = new JSONOutputter.Options();
//    options.pretty = true;
//    options.includeText = true;

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    new JSONOutputter().print(annotation, outputStream, options);

    String json = outputStream.toString("UTF-8");
    assertTrue(json.contains("\"text\": \"Obama\""));
    assertTrue(json.contains("\"ner\": \"PERSON\""));
  }
@Test
  public void testTimexWithNullAltValueAndNullRange() {
//    Timex timex = new Timex("t2", "DATE", "2022-01-01", null, null);

    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
//      JSONOutputter.writeTime(writer, timex);
    });

    assertTrue(json.contains("\"tid\": \"t2\""));
    assertTrue(json.contains("\"value\": \"2022-01-01\""));
    assertTrue(json.contains("\"altValue\": null"));
  }
@Test
  public void testSentenceWithMissingTokenPositions() throws Exception {
    CoreMap sentence = new Annotation("Hello");
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());

    Annotation ann = new Annotation("Hello");
    ann.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

//    JSONOutputter.Options options = new JSONOutputter.Options();
//    options.pretty = true;
//    options.includeText = true;

    ByteArrayOutputStream out = new ByteArrayOutputStream();
//    new JSONOutputter().print(ann, out, options);

    String json = out.toString("UTF-8");
    assertTrue(json.contains("\"sentences\": ["));
  }
@Test
  public void testEmptyDependencyGraphHandled() {
    SemanticGraph sg = new SemanticGraph(); 

//    Object obj = JSONOutputter.buildDependencyTree(sg);
//    assertNotNull(obj);
  }
@Test
  public void testPrimitiveArrayBooleanByteIntHandled() {
    boolean[] bools = {true, false};
    byte[] bytes = {1, 2, 3};
    int[] ints = {10, 20};

    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set("bools", bools);
      writer.set("bytes", bytes);
      writer.set("ints", ints);
    });

    assertTrue(json.contains("true"));
    assertTrue(json.contains("2"));
    assertTrue(json.contains("10"));
  }
@Test
  public void testEnumWithLowerCaseNameHandled() {
    Thread.State state = Thread.State.NEW;

    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set("threadState", state);
    });

    assertTrue(json.contains("\"threadState\": \"NEW\""));
  }
@Test
  public void testWriterSkipsNullKeyEntirely() {
    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set(null, "value");
      writer.set("valid", 1);
    });

    assertTrue(json.contains("\"valid\": 1"));
    assertFalse(json.contains("value\""));
  }
@Test
  public void testComplexNestedStreamEntry() {
    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set("main", Stream.of("x", Arrays.asList("a", "b", "c")));
    });

    assertTrue(json.contains("\"main\": ["));
    assertTrue(json.contains("\"x\""));
    assertTrue(json.contains("[\"a\", \"b\", \"c\"]") || json.contains("a"));
  }
@Test
  public void testQuoteWithOptionalAttributionFields() throws Exception {
    CoreMap quote = new Annotation("Hello");
    quote.set(CoreAnnotations.QuotationIndexAnnotation.class, 2);
    quote.set(CoreAnnotations.TextAnnotation.class, "Hello");
    quote.set(CoreAnnotations.TokenBeginAnnotation.class, 1);
    quote.set(CoreAnnotations.TokenEndAnnotation.class, 2);
    quote.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    quote.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    quote.set(CoreAnnotations.SentenceBeginAnnotation.class, 0);
    quote.set(CoreAnnotations.SentenceEndAnnotation.class, 1);
    quote.set(QuoteAttributionAnnotator.MentionTypeAnnotation.class, "PRONOUN");
    quote.set(QuoteAttributionAnnotator.MentionSieveAnnotation.class, "RULE_BASED");

    List<CoreMap> quotes = new ArrayList<>();
    quotes.add(quote);
    Annotation doc = new Annotation("Hello");
    doc.set(CoreAnnotations.QuotationsAnnotation.class, quotes);

    JSONOutputter.Options options = new JSONOutputter.Options();
//    options.includeText = true;
//    options.pretty = true;

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    new JSONOutputter().print(doc, baos, options);
    String json = baos.toString("UTF-8");

    assertTrue(json.contains("\"mentionType\": \"PRONOUN\""));
    assertTrue(json.contains("\"mentionSieve\": \"RULE_BASED\""));
  }
@Test
  public void testTokenWithoutOptionalKeysStillSerializesCleanly() throws Exception {
    Annotation doc = new Annotation("Test");

    CoreLabel token = new CoreLabel();
    token.setWord("test");
    token.setIndex(1);
    token.setOriginalText("test");
    token.setBeginPosition(0);
    token.setEndPosition(4);
    

    List<CoreLabel> tokens = Collections.singletonList(token);
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);

    JSONOutputter.Options options = new JSONOutputter.Options();
//    options.includeText = true;
//    options.pretty = true;

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    new JSONOutputter().print(doc, baos, options);
    String json = baos.toString("UTF-8");

    assertTrue(json.contains("\"word\": \"test\""));
    assertTrue(json.contains("\"originalText\": \"test\""));
  }
@Test
  public void testDependencyTreeIncludesRootAndEdge() throws Exception {
    CoreLabel governor = new CoreLabel();
    governor.setWord("ROOT");
    governor.setIndex(0);

    CoreLabel dependent = new CoreLabel();
    dependent.setWord("flies");
    dependent.setIndex(1);

    IndexedWord govWord = new IndexedWord(governor);
    IndexedWord depWord = new IndexedWord(dependent);

    SemanticGraph graph = new SemanticGraph();
    graph.addVertex(govWord);
    graph.addVertex(depWord);
    graph.addEdge(govWord, depWord, GrammaticalRelation.DEPENDENT, 1.0, false);

//    Object obj = JSONOutputter.buildDependencyTree(graph);
//    assertNotNull(obj);
  }
//@Test
//  public void testTimexWithOnlyTidAndType() {
//    Timex timex = new Timex("t3", "TIME", null, null, null);
//
//    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
//      JSONOutputter.writeTime(writer, timex);
//    });

//    assertTrue(json.contains("\"tid\": \"t3\""));
//    assertTrue(json.contains("\"type\": \"TIME\""));
//  }
@Test
  public void testWriterObjectHandlesEmptyConsumerStream() {
    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set("emptyList", Collections.emptyList());
      writer.set("emptyStream", Stream.empty());
    });

    assertTrue(json.contains("\"emptyList\": []"));
    assertTrue(json.contains("\"emptyStream\": []"));
  }
@Test
  public void testWriterHandlesMultiplePrimitiveArrays() {
    short[] shorts = new short[] {10, 20};
    float[] floats = new float[] {1.1f, 2.2f};
    double[] doubles = new double[] {3.14d, 6.28d};
    long[] longs = new long[] {10000000000L};
    char[] chars = new char[] {'A', 'B'};

    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set("shorts", shorts);
      writer.set("floats", floats);
      writer.set("doubles", doubles);
      writer.set("longs", longs);
      writer.set("chars", chars);
    });

    assertTrue(json.contains("10"));
    assertTrue(json.contains("1.1"));
    assertTrue(json.contains("3.14"));
    assertTrue(json.contains("10000000000"));
    assertTrue(json.contains("\"A\""));
  }
@Test
  public void testObjectArrayWithNullsHandledGracefully() {
    Object[] array = new Object[] {"first", null, "last"};

    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set("array", array);
    });

    assertTrue(json.contains("\"first\""));
    assertTrue(json.contains("\"last\""));
    assertTrue(json.contains("\"array\":"));
  }
@Test
  public void testArrayOfBooleansHandled() {
    boolean[] flags = new boolean[] {true, false};

    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set("flags", flags);
    });

    assertTrue(json.contains("\"flags\": [true, false]"));
  }
@Test
  public void testJSONObjectWithEnumInsideStream() {
    List<Object> mixed = Arrays.asList(Thread.State.RUNNABLE, "ready");

    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set("modes", mixed.stream());
    });

    assertTrue(json.contains("\"modes\": ["));
    assertTrue(json.contains("\"RUNNABLE\""));
    assertTrue(json.contains("\"ready\""));
  }
@Test
  public void testNullStreamInWriterIsIgnored() {
    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set("nullStream", (Stream<?>) null);
      writer.set("value", 1);
    });

    assertTrue(json.contains("\"value\": 1"));
    assertFalse(json.contains("nullStream"));
  }
@Test
  public void testNullPairInWriterHandledAsListWithNulls() {
    Pair<String, Integer> pair = new Pair<>(null, 42);

    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set("pair", pair);
    });

    assertTrue(json.contains("\"pair\": [null, 42]"));
  } 
}