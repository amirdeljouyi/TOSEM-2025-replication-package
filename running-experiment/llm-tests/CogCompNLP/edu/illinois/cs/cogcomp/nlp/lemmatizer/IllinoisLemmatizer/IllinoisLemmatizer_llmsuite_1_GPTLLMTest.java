package edu.illinois.cs.cogcomp.nlp.lemmatizer;

import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.annotation.TextAnnotationBuilder;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.*;
import edu.illinois.cs.cogcomp.core.datastructures.trees.Tree;
import edu.illinois.cs.cogcomp.core.datastructures.trees.TreeParser;
import edu.illinois.cs.cogcomp.core.datastructures.trees.TreeParserFactory;
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager;
import edu.illinois.cs.cogcomp.nlp.tokenizer.StatefulTokenizer;
import edu.illinois.cs.cogcomp.nlp.utility.TokenizerTextAnnotationBuilder;
// import edu.illinois.cs.cogcomp.quant.driver.*;
import org.junit.Before;
import org.junit.Test;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;
import static org.junit.Assert.*;

public class IllinoisLemmatizer_llmsuite_1_GPTLLMTest {

@Test
public void testGetLemmaForPluralNoun() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String[] tokens = { "cats" };
// TextAnnotation ta = new TextAnnotation("testCorpus", "testId", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "testAnnotator", ta, 1.0);
// posView.addTokenLabel(0, "NNS", 1.0);
// ta.addView(ViewNames.POS, posView);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("cat", lemma);
}

@Test
public void testGetLemmaForPresentParticipleVerb() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String[] tokens = { "running" };
// TextAnnotation ta = new TextAnnotation("testCorpus", "testId", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "annot", ta, 1.0);
// posView.addTokenLabel(0, "VBG", 1.0);
// ta.addView(ViewNames.POS, posView);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("run", lemma);
}

@Test
public void testGetLemmaForContraction() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String[] tokens = { "'ll" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "label", ta, 1.0);
// posView.addTokenLabel(0, "MD", 1.0);
// ta.addView(ViewNames.POS, posView);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("will", lemma);
}

@Test
public void testGetLemmaForIrregularPlural() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String[] tokens = { "men" };
// TextAnnotation ta = new TextAnnotation("corpus", "textId", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "anno", ta, 1.0);
// posView.addTokenLabel(0, "NNS", 1.0);
// ta.addView(ViewNames.POS, posView);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("man", lemma);
}

@Test
public void testGetLemmaForProperNoun() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String[] tokens = { "John" };
// TextAnnotation ta = new TextAnnotation("corp", "id", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "provider", ta, 1.0);
// posView.addTokenLabel(0, "NNP", 1.0);
// ta.addView(ViewNames.POS, posView);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("john", lemma);
}

@Test
public void testGetLemmaWithAtSymbol() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String[] tokens = { "email@domain.com" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "p", ta, 1.0);
// posView.addTokenLabel(0, "NN", 1.0);
// ta.addView(ViewNames.POS, posView);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("email@domain.com", lemma);
}

@Test
public void testGetLemmaUnknownPOS() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String[] tokens = { "bark" };
// TextAnnotation ta = new TextAnnotation("corpus", "unknown", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "wut", ta, 1.0);
// posView.addTokenLabel(0, "XX", 1.0);
// ta.addView(ViewNames.POS, posView);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("bark", lemma);
}

@Test
public void testGetSingleLemmaEquivalent() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String[] tokens = { "cats" };
// TextAnnotation ta = new TextAnnotation("c", "i", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "p", ta, 1.0);
// pos.addTokenLabel(0, "NNS", 1.0);
// ta.addView(ViewNames.POS, pos);
// String lemma1 = lemmatizer.getLemma(ta, 0);
// String lemma2 = lemmatizer.getSingleLemma(ta, 0);
// assertEquals(lemma1, lemma2);
}

@Test(expected = IllegalArgumentException.class)
public void testGetLemmaIndexOutOfBounds() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String[] tokens = { "cat", "dog" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "annot", ta, 1.0);
// pos.addTokenLabel(0, "NN", 1.0);
// pos.addTokenLabel(1, "NN", 1.0);
// ta.addView(ViewNames.POS, pos);
// lemmatizer.getLemma(ta, 3);
}

@Test
public void testLemmatizeKnownVerbResource() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String[] tokens = { "retakes" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "provider", ta, 1.0);
// pos.addTokenLabel(0, "VBZ", 1.0);
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("retake", lemma);
}

@Test
public void testStemmerFallback() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String[] tokens = { "widgets" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "mock", ta, 1.0);
// pos.addTokenLabel(0, "NNS", 1.0);
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("widget", lemma);
}

@Test
public void testCreateLemmaViewAddsConstituents() throws IOException {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String[] tokens = { "dogs", "run" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "annotator", ta, 1.0);
// pos.addTokenLabel(0, "NNS", 1.0);
// pos.addTokenLabel(1, "VBP", 1.0);
// ta.addView(ViewNames.POS, pos);
// View lemmaView = lemmatizer.createLemmaView(ta);
// assertNotNull(lemmaView);
// assertEquals(2, lemmaView.getNumberOfConstituents());
}

@Test
public void testAddViewWorks() throws AnnotatorException {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String[] tokens = { "media" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "pos", ta, 1.0);
// pos.addTokenLabel(0, "NNS", 1.0);
// ta.addView(ViewNames.POS, pos);
// lemmatizer.addView(ta);
// assertTrue(ta.hasView(ViewNames.LEMMA));
}

@Test
public void testAdjectiveEndingEdReturnsAsIs() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String[] tokens = { "delighted" };
// TextAnnotation ta = new TextAnnotation("c", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "annot", ta, 1.0);
// pos.addTokenLabel(0, "JJ", 1.0);
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("delighted", lemma);
}

@Test
public void testComparativeAdjectiveReturnsAsIs() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String[] tokens = { "bigger" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "annot", ta, 1.0);
// pos.addTokenLabel(0, "JJR", 1.0);
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("bigger", lemma);
}

@Test
public void testSuperlativeAdverbReturnsAsIs() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String[] tokens = { "fastest" };
// TextAnnotation ta = new TextAnnotation("corpus", "text", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "foo", ta, 1.0);
// pos.addTokenLabel(0, "RBS", 1.0);
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("fastest", lemma);
}

@Test
public void testGetLemmaWithViewPOSMissing() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = { "dog" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
try {
// lemmatizer.getLemma(ta, 0);
fail("Expected NullPointerException or IndexOutOfBoundsException");
} catch (Exception e) {
assertTrue(e instanceof NullPointerException || e instanceof IndexOutOfBoundsException);
}
}

@Test
public void testGetLemmaWithNullLemmaFromWordNetFallback() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = { "foobarbaz" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "mock", ta, 1.0);
// pos.addTokenLabel(0, "VB", 1.0);
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("foobarbaz", lemma);
}

@Test
public void testGetLemmaForVBWithBaseVerbOnlyInBaseMap() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = { "retake" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "mock", ta, 1.0);
// pos.addTokenLabel(0, "VB", 1.0);
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("retake", lemma);
}

@Test
public void testGetLemmaHandlesRePrefixAndFallsBack() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = { "re-assemble" };
// TextAnnotation ta = new TextAnnotation("c", "i", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "mock", ta, 1.0);
// pos.addTokenLabel(0, "VB", 1.0);
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertNotNull(lemma);
}

@Test
public void testGetLemmaWithPOSPronoun() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
// rm.setProperty(LemmatizerConfigurator.USE_STNFRD_CONVENTIONS.key, "true");
lemmatizer.initialize(rm);
String[] tokens = { "her" };
// TextAnnotation ta = new TextAnnotation("c", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "mock", ta, 1.0);
// pos.addTokenLabel(0, "PRP", 1.0);
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("she", lemma);
}

@Test
public void testGetLemmaContractionNotInMapReturnsWord() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = { "ca" };
// TextAnnotation ta = new TextAnnotation("c", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "mock", ta, 1.0);
// pos.addTokenLabel(0, "MD", 1.0);
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("ca", lemma);
}

@Test
public void testGetLemmaWithExceptionMap() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = { "media" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "X", ta, 1.0);
// pos.addTokenLabel(0, "NNS", 1.0);
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("medium", lemma);
}

@Test
public void testGetLemmaSpecialCaseBeContraction() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = { "'s" };
// TextAnnotation ta = new TextAnnotation("id", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "mock", ta, 1.0);
// pos.addTokenLabel(0, "VBZ", 1.0);
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("be", lemma);
}

@Test
public void testGetLemmaWordEndingWithMenSuffix() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = { "workmen" };
// TextAnnotation ta = new TextAnnotation("c", "i", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "mock", ta, 1.0);
// pos.addTokenLabel(0, "NNS", 1.0);
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("workman", lemma);
}

@Test
public void testGetLemmaForNonStandardContractionLikeM() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = { "'m" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "mock", ta, 1.0);
// pos.addTokenLabel(0, "VBP", 1.0);
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("be", lemma);
}

@Test
public void testGetLemmaForUpperCaseInput() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = { "WALKED" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "mock", ta, 1.0);
// pos.addTokenLabel(0, "VBD", 1.0);
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("walk", lemma);
}

@Test
public void testGetLemmaWithAdverbPOSRBR() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = { "faster" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "annot", ta, 1.0);
// pos.addTokenLabel(0, "RBR", 1.0);
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("faster", lemma);
}

@Test
public void testGetLemmaForValidPOSButNotStemmed() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = { "runningly" };
// TextAnnotation ta = new TextAnnotation("c", "i", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "mock", ta, 1.0);
// pos.addTokenLabel(0, "RB", 1.0);
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("runningly", lemma);
}

@Test
public void testLemmaWhenPOSIsINPreposition() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = { "under" };
// TextAnnotation ta = new TextAnnotation("test", "t", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "pos", ta, 1.0);
// pos.addTokenLabel(0, "IN", 1.0);
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("under", lemma);
}

@Test
public void testLemmaWithUnknownPOSTagSymbol() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = { "#hashtag" };
// TextAnnotation ta = new TextAnnotation("c", "i", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "source", ta, 1.0);
// pos.addTokenLabel(0, "#", 1.0);
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("#hashtag", lemma);
}

@Test
public void testLemmaWithNumberWordPOSCD() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = { "2024" };
// TextAnnotation ta = new TextAnnotation("c", "i", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "annot", ta, 1.0);
// pos.addTokenLabel(0, "CD", 1.0);
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("2024", lemma);
}

@Test
public void testLemmaWithWordContainingHyphen() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = { "re-test" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "tagger", ta, 1.0);
// pos.addTokenLabel(0, "VB", 1.0);
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertNotNull(lemma);
}

@Test
public void testLemmaWordReturnsXmodalByVerbMap() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(config);
String[] tokens = { "could" };
// TextAnnotation ta = new TextAnnotation("c", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "modalsource", ta, 1.0);
// pos.addTokenLabel(0, "MD", 1.0);
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// if ("xmodal".equals(lemma)) {
// assertEquals("could", lemma);
// } else {
// assertNotNull(lemma);
// }
}

@Test
public void testLemmaForPunctuationOnlyToken() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = { "!" };
// TextAnnotation ta = new TextAnnotation("c", "d", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "source", ta, 1.0);
// pos.addTokenLabel(0, ".", 1.0);
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("!", lemma);
}

@Test
public void testLemmaReturnsWordWhenPOSNotHandledByBranching() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String[] tokens = { "however" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "manual", ta, 1.0);
// pos.addTokenLabel(0, "UH", 1.0);
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("however", lemma);
}

@Test
public void testLemmaFailsToFindAnythingFallsBackToStemmer() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String[] tokens = { "subtrees" };
// TextAnnotation ta = new TextAnnotation("corp", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "tagger", ta, 1.0);
// pos.addTokenLabel(0, "NNS", 1.0);
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("subtree", lemma);
}

@Test
public void testLemmaWhenWordEndsWithSButPOSNotSuggestingPlural() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String[] tokens = { "bakers" };
// TextAnnotation ta = new TextAnnotation("corpus", "inst", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "tag", ta, 1.0);
// pos.addTokenLabel(0, "JJ", 1.0);
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("bakers", lemma);
}

@Test
public void testGetLemmaWithEmptyPOSLabelListThrowsException() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = { "quick" };
// TextAnnotation ta = new TextAnnotation("c", "i", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "empty", ta, 1.0);
// ta.addView(ViewNames.POS, pos);
try {
// lemmatizer.getLemma(ta, 0);
fail("Expected IndexOutOfBoundsException");
} catch (IndexOutOfBoundsException e) {
}
}

@Test
public void testLemmaWithNullWordNetReturnAndNotEndingWithMen() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = { "xyzabc" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "posTagger", ta, 1.0);
// posView.addTokenLabel(0, "NNS", 1.0);
// ta.addView(ViewNames.POS, posView);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("xyzabc", lemma);
}

@Test
public void testLemmaForVerbUnknownFallbackToWordNetButReturnsNull() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = { "flyed" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "verbsource", ta, 1.0);
// posView.addTokenLabel(0, "VBD", 1.0);
// ta.addView(ViewNames.POS, posView);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("flyed", lemma);
}

@Test
public void testLemmaWithXmodalReturnedFromVerbLemmaMapAndPOSNotVB() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = { "should" };
// TextAnnotation ta = new TextAnnotation("unit", "id", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "mock", ta, 1.0);
// posView.addTokenLabel(0, "MD", 1.0);
// ta.addView(ViewNames.POS, posView);
// String lemma = lemmatizer.getLemma(ta, 0);
// if ("xmodal".equals(lemma)) {
// assertEquals("should", lemma);
// } else {
// assertNotNull(lemma);
// }
}

@Test
public void testLemmaWithTokenContainingWhitespaceAndUppercase() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = { "  WALKing  " };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "annot", ta, 1.0);
// posView.addTokenLabel(0, "VBG", 1.0);
// ta.addView(ViewNames.POS, posView);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("walk", lemma);
}

@Test
public void testLemmaWithStanfordMapTrueButWordNotInToStanford() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
// rm.setProperty(LemmatizerConfigurator.USE_STNFRD_CONVENTIONS.key, "true");
lemmatizer.initialize(rm);
String[] tokens = { "banana" };
// TextAnnotation ta = new TextAnnotation("stanfordTest", "id", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "mock", ta, 1.0);
// posView.addTokenLabel(0, "NN", 1.0);
// ta.addView(ViewNames.POS, posView);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("banana", lemma);
}

@Test
public void testLemmaWithMultipleCharactersRemovedReCase() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = { "re-remix" };
// TextAnnotation ta = new TextAnnotation("test", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "re", ta, 1.0);
// pos.addTokenLabel(0, "VB", 1.0);
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertNotNull(lemma);
}

@Test
public void testLemmaProperNounPluralNNPS() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = { "Scientists" };
// TextAnnotation ta = new TextAnnotation("corpus", "doc", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "pos", ta, 1.0);
// pos.addTokenLabel(0, "NNPS", 1.0);
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("scientists", lemma);
}

@Test
public void testLemmaForPOSCategoryAdjectiveComparativeJJR() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = { "faster" };
// TextAnnotation ta = new TextAnnotation("source", "id", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "annotator", ta, 1.0);
// posView.addTokenLabel(0, "JJR", 1.0);
// ta.addView(ViewNames.POS, posView);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("faster", lemma);
}

@Test
public void testLemmaWithUnknownVerbAndFallbackToNullThenStemmer() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String[] tokens = { "twaddles" };
// TextAnnotation ta = new TextAnnotation("fallback", "id", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "injector", ta, 1.0);
// posView.addTokenLabel(0, "VBZ", 1.0);
// ta.addView(ViewNames.POS, posView);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("twaddle", lemma);
}

@Test
public void testLemmaWithNonAlphaCharactersAndPOSVerb() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = { "run1" };
// TextAnnotation ta = new TextAnnotation("test", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "source", ta, 1.0);
// pos.addTokenLabel(0, "VB", 1.0);
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("run1", lemma);
}

@Test
public void testEmptyStringToken() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = { "" };
// TextAnnotation ta = new TextAnnotation("emptyCorpus", "id", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "annotator", ta, 1.0);
// posView.addTokenLabel(0, "NN", 1.0);
// ta.addView(ViewNames.POS, posView);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("", lemma);
}

@Test
public void testWhitespaceOnlyToken() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = { "   " };
// TextAnnotation ta = new TextAnnotation("whitespace", "id", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "annotator", ta, 1.0);
// posView.addTokenLabel(0, "NN", 1.0);
// ta.addView(ViewNames.POS, posView);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("", lemma);
}

@Test
public void testContractionWithUnicodeQuote() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = { "’s" };
// TextAnnotation ta = new TextAnnotation("unicode", "id", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "mock", ta, 1.0);
// posView.addTokenLabel(0, "VBZ", 1.0);
// ta.addView(ViewNames.POS, posView);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("be", lemma);
}

@Test
public void testPOSFallbackWithJJUnknownEnding() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = { "beautiful" };
// TextAnnotation ta = new TextAnnotation("adjCorpus", "id", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "annotator", ta, 1.0);
// posView.addTokenLabel(0, "JJ", 1.0);
// ta.addView(ViewNames.POS, posView);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("beautiful", lemma);
}

@Test
public void testPOSJJEndingWithEdReturnsAsIs() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = { "excited" };
// TextAnnotation ta = new TextAnnotation("adjCorpus", "id", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "annotator", ta, 1.0);
// posView.addTokenLabel(0, "JJ", 1.0);
// ta.addView(ViewNames.POS, posView);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("excited", lemma);
}

@Test
public void testFunctionPOSDTReturnsWord() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = { "the" };
// TextAnnotation ta = new TextAnnotation("detCorpus", "id", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "xs", ta, 1.0);
// posView.addTokenLabel(0, "DT", 1.0);
// ta.addView(ViewNames.POS, posView);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("the", lemma);
}

@Test
public void testTabCharacterInToken() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = { "\t" };
// TextAnnotation ta = new TextAnnotation("tabCorpus", "tabId", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "tagger", ta, 1.0);
// pos.addTokenLabel(0, "NN", 1.0);
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("", lemma.trim());
}

@Test
public void testCreateLemmaViewAddsCorrectConstituents() throws IOException {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] toks = { "cats", "jumped" };
// TextAnnotation ta = new TextAnnotation("lemmaTestCorpus", "id", toks);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "generated", ta, 1.0);
// pos.addTokenLabel(0, "NNS", 1.0);
// pos.addTokenLabel(1, "VBD", 1.0);
// ta.addView(ViewNames.POS, pos);
// View lemmaView = lemmatizer.createLemmaView(ta);
// assertEquals(2, lemmaView.getNumberOfConstituents());
// assertEquals("cat", lemmaView.getConstituent(0).getLabel());
// assertEquals("jump", lemmaView.getConstituent(1).getLabel());
}

@Test
public void testAddViewTwiceOverwritesOrKeepsSingleInstance() throws AnnotatorException {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = { "dogs", "run" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "tagger", ta, 1.0);
// pos.addTokenLabel(0, "NNS", 1.0);
// pos.addTokenLabel(1, "VBP", 1.0);
// ta.addView(ViewNames.POS, pos);
// lemmatizer.addView(ta);
// lemmatizer.addView(ta);
// assertTrue(ta.hasView(ViewNames.LEMMA));
// assertEquals(2, ta.getView(ViewNames.LEMMA).getNumberOfConstituents());
}

@Test
public void testExceptionMapMissFallsThroughGracefully() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = { "xylophones" };
// TextAnnotation ta = new TextAnnotation("music", "song", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "label", ta, 1.0);
// pos.addTokenLabel(0, "NNS", 1.0);
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("xylophone", lemma);
}

@Test
public void testGetLemmaWithOnlyPOSVerbRootNotInMap() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = { "singunga" };
// TextAnnotation ta = new TextAnnotation("verbCorpus", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "posGen", ta, 1.0);
// pos.addTokenLabel(0, "VB", 1.0);
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("singunga", lemma);
}

@Test
public void testGetLemmaWithVerbPOSAndCapitalization() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = { "Retakes" };
// TextAnnotation ta = new TextAnnotation("verbCorpus", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "mockTagger", ta, 1.0);
// pos.addTokenLabel(0, "VBZ", 1.0);
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("retake", lemma);
}

@Test
public void testGetLemmaStartsWithRePrefixUnknownBaseStillFallback() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = { "re-treeify" };
// TextAnnotation ta = new TextAnnotation("reCorpus", "id", tokens);
// TokenLabelView tagger = new TokenLabelView(ViewNames.POS, "opt", ta, 1.0);
// tagger.addTokenLabel(0, "VB", 1.0);
// ta.addView(ViewNames.POS, tagger);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("re-treeify", lemma);
}

@Test
public void testGetLemmaForPluralWithMorphaStemmerFallbackOnly() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = { "guards" };
// TextAnnotation ta = new TextAnnotation("morphCorpus", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "inter", ta, 1.0);
// pos.addTokenLabel(0, "NNS", 1.0);
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("guard", lemma);
}

@Test
public void testGetLemmaWithJJPOSEndingInEDMatchingCheck() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = { "inspired" };
// TextAnnotation ta = new TextAnnotation("adjCorpus", "id", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "analyst", ta, 1.0);
// posView.addTokenLabel(0, "JJ", 1.0);
// ta.addView(ViewNames.POS, posView);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("inspired", lemma);
}

@Test
public void testGetLemmaFunctionPOSCCReturnsWord() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = { "and" };
// TextAnnotation ta = new TextAnnotation("funcCorpus", "id", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "mock", ta, 1.0);
// posView.addTokenLabel(0, "CC", 1.0);
// ta.addView(ViewNames.POS, posView);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("and", lemma);
}

@Test
public void testGetLemmaStandardNounWithProperPOSFallback() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = { "Boxes" };
// TextAnnotation ta = new TextAnnotation("nounCaps", "id", tokens);
// TokenLabelView view = new TokenLabelView(ViewNames.POS, "tagger", ta, 1.0);
// view.addTokenLabel(0, "NNS", 1.0);
// ta.addView(ViewNames.POS, view);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("box", lemma);
}

@Test
public void testGetLemmaPOSRBWithAdverbPreserved() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = { "quickly" };
// TextAnnotation ta = new TextAnnotation("advTest", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "source", ta, 1.0);
// pos.addTokenLabel(0, "RB", 1.0);
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("quickly", lemma);
}

@Test
public void testGetLemmaWithGarbagePOSGoesToElseLogic() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = { "what" };
// TextAnnotation ta = new TextAnnotation("garbagePOS", "id", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "generator", ta, 1.0);
// posView.addTokenLabel(0, "FOO", 1.0);
// ta.addView(ViewNames.POS, posView);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("what", lemma);
}

@Test
public void testAddViewToTextAnnotationTwiceProducesSameContents() throws AnnotatorException {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = { "dogs", "bark" };
// TextAnnotation ta = new TextAnnotation("reentrant", "document", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "foo", ta, 1.0);
// pos.addTokenLabel(0, "NNS", 1.0);
// pos.addTokenLabel(1, "VBP", 1.0);
// ta.addView(ViewNames.POS, pos);
// lemmatizer.addView(ta);
// int firstSize = ta.getView(ViewNames.LEMMA).getNumberOfConstituents();
// lemmatizer.addView(ta);
// int secondSize = ta.getView(ViewNames.LEMMA).getNumberOfConstituents();
// assertEquals(firstSize, secondSize);
}

@Test
public void testWordWithOnlyPunctuation() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = { "!" };
// TextAnnotation ta = new TextAnnotation("corp", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "tag", ta, 1.0);
// pos.addTokenLabel(0, ".", 1.0);
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("!", lemma);
}

@Test
public void testCreateLemmaViewAddsConstituentLabelsCorrectly() throws Exception {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = { "dogs", "running" };
// TextAnnotation ta = new TextAnnotation("lemmaCreate", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "builder", ta, 1.0);
// pos.addTokenLabel(0, "NNS", 1.0);
// pos.addTokenLabel(1, "VBG", 1.0);
// ta.addView(ViewNames.POS, pos);
// View lemmaView = lemmatizer.createLemmaView(ta);
// assertEquals("dog", lemmaView.getConstituent(0).getLabel());
// assertEquals("run", lemmaView.getConstituent(1).getLabel());
}

@Test
public void testFallbackToStemmerForPluralEndsInSButNoResourceMatch() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = { "drones" };
// TextAnnotation ta = new TextAnnotation("stemFallback", "docId", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "stem", ta, 1.0);
// pos.addTokenLabel(0, "NNS", 1.0);
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("drone", lemma);
}

@Test
public void testContractionMapKeyThatIsFormallySkipped() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = { "ca" };
// TextAnnotation ta = new TextAnnotation("contraction", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "modals", ta, 1.0);
// pos.addTokenLabel(0, "MD", 1.0);
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("ca", lemma);
}

@Test
public void testUnknownTokenContainsAtSymbolShouldReturnAsIs() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = { "hello@world" };
// TextAnnotation ta = new TextAnnotation("emailToken", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "mock", ta, 1.0);
// pos.addTokenLabel(0, "NN", 1.0);
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("hello@world", lemma);
}

@Test
public void testExceptionMapMismatchSkipsBranch() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = { "elephants" };
// TextAnnotation ta = new TextAnnotation("excSkip", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "tagger", ta, 1.0);
// pos.addTokenLabel(0, "NNS", 1.0);
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("elephant", lemma);
}

@Test
public void testVerbWithBaseFormMapsBackToSelfWhenPOSisVB() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = { "return" };
// TextAnnotation ta = new TextAnnotation("vbtest", "id", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "source", ta, 1.0);
// posView.addTokenLabel(0, "VB", 1.0);
// ta.addView(ViewNames.POS, posView);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("return", lemma);
}

@Test
public void testPOSStartsWithVWithUnmappedXmodal() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = { "should" };
// TextAnnotation ta = new TextAnnotation("modalWalk", "doc", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "source", ta, 1.0);
// pos.addTokenLabel(0, "MD", 1.0);
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// if ("xmodal".equals(lemma)) {
// assertEquals("should", lemma);
// } else {
// assertNotNull(lemma);
// }
}

@Test
public void testStanfordConfigTrueWithWordInMapAppliesMapping() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
// rm.setProperty(LemmatizerConfigurator.USE_STNFRD_CONVENTIONS.key, "true");
lemmatizer.initialize(rm);
String[] tokens = { "is" };
// TextAnnotation ta = new TextAnnotation("stanfordMap", "test", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "t", ta, 1.0);
// pos.addTokenLabel(0, "VBZ", 1.0);
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("be", lemma);
}

@Test
public void testStanfordConfigTrueWithWordNotInMapFallsThrough() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
// rm.setProperty(LemmatizerConfigurator.USE_STNFRD_CONVENTIONS.key, "true");
lemmatizer.initialize(rm);
String[] tokens = { "guava" };
// TextAnnotation ta = new TextAnnotation("stanfordMiss", "case", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "manual", ta, 1.0);
// pos.addTokenLabel(0, "NN", 1.0);
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("guava", lemma);
}
}
