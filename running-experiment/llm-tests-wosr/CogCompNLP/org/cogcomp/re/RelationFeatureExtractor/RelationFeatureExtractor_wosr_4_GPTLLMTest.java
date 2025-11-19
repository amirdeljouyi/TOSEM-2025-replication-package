public class RelationFeatureExtractor_wosr_4_GPTLLMTest { 

 @Test
    public void testGetEntityHeadForConstituent_WithPredictedAttribute() {
        TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("John saw Mary.", "dummy");
        View dummyView = new View("TEST", "dummygenerator", ta, 1.0);
        Constituent g = new Constituent("PER", 1.0, "TEST", ta, 0, 1);
        g.addAttribute("IsPredicted", "true");
        dummyView.addConstituent(g);
        ta.addView("TEST", dummyView);

        Constituent result = RelationFeatureExtractor.getEntityHeadForConstituent(g, ta, "TEST");

        assertNotNull(result);
        assertEquals("PER", result.getLabel());
        assertEquals(0, result.getStartSpan());
        assertEquals(1, result.getEndSpan());
    }
@Test
    public void testGetEntityHeadForConstituent_WithEntityHeadStartSpan() {
        TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("John saw Mary.", "dummy");
        View dummyView = new View("TEST", "dummygenerator", ta, 1.0);
        Constituent g = new Constituent("PER", 1.0, "TEST", ta, 0, 1);
        g.addAttribute("EntityHeadStartSpan", "0");
        dummyView.addConstituent(g);
        ta.addView("TEST", dummyView);

        Constituent result = RelationFeatureExtractor.getEntityHeadForConstituent(g, ta, "TEST");

        assertNotNull(result);
        assertEquals("PER", result.getLabel());
        assertEquals(0, result.getStartSpan());
        assertEquals(1, result.getEndSpan());
    }
@Test
    public void testGetEntityHeadForConstituent_WithCharOffsets() {
        TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("Barack Obama met Angela Merkel.", "dummy");
        View dummyView = new View("TEST", "dummygenerator", ta, 1.0);
        Constituent g = new Constituent("PER", 1.0, "TEST", ta, 0, 2);
        g.addAttribute(ACEReader.EntityHeadStartCharOffset, "0");
        g.addAttribute(ACEReader.EntityHeadEndCharOffset, Integer.toString(6));  
        dummyView.addConstituent(g);
        ta.addView("TEST", dummyView);

        Constituent result = RelationFeatureExtractor.getEntityHeadForConstituent(g, ta, "TEST");

        assertNotNull(result);
        assertEquals(0, result.getStartSpan());
        assertEquals(1, result.getEndSpan());
    }
@Test
    public void testIsPossessive_True_PosTag() {
        TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("John's book is here.", "dummy");
        View view = new View("TEST", "dummy", ta, 1.0);

        Constituent c1 = new Constituent("PER", 1.0, "TEST", ta, 0, 1);
        Constituent c2 = new Constituent("OBJ", 1.0, "TEST", ta, 1, 2);
        view.addConstituent(c1);
        view.addConstituent(c2);
        ta.addView("TEST", view);

        View posView = new View(ViewNames.POS, "tester", ta, 1.0);
        posView.addConstituent(new Constituent("POS", 1.0, ViewNames.POS, ta, 1, 2));
        ta.addView(ViewNames.POS, posView);

        Relation r = new Relation("testRel", c1, c2, 1.0);

        assertTrue(RelationFeatureExtractor.isPossessive(r));
    }
@Test
    public void testIsPossessive_False_NoPossessive() {
        TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("John saw Mary.", "dummy");
        View view = new View("TEST", "dummy", ta, 1.0);

        Constituent c1 = new Constituent("PER", 1.0, "TEST", ta, 0, 1);
        Constituent c2 = new Constituent("PER", 1.0, "TEST", ta, 2, 3);
        view.addConstituent(c1);
        view.addConstituent(c2);
        ta.addView("TEST", view);

        View posView = new View(ViewNames.POS, "tester", ta, 1.0);
        posView.addConstituent(new Constituent("NNP", 1.0, ViewNames.POS, ta, 0, 1));
        posView.addConstituent(new Constituent("VBD", 1.0, ViewNames.POS, ta, 1, 2));
        posView.addConstituent(new Constituent("NNP", 1.0, ViewNames.POS, ta, 2, 3));
        ta.addView(ViewNames.POS, posView);

        Relation r = new Relation("testRel", c1, c2, 1.0);
        assertFalse(RelationFeatureExtractor.isPossessive(r));
    }
@Test
    public void testIsNoun_True_NNP() {
        assertTrue(RelationFeatureExtractor.isNoun("NNP"));
    }
@Test
    public void testIsNoun_True_RB() {
        assertTrue(RelationFeatureExtractor.isNoun("RB"));
    }
@Test
    public void testIsNoun_True_WP() {
        assertTrue(RelationFeatureExtractor.isNoun("WP"));
    }
@Test
    public void testIsNoun_False_VB() {
        assertFalse(RelationFeatureExtractor.isNoun("VB"));
    }
@Test
    public void testGetLexicalFeaturePartA_ReturnsTokens() {
        TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("John saw Mary.", "dummy");
        Constituent source = new Constituent("PER", 1.0, "MENTIONS", ta, 0, 2); 
        Constituent target = new Constituent("PER", 1.0, "MENTIONS", ta, 2, 3);
        Relation r = new Relation("dummy", source, target, 1.0);
        List<String> result = new RelationFeatureExtractor().getLexicalFeaturePartA(r);

        assertEquals(2, result.size());
        assertEquals("John", result.get(0));
        assertEquals("saw", result.get(1));
    }
@Test
    public void testGetLexicalFeaturePartB_ReturnsTokens() {
        TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("John saw Mary.", "dummy");
        Constituent source = new Constituent("PER", 1.0, "MENTIONS", ta, 0, 1);
        Constituent target = new Constituent("PER", 1.0, "MENTIONS", ta, 2, 3); 
        Relation r = new Relation("dummy", source, target, 1.0);
        List<String> result = new RelationFeatureExtractor().getLexicalFeaturePartB(r);

        assertEquals(1, result.size());
        assertEquals("Mary", result.get(0));
    }
@Test
    public void testGetLexicalFeaturePartC_SingleWordFound() {
        TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("John met with Mary.", "dummy");
        Constituent source = new Constituent("PER", 1.0, "MENTIONS", ta, 0, 1); 
        Constituent target = new Constituent("PER", 1.0, "MENTIONS", ta, 2, 3); 
        Relation r = new Relation("dummy", source, target, 1.0);

        List<String> result = new RelationFeatureExtractor().getLexicalFeaturePartC(r);
        assertEquals(1, result.size());
        assertTrue(result.get(0).startsWith("singleword_"));
    }
@Test
    public void testGetLexicalFeaturePartC_NoWordBetween() {
        TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("John saw Mary.", "dummy");
        Constituent c1 = new Constituent("PER", 1.0, "MENTIONS", ta, 0, 1);
        Constituent c2 = new Constituent("PER", 1.0, "MENTIONS", ta, 2, 3);
        Relation r = new Relation("dummy", c1, c2, 1.0);

        List<String> features = new RelationFeatureExtractor().getLexicalFeaturePartC(r);
        assertEquals(1, features.size());
        assertEquals("No_singleword", features.get(0));
    }
@Test
    public void testGetCorefTag_TRUE() {
        TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("Obama met the President.", "dummy");
        Constituent c1 = new Constituent("PER", 1.0, "MENTION", ta, 0, 1);
        Constituent c2 = new Constituent("PER", 1.0, "MENTION", ta, 3, 4);
        c1.addAttribute("EntityID", "E1");
        c2.addAttribute("EntityID", "E1");

        Relation r = new Relation("coref", c1, c2, 1.0);

        assertEquals("TRUE", new RelationFeatureExtractor().getCorefTag(r));
    }
@Test
    public void testGetCorefTag_FALSE() {
        TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("Obama met Merkel.", "dummy");
        Constituent c1 = new Constituent("PER", 1.0, "MENTION", ta, 0, 1);
        Constituent c2 = new Constituent("PER", 1.0, "MENTION", ta, 2, 3);
        c1.addAttribute("EntityID", "E1");
        c2.addAttribute("EntityID", "E2");

        Relation r = new Relation("coref", c1, c2, 1.0);

        assertEquals("FALSE", new RelationFeatureExtractor().getCorefTag(r));
    } 
}