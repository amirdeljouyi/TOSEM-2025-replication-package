public class RelationFeatureExtractor_wosr_1_GPTLLMTest { 

 @Test
    public void testGetEntityHeadForConstituent_Predicted() throws Exception {
        String text = "Barack Obama visited France.";
        TextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new StatefulTokenizer());
        TextAnnotation ta = builder.createTextAnnotation("test", "1", text);

        Constituent extent = new Constituent("PERSON", 1.0, "testView", ta, 0, 2);
        extent.addAttribute("IsPredicted", "true");

        Constituent result = RelationFeatureExtractor.getEntityHeadForConstituent(extent, ta, "testView");
        assertEquals(extent, result);
    }
@Test
    public void testGetEntityHeadForConstituent_MentionAnnotator() throws Exception {
        String text = "John, the president, met the ambassador.";
        TextAnnotationBuilder builder = new TokenizerTextAnnotationBuilder(new StatefulTokenizer());
        TextAnnotation ta = builder.createTextAnnotation("test", "2", text);

        Constituent extent = new Constituent("ORG", 1.0, "mentionView", ta, 1, 3);
        extent.addAttribute("EntityHeadStartSpan", "1");

        Constituent head = RelationFeatureExtractor.getEntityHeadForConstituent(extent, ta, "mentionView");
        assertNotNull(head);
        assertTrue(head.getStartSpan() >= extent.getStartSpan());
        assertEquals("ORG", head.getLabel());
    }
@Test
    public void testGetEntityHeadForConstituent_FromCharOffsets() throws Exception {
        String text = "Apple Inc is based in Cupertino.";
        TextAnnotation ta = BasicTextAnnotationBuilder.createFromTokenizedString(Arrays.asList(
                Arrays.asList("Apple", "Inc", "is", "based", "in", "Cupertino", ".")
        ));

        Constituent extent = new Constituent("ORG", 1.0, "mention", ta, 0, 2);
        extent.addAttribute("EntityHeadStartCharOffset", "0");
        extent.addAttribute("EntityHeadEndCharOffset", "10");

        Constituent head = RelationFeatureExtractor.getEntityHeadForConstituent(extent, ta, "mention");
        assertNotNull(head);
        assertEquals(0, head.getStartSpan());
        assertEquals("ORG", head.getLabel());
    }
@Test
    public void testGetEntityHeadForConstituent_InvalidSpan() throws Exception {
        String text = "Illinois is a state.";
        TextAnnotation ta = BasicTextAnnotationBuilder.createFromTokenizedString(Arrays.asList(
                Arrays.asList("Illinois", "is", "a", "state", ".")
        ));

        Constituent extent = new Constituent("GPE", 1.0, "mention", ta, 0, 1);
        extent.addAttribute("EntityHeadStartCharOffset", "-1");
        extent.addAttribute("EntityHeadEndCharOffset", "1");

        Constituent head = RelationFeatureExtractor.getEntityHeadForConstituent(extent, ta, "mention");
        assertNull(head);
    }
@Test
    public void testIsNounPositiveCases() {
        assertTrue(RelationFeatureExtractor.isNoun("NN"));
        assertTrue(RelationFeatureExtractor.isNoun("NNP"));
        assertTrue(RelationFeatureExtractor.isNoun("RB"));
        assertTrue(RelationFeatureExtractor.isNoun("WP"));
        assertTrue(RelationFeatureExtractor.isNoun("WP$"));
    }
@Test
    public void testIsNounNegativeCases() {
        assertFalse(RelationFeatureExtractor.isNoun("VB"));
        assertFalse(RelationFeatureExtractor.isNoun("JJ"));
        assertFalse(RelationFeatureExtractor.isNoun("IN"));
    }
@Test
    public void testGetLexicalFeaturePartA() throws Exception {
        String text = "Obama met Macron.";
        TextAnnotation ta = BasicTextAnnotationBuilder.createFromTokenizedString(
            Collections.singletonList(Arrays.asList("Obama", "met", "Macron", "."))
        );
        Constituent source = new Constituent("PER", 1.0, "test", ta, 0, 1);
        Constituent target = new Constituent("PER", 1.0, "test", ta, 2, 3);
        Relation r = new Relation("testRel", source, target, 1.0);

        RelationFeatureExtractor extractor = new RelationFeatureExtractor();
        List<String> tokens = extractor.getLexicalFeaturePartA(r);
        assertEquals(1, tokens.size());
        assertEquals("Obama", tokens.get(0));
    }
@Test
    public void testGetLexicalFeaturePartC_SingleWordBetween() throws Exception {
        String text = "Obama and Macron spoke.";
        TextAnnotation ta = BasicTextAnnotationBuilder.createFromTokenizedString(
            Collections.singletonList(Arrays.asList("Obama", "and", "Macron", "spoke", "."))
        );
        Constituent source = new Constituent("PER", 1.0, "view", ta, 0, 1);
        Constituent target = new Constituent("PER", 1.0, "view", ta, 2, 3);
        Relation r = new Relation("test", source, target, 1.0);

        RelationFeatureExtractor extractor = new RelationFeatureExtractor();
        List<String> feats = extractor.getLexicalFeaturePartC(r);
        assertEquals(1, feats.size());
        assertTrue(feats.get(0).contains("singleword"));
    }
@Test
    public void testGetLexicalFeaturePartC_NoSingleWord() throws Exception {
        String text = "Obama Macron.";
        TextAnnotation ta = BasicTextAnnotationBuilder.createFromTokenizedString(
            Collections.singletonList(Arrays.asList("Obama", "Macron", "."))
        );
        Constituent source = new Constituent("PER", 1.0, "view", ta, 0, 1);
        Constituent target = new Constituent("PER", 1.0, "view", ta, 1, 2);
        Relation r = new Relation("testRel", source, target, 1.0);

        RelationFeatureExtractor extractor = new RelationFeatureExtractor();
        List<String> feats = extractor.getLexicalFeaturePartC(r);
        assertEquals(1, feats.size());
        assertEquals("No_singleword", feats.get(0));
    }
@Test
    public void testGetCorefTagTrue() throws Exception {
        String text = "Obama met Obama.";
        TextAnnotation ta = BasicTextAnnotationBuilder.createFromTokenizedString(Arrays.asList(
            Arrays.asList("Obama", "met", "Obama", ".")
        ));
        Constituent source = new Constituent("PER", 1.0, "view", ta, 0, 1);
        Constituent target = new Constituent("PER", 1.0, "view", ta, 2, 3);
        source.addAttribute("EntityID", "E1");
        target.addAttribute("EntityID", "E1");

        Relation r = new Relation("rel", source, target, 1.0);

        RelationFeatureExtractor extractor = new RelationFeatureExtractor();
        String tag = extractor.getCorefTag(r);
        assertEquals("TRUE", tag);
    }
@Test
    public void testGetCorefTagFalse() throws Exception {
        String text = "Obama met Macron.";
        TextAnnotation ta = BasicTextAnnotationBuilder.createFromTokenizedString(Arrays.asList(
            Arrays.asList("Obama", "met", "Macron", ".")
        ));
        Constituent source = new Constituent("PER", 1.0, "view", ta, 0, 1);
        Constituent target = new Constituent("PER", 1.0, "view", ta, 2, 3);
        source.addAttribute("EntityID", "E1");
        target.addAttribute("EntityID", "E2");

        Relation r = new Relation("rel", source, target, 1.0);

        RelationFeatureExtractor extractor = new RelationFeatureExtractor();
        String tag = extractor.getCorefTag(r);
        assertEquals("FALSE", tag);
    }
@Test
    public void testPatternRecognition_SameHead() throws Exception {
        String text = "Obama met Obama.";
        TextAnnotation ta = BasicTextAnnotationBuilder.createFromTokenizedString(Arrays.asList(
            Arrays.asList("Obama", "met", "Obama", ".")
        ));
        Constituent c1 = new Constituent("PER", 1.0, "view", ta, 0, 1);
        Constituent c2 = new Constituent("PER", 1.0, "view", ta, 2, 3);

        List<String> patterns = RelationFeatureExtractor.patternRecognition(c1, c2);
        assertNotNull(patterns);
        assertFalse(patterns.isEmpty());
    }
@Test
    public void testOnlyNounBetweenTrue() throws Exception {
        String text = "John apple pie.";
        TextAnnotation ta = BasicTextAnnotationBuilder.createFromTokenizedString(Arrays.asList(
            Arrays.asList("John", "apple", "pie", ".")
        ));
        Constituent c1 = new Constituent("ENTITY", 1.0, "view", ta, 0, 1);
        Constituent c2 = new Constituent("ENTITY", 1.0, "view", ta, 2, 3);

        View posView = new View(ViewNames.POS, "test", ta, 1.0);
        posView.addConstituent(new Constituent("NN", ta, 1, 2));
        ta.addView(ViewNames.POS, posView);

        assertTrue(RelationFeatureExtractor.onlyNounBetween(c1, c2));
    }
@Test
    public void testOnlyNounBetweenFalse() throws Exception {
        String text = "John quickly pie.";
        TextAnnotation ta = BasicTextAnnotationBuilder.createFromTokenizedString(Arrays.asList(
            Arrays.asList("John", "quickly", "pie", ".")
        ));
        Constituent c1 = new Constituent("ENTITY", 1.0, "view", ta, 0, 1);
        Constituent c2 = new Constituent("ENTITY", 1.0, "view", ta, 2, 3);

        View posView = new View(ViewNames.POS, "test", ta, 1.0);
        posView.addConstituent(new Constituent("RB", ta, 1, 2)); 
        ta.addView(ViewNames.POS, posView);

        assertFalse(RelationFeatureExtractor.onlyNounBetween(c1, c2));
    } 
}