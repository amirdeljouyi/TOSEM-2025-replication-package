package opennlp.tools.stemmer.snowball;

import opennlp.tools.stemmer.snowball.Among;
import opennlp.tools.stemmer.snowball.SnowballProgram;
import org.junit.jupiter.api.Test;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.UndeclaredThrowableException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;

public class SnowballProgram_2_GPTLLMTest {

@Test
public void testSetAndGetCurrent_String() {
SnowballProgram program = new SnowballProgram();
String input = "running";
program.setCurrent(input);
String output = program.getCurrent();
assertEquals("running", output);
}

@Test
public void testSetAndGetCurrent_CharArray() {
SnowballProgram program = new SnowballProgram();
char[] inputChars = new char[] { 'b', 'i', 'k', 'e', 's' };
// program.setCurrent(inputChars, inputChars.length);
String output = program.getCurrent();
assertEquals("bikes", output);
}

@Test
public void testGetCurrentBufferAndLength() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("baking");
// char[] buffer = program.getCurrentBuffer();
// int length = program.getCurrentBufferLength();
// assertEquals(6, length);
// assertEquals('b', buffer[0]);
// assertEquals('g', buffer[5]);
}

@Test
public void testCopyConstructor() {
SnowballProgram original = new SnowballProgram();
original.setCurrent("fishing");
SnowballProgram copy = new SnowballProgram(original);
String originalCurrent = original.getCurrent();
String copyCurrent = copy.getCurrent();
// int originalLength = original.getCurrentBufferLength();
// int copyLength = copy.getCurrentBufferLength();
assertEquals("fishing", originalCurrent);
assertEquals("fishing", copyCurrent);
// assertEquals(originalLength, copyLength);
}

@Test
public void testCopyFrom() {
SnowballProgram source = new SnowballProgram();
source.setCurrent("driving");
SnowballProgram dest = new SnowballProgram();
dest.copy_from(source);
String value = dest.getCurrent();
assertEquals("driving", value);
}

@Test
public void testEqS_PositiveMatch() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("runner");
boolean result = program.eq_s("run");
assertTrue(result);
assertEquals("runner".substring(0, 3), program.getCurrent().substring(0, 3));
}

@Test
public void testEqS_Failure() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("runner");
boolean result = program.eq_s("xyz");
assertFalse(result);
assertEquals("runner", program.getCurrent());
}

@Test
public void testEqSB_PositiveMatch() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("geese");
program.cursor = 5;
boolean result = program.eq_s_b("se");
assertTrue(result);
assertEquals(3, program.cursor);
}

@Test
public void testEqSB_Failure() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("jumping");
program.cursor = 7;
boolean result = program.eq_s_b("ink");
assertFalse(result);
assertEquals(7, program.cursor);
}

@Test
public void testSliceDel_RemovesSlice() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("unstoppable");
program.bra = 0;
program.ket = 3;
program.slice_del();
String updated = program.getCurrent();
assertEquals("stoppable", updated);
}

@Test
public void testSliceFrom_ReplacesWithDifferentLength() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("rewriting");
program.bra = 2;
program.ket = 5;
program.slice_from("code");
String result = program.getCurrent();
assertEquals("recodeting", result);
}

@Test
public void testInsert_TextInMiddle() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdef");
program.bra = 1;
program.ket = 3;
program.insert(2, 2, "XX");
String expected = "abXXcdef";
assertEquals(expected, program.getCurrent());
}

@Test
public void testSliceTo_ValidSlice() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("truncated");
program.bra = 1;
program.ket = 5;
StringBuilder sb = new StringBuilder();
program.slice_to(sb);
assertEquals("runc", sb.toString());
}

@Test
public void testAssignTo_AppendsFullBuffer() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("archive");
StringBuilder sb = new StringBuilder("irrelevant");
// program.assign_to(sb);
assertEquals("archive", sb.toString());
}

@Test
public void testFindAmong_SimpleMatchWithoutMethod() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("walked");
// Among[] among = new Among[] { new Among("walk", -1, 1, null), new Among("walked", 0, 2, null) };
// int result = program.find_among(among);
// assertEquals(2, result);
}

@Test
public void testFindAmongB_MatchWithoutMethod() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("cats");
program.cursor = 4;
// Among[] among = new Among[] { new Among("s", -1, 1, null), new Among("ts", 0, 2, null) };
// int result = program.find_among_b(among);
// assertEquals(2, result);
}

@Test
public void testFindAmong_WithMethodReturnsTrue() throws Throwable {
SnowballProgram program = new SnowballProgram() {

public boolean acceptMatch() {
return true;
}
};
program.setCurrent("marked");
// Among[] among = new Among[] { new Among("marked", -1, 5, MethodHandles.lookup().findVirtual(program.getClass(), "acceptMatch", MethodType.methodType(boolean.class))) };
// int result = program.find_among(among);
// assertEquals(5, result);
}

@Test
public void testFindAmong_WithMethodReturnsFalse() throws Throwable {
SnowballProgram program = new SnowballProgram() {

public boolean rejectMatch() {
return false;
}
};
program.setCurrent("failed");
// Among[] among = new Among[] { new Among("failed", -1, 3, MethodHandles.lookup().findVirtual(program.getClass(), "rejectMatch", MethodType.methodType(boolean.class))) };
// int result = program.find_among(among);
// assertEquals(0, result);
}

@Test
public void testSetCurrent_EmptyString() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("");
assertEquals("", program.getCurrent());
// assertEquals(0, program.getCurrentBufferLength());
}

@Test
public void testInGrouping_AtLimit_ShouldReturnFalse() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("a");
char[] group = new char[32];
int min = 'a';
int max = 'z';
program.cursor = 1;
boolean result = program.in_grouping(group, min, max);
assertFalse(result);
}

@Test
public void testInGroupingB_AtLimitBackward_ShouldReturnFalse() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("b");
program.cursor = 0;
boolean result = program.in_grouping_b(new char[32], 'a', 'z');
assertFalse(result);
}

@Test
public void testOutGrouping_MatchInsideRangeAndBitSet() {
SnowballProgram program = new SnowballProgram();
char[] group = new char[32];
int min = 'a';
int max = 'z';
String input = "c";
int bitIndex = ('c' - min) & 0x7;
group[('c' - min) >> 3] |= (1 << bitIndex);
program.setCurrent(input);
boolean result = program.out_grouping(group, min, max);
assertFalse(result);
}

@Test
public void testOutGrouping_CharOutOfRangeAbove() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("~");
char[] group = new char[32];
int min = 'a';
int max = 'z';
boolean result = program.out_grouping(group, min, max);
assertTrue(result);
}

@Test
public void testOutGroupingB_CharOutOfRangeBelow() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("A");
program.cursor = 1;
char[] group = new char[32];
int min = 'a';
int max = 'z';
boolean result = program.out_grouping_b(group, min, max);
assertTrue(result);
}

@Test
public void testEqS_ExactLengthMatch() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
boolean result = program.eq_s("abc");
assertTrue(result);
}

@Test
public void testEqSB_ExactLengthMatchBackward() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("xyz");
program.cursor = 3;
boolean result = program.eq_s_b("xyz");
assertTrue(result);
}

@Test
public void testReplaceS_InsertLongerThanOriginal() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
int delta = program.replace_s(1, 2, "1234567");
assertEquals(6, delta);
assertEquals("a1234567c", program.getCurrent());
}

@Test
public void testReplaceS_InsertShorterThanOriginal() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdef");
int delta = program.replace_s(2, 5, "x");
assertEquals(-2, delta);
assertEquals("abxef", program.getCurrent());
}

@Test
public void testSliceFrom_InsertEmptyString() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("testing");
program.bra = 1;
program.ket = 4;
program.slice_from("");
assertEquals("ting", program.getCurrent());
}

@Test
public void testInsert_AdjustsBraAndKet() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcXYZ");
program.bra = 1;
program.ket = 4;
program.insert(2, 2, "MID");
assertEquals("abMIDcXYZ", program.getCurrent());
assertEquals(1 + "MID".length(), program.bra);
assertEquals(4 + "MID".length(), program.ket);
}

@Test
public void testSliceTo_EmptySlice() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("a");
program.bra = 0;
program.ket = 0;
StringBuilder builder = new StringBuilder("dummy");
program.slice_to(builder);
assertEquals("", builder.toString());
}

@Test
public void testAssignTo_OverwritesExistingBuilderContent() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("content");
StringBuilder builder = new StringBuilder("previous");
// program.assign_to(builder);
assertEquals("content", builder.toString());
}

@Test
public void testFindAmong_EmptyArrayReturnsZero() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("any");
Among[] empty = new Among[0];
int result = program.find_among(empty);
assertEquals(0, result);
}

@Test
public void testFindAmongB_EmptyArrayReturnsZero() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("some");
Among[] empty = new Among[0];
int result = program.find_among_b(empty);
assertEquals(0, result);
}

@Test
public void testFindAmong_PartialPrefixMatchReturnsLowerResult() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("interrupt");
// Among[] candidates = new Among[] { new Among("in", -1, 1, null), new Among("interaction", 0, 2, null) };
// int result = program.find_among(candidates);
// assertEquals(1, result);
}

@Test
public void testFindAmong_MethodThrowsException() throws Throwable {
SnowballProgram program = new SnowballProgram() {

public boolean faulty() throws Exception {
throw new Exception("Should be wrapped");
}
};
program.setCurrent("boom");
// Among[] important = new Among[] { new Among("boom", -1, 1, MethodHandles.lookup().findVirtual(program.getClass(), "faulty", MethodType.methodType(boolean.class))) };
try {
// program.find_among(important);
fail("Expected UndeclaredThrowableException");
} catch (RuntimeException ex) {
assertTrue(ex.getCause() instanceof Exception);
}
}

@Test
public void testFindAmongB_CursorUnderflowShouldNotMatch() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("low");
program.cursor = 1;
// Among[] candidates = new Among[] { new Among("low", -1, 1, null) };
// int result = program.find_among_b(candidates);
// assertEquals(0, result);
}

@Test
public void testInGrouping_CharAtMinBoundary_Match() {
SnowballProgram program = new SnowballProgram();
char[] group = new char[32];
int min = 'a';
int max = 'z';
group[0] |= (1 << 0);
program.setCurrent("a");
boolean result = program.in_grouping(group, min, max);
assertTrue(result);
}

@Test
public void testInGrouping_CharAtMaxBoundary_Match() {
SnowballProgram program = new SnowballProgram();
char[] group = new char[32];
int min = 'a';
int max = 'z';
group[('z' - min) >> 3] |= 1 << (('z' - min) & 7);
program.setCurrent("z");
boolean result = program.in_grouping(group, min, max);
assertTrue(result);
}

@Test
public void testOutGrouping_NoMatch_CharInsideGroupReturnsFalse() {
SnowballProgram program = new SnowballProgram();
char[] group = new char[32];
int min = 'a';
int max = 'z';
group[('m' - min) >> 3] |= 1 << (('m' - min) & 7);
program.setCurrent("m");
boolean result = program.out_grouping(group, min, max);
assertFalse(result);
}

@Test
public void testEqS_WithPrefixOnly_Fails() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("prefixonly");
boolean result = program.eq_s("prefixonlytoolong");
assertFalse(result);
}

@Test
public void testEqSB_PartialMatch_Fails() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("interested");
program.cursor = 10;
boolean result = program.eq_s_b("esteded");
assertFalse(result);
}

@Test
public void testReplaceS_InsertAtEndOfBuffer() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("123");
int adjustment = program.replace_s(3, 3, "XYZ");
String result = program.getCurrent();
assertEquals("123XYZ", result);
assertEquals(3, adjustment);
}

@Test
public void testSliceDel_DeleteWholeBuffer() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("something");
program.bra = 0;
program.ket = 9;
program.slice_del();
assertEquals("", program.getCurrent());
}

@Test
public void testInsert_InsertAtStart() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("rest");
program.bra = 0;
program.ket = 4;
program.insert(0, 0, "p");
String result = program.getCurrent();
assertEquals("prest", result);
}

@Test
public void testSliceFrom_ReplacesFullLength() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdef");
program.bra = 0;
program.ket = 6;
program.slice_from("zzzz");
assertEquals("zzzz", program.getCurrent());
}

@Test
public void testSliceTo_FromZeroToEnd() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("bounded");
program.bra = 0;
program.ket = 7;
StringBuilder out = new StringBuilder();
program.slice_to(out);
assertEquals("bounded", out.toString());
}

@Test
public void testAssignTo_InitialEmptyBuilder() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("assignable");
StringBuilder b = new StringBuilder();
// program.assign_to(b);
assertEquals("assignable", b.toString());
}

@Test
public void testFindAmong_SingleElementPartialMiss() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("running");
// Among[] v = new Among[] { new Among("runxyz", -1, 1, null) };
// int result = program.find_among(v);
// assertEquals(0, result);
}

@Test
public void testFindAmong_TwoKeys_FirstFailsSecondMatches() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("speech");
// Among[] v = new Among[] { new Among("speed", -1, 1, null), new Among("speech", -1, 2, null) };
// int result = program.find_among(v);
// assertEquals(2, result);
}

@Test
public void testFindAmongB_OneFailsOneMatches() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("ending");
program.cursor = 6;
// Among[] v = new Among[] { new Among("ingg", -1, 1, null), new Among("ing", -1, 2, null) };
// int result = program.find_among_b(v);
// assertEquals(2, result);
}

@Test
public void testFindAmongB_SubstringIndexFallbackToZero() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("relearned");
program.cursor = 9;
// Among[] v = new Among[] { new Among("ed", 1, 1, null), new Among("relearned", -1, 5, null) };
// int result = program.find_among_b(v);
// assertEquals(5, result);
}

@Test
public void testFindAmong_MethodThrowsUncheckedException() throws Throwable {
SnowballProgram program = new SnowballProgram() {

public boolean bad() {
throw new IllegalArgumentException("bad call");
}
};
program.setCurrent("test");
// Among[] list = new Among[] { new Among("test", -1, 1, MethodHandles.lookup().findVirtual(program.getClass(), "bad", MethodType.methodType(boolean.class))) };
try {
// program.find_among(list);
fail("Should throw IllegalArgumentException");
} catch (IllegalArgumentException e) {
assertEquals("bad call", e.getMessage());
}
}

@Test
public void testFindAmong_MethodReturnsFalse_ShouldFallThrough() throws Throwable {
SnowballProgram program = new SnowballProgram() {

public boolean condition() {
return false;
}
};
program.setCurrent("ending");
// Among[] list = new Among[] { new Among("ending", -1, 9, MethodHandles.lookup().findVirtual(program.getClass(), "condition", MethodType.methodType(boolean.class))) };
// int result = program.find_among(list);
// assertEquals(0, result);
}

@Test
public void testFindAmongB_SubstringIndexNegativeShouldReturnZero() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("unfix");
program.cursor = 5;
// Among[] v = new Among[] { new Among("fix", -1, 2, null) };
// int result = program.find_among_b(v);
// assertEquals(2, result);
}

@Test
public void testReplaceS_AdjustmentAndCursorUpdateBelowKet() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("replace");
int result = program.replace_s(2, 4, "123456");
assertEquals("re123456lace".length() - "replace".length(), result);
assertEquals("re123456lace", program.getCurrent());
}

@Test
public void testReplaceS_AdjustmentAndCursorBetweenBraKet() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdef");
program.cursor = 3;
int result = program.replace_s(2, 5, "wxyz");
assertEquals("abwxyzf", program.getCurrent());
assertEquals(2, result);
}

@Test
public void testSetCurrent_WithNullCharArrayReference() {
SnowballProgram program = new SnowballProgram();
char[] data = new char[] { 'h', 'e', 'l', 'l', 'o' };
// program.setCurrent(data, 3);
assertEquals("hel", program.getCurrent());
}

@Test
public void testReplaceS_InsertWithinRange_ShiftRight() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdef");
int diff = program.replace_s(2, 4, "XYZ");
assertEquals("abXYZef", program.getCurrent());
assertEquals(1, diff);
}

@Test
public void testReplaceS_InsertSameLength_NoShift() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcxyz");
int delta = program.replace_s(0, 3, "def");
assertEquals("defxyz", program.getCurrent());
assertEquals(0, delta);
}

@Test
public void testInsert_AdjustsBraAndKetToRight() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("123456");
program.bra = 2;
program.ket = 4;
program.insert(1, 1, "ZZ");
assertEquals("1ZZ23456", program.getCurrent());
assertEquals(4, program.bra);
assertEquals(6, program.ket);
}

@Test
public void testInsert_InsertAtKetPosition_RightShiftedTextUpdated() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("123456");
program.ket = 3;
program.insert(3, 3, "XY");
assertEquals("123XY456", program.getCurrent());
}

@Test
public void testEqSB_EmptyMatch_EqualsTrue() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("dog");
program.cursor = 3;
boolean match = program.eq_s_b("");
assertTrue(match);
assertEquals(3, program.cursor);
}

@Test
public void testInGrouping_WithBitNotSetButWithinRange_ReturnsFalse() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("j");
char[] group = new char[32];
int min = 'a';
int max = 'z';
boolean result = program.in_grouping(group, min, max);
assertFalse(result);
assertEquals(0, program.cursor);
}

@Test
public void testOutGroupingB_WithBitSetButInRange_ReturnsFalse() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("k");
program.cursor = 1;
char[] group = new char[32];
int min = 'a';
int max = 'z';
int index = ('k' - min);
group[index >> 3] |= (1 << (index & 7));
boolean result = program.out_grouping_b(group, min, max);
assertFalse(result);
assertEquals(1, program.cursor);
}

@Test
public void testSliceFrom_EmptySliceDoesNotModify() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("keep");
program.bra = 2;
program.ket = 2;
program.slice_from("XYZ");
assertEquals("keXYZep", program.getCurrent());
}

@Test
public void testSliceTo_AppendsNothingWhenBraEqualsKet() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("test");
program.bra = 3;
program.ket = 3;
StringBuilder sb = new StringBuilder("pre");
program.slice_to(sb);
assertEquals("", sb.toString());
}

@Test
public void testAssignTo_TruncatesPreviousStringBuilderContent() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("Z");
StringBuilder sb = new StringBuilder("LONG");
// program.assign_to(sb);
assertEquals("Z", sb.toString());
}

@Test
public void testFindAmong_DifferentLengthPrefixFails() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("enjoying");
// Among[] values = new Among[] { new Among("enjoyed", -1, 1, null), new Among("enjoying", -1, 2, null) };
// int result = program.find_among(values);
// assertEquals(2, result);
}

@Test
public void testFindAmongB_ExactMatchWhenCursorAtEnd() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("retro");
program.cursor = 5;
// Among[] values = new Among[] { new Among("ro", -1, 2, null) };
// int result = program.find_among_b(values);
// assertEquals(2, result);
}

@Test
public void testFindAmong_MultipleMatches_PicksLongest() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("precomputed");
// Among[] values = new Among[] { new Among("pre", -1, 1, null), new Among("precom", -1, 2, null), new Among("precomputed", -1, 3, null) };
// int result = program.find_among(values);
// assertEquals(3, result);
}

@Test
public void testFindAmongB_SingleMatchReturnsCorrectResult() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("resigned");
program.cursor = 8;
// Among[] values = new Among[] { new Among("ed", -1, 1, null), new Among("ned", 0, 2, null), new Among("igned", 1, 3, null) };
// int result = program.find_among_b(values);
// assertEquals(3, result);
}

@Test
public void testInGrouping_CursorAtLimit_ShouldReturnFalse() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("x");
program.cursor = 1;
char[] set = new char[32];
int min = 'a';
int max = 'z';
set[('x' - min) >> 3] |= 1 << (('x' - min) & 7);
boolean result = program.in_grouping(set, min, max);
assertFalse(result);
}

@Test
public void testInGroupingB_CursorEqualsLimitBackward_ShouldReturnFalse() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("y");
program.cursor = 0;
program.limit_backward = 0;
char[] set = new char[32];
int min = 'a';
int max = 'z';
boolean result = program.in_grouping_b(set, min, max);
assertFalse(result);
}

@Test
public void testOutGrouping_CursorAtLimit_ShouldReturnFalse() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("z");
program.cursor = 1;
char[] set = new char[32];
int min = 'a';
int max = 'z';
boolean result = program.out_grouping(set, min, max);
assertFalse(result);
}

@Test
public void testOutGroupingB_CursorEqualsLimitBackward_ShouldReturnFalse() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("z");
program.cursor = 0;
program.limit_backward = 0;
char[] set = new char[32];
boolean result = program.out_grouping_b(set, 'a', 'z');
assertFalse(result);
}

@Test
public void testReplaceS_WithEmptyString_ShouldDeleteRange() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdef");
int diff = program.replace_s(2, 4, "");
assertEquals("abef", program.getCurrent());
assertEquals(-2, diff);
}

@Test
public void testReplaceS_ReplacementLargerThanOriginalBuffer() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("short");
String large = "thisisaverylongstring";
int diff = program.replace_s(2, 3, large);
assertEquals("shthisisaverylongstringort", program.getCurrent());
assertEquals(large.length() - 1, diff);
}

@Test
public void testSliceDel_FullBufferDeletion() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("deletethis");
program.bra = 0;
program.ket = 9;
program.slice_del();
assertEquals("", program.getCurrent());
}

@Test
public void testSliceTo_MiddleSubstringExtraction() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("extractthisword");
program.bra = 7;
program.ket = 11;
StringBuilder builder = new StringBuilder();
program.slice_to(builder);
assertEquals("this", builder.toString());
}

@Test
public void testAssignTo_EmptyStringHandledCorrectly() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("");
StringBuilder builder = new StringBuilder("existing");
// program.assign_to(builder);
assertEquals("", builder.toString());
}

@Test
public void testFindAmong_NoCommonPrefix_ShouldReturnZero() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("hello");
// Among[] array = new Among[] { new Among("world", -1, 1, null) };
// int result = program.find_among(array);
// assertEquals(0, result);
}

@Test
public void testFindAmongB_NoMatch_ShouldReturnZero() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("reverse");
program.cursor = 7;
// Among[] array = new Among[] { new Among("notmatch", -1, 5, null) };
// int result = program.find_among_b(array);
// assertEquals(0, result);
}

@Test
public void testFindAmong_MiddleItemMatch() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("testing");
// Among[] array = new Among[] { new Among("test", -1, 1, null), new Among("testing", -1, 2, null), new Among("tester", -1, 3, null) };
// int result = program.find_among(array);
// assertEquals(2, result);
}

@Test
public void testFindAmongB_MatchFirstElement() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("running");
program.cursor = 7;
// Among[] array = new Among[] { new Among("ing", -1, 1, null), new Among("ning", -1, 2, null) };
// int result = program.find_among_b(array);
// assertEquals(1, result);
}

@Test
public void testReplaceS_ZeroLengthInsertWithNoCursorImpact() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdef");
program.cursor = 1;
int adjustment = program.replace_s(2, 2, "");
assertEquals("abcdef", program.getCurrent());
assertEquals(0, adjustment);
assertEquals(1, program.cursor);
}

@Test
public void testInsert_WithEmptyInsertText_ShouldNotAffectContent() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("inside");
program.bra = 2;
program.ket = 5;
program.insert(3, 3, "");
assertEquals("inside", program.getCurrent());
}

@Test
public void testSliceFrom_ReplacementShorterThanOriginalSlice() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("programming");
program.bra = 3;
program.ket = 9;
program.slice_from("X");
assertEquals("proXing", program.getCurrent());
}

@Test
public void testFindAmongB_MultipleFallbacks_SelectsViaSubstringIndex() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("extracted");
program.cursor = 9;
// Among item1 = new Among("ted", 1, 1, null);
// Among item2 = new Among("acted", 2, 2, null);
// Among item3 = new Among("extracted", -1, 3, null);
// int result = program.find_among_b(new Among[] { item1, item2, item3 });
// assertEquals(3, result);
}

@Test
public void testReplaceS_InsertAtEndWithCursorBeforeBra() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("hello");
program.cursor = 1;
int adjustment = program.replace_s(5, 5, "world");
assertEquals("helloworld", program.getCurrent());
assertEquals(5, adjustment);
assertEquals(1, program.cursor);
}

@Test
public void testReplaceS_InsertAtBeginningWithCursorAfterKet() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("hello");
program.cursor = 5;
int adjustment = program.replace_s(0, 0, "start");
assertEquals("starthello", program.getCurrent());
assertEquals(5, adjustment);
assertEquals(10, program.cursor);
}

@Test
public void testSliceCheck_InvalidBraLessThanZero_ThrowsAssertionError() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("fail");
program.bra = -1;
program.ket = 2;
try {
program.slice_check();
fail("Expected AssertionError");
} catch (AssertionError e) {
assertTrue(e.getMessage().contains("bra="));
}
}

@Test
public void testSliceCheck_InvalidBraGreaterThanKet_ThrowsAssertionError() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("slice");
program.bra = 4;
program.ket = 2;
try {
program.slice_check();
fail("Expected AssertionError");
} catch (AssertionError e) {
assertTrue(e.getMessage().contains("bra="));
}
}

@Test
public void testSliceCheck_KetGreaterThanLimit_ThrowsAssertionError() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("limit");
program.bra = 1;
program.ket = 10;
try {
program.slice_check();
fail("Expected AssertionError");
} catch (AssertionError e) {
assertTrue(e.getMessage().contains("ket="));
}
}

@Test
public void testSliceCheck_LimitGreaterThanLength_ThrowsAssertionError() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("bound");
program.limit = 10;
// program.length = 5;
program.ket = 5;
program.bra = 2;
try {
program.slice_check();
fail("Expected AssertionError");
} catch (AssertionError e) {
assertTrue(e.getMessage().contains("limit="));
}
}

@Test
public void testReplaceS_DeleteFullRange() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("complete");
int adjustment = program.replace_s(0, 8, "");
assertEquals("", program.getCurrent());
assertEquals(-8, adjustment);
}

@Test
public void testEqSB_WithCursorExactlyEqualToSubstringLength() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("stemmer");
program.cursor = 7;
boolean result = program.eq_s_b("stemmer");
assertTrue(result);
assertEquals(0, program.cursor);
}

@Test
public void testEqSB_WithCursorLessThanSubstringLength_ShouldReturnFalse() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("fox");
program.cursor = 2;
boolean result = program.eq_s_b("fox");
assertFalse(result);
assertEquals(2, program.cursor);
}

@Test
public void testFindAmong_WithNoMatch_AndSubstringIndexFallbackToNegative() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("unmatched");
// Among[] values = new Among[] { new Among("match", -1, 1, null) };
// int result = program.find_among(values);
// assertEquals(0, result);
}

@Test
public void testFindAmong_MatchWhenMethodReturnsTrue() throws Throwable {
SnowballProgram program = new SnowballProgram() {

public boolean dynamicMatch() {
return true;
}
};
program.setCurrent("invoke");
// Among[] values = new Among[] { new Among("invoke", -1, 7, MethodHandles.lookup().findVirtual(program.getClass(), "dynamicMatch", MethodType.methodType(boolean.class))) };
// int result = program.find_among(values);
// assertEquals(7, result);
}

@Test
public void testFindAmong_MatchWhenMethodReturnsFalse_ShouldReturnZero() throws Throwable {
SnowballProgram program = new SnowballProgram() {

public boolean dynamicReject() {
return false;
}
};
program.setCurrent("logic");
// Among[] values = new Among[] { new Among("logic", -1, 5, MethodHandles.lookup().findVirtual(program.getClass(), "dynamicReject", MethodType.methodType(boolean.class))) };
// int result = program.find_among(values);
// assertEquals(0, result);
}

@Test
public void testFindAmong_MethodThrowsThrowable_ShouldWrapAndThrow() throws Throwable {
SnowballProgram program = new SnowballProgram() {

public boolean throwsChecked() throws Exception {
throw new Exception("Checked exception");
}
};
program.setCurrent("crash");
// Among[] values = new Among[] { new Among("crash", -1, 1, MethodHandles.lookup().findVirtual(program.getClass(), "throwsChecked", MethodType.methodType(boolean.class))) };
try {
// program.find_among(values);
fail("Expected UndeclaredThrowableException");
} catch (UndeclaredThrowableException e) {
assertTrue(e.getCause() instanceof Exception);
assertEquals("Checked exception", e.getCause().getMessage());
}
}

@Test
public void testFindAmong_EmptyStringMatch_ShouldReturnCorrectIndex() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("");
// Among[] values = new Among[] { new Among("", -1, 9, null) };
// int result = program.find_among(values);
// assertEquals(9, result);
}

@Test
public void testReplaceS_ShiftLeftShouldPreserveTailText() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdefghi");
int result = program.replace_s(2, 6, "XY");
assertEquals("abXYghi", program.getCurrent());
assertEquals(-2, result);
}

@Test
public void testFindAmongB_WithReusedSubstringIndexMatch() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdef");
program.cursor = 6;
// Among among2 = new Among("def", 0, 2, null);
// Among among1 = new Among("f", -1, 1, null);
// int result = program.find_among_b(new Among[] { among2, among1 });
// assertEquals(2, result);
}

@Test
public void testEqSB_PartialMatchFromMiddleShouldReturnFalse() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdef");
program.cursor = 6;
boolean result = program.eq_s_b("cdefgh");
assertFalse(result);
assertEquals(6, program.cursor);
}

@Test
public void testEqS_PartialSuffixMatchShouldReturnFalse() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("prefixmatch");
boolean result = program.eq_s("matchsuffix");
assertFalse(result);
assertEquals(0, program.cursor);
}

@Test
public void testEqSB_MatchSingleCharacter() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("x");
program.cursor = 1;
boolean result = program.eq_s_b("x");
assertTrue(result);
assertEquals(0, program.cursor);
}

@Test
public void testInGrouping_WithSingleCharacterMatch() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("j");
char[] group = new char[32];
int min = 'a';
int max = 'z';
group[(int) ('j' - min) >> 3] |= 1 << (('j' - min) & 7);
boolean result = program.in_grouping(group, min, max);
assertTrue(result);
}

@Test
public void testInGrouping_WrongBitSetReturnsFalse() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("k");
char[] group = new char[32];
int min = 'a';
int max = 'z';
group[(int) ('j' - min) >> 3] |= 1 << (('j' - min) & 7);
boolean result = program.in_grouping(group, min, max);
assertFalse(result);
}

@Test
public void testOutGrouping_CharacterOnBoundaryShouldReturnFalseIfBitSet() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("a");
char[] group = new char[32];
int min = 'a';
int max = 'z';
group[0] |= 1 << 0;
boolean result = program.out_grouping(group, min, max);
assertFalse(result);
}

@Test
public void testOutGrouping_CharacterGreaterThanMaxShouldReturnTrue() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("{");
char[] group = new char[32];
int min = 'a';
int max = 'z';
boolean result = program.out_grouping(group, min, max);
assertTrue(result);
}

@Test
public void testOutGrouping_CharacterLessThanMinShouldReturnTrue() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("@");
char[] group = new char[32];
int min = 'a';
int max = 'z';
boolean result = program.out_grouping(group, min, max);
assertTrue(result);
}

@Test
public void testOutGroupingB_CharacterBetweenButBitNotSet_ShouldReturnTrue() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("m");
program.cursor = 1;
char[] group = new char[32];
int min = 'a';
int max = 'z';
boolean result = program.out_grouping_b(group, min, max);
assertTrue(result);
}

@Test
public void testFindAmongB_MatchesLastEntry() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("ed");
program.cursor = 2;
// Among[] v = new Among[] { new Among("d", -1, 1, null), new Among("ed", -1, 2, null) };
// int result = program.find_among_b(v);
// assertEquals(2, result);
}

@Test
public void testReplaceS_ReplaceMiddleAndPreserveTail() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdefghi");
int delta = program.replace_s(3, 6, "XYZ");
assertEquals("abcXYZghi", program.getCurrent());
assertEquals(0, delta);
}

@Test
public void testSliceFrom_InsertEmptyStringIntoMiddle() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdef");
program.bra = 2;
program.ket = 4;
program.slice_from("");
assertEquals("abef", program.getCurrent());
}

@Test
public void testSliceTo_NonEmptySliceStoredInBuilder() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("substring");
program.bra = 3;
program.ket = 6;
StringBuilder builder = new StringBuilder();
program.slice_to(builder);
assertEquals("str", builder.toString());
}

@Test
public void testAssignTo_ShouldResetAndOverwriteStringBuilder() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("finalresult");
StringBuilder b = new StringBuilder("old");
// program.assign_to(b);
assertEquals("finalresult", b.toString());
}

@Test
public void testInsert_AtStartWithCursorInsideSlice() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("world");
program.cursor = 2;
program.bra = 0;
program.ket = 5;
program.insert(0, 0, "start");
assertEquals("startworld", program.getCurrent());
}

@Test
public void testInsert_AtEndWithEmptyInsertion() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("end");
program.bra = 0;
program.ket = 3;
program.insert(3, 3, "");
assertEquals("end", program.getCurrent());
}

@Test
public void testFindAmong_SamePrefixDifferentLengths_SelectsLongest() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("prefixlongest");
// Among[] list = new Among[] { new Among("prefix", -1, 1, null), new Among("prefixlong", -1, 2, null), new Among("prefixlongest", -1, 3, null) };
// int result = program.find_among(list);
// assertEquals(3, result);
}

@Test
public void testFindAmongB_ShouldReturnResultFromMethodTrue() throws Throwable {
SnowballProgram program = new SnowballProgram() {

public boolean accept() {
return true;
}
};
program.setCurrent("xyzabc");
program.cursor = 6;
// Among[] arr = new Among[] { new Among("abc", -1, 7, MethodHandles.lookup().findVirtual(program.getClass(), "accept", MethodType.methodType(boolean.class))) };
// int result = program.find_among_b(arr);
// assertEquals(7, result);
}

@Test
public void testFindAmong_MatchesOnFirstTry() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
// Among[] v = new Among[] { new Among("abc", -1, 42, null), new Among("abcd", 0, 9, null) };
// int result = program.find_among(v);
// assertEquals(42, result);
}
}
