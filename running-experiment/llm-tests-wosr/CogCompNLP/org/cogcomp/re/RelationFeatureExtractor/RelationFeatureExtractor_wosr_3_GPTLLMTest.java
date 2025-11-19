public class RelationFeatureExtractor_wosr_3_GPTLLMTest { 

 @Test
    public void testIsNoun_positiveCases() {
        assertTrue(RelationFeatureExtractor.isNoun("NN"));
        assertTrue(RelationFeatureExtractor.isNoun("NNS"));
        assertTrue(RelationFeatureExtractor.isNoun("NNP"));
        assertTrue(RelationFeatureExtractor.isNoun("RB"));
        assertTrue(RelationFeatureExtractor.isNoun("RBR"));
        assertTrue(RelationFeatureExtractor.isNoun("WP"));
        assertTrue(RelationFeatureExtractor.isNoun("WPS"));
    }
@Test
    public void testIsNoun_negativeCases() {
        assertFalse(RelationFeatureExtractor.isNoun("VB"));
        assertFalse(RelationFeatureExtractor.isNoun("JJ"));
        assertFalse(RelationFeatureExtractor.isNoun("IN"));
        assertFalse(RelationFeatureExtractor.isNoun("TO"));
    }
@Test
    public void testGetCorefTag_trueCase() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generateTextAnnotation();
        Constituent c1 = new Constituent("PER", 1.0, "MENTION", ta, 0, 1);
        Constituent c2 = new Constituent("PER", 1.0, "MENTION", ta, 2, 3);
        c1.addAttribute("EntityID", "E1");
        c2.addAttribute("EntityID", "E1");
        Relation r = new Relation("testRel", c1, c2, 1.0);
        RelationFeatureExtractor extractor = new RelationFeatureExtractor();
        String tag = extractor.getCorefTag(r);
        assertEquals("TRUE", tag);
    }
@Test
    public void testGetCorefTag_falseCase() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generateTextAnnotation();
        Constituent c1 = new Constituent("PER", 1.0, "MENTION", ta, 0, 1);
        Constituent c2 = new Constituent("PER", 1.0, "MENTION", ta, 2, 3);
        c1.addAttribute("EntityID", "E1");
        c2.addAttribute("EntityID", "E2");
        Relation r = new Relation("testRel", c1, c2, 1.0);
        RelationFeatureExtractor extractor = new RelationFeatureExtractor();
        String tag = extractor.getCorefTag(r);
        assertEquals("FALSE", tag);
    }
@Test
    public void testGetLexicalFeaturePartA_singleWord() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generateTextAnnotation("John lives in Chicago");
        Constituent source = new Constituent("PER", 1.0, "MENTION", ta, 0, 1);
        Constituent target = new Constituent("LOC", 1.0, "MENTION", ta, 3, 4);
        Relation r = new Relation("testRel", source, target, 1.0);
        RelationFeatureExtractor extractor = new RelationFeatureExtractor();
        List<String> result = extractor.getLexicalFeaturePartA(r);
        assertEquals(1, result.size());
        assertEquals("John", result.get(0));
    }
@Test
    public void testGetLexicalFeaturePartB_singleWord() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generateTextAnnotation("John lives in Chicago");
        Constituent source = new Constituent("PER", 1.0, "MENTION", ta, 0, 1);
        Constituent target = new Constituent("LOC", 1.0, "MENTION", ta, 3, 4);
        Relation r = new Relation("testRel", source, target, 1.0);
        RelationFeatureExtractor extractor = new RelationFeatureExtractor();
        List<String> result = extractor.getLexicalFeaturePartB(r);
        assertEquals(1, result.size());
        assertEquals("Chicago", result.get(0));
    }
@Test
    public void testOnlyNounBetween_trueCase() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generateTextAnnotation("John manager location");
        ta.addView(ViewNames.POS, DummyTextAnnotationGenerator.createPosView(ta, new String[]{"NNP", "NN", "NN"}));
        Constituent front = new Constituent("PER", 1.0, "MENTION", ta, 0, 1);
        Constituent back = new Constituent("LOC", 1.0, "MENTION", ta, 2, 3);
        RelationFeatureExtractor extractor = new RelationFeatureExtractor();
        boolean result = RelationFeatureExtractor.onlyNounBetween(front, back);
        assertTrue(result);
    }
@Test
    public void testOnlyNounBetween_falseCase() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generateTextAnnotation("John likes pizza");
        ta.addView(ViewNames.POS, DummyTextAnnotationGenerator.createPosView(ta, new String[]{"NNP", "VB", "NN"}));
        Constituent front = new Constituent("PER", 1.0, "MENTION", ta, 0, 1);
        Constituent back = new Constituent("FOOD", 1.0, "MENTION", ta, 2, 3);
        RelationFeatureExtractor extractor = new RelationFeatureExtractor();
        boolean result = RelationFeatureExtractor.onlyNounBetween(front, back);
        assertFalse(result);
    }
@Test
    public void testPatternRecognition_sameHeadSpan() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generateTextAnnotation("John lives in Chicago");
        Constituent source = new Constituent("PER", 1.0, "MENTION", ta, 0, 1);
        Constituent target = new Constituent("PER", 1.0, "MENTION", ta, 0, 1);
        List<String> result = RelationFeatureExtractor.patternRecognition(source, target);
        assertTrue(result.contains("SAME_SOURCE_TARGET_EXCEPTION"));
    }
@Test
    public void testPatternRecognition_sameExtentSpan() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generateTextAnnotation("John lives in Chicago");
        Constituent source = new Constituent("PER", 1.0, "MENTION", ta, 1, 2);
        Constituent target = new Constituent("PER", 1.0, "MENTION", ta, 1, 2);
        List<String> result = RelationFeatureExtractor.patternRecognition(source, target);
        assertTrue(result.contains("SAME_SOURCE_TARGET_EXTENT_EXCEPTION"));
    }
@Test
    public void testGetLexicalFeaturePartC_singleWordBetweenEntities() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generateTextAnnotation("President of Company");
        Constituent source = new Constituent("PER", 1.0, "MENTION", ta, 0, 1);
        Constituent target = new Constituent("ORG", 1.0, "MENTION", ta, 2, 3);
        Relation r = new Relation("testRel", source, target, 1.0);
        RelationFeatureExtractor extractor = new RelationFeatureExtractor();
        List<String> result = extractor.getLexicalFeaturePartC(r);
        assertEquals(1, result.size());
        assertEquals("singleword_of", result.get(0));
    }
@Test
    public void testGetLexicalFeaturePartC_noSingleWord() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generateTextAnnotation("President is head of Company");
        Constituent source = new Constituent("PER", 1.0, "MENTION", ta, 0, 1);
        Constituent target = new Constituent("ORG", 1.0, "MENTION", ta, 4, 5);
        Relation r = new Relation("testRel", source, target, 1.0);
        RelationFeatureExtractor extractor = new RelationFeatureExtractor();
        List<String> result = extractor.getLexicalFeaturePartC(r);
        assertEquals(1, result.size());
        assertEquals("No_singleword", result.get(0));
    }
@Test
    public void testGetLexicalFeaturePartD_multipleBetweenTokens() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generateTextAnnotation("John traveled through the city");
        Constituent source = new Constituent("PER", 1.0, "MENTION", ta, 0, 1);
        Constituent target = new Constituent("LOC", 1.0, "MENTION", ta, 4, 5);
        Relation r = new Relation("testRel", source, target, 1.0);
        RelationFeatureExtractor extractor = new RelationFeatureExtractor();
        List<String> result = extractor.getLexicalFeaturePartD(r);
        assertFalse(result.contains("No_between_features"));
        assertTrue(result.contains("between_first_traveled"));
        assertTrue(result.contains("between_first_the"));
        assertTrue(result.contains("in_between_through"));
    }
@Test
    public void testGetLexicalFeaturePartD_noBetweenTokens() {
        TextAnnotation ta = DummyTextAnnotationGenerator.generateTextAnnotation("John and Mary");
        Constituent source = new Constituent("PER", 1.0, "MENTION", ta, 0, 1);
        Constituent target = new Constituent("PER", 1.0, "MENTION", ta, 1, 2);
        Relation r = new Relation("testRel", source, target, 1.0);
        RelationFeatureExtractor extractor = new RelationFeatureExtractor();
        List<String> result = extractor.getLexicalFeaturePartD(r);
        assertTrue(result.contains("No_between_features"));
    } 
}