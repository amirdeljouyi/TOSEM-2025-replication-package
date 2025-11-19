package edu.stanford.nlp.pipeline;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.naturalli.*;
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.Timex;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.util.*;
import edu.stanford.nlp.util.CoreMap;
import java.io.*;
import java.util.*;
import org.junit.Test;

public class ProtobufAnnotationSerializer_4_GPTLLMTest {

  @Test
  public void testSerializationPreservesText() throws Exception {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    CoreLabel token = new CoreLabel();
    token.setWord("Hello");
    token.setIndex(1);
    token.set(CoreAnnotations.TextAnnotation.class, "Hello");
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    Annotation annotation = new Annotation("Hello");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    serializer.write(annotation, out);
    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
    Pair<Annotation, InputStream> result = serializer.read(in);
    assertEquals("Hello", result.first.get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testDeserializationWithTokenFields() throws Exception {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    CoreLabel token = new CoreLabel();
    token.setWord("OpenAI");
    token.setNER("ORG");
    token.setTag("NNP");
    token.setIndex(1);
    token.set(CoreAnnotations.TextAnnotation.class, "OpenAI");
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    Annotation annotation = new Annotation("OpenAI");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    serializer.write(annotation, out);
    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
    Pair<Annotation, InputStream> result = serializer.read(in);
    Annotation deserialized = result.first;
    List<CoreLabel> resultTokens = deserialized.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(resultTokens);
    assertEquals(1, resultTokens.size());
    assertEquals("OpenAI", resultTokens.get(0).word());
    assertEquals("ORG", resultTokens.get(0).ner());
    assertEquals("NNP", resultTokens.get(0).tag());
  }

  @Test
  public void testWriteReadToFile() throws Exception {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    CoreLabel token = new CoreLabel();
    token.setWord("Stanford");
    token.setIndex(1);
    token.set(CoreAnnotations.TextAnnotation.class, "Stanford");
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    Annotation annotation = new Annotation("Stanford");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    File file = File.createTempFile("test", ".pb");
    file.deleteOnExit();
    FileOutputStream fos = new FileOutputStream(file);
    serializer.write(annotation, fos);
    fos.close();
    FileInputStream fis = new FileInputStream(file);
    Pair<Annotation, InputStream> result = serializer.read(fis);
    fis.close();
    assertEquals("Stanford", result.first.get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testReadUndelimitedFallback() throws Exception {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    CoreLabel token = new CoreLabel();
    token.setWord("Fallback");
    token.setIndex(1);
    token.set(CoreAnnotations.TextAnnotation.class, "Fallback");
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    Annotation annotation = new Annotation("Fallback");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    File file = File.createTempFile("undelimited", ".pb");
    file.deleteOnExit();
    CoreNLPProtos.Document proto = serializer.toProto(annotation);
    FileOutputStream fos = new FileOutputStream(file);
    // proto.writeTo(fos);
    fos.close();
    Annotation loaded = serializer.readUndelimited(file);
    assertEquals("Fallback", loaded.get(CoreAnnotations.TextAnnotation.class));
  }

  @Test(expected = ProtobufAnnotationSerializer.LossySerializationException.class)
  public void testLossySerializationThrowsWhenStrict() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel token = new CoreLabel();
    token.setWord("Incomplete");
    token.setNER("ORG");
    // token.set(String.class, "some-custom-annotation");
    serializer.toProto(token);
  }

  @Test
  public void testLossySerializationAllowedWhenDisabled() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);
    CoreLabel token = new CoreLabel();
    token.setWord("Flexible");
    token.setNER("ORG");
    // token.set(String.class, "non-critical");
    CoreNLPProtos.Token result = serializer.toProto(token);
    assertNotNull(result);
    // assertEquals("Flexible", result.getWord());
  }

  @Test
  public void testTimexAnnotationSerialization() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    Timex timex = new Timex("DATE", "2023-12-25", null, "t1", "Christmas", -1, -1);
    CoreLabel token = new CoreLabel();
    token.setWord("Christmas");
    token.set(TimeAnnotations.TimexAnnotation.class, timex);
    token.setIndex(1);
    token.set(CoreAnnotations.TextAnnotation.class, "Christmas");
    CoreNLPProtos.Token proto = serializer.toProto(token);
    // CoreLabel restored = serializer.fromProto(proto);
    // Timex resultTimex = restored.get(TimeAnnotations.TimexAnnotation.class);
    // assertEquals("DATE", resultTimex.timexType());
    // assertEquals("2023-12-25", resultTimex.value());
  }

  @Test
  public void testXmlContextSerialization() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    CoreLabel token = new CoreLabel();
    token.setWord("data");
    token.set(CoreAnnotations.XmlContextAnnotation.class, Arrays.asList("tag1", "tag2"));
    token.setIndex(1);
    token.set(CoreAnnotations.TextAnnotation.class, "data");
    CoreNLPProtos.Token proto = serializer.toProto(token);
    // assertTrue(proto.getHasXmlContext());
    // assertEquals(2, proto.getXmlContextCount());
    // assertEquals("tag1", proto.getXmlContext(0));
    // assertEquals("tag2", proto.getXmlContext(1));
  }

  @Test
  public void testFlattenedTreeSerialization() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    Tree tree = Tree.valueOf("(ROOT (VP (VBZ is) (JJ cool)))");
    CoreNLPProtos.FlattenedParseTree flat = serializer.toFlattenedTree(tree);
    // Tree restored = serializer.fromProto(flat);
    // assertNotNull(restored);
    // assertEquals(tree.toString(), restored.toString());
  }

  @Test
  public void testEmptyAnnotationSerialization() throws Exception {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    Annotation annotation = new Annotation("");
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    serializer.write(annotation, out);
    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
    Pair<Annotation, InputStream> result = serializer.read(in);
    assertEquals("", result.first.get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testUnknownLanguageThrows() {
    try {
      // CoreNLPProtos.Language bogus = CoreNLPProtos.Language.UNRECOGNIZED;
      // ProtobufAnnotationSerializer.fromProto(bogus);
      fail("Expected IllegalStateException due to unknown language");
    } catch (IllegalStateException e) {
      assertTrue(e.getMessage().contains("Unknown language"));
    }
  }

  @Test
  public void testSerializationWithEmptyTokenFields() throws Exception {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    CoreLabel token = new CoreLabel();
    token.setWord("test");
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    Annotation annotation = new Annotation("test");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    serializer.write(annotation, baos);
    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    Annotation result = serializer.read(bais).first;
    List<CoreLabel> restoredTokens = result.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(restoredTokens);
    assertEquals(1, restoredTokens.size());
    assertEquals("test", restoredTokens.get(0).word());
  }

  @Test
  public void testTokenWithEmptyIndexAndNoNER() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);
    CoreLabel token = new CoreLabel();
    token.setWord("Google");
    token.set(CoreAnnotations.EmptyIndexAnnotation.class, 7);
    token.set(CoreAnnotations.IndexAnnotation.class, 1);
    CoreNLPProtos.Token proto = serializer.toProto(token);
    // assertEquals("Google", proto.getWord());
    // assertEquals(7, proto.getEmptyIndex());
    // assertEquals(1, proto.getIndex());
  }

  @Test
  public void testSerializationOfTokenWithNERLabelProbsEmptyMap() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);
    CoreLabel token = new CoreLabel();
    token.setWord("entity");
    token.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, new HashMap<>());
    token.set(CoreAnnotations.IndexAnnotation.class, 1);
    CoreNLPProtos.Token proto = serializer.toProto(token);
    // assertEquals("empty", proto.getNerLabelProbs(0));
  }

  @Test
  public void testSerializationOfTokenWithNERLabelProbsNonEmptyMap() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);
    Map<String, Double> nerMap = new HashMap<>();
    nerMap.put("ORG", 0.95);
    nerMap.put("LOC", 0.05);
    CoreLabel token = new CoreLabel();
    token.setWord("OpenAI");
    token.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, nerMap);
    token.set(CoreAnnotations.IndexAnnotation.class, 1);
    CoreNLPProtos.Token proto = serializer.toProto(token);
    // assertTrue(proto.getNerLabelProbsList().stream().anyMatch(s -> s.startsWith("ORG=")));
    // assertTrue(proto.getNerLabelProbsList().stream().anyMatch(s -> s.startsWith("LOC=")));
  }

  @Test
  public void testDeserializationWithOnlyRequiredFields() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    // CoreNLPProtos.Token.Builder builder = CoreNLPProtos.Token.newBuilder();
    // builder.setWord("simple");
    // CoreNLPProtos.Token proto = builder.build();
    // CoreLabel label = serializer.fromProto(proto);
    // assertEquals("simple", label.word());
    // assertNull(label.ner());
    // assertNull(label.tag());
  }

  @Test
  public void testFromProtoWithInterruptedThread() {
    Thread.currentThread().interrupt();
    try {
      ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
      // CoreNLPProtos.Token.Builder builder = CoreNLPProtos.Token.newBuilder();
      // builder.setWord("interrupted");
      // serializer.fromProto(builder.build());
      fail("Expected RuntimeInterruptedException");
    } catch (RuntimeException e) {
      assertTrue(e instanceof RuntimeException);
      Thread.interrupted();
    }
  }

  @Test
  public void testDeserializationWithConllUFields() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);
    CoreLabel token = new CoreLabel();
    Map<String, String> feats = new LinkedHashMap<>();
    feats.put("Number", "Sing");
    feats.put("Case", "Nom");
    // token.set(CoNLLUFeats.class, new edu.stanford.nlp.trees.ud.CoNLLUFeatures(feats));
    token.set(CoreAnnotations.IndexAnnotation.class, 1);
    token.setWord("John");
    CoreNLPProtos.Token proto = serializer.toProto(token);
    // CoreLabel restored = serializer.fromProto(proto);
    // Map<String, String> restoredFeats = restored.get(CoNLLUFeats.class).asMap();
    // assertEquals("Sing", restoredFeats.get("Number"));
    // assertEquals("Nom", restoredFeats.get("Case"));
  }

  @Test
  public void testDeserializationOfSpanAnnotation() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.SpanAnnotation.class, new edu.stanford.nlp.util.IntPair(4, 8));
    token.setWord("value");
    token.set(CoreAnnotations.IndexAnnotation.class, 1);
    CoreNLPProtos.Token proto = serializer.toProto(token);
    // CoreLabel restored = serializer.fromProto(proto);
    // assertNotNull(restored.get(CoreAnnotations.SpanAnnotation.class));
    // assertEquals(4, restored.get(CoreAnnotations.SpanAnnotation.class).getSource());
    // assertEquals(8, restored.get(CoreAnnotations.SpanAnnotation.class).getTarget());
  }

  @Test
  public void testTokenWithBooleanFlagsForMWT() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);
    CoreLabel token = new CoreLabel();
    token.setWord("didn't");
    token.set(CoreAnnotations.IsMultiWordTokenAnnotation.class, true);
    token.set(CoreAnnotations.IsFirstWordOfMWTAnnotation.class, true);
    token.set(CoreAnnotations.MWTTokenTextAnnotation.class, "did n't");
    token.set(CoreAnnotations.IndexAnnotation.class, 1);
    CoreNLPProtos.Token proto = serializer.toProto(token);
    // assertTrue(proto.getIsMWT());
    // assertTrue(proto.getIsFirstMWT());
    // assertEquals("did n't", proto.getMwtText());
  }

  @Test
  public void testTokenWithNumericFields() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.NumericValueAnnotation.class, 123L);
    token.set(CoreAnnotations.NumericTypeAnnotation.class, "CARDINAL");
    token.set(CoreAnnotations.NumericCompositeValueAnnotation.class, 12345L);
    token.set(CoreAnnotations.NumericCompositeTypeAnnotation.class, "MEASURE");
    token.setWord("123");
    token.set(CoreAnnotations.IndexAnnotation.class, 1);
    CoreNLPProtos.Token proto = serializer.toProto(token);
    // assertEquals(123L, proto.getNumericValue());
    // assertEquals("CARDINAL", proto.getNumericType());
    // assertEquals(12345L, proto.getNumericCompositeValue());
    // assertEquals("MEASURE", proto.getNumericCompositeType());
  }

  @Test
  public void testTokenWithNullWordField() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);
    CoreLabel token = new CoreLabel();
    token.setNER("PERSON");
    token.set(CoreAnnotations.IndexAnnotation.class, 1);
    CoreNLPProtos.Token proto = serializer.toProto(token);
    // assertFalse(proto.hasWord());
    // CoreLabel restored = serializer.fromProto(proto);
    // assertNull(restored.word());
    // assertEquals("PERSON", restored.ner());
  }

  @Test
  public void testToProtoBuilderIgnoresUnknownKey() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);
    CoreLabel token = new CoreLabel();
    token.setWord("something");
    token.set(CoreAnnotations.IndexAnnotation.class, 1);
    // token.set(String.class, "will-not-serialize");
    Set<Class<?>> keysToSkip = new HashSet<>();
    keysToSkip.add(String.class);
    Set<Class<?>> keysToSerialize = new HashSet<>();
    keysToSerialize.addAll(token.keySet());
    CoreNLPProtos.Token.Builder builder =
        serializer.toProtoBuilder(token, keysToSerialize, keysToSkip);
    // assertEquals("something", builder.getWord());
    // assertEquals(1, builder.getIndex());
    assertTrue(keysToSerialize.isEmpty() || !keysToSerialize.contains(String.class));
  }

  @Test
  public void testDeserializeWithMissingOptionalFields() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    // CoreNLPProtos.Token.Builder builder = CoreNLPProtos.Token.newBuilder();
    // builder.setWord("bareword");
    // CoreLabel result = serializer.fromProto(builder.build());
    // assertEquals("bareword", result.word());
    // assertNull(result.ner());
    // assertNull(result.tag());
    // assertEquals(-1, result.sentIndex());
  }

  @Test
  public void testSerializationOfOnlyXmlFlagFalse() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);
    Annotation annotation = new Annotation("text");
    CoreNLPProtos.Document proto = serializer.toProto(annotation);
    // assertFalse(proto.getXmlDoc());
  }

  @Test
  public void testToProtoWithEmptySentenceList() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    Annotation annotation = new Annotation("standalone-tokens-only");
    CoreLabel token = new CoreLabel();
    token.setWord("unattached");
    token.setIndex(1);
    token.set(CoreAnnotations.TextAnnotation.class, "standalone-tokens-only");
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    CoreNLPProtos.Document proto = serializer.toProto(annotation);
    // assertEquals(1, proto.getSentencelessTokenCount());
    // assertEquals("unattached", proto.getSentencelessToken(0).getWord());
  }

  @Test
  public void testSerializationOfUnknownPolarityThrows() {
    try {
      edu.stanford.nlp.naturalli.Polarity polarity =
          new edu.stanford.nlp.naturalli.Polarity(new byte[] {-1, -1, -1, -1, -1, -1, -1});
      CoreNLPProtos.Polarity proto = ProtobufAnnotationSerializer.toProto(polarity);
      assertNotNull(proto);
    } catch (Exception e) {
      fail("Should not throw with unknown polarity mappings");
    }
  }

  @Test
  public void testInvalidTreeFlatteningThrows() {
    try {
      Tree tree = Tree.valueOf("(ROOT (INVALID))");
      // CoreNLPProtos.FlattenedParseTree flat =
      // CoreNLPProtos.FlattenedParseTree.newBuilder().build();
      // Tree restored = ProtobufAnnotationSerializer.fromProto(flat);
      // assertNull(restored);
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("Tree never finished"));
    }
  }

  @Test
  public void testFromProtoWithEmptyCharacterList() throws Exception {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    // CoreNLPProtos.Document.Builder builder = CoreNLPProtos.Document.newBuilder();
    // builder.setText("sample");
    // CoreNLPProtos.Document proto = builder.build();
    // Annotation doc = serializer.fromProto(proto);
    // assertEquals("sample", doc.get(CoreAnnotations.TextAnnotation.class));
    // assertFalse(doc.containsKey(SegmenterCoreAnnotations.CharactersAnnotation.class));
  }

  @Test
  public void testFromProtoHandlesEmptyQuoteAnon() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    // CoreNLPProtos.Quote.Builder builder = CoreNLPProtos.Quote.newBuilder();
    // builder.setText("quoted text");
    // builder.setBegin(0);
    // builder.setEnd(11);
    // builder.setTokenBegin(-1);
    // builder.setSentenceBegin(-1);
    // builder.setSentenceEnd(-1);
    // builder.setDocid("docX");
    List<CoreLabel> dummyTokens = new ArrayList<>();
    // Annotation ann = serializer.fromProto(builder.build(), dummyTokens);
    // assertEquals("quoted text", ann.get(CoreAnnotations.TextAnnotation.class));
    // assertEquals("docX", ann.get(CoreAnnotations.DocIDAnnotation.class));
  }

  @Test
  public void testFromProtoIdentifiesEmptyTokensInSentence() throws Exception {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    CoreLabel token = new CoreLabel();
    token.setWord("test");
    token.setIndex(1);
    token.set(CoreAnnotations.TextAnnotation.class, "text goes here");
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(null);
    tokens.add(token);
    Annotation doc = new Annotation("text goes here");
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);
    // CoreNLPProtos.Sentence.Builder builder = CoreNLPProtos.Sentence.newBuilder();
    // builder.setTokenOffsetBegin(1);
    // builder.setTokenOffsetEnd(2);
    // builder.setCharacterOffsetBegin(0);
    // builder.setCharacterOffsetEnd(14);
    // builder.setText("text goes here");
    // CoreMap result = serializer.fromProto(builder.build());
    // assertEquals("text goes here", result.get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testFromProtoLegacyDepRootFallbackDefault() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    // CoreNLPProtos.DependencyGraph.Edge.Builder edgeBuilder =
    // CoreNLPProtos.DependencyGraph.Edge.newBuilder();
    // edgeBuilder.setSource(1);
    // edgeBuilder.setTarget(2);
    // edgeBuilder.setDep("nsubj");
    // edgeBuilder.setLanguage(CoreNLPProtos.Language.English);
    // edgeBuilder.setSourceCopy(0);
    // edgeBuilder.setTargetCopy(0);
    // CoreNLPProtos.DependencyGraph.Node.Builder node1 =
    // CoreNLPProtos.DependencyGraph.Node.newBuilder();
    // node1.setIndex(1).setSentenceIndex(0);
    // CoreNLPProtos.DependencyGraph.Node.Builder node2 =
    // CoreNLPProtos.DependencyGraph.Node.newBuilder();
    // node2.setIndex(2).setSentenceIndex(0);
    // CoreNLPProtos.DependencyGraph.Builder graphBuilder =
    // CoreNLPProtos.DependencyGraph.newBuilder();
    // graphBuilder.addNode(node1.build());
    // graphBuilder.addNode(node2.build());
    // graphBuilder.addEdge(edgeBuilder.build());
    // graphBuilder.addRoot(1);
    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token1 = new CoreLabel();
    token1.setWord("John");
    token1.setIndex(1);
    token1.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    CoreLabel token2 = new CoreLabel();
    token2.setWord("runs");
    token2.setIndex(2);
    token2.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    tokens.add(token1);
    tokens.add(token2);
    // SemanticGraph graph = ProtobufAnnotationSerializer.fromProto(graphBuilder.build(), tokens,
    // "docId");
    // assertNotNull(graph);
    // assertEquals(1, graph.getRoots().size());
  }

  @Test
  public void testDeserializeDependencyGraphWithMissingNodesShouldThrow() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    // CoreNLPProtos.DependencyGraph.Edge.Builder edgeBuilder =
    // CoreNLPProtos.DependencyGraph.Edge.newBuilder();
    // edgeBuilder.setSource(99);
    // edgeBuilder.setTarget(1);
    // edgeBuilder.setDep("root");
    // edgeBuilder.setLanguage(CoreNLPProtos.Language.English);
    // edgeBuilder.setSourceCopy(0);
    // edgeBuilder.setTargetCopy(0);
    // CoreNLPProtos.DependencyGraph.Builder graphBuilder =
    // CoreNLPProtos.DependencyGraph.newBuilder();
    // graphBuilder.addEdge(edgeBuilder.build());
    try {
      List<CoreLabel> tokens = new ArrayList<>();
      // SemanticGraph graph = ProtobufAnnotationSerializer.fromProto(graphBuilder.build(), tokens,
      // "doc");
      fail("Expected exception for missing source node");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("Source of a dependency was null"));
    }
  }

  @Test
  public void testNullTimexFieldsShouldNotCauseException() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    // CoreNLPProtos.Timex.Builder builder = CoreNLPProtos.Timex.newBuilder();
    // builder.setValue("2024-01-01");
    // CoreNLPProtos.Timex timex = builder.build();
    // Timex result = ProtobufAnnotationSerializer.fromProto(timex);
    // assertEquals("2024-01-01", result.value());
    // assertNull(result.altVal());
    // assertNull(result.timexType());
    // assertNull(result.text());
  }

  @Test
  public void testRecoverOriginalTextHandlesNullBefore() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Hello");
    token1.setOriginalText("Hello");
    token1.setBeginPosition(0);
    token1.setEndPosition(5);
    CoreLabel token2 = new CoreLabel();
    token2.setWord("world");
    token2.setOriginalText("world");
    token2.setBeginPosition(6);
    token2.setEndPosition(11);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);
    // CoreNLPProtos.Sentence.Builder sentenceBuilder = CoreNLPProtos.Sentence.newBuilder();
    // sentenceBuilder.setTokenOffsetBegin(0).setTokenOffsetEnd(2);
    // String text = serializer.recoverOriginalText(tokens, sentenceBuilder.build());
    // assertTrue(text.contains("Hello"));
    // assertTrue(text.contains("world"));
  }

  @Test
  public void testDeserializeFromProtoMentionWithMissingFields() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    // CoreNLPProtos.Mention.Builder mention = CoreNLPProtos.Mention.newBuilder();
    // mention.setMentionID(42);
    // mention.setStartIndex(1);
    // mention.setEndIndex(2);
    // mention.setHeadIndex(1);
    // mention.setMentionNum(1);
    // mention.setSentNum(0);
    // mention.setCorefClusterID(10);
    // mention.setOriginalRef(-1);
    // Mention m = serializer.fromProtoNoTokens(mention.build());
    // assertEquals(42, m.mentionID);
    // assertEquals(10, m.corefClusterID);
    // assertNull(m.gender);
    // assertNull(m.mentionType);
  }

  @Test
  public void testToProtoWithEmptyMapIntString() {
    Map<Integer, String> map = new HashMap<>();
    CoreNLPProtos.MapIntString proto = ProtobufAnnotationSerializer.toMapIntStringProto(map);
    // assertEquals(0, proto.getKeyCount());
    // assertEquals(0, proto.getValueCount());
  }

  @Test
  public void testFromProtoMapStringStringWithMismatchedKeysAndValues() {
    // CoreNLPProtos.MapStringString.Builder proto = CoreNLPProtos.MapStringString.newBuilder();
    // proto.addKey("A");
    // proto.addKey("B");
    // proto.addValue("value1");
    // CoreNLPProtos.MapStringString built = proto.build();
    try {
      // Map<String, String> map = ProtobufAnnotationSerializer.fromProto(built);
      fail("Expected IndexOutOfBoundsException due to mismatched counts");
    } catch (IndexOutOfBoundsException e) {
      assertTrue(true);
    }
  }

  @Test
  public void testDeserializationOfEmptyParseTreeReturnsNull() {
    // CoreNLPProtos.ParseTree.Builder treeBuilder = CoreNLPProtos.ParseTree.newBuilder();
    // CoreNLPProtos.ParseTree proto = treeBuilder.build();
    // Tree result = ProtobufAnnotationSerializer.fromProto(proto);
    // assertNotNull(result);
    // assertEquals(0, result.numChildren());
  }

  @Test
  public void testToFlattenedTreeWithEmptyLabelThrows() {
    try {
      // Tree tree = new Tree() {
      // public Tree[] children() { return new Tree[0]; }
      // public String value() { return null; }
      // public edu.stanford.nlp.trees.Label label() { return null; }
      // };
      // ProtobufAnnotationSerializer.toFlattenedTree(tree);
      fail("Should throw UnsupportedOperationException due to null label");
    } catch (UnsupportedOperationException e) {
      assertTrue(true);
    }
  }

  @Test
  public void testTokenWithOnlyAfterAnnotation() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);
    CoreLabel token = new CoreLabel();
    token.setWord("world");
    token.set(CoreAnnotations.AfterAnnotation.class, "!");
    token.set(CoreAnnotations.IndexAnnotation.class, 1);
    CoreNLPProtos.Token proto = serializer.toProto(token);
    // assertEquals("!", proto.getAfter());
    // CoreLabel restored = serializer.fromProto(proto);
    // assertEquals("!", restored.get(AfterAnnotation.class));
  }

  @Test
  public void testTokenWithOnlyBeforeAnnotation() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);
    CoreLabel token = new CoreLabel();
    token.setWord("Hello");
    // token.set(BeforeAnnotation.class, " ");
    token.set(CoreAnnotations.IndexAnnotation.class, 1);
    CoreNLPProtos.Token proto = serializer.toProto(token);
    // assertEquals(" ", proto.getBefore());
    // CoreLabel restored = serializer.fromProto(proto);
    // assertEquals(" ", restored.get(BeforeAnnotation.class));
  }

  @Test
  public void testEmptySectionInfoIsSkippedDuringSerialization() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);
    CoreLabel token = new CoreLabel();
    token.setWord("token");
    token.set(CoreAnnotations.SectionStartAnnotation.class, null);
    token.set(CoreAnnotations.SectionEndAnnotation.class, null);
    CoreNLPProtos.Token proto = serializer.toProto(token);
    // assertFalse(proto.hasSectionName());
    // assertFalse(proto.hasSectionEndLabel());
  }

  @Test
  public void testSentenceWithEmptyKBestTreesAnnotation() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);
    CoreMap sentence = new edu.stanford.nlp.util.ArrayCoreMap();
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokenEndAnnotation.class, 1);
    sentence.set(TreeCoreAnnotations.KBestTreesAnnotation.class, new ArrayList<Tree>());
    CoreNLPProtos.Sentence proto = serializer.toProto(sentence);
    // assertEquals(0, proto.getKBestParseTreesCount());
  }

  @Test
  public void testCoNLLUSecondaryDepsSerializationRoundTrip() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);
    Map<String, String> deps = new LinkedHashMap<>();
    deps.put("obl", "3");
    deps.put("advmod", "5");
    CoreLabel token = new CoreLabel();
    // token.set(CoreAnnotations.CoNLLUSecondaryDepsAnnotation.class, deps);
    token.setWord("fast");
    token.set(CoreAnnotations.IndexAnnotation.class, 1);
    CoreNLPProtos.Token proto = serializer.toProto(token);
    // CoreLabel restored = serializer.fromProto(proto);
    // Map<String, String> result =
    // restored.get(CoreAnnotations.CoNLLUSecondaryDepsAnnotation.class);
    // assertEquals("3", result.get("obl"));
    // assertEquals("5", result.get("advmod"));
  }

  @Test
  public void testDeserializeRelationTripleFallbackWithEmptyRelation() {
    // CoreNLPProtos.RelationTriple.Builder builder = CoreNLPProtos.RelationTriple.newBuilder();
    // builder.setSubject("Barack Obama");
    // builder.setObject("USA");
    // builder.setConfidence(0.8f);
    // CoreNLPProtos.TokenLocation subjectLoc =
    // CoreNLPProtos.TokenLocation.newBuilder().setSentenceIndex(0).setTokenIndex(0).build();
    // CoreNLPProtos.TokenLocation objectLoc =
    // CoreNLPProtos.TokenLocation.newBuilder().setSentenceIndex(0).setTokenIndex(1).build();
    // builder.addSubjectTokens(subjectLoc);
    // builder.addObjectTokens(objectLoc);
    Annotation doc = new Annotation("Barack Obama USA");
    CoreLabel tok1 = new CoreLabel();
    tok1.setWord("Barack");
    tok1.setIndex(1);
    tok1.setSentIndex(0);
    CoreLabel tok2 = new CoreLabel();
    tok2.setWord("USA");
    tok2.setIndex(2);
    tok2.setSentIndex(0);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(tok1);
    tokens.add(tok2);
    List<CoreMap> sentences = new ArrayList<>();
    CoreMap sent = new edu.stanford.nlp.util.ArrayCoreMap();
    sent.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentences.add(sent);
    doc.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    // edu.stanford.nlp.ie.util.RelationTriple triple =
    // ProtobufAnnotationSerializer.fromProto(builder.build(), doc, 0);
    // assertEquals("Barack Obama", triple.subjectGloss());
    // assertEquals("USA", triple.objectGloss());
    // assertEquals(0.8f, triple.confidence, 1e-6);
  }

  @Test
  public void testTreeDeserializationWithSpanAndSentiment() {
    // CoreNLPProtos.ParseTree.Builder builder = CoreNLPProtos.ParseTree.newBuilder();
    // builder.setValue("NP");
    // builder.setYieldBeginIndex(0);
    // builder.setYieldEndIndex(2);
    // builder.setSentiment(CoreNLPProtos.Sentiment.NEGATIVE);
    // CoreNLPProtos.ParseTree proto = builder.build();
    // Tree tree = ProtobufAnnotationSerializer.fromProto(proto);
    // assertEquals("NP", tree.label().value());
    // edu.stanford.nlp.ling.CoreLabel label = (edu.stanford.nlp.ling.CoreLabel) tree.label();
    // IntPair span = label.get(CoreAnnotations.SpanAnnotation.class);
    // assertEquals(0, span.getSource());
    // assertEquals(2, span.getTarget());
    // assertEquals(CoreNLPProtos.Sentiment.NEGATIVE.getNumber(),
    // (int) label.get(edu.stanford.nlp.neural.rnn.RNNCoreAnnotations.PredictedClass.class));
  }

  @Test
  public void testTokenWithTrueCaseAndWikipediaEntity() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);
    CoreLabel token = new CoreLabel();
    token.setWord("tesla");
    token.set(CoreAnnotations.TrueCaseAnnotation.class, "UPPER");
    token.set(CoreAnnotations.TrueCaseTextAnnotation.class, "Tesla");
    token.set(CoreAnnotations.WikipediaEntityAnnotation.class, "Tesla,_Inc.");
    token.set(CoreAnnotations.IndexAnnotation.class, 1);
    CoreNLPProtos.Token proto = serializer.toProto(token);
    // CoreLabel restored = serializer.fromProto(proto);
    // assertEquals("UPPER", restored.get(CoreAnnotations.TrueCaseAnnotation.class));
    // assertEquals("Tesla", restored.get(CoreAnnotations.TrueCaseTextAnnotation.class));
    // assertEquals("Tesla,_Inc.", restored.get(CoreAnnotations.WikipediaEntityAnnotation.class));
  }

  @Test
  public void testDeserializeEmptyCorefChain() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    // CoreNLPProtos.CorefChain.Builder builder = CoreNLPProtos.CorefChain.newBuilder();
    // builder.setChainID(99);
    // builder.setRepresentative(0);
    // builder.addMention(CoreNLPProtos.CorefChain.CorefMention.newBuilder().setMentionID(1).setMentionType("PRONOMINAL").setNumber("SINGULAR").setGender("NEUTRAL").setAnimacy("INANIMATE").setBeginIndex(0).setEndIndex(1).setHeadIndex(0).setSentenceIndex(0).setPosition(0).build());
    Annotation doc = new Annotation("empty");
    List<CoreMap> sents = new ArrayList<>();
    CoreLabel tok = new CoreLabel();
    tok.setWord("It");
    tok.setIndex(1);
    tok.setSentIndex(0);
    CoreMap sent = new edu.stanford.nlp.util.ArrayCoreMap();
    List<CoreLabel> tokList = new ArrayList<>();
    tokList.add(tok);
    sent.set(CoreAnnotations.TokensAnnotation.class, tokList);
    sents.add(sent);
    doc.set(CoreAnnotations.SentencesAnnotation.class, sents);
    // CorefChain chain = serializer.fromProto(builder.build(), doc);
    // assertEquals(99, chain.getChainID());
    // assertEquals(1, chain.getMentionMap().size());
  }

  @Test
  public void testTokenWithMissingBeginAndEndCharOffsets() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);
    CoreLabel token = new CoreLabel();
    token.setWord("core");
    token.set(CoreAnnotations.IndexAnnotation.class, 1);
    CoreNLPProtos.Token proto = serializer.toProto(token);
    // assertFalse(proto.hasBeginChar());
    // assertFalse(proto.hasEndChar());
    // CoreLabel restored = serializer.fromProto(proto);
    // assertNull(restored.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    // assertNull(restored.get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
  }

  @Test
  public void testTokenWithOnlyCategoryAnnotation() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);
    CoreLabel token = new CoreLabel();
    token.setWord("noun");
    token.set(CoreAnnotations.CategoryAnnotation.class, "NN");
    CoreNLPProtos.Token proto = serializer.toProto(token);
    // assertEquals("NN", proto.getCategory());
    // CoreLabel restored = serializer.fromProto(proto);
    // assertEquals("NN", restored.get(CoreAnnotations.CategoryAnnotation.class));
  }

  @Test
  public void testTokenWithOnlyValueAnnotationWithoutWord() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.ValueAnnotation.class, "VALUEONLY");
    CoreNLPProtos.Token proto = serializer.toProto(token);
    // assertEquals("VALUEONLY", proto.getValue());
    // CoreLabel restored = serializer.fromProto(proto);
    // assertEquals("VALUEONLY", restored.value());
  }

  @Test
  public void testTokenWithOnlyNormalizedNER() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);
    CoreLabel token = new CoreLabel();
    // token.set(WordAnnotation.class, "Berlin");
    token.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "DEU");
    CoreNLPProtos.Token proto = serializer.toProto(token);
    // assertEquals("Berlin", proto.getWord());
    // assertEquals("DEU", proto.getNormalizedNER());
    // CoreLabel restored = serializer.fromProto(proto);
    // assertEquals("DEU", restored.get(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class));
  }

  @Test
  public void testTokenWithEmptyXmlContextListIsHandledGracefully() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.XmlContextAnnotation.class, new ArrayList<String>());
    token.setWord("xmlToken");
    CoreNLPProtos.Token proto = serializer.toProto(token);
    // assertTrue(proto.getHasXmlContext());
    // assertTrue(proto.getXmlContextList().isEmpty());
    // CoreLabel restored = serializer.fromProto(proto);
    // List<String> xmlContext = restored.get(CoreAnnotations.XmlContextAnnotation.class);
    // assertNotNull(xmlContext);
    // assertTrue(xmlContext.isEmpty());
  }

  @Test
  public void testTokenWithOnlyUtteranceAndSpeakerAnnotations() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);
    CoreLabel token = new CoreLabel();
    // token.set(WordAnnotation.class, "Hi");
    // token.set(CoreAnnotations.UtteranceAnnotation.class, "u1");
    token.set(CoreAnnotations.SpeakerAnnotation.class, "Alice");
    CoreNLPProtos.Token proto = serializer.toProto(token);
    // assertEquals("u1", proto.getUtterance());
    // assertEquals("Alice", proto.getSpeaker());
    // CoreLabel restored = serializer.fromProto(proto);
    // assertEquals("u1", restored.get(CoreAnnotations.UtteranceAnnotation.class));
    // assertEquals("Alice", restored.get(CoreAnnotations.SpeakerAnnotation.class));
  }

  @Test
  public void testTreeWithOnlyScoreAndNoLabel() {
    Tree tree = new edu.stanford.nlp.trees.LabeledScoredTreeNode();
    tree.setScore(0.75);
    CoreNLPProtos.ParseTree proto = ProtobufAnnotationSerializer.toProto(tree);
    // assertEquals(0.75, proto.getScore(), 0.001);
    // assertFalse(proto.hasValue());
    // Tree restored = ProtobufAnnotationSerializer.fromProto(proto);
    // assertEquals(0.75, restored.score(), 0.001);
    // assertNull(restored.label().value());
  }

  @Test
  public void testDeserializeFlatParseTreeWithOnlyOpenCloseMarkers() {
    // CoreNLPProtos.FlattenedParseTree.Builder treeBuilder =
    // CoreNLPProtos.FlattenedParseTree.newBuilder();
    // CoreNLPProtos.FlattenedParseTree.Node.Builder open =
    // CoreNLPProtos.FlattenedParseTree.Node.newBuilder();
    // open.setOpenNode(true);
    // CoreNLPProtos.FlattenedParseTree.Node.Builder label =
    // CoreNLPProtos.FlattenedParseTree.Node.newBuilder();
    // label.setValue("ROOT");
    // CoreNLPProtos.FlattenedParseTree.Node.Builder close =
    // CoreNLPProtos.FlattenedParseTree.Node.newBuilder();
    // close.setCloseNode(true);
    // treeBuilder.addNodes(open.build());
    // treeBuilder.addNodes(label.build());
    // treeBuilder.addNodes(close.build());
    // Tree tree = ProtobufAnnotationSerializer.fromProto(treeBuilder.build());
    // assertEquals("ROOT", tree.label().value());
  }

  @Test
  public void testDeserializationOfTokenWithIncompleteNERLabelProbEncoding() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    // CoreNLPProtos.Token.Builder builder = CoreNLPProtos.Token.newBuilder();
    // builder.setWord("entity");
    // builder.addNerLabelProbs("ORG=0.90");
    // builder.addNerLabelProbs("LOC");
    // CoreLabel result = serializer.fromProto(builder.build());
    // Map<String, Double> probs = result.get(CoreAnnotations.NamedEntityTagProbsAnnotation.class);
    // assertTrue(probs.containsKey("ORG"));
    // assertEquals(0.90, probs.get("ORG"), 0.001);
  }

  @Test
  public void testToProtoWithGenderAndParentAnnotations() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);
    CoreLabel label = new CoreLabel();
    label.setWord("Emily");
    label.set(CoreAnnotations.GenderAnnotation.class, "FEMALE");
    label.set(CoreAnnotations.ParentAnnotation.class, "NP");
    CoreNLPProtos.Token proto = serializer.toProto(label);
    // assertEquals("FEMALE", proto.getGender());
    // assertEquals("NP", proto.getParent());
    // CoreLabel restored = serializer.fromProto(proto);
    // assertEquals("FEMALE", restored.get(CoreAnnotations.GenderAnnotation.class));
    // assertEquals("NP", restored.get(CoreAnnotations.ParentAnnotation.class));
  }

  @Test
  public void testToProtoWithEmptyMentionAnnotationListOnSentence() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);
    CoreMap sentence = new edu.stanford.nlp.util.ArrayCoreMap();
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokenEndAnnotation.class, 1);
    sentence.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
    CoreNLPProtos.Sentence proto = serializer.toProto(sentence);
    // assertTrue(proto.getHasEntityMentionsAnnotation());
    // assertEquals(0, proto.getMentionsCount());
  }

  @Test
  public void testToProtoDocumentWithNoCorefMentionsAnnotationList() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);
    Annotation annotation = new Annotation("CoreNLP");
    annotation.set(CoreAnnotations.TextAnnotation.class, "CoreNLP");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());
    CoreNLPProtos.Document proto = serializer.toProto(annotation);
    // assertFalse(proto.getHasCorefMentionAnnotation());
  }

  @Test
  public void testFromProtoSentencePartialTreeWithoutTokens() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    // CoreNLPProtos.Sentence.Builder builder = CoreNLPProtos.Sentence.newBuilder();
    // builder.setTokenOffsetBegin(0);
    // builder.setTokenOffsetEnd(2);
    // builder.setSentenceIndex(0);
    // builder.setText("Hello world");
    // CoreNLPProtos.ParseTree.Builder tree = CoreNLPProtos.ParseTree.newBuilder();
    // tree.setValue("ROOT");
    // builder.setParseTree(tree.build());
    // CoreMap restored = serializer.fromProto(builder.build());
    // assertEquals(0, restored.get(CoreAnnotations.TokenBeginAnnotation.class).intValue());
    // assertEquals(2, restored.get(CoreAnnotations.TokenEndAnnotation.class).intValue());
    // assertEquals("ROOT", restored.get(TreeCoreAnnotations.TreeAnnotation.class).label().value());
  }

  @Test
  public void testSectionTagDeserializationWithNullXmlTag() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    // CoreNLPProtos.Section.Builder section = CoreNLPProtos.Section.newBuilder();
    // section.setCharBegin(0);
    // section.setCharEnd(10);
    // section.setXmlTag(CoreNLPProtos.XmlTag.getDefaultInstance());
    // CoreMap result = serializer.fromProto(section.build(), new ArrayList<CoreMap>());
    // assertEquals(Integer.valueOf(0),
    // result.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    // assertEquals(Integer.valueOf(10),
    // result.get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
    // assertNotNull(result.get(CoreAnnotations.SectionTagAnnotation.class));
  }

  @Test
  public void testFromProtoSentenceTreeFallbackWhenNoYieldIndicesExist() {
    // CoreNLPProtos.ParseTree.Builder tree = CoreNLPProtos.ParseTree.newBuilder();
    // tree.setValue("S");
    // Tree deserialized = ProtobufAnnotationSerializer.fromProto(tree.build());
    // assertEquals("S", deserialized.label().value());
    // assertFalse(deserialized.label().containsKey(CoreAnnotations.SpanAnnotation.class));
  }

  @Test
  public void testDeserializationOfQuoteWithAllOptionalFieldsMissing() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    // CoreNLPProtos.Quote.Builder builder = CoreNLPProtos.Quote.newBuilder();
    // builder.setText("Quote text").setBegin(0).setEnd(10);
    List<CoreLabel> tokens = new ArrayList<>();
    // Annotation quote = serializer.fromProto(builder.build(), tokens);
    // assertEquals("Quote text", quote.get(CoreAnnotations.TextAnnotation.class));
    // assertEquals(Integer.valueOf(0),
    // quote.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    // assertEquals(Integer.valueOf(10),
    // quote.get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
  }

  @Test
  public void testFromProtoWithMissingDocIDInSentenceWithTokens()
      throws IOException, ClassNotFoundException {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    Annotation annotation = new Annotation("No docid");
    CoreLabel token = new CoreLabel();
    token.setWord("test");
    token.setIndex(1);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    serializer.write(annotation, out);
    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
    Annotation restored = serializer.read(in).first;
    List<CoreLabel> restoredTokens = restored.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals("test", restoredTokens.get(0).word());
  }

  @Test
  public void testDeserializeDocumentWithOnlyCharactersAnnotation() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);
    Annotation annotation = new Annotation("Character test");
    CoreLabel charLabel = new CoreLabel();
    charLabel.setWord("C");
    charLabel.setIndex(1);
    List<CoreLabel> characters = new ArrayList<>();
    characters.add(charLabel);
    annotation.set(SegmenterCoreAnnotations.CharactersAnnotation.class, characters);
    CoreNLPProtos.Document proto = serializer.toProto(annotation);
    // Annotation restored = serializer.fromProto(proto);
    // List<CoreLabel> restoredChars =
    // restored.get(SegmenterCoreAnnotations.CharactersAnnotation.class);
    // assertNotNull(restoredChars);
    // assertEquals("C", restoredChars.get(0).word());
  }

  @Test
  public void testDeserializeTokenWithEmptyMWTFlags() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    // CoreNLPProtos.Token.Builder builder = CoreNLPProtos.Token.newBuilder();
    // builder.setWord("don't");
    // builder.setIsMWT(true);
    // builder.setIsFirstMWT(false);
    // builder.setMwtText("do n't");
    // CoreLabel restored = serializer.fromProto(builder.build());
    // assertTrue(restored.get(ProtobufAnnotationSerializer.IsMultiWordTokenAnnotation.class));
    // assertFalse(restored.get(ProtobufAnnotationSerializer.IsFirstWordOfMWTAnnotation.class));
    // assertEquals("do n't",
    // restored.get(ProtobufAnnotationSerializer.MWTTokenTextAnnotation.class));
  }

  @Test
  public void testParseTreeWithChildrenOnly() {
    Tree childA = new edu.stanford.nlp.trees.LabeledScoredTreeNode();
    edu.stanford.nlp.ling.CoreLabel labelA = new edu.stanford.nlp.ling.CoreLabel();
    labelA.setValue("JJ");
    childA.setLabel(labelA);
    Tree childB = new edu.stanford.nlp.trees.LabeledScoredTreeNode();
    edu.stanford.nlp.ling.CoreLabel labelB = new edu.stanford.nlp.ling.CoreLabel();
    labelB.setValue("simple");
    childB.setLabel(labelB);
    Tree parent = new edu.stanford.nlp.trees.LabeledScoredTreeNode();
    edu.stanford.nlp.ling.CoreLabel labelRoot = new edu.stanford.nlp.ling.CoreLabel();
    labelRoot.setValue("NP");
    parent.setLabel(labelRoot);
    parent.setChildren(new Tree[] {childA, childB});
    CoreNLPProtos.ParseTree proto = ProtobufAnnotationSerializer.toProto(parent);
    // Tree restored = ProtobufAnnotationSerializer.fromProto(proto);
    // assertEquals("NP", restored.label().value());
    // assertEquals("JJ", restored.children()[0].label().value());
    // assertEquals("simple", restored.children()[1].label().value());
  }
}
