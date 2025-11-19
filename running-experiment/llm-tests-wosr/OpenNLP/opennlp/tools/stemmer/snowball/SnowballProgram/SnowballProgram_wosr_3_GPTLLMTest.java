public class SnowballProgram_wosr_3_GPTLLMTest { 

 @Test
  public void testSetAndGetCurrentString() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("running");
    assertEquals("running", program.getCurrent());
  }
@Test
  public void testSetAndGetCurrentBuffer() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("happily");
    char[] buffer = program.getCurrentBuffer();
    assertEquals('h', buffer[0]);
    assertEquals('y', buffer[6]);
  }
@Test
  public void testGetCurrentBufferLength() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("simple");
    assertEquals(6, program.getCurrentBufferLength());
  }
@Test
  public void testSetCurrentCharArray() {
    SnowballProgram program = new SnowballProgram();
    char[] value = {'a', 'b', 'c', 'd'};
    program.setCurrent(value, 4);
    assertEquals("abcd", program.getCurrent());
  }
@Test
  public void testCopyConstructorClonesFields() {
    SnowballProgram program1 = new SnowballProgram();
    program1.setCurrent("testing");
    SnowballProgram program2 = new SnowballProgram(program1);
    assertArrayEquals(program1.getCurrentBuffer(), program2.getCurrentBuffer());
    assertEquals(program1.getCurrent(), program2.getCurrent());
  }
@Test
  public void testCopyFromMethod() {
    SnowballProgram original = new SnowballProgram();
    original.setCurrent("stemmed");
    SnowballProgram target = new SnowballProgram();
    target.setCurrent("words");
    target.copy_from(original);
    assertEquals("stemmed", target.getCurrent());
  }
@Test
  public void testInGroupingReturnsTrue() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("a");
    program.cursor = 0;
    char[] group = {(char) 0b00000001}; 
    assertTrue(program.in_grouping(group, 'a', 'a'));
    assertEquals(1, program.cursor);
  }
@Test
  public void testInGroupingReturnsFalseOutsideRange() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("z");
    program.cursor = 0;
    char[] group = {(char) 0b00000001}; 
    assertFalse(program.in_grouping(group, 'a', 'a'));
    assertEquals(0, program.cursor);
  }
@Test
  public void testInGroupingBReturnsTrue() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("b");
    program.cursor = 1;
    char[] group = {(char) 0b00000100}; 
    assertTrue(program.in_grouping_b(group, 'a', 'h'));
    assertEquals(0, program.cursor);
  }
@Test
  public void testInGroupingBReturnsFalseOutOfBounds() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("y");
    program.cursor = 1;
    char[] group = {(char) 0b00000100}; 
    assertFalse(program.in_grouping_b(group, 'a', 'h'));
    assertEquals(1, program.cursor);
  }
@Test
  public void testOutGroupingReturnsTrueOutsideRange() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("z");
    program.cursor = 0;
    char[] group = {(char) 0b00000001}; 
    assertTrue(program.out_grouping(group, 'a', 'a'));
    assertEquals(1, program.cursor);
  }
@Test
  public void testOutGroupingReturnsFalseInsideGroup() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("a");
    program.cursor = 0;
    char[] group = {(char) 0b00000001}; 
    assertFalse(program.out_grouping(group, 'a', 'a'));
    assertEquals(0, program.cursor);
  }
@Test
  public void testOutGroupingBTrueOutsideRange() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("z");
    program.cursor = 1;
    char[] group = {(char) 0b00000001}; 
    assertTrue(program.out_grouping_b(group, 'a', 'a'));
    assertEquals(0, program.cursor);
  }
@Test
  public void testOutGroupingBFalseInsideGroup() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("a");
    program.cursor = 1;
    char[] group = {(char) 0b00000001}; 
    assertFalse(program.out_grouping_b(group, 'a', 'a'));
    assertEquals(1, program.cursor);
  }
@Test
  public void testEqSReturnsTrue() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("start");
    program.cursor = 0;
    assertTrue(program.eq_s("sta"));
    assertEquals(3, program.cursor);
  }
@Test
  public void testEqSReturnsFalseDueToMismatch() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("house");
    program.cursor = 0;
    assertFalse(program.eq_s("horse"));
    assertEquals(0, program.cursor);
  }
@Test
  public void testEqSBReturnsTrue() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("ending");
    program.cursor = 6;
    assertTrue(program.eq_s_b("ing"));
    assertEquals(3, program.cursor);
  }
@Test
  public void testEqSBReturnsFalse() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("prefix");
    program.cursor = 6;
    assertFalse(program.eq_s_b("post"));
    assertEquals(6, program.cursor);
  }
@Test
  public void testReplaceSShorter() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("testing");
    int adjustment = program.replace_s(4, 7, "ed");
    assertEquals("tested", program.getCurrent());
    assertEquals(-1, adjustment); 
  }
@Test
  public void testReplaceSLonger() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("test");
    int adjustment = program.replace_s(4, 4, "ing");
    assertEquals("testing", program.getCurrent());
    assertEquals(3, adjustment);
  }
@Test
  public void testSliceFrom() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("testing");
    program.bra = 4;
    program.ket = 7;
    program.slice_from("ed");
    assertEquals("tested", program.getCurrent());
  }
@Test
  public void testSliceDel() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("sadness");
    program.bra = 3;
    program.ket = 7;
    program.slice_del();
    assertEquals("sad", program.getCurrent());
  }
@Test
  public void testInsertInMiddle() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("sing");
    int oldBra = 1;
    int oldKet = 3;
    program.bra = oldBra;
    program.ket = oldKet;
    program.insert(1, 3, "umm");
    assertEquals("summng", program.getCurrent());
    assertEquals(oldBra + 1, program.bra);
    assertEquals(oldKet + 1, program.ket);
  }
@Test
  public void testSliceToCopiesSubstring() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("retesting");
    program.bra = 2;
    program.ket = 5;
    StringBuilder builder = new StringBuilder();
    program.slice_to(builder);
    assertEquals("tes", builder.toString());
  }
@Test
  public void testAssignToCopiesFullStem() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("contextual");
    StringBuilder builder = new StringBuilder();
    program.assign_to(builder);
    assertEquals("contextual", builder.toString());
  }
@Test(expected = AssertionError.class)
  public void testSliceCheckFailsOnInvalidBraKet() {
    SnowballProgram program = new SnowballProgram();
    program.setCurrent("error");
    program.bra = 4;
    program.ket = 2;
    program.slice_check();
  } 
}