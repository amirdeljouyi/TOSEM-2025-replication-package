public class DocumentStaxUtils_wosr_5_GPTLLMTest { 

 @Test
  public void testReadTextWithNodes_ValidNodes() throws Exception {
    String xml = "<TextWithNodes>"
               + "Hello <Node id=\"1\"/>world<Node id=\"2\"/>!"
               + "</TextWithNodes>";
    XMLInputFactory factory = XMLInputFactory.newInstance();
    InputStream input = new ByteArrayInputStream(xml.getBytes("UTF-8"));
    XMLStreamReader reader = factory.createXMLStreamReader(input);
    while(reader.hasNext() && (!reader.isStartElement() || !"TextWithNodes".equals(reader.getLocalName()))) {
      reader.next();
    }

    Map<Integer, Long> nodeOffsets = new HashMap<Integer, Long>();
    String text = DocumentStaxUtils.readTextWithNodes(reader, nodeOffsets);

    assertEquals("Hello world!", text);
    assertEquals(Long.valueOf(6), nodeOffsets.get(1));
    assertEquals(Long.valueOf(11), nodeOffsets.get(2));
  }
@Test(expected = XMLStreamException.class)
  public void testReadTextWithNodes_InvalidNodeId() throws Exception {
    String xml = "<TextWithNodes>Text<Node id=\"abc\"/></TextWithNodes>";
    XMLInputFactory factory = XMLInputFactory.newInstance();
    InputStream input = new ByteArrayInputStream(xml.getBytes("UTF-8"));
    XMLStreamReader reader = factory.createXMLStreamReader(input);
    while(reader.hasNext() && (!reader.isStartElement() || !"TextWithNodes".equals(reader.getLocalName()))) {
      reader.next();
    }

    DocumentStaxUtils.readTextWithNodes(reader, new HashMap<Integer, Long>());
  }
@Test(expected = XMLStreamException.class)
  public void testReadTextWithNodes_MissingNodeIdAttribute() throws Exception {
    String xml = "<TextWithNodes>Text<Node/></TextWithNodes>";
    XMLInputFactory factory = XMLInputFactory.newInstance();
    InputStream input = new ByteArrayInputStream(xml.getBytes("UTF-8"));
    XMLStreamReader reader = factory.createXMLStreamReader(input);
    while(reader.hasNext() && (!reader.isStartElement() || !"TextWithNodes".equals(reader.getLocalName()))) {
      reader.next();
    }

    DocumentStaxUtils.readTextWithNodes(reader, new HashMap<Integer, Long>());
  }
@Test
  public void testReplaceInvalidXmlCharacters_AllValid() {
    String valid = "This text is safe!";
    String replaced = DocumentStaxUtils.replaceXMLIllegalCharactersInString(valid);
    assertSame(valid, replaced);
  }
@Test
  public void testReplaceInvalidXmlCharacters_Controls() {
    String original = "Hello\u0003World";
    String replaced = DocumentStaxUtils.replaceXMLIllegalCharactersInString(original);
    assertEquals("Hello World", replaced);
  }
@Test
  public void testIsInvalidXmlChar_LegalCharacters() {
    CharSequence cs = "Legal";
    assertFalse(DocumentStaxUtils.isInvalidXmlChar(cs, 0));
  }
@Test
  public void testIsInvalidXmlChar_ControlCharacter() {
    CharSequence cs = "\u0001abc";
    assertTrue(DocumentStaxUtils.isInvalidXmlChar(cs, 0));
  }
@Test
  public void testAnnotationObjectSettersAndGetters() {
    AnnotationObject ann = new DocumentStaxUtils.AnnotationObject();
    ann.setElemName("Person");
    ann.setId(123);
    ann.setStart(10L);
    ann.setEnd(20L);
    FeatureMap fm = Factory.newFeatureMap();
    fm.put("key", "val");
    ann.setFM(fm);

    assertEquals("Person", ann.getElemName());
    assertEquals(Integer.valueOf(123), ann.getId());
    assertEquals(Long.valueOf(10), ann.getStart());
    assertEquals(Long.valueOf(20), ann.getEnd());
    assertEquals("val", ann.getFM().get("key"));
  }
@Test
  public void testArrayCharSequenceMethods() {
    char[] array = "Hello World".toCharArray();
    DocumentStaxUtils.ArrayCharSequence cs = new DocumentStaxUtils.ArrayCharSequence(array, 0, 5);
    assertEquals(5, cs.length());
    assertEquals('H', cs.charAt(0));
    assertEquals("Hello", cs.toString());
  }
@Test(expected = XMLStreamException.class)
  public void testReadAnnotationSet_InvalidStartNode() throws XMLStreamException {
    String xml = "<AnnotationSet>"
               + "<Annotation StartNode=\"abc\" EndNode=\"2\" Type=\"type\">"
               + "<Feature><Name className=\"java.lang.String\">key</Name>"
               + "<Value className=\"java.lang.String\">value</Value></Feature>"
               + "</Annotation>"
               + "</AnnotationSet>";
    XMLInputFactory factory = XMLInputFactory.newInstance();
    InputStream input = new ByteArrayInputStream(xml.getBytes("UTF-8"));
    XMLStreamReader reader = factory.createXMLStreamReader(input);
    while(reader.hasNext() && (!reader.isStartElement() || !"AnnotationSet".equals(reader.getLocalName()))) {
      reader.next();
    }

    DocumentStaxUtils.readAnnotationSet(reader, new DummyAnnotationSet(), new HashMap<Integer,Long>(), new TreeSet<Integer>(), null);
  }
@Test
  public void testContainsEnoughLTs_True() {
    String xmlFragment = "<<<<<<<important>>>>>";
    assertTrue(DocumentStaxUtils.containsEnoughLTs(xmlFragment));
  }
@Test
  public void testContainsEnoughLTs_False() {
    String xmlFragment = "He said <hello>.";
    assertFalse(DocumentStaxUtils.containsEnoughLTs(xmlFragment));
  } 
}