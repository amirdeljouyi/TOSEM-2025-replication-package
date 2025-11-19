public class EnglishTreebankParserParams_wosr_3_GPTLLMTest { 

 @Test
  public void testHeadFinderNotNull() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    assertNotNull(params.headFinder());
  }
@Test
  public void testTypedDependencyHeadFinderDefault() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    HeadFinder headFinder = params.typedDependencyHeadFinder();
    assertTrue(headFinder instanceof UniversalSemanticHeadFinder);
  }
@Test
  public void testTypedDependencyHeadFinderOriginalDependencies() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setGenerateOriginalDependencies(true);
    HeadFinder headFinder = params.typedDependencyHeadFinder();
    assertTrue(headFinder instanceof SemanticHeadFinder);
  }
@Test
  public void testDiskTreebankFactory() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    Treebank tb = params.diskTreebank();
    assertTrue(tb instanceof DiskTreebank);
  }
@Test
  public void testMemoryTreebankFactory() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    Treebank tb = params.memoryTreebank();
    assertTrue(tb instanceof MemoryTreebank);
  }
@Test
  public void testTestMemoryTreebankFactory() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    Treebank tb = params.testMemoryTreebank();
    assertTrue(tb instanceof MemoryTreebank);
  }
@Test
  public void testCollinizerNotNull() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    assertNotNull(params.collinizer());
  }
@Test
  public void testCollinizerEvalbNotNull() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    assertNotNull(params.collinizerEvalb());
  }
@Test
  public void testDefaultTestSentence() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    List<Word> sentence = params.defaultTestSentence();
    List<String> expected = Arrays.asList("This", "is", "just", "a", "test", ".");
    for (int i = 0; i < expected.size(); i++) {
      assertEquals(expected.get(i), sentence.get(i).word());
    }
  }
@Test
  public void testSisterSplittersLevel1() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] splitters = params.sisterSplitters();
    assertArrayEquals(EnglishTreebankParserParams.sisterSplit1, splitters);
  }
@Test
  public void testSetOptionFlagSplitIN() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    int result = params.setOptionFlag(new String[]{"-splitIN", "3"}, 0);
    assertEquals(2, result);
    assertEquals(3, params.englishTrain.splitIN);
  }
@Test
  public void testSubcategoryStripperRemovesFunctionTags() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree tree = tf.newTreeNode(new CategoryWordTag("NP-TMP", "foo", "NP-TMP"),
        Arrays.asList(tf.newLeaf(new Word("today"))));
    tree.setScore(4.2);
    Tree transformed = params.subcategoryStripper().transformTree(tree);
    assertEquals("NP", transformed.label().value());
  }
@Test
  public void testSubcategoryStripperRetainsTmpSubcategory() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.englishTest.retainTMPSubcategories = true;
    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree tree = tf.newTreeNode(new CategoryWordTag("PP-TMP", "in", "PP-TMP"),
        Arrays.asList(tf.newLeaf(new Word("December"))));
    tree.setScore(0.9);
    Tree transformed = params.subcategoryStripper().transformTree(tree);
    assertEquals("PP-TMP", transformed.label().value());
  }
@Test
  public void testSubcategoryStripperNestedNPRemovalCollinsStyle() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.englishTrain.splitBaseNP = 2;
    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree inner = tf.newTreeNode(new CategoryWordTag("NP", "John", "NP"),
        Arrays.asList(tf.newLeaf(new Word("John"))));
    Tree outer = tf.newTreeNode(new CategoryWordTag("NP", "Outer", "NP"),
        Arrays.asList(inner));
    Tree transformed = params.subcategoryStripper().transformTree(outer);
    assertEquals("NP", transformed.label().value());
    assertEquals(1, transformed.numChildren());
    assertEquals("John", transformed.firstChild().yieldWords().get(0).word());
  }
@Test
  public void testTransformTreeVPHeadSplitVP3() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.englishTrain.splitVP = 3;
    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree leaf = tf.newLeaf(new Word("running"));
    CoreLabel leafLabel = new CoreLabel();
    leafLabel.setWord("running");
    leafLabel.setTag("VBG");
    leaf.setLabel(leafLabel);
    Tree vp = tf.newTreeNode("VP", Arrays.asList(tf.newTreeNode("VBG", List.of(leaf))));
    CoreLabel vpLabel = new CoreLabel();
    vpLabel.setValue("VP");
    vpLabel.setTag("VBG");
    vpLabel.setWord("running");
    vp.setLabel(vpLabel);
    Tree root = tf.newTreeNode("S", List.of(vp));
    vp.setParents(root);
    Tree transformed = params.transformTree(vp, root);
    assertTrue(transformed.label().value().startsWith("VP-"));
  }
@Test
  public void testTransformTreePreTerminalNNSVerbTagCorrection() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.englishTrain.correctTags = true;
    TreeFactory tf = new LabeledScoredTreeFactory();
    CoreLabel leafLabel = new CoreLabel();
    leafLabel.setWord("start-up");
    leafLabel.setTag("NNS");
    Tree leaf = tf.newLeaf(leafLabel);
    Tree preTerm = tf.newTreeNode("NNS", List.of(leaf));
    CoreLabel parentLabel = new CoreLabel();
    parentLabel.setValue("VP");
    preTerm.setLabel(leafLabel);
    Tree vp = tf.newTreeNode(parentLabel, List.of(preTerm));
    Tree transformed = params.transformTree(preTerm, vp);
    assertEquals("NN", ((CategoryWordTag) transformed.label()).category());
  }
@Test
  public void testTransformTreeEmptyIsHandled() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    Tree result = params.transformTree(null, null);
    assertNull(result);
  }
@Test
  public void testParseTreeFromStringAndTransform() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String treeStr = "(S (NP (DT The) (NN dog)) (VP (VBZ barks)) (. .))";
    TreeReader reader = new PennTreeReader(new StringReader(treeStr),
        new LabeledScoredTreeFactory(), new BobChrisTreeNormalizer(params.treebankLanguagePack()));
    Tree rawTree = reader.readTree();
    Tree transformed = params.transformTree(rawTree, rawTree);
    assertNotNull(transformed);
    assertEquals("S", transformed.label().value());
  }
@Test
  public void testChangeBaseCatNoAnnotation() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String output = params.changeBaseCat("NP", "VP");
    assertEquals("VP", output);
  }
@Test
  public void testChangeBaseCatWithAnnotation() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String output = params.changeBaseCat("NP-TMP", "VP");
    assertEquals("VP-TMP", output);
  }
@Test
  public void testHasVerbInTree() {
    TreeFactory tf = new LabeledScoredTreeFactory();
    CategoryWordTag leafLabel = new CategoryWordTag("VB", "run", "VB");
    Tree leaf = tf.newLeaf(leafLabel);
    Tree preterminal = tf.newTreeNode("VB", List.of(leaf));
    Tree sentence = tf.newTreeNode("S", List.of(preterminal));
    boolean hasClausal = EnglishTreebankParserParams.hasClausalV(sentence);
    assertTrue(hasClausal);
  }
@Test
  public void testHasVerbInTerminalsList() {
    Label label = new CategoryWordTag("VBZ", "runs", "VBZ");
    List<Label> labels = List.of(label);
    boolean result = EnglishTreebankParserParams.hasV(labels);
    assertTrue(result);
  }
@Test
  public void testHasPrepositionInTerminalsList() {
    Label label = new CategoryWordTag("IN", "on", "IN");
    List<Label> labels = List.of(label);
    boolean result = EnglishTreebankParserParams.hasI(labels);
    assertTrue(result);
  }
@Test
  public void testHasConjunctionInTerminalsList() {
    Label label = new CategoryWordTag("CC", "and", "CC");
    List<Label> labels = List.of(label);
    boolean result = EnglishTreebankParserParams.hasC(labels);
    assertTrue(result);
  } 
}