public class QueryParser_wosr_1_GPTLLMTest { 

 @Test
  public void testIsValidQuerySimpleToken() {
    boolean isValid = QueryParser.isValidQuery("{Token}");
    assertTrue(isValid);
  }
@Test
  public void testIsValidQueryMultipleTokens() {
    boolean isValid = QueryParser.isValidQuery("{Token}{Token}");
    assertTrue(isValid);
  }
@Test
  public void testIsValidQueryQuotedString() {
    boolean isValid = QueryParser.isValidQuery("\"hello\"");
    assertTrue(isValid);
  }
@Test
  public void testIsValidQueryAnnotationTypeFeature() {
    boolean isValid = QueryParser.isValidQuery("{Person.gender==\"male\"}");
    assertTrue(isValid);
  }
@Test
  public void testIsValidQueryInvalidBrace() {
    boolean isValid = QueryParser.isValidQuery("{Person");
    assertFalse(isValid);
  }

  @Test
  public void testParseSingleTokenPhrase() throws Exception {
    QueryParser parser = new QueryParser();
    Query[] queries = parser.parse("content", "{Token}", "Token", null, null);
    assertEquals(1, queries.length);
    assertTrue(queries[0] instanceof BooleanQuery);
  }

  @Test
  public void testParseQuotedPhrase() throws Exception {
    QueryParser parser = new QueryParser();
    Query[] queries = parser.parse("content", "\"hello world\"", "Token", null, null);
    assertEquals(1, queries.length);
    assertTrue(queries[0] instanceof BooleanQuery);
  }

  @Test
  public void testParseMixedTokenPhrase() throws Exception {
    QueryParser parser = new QueryParser();
    Query[] queries = parser.parse("content", "{Person.gender==\"male\"}{Token}", "Token", null, null);
    assertEquals(1, queries.length);
    assertTrue(queries[0] instanceof BooleanQuery);
    assertTrue(parser.needValidation());
  }

  @Test
  public void testParseWithCorpusAndSet() throws Exception {
    QueryParser parser = new QueryParser();
    Query[] queries = parser.parse("content", "{Token}", "Token", "corpus123", "defaultSet");
    assertEquals(1, queries.length);
    assertTrue(queries[0] instanceof BooleanQuery);
  }

  @Test
  public void testParseAnnotationTypeWithStringValue() throws Exception {
    QueryParser parser = new QueryParser();
    Query[] queries = parser.parse("content", "{Person==\"John\"}", "Token", null, null);
    assertEquals(1, queries.length);
    Query q = queries[0];
    assertTrue(q instanceof BooleanQuery);
  }

  @Test
  public void testParseAnnotationFeatureEqualsString() throws Exception {
    QueryParser parser = new QueryParser();
    Query[] queries = parser.parse("text", "{Person.gender==\"male\"}", "Token", null, null);
    assertEquals(1, queries.length);
    BooleanQuery bq = (BooleanQuery)queries[0];
    assertNotNull(bq);
  }

  @Test
  public void testFindTokensSingleAnnotation() throws Exception {
    QueryParser qp = new QueryParser();
    List<String> tokens = qp.findTokens("{Token}");
    assertEquals(1, tokens.size());
    assertEquals("{Token}", tokens.get(0));
  }

  @Test
  public void testFindTokensMultipleMix() throws Exception {
    QueryParser qp = new QueryParser();
    List<String> tokens = qp.findTokens("{Person}\"said\"");
    assertEquals(2, tokens.size());
    assertEquals("{Person}", tokens.get(0));
    assertEquals("\"said\"", tokens.get(1));
  }

  @Test
  public void testFindTokensWithEscapedBraces() throws Exception {
    QueryParser qp = new QueryParser();
    List<String> tokens = qp.findTokens("{Person} \\{escaped\\}");
    assertEquals(2, tokens.size());
  }

  @Test(expected = SearchException.class)
  public void testFindTokensUnbalancedBraceOpen() throws Exception {
    QueryParser qp = new QueryParser();
    qp.findTokens("{Token");
  }

  @Test(expected = SearchException.class)
  public void testFindTokensUnbalancedBraceClose() throws Exception {
    QueryParser qp = new QueryParser();
    qp.findTokens("Token}");
  }

  @Test
  public void testCreateTermsQuotedText() throws Exception {
    QueryParser qp = new QueryParser();
    List<?>[] result = qp.createTerms("\"hello world\"");
    List<?> terms = result[0];
    assertEquals(2, terms.size());
    assertTrue(terms.get(0) instanceof Term);
  }

  @Test
  public void testCreateTermsAnnotationTypeOnly() throws Exception {
    QueryParser qp = new QueryParser();
    List<?>[] result = qp.createTerms("{Token}");
    List<?> terms = result[0];
    assertEquals(1, terms.size());
    Term term = (Term)terms.get(0);
    assertEquals("Token", term.text());
    assertEquals("*", term.type());
  }

  @Test
  public void testCreateTermsWithFeatureEqualsQuoted() throws Exception {
    QueryParser qp = new QueryParser();
    List<?>[] result = qp.createTerms("{Person.gender==\"male\"}");
    List<?> terms = result[0];
    assertEquals(1, terms.size());
    Term term = (Term)terms.get(0);
    assertEquals("male", term.text());
    assertEquals("Person.gender", term.type());
  }

  @Test
  public void testCreateTermsWithMultipleAnnotations() throws Exception {
    QueryParser qp = new QueryParser();
    List<?>[] result = qp.createTerms("{Token, Token==\"the\"}");
    List<?> terms = result[0];
    assertEquals(2, terms.size());
  }

  @Test(expected = SearchException.class)
  public void testCreateTermsMissingEqualOperator() throws Exception {
    QueryParser qp = new QueryParser();
    qp.createTerms("{Person.feature\"value\"}");
  }

  @Test(expected = SearchException.class)
  public void testCreateTermsInvalidDotWithoutEquals() throws Exception {
    QueryParser qp = new QueryParser();
    qp.createTerms("{Person.feature}");
  }

  @Test
  public void testNeedValidationTrueAfterParsing() throws Exception {
    QueryParser parser = new QueryParser();
    parser.parse("text", "{Person.gender==\"male\"}{Token}", "Token", null, null);
    assertTrue(parser.needValidation());
  }

  @Test
  public void testNeedValidationFalseForTokensOnly() throws Exception {
    QueryParser parser = new QueryParser();
    parser.parse("text", "{Token}{Token}", "Token", null, null);
    assertFalse(parser.needValidation());
  }

  @Test
  public void testGetQueryStringAfterParse() throws Exception {
    QueryParser parser = new QueryParser();
    parser.parse("text", "{Token}", "Token", null, null);
    String queryString = parser.getQueryString(0);
    assertNotNull(queryString);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testGetQueryStringInvalidIndex() throws Exception {
    QueryParser parser = new QueryParser();
    parser.parse("text", "{Token}", "Token", null, null);
    parser.getQueryString(10);
  }

  @Test
  public void testParseMultipleQuerySegments() throws Exception {
    QueryParser parser = new QueryParser();
    Query[] queries = parser.parse("text", "{Token}|{Token}", "Token", null, null);
    assertEquals(2, queries.length);
  }
}
@Test
  public void testParseSingleTokenPhrase() throws Exception {
    QueryParser parser = new QueryParser();
    Query[] queries = parser.parse("content", "{Token}", "Token", null, null);
    assertEquals(1, queries.length);
    assertTrue(queries[0] instanceof BooleanQuery);
  }
@Test
  public void testParseQuotedPhrase() throws Exception {
    QueryParser parser = new QueryParser();
    Query[] queries = parser.parse("content", "\"hello world\"", "Token", null, null);
    assertEquals(1, queries.length);
    assertTrue(queries[0] instanceof BooleanQuery);
  }
@Test
  public void testParseMixedTokenPhrase() throws Exception {
    QueryParser parser = new QueryParser();
    Query[] queries = parser.parse("content", "{Person.gender==\"male\"}{Token}", "Token", null, null);
    assertEquals(1, queries.length);
    assertTrue(queries[0] instanceof BooleanQuery);
    assertTrue(parser.needValidation());
  }
@Test
  public void testParseWithCorpusAndSet() throws Exception {
    QueryParser parser = new QueryParser();
    Query[] queries = parser.parse("content", "{Token}", "Token", "corpus123", "defaultSet");
    assertEquals(1, queries.length);
    assertTrue(queries[0] instanceof BooleanQuery);
  }
@Test
  public void testParseAnnotationTypeWithStringValue() throws Exception {
    QueryParser parser = new QueryParser();
    Query[] queries = parser.parse("content", "{Person==\"John\"}", "Token", null, null);
    assertEquals(1, queries.length);
    Query q = queries[0];
    assertTrue(q instanceof BooleanQuery);
  }
@Test
  public void testParseAnnotationFeatureEqualsString() throws Exception {
    QueryParser parser = new QueryParser();
    Query[] queries = parser.parse("text", "{Person.gender==\"male\"}", "Token", null, null);
    assertEquals(1, queries.length);
    BooleanQuery bq = (BooleanQuery)queries[0];
    assertNotNull(bq);
  }
@Test
  public void testFindTokensSingleAnnotation() throws Exception {
    QueryParser qp = new QueryParser();
    List<String> tokens = qp.findTokens("{Token}");
    assertEquals(1, tokens.size());
    assertEquals("{Token}", tokens.get(0));
  }
@Test
  public void testFindTokensMultipleMix() throws Exception {
    QueryParser qp = new QueryParser();
    List<String> tokens = qp.findTokens("{Person}\"said\"");
    assertEquals(2, tokens.size());
    assertEquals("{Person}", tokens.get(0));
    assertEquals("\"said\"", tokens.get(1));
  }
@Test
  public void testFindTokensWithEscapedBraces() throws Exception {
    QueryParser qp = new QueryParser();
    List<String> tokens = qp.findTokens("{Person} \\{escaped\\}");
    assertEquals(2, tokens.size());
  }
@Test(expected = SearchException.class)
  public void testFindTokensUnbalancedBraceOpen() throws Exception {
    QueryParser qp = new QueryParser();
    qp.findTokens("{Token");
  }

  @Test(expected = SearchException.class)
  public void testFindTokensUnbalancedBraceClose() throws Exception {
    QueryParser qp = new QueryParser();
    qp.findTokens("Token}");
  }
@Test(expected = SearchException.class)
  public void testFindTokensUnbalancedBraceClose() throws Exception {
    QueryParser qp = new QueryParser();
    qp.findTokens("Token}
@Test
  public void testCreateTermsQuotedText() throws Exception {
    QueryParser qp = new QueryParser();
    List<?>[] result = qp.createTerms("\"hello world\"");
    List<?> terms = result[0];
    assertEquals(2, terms.size());
    assertTrue(terms.get(0) instanceof Term);
  }
@Test
  public void testCreateTermsAnnotationTypeOnly() throws Exception {
    QueryParser qp = new QueryParser();
    List<?>[] result = qp.createTerms("{Token}");
    List<?> terms = result[0];
    assertEquals(1, terms.size());
    Term term = (Term)terms.get(0);
    assertEquals("Token", term.text());
    assertEquals("*", term.type());
  }
@Test
  public void testCreateTermsWithFeatureEqualsQuoted() throws Exception {
    QueryParser qp = new QueryParser();
    List<?>[] result = qp.createTerms("{Person.gender==\"male\"}");
    List<?> terms = result[0];
    assertEquals(1, terms.size());
    Term term = (Term)terms.get(0);
    assertEquals("male", term.text());
    assertEquals("Person.gender", term.type());
  }
@Test
  public void testCreateTermsWithMultipleAnnotations() throws Exception {
    QueryParser qp = new QueryParser();
    List<?>[] result = qp.createTerms("{Token, Token==\"the\"}");
    List<?> terms = result[0];
    assertEquals(2, terms.size());
  }
@Test(expected = SearchException.class)
  public void testCreateTermsMissingEqualOperator() throws Exception {
    QueryParser qp = new QueryParser();
    qp.createTerms("{Person.feature\"value\"}");
  }
@Test(expected = SearchException.class)
  public void testCreateTermsInvalidDotWithoutEquals() throws Exception {
    QueryParser qp = new QueryParser();
    qp.createTerms("{Person.feature}");
  }
@Test
  public void testNeedValidationTrueAfterParsing() throws Exception {
    QueryParser parser = new QueryParser();
    parser.parse("text", "{Person.gender==\"male\"}{Token}", "Token", null, null);
    assertTrue(parser.needValidation());
  }
@Test
  public void testNeedValidationFalseForTokensOnly() throws Exception {
    QueryParser parser = new QueryParser();
    parser.parse("text", "{Token}{Token}", "Token", null, null);
    assertFalse(parser.needValidation());
  }
@Test
  public void testGetQueryStringAfterParse() throws Exception {
    QueryParser parser = new QueryParser();
    parser.parse("text", "{Token}", "Token", null, null);
    String queryString = parser.getQueryString(0);
    assertNotNull(queryString);
  }
@Test(expected = IndexOutOfBoundsException.class)
  public void testGetQueryStringInvalidIndex() throws Exception {
    QueryParser parser = new QueryParser();
    parser.parse("text", "{Token}", "Token", null, null);
    parser.getQueryString(10);
  }
@Test
  public void testParseMultipleQuerySegments() throws Exception {
    QueryParser parser = new QueryParser();
    Query[] queries = parser.parse("text", "{Token}|{Token}", "Token", null, null);
    assertEquals(2, queries.length);
  } 
}