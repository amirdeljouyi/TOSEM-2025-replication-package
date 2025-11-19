public class DocumentImpl_wosr_3_GPTLLMTest { 

 @Test
  public void testDefaultConstructorAndGetContent() {
    DocumentImpl document = new DocumentImpl();
    assertNotNull("Document content should be initialized", document.getContent());
    assertEquals("Initial content should be empty", "", document.getContent().toString());
  }
@Test
  public void testStringContentInitialization() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setStringContent("Hello world.");
    Document returned = document.init();
    assertEquals("Hello world.", returned.getContent().toString());
  }
@Test
  public void testSetAndGetPreserveOriginalContentFlag() {
    DocumentImpl doc = new DocumentImpl();
    doc.setPreserveOriginalContent(Boolean.TRUE);
    assertTrue(doc.getPreserveOriginalContent());
    doc.setPreserveOriginalContent(Boolean.FALSE);
    assertFalse(doc.getPreserveOriginalContent());
  }
@Test
  public void testSetAndGetCollectRepositioningInfoFlag() {
    DocumentImpl doc = new DocumentImpl();
    doc.setCollectRepositioningInfo(Boolean.TRUE);
    assertTrue(doc.getCollectRepositioningInfo());
  }
@Test
  public void testSetAndGetSourceUrl() throws MalformedURLException {
    DocumentImpl doc = new DocumentImpl();
    URL url = new URL("http://example.com/sample.txt");
    doc.setSourceUrl(url);
    assertEquals(url, doc.getSourceUrl());
  }
@Test
  public void testSetAndGetSourceUrlOffsets() {
    DocumentImpl doc = new DocumentImpl();
    doc.setSourceUrlStartOffset(10L);
    doc.setSourceUrlEndOffset(50L);
    Long[] offsets = doc.getSourceUrlOffsets();
    assertEquals(Long.valueOf(10), offsets[0]);
    assertEquals(Long.valueOf(50), offsets[1]);
  }
@Test
  public void testSetAndGetEncoding() {
    DocumentImpl doc = new DocumentImpl();
    doc.setEncoding("UTF-16");
    assertEquals("UTF-16", doc.getEncoding());
  }
@Test
  public void testGetEncodingFallbackToSystemDefault() {
    DocumentImpl doc = new DocumentImpl();
    assertNotNull("Encoding should fall back to default", doc.getEncoding());
  }
@Test
  public void testMarkupAwareSetting() {
    DocumentImpl doc = new DocumentImpl();
    doc.setMarkupAware(Boolean.TRUE);
    assertTrue(doc.getMarkupAware());
  }
@Test
  public void testGetAnnotationsCreatesDefaultSet() {
    DocumentImpl doc = new DocumentImpl();
    assertNotNull(doc.getAnnotations());
    assertTrue(doc.getAnnotations() instanceof AnnotationSetImpl);
  }
@Test
  public void testGetNamedAnnotationSetsReturnsMap() {
    DocumentImpl doc = new DocumentImpl();
    Map<String, ?> map = doc.getNamedAnnotationSets();
    assertNotNull(map);
    assertTrue(map.isEmpty());
  }
@Test
  public void testAddAndRemoveNamedAnnotationSet() {
    DocumentImpl doc = new DocumentImpl();
    doc.getAnnotations("TestSet");
    Set<String> names = doc.getAnnotationSetNames();
    assertTrue(names.contains("TestSet"));
    doc.removeAnnotationSet("TestSet");
    assertFalse(doc.getAnnotationSetNames().contains("TestSet"));
  }
@Test(expected = InvalidOffsetException.class)
  public void testEditContentWithInvalidOffsetsThrowsException() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setStringContent("ABCDE");
    doc.init();
    doc.edit(10L, 15L, new DocumentContentImpl("XYZ"));
  }
@Test
  public void testEditContentWithReplacement() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setStringContent("0123456789");
    doc.init();
    doc.edit(3L, 6L, new DocumentContentImpl("ABC"));
    assertEquals("012ABC6789", doc.getContent().toString());
  }
@Test
  public void testEditContentWithNullReplacementIsDeletion() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setStringContent("ABCDEFGHIJ");
    doc.init();
    doc.edit(2L, 5L, null);
    assertEquals("ABFGHIJ", doc.getContent().toString());
  }
@Test
  public void testIsValidOffsetTrueForWithinBounds() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setStringContent("123456");
    doc.init();
    assertTrue(doc.isValidOffset(3L));
  }
@Test
  public void testIsValidOffsetFalseForNullOrOutOfBounds() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setStringContent("XYZ");
    doc.init();
    assertFalse(doc.isValidOffset(null));
    assertFalse(doc.isValidOffset(-1L));
    assertFalse(doc.isValidOffset(100L));
  }
@Test
  public void testIsValidOffsetRangeValid() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setStringContent("abcdefghijkl");
    doc.init();
    assertTrue(doc.isValidOffsetRange(1L, 4L));
  }
@Test
  public void testIsValidOffsetRangeInvalid() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setStringContent("abcdefg");
    doc.init();
    assertFalse(doc.isValidOffsetRange(4L, 1L));
    assertFalse(doc.isValidOffsetRange(null, 1L));
    assertFalse(doc.isValidOffsetRange(1L, null));
    assertFalse(doc.isValidOffsetRange(1L, 1000L));
  }
@Test
  public void testNextAnnotationIdIncrements() {
    DocumentImpl doc = new DocumentImpl();
    int id1 = doc.getNextAnnotationId();
    int id2 = doc.getNextAnnotationId();
    assertEquals(id1 + 1, id2);
  }
@Test
  public void testPeakAtNextAnnotationIdDoesNotIncrement() {
    DocumentImpl doc = new DocumentImpl();
    int id = doc.peakAtNextAnnotationId();
    int expected = doc.getNextAnnotationId();
    assertEquals(id, expected);
  }
@Test
  public void testCompareToOrdering() throws MalformedURLException {
    DocumentImpl doc1 = new DocumentImpl();
    DocumentImpl doc2 = new DocumentImpl();
    doc1.setSourceUrl(new URL("http://example.com/a"));
    doc2.setSourceUrl(new URL("http://example.com/b"));
    assertTrue(doc1.compareTo(doc2) < 0);
    assertTrue(doc2.compareTo(doc1) > 0);
  }
@Test
  public void testToStringIncludesContent() {
    DocumentImpl doc = new DocumentImpl();
    String contentStr = doc.toString();
    assertNotNull(contentStr);
    assertTrue(contentStr.contains("DocumentImpl"));
  }
@Test
  public void testToXmlGeneratesGateXml() throws Exception {
    DocumentImpl doc = new DocumentImpl();
    doc.setStringContent("This is a simple test.");
    doc.init();
    String xmlOutput = doc.toXml();
    assertNotNull(xmlOutput);
    assertTrue(xmlOutput.contains("GateDocument"));
  } 
}