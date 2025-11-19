public class QueryParser_wosr_2_GPTLLMTest { 

 @Test
  public void testValidSimpleTokenQuery() throws Exception {
    QueryParser qp = new QueryParser();
    String query = "{Token}";
    Query[] result = qp.parse("content", query, "Token", null, null);
    
    assertNotNull(result);
    assertEquals(1, result.length);
    assertTrue(result[0] instanceof BooleanQuery);
    BooleanQuery boolQuery = (BooleanQuery) result[0];
    List<?> clauses = boolQuery.getClauses();
    assertEquals(2, clauses.size());
  }
@Test
  public void testValidMultiTokenQuery() throws Exception {
    QueryParser qp = new QueryParser();
    String query = "{Token}{Token}";
    Query[] result = qp.parse("text", query, "Token", null, null);
    assertNotNull(result);
    assertEquals(1, result.length);
    Query mainQuery = result[0];
    assertTrue(mainQuery instanceof BooleanQuery);
    BooleanQuery boolQuery = (BooleanQuery) mainQuery;
    List<?> clauses = boolQuery.getClauses();
    assertEquals(2, clauses.size());
  }
@Test
  public void testPhraseQueryWithQuotedText() throws Exception {
    QueryParser qp = new QueryParser();
    String query = "\"hello world\"";
    Query[] result = qp.parse("text", query, "Token", null, null);
    assertNotNull(result);
    assertEquals(1, result.length);
    BooleanQuery bq = (BooleanQuery) result[0];
    List<?> clauses = bq.getClauses();
    assertEquals(2, clauses.size());
  }
@Test
  public void testFeatureEqualsStringQuery() throws Exception {
    QueryParser qp = new QueryParser();
    String query = "{Person.gender==\"male\"}";
    Query[] result = qp.parse("text", query, "Token", null, null);
    assertNotNull(result);
    assertEquals(1, result.length);
    Query q = result[0];
    assertTrue(q instanceof BooleanQuery);
  }
@Test
  public void testAnnotationWithMultipleFeatures() throws Exception {
    QueryParser qp = new QueryParser();
    String query = "{Person, Token, Person.gender==\"male\"}";
    Query[] result = qp.parse("doc", query, "Token", null, null);
    assertNotNull(result);
    assertEquals(1, result.length);
    assertTrue(result[0] instanceof BooleanQuery);
  }
@Test(expected = SearchException.class)
  public void testUnbalancedOpeningBraceThrowsException() throws Exception {
    QueryParser qp = new QueryParser();
    qp.findTokens("{Token");
  }

  @Test(expected = SearchException.class)
  public void testUnbalancedClosingBraceThrowsException() throws Exception {
    QueryParser qp = new QueryParser();
    qp.findTokens("Token}");
  }
@Test(expected = SearchException.class)
  public void testUnbalancedClosingBraceThrowsException() throws Exception {
    QueryParser qp = new QueryParser();
    qp.findTokens("Token}
@Test
  public void testIsValidQueryReturnsTrueForCorrectSyntax() {
    String query = "{Token}";
    assertTrue(QueryParser.isValidQuery(query));
  }
@Test
  public void testIsValidQueryReturnsFalseForIncorrectSyntax() {
    String query = "{Token";
    assertFalse(QueryParser.isValidQuery(query));
  }

  @Test
  public void testGetQueryString() throws Exception {
    QueryParser qp = new QueryParser();
    String query = "{Token.gender==\"male\"}";
    qp.parse("fieldName", query, "Token", null, null);
    String result = qp.getQueryString(0);
    assertEquals(query, result);
  }

  @Test
  public void testCreateTermsWithQuotedString() throws Exception {
    QueryParser qp = new QueryParser();
    String token = "\"hello world\"";
    List<?>[] terms = qp.createTerms(token);
    @SuppressWarnings("unchecked")
    List<Term> termList = (List<Term>) terms[0];
    assertEquals(2, termList.size());
    assertEquals("hello", termList.get(0).text());
    assertEquals("world", termList.get(1).text());
  }

  @Test
  public void testCreateTermsWithAnnotationTypeOnly() throws Exception {
    QueryParser qp = new QueryParser();
    String token = "{Token}";
    List<?>[] terms = qp.createTerms(token);
    @SuppressWarnings("unchecked")
    List<Term> termList = (List<Term>) terms[0];
    assertEquals(1, termList.size());
    Term term = termList.get(0);
    assertEquals("Token", term.text());
    assertEquals("*", term.type());
  }

  @Test(expected = SearchException.class)
  public void testCreateTermsWithMissingOperator() throws Exception {
    QueryParser qp = new QueryParser();
    qp.createTerms("{Token.feature=\"value\"}");
  }

  @Test
  public void testNeedValidationTrueScenario() throws Exception {
    QueryParser qp = new QueryParser();
    String query = "{Person.gender==\"male\"}";
    qp.parse("text", query, "Token", null, null);
    assertTrue(qp.needValidation());
  }

  @Test
  public void testNeedValidationFalseScenario() throws Exception {
    QueryParser qp = new QueryParser();
    String query = "{Token}";
    qp.parse("text", query, "Token", null, null);
    assertFalse(qp.needValidation());
  }

  @Test
  public void testCombinedCorpusAndAnnotationSetQuery() throws Exception {
    QueryParser qp = new QueryParser();
    String query = "\"Hello\"";
    String corpusId = "corpus42";
    String annotationSet = "OriginalMarkups";
    Query[] qs = qp.parse("content", query, "Token", corpusId, annotationSet);
    assertNotNull(qs);
    assertEquals(1, qs.length);
    assertTrue(qs[0] instanceof BooleanQuery);
  }

  @Test
  public void testEscapedBracesInString() throws Exception {
    QueryParser qp = new QueryParser();
    String query = "\"\\{Hello\\}\"";
    List<String> tokens = qp.findTokens(query);
    assertEquals(1, tokens.size());
    assertEquals("\"\\{Hello\\}\"", tokens.get(0));
  }

  @Test
  public void testMixedQuotedAndAnnotationQuery() throws Exception {
    QueryParser qp = new QueryParser();
    String query = "{Person}\"said\"";
    Query[] result = qp.parse("text", query, "Token", null, null);
    assertNotNull(result);
    assertEquals(1, result.length);
    assertTrue(result[0] instanceof BooleanQuery);
  }

  @Test
  public void testParsingMultipleQueriesWithOR() throws Exception {
    QueryParser qp = new QueryParser();
    String query = "{Token}|\"hello\"";
    Query[] result = qp.parse("text", query, "Token", null, null);
    assertNotNull(result);
    assertEquals(2, result.length);
    assertTrue(result[0] instanceof BooleanQuery);
    assertTrue(result[1] instanceof BooleanQuery);
  }
}
@Test
  public void testGetQueryString() throws Exception {
    QueryParser qp = new QueryParser();
    String query = "{Token.gender==\"male\"}";
    qp.parse("fieldName", query, "Token", null, null);
    String result = qp.getQueryString(0);
    assertEquals(query, result);
  }
@Test
  public void testCreateTermsWithQuotedString() throws Exception {
    QueryParser qp = new QueryParser();
    String token = "\"hello world\"";
    List<?>[] terms = qp.createTerms(token);
    @SuppressWarnings("unchecked")
    List<Term> termList = (List<Term>) terms[0];
    assertEquals(2, termList.size());
    assertEquals("hello", termList.get(0).text());
    assertEquals("world", termList.get(1).text());
  }
@Test
  public void testCreateTermsWithAnnotationTypeOnly() throws Exception {
    QueryParser qp = new QueryParser();
    String token = "{Token}";
    List<?>[] terms = qp.createTerms(token);
    @SuppressWarnings("unchecked")
    List<Term> termList = (List<Term>) terms[0];
    assertEquals(1, termList.size());
    Term term = termList.get(0);
    assertEquals("Token", term.text());
    assertEquals("*", term.type());
  }
@Test(expected = SearchException.class)
  public void testCreateTermsWithMissingOperator() throws Exception {
    QueryParser qp = new QueryParser();
    qp.createTerms("{Token.feature=\"value\"}");
  }
@Test
  public void testNeedValidationTrueScenario() throws Exception {
    QueryParser qp = new QueryParser();
    String query = "{Person.gender==\"male\"}";
    qp.parse("text", query, "Token", null, null);
    assertTrue(qp.needValidation());
  }
@Test
  public void testNeedValidationFalseScenario() throws Exception {
    QueryParser qp = new QueryParser();
    String query = "{Token}";
    qp.parse("text", query, "Token", null, null);
    assertFalse(qp.needValidation());
  }
@Test
  public void testCombinedCorpusAndAnnotationSetQuery() throws Exception {
    QueryParser qp = new QueryParser();
    String query = "\"Hello\"";
    String corpusId = "corpus42";
    String annotationSet = "OriginalMarkups";
    Query[] qs = qp.parse("content", query, "Token", corpusId, annotationSet);
    assertNotNull(qs);
    assertEquals(1, qs.length);
    assertTrue(qs[0] instanceof BooleanQuery);
  }
@Test
  public void testEscapedBracesInString() throws Exception {
    QueryParser qp = new QueryParser();
    String query = "\"\\{Hello\\}\"";
    List<String> tokens = qp.findTokens(query);
    assertEquals(1, tokens.size());
    assertEquals("\"\\{Hello\\}\"", tokens.get(0));
  }
@Test
  public void testMixedQuotedAndAnnotationQuery() throws Exception {
    QueryParser qp = new QueryParser();
    String query = "{Person}\"said\"";
    Query[] result = qp.parse("text", query, "Token", null, null);
    assertNotNull(result);
    assertEquals(1, result.length);
    assertTrue(result[0] instanceof BooleanQuery);
  }
@Test
  public void testParsingMultipleQueriesWithOR() throws Exception {
    QueryParser qp = new QueryParser();
    String query = "{Token}|\"hello\"";
    Query[] result = qp.parse("text", query, "Token", null, null);
    assertNotNull(result);
    assertEquals(2, result.length);
    assertTrue(result[0] instanceof BooleanQuery);
    assertTrue(result[1] instanceof BooleanQuery);
  } 
}