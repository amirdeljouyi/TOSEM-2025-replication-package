package gate.creole.annic.lucene;

import gate.*;
import gate.creole.ResourceInstantiationException;
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

public class SubQueryParser_llmsuite_2_GPTLLMTest {

 @Test
  public void testParseQueryBasicOrStatement() throws Exception {
    String input = "{A}|{B}";
    List<String> result = SubQueryParser.parseQuery(input);
    
    assertNotNull(result);
    assertEquals(2, result.size());

    String query0 = result.get(0).trim();
    String query1 = result.get(1).trim();

    boolean hasA = query0.equals("{A}") || query1.equals("{A}");
    boolean hasB = query0.equals("{B}") || query1.equals("{B}");

    assertTrue(hasA);
    assertTrue(hasB);
  }
@Test
  public void testParseQueryNestedOr() throws Exception {
    String input = "{A}({B}|{C})";
    List<String> result = SubQueryParser.parseQuery(input);

    assertNotNull(result);
    assertEquals(2, result.size());

    String query0 = result.get(0).trim();
    String query1 = result.get(1).trim();

    boolean hasAB = query0.equals("{A}{B}") || query1.equals("{A}{B}");
    boolean hasAC = query0.equals("{A}{C}") || query1.equals("{A}{C}");
    
    assertTrue(hasAB);
    assertTrue(hasAC);
  }
@Test
  public void testParseQueryWithWildcardRepeatPlus3() throws Exception {
    String input = "(A)+3";
    List<String> result = SubQueryParser.parseQuery(input);

    assertNotNull(result);
    assertEquals(1, result.size());

    String query = result.get(0);
    assertThat(query, containsString("(A)"));
    assertThat(query, containsString("((A)"));
    assertThat(query, containsString("|"));
  }
@Test
  public void testScanQueryForOrOrBracketReturnsTrueOnPipe() {
    String input = "abc|def";
    boolean result = SubQueryParser.scanQueryForOrOrBracket(input);
    assertTrue(result);
  }
@Test
  public void testScanQueryForOrOrBracketReturnsTrueOnBracket() {
    String input = "abc(def)";
    boolean result = SubQueryParser.scanQueryForOrOrBracket(input);
    assertTrue(result);
  }
@Test
  public void testScanQueryForOrOrBracketReturnsFalseForEscapedPipeAndBracket() {
    String input = "abc\\|def\\(";
    boolean result = SubQueryParser.scanQueryForOrOrBracket(input);
    assertFalse(result);
  }
@Test
  public void testFindOrTokensSimple() {
    String input = "{A}|{B}";
    List<String> result = SubQueryParser.findOrTokens(input);

    assertNotNull(result);
    assertEquals(2, result.size());

    String token0 = result.get(0).trim();
    String token1 = result.get(1).trim();

    assertTrue(token0.equals("{A}") || token1.equals("{A}"));
    assertTrue(token0.equals("{B}") || token1.equals("{B}"));
  }
@Test
  public void testFindOrTokensNested() {
    String input = "({A}{B})|({C}{D})";
    List<String> result = SubQueryParser.findOrTokens(input);

    assertNotNull(result);
    assertEquals(2, result.size());

    String token0 = result.get(0).trim();
    String token1 = result.get(1).trim();

    boolean condition1 = token0.equals("({A}{B})") || token1.equals("({A}{B})");
    boolean condition2 = token0.equals("({C}{D})") || token1.equals("({C}{D})");

    assertTrue(condition1);
    assertTrue(condition2);
  }
@Test(expected = Exception.class)
  public void testParseQueryUnbalancedOpenBracketThrowsException() throws Exception {
    String input = "({A}|{B}";
    SubQueryParser.parseQuery(input);
  }
@Test(expected = Exception.class)
  public void testParseQueryUnbalancedCloseBracketThrowsException() throws Exception {
    String input = "{A}|{B})";
    SubQueryParser.parseQuery(input);
  }
@Test
  public void testParseQueryComplexPattern() throws Exception {
    String input = "({A}|{B})(C)+2({Y}|(Z))*3";
    List<String> result = SubQueryParser.parseQuery(input);

    assertNotNull(result);
    assertEquals(2, result.size());

    String q0 = result.get(0);
    String q1 = result.get(1);

    boolean hasAorB = q0.contains("{A}") || q0.contains("{B}") ||
                      q1.contains("{A}") || q1.contains("{B}");
    
    boolean hasC = q0.contains("(C)") || q1.contains("(C)");
    boolean hasYorZ = q0.contains("{Y}") || q0.contains("(Z)") ||
                      q1.contains("{Y}") || q1.contains("(Z)");

    assertTrue(hasAorB);
    assertTrue(hasC);
    assertTrue(hasYorZ);
  }
@Test
  public void testParseQueryWithEscapedCharacters() throws Exception {
    String input = "{A}\\|{B}\\({C})";
    List<String> result = SubQueryParser.parseQuery(input);

    assertNotNull(result);
    assertEquals(1, result.size());

    String query = result.get(0);
    assertTrue(query.contains("\\|"));
    assertTrue(query.contains("\\("));
    assertTrue(query.contains("{A}"));
    assertTrue(query.contains("{B}"));
    assertTrue(query.contains("{C}"));
  }
@Test
  public void testParseQueryWithWildcardStarNoNumber() throws Exception {
    String input = "(X)*";
    List<String> result = SubQueryParser.parseQuery(input);

    assertNotNull(result);
    assertEquals(1, result.size());

    String query = result.get(0);
    assertTrue(query.contains("{__o__}"));
    assertTrue(query.contains("(X)"));
  }
@Test
  public void testParseQueryWithWildcardPlusNoNumber() throws Exception {
    String input = "(Y)+";
    List<String> result = SubQueryParser.parseQuery(input);

    assertNotNull(result);
    assertEquals(1, result.size());

    String query = result.get(0);
    assertTrue(query.contains("(Y)"));
    assertFalse(query.contains("{__o__}"));
  }
@Test
  public void testParseQueryWithWildcardQuestionNoNumber() throws Exception {
    String input = "(Z)?";
    List<String> result = SubQueryParser.parseQuery(input);

    assertNotNull(result);
    assertEquals(1, result.size());

    String query = result.get(0);
    assertTrue(query.contains("(Z)"));
    assertTrue(query.contains("{__o__}"));
  }
@Test
  public void testParseQueryUnescapedBackslashBeforeBracketIgnored() throws Exception {
    String input = "\\(Z)";
    List<String> result = SubQueryParser.parseQuery(input);

    assertNotNull(result);
    assertEquals(1, result.size());

    String query = result.get(0);
    assertTrue(query.contains("\\(Z)"));
  }
@Test
  public void testParseQueryOnlySingleOpenBracket() {
    String input = "(";
    try {
      SubQueryParser.parseQuery(input);
      fail("Expected Exception due to unmatched opening bracket");
    } catch (Exception se) {
      assertThat(se.getMessage().toLowerCase(), containsString("unbalanced"));
    }
  }
@Test
  public void testParseQueryOnlySingleCloseBracket() {
    String input = ")";
    try {
      SubQueryParser.parseQuery(input);
      fail("Expected Exception due to unmatched closing bracket");
    } catch (Exception se) {
      assertThat(se.getMessage().toLowerCase(), containsString("unbalanced"));
    }
  }
@Test
  public void testParseQueryMultipleEmptyGroups() throws Exception {
    String input = "(()|())";
    List<String> result = SubQueryParser.parseQuery(input);

    assertNotNull(result);
    assertEquals(1, result.size());

    String query = result.get(0).trim();
    assertTrue(query.equals("") || query.equals(" "));
  }
@Test
  public void testParseQueryComplexNestedOrs() throws Exception {
    String input = "(({A}|{B})|({C}|{D}))";
    List<String> result = SubQueryParser.parseQuery(input);

    assertNotNull(result);
    assertEquals(4, result.size());

    assertTrue(result.contains("{A}"));
    assertTrue(result.contains("{B}"));
    assertTrue(result.contains("{C}"));
    assertTrue(result.contains("{D}"));
  }
@Test
  public void testParseQueryWithEmptyStringInsideGroup() throws Exception {
    String input = "({A}|)";
    List<String> result = SubQueryParser.parseQuery(input);

    assertNotNull(result);
    assertEquals(2, result.size());

    assertTrue(result.contains("{A}"));
    assertTrue(result.contains(""));
  }
@Test
  public void testParseQueryWithEscapePipeInsideToken() throws Exception {
    String input = "{A\\|B}|{C}";
    List<String> result = SubQueryParser.parseQuery(input);

    assertNotNull(result);
    assertEquals(2, result.size());

    assertTrue(result.contains("{A\\|B}"));
    assertTrue(result.contains("{C}"));
  }
@Test
  public void testParseQueryEscapedParenthesisAreIgnored() throws Exception {
    String input = "\\({A}|{B}\\)";
    List<String> result = SubQueryParser.parseQuery(input);

    assertNotNull(result);
    assertEquals(1, result.size());

    String query = result.get(0);
    assertThat(query, containsString("\\({A}"));
    assertThat(query, containsString("{B}\\)"));
  }
@Test
  public void testFindOrTokensEmptyInputReturnsSingleEmptyToken() {
    String input = "";
    List<String> result = SubQueryParser.findOrTokens(input);

    assertNotNull(result);
    assertEquals(1, result.size());

    assertEquals("", result.get(0));
  }
@Test
  public void testFindOrTokensSingleTokenNoOr() {
    String input = "{X}{Y}";
    List<String> result = SubQueryParser.findOrTokens(input);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("{X}{Y}", result.get(0));
  }
@Test
  public void testFindOrTokensOnlyOrCharacters() {
    String input = "|";
    List<String> result = SubQueryParser.findOrTokens(input);

    assertNotNull(result);
    assertEquals(2, result.size());

    assertEquals("", result.get(0));
    assertEquals("", result.get(1));
  }
@Test
  public void testScanQueryWithMultipleEscapedOrsAndBrackets() {
    String input = "\\|\\(\\)\\|";
    boolean result = SubQueryParser.scanQueryForOrOrBracket(input);

    assertFalse(result);
  }
@Test
  public void testParseQueryWildcardWithLongNumber() throws Exception {
    String input = "(X)+10";
    List<String> result = SubQueryParser.parseQuery(input);

    assertNotNull(result);
    assertEquals(1, result.size());

    String query = result.get(0);
    assertThat(query, containsString("(X)"));
    assertThat(query, containsString("|"));
  }
@Test
  public void testParseQueryWithWhitespaceBetweenTokensAndOperators() throws Exception {
    String input = "({A} | {B})";
    List<String> result = SubQueryParser.parseQuery(input);

    assertNotNull(result);
    assertEquals(2, result.size());

    assertTrue(result.contains("{A}"));
    assertTrue(result.contains("{B}"));
  }
@Test
  public void testParseQueryHandlesNestedOptionalAndRepeat() throws Exception {
    String input = "((A)?2)+2";
    List<String> result = SubQueryParser.parseQuery(input);

    assertNotNull(result);
    assertEquals(1, result.size());

    String query = result.get(0);
    assertThat(query, containsString("{__o__}"));
    assertThat(query, containsString("(A)"));
  }
@Test
  public void testParseQueryMultipleAdjacentOrs() throws Exception {
    String input = "({A}|{B}|{C})";
    List<String> result = SubQueryParser.parseQuery(input);

    assertNotNull(result);
    assertEquals(3, result.size());
    assertTrue(result.contains("{A}"));
    assertTrue(result.contains("{B}"));
    assertTrue(result.contains("{C}"));
  }
@Test
  public void testParseQueryOrInsideWildcard() throws Exception {
    String input = "(({A}|{B}))+2";
    List<String> result = SubQueryParser.parseQuery(input);

    assertNotNull(result);
    assertEquals(2, result.size());
    String q0 = result.get(0);
    String q1 = result.get(1);
    assertTrue(q0.contains("{A}") || q1.contains("{A}"));
    assertTrue(q0.contains("{B}") || q1.contains("{B}"));
  }
@Test
  public void testParseQueryWithEscapedEscapeChar() throws Exception {
    String input = "\\\\(A|B)";
    List<String> result = SubQueryParser.parseQuery(input);

    assertNotNull(result);
    assertEquals(2, result.size());
    String q0 = result.get(0);
    String q1 = result.get(1);
    assertTrue(q0.contains("\\\\"));
    assertTrue(q1.contains("\\\\"));
  }
@Test
  public void testParseQueryUnclosedOuterBracket() {
    String input = "({A}|{B}";
    try {
      SubQueryParser.parseQuery(input);
      fail("Expected Exception due to unmatched brackets");
    } catch (Exception e) {
      String msg = e.getMessage();
      assertTrue(msg.toLowerCase().contains("unbalanced"));
    }
  }
@Test
  public void testParseQueryClosingBracketFirst() {
    String input = "){A}";
    try {
      SubQueryParser.parseQuery(input);
      fail("Expected Exception due to unmatched closing bracket");
    } catch (Exception e) {
      String msg = e.getMessage();
      assertTrue(msg.toLowerCase().contains("unbalanced"));
    }
  }
@Test
  public void testFindOrTokensWithUnbalancedNestedBrackets() {
    String query = "({A}|({B}|{C})";
    List<String> result = SubQueryParser.findOrTokens(query);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(query, result.get(0));
  }
@Test
  public void testWildcardPlusWithWhitespace() throws Exception {
    String input = "(A) +3";
    List<String> result = SubQueryParser.parseQuery(input);

    assertNotNull(result);
    assertEquals(1, result.size());
    String query = result.get(0);
    assertTrue(query.contains("(A)"));
    assertTrue(query.contains("|"));
  }
@Test
  public void testParseQueryWithDeeplyNestedEmptyBrackets() throws Exception {
    String input = "(((())))";
    List<String> result = SubQueryParser.parseQuery(input);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("", result.get(0).trim());
  }
@Test
  public void testParseQueryWithMultipleConsecutiveWildcards() throws Exception {
    String input = "(X)+2*3";
    List<String> result = SubQueryParser.parseQuery(input);

    assertNotNull(result);
    assertFalse(result.isEmpty()); 
  }
@Test
  public void testParseQueryWithTrailingPipe() throws Exception {
    String input = "{A}|";
    List<String> result = SubQueryParser.parseQuery(input);

    assertNotNull(result);
    assertEquals(2, result.size());
    assertTrue(result.contains("{A}"));
    assertTrue(result.contains(""));
  }
@Test
  public void testParseQueryPipeInsideNestedOr() throws Exception {
    String input = "({A}|({B}|{C}))";
    List<String> result = SubQueryParser.parseQuery(input);

    assertNotNull(result);
    assertEquals(3, result.size());
    assertTrue(result.contains("{A}"));
    assertTrue(result.contains("{B}"));
    assertTrue(result.contains("{C}"));
  }
@Test
  public void testParseQueryOnlyPipeIsTwoEmpties() throws Exception {
    String input = "|";
    List<String> result = SubQueryParser.parseQuery(input);

    assertNotNull(result);
    assertEquals(2, result.size());
    assertTrue(result.contains(""));
  }
@Test
  public void testParseQueryNumericWildcardWithInvalidNumber() throws Exception {
    String input = "(X)+XY";
    List<String> result = SubQueryParser.parseQuery(input);

    assertNotNull(result);
    assertEquals(1, result.size()); 
    assertThat(result.get(0), containsString("(X)"));
  }
@Test
  public void testParseQueryWithWhitespaceBetweenWildcardsAndNumbers() throws Exception {
    String input = "(X)+ 2";
    List<String> result = SubQueryParser.parseQuery(input);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("(X)"));
  }
@Test
  public void testParseQueryValidEmptyGroupInMiddle() throws Exception {
    String input = "{A}()|{B}";
    List<String> result = SubQueryParser.parseQuery(input);

    assertNotNull(result);
    assertEquals(2, result.size());
    assertTrue(result.contains("{A}"));
    assertTrue(result.contains("{B}"));
  }
@Test
  public void testParseQueryEmptyGroup() throws Exception {
    String input = "()";
    List<String> result = SubQueryParser.parseQuery(input);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("", result.get(0).trim());
  }
@Test
  public void testParseQueryOnlyBackslash() throws Exception {
    String input = "\\";
    List<String> result = SubQueryParser.parseQuery(input);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("\\", result.get(0));
  }
@Test
  public void testParseQueryEscapedOpenBracket() throws Exception {
    String input = "\\({A})";
    List<String> result = SubQueryParser.parseQuery(input);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("\\({A}"));
  }
@Test
  public void testParseQueryEscapedCloseBracket() throws Exception {
    String input = "({A}\\)";
    List<String> result = SubQueryParser.parseQuery(input);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertThat(result.get(0), containsString("\\)"));
  }
@Test
  public void testParseQueryWithOnlyWhitespaceBetweenOrs() throws Exception {
    String input = "(  |   )";
    List<String> result = SubQueryParser.parseQuery(input);

    assertNotNull(result);
    assertEquals(2, result.size());
    assertTrue(result.get(0).trim().isEmpty());
    assertTrue(result.get(1).trim().isEmpty());
  }
@Test
  public void testFindOrTokensWithDeeplyNestedOr() {
    String input = "{A}|(({B}|{C})|{D})";
    List<String> result = SubQueryParser.findOrTokens(input);

    assertNotNull(result);
    assertEquals(2, result.size());

    assertTrue(result.get(0).contains("{A}"));
    assertTrue(result.get(1).contains("(({B}|{C})|{D})"));
  }
@Test
  public void testScanQueryForAllEscapedPipesAndBrackets() {
    String input = "\\|\\(\\)\\|";
    boolean result = SubQueryParser.scanQueryForOrOrBracket(input);

    assertFalse(result);
  }
@Test
  public void testParseQueryRepeatedEmptyString() throws Exception {
    String input = "(()+3)";
    List<String> result = SubQueryParser.parseQuery(input);

    assertNotNull(result);
    assertEquals(1, result.size());

    String q = result.get(0);
    assertTrue(q.contains("{__o__}") || q.contains("()"));
  }
@Test
  public void testParseQueryOrOperatorWithoutBrackets() throws Exception {
    String input = "{X}|{Y}|{Z}";
    List<String> result = SubQueryParser.parseQuery(input);

    assertNotNull(result);
    assertEquals(3, result.size());
    assertTrue(result.contains("{X}"));
    assertTrue(result.contains("{Y}"));
    assertTrue(result.contains("{Z}"));
  }
@Test
  public void testParseQueryUnescapedWildcardWithoutGroup() throws Exception {
    String input = "A+2";
    List<String> result = SubQueryParser.parseQuery(input);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("A+2"));
  }
@Test
  public void testParseQueryUnescapedOrWithoutBrackets() throws Exception {
    String input = "X|Y";
    List<String> result = SubQueryParser.parseQuery(input);

    assertNotNull(result);
    assertEquals(2, result.size());
    assertTrue(result.contains("X"));
    assertTrue(result.contains("Y"));
  }
@Test
  public void testParseQueryWildcardWithMultipleDigits() throws Exception {
    String input = "(X)+123";
    List<String> result = SubQueryParser.parseQuery(input);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("(X)"));
  }
@Test
  public void testParseQueryLongRepeatsWithZeroRepeatWildcard() throws Exception {
    String input = "(Y)*0";
    List<String> result = SubQueryParser.parseQuery(input);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("{__o__}"));
  }
@Test
  public void testParseQueryLongRepeatsWithQuestionMarkZeroRepeat() throws Exception {
    String input = "(Z)?0";
    List<String> result = SubQueryParser.parseQuery(input);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("{__o__}"));
  }
@Test(expected = NumberFormatException.class)
  public void testParseQueryInvalidWildcardRepeatCharacters() throws Exception {
    String input = "(A)+xyz";
    SubQueryParser.parseQuery(input);
  }
@Test
  public void testFindOrTokensWithNoRealOrAtTopLevel() {
    String input = "({A}{B})";
    List<String> result = SubQueryParser.findOrTokens(input);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("({A}{B})", result.get(0));
  }
@Test
  public void testParseQueryWithComplexNestingAndWildcards() throws Exception {
    String input = "(({A}|({B}|{C}))+2)({D}|{E})";
    List<String> result = SubQueryParser.parseQuery(input);

    assertNotNull(result);
    assertEquals(6, result.size());

    boolean containsA = result.get(0).contains("{A}") || result.get(1).contains("{A}") || result.get(2).contains("{A}");
    boolean containsE = result.get(3).contains("{E}") || result.get(4).contains("{E}") || result.get(5).contains("{E}");

    assertTrue(containsA);
    assertTrue(containsE);
  }
@Test
  public void testParseQueryWithEscapedBackslashAtEnd() throws Exception {
    String input = "abc\\\\";
    List<String> result = SubQueryParser.parseQuery(input);
    assertNotNull(result);
    assertEquals(1, result.size());
    assertTrue(result.get(0).endsWith("\\\\"));
  }
@Test
  public void testParseQueryWithNestedBracketsAdjacentToOr() throws Exception {
    String input = "({A}({B}|{C}))";
    List<String> result = SubQueryParser.parseQuery(input);
    assertNotNull(result);
    assertEquals(2, result.size());
    assertTrue(result.get(0).contains("{B}") || result.get(1).contains("{B}"));
    assertTrue(result.get(0).contains("{C}") || result.get(1).contains("{C}"));
    assertTrue(result.get(0).contains("{A}") || result.get(1).contains("{A}"));
  }
@Test
  public void testParseQueryWithInterleavedTokensAndWildcards() throws Exception {
    String input = "(A)+2(B)*2(C)?2";
    List<String> result = SubQueryParser.parseQuery(input);
    assertNotNull(result);
    assertEquals(1, result.size());
    String query = result.get(0);
    assertTrue(query.contains("(A)"));
    assertTrue(query.contains("(B)"));
    assertTrue(query.contains("(C)"));
    assertTrue(query.contains("{__o__}"));
  }
@Test
  public void testParseQueryWithOnlyWildcardSymbol() throws Exception {
    String input = "*";
    List<String> result = SubQueryParser.parseQuery(input);
    assertNotNull(result);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("*"));
  }
@Test
  public void testParseQueryWithWildcardSymbolInMiddleOfToken() throws Exception {
    String input = "{A*B}";
    List<String> result = SubQueryParser.parseQuery(input);
    assertNotNull(result);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("{A*B}"));
  }
@Test
  public void testParseQueryOrWithEmptyLeftSide() throws Exception {
    String input = "|{X}";
    List<String> result = SubQueryParser.parseQuery(input);
    assertNotNull(result);
    assertEquals(2, result.size());
    assertTrue(result.contains(""));
    assertTrue(result.contains("{X}"));
  }
@Test
  public void testParseQueryOrWithEmptyRightSide() throws Exception {
    String input = "{X}|";
    List<String> result = SubQueryParser.parseQuery(input);
    assertNotNull(result);
    assertEquals(2, result.size());
    assertTrue(result.contains("{X}"));
    assertTrue(result.contains(""));
  }
@Test
  public void testParseQueryOnlyOneWildcardPlusNoParentheses() throws Exception {
    String input = "a+2";
    List<String> result = SubQueryParser.parseQuery(input);
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("a+2", result.get(0));
  }
@Test
  public void testParseQueryOnlyOneWildcardStarDigitNoParentheses() throws Exception {
    String input = "a*3";
    List<String> result = SubQueryParser.parseQuery(input);
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("a*3", result.get(0));
  }
@Test
  public void testScanQueryForDoubleEscapedBracketAndOr() {
    String input = "\\\\(A\\\\|B\\\\)";
    boolean result = SubQueryParser.scanQueryForOrOrBracket(input);
    assertFalse(result); 
  }
@Test
  public void testFindOrTokensWithEscapedOrCharacter() {
    String input = "{A}\\|{B}|{C}";
    List<String> tokens = SubQueryParser.findOrTokens(input);
    assertNotNull(tokens);
    assertEquals(2, tokens.size());
    assertTrue(tokens.get(0).contains("\\|"));
    assertEquals("{C}", tokens.get(1));
  }
@Test
  public void testFindOrTokensWithMultipleNestedOrs() {
    String input = "({A}|({B}|{C}))|{D}";
    List<String> tokens = SubQueryParser.findOrTokens(input);
    assertNotNull(tokens);
    assertEquals(2, tokens.size());
    assertTrue(tokens.get(0).contains("({A}|({B}|{C}))"));
    assertEquals("{D}", tokens.get(1));
  }
@Test
  public void testParseQueryWithLongEscapedSequence() throws Exception {
    String input = "\\\\\\({A}\\|{B}\\)";
    List<String> result = SubQueryParser.parseQuery(input);
    assertNotNull(result);
    assertEquals(1, result.size());
    String query = result.get(0);
    assertTrue(query.contains("\\\\\\(") || query.contains("\\|"));
  }
@Test
  public void testParseQueryRedundantEscapesIgnored() throws Exception {
    String input = "\\\\{A}|\\\\{B}";
    List<String> result = SubQueryParser.parseQuery(input);
    assertNotNull(result);
    assertEquals(2, result.size());
    assertTrue(result.get(0).contains("\\\\{A}") || result.get(1).contains("\\\\{A}"));
    assertTrue(result.get(0).contains("\\\\{B}") || result.get(1).contains("\\\\{B}"));
  }
@Test
  public void testParseQueryWithPipeSymbolAtBeginningAndEnd() throws Exception {
    String input = "|{M}|";
    List<String> result = SubQueryParser.parseQuery(input);
    assertNotNull(result);
    assertEquals(3, result.size());
    assertTrue(result.contains(""));
    assertTrue(result.contains("{M}"));
  }
@Test
  public void testParseQueryWithWildcardPlusFollowedByWhitespaceAndNumber() throws Exception {
    String input = "(X)+ 5";
    List<String> result = SubQueryParser.parseQuery(input);
    assertNotNull(result);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("(X)"));
  }
@Test
  public void testParseQueryWithMissingClosingBracketInNestedGroup() {
    String input = "({A}({B}|{C})";
    try {
      SubQueryParser.parseQuery(input);
      fail("Expected Exception");
    } catch (Exception e) {
      assertTrue(e.getMessage().toLowerCase().contains("unbalanced"));
    }
  }
@Test
  public void testParseQueryWithExtraClosingBracketInNestedGroup() {
    String input = "({A}({B}|{C})))";
    try {
      SubQueryParser.parseQuery(input);
      fail("Expected Exception");
    } catch (Exception e) {
      assertTrue(e.getMessage().toLowerCase().contains("unbalanced"));
    }
  }
@Test
  public void testParseQueryEscapedSingleBackslashOnly() throws Exception {
    String input = "\\";
    List<String> result = SubQueryParser.parseQuery(input);
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("\\", result.get(0));
  }
@Test
  public void testParseQueryOrOperatorsOnlyWhitespaceInGroups() throws Exception {
    String input = "(     |   )";
    List<String> result = SubQueryParser.parseQuery(input);
    assertNotNull(result);
    assertEquals(2, result.size());
    assertTrue(result.get(0).trim().isEmpty());
    assertTrue(result.get(1).trim().isEmpty());
  }
@Test
  public void testParseQueryWildcardPlusNoDigits() throws Exception {
    String input = "(T)+";
    List<String> result = SubQueryParser.parseQuery(input);
    assertNotNull(result);
    assertEquals(1, result.size());
    String q = result.get(0);
    assertTrue(q.contains("(T)"));
    assertFalse(q.contains("{__o__}")); 
  }
@Test
  public void testParseQueryWildcardStarNoDigits() throws Exception {
    String input = "(T)*";
    List<String> result = SubQueryParser.parseQuery(input);
    assertNotNull(result);
    assertEquals(1, result.size());
    String q = result.get(0);
    assertTrue(q.contains("(T)"));
    assertTrue(q.contains("{__o__}")); 
  }
@Test
  public void testParseQueryWildcardQuestionNoDigits() throws Exception {
    String input = "(W)?";
    List<String> result = SubQueryParser.parseQuery(input);
    assertNotNull(result);
    assertEquals(1, result.size());
    String q = result.get(0);
    assertTrue(q.contains("(W)"));
    assertTrue(q.contains("{__o__}"));
  }
@Test
  public void testParseQueryDoubleEscapedPipeAndParens() throws Exception {
    String input = "word\\\\|more\\\\(stuff\\\\)";
    List<String> result = SubQueryParser.parseQuery(input);
    assertNotNull(result);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("\\\\|"));
    assertTrue(result.get(0).contains("\\\\("));
    assertTrue(result.get(0).contains("\\\\)"));
  }
@Test
  public void testParseQueryOrWithBracketsAndWhitespaceBetween() throws Exception {
    String input = "({A}|    {B})";
    List<String> result = SubQueryParser.parseQuery(input);
    assertNotNull(result);
    assertEquals(2, result.size());
    assertTrue(result.get(0).contains("{A}") || result.get(1).contains("{A}"));
    assertTrue(result.get(0).contains("{B}") || result.get(1).contains("{B}"));
  }
@Test
  public void testParseQueryEmptyInputOnlySpaces() throws Exception {
    String input = "     ";
    List<String> result = SubQueryParser.parseQuery(input);
    assertNotNull(result);
    assertEquals(0, result.size());
  }
@Test
  public void testParseQueryWithEmptyBlockRepeats() throws Exception {
    String input = "()*3";
    List<String> result = SubQueryParser.parseQuery(input);
    assertNotNull(result);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("{__o__}") || result.get(0).contains("()"));
  }
@Test
  public void testParseQueryOnlyUnescapedPipeAsLiteral() throws Exception {
    String input = "|";
    List<String> result = SubQueryParser.parseQuery(input);
    assertNotNull(result);
    assertEquals(2, result.size());
    assertTrue(result.contains(""));
  }
@Test
  public void testParseQueryNestedBracketsWithoutOr() throws Exception {
    String input = "({A}({B}{C}))";
    List<String> result = SubQueryParser.parseQuery(input);
    assertNotNull(result);
    assertEquals(1, result.size());
    String q = result.get(0);
    assertTrue(q.contains("{A}"));
    assertTrue(q.contains("{B}"));
    assertTrue(q.contains("{C}"));
  }
@Test
  public void testScanQueryForBracketOnlyEscaped() {
    String input = "xyz \\(abc\\)";
    boolean hasOperator = SubQueryParser.scanQueryForOrOrBracket(input);
    assertFalse(hasOperator);
  }
@Test
  public void testScanQueryForOrAsFirstCharacter() {
    String input = "|abc";
    boolean result = SubQueryParser.scanQueryForOrOrBracket(input);
    assertTrue(result);
  }
@Test
  public void testParseQueryWildcardsWithSpacesBetweenCharacters() throws Exception {
    String input = "( A )+2";
    List<String> result = SubQueryParser.parseQuery(input);
    assertNotNull(result);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("( A )"));
  }
@Test
  public void testParseQueryWithOnlyOpeningBracket() {
    String input = "(";
    try {
      SubQueryParser.parseQuery(input);
      fail("Expected Exception due to unmatched opening (");
    } catch (Exception e) {
      assertTrue(e.getMessage().contains("closing bracket"));
    }
  }
@Test
  public void testParseQueryWithOnlyClosingBracket() {
    String input = ")";
    try {
      SubQueryParser.parseQuery(input);
      fail("Expected Exception due to unmatched closing )");
    } catch (Exception e) {
      assertTrue(e.getMessage().contains("opening bracket"));
    }
  }
@Test
  public void testParseQueryWithWildcardNumberFollowedByLetters() throws Exception {
    String input = "(T)+2abc";
    List<String> result = SubQueryParser.parseQuery(input);
    assertNotNull(result);
    assertEquals(1, result.size());
    
    assertTrue(result.get(0).contains("(T)"));
  }
@Test
  public void testFindOrTokensWithMultipleTopLevelOrs() {
    String input = "{A}|{B}|{C}";
    List<String> tokens = SubQueryParser.findOrTokens(input);
    assertNotNull(tokens);
    assertEquals(3, tokens.size());
    assertEquals("{A}", tokens.get(0));
    assertEquals("{B}", tokens.get(1));
    assertEquals("{C}", tokens.get(2));
  }
@Test
  public void testFindOrTokensWithEscapedOr() {
    String input = "{A}\\|{B}|{C}";
    List<String> tokens = SubQueryParser.findOrTokens(input);
    assertNotNull(tokens);
    assertEquals(2, tokens.size());
    assertEquals("{A}\\|{B}", tokens.get(0));
    assertEquals("{C}", tokens.get(1));
  }
@Test
  public void testFindOrTokensWithBalancedNestedGroup() {
    String input = "({A}|({B}|{C}))";
    List<String> tokens = SubQueryParser.findOrTokens(input);
    assertNotNull(tokens);
    assertEquals(1, tokens.size());
    assertEquals("({A}|({B}|{C}))", tokens.get(0));
  }
@Test
  public void testParseQueryWhitespaceAndOrOnly() throws Exception {
    String input = "   |  ";
    List<String> result = SubQueryParser.parseQuery(input);
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals("", result.get(0).trim());
    assertEquals("", result.get(1).trim());
  }
@Test
  public void testParseQueryWildcardWithSpaceBeforeDigit() throws Exception {
    String input = "(Z)+ 2";
    List<String> result = SubQueryParser.parseQuery(input);
    assertNotNull(result);
    assertEquals(1, result.size());
    String q = result.get(0);
    assertTrue(q.contains("(Z)"));
  }
@Test
  public void testParseQueryWithEscapedPipeInMiddleOfToken() throws Exception {
    String input = "{A\\|B}";
    List<String> result = SubQueryParser.parseQuery(input);
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("{A\\|B}", result.get(0));
  }
@Test
  public void testParseQueryMultipleLayersNestedWithWildcard() throws Exception {
    String input = "(((X)+2))+3";
    List<String> result = SubQueryParser.parseQuery(input);
    assertNotNull(result);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("((X)"));
    assertTrue(result.get(0).contains("|"));
  }
@Test
  public void testParseQueryOrWithUntrimmedTokens() throws Exception {
    String input = "(  {A}   |   {B}   )";
    List<String> result = SubQueryParser.parseQuery(input);
    assertNotNull(result);
    assertEquals(2, result.size());
    assertTrue(result.get(0).contains("{A}"));
    assertTrue(result.get(1).contains("{B}"));
  }
@Test
  public void testParseQueryGroupFollowedByUnknownWildcardSymbol() throws Exception {
    String input = "(X)#3";  
    List<String> result = SubQueryParser.parseQuery(input);
    assertNotNull(result);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("(X)#3"));
  }
@Test
  public void testParseQueryWithWildcardWithLeadingZeros() throws Exception {
    String input = "(Y)+0003";
    List<String> result = SubQueryParser.parseQuery(input);
    assertNotNull(result);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("(Y)"));
    assertTrue(result.get(0).contains("|"));
  }
@Test
  public void testParseQueryWithInvalidWildcardSuffix_NonDigit() throws Exception {
    String input = "(ZZZ)*x";
    List<String> result = SubQueryParser.parseQuery(input);
    assertNotNull(result);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("(ZZZ)*x")); 
  }
@Test
  public void testParseQueryTripleNestedBracketsOnly() throws Exception {
    String input = "((()))";
    List<String> result = SubQueryParser.parseQuery(input);
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("", result.get(0).trim());
  }
@Test
  public void testFindOrTokensOrAtStartAndEnd() {
    String input = "|{A}|";
    List<String> tokens = SubQueryParser.findOrTokens(input);
    assertNotNull(tokens);
    assertEquals(3, tokens.size());
    assertEquals("", tokens.get(0));
    assertEquals("{A}", tokens.get(1));
    assertEquals("", tokens.get(2));
  }
@Test
  public void testFindOrTokensPipeInsideGroupIsNotSplit() {
    String input = "({A}|({B}|{C})|{D})";
    List<String> tokens = SubQueryParser.findOrTokens(input.substring(1, input.length() - 1));
    assertNotNull(tokens);
    assertEquals(3, tokens.size());
    assertTrue(tokens.get(0).contains("{A}"));
    assertTrue(tokens.get(1).contains("({B}|{C})"));
    assertTrue(tokens.get(2).contains("{D}"));
  }
@Test
  public void testScanQueryForOrOnlyInsideBrackets() {
    String input = "({A}|{B})";
    boolean result = SubQueryParser.scanQueryForOrOrBracket(input);
    assertTrue(result);
  }
@Test
  public void testScanQueryForOrWhenEscaped() {
    String input = "foo\\|bar";
    boolean result = SubQueryParser.scanQueryForOrOrBracket(input);
    assertFalse(result);
  }
@Test
  public void testScanQueryForOpenParenOnlyEscaped() {
    String input = "a \\(";
    boolean result = SubQueryParser.scanQueryForOrOrBracket(input);
    assertFalse(result);
  }
@Test
  public void testParseQueryWithSpaceInsideWildcardSuffixFailsGracefully() throws Exception {
    String input = "(X)+2 3";
    List<String> result = SubQueryParser.parseQuery(input); 
    assertNotNull(result);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("(X)"));
  }
@Test
  public void testParseQueryEmptyGroupThenToken() throws Exception {
    String input = "()X";
    List<String> result = SubQueryParser.parseQuery(input);
    assertNotNull(result);
    assertEquals(1, result.size());
    assertTrue(result.get(0).endsWith("X"));
  }
@Test
  public void testParseQueryRepetitiveNestedEmptyGroups() throws Exception {
    String input = "((())+2)";
    List<String> result = SubQueryParser.parseQuery(input);
    assertNotNull(result);
    assertEquals(1, result.size());
  } 
}