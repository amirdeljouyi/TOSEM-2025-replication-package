public class SnowballProgram_wosr_4_GPTLLMTest { 

 @Test
  public void testSetAndGetCurrentString() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("running");
    assertEquals("running", program.getCurrent());
  }
@Test
  public void testSetCurrentWithCharArray() {
    SnowballProgram program = new SnowballProgram();
    char[] content = "stemming".toCharArray();
    program.setCurrent(content, content.length);
    assertEquals("stemming", program.getCurrent());
  }
@Test
  public void testGetCurrentBufferAndLength() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("prefix");
    assertEquals('p', program.getCurrentBuffer()[0]);
    assertEquals(6, program.getCurrentBufferLength());
  }
@Test
  public void testInGrouping_True() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("a");
    program.cursor = 0;
    char[] group = new char[]{0};
    group[0] |= (1 << ('a' - 'a'));
    boolean result = program.in_grouping(group, 'a', 'a');
    assertTrue(result);
    assertEquals(1, program.cursor);
  }
@Test
  public void testInGrouping_False_OutOfRange() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("z");
    program.cursor = 0;
    char[] group = new char[1];
    boolean result = program.in_grouping(group, 'a', 'm');
    assertFalse(result);
    assertEquals(0, program.cursor);
  }
@Test
  public void testInGroupingB_True() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("b");
    program.cursor = 1;
    char[] group = new char[]{0};
    group[0] |= (1 << ('b' - 'b'));
    boolean result = program.in_grouping_b(group, 'b', 'b');
    assertTrue(result);
    assertEquals(0, program.cursor);
  }
@Test
  public void testOutGrouping_True() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("z");
    program.cursor = 0;
    char[] group = new char[1];
    boolean result = program.out_grouping(group, 'a', 'm');
    assertTrue(result);
    assertEquals(1, program.cursor);
  }
@Test
  public void testOutGrouping_False() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("a");
    program.cursor = 0;
    char[] group = new char[1];
    group[0] |= (1 << ('a' - 'a'));
    boolean result = program.out_grouping(group, 'a', 'a');
    assertFalse(result);
    assertEquals(0, program.cursor);
  }
@Test
  public void testEqS_True() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("abc");
    program.cursor = 0;
    boolean result = program.eq_s("ab");
    assertTrue(result);
    assertEquals(2, program.cursor);
  }
@Test
  public void testEqS_False() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("abc");
    program.cursor = 0;
    boolean result = program.eq_s("ac");
    assertFalse(result);
    assertEquals(0, program.cursor);
  }
@Test
  public void testEqSB_True() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("ending");
    program.cursor = 6;
    boolean result = program.eq_s_b("ing");
    assertTrue(result);
    assertEquals(3, program.cursor);
  }
@Test
  public void testEqSB_False() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("stop");
    program.cursor = 4;
    boolean result = program.eq_s_b("top");
    assertFalse(result);
    assertEquals(4, program.cursor);
  }
@Test
  public void testReplaceS_IncreaseLength() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("abc");
    int result = program.replace_s(1, 2, "xyz");
    assertEquals(2, result);
    assertEquals("axyzc", program.getCurrent());
  }
@Test
  public void testReplaceS_DecreaseLength() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("abcdef");
    int result = program.replace_s(2, 5, "X");
    assertEquals(-2, result);
    assertEquals("abXf", program.getCurrent());
  }
@Test
  public void testSliceDel() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("testing");
    program.bra = 1;
    program.ket = 4;
    program.slice_del();
    assertEquals("ting", program.getCurrent());
  }
@Test
  public void testSliceFrom() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("connect");
    program.bra = 3;
    program.ket = 7;
    program.slice_from("struct");
    assertEquals("construct", program.getCurrent());
  }
@Test
  public void testInsertAtBeginning() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("world");
    program.bra = 0;
    program.ket = 0;
    program.insert(0, 0, "hello ");
    assertEquals("hello world", program.getCurrent());
  }
@Test
  public void testInsertInMiddle() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("helloworld");
    program.bra = 5;
    program.ket = 5;
    program.insert(5, 5, " ");
    assertEquals("hello world", program.getCurrent());
  }
@Test
  public void testSliceToBuilder() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("mountain");
    program.bra = 0;
    program.ket = 5;
    StringBuilder sb = new StringBuilder();
    program.slice_to(sb);
    assertEquals("mount", sb.toString());
  }
@Test
  public void testAssignToBuilder() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("complete");
    StringBuilder sb = new StringBuilder();
    program.assign_to(sb);
    assertEquals("complete", sb.toString());
  }
@Test
  public void testCopyConstructorCreatesEquivalentState() {
    SnowballProgram original = new SnowballProgram();
    original.setCurrent("state");
    original.cursor = 3;
    original.bra = 1;
    original.ket = 4;

    SnowballProgram copy = new SnowballProgram(original);
    assertEquals(original.getCurrent(), copy.getCurrent());
    assertEquals(original.cursor, copy.cursor);
    assertEquals(original.bra, copy.bra);
    assertEquals(original.ket, copy.ket);
  }
@Test
  public void testCopyFromCopiesStateCorrectly() {
    SnowballProgram original = new SnowballProgram();
    original.setCurrent("example");
    original.cursor = 2;
    original.bra = 1;
    original.ket = 5;

    SnowballProgram copy = new SnowballProgram();
    copy.copy_from(original);

    assertEquals(original.getCurrent(), copy.getCurrent());
    assertEquals(original.cursor, copy.cursor);
    assertEquals(original.bra, copy.bra);
    assertEquals(original.ket, copy.ket);
  }
@Test
  public void testFindAmongMatchesDirectly() throws Throwable {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("run");
    Among[] among = {
      new Among("run", -1, 1, null)
    };
    int result = program.find_among(among);
    assertEquals(1, result);
  }
@Test
  public void testFindAmongWithMethodReturnsCorrectly() throws Throwable {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("abc");

    MethodHandle method = MethodHandles.constant(boolean.class, true)
        .asType(MethodType.methodType(boolean.class, SnowballProgram.class));

    Among[] among = {
        new Among("abc", -1, 42, method)
    };
    int result = program.find_among(among);
    assertEquals(42, result);
  }
@Test
  public void testFindAmongNotMatchingReturnsZero() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("xyz");
    Among[] among = {
        new Among("abc", -1, 1, null)
    };
    int result = program.find_among(among);
    assertEquals(0, result);
  }
@Test
  public void testFindAmongBMatches() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("xyz");
    program.cursor = 3;
    Among[] among = {
        new Among("xyz", -1, 10, null)
    };
    int result = program.find_among_b(among);
    assertEquals(10, result);
    assertEquals(0, program.cursor);
  }
@Test
  public void testFindAmongBWithMethodTrue() throws Throwable {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("xyz");
    program.cursor = 3;

    MethodHandle method = MethodHandles.constant(boolean.class, true)
        .asType(MethodType.methodType(boolean.class, SnowballProgram.class));

    Among[] among = {
        new Among("xyz", -1, 77, method)
    };
    int result = program.find_among_b(among);
    assertEquals(77, result);
  }
@Test
  public void testFindAmongBNotMatchedReturnsZero() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("open");
    program.cursor = 4;
    Among[] among = {
        new Among("close", -1, 5, null)
    };
    int result = program.find_among_b(among);
    assertEquals(0, result);
  } 
}