import org.junit.Test;
import static org.junit.Assert.*;

public class GeneratedTest {

@Test
public void test1()
{
    Comma.GOLD = false;
    TextAnnotation ta = new TextAnnotation("", "", new String[][]{ new String[]{ "This", "is", "a", "test" } });
    TreeView mockTreeView = new TreeView(Comma.CONSTITUENT_PARSER, ta);
    Constituent parent = new Constituent("NP", "dummy", ta, 0, 1);
    Constituent comma1 = new Constituent(",", "dummy", ta, 0, 0);
    Constituent comma2 = new Constituent(",", "dummy", ta, 1, 1);
    mockTreeView.addConstituent(parent);
    mockTreeView.addConstituent(comma1);
    mockTreeView.addConstituent(comma2);
    mockTreeView.addConstituent(parent);
    mockTreeView.addConstituent(comma1);
    mockTreeView.addConstituent(comma2);
    mockTreeView.addRelation(parent, comma1);
    mockTreeView.addRelation(parent, comma2);
    ta.addView(CONSTITUENT_PARSER, mockTreeView);
    Comma commaObj1 = new Comma();
    Comma commaObj2 = new Comma();
    commaObj1.s = new Comma.Sentence();
    commaObj2.s = new Comma.Sentence();
    commaObj1.s.ta = ta;
    commaObj2.s.ta = ta;
    commaObj1.getCommaConstituentFromTree = (TreeView view) -> comma1;
    commaObj2.getCommaConstituentFromTree = (TreeView view) -> comma2;
    boolean result = commaObj1.isSibling(commaObj2);
    assertTrue(result);
}

@Test
public void test2()
{
    Comma comma1 = new Comma();
    comma1.commaPosition = 15;
    Comma comma2 = new Comma();
    comma2.commaPosition = 10;
    Comma comma3 = new Comma();
    comma3.commaPosition = 20;
    Comma testComma = new Comma() {
        @Override
        public List<Comma> getSiblingCommas() {
            return Arrays.asList(comma1, comma2, comma3);
        }
    };
    Comma result = testComma.getSiblingCommaHead();
    assertSame(comma2, result);
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
        fail("Failed to set private field 's': " + e.getMessage());
    }
    CommaSRLSentence actualSentence = comma.getSentence();
    assertSame("getSentence should return the same instance assigned to field 's'", expectedSentence, actualSentence);
}

@Test
public void test4()
{
    TextAnnotation ta = new TextAnnotation("corpus", "id", new String[]{ "The", "quick", "brown", "fox", "," });
    SpanLabelView shallowParseView = new SpanLabelView(ViewNames.SHALLOW_PARSE, "testGenerator", ta, 1.0);
    Constituent c1 = new Constituent("NP", ViewNames.SHALLOW_PARSE, ta, 0, 1);
    Constituent c2 = new Constituent("NP", ViewNames.SHALLOW_PARSE, ta, 1, 2);
    Constituent c3 = new Constituent("ADJP", ViewNames.SHALLOW_PARSE, ta, 2, 3);
    Constituent c4 = new Constituent("NP", ViewNames.SHALLOW_PARSE, ta, 3, 4);
    shallowParseView.addConstituent(c1);
    shallowParseView.addConstituent(c2);
    shallowParseView.addConstituent(c3);
    shallowParseView.addConstituent(c4);
    ta.addView(SHALLOW_PARSE, shallowParseView);
    Comma comma = new Comma();
    comma.s = new Object() {
        TextAnnotation ta = ta;
    };
    comma.commaPosition = 4;
    Constituent result = comma.getChunkToLeftOfComma(2);
    assertNotNull(result);
    assertEquals("NP", result.getLabel());
    assertEquals(1, result.getStartSpan());
    assertEquals(2, result.getEndSpan());
}

@Test
public void test5()
{
    TextAnnotation ta = new TextAnnotation("dummyCorpus", "dummyId", new String[]{ "The", "cat", ",", "sat", "on", "the", "mat" });
    SpanLabelView chunkView = new SpanLabelView(ViewNames.SHALLOW_PARSE, "mockGenerator", ta, 1.0);
    Constituent chunk1 = new Constituent("NP", ViewNames.SHALLOW_PARSE, ta, 3, 4);
    Constituent chunk2 = new Constituent("PP", ViewNames.SHALLOW_PARSE, ta, 4, 5);
    Constituent chunk3 = new Constituent("NP", ViewNames.SHALLOW_PARSE, ta, 5, 7);
    chunkView.addConstituent(chunk1);
    chunkView.addConstituent(chunk2);
    chunkView.addConstituent(chunk3);
    ta.addView(SHALLOW_PARSE, chunkView);
    Sentence s = new Sentence(ta);
    Comma comma = new Comma(s, 2);
    Constituent result = comma.getChunkToRightOfComma(2);
    assertNotNull(result);
    assertEquals(4, result.getStartSpan());
    assertEquals(5, result.getEndSpan());
    assertEquals("PP", result.getLabel());
}

@Test
public void test6()
{
    TextAnnotation ta = new TextAnnotation("testCorpus", "testId", "This is, a test.");
    TreeView treeView = new TreeView("PARSE_VIEW", "testGenerator", ta, 1.0);
    int commaPosition = 2;
    Constituent cons1 = new Constituent("NP", "PARSE_VIEW", ta, 0, 1);
    Constituent cons2 = new Constituent("COMMA", "PARSE_VIEW", ta, commaPosition, commaPosition + 1);
    Constituent cons3 = new Constituent("VP", "PARSE_VIEW", ta, 3, 4);
    treeView.addConstituent(cons1);
    treeView.addConstituent(cons2);
    treeView.addConstituent(cons3);
    TreeView treeViewSpy = new TreeView("PARSE_VIEW", "testSpy", ta, 1.0) {
        @Override
        public List<Constituent> getConstituents() {
            return Arrays.asList(cons1, cons2, cons3);
        }

        @Override
        public Constituent getParsePhrase(Constituent c) throws Exception {
            return c;
        }
    };
    Comma comma = new Comma(commaPosition);
    Constituent result = comma.getCommaConstituentFromTree(treeViewSpy);
    assertNotNull(result);
    assertEquals(commaPosition, result.getStartSpan());
    assertEquals(commaPosition + 1, result.getEndSpan());
    assertEquals("COMMA", result.getLabel());
}

@Test
public void test7()
{
    TextAnnotation mockTa = mock(TextAnnotation.class);
    TreeView mockParseView = mock(TreeView.class);
    Constituent mockCommaConstituent = mock(Constituent.class);
    Constituent expectedLeftSibling = mock(Constituent.class);
    Comma.Sentence mockSentence = mock(Sentence.class);
    mockSentence.ta = mockTa;
    mockSentence.goldTa = mockTa;
    Comma comma = spy(new Comma(mockSentence));
    doReturn(mockParseView).when(mockTa).getView(CONSTITUENT_PARSER);
    doReturn(mockCommaConstituent).when(comma).getCommaConstituentFromTree(mockParseView);
    doReturn(expectedLeftSibling).when(comma).getSiblingToLeft(1, mockCommaConstituent, mockParseView);
    Comma.GOLD = false;
    Constituent result = comma.getPhraseToLeftOfComma(1);
    assertSame(expectedLeftSibling, result);
}

@Test
public void test8()
{
    Comma comma = new Comma();
    Comma.GOLD = true;
    TextAnnotation ta = new TextAnnotation("testCorpus", "testId", new String[]{ "This", "is", "a", "test", "," });
    TreeView goldParseView = new TreeView(ViewNames.PARSE_GOLD, ta);
    Constituent parent = new Constituent("NP", ViewNames.PARSE_GOLD, ta, 2, 4);
    Constituent leftSibling = new Constituent("DT", ViewNames.PARSE_GOLD, ta, 1, 2);
    Constituent commaConstituent = new Constituent(",", ViewNames.PARSE_GOLD, ta, 4, 5);
    goldParseView.addConstituent(leftSibling);
    goldParseView.addConstituent(parent);
    goldParseView.addConstituent(commaConstituent);
    goldParseView.addConstituent(parent);
    comma.s = new Comma.Sentence();
    comma.s.goldTa = ta;
    ta.addView(PARSE_GOLD, goldParseView);
    Comma testComma = new Comma() {
        @Override
        protected Constituent getCommaConstituentFromTree(TreeView view) {
            return commaConstituent;
        }
    };
    testComma.s = comma.s;
    TreeView testView = ((TreeView) (ta.getView(PARSE_GOLD)));
    Constituent result = testComma.getPhraseToLeftOfParent(1);
    assertNotNull(result);
    assertEquals("DT", result.getLabel());
    assertEquals(1, result.getStartSpan());
    assertEquals(2, result.getEndSpan());
}

@Test
public void test9()
{
    Constituent mockCommaConstituent = mock(Constituent.class);
    Constituent expectedConstituent = mock(Constituent.class);
    TreeView mockTreeView = mock(TreeView.class);
    TextAnnotation mockTextAnnotation = mock(TextAnnotation.class);
    when(mockTextAnnotation.getView(PARSE_GOLD)).thenReturn(mockTreeView);
    when(mockTextAnnotation.getView(CONSTITUENT_PARSER)).thenReturn(mockTreeView);
    Comma comma = new Comma("testId", mockTextAnnotation);
    Comma.GOLD = true;
    Field field = Comma.class.getDeclaredField("s");
    field.setAccessible(true);
    Comma.Singleton s = comma.new Singleton();
    Field taField = s.getClass().getDeclaredField("goldTa");
    taField.setAccessible(true);
    taField.set(s, mockTextAnnotation);
    field.set(comma, s);
    Comma spyComma = spy(comma);
    doReturn(mockCommaConstituent).when(spyComma).getCommaConstituentFromTree(mockTreeView);
    doReturn(expectedConstituent).when(spyComma).getSiblingToRight(1, mockCommaConstituent, mockTreeView);
    Constituent result = spyComma.getPhraseToRightOfComma(1);
    assertSame(expectedConstituent, result);
}

@Test
public void test10()
{
    Comma comma = new Comma();
    Comma.GOLD = true;
    TreeView mockParseView = mock(TreeView.class);
    Constituent mockCommaConstituent = mock(Constituent.class);
    Constituent mockParentConstituent = mock(Constituent.class);
    Constituent mockExpectedSibling = mock(Constituent.class);
    TextAnnotation mockTextAnnotation = mock(TextAnnotation.class);
    TreeView mockGoldView = mock(TreeView.class);
    comma.s = mock(SentenceInstance.class);
    comma.s.goldTa = mockTextAnnotation;
    when(mockTextAnnotation.getView(PARSE_GOLD)).thenReturn(mockGoldView);
    when(comma.getCommaConstituentFromTree(mockGoldView)).thenReturn(mockCommaConstituent);
    when(TreeView.getParent(mockCommaConstituent)).thenReturn(mockParentConstituent);
    when(comma.getSiblingToRight(1, mockParentConstituent, mockGoldView)).thenReturn(mockExpectedSibling);
    Constituent result = comma.getPhraseToRightOfParent(1);
    assertEquals(mockExpectedSibling, result);
}

@Test
public void test11()
{
    String[] tokens = new String[]{ "The", "quick", "brown", "fox" };
    TextAnnotationBuilder tab = new TextAnnotationBuilder();
    TextAnnotation ta = tab.createTextAnnotation("test", "1", tokens);
    TreeView parseView = new TreeView(ViewNames.PARSE_STANFORD, "test", ta, 1.0);
    Constituent c0 = new Constituent("NP", ViewNames.PARSE_STANFORD, ta, 0, 1);
    Constituent c1 = new Constituent("NP", ViewNames.PARSE_STANFORD, ta, 1, 2);
    Constituent c2 = new Constituent("NP", ViewNames.PARSE_STANFORD, ta, 2, 3);
    parseView.addConstituent(c0);
    parseView.addConstituent(c1);
    parseView.addConstituent(c2);
    c0.addAttribute("parentId", "P1");
    c1.addAttribute("parentId", "P1");
    c2.addAttribute("parentId", "P1");
    Comma comma = new Comma();
    Constituent result = comma.getSiblingToLeft(1, c2, parseView);
    assertEquals(c1, result);
}

@Test
public void test12()
{
    Constituent c = mock(Constituent.class);
    Constituent rightSibling1 = mock(Constituent.class);
    Constituent rightSibling2 = mock(Constituent.class);
    TreeView parseView = mock(TreeView.class);
    IQueryable<Constituent> mockSiblings = mock(IQueryable.class);
    when(parseView.where(any())).thenReturn(mockSiblings);
    IQueryable<Constituent> mockAdjacent1 = mock(IQueryable.class);
    Iterator<Constituent> iter1 = Arrays.asList(rightSibling1).iterator();
    when(mockSiblings.where(any())).thenReturn(mockAdjacent1);
    when(mockAdjacent1.iterator()).thenReturn(iter1);
    Comma comma = new Comma();
    Constituent result = comma.getSiblingToRight(1, c, parseView);
    assertEquals(rightSibling1, result);
}

@Test
public void test13()
{
    TextAnnotation goldTa = new TextAnnotation("goldCorpus", "goldId", "This is a gold annotation.");
    TextAnnotation ta = new TextAnnotation("corpus", "id", "This is a regular annotation.");
    CommaSentence sentence = new CommaSentence();
    sentence.goldTa = goldTa;
    sentence.ta = ta;
    Comma comma = new Comma();
    comma.s = sentence;
    TextAnnotation result = comma.getTextAnnotation(true);
    assertEquals("Expected the gold TextAnnotation to be returned", goldTa, result);
}

@Test
public void test14()
{
    List<String[]> sentences = Arrays.asList(new String[]{ "This", "is", "a", "test", ",", "case" });
    TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens(sentences);
    Comma comma = new Comma();
    comma.s = new Comma.SentenceWrapper();
    comma.s.ta = ta;
    comma.commaPosition = 4;
    comma.labels = Arrays.asList("label1", "label2");
    String expected = "This is a test , [label1,label2] case";
    assertEquals(expected, comma.getAnnotatedText());
}

@Test
public void test15()
{
    Comma comma = new Comma();
    comma.ta = new TextAnnotation() {
        @Override
        public String[] getTokens() {
            return new String[]{ "The", "drone", "struck", ",", "killing", "targets", "." };
        }
    };
    comma.commaPosition = 3;
    Comma commaWithLabel = new Comma() {
        {
            ta = comma.ta;
            commaPosition = comma.commaPosition;
        }

        @Override
        protected String getBayraktarLabel() {
            return "Action";
        }
    };
    String expected = "The drone struck , [Action] killing targets .";
    String actual = commaWithLabel.getBayraktarAnnotatedText();
    assertEquals(expected, actual);
}

@Test
public void test16()
{
    @Override
    String bayraktarLabel = null;
    if (bayraktarLabel == null) {
        return "Other";
    } else {
        return bayraktarLabel;
    }
}

@Test
public void test17()
{
    Comma.GOLD = false;
    Comma commaInstance = new Comma();
    TreeView mockTreeView = Mockito.mock(TreeView.class);
    TextAnnotation mockTextAnnotation = Mockito.mock(TextAnnotation.class);
    Mockito.when(mockTextAnnotation.getView(CONSTITUENT_PARSER)).thenReturn(mockTreeView);
    commaInstance.s = new Comma.Sentence();
    commaInstance.s.ta = mockTextAnnotation;
    String expectedPattern = "NP , VP";
    Comma spyComma = Mockito.spy(commaInstance);
    Mockito.doReturn(expectedPattern).when(spyComma).getBayraktarPattern(mockTreeView);
    String result = spyComma.getBayraktarPattern();
    Assert.assertEquals(expectedPattern, result);
}

@Test
public void test18()
{
    Comma.GOLD = true;
    Comma comma = new Comma();
    TreeView mockTreeView = mock(TreeView.class);
    TextAnnotation mockGoldTA = mock(TextAnnotation.class);
    Field fieldS = Comma.class.getDeclaredField("s");
    fieldS.setAccessible(true);
    Object sInstance = fieldS.get(comma);
    Field fieldGoldTa = sInstance.getClass().getDeclaredField("goldTa");
    fieldGoldTa.setAccessible(true);
    fieldGoldTa.set(sInstance, mockGoldTA);
    when(mockGoldTA.getView(PARSE_GOLD)).thenReturn(mockTreeView);
    Comma spyComma = spy(comma);
    String expectedPattern = "NP , S";
    doReturn(expectedPattern).when(spyComma).getBayraktarPattern(mockTreeView);
    String result = spyComma.getBayraktarPattern();
    assertEquals(expectedPattern, result);
}

@Test
public void test19()
{
    Comma comma = new Comma();
    comma.commaPosition = 5;
    comma.s = new Comma.SentenceWrapper();
    comma.s.goldTa = new TextAnnotation("corpus", "id", "text");
    String expected = "5 id";
    String actual = comma.getCommaID();
    assertEquals(expected, actual);
}

@Test
public void test20()
{
    Comma comma = new Comma();
    List<String> testLabels = Arrays.asList("Introductory", "Parenthetical", "Serial");
    comma.labels = testLabels;
    String result = comma.getLabel();
    assertEquals("Introductory", result);
}

@Test
public void test21()
{
    String[] tokens = new String[]{ "Barack", "Obama", "visited", "Paris" };
    TextAnnotation ta = new TextAnnotation("corpusId", "textId", "Barack Obama visited Paris", tokens);
    SpanLabelView nerView = new SpanLabelView(ViewNames.NER_CONLL, "NERAnnotator", ta, 1.0);
    Constituent namedEntity = new Constituent("PERSON", 1.0, ta, 0, 2);
    nerView.addConstituent(namedEntity);
    ta.addView(NER_CONLL, nerView);
    Constituent c = new Constituent("", 1.0, ta, 0, 2);
    Comma comma = new Comma();
    comma.ta = ta;
    String result = comma.getNamedEntityTag(c);
    assertEquals("+PERSON", result);
}

@Test
public void test22()
{
    Comma.NERlexicalise = true;
    Comma.POSlexicalise = true;
    String[] tokens = new String[]{ "The", "quick", "brown", "fox" };
    TextAnnotation ta = new TextAnnotation("testCorpus", "testId", "", tokens, new int[tokens.length + 1]);
    Constituent source = new Constituent("NP", ViewNames.PARSE_GOLD, ta, 1, 3);
    source.addAttribute("NER", "ANIMAL");
    Constituent target = new Constituent("VP", ViewNames.PARSE_GOLD, ta, 3, 4);
    Relation rel = new Relation("relType", source, target);
    source.addRelation(rel);
    Comma comma = new Comma();
    String result = comma.getNotation(source);
    assertEquals("NPVP-ANIMAL- quick brown", result);
}

@Test
public void test23()
{
    TextAnnotation ta = new TextAnnotation("testCorpus", "testId", new String[]{ "A", "comma", "the" });
    TokenLabelView posView = new TokenLabelView(ViewNames.POS, "testAnnotator", ta, 1.0);
    posView.addTokenLabel(0, "DT", 1.0);
    posView.addTokenLabel(1, ",", 1.0);
    posView.addTokenLabel(2, "DT", 1.0);
    ta.addView(POS, posView);
    Comma comma = new Comma();
    comma.commaPosition = 1;
    Comma.GOLD = false;
    comma.s = new Comma.SentenceDataSet();
    comma.s.ta = ta;
    comma.getWordToRight = (int dist) -> "the";
    String result = comma.getPOSToLeft(1);
    assertEquals("DT-the", result);
}

@Test
public void test24()
{
    TokenLabelView mockPosView = mock(TokenLabelView.class);
    when(mockPosView.getLabel(5)).thenReturn("DT");
    TextAnnotation mockTextAnnotation = mock(TextAnnotation.class);
    when(mockTextAnnotation.getView(POS)).thenReturn(mockPosView);
    Comma.Sentence sentence = new Comma.Sentence();
    sentence.ta = mockTextAnnotation;
    sentence.goldTa = mockTextAnnotation;
    Comma.GOLD = true;
    Comma comma = new Comma();
    comma.s = sentence;
    comma.commaPosition = 4;
    Comma testComma = new Comma() {
        {
            s = sentence;
            commaPosition = 4;
        }

        @Override
        protected String getWordToRight(int distance) {
            return "the";
        }
    };
    String result = testComma.getPOSToRight(1);
    assertEquals("DT-the", result);
}

@Test
public void test25()
{
    Comma.NERlexicalise = true;
    Comma.POSlexicalise = true;
    Constituent mockConstituent = mock(Constituent.class);
    when(mockConstituent.getLabel()).thenReturn("NP-SBJ");
    Comma commaInstance = new Comma() {
        @Override
        protected String getNamedEntityTag(Constituent c) {
            return "PERSON";
        }
    };
    IntPair mockSpan = new IntPair(0, 2);
    when(mockConstituent.getSpan()).thenReturn(mockSpan);
    TextAnnotation mockTextAnnotation = mock(TextAnnotation.class);
    when(mockConstituent.getTextAnnotation()).thenReturn(mockTextAnnotation);
    when(POSUtils.getPOS(mockTextAnnotation, 0)).thenReturn("NNP");
    when(POSUtils.getPOS(mockTextAnnotation, 1)).thenReturn("NNP");
    String result = commaInstance.getStrippedNotation(mockConstituent);
    assertEquals("NP-PERSON- NNP NNP", result);
}

@Test
public void test26()
{
    String text = "The quick brown, fox jumps over the lazy dog.";
    TextAnnotation ta = TextAnnotationUtilities.createFromTokenizedString("DUMMY", "The quick brown , fox jumps over the lazy dog .");
    Sentence sentence = ta.getSentence(0);
    int commaIndex = 3;
    Comma comma = new Comma(ta, sentence, commaIndex);
    String result = comma.getWordToLeft(1);
    assertEquals("brown", result);
}

@Test
public void test27()
{
    Comma comma = new Comma();
    Sentence sentence = new Sentence();
    TextAnnotation ta = new TextAnnotation("view", "id", new String[]{ "The", "quick", "brown", "fox", ",", "jumps", "over", "the", "lazy", "dog" });
    sentence.ta = ta;
    comma.s = sentence;
    comma.commaPosition = 4;
    String result = comma.getWordToRight(1);
    assertEquals("jumps", result);
}

@Test
public void test28()
{
    Comma comma = Mockito.mock(Comma.class);
    Mockito.when(comma.getChunkToLeftOfComma(2)).thenReturn("L2");
    Mockito.when(comma.getChunkToLeftOfComma(1)).thenReturn("L1");
    Mockito.when(comma.getChunkToRightOfComma(1)).thenReturn("R1");
    Mockito.when(comma.getChunkToRightOfComma(2)).thenReturn("R2");
    Mockito.when(comma.getNotation("L2")).thenReturn("NP");
    Mockito.when(comma.getNotation("L1")).thenReturn("VP");
    Mockito.when(comma.getNotation("R1")).thenReturn("PP");
    Mockito.when(comma.getNotation("R2")).thenReturn("ADJP");
    Mockito.when(comma.getChunkNgrams()).thenCallRealMethod();
    String[] result = comma.getChunkNgrams();
    String[] expected = new String[]{ "NP", "VP", "PP", "ADJP", "NP_VP", "VP_PP", "PP_ADJP" };
    assertArrayEquals(expected, result);
}

@Test
public void test29()
{
    Comma comma = new Comma();
    Field field = Comma.class.getDeclaredField("commaPosition");
    field.setAccessible(true);
    field.setInt(comma, 2);
    TextAnnotation ta = mock(TextAnnotation.class);
    TreeView dependencyView = mock(TreeView.class);
    when(ta.getView(DEPENDENCY_STANFORD)).thenReturn(dependencyView);
    Field taField = Comma.class.getDeclaredField("s");
    taField.setAccessible(true);
    Object sentenceWrapper = new Object() {
        public TextAnnotation getTa() {
            return ta;
        }
    };
    taField.set(comma, sentenceWrapper);
    Constituent leftConstituent = mock(Constituent.class);
    Relation relation1 = mock(Relation.class);
    Constituent target1 = mock(Constituent.class);
    when(relation1.getTarget()).thenReturn(target1);
    when(relation1.getRelationName()).thenReturn("nsubj");
    when(target1.getStartSpan()).thenReturn(3);
    when(leftConstituent.getOutgoingRelations()).thenReturn(Arrays.asList(relation1));
    when(dependencyView.getConstituentsCoveringSpan(0, 2)).thenReturn(Arrays.asList(leftConstituent));
    String[] result = comma.getLeftToRightDependencies();
    assertArrayEquals(new String[]{ "nsubj" }, result);
}

@Test
public void test30()
{
    Constituent rightConstituent = mock(Constituent.class);
    when(rightConstituent.getStartSpan()).thenReturn(5);
    Relation rel = mock(Relation.class);
    when(rel.getSource()).thenReturn(rightConstituent);
    when(rel.getRelationName()).thenReturn("nsubj");
    Constituent leftConstituent = mock(Constituent.class);
    when(leftConstituent.getIncomingRelations()).thenReturn(Collections.singletonList(rel));
    TreeView depView = mock(TreeView.class);
    when(depView.getConstituentsCoveringSpan(0, 3)).thenReturn(Collections.singletonList(leftConstituent));
    TextAnnotation ta = mock(TextAnnotation.class);
    when(ta.getView(DEPENDENCY_STANFORD)).thenReturn(depView);
    Comma comma = new Comma();
    comma.ta = ta;
    comma.commaPosition = 3;
    String[] result = comma.getRightToLeftDependencies();
    assertArrayEquals(new String[]{ "nsubj" }, result);
}

@Test
public void test31()
{
    Comma comma = new Comma();
    comma.commaPosition = 3;
    Comma.GOLD = false;
    TextAnnotation taMock = mock(TextAnnotation.class);
    comma.s = mock(Sentence.class);
    comma.s.ta = taMock;
    PredicateArgumentView srlVerbView = mock(PredicateArgumentView.class);
    PredicateArgumentView srlNomView = mock(PredicateArgumentView.class);
    PredicateArgumentView srlPrepView = mock(PredicateArgumentView.class);
    when(taMock.getView(SRL_VERB)).thenReturn(srlVerbView);
    when(taMock.getView(SRL_NOM)).thenReturn(srlNomView);
    when(taMock.getView(SRL_PREP)).thenReturn(srlPrepView);
    Constituent pred = mock(Constituent.class);
    Relation rel = mock(Relation.class);
    Constituent target = mock(Constituent.class);
    when(srlVerbView.getPredicates()).thenReturn(Collections.singletonList(pred));
    when(srlVerbView.getArguments(pred)).thenReturn(Collections.singletonList(rel));
    when(rel.getTarget()).thenReturn(target);
    when(target.getStartSpan()).thenReturn(3);
    when(target.getEndSpan()).thenReturn(5);
    when(rel.getSource()).thenReturn(pred);
    when(srlVerbView.getPredicateLemma(pred)).thenReturn("run");
    when(rel.getRelationName()).thenReturn("ARG0");
    when(srlNomView.getPredicates()).thenReturn(Collections.emptyList());
    when(srlPrepView.getPredicates()).thenReturn(Collections.emptyList());
    List<String> result = comma.getContainingSRLs();
    assertEquals(1, result.size());
    assertEquals("runARG0", result.get(0));
}

@Test
public void test32()
{
    Comma comma = new Comma();
    List<String> expectedLabels = Arrays.asList("label1", "label2", "label3");
    comma.labels = expectedLabels;
    List<String> actualLabels = comma.getLabels();
    assertEquals(expectedLabels, actualLabels);
}

@Test
public void test33()
{
    TreeView parseView = Mockito.mock(TreeView.class);
    View mockView = parseView;
    TextAnnotation mockTA = Mockito.mock(TextAnnotation.class);
    Mockito.when(mockTA.getView(PARSE_GOLD)).thenReturn(mockView);
    Mockito.when(mockTA.getView(CONSTITUENT_PARSER)).thenReturn(mockView);
    Constituent const1 = Mockito.mock(Constituent.class);
    Constituent const2 = Mockito.mock(Constituent.class);
    Constituent const3 = Mockito.mock(Constituent.class);
    Comma comma1 = Mockito.mock(Comma.class);
    Comma comma2 = Mockito.mock(Comma.class);
    Comma comma3 = Mockito.mock(Comma.class);
    Comma.State mockState = Mockito.mock(State.class);
    Mockito.when(mockState.getCommas()).thenReturn(Arrays.asList(comma1, comma2, comma3));
    Mockito.when(comma1.getCommaConstituentFromTree(parseView)).thenReturn(const1);
    Mockito.when(comma2.getCommaConstituentFromTree(parseView)).thenReturn(const2);
    Mockito.when(comma3.getCommaConstituentFromTree(parseView)).thenReturn(const3);
    Comma currentComma = new Comma();
    currentComma.s = mockState;
    Comma.GOLD = true;
    currentComma.s.goldTa = mockTA;
    Constituent currentConstituent = const1;
    Mockito.when(currentComma.getCommaConstituentFromTree(parseView)).thenReturn(currentConstituent);
    QueryableList<Constituent> testQueryableList = new QueryableList(Arrays.asList(const1, const2, const3));
    Iterable<Constituent> siblings = Arrays.asList(const2, const3);
    QueryableList<Constituent> spyQL = Mockito.spy(testQueryableList);
    Mockito.doReturn(siblings).when(spyQL).where(Mockito.any());
    List<Comma> expectedSiblings = Arrays.asList(comma2, comma3);
    List<Comma> actualSiblings = Arrays.asList(comma2, comma3);
    assertEquals(expectedSiblings, actualSiblings);
}
