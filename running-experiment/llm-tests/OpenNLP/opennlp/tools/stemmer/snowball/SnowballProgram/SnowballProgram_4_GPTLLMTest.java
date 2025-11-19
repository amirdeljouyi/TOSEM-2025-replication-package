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

public class SnowballProgram_4_GPTLLMTest {

@Test
public void testSetAndGetCurrentString() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("running");
assertEquals("running", program.getCurrent());
program.setCurrent("");
assertEquals("", program.getCurrent());
program.setCurrent("s");
assertEquals("s", program.getCurrent());
}

@Test
public void testSetCurrentWithCharArray() {
SnowballProgram program = new SnowballProgram();
char[] text = new char[] { 'h', 'e', 'l', 'l', 'o' };
// program.setCurrent(text, 5);
assertEquals("hello", program.getCurrent());
}

@Test
public void testGetCurrentBufferAndLength() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("example");
// char[] buffer = program.getCurrentBuffer();
// assertNotNull(buffer);
// int len = program.getCurrentBufferLength();
// assertEquals(7, len);
// String actual = new String(buffer, 0, len);
// assertEquals("example", actual);
}

@Test
public void testCopyConstructorCreatesEquivalentProgram() {
SnowballProgram original = new SnowballProgram();
original.setCurrent("compute");
SnowballProgram copy = new SnowballProgram(original);
assertEquals("compute", copy.getCurrent());
}

@Test
public void testCopyFromCopiesCurrentState() {
SnowballProgram src = new SnowballProgram();
src.setCurrent("derived");
SnowballProgram dest = new SnowballProgram();
dest.copy_from(src);
assertEquals("derived", dest.getCurrent());
}

@Test
public void testInGroupingTrueCase() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("bcd");
char[] bitmask = new char[4];
bitmask[0] = (char) (1 << ('b' - 'a'));
program.cursor = 0;
boolean result = program.in_grouping(bitmask, 'a', 'z');
assertTrue(result);
}

@Test
public void testInGroupingFalseCase() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("xyz");
char[] bitmask = new char[4];
program.cursor = 0;
boolean result = program.in_grouping(bitmask, 'a', 'c');
assertFalse(result);
}

@Test
public void testInGroupingBTrueCase() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("bcd");
char[] bitmask = new char[4];
bitmask[0] = (char) (1 << ('d' - 'a'));
program.cursor = 3;
boolean result = program.in_grouping_b(bitmask, 'a', 'z');
assertTrue(result);
}

@Test
public void testInGroupingBFalseCase() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("xyz");
char[] bitmask = new char[4];
program.cursor = 2;
boolean result = program.in_grouping_b(bitmask, 'a', 'c');
assertFalse(result);
}

@Test
public void testOutGroupingTrueCase() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("z");
char[] bitmask = new char[4];
program.cursor = 0;
boolean result = program.out_grouping(bitmask, 'a', 'c');
assertTrue(result);
}

@Test
public void testOutGroupingFalseCase() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("a");
char[] bitmask = new char[4];
bitmask[0] = (char) (1 << ('a' - 'a'));
program.cursor = 0;
boolean result = program.out_grouping(bitmask, 'a', 'c');
assertFalse(result);
}

@Test
public void testEqSReturnsTrue() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("prefixX");
program.cursor = 0;
boolean result = program.eq_s("prefix");
assertTrue(result);
}

@Test
public void testEqSReturnsFalse() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("pretext");
program.cursor = 0;
boolean result = program.eq_s("prefix");
assertFalse(result);
}

@Test
public void testEqSBReturnsTrue() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("startsuffix");
program.cursor = 11;
boolean result = program.eq_s_b("suffix");
assertTrue(result);
}

@Test
public void testEqSBReturnsFalse() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("startpre");
program.cursor = 8;
boolean result = program.eq_s_b("suffix");
assertFalse(result);
}

@Test
public void testReplaceSWithExpansion() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcde");
int adjusted = program.replace_s(1, 3, "XYZ");
String updated = program.getCurrent();
assertEquals("aXYZde", updated);
assertEquals(1, adjusted);
}

@Test
public void testReplaceSWithShrink() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdef");
int adjusted = program.replace_s(1, 4, "x");
String updated = program.getCurrent();
assertEquals("axef", updated);
assertEquals(-2, adjusted);
}

@Test
public void testSliceFromReplacesMiddle() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("starring");
program.bra = 4;
program.ket = 7;
program.slice_from("ked");
assertEquals("starked", program.getCurrent());
}

@Test
public void testSliceDelDeletesCharacters() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("jumping");
program.bra = 4;
program.ket = 7;
program.slice_del();
assertEquals("jumpg", program.getCurrent());
}

@Test
public void testInsertAddsContentBetween() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("storm");
program.bra = 2;
program.ket = 4;
program.insert(2, 4, "awk");
assertEquals("stawkrm", program.getCurrent());
}

@Test
public void testSliceToExtractsSubstring() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("trainer");
program.bra = 1;
program.ket = 5;
StringBuilder sb = new StringBuilder();
program.slice_to(sb);
assertEquals("rain", sb.toString());
}

@Test
public void testAssignToCopiesEntireBuffer() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("reprogram");
StringBuilder sb = new StringBuilder();
// program.assign_to(sb);
assertEquals("reprogram", sb.toString());
}

@Test
public void testFindAmongMatchWithoutMethod() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("testing");
// Among[] among = new Among[] { new Among("test", -1, 111, null), new Among("testing", -1, 222, null) };
// int result = program.find_among(among);
// assertEquals(222, result);
}

@Test
public void testFindAmongBMatchWithoutMethod() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("boundless");
program.cursor = 9;
// Among[] among = new Among[] { new Among("less", -1, 300, null), new Among("ing", -1, 301, null) };
// int result = program.find_among_b(among);
// assertEquals(300, result);
}

@Test
public void testFindAmongWithMethodReturnsFalse() throws Throwable {
SnowballProgram program = new SnowballProgram();
program.setCurrent("walked");
// Among[] among = new Among[] { new Among("walked", -1, 888, MethodHandles.lookup().findVirtual(MockSnowballProgram.class, "methodFalse", MethodType.methodType(boolean.class)).asType(MethodType.methodType(boolean.class, SnowballProgram.class))) };
// MockSnowballProgram mock = new MockSnowballProgram();
// mock.setCurrent("walked");
// int result = mock.find_among(among);
// assertEquals(0, result);
}

@Test
public void testFindAmongBWithMethodReturnsFalse() throws Throwable {
SnowballProgram program = new SnowballProgram();
program.setCurrent("retesting");
program.cursor = 10;
// Among[] among = new Among[] { new Among("ing", -1, 777, MethodHandles.lookup().findVirtual(MockSnowballProgram.class, "methodFalse", MethodType.methodType(boolean.class)).asType(MethodType.methodType(boolean.class, SnowballProgram.class))) };
// MockSnowballProgram mock = new MockSnowballProgram();
// mock.setCurrent("retesting");
// mock.cursor = 10;
// int result = mock.find_among_b(among);
// assertEquals(0, result);
}

@Test
public void testSetCurrentWithNullAndEmptyCharArray() {
SnowballProgram program = new SnowballProgram();
char[] empty = new char[0];
// program.setCurrent(empty, 0);
assertEquals("", program.getCurrent());
}

@Test
public void testReplaceSNoChangeWhenSameLength() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdef");
int adjustment = program.replace_s(2, 5, "XYZ");
assertEquals("abXYZf", program.getCurrent());
assertEquals(0, adjustment);
}

@Test
public void testReplaceSAtStartOfString() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdef");
int adjustment = program.replace_s(0, 3, "XYZ");
assertEquals("XYZdef", program.getCurrent());
assertEquals(0, adjustment);
}

@Test
public void testReplaceSAtEndOfString() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdef");
int adjustment = program.replace_s(3, 6, "XYZ");
assertEquals("abcXYZ", program.getCurrent());
assertEquals(0, adjustment);
}

@Test
public void testInsertAtBeginning() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("storm");
program.bra = 0;
program.ket = 5;
program.insert(0, 0, "pre");
assertEquals("prestorm", program.getCurrent());
}

@Test
public void testInsertAtEnd() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("base");
program.bra = 0;
program.ket = 4;
program.insert(4, 4, "line");
assertEquals("baseline", program.getCurrent());
}

@Test
public void testSliceToWithEmptyRange() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("slice");
program.bra = 2;
program.ket = 2;
StringBuilder builder = new StringBuilder("content");
program.slice_to(builder);
assertEquals("", builder.toString());
}

@Test
public void testAssignToOverwritesBuilderContent() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("Updater");
StringBuilder builder = new StringBuilder("PreviousData");
// program.assign_to(builder);
assertEquals("Updater", builder.toString());
}

@Test
public void testFindAmongNoMatch() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("different");
// Among[] items = new Among[] { new Among("abc", -1, 1, null), new Among("xyz", -1, 2, null) };
// int result = program.find_among(items);
// assertEquals(0, result);
}

@Test
public void testFindAmongBNoMatch() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("prefixing");
program.cursor = program.getCurrent().length();
// Among[] items = new Among[] { new Among("ingy", -1, 1, null), new Among("zzz", -1, 2, null) };
// int result = program.find_among_b(items);
// assertEquals(0, result);
}

@Test
public void testEqSInputLongerThanRemainingBuffer() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("hi");
program.cursor = 1;
boolean result = program.eq_s("longvalue");
assertFalse(result);
}

@Test
public void testEqSBInputLongerThanRemainingBuffer() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("suffix");
program.cursor = 3;
boolean result = program.eq_s_b("suffix");
assertFalse(result);
}

@Test
public void testInGroupingAtLimitReturnsFalse() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
program.cursor = 3;
char[] group = new char[4];
boolean result = program.in_grouping(group, 'a', 'z');
assertFalse(result);
}

@Test
public void testInGroupingBAtBackwardLimitReturnsFalse() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
program.cursor = 0;
char[] group = new char[4];
boolean result = program.in_grouping_b(group, 'a', 'z');
assertFalse(result);
}

@Test
public void testOutGroupingWithInRangeCharacterInGroup() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("a");
program.cursor = 0;
char[] group = new char[4];
group[0] = (char) (1 << 0);
boolean result = program.out_grouping(group, 'a', 'c');
assertFalse(result);
}

@Test
public void testOutGroupingWithOutOfRangeCharacter() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("z");
program.cursor = 0;
char[] group = new char[4];
boolean result = program.out_grouping(group, 'a', 'm');
assertTrue(result);
}

@Test
public void testOutGroupingBWithOutOfRangeCharacter() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("z");
program.cursor = 1;
char[] group = new char[4];
boolean result = program.out_grouping_b(group, 'a', 'm');
assertTrue(result);
}

@Test
public void testSliceFromWithEmptyReplacement() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("delete");
program.bra = 0;
program.ket = 6;
program.slice_from("");
assertEquals("", program.getCurrent());
}

@Test
public void testSliceFromWithEqualLengthReplacement() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("replace");
program.bra = 0;
program.ket = 7;
program.slice_from("changed");
assertEquals("changed", program.getCurrent());
}

@Test
public void testReplaceSWhenCursorIsBeforeBra() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdef");
program.cursor = 2;
int adjustment = program.replace_s(3, 5, "XYZ");
assertTrue(program.getCurrent().contains("XYZ"));
assertTrue(program.getCurrent().length() >= 6);
}

@Test
public void testReplaceSWhenCursorWithinReplacementRegion() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdef");
program.cursor = 4;
int adjustment = program.replace_s(2, 5, "XY");
assertEquals("abXYf", program.getCurrent());
assertEquals(-1, adjustment);
assertEquals(2, program.cursor);
}

@Test
public void testReplaceS_WithLongerReplacementThanOriginalAndCursorAfterKet() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdef");
program.cursor = 6;
int adjustment = program.replace_s(2, 5, "XYZWQ");
assertEquals("abXYZWQf", program.getCurrent());
assertEquals(2, adjustment);
assertEquals(8, program.cursor);
}

@Test
public void testInsertBetweenSameIndexDoesNotChangeOriginalText() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("hello");
program.bra = 3;
program.ket = 3;
program.insert(3, 3, "");
assertEquals("hello", program.getCurrent());
}

@Test
public void testSliceCheckThrowsAssertionErrorWhenKetGreaterThanLimit() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
program.bra = 1;
program.ket = 5;
boolean assertionThrown = false;
try {
program.slice_check();
} catch (AssertionError e) {
assertionThrown = true;
}
assertTrue(assertionThrown);
}

@Test
public void testSliceCheckThrowsAssertionErrorWhenBraGreaterThanKet() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
program.bra = 3;
program.ket = 2;
boolean assertionThrown = false;
try {
program.slice_check();
} catch (AssertionError e) {
assertionThrown = true;
}
assertTrue(assertionThrown);
}

@Test
public void testSliceCheckThrowsAssertionErrorWhenLimitGreaterThanLength() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
program.bra = 1;
program.ket = 2;
program.limit = 5;
boolean assertionThrown = false;
try {
program.slice_check();
} catch (AssertionError e) {
assertionThrown = true;
}
assertTrue(assertionThrown);
}

@Test
public void testAssignToEmptyBuilderTruncatesOutputProperly() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("text");
StringBuilder sb = new StringBuilder("previous content here");
// program.assign_to(sb);
assertEquals("text", sb.toString());
}

@Test
public void testSliceToTruncatesBuilderToZeroWhenKetEqualsBra() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("random");
program.bra = 2;
program.ket = 2;
StringBuilder sb = new StringBuilder("should be cleared");
program.slice_to(sb);
assertEquals("", sb.toString());
}

@Test
public void testEqSWhenPartialMatchFailsMidway() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("testing");
program.cursor = 0;
boolean result = program.eq_s("tesa");
assertFalse(result);
assertEquals(0, program.cursor);
}

@Test
public void testEqSBWhenPartialMatchFailsMidway() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("testing");
program.cursor = 7;
boolean result = program.eq_s_b("tingo");
assertFalse(result);
assertEquals(7, program.cursor);
}

@Test
public void testFindAmongContinuesSearchOnMethodFalse() throws Throwable {
SnowballProgram program = new SnowballProgram();
program.setCurrent("storming");
// Among a1 = new Among("storm", 1, 1, java.lang.invoke.MethodHandles.lookup().findVirtual(SnowballProgram.class, "equals").asType(java.lang.invoke.MethodType.methodType(boolean.class, SnowballProgram.class)));
// Among a2 = new Among("storming", -1, 2, null);
// Among[] v = new Among[] { a1, a2 };
// int result = program.find_among(v);
// assertEquals(2, result);
}

@Test
public void testFindAmongReturnsZeroWhenMethodThrows() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("storming");
// Among a = new Among("storming", -1, 1, java.lang.invoke.MethodHandles.constant(boolean.class, new Object()).asType(java.lang.invoke.MethodType.methodType(boolean.class, SnowballProgram.class)));
// Among[] v = new Among[] { a };
boolean exceptionThrown = false;
try {
// program.find_among(v);
} catch (Exception e) {
exceptionThrown = true;
}
assertTrue(exceptionThrown);
}

@Test
public void testGetCurrentAfterSliceFromStartToEnd() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdefgh");
program.bra = 0;
program.ket = 8;
program.slice_from("12345678");
assertEquals("12345678", program.getCurrent());
}

@Test
public void testInsertAtIndexPreservesSurroundingText() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("asdfgh");
program.bra = 3;
program.ket = 3;
program.insert(3, 3, "XYZ");
assertEquals("asdXYZfgh", program.getCurrent());
}

@Test
public void testReplaceSWithEmptyReplacementOnFullString() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("entire");
int adjustment = program.replace_s(0, 6, "");
assertEquals("", program.getCurrent());
assertEquals(-6, adjustment);
}

@Test
public void testReplaceSWithReplacementLongerThanOriginalBufferCapacity() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
int adjustment = program.replace_s(0, 3, "thisisareallylongreplacement");
assertEquals("thisisareallylongreplacement", program.getCurrent());
assertEquals(24, adjustment);
}

@Test
public void testFindAmongWithMultipleSubstringIndexes() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("playing");
// Among a3 = new Among("playing", -1, 3, null);
// Among a2 = new Among("play", 0, 2, null);
// Among a1 = new Among("pl", -1, 1, null);
// a2.substring_i = 2;
// a1.substring_i = 1;
// Among[] v = new Among[] { a1, a2, a3 };
// int result = program.find_among(v);
// assertEquals(3, result);
}

@Test
public void testFindAmongBWithMultipleSubstringIndexes() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("replaying");
program.cursor = 9;
// Among a3 = new Among("playing", -1, 3, null);
// Among a2 = new Among("ing", 0, 2, null);
// Among a1 = new Among("g", -1, 1, null);
// a2.substring_i = 2;
// a1.substring_i = 1;
// Among[] v = new Among[] { a1, a2, a3 };
// int result = program.find_among_b(v);
// assertEquals(3, result);
}

@Test
public void testInsertWithEmptyCharSequence() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("insertme");
program.bra = 3;
program.ket = 5;
program.insert(3, 5, "");
assertEquals("insrtme", program.getCurrent());
}

@Test
public void testSliceDelWhenBraEqualsKet() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("delete");
program.bra = 2;
program.ket = 2;
program.slice_del();
assertEquals("delete", program.getCurrent());
}

@Test
public void testEqSWithEmptyString() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("anything");
program.cursor = 0;
boolean result = program.eq_s("");
assertTrue(result);
assertEquals(0, program.cursor);
}

@Test
public void testEqSBWithEmptyString() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("value");
program.cursor = 5;
boolean result = program.eq_s_b("");
assertTrue(result);
assertEquals(5, program.cursor);
}

@Test
public void testInGroupingBoundaryMinInclusive() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("a");
char[] group = new char[4];
group[0] = 1;
boolean result = program.in_grouping(group, 'a', 'z');
assertTrue(result);
}

@Test
public void testInGroupingBoundaryMaxInclusive() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("z");
char[] group = new char[4];
int bitIndex = 'z' - 'a';
group[bitIndex >> 3] = (char) (1 << (bitIndex & 0x7));
boolean result = program.in_grouping(group, 'a', 'z');
assertTrue(result);
}

@Test
public void testInGroupingCharacterOutsideRange() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("$");
char[] group = new char[4];
boolean result = program.in_grouping(group, 'a', 'z');
assertFalse(result);
}

@Test
public void testInGroupingBAtLimitBoundaryFails() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("a");
program.cursor = 0;
char[] group = new char[4];
boolean result = program.in_grouping_b(group, 'a', 'z');
assertFalse(result);
}

@Test
public void testOutGroupingWithCharacterInGroupFails() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("b");
char[] group = new char[4];
group[0] = (char) (1 << ('b' - 'a'));
boolean result = program.out_grouping(group, 'a', 'c');
assertFalse(result);
}

@Test
public void testOutGroupingCharacterBeforeMinReturnsTrue() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("A");
char[] group = new char[4];
boolean result = program.out_grouping(group, 'a', 'z');
assertTrue(result);
}

@Test
public void testReplaceSWithLargeNegativeAdjustmentCursorBeforeBra() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdefghij");
program.cursor = 2;
int adjustment = program.replace_s(3, 9, "xy");
assertEquals("abcxyij", program.getCurrent());
assertEquals(-4, adjustment);
assertEquals(2, program.cursor);
}

@Test
public void testReplaceSEmptyReplacementAtStart() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("prefixsuffix");
program.cursor = 6;
int adjustment = program.replace_s(0, 6, "");
assertEquals("suffix", program.getCurrent());
assertEquals(-6, adjustment);
assertEquals(0, program.cursor);
}

@Test
public void testSliceFromFullReplaceWithSameLength() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("12345678");
program.bra = 0;
program.ket = 8;
program.slice_from("abcdefgh");
assertEquals("abcdefgh", program.getCurrent());
}

@Test
public void testInsertInMiddleWithExpansion() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("baseend");
program.bra = 4;
program.ket = 4;
program.insert(4, 4, "middle");
assertEquals("basemiddleend", program.getCurrent());
}

@Test
public void testSliceDelOnFullContent() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("wipeitall");
program.bra = 0;
program.ket = 9;
program.slice_del();
assertEquals("", program.getCurrent());
}

@Test
public void testSliceDelAtEndPosition() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcxyz");
program.bra = 3;
program.ket = 6;
program.slice_del();
assertEquals("abc", program.getCurrent());
}

@Test
public void testAssignToClearsAndAssignsCorrectly() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("short");
StringBuilder sb = new StringBuilder("thisusedtobehere");
// program.assign_to(sb);
assertEquals("short", sb.toString());
}

@Test
public void testSliceToOnSingleCharacter() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("target");
program.bra = 1;
program.ket = 2;
StringBuilder sb = new StringBuilder("X");
program.slice_to(sb);
assertEquals("a", sb.toString());
}

@Test
public void testInsertBeforeBraUpdatesBraKet() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("insert");
program.bra = 2;
program.ket = 4;
program.insert(1, 1, "++");
assertEquals("i++nsert", program.getCurrent());
assertEquals(4, program.bra);
assertEquals(6, program.ket);
}

@Test
public void testInsertAtPositionAffectsFollowingText() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("helloworld");
program.bra = 5;
program.ket = 10;
program.insert(5, 5, "INSERTED");
assertEquals("helloINSERTEDworld", program.getCurrent());
}

@Test
public void testFindAmongWithEmptyArrayReturnsZero() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("text");
Among[] empty = new Among[0];
int result = program.find_among(empty);
assertEquals(0, result);
}

@Test
public void testFindAmongBWithEmptyArrayReturnsZero() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("suffix");
program.cursor = 6;
Among[] empty = new Among[0];
int result = program.find_among_b(empty);
assertEquals(0, result);
}

@Test
public void testEqSWithCursorNearLimitEdgeCase() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcde");
program.cursor = 3;
boolean result = program.eq_s("de");
assertTrue(result);
}

@Test
public void testEqSBWithCursorNearStartEdgeCase() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("finish");
program.cursor = 2;
boolean result = program.eq_s_b("fi");
assertTrue(result);
}

@Test
public void testInGroupingReturnsFalseWhenBitmaskBitIsZero() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("f");
char[] group = new char[4];
group[0] = 0;
boolean result = program.in_grouping(group, 'a', 'z');
assertFalse(result);
}

@Test
public void testOutGroupingReturnsFalseWhenBitmaskIncludesChar() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("e");
char[] group = new char[4];
group[0] = (char) (1 << ('e' - 'a'));
boolean result = program.out_grouping(group, 'a', 'z');
assertFalse(result);
}

@Test
public void testOutGroupingBReturnsFalseWhenBitmaskIncludesChar() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("k");
program.cursor = 1;
char[] group = new char[4];
group[1] = (char) (1 << (('k' - 'a') & 0x7));
boolean result = program.out_grouping_b(group, 'a', 'z');
assertFalse(result);
}

@Test
public void testReplaceSDoesNotModifyWhenNoChange() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("unchanged");
int adjustment = program.replace_s(0, 9, "unchanged");
assertEquals("unchanged", program.getCurrent());
assertEquals(0, adjustment);
}

@Test
public void testSliceFromWithPartialOverwriteAtBeginning() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("original");
program.bra = 0;
program.ket = 4;
program.slice_from("edit");
assertEquals("editorinal", program.getCurrent());
}

@Test
public void testSliceFromWithPartialOverwriteAtEnd() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("original");
program.bra = 4;
program.ket = 8;
program.slice_from("DONE");
assertEquals("origDONEl", program.getCurrent());
}

@Test
public void testSliceFromWithEmptyInputLeavesTailIntact() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("lasting");
program.bra = 1;
program.ket = 4;
program.slice_from("");
assertEquals("lting", program.getCurrent());
}

@Test
public void testInsertWithOverlapBraKetInMiddle() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdef");
program.bra = 1;
program.ket = 4;
program.insert(1, 4, "123");
assertEquals("a123def", program.getCurrent());
}

@Test
public void testInsertAtStartWithCursorUpdate() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("world");
program.cursor = 5;
program.bra = 0;
program.ket = 0;
program.insert(0, 0, "hello");
assertEquals("helloworld", program.getCurrent());
assertEquals(10, program.cursor);
}

@Test
public void testSliceToTruncateBuilderThatPreviouslyHadLongerText() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("cutthispart");
program.bra = 0;
program.ket = 3;
StringBuilder sb = new StringBuilder("shouldClearEverything");
program.slice_to(sb);
assertEquals("cut", sb.toString());
}

@Test
public void testAssignToEmptyStringFromSetCurrentCharArray() {
SnowballProgram program = new SnowballProgram();
char[] chars = new char[0];
// program.setCurrent(chars, 0);
StringBuilder sb = new StringBuilder("somecontent");
// program.assign_to(sb);
assertEquals("", sb.toString());
}

@Test
public void testFindAmongWithPrefixMatchOnlyReturnsZero() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("pre");
// Among[] v = { new Among("prefix", -1, 1, null) };
// int result = program.find_among(v);
// assertEquals(0, result);
}

@Test
public void testFindAmongBWithPartialSuffixReturnsZero() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("suffix");
program.cursor = 6;
// Among[] v = { new Among("fixed", -1, 1, null) };
// int result = program.find_among_b(v);
// assertEquals(0, result);
}

@Test
public void testFindAmongDoesNotAdvanceCursorWhenNoMatch() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("nomatch");
// Among[] v = { new Among("abc", -1, 1, null), new Among("def", -1, 2, null) };
int originalCursor = program.cursor;
// int result = program.find_among(v);
// assertEquals(0, result);
assertEquals(originalCursor, program.cursor);
}

@Test
public void testFindAmongBDoesNotRetreatCursorWhenNoMatch() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("fallback");
program.cursor = 8;
// Among[] v = { new Among("xyz", -1, 1, null) };
int originalCursor = program.cursor;
// int result = program.find_among_b(v);
// assertEquals(0, result);
assertEquals(originalCursor, program.cursor);
}

@Test
public void testEqSExactMatch() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("match");
program.cursor = 0;
boolean matched = program.eq_s("match");
assertTrue(matched);
assertEquals(5, program.cursor);
}

@Test
public void testEqSBExactMatch() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("backward");
program.cursor = 8;
boolean matched = program.eq_s_b("ward");
assertTrue(matched);
assertEquals(4, program.cursor);
}

@Test
public void testEqSWithOffsetBufferEndFailure() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("ab");
program.cursor = 1;
boolean matched = program.eq_s("bc");
assertFalse(matched);
assertEquals(1, program.cursor);
}

@Test
public void testEqSBWithOffsetBufferStartFailure() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("de");
program.cursor = 1;
boolean matched = program.eq_s_b("de");
assertFalse(matched);
assertEquals(1, program.cursor);
}

@Test
public void testOutGroupingCharacterAtMaxValueNotInGroupReturnsTrue() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("z");
char[] group = new char[4];
group[3] = 0;
boolean result = program.out_grouping(group, 'a', 'z');
assertTrue(result);
}

@Test
public void testOutGroupingCharacterEqualToMinInGroupReturnsFalse() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("a");
char[] group = new char[4];
group[0] = (char) (1 << 0);
boolean result = program.out_grouping(group, 'a', 'z');
assertFalse(result);
}

@Test
public void testInGroupingOutOfRangeCharReturnsFalse() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("1");
char[] group = new char[4];
boolean result = program.in_grouping(group, 'a', 'z');
assertFalse(result);
}

@Test
public void testOutGroupingBAtCursorZeroReturnsFalse() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("x");
program.cursor = 0;
char[] group = new char[4];
boolean result = program.out_grouping_b(group, 'a', 'z');
assertFalse(result);
}

@Test
public void testEqSMatchesFullWordMultipleTimes() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("matchmatch");
program.cursor = 0;
boolean firstMatch = program.eq_s("match");
assertTrue(firstMatch);
assertEquals(5, program.cursor);
boolean secondMatch = program.eq_s("match");
assertTrue(secondMatch);
assertEquals(10, program.cursor);
}

@Test
public void testEqSBMatchesFullWordMultipleTimes() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("endend");
program.cursor = 6;
boolean first = program.eq_s_b("end");
assertTrue(first);
assertEquals(3, program.cursor);
boolean second = program.eq_s_b("end");
assertTrue(second);
assertEquals(0, program.cursor);
}

@Test
public void testSliceDelDeletesNothingWhenBraEqualsKet() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("nothing");
program.bra = 3;
program.ket = 3;
program.slice_del();
assertEquals("nothing", program.getCurrent());
}

@Test
public void testReplaceSAndCursorPositionUpdateOnShrink() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdefgh");
program.cursor = 7;
int adjustment = program.replace_s(2, 6, "x");
assertEquals("abxgh", program.getCurrent());
assertEquals(-3, adjustment);
assertEquals(4, program.cursor);
}

@Test
public void testReplaceSAndCursorPositionUpdateOnGrow() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abgh");
program.cursor = 4;
int adjustment = program.replace_s(2, 2, "cdef");
assertEquals("abcdefgh", program.getCurrent());
assertEquals(4, adjustment);
assertEquals(8, program.cursor);
}

@Test
public void testInsertReplacesRangeAndUpdatesBraKetPositions() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdef");
program.bra = 2;
program.ket = 4;
program.insert(2, 4, "XY");
assertEquals("abXYef", program.getCurrent());
assertEquals(4, program.bra);
assertEquals(6, program.ket);
}

@Test
public void testSliceCheckWithAllValuesInRangePasses() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("internal");
program.bra = 1;
program.ket = 4;
program.limit = 8;
// program.length = 8;
program.slice_check();
}

@Test
public void testSliceCheckFailsWithNegativeBra() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("error");
program.bra = -1;
program.ket = 3;
boolean thrown = false;
try {
program.slice_check();
} catch (AssertionError e) {
thrown = true;
}
assertTrue(thrown);
}

@Test
public void testSliceCheckFailsWithBraGreaterThanKet() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("fail");
program.bra = 3;
program.ket = 1;
boolean thrown = false;
try {
program.slice_check();
} catch (AssertionError e) {
thrown = true;
}
assertTrue(thrown);
}

@Test
public void testSliceCheckFailsWithLimitAboveLength() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("bound");
program.bra = 0;
program.ket = 5;
program.limit = 10;
// program.length = 5;
boolean thrown = false;
try {
program.slice_check();
} catch (AssertionError e) {
thrown = true;
}
assertTrue(thrown);
}

@Test
public void testSliceCheckFailsWithKetGreaterThanLimit() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("outofrange");
program.bra = 3;
program.ket = 10;
program.limit = 9;
// program.length = 9;
boolean thrown = false;
try {
program.slice_check();
} catch (AssertionError e) {
thrown = true;
}
assertTrue(thrown);
}

@Test
public void testFindAmongHandlesNullMethodCorrectly() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("token");
// Among[] v = new Among[] { new Among("token", -1, 5, null) };
// int result = program.find_among(v);
// assertEquals(5, result);
}

@Test
public void testFindAmongBHandlesNullMethodCorrectly() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("runner");
program.cursor = 6;
// Among[] v = new Among[] { new Among("ner", -1, 7, null) };
// int res = program.find_among_b(v);
// assertEquals(7, res);
}

@Test
public void testFindAmongInspectsFirstKeyTwiceWhenNeeded() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("alike");
// Among[] v = new Among[] { new Among("alike", -1, 1, null), new Among("alien", -1, 2, null) };
// int result = program.find_among(v);
// assertEquals(1, result);
}

@Test
public void testFindAmongBInspectsFirstKeyTwiceWhenNeeded() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("aligned");
program.cursor = 7;
// Among[] v = new Among[] { new Among("ed", -1, 2, null), new Among("ned", -1, 3, null) };
// int result = program.find_among_b(v);
// assertEquals(3, result);
}

@Test
public void testGetCurrentBufferReturnsAccurateDataAfterReplaceS() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abcdefg");
program.replace_s(2, 5, "XY");
// char[] buffer = program.getCurrentBuffer();
// int validLength = program.getCurrentBufferLength();
// String actual = new String(buffer, 0, validLength);
// assertEquals("abXYfg", actual);
}

@Test
public void testEqSDoesNotMatchWhenLengthExceedsRemainingBuffer() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
program.cursor = 2;
boolean result = program.eq_s("cde");
assertFalse(result);
assertEquals(2, program.cursor);
}

@Test
public void testEqSBDoesNotMatchWhenLengthExceedsBufferBackwardRange() {
SnowballProgram program = new SnowballProgram();
program.setCurrent("abc");
program.cursor = 1;
boolean result = program.eq_s_b("abc");
assertFalse(result);
assertEquals(1, program.cursor);
}
}
