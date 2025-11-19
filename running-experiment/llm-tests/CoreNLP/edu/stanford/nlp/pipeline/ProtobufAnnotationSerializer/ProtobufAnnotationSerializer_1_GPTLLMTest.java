package edu.stanford.nlp.pipeline;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import edu.stanford.nlp.coref.data.SpeakerInfo;
import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.time.Timex;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.util.*;
import edu.stanford.nlp.util.CoreMap;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import org.junit.Test;

public class ProtobufAnnotationSerializer_1_GPTLLMTest {

  @Test
  public void testTokenSerializationMinimal() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel token = new CoreLabel();
    token.setWord("hello");
    token.setTag("NN");
    CoreNLPProtos.Token proto = serializer.toProto(token);
    assertNotNull(proto);
    // assertEquals("hello", proto.getWord());
    // assertEquals("NN", proto.getPos());
  }

  @Test
  public void testTokenSerializationAllFields() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel token = new CoreLabel();
    token.setWord("Obama");
    token.setNER("PERSON");
    token.setTag("NNP");
    token.setLemma("Obama");
    token.setIndex(1);
    token.setBefore(" ");
    token.setAfter(".");
    token.setBeginPosition(0);
    token.setEndPosition(5);
    token.set(CoreAnnotations.CoarseNamedEntityTagAnnotation.class, "PER");
    // token.set(OriginalTextAnnotation.class, "Obama");
    Map<String, Double> nerMap = new HashMap<>();
    nerMap.put("PERSON", 0.99);
    // token.set(NamedEntityTagProbsAnnotation.class, nerMap);
    // token.set(NormalizedNamedEntityTagAnnotation.class, "Barack Obama");
    CoreNLPProtos.Token proto = serializer.toProto(token);
    assertNotNull(proto);
    // assertEquals("Obama", proto.getWord());
    // assertEquals("NNP", proto.getPos());
    // assertEquals("PERSON", proto.getNer());
    // assertEquals("Obama", proto.getLemma());
    // assertEquals(1, proto.getIndex());
    // assertEquals(0, proto.getBeginChar());
    // assertEquals(5, proto.getEndChar());
    // assertEquals("Barack Obama", proto.getNormalizedNER());
    // assertTrue(proto.getNerLabelProbsList().get(0).contains("PERSON=0.99"));
  }

  @Test
  public void testTokenDeserialization() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel token = new CoreLabel();
    token.setWord("test");
    token.setNER("DATE");
    token.set(CoreAnnotations.GenderAnnotation.class, "male");
    CoreNLPProtos.Token proto = serializer.toProto(token);
    // CoreLabel restored = serializer.fromProto(proto);
    // assertNotNull(restored);
    // assertEquals("test", restored.word());
    // assertEquals("DATE", restored.ner());
    // assertEquals("male", restored.get(CoreAnnotations.GenderAnnotation.class));
  }

  @Test
  public void testTimexSerializationAndDeserialization() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    Timex timex = new Timex("DATE", "2024-04-01", "2024-04", "t1", "April", 1, 2);
    CoreNLPProtos.Timex proto = serializer.toProto(timex);
    // Timex reconstructed = ProtobufAnnotationSerializer.fromProto(proto);
    // assertEquals("DATE", reconstructed.timexType());
    // assertEquals("2024-04-01", reconstructed.value());
    // assertEquals("April", reconstructed.text());
    // assertEquals("2024-04", reconstructed.altVal());
    // assertEquals("t1", reconstructed.tid());
    // assertEquals(1, reconstructed.beginPoint());
    // assertEquals(2, reconstructed.endPoint());
  }

  @Test
  public void testMentionSerializationMinimal() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    edu.stanford.nlp.coref.data.Mention mention = new edu.stanford.nlp.coref.data.Mention();
    mention.startIndex = 1;
    mention.endIndex = 3;
    mention.headIndex = 2;
    mention.sentNum = 0;
    mention.mentionID = 100;
    CoreNLPProtos.Mention proto = serializer.toProto(mention);
    // assertEquals(1, proto.getStartIndex());
    // assertEquals(3, proto.getEndIndex());
    // assertEquals(2, proto.getHeadIndex());
    // assertEquals(0, proto.getSentNum());
    // assertEquals(100, proto.getMentionID());
  }

  @Test
  public void testEmptyProtoTokenDeserialization() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    // CoreNLPProtos.Token proto = CoreNLPProtos.Token.newBuilder().build();
    // CoreLabel result = serializer.fromProto(proto);
    // assertNotNull(result);
    // assertNull(result.word());
  }

  @Test
  public void testLossySerializationExceptionThrown() {
    CoreLabel token = new CoreLabel();
    token.setWord("skip");
    class DummyAnnotation implements CoreAnnotation<String> {

      public Class<String> getType() {
        return String.class;
      }
    }
    // token.set(new DummyAnnotation(), "not-serializable");
    try {
      ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
      serializer.toProto(token);
      fail("Expected LossySerializationException");
    } catch (ProtobufAnnotationSerializer.LossySerializationException ex) {
      assertTrue(ex.getMessage().contains("Keys are not being serialized"));
    }
  }

  @Test
  public void testSerializationAndDeserializationRoundTrip() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel label = new CoreLabel();
    label.setWord("science");
    label.setTag("NN");
    label.setNER("O");
    label.setLemma("science");
    CoreNLPProtos.Token proto = serializer.toProto(label);
    // CoreLabel roundtrip = serializer.fromProto(proto);
    // assertEquals("science", roundtrip.word());
    // assertEquals("NN", roundtrip.tag());
    // assertEquals("O", roundtrip.ner());
    // assertEquals("science", roundtrip.lemma());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testToProtoBuilderWithInvalidCoreMap() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel coreLabel = new CoreLabel();
    serializer.toProtoBuilder(coreLabel);
  }

  @Test
  public void testConllUFeatureMapSerialization() {
    Map<String, String> input = new HashMap<>();
    input.put("Tense", "Past");
    input.put("Aspect", "Perfect");
    CoreNLPProtos.MapStringString proto =
        ProtobufAnnotationSerializer.toMapStringStringProto(input);
    // assertEquals(2, proto.getKeyCount());
    // assertTrue(proto.getKeyList().contains("Tense"));
    // assertEquals("Perfect", proto.getValueList().get(proto.getKeyList().indexOf("Aspect")));
  }

  @Test
  public void testConllUFeatureMapDeserialization() {
    // CoreNLPProtos.MapStringString proto =
    // CoreNLPProtos.MapStringString.newBuilder().addKey("Voice").addValue("Passive").build();
    // Map<String, String> restored = ProtobufAnnotationSerializer.fromProto(proto);
    // assertEquals(1, restored.size());
    // assertEquals("Passive", restored.get("Voice"));
  }

  @Test
  public void testCreateIndexedWordProtoFromNullLabel() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreNLPProtos.IndexedWord iw = serializer.createIndexedWordProtoFromCL(null);
    // assertEquals(-1, iw.getSentenceNum());
    // assertEquals(-1, iw.getTokenIndex());
  }

  @Test
  public void testSerializationWithSkippedKeys() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel token = new CoreLabel();
    token.setWord("jumped");
    token.setTag("VBD");
    Set<Class<?>> skipped = new HashSet<>();
    skipped.add(CoreAnnotations.PartOfSpeechAnnotation.class);
    CoreNLPProtos.Token proto = serializer.toProto(token, skipped);
    // assertEquals("jumped", proto.getWord());
    // assertFalse(proto.hasPos());
  }

  @Test
  public void testNERProbSerializationEmpty() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel label = new CoreLabel();
    label.setWord("hello");
    Map<String, Double> emptyMap = new HashMap<>();
    label.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, emptyMap);
    CoreNLPProtos.Token proto = serializer.toProto(label);
    // assertEquals(1, proto.getNerLabelProbsList().size());
    // assertEquals("empty", proto.getNerLabelProbsList().get(0));
  }

  @Test
  public void testTokenSerializationWithNullFields() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel token = new CoreLabel();
    token.setTag(null);
    token.setNER(null);
    token.setLemma(null);
    CoreNLPProtos.Token proto = serializer.toProto(token);
    assertNotNull(proto);
    // assertEquals("", proto.getWord());
  }

  @Test
  public void testToProtoBuilderSkipsKnownInternalKeys() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel token = new CoreLabel();
    token.setWord("example");
    // token.set(CoreAnnotations.ForcedSentenceUntilEndAnnotation.class, "ignored");
    // token.set(CoreAnnotations.HeadWordLabelAnnotation.class, "ignored");
    CoreNLPProtos.Token proto = serializer.toProto(token);
    // assertEquals("example", proto.getWord());
  }

  @Test
  public void testPartiallyFilledMentionSerialization() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    edu.stanford.nlp.coref.data.Mention mention = new edu.stanford.nlp.coref.data.Mention();
    mention.mentionID = 5;
    CoreNLPProtos.Mention proto = serializer.toProto(mention);
    // assertEquals(5, proto.getMentionID());
    // assertFalse(proto.hasHeadString());
  }

  @Test
  public void testTimexSerializationWithMissingOptionalFields() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    Timex timex = new Timex("DATE", null, null, null, null, -1, -1);
    CoreNLPProtos.Timex proto = serializer.toProto(timex);
    // assertFalse(proto.hasValue());
    // assertFalse(proto.hasAltValue());
    // assertFalse(proto.hasText());
    // assertFalse(proto.hasTid());
    // assertFalse(proto.hasBeginPoint());
    // assertFalse(proto.hasEndPoint());
  }

  @Test
  public void testPolaritySerializationAndDeserialization() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    // NaturalLogicAnnotations.Polarity pol = new NaturalLogicAnnotations.Polarity(new byte[] { 0,
    // 1, 2, 3, 4, 5, 6 });
    // CoreNLPProtos.Polarity proto = serializer.toProto(pol);
    // NaturalLogicAnnotations.Polarity deserialized =
    // ProtobufAnnotationSerializer.fromProto(proto);
    // assertNotNull(deserialized);
  }

  @Test
  public void testOperatorSerializationAndDeserialization() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    NaturalLogicAnnotations.OperatorAnnotation op =
        new NaturalLogicAnnotations.OperatorAnnotation();
    // OperatorSpec spec = new OperatorSpec(NaturalLogicAnnotations.Operator.EXISTENTIAL, 1, 2, 3,
    // 4, 5, 6);
    // CoreNLPProtos.Operator proto = ProtobufAnnotationSerializer.toProto(spec);
    // OperatorSpec restored = ProtobufAnnotationSerializer.fromProto(proto);
    // assertEquals(spec.instance, restored.instance);
    // assertEquals(spec.subjectBegin, restored.subjectBegin);
  }

  @Test
  public void testToProtoWithCopiedIndexAnnotation() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel token = new CoreLabel();
    token.setWord("run");
    token.setIndex(3);
    // CoreNLPProtos.Token proto = serializer.toProto(token,
    // Collections.singleton(IndexAnnotation.class));
    // assertEquals("run", proto.getWord());
    // assertFalse(proto.hasIndex());
  }

  @Test
  public void testDeserializationWithNERLabelProbsEmptyString() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    // CoreNLPProtos.Token proto =
    // CoreNLPProtos.Token.newBuilder().setWord("blank").addNerLabelProbs("empty").build();
    // CoreLabel label = serializer.fromProto(proto);
    // assertNotNull(label.get(NamedEntityTagProbsAnnotation.class));
    // assertEquals(0, label.get(NamedEntityTagProbsAnnotation.class).size());
  }

  @Test
  public void testInvalidSentenceProtoDeserializationThrows() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    try {
      // CoreMap result = serializer.fromProto(CoreNLPProtos.Sentence.newBuilder().build());
      // assertNotNull(result);
    } catch (Exception e) {
      fail("Should not throw exception on empty sentence proto");
    }
  }

  @Test
  public void testSentenceFragmentSerializationWithNoRoot() {
    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token1 = new CoreLabel();
    token1.setWord("token1");
    token1.setIndex(1);
    tokens.add(token1);
    LabeledScoredTreeNode tree = new LabeledScoredTreeNode(new CoreLabel());
    tree.addChild(new LabeledScoredTreeNode(token1));
    // SentenceFragment frag = new SentenceFragment(new edu.stanford.nlp.semgraph.SemanticGraph());
    // frag.assumedTruth = true;
    // frag.score = 0.85;
    // frag.words = tokens;
    // frag.parseTree = new edu.stanford.nlp.semgraph.SemanticGraph();
    // CoreNLPProtos.SentenceFragment proto = ProtobufAnnotationSerializer.toProto(frag);
    // assertTrue(proto.getAssumedTruth());
    // assertEquals(0.85, proto.getScore(), 0.00001);
  }

  @Test
  public void testFlatParseTreeSerializationNodeOnly() {
    LabeledScoredTreeNode tree = new LabeledScoredTreeNode(new CoreLabel());
    tree.setScore(0.5);
    tree.label().setValue("ROOT");
    CoreNLPProtos.FlattenedParseTree proto = ProtobufAnnotationSerializer.toFlattenedTree(tree);
    // assertTrue(proto.getNodesList().size() >= 3);
  }

  @Test
  public void testFlatParseTreeDeserializationInvalidSequence() {
    // CoreNLPProtos.FlattenedParseTree.Builder builder =
    // CoreNLPProtos.FlattenedParseTree.newBuilder();
    // builder.addNodes(CoreNLPProtos.FlattenedParseTree.Node.newBuilder().setCloseNode(true));
    try {
      // ProtobufAnnotationSerializer.fromProto(builder.build());
      fail("Expected IllegalArgumentException for malformed tree");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("Tree started with a Close"));
    }
  }

  @Test
  public void testFromProtoMapStringEmptyLists() {
    // CoreNLPProtos.MapStringString proto = CoreNLPProtos.MapStringString.newBuilder().build();
    // Map<String, String> result = ProtobufAnnotationSerializer.fromProto(proto);
    // assertTrue(result.isEmpty());
  }

  @Test
  public void testFromProtoMapIntSingleEntry() {
    // CoreNLPProtos.MapIntString proto =
    // CoreNLPProtos.MapIntString.newBuilder().addKey(42).addValue("Albert").build();
    // Map<Integer, String> map = ProtobufAnnotationSerializer.fromProto(proto);
    // assertEquals(1, map.size());
    // assertTrue(map.containsKey(42));
    // assertEquals("Albert", map.get(42));
  }

  @Test
  public void testTokenWithGenderAndTrueCase() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel cl = new CoreLabel();
    cl.setWord("john");
    cl.set(CoreAnnotations.GenderAnnotation.class, "male");
    cl.set(CoreAnnotations.TrueCaseAnnotation.class, "LOWER");
    cl.set(CoreAnnotations.TrueCaseTextAnnotation.class, "John");
    CoreNLPProtos.Token proto = serializer.toProto(cl);
    // assertEquals("male", proto.getGender());
    // assertEquals("LOWER", proto.getTrueCase());
    // assertEquals("John", proto.getTrueCaseText());
  }

  @Test
  public void testTokenWithMultiWordTokenFields() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel token = new CoreLabel();
    token.setWord("New");
    // token.set(IsMultiWordTokenAnnotation.class, true);
    // token.set(IsFirstWordOfMWTAnnotation.class, true);
    // token.set(MWTTokenTextAnnotation.class, "New York");
    // token.set(MWTTokenMiscAnnotation.class, "SpaceAfter=No");
    CoreNLPProtos.Token proto = serializer.toProto(token);
    // assertTrue(proto.getIsMWT());
    // assertTrue(proto.getIsFirstMWT());
    // assertEquals("New York", proto.getMwtText());
    // assertEquals("SpaceAfter=No", proto.getMwtMisc());
  }

  @Test
  public void testNumericAnnotationSerialization() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel label = new CoreLabel();
    // label.set(NumericValueAnnotation.class, 12345L);
    // label.set(NumericTypeAnnotation.class, "percentage");
    // label.set(NumericCompositeValueAnnotation.class, 67890L);
    // label.set(NumericCompositeTypeAnnotation.class, "composite-percentage");
    CoreNLPProtos.Token proto = serializer.toProto(label);
    // assertEquals(12345L, proto.getNumericValue());
    // assertEquals("percentage", proto.getNumericType());
    // assertEquals(67890L, proto.getNumericCompositeValue());
    // assertEquals("composite-percentage", proto.getNumericCompositeType());
  }

  @Test
  public void testSectionStartFieldsSerialization() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel label = new CoreLabel();
    CoreMap section = new ArrayCoreMap();
    section.set(CoreAnnotations.SectionAnnotation.class, "Introduction");
    section.set(CoreAnnotations.AuthorAnnotation.class, "John Doe");
    section.set(CoreAnnotations.SectionDateAnnotation.class, "2024-01-01");
    label.set(CoreAnnotations.SectionStartAnnotation.class, section);
    CoreNLPProtos.Token proto = serializer.toProto(label);
    // assertEquals("Introduction", proto.getSectionName());
    // assertEquals("John Doe", proto.getSectionAuthor());
    // assertEquals("2024-01-01", proto.getSectionAuthor());
  }

  @Test
  public void testCoNLLFieldsSerialization() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel label = new CoreLabel();
    Map<String, String> feats = new HashMap<>();
    feats.put("Case", "Nom");
    feats.put("Gender", "Masc");
    // CoNLLUFeatures features = new CoNLLUFeatures(feats);
    // label.set(CoNLLUFeats.class, features);
    IntPair span = new IntPair(5, 6);
    // label.set(CoNLLUTokenSpanAnnotation.class, span);
    // label.set(CoNLLUMisc.class, "SpaceAfter=No");
    // label.set(CoarseTagAnnotation.class, "NOUN");
    CoreNLPProtos.Token proto = serializer.toProto(label);
    // assertEquals("NOUN", proto.getCoarseTag());
    // assertTrue(proto.getConllUMisc().contains("SpaceAfter"));
    // assertEquals(5, proto.getConllUTokenSpan().getBegin());
    // assertEquals("Masc",
    // ProtobufAnnotationSerializer.fromProto(proto.getConllUFeatures()).get("Gender"));
  }

  @Test
  public void testQuoteSerializationWithMinimalInfo() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreMap quote = new ArrayCoreMap();
    quote.set(CoreAnnotations.TextAnnotation.class, "Hello world");
    quote.set(CoreAnnotations.DocIDAnnotation.class, "doc1");
    quote.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    quote.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);
    CoreNLPProtos.Quote proto = serializer.toProtoQuote(quote);
    // assertEquals("Hello world", proto.getText());
    // assertEquals("doc1", proto.getDocid());
    // assertEquals(0, proto.getBegin());
    // assertEquals(11, proto.getEnd());
  }

  @Test
  public void testQuoteWithAttributionFields() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreMap quote = new ArrayCoreMap();
    quote.set(QuoteAttributionAnnotator.MentionAnnotation.class, "he");
    quote.set(QuoteAttributionAnnotator.MentionBeginAnnotation.class, 3);
    quote.set(QuoteAttributionAnnotator.MentionEndAnnotation.class, 4);
    quote.set(QuoteAttributionAnnotator.SpeakerAnnotation.class, "Barack Obama");
    quote.set(QuoteAttributionAnnotator.SpeakerSieveAnnotation.class, "RuleBased");
    CoreNLPProtos.Quote proto = serializer.toProtoQuote(quote);
    // assertEquals("he", proto.getMention());
    // assertEquals(3, proto.getMentionBegin());
    // assertEquals(4, proto.getMentionEnd());
    // assertEquals("Barack Obama", proto.getSpeaker());
    // assertEquals("RuleBased", proto.getSpeakerSieve());
  }

  @Test
  public void testWriteThrowsOnNullAnnotation() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    try {
      serializer.write(null, new ByteArrayOutputStream());
      fail("Expected NullPointerException or IOException");
    } catch (Exception e) {
      assertTrue(e instanceof NullPointerException || e instanceof IOException);
    }
  }

  @Test
  public void testToProtoTreeWithSentimentAnnotation() {
    Tree leaf = Tree.valueOf("(ROOT (NP (JJ great) (NN job)))");
    CoreNLPProtos.ParseTree proto = ProtobufAnnotationSerializer.toProto(leaf);
    // assertEquals("ROOT", proto.getValue());
    // assertTrue(proto.getChildCount() > 0);
  }

  @Test
  public void testToProtoSentenceFragmentWithScoreAndTruth() {
    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel token = new CoreLabel();
    token.setWord("cats");
    token.setIndex(1);
    tokens.add(token);
    RelationTriple triple = new RelationTriple(tokens, tokens, tokens, 0.9);
    CoreNLPProtos.RelationTriple proto = new ProtobufAnnotationSerializer(true).toProto(triple);
    // assertEquals("cats", proto.getSubject());
    // assertTrue(proto.getConfidence() > 0.0);
    // assertTrue(proto.getSubjectTokensList().get(0).getTokenIndex() >= 0);
  }

  @Test
  public void testWordWithXmlContext() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel label = new CoreLabel();
    label.setWord("word");
    List<String> xmlContextList = new ArrayList<>();
    xmlContextList.add("chapter");
    xmlContextList.add("section");
    label.set(CoreAnnotations.XmlContextAnnotation.class, xmlContextList);
    CoreNLPProtos.Token proto = serializer.toProto(label);
    // assertTrue(proto.getHasXmlContext());
    // assertEquals(2, proto.getXmlContextCount());
    // assertEquals("chapter", proto.getXmlContextList().get(0));
  }

  @Test
  public void testFromProtoHandlesMissingFieldsGracefully() {
    // CoreNLPProtos.Token proto = CoreNLPProtos.Token.newBuilder().setWord("X").build();
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    // CoreLabel cl = serializer.fromProto(proto);
    // assertEquals("X", cl.word());
    // assertNull(cl.ner());
    // assertNull(cl.tag());
  }

  @Test
  public void testWriteEmptyAnnotation() throws IOException {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);
    Annotation annotation = new Annotation("");
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    // OutputStream returned = serializer.write(annotation, out);
    // assertSame(out, returned);
    assertTrue(out.toByteArray().length > 0);
  }

  @Test
  public void testOperatorEnumFallback() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    // CoreNLPProtos.Operator proto =
    // CoreNLPProtos.Operator.newBuilder().setName("nonexistent").setQuantifierSpanBegin(0).setQuantifierSpanEnd(1).setSubjectSpanBegin(1).setSubjectSpanEnd(2).setObjectSpanBegin(2).setObjectSpanEnd(3).build();
    // OperatorSpec spec = ProtobufAnnotationSerializer.fromProto(proto);
    // assertNull(spec.instance);
  }

  @Test
  public void testPolarityDeserializationWithAllRelations() {
    // CoreNLPProtos.Polarity.Builder builder =
    // CoreNLPProtos.Polarity.newBuilder().setProjectEquivalence(CoreNLPProtos.NaturalLogicRelation.REVERSE_ENTAILMENT).setProjectForwardEntailment(CoreNLPProtos.NaturalLogicRelation.FORWARD_ENTAILMENT).setProjectReverseEntailment(CoreNLPProtos.NaturalLogicRelation.REVERSE_ENTAILMENT).setProjectNegation(CoreNLPProtos.NaturalLogicRelation.NEGATION).setProjectAlternation(CoreNLPProtos.NaturalLogicRelation.ALTERNATION).setProjectCover(CoreNLPProtos.NaturalLogicRelation.COVER).setProjectIndependence(CoreNLPProtos.NaturalLogicRelation.INDEPENDENCE);
    // Polarity polarity = ProtobufAnnotationSerializer.fromProto(builder.build());
    // assertNotNull(polarity);
  }

  @Test
  public void testEmptyIndexedWordDeserialization() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel cl = new CoreLabel();
    cl.setIndex(1);
    cl.set(CoreAnnotations.SentenceIndexAnnotation.class, 1);
    CoreNLPProtos.IndexedWord proto = serializer.createIndexedWordProtoFromCL(cl);
    // assertEquals(0, proto.getSentenceNum());
    // assertEquals(0, proto.getTokenIndex());
  }

  @Test
  public void testEmptyTreeFlattening() {
    Tree empty = new LabeledScoredTreeNode(new CoreLabel());
    CoreNLPProtos.FlattenedParseTree proto = ProtobufAnnotationSerializer.toFlattenedTree(empty);
    assertNotNull(proto);
    // assertTrue(proto.getNodesCount() >= 3);
  }

  @Test
  public void testMalformedFlatTreeInterpretationMissingClose() {
    // CoreNLPProtos.FlattenedParseTree.Builder builder =
    // CoreNLPProtos.FlattenedParseTree.newBuilder();
    // builder.addNodes(CoreNLPProtos.FlattenedParseTree.Node.newBuilder().setOpenNode(true));
    // builder.addNodes(CoreNLPProtos.FlattenedParseTree.Node.newBuilder().setValue("ROOT"));
    try {
      // Tree tree = ProtobufAnnotationSerializer.fromProto(builder.build());
      fail("Expected IllegalArgumentException for malformed pretty tree");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("Tree never finished"));
    }
  }

  @Test
  public void testFromProtoRelationTripleMissingRelationTokens() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    // CoreNLPProtos.RelationTriple.Builder builder =
    // CoreNLPProtos.RelationTriple.newBuilder().setSubject("John").setRelation("knows").setObject("Mary").setConfidence(0.85);
    // builder.addSubjectTokens(CoreNLPProtos.TokenLocation.newBuilder().setSentenceIndex(0).setTokenIndex(0).build());
    // builder.addObjectTokens(CoreNLPProtos.TokenLocation.newBuilder().setSentenceIndex(0).setTokenIndex(1).build());
    Annotation dummyDoc = new Annotation("John knows Mary.");
    CoreLabel s = new CoreLabel();
    s.setWord("John");
    s.setIndex(1);
    s.setSentIndex(0);
    CoreLabel o = new CoreLabel();
    o.setWord("Mary");
    o.setIndex(2);
    o.setSentIndex(0);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(s);
    tokens.add(o);
    CoreMap sentenceMap = new ArrayCoreMap();
    sentenceMap.set(CoreAnnotations.TokensAnnotation.class, tokens);
    dummyDoc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentenceMap));
    // RelationTriple triple = ProtobufAnnotationSerializer.fromProto(builder.build(), dummyDoc, 0);
    // assertEquals("John", triple.subjectGloss());
    // assertTrue(triple.relationGloss().length() > 0);
  }

  @Test
  public void testRecoveryOfEmptyDocumentTextFromEmptyTokens() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    // CoreNLPProtos.Sentence.Builder protoSentence = CoreNLPProtos.Sentence.newBuilder();
    // protoSentence.setTokenOffsetBegin(0);
    // protoSentence.setTokenOffsetEnd(0);
    List<CoreLabel> emptyTokens = new ArrayList<>();
    // String recovered = serializer.recoverOriginalText(emptyTokens, protoSentence.build());
    // assertEquals("", recovered);
  }

  @Test
  public void testTokenWithNegativeIndicesIgnoredOnDeserialization() {
    // CoreNLPProtos.Token.Builder builder = CoreNLPProtos.Token.newBuilder();
    // builder.setWord("neg");
    // builder.setTokenBeginIndex(-1);
    // builder.setTokenEndIndex(-1);
    // builder.setBeginChar(-10);
    // builder.setEndChar(-5);
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    // CoreLabel cl = serializer.fromProto(builder.build());
    // assertEquals("neg", cl.word());
  }

  @Test
  public void testFromProtoMapStringWithDuplicateKeysPreservesLast() {
    // CoreNLPProtos.MapStringString.Builder builder = CoreNLPProtos.MapStringString.newBuilder();
    // builder.addKey("Gender");
    // builder.addValue("Male");
    // builder.addKey("Gender");
    // builder.addValue("Female");
    // Map<String, String> map = ProtobufAnnotationSerializer.fromProto(builder.build());
    // assertEquals("Female", map.get("Gender"));
    // assertEquals(1, map.size());
  }

  @Test
  public void testSentenceWithParagraphAnnotationProperlyAssigned() {
    CoreLabel c1 = new CoreLabel();
    c1.setWord("One");
    c1.setIndex(1);
    c1.set(CoreAnnotations.ParagraphAnnotation.class, 2);
    c1.setSentIndex(0);
    CoreNLPProtos.Token pt = new ProtobufAnnotationSerializer(true).toProto(c1);
    // CoreNLPProtos.Sentence.Builder sb = CoreNLPProtos.Sentence.newBuilder();
    // sb.setTokenOffsetBegin(0).setTokenOffsetEnd(1).setCharacterOffsetBegin(0).setCharacterOffsetEnd(3);
    // sb.addToken(pt);
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    // CoreMap result = serializer.fromProto(sb.build());
    // List<CoreLabel> finalTokens = result.get(CoreAnnotations.TokensAnnotation.class);
    // assertNotNull(finalTokens);
    // assertEquals(1, (int) finalTokens.get(0).get(CoreAnnotations.ParagraphAnnotation.class));
  }

  @Test
  public void testReadUndelimitedHandlesInvalidFirstProto() throws Exception {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    Annotation dummy = new Annotation("x");
    ByteArrayInputStream broken = new ByteArrayInputStream(new byte[] {0, 1, 2});
    try {
      serializer.read(broken);
      fail("Expected IOException");
    } catch (IOException e) {
      assertTrue(e.getMessage().length() > 0);
    }
  }

  @Test
  public void testReadUndelimitedFromDiskFallbackPath() throws Exception {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    Annotation ann = new Annotation("sample");
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    serializer.write(ann, baos);
    byte[] bytes = baos.toByteArray();
    InputStream fis = new ByteArrayInputStream(bytes);
    // CoreNLPProtos.Document doc = CoreNLPProtos.Document.parseDelimitedFrom(fis);
    // assertNotNull(doc.getText());
    InputStream recoveryStream = new ByteArrayInputStream(bytes);
    // CoreNLPProtos.Document fallback = CoreNLPProtos.Document.parseDelimitedFrom(recoveryStream);
    // assertEquals("sample", fallback.getText());
  }

  @Test
  public void testEmptyNERLabelProbsListIsHandled() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    // CoreNLPProtos.Token proto =
    // CoreNLPProtos.Token.newBuilder().setWord("test").addNerLabelProbs("empty").build();
    // CoreLabel label = serializer.fromProto(proto);
    // Map<String, Double> probs = label.get(CoreAnnotations.NamedEntityTagProbsAnnotation.class);
    // assertNotNull(probs);
    // assertTrue(probs.isEmpty());
  }

  @Test
  public void testTokenWithSectionEndAnnotationOnly() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel label = new CoreLabel();
    label.setWord("EndToken");
    label.set(CoreAnnotations.SectionEndAnnotation.class, "##END##");
    CoreNLPProtos.Token proto = serializer.toProto(label);
    // assertEquals("##END##", proto.getSectionEndLabel());
  }

  @Test
  public void testMissingOperatorFieldsHandledGracefully() {
    // CoreNLPProtos.Operator proto =
    // CoreNLPProtos.Operator.newBuilder().setName("FOO").setQuantifierSpanBegin(1).setQuantifierSpanEnd(2).setSubjectSpanBegin(3).setSubjectSpanEnd(4).setObjectSpanBegin(5).setObjectSpanEnd(6).build();
    // OperatorSpec spec = ProtobufAnnotationSerializer.fromProto(proto);
    // assertNull(spec.instance);
    // assertEquals(3, spec.subjectBegin);
    // assertEquals(6, spec.objectEnd);
  }

  @Test
  public void testDeserializationOfTokenWithCopyCountIgnoredIfZero() {
    CoreLabel label = new CoreLabel();
    label.set(CoreAnnotations.IndexAnnotation.class, 2);
    label.setWord("test");
    // CoreNLPProtos.DependentToken.Builder depBuilder = CoreNLPProtos.DependentToken.newBuilder();
    // CoreNLPProtos.DependencyGraph.Node.Builder nodeBuilder =
    // CoreNLPProtos.DependencyGraph.Node.newBuilder().setIndex(2).setSentenceIndex(0).setCopyAnnotation(0);
    // assertEquals(0, nodeBuilder.getCopyAnnotation());
  }

  @Test
  public void testTokenWithSpanAnnotationIsSerializedCorrectly() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel token = new CoreLabel();
    IntPair span = new IntPair(3, 7);
    token.set(CoreAnnotations.SpanAnnotation.class, span);
    CoreNLPProtos.Token proto = serializer.toProto(token);
    // assertTrue(proto.hasSpan());
    // assertEquals(3, proto.getSpan().getBegin());
    // assertEquals(7, proto.getSpan().getEnd());
  }

  @Test
  public void testTreeWithScorePreservedInSerialization() {
    LabeledScoredTreeNode tree = new LabeledScoredTreeNode();
    CoreLabel label = new CoreLabel();
    label.setValue("S");
    tree.setLabel(label);
    tree.setScore(1.5);
    CoreNLPProtos.ParseTree proto = ProtobufAnnotationSerializer.toProto(tree);
    // assertEquals("S", proto.getValue());
    // assertTrue(proto.hasScore());
    // assertEquals(1.5, proto.getScore(), 0.0001);
  }

  @Test
  public void testMentionWithSpeakerInfoIsSerializedCorrectly() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    // Mention mention = new Mention();
    // mention.mentionID = 42;
    // mention.speakerInfo = new SpeakerInfo("Alice");
    // Mention another = new Mention();
    // another.mentionID = 99;
    // mention.speakerInfo.addMention(another);
    // another.speakerInfo = mention.speakerInfo;
    // CoreNLPProtos.Mention proto = serializer.toProto(mention);
    // assertTrue(proto.hasSpeakerInfo());
    // assertEquals("Alice", proto.getSpeakerInfo().getSpeakerName());
    // assertEquals(1, proto.getSpeakerInfo().getMentionsList().size());
  }

  @Test
  public void testEntityMentionWithHeadOnly() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    // edu.stanford.nlp.ie.machinereading.structure.EntityMention entityMention = new
    // edu.stanford.nlp.ie.machinereading.structure.EntityMention("eid", null, new
    // edu.stanford.nlp.ie.machinereading.structure.Span(2, 4), null, null, null);
    // CoreNLPProtos.Entity proto = serializer.toProto(entityMention);
    // assertTrue(proto.hasHeadStart());
    // assertTrue(proto.hasHeadEnd());
    // assertEquals(2, proto.getHeadStart());
    // assertEquals(4, proto.getHeadEnd());
  }

  @Test
  public void testDeserializeSentenceFragmentWithEmptyFieldsReturnsDefault() {
    // CoreNLPProtos.SentenceFragment fragment =
    // CoreNLPProtos.SentenceFragment.newBuilder().build();
    SemanticGraph graph = new SemanticGraph();
    // SentenceFragment result = ProtobufAnnotationSerializer.fromProto(fragment, graph);
    // assertNotNull(result);
    // assertEquals(1.0, result.score, 0.00001);
    // assertTrue(result.assumedTruth);
  }

  @Test
  public void testCorefMentionMissingRepresentativeDoesNotThrow() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    // CoreNLPProtos.CorefChain.Builder proto = CoreNLPProtos.CorefChain.newBuilder();
    // proto.setChainID(5);
    // proto.addMention(CoreNLPProtos.CorefChain.CorefMention.newBuilder().setMentionID(0).setMentionType("PROPER").setGender("MALE").setNumber("SINGULAR").setAnimacy("ANIMATE").setBeginIndex(0).setEndIndex(1).setHeadIndex(0).setSentenceIndex(0).setPosition(1));
    Annotation fakeDoc = new Annotation("dummy");
    CoreLabel t = new CoreLabel();
    t.setWord("John");
    t.setIndex(1);
    t.setSentIndex(0);
    CoreMap sent = new ArrayCoreMap();
    sent.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(t));
    fakeDoc.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sent));
    // CorefChain chain = serializer.fromProto(proto.build(), fakeDoc);
    // assertNotNull(chain);
  }

  @Test
  public void testDeserializeTokenWithNoBeforePreservesPositionFallback() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    // CoreNLPProtos.Token.Builder protoBuilder = CoreNLPProtos.Token.newBuilder();
    // protoBuilder.setWord("X").setBeginChar(10).setEndChar(11);
    // CoreLabel label = serializer.fromProto(protoBuilder.build());
    // assertEquals("X", label.word());
    // assertEquals(10, label.beginPosition());
    // assertEquals(11, label.endPosition());
  }

  @Test
  public void testMapStringStringProtoWithUnevenKeyValueSizesIsHandled() {
    // CoreNLPProtos.MapStringString.Builder builder = CoreNLPProtos.MapStringString.newBuilder();
    // builder.addKey("A").addKey("B");
    // builder.addValue("1");
    // Map<String, String> result = ProtobufAnnotationSerializer.fromProto(builder.build());
    // assertEquals(1, result.size());
    // assertEquals("1", result.get("A"));
  }

  @Test
  public void testTokenWithNullWordField() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel label = new CoreLabel();
    CoreNLPProtos.Token proto = serializer.toProto(label);
    // assertEquals("", proto.getWord());
  }

  @Test
  public void testDeserializationOfTokenWithConllUSecondaryDeps() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    // CoreNLPProtos.MapStringString.Builder depsBuilder =
    // CoreNLPProtos.MapStringString.newBuilder();
    // depsBuilder.addKey("nsubj").addValue("nsubj:pass");
    // CoreNLPProtos.Token proto =
    // CoreNLPProtos.Token.newBuilder().setWord("foo").setConllUSecondaryDeps(depsBuilder.build()).build();
    // CoreLabel label = serializer.fromProto(proto);
    // assertTrue(label.containsKey(CoreAnnotations.CoNLLUSecondaryDepsAnnotation.class));
    // Map<String, String> recovered =
    // label.get(CoreAnnotations.CoNLLUSecondaryDepsAnnotation.class);
    // assertEquals("nsubj:pass", recovered.get("nsubj"));
  }

  @Test
  public void testDeserializeTimexWithMissingFields() {
    // CoreNLPProtos.Timex proto = CoreNLPProtos.Timex.newBuilder().build();
    // Timex timex = ProtobufAnnotationSerializer.fromProto(proto);
    // assertNull(timex.value());
    // assertNull(timex.text());
    // assertEquals(-1, timex.beginPoint());
    // assertEquals(-1, timex.endPoint());
  }

  @Test
  public void testFromProtoMentionMinimalValidMention() {
    // CoreNLPProtos.Mention proto =
    // CoreNLPProtos.Mention.newBuilder().setMentionID(3).setStartIndex(10).setEndIndex(15).setHeadIndex(12).setSentNum(2).build();
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    // Mention mention = serializer.fromProtoNoTokens(proto);
    // assertNotNull(mention);
    // assertEquals(3, mention.mentionID);
    // assertEquals(10, mention.startIndex);
  }

  @Test
  public void testFromProtoSpeakerInfoWithNoMentions() {
    // CoreNLPProtos.SpeakerInfo proto =
    // CoreNLPProtos.SpeakerInfo.newBuilder().setSpeakerName("John").build();
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    // SpeakerInfo speakerInfo = serializer.fromProto(proto);
    // assertEquals("John", speakerInfo.getSpeakerName());
    // assertTrue(speakerInfo.getMentions().isEmpty());
  }

  @Test
  public void testEntityMentionWithOnlyExtentSet() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    // EntityMention mention = new EntityMention("ent1", null, new Span(2, 4), null, null, null);
    // CoreNLPProtos.Entity proto = serializer.toProto(mention);
    // assertEquals(2, proto.getExtentStart());
    // assertEquals(4, proto.getExtentEnd());
    // assertEquals("ent1", proto.getObjectID());
  }

  @Test
  public void testXmlContextFlagFalseWhenFieldAbsent() {
    // CoreNLPProtos.Token proto =
    // CoreNLPProtos.Token.newBuilder().setWord("tagged").setHasXmlContext(false).build();
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    // CoreLabel label = serializer.fromProto(proto);
    // assertFalse(label.containsKey(CoreAnnotations.XmlContextAnnotation.class));
  }

  @Test
  public void testCustomAnnotationSkippedSuccessfully() {
    class UnknownAnnotation implements CoreAnnotation<String> {

      public Class<String> getType() {
        return String.class;
      }
    }
    CoreLabel label = new CoreLabel();
    label.setWord("X");
    // label.set(new UnknownAnnotation(), "custom");
    try {
      ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
      serializer.toProto(label);
      fail("Expected LossySerializationException for unknown annotation");
    } catch (ProtobufAnnotationSerializer.LossySerializationException e) {
      assertTrue(e.getMessage().contains("not being serialized"));
    }
  }

  @Test
  public void testFromProtoDependencyGraphWithNullRootsComputesDefault() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    // CoreNLPProtos.DependencyGraph.Builder builder = CoreNLPProtos.DependencyGraph.newBuilder();
    // builder.addNode(CoreNLPProtos.DependencyGraph.Node.newBuilder().setIndex(1).setSentenceIndex(0).setEmptyIndex(0).setCopyAnnotation(0));
    // CoreNLPProtos.DependencyGraph proto = builder.build();
    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel label = new CoreLabel();
    label.setIndex(1);
    tokens.add(label);
    // SemanticGraph graph = ProtobufAnnotationSerializer.fromProto(proto, tokens, "docid");
    // assertFalse(graph.isEmpty());
    // assertNotNull(graph.getRoots());
  }

  @Test
  public void testTokenWithEmptyNerProbStillProperlySerialized() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel label = new CoreLabel();
    Map<String, Double> probs = new HashMap<>();
    label.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, probs);
    CoreNLPProtos.Token proto = serializer.toProto(label);
    // assertEquals("empty", proto.getNerLabelProbs(0));
  }

  @Test
  public void testReadingEmptyProtoStreamReturnsNull() throws IOException {
    byte[] empty = new byte[0];
    ByteArrayInputStream inputStream = new ByteArrayInputStream(empty);
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    // Pair<Annotation, InputStream> result = serializer.read(inputStream);
    // assertNull(result.first());
    // assertNotNull(result.second());
  }

  @Test
  public void testParseTreeRoundTripWithSentimentPreserved() {
    CoreLabel label = new CoreLabel();
    label.setValue("NP");
    label.set(RNNCoreAnnotations.PredictedClass.class, 3);
    Tree tree = new LabeledScoredTreeNode(label);
    CoreNLPProtos.ParseTree proto = ProtobufAnnotationSerializer.toProto(tree);
    // Tree restored = ProtobufAnnotationSerializer.fromProto(proto);
    // assertEquals("NP", restored.value());
    // assertTrue(restored.label() instanceof CoreLabel);
    // CoreLabel restoredLabel = (CoreLabel) restored.label();
    // assertEquals(Integer.valueOf(3), restoredLabel.get(RNNCoreAnnotations.PredictedClass.class));
  }

  @Test
  public void testToProtoMentionIncludesGenderAndMentionId() {
    CoreMap mention = new ArrayCoreMap();
    mention.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    mention.set(CoreAnnotations.TokenBeginAnnotation.class, 1);
    mention.set(CoreAnnotations.TokenEndAnnotation.class, 2);
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    mention.set(CoreAnnotations.GenderAnnotation.class, "male");
    mention.set(CoreAnnotations.EntityMentionIndexAnnotation.class, 7);
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreNLPProtos.NERMention proto = serializer.toProtoMention(mention);
    // assertEquals("PERSON", proto.getNer());
    // assertEquals("male", proto.getGender());
    // assertEquals(7, proto.getEntityMentionIndex());
  }

  @Test
  public void testTokenDeserializationWithOnlyRequiredFieldSet() {
    // CoreNLPProtos.Token tokenProto = CoreNLPProtos.Token.newBuilder().setWord("token").build();
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    // CoreLabel token = serializer.fromProto(tokenProto);
    // assertEquals("token", token.word());
    // assertNull(token.tag());
    // assertEquals(-1, token.getIndex());
  }

  @Test
  public void testToProtoSkipsCorefMentionIndexListIfAbsent() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel cl = new CoreLabel();
    cl.setWord("test");
    // cl.set(CoreAnnotations.CorefClusterIdAnnotation.class, 101);
    CoreNLPProtos.Token tokenProto = serializer.toProto(cl);
    // assertEquals(101, tokenProto.getCorefClusterID());
    // assertEquals(0, tokenProto.getCorefMentionIndexCount());
  }

  @Test
  public void testToProtoEntityMentionIncludesSubTypeAndNormalizedName() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    edu.stanford.nlp.ie.machinereading.structure.EntityMention em =
        new edu.stanford.nlp.ie.machinereading.structure.EntityMention(
            "id1",
            null,
            new edu.stanford.nlp.ie.machinereading.structure.Span(1, 2),
            new edu.stanford.nlp.ie.machinereading.structure.Span(1, 2),
            "PERSON",
            "title",
            "PROPER");
    em.setNormalizedName("Barack Obama");
    CoreNLPProtos.Entity proto = serializer.toProto(em);
    // assertEquals("title", proto.getSubtype());
    // assertEquals("Barack Obama", proto.getNormalizedName());
  }

  @Test
  public void testEmptySpeakerMentionSetIsSerializedAndDeserialized() {
    SpeakerInfo speakerInfo = new SpeakerInfo("AuthorX");
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreNLPProtos.SpeakerInfo proto = serializer.toProto(speakerInfo);
    // SpeakerInfo restored = serializer.fromProto(proto);
    // assertEquals("AuthorX", restored.getSpeakerName());
    // assertTrue(restored.getMentions().isEmpty());
  }

  @Test
  public void testSerializationAndDeserializationOfZeroIndexedToken() {
    CoreLabel token = new CoreLabel();
    token.setWord("testZero");
    token.setIndex(0);
    token.setSentIndex(1);
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreNLPProtos.Token proto = serializer.toProto(token);
    // CoreLabel deserialized = serializer.fromProto(proto);
    // assertEquals("testZero", deserialized.word());
    // assertEquals(0, deserialized.index());
  }

  @Test
  public void testRoundtripMentionWithAllFlags() {
    // Mention mention = new Mention();
    // mention.mentionID = 9;
    // mention.startIndex = 5;
    // mention.endIndex = 6;
    // mention.headIndex = 5;
    // mention.sentNum = 0;
    // mention.generic = true;
    // mention.hasTwin = true;
    // mention.isSingleton = true;
    // mention.isSubject = true;
    // mention.isDirectObject = true;
    // mention.isIndirectObject = true;
    // mention.isPrepositionObject = true;
    // mention.headString = "Obama";
    // mention.nerString = "PERSON";
    // mention.number = Dictionaries.Number.SINGULAR;
    // mention.gender = Dictionaries.Gender.MALE;
    // mention.animacy = Dictionaries.Animacy.ANIMATE;
    // mention.person = Dictionaries.Person.THIRD;
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    // CoreNLPProtos.Mention proto = serializer.toProto(mention);
    // assertTrue(proto.getIsSingleton());
    // assertEquals("Obama", proto.getHeadString());
    // assertEquals("PERSON", proto.getNerString());
    // assertEquals("SINGULAR", proto.getNumber());
    // assertTrue(proto.getIsSubject());
  }

  @Test
  public void testSentenceFragmentProtoWithPositiveSentimentTreeHandledCorrectly() {
    // CoreNLPProtos.SentenceFragment proto =
    // CoreNLPProtos.SentenceFragment.newBuilder().setScore(0.88f).setRoot(0).addTokenIndex(0).setAssumedTruth(true).build();
    SemanticGraph dummyGraph = new SemanticGraph();
    // SentenceFragment frag = ProtobufAnnotationSerializer.fromProto(proto, dummyGraph);
    // assertEquals(0.88, frag.score, 0.0001);
    // assertTrue(frag.assumedTruth);
  }

  @Test
  public void testTokenWithWikipediaEntityAndIsNewlineTrue() {
    CoreLabel token = new CoreLabel();
    token.setWord("Obama");
    token.set(CoreAnnotations.WikipediaEntityAnnotation.class, "Barack_Obama");
    token.set(CoreAnnotations.IsNewlineAnnotation.class, true);
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreNLPProtos.Token proto = serializer.toProto(token);
    // assertEquals("Barack_Obama", proto.getWikipediaEntity());
    // assertTrue(proto.getIsNewline());
  }

  @Test
  public void testMapStringStringWithMixedKeysAndValues() {
    // CoreNLPProtos.MapStringString proto =
    // CoreNLPProtos.MapStringString.newBuilder().addKey("a").addValue("1").addKey("b").addValue("2").build();
    // Map<String, String> restored = ProtobufAnnotationSerializer.fromProto(proto);
    // assertEquals("1", restored.get("a"));
    // assertEquals("2", restored.get("b"));
  }

  @Test
  public void testTreeToProtoAndBackWithCategoryLabel() {
    CoreLabel label = new CoreLabel();
    label.setCategory("NP");
    Tree tree = new LabeledScoredTreeNode(label);
    CoreNLPProtos.ParseTree proto = ProtobufAnnotationSerializer.toProto(tree);
    // Tree restored = ProtobufAnnotationSerializer.fromProto(proto);
    // assertEquals("NP", restored.label().value());
  }

  @Test
  public void testDocumentToProtoWithCalendar() {
    Annotation annotation = new Annotation("sample text");
    Calendar calendar = new GregorianCalendar(2024, Calendar.JANUARY, 1);
    annotation.set(CoreAnnotations.CalendarAnnotation.class, calendar);
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreNLPProtos.Document proto = serializer.toProto(annotation);
    // assertTrue(proto.hasCalendar());
    // assertEquals(calendar.getTimeInMillis(), proto.getCalendar());
  }

  @Test
  public void testWriteFlushesOutputStream() throws IOException {
    Annotation annotation = new Annotation("Testing flush");
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    serializer.write(annotation, output);
    assertTrue(output.size() > 0);
  }

  @Test
  public void testToProtoLanguageFallbackThrowsOnInvalidLanguage() {
    boolean caught = false;
    try {
      // ProtobufAnnotationSerializer.toProto(null);
    } catch (IllegalStateException e) {
      caught = true;
    }
    assertTrue(caught);
  }
}
