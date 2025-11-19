public class QueryParser_wosr_5_GPTLLMTest { 

 @Test
  public void testValidSimpleQuery() {
    QueryParser parser = new QueryParser();
    Query[] result = null;
    try {
      result = parser.parse("content", "{Token}", "Token", null, null);
    } catch(SearchException e) {
      fail("Exception should not be thrown for valid query");
    }
    assertNotNull(result);
    assertEquals(1, result.length);
    assertTrue(result[0] instanceof BooleanQuery);
    assertFalse(parser.needValidation());
    assertEquals("{Token}", parser.getQueryString(0));
  }
@Test
  public void testValidQuotedStringQuery() {
    QueryParser parser = new QueryParser();
    Query[] result = null;
    try {
      result = parser.parse("content", "\"hello\"", "Token", null, null);
    } catch(SearchException e) {
      fail("Exception should not be thrown for quoted string query");
    }
    assertNotNull(result);
    assertEquals(1, result.length);
    assertTrue(result[0] instanceof BooleanQuery);
    assertFalse(parser.needValidation());
    assertEquals("\"hello\"", parser.getQueryString(0));
  }
@Test
  public void testMultipleAnnotationsQuery() {
    QueryParser parser = new QueryParser();
    Query[] result = null;
    try {
      result = parser.parse("field", "{Person}{Token}", "Token", null, null);
    } catch(SearchException e) {
      fail("Valid multi-annotation query should not throw");
    }
    assertNotNull(result);
    assertEquals(1, result.length);
    assertTrue(result[0] instanceof BooleanQuery);
    assertTrue(parser.needValidation());
    assertEquals("{Person}{Token}", parser.getQueryString(0));
  }
@Test
  public void testAnnotationWithFeatureEqualsQuotedString() {
    QueryParser parser = new QueryParser();
    Query[] result = null;
    try {
      result = parser.parse("speech", "{Person.gender==\"male\"}", "Token", null, null);
    } catch(SearchException e) {
      fail("Valid annotation feature query should not throw");
    }
    assertNotNull(result);
    assertEquals(1, result.length);
    assertTrue(result[0] instanceof BooleanQuery);
    assertTrue(parser.needValidation());
    assertEquals("{Person.gender==\"male\"}", parser.getQueryString(0));
  }
@Test
  public void testMixedTextAndAnnotationQuery() {
    QueryParser parser = new QueryParser();
    Query[] result = null;
    String queryText = "{Person}\"said\"\"hello\"{Gender==\"female\"}";
    try {
      result = parser.parse("conversation", queryText, "Token", null, null);
    } catch(SearchException e) {
      fail("Mixed query with annotations and text should not throw exception");
    }
    assertNotNull(result);
    assertEquals(1, result.length);
    assertTrue(result[0] instanceof BooleanQuery);
    assertTrue(parser.needValidation());
    assertEquals(queryText, parser.getQueryString(0));
  }
@Test
  public void testInvalidUnbalancedOpeningBrace() {
    QueryParser parser = new QueryParser();
    try {
      parser.parse("bad", "{Token", "Token", null, null);
      fail("Expected SearchException for unbalanced brace");
    } catch(SearchException e) {
      assertEquals("unbalanced braces", e.getMessage());
    }
  }

  @Test
  public void testInvalidUnbalancedClosingBrace() {
    QueryParser parser = new QueryParser();
    try {
      parser.parse("bad", "Token}", "Token", null, null);
      fail("Expected SearchException for unbalanced brace");
    } catch(SearchException e) {
      assertEquals("unbalanced braces", e.getMessage());
    }
  }
@Test
  public void testInvalidUnbalancedClosingBrace() {
    QueryParser parser = new QueryParser();
    try {
      parser.parse("bad", "Token}", "Token", null, null);
      fail("Expected SearchException for unbalanced brace");
    }
@Test
  public void testIsValidQueryReturnsTrue() {
    boolean valid = QueryParser.isValidQuery("{Token}\"hello\"");
    assertTrue(valid);
  }
@Test
  public void testIsValidQueryReturnsFalse() {
    boolean valid = QueryParser.isValidQuery("{Token");
    assertFalse(valid);
  }

  @Test
  public void testGetQueryStringMultipleQueries() {
    QueryParser parser = new QueryParser();
    try {
      parser.parse("field", "{Token}|{Person}", "Token", null, null);
    } catch(SearchException e) {
      fail("Valid OR query should parse");
    }
    assertEquals("{Token}", parser.getQueryString(0));
    assertEquals("{Person}", parser.getQueryString(1));
  }

  @Test
  public void testQueryWithCorpusAndAnnotationSet() {
    QueryParser parser = new QueryParser();
    Query[] result = null;
    try {
      result = parser.parse("field", "{Token}", "Token", "corpus123", "setA");
    } catch(SearchException e) {
      fail("Query including corpus and annotationSet should be valid");
    }
    assertNotNull(result);
    assertEquals(1, result.length);
    assertTrue(result[0] instanceof BooleanQuery);
    assertFalse(parser.needValidation());
    assertEquals("{Token}", parser.getQueryString(0));
  }

  @Test
  public void testQueryWithOnlyCorpusId() {
    QueryParser parser = new QueryParser();
    Query[] result = null;
    try {
      result = parser.parse("field", "{Token}", "Token", "c1", null);
    } catch(SearchException e) {
      fail("Query with corpus ID only should be valid");
    }
    assertNotNull(result);
    assertEquals(1, result.length);
    assertTrue(result[0] instanceof BooleanQuery);
    assertFalse(parser.needValidation());
  }

  @Test
  public void testQueryWithOnlyAnnotationSet() {
    QueryParser parser = new QueryParser();
    Query[] result = null;
    try {
      result = parser.parse("field", "{Token}", "Token", null, "setB");
    } catch(SearchException e) {
      fail("Query with annotation set only should parse successfully");
    }
    assertNotNull(result);
    assertEquals(1, result.length);
    assertTrue(result[0] instanceof BooleanQuery);
    assertFalse(parser.needValidation());
  }

  @Test
  public void testCreateTermsWithPlainQuotedText() {
    QueryParser parser = new QueryParser();
    List<?>[] terms = null;
    try {
      terms = parser.createTerms("\"sample text\"");
    } catch(SearchException e) {
      fail("Quoted plain text should not throw exception");
    }
    @SuppressWarnings("unchecked")
    List<Term> termList = (List<Term>)terms[0];
    assertEquals(2, termList.size());
    assertEquals("sample", termList.get(0).text());
    assertEquals("text", termList.get(1).text());
  }

  @Test
  public void testAnnotationWithCommaSeparatedTypes() {
    QueryParser parser = new QueryParser();
    List<?>[] terms = null;
    try {
      terms = parser.createTerms("{Tag1,Tag2}");
    } catch(SearchException e) {
      fail("Comma-separated annotation types should be valid");
    }
    @SuppressWarnings("unchecked")
    List<Term> termList = (List<Term>)terms[0];
    assertEquals(2, termList.size());
    assertEquals("Tag1", termList.get(0).text());
    assertEquals("Tag2", termList.get(1).text());
  }

  @Test
  public void testAnnotationWithMultipleFeatures() {
    QueryParser parser = new QueryParser();
    List<?>[] terms = null;
    try {
      terms = parser.createTerms("{Person.gender==\"male\",Person.age==\"30\"}");
    } catch(SearchException e) {
      fail("Multiple features should not throw");
    }
    @SuppressWarnings("unchecked")
    List<Term> termList = (List<Term>)terms[0];
    assertEquals(2, termList.size());
    assertEquals("male", termList.get(0).text());
    assertEquals("30", termList.get(1).text());
  }

  @Test
  public void testInvalidAnnotationMissingEqualitySign() {
    QueryParser parser = new QueryParser();
    try {
      parser.createTerms("{Token.string\"value\"}");
      fail("Missing == should throw SearchException");
    } catch(SearchException e) {
      assertTrue(e.getMessage().contains("missing operator"));
    }
  }
}
@Test
  public void testGetQueryStringMultipleQueries() {
    QueryParser parser = new QueryParser();
    try {
      parser.parse("field", "{Token}|{Person}", "Token", null, null);
    } catch(SearchException e) {
      fail("Valid OR query should parse");
    }
    assertEquals("{Token}", parser.getQueryString(0));
    assertEquals("{Person}", parser.getQueryString(1));
  }
@Test
  public void testQueryWithCorpusAndAnnotationSet() {
    QueryParser parser = new QueryParser();
    Query[] result = null;
    try {
      result = parser.parse("field", "{Token}", "Token", "corpus123", "setA");
    } catch(SearchException e) {
      fail("Query including corpus and annotationSet should be valid");
    }
    assertNotNull(result);
    assertEquals(1, result.length);
    assertTrue(result[0] instanceof BooleanQuery);
    assertFalse(parser.needValidation());
    assertEquals("{Token}", parser.getQueryString(0));
  }
@Test
  public void testQueryWithOnlyCorpusId() {
    QueryParser parser = new QueryParser();
    Query[] result = null;
    try {
      result = parser.parse("field", "{Token}", "Token", "c1", null);
    } catch(SearchException e) {
      fail("Query with corpus ID only should be valid");
    }
    assertNotNull(result);
    assertEquals(1, result.length);
    assertTrue(result[0] instanceof BooleanQuery);
    assertFalse(parser.needValidation());
  }
@Test
  public void testQueryWithOnlyAnnotationSet() {
    QueryParser parser = new QueryParser();
    Query[] result = null;
    try {
      result = parser.parse("field", "{Token}", "Token", null, "setB");
    } catch(SearchException e) {
      fail("Query with annotation set only should parse successfully");
    }
    assertNotNull(result);
    assertEquals(1, result.length);
    assertTrue(result[0] instanceof BooleanQuery);
    assertFalse(parser.needValidation());
  }
@Test
  public void testCreateTermsWithPlainQuotedText() {
    QueryParser parser = new QueryParser();
    List<?>[] terms = null;
    try {
      terms = parser.createTerms("\"sample text\"");
    } catch(SearchException e) {
      fail("Quoted plain text should not throw exception");
    }
    @SuppressWarnings("unchecked")
    List<Term> termList = (List<Term>)terms[0];
    assertEquals(2, termList.size());
    assertEquals("sample", termList.get(0).text());
    assertEquals("text", termList.get(1).text());
  }
@Test
  public void testAnnotationWithCommaSeparatedTypes() {
    QueryParser parser = new QueryParser();
    List<?>[] terms = null;
    try {
      terms = parser.createTerms("{Tag1,Tag2}");
    } catch(SearchException e) {
      fail("Comma-separated annotation types should be valid");
    }
    @SuppressWarnings("unchecked")
    List<Term> termList = (List<Term>)terms[0];
    assertEquals(2, termList.size());
    assertEquals("Tag1", termList.get(0).text());
    assertEquals("Tag2", termList.get(1).text());
  }
@Test
  public void testAnnotationWithMultipleFeatures() {
    QueryParser parser = new QueryParser();
    List<?>[] terms = null;
    try {
      terms = parser.createTerms("{Person.gender==\"male\",Person.age==\"30\"}");
    } catch(SearchException e) {
      fail("Multiple features should not throw");
    }
    @SuppressWarnings("unchecked")
    List<Term> termList = (List<Term>)terms[0];
    assertEquals(2, termList.size());
    assertEquals("male", termList.get(0).text());
    assertEquals("30", termList.get(1).text());
  }
@Test
  public void testInvalidAnnotationMissingEqualitySign() {
    QueryParser parser = new QueryParser();
    try {
      parser.createTerms("{Token.string\"value\"}");
      fail("Missing == should throw SearchException");
    } catch(SearchException e) {
      assertTrue(e.getMessage().contains("missing operator"));
    }
  } 
}