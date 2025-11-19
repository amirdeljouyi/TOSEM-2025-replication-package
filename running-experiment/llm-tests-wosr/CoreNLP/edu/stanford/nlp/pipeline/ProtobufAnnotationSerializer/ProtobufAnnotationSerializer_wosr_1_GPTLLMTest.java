public class ProtobufAnnotationSerializer_wosr_1_GPTLLMTest { 

 @Test
  public void testToFromProtoCoreLabel_minimalFields() {
    CoreLabel coreLabel = new CoreLabel();
    coreLabel.setWord("Stanford");
    coreLabel.setTag("NNP");
    coreLabel.setIndex(1);

    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);
    CoreNLPProtos.Token proto = serializer.toProto(coreLabel);

    CoreLabel result = serializer.fromProto(proto);
    assertEquals("Stanford", result.word());
    assertEquals("NNP", result.tag());
    assertEquals(1, result.index());
  }
@Test
  public void testToFromProtoCoreLabel_allFields() {
    CoreLabel coreLabel = new CoreLabel();
    coreLabel.setWord("California");
    coreLabel.setTag("NNP");
    coreLabel.setNER("LOCATION");
    coreLabel.setLemma("California");
    coreLabel.setValue("California");
    coreLabel.setOriginalText("California");
    coreLabel.setBeginPosition(0);
    coreLabel.setEndPosition(10);
    coreLabel.setIndex(1);

    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);
    CoreNLPProtos.Token proto = serializer.toProto(coreLabel);
    CoreLabel result = serializer.fromProto(proto);

    assertEquals("California", result.word());
    assertEquals("NNP", result.tag());
    assertEquals("LOCATION", result.ner());
    assertEquals("California", result.lemma());
    assertEquals("California", result.value());
    assertEquals("California", result.originalText());
    assertEquals(0, result.beginPosition());
    assertEquals(10, result.endPosition());
    assertEquals(1, result.index());
  }
@Test
  public void testToFromProtoSentence() {
    CoreLabel tok1 = new CoreLabel();
    tok1.setWord("Stanford");
    tok1.setTag("NNP");
    tok1.setIndex(1);
    tok1.set(SentenceIndexAnnotation.class, 0);

    CoreLabel tok2 = new CoreLabel();
    tok2.setWord("University");
    tok2.setTag("NNP");
    tok2.setIndex(2);
    tok2.set(SentenceIndexAnnotation.class, 0);

    List<CoreLabel> tokens = Arrays.asList(tok1, tok2);
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(TokenBeginAnnotation.class, 0);
    sentence.set(TokenEndAnnotation.class, 2);
    sentence.set(TokensAnnotation.class, tokens);
    sentence.set(SentenceIndexAnnotation.class, 0);

    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);
    CoreNLPProtos.Sentence proto = serializer.toProto(sentence);
    CoreMap result = serializer.fromProtoNoTokens(proto);

    assertEquals(0, result.get(TokenBeginAnnotation.class).intValue());
    assertEquals(2, result.get(TokenEndAnnotation.class).intValue());
    assertEquals(0, result.get(SentenceIndexAnnotation.class).intValue());
  }
@Test
  public void testWriteAndReadAnnotation() throws Exception {
    CoreLabel tok1 = new CoreLabel();
    tok1.setWord("Stanford");
    tok1.setTag("NNP");
    tok1.setIndex(1);
    tok1.set(SentenceIndexAnnotation.class, 0);

    CoreLabel tok2 = new CoreLabel();
    tok2.setWord("University");
    tok2.setTag("NNP");
    tok2.setIndex(2);
    tok2.set(SentenceIndexAnnotation.class, 0);

    List<CoreLabel> tokens = Arrays.asList(tok1, tok2);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(TokenBeginAnnotation.class, 0);
    sentence.set(TokenEndAnnotation.class, 2);
    sentence.set(TokensAnnotation.class, tokens);
    sentence.set(SentenceIndexAnnotation.class, 0);
    sentence.set(TextAnnotation.class, "Stanford University");

    Annotation doc = new Annotation("Stanford University");
    doc.set(SentencesAnnotation.class, Collections.singletonList(sentence));
    doc.set(TokensAnnotation.class, tokens);

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);
    serializer.write(doc, out);

    byte[] bytes = out.toByteArray();
    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
    Pair<Annotation, InputStream> result = serializer.read(in);

    Annotation deserialized = result.first;
    assertNotNull(deserialized);
    assertEquals("Stanford University", deserialized.get(TextAnnotation.class));
    List<CoreMap> resultSentences = deserialized.get(SentencesAnnotation.class);
    assertEquals(1, resultSentences.size());

    CoreMap s = resultSentences.get(0);
    List<CoreLabel> resultTokens = s.get(TokensAnnotation.class);
    assertEquals(2, resultTokens.size());
    assertEquals("Stanford", resultTokens.get(0).word());
    assertEquals("University", resultTokens.get(1).word());
  }
@Test(expected = ProtobufAnnotationSerializer.LossySerializationException.class)
  public void testLossySerialization_throwsException() {
    CoreLabel coreLabel = new CoreLabel();
    coreLabel.setWord("Stanford");
    coreLabel.setTag("NNP");
    coreLabel.setIndex(1);
    
    coreLabel.set(DocIDAnnotation.class, "doc123");

    Set<Class<?>> skip = new HashSet<>();
    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
    serializer.toProto(coreLabel, skip);  
  }
@Test
  public void testReadUndelimited_handlesFallback() throws Exception {
    CoreLabel token = new CoreLabel();
    token.setWord("foo");
    token.set(SentenceIndexAnnotation.class, 0);
    token.set(IndexAnnotation.class, 1);

    CoreMap sentence = new ArrayCoreMap();
    sentence.set(TokenBeginAnnotation.class, 0);
    sentence.set(TokenEndAnnotation.class, 1);
    sentence.set(SentenceIndexAnnotation.class, 0);
    sentence.set(TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(TextAnnotation.class, "foo");

    Annotation annotation = new Annotation("foo");
    annotation.set(SentencesAnnotation.class, Collections.singletonList(sentence));
    annotation.set(TokensAnnotation.class, Collections.singletonList(token));

    ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(false);
    File tempFile = File.createTempFile("protobuf-ser-", ".bin");

    FileOutputStream fos = new FileOutputStream(tempFile);
    serializer.toProto(annotation).writeTo(fos);
    fos.close();

    Annotation deserialized = serializer.readUndelimited(tempFile);
    assertEquals("foo", deserialized.get(TextAnnotation.class));
    assertTrue(tempFile.delete());
  } 
}