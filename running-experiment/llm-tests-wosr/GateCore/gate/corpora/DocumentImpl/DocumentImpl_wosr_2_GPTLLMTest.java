public class DocumentImpl_wosr_2_GPTLLMTest { 

 @Test
  public void testDefaultConstructor() {
    DocumentImpl doc = new DocumentImpl();
    Assert.assertNotNull(doc);
    Assert.assertNotNull(doc.getFeatures());
    Assert.assertTrue(doc.getFeatures().isEmpty());
    Assert.assertNotNull(doc.getContent());
    Assert.assertEquals("", doc.getContent().toString());
  }
@Test
  public void testSetAndGetStringContent() {
    DocumentImpl doc = new DocumentImpl();
    doc.setStringContent("Test content");
    Assert.assertEquals("Test content", doc.getStringContent());
  }
@Test
  public void testSetAndGetEncoding() {
    DocumentImpl doc = new DocumentImpl();
    doc.setEncoding("ISO-8859-1");
    Assert.assertEquals("ISO-8859-1", doc.getEncoding());
  }
@Test
  public void testSetAndGetPreserveOriginalContent() {
    DocumentImpl doc = new DocumentImpl();
    doc.setPreserveOriginalContent(true);
    Assert.assertTrue(doc.getPreserveOriginalContent());
  }
@Test
  public void testSetAndGetCollectRepositioningInfo() {
    DocumentImpl doc = new DocumentImpl();
    doc.setCollectRepositioningInfo(true);
    Assert.assertTrue(doc.getCollectRepositioningInfo());
  }
@Test
  public void testSetAndGetMimeType() {
    DocumentImpl doc = new DocumentImpl();
    doc.setMimeType("text/xml");
    Assert.assertEquals("text/xml", doc.getMimeType());
  }
@Test
  public void testSetAndGetSourceUrl() throws MalformedURLException {
    DocumentImpl doc = new DocumentImpl();
    URL url = new URL("http://example.com/test.txt");
    doc.setSourceUrl(url);
    Assert.assertEquals(url, doc.getSourceUrl());
  }
@Test
  public void testSetAndGetSourceUrlOffsets() {
    DocumentImpl doc = new DocumentImpl();
    doc.setSourceUrlStartOffset(10L);
    doc.setSourceUrlEndOffset(20L);
    Long[] offsets = doc.getSourceUrlOffsets();
    Assert.assertEquals(Long.valueOf(10), offsets[0]);
    Assert.assertEquals(Long.valueOf(20), offsets[1]);
  }
@Test
  public void testMarkupAwareFlag() {
    DocumentImpl doc = new DocumentImpl();
    doc.setMarkupAware(true);
    Assert.assertTrue(doc.getMarkupAware());
  }
@Test
  public void testInitializationWithStringContent() throws ResourceInstantiationException {
    DocumentImpl doc = new DocumentImpl();
    doc.setStringContent("Hello world");
    doc.init();
    Assert.assertNotNull(doc.getContent());
    Assert.assertTrue(doc.getContent().toString().contains("Hello"));
  }
@Test(expected = ResourceInstantiationException.class)
  public void testInitThrowsExceptionIfSourceUrlAndContentNull() throws ResourceInstantiationException {
    DocumentImpl doc = new DocumentImpl();
    doc.setStringContent(null);
    doc.setSourceUrl(null);
    doc.init();
  }
@Test
  public void testGetAnnotationsDefaultSet() {
    DocumentImpl doc = new DocumentImpl();
    AnnotationSet set = doc.getAnnotations();
    Assert.assertNotNull(set);
    Assert.assertEquals(0, set.size());
  }
@Test
  public void testNamedAnnotationSetCreation() {
    DocumentImpl doc = new DocumentImpl();
    AnnotationSet set1 = doc.getAnnotations("Set1");
    AnnotationSet set2 = doc.getAnnotations("Set1");
    Assert.assertNotNull(set1);
    Assert.assertSame(set1, set2);
  }
@Test
  public void testRemoveNamedAnnotationSet() {
    DocumentImpl doc = new DocumentImpl();
    doc.getAnnotations("tempSet");
    Map<String, AnnotationSet> named = doc.getNamedAnnotationSets();
    Assert.assertTrue(named.containsKey("tempSet"));
    doc.removeAnnotationSet("tempSet");
    Assert.assertFalse(named.containsKey("tempSet"));
  }
@Test
  public void testEditContentAndTriggerListeners() throws InvalidOffsetException {
    DocumentImpl doc = new DocumentImpl();
    doc.setStringContent("abcdefg");
    doc.init();

    final boolean[] eventFired = {false};

    doc.addDocumentListener(new DocumentListener() {
      @Override
      public void annotationSetAdded(DocumentEvent e) { }

      @Override
      public void annotationSetRemoved(DocumentEvent e) { }

      @Override
      public void contentEdited(DocumentEvent e) {
        eventFired[0] = true;
      }
    });

    DocumentContent newContent = new DocumentContentImpl("XY");
    doc.edit(1L, 3L, newContent); 

    Assert.assertTrue(eventFired[0]);
  }
@Test(expected = InvalidOffsetException.class)
  public void testEditWithInvalidOffsetThrows() throws InvalidOffsetException {
    DocumentImpl doc = new DocumentImpl();
    doc.setStringContent("abc");
    doc.init();
    DocumentContent replacement = new DocumentContentImpl("x");
    doc.edit(-1L, 10L, replacement);
  }
@Test
  public void testIsValidOffsetRangeValid() {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(new DocumentContentImpl("123456"));
    boolean valid = doc.isValidOffsetRange(1L, 3L);
    Assert.assertTrue(valid);
  }
@Test
  public void testIsValidOffsetRangeInvalid() {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(new DocumentContentImpl("123"));
    boolean invalid = doc.isValidOffsetRange(null, 2L);
    Assert.assertFalse(invalid);
    invalid = doc.isValidOffsetRange(1L, 10L);
    Assert.assertFalse(invalid);
  }
@Test
  public void testNextAnnotationIdManagement() {
    DocumentImpl doc = new DocumentImpl();
    int id1 = doc.getNextAnnotationId();
    int id2 = doc.getNextAnnotationId();
    Assert.assertEquals(id1 + 1, id2.intValue());
    doc.setNextAnnotationId(100);
    Assert.assertEquals(Integer.valueOf(100), doc.peakAtNextAnnotationId());
  }
@Test
  public void testGetAnnotationSetNames() {
    DocumentImpl doc = new DocumentImpl();
    doc.getAnnotations("setA");
    Set<String> names = doc.getAnnotationSetNames();
    Assert.assertTrue(names.contains("setA"));
  }
@Test
  public void testToStringDoesNotThrow() {
    DocumentImpl doc = new DocumentImpl();
    doc.setStringContent("data");
    doc.setEncoding("UTF-8");
    String toString = doc.toString();
    Assert.assertTrue(toString.contains("DocumentImpl"));
  }
@Test
  public void testCompareToWorks() throws MalformedURLException {
    DocumentImpl doc1 = new DocumentImpl();
    doc1.setSourceUrl(new URL("http://example.com/a.txt"));
    DocumentImpl doc2 = new DocumentImpl();
    doc2.setSourceUrl(new URL("http://example.com/b.txt"));
    Assert.assertTrue(doc1.compareTo(doc2) < 0);
  }
@Test
  public void testCleanupCleansNamedSets() {
    DocumentImpl doc = new DocumentImpl();
    doc.getAnnotations("temp");
    Assert.assertFalse(doc.getNamedAnnotationSets().isEmpty());
    doc.cleanup();
    Assert.assertTrue(doc.getNamedAnnotationSets().isEmpty());
  }
@Test
  public void testEmptyContentEditDoesNotFail() throws InvalidOffsetException {
    DocumentImpl doc = new DocumentImpl();
    DocumentContentImpl content = new DocumentContentImpl("xyz");
    doc.setContent(content);
    DocumentContent newContent = new DocumentContentImpl("");
    doc.edit(0L, 1L, newContent);
    Assert.assertTrue(doc.getContent().toString().startsWith("yz"));
  }
@Test
  public void testSetDefaultAnnotationsWorks() {
    DocumentImpl doc = new DocumentImpl();
    AnnotationSet original = doc.getAnnotations("dummy");
    doc.setDefaultAnnotations(original);
    Assert.assertEquals(original, doc.getAnnotations());
  }
@Test
  public void testInitDocumentCreatesContentFromString() throws ResourceInstantiationException {
    DocumentImpl doc = new DocumentImpl();
    doc.setStringContent("Test string");
    doc.init();
    Assert.assertTrue(doc.getContent().toString().contains("Test"));
  } 
}