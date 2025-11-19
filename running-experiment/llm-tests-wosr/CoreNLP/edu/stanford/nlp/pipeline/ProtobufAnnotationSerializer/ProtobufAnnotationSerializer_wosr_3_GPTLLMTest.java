public class ProtobufAnnotationSerializer_wosr_3_GPTLLMTest { 

 @Test
  public void testTokenSerializationAndDeserialization() {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();

    CoreLabel token = new CoreLabel();
    token.setWord("Stanford");
    token.setTag("NNP");
    token.setNER("ORGANIZATION");
    token.setLemma("Stanford");
    token.setIndex(1);
    token.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);

    CoreNLPProtos.Token protoToken = serializer.toProto(token);
    CoreLabel deserializedToken = serializer.fromProto(protoToken);

    Assert.assertEquals("Stanford", deserializedToken.word());
    Assert.assertEquals("NNP", deserializedToken.tag());
    Assert.assertEquals("ORGANIZATION", deserializedToken.ner());
    Assert.assertEquals("Stanford", deserializedToken.lemma());
    Assert.assertEquals(1, deserializedToken.index());
  }
@Test
  public void testLossySerializationThrowsExceptionWhenEnforced() {
    CoreLabel token = new CoreLabel();
    token.setWord("test");
    token.setTag("NN");

    Set<Class<?>> invalidKeys = new HashSet<>();
    invalidKeys.add(DocumentAnnotation.class); 

    boolean thrown = false;
    try {
      ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
      serializer.toProto(token, invalidKeys);
    } catch (ProtobufAnnotationSerializer.LossySerializationException e) {
      thrown = true;
    }

    Assert.assertTrue("Expected LossySerializationException was not thrown", thrown);
  }
@Test
  public void testLossySerializationIgnoredWhenDisabled() {
    CoreLabel token = new CoreLabel();
    token.setWord("test");
    token.setTag("NN");

    Set<Class<?>> invalidKeys = new HashSet<>();
    invalidKeys.add(DocumentAnnotation.class);

    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);
    CoreNLPProtos.Token protoToken = serializer.toProto(token, invalidKeys);

    Assert.assertNotNull(protoToken);
    Assert.assertEquals("test", protoToken.getWord());
  }
@Test
  public void testSentenceSerializationAndDeserialization() {
    CoreLabel token = new CoreLabel();
    token.setWord("Protobuf");
    token.setTag("NN");
    token.set(CoreAnnotations.SentenceIndexAnnotation.class, 0);
    token.setIndex(1);
    token.set(CharacterOffsetBeginAnnotation.class, 0);
    token.set(CharacterOffsetEndAnnotation.class, 8);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer();
    CoreMap sentence = new edu.stanford.nlp.util.ArrayCoreMap();
    sentence.set(TokensAnnotation.class, tokens);
    sentence.set(TokenBeginAnnotation.class, 0);
    sentence.set(TokenEndAnnotation.class, 1);
    sentence.set(SentenceIndexAnnotation.class, 0);

    CoreNLPProtos.Sentence proto = serializer.toProto(sentence);
    CoreMap deserialized = serializer.fromProto(proto);

    Assert.assertEquals("Protobuf", deserialized.get(CoreAnnotations.TokensAnnotation.class).get(0).word());
    Assert.assertEquals((Integer)0, deserialized.get(TokenBeginAnnotation.class));
    Assert.assertEquals((Integer)1, deserialized.get(TokenEndAnnotation.class));
  }
@Test
  public void testDocumentSerializationAndDeserialization() throws IOException, ClassNotFoundException {
    CoreLabel token = new CoreLabel();
    token.setWord("AI");
    token.setTag("NN");
    token.setNER("ORGANIZATION");
    token.setIndex(1);
    token.set(CharacterOffsetBeginAnnotation.class, 0);
    token.set(CharacterOffsetEndAnnotation.class, 2);
    token.set(SentenceIndexAnnotation.class, 0);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new edu.stanford.nlp.util.ArrayCoreMap();
    sentence.set(TokensAnnotation.class, tokens);
    sentence.set(TokenBeginAnnotation.class, 0);
    sentence.set(TokenEndAnnotation.class, 1);
    sentence.set(SentenceIndexAnnotation.class, 0);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation ann = new Annotation("AI");
    ann.set(CoreAnnotations.TokensAnnotation.class, tokens);
    ann.set(SentencesAnnotation.class, sentences);
    ann.set(CoreAnnotations.DocIDAnnotation.class, "testDoc");

    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    serializer.write(ann, bos);

    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    Pair<Annotation, InputStream> result = serializer.read(bis);

    Annotation deserialized = result.first();

    Assert.assertEquals("AI", deserialized.get(CoreAnnotations.TextAnnotation.class));
    Assert.assertEquals("testDoc", deserialized.get(CoreAnnotations.DocIDAnnotation.class));
    Assert.assertEquals(1, deserialized.get(SentencesAnnotation.class).size());
    CoreMap deserializedSentence = deserialized.get(SentencesAnnotation.class).get(0);
    Assert.assertEquals("AI", deserializedSentence.get(TokensAnnotation.class).get(0).word());
  }
@Test
  public void testReadUndelimitedHandlesDelimitErrorGracefully() throws IOException {
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);
    File tmp = File.createTempFile("corenlp_tmp_proto", ".ser");

    CoreLabel token = new CoreLabel();
    token.setWord("Stanford");
    token.setTag("NNP");
    token.setIndex(1);
    token.set(SentenceIndexAnnotation.class, 0);
    token.set(CharacterOffsetBeginAnnotation.class, 0);
    token.set(CharacterOffsetEndAnnotation.class, 8);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    CoreMap sentence = new edu.stanford.nlp.util.ArrayCoreMap();
    sentence.set(TokensAnnotation.class, tokens);
    sentence.set(TokenBeginAnnotation.class, 0);
    sentence.set(TokenEndAnnotation.class, 1);
    sentence.set(SentenceIndexAnnotation.class, 0);

    List<CoreMap> sentences = new ArrayList<>();
    sentences.add(sentence);

    Annotation ann = new Annotation("Stanford");
    ann.set(CoreAnnotations.TokensAnnotation.class, tokens);
    ann.set(SentencesAnnotation.class, sentences);

    OutputStream os = new FileOutputStream(tmp);
    serializer.write(ann, os);
    os.close();

    Annotation deserialized = serializer.readUndelimited(tmp);
    Assert.assertEquals("Stanford", deserialized.get(TextAnnotation.class));
    tmp.delete();
  }
@Test
  public void testEmptyAnnotationSerialization() throws IOException, ClassNotFoundException {
    Annotation ann = new Annotation("");
    ann.set(TextAnnotation.class, "");

    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    serializer.write(ann, bos);

    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    Pair<Annotation, InputStream> result = serializer.read(bis);

    Annotation deserialized = result.first();
    Assert.assertEquals("", deserialized.get(TextAnnotation.class));
  } 
}