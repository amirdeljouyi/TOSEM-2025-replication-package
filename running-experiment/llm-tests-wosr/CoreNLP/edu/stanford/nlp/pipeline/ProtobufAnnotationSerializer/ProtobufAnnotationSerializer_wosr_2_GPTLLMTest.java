public class ProtobufAnnotationSerializer_wosr_2_GPTLLMTest { 

 @Test
  public void testToFromProtoCoreLabelLossless() {
    CoreLabel label = new CoreLabel();
    label.setWord("Stanford");
    label.setTag("NNP");
    label.setIndex(1);
    label.setBefore(" ");
    label.setAfter(" ");
    label.setNER("ORGANIZATION");

    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreNLPProtos.Token proto = serializer.toProto(label);
    CoreLabel deserialized = serializer.fromProto(proto);

    assertEquals("Stanford", deserialized.word());
    assertEquals("NNP", deserialized.tag());
    assertEquals("ORGANIZATION", deserialized.ner());
    assertEquals(" ", deserialized.before());
    assertEquals(" ", deserialized.after());
    assertEquals(1, deserialized.index());
  }
@Test
  public void testToProtoCoreLabelLossySerializationException() {
    CoreLabel label = new CoreLabel();
    label.setWord("test");
    label.setTag("VB");
    label.setNER("O");
    label.set(CoreAnnotations.HeadWordLabelAnnotation.class, "HEAD");

    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);

    boolean exceptionThrown = false;
    try {
      serializer.toProto(label);
    } catch (ProtobufAnnotationSerializer.LossySerializationException e) {
      exceptionThrown = true;
    }

    assertTrue("Expected LossySerializationException for unmapped annotation", exceptionThrown);
  }
@Test
  public void testToProtoTreeSerialization() {
    Tree leaf = new LabeledScoredTreeNode();
    leaf.setLabel(new CoreLabel());
    leaf.label().setValue("dog");
    leaf.setScore(1.0);

    Tree noun = new LabeledScoredTreeNode();
    noun.setLabel(new CoreLabel());
    noun.label().setValue("NP");
    noun.setChildren(new Tree[]{leaf});

    Tree root = new LabeledScoredTreeNode();
    root.setLabel(new CoreLabel());
    root.label().setValue("S");
    root.setChildren(new Tree[]{noun});

    CoreNLPProtos.ParseTree proto = ProtobufAnnotationSerializer.toProto(root);
    Tree deserialized = ProtobufAnnotationSerializer.fromProto(proto);

    assertEquals("S", deserialized.label().value());
    assertEquals("NP", deserialized.children()[0].label().value());
    assertEquals("dog", deserialized.children()[0].children()[0].label().value());
  }
@Test
  public void testSerializeDeserializeTimex() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();

    Timex timex = new Timex("DATE", "2024-06-01", "2024-06-01", "t1", "June 1, 2024", 0, 0);

    CoreNLPProtos.Timex proto = serializer.toProto(timex);
    Timex deserialized = serializer.fromProto(proto);

    assertEquals("DATE", deserialized.timexType());
    assertEquals("2024-06-01", deserialized.value());
    assertEquals("t1", deserialized.tid());
    assertEquals("June 1, 2024", deserialized.text());
  }
@Test(expected = IllegalArgumentException.class)
  public void testInvalidSentenceProtoBuilderFromCoreLabelThrowsException() {
    CoreLabel label = new CoreLabel();
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    serializer.toProtoBuilder(label);
  }
@Test
  public void testToProtoDocumentContainsTokens() throws IOException {
    CoreLabel token = new CoreLabel();
    token.setWord("Stanford");
    token.setNER("ORGANIZATION");
    token.setIndex(1);

    Annotation annotation = new Annotation("Stanford");
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    serializer.write(annotation, os);

    ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
    Pair<Annotation, InputStream> pair = serializer.read(is);
    Annotation result = pair.first;

    List<CoreLabel> deserializedTokens = result.get(CoreAnnotations.TokensAnnotation.class);
    assertNotNull("Deserialized tokens should not be null", deserializedTokens);
    assertEquals("Stanford", deserializedTokens.get(0).word());
    assertEquals("ORGANIZATION", deserializedTokens.get(0).ner());
    assertEquals(1, deserializedTokens.get(0).index());
  }
@Test
  public void testToProtoWithSkippedKeys() {
    CoreLabel label = new CoreLabel();
    label.setWord("bank");
    label.setTag("NN");
    label.setNER("MONEY");

    HashSet<Class<?>> skip = new HashSet<>();
    skip.add(NamedEntityTagAnnotation.class);

    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);
    CoreNLPProtos.Token proto = serializer.toProto(label, skip);

    assertEquals("bank", proto.getWord());
    assertEquals("NN", proto.getPos());
    assertFalse(proto.hasNer());
  }
@Test
  public void testToProtoOptionalFieldsUnset() {
    CoreLabel label = new CoreLabel();
    label.setWord("hi");

    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreNLPProtos.Token proto = serializer.toProto(label);

    assertEquals("hi", proto.getWord());
    assertFalse(proto.hasPos());
    assertFalse(proto.hasNer());
    assertFalse(proto.hasLemma());
  }
@Test
  public void testToFromProtoWithTrueCaseAnnotation() {
    CoreLabel label = new CoreLabel();
    label.setWord("apple");
    label.set(TrueCaseAnnotation.class, "ORIGINAL");
    label.set(TrueCaseTextAnnotation.class, "Apple");

    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreNLPProtos.Token proto = serializer.toProto(label);
    CoreLabel deserialized = serializer.fromProto(proto);

    assertEquals("apple", deserialized.word());
    assertEquals("ORIGINAL", deserialized.get(TrueCaseAnnotation.class));
    assertEquals("Apple", deserialized.get(TrueCaseTextAnnotation.class));
  }
@Test
  public void testToProtoHandlesEmptyNERProbs() {
    CoreLabel label = new CoreLabel();
    label.setWord("U.S.");
    label.setNER("LOCATION");
    Map<String, Double> emptyProbs = new HashMap<>();
    label.set(NamedEntityTagProbsAnnotation.class, emptyProbs);

    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreNLPProtos.Token proto = serializer.toProto(label);

    assertEquals("U.S.", proto.getWord());
    assertEquals("LOCATION", proto.getNer());
    assertEquals(1, proto.getNerLabelProbsCount());
    assertEquals("empty", proto.getNerLabelProbs(0));
  }
@Test
  public void testFromProtoTokenIgnoresMissingOptionalFields() {
    CoreNLPProtos.Token tokenProto = CoreNLPProtos.Token.newBuilder()
        .setWord("hello")
        .build();

    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel result = serializer.fromProto(tokenProto);

    assertEquals("hello", result.word());
    assertNull(result.tag());
    assertNull(result.ner());
  } 
}