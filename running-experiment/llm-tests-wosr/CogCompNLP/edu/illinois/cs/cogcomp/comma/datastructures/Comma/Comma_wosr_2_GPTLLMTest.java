public class Comma_wosr_2_GPTLLMTest { 

 @Test
    public void testGetLabelWithSingleLabel() {
        String[] tokens = {"This", ",", "is", "a", "test", "."};
        TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation(tokens);
        TextAnnotation goldTa = DummyTextAnnotationBuilder.buildTextAnnotation(tokens);
        List<String> labels = Collections.singletonList("LabelA");
        CommaSRLSentence sentence = new CommaSRLSentence(ta, goldTa, Collections.emptyList());

        Comma comma = new Comma(1, sentence, labels);

        assertEquals("LabelA", comma.getLabel());
    }
@Test
    public void testGetLabels() {
        String[] tokens = {"Test", ",", "example"};
        TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation(tokens);
        TextAnnotation goldTa = DummyTextAnnotationBuilder.buildTextAnnotation(tokens);
        List<String> labels = Arrays.asList("LabelA", "LabelB");
        CommaSRLSentence sentence = new CommaSRLSentence(ta, goldTa, Collections.emptyList());
        Comma comma = new Comma(1, sentence, labels);

        List<String> result = comma.getLabels();
        assertEquals(2, result.size());
        assertEquals("LabelA", result.get(0));
        assertEquals("LabelB", result.get(1));
    }
@Test
    public void testGetPosition() {
        String[] tokens = {"Hello", ",", "world"};
        TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation(tokens);
        TextAnnotation goldTa = DummyTextAnnotationBuilder.buildTextAnnotation(tokens);
        CommaSRLSentence sentence = new CommaSRLSentence(ta, goldTa, Collections.emptyList());
        Comma comma = new Comma(1, sentence, Arrays.asList("Greeting"));

        assertEquals(1, comma.getPosition());
    }
@Test
    public void testGetCommaID() {
        String[] tokens = {"Unit", ",", "test"};
        TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation(tokens);
        ta.setId("exampleTAID");
        TextAnnotation goldTa = DummyTextAnnotationBuilder.buildTextAnnotation(tokens);
        goldTa.setId("goldTAID");

        CommaSRLSentence sentence = new CommaSRLSentence(ta, goldTa, Collections.emptyList());
        Comma comma = new Comma(1, sentence, Arrays.asList("TestLabel"));

        String id = comma.getCommaID();
        assertEquals("1 goldTAID", id);
    }
@Test
    public void testGetWordToRight_insideBounds() {
        String[] tokens = {"This", ",", "is", "fine"};
        TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation(tokens);
        TextAnnotation goldTa = DummyTextAnnotationBuilder.buildTextAnnotation(tokens);
        CommaSRLSentence sentence = new CommaSRLSentence(ta, goldTa, Collections.emptyList());
        Comma comma = new Comma(1, sentence);

        assertEquals("is", comma.getWordToRight(1));
        assertEquals("fine", comma.getWordToRight(2));
    }
@Test
    public void testGetWordToRight_outOfBounds() {
        String[] tokens = {"Alpha", ",", "omega"};
        TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation(tokens);
        TextAnnotation goldTa = DummyTextAnnotationBuilder.buildTextAnnotation(tokens);
        CommaSRLSentence sentence = new CommaSRLSentence(ta, goldTa, Collections.emptyList());
        Comma comma = new Comma(1, sentence);

        assertEquals("###", comma.getWordToRight(3));
    }
@Test
    public void testGetWordToLeft_insideBounds() {
        String[] tokens = {"First", ",", "Second"};
        TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation(tokens);
        TextAnnotation goldTa = DummyTextAnnotationBuilder.buildTextAnnotation(tokens);
        CommaSRLSentence sentence = new CommaSRLSentence(ta, goldTa, Collections.emptyList());
        Comma comma = new Comma(1, sentence);

        assertEquals("First", comma.getWordToLeft(1));
    }
@Test
    public void testGetWordToLeft_outOfBounds() {
        String[] tokens = {"Word", ","};
        TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation(tokens);
        TextAnnotation goldTa = DummyTextAnnotationBuilder.buildTextAnnotation(tokens);
        CommaSRLSentence sentence = new CommaSRLSentence(ta, goldTa, Collections.emptyList());
        Comma comma = new Comma(0, sentence);

        assertEquals("$$$", comma.getWordToLeft(1));
    }
@Test
    public void testGetTextAnnotation_goldTrue() {
        String[] tokens = {"A", ",", "B"};
        TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation(tokens);
        TextAnnotation goldTa = DummyTextAnnotationBuilder.buildTextAnnotation(tokens);
        CommaSRLSentence sentence = new CommaSRLSentence(ta, goldTa, Collections.emptyList());
        Comma comma = new Comma(1, sentence);

        assertEquals(goldTa, comma.getTextAnnotation(true));
    }
@Test
    public void testGetTextAnnotation_goldFalse() {
        String[] tokens = {"A", ",", "B"};
        TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation(tokens);
        TextAnnotation goldTa = DummyTextAnnotationBuilder.buildTextAnnotation(tokens);
        CommaSRLSentence sentence = new CommaSRLSentence(ta, goldTa, Collections.emptyList());
        Comma comma = new Comma(1, sentence);

        assertEquals(ta, comma.getTextAnnotation(false));
    }
@Test
    public void testGetStrippedNotation_withNERandPOSOff() {
        String[] tokens = {"The", ",", "cat"};
        TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation(tokens);
        TextAnnotation goldTa = DummyTextAnnotationBuilder.buildTextAnnotation(tokens);
        Constituent fakeChunk = new Constituent("NP-ARG0", "view", ta, 0, 1);
        CommaSRLSentence sentence = new CommaSRLSentence(ta, goldTa, Collections.emptyList());
        Comma comma = new Comma(1, sentence);
        String result = comma.getStrippedNotation(fakeChunk);

        assertEquals("NP", result);
    }
@Test
    public void testGetAnnotatedText() {
        String[] tokens = {"Start", ",", "End"};
        TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation(tokens);
        TextAnnotation goldTa = DummyTextAnnotationBuilder.buildTextAnnotation(tokens);
        List<String> labels = Arrays.asList("LabelX");
        CommaSRLSentence sentence = new CommaSRLSentence(ta, goldTa, Collections.emptyList());
        Comma comma = new Comma(1, sentence, labels);

        String annotated = comma.getAnnotatedText();
        assertEquals("Start , [LabelX] End", annotated);
    }
@Test
    public void testGetBayraktarLabel_whenDefaultOther() {
        String[] tokens = {"A", ",", "B"};
        TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation(tokens);
        TextAnnotation goldTa = DummyTextAnnotationBuilder.buildTextAnnotation(tokens);
        CommaSRLSentence sentence = new CommaSRLSentence(ta, goldTa, Collections.emptyList());
        Comma comma = new Comma(1, sentence);

        String label = comma.getBayraktarLabel();
        assertNotNull(label);
    }
@Test
    public void testGetBayraktarPattern_format() {
        String[] tokens = {"A", ",", "B"};
        TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation(tokens);
        TextAnnotation goldTa = DummyTextAnnotationBuilder.buildTextAnnotation(tokens);
        CommaSRLSentence sentence = new CommaSRLSentence(ta, goldTa, Collections.emptyList());
        Comma comma = new Comma(1, sentence);

        String pattern = comma.getBayraktarPattern();
        assertNotNull(pattern);
        assertTrue(pattern.contains("-->"));
    } 
}