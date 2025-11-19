public class NERAnnotator_wosr_3_GPTLLMTest { 

 @Test
    public void testDefaultConstructorWithConllViewDoesNotThrow() throws IOException {
        NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
        assertNotNull(annotator);
        assertEquals(ViewNames.NER_CONLL, annotator.getViewName());
    }
@Test
    public void testConstructorWithResourceManagerAndCustomView() {
        Properties props = new Properties();
        props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
        ResourceManager rm = new ResourceManager(props);
        NERAnnotator annotator = new NERAnnotator(rm, "TestNERView");
        assertNotNull(annotator);
        assertEquals("TestNERView", annotator.getViewName());
    }
@Test
    public void testAddViewCreatesNERViewInTextAnnotation() throws IOException {
        NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
        TextAnnotation ta = TextAnnotationUtilities.createSimpleTextAnnotation("The CEO of Apple lives in California.");
        annotator.addView(ta);
        assertTrue(ta.hasView(ViewNames.NER_CONLL));
        assertTrue(ta.getView(ViewNames.NER_CONLL) instanceof SpanLabelView);
    }
@Test
    public void testAddViewHandlesEmptySentenceTokens() throws IOException {
        NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
        TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationWithEmptyTokensInSentence();
        annotator.addView(ta);
        assertTrue(ta.hasView(ViewNames.NER_CONLL));
        SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
        assertNotNull(view);
    }
@Test
    public void testAddViewWithNoSentencesDoesNotFail() throws IOException {
        NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
        TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationWithNoSentences("No sentence.");
        annotator.addView(ta);
        assertTrue(ta.hasView(ViewNames.NER_CONLL));
    }
@Test
    public void testGetTagValuesReturnsNonEmptySet() throws IOException {
        Properties props = new Properties();
        props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
        ResourceManager rm = new ResourceManager(props);
        NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
        Set<String> tagValues = annotator.getTagValues();
        assertNotNull(tagValues);
        assertFalse(tagValues.isEmpty());
    }
@Test
    public void testGetL1FeatureWeightsReturnsNonEmptyMap() throws IOException {
        NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
        HashMap<Feature, double[]> weights = annotator.getL1FeatureWeights();
        assertNotNull(weights);
        assertFalse(weights.isEmpty());
        for (Map.Entry<Feature, double[]> entry : weights.entrySet()) {
            assertNotNull(entry.getKey());
            assertNotNull(entry.getValue());
        }
    }
@Test
    public void testGetL2FeatureWeightsReturnsNonEmptyMap() throws IOException {
        NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
        HashMap<Feature, double[]> weights = annotator.getL2FeatureWeights();
        assertNotNull(weights);
        assertFalse(weights.isEmpty());
    }
@Test
    public void testAddViewDoesNotDuplicateView() throws IOException {
        NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
        TextAnnotation ta = TextAnnotationUtilities.createSimpleTextAnnotation("Barack Obama visited Berlin.");
        annotator.addView(ta);
        SpanLabelView view1 = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
        annotator.addView(ta);
        SpanLabelView view2 = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
        assertSame(view1, view2);
    }
@Test
    public void testAddViewWithMultipleEntitiesAnnotation() throws IOException {
        NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
        String text = "Barack Obama was born in Hawaii. Google Inc. is based in Mountain View.";
        TextAnnotation ta = TextAnnotationUtilities.createSimpleTextAnnotation(text);
        annotator.addView(ta);
        SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
        assertTrue(view.getNumberOfConstituents() > 0);
    }
@Test
    public void testAddViewWithSingleToken() throws IOException {
        NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
        TextAnnotation ta = TextAnnotationUtilities.createSimpleTextAnnotation("Obama");
        annotator.addView(ta);
        SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
        assertNotNull(view);
    }
@Test
    public void testInitializationWithOntonotesModel() {
        Properties props = new Properties();
        props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
        ResourceManager rm = new ResourceManager(props);
        NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_ONTONOTES);
        annotator.initialize(rm);
        assertNotNull(annotator.getTagValues());
    }
@Test
    public void testMainMethodHandlesInvalidArgsGracefully() {
        String[] args = new String[0];
        try {
            NERAnnotator.main(args);
        } catch (Exception e) {
            fail("Main method threw unexpected exception.");
        }
    }
@Test
    public void testGetL1L2WeightsConsistency() throws IOException {
        NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
        HashMap<Feature, double[]> l1 = annotator.getL1FeatureWeights();
        HashMap<Feature, double[]> l2 = annotator.getL2FeatureWeights();
        assertNotSame(l1.keySet(), l2.keySet());
        assertNotEquals(l1.size(), 0);
        assertNotEquals(l2.size(), 0);
    } 
}