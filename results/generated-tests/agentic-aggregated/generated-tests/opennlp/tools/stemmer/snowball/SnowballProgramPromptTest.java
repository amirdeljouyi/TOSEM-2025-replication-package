package opennlp.tools.stemmer.snowball;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SnowballProgramPromptTest {

  @Test
  public void constructorStartsWithEmptyCurrentText() {
    SnowballProgram program = new SnowballProgram();

    assertEquals("", program.getCurrent());
  }

  @Test
  public void setCurrentReplacesPreviouslyConfiguredText() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("first");
    assertEquals("first", program.getCurrent());

    program.setCurrent("second");
    assertEquals("second", program.getCurrent());
  }

  @Test
  public void setCurrentAcceptsEmptyTextAfterNonEmptyText() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("nonempty");
    program.setCurrent("");

    assertEquals("", program.getCurrent());
    assertTrue(program.eq_s(""));
    assertFalse(program.eq_s("x"));
  }

  @Test(expected = NullPointerException.class)
  public void setCurrentRejectsNull() {
    SnowballProgram program = new SnowballProgram();

    program.setCurrent(null);
  }

  @Test
  public void currentTextPreservesUnicodeCodeUnits() {
    SnowballProgram program = new SnowballProgram();
    String text = "caf\u00e9 \u03a3 \ud83d\ude00";

    program.setCurrent(text);

    assertEquals(text, program.getCurrent());
    assertTrue(program.eq_s(text));
  }

  @Test
  public void separateProgramsRetainIndependentCurrentText() {
    SnowballProgram first = new SnowballProgram();
    SnowballProgram second = new SnowballProgram();
    first.setCurrent("alpha");
    second.setCurrent("beta");

    first.setCurrent("changed");

    assertEquals("changed", first.getCurrent());
    assertEquals("beta", second.getCurrent());
  }

  @Test
  public void copyConstructorCopiesCurrentTextAndMatchingPosition() {
    SnowballProgram original = new SnowballProgram();
    original.setCurrent("prefix-suffix");
    assertTrue(original.eq_s("prefix-"));

    SnowballProgram copy = new SnowballProgram(original);

    assertEquals("prefix-suffix", copy.getCurrent());
    assertTrue(copy.eq_s("suffix"));
    assertTrue(original.eq_s("suffix"));
  }

  @Test
  public void copyConstructorSharesExistingMutableBuffer() {
    SnowballProgram original = new SnowballProgram();
    original.setCurrent("abc");
    SnowballProgram copy = new SnowballProgram(original);

    original.replace_s(1, 2, "X");

    assertEquals("aXc", original.getCurrent());
    assertEquals("aXc", copy.getCurrent());
  }

  @Test
  public void setCurrentDetachesProgramFromCopiedBuffer() {
    SnowballProgram original = new SnowballProgram();
    original.setCurrent("shared");
    SnowballProgram copy = new SnowballProgram(original);

    original.setCurrent("new value");

    assertEquals("new value", original.getCurrent());
    assertEquals("shared", copy.getCurrent());
    assertNotSame(original.getCurrent(), copy.getCurrent());
  }

  @Test
  public void copyFromCopiesTextAndCursorState() {
    SnowballProgram source = new SnowballProgram();
    source.setCurrent("abcdef");
    assertTrue(source.eq_s("ab"));
    SnowballProgram destination = new SnowballProgram();
    destination.setCurrent("discarded");

    destination.copy_from(source);

    assertEquals("abcdef", destination.getCurrent());
    assertTrue(destination.eq_s("cd"));
    assertTrue(source.eq_s("cd"));
  }

  @Test
  public void forwardEqualityConsumesAnExactMatch() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("snowball");

    assertTrue(program.eq_s(new StringBuilder("snow")));
    assertTrue(program.eq_s("ball"));
    assertFalse(program.eq_s("x"));
  }

  @Test
  public void forwardEqualityMismatchDoesNotMovePosition() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("testing");

    assertFalse(program.eq_s("toast"));
    assertTrue(program.eq_s("test"));
    assertTrue(program.eq_s("ing"));
  }

  @Test
  public void forwardEqualityRejectsTextLongerThanRemainder() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("abc");
    assertTrue(program.eq_s("ab"));

    assertFalse(program.eq_s("bc"));
    assertTrue(program.eq_s("c"));
  }

  @Test
  public void backwardEqualityConsumesFromEnd() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("prefix-suffix");
    assertTrue(program.eq_s(program.getCurrent()));

    assertTrue(program.eq_s_b(new StringBuilder("suffix")));
    assertTrue(program.eq_s_b("prefix-"));
    assertFalse(program.eq_s_b("x"));
  }

  @Test
  public void backwardEqualityMismatchDoesNotMovePosition() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("abcdef");
    assertTrue(program.eq_s("abcdef"));

    assertFalse(program.eq_s_b("xef"));
    assertTrue(program.eq_s_b("def"));
    assertTrue(program.eq_s_b("abc"));
  }

  @Test
  public void inGroupingConsumesOnlyConfiguredCharacters() {
    SnowballProgram program = new SnowballProgram();
    char[] group = {(char) 5};
    program.setCurrent("abc");

    assertTrue(program.in_grouping(group, 'a', 'c'));
    assertFalse(program.in_grouping(group, 'a', 'c'));
    assertTrue(program.eq_s("bc"));
  }

  @Test
  public void inGroupingDoesNotConsumeOutOfRangeOrEndPosition() {
    SnowballProgram program = new SnowballProgram();
    char[] group = {(char) 7};
    program.setCurrent("z");

    assertFalse(program.in_grouping(group, 'a', 'c'));
    assertTrue(program.eq_s("z"));
    assertFalse(program.in_grouping(group, 'a', 'c'));
  }

  @Test
  public void outGroupingConsumesAbsentAndOutOfRangeCharacters() {
    SnowballProgram program = new SnowballProgram();
    char[] onlyA = {(char) 1};
    program.setCurrent("bza");

    assertTrue(program.out_grouping(onlyA, 'a', 'c'));
    assertTrue(program.out_grouping(onlyA, 'a', 'c'));
    assertFalse(program.out_grouping(onlyA, 'a', 'c'));
    assertTrue(program.eq_s("a"));
  }

  @Test
  public void backwardGroupingOperationsMoveTowardBeginning() {
    SnowballProgram program = new SnowballProgram();
    char[] onlyC = {(char) 4};
    program.setCurrent("abc");
    assertTrue(program.eq_s("abc"));

    assertTrue(program.in_grouping_b(onlyC, 'a', 'c'));
    assertTrue(program.out_grouping_b(onlyC, 'a', 'c'));
    assertTrue(program.out_grouping_b(onlyC, 'a', 'c'));
    assertFalse(program.out_grouping_b(onlyC, 'a', 'c'));
  }

  @Test
  public void replaceCanShrinkMiddleOfCurrentText() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("unwanted");

    assertEquals(-3, program.replace_s(2, 6, "x"));
    assertEquals("unxed", program.getCurrent());
  }

  @Test
  public void replaceCanExpandMiddleOfCurrentText() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("abcd");

    assertEquals(3, program.replace_s(1, 2, "LONG"));
    assertEquals("aLONGcd", program.getCurrent());
  }

  @Test
  public void replaceCanKeepLengthAndUseCharSequenceContents() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("abcd");

    assertEquals(0, program.replace_s(1, 3, new StringBuilder("XY").toString()));
    assertEquals("aXYd", program.getCurrent());
  }

  @Test
  public void shrinkingReplacementAdjustsCursorAfterChangedRange() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("abcdef");
    assertTrue(program.eq_s("abc"));

    program.replace_s(0, 2, "X");

    assertEquals("Xcdef", program.getCurrent());
    assertTrue(program.eq_s("def"));
  }

  @Test
  public void replacementMovesCursorAtInsidePositionToStartOfRange() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("abcdef");
    assertTrue(program.eq_s("abc"));

    program.replace_s(2, 5, "Q");

    assertEquals("abQf", program.getCurrent());
    assertTrue(program.eq_s("Qf"));
  }

  @Test
  public void replacementAfterCursorLeavesCursorUnchanged() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("abcdef");
    assertTrue(program.eq_s("a"));

    program.replace_s(2, 4, "XYZ");

    assertEquals("abXYZef", program.getCurrent());
    assertTrue(program.eq_s("bXYZef"));
  }

  @Test
  public void sliceFromReplacesInitialWholeSlice() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("entire value");

    program.slice_from(new StringBuilder("replacement"));

    assertEquals("replacement", program.getCurrent());
  }

  @Test
  public void sliceDeleteRemovesInitialWholeSlice() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("delete me");

    program.slice_del();

    assertEquals("", program.getCurrent());
  }

  @Test
  public void insertUpdatesSliceBoundariesForSubsequentOperations() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("abc");

    program.insert(0, 0, new StringBuilder("X"));
    StringBuilder selected = new StringBuilder("old contents");
    program.slice_to(selected);
    program.slice_from("Y");

    assertEquals("abc", selected.toString());
    assertEquals("XY", program.getCurrent());
  }

  @Test
  public void sliceToClearsDestinationBeforeCopying() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("fresh");
    StringBuilder destination = new StringBuilder("stale text");

    program.slice_to(destination);

    assertEquals("fresh", destination.toString());
    assertEquals("fresh", program.getCurrent());
  }

  @Test
  public void findAmongReturnsExactResultAndConsumesMatch() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("testing");
    Among[] choices = {new Among("test", -1, 17)};

    assertEquals(17, program.find_among(choices));
    assertTrue(program.eq_s("ing"));
  }

  @Test
  public void findAmongUsesConfiguredFallbackSubstring() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("ax");
    Among[] choices = {
        new Among("a", -1, 1),
        new Among("ab", 0, 2)
    };

    assertEquals(1, program.find_among(choices));
    assertTrue(program.eq_s("x"));
  }

  @Test
  public void findAmongReturnsZeroAndDoesNotConsumeOnNoMatch() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("other");
    Among[] choices = {new Among("test", -1, 9)};

    assertEquals(0, program.find_among(choices));
    assertTrue(program.eq_s("other"));
  }

  @Test
  public void findAmongCanMatchEmptyChoiceWithoutMoving() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("text");
    Among[] choices = {new Among("", -1, 4)};

    assertEquals(4, program.find_among(choices));
    assertTrue(program.eq_s("text"));
  }

  @Test
  public void findAmongBackwardReturnsResultAndConsumesSuffix() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("testing");
    assertTrue(program.eq_s("testing"));
    Among[] choices = {new Among("ing", -1, 23)};

    assertEquals(23, program.find_among_b(choices));
    assertTrue(program.eq_s_b("test"));
  }

  @Test
  public void concreteStemmersCanReuseInheritedCurrentState() {
    englishStemmer english = new englishStemmer();
    porterStemmer porter = new porterStemmer();
    english.setCurrent("relational");
    porter.setCurrent("ponies");

    assertTrue(english.stem());
    assertTrue(porter.stem());
    assertEquals("relat", english.getCurrent());
    assertEquals("poni", porter.getCurrent());

    english.setCurrent("skies");
    assertTrue(english.stem());
    assertEquals("sky", english.getCurrent());
    assertEquals("poni", porter.getCurrent());
  }
}
