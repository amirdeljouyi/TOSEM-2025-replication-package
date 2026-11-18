package edu.illinois.cs.cogcomp.nlp.lemmatizer;

import edu.illinois.cs.cogcomp.core.datastructures.IntPair;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TokenLabelView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.View;
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.*;

public class IllinoisLemmatizerPromptTest {

    @Test
    public void defaultConstructorIsLazyAndInitializesOnFirstLookup() {
        IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer();

        assertFalse(lemmatizer.isInitialized());
        assertEquals("media", lemmatizer.getLemma("media", "NNS"));
        assertTrue(lemmatizer.isInitialized());
    }

    @Test
    public void eagerConstructorLoadsResourcesImmediately() {
        IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(false);

        assertTrue(lemmatizer.isInitialized());
        assertEquals("man", lemmatizer.getLemma("men", "NNS"));
    }

    @Test
    public void lemmatizesRegularAndIrregularVerbsAcrossTags() {
        IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer();

        assertEquals("retake", lemmatizer.getLemma("retakes", "VBZ"));
        assertEquals("put", lemmatizer.getLemma("putting", "VBG"));
        assertEquals("go", lemmatizer.getLemma("went", "VBD"));
        assertEquals("be", lemmatizer.getLemma("IS", "VBZ"));
        assertEquals("put", lemmatizer.getLemma("put", "VB"));
    }

    @Test
    public void lowercasesBeforeApplyingFunctionWordRules() {
        IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer();

        assertEquals("the", lemmatizer.getLemma("THE", "DT"));
        assertEquals("and", lemmatizer.getLemma("And", "CC"));
        assertEquals("42", lemmatizer.getLemma("42", "CD"));
    }

    @Test
    public void handlesEnglishContractionsIndependentlyOfPos() {
        IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer();

        assertEquals("have", lemmatizer.getLemma("'D", "MD"));
        assertEquals("will", lemmatizer.getLemma("'ll", "NN"));
        assertEquals("be", lemmatizer.getLemma("'RE", "VBP"));
        assertEquals("be", lemmatizer.getLemma("'m", "PRP"));
        assertEquals("have", lemmatizer.getLemma("'ve", "VBP"));
        assertEquals("'s", lemmatizer.getLemma("'s", "POS"));
    }

    @Test
    public void treatsAsciiAndCurlySAsBeWhenVerbTagged() {
        IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer();

        assertEquals("be", lemmatizer.getLemma("'s", "VBZ"));
        assertEquals("be", lemmatizer.getLemma("’S", "VBZ"));
    }

    @Test
    public void preservesEmailLikeTokensAfterLowercasing() {
        IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer();

        assertEquals("person@example.com", lemmatizer.getLemma("Person@Example.COM", "NNS"));
        assertEquals("@handle", lemmatizer.getLemma("@Handle", "VBZ"));
    }

    @Test
    public void appliesRulesForProperNounsAdverbsAndParticipialAdjectives() {
        IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer();

        assertEquals("illinois", lemmatizer.getLemma("Illinois", "NNP"));
        assertEquals("americans", lemmatizer.getLemma("Americans", "NNPS"));
        assertEquals("better", lemmatizer.getLemma("BETTER", "RBR"));
        assertEquals("fastest", lemmatizer.getLemma("Fastest", "RBS"));
        assertEquals("quickly", lemmatizer.getLemma("Quickly", "RB"));
        assertEquals("talented", lemmatizer.getLemma("Talented", "JJ"));
    }

    @Test
    public void usesWordnetForCommonNominalAndAdjectivalInflections() {
        IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer();

        assertEquals("child", lemmatizer.getLemma("children", "NNS"));
        assertEquals("mouse", lemmatizer.getLemma("mice", "NNS"));
        assertEquals("better", lemmatizer.getLemma("better", "JJR"));
    }

    @Test
    public void handlesRePrefixedKnownVerbForms() {
        IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer();

        assertEquals("take", lemmatizer.getLemma("re-takes", "NN"));
        assertEquals("write", lemmatizer.getLemma("re-wrote", "VBD"));
    }

    @Test
    public void stanfordConventionConfigurationNormalizesPronounsAndArticles() {
        Properties properties = new Properties();
        properties.setProperty("useStanfordConventions", "true");
        ResourceManager manager = new ResourceManager(properties);
        IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer(manager);

        assertEquals("she", lemmatizer.getLemma("HER", "PRP"));
        assertEquals("he", lemmatizer.getLemma("him", "PRP"));
        assertEquals("they", lemmatizer.getLemma("their", "PRP$"));
        assertEquals("they", lemmatizer.getLemma("them", "PRP"));
        assertEquals("i", lemmatizer.getLemma("me", "PRP"));
        assertEquals("a", lemmatizer.getLemma("an", "DT"));
    }

    @Test
    public void defaultConfigurationDoesNotApplyStanfordPronounConvention() {
        IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer();

        assertEquals("her", lemmatizer.getLemma("her", "PRP"));
        assertEquals("him", lemmatizer.getLemma("him", "PRP"));
        assertEquals("their", lemmatizer.getLemma("their", "PRP$"));
    }

    @Test
    public void textAnnotationLookupUsesTokenAndCorrespondingPosLabel() {
        TextAnnotation annotation = new TextAnnotation("corpus", "doc", "MEN went home",
                new IntPair[] {new IntPair(0, 3), new IntPair(4, 8), new IntPair(9, 13)},
                new String[] {"MEN", "went", "home"}, new int[] {3});
        TokenLabelView pos = new TokenLabelView(ViewNames.POS, "manual-pos", annotation, 1.0);
        pos.addTokenLabel(0, "NNS", 1.0);
        pos.addTokenLabel(1, "VBD", 1.0);
        pos.addTokenLabel(2, "NN", 1.0);
        annotation.addView(ViewNames.POS, pos);
        IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer();

        assertEquals("man", lemmatizer.getLemma(annotation, 0));
        assertEquals("go", lemmatizer.getLemma(annotation, 1));
        assertEquals("home", lemmatizer.getLemma(annotation, 2));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void deprecatedSingleLemmaDelegatesToCurrentLookup() {
        TextAnnotation annotation = new TextAnnotation("", "", "Children",
                new IntPair[] {new IntPair(0, 8)}, new String[] {"Children"}, new int[] {1});
        TokenLabelView pos = new TokenLabelView(ViewNames.POS, annotation);
        pos.addTokenLabel(0, "NNS", 1.0);
        annotation.addView(ViewNames.POS, pos);
        IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer();

        assertEquals(lemmatizer.getLemma(annotation, 0), lemmatizer.getSingleLemma(annotation, 0));
        assertEquals("child", lemmatizer.getSingleLemma(annotation, 0));
    }

    @Test
    public void tokenIndexEqualToLengthProducesDescriptiveFailure() {
        TextAnnotation annotation = new TextAnnotation("", "", "only",
                new IntPair[] {new IntPair(0, 4)}, new String[] {"only"}, new int[] {1});
        TokenLabelView pos = new TokenLabelView(ViewNames.POS, annotation);
        pos.addTokenLabel(0, "RB", 1.0);
        annotation.addView(ViewNames.POS, pos);
        IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer();

        try {
            lemmatizer.getLemma(annotation, 1);
            fail("Expected an out-of-range token index to be rejected");
        } catch (IllegalArgumentException exception) {
            assertTrue(exception.getMessage().contains("index '1' is out of range"));
            assertTrue(exception.getMessage().contains("has '1' tokens"));
        }
    }

    @Test(expected = NullPointerException.class)
    public void nullWordIsRejected() {
        IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer();

        lemmatizer.getLemma(null, "NN");
    }

    @Test(expected = NullPointerException.class)
    public void nullPosIsRejected() {
        IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer();

        lemmatizer.getLemma("word", null);
    }

    @Test
    public void createLemmaViewAddsOneAlignedLabelPerToken() throws Exception {
        TextAnnotation annotation = new TextAnnotation("news", "article-7",
                "The children were running",
                new IntPair[] {new IntPair(0, 3), new IntPair(4, 12),
                        new IntPair(13, 17), new IntPair(18, 25)},
                new String[] {"The", "children", "were", "running"}, new int[] {4});
        TokenLabelView pos = new TokenLabelView(ViewNames.POS, "pipeline-pos", annotation, 1.0);
        pos.addTokenLabel(0, "DT", 1.0);
        pos.addTokenLabel(1, "NNS", 1.0);
        pos.addTokenLabel(2, "VBD", 1.0);
        pos.addTokenLabel(3, "VBG", 1.0);
        annotation.addView(ViewNames.POS, pos);
        IllinoisLemmatizer lemmatizer = new IllinoisLemmatizer();

        View result = lemmatizer.createLemmaView(annotation);

        assertSame(result, annotation.getView(ViewNames.LEMMA));
        assertEquals(ViewNames.LEMMA, result.getViewName());
        assertEquals(4, result.getNumberOfConstituents());
        assertEquals(Arrays.asList("the"), result.getLabelsCoveringToken(0));
        assertEquals(Arrays.asList("child"), result.getLabelsCoveringToken(1));
        assertEquals(Arrays.asList("be"), result.getLabelsCoveringToken(2));
        assertEquals(Arrays.asList("run"), result.getLabelsCoveringToken(3));
    }

    @Test
    public void addViewAndAnnotatorGetViewIntegrateWithTextAnnotation() throws Exception {
        TextAnnotation first = new TextAnnotation("", "first", "Mice ate",
                new IntPair[] {new IntPair(0, 4), new IntPair(5, 8)},
                new String[] {"Mice", "ate"}, new int[] {2});
        TokenLabelView firstPos = new TokenLabelView(ViewNames.POS, first);
        firstPos.addTokenLabel(0, "NNS", 1.0);
        firstPos.addTokenLabel(1, "VBD", 1.0);
        first.addView(ViewNames.POS, firstPos);
        IllinoisLemmatizer direct = new IllinoisLemmatizer();
        direct.addView(first);

        assertEquals("mouse", first.getView(ViewNames.LEMMA).getLabelsCoveringToken(0).get(0));
        assertEquals("eat", first.getView(ViewNames.LEMMA).getLabelsCoveringToken(1).get(0));

        TextAnnotation second = new TextAnnotation("", "second", "Women spoke",
                new IntPair[] {new IntPair(0, 5), new IntPair(6, 11)},
                new String[] {"Women", "spoke"}, new int[] {2});
        TokenLabelView secondPos = new TokenLabelView(ViewNames.POS, second);
        secondPos.addTokenLabel(0, "NNS", 1.0);
        secondPos.addTokenLabel(1, "VBD", 1.0);
        second.addView(ViewNames.POS, secondPos);
        IllinoisLemmatizer throughAnnotator = new IllinoisLemmatizer();
        View generated = throughAnnotator.getView(second);

        assertSame(generated, second.getView(ViewNames.LEMMA));
        assertEquals("woman", generated.getLabelsCoveringToken(0).get(0));
        assertEquals("speak", generated.getLabelsCoveringToken(1).get(0));
    }

    @Test
    public void classpathReaderReturnsPackagedLexiconContent() {
        List<String> verbLines = IllinoisLemmatizer.readFromClasspath("verb-lemDict.txt");
        List<String> exceptionLines = IllinoisLemmatizer.readFromClasspath("exceptions.txt");

        assertNotNull(verbLines);
        assertNotNull(exceptionLines);
        assertFalse(verbLines.isEmpty());
        assertFalse(exceptionLines.isEmpty());
        assertFalse(verbLines.get(0).trim().isEmpty());
        assertFalse(exceptionLines.get(0).trim().isEmpty());
    }
}
