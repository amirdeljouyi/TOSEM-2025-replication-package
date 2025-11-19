public class NERAnnotator_wosr_1_GPTLLMTest { 

 @Test
    public void testConstructorWithViewName() throws IOException {
        String view = ViewNames.NER_CONLL;
        NERAnnotator annotator = new NERAnnotator(view);
        assertNotNull(annotator);
        assertEquals(view, annotator.getViewName());
    }
@Test
    public void testConstructorWithResourceManager() throws IOException {
        Properties props = new Properties();
        props.setProperty("configKey", "configValue");
        ResourceManager rm = new ResourceManager(props);
        NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);
        assertNotNull(annotator);
        assertEquals(ViewNames.NER_CONLL, annotator.getViewName());
    }
@Test
    public void testAddViewCreatesNERView() throws IOException {
        NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
        String[] tokens = new String[]{"Barack", "Obama", "was", "born", "in", "Hawaii", "."};
        int[] sentenceStartOffsets = new int[]{0};
        int[] sentenceEndOffsets = new int[]{7};
        TextAnnotation ta = TextAnnotationUtilities.createTextAnnotation("test", "test", tokens, sentenceStartOffsets, sentenceEndOffsets);
        annotator.getView(ta);
        assertTrue(ta.hasView(ViewNames.NER_CONLL));
    }
@Test
    public void testAddViewWithEmptySentence() throws IOException {
        NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
        String[] tokens = new String[]{" "};
        int[] starts = new int[]{0};
        int[] ends = new int[]{1};
        TextAnnotation ta = TextAnnotationUtilities.createTextAnnotation("id", "src", tokens, starts, ends);
        annotator.getView(ta);
        assertTrue(ta.hasView(ViewNames.NER_CONLL));
    }
@Test
    public void testAddViewWithMultipleSentences() throws IOException {
        NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
        String[] tokens = new String[]{"Barack", "Obama", ".", "He", "was", "president", "."};
        int[] startOffsets = new int[]{0, 3};
        int[] endOffsets = new int[]{3, 7};
        TextAnnotation ta = TextAnnotationUtilities.createTextAnnotation("multi", "test", tokens, startOffsets, endOffsets);
        annotator.getView(ta);
        SpanLabelView view = (SpanLabelView) ta.getView(ViewNames.NER_CONLL);
        assertNotNull(view);
    }
@Test
    public void testGetTagValuesNotNull() throws IOException {
        NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
        Set<String> tagValues = annotator.getTagValues();
        assertNotNull(tagValues);
        assertFalse(tagValues.isEmpty());
    }
@Test
    public void testL1FeatureWeightsAreDefined() throws IOException {
        NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
        HashMap<Feature, double[]> weights = annotator.getL1FeatureWeights();
        assertNotNull(weights);
        assertFalse(weights.isEmpty());
    }
@Test
    public void testL2FeatureWeightsAreDefined() throws IOException {
        NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
        HashMap<Feature, double[]> weights = annotator.getL2FeatureWeights();
        assertNotNull(weights);
        assertFalse(weights.isEmpty());
    }
@Test
    public void testL1AndL2WeightsHaveSameFeaturesIfModelSame() throws IOException {
        NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
        HashMap<Feature, double[]> l1Weights = annotator.getL1FeatureWeights();
        HashMap<Feature, double[]> l2Weights = annotator.getL2FeatureWeights();
        assertFalse(l1Weights.keySet().equals(l2Weights.keySet()));
    }
@Test
    public void testL1WeightsContainValidWeightVectors() throws IOException {
        NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
        HashMap<Feature, double[]> weights = annotator.getL1FeatureWeights();
        Feature exampleKey = weights.keySet().iterator().next();
        double[] vector = weights.get(exampleKey);
        assertNotNull(vector);
        assertTrue(vector.length > 0);
    }
@Test
    public void testTagValuesContainCommonLabels() throws IOException {
        NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
        Set<String> tags = annotator.getTagValues();
        assertTrue(tags.contains("O"));
    }
@Test
    public void testAddViewHandlesNoEntitiesGracefully() throws IOException {
        NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
        String[] tokens = new String[]{"the", "car", "is", "blue"};
        int[] starts = new int[]{0};
        int[] ends = new int[]{4};
        TextAnnotation ta = TextAnnotationUtilities.createTextAnnotation("plain", "text", tokens, starts, ends);
        annotator.getView(ta);
        assertTrue(ta.hasView(ViewNames.NER_CONLL));
    }
@Test
    public void testMultipleNERAnnotatorsIndependent() throws IOException {
        NERAnnotator conllAnnotator = new NERAnnotator(ViewNames.NER_CONLL);
        NERAnnotator ontoAnnotator = new NERAnnotator(ViewNames.NER_ONTONOTES);
        assertNotSame(conllAnnotator.getViewName(), ontoAnnotator.getViewName());
    } 
}