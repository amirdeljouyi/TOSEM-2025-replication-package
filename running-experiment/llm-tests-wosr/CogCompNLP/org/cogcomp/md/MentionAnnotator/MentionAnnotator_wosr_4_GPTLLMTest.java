public class MentionAnnotator_wosr_4_GPTLLMTest { 

 @Test
    public void testDefaultConstructorInitializesProperly() {
        MentionAnnotator annotator = new MentionAnnotator();
        assertNotNull(annotator);
    }
@Test
    public void testConstructorWithModeInitializesProperly() {
        MentionAnnotator annotator = new MentionAnnotator("ACE_TYPE");
        assertNotNull(annotator);
    }
@Test
    public void testConstructorWithModelPathsInitializesProperly() {
        MentionAnnotator annotator = new MentionAnnotator("path/to/nam", "path/to/nom", "path/to/pro", "path/to/extent", "MODE");
        assertNotNull(annotator);
    }
@Test
    public void testInitializeACETypeMode() {
        MentionAnnotator annotator = new MentionAnnotator(false, "ACE_TYPE");
        ResourceManager rm = new ResourceConfigurator().getDefaultConfig();
        annotator.initialize(rm);
        assertNotNull(annotator);
    }
@Test
    public void testInitializeERENonTypeMode() {
        MentionAnnotator annotator = new MentionAnnotator(false, "ERE_NONTYPE");
        ResourceManager rm = new ResourceConfigurator().getDefaultConfig();
        annotator.initialize(rm);
        assertNotNull(annotator);
    }
@Test(expected = AnnotatorException.class)
    public void testAddViewThrowsExceptionWithoutPOS() throws AnnotatorException {
        MentionAnnotator annotator = new MentionAnnotator();
        TextAnnotation ta = new TextAnnotation("testCorpus", "testTextId", new String[][]{{"John", "loves", "Mary"}});
        annotator.addView(ta);
    }
@Test
    public void testAddViewAddsMentionViewSuccessfully() throws Exception {
        MentionAnnotator annotator = new MentionAnnotator();
        ResourceManager rm = new ResourceConfigurator().getDefaultConfig();
        annotator.initialize(rm);

        TextAnnotation ta = new TextAnnotation("testCorpus", "testTextId", new String[][]{{"John", "visited", "Paris"}});
        View tokenView = new TokenLabelView(ViewNames.TOKENS, "testGenerator", ta, 1.0);
        for (int i = 0; i < ta.size(); i++) {
            tokenView.addConstituent(new Constituent("test", ViewNames.TOKENS, ta, i, i + 1));
        }
        View posView = new TokenLabelView(ViewNames.POS, "testPOSGen", ta, 1.0);
        posView.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 1));
        posView.addConstituent(new Constituent("VBD", ViewNames.POS, ta, 1, 2));
        posView.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 2, 3));
        ta.addView(ViewNames.TOKENS, tokenView);
        ta.addView(ViewNames.POS, posView);

        annotator.addView(ta);
        assertTrue(ta.hasView(ViewNames.MENTION));
    }
@Test
    public void testGetHeadConstituentReturnsValidConstituent() {
        TextAnnotation ta = new TextAnnotation("testCorpus", "testTextId", new String[][]{{"foo", "bar", "baz"}});
        Constituent cons = new Constituent("label", "test", ta, 0, 3);
        cons.addAttribute("EntityHeadStartSpan", "1");
        cons.addAttribute("EntityHeadEndSpan", "2");
        cons.addAttribute("Extra", "value");
        Constituent result = MentionAnnotator.getHeadConstituent(cons, ViewNames.MENTION);
        assertNotNull(result);
        assertEquals(1, result.getStartSpan());
        assertEquals(2, result.getEndSpan());
        assertEquals("value", result.getAttribute("Extra"));
    }
@Test
    public void testGetHeadConstituentWithMissingAttributesReturnsNull() {
        TextAnnotation ta = new TextAnnotation("testCorpus", "testTextId", new String[][]{{"a", "b", "c"}});
        Constituent cons = new Constituent("label", "test", ta, 0, 3);
        Constituent result = MentionAnnotator.getHeadConstituent(cons, ViewNames.MENTION);
        assertNull(result);
    }
@Test
    public void testMultipleConstructorsAllProduceValidInstance() {
        MentionAnnotator annotator1 = new MentionAnnotator();
        MentionAnnotator annotator2 = new MentionAnnotator("ERE_TYPE");
        MentionAnnotator annotator3 = new MentionAnnotator(true, "ACE_TYPE");
        MentionAnnotator annotator4 = new MentionAnnotator("nam", "nom", "pro", "extent", "ACE_TYPE");
        assertNotNull(annotator1);
        assertNotNull(annotator2);
        assertNotNull(annotator3);
        assertNotNull(annotator4);
    }
@Test
    public void testInitializeDoesNotThrowWhenInvalidMode() {
        MentionAnnotator annotator = new MentionAnnotator(false, "INVALID_MODE");
        ResourceManager rm = new ResourceConfigurator().getDefaultConfig();
        try {
            annotator.initialize(rm);
        } catch (Exception e) {
            fail("Initialization with invalid mode should not throw.");
        }
    }
@Test
    public void testAddViewOnTextAnnotationWithEmptyTokenView() throws Exception {
        MentionAnnotator annotator = new MentionAnnotator();
        ResourceManager rm = new ResourceConfigurator().getDefaultConfig();
        annotator.initialize(rm);

        TextAnnotation ta = new TextAnnotation("testCorpus", "testId", new String[][]{{}});
        View tokenView = new TokenLabelView(ViewNames.TOKENS, "src", ta, 1.0);
        View posView = new TokenLabelView(ViewNames.POS, "posSrc", ta, 1.0);
        ta.addView(ViewNames.TOKENS, tokenView);
        ta.addView(ViewNames.POS, posView);

        annotator.addView(ta);
        assertTrue(ta.hasView(ViewNames.MENTION));
    }
@Test
    public void testAddViewOnSingleTokenTextAnnotation() throws Exception {
        MentionAnnotator annotator = new MentionAnnotator();
        ResourceManager rm = new ResourceConfigurator().getDefaultConfig();
        annotator.initialize(rm);

        TextAnnotation ta = new TextAnnotation("testCorpus", "testId", new String[][]{{"Obama"}});
        View tokenView = new TokenLabelView(ViewNames.TOKENS, "src", ta, 1.0);
        Constituent token = new Constituent("NNP", ViewNames.TOKENS, ta, 0, 1);
        tokenView.addConstituent(token);
        View posView = new TokenLabelView(ViewNames.POS, "posSrc", ta, 1.0);
        posView.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 1));
        ta.addView(ViewNames.TOKENS, tokenView);
        ta.addView(ViewNames.POS, posView);

        annotator.addView(ta);

        assertTrue(ta.hasView(ViewNames.MENTION));
    }
@Test
    public void testAddViewHandlesHttpTokenProperly() throws Exception {
        MentionAnnotator annotator = new MentionAnnotator();
        ResourceManager rm = new ResourceConfigurator().getDefaultConfig();
        annotator.initialize(rm);

        TextAnnotation ta = new TextAnnotation("testCorpus", "testId", new String[][]{{"http://example.com"}});
        View tokenView = new TokenLabelView(ViewNames.TOKENS, "src", ta, 1.0);
        tokenView.addConstituent(new Constituent("LINK", ViewNames.TOKENS, ta, 0, 1));
        View posView = new TokenLabelView(ViewNames.POS, "src", ta, 1.0);
        posView.addConstituent(new Constituent("NN", ViewNames.POS, ta, 0, 1));
        ta.addView(ViewNames.TOKENS, tokenView);
        ta.addView(ViewNames.POS, posView);

        annotator.addView(ta);

        assertTrue(ta.hasView(ViewNames.MENTION));
    }
@Test
    public void testGetHeadConstituentPreservesAttributes() {
        TextAnnotation ta = new TextAnnotation("test", "id", new String[][]{{"hello", "world"}});
        Constituent original = new Constituent("label", "SOURCE", ta, 0, 2);
        original.addAttribute("EntityHeadStartSpan", "0");
        original.addAttribute("EntityHeadEndSpan", "1");
        original.addAttribute("attribute1", "value1");
        original.addAttribute("attribute2", "value2");

        Constituent result = MentionAnnotator.getHeadConstituent(original, ViewNames.MENTION);

        assertNotNull(result);
        assertEquals("value1", result.getAttribute("attribute1"));
        assertEquals("value2", result.getAttribute("attribute2"));
    } 
}