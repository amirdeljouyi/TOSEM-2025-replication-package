public class DocumentImpl_wosr_1_GPTLLMTest { 

 @Test
  public void testDefaultConstructorSetsNonNullContent() {
    DocumentImpl doc = new DocumentImpl();
    assertNotNull("Content should not be null after default construction", doc.getContent());
    assertEquals("", doc.getContent().toString());
  }
@Test
  public void testSetAndGetStringContent() {
    DocumentImpl doc = new DocumentImpl();
    String exampleContent = "This is a test.";
    doc.setStringContent(exampleContent);
    assertEquals(exampleContent, doc.getStringContent());
  }
@Test
  public void testSetAndGetSourceUrl() throws MalformedURLException {
    DocumentImpl doc = new DocumentImpl();
    URL url = new URL("http://example.com/file.txt");
    doc.setSourceUrl(url);
    assertEquals(url, doc.getSourceUrl());
  }
@Test
  public void testGetSourceUrlOffsetsWhenNotSet() {
    DocumentImpl doc = new DocumentImpl();
    Long[] offsets = doc.getSourceUrlOffsets();
    assertNull(offsets[0]);
    assertNull(offsets[1]);
  }
@Test
  public void testSetAndGetSourceUrlOffsets() {
    DocumentImpl doc = new DocumentImpl();
    Long start = 10L;
    Long end = 20L;
    doc.setSourceUrlStartOffset(start);
    doc.setSourceUrlEndOffset(end);
    Long[] result = doc.getSourceUrlOffsets();
    assertEquals(start, result[0]);
    assertEquals(end, result[1]);
  }
@Test
  public void testSetAndGetPreserveOriginalContent() {
    DocumentImpl doc = new DocumentImpl();
    doc.setPreserveOriginalContent(Boolean.TRUE);
    assertEquals(Boolean.TRUE, doc.getPreserveOriginalContent());
  }
@Test
  public void testSetAndGetCollectRepositioningInfo() {
    DocumentImpl doc = new DocumentImpl();
    doc.setCollectRepositioningInfo(Boolean.TRUE);
    assertEquals(Boolean.TRUE, doc.getCollectRepositioningInfo());
  }
@Test
  public void testSetAndGetEncoding() {
    DocumentImpl doc = new DocumentImpl();
    String encoding = "UTF-16";
    doc.setEncoding(encoding);
    assertEquals(encoding, doc.getEncoding());
  }
@Test
  public void testGetAnnotationsReturnsNonNullDefaultSet() {
    DocumentImpl doc = new DocumentImpl();
    AnnotationSet defaultSet = doc.getAnnotations();
    assertNotNull(defaultSet);
    assertTrue(defaultSet instanceof AnnotationSetImpl);
  }
@Test
  public void testGetNamedAnnotationSetsInitiallyEmpty() {
    DocumentImpl doc = new DocumentImpl();
    Map<String, AnnotationSet> namedSets = doc.getNamedAnnotationSets();
    assertNotNull(namedSets);
    assertTrue(namedSets.isEmpty());
  }
@Test
  public void testAddAndRetrieveNamedAnnotationSet() {
    DocumentImpl doc = new DocumentImpl();
    String name = "CustomSet";
    AnnotationSet set = doc.getAnnotations(name);
    assertNotNull(set);
    assertEquals(set, doc.getNamedAnnotationSets().get(name));
  }
@Test
  public void testRemoveNamedAnnotationSet() {
    DocumentImpl doc = new DocumentImpl();
    String name = "TempSet";
    doc.getAnnotations(name);
    doc.removeAnnotationSet(name);
    assertFalse(doc.getNamedAnnotationSets().containsKey(name));
  }
@Test
  public void testEditPropagatesToContentAndAnnotations() throws InvalidOffsetException {
    DocumentImpl doc = new DocumentImpl();
    DocumentContentImpl oldContent = new DocumentContentImpl("abcdef");
    doc.setContent(oldContent);
    doc.getAnnotations(); 
    DocumentContentImpl replacement = new DocumentContentImpl("XY");

    doc.edit(1L, 3L, replacement);

    assertEquals("aXYdef", doc.getContent().toString());
  }
@Test(expected = InvalidOffsetException.class)
  public void testEditThrowsExceptionOnInvalidRange() throws InvalidOffsetException {
    DocumentImpl doc = new DocumentImpl();
    DocumentContentImpl content = new DocumentContentImpl("123456");
    doc.setContent(content);
    DocumentContentImpl replacement = new DocumentContentImpl("ZZ");

    doc.edit(5L, 2L, replacement); 
  }
@Test
  public void testIsValidOffsetReturnsFalseForNull() {
    DocumentImpl doc = new DocumentImpl();
    assertFalse(doc.isValidOffset(null));
  }
@Test
  public void testIsValidOffsetReturnsFalseForNegative() {
    DocumentImpl doc = new DocumentImpl();
    assertFalse(doc.isValidOffset(-1L));
  }
@Test
  public void testIsValidOffsetReturnsFalseForTooLarge() {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(new DocumentContentImpl("abc"));
    assertFalse(doc.isValidOffset(10L));
  }
@Test
  public void testIsValidOffsetReturnsTrueForValidOffset() {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(new DocumentContentImpl("xyz"));
    assertTrue(doc.isValidOffset(2L));
  }
@Test
  public void testIsValidOffsetRangeReturnsFalseIfEndBeforeStart() {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(new DocumentContentImpl("Hello World"));
    assertFalse(doc.isValidOffsetRange(5L, 2L));
  }
@Test
  public void testIsValidOffsetRangeReturnsTrueForValidRange() {
    DocumentImpl doc = new DocumentImpl();
    doc.setContent(new DocumentContentImpl("abcdefgh"));
    assertTrue(doc.isValidOffsetRange(1L, 5L));
  }
@Test
  public void testGetNextAnnotationIdIncrementsId() {
    DocumentImpl doc = new DocumentImpl();
    Integer first = doc.getNextAnnotationId();
    Integer second = doc.getNextAnnotationId();
    assertEquals(Integer.valueOf(first + 1), second);
  }
@Test
  public void testPeakAtNextAnnotationIdDoesNotIncrement() {
    DocumentImpl doc = new DocumentImpl();
    Integer before = doc.peakAtNextAnnotationId();
    Integer after = doc.peakAtNextAnnotationId();
    assertEquals(before, after);
  }
@Test
  public void testToStringProvidesMeaningfulOutput() {
    DocumentImpl doc = new DocumentImpl();
    String str = doc.toString();
    assertTrue(str.contains("DocumentImpl"));
    assertTrue(str.contains("content:"));
  }
@Test
  public void testCompareToBasedOnUrl() throws MalformedURLException {
    DocumentImpl doc1 = new DocumentImpl();
    DocumentImpl doc2 = new DocumentImpl();
    doc1.setSourceUrl(new URL("http://a.com/doc"));
    doc2.setSourceUrl(new URL("http://b.com/doc"));
    assertTrue(doc1.compareTo(doc2) < 0);
  }
@Test
  public void testGetAndRemoveListeners() {
    DocumentImpl doc = new DocumentImpl();
    DocumentListener listener = new DocumentListener() {
      public void annotationSetAdded(DocumentEvent e) {}
      public void annotationSetRemoved(DocumentEvent e) {}
      public void contentEdited(DocumentEvent e) {}
    };
    doc.addDocumentListener(listener);
    doc.removeDocumentListener(listener);
    doc.fireContentEdited(new DocumentEvent(doc, DocumentEvent.CONTENT_EDITED, 0L, 0L));
  }
@Test
  public void testSetAndGetMimeType() {
    DocumentImpl doc = new DocumentImpl();
    doc.setMimeType("text/plain");
    assertEquals("text/plain", doc.getMimeType());
  } 
}