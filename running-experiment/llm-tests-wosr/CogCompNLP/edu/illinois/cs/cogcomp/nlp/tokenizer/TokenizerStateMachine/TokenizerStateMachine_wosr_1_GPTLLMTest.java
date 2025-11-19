public class TokenizerStateMachine_wosr_1_GPTLLMTest { 

 @Test
    public void testIsEmail_validFullEmail_domainWithDotCom() throws Exception {
        TokenizerStateMachine machine = new TokenizerStateMachine(false, false);
        String input = "hello user@example.com here";
        machine.parseText(input);

        TokenizerStateMachine.State state = machine.completed.get(1);
        assertEquals("user@example.com", state.getWord());
    }
@Test
    public void testIsEmail_validShortEmail_domainWithoutDot() throws Exception {
        TokenizerStateMachine machine = new TokenizerStateMachine(false, false);
        String input = "hello user@localhost here";
        machine.parseText(input);

        TokenizerStateMachine.State state = machine.completed.get(1);
        assertEquals("user@localhost", state.getWord());
    }
@Test
    public void testIsEmail_emailFollowedByWhitespace() throws Exception {
        TokenizerStateMachine machine = new TokenizerStateMachine(false, false);
        String input = "email someone@mail.com ";
        machine.parseText(input);

        TokenizerStateMachine.State state = machine.completed.get(1);
        assertEquals("someone@mail.com", state.getWord());
    }
@Test
    public void testIsEmail_noMatchOutput() throws Exception {
        TokenizerStateMachine machine = new TokenizerStateMachine(false, false);
        String input = "this is not an email!";
        machine.parseText(input);

        boolean foundEmail = false;
        for (TokenizerStateMachine.State state : machine.completed) {
            if (state.getWord().contains("@")) {
                foundEmail = true;
                break;
            }
        }
        assertFalse(foundEmail);
    }
@Test
    public void testIsURL_validHttpUrl() throws Exception {
        TokenizerStateMachine machine = new TokenizerStateMachine(false, false);
        String input = "Visit http://example.com for more.";
        machine.parseText(input);

        boolean found = false;
        for (TokenizerStateMachine.State s : machine.completed) {
            if (s.getWord().startsWith("http://")) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }
@Test
    public void testIsURL_invalidScheme_missingAfterColon() throws Exception {
        TokenizerStateMachine machine = new TokenizerStateMachine(false, false);
        String input = "Invalid proto: text here.";
        machine.parseText(input);

        boolean foundUrl = false;
        for (TokenizerStateMachine.State s : machine.completed) {
            if (s.getWord().startsWith("proto:")) {
                foundUrl = true;
                break;
            }
        }
        assertFalse(foundUrl);
    }
@Test
    public void testGetNextWord_earlyPeriod_contraction() throws Exception {
        TokenizerStateMachine machine = new TokenizerStateMachine(false, false);
        String input = "It’s been a while.";
        machine.parseText(input);

        boolean contractionHit = false;
        for (TokenizerStateMachine.State s : machine.completed) {
            if ("’s".equals(s.getWord())) {
                contractionHit = true;
                break;
            }
        }
        assertTrue(contractionHit);
    }
@Test
    public void testParsingWithSplitOnDashEnabled() throws Exception {
        TokenizerStateMachine machine = new TokenizerStateMachine(true, false);
        String input = "dash-separated-word";
        machine.parseText(input);

        int tokenCount = machine.completed.size();
        assertTrue(tokenCount >= 3);
        assertEquals("word", machine.completed.get(tokenCount - 1).getWord());
    }
@Test
    public void testParsingWithSplitOnTwoNewlinesEnabled() throws Exception {
        TokenizerStateMachine machine = new TokenizerStateMachine(false, true);
        String input = "First sentence.\n\nSecond sentence.";
        machine.parseText(input);

        int sentenceCount = 0;
        for (TokenizerStateMachine.State s : machine.completed) {
            if (s.getWord().endsWith(".")) {
                sentenceCount++;
            }
        }
        assertEquals(2, sentenceCount);
    }
@Test
    public void testAcronymsAbbreviationRecognition() throws Exception {
        TokenizerStateMachine machine = new TokenizerStateMachine(false, false);
        String input = "U.S.A. is an abbreviation.";
        machine.parseText(input);

        boolean foundAbbreviation = false;
        for (TokenizerStateMachine.State s : machine.completed) {
            if (s.isAbbr() && s.getWord().contains("U.S.A.")) {
                foundAbbreviation = true;
                break;
            }
        }
        assertTrue(foundAbbreviation);
    }
@Test
    public void testAbbreviationFalseWhenNotUpperCase() throws Exception {
        TokenizerStateMachine machine = new TokenizerStateMachine(false, false);
        String input = "u.s.a is lowercase.";
        machine.parseText(input);

        boolean falseAbbr = false;
        for (TokenizerStateMachine.State s : machine.completed) {
            if (!s.isAbbr() && s.getWord().equals("u.s.a")) {
                falseAbbr = true;
                break;
            }
        }
        assertTrue(falseAbbr);
    }
@Test
    public void testNumericDetectionWithCommasAndDots() throws Exception {
        TokenizerStateMachine machine = new TokenizerStateMachine(false, false);
        String input = "The value is 1,000.50 dollars.";
        machine.parseText(input);

        boolean numericDetected = false;
        for (TokenizerStateMachine.State s : machine.completed) {
            if (s.isNumeric() && s.getWord().contains("1,000.50")) {
                numericDetected = true;
                break;
            }
        }
        assertTrue(numericDetected);
    }
@Test
    public void testDateIsProperlyDetected() throws Exception {
        TokenizerStateMachine machine = new TokenizerStateMachine(false, false);
        String input = "Deadline is 02/15/2024.";
        machine.parseText(input);

        boolean dateFound = false;
        for (TokenizerStateMachine.State s : machine.completed) {
            if (s.isDate() && s.getWord().equals("02/15/2024")) {
                dateFound = true;
                break;
            }
        }
        assertTrue(dateFound);
    }
@Test
    public void testPeekCharacterInBounds() throws Exception {
        TokenizerStateMachine machine = new TokenizerStateMachine(false, false);
        machine.text = "hello".toCharArray();
        machine.current = 1;
        char peekResult = machine.peek(2);
        assertEquals('l', peekResult);
    }
@Test
    public void testPeekCharacter_outOfBoundsReturnsNullChar() throws Exception {
        TokenizerStateMachine machine = new TokenizerStateMachine(false, false);
        machine.text = "hi".toCharArray();
        machine.current = 1;
        char peekValue = machine.peek(100);
        assertEquals('\000', peekValue);
    }
@Test
    public void testStateSizeWhenPopped() throws Exception {
        TokenizerStateMachine.State state = new TokenizerStateMachine(false, false).new State(TokenizerState.IN_WORD);
        state.push(5);
        state.pop(10);
        assertEquals(5, state.size());
    }
@Test
    public void testPopResetsStackProperly() throws Exception {
        TokenizerStateMachine machine = new TokenizerStateMachine(false, false);
        machine.stack = new java.util.ArrayList<>();
        machine.completed = new java.util.ArrayList<>();
        TokenizerStateMachine.State s = machine.new State(TokenizerState.IN_SENTENCE);
        s.push(0);
        machine.stack.add(s);

        boolean result = machine.pop(5);
        assertTrue(result);
        assertEquals(1, machine.stack.size());
    }
@Test
    public void testPushAddsToStack() throws Exception {
        TokenizerStateMachine machine = new TokenizerStateMachine(false, false);
        machine.stack = new java.util.ArrayList<>();
        machine.completed = new java.util.ArrayList<>();
        TokenizerStateMachine.State s = machine.new State(TokenizerState.IN_WORD);
        machine.push(s, 2);
        assertEquals(1, machine.stack.size());
    }
@Test
    public void testIsContinue_returnsTrueForMultiplePeriods() throws Exception {
        TokenizerStateMachine machine = new TokenizerStateMachine(false, false);
        machine.stack = new java.util.ArrayList<>();
        machine.completed = new java.util.ArrayList<>();

        TokenizerStateMachine.State s = machine.new State(TokenizerState.IN_SPECIAL);
        s.push(0);
        machine.text = "... Next".toCharArray();
        machine.current = 5;
        s.pop(3);
        machine.completed.add(s);

        assertTrue(machine.isContinue());
    }
@Test
    public void testIsContinue_returnsFalseForNonDotChars() throws Exception {
        TokenizerStateMachine machine = new TokenizerStateMachine(false, false);
        machine.stack = new java.util.ArrayList<>();
        machine.completed = new java.util.ArrayList<>();

        TokenizerStateMachine.State s = machine.new State(TokenizerState.IN_SPECIAL);
        s.push(0);
        machine.text = ".ab ".toCharArray();
        machine.current = 4;
        s.pop(3);
        machine.completed.add(s);

        assertFalse(machine.isContinue());
    }
@Test
    public void testContractionSplitting_detectsArent() throws Exception {
        TokenizerStateMachine machine = new TokenizerStateMachine(false, false);
        String input = "They aren't coming.";
        machine.parseText(input);

        boolean splitFound = false;
        boolean secondIsNt = false;
        for (int i = 0; i < machine.completed.size() - 1; i++) {
            if ("are".equals(machine.completed.get(i).getWord())
             && "n't".equals(machine.completed.get(i+1).getWord())) {
                splitFound = true;
                secondIsNt = true;
                break;
            }
        }
        assertFalse(splitFound && secondIsNt); 
    }
@Test
    public void testSpecialCharacterGrouping_multipleSymbols() throws Exception {
        TokenizerStateMachine machine = new TokenizerStateMachine(false, false);
        String input = "!!! Done";
        machine.parseText(input);

        boolean grouped = false;
        for (TokenizerStateMachine.State s : machine.completed) {
            if (s.getWord().equals("!!!")) {
                grouped = true;
                break;
            }
        }
        assertFalse(grouped); 
    }
@Test
    public void testParsingEmptyStringDoesNotCrash() throws Exception {
        TokenizerStateMachine machine = new TokenizerStateMachine(false, false);
        String input = "";
        machine.parseText(input);

        assertEquals(0, machine.completed.size());
    } 
}