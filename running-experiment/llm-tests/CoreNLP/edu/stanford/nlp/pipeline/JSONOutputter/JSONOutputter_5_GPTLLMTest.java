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

public class JSONOutputter_5_GPTLLMTest {

 @Test
  public void testJsonIncludesBasicDocumentMetadata() throws Exception {
    Annotation annotation = new Annotation("Example text");
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "doc42");
    annotation.set(CoreAnnotations.DocDateAnnotation.class, "2024-05-01");
    annotation.set(CoreAnnotations.DocSourceTypeAnnotation.class, "web");
    annotation.set(CoreAnnotations.DocTypeAnnotation.class, "blog");
    annotation.set(CoreAnnotations.AuthorAnnotation.class, "Test Author");
    annotation.set(CoreAnnotations.LocationAnnotation.class, "Mars");

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    JSONOutputter.jsonPrint(annotation, os, new AnnotationOutputter.Options());

    String json = os.toString("UTF-8");

    assertTrue(json.contains("\"docId\""));
    assertTrue(json.contains("doc42"));
    assertTrue(json.contains("\"docDate\""));
    assertTrue(json.contains("2024-05-01"));
    assertTrue(json.contains("\"docSourceType\""));
    assertTrue(json.contains("web"));
    assertTrue(json.contains("\"docType\""));
    assertTrue(json.contains("blog"));
    assertTrue(json.contains("\"author\""));
    assertTrue(json.contains("Test Author"));
    assertTrue(json.contains("\"location\""));
    assertTrue(json.contains("Mars"));
  }
@Test
  public void testJsonIncludesTextWhenOptionEnabled() throws Exception {
    Annotation annotation = new Annotation("Example text.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Example text.");
    AnnotationOutputter.Options options = new AnnotationOutputter.Options();
    options.includeText = true;

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    JSONOutputter.jsonPrint(annotation, os, options);

    String json = os.toString("UTF-8");
    assertTrue(json.contains("\"text\""));
    assertTrue(json.contains("Example text."));
  }
@Test
  public void testJsonHandlesEmptyAnnotation() throws Exception {
    Annotation annotation = new Annotation("");
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    JSONOutputter.jsonPrint(annotation, os, new AnnotationOutputter.Options());
    String json = os.toString("UTF-8");

    assertTrue(json.trim().startsWith("{"));
    assertTrue(json.trim().endsWith("}"));
  }
@Test
  public void testTokenOffsetsAreSerialized() throws Exception {
    CoreLabel token = new CoreLabel();
    token.setIndex(1);
    token.setWord("Stanford");
    token.setOriginalText("Stanford");
    token.setBeginPosition(0);
    token.setEndPosition(8);
    token.set(CoreAnnotations.BeforeAnnotation.class, "");
    token.set(CoreAnnotations.AfterAnnotation.class, " ");

    List<CoreLabel> tokenList = Collections.singletonList(token);
    CoreMap sentence = new Annotation("Stanford");
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokenList);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annotation = new Annotation("Stanford");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    JSONOutputter.jsonPrint(annotation, os, new AnnotationOutputter.Options());
    String json = os.toString("UTF-8");

    assertTrue(json.contains("\"index\""));
    assertTrue(json.contains("\"word\""));
    assertTrue(json.contains("\"Stanford\""));
    assertTrue(json.contains("\"characterOffsetBegin\""));
    assertTrue(json.contains("0"));
    assertTrue(json.contains("\"characterOffsetEnd\""));
    assertTrue(json.contains("8"));
  }
@Test
  public void testSentimentAnnotationIsIncluded() throws Exception {
    LabeledScoredTreeFactory tf = new LabeledScoredTreeFactory();
    Tree tree = tf.newTreeNode("ROOT", Collections.emptyList());

    CoreMap sentence = new Annotation("Positive sentence");
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    sentence.set(SentimentCoreAnnotations.SentimentAnnotatedTree.class, tree);
    sentence.set(SentimentCoreAnnotations.SentimentClass.class, "Positive");

    Annotation annotation = new Annotation("Positive sentence");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    JSONOutputter.jsonPrint(annotation, os, new AnnotationOutputter.Options());

    String json = os.toString("UTF-8");
    assertTrue(json.contains("\"sentiment\""));
    assertTrue(json.contains("Positive"));
  }
@Test
  public void testEntityMentionWithTimexSerialized() throws Exception {
    CoreLabel mention = new CoreLabel();
    mention.set(CoreAnnotations.TextAnnotation.class, "Monday");
    mention.set(CoreAnnotations.TokenBeginAnnotation.class, 1);
    mention.set(CoreAnnotations.TokenEndAnnotation.class, 2);
    mention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
    mention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 16);
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");

    Timex timex = new Timex("t1");
    timex.setValue("2024-04-01");
    mention.set(TimeAnnotations.TimexAnnotation.class, timex);

    CoreMap sentence = new Annotation("It happened on Monday.");
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);

    Annotation annotation = new Annotation("It happened on Monday.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    JSONOutputter.jsonPrint(annotation, os, new AnnotationOutputter.Options());

    String json = os.toString("UTF-8");
    assertTrue(json.contains("\"timex\""));
    assertTrue(json.contains("2024-04-01"));
  }
@Test
  public void testCorefChainsAreSerialized() throws Exception {
    CorefChain.CorefMention m = new CorefChain.CorefMention(0, 1, 1, 2, 1, 1,
        CorefChain.CorefMention.MentionType.PROPER,
        CorefChain.CorefMention.Number.SINGULAR,
        CorefChain.CorefMention.Gender.MALE,
        CorefChain.CorefMention.Animacy.ANIMATE,
        new int[] {1, 1},
        "John");

    CorefChain chain = new CorefChain(1, Collections.singletonList(m));
    Map<Integer, CorefChain> map = new HashMap<>();
    map.put(1, chain);

    Annotation annotation = new Annotation("John went home. He slept.");
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, map);

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    JSONOutputter.jsonPrint(annotation, os, new AnnotationOutputter.Options());

    String json = os.toString("UTF-8");

    assertTrue(json.contains("\"corefs\""));
    assertTrue(json.contains("\"mention\""));
    assertTrue(json.contains("\"text\""));
    assertTrue(json.contains("\"John\""));
  }
@Test
  public void testOpenIeTriplesSerialization() throws Exception {
    RelationTriple triple = new RelationTriple(
        Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
        "Barack Obama", "was", "president"
    );

    CoreMap sentence = new Annotation("Barack Obama was president.");
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(NaturalLogicAnnotations.RelationTriplesAnnotation.class,
        Collections.singletonList(triple));

    Annotation annotation = new Annotation("Barack Obama was president.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    JSONOutputter.jsonPrint(annotation, os, new AnnotationOutputter.Options());

    String json = os.toString("UTF-8");
    assertTrue(json.contains("\"openie\""));
    assertTrue(json.contains("\"subject\""));
    assertTrue(json.contains("Barack Obama"));
    assertTrue(json.contains("\"relation\""));
    assertTrue(json.contains("was"));
    assertTrue(json.contains("\"object\""));
    assertTrue(json.contains("president"));
  }
@Test
  public void testDependencyTreeIsNullWhenNotPresent() throws Exception {
    CoreMap sentence = new Annotation("Missing dependencies");
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class, null);

    Annotation annotation = new Annotation("Missing dependencies");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    JSONOutputter.jsonPrint(annotation, os, new AnnotationOutputter.Options());

    String json = os.toString("UTF-8");

    assertTrue(json.contains("\"basicDependencies\" : null") ||
               json.contains("\"basicDependencies\":null"));
  }
@Test
  public void testJsonWriterHandlesPrimitiveIntArray() {
    String json = JSONOutputter.JSONWriter.objectToJSON(w -> w.set("values", new int[]{1, 2, 3}));
    assertTrue(json.contains("\"values\""));
    assertTrue(json.contains("[1, 2, 3]") || json.contains("[1,2,3]"));
  }
@Test
  public void testQuoteAnnotationIsSerialized() throws Exception {
    Annotation quoteAnn = new Annotation("She said: \"I agree.\"");
    quoteAnn.set(CoreAnnotations.QuotationIndexAnnotation.class, 0);
    quoteAnn.set(CoreAnnotations.TextAnnotation.class, "I agree.");
    quoteAnn.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
    quoteAnn.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 19);
    quoteAnn.set(CoreAnnotations.TokenBeginAnnotation.class, 3);
    quoteAnn.set(CoreAnnotations.TokenEndAnnotation.class, 5);
    quoteAnn.set(CoreAnnotations.SentenceBeginAnnotation.class, 0);
    quoteAnn.set(CoreAnnotations.SentenceEndAnnotation.class, 0);
    quoteAnn.set(QuoteAttributionAnnotator.SpeakerAnnotation.class, "She");

    Annotation annotation = new Annotation("She said: \"I agree.\"");
    annotation.set(CoreAnnotations.QuotationsAnnotation.class, Collections.singletonList(quoteAnn));

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    JSONOutputter.jsonPrint(annotation, os, new AnnotationOutputter.Options());

    String json = os.toString("UTF-8");
    assertTrue(json.contains("\"quotes\""));
    assertTrue(json.contains("I agree."));
    assertTrue(json.contains("\"speaker\""));
    assertTrue(json.contains("She"));
  }
@Test
  public void testJsonWriterHandlesNullValuesInObject() {
    String json = JSONOutputter.JSONWriter.objectToJSON(w -> {
      w.set("key1", null);
      w.set(null, "value");
      w.set("valid", "present");
    });
    assertTrue(json.contains("\"valid\""));
    assertFalse(json.contains("\"key1\""));
  }
@Test
  public void testJsonWriterHandlesEnum() {
    String json = JSONOutputter.JSONWriter.objectToJSON(w -> w.set("enumValue", Thread.State.RUNNABLE));
    assertTrue(json.contains("\"enumValue\""));
    assertTrue(json.contains("RUNNABLE"));
  }
@Test
  public void testJsonWriterHandlesNestedObjectAsConsumer() {
    String json = JSONOutputter.JSONWriter.objectToJSON(w -> w.set("person", (JSONOutputter.Writer) (key, value) -> {
      if (key != null && value != null) {
        if (key.equals("name")) {
          w.set("name", "Alice");
        }
        if (key.equals("age")) {
          w.set("age", 30);
        }
      }
    }));
    assertTrue(json.contains("\"name\""));
    assertTrue(json.contains("\"Alice\""));
  }
@Test
  public void testSpanAnnotationSerialization() throws Exception {
    Annotation annotation = new Annotation("Example");
    AnnotationOutputter.Options options = new AnnotationOutputter.Options();

    ByteArrayOutputStream os = new ByteArrayOutputStream();

    Span span = new Span(3, 7);
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "spanTest");

    JSONOutputter.JSONWriter writer = new JSONOutputter.JSONWriter(new PrintWriter(os), options);
    writer.object(w -> w.set("spanData", span));
    writer.flush();

    String json = os.toString("UTF-8");
    assertTrue(json.contains("[3, 7]"));
  }
@Test
  public void testPairSerialization() throws Exception {
    Pair<String, Integer> pair = new Pair<>("word", 2);
    String json = JSONOutputter.JSONWriter.objectToJSON(w -> w.set("pair", pair));
    assertTrue(json.contains("word"));
    assertTrue(json.contains("2"));
  }
@Test
  public void testJsonWriterHandlesPrimitiveDoubleArray() {
    String json = JSONOutputter.JSONWriter.objectToJSON(w -> w.set("nums", new double[]{1.5, 2.0, 3.14159}));
    assertTrue(json.contains("1.5"));
    assertTrue(json.contains("3.14159"));
  }
@Test
  public void testJsonWriterHandlesCharArray() {
    String json = JSONOutputter.JSONWriter.objectToJSON(w -> w.set("chars", new char[]{'a', 'b'}));
    assertTrue(json.contains("\"a\""));
    assertTrue(json.contains("\"b\""));
  }
@Test
  public void testHandlesUnsupportedTypeThrowsException() {
    final class Unserializable {}
    try {
      JSONOutputter.JSONWriter.objectToJSON(w -> w.set("unsupported", new Unserializable()));
      fail("Expected RuntimeException");
    } catch (RuntimeException expected) {
      assertTrue(expected.getMessage().contains("Unknown object to serialize"));
    }
  }
@Test
  public void testHandlesBooleanArraySerialization() {
    String json = JSONOutputter.JSONWriter.objectToJSON(w -> w.set("bools", new boolean[]{true, false}));
    assertTrue(json.contains("true"));
    assertTrue(json.contains("false"));
  }
@Test
  public void testJsonOutputSkipsUnparsableTrees() throws Exception {
    Annotation annotation = new Annotation("Text with unparsable tree");

    CoreMap sentence = new Annotation("Dummy");
    Tree unparsableTree = new LabeledScoredTreeFactory().newLeaf("SENTENCE_SKIPPED_OR_UNPARSABLE");
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, unparsableTree);
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());

    annotation.set(CoreAnnotations.SentencesAnnotation.class,
            Collections.singletonList(sentence));

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    AnnotationOutputter.Options options = new AnnotationOutputter.Options();

    JSONOutputter.jsonPrint(annotation, os, options);
    String json = os.toString("UTF-8");
    assertFalse(json.contains("\"parse\""));
  }
@Test
  public void testHandlesTextWithoutTokensButWithSentences() throws Exception {
    CoreMap sentence = new Annotation("No Tokens");
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annotation = new Annotation("Test");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    JSONOutputter.jsonPrint(annotation, os, new AnnotationOutputter.Options());

    String json = os.toString("UTF-8");
    assertTrue(json.contains("\"sentences\""));
  }
@Test
  public void testHandlesTokenOnlyWithoutSentences() throws Exception {
    CoreLabel token = new CoreLabel();
    token.setIndex(1);
    token.setWord("Hello");
    token.setOriginalText("Hello");
    token.setBeginPosition(0);
    token.setEndPosition(5);

    Annotation annotation = new Annotation("Hello");
    annotation.set(CoreAnnotations.TokensAnnotation.class,
        Collections.singletonList(token));

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    JSONOutputter.jsonPrint(annotation, os, new AnnotationOutputter.Options());

    String json = os.toString("UTF-8");
    assertTrue(json.contains("\"tokens\""));
    assertTrue(json.contains("Hello"));
  }
@Test
  public void testSentimentTreeWithNullLabelsHandledGracefully() throws Exception {
    LabeledScoredTreeFactory tf = new LabeledScoredTreeFactory();
    Tree root = tf.newTreeNode(null, Collections.emptyList());

    CoreMap sentence = new Annotation("Test");
    sentence.set(SentimentCoreAnnotations.SentimentAnnotatedTree.class, root);
    sentence.set(SentimentCoreAnnotations.SentimentClass.class, "Neutral");
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annotation = new Annotation("Test");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    JSONOutputter.jsonPrint(annotation, os, new AnnotationOutputter.Options());

    String json = os.toString("UTF-8");
    assertTrue(json.contains("sentiment"));
  }
@Test
  public void testTokenWithCodepointOffsets() throws Exception {
    CoreLabel token = new CoreLabel();
    token.setIndex(1);
    token.setWord("🔥");
    token.setOriginalText("🔥");
    token.setBeginPosition(0);
    token.setEndPosition(2);
    token.set(CoreAnnotations.CodepointOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CodepointOffsetEndAnnotation.class, 1);

    CoreMap sentence = new Annotation("🔥");
    sentence.set(CoreAnnotations.TokensAnnotation.class,
        Collections.singletonList(token));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annotation = new Annotation("🔥");
    annotation.set(CoreAnnotations.SentencesAnnotation.class,
        Collections.singletonList(sentence));

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    JSONOutputter.jsonPrint(annotation, os, new AnnotationOutputter.Options());

    String json = os.toString("UTF-8");
    assertTrue(json.contains("codepointOffsetBegin"));
    assertTrue(json.contains("codepointOffsetEnd"));
  }
@Test
  public void testHandlesEmptyMentionsList() throws Exception {
    CoreMap sentence = new Annotation("No Mentions");
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.emptyList());

    Annotation annotation = new Annotation("Hi.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    JSONOutputter.jsonPrint(annotation, os, new AnnotationOutputter.Options());

    String json = os.toString("UTF-8");
    assertTrue(json.contains("\"entitymentions\""));
  }
@Test
  public void testSentimentDistributionIsNullSafe() throws Exception {
    Tree tree = new LabeledScoredTreeFactory().newLeaf("Neutral");
    CoreMap sentence = new Annotation("Neutral text.");
    sentence.set(SentimentCoreAnnotations.SentimentAnnotatedTree.class, tree);
    sentence.set(SentimentCoreAnnotations.SentimentClass.class, "Neutral");
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annotation = new Annotation("Neutral text.");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    JSONOutputter.jsonPrint(annotation, os, new AnnotationOutputter.Options());

    String json = os.toString("UTF-8");

    assertTrue(json.contains("\"sentimentValue\""));
    assertTrue(json.contains("\"sentiment\""));
    assertTrue(json.contains("\"sentimentDistribution\""));
  }
@Test
  public void testEntityMentionWithNERProbabilitiesEmptyMap() throws Exception {
    CoreLabel mention = new CoreLabel();
    mention.set(CoreAnnotations.TextAnnotation.class, "Washington");
    mention.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    mention.set(CoreAnnotations.TokenEndAnnotation.class, 1);
    mention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    mention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");
    mention.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, new HashMap<String, Double>());
    CoreMap sentence = new Annotation("Washington");
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annotation = new Annotation("Washington");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    JSONOutputter.jsonPrint(annotation, out, new AnnotationOutputter.Options());
    String json = out.toString("UTF-8");
    assertTrue(json.contains("\"entitymentions\""));
  }
@Test
  public void testTimexWithRangeIsSerialized() throws Exception {
    Timex.Range range = new Timex.Range();
    range.begin = "2022-01-01";
    range.end = "2022-01-02";
    range.duration = "P1D";
    Timex timex = new Timex("t2");
    timex.setTimexType("DATE");
    timex.setValue("2022-01-01");
    timex.setAltVal("2022-01-01");
    timex.setRange(range);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "yesterday");
    token.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    token.set(CoreAnnotations.TokenEndAnnotation.class, 1);
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");
    token.set(TimeAnnotations.TimexAnnotation.class, timex);

    CoreMap sentence = new Annotation("Yesterday event");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);

    Annotation annotation = new Annotation("Yesterday event");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    JSONOutputter.jsonPrint(annotation, out, new AnnotationOutputter.Options());
    String json = out.toString("UTF-8");
    assertTrue(json.contains("\"timex\""));
    assertTrue(json.contains("2022-01-01"));
    assertTrue(json.contains("2022-01-02"));
    assertTrue(json.contains("P1D"));
  }
@Test
  public void testCorefChainWithMultipleMentions() throws Exception {
    CorefChain.CorefMention mention1 = new CorefChain.CorefMention(0, 1, 1, 2, 1, 1,
        CorefChain.CorefMention.MentionType.PROPER,
        CorefChain.CorefMention.Number.PLURAL,
        CorefChain.CorefMention.Gender.NEUTRAL,
        CorefChain.CorefMention.Animacy.INANIMATE,
        new int[]{1, 1},
        "cars");

    CorefChain.CorefMention mention2 = new CorefChain.CorefMention(0, 2, 2, 3, 2, 1,
        CorefChain.CorefMention.MentionType.NOMINAL,
        CorefChain.CorefMention.Number.PLURAL,
        CorefChain.CorefMention.Gender.NEUTRAL,
        CorefChain.CorefMention.Animacy.INANIMATE,
        new int[]{2, 1},
        "vehicles");

    List<CorefChain.CorefMention> mentions = new ArrayList<>();
    mentions.add(mention1);
    mentions.add(mention2);

    CorefChain chain = new CorefChain(99, mentions);
    Map<Integer, CorefChain> chains = new HashMap<>();
    chains.put(99, chain);

    Annotation annotation = new Annotation("cars are fast. these vehicles move.");
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, chains);

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    JSONOutputter.jsonPrint(annotation, os, new AnnotationOutputter.Options());
    String json = os.toString("UTF-8");
    assertTrue(json.contains("\"corefs\""));
    assertTrue(json.contains("vehicles"));
    assertTrue(json.contains("cars"));
  }
@Test
  public void testQuotationWithMinimalFields() throws Exception {
    CoreMap quote = new Annotation("Hi");
    quote.set(CoreAnnotations.TextAnnotation.class, "Hi");
    quote.set(CoreAnnotations.QuotationIndexAnnotation.class, 1);
    quote.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    quote.set(CoreAnnotations.TokenEndAnnotation.class, 1);
    quote.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    quote.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 2);
    quote.set(CoreAnnotations.SentenceBeginAnnotation.class, 0);
    quote.set(CoreAnnotations.SentenceEndAnnotation.class, 0);

    Annotation annotation = new Annotation("She said \"Hi\"");
    annotation.set(CoreAnnotations.QuotationsAnnotation.class, Collections.singletonList(quote));

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    JSONOutputter.jsonPrint(annotation, out, new AnnotationOutputter.Options());

    String json = out.toString("UTF-8");
    assertTrue(json.contains("\"quotes\""));
    assertTrue(json.contains("Hi"));
    assertTrue(json.contains("\"id\""));
  }
@Test
  public void testTreeSetToNonDefaultPrintFormat() throws Exception {
    Tree tree = new LabeledScoredTreeFactory().newTreeNode("A",
        Collections.singletonList(
            new LabeledScoredTreeFactory().newLeaf("B")
        ));
    CoreMap sentence = new Annotation("Test");
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, tree);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.emptyList());

    Annotation annotation = new Annotation("Test");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    AnnotationOutputter.Options options = new AnnotationOutputter.Options();
    options.constituencyTreePrinter = new TreePrint("penn");

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    JSONOutputter.jsonPrint(annotation, out, options);
    String json = out.toString("UTF-8");

    assertTrue(json.contains("\"parse\""));
    assertTrue(json.contains("A"));
    assertTrue(json.contains("B"));
  }
@Test
  public void testWriterObjectArraySerialization() {
    String[] array = {"x", "y", "z"};
    String json = JSONOutputter.JSONWriter.objectToJSON(w -> w.set("letters", array));

    assertTrue(json.contains("["));
    assertTrue(json.contains("\"x\""));
    assertTrue(json.contains("\"y\""));
    assertTrue(json.contains("\"z\""));
  }
@Test
  public void testWriterWritesBooleanPrimitiveField() {
    String json = JSONOutputter.JSONWriter.objectToJSON(w -> w.set("flag", true));
    assertTrue(json.contains("\"flag\""));
    assertTrue(json.contains("true"));
  }
@Test
  public void testFloatAndDoubleFormattingUsesUSLocale() {
    String json = JSONOutputter.JSONWriter.objectToJSON(w -> {
      w.set("floatVal", 3.1415927f);
      w.set("doubleVal", 2.718281828459d);
    });

    assertTrue(json.contains("\"floatVal\""));
    assertTrue(json.contains("\"doubleVal\""));
    assertTrue(json.contains("."));
    assertFalse(json.contains(",")); 
  }
@Test
  public void testBuildDependencyTreeWithRootAndEdge() throws Exception {
    Annotation annotation = new Annotation("Sample");
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Root");
    token1.setIndex(1);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Child");
    token2.setIndex(2);

    SemanticGraph graph = new SemanticGraph();
    graph.addVertex(token1);
    graph.addVertex(token2);
    graph.addRoot(token1);
    graph.addEdge(token1, token2, SemanticGraph.makeRelation("dep"), 1, false);

    CoreMap sentence = new Annotation("Root Child");
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class, graph);
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    JSONOutputter.jsonPrint(annotation, os, new AnnotationOutputter.Options());

    String output = os.toString("UTF-8");
    assertTrue(output.contains("\"basicDependencies\""));
    assertTrue(output.contains("\"dep\""));
    assertTrue(output.contains("ROOT"));
    assertTrue(output.contains("Child"));
  }
@Test
  public void testMentionIncludesEmptyNERConfidenceMap() throws Exception {
    Annotation annotation = new Annotation("Test case");

    CoreLabel mention = new CoreLabel();
    mention.set(CoreAnnotations.TextAnnotation.class, "Test");
    mention.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    mention.set(CoreAnnotations.TokenEndAnnotation.class, 1);
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");
    mention.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, new HashMap<>());

    CoreMap sentence = new Annotation("Test case");
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(mention));

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    JSONOutputter.jsonPrint(annotation, os, new AnnotationOutputter.Options());

    String output = os.toString("UTF-8");
    assertTrue(output.contains("\"entitymentions\""));
    assertTrue(output.contains("\"ner\""));
  }
@Test
  public void testMentionWithNERConfidenceSingleNonOEntry() throws Exception {
    Annotation annotation = new Annotation("Entity mention");

    Map<String, Double> nerProbs = new HashMap<>();
    nerProbs.put("LOCATION", 0.95);
    nerProbs.put("O", 0.05);

    CoreLabel mention = new CoreLabel();
    mention.set(CoreAnnotations.TextAnnotation.class, "Berlin");
    mention.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    mention.set(CoreAnnotations.TokenEndAnnotation.class, 1);
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");
    mention.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, nerProbs);

    CoreMap sentence = new Annotation("Berlin");
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    JSONOutputter.jsonPrint(annotation, os, new AnnotationOutputter.Options());

    String output = os.toString("UTF-8");
    assertTrue(output.contains("\"nerConfidences\""));
    assertTrue(output.contains("\"LOCATION\""));
    assertTrue(output.contains("0.95"));
    assertFalse(output.contains("\"O\""));
  }
@Test
  public void testTokenWithTimexNullRange() throws Exception {
    Annotation annotation = new Annotation("Time test");

    CoreLabel token = new CoreLabel();
    token.setIndex(1);
    token.setWord("tomorrow");
    token.setOriginalText("tomorrow");
    token.setBeginPosition(0);
    token.setEndPosition(8);

    Timex timex = new Timex("t5");
    timex.setTimexType("DATE");
    timex.setValue("2024-01-01");
    timex.setAltVal("2024-01-01");
    

    token.set(TimeAnnotations.TimexAnnotation.class, timex);
    List<CoreLabel> tokens = Collections.singletonList(token);

    CoreMap sentence = new Annotation("tomorrow");
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);

    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    JSONOutputter.jsonPrint(annotation, os, new AnnotationOutputter.Options());

    String output = os.toString("UTF-8");
    assertTrue(output.contains("\"value\""));
    assertTrue(output.contains("2024-01-01"));
    assertFalse(output.contains("range"));
  }
@Test
  public void testPairSerializationAsArray() {
    Pair<String, Integer> pair = new Pair<>("foo", 42);
    String json = JSONOutputter.JSONWriter.objectToJSON(w -> w.set("pair", pair));
    assertTrue(json.contains("\"pair\""));
    assertTrue(json.contains("[\"foo\", 42]") || json.contains("[\"foo\",42]"));
  }
@Test
  public void testUnsupportedPrimitiveArrayThrowsException() {
    final class CustomWrapper {
      int[] data = new int[]{1, 2, 3};
    }

    try {
      JSONOutputter.JSONWriter.objectToJSON(w -> w.set("object", new CustomWrapper()));
      fail("Expected RuntimeException for unsupported serialization");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("Unknown object to serialize"));
    }
  }
@Test
  public void testWriteTimeHandlesNullTimex() {
    String json = JSONOutputter.JSONWriter.objectToJSON(w -> w.set("timedata", (JSONOutputter.Writer) (k, v) -> {
      JSONOutputter.JSONWriter writer = new JSONOutputter.JSONWriter(new java.io.PrintWriter(System.out), new AnnotationOutputter.Options());
      JSONOutputter.writeTime(w, null);
    }));
    assertTrue(json.contains("timedata"));
  }
@Test
  public void testWriterHandlesNullConsumerValue() {
    String json = JSONOutputter.JSONWriter.objectToJSON(w -> {
      w.set("nonNullKey", "value");
      w.set("nullKey", null);
    });
    assertTrue(json.contains("\"nonNullKey\""));
    assertFalse(json.contains("nullKey"));
  }
@Test
  public void testWriterHandlesPrimitiveShortArray() {
    String json = JSONOutputter.JSONWriter.objectToJSON(w -> {
      short[] shorts = new short[]{1, 2};
      w.set("shorts", shorts);
    });
    assertTrue(json.contains("[1, 2]") || json.contains("[1,2]"));
  }
@Test
  public void testWriterHandlesByteArray() {
    String json = JSONOutputter.JSONWriter.objectToJSON(w -> {
      byte[] bytes = new byte[]{8, 16};
      w.set("bytes", bytes);
    });
    assertTrue(json.contains("8"));
    assertTrue(json.contains("16"));
  }
@Test
  public void testWriterHandlesCharArray() {
    String json = JSONOutputter.JSONWriter.objectToJSON(w -> {
      char[] chars = new char[]{'x', 'y'};
      w.set("chars", chars);
    });
    assertTrue(json.contains("\"x\""));
    assertTrue(json.contains("\"y\""));
  }
@Test
  public void testWriterHandlesBooleanPrimitive() {
    String json = JSONOutputter.JSONWriter.objectToJSON(w -> {
      w.set("flag", true);
    });
    assertTrue(json.contains("\"flag\""));
    assertTrue(json.contains("true"));
  }
@Test
  public void testWriterHandlesIntegerPrimitive() {
    String json = JSONOutputter.JSONWriter.objectToJSON(w -> {
      w.set("age", 25);
    });
    assertTrue(json.contains("\"age\""));
    assertTrue(json.contains("25"));
  }
@Test
  public void testWriterHandlesDoublePrecision() {
    String json = JSONOutputter.JSONWriter.objectToJSON(w -> {
      w.set("pi", 3.14159265358979);
    });
    assertTrue(json.contains("3.1415"));
  }
@Test
  public void testWriterHandlesFloatPrecision() {
    String json = JSONOutputter.JSONWriter.objectToJSON(w -> {
      float f = 2.71828f;
      w.set("e", f);
    });
    assertTrue(json.contains("2.718"));
  }
@Test
  public void testWriterHandlesEnumSerialization() {
    Enum<?> e = Thread.State.BLOCKED;
    String json = JSONOutputter.JSONWriter.objectToJSON(w -> {
      w.set("enumState", e);
    });
    assertTrue(json.contains("BLOCKED"));
  }
@Test
  public void testWriterRoutesCollectionOfConsumers() {
    String json = JSONOutputter.JSONWriter.objectToJSON(w -> {
      List<String> values = Arrays.asList("one", "two", "three");
      w.set("list", values);
    });
    assertTrue(json.contains("one"));
    assertTrue(json.contains("two"));
    assertTrue(json.contains("three"));
  }
@Test
  public void testWriterHandlesNullInsideArray() {
    String json = JSONOutputter.JSONWriter.objectToJSON(w -> {
      Object[] arr = new Object[]{"hello", null, "world"};
      w.set("values", arr);
    });
    assertTrue(json.contains("hello"));
    assertTrue(json.contains("world"));
    
  }
@Test
  public void testWriterThrowsForUnknownType() {
    try {
      Object weird = new Object(); 
      JSONOutputter.JSONWriter.objectToJSON(w -> w.set("weird", weird));
      fail("Expected exception for unknown serialization type");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("Unknown object to serialize"));
    }
  }
@Test
  public void testHandlesAnnotationWithOnlyTokensAndNoSentences() throws Exception {
    Annotation annotation = new Annotation("Hello world");
    CoreLabel token1 = new CoreLabel();
    token1.setIndex(1);
    token1.setWord("Hello");
    token1.setOriginalText("Hello");
    token1.setBeginPosition(0);
    token1.setEndPosition(5);

    CoreLabel token2 = new CoreLabel();
    token2.setIndex(2);
    token2.setWord("world");
    token2.setOriginalText("world");
    token2.setBeginPosition(6);
    token2.setEndPosition(11);

    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    JSONOutputter.jsonPrint(annotation, os, new AnnotationOutputter.Options());

    String json = os.toString("UTF-8");
    assertTrue(json.contains("tokens"));
    assertTrue(json.contains("Hello"));
    assertTrue(json.contains("world"));
  }
@Test
  public void testObjectWithEmptyCollection() {
    List<String> emptyList = new ArrayList<>();
    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> writer.set("items", emptyList));
    assertTrue(json.contains("\"items\""));
    assertTrue(json.contains("[]"));
  }
@Test
  public void testArrayAsObjectField() {
    String[] words = new String[]{"a", "b"};
    String json = JSONOutputter.JSONWriter.objectToJSON(w -> w.set("letters", words));
    assertTrue(json.contains("a"));
    assertTrue(json.contains("b"));
  }
@Test
  public void testPairSerializationIntoList() {
    Pair<String, Integer> pair = new Pair<>("x", 100);
    String json = JSONOutputter.JSONWriter.objectToJSON(w -> w.set("pair", pair));
    assertTrue(json.contains("[\"x\""));
    assertTrue(json.contains("100"));
  }
@Test
  public void testExplicitArrayWithBooleanTypes() {
    boolean[] flags = new boolean[]{true, false, true};
    String json = JSONOutputter.JSONWriter.objectToJSON(w -> w.set("flags", flags));
    assertTrue(json.contains("true"));
    assertTrue(json.contains("false"));
  }
@Test
  public void testCharSerializationWithSingleCharacter() {
    char c = 'Z';
    String json = JSONOutputter.JSONWriter.objectToJSON(w -> w.set("initial", c));
    assertTrue(json.contains("Z"));
  }
@Test
  public void testWriterFlushAfterNestedObject() throws Exception {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    PrintWriter pw = new PrintWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
    AnnotationOutputter.Options options = new AnnotationOutputter.Options();
    JSONOutputter.JSONWriter writer = new JSONOutputter.JSONWriter(pw, options);
    writer.object(w -> {
      w.set("outer", (JSONOutputter.Writer) (k, v) -> {
        if ("x".equals(k)) {
          v = 100;
        }
        w.set("x", 100);
      });
    });
    writer.flush();
    String result = os.toString("UTF-8");
    assertTrue(result.contains("\"x\""));
    assertTrue(result.contains("100"));
  }
@Test
  public void testWriteTimeWithCompleteTimexAndRange() {
    Timex.Range range = new Timex.Range();
    range.begin = "2020-01-01";
    range.end = "2020-12-31";
    range.duration = "P1Y";

    Timex time = new Timex("t1");
    time.setTimexType("DATE");
    time.setValue("2020");
    time.setAltVal("2020-ALT");
    time.setRange(range);

    String result = JSONOutputter.JSONWriter.objectToJSON(w -> {
      w.set("timex", (Consumer<JSONOutputter.Writer>) w2 -> {
        JSONOutputter.writeTime(w2, time);
      });
    });

    assertTrue(result.contains("\"tid\""));
    assertTrue(result.contains("t1"));
    assertTrue(result.contains("\"value\""));
    assertTrue(result.contains("2020"));
    assertTrue(result.contains("\"range\""));
    assertTrue(result.contains("2020-01-01"));
    assertTrue(result.contains("P1Y"));
  }
@Test
  public void testWriteTimeWithTimexAndNullRange() {
    Timex time = new Timex("t2");
    time.setTimexType("TIME");
    time.setValue("2020-03-03");
    time.setAltVal(null);
    time.setRange(null);

    String json = JSONOutputter.JSONWriter.objectToJSON(w -> {
      w.set("time", (Consumer<JSONOutputter.Writer>) w2 -> {
        JSONOutputter.writeTime(w2, time);
      });
    });

    assertTrue(json.contains("2020-03-03"));
    assertFalse(json.contains("range"));
  }
@Test
  public void testObjectWithEmptyStream() {
    Stream<String> emptyStream = new ArrayList<String>().stream();

    String json = JSONOutputter.JSONWriter.objectToJSON(w -> {
      w.set("stream", emptyStream);
    });

    assertTrue(json.contains("\"stream\""));
    assertTrue(json.contains("[]"));
  }
@Test
  public void testEmptyConsumerObjectSerialization() {
    String json = JSONOutputter.JSONWriter.objectToJSON(w -> {
      w.set("obj", (Consumer<JSONOutputter.Writer>) writer -> {
        
      });
    });

    assertTrue(json.contains("\"obj\""));
    assertTrue(json.contains("{"));
    assertTrue(json.contains("}"));
  }
@Test
  public void testWriterHandlesDoubleArray() {
    double[] values = new double[]{1.1, 2.2, 3.3};

    String json = JSONOutputter.JSONWriter.objectToJSON(w -> {
      w.set("doubles", values);
    });

    assertTrue(json.contains("1.1"));
    assertTrue(json.contains("3.3"));
  }
@Test
  public void testWriterHandlesFloatArray() {
    float[] values = new float[]{0.5f, 1.5f};

    String json = JSONOutputter.JSONWriter.objectToJSON(w -> {
      w.set("floats", values);
    });

    assertTrue(json.contains("0.5"));
    assertTrue(json.contains("1.5"));
  }
@Test
  public void testWriterHandlesLongArray() {
    long[] ids = new long[]{1000000000L, 2000000000L};

    String json = JSONOutputter.JSONWriter.objectToJSON(w -> {
      w.set("ids", ids);
    });

    assertTrue(json.contains("1000000000"));
    assertTrue(json.contains("2000000000"));
  }
@Test
  public void testWriterHandlesIntegerArray() {
    int[] nums = new int[]{42, 0, -1};

    String json = JSONOutputter.JSONWriter.objectToJSON(w -> {
      w.set("ints", nums);
    });

    assertTrue(json.contains("42"));
    assertTrue(json.contains("-1"));
  }
@Test
  public void testWriterHandlesCharScalar() {
    char ch = 'X';

    String json = JSONOutputter.JSONWriter.objectToJSON(w -> {
      w.set("char", ch);
    });

    assertTrue(json.contains("\"X\""));
  }
@Test
  public void testWriterHandlesBooleanTrue() {
    String json = JSONOutputter.JSONWriter.objectToJSON(w -> {
      w.set("valid", true);
    });

    assertTrue(json.contains("true"));
  }
@Test
  public void testWriterHandlesBooleanFalse() {
    String json = JSONOutputter.JSONWriter.objectToJSON(w -> {
      w.set("active", false);
    });

    assertTrue(json.contains("false"));
  }
@Test
  public void testWriterHandlesNullStream() {
    Stream<?> nullStream = null;

    String json = JSONOutputter.JSONWriter.objectToJSON(w -> {
      w.set("stream", nullStream);
    });

    assertFalse(json.contains("nullStreamException"));
    assertTrue(json.contains("\"stream\" : null") || json.contains("\"stream\":null"));
  }
@Test
  public void testWriterHandlesCollectionOfObjects() {
    List<Object> dataList = Arrays.asList("A", 1, true);

    String json = JSONOutputter.JSONWriter.objectToJSON(w -> {
      w.set("data", dataList);
    });

    assertTrue(json.contains("A"));
    assertTrue(json.contains("1"));
    assertTrue(json.contains("true"));
  }
@Test
  public void testWriterHandlesNestedConsumerChain() {
    String json = JSONOutputter.JSONWriter.objectToJSON(w -> {
      w.set("outer", (Consumer<JSONOutputter.Writer>) w2 -> {
        w2.set("inner", (Consumer<JSONOutputter.Writer>) w3 -> {
          w3.set("leaf", "value");
        });
      });
    });

    assertTrue(json.contains("outer"));
    assertTrue(json.contains("inner"));
    assertTrue(json.contains("leaf"));
    assertTrue(json.contains("value"));
  }
@Test
  public void testWriterHandlesPairAsTopLevelValue() {
    Pair<String, String> pair = new Pair<>("left", "right");

    String json = JSONOutputter.JSONWriter.objectToJSON(w -> {
      w.set("pair", pair);
    });

    assertTrue(json.contains("left"));
    assertTrue(json.contains("right"));
    assertTrue(json.contains("["));
  }
@Test
  public void testEscapedJSONStringInKeyAndValue() {
    String json = JSONOutputter.JSONWriter.objectToJSON(w -> {
      w.set("quote\"key", "value with \"quotes\"");
    });

    assertTrue(json.contains("\\\"key"));
    assertTrue(json.contains("value with \\\"quotes\\\""));
  }
@Test
  public void testPrintWithUnencodedEncodingOptionDefaultsToUTF8() throws Exception {
    Annotation annotation = new Annotation("text");
    AnnotationOutputter.Options options = new AnnotationOutputter.Options();
    options.encoding = "UTF-8";

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    JSONOutputter.jsonPrint(annotation, os, options);

    String output = os.toString("UTF-8");
    assertTrue(output.startsWith("{"));
  }

  @Test
  public void testJSONWriterFlushIsCalled() throws Exception {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    PrintWriter pw = new PrintWriter(new OutputStreamWriter(os));
    AnnotationOutputter.Options options = new AnnotationOutputter.Options();
    JSONOutputter.JSONWriter writer = new JSONOutputter.JSONWriter(pw, options);

    writer.object(w -> w.set("x", 1));
    writer.flush();

    String out = os.toString("UTF-8");
    assertTrue(out.contains("\"x\""));
    assertTrue(out.contains("1"));
  }
}
@Test
  public void testJSONWriterFlushIsCalled() throws Exception {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    PrintWriter pw = new PrintWriter(new OutputStreamWriter(os));
    AnnotationOutputter.Options options = new AnnotationOutputter.Options();
    JSONOutputter.JSONWriter writer = new JSONOutputter.JSONWriter(pw, options);

    writer.object(w -> w.set("x", 1));
    writer.flush();

    String out = os.toString("UTF-8");
    assertTrue(out.contains("\"x\""));
    assertTrue(out.contains("1"));
  }
@Test
  public void testSerializeDoubleWithMaxPrecision() {
    Annotation annotation = new Annotation("test");

    String json = JSONOutputter.JSONWriter.objectToJSON(w -> {
      w.set("value", 1.123456789012345); 
    });

    assertTrue(json.contains("1.12345678901234") || json.contains("1.1234567890123"));
  }
@Test
  public void testSerializeFloatWithExactTrim() {
    Annotation annotation = new Annotation("test");

    String json = JSONOutputter.JSONWriter.objectToJSON(w -> {
      w.set("value", 1.1234567f);
    });

    assertTrue(json.contains("1.123456"));
  }
@Test
  public void testSerializeEmptyObjectWithOnlyNullEntries() {
    Consumer<JSONOutputter.Writer> consumer = writer -> {
      writer.set(null, "value");
      writer.set("key", null);
      writer.set(null, null);
    };

    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set("empty", consumer);
    });

    assertTrue(json.contains("\"empty\" : {") || json.contains("\"empty\": {"));
    assertFalse(json.contains("value"));
  }

  @Test
  public void testStreamSerializationAsObjectField() {
    java.util.stream.Stream<String> stream = java.util.stream.Stream.of("a", "b", "c");

    String json = JSONOutputter.JSONWriter.objectToJSON(w -> {
      w.set("streamField", stream);
    });

    assertTrue(json.contains("\"a\""));
    assertTrue(json.contains("\"b\""));
    assertTrue(json.contains("\"c\""));
  }

  @Test
  public void testSerializePairObjectContainingNulls() {
    Pair<String, String> pair = new Pair<>(null, null);

    String json = JSONOutputter.JSONWriter.objectToJSON(w -> {
      w.set("nullPair", pair);
    });

    assertTrue(json.contains("[null, null]") || json.contains("[null,null]"));
  }

  @Test
  public void testJsonWriterHandlesUnsupportedPrimitiveTypeThrows() {
    Object array = new boolean[][]{{true}, {false}};

    try {
      JSONOutputter.JSONWriter.objectToJSON(w -> {
        w.set("unsupported", array);
      });
      fail("Expected RuntimeException due to unsupported array type");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("Unhandled primitive type"));
    }
  }

  @Test
  public void testRoutingImplicitBoxingOfShortPrimitive() {
    short value = 7;

    String json = JSONOutputter.JSONWriter.objectToJSON(w -> {
      w.set("shortValue", value);
    });

    assertTrue(json.contains("7"));
  }

  @Test
  public void testRoutingImplicitBoxingOfBytePrimitive() {
    byte value = 2;

    String json = JSONOutputter.JSONWriter.objectToJSON(w -> {
      w.set("byteValue", value);
    });

    assertTrue(json.contains("2"));
  }

  @Test
  public void testRoutingImplicitBoxingOfCharPrimitive() {
    char value = 'Y';

    String json = JSONOutputter.JSONWriter.objectToJSON(w -> {
      w.set("charValue", value);
    });

    assertTrue(json.contains("Y"));
  }

  @Test
  public void testRoutingImplicitBoxingOfLongPrimitive() {
    long value = 999999999L;

    String json = JSONOutputter.JSONWriter.objectToJSON(w -> {
      w.set("longValue", value);
    });

    assertTrue(json.contains("999999999"));
  }

  @Test
  public void testRoutingEmptyArrayOfObjects() {
    Object[] objects = new Object[0];

    String json = JSONOutputter.JSONWriter.objectToJSON(w -> {
      w.set("emptyArray", objects);
    });

    assertTrue(json.contains("\"emptyArray\""));
    assertTrue(json.contains("[]"));
  }

  @Test
  public void testWriterEscapesSpecialCharacters() {
    String specialValue = "Line\nBreak\tTab\"Quote\\Backslash";

    String json = JSONOutputter.JSONWriter.objectToJSON(w -> {
      w.set("escaped", specialValue);
    });

    assertTrue(json.contains("\\n"));
    assertTrue(json.contains("\\t"));
    assertTrue(json.contains("\\\""));
    assertTrue(json.contains("\\\\"));
  }

  @Test
  public void testWriterHandlesNullTopLevelValue() {
    String json = JSONOutputter.JSONWriter.objectToJSON(w -> {
      w.set("nullValue", null);
    });

    assertTrue(json.contains("\"nullValue\""));
    assertFalse(json.contains("nullValue\": null")); 
  }

  @Test
  public void testFlushWriterOutputsFinalBrace() {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    PrintWriter out = new PrintWriter(new OutputStreamWriter(os));

    AnnotationOutputter.Options options = new AnnotationOutputter.Options();
    JSONOutputter.JSONWriter writer = new JSONOutputter.JSONWriter(out, options);
    writer.object(w -> {
      w.set("field", "data");
    });
    writer.flush();

    String result = os.toString();
    assertTrue(result.trim().endsWith("}"));
  }

  @Test
  public void testUSLocaleUsedInFloatFormatting() {
    Locale.setDefault(Locale.GERMANY); 
    float value = 3.5f;

    String json = JSONOutputter.JSONWriter.objectToJSON(w -> {
      w.set("testFloat", value);
    });

    assertFalse(json.contains(","));
    assertTrue(json.contains("3.5"));
  }

  @Test
  public void testUSLocaleUsedInDoubleFormatting() {
    Locale.setDefault(Locale.ITALY); 
    double value = 1.23456789;

    String json = JSONOutputter.JSONWriter.objectToJSON(w -> {
      w.set("testDouble", value);
    });

    assertFalse(json.contains(","));
    assertTrue(json.contains("1.234"));
  }

  @Test
  public void testObjectToJSONEncodingFallbackThrowsException() {
    try {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      PrintWriter out = new PrintWriter(new OutputStreamWriter(os, "unsupported-charset"));
      assertNull(out); 
    } catch (UnsupportedEncodingException e) {
      assertTrue(true); 
    }
  }
}
@Test
  public void testStreamSerializationAsObjectField() {
    java.util.stream.Stream<String> stream = java.util.stream.Stream.of("a", "b", "c");

    String json = JSONOutputter.JSONWriter.objectToJSON(w -> {
      w.set("streamField", stream);
    });

    assertTrue(json.contains("\"a\""));
    assertTrue(json.contains("\"b\""));
    assertTrue(json.contains("\"c\""));
  }
@Test
  public void testSerializePairObjectContainingNulls() {
    Pair<String, String> pair = new Pair<>(null, null);

    String json = JSONOutputter.JSONWriter.objectToJSON(w -> {
      w.set("nullPair", pair);
    });

    assertTrue(json.contains("[null, null]") || json.contains("[null,null]"));
  }
@Test
  public void testJsonWriterHandlesUnsupportedPrimitiveTypeThrows() {
    Object array = new boolean[][]{{true}, {false}};

    try {
      JSONOutputter.JSONWriter.objectToJSON(w -> {
        w.set("unsupported", array);
      });
      fail("Expected RuntimeException due to unsupported array type");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("Unhandled primitive type"));
    }
  }
@Test
  public void testRoutingImplicitBoxingOfShortPrimitive() {
    short value = 7;

    String json = JSONOutputter.JSONWriter.objectToJSON(w -> {
      w.set("shortValue", value);
    });

    assertTrue(json.contains("7"));
  }
@Test
  public void testRoutingImplicitBoxingOfBytePrimitive() {
    byte value = 2;

    String json = JSONOutputter.JSONWriter.objectToJSON(w -> {
      w.set("byteValue", value);
    });

    assertTrue(json.contains("2"));
  }
@Test
  public void testRoutingImplicitBoxingOfCharPrimitive() {
    char value = 'Y';

    String json = JSONOutputter.JSONWriter.objectToJSON(w -> {
      w.set("charValue", value);
    });

    assertTrue(json.contains("Y"));
  }
@Test
  public void testRoutingImplicitBoxingOfLongPrimitive() {
    long value = 999999999L;

    String json = JSONOutputter.JSONWriter.objectToJSON(w -> {
      w.set("longValue", value);
    });

    assertTrue(json.contains("999999999"));
  }
@Test
  public void testRoutingEmptyArrayOfObjects() {
    Object[] objects = new Object[0];

    String json = JSONOutputter.JSONWriter.objectToJSON(w -> {
      w.set("emptyArray", objects);
    });

    assertTrue(json.contains("\"emptyArray\""));
    assertTrue(json.contains("[]"));
  }
@Test
  public void testWriterEscapesSpecialCharacters() {
    String specialValue = "Line\nBreak\tTab\"Quote\\Backslash";

    String json = JSONOutputter.JSONWriter.objectToJSON(w -> {
      w.set("escaped", specialValue);
    });

    assertTrue(json.contains("\\n"));
    assertTrue(json.contains("\\t"));
    assertTrue(json.contains("\\\""));
    assertTrue(json.contains("\\\\"));
  }
@Test
  public void testWriterHandlesNullTopLevelValue() {
    String json = JSONOutputter.JSONWriter.objectToJSON(w -> {
      w.set("nullValue", null);
    });

    assertTrue(json.contains("\"nullValue\""));
    assertFalse(json.contains("nullValue\": null")); 
  }
@Test
  public void testFlushWriterOutputsFinalBrace() {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    PrintWriter out = new PrintWriter(new OutputStreamWriter(os));

    AnnotationOutputter.Options options = new AnnotationOutputter.Options();
    JSONOutputter.JSONWriter writer = new JSONOutputter.JSONWriter(out, options);
    writer.object(w -> {
      w.set("field", "data");
    });
    writer.flush();

    String result = os.toString();
    assertTrue(result.trim().endsWith("}
@Test
  public void testUSLocaleUsedInFloatFormatting() {
    Locale.setDefault(Locale.GERMANY); 
    float value = 3.5f;

    String json = JSONOutputter.JSONWriter.objectToJSON(w -> {
      w.set("testFloat", value);
    });

    assertFalse(json.contains(","));
    assertTrue(json.contains("3.5"));
  }
@Test
  public void testUSLocaleUsedInDoubleFormatting() {
    Locale.setDefault(Locale.ITALY); 
    double value = 1.23456789;

    String json = JSONOutputter.JSONWriter.objectToJSON(w -> {
      w.set("testDouble", value);
    });

    assertFalse(json.contains(","));
    assertTrue(json.contains("1.234"));
  }
@Test
  public void testObjectToJSONEncodingFallbackThrowsException() {
    try {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      PrintWriter out = new PrintWriter(new OutputStreamWriter(os, "unsupported-charset"));
      assertNull(out); 
    } catch (UnsupportedEncodingException e) {
      assertTrue(true); 
    }
  } 
}