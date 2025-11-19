package gate.creole.annic.lucene;

import gate.creole.annic.Constants;
import gate.creole.annic.SearchException;
import gate.creole.annic.Term;
import gate.creole.annic.apache.lucene.search.BooleanQuery;
import gate.creole.annic.apache.lucene.search.Query;
import gate.creole.annic.apache.lucene.search.TermQuery;
import gate.creole.annic.lucene.QueryParser;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class QueryParser_2_GPTLLMTest {

    @Test
    public void testIsValidQueryWithValidInput() {
        String query = "\"hello\" {Token} {Person.gender==\"male\"}";
        boolean result = QueryParser.isValidQuery(query);
        assertTrue(result);
    }

    @Test
    public void testIsValidQueryWithInvalidInput() {
        String query = "\"hello\" {Token {Person.gender==\"male\"}";
        boolean result = QueryParser.isValidQuery(query);
        assertFalse(result);
    }

    @Test
    public void testParseSimplePhraseQueryWithoutCorpusIDOrAnnotationSet() throws Exception {
        QueryParser parser = new QueryParser();
        String queryStr = "\"hello world\"";
        Query[] queries = parser.parse("content", queryStr, "Token", null, null);

        assertNotNull(queries);
        assertEquals(1, queries.length);
        assertTrue(queries[0] instanceof BooleanQuery);

        BooleanQuery booleanQuery = (BooleanQuery) queries[0];
        Query clause1 = booleanQuery.getClauses()[0].getQuery();
        Query clause2 = booleanQuery.getClauses()[1].getQuery();

        boolean clause1IsTermQuery = clause1 instanceof TermQuery;
        boolean clause2IsTermQuery = clause2 instanceof TermQuery;


        boolean foundCombinedSet = false;

        if (clause1IsTermQuery) {
            gate.creole.annic.apache.lucene.index.Term term = ((TermQuery) clause1).getTerm();
            if (Constants.ANNOTATION_SET_ID.equals(term.type()) &&
                    Constants.COMBINED_SET.equals(term.text())) {
                foundCombinedSet = true;
            }
        }

        if (clause2IsTermQuery) {
            gate.creole.annic.apache.lucene.index.Term term = ((TermQuery) clause2).getTerm();
            if (Constants.ANNOTATION_SET_ID.equals(term.type()) &&
                    Constants.COMBINED_SET.equals(term.text())) {
                foundCombinedSet = true;
            }
        }

        assertTrue(foundCombinedSet);
    }

    @Test
    public void testCreateTermsWithSimpleAnnotation() throws Exception {
        QueryParser parser = new QueryParser();
        List<?>[] result = parser.createTerms("{Person}");

        assertNotNull(result);
        assertEquals(3, result.length);
        assertEquals(1, result[0].size());
        assertTrue(result[0].get(0) instanceof Term);

        Term term = (Term) result[0].get(0);
        assertEquals("Person", term.text());
        assertEquals("*", term.type());
    }

    @Test
    public void testCreateTermsWithAnnotationAndFeature() throws Exception {
        QueryParser parser = new QueryParser();
        List<?>[] result = parser.createTerms("{Person.gender==\"male\"}");

        assertNotNull(result);
        assertEquals(3, result.length);
        assertEquals(1, result[0].size());

        Term term = (Term) result[0].get(0);
        assertEquals("male", term.text());
        assertEquals("Person.gender", term.type());
    }

    @Test(expected = SearchException.class)
    public void testCreateTermsMissingEqualsOperatorThrowsException() throws Exception {
        QueryParser parser = new QueryParser();
        parser.createTerms("{Person.gender=\"male\"}");
    }

    @Test
    public void testNormRemovesBackslashesBeforeSpecialChars() {
        QueryParser parser = new QueryParser();
        String raw = "\\{escape\\}\\\"test\\\"";
        String norm = parser.norm(raw);
        assertEquals("{escape}\"test\"", norm);
    }

    @Test
    public void testSplitStringHandlesUnescapedDelimiter() {
        QueryParser parser = new QueryParser();
        List<String> result = parser.splitString("Token,Location", ',', false);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Token", result.get(0));
        assertEquals("Location", result.get(1));
    }

    @Test
    public void testSplitStringKeepsEscapedCommasTogether() {
        QueryParser parser = new QueryParser();
        List<String> result = parser.splitString("Feature==\"male\\,strong\",Other==\"weak\"", ',', false);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).contains("\\,"));
    }

    @Test
    public void testFindIndexOfWithEscapedCharReturnsNegative() {
        QueryParser parser = new QueryParser();
        String input = "Person\\.gender==\"male\"";
        int index = parser.findIndexOf(input, '.');
        assertEquals(-1, index);
    }

    @Test
    public void testFindIndexOfWithUnescapedCharReturnsValidIndex() {
        QueryParser parser = new QueryParser();
        String input = "Person.gender==\"male\"";
        int index = parser.findIndexOf(input, '.');
        assertTrue(index >= 0);
    }

    @Test
    public void testCreateTermsWithMultipleAnnotationsSeparatedByComma() throws Exception {
        QueryParser parser = new QueryParser();
        List<?>[] result = parser.createTerms("{Person, Location}");

        assertNotNull(result);
        assertEquals(2, result[0].size());

        Term t0 = (Term) result[0].get(0);
        Term t1 = (Term) result[0].get(1);

        assertEquals("Person", t0.text());
        assertEquals("Location", t1.text());
    }

    @Test
    public void testParseSimplePhraseQueryWithoutCorpusIDOrAnnotationSet() throws Exception {
        QueryParser parser = new QueryParser();
        String queryStr = "\"hello world\"";
        Query[] queries = parser.parse("content", queryStr, "Token", null, null);

        assertNotNull(queries);
        assertEquals(1, queries.length);
        assertTrue(queries[0] instanceof BooleanQuery);

        BooleanQuery booleanQuery = (BooleanQuery) queries[0];
        Query clause1 = booleanQuery.getClauses()[0].getQuery();
        Query clause2 = booleanQuery.getClauses()[1].getQuery();

        boolean clause1IsTermQuery = clause1 instanceof TermQuery;
        boolean clause2IsTermQuery = clause2 instanceof TermQuery;


        boolean foundCombinedSet = false;

        if (clause1IsTermQuery) {
            Term term = ((TermQuery) clause1).getTerm();
            if (Constants.ANNOTATION_SET_ID.equals(term.type()) &&
                    Constants.COMBINED_SET.equals(term.text())) {
                foundCombinedSet = true;
            }
        }

        if (clause2IsTermQuery) {
            Term term = ((TermQuery) clause2).getTerm();
            if (Constants.ANNOTATION_SET_ID.equals(term.type()) &&
                    Constants.COMBINED_SET.equals(term.text())) {
                foundCombinedSet = true;
            }
        }

        assertTrue(foundCombinedSet);
    }

    @Test
    public void testParseWithCorpusIdAndAnnotationSet() throws Exception {
        QueryParser parser = new QueryParser();
        Query[] queries = parser.parse("content", "{Token}", "Token", "Corpus123", "AS1");

        assertNotNull(queries);
        assertEquals(1, queries.length);
        assertTrue(queries[0] instanceof BooleanQuery);
        BooleanQuery bq = (BooleanQuery) queries[0];

        assertEquals(3, bq.getClauses().length);
    }

    @Test
    public void testFindTokensWithWellFormedInput() throws Exception {
        QueryParser parser = new QueryParser();
        List<String> tokens = parser.findTokens("{Person} \"said\" {Person.gender==\"male\"}");

        assertNotNull(tokens);
        assertEquals(3, tokens.size());

        assertEquals("{Person}", tokens.get(0));
        assertEquals("\"said\"", tokens.get(1));
        assertEquals("{Person.gender==\"male\"}", tokens.get(2));
    }

    @Test(expected = SearchException.class)
    public void testFindTokensMissingClosingBraceThrowsException() throws Exception {
        QueryParser parser = new QueryParser();
        parser.findTokens("{Person} {Token {Person.gender==\"male\"}");
    }

    @Test(expected = SearchException.class)
    public void testFindTokensExtraClosingBraceThrowsException() throws Exception {
        QueryParser parser = new QueryParser();
        parser.findTokens("{Person} {Person.gender==\"male\"}}");
    }

    @Test
    public void testCreateTermsWithSimpleAnnotation() throws Exception {
        QueryParser parser = new QueryParser();
        List<?>[] result = parser.createTerms("{Person}");

        assertNotNull(result);
        assertEquals(3, result.length);
        assertEquals(1, result[0].size());
        assertTrue(result[0].get(0) instanceof Term);

        Term term = (Term) result[0].get(0);
        assertEquals("Person", term.text());
        assertEquals("*", term.type());
    }

    @Test
    public void testCreateTermsWithAnnotationAndFeature() throws Exception {
        QueryParser parser = new QueryParser();
        List<?>[] result = parser.createTerms("{Person.gender==\"male\"}");

        assertNotNull(result);
        assertEquals(3, result.length);
        assertEquals(1, result[0].size());

        Term term = (Term) result[0].get(0);
        assertEquals("male", term.text());
        assertEquals("Person.gender", term.type());
    }

    @Test
    public void testNormRemovesBackslashesBeforeSpecialChars() {
        QueryParser parser = new QueryParser();
        String raw = "\\{escape\\}\\\"test\\\"";
        String norm = parser.norm(raw);
        assertEquals("{escape}\"test\"", norm);
    }

    @Test
    public void testSplitStringHandlesUnescapedDelimiter() {
        QueryParser parser = new QueryParser();
        List<String> result = parser.splitString("Token,Location", ',', false);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Token", result.get(0));
        assertEquals("Location", result.get(1));
    }

    @Test
    public void testSplitStringKeepsEscapedCommasTogether() {
        QueryParser parser = new QueryParser();
        List<String> result = parser.splitString("Feature==\"male\\,strong\",Other==\"weak\"", ',', false);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).contains("\\,"));
    }

    @Test
    public void testFindIndexOfWithEscapedCharReturnsNegative() {
        QueryParser parser = new QueryParser();
        String input = "Person\\.gender==\"male\"";
        int index = parser.findIndexOf(input, '.');
        assertEquals(-1, index);
    }

    @Test
    public void testFindIndexOfWithUnescapedCharReturnsValidIndex() {
        QueryParser parser = new QueryParser();
        String input = "Person.gender==\"male\"";
        int index = parser.findIndexOf(input, '.');
        assertTrue(index >= 0);
    }

    @Test
    public void testCreateTermsWithMultipleAnnotationsSeparatedByComma() throws Exception {
        QueryParser parser = new QueryParser();
        List<?>[] result = parser.createTerms("{Person, Location}");

        assertNotNull(result);
        assertEquals(2, result[0].size());

        Term t0 = (Term) result[0].get(0);
        Term t1 = (Term) result[0].get(1);

        assertEquals("Person", t0.text());
        assertEquals("Location", t1.text());
    }

    @Test
    public void testNeedValidationFlagReturnsFalseForBaseTokenOnly() throws Exception {
        QueryParser parser = new QueryParser();
        parser.parse("field", "{Token},{Token}", "Token", null, null);
        assertFalse(parser.needValidation());
    }

    @Test
    public void testNeedValidationFlagReturnsTrueWithMixedTokens() throws Exception {
        QueryParser parser = new QueryParser();
        parser.parse("field", "{Person.gender==\"male\"}", "Token", null, null);
        assertTrue(parser.needValidation());
    }

    @Test
    public void testCreateTermsWithQuotedAnnotationValue() throws Exception {
        QueryParser parser = new QueryParser();
        List<?>[] result = parser.createTerms("{Person==\"he said\"}");

        assertEquals(1, result[0].size());
        Term term = (Term) result[0].get(0);
        assertEquals("he said", term.text());
        assertEquals("Person.string", term.type());
    }

    @Test
    public void testCreateTermsWithMultipleTypeAndFeatureMix() throws Exception {
        QueryParser parser = new QueryParser();
        List<?>[] result = parser.createTerms("{Person, Person.gender==\"male\", Location==\"Paris\"}");

        assertEquals(3, result[0].size());

        Term t0 = (Term) result[0].get(0);
        Term t1 = (Term) result[0].get(1);
        Term t2 = (Term) result[0].get(2);

        assertEquals("Person", t0.text());
        assertEquals("*", t0.type());

        assertEquals("male", t1.text());
        assertEquals("Person.gender", t1.type());

        assertEquals("Paris", t2.text());
        assertEquals("Location.string", t2.type());
    }

    @Test(expected = SearchException.class)
    public void testCreateTermsRaisesMissingDotBeforeEquals() throws Exception {
        QueryParser parser = new QueryParser();
        parser.createTerms("{Token feature==\"value\"}");
    }

    @Test(expected = SearchException.class)
    public void testCreateTermsRaisesExceptionOnBadSyntaxNoQuotes() throws Exception {
        QueryParser parser = new QueryParser();
        parser.createTerms("{Person.gender==male}");
    }

    @Test
    public void testParseHandlesSingleQuotedStringOnly() throws Exception {
        QueryParser parser = new QueryParser();
        Query[] queries = parser.parse("field", "\"hello\"", "Token", null, null);

        assertNotNull(queries);
        assertEquals(1, queries.length);
    }

    @Test
    public void testCreateTermsWithBackslashesInQuotedString() throws Exception {
        QueryParser parser = new QueryParser();
        List<?>[] result = parser.createTerms("\"he said \\\"hello\\\"\"");

        assertEquals(1, result[0].size());
        Term term = (Term) result[0].get(0);
        assertEquals("he said \"hello\"", term.text());
    }

    @Test
    public void testIsBaseTokenTermFalseForWrongField() {
        QueryParser parser = new QueryParser();
        Term term = new Term("field", "Person", "NotToken");
        assertFalse(parser.isBaseTokenTerm(term));
    }

    @Test
    public void testParseWithEmptyQueryReturnsEmptyResult() throws Exception {
        QueryParser parser = new QueryParser();
        Query[] result = parser.parse("field", "", "Token", null, null);

        assertNotNull(result);
        assertEquals(0, result.length);
    }

    @Test(expected = SearchException.class)
    public void testFindTokensUnescapedExtraOpeningBraceInMiddle() throws Exception {
        QueryParser parser = new QueryParser();
        parser.findTokens("{Person} {Token} { {Gender==\"male\"}");
    }

    @Test
    public void testCreateTermsWithMultipleSeparatedQuotedStrings() throws Exception {
        QueryParser parser = new QueryParser();
        List<?>[] result = parser.createTerms("\"John said Hello\"");

        assertEquals(3, result[0].size());

        Term t0 = (Term) result[0].get(0);
        assertEquals("John", t0.text());

        Term t1 = (Term) result[0].get(1);
        assertEquals("said", t1.text());

        Term t2 = (Term) result[0].get(2);
        assertEquals("Hello", t2.text());
    }

    @Test
    public void testCreateTermsWithEmptyFeatureValue() throws Exception {
        QueryParser parser = new QueryParser();
        List<?>[] result = parser.createTerms("{Person.gender==\"\"}");

        assertEquals(1, result[0].size());
        Term term = (Term) result[0].get(0);
        assertEquals("", term.text());
        assertEquals("Person.gender", term.type());
    }

    @Test
    public void testSplitStringHandlesEscapedBackslash() {
        QueryParser parser = new QueryParser();
        List<String> result = parser.splitString("Token\\\\,Location", ',', false);

        assertEquals(2, result.size());
        assertEquals("Token\\\\", result.get(0));
        assertEquals("Location", result.get(1));
    }

    @Test
    public void testParseWithAnnotationSetOnly() throws Exception {
        QueryParser parser = new QueryParser();
        Query[] queries = parser.parse("field", "{Token}", "Token", null, "MyAnnotationSet");

        assertEquals(1, queries.length);
        assertTrue(queries[0] instanceof BooleanQuery);
        BooleanQuery bq = (BooleanQuery) queries[0];
        assertEquals(2, bq.getClauses().length);
    }

    @Test
    public void testParseWithCorpusOnly() throws Exception {
        QueryParser parser = new QueryParser();
        Query[] queries = parser.parse("field", "{Token}", "Token", "corpus-id", null);

        assertEquals(1, queries.length);
        assertTrue(queries[0] instanceof BooleanQuery);

        BooleanQuery bq = (BooleanQuery) queries[0];
        assertEquals(2, bq.getClauses().length);
    }

    @Test(expected = SearchException.class)
    public void testCreateTermsThrowsIndexOutBoundsForEmptyCurly() throws Exception {
        QueryParser parser = new QueryParser();
        parser.createTerms("{}");
    }

    @Test
    public void testCreateTermsWithDoubleEscapeCharacters() throws Exception {
        QueryParser parser = new QueryParser();
        List<?>[] result = parser.createTerms("\"escaped\\\\string\"");

        Term term = (Term) result[0].get(0);
        assertEquals("escaped\\string", term.text());
    }

    @Test
    public void testCreateTermsWithDotInQuotedString() throws Exception {
        QueryParser parser = new QueryParser();
        List<?>[] result = parser.createTerms("\"Mr. Smith\"");

        assertEquals(2, result[0].size());
        Term t0 = (Term) result[0].get(0);
        Term t1 = (Term) result[0].get(1);

        assertEquals("Mr.", t0.text());
        assertEquals("Smith", t1.text());
    }

    @Test
    public void testCreateTermsWithEmptyQuotedStringLiteral() throws Exception {
        QueryParser parser = new QueryParser();
        List<?>[] result = parser.createTerms("\"\"");
        assertEquals(0, result[0].size());
    }

    @Test
    public void testCreateTermsWithStringWithOnlySpaces() throws Exception {
        QueryParser parser = new QueryParser();
        List<?>[] result = parser.createTerms("\"   \"");
        assertEquals(0, result[0].size());
    }

    @Test
    public void testCreateTermsWithQuotedStringContainingEscapedDoubleQuotes() throws Exception {
        QueryParser parser = new QueryParser();
        List<?>[] result = parser.createTerms("\"he said \\\"hello\\\"\"");
        assertEquals(3, result[0].size());
        Term term0 = (Term) result[0].get(0);
        Term term1 = (Term) result[0].get(1);
        Term term2 = (Term) result[0].get(2);
        assertEquals("he", term0.text());
        assertEquals("said", term1.text());
        assertEquals("\"hello\"", term2.text());
    }

    @Test(expected = SearchException.class)
    public void testCreateTermsMissingAnnotationTypeThrowsException() throws Exception {
        QueryParser parser = new QueryParser();
        parser.createTerms("{==\"value\"}");
    }

    @Test(expected = SearchException.class)
    public void testCreateTermsOnlyFeatureWithNoTypeThrowsException() throws Exception {
        QueryParser parser = new QueryParser();
        parser.createTerms("{.feature==\"val\"}");
    }

    @Test
    public void testSplitStringWithNoSeparatorReturnsFullString() {
        QueryParser parser = new QueryParser();
        List<String> result = parser.splitString("OnlyOneField", ',', false);
        assertEquals(1, result.size());
        assertEquals("OnlyOneField", result.get(0));
    }

    @Test
    public void testSplitStringWithTrailingEscapeChar() {
        QueryParser parser = new QueryParser();
        List<String> result = parser.splitString("Test\\", ',', false);
        assertEquals(1, result.size());
        assertEquals("Test\\", result.get(0));
    }

    @Test
    public void testFindIndexOfWithMultipleEscapedAndUnescapedTargets() {
        QueryParser parser = new QueryParser();
        String input = "escaped\\.dot.normal.dot";
        int index = parser.findIndexOf(input, '.');
        assertTrue(index > "escaped\\.dot".length());
    }

    @Test
    public void testNormWithEscapeBeforeNonSpecialCharGetsPreserved() {
        QueryParser parser = new QueryParser();
        String normalized = parser.norm("a\\b");
        assertEquals("a\\b", normalized);
    }

    @Test
    public void testCreateTermsWithFeatureContainingDotInValue() throws Exception {
        QueryParser parser = new QueryParser();
        List<?>[] result = parser.createTerms("{Entity.type==\"person.name\"}");
        assertEquals(1, result[0].size());
        Term term = (Term) result[0].get(0);
        assertEquals("person.name", term.text());
        assertEquals("Entity.type", term.type());
    }

    @Test
    public void testCreateTermsWithMultipleMixedTokensWithQuotesAndTypes() throws Exception {
        QueryParser parser = new QueryParser();
        List<?>[] result = parser.createTerms("{Entity},\"said\",{Entity.type==\"person\"}");
        assertEquals(3, result[0].size());
        Term t0 = (Term) result[0].get(0);
        Term t1 = (Term) result[0].get(1);
        Term t2 = (Term) result[0].get(2);
        assertEquals("Entity", t0.text());
        assertEquals("*", t0.type());
        assertEquals("said", t1.text());
        assertEquals("Token.string", t1.type());
        assertEquals("person", t2.text());
        assertEquals("Entity.type", t2.type());
    }

    @Test
    public void testCreateTermsWithFeatureNameContainingUnderscore() throws Exception {
        QueryParser parser = new QueryParser();
        List<?>[] result = parser.createTerms("{Entity.person_type==\"actor\"}");
        assertEquals(1, result[0].size());
        Term t = (Term) result[0].get(0);
        assertEquals("actor", t.text());
        assertEquals("Entity.person_type", t.type());
    }

    @Test
    public void testFindTokensComplexMixedFormat() throws Exception {
        QueryParser parser = new QueryParser();
        List<String> tokens = parser.findTokens("{A==\"one\"} \"middle\" {B.feature==\"val\"}");
        assertEquals(3, tokens.size());
        assertEquals("{A==\"one\"}", tokens.get(0));
        assertEquals("\"middle\"", tokens.get(1));
        assertEquals("{B.feature==\"val\"}", tokens.get(2));
    }

    @Test
    public void testParseWithComplexAnnotationSequence() throws Exception {
        QueryParser parser = new QueryParser();
        Query[] queries = parser.parse("text", "{Token} {Entity.type==\"person\"} \"says\"", "Token", null, null);
        assertEquals(1, queries.length);
        assertTrue(queries[0] instanceof gate.creole.annic.apache.lucene.search.BooleanQuery);
    }

    @Test
    public void testSplitStringHandlesMultipleEscapedSeparators() {
        QueryParser parser = new QueryParser();
        List<String> result = parser.splitString("a\\,b\\,c,d", ',', false);
        assertEquals(2, result.size());
        assertEquals("a\\,b\\,c", result.get(0));
        assertEquals("d", result.get(1));
    }

    @Test(expected = SearchException.class)
    public void testParseWithUnbalancedQuote() throws Exception {
        QueryParser parser = new QueryParser();
        parser.parse("field", "\"unclosed", "Token", null, null);
    }

    @Test(expected = SearchException.class)
    public void testCreateTermsWithSingleOpenBraceOnly() throws Exception {
        QueryParser parser = new QueryParser();
        parser.createTerms("{");
    }

    @Test(expected = SearchException.class)
    public void testCreateTermsWithSingleClosingBraceOnly() throws Exception {
        QueryParser parser = new QueryParser();
        parser.createTerms("}");
    }

    @Test(expected = SearchException.class)
    public void testCreateTermsWithMismatchedBracesAroundText() throws Exception {
        QueryParser parser = new QueryParser();
        parser.createTerms("{Entity");
    }

    @Test
    public void testCreateTermsWithTokenHavingDigitsAndSymbols() throws Exception {
        QueryParser parser = new QueryParser();
        List<?>[] result = parser.createTerms("\"$500.00 is 100% legit\"");
        assertNotNull(result);
        assertEquals(4, result[0].size());
        Term t0 = (Term) result[0].get(0);
        assertEquals("$500.00", t0.text());
        Term t3 = (Term) result[0].get(3);
        assertEquals("legit", t3.text());
    }

    @Test
    public void testSplitStringWithOnlyEscapedSeparators() {
        QueryParser parser = new QueryParser();
        List<String> result = parser.splitString("Token\\,Type\\,Gender", ',', false);
        assertEquals(1, result.size());
        assertEquals("Token\\,Type\\,Gender", result.get(0));
    }

    @Test
    public void testNormSkipsOnlySupportedEscapes() {
        QueryParser parser = new QueryParser();
        String input = "\\n\\t\\,\\.";
        String result = parser.norm(input);
        assertEquals("\\n\\t", result);
    }

    @Test
    public void testCreateTermsAcceptsExtraWhitespaceAroundBraces() throws Exception {
        QueryParser parser = new QueryParser();
        List<?>[] result = parser.createTerms("   {  Person   }   ");
        Term term = (Term) result[0].get(0);
        assertEquals("Person", term.text());
    }

    @Test
    public void testNormHandlesEmptyOrNullInputGracefully() {
        QueryParser parser = new QueryParser();
        String result1 = parser.norm("");
        assertEquals("", result1);
        String result2 = parser.norm(" ");
        assertEquals(" ", result2);
    }

    @Test
    public void testSplitStringHandlesEmptyString() {
        QueryParser parser = new QueryParser();
        List<String> result = parser.splitString("", ',', true);
        assertEquals(0, result.size());
    }

    @Test
    public void testSplitStringWithMultipleCommas() {
        QueryParser parser = new QueryParser();
        List<String> result = parser.splitString("Token,Type,Gender", ',', false);
        assertEquals(3, result.size());
        assertEquals("Token", result.get(0));
        assertEquals("Type", result.get(1));
        assertEquals("Gender", result.get(2));
    }

    @Test
    public void testFindTokensWithEscapedBraces() throws Exception {
        QueryParser parser = new QueryParser();
        List<String> tokens = parser.findTokens("\\{escaped\\} {Proper}");
        assertEquals(2, tokens.size());
        assertEquals("\\{escaped\\}", tokens.get(0));
        assertEquals("{Proper}", tokens.get(1));
    }

    @Test
    public void testCreateTermsWithTokenHavingDigitsAndSymbols() throws Exception {
        QueryParser parser = new QueryParser();
        List<?>[] result = parser.createTerms("\"$500.00 is 100% legit\"");
        assertNotNull(result);
        assertEquals(4, result[0].size());
        Term t0 = (Term) result[0].get(0);
        assertEquals("$500.00", t0.text());
        Term t3 = (Term) result[0].get(3);
        assertEquals("legit", t3.text());
    }

    @Test
    public void testSplitStringWithOnlyEscapedSeparators() {
        QueryParser parser = new QueryParser();
        List<String> result = parser.splitString("Token\\,Type\\,Gender", ',', false);
        assertEquals(1, result.size());
        assertEquals("Token\\,Type\\,Gender", result.get(0));
    }

    @Test
    public void testNormSkipsOnlySupportedEscapes() {
        QueryParser parser = new QueryParser();
        String input = "\\n\\t\\,\\.";
        String result = parser.norm(input);
        assertEquals("\\n\\t", result);
    }

    @Test
    public void testCreateTermsAcceptsExtraWhitespaceAroundBraces() throws Exception {
        QueryParser parser = new QueryParser();
        List<?>[] result = parser.createTerms("   {  Person   }   ");
        Term term = (Term) result[0].get(0);
        assertEquals("Person", term.text());
    }

    @Test
    public void testNormHandlesEmptyOrNullInputGracefully() {
        QueryParser parser = new QueryParser();
        String result1 = parser.norm("");
        assertEquals("", result1);
        String result2 = parser.norm(" ");
        assertEquals(" ", result2);
    }

    @Test
    public void testSplitStringHandlesEmptyString() {
        QueryParser parser = new QueryParser();
        List<String> result = parser.splitString("", ',', true);
        assertEquals(0, result.size());
    }

    @Test
    public void testSplitStringWithMultipleCommas() {
        QueryParser parser = new QueryParser();
        List<String> result = parser.splitString("Token,Type,Gender", ',', false);
        assertEquals(3, result.size());
        assertEquals("Token", result.get(0));
        assertEquals("Type", result.get(1));
        assertEquals("Gender", result.get(2));
    }

    @Test
    public void testCreateTermsWithQuotedStringThatHasEmbeddedComma() throws Exception {
        QueryParser parser = new QueryParser();
        List<?>[] result = parser.createTerms("\"hello, world\"");
        assertEquals(2, result[0].size());
        Term t0 = (Term) result[0].get(0);
        assertEquals("hello,", t0.text());
        Term t1 = (Term) result[0].get(1);
        assertEquals("world", t1.text());
    }

    @Test
    public void testQuotedLiteralFollowedByAnnotationTypeLiteral() throws Exception {
        QueryParser parser = new QueryParser();
        Query[] result = parser.parse("field", "\"hello\" {Person}", "Token", null, null);
        assertEquals(1, result.length);
        assertTrue(result[0] instanceof BooleanQuery);
    }

    @Test
    public void testAnnotationTypeFollowedByQuoted() throws Exception {
        QueryParser parser = new QueryParser();
        Query[] result = parser.parse("field", "{Person} \"hello\"", "Token", null, null);
        assertEquals(1, result.length);
        assertTrue(result[0] instanceof BooleanQuery);
    }

    @Test
    public void testFindTokensPreservesWhitespaceOutsideBraces() throws Exception {
        QueryParser parser = new QueryParser();
        List<String> tokens = parser.findTokens("   {Person}    \"data\" ");
        assertEquals(2, tokens.size());
        assertEquals("{Person}", tokens.get(0));
        assertEquals("\"data\"", tokens.get(1));
    }

    @Test
    public void testCreateTermsWithMultipleQuotedWordsMixedWithAnnotation() throws Exception {
        QueryParser parser = new QueryParser();
        List<?>[] result = parser.createTerms("\"first second\" {Person} \"third\"");
        assertEquals(4, result[0].size());
        Term t0 = (Term) result[0].get(0);
        Term t1 = (Term) result[0].get(1);
        Term t2 = (Term) result[0].get(2);
        Term t3 = (Term) result[0].get(3);
        assertEquals("first", t0.text());
        assertEquals("second", t1.text());
        assertEquals("Person", t2.text());
        assertEquals("third", t3.text());
    }

    @Test
    public void testIsValidQueryWithUnsupportedCharactersSucceeds() {
        String query = "{Token} \"#@$%!\"";
        assertTrue(QueryParser.isValidQuery(query));
    }

    @Test(expected = SearchException.class)
    public void testCreateTermsWithAnnotationFeatureAndInvalidToken() throws Exception {
        QueryParser parser = new QueryParser();
        parser.createTerms("{Token.feature==}");
    }

    @Test
    public void testFindTokensWithEscapedOpeningBraceOnly() throws Exception {
        QueryParser parser = new QueryParser();
        List<String> tokens = parser.findTokens("\\{Token} {Person}");
        assertEquals(2, tokens.size());
        assertEquals("\\{Token}", tokens.get(0));
        assertEquals("{Person}", tokens.get(1));
    }

    @Test
    public void testSplitStringWithPrefixEscapeOnFirstChar() {
        QueryParser parser = new QueryParser();
        List<String> result = parser.splitString("\\,Token", ',', false);
        assertEquals(1, result.size());
        assertEquals("\\,Token", result.get(0));
    }

    @Test
    public void testSplitStringMultipleEscapeEdges() {
        QueryParser parser = new QueryParser();
        List<String> result = parser.splitString("A\\,,B\\\\,C,", ',', false);
        assertEquals(3, result.size());
        assertEquals("A\\,", result.get(0));
        assertEquals("B\\\\", result.get(1));
        assertEquals("C", result.get(2));
    }

    @Test
    public void testNormRemovesOnlySupportedEscapeSequences() {
        QueryParser parser = new QueryParser();
        String result = parser.norm("\\\\Hello\\,World\\!\\\"Test\\{\\}");
        assertEquals("\\\\HelloWorld!Test{}", result);
    }

    @Test
    public void testCreateTermsHandlesCommaSeparatedQuotedValues() throws Exception {
        QueryParser parser = new QueryParser();
        List<?>[] result = parser.createTerms("\"first,second\" \"third\"");
        assertEquals(3, result[0].size());
        Term t0 = (Term) result[0].get(0);
        Term t1 = (Term) result[0].get(1);
        Term t2 = (Term) result[0].get(2);
        assertEquals("first,", t0.text());
        assertEquals("second", t1.text());
        assertEquals("third", t2.text());
    }

    @Test
    public void testCreateTermsWithMultipleFeatureDotStructure() throws Exception {
        QueryParser parser = new QueryParser();
        List<?>[] result = parser.createTerms("{Token.sub.feature==\"val\"}");
        assertEquals(1, result[0].size());
        Term t = (Term) result[0].get(0);
        assertEquals("val", t.text());
        assertEquals("Token.sub.feature", t.type());
    }

    @Test
    public void testSplitStringHandlesEmptyInputWithDelimiter() {
        QueryParser parser = new QueryParser();
        List<String> result = parser.splitString(",", ',', false);
        assertEquals(2, result.size());
        assertEquals("", result.get(0));
        assertEquals("", result.get(1));
    }

    @Test
    public void testSplitStringHandlesSingleCharOnlyDelimiter() {
        QueryParser parser = new QueryParser();
        List<String> result = parser.splitString(",", ',', false);
        assertEquals(2, result.size());
        assertEquals("", result.get(0));
        assertEquals("", result.get(1));
    }

    @Test(expected = SearchException.class)
    public void testCreateTermsDotOperatorPresentWithoutSeparators() throws Exception {
        QueryParser parser = new QueryParser();
        parser.createTerms("{Token.feature}");
    }

    @Test
    public void testCreateTermsHandlesQuotedLiteralWithDotBetweenWords() throws Exception {
        QueryParser parser = new QueryParser();
        List<?>[] result = parser.createTerms("\"Dr. Watson\"");
        assertEquals(2, result[0].size());
        Term t0 = (Term) result[0].get(0);
        Term t1 = (Term) result[0].get(1);
        assertEquals("Dr.", t0.text());
        assertEquals("Watson", t1.text());
    }

    @Test
    public void testNormSkipsEscapedBackslashFollowedBySupportedChar() {
        QueryParser parser = new QueryParser();
        String result = parser.norm("\\\\\"escapedquote\\\"");
        assertEquals("\\escapedquote\"", result);
    }

    @Test
    public void testSplitStringWithNoSplitCharacterPresent() {
        QueryParser parser = new QueryParser();
        List<String> result = parser.splitString("nosplitvalue", ',', false);
        assertEquals(1, result.size());
        assertEquals("nosplitvalue", result.get(0));
    }

    @Test
    public void testCreateTermsWithEmptyBracesAndWhitespace() throws Exception {
        QueryParser parser = new QueryParser();
        try {
            parser.createTerms("{     }");
            fail("Expected exception for empty annotation inside braces");
        } catch (SearchException e) {
            assertTrue(e.getMessage().toLowerCase().contains("missing"));
        }
    }

    @Test
    public void testFindTokensWithOnlyWhitespace() throws Exception {
        QueryParser parser = new QueryParser();
        List<String> result = parser.findTokens("   ");
        assertEquals(0, result.size());
    }

    @Test
    public void testCreateTermsWithLiteralDotToken() throws Exception {
        QueryParser parser = new QueryParser();
        List<?>[] result = parser.createTerms("\"...\"");
        assertEquals(1, result[0].size());
        Term t0 = (Term) result[0].get(0);
        assertEquals("...", t0.text());
    }

    @Test(expected = SearchException.class)
    public void testFindTokensWithEscapeAtEndThrowsException() throws Exception {
        QueryParser parser = new QueryParser();
        parser.findTokens("\"incomplete escape sequence\\");
    }

    @Test
    public void testCreateTermsHandlesMultipleFeatureAnnotsWithSamePosition() throws Exception {
        QueryParser parser = new QueryParser();
        List<?>[] result = parser.createTerms("{Token.type==\"NN\",Token.pos==\"singular\"}");
        assertEquals(2, result[0].size());

        Term term1 = (Term) result[0].get(0);
        assertEquals("NN", term1.text());
        assertEquals("Token.type", term1.type());

        Term term2 = (Term) result[0].get(1);
        assertEquals("singular", term2.text());
        assertEquals("Token.pos", term2.type());

        Integer pos1 = (Integer) result[1].get(0);
        Integer pos2 = (Integer) result[1].get(1);
        assertEquals(pos1, pos2);
    }

    @Test
    public void testCreateTermsHandlesMixedBaseTokenAndOtherTypes() throws Exception {
        QueryParser parser = new QueryParser();
        List<?>[] result = parser.createTerms("{Token, Location}");

        assertEquals(2, result[0].size());

        Term term0 = (Term) result[0].get(0);
        assertEquals("Token", term0.text());
        assertEquals("*", term0.type());

        Term term1 = (Term) result[0].get(1);
        assertEquals("Location", term1.text());
        assertEquals("*", term1.type());
    }

    @Test(expected = SearchException.class)
    public void testCreateTermsFailsWhenEqualsIsNotDoubleEquals() throws Exception {
        QueryParser parser = new QueryParser();
        parser.createTerms("{Token.feature=\"value\"}");
    }

    @Test
    public void testFindTokensWithQuotedAnnotationValueContainingBraces() throws Exception {
        QueryParser parser = new QueryParser();
        List<String> tokens = parser.findTokens("{Entity==\"{name}\"}");
        assertEquals(1, tokens.size());
        assertEquals("{Entity==\"{name}\"}", tokens.get(0));
    }

    @Test
    public void testCreateTermsHandlesNestedQuotesInsideValueProperly() throws Exception {
        QueryParser parser = new QueryParser();
        List<?>[] result = parser.createTerms("{Person.quote==\"\\\"hello\\\"\"}");
        assertEquals(1, result[0].size());
        Term t = (Term) result[0].get(0);
        assertEquals("\"hello\"", t.text());
        assertEquals("Person.quote", t.type());
    }

    @Test(expected = SearchException.class)
    public void testCreateTermsWithAnnotationFeatureAndNoTextValue() throws Exception {
        QueryParser parser = new QueryParser();
        parser.createTerms("{Entity.type==}");
    }

    @Test
    public void testCreateTermsHandlesQuotedMultiwordValue() throws Exception {
        QueryParser parser = new QueryParser();
        List<?>[] result = parser.createTerms("{Person.fullName==\"John Doe\"}");
        assertEquals(1, result[0].size());
        Term term = (Term) result[0].get(0);
        assertEquals("John Doe", term.text());
        assertEquals("Person.fullName", term.type());
    }

    @Test
    public void testCreateTermsTreatsSingleWordQuotedValueAsSingleTerm() throws Exception {
        QueryParser parser = new QueryParser();
        List<?>[] result = parser.createTerms("{Location==\"Paris\"}");
        assertEquals(1, result[0].size());
        Term term = (Term) result[0].get(0);
        assertEquals("Paris", term.text());
        assertEquals("Location.string", term.type());
    }

    @Test
    public void testCreateTermsBackslashFollowedByTextInAnnotationValue() throws Exception {
        QueryParser parser = new QueryParser();
        List<?>[] result = parser.createTerms("{Entity.type==\"file\\\\name\"}");
        assertEquals(1, result[0].size());
        Term t = (Term) result[0].get(0);
        assertEquals("file\\name", t.text());
    }

    @Test
    public void testFindTokensHandlesQuotedStringsBetweenBracedTerms() throws Exception {
        QueryParser parser = new QueryParser();
        List<String> tokens = parser.findTokens("{Person} \"walked\" {Destination}");
        assertEquals(3, tokens.size());
        assertEquals("{Person}", tokens.get(0));
        assertEquals("\"walked\"", tokens.get(1));
        assertEquals("{Destination}", tokens.get(2));
    }

    @Test
    public void testCreateTermsHandlesQuotedEmptyAnnotationsGracefully() throws Exception {
        QueryParser parser = new QueryParser();
        List<?>[] result = parser.createTerms("\"\"");
        assertEquals(0, result[0].size());
    }

    @Test(expected = SearchException.class)
    public void testParseRaisesExceptionForUnterminatedAnnotation() throws Exception {
        QueryParser parser = new QueryParser();
        parser.parse("field", "{Token", "Token", null, null);
    }

    @Test
    public void testCreateTermsWithLeadingAndTrailingWhitespaceInQuotedString() throws Exception {
        QueryParser parser = new QueryParser();
        List<?>[] result = parser.createTerms("\" leading and trailing \"");
        assertEquals(3, result[0].size());
        Term t0 = (Term) result[0].get(0);
        Term t2 = (Term) result[0].get(2);
        assertEquals("leading", t0.text());
        assertEquals("trailing", t2.text());
    }

    @Test
    public void testNormStripsEscapesFromQuotedTokens() {
        QueryParser parser = new QueryParser();
        String result = parser.norm("\\\"hello\\\" \\{escaped\\}");
        assertEquals("\"hello\" {escaped}", result);
    }

    @Test
    public void testSplitStringHandlesMultipleUnescapedSeparators() {
        QueryParser parser = new QueryParser();
        List<String> result = parser.splitString("Token,Location,Gender", ',', true);
        assertEquals(3, result.size());
        assertEquals("Token", result.get(0));
        assertEquals("Location", result.get(1));
        assertEquals("Gender", result.get(2));
    }

    @Test
    public void testFindIndexOfSkipsEscapedCharactersCorrectly() {
        QueryParser parser = new QueryParser();
        String input = "Type\\.sub.type==value";
        int index = parser.findIndexOf(input, '.');
        assertEquals(9, index);
    }

    @Test
    public void testCreateTermsPreservesDotNotationOrder() throws Exception {
        QueryParser parser = new QueryParser();
        List<?>[] result = parser.createTerms("{X.Y.Z==\"value\"}");
        Term t = (Term) result[0].get(0);
        assertEquals("value", t.text());
        assertEquals("X.Y.Z", t.type());
    }

    @Test
    public void testCreateTermsWithLeadingAndTrailingWhitespaceInQuotedString() throws Exception {
        QueryParser parser = new QueryParser();
        List<?>[] result = parser.createTerms("\" leading and trailing \"");
        assertEquals(3, result[0].size());
        Term t0 = (Term) result[0].get(0);
        Term t2 = (Term) result[0].get(2);
        assertEquals("leading", t0.text());
        assertEquals("trailing", t2.text());
    }

    @Test
    public void testNormStripsEscapesFromQuotedTokens() {
        QueryParser parser = new QueryParser();
        String result = parser.norm("\\\"hello\\\" \\{escaped\\}");
        assertEquals("\"hello\" {escaped}", result);
    }

    @Test
    public void testSplitStringHandlesMultipleUnescapedSeparators() {
        QueryParser parser = new QueryParser();
        List<String> result = parser.splitString("Token,Location,Gender", ',', true);
        assertEquals(3, result.size());
        assertEquals("Token", result.get(0));
        assertEquals("Location", result.get(1));
        assertEquals("Gender", result.get(2));
    }

    @Test
    public void testFindIndexOfSkipsEscapedCharactersCorrectly() {
        QueryParser parser = new QueryParser();
        String input = "Type\\.sub.type==value";
        int index = parser.findIndexOf(input, '.');
        assertEquals(9, index);
    }

    @Test
    public void testCreateTermsPreservesDotNotationOrder() throws Exception {
        QueryParser parser = new QueryParser();
        List<?>[] result = parser.createTerms("{X.Y.Z==\"value\"}");
        Term t = (Term) result[0].get(0);
        assertEquals("value", t.text());
        assertEquals("X.Y.Z", t.type());
    }

    @Test(expected = SearchException.class)
    public void testCreateTermsThrowsWhenEqualsOperatorMissingWithDotPresent() throws Exception {
        QueryParser parser = new QueryParser();
        parser.createTerms("{Entity.feature}");
    }

    @Test
    public void testParseWithNullFieldAndAnnotationSet() throws Exception {
        QueryParser parser = new QueryParser();
        Query[] result = parser.parse("field", "{Token}", "Token", null, null);
        assertEquals(1, result.length);
    }

    @Test
    public void testParseWithOnlyAnnotationSet() throws Exception {
        QueryParser parser = new QueryParser();
        Query[] result = parser.parse("field", "{Token}", "Token", null, "MySet");
        assertEquals(1, result.length);
        assertTrue(result[0] instanceof BooleanQuery);
        BooleanQuery bq = (BooleanQuery) result[0];
        assertEquals(2, bq.getClauses().length);
    }

    @Test
    public void testParseWithOnlyCorpusId() throws Exception {
        QueryParser parser = new QueryParser();
        Query[] result = parser.parse("field", "{Token}", "Token", "Corpus42", null);
        assertEquals(1, result.length);
        assertTrue(result[0] instanceof BooleanQuery);
        BooleanQuery bq = (BooleanQuery) result[0];
        assertEquals(2, bq.getClauses().length);
    }

    @Test
    public void testNormWithOnlyEscapedSpecialChars() {
        QueryParser parser = new QueryParser();
        String norm = parser.norm("\\\\ \\( \\) \\{ \\} \\, \\. \\\"");
        assertEquals(" ", norm);
    }

    @Test
    public void testCreateTermsWithDoubleQuotedStringAnnotationValue() throws Exception {
        QueryParser parser = new QueryParser();
        List<?>[] result = parser.createTerms("{Person==\"\\\"John\\\"\"}");
        assertEquals(1, result[0].size());
        Term t = (Term) result[0].get(0);
        assertEquals("\"John\"", t.text());
        assertEquals("Person.string", t.type());
    }

    @Test
    public void testCreateTermsWithMultipleEmptyTokensIgnored() throws Exception {
        QueryParser parser = new QueryParser();
        List<?>[] result = parser.createTerms("\"one  two   three\"");
        assertEquals(3, result[0].size());
        Term t0 = (Term) result[0].get(0);
        assertEquals("one", t0.text());
        Term t2 = (Term) result[0].get(2);
        assertEquals("three", t2.text());
    }

    @Test
    public void testSplitStringWithUnescapedAndEscapedSeparators() {
        QueryParser parser = new QueryParser();
        List<String> result = parser.splitString("A\\,B,C", ',', false);
        assertEquals(2, result.size());
        assertEquals("A\\,B", result.get(0));
        assertEquals("C", result.get(1));
    }

    @Test
    public void testSplitStringEndsWithSeparator() {
        QueryParser parser = new QueryParser();
        List<String> result = parser.splitString("Token,Type,", ',', false);
        assertEquals(3, result.size());
        assertEquals("Token", result.get(0));
        assertEquals("Type", result.get(1));
        assertEquals("", result.get(2));
    }

    @Test(expected = SearchException.class)
    public void testCreateTermsWithDotNoEqualsThrowsException() throws Exception {
        QueryParser parser = new QueryParser();
        parser.createTerms("{Entity.feature}");
    }

    @Test(expected = SearchException.class)
    public void testCreateTermsWithInvalidDotEqualsOrder() throws Exception {
        QueryParser parser = new QueryParser();
        parser.createTerms("{Entity.==\"val\"}");
    }

    @Test
    public void testFindTokensPreservesLeadingWhitespaceInQuotes() throws Exception {
        QueryParser parser = new QueryParser();
        List<String> result = parser.findTokens("\"   hello  world  \"");
        assertEquals(1, result.size());
        assertEquals("\"   hello  world  \"", result.get(0));
    }

    @Test
    public void testQuotedMultiWordsSplitIntoIndividualTerms() throws Exception {
        QueryParser parser = new QueryParser();
        List<?>[] result = parser.createTerms("\"New York City\"");
        assertEquals(3, result[0].size());
        Term t0 = (Term) result[0].get(0);
        assertEquals("New", t0.text());
        Term t2 = (Term) result[0].get(2);
        assertEquals("City", t2.text());
    }

    @Test
    public void testCreateTermsWithTripleDotInsideQuotes() throws Exception {
        QueryParser parser = new QueryParser();
        List<?>[] result = parser.createTerms("\"Wait... what\"");
        assertEquals(2, result[0].size());
        Term t0 = (Term) result[0].get(0);
        assertEquals("Wait...", t0.text());
        Term t1 = (Term) result[0].get(1);
        assertEquals("what", t1.text());
    }

    @Test(expected = SearchException.class)
    public void testCreateTermsWithDotBeforeEqualsButNoFeatureName() throws Exception {
        QueryParser parser = new QueryParser();
        parser.createTerms("{Person..==\"value\"}");
    }

    @Test
    public void testNormRemovesEscapeBeforeSingleChar() {
        QueryParser parser = new QueryParser();
        String result = parser.norm("\\,foo\\.bar\\\"baz");
        assertEquals("foobar\"baz", result);
    }

    @Test
    public void testParseEmptyAnnotationSetUsesCombinedSet() throws Exception {
        QueryParser parser = new QueryParser();
        Query[] result = parser.parse("f", "{Token}", "Token", "Corpus1", "");
        assertEquals(1, result.length);
        assertTrue(result[0] instanceof BooleanQuery);
        BooleanQuery bq = (BooleanQuery) result[0];
        assertEquals(2, bq.getClauses().length);
    }

    @Test
    public void testCreateTermsAnnotationWithMultipleQuotedEqualsStrings() throws Exception {
        QueryParser parser = new QueryParser();
        List<?>[] result = parser.createTerms("{A==\"B\" , A.feature==\"C\" , D==\"E\"}");
        assertEquals(3, result[0].size());
        Term t0 = (Term) result[0].get(0);
        assertEquals("B", t0.text());
        Term t1 = (Term) result[0].get(1);
        assertEquals("C", t1.text());
        Term t2 = (Term) result[0].get(2);
        assertEquals("E", t2.text());
    }

    @Test
    public void testTermTypeForPureStringIsBaseAnnotationSuffixString() throws Exception {
        QueryParser parser = new QueryParser();
        List<?>[] result = parser.createTerms("\"hello\"");
        Term t0 = (Term) result[0].get(0);
        assertTrue(t0.type().endsWith(".string"));
    }

    @Test
    public void testTermTypeForFeatureBasedTypeIncludesDotInTypeField() throws Exception {
        QueryParser parser = new QueryParser();
        List<?>[] result = parser.createTerms("{Person.gender==\"male\"}");
        Term t0 = (Term) result[0].get(0);
        assertEquals("Person.gender", t0.type());
    }
}