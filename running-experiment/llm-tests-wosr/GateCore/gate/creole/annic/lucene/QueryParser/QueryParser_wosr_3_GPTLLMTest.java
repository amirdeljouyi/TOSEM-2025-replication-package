public class QueryParser_wosr_3_GPTLLMTest { 

 @Test
  public void testValidSimpleTokenQuery() throws Exception {
    QueryParser parser = new QueryParser();
    Query[] queries = parser.parse("contents", "{Token}", "Token", null, null);
    assertEquals(1, queries.length);
    assertTrue(queries[0] instanceof BooleanQuery);
    assertNotNull(queries[0].toString());
  }
@Test
  public void testValidQuotedQuery() throws Exception {
    QueryParser parser = new QueryParser();
    Query[] queries = parser.parse("contents", "\"Hello\"", "Token", null, null);
    assertEquals(1, queries.length);
    assertTrue(queries[0] instanceof BooleanQuery);
    assertNotNull(queries[0].toString());
  }
@Test
  public void testValidAnnotationWithFeatureQuery() throws Exception {
    QueryParser parser = new QueryParser();
    Query[] queries = parser.parse("contents", "{Person.gender==\"male\"}", "Token", null, null);
    assertEquals(1, queries.length);
    assertTrue(queries[0] instanceof BooleanQuery);
    assertNotNull(queries[0].toString());
    assertTrue(parser.needValidation());
  }
@Test
  public void testMultipleClausesWithFeature() throws Exception {
    QueryParser parser = new QueryParser();
    Query[] queries = parser.parse("contents", "{Token} \"said\" {Person.name==\"John\"}", "Token", null, null);
    assertEquals(1, queries.length);
    Query q = queries[0];
    assertTrue(q instanceof BooleanQuery);
    assertTrue(parser.needValidation());
  }
@Test
  public void testCorpusIdInclusion() throws Exception {
    QueryParser parser = new QueryParser();
    String corpusId = "corpus123";
    Query[] queries = parser.parse("contents", "{Token}", "Token", corpusId, null);
    assertEquals(1, queries.length);
    assertTrue(queries[0] instanceof BooleanQuery);
    BooleanQuery bq = (BooleanQuery) queries[0];
    String queryString = bq.toString();
    assertTrue(queryString.contains("corpusId:corpus123"));
  }
@Test
  public void testAnnotationSetInclusion() throws Exception {
    QueryParser parser = new QueryParser();
    String annotationSet = "Set1";
    Query[] queries = parser.parse("contents", "\"hello\"", "Token", null, annotationSet);
    assertEquals(1, queries.length);
    assertTrue(queries[0] instanceof BooleanQuery);
    String queryString = queries[0].toString();
    assertTrue(queryString.contains("annotationSetId:Set1"));
  }
@Test
  public void testParseEmptyString() throws Exception {
    QueryParser parser = new QueryParser();
    try {
      parser.parse("contents", "", "Token", null, null);
      fail("Expected SearchException for empty query");
    } catch (SearchException ex) {
      assertNotNull(ex.getMessage());
    }
  }
@Test
  public void testInvalidSyntaxDoubleFeature() throws Exception {
    QueryParser parser = new QueryParser();
    try {
      parser.parse("contents", "{Token.string==value}", "Token", null, null);
      fail("Expected SearchException due to missing quotes");
    } catch (SearchException ex) {
      assertTrue(ex.getMessage().contains("equal operator"));
    }
  }
@Test
  public void testComplexPhraseQuery() throws Exception {
    QueryParser parser = new QueryParser();
    String query = "{Person} \"likes\" {Food.type==\"dessert\"}";
    Query[] queries = parser.parse("contents", query, "Token", null, null);
    assertEquals(1, queries.length);
    assertTrue(queries[0] instanceof BooleanQuery);
    assertTrue(parser.needValidation());
  }
@Test
  public void testGetQueryString() throws Exception {
    QueryParser parser = new QueryParser();
    parser.parse("contents", "{Token} \"loves\" \"coffee\"", "Token", null, null);
    String qString = parser.getQueryString(0);
    assertNotNull(qString);
    assertTrue(qString.contains("{Token}"));
  }
@Test
  public void testValidMultipleQueries() throws Exception {
    QueryParser parser = new QueryParser();
    parser.parse("contents", "{Token}|{Person}", "Token", null, null);
    assertEquals("{Token}", parser.getQueryString(0));
    assertEquals("{Person}", parser.getQueryString(1));
  }
@Test
  public void testFindTokensWithQuotedText() throws Exception {
    QueryParser parser = new QueryParser();
    List<String> tokens = parser.findTokens("\"hello world\"");
    assertEquals(1, tokens.size());
    assertEquals("\"hello world\"", tokens.get(0));
  }
@Test
  public void testIsValidQueryExpectingTrue() {
    boolean valid = QueryParser.isValidQuery("{Token}");
    assertTrue(valid);
  }
@Test
  public void testIsValidQueryExpectingFalse() {
    boolean valid = QueryParser.isValidQuery("{Token");
    assertFalse(valid);
  }

  @Test
  public void testCreateTermsForPlainText() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("\"Hello there\"");
    List<?> terms = result[0];
    assertEquals(2, terms.size());
    Term t0 = (Term) terms.get(0);
    assertEquals("Hello", t0.text());
    assertEquals("Token.string", t0.type());
  }

  @Test
  public void testCreateTermsForAnnotationOnly() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("{Person}");
    List<?> terms = result[0];
    assertEquals(1, terms.size());
    Term t = (Term) terms.get(0);
    assertEquals("Person", t.text());
    assertEquals("*", t.type());
  }

  @Test
  public void testCreateTermsWithFeatureEquality() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("{Person.name==\"John\"}");
    List<?> terms = result[0];
    assertEquals(1, terms.size());
    Term t = (Term) terms.get(0);
    assertEquals("John", t.text());
    assertEquals("Person.name", t.type());
  }

  @Test
  public void testInvalidFeatureSyntaxThrowsException() {
    QueryParser parser = new QueryParser();
    try {
      parser.createTerms("{Person.name}");
      fail("Expected SearchException for missing '=='");
    } catch (SearchException ex) {
      assertTrue(ex.getMessage().contains("missing operator"));
    }
  }

  @Test
  public void testNeedValidationTrueWhenMixedTypes() throws Exception {
    QueryParser parser = new QueryParser();
    parser.parse("contents", "{Token} {Person.name==\"Alice\"}", "Token", null, null);
    assertTrue(parser.needValidation());
  }

  @Test
  public void testNeedValidationFalseForOnlyTokens() throws Exception {
    QueryParser parser = new QueryParser();
    parser.parse("contents", "{Token} {Token}", "Token", null, null);
    assertFalse(parser.needValidation());
  }

  @Test
  public void testEscapedBracesParsedCorrectly() throws Exception {
    QueryParser parser = new QueryParser();
    List<String> tokens = parser.findTokens("\\{Token\\} {Person}");
    assertEquals(2, tokens.size());
    assertEquals("\\{Token\\}", tokens.get(0));
    assertEquals("{Person}", tokens.get(1));
  }
}
@Test
  public void testCreateTermsForPlainText() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("\"Hello there\"");
    List<?> terms = result[0];
    assertEquals(2, terms.size());
    Term t0 = (Term) terms.get(0);
    assertEquals("Hello", t0.text());
    assertEquals("Token.string", t0.type());
  }
@Test
  public void testCreateTermsForAnnotationOnly() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("{Person}");
    List<?> terms = result[0];
    assertEquals(1, terms.size());
    Term t = (Term) terms.get(0);
    assertEquals("Person", t.text());
    assertEquals("*", t.type());
  }
@Test
  public void testCreateTermsWithFeatureEquality() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("{Person.name==\"John\"}");
    List<?> terms = result[0];
    assertEquals(1, terms.size());
    Term t = (Term) terms.get(0);
    assertEquals("John", t.text());
    assertEquals("Person.name", t.type());
  }
@Test
  public void testInvalidFeatureSyntaxThrowsException() {
    QueryParser parser = new QueryParser();
    try {
      parser.createTerms("{Person.name}");
      fail("Expected SearchException for missing '=='");
    } catch (SearchException ex) {
      assertTrue(ex.getMessage().contains("missing operator"));
    }
  }
@Test
  public void testNeedValidationTrueWhenMixedTypes() throws Exception {
    QueryParser parser = new QueryParser();
    parser.parse("contents", "{Token} {Person.name==\"Alice\"}", "Token", null, null);
    assertTrue(parser.needValidation());
  }
@Test
  public void testNeedValidationFalseForOnlyTokens() throws Exception {
    QueryParser parser = new QueryParser();
    parser.parse("contents", "{Token} {Token}", "Token", null, null);
    assertFalse(parser.needValidation());
  }
@Test
  public void testEscapedBracesParsedCorrectly() throws Exception {
    QueryParser parser = new QueryParser();
    List<String> tokens = parser.findTokens("\\{Token\\} {Person}");
    assertEquals(2, tokens.size());
    assertEquals("\\{Token\\}", tokens.get(0));
    assertEquals("{Person}", tokens.get(1));
  } 
}