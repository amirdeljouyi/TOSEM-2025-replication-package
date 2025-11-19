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

public class QueryParser_5_GPTLLMTest {

 @Test
  public void testIsValidQueryWithSimpleAnnotation() {
    boolean valid = QueryParser.isValidQuery("{Token}");
    assertTrue(valid);
  }
@Test
  public void testIsValidQueryWithQuotedString() {
    boolean valid = QueryParser.isValidQuery("\"hello world\"");
    assertTrue(valid);
  }
@Test
  public void testIsValidQueryWithFeatureEquality() {
    boolean valid = QueryParser.isValidQuery("{Person.gender==\"male\"}");
    assertTrue(valid);
  }
@Test
  public void testIsValidQueryWithMultipleComponents() {
    boolean valid = QueryParser.isValidQuery("{Token}\"hello\"{Person.gender==\"male\"}");
    assertTrue(valid);
  }
@Test
  public void testIsValidQueryFailsOnMissingEquality() {
    boolean valid = QueryParser.isValidQuery("{Person.gender=\"male\"}");
    assertFalse(valid);
  }
@Test
  public void testIsValidQueryFailsOnDoubleOpenBrace() {
    boolean valid = QueryParser.isValidQuery("{Token{");
    assertFalse(valid);
  }

  @Test
  public void testIsValidQueryFailsOnCloseBraceOnly() {
    boolean valid = QueryParser.isValidQuery("Token}");
    assertFalse(valid);
  }

  @Test
  public void testParseSimpleAnnotationQuery() throws Exception {
    QueryParser parser = new QueryParser();
    Query[] queries = parser.parse("contents", "{Token}", "Token", null, null);
    assertNotNull(queries);
    assertEquals(1, queries.length);
  }

  @Test
  public void testParseAnnotatedQueryWithCorpusAndSet() throws Exception {
    QueryParser parser = new QueryParser();
    Query[] queries = parser.parse("contents", "{Lookup}", "Token", "myCorpus", "mySet");
    assertNotNull(queries);
    assertEquals(1, queries.length);
    String queryStr = queries[0].toString();
    assertTrue(queryStr.contains("myCorpus"));
    assertTrue(queryStr.contains("mySet"));
  }

  @Test
  public void testGetQueryStringReturnsCorrectNormalizedQuery() throws Exception {
    QueryParser parser = new QueryParser();
    parser.parse("contents", "{Token}", "Token", null, null);
    String result = parser.getQueryString(0);
    assertEquals("{Token}", result);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testGetQueryStringThrowsOnInvalidIndex() {
    QueryParser parser = new QueryParser();
    parser.getQueryString(5);
  }

  @Test
  public void testCreateTermsWithSingleAnnotation() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] parts = parser.createTerms("{Token}");
    assertNotNull(parts);
    assertEquals(1, parts[0].size());
    assertEquals(1, parts[1].size());
    assertEquals(1, parts[2].size());
  }

  @Test
  public void testCreateTermsWithQuotedPhrase() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] parts = parser.createTerms("\"foo bar\"");
    assertNotNull(parts);
    assertEquals(2, parts[0].size());
    assertEquals(2, parts[1].size());
    assertEquals(2, parts[2].size());
  }

  @Test
  public void testCreateTermsWithAnnotationFeatureEquals() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] parts = parser.createTerms("{Person.gender==\"male\"}");
    assertNotNull(parts);
    assertEquals(1, parts[0].size());
  }

  @Test
  public void testCreateTermsWithCommaSeparatedAnnotations() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] parts = parser.createTerms("{Token, Lookup}");
    assertNotNull(parts);
    assertEquals(2, parts[0].size());
  }

  @Test
  public void testCreateTermsMixedTypeFeatureEquality() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] parts = parser.createTerms("{Person, Person.gender==\"male\"}");
    assertNotNull(parts);
    assertEquals(2, parts[0].size());
  }

  @Test(expected = SearchException.class)
  public void testMalformedAnnotationThrowsMissingEqualSign() throws Exception {
    QueryParser parser = new QueryParser();
    parser.createTerms("{Person.gender=\"male\"}");
  }

  @Test
  public void testFindTokensWithMixedAnnotationsAndStrings() throws Exception {
    QueryParser parser = new QueryParser();
    List<String> tokens = parser.findTokens("{Token}\"said\"{Person.gender==\"male\"}");
    assertNotNull(tokens);
    assertEquals("{Token}", tokens.get(0));
    assertEquals("\"said\"", tokens.get(1));
    assertEquals("{Person.gender==\"male\"}", tokens.get(2));
  }

  @Test(expected = SearchException.class)
  public void testFindTokensFailsWithUnbalancedOpenBrace() throws Exception {
    QueryParser parser = new QueryParser();
    parser.findTokens("{Token\"missing brace");
  }

  @Test(expected = SearchException.class)
  public void testFindTokensFailsWithExcessClosingBrace() throws Exception {
    QueryParser parser = new QueryParser();
    parser.findTokens("Token}\"extra brace\"");
  }

  @Test
  public void testNeedValidationIsTrueForLookupAndFeatureQuery() throws Exception {
    QueryParser parser = new QueryParser();
    parser.parse("contents", "{Lookup}{Person.gender==\"male\"}", "Token", null, null);
    boolean result = parser.needValidation();
    assertTrue(result);
  }

  @Test
  public void testNeedValidationIsFalseForBasicTokenQuery() throws Exception {
    QueryParser parser = new QueryParser();
    parser.parse("contents", "{Token}", "Token", null, null);
    boolean result = parser.needValidation();
    assertFalse(result);
  }

  @Test
  public void testParseQueryWithMultipleMixedTokens() throws Exception {
    QueryParser parser = new QueryParser();
    String inputQuery = "{Person}\"said\"{Lookup}{Token.gender==\"female\"}";
    Query[] queries = parser.parse("contents", inputQuery, "Token", "corpusX", "annSetY");
    assertNotNull(queries);
    assertEquals(1, queries.length);
    String str = queries[0].toString();
    assertTrue(str.contains("corpusX"));
    assertTrue(str.contains("annSetY"));
  }

  @Test
  public void testSplitStringReturnsCorrectlyEscapedStrings() {
    QueryParser parser = new QueryParser();
    List<String> result = parser.splitString("A,B\\,C,D", ',', true);
    assertEquals("A", result.get(0));
    assertEquals("B,C", result.get(1));
    assertEquals("D", result.get(2));
  }

  @Test
  public void testNormRemovesEscapedSpecialCharacters() {
    QueryParser parser = new QueryParser();
    String raw = "\\\"He\\\" said \\\\{run\\\\}!";
    String normed = parser.norm(raw);
    assertEquals("He said {run}!", normed);
  }

  @Test
  public void testIsEscapeSequenceTrueWhenPrecededByBackslash() {
    QueryParser parser = new QueryParser();
    boolean result = parser.isEscapeSequence("a\\,", 2);
    assertTrue(result);
  }

  @Test
  public void testIsEscapeSequenceFalseWithoutBackslash() {
    QueryParser parser = new QueryParser();
    boolean result = parser.isEscapeSequence("abc,", 3);
    assertFalse(result);
  }

  @Test
  public void testFindIndexOfSkipsEscapedCharacters() {
    QueryParser parser = new QueryParser();
    String input = "A\\,B,C";
    int index = parser.findIndexOf(input, ',');
    assertEquals(5, index);
  }

@Test
  public void testSplitStringReturnsCorrectlyEscapedStrings() {
    QueryParser parser = new QueryParser();
    List<String> result = parser.splitString("A,B\\,C,D", ',', true);
    assertEquals("A", result.get(0));
    assertEquals("B,C", result.get(1));
    assertEquals("D", result.get(2));
  }
@Test
  public void testNormRemovesEscapedSpecialCharacters() {
    QueryParser parser = new QueryParser();
    String raw = "\\\"He\\\" said \\\\{run\\\\}!";
    String normed = parser.norm(raw);
    assertEquals("He said {run}!", normed);
  }
@Test
  public void testIsEscapeSequenceTrueWhenPrecededByBackslash() {
    QueryParser parser = new QueryParser();
    boolean result = parser.isEscapeSequence("a\\,", 2);
    assertTrue(result);
  }
@Test
  public void testIsEscapeSequenceFalseWithoutBackslash() {
    QueryParser parser = new QueryParser();
    boolean result = parser.isEscapeSequence("abc,", 3);
    assertFalse(result);
  }
@Test
  public void testFindIndexOfSkipsEscapedCharacters() {
    QueryParser parser = new QueryParser();
    String input = "A\\,B,C";
    int index = parser.findIndexOf(input, ',');
    assertEquals(5, index);
  }
@Test
  public void testCreateTermsHandlesUnquotedStringTokens() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] termsData = parser.createTerms("plain text phrase");
    assertNotNull(termsData);
    assertEquals(3, termsData[0].size());
  }
@Test
  public void testCreateTermsHandlesEmptyStringToken() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] parts = parser.createTerms("\" \"");
    assertEquals(0, parts[0].size());
    assertEquals(0, parts[1].size());
    assertEquals(0, parts[2].size());
  }
@Test
  public void testCreateTermsHandlesUnclosedQuotes() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] parts = parser.createTerms("\"unclosed quote");
    assertEquals(2, parts[0].size());
  }
@Test
  public void testCreateTermsHandlesMultipleQuotedTokens() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] parts = parser.createTerms("\"A B C\"");
    assertEquals(3, parts[0].size());
  }
@Test(expected = SearchException.class)
  public void testCreateTermsThrowsOnUnbalancedMixedEqualsAndDot() throws Exception {
    QueryParser parser = new QueryParser();
    parser.createTerms("{Person.gender=\"male\"}");
  }
@Test
  public void testCreateTermsHandlesOnlyEscapedCharacters() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] parts = parser.createTerms("\"\\, \\\\ \\\"\"");
    assertEquals(3, parts[0].size());
  }
@Test
  public void testSplitStringHandlesDelimiterAtStart() {
    QueryParser parser = new QueryParser();
    List<String> result = parser.splitString(",x,y", ',', true);
    assertEquals("", result.get(0));
    assertEquals("x", result.get(1));
    assertEquals("y", result.get(2));
  }
@Test
  public void testSplitStringHandlesTrailingDelimiter() {
    QueryParser parser = new QueryParser();
    List<String> result = parser.splitString("a,b,", ',', true);
    assertEquals("a", result.get(0));
    assertEquals("b", result.get(1));
    assertEquals("", result.get(2));
  }
@Test
  public void testSplitStringHandlesDoubleDelimiter() {
    QueryParser parser = new QueryParser();
    List<String> result = parser.splitString("a,,b", ',', true);
    assertEquals("a", result.get(0));
    assertEquals("", result.get(1));
    assertEquals("b", result.get(2));
  }
@Test
  public void testNormKeepsUnescapedSpecialChars() {
    QueryParser parser = new QueryParser();
    String input = "keep_this_*()";
    String output = parser.norm(input);
    assertEquals("keep_this_*()", output);
  }
@Test
  public void testNormSkipsBackslashEscapeQuotesOnly() {
    QueryParser parser = new QueryParser();
    String input = "He said \\\"hi\\\".";
    String output = parser.norm(input);
    assertEquals("He said hi.", output);
  }
@Test
  public void testNormHandlesMultipleEscapePatterns() {
    QueryParser parser = new QueryParser();
    String input = "\\\\ \\\" \\. \\{ \\} \\(";
    String output = parser.norm(input);
    assertEquals(" ", output.trim());
  }
@Test
  public void testFindTokensHandlesRedundantWhitespaceBetweenTokens() throws Exception {
    QueryParser parser = new QueryParser();
    List<String> tokens = parser.findTokens("   {Token}   \"hello\" {Person.gender==\"male\"}  ");
    assertEquals(3, tokens.size());
    assertEquals("{Token}", tokens.get(0));
    assertEquals("\"hello\"", tokens.get(1));
    assertEquals("{Person.gender==\"male\"}", tokens.get(2));
  }
@Test
  public void testFindTokensHandlesTrailingTextAfterBrace() throws Exception {
    QueryParser parser = new QueryParser();
    List<String> tokens = parser.findTokens("{Token}extra");
    assertEquals(2, tokens.size());
    assertEquals("{Token}", tokens.get(0));
    assertEquals("extra", tokens.get(1));
  }
@Test(expected = SearchException.class)
  public void testFindTokensThrowsOnNestedUnbalancedBraces() throws Exception {
    QueryParser parser = new QueryParser();
    parser.findTokens("{Token{Type}");
  }
@Test
  public void testCreateTermsMultipleFeatureConstraintsSameBlock() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("{Token.string==\"hello\",Token.partOfSpeech==\"NN\"}");
    assertNotNull(result);
    assertEquals(2, result[0].size());
  }
@Test(expected = SearchException.class)
  public void testCreateTermsThrowsOnTripleEquals() throws Exception {
    QueryParser parser = new QueryParser();
    parser.createTerms("{Person.gender===\"male\"}");
  }
@Test(expected = SearchException.class)
  public void testCreateTermsThrowsOnSingleEquals() throws Exception {
    QueryParser parser = new QueryParser();
    parser.createTerms("{Person.gender=\"male\"}");
  }
@Test
  public void testCreateTermsHandlesEscapedQuotesInsideValue() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("{Person.name==\"Bob\\\"Smith\"}");
    assertNotNull(result);
    assertEquals(1, result[0].size());
  }
@Test
  public void testCreateTermsHandlesDoubleDotInFeature() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("{Token..string==\"value\"}");
    assertEquals(1, result[0].size());
  }
@Test
  public void testParseHandlesQuotedTermFollowedByAnnotation() throws Exception {
    QueryParser parser = new QueryParser();
    Query[] queries = parser.parse("contents", "\"Hello\"{Token}", "Token", null, null);
    assertNotNull(queries);
    assertEquals(1, queries.length);
    String qStr = queries[0].toString();
    assertTrue(qStr.contains("Hello"));
  }
@Test
  public void testFindTokensHandlesTrailingCharactersAfterAnnotation() throws Exception {
    QueryParser parser = new QueryParser();
    List<String> tokens = parser.findTokens("{Token}dummyText");
    assertEquals(2, tokens.size());
    assertEquals("{Token}", tokens.get(0));
    assertEquals("dummyText", tokens.get(1));
  }
@Test(expected = SearchException.class)
  public void testFindTokensThrowsOnEscapedOpeningBrace() throws Exception {
    QueryParser parser = new QueryParser();
    parser.findTokens("{Token}\\{Malformed}");
  }
@Test
  public void testNormAllowsBackslashBeforeNonSymbol() {
    QueryParser parser = new QueryParser();
    String value = parser.norm("example\\z");
    assertEquals("example\\z", value);
  }
@Test
  public void testNormSkipsBackslashForSpecialEscapedSymbols() {
    QueryParser parser = new QueryParser();
    String value = parser.norm("escaped\\,dot\\.");
    assertEquals("escapeddot.", value);
  }
@Test
  public void testSplitStringHandlesOnlyDelimiter() {
    QueryParser parser = new QueryParser();
    List<String> result = parser.splitString(",", ',', true);
    assertEquals("", result.get(0));
    assertEquals("", result.get(1));
  }
@Test
  public void testSplitStringReturnsOriginalWhenNoDelimiterExists() {
    QueryParser parser = new QueryParser();
    List<String> result = parser.splitString("NodeTextOnly", ',', false);
    assertEquals("NodeTextOnly", result.get(0));
  }
@Test
  public void testFindTokensHandlesEmptyString() throws Exception {
    QueryParser parser = new QueryParser();
    List<String> tokens = parser.findTokens("");
    assertNotNull(tokens);
    assertEquals(0, tokens.size());
  }
@Test
  public void testFindTokensHandlesOnlyWhitespace() throws Exception {
    QueryParser parser = new QueryParser();
    List<String> tokens = parser.findTokens("   \t   ");
    assertNotNull(tokens);
    assertEquals(0, tokens.size());
  }
@Test(expected = SearchException.class)
  public void testCreateTermsThrowsIfAnnotationBlockContainsOnlyDot() throws Exception {
    QueryParser parser = new QueryParser();
    parser.createTerms("{.}");
  }
@Test
  public void testFindTokensHandlesNewlineCharacters() throws Exception {
    QueryParser parser = new QueryParser();
    List<String> tokens = parser.findTokens("{Token}\n\"Hello\"\r{Person.gender==\"male\"}");
    assertEquals("{Token}", tokens.get(0));
    assertEquals("\"Hello\"", tokens.get(1));
    assertEquals("{Person.gender==\"male\"}", tokens.get(2));
  }
@Test
  public void testCreateTermsSupportsAnnotationWithMultipleDots() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("{Entity.feature.subFeature==\"val\"}");
    assertEquals(1, result[0].size());
  }
@Test
  public void testParseHandlesMultiSpaceBetweenTokens() throws Exception {
    QueryParser parser = new QueryParser();
    Query[] queries = parser.parse("contents", "\"John   Smith\"", "Token", null, null);
    assertEquals(1, queries.length);
    assertTrue(queries[0].toString().contains("John"));
  }
@Test(expected = SearchException.class)
  public void testMalformedNestedAnnotationBlockThrows() throws Exception {
    QueryParser parser = new QueryParser();
    parser.createTerms("{Token,{Person.gender==\"male\"}");
  }

@Test(expected = SearchException.class)
  public void testFindTokensWithBackToBackOpeningBracesThrows() throws Exception {
    QueryParser parser = new QueryParser();
    parser.findTokens("{Token{{Person}}");
  }

  @Test
  public void testCreateTermsHandlesEscapedQuoteInsideAnnotationValue() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("{Person.name==\"O\\\"Connor\"}");
    assertEquals(1, result[0].size());
  }

  @Test(expected = SearchException.class)
  public void testCreateTermsWithEqualsButNoValueThrows() throws Exception {
    QueryParser parser = new QueryParser();
    parser.createTerms("{Person.gender==}");
  }

  @Test
  public void testCreateTermsWithExcessiveSpacesStillParses() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("{     Person.gender   ==    \"male\"     }");
    assertEquals(1, result[0].size());
  }

  @Test(expected = SearchException.class)
  public void testCreateTermsWithOnlyEqualityOperatorThrows() throws Exception {
    QueryParser parser = new QueryParser();
    parser.createTerms("{==}");
  }

  @Test
  public void testCreateTermsWithLiteralEscapedBracesInQuote() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("\"This is a \\{test\\}\"");
    assertEquals(4, result[0].size());
  }

  @Test
  public void testCreateTermsWithDoubleEquality() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("{Type1==\"value1\",Type2==\"value2\"}");
    assertEquals(2, result[0].size());
  }

  @Test
  public void testCreateTermsHandlesDotOperatorWithoutFeatureValue() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("{Type.feature==  \"value\" }");
    assertEquals(1, result[0].size());
  }

  @Test(expected = SearchException.class)
  public void testCreateTermsThrowsIfTypeFeatureStructureIncomplete() throws Exception {
    QueryParser parser = new QueryParser();
    parser.createTerms("{Type.==\"value\"}");
  }

  @Test
  public void testNormHandlesMultipleTypesOfEscapesTogether() {
    QueryParser parser = new QueryParser();
    String input = "\\\"Hello\\\"\\(world\\)\\\\stuff\\,test";
    String result = parser.norm(input);
    assertEquals("Helloworldstufftest", result);
  }

  @Test
  public void testSplitStringWithConsecutiveSeparators() {
    QueryParser parser = new QueryParser();
    List<String> result = parser.splitString("A,,B,,C", ',', true);
    assertEquals("A", result.get(0));
    assertEquals("", result.get(1));
    assertEquals("B", result.get(2));
    assertEquals("", result.get(3));
    assertEquals("C", result.get(4));
  }

  @Test
  public void testSplitStringHandlesEmptyInput() {
    QueryParser parser = new QueryParser();
    List<String> result = parser.splitString("", ',', true);
    assertEquals(0, result.size());
  }

@Test
  public void testNormHandlesMultipleTypesOfEscapesTogether() {
    QueryParser parser = new QueryParser();
    String input = "\\\"Hello\\\"\\(world\\)\\\\stuff\\,test";
    String result = parser.norm(input);
    assertEquals("Helloworldstufftest", result);
  }
@Test
  public void testSplitStringWithConsecutiveSeparators() {
    QueryParser parser = new QueryParser();
    List<String> result = parser.splitString("A,,B,,C", ',', true);
    assertEquals("A", result.get(0));
    assertEquals("", result.get(1));
    assertEquals("B", result.get(2));
    assertEquals("", result.get(3));
    assertEquals("C", result.get(4));
  }
@Test
  public void testSplitStringHandlesEmptyInput() {
    QueryParser parser = new QueryParser();
    List<String> result = parser.splitString("", ',', true);
    assertEquals(0, result.size());
  }
@Test
  public void testCreateTermsWithMultiplePhrasesSeparatedBySpaces() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("\"This is a   test  string\"");
    assertEquals(5, result[0].size()); 
  }
@Test
  public void testFindTokensHandlesQuotesAndBracesMixed() throws Exception {
    QueryParser parser = new QueryParser();
    List<String> tokens = parser.findTokens("{Person}\"said\"\"hello\"{Token}");
    assertEquals(4, tokens.size());
    assertEquals("{Person}", tokens.get(0));
    assertEquals("\"said\"", tokens.get(1));
    assertEquals("\"hello\"", tokens.get(2));
    assertEquals("{Token}", tokens.get(3));
  }
@Test
  public void testCreateTermsWithMultipleTermsReturnsPhraseQuery() throws Exception {
    QueryParser parser = new QueryParser();
    Query[] queries = parser.parse("contents", "{Person}{Token}{Location}", "Token", null, null);
    assertEquals(1, queries.length);
    assertTrue(queries[0].toString().contains("Phrase"));
  }
@Test
  public void testParseSingleAnnotationBlockResultsInTermQuery() throws Exception {
    QueryParser parser = new QueryParser();
    Query[] queries = parser.parse("contents", "{Person}", "Token", null, null);
    assertEquals(1, queries.length);
    assertFalse(queries[0].toString().contains("Phrase"));
    assertTrue(queries[0].toString().contains("Person"));
  }
@Test
  public void testParseHandlesQuotesFollowedByUnquotedText() throws Exception {
    QueryParser parser = new QueryParser();
    Query[] queries = parser.parse("contents", "\"hello\"World", "Token", null, null);
    assertEquals(1, queries.length);
    String str = queries[0].toString();
    assertTrue(str.contains("hello"));
    assertTrue(str.contains("World"));
  }
@Test(expected = SearchException.class)
  public void testCreateTermsUnsupportedFormatJustDot() throws Exception {
    QueryParser parser = new QueryParser();
    parser.createTerms("{.string==value}");
  }
@Test
  public void testFindTokensWithTrailingEscapedQuote() throws Exception {
    QueryParser parser = new QueryParser();
    List<String> tokens = parser.findTokens("\"value with quote\\\"\"");
    assertEquals(1, tokens.size());
    assertEquals("\"value with quote\\\"\"", tokens.get(0));
  }
@Test
  public void testSingleCharacterQueryIsValid() {
    boolean valid = QueryParser.isValidQuery("a");
    assertTrue(valid);
  }
@Test
  public void testSingleBackslashQueryShouldBeValidLiteral() {
    boolean valid = QueryParser.isValidQuery("\\");
    assertTrue(valid);
  }
@Test(expected = SearchException.class)
  public void testCreateTermsWithUnclosedBraceThrows() throws Exception {
    QueryParser parser = new QueryParser();
    parser.createTerms("{Token");
  }

  @Test(expected = SearchException.class)
  public void testCreateTermsWithOnlyOperatorAndNoAnnotationThrows() throws Exception {
    QueryParser parser = new QueryParser();
    parser.createTerms("==");
  }

  @Test
  public void testCreateTermsWithEmptyFeatureTypeStillParsesWhenQuoted() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] terms = parser.createTerms("{Person==\"male\"}");
    assertEquals(1, terms[0].size());
  }

  @Test
  public void testCreateTermsWithUnsupportedMixedAnnotationListStillWorksForValidItems() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] terms = parser.createTerms("{Token,Invalid.feature}");
    assertEquals(2, terms[0].size());
  }

  @Test(expected = SearchException.class)
  public void testCreateTermsWithBrokenFeatureDotThrows() throws Exception {
    QueryParser parser = new QueryParser();
    parser.createTerms("{Token.==\"text\"}");
  }

  @Test
  public void testCreateTermsWithFeatureNameContainingHyphens() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] terms = parser.createTerms("{Person.gender-type==\"male\"}");
    assertEquals(1, terms[0].size());
  }

  @Test
  public void testCreateTermsWithFeatureValueContainingEscapedComma() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] terms = parser.createTerms("{Person.name==\"Last\\, First\"}");
    assertEquals(1, terms[0].size());
  }

  @Test
  public void testNormPreservesValidNonSpecialBackslashes() {
    QueryParser parser = new QueryParser();
    String original = "abc\\z";
    String result = parser.norm(original);
    assertEquals("abc\\z", result);
  }

  @Test
  public void testNormStripsOnlyRecognizedEscapedChars() {
    QueryParser parser = new QueryParser();
    String input = "\\,\\{\\}\\\"\\\\.data\\X";
    String cleaned = parser.norm(input);
    assertEquals("data\\X", cleaned);
  }

  @Test(expected = SearchException.class)
  public void testCreateTermsWithExcessEqualSignsThrows() throws Exception {
    QueryParser parser = new QueryParser();
    parser.createTerms("{Person.feature====\"true\"}");
  }

  @Test
  public void testCreateTermsWithFeatureQuotedButUnusuallyEscapedValue() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] terms = parser.createTerms("{Person.feature==\"\\\"yes\\\"\"}");
    assertEquals(1, terms[0].size());
  }

  @Test
  public void testCreateTermsReturnsCorrectNumberOfTermsAfterEscapedQuotesAndSpaces() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] terms = parser.createTerms("\"a \\\"quoted\\\" token here\"");
    assertEquals(4, terms[0].size());
  }

  @Test
  public void testFlagNeedValidationFalseForSingleBaseTokenAnnotation() throws Exception {
    QueryParser parser = new QueryParser();
    parser.parse("f", "{Token}", "Token", null, null);
    assertFalse(parser.needValidation());
  }

  @Test
  public void testFlagNeedValidationTrueForNonBaseTokenAnnotationStart() throws Exception {
    QueryParser parser = new QueryParser();
    parser.parse("f", "{Lookup}{Token}", "Token", null, null);
    assertTrue(parser.needValidation());
  }

  @Test
  public void testFindTokensAcceptsMultipleAdjacentQuotedStrings() throws Exception {
    QueryParser parser = new QueryParser();
    List<String> tokens = parser.findTokens("\"one\"\"two\"\"three\"");
    assertEquals(3, tokens.size());
    assertEquals("\"one\"", tokens.get(0));
    assertEquals("\"two\"", tokens.get(1));
    assertEquals("\"three\"", tokens.get(2));
  }

  @Test(expected = SearchException.class)
  public void testFindTokensFailsIfClosingBraceBeforeOpeningBrace() throws Exception {
    QueryParser parser = new QueryParser();
    parser.findTokens("}Token{");
  }

  @Test
  public void testCreateTermsMixedQuotedAndEscapedTokens() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("\"Run \\{ fast \\}\"");
    assertEquals(4, result[0].size());
  }

  @Test
  public void testFindTokensWithMisplacedBracesAndQuotesReturnsFullTokens() throws Exception {
    QueryParser parser = new QueryParser();
    List<String> result = parser.findTokens("{Token}\"Hello\"junk{Anno==\"text\"}");
    assertEquals(4, result.size());
    assertEquals("{Token}", result.get(0));
    assertEquals("\"Hello\"", result.get(1));
    assertEquals("junk", result.get(2));
    assertEquals("{Anno==\"text\"}", result.get(3));
  }

@Test
  public void testNormPreservesValidNonSpecialBackslashes() {
    QueryParser parser = new QueryParser();
    String original = "abc\\z";
    String result = parser.norm(original);
    assertEquals("abc\\z", result);
  }
@Test
  public void testNormStripsOnlyRecognizedEscapedChars() {
    QueryParser parser = new QueryParser();
    String input = "\\,\\{\\}\\\"\\\\.data\\X";
    String cleaned = parser.norm(input);
    assertEquals("data\\X", cleaned);
  }
@Test(expected = SearchException.class)
  public void testCreateTermsThrowsOnAnnotationWithOnlyDot() throws Exception {
    QueryParser parser = new QueryParser();
    parser.createTerms("{.}");
  }
@Test(expected = SearchException.class)
  public void testCreateTermsThrowsOnMalformedCommaAtEnd() throws Exception {
    QueryParser parser = new QueryParser();
    parser.createTerms("{Token,}");
  }
@Test(expected = SearchException.class)
  public void testCreateTermsThrowsOnDoubleComma() throws Exception {
    QueryParser parser = new QueryParser();
    parser.createTerms("{Token,,Person}");
  }
@Test
  public void testCreateTermsParsesMultipleValidFeatureConditions() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] terms = parser.createTerms("{Entity.type==\"Location\",Entity.role==\"Admin\"}");
    assertEquals(2, terms[0].size());
    assertEquals(2, terms[1].size());
    assertEquals(2, terms[2].size());
  }

@Test(expected = SearchException.class)
  public void testFindTokensThrowsOnUnclosedQuote() throws Exception {
    QueryParser parser = new QueryParser();
    parser.findTokens("\"Some text");
  }
@Test
  public void testFindTokensHandlesEscapedQuotedText() throws Exception {
    QueryParser parser = new QueryParser();
    List<String> tokens = parser.findTokens("\"He said \\\"Hello\\\"\"");
    assertEquals(1, tokens.size());
    assertEquals("\"He said \\\"Hello\\\"\"", tokens.get(0));
  }
@Test(expected = SearchException.class)
  public void testCreateTermsThrowsWhenDotWithoutFeatureName() throws Exception {
    QueryParser parser = new QueryParser();
    parser.createTerms("{Entity.==\"value\"}");
  }
@Test
  public void testCreateTermsHandlesMultipleNestedQuotesInFeatureValue() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("{Entity.role==\"\\\"User\\\"\"}");
    assertEquals(1, result[0].size());
  }
@Test
  public void testCreateTermsHandlesUnquotedValuesContainingSpaces() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("{Type==tag with space}");
    assertEquals(1, result[0].size());
  }
@Test
  public void testSplitStringHandlesMultipleDifferentEscapes() {
    QueryParser parser = new QueryParser();
    List<String> result = parser.splitString("A, B\\,C, D\\\\,E", ',', true);
    assertEquals("A", result.get(0));
    assertEquals("B,C", result.get(1));
    assertEquals("D\\\\", result.get(2));
    assertEquals("E", result.get(3));
  }
@Test
  public void testNormRemovesEscapedQuotesOnly() {
    QueryParser parser = new QueryParser();
    String text = "\\\"Hello\\\", World";
    String result = parser.norm(text);
    assertEquals("Hello, World", result);
  }
@Test
  public void testSplitStringReturnsSingleItemIfNoDelimiterPresent() {
    QueryParser parser = new QueryParser();
    List<String> strings = parser.splitString("NoDelimiterHere", ',', false);
    assertEquals(1, strings.size());
    assertEquals("NoDelimiterHere", strings.get(0));
  }
@Test
  public void testCreateTermsHandlesSpecialCharactersAsValues() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("{Rule==\"\\(.*\\) pattern\"}");
    assertEquals(1, result[0].size());
  }
@Test(expected = SearchException.class)
  public void testFindTokensThrowsOnEmptyNestedBraces() throws Exception {
    QueryParser parser = new QueryParser();
    parser.findTokens("{Token{{}}}");
  }
@Test
  public void testCreateTermsHandlesFeatureNameContainingNumbers() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("{Person.version2==\"active\"}");
    assertEquals(1, result[0].size());
  }
@Test
  public void testCreateTermsHandlesQuotedMultiWordValues() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("{Tag==\"Natural Language Processing\"}");
    assertEquals(1, result[0].size());
  }
@Test
  public void testFindTokensHandlesLeadingAndTrailingWhitespace() throws Exception {
    QueryParser parser = new QueryParser();
    List<String> tokens = parser.findTokens("   {Tag}   ");
    assertEquals(1, tokens.size());
    assertEquals("{Tag}", tokens.get(0));
  }
@Test
  public void testCreateTermsHandlesUnbalancedFeatureFormatWithNoQuotes() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("{Person.gender==male}");
    assertEquals(1, result[0].size());
  }
@Test
  public void testCreateTermsHandlesRedundantEscapeSequencesSafely() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("\"Token\\\\\\\\\\\"\"");
    assertEquals(1, result[0].size());
  }
@Test(expected = SearchException.class)
  public void testParseWithNullQueryThrows() throws Exception {
    QueryParser parser = new QueryParser();
    parser.parse("field", null, "Token", null, null);
  }
@Test(expected = SearchException.class)
  public void testParseWithEmptyQueryThrows() throws Exception {
    QueryParser parser = new QueryParser();
    parser.parse("field", "", "Token", null, null);
  }
@Test
  public void testFindTokensWithEmptyQuotes() throws Exception {
    QueryParser parser = new QueryParser();
    List<String> tokens = parser.findTokens("\"\"");
    assertEquals(1, tokens.size());
    assertEquals("\"\"", tokens.get(0));
  }
@Test
  public void testFindTokensWithEscapedQuoteAtEnd() throws Exception {
    QueryParser parser = new QueryParser();
    List<String> tokens = parser.findTokens("\"data\\\"\"");
    assertEquals(1, tokens.size());
    assertEquals("\"data\\\"\"", tokens.get(0));
  }
@Test
  public void testFindTokensWithMultipleSpacesAndOneQuote() throws Exception {
    QueryParser parser = new QueryParser();
    List<String> tokens = parser.findTokens("   \"hello world\"  ");
    assertEquals(1, tokens.size());
    assertEquals("\"hello world\"", tokens.get(0));
  }
@Test(expected = SearchException.class)
  public void testCreateTermsThrowsOnUnclosedCompositeBlock() throws Exception {
    QueryParser parser = new QueryParser();
    parser.createTerms("{Token,\"hello\"");
  }

  @Test(expected = SearchException.class)
  public void testCreateTermsThrowsOnIncompleteAnnotFeatureSyntax() throws Exception {
    QueryParser parser = new QueryParser();
    parser.createTerms("{Token.partOfSpeech=}");
  }

  @Test
  public void testCreateTermsHandlesEscapedDotsInFeatureName() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] parts = parser.createTerms("{Entity.feature\\.name==\"value\"}");
    assertEquals(1, parts[0].size());
  }

  @Test
  public void testFindIndexOfHandlesEscapeAsFinalCharacter() {
    QueryParser parser = new QueryParser();
    int index = parser.findIndexOf("hello\\", '.');
    assertEquals(-1, index);
  }

  @Test
  public void testFindIndexOfSkipsEscapedMultipleTargets() {
    QueryParser parser = new QueryParser();
    int index = parser.findIndexOf("x.om\\,a.b", ',');
    assertEquals(-1, index);
  }

  @Test
  public void testSplitStringWithLongBackslashRun() {
    QueryParser parser = new QueryParser();
    List<String> split = parser.splitString("path\\\\\\\\\\,folder,final", ',', true);
    assertEquals("path\\\\\\\\,folder", split.get(0));
    assertEquals("final", split.get(1));
  }

  @Test
  public void testSplitStringWithNoEscapeAndNoDelimiterReturnsOriginal() {
    QueryParser parser = new QueryParser();
    List<String> split = parser.splitString("simpletext", ',', false);
    assertEquals(1, split.size());
    assertEquals("simpletext", split.get(0));
  }

  @Test
  public void testNormWithMixedAllowedAndDisallowedCharacters() {
    QueryParser parser = new QueryParser();
    String value = parser.norm("ok\\\"text\\\\invalid\\Zend");
    assertEquals("oktext\\invalid\\Zend", value);
  }
@Test
  public void testFindIndexOfHandlesEscapeAsFinalCharacter() {
    QueryParser parser = new QueryParser();
    int index = parser.findIndexOf("hello\\", '.');
    assertEquals(-1, index);
  }
@Test
  public void testFindIndexOfSkipsEscapedMultipleTargets() {
    QueryParser parser = new QueryParser();
    int index = parser.findIndexOf("x.om\\,a.b", ',');
    assertEquals(-1, index);
  }
@Test
  public void testSplitStringWithLongBackslashRun() {
    QueryParser parser = new QueryParser();
    List<String> split = parser.splitString("path\\\\\\\\\\,folder,final", ',', true);
    assertEquals("path\\\\\\\\,folder", split.get(0));
    assertEquals("final", split.get(1));
  }
@Test
  public void testSplitStringWithNoEscapeAndNoDelimiterReturnsOriginal() {
    QueryParser parser = new QueryParser();
    List<String> split = parser.splitString("simpletext", ',', false);
    assertEquals(1, split.size());
    assertEquals("simpletext", split.get(0));
  }
@Test
  public void testNormWithMixedAllowedAndDisallowedCharacters() {
    QueryParser parser = new QueryParser();
    String value = parser.norm("ok\\\"text\\\\invalid\\Zend");
    assertEquals("oktext\\invalid\\Zend", value);
  }
@Test
  public void testCreateTermsWithFeatureContainingColons() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("{Entity.prop:type==\"Person\"}");
    assertEquals(1, result[0].size());
  }
@Test(expected = SearchException.class)
  public void testCreateTermsThrowsOnMissingFeatureNameAfterDot() throws Exception {
    QueryParser parser = new QueryParser();
    parser.createTerms("{Token.==\"value\"}");
  }
@Test(expected = SearchException.class)
  public void testCreateTermsThrowsOnFeatureMissingClosingQuote() throws Exception {
    QueryParser parser = new QueryParser();
    parser.createTerms("{Label==\"unclosed}");
  }
@Test
  public void testCreateTermsSupportsQuotedStringWithDotsInsideValue() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("{Entity==\"U.K. based\"}");
    assertEquals(1, result[0].size());
  }
@Test
  public void testCreateTermsSupportsQuotedStringWithMultipleCommas() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("{Location==\"New York, USA, Earth\"}");
    assertEquals(1, result[0].size());
  }
@Test
  public void testCreateTermsParsesDeepNestedFeatureCorrectly() throws Exception {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("{Entity.location.city==\"Paris\"}");
    assertEquals(1, result[0].size());
  } 
}