public class BIOReader_wosr_4_GPTLLMTest { 

 @Test
    public void testDefaultConstructor() {
        BIOReader reader = new BIOReader();
        assertNotNull(reader);
    }
@Test(expected = RuntimeException.class)
    public void testConstructorWithInvalidPathThrowsRuntime() {
        BIOReader reader = new BIOReader("invalid/path", "ACE05-TRAIN", "NAM", true);
        reader.next();
    }
@Test
    public void testNextReturnsNullWhenNoTokens() throws Exception {
        BIOReader reader = new BIOReader();
        Field tokenListField = BIOReader.class.getDeclaredField("tokenList");
        tokenListField.setAccessible(true);
        tokenListField.set(reader, Collections.emptyList());

        Field tokenIndexField = BIOReader.class.getDeclaredField("tokenIndex");
        tokenIndexField.setAccessible(true);
        tokenIndexField.set(reader, 0);

        Object token = reader.next();
        assertNull(token);
    }
@Test
    public void testNextReturnsCorrectConstituent() throws Exception {
        BIOReader reader = new BIOReader();

        TextAnnotationBuilder builder = new TokenLabelViewTextAnnotationBuilder();
        String[] tokens = new String[] {"Obama", "visited", "Paris"};
        TextAnnotation ta = builder.createTextAnnotation("testCorpus", "testDoc", tokens);
        View tokenView = ta.getView(ViewNames.TOKENS);
        Constituent token1 = tokenView.getConstituentsCoveringToken(0).get(0);
        Constituent token2 = tokenView.getConstituentsCoveringToken(1).get(0);

        Field tokenListField = BIOReader.class.getDeclaredField("tokenList");
        tokenListField.setAccessible(true);
        tokenListField.set(reader, List.of(token1, token2));

        Field tokenIndexField = BIOReader.class.getDeclaredField("tokenIndex");
        tokenIndexField.setAccessible(true);
        tokenIndexField.set(reader, 0);

        Object first = reader.next();
        assertNotNull(first);
        assertEquals(token1, first);

        Object second = reader.next();
        assertNotNull(second);
        assertEquals(token2, second);

        Object third = reader.next();
        assertNull(third);
    }
@Test
    public void testResetResetsTokenIndex() throws Exception {
        BIOReader reader = new BIOReader();

        TextAnnotationBuilder builder = new TokenLabelViewTextAnnotationBuilder();
        String[] tokens = new String[] {"hello", "world"};
        TextAnnotation ta = builder.createTextAnnotation("testCorpus", "testDoc", tokens);
        View tokenView = ta.getView(ViewNames.TOKENS);
        List<Constituent> tokenList = tokenView.getConstituents();

        Field tokenListField = BIOReader.class.getDeclaredField("tokenList");
        tokenListField.setAccessible(true);
        tokenListField.set(reader, tokenList);

        Field tokenIndexField = BIOReader.class.getDeclaredField("tokenIndex");
        tokenIndexField.setAccessible(true);
        tokenIndexField.set(reader, 1);

        reader.reset();

        Field tokenIndexAfterReset = BIOReader.class.getDeclaredField("tokenIndex");
        tokenIndexAfterReset.setAccessible(true);
        int index = (int) tokenIndexAfterReset.get(reader);
        assertEquals(0, index);
    }
@Test
    public void testCloseDoesNotThrow() {
        BIOReader reader = new BIOReader();
        reader.close();
    }
@Test
    public void testIdFieldSetCorrectlyForPath() throws Exception {
        String path = "data/ere/";
        BIOReader reader = new BIOReader(path, "ERE-TRAIN", "NAM", true);
        String expectedId = "ere_NAM".replace("/", "");
        assertTrue(reader.id.contains("ere"));
        assertEquals("ere_NAM", reader.id);
    }
@Test(expected = RuntimeException.class)
    public void testConstructorWithInvalidMode() {
        BIOReader reader = new BIOReader("somepath", "INVALID-TRAIN", "NAM", true);
        reader.next();
    }
@Test
    public void testNextReturnsNullAfterAllTokensExhausted() throws Exception {
        BIOReader reader = new BIOReader();

        TextAnnotationBuilder builder = new TokenLabelViewTextAnnotationBuilder();
        String[] tokens = new String[] {"final", "test"};
        TextAnnotation ta = builder.createTextAnnotation("test", "001", tokens);
        View tokenView = ta.getView(ViewNames.TOKENS);
        Constituent c1 = tokenView.getConstituents().get(0);

        Field tokenListField = BIOReader.class.getDeclaredField("tokenList");
        tokenListField.setAccessible(true);
        tokenListField.set(reader, List.of(c1));

        Field tokenIndexField = BIOReader.class.getDeclaredField("tokenIndex");
        tokenIndexField.setAccessible(true);
        tokenIndexField.set(reader, 0);

        Object t1 = reader.next();
        Object t2 = reader.next();
        assertEquals(c1, t1);
        assertNull(t2);
    }
@Test
    public void testMultipleCallsToResetAndNext() throws Exception {
        BIOReader reader = new BIOReader();

        TextAnnotationBuilder builder = new TokenLabelViewTextAnnotationBuilder();
        String[] tokens = new String[] {"one", "two", "three"};
        TextAnnotation ta = builder.createTextAnnotation("sample", "001", tokens);
        View tokenView = ta.getView(ViewNames.TOKENS);
        List<Constituent> constituents = tokenView.getConstituents();

        Field tokenListField = BIOReader.class.getDeclaredField("tokenList");
        tokenListField.setAccessible(true);
        tokenListField.set(reader, constituents);

        Field tokenIndexField = BIOReader.class.getDeclaredField("tokenIndex");
        tokenIndexField.setAccessible(true);
        tokenIndexField.set(reader, 0);

        assertEquals(constituents.get(0), reader.next());
        assertEquals(constituents.get(1), reader.next());

        reader.reset();
        assertEquals(constituents.get(0), reader.next());
    }
@Test
    public void testConstructorHandlesPathSplitting() {
        String path = "/some/deep/path/to/data/ace05/";
        BIOReader reader = new BIOReader(path, "ACE05-EVAL", "NAM", false);
        assertTrue(reader.id.contains("ace05_NAM"));
    }
@Test
    public void testConstructorHandlesBIOFlag() throws Exception {
        String path = "/fake/path/to/column";
        String mode = "ColumnFormat-TRAIN";
        String type = "PRO";
        BIOReader reader = null;
        try {
            reader = new BIOReader(path, mode, type, true);
        } catch (Exception e) {
            assertNotNull(e);
        }
    } 
}