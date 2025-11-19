public class DocumentImpl_wosr_4_GPTLLMTest { 

 @Test
  public void testSetAndGetPreserveOriginalContentTrue() {
    DocumentImpl document = new DocumentImpl();
    document.setPreserveOriginalContent(Boolean.TRUE);
    Boolean value = document.getPreserveOriginalContent();
    assertTrue("Should return TRUE", value);
  }
@Test
  public void testSetAndGetPreserveOriginalContentFalse() {
    DocumentImpl document = new DocumentImpl();
    document.setPreserveOriginalContent(Boolean.FALSE);
    Boolean value = document.getPreserveOriginalContent();
    assertFalse("Should return FALSE", value);
  }
@Test
  public void testSetAndGetCollectRepositioningInfoTrue() {
    DocumentImpl document = new DocumentImpl();
    document.setCollectRepositioningInfo(Boolean.TRUE);
    Boolean value = document.getCollectRepositioningInfo();
    assertTrue("Should return TRUE", value);
  }
@Test
  public void testSetAndGetCollectRepositioningInfoFalse() {
    DocumentImpl document = new DocumentImpl();
    document.setCollectRepositioningInfo(Boolean.FALSE);
    Boolean value = document.getCollectRepositioningInfo();
    assertFalse("Should return FALSE", value);
  }
@Test
  public void testSetAndGetEncoding() {
    DocumentImpl document = new DocumentImpl();
    document.setEncoding("ISO-8859-1");
    String encoding = document.getEncoding();
    assertEquals("ISO-8859-1", encoding);
  }
@Test
  public void testDefaultAnnotationsCreation() {
    DocumentImpl document = new DocumentImpl();
    AnnotationSet annotations = document.getAnnotations();
    assertNotNull("AnnotationSet should not be null", annotations);
    assertTrue(annotations instanceof AnnotationSetImpl);
  }
@Test
  public void testNamedAnnotationSetCreation() {
    DocumentImpl document = new DocumentImpl();
    AnnotationSet set = document.getAnnotations("testSet");
    assertNotNull("Named AnnotationSet should not be null", set);
    assertTrue(set instanceof AnnotationSetImpl);
    assertEquals(document.getAnnotations("testSet"), set);
  }
@Test
  public void testRemoveAnnotationSet() {
    DocumentImpl document = new DocumentImpl();
    AnnotationSet set = document.getAnnotations("tempSet");
    assertNotNull(set);
    document.removeAnnotationSet("tempSet");
    assertNull(document.getNamedAnnotationSets().get("tempSet"));
  }
@Test
  public void testSetAndGetMimeType() {
    DocumentImpl document = new DocumentImpl();
    document.setMimeType("text/xml");
    String mimeType = document.getMimeType();
    assertEquals("text/xml", mimeType);
  }
@Test
  public void testSetAndGetSourceUrl() throws MalformedURLException {
    DocumentImpl document = new DocumentImpl();
    URL testUrl = new URL("http://example.com/test.txt");
    document.setSourceUrl(testUrl);
    URL retrieved = document.getSourceUrl();
    assertEquals(testUrl, retrieved);
  }
@Test
  public void testSetAndGetSourceUrlOffsets() {
    DocumentImpl document = new DocumentImpl();
    document.setSourceUrlStartOffset(5L);
    document.setSourceUrlEndOffset(10L);
    Long[] offsets = document.getSourceUrlOffsets();
    assertEquals((Long)5L, offsets[0]);
    assertEquals((Long)10L, offsets[1]);
  }
@Test
  public void testIsValidOffsetTrue() {
    DocumentImpl document = new DocumentImpl();
    String sampleText = "Hello World";
    document.setContent(new DocumentContentImpl(sampleText));
    boolean valid = document.isValidOffset(5L);
    assertTrue("Offset within range should be valid", valid);
  }
@Test
  public void testIsValidOffsetFalseNegative() {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("data"));
    boolean valid = document.isValidOffset(-1L);
    assertFalse("Negative offset should be invalid", valid);
  }
@Test
  public void testIsValidOffsetFalseTooLarge() {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("12345"));
    boolean valid = document.isValidOffset(10L);
    assertFalse("Too large offset should be invalid", valid);
  }
@Test
  public void testIsValidOffsetRangeValid() {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("ABCDE"));
    boolean valid = document.isValidOffsetRange(0L, 5L);
    assertTrue("Valid offset range should be valid", valid);
  }
@Test
  public void testIsValidOffsetRangeReversed() {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("xyz"));
    boolean valid = document.isValidOffsetRange(2L, 1L);
    assertFalse("Start > End range should be invalid", valid);
  }
@Test
  public void testEditDeletesText() throws InvalidOffsetException {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("Hello World"));
    document.edit(6L, 11L, null);
    String newContent = document.getContent().toString();
    assertEquals("Hello ", newContent);
  }
@Test
  public void testEditReplacesText() throws InvalidOffsetException {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("Hello World"));
    DocumentContent newContent = new DocumentContentImpl("GATE");
    document.edit(6L, 11L, newContent);
    String result = document.getContent().toString();
    assertEquals("Hello GATE", result);
  }
@Test(expected = InvalidOffsetException.class)
  public void testEditInvalidStartOffsetThrows() throws InvalidOffsetException {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("123456"));
    document.edit(-2L, 3L, new DocumentContentImpl("x"));
  }
@Test(expected = InvalidOffsetException.class)
  public void testEditInvalidEndOffsetThrows() throws InvalidOffsetException {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("abcdef"));
    document.edit(2L, 20L, new DocumentContentImpl("y"));
  }
@Test
  public void testGetFeaturesWhenNullCreatesNewMap() {
    DocumentImpl document = new DocumentImpl();
    assertNotNull("Features map should not be null", document.getFeatures());
  }
@Test
  public void testSetAndGetStringContent() throws ResourceInstantiationException {
    DocumentImpl document = new DocumentImpl();
    document.setStringContent("This is a string.");
    Resource r = document.init();
    assertNotNull(r);
    String result = document.getContent().toString();
    assertEquals("This is a string.", result);
  }
@Test
  public void testGetContentDefaultNotNull() {
    DocumentImpl document = new DocumentImpl();
    assertNotNull("Content should be initialized by default constructor", document.getContent());
  } 
}