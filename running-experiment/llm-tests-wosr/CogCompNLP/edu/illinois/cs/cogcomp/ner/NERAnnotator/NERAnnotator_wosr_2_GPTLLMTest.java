public class NERAnnotator_wosr_2_GPTLLMTest { 

 @Test
    public void testConstructorWithViewName() throws IOException {
        NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
        assertNotNull(annotator);
        assertEquals(ViewNames.NER_CONLL, annotator.getViewName());
    }
@Test
    public void testConstructorWithEmptyConfigAndViewName() throws IOException {
        ResourceManager rm = new ResourceManager(new Properties());
        NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_ONTONOTES);
        assertNotNull(annotator);
        assertEquals(ViewNames.NER_ONTONOTES, annotator.getViewName());
    }
@Test
    public void testAddViewGeneratesNERViewForSimpleText() throws IOException {
        NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
        TextAnnotation ta = new TextAnnotation("testCorpus", "testId", new String[]{"Barack", "Obama", "was", "born", "in", "Hawaii"});
        ta.addSentence(0, 2);
        ta.addSentence(2, 6);
        annotator.getView(ta);
        assertTrue(ta.hasView(ViewNames.NER_CONLL));
        assertTrue(ta.getView(ViewNames.NER_CONLL) instanceof SpanLabelView);
    }
@Test
    public void testAddViewEmptyInput() throws IOException {
        NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
        TextAnnotation ta = new TextAnnotation("corpus", "id", new String[]{});
        annotator.getView(ta);
        assertTrue(ta.hasView(ViewNames.NER_CONLL));
        SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
        assertEquals(0, view.getNumberOfConstituents());
    }
@Test
    public void testAddViewNoNERLabels() throws IOException {
        NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
        TextAnnotation ta = new TextAnnotation("source", "id", new String[]{"this", "is", "a", "test"});
        ta.addSentence(0, 4);
        annotator.getView(ta);
        assertTrue(ta.hasView(ViewNames.NER_CONLL));
        SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
        assertTrue(view.getNumberOfConstituents() >= 0);
    }
@Test
    public void testAddViewHandlesSingleToken() throws IOException {
        NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
        TextAnnotation ta = new TextAnnotation("corpus", "id", new String[]{"Illinois"});
        ta.addSentence(0, 1);
        annotator.getView(ta);
        assertTrue(ta.hasView(ViewNames.NER_CONLL));
        SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
        assertTrue(view.getNumberOfConstituents() >= 0);
    }
@Test
    public void testGetTagValuesReturnsNonEmptySet() throws IOException {
        NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
        Set<String> tags = annotator.getTagValues();
        assertNotNull(tags);
        assertFalse(tags.isEmpty());
        for (String tag : tags) {
            assertNotNull(tag);
            assertTrue(tag.length() > 0);
        }
    }
@Test
    public void testGetL1FeatureWeightsReturnsNonEmptyMap() throws IOException {
        NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
        HashMap<Feature, double[]> weights = annotator.getL1FeatureWeights();
        assertNotNull(weights);
        assertFalse(weights.isEmpty());
        for (Map.Entry<Feature, double[]> entry : weights.entrySet()) {
            Feature f = entry.getKey();
            double[] w = entry.getValue();
            assertNotNull(f);
            assertNotNull(w);
            assertTrue(w.length > 0);
        }
    }
@Test
    public void testGetL2FeatureWeightsReturnsNonEmptyMap() throws IOException {
        NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
        HashMap<Feature, double[]> weights = annotator.getL2FeatureWeights();
        assertNotNull(weights);
        assertFalse(weights.isEmpty());
        for (Map.Entry<Feature, double[]> entry : weights.entrySet()) {
            Feature f = entry.getKey();
            double[] w = entry.getValue();
            assertNotNull(f);
            assertNotNull(w);
            assertTrue(w.length > 0);
        }
    }
@Test
    public void testMultipleAddViewCalls() throws IOException {
        NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
        TextAnnotation ta = new TextAnnotation("corpus", "id", new String[]{"Barack", "Obama", "visited", "France"});
        ta.addSentence(0, 4);
        annotator.getView(ta);
        annotator.getView(ta);
        assertTrue(ta.hasView(ViewNames.NER_CONLL));
        SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
        assertTrue(view.getNumberOfConstituents() >= 0);
    }
@Test
    public void testTagValuesWithCustomResourceManager() throws IOException {
        Properties props = new Properties();
        props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, Configurator.FALSE);
        ResourceManager rm = new ResourceManager(props);
        NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_ONTONOTES);
        Set<String> tags = annotator.getTagValues();
        assertNotNull(tags);
        assertFalse(tags.isEmpty());
    }
@Test
    public void testAddViewWithMultipleSentences() throws IOException {
        NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
        String[] tokens = new String[]{"Barack", "Obama", "was", "born", ".", "He", "was", "president"};
        TextAnnotation ta = new TextAnnotation("src", "id", tokens);
        ta.addSentence(0, 5);
        ta.addSentence(5, 8);
        annotator.getView(ta);
        assertTrue(ta.hasView(ViewNames.NER_CONLL));
        SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
        assertTrue(view.getNumberOfConstituents() >= 0);
    }
@Test
    public void testAddViewWithMalformedTokenInput() throws IOException {
        NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
        String[] tokens = new String[]{"", "Obama", "was", "born"};
        TextAnnotation ta = new TextAnnotation("src", "id", tokens);
        ta.addSentence(0, 4);
        annotator.getView(ta);
        assertTrue(ta.hasView(ViewNames.NER_CONLL));
    }
@Test
    public void testGetL1AndL2FeatureWeightConsistency() throws IOException {
        NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
        HashMap<Feature, double[]> l1 = annotator.getL1FeatureWeights();
        HashMap<Feature, double[]> l2 = annotator.getL2FeatureWeights();
        assertNotNull(l1);
        assertNotNull(l2);
        assertFalse(l1.equals(l2)); 
    }
@Test
    public void testNERAnnotatorWithOntonotes() throws IOException {
        NERAnnotator annotator = new NERAnnotator(ViewNames.NER_ONTONOTES);
        TextAnnotation ta = new TextAnnotation("src", "id", new String[]{"Barack", "Obama", "is", "good"});
        ta.addSentence(0, 4);
        annotator.getView(ta);
        assertTrue(ta.hasView(ViewNames.NER_ONTONOTES));
    } 
}