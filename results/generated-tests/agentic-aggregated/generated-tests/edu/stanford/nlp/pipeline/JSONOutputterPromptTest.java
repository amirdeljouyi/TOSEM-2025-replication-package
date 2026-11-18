package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.Dictionaries;
import edu.stanford.nlp.ie.machinereading.structure.Span;
import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.Timex;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.IntPair;
import edu.stanford.nlp.util.Pair;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class JSONOutputterPromptTest {

  @Test
  public void emptyAnnotationOmitsNullMetadataAndTextByDefault() throws Exception {
    Annotation annotation = new Annotation("invisible text");

    String json = JSONOutputter.jsonPrint(annotation);

    assertEquals("{\n}\n", json);
    assertFalse(json.contains("text"));
  }

  @Test
  public void documentMetadataAndTextAreEscapedAndIncludedWhenRequested() throws Exception {
    Annotation annotation = new Annotation("line one\n\"line two\"\\end");
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "doc-7");
    annotation.set(CoreAnnotations.DocDateAnnotation.class, "2024-02-29");
    annotation.set(CoreAnnotations.DocSourceTypeAnnotation.class, "news");
    annotation.set(CoreAnnotations.DocTypeAnnotation.class, "article");
    annotation.set(CoreAnnotations.AuthorAnnotation.class, "A & B");
    annotation.set(CoreAnnotations.LocationAnnotation.class, "Zürich");
    Properties properties = new Properties();
    properties.setProperty("output.includeText", "true");
    properties.setProperty("output.prettyPrint", "false");

    ByteArrayOutputStream target = new ByteArrayOutputStream();
    JSONOutputter.jsonPrint(annotation, target, new AnnotationOutputter.Options(properties));
    String json = target.toString(StandardCharsets.UTF_8.name());

    assertEquals("{\"docId\":\"doc-7\",\"docDate\":\"2024-02-29\",\"docSourceType\":\"news\",\"docType\":\"article\",\"author\":\"A & B\",\"location\":\"Zürich\",\"text\":\"line one\\n\\\"line two\\\"\\\\end\"}", json);
  }

  @Test
  public void tokensAtDocumentLevelUseTheReducedTokenShape() throws Exception {
    Annotation annotation = new Annotation("Hello");
    CoreLabel token = new CoreLabel();
    token.setIndex(4);
    token.setWord("Hello");
    token.setOriginalText("HELLO");
    token.setBeginPosition(10);
    token.setEndPosition(15);
    token.setLemma("hello");
    token.setTag("UH");
    token.setNER("O");
    token.set(CoreAnnotations.CodepointOffsetBeginAnnotation.class, 8);
    token.set(CoreAnnotations.CodepointOffsetEndAnnotation.class, 13);
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    String json = JSONOutputter.jsonPrint(annotation);

    assertTrue(json.contains("\"index\": 4"));
    assertTrue(json.contains("\"word\": \"Hello\""));
    assertTrue(json.contains("\"originalText\": \"HELLO\""));
    assertTrue(json.contains("\"characterOffsetBegin\": 10"));
    assertTrue(json.contains("\"codepointOffsetEnd\": 13"));
    assertFalse(json.contains("\"lemma\""));
    assertFalse(json.contains("\"pos\""));
    assertFalse(json.contains("\"ner\""));
  }

  @Test
  public void sentenceMetadataAndRichTokenAnnotationsAreSerialized() throws Exception {
    Annotation annotation = new Annotation("Ada spoke.");
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.SentenceIDAnnotation.class, "s-1");
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 2);
    sentence.set(CoreAnnotations.LineNumberAnnotation.class, 9);
    sentence.set(CoreAnnotations.ParagraphIndexAnnotation.class, 3);
    sentence.set(CoreAnnotations.SpeakerAnnotation.class, "narrator");
    sentence.set(CoreAnnotations.SpeakerTypeAnnotation.class, "PER");
    CoreLabel token = new CoreLabel();
    token.setIndex(1);
    token.setWord("Ada");
    token.setOriginalText("Ada");
    token.setLemma("Ada");
    token.setBeginPosition(0);
    token.setEndPosition(3);
    token.setTag("NNP");
    token.setNER("PERSON");
    token.setBefore("");
    token.setAfter(" ");
    token.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "Ada");
    token.set(CoreAnnotations.SpeakerAnnotation.class, "Ada");
    token.set(CoreAnnotations.SpeakerTypeAnnotation.class, "PER");
    token.set(CoreAnnotations.TrueCaseAnnotation.class, "INIT_UPPER");
    token.set(CoreAnnotations.TrueCaseTextAnnotation.class, "Ada");
    token.set(CoreAnnotations.WikipediaEntityAnnotation.class, "Ada_Lovelace");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    String json = JSONOutputter.jsonPrint(annotation);

    assertTrue(json.contains("\"id\": \"s-1\""));
    assertTrue(json.contains("\"paragraph\": 3"));
    assertTrue(json.contains("\"lemma\": \"Ada\""));
    assertTrue(json.contains("\"pos\": \"NNP\""));
    assertTrue(json.contains("\"normalizedNER\": \"Ada\""));
    assertTrue(json.contains("\"truecase\": \"INIT_UPPER\""));
    assertTrue(json.contains("\"entitylink\": \"Ada_Lovelace\""));
  }

  @Test
  public void constituencyAndBinarizedTreesUseOneLineDefaultRendering() throws Exception {
    Annotation annotation = new Annotation("Cats sleep");
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class,
        Tree.valueOf("(ROOT (S (NP (NNS Cats)) (VP (VBP sleep))))"));
    sentence.set(TreeCoreAnnotations.BinarizedTreeAnnotation.class,
        Tree.valueOf("(ROOT (S (NNS Cats) (VBP sleep)))"));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    String json = JSONOutputter.jsonPrint(annotation);

    assertTrue(json.contains("\"parse\": \"(ROOT (S (NP (NNS Cats)) (VP (VBP sleep))))\""));
    assertTrue(json.contains("\"binaryParse\": \"(ROOT (S (NNS Cats) (VBP sleep)))\""));
  }

  @Test
  public void dependencyGraphContainsRootAndSortedEdgeDetails() throws Exception {
    Annotation annotation = new Annotation("Birds fly");
    CoreMap sentence = new ArrayCoreMap();
    CoreLabel birdsLabel = new CoreLabel();
    birdsLabel.setWord("Birds");
    birdsLabel.setIndex(1);
    CoreLabel flyLabel = new CoreLabel();
    flyLabel.setWord("fly");
    flyLabel.setIndex(2);
    IndexedWord birds = new IndexedWord(birdsLabel);
    IndexedWord fly = new IndexedWord(flyLabel);
    SemanticGraph graph = new SemanticGraph();
    graph.addRoot(fly);
    graph.addEdge(fly, birds, GrammaticalRelation.valueOf("nsubj"), 1.0, false);
    sentence.set(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class, graph);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    String json = JSONOutputter.jsonPrint(annotation);

    assertTrue(json.contains("\"dep\": \"ROOT\""));
    assertTrue(json.contains("\"governor\": 0"));
    assertTrue(json.contains("\"dependent\": 2"));
    assertTrue(json.contains("\"dependentGloss\": \"fly\""));
    assertTrue(json.contains("\"dep\": \"nsubj\""));
    assertTrue(json.contains("\"governorGloss\": \"fly\""));
    assertTrue(json.contains("\"dependentGloss\": \"Birds\""));
    assertFalse(json.contains("enhancedDependencies"));
  }

  @Test
  public void entityMentionComputesSentenceRelativeOffsetsAndConfidenceMap() throws Exception {
    Annotation annotation = new Annotation("met Ada");
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 10);
    CoreMap mention = new ArrayCoreMap();
    mention.set(CoreAnnotations.TokenBeginAnnotation.class, 11);
    mention.set(CoreAnnotations.TokenEndAnnotation.class, 12);
    mention.set(CoreAnnotations.TextAnnotation.class, "Ada");
    mention.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 4);
    mention.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    mention.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "Ada Lovelace");
    mention.set(CoreAnnotations.WikipediaEntityAnnotation.class, "Ada_Lovelace");
    Map<String, Double> confidences = new LinkedHashMap<>();
    confidences.put("O", 0.02);
    confidences.put("PERSON", 0.98);
    mention.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, confidences);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    String json = JSONOutputter.jsonPrint(annotation);

    assertTrue(json.contains("\"docTokenBegin\": 11"));
    assertTrue(json.contains("\"tokenBegin\": 1"));
    assertTrue(json.contains("\"tokenEnd\": 2"));
    assertTrue(json.contains("\"PERSON\": 0.98"));
    assertFalse(json.contains("\"O\": 0.02"));
  }

  @Test
  public void entityMentionWithoutSentenceOffsetOmitsRelativeOffsets() throws Exception {
    Annotation annotation = new Annotation("Ada");
    CoreMap sentence = new ArrayCoreMap();
    CoreMap mention = new ArrayCoreMap();
    mention.set(CoreAnnotations.TokenBeginAnnotation.class, 7);
    mention.set(CoreAnnotations.TokenEndAnnotation.class, 8);
    mention.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, Collections.emptyMap());
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    String json = JSONOutputter.jsonPrint(annotation);

    assertTrue(json.contains("\"docTokenBegin\": 7"));
    assertFalse(json.contains("\"tokenBegin\""));
    assertFalse(json.contains("nerConfidences"));
  }

  @Test
  public void timexIsWrittenForBothTokenAndEntityMention() throws Exception {
    Annotation annotation = new Annotation("tomorrow");
    CoreMap sentence = new ArrayCoreMap();
    CoreLabel token = new CoreLabel();
    token.setIndex(1);
    token.setWord("tomorrow");
    token.set(TimeAnnotations.TimexAnnotation.class,
        new Timex("DATE", "2026-10-19", "NEXT_DAY", "t1", "tomorrow", -1, -1));
    CoreMap mention = new ArrayCoreMap();
    mention.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, Collections.emptyMap());
    mention.set(TimeAnnotations.TimexAnnotation.class, new Timex("DATE", "2026-10-19"));
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.MentionsAnnotation.class, Collections.singletonList(mention));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    String json = JSONOutputter.jsonPrint(annotation);

    assertTrue(json.contains("\"tid\": \"t1\""));
    assertTrue(json.contains("\"type\": \"DATE\""));
    assertTrue(json.contains("\"value\": \"2026-10-19\""));
    assertTrue(json.contains("\"altValue\": \"NEXT_DAY\""));
    assertFalse(json.contains("\"range\""));
  }

  @Test
  public void openIeAndKbpTriplesIncludeGlossesAndHalfOpenSpans() throws Exception {
    Annotation annotation = new Annotation("Ada wrote code");
    CoreMap sentence = new ArrayCoreMap();
    CoreLabel subject = new CoreLabel();
    subject.setWord("Ada");
    subject.setIndex(1);
    CoreLabel relation = new CoreLabel();
    relation.setWord("wrote");
    relation.setIndex(2);
    CoreLabel object = new CoreLabel();
    object.setWord("code");
    object.setIndex(3);
    RelationTriple triple = new RelationTriple(Collections.singletonList(subject),
        Collections.singletonList(relation), Collections.singletonList(object));
    sentence.set(NaturalLogicAnnotations.RelationTriplesAnnotation.class,
        Collections.singletonList(triple));
    sentence.set(CoreAnnotations.KBPTriplesAnnotation.class, Collections.singletonList(triple));
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    String json = JSONOutputter.jsonPrint(annotation);

    assertTrue(json.contains("\"openie\""));
    assertTrue(json.contains("\"kbp\""));
    assertTrue(json.contains("\"subject\": \"Ada\""));
    assertTrue(json.contains("\"subjectSpan\": [0, 1]"));
    assertTrue(json.contains("\"relationSpan\": [1, 2]"));
    assertTrue(json.contains("\"objectSpan\": [2, 3]"));
  }

  @Test
  public void quotationDefaultsUnknownSpeakersAndIncludesAttribution() throws Exception {
    Annotation annotation = new Annotation("\"Hi\"");
    CoreMap quote = new ArrayCoreMap();
    quote.set(CoreAnnotations.QuotationIndexAnnotation.class, 5);
    quote.set(CoreAnnotations.TextAnnotation.class, "Hi");
    quote.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    quote.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    quote.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    quote.set(CoreAnnotations.TokenEndAnnotation.class, 1);
    quote.set(CoreAnnotations.SentenceBeginAnnotation.class, 0);
    quote.set(CoreAnnotations.SentenceEndAnnotation.class, 1);
    quote.set(QuoteAttributionAnnotator.MentionAnnotation.class, "Ada");
    quote.set(QuoteAttributionAnnotator.MentionBeginAnnotation.class, 3);
    quote.set(QuoteAttributionAnnotator.MentionEndAnnotation.class, 4);
    quote.set(QuoteAttributionAnnotator.MentionTypeAnnotation.class, "name");
    quote.set(QuoteAttributionAnnotator.MentionSieveAnnotation.class, "closest");
    annotation.set(CoreAnnotations.QuotationsAnnotation.class, Collections.singletonList(quote));

    String json = JSONOutputter.jsonPrint(annotation);

    assertTrue(json.contains("\"id\": 5"));
    assertTrue(json.contains("\"mention\": \"Ada\""));
    assertTrue(json.contains("\"mentionSieve\": \"closest\""));
    assertTrue(json.contains("\"speaker\": \"Unknown\""));
    assertTrue(json.contains("\"canonicalSpeaker\": \"Unknown\""));
  }

  @Test
  public void quotationIncludesSpeakerAndCanonicalSpeakerDetails() throws Exception {
    Annotation annotation = new Annotation("\"Hi\"");
    CoreMap quote = new ArrayCoreMap();
    quote.set(CoreAnnotations.QuotationIndexAnnotation.class, 1);
    quote.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    quote.set(QuoteAttributionAnnotator.SpeakerAnnotation.class, "Ada");
    quote.set(QuoteAttributionAnnotator.SpeakerSieveAnnotation.class, "vocative");
    quote.set(QuoteAttributionAnnotator.CanonicalMentionAnnotation.class, "Ada Lovelace");
    quote.set(QuoteAttributionAnnotator.CanonicalMentionBeginAnnotation.class, 8);
    quote.set(QuoteAttributionAnnotator.CanonicalMentionEndAnnotation.class, 10);
    annotation.set(CoreAnnotations.QuotationsAnnotation.class, Collections.singletonList(quote));

    String json = JSONOutputter.jsonPrint(annotation);

    assertTrue(json.contains("\"speaker\": \"Ada\""));
    assertTrue(json.contains("\"speakerSieve\": \"vocative\""));
    assertTrue(json.contains("\"canonicalSpeaker\": \"Ada Lovelace\""));
    assertTrue(json.contains("\"canonicalMentionBegin\": 8"));
  }

  @Test
  public void sectionsContainOptionalMetadataAndSentenceIndexes() throws Exception {
    Annotation annotation = new Annotation("Section");
    CoreMap firstSentence = new ArrayCoreMap();
    firstSentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 2);
    CoreMap secondSentence = new ArrayCoreMap();
    secondSentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 3);
    CoreMap section = new ArrayCoreMap();
    section.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
    section.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 40);
    section.set(CoreAnnotations.AuthorAnnotation.class, "editor");
    section.set(CoreAnnotations.SectionDateAnnotation.class, "2026-10-18");
    section.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(firstSentence, secondSentence));
    annotation.set(CoreAnnotations.SectionsAnnotation.class, Collections.singletonList(section));

    String json = JSONOutputter.jsonPrint(annotation);

    assertTrue(json.contains("\"charBegin\": 10"));
    assertTrue(json.contains("\"charEnd\": 40"));
    assertTrue(json.contains("\"author\": \"editor\""));
    assertTrue(json.contains("\"dateTime\": \"2026-10-18\""));
    assertTrue(json.contains("\"index\": 2"));
    assertTrue(json.contains("\"index\": 3"));
  }

  @Test
  public void coreferenceChainSerializesMentionAttributesAndRepresentativeFlag() throws Exception {
    Annotation annotation = new Annotation("Ada said she wrote it");
    CorefChain.CorefMention mention = new CorefChain.CorefMention(
        Dictionaries.MentionType.PROPER, Dictionaries.Number.SINGULAR,
        Dictionaries.Gender.FEMALE, Dictionaries.Animacy.ANIMATE,
        1, 2, 1, 42, 7, 1, new IntPair(1, 1), "Ada");
    Map<IntPair, java.util.Set<CorefChain.CorefMention>> mentionMap = new LinkedHashMap<>();
    mentionMap.put(new IntPair(1, 1), Collections.singleton(mention));
    CorefChain chain = new CorefChain(42, mentionMap, mention);
    Map<Integer, CorefChain> chains = new LinkedHashMap<>();
    chains.put(42, chain);
    annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, chains);

    String json = JSONOutputter.jsonPrint(annotation);

    assertTrue(json.contains("\"42\""));
    assertTrue(json.contains("\"id\": 7"));
    assertTrue(json.contains("\"text\": \"Ada\""));
    assertTrue(json.contains("\"type\": \"PROPER\""));
    assertTrue(json.contains("\"gender\": \"FEMALE\""));
    assertTrue(json.contains("\"position\": ["));
    assertTrue(json.contains("\"isRepresentativeMention\": true"));
  }

  @Test
  public void outputStreamAndInstanceEntryPointsProduceEquivalentJson() throws Exception {
    Annotation annotation = new Annotation("x");
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "same");
    ByteArrayOutputStream staticTarget = new ByteArrayOutputStream();
    ByteArrayOutputStream instanceTarget = new ByteArrayOutputStream();

    JSONOutputter.jsonPrint(annotation, staticTarget);
    new JSONOutputter().print(annotation, instanceTarget);

    assertEquals(staticTarget.toString(StandardCharsets.UTF_8.name()),
        instanceTarget.toString(StandardCharsets.UTF_8.name()));
    assertEquals(JSONOutputter.jsonPrint(annotation),
        staticTarget.toString(StandardCharsets.UTF_8.name()));
  }

  @Test
  public void pipelineEntryPointUsesPipelineOutputProperties() throws Exception {
    Annotation annotation = new Annotation("shown");
    Properties properties = new Properties();
    properties.setProperty("annotators", "");
    properties.setProperty("output.includeText", "true");
    properties.setProperty("output.prettyPrint", "false");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(properties, false);
    ByteArrayOutputStream target = new ByteArrayOutputStream();

    JSONOutputter.jsonPrint(annotation, target, pipeline);

    assertEquals("{\"text\":\"shown\"}", target.toString(StandardCharsets.UTF_8.name()));
  }

  @Test
  public void configuredEncodingIsUsedForOutputBytes() throws Exception {
    Annotation annotation = new Annotation("ignored");
    annotation.set(CoreAnnotations.AuthorAnnotation.class, "René");
    Properties properties = new Properties();
    properties.setProperty("encoding", "UTF-16LE");
    properties.setProperty("output.prettyPrint", "false");
    ByteArrayOutputStream target = new ByteArrayOutputStream();

    JSONOutputter.jsonPrint(annotation, target, new AnnotationOutputter.Options(properties));

    assertEquals("{\"author\":\"René\"}", target.toString("UTF-16LE"));
  }

  @Test
  public void jsonWriterSkipsNullKeysAndValuesAndEscapesKeys() {
    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set(null, "ignored");
      writer.set("alsoIgnored", null);
      writer.set("quoted\"key", "tab\tnewline\nslash\\");
    });

    assertEquals("{\n  \"quoted\\\"key\": \"tab\\tnewline\\nslash\\\\\"\n}", json);
  }

  @Test
  public void jsonWriterSerializesCollectionsPairsSpansAndStreams() {
    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set("collection", Arrays.asList("a", 2, true));
      writer.set("pair", new Pair<>("left", 9));
      writer.set("span", new Span(3, 8));
      writer.set("stream", Stream.of("x", "y"));
    });

    assertTrue(json.contains("\"collection\": ["));
    assertTrue(json.contains("\"pair\": ["));
    assertTrue(json.contains("\"left\""));
    assertTrue(json.contains("\"span\": [3, 8]"));
    assertTrue(json.contains("\"stream\": ["));
  }

  @Test
  public void jsonWriterSerializesAllPrimitiveArrayKindsAndObjectArrays() {
    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set("ints", new int[]{1, 2});
      writer.set("shorts", new short[]{3});
      writer.set("bytes", new byte[]{4});
      writer.set("longs", new long[]{5L});
      writer.set("chars", new char[]{'q'});
      writer.set("floats", new float[]{1.25f});
      writer.set("doubles", new double[]{2.5d});
      writer.set("booleans", new boolean[]{true, false});
      writer.set("objects", new String[]{"z"});
    });

    assertTrue(json.contains("\"ints\": [\n    1,\n    2"));
    assertTrue(json.contains("\"shorts\": [\n    3"));
    assertTrue(json.contains("\"bytes\": [\n    4"));
    assertTrue(json.contains("\"longs\": [\n    5"));
    assertTrue(json.contains("\"chars\": [\n    q"));
    assertTrue(json.contains("\"floats\": [\n    1.25"));
    assertTrue(json.contains("\"doubles\": [\n    2.5"));
    assertTrue(json.contains("\"booleans\": [\n    true,\n    false"));
    assertTrue(json.contains("\"objects\": [\n    \"z\""));
  }

  @Test
  public void jsonWriterSerializesNumbersBooleansCharactersAndEnums() {
    String json = JSONOutputter.JSONWriter.objectToJSON(writer -> {
      writer.set("integer", Integer.valueOf(1));
      writer.set("short", Short.valueOf((short) 2));
      writer.set("byte", Byte.valueOf((byte) 3));
      writer.set("long", Long.valueOf(4L));
      writer.set("character", Character.valueOf('c'));
      writer.set("float", Float.valueOf(1.23456789f));
      writer.set("double", Double.valueOf(9.876543210987654d));
      writer.set("boolean", Boolean.FALSE);
      writer.set("enum", Dictionaries.Gender.NEUTRAL);
    });

    assertTrue(json.contains("\"integer\": 1"));
    assertTrue(json.contains("\"short\": 2"));
    assertTrue(json.contains("\"byte\": 3"));
    assertTrue(json.contains("\"long\": 4"));
    assertTrue(json.contains("\"character\": c"));
    assertTrue(json.contains("\"float\": 1.2345679"));
    assertTrue(json.contains("\"double\": 9.87654321098765"));
    assertTrue(json.contains("\"boolean\": false"));
    assertTrue(json.contains("\"enum\": \"NEUTRAL\""));
  }

  @Test
  public void jsonWriterSupportsNestedObjectCallbacksAndCompactFormatting() {
    StringWriter sink = new StringWriter();
    JSONOutputter.JSONWriter jsonWriter = new JSONOutputter.JSONWriter(
        new PrintWriter(sink), new AnnotationOutputter.Options(false));

    jsonWriter.object(writer -> writer.set("outer",
        (java.util.function.Consumer<JSONOutputter.Writer>) nested -> nested.set("answer", 42)));
    jsonWriter.flush();

    assertEquals("{\"outer\":{\"answer\":42}}", sink.toString());
  }

  @Test
  public void jsonWriterRejectsUnsupportedValues() {
    RuntimeException thrown = assertThrows(RuntimeException.class,
        () -> JSONOutputter.JSONWriter.objectToJSON(writer -> writer.set("bad", new Object())));

    assertTrue(thrown.getMessage().contains("Unknown object to serialize"));
  }
}
