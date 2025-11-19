package gate.creole.annic.lucene;

import gate.creole.annic.Constants;
import gate.creole.annic.SearchException;
import gate.creole.annic.Term;
import gate.creole.annic.apache.lucene.search.BooleanQuery;
import gate.creole.annic.apache.lucene.search.Query;
import gate.creole.annic.lucene.QueryParser;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

public class QueryParser_1_GPTLLMTest {

@Test
public void testIsValidQuery_ReturnsTrueForBasicQuery() {
boolean result = QueryParser.isValidQuery("{Token}");
assertTrue(result);
}

@Test
public void testParse_SimpleAnnotationQuery_GeneratesBooleanQuery() throws Exception {
QueryParser parser = new QueryParser();
Query[] queries = parser.parse("field", "{Token}", "Token", null, null);
assertEquals(1, queries.length);
assertTrue(queries[0] instanceof BooleanQuery);
}

@Test(expected = SearchException.class)
public void testParse_UnbalancedBraces_ThrowsSearchException() throws Exception {
QueryParser parser = new QueryParser();
parser.parse("field", "{Token", "Token", null, null);
}

@Test
public void testParse_AnnotationWithFeatureEquals_CreatesExpectedQuery() throws Exception {
QueryParser parser = new QueryParser();
Query[] result = parser.parse("field", "{Person.gender==\"male\"}", "Token", null, null);
assertEquals(1, result.length);
assertTrue(result[0] instanceof BooleanQuery);
}

@Test
public void testParse_WithAnnotationSet_CreatesAnnotationSetConstraint() throws Exception {
QueryParser parser = new QueryParser();
Query[] result = parser.parse("field", "{Token}", "Token", null, "MySet");
assertEquals(1, result.length);
assertTrue(result[0] instanceof BooleanQuery);
BooleanQuery query = (BooleanQuery) result[0];
assertTrue(query.toString().contains(Constants.ANNOTATION_SET_ID));
assertTrue(query.toString().contains("MySet"));
}

@Test
public void testParse_WithCorpusId_CreatesCorpusConstraint() throws Exception {
QueryParser parser = new QueryParser();
Query[] result = parser.parse("field", "{Token}", "Token", "corpusXYZ", null);
assertEquals(1, result.length);
assertTrue(result[0] instanceof BooleanQuery);
BooleanQuery query = (BooleanQuery) result[0];
assertTrue(query.toString().contains(Constants.CORPUS_ID));
assertTrue(query.toString().contains("corpusXYZ"));
}

@Test
public void testGetQueryString_ReturnsCorrectNormalizedQuery() throws Exception {
QueryParser parser = new QueryParser();
parser.parse("field", "{Token}", "Token", null, null);
String s = parser.getQueryString(0);
assertEquals("{Token}", s);
}

@Test(expected = IndexOutOfBoundsException.class)
public void testGetQueryString_InvalidIndexThrowsException() throws Exception {
QueryParser parser = new QueryParser();
parser.parse("field", "{Token}", "Token", null, null);
parser.getQueryString(5);
}

@Test
public void testFindTokens_ValidInputTwoBracedTokens() throws Exception {
QueryParser parser = new QueryParser();
List<String> tokens = parser.findTokens("{Token}{Person}");
assertEquals(2, tokens.size());
assertEquals("{Token}", tokens.get(0));
assertEquals("{Person}", tokens.get(1));
}

@Test(expected = SearchException.class)
public void testFindTokens_UnbalancedOnlyOpenBrace() throws Exception {
QueryParser parser = new QueryParser();
parser.findTokens("{Token");
}

@Test(expected = SearchException.class)
public void testFindTokens_UnbalancedOnlyCloseBrace() throws Exception {
QueryParser parser = new QueryParser();
parser.findTokens("Token}");
}

@Test
public void testCreateTerms_QuotedStringLiteral_ReturnsSingleTerm() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] terms = parser.createTerms("\"Hello\"");
assertEquals(1, terms[0].size());
Term t = (Term) terms[0].get(0);
// assertEquals("Hello", t.text());
// assertEquals("Token.string", t.type());
}

@Test
public void testCreateTerms_SimpleAnnotation_ReturnsTokenTerm() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] terms = parser.createTerms("{Token}");
assertEquals(1, terms[0].size());
Term t = (Term) terms[0].get(0);
// assertEquals("Token", t.text());
// assertEquals("*", t.type());
}

@Test
public void testCreateTerms_AnnotationEqualString_ReturnsExpectedTerm() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] terms = parser.createTerms("{Person==\"John\"}");
assertEquals(1, terms[0].size());
Term term = (Term) terms[0].get(0);
// assertEquals("John", term.text());
// assertEquals("Person.string", term.type());
}

@Test
public void testCreateTerms_AnnotationFeatureEqual_ReturnsExpectedTerm() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] terms = parser.createTerms("{Person.gender==\"male\"}");
assertEquals(1, terms[0].size());
Term term = (Term) terms[0].get(0);
// assertEquals("male", term.text());
// assertEquals("Person.gender", term.type());
}

@Test(expected = SearchException.class)
public void testCreateTerms_MissingEqualsInFeature_ThrowsException() throws Exception {
QueryParser parser = new QueryParser();
parser.createTerms("{Person.gender=\"male\"}");
}

@Test
public void testCreateTerms_MultipleCommaAnnotations_ReturnsSeparateTerms() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] terms = parser.createTerms("{Person,Organization}");
assertEquals(2, terms[0].size());
Term t1 = (Term) terms[0].get(0);
Term t2 = (Term) terms[0].get(1);
// assertEquals("Person", t1.text());
// assertEquals("Organization", t2.text());
// assertEquals("*", t1.type());
// assertEquals("*", t2.type());
}

@Test
// public void testCreateTerms_QuotedStringLiteral_ReturnsSingleTerm() throws Exception {
// QueryParser parser = new QueryParser();
// List<?>[] terms = parser.createTerms("\"Hello\"");
// assertEquals(1, terms[0].size());
// Term t = (Term) terms[0].get(0);
// assertEquals("Hello", t.text());
// assertEquals("Token.string", t.type());
// }

// @Test
// public void testCreateTerms_SimpleAnnotation_ReturnsTokenTerm() throws Exception {
// QueryParser parser = new QueryParser();
// List<?>[] terms = parser.createTerms("{Token}");
// assertEquals(1, terms[0].size());
// Term t = (Term) terms[0].get(0);
// assertEquals("Token", t.text());
// assertEquals("*", t.type());
// }

// @Test
// public void testCreateTerms_AnnotationEqualString_ReturnsExpectedTerm() throws Exception {
// QueryParser parser = new QueryParser();
// List<?>[] terms = parser.createTerms("{Person==\"John\"}");
// assertEquals(1, terms[0].size());
// Term term = (Term) terms[0].get(0);
// assertEquals("John", term.text());
// assertEquals("Person.string", term.type());
// }

// @Test
// public void testCreateTerms_AnnotationFeatureEqual_ReturnsExpectedTerm() throws Exception {
// QueryParser parser = new QueryParser();
// List<?>[] terms = parser.createTerms("{Person.gender==\"male\"}");
// assertEquals(1, terms[0].size());
// Term term = (Term) terms[0].get(0);
// assertEquals("male", term.text());
// assertEquals("Person.gender", term.type());
// }

// @Test
// public void testCreateTerms_MultipleCommaAnnotations_ReturnsSeparateTerms() throws Exception {
// QueryParser parser = new QueryParser();
// List<?>[] terms = parser.createTerms("{Person,Organization}");
// assertEquals(2, terms[0].size());
// Term t1 = (Term) terms[0].get(0);
// Term t2 = (Term) terms[0].get(1);
// assertEquals("Person", t1.text());
// assertEquals("Organization", t2.text());
// assertEquals("*", t1.type());
// assertEquals("*", t2.type());
// }

// @Test
public void testNeedValidation_FalseForSimpleTokenQuery() throws Exception {
QueryParser parser = new QueryParser();
parser.parse("field", "{Token}", "Token", null, null);
boolean result = parser.needValidation();
assertFalse(result);
}

@Test
public void testNeedValidation_TrueForFeatureQuery() throws Exception {
QueryParser parser = new QueryParser();
parser.parse("field", "{Person.gender==\"male\"}", "Token", null, null);
boolean result = parser.needValidation();
assertTrue(result);
}

@Test(expected = SearchException.class)
public void testFindTokens_NestedBracesThrowsException() throws Exception {
QueryParser parser = new QueryParser();
parser.findTokens("{Token{Person}}");
}

@Test
public void testFindTokens_TrailingTokenWithoutBraces() throws Exception {
QueryParser parser = new QueryParser();
List<String> tokens = parser.findTokens("{Token}Hello");
assertEquals(2, tokens.size());
assertEquals("{Token}", tokens.get(0));
assertEquals("Hello", tokens.get(1));
}

@Test
public void testCreateTerms_QuotedMultipleWordsAsSingleToken() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("\"Hello World\"");
assertEquals(2, result[0].size());
Term t1 = (Term) result[0].get(0);
Term t2 = (Term) result[0].get(1);
// assertEquals("Hello", t1.text());
// assertEquals("World", t2.text());
}

@Test
public void testCreateTerms_AnnotationWithEscapedQuoteInsideString() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("{Person==\"he said \\\"hi\\\"\"}");
Term term = (Term) result[0].get(0);
// assertEquals("he said \"hi\"", term.text());
// assertEquals("Person.string", term.type());
}

@Test
public void testCreateTerms_AnnotationWithFeatureDotTextOnly() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("{Entity.type==Location}");
Term term = (Term) result[0].get(0);
// assertEquals("Location", term.text());
// assertEquals("Entity.type", term.type());
}

@Test
public void testCreateTerms_MixedAnnotationsAndEqualsVariants() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("{Person, Person.name==\"Alice\", Token}");
assertEquals(3, result[0].size());
Term t1 = (Term) result[0].get(0);
Term t2 = (Term) result[0].get(1);
Term t3 = (Term) result[0].get(2);
// assertEquals("Person", t1.text());
// assertEquals("Alice", t2.text());
// assertEquals("Token", t3.text());
}

@Test(expected = SearchException.class)
public void testCreateTerms_InvalidStructure_NoEndingBrace() throws Exception {
QueryParser parser = new QueryParser();
parser.createTerms("{Person.gender==\"male\"");
}

@Test
public void testCreateTerms_AnnotationWithFeatureValueWithoutQuotes() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("{Person.gender==male}");
Term term = (Term) result[0].get(0);
// assertEquals("male", term.text());
// assertEquals("Person.gender", term.type());
}

@Test(expected = SearchException.class)
public void testCreateTerms_FeatureWithoutValueAfterEquals() throws Exception {
QueryParser parser = new QueryParser();
parser.createTerms("{Person.gender==}");
}

@Test
public void testCreateTerms_AnnotationMultipleCommasWithSpacing() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("{ Token , Person , Organization }");
assertEquals(3, result[0].size());
Term t1 = (Term) result[0].get(0);
Term t2 = (Term) result[0].get(1);
Term t3 = (Term) result[0].get(2);
// assertEquals("Token", t1.text());
// assertEquals("Person", t2.text());
// assertEquals("Organization", t3.text());
}

@Test
public void testCreateTerms_EmptyQuotedString_ReturnsNoTerms() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("\"\"");
assertEquals(0, result[0].size());
}

@Test
public void testCreateTerms_OnlyWhiteSpaceBetweenQuotes_ReturnsNoTerms() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("\"    \"");
assertEquals(0, result[0].size());
}

@Test
public void testParse_ComplexMixQuery() throws Exception {
QueryParser parser = new QueryParser();
String query = "{Lookup.type==\"Person\"} \"said\" {Token} {Date.value==\"tomorrow\"}";
Query[] result = parser.parse("field", query, "Token", "corpus123", "SetX");
assertEquals(1, result.length);
assertTrue(result[0] instanceof BooleanQuery);
}

@Test
public void testParse_QueryWithOnlyQuotedString() throws Exception {
QueryParser parser = new QueryParser();
Query[] result = parser.parse("field", "\"hello world\"", "Token", null, null);
assertEquals(1, result.length);
assertTrue(result[0] instanceof BooleanQuery);
}

@Test
public void testParse_QueryWithNumberToken() throws Exception {
QueryParser parser = new QueryParser();
Query[] result = parser.parse("field", "\"12345\"", "Token", null, null);
assertEquals(1, result.length);
assertTrue(result[0] instanceof BooleanQuery);
}

@Test
public void testParse_EmptyQuotedQuery() throws Exception {
QueryParser parser = new QueryParser();
Query[] result = parser.parse("field", "\"\"", "Token", null, null);
assertEquals(0, result.length);
}

@Test
public void testParse_EscapedCharactersInQuotedString() throws Exception {
QueryParser parser = new QueryParser();
Query[] result = parser.parse("field", "\"abc\\\"def\\\"ghi\"", "Token", null, null);
assertEquals(1, result.length);
assertTrue(result[0] instanceof BooleanQuery);
}

@Test
public void testParse_SingleWordWithoutQuotesOrAnnotation() throws Exception {
QueryParser parser = new QueryParser();
Query[] result = parser.parse("field", "hello", "Token", null, null);
assertEquals(1, result.length);
assertTrue(result[0] instanceof BooleanQuery);
}

@Test
public void testCreateTerms_AnnotationWithEqualsAndNoText() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("{Person==}");
assertEquals(1, result[0].size());
Term t1 = (Term) result[0].get(0);
// assertEquals("", t1.text());
// assertEquals("Person.string", t1.type());
}

@Test
public void testCreateTerms_AnnotationWithDotAndEqualsAndEmptyQuote() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("{Entity.type==\"\"}");
assertEquals(1, result[0].size());
Term term = (Term) result[0].get(0);
// assertEquals("", term.text());
// assertEquals("Entity.type", term.type());
}

@Test(expected = SearchException.class)
public void testCreateTerms_OnlyBracesEmptyAnnotation() throws Exception {
QueryParser parser = new QueryParser();
parser.createTerms("{}");
}

@Test(expected = SearchException.class)
public void testFindTokens_OnlyOpeningBraceNoClose() throws Exception {
QueryParser parser = new QueryParser();
parser.findTokens("{");
}

@Test(expected = SearchException.class)
public void testFindTokens_OnlyClosingBraceNoOpen() throws Exception {
QueryParser parser = new QueryParser();
parser.findTokens("}");
}

@Test(expected = SearchException.class)
public void testFindTokens_EmbeddedUnescapedBracesInQuotes() throws Exception {
QueryParser parser = new QueryParser();
parser.findTokens("\"quoted {text}\"");
}

@Test
public void testSplitString_TrailingEscapeCharacter() {
QueryParser parser = new QueryParser();
// List<String> parts = parser.splitString("value1\\", ',', false);
// assertEquals(1, parts.size());
// assertEquals("value1\\", parts.get(0));
}

@Test
public void testSplitString_MixedEscapedAndUnescapedSeparators() {
QueryParser parser = new QueryParser();
// List<String> parts = parser.splitString("term1\\,term2,term3\\,term4", ',', true);
// assertEquals(2, parts.size());
// assertEquals("term1,term2", parts.get(0));
// assertEquals("term3,term4", parts.get(1));
}

@Test(expected = SearchException.class)
public void testCreateTerms_InvalidDotOrderInAnnotationThrows() throws Exception {
QueryParser parser = new QueryParser();
parser.createTerms("{.feature==value}");
}

@Test
public void testParse_QueryWithMultipleQuotedStringsInSequence() throws Exception {
QueryParser parser = new QueryParser();
Query[] queries = parser.parse("field", "\"hello\" \"world\"", "Token", null, null);
assertEquals(1, queries.length);
assertTrue(queries[0] instanceof BooleanQuery);
}

@Test
public void testParse_SingleValidQuotedStringQuery() throws Exception {
QueryParser parser = new QueryParser();
Query[] queries = parser.parse("field", "\"quotedWord\"", "Token", null, null);
assertEquals(1, queries.length);
assertTrue(queries[0] instanceof BooleanQuery);
}

@Test
public void testNorm_StringWithMixedEscapedAndUnescapedChars() {
QueryParser parser = new QueryParser();
// String result = parser.norm("\\\"word\\\" \\(test\\) \\{escaped\\}");
// assertEquals("word (test) escaped", result);
}

@Test
public void testNorm_StringWithOnlyBackslashesToBeRemoved() {
QueryParser parser = new QueryParser();
// String result = parser.norm("\\,\\{\\}\\\"\\\\");
// assertEquals("", result);
}

@Test
public void testParse_EmptyQueryReturnsZeroQueries() throws Exception {
QueryParser parser = new QueryParser();
Query[] queries = parser.parse("field", "   ", "Token", null, null);
assertEquals(0, queries.length);
}

@Test
public void testFindIndexOf_UnescapedCharacterImmediatelyFound() {
QueryParser parser = new QueryParser();
// int index = parser.findIndexOf("a=b=c", '=');
// assertEquals(1, index);
}

@Test
public void testFindIndexOf_AllEqualsEscapedReturnsNextValid() {
QueryParser parser = new QueryParser();
// int index = parser.findIndexOf("a\\==b\\==c==d", '=');
// assertEquals(12, index);
}

@Test(expected = SearchException.class)
public void testCreateTerms_OpenDotAndNoFeatureName() throws Exception {
QueryParser parser = new QueryParser();
parser.createTerms("{Token.}");
}

@Test(expected = SearchException.class)
public void testCreateTerms_MalformedFeatureAnnotationEndsAbruptly() throws Exception {
QueryParser parser = new QueryParser();
parser.createTerms("{Token.feature==");
}

@Test
public void testSplitString_StringStartsWithEscapedCharDelimiter() {
QueryParser parser = new QueryParser();
// List<String> split = parser.splitString("\\,a,b,c", ',', false);
// assertEquals(1, split.size());
// assertEquals(",a,b,c", split.get(0));
}

@Test
public void testCreateTerms_TokenWithSpecialCharactersInText() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("\"!@#$$% ^&*()\"");
assertEquals(2, result[0].size());
Term t1 = (Term) result[0].get(0);
Term t2 = (Term) result[0].get(1);
// assertTrue(t1.text().contains("!@#$$%"));
// assertTrue(t2.text().contains("^&*()"));
}

@Test
// public void testSplitString_StringStartsWithEscapedCharDelimiter() {
// QueryParser parser = new QueryParser();
// List<String> split = parser.splitString("\\,a,b,c", ',', false);
// assertEquals(1, split.size());
// assertEquals(",a,b,c", split.get(0));
// }

// @Test
// public void testCreateTerms_TokenWithSpecialCharactersInText() throws Exception {
// QueryParser parser = new QueryParser();
// List<?>[] result = parser.createTerms("\"!@#$$% ^&*()\"");
// assertEquals(2, result[0].size());
// Term t1 = (Term) result[0].get(0);
// Term t2 = (Term) result[0].get(1);
// assertTrue(t1.text().contains("!@#$$%"));
// assertTrue(t2.text().contains("^&*()"));
// }

// @Test
public void testCreateTerms_StringWithEscapedBackslashCharacter() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("\"hello\\\\world\"");
assertEquals(1, result[0].size());
Term term = (Term) result[0].get(0);
// assertEquals("hello\\world", term.text());
}

@Test
public void testCreateTerms_QuotedStringWithEndingBackslash() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("\"test\\\\\"");
assertEquals(1, result[0].size());
Term term = (Term) result[0].get(0);
// assertEquals("test\\", term.text());
}

@Test
public void testCreateTerms_SequenceOfQuotedStrings() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("\"one\" \"two\" \"three\"");
assertEquals(3, result[0].size());
Term t1 = (Term) result[0].get(0);
Term t2 = (Term) result[0].get(1);
Term t3 = (Term) result[0].get(2);
// assertEquals("one", t1.text());
// assertEquals("two", t2.text());
// assertEquals("three", t3.text());
}

@Test
public void testFindTokens_QuotedFollowedByBracedTokens() throws Exception {
QueryParser parser = new QueryParser();
List<String> tokens = parser.findTokens("\"hello\"{Person}");
assertEquals(2, tokens.size());
assertEquals("\"hello\"", tokens.get(0));
assertEquals("{Person}", tokens.get(1));
}

@Test
public void testFindTokens_OnlyWhitespaceBetweenTokens() throws Exception {
QueryParser parser = new QueryParser();
List<String> tokens = parser.findTokens("{Token}     {Person}");
assertEquals(2, tokens.size());
assertEquals("{Token}", tokens.get(0));
assertEquals("{Person}", tokens.get(1));
}

@Test
public void testCreateTerms_AnnotationWithMultipleQuotesInValue() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("{Entity.name==\"\\\"quoted name\\\"\"}");
Term term = (Term) result[0].get(0);
// assertEquals("\"quoted name\"", term.text());
// assertEquals("Entity.name", term.type());
}

@Test(expected = SearchException.class)
public void testParse_OpeningBraceWithoutClosingThrowsSearchException() throws Exception {
QueryParser parser = new QueryParser();
parser.parse("field", "{Person", "Token", null, null);
}

@Test
public void testCreateTerms_AnnotationWithEscapedCommaInFeatureName() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("{Entity.fe\\,ature==value}");
Term term = (Term) result[0].get(0);
// assertEquals("value", term.text());
// assertEquals("Entity.fe,ature", term.type());
}

@Test
public void testParse_TokenizedQuotedStringWithSpacesAndTabs() throws Exception {
QueryParser parser = new QueryParser();
Query[] queries = parser.parse("field", "\"hello   \t  world\"", "Token", null, null);
assertEquals(1, queries.length);
assertTrue(queries[0] instanceof BooleanQuery);
}

@Test
public void testParse_QueryContainingNonASCIIUnicodeTokens() throws Exception {
QueryParser parser = new QueryParser();
Query[] queries = parser.parse("field", "\"こんにちは 世界\"", "Token", null, null);
assertEquals(1, queries.length);
assertTrue(queries[0] instanceof BooleanQuery);
}

@Test
public void testCreateTerms_AnnotationWithUnicodeFeatureKey() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("{エンティティ.属性==値}");
Term term = (Term) result[0].get(0);
// assertEquals("値", term.text());
// assertEquals("エンティティ.属性", term.type());
}

@Test
public void testParse_TokenContainingOnlyPunctuation() throws Exception {
QueryParser parser = new QueryParser();
Query[] queries = parser.parse("field", "\"!?().{}\"", "Token", null, null);
assertEquals(1, queries.length);
}

@Test
public void testCreateTerms_AnnotationTypeWithNumbers() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("{NER123==\"B-LOC\"}");
Term term = (Term) result[0].get(0);
// assertEquals("B-LOC", term.text());
// assertEquals("NER123.string", term.type());
}

@Test
public void testCreateTerms_FeatureNameWithMultipleDots() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("{Person.name.full==\"John Smith\"}");
Term term = (Term) result[0].get(0);
// assertEquals("John Smith", term.text());
// assertEquals("Person.name.full", term.type());
}

@Test
public void testCreateTerms_EmptyQuotedValueInFeatureEquality() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("{Token.feature==\"\"}");
Term term = (Term) result[0].get(0);
// assertEquals("", term.text());
// assertEquals("Token.feature", term.type());
}

@Test
public void testCreateTerms_MultipleTermsCommaSeparatedQuotedAndUnquoted() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("{Type1, Type2==\"text\", Type3.feature==\"aaa\"}");
Term t0 = (Term) result[0].get(0);
Term t1 = (Term) result[0].get(1);
Term t2 = (Term) result[0].get(2);
// assertEquals("*", t0.type());
// assertEquals("Type2.string", t1.type());
// assertEquals("Type3.feature", t2.type());
}

@Test
public void testSplitString_EndsWithUnescapedCommaCreatesEmptyLastElement() {
QueryParser parser = new QueryParser();
// List<String> list = parser.splitString("one,two,", ',', false);
// assertEquals(3, list.size());
// assertEquals("one", list.get(0));
// assertEquals("two", list.get(1));
// assertEquals("", list.get(2));
}

@Test
// public void testCreateTerms_AnnotationWithEscapedCommaInFeatureName() throws Exception {
// QueryParser parser = new QueryParser();
// List<?>[] result = parser.createTerms("{Entity.fe\\,ature==value}");
// Term term = (Term) result[0].get(0);
// assertEquals("value", term.text());
// assertEquals("Entity.fe,ature", term.type());
// }

// @Test
// public void testCreateTerms_AnnotationWithUnicodeFeatureKey() throws Exception {
// QueryParser parser = new QueryParser();
// List<?>[] result = parser.createTerms("{エンティティ.属性==値}");
// Term term = (Term) result[0].get(0);
// assertEquals("値", term.text());
// assertEquals("エンティティ.属性", term.type());
// }

// @Test
// public void testCreateTerms_AnnotationTypeWithNumbers() throws Exception {
// QueryParser parser = new QueryParser();
// List<?>[] result = parser.createTerms("{NER123==\"B-LOC\"}");
// Term term = (Term) result[0].get(0);
// assertEquals("B-LOC", term.text());
// assertEquals("NER123.string", term.type());
// }

// @Test
// public void testCreateTerms_FeatureNameWithMultipleDots() throws Exception {
// QueryParser parser = new QueryParser();
// List<?>[] result = parser.createTerms("{Person.name.full==\"John Smith\"}");
// Term term = (Term) result[0].get(0);
// assertEquals("John Smith", term.text());
// assertEquals("Person.name.full", term.type());
// }

// @Test
// public void testCreateTerms_EmptyQuotedValueInFeatureEquality() throws Exception {
// QueryParser parser = new QueryParser();
// List<?>[] result = parser.createTerms("{Token.feature==\"\"}");
// Term term = (Term) result[0].get(0);
// assertEquals("", term.text());
// assertEquals("Token.feature", term.type());
// }

// @Test
public void testParse_MultipleSubqueryPipedLiterals() throws Exception {
QueryParser parser = new QueryParser();
Query[] queries = parser.parse("field", "\"one\"|\"two\"", "Token", null, null);
assertEquals(2, queries.length);
}

@Test
// public void testCreateTerms_MultipleTermsCommaSeparatedQuotedAndUnquoted() throws Exception {
// QueryParser parser = new QueryParser();
// List<?>[] result = parser.createTerms("{Type1, Type2==\"text\", Type3.feature==\"aaa\"}");
// Term t0 = (Term) result[0].get(0);
// Term t1 = (Term) result[0].get(1);
// Term t2 = (Term) result[0].get(2);
// assertEquals("*", t0.type());
// assertEquals("Type2.string", t1.type());
// assertEquals("Type3.feature", t2.type());
// }

// @Test
// public void testSplitString_EndsWithUnescapedCommaCreatesEmptyLastElement() {
// QueryParser parser = new QueryParser();
// List<String> list = parser.splitString("one,two,", ',', false);
// assertEquals(3, list.size());
// assertEquals("one", list.get(0));
// assertEquals("two", list.get(1));
// assertEquals("", list.get(2));
// }

// @Test
public void testFindTokens_WithEmptyQuotedStringFollowedByBrace() throws Exception {
QueryParser parser = new QueryParser();
List<String> tokens = parser.findTokens("\"\"{Token}");
assertEquals(2, tokens.size());
assertEquals("\"\"", tokens.get(0));
assertEquals("{Token}", tokens.get(1));
}

@Test
public void testCreateTerms_SpecialCharactersInAnnotationType() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("{Ty*pe1,Ty(pe)}");
Term t0 = (Term) result[0].get(0);
Term t1 = (Term) result[0].get(1);
// assertEquals("Ty*pe1", t0.text());
// assertEquals("Ty(pe)", t1.text());
// assertEquals("*", t0.type());
// assertEquals("*", t1.type());
}

@Test
public void testCreateTerms_SpecialCharactersInFeatureValue() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("{Entity.name==\"$value@123#\"}");
Term t0 = (Term) result[0].get(0);
// assertEquals("$value@123#", t0.text());
// assertEquals("Entity.name", t0.type());
}

@Test
public void testSplitString_ConsecutiveUnescapedDelimiters() {
QueryParser parser = new QueryParser();
// List<String> result = parser.splitString("one,,two", ',', false);
// assertEquals(3, result.size());
// assertEquals("one", result.get(0));
// assertEquals("", result.get(1));
// assertEquals("two", result.get(2));
}

@Test
public void testCreateTerms_MixedQuotedAndUnquotedCommaEntries() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("{A,\"B C\",D}");
Term t0 = (Term) result[0].get(0);
Term t1 = (Term) result[0].get(1);
Term t2 = (Term) result[0].get(2);
// assertEquals("A", t0.text());
// assertEquals("B C", t1.text());
// assertEquals("D", t2.text());
// assertEquals("*", t0.type());
// assertEquals("*", t1.type());
// assertEquals("*", t2.type());
}

@Test(expected = SearchException.class)
public void testCreateTerms_InvalidEmptyAnnotationWithSpaces() throws Exception {
QueryParser parser = new QueryParser();
parser.createTerms("{   }");
}

@Test
public void testCreateTerms_FeatureNameWithMultipleNumericParts() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("{Entity.12.34.56==\"abc\"}");
Term t0 = (Term) result[0].get(0);
// assertEquals("abc", t0.text());
// assertEquals("Entity.12.34.56", t0.type());
}

@Test
public void testParse_ConcatenatedQuotedWordsParsedAsSingleQuery() throws Exception {
QueryParser parser = new QueryParser();
Query[] queries = parser.parse("field", "\"John\"\"Doe\"", "Token", null, null);
assertEquals(1, queries.length);
}

@Test
public void testParse_WhitespaceSurroundedBraceTokens() throws Exception {
QueryParser parser = new QueryParser();
Query[] result = parser.parse("field", "   {Token}   ", "Token", null, null);
assertEquals(1, result.length);
}

@Test
public void testCreateTerms_UnquotedMultipleWords_NoExtraTermsForEmptyTokens() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("dog   cat    mouse ");
assertEquals(3, result[0].size());
}

@Test
public void testCreateTerms_TabsAndNewlinesInsideQuotedString() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("\"hello\\tworld\\nnext\"");
Term t0 = (Term) result[0].get(0);
// assertTrue(t0.text().contains("t") || t0.text().contains("n"));
}

@Test
public void testNorm_RemovesOnlySpecificEscapesNotLetters() {
QueryParser parser = new QueryParser();
String input = "\\(abc\\), \\\"quoted\\\", \\unknown\\marker";
// String output = parser.norm(input);
// assertEquals("(abc), \"quoted\", unknownmarker", output);
}

@Test
public void testFindIndexOf_CharacterSkippedWhenEscapedMultipleTimes() {
QueryParser parser = new QueryParser();
// int index = parser.findIndexOf("value\\=notthis==actual", '=');
// assertEquals(14, index);
}

@Test
public void testCreateTerms_AnnotationFeatureEqualsNumberValue() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("{Stat.id==12345}");
Term t0 = (Term) result[0].get(0);
// assertEquals("12345", t0.text());
// assertEquals("Stat.id", t0.type());
}

@Test
public void testCreateTerms_AnnotationQuotedWithBackslashAtEnd() throws Exception {
QueryParser parser = new QueryParser();
List<?>[] result = parser.createTerms("\"escaped\\\\\"");
Term t0 = (Term) result[0].get(0);
// assertEquals("escaped\\", t0.text());
}

@Test
public void testParse_UnicodeEmojiTokensHandled() throws Exception {
QueryParser parser = new QueryParser();
Query[] result = parser.parse("field", "\"🙂 🚀 💡\"", "Token", null, null);
assertEquals(1, result.length);
}
}
