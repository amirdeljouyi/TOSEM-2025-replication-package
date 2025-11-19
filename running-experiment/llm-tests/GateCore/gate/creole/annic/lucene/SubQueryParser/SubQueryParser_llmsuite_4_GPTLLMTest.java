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


public class SubQueryParser_llmsuite_4_GPTLLMTest {

 @Test
  public void testSimpleConcatenation() throws Exception {
    List<String> result = SubQueryParser.parseQuery("{A}{B}{C}");
    assertEquals(1, result.size());
    assertEquals("({A}{B}{C})", result.get(0));
  }
@Test
  public void testOrExpression() throws Exception {
    List<String> result = SubQueryParser.parseQuery("({A}|{B}){C}");
    assertEquals(2, result.size());

    String first = result.get(0);
    String second = result.get(1);

    boolean hasFirst = first.equals("({A}{C})") || second.equals("({A}{C})");
    boolean hasSecond = first.equals("({B}{C})") || second.equals("({B}{C})");

    assertTrue(hasFirst);
    assertTrue(hasSecond);
  }
@Test
  public void testNestedOrExpression() throws Exception {
    List<String> result = SubQueryParser.parseQuery("{X}({A}|({B}|{C})){Y}");
    assertEquals(3, result.size());

    boolean containsXA = result.get(0).contains("{X}") && result.get(0).contains("{A}") && result.get(0).contains("{Y}");
    boolean containsXB = result.get(1).contains("{X}") && result.get(1).contains("{B}") && result.get(1).contains("{Y}");
    boolean containsXC = result.get(2).contains("{X}") && result.get(2).contains("{C}") && result.get(2).contains("{Y}");

    assertTrue(containsXA || containsXB || containsXC);
  }
@Test
  public void testWildcardPlusExact3() throws Exception {
    List<String> result = SubQueryParser.parseQuery("({T})+3");
    assertEquals(1, result.size());

    String parsed = result.get(0);

    boolean containsOne = parsed.contains("({({T})})");
    boolean containsTwo = parsed.contains("({({T})({T})})");
    boolean containsThree = parsed.contains("({({T})({T})({T})})");
    boolean noOptional = !parsed.contains("{__o__}");

    assertTrue(containsOne);
    assertTrue(containsTwo);
    assertTrue(containsThree);
    assertTrue(noOptional);
  }
@Test
  public void testWildcardStarExpression() throws Exception {
    List<String> result = SubQueryParser.parseQuery("({A})*2");
    assertEquals(1, result.size());

    String parsed = result.get(0);
    boolean containsZero = parsed.contains("{__o__}");
    boolean containsOne = parsed.contains("({({A})})");
    boolean containsTwo = parsed.contains("({({A})({A})})");

    assertTrue(containsZero);
    assertTrue(containsOne);
    assertTrue(containsTwo);
  }
@Test
  public void testWildcardOptionalQuestionMark() throws Exception {
    List<String> result = SubQueryParser.parseQuery("({Q})?2");
    assertEquals(1, result.size());

    String parsed = result.get(0);

    boolean containsOptional = parsed.contains("{__o__}");
    boolean containsOne = parsed.contains("({({Q})})");
    boolean containsTwo = parsed.contains("({({Q})({Q})})");

    assertTrue(containsOptional);
    assertTrue(containsOne);
    assertTrue(containsTwo);
  }
@Test
  public void testMultipleTopLevelOrs() throws Exception {
    List<String> result = SubQueryParser.parseQuery("{A}|{B}|{C}");
    assertEquals(3, result.size());

    String r1 = result.get(0);
    String r2 = result.get(1);
    String r3 = result.get(2);

    boolean hasA = (r1.trim().equals("{A}") || r2.trim().equals("{A}") || r3.trim().equals("{A}"));
    boolean hasB = (r1.trim().equals("{B}") || r2.trim().equals("{B}") || r3.trim().equals("{B}"));
    boolean hasC = (r1.trim().equals("{C}") || r2.trim().equals("{C}") || r3.trim().equals("{C}"));

    assertTrue(hasA);
    assertTrue(hasB);
    assertTrue(hasC);
  }
@Test
  public void testEscapedOrIsNotParsedAsLogicalOr() throws Exception {
    List<String> result = SubQueryParser.parseQuery("{A}\\|{B}");
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("\\|"));
  }
@Test
  public void testEscapedParentheses() throws Exception {
    List<String> result = SubQueryParser.parseQuery("{A}\\({B}\\)");
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("\\("));
    assertTrue(result.get(0).contains("\\)"));
  }
@Test(expected = SearchException.class)
  public void testUnbalancedOpenBracketThrowsException() throws Exception {
    SubQueryParser.parseQuery("({A}|{B}{C}");
  }
@Test(expected = SearchException.class)
  public void testUnbalancedCloseBracketThrowsException() throws Exception {
    SubQueryParser.parseQuery("{A}|{B}){C}");
  }
@Test
  public void testEmptyQueryReturnsEmptyList() throws Exception {
    List<String> result = SubQueryParser.parseQuery("");
    assertTrue(result.isEmpty());
  }
@Test
  public void testWhitespaceOnlyQueryReturnsEmptyList() throws Exception {
    List<String> result = SubQueryParser.parseQuery("   ");
    assertTrue(result.isEmpty());
  }
@Test
  public void testWildcardWithoutNumberDefaultsToOne() throws Exception {
    List<String> result = SubQueryParser.parseQuery("({Z})+");
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("({({Z})})"));
    assertFalse(result.get(0).contains("{__o__}"));
  }
@Test
  public void testScanQueryWithLogicalOr() {
    boolean hasOr = SubQueryParser.scanQueryForOrOrBracket("{A}|{B}");
    assertTrue(hasOr);
  }
@Test
  public void testScanQueryWithGroupBracket() {
    boolean hasBracket = SubQueryParser.scanQueryForOrOrBracket("({A}{B})");
    assertTrue(hasBracket);
  }
@Test
  public void testScanQueryWithEscapeCharacters() {
    boolean result = SubQueryParser.scanQueryForOrOrBracket("\\(\\)\\|");
    assertFalse(result);
  }
@Test
  public void testFindOrTokensSimpleSplit() {
    List<String> tokens = SubQueryParser.findOrTokens("{A}|{B}|{C}");
    assertEquals(3, tokens.size());

    assertEquals("{A}", tokens.get(0));
    assertEquals("{B}", tokens.get(1));
    assertEquals("{C}", tokens.get(2));
  }
@Test
  public void testLargeCompositeQuery() throws Exception {
    List<String> result = SubQueryParser.parseQuery("({A}|{B})({C}|{D})+2");
    assertNotNull(result);
    assertFalse(result.isEmpty());

    String q0 = result.get(0);
    assertTrue(q0.contains("{A}") || q0.contains("{B}"));
    assertTrue(q0.contains("{C}") || q0.contains("{D}"));

    assertTrue(result.size() >= 2);
  }
@Test
  public void testSingleTokenWrappedWithExtraParens() throws Exception {
    List<String> result = SubQueryParser.parseQuery("(({X}))");
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("{X}"));
  }
@Test
  public void testTrailingWildcardWithNoDigits() throws Exception {
    List<String> result = SubQueryParser.parseQuery("({A})+");
    assertEquals(1, result.size());
    String parsed = result.get(0);
    assertTrue(parsed.contains("({({A})})"));
    assertFalse(parsed.contains("{__o__}"));
  }
@Test
  public void testEscapedEscapeCharacter() throws Exception {
    List<String> result = SubQueryParser.parseQuery("foo\\\\bar");
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("\\\\"));
  }
@Test
  public void testOnlyOrSymbol() throws Exception {
    List<String> result = SubQueryParser.parseQuery("|");
    assertEquals(2, result.size());
    assertEquals("()", result.get(0));
    assertEquals("()", result.get(1));
  }
@Test
  public void testWildcardQuestionMarkOnly() throws Exception {
    List<String> result = SubQueryParser.parseQuery("({W})?");
    assertEquals(1, result.size());
    String parsed = result.get(0);
    assertTrue(parsed.contains("({({W})})"));
    assertTrue(parsed.contains("{__o__}"));
  }
@Test
  public void testDeeplyNestedParensWithOrAndWildcards() throws Exception {
    List<String> result = SubQueryParser.parseQuery("(({A}|({B}|{C})))*1");
    assertEquals(3, result.size());
    String q0 = result.get(0);
    String q1 = result.get(1);
    String q2 = result.get(2);
    assertTrue(q0.contains("{A}") || q0.contains("{B}") || q0.contains("{C}"));
    assertTrue(q1.contains("{A}") || q1.contains("{B}") || q1.contains("{C}"));
    assertTrue(q2.contains("{A}") || q2.contains("{B}") || q2.contains("{C}"));
  }
@Test
  public void testInputWithWhitespaceBetweenTokens() throws Exception {
    List<String> result = SubQueryParser.parseQuery("{A}   {B} \t {C} ");
    assertEquals(1, result.size());
    assertEquals("({A}{B}{C})", result.get(0));
  }
@Test
  public void testUnbalancedWithNoOpenBracketThrowsException() throws Exception {
    try {
      SubQueryParser.parseQuery("A)B");
      fail("Expected exception for unmatched closing bracket.");
    } catch (SearchException e) {
      assertTrue(e.getMessage().contains("unbalanced brackets"));
    }
  }
@Test
  public void testUnbalancedWithExtraOpenBracketThrowsException() throws Exception {
    try {
      SubQueryParser.parseQuery("((A{B})");
      fail("Expected exception for unmatched open bracket.");
    } catch (SearchException e) {
      assertTrue(e.getMessage().contains("unbalanced brackets"));
    }
  }
@Test
  public void testFindOrTokensWithNestedMixedContent() {
    List<String> tokens = SubQueryParser.findOrTokens("{A}|({B}|({C}|{D}))|{E}");
    assertEquals(3, tokens.size());
    assertEquals("{A}", tokens.get(0));
    assertEquals("({B}|({C}|{D}))", tokens.get(1));
    assertEquals("{E}", tokens.get(2));
  }
@Test
  public void testScanQueryWithOnlySpacesReturnsFalse() {
    boolean result = SubQueryParser.scanQueryForOrOrBracket("     ");
    assertFalse(result);
  }
@Test
  public void testScanQueryWithEscapedCharactersInMiddle() {
    boolean result = SubQueryParser.scanQueryForOrOrBracket("foo\\|bar\\(test\\)");
    assertFalse(result);
  }
@Test
  public void testConsecutiveWildcardsHandledCorrectly() throws Exception {
    List<String> result = SubQueryParser.parseQuery("(({V})+2)+2");
    assertEquals(1, result.size());
    String query = result.get(0);
    assertTrue(query.contains("({({({V})})})"));
  }
@Test
  public void testEmptyParenthesesTreatedAsEmptyToken() throws Exception {
    List<String> result = SubQueryParser.parseQuery("foo()bar");
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("foo()bar"));
  }
@Test
  public void testRedundantWildcardsDoNotCrash() throws Exception {
    List<String> result = SubQueryParser.parseQuery("(({X}))*1");
    assertNotNull(result);
    assertFalse(result.isEmpty());
  }
@Test
  public void testWhitespaceOnlyBetweenOperators() throws Exception {
    List<String> result = SubQueryParser.parseQuery("({A}) | ({B})");
    assertEquals(2, result.size());
    assertTrue(result.get(0).contains("{A}") || result.get(1).contains("{A}"));
    assertTrue(result.get(0).contains("{B}") || result.get(1).contains("{B}"));
  }
@Test
  public void testQueryWithSpecialCharactersOutsideTokens() throws Exception {
    List<String> result = SubQueryParser.parseQuery("abc{X}!@#");
    assertEquals(1, result.size());
    String parsed = result.get(0);
    assertTrue(parsed.contains("abc"));
    assertTrue(parsed.contains("{X}"));
    assertTrue(parsed.contains("!@#"));
  }
@Test
  public void testQueryWithEmptyOrBranches() throws Exception {
    List<String> result = SubQueryParser.parseQuery("({A}|)");
    assertEquals(2, result.size());
    assertTrue(result.get(0).contains("{A}") || result.get(1).contains("{A}"));
  }
@Test
  public void testQueryWithOnlyBracketsAndNoContent() throws Exception {
    List<String> result = SubQueryParser.parseQuery("()");
    assertEquals(1, result.size());
    assertEquals("()", result.get(0));
  }
@Test
  public void testQueryWithMultipleNestedWildcards() throws Exception {
    List<String> result = SubQueryParser.parseQuery("(({A})+2)+");
    assertEquals(1, result.size());
    String parsed = result.get(0);
    assertTrue(parsed.contains("{A}"));
    assertTrue(parsed.contains("({("));
  }


@Test
  public void testEmptyGroupInOrClause() throws Exception {
    List<String> result = SubQueryParser.parseQuery("({A}|())");
    assertEquals(2, result.size());
    String query1 = result.get(0);
    String query2 = result.get(1);
    assertTrue(query1.contains("{A}") || query2.contains("{A}"));
    assertTrue(query1.contains("()") || query2.contains("()"));
  }
@Test
  public void testSpecialCharactersInsideTokens() throws Exception {
    List<String> result = SubQueryParser.parseQuery("{A_B-C.1}");
    assertEquals(1, result.size());
    assertEquals("({A_B-C.1})", result.get(0));
  }
@Test
  public void testSingleOrOperatorAlone() throws Exception {
    List<String> result = SubQueryParser.parseQuery("|");
    assertEquals(2, result.size());
    assertEquals("()", result.get(0));
    assertEquals("()", result.get(1));
  }
@Test
  public void testUnescapedOrInsideTokenName() throws Exception {
    List<String> result = SubQueryParser.parseQuery("{A|B}");
    assertEquals(1, result.size());
    assertEquals("({A|B})", result.get(0));
  }
@Test
  public void testOrAtStartOfQuery() throws Exception {
    List<String> result = SubQueryParser.parseQuery("|{X}");
    assertEquals(2, result.size());
    assertEquals("{X}", result.get(0).trim());
    assertEquals("", result.get(1).trim());
  }
@Test
  public void testOrAtEndOfQuery() throws Exception {
    List<String> result = SubQueryParser.parseQuery("{X}|");
    assertEquals(2, result.size());
    assertEquals("{X}", result.get(0).trim());
    assertEquals("", result.get(1).trim());
  }
@Test
  public void testMultipleConsecutiveEmptyGroups() throws Exception {
    List<String> result = SubQueryParser.parseQuery("()()()()");
    assertEquals(1, result.size());
    assertEquals("(()()()())", result.get(0));
  }
@Test
  public void testNestedWildcardsWithoutRepetitionNumber() throws Exception {
    List<String> result = SubQueryParser.parseQuery("(({X})+)+");
    assertEquals(1, result.size());
    String query = result.get(0);
    assertTrue(query.contains("({({({X})})})"));
  }
@Test
  public void testWildcardStarWithoutRepetitionNumber() throws Exception {
    List<String> result = SubQueryParser.parseQuery("({Z})*");
    assertEquals(1, result.size());
    String parsed = result.get(0);
    assertTrue(parsed.contains("({({Z})})"));
    assertTrue(parsed.contains("{__o__}"));
  }
@Test
  public void testMixedLogicalOperators() throws Exception {
    List<String> result = SubQueryParser.parseQuery("({A}|{B})({C}|{D})");
    assertEquals(4, result.size());
    boolean a = result.get(0).contains("{A}{C}") || result.get(1).contains("{A}{C}") || result.get(2).contains("{A}{C}") || result.get(3).contains("{A}{C}");
    boolean b = result.get(0).contains("{A}{D}") || result.get(1).contains("{A}{D}") || result.get(2).contains("{A}{D}") || result.get(3).contains("{A}{D}");
    boolean c = result.get(0).contains("{B}{C}") || result.get(1).contains("{B}{C}") || result.get(2).contains("{B}{C}") || result.get(3).contains("{B}{C}");
    boolean d = result.get(0).contains("{B}{D}") || result.get(1).contains("{B}{D}") || result.get(2).contains("{B}{D}") || result.get(3).contains("{B}{D}");
    assertTrue(a);
    assertTrue(b);
    assertTrue(c);
    assertTrue(d);
  }
@Test
  public void testOnlyOpeningBracketThrowsSearchException() throws Exception {
    try {
      SubQueryParser.parseQuery("(");
      fail("Expected SearchException due to unmatched opening parenthesis");
    } catch (SearchException e) {
      assertTrue(e.getMessage().toLowerCase().contains("unbalanced"));
    }
  }
@Test
  public void testOnlyClosingBracketThrowsSearchException() throws Exception {
    try {
      SubQueryParser.parseQuery(")");
      fail("Expected SearchException due to unmatched closing parenthesis");
    } catch (SearchException e) {
      assertTrue(e.getMessage().toLowerCase().contains("unbalanced"));
    }
  }
@Test
  public void testEmptyBracketsWithWildcard() throws Exception {
    List<String> result = SubQueryParser.parseQuery("()*2");
    assertEquals(1, result.size());
    String parsed = result.get(0);
    assertTrue(parsed.contains("(() | (()()) | (()()()))")); 
  }
@Test
  public void testTrailingWhitespaceOnlyAfterValidQuery() throws Exception {
    List<String> result = SubQueryParser.parseQuery("{T}   ");
    assertEquals(1, result.size());
    assertEquals("({T})", result.get(0));
  }
@Test
  public void testLeadingWhitespaceOnlyBeforeValidQuery() throws Exception {
    List<String> result = SubQueryParser.parseQuery("   {T}");
    assertEquals(1, result.size());
    assertEquals("({T})", result.get(0));
  }
@Test
  public void testWildcardWithLongDigitRepeat() throws Exception {
    List<String> result = SubQueryParser.parseQuery("({D})*10");
    assertEquals(1, result.size());
    String parsed = result.get(0);
    assertTrue(parsed.contains("{__o__}"));
    assertTrue(parsed.contains("({({D})({D})({D})")); 
  }


@Test
  public void testWildcardWithNonDigitSuffixIgnored() throws Exception {
    List<String> result = SubQueryParser.parseQuery("({A})+x");
    assertEquals(1, result.size());
    String query = result.get(0);
    assertTrue(query.contains("({({A})})"));
    assertFalse(query.contains("({({A})({A})})")); 
  }
@Test
  public void testWildcardWithNegativeRepetitionThrowsException() {
    try {
      SubQueryParser.parseQuery("({A})+ -1");
      fail("Expected exception for invalid repetition value.");
    } catch (Exception e) {
      assertTrue(e instanceof NumberFormatException || e instanceof SearchException);
    }
  }
@Test
  public void testEscapedBackslashBeforeWildcardSymbolNotTreatedAsWildcard() throws Exception {
    List<String> result = SubQueryParser.parseQuery("{A}\\+3");
    assertEquals(1, result.size());
    String query = result.get(0);
    assertTrue(query.contains("\\+3"));
    assertFalse(query.contains("{({A})({A})({A})}"));
  }
@Test
  public void testBracketOnlyStringReturnsNormalizedEmpty() throws Exception {
    List<String> result = SubQueryParser.parseQuery("()");
    assertEquals(1, result.size());
    assertEquals("()", result.get(0));
  }
@Test
  public void testEmptyRepetitionGroupFollowedByToken() throws Exception {
    List<String> result = SubQueryParser.parseQuery("(){A}");
    assertEquals(1, result.size());
    assertEquals("((){A})", result.get(0));
  }
@Test
  public void testOrGroupWithOnlyEmptyBranches() throws Exception {
    List<String> result = SubQueryParser.parseQuery("(|)");
    assertEquals(2, result.size());
    assertEquals("()", result.get(0));
    assertEquals("()", result.get(1));
  }
@Test
  public void testUnicodeCharacterInsideToken() throws Exception {
    List<String> result = SubQueryParser.parseQuery("{Тест}");
    assertEquals(1, result.size());
    String query = result.get(0);
    assertTrue(query.contains("{Тест}"));
  }
@Test
  public void testExcessivelyNestedValidQuery() throws Exception {
    List<String> result = SubQueryParser.parseQuery("(((((({A})))))({B})))");
    assertTrue(result.get(0).contains("{A}"));
    assertTrue(result.get(0).contains("{B}"));
  }
@Test
  public void testScanQueryWithOnlyWhitespacesReturnsFalse() {
    boolean scan = SubQueryParser.scanQueryForOrOrBracket("     ");
    assertFalse(scan);
  }
@Test
  public void testScanQueryWithEscapedOrReturnsFalse() {
    boolean result = SubQueryParser.scanQueryForOrOrBracket("foo\\|bar");
    assertFalse(result);
  }
@Test
  public void testFindOrTokensWithMultipleUnescapedOrs() {
    List<String> result = SubQueryParser.findOrTokens("foo|bar|baz");
    assertEquals(3, result.size());
    assertEquals("foo", result.get(0));
    assertEquals("bar", result.get(1));
    assertEquals("baz", result.get(2));
  }
@Test
  public void testFindOrTokensWithEscapedOrsOnly() {
    List<String> result = SubQueryParser.findOrTokens("foo\\|bar\\|baz");
    assertEquals(1, result.size());
    assertEquals("foo\\|bar\\|baz", result.get(0));
  }
@Test
  public void testDuplicateWithNullInitialString() {
    List<String> result = SubQueryParser.duplicate(List.of(), null, 0, 3);
    assertEquals(3, result.size());
    assertEquals("", result.get(0));
    assertEquals("", result.get(1));
    assertEquals("", result.get(2));
  }
@Test
  public void testFindBracketClosingForUnmatchedStartReturnsMinusOne() {
    int pos = SubQueryParser.findBracketClosingPosition(5, "(a(b(c)");
    assertEquals(-1, pos);
  }
@Test
  public void testFindBracketClosingReturnsValidIndex() {
    int pos = SubQueryParser.findBracketClosingPosition(0, "(a(b)c)");
    assertEquals(6, pos);
  }
@Test
  public void testQueryWithMultipleEscapedBackslashesAndPipes() throws Exception {
    List<String> result = SubQueryParser.parseQuery("foo\\\\\\|bar");
    assertEquals(1, result.size());
    String parsed = result.get(0);
    assertTrue(parsed.contains("\\\\|"));
  }
@Test
  public void testQueryWithRedundantDoubleOrOperators() throws Exception {
    List<String> result = SubQueryParser.parseQuery("{A}||{B}");
    assertEquals(3, result.size());
    assertTrue(result.get(0).contains("{A}") || result.get(1).contains("{A}") || result.get(2).contains("{A}"));
    assertTrue(result.get(0).contains("{B}") || result.get(1).contains("{B}") || result.get(2).contains("{B}"));
  }
@Test
  public void testRepeatedNormalizedQueriesAreUnique() throws Exception {
    List<String> result = SubQueryParser.parseQuery("({A}|{A})");
    assertEquals(1, result.size());
    assertEquals("({A})", result.get(0));
  }
@Test
  public void testConsecutiveWildcardsIgnoreOverlap() throws Exception {
    List<String> result = SubQueryParser.parseQuery("(({A})+3)+1");
    assertEquals(1, result.size());
    String query = result.get(0);
    assertTrue(query.contains("{A}"));
    assertTrue(query.contains("({({A})({A})({A})})"));
  }
@Test
  public void testQueryWithUnbalancedQuotesInsideToken() throws Exception {
    List<String> result = SubQueryParser.parseQuery("{A\"B}");
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("{A\"B}"));
  }
@Test
  public void testParseQueryWithSingleCharacterToken() throws Exception {
    List<String> result = SubQueryParser.parseQuery("{X}");
    assertEquals(1, result.size());
    assertEquals("({X})", result.get(0));
  }
@Test
  public void testMultipleEmptyOrBranches() throws Exception {
    List<String> result = SubQueryParser.parseQuery("(|)|()");
    assertEquals(3, result.size());
    assertEquals("()", result.get(0));
    assertEquals("()", result.get(1));
    assertEquals("()", result.get(2));
  }
@Test
  public void testParseQueryWithSequentialWildcardPatterns() throws Exception {
    List<String> result = SubQueryParser.parseQuery("({A})+2({B})*2");
    assertEquals(1, result.size());
    String q = result.get(0);
    assertTrue(q.contains("{A}"));
    assertTrue(q.contains("{B}"));
    assertTrue(q.contains("{__o__}"));
  }
@Test
  public void testParseQueryWithEscapedOpenBracketInsideGroup() throws Exception {
    List<String> result = SubQueryParser.parseQuery("({A}\\({B})");
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("{A}\\("));
  }
@Test
  public void testParseQueryWithEscapedCloseBracketInsideGroup() throws Exception {
    List<String> result = SubQueryParser.parseQuery("({A}\\){B})");
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("\\)"));
  }
@Test
  public void testParseQueryWithOnlyPunctuation() throws Exception {
    List<String> result = SubQueryParser.parseQuery(".,;:");
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains(".,;:"));
  }
@Test
  public void testParseQueryWithSingleSpecialCharacterEscaped() throws Exception {
    List<String> result = SubQueryParser.parseQuery("\\|");
    assertEquals(1, result.size());
    assertEquals("\\|", result.get(0));
  }
@Test
  public void testFindBracketClosingPositionNoClosing() {
    int pos = SubQueryParser.findBracketClosingPosition(0, "(A(B");
    assertEquals(-1, pos);
  }
@Test
  public void testFindBracketClosingPositionNestedClose() {
    int pos = SubQueryParser.findBracketClosingPosition(0, "(A(B)C)");
    assertEquals(6, pos);
  }
@Test
  public void testIsOpenBracketEscapedReturnsFalse() {
    boolean isOpen = SubQueryParser.scanQueryForOrOrBracket("Test \\( Value");
    assertFalse(isOpen);
  }
@Test
  public void testWildcardWithUnexpectedTerminator() throws Exception {
    List<String> result = SubQueryParser.parseQuery("({W})+3a");
    assertEquals(1, result.size());
    String q = result.get(0);
    assertTrue(q.contains("{W}"));
  }
@Test
  public void testParseQueryWithMultipleNestedEmptyGroups() throws Exception {
    List<String> result = SubQueryParser.parseQuery("(((())))");
    assertEquals(1, result.size());
    assertEquals("(((())))", result.get(0));
  }
@Test
  public void testQueryWithPlusWildcardWithoutGroup() throws Exception {
    List<String> result = SubQueryParser.parseQuery("{X}+");
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("{X}"));
  }
@Test
  public void testQueryWithStarWildcardWithoutGroup() throws Exception {
    List<String> result = SubQueryParser.parseQuery("{X}*");
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("{X}"));
  }
@Test
  public void testQueryWithQuestionWildcardWithoutGroup() throws Exception {
    List<String> result = SubQueryParser.parseQuery("{X}?");
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("{X}"));
  }
@Test
  public void testParseQueryWithUnbalancedWildcardsStillReturnsResult() throws Exception {
    List<String> result = SubQueryParser.parseQuery("({X})+3x");
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("{X}"));
  }
@Test(expected = SearchException.class)
  public void testParseFailsWithOnlyOpenParenEscaped() throws Exception {
    SubQueryParser.parseQuery("Test \\(");
  }
@Test(expected = SearchException.class)
  public void testParseQueryWithSingleOpeningParenthesisFails() throws Exception {
    SubQueryParser.parseQuery("(");
  }
@Test(expected = SearchException.class)
  public void testParseQueryWithSingleClosingParenthesisFails() throws Exception {
    SubQueryParser.parseQuery(")");
  }
@Test
  public void testBracketedOrWithWhitespaceAndEmptyBranches() throws Exception {
    List<String> result = SubQueryParser.parseQuery("({A} | )");
    assertEquals(2, result.size());
    assertTrue(result.get(0).contains("{A}") || result.get(1).contains("{A}"));
    assertTrue(result.get(0).contains("()") || result.get(1).contains("()"));
  }
@Test
  public void testConsecutiveORWithNothingBetween() throws Exception {
    List<String> result = SubQueryParser.parseQuery("| |");
    assertEquals(3, result.size());
    assertEquals("()", result.get(0));
    assertEquals("()", result.get(1));
    assertEquals("()", result.get(2));
  }
@Test
  public void testParseQueryWithMultipleNestedGroupsAndEscapes() throws Exception {
    List<String> result = SubQueryParser.parseQuery("({A}\\({B}) | {C})");
    assertEquals(2, result.size());
    assertTrue(result.get(0).contains("{A}\\(") || result.get(1).contains("{A}\\("));
    assertTrue(result.get(0).contains("{B}") || result.get(1).contains("{B}"));
    assertTrue(result.get(0).contains("{C}") || result.get(1).contains("{C}"));
  }
@Test
  public void testQueryWithUnescapedPipeInsideTokenIsIgnored() throws Exception {
    List<String> result = SubQueryParser.parseQuery("{A|B}");
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("{A|B}"));
  }
@Test
  public void testQueryWithMultipleOpenThenSingleCloseBracket() throws Exception {
    try {
      SubQueryParser.parseQuery("((A)");
      fail("Expected SearchException due to unmatched opening bracket");
    } catch (SearchException e) {
      assertTrue(e.getMessage().toLowerCase().contains("unbalanced brackets"));
    }
  }
@Test
  public void testQueryWithMultipleCloseThenSingleOpenBracket() throws Exception {
    try {
      SubQueryParser.parseQuery("A))");
      fail("Expected SearchException due to unmatched closing bracket");
    } catch (SearchException e) {
      assertTrue(e.getMessage().toLowerCase().contains("unbalanced brackets"));
    }
  }
@Test
  public void testParseQueryWithImproperWildcardPlacement() throws Exception {
    List<String> result = SubQueryParser.parseQuery("*3({A})");
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("*3({A})") || result.get(0).contains("{A}"));
  }
@Test
  public void testParseQueryWithWildcardAndNoiseCharacters() throws Exception {
    List<String> result = SubQueryParser.parseQuery("({A})+3abc");
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("{A}"));
    assertTrue(result.get(0).contains("abc"));
  }
@Test
  public void testParseGroupWithSpecialCharactersInsideEscapedPipe() throws Exception {
    List<String> result = SubQueryParser.parseQuery("({X}\\|{Y})");
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("\\|"));
    assertTrue(result.get(0).contains("{X}"));
    assertTrue(result.get(0).contains("{Y}"));
  }
@Test
  public void testScanQueryContainingOnlyEscapedCharactersReturnsFalse() {
    boolean scan = SubQueryParser.scanQueryForOrOrBracket("\\(\\)\\|");
    assertFalse(scan);
  }
@Test
  public void testScanQueryWithValidBracketButEscapedOrReturnsTrue() {
    boolean scan = SubQueryParser.scanQueryForOrOrBracket("({X}\\|Y)");
    assertTrue(scan);
  }
@Test
  public void testFindOrTokensReturnsSingleTokenIfNoSeparatorsFound() {
    List<String> tokens = SubQueryParser.findOrTokens("{A}{B}{C}");
    assertEquals(1, tokens.size());
    assertEquals("{A}{B}{C}", tokens.get(0));
  }
@Test
  public void testFindOrTokensHandlesMixedBracketsCorrectly() {
    List<String> tokens = SubQueryParser.findOrTokens("{A}|({B}|{C})|{D}");
    assertEquals(3, tokens.size());
    assertEquals("{A}", tokens.get(0));
    assertEquals("({B}|{C})", tokens.get(1));
    assertEquals("{D}", tokens.get(2));
  }
@Test
  public void testQueryWithDuplicateLogicIsNormalized() throws Exception {
    List<String> result = SubQueryParser.parseQuery("({A}|{A})");
    assertEquals(1, result.size());
    assertEquals("({A})", result.get(0));
  }
@Test
  public void testParseQueryWithOrInMiddleWithoutBrackets() throws Exception {
    List<String> result = SubQueryParser.parseQuery("{A}|{B}{C}");
    assertEquals(2, result.size());
    assertTrue(result.get(0).contains("{A}") || result.get(1).contains("{A}"));
    assertTrue(result.get(0).contains("{B}{C}") || result.get(1).contains("{B}{C}"));
  }
@Test
  public void testQueryWithMultipleRedundantOperatorsAndWhiteSpace() throws Exception {
    List<String> result = SubQueryParser.parseQuery(" {A} |     | {B} ");
    assertEquals(3, result.size());
    assertTrue(result.get(0).contains("{A}") || result.get(1).contains("{A}") || result.get(2).contains("{A}"));
    assertTrue(result.get(0).contains("{B}") || result.get(1).contains("{B}") || result.get(2).contains("{B}"));
  }
@Test
  public void testParseQueryWithOnlyWhitespaceReturnsEmptyList() throws Exception {
    List<String> result = SubQueryParser.parseQuery("     \t ");
    assertEquals(0, result.size());
  }
@Test
  public void testParseOrGroupWithOptionalSymbolAppended() throws Exception {
    List<String> result = SubQueryParser.parseQuery("({A}|{B})?");
    assertEquals(2, result.size());
    assertTrue(result.get(0).contains("{A}") || result.get(1).contains("{A}"));
    assertTrue(result.get(0).contains("{B}") || result.get(1).contains("{B}"));
    assertTrue(result.get(0).contains("{__o__}") || result.get(1).contains("{__o__}"));
  }
@Test
  public void testParseDeeplyNestedRedundantStructure() throws Exception {
    List<String> result = SubQueryParser.parseQuery("((((({A}))))({B}|{C}))");
    assertEquals(2, result.size());
    assertTrue(result.get(0).contains("{A}") && result.get(0).contains("{B}"));
    assertTrue(result.get(1).contains("{C}"));
  } 
}