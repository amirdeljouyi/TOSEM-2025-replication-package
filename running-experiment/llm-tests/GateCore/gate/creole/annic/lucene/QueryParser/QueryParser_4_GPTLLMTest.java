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

public class QueryParser_4_GPTLLMTest {

 @Test
  public void testIsValidQueryWithValidSimpleQuery() {
    boolean result = QueryParser.isValidQuery("{Token}");
    assertTrue(result);
  }
@Test
  public void testIsValidQueryWithValidComplexQuery() {
    boolean result = QueryParser.isValidQuery("{Person.gender==\"male\"}");
    assertTrue(result);
  }
@Test
  public void testIsValidQueryWithQuotedWords() {
    boolean result = QueryParser.isValidQuery("\"Hello World\"");
    assertTrue(result);
  }
@Test
  public void testIsValidQueryWithMultipleAnnotations() {
    boolean result = QueryParser.isValidQuery("{Person}{Token}{Person.gender==\"male\"}");
    assertTrue(result);
  }
@Test
  public void testIsValidQueryWithUnbalancedBracesOpening() {
    boolean result = QueryParser.isValidQuery("{Token");
    assertFalse(result);
  }

  @Test
  public void testIsValidQueryWithUnbalancedBracesClosing() {
    boolean result = QueryParser.isValidQuery("Token}");
    assertFalse(result);
  }
@Test
  public void testParseWithSingleAnnotation() throws SearchException {
    QueryParser parser = new QueryParser();
    Query[] queries = parser.parse("contents", "{Token}", "Token", null, null);
    assertEquals(1, queries.length);
    assertTrue(queries[0] instanceof BooleanQuery);
  }
@Test
  public void testParseWithAnnotationAndFeature() throws SearchException {
    QueryParser parser = new QueryParser();
    Query[] queries = parser.parse("contents", "{Person.gender==\"male\"}", "Token", null, null);
    assertEquals(1, queries.length);
    assertTrue(queries[0] instanceof BooleanQuery);
  }
@Test
  public void testParseWithQuotedText() throws SearchException {
    QueryParser parser = new QueryParser();
    Query[] queries = parser.parse("contents", "\"Hello World\"", "Token", null, null);
    assertEquals(1, queries.length);
    assertTrue(queries[0] instanceof BooleanQuery);
  }
@Test
  public void testFindTokensMultipleBraces() throws SearchException {
    QueryParser parser = new QueryParser();
    List<String> tokens = parser.findTokens("{Token}{Person}");
    assertEquals(2, tokens.size());
    assertEquals("{Token}", tokens.get(0));
    assertEquals("{Person}", tokens.get(1));
  }
@Test(expected = SearchException.class)
  public void testFindTokensUnbalancedOpeningBrace() throws SearchException {
    QueryParser parser = new QueryParser();
    parser.findTokens("{Token");
  }

  @Test(expected = SearchException.class)
  public void testFindTokensUnbalancedClosingBrace() throws SearchException {
    QueryParser parser = new QueryParser();
    parser.findTokens("Token}");
  }
@Test(expected = SearchException.class)
  public void testFindTokensUnbalancedClosingBrace() throws SearchException {
    QueryParser parser = new QueryParser();
    parser.findTokens("Token}
  }
@Test
  public void testCreateTermsWithSimpleAnnotation() throws SearchException {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("{Token}");
    Object term = result[0].get(0);
    assertTrue(term instanceof Term);
    Term casted = (Term) term;
    assertEquals("Token", casted.text());
    assertEquals("*", casted.type());
  }
@Test
  public void testCreateTermsWithQuotedStringMatch() throws SearchException {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("{Token==\"yes\"}");
    Object term = result[0].get(0);
    assertTrue(term instanceof Term);
    Term casted = (Term) term;
    assertEquals("yes", casted.text());
    assertEquals("Token.string", casted.type());
  }
@Test
  public void testCreateTermsWithFeatureMatch() throws SearchException {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("{Person.gender==\"male\"}");
    Object term = result[0].get(0);
    assertTrue(term instanceof Term);
    Term casted = (Term) term;
    assertEquals("male", casted.text());
    assertEquals("Person.gender", casted.type());
  }
@Test
  public void testCreateTermsWithQuotedWords() throws SearchException {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("\"Hello World\"");
    Object term1 = result[0].get(0);
    Object term2 = result[0].get(1);
    assertTrue(term1 instanceof Term);
    assertTrue(term2 instanceof Term);
    assertEquals("Hello", ((Term) term1).text());
    assertEquals("World", ((Term) term2).text());
  }
@Test(expected = SearchException.class)
  public void testCreateTermsMissingEqualsOperator() throws SearchException {
    QueryParser parser = new QueryParser();
    parser.createTerms("{Person.gender\"male\"}");
  }
@Test
  public void testNormalizationStripsEscapes() {
    QueryParser parser = new QueryParser();
    String normalized = parser.norm("Hello\\{World\\} \\\"quote\\\"");
    assertEquals("Hello{World} quote", normalized);
  }
@Test
  public void testSplitStringWithEscapedComma() {
    QueryParser parser = new QueryParser();
    ArrayList<String> result = parser.splitString("a,b\\,c,d", ',', true);
    assertEquals(3, result.size());
    assertEquals("a", result.get(0));
    assertEquals("b,c", result.get(1));
    assertEquals("d", result.get(2));
  }
@Test
  public void testParseWithCorpusAndAnnotationSet() throws SearchException {
    QueryParser parser = new QueryParser();
    Query[] queries = parser.parse("contents", "{Token}", "Token", "corpus123", "default");
    assertEquals(1, queries.length);
    assertTrue(queries[0] instanceof BooleanQuery);
  }
@Test
  public void testGetQueryStringReturnsCorrectValue() throws SearchException {
    QueryParser parser = new QueryParser();
    parser.parse("contents", "{Token}", "Token", null, null);
    String result = parser.getQueryString(0);
    assertEquals("{Token}", result);
  }
@Test(expected = IndexOutOfBoundsException.class)
  public void testGetQueryStringThrowsForInvalidIndex() throws SearchException {
    QueryParser parser = new QueryParser();
    parser.parse("contents", "{Token}", "Token", null, null);
    parser.getQueryString(5);
  }
@Test
  public void testSplitStringWithoutEscapedChar() {
    QueryParser parser = new QueryParser();
    ArrayList<String> result = parser.splitString("x=y=z", '=', false);
    assertEquals(3, result.size());
    assertEquals("x", result.get(0));
    assertEquals("y", result.get(1));
    assertEquals("z", result.get(2));
  }
@Test
  public void testSplitStringWithAllEscaped() {
    QueryParser parser = new QueryParser();
    ArrayList<String> result = parser.splitString("a\\=b\\=c", '=', false);
    assertEquals(1, result.size());
    assertEquals("a=b=c", result.get(0));
  }
@Test
  public void testEscapeSequenceDetectedCorrectly() {
    QueryParser parser = new QueryParser();
    boolean result = parser.isEscapeSequence("a\\=b", 2);
    assertTrue(result);
  }
@Test
  public void testEscapeSequenceNotDetectedIfFirstChar() {
    QueryParser parser = new QueryParser();
    boolean result = parser.isEscapeSequence("=a", 0);
    assertFalse(result);
  }
@Test
  public void testOpenBraceDetectedCorrectly() {
    QueryParser parser = new QueryParser();
    boolean result = parser.isOpeneningBrace('{', 'a');
    assertTrue(result);
  }

  @Test
  public void testOpenBraceNotDetectedIfEscaped() {
    QueryParser parser = new QueryParser();
    boolean result = parser.isOpeneningBrace('{', '\\');
    assertFalse(result);
  }

  @Test
  public void testCloseBraceDetectedCorrectly() {
    QueryParser parser = new QueryParser();
    boolean result = parser.isClosingBrace('}', 'a');
    assertTrue(result);
  }

  @Test
  public void testCloseBraceNotDetectedIfEscaped() {
    QueryParser parser = new QueryParser();
    boolean result = parser.isClosingBrace('}', '\\');
    assertFalse(result);
  }
@Test
  public void testOpenBraceNotDetectedIfEscaped() {
    QueryParser parser = new QueryParser();
    boolean result = parser.isOpeneningBrace('{', '\\');
    assertFalse(result);
  }

  @Test
  public void testCloseBraceDetectedCorrectly() {
    QueryParser parser = new QueryParser();
    boolean result = parser.isClosingBrace('}', 'a');
    assertTrue(result);
  }
@Test
  public void testCreateTermsWithAnnotationEqualsUnquotedString() throws SearchException {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("{Token==yes}");
    assertEquals(1, result[0].size());
    Term term = (Term) result[0].get(0);
    assertEquals("yes", term.text());
    assertEquals("Token.string", term.type());
  }
@Test
  public void testCreateTermsWithAnnotationEqualsQuotedStringWithEscapes() throws SearchException {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("{Person.name==\"John\\\"Doe\"}");
    assertEquals(1, result[0].size());
    Term term = (Term) result[0].get(0);
    assertEquals("John\"Doe", term.text());
    assertEquals("Person.name", term.type());
  }
@Test
  public void testCreateTermsWithMultipleCommaSeparatedValues() throws SearchException {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("{Foo, Bar, Baz}");
    assertEquals(3, result[0].size());
    Term term0 = (Term) result[0].get(0);
    Term term1 = (Term) result[0].get(1);
    Term term2 = (Term) result[0].get(2);
    assertEquals("Foo", term0.text());
    assertEquals("Bar", term1.text());
    assertEquals("Baz", term2.text());
  }
@Test
  public void testCreateTermsQuotedTextWithMultipleSpaces() throws SearchException {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("\"  Hello    World  Again  \"");
    assertEquals(3, result[0].size());
    Term term0 = (Term) result[0].get(0);
    Term term1 = (Term) result[0].get(1);
    Term term2 = (Term) result[0].get(2);
    assertEquals("Hello", term0.text());
    assertEquals("World", term1.text());
    assertEquals("Again", term2.text());
  }
@Test(expected = SearchException.class)
  public void testCreateTermsWithOnlyFeatureNoOperator() throws SearchException {
    QueryParser parser = new QueryParser();
    parser.createTerms("{Token.feature}");
  }
@Test(expected = SearchException.class)
  public void testCreateTermsWithOnlyEqualsSignNoRightSide() throws SearchException {
    QueryParser parser = new QueryParser();
    parser.createTerms("{Token==}");
  }
@Test
  public void testCreateTermsWithEscapedQuoteInStringMatches() throws SearchException {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("{Token==\"\\\"quoted\\\"\"}");
    assertEquals(1, result[0].size());
    Term term = (Term) result[0].get(0);
    assertEquals("\"quoted\"", term.text());
  }
@Test
  public void testSplitStringWithTrailingDelimiter() {
    QueryParser parser = new QueryParser();
    ArrayList<String> result = parser.splitString("a,b,", ',', true);
    assertEquals(3, result.size());
    assertEquals("a", result.get(0));
    assertEquals("b", result.get(1));
    assertEquals("", result.get(2));
  }
@Test
  public void testFindTokensMixedBracesAndQuotes() throws SearchException {
    QueryParser parser = new QueryParser();
    List<String> result = parser.findTokens("{Person}\"said\"{Token}");
    assertEquals(3, result.size());
    assertEquals("{Person}", result.get(0));
    assertEquals("\"said\"", result.get(1));
    assertEquals("{Token}", result.get(2));
  }
@Test
  public void testFindTokensHandlesWhitespaceBetweenTokens() throws SearchException {
    QueryParser parser = new QueryParser();
    List<String> result = parser.findTokens(" {Token}  {Person} ");
    assertEquals(2, result.size());
    assertEquals("{Token}", result.get(0));
    assertEquals("{Person}", result.get(1));
  }
@Test
  public void testCreateTermsWithMultipleComplexConditions() throws SearchException {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("{Person.gender==\"male\",Person.name==\"John\"}");
    assertEquals(2, result[0].size());
    Term term0 = (Term) result[0].get(0);
    Term term1 = (Term) result[0].get(1);
    assertEquals("male", term0.text());
    assertEquals("Person.gender", term0.type());
    assertEquals("John", term1.text());
    assertEquals("Person.name", term1.type());
  }
@Test(expected = SearchException.class)
  public void testCreateTermsWithDotButNoFeature() throws SearchException {
    QueryParser parser = new QueryParser();
    parser.createTerms("{Annotation.==value}");
  }
@Test
  public void testNormIgnoresUnrecognizedEscapeSequence() {
    QueryParser parser = new QueryParser();
    String result = parser.norm("value\\x");
    assertEquals("value\\x", result);
  }
@Test
  public void testNormPreservesTextWithNoEscape() {
    QueryParser parser = new QueryParser();
    String result = parser.norm("normalText");
    assertEquals("normalText", result);
  }
@Test
  public void testSplitStringWithNoSeparatorsReturnsWhole() {
    QueryParser parser = new QueryParser();
    ArrayList<String> result = parser.splitString("singleValue", ',', true);
    assertEquals(1, result.size());
    assertEquals("singleValue", result.get(0));
  }
@Test
  public void testFindTokensWithOnlyQuotes() throws SearchException {
    QueryParser parser = new QueryParser();
    List<String> result = parser.findTokens("\"justText\"");
    assertEquals(1, result.size());
    assertEquals("\"justText\"", result.get(0));
  }
@Test(expected = SearchException.class)
  public void testFindTokensWithEmptyBrace() throws SearchException {
    QueryParser parser = new QueryParser();
    parser.findTokens("{}");
  }
@Test
  public void testFindTokensHandlesEscapedBracesCorrectly() throws SearchException {
    QueryParser parser = new QueryParser();
    List<String> result = parser.findTokens("\\{Token\\} {Person}");
    assertEquals(2, result.size());
    assertEquals("\\{Token\\}", result.get(0));
    assertEquals("{Person}", result.get(1));
  }
@Test(expected = SearchException.class)
  public void testCreateTermsWithNestedBracesThrowsError() throws SearchException {
    QueryParser parser = new QueryParser();
    parser.createTerms("{{Token}}");
  }
@Test
  public void testFindIndexOfWithEscapedCharacters() {
    QueryParser parser = new QueryParser();
    int result = parser.findIndexOf("abc\\=def=ghi", '=');
    assertEquals(9, result);
  }
@Test
  public void testFindIndexOfNoOccurrenceReturnsNegativeOne() {
    QueryParser parser = new QueryParser();
    int result = parser.findIndexOf("abcdef", '@');
    assertEquals(-1, result);
  }
@Test(expected = SearchException.class)
  public void testFindTokensWithNestedOpeningBraceThrows() throws SearchException {
    QueryParser parser = new QueryParser();
    parser.findTokens("{Token {Person}");
  }

  @Test(expected = SearchException.class)
  public void testFindTokensWithNestedClosingBraceThrows() throws SearchException {
    QueryParser parser = new QueryParser();
    parser.findTokens("{Token}}");
  }
@Test(expected = SearchException.class)
  public void testFindTokensWithNestedClosingBraceThrows() throws SearchException {
    QueryParser parser = new QueryParser();
    parser.findTokens("{Token}");
 }
@Test(expected = SearchException.class)
  public void testCreateTermsWithFeatureWithoutTypeThrows() throws SearchException {
    QueryParser parser = new QueryParser();
    parser.createTerms("{.gender==male}");
  }
@Test
  public void testCreateTermsWithMixedAnnotationAndValueOnly() throws SearchException {
    QueryParser parser = new QueryParser();
    List<?>[] results = parser.createTerms("{Token, Token==\"yes\"}");
    assertEquals(2, results[0].size());
    Term term0 = (Term) results[0].get(0);
    Term term1 = (Term) results[0].get(1);
    assertEquals("Token", term0.text());
    assertEquals("*", term0.type());
    assertEquals("yes", term1.text());
    assertEquals("Token.string", term1.type());
  }
@Test
  public void testCreateTermsWithAnnotationAndSingleFeatureValue() throws SearchException {
    QueryParser parser = new QueryParser();
    List<?>[] results = parser.createTerms("{Person.gender==\"male\"}");
    assertEquals(1, results[0].size());
    Term term = (Term) results[0].get(0);
    assertEquals("male", term.text());
    assertEquals("Person.gender", term.type());
  }
@Test
  public void testCreateTermsShouldTrimSpacesFromValues() throws SearchException {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("{ Token == \" hello \" }");
    assertEquals(1, result[0].size());
    Term term = (Term) result[0].get(0);
    assertEquals(" hello ", term.text());
    assertEquals("Token.string", term.type());
  }
@Test(expected = SearchException.class)
  public void testCreateTermsWithCommaButNoTypeThrows() throws SearchException {
    QueryParser parser = new QueryParser();
    parser.createTerms("{==value,}");
  }
@Test
  public void testSplitStringHandlesBackslashEscapeAtEndProperly() {
    QueryParser parser = new QueryParser();
    ArrayList<String> result = parser.splitString("a,b\\", ',', false);
    assertEquals(2, result.size());
    assertEquals("a", result.get(0));
    assertEquals("b\\", result.get(1));
  }
@Test
  public void testSplitStringHandlesMultipleEscapedSeparators() {
    QueryParser parser = new QueryParser();
    ArrayList<String> result = parser.splitString("a\\,b\\,c", ',', false);
    assertEquals(1, result.size());
    assertEquals("a,b,c", result.get(0));
  }
@Test
  public void testNormHandlesMultipleEscapeCharacters() {
    QueryParser parser = new QueryParser();
    String result = parser.norm("a\\{b\\}c\\\"d\\\\e\\.");
    assertEquals("a{b}c\"d\\e.", result);
  }
@Test
  public void testCreateTermsWithQuotedValueContainingComma() throws SearchException {
    QueryParser parser = new QueryParser();
    List<?>[] results = parser.createTerms("{Token==\"yes,please\"}");
    assertEquals(1, results[0].size());
    Term term = (Term) results[0].get(0);
    assertEquals("yes,please", term.text());
    assertEquals("Token.string", term.type());
  }
@Test
  public void testCreateTermsWithAnnotationDotInName() throws SearchException {
    QueryParser parser = new QueryParser();
    List<?>[] results = parser.createTerms("{Some.Type==\"value\"}");
    assertEquals(1, results[0].size());
    Term term = (Term) results[0].get(0);
    assertEquals("value", term.text());
    assertEquals("Some.Type.string", term.type());
  }
@Test
  public void testNormReturnsOriginalIfNoEscape() {
    QueryParser parser = new QueryParser();
    String result = parser.norm("NoSpecialChars");
    assertEquals("NoSpecialChars", result);
  }
@Test(expected = SearchException.class)
  public void testCreateTermsFailsOnEmptyAnnotationBlock() throws SearchException {
    QueryParser parser = new QueryParser();
    parser.createTerms("{}");
  }
@Test
  public void testFindTokensWithWhitespaceOnly() throws SearchException {
    QueryParser parser = new QueryParser();
    List<String> result = parser.findTokens("   ");
    assertEquals(0, result.size());
  }
@Test
  public void testCreateTermsHandlesLeadingAndTrailingWhitespace() throws SearchException {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("   {Token}   ");
    assertEquals(1, result[0].size());
    Term term = (Term) result[0].get(0);
    assertEquals("Token", term.text());
    assertEquals("*", term.type());
  }
@Test
  public void testCreateTermsWithMultipleQuotedWordsInOneToken() throws SearchException {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("\"He said Hello\"");
    assertEquals(3, result[0].size());
    Term t0 = (Term) result[0].get(0);
    Term t1 = (Term) result[0].get(1);
    Term t2 = (Term) result[0].get(2);
    assertEquals("He", t0.text());
    assertEquals("said", t1.text());
    assertEquals("Hello", t2.text());
  }
@Test
  public void testSplitStringWithEmptyInput() {
    QueryParser parser = new QueryParser();
    ArrayList<String> result = parser.splitString("", ',', true);
    assertEquals(0, result.size());
  }
@Test
  public void testSplitStringWithSingleCharNoDelimiter() {
    QueryParser parser = new QueryParser();
    ArrayList<String> result = parser.splitString("x", ',', false);
    assertEquals(1, result.size());
    assertEquals("x", result.get(0));
  }
@Test
  public void testSplitStringDelimiterAtStart() {
    QueryParser parser = new QueryParser();
    ArrayList<String> result = parser.splitString(",value", ',', false);
    assertEquals(2, result.size());
    assertEquals("", result.get(0));
    assertEquals("value", result.get(1));
  }
@Test
  public void testSplitStringDelimiterAtEnd() {
    QueryParser parser = new QueryParser();
    ArrayList<String> result = parser.splitString("value,", ',', false);
    assertEquals(2, result.size());
    assertEquals("value", result.get(0));
    assertEquals("", result.get(1));
  }
@Test
  public void testNormWithOnlyEscapeCharacters() {
    QueryParser parser = new QueryParser();
    String result = parser.norm("\\,\\\"\\{\\}\\.");
    assertEquals(",", result);
  }
@Test
  public void testNormWithValidEscapedAndNormalChars() {
    QueryParser parser = new QueryParser();
    String result = parser.norm("hello\\{\\}world");
    assertEquals("helloworld", result);
  }
@Test
  public void testNormWithUnescapedBackslash() {
    QueryParser parser = new QueryParser();
    String result = parser.norm("value\\n");
    assertEquals("value\\n", result);
  }
@Test
  public void testFindTokensWithSequentialQuotedAndAnnotatedTokens() throws SearchException {
    QueryParser parser = new QueryParser();
    List<String> result = parser.findTokens("\"Hello\"{Token}\"World\"");
    assertEquals(3, result.size());
    assertEquals("\"Hello\"", result.get(0));
    assertEquals("{Token}", result.get(1));
    assertEquals("\"World\"", result.get(2));
  }
@Test(expected = SearchException.class)
  public void testFindTokensThrowsOnUnterminatedQuoteMismatchWithBrace() throws SearchException {
    QueryParser parser = new QueryParser();
    parser.findTokens("\"text{Token}");
  }
@Test
  public void testCreateTermsLiteralWithEscapedQuotes() throws SearchException {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("\"He said \\\"Hi\\\"\"");
    assertEquals(3, result[0].size());
    Term t0 = (Term) result[0].get(0);
    Term t1 = (Term) result[0].get(1);
    Term t2 = (Term) result[0].get(2);
    assertEquals("He", t0.text());
    assertEquals("said", t1.text());
    assertEquals("\"Hi\"", t2.text());
  }
@Test(expected = SearchException.class)
  public void testCreateTermsWithInvalidFeatureMissingDoubleEquals() throws SearchException {
    QueryParser parser = new QueryParser();
    parser.createTerms("{Token.value=value}");
  }
@Test(expected = SearchException.class)
  public void testCreateTermsMissingFeatureAfterDot() throws SearchException {
    QueryParser parser = new QueryParser();
    parser.createTerms("{Token.==\"text\"}");
  }
@Test
  public void testFindIndexOfFindsUnescapedChar() {
    QueryParser parser = new QueryParser();
    int index = parser.findIndexOf("text=value", '=');
    assertEquals(4, index);
  }
@Test
  public void testFindIndexOfSkipsEscapedEquals() {
    QueryParser parser = new QueryParser();
    int index = parser.findIndexOf("text\\==value", '=');
    assertEquals(10, index);
  }
@Test
  public void testFindIndexOfNoMatchReturnsNegativeOne() {
    QueryParser parser = new QueryParser();
    int index = parser.findIndexOf("abc", ',');
    assertEquals(-1, index);
  }
@Test
  public void testIsEscapeSequenceWithValidEscape() {
    QueryParser parser = new QueryParser();
    boolean result = parser.isEscapeSequence("a\\=b", 2);
    assertTrue(result);
  }
@Test
  public void testIsEscapeSequenceWithInvalidEscape() {
    QueryParser parser = new QueryParser();
    boolean result = parser.isEscapeSequence("abc", 1);
    assertFalse(result);
  }
@Test
  public void testIsOpeneningBraceTrue() {
    QueryParser parser = new QueryParser();
    boolean result = parser.isOpeneningBrace('{', 'x');
    assertTrue(result);
  }

  @Test
  public void testIsOpeneningBraceFalseIfEscaped() {
    QueryParser parser = new QueryParser();
    boolean result = parser.isOpeneningBrace('{', '\\');
    assertFalse(result);
  }

  @Test
  public void testIsClosingBraceTrue() {
    QueryParser parser = new QueryParser();
    boolean result = parser.isClosingBrace('}', 'x');
    assertTrue(result);
  }

  @Test
  public void testIsClosingBraceFalseIfEscaped() {
    QueryParser parser = new QueryParser();
    boolean result = parser.isClosingBrace('}', '\\');
    assertFalse(result);
  }
@Test
  public void testIsOpeneningBraceFalseIfEscaped() {
    QueryParser parser = new QueryParser();
    boolean result = parser.isOpeneningBrace('{', '\\');
    assertFalse(result);
  }

  @Test
  public void testIsClosingBraceTrue() {
    QueryParser parser = new QueryParser();
    boolean result = parser.isClosingBrace('}', 'x');
    assertTrue(result);
  }
@Test
  public void testFindTokensEmptyString() throws SearchException {
    QueryParser parser = new QueryParser();
    List<String> tokens = parser.findTokens("");
    assertEquals(0, tokens.size());
  }
@Test
  public void testSplitStringWithBackslashesOnly() {
    QueryParser parser = new QueryParser();
    ArrayList<String> result = parser.splitString("\\\\", ',', true);
    assertEquals(1, result.size());
    assertEquals("\\\\", result.get(0));
  }
@Test(expected = SearchException.class)
  public void testCreateTermsWithInvalidAnnotationSyntaxDoubleComma() throws SearchException {
    QueryParser parser = new QueryParser();
    parser.createTerms("{Token,,Other}");
  }
@Test(expected = SearchException.class)
  public void testCreateTermsWithMissingAnnotationTypeBeforeDot() throws SearchException {
    QueryParser parser = new QueryParser();
    parser.createTerms("{.feature==\"value\"}");
  }
@Test(expected = SearchException.class)
  public void testCreateTermsWithEmptyQuotesThrows() throws SearchException {
    QueryParser parser = new QueryParser();
    parser.createTerms("\"\"");
  }
@Test(expected = SearchException.class)
  public void testCreateTermsWithMalformedQuotedAnnotationString() throws SearchException {
    QueryParser parser = new QueryParser();
    parser.createTerms("{Token==\"missingEnd}");
  }
@Test(expected = SearchException.class)
  public void testCreateTermsWithOnlyCommaInsideAnnotation() throws SearchException {
    QueryParser parser = new QueryParser();
    parser.createTerms("{,}");
  }
@Test
  public void testCreateTermsWithAnnotationDotInFeatureName() throws SearchException {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("{Custom.type.feature==\"val\"}");
    assertEquals(1, result[0].size());
    Term term = (Term) result[0].get(0);
    assertEquals("val", term.text());
    assertEquals("Custom.type.feature", term.type());
  }
@Test
  public void testCreateTermsWithEmptyStringLiteral() throws SearchException {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("\"\"");
    assertEquals(0, result[0].size());
  }
@Test(expected = SearchException.class)
  public void testFindTokensWithNestedOpeningBraces() throws SearchException {
    QueryParser parser = new QueryParser();
    parser.findTokens("{Token{Another}");
  }
@Test
  public void testSplitStringWithAdjacentDelimiters() {
    QueryParser parser = new QueryParser();
    ArrayList<String> result = parser.splitString("a,,b", ',', true);
    assertEquals(3, result.size());
    assertEquals("a", result.get(0));
    assertEquals("", result.get(1));
    assertEquals("b", result.get(2));
  }
@Test
  public void testSplitStringOnlyOneDelimiterNoText() {
    QueryParser parser = new QueryParser();
    ArrayList<String> result = parser.splitString(",", ',', true);
    assertEquals(2, result.size());
    assertEquals("", result.get(0));
    assertEquals("", result.get(1));
  }
@Test
  public void testNormWithMixedEscapedAndUnescapedChars() {
    QueryParser parser = new QueryParser();
    String input = "\\{Hello\\} World \\\\ \"Text\"";
    String output = parser.norm(input);
    assertEquals("{Hello} World \\ Text", output);
  }
@Test
  public void testNormRemovesEscapedSpecialCharsOnly() {
    QueryParser parser = new QueryParser();
    String input = "\\\"quoted\\text\\,\\{end\\}";
    String output = parser.norm(input);
    assertEquals("quoted\\text,end", output);}
@Test
  public void testIsEscapeSequenceFalseOnFirstChar() {
    QueryParser parser = new QueryParser();
    boolean result = parser.isEscapeSequence("\\=hello", 0);
    assertFalse(result);
  }
@Test
  public void testIsEscapeSequenceFalseWhenNoEscape() {
    QueryParser parser = new QueryParser();
    boolean result = parser.isEscapeSequence("abc", 1);
    assertFalse(result);
  }
@Test
  public void testIsEscapeSequenceTrueWithBackslashBefore() {
    QueryParser parser = new QueryParser();
    boolean result = parser.isEscapeSequence("x\\=y", 2);
    assertTrue(result);
  }
@Test
  public void testFindTokensHandlesEscapeBraceCorrectly() throws SearchException {
    QueryParser parser = new QueryParser();
    List<String> tokens = parser.findTokens("\\{notAnnotation\\}{Real}");
    assertEquals(2, tokens.size());
    assertEquals("\\{notAnnotation\\}", tokens.get(0));
    assertEquals("{Real}", tokens.get(1));
  }
@Test
  public void testCreateTermsParsesMultipleTokensWithSpaces() throws SearchException {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("\" one  two three \"");
    assertEquals(3, result[0].size());
    Term t0 = (Term) result[0].get(0);
    Term t1 = (Term) result[0].get(1);
    Term t2 = (Term) result[0].get(2);
    assertEquals("one", t0.text());
    assertEquals("two", t1.text());
    assertEquals("three", t2.text());
  }
@Test
  public void testCreateTermsWithMultipleFeatureAnnotationsMixed() throws SearchException {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("{Entity,Entity.type==\"name\",Entity.category==\"person\"}");
    assertEquals(3, result[0].size());
    Term t0 = (Term) result[0].get(0);
    Term t1 = (Term) result[0].get(1);
    Term t2 = (Term) result[0].get(2);
    assertEquals("Entity", t0.text());
    assertEquals("*", t0.type());
    assertEquals("name", t1.text());
    assertEquals("Entity.type", t1.type());
    assertEquals("person", t2.text());
    assertEquals("Entity.category", t2.type());
  }
@Test
  public void testCreateTermsWithQuotedFeatureValueWithEmbeddedComma() throws SearchException {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("{Entity.name==\"Doe, John\"}");
    assertEquals(1, result[0].size());
    Term term = (Term) result[0].get(0);
    assertEquals("Doe, John", term.text());
    assertEquals("Entity.name", term.type());
  }
@Test
  public void testCreateTermsWithMultipleMixedEntryWithSpacesAndFeatures() throws SearchException {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("{Token , Token==\"yes\", Token.pos==\"NN\" }");
    assertEquals(3, result[0].size());
    Term t0 = (Term) result[0].get(0);
    Term t1 = (Term) result[0].get(1);
    Term t2 = (Term) result[0].get(2);
    assertEquals("Token", t0.text());
    assertEquals("*", t0.type());
    assertEquals("yes", t1.text());
    assertEquals("Token.string", t1.type());
    assertEquals("NN", t2.text());
    assertEquals("Token.pos", t2.type());
  }
@Test(expected = SearchException.class)
  public void testCreateTermsWithNestedCommaButNoAnnotation() throws SearchException {
    QueryParser parser = new QueryParser();
    parser.createTerms("{==}");
  }
@Test
  public void testSplitStringHandlesMultipleEscapedCommasBackToBack() {
    QueryParser parser = new QueryParser();
    ArrayList<String> list = parser.splitString("a\\,b\\,c\\,,d", ',', true);
    assertEquals(2, list.size());
    assertEquals("a,b,c,", list.get(0));
    assertEquals("d", list.get(1));
  }
@Test
  public void testCreateTermsWithFeatureValueContainingMultipleDots() throws SearchException {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("{Name.long.full==\"John.Doe.Smith\"}");
    assertEquals(1, result[0].size());
    Term term = (Term) result[0].get(0);
    assertEquals("John.Doe.Smith", term.text());
    assertEquals("Name.long.full", term.type());
  }
@Test(expected = SearchException.class)
  public void testCreateTermsWithCommaNoAnnotationText() throws SearchException {
    QueryParser parser = new QueryParser();
    parser.createTerms("{ , }");
  }
@Test
  public void testNormSkipsAllEscapedLuceneSpecials() {
    QueryParser parser = new QueryParser();
    String norm = parser.norm("\\.\\,\\(\\)\\{\\}\\\"\\\\");
    assertEquals("", norm);
  }
@Test
  public void testNormRetainsNonSpecialEscape() {
    QueryParser parser = new QueryParser();
    String norm = parser.norm("value\\x");
    assertEquals("value\\x", norm);
  }
@Test
  public void testFindTokensWithMultipleWhitespaceSeparators() throws SearchException {
    QueryParser parser = new QueryParser();
    List<String> tokens = parser.findTokens("   {Token}    \"text\"  {Person.name==\"Bob\"}    ");
    assertEquals(3, tokens.size());
    assertEquals("{Token}", tokens.get(0));
    assertEquals("\"text\"", tokens.get(1));
    assertEquals("{Person.name==\"Bob\"}", tokens.get(2));
  }
@Test(expected = SearchException.class)
  public void testCreateTermsWithOnlyOpeningBraceThrows() throws SearchException {
    QueryParser parser = new QueryParser();
    parser.createTerms("{");
  }

  @Test(expected = SearchException.class)
  public void testFindTokensWithMismatchedEscapeSequenceAtEnd() throws SearchException {
    QueryParser parser = new QueryParser();
    parser.findTokens("{Entity.name==\"John\\\"}");
  }

  @Test(expected = SearchException.class)
  public void testCreateTermsWithMalformedDotChain() throws SearchException {
    QueryParser parser = new QueryParser();
    parser.createTerms("{Entity..subtype==\"x\"}");
  }

  @Test
  public void testCreateTermsWithMultipleTermsAtOnePosition() throws SearchException {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("{Lookup==\"location\", Lookup==\"place\"}");
    assertEquals(2, result[0].size());
    int position0 = (Integer) result[1].get(0);
    int position1 = (Integer) result[1].get(1);
    assertEquals(position0, position1); 
  }

  @Test
  public void testFindTokensEmptyQuotedTextStillParsedAsToken() throws SearchException {
    QueryParser parser = new QueryParser();
    List<String> result = parser.findTokens("\"\"");
    assertEquals(1, result.size());
    assertEquals("\"\"", result.get(0));
  }

  @Test
  public void testCreateTermsWithCommaAsPartOfQuotedValue() throws SearchException {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("{Entity==\"human,male\"}");
    assertEquals(1, result[0].size());
    Term term = (Term) result[0].get(0);
    assertEquals("human,male", term.text());
  }

  @Test
  public void testSplitStringWithEscapedBackslashAndComma() {
    QueryParser parser = new QueryParser();
    ArrayList<String> result = parser.splitString("A\\\\\\,B,C", ',', false);
    assertEquals(2, result.size());
    assertEquals("A\\,B", result.get(0));
    assertEquals("C", result.get(1));
  }

  @Test(expected = SearchException.class)
  public void testCreateTermsThrowsForInvalidFeatureOnlyDots() throws SearchException {
    QueryParser parser = new QueryParser();
    parser.createTerms("{...==\"value\"}");
  }

@Test(expected = SearchException.class)
  public void testFindTokensWithMismatchedEscapeSequenceAtEnd() throws SearchException {
    QueryParser parser = new QueryParser();
    parser.findTokens("{Entity.name==\"John\\\"}");
  }
@Test(expected = SearchException.class)
  public void testCreateTermsWithMalformedDotChain() throws SearchException {
    QueryParser parser = new QueryParser();
    parser.createTerms("{Entity..subtype==\"x\"}");
  }
@Test
  public void testCreateTermsWithMultipleTermsAtOnePosition() throws SearchException {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("{Lookup==\"location\", Lookup==\"place\"}");
    assertEquals(2, result[0].size());
    int position0 = (Integer) result[1].get(0);
    int position1 = (Integer) result[1].get(1);
    assertEquals(position0, position1); 
  }
@Test
  public void testFindTokensEmptyQuotedTextStillParsedAsToken() throws SearchException {
    QueryParser parser = new QueryParser();
    List<String> result = parser.findTokens("\"\"");
    assertEquals(1, result.size());
    assertEquals("\"\"", result.get(0));
  }
@Test
  public void testCreateTermsWithCommaAsPartOfQuotedValue() throws SearchException {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("{Entity==\"human,male\"}");
    assertEquals(1, result[0].size());
    Term term = (Term) result[0].get(0);
    assertEquals("human,male", term.text());
  }
@Test
  public void testSplitStringWithEscapedBackslashAndComma() {
    QueryParser parser = new QueryParser();
    ArrayList<String> result = parser.splitString("A\\\\\\,B,C", ',', false);
    assertEquals(2, result.size());
    assertEquals("A\\,B", result.get(0));
    assertEquals("C", result.get(1));
  }
@Test(expected = SearchException.class)
  public void testCreateTermsThrowsForInvalidFeatureOnlyDots() throws SearchException {
    QueryParser parser = new QueryParser();
    parser.createTerms("{...==\"value\"}");
  }
@Test
  public void testSplitStringWithMultipleEscapedDelimitersMixed() {
    QueryParser parser = new QueryParser();
    ArrayList<String> result = parser.splitString("x,y\\,z\\,w", ',', false);
    assertEquals(2, result.size());
    assertEquals("x", result.get(0));
    assertEquals("y,z,w", result.get(1));
  }
@Test
  public void testSplitStringEscapedDelimiterAtEnd() {
    QueryParser parser = new QueryParser();
    ArrayList<String> result = parser.splitString("a\\,", ',', false);
    assertEquals(1, result.size());
    assertEquals("a,", result.get(0));
  }
@Test
  public void testNormWithEscapedCharactersNonSpecialIgnored() {
    QueryParser parser = new QueryParser();
    String result = parser.norm("abc\\m\\n\\o");
    assertEquals("abc\\m\\n\\o", result);
  }
@Test
  public void testNormPreservesContentAfterLastEscape() {
    QueryParser parser = new QueryParser();
    String result = parser.norm("hello\\.");
    assertEquals("hello", result);
  }
@Test
  public void testFindTokensWithOnlyWhitespaceReturnsEmptyList() throws SearchException {
    QueryParser parser = new QueryParser();
    List<String> result = parser.findTokens("     ");
    assertEquals(0, result.size());
  }
@Test
  public void testFindTokensWithEmptyQuotedText() throws SearchException {
    QueryParser parser = new QueryParser();
    List<String> result = parser.findTokens("\"\"");
    assertEquals(1, result.size());
    assertEquals("\"\"", result.get(0));
  }
@Test
  public void testFindTokensWithTextBeforeBrace() throws SearchException {
    QueryParser parser = new QueryParser();
    List<String> result = parser.findTokens("text {Token}");
    assertEquals(2, result.size());
    assertEquals("text", result.get(0));
    assertEquals("{Token}", result.get(1));
  }
@Test
  public void testCreateTermsFromEmptyQuotedString() throws SearchException {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("\"\"");
    assertEquals(0, result[0].size());
  }
@Test
  public void testCreateTermsFromQuotedStringWithMultipleSpaces() throws SearchException {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("\"  this   is   spaced  \"");
    assertEquals(3, result[0].size());
    Term t0 = (Term) result[0].get(0);
    Term t1 = (Term) result[0].get(1);
    Term t2 = (Term) result[0].get(2);
    assertEquals("this", t0.text());
    assertEquals("is", t1.text());
    assertEquals("spaced", t2.text());
  }
@Test
  public void testCreateTermsWithAnnotationContainingSpaceAroundEquals() throws SearchException {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("{Token == \"value\"}");
    assertEquals(1, result[0].size());
    Term term = (Term) result[0].get(0);
    assertEquals("value", term.text());
    assertEquals("Token.string", term.type());
  }
@Test(expected = SearchException.class)
  public void testCreateTermsWithFeatureMissingRightSide() throws SearchException {
    QueryParser parser = new QueryParser();
    parser.createTerms("{Token.feature==}");
  }
@Test(expected = SearchException.class)
  public void testCreateTermsWithAnnotationDotButNoType() throws SearchException {
    QueryParser parser = new QueryParser();
    parser.createTerms("{.==\"abc\"}");
  }
@Test
  public void testCreateTermsWithMultipleDotsInFeatureName() throws SearchException {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("{Person.name.full==\"John\"}");
    assertEquals(1, result[0].size());
    Term term = (Term) result[0].get(0);
    assertEquals("Person.name.full", term.type());
    assertEquals("John", term.text());
  }
@Test
  public void testCreateTermsWithMultipleAnnotationsFlatSequence() throws SearchException {
    QueryParser parser = new QueryParser();
    List<?>[] result = parser.createTerms("{Token},{Location},{Person==\"Bob\"}");
    Term t0 = (Term) result[0].get(0);
    Term t1 = (Term) result[0].get(1);
    Term t2 = (Term) result[0].get(2);
    assertEquals("Token", t0.text());
    assertEquals("*", t0.type());
    assertEquals("Location", t1.text());
    assertEquals("*", t1.type());
    assertEquals("Bob", t2.text());
    assertEquals("Person.string", t2.type());
    assertEquals(3, result[0].size());
  }
@Test
  public void testIsEscapeSequenceAtStartReturnsFalse() {
    QueryParser parser = new QueryParser();
    boolean result = parser.isEscapeSequence("=a", 0);
    assertFalse(result);
  }
@Test
  public void testIsEscapeSequenceWhenPreviousCharIsNotBackslash() {
    QueryParser parser = new QueryParser();
    boolean result = parser.isEscapeSequence("abc", 2);
    assertFalse(result);
  }
@Test
  public void testIsEscapeSequenceWhenPreviousCharIsBackslash() {
    QueryParser parser = new QueryParser();
    boolean result = parser.isEscapeSequence("a\\=b", 2);
    assertTrue(result);
  }
@Test
  public void testFindIndexOfSkipEscaped() {
    QueryParser parser = new QueryParser();
    int index = parser.findIndexOf("hello\\=world=yes", '=');
    assertEquals(15, index); 
  }
@Test
  public void testFindIndexOfNoMatchReturnsMinusOne() {
    QueryParser parser = new QueryParser();
    int index = parser.findIndexOf("abc", ';');
    assertEquals(-1, index);
  }
@Test
  public void testFindTokensWithTextAfterValidAnnotation() throws SearchException {
    QueryParser parser = new QueryParser();
    List<String> result = parser.findTokens("{Person} extra");
    assertEquals(2, result.size());
    assertEquals("{Person}", result.get(0));
    assertEquals("extra", result.get(1));
  }
@Test
  public void testSplitStringEntryWithEscapedBackslashBeforeSeparator() {
    QueryParser parser = new QueryParser();
    ArrayList<String> result = parser.splitString("a\\\\,b", ',', false);
    assertEquals(2, result.size());
    assertEquals("a\\", result.get(0));
    assertEquals("b", result.get(1));
  } 
}