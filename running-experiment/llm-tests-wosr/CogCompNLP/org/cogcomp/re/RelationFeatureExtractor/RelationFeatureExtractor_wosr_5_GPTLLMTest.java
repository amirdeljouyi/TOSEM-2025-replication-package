public class RelationFeatureExtractor_wosr_5_GPTLLMTest { 

 @Test
    public void testGetLexicalFeaturePartA_returnsCorrectTokens() throws Exception {
        String text = "Barack Obama visited Paris.";
        Tokenizer tokenizer = TokenizerProvider.getInstance().getTokenizer();
        TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
        TextAnnotation ta = builder.createTextAnnotation("test", "1", text);

        Constituent source = new Constituent("PER", 1.0, "MENTION", ta, 0, 2);
        Constituent target = new Constituent("LOC", 1.0, "MENTION", ta, 3, 4);
        Relation relation = new Relation("test", source, target, 1.0);

        RelationFeatureExtractor extractor = new RelationFeatureExtractor();
        List<String> features = extractor.getLexicalFeaturePartA(relation);
        assertEquals(Arrays.asList("Barack", "Obama"), features);
    }
@Test
    public void testGetLexicalFeaturePartB_returnsCorrectTokens() throws Exception {
        String text = "President Obama arrived in Berlin.";
        Tokenizer tokenizer = TokenizerProvider.getInstance().getTokenizer();
        TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
        TextAnnotation ta = builder.createTextAnnotation("test", "1", text);

        Constituent source = new Constituent("PER", 1.0, "MENTION", ta, 1, 2);
        Constituent target = new Constituent("LOC", 1.0, "MENTION", ta, 4, 5);
        Relation relation = new Relation("test", source, target, 1.0);

        RelationFeatureExtractor extractor = new RelationFeatureExtractor();
        List<String> features = extractor.getLexicalFeaturePartB(relation);
        assertEquals(Collections.singletonList("Berlin"), features);
    }
@Test
    public void testGetLexicalFeaturePartC_withSingleWordBetween_returnsFeature() throws Exception {
        String text = "Obama and Merkel.";
        Tokenizer tokenizer = TokenizerProvider.getInstance().getTokenizer();
        TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
        TextAnnotation ta = builder.createTextAnnotation("test", "1", text);

        Constituent source = new Constituent("PER", 1.0, "MENTION", ta, 0, 1);
        Constituent target = new Constituent("PER", 1.0, "MENTION", ta, 2, 3);
        Relation relation = new Relation("test", source, target, 1.0);

        RelationFeatureExtractor extractor = new RelationFeatureExtractor();
        List<String> features = extractor.getLexicalFeaturePartC(relation);
        assertEquals(Collections.singletonList("singleword_and"), features);
    }
@Test
    public void testGetLexicalFeaturePartC_withNoSingleWordBetween_returnsNoSingleword() throws Exception {
        String text = "Obama met Merkel in Berlin.";
        Tokenizer tokenizer = TokenizerProvider.getInstance().getTokenizer();
        TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
        TextAnnotation ta = builder.createTextAnnotation("test", "1", text);

        Constituent source = new Constituent("PER", 1.0, "MENTION", ta, 0, 1);
        Constituent target = new Constituent("PER", 1.0, "MENTION", ta, 2, 3);
        Relation relation = new Relation("test", source, target, 1.0);

        RelationFeatureExtractor extractor = new RelationFeatureExtractor();
        List<String> features = extractor.getLexicalFeaturePartC(relation);
        assertEquals(Collections.singletonList("No_singleword"), features);
    }
@Test
    public void testIsNoun_returnsTrueForNouns() {
        assertTrue(RelationFeatureExtractor.isNoun("NN"));
        assertTrue(RelationFeatureExtractor.isNoun("NNP"));
        assertTrue(RelationFeatureExtractor.isNoun("RB"));
        assertTrue(RelationFeatureExtractor.isNoun("WP"));
    }
@Test
    public void testIsNoun_returnsFalseForNonNouns() {
        assertFalse(RelationFeatureExtractor.isNoun("VB"));
        assertFalse(RelationFeatureExtractor.isNoun("IN"));
        assertFalse(RelationFeatureExtractor.isNoun("DT"));
    }
@Test
    public void testPatternRecognition_sameHeadSpan_returnsSameSourceTargetException() throws Exception {
        String text = "Obama met Obama.";
        Tokenizer tokenizer = TokenizerProvider.getInstance().getTokenizer();
        TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
        TextAnnotation ta = builder.createTextAnnotation("test", "1", text);

        Constituent cons1 = new Constituent("PER", 1.0, "MENTION", ta, 0, 1);
        Constituent cons2 = new Constituent("PER", 1.0, "MENTION", ta, 0, 1);

        cons1.addAttribute("EnityType", "PER"); 
        cons2.addAttribute("EntityType", "PER");

        List<String> features = RelationFeatureExtractor.patternRecognition(cons1, cons2);
        assertTrue(features.contains("SAME_SOURCE_TARGET_EXCEPTION"));
    }
@Test
    public void testGetCorefTag_returnsTRUEWhenEntityIDMatches() throws Exception {
        String text = "Obama is the president. He gave a speech.";
        Tokenizer tokenizer = TokenizerProvider.getInstance().getTokenizer();
        TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
        TextAnnotation ta = builder.createTextAnnotation("test", "1", text);

        Constituent cons1 = new Constituent("PER", 1.0, "MENTION", ta, 0, 1);
        Constituent cons2 = new Constituent("PER", 1.0, "MENTION", ta, 5, 6);
        cons1.addAttribute("EntityID", "E1");
        cons2.addAttribute("EntityID", "E1");

        Relation relation = new Relation("test", cons1, cons2, 1.0);
        RelationFeatureExtractor extractor = new RelationFeatureExtractor();
        String result = extractor.getCorefTag(relation);
        assertEquals("TRUE", result);
    }
@Test
    public void testGetCorefTag_returnsFALSEWhenEntityIDDiffers() throws Exception {
        String text = "Obama met Merkel.";
        Tokenizer tokenizer = TokenizerProvider.getInstance().getTokenizer();
        TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
        TextAnnotation ta = builder.createTextAnnotation("test", "1", text);

        Constituent cons1 = new Constituent("PER", 1.0, "MENTION", ta, 0, 1);
        Constituent cons2 = new Constituent("PER", 1.0, "MENTION", ta, 2, 3);
        cons1.addAttribute("EntityID", "E1");
        cons2.addAttribute("EntityID", "E2");

        Relation relation = new Relation("test", cons1, cons2, 1.0);
        RelationFeatureExtractor extractor = new RelationFeatureExtractor();
        String result = extractor.getCorefTag(relation);
        assertEquals("FALSE", result);
    }
@Test
    public void testGetLexicalFeaturePartD_returnsCorrectBetweenTokens() throws Exception {
        String text = "Obama met Angela Merkel in Berlin.";
        Tokenizer tokenizer = TokenizerProvider.getInstance().getTokenizer();
        TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
        TextAnnotation ta = builder.createTextAnnotation("test", "1", text);

        Constituent source = new Constituent("PER", 1.0, "MENTION", ta, 0, 1);
        Constituent target = new Constituent("GPE", 1.0, "MENTION", ta, 6, 7);
        Relation relation = new Relation("test", source, target, 1.0);

        RelationFeatureExtractor extractor = new RelationFeatureExtractor();
        List<String> features = extractor.getLexicalFeaturePartD(relation);
        assertTrue(features.contains("between_first_met"));
    }
@Test
    public void testGetLexicalFeaturePartE_prefixSuffixOnSameSentence() throws Exception {
        String text = "Mr. Barack Obama went to Paris.";
        Tokenizer tokenizer = TokenizerProvider.getInstance().getTokenizer();
        TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
        TextAnnotation ta = builder.createTextAnnotation("test", "1", text);

        Sentence sentence = ta.getSentence(0);
        Constituent source = new Constituent("PER", 1.0, "MENTION", ta, 1, 3);
        Constituent target = new Constituent("LOC", 1.0, "MENTION", ta, 5, 6);
        Relation relation = new Relation("test", source, target, 1.0);

        RelationFeatureExtractor extractor = new RelationFeatureExtractor();
        List<String> features = extractor.getLexicalFeaturePartE(relation);
        assertTrue(features.contains("fwM1_Mr."));
        assertTrue(features.contains("fwM2_Paris"));
    }
@Test
    public void testGetLexicalFeaturePartF_returnsHeadFormFeatures() throws Exception {
        String text = "Obama met Merkel.";
        Tokenizer tokenizer = TokenizerProvider.getInstance().getTokenizer();
        TokenizerTextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(tokenizer);
        TextAnnotation ta = builder.createTextAnnotation("test", "1", text);

        Constituent source = new Constituent("PER", 1.0, "MENTION", ta, 0, 1);
        Constituent target = new Constituent("PER", 1.0, "MENTION", ta, 2, 3);
        Relation relation = new Relation("test", source, target, 1.0);

        RelationFeatureExtractor extractor = new RelationFeatureExtractor();
        List<String> features = extractor.getLexicalFeaturePartF(relation);

        assertTrue(features.get(0).startsWith("HM1_"));
        assertTrue(features.get(1).startsWith("HM2_"));
        assertTrue(features.get(2).startsWith("HM12_"));
    } 
}