package gate.creole.annic.lucene;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import gate.creole.annic.apache.lucene.index.Term;
import gate.creole.annic.apache.lucene.search.BooleanQuery;
import gate.creole.annic.apache.lucene.search.PhraseQuery;
import gate.creole.annic.apache.lucene.search.Query;
import gate.creole.ir.SearchException;

public class QueryParserPromptTest {

  @Test
  public void findTokensSeparatesTextAndAnnotationExpressions() throws Exception {
    QueryParser parser = new QueryParser();

    List<String> tokens = parser.findTokens("  John Smith {Person.gender==\"male\"} said hello {Token}  ");

    assertEquals(4, tokens.size());
    assertEquals("John Smith", tokens.get(0));
    assertEquals("{Person.gender==\"male\"}", tokens.get(1));
    assertEquals("said hello", tokens.get(2));
    assertEquals("{Token}", tokens.get(3));
  }

  @Test
  public void findTokensKeepsEscapedBracesAsLiteralText() throws Exception {
    QueryParser parser = new QueryParser();

    List<String> tokens = parser.findTokens("before \\{literal\\} after {Token}");

    assertEquals(2, tokens.size());
    assertEquals("before \\{literal\\} after", tokens.get(0));
    assertEquals("{Token}", tokens.get(1));
  }

  @Test
  public void findTokensReturnsOneUnbracedTokenWithoutTrimmingItsEnd() throws Exception {
    QueryParser parser = new QueryParser();

    List<String> tokens = parser.findTokens("  two words  ");

    assertEquals(1, tokens.size());
    assertEquals("  two words  ", tokens.get(0));
  }

  @Test
  public void findTokensRejectsNestedOpeningBrace() throws Exception {
    QueryParser parser = new QueryParser();

    try {
      parser.findTokens("{Token{Lookup}}");
      fail("Expected SearchException");
    } catch(SearchException expected) {
      assertTrue(expected.getMessage().contains("unbalanced braces"));
      assertTrue(expected.getMessage().contains("closing brace"));
      assertTrue(expected.getMessage().endsWith("      ^"));
    }
  }

  @Test
  public void findTokensRejectsMissingClosingBrace() throws Exception {
    QueryParser parser = new QueryParser();

    try {
      parser.findTokens("word {Token");
      fail("Expected SearchException");
    } catch(SearchException expected) {
      assertTrue(expected.getMessage().contains("One closing brace"));
      assertTrue(expected.getMessage().contains("word {Token"));
    }
  }

  @Test
  public void findTokensRejectsMissingOpeningBrace() throws Exception {
    QueryParser parser = new QueryParser();

    try {
      parser.findTokens("Token}");
      fail("Expected SearchException");
    } catch(SearchException expected) {
      assertTrue(expected.getMessage().contains("opening brace"));
      assertTrue(expected.getMessage().endsWith("     ^"));
    }
  }

  @Test
  public void createTermsBuildsBaseTokenAnnotationTerm() throws Exception {
    QueryParser parser = new QueryParser();

    List<?>[] result = parser.createTerms(" {Token} ");
    List<?> terms = result[0];
    List<?> positions = result[1];
    List<?> considered = result[2];
    Term term = (Term)terms.get(0);

    assertEquals(1, terms.size());
    assertEquals("", term.field());
    assertEquals("Token", term.text());
    assertEquals("*", term.type());
    assertEquals(Integer.valueOf(0), positions.get(0));
    assertEquals(Boolean.TRUE, considered.get(0));
  }

  @Test
  public void createTermsBuildsQuotedAnnotationStringTerm() throws Exception {
    QueryParser parser = new QueryParser();

    List<?>[] result = parser.createTerms("{Token==\"New York\"}");
    Term term = (Term)result[0].get(0);

    assertEquals("New York", term.text());
    assertEquals("Token.string", term.type());
    assertEquals(Integer.valueOf(0), result[1].get(0));
    assertEquals(Boolean.TRUE, result[2].get(0));
  }

  @Test
  public void createTermsBuildsFeatureTermAndRemovesEscapes() throws Exception {
    QueryParser parser = new QueryParser();

    List<?>[] result = parser.createTerms("{Token.kind==\"proper\\,noun\"}");
    Term term = (Term)result[0].get(0);

    assertEquals("proper,noun", term.text());
    assertEquals("Token.kind", term.type());
    assertEquals(Integer.valueOf(0), result[1].get(0));
  }

  @Test
  public void createTermsBuildsSeveralAlternativesAtOnePosition() throws Exception {
    QueryParser parser = new QueryParser();

    List<?>[] result = parser.createTerms("{Lookup, Token.kind==word, Token==\"surface\"}");
    Term first = (Term)result[0].get(0);
    Term second = (Term)result[0].get(1);
    Term third = (Term)result[0].get(2);

    assertEquals(3, result[0].size());
    assertEquals("Lookup", first.text());
    assertEquals("*", first.type());
    assertEquals("word", second.text());
    assertEquals("Token.kind", second.type());
    assertEquals("surface", third.text());
    assertEquals("Token.string", third.type());
    assertEquals(Integer.valueOf(0), result[1].get(0));
    assertEquals(Integer.valueOf(0), result[1].get(1));
    assertEquals(Integer.valueOf(0), result[1].get(2));
    assertEquals(Boolean.TRUE, result[2].get(0));
    assertEquals(Boolean.FALSE, result[2].get(1));
    assertEquals(Boolean.FALSE, result[2].get(2));
  }

  @Test
  public void createTermsTreatsEscapedCommaAsPartOfAnnotationName() throws Exception {
    QueryParser parser = new QueryParser();

    List<?>[] result = parser.createTerms("{Name\\,WithComma,Token}");
    Term first = (Term)result[0].get(0);
    Term second = (Term)result[0].get(1);

    assertEquals(2, result[0].size());
    assertEquals("Name,WithComma", first.text());
    assertEquals("Token", second.text());
  }

  @Test
  public void createTermsSplitsUnbracedTextAndAdvancesPositions() throws Exception {
    QueryParser parser = new QueryParser();

    List<?>[] result = parser.createTerms("  \"hello world\"  again ");
    Term first = (Term)result[0].get(0);
    Term second = (Term)result[0].get(1);
    Term third = (Term)result[0].get(2);

    assertEquals(3, result[0].size());
    assertEquals("hello", first.text());
    assertEquals("world", second.text());
    assertEquals("again", third.text());
    assertEquals("Token.string", first.type());
    assertEquals(Integer.valueOf(0), result[1].get(0));
    assertEquals(Integer.valueOf(1), result[1].get(1));
    assertEquals(Integer.valueOf(2), result[1].get(2));
    assertEquals(Boolean.TRUE, result[2].get(0));
    assertEquals(Boolean.TRUE, result[2].get(1));
    assertEquals(Boolean.TRUE, result[2].get(2));
  }

  @Test
  public void createTermsMaintainsPositionAcrossCalls() throws Exception {
    QueryParser parser = new QueryParser();

    List<?>[] first = parser.createTerms("one two");
    List<?>[] second = parser.createTerms("{Token.kind==word,Token}");
    List<?>[] third = parser.createTerms("three");

    assertEquals(Integer.valueOf(0), first[1].get(0));
    assertEquals(Integer.valueOf(1), first[1].get(1));
    assertEquals(Integer.valueOf(2), second[1].get(0));
    assertEquals(Integer.valueOf(2), second[1].get(1));
    assertEquals(Integer.valueOf(3), third[1].get(0));
  }

  @Test
  public void createTermsRejectsFeatureWithoutDoubleEquals() throws Exception {
    QueryParser parser = new QueryParser();

    try {
      parser.createTerms("{Token.kind=word}");
      fail("Expected SearchException");
    } catch(SearchException expected) {
      assertTrue(expected.getMessage().contains("missing operator"));
      assertTrue(expected.getMessage().contains("equal operator (==)"));
      assertTrue(expected.getMessage().contains("Token.kind=word"));
    }
  }

  @Test
  public void parseSingleWordCreatesFilteredBooleanTermQuery() throws Exception {
    QueryParser parser = new QueryParser();

    Query[] queries = parser.parse("contents", "hello", "Token", null, null);

    assertEquals(1, queries.length);
    assertTrue(queries[0] instanceof BooleanQuery);
    assertEquals(2, ((BooleanQuery)queries[0]).getClauses().length);
    assertEquals("-ANNOTATION_SET_ID:Combined sets +contents:hello", queries[0].toString());
    assertFalse(parser.needValidation());
  }

  @Test
  public void parsePhraseCreatesPhraseQueryAndUsesCustomBaseTokenType() throws Exception {
    QueryParser parser = new QueryParser();

    Query[] queries = parser.parse("body", "alpha beta {Word.lemma==\"gamma\"}", "Word", null, null);

    assertEquals(1, queries.length);
    assertEquals("-ANNOTATION_SET_ID:Combined sets +body:\"alpha beta gamma\"", queries[0].toString());
    assertFalse(parser.needValidation());
  }

  @Test
  public void parseNonBaseAnnotationRequiresValidation() throws Exception {
    QueryParser parser = new QueryParser();

    Query[] queries = parser.parse("contents", "{Person.gender==male}", "Token", null, null);

    assertEquals("-ANNOTATION_SET_ID:Combined sets +contents:male", queries[0].toString());
    assertTrue(parser.needValidation());
  }

  @Test
  public void parseStopsPhraseAfterNonBaseAnnotation() throws Exception {
    QueryParser parser = new QueryParser();

    Query[] queries = parser.parse("contents", "one {Person} two", "Token", null, null);

    assertEquals("-ANNOTATION_SET_ID:Combined sets +contents:\"one\"", queries[0].toString());
    assertTrue(parser.needValidation());
  }

  @Test
  public void parseAddsCorpusAndDefaultAnnotationSetExclusion() throws Exception {
    QueryParser parser = new QueryParser();

    Query[] queries = parser.parse("contents", "word", "Token", "corpus-7", null);

    assertEquals(3, ((BooleanQuery)queries[0]).getClauses().length);
    assertEquals("+contents:word +CORPUS_ID:corpus-7 -ANNOTATION_SET_ID:Combined sets", queries[0].toString());
  }

  @Test
  public void parseAddsExplicitAnnotationSetWithoutDefaultExclusion() throws Exception {
    QueryParser parser = new QueryParser();

    Query[] queries = parser.parse("contents", "word", "Token", null, "KeySet");

    assertEquals(2, ((BooleanQuery)queries[0]).getClauses().length);
    assertEquals("+contents:word +ANNOTATION_SET_ID:KeySet", queries[0].toString());
  }

  @Test
  public void parseAddsBothCorpusAndExplicitAnnotationSet() throws Exception {
    QueryParser parser = new QueryParser();

    Query[] queries = parser.parse("contents", "word", "Token", "corpus-9", "Original markups");

    assertEquals(3, ((BooleanQuery)queries[0]).getClauses().length);
    assertEquals("+contents:word +CORPUS_ID:corpus-9 +ANNOTATION_SET_ID:Original markups", queries[0].toString());
  }

  @Test
  public void parseNormalizesAlternationIntoMultipleQueries() throws Exception {
    QueryParser parser = new QueryParser();

    Query[] queries = parser.parse("contents", "{Token}({Token==a}|{Token==b}){Token==c}", "Token", null, null);

    assertEquals(2, queries.length);
    assertEquals("-ANNOTATION_SET_ID:Combined sets +contents:\"Token b c\"", queries[0].toString());
    assertEquals("-ANNOTATION_SET_ID:Combined sets +contents:\"Token a c\"", queries[1].toString());
    assertTrue(parser.getQueryString(0).contains("{Token==b}"));
    assertTrue(parser.getQueryString(1).contains("{Token==a}"));
  }

  @Test
  public void parseExpandsBoundedPlusWildcard() throws Exception {
    QueryParser parser = new QueryParser();

    Query[] queries = parser.parse("contents", "({Token==x})+2", "Token", null, null);

    assertEquals(2, queries.length);
    assertEquals("-ANNOTATION_SET_ID:Combined sets +contents:\"x x\"", queries[0].toString());
    assertEquals("-ANNOTATION_SET_ID:Combined sets +contents:x", queries[1].toString());
  }

  @Test
  public void parseResetsFieldBaseTypePositionAndNormalizedQueriesWhenReused() throws Exception {
    QueryParser parser = new QueryParser();

    Query[] first = parser.parse("first", "a b", "Token", null, null);
    Query[] second = parser.parse("second", "{Word} tail", "Word", null, "Set2");

    assertEquals("-ANNOTATION_SET_ID:Combined sets +first:\"a b\"", first[0].toString());
    assertEquals(1, second.length);
    assertEquals("+second:\"Word tail\" +ANNOTATION_SET_ID:Set2", second[0].toString());
    assertEquals("{Word} tail", parser.getQueryString(0));
    assertFalse(parser.needValidation());
  }

  @Test
  public void parseReturnsPhraseTermsWithExpectedPublicMetadata() throws Exception {
    QueryParser parser = new QueryParser();

    Query[] wrapped = parser.parse("body", "one two", "Token", null, null);
    List<?>[] direct = parser.createTerms("{Token.kind==word}");
    Term directTerm = (Term)direct[0].get(0);

    assertNotNull(wrapped[0]);
    assertEquals("body", directTerm.field());
    assertEquals("word", directTerm.text());
    assertEquals("Token.kind", directTerm.type());
  }

  @Test
  public void isValidQueryAcceptsRepresentativeValidSyntax() {
    assertTrue(QueryParser.isValidQuery("{Token} ({Token.kind==word}|\"hello world\")"));
  }

  @Test
  public void isValidQueryRejectsMalformedBraceAndBracketSyntax() {
    assertFalse(QueryParser.isValidQuery("{Token"));
    assertFalse(QueryParser.isValidQuery("({Token}|{Lookup}"));
  }

  @Test
  public void parseRejectsUnbalancedParentheses() throws Exception {
    QueryParser parser = new QueryParser();

    try {
      parser.parse("contents", "({Token}|{Lookup}", "Token", null, null);
      fail("Expected SearchException");
    } catch(SearchException expected) {
      assertTrue(expected.getMessage().contains("unbalanced brackets"));
      assertTrue(expected.getMessage().contains("closing bracket"));
    }
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void getQueryStringRejectsIndexBeyondNormalizedQueries() throws Exception {
    QueryParser parser = new QueryParser();
    parser.parse("contents", "word", "Token", null, null);

    parser.getQueryString(1);
  }

  @Test
  public void phraseObjectExposesAllTermsFromDirectConstructionScenario() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] built = parser.createTerms("a b");
    PhraseQuery phrase = new PhraseQuery();
    phrase.add((Term)built[0].get(0), (Integer)built[1].get(0), (Boolean)built[2].get(0));
    phrase.add((Term)built[0].get(1), (Integer)built[1].get(1), (Boolean)built[2].get(1));

    assertArrayEquals(new String[] {"a", "b"},
        new String[] {phrase.getTerms()[0].text(), phrase.getTerms()[1].text()});
    assertEquals(0, phrase.getSlop());
  }
}
