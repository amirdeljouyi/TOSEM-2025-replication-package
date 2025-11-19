public class Comma_wosr_5_GPTLLMTest { 

 @Test
    public void testGetLabelAndGetLabels() {
        List<String> labels = new ArrayList<>();
        labels.add("LabelA");
        labels.add("LabelB");

        TextAnnotation ta = DummyTextAnnotationGenerator.generate("The quick, brown fox jumps.");
        CommaSRLSentence s = new CommaSRLSentence(ta, ta);

        Comma comma = new Comma(2, s, labels);

        assertEquals("LabelA", comma.getLabel());
        assertEquals(2, comma.getLabels().size());
        assertTrue(comma.getLabels().contains("LabelA"));
        assertTrue(comma.getLabels().contains("LabelB"));
    }
@Test
    public void testGetCommaID() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generate("One, two, three.");
        ta.setId("TA123");
        CommaSRLSentence s = new CommaSRLSentence(ta, ta);
        Comma comma = new Comma(1, s, Collections.singletonList("testLabel"));
        assertEquals("1 TA123", comma.getCommaID());
    }
@Test
    public void testGetWordToRightWithinBounds() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generate("A comma here , is clear.");
        CommaSRLSentence s = new CommaSRLSentence(ta, ta);
        Comma comma = new Comma(3, s, Collections.singletonList("label"));
        String right = comma.getWordToRight(1);
        assertEquals("is", right);
    }
@Test
    public void testGetWordToRightOutOfBounds() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generate("Word , end");
        CommaSRLSentence s = new CommaSRLSentence(ta, ta);
        Comma comma = new Comma(1, s, Collections.singletonList("label"));
        String right = comma.getWordToRight(2); 
        assertEquals("###", right);
    }
@Test
    public void testGetWordToLeftWithinBounds() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generate("Start here , now.");
        CommaSRLSentence s = new CommaSRLSentence(ta, ta);
        Comma comma = new Comma(2, s, Collections.singletonList("label"));
        String left = comma.getWordToLeft(1);
        assertEquals("here", left);
    }
@Test
    public void testGetWordToLeftOutOfBounds() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generate("Only , start");
        CommaSRLSentence s = new CommaSRLSentence(ta, ta);
        Comma comma = new Comma(0, s, Collections.singletonList("label"));
        String left = comma.getWordToLeft(1);
        assertEquals("$$$", left);
    }
@Test
    public void testGetPOSToLeftAndRight() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generate("The quick , red fox.");
        View posView = DummyTextAnnotationGenerator.addPOS(ta, Arrays.asList("DT", "JJ", ",", "JJ", "NN"));
        ta.addView(ViewNames.POS, posView);

        CommaSRLSentence s = new CommaSRLSentence(ta, ta);
        Comma.useGoldFeatures(false);
        Comma comma = new Comma(2, s, Collections.singletonList("label"));
        String left = comma.getPOSToLeft(1);
        String right = comma.getPOSToRight(1);
        assertEquals("JJ", left);
        assertEquals("JJ", right);
    }
@Test
    public void testGetChunkToLeftAndRightOfComma() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generate("This is , fine.");
        List<Pair<IntPair, String>> chunks = new ArrayList<>();
        chunks.add(Pair.of(new IntPair(0, 2), "NP")); 
        chunks.add(Pair.of(new IntPair(3, 4), "ADJP")); 

        View chunkView = DummyTextAnnotationGenerator.buildChunkView("chunk", ta, chunks);
        ta.addView(ViewNames.SHALLOW_PARSE, chunkView);

        CommaSRLSentence s = new CommaSRLSentence(ta, ta);
        Comma comma = new Comma(2, s, Collections.singletonList("label"));

        Constituent leftChunk = comma.getChunkToLeftOfComma(1);
        Constituent rightChunk = comma.getChunkToRightOfComma(1);

        assertNotNull(leftChunk);
        assertNotNull(rightChunk);
        assertEquals("NP", leftChunk.getLabel());
        assertEquals("ADJP", rightChunk.getLabel());
    }
@Test
    public void testGetBayraktarLabelDefault() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generate("This , is example .");
        View parseView = DummyTextAnnotationGenerator.emptyParseViewWithComma(ta, ViewNames.PARSE_STANFORD, 1);
        ta.addView(ViewNames.PARSE_STANFORD, parseView);

        CommaSRLSentence s = new CommaSRLSentence(ta, ta);
        Comma comma = new Comma(1, s, Collections.singletonList("label"));
        Comma.useGoldFeatures(false);

        String label = comma.getBayraktarLabel();
        assertNotNull(label);
        assertEquals("Other", label); 
    }
@Test
    public void testGetAnnotatedText() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generate("A quick , test example.");
        CommaSRLSentence s = new CommaSRLSentence(ta, ta);
        List<String> labels = new ArrayList<>();
        labels.add("X");
        labels.add("Y");
        Comma comma = new Comma(2, s, labels);
        String result = comma.getAnnotatedText();
        assertTrue(result.contains("[X,Y]"));
    }
@Test
    public void testGetWordNgrams() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generate("The fast , brown fox.");
        CommaSRLSentence s = new CommaSRLSentence(ta, ta);
        Comma comma = new Comma(2, s, Collections.singletonList("label"));

        String[] ngrams = comma.getWordNgrams();
        assertNotNull(ngrams);
        assertTrue(ngrams.length > 0);
    }
@Test
    public void testGetPOSNgrams() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generate("The fast , brown fox jumps high .");
        View pos = DummyTextAnnotationGenerator.addPOS(ta, Arrays.asList("DT", "JJ", ",", "JJ", "NN", "VBZ", "RB", "."));
        ta.addView(ViewNames.POS, pos);

        CommaSRLSentence s = new CommaSRLSentence(ta, ta);
        Comma comma = new Comma(2, s, Collections.singletonList("label"));
        String[] posNgrams = comma.getPOSNgrams();

        assertNotNull(posNgrams);
        assertTrue(posNgrams.length > 0);
    }
@Test
    public void testGetNotationHandlesNull() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generate("This is fine.");
        CommaSRLSentence s = new CommaSRLSentence(ta, ta);
        Comma comma = new Comma(1, s, Collections.singletonList("test"));
        assertEquals("NULL", comma.getNotation(null));
    }
@Test
    public void testGetStrippedNotationBasic() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generate("This is fine.");
        Constituent c = new Constituent("NN-SOMETHING", "view", ta, 0, 1);
        View dummyView = new TokenLabelView("view", "annotator", ta, 1.0);
        dummyView.addConstituent(c);
        ta.addView("view", dummyView);

        CommaSRLSentence s = new CommaSRLSentence(ta, ta);
        Comma comma = new Comma(0, s, Collections.singletonList("label"));
        assertTrue(comma.getStrippedNotation(c).startsWith("NN"));
    } 
}