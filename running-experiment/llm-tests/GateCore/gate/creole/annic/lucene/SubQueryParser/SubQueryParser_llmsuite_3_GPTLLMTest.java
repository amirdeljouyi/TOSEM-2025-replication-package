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

public class SubQueryParser_llmsuite_3_GPTLLMTest {

 @Test
  public void testSimpleLiteralQuery() throws Exception {
    List<String> results = SubQueryParser.parseQuery("{A}");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertEquals("({A})", results.get(0));
  }
@Test
  public void testOrQuerySingleLevel() throws Exception {
    List<String> results = SubQueryParser.parseQuery("{A} | {B}");
    assertNotNull(results);
    assertEquals(2, results.size());
    assertTrue(results.contains("{A}"));
    assertTrue(results.contains("{B}"));
  }
@Test
  public void testNestedOrQuery() throws Exception {
    List<String> results = SubQueryParser.parseQuery("({A}|{B}){C}");
    assertNotNull(results);
    assertEquals(2, results.size());
    assertTrue(results.contains("{A}{C}"));
    assertTrue(results.contains("{B}{C}"));
  }
@Test
  public void testWildcardPlusNumber() throws Exception {
    List<String> results = SubQueryParser.parseQuery("({X})+2");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertTrue(results.get(0).contains("{X}"));
    assertTrue(results.get(0).contains("|"));
  }
@Test
  public void testWildcardStarNumberIncludesOptional() throws Exception {
    List<String> results = SubQueryParser.parseQuery("({X})*2");
    assertNotNull(results);
    assertEquals(1, results.size());
    String result = results.get(0);
    assertTrue(result.contains("{__o__}"));
  }
@Test
  public void testWildcardSinglePlus() throws Exception {
    List<String> results = SubQueryParser.parseQuery("({X})+");
    assertNotNull(results);
    assertEquals(1, results.size());
    String result = results.get(0);
    assertTrue(result.contains("{X}"));
  }
@Test
  public void testMultipleOrAndWildcard() throws Exception {
    List<String> results = SubQueryParser.parseQuery("({A}|{B})({C}|{D})");
    assertNotNull(results);
    assertEquals(4, results.size());
    assertTrue(results.contains("{A}{C}"));
    assertTrue(results.contains("{A}{D}"));
    assertTrue(results.contains("{B}{C}"));
    assertTrue(results.contains("{B}{D}"));
  }
@Test
  public void testTripleNestedOr() throws Exception {
    List<String> results = SubQueryParser.parseQuery("((({A}|{B})))");
    assertNotNull(results);
    assertEquals(2, results.size());
    assertTrue(results.contains("{A}"));
    assertTrue(results.contains("{B}"));
  }
@Test(expected = SearchException.class)
  public void testUnbalancedMissingClosingBracket() throws Exception {
    SubQueryParser.parseQuery("({A}|{B}");
  }
@Test(expected = SearchException.class)
  public void testUnbalancedMissingOpeningBracket() throws Exception {
    SubQueryParser.parseQuery("{A}|{B})");
  }
@Test
  public void testOrInsideBracketsCombinedWithSecond() throws Exception {
    List<String> results = SubQueryParser.parseQuery("({A}|({B}|{C})){D}");
    assertNotNull(results);
    assertEquals(3, results.size());
    assertTrue(results.contains("{A}{D}"));
    assertTrue(results.contains("{B}{D}"));
    assertTrue(results.contains("{C}{D}"));
  }
@Test
  public void testQueryTrimsWhitespace() throws Exception {
    List<String> results = SubQueryParser.parseQuery("  ({A}  |  {B} ) {C}  ");
    assertNotNull(results);
    assertEquals(2, results.size());
    assertTrue(results.contains("{A}{C}"));
    assertTrue(results.contains("{B}{C}"));
  }
@Test
  public void testScanQueryForOrOrBracketReturnsTrueForOr() {
    boolean result = SubQueryParser.scanQueryForOrOrBracket("{A}|{B}");
    assertTrue(result);
  }
@Test
  public void testScanQueryForOrOrBracketReturnsTrueForParentheses() {
    boolean result = SubQueryParser.scanQueryForOrOrBracket("({A})");
    assertTrue(result);
  }
@Test
  public void testScanQueryForOrOrBracketReturnsFalse() {
    boolean result1 = SubQueryParser.scanQueryForOrOrBracket("ABC{X}");
    boolean result2 = SubQueryParser.scanQueryForOrOrBracket("{A}{B}");
    assertFalse(result1);
    assertFalse(result2);
  }
@Test
  public void testOrTokensFlat() {
    List<String> tokens = SubQueryParser.findOrTokens("{X}|{Y}|{Z}");
    assertNotNull(tokens);
    assertEquals(3, tokens.size());
    assertEquals("{X}", tokens.get(0));
    assertEquals("{Y}", tokens.get(1));
    assertEquals("{Z}", tokens.get(2));
  }
@Test
  public void testOrTokensNested() {
    List<String> tokens = SubQueryParser.findOrTokens("{X}|({Y}|{Z})");
    assertNotNull(tokens);
    assertEquals(2, tokens.size());
    assertEquals("{X}", tokens.get(0));
    assertEquals("({Y}|{Z})", tokens.get(1));
  }
@Test
  public void testWildcardQuestionMark() throws Exception {
    List<String> results = SubQueryParser.parseQuery("({W})?2");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertTrue(results.get(0).contains("{__o__}"));
  }
@Test
  public void testComplexNestedCombination() throws Exception {
    List<String> results = SubQueryParser.parseQuery("({A}|({B}|{C}))({D}|{E})");
    assertNotNull(results);
    assertEquals(6, results.size());
    assertTrue(results.contains("{A}{D}"));
    assertTrue(results.contains("{A}{E}"));
    assertTrue(results.contains("{B}{D}"));
    assertTrue(results.contains("{B}{E}"));
    assertTrue(results.contains("{C}{D}"));
    assertTrue(results.contains("{C}{E}"));
  }
@Test
  public void testEmptyInputReturnsEmptyList() throws Exception {
    List<String> results = SubQueryParser.parseQuery("");
    assertNotNull(results);
    assertEquals(0, results.size());
  }
@Test
  public void testRedundantDuplicatesAreRemoved() throws Exception {
    List<String> results = SubQueryParser.parseQuery("((({A}|{A})){B})");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertEquals("{A}{B}", results.get(0));
  }
@Test
  public void testWildcardZeroOptional() throws Exception {
    List<String> results = SubQueryParser.parseQuery("({Z})*0");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertTrue(results.get(0).contains("{__o__}"));
  }
@Test(expected = SearchException.class)
  public void testEscapeCharacterOrSymbolUnsupported() throws Exception {
    SubQueryParser.parseQuery("({A}\\|{B})");
  }
@Test
  public void testSingleCharacterQuery() throws Exception {
    List<String> results = SubQueryParser.parseQuery("A");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertEquals("(A)", results.get(0));
  }
@Test
  public void testWhitespaceOnlyQueryReturnsEmptyList() throws Exception {
    List<String> results = SubQueryParser.parseQuery("     ");
    assertNotNull(results);
    assertTrue(results.isEmpty());
  }
@Test
  public void testOrInsideNestedBrackets() throws Exception {
    List<String> results = SubQueryParser.parseQuery("({A}|({B}|({C}|{D})))");
    assertNotNull(results);
    assertEquals(4, results.size());
    assertTrue(results.contains("{A}"));
    assertTrue(results.contains("{B}"));
    assertTrue(results.contains("{C}"));
    assertTrue(results.contains("{D}"));
  }
@Test
  public void testEscapedOpenBracketIsNotParsed() throws Exception {
    List<String> results = SubQueryParser.parseQuery("\\({A}\\)");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertEquals("(\\({A}\\))", results.get(0));
  }
@Test
  public void testEscapedOrIsNotSplitAsOrToken() throws Exception {
    List<String> results = SubQueryParser.parseQuery("{A}\\|{B}");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertEquals("({A}\\|{B})", results.get(0));
  }
@Test
  public void testTrailingWildcardWithNoNumber() throws Exception {
    List<String> results = SubQueryParser.parseQuery("({A})*");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertTrue(results.get(0).contains("{A}"));
    assertTrue(results.get(0).contains("{__o__}"));
  }
@Test
  public void testVeryDeeplyNestedBrackets() throws Exception {
    List<String> results = SubQueryParser.parseQuery("((((((({X}|{Y})))))))");
    assertNotNull(results);
    assertEquals(2, results.size());
    assertTrue(results.contains("{X}"));
    assertTrue(results.contains("{Y}"));
  }
@Test
  public void testInvalidWildcardWithLetterAfterSymbol() throws Exception {
    List<String> results = SubQueryParser.parseQuery("({X})*A");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertTrue(results.get(0).contains("{X}"));
  }
@Test
  public void testZeroRepeatWithQuestionMark() throws Exception {
    List<String> results = SubQueryParser.parseQuery("({B})?0");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertTrue(results.get(0).contains("{__o__}"));
  }
@Test
  public void testWildcardWithoutBracketsIsIgnored() throws Exception {
    List<String> results = SubQueryParser.parseQuery("A*3");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertEquals("(A*3)", results.get(0));
  }
@Test
  public void testConsecutiveNestedOptionalWildcards() throws Exception {
    List<String> results = SubQueryParser.parseQuery("(({A})?2)?2");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertTrue(results.get(0).contains("{__o__}"));
  }
@Test
  public void testEmptyBracketsStillParsed() throws Exception {
    List<String> results = SubQueryParser.parseQuery("({})");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertTrue(results.get(0).contains("{}"));
  }
@Test(expected = SearchException.class)
  public void testBracketMismatchExtraClosing() throws Exception {
    SubQueryParser.parseQuery("({A}){B})");
  }
@Test(expected = SearchException.class)
  public void testBracketMismatchExtraOpening() throws Exception {
    SubQueryParser.parseQuery("(({A}){B}");
  }
@Test
  public void testBraceCharactersAutoWrap() throws Exception {
    List<String> results = SubQueryParser.parseQuery("{abc}");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertEquals("({abc})", results.get(0));
  }
@Test
  public void testQueryWithMultipleSpacesBetweenTokens() throws Exception {
    List<String> results = SubQueryParser.parseQuery("{A}       {B}    ");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertEquals("({A}       {B})", results.get(0));
  }
@Test
  public void testOrWithMixedSpacingBetweenTokens() throws Exception {
    List<String> results = SubQueryParser.parseQuery(" {A} |{B}     | { C } ");
    assertNotNull(results);
    assertEquals(3, results.size());
    assertTrue(results.contains("{A}"));
    assertTrue(results.contains("{B}"));
    assertTrue(results.contains("{ C }"));
  }
@Test
  public void testOnlyWildcardSymbolWithoutBrackets() throws Exception {
    List<String> results = SubQueryParser.parseQuery("*3");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertEquals("(*3)", results.get(0));
  }
@Test
  public void testNestedOrWithOptionalPlaceholderExpanded() throws Exception {
    List<String> results = SubQueryParser.parseQuery("({A}|{B})?");
    assertNotNull(results);
    assertEquals(3, results.size());
    assertTrue(results.contains("{A}"));
    assertTrue(results.contains("{B}"));
    assertTrue(results.contains("{__o__}"));
  }
@Test
  public void testEmptyParensArePreserved() throws Exception {
    List<String> results = SubQueryParser.parseQuery("()");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertEquals("(())", results.get(0));
  }
@Test
  public void testOrWithNoLeftOperand() throws Exception {
    List<String> results = SubQueryParser.parseQuery("| {B}");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertTrue(results.get(0).contains("{B}"));
  }
@Test
  public void testOrWithNoRightOperand() throws Exception {
    List<String> results = SubQueryParser.parseQuery("{A} |");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertTrue(results.get(0).contains("{A}"));
  }
@Test
  public void testOrWithEmptyOperands() throws Exception {
    List<String> results = SubQueryParser.parseQuery("|");
    assertNotNull(results);
    assertEquals(0, results.size()); 
  }
@Test
  public void testDoubleOrWithEmptyMiddle() throws Exception {
    List<String> results = SubQueryParser.parseQuery("{A}||{B}");
    assertNotNull(results);
    assertEquals(2, results.size());
    assertTrue(results.contains("{A}"));
    assertTrue(results.contains("{B}"));
  }
@Test
  public void testMultipleWildcardsDifferentOperators() throws Exception {
    List<String> results = SubQueryParser.parseQuery("({X})+({Y})*({Z})?");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertTrue(results.get(0).contains("{X}"));
    assertTrue(results.get(0).contains("{Y}"));
    assertTrue(results.get(0).contains("{Z}"));
    assertTrue(results.get(0).contains("{__o__}"));
  }
@Test
  public void testWildcardsWithMultiDigitRepeat() throws Exception {
    List<String> results = SubQueryParser.parseQuery("({X})+12");
    assertNotNull(results);
    assertEquals(1, results.size());
    String query = results.get(0);
    assertTrue(query.contains("{X}"));
    assertTrue(query.contains("({X})({X})")); 
  }
@Test
  public void testWildcardWithIncompleteNumericValue() throws Exception {
    List<String> results = SubQueryParser.parseQuery("({A})+1abc");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertTrue(results.get(0).contains("{A}"));
  }
@Test(expected = SearchException.class)
  public void testOpenBracketPrecededByEscapeDoesNotOpen() throws Exception {
    SubQueryParser.parseQuery("\\({A})");
  }
@Test
  public void testBackslashBeforeBracketIgnored() throws Exception {
    List<String> results = SubQueryParser.parseQuery("\\(A\\)");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertEquals("(\\(A\\))", results.get(0)); 
  }
@Test
  public void testBackslashBeforeOrSymbolIgnored() throws Exception {
    List<String> results = SubQueryParser.parseQuery("{A}\\|{B}");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertEquals("({A}\\|{B})", results.get(0));
  }
@Test
  public void testBackslashBeforeClosingBracketIgnored() throws Exception {
    List<String> results = SubQueryParser.parseQuery("{A}\\)");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertEquals("({A}\\))", results.get(0));
  }
@Test
  public void testPlainTextNoBrackets() throws Exception {
    List<String> results = SubQueryParser.parseQuery("plainword");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertEquals("(plainword)", results.get(0));
  }
@Test
  public void testNestedGroupWithOrAndRepetition() throws Exception {
    List<String> results = SubQueryParser.parseQuery("(({A}|{B})+2)");
    assertNotNull(results);
    assertEquals(2, results.size());
    assertTrue(results.contains("{A}{A}"));
    assertTrue(results.contains("{B}{B}"));
  }
@Test
  public void testOptionalGroupWithInnerOr() throws Exception {
    List<String> results = SubQueryParser.parseQuery("(({A}|{B})?)");
    assertNotNull(results);
    assertEquals(3, results.size());
    assertTrue(results.contains("{__o__}"));
    assertTrue(results.contains("{A}"));
    assertTrue(results.contains("{B}"));
  }
@Test
  public void testMalformedWildCardNoParamUsedAsSingleRepeat() throws Exception {
    List<String> results = SubQueryParser.parseQuery("({X})+badtext");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertTrue(results.get(0).contains("{X}"));
  }
@Test
  public void testEscapedWhitespaceSequence() throws Exception {
    List<String> results = SubQueryParser.parseQuery("\\ {A}  \\  {B}");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertEquals("(\\ {A}  \\  {B})", results.get(0));
  }
@Test(expected = SearchException.class)
  public void testMismatchedBracketWithoutClosing() throws Exception {
    SubQueryParser.parseQuery("({A}");
  }
@Test(expected = SearchException.class)
  public void testMismatchedBracketWithoutOpening() throws Exception {
    SubQueryParser.parseQuery("{A})");
  }
@Test
  public void testWhitespaceBetweenWildcardsAndGroups() throws Exception {
    List<String> results = SubQueryParser.parseQuery("({A}) +2 ({B})*2 ({C})?1");
    assertNotNull(results);
    assertEquals(1, results.size());
  }
@Test
  public void testOnlyOpenParenthesis() throws Exception {
    List<String> results = SubQueryParser.parseQuery("(");
    assertNotNull(results);
    assertTrue(results.isEmpty());
  }
@Test
  public void testOnlyCloseParenthesis() throws Exception {
    try {
      SubQueryParser.parseQuery(")");
      fail("Expected SearchException");
    } catch (SearchException e) {
      assertEquals("unbalanced brackets", e.getReason());
    }
  }
@Test
  public void testEmptyGroup() throws Exception {
    List<String> results = SubQueryParser.parseQuery("()");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertEquals("(()())", results.get(0));
  }
@Test
  public void testEmptyGroupWithWildcard() throws Exception {
    List<String> results = SubQueryParser.parseQuery("()*3");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertTrue(results.get(0).contains("{__o__}"));
  }
@Test
  public void testEscapedBackslashBeforeParens() throws Exception {
    List<String> results = SubQueryParser.parseQuery("\\\\({A})");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertTrue(results.get(0).contains("\\\\({A})") || results.get(0).contains("({A})"));
  }
@Test
  public void testEscapeSequenceBeforePipeIgnored() throws Exception {
    List<String> results = SubQueryParser.parseQuery("{A}\\|{B}");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertEquals("({A}\\|{B})", results.get(0));
  }
@Test
  public void testEscapeSequenceBeforeCloseBracketIgnored() throws Exception {
    List<String> results = SubQueryParser.parseQuery("{A}\\)");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertEquals("({A}\\))", results.get(0));
  }
@Test
  public void testOrAtStartOfToken() throws Exception {
    List<String> results = SubQueryParser.parseQuery("|{X}");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertEquals("{X}", results.get(0));
  }
@Test
  public void testOrAtEndOfToken() throws Exception {
    List<String> results = SubQueryParser.parseQuery("{X}|");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertEquals("{X}", results.get(0));
  }
@Test
  public void testOrWithEmptyMiddleLiteral() throws Exception {
    List<String> results = SubQueryParser.parseQuery("{X}||{Y}");
    assertNotNull(results);
    assertEquals(2, results.size());
    assertTrue(results.contains("{X}"));
    assertTrue(results.contains("{Y}"));
  }
@Test
  public void testWildcardWithoutNumericRepetition() throws Exception {
    List<String> results = SubQueryParser.parseQuery("({T})*");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertTrue(results.get(0).contains("{__o__}"));
    assertTrue(results.get(0).contains("{T}"));
  }
@Test
  public void testPlusFollowedByTwoDigits() throws Exception {
    List<String> results = SubQueryParser.parseQuery("({X})+12");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertTrue(results.get(0).contains("{X}"));
  }
@Test
  public void testStarWithNonDigitFollowing() throws Exception {
    List<String> results = SubQueryParser.parseQuery("({X})*a");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertTrue(results.get(0).contains("{X}"));
  }
@Test
  public void testQueryWithOnlyWildcardCharacter() throws Exception {
    List<String> results = SubQueryParser.parseQuery("*");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertEquals("(*)", results.get(0));
  }
@Test
  public void testQueryWithOnlyPipeCharacter() throws Exception {
    List<String> results = SubQueryParser.parseQuery("|");
    assertNotNull(results);
    assertTrue(results.isEmpty());
  }
@Test
  public void testNestedWildcardWithZeroRepetition() throws Exception {
    List<String> results = SubQueryParser.parseQuery("(({A})+0)");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertTrue(results.get(0).contains("{A}") || results.get(0).contains("{__o__}"));
  }
@Test
  public void testRedundantWhitespaceBetweenTokens() throws Exception {
    List<String> results = SubQueryParser.parseQuery("   {A}     {B}     {C}   ");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertEquals("({A}     {B}     {C})", results.get(0));
  }
@Test
  public void testEscapedOpenBracketPreventingOpening() throws Exception {
    List<String> results = SubQueryParser.parseQuery("\\({A}|{B})");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertEquals("(\\({A}|{B}))", results.get(0));
  }
@Test
  public void testWildcardWithoutFollowupDigits() throws Exception {
    List<String> results = SubQueryParser.parseQuery("({X})+ ");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertTrue(results.get(0).contains("{X}"));
  }
@Test
  public void testWildcardWithZeroRepetitionExplicit() throws Exception {
    List<String> results = SubQueryParser.parseQuery("({Y})+0");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertTrue(results.get(0).contains("{Y}")); 
  }
@Test
  public void testTokenWithEscapedPipeOnly() throws Exception {
    List<String> results = SubQueryParser.parseQuery("{A}\\|{B}");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertTrue(results.get(0).contains("\\|"));
    assertFalse(results.contains("{B}")); 
  }
@Test
  public void testTokenWithEscapeInMiddle() throws Exception {
    List<String> results = SubQueryParser.parseQuery("word\\(test\\)");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertEquals("(word\\(test\\))", results.get(0));
  }
@Test
  public void testMultipleOrNotGrouped() throws Exception {
    List<String> results = SubQueryParser.parseQuery("{A}|{B}|{C}");
    assertNotNull(results);
    assertEquals(3, results.size());
    assertTrue(results.contains("{A}"));
    assertTrue(results.contains("{B}"));
    assertTrue(results.contains("{C}"));
  }
@Test
  public void testEmptyWildcardContent() throws Exception {
    List<String> results = SubQueryParser.parseQuery("()?");
    assertNotNull(results);
    assertEquals(2, results.size());
    assertTrue(results.contains("()"));
    assertTrue(results.contains("{__o__}"));
  }
@Test
  public void testPipeAtEndOfBracketedGroup() throws Exception {
    List<String> results = SubQueryParser.parseQuery("({A}|)");
    assertNotNull(results);
    assertTrue(results.contains("{A}"));
  }
@Test
  public void testPipeAtStartOfBracketedGroup() throws Exception {
    List<String> results = SubQueryParser.parseQuery("(|{B})");
    assertNotNull(results);
    assertTrue(results.contains("{B}"));
  }
@Test
  public void testRepeatingEmptyGroup() throws Exception {
    List<String> results = SubQueryParser.parseQuery("()*2");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertTrue(results.get(0).contains("{__o__}") || results.get(0).contains("()"));
  }
@Test
  public void testTokenWithMixedEscapeAndWildcard() throws Exception {
    List<String> results = SubQueryParser.parseQuery("({X\\(1\\)})");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertTrue(results.get(0).contains("{X\\(1\\)}"));
  }
@Test
  public void testQueryContainingUnescapedBackslashOnly() throws Exception {
    List<String> results = SubQueryParser.parseQuery("\\");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertEquals("(\\)", results.get(0));
  }
@Test
  public void testQueryWithMultipleNestedOrsAndWildcard() throws Exception {
    List<String> results = SubQueryParser.parseQuery("(({A}|{B})|({C}|{D}))*2");
    assertNotNull(results);
    assertTrue(results.size() >= 4); 
  }
@Test
  public void testSingleOrSymbolOnly() throws Exception {
    List<String> results = SubQueryParser.parseQuery("|");
    assertNotNull(results);
    assertEquals(0, results.size());
  }
@Test
  public void testUsingQuestionMarkWithoutDigits() throws Exception {
    List<String> results = SubQueryParser.parseQuery("({X})?");
    assertNotNull(results);
    assertEquals(2, results.size());
    assertTrue(results.contains("{__o__}"));
    assertTrue(results.contains("{X}"));
  }
@Test
  public void testGroupContainingOnlyWhitespace() throws Exception {
    List<String> results = SubQueryParser.parseQuery("(   )");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertTrue(results.get(0).contains("(   )"));
  }

@Test(expected = SearchException.class)
  public void testNestedUnbalancedOpeningMissingClosing() throws Exception {
    SubQueryParser.parseQuery("(({A}");
  }
@Test
  public void testRedundantOrBranchesRemoved() throws Exception {
    List<String> results = SubQueryParser.parseQuery("({X}|{X})");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertEquals("{X}", results.get(0));
  }
@Test
  public void testRedundantFinalEmptyQueryOmitted() throws Exception {
    List<String> results = SubQueryParser.parseQuery("{A}({B}|{C})");
    assertNotNull(results);
    assertFalse(results.contains("")); 
  }
@Test
  public void testMixOfLiteralAndBracketedSymbols() throws Exception {
    List<String> results = SubQueryParser.parseQuery("{A}B({C}|D)");
    assertNotNull(results);
    assertTrue(results.size() > 0);
    for (String result : results) {
      assertTrue(result.contains("{A}"));
      assertTrue(result.contains("B"));
    }
  }
@Test
  public void testEscapedOrInsideNestedGroup() throws Exception {
    List<String> results = SubQueryParser.parseQuery("({A}\\|{B}){C}");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertEquals("({A}\\|{B}{C})", results.get(0));
  }
@Test
  public void testEscapeCharacterAtEnd() throws Exception {
    List<String> results = SubQueryParser.parseQuery("text\\");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertEquals("(text\\)", results.get(0));
  }
@Test
  public void testBackToBackOrSymbols() throws Exception {
    List<String> results = SubQueryParser.parseQuery("{A}|||{B}");
    assertNotNull(results);
    assertEquals(2, results.size());
    assertTrue(results.contains("{A}"));
    assertTrue(results.contains("{B}"));
  }
@Test
  public void testDoubleWildcardWithConflictingSuffixes() throws Exception {
    List<String> results = SubQueryParser.parseQuery("({X})+2*3");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertTrue(results.get(0).contains("{X}")); 
  }
@Test
  public void testUnexpectedCharactersAfterWildcard() throws Exception {
    List<String> results = SubQueryParser.parseQuery("({Y})*2ABC");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertTrue(results.get(0).contains("{Y}")); 
    assertTrue(results.get(0).contains("ABC"));
  }
@Test
  public void testUnknownOperatorsAreIncludedAsLiterals() throws Exception {
    List<String> results = SubQueryParser.parseQuery("{A}#({B})");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertTrue(results.get(0).contains("#"));
    assertTrue(results.get(0).contains("{A}"));
    assertTrue(results.get(0).contains("{B}"));
  }
@Test
  public void testMisplacedExtraClosingBracketWithOr() throws Exception {
    try {
      SubQueryParser.parseQuery("({A}|{B}))");
      fail("Expected SearchException to be thrown");
    } catch (SearchException ex) {
      assertEquals("unbalanced brackets", ex.getReason());
      assertEquals("a opening bracket (() is missing for this closing bracket", ex.getMessage());
    }
  }
@Test
  public void testBareNestedOpenParensWithoutClosing() throws Exception {
    try {
      SubQueryParser.parseQuery("(({A}|{B})");
      fail("Expected SearchException to be thrown");
    } catch (SearchException ex) {
      assertEquals("unbalanced brackets", ex.getReason());
    }
  }
@Test
  public void testInputOnlyEscapeCharacters() throws Exception {
    List<String> results = SubQueryParser.parseQuery("\\\\\\\\");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertEquals("(\\\\\\\\)", results.get(0));
  }
@Test
  public void testEmptyPipeBranchesInsideGroup() throws Exception {
    List<String> results = SubQueryParser.parseQuery("(|)");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertEquals("{__o__}", results.get(0));
  }
@Test
  public void testSpacesBeforeAndAfterOrInsideGroup() throws Exception {
    List<String> results = SubQueryParser.parseQuery("({A} | {B})");
    assertNotNull(results);
    assertEquals(2, results.size());
    assertTrue(results.contains("{A}"));
    assertTrue(results.contains("{B}"));
  }
@Test
  public void testDuplicateTextExpansionWhenNoDuplicationNeeded() throws Exception {
    List<String> results = SubQueryParser.parseQuery("{A}{B}|{C}");
    assertNotNull(results);
    assertEquals(2, results.size());
    assertTrue(results.contains("{A}{B}"));
    assertTrue(results.contains("{C}"));
  }
@Test
  public void testGroupWithOnlyWhitespaceAndWildcard() throws Exception {
    List<String> results = SubQueryParser.parseQuery("(    )*2");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertTrue(results.get(0).contains("    "));
  }
@Test
  public void testWhitespaceOnlyInsideOptionalGroup() throws Exception {
    List<String> results = SubQueryParser.parseQuery("(   )?");
    assertNotNull(results);
    assertEquals(2, results.size());
    assertTrue(results.contains("{__o__}"));
    assertTrue(results.get(0).contains("   ") || results.get(1).contains("   "));
  }
@Test
  public void testOrGroupWithEmptyFinalClause() throws Exception {
    List<String> results = SubQueryParser.parseQuery("({X}|{Y}|)");
    assertNotNull(results);
    assertEquals(2, results.size());
    assertTrue(results.contains("{X}"));
    assertTrue(results.contains("{Y}")); 
  }
@Test
  public void testWildcardWithoutBracketsFollowedByDigits() throws Exception {
    List<String> results = SubQueryParser.parseQuery("X+3Y");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertEquals("(X+3Y)", results.get(0)); 
  }
@Test(expected = SearchException.class)
  public void testOnlyOpeningBracketWithoutClosingAnywhere() throws Exception {
    SubQueryParser.parseQuery("({X}");
  }
@Test
  public void testGroupContainingPipeAndTextWithEscapedBackslash() throws Exception {
    List<String> results = SubQueryParser.parseQuery("({a}|\\{b\\})");
    assertNotNull(results);
    assertEquals(2, results.size());
    assertTrue(results.get(0).contains("{a}") || results.get(1).contains("{a}"));
    assertTrue(results.get(0).contains("\\{b\\}") || results.get(1).contains("\\{b\\}"));
  }
@Test
  public void testValidQueryWithTrailingSpace() throws Exception {
    List<String> results = SubQueryParser.parseQuery("{A}|{B} ");
    assertNotNull(results);
    assertEquals(2, results.size());
    assertTrue(results.contains("{A}"));
    assertTrue(results.contains("{B}"));
  }
@Test
  public void testMultipleConsecutiveEscapeCharsInsideGroup() throws Exception {
    List<String> results = SubQueryParser.parseQuery("({A}\\|{B}\\|{C})");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertTrue(results.get(0).contains("\\|"));
    assertFalse(results.contains("{B}"));
  }
@Test
  public void testMixedRawAndBracketedTokens() throws Exception {
    List<String> results = SubQueryParser.parseQuery("A{B}(C|D)");
    assertNotNull(results);
    assertEquals(2, results.size());
    assertTrue(results.get(0).contains("A"));
    assertTrue(results.get(0).contains("{B}"));
    assertTrue(results.get(0).contains("C") || results.get(0).contains("D"));
  }
@Test
  public void testRedundantWildcardsAreHandledGracefully() throws Exception {
    List<String> results = SubQueryParser.parseQuery("(({A})*2)*1");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertTrue(results.get(0).contains("{A}"));
    assertTrue(results.get(0).contains("{__o__}"));
  }
@Test
  public void testSpaceOnlyTokenAfterWildcard() throws Exception {
    List<String> results = SubQueryParser.parseQuery("({B})*2 ");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertTrue(results.get(0).contains("{B}"));
  }
@Test
  public void testNestedEmptyGroupsParse() throws Exception {
    List<String> results = SubQueryParser.parseQuery("((()))");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertEquals("(((())))", results.get(0));
  }
@Test
  public void testPipeOutsideGroupIgnoredAsLiteral() throws Exception {
    List<String> results = SubQueryParser.parseQuery("{A}|text|{B}");
    assertNotNull(results);
    assertEquals(3, results.size());
    assertTrue(results.contains("{A}"));
    assertTrue(results.contains("{B}"));
    assertTrue(results.stream().anyMatch(s -> s.contains("text")));
  }
@Test
  public void testBackslashAtEndOfQueryPreserved() throws Exception {
    List<String> results = SubQueryParser.parseQuery("{A}\\");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertTrue(results.get(0).endsWith("\\" + ")")); 
  }
@Test
  public void testDoubleEscapeBeforeBracketIgnored() throws Exception {
    List<String> results = SubQueryParser.parseQuery("\\\\({X})");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertTrue(results.get(0).contains("\\\\({X})"));
  }
@Test
  public void testMixedOptionalAndRepetitionWithNumericSuffix() throws Exception {
    List<String> results = SubQueryParser.parseQuery("({A})?3({B})+2");
    assertNotNull(results);
    assertEquals(3, results.size());
    assertTrue(results.get(0).contains("{A}"));
    assertTrue(results.get(1).contains("{B}"));
  }
@Test
  public void testTokenEndingInWildcardCharacterParsedAsIs() throws Exception {
    List<String> results = SubQueryParser.parseQuery("{A*}");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertEquals("({A*})", results.get(0));
  }
@Test
  public void testGroupWithOnlyWildcardPlaceholder() throws Exception {
    List<String> results = SubQueryParser.parseQuery("({__o__})");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertEquals("({__o__})", results.get(0));
  }
@Test
  public void testRepeatingPlaceholderWithWildcard() throws Exception {
    List<String> results = SubQueryParser.parseQuery("({__o__})*2");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertTrue(results.get(0).contains("{__o__}"));
  }
@Test
  public void testGroupWithUnbalancedPipeOnlyStart() throws Exception {
    List<String> results = SubQueryParser.parseQuery("(|{X})");
    assertNotNull(results);
    assertEquals(1, results.size()); 
    assertTrue(results.contains("{X}"));
  }
@Test
  public void testGroupWithUnbalancedPipeOnlyEnd() throws Exception {
    List<String> results = SubQueryParser.parseQuery("({X}|)");
    assertNotNull(results);
    assertEquals(1, results.size()); 
    assertTrue(results.contains("{X}"));
  }
@Test
  public void testZeroRepeatPlusStillReturnsValid() throws Exception {
    List<String> results = SubQueryParser.parseQuery("({Z})+0");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertTrue(results.get(0).contains("{Z}"));
  }
@Test
  public void testZeroRepeatStarStillIncludesPlaceholder() throws Exception {
    List<String> results = SubQueryParser.parseQuery("({Z})*0");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertTrue(results.get(0).contains("{__o__}"));
  }
@Test
  public void testBracketAndWildcardNoSpacing() throws Exception {
    List<String> results = SubQueryParser.parseQuery("({Z})+1({X})*1");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertTrue(results.get(0).contains("{Z}"));
    assertTrue(results.get(0).contains("{X}"));
  }
@Test
  public void testOrWithWhitespaceAroundSymbols() throws Exception {
    List<String> results = SubQueryParser.parseQuery("{A} | {B} ");
    assertNotNull(results);
    assertEquals(2, results.size());
    assertTrue(results.contains("{A}"));
    assertTrue(results.contains("{B}"));
  }
@Test
  public void testInvalidWildcardWithNoBracketsIsLiteral() throws Exception {
    List<String> results = SubQueryParser.parseQuery("A+2");
    assertNotNull(results);
    assertEquals(1, results.size());
    assertEquals("(A+2)", results.get(0)); 
  } 
}