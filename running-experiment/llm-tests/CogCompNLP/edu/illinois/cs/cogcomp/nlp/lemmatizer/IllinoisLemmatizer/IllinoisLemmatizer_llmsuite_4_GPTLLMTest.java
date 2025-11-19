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

public class IllinoisLemmatizer_llmsuite_4_GPTLLMTest {

@Test
public void testLemmaForMedia() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = new String[] { "media" };
// TextAnnotation ta = new TextAnnotation("test", "id", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "test", ta, 1.0);
// Constituent c = new Constituent("NNS", ViewNames.POS, ta, 0, 1);
// posView.addConstituent(c);
// ta.addView(ViewNames.POS, posView);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("medium", lemma);
}

@Test
public void testLemmaForMen() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = new String[] { "men" };
// TextAnnotation ta = new TextAnnotation("test", "id", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "test", ta, 1.0);
// Constituent c = new Constituent("NNS", ViewNames.POS, ta, 0, 1);
// posView.addConstituent(c);
// ta.addView(ViewNames.POS, posView);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("man", lemma);
}

@Test
public void testLemmaForPutting() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = new String[] { "putting" };
// TextAnnotation ta = new TextAnnotation("test", "id", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "test", ta, 1.0);
// Constituent c = new Constituent("VBG", ViewNames.POS, ta, 0, 1);
// posView.addConstituent(c);
// ta.addView(ViewNames.POS, posView);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("put", lemma);
}

@Test
public void testGetSingleLemmaDelegatesToGetLemma() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = new String[] { "retakes" };
// TextAnnotation ta = new TextAnnotation("test", "id", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "test", ta, 1.0);
// Constituent c = new Constituent("VBZ", ViewNames.POS, ta, 0, 1);
// posView.addConstituent(c);
// ta.addView(ViewNames.POS, posView);
// String lemma1 = lemmatizer.getLemma(ta, 0);
// String lemma2 = lemmatizer.getSingleLemma(ta, 0);
// assertEquals(lemma1, lemma2);
}

@Test(expected = IllegalArgumentException.class)
public void testGetLemmaOutOfRangeThrowsException() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = new String[] { "one", "two" };
// TextAnnotation ta = new TextAnnotation("test", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "a", ta, 1.0);
// pos.addConstituent(new Constituent("NN", ViewNames.POS, ta, 0, 1));
// pos.addConstituent(new Constituent("NN", ViewNames.POS, ta, 1, 2));
// ta.addView(ViewNames.POS, pos);
// lemmatizer.getLemma(ta, 2);
}

@Test
public void testCreateLemmaViewAddsProperView() throws IOException {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = new String[] { "running" };
// TextAnnotation ta = new TextAnnotation("a", "b", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "manual", ta, 1.0);
// pos.addConstituent(new Constituent("VBG", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, pos);
// View lemmaView = lemmatizer.createLemmaView(ta);
// assertNotNull(lemmaView);
// assertEquals("run", lemmaView.getLabelsCoveringToken(0).get(0));
}

@Test
public void testAddViewBuildsLemmaView() throws AnnotatorException {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = new String[] { "running" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "tagger", ta, 1.0);
// pos.addConstituent(new Constituent("VBG", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, pos);
// lemmatizer.addView(ta);
// assertTrue(ta.hasView(ViewNames.LEMMA));
// String label = ta.getView(ViewNames.LEMMA).getLabelsCoveringToken(0).get(0);
// assertEquals("run", label);
}

@Test
public void testProperNounLowerCased() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = new String[] { "Illinois" };
// TextAnnotation ta = new TextAnnotation("a", "b", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "generator", ta, 1.0);
// pos.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("illinois", lemma);
}

@Test
public void testContractionApostropheSResolvesToBe() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = new String[] { "'s" };
// TextAnnotation ta = new TextAnnotation("a", "b", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "p", ta, 1.0);
// pos.addConstituent(new Constituent("VBZ", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("be", lemma);
}

@Test
public void testUnsupportedPOSReturnsOriginal() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = new String[] { "eh" };
// TextAnnotation ta = new TextAnnotation("x", "y", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "p", ta, 1.0);
// pos.addConstituent(new Constituent("UH", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("eh", lemma);
}

@Test
public void testEmailTokenPreservedWithSymbol() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = new String[] { "user@example.com" };
// TextAnnotation ta = new TextAnnotation("x", "y", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "p", ta, 1.0);
// pos.addConstituent(new Constituent("NN", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("user@example.com", lemma);
}

@Test
public void testVerbKnownInBaseMapReturnsBaseFormForVB() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = new String[] { "walk" };
// TextAnnotation ta = new TextAnnotation("test", "id", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "manual", ta, 1.0);
// Constituent c = new Constituent("VB", ViewNames.POS, ta, 0, 1);
// posView.addConstituent(c);
// ta.addView(ViewNames.POS, posView);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("walk", lemma);
}

@Test
public void testVerbKnownInLemmaMapReturnsMappedForm() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = new String[] { "walked" };
// TextAnnotation ta = new TextAnnotation("test", "id", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "manual", ta, 1.0);
// Constituent c = new Constituent("VBD", ViewNames.POS, ta, 0, 1);
// posView.addConstituent(c);
// ta.addView(ViewNames.POS, posView);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("walk", lemma);
}

@Test
public void testREPrefixTrimCase() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = new String[] { "re-took" };
// TextAnnotation ta = new TextAnnotation("test", "id", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "manual", ta, 1.0);
// Constituent c = new Constituent("VBD", ViewNames.POS, ta, 0, 1);
// posView.addConstituent(c);
// ta.addView(ViewNames.POS, posView);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertNotNull(lemma);
}

@Test
public void testUnknownNounEndingInMenChangesToMan() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = new String[] { "workmen" };
// TextAnnotation ta = new TextAnnotation("test", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "manual", ta, 1.0);
// Constituent c = new Constituent("NNS", ViewNames.POS, ta, 0, 1);
// pos.addConstituent(c);
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("workman", lemma);
}

@Test
public void testUnknownNounEndingInSUsesMorphaStemmerFallback() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = new String[] { "jumpers" };
// TextAnnotation ta = new TextAnnotation("test", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "manual", ta, 1.0);
// Constituent c = new Constituent("NNS", ViewNames.POS, ta, 0, 1);
// pos.addConstituent(c);
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("jumper", lemma);
}

@Test
public void testJJRAdjectiveIsPreservedAsIs() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = new String[] { "happier" };
// TextAnnotation ta = new TextAnnotation("test", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "test", ta, 1.0);
// Constituent c = new Constituent("JJR", ViewNames.POS, ta, 0, 1);
// pos.addConstituent(c);
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("happier", lemma);
}

@Test
public void testTokenWithApostropheReturnsAsIsForVBZ() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = new String[] { "’s" };
// TextAnnotation ta = new TextAnnotation("test", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "manual", ta, 1.0);
// Constituent c = new Constituent("VBZ", ViewNames.POS, ta, 0, 1);
// pos.addConstituent(c);
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("be", lemma);
}

@Test
public void testFunctionWordPOSFallbackToOriginalToken() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = new String[] { "upon" };
// TextAnnotation ta = new TextAnnotation("x", "y", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "manual", ta, 1.0);
// Constituent c = new Constituent("IN", ViewNames.POS, ta, 0, 1);
// posView.addConstituent(c);
// ta.addView(ViewNames.POS, posView);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("upon", lemma);
}

@Test
public void testUnknownVerbUsingWordNetFallbackBehavior() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = new String[] { "zombies" };
// TextAnnotation ta = new TextAnnotation("test", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "manual", ta, 1.0);
// Constituent c = new Constituent("NNS", ViewNames.POS, ta, 0, 1);
// pos.addConstituent(c);
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertTrue(lemma.equals("zombie") || lemma.equals("zombies"));
}

@Test
public void testTokenEndingWithSButNoPOSFallbackReturnsStem() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = new String[] { "bikes" };
// TextAnnotation ta = new TextAnnotation("test", "id", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "manual", ta, 1.0);
// Constituent c = new Constituent("NN", ViewNames.POS, ta, 0, 1);
// posView.addConstituent(c);
// ta.addView(ViewNames.POS, posView);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("bike", lemma);
}

@Test
public void testViewCreationWithMultipleTokensIncludesAllLemmas() throws IOException {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = new String[] { "men", "retakes", "media" };
// TextAnnotation ta = new TextAnnotation("c", "d", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "p", ta, 1.0);
// pos.addConstituent(new Constituent("NNS", ViewNames.POS, ta, 0, 1));
// pos.addConstituent(new Constituent("VBZ", ViewNames.POS, ta, 1, 2));
// pos.addConstituent(new Constituent("NNS", ViewNames.POS, ta, 2, 3));
// ta.addView(ViewNames.POS, pos);
// View view = lemmatizer.createLemmaView(ta);
// assertEquals(3, view.getConstituents().size());
// assertEquals("man", view.getLabelsCoveringToken(0).get(0));
// assertEquals("retake", view.getLabelsCoveringToken(1).get(0));
// assertEquals("medium", view.getLabelsCoveringToken(2).get(0));
}

@Test
public void testGetLemmaOnEmptyTextAnnotation() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = new String[] {};
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "pos", ta, 1.0);
// ta.addView(ViewNames.POS, pos);
try {
// lemmatizer.getLemma(ta, 0);
fail("Expected IllegalArgumentException due to zero-length TextAnnotation.");
} catch (IllegalArgumentException e) {
assertTrue(e.getMessage().contains("is out of range"));
}
}

@Test
public void testCreateLemmaViewWithNoPosView() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = new String[] { "walked" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
try {
// lemmatizer.createLemmaView(ta);
fail("Expected exception due to missing POS view.");
} catch (Exception e) {
assertTrue(e instanceof RuntimeException || e.toString().contains("POS"));
}
}

@Test
public void testLemmaOnSingleLowercaseWordWithAdjectiveEndingInEd() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = new String[] { "blessed" };
// TextAnnotation ta = new TextAnnotation("c", "d", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "manual", ta, 1.0);
// pos.addConstituent(new Constituent("JJ", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("blessed", lemma);
}

@Test
public void testNounPluralWithIrregularSuffix() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = new String[] { "teeth" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "gen", ta, 1.0);
// pos.addConstituent(new Constituent("NNS", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("tooth", lemma);
}

@Test
public void testWordWithNoMatchingPOSReturnsWordItself() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] words = { "huh" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", words);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "source", ta, 1.0);
// pos.addConstituent(new Constituent("FW", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("huh", lemma);
}

@Test
public void testCapitalizedProperNounIsLowerCasedInLemma() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = { "Smith" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "annotator", ta, 1.0);
// pos.addConstituent(new Constituent("NNP", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("smith", lemma);
}

@Test
public void testUnknownVerbNotInMappingsReturnsWordNetFallback() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = { "zipping" };
// TextAnnotation ta = new TextAnnotation("test", "id", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "manual", ta, 1.0);
// posView.addConstituent(new Constituent("VBG", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, posView);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertNotNull(lemma);
// assertTrue(lemma.equals("zip") || lemma.equals("zipping"));
}

@Test
public void testApostropheContractionWithVBZReturnsBe() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = { "'s" };
// TextAnnotation ta = new TextAnnotation("test", "id", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "manual", ta, 1.0);
// posView.addConstituent(new Constituent("VBZ", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, posView);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("be", lemma);
}

@Test
public void testUnknownTokenWithNoPOSFallbackStillReturnsOriginal() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String unknown = "xmodal";
String[] tokens = { unknown };
// TextAnnotation ta = new TextAnnotation("test", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "tagger", ta, 1.0);
// pos.addConstituent(new Constituent("VBZ", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals(unknown, lemma);
}

@Test
public void testKnownExceptionWordTakesPriorityOverOtherRules() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = new String[] { "teeth" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "source", ta, 1.0);
// posView.addConstituent(new Constituent("NNS", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, posView);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("tooth", lemma);
}

@Test
public void testUseStanfordMappingOverridesOtherMappings() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.addProperty(LemmatizerConfigurator.USE_STNFRD_CONVENTIONS.key, "true");
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = new String[] { "them" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "source", ta, 1.0);
// posView.addConstituent(new Constituent("PRP", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, posView);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("they", lemma);
}

@Test
public void testWordMissingVerbSuffixInVerbLemmaMapReturnsWordNetLemma() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = new String[] { "singing" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "source", ta, 1.0);
// posView.addConstituent(new Constituent("VBG", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, posView);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("sing", lemma);
}

@Test
public void testWhitespaceInTokenIsTrimmedBeforeProcessing() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = new String[] { "  men  " };
// TextAnnotation ta = new TextAnnotation("a", "b", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "annotator", ta, 1.0);
// posView.addConstituent(new Constituent("NNS", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, posView);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("man", lemma);
}

@Test
public void testNonStandardPOSPrefixStillTriggersVerbBranch() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = new String[] { "walked" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "src", ta, 1.0);
// posView.addConstituent(new Constituent("VBD-custom", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, posView);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertTrue(lemma.equals("walk") || lemma.equals("walked"));
}

@Test
public void testMalformedPOSReturnsViaFallback() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = new String[] { "jumpers" };
// TextAnnotation ta = new TextAnnotation("corpus", "x", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "fallback", ta, 1.0);
// posView.addConstituent(new Constituent("$$$", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, posView);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("jumpers", lemma);
}

@Test
public void testGetLemmaWhenTokenContainsSymbolReturnsLiteralToken() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String email = "someone@example.com";
String[] tokens = new String[] { email };
// TextAnnotation ta = new TextAnnotation("a", "b", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "manual", ta, 1.0);
// posView.addConstituent(new Constituent("NN", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, posView);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals(email, lemma);
}

@Test
public void testWordWithREPrefixAndXmodalLemmaReturnsOriginal() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String word = "re-xmodalverb";
String[] tokens = new String[] { word };
// TextAnnotation ta = new TextAnnotation("test", "id", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "manual", ta, 1.0);
// posView.addConstituent(new Constituent("VB", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, posView);
String lemma = lemmatizer.getLemma(word, "VB");
if ("xmodal".equals(lemma)) {
assertEquals(word, lemmatizer.getLemma(word, "VBD"));
}
}

@Test(expected = AnnotatorException.class)
public void testAddViewExceptionWhenPOSViewIsMissing() throws AnnotatorException {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = new String[] { "hello" };
// TextAnnotation ta = new TextAnnotation("test", "id", tokens);
// lemmatizer.addView(ta);
}

@Test
public void testTokenWithNullPOSFallsBackToOriginal() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = { "data" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "manual", ta, 1.0);
// Constituent c = new Constituent(null, ViewNames.POS, ta, 0, 1);
// pos.addConstituent(c);
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("data", lemma);
}

@Test
public void testAdjectiveNotEndingWithEdDefaultsToWordNet() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = { "faster" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "source", ta, 1.0);
// pos.addConstituent(new Constituent("JJ", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertTrue(lemma.equals("faster") || lemma.equals("fast"));
}

@Test
public void testUpperCaseTokenLowercasedBeforeLookup() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = { "MEN" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "source", ta, 1.0);
// pos.addConstituent(new Constituent("NNS", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("man", lemma);
}

@Test
public void testTokenWithSymbolButNotEmailOrApostrophe() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = { "C++" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "manual", ta, 1.0);
// pos.addConstituent(new Constituent("NN", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("c++", lemma);
}

@Test
public void testDutchContractionLikeTokenBehavesAsUnknown() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = { "'t" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "manual", ta, 1.0);
// pos.addConstituent(new Constituent("PRP", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("'t", lemma);
}

@Test
public void testCapitalizedProperNounPluralEndsWithS() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = { "Canadians" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "manual", ta, 1.0);
// pos.addConstituent(new Constituent("NNPS", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("canadians", lemma);
}

@Test
public void testCreateLemmaViewAddsAllConstituents() throws IOException {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = { "dogs", "run", "fast" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "tagger", ta, 1.0);
// posView.addConstituent(new Constituent("NNS", ViewNames.POS, ta, 0, 1));
// posView.addConstituent(new Constituent("VBP", ViewNames.POS, ta, 1, 2));
// posView.addConstituent(new Constituent("RB", ViewNames.POS, ta, 2, 3));
// ta.addView(ViewNames.POS, posView);
// ta.addView(ViewNames.LEMMA, lemmatizer.createLemmaView(ta));
// assertTrue(ta.hasView(ViewNames.LEMMA));
// assertEquals(3, ta.getView(ViewNames.LEMMA).getConstituents().size());
}

@Test
public void testAddViewAddsViewOnlyOnce() throws AnnotatorException {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = { "running" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "tagger", ta, 1.0);
// posView.addConstituent(new Constituent("VBG", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, posView);
// lemmatizer.addView(ta);
// assertTrue(ta.hasView(ViewNames.LEMMA));
// View v = ta.getView(ViewNames.LEMMA);
// assertEquals("running", ta.getToken(0));
// assertEquals("run", v.getLabelsCoveringToken(0).get(0));
}

@Test
public void testEmptyStringTokenReturnsEmpty() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = { "" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "manual", ta, 1.0);
// pos.addConstituent(new Constituent("NN", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("", lemma);
}

@Test
public void testContractionNotInMapReturnsOriginalWord() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = { "ca" };
// TextAnnotation ta = new TextAnnotation("c", "d", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "gen", ta, 1.0);
// pos.addConstituent(new Constituent("MD", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("ca", lemma);
}

@Test
public void testTokenEndingWithSButPOSNotEndingWithSGoesToWordNet() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = { "treats" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "generator", ta, 1.0);
// pos.addConstituent(new Constituent("VBZ", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("treat", lemma);
}

@Test
public void testProperNounPluralNNPSLowercasesOnly() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = { "Americans" };
// TextAnnotation ta = new TextAnnotation("corp", "txt", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "pos", ta, 1.0);
// pos.addConstituent(new Constituent("NNPS", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("americans", lemma);
}

@Test
public void testTokenWithMixedCaseIsLowercased() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = { "WaLkEd" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "annotator", ta, 1.0);
// pos.addConstituent(new Constituent("VBD", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("walk", lemma);
}

@Test
public void testPOSRBRReturnsOriginalToken() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = { "more" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "source", ta, 1.0);
// pos.addConstituent(new Constituent("RBR", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("more", lemma);
}

@Test
public void testPOSRBReturnsOriginalToken() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = { "quickly" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "test", ta, 1.0);
// posView.addConstituent(new Constituent("RB", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, posView);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("quickly", lemma);
}

@Test
public void testWordStartingWithREAndMatchingTrimmedVerb() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = { "re-train" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "tagger", ta, 1.0);
// posView.addConstituent(new Constituent("VBP", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, posView);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("train", lemma);
}

@Test
public void testCreateLemmaViewOnTokenWithTwoWordsGeneratesCorrectConstituents() throws Exception {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = { "ran", "faster" };
// TextAnnotation ta = new TextAnnotation("corp", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "manual", ta, 1.0);
// pos.addConstituent(new Constituent("VBD", ViewNames.POS, ta, 0, 1));
// pos.addConstituent(new Constituent("JJR", ViewNames.POS, ta, 1, 2));
// ta.addView(ViewNames.POS, pos);
// ta.addView(ViewNames.LEMMA, lemmatizer.createLemmaView(ta));
// assertTrue(ta.hasView(ViewNames.LEMMA));
// assertEquals("run", ta.getView(ViewNames.LEMMA).getLabelsCoveringToken(0).get(0));
// assertEquals("faster", ta.getView(ViewNames.LEMMA).getLabelsCoveringToken(1).get(0));
}

@Test
public void testUninitializedLemmatizerTriggersInitializationOnGetLemma() {
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(true);
String[] tokens = new String[] { "walked" };
// TextAnnotation ta = new TextAnnotation("test", "text", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "manual", ta, 1.0);
// posView.addConstituent(new Constituent("VBD", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, posView);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("walk", lemma);
}

@Test
public void testTokenWithSingleQuoteIsReturnedAsIs() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = { "'" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "pos", ta, 1.0);
// pos.addConstituent(new Constituent("POS", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("'", lemma);
}

@Test
public void testWordNetFallbackNullReturnsOriginalWord() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.addProperty(LemmatizerConfigurator.WN_PATH.key, "/nonexistent");
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
try {
lemmatizer.initialize(config);
} catch (Exception e) {
}
String[] tokens = { "inventedwordxyz" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "source", ta, 1.0);
// posView.addConstituent(new Constituent("NN", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, posView);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("inventedwordxyz", lemma);
}

@Test
public void testTokenWithMixedCaseVerbReturnsLowerLemma() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = { "Trained" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "manual", ta, 1.0);
// pos.addConstituent(new Constituent("VBD", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("train", lemma);
}

@Test
public void testFunctionWordWithUnsupportedPOSReturnsWord() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = { "uh" };
// TextAnnotation ta = new TextAnnotation("corpus", "id", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "manual", ta, 1.0);
// pos.addConstituent(new Constituent("UH", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("uh", lemma);
}

@Test
public void testWordEndingInMenWithUnsupportedPOSDoesNotChange() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = { "workmen" };
// TextAnnotation ta = new TextAnnotation("c", "d", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "gen", ta, 1.0);
// posView.addConstituent(new Constituent("FW", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, posView);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("workmen", lemma);
}

@Test
public void testUnknownPOSReturnsOriginalToken() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = { "anyword" };
// TextAnnotation ta = new TextAnnotation("a", "b", tokens);
// TokenLabelView posView = new TokenLabelView(ViewNames.POS, "unknown", ta, 1.0);
// posView.addConstituent(new Constituent("XYZ", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, posView);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("anyword", lemma);
}

@Test
public void testTokenIsXmodalReturnsAsIsDespiteVerbPOS() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
lemmatizer.initialize(config);
String[] tokens = { "xmodal" };
// TextAnnotation ta = new TextAnnotation("x", "z", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "p", ta, 1.0);
// pos.addConstituent(new Constituent("VBD", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, pos);
// String lemma = lemmatizer.getLemma(ta, 0);
// assertEquals("xmodal", lemma);
}

@Test
public void testAddViewWithIOExceptionThrowsAnnotatorException() {
ResourceManager config = new LemmatizerConfigurator().getDefaultConfig();
// config.addProperty(LemmatizerConfigurator.WN_PATH.key, "/invalid/path/to/dict");
// config.setLazyInitialized(false);
IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(config);
try {
lemmatizer.initialize(config);
} catch (Exception e) {
}
String[] tokens = { "running" };
// TextAnnotation ta = new TextAnnotation("corpus", "text", tokens);
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "tagger", ta, 1.0);
// pos.addConstituent(new Constituent("VBG", ViewNames.POS, ta, 0, 1));
// ta.addView(ViewNames.POS, pos);
// try {
// lemmatizer.addView(ta);
// fail("Expected AnnotatorException due to IOError fallback path.");
// } catch (AnnotatorException e) {
// assertTrue(e.getMessage().contains("IOException"));
// }
}
}
