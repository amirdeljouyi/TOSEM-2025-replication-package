public class ProtobufAnnotationSerializer_wosr_5_GPTLLMTest { 

 @Test
  public void testDefaultConstructorEnforcesLosslessSerialization() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    assertTrue(serializer.enforceLosslessSerialization);
  }
@Test
  public void testBooleanConstructorSetsFlagFalse() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);
    assertFalse(serializer.enforceLosslessSerialization);
  }
@Test(expected = ProtobufAnnotationSerializer.LossySerializationException.class)
  public void testLossySerializationThrowsExceptionWhenEnforced() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel label = new CoreLabel();
    label.setValue("value");
    label.setIndex(5);
    label.setNER("PERSON");
    serializer.toProto(label); 
  }
@Test
  public void testLossySerializationDoesNotThrowWhenNotEnforced() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);
    CoreLabel label = new CoreLabel();
    label.setValue("value");
    label.setIndex(3);
    label.setNER("LOCATION");
    CoreNLPProtos.Token proto = serializer.toProto(label);
    assertEquals("LOCATION", proto.getNer());
  }
@Test
  public void testSerializationDeserializationOfMinimalAnnotation() throws IOException, ClassNotFoundException {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);

    Annotation input = new Annotation("This is a test.");
    Annotation output;

    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    serializer.write(input, outStream);

    ByteArrayInputStream inStream = new ByteArrayInputStream(outStream.toByteArray());
    output = serializer.read(inStream).first;

    assertEquals("This is a test.", output.get(TextAnnotation.class));
  }
@Test
  public void testSerializationPreservesTextWhenTokensPresent() throws IOException {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);

    Annotation annotation = new Annotation("Testing serialization.");
    List<CoreLabel> tokens = new ArrayList<>();

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Testing");
    token1.setBeginPosition(0);
    token1.setEndPosition(7);
    tokens.add(token1);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("serialization");
    token2.setBeginPosition(8);
    token2.setEndPosition(21);
    tokens.add(token2);

    CoreLabel token3 = new CoreLabel();
    token3.setWord(".");
    token3.setBeginPosition(21);
    token3.setEndPosition(22);
    tokens.add(token3);

    annotation.set(TokensAnnotation.class, tokens);
    annotation.set(TextAnnotation.class, "Testing serialization.");

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    serializer.write(annotation, baos);

    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    Annotation deserialized = serializer.read(bais).first;
    assertEquals("Testing serialization.", deserialized.get(TextAnnotation.class));
  }
@Test
  public void testSerializationWithDocId() throws IOException, ClassNotFoundException {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);

    Annotation ann = new Annotation("This is a doc.");
    ann.set(CoreAnnotations.DocIDAnnotation.class, "testDoc");

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    serializer.write(ann, os);

    ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
    Annotation deserialized = serializer.read(is).first;

    assertEquals("testDoc", deserialized.get(CoreAnnotations.DocIDAnnotation.class));
  }
@Test
  public void testFromProtoTreeWithEmptyTreeReturnsNull() {
    CoreNLPProtos.FlattenedParseTree emptyTree = CoreNLPProtos.FlattenedParseTree.newBuilder().build();
    Tree tree = ProtobufAnnotationSerializer.fromProto(emptyTree);
    assertNull(tree);
  }
@Test(expected = IllegalArgumentException.class)
  public void testFromProtoTreeThrowsOnInvalidCloseNode() {
    CoreNLPProtos.FlattenedParseTree.Node node = CoreNLPProtos.FlattenedParseTree.Node.newBuilder().setCloseNode(true).build();
    CoreNLPProtos.FlattenedParseTree tree = CoreNLPProtos.FlattenedParseTree.newBuilder().addNodes(node).build();
    ProtobufAnnotationSerializer.fromProto(tree);
  }
@Test(expected = IllegalArgumentException.class)
  public void testFromProtoTreeThrowsOnUnfinishedTree() {
    CoreNLPProtos.FlattenedParseTree.Node open = CoreNLPProtos.FlattenedParseTree.Node.newBuilder().setOpenNode(true).build();
    CoreNLPProtos.FlattenedParseTree.Node label = CoreNLPProtos.FlattenedParseTree.Node.newBuilder().setValue("NP").build();
    CoreNLPProtos.FlattenedParseTree tree = CoreNLPProtos.FlattenedParseTree.newBuilder()
        .addNodes(open)
        .addNodes(label)
        .build();
    ProtobufAnnotationSerializer.fromProto(tree);
  }
@Test(expected = IllegalArgumentException.class)
  public void testFromProtoTreeThrowsOnMissingOpenNodeBeforeLabel() {
    CoreNLPProtos.FlattenedParseTree.Node node = CoreNLPProtos.FlattenedParseTree.Node.newBuilder().setValue("NP").build();
    CoreNLPProtos.FlattenedParseTree tree = CoreNLPProtos.FlattenedParseTree.newBuilder().addNodes(node).build();
    ProtobufAnnotationSerializer.fromProto(tree);
  }
@Test(expected = IllegalArgumentException.class)
  public void testFromProtoTreeThrowsIfChildBeforeLabel() {
    CoreNLPProtos.FlattenedParseTree.Node open = CoreNLPProtos.FlattenedParseTree.Node.newBuilder().setOpenNode(true).build();
    CoreNLPProtos.FlattenedParseTree.Node open2 = CoreNLPProtos.FlattenedParseTree.Node.newBuilder().setOpenNode(true).build();
    CoreNLPProtos.FlattenedParseTree.Node label = CoreNLPProtos.FlattenedParseTree.Node.newBuilder().setValue("NP").build();
    CoreNLPProtos.FlattenedParseTree.Node close2 = CoreNLPProtos.FlattenedParseTree.Node.newBuilder().setCloseNode(true).build();
    CoreNLPProtos.FlattenedParseTree.Node close1 = CoreNLPProtos.FlattenedParseTree.Node.newBuilder().setCloseNode(true).build();

    CoreNLPProtos.FlattenedParseTree tree = CoreNLPProtos.FlattenedParseTree.newBuilder()
        .addNodes(open)
        .addNodes(open2)
        .addNodes(label)
        .addNodes(close2)
        .addNodes(close1)
        .build();

    ProtobufAnnotationSerializer.fromProto(tree);
  }
@Test
  public void testCustomKeyCausesLossySerializationException() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    CoreLabel label = new CoreLabel();
    label.setWord("custom");
    label.set(CoreLabel.OutputLabelAnnotation.class, "CUSTOM");
    try {
      serializer.toProto(label);
      fail("Expected LossySerializationException to be thrown");
    } catch (LossySerializationException e) {
      assertTrue(e.getMessage().contains("Keys are not being serialized"));
    }
  }
@Test
  public void testToProtoBuilderSkipsUnknownKeys() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);
    CoreLabel label = new CoreLabel();
    label.setWord("word");
    label.set(CoreLabel.OutputLabelAnnotation.class, "custom");

    Set<Class<?>> keysToSkip = new HashSet<>();
    keysToSkip.add(CoreLabel.OutputLabelAnnotation.class);

    CoreNLPProtos.Token proto = serializer.toProto(label, keysToSkip);
    assertEquals("word", proto.getWord());
  } 
}