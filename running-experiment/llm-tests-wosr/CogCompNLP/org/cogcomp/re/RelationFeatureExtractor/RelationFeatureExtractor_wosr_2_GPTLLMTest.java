public class RelationFeatureExtractor_wosr_2_GPTLLMTest { 

 @Test
    public void testIsNounTrueForNNP() {
        String posTag = "NNP";
        assertTrue(RelationFeatureExtractor.isNoun(posTag));
    }
@Test
    public void testIsNounTrueForRB() {
        String posTag = "RB";
        assertTrue(RelationFeatureExtractor.isNoun(posTag));
    }
@Test
    public void testIsNounTrueForWP() {
        String posTag = "WP";
        assertTrue(RelationFeatureExtractor.isNoun(posTag));
    }
@Test
    public void testIsNounFalse() {
        String posTag = "VB";
        assertFalse(RelationFeatureExtractor.isNoun(posTag));
    }
@Test
    public void testOnlyNounBetweenTrue() throws Exception {
        String[] tokens = {"The", "quick", "brown", "fox"};
        TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens(Arrays.asList(tokens));

        View posView = new View(ViewNames.POS, "test", ta, 1.0);
        posView.addConstituent(new Constituent("NN", 1.0, ViewNames.POS, ta, 1, 2));
        ta.addView(ViewNames.POS, posView);

        Constituent front = new Constituent("PER", 1.0, "dummy", ta, 0, 1);
        Constituent back = new Constituent("ORG", 1.0, "dummy", ta, 2, 3);

        assertTrue(RelationFeatureExtractor.onlyNounBetween(front, back));
    }
@Test
    public void testOnlyNounBetweenFalse() throws Exception {
        String[] tokens = {"John", "is", "cool"};
        TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens(Arrays.asList(tokens));

        View posView = new View(ViewNames.POS, "test", ta, 1.0);
        posView.addConstituent(new Constituent("VBZ", 1.0, ViewNames.POS, ta, 1, 2));
        ta.addView(ViewNames.POS, posView);

        Constituent front = new Constituent("PER", 1.0, "dummy", ta, 0, 1);
        Constituent back = new Constituent("ORG", 1.0, "dummy", ta, 2, 3);

        assertFalse(RelationFeatureExtractor.onlyNounBetween(front, back));
    }
@Test
    public void testPatternRecognitionSameHeadSpan() throws Exception {
        String[] tokens = {"John", "Smith"};
        TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens(Arrays.asList(tokens));

        Constituent source = new Constituent("PER", 1.0, "dummy", ta, 0, 1);
        source.addAttribute("EntityHeadStartCharOffset", "0");
        source.addAttribute("EntityHeadEndCharOffset", "4");

        Constituent target = new Constituent("ORG", 1.0, "dummy", ta, 0, 1);
        target.addAttribute("EntityHeadStartCharOffset", "0");
        target.addAttribute("EntityHeadEndCharOffset", "4");

        List<String> features = RelationFeatureExtractor.patternRecognition(source, target);
        assertTrue(features.contains("SAME_SOURCE_TARGET_EXCEPTION"));
    }
@Test
    public void testGetCorefTagTrue() throws Exception {
        String[] tokens = {"John", "and", "he"};
        TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens(Arrays.asList(tokens));

        Constituent source = new Constituent("PER", 1.0, "dummy", ta, 0, 1);
        source.addAttribute("EntityID", "E1");

        Constituent target = new Constituent("PER", 1.0, "dummy", ta, 2, 3);
        target.addAttribute("EntityID", "E1");

        Relation r = new Relation("dummyRel", source, target, 1.0);

        RelationFeatureExtractor extractor = new RelationFeatureExtractor();
        String result = extractor.getCorefTag(r);
        assertEquals("TRUE", result);
    }
@Test
    public void testGetCorefTagFalseWhenIdsDifferent() throws Exception {
        String[] tokens = {"John", "and", "he"};
        TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens(Arrays.asList(tokens));

        Constituent source = new Constituent("PER", 1.0, "dummy", ta, 0, 1);
        source.addAttribute("EntityID", "E1");

        Constituent target = new Constituent("PER", 1.0, "dummy", ta, 2, 3);
        target.addAttribute("EntityID", "E2");

        Relation r = new Relation("dummyRel", source, target, 1.0);

        RelationFeatureExtractor extractor = new RelationFeatureExtractor();
        String result = extractor.getCorefTag(r);
        assertEquals("FALSE", result);
    }
@Test
    public void testGetCorefTagFalseWhenIdMissing() throws Exception {
        String[] tokens = {"John", "and", "he"};
        TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens(Arrays.asList(tokens));

        Constituent source = new Constituent("PER", 1.0, "dummy", ta, 0, 1);

        Constituent target = new Constituent("PER", 1.0, "dummy", ta, 2, 3);

        Relation r = new Relation("dummyRel", source, target, 1.0);

        RelationFeatureExtractor extractor = new RelationFeatureExtractor();
        String result = extractor.getCorefTag(r);
        assertEquals("FALSE", result);
    }
@Test
    public void testGetLexicalFeaturePartA() throws Exception {
        String[] tokens = {"John", "Smith", "works"};
        TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens(Arrays.asList(tokens));

        Constituent source = new Constituent("PERSON", 1.0, "dummy", ta, 0, 2);
        Constituent target = new Constituent("ORG", 1.0, "dummy", ta, 2, 3);
        Relation r = new Relation("employee_of", source, target, 1.0);

        RelationFeatureExtractor extractor = new RelationFeatureExtractor();
        List<String> features = extractor.getLexicalFeaturePartA(r);
        assertEquals(Arrays.asList("John", "Smith"), features);
    }
@Test
    public void testGetLexicalFeaturePartB() throws Exception {
        String[] tokens = {"John", "Smith", "works", "at", "Google"};
        TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens(Arrays.asList(tokens));

        Constituent source = new Constituent("PERSON", 1.0, "dummy", ta, 0, 2);
        Constituent target = new Constituent("ORG", 1.0, "dummy", ta, 4, 5);

        Relation r = new Relation("employee_of", source, target, 1.0);
        RelationFeatureExtractor extractor = new RelationFeatureExtractor();
        List<String> result = extractor.getLexicalFeaturePartB(r);

        assertEquals(Collections.singletonList("Google"), result);
    }
@Test
    public void testGetLexicalFeaturePartCWithSingleWord() throws Exception {
        String[] tokens = {"John", "Smith", "and", "Mary"};
        TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens(Arrays.asList(tokens));

        Constituent source = new Constituent("PER", 1.0, "dummy", ta, 0, 2);
        Constituent target = new Constituent("PER", 1.0, "dummy", ta, 3, 4);
        Relation r = new Relation("peer", source, target, 1.0);

        RelationFeatureExtractor extractor = new RelationFeatureExtractor();
        List<String> result = extractor.getLexicalFeaturePartC(r);

        assertEquals(Collections.singletonList("singleword_and"), result);
    }
@Test
    public void testGetLexicalFeaturePartDWithGap() throws Exception {
        String[] tokens = {"John", "and", "Mary", "work"};
        TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens(Arrays.asList(tokens));

        Constituent source = new Constituent("PER", 1.0, "dummy", ta, 0, 1);
        Constituent target = new Constituent("PER", 1.0, "dummy", ta, 3, 4);
        Relation r = new Relation("dummy", source, target, 1.0);

        RelationFeatureExtractor extractor = new RelationFeatureExtractor();
        List<String> result = extractor.getLexicalFeaturePartD(r);

        assertTrue(result.contains("between_first_and"));
        assertTrue(result.contains("between_first_Mary"));
    }
@Test
    public void testGetLexicalFeaturePartDWithNoGap() throws Exception {
        String[] tokens = {"John", "Mary"};
        TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens(Arrays.asList(tokens));

        Constituent source = new Constituent("PER", 1.0, "dummy", ta, 0, 1);
        Constituent target = new Constituent("PER", 1.0, "dummy", ta, 1, 2);
        Relation r = new Relation("dummy", source, target, 1.0);

        RelationFeatureExtractor extractor = new RelationFeatureExtractor();
        List<String> result = extractor.getLexicalFeaturePartD(r);

        assertTrue(result.contains("No_between_features"));
    }
@Test
    public void testGetLexicalFeaturePartF() throws Exception {
        String[] tokens = {"John", "Smith"};
        TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens(Arrays.asList(tokens));

        Constituent source = new Constituent("PER", 1.0, "EntityHeads", ta, 0, 1);
        Constituent target = new Constituent("ORG", 1.0, "EntityHeads", ta, 1, 2);
        source.addAttribute("IsPredicted", "true");
        target.addAttribute("IsPredicted", "true");
        Relation r = new Relation("dummy", source, target, 1.0);

        RelationFeatureExtractor extractor = new RelationFeatureExtractor();
        List<String> result = extractor.getLexicalFeaturePartF(r);
        assertTrue(result.stream().anyMatch(f -> f.startsWith("HM1_")));
        assertTrue(result.stream().anyMatch(f -> f.startsWith("HM2_")));
        assertTrue(result.stream().anyMatch(f -> f.startsWith("HM12_")));
    } 
}