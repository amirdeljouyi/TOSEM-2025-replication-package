public class TokenizerStateMachine_wosr_2_GPTLLMTest { 

 @Test
    public void testSimpleSentenceParsing() {
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
        String input = "This is a test.";
        tokenizer.parseText(input);

        List<String> tokens = new ArrayList<>();
        for (TokenizerStateMachine.State s : tokenizer.completed) {
            tokens.add(s.getWord());
        }

        Assert.assertEquals(5, tokens.size());
        Assert.assertEquals("This", tokens.get(0));
        Assert.assertEquals("is", tokens.get(1));
        Assert.assertEquals("a", tokens.get(2));
        Assert.assertEquals("test", tokens.get(3));
        Assert.assertEquals(".", tokens.get(4));
    }
@Test
    public void testMultipleSentences() {
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
        String input = "Hello world! How are you?";
        tokenizer.parseText(input);

        List<String> tokens = new ArrayList<>();
        for (TokenizerStateMachine.State s : tokenizer.completed) {
            tokens.add(s.getWord());
        }

        Assert.assertEquals(7, tokens.size());
        Assert.assertEquals("Hello", tokens.get(0));
        Assert.assertEquals("world", tokens.get(1));
        Assert.assertEquals("!", tokens.get(2));
        Assert.assertEquals("How", tokens.get(3));
        Assert.assertEquals("are", tokens.get(4));
        Assert.assertEquals("you", tokens.get(5));
        Assert.assertEquals("?", tokens.get(6));
    }
@Test
    public void testSentenceWithAbbreviation() {
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
        String input = "I live in the U.S.";
        tokenizer.parseText(input);

        List<String> tokens = new ArrayList<>();
        for (TokenizerStateMachine.State s : tokenizer.completed) {
            tokens.add(s.getWord());
        }

        Assert.assertEquals(6, tokens.size());
        Assert.assertEquals("I", tokens.get(0));
        Assert.assertEquals("live", tokens.get(1));
        Assert.assertEquals("in", tokens.get(2));
        Assert.assertEquals("the", tokens.get(3));
        Assert.assertEquals("U.S.", tokens.get(4));
        Assert.assertEquals(".", tokens.get(5));
    }
@Test
    public void testEmailRecognition() {
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
        String input = "Contact me at user@example.com for questions.";
        tokenizer.parseText(input);

        List<String> tokens = new ArrayList<>();
        for (TokenizerStateMachine.State s : tokenizer.completed) {
            tokens.add(s.getWord());
        }

        Assert.assertTrue(tokens.contains("user@example.com"));
    }
@Test
    public void testUrlRecognition() {
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
        String input = "Visit http://www.example.com for details.";
        tokenizer.parseText(input);

        List<String> tokens = new ArrayList<>();
        for (TokenizerStateMachine.State s : tokenizer.completed) {
            tokens.add(s.getWord());
        }

        boolean hasURL = false;
        for (String token : tokens) {
            if (token.contains("http://www.example.com")) {
                hasURL = true;
                break;
            }
        }

        Assert.assertTrue(hasURL);
    }
@Test
    public void testContractionsHandling() {
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
        String input = "It's John's book. He's reading.";
        tokenizer.parseText(input);

        List<String> tokens = new ArrayList<>();
        for (TokenizerStateMachine.State s : tokenizer.completed) {
            tokens.add(s.getWord());
        }

        Assert.assertTrue(tokens.contains("It's") || (tokens.contains("It") && tokens.contains("'s")));
        Assert.assertTrue(tokens.contains("He's") || (tokens.contains("He") && tokens.contains("'s")));
    }
@Test
    public void testHyphenSplitEnabled() {
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
        String input = "This is a well-known fact.";
        tokenizer.parseText(input);

        List<String> tokens = new ArrayList<>();
        for (TokenizerStateMachine.State s : tokenizer.completed) {
            tokens.add(s.getWord());
        }

        Assert.assertTrue(tokens.contains("well"));
        Assert.assertTrue(tokens.contains("-"));
        Assert.assertTrue(tokens.contains("known"));
    }
@Test
    public void testHyphenSplitDisabled() {
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
        String input = "This is a well-known fact.";
        tokenizer.parseText(input);

        List<String> tokens = new ArrayList<>();
        for (TokenizerStateMachine.State s : tokenizer.completed) {
            tokens.add(s.getWord());
        }

        Assert.assertTrue(tokens.contains("well-known"));
    }
@Test
    public void testNumbersWithCommaAndDots() {
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
        String input = "The price is $1,234.56.";
        tokenizer.parseText(input);

        List<String> tokens = new ArrayList<>();
        for (TokenizerStateMachine.State s : tokenizer.completed) {
            tokens.add(s.getWord());
        }

        Assert.assertTrue(tokens.contains("$"));
        Assert.assertTrue(tokens.contains("1,234.56") || tokens.contains("1,234") || tokens.contains(".56"));
    }
@Test
    public void testSlashInDates() {
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
        String input = "Today's date is 12/31/2020.";
        tokenizer.parseText(input);

        List<String> tokens = new ArrayList<>();
        for (TokenizerStateMachine.State s : tokenizer.completed) {
            tokens.add(s.getWord());
        }

        Assert.assertTrue(tokens.contains("12/31/2020"));
    }
@Test
    public void testSentenceEndWithQuotes() {
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
        String input = "\"Hello.\" She said.";
        tokenizer.parseText(input);

        List<String> tokens = new ArrayList<>();
        for (TokenizerStateMachine.State s : tokenizer.completed) {
            tokens.add(s.getWord());
        }

        Assert.assertTrue(tokens.contains("\""));
        Assert.assertTrue(tokens.contains("Hello"));
        Assert.assertTrue(tokens.contains("."));
        Assert.assertTrue(tokens.contains("She"));
    }
@Test
    public void testEmptyString() {
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
        String input = "";
        tokenizer.parseText(input);

        Assert.assertTrue(tokenizer.completed.isEmpty());
    }
@Test
    public void testWhitespaceOnly() {
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
        String input = "     ";
        tokenizer.parseText(input);

        Assert.assertTrue(tokenizer.completed.isEmpty());
    }
@Test
    public void testUnprintableCharacters() {
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
        String input = "Word\u0000Another";
        tokenizer.parseText(input);

        List<String> tokens = new ArrayList<>();
        for (TokenizerStateMachine.State s : tokenizer.completed) {
            tokens.add(s.getWord());
        }

        Assert.assertTrue(tokens.contains("Word"));
        Assert.assertTrue(tokens.contains("Another"));
    }
@Test
    public void testGetNextWord() {
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
        tokenizer.parseText("It's raining.");
        int index = 0;
        for (TokenizerStateMachine.State s : tokenizer.stack) {
            index++;
        }
        String nextWord = tokenizer.getNextWord();

        Assert.assertTrue(nextWord.startsWith("It's") || nextWord.equals("raining"));
    }
@Test
    public void testEllipsisContinuation() {
        TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
        String input = "Wait...";
        tokenizer.parseText(input);

        List<String> tokens = new ArrayList<>();
        for (TokenizerStateMachine.State s : tokenizer.completed) {
            tokens.add(s.getWord());
        }

        Assert.assertTrue(tokens.contains("...") || tokens.contains(".") || tokens.contains("Wait"));
    } 
}