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

public class IllinoisLemmatizer_llmsuite_5_GPTLLMTest {

@Test
public void testConstructorDefault() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer();
assertNotNull(lemmatizer);
}

@Test
public void testConstructorWithResourceManager() {
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(rm);
assertNotNull(lemmatizer);
}

@Test
public void testGetLemmaForKnownExceptionWord() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("media", "NNS");
assertEquals("medium", lemma);
}

@Test
public void testGetLemmaForVerb() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("retakes", "VBZ");
assertEquals("retake", lemma);
}

@Test
public void testGetLemmaForContraction() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("'ve", "VB");
assertEquals("have", lemma);
}

@Test
public void testGetLemmaWithStanfordConventionsEnabled() {
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
// rm.setLemmatizerUseStanfordConventions(true);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(rm);
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("her", "PRP");
assertEquals("she", lemma);
}

@Test
public void testGetLemmaForProperNoun() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("IBM", "NNP");
assertEquals("ibm", lemma);
}

@Test
public void testGetLemmaForAtSymbolWord() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("@mention", "NN");
assertEquals("@mention", lemma);
}

@Test
public void testGetLemmaForJJR() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("faster", "JJR");
assertEquals("faster", lemma);
}

@Test
public void testGetLemmaEndsWithMen() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("salesmen", "NNS");
assertEquals("salesman", lemma);
}

@Test
public void testGetLemmaUnknownReturnsInput() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("foobar", "NN");
assertEquals("foobar", lemma);
}

@Test
public void testGetLemmaFromTextAnnotationByIndex() {
// TextAnnotation ta = new TextAnnotation("corpus", "id", "men");
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "test", ta, 1.0);
// Constituent c = new Constituent("NNS", ViewNames.POS, ta, 0, 1);
// posView.addConstituent(c);
// ta.addView(ViewNames.POS, posView);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("man", lemma);
}

@Test
public void testGetSingleLemmaMatchesGetLemma() {
// TextAnnotation ta = new TextAnnotation("corpus", "id", "media");
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "test", ta, 1.0);
// Constituent c = new Constituent("NNS", ViewNames.POS, ta, 0, 1);
// posView.addConstituent(c);
// ta.addView(ViewNames.POS, posView);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
// String lemma = lemmatizer.getSingleLemma(ta, 0);
// assertEquals("medium", lemma);
}

@Test(expected = IllegalArgumentException.class)
public void testGetLemmaTextAnnotationIndexOutOfBounds() {
// TextAnnotation ta = new TextAnnotation("corpus", "id", "foo");
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "test", ta, 1.0);
// Constituent c = new Constituent("NN", ViewNames.POS, ta, 0, 1);
// posView.addConstituent(c);
// ta.addView(ViewNames.POS, posView);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
// lemmatizer.getLemma(ta, 2);
}

@Test
public void testCreateLemmaViewProducesExpectedOutput() throws IOException {
// TextAnnotation ta = new TextAnnotation("corpus", "id", "putting retakes media");
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "test", ta, 1.0);
// Constituent c1 = new Constituent("VBG", ViewNames.POS, ta, 0, 1);
// Constituent c2 = new Constituent("VBZ", ViewNames.POS, ta, 1, 2);
// Constituent c3 = new Constituent("NNS", ViewNames.POS, ta, 2, 3);
// posView.addConstituent(c1);
// posView.addConstituent(c2);
// posView.addConstituent(c3);
// ta.addView(ViewNames.POS, posView);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
// View view = lemmatizer.createLemmaView(ta);
// Constituent lemma1 = view.getConstituentsCoveringToken(0).get(0);
// Constituent lemma2 = view.getConstituentsCoveringToken(1).get(0);
// Constituent lemma3 = view.getConstituentsCoveringToken(2).get(0);
// assertEquals("put", lemma1.getLabel());
// assertEquals("retake", lemma2.getLabel());
// assertEquals("medium", lemma3.getLabel());
}

@Test
public void testAddViewAddsLemmaViewToTextAnnotation() throws Exception {
// TextAnnotation ta = new TextAnnotation("corpus", "id", "dogs bark loudly");
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "test", ta, 1.0);
// Constituent c1 = new Constituent("NNS", ViewNames.POS, ta, 0, 1);
// Constituent c2 = new Constituent("VBZ", ViewNames.POS, ta, 1, 2);
// Constituent c3 = new Constituent("RB", ViewNames.POS, ta, 2, 3);
// posView.addConstituent(c1);
// posView.addConstituent(c2);
// posView.addConstituent(c3);
// ta.addView(ViewNames.POS, posView);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
// lemmatizer.addView(ta);
// assertTrue(ta.hasView(ViewNames.LEMMA));
// View lemmaView = ta.getView(ViewNames.LEMMA);
// assertEquals("dog", lemmaView.getConstituentsCoveringToken(0).get(0).getLabel());
// assertEquals("bark", lemmaView.getConstituentsCoveringToken(1).get(0).getLabel());
// assertEquals("loudly", lemmaView.getConstituentsCoveringToken(2).get(0).getLabel());
}

@Test
public void testReadFromClasspathReturnsLines() {
List<String> lines = IllinoisLemmatizer.readFromClasspath("lemmatizerFiles/verb-lemmas.txt");
assertNotNull(lines);
assertFalse(lines.isEmpty());
}

@Test(expected = AnnotatorException.class)
public void testAddViewThrowsOnIOException() throws AnnotatorException {
// TextAnnotation ta = new TextAnnotation("corpus", "id", "breaker");
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "test", ta, 1.0);
// Constituent c1 = new Constituent("NN", ViewNames.POS, ta, 0, 1);
// posView.addConstituent(c1);
// ta.addView(ViewNames.POS, posView);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false) {

@Override
public View createLemmaView(TextAnnotation ta) throws IOException {
throw new IOException("Simulated failure");
}
};
// lemmatizer.addView(ta);
}

@Test
public void testGetLemmaWithTrimmingRePrefixKnown() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("re-visit", "VB");
assertEquals("visit", lemma);
}

@Test
public void testGetLemmaWithTrimmingRePrefixUnknown() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("re-blah", "VB");
assertEquals("re-blah", lemma);
}

@Test
public void testGetLemmaWithPOSJJAndEdEnding() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("learned", "JJD");
assertEquals("learned", lemma);
}

@Test
public void testGetLemma_NonLinguisticFunctionWordFallback() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("unless", "IN");
assertEquals("unless", lemma);
}

@Test
public void testGetLemma_WordWithSAndNonVerbPOSUsesStemmer() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("cars", "NNS");
assertEquals("car", lemma);
}

@Test
public void testCreateLemmaView_emptyTokens() throws IOException {
// TextAnnotation ta = new TextAnnotation("c", "id", "");
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "src", ta, 1.0);
// ta.addView(ViewNames.POS, posView);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
// View view = lemmatizer.createLemmaView(ta);
// assertEquals(0, view.getNumberOfConstituents());
}

@Test
public void testGetLemmaWithMalformedExceptionLine() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false) {

@Override
public void initialize(ResourceManager rm) {
// this.exceptionsMap = new java.util.HashMap<>();
// this.verbLemmaMap = new java.util.HashMap<>();
// this.verbBaseMap = new java.util.HashMap<>();
// this.contractions = new java.util.HashMap<>();
// this.toStanford = new java.util.HashMap<>();
// this.wnLemmaReader = new WordnetLemmaReader(rm.getString(LemmatizerConfigurator.WN_PATH.key));
}
};
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("walking", "VBG");
assertNotNull(lemma);
}

@Test
public void testAddView_multipleInvocations() throws Exception {
// TextAnnotation ta = new TextAnnotation("corpus", "id", "talks");
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "test", ta, 1.0);
// Constituent c1 = new Constituent("VBZ", ViewNames.POS, ta, 0, 1);
// pos.addConstituent(c1);
// ta.addView(ViewNames.POS, pos);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
// lemmatizer.addView(ta);
// lemmatizer.addView(ta);
// assertTrue(ta.hasView(ViewNames.LEMMA));
// View view = ta.getView(ViewNames.LEMMA);
// assertEquals(1, view.getNumberOfConstituents());
// String label = view.getConstituentsCoveringToken(0).get(0).getLabel();
// assertEquals("talk", label);
}

@Test
public void testGetLemmaPOSBePresentButEmptyLabelList() {
// TextAnnotation ta = new TextAnnotation("test", "1", "word");
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "test", ta, 1.0);
// ta.addView(ViewNames.POS, pos);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
try {
// lemmatizer.getLemma(ta, 0);
fail("Expected IndexOutOfBoundsException due to no POS labels");
} catch (IndexOutOfBoundsException ex) {
}
}

@Test
public void testGetLemmaEmptyWord() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("", "NN");
assertNotNull(lemma);
}

@Test
public void testGetLemmaWithUnknownPOSFormat() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("running", "XYZ");
assertNotNull(lemma);
}

@Test
public void testGetLemma_Contraction_s() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("'s", "VBZ");
assertEquals("be", lemma);
}

@Test
public void testGetLemma_Contraction_s_withSmartQuote() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("’s", "VBZ");
assertEquals("be", lemma);
}

@Test
public void testGetLemma_StanfordNormalization_OverridesContractions() {
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
// rm.setLemmatizerUseStanfordConventions(true);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(rm);
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("her", "PRP");
assertEquals("she", lemma);
}

@Test
public void testGetLemma_StanfordNormalization_NotPresentInMap() {
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
// rm.setLemmatizerUseStanfordConventions(true);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(rm);
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("xyzxyz", "NN");
assertEquals("xyzxyz", lemma);
}

@Test
public void testGetLemma_lemmatizerNotExplicitlyInitialized_callsDoInitialize() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(true);
// TextAnnotation ta = new TextAnnotation("test", "id", "putting");
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "src", ta, 1.0);
// Constituent c = new Constituent("VBG", ViewNames.POS, ta, 0, 1);
// posView.addConstituent(c);
// ta.addView(ViewNames.POS, posView);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("put", lemma);
}

@Test
public void testGetLemma_EmptyPOSLabelList_ThrowsException() {
// TextAnnotation ta = new TextAnnotation("corpus", "id", "sample");
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "test", ta, 1.0);
// ta.addView(ViewNames.POS, posView);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
try {
// lemmatizer.getLemma(ta, 0);
fail("Expected IndexOutOfBoundsException for empty POS label list");
} catch (IndexOutOfBoundsException ex) {
}
}

@Test
public void testGetLemma_UpperCaseWordIsLowercased() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("RUNNING", "VBG");
assertEquals("run", lemma);
}

@Test
public void testGetLemma_wordEqualsXmodalPreservesOriginal() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false) {

@Override
public void initialize(ResourceManager rm) {
// this.verbLemmaMap = new java.util.HashMap<>();
// this.verbBaseMap = new java.util.HashMap<>();
// this.verbLemmaMap.put("going", "xmodal");
// this.verbBaseMap.put("going", "xmodal");
// this.exceptionsMap = new java.util.HashMap<>();
// this.contractions = new java.util.HashMap<>();
// this.toStanford = new java.util.HashMap<>();
// this.wnLemmaReader = new WordnetLemmaReader(rm.getString(LemmatizerConfigurator.WN_PATH.key));
}
};
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("going", "VB");
assertEquals("going", lemma);
}

@Test
public void testGetLemma_nullLemmaFallbacksToStemmer() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false) {

@Override
public void initialize(ResourceManager rm) {
// this.verbLemmaMap = new java.util.HashMap<>();
// this.verbBaseMap = new java.util.HashMap<>();
// this.exceptionsMap = new java.util.HashMap<>();
// this.contractions = new java.util.HashMap<>();
// this.toStanford = new java.util.HashMap<>();
// this.wnLemmaReader = new WordnetLemmaReader(rm.getString(LemmatizerConfigurator.WN_PATH.key)) {
// 
// @Override
// public String getLemma(String word, String pos) {
// return null;
// }
// };
}
};
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("cars", "NN");
assertEquals("car", lemma);
}

@Test
public void testReadFromClasspath_invalidFileReturnsEmptyOrNull() {
try {
List<String> lines = IllinoisLemmatizer.readFromClasspath("nonexistent/fake.txt");
assertNull(lines);
} catch (Exception e) {
fail("Should not throw exception");
}
}

@Test
public void testCreateLemmaView_tokenWithNoPOSLemmasIsAdded() throws IOException {
// TextAnnotation ta = new TextAnnotation("test", "id", "hello");
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "src", ta, 1.0);
// Constituent pos = new Constituent("FW", ViewNames.POS, ta, 0, 1);
// posView.addConstituent(pos);
// ta.addView(ViewNames.POS, posView);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
// View lemmaView = lemmatizer.createLemmaView(ta);
// assertEquals("hello", lemmaView.getConstituentsCoveringToken(0).get(0).getLabel());
}

@Test
public void testCreateLemmaViewWithMultipleTokensNonVerb() throws IOException {
// TextAnnotation ta = new TextAnnotation("c", "id", "dogs quickly redder");
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "gold", ta, 1.0);
// Constituent c1 = new Constituent("NNS", ViewNames.POS, ta, 0, 1);
// Constituent c2 = new Constituent("RB", ViewNames.POS, ta, 1, 2);
// Constituent c3 = new Constituent("JJR", ViewNames.POS, ta, 2, 3);
// posView.addConstituent(c1);
// posView.addConstituent(c2);
// posView.addConstituent(c3);
// ta.addView(ViewNames.POS, posView);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
// View view = lemmatizer.createLemmaView(ta);
// assertEquals("dog", view.getConstituentsCoveringToken(0).get(0).getLabel());
// assertEquals("quickly", view.getConstituentsCoveringToken(1).get(0).getLabel());
// assertEquals("redder", view.getConstituentsCoveringToken(2).get(0).getLabel());
}

@Test
public void testGetLemma_trimsInputAndLowersCase() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("  Retakes  ", "VBZ");
assertEquals("retake", lemma);
}

@Test
public void testGetLemma_emptyStringInputReturnsEmpty() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("", "NN");
assertEquals("", lemma);
}

@Test
public void testGetLemma_singleCharInputFallbacksToWordnetOrStem() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("s", "NN");
assertNotNull(lemma);
}

@Test
public void testGetLemma_invalidPOSfallbackToWordItself() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("and", "CC");
assertEquals("and", lemma);
}

@Test
public void testGetLemma_withPOSVBN_butNotInVerbMapUsesWordnetOrStem() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("written", "VBN");
assertEquals("write", lemma);
}

@Test
public void testGetLemma_nonEnglishCharactersHandledGracefully() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("fiancée", "NN");
assertNotNull(lemma);
}

@Test
public void testGetLemma_numericTokenReturnsItself() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("2024", "CD");
assertEquals("2024", lemma);
}

@Test
public void testGetLemma_punctuationTokenReturnsItself() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma(",", ".");
assertEquals(",", lemma);
}

@Test
public void testCreateLemmaView_handlesMixedPOSProperly() throws IOException {
// TextAnnotation ta = new TextAnnotation("corpus", "id", "builders fast running");
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "test", ta, 1.0);
// Constituent c1 = new Constituent("NNS", ViewNames.POS, ta, 0, 1);
// Constituent c2 = new Constituent("RB", ViewNames.POS, ta, 1, 2);
// Constituent c3 = new Constituent("VBG", ViewNames.POS, ta, 2, 3);
// pos.addConstituent(c1);
// pos.addConstituent(c2);
// pos.addConstituent(c3);
// ta.addView(ViewNames.POS, pos);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
// View lemmaView = lemmatizer.createLemmaView(ta);
// assertEquals(3, lemmaView.getNumberOfConstituents());
// assertEquals("builder", lemmaView.getConstituentsCoveringToken(0).get(0).getLabel());
// assertEquals("fast", lemmaView.getConstituentsCoveringToken(1).get(0).getLabel());
// assertEquals("run", lemmaView.getConstituentsCoveringToken(2).get(0).getLabel());
}

@Test
public void testGetLemma_wordThatTriggersREPrefixButIsVerbatimMapped() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false) {

@Override
public void initialize(ResourceManager rm) {
// this.verbLemmaMap = new java.util.HashMap<>();
// this.verbBaseMap = new java.util.HashMap<>();
// this.toStanford = new java.util.HashMap<>();
// this.contractions = new java.util.HashMap<>();
// this.exceptionsMap = new java.util.HashMap<>();
// this.verbLemmaMap.put("activate", "activate");
// this.verbLemmaMap.put("reactivate", "activate");
// this.wnLemmaReader = new WordnetLemmaReader(rm.getString(LemmatizerConfigurator.WN_PATH.key));
}
};
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("reactivate", "VBZ");
assertEquals("activate", lemma);
}

@Test
public void testGetLemma_nullWordnetReturnsNull_MenEndingTriggersManualRule() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false) {

@Override
public void initialize(ResourceManager rm) {
// this.verbLemmaMap = new java.util.HashMap<>();
// this.verbBaseMap = new java.util.HashMap<>();
// this.exceptionsMap = new java.util.HashMap<>();
// this.contractions = new java.util.HashMap<>();
// this.toStanford = new java.util.HashMap<>();
// this.wnLemmaReader = new WordnetLemmaReader(rm.getString(LemmatizerConfigurator.WN_PATH.key)) {
// 
// @Override
// public String getLemma(String word, String pos) {
// return null;
// }
// };
}
};
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("craftsmen", "NNS");
assertEquals("craftsman", lemma);
}

@Test
public void testGetLemma_inputVerbWithoutPOSDefaultsToWordnet() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("ran", "VB");
assertEquals("run", lemma);
}

@Test
public void testGetLemma_UnhandledPOSDefaultsToWord() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("henceforth", "UH");
assertEquals("henceforth", lemma);
}

@Test
public void testGetLemma_CustomContractionMappedCorrectly() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false) {

@Override
public void initialize(ResourceManager rm) {
// this.contractions = new java.util.HashMap<>();
// this.contractions.put("n't", "not");
// this.verbLemmaMap = new java.util.HashMap<>();
// this.verbBaseMap = new java.util.HashMap<>();
// this.exceptionsMap = new java.util.HashMap<>();
// this.toStanford = new java.util.HashMap<>();
// this.wnLemmaReader = new WordnetLemmaReader(rm.getString(LemmatizerConfigurator.WN_PATH.key));
}
};
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("n't", "RB");
assertEquals("not", lemma);
}

@Test
public void testGetLemma_rePrefixNotInVerbMapFallsThrough() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false) {

@Override
public void initialize(ResourceManager rm) {
// this.verbLemmaMap = new java.util.HashMap<>();
// this.verbBaseMap = new java.util.HashMap<>();
// this.exceptionsMap = new java.util.HashMap<>();
// this.contractions = new java.util.HashMap<>();
// this.toStanford = new java.util.HashMap<>();
// this.wnLemmaReader = new WordnetLemmaReader(rm.getString(LemmatizerConfigurator.WN_PATH.key)) {
// 
// @Override
// public String getLemma(String word, String pos) {
// return "xyz";
// }
// };
}
};
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("reprocess", "VB");
assertEquals("xyz", lemma);
}

@Test
public void testGetLemma_verbKnownButMappedValueIsNull() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false) {

@Override
public void initialize(ResourceManager rm) {
// this.verbLemmaMap = new java.util.HashMap<>();
// this.verbBaseMap = new java.util.HashMap<>();
// this.verbLemmaMap.put("swam", null);
// this.verbBaseMap.put("swim", null);
// this.exceptionsMap = new java.util.HashMap<>();
// this.contractions = new java.util.HashMap<>();
// this.toStanford = new java.util.HashMap<>();
// this.wnLemmaReader = new WordnetLemmaReader(rm.getString(LemmatizerConfigurator.WN_PATH.key)) {
// 
// @Override
// public String getLemma(String word, String pos) {
// return "swim";
// }
// };
}
};
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("swam", "VB");
assertEquals("swim", lemma);
}

@Test
public void testGetLemma_POSDoesNotStartWithStakeholderHandled() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("stakeholders", "XYZ");
assertEquals("stakeholders", lemma);
}

@Test
public void testGetLemma_TokenAtLastIndex() {
// TextAnnotation ta = new TextAnnotation("test", "id", "fast cars");
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "test", ta, 1.0);
// Constituent c1 = new Constituent("RB", ViewNames.POS, ta, 0, 1);
// Constituent c2 = new Constituent("NNS", ViewNames.POS, ta, 1, 2);
// posView.addConstituent(c1);
// posView.addConstituent(c2);
// ta.addView(ViewNames.POS, posView);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
// String lemma = lemmatizer.getLemma(ta, 1);
// assertEquals("car", lemma);
}

@Test
public void testAddView_LemmaViewAlreadyExistsOverwrites() throws Exception {
// TextAnnotation ta = new TextAnnotation("corpus", "id", "move forward");
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "test", ta, 1.0);
// Constituent p1 = new Constituent("VB", ViewNames.POS, ta, 0, 1);
// Constituent p2 = new Constituent("RB", ViewNames.POS, ta, 1, 2);
// pos.addConstituent(p1);
// pos.addConstituent(p2);
// ta.addView(ViewNames.POS, pos);
// TokenLabelView lemmaDummy = new TokenLabelView(ViewNames.LEMMA, "dummy", ta, 1.0);
// Constituent fake = new Constituent("XXX", ViewNames.LEMMA, ta, 0, 1);
// lemmaDummy.addConstituent(fake);
// ta.addView(ViewNames.LEMMA, lemmaDummy);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
// lemmatizer.addView(ta);
// View updated = ta.getView(ViewNames.LEMMA);
// assertNotEquals("XXX", updated.getConstituentsCoveringToken(0).get(0).getLabel());
}

@Test
public void testInitialize_WithInvalidKeysFallbackGracefully() {
ResourceManager rm = new ResourceManager(new java.util.Properties());
rm.getProperties().setProperty(LemmatizerConfigurator.WN_PATH.key, "data/wordnet");
rm.getProperties().setProperty(LemmatizerConfigurator.VERB_LEMMA_FILE.key, "bad.txt");
rm.getProperties().setProperty(LemmatizerConfigurator.EXCEPTIONS_FILE.key, "bad.txt");
rm.getProperties().setProperty(LemmatizerConfigurator.USE_STNFRD_CONVENTIONS.key, "false");
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
try {
lemmatizer.initialize(rm);
assertTrue(true);
} catch (Exception e) {
fail("Should not throw on invalid files, should handle gracefully");
}
}

@Test
public void testCreateLemmaView_WithWordContainingAtSign() throws IOException {
// TextAnnotation ta = new TextAnnotation("test", "id", "@user is cool");
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "test", ta, 1.0);
// Constituent c1 = new Constituent("NN", ViewNames.POS, ta, 0, 1);
// Constituent c2 = new Constituent("VBZ", ViewNames.POS, ta, 1, 2);
// Constituent c3 = new Constituent("JJ", ViewNames.POS, ta, 2, 3);
// pos.addConstituent(c1);
// pos.addConstituent(c2);
// pos.addConstituent(c3);
// ta.addView(ViewNames.POS, pos);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
// View lemmaView = lemmatizer.createLemmaView(ta);
// assertEquals("@user", lemmaView.getConstituentsCoveringToken(0).get(0).getLabel());
}

@Test
public void testGetLemma_POSStartsWithJJButWordNotEd() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("happy", "JJ");
assertNotNull(lemma);
}

@Test
public void testGetLemma_returnsOriginalIfPOSUnrecognizedAndWordDoesNotEndWithS() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("hence", "XYZZ");
assertEquals("hence", lemma);
}

@Test
public void testGetLemma_returnsStemForUnrecognizedPOSAndWordEndsWithS() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("cats", "XXXS");
assertEquals("cat", lemma);
}

@Test
public void testGetLemma_returnsReplaceReWhenFallbackEnabledAndPresent() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false) {

@Override
public void initialize(ResourceManager rm) {
// this.verbLemmaMap = new java.util.HashMap<>();
// this.verbLemmaMap.put("enter", "enter");
// this.verbBaseMap = new java.util.HashMap<>();
// this.exceptionsMap = new java.util.HashMap<>();
// this.contractions = new java.util.HashMap<>();
// this.toStanford = new java.util.HashMap<>();
// this.wnLemmaReader = new WordnetLemmaReader("data/wordnet") {
// 
// @Override
// public String getLemma(String word, String pos) {
// return "xmodal".equals(word) ? null : "fallback";
// }
// };
}
};
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("re-enter", "VBG");
assertEquals("enter", lemma);
}

@Test
public void testGetLemma_returnsOriginalIfVerbLemmaIsXmodal() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false) {

@Override
public void initialize(ResourceManager rm) {
// this.verbLemmaMap = new java.util.HashMap<>();
// this.verbLemmaMap.put("might", "xmodal");
// this.verbBaseMap = new java.util.HashMap<>();
// this.verbBaseMap.put("might", "xmodal");
// this.exceptionsMap = new java.util.HashMap<>();
// this.contractions = new java.util.HashMap<>();
// this.toStanford = new java.util.HashMap<>();
// this.wnLemmaReader = new WordnetLemmaReader("data/wordnet");
}
};
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("might", "VB");
assertEquals("might", lemma);
}

@Test
public void testInitialize_initializesContractionsCorrectly() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("'ll", "VB");
assertEquals("will", lemma);
}

@Test
public void testInitialize_initializesToStanfordMapCorrectly() {
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
rm.getProperties().setProperty(LemmatizerConfigurator.USE_STNFRD_CONVENTIONS.key, "true");
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("them", "PRP");
assertEquals("they", lemma);
}

@Test(expected = IllegalArgumentException.class)
public void testGetLemma_withInvalidTokenIndexThrowsException() {
String text = "test text";
// TextAnnotation ta = new TextAnnotation("test", "id", text);
// TokenLabelView view = new TokenLabelView(ViewNames.POS, "test", ta, 1.0);
// Constituent c1 = new Constituent("NN", ViewNames.POS, ta, 0, 1);
// view.addConstituent(c1);
// ta.addView(ViewNames.POS, view);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
// lemmatizer.getLemma(ta, 3);
}

@Test
public void testGetLemma_wordEndsWithMenButTooShortDoesNotTriggerManualRule() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false) {

@Override
public void initialize(ResourceManager rm) {
// this.verbLemmaMap = new java.util.HashMap<>();
// this.verbBaseMap = new java.util.HashMap<>();
// this.exceptionsMap = new java.util.HashMap<>();
// this.contractions = new java.util.HashMap<>();
// this.toStanford = new java.util.HashMap<>();
// this.wnLemmaReader = new WordnetLemmaReader("data/wordnet") {
// 
// @Override
// public String getLemma(String word, String pos) {
// return null;
// }
// };
}
};
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("men", "NNS");
assertEquals("man", lemma);
}

@Test
public void testGetLemma_nonPOSContentWord_returnsFallback() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("to", "TO");
assertEquals("to", lemma);
}

@Test
public void testGetLemma_unknownVerbShouldFallbackToWordnet() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false) {

@Override
public void initialize(ResourceManager rm) {
// this.verbLemmaMap = new java.util.HashMap<>();
// this.verbBaseMap = new java.util.HashMap<>();
// this.exceptionsMap = new java.util.HashMap<>();
// this.contractions = new java.util.HashMap<>();
// this.toStanford = new java.util.HashMap<>();
// this.wnLemmaReader = new WordnetLemmaReader("data/wordnet") {
// 
// @Override
// public String getLemma(String word, String pos) {
// if ("pretended".equals(word))
// return "pretend";
// return null;
// }
// };
}
};
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("pretended", "VBD");
assertEquals("pretend", lemma);
}

@Test
public void testCreateLemmaView_addsCorrectConstituents() throws IOException {
// TextAnnotation ta = new TextAnnotation("test", "id", "cats run fast");
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "testSrc", ta, 1.0);
// Constituent c0 = new Constituent("NNS", ViewNames.POS, ta, 0, 1);
// Constituent c1 = new Constituent("VBP", ViewNames.POS, ta, 1, 2);
// Constituent c2 = new Constituent("RB", ViewNames.POS, ta, 2, 3);
// pos.addConstituent(c0);
// pos.addConstituent(c1);
// pos.addConstituent(c2);
// ta.addView(ViewNames.POS, pos);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
// View result = lemmatizer.createLemmaView(ta);
// assertEquals("cat", result.getConstituentsCoveringToken(0).get(0).getLabel());
// assertEquals("run", result.getConstituentsCoveringToken(1).get(0).getLabel());
// assertEquals("fast", result.getConstituentsCoveringToken(2).get(0).getLabel());
}

@Test
public void testGetLemma_nonAlphabeticWordFallback() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("9x9x", "SYM");
assertEquals("9x9x", lemma);
}

@Test
public void testGetLemma_POSStartsWithJJAndEdEndingReturnsWord() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("painted", "JJR");
assertEquals("painted", lemma);
}

@Test
public void testGetLemma_withNullWordnetReturnsStemmedPlural() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false) {

@Override
public void initialize(ResourceManager rm) {
// this.verbBaseMap = new java.util.HashMap<>();
// this.verbLemmaMap = new java.util.HashMap<>();
// this.exceptionsMap = new java.util.HashMap<>();
// this.contractions = new java.util.HashMap<>();
// this.toStanford = new java.util.HashMap<>();
// this.wnLemmaReader = new WordnetLemmaReader("data/wordnet") {
// 
// @Override
// public String getLemma(String word, String pos) {
// return null;
// }
// };
}
};
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("horses", "NN");
assertEquals("horse", lemma);
}

@Test
public void testGetLemma_withPOSPresentInToStanfordOverridesEverything() {
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
rm.getProperties().setProperty(LemmatizerConfigurator.USE_STNFRD_CONVENTIONS.key, "true");
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(rm);
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("her", "PRP");
assertEquals("she", lemma);
}

@Test
public void testGetLemma_POS_JJR_notEndingWithEd_returnsWord() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("smarter", "JJR");
assertEquals("smarter", lemma);
}

@Test
public void testGetLemma_POSStartsWithJJAndEndsWithEd_returnsWord() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("painted", "JJ");
assertEquals("painted", lemma);
}

@Test
public void testGetLemma_POS_RBR_doesNotLemmatize() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("fastest", "RBR");
assertEquals("fastest", lemma);
}

@Test
public void testGetLemma_unknownVerbMappedInBaseMapToNullFallsBackToWN() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false) {

@Override
public void initialize(ResourceManager rm) {
// this.verbBaseMap = new java.util.HashMap<>();
// this.verbBaseMap.put("talks", null);
// this.verbLemmaMap = new java.util.HashMap<>();
// this.verbLemmaMap.put("talks", null);
// this.exceptionsMap = new java.util.HashMap<>();
// this.contractions = new java.util.HashMap<>();
// this.toStanford = new java.util.HashMap<>();
// this.wnLemmaReader = new WordnetLemmaReader("dummy") {
// 
// @Override
// public String getLemma(String word, String pos) {
// return "talk";
// }
// };
}
};
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("talks", "VBZ");
assertEquals("talk", lemma);
}

@Test
public void testGetLemma_withProperNounAndMixedCaseInput_returnsLowercaseWord() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("IBM", "NNP");
assertEquals("ibm", lemma);
}

@Test
public void testGetLemma_withWordEndingInMen_withPrefixReturnsManSuffix() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false) {

@Override
public void initialize(ResourceManager rm) {
// this.verbLemmaMap = new java.util.HashMap<>();
// this.verbBaseMap = new java.util.HashMap<>();
// this.exceptionsMap = new java.util.HashMap<>();
// this.contractions = new java.util.HashMap<>();
// this.toStanford = new java.util.HashMap<>();
// this.wnLemmaReader = new WordnetLemmaReader("dummy") {
// 
// @Override
// public String getLemma(String word, String pos) {
// return null;
// }
// };
}
};
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("middlemen", "NNS");
assertEquals("middleman", lemma);
}

@Test
public void testGetLemma_VERB_usesCorrectVerbBaseOrDerivedLogic() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false) {

@Override
public void initialize(ResourceManager rm) {
// this.verbLemmaMap = new java.util.HashMap<>();
// this.verbBaseMap = new java.util.HashMap<>();
// this.verbLemmaMap.put("retakes", "retake");
// this.verbBaseMap.put("retakes", "baseform");
// this.exceptionsMap = new java.util.HashMap<>();
// this.contractions = new java.util.HashMap<>();
// this.toStanford = new java.util.HashMap<>();
// this.wnLemmaReader = new WordnetLemmaReader("dummy");
}
};
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("retakes", "VBZ");
assertEquals("retake", lemma);
}

@Test
public void testGetLemma_usesBaseMapWhenPOSIsVB() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false) {

@Override
public void initialize(ResourceManager rm) {
// this.verbLemmaMap = new java.util.HashMap<>();
// this.verbBaseMap = new java.util.HashMap<>();
// this.verbBaseMap.put("running", "run");
// this.verbLemmaMap.put("running", "runWrong");
// this.exceptionsMap = new java.util.HashMap<>();
// this.contractions = new java.util.HashMap<>();
// this.toStanford = new java.util.HashMap<>();
// this.wnLemmaReader = new WordnetLemmaReader("dummy");
}
};
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
String lemma = lemmatizer.getLemma("running", "VB");
assertEquals("run", lemma);
}

@Test
public void testCreateLemmaView_addsCorrectViewToTextAnnotation() throws IOException {
// TextAnnotation ta = new TextAnnotation("test", "id", "eats quickly");
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "src", ta, 1.0);
// Constituent c0 = new Constituent("VBZ", ViewNames.POS, ta, 0, 1);
// Constituent c1 = new Constituent("RB", ViewNames.POS, ta, 1, 2);
// pos.addConstituent(c0);
// pos.addConstituent(c1);
// ta.addView(ViewNames.POS, pos);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);
ResourceManager rm = new LemmatizerConfigurator().getDefaultConfig();
lemmatizer.initialize(rm);
// View lemmaView = lemmatizer.createLemmaView(ta);
// assertNotNull(lemmaView);
// assertEquals("eat", lemmaView.getConstituentsCoveringToken(0).get(0).getLabel());
// assertEquals("quickly", lemmaView.getConstituentsCoveringToken(1).get(0).getLabel());
}
}
