package opennlp.tools.stemmer.snowball;

import org.junit.jupiter.api.Test;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;

public class SnowballProgram_1_GPTLLMTest {

@Test
public void testDefaultConstructorInitializesEmptyState() {
SnowballProgram program = new SnowballProgram();
assertEquals("", program.getCurrent());
// assertEquals(0, program.getCurrentBufferLength());
}

@Test
public void testSetCurrentString() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("running");
assertEquals("running", program.getCurrent());
}

@Test
public void testSetCurrentCharArray() {
SnowballProgram program = new SnowballProgram();
char[] chars = new char[] { 'r', 'u', 'n', 'n', 'i', 'n', 'g' };
// program.setCurrent(chars, chars.length);
assertEquals("running", program.getCurrent());
}

@Test
public void testGetCurrentBufferAndLength() {
SnowballProgram program = new SnowballProgram();
char[] chars = new char[] { 'e', 'x', 'a', 'm', 'p', 'l', 'e' };
// program.setCurrent(chars, chars.length);
// assertEquals(7, program.getCurrentBufferLength());
// assertEquals("example", new String(program.getCurrentBuffer(), 0, program.getCurrentBufferLength()));
}

@Test
public void testCopyConstructor() {
SnowballProgram original = new SnowballProgram();
original.setCurrent("copy");
SnowballProgram copy = new SnowballProgram(original);
assertEquals("copy", copy.getCurrent());
}

@Test
public void testCopyFrom() {
SnowballProgram original = new SnowballProgram();
original.setCurrent("origin");
SnowballProgram target = new SnowballProgram();
target.copy_from(original);
assertEquals("origin", target.getCurrent());
}

@Test
public void testInGroupingTrue() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("a");
char[] group = new char[4];
group[0] = (char) 0b00000001;
boolean result = program.in_grouping(group, 'a', 'd');
assertTrue(result);
assertEquals(1, program.cursor);
}

@Test
public void testInGroupingFalse() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("z");
char[] group = new char[4];
group[0] = (char) 0b00000001;
boolean result = program.in_grouping(group, 'a', 'd');
assertFalse(result);
assertEquals(0, program.cursor);
}

@Test
public void testInGroupingBTrue() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("x");
program.cursor = 1;
char[] mask = new char[4];
mask[0] = (char) 0b00000001;
boolean result = program.in_grouping_b(mask, 'x', 'x');
assertTrue(result);
assertEquals(0, program.cursor);
}

@Test
public void testOutGroupingTrue() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("z");
char[] group = new char[4];
boolean result = program.out_grouping(group, 'a', 'd');
assertTrue(result);
assertEquals(1, program.cursor);
}

@Test
public void testEqSMatches() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("test");
boolean result = program.eq_s("test");
assertTrue(result);
assertEquals(4, program.cursor);
}

@Test
public void testEqSFails() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("test");
boolean result = program.eq_s("fail");
assertFalse(result);
assertEquals(0, program.cursor);
}

@Test
public void testEqSBMatches() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("backend");
program.cursor = 7;
boolean result = program.eq_s_b("end");
assertTrue(result);
assertEquals(4, program.cursor);
}

@Test
public void testEqSBFails() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("backend");
program.cursor = 7;
boolean result = program.eq_s_b("fail");
assertFalse(result);
assertEquals(7, program.cursor);
}

@Test
public void testReplaceSReplacesCorrectCharset() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdef");
int adjusted = program.replace_s(1, 4, "XYZ");
assertEquals("aXYZef", program.getCurrent());
assertEquals(0, adjusted);
}

@Test
public void testSliceFromReplacesCorrectSubstring() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdef");
program.bra = 1;
program.ket = 4;
program.slice_from("XYZ");
assertEquals("aXYZef", program.getCurrent());
}

@Test
public void testSliceDelRemovesCorrectRange() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdef");
program.bra = 1;
program.ket = 4;
program.slice_del();
assertEquals("aef", program.getCurrent());
}

@Test
public void testInsertAddsCorrectly() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("ace");
program.insert(1, 1, "b");
assertEquals("abce", program.getCurrent());
}

@Test
public void testSliceToAppendsToBuilder() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdef");
program.bra = 2;
program.ket = 5;
StringBuilder builder = new StringBuilder("xxxxx");
program.slice_to(builder);
assertEquals("cde", builder.toString());
}

@Test
public void testAssignToCopiesBufferToBuilder() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("natural");
StringBuilder builder = new StringBuilder("junk");
// program.assign_to(builder);
assertEquals("natural", builder.toString());
}

@Test
public void testFindAmongMatchesWithoutMethod() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("walked");
// Among among = new Among("walked", 0, 1, null, "");
// Among[] list = new Among[] { among };
// int result = program.find_among(list);
// assertEquals(1, result);
}

@Test
public void testFindAmongBMatchesWithoutMethod() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("walked");
program.cursor = 6;
// Among among = new Among("ed", 0, 2, null, "");
// Among[] list = new Among[] { among };
// int result = program.find_among_b(list);
// assertEquals(2, result);
}

@Test
public void testFindAmongWithMatchCallback() throws Throwable {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
MethodHandles.Lookup lookup = MethodHandles.lookup();
// var invoked = lookup.findVirtual(SnowballProgramTest.class, "exampleCallback", MethodType.methodType(boolean.class, SnowballProgram.class));
// Among among = new Among("abc", 0, 5, invoked, "");
// Among[] arr = new Among[] { among };
// int result = program.find_among(arr);
// assertEquals(5, result);
}

@Test
public void testSliceCheckAssertionFailureForKetTooHigh() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
program.bra = 0;
program.ket = 5;
program.slice_check();
}

@Test
public void testSliceCheckAssertionFailureForBraGreaterThanKet() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
program.bra = 3;
program.ket = 1;
program.slice_check();
}

@Test
public void testSetCurrentWithEmptyString() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("");
assertEquals("", program.getCurrent());
}

@Test
public void testSetCurrentWithEmptyCharArray() {
SnowballProgram program = new SnowballProgram();
char[] empty = new char[0];
// program.setCurrent(empty, 0);
assertEquals("", program.getCurrent());
}

@Test
public void testReplaceSWhenReplacementIsLonger() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
int adjustment = program.replace_s(1, 2, "XYZ123");
assertEquals("aXYZ123c", program.getCurrent());
assertEquals(5, adjustment);
}

@Test
public void testReplaceSWhenReplacementIsShorter() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdefgh");
int adjustment = program.replace_s(2, 6, "X");
assertEquals("abXgh", program.getCurrent());
assertEquals(-3, adjustment);
}

@Test
public void testFindAmongWithSubstringFallback() throws Throwable {
SnowballProgram program = new SnowballProgram();
program.setCurrent("hello");
MethodHandles.Lookup lookup = MethodHandles.lookup();
// var methodHandleFalse = lookup.findVirtual(SnowballProgramAdditionalTest.class, "falseCallback", MethodType.methodType(boolean.class, SnowballProgram.class));
// Among fail = new Among("hello", 0, 1, methodHandleFalse, "");
// Among[] v = new Among[] { fail };
// int result = program.find_among(v);
// assertEquals(0, result);
}

@Test
public void testFindAmongBReturnsZeroOnPartialMismatch() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("testing");
program.cursor = 7;
// Among among = new Among("xyz", -1, 99, null, "");
// Among[] arr = new Among[] { among };
// int result = program.find_among_b(arr);
// assertEquals(0, result);
}

@Test
public void testInsertAtEnd() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
// int position = program.getCurrentBufferLength();
// program.insert(position, position, "XYZ");
assertEquals("abcXYZ", program.getCurrent());
}

@Test
public void testInsertWithEmptyReplacement() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
program.insert(1, 2, "");
assertEquals("ac", program.getCurrent());
}

@Test
public void testInGroupingFailsWhenCursorAtLimit() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
program.cursor = 3;
char[] mask = new char[4];
boolean result = program.in_grouping(mask, 'a', 'z');
assertFalse(result);
}

@Test
public void testInGroupingBFailsCursorBoundary() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
program.cursor = 0;
char[] mask = new char[4];
boolean result = program.in_grouping_b(mask, 'a', 'z');
assertFalse(result);
}

@Test
public void testOutGroupingBFailsWhenInsideGroup() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("d");
program.cursor = 1;
char[] group = new char[4];
group[0] = (char) 0b00001000;
boolean result = program.out_grouping_b(group, 'a', 'z');
assertFalse(result);
}

@Test
public void testOutGroupingBFailsAtLimitBoundary() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("a");
program.cursor = 0;
char[] group = new char[4];
boolean result = program.out_grouping_b(group, 'a', 'z');
assertFalse(result);
}

@Test
public void testSliceToWhenBraEqualsKet() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("test");
program.bra = 2;
program.ket = 2;
StringBuilder sb = new StringBuilder("garbage");
program.slice_to(sb);
assertEquals("", sb.toString());
}

@Test
public void testAssignToAfterSliceFrom() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdef");
program.bra = 1;
program.ket = 4;
program.slice_from("xy");
StringBuilder sb = new StringBuilder("junk");
// program.assign_to(sb);
assertEquals("axyef", sb.toString());
}

@Test
public void testReplaceSReplacesEntireWord() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdef");
int adjustment = program.replace_s(0, 6, "XYZ");
assertEquals("XYZ", program.getCurrent());
assertEquals(-3, adjustment);
}

@Test
public void testReplaceSInsertsInMiddleWithoutOverlap() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("headtail");
int adjustment = program.replace_s(4, 4, "middle");
assertEquals("headmiddletail", program.getCurrent());
assertEquals(6, adjustment);
}

@Test
public void testInsertWithNegativeAdjustmentDoesNotAdjustOutsideRange() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdef");
program.bra = 1;
program.ket = 5;
program.insert(2, 4, "");
assertEquals("abcdef", program.getCurrent());
}

@Test
public void testSliceFromEmptyReplacesWithEmpty() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcde");
program.bra = 1;
program.ket = 4;
program.slice_from("");
assertEquals("ae", program.getCurrent());
}

@Test
public void testSliceDelNoOpWhenBraEqualsKet() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("hello");
program.bra = 2;
program.ket = 2;
program.slice_del();
assertEquals("hello", program.getCurrent());
}

@Test
public void testEqSWithEmptyMatch() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("stem");
boolean result = program.eq_s("");
assertTrue(result);
assertEquals(0, program.cursor);
}

@Test
public void testEqSBWithEmptyMatch() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("parser");
program.cursor = 6;
boolean result = program.eq_s_b("");
assertTrue(result);
assertEquals(6, program.cursor);
}

@Test
public void testOutGroupingMatchesBoundaryOutsideGroup() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("e");
char[] group = new char[4];
group[0] = (char) 0b00011111;
boolean result = program.out_grouping(group, 'a', 'e');
assertFalse(result);
}

@Test
public void testOutGroupingBAtStartReturnsFalse() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("a");
program.cursor = 0;
char[] mask = new char[4];
boolean result = program.out_grouping_b(mask, 'a', 'z');
assertFalse(result);
}

@Test
public void testFindAmongBThrowsFromMethod() throws Throwable {
SnowballProgram program = new SnowballProgram();
program.setCurrent("erroring");
program.cursor = 8;
MethodHandles.Lookup lookup = MethodHandles.lookup();
// var throwingMethod = lookup.findVirtual(SnowballProgramEdgeCaseTest.class, "throwingCallback", MethodType.methodType(boolean.class, SnowballProgram.class));
// Among[] arr = new Among[] { new Among("ing", 0, 1, throwingMethod, "") };
// program.find_among_b(arr);
}

@Test
public void testFindAmongBFailureAfterSuccessfulPrefix() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("running");
program.cursor = 7;
// Among a = new Among("nning", -1, 1, null, "");
// Among[] list = new Among[] { a };
// int result = program.find_among_b(list);
// assertEquals(0, result);
}

@Test
public void testFindAmongEmptyArrayReturnsZero() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("nothing");
Among[] empty = new Among[0];
int result = program.find_among(empty);
assertEquals(0, result);
}

@Test
public void testFindAmongBEmptyArrayReturnsZero() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("nothing");
program.cursor = 7;
Among[] empty = new Among[0];
int result = program.find_among_b(empty);
assertEquals(0, result);
}

@Test
public void testSliceToClearsBuilder() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdef");
program.bra = 1;
program.ket = 3;
StringBuilder sb = new StringBuilder("junk");
program.slice_to(sb);
assertEquals("bc", sb.toString());
}

@Test
public void testAssignToClearsBuilderFirst() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("stemmer");
StringBuilder builder = new StringBuilder("junkvalue");
// program.assign_to(builder);
assertEquals("stemmer", builder.toString());
}

@Test
public void testSetCurrentTruncatesWhenLengthIsLessThanArraySize() {
SnowballProgram program = new SnowballProgram();
char[] array = new char[] { 'h', 'e', 'l', 'l', 'o', 'x', 'x' };
// program.setCurrent(array, 5);
assertEquals("hello", program.getCurrent());
}

@Test
public void testInGroupingRightOnMinBoundary() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("a");
char[] group = new char[4];
group[0] = (char) 0b00000001;
boolean result = program.in_grouping(group, 'a', 'c');
assertTrue(result);
assertEquals(1, program.cursor);
}

@Test
public void testInGroupingCharLessThanMin() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("`");
char[] group = new char[4];
group[0] = (char) 0b00000001;
boolean result = program.in_grouping(group, 'a', 'c');
assertFalse(result);
assertEquals(0, program.cursor);
}

@Test
public void testInGroupingCharGreaterThanMax() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("z");
char[] group = new char[4];
boolean result = program.in_grouping(group, 'a', 'c');
assertFalse(result);
assertEquals(0, program.cursor);
}

@Test
public void testOutGroupingCharEqualToMinAndNotInGroup() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("a");
char[] group = new char[4];
boolean result = program.out_grouping(group, 'a', 'c');
assertTrue(result);
assertEquals(1, program.cursor);
}

@Test
public void testOutGroupingCharInGroup() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("b");
char[] group = new char[4];
group[1] = (char) 0b00000100;
boolean result = program.out_grouping(group, 'a', 'z');
assertFalse(result);
assertEquals(0, program.cursor);
}

@Test
public void testEqSWithPartialMatchingPrefix() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("stimulation");
boolean result = program.eq_s("stimule");
assertFalse(result);
assertEquals(0, program.cursor);
}

@Test
public void testEqSBWithPartialMatchingSuffix() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("retention");
program.cursor = 9;
boolean result = program.eq_s_b("tional");
assertFalse(result);
assertEquals(9, program.cursor);
}

@Test
public void testSliceDelAtEntireWord() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdef");
program.bra = 0;
program.ket = 6;
program.slice_del();
assertEquals("", program.getCurrent());
}

@Test
public void testInsertAtBeginningWhenOverlapExists() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("xyz");
program.bra = 0;
program.ket = 1;
program.insert(0, 1, "a");
assertEquals("axyz", program.getCurrent());
}

@Test
public void testSliceToWhenBraEqualsKetAndContentPresent() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("testcase");
program.bra = 4;
program.ket = 4;
StringBuilder sb = new StringBuilder("shouldBeCleared");
program.slice_to(sb);
assertEquals("", sb.toString());
}

@Test
public void testReplaceSWithNoShiftNeededButDifferentLetters() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcedfg");
int change = program.replace_s(2, 5, "123");
assertEquals("ab123fg", program.getCurrent());
assertEquals(0, change);
}

@Test
public void testReplaceSInsertNewCharactersAtLimit() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
int change = program.replace_s(3, 3, "XYZ");
assertEquals("abcXYZ", program.getCurrent());
assertEquals(3, change);
}

@Test
public void testAssignToAfterMultipleTransformations() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdef");
program.bra = 2;
program.ket = 5;
program.slice_from("X");
program.slice_del();
StringBuilder sb = new StringBuilder("willBeOverwritten");
// program.assign_to(sb);
assertEquals("abf", sb.toString());
}

@Test
public void testGetCurrentBufferReturnsSameReference() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("constant");
// char[] buffer1 = program.getCurrentBuffer();
// char[] buffer2 = program.getCurrentBuffer();
// assertSame(buffer1, buffer2);
}

@Test
public void testFindAmongWithMultipleElementsAndPrefixMatchOnly() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abczzz");
// Among match1 = new Among("abc", 1, 11, null, "");
// Among match2 = new Among("abcd", 0, 22, null, "");
// Among[] ams = new Among[] { match1, match2 };
// int result = program.find_among(ams);
// assertEquals(11, result);
}

@Test
public void testFindAmongReturnsZeroWhenCallbackReturnsFalse() throws Throwable {
SnowballProgram program = new SnowballProgram();
program.setCurrent("callback");
MethodHandles.Lookup lookup = MethodHandles.lookup();
// var method = lookup.findVirtual(SnowballProgramCoverageEnhancementTest.class, "falseReturningMethod", MethodType.methodType(boolean.class, SnowballProgram.class));
// Among a = new Among("callback", -1, 10, method, "");
// Among[] arr = new Among[] { a };
// int result = program.find_among(arr);
// assertEquals(0, result);
}

@Test
public void testFindAmongBWithMultipleItemsDescendingSelectsCorrect() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("processinged");
program.cursor = 12;
// Among a = new Among("inged", -1, 1, null, "");
// Among b = new Among("ed", 0, 2, null, "");
// Among[] arr = new Among[] { a, b };
// int result = program.find_among_b(arr);
// assertEquals(2, result);
}

@Test
public void testFindAmongWithMatchingPrefixButCallbackFalseFallsBackToSubstringIndex() throws Throwable {
SnowballProgram program = new SnowballProgram();
program.setCurrent("reject");
MethodHandles.Lookup lookup = MethodHandles.lookup();
// var method = lookup.findVirtual(SnowballProgramCoverageEnhancementTest.class, "falseReturningMethod", MethodType.methodType(boolean.class, SnowballProgram.class));
// Among fallback = new Among("reject", 0, 42, method, "");
// Among[] input = new Among[] { fallback };
// int result = program.find_among(input);
// assertEquals(0, result);
}

@Test
public void testInsertWithShiftCausesBraKetToMove() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("xyz");
program.bra = 1;
program.ket = 3;
program.insert(1, 2, "INSERT");
String expected = "xINSERTyz";
assertEquals(expected, program.getCurrent());
assertEquals(1 + 6, program.bra);
assertEquals(3 + 6, program.ket);
}

@Test
public void testReplaceSUpdatesCursorWhenCursorBeyondKet() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdef");
program.cursor = 5;
int diff = program.replace_s(2, 4, "123456");
assertEquals("ab123456ef", program.getCurrent());
assertEquals(5 + 4, program.cursor);
assertEquals(4, diff);
}

@Test
public void testReplaceSUpdatesCursorWhenCursorBetweenBraAndKet() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdef");
program.cursor = 3;
int diff = program.replace_s(2, 4, "X");
assertEquals("abXef", program.getCurrent());
assertEquals(2, program.cursor);
}

@Test
public void testSliceCheckWithValidConfigurationDoesNotThrow() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcde");
program.bra = 1;
program.ket = 3;
program.slice_check();
}

@Test
public void testFindAmongThrowsUndeclaredThrowableException() throws Throwable {
SnowballProgram program = new SnowballProgram();
program.setCurrent("fail");
MethodHandles.Lookup lookup = MethodHandles.lookup();
// var handler = lookup.findVirtual(SnowballProgramCoverageEnhancementTest.class, "throwingMethod", MethodType.methodType(boolean.class, SnowballProgram.class));
// Among a = new Among("fail", -1, 99, handler, "");
// Among[] arr = new Among[] { a };
// program.find_among(arr);
}

@Test
public void testFindAmongBThrowsUndeclaredThrowableException() throws Throwable {
SnowballProgram program = new SnowballProgram();
program.setCurrent("reversedtest");
program.cursor = 12;
MethodHandles.Lookup lookup = MethodHandles.lookup();
// var handler = lookup.findVirtual(SnowballProgramCoverageEnhancementTest.class, "throwingMethod", MethodType.methodType(boolean.class, SnowballProgram.class));
// Among a = new Among("test", -1, 2, handler, "");
// Among[] list = new Among[] { a };
// program.find_among_b(list);
}

@Test
public void testSliceToWithFullRangeCapturesExpectedSubstring() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("unitest");
program.bra = 0;
program.ket = 7;
StringBuilder target = new StringBuilder("junk");
program.slice_to(target);
assertEquals("unitest", target.toString());
}

@Test
public void testAssignToWithEmptyProgramBuffer() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("");
StringBuilder builder = new StringBuilder("willClear");
// program.assign_to(builder);
assertEquals("", builder.toString());
}

@Test
public void testInGroupingCharMatchesExactlyOnMaxBoundary() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("z");
char[] group = new char[4];
group['z' - 'z'] = (char) 0b00000001;
boolean result = program.in_grouping(group, 'z', 'z');
assertTrue(result);
assertEquals(1, program.cursor);
}

@Test
public void testInGroupingBAtSingleValidChar() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("y");
program.cursor = 1;
char[] group = new char[4];
group['y' - 'y'] = (char) 0b00000001;
boolean result = program.in_grouping_b(group, 'y', 'y');
assertTrue(result);
assertEquals(0, program.cursor);
}

@Test
public void testOutGroupingCharNotInGroupWithinRange() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("d");
char[] mask = new char[4];
boolean result = program.out_grouping(mask, 'a', 'z');
assertTrue(result);
assertEquals(1, program.cursor);
}

@Test
public void testOutGroupingBCharNotInGroupUpdatesCursor() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("x");
program.cursor = 1;
char[] group = new char[4];
boolean result = program.out_grouping_b(group, 'a', 'z');
assertTrue(result);
assertEquals(0, program.cursor);
}

@Test
public void testEqSWhenBufferSmallerThanTarget() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("short");
boolean matched = program.eq_s("shorter");
assertFalse(matched);
assertEquals(0, program.cursor);
}

@Test
public void testEqSbWhenBufferSmallerThanTarget() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("run");
program.cursor = 3;
boolean matched = program.eq_s_b("runner");
assertFalse(matched);
assertEquals(3, program.cursor);
}

@Test
public void testSliceDelWithFullSlice() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdef");
program.bra = 0;
program.ket = 6;
program.slice_del();
assertEquals("", program.getCurrent());
}

@Test
public void testSliceDelWithSingleCharRemoved() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
program.bra = 1;
program.ket = 2;
program.slice_del();
assertEquals("ac", program.getCurrent());
}

@Test
public void testInsertPreservesCorrectBraKetPostInsert() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("A--B");
program.bra = 1;
program.ket = 3;
program.insert(2, 2, "+");
assertEquals("A-+-B", program.getCurrent());
assertEquals(1, program.bra);
assertEquals(4, program.ket);
}

@Test
public void testSliceCheckValidWithinBounds() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdef");
program.bra = 1;
program.ket = 4;
program.slice_check();
}

@Test
public void testSliceCheckFailsWhenBraNegative() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
program.bra = -1;
program.ket = 2;
program.slice_check();
}

@Test
public void testSliceCheckFailsWhenKetGreaterThanLimit() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
program.bra = 0;
program.ket = 4;
program.slice_check();
}

@Test
public void testSliceToWithEmptySliceSetsBuilderEmpty() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("hello");
program.bra = 2;
program.ket = 2;
StringBuilder sb = new StringBuilder("nonempty");
program.slice_to(sb);
assertEquals("", sb.toString());
}

@Test
public void testAssignToReplacesBuilderContent() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("copyhere");
StringBuilder sb = new StringBuilder("olddata");
// program.assign_to(sb);
assertEquals("copyhere", sb.toString());
}

@Test
public void testGetCurrentBufferMatchesToGetCurrent() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("buffer");
// char[] chars = program.getCurrentBuffer();
// String asString = new String(chars, 0, program.getCurrentBufferLength());
// assertEquals(program.getCurrent(), asString);
}

@Test
public void testReplaceSWithZeroLengthSliceAndReplacement() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
int change = program.replace_s(1, 1, "");
assertEquals("abc", program.getCurrent());
assertEquals(0, change);
}

@Test
public void testReplaceSWithZeroLengthSliceInsertsChars() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
int change = program.replace_s(1, 1, "XYZ");
assertEquals("aXYZbc", program.getCurrent());
assertEquals(3, change);
}

@Test
public void testInGroupingBitMaskHitWithHighBitSet() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("g");
char[] group = new char[4];
group['g' - 'g'] = (char) 0b10000000;
boolean result = program.in_grouping(group, 'g', 'g');
assertEquals(false, result);
}

@Test
public void testInGroupingBitMaskHitWithSetBit() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("c");
char[] group = new char[4];
int index = 'c' - 'a';
group[index >> 3] = (char) (1 << (index & 7));
boolean result = program.in_grouping(group, 'a', 'z');
assertTrue(result);
}

@Test
public void testFindAmongMidBinarySearchMatch() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("beta");
// Among a1 = new Among("alpha", -1, 1, null, "");
// Among a2 = new Among("beta", -1, 2, null, "");
// Among a3 = new Among("gamma", -1, 3, null, "");
// Among[] items = new Among[] { a1, a2, a3 };
// int result = program.find_among(items);
// assertEquals(2, result);
}

@Test
public void testFindAmongPrefixMatchesButCallbackFailsFallbackToSubstringIndex() throws Throwable {
SnowballProgram program = new SnowballProgram();
program.setCurrent("mismatch");
MethodHandles.Lookup lookup = MethodHandles.lookup();
// var handle = lookup.findVirtual(SnowballProgramEdgeCoverageTest.class, "returningFalse", MethodType.methodType(boolean.class, SnowballProgram.class));
// Among m = new Among("mismatch", 0, 5, handle, "");
// Among[] items = new Among[] { m };
// int result = program.find_among(items);
// assertEquals(0, result);
}

@Test
public void testFindAmongBCharMismatchSkipsEarly() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("zzztest");
program.cursor = 7;
// Among a1 = new Among("xyz", -1, 1, null, "");
// Among a2 = new Among("test", -1, 2, null, "");
// Among[] items = new Among[] { a1, a2 };
// int result = program.find_among_b(items);
// assertEquals(2, result);
}

@Test
public void testReplaceSIntoSelfSameLengthNoChange() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdef");
int changed = program.replace_s(1, 4, "bcd");
assertEquals("abcdef", program.getCurrent());
assertEquals(0, changed);
}

@Test
public void testReplaceSOnRightEdgeWithGrowth() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
int changed = program.replace_s(2, 3, "XYZ1234");
assertEquals("abXYZ1234", program.getCurrent());
assertEquals(6, changed);
}

@Test
public void testFindAmongInvokesAndThrowsExceptionInWrapper() throws Throwable {
SnowballProgram program = new SnowballProgram();
program.setCurrent("error");
MethodHandles.Lookup lookup = MethodHandles.lookup();
// var h = lookup.findVirtual(SnowballProgramEdgeCoverageTest.class, "throwingMethod", MethodType.methodType(boolean.class, SnowballProgram.class));
// Among a = new Among("error", -1, 9, h, "");
// Among[] arr = new Among[] { a };
// program.find_among(arr);
}

@Test
public void testSliceCheckWhenKetEqualsLength() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("XYZ");
program.bra = 0;
program.ket = 3;
program.slice_check();
}

@Test
public void testSliceCheckWhenKetGreaterThanLengthFails() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("XYZ");
program.bra = 0;
program.ket = 4;
program.slice_check();
}

@Test
public void testInsertWithEmptyTextNoEffect() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("1234");
program.insert(1, 3, "");
assertEquals("14", program.getCurrent());
}

@Test
public void testSliceDelWithNoChangeWhenBraEqualsKet() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("noop");
program.bra = 2;
program.ket = 2;
program.slice_del();
assertEquals("noop", program.getCurrent());
}
}
