public class SnowballProgram_wosr_1_GPTLLMTest { 

 @Test
  public void testSetAndGetCurrentString() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("stemming");
    String result = program.getCurrent();
    assertEquals("stemming", result);
    assertNotNull(program.getCurrentBuffer());
    assertEquals("stemming".length(), program.getCurrentBufferLength());
  }
@Test
  public void testSetCurrentWithCharArray() {
    SnowballProgram program = new SnowballProgram();
    char[] input = {'s', 't', 'e', 'm'};
    program.setCurrent(input, 4);
    assertEquals("stem", program.getCurrent());
    assertEquals(4, program.getCurrentBufferLength());
  }
@Test
  public void testCopyConstructor() {
    SnowballProgram base = new SnowballProgram();
    base.setCurrent("copy");
    SnowballProgram copy = new SnowballProgram(base);
    assertEquals("copy", copy.getCurrent());
  }
@Test
  public void testCopyFrom() {
    SnowballProgram source = new SnowballProgram();
    source.setCurrent("source");
    SnowballProgram target = new SnowballProgram();
    target.copy_from(source);
    assertEquals("source", target.getCurrent());
  }
@Test
  public void testInGroupingReturnsTrue() {
    SnowballProgram program = new SnowballProgram();
    char[] pattern = new char[4];
    pattern[0] = (char) (1 << 0); 
    program.setCurrent("abc");
    boolean result = program.in_grouping(pattern, 'a', 'd');
    assertTrue(result);
  }
@Test
  public void testInGroupingReturnsFalseWhenCursorAtLimit() {
    SnowballProgram program = new SnowballProgram();
    char[] pattern = new char[4];
    program.setCurrent("z");
    program.cursor = 1;
    boolean result = program.in_grouping(pattern, 'a', 'z');
    assertFalse(result);
  }
@Test
  public void testOutGroupingReturnsTrueForOutsideRange() {
    SnowballProgram program = new SnowballProgram();
    char[] pattern = new char[4];
    program.setCurrent("1z");
    boolean result = program.out_grouping(pattern, 'a', 'z');
    assertTrue(result);
  }
@Test
  public void testOutGroupingReturnsFalseForInsideRangeAndSetBit() {
    SnowballProgram program = new SnowballProgram();
    char[] pattern = new char[4];
    pattern[0] = (char) (1 << 0); 
    program.setCurrent("a");
    boolean result = program.out_grouping(pattern, 'a', 'd');
    assertFalse(result);
  }
@Test
  public void testEqSMatches() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("hello");
    boolean result = program.eq_s("hell");
    assertTrue(result);
    assertEquals(4, program.cursor);
  }
@Test
  public void testEqSDoesNotMatch() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("hello");
    boolean result = program.eq_s("help");
    assertFalse(result);
    assertEquals(0, program.cursor);
  }
@Test
  public void testEqSBMatches() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("hello");
    program.cursor = 5;
    boolean result = program.eq_s_b("llo");
    assertTrue(result);
    assertEquals(2, program.cursor);
  }
@Test
  public void testEqSBDoesNotMatch() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("hello");
    program.cursor = 5;
    boolean result = program.eq_s_b("llll");
    assertFalse(result);
    assertEquals(5, program.cursor);
  }
@Test
  public void testReplaceSShorterReplacement() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("testing");
    int adjustment = program.replace_s(4, 7, "ed"); 
    assertEquals("testedg", program.getCurrent());
    assertEquals(7, program.getCurrentBufferLength());
    assertEquals(2, adjustment - 1); 
  }
@Test
  public void testReplaceSLongerReplacement() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("play");
    int adjustment = program.replace_s(0, 4, "playing");
    assertEquals("playing", program.getCurrent());
    assertEquals(7, program.getCurrentBufferLength());
    assertEquals(3, adjustment);
  }
@Test
  public void testSliceDelOnFullWord() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("cleaning");
    program.bra = 0;
    program.ket = 8;
    program.slice_del();
    assertEquals("", program.getCurrent());
  }
@Test
  public void testSliceFromWithinWord() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("driving");
    program.bra = 4;
    program.ket = 7;
    program.slice_from("ive");
    assertEquals("driviveg", program.getCurrent());
  }
@Test
  public void testInsertInMiddle() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("wrong");
    program.bra = 1;
    program.ket = 4;
    program.insert(1, 1, "ri");
    assertEquals("wrirong", program.getCurrent());
  }
@Test
  public void testSliceToExtractPortion() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("segment");
    program.bra = 3;
    program.ket = 6;
    StringBuilder sb = new StringBuilder();
    program.slice_to(sb);
    assertEquals("men", sb.toString());
  }
@Test
  public void testAssignToFullContent() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("convert");
    StringBuilder sb = new StringBuilder("XXXX");
    program.assign_to(sb);
    assertEquals("convert", sb.toString());
  }
@Test
  public void testInGroupingBackwardsReturnsTrue() {
    SnowballProgram program = new SnowballProgram();
    char[] pattern = new char[4];
    pattern[0] = (char) (1 << 0); 
    program.setCurrent("ba");
    program.cursor = 2;
    boolean result = program.in_grouping_b(pattern, 'a', 'z');
    assertTrue(result);
    assertEquals(1, program.cursor);
  }
@Test
  public void testOutGroupingBackwardsReturnsTrue() {
    SnowballProgram program = new SnowballProgram();
    char[] pattern = new char[4];
    pattern[0] = (char) (1 << 1); 
    program.setCurrent("c");
    program.cursor = 1;
    boolean result = program.out_grouping_b(pattern, 'a', 'c');
    assertTrue(result);
  }
@Test
  public void testFindAmongSingleMatchWithNoMethod() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("test");
    Among among = new Among("te", -1, 42, null);
    int result = program.find_among(new Among[]{among});
    assertEquals(42, result);
  }
@Test
  public void testFindAmong_bSingleMatchWithNoMethod() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("ed");
    program.cursor = 2;
    Among among = new Among("ed", -1, 9, null);
    int result = program.find_among_b(new Among[]{among});
    assertEquals(9, result);
  }
@Test
  public void testFindAmongWithMethodReturnsTrue() throws Throwable {
    SnowballProgram program = new SnowballProgram();
    MethodHandle method = MethodHandles.lookup()
        .findVirtual(SnowballProgram.class, "dummyTrueMethod", MethodType.methodType(boolean.class));
    Among[] v = new Among[]{
        new Among("xx", -1, 7, method)
    };
    program.setCurrent("xx");
    int result = program.find_among(v);
    assertEquals(7, result);
  }
@Test
  public void testFindAmongWithMethodReturnsFalse() throws Throwable {
    SnowballProgram program = new SnowballProgram();
    MethodHandle method = MethodHandles.lookup()
        .findVirtual(SnowballProgram.class, "dummyFalseMethod", MethodType.methodType(boolean.class));
    Among[] v = new Among[]{
        new Among("yy", -1, 7, method)
    };
    program.setCurrent("yy");
    int result = program.find_among(v);
    assertEquals(0, result);
  } 
}