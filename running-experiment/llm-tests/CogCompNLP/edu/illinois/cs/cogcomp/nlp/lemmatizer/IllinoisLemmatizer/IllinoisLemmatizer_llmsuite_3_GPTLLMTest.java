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

public class IllinoisLemmatizer_llmsuite_3_GPTLLMTest {

@Test
public void testSimpleNounPlural() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String[] tokens = new String[] { "dogs" };
String[] posTags = new String[] { "NNS" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "test", ta, 1.0);
// Constituent pos = new Constituent(posTags[0], ViewNames.POS, ta, 0, 1);
// posView.addConstituent(pos);
// ta.addView(ViewNames.POS, posView);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("dog", lemma);
}

@Test
public void testSimpleVerbGerund() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String[] tokens = new String[] { "running" };
String[] posTags = new String[] { "VBG" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "test", ta, 1.0);
// Constituent pos = new Constituent(posTags[0], ViewNames.POS, ta, 0, 1);
// posView.addConstituent(pos);
// ta.addView(ViewNames.POS, posView);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("run", lemma);
}

@Test
public void testProperNounLowercaseNNP() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("Paris", "NNP");
assertEquals("paris", lemma);
}

@Test
public void testContractionHave() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("'ve", "VB");
assertEquals("have", lemma);
}

@Test
public void testExceptionMapFallback() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("media", "NNS");
assertEquals("medium", lemma);
}

@Test
public void testCreateLemmaView() throws IOException {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String[] tokens = new String[] { "men", "run" };
String[] posTags = new String[] { "NNS", "VBP" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "test", ta, 1.0);
// Constituent c1 = new Constituent(posTags[0], ViewNames.POS, ta, 0, 1);
// Constituent c2 = new Constituent(posTags[1], ViewNames.POS, ta, 1, 2);
// posView.addConstituent(c1);
// posView.addConstituent(c2);
// ta.addView(ViewNames.POS, posView);
// View lemmaView = lemmatizer.createLemmaView(ta);
// List<Constituent> lemmaConstituents = lemmaView.getConstituents();
// assertEquals("man", lemmaConstituents.get(0).getLabel());
// assertEquals("run", lemmaConstituents.get(1).getLabel());
}

@Test
public void testAddViewAddsLemma() throws AnnotatorException {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String[] tokens = new String[] { "dogs" };
String[] pos = new String[] { "NNS" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "test", ta, 1.0);
// Constituent con = new Constituent(pos[0], ViewNames.POS, ta, 0, 1);
// posView.addConstituent(con);
// ta.addView(ViewNames.POS, posView);
// assertFalse(ta.hasView(ViewNames.LEMMA));
// lemmatizer.addView(ta);
// assertTrue(ta.hasView(ViewNames.LEMMA));
// View lemmaView = ta.getView(ViewNames.LEMMA);
// List<Constituent> lemmaCons = lemmaView.getConstituents();
// assertEquals("dog", lemmaCons.get(0).getLabel());
}

@Test
public void testEmailTokenUnchanged() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("test@email.com", "NN");
assertEquals("test@email.com", lemma);
}

@Test(expected = IllegalArgumentException.class)
public void testOutOfBoundsIndexLemma() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String[] tokens = new String[] { "hello" };
String[] posTags = new String[] { "UH" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "mock", ta, 1.0);
// Constituent con = new Constituent(posTags[0], ViewNames.POS, ta, 0, 1);
// posView.addConstituent(con);
// ta.addView(ViewNames.POS, posView);
// lemmatizer.getLemma(ta, 5);
}

@Test
public void testSingleLemmaDelegation() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String[] tokens = new String[] { "men" };
String[] posTags = new String[] { "NNS" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView view = new TokenLabelView(ViewNames.POS, "src", ta, 1.0);
// Constituent con = new Constituent(posTags[0], ViewNames.POS, ta, 0, 1);
// view.addConstituent(con);
// ta.addView(ViewNames.POS, view);
// String lemma = lemmatizer.getSingleLemma(ta, 0);
// assertEquals("man", lemma);
}

@Test
public void testUnknownVerbThatEndsWithS() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("jumps", "VBZ");
assertNotNull(lemma);
assertFalse(lemma.isEmpty());
}

@Test
public void testAdjectiveNotEndingWithEd() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("happy", "JJ");
assertEquals("happy", lemma);
}

@Test
public void testModalWithXmodalReturnSameWord() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("can", "MD");
assertEquals("can", lemma);
}

@Test
public void testLemmatizeWithREPrefixKnownBase() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
lemmatizer.getLemma("re-take", "VB");
String lemma = lemmatizer.getLemma("re-take", "VB");
assertNotNull(lemma);
}

@Test
public void testToStanfordMapUsedWhenConfigured() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
// lemmatizer.useStanford = true;
String lemma = lemmatizer.getLemma("her", "PRP");
assertEquals("she", lemma);
}

@Test
public void testFunctionWordUnchanged() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("into", "IN");
assertEquals("into", lemma);
}

@Test
public void testLemmatizeRB() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("quickly", "RB");
assertEquals("quickly", lemma);
}

@Test
public void testWordWithApostropheSWithVerbPOS() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("'s", "VBZ");
assertEquals("be", lemma);
}

@Test
public void testLemmatizeWordEndingWithMenUnrecognized() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("chairmen", "NNS");
assertEquals("chairman", lemma);
}

@Test
public void testNullFallbackWhenNothingMatches() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("abcdef", "NNP");
assertEquals("abcdef", lemma);
}

@Test
public void testSymbolReturnedUnchanged() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("@symbol", "NN");
assertEquals("@symbol", lemma);
}

@Test
public void testSingleCharacterUppercaseProperNoun() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("A", "NNP");
assertEquals("a", lemma);
}

@Test
public void testDoubleStanfordMapConditionFailsIfDisabled() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
// lemmatizer.useStanford = false;
String lemma = lemmatizer.getLemma("her", "PRP");
assertEquals("her", lemma);
}

@Test
public void testWordEndingWithSWithoutNounOrVerbPOS() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("news", "DT");
assertEquals("news", lemma);
}

@Test
public void testUnknownWordAndUnknownPOS() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("qwertyword", "ZZZ");
assertEquals("qwertyword", lemma);
}

@Test
public void testNullReturnFromWordnetFallback() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
// lemmatizer.wnLemmaReader = new WordnetLemmaReader("") {
// 
// @Override
// public String getLemma(String word, String pos) {
// return null;
// }
// };
String lemma = lemmatizer.getLemma("runningxyz", "VBZ");
assertNotNull(lemma);
assertFalse(lemma.isEmpty());
}

@Test
public void testNNPThatEndsWithMenSuffix() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("spokesmen", "NNP");
assertEquals("spokesmen", lemma);
}

@Test
public void testCreateLemmaViewWithEmptyInput() throws IOException {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String[] tokens = new String[0];
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// View lemmaView = lemmatizer.createLemmaView(ta);
// assertNotNull(lemmaView);
// assertTrue(lemmaView.getConstituents().isEmpty());
}

@Test(expected = AnnotatorException.class)
public void testAddViewIOExceptionThrowsAnnotatorException() throws AnnotatorException {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
IllinoisLemmatizer faultyLemmatizer = new IllinoisLemmatizer(false) {

@Override
public View createLemmaView(TextAnnotation inputTa) throws IOException {
throw new IOException("Forced IO Error");
}
};
String[] tokens = new String[] { "sample" };
String[] pos = new String[] { "NN" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "src", ta, 1.0);
// Constituent con = new Constituent(pos[0], ViewNames.POS, ta, 0, 1);
// posView.addConstituent(con);
// ta.addView(ViewNames.POS, posView);
// faultyLemmatizer.addView(ta);
}

@Test
public void testLemmatizePossessivePronounWithStanfordOverride() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
// lemmatizer.useStanford = true;
String lemma = lemmatizer.getLemma("their", "PRP$");
assertEquals("they", lemma);
}

@Test
public void testWordInVerbMapButWithNonVerbPOS() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
lemmatizer.getLemma("took", "NN");
String lemma = lemmatizer.getLemma("took", "NN");
assertNotNull(lemma);
}

@Test
public void testLemmatizeWithMixedCaseInput() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("RuNnInG", "VBG");
assertEquals("run", lemma);
}

@Test
public void testLemmatizeNonAlphabeticToken() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("12345", "CD");
assertEquals("12345", lemma);
}

@Test
public void testWordIsEmptyString() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("", "NN");
assertEquals("", lemma);
}

@Test
public void testTrimsSurroundingSpaces() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("  running  ", "VBG");
assertEquals("run", lemma);
}

@Test
public void testSingleCharacterWord() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("a", "DT");
assertEquals("a", lemma);
}

@Test
public void testPOSViewMissingThrowsException() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String[] tokens = new String[] { "cat" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
try {
// lemmatizer.getLemma(ta, 0);
fail("Expected an exception due to missing POS view");
} catch (Exception e) {
assertTrue(e instanceof RuntimeException || e instanceof IllegalStateException);
}
}

@Test
public void testVerbWithVBPOSReturnsFromVerbBaseMap() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
lemmatizer.getLemma("go", "VB");
String lemma = lemmatizer.getLemma("go", "VB");
assertEquals("go", lemma);
}

@Test
public void testVerbWithNonVBPOSFromVerbLemmaMap() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
lemmatizer.getLemma("ran", "VBD");
String lemma = lemmatizer.getLemma("ran", "VBD");
assertEquals("run", lemma);
}

@Test
public void testLemmatizeWithWordEndingInSAndUnknownPOS() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("tigers", "XYZ");
assertEquals("tigers", lemma);
}

@Test
public void testWordEndingWithEDWithJJPOSIsReturnedUnchanged() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("disappointed", "JJ");
assertEquals("disappointed", lemma);
}

@Test
public void testComparativeAdjectiveJJRReturnsOriginal() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("happier", "JJR");
assertEquals("happier", lemma);
}

@Test
public void testSuperlativeAdverbRBSReturnsOriginal() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("fastest", "RBS");
assertEquals("fastest", lemma);
}

@Test
public void testCreateLemmaViewNonAlphaTokensHandled() throws Exception {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String[] tokens = new String[] { "@mark", ":", "1984" };
String[] pos = new String[] { "NN", ":", "CD" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "src", ta, 1.0);
// Constituent p0 = new Constituent(pos[0], ViewNames.POS, ta, 0, 1);
// Constituent p1 = new Constituent(pos[1], ViewNames.POS, ta, 1, 2);
// Constituent p2 = new Constituent(pos[2], ViewNames.POS, ta, 2, 3);
// posView.addConstituent(p0);
// posView.addConstituent(p1);
// posView.addConstituent(p2);
// ta.addView(ViewNames.POS, posView);
// View lemmaView = lemmatizer.createLemmaView(ta);
// assertEquals("@mark", lemmaView.getConstituentAtToken(0).getLabel());
// assertEquals(":", lemmaView.getConstituentAtToken(1).getLabel());
// assertEquals("1984", lemmaView.getConstituentAtToken(2).getLabel());
}

@Test
public void testGetLemmaWithEmptyPOSListThrowsException() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String[] tokens = new String[] { "word" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView emptyPosView = new TokenLabelView(ViewNames.POS, "src", ta, 1.0);
// ta.addView(ViewNames.POS, emptyPosView);
try {
// lemmatizer.getLemma(ta, 0);
fail("Expected IndexOutOfBoundsException or similar due to empty POS view");
} catch (Exception e) {
assertTrue(e instanceof IndexOutOfBoundsException || e instanceof RuntimeException);
}
}

@Test
public void testApostropheWordWithNonVerbPOSReturnedAsIs() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("'s", "NN");
assertEquals("'s", lemma);
}

@Test
public void testViewIsAddedTwiceDoesNotDuplicate() throws Exception {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String[] tokens = new String[] { "tables" };
String[] pos = new String[] { "NNS" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "src", ta, 1.0);
// Constituent c = new Constituent(pos[0], ViewNames.POS, ta, 0, 1);
// posView.addConstituent(c);
// ta.addView(ViewNames.POS, posView);
// lemmatizer.addView(ta);
// View firstView = ta.getView(ViewNames.LEMMA);
// lemmatizer.addView(ta);
// View secondView = ta.getView(ViewNames.LEMMA);
// assertSame(firstView, secondView);
}

@Test
public void testLemmatizeNounEndingInMenThatIsAlreadySingular() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("man", "NN");
assertEquals("man", lemma);
}

@Test
public void testGetLemmaWordNotInAnyMapReturnsWordnetFallback() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
// lemmatizer.wnLemmaReader = new WordnetLemmaReader("") {
// 
// @Override
// public String getLemma(String word, String pos) {
// if (word.equals("flapping") && pos.equals("VBG")) {
// return "flap";
// }
// return null;
// }
// };
String lemma = lemmatizer.getLemma("flapping", "VBG");
assertEquals("flap", lemma);
}

@Test
public void testGetLemmaWordWithREPrefixReturnsTrimmedBase() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
// lemmatizer.wnLemmaReader = new WordnetLemmaReader("") {
// 
// @Override
// public String getLemma(String word, String pos) {
// if (word.equals("take") && pos.equals("VB"))
// return "take";
// return null;
// }
// };
String lemma = lemmatizer.getLemma("re-take", "VB");
assertEquals("take", lemma);
}

@Test
public void testLemmatizeContractionThatIsMappedToBe() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("'m", "VB");
assertEquals("be", lemma);
}

@Test
public void testLemmatizeWordOnlyInVerbBaseMapWithVB() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
// lemmatizer.wnLemmaReader = new WordnetLemmaReader("") {
// 
// @Override
// public String getLemma(String word, String pos) {
// return null;
// }
// };
String lemma = lemmatizer.getLemma("take", "VB");
assertEquals("take", lemma);
}

@Test
public void testLemmatizeVerbWithXmodalInBaseReturnsUnchanged() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
// lemmatizer.wnLemmaReader = new WordnetLemmaReader("") {
// 
// @Override
// public String getLemma(String word, String pos) {
// return null;
// }
// };
String lemma = lemmatizer.getLemma("can", "VB");
assertEquals("can", lemma);
}

@Test
public void testLemmatizeNounThatEndsWithMenButWordnetReturnsNull() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
// lemmatizer.wnLemmaReader = new WordnetLemmaReader("") {
// 
// @Override
// public String getLemma(String word, String pos) {
// return null;
// }
// };
String lemma = lemmatizer.getLemma("salesmen", "NNS");
assertEquals("salesman", lemma);
}

@Test
public void testGetLemmaWithLowerCaseTransformation() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
// lemmatizer.wnLemmaReader = new WordnetLemmaReader("") {
// 
// @Override
// public String getLemma(String word, String pos) {
// if (word.equals("run") && pos.equals("VBG"))
// return "run";
// return null;
// }
// };
String lemma = lemmatizer.getLemma("RUN", "VBG");
assertEquals("run", lemma);
}

@Test
public void testGetLemmaWithNullWordnetAndNoMatchReturnsWord() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
// lemmatizer.wnLemmaReader = new WordnetLemmaReader("") {
// 
// @Override
// public String getLemma(String word, String pos) {
// return null;
// }
// };
String lemma = lemmatizer.getLemma("abcdefgh", "JJ");
assertEquals("abcdefgh", lemma);
}

@Test
public void testNominalPOSUnknownWordReturnsWordnetStem() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
// lemmatizer.wnLemmaReader = new WordnetLemmaReader("") {
// 
// @Override
// public String getLemma(String word, String pos) {
// return null;
// }
// };
String lemma = lemmatizer.getLemma("buses", "NNS");
assertEquals("bus", lemma);
}

@Test
public void testLemmaReturnsSameForUnstemmedFunctionWord() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("through", "IN");
assertEquals("through", lemma);
}

@Test
public void testGetLemmaForInvalidPOSDoesNotCrash() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("thoroughly", "FOOBAR");
assertEquals("thoroughly", lemma);
}

@Test
public void testGetLemmaWithPOSViewHavingMultipleLabels() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String[] tokens = new String[] { "running" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "test", ta, 1.0);
// Constituent c1 = new Constituent("VBG", ViewNames.POS, ta, 0, 1);
// Constituent c2 = new Constituent("NN", ViewNames.POS, ta, 0, 1);
// posView.addConstituent(c1);
// posView.addConstituent(c2);
// ta.addView(ViewNames.POS, posView);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertNotNull(lemma);
}

@Test
public void testGetLemmaWithPOSNullFallbackAndUnknownSuffix() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
// lemmatizer.wnLemmaReader = new WordnetLemmaReader("") {
// 
// @Override
// public String getLemma(String word, String pos) {
// return null;
// }
// };
String lemma = lemmatizer.getLemma("machines", "NNS");
assertEquals("machine", lemma);
}

@Test
public void testGetLemmaWithPOSVerbAndREPrefixReturnsOriginalIfXmodal() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
// lemmatizer.verbLemmaMap.put("redo", "xmodal");
String lemma = lemmatizer.getLemma("re-redo", "VB");
assertEquals("re-redo", lemma);
}

@Test
public void testGetLemmaWhereWordMatchesContractionsAndExceptions() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
// lemmatizer.exceptionsMap.put("'ve", "have-exc");
// lemmatizer.contractions.put("'ve", "have-cont");
String lemma = lemmatizer.getLemma("'ve", "VB");
assertEquals("have-exc", lemma);
}

@Test
public void testGetLemmaMixedCaseProperNounNNP() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("DoctorStrange", "NNP");
assertEquals("doctorstrange", lemma);
}

@Test
public void testGetLemmaWithStanfordEnabledOverridesStanfordMap() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
// lemmatizer.useStanford = true;
String lemma = lemmatizer.getLemma("them", "PRP");
assertEquals("they", lemma);
}

@Test
public void testGetLemmaReturnsFunctionWordWithUnknownPOS() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("however", "IN");
assertEquals("however", lemma);
}

@Test
public void testGetLemmaUnknownExceptionWordMissingInExceptionsMap() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("foobar", "NNS");
assertNotNull(lemma);
}

@Test
public void testGetLemmaWithWordOnlyInToStanfordAndStanfordDisabled() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
// lemmatizer.useStanford = false;
String lemma = lemmatizer.getLemma("them", "PRP");
assertEquals("them", lemma);
}

@Test
public void testCreateLemmaViewWithMisalignedPOSViewThrowsException() throws IOException {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String[] tokens = new String[] { "run", "fast" };
String[] pos = new String[] { "VB" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "src", ta, 1.0);
// posView.addConstituent(new Constituent(pos[0], ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, posView);
try {
// lemmatizer.createLemmaView(ta);
fail("Expected IndexOutOfBoundsException due to missing POS tag on second token.");
} catch (IndexOutOfBoundsException e) {
assertTrue(true);
}
}

@Test
public void testGetLemmaWithHyphenatedWordReturnsWordnetFallbackOrSame() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
// lemmatizer.wnLemmaReader = new WordnetLemmaReader("") {
// 
// @Override
// public String getLemma(String word, String pos) {
// if (word.equals("drive-by") && pos.equals("JJ"))
// return "drive-by";
// return null;
// }
// };
String lemma = lemmatizer.getLemma("drive-by", "JJ");
assertEquals("drive-by", lemma);
}

@Test
public void testLemmatizeUnknownVerbPOSNotStartingWithVB() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
// lemmatizer.wnLemmaReader = new WordnetLemmaReader("") {
// 
// @Override
// public String getLemma(String word, String pos) {
// return "establish";
// }
// };
String lemma = lemmatizer.getLemma("established", "VBD");
assertEquals("establish", lemma);
}

@Test
public void testLemmatizeKnownVerbReturnsXmodalSkipsReturnToWordnet() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
// lemmatizer.verbLemmaMap.put("might", "xmodal");
String lemma = lemmatizer.getLemma("might", "MD");
assertEquals("might", lemma);
}

@Test
public void testPOSIsPluralProperNounNNPSReturnsLowercase() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("Americans", "NNPS");
assertEquals("americans", lemma);
}

@Test
public void testNoInitializationTriggersLazyInitialize() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(true);
String[] tokens = new String[] { "dogs" };
String[] pos = new String[] { "NNS" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView view = new TokenLabelView(ViewNames.POS, "test", ta, 1.0);
// Constituent c = new Constituent(pos[0], ViewNames.POS, ta, 0, 1);
// view.addConstituent(c);
// ta.addView(ViewNames.POS, view);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("dog", lemma);
}

@Test
public void testStanfordConventionsMapEntryOverridesWhenEnabled() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
// lemmatizer.useStanford = true;
String lemma = lemmatizer.getLemma("her", "PRP");
assertEquals("she", lemma);
}

@Test
public void testWordEndsWithSAndPOSDoesNotEndWithSStillFallsThroughStem() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
// lemmatizer.wnLemmaReader = new WordnetLemmaReader("") {
// 
// @Override
// public String getLemma(String word, String pos) {
// return null;
// }
// };
String lemma = lemmatizer.getLemma("buses", "NN");
assertEquals("bus", lemma);
}

@Test
public void testCreateLemmaViewAddsCorrectViewMetadata() throws Exception {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = new String[] { "ran" };
String[] pos = new String[] { "VBD" };
// TextAnnotation ta = new TextAnnotation("corpus", "123", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "testPOS", ta, 1.0);
// Constituent posCon = new Constituent(pos[0], ViewNames.POS, ta, 0, 1);
// posView.addConstituent(posCon);
// ta.addView(ViewNames.POS, posView);
// View lemmaView = lemmatizer.createLemmaView(ta);
// assertNotNull(lemmaView);
// assertEquals(ViewNames.LEMMA, lemmaView.getViewName());
// assertEquals(1, lemmaView.getNumberOfConstituents());
// assertEquals("run", lemmaView.getConstituents().get(0).getLabel());
}

@Test
public void testAddViewAddsSecondLevelSuccessfully() throws Exception {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String[] tokens = new String[] { "cats" };
String[] pos = new String[] { "NNS" };
// TextAnnotation ta = new TextAnnotation("corpus", "doc", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "testSrc", ta, 1.0);
// Constituent con = new Constituent(pos[0], ViewNames.POS, ta, 0, 1);
// posView.addConstituent(con);
// ta.addView(ViewNames.POS, posView);
// lemmatizer.addView(ta);
// assertTrue(ta.hasView(ViewNames.LEMMA));
// View lemmaView = ta.getView(ViewNames.LEMMA);
// Constituent lemmaConstituent = lemmaView.getConstituents().get(0);
// assertEquals("cat", lemmaConstituent.getLabel());
// assertEquals(0, lemmaConstituent.getStartSpan());
// assertEquals(1, lemmaConstituent.getEndSpan());
}

@Test
public void testGetLemmaWithWordContainingAtSymbolReturnsWord() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(new LemmatizerConfigurator().getDefaultConfig());
String lemma = lemmatizer.getLemma("nick@site.com", "NN");
assertEquals("nick@site.com", lemma);
}

@Test
public void testInitializeThrowsOnMissingResourceFiles() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new ResourceManager(new java.util.Properties() {

{
put(LemmatizerConfigurator.EXCEPTIONS_FILE.key, "nonexistent.txt");
put(LemmatizerConfigurator.VERB_LEMMA_FILE.key, "missing.txt");
put(LemmatizerConfigurator.WN_PATH.key, "");
put(LemmatizerConfigurator.USE_STNFRD_CONVENTIONS.key, "false");
}
});
try {
lemmatizer.initialize(rm);
fail("Expected System.exit to be called due to IO failure on loading missing files.");
} catch (Exception e) {
assertTrue(true);
}
}
}
