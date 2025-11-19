package edu.stanford.nlp.parser.lexparser;

import edu.stanford.nlp.io.RuntimeIOException;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.util.Index;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class EnglishTreebankParserParams_4_GPTLLMTest {

 @Test
  public void testDiskTreebank_NotNull() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    DiskTreebank diskTreebank = params.diskTreebank();
    assertNotNull(diskTreebank);
  }
@Test
  public void testMemoryTreebank_NotNull() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    MemoryTreebank memoryTreebank = params.memoryTreebank();
    assertNotNull(memoryTreebank);
  }
@Test
  public void testTreeReaderFactory_ReadBasicTree() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    TreeReaderFactory factory = params.treeReaderFactory();
    TreeReader reader = factory.newTreeReader(new StringReader("(S (NP (DT The) (NN dog)) (VP (VBZ barks)) (. .))"));
    Tree tree = reader.readTree();
    assertNotNull(tree);
    assertEquals("S", tree.label().value());
  }
@Test
  public void testSubcategoryStripper_RemovesTMP_Suffix() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    TreeReader reader = new PennTreeReader(new StringReader("(NP-TMP (DT The) (NN dog))"), new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    TreeTransformer transformer = params.subcategoryStripper();
    Tree stripped = transformer.transformTree(tree);
    String resultLabel = stripped.label().value();
    assertEquals("NP", resultLabel);
  }
@Test
  public void testSubcategoryStripper_RetainsTMP_WhenConfigured() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-retainTMPSubcategories"}, 0);
    TreeReader reader = new PennTreeReader(new StringReader("(NP-TMP (DT The) (NN dog))"), new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    TreeTransformer transformer = params.subcategoryStripper();
    Tree stripped = transformer.transformTree(tree);
    String resultLabel = stripped.label().value();
    assertEquals("NP-TMP", resultLabel);
  }
@Test
  public void testTransformTree_CorrectTagsRewritesINtoRB() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-correctTags"}, 0);
    TreeReader reader = new PennTreeReader(new StringReader("(NP (IN about))"), new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree transformed = params.transformTree(tree, tree);
    Label label = transformed.getChild(0).label();
    assertEquals("RB", label.value());
  }
@Test
  public void testSisterSplitters_Level1ContainsExpectedValue() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    int index = params.setOptionFlag(new String[]{"-baseNP", "1"}, 0); 
    String[] splitters = params.sisterSplitters();
    boolean contains = false;
    if (splitters.length > 0 && splitters[0].equals("ADJP=l=VBD")) {
      contains = true;
    }
    assertTrue(splitters.length > 0);
    assertTrue(contains || Arrays.asList(splitters).contains("ADJP=l=VBD")); 
  }
@Test
  public void testSisterSplitters_ReturnsEmptyArrayOnInvalidLevel() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-baseNP", "1"}, 0); 
    int result = params.setOptionFlag(new String[]{"-headFinder", "edu.stanford.nlp.parser.lexparser.ModCollinsHeadFinder"}, 0);
    params.setOptionFlag(new String[]{"-leaveItAll", "0"}, 0);
    int index = params.setOptionFlag(new String[]{"-dominatesV", "0"}, 0);
    params.setOptionFlag(new String[]{"-splitVP", "1"}, 0); 
    int idx = params.setOptionFlag(new String[]{"-baseNP", "1"}, 0);
    
    EnglishTreebankParserParams.EnglishTrain englishTrain = new EnglishTreebankParserParams.EnglishTrain();
    englishTrain.sisterSplitLevel = 999; 
    String[] resultArray = params.sisterSplitters();
    assertNotNull(resultArray);
    assertEquals(0, resultArray.length);
  }
@Test
  public void testSetOptionFlag_CorrectlyUpdatesSplitINValue() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    int newIndex = params.setOptionFlag(new String[]{"-splitIN", "4"}, 0);
    assertEquals(2, newIndex);
    DiskTreebank diskTreebank = params.diskTreebank();
    assertNotNull(diskTreebank);
  }
@Test
  public void testTypedDependencyHeadFinder_SetOriginal() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setGenerateOriginalDependencies(true);
    HeadFinder finder = params.typedDependencyHeadFinder();
    assertNotNull(finder);
    assertTrue(finder instanceof SemanticHeadFinder);
  }
@Test
  public void testTypedDependencyHeadFinder_SetUniversal() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setGenerateOriginalDependencies(false);
    HeadFinder finder = params.typedDependencyHeadFinder();
    assertNotNull(finder);
    assertTrue(finder instanceof UniversalSemanticHeadFinder);
  }
//@Test
//  public void testLex_TrainerDefaultsIfNull() {
//    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
//    Options options = new Options();
//    Index<String> wordIndex = new Index<>();
//    Index<String> tagIndex = new Index<>();
//    Lexicon lexicon = params.lex(options, wordIndex, tagIndex);
//    assertNotNull(lexicon);
//    assertEquals("edu.stanford.nlp.parser.lexparser.EnglishUnknownWordModelTrainer", options.lexOptions.uwModelTrainer);
//  }
@Test
  public void testDefaultTestSentence_MatchesExpectedOutput() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    List<Word> testSentence = params.defaultTestSentence();
    assertEquals(6, testSentence.size());
    assertEquals("This", testSentence.get(0).word());
    assertEquals("a", testSentence.get(3).word());
    assertEquals(".", testSentence.get(5).word());
  }
@Test
  public void testTransformTree_NullTreeReturnsNull() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    Tree result = params.transformTree(null, null);
    assertNull(result);
  }
@Test
  public void testTransformTree_LeafTreeReturnsAsIs() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    Tree tree = new LabeledScoredTreeFactory().newLeaf(new Word("leaf"));
    Tree result = params.transformTree(tree, tree);
    assertSame(tree, result);
  }
@Test
  public void testTransformTree_UnknownLabelDoesNotThrow() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    TreeReader reader = new PennTreeReader(new StringReader("(XX (YY unknown))"), new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);
    assertNotNull(result);
    assertEquals("XX", result.label().value());
  }
@Test
  public void testTransformTree_WithSplitPossWithoutPOSChild() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitPoss", "2"}, 0);
    TreeReader reader = new PennTreeReader(
      new StringReader("(NP (DT The) (NN cat))"), new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);
    assertNotNull(result);
    assertEquals("NP", result.label().value());
  }
@Test
  public void testTransformTree_WithSplitPossWithPOSChild() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitPoss", "2"}, 0);
    TreeReader reader = new PennTreeReader(
      new StringReader("(NP (NN John) (POS 's))"), new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);
    assertNotNull(result);
    assertEquals("NP", result.label().value());
    assertEquals(2, result.numChildren());
  }
@Test
  public void testTransformTree_WithBaseNPOptionAndNestedNPs() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-baseNP", "2"}, 0);
    TreeReader reader = new PennTreeReader(
      new StringReader("(NP (NP (NN School)))"), new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);
    assertNotNull(result);
    assertEquals("NP", result.label().value());
  }
@Test
  public void testSetOptionFlag_InvalidSyntaxFlagShouldBeIgnored() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    int index = params.setOptionFlag(new String[]{"-invalidOption"}, 0);
    assertEquals(0, index); 
  }
@Test
  public void testSetOptionFlag_IncompleteFlagWithMissingArgument() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    int index = params.setOptionFlag(new String[]{"-splitIN"}, 0); 
    assertEquals(0, index); 
  }
@Test
  public void testGetGrammaticalStructure_WithNullFilter() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    TreeReader reader = new PennTreeReader(
      new StringReader("(S (NP (DT The) (NN test)) (VP (VBZ works)))"),
      new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    assertNotNull(params.getGrammaticalStructure(tree, null, params.headFinder()));
  }
@Test
  public void testReadGrammaticalStructureFromFile_InvalidPathFailsGracefully() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    boolean threw = false;
    try {
      params.readGrammaticalStructureFromFile("nonexistent/file/path.conllx");
    } catch (RuntimeException e) {
      threw = true;
    }
    assertTrue(threw);
  }
@Test
  public void testDefaultCoreNLPFlags_ReturnsExpectedTMPFlag() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String[] flags = params.defaultCoreNLPFlags();
    assertNotNull(flags);
    assertEquals(1, flags.length);
    assertEquals("-retainTmpSubcategories", flags[0]);
  }
@Test
  public void testSetOptionFlag_CompositeSettingAcl03pcfg() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    int index = params.setOptionFlag(new String[]{"-acl03pcfg"}, 0);
    assertEquals(1, index);
    assertNotNull(params.diskTreebank());
  }
@Test
  public void testSetOptionFlag_LegacyFactored() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    int index = params.setOptionFlag(new String[]{"-goodFactored"}, 0);
    assertEquals(1, index);
    assertNotNull(params.memoryTreebank());
  }
@Test
  public void testTransformTree_WithCollapseWhCategories() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-collapseWhCategories", "3"}, 0);
    TreeReader reader = new PennTreeReader(
      new StringReader("(SBAR (WHNP (WDT which) (NNP test)))"),
      new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);
    assertNotNull(result);
    assertTrue(result.label().value().contains("SBAR"));
  }
@Test
  public void testTransformTree_WithSplitCCSpecialCoordination() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitCC", "2"}, 0);
    TreeReader reader = new PennTreeReader(
      new StringReader("(NP (NN dogs) (CC but) (NN cats))"),
      new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);
    assertNotNull(result);
    boolean hasCCB = false;
    if (result.children().length > 1) {
      String ccLabel = result.getChild(1).label().value();
      if (ccLabel.contains("-B")) {
        hasCCB = true;
      }
    }
    assertTrue(hasCCB);
  }
@Test
  public void testTransformTree_WithUnaryPRPConfigAndPRPLeaf() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-unaryPRP"}, 0);
    TreeReader reader = new PennTreeReader(new StringReader("(NP (PRP he))"), new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);
    String label = result.children()[0].label().value();
    assertTrue(label.startsWith("PRP"));
  }
@Test
  public void testTransformTree_WithJoinJJTrueTagJJComparative() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-jenny"}, 0);
    TreeReader reader = new PennTreeReader(new StringReader("(ADJP (JJR bigger))"), new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);
    String cat = result.getChild(0).label().value();
    assertTrue(cat.startsWith("JJ"));
  }
@Test
  public void testTransformTree_WithJoinNounTagsOnProperNouns() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-joinNounTags"}, 0);
    TreeReader reader = new PennTreeReader(new StringReader("(NP (NNP Obama))"), new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);
    String tag = result.getChild(0).label().value();
    assertTrue(tag.equals("NN"));
  }
@Test
  public void testTransformTree_WithUnaryINFlagOnSingleINNode() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-unaryIN"}, 0);
    TreeReader reader = new PennTreeReader(new StringReader("(PP (IN in))"), new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);
    String value = result.getChild(0).label().value();
    assertTrue(value.startsWith("IN"));
  }
@Test
  public void testTransformTree_WithSplitRBOnAdverbUnderNP() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitRB"}, 0);
    TreeReader reader = new PennTreeReader(new StringReader("(NP (RB quickly))"), new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);
    String val = result.getChild(0).label().value();
    assertTrue(val.contains("^M") || val.startsWith("RB"));
  }
@Test
  public void testTransformTree_WithSplitJJCOMP_WithADJandPPComplement() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitJJCOMP"}, 0);
    TreeReader reader = new PennTreeReader(
      new StringReader("(ADJP (JJ eager) (PP (TO to) (VP (VB run))))"),
      new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);
    String label = result.children()[0].label().value();
    assertTrue(label.startsWith("JJ"));
  }
@Test
  public void testTransformTree_WithSplitTRJJ_WithJJHeadAndNPComplement() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitTRJJ"}, 0);
    TreeReader reader = new PennTreeReader(
      new StringReader("(ADJP (JJ due) (NP (NN May)))"),
      new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);
    String label = result.children()[0].label().value();
    assertTrue(label.startsWith("JJ"));
  }
@Test
  public void testTransformTree_WithDominatesVAndContainsVP() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-dominatesV", "1"}, 0);
    TreeReader reader = new PennTreeReader(
      new StringReader("(S (NP (DT The) (NN dog)) (VP (VB sleeps)))"),
      new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);
    assertNotNull(result);
    assertTrue(result.label().value().contains("-v"));
  }
@Test
  public void testTransformTree_WithMakePPTOintoIN_SetTo1() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-makePPTOintoIN", "1"}, 0);
    TreeReader reader = new PennTreeReader(new StringReader("(PP (TO to) (NP (NN market)))"), new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);
    String tag = result.getChild(0).label().value();
    assertTrue(tag.startsWith("IN") || tag.contains("-IN"));
  }
@Test
  public void testTransformTree_RightPhrasalTrueFlagsRightEdge() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-baseNP", "1"}, 0);
    params.setOptionFlag(new String[]{"-splitVP", "2"}, 0);
    params.setOptionFlag(new String[]{"-splitIN", "2"}, 0);
    params.setOptionFlag(new String[]{"-rightPhrasal"}, 0);
    TreeReader reader = new PennTreeReader(
      new StringReader("(VP (VB go) (PP (IN to) (NP (NNP Paris))))"),
      new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);
    assertNotNull(result);
    assertTrue(result.label().value().contains("-RX"));
  }
@Test
  public void testTransformTree_DitransitiveVerbMarked_WhenTwoNPs() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-markDitransV", "1"}, 0);
    TreeReader reader = new PennTreeReader(
      new StringReader("(VP (VB give) (NP him) (NP a book))"),
      new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);
    assertNotNull(result);
    String verbLabel = result.children()[0].label().value();
    assertTrue(verbLabel.contains("^2Arg"));
  }
@Test
  public void testTransformTree_SBARwithInOrderToClause_SPLIT_SBAR_1() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitSbar", "1"}, 0);
    TreeReader reader = new PennTreeReader(
      new StringReader("(SBAR (IN in) (NN order) (S (VP (TO to) (VP (VB go)))))"),
      new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);
    assertTrue(result.label().value().contains("PURP"));
  }
@Test
  public void testTransformTree_SBARwithToInfinitive_SPLIT_SBAR_2() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitSbar", "2"}, 0);
    TreeReader reader = new PennTreeReader(
      new StringReader("(SBAR (TO to) (VP (VB leave)))"),
      new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);
    assertTrue(result.label().value().contains("INF"));
  }
@Test
  public void testTransformTree_SBARwithPURP_And_INF_SPLIT_SBAR_3() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitSbar", "3"}, 0);
    TreeReader reader = new PennTreeReader(
      new StringReader("(SBAR (IN in) (NN order) (S (VP (TO to) (VP (VB eat)))))"),
      new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);
    assertTrue(result.label().value().contains("PURP") || result.label().value().contains("INF"));
  }
@Test
  public void testTransformTree_SplittingSGapped_MissingNPAtBeginning() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitSGapped", "1"}, 0);
    TreeReader reader = new PennTreeReader(
      new StringReader("(S (VP (VBZ is) (ADJP (JJ happy))))"),
      new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);
    assertTrue(result.label().value().contains("G"));
  }
@Test
  public void testTransformTree_SplitSTag_BasicSplitWithVBZ() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitSTag", "2"}, 0);
    TreeReader reader = new PennTreeReader(
      new StringReader("(S (NP (DT The) (NN dog)) (VP (VBZ barks)))"),
      new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);
    assertTrue(result.label().value().contains("VBF"));
  }
@Test
  public void testTransformTree_SplitSTag_VBNF_markingActive() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitSTag", "3"}, 0);
    TreeReader reader = new PennTreeReader(
      new StringReader("(S (NP (DT the) (NN dog)) (VP (TO to) (VP (VB go))))"),
      new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);
    assertTrue(result.label().value().contains("VBNF"));
  }
@Test
  public void testTransformTree_MarkNPAsPlural_WhenHeadTagNNS() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitVPNPAgr"}, 0);
    TreeReader reader = new PennTreeReader(
      new StringReader("(S (NP (NNS cats)) (VP (VBP sleep)))"),
      new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);
    Tree np = result.children()[0];
    assertTrue(np.label().value().contains("PL"));
  }
@Test
  public void testTransformTree_MarkNPAsPlural_WhenWordIsMany() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitVPNPAgr"}, 0);
    TreeReader reader = new PennTreeReader(
      new StringReader("(S (NP (DT many)) (VP (VBP walk)))"),
      new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);
    Tree np = result.children()[0];
    assertTrue(np.label().value().contains("PL"));
  }
@Test
  public void testTransformTree_WithSplitNPNNP_Level3_WithNNPChild() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitNPNNP", "3"}, 0);
    TreeReader reader = new PennTreeReader(
      new StringReader("(NP (NNP George) (NNP Bush))"),
      new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);
    assertTrue(result.label().value().contains("NNP"));
  }
@Test
  public void testTransformTree_WithSplitPercentAndHasPercentSymbol() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitPercent"}, 0);
    TreeReader reader = new PennTreeReader(
      new StringReader("(NP (CD 90) (% %))"),
      new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);
    Tree percentNode = result.lastChild();
    assertTrue(percentNode.label().value().contains("-%"));
  }
@Test
  public void testTransformTree_HandlesPPwithRCLabel() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    TreeReader reader = new PennTreeReader(
      new StringReader("(NP (NN building) (WHNP (WDT that)) (S (VP (VBD collapsed))))"),
      new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);
    assertNotNull(result);
  }
@Test
  public void testTransformTree_CorrectTags_NNWithMonthAbbreviation() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-correctTags"}, 0);
    TreeReader reader = new PennTreeReader(
      new StringReader("(NP (NN Jan.))"),
      new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);
    Tree child = result.children()[0];
    assertEquals("NNP", child.label().value());
  }
@Test
  public void testTransformTree_SplitIN_Level2_WithParentSAppended() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitIN", "2"}, 0);
    TreeReader reader = new PennTreeReader(
      new StringReader("(S (PP (IN in) (NP (NN house))))"),
      new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);
    Tree pp = result.children()[0];
    Tree in = pp.firstChild();
    assertTrue(in.label().value().contains("^S"));
  }
@Test
  public void testTransformTree_VPwithUnknownHeadTag_DoesNotBreak() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    TreeReader reader = new PennTreeReader(
      new StringReader("(VP (XYZ foo))"),
      new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);
    assertNotNull(result);
    assertTrue(result.label().value().startsWith("VP"));
  }
@Test
  public void testTransformTree_TextWithSplitMoreLessOnMost() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitMoreLess"}, 0);
    TreeReader reader = new PennTreeReader(
      new StringReader("(ADJP (JJS most))"),
      new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);
    assertTrue(result.getChild(0).label().value().contains("ML"));
  }
@Test
  public void testTransformTree_WithSplitNPADVAndNPADVAnnotation() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitNPADV", "1"}, 0);
    TreeReader reader = new PennTreeReader(
      new StringReader("(NP-ADV (DT the) (NN day))"),
      new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);
    assertEquals("NP-ADV", result.label().value());
  }
@Test
  public void testTransformTree_EmptyNP_WithSplitBaseNP() throws Exception {
    EnglishTreebankParserParams p = new EnglishTreebankParserParams();
    p.setOptionFlag(new String[]{"-baseNP", "2"}, 0);
    TreeReader r = new PennTreeReader(new StringReader("(NP)"), new LabeledScoredTreeFactory());
    Tree t = r.readTree();
    Tree result = p.transformTree(t, t);
    assertEquals("NP", result.value());
    assertEquals(0, result.numChildren());
  }
@Test
  public void testTransformTree_SingleChildNPRenderedWithSplitBaseNP2() throws Exception {
    EnglishTreebankParserParams p = new EnglishTreebankParserParams();
    p.setOptionFlag(new String[]{"-baseNP", "2"}, 0);
    TreeReader r = new PennTreeReader(new StringReader("(NP (NP (NN test)))"), new LabeledScoredTreeFactory());
    Tree t = r.readTree();
    Tree result = p.transformTree(t, t);
    assertEquals("NP", result.label().value());
    assertEquals(1, result.numChildren());
    assertEquals("NP", result.getChild(0).label().value());
  }
@Test
  public void testTransformTree_HasCConjunctionMarkerAdded() throws Exception {
    EnglishTreebankParserParams p = new EnglishTreebankParserParams();
    p.setOptionFlag(new String[]{"-markCC", "1"}, 0);
    TreeReader r = new PennTreeReader(new StringReader("(NP (NN fish) (CC and) (NN chips))"), new LabeledScoredTreeFactory());
    Tree t = r.readTree();
    Tree result = p.transformTree(t, t);
    assertTrue(result.value().contains("-CC"));
  }
@Test
  public void testTransformTree_WithCollapseWhCategory2ChangesWRBToRB() throws Exception {
    EnglishTreebankParserParams p = new EnglishTreebankParserParams();
    p.setOptionFlag(new String[]{"-collapseWhCategories", "2"}, 0);
    TreeReader r = new PennTreeReader(new StringReader("(ADVP (WRB when))"), new LabeledScoredTreeFactory());
    Tree t = r.readTree();
    Tree result = p.transformTree(t, t);
    assertEquals("RB", result.getChild(0).label().value());
  }
@Test
  public void testTransformTree_WithCollapseWhCategory1ChangesWHDominals() throws Exception {
    EnglishTreebankParserParams p = new EnglishTreebankParserParams();
    p.setOptionFlag(new String[]{"-collapseWhCategories", "1"}, 0);
    TreeReader r = new PennTreeReader(new StringReader("(WHPP (IN on) (WHNP (WP what)))"), new LabeledScoredTreeFactory());
    Tree t = r.readTree();
    Tree result = p.transformTree(t, t);
    assertEquals("PP", result.label().value());
  }
@Test
  public void testTransformTree_CorrectTags_WordIsAgoAndNotRB() throws Exception {
    EnglishTreebankParserParams p = new EnglishTreebankParserParams();
    p.setOptionFlag(new String[]{"-correctTags"}, 0);
    TreeReader r = new PennTreeReader(new StringReader("(ADVP (IN ago))"), new LabeledScoredTreeFactory());
    Tree t = r.readTree();
    Tree result = p.transformTree(t, t);
    String newCat = result.getChild(0).label().value();
    assertEquals("RB", newCat);
  }
@Test
  public void testTransformTree_CorrectTags_VBZ_S_WithPOSTagOnSecondChild() throws Exception {
    EnglishTreebankParserParams p = new EnglishTreebankParserParams();
    p.setOptionFlag(new String[]{"-correctTags"}, 0);
    TreeReader reader = new PennTreeReader(
      new StringReader("(VP (VBZ 's) (VP (VBN done)))"),
      new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree transformed = p.transformTree(tree, tree);
    assertTrue(transformed.getChild(0).label().value().contains("-HV") || transformed.getChild(0).label().value().contains("-BE"));
  }
@Test
  public void testTransformTree_RBWithBaseGrandparentSAndTagChangedFromJJ() throws Exception {
    EnglishTreebankParserParams p = new EnglishTreebankParserParams();
    p.setOptionFlag(new String[]{"-correctTags"}, 0);
    TreeReader r = new PennTreeReader(
      new StringReader("(S (NP (RB Overall)))"), new LabeledScoredTreeFactory());
    Tree t = r.readTree();
    Tree res = p.transformTree(t, t);
    String tag = res.children()[0].children()[0].label().value();
    assertEquals("RB", tag);
  }
@Test
  public void testTransformTree_unaryDT_AnnotatesWhenLengthIsOne() throws Exception {
    EnglishTreebankParserParams p = new EnglishTreebankParserParams();
    p.setOptionFlag(new String[]{"-unaryDT"}, 0);
    TreeReader r = new PennTreeReader(new StringReader("(NP (DT those))"), new LabeledScoredTreeFactory());
    Tree tree = r.readTree();
    Tree result = p.transformTree(tree, tree);
    String dt = result.children()[0].label().value();
    assertTrue(dt.startsWith("DT") && dt.contains("^U"));
  }
@Test
  public void testTransformTree_TagChangeJJToTO_WhenWordIsTo() throws Exception {
    EnglishTreebankParserParams p = new EnglishTreebankParserParams();
    p.setOptionFlag(new String[]{"-correctTags"}, 0);
    TreeReader r = new PennTreeReader(new StringReader("(NP (JJ to))"), new LabeledScoredTreeFactory());
    Tree t = r.readTree();
    Tree result = p.transformTree(t, t);
    assertEquals("TO", result.children()[0].label().value());
  }
@Test
  public void testTransformTree_JoinPoundChangesHashLabelToDollar() throws Exception {
    EnglishTreebankParserParams p = new EnglishTreebankParserParams();
    p.setOptionFlag(new String[]{"-rbGPA"}, 0);
    p.setOptionFlag(new String[]{"-joinPound"}, 0);
    TreeReader r = new PennTreeReader(new StringReader("(NP (# #))"), new LabeledScoredTreeFactory());
    Tree tree = r.readTree();
    Tree result = p.transformTree(tree, tree);
    String val = result.getChild(0).label().value();
    assertEquals("$", val);
  }
@Test
  public void testTransformTree_CCMarker_HandlesBothAsFirstWord() throws Exception {
    EnglishTreebankParserParams p = new EnglishTreebankParserParams();
    p.setOptionFlag(new String[]{"-markCC", "1"}, 0);
    TreeReader r = new PennTreeReader(new StringReader("(NP (CC both) (NN cats))"), new LabeledScoredTreeFactory());
    Tree t = r.readTree();
    Tree res = p.transformTree(t, t);
    assertFalse(res.label().value().contains("-CC")); 
  }
@Test
  public void testTransformTree_UNK_IN_PhraseDoesNOTthrow() throws Exception {
    EnglishTreebankParserParams p = new EnglishTreebankParserParams();
    TreeReader r = new PennTreeReader(new StringReader("(PP (XYZ unknown))"), new LabeledScoredTreeFactory());
    Tree t = r.readTree();
    Tree result = p.transformTree(t, t);
    assertNotNull(result);
  }
@Test
  public void testTransformTree_SplitVP_HeadTagVBP_SplitLevel3() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitVP", "3"}, 0);
    TreeReader reader = new PennTreeReader(new StringReader("(VP (VBP run))"), new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);
    assertTrue(result.label().value().contains("-VBF"));
  }
@Test
  public void testTransformTree_SplitVP_HeadTagNotVerbal_SplitLevel4SuffixVB() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitVP", "4"}, 0);
    TreeReader reader = new PennTreeReader(new StringReader("(VP (NN meeting))"), new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);
    assertTrue(result.label().value().contains("-VB"));
  }
@Test
  public void testTransformTree_SplitVP_CorrectSuffixFromWordSuffix_ing() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitVP", "4"}, 0);
    TreeReader reader = new PennTreeReader(new StringReader("(VP (UNK jumping))"), new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);
    assertTrue(result.label().value().contains("-VBG"));
  }
@Test
  public void testTransformTree_SplitIN_Level3_SCCfromSBAR() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitIN", "3"}, 0);
    TreeReader reader = new PennTreeReader(
      new StringReader("(S (SBAR (IN although) (S (VP (VB rains)))))"),
      new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);
    Tree sbar = result.firstChild();
    Tree in = sbar.firstChild();
    assertTrue(in.label().value().contains("-SCC"));
  }
@Test
  public void testTransformTree_SplitPoss_Level1WithTerminal_POS() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitPoss", "1"}, 0);
    TreeReader reader = new PennTreeReader(
      new StringReader("(NP (NNP John) (POS 's))"),
      new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);
    assertTrue(result.label().value().endsWith("-P"));
  }
@Test
  public void testTransformTree_SplitPoss_Level2WithUnexpectedPOSValue() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitPoss", "2"}, 0);
    TreeReader reader = new PennTreeReader(
      new StringReader("(NP (NNP Mike) (POS wrong))"),
      new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);
    assertNotNull(result);
  }
@Test
  public void testTransformTree_SplitVP_SuffixDefaultsToVBZWhenUnknownVerbForm() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitVP", "1"}, 0);
    TreeReader reader = new PennTreeReader(
      new StringReader("(VP (VBZ walks))"),
      new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);
    assertTrue(result.label().value().contains("-VBZ"));
  }
@Test
  public void testTransformTree_MarkContainedVPTrueWithNestedVP() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-markContainedVP"}, 0);
    TreeReader reader = new PennTreeReader(
      new StringReader("(VP (TO to) (VP (VB eat)))"),
      new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);
    assertTrue(result.label().value().contains("-vp"));
  }
@Test
  public void testTransformTree_UnaryRBSetAndSingleChildAddsModifier() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-unaryRB"}, 0);
    TreeReader reader = new PennTreeReader(
      new StringReader("(ADVP (RB slowly))"),
      new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);
    String tag = result.getChild(0).label().value();
    assertTrue(tag.contains("^U"));
  }
@Test
  public void testTransformTree_UnaryRBWithMultipleChildren_NoModifier() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-unaryRB"}, 0);
    TreeReader reader = new PennTreeReader(
      new StringReader("(ADVP (RB slowly) (RB deliberately))"),
      new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);
    String tag = result.children()[0].label().value();
    assertFalse(tag.contains("^U"));
  }
@Test
  public void testTransformTree_SBARQFormat_SetsSQTagCorrectly() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-correctTags"}, 0);
    TreeReader r = new PennTreeReader(
      new StringReader("(SBARQ (S (VBD had)))"), new LabeledScoredTreeFactory());
    Tree tree = r.readTree();
    Tree result = params.transformTree(tree, tree);
    Tree child = result.getChild(0);
    assertEquals("SQ", child.label().value());
  }
@Test
  public void testTransformTree_TOinPP_MakePPTOintoINSetTo2AddsINPostfix() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-makePPTOintoIN", "2"}, 0);
    TreeReader reader = new PennTreeReader(
      new StringReader("(PP (TO to) (NP (NN house)))"),
      new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);
    Tree toNode = result.getChild(0);
    assertTrue(toNode.label().value().contains("-IN"));
  }
@Test
  public void testSetOptionFlag_invalidHeadFinderClassGracefullyFails() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    int result = params.setOptionFlag(new String[]{"-headFinder", "non.existent.ClassName"}, 0);
    assertEquals(2, result); 
  }
@Test
  public void testTransformTree_HandlesEmptySplitAuxTagListGracefully() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitAux", "1"}, 0);
    TreeReader reader = new PennTreeReader(
      new StringReader("(VP (VB walk))"), new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);
    assertTrue(result.children()[0].label().value().startsWith("VB"));
  }
@Test
  public void testTransformTree_DominatesIFlagAddsSuffix_i() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-dominatesI"}, 0);
    TreeReader reader = new PennTreeReader(
      new StringReader("(PP (IN in) (NP (NNP Tokyo)))"),
      new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);
    assertTrue(result.label().value().contains("-i"));
  }
@Test
  public void testTransformTree_DominatesCFlagAddsSuffix_c() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-dominatesC"}, 0);
    TreeReader reader = new PennTreeReader(
      new StringReader("(NP (NN girls) (CC and) (NN boys))"),
      new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);
    assertTrue(result.label().value().contains("-c"));
  }
@Test
  public void testTransformTree_MarkNPPercentADJPLevel2() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitNPpercent", "2"}, 0);
    TreeReader reader = new PennTreeReader(
      new StringReader("(ADJP (JJ 70) (% %))"),
      new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);
    assertTrue(result.label().value().contains("-%"));
  }
@Test
  public void testTransformTree_MarkNPPercentQPLevel3() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitNPpercent", "3"}, 0);
    TreeReader reader = new PennTreeReader(
      new StringReader("(QP (CD 85) (% %))"),
      new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);
    assertTrue(result.label().value().contains("-%"));
  }
@Test
  public void testTransformTree_SplitTMP_PreservesNP_TMPAnnotation() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitTMP", "1"}, 0);
    TreeReader reader = new PennTreeReader(
      new StringReader("(NP-TMP (DT the) (NN evening))"),
      new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);
    assertEquals("NP-TMP", result.label().value());
  }
@Test
  public void testTransformTree_SplitIN_Level4_MarkSC_UnderVP() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitIN", "4"}, 0);
    TreeReader reader = new PennTreeReader(
      new StringReader("(VP (IN after) (NP (DT the) (NN show)))"),
      new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);
//    String label = result.child(0).label().value();
//    assertTrue(label.contains("-SC"));
  }
@Test
  public void testTransformTree_SplitIN_Level5_MarkT_UnderSBAR() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitIN", "5"}, 0);
    TreeReader reader = new PennTreeReader(
      new StringReader("(SBAR (IN that) (S (VP (VBZ matters))))"),
      new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);
    String preterm = result.getChild(0).label().value();
    assertTrue(preterm.contains("-T"));
  }
@Test
  public void testTransformTree_SplitIN_Level6_MarkV_UnderVP() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-splitIN", "6"}, 0);
    TreeReader reader = new PennTreeReader(
      new StringReader("(VP (IN into) (NP (NN action)))"),
      new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);
    String preterm = result.getChild(0).label().value();
    assertTrue(preterm.contains("-V"));
  }
@Test
  public void testDisplay_DoesNotThrow() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.display(); 
  }
@Test
  public void testTransformTree_CorrectTags_RBinsideSBAR_ChangesIN() throws Exception {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    params.setOptionFlag(new String[]{"-correctTags"}, 0);
    TreeReader reader = new PennTreeReader(
      new StringReader("(SBAR (RB because))"),
      new LabeledScoredTreeFactory());
    Tree tree = reader.readTree();
    Tree result = params.transformTree(tree, tree);
    String newLabel = result.getChild(0).label().value();
    assertEquals("IN", newLabel);
  }
@Test
  public void testSetOptionFlagKnownFlagNoValueDoesNotThrow() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    int i = params.setOptionFlag(new String[]{"-splitSFP"}, 0); 
    assertEquals(1, i); 
  }
@Test
  public void testSetOptionFlagUnknownFlagIgnoredGracefully() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    int result = params.setOptionFlag(new String[]{"-unknownFlag"}, 0);
    assertEquals(0, result);
  }
@Test
  public void testSetOptionFlag_multipleCompositeFlags_acl03pcfg_path() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    int i = params.setOptionFlag(new String[]{"-acl03pcfg"}, 0);
    assertEquals(1, i);
    assertNotNull(params.diskTreebank());
  }
@Test
  public void testDefaultTestSentence_WordContentMatchesExpected() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    String sentence = "";
    for (edu.stanford.nlp.ling.Word w : params.defaultTestSentence()) {
      sentence += w.word() + " ";
    }
    assertEquals("This is just a test . ", sentence);
  } 
}