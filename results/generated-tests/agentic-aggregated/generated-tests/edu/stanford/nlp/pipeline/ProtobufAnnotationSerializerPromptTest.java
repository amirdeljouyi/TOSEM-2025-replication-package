package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.international.Language;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.Timex;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.IntPair;
import edu.stanford.nlp.util.Pair;
import edu.stanford.nlp.util.RuntimeInterruptedException;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ProtobufAnnotationSerializerPromptTest {

  @Test
  public void tokenRoundTripPreservesCommonAndSpecialAnnotations() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    CoreLabel token = new CoreLabel();
    token.setWord("Amsterdam");
    token.setValue("Amsterdam-value");
    token.setTag("NNP");
    token.setLemma("Amsterdam");
    token.setNER("CITY");
    token.setOriginalText("AMSTERDAM");
    token.setBefore(" ");
    token.setAfter("!");
    token.setIndex(4);
    token.setBeginPosition(12);
    token.setEndPosition(21);
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "LOCATION");
    token.set(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class, "CITY");
    token.set(CoreAnnotations.CodepointOffsetBeginAnnotation.class, 11);
    token.set(CoreAnnotations.CodepointOffsetEndAnnotation.class, 20);
    token.set(CoreAnnotations.UtteranceAnnotation.class, 3);
    token.set(CoreAnnotations.SpeakerAnnotation.class, "speaker-1");
    token.set(CoreAnnotations.GenderAnnotation.class, "NEUTRAL");
    token.set(CoreAnnotations.SpanAnnotation.class, new IntPair(2, 5));
    token.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel restored = serializer.fromProto(serializer.toProto(token));

    assertEquals("Amsterdam", restored.word());
    assertEquals("Amsterdam-value", restored.value());
    assertEquals("NNP", restored.tag());
    assertEquals("Amsterdam", restored.lemma());
    assertEquals("CITY", restored.ner());
    assertEquals("AMSTERDAM", restored.originalText());
    assertEquals(" ", restored.before());
    assertEquals("!", restored.after());
    assertEquals(4, restored.index());
    assertEquals(12, restored.beginPosition());
    assertEquals(21, restored.endPosition());
    assertEquals("LOCATION", restored.get(CoreAnnotations.CoarseNamedEntityTagAnnotation.class));
    assertEquals("CITY", restored.get(CoreAnnotations.FineGrainedNamedEntityTagAnnotation.class));
    assertEquals(Integer.valueOf(11), restored.get(CoreAnnotations.CodepointOffsetBeginAnnotation.class));
    assertEquals(Integer.valueOf(20), restored.get(CoreAnnotations.CodepointOffsetEndAnnotation.class));
    assertEquals(Integer.valueOf(3), restored.get(CoreAnnotations.UtteranceAnnotation.class));
    assertEquals("speaker-1", restored.get(CoreAnnotations.SpeakerAnnotation.class));
    assertEquals("NEUTRAL", restored.get(CoreAnnotations.GenderAnnotation.class));
    assertEquals(new IntPair(2, 5), restored.get(CoreAnnotations.SpanAnnotation.class));
    assertEquals(Boolean.FALSE, restored.get(CoreAnnotations.IsNewlineAnnotation.class));
  }

  @Test
  public void tokenRoundTripPreservesEmptyNerProbabilityMap() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    CoreLabel token = new CoreLabel();
    token.setWord("none");
    token.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, new HashMap<String, Double>());

    CoreNLPProtos.Token proto = serializer.toProto(token);
    CoreLabel restored = serializer.fromProto(proto);

    assertEquals(1, proto.getNerLabelProbsCount());
    assertEquals("empty", proto.getNerLabelProbs(0));
    assertNotNull(restored.get(CoreAnnotations.NamedEntityTagProbsAnnotation.class));
    assertTrue(restored.get(CoreAnnotations.NamedEntityTagProbsAnnotation.class).isEmpty());
  }

  @Test
  public void tokenRoundTripPreservesPopulatedNerProbabilityMap() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    CoreLabel token = new CoreLabel();
    Map<String, Double> probabilities = new LinkedHashMap<>();
    probabilities.put("PERSON", 0.875);
    probabilities.put("O", 0.125);
    token.setWord("Ada");
    token.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, probabilities);

    CoreLabel restored = serializer.fromProto(serializer.toProto(token));

    assertEquals(Double.valueOf(0.875), restored.get(CoreAnnotations.NamedEntityTagProbsAnnotation.class).get("PERSON"));
    assertEquals(Double.valueOf(0.125), restored.get(CoreAnnotations.NamedEntityTagProbsAnnotation.class).get("O"));
  }

  @Test(expected = ProtobufAnnotationSerializer.LossySerializationException.class)
  public void defaultSerializerRejectsAnUnsupportedTokenAnnotation() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    CoreLabel token = new CoreLabel();
    token.setWord("shape");
    token.set(CoreAnnotations.ShapeAnnotation.class, "xxxx");

    serializer.toProto(token);
  }

  @Test
  public void lossySerializerDropsAnUnsupportedTokenAnnotation() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);
    CoreLabel token = new CoreLabel();
    token.setWord("shape");
    token.set(CoreAnnotations.ShapeAnnotation.class, "xxxx");

    CoreLabel restored = serializer.fromProto(serializer.toProto(token));

    assertEquals("shape", restored.word());
    assertNull(restored.get(CoreAnnotations.ShapeAnnotation.class));
  }

  @Test
  public void explicitSkipSetOmitsEvenASupportedTokenAnnotation() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    CoreLabel token = new CoreLabel();
    token.setWord("runs");
    token.setTag("VBZ");

    CoreNLPProtos.Token proto = serializer.toProto(
        token, Collections.<Class<?>>singleton(CoreAnnotations.PartOfSpeechAnnotation.class));

    assertEquals("runs", proto.getWord());
    assertFalse(proto.hasPos());
    assertNull(serializer.fromProto(proto).tag());
  }

  @Test
  public void sentenceRoundTripPreservesTokensOffsetsAndMetadata() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    CoreLabel first = new CoreLabel();
    first.setWord("Ada");
    first.setOriginalText("Ada");
    first.setBefore("");
    first.setAfter(" ");
    first.setIndex(7);
    first.setBeginPosition(0);
    first.setEndPosition(3);
    CoreLabel second = new CoreLabel();
    second.setWord("codes");
    second.setOriginalText("codes");
    second.setBefore(" ");
    second.setAfter(".");
    second.setIndex(8);
    second.setBeginPosition(4);
    second.setEndPosition(9);
    ArrayCoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TextAnnotation.class, "Ada codes");
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokenEndAnnotation.class, 2);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 5);
    sentence.set(CoreAnnotations.SentenceIDAnnotation.class, "s-5");
    sentence.set(CoreAnnotations.SpeakerAnnotation.class, "narrator");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(first, second));

    CoreNLPProtos.Sentence proto = serializer.toProto(sentence);
    CoreMap restored = serializer.fromProto(proto);
    List<CoreLabel> restoredTokens = restored.get(CoreAnnotations.TokensAnnotation.class);

    assertEquals("Ada codes", restored.get(CoreAnnotations.TextAnnotation.class));
    assertEquals(Integer.valueOf(0), restored.get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals(Integer.valueOf(2), restored.get(CoreAnnotations.TokenEndAnnotation.class));
    assertEquals(Integer.valueOf(5), restored.get(CoreAnnotations.SentenceIndexAnnotation.class));
    assertEquals("s-5", proto.getSentenceID());
    assertNull(restored.get(CoreAnnotations.SentenceIDAnnotation.class));
    assertEquals("narrator", restored.get(CoreAnnotations.SpeakerAnnotation.class));
    assertEquals(2, restoredTokens.size());
    assertEquals("Ada", restoredTokens.get(0).word());
    assertEquals(7, restoredTokens.get(0).index());
    assertEquals("codes", restoredTokens.get(1).word());
    assertEquals(8, restoredTokens.get(1).index());
  }

  @Test(expected = IllegalArgumentException.class)
  public void sentenceSerializationRejectsACoreLabelMasqueradingAsSentence() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    token.set(CoreAnnotations.TokenEndAnnotation.class, 1);

    serializer.toProto((CoreMap) token);
  }

  @Test
  public void documentRoundTripReusesDocumentTokensAndPopulatesIndexes() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    CoreLabel first = new CoreLabel();
    first.setWord("One");
    first.setOriginalText("One");
    first.setBefore("");
    first.setAfter(" ");
    first.setBeginPosition(0);
    first.setEndPosition(3);
    first.setIndex(1);
    CoreLabel second = new CoreLabel();
    second.setWord("test");
    second.setOriginalText("test");
    second.setBefore(" ");
    second.setAfter("");
    second.setBeginPosition(4);
    second.setEndPosition(8);
    second.setIndex(2);
    ArrayCoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TextAnnotation.class, "One test");
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokenEndAnnotation.class, 2);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(first, second));
    Annotation document = new Annotation("One test");
    document.set(CoreAnnotations.DocIDAnnotation.class, "doc-9");
    document.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(first, second));
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.<CoreMap>singletonList(sentence));

    Annotation restored = serializer.fromProto(serializer.toProto(document));
    List<CoreLabel> documentTokens = restored.get(CoreAnnotations.TokensAnnotation.class);
    CoreMap restoredSentence = restored.get(CoreAnnotations.SentencesAnnotation.class).get(0);

    assertEquals("One test", restored.get(CoreAnnotations.TextAnnotation.class));
    assertEquals("doc-9", restored.get(CoreAnnotations.DocIDAnnotation.class));
    assertEquals(2, documentTokens.size());
    assertSame(documentTokens.get(0), restoredSentence.get(CoreAnnotations.TokensAnnotation.class).get(0));
    assertSame(documentTokens.get(1), restoredSentence.get(CoreAnnotations.TokensAnnotation.class).get(1));
    assertEquals(1, documentTokens.get(0).index());
    assertEquals(2, documentTokens.get(1).index());
    assertEquals(0, documentTokens.get(0).sentIndex());
    assertEquals("doc-9", documentTokens.get(1).docID());
  }

  @Test
  public void writeAndReadSupportMultipleDelimitedDocumentsInOneStream() throws Exception {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    Annotation first = new Annotation("first");
    first.set(CoreAnnotations.DocIDAnnotation.class, "d1");
    Annotation second = new Annotation("second");
    second.set(CoreAnnotations.DocIDAnnotation.class, "d2");
    ByteArrayOutputStream output = new ByteArrayOutputStream();

    assertSame(output, serializer.write(first, output));
    serializer.write(second, output);
    ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
    Pair<Annotation, java.io.InputStream> firstRead = serializer.read(input);
    Pair<Annotation, java.io.InputStream> secondRead = serializer.read(firstRead.second());

    assertSame(input, firstRead.second());
    assertSame(input, secondRead.second());
    assertEquals("first", firstRead.first().get(CoreAnnotations.TextAnnotation.class));
    assertEquals("d1", firstRead.first().get(CoreAnnotations.DocIDAnnotation.class));
    assertEquals("second", secondRead.first().get(CoreAnnotations.TextAnnotation.class));
    assertEquals("d2", secondRead.first().get(CoreAnnotations.DocIDAnnotation.class));
    assertEquals(0, input.available());
  }

  @Test
  public void readUndelimitedReadsRawProtocolBufferFile() throws Exception {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    Annotation document = new Annotation("raw document");
    document.set(CoreAnnotations.DocDateAnnotation.class, "2026-10-18");
    Path path = Files.createTempFile("protobuf-annotation-raw", ".bin");
    Files.write(path, serializer.toProto(document).toByteArray());

    Annotation restored = serializer.readUndelimited(path.toFile());

    assertEquals("raw document", restored.get(CoreAnnotations.TextAnnotation.class));
    assertEquals("2026-10-18", restored.get(CoreAnnotations.DocDateAnnotation.class));
    Files.delete(path);
  }

  @Test
  public void readUndelimitedFallsBackToDelimitedProtocolBufferFile() throws Exception {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    Annotation document = new Annotation("framed document");
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    serializer.write(document, output);
    Path path = Files.createTempFile("protobuf-annotation-framed", ".bin");
    Files.write(path, output.toByteArray());

    Annotation restored = serializer.readUndelimited(path.toFile());

    assertEquals("framed document", restored.get(CoreAnnotations.TextAnnotation.class));
    Files.delete(path);
  }

  @Test
  public void parseTreeRoundTripPreservesStructureScoresAndSpans() {
    Tree tree = Tree.valueOf("(ROOT (S (NP (NNP Ada)) (VP (VBZ codes))))");
    tree.setScore(0.75);
    tree.setSpans();

    CoreNLPProtos.ParseTree proto = ProtobufAnnotationSerializer.toProto(tree);
    Tree restored = ProtobufAnnotationSerializer.fromProto(proto);

    assertEquals(tree.toString(), restored.toString());
    assertEquals(0.75, restored.score(), 0.0);
    assertEquals(new IntPair(0, 1), restored.getSpan());
    assertTrue(proto.hasYieldBeginIndex());
    assertTrue(proto.hasYieldEndIndex());
  }

  @Test
  public void parseTreeRoundTripCanAttachCallerProvidedLeafTokens() {
    Tree tree = Tree.valueOf("(S (NN Alice) (VBZ runs))");
    CoreLabel alice = new CoreLabel();
    alice.setWord("Alice");
    CoreLabel runs = new CoreLabel();
    runs.setWord("runs");

    Tree restored = ProtobufAnnotationSerializer.fromProto(
        ProtobufAnnotationSerializer.toProto(tree), Arrays.asList(alice, runs));

    assertSame(alice, restored.getLeaves().get(0).label());
    assertSame(runs, restored.getLeaves().get(1).label());
  }

  @Test
  public void flattenedTreeRoundTripPreservesADeepTreeAndScores() {
    Tree tree = Tree.valueOf("(ROOT (A (B (C leaf))) (D other))");
    tree.setScore(2.5);

    CoreNLPProtos.FlattenedParseTree proto = ProtobufAnnotationSerializer.toFlattenedTree(tree);
    Tree restored = ProtobufAnnotationSerializer.fromProto(proto);

    assertEquals(tree.toString(), restored.toString());
    assertEquals(2.5, restored.score(), 0.0);
    assertTrue(proto.getNodesCount() > tree.size());
  }

  @Test
  public void emptyFlattenedTreeDeserializesToNull() {
    assertNull(ProtobufAnnotationSerializer.fromProto(CoreNLPProtos.FlattenedParseTree.getDefaultInstance()));
  }

  @Test(expected = IllegalArgumentException.class)
  public void malformedFlattenedTreeStartingWithLabelIsRejected() {
    CoreNLPProtos.FlattenedParseTree proto = CoreNLPProtos.FlattenedParseTree.newBuilder()
        .addNodes(CoreNLPProtos.FlattenedParseTree.Node.newBuilder().setValue("ROOT").build())
        .build();

    ProtobufAnnotationSerializer.fromProto(proto);
  }

  @Test(expected = IllegalArgumentException.class)
  public void unfinishedFlattenedTreeIsRejected() {
    CoreNLPProtos.FlattenedParseTree proto = CoreNLPProtos.FlattenedParseTree.newBuilder()
        .addNodes(CoreNLPProtos.FlattenedParseTree.Node.newBuilder().setOpenNode(true).build())
        .addNodes(CoreNLPProtos.FlattenedParseTree.Node.newBuilder().setValue("ROOT").build())
        .build();

    ProtobufAnnotationSerializer.fromProto(proto);
  }

  @Test
  public void stringAndIntegerMapsRoundTripIncludingEmptyValues() {
    Map<String, String> strings = new LinkedHashMap<>();
    strings.put("first", "one");
    strings.put("empty", "");
    Map<Integer, String> integers = new LinkedHashMap<>();
    integers.put(-2, "negative");
    integers.put(0, "zero");

    Map<String, String> restoredStrings = ProtobufAnnotationSerializer.fromProto(
        ProtobufAnnotationSerializer.toMapStringStringProto(strings));
    Map<Integer, String> restoredIntegers = ProtobufAnnotationSerializer.fromProto(
        ProtobufAnnotationSerializer.toMapIntStringProto(integers));

    assertEquals(strings, restoredStrings);
    assertEquals(integers, restoredIntegers);
    assertEquals(Arrays.asList("first", "empty"), Arrays.asList(restoredStrings.keySet().toArray()));
  }

  @Test
  public void duplicateStringMapKeysUseTheLastSerializedValue() {
    CoreNLPProtos.MapStringString proto = CoreNLPProtos.MapStringString.newBuilder()
        .addKey("duplicate").addValue("old")
        .addKey("duplicate").addValue("new")
        .build();

    Map<String, String> restored = ProtobufAnnotationSerializer.fromProto(proto);

    assertEquals(1, restored.size());
    assertEquals("new", restored.get("duplicate"));
  }

  @Test
  public void supportedLanguagesRoundTripAcrossBothConversionMethods() {
    assertEquals(Language.Arabic, ProtobufAnnotationSerializer.fromProto(ProtobufAnnotationSerializer.toProto(Language.Arabic)));
    assertEquals(Language.Chinese, ProtobufAnnotationSerializer.fromProto(ProtobufAnnotationSerializer.toProto(Language.Chinese)));
    assertEquals(Language.English, ProtobufAnnotationSerializer.fromProto(ProtobufAnnotationSerializer.toProto(Language.English)));
    assertEquals(Language.UniversalEnglish, ProtobufAnnotationSerializer.fromProto(ProtobufAnnotationSerializer.toProto(Language.UniversalEnglish)));
    assertEquals(Language.French, ProtobufAnnotationSerializer.fromProto(ProtobufAnnotationSerializer.toProto(Language.French)));
    assertEquals(Language.Unknown, ProtobufAnnotationSerializer.fromProto(ProtobufAnnotationSerializer.toProto(Language.Unknown)));
    assertEquals(Language.Any, ProtobufAnnotationSerializer.fromProto(ProtobufAnnotationSerializer.toProto(Language.Any)));
  }

  @Test(expected = IllegalStateException.class)
  public void unsupportedLanguageIsRejected() {
    ProtobufAnnotationSerializer.toProto(Language.Italian);
  }

  @Test
  public void quoteConversionCopiesOffsetsAndAttributionFields() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    ArrayCoreMap quote = new ArrayCoreMap();
    quote.set(CoreAnnotations.TextAnnotation.class, "\"Hello\"");
    quote.set(CoreAnnotations.DocIDAnnotation.class, "novel");
    quote.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
    quote.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 17);
    quote.set(CoreAnnotations.SentenceBeginAnnotation.class, 2);
    quote.set(CoreAnnotations.SentenceEndAnnotation.class, 3);
    quote.set(CoreAnnotations.TokenBeginAnnotation.class, 4);
    quote.set(CoreAnnotations.TokenEndAnnotation.class, 5);
    quote.set(CoreAnnotations.QuotationIndexAnnotation.class, 1);
    quote.set(CoreAnnotations.AuthorAnnotation.class, "Ada");
    quote.set(QuoteAttributionAnnotator.MentionAnnotation.class, "Ada Lovelace");
    quote.set(QuoteAttributionAnnotator.SpeakerAnnotation.class, "Ada");

    CoreNLPProtos.Quote proto = serializer.toProtoQuote(quote);

    assertEquals("\"Hello\"", proto.getText());
    assertEquals("novel", proto.getDocid());
    assertEquals(10, proto.getBegin());
    assertEquals(17, proto.getEnd());
    assertEquals(2, proto.getSentenceBegin());
    assertEquals(3, proto.getSentenceEnd());
    assertEquals(4, proto.getTokenBegin());
    assertEquals(5, proto.getTokenEnd());
    assertEquals(1, proto.getIndex());
    assertEquals("Ada", proto.getAuthor());
    assertEquals("Ada Lovelace", proto.getMention());
    assertEquals("Ada", proto.getSpeaker());
  }

  @Test
  public void entityMentionConversionCopiesOptionalMetadataAndTimex() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    ArrayCoreMap mention = new ArrayCoreMap();
    mention.set(CoreAnnotations.SentenceIndexAnnotation.class, 3);
    mention.set(CoreAnnotations.TokenBeginAnnotation.class, 6);
    mention.set(CoreAnnotations.TokenEndAnnotation.class, 8);
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");
    mention.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "2026-10-18");
    mention.set(CoreAnnotations.EntityTypeAnnotation.class, "TEMPORAL");
    mention.set(CoreAnnotations.WikipediaEntityAnnotation.class, "October_18");
    mention.set(CoreAnnotations.GenderAnnotation.class, "UNKNOWN");
    mention.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 12);
    mention.set(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class, 9);
    mention.set(CoreAnnotations.TextAnnotation.class, "October 18");
    mention.set(TimeAnnotations.TimexAnnotation.class, new Timex("DATE", "2026-10-18"));

    CoreNLPProtos.NERMention proto = serializer.toProtoMention(mention);

    assertEquals(3, proto.getSentenceIndex());
    assertEquals(6, proto.getTokenStartInSentenceInclusive());
    assertEquals(8, proto.getTokenEndInSentenceExclusive());
    assertEquals("DATE", proto.getNer());
    assertEquals("2026-10-18", proto.getNormalizedNER());
    assertEquals("TEMPORAL", proto.getEntityType());
    assertEquals("October_18", proto.getWikipediaEntity());
    assertEquals("UNKNOWN", proto.getGender());
    assertEquals(12, proto.getEntityMentionIndex());
    assertEquals(9, proto.getCanonicalEntityMentionIndex());
    assertEquals("October 18", proto.getEntityMentionText());
    assertEquals("DATE", proto.getTimex().getType());
    assertEquals("2026-10-18", proto.getTimex().getValue());
  }

  @Test
  public void dependencyGraphRoundTripPreservesRootEdgeAndTokenData() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    CoreLabel governorLabel = new CoreLabel();
    governorLabel.setWord("codes");
    governorLabel.setIndex(2);
    governorLabel.setSentIndex(0);
    CoreLabel dependentLabel = new CoreLabel();
    dependentLabel.setWord("Ada");
    dependentLabel.setIndex(1);
    dependentLabel.setSentIndex(0);
    IndexedWord governor = new IndexedWord(governorLabel);
    IndexedWord dependent = new IndexedWord(dependentLabel);
    SemanticGraph graph = new SemanticGraph();
    graph.addRoot(governor);
    graph.addVertex(dependent);
    graph.addEdge(governor, dependent, GrammaticalRelation.DEPENDENT, 1.0, false);

    CoreNLPProtos.DependencyGraph proto = serializer.toProto(graph, true);
    SemanticGraph restored = ProtobufAnnotationSerializer.fromProto(
        proto, Arrays.asList(dependentLabel, governorLabel), "doc");

    assertEquals(2, proto.getNodeCount());
    assertEquals(2, proto.getTokenCount());
    assertEquals(1, proto.getEdgeCount());
    assertEquals(1, restored.edgeCount());
    assertEquals("codes", restored.getFirstRoot().word());
    assertEquals("Ada", restored.getChildList(restored.getFirstRoot()).get(0).word());
  }

  @Test(expected = RuntimeInterruptedException.class)
  public void flattenedTreeSerializationHonorsThreadInterruption() {
    Thread.currentThread().interrupt();
    ProtobufAnnotationSerializer.toFlattenedTree(Tree.valueOf("(ROOT leaf)"));
  }
}
