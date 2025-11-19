public class QueryParser_wosr_4_GPTLLMTest { 

 @Test
  public void testValidSimpleAnnotationQuery() throws Exception {
    QueryParser parser = new QueryParser();
    Query[] results = parser.parse("content", "{Token}", "Token", null, null);
    assertEquals(1, results.length);
    assertTrue(results[0] instanceof BooleanQuery);
    BooleanQuery bq = (BooleanQuery) results[0];
    assertEquals(2, bq.getClauses().size());
    Query clause1 = bq.getClauses().get(0).getQuery();
    Query clause2 = bq.getClauses().get(1).getQuery();
    assertTrue(clause1 instanceof TermQuery);
    assertTrue(clause2 instanceof TermQuery || clause2 instanceof PhraseQuery);
  }
@Test
  public void testValidLiteralQuery() throws Exception {
    QueryParser parser = new QueryParser();
    Query[] results = parser.parse("content", "\"hello\"", "Token", null, null);
    assertEquals(1, results.length);
    Query query = results[0];
    assertTrue(query instanceof BooleanQuery);
    BooleanQuery bq = (BooleanQuery) query;
    Query phrase = bq.getClauses().get(1).getQuery();
    assertTrue(phrase instanceof TermQuery || phrase instanceof PhraseQuery);
  }
@Test
  public void testAnnotationTypeWithFeatureEqualsString() throws Exception {
    QueryParser parser = new QueryParser();
    Query[] results = parser.parse("content", "{Person.gender==\"male\"}", "Token", null, null);
    assertEquals(1, results.length);
    BooleanQuery bq = (BooleanQuery) results[0];
    Query phrase = bq.getClauses().get(1).getQuery();
    assertTrue(phrase instanceof TermQuery || phrase instanceof PhraseQuery);
  }
@Test
  public void testMultipleAnnotationTypes() throws Exception {
    QueryParser parser = new QueryParser();
    Query[] results = parser.parse("content", "{Token, Lookup}", "Token", null, null);
    assertEquals(1, results.length);
    Query q = results[0];
    assertTrue(q instanceof BooleanQuery);
    BooleanQuery bq = (BooleanQuery) q;
    assertEquals(2, bq.getClauses().size());
  }
@Test
  public void testSimplePhraseQuery() throws Exception {
    QueryParser parser = new QueryParser();
    Query[] results = parser.parse("content", "{Token} \"hello world\"", "Token", null, null);
    assertEquals(1, results.length);
    BooleanQuery bq = (BooleanQuery) results[0];
    Query phrase = bq.getClauses().get(1).getQuery();
    assertTrue(phrase instanceof PhraseQuery);
  }
@Test
  public void testCorpusAndAnnotationSetAreNull() throws Exception {
    QueryParser parser = new QueryParser();
    Query[] results = parser.parse("field", "{Lookup} \"John\"", "Token", null, null);
    assertEquals(1, results.length);
    BooleanQuery bq = (BooleanQuery) results[0];
    assertEquals(2, bq.getClauses().size());
    TermQuery first = (TermQuery) bq.getClauses().get(0).getQuery();
    assertEquals(Constants.ANNOTATION_SET_ID, first.getTerm().type());
    assertEquals(Constants.COMBINED_SET, first.getTerm().text());
  }
@Test
  public void testCorpusIdProvided() throws Exception {
    QueryParser parser = new QueryParser();
    Query[] results = parser.parse("field", "{Lookup}", "Token", "corpus123", null);
    assertEquals(1, results.length);
    BooleanQuery bq = (BooleanQuery) results[0];
    boolean foundCorpusTerm = false;
    for (int i = 0; i < bq.getClauses().size(); i++) {
      Query sub = bq.getClauses().get(i).getQuery();
      if (sub instanceof TermQuery) {
        Term term = ((TermQuery) sub).getTerm();
        if (term.type().equals(Constants.CORPUS_ID) && term.text().equals("corpus123")) {
          foundCorpusTerm = true;
        }
      }
    }
    assertTrue(foundCorpusTerm);
  }
@Test
  public void testAnnotationSetProvided() throws Exception {
    QueryParser parser = new QueryParser();
    Query[] results = parser.parse("field", "{Lookup}", "Token", null, "annSet");
    assertEquals(1, results.length);
    BooleanQuery bq = (BooleanQuery) results[0];
    boolean foundAnnSetTerm = false;
    for (int i = 0; i < bq.getClauses().size(); i++) {
      Query sub = bq.getClauses().get(i).getQuery();
      if (sub instanceof TermQuery) {
        Term t = ((TermQuery) sub).getTerm();
        if (t.type().equals(Constants.ANNOTATION_SET_ID) && t.text().equals("annSet")) {
          foundAnnSetTerm = true;
        }
      }
    }
    assertTrue(foundAnnSetTerm);
  }
@Test
  public void testFindTokensSplitsCorrectly() throws Exception {
    QueryParser parser = new QueryParser();
    List<String> tokens = parser.findTokens("{Person} \"said\" {Token}");
    assertEquals(3, tokens.size());
    assertEquals("{Person}", tokens.get(0));
    assertEquals("\"said\"", tokens.get(1));
    assertEquals("{Token}", tokens.get(2));
  }
@Test
  public void testIsValidQueryTrue() {
    assertTrue(QueryParser.isValidQuery("{Token}"));
  }
@Test
  public void testIsValidQueryFalse() {
    assertFalse(QueryParser.isValidQuery("{Token"));
  }

  @Test
  public void testCreateTermsWithSingleQuotedLiteral() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("\"hello\"");
    List<?> terms = result[0];
    assertEquals(1, terms.size());
    assertTrue(terms.get(0) instanceof Term);
    Term t = (Term) terms.get(0);
    assertEquals("hello", t.text());
  }

  @Test
  public void testCreateTermsWithMultipleSpaceSeparatedWords() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("\"hello world how are you\"");
    List<?> terms = result[0];
    assertEquals(5, terms.size());
    for(Object o : terms) assertTrue(o instanceof Term);
  }

  @Test
  public void testCreateTermsWithAnnotationAndFeature() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("{Person.gender==\"female\"}");
    List<?> terms = result[0];
    assertEquals(1, terms.size());
    Term t = (Term) terms.get(0);
    assertEquals("female", t.text());
    assertEquals("Person.gender", t.type());
  }

  @Test
  public void testFeatureWithDotOnlyNoEquals() {
    QueryParser parser = new QueryParser();
    try {
      parser.createTerms("{Person.gender}");
      fail("Expected SearchException");
    } catch (SearchException e) {
      assertEquals("missing operator", e.getReason());
    } catch (Exception e) {
      fail("Expected SearchException");
    }
  }

  @Test
  public void testCreateTermsWithMultipleMixedTerms() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("{Token, Token.string==\"and\", Person.gender==\"male\"}");
    List<?> terms = result[0];
    assertEquals(3, terms.size());
  }

  @Test
  public void testCreateTermsWithLiteralText() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("\"London is beautiful\"");
    List<?> terms = result[0];
    assertEquals(3, terms.size());
    Term t0 = (Term) terms.get(0);
    Term t2 = (Term) terms.get(2);
    assertEquals("London", t0.text());
    assertEquals("beautiful", t2.text());
  }

  @Test
  public void testNeedValidationIsFalseForSingleToken() throws Exception {
    QueryParser parser = new QueryParser();
    parser.parse("content", "{Token}", "Token", null, null);
    assertFalse(parser.needValidation());
  }

  @Test
  public void testNeedValidationIsTrueForComplexPhrase() throws Exception {
    QueryParser parser = new QueryParser();
    parser.parse("content", "{Token} \"hello\" {Person.gender==\"male\"}", "Token", null, null);
    assertTrue(parser.needValidation());
  }

  @Test
  public void testGetQueryStringReturnsCorrectString() throws Exception {
    QueryParser parser = new QueryParser();
    parser.parse("content", "{Token}", "Token", null, null);
    String qStr = parser.getQueryString(0);
    assertNotNull(qStr);
    assertTrue(qStr.contains("Token"));
  }
}
@Test
  public void testCreateTermsWithSingleQuotedLiteral() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("\"hello\"");
    List<?> terms = result[0];
    assertEquals(1, terms.size());
    assertTrue(terms.get(0) instanceof Term);
    Term t = (Term) terms.get(0);
    assertEquals("hello", t.text());
  }
@Test
  public void testCreateTermsWithMultipleSpaceSeparatedWords() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("\"hello world how are you\"");
    List<?> terms = result[0];
    assertEquals(5, terms.size());
    for(Object o : terms) assertTrue(o instanceof Term);
  }
@Test
  public void testCreateTermsWithAnnotationAndFeature() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("{Person.gender==\"female\"}");
    List<?> terms = result[0];
    assertEquals(1, terms.size());
    Term t = (Term) terms.get(0);
    assertEquals("female", t.text());
    assertEquals("Person.gender", t.type());
  }
@Test
  public void testFeatureWithDotOnlyNoEquals() {
    QueryParser parser = new QueryParser();
    try {
      parser.createTerms("{Person.gender}");
      fail("Expected SearchException");
    } catch (SearchException e) {
      assertEquals("missing operator", e.getReason());
    } catch (Exception e) {
      fail("Expected SearchException");
    }
  }
@Test
  public void testCreateTermsWithMultipleMixedTerms() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("{Token, Token.string==\"and\", Person.gender==\"male\"}");
    List<?> terms = result[0];
    assertEquals(3, terms.size());
  }
@Test
  public void testCreateTermsWithLiteralText() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("\"London is beautiful\"");
    List<?> terms = result[0];
    assertEquals(3, terms.size());
    Term t0 = (Term) terms.get(0);
    Term t2 = (Term) terms.get(2);
    assertEquals("London", t0.text());
    assertEquals("beautiful", t2.text());
  }
@Test
  public void testNeedValidationIsFalseForSingleToken() throws Exception {
    QueryParser parser = new QueryParser();
    parser.parse("content", "{Token}", "Token", null, null);
    assertFalse(parser.needValidation());
  }
@Test
  public void testNeedValidationIsTrueForComplexPhrase() throws Exception {
    QueryParser parser = new QueryParser();
    parser.parse("content", "{Token} \"hello\" {Person.gender==\"male\"}", "Token", null, null);
    assertTrue(parser.needValidation());
  }
@Test
  public void testGetQueryStringReturnsCorrectString() throws Exception {
    QueryParser parser = new QueryParser();
    parser.parse("content", "{Token}", "Token", null, null);
    String qStr = parser.getQueryString(0);
    assertNotNull(qStr);
    assertTrue(qStr.contains("Token"));
  } 
}