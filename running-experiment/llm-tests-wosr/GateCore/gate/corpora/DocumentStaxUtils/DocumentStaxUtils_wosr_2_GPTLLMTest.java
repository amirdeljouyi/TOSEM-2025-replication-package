public class DocumentStaxUtils_wosr_2_GPTLLMTest { 

 @Test
  public void testReadAnnotationSetWithValidOffsetsAndIds() throws Exception {
    String xml = "<AnnotationSet>" +
                   "<Annotation Type=\"Person\" StartNode=\"0\" EndNode=\"5\" Id=\"1\">" +
                     "<Feature>" +
                        "<Name className=\"java.lang.String\">gender</Name>" +
                        "<Value className=\"java.lang.String\">male</Value>" +
                     "</Feature>" +
                   "</Annotation>" +
                 "</AnnotationSet>";
    XMLStreamReader xsr = XMLInputFactory.newInstance()
        .createXMLStreamReader(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));

    while(xsr.hasNext() && !(xsr.isStartElement() && "AnnotationSet".equals(xsr.getLocalName()))) {
      xsr.next();
    }

    AnnotationSet set = Factory.newDocument("").getAnnotations();
    set.clear();
    Map<Integer, Long> nodeMap = new HashMap<Integer, Long>();
    nodeMap.put(0, 0L);
    nodeMap.put(5, 5L);
    Set<Integer> ids = new HashSet<Integer>();

    Boolean result = DocumentStaxUtils.readAnnotationSet(xsr, set, nodeMap, ids, null);
    assertTrue(result);
    assertEquals(1, set.size());
    Annotation a = set.iterator().next();
    assertEquals("Person", a.getType());
    assertEquals(Long.valueOf(0), a.getStartNode().getOffset());
    assertEquals(Long.valueOf(5), a.getEndNode().getOffset());
    assertEquals("male", a.getFeatures().get("gender"));
  }
@Test(expected = XMLStreamException.class)
  public void testReadAnnotationSetMissingStartNode() throws Exception {
    String xml = "<AnnotationSet>" +
                   "<Annotation Type=\"Location\" EndNode=\"5\" Id=\"2\">" +
                     "<Feature>" +
                        "<Name className=\"java.lang.String\">region</Name>" +
                        "<Value className=\"java.lang.String\">north</Value>" +
                     "</Feature>" +
                   "</Annotation>" +
                 "</AnnotationSet>";

    XMLStreamReader xsr = XMLInputFactory.newInstance()
        .createXMLStreamReader(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));

    while(xsr.hasNext() && !(xsr.isStartElement() && "AnnotationSet".equals(xsr.getLocalName()))) {
      xsr.next();
    }

    AnnotationSet set = Factory.newDocument("").getAnnotations();
    set.clear();
    Map<Integer, Long> nodeMap = new HashMap<Integer, Long>();
    nodeMap.put(5, 5L);
    Set<Integer> ids = new HashSet<Integer>();

    DocumentStaxUtils.readAnnotationSet(xsr, set, nodeMap, ids, null);
  }
@Test(expected = XMLStreamException.class)
  public void testReadAnnotationSetWithNonIntegerId() throws Exception {
    String xml = "<AnnotationSet>" +
                   "<Annotation Type=\"Person\" StartNode=\"0\" EndNode=\"5\" Id=\"abc\">" +
                     "<Feature>" +
                        "<Name className=\"java.lang.String\">name</Name>" +
                        "<Value className=\"java.lang.String\">John</Value>" +
                     "</Feature>" +
                   "</Annotation>" +
                 "</AnnotationSet>";

    XMLStreamReader xsr = XMLInputFactory.newInstance()
        .createXMLStreamReader(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));

    while(xsr.hasNext() && !(xsr.isStartElement() && "AnnotationSet".equals(xsr.getLocalName()))) {
      xsr.next();
    }

    AnnotationSet set = Factory.newDocument("").getAnnotations();
    set.clear();
    Map<Integer, Long> nodeMap = new HashMap<Integer, Long>();
    nodeMap.put(0, 0L);
    nodeMap.put(5, 5L);
    Set<Integer> ids = new HashSet<Integer>();

    DocumentStaxUtils.readAnnotationSet(xsr, set, nodeMap, ids, null);
  }
@Test(expected = XMLStreamException.class)
  public void testReadAnnotationSetWithDuplicateIdInSet() throws Exception {
    String xml = "<AnnotationSet>" +
                   "<Annotation Type=\"Location\" StartNode=\"0\" EndNode=\"5\" Id=\"100\">" +
                     "<Feature>" +
                        "<Name className=\"java.lang.String\">region</Name>" +
                        "<Value className=\"java.lang.String\">east</Value>" +
                     "</Feature>" +
                   "</Annotation>" +
                   "<Annotation Type=\"Location\" StartNode=\"5\" EndNode=\"10\" Id=\"100\">" +
                     "<Feature>" +
                        "<Name className=\"java.lang.String\">region</Name>" +
                        "<Value className=\"java.lang.String\">west</Value>" +
                     "</Feature>" +
                   "</Annotation>" +
                 "</AnnotationSet>";

    XMLStreamReader xsr = XMLInputFactory.newInstance()
        .createXMLStreamReader(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));

    while(xsr.hasNext() && !(xsr.isStartElement() && "AnnotationSet".equals(xsr.getLocalName()))) {
      xsr.next();
    }

    AnnotationSet set = Factory.newDocument("").getAnnotations();
    set.clear();
    Map<Integer, Long> nodeMap = new HashMap<Integer, Long>();
    nodeMap.put(0, 0L);
    nodeMap.put(5, 5L);
    nodeMap.put(10, 10L);
    Set<Integer> ids = new HashSet<Integer>();

    DocumentStaxUtils.readAnnotationSet(xsr, set, nodeMap, ids, null);
  }
@Test
  public void testReadAnnotationSetWithoutIdsOldStyle() throws Exception {
    String xml = "<AnnotationSet>" +
                   "<Annotation Type=\"Token\" StartNode=\"0\" EndNode=\"3\">" +
                     "<Feature>" +
                        "<Name className=\"java.lang.String\">kind</Name>" +
                        "<Value className=\"java.lang.String\">word</Value>" +
                     "</Feature>" +
                   "</Annotation>" +
                 "</AnnotationSet>";

    XMLStreamReader xsr = XMLInputFactory.newInstance()
        .createXMLStreamReader(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));

    while(xsr.hasNext() && !(xsr.isStartElement() && "AnnotationSet".equals(xsr.getLocalName()))) {
      xsr.next();
    }

    AnnotationSet set = Factory.newDocument("").getAnnotations();
    set.clear();
    Map<Integer, Long> nodeMap = new HashMap<Integer, Long>();
    nodeMap.put(0, 0L);
    nodeMap.put(3, 3L);
    Set<Integer> ids = new HashSet<Integer>();

    Boolean result = DocumentStaxUtils.readAnnotationSet(xsr, set, nodeMap, ids, null);
    assertFalse(result);
    assertEquals(1, set.size());
    Annotation a = set.iterator().next();
    assertEquals("Token", a.getType());
    assertEquals("word", a.getFeatures().get("kind"));
    assertEquals(Long.valueOf(0L), a.getStartNode().getOffset());
    assertEquals(Long.valueOf(3L), a.getEndNode().getOffset());
  }
@Test(expected = XMLStreamException.class)
  public void testReadAnnotationSetMixingOldAndNewStyle() throws Exception {
    String xml =
        "<AnnotationSet>" +
            "<Annotation Type=\"Person\" StartNode=\"0\" EndNode=\"2\" Id=\"42\">" +
              "<Feature>" +
                "<Name className=\"java.lang.String\">name</Name>" +
                "<Value className=\"java.lang.String\">Alice</Value>" +
              "</Feature>" +
            "</Annotation>" +
            "<Annotation Type=\"Person\" StartNode=\"2\" EndNode=\"4\">" +
              "<Feature>" +
                "<Name className=\"java.lang.String\">name</Name>" +
                "<Value className=\"java.lang.String\">Bob</Value>" +
              "</Feature>" +
            "</Annotation>" +
        "</AnnotationSet>";

    XMLStreamReader xsr = XMLInputFactory.newInstance()
        .createXMLStreamReader(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));

    while(xsr.hasNext() && !(xsr.isStartElement() && "AnnotationSet".equals(xsr.getLocalName()))) {
      xsr.next();
    }

    AnnotationSet set = Factory.newDocument("").getAnnotations();
    set.clear();
    Map<Integer, Long> nodeMap = new HashMap<Integer, Long>();
    nodeMap.put(0, 0L);
    nodeMap.put(2, 2L);
    nodeMap.put(4, 4L);
    Set<Integer> ids = new HashSet<Integer>();

    DocumentStaxUtils.readAnnotationSet(xsr, set, nodeMap, ids, null);
  }
@Test(expected = XMLStreamException.class)
  public void testReadAnnotationSetInvalidStartNodeReference() throws Exception {
    String xml =
        "<AnnotationSet>" +
            "<Annotation Type=\"Entity\" StartNode=\"999\" EndNode=\"5\" Id=\"10\">" +
              "<Feature>" +
                "<Name className=\"java.lang.String\">type</Name>" +
                "<Value className=\"java.lang.String\">organization</Value>" +
              "</Feature>" +
            "</Annotation>" +
        "</AnnotationSet>";

    XMLStreamReader xsr = XMLInputFactory.newInstance()
        .createXMLStreamReader(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));

    while(xsr.hasNext() && !(xsr.isStartElement() && "AnnotationSet".equals(xsr.getLocalName()))) {
      xsr.next();
    }

    AnnotationSet set = Factory.newDocument("").getAnnotations();
    set.clear();
    Map<Integer, Long> nodeMap = new HashMap<Integer, Long>();
    nodeMap.put(5, 5L);
    Set<Integer> ids = new HashSet<Integer>();

    DocumentStaxUtils.readAnnotationSet(xsr, set, nodeMap, ids, null);
  } 
}