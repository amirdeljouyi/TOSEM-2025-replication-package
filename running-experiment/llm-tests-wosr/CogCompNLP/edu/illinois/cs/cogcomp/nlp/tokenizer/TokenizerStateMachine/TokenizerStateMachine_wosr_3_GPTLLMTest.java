public class TokenizerStateMachine_wosr_3_GPTLLMTest { 

 @Test
    public void testIsEmail_ValidSimpleAddress() {
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
        String input = "user@example.com";
        tokenizer.text = input.toCharArray();
        tokenizer.textstring = input;
        tokenizer.stack = new ArrayList<>();
        tokenizer.completed = new ArrayList<>();
        tokenizer.current = 0;
        TokenizerStateMachine.State state = tokenizer.new State(TokenizerState.IN_WORD);
        state.push(0);
        tokenizer.stack.add(state);

        boolean result = tokenizer.isEmail();

        assertTrue(result);
        assertTrue(tokenizer.completed.size() > 0);
    }
@Test
    public void testIsEmail_ValidWithDotDomain() {
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
        String input = "john.doe@sub.example.com";
        tokenizer.text = input.toCharArray();
        tokenizer.textstring = input;
        tokenizer.stack = new ArrayList<>();
        tokenizer.completed = new ArrayList<>();
        tokenizer.current = 0;
        TokenizerStateMachine.State state = tokenizer.new State(TokenizerState.IN_WORD);
        state.push(0);
        tokenizer.stack.add(state);

        boolean result = tokenizer.isEmail();

        assertTrue(result);
        assertEquals("john.doe@sub.example.com", tokenizer.completed.get(0).getWord());
    }
@Test
    public void testIsEmail_InvalidAddress() {
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
        String input = "user@@example..com";
        tokenizer.text = input.toCharArray();
        tokenizer.textstring = input;
        tokenizer.stack = new ArrayList<>();
        tokenizer.completed = new ArrayList<>();
        tokenizer.current = 0;
        TokenizerStateMachine.State state = tokenizer.new State(TokenizerState.IN_WORD);
        state.push(0);
        tokenizer.stack.add(state);

        boolean result = tokenizer.isEmail();

        assertFalse(result);
    }
@Test
    public void testIsURL_ValidHttpURL() {
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
        String input = "http://example.com";
        tokenizer.text = input.toCharArray();
        tokenizer.textstring = input;
        tokenizer.stack = new ArrayList<>();
        tokenizer.completed = new ArrayList<>();
        tokenizer.current = 4;

        TokenizerStateMachine.State state = tokenizer.new State(TokenizerState.IN_WORD);
        state.push(0);
        tokenizer.stack.add(state);

        boolean result = tokenizer.isURL();

        assertTrue(result);
        assertTrue(tokenizer.completed.size() > 0);
        assertEquals("http://example.com", tokenizer.completed.get(0).getWord());
    }
@Test
    public void testIsURL_InvalidURLWithWhitespace() {
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
        String input = "http: //example.com";
        tokenizer.text = input.toCharArray();
        tokenizer.textstring = input;
        tokenizer.stack = new ArrayList<>();
        tokenizer.completed = new ArrayList<>();
        tokenizer.current = 4;

        TokenizerStateMachine.State state = tokenizer.new State(TokenizerState.IN_WORD);
        state.push(0);
        tokenizer.stack.add(state);

        boolean result = tokenizer.isURL();

        assertFalse(result);
        assertEquals(0, tokenizer.completed.size());
    }
@Test
    public void testPeekWithinBounds() {
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
        tokenizer.text = "abcde".toCharArray();
        tokenizer.current = 2;

        char result = tokenizer.peek(1); 

        assertEquals('d', result);
    }
@Test
    public void testPeekOutOfBoundsLow() {
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
        tokenizer.text = "abcde".toCharArray();
        tokenizer.current = 0;

        char result = tokenizer.peek(-1);

        assertEquals('\000', result);
    }
@Test
    public void testPeekOutOfBoundsHigh() {
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
        tokenizer.text = "abcde".toCharArray();
        tokenizer.current = 4;

        char result = tokenizer.peek(2);

        assertEquals('\000', result);
    }
@Test
    public void testIsAbbr_CustomAcronym() {
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
        tokenizer.text = "U.S.A.".toCharArray();
        tokenizer.current = 5;

        TokenizerStateMachine.State state = tokenizer.new State(TokenizerState.IN_WORD);
        state.push(0);
        state.pop(6); 
        assertTrue(state.isAbbr());
    }
@Test
    public void testIsNumeric_ValidDigits() {
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
        tokenizer.text = "1234".toCharArray();
        tokenizer.current = 4;

        TokenizerStateMachine.State state = tokenizer.new State(TokenizerState.IN_WORD);
        state.push(0);
        state.pop(4);

        assertTrue(state.isNumeric());
    }
@Test
    public void testIsNumeric_InvalidMixedChars() {
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
        tokenizer.text = "12ab".toCharArray();
        tokenizer.current = 4;

        TokenizerStateMachine.State state = tokenizer.new State(TokenizerState.IN_WORD);
        state.push(0);
        state.pop(4);

        assertFalse(state.isNumeric());
    }
@Test
    public void testIsContinue_MultiplePeriods() {
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
        tokenizer.text = "....".toCharArray();
        tokenizer.current = 4;

        TokenizerStateMachine.State state = tokenizer.new State(TokenizerState.IN_SPECIAL);
        state.push(0);
        state.pop(4);

        tokenizer.completed = new ArrayList<>();
        tokenizer.completed.add(state);

        assertTrue(tokenizer.isContinue());
    }
@Test
    public void testIsContinue_SinglePeriod() {
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
        tokenizer.text = ".".toCharArray();
        tokenizer.current = 1;

        TokenizerStateMachine.State state = tokenizer.new State(TokenizerState.IN_SPECIAL);
        state.push(0);
        state.pop(1);

        tokenizer.completed = new ArrayList<>();
        tokenizer.completed.add(state);

        assertFalse(tokenizer.isContinue());
    }
@Test
    public void testGetNextWord_TerminatedByWhitespace() {
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
        tokenizer.textstring = "hello world";
        tokenizer.text = tokenizer.textstring.toCharArray();
        tokenizer.current = 0;

        String word = tokenizer.getNextWord();

        assertEquals("hello", word);
    }
@Test
    public void testGetNextWord_AtEndOfString() {
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
        tokenizer.textstring = "data";
        tokenizer.text = tokenizer.textstring.toCharArray();
        tokenizer.current = 0;

        String word = tokenizer.getNextWord();

        assertEquals("data", word);
    }
@Test
    public void testPopWhenStackEmptyCreatesNewSentenceState() {
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
        tokenizer.stack = new ArrayList<>();
        tokenizer.completed = new ArrayList<>();
        tokenizer.text = "hi.".toCharArray();
        tokenizer.textstring = "hi.";
        tokenizer.current = 3;

        boolean result = tokenizer.pop(3);

        assertTrue(result);
        assertEquals(1, tokenizer.stack.size());
    }
@Test
    public void testPushStackStateIndexAndTextSpan() {
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
        TokenizerStateMachine.State newState = tokenizer.new State(TokenizerState.IN_WORD);

        tokenizer.stack = new ArrayList<>();
        tokenizer.push(newState, 2);

        assertEquals(1, tokenizer.stack.size());
        TokenizerStateMachine.State s = tokenizer.stack.get(0);

        assertEquals(TokenizerState.IN_WORD.ordinal(), s.stateIndex());
        assertEquals(2, s.start);
    }
@Test
    public void testStatePushAndPopSpan() {
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
        TokenizerStateMachine.State s = tokenizer.new State(TokenizerState.IN_WORD);

        s.push(1);
        s.pop(5);

        assertEquals(1, s.start);
        assertEquals(5, s.end);
        assertEquals(4, s.size());
    }
@Test
    public void testIsDate_AllDigitsAndSlash() {
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
        tokenizer.text = "12/31/2022".toCharArray();
        tokenizer.current = 10;

        TokenizerStateMachine.State s = tokenizer.new State(TokenizerState.IN_WORD);
        s.push(0);
        s.pop(10);

        assertTrue(s.isDate());
    }
@Test
    public void testIsDate_WithInvalidChar() {
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
        tokenizer.text = "12/ab/34".toCharArray();
        tokenizer.current = 8;

        TokenizerStateMachine.State s = tokenizer.new State(TokenizerState.IN_WORD);
        s.push(0);
        s.pop(8);

        assertFalse(s.isDate());
    } 
}