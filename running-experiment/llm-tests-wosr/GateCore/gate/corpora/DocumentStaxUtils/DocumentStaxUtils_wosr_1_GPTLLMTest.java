public class DocumentStaxUtils_wosr_1_GPTLLMTest { 

 @Test
  public void testReadGateXmlWithDefaultAnnotationSet() throws Exception {
    String xml =
            "<?xml version=\"1.0\"?>\n"
                    + "<GateDocument version=\"3\">\n"
                    + "  <GateDocumentFeatures>\n"
                    + "    <Feature>\n"
                    + "      <Name className=\"java.lang.String\">source</Name>\n"
                    + "      <Value className=\"java.lang.String\">unit/test</Value>\n"
                    + "    </Feature>\n"
                    + "  </GateDocumentFeatures>\n"
                    + "  <TextWithNodes>\n"
                    + "    Text before node.\n"
                    + "    <Node id=\"0\"/>\n"
                    + "    Node boundary.\n"
                    + "    <Node id=\"1\"/>\n"
                    + "    Text after node.\n"
                    + "  </TextWithNodes>\n"
                    + "  <AnnotationSet>\n"
                    + "    <Annotation Id=\"1\" Type=\"Token\" StartNode=\"0\" EndNode=\"1\">\n"
                    + "      <Feature>\n"
                    + "        <Name className=\"java.lang.String\">kind</Name>\n"
                    + "        <Value className=\"java.lang.String\">word</Value>\n"
                    + "      </Feature>\n"
                    + "    </Annotation>\n"
                    + "  </AnnotationSet>\n"
                    + "</GateDocument>";

    Document doc = Factory.newDocument("");
    XMLInputFactory factory = XMLInputFactory.newInstance();
    XMLStreamReader xsr = factory.createXMLStreamReader(new StringReader(xml));

    while (!xsr.isStartElement()) {
      xsr.next();
    }

    DocumentStaxUtils.readGateXmlDocument(xsr, doc);

    assertEquals("Text before node.    Node boundary.    Text after node.", doc.getContent().toString());

    FeatureMap features = doc.getFeatures();
    assertEquals("unit/test", features.get("source"));

    AnnotationSet defaultSet = doc.getAnnotations();
    Annotation token = defaultSet.get(1);
    assertNotNull(token);
    assertEquals("Token", token.getType());
    assertEquals("word", token.getFeatures().get("kind"));
  }
@Test
  public void testReadGateXmlWithNamedAnnotationSet() throws Exception {
    String xml =
            "<?xml version=\"1.0\"?>\n"
                    + "<GateDocument version=\"3\">\n"
                    + "  <GateDocumentFeatures/>\n"
                    + "  <TextWithNodes>\n"
                    + "    This is text. <Node id=\"0\"/>Mid<Node id=\"1\"/>End.\n"
                    + "  </TextWithNodes>\n"
                    + "  <AnnotationSet Name=\"MySet\">\n"
                    + "    <Annotation Id=\"42\" Type=\"Entity\" StartNode=\"0\" EndNode=\"1\">\n"
                    + "      <Feature>\n"
                    + "        <Name className=\"java.lang.String\">label</Name>\n"
                    + "        <Value className=\"java.lang.String\">TestLabel</Value>\n"
                    + "      </Feature>\n"
                    + "    </Annotation>\n"
                    + "  </AnnotationSet>\n"
                    + "</GateDocument>";

    Document doc = Factory.newDocument("");
    XMLInputFactory factory = XMLInputFactory.newInstance();
    XMLStreamReader xsr = factory.createXMLStreamReader(new StringReader(xml));

    while (!xsr.isStartElement()) {
      xsr.next();
    }

    DocumentStaxUtils.readGateXmlDocument(xsr, doc);

    assertTrue(doc.getNamedAnnotationSets().containsKey("MySet"));
    AnnotationSet namedSet = doc.getAnnotations("MySet");
    Set<Annotation> annotations = namedSet.get("Entity");
    assertEquals(1, annotations.size());

    Annotation a = annotations.iterator().next();
    assertEquals(Integer.valueOf(42), a.getId());
    assertEquals("Entity", a.getType());
    assertEquals("TestLabel", a.getFeatures().get("label"));
  }
@Test(expected = XMLStreamException.class)
  public void testInvalidStartNodeIdThrowsException() throws Exception {
    String xml =
            "<GateDocument version=\"3\">\n"
                    + "  <GateDocumentFeatures/>\n"
                    + "  <TextWithNodes>\n"
                    + "    <Node id=\"100\"/>\n"
                    + "  </TextWithNodes>\n"
                    + "  <AnnotationSet>\n"
                    + "    <Annotation Id=\"7\" Type=\"Err\" StartNode=\"200\" EndNode=\"100\">\n"
                    + "      <Feature><Name className=\"java.lang.String\">k</Name><Value className=\"java.lang.String\">v</Value></Feature>\n"
                    + "    </Annotation>\n"
                    + "  </AnnotationSet>\n"
                    + "</GateDocument>";

    Document doc = Factory.newDocument("");
    XMLStreamReader xsr = XMLInputFactory.newInstance()
            .createXMLStreamReader(new StringReader(xml));
    while (!xsr.isStartElement()) {
      xsr.next();
    }

    DocumentStaxUtils.readGateXmlDocument(xsr, doc);
  }
@Test(expected = XMLStreamException.class)
  public void testMissingStartGateDocumentThrowsException() throws Exception {
    String xml =
            "<NotGateDocument/>";

    Document doc = Factory.newDocument("");
    XMLStreamReader xsr = XMLInputFactory.newInstance()
            .createXMLStreamReader(new StringReader(xml));

    while (!xsr.isStartElement()) {
      xsr.next();
    }

    DocumentStaxUtils.readGateXmlDocument(xsr, doc);
  }
@Test
  public void testReadGateXmlDocumentWithStatusListener() throws Exception {
    String xml =
            "<GateDocument version=\"3\">\n"
                    + "  <GateDocumentFeatures/>\n"
                    + "  <TextWithNodes>\n"
                    + "    test<Node id=\"0\"/>a<Node id=\"1\"/>btext\n"
                    + "  </TextWithNodes>\n"
                    + "  <AnnotationSet>\n"
                    + "    <Annotation Id=\"3\" Type=\"Word\" StartNode=\"0\" EndNode=\"1\">\n"
                    + "      <Feature>\n"
                    + "        <Name className=\"java.lang.String\">tag</Name>\n"
                    + "        <Value className=\"java.lang.String\">NOUN</Value>\n"
                    + "      </Feature>\n"
                    + "    </Annotation>\n"
                    + "  </AnnotationSet>\n"
                    + "</GateDocument>";

    Document doc = Factory.newDocument("");
    XMLStreamReader xsr = XMLInputFactory.newInstance()
            .createXMLStreamReader(new StringReader(xml));
    while (!xsr.isStartElement()) {
      xsr.next();
    }

    StatusListenerStub stub = new StatusListenerStub();

    DocumentStaxUtils.readGateXmlDocument(xsr, doc, stub);
    assertEquals(1, doc.getAnnotations().size());
    Annotation a = doc.getAnnotations().get(3);
    assertEquals("Word", a.getType());
    assertEquals("NOUN", a.getFeatures().get("tag"));
  }
@Test
  public void testAnnotationSetWithoutIdAllowedInOldFormat() throws Exception {
    String xml =
            "<GateDocument version=\"3\">\n"
                    + "  <GateDocumentFeatures/>\n"
                    + "  <TextWithNodes>\n"
                    + "    prefix<Node id=\"10\"/>mid<Node id=\"20\"/>suffix\n"
                    + "  </TextWithNodes>\n"
                    + "  <AnnotationSet>\n"
                    + "    <Annotation Type=\"Mark\" StartNode=\"10\" EndNode=\"20\">\n"
                    + "      <Feature>\n"
                    + "        <Name className=\"java.lang.String\">cat</Name>\n"
                    + "        <Value className=\"java.lang.String\">TEST</Value>\n"
                    + "      </Feature>\n"
                    + "    </Annotation>\n"
                    + "  </AnnotationSet>\n"
                    + "</GateDocument>";

    Document doc = Factory.newDocument("");
    XMLStreamReader xsr = XMLInputFactory.newInstance()
            .createXMLStreamReader(new StringReader(xml));
    while (!xsr.isStartElement()) {
      xsr.next();
    }

    DocumentStaxUtils.readGateXmlDocument(xsr, doc);
    assertEquals("prefixmidsuffix", doc.getContent().toString().replaceAll("\\s+", ""));
    Annotation a = doc.getAnnotations().iterator().next();
    assertEquals("Mark", a.getType());
    assertEquals("TEST", a.getFeatures().get("cat"));
  }
@Test(expected = XMLStreamException.class)
  public void testMixedAnnotationIdStyleThrowsException() throws Exception {
    String xml =
            "<GateDocument version=\"3\">\n"
                    + "  <GateDocumentFeatures/>\n"
                    + "  <TextWithNodes>\n"
                    + "    <Node id=\"0\"/>\n"
                    + "    <Node id=\"1\"/>\n"
                    + "  </TextWithNodes>\n"
                    + "  <AnnotationSet>\n"
                    + "    <Annotation Id=\"1\" Type=\"Foo\" StartNode=\"0\" EndNode=\"1\">\n"
                    + "      <Feature><Name className=\"java.lang.String\">key</Name><Value className=\"java.lang.String\">val</Value></Feature>\n"
                    + "    </Annotation>\n"
                    + "    <Annotation Type=\"Bar\" StartNode=\"0\" EndNode=\"1\">\n"
                    + "      <Feature><Name className=\"java.lang.String\">key</Name><Value className=\"java.lang.String\">noid</Value></Feature>\n"
                    + "    </Annotation>\n"
                    + "  </AnnotationSet>\n"
                    + "</GateDocument>";

    Document doc = Factory.newDocument("");
    XMLStreamReader xsr = XMLInputFactory.newInstance()
            .createXMLStreamReader(new StringReader(xml));
    while (!xsr.isStartElement()) {
      xsr.next();
    }

    DocumentStaxUtils.readGateXmlDocument(xsr, doc);
  } 
}