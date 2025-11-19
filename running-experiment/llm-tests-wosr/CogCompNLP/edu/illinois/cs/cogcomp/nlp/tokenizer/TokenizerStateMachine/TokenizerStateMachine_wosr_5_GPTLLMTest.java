public class TokenizerStateMachine_wosr_5_GPTLLMTest { 

 @Test
    public void testSimpleSentenceParsing() {
        String text = "This is a test.";
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
        tokenizer.parseText(text);

        List<TokenizerStateMachine.State> completed = tokenizer.completed;
        assertEquals(5, completed.size());
        assertEquals("This", completed.get(0).getWord());
        assertEquals("is", completed.get(1).getWord());
        assertEquals("a", completed.get(2).getWord());
        assertEquals("test", completed.get(3).getWord());
        assertEquals(".", completed.get(4).getWord());
    }
@Test
    public void testSentenceEndingWithMultiplePeriods() {
        String text = "Wait...";
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
        tokenizer.parseText(text);

        List<TokenizerStateMachine.State> completed = tokenizer.completed;
        assertEquals(2, completed.size());
        assertEquals("Wait", completed.get(0).getWord());
        assertEquals("...", completed.get(1).getWord());
    }
@Test
    public void testPeriodInsideAbbreviation() {
        String text = "He lives in the U.S.";
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
        tokenizer.parseText(text);

        List<TokenizerStateMachine.State> completed = tokenizer.completed;
        assertEquals(7, completed.size());
        assertEquals("He", completed.get(0).getWord());
        assertEquals("lives", completed.get(1).getWord());
        assertEquals("in", completed.get(2).getWord());
        assertEquals("the", completed.get(3).getWord());
        assertEquals("U.S.", completed.get(4).getWord());
        assertEquals(".", completed.get(6).getWord());
    }
@Test
    public void testEmailIdentification() {
        String text = "Contact me at john.doe@example.com now.";
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
        tokenizer.parseText(text);

        List<TokenizerStateMachine.State> completed = tokenizer.completed;
        boolean emailFound = false;
        for (TokenizerStateMachine.State t : completed) {
            if (t.getWord().equals("john.doe@example.com")) {
                emailFound = true;
                break;
            }
        }
        assertTrue(emailFound);
    }
@Test
    public void testUrlDetection() {
        String text = "Visit http://example.com:8080/page today.";
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
        tokenizer.parseText(text);

        List<TokenizerStateMachine.State> completed = tokenizer.completed;
        boolean urlFound = false;
        for (TokenizerStateMachine.State t : completed) {
            if (t.getWord().equals("http://example.com:8080/page")) {
                urlFound = true;
                break;
            }
        }
        assertTrue(urlFound);
    }
@Test
    public void testHyphenHandlingWithSplitOnDashTrue() {
        String text = "A well-known author.";
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
        tokenizer.parseText(text);

        List<TokenizerStateMachine.State> completed = tokenizer.completed;
        assertEquals(5, completed.size());
        assertEquals("A", completed.get(0).getWord());
        assertEquals("well", completed.get(1).getWord());
        assertEquals("-", completed.get(2).getWord());
        assertEquals("known", completed.get(3).getWord());
        assertEquals("author", completed.get(4).getWord());
    }
@Test
    public void testHyphenHandlingWithSplitOnDashFalse() {
        String text = "A well-known author.";
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
        tokenizer.parseText(text);

        List<TokenizerStateMachine.State> completed = tokenizer.completed;
        assertEquals(3, completed.size());
        assertEquals("A", completed.get(0).getWord());
        assertEquals("well-known", completed.get(1).getWord());
        assertEquals("author", completed.get(2).getWord());
    }
@Test
    public void testApostropheContractionIts() {
        String text = "It's raining.";
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
        tokenizer.parseText(text);

        List<TokenizerStateMachine.State> completed = tokenizer.completed;
        assertEquals(4, completed.size());
        assertEquals("It", completed.get(0).getWord());
        assertEquals("'s", completed.get(1).getWord());
        assertEquals("raining", completed.get(2).getWord());
        assertEquals(".", completed.get(3).getWord());
    }
@Test
    public void testDecimalNumber() {
        String text = "The price is 3.99 dollars.";
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
        tokenizer.parseText(text);

        List<TokenizerStateMachine.State> completed = tokenizer.completed;
        boolean found = false;
        for (TokenizerStateMachine.State state : completed) {
            if (state.getWord().equals("3.99")) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }
@Test
    public void testMultipleSentencesWithSplitOnTwoNewlinesFalse() {
        String text = "First line.\nSecond line.";
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
        tokenizer.parseText(text);

        List<TokenizerStateMachine.State> completed = tokenizer.completed;
        assertEquals(6, completed.size());
        assertEquals("First", completed.get(0).getWord());
        assertEquals("line", completed.get(1).getWord());
        assertEquals(".", completed.get(2).getWord());
        assertEquals("Second", completed.get(3).getWord());
        assertEquals("line", completed.get(4).getWord());
        assertEquals(".", completed.get(5).getWord());
    }
@Test
    public void testSplitOnTwoNewlinesTrue() {
        String text = "Hello world.\n\nNew paragraph.";
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, true);
        tokenizer.parseText(text);

        List<TokenizerStateMachine.State> completed = tokenizer.completed;
        assertEquals(6, completed.size());
        assertEquals("Hello", completed.get(0).getWord());
        assertEquals("world", completed.get(1).getWord());
        assertEquals(".", completed.get(2).getWord());
        assertEquals("New", completed.get(3).getWord());
        assertEquals("paragraph", completed.get(4).getWord());
        assertEquals(".", completed.get(5).getWord());
    }
@Test
    public void testEmptyInput() {
        String text = "";
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
        tokenizer.parseText(text);
        assertEquals(0, tokenizer.completed.size());
    }
@Test
    public void testUnprintableCharacterHandling() {
        String text = "Print\u0007this.";
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
        tokenizer.parseText(text);

        List<TokenizerStateMachine.State> completed = tokenizer.completed;
        assertEquals(3, completed.size());
        assertEquals("Print", completed.get(0).getWord());
        assertEquals("this", completed.get(1).getWord());
        assertEquals(".", completed.get(2).getWord());
    }
@Test
    public void testPeekBoundariesNegativeIndex() {
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
        tokenizer.text = "abc".toCharArray();
        tokenizer.current = 0;
        char result = tokenizer.peek(-1);
        assertEquals('\000', result);
    }
@Test
    public void testPeekBoundariesOverflowIndex() {
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
        tokenizer.text = "abc".toCharArray();
        tokenizer.current = 2;
        char result = tokenizer.peek(2);
        assertEquals('\000', result);
    }
@Test
    public void testIsContinueWithSinglePeriod() {
        String text = "End.";
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
        tokenizer.parseText(text);
        tokenizer.completed.get(tokenizer.completed.size() - 1).pop(tokenizer.text.length);
        boolean result = tokenizer.isContinue();
        assertFalse(result);
    }
@Test
    public void testIsContinueWithEllipsis() {
        String text = "Wait...";
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
        tokenizer.parseText(text);
        tokenizer.completed.get(tokenizer.completed.size() - 1).pop(tokenizer.text.length);
        boolean result = tokenizer.isContinue();
        assertTrue(result);
    }
@Test
    public void testAcronymDetectionTrue() {
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
        tokenizer.text = "U.S.".toCharArray();
        TokenizerStateMachine.State state = tokenizer.new State(TokenizerState.IN_WORD);
        state.start = 0;
        state.pop(tokenizer.text.length);
        tokenizer.stack = new java.util.ArrayList<>();
        tokenizer.stack.add(state);
        tokenizer.current = tokenizer.text.length;
        assertTrue(state.isAbbr());
    }
@Test
    public void testAcronymDetectionFalse() {
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
        tokenizer.text = "Random.".toCharArray();
        TokenizerStateMachine.State state = tokenizer.new State(TokenizerState.IN_WORD);
        state.start = 0;
        state.pop(tokenizer.text.length);
        tokenizer.stack = new java.util.ArrayList<>();
        tokenizer.stack.add(state);
        tokenizer.current = tokenizer.text.length;
        assertFalse(state.isAbbr());
    } 
}