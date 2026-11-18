package edu.stanford.nlp.parser.lexparser;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.trees.DiskTreebank;
import edu.stanford.nlp.trees.HeadFinder;
import edu.stanford.nlp.trees.MemoryTreebank;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeReader;
import edu.stanford.nlp.trees.international.pennchinese.BikelChineseHeadFinder;
import edu.stanford.nlp.trees.international.pennchinese.ChineseHeadFinder;
import edu.stanford.nlp.trees.international.pennchinese.ChineseSemanticHeadFinder;
import edu.stanford.nlp.trees.international.pennchinese.SunJurafskyChineseHeadFinder;
import edu.stanford.nlp.trees.international.pennchinese.UniversalChineseSemanticHeadFinder;
import edu.stanford.nlp.util.HashIndex;
import org.junit.Test;

import java.io.StringReader;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class ChineseTreebankParserParamsPromptTest {

  @Test
  public void defaultHeadFindersAndDependencyModeAreConsistent() {
    ChineseTreebankParserParams params = new ChineseTreebankParserParams();

    assertTrue(params.headFinder() instanceof ChineseHeadFinder);
    assertTrue(params.typedDependencyHeadFinder() instanceof UniversalChineseSemanticHeadFinder);
    assertFalse(params.generateOriginalDependencies());
    assertTrue(params.supportsBasicDependencies());

    params.setGenerateOriginalDependencies(true);
    assertTrue(params.generateOriginalDependencies());
    assertTrue(params.typedDependencyHeadFinder() instanceof ChineseSemanticHeadFinder);
  }

  @Test
  public void headFinderFlagsSelectImplementationsAndSunTakesPrecedence() {
    ChineseTreebankParserParams params = new ChineseTreebankParserParams();

    assertEquals(1, params.setOptionFlag(new String[] {"-bikelHead"}, 0));
    assertTrue(params.headFinder() instanceof BikelChineseHeadFinder);
    assertEquals(1, params.setOptionFlag(new String[] {"-sunHead"}, 0));
    assertTrue(params.headFinder() instanceof SunJurafskyChineseHeadFinder);
  }

  @Test
  public void reflectiveHeadFinderOverridesBuiltInSelection() {
    ChineseTreebankParserParams params = new ChineseTreebankParserParams();

    assertEquals(2, params.setOptionFlag(new String[] {
        "-headFinder", "edu.stanford.nlp.trees.international.pennchinese.BikelChineseHeadFinder"
    }, 0));
    assertTrue(params.headFinder() instanceof BikelChineseHeadFinder);
    assertEquals(1, params.setOptionFlag(new String[] {"-sunHead"}, 0));
    assertTrue(params.headFinder() instanceof BikelChineseHeadFinder);
  }

  @Test(expected = RuntimeException.class)
  public void reflectiveHeadFinderRejectsMissingClass() {
    ChineseTreebankParserParams params = new ChineseTreebankParserParams();
    params.setOptionFlag(new String[] {"-headFinder", "not.a.real.HeadFinder"}, 0);
  }

  @Test
  public void smoothingAndSisterSplitterDefaultsAreStable() {
    ChineseTreebankParserParams params = new ChineseTreebankParserParams();

    assertArrayEquals(new double[] {5.8, 17.7, 6.5, 0.4},
        params.MLEDependencyGrammarSmoothingParams(), 0.0);
    assertArrayEquals(new String[0], params.sisterSplitters());
  }

  @Test
  public void treebankFactoriesUseChineseReadersAndConfiguredEncoding() throws Exception {
    ChineseTreebankParserParams params = new ChineseTreebankParserParams();
    TreeReader reader = params.treeReaderFactory().newTreeReader(
        new StringReader("(ROOT (IP (NP (NN \u5b66\u6821))))"));
    Tree tree = reader.readTree();
    DiskTreebank disk = params.diskTreebank();
    MemoryTreebank memory = params.memoryTreebank();

    assertEquals("ROOT", tree.value());
    assertEquals("IP", tree.firstChild().value());
    assertEquals("utf-8", disk.encoding());
    assertEquals("utf-8", memory.encoding());
  }

  @Test
  public void invalidInputEncodingFallsBackToUtf8ForBothTreebanks() {
    ChineseTreebankParserParams params = new ChineseTreebankParserParams();
    params.setInputEncoding("definitely-not-a-character-set");

    assertEquals("UTF-8", params.diskTreebank().encoding());
    assertEquals("UTF-8", params.memoryTreebank().encoding());
  }

  @Test
  public void collinizerFactoriesAndLanguagePackAreChineseSpecific() {
    ChineseTreebankParserParams params = new ChineseTreebankParserParams();

    assertEquals("ChineseCollinizer", params.collinizer().getClass().getSimpleName());
    assertEquals("ChineseCollinizer", params.collinizerEvalb().getClass().getSimpleName());
    assertEquals("ChineseTreebankLanguagePack",
        params.treebankLanguagePack().getClass().getSimpleName());
    assertNotSame(params.collinizer(), params.collinizerEvalb());
  }

  @Test
  public void lexCreatesStandardAndCharacterBasedImplementations() {
    ChineseTreebankParserParams standardParams = new ChineseTreebankParserParams();
    Lexicon standard = standardParams.lex(new Options(standardParams),
        new HashIndex<String>(), new HashIndex<String>());
    ChineseTreebankParserParams characterParams = new ChineseTreebankParserParams();
    characterParams.setOptionFlag(new String[] {"-charLex"}, 0);
    Lexicon character = characterParams.lex(new Options(characterParams),
        new HashIndex<String>(), new HashIndex<String>());

    assertTrue(standard instanceof ChineseLexicon);
    assertTrue(character instanceof ChineseCharacterBasedLexicon);
  }

  @Test
  public void dependencyGrammarExtractorChangesForSimilarWordSmoothing() {
    ChineseTreebankParserParams ordinaryParams = new ChineseTreebankParserParams();
    Extractor<DependencyGrammar> ordinary = ordinaryParams.dependencyGrammarExtractor(
        new Options(ordinaryParams), new HashIndex<String>(), new HashIndex<String>());
    ChineseTreebankParserParams smoothedParams = new ChineseTreebankParserParams();
    smoothedParams.setOptionFlag(new String[] {"-similarWordSmoothing"}, 0);
    Extractor<DependencyGrammar> smoothed = smoothedParams.dependencyGrammarExtractor(
        new Options(smoothedParams), new HashIndex<String>(), new HashIndex<String>());

    assertEquals(MLEDependencyGrammarExtractor.class, ordinary.getClass());
    assertNotEquals(MLEDependencyGrammarExtractor.class, smoothed.getClass());
    assertTrue(smoothed instanceof MLEDependencyGrammarExtractor);
  }

  @Test
  public void defaultSentenceHasExpectedWordsAndIndependentLists() {
    ChineseTreebankParserParams params = new ChineseTreebankParserParams();
    ArrayList<Word> first = params.defaultTestSentence();
    ArrayList<Word> second = params.defaultTestSentence();

    assertEquals(6, first.size());
    assertEquals("\u951f\u65a4\u62f7", first.get(0).word());
    assertEquals("\u5b66\u6821", first.get(2).word());
    assertEquals("\u5b66\u4e60", first.get(4).word());
    assertEquals(first, second);
    assertNotSame(first, second);
  }

  @Test
  public void transformTreeReturnsNullAndLeavesUnchanged() {
    ChineseTreebankParserParams params = new ChineseTreebankParserParams();
    Tree leaf = Tree.valueOf("(NN \u5b66)").firstChild();

    assertNull(params.transformTree(null, null));
    assertSame(leaf, params.transformTree(leaf, null));
    assertEquals("\u5b66", leaf.value());
  }

  @Test
  public void punctuationIsSplitIntoChineseSpecificTags() {
    ChineseTreebankParserParams params = new ChineseTreebankParserParams();
    Tree commaTree = Tree.valueOf("(ROOT (PU \uFF0C))");
    Tree comma = commaTree.firstChild();
    CoreLabel commaLabel = (CoreLabel) comma.label();
    commaLabel.setValue("PU");
    commaLabel.setTag("PU");
    commaLabel.setWord("\uFF0C");
    Tree douTree = Tree.valueOf("(ROOT (PU \u3001))");
    Tree dou = douTree.firstChild();
    CoreLabel douLabel = (CoreLabel) dou.label();
    douLabel.setValue("PU");
    douLabel.setTag("PU");
    douLabel.setWord("\u3001");
    Tree periodTree = Tree.valueOf("(ROOT (PU \u3002))");
    Tree period = periodTree.firstChild();
    CoreLabel periodLabel = (CoreLabel) period.label();
    periodLabel.setValue("PU");
    periodLabel.setTag("PU");
    periodLabel.setWord("\u3002");

    params.transformTree(comma, commaTree);
    params.transformTree(dou, douTree);
    params.transformTree(period, periodTree);

    assertEquals("PU-COMMA", comma.value());
    assertEquals("PU-DOU", dou.value());
    assertEquals("PU-ENDSENT", period.value());
  }

  @Test
  public void leftRightPunctuationOptionDistinguishesQuotesAndParentheses() {
    ChineseTreebankParserParams params = new ChineseTreebankParserParams();
    params.setOptionFlag(new String[] {"-chineseSplitPunctLR"}, 0);
    Tree leftQuoteTree = Tree.valueOf("(ROOT (PU \u201c))");
    Tree leftQuote = leftQuoteTree.firstChild();
    CoreLabel leftQuoteLabel = (CoreLabel) leftQuote.label();
    leftQuoteLabel.setValue("PU");
    leftQuoteLabel.setTag("PU");
    leftQuoteLabel.setWord("\u201c");
    Tree rightParenTree = Tree.valueOf("(ROOT (PU \uFF09))");
    Tree rightParen = rightParenTree.firstChild();
    CoreLabel rightParenLabel = (CoreLabel) rightParen.label();
    rightParenLabel.setValue("PU");
    rightParenLabel.setTag("PU");
    rightParenLabel.setWord("\uFF09");

    params.transformTree(leftQuote, leftQuoteTree);
    params.transformTree(rightParen, rightParenTree);

    assertEquals("PU-LQUOTE", leftQuote.value());
    assertEquals("PU-RPAREN", rightParen.value());
  }

  @Test
  public void defaultTagAnnotationsUseSiblingAndGrandparentContext() {
    ChineseTreebankParserParams params = new ChineseTreebankParserParams();
    Tree verbTree = Tree.valueOf("(ROOT (VP (VV \u8bf4) (IP (NP (NN \u8bdd)))))");
    Tree verb = verbTree.firstChild().firstChild();
    CoreLabel verbLabel = (CoreLabel) verb.label();
    verbLabel.setValue("VV");
    verbLabel.setTag("VV");
    verbLabel.setWord("\u8bf4");
    Tree adTree = Tree.valueOf("(ROOT (IP (VP (AD \u5f88) (VV \u597d))))");
    Tree ad = adTree.firstChild().firstChild().firstChild();
    CoreLabel adLabel = (CoreLabel) ad.label();
    adLabel.setValue("AD");
    adLabel.setTag("AD");
    adLabel.setWord("\u5f88");

    params.transformTree(verb, verbTree);
    params.transformTree(ad, adTree);

    assertEquals("VV-IP", verb.value());
    assertEquals("AD~IP", ad.value());
  }

  @Test
  public void vanillaBundleDisablesDefaultTagAndPhraseAnnotations() {
    ChineseTreebankParserParams params = new ChineseTreebankParserParams();
    assertEquals(1, params.setOptionFlag(new String[] {"-vanilla"}, 0));
    Tree tree = Tree.valueOf("(ROOT (VP (VV \u8bf4) (IP (NP (NN \u8bdd)))))");
    Tree verb = tree.firstChild().firstChild();
    CoreLabel verbLabel = (CoreLabel) verb.label();
    verbLabel.setValue("VV");
    verbLabel.setTag("VV");
    verbLabel.setWord("\u8bf4");
    Tree vp = tree.firstChild();

    params.transformTree(verb, tree);
    params.transformTree(vp, tree);

    assertEquals("VV", verb.value());
    assertEquals("VP", vp.value());
  }

  @Test
  public void vpSplitClassifiesComplementCoordinateAndAdjunctStructures() {
    ChineseTreebankParserParams params = new ChineseTreebankParserParams();
    Tree complementTree = Tree.valueOf("(ROOT (VP (VV \u5b66) (NP (NN \u4e2d\u6587))))");
    Tree coordinateTree = Tree.valueOf("(ROOT (VP (VP (VV \u5b66)) (CC \u548c) (VP (VV \u6559))))");
    Tree adjunctTree = Tree.valueOf("(ROOT (VP (ADVP (AD \u5feb)) (NP (NN \u4e66))))");

    params.transformTree(complementTree.firstChild(), complementTree);
    params.transformTree(coordinateTree.firstChild(), coordinateTree);
    params.transformTree(adjunctTree.firstChild(), adjunctTree);

    assertEquals("VP-COMP", complementTree.firstChild().value());
    assertEquals("VP-CRD", coordinateTree.firstChild().value());
    assertEquals("VP-ADJT", adjunctTree.firstChild().value());
  }

  @Test
  public void phraseAnnotationsCaptureBaDecAndVerbContexts() {
    ChineseTreebankParserParams params = new ChineseTreebankParserParams();
    Tree baTree = Tree.valueOf("(ROOT (VP (BA \u628a) (IP (NP (NN \u4e66)))))");
    Tree baIp = baTree.firstChild().getChild(1);
    Tree verbTree = Tree.valueOf("(ROOT (VP (VV \u8bf4) (IP (NP (NN \u8bdd)))))");
    Tree verbIp = verbTree.firstChild().getChild(1);
    Tree decTree = Tree.valueOf("(ROOT (NP (IP (VP (VV \u5199))) (DEC \u7684)))");
    Tree decIp = decTree.firstChild().firstChild();

    params.transformTree(baIp, baTree);
    params.transformTree(verbIp, verbTree);
    params.transformTree(decIp, decTree);

    assertEquals("IP=BA", baIp.value());
    assertEquals("IP=VVP", verbIp.value());
    assertEquals("IP=DEC", decIp.value());
  }

  @Test
  public void nounPhraseAnnotationsDifferentiateModifierModifiedAndConjunct() {
    ChineseTreebankParserParams params = new ChineseTreebankParserParams();
    Tree modifierTree = Tree.valueOf("(ROOT (NP (NP (NN \u4e2d\u6587)) (NP (NN \u4e66))))");
    Tree modifier = modifierTree.firstChild().firstChild();
    Tree modified = modifierTree.firstChild().getChild(1);
    Tree conjunctionTree = Tree.valueOf("(ROOT (NP (NP (NN \u7532)) (CC \u548c) (NP (NN \u4e59))))");
    Tree conjunct = conjunctionTree.firstChild().firstChild();

    params.transformTree(modifier, modifierTree);
    params.transformTree(modified, modifierTree);
    params.transformTree(conjunct, conjunctionTree);

    assertEquals("NP=MODIFIERNP", modifier.value());
    assertEquals("NP=MODIFIEDNP", modified.value());
    assertEquals("NP=MODIFIERNP=CONJ", conjunct.value());
  }

  @Test
  public void optInUnaryRootBaseAndConjunctionAnnotationsCompose() {
    ChineseTreebankParserParams params = new ChineseTreebankParserParams();
    params.setOptionFlag(new String[] {"-unaryIP"}, 0);
    params.setOptionFlag(new String[] {"-paRootDtr"}, 0);
    params.setOptionFlag(new String[] {"-markCC"}, 0);
    Tree unaryTree = Tree.valueOf("(ROOT (IP (VP (VV \u5b66))))");
    Tree conjunctionTree = Tree.valueOf("(ROOT (NP (NN \u7532) (CC \u548c) (NN \u4e59)))");

    params.transformTree(unaryTree.firstChild(), unaryTree);
    params.transformTree(conjunctionTree.firstChild(), conjunctionTree);

    assertEquals("IP^ROOT-U", unaryTree.firstChild().value());
    assertEquals("NP^ROOT-CC", conjunctionTree.firstChild().value());
  }

  @Test
  public void baseNpAndDominatesVerbOptionsAnnotatePhrases() {
    ChineseTreebankParserParams params = new ChineseTreebankParserParams();
    params.setOptionFlag(new String[] {"-baseNP"}, 0);
    params.setOptionFlag(new String[] {"-dominatesV"}, 0);
    Tree npTree = Tree.valueOf("(ROOT (NP (NN \u4e66)))");
    Tree vpTree = Tree.valueOf("(ROOT (VP (VV \u5b66)))");

    params.transformTree(npTree.firstChild(), npTree);
    params.transformTree(vpTree.firstChild(), vpTree);

    assertEquals("NP-B", npTree.firstChild().value());
    assertEquals("VP-v-COMP", vpTree.firstChild().value());
  }

  @Test
  public void optionParsingIsCaseInsensitiveAndConsumesCorrectNumberOfTokens() {
    ChineseTreebankParserParams params = new ChineseTreebankParserParams();

    assertEquals(2, params.setOptionFlag(new String[] {"-ChInEsEsPlItVp", "1"}, 0));
    assertEquals(3, params.setOptionFlag(new String[] {"ignored", "-lengthPenalty", "2.75"}, 1));
    assertEquals(3, params.setOptionFlag(new String[] {"ignored", "-penaltyType", "2"}, 1));
    assertEquals(0, params.setOptionFlag(new String[] {"-unknownOption"}, 0));
    assertEquals(0, params.setOptionFlag(new String[] {"-lengthPenalty"}, 0));
  }

  @Test(expected = NumberFormatException.class)
  public void numericOptionRejectsMalformedValue() {
    ChineseTreebankParserParams params = new ChineseTreebankParserParams();
    params.setOptionFlag(new String[] {"-chineseSplitVP", "not-a-number"}, 0);
  }

  @Test(expected = ArrayIndexOutOfBoundsException.class)
  public void unguardedBooleanOptionWithoutValueFailsFast() {
    ChineseTreebankParserParams params = new ChineseTreebankParserParams();
    params.setOptionFlag(new String[] {"-markVPadjunct"}, 0);
  }

  @Test
  public void unreadableDependencyFileIsWrappedForBothDependencyModes() {
    ChineseTreebankParserParams params = new ChineseTreebankParserParams();
    try {
      params.readGrammaticalStructureFromFile("missing-dependency-file.conllx");
      fail("Expected the universal dependency reader to fail");
    } catch (edu.stanford.nlp.io.RuntimeIOException expected) {
      assertNotNull(expected.getCause());
    }
    params.setGenerateOriginalDependencies(true);
    try {
      params.readGrammaticalStructureFromFile("missing-original-dependency-file.conllx");
      fail("Expected the original dependency reader to fail");
    } catch (edu.stanford.nlp.io.RuntimeIOException expected) {
      assertNotNull(expected.getCause());
    }
  }
}
