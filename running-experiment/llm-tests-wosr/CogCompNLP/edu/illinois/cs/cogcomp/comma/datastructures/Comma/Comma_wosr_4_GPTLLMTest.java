public class Comma_wosr_4_GPTLLMTest { 

 @Test
    public void testGetLabelAndLabels() {
        List<String> labels = Arrays.asList("Cause", "Contrast");
        TextAnnotation ta = DummyTextAnnotationGenerator.generateTextAnnotationWithPOSAndShallowParse("John, who is a teacher, lives in Chicago.");
        CommaSRLSentence sentence = new CommaSRLSentence(ta, ta);
        Comma comma = new Comma(1, sentence, labels);

        assertEquals("Cause", comma.getLabel());
        assertEquals(labels, comma.getLabels());
    }
@Test
    public void testGetWordToRightAndLeft() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generateTextAnnotation("The dog, big and hairy, barked.");
        CommaSRLSentence sentence = new CommaSRLSentence(ta, ta);
        Comma comma = new Comma(2, sentence);

        assertEquals("big", comma.getWordToRight(1));
        assertEquals("The", comma.getWordToLeft(2));
    }
@Test
    public void testGetWordOutOfBoundsReturnsPlaceholders() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generateTextAnnotation("Hi,");
        CommaSRLSentence sentence = new CommaSRLSentence(ta, ta);
        Comma comma = new Comma(1, sentence);

        assertEquals("###", comma.getWordToRight(1));
        assertEquals("Hi", comma.getWordToLeft(1));
        assertEquals("$$$", comma.getWordToLeft(2));
    }
@Test
    public void testGetPOSLeftRightWithDTSpecialCase() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generateTextAnnotationWithPOS("He said , the man left .");
        CommaSRLSentence sentence = new CommaSRLSentence(ta, ta);
        Comma comma = new Comma(2, sentence);

        assertEquals("VBD", comma.getPOSToLeft(1));  
        assertEquals("DT-the", comma.getPOSToRight(1));  
    }
@Test
    public void testGetChunkToRightAndLeftOfCommaReturnsCorrectChunks() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generateTextAnnotationWithShallowParse("John, the tall teacher, waved.");
        CommaSRLSentence sentence = new CommaSRLSentence(ta, ta);
        Comma comma = new Comma(1, sentence);

        Constituent rightChunk = comma.getChunkToRightOfComma(1);
        Constituent leftChunk = comma.getChunkToLeftOfComma(1);

        assertNotNull(rightChunk);
        assertNotNull(leftChunk);
        assertEquals("NP", rightChunk.getLabel());
        assertEquals("NNP", leftChunk.getLabel());
    }
@Test
    public void testGetStrippedNotationWithPOSAndNEROn() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generateNERAndPOSAnnotatedTextAnnotation("Barack Obama, the president, spoke.");
        CommaSRLSentence sentence = new CommaSRLSentence(ta, ta);
        Comma comma = new Comma(2, sentence);

        Constituent chunk = comma.getChunkToLeftOfComma(1);
        String notation = comma.getStrippedNotation(chunk);
        assertTrue(notation.startsWith("NP"));
        assertTrue(notation.contains("NNP"));
    }
@Test
    public void testGetCommaID() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generateTextAnnotation("Hello, world.");
        CommaSRLSentence sentence = new CommaSRLSentence(ta, ta);
        Comma comma = new Comma(1, sentence);

        String expected = "1 " + ta.getId();
        assertEquals(expected, comma.getCommaID());
    }
@Test
    public void testGetBayraktarLabelReturnsOtherIfNull() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generateTextAnnotation("X, Y.");
        CommaSRLSentence sentence = new CommaSRLSentence(ta, ta);
        Comma comma = new Comma(1, sentence);

        String label = comma.getBayraktarLabel();
        assertEquals("Other", label);
    }
@Test
    public void testGetBayraktarPatternWithSimpleSentence() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generateTextAnnotationWithParse("He ran, but fell.");
        CommaSRLSentence sentence = new CommaSRLSentence(ta, ta);
        Comma comma = new Comma(2, sentence);

        String pattern = comma.getBayraktarPattern();
        assertNotNull(pattern);
        assertTrue(pattern.contains("-->"));
    }
@Test
    public void testGetLeftToRightDependencies() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generateDependencyAnnotated("The man, old and wise, spoke.");
        CommaSRLSentence sentence = new CommaSRLSentence(ta, ta);
        Comma comma = new Comma(2, sentence);

        String[] dependencies = comma.getLeftToRightDependencies();
        assertNotNull(dependencies);
    }
@Test
    public void testGetRightToLeftDependencies() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generateDependencyAnnotated("The man, calm and quiet, walked.");
        CommaSRLSentence sentence = new CommaSRLSentence(ta, ta);
        Comma comma = new Comma(2, sentence);

        String[] dependencies = comma.getRightToLeftDependencies();
        assertNotNull(dependencies);
    }
@Test
    public void testGetAnnotatedText() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generateTextAnnotation("First, then later.");
        List<String> labels = Arrays.asList("Temporal");
        CommaSRLSentence sentence = new CommaSRLSentence(ta, ta);
        Comma comma = new Comma(1, sentence, labels);

        String annotated = comma.getAnnotatedText();
        assertTrue(annotated.contains("[Temporal]"));
    }
@Test
    public void testGetBayraktarAnnotatedText() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generateTextAnnotation("Yes, indeed she did.");
        CommaSRLSentence sentence = new CommaSRLSentence(ta, ta);
        Comma comma = new Comma(1, sentence);

        String annotated = comma.getBayraktarAnnotatedText();
        assertTrue(annotated.contains("Other"));
    }
@Test
    public void testIsSiblingTrueForSameParent() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generateTextAnnotationWithParse("He ran fast, and tripped, but stood up.");
        CommaSRLSentence sentence = new CommaSRLSentence(ta, ta);
        Comma comma1 = new Comma(4, sentence);
        Comma comma2 = new Comma(7, sentence);

        assertTrue(comma1.isSibling(comma2));
    }
@Test
    public void testIsSiblingFalseForDifferentParents() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generateTextAnnotationWithParse("Although late, she arrived.");
        CommaSRLSentence sentence = new CommaSRLSentence(ta, ta);
        Comma comma1 = new Comma(1, sentence);
        Comma comma2 = new Comma(3, sentence);

        assertFalse(comma1.isSibling(comma2));
    }
@Test
    public void testGetWordNgramsBasic() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generateTextAnnotation("Run, jump, and fall.");
        CommaSRLSentence sentence = new CommaSRLSentence(ta, ta);
        Comma comma = new Comma(1, sentence);

        String[] ngrams = comma.getWordNgrams();
        assertTrue(ngrams.length > 0);
    }
@Test
    public void testGetPOSNgramsReturnsMultiple() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generateTextAnnotationWithPOS("He ran, fell and got up.");
        CommaSRLSentence sentence = new CommaSRLSentence(ta, ta);
        Comma comma = new Comma(2, sentence);

        String[] posNgrams = comma.getPOSNgrams();
        assertTrue(posNgrams.length > 0);
    }
@Test
    public void testGetChunkNgrams() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generateTextAnnotationWithShallowParse("Tom, a teacher, spoke.");
        CommaSRLSentence sentence = new CommaSRLSentence(ta, ta);
        Comma comma = new Comma(1, sentence);

        String[] chunkNgrams = comma.getChunkNgrams();
        assertTrue(chunkNgrams.length > 0);
    }
@Test
    public void testGetParentSiblingPhraseNgrams() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generateTextAnnotationWithParse("He danced, but tripped, then ran.");
        CommaSRLSentence sentence = new CommaSRLSentence(ta, ta);
        Comma comma = new Comma(2, sentence);

        String[] ngrams = comma.getParentSiblingPhraseNgrams();
        assertNotNull(ngrams);
        assertTrue(ngrams.length >= 1);
    } 
}