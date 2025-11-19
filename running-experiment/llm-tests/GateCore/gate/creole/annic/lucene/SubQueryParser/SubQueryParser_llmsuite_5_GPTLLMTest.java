package gate.creole.annic.lucene;

import gate.*;
import gate.creole.ResourceInstantiationException;
import gate.creole.ir.SearchException;
import gate.event.CreoleEvent;
import gate.event.CreoleListener;
import gate.event.PluginListener;
import gate.util.GateException;
import gate.util.GateRuntimeException;
import gate.util.InvalidOffsetException;
import org.apache.tools.ant.types.resources.comparators.Content;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import gate.creole.AnnotationSchema;

import java.awt.*;
import java.net.URL;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.Element;

public class SubQueryParser_llmsuite_5_GPTLLMTest {

 @Test
  public void testSimpleConcatenatedQuery() throws Exception {
    String query = "{A}{B}{C}";
    List<String> parsed = SubQueryParser.parseQuery(query);
    assertEquals(1, parsed.size());
    assertEquals("({A}{B}{C})", parsed.get(0));
  }
@Test
  public void testSimpleOrQuery() throws Exception {
    String query = "{A}|{B}";
    List<String> parsed = SubQueryParser.parseQuery(query);
    assertEquals(2, parsed.size());
    assertTrue(parsed.contains("({A})"));
    assertTrue(parsed.contains("({B})"));
  }
@Test
  public void testGroupedOrQueryWithPrefixAndSuffix() throws Exception {
    String query = "{X}({A}|{B}){Y}";
    List<String> parsed = SubQueryParser.parseQuery(query);
    assertEquals(2, parsed.size());
    assertTrue(parsed.contains("({X}{A}{Y})"));
    assertTrue(parsed.contains("({X}{B}{Y})"));
  }
@Test
  public void testWildcardPlus3() throws Exception {
    String query = "({A})+3";
    List<String> parsed = SubQueryParser.parseQuery(query);
    assertEquals(1, parsed.size());
    String value = parsed.get(0);
    assertTrue(value.contains("(({A}) | (({A})({A})) | (({A})({A})({A})))"));
  }
@Test
  public void testWildcardStar2() throws Exception {
    String query = "({A})*2";
    List<String> parsed = SubQueryParser.parseQuery(query);
    assertEquals(1, parsed.size());
    String result = parsed.get(0);
    assertTrue(result.contains("(({A}) | (({A})({A}))"));
    assertTrue(result.contains("| {__o__})"));
  }
@Test
  public void testWildcardQuestion3() throws Exception {
    String query = "({A})?3";
    List<String> parsed = SubQueryParser.parseQuery(query);
    assertEquals(1, parsed.size());
    String value = parsed.get(0);
    assertTrue(value.contains("(({A}) | (({A})({A})) | (({A})({A})({A})) | {__o__})"));
  }
@Test(expected = SearchException.class)
  public void testUnbalancedOpeningBracket() throws Exception {
    String query = "({A}";
    SubQueryParser.parseQuery(query);
  }
@Test(expected = SearchException.class)
  public void testUnbalancedClosingBracket() throws Exception {
    String query = "{A})";
    SubQueryParser.parseQuery(query);
  }
@Test
  public void testEmptyQueryReturnsEmptyList() throws Exception {
    String query = "";
    List<String> parsed = SubQueryParser.parseQuery(query);
    assertEquals(0, parsed.size());
  }
@Test
  public void testEscapeCharacterInBrackets() throws Exception {
    String query = "\\({A}\\)";
    List<String> parsed = SubQueryParser.parseQuery(query);
    assertEquals(1, parsed.size());
    assertEquals("(\\({A}\\))", parsed.get(0));
  }
@Test
  public void testEscapeCharacterWithOr() throws Exception {
    String query = "{A}\\|{B}";
    List<String> parsed = SubQueryParser.parseQuery(query);
    assertEquals(1, parsed.size());
    assertEquals("({A}\\|{B})", parsed.get(0));
  }
@Test
  public void testScanQueryForOrOrBracketTrueForBarPipe() {
    String input = "foo|(bar)";
    boolean result = SubQueryParser.scanQueryForOrOrBracket(input);
    assertTrue(result);
  }
@Test
  public void testScanQueryForOrOrBracketFalseForEscapedPipeAndBracket() {
    String input = "value\\|escaped\\(split";
    boolean result = SubQueryParser.scanQueryForOrOrBracket(input);
    assertFalse(result);
  }
@Test
  public void testFindOrTokensWithNestedOr() {
    String input = "{A}|({B}|({C}\\|{D}))";
    ArrayList<String> tokens = SubQueryParser.findOrTokens(input);
    assertEquals(2, tokens.size());
    assertEquals("{A}", tokens.get(0));
    assertEquals("({B}|({C}\\|{D}))", tokens.get(1));
  }
@Test
  public void testFindBracketClosingPositionValidIndex() {
    String input = "({A}{B})";
    int pos = SubQueryParser.parseQuery("(" + input + ")").get(0).indexOf(")");
    assertTrue(pos > 0);
  }
@Test
  public void testFindBracketClosingPositionInvalidReturnsMinus1() {
    String input = "({A}{B}";
    int result = -1;
    try {
      SubQueryParser.parseQuery(input);
    } catch (SearchException e) {
      result = -1;
    }
    assertEquals(-1, result);
  }
@Test
  public void testWildcardPlusWithoutRepeatCount() throws Exception {
    String query = "({X})+";
    List<String> parsed = SubQueryParser.parseQuery(query);
    assertEquals(1, parsed.size());
    String s = parsed.get(0);
    assertTrue(s.contains("(({X}))"));
  }
@Test
  public void testQueryWithOrAtStart() throws Exception {
    String query = "({A}|{B}){C}";
    List<String> parsed = SubQueryParser.parseQuery(query);
    assertEquals(2, parsed.size());
    assertTrue(parsed.contains("({A}{C})"));
    assertTrue(parsed.contains("({B}{C})"));
  }
@Test
  public void testFindOrTokensSingleTokenNoOr() {
    String input = "{Token}";
    ArrayList<String> result = SubQueryParser.findOrTokens(input);
    assertEquals(1, result.size());
    assertEquals("{Token}", result.get(0));
  }
@Test
  public void testWhitespaceOnlyOrClauseWithWildcard() throws Exception {
    String query = "( )*1";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("({__o__})") || result.get(0).contains(" | "));
  }
@Test
  public void testWildcardFollowedByNonDigit() throws Exception {
    String query = "({A})*x";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("(({A}))")); 
  }
@Test
  public void testEscapedBackslashInsideGroup() throws Exception {
    String query = "({A\\B}|{C})";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(2, result.size());
    assertTrue(result.contains("({A\\B})"));
    assertTrue(result.contains("({C})"));
  }
@Test
  public void testNestedGroupsWithMixedWildcards() throws Exception {
    String query = "(({A})+2({B}|{C})*1)";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(2, result.size());
    String first = result.get(0);
    String second = result.get(1);
    assertTrue(first.contains("{A}"));
    assertTrue(second.contains("{C}") || second.contains("{B}"));
  }
@Test
  public void testOrAtEndIncludingEmptyAlternative() throws Exception {
    String query = "{A}|";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertEquals("({A})", result.get(0)); 
  }
@Test
  public void testEmptyCurlyBracesGroup() throws Exception {
    String query = "({})";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertEquals("({})", result.get(0));
  }
@Test(expected = SearchException.class)
  public void testInvalidWildcardMixThrowsException() throws Exception {
    String query = "({X})++2";
    SubQueryParser.parseQuery(query);
  }
@Test
  public void testDoubleOrNoGroup() throws Exception {
    String query = "{A}|{B}|{C}";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(3, result.size());
    assertTrue(result.contains("({A})"));
    assertTrue(result.contains("({B})"));
    assertTrue(result.contains("({C})"));
  }
@Test
  public void testPlusWithoutBracketsIsIgnored() throws Exception {
    String query = "{A}+2";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    
    assertTrue(result.get(0).contains("{A}+2"));
  }
@Test
  public void testMultipleNestedParentheses() throws Exception {
    String query = "((({A}|{B})))";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(2, result.size());
    assertTrue(result.contains("((({A})))"));
    assertTrue(result.contains("((({B})))"));
  }
@Test
  public void testUnbalancedNestedBracketsThrows() throws Throwable {
    try {
      String query = "(({A}|{B})";
      SubQueryParser.parseQuery(query);
      fail("Expected SearchException");
    } catch (SearchException e) {
      assertEquals("unbalanced brackets", e.getMessage());
    }
  }
@Test
  public void testWildcardWithZeroRepetition() throws Exception {
    String query = "({A})*0";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("{__o__}"));
  }
@Test
  public void testWhiteSpaceBetweenTokens() throws Exception {
    String query = "{A} ( {B}| {C}) {D}";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(2, result.size());
    assertTrue(result.get(0).contains("{A}{B}{D}"));
    assertTrue(result.get(1).contains("{A}{C}{D}"));
  }
@Test
  public void testBracketInsideTermsButOutsideGrammar() throws Exception {
    String query = "{term(with)brackets}";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertEquals("({term(with)brackets})", result.get(0));
  }
@Test
  public void testMultipleStarWithoutBrackets() throws Exception {
    String query = "{A}*2{B}";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("{A}*2{B}")); 
  }
@Test
  public void testComplexOrWithMixedEscapes() throws Exception {
    String query = "{A}\\|{B}|({C}\\|{D})";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(2, result.size());
    assertTrue(result.get(0).contains("{A}\\|{B}"));
    assertTrue(result.get(1).contains("{C}\\|{D}"));
  }
@Test
  public void testSingleLiteralCharacter() throws Exception {
    String query = "X";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertEquals("( X )", result.get(0));
  }
@Test
  public void testLiteralPipeAsCharacter() throws Exception {
    String query = "\\|";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertEquals("( \\| )", result.get(0));
  }
@Test
  public void testLiteralEscapedBackslash() throws Exception {
    String query = "\\\\";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertEquals("( \\\\ )", result.get(0));
  }
@Test
  public void testWildcardWithNonDigitAfterSymbol() throws Exception {
    String query = "({X})*X";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("({X})*X"));
  }
@Test
  public void testEscapedParenthesisInsideExpression() throws Exception {
    String query = "{A}\\(|{B}\\)";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(2, result.size());
    assertTrue(result.contains("({A}\\()"));
    assertTrue(result.contains("({B}\\))"));
  }
@Test
  public void testLonePipeReturnsTwoEmptyAlternatives() throws Exception {
    String query = "|";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(2, result.size());
    assertTrue(result.contains("()"));
  }
@Test
  public void testNestedTripleOrInGroup() throws Exception {
    String query = "((({A}|{B}|{C})))";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(3, result.size());
    assertTrue(result.contains("((({A})))"));
    assertTrue(result.contains("((({B})))"));
    assertTrue(result.contains("((({C})))"));
  }
@Test
  public void testWildcardImmediatelyAfterClosingParenthesis() throws Exception {
    String query = "({A})+";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    String str = result.get(0);
    assertTrue(str.contains("(({A})"));
  }
@Test
  public void testOrWithEmptyStringBeforeAndAfter() throws Exception {
    String query = "|";
    List<String> result = SubQueryParser.parseQuery(query);
    assertTrue(result.contains("()"));
  }
@Test
  public void testOrWithWhitespaceOnOneSide() throws Exception {
    String query = " {A} | ";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("{A}"));
  }
@Test
  public void testEmptyGroupProcessedCorrectly() throws Exception {
    String query = "()";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertEquals("()", result.get(0));
  }
@Test
  public void testORWithEmptyBranchLeft() throws Exception {
    String query = "|{A}";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(2, result.size());
    assertTrue(result.get(0).contains("()"));
    assertTrue(result.get(1).contains("{A}"));
  }
@Test
  public void testORWithEmptyBranchRight() throws Exception {
    String query = "{A}|";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(2, result.size());
    assertTrue(result.get(1).contains("()"));
  }
@Test
  public void testQueryOnlyWithWhiteSpace() throws Exception {
    String query = "   ";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(0, result.size());
  }
@Test
  public void testGroupContainingOnlyWildcardPattern() throws Exception {
    String query = "({A}*)";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("{A}*"));
  }
@Test
  public void testGroupWithEscapedPipeAndParenthesesInsideTokens() throws Exception {
    String query = "{foo\\|bar}|{baz\\(boo)";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(2, result.size());
    assertTrue(result.get(0).contains("{foo\\|bar}"));
    assertTrue(result.get(1).contains("{baz\\(boo}"));
  }

  @Test(expected = SearchException.class)
  public void testTooManyClosingBrackets() throws Exception {
    String query = "{A})}";
    SubQueryParser.parseQuery(query);
  }
@Test
  public void testOnlyOpeningBracketLiteral() throws Exception {
    String query = "(";
    try {
      SubQueryParser.parseQuery(query);
    } catch (SearchException e) {
      assertEquals("unbalanced brackets", e.getMessage());
    }
  }
@Test
  public void testLiteralWithBothEscapedAndRawSpecialCharacters() throws Exception {
    String query = "x\\(\\)y|(a\\|b)";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(2, result.size());
    assertTrue(result.get(0).contains("x\\(\\)y"));
    assertTrue(result.get(1).contains("(a\\|b)"));
  }
@Test
  public void testOrInsideWildcards() throws Exception {
    String query = "(({A}|{B})+2)";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(2, result.size());
    assertTrue(result.get(0).contains("{A}"));
    assertTrue(result.get(1).contains("{B}"));
    assertTrue(result.get(0).contains("{A}{A}"));
  }
@Test
  public void testEscapedBackslashThenPipe() throws Exception {
    String query = "{foo}\\\\|{bar}";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(2, result.size());
    assertTrue(result.get(0).contains("{foo}\\\\"));
    assertTrue(result.get(1).contains("{bar}"));
  }
@Test
  public void testInputWithOnlyWildcardSymbolAndNoGroup() throws Exception {
    String query = "*3";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("*3"));
  }
@Test
  public void testNestedMultipleGroupLayersWithMixedSymbols() throws Exception {
    String query = "((({X})*2|({Y})+1)?3)";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(2, result.size());
    assertTrue(result.get(0).contains("{X}"));
    assertTrue(result.get(1).contains("{Y}"));
  }
@Test
  public void testWildcardPlusWithoutNumberAndWithOrInside() throws Exception {
    String query = "({A}|{B})+";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(2, result.size());
    assertTrue(result.get(0).contains("{A}"));
    assertTrue(result.get(1).contains("{B}"));
  }
@Test
  public void testSpaceAroundGroupExpression() throws Exception {
    String query = " {A} ";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("{A}"));
  }
@Test
  public void testEscapedEmptyBrackets() throws Exception {
    String query = "\\(\\)";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertEquals("(\\(\\))", result.get(0));
  }
@Test
  public void testGroupWithWildcardPlusAndExtraCharacters() throws Exception {
    String query = "({X})+3ABC";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    String value = result.get(0);
    assertTrue(value.contains("{X}"));
    assertTrue(value.contains("A"));
    assertTrue(value.contains("B"));
    assertTrue(value.contains("C"));
  }
@Test
  public void testGroupWithWildcardFollowedByAlphabetCharacter() throws Exception {
    String query = "({X})*Z";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("*Z"));
  }
@Test
  public void testUnescapedPipeAtBeginningIsDetected() throws Exception {
    String query = "|{A}";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(2, result.size());
    assertTrue(result.get(0).contains("()"));
    assertTrue(result.get(1).contains("{A}"));
  }
@Test
  public void testUnescapedPipeAtEndIsDetected() throws Exception {
    String query = "{A}|";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(2, result.size());
    assertTrue(result.get(0).contains("{A}"));
    assertTrue(result.get(1).contains("()"));
  }
@Test
  public void testSpaceBetweenOrBranchesIsHandled() throws Exception {
    String query = "{X} | {Y}";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(2, result.size());
    assertTrue(result.get(0).contains("{X}"));
    assertTrue(result.get(1).contains("{Y}"));
  }
@Test
  public void testEscapedPipeCharacterIgnoredAsLogicalOR() throws Exception {
    String query = "{A}\\|{B}";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertEquals("({A}\\|{B})", result.get(0));
  }
@Test
  public void testEscapedOpenBracketIgnoredAsGroupStart() throws Exception {
    String query = "foo\\(bar\\)";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertEquals("(foo\\(bar\\))", result.get(0));
  }
@Test
  public void testWildcardStarWithZeroRepetitionResultsInOptionalClause() throws Exception {
    String query = "({W})*0";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    String parsed = result.get(0);
    assertTrue(parsed.contains("{__o__}"));
  }
@Test
  public void testOrWithAllEmptyAlternatives() throws Exception {
    String query = "()|()";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(2, result.size());
    assertEquals("()", result.get(0));
    assertEquals("()", result.get(1));
  }
@Test
  public void testMultipleNestedOrWithWhitespaceIsBalanced() throws Exception {
    String query = "(({A} | {B}) | ({C} | {D}))";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(4, result.size());
    assertTrue(result.get(0).contains("{A}"));
    assertTrue(result.get(1).contains("{B}"));
    assertTrue(result.get(2).contains("{C}"));
    assertTrue(result.get(3).contains("{D}"));
  }
@Test
  public void testDeepRecursionGroupAndWildcardReplacement() throws Exception {
    String query = "(({A})+2({B}|{C})+1)";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(2, result.size());
    assertTrue(result.get(0).contains("{A}"));
    assertTrue(result.get(0).contains("{B}"));
    assertTrue(result.get(1).contains("{C}"));
  }
@Test
  public void testGroupStartsWithTwoEscapedCharacters() throws Exception {
    String query = "\\\\|\\(";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(2, result.size());
    assertEquals("(\\\\)", result.get(0));
    assertEquals("(\\()", result.get(1));
  }
@Test
  public void testComplexLiteralBracketsIgnoredForParsing() throws Exception {
    String query = "{a(b)c}|{x(y)z}";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(2, result.size());
    assertTrue(result.get(0).contains("{a(b)c}") || result.get(1).contains("{a(b)c}"));
    assertTrue(result.get(0).contains("{x(y)z}") || result.get(1).contains("{x(y)z}"));
  }
@Test
  public void testRedundantNestedWildcardGroupsEvaluatedCorrectly() throws Exception {
    String query = "(({X})+2)+1";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("{X}"));
    assertTrue(result.get(0).contains("(({X})({X}))"));
  }
@Test
  public void testWildcardWithoutParenthesesIsPreservedAsLiteral() throws Exception {
    String query = "{X}+3";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertEquals("({X}+3)", result.get(0));
  }
@Test(expected = SearchException.class)
  public void testMissingOpeningBracketRaisesException() throws Exception {
    String query = "A)";
    SubQueryParser.parseQuery(query);
  }
@Test(expected = SearchException.class)
  public void testMissingClosingBracketRaisesException() throws Exception {
    String query = "(A";
    SubQueryParser.parseQuery(query);
  }
@Test
  public void testTokenWithUnderscoreAndDigits() throws Exception {
    String query = "{token_1}|{token_2}";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(2, result.size());
    assertTrue(result.get(0).contains("{token_1}") || result.get(1).contains("{token_1}"));
  }
@Test
  public void testOrWithPartialTokensIncludesAll() throws Exception {
    String query = "{A}|{B}{C}";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(2, result.size());
    assertTrue(result.get(0).contains("{A}") || result.get(1).contains("{A}"));
    assertTrue(result.get(0).contains("{B}{C}") || result.get(1).contains("{B}{C}"));
  }
@Test
  public void testLiteralWithoutBracesOrBracketsParsedAsIs() throws Exception {
    String query = "aXbY";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("aXbY"));
  }
@Test
  public void testWildcardWithoutGroupIgnoredAsWildcard() throws Exception {
    String query = "{X}*3";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertEquals("({X}*3)", result.get(0));
  }
@Test
  public void testMultipleWildcardsInOneQuery() throws Exception {
    String query = "({A})+2({B})*1({C})?3";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("{A}"));
    assertTrue(result.get(0).contains("{B}"));
    assertTrue(result.get(0).contains("{C}"));
  }
@Test
  public void testExtraCloseBracketOutsideGroupKeepsParsing() throws Exception {
    String query = "({A})){B}";
    try {
      SubQueryParser.parseQuery(query);
      fail("Should throw SearchException");
    } catch (SearchException e) {
      assertTrue(e.getMessage().contains("unbalanced brackets"));
    }
  }
@Test
  public void testUnclosedLiteralGroupAllowedInCurlyBraces() throws Exception {
    String query = "{A|B}";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertEquals("({A|B})", result.get(0));
  }
@Test
  public void testWildcardWithNoRepeatDigitDefaultsToOne() throws Exception {
    String query = "({Z})*";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("({Z})"));
  }
@Test
  public void testEscapedOrAndEscapedBracketsTogether() throws Exception {
    String query = "\\({A}\\)\\|\\({B})";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertEquals("(\\({A}\\)\\|\\({B}))", result.get(0));
  }
@Test
  public void testTrailingOrAtGroupEndResolvesProperly() throws Exception {
    String query = "({A}|)";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(2, result.size());
    assertTrue(result.get(0).contains("{A}") || result.get(1).contains("{A}"));
    assertTrue(result.get(0).contains("{__o__}") || result.get(1).contains("{__o__}"));
  }
@Test
  public void testOrInsideDeeplyNestedBrackets() throws Exception {
    String query = "((((({X}|{Y})))){Z})";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(2, result.size());
    assertTrue(result.get(0).contains("{X}{Z}"));
    assertTrue(result.get(1).contains("{Y}{Z}"));
  }
@Test
  public void testMultipleEscapedPipesDoNotBreakParsing() throws Exception {
    String query = "{A}\\|\\|{B}";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertEquals("({A}\\|\\|{B})", result.get(0));
  }
@Test
  public void testEmptyBracketGroupEmbeddedWithinValid() throws Exception {
    String query = "{A}(){B}";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("{A}"));
    assertTrue(result.get(0).contains("{B}"));
  }
@Test
  public void testEscapedBackslashInLiteral() throws Exception {
    String query = "{abc\\\\def}";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertEquals("({abc\\\\def})", result.get(0));
  }
@Test
  public void testOrWithOnlyEmptyStringBranch() throws Exception {
    String query = "()|()";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(2, result.size());
    assertEquals("()", result.get(0));
    assertEquals("()", result.get(1));
  }
@Test
  public void testNestedWildcardFollowedByChar() throws Exception {
    String query = "(({A})+2)X";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("{A}"));
    assertTrue(result.get(0).endsWith("X)"));
  }
@Test
  public void testBackslashAtEndOfInputHandledAsLiteral() throws Exception {
    String query = "{X}\\";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertEquals("({X}\\)", result.get(0));
  }
@Test
  public void testMultipleAdjacentWildcardsParsedCorrectly() throws Exception {
    String query = "(({A})*2)+3";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    String composed = result.get(0);
    assertTrue(composed.contains("{A}"));
    assertTrue(composed.contains("|"));
  }
@Test(expected = SearchException.class)
  public void testBracketMismatchWithoutAnyGroup() throws Exception {
    String query = "abc)";
    SubQueryParser.parseQuery(query);
  }
@Test(expected = SearchException.class)
  public void testUnmatchedMultipleClosingBrackets() throws Exception {
    String query = "({A}))";
    SubQueryParser.parseQuery(query);
  }
@Test
  public void testMixedGroupAndLiteralTextParsedTogether() throws Exception {
    String query = "{A}foo({B}|{C})bar{D}";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(2, result.size());
    String r1 = result.get(0);
    String r2 = result.get(1);
    assertTrue(r1.contains("{A}"));
    assertTrue(r1.contains("foo"));
    assertTrue(r2.contains("bar"));
  }
@Test
  public void testSpaceOnlyGroupNotDiscarded() throws Exception {
    String query = "({ })";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertEquals("({ })", result.get(0));
  }
@Test(expected = SearchException.class)
  public void testMissingClosingBracketInWildcardSectionThrows() throws Exception {
    String query = "({A}+3";
    SubQueryParser.parseQuery(query);
  }
@Test
  public void testWildcardWithAtLeastOneOptionalMarker() throws Exception {
    String query = "({B})?2";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("{B}"));
    assertTrue(result.get(0).contains("| {__o__}"));
  }
@Test
  public void testMixedValidAndEscapedBracketsWithinLiteral() throws Exception {
    String query = "{X\\(Y\\)}";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("{X\\(Y\\)}"));
  }
@Test
  public void testOrOperatorSingleSideGroupMissing() throws Exception {
    String query = "|{A}";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(2, result.size());
    assertTrue(result.get(0).trim().isEmpty() || result.get(1).trim().isEmpty());
    assertTrue(result.get(0).contains("{A}") || result.get(1).contains("{A}"));
  }
@Test
  public void testOrWithThreeAlternativesTopLevel() throws Exception {
    String query = "{A}|{B}|{C}";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(3, result.size());
    assertTrue(result.contains("({A})"));
    assertTrue(result.contains("({B})"));
    assertTrue(result.contains("({C})"));
  }
@Test
  public void testDeepNestedWildcardPlusStarCombo() throws Exception {
    String query = "((({A})+2)*1)";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("{A}"));
    assertTrue(result.get(0).contains("| {__o__}") || result.get(0).contains("({A})({A})"));
  }
@Test
  public void testEmptyGroupExpression() throws Exception {
    String query = "()";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertEquals("()", result.get(0));
  }
@Test
  public void testSingleCharacterAsQuery() throws Exception {
    String query = "A";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertEquals("( A )", result.get(0));
  }
@Test
  public void testEscapePipeNotTreatedAsOR() throws Exception {
    String query = "{A}\\|{B}";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertEquals("({A}\\|{B})", result.get(0));
  }
@Test
  public void testEscapeBracketNotTreatedAsGroup() throws Exception {
    String query = "\\(test\\)";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertEquals("(\\(test\\))", result.get(0));
  }
@Test
  public void testWhitespacePaddingIsTrimmed() throws Exception {
    String query = "   {X}{Y}   ";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertEquals("({X}{Y})", result.get(0));
  }
@Test
  public void testGroupWithOnlyWildcardIsExpanded() throws Exception {
    String query = "({X})+2";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    String output = result.get(0);
    assertTrue(output.contains("(({X}) | (({X})({X})))"));
  }
@Test
  public void testFindOrTokensEmptyBranchInBetween() {
    String query = "{A}||{B}";
    List<String> result = SubQueryParser.findOrTokens(query);
    assertEquals(3, result.size());
    assertEquals("{A}", result.get(0));
    assertEquals("", result.get(1));
    assertEquals("{B}", result.get(2));
  }
@Test
  public void testMultipleEscapedPipesInsideSequence() throws Exception {
    String query = "{word\\|with\\|pipes}";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("{word\\|with\\|pipes}"));
  }
@Test(expected = SearchException.class)
  public void testUnexpectedClosingBracketAtStartThrows() throws Exception {
    String query = ")";
    SubQueryParser.parseQuery(query);
  }
@Test
  public void testLiteralDocSyntaxWithoutCurlyTokens() throws Exception {
    String query = "X|(Y)";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(2, result.size());
    assertTrue(result.get(0).contains("X"));
    assertTrue(result.get(1).contains("Y"));
  }
@Test
  public void testORWithCurlyAndNonCurlyMixed() throws Exception {
    String query = "{A}|B";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(2, result.size());
    assertTrue(result.contains("({A})"));
    assertTrue(result.contains("(B)"));
  }
@Test
  public void testGroupWithEscapedBackslashes() throws Exception {
    String query = "{text\\\\value}";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertEquals("({text\\\\value})", result.get(0));
  }
@Test
  public void testWildcardComboStarAndPlusDistinct() throws Exception {
    String query = "({A})*2({B})+1";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    String value = result.get(0);
    assertTrue(value.contains("{A}"));
    assertTrue(value.contains("{B}"));
  }
@Test
  public void testMultipleAlternativesInsideSingleGroupProcessed() throws Exception {
    String query = "({A}|{B}|{C})";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(3, result.size());
    assertTrue(result.contains("({A})"));
    assertTrue(result.contains("({B})"));
    assertTrue(result.contains("({C})"));
  }
@Test
  public void testQueryWithAdjacentWildcards() throws Exception {
    String query = "({T})+2({G})*3";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    String output = result.get(0);
    assertTrue(output.contains("{T}"));
    assertTrue(output.contains("{G}"));
    assertTrue(output.contains("|"));
  }
@Test
  public void testEmptyCurlyBracesWithinOrGroup() throws Exception {
    String query = "({}|{A})";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(2, result.size());
    assertTrue(result.get(0).contains("{}") || result.get(1).contains("{}"));
    assertTrue(result.get(0).contains("{A}") || result.get(1).contains("{A}"));
  }
@Test
  public void testMultipleEscapedCharactersInSameGroup() throws Exception {
    String query = "\\(text\\)\\|xyz";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertEquals("(\\(text\\)\\|xyz)", result.get(0));
  }
@Test
  public void testMalformedRepetitionSymbolOnly() throws Exception {
    String query = "*2";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertEquals("(*2)", result.get(0));
  }
@Test
  public void testMatcherWithOrAndTrailingLiteral() throws Exception {
    String query = "({A}|{B})xyz";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(2, result.size());
    assertTrue(result.get(0).endsWith("xyz)"));
    assertTrue(result.get(1).endsWith("xyz)"));
  }
@Test
  public void testOrInsideDeepWildcardLayer() throws Exception {
    String query = "(({X}|{Y})+1)";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(2, result.size());
    assertTrue(result.get(0).contains("{X}"));
    assertTrue(result.get(1).contains("{Y}"));
  }
@Test
  public void testEscapedBackslashRightOutsideToken() throws Exception {
    String query = "{Z}\\\\";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertEquals("({Z}\\\\)", result.get(0));
  }
@Test
  public void testQueryEndingWithUnescapedBackslashHandledSafely() throws Exception {
    String query = "abc\\";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertEquals("(abc\\)", result.get(0));
  }
@Test
  public void testFindBracketClosingPositionUnbalancedGroupReturnsNegativeOne() throws Exception {
    String query = "({A}({B}{C}";
    int result = -1;
    try {
      List<String> r = SubQueryParser.parseQuery(query);
    } catch (SearchException e) {
      result = -1;
    }
    assertEquals(-1, result);
  }
@Test
  public void testEmptyStringInsideGroup() throws Exception {
    String query = "({})+1";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("{}"));
  }
@Test
  public void testOnlyEscapeCharacterThenORSymbol() throws Exception {
    String query = "\\|";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertEquals("(\\|)", result.get(0));
  }
@Test
  public void testOnlyEscapeCharacterThenBracketSymbols() throws Exception {
    String query = "\\(abc\\)";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertEquals("(\\(abc\\))", result.get(0));
  }
@Test
  public void testEmptyTokenInputBetweenCurlyBraces() throws Exception {
    String query = "{ }";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertEquals("({ })", result.get(0));
  }
@Test
  public void testLiteralPlusWildcardBracketSequence() throws Exception {
    String query = "foo({A})+2";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("foo"));
    assertTrue(result.get(0).contains("{A}"));
  }
@Test
  public void testMixedOrWithEmptyLiteralBranch() throws Exception {
    String query = "|()";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(2, result.size());
    assertTrue(result.get(0).equals("()") || result.get(1).equals("()"));
  }
@Test
  public void testWhitespaceOnlyGroupIgnoredAfterFinalTrim() throws Exception {
    String query = "   ";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(0, result.size());
  }
@Test
  public void testGroupWithMultiLevelNestedParentheses() throws Exception {
    String query = "(({A}|({B}|{C})))";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(3, result.size());
    assertTrue(result.get(0).contains("{A}") || result.get(1).contains("{A}") || result.get(2).contains("{A}"));
    assertTrue(result.get(0).contains("{B}") || result.get(1).contains("{B}") || result.get(2).contains("{B}"));
    assertTrue(result.get(0).contains("{C}") || result.get(1).contains("{C}") || result.get(2).contains("{C}"));
  }
@Test
  public void testWildcardWithInvalidCharacterSuffixStopsAtSymbol() throws Exception {
    String query = "({Z})*xY";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("{Z}") && result.get(0).contains("xY"));
  }
@Test
  public void testBracketGroupInterleavedWithSymbols() throws Exception {
    String query = "#({A})#({B})#";
    List<String> result = SubQueryParser.parseQuery(query);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("{A}"));
    assertTrue(result.get(0).contains("{B}"));
    assertTrue(result.get(0).contains("#"));
  } 
}