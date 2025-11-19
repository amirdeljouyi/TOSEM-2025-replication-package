public class Comma_wosr_1_GPTLLMTest { 

 @Test
    public void testGetLabel() {
        List<String> labels = new ArrayList<>();
        labels.add("APPOS");
        TextAnnotation ta = DummyTextAnnotationGenerator.generateSimpleTextAnnotation();
        CommaSRLSentence s = new CommaSRLSentence(ta, ta);
        Comma comma = new Comma(2, s, labels);
        assertEquals("APPOS", comma.getLabel());
    }
@Test
    public void testGetLabels() {
        List<String> labels = Arrays.asList("APPOS", "GAPPING");
        TextAnnotation ta = DummyTextAnnotationGenerator.generateSimpleTextAnnotation();
        CommaSRLSentence s = new CommaSRLSentence(ta, ta);
        Comma comma = new Comma(1, s, labels);
        assertEquals(labels, comma.getLabels());
    }
@Test
    public void testGetPosition() {
        List<String> labels = Arrays.asList("ADV");
        TextAnnotation ta = DummyTextAnnotationGenerator.generateSimpleTextAnnotation();
        CommaSRLSentence s = new CommaSRLSentence(ta, ta);
        Comma comma = new Comma(3, s, labels);
        assertEquals(3, comma.getPosition());
    }
@Test
    public void testGetSentence() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generateSimpleTextAnnotation();
        CommaSRLSentence s = new CommaSRLSentence(ta, ta);
        Comma comma = new Comma(0, s);
        assertEquals(s, comma.getSentence());
    }
@Test
    public void testGetCommaID() {
        List<String> labels = Arrays.asList("ADV");
        TextAnnotation ta = DummyTextAnnotationGenerator.generateSimpleTextAnnotation();
        CommaSRLSentence s = new CommaSRLSentence(ta, ta);
        Comma comma = new Comma(5, s, labels);
        assertEquals("5 dummy", comma.getCommaID());
    }
@Test
    public void testGetWordToRightInBounds() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generateCustomTextAnnotation("She went to the store , then came home .");
        CommaSRLSentence s = new CommaSRLSentence(ta, ta);
        Comma comma = new Comma(5, s); 
        assertEquals("then", comma.getWordToRight(1));
    }
@Test
    public void testGetWordToRightOutOfBounds() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generateCustomTextAnnotation("He sleeps ,");
        CommaSRLSentence s = new CommaSRLSentence(ta, ta);
        Comma comma = new Comma(2, s); 
        assertEquals("###", comma.getWordToRight(1));
    }
@Test
    public void testGetWordToLeftInBounds() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generateCustomTextAnnotation("He sleeps , quietly");
        CommaSRLSentence s = new CommaSRLSentence(ta, ta);
        Comma comma = new Comma(2, s);
        assertEquals("sleeps", comma.getWordToLeft(1));
    }
@Test
    public void testGetWordToLeftOutOfBounds() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generateCustomTextAnnotation("Hi , there");
        CommaSRLSentence s = new CommaSRLSentence(ta, ta);
        Comma comma = new Comma(1, s);
        assertEquals("Hi", comma.getWordToLeft(1));
        assertEquals("$$$", comma.getWordToLeft(2));
    }
@Test
    public void testGetPostoLeftDTTheSpecialCase() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generateCustomTextAnnotation(
                "The house , is big");
        ta.addView(ViewNames.POS, DummyViewFactory.createPosView(ta, new String[]{"DT", "NN", ",", "VBZ", "JJ"}));
        CommaSRLSentence s = new CommaSRLSentence(ta, ta);
        Comma useGoldTrue = new Comma(2, s);
        Comma.useGoldFeatures(false);
        assertEquals("DT-the", useGoldTrue.getPOSToLeft(1));
    }
@Test
    public void testGetPostoRightDTTheSpecialCase() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generateCustomTextAnnotation(
                "We saw , the house");
        ta.addView(ViewNames.POS, DummyViewFactory.createPosView(ta, new String[]{"PRP", "VBD", ",", "DT", "NN"}));
        CommaSRLSentence s = new CommaSRLSentence(ta, ta);
        Comma.useGoldFeatures(false);
        Comma comma = new Comma(2, s);
        assertEquals("DT-the", comma.getPOSToRight(1));
    }
@Test
    public void testGetPhraseToLeftOfCommaReturnsNullWhenNotFound() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generateMinimalParseTreeForCommaTest();
        CommaSRLSentence s = new CommaSRLSentence(ta, ta);
        Comma.useGoldFeatures(false);
        Comma comma = new Comma(3, s);
        assertNull(comma.getPhraseToLeftOfComma(10));
    }
@Test
    public void testGetChunkToRightOfCommaNullSafeAccess() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generateChunkedTextAnnotation();
        CommaSRLSentence s = new CommaSRLSentence(ta, ta);
        Comma comma = new Comma(3, s);
        assertNull(comma.getChunkToRightOfComma(100));
    }
@Test
    public void testGetChunkToLeftOfCommaNullSafeAccess() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generateChunkedTextAnnotation();
        CommaSRLSentence s = new CommaSRLSentence(ta, ta);
        Comma comma = new Comma(3, s);
        assertNull(comma.getChunkToLeftOfComma(100));
    }
@Test
    public void testGetBayraktarLabelReturnsFallback() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generateSimpleTextAnnotation();
        CommaSRLSentence s = new CommaSRLSentence(ta, ta);
        Comma comma = new Comma(3, s);
        String label = comma.getBayraktarLabel();
        assertNotNull(label);
        assertTrue(label.equals("Other") || label.matches("\\w+"));
    }
@Test
    public void testGetAnnotatedText() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generateCustomTextAnnotation("The boy , ran home");
        List<String> labels = Arrays.asList("APPOS");
        CommaSRLSentence s = new CommaSRLSentence(ta, ta);
        Comma comma = new Comma(2, s, labels); 
        String text = comma.getAnnotatedText();
        assertTrue(text.contains("[APPOS]"));
    }
@Test
    public void testGetBayraktarAnnotatedText() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generateSimpleTextAnnotation();
        CommaSRLSentence s = new CommaSRLSentence(ta, ta);
        Comma comma = new Comma(1, s);
        String annotated = comma.getBayraktarAnnotatedText();
        assertTrue(annotated.contains("]"));
    }
@Test
    public void testGetStrippedNotation() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generateSimpleChunkedTextAnnotation();
        Constituent c = new Constituent("NP-LOC", "dummyView", ta, 0, 2);
        CommaSRLSentence s = new CommaSRLSentence(ta, ta);
        Comma comma = new Comma(1, s);
        String notation = comma.getStrippedNotation(c);
        assertTrue(notation.startsWith("NP"));
    }
@Test
    public void testGetWordNgramsLength() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generateCustomTextAnnotation("We quickly , ran fast home");
        CommaSRLSentence s = new CommaSRLSentence(ta, ta);
        Comma comma = new Comma(2, s);
        String[] ngrams = comma.getWordNgrams();
        assertNotNull(ngrams);
        assertTrue(ngrams.length > 0);
    }
@Test
    public void testGetChunkNgramsLength() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generateChunkedTextAnnotation();
        CommaSRLSentence s = new CommaSRLSentence(ta, ta);
        Comma comma = new Comma(3, s);
        String[] chunks = comma.getChunkNgrams();
        assertNotNull(chunks);
        assertTrue(chunks.length > 0);
    }
@Test
    public void testGetPOSNgramsContainsBigram() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generateCustomTextAnnotation("I saw it , yesterday .");
        ta.addView(ViewNames.POS, DummyViewFactory.createPosView(ta, new String[]{"PRP", "VBD", "PRP", ",", "NN", "."}));
        CommaSRLSentence s = new CommaSRLSentence(ta, ta);
        Comma comma = new Comma(3, s);
        String[] posNgrams = comma.getPOSNgrams();
        boolean hasBigram = false;
        for (String n : posNgrams) {
            if (n.split(" ").length == 2) {
                hasBigram = true;
                break;
            }
        }
        assertTrue(hasBigram);
    } 
}