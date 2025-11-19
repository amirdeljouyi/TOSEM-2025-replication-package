public class Comma_wosr_3_GPTLLMTest { 

 @Test
    public void testGetWordToRightWithinBounds() {
        String[] tokens = {"This", ",", "is", "a", "test", "."};
        TextAnnotation ta = mock(TextAnnotation.class);
        when(ta.getTokens()).thenReturn(tokens);
        when(ta.getToken(2)).thenReturn("is");

        CommaSRLSentence sentence = mock(CommaSRLSentence.class);
        sentence.ta = ta;

        Comma comma = new Comma(1, sentence);

        assertEquals("is", comma.getWordToRight(1));
    }
@Test
    public void testGetWordToRightOutOfBounds() {
        String[] tokens = {"This", ",", "is"};
        TextAnnotation ta = mock(TextAnnotation.class);
        when(ta.getTokens()).thenReturn(tokens);

        CommaSRLSentence sentence = mock(CommaSRLSentence.class);
        sentence.ta = ta;

        Comma comma = new Comma(2, sentence);

        assertEquals("###", comma.getWordToRight(1));
    }
@Test
    public void testGetWordToLeftWithinBounds() {
        String[] tokens = {"One", "Two", ","};
        TextAnnotation ta = mock(TextAnnotation.class);
        when(ta.getTokens()).thenReturn(tokens);
        when(ta.getToken(1)).thenReturn("Two");

        CommaSRLSentence sentence = mock(CommaSRLSentence.class);
        sentence.ta = ta;

        Comma comma = new Comma(2, sentence);

        assertEquals("Two", comma.getWordToLeft(1));
    }
@Test
    public void testGetWordToLeftOutOfBounds() {
        String[] tokens = {"_", ","};
        TextAnnotation ta = mock(TextAnnotation.class);
        when(ta.getTokens()).thenReturn(tokens);

        CommaSRLSentence sentence = mock(CommaSRLSentence.class);
        sentence.ta = ta;

        Comma comma = new Comma(0, sentence);

        assertEquals("$$$", comma.getWordToLeft(1));
    }
@Test
    public void testGetPosition() {
        CommaSRLSentence sentence = mock(CommaSRLSentence.class);
        Comma comma = new Comma(5, sentence);

        assertEquals(5, comma.getPosition());
    }
@Test
    public void testGetLabelSingle() {
        CommaSRLSentence sentence = mock(CommaSRLSentence.class);
        List<String> labels = Collections.singletonList("PARENTHESES");

        Comma comma = new Comma(3, sentence, labels);

        assertEquals("PARENTHESES", comma.getLabel());
    }
@Test
    public void testGetLabelsMultiple() {
        CommaSRLSentence sentence = mock(CommaSRLSentence.class);
        List<String> labels = Arrays.asList("PAREN", "LIST");

        Comma comma = new Comma(3, sentence, labels);
        List<String> actual = comma.getLabels();

        assertEquals(2, actual.size());
        assertTrue(actual.contains("PAREN"));
        assertTrue(actual.contains("LIST"));
    }
@Test
    public void testGetCommaID() {
        TextAnnotation goldTa = mock(TextAnnotation.class);
        when(goldTa.getId()).thenReturn("test123");

        CommaSRLSentence sentence = mock(CommaSRLSentence.class);
        sentence.goldTa = goldTa;

        Comma comma = new Comma(7, sentence, Collections.singletonList("X"));
        assertEquals("7 test123", comma.getCommaID());
    }
@Test
    public void testGetTextAnnotationGoldTrue() {
        TextAnnotation goldTa = mock(TextAnnotation.class);
        TextAnnotation ta = mock(TextAnnotation.class);

        CommaSRLSentence sentence = mock(CommaSRLSentence.class);
        sentence.ta = ta;
        sentence.goldTa = goldTa;

        Comma comma = new Comma(3, sentence);

        assertEquals(goldTa, comma.getTextAnnotation(true));
    }
@Test
    public void testGetTextAnnotationGoldFalse() {
        TextAnnotation goldTa = mock(TextAnnotation.class);
        TextAnnotation ta = mock(TextAnnotation.class);

        CommaSRLSentence sentence = mock(CommaSRLSentence.class);
        sentence.ta = ta;
        sentence.goldTa = goldTa;

        Comma comma = new Comma(3, sentence);

        assertEquals(ta, comma.getTextAnnotation(false));
    }
@Test
    public void testGetParentSiblingPhraseNGramsNotNull() {
        Constituent c1 = mock(Constituent.class);
        when(c1.getLabel()).thenReturn("NP");
        when(c1.getSpan()).thenReturn(new IntPair(0, 1));
        TextAnnotation ta = mock(TextAnnotation.class);
        when(ta.getToken(0)).thenReturn("This");
        when(ta.getView(ViewNames.POS)).thenReturn(new TokenLabelView(ViewNames.POS, "test", ta, 0.0));
        when(ta.getTokens()).thenReturn(new String[]{"This", ",", "is", "a", "test"});

        CommaSRLSentence sentence = mock(CommaSRLSentence.class);
        sentence.ta = ta;
        when(ta.getView(ViewNames.PARSE_STANFORD)).thenReturn(mock(TreeView.class));
        Comma comma = new Comma(1, sentence, Collections.singletonList("LIST"));

        try {
            comma.getParentSiblingPhraseNgrams();
        } catch (Exception ex) {
            fail("getParentSiblingPhraseNgrams threw an exception: " + ex.getMessage());
        }
    }
@Test
    public void testGetAnnotatedText() {
        String[] tokens = {"He", ",", "however", ",", "left"};
        List<String> labels = Arrays.asList("PARENTHETICAL");

        TextAnnotation ta = mock(TextAnnotation.class);
        when(ta.getTokens()).thenReturn(tokens);

        CommaSRLSentence sentence = mock(CommaSRLSentence.class);
        sentence.ta = ta;

        Comma comma = new Comma(1, sentence, labels);

        String result = comma.getAnnotatedText();
        assertTrue(result.startsWith("He ,[PARENTHETICAL]"));
    }
@Test
    public void testGetBayraktarLabelWhenNull() {
        BayraktarPatternLabeler labeler = mock(BayraktarPatternLabeler.class);
        Comma comma = mock(Comma.class);
        when(BayraktarPatternLabeler.getLabel(comma)).thenReturn(null);

        Comma real = new Comma(0, mock(CommaSRLSentence.class), Collections.singletonList("X"));
        assertEquals("Other", real.getBayraktarLabel()); 
    }
@Test
    public void testGetStrippedNotationNullConstituent() {
        Comma comma = new Comma(0, mock(CommaSRLSentence.class), Collections.singletonList("X"));
        assertEquals("NULL", comma.getStrippedNotation(null));
    }
@Test
    public void testGetNotationNullConstituent() {
        Comma comma = new Comma(0, mock(CommaSRLSentence.class), Collections.singletonList("X"));
        assertEquals("NULL", comma.getNotation(null));
    }
@Test
    public void testGetBayraktarAnnotatedTextFormat() {
        String[] tokens = {"This", ",", "is"};
        TextAnnotation ta = mock(TextAnnotation.class);
        when(ta.getTokens()).thenReturn(tokens);

        CommaSRLSentence sentence = mock(CommaSRLSentence.class);
        sentence.ta = ta;

        Comma comma = new Comma(1, sentence, Collections.singletonList("LIST"));
        String result = comma.getBayraktarAnnotatedText();
        assertTrue(result.contains("["));
        assertTrue(result.contains("]"));
    } 
}