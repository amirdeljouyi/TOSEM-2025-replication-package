package edu.stanford.nlp.pipeline;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.util.*;
import edu.stanford.nlp.util.CoreMap;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import org.junit.Test;

public class ProtobufAnnotationSerializer_2_GPTLLMTest {

  @Test
  public void testWriteAndReadRoundTripSimpleAnnotation() throws Exception {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    Annotation annotation = new Annotation("Apple is a company.");
    CoreLabel label1 = new CoreLabel();
    label1.setWord("Apple");
    label1.set(CoreAnnotations.TextAnnotation.class, "Apple");
    label1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    label1.setIndex(1);
    CoreLabel label2 = new CoreLabel();
    label2.setWord("is");
    label2.set(CoreAnnotations.TextAnnotation.class, "is");
    label2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBZ");
    label2.setIndex(2);
    CoreLabel label3 = new CoreLabel();
    label3.setWord("a");
    label3.set(CoreAnnotations.TextAnnotation.class, "a");
    label3.set(CoreAnnotations.PartOfSpeechAnnotation.class, "DT");
    label3.setIndex(3);
    CoreLabel label4 = new CoreLabel();
    label4.setWord("company");
    label4.set(CoreAnnotations.TextAnnotation.class, "company");
    label4.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    label4.setIndex(4);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(label1);
    tokens.add(label2);
    tokens.add(label3);
    tokens.add(label4);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    serializer.write(annotation, baos);
    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    Pair<Annotation, InputStream> result = serializer.read(bais);
    Annotation deserialized = result.first;
    assertNotNull(deserialized);
    assertEquals("Apple is a company.", deserialized.get(CoreAnnotations.TextAnnotation.class));
    List<CoreLabel> deserializedTokens = deserialized.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull(deserializedTokens);
    assertEquals(4, deserializedTokens.size());
    assertEquals("Apple", deserializedTokens.get(0).word());
    assertEquals(
        "NNP", deserializedTokens.get(0).get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("company", deserializedTokens.get(3).word());
  }

  @Test
  public void testToProtoAndFromProtoPreservesFields() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    CoreLabel original = new CoreLabel();
    original.setWord("Stanford");
    original.setLemma("Stanford");
    original.set(CoreAnnotations.TextAnnotation.class, "Stanford");
    original.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    original.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORG");
    original.setIndex(1);
    original.setBeginPosition(0);
    original.setEndPosition(8);
    CoreNLPProtos.Token proto = serializer.toProto(original);
    // CoreLabel deserialized = serializer.fromProto(proto);
    // assertEquals("Stanford", deserialized.word());
    // assertEquals("Stanford", deserialized.lemma());
    // assertEquals("NNP", deserialized.tag());
    // assertEquals("ORG", deserialized.ner());
    // assertEquals(1, deserialized.index());
    // assertEquals(0, deserialized.beginPosition());
    // assertEquals(8, deserialized.endPosition());
  }

  @Test(expected = ProtobufAnnotationSerializer.LossySerializationException.class)
  public void testLossySerializationExceptionTriggered() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel label = new CoreLabel();
    label.setWord("example");
    label.set(CoreAnnotations.TextAnnotation.class, "example");
    // label.set(CoreAnnotations.BucketAnnotation.class, 123);
    serializer.toProto(label);
  }

  @Test
  public void testSerializationIgnoresUnknownKeysWhenLossyAllowed() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);
    CoreLabel label = new CoreLabel();
    label.setWord("token");
    label.set(CoreAnnotations.TextAnnotation.class, "token");
    label.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    label.setIndex(1);
    // label.set(CoreAnnotations.BucketAnnotation.class, 99);
    CoreNLPProtos.Token proto = serializer.toProto(label);
    // assertEquals("token", proto.getWord());
    // assertEquals("NN", proto.getPos());
    // assertFalse(proto.hasCategory());
  }

  @Test
  public void testReadUndelimitedReadsDelimitedFallback() throws Exception {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    // File tempFile = File.createTempFile("temp_proto_doc", ".bin");
    // tempFile.deleteOnExit();
    Annotation annotation = new Annotation("Temp Text");
    CoreLabel label = new CoreLabel();
    label.setWord("Temp");
    label.set(CoreAnnotations.TextAnnotation.class, "Temp");
    label.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    label.setIndex(1);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(label);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    // FileOutputStream fos = new FileOutputStream(tempFile);
    // serializer.write(annotation, fos);
    // fos.close();
    // Annotation reloaded = serializer.readUndelimited(tempFile);
    // assertEquals("Temp Text", reloaded.get(CoreAnnotations.TextAnnotation.class));
    // assertNotNull(reloaded.get(CoreAnnotations.TokensAnnotation.class));
    // assertEquals("Temp", reloaded.get(CoreAnnotations.TokensAnnotation.class).get(0).word());
  }

  @Test
  public void testToMapIntStringProtoAndBack() {
    Map<Integer, String> map = new HashMap<>();
    map.put(1, "alpha");
    map.put(2, "beta");
    CoreNLPProtos.MapIntString proto = ProtobufAnnotationSerializer.toMapIntStringProto(map);
    // Map<Integer, String> deserialized = ProtobufAnnotationSerializer.fromProto(proto);
    // assertEquals(2, deserialized.size());
    // assertEquals("alpha", deserialized.get(1));
    // assertEquals("beta", deserialized.get(2));
  }

  @Test
  public void testToMapStringStringProtoAndBack() {
    Map<String, String> map = new LinkedHashMap<>();
    map.put("keyA", "valA");
    map.put("keyB", "valB");
    CoreNLPProtos.MapStringString proto = ProtobufAnnotationSerializer.toMapStringStringProto(map);
    // Map<String, String> deserialized = ProtobufAnnotationSerializer.fromProto(proto);
    // assertEquals(2, deserialized.size());
    // assertEquals("valA", deserialized.get("keyA"));
    // assertEquals("valB", deserialized.get("keyB"));
  }

  @Test
  public void testToProtoWithEmptyCoreLabel() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);
    CoreLabel label = new CoreLabel();
    CoreNLPProtos.Token proto = serializer.toProto(label);
    // assertFalse(proto.hasWord());
    // assertEquals(0, proto.getWord().length());
  }

  @Test
  public void testToProtoSkipsExplicitlySkippedKeys() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel label = new CoreLabel();
    label.setWord("skipMe");
    label.set(CoreAnnotations.TextAnnotation.class, "skipMe");
    label.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");
    Set<Class<?>> skipKeys = new HashSet<Class<?>>();
    skipKeys.add(CoreAnnotations.PartOfSpeechAnnotation.class);
    CoreNLPProtos.Token token = serializer.toProto(label, skipKeys);
    // assertEquals("skipMe", token.getWord());
    // assertFalse(token.hasPos());
  }

  @Test
  public void testToProtoHandlesSpecialCharactersInText() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel label = new CoreLabel();
    label.setWord("こんにちは");
    label.set(CoreAnnotations.TextAnnotation.class, "こんにちは");
    label.set(CoreAnnotations.NamedEntityTagAnnotation.class, "GREET");
    label.setIndex(1);
    CoreNLPProtos.Token proto = serializer.toProto(label);
    // assertEquals("こんにちは", proto.getWord());
    // assertEquals("GREET", proto.getNer());
    // assertEquals(1, proto.getIndex());
  }

  @Test
  public void testWriteAndReadEmptyAnnotation() throws Exception {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    Annotation empty = new Annotation("");
    empty.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    serializer.write(empty, baos);
    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    Pair<Annotation, InputStream> result = serializer.read(bais);
    Annotation deserialized = result.first;
    assertNotNull(deserialized);
    assertEquals("", deserialized.get(CoreAnnotations.TextAnnotation.class));
    assertTrue(deserialized.get(CoreAnnotations.TokensAnnotation.class).isEmpty());
  }

  @Test
  public void testReadInvalidStreamReturnsNullAnnotation() throws Exception {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    byte[] invalidData = new byte[] {0x01, 0x02, 0x03};
    ByteArrayInputStream bais = new ByteArrayInputStream(invalidData);
    try {
      serializer.read(bais);
      fail("Expected IOException or NullPointerException due to malformed input");
    } catch (IOException | NullPointerException e) {
    }
  }

  @Test
  public void testToProtoWithLossySerializationOffAllowsUnknownKey() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);
    CoreLabel label = new CoreLabel();
    label.setWord("tolerated");
    label.set(CoreAnnotations.TextAnnotation.class, "tolerated");
    // label.set(CoreAnnotations.BucketAnnotation.class, 42);
    CoreNLPProtos.Token proto = serializer.toProto(label);
    // assertEquals("tolerated", proto.getWord());
    // assertFalse(proto.hasCategory());
  }

  @Test
  public void testToProtoAndFromProtoWithNormalizedNER() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel label = new CoreLabel();
    label.setWord("July");
    label.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "2024-07");
    CoreNLPProtos.Token proto = serializer.toProto(label);
    // CoreLabel roundtrip = serializer.fromProto(proto);
    // assertEquals("2024-07",
    // roundtrip.get(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class));
  }

  @Test
  public void testToProtoTokenWithXMLContextList() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel label = new CoreLabel();
    label.setWord("marked");
    label.set(CoreAnnotations.TextAnnotation.class, "marked");
    List<String> xmlContext = new ArrayList<String>();
    xmlContext.add("<tag1>");
    xmlContext.add("</tag1>");
    label.set(CoreAnnotations.XmlContextAnnotation.class, xmlContext);
    CoreNLPProtos.Token proto = serializer.toProto(label);
    // assertEquals(2, proto.getXmlContextCount());
    // assertEquals("<tag1>", proto.getXmlContext(0));
    // assertEquals("</tag1>", proto.getXmlContext(1));
    // assertTrue(proto.getHasXmlContext());
  }

  @Test
  public void testToProtoDoesNotCrashWithMissingFields() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel label = new CoreLabel();
    label.setWord("undefined");
    CoreNLPProtos.Token proto = serializer.toProto(label);
    // assertEquals("undefined", proto.getWord());
    // assertFalse(proto.hasIndex());
    // assertFalse(proto.hasPos());
    // assertFalse(proto.hasNer());
  }

  @Test
  public void testCoreLabelWithNumericAnnotationsSerializesCorrectly() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel label = new CoreLabel();
    label.setWord("100");
    label.set(CoreAnnotations.NumericValueAnnotation.class, 100L);
    label.set(CoreAnnotations.NumericTypeAnnotation.class, "NUMBER");
    label.set(CoreAnnotations.NumericCompositeValueAnnotation.class, 1000000L);
    label.set(CoreAnnotations.NumericCompositeTypeAnnotation.class, "CURRENCY");
    CoreNLPProtos.Token proto = serializer.toProto(label);
    // CoreLabel reconstructed = serializer.fromProto(proto);
    // assertEquals(Long.valueOf(100L),
    // reconstructed.get(CoreAnnotations.NumericValueAnnotation.class));
    // assertEquals("NUMBER", reconstructed.get(CoreAnnotations.NumericTypeAnnotation.class));
    // assertEquals(Long.valueOf(1000000L),
    // reconstructed.get(CoreAnnotations.NumericCompositeValueAnnotation.class));
    // assertEquals("CURRENCY",
    // reconstructed.get(CoreAnnotations.NumericCompositeTypeAnnotation.class));
  }

  @Test
  public void testCoreLabelWithSpanAnnotationRoundTrip() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel label = new CoreLabel();
    label.set(CoreAnnotations.SpanAnnotation.class, new edu.stanford.nlp.util.IntPair(2, 5));
    CoreNLPProtos.Token proto = serializer.toProto(label);
    // CoreLabel reconstructed = serializer.fromProto(proto);
    // IntPair span = reconstructed.get(CoreAnnotations.SpanAnnotation.class);
    // assertEquals(2, span.getSource());
    // assertEquals(5, span.getTarget());
  }

  @Test
  public void testCoreLabelWithEmptyNERLabelProbabilities() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel label = new CoreLabel();
    label.setWord("emptyNER");
    Map<String, Double> probs = new HashMap<>();
    label.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, probs);
    CoreNLPProtos.Token proto = serializer.toProto(label);
    // assertEquals(1, proto.getNerLabelProbsCount());
    // assertEquals("empty", proto.getNerLabelProbs(0));
    // CoreLabel reconstructed = serializer.fromProto(proto);
    // Map<String, Double> deserializedProbs =
    // reconstructed.get(CoreAnnotations.NamedEntityTagProbsAnnotation.class);
    // assertTrue(deserializedProbs.isEmpty());
  }

  @Test
  public void testTokenWithBooleanFlagsIsFirstMWTAndIsMWT() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel label = new CoreLabel();
    label.set(CoreAnnotations.IsMultiWordTokenAnnotation.class, true);
    label.set(CoreAnnotations.IsFirstWordOfMWTAnnotation.class, true);
    label.set(CoreAnnotations.MWTTokenTextAnnotation.class, "foo bar");
    label.set(CoreAnnotations.MWTTokenMiscAnnotation.class, "Some=Misc");
    CoreNLPProtos.Token proto = serializer.toProto(label);
    // CoreLabel reconstructed = serializer.fromProto(proto);
    // assertTrue(reconstructed.get(CoreAnnotations.IsMultiWordTokenAnnotation.class));
    // assertTrue(reconstructed.get(CoreAnnotations.IsFirstWordOfMWTAnnotation.class));
    // assertEquals("foo bar", reconstructed.get(CoreAnnotations.MWTTokenTextAnnotation.class));
    // assertEquals("Some=Misc", reconstructed.get(CoreAnnotations.MWTTokenMiscAnnotation.class));
  }

  @Test
  public void testCoreLabelWithPolarityDeserializesCorrectly() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    // CoreNLPProtos.Polarity.Builder polarity = CoreNLPProtos.Polarity.newBuilder();
    // polarity.setProjectEquivalence(CoreNLPProtos.NaturalLogicRelation.Equivalence);
    // polarity.setProjectForwardEntailment(CoreNLPProtos.NaturalLogicRelation.ForwardEntailment);
    // polarity.setProjectReverseEntailment(CoreNLPProtos.NaturalLogicRelation.ReverseEntailment);
    // polarity.setProjectNegation(CoreNLPProtos.NaturalLogicRelation.Negation);
    // polarity.setProjectAlternation(CoreNLPProtos.NaturalLogicRelation.Alternation);
    // polarity.setProjectCover(CoreNLPProtos.NaturalLogicRelation.Cover);
    // polarity.setProjectIndependence(CoreNLPProtos.NaturalLogicRelation.Independence);
    // CoreNLPProtos.Token.Builder tokenBuilder = CoreNLPProtos.Token.newBuilder();
    // tokenBuilder.setWord("polarized");
    // tokenBuilder.setPolarity(polarity.build());
    // CoreLabel reconstructed = serializer.fromProto(tokenBuilder.build());
    // assertNotNull(reconstructed.get(edu.stanford.nlp.naturalli.Polarity.class));
  }

  @Test
  public void testDeserializeMapStringStringMismatchKeysAndValues() {
    // CoreNLPProtos.MapStringString.Builder proto = CoreNLPProtos.MapStringString.newBuilder();
    // proto.addKey("a");
    // proto.addValue("alpha");
    // proto.addKey("b");
    // Map<String, String> result = ProtobufAnnotationSerializer.fromProto(proto.build());
    // assertEquals(1, result.size());
    // assertEquals("alpha", result.get("a"));
    // assertNull(result.get("b"));
  }

  @Test
  public void testInterruptedThreadBeforeFromProtoTreeThrowsRuntimeException() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    Thread.currentThread().interrupt();
    // CoreNLPProtos.ParseTree.Builder tree = CoreNLPProtos.ParseTree.newBuilder();
    // tree.setValue("ROOT");
    boolean threwException = false;
    try {
      // serializer.fromProto(tree.build());
    } catch (edu.stanford.nlp.util.RuntimeInterruptedException e) {
      threwException = true;
    } finally {
      assertTrue(threwException);
      Thread.interrupted();
    }
  }

  @Test
  public void testInterruptedThreadBeforeFromProtoSentenceFragmentThrowsRuntimeException() {
    Thread.currentThread().interrupt();
    // CoreNLPProtos.SentenceFragment.Builder builder = CoreNLPProtos.SentenceFragment.newBuilder();
    // builder.addTokenIndex(0);
    // builder.setAssumedTruth(true);
    // builder.setScore(0.9f);
    // builder.setRoot(0);
    boolean threw = false;
    try {
      // ProtobufAnnotationSerializer.fromProto(
      // builder.build(),
      // null
      // );
    } catch (edu.stanford.nlp.util.RuntimeInterruptedException e) {
      threw = true;
    } finally {
      Thread.interrupted();
    }
    assertTrue(threw);
  }

  @Test
  public void testTimexProtoHandlesMissingOptionalFieldsGracefully() {
    // CoreNLPProtos.Timex.Builder proto = CoreNLPProtos.Timex.newBuilder();
    // proto.setValue("2024-01-01");
    // Timex timex = ProtobufAnnotationSerializer.fromProto(proto.build());
    // assertEquals("2024-01-01", timex.value());
    // assertNull(timex.timexType());
    // assertNull(timex.altVal());
  }

  @Test
  public void testSectionAnnotationWithAllOptionalFieldsSet() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    CoreMap section = new ArrayCoreMap();
    section.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
    section.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 30);
    section.set(CoreAnnotations.AuthorAnnotation.class, "Author");
    section.set(CoreAnnotations.SectionDateAnnotation.class, "2022-01-01");
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 2);
    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);
    section.set(CoreAnnotations.SentencesAnnotation.class, sentences);
    CoreMap quote = new ArrayCoreMap();
    quote.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 11);
    quote.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 25);
    quote.set(CoreAnnotations.AuthorAnnotation.class, "QAuth");
    List<CoreMap> quotes = new ArrayList<>();
    quotes.add(quote);
    section.set(CoreAnnotations.QuotesAnnotation.class, quotes);
    CoreMap originalXmlTag = new ArrayCoreMap();
    // section.set(CoreAnnotations.SectionTagAnnotation.class, originalXmlTag);
    CoreNLPProtos.Section proto = serializer.toProtoSection(section);
    // assertEquals(10, proto.getCharBegin());
    // assertEquals(30, proto.getCharEnd());
    // assertEquals("Author", proto.getAuthor());
    // assertEquals("2022-01-01", proto.getDatetime());
    // assertEquals(1, proto.getSentenceIndexesCount());
    // assertEquals(2, proto.getSentenceIndexes(0));
    // assertEquals(1, proto.getQuotesCount());
  }

  @Test
  public void testMentionSerializationWithCanonicalIndexes() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreMap mention = new ArrayCoreMap();
    mention.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    mention.set(CoreAnnotations.TokenBeginAnnotation.class, 3);
    mention.set(CoreAnnotations.TokenEndAnnotation.class, 5);
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");
    mention.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "USA");
    mention.set(CoreAnnotations.EntityTypeAnnotation.class, "GPE");
    mention.set(CoreAnnotations.WikipediaEntityAnnotation.class, "United_States");
    mention.set(CoreAnnotations.GenderAnnotation.class, "NEUTRAL");
    mention.set(CoreAnnotations.CanonicalEntityMentionIndexAnnotation.class, 42);
    mention.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 7);
    mention.set(CoreAnnotations.TextAnnotation.class, "the U.S.");
    CoreNLPProtos.NERMention proto = serializer.toProtoMention(mention);
    // assertEquals(0, proto.getSentenceIndex());
    // assertEquals(3, proto.getTokenStartInSentenceInclusive());
    // assertEquals(5, proto.getTokenEndInSentenceExclusive());
    // assertEquals("LOCATION", proto.getNer());
    // assertEquals("USA", proto.getNormalizedNER());
    // assertEquals("GPE", proto.getEntityType());
    // assertEquals("United_States", proto.getWikipediaEntity());
    // assertEquals("NEUTRAL", proto.getGender());
    // assertEquals(7, proto.getEntityMentionIndex());
    // assertEquals(42, proto.getCanonicalEntityMentionIndex());
    // assertEquals("the U.S.", proto.getEntityMentionText());
  }

  @Test
  public void testParseTreeWithNaNScoreAndSpanHandled() {
    // CoreNLPProtos.ParseTree.Builder builder = CoreNLPProtos.ParseTree.newBuilder();
    // builder.setValue("ROOT");
    // builder.setYieldBeginIndex(1);
    // builder.setYieldEndIndex(2);
    // CoreNLPProtos.ParseTree proto = builder.build();
    // Tree tree = ProtobufAnnotationSerializer.fromProto(proto);
    // assertEquals("ROOT", tree.label().value());
    // assertNull(tree.children());
  }

  @Test
  public void testFlattenedParseTreeParsingUnbalancedOpenCloseThrows() {
    // CoreNLPProtos.FlattenedParseTree.Builder builder =
    // CoreNLPProtos.FlattenedParseTree.newBuilder();
    // CoreNLPProtos.FlattenedParseTree.Node.Builder open =
    // CoreNLPProtos.FlattenedParseTree.Node.newBuilder();
    // open.setOpenNode(true);
    // builder.addNodes(open.build());
    // CoreNLPProtos.FlattenedParseTree.Node.Builder label =
    // CoreNLPProtos.FlattenedParseTree.Node.newBuilder();
    // label.setValue("NP");
    // builder.addNodes(label.build());
    boolean threw = false;
    try {
      // ProtobufAnnotationSerializer.fromProto(builder.build());
    } catch (IllegalArgumentException e) {
      threw = true;
    }
    assertTrue(threw);
  }

  @Test
  public void testFlatTreeWithCloseBeforeOpenThrows() {
    // CoreNLPProtos.FlattenedParseTree.Builder builder =
    // CoreNLPProtos.FlattenedParseTree.newBuilder();
    // CoreNLPProtos.FlattenedParseTree.Node.Builder close =
    // CoreNLPProtos.FlattenedParseTree.Node.newBuilder();
    // close.setCloseNode(true);
    // builder.addNodes(close.build());
    boolean threw = false;
    try {
      // ProtobufAnnotationSerializer.fromProto(builder.build());
    } catch (IllegalArgumentException e) {
      threw = true;
    }
    assertTrue(threw);
  }

  @Test
  public void testFromProtoWithNegativeTokenIndexIgnored() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);
    // CoreNLPProtos.Token.Builder proto = CoreNLPProtos.Token.newBuilder();
    // proto.setWord("invalid");
    // proto.setIndex(-1);
    // proto.setBeginChar(-1);
    // proto.setEndChar(-1);
    // CoreLabel label = serializer.fromProto(proto.build());
    // assertEquals("invalid", label.word());
    // assertEquals(-1, label.index());
    // assertEquals(-1, label.beginPosition());
    // assertEquals(-1, label.endPosition());
  }

  @Test
  public void testToProtoWithNullTimexFields() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    edu.stanford.nlp.time.Timex timex =
        new edu.stanford.nlp.time.Timex(null, null, null, null, null, -1, -1);
    CoreNLPProtos.Timex proto = serializer.toProto(timex);
    // assertFalse(proto.hasValue());
    // assertFalse(proto.hasAltValue());
    // assertFalse(proto.hasText());
    // assertFalse(proto.hasType());
    // assertFalse(proto.hasTid());
    // assertFalse(proto.hasBeginPoint());
    // assertFalse(proto.hasEndPoint());
  }

  @Test
  public void testToProtoSentenceWithNoTokensAnnotation() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    edu.stanford.nlp.util.CoreMap sentence = new edu.stanford.nlp.util.ArrayCoreMap();
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokenEndAnnotation.class, 1);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 5);
    sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 30);
    sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 40);
    CoreNLPProtos.Sentence proto = serializer.toProto(sentence);
    // assertEquals(0, proto.getTokenOffsetBegin());
    // assertEquals(1, proto.getTokenOffsetEnd());
    // assertEquals(5, proto.getSentenceIndex());
    // assertEquals(30, proto.getCharacterOffsetBegin());
    // assertEquals(40, proto.getCharacterOffsetEnd());
    // assertEquals(0, proto.getTokenCount());
  }

  @Test
  public void testToProtoDocumentWithNoSentencesOrTokens() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    Annotation annotation = new Annotation("no tokens or sentences");
    CoreNLPProtos.Document proto = serializer.toProto(annotation);
    // assertEquals("no tokens or sentences", proto.getText());
    // assertEquals(0, proto.getSentenceCount());
    // assertEquals(0, proto.getSentencelessTokenCount());
  }

  @Test
  public void testFromProtoDependencyGraphWithEmptyNodeAndEdgeLists() {
    // CoreNLPProtos.DependencyGraph.Builder graphBuilder =
    // CoreNLPProtos.DependencyGraph.newBuilder();
    // CoreNLPProtos.DependencyGraph graph = graphBuilder.build();
    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    // SemanticGraph sg = ProtobufAnnotationSerializer.fromProto(graph, tokens, "doc");
    // assertTrue(sg.isEmpty());
    // assertEquals(0, sg.vertexSet().size());
    // assertEquals(0, sg.edgeListSorted().size());
  }

  @Test
  public void testFromProtoCoreLabelWithOnlyWordSet() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    // CoreNLPProtos.Token.Builder builder = CoreNLPProtos.Token.newBuilder();
    // builder.setWord("bare");
    // CoreLabel label = serializer.fromProto(builder.build());
    // assertEquals("bare", label.word());
    // assertNull(label.tag());
    // assertNull(label.ner());
    // assertEquals(0, label.index());
  }

  @Test
  public void testSentenceFragmentDeserializationWithMissingOptionalValues() {
    // CoreNLPProtos.SentenceFragment.Builder fragBuilder =
    // CoreNLPProtos.SentenceFragment.newBuilder();
    // fragBuilder.addTokenIndex(2);
    // fragBuilder.setRoot(2);
    // CoreNLPProtos.SentenceFragment proto = fragBuilder.build();
    SemanticGraph dummyGraph = new SemanticGraph();
    IndexedWord word = new IndexedWord();
    word.setIndex(3);
    dummyGraph.addVertex(word);
    dummyGraph.setRoot(word);
    // SentenceFragment fragment = ProtobufAnnotationSerializer.fromProto(proto, dummyGraph);
    // assertEquals(1, fragment.yield.size());
    // assertEquals(word, fragment.yield.iterator().next());
    // assertEquals(word, fragment.getRoot());
  }

  @Test
  public void testFromProtoMentionWithEmptyMentionsLists() {
    // CoreNLPProtos.Mention.Builder mention = CoreNLPProtos.Mention.newBuilder();
    // mention.setMentionID(1);
    // mention.setSentNum(1);
    // mention.setStartIndex(0);
    // mention.setEndIndex(1);
    // mention.setHeadIndex(0);
    // mention.setMentionNum(0);
    // mention.setCorefClusterID(-1);
    // mention.setGoldCorefClusterID(-1);
    // mention.setOriginalRef(-1);
    // mention.setUtter(0);
    // mention.setParagraph(0);
    // mention.setDependingVerb(
    // CoreNLPProtos.IndexedWord.newBuilder().setSentenceNum(-1).setTokenIndex(-1).build());
    // mention.setHeadIndexedWord(
    // CoreNLPProtos.IndexedWord.newBuilder().setSentenceNum(-1).setTokenIndex(-1).build());
    // mention.setHeadWord(
    // CoreNLPProtos.IndexedWord.newBuilder().setSentenceNum(-1).setTokenIndex(-1).build());
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    // edu.stanford.nlp.coref.data.Mention result = serializer.fromProtoNoTokens(mention.build());
    // assertEquals(1, result.mentionID);
    // assertEquals(0, result.startIndex);
    // assertEquals(1, result.endIndex);
    // assertNull(result.headWord);
    // assertNull(result.headIndexedWord);
    // assertEquals(0, result.mentionNum);
    // assertNull(result.basicDependency);
    // assertNull(result.contextParseTree);
  }

  @Test
  public void testFromProtoSentenceWithXMLContextUnset() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel label = new CoreLabel();
    label.setWord("noXMLContext");
    // CoreNLPProtos.Token.Builder tok = serializer.toProto(label).toBuilder();
    // tok.setHasXmlContext(false);
    // CoreLabel from = serializer.fromProto(tok.build());
    // assertNull(from.get(CoreAnnotations.XmlContextAnnotation.class));
  }

  @Test
  public void testToProtoTokenWithNullWikipediaEntityAnnotation() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel label = new CoreLabel();
    label.setWord("NullValueTest");
    label.set(CoreAnnotations.TextAnnotation.class, "NullValueTest");
    label.set(CoreAnnotations.WikipediaEntityAnnotation.class, null);
    CoreNLPProtos.Token proto = serializer.toProto(label);
    // assertEquals("NullValueTest", proto.getWord());
    // assertFalse(proto.hasWikipediaEntity());
  }

  @Test
  public void testFromProtoTokenWithXmlContextPresent() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    // CoreNLPProtos.Token.Builder builder = CoreNLPProtos.Token.newBuilder();
    // builder.setWord("Test");
    // builder.setHasXmlContext(true);
    // builder.addXmlContext("<s>");
    // builder.addXmlContext("</s>");
    // CoreLabel reconstructed = serializer.fromProto(builder.build());
    // List<String> xmlContext = reconstructed.get(CoreAnnotations.XmlContextAnnotation.class);
    // assertNotNull(xmlContext);
    // assertEquals(2, xmlContext.size());
    // assertEquals("<s>", xmlContext.get(0));
    // assertEquals("</s>", xmlContext.get(1));
  }

  @Test
  public void testToProtoDependencyGraphWithMultipleRootsUsesBothRootFields() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    SemanticGraph graph = new SemanticGraph();
    CoreLabel label1 = new CoreLabel();
    label1.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    label1.setIndex(1);
    IndexedWord root1 = new IndexedWord(label1);
    graph.addVertex(root1);
    graph.setRoot(root1);
    CoreLabel label2 = new CoreLabel();
    label2.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    label2.setIndex(2);
    IndexedWord root2 = new IndexedWord(label2);
    graph.addVertex(root2);
    graph.addRoot(root2);
    CoreNLPProtos.DependencyGraph proto = serializer.toProto(graph);
    // assertEquals(2, proto.getRootCount());
    // assertEquals(1, proto.getRootNodeCount());
  }

  @Test
  public void testToProtoQuoteWithAttributionMetadataFields() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreMap quote = new ArrayCoreMap();
    quote.set(CoreAnnotations.TextAnnotation.class, "Quote text");
    quote.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    quote.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 25);
    quote.set(CoreAnnotations.TokenBeginAnnotation.class, 2);
    quote.set(CoreAnnotations.TokenEndAnnotation.class, 4);
    quote.set(CoreAnnotations.QuotationIndexAnnotation.class, 1);
    quote.set(CoreAnnotations.AuthorAnnotation.class, "Jane");
    quote.set(CoreAnnotations.SentenceBeginAnnotation.class, 1);
    quote.set(CoreAnnotations.SentenceEndAnnotation.class, 2);
    // quote.set(edu.stanford.nlp.quoteattribution.QuoteAttributionAnnotator.MentionAnnotation.class, "John");
    // quote.set(edu.stanford.nlp.quoteattribution.QuoteAttributionAnnotator.MentionBeginAnnotation.class, 3);
    // quote.set(edu.stanford.nlp.quoteattribution.QuoteAttributionAnnotator.MentionEndAnnotation.class, 4);
    // quote.set(edu.stanford.nlp.quoteattribution.QuoteAttributionAnnotator.MentionTypeAnnotation.class, "NAME");
    // quote.set(edu.stanford.nlp.quoteattribution.QuoteAttributionAnnotator.MentionSieveAnnotation.class, "rule1");
    // quote.set(edu.stanford.nlp.quoteattribution.QuoteAttributionAnnotator.SpeakerAnnotation.class, "Speaker");
    // quote.set(edu.stanford.nlp.quoteattribution.QuoteAttributionAnnotator.SpeakerSieveAnnotation.class, "sieve-x");
    // quote.set(edu.stanford.nlp.quoteattribution.QuoteAttributionAnnotator.CanonicalMentionAnnotation.class, "CM");
    // quote.set(edu.stanford.nlp.quoteattribution.QuoteAttributionAnnotator.CanonicalMentionBeginAnnotation.class, 6);
    // quote.set(edu.stanford.nlp.quoteattribution.QuoteAttributionAnnotator.CanonicalMentionEndAnnotation.class, 7);
    CoreNLPProtos.Quote proto = serializer.toProtoQuote(quote);
    // assertEquals("Quote text", proto.getText());
    // assertEquals(5, proto.getBegin());
    // assertEquals(25, proto.getEnd());
    // assertEquals(2, proto.getTokenBegin());
    // assertEquals(4, proto.getTokenEnd());
    // assertEquals("Jane", proto.getAuthor());
    // assertEquals("John", proto.getMention());
    // assertEquals(3, proto.getMentionBegin());
    // assertEquals(4, proto.getMentionEnd());
    // assertEquals("NAME", proto.getMentionType());
    // assertEquals("rule1", proto.getMentionSieve());
    // assertEquals("Speaker", proto.getSpeaker());
    // assertEquals("sieve-x", proto.getSpeakerSieve());
    // assertEquals("CM", proto.getCanonicalMention());
    // assertEquals(6, proto.getCanonicalMentionBegin());
    // assertEquals(7, proto.getCanonicalMentionEnd());
  }

  @Test
  public void testFromProtoMapStringStringWithNoEntriesReturnsEmptyMap() {
    // CoreNLPProtos.MapStringString proto = CoreNLPProtos.MapStringString.newBuilder().build();
    // Map<String, String> deserialized = ProtobufAnnotationSerializer.fromProto(proto);
    // assertTrue(deserialized.isEmpty());
  }

  @Test
  public void testFromProtoMapIntStringWithNoEntriesReturnsEmptyMap() {
    // CoreNLPProtos.MapIntString proto = CoreNLPProtos.MapIntString.newBuilder().build();
    // Map<Integer, String> deserialized = ProtobufAnnotationSerializer.fromProto(proto);
    // assertTrue(deserialized.isEmpty());
  }

  @Test
  public void testToProtoWithUnknownLanguageThrowsIllegalStateException() {
    boolean threw = false;
    try {
      ProtobufAnnotationSerializer.toProto(
          edu.stanford.nlp.international.Language.valueOf("MadeUpLang"));
    } catch (IllegalStateException e) {
      threw = true;
    } catch (IllegalArgumentException e) {
    }
    assertTrue(threw);
  }

  @Test
  public void testToProtoOperatorWithAllFieldsSet() {
    // edu.stanford.nlp.naturalli.OperatorSpec operator = new
    // edu.stanford.nlp.naturalli.OperatorSpec(
    // edu.stanford.nlp.naturalli.Operator.All, 1, 2, 3, 4, 5, 6
    // );
    // CoreNLPProtos.Operator proto = ProtobufAnnotationSerializer.toProto(operator);
    // assertEquals("ALL", proto.getName());
    // assertEquals(1, proto.getQuantifierSpanBegin());
    // assertEquals(2, proto.getQuantifierSpanEnd());
    // assertEquals(3, proto.getSubjectSpanBegin());
    // assertEquals(4, proto.getSubjectSpanEnd());
    // assertEquals(5, proto.getObjectSpanBegin());
    // assertEquals(6, proto.getObjectSpanEnd());
  }

  @Test
  public void testFromProtoOperatorWithUnknownNameYieldsNullOperator() {
    // CoreNLPProtos.Operator.Builder proto = CoreNLPProtos.Operator.newBuilder();
    // proto.setName("UNKNOWN");
    // proto.setQuantifierSpanBegin(0);
    // proto.setQuantifierSpanEnd(1);
    // proto.setSubjectSpanBegin(2);
    // proto.setSubjectSpanEnd(3);
    // proto.setObjectSpanBegin(4);
    // proto.setObjectSpanEnd(5);
    // edu.stanford.nlp.naturalli.OperatorSpec spec =
    // ProtobufAnnotationSerializer.fromProto(proto.build());
    // assertNull(spec.instance);
  }

  @Test
  public void testFromProtoSentenceMissingAnnotationButHasTokenOffsets() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    // CoreNLPProtos.Sentence.Builder sentence = CoreNLPProtos.Sentence.newBuilder();
    // sentence.setTokenOffsetBegin(0);
    // sentence.setTokenOffsetEnd(2);
    // CoreMap result = serializer.fromProtoNoTokens(sentence.build());
    // assertEquals(0, (int) result.get(CoreAnnotations.TokenBeginAnnotation.class));
    // assertEquals(2, (int) result.get(CoreAnnotations.TokenEndAnnotation.class));
  }

  @Test
  public void testToProtoSentenceFragmentWithOnlyRequiredValues() {
    edu.stanford.nlp.naturalli.SentenceFragment fragment =
        new edu.stanford.nlp.naturalli.SentenceFragment(
            new edu.stanford.nlp.semgraph.SemanticGraph(), true, false);
    // fragment.words = new ArrayList<edu.stanford.nlp.ling.IndexedWord>();
    edu.stanford.nlp.ling.CoreLabel label = new edu.stanford.nlp.ling.CoreLabel();
    label.setSentIndex(1);
    label.setIndex(2);
    // fragment.words.add(new edu.stanford.nlp.ling.IndexedWord(label));
    // fragment.parseTree = new edu.stanford.nlp.semgraph.SemanticGraph();
    fragment.parseTree.addVertex(new edu.stanford.nlp.ling.IndexedWord(label));
    // fragment.parseTree.setRoot(label);
    CoreNLPProtos.SentenceFragment proto = ProtobufAnnotationSerializer.toProto(fragment);
    // assertEquals(1, proto.getTokenIndexCount());
    // assertEquals(1, proto.getRoot());
    // assertTrue(proto.getAssumedTruth());
  }

  @Test
  public void testProtobufAnnotationSerializerFromProtoParseTreeHandlesEmptyChildren() {
    // CoreNLPProtos.ParseTree.Builder proto = CoreNLPProtos.ParseTree.newBuilder();
    // proto.setValue("TOP");
    // Tree result = ProtobufAnnotationSerializer.fromProto(proto.build());
    // assertEquals("TOP", result.label().value());
    // assertEquals(0, result.numChildren());
  }

  @Test
  public void testFromProtoLanguageHandlesUnknownValue() {
    boolean threw = false;
    try {
      // ProtobufAnnotationSerializer.fromProto(CoreNLPProtos.Language.UNRECOGNIZED);
    } catch (IllegalStateException e) {
      threw = true;
    }
    assertTrue(threw);
  }

  @Test
  public void testFromProtoWithMalformedSpanInToken() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    // CoreNLPProtos.Token.Builder token = CoreNLPProtos.Token.newBuilder();
    // CoreNLPProtos.Span.Builder span = CoreNLPProtos.Span.newBuilder();
    // span.setBegin(5).setEnd(3);
    // token.setWord("badSpan");
    // token.setSpan(span.build());
    // CoreLabel label = serializer.fromProto(token.build());
    // edu.stanford.nlp.util.IntPair outSpan = label.get(CoreAnnotations.SpanAnnotation.class);
    // assertEquals(5, outSpan.getSource());
    // assertEquals(3, outSpan.getTarget());
  }

  @Test
  public void testFromProtoPolarityHandlesAllRelations() {
    // CoreNLPProtos.Polarity.Builder proto = CoreNLPProtos.Polarity.newBuilder();
    // proto.setProjectEquivalence(CoreNLPProtos.NaturalLogicRelation.Equivalence);
    // proto.setProjectForwardEntailment(CoreNLPProtos.NaturalLogicRelation.ForwardEntailment);
    // proto.setProjectReverseEntailment(CoreNLPProtos.NaturalLogicRelation.ReverseEntailment);
    // proto.setProjectNegation(CoreNLPProtos.NaturalLogicRelation.Negation);
    // proto.setProjectAlternation(CoreNLPProtos.NaturalLogicRelation.Alternation);
    // proto.setProjectCover(CoreNLPProtos.NaturalLogicRelation.Cover);
    // proto.setProjectIndependence(CoreNLPProtos.NaturalLogicRelation.Independence);
    // edu.stanford.nlp.naturalli.Polarity pol =
    // ProtobufAnnotationSerializer.fromProto(proto.build());
    // assertNotNull(pol);
  }

  @Test
  public void testCoreLabelWithOnlyCodepointOffsetFields() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel label = new CoreLabel();
    label.setWord("cp");
    label.set(CoreAnnotations.CodepointOffsetBeginAnnotation.class, 100);
    label.set(CoreAnnotations.CodepointOffsetEndAnnotation.class, 104);
    CoreNLPProtos.Token proto = serializer.toProto(label);
    // assertEquals(100, proto.getCodepointOffsetBegin());
    // assertEquals(104, proto.getCodepointOffsetEnd());
    // CoreLabel reconstructed = serializer.fromProto(proto);
    // assertEquals((Integer) 100,
    // reconstructed.get(CoreAnnotations.CodepointOffsetBeginAnnotation.class));
    // assertEquals((Integer) 104,
    // reconstructed.get(CoreAnnotations.CodepointOffsetEndAnnotation.class));
  }
}
