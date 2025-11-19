public class EnglishTreebankParserParams_wosr_1_GPTLLMTest { 

 @Test
  public void testDefaultConstructor() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    Assert.assertNotNull(params.headFinder());
    Assert.assertNotNull(params.treebankLanguagePack());
  }
@Test
  public void testDiskTreebankNotNull() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    DiskTreebank tb = params.diskTreebank();
    Assert.assertNotNull(tb);
  }
@Test
  public void testMemoryTreebankNotNull() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    MemoryTreebank tb = params.memoryTreebank();
    Assert.assertNotNull(tb);
  }
@Test
  public void testTestMemoryTreebankNotNull() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    MemoryTreebank tb = params.testMemoryTreebank();
    Assert.assertNotNull(tb);
  }
@Test
  public void testTreeReaderFactoryReturnsNonNullReader() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    TreeReaderFactory trf = params.treeReaderFactory();
    TreeReader reader = trf.newTreeReader(new StringReader("(ROOT (S (NP (DT The) (NN cat)) (VP (VBZ sits)) (. .)))"));
    Assert.assertNotNull(reader);
  }
@Test
  public void testCollinizerReturnsNonNull() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    AbstractCollinizer collinizer = params.collinizer();
    Assert.assertNotNull(collinizer);
  }
@Test
  public void testTypedDependencyHeadFinderDefault() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    HeadFinder hf = params.typedDependencyHeadFinder();
    Assert.assertTrue(hf instanceof UniversalSemanticHeadFinder);
  }
@Test
  public void testTypedDependencyHeadFinderOriginalTrue() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setGenerateOriginalDependencies(true);
    HeadFinder hf = params.typedDependencyHeadFinder();
    Assert.assertTrue(hf instanceof SemanticHeadFinder);
  }
@Test
  public void testSubcategoryStripperOnLeafTree() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree leaf = tf.newLeaf(new Word("cat"));
    Tree result = params.subcategoryStripper().transformTree(leaf);
    Assert.assertNotNull(result);
    Assert.assertEquals("cat", result.nodeString());
    Assert.assertTrue(result.isLeaf());
  }
@Test
  public void testSubcategoryStripperSimpleTree() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    LabeledScoredTreeFactory tf = new LabeledScoredTreeFactory();
    Tree np = tf.newTreeNode(new CategoryWordTag("NP-TMP", "NP-TMP", "NP"), List.of(
      tf.newLeaf(new CategoryWordTag("cat", "cat", "NN"))
    ));
    Tree stripped = params.subcategoryStripper().transformTree(np);
    Assert.assertNotNull(stripped);
    Assert.assertEquals("NP-TMP", stripped.label().value());
  }
@Test
  public void testDefaultTestSentenceContent() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    List<Word> sentence = params.defaultTestSentence();
    Assert.assertEquals(6, sentence.size());
    Assert.assertEquals("This", sentence.get(0).word());
    Assert.assertEquals("test", sentence.get(4).word());
    Assert.assertEquals(".", sentence.get(5).word());
  }
@Test
  public void testSisterSplitLevel1() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] splitters = params.sisterSplitters();
    Assert.assertTrue(splitters.length > 0);
    Assert.assertTrue(contains(splitters, "ADJP=l=VBD"));
  }
@Test
  public void testSisterSplitLevel4() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-sisterSplitLevel", "4"}, 0);
    String[] splitters = params.sisterSplitters();
    Assert.assertTrue(splitters.length > 0);
    Assert.assertTrue(contains(splitters, "VP=l=NP"));
  }
@Test
  public void testTransformTreeBaseNPInsertion() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-baseNP", "2"}, 0);
    TreeFactory tf = new LabeledScoredTreeFactory();
    Tree leaf = tf.newLeaf(new CategoryWordTag("cat", "cat", "NN"));
    Tree preTerm = tf.newTreeNode(new CategoryWordTag("NN", "cat", "NN"), List.of(leaf));
    Tree np = tf.newTreeNode(new CategoryWordTag("NP", "cat", "NN"), List.of(preTerm));
    Tree newTree = params.transformTree(np, np);
    Assert.assertNotNull(newTree);
    Assert.assertEquals("NP", newTree.label().value().split("\\^")[0]);
  }
@Test
  public void testCorrectTagsEnabled() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-correctTags"}, 0);
    Assert.assertTrue(params.englishTrain.correctTags);
  } 
}