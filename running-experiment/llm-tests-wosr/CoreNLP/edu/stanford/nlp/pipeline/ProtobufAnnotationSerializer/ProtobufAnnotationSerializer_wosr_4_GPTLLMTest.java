public class ProtobufAnnotationSerializer_wosr_4_GPTLLMTest { 

 @Test
  public void testWriteAndReadAnnotation_shouldPreserveText() throws Exception {
    Annotation annotation = new Annotation("This is a test.");
    annotation.set(TextAnnotation.class, "This is a test.");

    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    serializer.write(annotation, out);

    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
    Pair<Annotation, InputStream> result = serializer.read(in);

    Annotation deserialized = result.first;
    assertEquals("This is a test.", deserialized.get(TextAnnotation.class));
  }
@Test
  public void testWriteAndReadEmptyAnnotation_shouldSucceed() throws Exception {
    Annotation annotation = new Annotation("");
    annotation.set(TextAnnotation.class, "");

    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    serializer.write(annotation, out);

    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
    Pair<Annotation, InputStream> result = serializer.read(in);

    Annotation deserialized = result.first;
    assertEquals("", deserialized.get(TextAnnotation.class));
  }
@Test(expected = ProtobufAnnotationSerializer.LossySerializationException.class)
  public void testWrite_withLossySerialization_shouldThrow() throws Exception {
    Annotation annotation = new Annotation("This is a lossy test.");
    annotation.set(TextAnnotation.class, "This is a lossy test.");
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true) {
      @Override
      public CoreNLPProtos.Document toProto(Annotation doc) {
        Set<Class<?>> keysToSerialize = new HashSet<>(doc.keySet());
        
        return CoreNLPProtos.Document.newBuilder().setText("fake").build();
      }
    };

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    serializer.write(annotation, out); 
  }
@Test
  public void testToProtoFromProto_shouldMaintainCoreLabelContent() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();

    CoreLabel label = new CoreLabel();
    label.setWord("Stanford");
    label.setValue("Stanford");
    label.setTag("NNP");

    CoreNLPProtos.Token proto = serializer.toProto(label);
    CoreLabel result = serializer.fromProto(proto);

    assertEquals("Stanford", result.word());
    assertEquals("Stanford", result.value());
    assertEquals("NNP", result.tag());
  }
@Test
  public void testFromProto_emptyToken_shouldReturnMinimalCoreLabel() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();

    CoreNLPProtos.Token proto = CoreNLPProtos.Token.newBuilder().setWord("empty").build();
    CoreLabel result = serializer.fromProto(proto);

    assertEquals("empty", result.word());
    assertNull(result.tag());
  }
@Test
  public void testToProtoCoreLabel_shouldSkipNonSerializableFields() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);

    CoreLabel label = new CoreLabel();
    label.setWord("token");
    label.setValue("token_val");
    label.setTag("JJ");
    Set<Class<?>> keysToSkip = new HashSet<>();
    keysToSkip.add(CoreAnnotations.ValueAnnotation.class);

    CoreNLPProtos.Token result = serializer.toProto(label, keysToSkip);

    assertEquals("token", result.getWord());
    assertEquals("JJ", result.getPos());
    assertFalse(result.hasValue());
  }
@Test
  public void testFromProtoSentence_withTokens_shouldHaveTokenText() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();

    CoreNLPProtos.Token token = CoreNLPProtos.Token.newBuilder().setWord("Hello").build();
    CoreNLPProtos.Sentence sentence = CoreNLPProtos.Sentence.newBuilder()
        .setTokenOffsetBegin(0)
        .setTokenOffsetEnd(1)
        .addToken(token)
        .build();

    CoreMap result = serializer.fromProto(sentence);

    assertNotNull(result);
    List<CoreLabel> tokens = result.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(1, tokens.size());
    assertEquals("Hello", tokens.get(0).word());
  }
@Test
  public void testToProtoAndFromProtoRoundTripCoreLabel_shouldPreserveContent() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();

    CoreLabel label = new CoreLabel();
    label.setWord("testWord");
    label.setTag("NN");
    label.setValue("valueTest");

    CoreNLPProtos.Token proto = serializer.toProto(label);
    CoreLabel result = serializer.fromProto(proto);

    assertEquals("testWord", result.word());
    assertEquals("NN", result.tag());
    assertEquals("valueTest", result.value());
  }
@Test
  public void testToProtoPartialSentence_shouldNotThrow() {
    CoreLabel token = new CoreLabel();
    token.setWord("partial");
    token.setTag("JJ");

    CoreMap sentence = new Annotation("");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    sentence.set(CoreAnnotations.TokenEndAnnotation.class, 1);
    sentence.set(TextAnnotation.class, "partial");

    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    CoreNLPProtos.Sentence proto = serializer.toProto(sentence);

    assertTrue(proto.getTokenCount() > 0);
    assertEquals("partial", proto.getToken(0).getWord());
  }
@Test
  public void testFromProtoDocument_withSentences_shouldRestoreText() throws IOException {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();

    CoreNLPProtos.Token token = CoreNLPProtos.Token.newBuilder().setWord("sample").build();
    CoreNLPProtos.Sentence sentence = CoreNLPProtos.Sentence.newBuilder()
        .setTokenOffsetBegin(0)
        .setTokenOffsetEnd(1)
        .addToken(token)
        .setCharacterOffsetBegin(0)
        .setCharacterOffsetEnd(6)
        .build();

    CoreNLPProtos.Document protoDoc = CoreNLPProtos.Document.newBuilder()
        .setText("sample")
        .addSentence(sentence)
        .build();

    Annotation result = serializer.fromProto(protoDoc);

    assertNotNull(result);
    assertEquals("sample", result.get(TextAnnotation.class));
    assertEquals(1, result.get(CoreAnnotations.SentencesAnnotation.class).size());
    List<CoreLabel> tokens = result.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(1, tokens.size());
    assertEquals("sample", tokens.get(0).word());
  }
@Test
  public void testToProtoTimex_shouldGenerateValue() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    edu.stanford.nlp.time.Timex timex = new edu.stanford.nlp.time.Timex("DATE", "2022-03-01", null, "t1", "March 1", 1, 2);

    CoreNLPProtos.Timex proto = serializer.toProto(timex);

    assertEquals("DATE", proto.getType());
    assertEquals("2022-03-01", proto.getValue());
    assertEquals("March 1", proto.getText());
  } 
}