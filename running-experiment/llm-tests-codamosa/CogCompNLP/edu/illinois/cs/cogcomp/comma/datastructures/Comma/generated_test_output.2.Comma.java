import org.junit.Test;
import static org.junit.Assert.*;

public class GeneratedTest {

@Test
public void test1()
{
    TextAnnotation ta = new TextAnnotation("corpus", "id", new String[][]{ new String[]{ "This", "is", "a", "test", "," } });
    TreeView parseView = new TreeView(ViewNames.PARSE_GOLD, ta);
    Constituent parent = new Constituent("NP", ViewNames.PARSE_GOLD, ta, 0, 1);
    Constituent comma1 = new Constituent("COMMA", ViewNames.PARSE_GOLD, ta, 2, 3);
    Constituent comma2 = new Constituent("COMMA", ViewNames.PARSE_GOLD, ta, 3, 4);
    parseView.addConstituent(parent);
    parent.addChild(comma1);
    parent.addChild(comma2);
    parseView.addConstituent(comma1);
    parseView.addConstituent(comma2);
    View dummyView = parseView;
    Comma s1 = new Comma() {
        {
            this.s = new Sentence();
        }

        @Override
        public Constituent getCommaConstituentFromTree(TreeView tree) {
            return comma1;
        }

        class Sentence {
            TextAnnotation ta = ta;

            TextAnnotation goldTa = ta;

            View getView(String name) {
                return dummyView;
            }
        }
    };
    Comma s2 = new Comma() {
        {
            this.s = new Sentence();
        }

        @Override
        public Constituent getCommaConstituentFromTree(TreeView tree) {
            return comma2;
        }

        class Sentence {
            TextAnnotation ta = ta;

            TextAnnotation goldTa = ta;

            View getView(String name) {
                return dummyView;
            }
        }
    };
    Comma.GOLD = true;
    boolean result = s1.isSibling(s2);
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
    Comma commaInstance = new Comma();
    try {
        Field field = Comma.class.getDeclaredField("s");
        field.setAccessible(true);
        field.set(commaInstance, expectedSentence);
    } catch (NoSuchFieldException | IllegalAccessException e) {
        fail("Failed to set private field 's': " + e.getMessage());
    }
    CommaSRLSentence actualSentence = commaInstance.getSentence();
    assertSame("getSentence should return the CommaSRLSentence instance set internally", expectedSentence, actualSentence);
}

@Test
public void test4()
{
    String[] tokens = new String[]{ "The", "quick", "brown", "fox", "," };
    TextAnnotation ta = new TextAnnotation("testCorpus", "testText", tokens);
    SpanLabelView shallowParseView = new SpanLabelView(ViewNames.SHALLOW_PARSE, "testGenerator", ta, 1.0);
    Constituent c1 = new Constituent("NP", "testGenerator", ta, 0, 1);
    Constituent c2 = new Constituent("NP", "testGenerator", ta, 1, 2);
    Constituent c3 = new Constituent("NP", "testGenerator", ta, 2, 3);
    Constituent c4 = new Constituent("NP", "testGenerator", ta, 3, 4);
    shallowParseView.addConstituent(c1);
    shallowParseView.addConstituent(c2);
    shallowParseView.addConstituent(c3);
    shallowParseView.addConstituent(c4);
    ta.addView(SHALLOW_PARSE, shallowParseView);
    Comma comma = new Comma();
    comma.s = new Comma.Sentence();
    comma.s.ta = ta;
    comma.commaPosition = 4;
    Constituent result = comma.getChunkToLeftOfComma(2);
    assertNotNull(result);
    assertEquals(1, result.getStartSpan());
    assertEquals(2, result.getEndSpan());
    assertEquals("NP", result.getLabel());
}

@Test
public void test5()
{
    String[] tokens = new String[]{ "He", "went", "home", ",", "then", "slept", "peacefully" };
    TextAnnotation ta = new TextAnnotation("testCorpus", "testId", "", tokens);
    SpanLabelView chunkView = new SpanLabelView(ViewNames.SHALLOW_PARSE, "testGenerator", ta, 1.0);
    chunkView.addConstituent(new Constituent("VP", ViewNames.SHALLOW_PARSE, ta, 4, 5));
    chunkView.addConstituent(new Constituent("VP", ViewNames.SHALLOW_PARSE, ta, 5, 7));
    ta.addView(SHALLOW_PARSE, chunkView);
    Sentence sentence = new Sentence(ta, 0, 0, tokens.length - 1);
    Comma comma = new Comma(sentence, 3);
    Constituent result = comma.getChunkToRightOfComma(1);
    assertNotNull(result);
    assertEquals(4, result.getStartSpan());
    assertEquals(5, result.getEndSpan());
    assertEquals("VP", result.getLabel());
}

@Test
public void test6()
{
    TreeView mockParseView = mock(TreeView.class);
    Constituent mockConstituent = mock(Constituent.class);
    Constituent mockPhrase = mock(Constituent.class);
    Comma commaObj = new Comma();
    try {
        Field field = Comma.class.getDeclaredField("commaPosition");
        field.setAccessible(true);
        field.setInt(commaObj, 2);
    } catch (Exception e) {
        fail("Reflection failed to set commaPosition: " + e.getMessage());
    }
    when(mockConstituent.isConsituentInRange(2, 3)).thenReturn(true);
    when(mockParseView.getConstituents()).thenReturn(Collections.singletonList(mockConstituent));
    when(mockParseView.getParsePhrase(mockConstituent)).thenReturn(mockPhrase);
    Constituent result = commaObj.getCommaConstituentFromTree(mockParseView);
    assertNotNull(result);
    assertEquals(mockPhrase, result);
}

@Test
public void test7()
{
    Field goldField = Comma.class.getDeclaredField("GOLD");
    goldField.setAccessible(true);
    goldField.set(null, true);
    TextAnnotation mockTA = mock(TextAnnotation.class);
    TreeView mockParseView = mock(TreeView.class);
    Constituent mockComma = mock(Constituent.class);
    Constituent expectedConstituent = mock(Constituent.class);
    Comma mockCommaInstance = mock(Comma.class);
    Field sField = Comma.class.getDeclaredField("s");
    sField.setAccessible(true);
    Object mockS = mock(Object.class);
    sField.set(mockCommaInstance, mockS);
    Method getViewMethod = mockS.getClass().getMethod("getView", String.class);
    when(getViewMethod.invoke(mockS, PARSE_GOLD)).thenReturn(mockParseView);
    Comma realComma = new Comma();
    sField.set(realComma, mockS);
    Comma spyComma = spy(realComma);
    doReturn(mockComma).when(spyComma).getCommaConstituentFromTree(mockParseView);
    doReturn(expectedConstituent).when(spyComma).getSiblingToLeft(1, mockComma, mockParseView);
    Constituent result = spyComma.getPhraseToLeftOfComma(1);
    assertEquals(expectedConstituent, result);
}

@Test
public void test8()
{
    Comma commaInstance = new Comma();
    TextAnnotation mockTextAnnotation = mock(TextAnnotation.class);
    TreeView mockTreeView = mock(TreeView.class);
    View mockView = mock(View.class);
    Constituent mockCommaConstituent = mock(Constituent.class);
    Constituent mockParent = mock(Constituent.class);
    Constituent mockSiblingLeft = mock(Constituent.class);
    Field sField = Comma.class.getDeclaredField("s");
    sField.setAccessible(true);
    Object sInstance = sField.get(commaInstance);
    Field taField = sInstance.getClass().getDeclaredField("ta");
    taField.setAccessible(true);
    taField.set(sInstance, mockTextAnnotation);
    Field goldField = Comma.class.getDeclaredField("GOLD");
    goldField.setAccessible(true);
    goldField.setBoolean(null, false);
    when(mockTextAnnotation.getView(CONSTITUENT_PARSER)).thenReturn(mockTreeView);
    Comma spyComma = spy(commaInstance);
    doReturn(mockCommaConstituent).when(spyComma).getCommaConstituentFromTree(mockTreeView);
    mockStatic(TreeView.class);
    when(TreeView.getParent(mockCommaConstituent)).thenReturn(mockParent);
    doReturn(mockSiblingLeft).when(spyComma).getSiblingToLeft(1, mockParent, mockTreeView);
    Constituent result = spyComma.getPhraseToLeftOfParent(1);
    assertSame(mockSiblingLeft, result);
}

@Test
public void test9()
{
    String sentence = "She enjoys painting, especially landscapes.";
    TextAnnotationBuilder builder = new TextAnnotationBuilder() {
        @Override
        public TextAnnotation createTextAnnotation(String corpusId, String docId, String text) {
            try {
                return TextAnnotationUtilities.createFromTokenizedString(corpusId, docId, "She enjoys painting , especially landscapes .");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };
    TextAnnotation ta = builder.createTextAnnotation("testCorpus", "testDoc", sentence);
    View parseView = new StanfordParse().getView(ta);
    ta.addView(PARSE_STANFORD, parseView);
    Comma.GOLD = false;
    Comma comma = new Comma(ta);
    Constituent result = comma.getPhraseToRightOfComma(1);
    assertNotNull(result);
    assertTrue(result.getSurfaceForm().startsWith("especially"));
}

@Test
public void test10()
{
    TextAnnotation ta = StringUtils.createTextAnnotationFromTokens("testCorpus", "testId", new String[][]{ new String[]{ "The", "cat", ",", "which", "was", "black", ",", "sat", "there" } });
    TreeView treeView = new TreeView(ViewNames.PARSE_GOLD, ta);
    Constituent the = new Constituent("DT", "testView", ta, 0, 1);
    Constituent cat = new Constituent("NN", "testView", ta, 1, 2);
    Constituent comma1 = new Constituent(",", "testView", ta, 2, 3);
    Constituent which = new Constituent("WDT", "testView", ta, 3, 4);
    Constituent was = new Constituent("VBD", "testView", ta, 4, 5);
    Constituent black = new Constituent("JJ", "testView", ta, 5, 6);
    Constituent comma2 = new Constituent(",", "testView", ta, 6, 7);
    Constituent sat = new Constituent("VBD", "testView", ta, 7, 8);
    Constituent there = new Constituent("RB", "testView", ta, 8, 9);
    Constituent phrase1 = new Constituent("NP", "testView", ta, 0, 2);
    Constituent phrase2 = new Constituent("SBAR", "testView", ta, 3, 6);
    Constituent phrase3 = new Constituent("VP", "testView", ta, 7, 9);
    Constituent parent = new Constituent("S", "testView", ta, 0, 9);
    treeView.addConstituent(the);
    treeView.addConstituent(cat);
    treeView.addConstituent(comma1);
    treeView.addConstituent(which);
    treeView.addConstituent(was);
    treeView.addConstituent(black);
    treeView.addConstituent(comma2);
    treeView.addConstituent(sat);
    treeView.addConstituent(there);
    treeView.addConstituent(phrase1);
    treeView.addConstituent(phrase2);
    treeView.addConstituent(phrase3);
    treeView.addConstituent(parent);
    treeView.addEdge(parent, phrase1);
    treeView.addEdge(parent, comma1);
    treeView.addEdge(parent, phrase2);
    treeView.addEdge(parent, comma2);
    treeView.addEdge(parent, phrase3);
    Sentence sentence = new Sentence();
    sentence.ta = ta;
    sentence.goldTa = ta;
    Comma.GOLD = true;
    Comma commaInstance = new Comma(sentence) {
        @Override
        protected Constituent getCommaConstituentFromTree(TreeView tree) {
            return comma1;
        }

        @Override
        protected Constituent getSiblingToRight(int distance, Constituent parentConst, TreeView tree) {
            return phrase2;
        }
    };
    Constituent result = commaInstance.getPhraseToRightOfParent(1);
    assertEquals(phrase2, result);
}

@Test
public void test11()
{
    TextAnnotation ta = mock(TextAnnotation.class);
    TreeView treeView = mock(TreeView.class);
    Constituent target = mock(Constituent.class);
    Constituent sibling1 = mock(Constituent.class);
    Constituent sibling2 = mock(Constituent.class);
    IQueryable<Constituent> allSiblings = mock(IQueryable.class);
    IQueryable<Constituent> firstQuery = mock(IQueryable.class);
    IQueryable<Constituent> secondQuery = mock(IQueryable.class);
    when(treeView.where(any())).thenReturn(allSiblings);
    when(allSiblings.where(argThat(( q) -> q.toString().contains("adjacentToBefore")))).thenReturn(firstQuery);
    when(firstQuery.iterator()).thenReturn(Arrays.asList(sibling1).iterator());
    when(allSiblings.where(argThat(( q) -> q.toString().contains("adjacentToBefore")))).thenReturn(secondQuery);
    when(secondQuery.iterator()).thenReturn(Arrays.asList(sibling2).iterator());
    Comma comma = new Comma();
    Constituent result = comma.getSiblingToLeft(1, sibling2, treeView);
    assertEquals(sibling1, result);
}

@Test
public void test12()
{
    Constituent c1 = mock(Constituent.class);
    Constituent c2 = mock(Constituent.class);
    Constituent c3 = mock(Constituent.class);
    TreeView mockParseView = mock(TreeView.class);
    IQueryable<Constituent> siblingSet = mock(IQueryable.class);
    IQueryable<Constituent> afterC1 = mock(IQueryable.class);
    when(mockParseView.where(Queries.isSiblingOf(c1))).thenReturn(siblingSet);
    when(siblingSet.where(Queries.adjacentToAfter(c1))).thenReturn(afterC1);
    Iterator<Constituent> iteratorC2 = Arrays.asList(c2).iterator();
    when(afterC1.iterator()).thenReturn(iteratorC2);
    Comma comma = new Comma();
    Constituent result = comma.getSiblingToRight(1, c1, mockParseView);
    assertEquals("Expected right sibling at distance 1", c2, result);
}

@Test
public void test13()
{
    TextAnnotation goldTextAnnotation = new TextAnnotation("goldCorpus", "doc1", new String[]{ "This", "is", "gold" });
    TextAnnotation predictedTextAnnotation = new TextAnnotation("predictedCorpus", "doc2", new String[]{ "This", "is", "predicted" });
    Sentence sentence = new Sentence();
    sentence.goldTa = goldTextAnnotation;
    sentence.ta = predictedTextAnnotation;
    Comma comma = new Comma();
    comma.s = sentence;
    TextAnnotation result = comma.getTextAnnotation(true);
    assertSame("Expected gold TextAnnotation to be returned when gold=true", goldTextAnnotation, result);
}

@Test
public void test14()
{
    Comma comma = new Comma();
    String[] mockTokens = new String[]{ "The", "quick", "brown", "fox", "jumps", "over", "the", "lazy", "dog" };
    comma.s = new Comma.Sentence();
    comma.s.ta = new Comma.Sentence.MockTextAnnotation(mockTokens);
    comma.commaPosition = 2;
    comma.labels = Arrays.asList("X", "Y");
    String expected = "The quick brown[X,Y] fox jumps over the lazy dog";
    String result = comma.getAnnotatedText();
    assertEquals(expected, result);
}

@Test
public void test15()
{
    Comma comma = new Comma();
    comma.ta = new TextAnnotation() {
        @Override
        public String[] getTokens() {
            return new String[]{ "The", "drone", "struck", "its", "target", ",", "causing", "destruction" };
        }
    };
    comma.commaPosition = 5;
    comma.getBayraktarLabel = new Supplier<String>() {
        public String get() {
            return "CAUSE";
        }
    };
    Comma testComma = new Comma() {
        {
            this.ta = comma.ta;
            this.commaPosition = comma.commaPosition;
        }

        @Override
        public String getBayraktarLabel() {
            return "CAUSE";
        }
    };
    String result = testComma.getBayraktarAnnotatedText();
    assertEquals("The drone struck its target , [CAUSE] causing destruction", result);
}

@Test
public void test16()
{
    Comma commaInstance = Mockito.mock(Comma.class, CALLS_REAL_METHODS);
    BayraktarPatternLabeler mockLabeler = mock(BayraktarPatternLabeler.class);
    try {
        Field field = BayraktarPatternLabeler.class.getDeclaredField("instance");
        field.setAccessible(true);
        field.set(null, mockLabeler);
    } catch (Exception e) {
    }
    String result = commaInstance.getBayraktarLabel();
    assertEquals("Other", result);
}

@Test
public void test17()
{
    Comma.GOLD = true;
    TextAnnotation mockGoldTa = mock(TextAnnotation.class);
    TreeView mockParseView = mock(TreeView.class);
    Comma comma = new Comma();
    comma.s = mock(CommaStructure.class);
    comma.s.goldTa = mockGoldTa;
    when(mockGoldTa.getView(PARSE_GOLD)).thenReturn(mockParseView);
    Comma spyComma = spy(comma);
    String expectedPattern = "NP,VP";
    doReturn(expectedPattern).when(spyComma).getBayraktarPattern(mockParseView);
    String result = spyComma.getBayraktarPattern();
    Assert.assertEquals(expectedPattern, result);
}

@Test
public void test18()
{
    Comma.GOLD = true;
    Comma commaInstance = new Comma();
    TextAnnotation goldTextAnnotation = mock(TextAnnotation.class);
    TreeView mockParseView = mock(TreeView.class);
    when(goldTextAnnotation.getView(PARSE_GOLD)).thenReturn(mockParseView);
    Comma.Sentence s = new Comma.Sentence();
    s.goldTa = goldTextAnnotation;
    commaInstance.s = s;
    Comma spyComma = spy(commaInstance);
    doReturn("NP,VP").when(spyComma).getBayraktarPattern(mockParseView);
    String result = spyComma.getBayraktarPattern();
    assertEquals("NP,VP", result);
}

@Test
public void test19()
{
    Comma comma = new Comma();
    comma.commaPosition = 5;
    comma.s = new SomeStructure();
    comma.s.goldTa = new TextAnnotation();
    comma.s.goldTa.setId("TA123");
    String result = comma.getCommaID();
    assertEquals("5 TA123", result);
}

@Test
public void test20()
{
    List<String> labels = new ArrayList<String>();
    labels.add("COORD");
    labels.add("ADV");
    Comma comma = new Comma(labels);
    assertEquals("COORD", comma.getLabel());
}

@Test
public void test21()
{
    TextAnnotation ta = new TextAnnotation("corpusId", "textId", "John Doe works at IBM.");
    SpanLabelView nerView = new SpanLabelView(ViewNames.NER_CONLL, "testGenerator", ta, 1.0);
    Constituent mainConstituent = new Constituent("O", ViewNames.NER_CONLL, ta, 0, 3);
    Constituent nerConstituent = new Constituent("PERSON", ViewNames.NER_CONLL, ta, 0, 2);
    nerConstituent.setSurfaceForm("John Doe");
    mainConstituent.setSurfaceForm("John Doe works");
    Constituent main = new Constituent("O", ViewNames.NER_CONLL, ta, 0, 3) {
        @Override
        public boolean doesConstituentCover(Constituent c) {
            return (c.getStartSpan() >= this.getStartSpan()) && (c.getEndSpan() <= this.getEndSpan());
        }

        @Override
        public int getNumberOfTokens() {
            return 3;
        }
    };
    Constituent ner = new Constituent("PERSON", ViewNames.NER_CONLL, ta, 0, 2) {
        @Override
        public String getLabel() {
            return "PERSON";
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj;
        }

        @Override
        public int getNumberOfTokens() {
            return 2;
        }
    };
    nerView.addConstituent(ner);
    ta.addView(NER_CONLL, nerView);
    main.setTextAnnotation(ta);
    Comma comma = new Comma();
    String result = comma.getNamedEntityTag(main);
    assertEquals("+PERSON", result);
}

@Test
public void test22()
{
    Comma.NERlexicalise = true;
    Comma.POSlexicalise = true;
    Constituent source = mock(Constituent.class);
    Constituent target = mock(Constituent.class);
    Relation rel = mock(Relation.class);
    TextAnnotation ta = mock(TextAnnotation.class);
    IntPair span = new IntPair(0, 2);
    when(source.getLabel()).thenReturn("NP");
    when(target.getLabel()).thenReturn("VP");
    when(rel.getTarget()).thenReturn(target);
    when(source.getOutgoingRelations()).thenReturn(Arrays.asList(rel));
    when(source.getViewName()).thenReturn(PARSE_GOLD);
    when(source.getTextAnnotation()).thenReturn(ta);
    when(source.getSpan()).thenReturn(span);
    when(ta.getTokenIdFromCharacterOffset(0)).thenReturn(0);
    when(ta.getTokenIdFromCharacterOffset(1)).thenReturn(1);
    when(ta.getTokenIdFromCharacterOffset(2)).thenReturn(2);
    when(ta.getView(POS)).thenReturn(null);
    Comma comma = new Comma() {
        @Override
        protected String getNamedEntityTag(Constituent c) {
            return "PERSON";
        }
    };
    String result = comma.getNotation(source);
    result = result.replaceAll("\\s+", " ").trim();
    assertEquals("NPVP-PERSON- NN VB", result);
}

@Test
public void test23()
{
    Comma comma = new Comma();
    Comma.GOLD = false;
    comma.commaPosition = 2;
    TextAnnotation mockTa = mock(TextAnnotation.class);
    TokenLabelView mockPosView = mock(TokenLabelView.class);
    when(mockPosView.getLabel(1)).thenReturn("DT");
    when(mockTa.getView(POS)).thenReturn(mockPosView);
    comma.s = new Comma.SentenceInstance();
    comma.s.ta = mockTa;
    Comma commaSpy = spy(comma);
    doReturn("the").when(commaSpy).getWordToRight(1);
    String result = commaSpy.getPOSToLeft(1);
    assertEquals("DT-the", result);
}

@Test
public void test24()
{
    Comma.GOLD = true;
    Comma comma = new Comma();
    comma.commaPosition = 3;
    TextAnnotation goldTa = mock(TextAnnotation.class);
    TokenLabelView posView = mock(TokenLabelView.class);
    when(goldTa.getView(POS)).thenReturn(posView);
    when(posView.getLabel(4)).thenReturn("DT");
    Comma.Sentence s = comma.new Sentence();
    s.goldTa = goldTa;
    comma.s = s;
    Comma spyComma = spy(comma);
    doReturn("the").when(spyComma).getWordToRight(1);
    String result = spyComma.getPOSToRight(1);
    assertEquals("DT-the", result);
}

@Test
public void test25()
{
    Comma.NERlexicalise = true;
    Comma.POSlexicalise = true;
    TextAnnotation ta = new TextAnnotation("dummyCorpus", "dummyTextId", "This is a test.");
    IntPair span = new IntPair(0, 2);
    Constituent c = new Constituent("NP-ARG0", "dummyView", ta, 0, 2);
    String result = new Comma().getStrippedNotation(c);
    assertEquals("NP-PERSON- NN VB", result);
}

@Test
public void test26()
{
    TextAnnotation ta = new TextAnnotation("dummyCorpus", "dummyId", new String[]{ "The", "quick", "brown", "fox", "," });
    Sentence sentence = new Sentence(ta);
    Comma comma = new Comma(sentence, 4);
    String result = comma.getWordToLeft(1);
    assertEquals("fox", result);
}

@Test
public void test27()
{
    String[] tokens = new String[]{ "The", "quick", "brown", ",", "fox", "jumps" };
    TextAnnotation ta = new TextAnnotation("dummyCorpusId", "dummyId", "The quick brown , fox jumps");
    ta.initializeTokenizedText(tokens);
    Sentence sentence = new Sentence(ta);
    Comma comma = new Comma();
    comma.s = sentence;
    comma.commaPosition = 3;
    String result = comma.getWordToRight(1);
    assertEquals("fox", result);
}

@Test
public void test28()
{
    TextAnnotation ta = new TextAnnotation("source", "id", new String[][]{ new String[]{ "The", "quick", "brown", "fox", ",", "jumps", "over", "the", "lazy", "dog" } });
    TreeView dependencyView = new TreeView(ViewNames.DEPENDENCY_STANFORD, "testAnnotator", ta, 1.0);
    int commaIndex = 4;
    Constituent leftConstituent1 = new Constituent("dep", ViewNames.DEPENDENCY_STANFORD, ta, 1, 2);
    Constituent leftConstituent2 = new Constituent("dep", ViewNames.DEPENDENCY_STANFORD, ta, 2, 3);
    Constituent rightConstituent1 = new Constituent("dep", ViewNames.DEPENDENCY_STANFORD, ta, 5, 6);
    Constituent rightConstituent2 = new Constituent("dep", ViewNames.DEPENDENCY_STANFORD, ta, 6, 7);
    Relation rel1 = new Relation("nsubj", leftConstituent1, rightConstituent1, 1.0);
    Relation rel2 = new Relation("advmod", leftConstituent2, rightConstituent2, 1.0);
    leftConstituent1.addRelation(rel1);
    leftConstituent2.addRelation(rel2);
    dependencyView.addConstituent(leftConstituent1);
    dependencyView.addConstituent(leftConstituent2);
    dependencyView.addConstituent(rightConstituent1);
    dependencyView.addConstituent(rightConstituent2);
    ta.addView(DEPENDENCY_STANFORD, dependencyView);
    Comma comma = new Comma();
    Field taField = Comma.class.getDeclaredField("ta");
    Field commaPosField = Comma.class.getDeclaredField("commaPosition");
    taField.setAccessible(true);
    commaPosField.setAccessible(true);
    taField.set(comma, ta);
    commaPosField.setInt(comma, commaIndex);
    String[] dependencies = comma.getLeftToRightDependencies();
    assertArrayEquals(new String[]{ "nsubj", "advmod" }, dependencies);
}

@Test
public void test29()
{
    Relation mockRelation = mock(Relation.class);
    when(mockRelation.getRelationName()).thenReturn("nsubj");
    Constituent mockSource = mock(Constituent.class);
    when(mockSource.getStartSpan()).thenReturn(5);
    when(mockRelation.getSource()).thenReturn(mockSource);
    Constituent mockConstituentLeft = mock(Constituent.class);
    when(mockConstituentLeft.getIncomingRelations()).thenReturn(Collections.singletonList(mockRelation));
    TreeView mockTreeView = mock(TreeView.class);
    when(mockTreeView.getConstituentsCoveringSpan(0, 3)).thenReturn(Collections.singletonList(mockConstituentLeft));
    TextAnnotation mockTA = mock(TextAnnotation.class);
    when(mockTA.getView(DEPENDENCY_STANFORD)).thenReturn(mockTreeView);
    Comma comma = new Comma();
    comma.ta = mockTA;
    comma.commaPosition = 3;
    String[] result = comma.getRightToLeftDependencies();
    assertArrayEquals(new String[]{ "nsubj" }, result);
}

@Test
public void test30()
{
    Constituent predicate = mock(Constituent.class);
    Constituent target = mock(Constituent.class);
    Relation relation = mock(Relation.class);
    when(relation.getTarget()).thenReturn(target);
    when(relation.getSource()).thenReturn(predicate);
    when(target.getStartSpan()).thenReturn(5);
    when(target.getEndSpan()).thenReturn(10);
    PredicateArgumentView srlVerbView = mock(PredicateArgumentView.class);
    when(srlVerbView.getPredicates()).thenReturn(Collections.singletonList(predicate));
    when(srlVerbView.getArguments(predicate)).thenReturn(Collections.singletonList(relation));
    when(srlVerbView.getPredicateLemma(predicate)).thenReturn("run");
    when(relation.getRelationName()).thenReturn("ARG1");
    PredicateArgumentView srlNomView = mock(PredicateArgumentView.class);
    when(srlNomView.getPredicates()).thenReturn(Collections.emptyList());
    PredicateArgumentView srlPrepView = mock(PredicateArgumentView.class);
    when(srlPrepView.getPredicates()).thenReturn(Collections.emptyList());
    TextAnnotation ta = mock(TextAnnotation.class);
    when(ta.getView(SRL_VERB)).thenReturn(srlVerbView);
    when(ta.getView(SRL_NOM)).thenReturn(srlNomView);
    when(ta.getView(SRL_PREP)).thenReturn(srlPrepView);
    Comma comma = new Comma();
    comma.commaPosition = 5;
    comma.s = mock(Sentence.class);
    comma.s.ta = ta;
    comma.s.goldTa = ta;
    try {
        Field field = Comma.class.getDeclaredField("GOLD");
        field.setAccessible(true);
        field.setBoolean(null, false);
    } catch (Exception e) {
        fail("Failed to set GOLD field via reflection: " + e.getMessage());
    }
    List<String> result = comma.getContainingSRLs();
    assertEquals(1, result.size());
    assertTrue(result.contains("runARG1"));
}

@Test
public void test31()
{
    Comma comma = new Comma();
    List<String> expectedLabels = Arrays.asList("Label1", "Label2", "Label3");
    comma.labels = expectedLabels;
    List<String> actualLabels = comma.getLabels();
    assertEquals(expectedLabels, actualLabels);
}

@Test
public void test32()
{
    TextAnnotation ta = new TextAnnotation("corpusId", "textId", new String[]{ "This", ",", "is", ",", "a", "test", "." });
    TreeView treeView = new TreeView(ViewNames.PARSE_GOLD, "testAnnotator", ta, 1.0);
    ta.addView(PARSE_GOLD, treeView);
    Constituent comma1Constituent = new Constituent("PUNC", ViewNames.PARSE_GOLD, ta, 1, 2);
    Constituent comma2Constituent = new Constituent("PUNC", ViewNames.PARSE_GOLD, ta, 3, 4);
    Constituent parent = new Constituent("NP", ViewNames.PARSE_GOLD, ta, 0, 6);
    comma1Constituent.setIncomingRelations(new ArrayList<>());
    comma2Constituent.setIncomingRelations(new ArrayList<>());
    treeView.addConstituent(parent);
    treeView.addConstituent(comma1Constituent);
    treeView.addConstituent(comma2Constituent);
    treeView.addRelation(new Relation("", parent, comma1Constituent, 1.0F));
    treeView.addRelation(new Relation("", parent, comma2Constituent, 1.0F));
    Comma comma1 = new Comma();
    Comma comma2 = new Comma();
    Comma.Sentence sentence = new Comma.Sentence();
    List<Comma> commaList = new ArrayList<>();
    commaList.add(comma1);
    commaList.add(comma2);
    sentence.ta = ta;
    sentence.goldTa = ta;
    sentence.getCommas = () -> commaList;
    Field sField = Comma.class.getDeclaredField("s");
    sField.setAccessible(true);
    sField.set(comma1, sentence);
    sField.set(comma2, sentence);
    Comma stubComma1 = new Comma() {
        @Override
        public Constituent getCommaConstituentFromTree(TreeView view) {
            return comma1Constituent;
        }
    };
    Comma stubComma2 = new Comma() {
        @Override
        public Constituent getCommaConstituentFromTree(TreeView view) {
            return comma2Constituent;
        }
    };
    sField.set(stubComma1, sentence);
    sField.set(stubComma2, sentence);
    commaList.clear();
    commaList.add(stubComma1);
    commaList.add(stubComma2);
    List<Comma> siblings = stubComma1.getSiblingCommas();
    assertEquals(1, siblings.size());
    assertTrue(siblings.contains(stubComma2));
}
