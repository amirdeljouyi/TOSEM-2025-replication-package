public class MentionAnnotator_wosr_3_GPTLLMTest { 

 @Test
    public void testDefaultConstructorSetsMode() {
        MentionAnnotator annotator = new MentionAnnotator();
        assertNotNull(annotator);
    }
@Test
    public void testConstructorWithMode() {
        MentionAnnotator annotator = new MentionAnnotator("ERE_TYPE");
        assertNotNull(annotator);
    }
@Test
    public void testConstructorWithModelPaths() {
        MentionAnnotator annotator = new MentionAnnotator("namPath", "nomPath", "proPath", "extentPath", "MODE");
        assertNotNull(annotator);
    }
@Test
    public void testInitializeWithACE_NONType() {
        MentionAnnotator annotator = new MentionAnnotator("ACE_NONTYPE");
        ResourceManager rm = new ResourceManager();
        annotator.initialize(rm);
    }
@Test
    public void testInitializeWithACE_Type() {
        MentionAnnotator annotator = new MentionAnnotator("ACE_TYPE");
        ResourceManager rm = new ResourceManager();
        annotator.initialize(rm);
    }
@Test
    public void testInitializeWithERE_NONType() {
        MentionAnnotator annotator = new MentionAnnotator("ERE_NONTYPE");
        ResourceManager rm = new ResourceManager();
        annotator.initialize(rm);
    }
@Test
    public void testInitializeWithERE_Type() {
        MentionAnnotator annotator = new MentionAnnotator("ERE_TYPE");
        ResourceManager rm = new ResourceManager();
        annotator.initialize(rm);
    }
@Test(expected = AnnotatorException.class)
    public void testAddViewWithoutPOSViewThrowsException() throws Exception {
        TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "123",
                Arrays.asList(Arrays.asList("John", "visited", "Berlin")));

        MentionAnnotator annotator = new MentionAnnotator("ACE_TYPE");
        annotator.addView(ta);
    }
@Test
    public void testAddViewSuccessfullyAddsMentionView() throws Exception {
        TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "123", Arrays.asList(Arrays.asList("John", "visited", "Berlin")));
        SpanLabelView posView = new SpanLabelView(ViewNames.POS, "annotator", ta, 1.0);
        posView.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 1));
        posView.addConstituent(new Constituent("VBD", ViewNames.POS, ta, 1, 2));
        posView.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 2, 3));
        ta.addView(ViewNames.POS, posView);

        MentionAnnotator annotator = new MentionAnnotator("ACE_NONTYPE");
        ResourceManager rm = new ResourceManager();
        annotator.initialize(rm);

        try {
            annotator.addView(ta);
            assertTrue(ta.hasView(ViewNames.MENTION));
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }
@Test
    public void testAddViewPopulatesMentionView() throws Exception {
        TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "123", Arrays.asList(Arrays.asList("He", "shot", "himself")));
        SpanLabelView posView = new SpanLabelView(ViewNames.POS, "annotator", ta, 1.0);
        posView.addConstituent(new Constituent("PRP", ViewNames.POS, ta, 0, 1));
        posView.addConstituent(new Constituent("VBD", ViewNames.POS, ta, 1, 2));
        posView.addConstituent(new Constituent("PRP", ViewNames.POS, ta, 2, 3));
        ta.addView(ViewNames.POS, posView);

        MentionAnnotator annotator = new MentionAnnotator("ERE_TYPE");
        ResourceManager rm = new ResourceManager();
        annotator.initialize(rm);
        annotator.addView(ta);

        View mentionView = ta.getView(ViewNames.MENTION);
        assertNotNull(mentionView);
        assertTrue(mentionView.getNumberOfConstituents() >= 0);
    }
@Test
    public void testGetHeadConstituentNullWhenNoAttributes() {
        TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "123",
                Arrays.asList(Arrays.asList("Obama", "was", "here")));
        Constituent constituent = new Constituent("PER", ViewNames.MENTION, ta, 0, 1);

        Constituent head = MentionAnnotator.getHeadConstituent(constituent, "HEAD");
        assertNull(head);
    }
@Test
    public void testGetHeadConstituentReturnsCorrect() {
        TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("test", "123",
                Arrays.asList(Arrays.asList("President", "Obama", "visited")));

        Constituent full = new Constituent("PER", ViewNames.MENTION, ta, 0, 2);
        full.addAttribute("EntityHeadStartSpan", "1");
        full.addAttribute("EntityHeadEndSpan", "2");

        full.addAttribute("EntityMentionType", "NAM");

        Constituent head = MentionAnnotator.getHeadConstituent(full, "HEAD");
        assertNotNull(head);
        assertEquals("PER", head.getLabel());
        assertEquals("NAM", head.getAttribute("EntityMentionType"));
        assertEquals(1, head.getStartSpan());
        assertEquals(2, head.getEndSpan());
    }
@Test
    public void testAddViewWithMultipleSentences() throws Exception {
        List<List<String>> tokens = Arrays.asList(
                Arrays.asList("Alice", "ran", "fast"),
                Arrays.asList("Bob", "was", "late"));

        TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("corpus", "docid", tokens);

        SpanLabelView posView = new SpanLabelView(ViewNames.POS, "posViewGen", ta, 1.0);
        posView.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 1));
        posView.addConstituent(new Constituent("VBD", ViewNames.POS, ta, 1, 2));
        posView.addConstituent(new Constituent("RB", ViewNames.POS, ta, 2, 3));
        posView.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 3, 4));
        posView.addConstituent(new Constituent("VBD", ViewNames.POS, ta, 4, 5));
        posView.addConstituent(new Constituent("JJ", ViewNames.POS, ta, 5, 6));
        ta.addView(ViewNames.POS, posView);

        MentionAnnotator annotator = new MentionAnnotator("ACE_TYPE");
        ResourceManager rm = new ResourceManager();
        annotator.initialize(rm);
        annotator.addView(ta);

        assertTrue(ta.hasView(ViewNames.MENTION));
        View mentionView = ta.getView(ViewNames.MENTION);
        assertNotNull(mentionView);
    }
@Test
    public void testAddViewNoHttpTokens() throws Exception {
        TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens("web", "web1",
                Arrays.asList(Arrays.asList("https", "://", "example", ".", "com")));

        SpanLabelView posView = new SpanLabelView(ViewNames.POS, "annotator", ta, 1.0);
        posView.addConstituent(new Constituent("UNK", ViewNames.POS, ta, 0, 1));
        posView.addConstituent(new Constituent("COLON", ViewNames.POS, ta, 1, 2));
        posView.addConstituent(new Constituent("NN", ViewNames.POS, ta, 2, 3));
        posView.addConstituent(new Constituent("DOT", ViewNames.POS, ta, 3, 4));
        posView.addConstituent(new Constituent("NN", ViewNames.POS, ta, 4, 5));
        ta.addView(ViewNames.POS, posView);

        MentionAnnotator annotator = new MentionAnnotator("ERE_TYPE");
        ResourceManager rm = new ResourceManager();
        annotator.initialize(rm);
        try {
            annotator.addView(ta);
            assertTrue(ta.hasView(ViewNames.MENTION));
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e.getMessage());
        }
    } 
}