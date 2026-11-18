package gate.creole.annic.lucene;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import gate.creole.ir.SearchException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;

public class SubQueryParserPromptTest {

  @Test
  public void constructorCreatesUsableParserInstance() throws Exception {
    SubQueryParser parser = new SubQueryParser();

    assertNotNull(parser);
    assertEquals(Collections.singletonList("{Token}"), parser.parseQuery("{Token}"));
  }

  @Test
  public void parseQueryReturnsNoQueriesForEmptyOrWhitespaceOnlyInput() throws Exception {
    List<String> emptyResult = SubQueryParser.parseQuery("");
    List<String> whitespaceResult = SubQueryParser.parseQuery(" \t  \n ");

    assertTrue(emptyResult.isEmpty());
    assertTrue(whitespaceResult.isEmpty());
  }

  @Test
  public void parseQueryTrimsOuterWhitespaceAndPreservesInnerText() throws Exception {
    List<String> result = SubQueryParser.parseQuery("  {Token string=\"New York\"}  ");

    assertEquals(Collections.singletonList("{Token string=\"New York\"}"), result);
  }

  @Test
  public void parseQueryNormalizesTopLevelOrAlternatives() throws Exception {
    List<String> result = SubQueryParser.parseQuery("{A}|{B}|{C}");

    assertEquals(Arrays.asList("{C}", "{B}", "{A}"), result);
  }

  @Test
  public void parseQueryDistributesSuffixAcrossGroupedAlternatives() throws Exception {
    List<String> result = SubQueryParser.parseQuery("({A}|{B}){C}");

    assertEquals(Arrays.asList("{B}{C}", "{A}{C}"), result);
  }

  @Test
  public void parseQueryDistributesPrefixAcrossGroupedAlternatives() throws Exception {
    List<String> result = SubQueryParser.parseQuery("{A}({B}|{C})");

    assertEquals(Arrays.asList("{A}{C}", "{A}{B}"), result);
  }

  @Test
  public void parseQueryBuildsCartesianProductAcrossTwoGroups() throws Exception {
    List<String> result = SubQueryParser.parseQuery("({A}|{B})({C}|{D})");

    assertEquals(Arrays.asList("{A}{D}", "{A}{C}", "{B}{D}", "{B}{C}"), result);
  }

  @Test
  public void parseQueryFlattensNestedAlternatives() throws Exception {
    List<String> result = SubQueryParser.parseQuery("(({A}|{B})|({C}|{D}))");

    assertEquals(Arrays.asList("{D}", "{C}", "{B}", "{A}"), result);
  }

  @Test
  public void parseQueryRemovesDuplicateNormalizedQueries() throws Exception {
    List<String> result = SubQueryParser.parseQuery("({A}|{A}|({A}))");

    assertEquals(Collections.singletonList("{A}"), result);
  }

  @Test
  public void parseQueryTreatsEscapedOperatorsAndBracketsAsText() throws Exception {
    List<String> escapedOr = SubQueryParser.parseQuery("A\\|B");
    List<String> escapedBrackets = SubQueryParser.parseQuery("\\(A\\)");
    List<String> mixed = SubQueryParser.parseQuery("(A\\|B|C)");

    assertEquals(Collections.singletonList("A\\|B"), escapedOr);
    assertEquals(Collections.singletonList("\\(A\\)"), escapedBrackets);
    assertEquals(Arrays.asList("C", "A\\|B"), mixed);
  }

  @Test
  public void parseQueryExpandsSingleCharacterWildcards() throws Exception {
    List<String> star = SubQueryParser.parseQuery("(A)*");
    List<String> plus = SubQueryParser.parseQuery("(A)+");
    List<String> question = SubQueryParser.parseQuery("(A)?");

    assertEquals(Collections.singletonList("A"), star);
    assertEquals(Collections.singletonList("A"), plus);
    assertEquals(Collections.singletonList("A"), question);
  }

  @Test
  public void parseQueryExpandsNumberedWildcardIntoEveryLength() throws Exception {
    List<String> star = SubQueryParser.parseQuery("(A)*3");
    List<String> plus = SubQueryParser.parseQuery("(A)+3");
    List<String> question = SubQueryParser.parseQuery("(A)?3");

    assertEquals(Arrays.asList("AAA", "AA", "A"), star);
    assertEquals(Arrays.asList("AAA", "AA", "A"), plus);
    assertEquals(Arrays.asList("AAA", "AA", "A"), question);
  }

  @Test
  public void parseQueryCombinesWildcardExpansionWithSurroundingText() throws Exception {
    List<String> result = SubQueryParser.parseQuery("x(A)+2y");

    assertEquals(Arrays.asList("x AAy", "xA y"), result);
  }

  @Test
  public void parseQueryCombinesNestedAlternativesWithWildcardExpansion() throws Exception {
    List<String> result = SubQueryParser.parseQuery("((A)|(B))*2");

    assertEquals(Arrays.asList("AB", "AA", "BB", "BA", "B", "A"), result);
  }

  @Test
  public void parseQueryWithZeroRepetitionsProducesNoNonemptyQuery() throws Exception {
    List<String> optional = SubQueryParser.parseQuery("(A)*0");
    List<String> required = SubQueryParser.parseQuery("(A)+0");

    assertTrue(optional.isEmpty());
    assertTrue(required.isEmpty());
  }

  @Test
  public void parseQueryLeavesOrdinaryWildcardCharactersUntouched() throws Exception {
    List<String> result = SubQueryParser.parseQuery("A* B+ C?");

    assertEquals(Collections.singletonList("A* B+ C?"), result);
  }

  @Test
  public void parseQueryRejectsMissingClosingBracketWithLocation() {
    SearchException exception = assertThrows(SearchException.class,
        () -> SubQueryParser.parseQuery("(A"));

    assertTrue(exception.getMessage().contains("unbalanced brackets"));
    assertTrue(exception.getMessage().contains("a closing bracket ()) is missing"));
    assertTrue(exception.getMessage().contains("( (A )"));
    assertTrue(exception.getMessage().endsWith("^"));
  }

  @Test
  public void parseQueryRejectsMissingOpeningBracketWithLocation() {
    SearchException exception = assertThrows(SearchException.class,
        () -> SubQueryParser.parseQuery("A)"));

    assertTrue(exception.getMessage().contains("unbalanced brackets"));
    assertTrue(exception.getMessage().contains("a opening bracket (() is missing"));
    assertTrue(exception.getMessage().contains("( A) )"));
    assertTrue(exception.getMessage().endsWith("^"));
  }

  @Test
  public void parseQueryRejectsNullInput() {
    assertThrows(NullPointerException.class, () -> SubQueryParser.parseQuery(null));
  }

  @Test
  public void parseQueryPropagatesOverflowInWildcardCount() {
    assertThrows(NumberFormatException.class,
        () -> SubQueryParser.parseQuery("(A)+999999999999999999999999"));
  }

  @Test
  public void findOrTokensSplitsOnlyTopLevelOperators() {
    ArrayList<String> tokens = SubQueryParser.findOrTokens("A|(B|C)|D");

    assertEquals(Arrays.asList("A", "(B|C)", "D"), tokens);
  }

  @Test
  public void findOrTokensPreservesEscapedOperator() {
    ArrayList<String> tokens = SubQueryParser.findOrTokens("A\\|B|C");

    assertEquals(Arrays.asList("A\\|B", "C"), tokens);
  }

  @Test
  public void findOrTokensHandlesEmptyAndPlainInput() {
    ArrayList<String> empty = SubQueryParser.findOrTokens("");
    ArrayList<String> plain = SubQueryParser.findOrTokens("plain text");

    assertTrue(empty.isEmpty());
    assertEquals(Collections.singletonList("plain text"), plain);
  }

  @Test
  public void findOrTokensRetainsLeadingButDropsTrailingEmptyToken() {
    ArrayList<String> leading = SubQueryParser.findOrTokens("|A");
    ArrayList<String> trailing = SubQueryParser.findOrTokens("A|");
    ArrayList<String> onlyOperator = SubQueryParser.findOrTokens("|");

    assertEquals(Arrays.asList("", "A"), leading);
    assertEquals(Collections.singletonList("A"), trailing);
    assertEquals(1, onlyOperator.size());
    assertEquals("", onlyOperator.get(0));
  }

  @Test
  public void findOrTokensKeepsParenthesesAndTheirNestedContent() {
    ArrayList<String> tokens = SubQueryParser.findOrTokens("(A)|(B|(C|D))");

    assertEquals(Arrays.asList("(A)", "(B|(C|D))"), tokens);
  }

  @Test
  public void scanQueryReturnsFalseWhenNoActiveSyntaxExists() {
    assertFalse(SubQueryParser.scanQueryForOrOrBracket(""));
    assertFalse(SubQueryParser.scanQueryForOrOrBracket("plain text"));
    assertFalse(SubQueryParser.scanQueryForOrOrBracket("A\\|B"));
    assertFalse(SubQueryParser.scanQueryForOrOrBracket("A\\(B"));
  }

  @Test
  public void scanQueryFindsOperatorsAndOpeningBracketsAtAnyPosition() {
    assertTrue(SubQueryParser.scanQueryForOrOrBracket("|A"));
    assertTrue(SubQueryParser.scanQueryForOrOrBracket("(A)"));
    assertTrue(SubQueryParser.scanQueryForOrOrBracket("A|B"));
    assertTrue(SubQueryParser.scanQueryForOrOrBracket("A(B)"));
  }

  @Test
  public void scanQueryContinuesPastEscapedSyntaxToFindActiveSyntax() {
    assertTrue(SubQueryParser.scanQueryForOrOrBracket("A\\|B(C)"));
    assertTrue(SubQueryParser.scanQueryForOrOrBracket("A|B\\(C"));
  }

  @Test
  public void mainReadsAndParsesAQueryBeforeEndingAtEof() throws Exception {
    InputStream originalIn = System.in;
    PrintStream originalOut = System.out;
    PrintStream originalErr = System.err;
    ByteArrayInputStream input = new ByteArrayInputStream("{A}|{B}\n".getBytes(StandardCharsets.UTF_8));
    ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
    ByteArrayOutputStream errorBytes = new ByteArrayOutputStream();
    PrintStream output = new PrintStream(outputBytes, true, StandardCharsets.UTF_8.name());
    PrintStream error = new PrintStream(errorBytes, true, StandardCharsets.UTF_8.name());

    try {
      System.setIn(input);
      System.setOut(output);
      System.setErr(error);
      SubQueryParser.main(new String[] {"ignored"});
    } finally {
      System.setIn(originalIn);
      System.setOut(originalOut);
      System.setErr(originalErr);
      output.close();
      error.close();
    }

    String standardOutput = outputBytes.toString(StandardCharsets.UTF_8.name());
    String standardError = errorBytes.toString(StandardCharsets.UTF_8.name());
    assertTrue(standardOutput.contains("Query: =>{B}"));
    assertTrue(standardOutput.contains("=>{A}"));
    assertTrue(standardError.contains("NullPointerException"));
  }
}
