public class NERAnnotator_wosr_5_GPTLLMTest { 

 @Test
    public void testConstructorWithViewNameDoesNotThrow() throws Exception {
        NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
        assertNotNull(annotator);
    }
@Test
    public void testConstructorWithResourcePath() throws Exception {
        Properties props = new Properties();
        props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
        ResourceManager rm = new ResourceManager(props);
        NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
        assertNotNull(annotator);
    }
@Test
    public void testConstructorWithConfigFilePath() throws Exception {
        Properties props = new Properties();
        props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "true");
        ResourceManager rm = new ResourceManager(props);
        NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
        assertNotNull(annotator);
    }
@Test
    public void testInitialize() throws Exception {
        Properties props = new Properties();
        props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
        ResourceManager rm = new ResourceManager(props);
        NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);

        
        annotator.doInitialize();

        Set<String> tagValues = annotator.getTagValues();
        assertNotNull(tagValues);
        assertFalse(tagValues.isEmpty());
    }
@Test
    public void testAddView() throws Exception {
        NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
        TextAnnotation ta = NERMockFactory.createSimpleTestAnnotation();
        annotator.doInitialize();
        annotator.addView(ta);
        assertTrue(ta.hasView(ViewNames.NER_CONLL));
        SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
        assertNotNull(view);
    }
@Test
    public void testAddViewWithEmptySentences() throws Exception {
        NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
        TextAnnotation ta = NERMockFactory.createEmptyTestAnnotation();
        annotator.doInitialize();
        annotator.addView(ta);
        assertTrue(ta.hasView(ViewNames.NER_CONLL));
        SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
        assertEquals(0, view.getNumberOfConstituents());
    }
@Test
    public void testGetTagValuesReturnsNonEmptySet() throws Exception {
        NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
        annotator.doInitialize();
        Set<String> tagValues = annotator.getTagValues();
        assertNotNull(tagValues);
        assertFalse(tagValues.isEmpty());
        assertTrue(tagValues.contains("O"));
    }
@Test
    public void testGetL1FeatureWeightsReturnsNonEmptyMap() throws Exception {
        NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
        annotator.doInitialize();
        HashMap<Feature, double[]> l1Weights = annotator.getL1FeatureWeights();
        assertNotNull(l1Weights);
        assertFalse(l1Weights.isEmpty());

        for (Map.Entry<Feature, double[]> entry : l1Weights.entrySet()) {
            assertNotNull(entry.getKey());
            assertNotNull(entry.getValue());
        }
    }
@Test
    public void testGetL2FeatureWeightsReturnsNonEmptyMap() throws Exception {
        NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
        annotator.doInitialize();
        HashMap<Feature, double[]> l2Weights = annotator.getL2FeatureWeights();
        assertNotNull(l2Weights);
        assertFalse(l2Weights.isEmpty());

        for (Map.Entry<Feature, double[]> entry : l2Weights.entrySet()) {
            assertNotNull(entry.getKey());
            assertNotNull(entry.getValue());
        }
    }
@Test
    public void testGetTagValuesLazilyInitializesIfNotInitialized() throws Exception {
        Properties props = new Properties();
        props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "true");
        ResourceManager rm = new ResourceManager(props);
        NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);

        Set<String> tags = annotator.getTagValues();
        assertNotNull(tags);
        assertFalse(tags.isEmpty());
    }
@Test
    public void testAddingViewMultipleTimes() throws Exception {
        NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
        TextAnnotation ta = NERMockFactory.createSimpleTestAnnotation();
        annotator.doInitialize();
        annotator.addView(ta);
        annotator.addView(ta);
        assertTrue(ta.hasView(ViewNames.NER_CONLL));
        SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
        assertNotNull(view);
    }
@Test
    public void testAddViewWithSingleToken() throws Exception {
        NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
        TextAnnotation ta = NERMockFactory.createSingleTokenAnnotation();
        annotator.doInitialize();
        annotator.addView(ta);
        assertTrue(ta.hasView(ViewNames.NER_CONLL));
    }
@Test
    public void testAddViewHandlesOffByOneTokens() throws Exception {
        NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
        TextAnnotation ta = NERMockFactory.createOffByOneAnnotation();
        annotator.doInitialize();
        annotator.addView(ta);
        assertTrue(ta.hasView(ViewNames.NER_CONLL));
    }
@Test
    public void testNERAnnotatorHandlesInvalidConfigGracefully() {
        try {
            String badConfig = "invalid_config.properties";
            NERAnnotator annotator = new NERAnnotator(badConfig, ViewNames.NER_CONLL);
            assertNotNull(annotator);
        } catch (IOException e) {
            assertTrue(e.getMessage().contains("invalid"));
        }
    }
@Test
    public void testAddViewWithMalformedSentence() throws Exception {
        NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
        TextAnnotation ta = NERMockFactory.createAnnotationWithMalformedSentence();
        annotator.doInitialize();
        annotator.addView(ta);
        assertTrue(ta.hasView(ViewNames.NER_CONLL));
    } 
}