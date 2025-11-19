public class SnowballProgram_wosr_2_GPTLLMTest { 

 @Test
  public void testSetAndGetCurrentString() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("running");
    assertEquals("running", program.getCurrent());
  }
@Test
  public void testSetCurrentWithCharArray() {
    SnowballProgram program = new SnowballProgram();
    char[] input = {'t', 'e', 's', 't'};
    program.setCurrent(input, 4);
    assertEquals("test", program.getCurrent());
    assertEquals(4, program.getCurrentBufferLength());
  }
@Test
  public void testGetCurrentBufferReturnsSameContentAsGetCurrent() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("example");
    assertEquals("example", new String(program.getCurrentBuffer(), 0, program.getCurrentBufferLength()));
  }
@Test
  public void testCopyConstructorCopiesStateCorrectly() {
    SnowballProgram original = new SnowballProgram();
    original.setCurrent("copytest");

    SnowballProgram copy = new SnowballProgram(original);
    assertEquals(original.getCurrent(), copy.getCurrent());
    assertEquals(original.getCurrentBufferLength(), copy.getCurrentBufferLength());
  }
@Test
  public void testCopyFromCopiesStateCorrectly() {
    SnowballProgram original = new SnowballProgram();
    original.setCurrent("input");
    SnowballProgram target = new SnowballProgram();
    target.copy_from(original);
    assertEquals("input", target.getCurrent());
  }
@Test
  public void testInGroupingTrueAdvanceCursor() {
    SnowballProgram program = new SnowballProgram();
    char[] chars = "abc".toCharArray();
    program.setCurrent(chars, chars.length);
    char[] grouping = {0b00000111}; 
    boolean result = program.in_grouping(grouping, 'a', 'c');
    assertTrue(result);
    assertEquals(1, program.cursor);
  }
@Test
  public void testInGroupingFalseNoAdvanceCursor() {
    SnowballProgram program = new SnowballProgram();
    char[] chars = "def".toCharArray();
    program.setCurrent(chars, chars.length);
    char[] grouping = {0b00000111}; 
    boolean result = program.in_grouping(grouping, 'a', 'c');
    assertFalse(result);
    assertEquals(0, program.cursor);
  }
@Test
  public void testInGroupingBTrueDecrementCursor() {
    SnowballProgram program = new SnowballProgram();
    char[] chars = "abc".toCharArray();
    program.setCurrent(chars, chars.length);
    program.cursor = 3;
    char[] grouping = {0b00000111}; 
    boolean result = program.in_grouping_b(grouping, 'a', 'c');
    assertTrue(result);
    assertEquals(2, program.cursor);
  }
@Test
  public void testOutGroupingTrueAdvanceCursor() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("x", 1);
    char[] grouping = {0b00000111}; 
    boolean result = program.out_grouping(grouping, 'a', 'c');
    assertTrue(result);
    assertEquals(1, program.cursor);
  }
@Test
  public void testOutGroupingFalseNoAdvanceCursor() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("a", 1);
    char[] grouping = {0b00000001}; 
    boolean result = program.out_grouping(grouping, 'a', 'a');
    assertFalse(result);
    assertEquals(0, program.cursor);
  }
@Test
  public void testEqSMatch() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("match");
    boolean result = program.eq_s("mat");
    assertTrue(result);
    assertEquals(3, program.cursor);
  }
@Test
  public void testEqSNoMatch() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("hello");
    boolean result = program.eq_s("world");
    assertFalse(result);
    assertEquals(0, program.cursor);
  }
@Test
  public void testEqSBMatch() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("backward", 8);
    program.cursor = 8;
    boolean result = program.eq_s_b("ward");
    assertTrue(result);
    assertEquals(4, program.cursor);
  }
@Test
  public void testEqSBNoMatch() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("backward", 8);
    program.cursor = 8;
    boolean result = program.eq_s_b("fail");
    assertFalse(result);
    assertEquals(8, program.cursor);
  }
@Test
  public void testReplaceSWithSameLength() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("test");
    int result = program.replace_s(1, 3, "oo");
    assertEquals(0, result);
    assertEquals("toost", program.getCurrent());
  }
@Test
  public void testReplaceSWithLonger() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("foo");
    int result = program.replace_s(1, 2, "oooo");
    assertEquals(3, result);
    assertEquals("foooo", program.getCurrent());
  }
@Test
  public void testReplaceSWithShorter() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("abcd");
    int result = program.replace_s(1, 3, "x");
    assertEquals(-1, result);
    assertEquals("axd", program.getCurrent());
  }
@Test
  public void testSliceCheckNoException() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("check");
    program.bra = 1;
    program.ket = 4;
    program.cursor = 2;
    program.limit = program.length;
    program.slice_check(); 
  }
@Test
  public void testSliceFromReplacesSlice() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("abcdef");
    program.bra = 1;
    program.ket = 4;
    program.slice_from("xyz");
    assertEquals("axyzef", program.getCurrent());
  }
@Test
  public void testSliceDelRemovesSlice() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("delete");
    program.bra = 1;
    program.ket = 4;
    program.slice_del();
    assertEquals("dte", program.getCurrent());
  }
@Test
  public void testInsertAddsContentAndAdjustsOffsets() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("12345");
    program.bra = 1;
    program.ket = 3;
    program.insert(2, 3, "XY");
    assertEquals("12XY345", program.getCurrent());
    assertEquals(1, program.bra);
    assertEquals(5, program.ket);
  }
@Test
  public void testSliceToCopiesToBuilder() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("foobar");
    program.bra = 1;
    program.ket = 4;
    StringBuilder builder = new StringBuilder();
    program.slice_to(builder);
    assertEquals("oob", builder.toString());
  }
@Test
  public void testAssignToCopiesFullToBuilder() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("builder");
    StringBuilder builder = new StringBuilder();
    program.assign_to(builder);
    assertEquals("builder", builder.toString());
  }
@Test
  public void testFindAmongSimpleMatchWithoutMethod() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("test");
    Among[] v = {
        new Among("te", -1, 1, null, ""),
        new Among("test", -1, 2, null, "")
    };
    int result = program.find_among(v);
    assertEquals(2, result);
  }
@Test
  public void testFindAmongBMatchWithoutMethod() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("xyztest");
    program.cursor = 7;
    Among[] v = {
        new Among("est", -1, 1, null, ""),
        new Among("test", -1, 2, null, "")
    };
    int result = program.find_among_b(v);
    assertEquals(2, result);
    assertEquals(3, program.cursor);
  }
@Test
  public void testFindAmongWithInjectedMethod() throws Throwable {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("hello");
    Among[] v = {
        new Among("hello", -1, 42,
            MethodHandles.lookup().bind(new Stub1(), "invoke", MethodType.methodType(boolean.class, SnowballProgram.class)),
            "")
    };
    int result = program.find_among(v);
    assertEquals(42, result);
  } 
}