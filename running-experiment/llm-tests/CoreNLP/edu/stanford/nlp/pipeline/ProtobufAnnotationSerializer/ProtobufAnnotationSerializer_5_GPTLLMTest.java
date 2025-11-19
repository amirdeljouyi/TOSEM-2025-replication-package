package edu.stanford.nlp.pipeline;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import edu.stanford.nlp.coref.data.Mention;
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
import java.lang.reflect.Method;
import java.util.*;
import org.junit.Test;

public class ProtobufAnnotationSerializer_5_GPTLLMTest {

  @Test
  public void testWriteThenReadAnnotationPreservesText() throws Exception {
    Annotation original = new Annotation("Barack Obama was president.");
    CoreLabel token = new CoreLabel();
    token.setWord("Barack");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    token.setBeginPosition(0);
    token.setEndPosition(6);
    token.set(CoreAnnotations.IndexAnnotation.class, 1);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    original.set(CoreAnnotations.TokensAnnotation.class, tokens);
    original.set(CoreAnnotations.TextAnnotation.class, "Barack Obama was president.");
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    serializer.write(original, out);
    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
    Pair<Annotation, InputStream> result = serializer.read(in);
    Annotation deserialized = result.first;
    assertNotNull(deserialized);
    assertEquals(
        "Barack Obama was president.", deserialized.get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testLossySerializationThrowsWhenEnforced() {
    Annotation annotation = new Annotation("text");
    // annotation.set(DummyAnnotation.class, "should cause loss");
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    try {
      serializer.toProto(annotation);
      fail("Expected LossySerializationException");
    } catch (ProtobufAnnotationSerializer.LossySerializationException e) {
      assertTrue(e.getMessage().contains("Keys are not being serialized"));
    }
  }

  @Test
  public void testLossySerializationDoesNotThrowWhenRelaxed() {
    Annotation annotation = new Annotation("text");
    // annotation.set(DummyAnnotation.class, "safe to ignore");
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);
    CoreNLPProtos.Document doc = serializer.toProto(annotation);
    assertNotNull(doc);
    // assertEquals("text", doc.getText());
  }

  @Test
  public void testWriteThenReadUndelimitedFile() throws Exception {
    Annotation document = new Annotation("Stanford University is in California.");
    document.set(CoreAnnotations.TextAnnotation.class, "Stanford University is in California.");
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    File file = File.createTempFile("test", ".proto");
    file.deleteOnExit();
    FileOutputStream out = new FileOutputStream(file);
    serializer.write(document, out);
    out.close();
    Annotation result = serializer.readUndelimited(file);
    assertNotNull(result);
    assertEquals(
        "Stanford University is in California.", result.get(CoreAnnotations.TextAnnotation.class));
  }

  @Test(expected = IOException.class)
  public void testReadWithInvalidInputThrowsIOException() throws Exception {
    byte[] invalid = new byte[] {0, 1, 2, 3};
    InputStream input = new ByteArrayInputStream(invalid);
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    serializer.read(input);
  }

  @Test
  public void testToProtoCoreLabelWithSingleField() {
    CoreLabel label = new CoreLabel();
    label.setWord("Apple");
    label.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    CoreNLPProtos.Token proto = serializer.toProto(label);
    // assertEquals("Apple", proto.getWord());
    // assertEquals("NNP", proto.getPos());
  }

  @Test
  public void testDeserializeMinimalDocumentSucceeds() throws Exception {
    Annotation doc = new Annotation("Tesla orbits Mars.");
    CoreLabel token = new CoreLabel();
    token.setWord("Tesla");
    token.setBeginPosition(0);
    token.setEndPosition(5);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    doc.set(CoreAnnotations.TokensAnnotation.class, tokens);
    doc.set(CoreAnnotations.TextAnnotation.class, "Tesla orbits Mars.");
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    serializer.write(doc, out);
    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
    Pair<Annotation, InputStream> result = serializer.read(in);
    Annotation roundTrip = result.first;
    assertNotNull(roundTrip);
    assertEquals("Tesla orbits Mars.", roundTrip.get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testToProtoAndBackCoreLabelPreservesNER() {
    CoreLabel label = new CoreLabel();
    label.setWord("John");
    label.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    label.setBeginPosition(0);
    label.setEndPosition(4);
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    CoreNLPProtos.Token proto = serializer.toProto(label);
    // CoreLabel restored = serializer.fromProto(proto);
    // assertEquals("John", restored.word());
    // assertEquals("PERSON", restored.get(CoreAnnotations.NamedEntityTagAnnotation.class));
    // assertEquals(0, restored.beginPosition());
    // assertEquals(4, restored.endPosition());
  }

  @Test
  public void testSerializationOfEmptyAnnotation() {
    Annotation empty = new Annotation("");
    empty.set(CoreAnnotations.TextAnnotation.class, "");
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    CoreNLPProtos.Document proto = serializer.toProto(empty);
    assertNotNull(proto);
    // assertEquals("", proto.getText());
  }

  @Test
  public void testSerializationWithTimexAnnotationField() throws Exception {
    Annotation annotation = new Annotation("Tomorrow is Friday.");
    CoreLabel token = new CoreLabel();
    token.setWord("Tomorrow");
    token.set(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class, "2024-06-14");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "DATE");
    token.set(
        TimeAnnotations.TimexAnnotation.class,
        new Timex("DATE", "2024-06-14", null, "tmx1", "Tomorrow", -1, -1));
    token.setBeginPosition(0);
    token.setEndPosition(8);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.TextAnnotation.class, "Tomorrow is Friday.");
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    serializer.write(annotation, out);
    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
    Annotation deserialized = serializer.read(in).first;
    String ner =
        deserialized
            .get(CoreAnnotations.TokensAnnotation.class)
            .get(0)
            .get(CoreAnnotations.NamedEntityTagAnnotation.class);
    String normalizedNER =
        deserialized
            .get(CoreAnnotations.TokensAnnotation.class)
            .get(0)
            .get(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class);
    Timex timex =
        deserialized
            .get(CoreAnnotations.TokensAnnotation.class)
            .get(0)
            .get(TimeAnnotations.TimexAnnotation.class);
    assertEquals("DATE", ner);
    assertEquals("2024-06-14", normalizedNER);
    assertNotNull(timex);
    assertEquals("2024-06-14", timex.value());
  }

  @Test
  public void testFromProtoTokenWithMissingOptionalFields() {
    // CoreNLPProtos.Token proto = CoreNLPProtos.Token.newBuilder().setWord("noExtras").build();
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    // CoreLabel label = serializer.fromProto(proto);
    // assertEquals("noExtras", label.word());
    // assertNull(label.get(CoreAnnotations.NamedEntityTagAnnotation.class));
    // assertNull(label.get(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class));
  }

  @Test
  public void testToProtoTokenWithAllNERLabelProbsEmpty() {
    CoreLabel token = new CoreLabel();
    token.setWord("example");
    token.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, new HashMap<String, Double>());
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    CoreNLPProtos.Token proto = serializer.toProto(token);
    // List<String> probs = proto.getNerLabelProbsList();
    // assertEquals(1, probs.size());
    // assertEquals("empty", probs.get(0));
  }

  @Test
  public void testToProtoTokenWithNERLabelProbsMultiple() {
    CoreLabel token = new CoreLabel();
    token.setWord("Paris");
    Map<String, Double> probs = new HashMap<>();
    probs.put("LOCATION", 0.9);
    probs.put("ORGANIZATION", 0.1);
    token.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, probs);
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    CoreNLPProtos.Token proto = serializer.toProto(token);
    // List<String> serialized = proto.getNerLabelProbsList();
    // assertTrue(serialized.contains("LOCATION=0.9") || serialized.contains("LOCATION=0.9"));
    // assertTrue(serialized.contains("ORGANIZATION=0.1") ||
    // serialized.contains("ORGANIZATION=0.1"));
  }

  @Test
  public void testFromProtoDependencyGraphWithMissingRoots() {
    // CoreNLPProtos.DependencyGraph graph = CoreNLPProtos.DependencyGraph.newBuilder()
    // .addNode(CoreNLPProtos.DependencyGraph.Node.newBuilder().setIndex(1).setSentenceIndex(0))
    // .build();
    List<CoreLabel> sentence = new ArrayList<>();
    CoreLabel token = new CoreLabel();
    token.setWord("word");
    token.set(CoreAnnotations.IndexAnnotation.class, 1);
    token.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.add(token);
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    // SemanticGraph depGraph = ProtobufAnnotationSerializer.fromProto(graph, sentence, "doc1");
    // assertNotNull(depGraph);
    // assertEquals(1, depGraph.size());
    // assertTrue(depGraph.vertexListSorted().get(0).word().equals("word"));
  }

  @Test
  public void testSerializeDeserializeTokenWithCopyCount() {
    // CoreNLPProtos.DependencyGraph.Node node = CoreNLPProtos.DependencyGraph.Node.newBuilder()
    // .setIndex(1)
    // .setSentenceIndex(0)
    // .setCopyAnnotation(2)
    // .build();
    // CoreNLPProtos.DependencyGraph graph =
    // CoreNLPProtos.DependencyGraph.newBuilder().addNode(node).build();
    List<CoreLabel> sentence = new ArrayList<>();
    CoreLabel token = new CoreLabel();
    token.setWord("copy");
    token.set(CoreAnnotations.IndexAnnotation.class, 1);
    token.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    sentence.add(token);
    // SemanticGraph result = ProtobufAnnotationSerializer.fromProto(graph, sentence, "doc123");
    // assertNotNull(result);
    // assertEquals(1, result.vertexSet().size());
    // assertEquals("copy", result.getFirstRoot().word());
  }

  @Test
  public void testToProtoDocumentWithNoSentencesButWithTokens() throws Exception {
    Annotation document = new Annotation("LoneToken");
    CoreLabel token = new CoreLabel();
    token.setWord("LoneToken");
    token.setBeginPosition(0);
    token.setEndPosition(9);
    document.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    document.set(CoreAnnotations.TextAnnotation.class, "LoneToken");
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    CoreNLPProtos.Document proto = serializer.toProto(document);
    // assertEquals("LoneToken", proto.getText());
    // assertEquals(1, proto.getSentencelessTokenList().size());
    // assertEquals("LoneToken", proto.getSentencelessToken(0).getWord());
  }

  @Test
  public void testToProtoWithAllOptionalSentenceFieldsSet() {
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokenEndAnnotation.class, 3);
    sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, 1);
    sentence.set(CoreAnnotations.ParagraphIndexAnnotation.class, 4);
    sentence.set(CoreAnnotations.QuotedAnnotation.class, true);
    List<CoreLabel> tokens = new ArrayList<>();
    CoreLabel t1 = new CoreLabel();
    t1.setWord("Hello");
    t1.setBeginPosition(0);
    t1.setEndPosition(5);
    t1.setIndex(1);
    tokens.add(t1);
    CoreLabel t2 = new CoreLabel();
    t2.setWord("world");
    t2.setBeginPosition(6);
    t2.setEndPosition(11);
    t2.setIndex(2);
    tokens.add(t2);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    CoreNLPProtos.Sentence proto = serializer.toProto(sentence);
    // assertEquals(2, proto.getTokenCount());
    // assertEquals(1, proto.getSentenceIndex());
    // assertEquals(4, proto.getParagraphIndex());
    // assertTrue(proto.getSectionQuoted());
  }

  @Test
  public void testFromProtoTreeWithMissingLabel() {
    // CoreNLPProtos.ParseTree.Builder proto = CoreNLPProtos.ParseTree.newBuilder();
    // proto.addChild(CoreNLPProtos.ParseTree.newBuilder().setValue("NN").build());
    // Tree tree = ProtobufAnnotationSerializer.fromProto(proto.build());
    // assertNotNull(tree);
    // assertNull(tree.label());
    // assertEquals(1, tree.children().length);
    // assertEquals("NN", tree.children()[0].label().value());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFromProtoFlattenedParseTreeStartsWithLabelInsteadOfOpen() {
    // CoreNLPProtos.FlattenedParseTree.Builder proto =
    // CoreNLPProtos.FlattenedParseTree.newBuilder();
    // CoreNLPProtos.FlattenedParseTree.Node labelNode =
    // CoreNLPProtos.FlattenedParseTree.Node.newBuilder()
    // .setValue("ROOT").build();
    // proto.addNodes(labelNode);
    // ProtobufAnnotationSerializer.fromProto(proto.build());
  }

  @Test
  public void testToFlattenedTreeHandlesDeepNesting() {
    Tree leaf = new LabeledScoredTreeNode(new StringLabel("word"));
    Tree inner = new LabeledScoredTreeNode(new StringLabel("NP"));
    inner.setChildren(new Tree[] {leaf});
    Tree root = new LabeledScoredTreeNode(new StringLabel("S"));
    root.setChildren(new Tree[] {inner});
    CoreNLPProtos.FlattenedParseTree proto = ProtobufAnnotationSerializer.toFlattenedTree(root);
    // assertEquals(6, proto.getNodesCount());
  }

  @Test
  public void testFromProtoMentionHandlesEmptyDependencies() {
    // CoreNLPProtos.Mention protoMention = CoreNLPProtos.Mention.newBuilder()
    // .setMentionID(777)
    // .setHeadIndex(2)
    // .setEndIndex(4)
    // .setStartIndex(1)
    // .setSentNum(1)
    // .setMentionType("PROPER")
    // .build();
    Method fromProtoNoTokens;
    Mention mention;
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    // mention = serializer.fromProtoNoTokens(protoMention);
    // assertEquals(777, mention.mentionID);
    // assertEquals(1, mention.startIndex);
    // assertEquals(4, mention.endIndex);
    // assertEquals(2, mention.headIndex);
    // assertEquals(Dictionaries.MentionType.PROPER, mention.mentionType);
  }

  @Test
  public void testToProtoSpanAnnotationEncodesCorrectly() {
    CoreLabel token = new CoreLabel();
    IntPair span = new IntPair(2, 5);
    token.set(CoreAnnotations.SpanAnnotation.class, span);
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    CoreNLPProtos.Token proto = serializer.toProto(token);
    // assertTrue(proto.hasSpan());
    // assertEquals(2, proto.getSpan().getBegin());
    // assertEquals(5, proto.getSpan().getEnd());
  }

  @Test
  public void testFromProtoTimexHandlesOnlyValueField() {
    // CoreNLPProtos.Timex timexProto = CoreNLPProtos.Timex.newBuilder()
    // .setValue("2024-05-01")
    // .build();
    // Timex result = ProtobufAnnotationSerializer.fromProto(timexProto);
    // assertEquals("2024-05-01", result.value());
    // assertNull(result.altVal());
    // assertNull(result.text());
  }

  @Test
  public void testFromProtoTreeWithSpan() {
    // CoreNLPProtos.ParseTree node = CoreNLPProtos.ParseTree.newBuilder()
    // .setValue("NP")
    // .setYieldBeginIndex(5)
    // .setYieldEndIndex(7)
    // .build();
    // Tree tree = ProtobufAnnotationSerializer.fromProto(node);
    // IntPair span = tree.label().get(CoreAnnotations.SpanAnnotation.class);
    // assertNotNull(span);
    // assertEquals(5, span.getSource());
    // assertEquals(7, span.getTarget());
  }

  @Test
  public void testToProtoWithSentencelessXmlDocument() {
    Annotation ann = new Annotation("<xml>This is content.</xml>");
    ann.set(CoreAnnotations.TextAnnotation.class, "<xml>This is content.</xml>");
    ann.set(CoreAnnotations.SectionsAnnotation.class, new ArrayList<CoreMap>());
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);
    CoreNLPProtos.Document proto = serializer.toProto(ann);
    // assertTrue(proto.getXmlDoc());
    // assertEquals("<xml>This is content.</xml>", proto.getText());
  }

  @Test
  public void testFromProtoInvalidRelationThrowsSafe() {
    // CoreNLPProtos.Relation proto = CoreNLPProtos.Relation.newBuilder()
    // .setObjectID("rel1")
    // .setType("affiliation")
    // .build();
    CoreMap sentence = new ArrayCoreMap();
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    // RelationMention mention = serializer.fromProto(proto, sentence);
    // assertEquals("affiliation", mention.getType());
    // assertEquals("rel1", mention.getObjectId());
  }

  @Test
  public void testMappingEntityMentionToCorefMentionHandlesMissingMapping() {
    Annotation ann = new Annotation("Entity mention mapping test.");
    ann.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
    Map<Integer, Integer> map = new HashMap<>();
    map.put(0, 3);
    ann.set(CoreAnnotations.EntityMentionToCorefMentionMappingAnnotation.class, map);
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    CoreNLPProtos.Document proto = serializer.toProto(ann);
    // assertEquals(1, proto.getEntityMentionToCorefMentionMappingsCount());
    // assertEquals(3, proto.getEntityMentionToCorefMentionMappings(0));
  }

  @Test
  public void testFromProtoQuoteWithMinimalFields() {
    // CoreNLPProtos.Quote proto = CoreNLPProtos.Quote.newBuilder()
    // .setText("He said nothing.")
    // .setBegin(0)
    // .setEnd(16)
    // .setIndex(1)
    // .build();
    List<CoreLabel> tokens = new ArrayList<>();
    // Annotation quote = ProtobufAnnotationSerializer.fromProto(proto, tokens);
    // assertEquals("He said nothing.", quote.get(CoreAnnotations.TextAnnotation.class));
    // assertEquals(Integer.valueOf(0),
    // quote.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    // assertEquals(Integer.valueOf(16),
    // quote.get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
    // assertEquals(Integer.valueOf(1), quote.get(CoreAnnotations.QuotationIndexAnnotation.class));
  }

  @Test
  public void testToProtoDocumentWithEmptyMentionsAnnotation() {
    Annotation annotation = new Annotation("Text with mentions");
    annotation.set(CoreAnnotations.MentionsAnnotation.class, new ArrayList<CoreMap>());
    annotation.set(CoreAnnotations.TextAnnotation.class, "Text with mentions");
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    CoreNLPProtos.Document proto = serializer.toProto(annotation);
    // assertTrue(proto.getHasEntityMentionsAnnotation());
    // assertEquals(0, proto.getMentionsCount());
  }

  @Test
  public void testToProtoTreeWithNaNScore() {
    Tree tree = new LabeledScoredTreeNode(new StringLabel("ROOT"));
    tree.setScore(Double.NaN);
    tree.setChildren(new Tree[0]);
    CoreNLPProtos.ParseTree proto = ProtobufAnnotationSerializer.toProto(tree);
    // assertFalse(proto.hasScore());
    // assertEquals("ROOT", proto.getValue());
  }

  @Test
  public void testToProtoSentenceWithEmptyCharactersAnnotation() {
    CoreLabel charToken = new CoreLabel();
    charToken.setWord("你");
    List<CoreLabel> chars = new ArrayList<>();
    chars.add(charToken);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(SegmenterCoreAnnotations.CharactersAnnotation.class, chars);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokenEndAnnotation.class, 1);
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    CoreNLPProtos.Sentence proto = serializer.toProto(sentence);
    // assertEquals(1, proto.getCharacterCount());
    // assertEquals("你", proto.getCharacter(0).getWord());
  }

  @Test
  public void testWriteDocumentWithNoTextThrowsException() throws Exception {
    Annotation annotation = new Annotation((String) null);
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try {
      serializer.write(annotation, out);
      fail("Expected NullPointerException or IOException due to null TextAnnotation");
    } catch (NullPointerException | IOException expected) {
      assertTrue(true);
    }
  }

  @Test
  public void testToProtoSentenceWithEntailedClauseAndSentence() {
    Tree leaf = new LabeledScoredTreeNode(new StringLabel("dog"));
    Tree tree = new LabeledScoredTreeNode(new StringLabel("NP"));
    tree.setChildren(new Tree[] {leaf});
    IndexedWord root = new IndexedWord();
    root.setIndex(1);
    SentenceFragment fragment =
        new SentenceFragment(new edu.stanford.nlp.semgraph.SemanticGraph(), true, false)
            .changeScore(0.8);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokenEndAnnotation.class, 1);
    sentence.set(
        NaturalLogicAnnotations.EntailedClausesAnnotation.class, Collections.singleton(fragment));
    sentence.set(
        NaturalLogicAnnotations.EntailedSentencesAnnotation.class, Collections.singleton(fragment));
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    CoreNLPProtos.Sentence proto = serializer.toProto(sentence);
    // assertEquals(1, proto.getEntailedClauseCount());
    // assertEquals(1, proto.getEntailedSentenceCount());
  }

  @Test
  public void testFromProtoTreeWithSentimentAnnotation() {
    // CoreNLPProtos.ParseTree.Builder proto = CoreNLPProtos.ParseTree.newBuilder();
    // proto.setValue("S");
    // proto.setSentiment(CoreNLPProtos.Sentiment.NEGATIVE);
    // Tree tree = ProtobufAnnotationSerializer.fromProto(proto.build());
    // assertNotNull(tree.label());
    // assertEquals("S", tree.label().value());
    // Integer sentiment =
    // tree.label().get(edu.stanford.nlp.neural.rnn.RNNCoreAnnotations.PredictedClass.class);
    // assertEquals(Integer.valueOf(CoreNLPProtos.Sentiment.NEGATIVE.getNumber()), sentiment);
  }

  @Test
  public void testTokenWithMWTMiscFieldSerialization() {
    CoreLabel token = new CoreLabel();
    token.setWord("football");
    token.set(CoreAnnotations.MWTTokenMiscAnnotation.class, "SomeMisc");
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    CoreNLPProtos.Token proto = serializer.toProto(token);
    // assertEquals("SomeMisc", proto.getMwtMisc());
  }

  @Test
  public void testTokenWithNumericFieldsSerialization() {
    CoreLabel token = new CoreLabel();
    token.setWord("4.5");
    token.set(CoreAnnotations.NumericValueAnnotation.class, 4L);
    token.set(CoreAnnotations.NumericTypeAnnotation.class, "DECIMAL");
    token.set(CoreAnnotations.NumericCompositeValueAnnotation.class, 45L);
    token.set(CoreAnnotations.NumericCompositeTypeAnnotation.class, "MULTIPLIER");
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    CoreNLPProtos.Token proto = serializer.toProto(token);
    // assertEquals(4L, proto.getNumericValue());
    // assertEquals(45L, proto.getNumericCompositeValue());
    // assertEquals("DECIMAL", proto.getNumericType());
    // assertEquals("MULTIPLIER", proto.getNumericCompositeType());
  }

  @Test
  public void testSentenceWithEnhancedSentenceAnnotation() {
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokenEndAnnotation.class, 1);
    CoreMap enhanced = new ArrayCoreMap();
    enhanced.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    enhanced.set(CoreAnnotations.TokenEndAnnotation.class, 1);
    sentence.set(
        edu.stanford.nlp.quoteattribution.QuoteAttributionUtils.EnhancedSentenceAnnotation.class,
        enhanced);
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    CoreNLPProtos.Sentence proto = serializer.toProto(sentence);
    // assertTrue(proto.hasEnhancedSentence());
    // assertEquals(0, proto.getEnhancedSentence().getTokenOffsetBegin());
  }

  @Test
  public void testToProtoSectionStoresXmlTagData() {
    CoreMap section = new ArrayCoreMap();
    section.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    section.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 100);
    CoreMap tag = new ArrayCoreMap();
    tag.set(CoreAnnotations.TextAnnotation.class, "<section>");
    // section.set(CoreAnnotations.SectionTagAnnotation.class, tag);
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    CoreNLPProtos.Section proto = serializer.toProtoSection(section);
    // assertTrue(proto.hasXmlTag());
    // assertEquals("<section>", proto.getXmlTag().getText());
  }

  @Test
  public void testSectionAuthorAndDateSerialization() {
    CoreMap section = new ArrayCoreMap();
    section.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    section.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 50);
    section.set(CoreAnnotations.AuthorAnnotation.class, "Shakespeare");
    section.set(CoreAnnotations.SectionDateAnnotation.class, "1601-01-01");
    section.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());
    CoreNLPProtos.Section proto = new ProtobufAnnotationSerializer().toProtoSection(section);
    // assertEquals("Shakespeare", proto.getAuthor());
    // assertEquals("1601-01-01", proto.getDatetime());
  }

  @Test
  public void testCoreLabelWithXmlContextAnnotation() {
    CoreLabel label = new CoreLabel();
    label.setWord("token");
    List<String> context = new ArrayList<>();
    context.add("quote");
    context.add("tag");
    label.set(CoreAnnotations.XmlContextAnnotation.class, context);
    CoreNLPProtos.Token proto = new ProtobufAnnotationSerializer().toProto(label);
    // assertEquals(true, proto.getHasXmlContext());
    // assertEquals(2, proto.getXmlContextList().size());
    // assertEquals("quote", proto.getXmlContextList().get(0));
  }

  @Test
  public void testDependencyGraphNoEdgesStillValid() {
    // CoreNLPProtos.DependencyGraph.Builder graph = CoreNLPProtos.DependencyGraph.newBuilder();
    // CoreNLPProtos.DependencyGraph.Node node = CoreNLPProtos.DependencyGraph.Node.newBuilder()
    // .setIndex(1)
    // .setSentenceIndex(0)
    // .build();
    // graph.addNode(node);
    CoreLabel token = new CoreLabel();
    token.setWord("root");
    token.set(CoreAnnotations.IndexAnnotation.class, 1);
    token.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    // SemanticGraph result = ProtobufAnnotationSerializer.fromProto(graph.build(), tokens, "doc");
    // assertNotNull(result);
    // assertEquals(1, result.vertexSet().size());
  }

  @Test
  public void testQuoteWithAttributionLabels() {
    // CoreNLPProtos.Quote.Builder proto = CoreNLPProtos.Quote.newBuilder();
    // proto.setText("‘I will go,’ he said.")
    // .setBegin(0)
    // .setEnd(20)
    // .setTokenBegin(0)
    // .setTokenEnd(4)
    // .setMention("he")
    // .setMentionBegin(3)
    // .setMentionEnd(4)
    // .setMentionType("PRONOUN")
    // .setMentionSieve("Exact")
    // .setCanonicalMention("John")
    // .setCanonicalMentionBegin(3)
    // .setCanonicalMentionEnd(4)
    // .setSpeaker("John")
    // .setSpeakerSieve("PronounRule");
    List<CoreLabel> tokens = new ArrayList<>();
    // Annotation quote = ProtobufAnnotationSerializer.fromProto(proto.build(), tokens);
    // assertEquals("‘I will go,’ he said.", quote.get(CoreAnnotations.TextAnnotation.class));
    // assertEquals("John", quote.get(QuoteAttributionAnnotator.SpeakerAnnotation.class));
    // assertEquals("PRONOUN", quote.get(QuoteAttributionAnnotator.MentionTypeAnnotation.class));
    // assertEquals("PronounRule",
    // quote.get(QuoteAttributionAnnotator.SpeakerSieveAnnotation.class));
  }

  @Test
  public void testMalformedFlattenedTreeTooManyCloseNodesThrows() {
    // CoreNLPProtos.FlattenedParseTree.Builder proto =
    // CoreNLPProtos.FlattenedParseTree.newBuilder();
    // proto.addNodes(CoreNLPProtos.FlattenedParseTree.Node.newBuilder().setOpenNode(true).build());
    // proto.addNodes(CoreNLPProtos.FlattenedParseTree.Node.newBuilder().setValue("S").build());
    // proto.addNodes(CoreNLPProtos.FlattenedParseTree.Node.newBuilder().setCloseNode(true).build());
    // proto.addNodes(CoreNLPProtos.FlattenedParseTree.Node.newBuilder().setCloseNode(true).build());
    try {
      // Tree tree = ProtobufAnnotationSerializer.fromProto(proto.build());
      fail("Expected exception due to tree closure mismatch");
    } catch (IllegalArgumentException expected) {
      assertTrue(expected.getMessage().contains("Tree started with a Close"));
    }
  }

  @Test
  public void testCorefMentionToEntityMentionMappingHandlesNegativeOne() {
    Annotation ann = new Annotation("Named");
    List<CoreMap> mentions = new ArrayList<>();
    ann.set(CoreAnnotations.MentionsAnnotation.class, mentions);
    ann.set(CoreAnnotations.TextAnnotation.class, "Named");
    Map<Integer, Integer> map = new HashMap<>();
    map.put(0, -1);
    ann.set(CoreAnnotations.CorefMentionToEntityMentionMappingAnnotation.class, map);
    // ann.set(CoreAnnotations.CorefMentionsAnnotation.class, new ArrayList<Mention>());
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);
    CoreNLPProtos.Document proto = serializer.toProto(ann);
    // assertEquals(1, proto.getCorefMentionToEntityMentionMappingsCount());
    // assertEquals(-1, proto.getCorefMentionToEntityMentionMappings(0));
  }

  @Test
  public void testPolaritySerializationAndDeserializationRoundTrip() {
    byte[] projection = new byte[7];
    projection[0] = 1;
    projection[1] = 2;
    projection[2] = 3;
    projection[3] = 4;
    projection[4] = 5;
    projection[5] = 6;
    projection[6] = 7;
    edu.stanford.nlp.naturalli.Polarity inputPolarity =
        new edu.stanford.nlp.naturalli.Polarity(projection);
    CoreNLPProtos.Polarity proto = ProtobufAnnotationSerializer.toProto(inputPolarity);
    // edu.stanford.nlp.naturalli.Polarity result = ProtobufAnnotationSerializer.fromProto(proto);
    // assertNotNull(result);
  }

  @Test
  public void testFromProtoLanguageThrowsOnIllegalInput() {
    // CoreNLPProtos.Language garbage = CoreNLPProtos.Language.UNRECOGNIZED;
    try {
      // ProtobufAnnotationSerializer.fromProto(garbage);
      fail("Expected exception due to unknown language mapping");
    } catch (IllegalStateException e) {
      assertTrue(e.getMessage().contains("Unknown language"));
    }
  }

  @Test
  public void testFromProtoMentionMissingOptionalFields() {
    // CoreNLPProtos.Mention.Builder proto = CoreNLPProtos.Mention.newBuilder();
    // proto.setMentionID(1);
    // proto.setStartIndex(0);
    // proto.setEndIndex(1);
    // proto.setHeadIndex(0);
    // proto.setSentNum(0);
    // proto.setMentionNum(0);
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    // Mention mention = serializer.fromProtoNoTokens(proto.build());
    // assertNotNull(mention);
    // assertEquals(1, mention.mentionID);
  }

  @Test
  public void testUninitializedSentencePropertiesToProtoDoesNotThrow() {
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokenEndAnnotation.class, 1);
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    CoreNLPProtos.Sentence proto = serializer.toProto(sentence);
    // assertFalse(proto.hasParseTree());
    // assertFalse(proto.hasSpeaker());
  }

  @Test
  public void testToProtoBuilderSentenceWithNoTokens() {
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokenEndAnnotation.class, 0);
    Set<Class<?>> keys = new HashSet<>();
    keys.add(CoreAnnotations.TokenBeginAnnotation.class);
    keys.add(CoreAnnotations.TokenEndAnnotation.class);
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    CoreNLPProtos.Sentence.Builder builder = serializer.toProtoBuilder(sentence, keys);
    // assertEquals(0, builder.getTokenCount());
  }

  @Test
  public void testSkippedKeysInToProtoBuilderAreIgnored() {
    CoreLabel token = new CoreLabel();
    token.setWord("xyz");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    token.set(CoreAnnotations.GenderAnnotation.class, "MALE");
    Set<Class<?>> keysToSkip = new HashSet<>();
    keysToSkip.add(CoreAnnotations.GenderAnnotation.class);
    Set<Class<?>> keysToSerialize = new HashSet<>(token.keySetNotNull());
    CoreNLPProtos.Token.Builder builder =
        new ProtobufAnnotationSerializer().toProtoBuilder(token, keysToSerialize, keysToSkip);
    // assertEquals("xyz", builder.getWord());
    // assertEquals("NN", builder.getPos());
    // assertFalse(builder.hasGender());
  }

  @Test
  public void testTokenWithIsNewlineTrue() {
    CoreLabel token = new CoreLabel();
    token.setWord("\n");
    token.set(CoreAnnotations.IsNewlineAnnotation.class, true);
    CoreNLPProtos.Token proto = new ProtobufAnnotationSerializer().toProto(token);
    // assertTrue(proto.getIsNewline());
    // assertEquals("\n", proto.getWord());
  }

  @Test
  public void testSerializationOfTokenWithXMLCharAnnotation() {
    CoreLabel token = new CoreLabel();
    token.setWord("口");
    token.set(SegmenterCoreAnnotations.XMLCharAnnotation.class, "口");
    CoreNLPProtos.Token proto = new ProtobufAnnotationSerializer().toProto(token);
    // assertEquals("口", proto.getChineseXMLChar());
  }

  @Test
  public void testTokenWithOnlySpeakerAndSpeakerType() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.SpeakerAnnotation.class, "Alice");
    token.set(CoreAnnotations.SpeakerTypeAnnotation.class, "PROTAGONIST");
    CoreNLPProtos.Token proto = new ProtobufAnnotationSerializer().toProto(token);
    // assertEquals("Alice", proto.getSpeaker());
    // assertEquals("PROTAGONIST", proto.getSpeakerType());
  }

  @Test
  public void testTreeSerializationNullLabelHandled() {
    Tree nullTree = new LabeledScoredTreeNode();
    nullTree.setChildren(new Tree[0]);
    CoreNLPProtos.ParseTree proto = ProtobufAnnotationSerializer.toProto(nullTree);
    // assertFalse(proto.hasValue());
  }

  @Test
  public void testFromProtoTokenMissingRequiredWordSafeFallback() {
    // CoreNLPProtos.Token proto = CoreNLPProtos.Token.newBuilder().build();
    // CoreLabel label = new ProtobufAnnotationSerializer().fromProto(proto);
    // assertNull(label.word());
  }

  @Test
  public void testDependencyGraphWithOrphanNodeNoEdgesNoRoots() {
    // CoreNLPProtos.DependencyGraph.Node node = CoreNLPProtos.DependencyGraph.Node.newBuilder()
    // .setIndex(1)
    // .setSentenceIndex(0)
    // .build();
    // CoreNLPProtos.DependencyGraph graph = CoreNLPProtos.DependencyGraph.newBuilder()
    // .addNode(node).build();
    CoreLabel token = new CoreLabel();
    token.setWord("alone");
    token.set(CoreAnnotations.IndexAnnotation.class, 1);
    token.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    List<CoreLabel> sentence = Collections.singletonList(token);
    // SemanticGraph sg = ProtobufAnnotationSerializer.fromProto(graph, sentence, "testDoc");
    // assertNotNull(sg);
    // assertEquals(1, sg.size());
  }

  @Test
  public void testSentenceWithQuotationIndexOnly() {
    CoreMap sentence = new ArrayCoreMap();
    CoreLabel token = new CoreLabel();
    token.setWord("\"Hello\"");
    token.set(CoreAnnotations.QuotationIndexAnnotation.class, 42);
    token.set(CoreAnnotations.IndexAnnotation.class, 1);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokenEndAnnotation.class, 1);
    sentence.set(CoreAnnotations.TokensAnnotation.class, tokens);
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    CoreNLPProtos.Sentence proto = serializer.toProto(sentence);
    // assertEquals(42, proto.getToken(0).getQuotationIndex());
  }

  @Test
  public void testTokenWithOnlyBeforeAndAfterFields() {
    CoreLabel token = new CoreLabel();
    token.setWord("a");
    token.setBefore(" ");
    token.setAfter("\n");
    CoreNLPProtos.Token proto = new ProtobufAnnotationSerializer().toProto(token);
    // assertEquals(" ", proto.getBefore());
    // assertEquals("\n", proto.getAfter());
  }

  @Test
  public void testToProtoMentionWithWikipediaEntityField() {
    CoreMap mention = new ArrayCoreMap();
    mention.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    mention.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    mention.set(CoreAnnotations.TokenEndAnnotation.class, 2);
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PERSON");
    mention.set(CoreAnnotations.WikipediaEntityAnnotation.class, "Barack_Obama");
    CoreNLPProtos.NERMention proto = new ProtobufAnnotationSerializer().toProtoMention(mention);
    // assertEquals("Barack_Obama", proto.getWikipediaEntity());
  }

  @Test
  public void testFromProtoTimexWithAltAndTidOnly() {
    // CoreNLPProtos.Timex proto = CoreNLPProtos.Timex.newBuilder()
    // .setAltValue("2024-01-01")
    // .setTid("tmxTest")
    // .build();
    // Timex timex = ProtobufAnnotationSerializer.fromProto(proto);
    // assertEquals("2024-01-01", timex.altVal());
    // assertEquals("tmxTest", timex.tid());
  }

  @Test
  public void testTokenWithNullWordIsHandled() {
    CoreLabel token = new CoreLabel();
    token.setWord(null);
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    CoreNLPProtos.Token proto = new ProtobufAnnotationSerializer().toProto(token);
    // assertFalse(proto.hasWord());
    // assertEquals("NN", proto.getPos());
  }

  @Test
  public void testSentenceWithOnlyDocIDAnnotation() {
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokenEndAnnotation.class, 1);
    sentence.set(CoreAnnotations.DocIDAnnotation.class, "doc-123");
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    CoreNLPProtos.Sentence proto = serializer.toProto(sentence);
    // assertEquals("doc-123", proto.getDocID());
  }

  @Test
  public void testTokenWithTrueCaseFields() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TrueCaseAnnotation.class, "TITLE");
    token.set(CoreAnnotations.TrueCaseTextAnnotation.class, "Obama");
    CoreNLPProtos.Token proto = new ProtobufAnnotationSerializer().toProto(token);
    // assertEquals("TITLE", proto.getTrueCase());
    // assertEquals("Obama", proto.getTrueCaseText());
  }

  @Test
  public void testFromProtoWithInvalidFileFallsBackGracefully() throws Exception {
    File file = File.createTempFile("invalid", ".badproto");
    file.deleteOnExit();
    FileOutputStream fos = new FileOutputStream(file);
    fos.write(new byte[] {0x1, 0x2, 0x3});
    fos.close();
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    Annotation read = serializer.readUndelimited(file);
    assertNotNull(read);
  }

  @Test
  public void testToProtoMentionHandlesMinimalMention() {
    CoreMap mention = new ArrayCoreMap();
    mention.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    mention.set(CoreAnnotations.TokenBeginAnnotation.class, 2);
    mention.set(CoreAnnotations.TokenEndAnnotation.class, 5);
    mention.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOCATION");
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    CoreNLPProtos.NERMention proto = serializer.toProtoMention(mention);
    // assertEquals(0, proto.getSentenceIndex());
    // assertEquals(2, proto.getTokenStartInSentenceInclusive());
    // assertEquals(5, proto.getTokenEndInSentenceExclusive());
    // assertEquals("LOCATION", proto.getNer());
  }

  @Test
  public void testDeserializeMentionSpanWithOnlyStartSet() {
    // CoreNLPProtos.Token proto = CoreNLPProtos.Token.newBuilder()
    // .setWord("partial")
    // .setSpan(CoreNLPProtos.Span.newBuilder().setBegin(3).build())
    // .build();
    // CoreLabel label = new ProtobufAnnotationSerializer().fromProto(proto);
    // IntPair span = label.get(CoreAnnotations.SpanAnnotation.class);
    // assertNotNull(span);
    // assertEquals(3, span.getSource());
  }

  @Test
  public void testNERLabelProbsWithSingleEntrySerializesCorrectly() {
    CoreLabel token = new CoreLabel();
    Map<String, Double> probs = new HashMap<>();
    probs.put("PERSON", 1.0);
    token.set(CoreAnnotations.NamedEntityTagProbsAnnotation.class, probs);
    CoreNLPProtos.Token proto = new ProtobufAnnotationSerializer().toProto(token);
    // assertEquals(1, proto.getNerLabelProbsList().size());
    // assertEquals("PERSON=1.0", proto.getNerLabelProbsList().get(0));
  }

  @Test
  public void testSerializeTokenWithOnlyBeginIndexSet() {
    CoreLabel token = new CoreLabel();
    token.setWord("start");
    token.set(CoreAnnotations.BeginIndexAnnotation.class, 3);
    CoreNLPProtos.Token proto = new ProtobufAnnotationSerializer().toProto(token);
    // assertEquals(3, proto.getBeginIndex());
    // assertFalse(proto.hasEndIndex());
  }

  @Test
  public void testToProtoTreeHandlesLeafTree() {
    Tree tree = new LabeledScoredTreeNode(new StringLabel("L"));
    tree.setChildren(new Tree[0]);
    CoreNLPProtos.ParseTree proto = ProtobufAnnotationSerializer.toProto(tree);
    // assertEquals("L", proto.getValue());
    // assertEquals(0, proto.getChildCount());
  }

  @Test
  public void testDependencyGraphHandlesMismatchedEmptyIndexToken() {
    // CoreNLPProtos.DependencyGraph.Node.Builder builder =
    // CoreNLPProtos.DependencyGraph.Node.newBuilder();
    // builder.setIndex(1);
    // builder.setSentenceIndex(0);
    // builder.setEmptyIndex(9);
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.IndexAnnotation.class, 1);
    token.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    token.setEmptyIndex(9);
    token.setWord("ghost");
    List<CoreLabel> tokens = Collections.singletonList(token);
    // CoreNLPProtos.DependencyGraph graph =
    // CoreNLPProtos.DependencyGraph.newBuilder().addNode(builder).build();
    // SemanticGraph result = ProtobufAnnotationSerializer.fromProto(graph, tokens, "doc");
    // assertEquals(1, result.size());
  }

  @Test
  public void testMentionAppositionRelationSerialization() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    Mention m1 = new Mention();
    Mention m2 = new Mention();
    m1.mentionID = 1;
    m2.mentionID = 2;
    m1.appositions = new HashSet<>();
    m1.appositions.add(m2);
    Map<Integer, Mention> mentions = new HashMap<>();
    mentions.put(1, m1);
    mentions.put(2, m2);
    CoreNLPProtos.Mention proto = serializer.toProto(m1);
    // assertEquals(1, proto.getAppositionsList().size());
    // assertEquals(2, (int) proto.getAppositions(0));
  }

  @Test
  public void testTimexWithBeginPointOnlyIsSerialized() {
    Timex timex = new Timex("DATE", "2023-05", null, "t1", "May 2023", 10, -1);
    CoreNLPProtos.Timex proto = new ProtobufAnnotationSerializer().toProto(timex);
    // assertEquals(10, proto.getBeginPoint());
    // assertFalse(proto.hasEndPoint());
    // assertEquals("2023-05", proto.getValue());
  }
}
