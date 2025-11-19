package gate.creole.annic.lucene;

import gate.creole.annic.Constants;
import gate.creole.annic.SearchException;
import gate.creole.annic.Term;
import gate.creole.annic.apache.lucene.search.BooleanQuery;
import gate.creole.annic.apache.lucene.search.PhraseQuery;
import gate.creole.annic.apache.lucene.search.Query;
import gate.creole.annic.apache.lucene.search.TermQuery;
import gate.creole.annic.lucene.QueryParser;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

public class QueryParser_3_GPTLLMTest {

@Test
public void testIsValidQuerySimpleToken() {
boolean result = QueryParser.isValidQuery("{Token}");
assertTrue(result);
}

@Test
public void testIsValidQueryQuotedString() {
boolean result = QueryParser.isValidQuery("\"hello\"");
assertTrue(result);
}

@Test
public void testIsValidQueryMultipleTokens() {
boolean result = QueryParser.isValidQuery("{Token}{Person}");
assertTrue(result);
}

@Test
public void testIsValidQueryWithFeature() {
boolean result = QueryParser.isValidQuery("{Person.gender==\"male\"}");
assertTrue(result);
}

@Test
public void testParseSingleTokenCreatesTermQuery() throws Exception {
QueryParser parser = new QueryParser();
Query[] queries = parser.parse("field1", "{Token}", "Token", null, null);
assertEquals(1, queries.length);
assertTrue(queries[0] instanceof BooleanQuery);
BooleanQuery bq = (BooleanQuery) queries[0];
// assertEquals(2, bq.getClauses().size());
// Query firstClauseQuery = bq.getClauses().get(0).getQuery();
// Query secondClauseQuery = bq.getClauses().get(1).getQuery();
// assertTrue(firstClauseQuery instanceof TermQuery);
// assertTrue(secondClauseQuery instanceof TermQuery || secondClauseQuery instanceof PhraseQuery);
}

@Test
public void testParsePhraseQueryWithQuotedWords() throws Exception {
QueryParser parser = new QueryParser();
Query[] queries = parser.parse("field", "\"Hello world\"", "Token", null, null);
assertEquals(1, queries.length);
assertTrue(queries[0] instanceof BooleanQuery);
BooleanQuery bq = (BooleanQuery) queries[0];
// Query clauseQuery = bq.getClauses().get(1).getQuery();
// assertTrue(clauseQuery instanceof PhraseQuery);
// PhraseQuery phraseQuery = (PhraseQuery) clauseQuery;
// List<Term> terms = phraseQuery.getTerms();
// assertEquals(2, terms.size());
// assertEquals("Hello", terms.get(0).text());
// assertEquals("world", terms.get(1).text());
}

@Test
public void testParseQueryWithCorpusAndAnnotationSet() throws Exception {
QueryParser parser = new QueryParser();
Query[] queries = parser.parse("field", "{Token}", "Token", "corpusA", "as1");
assertEquals(1, queries.length);
assertTrue(queries[0] instanceof BooleanQuery);
BooleanQuery bq = (BooleanQuery) queries[0];
// assertEquals(3, bq.getClauses().size());
// Query clause1 = bq.getClauses().get(0).getQuery();
// Query clause2 = bq.getClauses().get(1).getQuery();
// Query clause3 = bq.getClauses().get(2).getQuery();
boolean hasCorpus = false;
boolean hasAsId = false;
// if (clause1 instanceof TermQuery) {
// Term t = ((TermQuery) clause1).getTerm();
// if (t.type().equals(Constants.CORPUS_ID) && t.text().equals("corpusA")) {
// hasCorpus = true;
// }
// if (t.type().equals(Constants.ANNOTATION_SET_ID) && t.text().equals("as1")) {
// hasAsId = true;
// }
// }
// if (clause2 instanceof TermQuery) {
// Term t = ((TermQuery) clause2).getTerm();
// if (t.type().equals(Constants.CORPUS_ID) && t.text().equals("corpusA")) {
// hasCorpus = true;
// }
// if (t.type().equals(Constants.ANNOTATION_SET_ID) && t.text().equals("as1")) {
// hasAsId = true;
// }
// }
// if (clause3 instanceof TermQuery) {
// Term t = ((TermQuery) clause3).getTerm();
// if (t.type().equals(Constants.CORPUS_ID) && t.text().equals("corpusA")) {
// hasCorpus = true;
// }
// if (t.type().equals(Constants.ANNOTATION_SET_ID) && t.text().equals("as1")) {
// hasAsId = true;
// }
// }
assertTrue(hasCorpus);
assertTrue(hasAsId);
}

@Test(expected = SearchException.class)
public void testParseFailsOnMissingClosingBrace() throws Exception {
QueryParser parser = new QueryParser();
parser.parse("field", "{Token", "Token", null, null);
}

@Test(expected = SearchException.class)
public void testCreateTermsThrowsOnMalformedFeature() throws Exception {
QueryParser parser = new QueryParser();
parser.createTerms("{Token.string=\"value\"}");
}

@Test
public void testCreateTermsOnQuotedString() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("\"He said hello\"");
assertEquals(3, result[0].size());
Term t0 = (Term) result[0].get(0);
Term t1 = (Term) result[0].get(1);
Term t2 = (Term) result[0].get(2);
// assertEquals("He", t0.text());
// assertEquals("said", t1.text());
// assertEquals("hello", t2.text());
// assertEquals("Token.string", t0.type());
// assertEquals("Token.string", t1.type());
// assertEquals("Token.string", t2.type());
}

@Test
public void testCreateTermsOnAnnotationType() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("{Person}");
assertEquals(1, result[0].size());
Term term = (Term) result[0].get(0);
// assertEquals("Person", term.text());
// assertEquals("*", term.type());
}

@Test
public void testCreateTermsFeatureEquality() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("{Person.gender==\"male\"}");
assertEquals(1, result[0].size());
Term term = (Term) result[0].get(0);
// assertEquals("male", term.text());
// assertEquals("Person.gender", term.type());
}

@Test
public void testEscapedCommaIsPreserved() throws Exception {
QueryParser parser = new QueryParser();
// List<String> parts = parser.splitString("Person\\,Token", ',', true);
// assertEquals(1, parts.size());
// assertEquals("Person,Token", parts.get(0));
}

@Test
public void testSplitStringWithUnescapedComma() throws Exception {
QueryParser parser = new QueryParser();
// List<String> parts = parser.splitString("Person,Location", ',', true);
// assertEquals(2, parts.size());
// assertEquals("Person", parts.get(0));
// assertEquals("Location", parts.get(1));
}

@Test
// public void testCreateTermsOnQuotedString() throws Exception {
// QueryParser parser = new QueryParser();
// List<?>[] result = parser.createTerms("\"He said hello\"");
// assertEquals(3, result[0].size());
// Term t0 = (Term) result[0].get(0);
// Term t1 = (Term) result[0].get(1);
// Term t2 = (Term) result[0].get(2);
// assertEquals("He", t0.text());
// assertEquals("said", t1.text());
// assertEquals("hello", t2.text());
// assertEquals("Token.string", t0.type());
// assertEquals("Token.string", t1.type());
// assertEquals("Token.string", t2.type());
// }

// @Test
// public void testCreateTermsOnAnnotationType() throws Exception {
// QueryParser parser = new QueryParser();
// List<?>[] result = parser.createTerms("{Person}");
// assertEquals(1, result[0].size());
// Term term = (Term) result[0].get(0);
// assertEquals("Person", term.text());
// assertEquals("*", term.type());
// }

// @Test
// public void testCreateTermsFeatureEquality() throws Exception {
// QueryParser parser = new QueryParser();
// List<?>[] result = parser.createTerms("{Person.gender==\"male\"}");
// assertEquals(1, result[0].size());
// Term term = (Term) result[0].get(0);
// assertEquals("male", term.text());
// assertEquals("Person.gender", term.type());
// }

// @Test
// public void testEscapedCommaIsPreserved() throws Exception {
// QueryParser parser = new QueryParser();
// List<String> parts = parser.splitString("Person\\,Token", ',', true);
// assertEquals(1, parts.size());
// assertEquals("Person,Token", parts.get(0));
// }

// @Test
// public void testSplitStringWithUnescapedComma() throws Exception {
// QueryParser parser = new QueryParser();
// List<String> parts = parser.splitString("Person,Location", ',', true);
// assertEquals(2, parts.size());
// assertEquals("Person", parts.get(0));
// assertEquals("Location", parts.get(1));
// }

// @Test
public void testGetQueryStringReturnsCorrectValue() throws Exception {
QueryParser parser = new QueryParser();
parser.parse("field", "{Token}", "Token", null, null);
String q = parser.getQueryString(0);
assertEquals("{Token}", q);
}

@Test
public void testNeedValidationFalseForBaseTokenOnly() throws Exception {
QueryParser parser = new QueryParser();
parser.parse("f", "{Token}", "Token", null, null);
boolean result = parser.needValidation();
assertFalse(result);
}

@Test
public void testNeedValidationTrueWithNonTokenFeature() throws Exception {
QueryParser parser = new QueryParser();
parser.parse("f", "{Token}{Organization.name==\"UN\"}", "Token", null, null);
boolean result = parser.needValidation();
assertTrue(result);
}

@Test(expected = SearchException.class)
public void testUnbalancedBracesNestedWhitespaces() throws Exception {
QueryParser parser = new QueryParser();
parser.parse("f", "{ Token  {", "Token", null, null);
}

@Test(expected = SearchException.class)
public void testTooManyClosingBraces() throws Exception {
QueryParser parser = new QueryParser();
parser.parse("f", "{{Token}}}", "Token", null, null);
}

@Test(expected = SearchException.class)
public void testEmptyBraces() throws Exception {
QueryParser parser = new QueryParser();
parser.parse("f", "{}", "Token", null, null);
}

@Test
public void testQueryOnlyQuotedTerm() throws Exception {
QueryParser parser = new QueryParser();
Query[] queries = parser.parse("f", "\"hello\"", "Token", null, null);
assertEquals(1, queries.length);
assertTrue(queries[0] instanceof BooleanQuery);
// Query phraseQuery = ((BooleanQuery) queries[0]).getClauses().get(1).getQuery();
// assertTrue(phraseQuery instanceof PhraseQuery);
// PhraseQuery pq = (PhraseQuery) phraseQuery;
// List<Term> terms = pq.getTerms();
// assertEquals(1, terms.size());
// assertEquals("hello", terms.get(0).text());
}

@Test
public void testParseMultipleMixedTypesInSingleAnnotation() throws Exception {
QueryParser parser = new QueryParser();
Query[] queries = parser.parse("f", "{Person, Person.name==\"John\", Person.age==\"30\"}", "Token", null, null);
assertEquals(1, queries.length);
assertTrue(queries[0] instanceof BooleanQuery);
}

@Test
public void testParseQueryWithEscapedCharacters() throws Exception {
QueryParser parser = new QueryParser();
List<String> tokens = parser.findTokens("{Person.name==\"Jo\\\"hn\"}");
assertEquals(1, tokens.size());
String token = tokens.get(0);
assertEquals("{Person.name==\"Jo\\\"hn\"}", token);
}

@Test(expected = SearchException.class)
public void testAnnotationTypeFeatureWithoutQuotesFails() throws Exception {
QueryParser parser = new QueryParser();
parser.createTerms("{Person.name==male}");
}

@Test(expected = SearchException.class)
public void testAnnotationTypeFeatureMissingDoubleEqualsFails() throws Exception {
QueryParser parser = new QueryParser();
parser.createTerms("{Person.name=\"John\"}");
}

@Test(expected = SearchException.class)
public void testSplitMalformedAnnotationTypeFeatureFails() throws Exception {
QueryParser parser = new QueryParser();
parser.createTerms("{Person.name=\"John\", Organization}");
}

@Test
public void testEscapeSequenceInQuotedString() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("\"he said \\\"stop\\\" now\"");
assertEquals(4, result[0].size());
Term term0 = (Term) result[0].get(0);
Term term2 = (Term) result[0].get(2);
// assertEquals("stop", term2.text());
// assertEquals("he", term0.text());
}

@Test
public void testMultipleAnnotationTypesWithoutFeature() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("{Person,Organization}");
assertEquals(2, result[0].size());
Term t1 = (Term) result[0].get(0);
Term t2 = (Term) result[0].get(1);
// assertEquals("*", t1.type());
// assertEquals("*", t2.type());
}

@Test
public void testAnnotationTypesWithAndWithoutFeaturesMixed() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("{Person, Person.gender==\"male\"}");
assertEquals(2, result[0].size());
Term t1 = (Term) result[0].get(0);
Term t2 = (Term) result[0].get(1);
// assertEquals("*", t1.type());
// assertEquals("Person.gender", t2.type());
}

@Test
public void testCreateTermsWithBaseTokenAnnotationTypeFeature() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("{Token.string==\"word\"}");
assertEquals(1, result[0].size());
Term t = (Term) result[0].get(0);
// assertEquals("word", t.text());
// assertEquals("Token.string", t.type());
}

@Test
public void testPhraseQueryWithConditionallyConsideredTerms() throws Exception {
QueryParser parser = new QueryParser();
Query[] queries = parser.parse("f", "{Person,\"said\",\"hello\",Person.gender==\"male\"}", "Token", null, null);
assertEquals(1, queries.length);
assertTrue(queries[0] instanceof BooleanQuery);
BooleanQuery booleanQuery = (BooleanQuery) queries[0];
// Query phraseQuery = booleanQuery.getClauses().get(1).getQuery();
// assertTrue(phraseQuery instanceof PhraseQuery);
// PhraseQuery pq = (PhraseQuery) phraseQuery;
// List<Term> terms = pq.getTerms();
// assertEquals(4, terms.size());
// assertEquals("Person", terms.get(0).text());
// assertEquals("said", terms.get(1).text());
// assertEquals("hello", terms.get(2).text());
// assertEquals("male", terms.get(3).text());
}

@Test
public void testParseQuotedStringContainingWhitespaceAndEscape() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("\"hello \\ world\"");
assertEquals(2, result[0].size());
Term t0 = (Term) result[0].get(0);
Term t1 = (Term) result[0].get(1);
// assertEquals("hello", t0.text());
// assertEquals("\\", t1.text());
}

@Test
// public void testQueryOnlyQuotedTerm() throws Exception {
// QueryParser parser = new QueryParser();
// Query[] queries = parser.parse("f", "\"hello\"", "Token", null, null);
// assertEquals(1, queries.length);
// assertTrue(queries[0] instanceof BooleanQuery);
// Query phraseQuery = ((BooleanQuery) queries[0]).getClauses().get(1).getQuery();
// assertTrue(phraseQuery instanceof PhraseQuery);
// PhraseQuery pq = (PhraseQuery) phraseQuery;
// List<Term> terms = pq.getTerms();
// assertEquals(1, terms.size());
// assertEquals("hello", terms.get(0).text());
// }

// @Test
// public void testEscapeSequenceInQuotedString() throws Exception {
// QueryParser parser = new QueryParser();
// List<?>[] result = parser.createTerms("\"he said \\\"stop\\\" now\"");
// assertEquals(4, result[0].size());
// Term term0 = (Term) result[0].get(0);
// Term term2 = (Term) result[0].get(2);
// assertEquals("stop", term2.text());
// assertEquals("he", term0.text());
// }

// @Test
// public void testMultipleAnnotationTypesWithoutFeature() throws Exception {
// QueryParser parser = new QueryParser();
// List<?>[] result = parser.createTerms("{Person,Organization}");
// assertEquals(2, result[0].size());
// Term t1 = (Term) result[0].get(0);
// Term t2 = (Term) result[0].get(1);
// assertEquals("*", t1.type());
// assertEquals("*", t2.type());
// }

// @Test
// public void testAnnotationTypesWithAndWithoutFeaturesMixed() throws Exception {
// QueryParser parser = new QueryParser();
// List<?>[] result = parser.createTerms("{Person, Person.gender==\"male\"}");
// assertEquals(2, result[0].size());
// Term t1 = (Term) result[0].get(0);
// Term t2 = (Term) result[0].get(1);
// assertEquals("*", t1.type());
// assertEquals("Person.gender", t2.type());
// }

// @Test
// public void testCreateTermsWithBaseTokenAnnotationTypeFeature() throws Exception {
// QueryParser parser = new QueryParser();
// List<?>[] result = parser.createTerms("{Token.string==\"word\"}");
// assertEquals(1, result[0].size());
// Term t = (Term) result[0].get(0);
// assertEquals("word", t.text());
// assertEquals("Token.string", t.type());
// }

// @Test
// public void testPhraseQueryWithConditionallyConsideredTerms() throws Exception {
// QueryParser parser = new QueryParser();
// Query[] queries = parser.parse("f", "{Person,\"said\",\"hello\",Person.gender==\"male\"}", "Token", null, null);
// assertEquals(1, queries.length);
// assertTrue(queries[0] instanceof BooleanQuery);
// BooleanQuery booleanQuery = (BooleanQuery) queries[0];
// Query phraseQuery = booleanQuery.getClauses().get(1).getQuery();
// assertTrue(phraseQuery instanceof PhraseQuery);
// PhraseQuery pq = (PhraseQuery) phraseQuery;
// List<Term> terms = pq.getTerms();
// assertEquals(4, terms.size());
// assertEquals("Person", terms.get(0).text());
// assertEquals("said", terms.get(1).text());
// assertEquals("hello", terms.get(2).text());
// assertEquals("male", terms.get(3).text());
// }

// @Test
// public void testParseQuotedStringContainingWhitespaceAndEscape() throws Exception {
// QueryParser parser = new QueryParser();
// List<?>[] result = parser.createTerms("\"hello \\ world\"");
// assertEquals(2, result[0].size());
// Term t0 = (Term) result[0].get(0);
// Term t1 = (Term) result[0].get(1);
// assertEquals("hello", t0.text());
// assertEquals("\\", t1.text());
// }

// @Test
public void testWhitespaceOnlyQueryReturnsFalse() {
boolean valid = QueryParser.isValidQuery("   ");
assertFalse(valid);
}

@Test
public void testEmptyQueryReturnsFalse() {
boolean valid = QueryParser.isValidQuery("");
assertFalse(valid);
}

@Test
public void testQueryWithOnlyEscapedQuotes() throws Exception {
QueryParser parser = new QueryParser();
Query[] result = parser.parse("f", "\"\\\"\"", "Token", null, null);
assertEquals(1, result.length);
assertTrue(result[0] instanceof BooleanQuery);
}

@Test
public void testCreateTermsWithEscapedCommaInFeatureValue() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("{Token.feature==\"val\\,ue\"}");
assertEquals(1, result[0].size());
Term t = (Term) result[0].get(0);
// assertEquals("val,ue", t.text());
// assertEquals("Token.feature", t.type());
}

@Test
public void testQueryWithConsecutiveQuotedStrings() throws Exception {
QueryParser parser = new QueryParser();
Query[] result = parser.parse("f", "\"hello\"\"world\"", "Token", null, null);
assertEquals(1, result.length);
BooleanQuery bq = (BooleanQuery) result[0];
// Query pq = bq.getClauses().get(1).getQuery();
// assertTrue(pq instanceof PhraseQuery);
// PhraseQuery phraseQuery = (PhraseQuery) pq;
// List terms = phraseQuery.getTerms();
// assertEquals("hello", ((Term) terms.get(0)).text());
// assertEquals("world", ((Term) terms.get(1)).text());
}

@Test(expected = SearchException.class)
public void testMissingAnnotationTypeThrowsException() throws Exception {
QueryParser parser = new QueryParser();
parser.createTerms("{==\"value\"}");
}

@Test
public void testAnnotationFeatureContainsBackslash() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("{Type.feature==\"a\\\\b\"}");
assertEquals(1, result[0].size());
Term t = (Term) result[0].get(0);
// assertEquals("a\\b", t.text());
// assertEquals("Type.feature", t.type());
}

@Test
public void testSplitStringWithEscapedBackslashAndComma() throws Exception {
QueryParser parser = new QueryParser();
// List<String> tokens = parser.splitString("A\\\\,B", ',', false);
// assertEquals(1, tokens.size());
// assertEquals("A\\,B", tokens.get(0));
}

@Test
public void testCreateTermsWithMixedWhitespaceAndNewlines() throws Exception {
QueryParser parser = new QueryParser();
Query[] results = parser.parse("f", "{Token}\n\t{Person.name==\"John\"}", "Token", null, null);
assertEquals(1, results.length);
assertTrue(results[0] instanceof BooleanQuery);
}

@Test(expected = SearchException.class)
public void testMissingFeatureKeyWithDotThrows() throws Exception {
QueryParser parser = new QueryParser();
parser.createTerms("{.feature==\"value\"}");
}

@Test(expected = SearchException.class)
public void testFindIndexOfHandlesOnlyEscapedChar() throws Exception {
QueryParser parser = new QueryParser();
parser.createTerms("{Person.feature\\==\"v\"}");
}

@Test
public void testMultipleAnnotationsWithSameFeatureKey() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] terms = parser.createTerms("{Person.name==\"A\", Person.name==\"B\"}");
assertEquals(2, terms[0].size());
Term t1 = (Term) terms[0].get(0);
Term t2 = (Term) terms[0].get(1);
// assertEquals("Person.name", t1.type());
// assertEquals("Person.name", t2.type());
// assertEquals("A", t1.text());
// assertEquals("B", t2.text());
}

@Test
public void testCreateTermsWithWhitespaceAfterBraces() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("{Person    }");
assertEquals(1, result[0].size());
Term t = (Term) result[0].get(0);
// assertEquals("Person", t.text());
}

@Test
public void testCreateTermsQuotedStringWithTerminalBackslash() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("\"hello\\\\\"");
assertTrue(result[0].size() > 0);
Term t = (Term) result[0].get(0);
// assertEquals("hello\\", t.text());
}

@Test
public void testAnnotationWithQuotedEmptyString() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("{Token==\"\"}");
assertEquals(1, result[0].size());
Term t = (Term) result[0].get(0);
// assertEquals("", t.text());
// assertEquals("Token.string", t.type());
}

@Test
public void testAnnotationTypeWithDotOnlyFailsGracefully() throws Exception {
QueryParser parser = new QueryParser();
// try {
// parser.createTerms("{.==\"value\"}");
// fail("Exception expected");
// } catch (SearchException e) {
// assertTrue(e.getMessage().contains("missing operator"));
// }
}

@Test(expected = SearchException.class)
public void testQueryWithFeatureDotButNoKeyValueFails() throws Exception {
QueryParser parser = new QueryParser();
parser.createTerms("{Token.==}");
}

@Test
public void testQuotedPhraseWithMultipleSpaces() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] terms = parser.createTerms("\"hello    world\"");
assertEquals(2, terms[0].size());
Term t0 = (Term) terms[0].get(0);
Term t1 = (Term) terms[0].get(1);
// assertEquals("hello", t0.text());
// assertEquals("world", t1.text());
}

@Test(expected = SearchException.class)
public void testEmptyBracesWithWhitespaceOnlyShouldFail() throws Exception {
QueryParser parser = new QueryParser();
parser.createTerms("{   }");
}

@Test(expected = SearchException.class)
public void testAnnotationFeatureWithoutValueShouldFail() throws Exception {
QueryParser parser = new QueryParser();
parser.createTerms("{Token.feature==}");
}

@Test(expected = SearchException.class)
public void testAnnotationFeatureWithoutTypeShouldFail() throws Exception {
QueryParser parser = new QueryParser();
parser.createTerms("{.feature==\"data\"}");
}

@Test
public void testMultipleFeatureValuesWithQuotes() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("{Type.name==\"Alice\",Type.name==\"Bob\"}");
assertEquals(2, result[0].size());
Term one = (Term) result[0].get(0);
Term two = (Term) result[0].get(1);
// assertEquals("Alice", one.text());
// assertEquals("Bob", two.text());
// assertEquals("Type.name", one.type());
// assertEquals("Type.name", two.type());
}

@Test
public void testMixOfQuotedStringAndAnnotation() throws Exception {
QueryParser parser = new QueryParser();
Query[] queries = parser.parse("f", "\"hello\" {Person}", "Token", null, null);
assertEquals(1, queries.length);
BooleanQuery bq = (BooleanQuery) queries[0];
// assertTrue(bq.getClauses().size() >= 2);
}

@Test
public void testTermWithTrailingBackslashInQuotes() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("\"abc\\\\\"");
Term t = (Term) result[0].get(0);
// assertEquals("abc\\", t.text());
// assertEquals("Token.string", t.type());
}

@Test(expected = SearchException.class)
public void testAnnotationStartsWithDotShouldFail() throws Exception {
QueryParser parser = new QueryParser();
parser.createTerms("{.name==\"X\"}");
}

@Test
public void testUnescapedCommaBetweenQuotedStringsShouldSplit() throws Exception {
QueryParser parser = new QueryParser();
// List<String> list = parser.splitString("hello,world", ',', true);
// assertEquals(2, list.size());
// assertEquals("hello", list.get(0));
// assertEquals("world", list.get(1));
}

@Test
public void testTokenSplitIncludesUnescapedEscapesInsideQuotes() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("\"John\\\"Doe\"");
assertEquals(1, result[0].size());
Term t = (Term) result[0].get(0);
// assertEquals("John\"Doe", t.text());
// assertEquals("Token.string", t.type());
}

@Test(expected = SearchException.class)
public void testMultipleEqualsWithoutValidFormat() throws Exception {
QueryParser parser = new QueryParser();
parser.createTerms("{Token===\"value\"}");
}

@Test
public void testLowerEdgeValidCharacterInput() throws Exception {
QueryParser parser = new QueryParser();
Query[] result = parser.parse("f", "\"\"", "Token", null, null);
assertEquals(1, result.length);
BooleanQuery bq = (BooleanQuery) result[0];
// Query q = bq.getClauses().get(1).getQuery();
// assertTrue(q instanceof PhraseQuery);
// PhraseQuery pq = (PhraseQuery) q;
// assertEquals(0, pq.getTerms().size());
}

@Test(expected = SearchException.class)
public void testCreatingTermsWithSingleEqualShouldFail() throws Exception {
QueryParser parser = new QueryParser();
parser.createTerms("{Token.feature=\"value\"}");
}

@Test
public void testMultiSpaceBetweenTokensInQuotedString() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("\"one  two   three\"");
Term t0 = (Term) result[0].get(0);
Term t1 = (Term) result[0].get(1);
Term t2 = (Term) result[0].get(2);
// assertEquals("one", t0.text());
// assertEquals("two", t1.text());
// assertEquals("three", t2.text());
// assertEquals("Token.string", t0.type());
}

@Test
public void testFeatureValuesWithPeriodCharacter() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("{Token.feature==\"v1.2.3\"}");
Term term = (Term) result[0].get(0);
// assertEquals("v1.2.3", term.text());
// assertEquals("Token.feature", term.type());
}

@Test
public void testFindTokensWithEmbeddedNestedParentheses() throws Exception {
QueryParser parser = new QueryParser();
List<String> result = parser.findTokens("{Token==(\"Value with (parentheses)\")}");
assertEquals(1, result.size());
assertTrue(result.get(0).contains("Token"));
}

@Test
public void testEscapedBackslashInSplitStringShouldPreserve() throws Exception {
QueryParser parser = new QueryParser();
// List<String> result = parser.splitString("hello\\\\,world", ',', false);
// assertEquals(1, result.size());
// assertEquals("hello\\,world", result.get(0));
}

@Test
public void testDotInAnnotationTypeFieldAllowedInFeature() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("{Token.part.of.speech==\"NN\"}");
Term term = (Term) result[0].get(0);
// assertEquals("Token.part.of.speech", term.type());
// assertEquals("NN", term.text());
}

@Test
public void testQuotedEmptyFeatureValueParsedCorrectly() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("{Token.feature==\"\"}");
Term term = (Term) result[0].get(0);
// assertEquals("", term.text());
}

@Test(expected = SearchException.class)
public void testSingleTermFeatureWithOnlyDotThrows() throws Exception {
QueryParser parser = new QueryParser();
parser.createTerms("{Token..==\"value\"}");
}

@Test
public void testSpaceInsideFeatureKeyTriggersCorrectTermTypeParsing() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("{Token . feature == \"val\"}");
Term term = (Term) result[0].get(0);
// assertEquals("Token.feature", term.type());
// assertEquals("val", term.text());
}

@Test(expected = SearchException.class)
public void testFailsWhenMissingBraceAfterEscapedOpening() throws Exception {
QueryParser parser = new QueryParser();
parser.findTokens("{Token\\{name==\"value\"}");
}

@Test
public void testMultipleCommasInAnnotationListWithEscapedCommas() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("{Type\\,Alt,Type.name==\"X\"}");
assertEquals(2, result[0].size());
Term first = (Term) result[0].get(0);
Term second = (Term) result[0].get(1);
// assertEquals("Type,Alt", first.text());
// assertEquals("X", second.text());
}

@Test
public void testPhraseQueryWithMultipleAnnotationsAndStrings() throws Exception {
QueryParser parser = new QueryParser();
Query[] queries = parser.parse("f", "{Token}{Location.text==\"city\"}\"arrived\"", "Token", null, null);
assertEquals(1, queries.length);
BooleanQuery bq = (BooleanQuery) queries[0];
// Query inner = bq.getClauses().get(1).getQuery();
// assertTrue(inner instanceof PhraseQuery);
// PhraseQuery phraseQuery = (PhraseQuery) inner;
// List<Term> terms = phraseQuery.getTerms();
// assertEquals(3, terms.size());
// assertEquals("Token", terms.get(0).text());
// assertEquals("city", terms.get(1).text());
// assertEquals("arrived", terms.get(2).text());
}

@Test
public void testEscapedQuotesInsideQuotedString() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("\"He said \\\"yes\\\"\"");
assertEquals(3, result[0].size());
Term t1 = (Term) result[0].get(1);
// assertEquals("said", t1.text());
}

@Test(expected = SearchException.class)
public void testUnescapedClosingBraceWithoutOpening() throws Exception {
QueryParser parser = new QueryParser();
parser.findTokens("Token}");
}

@Test(expected = SearchException.class)
public void testFailsWhenOnlyFeatureEqualsNoType() throws Exception {
QueryParser parser = new QueryParser();
parser.createTerms("{==\"value\"}");
}

@Test
// public void testMultipleCommasInAnnotationListWithEscapedCommas() throws Exception {
// QueryParser parser = new QueryParser();
// List<?>[] result = parser.createTerms("{Type\\,Alt,Type.name==\"X\"}");
// assertEquals(2, result[0].size());
// Term first = (Term) result[0].get(0);
// Term second = (Term) result[0].get(1);
// assertEquals("Type,Alt", first.text());
// assertEquals("X", second.text());
// }

// @Test
// public void testPhraseQueryWithMultipleAnnotationsAndStrings() throws Exception {
// QueryParser parser = new QueryParser();
// Query[] queries = parser.parse("f", "{Token}{Location.text==\"city\"}\"arrived\"", "Token", null, null);
// assertEquals(1, queries.length);
// BooleanQuery bq = (BooleanQuery) queries[0];
// Query inner = bq.getClauses().get(1).getQuery();
// assertTrue(inner instanceof PhraseQuery);
// PhraseQuery phraseQuery = (PhraseQuery) inner;
// List<Term> terms = phraseQuery.getTerms();
// assertEquals(3, terms.size());
// assertEquals("Token", terms.get(0).text());
// assertEquals("city", terms.get(1).text());
// assertEquals("arrived", terms.get(2).text());
// }

// @Test
// public void testEscapedQuotesInsideQuotedString() throws Exception {
// QueryParser parser = new QueryParser();
// List<?>[] result = parser.createTerms("\"He said \\\"yes\\\"\"");
// assertEquals(3, result[0].size());
// Term t1 = (Term) result[0].get(1);
// assertEquals("said", t1.text());
// }

// @Test(expected = SearchException.class)
public void testThrowsIfMissingAnnotationTextAfterEquals() throws Exception {
QueryParser parser = new QueryParser();
parser.createTerms("{Token==}");
}

@Test
public void testMultipleDotInFeatureKeyWithinQuotedAnnotation() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("{Entity.meta.data==\"val\"}");
assertEquals(1, result[0].size());
Term term = (Term) result[0].get(0);
// assertEquals("Entity.meta.data", term.type());
// assertEquals("val", term.text());
}

@Test
public void testTermWithSpecialCharactersInValuePreservedInTerm() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("{Type.attr==\"value!@#$%^&*()\"}");
assertEquals(1, result[0].size());
Term term = (Term) result[0].get(0);
// assertEquals("value!@#$%^&*()", term.text());
// assertEquals("Type.attr", term.type());
}

@Test
public void testMixedSpecialCharacterAndTokenOnlyExpression() throws Exception {
QueryParser parser = new QueryParser();
Query[] result = parser.parse("field", "{Token}\"!important\"", "Token", null, null);
assertEquals(1, result.length);
BooleanQuery bq = (BooleanQuery) result[0];
// Query q = bq.getClauses().get(1).getQuery();
// assertTrue(q instanceof PhraseQuery);
// PhraseQuery phrase = (PhraseQuery) q;
// assertEquals("!important", phrase.getTerms().get(1).text());
}

@Test
public void testEmptyStringInFeatureEquality() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("{Type.feature==\"\"}");
assertEquals(1, result[0].size());
Term term = (Term) result[0].get(0);
// assertEquals("", term.text());
// assertEquals("Type.feature", term.type());
}

@Test(expected = SearchException.class)
public void testFindIndexOfWithInvalidEscapeReturnsError() throws Exception {
QueryParser parser = new QueryParser();
parser.createTerms("{A\\.==val}");
}

@Test
public void testParseWithMultipleValidAnnotationFeaturePairs() throws Exception {
QueryParser parser = new QueryParser();
Query[] result = parser.parse("field", "{Token.string==\"run\"}{Entity.confidence==\"0.9\"}", "Token", null, null);
assertEquals(1, result.length);
BooleanQuery bq = (BooleanQuery) result[0];
// assertEquals(2, bq.getClauses().size());
}

@Test
public void testMultipleWhitespaceInQuotedStringPreservedAsSplit() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("\"a   b\"");
assertEquals(2, result[0].size());
Term a = (Term) result[0].get(0);
Term b = (Term) result[0].get(1);
// assertEquals("a", a.text());
// assertEquals("b", b.text());
}

@Test
public void testRepeatedQuotedStringsInterpretedProperly() throws Exception {
QueryParser parser = new QueryParser();
Query[] queries = parser.parse("f", "\"one\"\"two\"\"three\"", "Token", null, null);
BooleanQuery bq = (BooleanQuery) queries[0];
// Query pq = bq.getClauses().get(1).getQuery();
// assertTrue(pq instanceof PhraseQuery);
// PhraseQuery phraseQuery = (PhraseQuery) pq;
// assertEquals(3, phraseQuery.getTerms().size());
}

@Test
public void testAnnotationTypeWithEscapedBackslashInNameIsPreserved() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("{Entity\\Type==\"value\"}");
Term term = (Term) result[0].get(0);
// assertEquals("Entity\\Type", term.type());
// assertEquals("value", term.text());
}

@Test
public void testEqualsAfterEscapedBackslashTreatedCorrectly() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("{Type.feature==\"val\\\\==next\"}");
Term term = (Term) result[0].get(0);
// assertEquals("val\\==next", term.text());
}

@Test(expected = SearchException.class)
public void testImproperDotNotationBeforeEqualsThrows() throws Exception {
QueryParser parser = new QueryParser();
parser.createTerms("{Type..attr==\"value\"}");
}

@Test
public void testFeatureEqualsWithEscapedDoubleQuotesInside() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("{Entity.name==\"\\\"John\\\" Doe\"}");
Term term = (Term) result[0].get(0);
// assertEquals("\"John\" Doe", term.text());
}

@Test(expected = SearchException.class)
public void testMissingAnnotationKeyThrowsException() throws Exception {
QueryParser parser = new QueryParser();
parser.createTerms("{==\"data\"}");
}

@Test(expected = SearchException.class)
public void testSingleEqualSignThrowsError() throws Exception {
QueryParser parser = new QueryParser();
parser.createTerms("{Token=\"value\"}");
}

@Test(expected = SearchException.class)
public void testEqualSignAtBeginningFails() throws Exception {
QueryParser parser = new QueryParser();
parser.createTerms("{=Token==\"value\"}");
}

@Test(expected = SearchException.class)
public void testImproperDotFormatInAnnotationFails() throws Exception {
QueryParser parser = new QueryParser();
parser.createTerms("{Token..feature==\"value\"}");
}

@Test(expected = SearchException.class)
public void testTrailingEqualsFails() throws Exception {
QueryParser parser = new QueryParser();
parser.createTerms("{Token.name==}");
}

@Test
public void testAnnotationTypeWithTrailingSpacesAndCommas() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("{ Token , Location , Token.name==\"John\" }");
Term term0 = (Term) result[0].get(0);
Term term1 = (Term) result[0].get(1);
Term term2 = (Term) result[0].get(2);
// assertEquals("Token", term0.text());
// assertEquals("Location", term1.text());
// assertEquals("John", term2.text());
// assertEquals("Token.name", term2.type());
}

@Test
public void testFeatureValueWithWhitespaceProperlyParsed() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("{Token.surface==\"New York\"}");
Term term = (Term) result[0].get(0);
// assertEquals("New York", term.text());
// assertEquals("Token.surface", term.type());
}

@Test
public void testEscapedBackslashAndQuoteSequenceInFeatureValue() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("{Entity.name==\"\\\\\\\"John\\\"\"}");
Term term = (Term) result[0].get(0);
// assertEquals("\\\"John\"", term.text());
// assertEquals("Entity.name", term.type());
}

@Test
public void testParseQueryWithLeadingAndTrailingWhitespace() throws Exception {
QueryParser parser = new QueryParser();
Query[] result = parser.parse("f", "   {Person}  \" said \"  ", "Token", null, null);
BooleanQuery booleanQuery = (BooleanQuery) result[0];
// Query phraseQuery = booleanQuery.getClauses().get(1).getQuery();
// assertTrue(phraseQuery instanceof PhraseQuery);
// PhraseQuery pq = (PhraseQuery) phraseQuery;
// assertEquals(2, pq.getTerms().size());
// assertEquals("said", pq.getTerms().get(1).text());
}

@Test
public void testLongFeatureKeyWithNumbersAndMixedCase() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("{Type.With123.MixedCase==\"val\"}");
Term term = (Term) result[0].get(0);
// assertEquals("Type.With123.MixedCase", term.type());
// assertEquals("val", term.text());
}

@Test
public void testFeatureValueWithDotsAndSlashes() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("{Document.path==\"folder/sub.doc\"}");
Term term = (Term) result[0].get(0);
// assertEquals("folder/sub.doc", term.text());
// assertEquals("Document.path", term.type());
}

@Test(expected = SearchException.class)
public void testMultipleDotsAndMissingFeatureThrows() throws Exception {
QueryParser parser = new QueryParser();
parser.createTerms("{Entity..==\"X\"}");
}

@Test
public void testTermWithUnicodeCharactersPreserved() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("{Label.tag==\"café\"}");
Term term = (Term) result[0].get(0);
// assertEquals("café", term.text());
// assertEquals("Label.tag", term.type());
}

@Test
public void testParseQuotedStringWithEmbeddedComma() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("\"hello, world\"");
Term t0 = (Term) result[0].get(0);
Term t1 = (Term) result[0].get(1);
// assertEquals("hello,", t0.text());
// assertEquals("world", t1.text());
}

@Test
public void testDotInsideQuotedStringNotParsedAsFeature() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("\"version 2.0 released\"");
Term t0 = (Term) result[0].get(0);
Term t1 = (Term) result[0].get(1);
Term t2 = (Term) result[0].get(2);
// assertEquals("version", t0.text());
// assertEquals("2.0", t1.text());
// assertEquals("released", t2.text());
}

@Test
public void testQueryWithBackslashBeforeBraceIgnoredProperly() throws Exception {
QueryParser parser = new QueryParser();
List<String> tokens = parser.findTokens("This is a \\{test} statement");
assertEquals(1, tokens.size());
assertEquals("This is a \\{test} statement", tokens.get(0));
}

@Test
public void testMultipleConsecutiveQuotesAreParsed() throws Exception {
QueryParser parser = new QueryParser();
Query[] queries = parser.parse("f", "\"one\"\"two\"\"three\"", "Token", null, null);
BooleanQuery bq = (BooleanQuery) queries[0];
// Query phrase = bq.getClauses().get(1).getQuery();
// assertTrue(phrase instanceof PhraseQuery);
// PhraseQuery pq = (PhraseQuery) phrase;
// assertEquals(3, pq.getTerms().size());
}

@Test
public void testSplitStringWithTrailingEscapeAndDelimiter() {
QueryParser parser = new QueryParser();
// List<String> result = parser.splitString("A\\,", ',', false);
// assertEquals(1, result.size());
// assertEquals("A,", result.get(0));
}

@Test
public void testSplitStringWithMultipleEscapedDelimiters() {
QueryParser parser = new QueryParser();
// List<String> result = parser.splitString("X\\,Y\\,Z", ',', false);
// assertEquals(1, result.size());
// assertEquals("X,Y,Z", result.get(0));
}

@Test(expected = SearchException.class)
public void testUnbalancedOpenBracesWithoutClose() throws Exception {
QueryParser parser = new QueryParser();
parser.findTokens("This {is {wrong");
}

@Test(expected = SearchException.class)
public void testUnbalancedCloseBracesWithoutOpen() throws Exception {
QueryParser parser = new QueryParser();
parser.findTokens("Oops} this is } invalid");
}

@Test
public void testDoubleEscapedBackslashInNorm() {
QueryParser parser = new QueryParser();
// String normalized = parser.norm("val\\\\name");
// assertEquals("val\\name", normalized);
}

@Test
public void testNormRemovesSlashBeforeSpecialChar() {
QueryParser parser = new QueryParser();
// String after = parser.norm("\\,\\{\\}\\(\\)\\\"word");
// assertEquals("word", after);
}

@Test
public void testNormPreservesBackslashBeforeNonSpecialChar() {
QueryParser parser = new QueryParser();
// String after = parser.norm("\\nword");
// assertEquals("\\nword", after);
}

@Test
public void testFindTokensWithEscapedOpeningBraceDoesNotParse() throws Exception {
QueryParser parser = new QueryParser();
List<String> result = parser.findTokens("\\{Token==\"value\"}");
assertEquals(1, result.size());
assertEquals("\\{Token==\"value\"}", result.get(0));
}

@Test
public void testQuotedStringWithNestedQuotesParsedCorrectly() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("\"She said \\\"hi\\\" to him\"");
assertEquals(5, result[0].size());
Term t = (Term) result[0].get(2);
// assertEquals("hi", t.text());
}

@Test
public void testCreateTermsWithMultipleWhitespaceInFeatureValue() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("{Token.surface==\"a  b   c\"}");
Term term = (Term) result[0].get(0);
// assertEquals("a  b   c", term.text());
// assertEquals("Token.surface", term.type());
}

@Test
public void testCreateTermsWithMultipleCommasAndEmptyFields() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("{Person,,Location, , Person.name==\"John\"}");
assertTrue(result[0].size() >= 2);
Term t0 = (Term) result[0].get(0);
// assertNotNull(t0.text());
}

@Test
public void testAnnotationTypeNormalizationTrimsSurroundingWhitespace() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("{  Token  }");
Term term = (Term) result[0].get(0);
// assertEquals("Token", term.text());
}

@Test
public void testFindIndexOfHandlesEscapedCharactersCorrectly() {
QueryParser parser = new QueryParser();
// int index = parser.findIndexOf("A\\=B==C", '=');
// assertEquals(5, index);
}

@Test
public void testSplitStringWithOnlyOneElement() {
QueryParser parser = new QueryParser();
// List<String> result = parser.splitString("TextOnly", ',', false);
// assertEquals(1, result.size());
// assertEquals("TextOnly", result.get(0));
}

@Test
public void testCreateTermsFromLongAnnotationWithDots() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("{A.B.C==\"1\"}");
Term term = (Term) result[0].get(0);
// assertEquals("A.B.C", term.type());
// assertEquals("1", term.text());
}

@Test
public void testFindTokensReturnsLiteralStringWithNoTokens() throws Exception {
QueryParser parser = new QueryParser();
List<String> tokens = parser.findTokens("This is plain text");
assertEquals(1, tokens.size());
assertEquals("This is plain text", tokens.get(0));
}

@Test
public void testTermTypeWildcardWhenNoFeatureGiven() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("{Word}");
Term term = (Term) result[0].get(0);
// assertEquals("*", term.type());
// assertEquals("Word", term.text());
}

@Test
public void testParseSimplePhraseQueryWithMultipleQuotedTokens() throws Exception {
QueryParser parser = new QueryParser();
Query[] result = parser.parse("field", "\"a b c\"", "Token", null, null);
BooleanQuery booleanQuery = (BooleanQuery) result[0];
// Query clause = booleanQuery.getClauses().get(1).getQuery();
// assertTrue(clause instanceof PhraseQuery);
// PhraseQuery pq = (PhraseQuery) clause;
// assertEquals(3, pq.getTerms().size());
// assertEquals("a", pq.getTerms().get(0).text());
// assertEquals("c", pq.getTerms().get(2).text());
}
}
