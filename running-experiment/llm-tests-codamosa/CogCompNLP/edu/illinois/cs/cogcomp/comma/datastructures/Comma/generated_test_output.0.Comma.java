import org.junit.Test;
import static org.junit.Assert.*;

public class GeneratedTest {

@Test
public void test1()
{
    TextAnnotation ta = new TextAnnotation("", "", new String[0][]);
    TreeView parseView = new TreeView(Comma.CONSTITUENT_PARSER, ta);
    Constituent parent = new Constituent("NP", Comma.CONSTITUENT_PARSER, ta, 0, 3);
    Constituent comma1 = new Constituent(",", Comma.CONSTITUENT_PARSER, ta, 1, 2);
    Constituent comma2 = new Constituent(",", Comma.CONSTITUENT_PARSER, ta, 2, 3);
    parseView.addConstituent(parent);
    parseView.addConstituent(comma1);
    parseView.addConstituent(comma2);
    parseView.addConstituentRelation("child", parent, comma1);
    parseView.addConstituentRelation("child", parent, comma2);
    View viewMap = parseView;
    Comma.GOLD = false;
    Comma commaObj1 = new Comma();
    Comma commaObj2 = new Comma();
    commaObj1.s = new Comma.SentenceInfo();
    commaObj2.s = new Comma.SentenceInfo();
    commaObj1.s.ta = ta;
    commaObj2.s.ta = ta;
    ta.addView(CONSTITUENT_PARSER, parseView);
    commaObj1.getCommaConstituentFromTree = ( v) -> comma1;
    commaObj2.getCommaConstituentFromTree = ( v) -> comma2;
    boolean result = commaObj1.isSibling(commaObj2);
    assertTrue(result);
}

@Test
public void test2()
{
    Comma comma1 = new Comma();
    comma1.commaPosition = 10;
    Comma comma2 = new Comma();
    comma2.commaPosition = 5;
    Comma comma3 = new Comma();
    comma3.commaPosition = 15;
    Comma testComma = new Comma() {
        @Override
        public List<Comma> getSiblingCommas() {
            return Arrays.asList(comma1, comma2, comma3);
        }
    };
    Comma result = testComma.getSiblingCommaHead();
    assertEquals(5, result.commaPosition);
}

@Test
public void test3()
{
    CommaSRLSentence expectedSentence = new CommaSRLSentence();
    Comma comma = new Comma();
    try {
        Field field = Comma.class.getDeclaredField("s");
        field.setAccessible(true);
        field.set(comma, expectedSentence);
    } catch (NoSuchFieldException | IllegalAccessException e) {
        fail("Failed to set up test due to reflection error: " + e.getMessage());
    }
    CommaSRLSentence actualSentence = comma.getSentence();
    assertSame("Expected getSentence() to return the same CommaSRLSentence instance", expectedSentence, actualSentence);
}

@Test
public void test4()
{
    TextAnnotation ta = new TextAnnotation("dummyCorpusId", "dummyId", "The quick, brown fox jumps.");
    SpanLabelView chunkView = new SpanLabelView(ViewNames.SHALLOW_PARSE, "testGenerator", ta, 1.0);
    Constituent c1 = new Constituent("NP", "testGenerator", ta, 0, 1);
    Constituent c2 = new Constituent("NP", "testGenerator", ta, 1, 2);
    Constituent c3 = new Constituent("NP", "testGenerator", ta, 2, 3);
    Constituent c4 = new Constituent("NP", "testGenerator", ta, 3, 4);
    chunkView.addConstituent(c1);
    chunkView.addConstituent(c2);
    chunkView.addConstituent(c3);
    chunkView.addConstituent(c4);
    ta.addView(SHALLOW_PARSE, chunkView);
    Comma comma = new Comma();
    try {
        Field taField = Comma.class.getDeclaredField("ta");
        taField.setAccessible(true);
        taField.set(comma, ta);
        Field commaPosField = Comma.class.getDeclaredField("commaPosition");
        commaPosField.setAccessible(true);
        commaPosField.setInt(comma, 2);
    } catch (NoSuchFieldException | IllegalAccessException e) {
        fail("Failed to set up test due to reflection: " + e.getMessage());
    }
    Constituent result = comma.getChunkToLeftOfComma(2);
    assertNotNull("Constituent should not be null", result);
    assertEquals("Retrieved chunk should match expected", c2.getStartSpan(), result.getStartSpan());
    assertEquals("Retrieved chunk should match expected", c2.getEndSpan(), result.getEndSpan());
}

@Test
public void test5()
{
    String[] tokens = new String[]{ "The", "cat", ",", "sat", "on", "the", "mat" };
    String sentence = String.join(" ", tokens);
    TextAnnotation ta = new TextAnnotation("testCorpus", "testId", sentence);
    ta.setTokens(tokens);
    Constituent chunk1 = new Constituent("NP", "SHALLOW_PARSE", ta, 3, 4);
    Constituent chunk2 = new Constituent("PP", "SHALLOW_PARSE", ta, 4, 6);
    Constituent chunk3 = new Constituent("NP", "SHALLOW_PARSE", ta, 6, 7);
    SpanLabelView chunkView = new SpanLabelView(ViewNames.SHALLOW_PARSE, "testGenerator", ta, 1.0);
    chunkView.addConstituent(chunk1);
    chunkView.addConstituent(chunk2);
    chunkView.addConstituent(chunk3);
    ta.addView(SHALLOW_PARSE, chunkView);
    Sentence sentenceWrapper = new Sentence(ta, 0, tokens.length);
    Comma comma = new Comma(sentenceWrapper, 2);
    Constituent result = comma.getChunkToRightOfComma(2);
    assertNotNull(result);
    assertEquals("PP", result.getLabel());
    assertEquals(4, result.getStartSpan());
    assertEquals(6, result.getEndSpan());
}

@Test
public void test6()
{
    TreeView mockParseView = mock(TreeView.class);
    Constituent mockConstituent = mock(Constituent.class);
    Constituent mockParsePhrase = mock(Constituent.class);
    Comma commaInstance = new Comma() {
        {
            this.commaPosition = 3;
        }
    };
    when(mockConstituent.isConsituentInRange(3, 4)).thenReturn(true);
    when(mockParseView.getConstituents()).thenReturn(Collections.singletonList(mockConstituent));
    when(mockParseView.getParsePhrase(mockConstituent)).thenReturn(mockParsePhrase);
    Constituent result = commaInstance.getCommaConstituentFromTree(mockParseView);
    assertEquals(mockParsePhrase, result);
}

@Test
public void test7()
{
    TextAnnotation goldTextAnnotation = mock(TextAnnotation.class);
    TextAnnotation textAnnotation = mock(TextAnnotation.class);
    TreeView mockParseView = mock(TreeView.class);
    Constituent mockComma = mock(Constituent.class);
    Constituent expectedLeftConstituent = mock(Constituent.class);
    Comma.GOLD = true;
    Comma commaInstance = new Comma();
    commaInstance.s = mock(Sentence.class);
    when(commaInstance.s.goldTa).thenReturn(goldTextAnnotation);
    when(goldTextAnnotation.getView(PARSE_GOLD)).thenReturn(mockParseView);
    Comma spyComma = spy(commaInstance);
    doReturn(mockComma).when(spyComma).getCommaConstituentFromTree(mockParseView);
    doReturn(expectedLeftConstituent).when(spyComma).getSiblingToLeft(1, mockComma, mockParseView);
    Constituent actual = spyComma.getPhraseToLeftOfComma(1);
    assertSame(expectedLeftConstituent, actual);
}

@Test
public void test8()
{
    Field goldField = Comma.class.getDeclaredField("GOLD");
    goldField.setAccessible(true);
    goldField.setBoolean(null, true);
    Comma commaInstance = new Comma();
    TextAnnotation goldTaMock = mock(TextAnnotation.class);
    TreeView parseViewMock = mock(TreeView.class);
    Constituent commaConstituentMock = mock(Constituent.class);
    Constituent parentConstituentMock = mock(Constituent.class);
    Constituent expectedLeftSiblingMock = mock(Constituent.class);
    Field sField = Comma.class.getDeclaredField("s");
    sField.setAccessible(true);
    Object sentenceMock = mock(Class.forName("edu.illinois.cs.cogcomp.comma.datastructures.Sentence"));
    sField.set(commaInstance, sentenceMock);
    when(((TextAnnotation) (Class.forName("edu.illinois.cs.cogcomp.comma.datastructures.Sentence").getDeclaredField("goldTa").get(sentenceMock))).getView(PARSE_GOLD)).thenReturn(parseViewMock);
    Method getCommaMethod = Comma.class.getDeclaredMethod("getCommaConstituentFromTree", TreeView.class);
    getCommaMethod.setAccessible(true);
    getCommaMethod.invoke(commaInstance, parseViewMock);
    Comma spyComma = spy(commaInstance);
    doReturn(commaConstituentMock).when(spyComma).getCommaConstituentFromTree(parseViewMock);
    mockStatic(TreeView.class);
    when(TreeView.getParent(commaConstituentMock)).thenReturn(parentConstituentMock);
    doReturn(expectedLeftSiblingMock).when(spyComma).getSiblingToLeft(1, parentConstituentMock, parseViewMock);
    Constituent result = spyComma.getPhraseToLeftOfParent(1);
    assertSame(expectedLeftSiblingMock, result);
}

@Test
public void test9()
{
    Field goldField = Comma.class.getDeclaredField("GOLD");
    goldField.setAccessible(true);
    goldField.setBoolean(null, true);
    Comma comma = new Comma();
    TextAnnotation mockTextAnnotation = mock(TextAnnotation.class);
    TreeView mockParseGoldView = mock(TreeView.class);
    Constituent mockCommaConstituent = mock(Constituent.class);
    Constituent mockRightSibling = mock(Constituent.class);
    Field sField = Comma.class.getDeclaredField("s");
    sField.setAccessible(true);
    Object sInstance = sField.get(comma);
    Field goldTaField = sInstance.getClass().getDeclaredField("goldTa");
    goldTaField.setAccessible(true);
    goldTaField.set(sInstance, mockTextAnnotation);
    when(mockTextAnnotation.getView(PARSE_GOLD)).thenReturn(mockParseGoldView);
    Method getCommaConstituentMethod = Comma.class.getDeclaredMethod("getCommaConstituentFromTree", TreeView.class);
    getCommaConstituentMethod.setAccessible(true);
    getCommaConstituentMethod.invoke(comma, mockParseGoldView);
    Field commaClass = Comma.class.getDeclaredField("class");
    commaClass.setAccessible(true);
    Method getSiblingMethod = Comma.class.getDeclaredMethod("getSiblingToRight", int.class, Constituent.class, TreeView.class);
    getSiblingMethod.setAccessible(true);
    Comma spyComma = spy(comma);
    doReturn(mockCommaConstituent).when(spyComma).getCommaConstituentFromTree(mockParseGoldView);
    doReturn(mockRightSibling).when(spyComma).getSiblingToRight(1, mockCommaConstituent, mockParseGoldView);
    Constituent result = spyComma.getPhraseToRightOfComma(1);
    assertEquals(mockRightSibling, result);
}

@Test
public void test10()
{
    TreeView mockParseView = mock(TreeView.class);
    Constituent mockComma = mock(Constituent.class);
    Constituent mockParent = mock(Constituent.class);
    Constituent expectedSibling = mock(Constituent.class);
    TextAnnotation mockGoldTa = mock(TextAnnotation.class);
    TextAnnotation mockTa = mock(TextAnnotation.class);
    TreeView mockConstituentParseView = mock(TreeView.class);
    TreeView mockGoldParseView = mock(TreeView.class);
    when(mockTa.getView(CONSTITUENT_PARSER)).thenReturn(mockConstituentParseView);
    when(mockGoldTa.getView(PARSE_GOLD)).thenReturn(mockGoldParseView);
    Comma commaInstance = new Comma();
    Field sField = Comma.class.getDeclaredField("s");
    sField.setAccessible(true);
    Object sInstance = sField.get(null);
    Field taField = sInstance.getClass().getDeclaredField("ta");
    taField.setAccessible(true);
    taField.set(sInstance, mockTa);
    Field goldTaField = sInstance.getClass().getDeclaredField("goldTa");
    goldTaField.setAccessible(true);
    goldTaField.set(sInstance, mockGoldTa);
    Field goldField = Comma.class.getDeclaredField("GOLD");
    goldField.setAccessible(true);
    goldField.set(null, false);
    Comma spyComma = spy(commaInstance);
    doReturn(mockComma).when(spyComma).getCommaConstituentFromTree(mockConstituentParseView);
    mockStatic(TreeView.class);
    when(TreeView.getParent(mockComma)).thenReturn(mockParent);
    doReturn(expectedSibling).when(spyComma).getSiblingToRight(1, mockParent, mockConstituentParseView);
    Constituent result = spyComma.getPhraseToRightOfParent(1);
    assertSame(expectedSibling, result);
}

@Test
public void test11()
{
    TreeView mockParseView = mock(TreeView.class);
    Constituent target = mock(Constituent.class);
    Constituent siblingLeft = mock(Constituent.class);
    IQueryable<Constituent> siblings = mock(IQueryable.class);
    IQueryable<Constituent> firstQuery = mock(IQueryable.class);
    when(mockParseView.where(Queries.isSiblingOf(target))).thenReturn(siblings);
    when(siblings.where(Queries.adjacentToBefore(target))).thenReturn(firstQuery);
    Iterator<Constituent> it = Arrays.asList(siblingLeft).iterator();
    when(firstQuery.iterator()).thenReturn(it);
    Comma comma = new Comma();
    Constituent result = comma.getSiblingToLeft(1, target, mockParseView);
    assertSame(siblingLeft, result);
}

@Test
public void test12()
{
    Constituent c = mock(Constituent.class);
    Constituent sibling1 = mock(Constituent.class);
    Constituent sibling2 = mock(Constituent.class);
    TreeView parseView = mock(TreeView.class);
    IQueryable<Constituent> siblingsQuery = mock(IQueryable.class);
    IQueryable<Constituent> firstQuery = mock(IQueryable.class);
    when(parseView.where(any())).thenReturn(siblingsQuery);
    when(siblingsQuery.where(any())).thenReturn(firstQuery);
    Iterator<Constituent> firstIter = Arrays.asList(sibling1).iterator();
    when(firstQuery.iterator()).thenReturn(firstIter);
    IQueryable<Constituent> secondQuery = mock(IQueryable.class);
    when(siblingsQuery.where(argThat(( q) -> {
        Constituent target = null;
        try {
            target = ((Constituent) (q.getClass().getMethod("getConstituent").invoke(q)));
        } catch (Exception e) {
        }
        return target == sibling1;
    }))).thenReturn(secondQuery);
    Iterator<Constituent> secondIter = Arrays.asList(sibling2).iterator();
    when(secondQuery.iterator()).thenReturn(secondIter);
    Comma comma = new Comma(new ResourceManager(new Properties()));
    Constituent result = comma.getSiblingToRight(1, c, parseView);
    assertEquals("The returned sibling should be the immediate right sibling.", sibling1, result);
}

@Test
public void test13()
{
    TextAnnotation expectedGoldTa = new TextAnnotation();
    TextAnnotation expectedTa = new TextAnnotation();
    Comma comma = new Comma();
    comma.s = new Comma.State();
    comma.s.goldTa = expectedGoldTa;
    comma.s.ta = expectedTa;
    TextAnnotation result = comma.getTextAnnotation(true);
    assertSame("Expected gold TextAnnotation to be returned when gold is true", expectedGoldTa, result);
}

@Test
public void test14()
{
    Comma comma = new Comma(5, "example", "text");
    int position = comma.getPosition();
    assertEquals(5, position);
}

@Test
public void test15()
{
    Comma comma = new Comma();
    comma.s = new Object() {
        String[] getTokens() {
            return new String[]{ "This", "is", "a", "test", ",", "indeed", "it", "is" };
        }
    };
    comma.commaPosition = 4;
    Comma testComma = new Comma() {
        @Override
        public String getBayraktarLabel() {
            return "EXPLANATION";
        }
    };
    testComma.s = comma.s;
    testComma.commaPosition = comma.commaPosition;
    String expected = "This is a test , [EXPLANATION] indeed it is";
    String actual = testComma.getBayraktarAnnotatedText();
    assertEquals(expected, actual);
}

@Test
public void test16()
{
    Comma commaInstance = new Comma();
    Method method = BayraktarPatternLabeler.class.getDeclaredMethod("getLabel", Comma.class);
    Field modifiersField = Field.class.getDeclaredField("modifiers");
    modifiersField.setAccessible(true);
    Method getDeclaredMethods0 = Class.class.getDeclaredMethod("getDeclaredMethods0", boolean.class);
    getDeclaredMethods0.setAccessible(true);
    Field field = BayraktarPatternLabeler.class.getDeclaredField("INSTANCE");
    field.setAccessible(true);
    modifiersField.setInt(field, field.getModifiers() & (~Modifier.FINAL));
    field.set(null, null);
    Proxy proxy = ((Proxy) (Proxy.newProxyInstance(BayraktarPatternLabeler.class.getClassLoader(), new Class<?>[0], ( proxy1, method1, args) -> null)));
    InvocationHandler handler = ( proxy1, method1, args) -> null;
    Method getLabelMethod = BayraktarPatternLabeler.class.getDeclaredMethod("getLabel", Comma.class);
    getLabelMethod.setAccessible(true);
    String result = commaInstance.getBayraktarLabel();
    assertEquals("Other", result);
}

@Test
public void test17()
{
    Comma commaInstance = new Comma();
    TreeView mockTreeView = mock(TreeView.class);
    TextAnnotation mockGoldTextAnnotation = mock(TextAnnotation.class);
    Comma.GOLD = true;
    commaInstance.s = mock(Sentence.class);
    commaInstance.s.goldTa = mockGoldTextAnnotation;
    when(mockGoldTextAnnotation.getView(PARSE_GOLD)).thenReturn(mockTreeView);
    Comma spyComma = spy(commaInstance);
    doReturn("NP,VP").when(spyComma).getBayraktarPattern(mockTreeView);
    String result = spyComma.getBayraktarPattern();
    assertEquals("NP,VP", result);
}

@Test
public void test18()
{
    Comma.GOLD = true;
    TreeView mockTreeView = mock(TreeView.class);
    TextAnnotation mockGoldTa = mock(TextAnnotation.class);
    when(mockGoldTa.getView(PARSE_GOLD)).thenReturn(mockTreeView);
    Comma comma = new Comma();
    comma.s = new Comma.Sentence();
    comma.s.goldTa = mockGoldTa;
    Comma spyComma = spy(comma);
    doReturn("NP-PRN-VP").when(spyComma).getBayraktarPattern(mockTreeView);
    String result = spyComma.getBayraktarPattern();
    assertEquals("NP-PRN-VP", result);
}

@Test
public void test19()
{
    Comma comma = new Comma();
    comma.commaPosition = 7;
    comma.s = new Object() {
        public TextAnnotation goldTa = new TextAnnotation("source", "view", "text") {
            @Override
            public String getId() {
                return "DOC123";
            }
        };
    };
    assertEquals("7 DOC123", comma.getCommaID());
}

@Test
public void test20()
{
    Comma comma = new Comma();
    List<String> labelList = Arrays.asList("Coordinating", "ClauseBoundary");
    comma.labels = labelList;
    String result = comma.getLabel();
    assertEquals("Coordinating", result);
}

@Test
public void test21()
{
    TextAnnotation ta = new TextAnnotation("dummyCorpus", "dummyId", new String[]{ "John", "Doe", "went", "home" });
    View nerView = new SpanLabelView(ViewNames.NER_CONLL, "testGenerator", ta, 1.0);
    Constituent namedEntity = new Constituent("PERSON", 0, 2, ViewNames.NER_CONLL, ta, 1.0);
    nerView.addConstituent(namedEntity);
    ta.addView(NER_CONLL, nerView);
    Constituent inputConstituent = new Constituent("dummy", 0, 3, "testView", ta);
    Comma comma = new Comma();
    comma.s = new Comma.Sentence();
    comma.s.ta = ta;
    String result = comma.getNamedEntityTag(inputConstituent);
    assertEquals("+PERSON", result);
}

@Test
public void test22()
{
    Comma.NERlexicalise = true;
    Comma.POSlexicalise = true;
    Constituent mockConstituent = mock(Constituent.class);
    when(mockConstituent.getLabel()).thenReturn("NP");
    when(mockConstituent.getViewName()).thenReturn(PARSE_GOLD);
    Constituent mockTarget = mock(Constituent.class);
    when(mockTarget.getLabel()).thenReturn("VP");
    Relation mockRelation = mock(Relation.class);
    when(mockRelation.getTarget()).thenReturn(mockTarget);
    List<Relation> outgoingRelations = new ArrayList<>();
    outgoingRelations.add(mockRelation);
    when(mockConstituent.getOutgoingRelations()).thenReturn(outgoingRelations);
    when(mockConstituent.getSpan()).thenReturn(new IntPair(0, 2));
    TextAnnotation mockTextAnnotation = mock(TextAnnotation.class);
    when(mockConstituent.getTextAnnotation()).thenReturn(mockTextAnnotation);
    mockStaticPOSUtils(mockTextAnnotation, new String[]{ "DT", "NN" });
    Comma comma = new Comma();
    String result = comma.getNotation(mockConstituent);
    assertEquals("NPVP-ORG- DT NN", result);
}

@Test
public void test23()
{
    TokenLabelView mockPosView = mock(TokenLabelView.class);
    when(mockPosView.getLabel(4)).thenReturn("DT");
    TextAnnotation mockTa = mock(TextAnnotation.class);
    when(mockTa.getView(POS)).thenReturn(mockPosView);
    Comma.SentenceData sentenceData = new Comma.SentenceData();
    sentenceData.ta = mockTa;
    sentenceData.goldTa = mockTa;
    Comma comma = new Comma();
    comma.s = sentenceData;
    comma.commaPosition = 5;
    Comma commaWithOverride = new Comma() {
        {
            this.s = sentenceData;
            this.commaPosition = 5;
        }

        @Override
        protected String getWordToRight(int distance) {
            return "the";
        }
    };
    Comma.GOLD = false;
    String result = commaWithOverride.getPOSToLeft(1);
    assertEquals("DT-the", result);
}

@Test
public void test24()
{
    Comma.GOLD = true;
    TokenLabelView mockPosView = mock(TokenLabelView.class);
    when(mockPosView.getLabel(6)).thenReturn("DT");
    TextAnnotation mockGoldTa = mock(TextAnnotation.class);
    when(mockGoldTa.getView(POS)).thenReturn(mockPosView);
    Comma comma = new Comma() {
        {
            this.commaPosition = 5;
            this.s = mock(Sentence.class);
            this.s.goldTa = mockGoldTa;
        }

        @Override
        public String getWordToRight(int distance) {
            return "the";
        }
    };
    String result = comma.getPOSToRight(1);
    assertEquals("DT-the", result);
}

@Test
public void test25()
{
    Comma.NERlexicalise = true;
    Comma.POSlexicalise = true;
    TextAnnotation mockTA = new TextAnnotation("corpus", "id", new String[]{ "word1", "word2" });
    Constituent mockConstituent = new Constituent("ARG1-Extra", "view", mockTA, 0, 2);
    Constituent spiedConstituent = new Constituent("ARG1-Extra", "view", mockTA, 0, 2) {
        @Override
        public String getLabel() {
            return "ARG1-Extra";
        }

        @Override
        public IntPair getSpan() {
            return new IntPair(0, 2);
        }

        @Override
        public TextAnnotation getTextAnnotation() {
            return mockTA;
        }
    };
    Comma commaTest = new Comma() {
        @Override
        protected String getNamedEntityTag(Constituent c) {
            return "PERSON";
        }
    };
    String result = commaTest.getStrippedNotation(spiedConstituent);
    assertEquals("ARG1-PERSON- NN VB", result);
}

@Test
public void test26()
{
    TextAnnotation textAnnotation = new TextAnnotation("dummyCorpus", "dummyView", "This is, a sample sentence.".toLowerCase());
    List<String> tokens = Arrays.asList("this", "is", ",", "a", "sample", "sentence", ".");
    Comma comma = new Comma();
    comma.s = new Comma.Sentence();
    comma.s.ta = new TextAnnotation("dummyCorpus", "dummyView", "");
    comma.commaPosition = 2;
    comma.s.ta = new TextAnnotation("source", "id", "this is , a sample sentence .") {
        @Override
        public String getToken(int tokenIndex) {
            return tokens.get(tokenIndex);
        }
    };
    String result = comma.getWordToLeft(1);
    assertEquals("is", result);
}

@Test
public void test27()
{
    TextAnnotation ta = new TextAnnotation("corpusId", "docId", new String[]{ "The", "quick", "brown", "fox", ",", "jumps", "over", "the", "lazy", "dog" });
    Sentence sentence = new Sentence();
    sentence.ta = ta;
    Comma comma = new Comma();
    comma.s = sentence;
    comma.commaPosition = 4;
    String result = comma.getWordToRight(1);
    assertEquals("jumps", result);
}

@Test
public void test28()
{
    Comma comma = spy(new Comma());
    doReturn("NP").when(comma).getNotation("left2");
    doReturn("VP").when(comma).getNotation("left1");
    doReturn("PP").when(comma).getNotation("right1");
    doReturn("ADJP").when(comma).getNotation("right2");
    doReturn("left2").when(comma).getChunkToLeftOfComma(2);
    doReturn("left1").when(comma).getChunkToLeftOfComma(1);
    doReturn("right1").when(comma).getChunkToRightOfComma(1);
    doReturn("right2").when(comma).getChunkToRightOfComma(2);
    List<String> chunkWindow = Arrays.asList("NP", "VP", "PP", "ADJP");
    List<String> uni = Arrays.asList("NP", "VP", "PP", "ADJP");
    List<String> bi = Arrays.asList("NP VP", "VP PP", "PP ADJP");
    mockStatic(NgramUtils.class);
    when(NgramUtils.ngrams(1, chunkWindow)).thenReturn(uni);
    when(NgramUtils.ngrams(2, chunkWindow)).thenReturn(bi);
    String[] result = comma.getChunkNgrams();
    unmockStatic(NgramUtils.class);
    String[] expected = new String[]{ "NP", "VP", "PP", "ADJP", "NP VP", "VP PP", "PP ADJP" };
    assertArrayEquals(expected, result);
}

@Test
public void test29()
{
    TextAnnotation ta = new TextAnnotation("testCorpus", "testId", "This is a test , sentence.");
    TreeView view = new TreeView(ViewNames.DEPENDENCY_STANFORD, ta);
    Constituent leftConst = new Constituent("token", ViewNames.DEPENDENCY_STANFORD, ta, 1, 2);
    Constituent rightConst = new Constituent("token", ViewNames.DEPENDENCY_STANFORD, ta, 5, 6);
    Relation rel = new Relation("nsubj", leftConst, rightConst, 1.0);
    leftConst.addRelation(rel);
    view.addConstituent(leftConst);
    view.addConstituent(rightConst);
    ta.addView(DEPENDENCY_STANFORD, view);
    Comma comma = new Comma();
    comma.ta = ta;
    comma.commaPosition = 3;
    String[] result = comma.getLeftToRightDependencies();
    assertArrayEquals(new String[]{ "nsubj" }, result);
}

@Test
public void test30()
{
    TreeView treeViewMock = mock(TreeView.class);
    TextAnnotation taMock = mock(TextAnnotation.class);
    View viewMock = treeViewMock;
    Constituent leftConstituent = mock(Constituent.class);
    Constituent rightTarget = mock(Constituent.class);
    Relation relationMock = mock(Relation.class);
    when(taMock.getView(DEPENDENCY_STANFORD)).thenReturn(viewMock);
    when(treeViewMock.getConstituentsCoveringSpan(0, 3)).thenReturn(Arrays.asList(leftConstituent));
    when(leftConstituent.getIncomingRelations()).thenReturn(Arrays.asList(relationMock));
    when(relationMock.getSource()).thenReturn(rightTarget);
    when(rightTarget.getStartSpan()).thenReturn(4);
    when(relationMock.getRelationName()).thenReturn("nsubj");
    Comma commaInstance = new Comma();
    Field taField = Comma.class.getDeclaredField("ta");
    taField.setAccessible(true);
    taField.set(commaInstance, taMock);
    Field commaPosField = Comma.class.getDeclaredField("commaPosition");
    commaPosField.setAccessible(true);
    commaPosField.setInt(commaInstance, 3);
    String[] result = commaInstance.getRightToLeftDependencies();
    assertArrayEquals(new String[]{ "nsubj" }, result);
}

@Test
public void test31()
{
    TextAnnotation ta = new TextAnnotation("", "", new String[]{ "John", "saw", "Mary", ",", "walking", "quickly" });
    PredicateArgumentView verbView = new PredicateArgumentView(ViewNames.SRL_VERB, ta);
    Constituent verbPred = new Constituent("predicate", ViewNames.SRL_VERB, ta, 1, 2);
    Constituent verbArg = new Constituent("ARG1", ViewNames.SRL_VERB, ta, 4, 5);
    Relation verbRel = new Relation("ARG1", verbPred, verbArg);
    verbView.addPredicateArgument(verbPred, verbArg, "ARG1");
    PredicateArgumentView nomView = new PredicateArgumentView(ViewNames.SRL_NOM, ta);
    PredicateArgumentView prepView = new PredicateArgumentView(ViewNames.SRL_PREP, ta);
    Constituent prepPred = new Constituent("predicate", ViewNames.SRL_PREP, ta, 0, 1);
    Constituent prepArg = new Constituent("ARG2", ViewNames.SRL_PREP, ta, 5, 6);
    Relation prepRel = new Relation("ARG2", prepPred, prepArg);
    prepView.addPredicateArgument(prepPred, prepArg, "ARG2");
    ta.addView(SRL_VERB, verbView);
    ta.addView(SRL_NOM, nomView);
    ta.addView(SRL_PREP, prepView);
    Comma comma = new Comma();
    comma.commaPosition = 4;
    Comma.GOLD = false;
    comma.s = new Object() {
        public TextAnnotation ta = ta;

        public TextAnnotation goldTa = null;
    };
    PredicateArgumentView pavVerb = ((PredicateArgumentView) (ta.getView(SRL_VERB)));
    pavVerb.setPredicateLemma(verbPred, "see");
    PredicateArgumentView pavPrep = ((PredicateArgumentView) (ta.getView(SRL_PREP)));
    pavPrep.setPredicateLemma(prepPred, "walk");
    List<String> result = comma.getContainingSRLs();
    assertEquals(2, result.size());
    assertTrue(result.contains("seeARG1"));
    assertTrue(result.contains("walkARG2"));
}

@Test
public void test32()
{
    Comma comma = new Comma();
    List<String> expectedLabels = Arrays.asList("label1", "label2", "label3");
    List<String> labelsField = comma.getLabels();
    labelsField.clear();
    labelsField.add("label1");
    labelsField.add("label2");
    labelsField.add("label3");
    List<String> actualLabels = comma.getLabels();
    assertEquals(expectedLabels, actualLabels);
}

@Test
public void test33()
{
    TextAnnotation ta = mock(TextAnnotation.class);
    TreeView parseView = mock(TreeView.class);
    View parseViewAsView = parseView;
    when(ta.getView(CONSTITUENT_PARSER)).thenReturn(parseViewAsView);
    Comma.SentenceData sentenceData = mock(SentenceData.class);
    Comma comma1 = mock(Comma.class);
    Comma comma2 = mock(Comma.class);
    Comma comma3 = mock(Comma.class);
    Constituent const1 = mock(Constituent.class);
    Constituent const2 = mock(Constituent.class);
    Constituent const3 = mock(Constituent.class);
    when(comma1.getCommaConstituentFromTree(parseView)).thenReturn(const1);
    when(comma2.getCommaConstituentFromTree(parseView)).thenReturn(const2);
    when(comma3.getCommaConstituentFromTree(parseView)).thenReturn(const3);
    when(comma3.getCommaConstituentFromTree(parseView)).thenReturn(const3);
    when(sentenceData.getCommas()).thenReturn(Arrays.asList(comma1, comma2, comma3));
    when(sentenceData.ta).thenReturn(ta);
    when(sentenceData.goldTa).thenReturn(null);
    Comma.GOLD = false;
    Comma actualComma = new Comma();
    setPrivateField(actualComma, "s", sentenceData);
    setPrivateField(actualComma, "s", sentenceData);
    QueryableList<Constituent> qList = new QueryableList(Arrays.asList(const1, const2, const3));
    Iterable<Constituent> siblingSet = Arrays.asList(const1, const2);
    List<Comma> siblings = actualComma.getSiblingCommas();
    assertNotNull(siblings);
    assertEquals(2, siblings.size());
    assertTrue(siblings.contains(comma1));
    assertTrue(siblings.contains(comma2));
    assertFalse(siblings.contains(comma3));
}
