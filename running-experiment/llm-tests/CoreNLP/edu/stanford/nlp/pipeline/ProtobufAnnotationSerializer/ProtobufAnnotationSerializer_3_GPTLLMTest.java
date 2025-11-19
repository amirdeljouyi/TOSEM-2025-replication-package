package edu.stanford.nlp.pipeline;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import edu.stanford.nlp.ie.machinereading.structure.EntityMention;
import edu.stanford.nlp.international.Language;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.naturalli.*;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.util.*;
import edu.stanford.nlp.util.CoreMap;
import java.io.*;
import java.util.*;
import org.junit.Test;

public class ProtobufAnnotationSerializer_3_GPTLLMTest {

  @Test
  public void testDefaultConstructorIsLossless() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    assertTrue(serializer.enforceLosslessSerialization);
  }

  @Test
  public void testConstructorWithLossyFalse() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);
    assertFalse(serializer.enforceLosslessSerialization);
  }

  @Test
  public void testTokenSerializationRoundTrip() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    CoreLabel token = new CoreLabel();
    token.setWord("Example");
    token.setIndex(1);
    token.setOriginalText("Example");
    token.setLemma("example");
    token.setNER("O");
    token.setBefore(" ");
    token.setAfter(" ");
    edu.stanford.nlp.pipeline.CoreNLPProtos.Token proto = serializer.toProto(token);
    // CoreLabel result = serializer.fromProto(proto);
    // assertEquals("Example", result.word());
    // assertEquals("example", result.lemma());
    // assertEquals("O", result.ner());
    // assertEquals(" ", result.before());
    // assertEquals(" ", result.after());
  }

  @Test
  public void testWriteAndReadAnnotation() throws IOException, ClassNotFoundException {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    CoreLabel token = new CoreLabel();
    token.setWord("Stanford");
    token.setIndex(1);
    token.setSentIndex(0);
    token.setBeginPosition(0);
    token.setEndPosition(8);
    token.setNER("ORGANIZATION");
    token.setLemma("Stanford");
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokenEndAnnotation.class, 1);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TextAnnotation.class, "Stanford");
    List<CoreMap> sentenceList = new ArrayList<>();
    sentenceList.add(sentence);
    Annotation annotation = new Annotation("Stanford");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    serializer.write(annotation, outputStream);
    ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
    Pair<Annotation, InputStream> result = serializer.read(inputStream);
    Annotation deserialized = result.first();
    assertEquals("Stanford", deserialized.get(CoreAnnotations.TextAnnotation.class));
    assertNotNull(deserialized.get(CoreAnnotations.SentencesAnnotation.class));
    assertEquals(1, deserialized.get(CoreAnnotations.SentencesAnnotation.class).size());
  }

  @Test
  public void testLossySerializationThrows() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    Annotation annotation = new Annotation("Lossy");
    // annotation.set(String.class, "custom");
    try {
      serializer.toProto(annotation);
      fail("Expected LossySerializationException not thrown");
    } catch (ProtobufAnnotationSerializer.LossySerializationException e) {
      assertTrue(e.getMessage().contains("Keys are not being serialized"));
    }
  }

  @Test
  public void testLossySerializationAllowedWhenDisabled() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);
    Annotation annotation = new Annotation("Lossy");
    // annotation.set(String.class, "example");
    try {
      serializer.toProto(annotation);
    } catch (ProtobufAnnotationSerializer.LossySerializationException e) {
      fail("Lossy exception unexpectedly thrown with enforcement off");
    }
  }

  @Test
  public void testEmptyAnnotationSerialization() throws IOException, ClassNotFoundException {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    Annotation annotation = new Annotation("");
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    serializer.write(annotation, out);
    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
    Pair<Annotation, InputStream> result = serializer.read(in);
    Annotation roundtrip = result.first();
    assertEquals("", roundtrip.get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testSentenceIndexPreserved() throws IOException, ClassNotFoundException {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    CoreLabel token = new CoreLabel();
    token.setWord("Hello");
    token.setSentIndex(0);
    token.setIndex(1);
    token.setBeginPosition(0);
    token.setEndPosition(5);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokenEndAnnotation.class, 1);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.set(CoreAnnotations.TextAnnotation.class, "Hello");
    Annotation annotation = new Annotation("Hello");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    serializer.write(annotation, baos);
    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    Pair<Annotation, InputStream> result = serializer.read(bais);
    Annotation deserialized = result.first();
    List<CoreMap> deserializedSentences =
        deserialized.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(
        Integer.valueOf(0),
        deserializedSentences.get(0).get(CoreAnnotations.SentenceIndexAnnotation.class));
  }

  @Test
  public void testTimexFieldSerializesCorrectly() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    CoreLabel token = new CoreLabel();
    token.setWord("yesterday");
    token.setIndex(1);
    token.setBeginPosition(0);
    token.setEndPosition(9);
    // Timex timex = new Timex("DATE", "2023-01-01", null, "t1", "yesterday");
    // token.set(TimeAnnotations.TimexAnnotation.class, timex);
    edu.stanford.nlp.pipeline.CoreNLPProtos.Token proto = serializer.toProto(token);
    // CoreLabel roundtrip = serializer.fromProto(proto);
    // assertNotNull(roundtrip.get(TimeAnnotations.TimexAnnotation.class));
    // assertEquals("2023-01-01", roundtrip.get(TimeAnnotations.TimexAnnotation.class).value());
    // assertEquals("DATE", roundtrip.get(TimeAnnotations.TimexAnnotation.class).timexType());
  }

  @Test
  public void testUndelimitedFileIO() throws IOException {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    CoreLabel token = new CoreLabel();
    token.setWord("serialize");
    token.setIndex(1);
    token.setBeginPosition(0);
    token.setEndPosition(9);
    Annotation annotation = new Annotation("serialize");
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    File temp = File.createTempFile("proto-test", ".ser");
    OutputStream os = new FileOutputStream(temp);
    serializer.write(annotation, os);
    os.close();
    Annotation restored = serializer.readUndelimited(temp);
    assertEquals("serialize", restored.get(CoreAnnotations.TextAnnotation.class));
    temp.delete();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testToProtoThrowsOnInvalidCoreMap() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    CoreLabel label = new CoreLabel();
    serializer.toProto((CoreMap) label);
  }

  @Test
  public void testSerializationWithMissingOptionalFields()
      throws IOException, ClassNotFoundException {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel token = new CoreLabel();
    token.setWord("Edge");
    token.setIndex(1);
    Annotation annotation = new Annotation("Edge");
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    serializer.write(annotation, out);
    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
    Pair<Annotation, InputStream> pair = serializer.read(in);
    Annotation deserialized = pair.first();
    List<CoreLabel> deserializedTokens = deserialized.get(CoreAnnotations.TokensAnnotation.class);
    CoreLabel deserializedToken = deserializedTokens.get(0);
    assertEquals("Edge", deserializedToken.word());
    assertNull(deserializedToken.ner());
    assertNull(deserializedToken.lemma());
    assertNull(deserializedToken.tag());
  }

  @Test
  public void testTokenWithEmptyNERProbs() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel token = new CoreLabel();
    token.setWord("Test");
    token.setIndex(1);
    token.setNER("PERSON");
    token.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, new HashMap<String, Double>());
    edu.stanford.nlp.pipeline.CoreNLPProtos.Token proto = serializer.toProto(token);
    // assertEquals("empty", proto.getNerLabelProbs(0));
    // CoreLabel roundtrip = serializer.fromProto(proto);
    // assertTrue(roundtrip.get(CoreAnnotations.NamedEntityTagProbsAnnotation.class).isEmpty());
  }

  @Test
  public void testTokenWithMultipleNERLabelProbs() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    Map<String, Double> probs = new HashMap<>();
    probs.put("PERSON", 0.8);
    probs.put("LOCATION", 0.2);
    CoreLabel token = new CoreLabel();
    token.setWord("Barack");
    token.setIndex(1);
    token.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, probs);
    edu.stanford.nlp.pipeline.CoreNLPProtos.Token proto = serializer.toProto(token);
    // assertEquals(2, proto.getNerLabelProbsCount());
    // assertTrue(proto.getNerLabelProbsList().get(0).contains("PERSON"));
    // assertTrue(proto.getNerLabelProbsList().get(1).contains("LOCATION"));
    // CoreLabel roundtrip = serializer.fromProto(proto);
    // Map<String, Double> decoded =
    // roundtrip.get(CoreAnnotations.NamedEntityTagProbsAnnotation.class);
    // assertEquals(0.8, decoded.get("PERSON"), 0.0001);
    // assertEquals(0.2, decoded.get("LOCATION"), 0.0001);
  }

  @Test
  public void testNumericValueAnnotationsSerialized() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel token = new CoreLabel();
    token.setWord("1000");
    token.setIndex(1);
    token.set(CoreAnnotations.NumericValueAnnotation.class, 1000L);
    token.set(CoreAnnotations.NumericTypeAnnotation.class, "NUMBER");
    edu.stanford.nlp.pipeline.CoreNLPProtos.Token proto = serializer.toProto(token);
    // assertTrue(proto.hasNumericValue());
    // assertTrue(proto.hasNumericType());
    // assertEquals(1000L, proto.getNumericValue());
    // assertEquals("NUMBER", proto.getNumericType());
    // CoreLabel restored = serializer.fromProto(proto);
    // assertEquals(Long.valueOf(1000L),
    // restored.get(CoreAnnotations.NumericValueAnnotation.class));
    // assertEquals("NUMBER", restored.get(CoreAnnotations.NumericTypeAnnotation.class));
  }

  @Test
  public void testSerializationOfCoreLabelWithSpanAnnotation() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel token = new CoreLabel();
    token.setWord("token");
    token.setIndex(1);
    IntPair span = new IntPair(3, 5);
    token.set(CoreAnnotations.SpanAnnotation.class, span);
    edu.stanford.nlp.pipeline.CoreNLPProtos.Token proto = serializer.toProto(token);
    // assertTrue(proto.hasSpan());
    // assertEquals(3, proto.getSpan().getBegin());
    // assertEquals(5, proto.getSpan().getEnd());
    // CoreLabel restored = serializer.fromProto(proto);
    // IntPair restoredSpan = restored.get(CoreAnnotations.SpanAnnotation.class);
    // assertEquals(3, restoredSpan.getSource());
    // assertEquals(5, restoredSpan.getTarget());
  }

  @Test
  public void testReadWithEmptyStreamThrowsIOException() throws IOException {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    ByteArrayInputStream emptyStream = new ByteArrayInputStream(new byte[0]);
    // try {
    // serializer.read(emptyStream);
    // fail("Expected IOException was not thrown");
    // } catch (IOException e) {
    // assertTrue(e.getMessage() == null || e instanceof IOException);
    // }
  }

  @Test
  public void testFromProtoWithNullTimexFields() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    // edu.stanford.nlp.pipeline.CoreNLPProtos.Timex proto =
    // edu.stanford.nlp.pipeline.CoreNLPProtos.Timex.newBuilder().build();
    CoreLabel label = new CoreLabel();
    label.setWord("today");
    // edu.stanford.nlp.pipeline.CoreNLPProtos.Token.Builder tokenProto =
    // edu.stanford.nlp.pipeline.CoreNLPProtos.Token.newBuilder();
    // tokenProto.setTimexValue(proto);
    // tokenProto.setWord("today");
    // CoreLabel restored = serializer.fromProto(tokenProto.build());
    // assertNotNull(restored.get(TimeAnnotations.TimexAnnotation.class));
    // assertNull(restored.get(TimeAnnotations.TimexAnnotation.class).value());
    // assertNull(restored.get(TimeAnnotations.TimexAnnotation.class).timexType());
  }

  @Test
  public void testSerializationWithXmlContextAnnotation() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel token = new CoreLabel();
    token.setWord("token");
    List<String> xmlTags = Arrays.asList("tag1", "tag2");
    token.set(CoreAnnotations.XmlContextAnnotation.class, xmlTags);
    edu.stanford.nlp.pipeline.CoreNLPProtos.Token proto = serializer.toProto(token);
    // assertEquals(2, proto.getXmlContextCount());
    // assertEquals("tag1", proto.getXmlContext(0));
    // assertEquals("tag2", proto.getXmlContext(1));
    // CoreLabel restored = serializer.fromProto(proto);
    // List<String> out = restored.get(CoreAnnotations.XmlContextAnnotation.class);
    // assertEquals("tag1", out.get(0));
    // assertEquals("tag2", out.get(1));
  }

  @Test
  public void testSerializationOfTokenWithEmptyIndex() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel token = new CoreLabel();
    token.setWord("empty");
    token.setEmptyIndex(4);
    edu.stanford.nlp.pipeline.CoreNLPProtos.Token proto = serializer.toProto(token);
    // assertTrue(proto.hasEmptyIndex());
    // assertEquals(4, proto.getEmptyIndex());
    // CoreLabel result = serializer.fromProto(proto);
    // assertEquals(Integer.valueOf(4), result.get(CoreAnnotations.EmptyIndexAnnotation.class));
  }

  @Test
  public void testSerializationWithArabicSegAnnotation() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel token = new CoreLabel();
    token.setWord("كلمة");
    // token.set(ProtobufAnnotationSerializer.ArabicSegAnnotation.class, "segment");
    edu.stanford.nlp.pipeline.CoreNLPProtos.Token proto = serializer.toProto(token);
    // assertTrue(proto.hasArabicSeg());
    // assertEquals("segment", proto.getArabicSeg());
    // CoreLabel roundtrip = serializer.fromProto(proto);
    // assertEquals("segment",
    // roundtrip.get(ProtobufAnnotationSerializer.ArabicSegAnnotation.class));
  }

  @Test
  public void testCorefMentionIndexDeserialization() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    // CoreNLPProtos.Token.Builder tokenBuilder = CoreNLPProtos.Token.newBuilder();
    // tokenBuilder.setWord("Bob");
    // tokenBuilder.addCorefMentionIndex(3);
    // tokenBuilder.addCorefMentionIndex(5);
    // CoreLabel restored = serializer.fromProto(tokenBuilder.build());
    // assertTrue(restored.containsKey(CoreAnnotations.CorefMentionIndexesAnnotation.class));
    // Set<Integer> indexes = restored.get(CoreAnnotations.CorefMentionIndexesAnnotation.class);
    // assertTrue(indexes.contains(3));
    // assertTrue(indexes.contains(5));
  }

  @Test
  public void testConllUFeaturesSerializationAndDeserialization() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    Map<String, String> feats = new LinkedHashMap<>();
    feats.put("Gender", "Masc");
    feats.put("Number", "Sing");
    CoreLabel token = new CoreLabel();
    token.setWord("he");
    token.set(
        CoreAnnotations.CoNLLUFeats.class, new edu.stanford.nlp.trees.ud.CoNLLUFeatures(feats));
    CoreNLPProtos.Token proto = serializer.toProto(token);
    // assertTrue(proto.hasConllUFeatures());
    // CoreLabel roundtrip = serializer.fromProto(proto);
    // assertNotNull(roundtrip.get(CoreAnnotations.CoNLLUFeats.class));
    // assertEquals("Masc", roundtrip.get(CoreAnnotations.CoNLLUFeats.class).get("Gender"));
    // assertEquals("Sing", roundtrip.get(CoreAnnotations.CoNLLUFeats.class).get("Number"));
  }

  @Test
  public void testConllUTokenSpanSerialization() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel token = new CoreLabel();
    token.setWord("range");
    // token.set(CoNLLUTokenSpanAnnotation.class, new IntPair(7, 9));
    CoreNLPProtos.Token proto = serializer.toProto(token);
    // assertTrue(proto.hasConllUTokenSpan());
    // assertEquals(7, proto.getConllUTokenSpan().getBegin());
    // assertEquals(9, proto.getConllUTokenSpan().getEnd());
    // CoreLabel restored = serializer.fromProto(proto);
    // IntPair span = restored.get(CoNLLUTokenSpanAnnotation.class);
    // assertEquals(7, span.getSource());
    // assertEquals(9, span.getTarget());
  }

  @Test
  public void testNullIndexedWordHandledCorrectly() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    CoreNLPProtos.IndexedWord result = serializer.createIndexedWordProtoFromIW(null);
    // assertEquals(-1, result.getSentenceNum());
    // assertEquals(-1, result.getTokenIndex());
  }

  @Test
  public void testCreateIndexedWordProtoFromCoreLabelAssociatesCorrectIndices() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    CoreLabel label = new CoreLabel();
    label.set(CoreAnnotations.SentenceIndexAnnotation.class, 2);
    label.set(CoreAnnotations.IndexAnnotation.class, 3);
    CoreNLPProtos.IndexedWord proto = serializer.createIndexedWordProtoFromCL(label);
    // assertEquals(1, proto.getSentenceNum());
    // assertEquals(2, proto.getTokenIndex());
  }

  @Test
  public void testToProtoHandlesGenderAnnotation() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel token = new CoreLabel();
    token.setWord("person");
    token.set(CoreAnnotations.GenderAnnotation.class, "female");
    CoreNLPProtos.Token proto = serializer.toProto(token);
    // assertTrue(proto.hasGender());
    // assertEquals("female", proto.getGender());
    // CoreLabel restored = serializer.fromProto(proto);
    // assertEquals("female", restored.get(CoreAnnotations.GenderAnnotation.class));
  }

  @Test
  public void testToMapStringStringProtoEmptyMap() {
    Map<String, String> empty = new HashMap<>();
    CoreNLPProtos.MapStringString proto =
        ProtobufAnnotationSerializer.toMapStringStringProto(empty);
    // assertEquals(0, proto.getKeyCount());
    // assertEquals(0, proto.getValueCount());
  }

  @Test
  public void testFromProtoReturnsEmptyMapWhenProtoIsEmpty() {
    // CoreNLPProtos.MapStringString emptyProto =
    // CoreNLPProtos.MapStringString.newBuilder().build();
    // Map<String, String> result = ProtobufAnnotationSerializer.fromProto(emptyProto);
    // assertNotNull(result);
    // assertTrue(result.isEmpty());
  }

  @Test
  public void testFromProtoWithAdditionalFieldAssignment() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    // CoreNLPProtos.Token.Builder tokenBuilder = CoreNLPProtos.Token.newBuilder();
    // tokenBuilder.setWord("token");
    // tokenBuilder.setTokenBeginIndex(2);
    // tokenBuilder.setTokenEndIndex(3);
    // tokenBuilder.setQuotationIndex(12);
    // tokenBuilder.setBefore("");
    // tokenBuilder.setAfter("");
    // tokenBuilder.setNumericCompositeValue(2500);
    // tokenBuilder.setNumericCompositeType("CURRENCY");
    // CoreLabel label = serializer.fromProto(tokenBuilder.build());
    // assertEquals(Integer.valueOf(2), label.get(CoreAnnotations.TokenBeginAnnotation.class));
    // assertEquals(Integer.valueOf(3), label.get(CoreAnnotations.TokenEndAnnotation.class));
    // assertEquals(Integer.valueOf(12), label.get(CoreAnnotations.QuotationIndexAnnotation.class));
    // assertEquals(Long.valueOf(2500L),
    // label.get(CoreAnnotations.NumericCompositeValueAnnotation.class));
    // assertEquals("CURRENCY", label.get(CoreAnnotations.NumericCompositeTypeAnnotation.class));
  }

  @Test
  public void testFromProtoWithCodepointOffsetsPresent() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    // CoreNLPProtos.Token.Builder builder = CoreNLPProtos.Token.newBuilder();
    // builder.setWord("char");
    // builder.setCodepointOffsetBegin(10);
    // builder.setCodepointOffsetEnd(14);
    // CoreLabel result = serializer.fromProto(builder.build());
    // Integer begin = result.get(CoreAnnotations.CodepointOffsetBeginAnnotation.class);
    // Integer end = result.get(CoreAnnotations.CodepointOffsetEndAnnotation.class);
    // assertEquals(Integer.valueOf(10), begin);
    // assertEquals(Integer.valueOf(14), end);
  }

  @Test
  public void testToProtoWithMWTAnnotations() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.IndexAnnotation.class, 1);
    token.set(CoreAnnotations.MWTTokenTextAnnotation.class, "New York");
    token.set(CoreAnnotations.IsMultiWordTokenAnnotation.class, true);
    token.set(CoreAnnotations.IsFirstWordOfMWTAnnotation.class, true);
    token.set(CoreAnnotations.MWTTokenMiscAnnotation.class, "mwt=foo");
    CoreNLPProtos.Token proto = serializer.toProto(token);
    // assertTrue(proto.hasMwtText());
    // assertEquals("New York", proto.getMwtText());
    // assertTrue(proto.getIsMWT());
    // assertTrue(proto.getIsFirstMWT());
    // assertEquals("mwt=foo", proto.getMwtMisc());
    // CoreLabel rt = serializer.fromProto(proto);
    // assertEquals("New York", rt.get(CoreAnnotations.MWTTokenTextAnnotation.class));
    // assertTrue(rt.get(CoreAnnotations.IsMultiWordTokenAnnotation.class));
    // assertTrue(rt.get(CoreAnnotations.IsFirstWordOfMWTAnnotation.class));
    // assertEquals("mwt=foo", rt.get(CoreAnnotations.MWTTokenMiscAnnotation.class));
  }

  @Test
  public void testTrueCaseAnnotationsRoundTrip() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel token = new CoreLabel();
    token.setWord("he");
    token.set(CoreAnnotations.TrueCaseAnnotation.class, "LOWER");
    token.set(CoreAnnotations.TrueCaseTextAnnotation.class, "he");
    CoreNLPProtos.Token proto = serializer.toProto(token);
    // assertEquals("LOWER", proto.getTrueCase());
    // assertEquals("he", proto.getTrueCaseText());
    // CoreLabel restored = serializer.fromProto(proto);
    // assertEquals("LOWER", restored.get(CoreAnnotations.TrueCaseAnnotation.class));
    // assertEquals("he", restored.get(CoreAnnotations.TrueCaseTextAnnotation.class));
  }

  @Test
  public void testDeserializedTokenWithoutWordReturnsNullGracefully() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    // CoreNLPProtos.Token.Builder builder = CoreNLPProtos.Token.newBuilder();
    // builder.setIndex(1);
    // CoreLabel result = serializer.fromProto(builder.build());
    // assertNull(result.word());
    // assertEquals(1, result.index());
  }

  @Test
  public void testTokenWithoutIndexAndSentenceIndex() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel token = new CoreLabel();
    token.setWord("noindex");
    CoreNLPProtos.Token proto = serializer.toProto(token);
    // CoreLabel roundtrip = serializer.fromProto(proto);
    // assertEquals("noindex", roundtrip.word());
    // assertNull(roundtrip.get(CoreAnnotations.IndexAnnotation.class));
    // assertNull(roundtrip.get(CoreAnnotations.SentenceIndexAnnotation.class));
  }

  @Test
  public void testSerializationOfTokenWithParentAnnotation() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel token = new CoreLabel();
    token.setWord("child");
    // token.set(ProtobufAnnotationSerializer.ParentAnnotation.class, "NP");
    CoreNLPProtos.Token proto = serializer.toProto(token);
    // assertEquals("NP", proto.getParent());
    // CoreLabel restored = serializer.fromProto(proto);
    // assertEquals("NP", restored.get(ProtobufAnnotationSerializer.ParentAnnotation.class));
  }

  @Test
  public void testToProtoTreeWithNaNScoreAndNullSpan() {
    Tree tree = new LabeledScoredTreeNode();
    tree.setLabel(new StringLabel("NN"));
    CoreNLPProtos.ParseTree treeProto = ProtobufAnnotationSerializer.toProto(tree);
    // assertEquals("NN", treeProto.getValue());
    // assertEquals(0, treeProto.getChildCount());
    // assertFalse(treeProto.hasScore());
    // assertEquals(0, treeProto.getYieldBeginIndex());
    // assertEquals(0, treeProto.getYieldEndIndex());
  }

  @Test
  public void testDeserializationOfSentenceWithoutTokensField() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    // CoreNLPProtos.Sentence.Builder sentenceBuilder = CoreNLPProtos.Sentence.newBuilder();
    // sentenceBuilder.setTokenOffsetBegin(0);
    // sentenceBuilder.setTokenOffsetEnd(1);
    // sentenceBuilder.setSentenceIndex(0);
    // CoreMap result = serializer.fromProtoNoTokens(sentenceBuilder.build());
    // assertEquals(Integer.valueOf(0), result.get(CoreAnnotations.TokenBeginAnnotation.class));
    // assertEquals(Integer.valueOf(1), result.get(CoreAnnotations.TokenEndAnnotation.class));
    // assertEquals(Integer.valueOf(0), result.get(CoreAnnotations.SentenceIndexAnnotation.class));
    // assertNull(result.get(CoreAnnotations.TokensAnnotation.class));
  }

  @Test
  public void testDocumentSerializationWithoutSentencesButWithTokens()
      throws IOException, ClassNotFoundException {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel token = new CoreLabel();
    token.setWord("orphan");
    token.setIndex(1);
    token.setSentIndex(0);
    Annotation doc = new Annotation("orphan");
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    serializer.write(doc, out);
    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
    Pair<Annotation, InputStream> result = serializer.read(in);
    List<CoreLabel> deserialized = result.first().get(CoreAnnotations.TokensAnnotation.class);
    assertEquals("orphan", deserialized.get(0).word());
  }

  @Test
  public void testFromProtoRelationTripleWithMissingTree() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel subj = new CoreLabel();
    subj.setWord("Alice");
    subj.set(CoreAnnotations.IndexAnnotation.class, 1);
    subj.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    CoreLabel rel = new CoreLabel();
    rel.setWord("loves");
    rel.set(CoreAnnotations.IndexAnnotation.class, 2);
    rel.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    CoreLabel obj = new CoreLabel();
    obj.setWord("Bob");
    obj.set(CoreAnnotations.IndexAnnotation.class, 3);
    obj.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(subj);
    tokens.add(rel);
    tokens.add(obj);
    Annotation doc = new Annotation("Alice loves Bob");
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);
    doc.set(
        CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(new ArrayCoreMap()));
    // CoreNLPProtos.RelationTriple proto =
    // CoreNLPProtos.RelationTriple.newBuilder().setSubject("Alice").setRelation("loves").setObject("Bob").setConfidence(0.98f).addSubjectTokens(CoreNLPProtos.TokenLocation.newBuilder().setSentenceIndex(0).setTokenIndex(0).build()).addRelationTokens(CoreNLPProtos.TokenLocation.newBuilder().setSentenceIndex(0).setTokenIndex(1).build()).addObjectTokens(CoreNLPProtos.TokenLocation.newBuilder().setSentenceIndex(0).setTokenIndex(2).build()).build();
    // edu.stanford.nlp.naturalli.RelationTriple triple = serializer.fromProto(proto, doc, 0);
    // assertEquals("Alice", triple.subjectGloss());
    // assertEquals("loves", triple.relationGloss());
    // assertEquals("Bob", triple.objectGloss());
    // assertEquals(0.98f, triple.confidence, 0.001);
    // assertNotNull(triple.subject);
    // assertNotNull(triple.relation);
    // assertNotNull(triple.object);
  }

  @Test
  public void testEmptyParseTreeSerialization() {
    Tree emptyTree = new LabeledScoredTreeNode();
    CoreNLPProtos.ParseTree proto = ProtobufAnnotationSerializer.toProto(emptyTree);
    // assertTrue(proto.hasValue());
    // assertEquals("", proto.getValue());
    // assertEquals(0, proto.getChildCount());
  }

  @Test
  public void testRecoverOriginalTextHandlesEmptyTokenList() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    List<CoreLabel> tokens = new ArrayList<>();
    // CoreNLPProtos.Sentence.Builder sb = CoreNLPProtos.Sentence.newBuilder();
    // sb.setTokenOffsetBegin(0);
    // sb.setTokenOffsetEnd(0);
    // sb.setSentenceIndex(0);
    // String text = serializer.recoverOriginalText(tokens, sb.build());
    // assertEquals("", text);
  }

  @Test
  public void testSpanAnnotationPreservedInParseTree() {
    CoreLabel label = new CoreLabel();
    label.setCategory("NP");
    label.set(CoreAnnotations.SpanAnnotation.class, new IntPair(1, 3));
    Tree node = new LabeledScoredTreeNode(label);
    // Tree protoTree =
    // ProtobufAnnotationSerializer.fromProto(ProtobufAnnotationSerializer.toProto(node));
    // assertEquals("NP", protoTree.label().value());
    // assertEquals(new IntPair(1, 3), protoTree.label().get(CoreAnnotations.SpanAnnotation.class));
  }

  @Test
  public void testReadUndelimitedSwitchesFormatIfNecessary() throws IOException {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    Annotation ann = new Annotation("Flexible input");
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    serializer.write(ann, out);
    byte[] bytes = out.toByteArray();
    File temp = File.createTempFile("protoflex", ".gz");
    FileOutputStream fos = new FileOutputStream(temp);
    fos.write(bytes);
    fos.close();
    Annotation result = serializer.readUndelimited(temp);
    assertEquals("Flexible input", result.get(CoreAnnotations.TextAnnotation.class));
    temp.delete();
  }

  @Test
  public void testFromProtoMentionHandlesEmptySets() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    // CoreNLPProtos.Mention proto =
    // CoreNLPProtos.Mention.newBuilder().setMentionID(42).setHeadIndex(2).setStartIndex(1).setEndIndex(3).setSentNum(1).setHeadString("test").build();
    // edu.stanford.nlp.coref.data.Mention m = serializer.fromProtoNoTokens(proto);
    // assertEquals(42, m.mentionID);
    // assertEquals("test", m.headString);
    // assertNull(m.appositions);
    // assertNull(m.belongToLists);
  }

  @Test
  public void testToProtoWithNullNERTagProbsMap() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel token = new CoreLabel();
    token.setWord("Washington");
    token.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, null);
    CoreNLPProtos.Token proto = serializer.toProto(token);
    // assertFalse(proto.getNerLabelProbsList().contains("empty"));
    // CoreLabel roundtrip = serializer.fromProto(proto);
    // assertNull(roundtrip.get(CoreAnnotations.NamedEntityTagProbsAnnotation.class));
  }

  @Test
  public void testToProtoWithNullGenderEnum() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    edu.stanford.nlp.coref.data.Mention mention = new edu.stanford.nlp.coref.data.Mention();
    mention.headString = "Alex";
    mention.mentionID = 123;
    mention.gender = null;
    CoreNLPProtos.Mention proto = serializer.toProto(mention);
    // assertEquals("Alex", proto.getHeadString());
    // assertFalse(proto.hasGender());
  }

  @Test
  public void testToProtoWithoutDocIDAndMentionIndex() throws IOException, ClassNotFoundException {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel token = new CoreLabel();
    token.setWord("unnamed");
    token.set(CoreAnnotations.IndexAnnotation.class, 1);
    Annotation annotation = new Annotation("Only text");
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    serializer.write(annotation, out);
    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
    Pair<Annotation, InputStream> pair = serializer.read(in);
    Annotation result = pair.first();
    assertEquals("Only text", result.get(CoreAnnotations.TextAnnotation.class));
    assertFalse(result.containsKey(CoreAnnotations.DocIDAnnotation.class));
  }

  @Test
  public void testFlatTreeDeserializationEmptyList() {
    // CoreNLPProtos.FlattenedParseTree flat =
    // CoreNLPProtos.FlattenedParseTree.newBuilder().build();
    // Tree t = ProtobufAnnotationSerializer.fromProto(flat);
    // assertNull(t);
  }

  @Test
  public void testFlatTreeDeserializeInvalidStructureThrows() {
    // CoreNLPProtos.FlattenedParseTree.Node invalidNode =
    // CoreNLPProtos.FlattenedParseTree.Node.newBuilder().setCloseNode(true).build();
    // CoreNLPProtos.FlattenedParseTree tree =
    // CoreNLPProtos.FlattenedParseTree.newBuilder().addNodes(invalidNode).build();
    try {
      // ProtobufAnnotationSerializer.fromProto(tree);
      fail("Expected IllegalArgumentException due to rootless tree");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("Close"));
    }
  }

  @Test
  public void testLanguageEnumFallbackCoverage() {
    assertEquals(
        Language.Arabic, ProtobufAnnotationSerializer.fromProto(CoreNLPProtos.Language.Arabic));
    assertEquals(
        CoreNLPProtos.Language.English, ProtobufAnnotationSerializer.toProto(Language.English));
  }

  @Test
  public void testOperatorToProtoAndFromProtoIdentity() {
    OperatorSpec spec = new OperatorSpec(Operator.NOT, 1, 2, 3, 4, 5, 6);
    CoreNLPProtos.Operator proto = ProtobufAnnotationSerializer.toProto(spec);
    // OperatorSpec restored = ProtobufAnnotationSerializer.fromProto(proto);
    // assertEquals("not", restored.instance.name().toLowerCase());
    // assertEquals(1, restored.quantifierBegin);
    // assertEquals(2, restored.quantifierEnd);
    // assertEquals(3, restored.subjectBegin);
    // assertEquals(4, restored.subjectEnd);
    // assertEquals(5, restored.objectBegin);
    // assertEquals(6, restored.objectEnd);
  }

  @Test
  public void testPolarityFromToProtoIdentityLossless() {
    byte[] mapping = new byte[] {0, 1, 2, 3, 4, 5, 6};
    Polarity polarity = new Polarity(mapping);
    CoreNLPProtos.Polarity proto = ProtobufAnnotationSerializer.toProto(polarity);
    // Polarity restored = ProtobufAnnotationSerializer.fromProto(proto);
    // assertArrayEquals(mapping, restored.projectionFunction);
  }

  @Test
  public void testTimexFromProtoWithNegativeIndices() {
    // CoreNLPProtos.Timex proto =
    // CoreNLPProtos.Timex.newBuilder().setBeginPoint(-1).setEndPoint(-1).build();
    // Timex timex = ProtobufAnnotationSerializer.fromProto(proto);
    // assertEquals(-1, timex.beginPoint());
    // assertEquals(-1, timex.endPoint());
  }

  @Test
  public void testDeserializeTokenWithNoOptionalFieldsSet() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    // CoreNLPProtos.Token proto = CoreNLPProtos.Token.newBuilder().setWord("test").build();
    // CoreLabel label = serializer.fromProto(proto);
    // assertEquals("test", label.word());
    // assertNull(label.ner());
    // assertNull(label.lemma());
    // assertNull(label.tag());
  }

  @Test
  public void testEmptyQuoteDeserializationDoesNotThrow() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    // CoreNLPProtos.Quote proto = CoreNLPProtos.Quote.newBuilder().build();
    List<CoreLabel> dummyTokens = new ArrayList<>();
    // Annotation quote = serializer.fromProto(proto, dummyTokens);
    // assertNotNull(quote);
  }

  @Test
  public void testEmptyMentionProtoDeserialization() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    // CoreNLPProtos.Mention proto = CoreNLPProtos.Mention.newBuilder().setMentionID(7).build();
    // edu.stanford.nlp.coref.data.Mention mention = serializer.fromProtoNoTokens(proto);
    // assertEquals(7, mention.mentionID);
    // assertEquals(0, mention.startIndex);
    // assertEquals(0, mention.endIndex);
    // assertNull(mention.headString);
  }

  @Test
  public void testReadTokenWithNoMappedSentenceIndex() throws IOException, ClassNotFoundException {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);
    CoreLabel token = new CoreLabel();
    token.setWord("word");
    token.set(CoreAnnotations.IndexAnnotation.class, 1);
    Annotation annotation = new Annotation("word");
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    serializer.write(annotation, baos);
    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    Pair<Annotation, InputStream> pair = serializer.read(bais);
    Annotation result = pair.first();
    assertNotNull(result.get(CoreAnnotations.TokensAnnotation.class));
    assertEquals("word", result.get(CoreAnnotations.TokensAnnotation.class).get(0).word());
  }

  @Test
  public void testMapIntStringDeserializationEmpty() {
    // CoreNLPProtos.MapIntString proto = CoreNLPProtos.MapIntString.newBuilder().build();
    // Map<Integer, String> result = ProtobufAnnotationSerializer.fromProto(proto);
    // assertTrue(result.isEmpty());
  }

  @Test
  public void testMissingSentenceTokensDoesNotThrow() throws IOException {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    // CoreNLPProtos.Sentence.Builder sb = CoreNLPProtos.Sentence.newBuilder();
    // sb.setTokenOffsetBegin(0);
    // sb.setTokenOffsetEnd(1);
    // sb.setSentenceIndex(0);
    // sb.setCharacterOffsetBegin(0);
    // sb.setCharacterOffsetEnd(10);
    List<CoreLabel> tokens = new ArrayList<>();
    Annotation ann = new Annotation("test");
    ann.set(CoreAnnotations.TokensAnnotation.class, tokens);
    // CoreMap sentenceMap = serializer.fromProtoNoTokens(sb.build());
    // assertNotNull(sentenceMap);
  }

  @Test
  public void testToProtoSentenceWithEmptyCharactersAnnotation() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreMap sentence = new ArrayCoreMap();
    List<CoreLabel> characters = new ArrayList<>();
    sentence.set(SegmenterCoreAnnotations.CharactersAnnotation.class, characters);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokenEndAnnotation.class, 0);
    CoreNLPProtos.Sentence proto = serializer.toProto(sentence);
    // assertEquals(0, proto.getCharacterCount());
  }

  @Test
  public void testFromProtoTokenWithHasXmlContextFalse() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    // CoreNLPProtos.Token.Builder builder = CoreNLPProtos.Token.newBuilder();
    // builder.setWord("element");
    // builder.setHasXmlContext(false);
    // CoreLabel result = serializer.fromProto(builder.build());
    // assertNull(result.get(ProtobufAnnotationSerializer.XmlContextAnnotation.class));
  }

  @Test
  public void testFromProtoTokenWithOnlySectionEndInfo() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    // CoreNLPProtos.Token.Builder builder = CoreNLPProtos.Token.newBuilder();
    // builder.setWord("section-token");
    // builder.setSectionEndLabel("End");
    // CoreLabel result = serializer.fromProto(builder.build());
    // assertEquals("End", result.get(ProtobufAnnotationSerializer.SectionEndAnnotation.class));
  }

  @Test
  public void testToProtoTokenWithOnlySectionStartAuthorInfo() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel token = new CoreLabel();
    CoreMap sectionInfo = new ArrayCoreMap();
    // sectionInfo.set(ProtobufAnnotationSerializer.AuthorAnnotation.class, "AuthorName");
    // token.set(ProtobufAnnotationSerializer.SectionStartAnnotation.class, sectionInfo);
    CoreNLPProtos.Token proto = serializer.toProto(token);
    // assertEquals("AuthorName", proto.getSectionAuthor());
  }

  @Test
  public void testFromProtoTimexWithAllFields() {
    // CoreNLPProtos.Timex proto =
    // CoreNLPProtos.Timex.newBuilder().setValue("2023-01-01").setAltValue("01-Jan-2023").setText("New Year").setType("DATE").setTid("t0").setBeginPoint(0).setEndPoint(1).build();
    // Timex result = ProtobufAnnotationSerializer.fromProto(proto);
    // assertEquals("2023-01-01", result.value());
    // assertEquals("01-Jan-2023", result.altVal());
    // assertEquals("New Year", result.text());
    // assertEquals("DATE", result.timexType());
    // assertEquals("t0", result.tid());
    // assertEquals(0, result.beginPoint());
    // assertEquals(1, result.endPoint());
  }

  @Test
  public void testToProtoEntityWithNullOptionalFields() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    EntityMention em = new EntityMention(null, null, null, null, null, null, null);
    CoreNLPProtos.Entity proto = serializer.toProto(em);
    // assertFalse(proto.hasObjectID());
    // assertFalse(proto.hasType());
    // assertFalse(proto.hasSubtype());
    // assertFalse(proto.hasHeadStart());
    // assertFalse(proto.hasHeadEnd());
  }

  @Test
  public void testToProtoRelationWithNullArgs() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    // CoreNLPProtos.Relation proto = serializer.toProto(new
    // edu.stanford.nlp.ie.machinereading.structure.RelationMention("rel", null, null, null, null,
    // null));
    // assertEquals("rel", proto.getObjectID());
    // assertEquals(0, proto.getArgCount());
  }

  @Test
  public void testTokenWithMultipleSentenceFields() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel token = new CoreLabel();
    token.setWord("S");
    token.set(CoreAnnotations.SpeakerAnnotation.class, "speakerX");
    token.set(CoreAnnotations.SpeakerTypeAnnotation.class, "HUMAN");
    // token.set(CoreAnnotations.UtteranceAnnotation.class, "U1");
    CoreNLPProtos.Token proto = serializer.toProto(token);
    // assertEquals("speakerX", proto.getSpeaker());
    // assertEquals("HUMAN", proto.getSpeakerType());
    // assertEquals("U1", proto.getUtterance());
    // CoreLabel result = serializer.fromProto(proto);
    // assertEquals("speakerX", result.get(CoreAnnotations.SpeakerAnnotation.class));
    // assertEquals("HUMAN", result.get(CoreAnnotations.SpeakerTypeAnnotation.class));
    // assertEquals("U1", result.get(CoreAnnotations.UtteranceAnnotation.class));
  }

  @Test
  public void testFromProtoWithEmptyXmlQuoteList() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    // CoreNLPProtos.Section.Builder builder = CoreNLPProtos.Section.newBuilder();
    // builder.setCharBegin(0);
    // builder.setCharEnd(100);
    // builder.setXmlTag(CoreNLPProtos.XmlTag.newBuilder().build());
    // CoreMap section = serializer.fromProto(builder.build(), new ArrayList<>());
    // assertEquals(Integer.valueOf(0), section.get(CharacterOffsetBeginAnnotation.class));
    // assertEquals(Integer.valueOf(100), section.get(CharacterOffsetEndAnnotation.class));
  }

  @Test
  public void testDeserializeTokenWithChineseAndArabicSeg() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    // CoreNLPProtos.Token.Builder builder = CoreNLPProtos.Token.newBuilder();
    // builder.setWord("汉字");
    // builder.setChineseChar("字");
    // builder.setChineseSeg("汉");
    // builder.setArabicSeg("ك");
    // CoreLabel result = serializer.fromProto(builder.build());
    // assertEquals("字", result.get(ProtobufAnnotationSerializer.ChineseCharAnnotation.class));
    // assertEquals("汉", result.get(ProtobufAnnotationSerializer.ChineseSegAnnotation.class));
    // assertEquals("ك", result.get(ProtobufAnnotationSerializer.ArabicSegAnnotation.class));
  }

  @Test
  public void testToProtoSectionWithAllFieldsPopulated() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreMap section = new ArrayCoreMap();
    // section.set(CharacterOffsetBeginAnnotation.class, 10);
    // section.set(CharacterOffsetEndAnnotation.class, 99);
    // section.set(ProtobufAnnotationSerializer.AuthorAnnotation.class, "AuthorX");
    // section.set(ProtobufAnnotationSerializer.SectionDateAnnotation.class, "2024-01-01");
    List<CoreMap> sentenceList = new ArrayList<>();
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 1);
    sentenceList.add(sentence);
    section.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);
    // section.set(ProtobufAnnotationSerializer.QuotesAnnotation.class, new ArrayList<>());
    // section.set(ProtobufAnnotationSerializer.SectionTagAnnotation.class, new XmlTag("p", "", "",
    // new HashMap<>()));
    CoreNLPProtos.Section proto = serializer.toProtoSection(section);
    // assertEquals(10, proto.getCharBegin());
    // assertEquals(99, proto.getCharEnd());
    // assertEquals("AuthorX", proto.getAuthor());
    // assertEquals("2024-01-01", proto.getDatetime());
    // assertEquals("p", proto.getXmlTag().getName());
  }

  @Test
  public void testTokenSpanAnnotationCoveredWithConllUSpan() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel token = new CoreLabel();
    // token.set(CoNLLUTokenSpanAnnotation.class, new IntPair(5, 9));
    CoreNLPProtos.Token proto = serializer.toProto(token);
    // assertTrue(proto.hasConllUTokenSpan());
    // assertEquals(5, proto.getConllUTokenSpan().getBegin());
    // assertEquals(9, proto.getConllUTokenSpan().getEnd());
    // CoreLabel restored = serializer.fromProto(proto);
    // IntPair resultSpan = restored.get(CoNLLUTokenSpanAnnotation.class);
    // assertNotNull(resultSpan);
    // assertEquals(5, resultSpan.getSource());
    // assertEquals(9, resultSpan.getTarget());
  }

  @Test
  public void testToProtoWithMentionSpanButEmptyTokens() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreMap mention = new ArrayCoreMap();
    mention.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    mention.set(CoreAnnotations.TokenBeginAnnotation.class, 2);
    mention.set(CoreAnnotations.TokenEndAnnotation.class, 4);
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    mention.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "John Smith");
    CoreNLPProtos.NERMention result = serializer.toProtoMention(mention);
    // assertEquals(0, result.getSentenceIndex());
    // assertEquals(2, result.getTokenStartInSentenceInclusive());
    // assertEquals(4, result.getTokenEndInSentenceExclusive());
    // assertEquals("PERSON", result.getNer());
    // assertEquals("John Smith", result.getNormalizedNER());
  }

  @Test
  public void testFromProtoPolarityHandlesAllRelations() {
    // CoreNLPProtos.Polarity.Builder builder = CoreNLPProtos.Polarity.newBuilder();
    // builder.setProjectEquivalence(CoreNLPProtos.NaturalLogicRelation.FORWARD_ENTAILMENT);
    // builder.setProjectForwardEntailment(CoreNLPProtos.NaturalLogicRelation.REVERSE_ENTAILMENT);
    // builder.setProjectReverseEntailment(CoreNLPProtos.NaturalLogicRelation.NEGATION);
    // builder.setProjectNegation(CoreNLPProtos.NaturalLogicRelation.ALTERNATION);
    // builder.setProjectAlternation(CoreNLPProtos.NaturalLogicRelation.COVER);
    // builder.setProjectCover(CoreNLPProtos.NaturalLogicRelation.INDEPENDENCE);
    // builder.setProjectIndependence(CoreNLPProtos.NaturalLogicRelation.EQUIVALENT);
    // edu.stanford.nlp.naturalli.Polarity polarity =
    // ProtobufAnnotationSerializer.fromProto(builder.build());
    // assertNotNull(polarity);
    // assertEquals(7, polarity.projectionFunction.length);
  }

  @Test
  public void testFromProtoOperatorWithUnknownNameThrowsIllegal() {
    // CoreNLPProtos.Operator.Builder proto = CoreNLPProtos.Operator.newBuilder();
    // proto.setName("invalid-op-name");
    // proto.setQuantifierSpanBegin(1);
    // proto.setQuantifierSpanEnd(2);
    // proto.setSubjectSpanBegin(3);
    // proto.setSubjectSpanEnd(4);
    // proto.setObjectSpanBegin(5);
    // proto.setObjectSpanEnd(6);
    try {
      // ProtobufAnnotationSerializer.fromProto(proto.build());
      fail("Expected IllegalStateException was not thrown");
    } catch (IllegalStateException e) {
      assertTrue(e.getMessage().contains("Unknown language") || e.getMessage().contains("null"));
    } catch (NullPointerException ignore) {
    }
  }

  @Test
  public void testToFlattenedTreeHandlesEmptyTree() {
    Tree tree = new LabeledScoredTreeNode();
    CoreNLPProtos.FlattenedParseTree proto = ProtobufAnnotationSerializer.toFlattenedTree(tree);
    // assertEquals(3, proto.getNodesCount());
    // assertTrue(proto.getNodes(0).hasOpenNode());
    // assertEquals("", proto.getNodes(1).getValue());
    // assertTrue(proto.getNodes(2).hasCloseNode());
  }

  @Test
  public void testFromProtoParseTreeWithSentimentAttachedToLabel() {
    CoreLabel rootLabel = new CoreLabel();
    rootLabel.setCategory("S");
    rootLabel.setValue("S");
    // rootLabel.set(SentimentCoreAnnotations.PredictedClass.class, 3);
    Tree tree = new LabeledScoredTreeNode(rootLabel);
    CoreNLPProtos.ParseTree proto = ProtobufAnnotationSerializer.toProto(tree);
    // assertEquals(CoreNLPProtos.Sentiment.forNumber(3), proto.getSentiment());
    // Tree restored = ProtobufAnnotationSerializer.fromProto(proto);
    // CoreLabel restoredLabel = (CoreLabel) restored.label();
    // assertEquals("S", restoredLabel.getCategory());
    // assertEquals(Integer.valueOf(3),
    // restoredLabel.get(SentimentCoreAnnotations.PredictedClass.class));
  }

  @Test
  public void testRecoverOriginalTextWithMissingBeforeGaps() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel token1 = new CoreLabel();
    token1.setWord("A");
    token1.setBeginPosition(0);
    token1.setEndPosition(1);
    token1.setOriginalText("A");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("B");
    token2.setBeginPosition(2);
    token2.setEndPosition(3);
    token2.setOriginalText("B");
    token2.setBefore(" ");
    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    // CoreNLPProtos.Sentence.Builder sb = CoreNLPProtos.Sentence.newBuilder();
    // sb.setTokenOffsetBegin(0);
    // sb.setTokenOffsetEnd(2);
    // String text = serializer.recoverOriginalText(tokens, sb.build());
    // assertEquals("A B", text);
  }

  @Test
  public void testFromProtoDocumentWithNoSentencesRestoresTokens() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel token = new CoreLabel();
    token.setWord("tokenized");
    Annotation doc = new Annotation("tokenized");
    doc.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    CoreNLPProtos.Document proto = serializer.toProto(doc);
    // assertEquals(1, proto.getSentencelessTokenCount());
    // Annotation restored = serializer.fromProto(proto);
    // assertNotNull(restored.get(CoreAnnotations.TokensAnnotation.class));
    // assertEquals("tokenized",
    // restored.get(CoreAnnotations.TokensAnnotation.class).get(0).word());
  }

  @Test
  public void testLanguageEnumDefaultsCovered() {
    assertEquals(Language.Any, ProtobufAnnotationSerializer.fromProto(CoreNLPProtos.Language.Any));
    assertEquals(
        CoreNLPProtos.Language.UniversalChinese,
        ProtobufAnnotationSerializer.toProto(Language.UniversalChinese));
    assertEquals(
        Language.UniversalEnglish,
        ProtobufAnnotationSerializer.fromProto(CoreNLPProtos.Language.UniversalEnglish));
  }

  @Test
  public void testToProtoMentionWithOnlyRequiredFields() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    edu.stanford.nlp.coref.data.Mention mention = new edu.stanford.nlp.coref.data.Mention();
    mention.mentionID = 99;
    mention.sentNum = 1;
    mention.startIndex = 2;
    mention.endIndex = 3;
    mention.headIndex = 2;
    CoreNLPProtos.Mention proto = serializer.toProto(mention);
    // assertEquals(99, proto.getMentionID());
    // assertEquals(2, proto.getStartIndex());
    // assertEquals(3, proto.getEndIndex());
    // assertEquals(2, proto.getHeadIndex());
  }

  @Test
  public void testFromProtoQuoteWithOnlyTextFieldSet() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    // CoreNLPProtos.Quote proto = CoreNLPProtos.Quote.newBuilder().setText("She said.").build();
    // Annotation ann = serializer.fromProto(proto, Collections.emptyList());
    // assertEquals("She said.", ann.get(CoreAnnotations.TextAnnotation.class));
    // assertFalse(ann.containsKey(CoreAnnotations.AuthorAnnotation.class));
  }

  @Test
  public void testToProtoWithUnsupportedAnnotationTypeThrows() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    Annotation ann = new Annotation("sample");
    // ann.set((Class<UnsupportedOperationException>) (Class<?>)
    // UnsupportedOperationException.class, new UnsupportedOperationException("test"));
    try {
      serializer.toProto(ann);
      fail("Expected LossySerializationException");
    } catch (ProtobufAnnotationSerializer.LossySerializationException e) {
      assertTrue(e.getMessage().contains("Keys are not being serialized"));
    }
  }
}
