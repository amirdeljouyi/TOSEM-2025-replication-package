public class DocumentStaxUtils_wosr_3_GPTLLMTest { 

 @Test
  public void testReadTextWithNodesBasic() throws XMLStreamException {
    String xml = "<TextWithNodes>" +
                 "Hello<Node id='1'/>world<Node id='2'/>!" +
                 "</TextWithNodes>";
    XMLInputFactory factory = XMLInputFactory.newInstance();
    XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));
    while(reader.hasNext() && reader.getEventType() != XMLStreamConstants.START_ELEMENT) {
      reader.next();
    }

    Map<Integer, Long> nodeMap = new java.util.HashMap<Integer, Long>();
    String result = DocumentStaxUtils.readTextWithNodes(reader, nodeMap);

    assertEquals("Helloworld!", result);
    assertEquals(Long.valueOf(5), nodeMap.get(1));
    assertEquals(Long.valueOf(10), nodeMap.get(2));
  }
@Test(expected = XMLStreamException.class)
  public void testReadTextWithNodesMissingNodeId() throws XMLStreamException {
    String xml = "<TextWithNodes>" +
                 "something<Node/>" +
                 "</TextWithNodes>";
    XMLInputFactory factory = XMLInputFactory.newInstance();
    XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));
    while(reader.hasNext() && reader.getEventType() != XMLStreamConstants.START_ELEMENT) {
      reader.next();
    }

    Map<Integer, Long> nodeMap = new java.util.HashMap<Integer, Long>();
    DocumentStaxUtils.readTextWithNodes(reader, nodeMap);
  }
@Test(expected = XMLStreamException.class)
  public void testReadAnnotationSetInvalidStartNodeIdFormat() throws XMLStreamException {
    String xml = "<AnnotationSet>" +
                 "<Annotation Type='Person' StartNode='abc' EndNode='2'>" +
                 "<Feature><Name className='java.lang.String'>name</Name>" +
                 "<Value className='java.lang.String'>Alice</Value></Feature>" +
                 "</Annotation></AnnotationSet>";
    XMLInputFactory factory = XMLInputFactory.newInstance();
    XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));
    while(reader.hasNext() && reader.getEventType() != XMLStreamConstants.START_ELEMENT) {
      reader.next();
    }
    AnnotationSet annSet = Factory.newDocument("").getAnnotations();
    Map<Integer, Long> nodeMap = new java.util.HashMap<Integer, Long>();
    nodeMap.put(2, 10L);
    Set<Integer> ids = new HashSet<Integer>();
    DocumentStaxUtils.readAnnotationSet(reader, annSet, nodeMap, ids, null);
  }
@Test(expected = XMLStreamException.class)
  public void testReadAnnotationSetDuplicateId() throws XMLStreamException {
    String xml = "<AnnotationSet>" +
                 "<Annotation Type='Person' StartNode='1' EndNode='2' Id='5'>" +
                 "<Feature><Name className='java.lang.String'>key</Name>" +
                 "<Value className='java.lang.String'>val</Value></Feature>" +
                 "</Annotation>" +
                 "<Annotation Type='Location' StartNode='2' EndNode='3' Id='5'>" +
                 "<Feature><Name className='java.lang.String'>key</Name>" +
                 "<Value className='java.lang.String'>place</Value></Feature>" +
                 "</Annotation>" +
                 "</AnnotationSet>";
    XMLInputFactory factory = XMLInputFactory.newInstance();
    XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));
    while(reader.hasNext() && reader.getEventType() != XMLStreamConstants.START_ELEMENT) {
      reader.next();
    }
    AnnotationSet set = Factory.newDocument("").getAnnotations();
    Map<Integer, Long> nodeMap = new java.util.HashMap<Integer, Long>();
    nodeMap.put(1, 0L);
    nodeMap.put(2, 5L);
    nodeMap.put(3, 10L);
    Set<Integer> ids = new java.util.HashSet<Integer>();
    DocumentStaxUtils.readAnnotationSet(reader, set, nodeMap, ids, Boolean.TRUE);
  }
@Test
  public void testReadAnnotationSetOldStyleNoId() throws XMLStreamException {
    String xml = "<AnnotationSet>" +
                 "<Annotation Type='Entity' StartNode='1' EndNode='3'>" +
                 "<Feature><Name className='java.lang.String'>type</Name>" +
                 "<Value className='java.lang.String'>Concept</Value></Feature>" +
                 "</Annotation>" +
                 "</AnnotationSet>";
    XMLInputFactory factory = XMLInputFactory.newInstance();
    XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));
    while(reader.hasNext() && reader.getEventType() != XMLStreamConstants.START_ELEMENT) {
      reader.next();
    }

    Document doc = Factory.newDocument("");
    AnnotationSet set = doc.getAnnotations();
    Map<Integer, Long> nodeMap = new java.util.HashMap<Integer, Long>();
    nodeMap.put(1, 0L);
    nodeMap.put(3, 10L);
    Set<Integer> ids = new java.util.HashSet<Integer>();

    Boolean style = DocumentStaxUtils.readAnnotationSet(reader, set, nodeMap, ids, null);

    assertNotNull(style);
    assertFalse(style); 
    assertEquals(1, set.size());
  }
@Test(expected = XMLStreamException.class)
  public void testReadAnnotationSetMixedStylesThrows() throws XMLStreamException {
    String xml = "<AnnotationSet>" +
                 "<Annotation Type='Tag' StartNode='1' EndNode='3'>" +
                 "<Feature><Name className='java.lang.String'>x</Name>" +
                 "<Value className='java.lang.String'>y</Value></Feature>" +
                 "</Annotation>" +
                 "<Annotation Type='Tag' StartNode='2' EndNode='4' Id='10'>" +
                 "<Feature><Name className='java.lang.String'>z</Name>" +
                 "<Value className='java.lang.String'>k</Value></Feature>" +
                 "</Annotation>" +
                 "</AnnotationSet>";
    XMLInputFactory factory = XMLInputFactory.newInstance();
    XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));
    while(reader.hasNext() && reader.getEventType() != XMLStreamConstants.START_ELEMENT) {
      reader.next();
    }

    Document doc = Factory.newDocument("");
    AnnotationSet set = doc.getAnnotations();
    Map<Integer, Long> nodeMap = new java.util.HashMap<Integer, Long>();
    nodeMap.put(1, 0L);
    nodeMap.put(2, 5L);
    nodeMap.put(3, 10L);
    nodeMap.put(4, 15L);
    Set<Integer> ids = new java.util.HashSet<Integer>();

    DocumentStaxUtils.readAnnotationSet(reader, set, nodeMap, ids, Boolean.FALSE);
  }
@Test
  public void testReplaceXMLIllegalControlCharacters() {
    char[] illegalText = { 'A', (char)0x0001, 'B', '\n', 'C' };
    DocumentStaxUtils.replaceXMLIllegalCharacters(illegalText);

    assertEquals('A', illegalText[0]);
    assertEquals(' ', illegalText[1]); 
    assertEquals('B', illegalText[2]);
    assertEquals('\n', illegalText[3]);
    assertEquals('C', illegalText[4]);
  }
@Test
  public void testReplaceXMLIllegalCharactersInStringValid() {
    String input = "Gate NLP is cool!";
    String result = DocumentStaxUtils.replaceXMLIllegalCharactersInString(input);
    assertSame(input, result);
  }
@Test
  public void testReplaceXMLIllegalCharactersInStringWithInvalids() {
    String input = "A\u0001B\u0002C";
    String result = DocumentStaxUtils.replaceXMLIllegalCharactersInString(input);
    assertEquals("A B C", result);
  }
@Test
  public void testIsInvalidXmlCharWithValidChar() {
    CharSequence seq = new StringBuilder("Z");
    boolean invalid = DocumentStaxUtils.isInvalidXmlChar(seq, 0);
    assertFalse(invalid);
  }
@Test
  public void testIsInvalidXmlCharWithUnpairedHighSurrogateAtEnd() {
    CharSequence seq = new StringBuilder().append((char)0xD800);
    boolean invalid = DocumentStaxUtils.isInvalidXmlChar(seq, 0);
    assertTrue(invalid);
  }
@Test
  public void testIsInvalidXmlCharWithLowSurrogateAtBeginning() {
    CharSequence seq = new StringBuilder().append((char)0xDC00).append('X');
    boolean invalid = DocumentStaxUtils.isInvalidXmlChar(seq, 0);
    assertTrue(invalid);
  }
@Test
  public void testIsInvalidXmlCharWithSurrogatePair() {
    char[] pair = new char[] { (char)0xD83D, (char)0xDC36 }; 
    CharSequence seq1 = new StringBuilder().append(pair[0]).append(pair[1]);
    boolean first = DocumentStaxUtils.isInvalidXmlChar(seq1, 0);
    boolean second = DocumentStaxUtils.isInvalidXmlChar(seq1, 1);
    assertFalse(first);
    assertFalse(second);
  }
@Test(expected = XMLStreamException.class)
  public void testReadFeatureNameOrValueFailsOnInvalidConstructor() throws XMLStreamException {
    String xml = "<Value className='java.awt.Point'>invalid</Value>";
    XMLInputFactory factory = XMLInputFactory.newInstance();
    XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));
    while(reader.hasNext() && reader.getEventType() != XMLStreamConstants.START_ELEMENT) {
      reader.next();
    }

    
    DocumentStaxUtils.readFeatureNameOrValue(reader);
  }
@Test
  public void testFeatureMapWithStringValues() throws XMLStreamException {
    String xml = "<Annotation>" +
                 "<Feature><Name className='java.lang.String'>name</Name>" +
                 "<Value className='java.lang.String'>value</Value></Feature>" +
                 "</Annotation>";
    XMLInputFactory factory = XMLInputFactory.newInstance();
    XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));
    while(reader.hasNext() && reader.getEventType() != XMLStreamConstants.START_ELEMENT) {
      reader.next();
    }

    FeatureMap map = DocumentStaxUtils.readFeatureMap(reader);
    assertEquals("value", map.get("name"));
  } 
}