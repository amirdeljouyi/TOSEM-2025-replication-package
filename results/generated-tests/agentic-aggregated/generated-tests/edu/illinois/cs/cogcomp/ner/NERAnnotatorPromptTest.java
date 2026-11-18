package edu.illinois.cs.cogcomp.ner;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

import org.junit.Test;

import edu.illinois.cs.cogcomp.core.datastructures.IntPair;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager;
import edu.illinois.cs.cogcomp.lbjava.classify.Feature;

public class NERAnnotatorPromptTest {

    @Test
    public void defaultConstructorIsLazyAndExposesRequestedViewName() throws Exception {
        NERAnnotator annotator = new NERAnnotator("NER_CONLL");

        assertEquals("NER_CONLL", annotator.getViewName());
        assertFalse(annotator.isInitialized());
        assertArrayEquals(new String[0], annotator.getRequiredViews());
        assertTrue(annotator.isSentenceLevel());
    }

    @Test
    public void resourceManagerCanSelectEagerInitialization() {
        Properties properties = new Properties();
        properties.setProperty("isLazilyInitialized", "false");

        NERAnnotator annotator =
                new NERAnnotator(new ResourceManager(properties), "NER_CONLL");

        assertTrue(annotator.isInitialized());
        assertEquals("NER_CONLL", annotator.getViewName());
    }

    @Test
    public void firstGetViewInitializesBeforeReportingUnavailableModelView() throws Exception {
        NERAnnotator annotator = new NERAnnotator("NER_CONLL");
        TextAnnotation text = new TextAnnotation("news", "doc-1",
                "John Smith visited Chicago .",
                new IntPair[] {new IntPair(0, 4), new IntPair(5, 10), new IntPair(11, 18),
                        new IntPair(19, 26), new IntPair(27, 28)},
                new String[] {"John", "Smith", "visited", "Chicago", "."}, new int[] {5});

        try {
            annotator.getView(text);
            fail("An empty benchmark model cache cannot produce a view");
        } catch (IllegalArgumentException expected) {
            assertEquals("View NER_CONLL not found", expected.getMessage());
            assertTrue(annotator.isInitialized());
            assertFalse(text.hasView("NER_CONLL"));
            assertArrayEquals(new String[] {"John", "Smith", "visited", "Chicago", "."},
                    text.getTokens());
        }
    }

    @Test
    public void unsupportedCustomViewNameIsRejectedDuringInitialization() throws Exception {
        String customView = "MY_NER_VIEW";
        NERAnnotator annotator = new NERAnnotator(customView);
        TextAnnotation text = new TextAnnotation("corpus", "custom", "Barack Obama spoke in Paris",
                new IntPair[] {new IntPair(0, 6), new IntPair(7, 12), new IntPair(13, 18),
                        new IntPair(19, 21), new IntPair(22, 27)},
                new String[] {"Barack", "Obama", "spoke", "in", "Paris"}, new int[] {5});
        try {
            annotator.getView(text);
            fail("A custom view has no default model mapping");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("no default with view name " + customView));
            assertFalse(annotator.isInitialized());
            assertFalse(text.hasView(customView));
            assertArrayEquals(new String[] {"Barack", "Obama", "spoke", "in", "Paris"},
                    text.getTokens());
        }
    }

    @Test
    public void failedMultiSentenceAnnotationPreservesInputStructure() throws Exception {
        NERAnnotator annotator = new NERAnnotator("NER_CONLL");
        TextAnnotation text = new TextAnnotation("news", "two-sentences",
                "Alice works at Google .\nBob moved to London .",
                new IntPair[] {new IntPair(0, 5), new IntPair(6, 11), new IntPair(12, 14),
                        new IntPair(15, 21), new IntPair(22, 23), new IntPair(24, 27),
                        new IntPair(28, 33), new IntPair(34, 36), new IntPair(37, 43),
                        new IntPair(44, 45)},
                new String[] {"Alice", "works", "at", "Google", ".", "Bob", "moved", "to",
                        "London", "."}, new int[] {5, 10});

        try {
            annotator.getView(text);
            fail("An empty benchmark model cache cannot produce a view");
        } catch (IllegalArgumentException expected) {
            assertEquals(2, text.getNumberOfSentences());
            assertEquals(10, text.size());
            assertEquals("Alice", text.getToken(0));
            assertEquals("London", text.getToken(8));
            assertFalse(text.hasView("NER_CONLL"));
        }
    }

    @Test
    public void initializedDirectAddWithUnavailableModelDoesNotCorruptSingleTokenText()
            throws Exception {
        NERAnnotator annotator = new NERAnnotator("NER_CONLL");
        TextAnnotation text = new TextAnnotation("news", "last-token", "Chicago",
                new IntPair[] {new IntPair(0, 7)}, new String[] {"Chicago"}, new int[] {1});

        assertTrue(annotator.getTagValues().isEmpty());
        annotator.addView(text);

        assertTrue(annotator.isInitialized());
        assertFalse(text.hasView("NER_CONLL"));
        assertEquals(1, text.size());
        assertEquals("Chicago", text.getToken(0));
    }

    @Test
    public void repeatedGetViewAfterInitializationConsistentlyReportsMissingView() throws Exception {
        NERAnnotator annotator = new NERAnnotator("NER_CONLL");
        TextAnnotation text = new TextAnnotation("news", "repeat", "Microsoft hired Alice .",
                new IntPair[] {new IntPair(0, 9), new IntPair(10, 15), new IntPair(16, 21),
                        new IntPair(22, 23)},
                new String[] {"Microsoft", "hired", "Alice", "."}, new int[] {4});

        try {
            annotator.getView(text);
            fail("First call should report the absent model view");
        } catch (IllegalArgumentException first) {
            assertTrue(annotator.isInitialized());
            assertEquals("View NER_CONLL not found", first.getMessage());
        }
        try {
            annotator.getView(text);
            fail("Second call should report the absent model view");
        } catch (IllegalArgumentException second) {
            assertTrue(annotator.isInitialized());
            assertEquals("View NER_CONLL not found", second.getMessage());
            assertFalse(text.hasView("NER_CONLL"));
        }
    }

    @Test
    public void runtimeAttributesPathInitializesBeforeReportingMissingView() throws Exception {
        NERAnnotator annotator = new NERAnnotator("NER_CONLL");
        TextAnnotation text = new TextAnnotation("news", "runtime", "IBM is in Armonk .",
                new IntPair[] {new IntPair(0, 3), new IntPair(4, 6), new IntPair(7, 9),
                        new IntPair(10, 16), new IntPair(17, 18)},
                new String[] {"IBM", "is", "in", "Armonk", "."}, new int[] {5});
        Properties runtimeProperties = new Properties();
        runtimeProperties.setProperty("unused", "value");

        try {
            annotator.getView(text, new ResourceManager(runtimeProperties));
            fail("An empty benchmark model cache cannot produce a view");
        } catch (IllegalArgumentException expected) {
            assertEquals("View NER_CONLL not found", expected.getMessage());
            assertTrue(annotator.isInitialized());
            assertFalse(text.hasView("NER_CONLL"));
        }
    }

    @Test
    public void tagValuesReflectEmptyLexiconWhenExternalModelFilesAreUnavailable() throws Exception {
        NERAnnotator annotator = new NERAnnotator("NER_CONLL");

        Set<String> actual = annotator.getTagValues();

        assertNotNull(actual);
        assertTrue(actual.isEmpty());
        assertTrue(annotator.isInitialized());
    }

    @Test
    public void returnedTagSetIsIndependentAcrossCalls() throws Exception {
        NERAnnotator annotator = new NERAnnotator("NER_CONLL");

        Set<String> first = annotator.getTagValues();
        first.clear();
        Set<String> second = annotator.getTagValues();

        assertNotSame(first, second);
        assertTrue(first.isEmpty());
        assertTrue(second.isEmpty());
    }

    @Test
    public void levelOneFeatureWeightsReturnEmptyIndependentMapWithoutModelFiles() throws Exception {
        NERAnnotator annotator = new NERAnnotator("NER_CONLL");

        HashMap<Feature, double[]> first = annotator.getL1FeatureWeights();
        HashMap<Feature, double[]> second = annotator.getL1FeatureWeights();

        assertTrue(first.isEmpty());
        assertTrue(second.isEmpty());
        assertNotSame(first, second);
        assertTrue(annotator.isInitialized());
    }

    @Test
    public void levelTwoFeatureWeightsReturnEmptyIndependentMapWithoutModelFiles() throws Exception {
        NERAnnotator annotator = new NERAnnotator("NER_CONLL");

        HashMap<Feature, double[]> first = annotator.getL2FeatureWeights();
        HashMap<Feature, double[]> second = annotator.getL2FeatureWeights();

        assertTrue(first.isEmpty());
        assertTrue(second.isEmpty());
        assertNotSame(first, second);
        assertEquals(first.keySet(), second.keySet());
        assertTrue(annotator.isInitialized());
    }

    @Test
    public void malformedDirectAddBeforeLazyInitializationFails() throws Exception {
        NERAnnotator annotator = new NERAnnotator("NER_CONLL");
        TextAnnotation text = new TextAnnotation("news", "misuse", "Paris",
                new IntPair[] {new IntPair(0, 5)}, new String[] {"Paris"}, new int[] {1});

        try {
            annotator.addView(text);
            fail("Direct addView should not bypass lazy initialization");
        } catch (NullPointerException expected) {
            assertFalse(annotator.isInitialized());
            assertFalse(text.hasView("NER_CONLL"));
        }
    }

    @Test
    public void missingConfigurationFileIsRejected() {
        try {
            new NERAnnotator("definitely-missing-ner-config.properties", "NER_CONLL");
            fail("A nonexistent configuration should not be accepted");
        } catch (IOException expected) {
            assertNotNull(expected.getMessage());
        }
    }

    @Test
    public void mainRejectsEmptyArgumentList() {
        try {
            NERAnnotator.main(new String[0]);
            fail("main should fail after reporting missing arguments");
        } catch (ArrayIndexOutOfBoundsException expected) {
            assertNotNull(expected);
        }
    }
}
