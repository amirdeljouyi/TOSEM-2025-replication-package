public class DocumentStaxUtils_wosr_4_GPTLLMTest { 

 @Test
  public void testReadGateXmlDocumentBasicTextContent() throws Exception {
    String xml = "<?xml version=\"1.0\"?>\n" +
            "<GateDocument version=\"3\">\n" +
            "<GateDocumentFeatures>\n" +
            "</GateDocumentFeatures>\n" +
            "<TextWithNodes>\n" +
            "Hello<Node id=\"0\"/>World<Node id=\"1\"/>!</TextWithNodes>\n" +
            "</GateDocument>";

    ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
    XMLInputFactory factory = XMLInputFactory.newInstance();
    XMLStreamReader xsr = factory.createXMLStreamReader(input);
    while(xsr.hasNext()) {
      xsr.next();
      if(xsr.isStartElement() && "GateDocument".equals(xsr.getLocalName())) {
        break;
      }
    }

    Document doc = Factory.newDocument("");
    DocumentStaxUtils.readGateXmlDocument(xsr, doc);

    DocumentContent content = doc.getContent();
    assertNotNull(content);
    String expectedText = "HelloWorld!";
    assertEquals(expectedText, content.toString());
  }
@Test
  public void testReadGateXmlDocumentWithDefaultAnnotationSet() throws Exception {
    String xml = "<?xml version=\"1.0\"?>\n" +
            "<GateDocument version=\"3\">\n" +
            "<GateDocumentFeatures></GateDocumentFeatures>\n" +
            "<TextWithNodes>Hello<Node id=\"0\"/>World<Node id=\"1\"/>!</TextWithNodes>\n" +
            "<AnnotationSet>\n" +
            "  <Annotation Id=\"1\" Type=\"Token\" StartNode=\"0\" EndNode=\"1\">\n" +
            "    <Feature>\n" +
            "      <Name className=\"java.lang.String\">kind</Name>\n" +
            "      <Value className=\"java.lang.String\">word</Value>\n" +
            "    </Feature>\n" +
            "  </Annotation>\n" +
            "</AnnotationSet>\n" +
            "</GateDocument>";

    ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
    XMLInputFactory factory = XMLInputFactory.newInstance();
    XMLStreamReader xsr = factory.createXMLStreamReader(input);

    while(xsr.hasNext()) {
      xsr.next();
      if(xsr.isStartElement() && "GateDocument".equals(xsr.getLocalName())) {
        break;
      }
    }

    Document doc = Factory.newDocument("");
    DocumentStaxUtils.readGateXmlDocument(xsr, doc);
    assertEquals("HelloWorld!", doc.getContent().toString());

    AnnotationSet defaultSet = doc.getAnnotations();
    assertEquals(1, defaultSet.size());
    Annotation a = defaultSet.iterator().next();
    assertEquals("Token", a.getType());
    assertEquals(0L, a.getStartNode().getOffset().longValue());
    assertEquals(1L, a.getEndNode().getOffset().longValue());
    FeatureMap features = a.getFeatures();
    assertEquals(1, features.size());
    assertEquals("word", features.get("kind"));
  }
@Test(expected = XMLStreamException.class)
  public void testReadGateXmlDocumentWithInvalidStartNode() throws Exception {
    String xml = "<?xml version=\"1.0\"?>\n" +
            "<GateDocument version=\"3\">\n" +
            "<GateDocumentFeatures></GateDocumentFeatures>\n" +
            "<TextWithNodes>Hi<Node id=\"0\"/>There</TextWithNodes>\n" +
            "<AnnotationSet>\n" +
            "<Annotation Id=\"1\" Type=\"Token\" StartNode=\"X\" EndNode=\"0\">\n" +
            "<Feature><Name className=\"java.lang.String\">dummy</Name>\n" +
            "<Value className=\"java.lang.String\">true</Value></Feature>\n" +
            "</Annotation>\n" +
            "</AnnotationSet>\n" +
            "</GateDocument>";

    ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
    XMLInputFactory factory = XMLInputFactory.newInstance();
    XMLStreamReader xsr = factory.createXMLStreamReader(input);
    
    while(xsr.hasNext()) {
      xsr.next();
      if(xsr.isStartElement() && "GateDocument".equals(xsr.getLocalName())) {
        break;
      }
    }

    Document doc = Factory.newDocument("");
    DocumentStaxUtils.readGateXmlDocument(xsr, doc);
  }
@Test
  public void testReadGateXmlDocumentWithStatusListenerNotifications() throws Exception {
    String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<GateDocument version=\"3\">\n" +
            "<GateDocumentFeatures></GateDocumentFeatures>\n" +
            "<TextWithNodes>Hello<Node id=\"0\"/>NLP</TextWithNodes>\n" +
            "<AnnotationSet>\n" +
            "<Annotation Id=\"5\" Type=\"Entity\" StartNode=\"0\" EndNode=\"0\">\n" +
            "<Feature>\n" +
            "<Name className=\"java.lang.String\">type</Name>\n" +
            "<Value className=\"java.lang.String\">Person</Value>\n" +
            "</Feature>\n" +
            "</Annotation>\n" +
            "</AnnotationSet>\n" +
            "</GateDocument>";

    ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
    XMLInputFactory factory = XMLInputFactory.newInstance();
    XMLStreamReader xsr = factory.createXMLStreamReader(input);

    while(xsr.hasNext()) {
      xsr.next();
      if(xsr.isStartElement() && "GateDocument".equals(xsr.getLocalName())) {
        break;
      }
    }

    final StringBuilder sb = new StringBuilder();
    StatusListener listener = status -> sb.append(status).append("/");
    Document doc = Factory.newDocument("");
    DocumentStaxUtils.readGateXmlDocument(xsr, doc, listener);

    assertTrue(sb.toString().contains("default annotation set"));
    assertEquals("HelloNLP", doc.getContent().toString());
    assertEquals(1, doc.getAnnotations().size());
  }
@Test
  public void testAnnotationIdsAreUnique() throws Exception {
    String xml = "<?xml version=\"1.0\"?>\n" +
            "<GateDocument version=\"3\">\n" +
            "<GateDocumentFeatures></GateDocumentFeatures>\n" +
            "<TextWithNodes>Hi<Node id=\"0\"/>Bye<Node id=\"1\"/></TextWithNodes>\n" +
            "<AnnotationSet>\n" +
            "  <Annotation Id=\"10\" Type=\"A\" StartNode=\"0\" EndNode=\"1\"><Feature><Name className=\"java.lang.String\">tag</Name><Value className=\"java.lang.String\">x</Value></Feature></Annotation>\n" +
            "  <Annotation Id=\"10\" Type=\"B\" StartNode=\"0\" EndNode=\"1\"><Feature><Name className=\"java.lang.String\">tag</Name><Value className=\"java.lang.String\">y</Value></Feature></Annotation>\n" +
            "</AnnotationSet>\n" +
            "</GateDocument>";

    ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
    XMLInputFactory factory = XMLInputFactory.newInstance();
    XMLStreamReader xsr = factory.createXMLStreamReader(input);

    while(xsr.hasNext()) {
      xsr.next();
      if(xsr.isStartElement() && "GateDocument".equals(xsr.getLocalName())) {
        break;
      }
    }

    Document doc = Factory.newDocument("");

    try {
      DocumentStaxUtils.readGateXmlDocument(xsr, doc);
      fail("Expected XMLStreamException for duplicate Ids");
    } catch(XMLStreamException expected) {
      assertTrue(expected.getMessage().contains("duplicate ID"));
    }
  } 
}