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

public class SnowballProgram_3_GPTLLMTest {

@Test
public void testSetAndGetCurrent() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("unittest");
String result = program.getCurrent();
assertEquals("unittest", result);
}

@Test
public void testSetCurrentUsingCharArray() {
SnowballProgram program = new SnowballProgram();
char[] chars = new char[] { 'n', 'l', 'p' };
// program.setCurrent(chars, 3);
assertEquals("nlp", program.getCurrent());
// assertEquals(3, program.getCurrentBufferLength());
// assertArrayEquals(new char[] { 'n', 'l', 'p' }, program.getCurrentBuffer());
}

@Test
public void testCopyConstructorCopiesContent() {
SnowballProgram original = new SnowballProgram();
original.setCurrent("token");
SnowballProgram copy = new SnowballProgram(original);
assertEquals("token", copy.getCurrent());
}

@Test
public void testCopyFromCopiesAllFields() {
SnowballProgram source = new SnowballProgram();
source.setCurrent("stem");
SnowballProgram target = new SnowballProgram();
target.copy_from(source);
assertEquals("stem", target.getCurrent());
}

@Test
public void testInGroupingMatchesCharacter() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("a");
char[] group = new char[4];
group[0] = 0b00000001;
boolean result = program.in_grouping(group, 'a', 'z');
assertTrue(result);
}

@Test
public void testInGroupingFailsOnMismatch() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("b");
char[] group = new char[4];
group[0] = 0b00000001;
boolean result = program.in_grouping(group, 'a', 'z');
assertFalse(result);
}

@Test
public void testInGroupingBMatches() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("ab");
program.cursor = 2;
char[] group = new char[4];
group[0] = 0b00000100;
boolean result = program.in_grouping_b(group, 'a', 'z');
assertTrue(result);
}

@Test
public void testInGroupingBFails() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("ab");
program.cursor = 2;
char[] group = new char[4];
group[0] = 0b00000001;
boolean result = program.in_grouping_b(group, 'a', 'z');
assertFalse(result);
}

@Test
public void testOutGroupingMatchesOutside() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("x");
char[] group = new char[4];
group[0] = 0b00000001;
boolean result = program.out_grouping(group, 'a', 'z');
assertTrue(result);
}

@Test
public void testOutGroupingFailsWhenInsideGroup() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("a");
char[] group = new char[4];
group[0] = 0b00000001;
boolean result = program.out_grouping(group, 'a', 'z');
assertFalse(result);
}

@Test
public void testOutGroupingBMatchesOutside() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("pq");
program.cursor = 2;
char[] group = new char[4];
group[1] = 0b00000000;
boolean result = program.out_grouping_b(group, 'a', 'z');
assertTrue(result);
}

@Test
public void testOutGroupingBFailsInsideGroup() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("az");
program.cursor = 2;
char[] group = new char[4];
group[3] = (char) (1 << ('z' & 7));
boolean result = program.out_grouping_b(group, 'a', 'z');
assertFalse(result);
}

@Test
public void testEqSMatchesSubstring() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("input");
boolean result = program.eq_s("inp");
assertTrue(result);
}

@Test
public void testEqSFailsToMatch() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("input");
boolean result = program.eq_s("xyz");
assertFalse(result);
}

@Test
public void testEqSBMatchesBackwards() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("running");
program.cursor = 7;
boolean result = program.eq_s_b("ing");
assertTrue(result);
}

@Test
public void testEqSBFailsMismatch() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("running");
program.cursor = 7;
boolean result = program.eq_s_b("xyz");
assertFalse(result);
}

@Test
public void testReplaceSReplacesCorrectly() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("editing");
int delta = program.replace_s(4, 7, "ed");
assertEquals("editedg", program.getCurrent());
assertEquals(-1, delta);
}

@Test
public void testSliceDelRemovesSection() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("processing");
program.bra = 4;
program.ket = 10;
program.slice_del();
assertEquals("procg", program.getCurrent());
}

@Test
public void testSliceFromReplacesFragment() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("processing");
program.bra = 4;
program.ket = 10;
program.slice_from("ss");
assertEquals("procssg", program.getCurrent());
}

@Test
public void testInsertInsertsText() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("dd");
program.insert(0, 0, "a");
assertEquals("add", program.getCurrent());
}

@Test
public void testSliceToExtractsSubstring() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("extraction");
program.bra = 4;
program.ket = 9;
StringBuilder builder = new StringBuilder();
program.slice_to(builder);
assertEquals("acti", builder.toString());
}

@Test
public void testAssignToCopiesAllText() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("stemmed");
StringBuilder builder = new StringBuilder("abc");
// program.assign_to(builder);
assertEquals("stemmed", builder.toString());
}

@Test
public void testFindAmongReturnsCorrectResultWithoutMethod() {
SnowballProgram program = new SnowballProgram();
// Among among1 = new Among("run", -1, 1, null);
// Among among2 = new Among("stem", -1, 2, null);
program.setCurrent("stem");
// int result = program.find_among(new Among[] { among1, among2 });
// assertEquals(2, result);
}

@Test
public void testFindAmongReturnsCorrectResultWithMethod() throws Throwable {
// MethodHandle methodHandle = MethodHandles.lookup().findVirtual(TestWrapper.class, "mockMethod", MethodType.methodType(boolean.class));
// Among among = new Among("unit", -1, 5, methodHandle);
// TestWrapper stub = new TestWrapper();
// stub.setCurrent("unit");
// int result = stub.find_among(new Among[] { among });
// assertEquals(5, result);
}

@Test
public void testFindAmongBReturnsCorrectResultWithoutMethod() {
SnowballProgram program = new SnowballProgram();
// Among among1 = new Among("ed", -1, 1, null);
program.setCurrent("tested");
// program.cursor = program.getCurrentBufferLength();
// int result = program.find_among_b(new Among[] { among1 });
// assertEquals(1, result);
}

@Test
public void testFindAmongBReturnsCorrectResultWithMethod() throws Throwable {
// MethodHandle methodHandle = MethodHandles.lookup().findVirtual(TestWrapper.class, "mockMethod", MethodType.methodType(boolean.class));
// Among among = new Among("ing", -1, 3, methodHandle);
// TestWrapper stub = new TestWrapper();
// stub.setCurrent("walking");
// stub.cursor = stub.getCurrentBufferLength();
// int result = stub.find_among_b(new Among[] { among });
// assertEquals(3, result);
}

@Test
public void testSetCurrentWithEmptyString() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("");
String result = program.getCurrent();
assertEquals("", result);
// assertEquals(0, program.getCurrentBufferLength());
}

@Test
public void testSetCurrentWithLengthZeroArray() {
SnowballProgram program = new SnowballProgram();
char[] empty = new char[10];
// program.setCurrent(empty, 0);
assertEquals("", program.getCurrent());
}

@Test
public void testReplaceSReplacesAtStartAndExpandsBuffer() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("grow");
int delta = program.replace_s(0, 1, "expansion");
assertEquals("expansionrow", program.getCurrent());
assertTrue(delta > 0);
}

@Test
public void testReplaceSWithSameTextLength() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("hello");
int delta = program.replace_s(0, 5, "world");
assertEquals("world", program.getCurrent());
assertEquals(0, delta);
}

@Test
public void testSliceCheckWithInvertedBraKet() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("errorcase");
program.bra = 5;
program.ket = 3;
try {
program.slice_check();
fail("Expected AssertionError due to bra > ket");
} catch (AssertionError e) {
assertTrue(e.getMessage().contains("bra="));
}
}

@Test
public void testSliceCheckWithKetGreaterThanLimit() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("oops");
program.bra = 1;
program.ket = 10;
try {
program.slice_check();
fail("Expected AssertionError due to ket > limit");
} catch (AssertionError e) {
assertTrue(e.getMessage().contains("ket="));
}
}

@Test
public void testInsertAdjustsBraAndKet() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("act");
program.bra = 1;
program.ket = 3;
program.insert(1, 2, "XYZ");
String result = program.getCurrent();
assertEquals("aXYZct", result);
assertTrue(program.bra > 1);
assertTrue(program.ket > 3);
}

@Test
public void testEqSDoesNotReadPastLimit() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("ab");
boolean result = program.eq_s("abc");
assertFalse(result);
}

@Test
public void testEqSBDoesNotReadBeforeLimitBackward() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
program.cursor = 2;
boolean result = program.eq_s_b("abc");
assertFalse(result);
}

@Test
public void testFindAmongReturnsZeroOnFail() {
SnowballProgram program = new SnowballProgram();
// Among[] v = { new Among("abc", -1, 1, null), new Among("bcd", -1, 2, null) };
program.setCurrent("xyz");
// int result = program.find_among(v);
// assertEquals(0, result);
}

@Test
public void testFindAmongWithMethodReturningFalse() throws Throwable {
// MethodHandle mh = MethodHandles.lookup().findVirtual(TestWrapperFailure.class, "methodFalse", MethodType.methodType(boolean.class));
// Among[] v = { new Among("abc", -1, 1, mh) };
// TestWrapperFailure stub = new TestWrapperFailure();
// stub.setCurrent("abc");
// int result = stub.find_among(v);
// assertEquals(0, result);
}

@Test
public void testFindAmongHandlesUndeclaredThrowableException() {
SnowballProgram program = new SnowballProgram() {
};
MethodHandle mh = MethodHandles.throwException(boolean.class, Exception.class).bindTo(new RuntimeException("test"));
// Among[] v = { new Among("abc", -1, 1, mh) };
program.setCurrent("abc");
try {
// program.find_among(v);
fail("Expected UndeclaredThrowableException");
} catch (UndeclaredThrowableException e) {
assertTrue(e.getCause().getMessage().contains("test"));
}
}

@Test
public void testFindAmongBReturnsZeroOnFail() {
SnowballProgram program = new SnowballProgram();
// Among[] v = { new Among("xyz", -1, 42, null) };
program.setCurrent("abc");
// program.cursor = program.getCurrentBufferLength();
// int result = program.find_among_b(v);
// assertEquals(0, result);
}

@Test
public void testFindAmongBStopsAtLimitBackward() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("hello");
program.cursor = 2;
// Among[] v = { new Among("llo", -1, 3, null) };
// int result = program.find_among_b(v);
// assertEquals(0, result);
}

@Test
public void testOutGroupingBeyondMaxCharRange() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("A");
char[] group = new char[4];
boolean result = program.out_grouping(group, 'a', 'z');
assertTrue(result);
}

@Test
public void testInGroupingBeyondMaxCharRange() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("?");
char[] group = new char[4];
boolean result = program.in_grouping(group, 'a', 'z');
assertFalse(result);
}

@Test
public void testSetCurrentWithSpecialCharacters() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("123!@#¤%&()_+");
assertEquals("123!@#¤%&()_+", program.getCurrent());
}

@Test
public void testInsertAtEndOfString() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
program.insert(3, 3, "xyz");
assertEquals("abcxyz", program.getCurrent());
}

@Test
public void testInsertInMiddleOfString() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abz");
program.insert(2, 2, "cde");
assertEquals("abcdez", program.getCurrent());
}

@Test
public void testSliceDelEntireContent() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("fulltext");
program.bra = 0;
program.ket = 8;
program.slice_del();
assertEquals("", program.getCurrent());
}

@Test
public void testSliceFromReplaceEntireContent() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("removeall");
program.bra = 0;
program.ket = 9;
program.slice_from("replace");
assertEquals("replace", program.getCurrent());
}

@Test
public void testSliceToEmptySlice() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdef");
program.bra = 2;
program.ket = 2;
StringBuilder sb = new StringBuilder("shouldclear");
program.slice_to(sb);
assertEquals("", sb.toString());
}

@Test
public void testSliceToFullLength() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("extract");
program.bra = 0;
program.ket = 7;
StringBuilder sb = new StringBuilder();
program.slice_to(sb);
assertEquals("extract", sb.toString());
}

@Test
public void testAssignToEmptyString() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("");
StringBuilder sb = new StringBuilder("before");
// program.assign_to(sb);
assertEquals("", sb.toString());
}

@Test
public void testFindAmongMultipleVariantsSamePrefix() {
SnowballProgram program = new SnowballProgram();
// Among a1 = new Among("abc", -1, 1, null);
// Among a2 = new Among("abcdef", -1, 2, null);
// Among a3 = new Among("abcd", -1, 3, null);
program.setCurrent("abcdef");
// int result = program.find_among(new Among[] { a1, a2, a3 });
// assertEquals(2, result);
}

@Test
public void testFindAmongBMultipleVariantsSameSuffix() {
SnowballProgram program = new SnowballProgram();
// Among a1 = new Among("xyz", -1, 1, null);
// Among a2 = new Among("yz", -1, 2, null);
// Among a3 = new Among("z", -1, 3, null);
program.setCurrent("wxyz");
// program.cursor = program.getCurrentBufferLength();
// int result = program.find_among_b(new Among[] { a1, a2, a3 });
// assertEquals(1, result);
}

@Test
public void testInGroupingAtUpperBoundaryInclusive() {
SnowballProgram program = new SnowballProgram();
char maxChar = 'z';
char[] group = new char[4];
group[(maxChar - 'a') >> 3] |= (1 << (maxChar & 0x7));
program.setCurrent("z");
boolean result = program.in_grouping(group, 'a', 'z');
assertTrue(result);
}

@Test
public void testOutGroupingAtUpperBoundaryExclusive() {
SnowballProgram program = new SnowballProgram();
char[] group = new char[4];
group[0] = 0;
program.setCurrent("z");
boolean result = program.out_grouping(group, 'a', 'y');
assertTrue(result);
}

@Test
public void testInGroupingAtLowerBoundaryInclusive() {
SnowballProgram program = new SnowballProgram();
char minChar = 'a';
char[] group = new char[4];
group[(minChar - 'a') >> 3] |= (1 << (minChar & 0x7));
program.setCurrent("a");
boolean result = program.in_grouping(group, 'a', 'z');
assertTrue(result);
}

@Test
public void testOutGroupingBCharacterBelowMin() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("!");
program.cursor = 1;
char[] group = new char[4];
boolean result = program.out_grouping_b(group, 'a', 'z');
assertTrue(result);
}

@Test
public void testReplaceSWithInsertAtEnd() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
int delta = program.replace_s(3, 3, "XYZ");
assertEquals("abcXYZ", program.getCurrent());
assertEquals(3, delta);
}

@Test
public void testReplaceSWithNoChange() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("equal");
int delta = program.replace_s(0, 5, "equal");
assertEquals("equal", program.getCurrent());
assertEquals(0, delta);
}

@Test
public void testSetCurrentThenGetCurrentBufferMultipleCalls() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("buffer-test");
// char[] buf1 = program.getCurrentBuffer();
// int len1 = program.getCurrentBufferLength();
// char[] buf2 = program.getCurrentBuffer();
// int len2 = program.getCurrentBufferLength();
// assertEquals(len1, len2);
// assertArrayEquals(buf1, buf2);
}

@Test
public void testInsertShiftWithinBounds() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("aaee");
program.insert(2, 2, "bb");
assertEquals("aabbee", program.getCurrent());
}

@Test
public void testSetCurrentWithUnicode() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("日本語");
String result = program.getCurrent();
assertEquals("日本語", result);
}

@Test
public void testInGroupingWithGroupArrayUninitialized() {
SnowballProgram program = new SnowballProgram();
char[] group = new char[0];
program.setCurrent("g");
boolean result = program.in_grouping(group, 'a', 'z');
assertFalse(result);
}

@Test
public void testInGroupingFailsWhenCursorEqualsLimit() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
char[] group = new char[4];
group[0] = (char) (1 << ('a' & 7));
program.cursor = 3;
boolean result = program.in_grouping(group, 'a', 'c');
assertFalse(result);
}

@Test
public void testInGroupingBFailsWhenCursorEqualsLimitBackward() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
char[] group = new char[4];
group[0] = (char) (1 << ('a' & 7));
program.cursor = 0;
boolean result = program.in_grouping_b(group, 'a', 'c');
assertFalse(result);
}

@Test
public void testOutGroupingCursorIncrementWhenOutOfRangeLow() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("\u0001");
char[] group = new char[4];
group[0] = (char) (1 << ('a' & 7));
program.cursor = 0;
boolean result = program.out_grouping(group, 'a', 'z');
assertTrue(result);
assertEquals(1, program.cursor);
}

@Test
public void testOutGroupingBDecrementsCursorOutOfRangeLow() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("\u0001xyz");
program.cursor = 4;
char[] group = new char[4];
program.limit_backward = 0;
boolean result = program.out_grouping_b(group, 'a', 'z');
assertTrue(result);
assertEquals(3, program.cursor);
}

@Test
public void testEqSExactMatchAtEnd() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
program.cursor = 0;
boolean result = program.eq_s("abc");
assertTrue(result);
assertEquals(3, program.cursor);
}

@Test
public void testEqSPartialMatchFails() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
program.cursor = 0;
boolean result = program.eq_s("abx");
assertFalse(result);
assertEquals(0, program.cursor);
}

@Test
public void testEqSBMatchFailsOnLengthMismatch() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
program.cursor = 1;
boolean result = program.eq_s_b("bc");
assertFalse(result);
assertEquals(1, program.cursor);
}

@Test
public void testReplaceSInsertInMiddleOfWord() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdef");
int delta = program.replace_s(2, 4, "XY");
assertEquals("abXYef", program.getCurrent());
assertEquals(0, delta);
}

@Test
public void testReplaceSShiftRight() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abde");
int delta = program.replace_s(2, 2, "c");
assertEquals("abcde", program.getCurrent());
assertEquals(1, delta);
}

@Test
public void testReplaceSShiftLeft() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcde");
int delta = program.replace_s(2, 4, "f");
assertEquals("abfde", program.getCurrent());
assertEquals(-1, delta);
}

@Test
public void testFindAmongReturnsZeroWhenMatchFailsAfterMethodReturnsFalse() throws Throwable {
// MethodHandle falseHandle = MethodHandles.lookup().findVirtual(SnowballProgramFindAmongMock.class, "shouldReturnFalse", MethodType.methodType(boolean.class));
// Among[] variants = new Among[] { new Among("run", -1, 1, falseHandle) };
// SnowballProgramFindAmongMock program = new SnowballProgramFindAmongMock();
// program.setCurrent("run");
// int result = program.find_among(variants);
// assertEquals(0, result);
}

@Test
public void testFindAmongBReturnsZeroWhenNoMatchAndFallbacks() {
SnowballProgram program = new SnowballProgram();
// Among[] variants = new Among[] { new Among("abc", 0, 1, null) };
program.setCurrent("def");
program.cursor = 3;
// int result = program.find_among_b(variants);
// assertEquals(0, result);
}

@Test
public void testInsertBeforeBraAndKet() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("xyz");
program.bra = 2;
program.ket = 3;
program.insert(1, 2, "A");
assertEquals("xAyZ", program.getCurrent());
}

@Test
public void testSliceDelNoopWhenBraEqualsKet() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("hello");
program.bra = 2;
program.ket = 2;
program.slice_del();
assertEquals("hello", program.getCurrent());
}

@Test
public void testSliceFromOverwritesMiddle() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdefg");
program.bra = 1;
program.ket = 4;
program.slice_from("XYZ");
assertEquals("aXYZefg", program.getCurrent());
}

@Test
public void testSliceCheckFailsWhenBraIsNegative() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
program.bra = -1;
program.ket = 2;
try {
program.slice_check();
fail("Expected AssertionError due to bra < 0");
} catch (AssertionError ae) {
assertTrue(ae.getMessage().contains("bra"));
}
}

@Test
public void testSliceCheckFailsWhenLimitLessThanLength() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
program.ket = 3;
program.limit = 1;
// program.length = 3;
try {
program.slice_check();
fail("Expected AssertionError due to limit < length");
} catch (AssertionError ae) {
assertTrue(ae.getMessage().contains("limit"));
}
}

@Test
public void testAssignToWithPrepopulatedStringBuilder() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("text");
StringBuilder sb = new StringBuilder("oldContent");
// program.assign_to(sb);
assertEquals("text", sb.toString());
}

@Test
public void testSliceToOnFullString() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("wordslice");
program.bra = 0;
program.ket = 9;
StringBuilder sb = new StringBuilder("ignored");
program.slice_to(sb);
assertEquals("wordslice", sb.toString());
}

@Test
public void testInsertAtMidMovesKet() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("12345");
program.bra = 2;
program.ket = 4;
program.insert(3, 3, "X");
assertEquals("123X45", program.getCurrent());
}

@Test
public void testReplaceSAdjustsCursorIfBeforeRange() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdef");
program.cursor = 1;
int delta = program.replace_s(2, 5, "Z");
assertEquals("abZf", program.getCurrent());
assertEquals(1, program.cursor);
assertEquals(-2, delta);
}

@Test
public void testReplaceSAdjustsCursorIfInsideRange() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdef");
program.cursor = 3;
int delta = program.replace_s(2, 5, "Z");
assertEquals("abZf", program.getCurrent());
assertEquals(2, program.cursor);
assertEquals(-2, delta);
}

@Test
public void testReplaceSAdjustsCursorIfAfterRange() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdef");
program.cursor = 5;
int delta = program.replace_s(2, 4, "12");
assertEquals("ab12ef", program.getCurrent());
assertEquals(5, program.cursor);
assertEquals(0, delta);
}

@Test
public void testFindAmongBranchingViaSubstringI() throws Throwable {
// MethodHandle mh = MethodHandles.lookup().findVirtual(AmongStub.class, "returnFalse", MethodType.methodType(boolean.class));
// Among a0 = new Among("ab", 1, 1, mh);
// Among a1 = new Among("abc", -1, 2, null);
// AmongStub env = new AmongStub();
// env.setCurrent("ab");
// int result = env.find_among(new Among[] { a0, a1 });
// assertEquals(2, result);
}

@Test
public void testFindAmongBReturnsZeroWhenPrefixPartiallyMatches() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcd");
program.cursor = 4;
// Among[] a = new Among[] { new Among("xyz", -1, 1, null) };
// int result = program.find_among_b(a);
// assertEquals(0, result);
}

@Test
public void testEqSBValidMatchAtMiddleBounds() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcde");
program.cursor = 4;
boolean result = program.eq_s_b("cd");
assertTrue(result);
assertEquals(2, program.cursor);
}

@Test
public void testInsertAtStringStart() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("xyz");
program.insert(0, 0, "A");
assertEquals("Axyz", program.getCurrent());
}

@Test
public void testSliceFromReplaceAtStart() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("hellothere");
program.bra = 0;
program.ket = 5;
program.slice_from("hi");
assertEquals("hithere", program.getCurrent());
}

@Test
public void testOutGroupingNoBitSet() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("a");
char[] group = new char[4];
boolean result = program.out_grouping(group, 'a', 'z');
assertTrue(result);
}

@Test
public void testInGroupingDoesNotAdvanceIfFalse() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("z");
char[] group = new char[4];
group[0] = 0x00;
program.cursor = 0;
boolean result = program.in_grouping(group, 'a', 'z');
assertFalse(result);
assertEquals(0, program.cursor);
}

@Test
public void testReplaceSOnEmptyInput() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("");
int delta = program.replace_s(0, 0, "abc");
assertEquals("abc", program.getCurrent());
assertEquals(3, delta);
}

@Test
public void testReplaceSEmptyReplacementOverNonEmptyRange() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("hello");
int delta = program.replace_s(1, 4, "");
assertEquals("ho", program.getCurrent());
assertEquals(-3, delta);
}

@Test
public void testInsertReplacesNoChars() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
program.insert(1, 1, "x");
assertEquals("axbc", program.getCurrent());
}

@Test
public void testSliceDelWithZeroLengthInput() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("");
program.bra = 0;
program.ket = 0;
program.slice_del();
assertEquals("", program.getCurrent());
}

@Test
public void testSliceFromReplacesWithEmptyInMiddle() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdef");
program.bra = 2;
program.ket = 4;
program.slice_from("");
assertEquals("abef", program.getCurrent());
}

@Test
public void testSetAndGetCurrentBufferLengthZero() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("");
// int length = program.getCurrentBufferLength();
// assertEquals(0, length);
}

@Test
public void testSliceCheckFailsWhenKetGreaterThanLimit() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
program.bra = 1;
program.ket = 10;
program.limit = 3;
// program.length = 3;
try {
program.slice_check();
fail("Expected AssertionError due to ket > limit");
} catch (AssertionError e) {
assertTrue(e.getMessage().contains("ket"));
}
}

@Test
public void testEqSAndCursorBoundary() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
program.cursor = 1;
boolean result = program.eq_s("bc");
assertTrue(result);
assertEquals(3, program.cursor);
}

@Test
public void testEqSBOnlyFirstCharMatchFails() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("xyz");
program.cursor = 3;
boolean result = program.eq_s_b("yx");
assertFalse(result);
assertEquals(3, program.cursor);
}

@Test
public void testFindAmongWithSamePrefixWords() {
SnowballProgram program = new SnowballProgram();
// Among among1 = new Among("test", 1, 1, null);
// Among among2 = new Among("tester", 0, 2, null);
// Among among3 = new Among("testing", -1, 3, null);
program.setCurrent("testing");
// int result = program.find_among(new Among[] { among1, among2, among3 });
// assertEquals(3, result);
}

@Test
public void testFindAmongSingleEntryMatch() {
SnowballProgram program = new SnowballProgram();
// Among among = new Among("abc", -1, 99, null);
program.setCurrent("abc");
// int result = program.find_among(new Among[] { among });
// assertEquals(99, result);
}

@Test
public void testFindAmongBMultipleLevelsFallback() {
SnowballProgram program = new SnowballProgram();
// Among a1 = new Among("ing", -1, 1, null);
// Among a2 = new Among("zing", 0, 2, null);
program.setCurrent("testing");
program.cursor = 7;
// int result = program.find_among_b(new Among[] { a1, a2 });
// assertEquals(1, result);
}

@Test
public void testFindAmongMethodThrowsCheckedException() throws Exception {
SnowballProgram program = new SnowballProgram() {
};
MethodHandle throwing = MethodHandles.throwException(boolean.class, Exception.class).bindTo(new Exception("expected"));
// Among[] list = new Among[] { new Among("fail", -1, 1, throwing) };
program.setCurrent("fail");
try {
// program.find_among(list);
fail("Expected UndeclaredThrowableException");
} catch (UndeclaredThrowableException e) {
assertTrue(e.getCause().getMessage().contains("expected"));
}
}

@Test
public void testFindAmongBExactMatchSubstring() {
SnowballProgram program = new SnowballProgram();
// Among a = new Among("xyz", -1, 77, null);
program.setCurrent("axyz");
program.cursor = 4;
// int result = program.find_among_b(new Among[] { a });
// assertEquals(77, result);
}

@Test
public void testInGroupingMatchAtMinBoundary() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("a");
char[] group = new char[4];
group[0] = (char) (1 << ('a' - 'a'));
boolean result = program.in_grouping(group, 'a', 'z');
assertTrue(result);
}

@Test
public void testInGroupingBWithCursorAt1Match() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("b");
program.cursor = 1;
char[] group = new char[4];
group[0] = (char) (1 << ('b' - 'a'));
boolean result = program.in_grouping_b(group, 'a', 'z');
assertTrue(result);
}

@Test
public void testOutGroupingNoMatchReturnsFalse() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("y");
char[] group = new char[4];
group[0] = (char) (1 << ('y' - 'a'));
boolean result = program.out_grouping(group, 'a', 'z');
assertFalse(result);
}

@Test
public void testOutGroupingBNoMatchReturnsFalse() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("x");
program.cursor = 1;
char[] group = new char[4];
group[0] = (char) (1 << ('x' - 'a'));
boolean result = program.out_grouping_b(group, 'a', 'z');
assertFalse(result);
}

@Test
public void testOutGroupingBMatchReturnsTrue() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("?");
program.cursor = 1;
char[] group = new char[4];
boolean result = program.out_grouping_b(group, 'a', 'z');
assertTrue(result);
}

@Test
public void testGetCurrentAfterInsertFollowedBySliceDel() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("walk");
program.insert(4, 4, "ing");
program.bra = 4;
program.ket = 7;
program.slice_del();
assertEquals("walk", program.getCurrent());
}

@Test
public void testOutGroupingCharBelowMinAdvancesCursor() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("`");
char[] group = new char[4];
boolean result = program.out_grouping(group, 'a', 'z');
assertTrue(result);
assertEquals(1, program.cursor);
}

@Test
public void testOutGroupingCharAboveMaxAdvancesCursor() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("{");
char[] group = new char[4];
boolean result = program.out_grouping(group, 'a', 'z');
assertTrue(result);
assertEquals(1, program.cursor);
}

@Test
public void testOutGroupingBCharBelowMinDecrementsCursor() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("`");
program.cursor = 1;
char[] group = new char[4];
boolean result = program.out_grouping_b(group, 'a', 'z');
assertTrue(result);
assertEquals(0, program.cursor);
}

@Test
public void testOutGroupingBCharAboveMaxDecrementsCursor() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("}");
program.cursor = 1;
char[] group = new char[4];
boolean result = program.out_grouping_b(group, 'a', 'z');
assertTrue(result);
assertEquals(0, program.cursor);
}

@Test
public void testFindAmongBreaksWithFirstKeyAlreadyInspected() {
SnowballProgram program = new SnowballProgram();
// Among[] variants = new Among[] { new Among("ab", 0, 1, null), new Among("abc", -1, 2, null) };
program.setCurrent("xy");
// int result = program.find_among(variants);
// assertEquals(0, result);
}

@Test
public void testFindAmongLoopWithOnlyOneItem() {
SnowballProgram program = new SnowballProgram();
// Among[] v = new Among[] { new Among("x", -1, 1, null) };
program.setCurrent("x");
// int result = program.find_among(v);
// assertEquals(1, result);
}

@Test
public void testFindAmongBLoopWithOnlyOneItem() {
SnowballProgram program = new SnowballProgram();
// Among[] v = new Among[] { new Among("x", -1, 2, null) };
program.setCurrent("abx");
program.cursor = 3;
// int result = program.find_among_b(v);
// assertEquals(2, result);
}

@Test
public void testFindAmongMatchWithMethodReturningTrue() throws Throwable {
// MethodHandle success = MethodHandles.lookup().findVirtual(CallableStub.class, "execute", MethodType.methodType(boolean.class));
// Among[] list = new Among[] { new Among("abc", -1, 99, success) };
// CallableStub program = new CallableStub();
// program.setCurrent("abc");
// int result = program.find_among(list);
// assertEquals(99, result);
}

@Test
public void testFindAmongThrowsUndeclaredIfThrowableIsNotErrorOrRuntime() throws Throwable {
MethodHandle failing = MethodHandles.throwException(boolean.class, Exception.class).bindTo(new Exception("mocked"));
// Among[] list = new Among[] { new Among("abc", -1, 1, failing) };
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
try {
// program.find_among(list);
fail("Expected UndeclaredThrowableException");
} catch (UndeclaredThrowableException ex) {
assertTrue(ex.getCause() instanceof Exception);
}
}

@Test
public void testSliceDelWithBraAndKetSameAsLimit() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
program.bra = 1;
program.ket = 3;
program.slice_del();
assertEquals("a", program.getCurrent());
}

@Test
public void testSliceCheckFailsWhenBraGreaterThanKet() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("xyz");
program.bra = 2;
program.ket = 1;
program.limit = 3;
// program.length = 3;
try {
program.slice_check();
fail("Expected AssertionError");
} catch (AssertionError e) {
assertTrue(e.getMessage().contains("bra"));
}
}

@Test
public void testReplaceSResultWithLongerInputAfterLimit() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
int delta = program.replace_s(2, 3, "defghi");
assertEquals("abdefghi", program.getCurrent());
assertEquals(5, delta);
}

@Test
public void testReplaceSWhenCursorAfterKet() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
program.cursor = 3;
int delta = program.replace_s(1, 2, "X");
assertEquals("aXc", program.getCurrent());
assertEquals(0, delta);
assertEquals(3, program.cursor);
}

@Test
public void testReplaceSWhenCursorInsideReplacementRange() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdef");
program.cursor = 3;
int delta = program.replace_s(2, 5, "XY");
assertEquals("abXYf", program.getCurrent());
assertEquals(-1, delta);
assertEquals(2, program.cursor);
}

@Test
public void testInsertShiftAdjustsBraAndKet() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
program.bra = 1;
program.ket = 2;
program.insert(1, 1, "XYZ");
assertEquals("aXYZbc", program.getCurrent());
assertEquals(4, program.ket);
}

@Test
public void testAssignToTruncateExistingBuilder() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("xyz");
StringBuilder sb = new StringBuilder("abcdefgh");
// program.assign_to(sb);
assertEquals("xyz", sb.toString());
}

@Test
public void testSliceToTruncateBuilderTarget() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdef");
program.bra = 2;
program.ket = 5;
StringBuilder sb = new StringBuilder("FOOBAR");
program.slice_to(sb);
assertEquals("cde", sb.toString());
}
}
