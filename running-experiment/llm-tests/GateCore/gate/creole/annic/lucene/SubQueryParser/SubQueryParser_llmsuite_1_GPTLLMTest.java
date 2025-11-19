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

public class SubQueryParser_llmsuite_1_GPTLLMTest { 

 @Test
  public void testSimpleQuery() throws Exception {
    String input = "{A}";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    assertEquals("({A})", result.get(0));
  }
@Test
  public void testSimpleOrQuery() throws Exception {
    String input = "{A}|{B}";
    List<String> result = SubQueryParser.parseQuery(input);

    boolean containsA = false;
    boolean containsB = false;

    if (result.size() == 2) {
      String r0 = result.get(0);
      String r1 = result.get(1);

      containsA = r0.trim().equals("{A}") || r1.trim().equals("{A}");
      containsB = r0.trim().equals("{B}") || r1.trim().equals("{B}");
    }

    assertTrue(containsA);
    assertTrue(containsB);
  }
@Test
  public void testOrWithinBrackets() throws Exception {
    String input = "({A}|{B}){C}";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(2, result.size());

    boolean hasAC = false;
    boolean hasBC = false;

    String r0 = result.get(0).replaceAll("[()\\s]", "");
    String r1 = result.get(1).replaceAll("[()\\s]", "");

    hasAC = r0.equals("{A}{C}") || r1.equals("{A}{C}");
    hasBC = r0.equals("{B}{C}") || r1.equals("{B}{C}");

    assertTrue(hasAC);
    assertTrue(hasBC);
  }
@Test
  public void testWildcardPlus3() throws Exception {
    String input = "({A})+3";
    List<String> result = SubQueryParser.parseQuery(input);

    assertEquals(1, result.size());
    String actual = result.get(0);

    boolean containsTripleA = actual.contains("{A}{A}{A}") || actual.contains("({A})({A})({A})");

    assertTrue(containsTripleA);
  }
@Test
  public void testWildcardStar2() throws Exception {
    String input = "({A})*2";
    List<String> result = SubQueryParser.parseQuery(input);

    assertEquals(1, result.size());
    String actual = result.get(0);

    boolean containsOptional = actual.contains("{__o__}");
    assertTrue(containsOptional);
  }
@Test
  public void testWildcardQuestion2() throws Exception {
    String input = "({A})?2";
    List<String> result = SubQueryParser.parseQuery(input);

    assertEquals(1, result.size());
    String actual = result.get(0);

    boolean containsOptional = actual.contains("{__o__}");
    assertTrue(containsOptional);
  }
@Test
  public void testEscapedCharacters() throws Exception {
    String input = "\\({A}\\|{B}\\)";
    List<String> result = SubQueryParser.parseQuery(input);

    assertEquals(1, result.size());
    String actual = result.get(0);

    assertTrue(actual.contains("\\("));
    assertTrue(actual.contains("\\|"));
    assertTrue(actual.contains("\\)"));
  }
@Test
  public void testNestedOrClause() throws Exception {
    String input = "({A}|({B}|{C})){D}";
    List<String> result = SubQueryParser.parseQuery(input);

    assertEquals(3, result.size());

    boolean containsAD = result.get(0).contains("{A}{D}") || result.get(1).contains("{A}{D}") || result.get(2).contains("{A}{D}");
    boolean containsBD = result.get(0).contains("{B}{D}") || result.get(1).contains("{B}{D}") || result.get(2).contains("{B}{D}");
    boolean containsCD = result.get(0).contains("{C}{D}") || result.get(1).contains("{C}{D}") || result.get(2).contains("{C}{D}");

    assertTrue(containsAD || containsBD || containsCD);
  }
@Test
  public void testEmptyQuery() throws Exception {
    String input = "";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(0, result.size());
  }
@Test
  public void testWhitespaceQuery() throws Exception {
    String input = "    ";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(0, result.size());
  }
@Test(expected = SearchException.class)
  public void testUnbalancedBracketsException1() throws Exception {
    String input = "({A}";
    SubQueryParser.parseQuery(input);
  }
@Test(expected = SearchException.class)
  public void testUnbalancedBracketsException2() throws Exception {
    String input = "{A})";
    SubQueryParser.parseQuery(input);
  }
@Test
  public void testScanQueryPositive() {
    boolean result1 = SubQueryParser.scanQueryForOrOrBracket("({A}|{B})");
    boolean result2 = SubQueryParser.scanQueryForOrOrBracket("{A}|{C}");
    boolean result3 = SubQueryParser.scanQueryForOrOrBracket("(A)");
    assertTrue(result1);
    assertTrue(result2);
    assertTrue(result3);
  }
@Test
  public void testScanQueryNegativeWithEscapes() {
    boolean result1 = SubQueryParser.scanQueryForOrOrBracket("\\(A\\|B\\)");
    boolean result2 = SubQueryParser.scanQueryForOrOrBracket("A B C");
    assertFalse(result1);
    assertFalse(result2);
  }
@Test
  public void testFindOrTokensSimple() {
    List<String> tokens = SubQueryParser.findOrTokens("{A}|{B}|{C}");

    assertEquals(3, tokens.size());
    assertEquals("{A}", tokens.get(0));
    assertEquals("{B}", tokens.get(1));
    assertEquals("{C}", tokens.get(2));
  }
@Test
  public void testFindOrTokensWithNesting() {
    List<String> tokens = SubQueryParser.findOrTokens("({A}|{B})|{C}");

    assertEquals(2, tokens.size());
    assertEquals("({A}|{B})", tokens.get(0));
    assertEquals("{C}", tokens.get(1));
  }
@Test
  public void testWildcardWithMissingNumber() throws Exception {
    String input = "({A})+";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    String actual = result.get(0);
    assertTrue(actual.contains("{A}"));
  }
@Test
  public void testLongOrChain() throws Exception {
    String input = "{A}|{B}|{C}|{D}";
    List<String> result = SubQueryParser.parseQuery(input);

    assertEquals(4, result.size());

    String a = result.get(0).trim();
    String b = result.get(1).trim();
    String c = result.get(2).trim();
    String d = result.get(3).trim();

    boolean foundA = a.equals("{A}") || b.equals("{A}") || c.equals("{A}") || d.equals("{A}");
    boolean foundB = a.equals("{B}") || b.equals("{B}") || c.equals("{B}") || d.equals("{B}");
    boolean foundC = a.equals("{C}") || b.equals("{C}") || c.equals("{C}") || d.equals("{C}");
    boolean foundD = a.equals("{D}") || b.equals("{D}") || c.equals("{D}") || d.equals("{D}");

    assertTrue(foundA);
    assertTrue(foundB);
    assertTrue(foundC);
    assertTrue(foundD);
  }
@Test(expected = SearchException.class)
  public void testOnlyWildcardThrows() throws Exception {
    String input = "*";
    SubQueryParser.parseQuery(input);
  }
@Test
  public void testEscapeBackslashOnly() throws Exception {
    String input = "\\";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    assertEquals("(\\)", result.get(0));
  }
@Test
  public void testEscapedOpenBracketInsideText() throws Exception {
    String input = "A\\(B";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    String actual = result.get(0);
    assertTrue(actual.contains("A\\(B") || actual.contains("\\(B"));
  }
@Test
  public void testEscapedClosingBracketInsideText() throws Exception {
    String input = "X\\)";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("\\)"));
  }
@Test
  public void testUnescapedPipeOnly() throws Exception {
    String input = "|";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(2, result.size());
    assertTrue(result.contains(""));
  }
@Test
  public void testEscapedPipeOnly() throws Exception {
    String input = "\\|";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("\\|"));
  }
@Test
  public void testOrWithEmptyAlternativeLeft() throws Exception {
    String input = "|{X}";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(2, result.size());
    String r0 = result.get(0).trim();
    String r1 = result.get(1).trim();
    assertTrue(r0.isEmpty() || r1.isEmpty());
    assertTrue(r0.equals("{X}") || r1.equals("{X}"));
  }
@Test
  public void testOrWithEmptyAlternativeRight() throws Exception {
    String input = "{Y}|";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(2, result.size());
    String r0 = result.get(0).trim();
    String r1 = result.get(1).trim();
    assertTrue(r0.isEmpty() || r1.isEmpty());
    assertTrue(r0.equals("{Y}") || r1.equals("{Y}"));
  }
@Test
  public void testWildcardWithoutRepeats() throws Exception {
    String input = "({X})+";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("{X}"));
  }
@Test
  public void testWildcardWithZeroRepeat() throws Exception {
    String input = "({A})+0";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    String output = result.get(0);
    assertTrue(output.contains("({A})"));
  }
@Test(expected = SearchException.class)
  public void testOnlyOpenBracket() throws Exception {
    String input = "(";
    SubQueryParser.parseQuery(input);
  }
@Test(expected = SearchException.class)
  public void testOnlyClosingBracket() throws Exception {
    String input = ")";
    SubQueryParser.parseQuery(input);
  }
@Test
  public void testDeeplyNestedWildcardsAndOrs() throws Exception {
    String input = "(({A}|{B})*2)|(({C}?3)|{D})";
    List<String> result = SubQueryParser.parseQuery(input);
    assertTrue(result.size() >= 2);
    boolean hasAorB = false;
    boolean hasD = false;
    boolean hasC = false;

    String r0 = result.get(0);
    String r1 = result.size() > 1 ? result.get(1) : "";

    hasAorB = r0.contains("{A}") || r0.contains("{B}") || r1.contains("{A}") || r1.contains("{B}");
    hasD = r0.contains("{D}") || r1.contains("{D}");
    hasC = r0.contains("{C}") || r1.contains("{C}");

    assertTrue(hasAorB || hasC || hasD);
  }
@Test
  public void testWildcardThatEndsParsing() throws Exception {
    String input = "({Z})*2 EXTRA";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("EXTRA"));
  }
@Test
  public void testOrWithoutParentheses() throws Exception {
    String input = "{A}|{B}{C}";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(2, result.size());
    boolean foundAC = false;
    boolean foundBC = false;

    String r0 = result.get(0);
    String r1 = result.get(1);

    foundAC = r0.contains("{A}") || r1.contains("{A}");
    foundBC = r0.contains("{B}{C}") || r1.contains("{B}{C}");

    assertTrue(foundAC);
    assertTrue(foundBC);
  }
@Test
  public void testEmptyParentheses() throws Exception {
    String input = "({})";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("{}"));
  }
@Test
  public void testEscapedEverything() throws Exception {
    String input = "\\(\\{A\\}\\|\\{B\\}\\)";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    String value = result.get(0);
    assertTrue(value.contains("\\("));
    assertTrue(value.contains("\\|"));
    assertTrue(value.contains("\\)"));
    assertTrue(value.contains("\\{A\\}"));
  }
@Test
  public void testOnlyLiteralCharacters() throws Exception {
    String input = "abc";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("abc"));
  }
@Test
  public void testNestedRepetitions() throws Exception {
    String input = "(({A})+2)+2";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    String output = result.get(0);
    assertTrue(output.contains("{A}"));
    assertTrue(output.contains("|"));
  }
@Test
  public void testMultipleORWithWhitespace() throws Exception {
    String input = "{A} | {B} | {C} ";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(3, result.size());
    assertTrue(result.contains("{A}"));
    assertTrue(result.contains("{B}"));
    assertTrue(result.contains("{C}"));
  }
@Test
  public void testOrInsideRepetitionClause() throws Exception {
    String input = "(({A}|{B})+2)";
    List<String> result = SubQueryParser.parseQuery(input);
    assertTrue(result.size() == 2 || result.size() == 3);
    boolean hasA = result.get(0).contains("{A}") || result.get(1).contains("{A}");
    boolean hasB = result.get(0).contains("{B}") || result.get(1).contains("{B}");
    assertTrue(hasA || hasB);
  }
@Test
  public void testEmptyStringInsideBrackets() throws Exception {
    String input = "({})";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("{}"));
  }
@Test
  public void testOnlyPipeSymbol() throws Exception {
    String input = "|";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(2, result.size());
    assertTrue(result.contains(""));
  }
@Test
  public void testOrBetweenWildcards() throws Exception {
    String input = "({A})*2|({B})+2";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(2, result.size());
    boolean foundWildcardA = result.get(0).contains("{A}") || result.get(1).contains("{A}");
    boolean foundWildcardB = result.get(0).contains("{B}") || result.get(1).contains("{B}");
    assertTrue(foundWildcardA);
    assertTrue(foundWildcardB);
  }
@Test(expected = SearchException.class)
  public void testInvalidNestedBrackets() throws Exception {
    String input = "(({{A}})";
    SubQueryParser.parseQuery(input);
  }
@Test
  public void testSingleTokenFollowedByWildcard() throws Exception {
    String input = "{A}+3";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    String output = result.get(0);
    assertTrue(output.contains("{A}"));
  }
@Test
  public void testMultipleEscapedBrackets() throws Exception {
    String input = "\\({A}\\)|\\({B}\\)";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(2, result.size());
    assertTrue(result.get(0).contains("\\(") || result.get(1).contains("\\("));
    assertTrue(result.get(0).contains("\\)") || result.get(1).contains("\\)"));
    assertTrue(result.get(0).contains("{A}") || result.get(1).contains("{B}"));
  }
@Test
  public void testTokenWithNoBrackets() throws Exception {
    String input = "ABC";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("ABC"));
  }
@Test
  public void testNestingOfDifferentWildcardTypes() throws Exception {
    String input = "(({A})+2)*2";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("{A}"));
  }
@Test
  public void testMultipleConsecutiveGroups() throws Exception {
    String input = "({A})({B})({C})";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());

    String output = result.get(0);
    assertTrue(output.contains("{A}"));
    assertTrue(output.contains("{B}"));
    assertTrue(output.contains("{C}"));
  }
@Test
  public void testWildcardQuestionWithoutRepeatDigit() throws Exception {
    String input = "({X})?";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("{__o__}"));
  }
@Test
  public void testWildcardZeroRepeatPlus() throws Exception {
    String input = "({A})+0";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
  }
@Test
  public void testEscapeSequenceAtEnd() throws Exception {
    String input = "abc\\";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    String clean = result.get(0);
    assertTrue(clean.endsWith("\\)"));
  }
@Test
  public void testOrWithNestedAndFlatTokens() throws Exception {
    String input = "({A}|({B}{C}))";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(2, result.size());
    assertTrue(result.get(0).contains("{A}") || result.get(1).contains("{A}"));
    assertTrue(result.get(0).contains("{B}{C}") || result.get(1).contains("{B}{C}"));
  }
@Test
  public void testConsecutiveOrWithoutBrackets() throws Exception {
    String input = "{A}|{B}|{C}";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(3, result.size());
    assertTrue(result.contains("{A}"));
    assertTrue(result.contains("{B}"));
    assertTrue(result.contains("{C}"));
  }
@Test
  public void testWildcardStarOnlyNoRepeatSuffix() throws Exception {
    String input = "({WORD})*";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("({WORD})"));
  }
@Test
  public void testWildcardQuestionOnlyNoRepeatSuffix() throws Exception {
    String input = "({X})?";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("{X}"));
    assertTrue(result.get(0).contains("{__o__}"));
  }
@Test
  public void testOrWithWhitespaceTokens() throws Exception {
    String input = "{A} | {B} ";
    List<String> result = SubQueryParser.parseQuery(input);
    String trimmed0 = result.get(0).trim();
    String trimmed1 = result.get(1).trim();
    assertTrue(trimmed0.equals("{A}") || trimmed0.equals("{B}"));
    assertTrue(trimmed1.equals("{A}") || trimmed1.equals("{B}"));
    assertEquals(2, result.size());
  }
@Test
  public void testComplexNestedOrInsideWildcardClause() throws Exception {
    String input = "(({X}|{Y})+2){Z}";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(2, result.size());
    boolean foundX = result.get(0).contains("{X}") || result.get(1).contains("{X}");
    boolean foundY = result.get(0).contains("{Y}") || result.get(1).contains("{Y}");
    boolean foundZ = result.get(0).contains("{Z}") && result.get(1).contains("{Z}");
    assertTrue(foundX || foundY);
    assertTrue(foundZ);
  }
@Test
  public void testBracketImmediatelyClosed() throws Exception {
    String input = "()";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("()"));
  }
@Test(expected = SearchException.class)
  public void testOnlyOpenRoundBracket() throws Exception {
    String input = "(";
    SubQueryParser.parseQuery(input);
  }
@Test(expected = SearchException.class)
  public void testOnlyCloseRoundBracket() throws Exception {
    String input = ")";
    SubQueryParser.parseQuery(input);
  }
@Test
  public void testEscapedPipeWithinTokens() throws Exception {
    String input = "{A\\|B}";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("\\|"));
  }
@Test
  public void testWildcardNoGroup() throws Exception {
    String input = "{A}+3";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("{A}"));
  }
@Test
  public void testMultipleEmptyOrBranches() throws Exception {
    String input = "| |";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(3, result.size());
    assertTrue(result.get(0).trim().isEmpty());
    assertTrue(result.get(1).trim().isEmpty());
    assertTrue(result.get(2).trim().isEmpty());
  }
@Test
  public void testRedundantOuterBrackets() throws Exception {
    String input = "(({A}))";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("{A}"));
  }
@Test
  public void testEscapedBackslashAtEnd() throws Exception {
    String input = "abc\\\\";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("\\\\"));
  }
@Test
  public void testRepetitionGroupWithWhitespaceInToken() throws Exception {
    String input = "({ A })+2";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("{ A }"));
  }
@Test
  public void testQueryWithEscapedRoundBrackets() throws Exception {
    String input = "\\({A}\\)";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("\\({A}\\)"));
  }
@Test
  public void testSingleWhitespaceOnly() throws Exception {
    String input = " ";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(0, result.size());
  }
@Test
  public void testLiteralOpenBracketEscaped() throws Exception {
    String input = "abc\\(";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("\\("));
  }
@Test
  public void testLiteralCloseBracketEscaped() throws Exception {
    String input = "def\\)";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("\\)"));
  }
@Test
  public void testLiteralOrEscaped() throws Exception {
    String input = "abc\\|xyz";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("abc\\|xyz"));
  }
@Test
  public void testOnlyWildcardSymbolPlus() throws Exception {
    String input = "+";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("+"));
  }
@Test
  public void testOnlyWildcardSymbolStar() throws Exception {
    String input = "*";
    try {
      SubQueryParser.parseQuery(input);
      fail("Expected SearchException");
    } catch (SearchException e) {
      assertTrue(e.getMessage().toLowerCase().contains("unbalanced"));
    }
  }
@Test
  public void testOnlyWildcardSymbolQuestion() throws Exception {
    String input = "?";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("?"));
  }
@Test
  public void testBracketFollowedByMultipleWildcardModifiers() throws Exception {
    String input = "({X})+3*2";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("{X}"));
  }
@Test(expected = SearchException.class)
  public void testIncompleteEscapeAtEnd() throws Exception {
    String input = "A\\";
    SubQueryParser.parseQuery(input);
  }
@Test
  public void testBalancedBracketsWithNoTokens() throws Exception {
    String input = "()()";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("()()"));
  }
@Test
  public void testAlternationWithWhitespaceOnlyElements() throws Exception {
    String input = "{A}| |{B}";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(3, result.size());
    boolean foundEmpty = result.get(0).trim().isEmpty() || result.get(1).trim().isEmpty() || result.get(2).trim().isEmpty();
    boolean foundA = result.get(0).contains("{A}") || result.get(1).contains("{A}") || result.get(2).contains("{A}");
    boolean foundB = result.get(0).contains("{B}") || result.get(1).contains("{B}") || result.get(2).contains("{B}");
    assertTrue(foundEmpty);
    assertTrue(foundA);
    assertTrue(foundB);
  }
@Test
  public void testConsecutiveEmptyBrackets() throws Exception {
    String input = "({})({})";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("({})({})"));
  }
@Test
  public void testWildcardWithSingleDigit() throws Exception {
    String input = "({T})+1";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    String expanded = result.get(0);
    assertTrue(expanded.contains("{T}"));
    assertFalse(expanded.contains("{__o__}"));
  }
@Test
  public void testWildcardZeroRepeatExpansion() throws Exception {
    String input = "({T})*0";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    String output = result.get(0);
    assertTrue(output.contains("{__o__}")); 
  }
@Test
  public void testOrWithEmptyLeftAndRight() throws Exception {
    String input = "|";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(2, result.size());
    assertTrue(result.get(0).trim().isEmpty());
    assertTrue(result.get(1).trim().isEmpty());
  }
@Test
  public void testQueryWithBackslashLiteral() throws Exception {
    String input = "abc\\\\def";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("\\\\"));
  }
@Test
  public void testEscapedBracketsWithinAlternatives() throws Exception {
    String input = "\\(\\{X\\}\\)|{Y}";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(2, result.size());
    String r0 = result.get(0);
    String r1 = result.get(1);
    boolean hasEscaped = r0.contains("\\(") || r1.contains("\\(");
    boolean hasY = r0.contains("{Y}") || r1.contains("{Y}");
    assertTrue(hasEscaped);
    assertTrue(hasY);
  }
@Test
  public void testFlatConcatenationOfMultipleTokens() throws Exception {
    String input = "{A}{B}{C}{D}";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    String resultString = result.get(0);
    assertTrue(resultString.contains("{A}"));
    assertTrue(resultString.contains("{B}"));
    assertTrue(resultString.contains("{C}"));
    assertTrue(resultString.contains("{D}"));
  }
@Test
  public void testMixOfLiteralCharactersAndBrackets() throws Exception {
    String input = "hello({A})world";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    String value = result.get(0);
    assertTrue(value.contains("hello"));
    assertTrue(value.contains("world"));
    assertTrue(value.contains("{A}"));
  }
@Test(expected = SearchException.class)
  public void testBracketOpenedAndUnclosedInsideWildcard() throws Exception {
    String input = "({A({B})+2";
    SubQueryParser.parseQuery(input);
  }

@Test
  public void testMultipleNestedOptionalWildcards() throws Exception {
    String input = "(({X})?2)?2";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    String output = result.get(0);
    assertTrue(output.contains("{__o__}"));
    assertTrue(output.contains("{X}") || output.contains("({X})"));
  }
@Test
  public void testOrWithDeeplyNestedGroups() throws Exception {
    String input = "((({A})|(({B})|{C})){D})";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(3, result.size());
    boolean hasAD = result.get(0).contains("{A}") && result.get(0).contains("{D}");
    boolean hasBD = result.get(1).contains("{B}") && result.get(1).contains("{D}");
    boolean hasCD = result.get(2).contains("{C}") && result.get(2).contains("{D}");
    assertTrue(hasAD || hasBD || hasCD);
  }
@Test
  public void testTokenContainingEscapedQuoteSymbol() throws Exception {
    String input = "{\\\"quoted\\\"}";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("\\\"quoted\\\""));
  }
@Test
  public void testConsecutiveWildcardsWithoutWhitespace() throws Exception {
    String input = "({X})+2({Y})+2";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    String output = result.get(0);
    assertTrue(output.contains("{X}"));
    assertTrue(output.contains("{Y}"));
  }
@Test
  public void testWildcardFollowedByNonDigitGarbage() throws Exception {
    String input = "({A})+gibberish";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("{A}"));
  }
@Test
  public void testAlternationInsideWildcardWithTrailingPlus() throws Exception {
    String input = "(({A}|{B})+)";
    List<String> result = SubQueryParser.parseQuery(input);
    assertTrue(result.size() >= 1);
    String output = result.get(0);
    assertTrue(output.contains("{A}") || output.contains("{B}"));
  }
@Test
  public void testTrickyEscapeMixInsideGroup() throws Exception {
    String input = "({A\\|B\\(C\\)})";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("\\("));
    assertTrue(result.get(0).contains("\\|"));
    assertTrue(result.get(0).contains("\\)"));
  }
@Test(expected = SearchException.class)
  public void testOrInsideInvalidBracketNesting() throws Exception {
    String input = "({A}|({B})";
    SubQueryParser.parseQuery(input);
  }
@Test
  public void testNoWhitespaceBetweenTokens() throws Exception {
    String input = "{A}{B}{C}";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    String out = result.get(0);
    assertTrue(out.contains("{A}") && out.contains("{B}") && out.contains("{C}"));
  }
@Test(expected = SearchException.class)
  public void testWildcardImmediatelyAfterUnclosedGroup() throws Exception {
    String input = "({A*3";
    SubQueryParser.parseQuery(input);
  }
@Test
  public void testEscapedEscapeCharacterInToken() throws Exception {
    String input = "{\\\\}";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("\\\\"));
  }
@Test(expected = SearchException.class)
  public void testMalformedOpenBracketInsideToken() throws Exception {
    String input = "({A}({B})";
    SubQueryParser.parseQuery(input);
  }
@Test
  public void testOrFollowingEmptyGroup() throws Exception {
    String input = "()|{X}";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(2, result.size());
    assertTrue(result.get(0).contains("()") || result.get(1).contains("()"));
    assertTrue(result.get(0).contains("{X}") || result.get(1).contains("{X}"));
  }
@Test
  public void testEmptyGroupInsideWildcard() throws Exception {
    String input = "(()+2)";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("()"));
  }
@Test
  public void testWildcardAppliedToWhitespaceOnlyGroup() throws Exception {
    String input = "( )*2";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("{__o__}") || result.get(0).contains("("));
  }
@Test
  public void testAlternationWithDeeplyNestedOptional() throws Exception {
    String input = "(({A}?2)|({B}?2))";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(2, result.size());
    boolean hasAorB = result.get(0).contains("{A}") || result.get(1).contains("{B}") ||
                      result.get(0).contains("{B}") || result.get(1).contains("{A}");
    boolean hasOptional = result.get(0).contains("{__o__}") || result.get(1).contains("{__o__}");
    assertTrue(hasAorB);
    assertTrue(hasOptional);
  }
@Test
  public void testAlternationWithWhitespaceOnlyTokens() throws Exception {
    String input = "   |   ";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(2, result.size());
    assertTrue(result.get(0).trim().isEmpty());
    assertTrue(result.get(1).trim().isEmpty());
  }
@Test
  public void testTokenWithMultipleBackslashes() throws Exception {
    String input = "{A\\\\B\\\\C}";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("\\\\"));
  }
@Test
  public void testUnbalancedGroupInsideRepetition() throws Exception {
    String input = "(({X})+2({Y})";
    try {
      SubQueryParser.parseQuery(input);
      fail("Expected SearchException");
    } catch (SearchException e) {
      assertTrue(e.getMessage().toLowerCase().contains("unbalanced"));
    }
  }
@Test
  public void testWhitespaceOnlyInsideGrouping() throws Exception {
    String input = "(   )";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("()") || result.get(0).contains(" "));
  }
@Test
  public void testAlternationWithEmptySecondBranch() throws Exception {
    String input = "{A}|";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(2, result.size());
    String r0 = result.get(0);
    String r1 = result.get(1);
    boolean hasA = r0.contains("{A}") || r1.contains("{A}");
    boolean hasEmpty = r0.trim().isEmpty() || r1.trim().isEmpty();
    assertTrue(hasA && hasEmpty);
  }
@Test
  public void testAlternationWithEmptyFirstBranch() throws Exception {
    String input = "|{B}";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(2, result.size());
    String r0 = result.get(0);
    String r1 = result.get(1);
    boolean hasB = r0.contains("{B}") || r1.contains("{B}");
    boolean hasEmpty = r0.trim().isEmpty() || r1.trim().isEmpty();
    assertTrue(hasB && hasEmpty);
  }
@Test
  public void testGroupWithMixedTokensAndWhitespace() throws Exception {
    String input = "({A} {B} {C})";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("{A}"));
    assertTrue(result.get(0).contains("{B}"));
    assertTrue(result.get(0).contains("{C}"));
  }
@Test
  public void testWildcardImmediatelyAfterTextToken() throws Exception {
    String input = "sample+2";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("sample"));
  }
@Test
  public void testWildcardImmediatelyAfterBracketTokenWithWhitespace() throws Exception {
    String input = "({A}) + 2";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    assertTrue(result.get(0).contains("{A}"));
  }
@Test
  public void testOrFollowingWildcardGroup() throws Exception {
    String input = "({A})*2|{B}";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(2, result.size());
    String r0 = result.get(0);
    String r1 = result.get(1);
    boolean hasAorWildcard = r0.contains("{A}") || r1.contains("{A}");
    boolean hasB = r0.contains("{B}") || r1.contains("{B}");
    assertTrue(hasAorWildcard && hasB);
  }
@Test
  public void testWildcardGroupFollowedByLiteralCharacters() throws Exception {
    String input = "({X})+2andThen";
    List<String> result = SubQueryParser.parseQuery(input);
    assertEquals(1, result.size());
    String combined = result.get(0);
    assertTrue(combined.contains("{X}"));
    assertTrue(combined.contains("andThen"));
  } 
}