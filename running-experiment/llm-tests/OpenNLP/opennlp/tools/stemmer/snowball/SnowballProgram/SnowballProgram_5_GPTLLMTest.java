package opennlp.tools.stemmer.snowball;

import org.junit.jupiter.api.Test;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.UndeclaredThrowableException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;

public class SnowballProgram_5_GPTLLMTest {

@Test
public void testSetAndGetCurrentString() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("example");
String result = program.getCurrent();
assertEquals("example", result);
}

@Test
public void testSetCurrentWithCharArray() {
SnowballProgram program = new SnowballProgram();
char[] text = new char[] { 'n', 'l', 'p' };
// program.setCurrent(text, 3);
String result = program.getCurrent();
assertEquals("nlp", result);
}

@Test
public void testGetCurrentBufferLength() {
SnowballProgram program = new SnowballProgram();
char[] input = new char[] { 'a', 'b', 'c', 'd', 'e' };
// program.setCurrent(input, 5);
// char[] buffer = program.getCurrentBuffer();
// int length = program.getCurrentBufferLength();
// assertEquals(5, length);
// assertEquals("abcde", new String(buffer, 0, length));
}

@Test
public void testCopyConstructorCopiesState() {
SnowballProgram original = new SnowballProgram();
original.setCurrent("copytest");
SnowballProgram copy = new SnowballProgram(original);
String valueFromCopy = copy.getCurrent();
assertEquals("copytest", valueFromCopy);
}

@Test
public void testCopyFromCopiesState() {
SnowballProgram source = new SnowballProgram();
source.setCurrent("source");
SnowballProgram target = new SnowballProgram();
target.copy_from(source);
String result = target.getCurrent();
assertEquals("source", result);
}

@Test
public void testInGroupingWhenCharacterMatches() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("b");
char[] group = new char[4];
group[0] = 0b00000010;
boolean result = program.in_grouping(group, 'a', 'd');
assertTrue(result);
}

@Test
public void testInGroupingWhenCharacterOutOfRange() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("z");
char[] group = new char[4];
boolean result = program.in_grouping(group, 'a', 'd');
assertFalse(result);
}

@Test
public void testInGroupingBackwardsWhenCharacterMatches() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("x");
program.cursor = 1;
char[] group = new char[4];
int offset = 'x' - 'a';
group[offset >> 3] |= (1 << (offset & 0x7));
boolean result = program.in_grouping_b(group, 'a', 'z');
assertTrue(result);
}

@Test
public void testOutGroupingWhenCharacterDoesNotMatch() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("d");
char[] group = new char[4];
group[0] = 0b00000010;
boolean result = program.out_grouping(group, 'a', 'd');
assertTrue(result);
}

@Test
public void testEqSMatchesPrefix() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("prefixmatch");
boolean matched = program.eq_s("prefix");
assertTrue(matched);
assertEquals(6, program.cursor);
}

@Test
public void testEqSDoesNotMatch() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("mismatch");
boolean matched = program.eq_s("match");
assertFalse(matched);
assertEquals(0, program.cursor);
}

@Test
public void testEqSBackwardsMatch() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("ending");
program.cursor = 6;
boolean matched = program.eq_s_b("ing");
assertTrue(matched);
assertEquals(3, program.cursor);
}

@Test
public void testReplaceSWithShorterReplacement() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdefg");
int adjustment = program.replace_s(2, 5, "XY");
String result = program.getCurrent();
assertEquals("abXYfg", result);
assertEquals(-1, adjustment);
}

@Test
public void testReplaceSWithLongerReplacement() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
int adjustment = program.replace_s(1, 2, "12345");
String result = program.getCurrent();
assertEquals("a12345c", result);
assertEquals(4, adjustment);
}

@Test
public void testSliceFromReplacesSubstring() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdef");
program.bra = 2;
program.ket = 5;
program.slice_from("XY");
String result = program.getCurrent();
assertEquals("abXYf", result);
}

@Test
public void testSliceDelRemovesSubstring() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdef");
program.bra = 1;
program.ket = 4;
program.slice_del();
String result = program.getCurrent();
assertEquals("aef", result);
}

@Test
public void testInsertTextAtPosition() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcde");
program.bra = 1;
program.ket = 3;
program.insert(2, 4, "XY");
String result = program.getCurrent();
assertEquals("abcXYe", result);
}

@Test
public void testSliceToCopiesSelectedSubstring() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("linguistics");
program.bra = 1;
program.ket = 5;
StringBuilder sb = new StringBuilder();
program.slice_to(sb);
assertEquals("ingu", sb.toString());
}

@Test
public void testAssignToCopiesEntireBuffer() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("ontology");
StringBuilder sb = new StringBuilder("xyz");
// program.assign_to(sb);
assertEquals("ontology", sb.toString());
}

@Test
public void testFindAmongMatchesWithoutMethod() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("stem");
// Among[] amongs = new Among[] { new Among("stem", -1, 101, null) };
// int result = program.find_among(amongs);
// assertEquals(101, result);
}

@Test
public void testFindAmongBackwardsMatchesWithoutMethod() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("ing");
program.cursor = 3;
// Among[] amongs = new Among[] { new Among("ing", -1, 77, null) };
// int result = program.find_among_b(amongs);
// assertEquals(77, result);
}

@Test
public void testFindAmongWithNonNullMethod() throws Throwable {
// StubProgram program = new StubProgram();
// program.setCurrent("abc");
// MethodHandle methodHandle = MethodHandles.lookup().findVirtual(StubProgram.class, "condition", MethodType.methodType(boolean.class));
// Among[] amongs = new Among[] { new Among("abc", -1, 55, methodHandle) };
// int result = program.find_among(amongs);
// assertEquals(55, result);
}

@Test
public void testFindAmongBackwardsWithNonNullMethod() throws Throwable {
// StubProgram program = new StubProgram();
// program.setCurrent("xyz");
// program.cursor = 3;
// MethodHandle methodHandle = MethodHandles.lookup().findVirtual(StubProgram.class, "condition", MethodType.methodType(boolean.class));
// Among[] amongs = new Among[] { new Among("xyz", -1, 99, methodHandle) };
// int result = program.find_among_b(amongs);
// assertEquals(99, result);
}

@Test
public void testInGroupingCursorAtLimit() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("a");
char[] group = new char[1];
group[0] = 0b00000001;
program.cursor = 1;
boolean result = program.in_grouping(group, 'a', 'z');
assertFalse(result);
}

@Test
public void testInGroupingBAtLimitBackward() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("a");
char[] group = new char[1];
group[0] = 0b00000001;
program.cursor = 0;
boolean result = program.in_grouping_b(group, 'a', 'z');
assertFalse(result);
}

@Test
public void testOutGroupingWhenMatchCharacter() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("b");
char[] group = new char[2];
int offset = 'b' - 'a';
group[offset >> 3] |= (1 << (offset & 0x7));
boolean result = program.out_grouping(group, 'a', 'c');
assertFalse(result);
}

@Test
public void testOutGroupingBWhenMatchCharacter() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("c");
char[] group = new char[2];
int offset = 'c' - 'a';
group[offset >> 3] |= (1 << (offset & 0x7));
program.cursor = 1;
boolean result = program.out_grouping_b(group, 'a', 'd');
assertFalse(result);
}

@Test
public void testReplaceSWithFullDeletion() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("xyz");
int adjustment = program.replace_s(0, 3, "");
assertEquals("", program.getCurrent());
assertEquals(-3, adjustment);
}

@Test
public void testSliceToWithEmptySlice() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("stem");
program.bra = 2;
program.ket = 2;
StringBuilder sb = new StringBuilder("init");
program.slice_to(sb);
assertEquals("", sb.toString());
}

@Test
public void testSliceCheckAssertionFailures() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
program.bra = 3;
program.ket = 2;
try {
program.slice_check();
fail("Expected AssertionError due to bra > ket");
} catch (AssertionError e) {
assertTrue(e.getMessage().contains("bra="));
}
}

@Test
public void testFindAmongNoMatch() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("nomatch");
// Among[] amongs = new Among[] { new Among("abc", -1, 1, null), new Among("xyz", -1, 2, null) };
// int result = program.find_among(amongs);
// assertEquals(0, result);
}

@Test
public void testFindAmongBNoMatch() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("nomatch");
program.cursor = 7;
// Among[] amongs = new Among[] { new Among("abc", -1, 1, null), new Among("xyz", -1, 2, null) };
// int result = program.find_among_b(amongs);
// assertEquals(0, result);
}

@Test
public void testFindAmongInvokesMethodFalse() throws Throwable {
// StubProgram program = new StubProgram(false);
// program.setCurrent("abc");
// MethodHandle mh = MethodHandles.lookup().findVirtual(StubProgram.class, "returnFlag", MethodType.methodType(boolean.class));
// Among[] amongs = new Among[] { new Among("abc", -1, 99, mh) };
// int result = program.find_among(amongs);
// assertEquals(0, result);
}

@Test
public void testFindAmongBInvokesMethodFalse() throws Throwable {
// StubProgram program = new StubProgram(false);
// program.setCurrent("abc");
// program.cursor = 3;
// MethodHandle mh = MethodHandles.lookup().findVirtual(StubProgram.class, "returnFlag", MethodType.methodType(boolean.class));
// Among[] amongs = new Among[] { new Among("abc", -1, 45, mh) };
// int result = program.find_among_b(amongs);
// assertEquals(0, result);
}

@Test
public void testReplaceSDoesNotChangeWhenReplacingWithSameLength() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
int delta = program.replace_s(0, 3, "xyz");
String result = program.getCurrent();
assertEquals("xyz", result);
assertEquals(0, delta);
}

@Test
public void testInsertBeforeBraKetAdjustsBraAndKet() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcde");
program.bra = 2;
program.ket = 4;
program.insert(1, 2, "XY");
assertEquals("aXYbcde", program.getCurrent());
assertEquals(4, program.bra);
assertEquals(6, program.ket);
}

@Test
public void testInsertAfterKetLeavesPositionUnchanged() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcde");
program.bra = 1;
program.ket = 2;
program.insert(4, 5, "XY");
assertEquals("abcdXYe", program.getCurrent());
assertEquals(1, program.bra);
assertEquals(2, program.ket);
}

@Test
public void testEqSWithEmptyString() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("");
boolean result = program.eq_s("");
assertTrue(result);
assertEquals(0, program.cursor);
}

@Test
public void testEqSBWithEmptyString() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
program.cursor = 3;
boolean result = program.eq_s_b("");
assertTrue(result);
assertEquals(3, program.cursor);
}

@Test
public void testFindAmongWithCommonPrefixButConditionFalse() throws Throwable {
SnowballProgram program = new SnowballProgram() {

public boolean testCondition() {
return false;
}
};
MethodHandle handle = MethodHandles.lookup().findVirtual(program.getClass(), "testCondition", MethodType.methodType(boolean.class));
program.setCurrent("abstract");
// Among[] amongs = new Among[] { new Among("abs", 0, 111, handle), new Among("abstain", -1, 222, null) };
// int result = program.find_among(amongs);
// assertEquals(0, result);
}

@Test
public void testFindAmongMatchesSecondCandidateWithBacktracking() throws Throwable {
SnowballProgram program = new SnowballProgram();
program.setCurrent("stemming");
// Among[] amongs = new Among[] { new Among("stem", 1, 42, null), new Among("stemming", -1, 99, null) };
// int result = program.find_among(amongs);
// assertEquals(99, result);
}

@Test
public void testReplaceSGrowsBufferBeyondOriginalCapacity() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("a");
StringBuilder longReplacement = new StringBuilder();
longReplacement.append("longstring123456789012345678901234567890XYZ");
// int delta = program.replace_s(0, 1, longReplacement);
// assertTrue(delta > 0);
assertEquals(longReplacement.toString(), program.getCurrent());
}

@Test
public void testReplaceSAdjustsCursorWhenInsideRange() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcde");
program.cursor = 3;
int delta = program.replace_s(1, 4, "123456");
assertEquals(1, program.cursor);
assertEquals("a123456e", program.getCurrent());
}

@Test
public void testReplaceSDoesNotMoveCursorWhenAfterKet() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdefg");
program.cursor = 6;
int delta = program.replace_s(2, 5, "xyz");
assertEquals(6 + delta, program.cursor);
assertEquals("abxyzfg", program.getCurrent());
}

@Test
public void testInsertTextBeforeBraAndKetAtStart() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("xyz");
program.bra = 0;
program.ket = 0;
program.insert(0, 0, "123");
assertEquals("123xyz", program.getCurrent());
assertEquals(3, program.bra);
assertEquals(3, program.ket);
}

@Test
public void testSliceDelWithZeroLength() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
program.bra = 1;
program.ket = 1;
program.slice_del();
assertEquals("abc", program.getCurrent());
}

@Test
public void testSliceCheckFailsOnLimitGreaterThanLength() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
program.bra = 1;
program.ket = 2;
SnowballProgram broken = new SnowballProgram() {

{
this.bra = 1;
this.ket = 2;
this.limit = 10;
// this.length = 3;
}
};
try {
broken.slice_check();
fail("Expected AssertionError due to limit > length");
} catch (AssertionError e) {
assertTrue(e.getMessage().contains("limit="));
}
}

@Test
public void testSliceToWithFullRange() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("complete");
program.bra = 0;
program.ket = 8;
StringBuilder sb = new StringBuilder("original");
program.slice_to(sb);
assertEquals("complete", sb.toString());
}

@Test
public void testInGroupingExactMinAndMaxBoundsIncluded() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("c");
char[] group = new char[4];
int offset = 'c' - 'c';
group[offset >> 3] |= (1 << (offset & 0x7));
boolean result = program.in_grouping(group, 'c', 'c');
assertTrue(result);
}

@Test
public void testOutGroupingCharOnBoundaryExcluded() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("d");
char[] group = new char[2];
int offset = 'd' - 'd';
group[offset >> 3] |= (1 << (offset & 0x7));
boolean result = program.out_grouping(group, 'd', 'd');
assertFalse(result);
}

@Test
public void testInGroupingBackwardsUnreachableLimitBackward() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
program.cursor = 0;
char[] group = new char[4];
boolean result = program.in_grouping_b(group, 'a', 'z');
assertFalse(result);
}

@Test
public void testFindAmongEmptyArrayReturnsZero() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("anything");
Among[] amongs = new Among[] {};
int result = program.find_among(amongs);
assertEquals(0, result);
}

@Test
public void testFindAmongBEmptyArrayReturnsZero() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("reverse");
program.cursor = 7;
Among[] amongs = new Among[] {};
int result = program.find_among_b(amongs);
assertEquals(0, result);
}

@Test
public void testReplaceSWithNoAdjustment() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
int delta = program.replace_s(0, 3, "abc");
assertEquals("abc", program.getCurrent());
assertEquals(0, delta);
}

@Test
public void testInsertBetweenUnrelatedPositions() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("xyz");
program.bra = 0;
program.ket = 1;
program.insert(1, 2, "123");
assertEquals("xy123z", program.getCurrent());
assertEquals(0, program.bra);
assertEquals(1, program.ket);
}

@Test
public void testSliceCheckInvalidKetGreaterThanLimit() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("toolkit");
program.bra = 2;
program.ket = 8;
try {
program.slice_check();
fail("Expected AssertionError for ket > limit");
} catch (AssertionError e) {
assertTrue(e.getMessage().contains("ket="));
}
}

@Test
public void testSliceCheckBraIsNegative() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("alpha");
program.bra = -1;
program.ket = 3;
try {
program.slice_check();
fail("Expected AssertionError for bra < 0");
} catch (AssertionError e) {
assertTrue(e.getMessage().contains("bra="));
}
}

@Test
public void testFindAmongThrowsUndeclaredThrowableException() {
SnowballProgram program = new SnowballProgram();
MethodHandle mh;
// try {
// mh = MethodHandles.lookup().findStatic(ThrowingClass.class, "throwsException", MethodType.methodType(boolean.class, SnowballProgram.class));
// } catch (NoSuchMethodException | IllegalAccessException e) {
// throw new RuntimeException(e);
// }
// Among[] amongs = new Among[] { new Among("fail", -1, 1, mh) };
program.setCurrent("fail");
try {
// program.find_among(amongs);
fail("Expected UndeclaredThrowableException");
} catch (UndeclaredThrowableException e) {
assertTrue(e.getCause() instanceof RuntimeException);
assertEquals("Expected exception", e.getCause().getMessage());
}
}

@Test
public void testGetCurrentBufferDoesNotCopyMemory() {
SnowballProgram program = new SnowballProgram();
char[] text = new char[] { 'd', 'e', 'e', 'p' };
// program.setCurrent(text, 4);
// char[] bufferRef = program.getCurrentBuffer();
// assertSame(text, bufferRef);
}

@Test
public void testInsertWithEmptyStringDoesNothing() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("insert");
program.bra = 1;
program.ket = 3;
program.insert(2, 4, "");
assertEquals("insert", program.getCurrent());
assertEquals(1, program.bra);
assertEquals(3, program.ket);
}

@Test
public void testAssignToOverwritesTargetStringBuilder() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
StringBuilder sb = new StringBuilder("This will be replaced");
// program.assign_to(sb);
assertEquals("abc", sb.toString());
}

@Test
public void testSliceToClearsBuilderBeforeAppend() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("slicedtext");
program.bra = 0;
program.ket = 6;
StringBuilder sb = new StringBuilder("should be cleared");
program.slice_to(sb);
assertEquals("sliced", sb.toString());
}

@Test
public void testEqSBackwardsNotMatchDueToOffset() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("hello");
program.cursor = 2;
boolean result = program.eq_s_b("llo");
assertFalse(result);
assertEquals(2, program.cursor);
}

@Test
public void testInGroupingCharBelowMin() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("a");
char[] group = new char[1];
boolean result = program.in_grouping(group, 'b', 'z');
assertFalse(result);
}

@Test
public void testInGroupingCharAboveMax() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("z");
char[] group = new char[1];
boolean result = program.in_grouping(group, 'a', 'y');
assertFalse(result);
}

@Test
public void testInGroupingCharWithinRangeButBitNotSet() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("c");
char[] group = new char[1];
boolean result = program.in_grouping(group, 'a', 'd');
assertFalse(result);
}

@Test
public void testInGroupingBCharBelowMin() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("a");
program.cursor = 1;
char[] group = new char[1];
boolean result = program.in_grouping_b(group, 'b', 'z');
assertFalse(result);
}

@Test
public void testOutGroupingCharWithinSet() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("c");
char[] group = new char[1];
int offset = 'c' - 'a';
group[offset >> 3] = (char) (1 << (offset & 0x7));
boolean result = program.out_grouping(group, 'a', 'z');
assertFalse(result);
}

@Test
public void testOutGroupingBCharOutsideSetAndOutOfMinMax() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("~");
program.cursor = 1;
char[] group = new char[1];
boolean result = program.out_grouping_b(group, 'a', 'z');
assertTrue(result);
}

@Test
public void testEqSShorterRemainingTextThanTerm() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("ab");
boolean result = program.eq_s("abc");
assertFalse(result);
assertEquals(0, program.cursor);
}

@Test
public void testEqSBShorterRemainingTextThanTerm() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("ab");
program.cursor = 2;
boolean result = program.eq_s_b("zab");
assertFalse(result);
assertEquals(2, program.cursor);
}

@Test
public void testReplaceSReplacesMiddlePreservesTail() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdef");
int delta = program.replace_s(2, 4, "XY");
assertEquals("abXYef", program.getCurrent());
assertEquals(0, delta);
}

@Test
public void testInsertAtEndOfWord() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
program.bra = 3;
program.ket = 3;
program.insert(3, 3, "123");
assertEquals("abc123", program.getCurrent());
}

@Test
public void testSliceFromSameContentNoChange() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("hello");
program.bra = 0;
program.ket = 5;
program.slice_from("hello");
assertEquals("hello", program.getCurrent());
}

@Test
public void testSliceFromEmptyStringOnFullRange() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("hello");
program.bra = 0;
program.ket = 5;
program.slice_from("");
assertEquals("", program.getCurrent());
}

@Test
public void testSliceDelDeletesMiddle() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abXYZcd");
program.bra = 2;
program.ket = 5;
program.slice_del();
assertEquals("abcd", program.getCurrent());
}

@Test
public void testSliceToAppendsExpectedText() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdefgh");
program.bra = 2;
program.ket = 6;
StringBuilder builder = new StringBuilder();
program.slice_to(builder);
assertEquals("cdef", builder.toString());
}

@Test
public void testAssignToClearsTargetBuilder() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("short");
StringBuilder builder = new StringBuilder("thiswillbecleared");
// program.assign_to(builder);
assertEquals("short", builder.toString());
}

@Test
public void testFindAmongMultiplePrefixSharingNoMatchFinal() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("stamina");
// Among[] amongs = new Among[] { new Among("stem", -1, 1, null), new Among("star", -1, 2, null), new Among("stack", -1, 3, null), new Among("stop", -1, 4, null) };
// int result = program.find_among(amongs);
// assertEquals(0, result);
}

@Test
public void testFindAmongBWithMultipleOptionsNoMatch() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("removal");
program.cursor = 7;
// Among[] amongs = new Among[] { new Among("ed", -1, 1, null), new Among("ing", -1, 2, null), new Among("est", -1, 3, null) };
// int result = program.find_among_b(amongs);
// assertEquals(0, result);
}

@Test
public void testInGroupingCursorAtLastChar() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("x");
char[] group = new char[4];
int offset = 'x' - 'x';
group[offset >> 3] = (char) (1 << (offset & 0x7));
boolean result = program.in_grouping(group, 'x', 'x');
assertTrue(result);
}

@Test
public void testInGroupingBCharNotInGroup() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("z");
program.cursor = 1;
char[] group = new char[2];
group[0] = 0b00000001;
boolean result = program.in_grouping_b(group, 'a', 'z');
assertFalse(result);
assertEquals(1, program.cursor);
}

@Test
public void testOutGroupingCharAtEndOfBuffer() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("m");
char[] group = new char[2];
group[0] = 0b00000000;
boolean result = program.out_grouping(group, 'm', 'm');
assertTrue(result);
}

@Test
public void testOutGroupingBCharMatchesGroup() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("c");
program.cursor = 1;
char[] group = new char[1];
int offset = 'c' - 'a';
group[offset >> 3] = (char) (1 << (offset & 0x7));
boolean result = program.out_grouping_b(group, 'a', 'z');
assertFalse(result);
}

@Test
public void testReplaceSFullReplaceShrinksBuffer() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdefgh");
int delta = program.replace_s(0, 8, "xyz");
assertEquals("xyz", program.getCurrent());
assertEquals(-5, delta);
}

@Test
public void testReplaceSInsertAtStartOfWord() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("suffix");
int delta = program.replace_s(0, 0, "pre");
assertEquals("presuffix", program.getCurrent());
assertEquals(3, delta);
}

@Test
public void testInsertBeforeEverything() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("root");
program.bra = 0;
program.ket = 0;
program.insert(0, 0, "pre");
assertEquals("preroot", program.getCurrent());
assertEquals(3, program.bra);
assertEquals(3, program.ket);
}

@Test
public void testInsertAfterEverything() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("stem");
program.bra = 4;
program.ket = 4;
program.insert(4, 4, "med");
assertEquals("stemmed", program.getCurrent());
assertEquals(7, program.bra);
assertEquals(7, program.ket);
}

@Test
public void testSliceFromToEmptyWord() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
program.bra = 0;
program.ket = 3;
program.slice_from("");
assertEquals("", program.getCurrent());
}

@Test
public void testSliceDelOnSingleChar() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("z");
program.bra = 0;
program.ket = 1;
program.slice_del();
assertEquals("", program.getCurrent());
}

@Test
public void testSliceToWithSingleCharRange() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("hello");
program.bra = 1;
program.ket = 2;
StringBuilder sb = new StringBuilder();
program.slice_to(sb);
assertEquals("e", sb.toString());
}

@Test
public void testAssignToOnLargeText() {
SnowballProgram program = new SnowballProgram();
String longText = "testingSnowballBufferCapsBeyondDefaultInitialSize";
program.setCurrent(longText);
StringBuilder sb = new StringBuilder("oldcontent1234");
// program.assign_to(sb);
assertEquals(longText, sb.toString());
}

@Test
public void testFindAmongPrefixMatchingButNoExecutes() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("preclude");
// Among[] amongs = new Among[] { new Among("pre", 0, 1, null), new Among("precursor", -1, 2, null) };
// int result = program.find_among(amongs);
// assertEquals(0, result);
}

@Test
public void testFindAmongLastEntryFallback() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("xyz");
// Among[] amongs = new Among[] { new Among("abc", 0, 1, null), new Among("def", 0, 2, null), new Among("xyz", -1, 99, null) };
// int result = program.find_among(amongs);
// assertEquals(99, result);
}

@Test
public void testFindAmongBExactSuffixMatch() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("suffix");
program.cursor = 6;
// Among[] amongs = new Among[] { new Among("fix", -1, 7, null), new Among("suffix", -1, 9, null) };
// int result = program.find_among_b(amongs);
// assertEquals(9, result);
}

@Test
public void testFindAmongBSuffixMatchConditionFalse() throws Throwable {
SnowballProgram program = new SnowballProgram() {

public boolean returnFalse() {
return false;
}
};
MethodHandle mh = MethodHandles.lookup().findVirtual(program.getClass(), "returnFalse", MethodType.methodType(boolean.class));
program.setCurrent("zoo");
program.cursor = 3;
// Among[] amongs = new Among[] { new Among("zoo", -1, 55, mh) };
// int result = program.find_among_b(amongs);
// assertEquals(0, result);
}

@Test
public void testReplaceSZeroLengthAfterStart() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdef");
int delta = program.replace_s(2, 2, "XYZ");
assertEquals("abXYZcdef", program.getCurrent());
assertEquals(3, delta);
}

@Test
public void testReplaceSEffectOnCursorWhenCursorBetweenBraAndKet() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdef");
program.cursor = 3;
int delta = program.replace_s(2, 5, "XY");
assertEquals("abXYf", program.getCurrent());
assertEquals(2, program.cursor);
assertEquals(-1, delta);
}

@Test
public void testReplaceSNoShiftWhenReplacementSameLength() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcde");
program.cursor = 5;
int delta = program.replace_s(1, 4, "XYZ");
assertEquals("aXYZe", program.getCurrent());
assertEquals(5, program.cursor);
assertEquals(0, delta);
}

@Test
public void testSliceCheckThrowsWhenBraGreaterThanKet() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
program.bra = 3;
program.ket = 2;
try {
program.slice_check();
fail("Expected AssertionError for bra > ket");
} catch (AssertionError e) {
assertTrue(e.getMessage().contains("bra="));
}
}

@Test
public void testSliceCheckThrowsWhenKetGreaterThanLimit() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("ab");
program.bra = 1;
program.ket = 3;
try {
program.slice_check();
fail("Expected AssertionError for ket > limit");
} catch (AssertionError e) {
assertTrue(e.getMessage().contains("ket="));
}
}

@Test
public void testSliceCheckThrowsWhenLimitGreaterThanLength() {
SnowballProgram program = new SnowballProgram() {

{
// current = new char[] { 'x', 'y', 'z' };
// length = 3;
limit = 5;
ket = 3;
bra = 1;
}
};
try {
program.slice_check();
fail("Expected AssertionError for limit > length");
} catch (AssertionError e) {
assertTrue(e.getMessage().contains("limit="));
}
}

@Test
public void testInGrouping_CursorBeyondLimit() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("ab");
program.cursor = 2;
char[] group = new char[1];
boolean result = program.in_grouping(group, 'a', 'z');
assertFalse(result);
}

@Test
public void testOutGroupingB_CursorEqualsLimitBackward() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("test");
program.cursor = 0;
char[] group = new char[1];
boolean result = program.out_grouping_b(group, 'a', 'z');
assertFalse(result);
}

@Test
public void testEqSBPartialMatchFails() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("testing");
program.cursor = 7;
boolean result = program.eq_s_b("sting");
assertFalse(result);
assertEquals(7, program.cursor);
}

@Test
public void testEqSBMatchWithCursorAtExactEnd() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("lion");
program.cursor = 4;
boolean result = program.eq_s_b("ion");
assertTrue(result);
assertEquals(1, program.cursor);
}

@Test
public void testEqSMatchEmptyString() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("");
boolean result = program.eq_s("");
assertTrue(result);
assertEquals(0, program.cursor);
}

@Test
public void testSliceDelWithLimitEdgeCase() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("12345");
program.bra = 4;
program.ket = 5;
program.slice_del();
assertEquals("1234", program.getCurrent());
}

@Test
public void testInsertAfterLastCharCursorUnchanged() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("a");
program.cursor = 1;
program.insert(1, 1, "z");
assertEquals("az", program.getCurrent());
assertEquals(1, program.cursor);
}

@Test
public void testFindAmongWithTwoSimilarPrefixes() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("starting");
// Among[] amongs = new Among[] { new Among("start", -1, 1, null), new Among("starting", -1, 2, null) };
// int result = program.find_among(amongs);
// assertEquals(2, result);
}

@Test
public void testFindAmongWithEqualPrefixNoMatchFallthrough() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("starter");
// Among[] amongs = new Among[] { new Among("start", 0, 1, null), new Among("starting", 0, 2, null) };
// int result = program.find_among(amongs);
// assertEquals(0, result);
}

@Test
public void testFindAmongBinarySearchOneEntry() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("unstem");
// Among[] amongs = new Among[] { new Among("unstem", -1, 5, null) };
// int result = program.find_among(amongs);
// assertEquals(5, result);
}
}
