public class NERAnnotator_wosr_4_GPTLLMTest { 

 @Test
    public void testConstructorWithOnlyViewName_shouldLazilyInitialize() throws IOException {
        String viewName = ViewNames.NER_CONLL;
        NERAnnotator annotator = new NERAnnotator(viewName);
        assertEquals(viewName, annotator.getViewName());
        
        Set<String> tags = annotator.getTagValues();
        assertNotNull(tags);
        assertTrue(tags.size() > 0);
    }
@Test
    public void testConstructorWithCustomProperties_shouldRespectConfig() throws IOException {
        Properties props = new Properties();
        props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
        ResourceManager rm = new ResourceManager(props);
        String viewName = ViewNames.NER_CONLL;

        NERAnnotator annotator = new NERAnnotator(rm, viewName);

        assertEquals(viewName, annotator.getViewName());
        assertNotNull(annotator.getTagValues());
    }
@Test
    public void testInitializationWithNEROntonotes_shouldUseNerOntonotesConfigurator() {
        Properties props = new Properties();
        props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
        ResourceManager rm = new ResourceManager(props);
        String viewName = ViewNames.NER_ONTONOTES;

        NERAnnotator annotator = new NERAnnotator(rm, viewName);

        try {
            annotator.initialize(rm);
            assertTrue(true); 
        } catch (Exception e) {
            fail("Initialization failed: " + e.getMessage());
        }
    }
@Test
    public void testInitializationWithNERConll_shouldUseNerBaseConfigurator() {
        Properties props = new Properties();
        props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");
        ResourceManager rm = new ResourceManager(props);
        String viewName = ViewNames.NER_CONLL;

        NERAnnotator annotator = new NERAnnotator(rm, viewName);

        try {
            annotator.initialize(rm);
            assertTrue(true);
        } catch (Exception e) {
            fail("Initialization failed: " + e.getMessage());
        }
    }
@Test
    public void testAddViewShouldUpdateTextAnnotationWithNERView() throws IOException {
        TextAnnotation ta = mock(TextAnnotation.class);
        when(ta.getView(ViewNames.NER_CONLL)).thenThrow(new IllegalArgumentException()); 

        String[] tokens = new String[] { "Barack", "Obama", "visited", "Berlin" };
        when(ta.getTokens()).thenReturn(tokens);
        when(ta.getNumberOfSentences()).thenReturn(1);

        Sentence sentence = mock(Sentence.class);
        when(ta.getSentence(0)).thenReturn(sentence);
        when(sentence.getTokens()).thenReturn(tokens);

        String viewName = ViewNames.NER_CONLL;
        NERAnnotator annotator = new NERAnnotator(viewName);
        annotator.getTagValues(); 

        annotator.addView(ta);

        verify(ta, times(1)).addView(eq(viewName), any());
    }
@Test
    public void testGetTagValuesShouldReturnNonEmptySet() throws IOException {
        NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
        Set<String> tags = annotator.getTagValues();
        assertNotNull(tags);
        assertFalse(tags.isEmpty());
    }
@Test(expected = IllegalArgumentException.class)
    public void testAddViewWithEmptySentenceShouldErrorGracefully() throws IOException {
        TextAnnotation ta = mock(TextAnnotation.class);
        when(ta.getTokens()).thenReturn(new String[] {});
        when(ta.getNumberOfSentences()).thenReturn(0);
        when(ta.getView(anyString())).thenThrow(new IllegalArgumentException());

        String viewName = ViewNames.NER_CONLL;
        NERAnnotator annotator = new NERAnnotator(viewName);
        annotator.getTagValues(); 
        annotator.addView(ta);
    }
@Test
    public void testGetL1FeatureWeightsReturnsValidMapWithEntries() throws IOException {
        NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
        annotator.getTagValues(); 

        HashMap<Feature, double[]> featureWeights = annotator.getL1FeatureWeights();
        assertNotNull(featureWeights);
        assertFalse(featureWeights.isEmpty());

        for (Feature f : featureWeights.keySet()) {
            double[] weights = featureWeights.get(f);
            assertNotNull(weights);
        }
    }
@Test
    public void testGetL2FeatureWeightsReturnsValidMapWithEntries() throws IOException {
        NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
        annotator.getTagValues(); 

        HashMap<Feature, double[]> featureWeights = annotator.getL2FeatureWeights();
        assertNotNull(featureWeights);
        assertFalse(featureWeights.isEmpty());

        for (Feature f : featureWeights.keySet()) {
            double[] weights = featureWeights.get(f);
            assertNotNull(weights);
        }
    }
@Test
    public void testMainProgramUsageMessageOnInvalidArgs() {
        String[] args = new String[] {};
        try {
            NERAnnotator.main(args);
            assertTrue(true); 
        } catch (Exception e) {
            fail("Main should not throw on invalid input.");
        }
    }
@Test
    public void testMainProgramInvalidConfigFileShouldHandleIOException() {
        String[] args = new String[] { "invalid/path/to/config.properties", "L1" };
        try {
            NERAnnotator.main(args);
            assertTrue(true); 
        } catch (Exception e) {
            fail("Should handle invalid config gracefully.");
        }
    }
@Test
    public void testTagSetContainsOBLabels() throws IOException {
        NERAnnotator annotator = new NERAnnotator(ViewNames.NER_CONLL);
        Set<String> tags = annotator.getTagValues();

        boolean containsO = false;
        boolean containsB = false;

        for (String tag : tags) {
            if (tag.equals("O")) containsO = true;
            if (tag.startsWith("B-")) containsB = true;
        }

        assertTrue("Expected 'O' tag in tag set", containsO);
        assertTrue("Expected 'B-' prefixed tag in tag set", containsB);
    }
@Test
    public void testConfigurationOverrideDisablesLazyInitialization() throws IOException {
        Properties props = new Properties();
        props.setProperty(AnnotatorConfigurator.IS_LAZILY_INITIALIZED.key, "false");

        ResourceManager rm = new ResourceManager(props);
        NERAnnotator annotator = new NERAnnotator(rm, ViewNames.NER_CONLL);

        assertNotNull(annotator.getTagValues());
    }
@Test
    public void testConstructorWithNonDefaultConfigValuesPath() throws IOException {
        Properties props = new NerBaseConfigurator().getDefaultConfig();
        FileTempConfigHelper helper = new FileTempConfigHelper(props);
        String configPath = helper.createTempConfigFile();

        NERAnnotator annotator = new NERAnnotator(configPath, ViewNames.NER_CONLL);

        Set<String> tags = annotator.getTagValues();
        assertNotNull(tags);
        assertFalse(tags.isEmpty());
    } 
}