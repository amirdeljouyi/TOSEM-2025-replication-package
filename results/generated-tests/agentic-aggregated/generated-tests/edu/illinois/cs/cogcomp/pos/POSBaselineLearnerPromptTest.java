package edu.illinois.cs.cogcomp.pos;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

import org.junit.Test;

import edu.illinois.cs.cogcomp.core.datastructures.vectors.ExceptionlessInputStream;
import edu.illinois.cs.cogcomp.core.datastructures.vectors.ExceptionlessOutputStream;
import edu.illinois.cs.cogcomp.lbjava.classify.Classifier;
import edu.illinois.cs.cogcomp.lbjava.classify.Feature;
import edu.illinois.cs.cogcomp.lbjava.classify.FeatureVector;
import edu.illinois.cs.cogcomp.lbjava.learn.Learner;

public class POSBaselineLearnerPromptTest {
    @Test
    public void defaultAndNamedConstructorsReportExpectedMetadata() {
        POSBaselineLearner unnamed = new POSBaselineLearner();
        POSBaselineLearner named = new POSBaselineLearner("edu.example.MyTagger");
        named.setExtractor(new Classifier("forms") {
            public FeatureVector classify(Object example) { return new FeatureVector(); }
            public String discreteValue(Object example) { return (String) example; }
        });

        assertEquals("edu.illinois.cs.cogcomp.lbjava.nlp.Word", unnamed.getInputType());
        assertEquals("edu.illinois.cs.cogcomp.lbjava.nlp.Word", named.getInputType());
        Feature prediction = named.featureValue("anything");
        assertEquals("edu.example", prediction.getPackage());
        assertEquals("MyTagger", prediction.getGeneratingClassifier());
    }

    @Test
    public void parametersConstructorCreatesEmptyLearner() {
        POSBaselineLearner.Parameters original = new POSBaselineLearner.Parameters();
        POSBaselineLearner.Parameters copy = new POSBaselineLearner.Parameters(original);
        POSBaselineLearner learner = new POSBaselineLearner(copy);

        assertEquals("edu.illinois.cs.cogcomp.lbjava.nlp.Word", learner.getInputType());
        assertFalse(learner.observed("token"));
        assertEquals(0, learner.observedCount("token"));
    }

    @Test
    public void looksLikeNumberAcceptsDigitsAndPermittedPunctuation() {
        assertTrue(POSBaselineLearner.looksLikeNumber("123"));
        assertTrue(POSBaselineLearner.looksLikeNumber("-1,234.50"));
        assertTrue(POSBaselineLearner.looksLikeNumber(".,-7-,."));
        assertTrue(POSBaselineLearner.looksLikeNumber("\u0661\u0662.\u0663"));
    }

    @Test
    public void looksLikeNumberRejectsMissingDigitsAndOtherCharacters() {
        assertFalse(POSBaselineLearner.looksLikeNumber(""));
        assertFalse(POSBaselineLearner.looksLikeNumber(".,-"));
        assertFalse(POSBaselineLearner.looksLikeNumber("12a"));
        assertFalse(POSBaselineLearner.looksLikeNumber("$12"));
        assertFalse(POSBaselineLearner.looksLikeNumber("1 2"));
    }

    @Test(expected = NullPointerException.class)
    public void looksLikeNumberRejectsNull() {
        POSBaselineLearner.looksLikeNumber(null);
    }

    @Test
    public void unseenFormsUseSpecialCasePredictions() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.setExtractor(new Classifier("forms") {
            public FeatureVector classify(Object example) { return new FeatureVector(); }
            public String discreteValue(Object example) { return (String) example; }
        });

        assertEquals(":", learner.discreteValue(";"));
        assertEquals("CD", learner.discreteValue("-9,000.25"));
        assertEquals("UNKNOWN", learner.discreteValue("unseen"));
        assertEquals("UNKNOWN", learner.discreteValue("."));
        assertFalse(learner.observed(";"));
        assertFalse(learner.observed("-9,000.25"));
    }

    @Test
    public void learningCountsLabelsAndChoosesMostFrequentTag() {
        POSBaselineLearner learner = new POSBaselineLearner("tagger");
        learner.setExtractor(new Classifier("forms") {
            public FeatureVector classify(Object example) { return new FeatureVector(); }
            public String discreteValue(Object example) { return ((String[]) example)[0]; }
        });
        learner.setLabeler(new Classifier("labels") {
            public FeatureVector classify(Object example) { return new FeatureVector(); }
            public String discreteValue(Object example) { return ((String[]) example)[1]; }
        });

        learner.learn((Object) new String[] {"record", "NN"});
        learner.learn((Object) new String[] {"record", "VB"});
        learner.learn((Object) new String[] {"record", "VB"});
        learner.learn((Object) new String[] {"other", "JJ"});

        assertTrue(learner.observed("record"));
        assertEquals(3, learner.observedCount("record"));
        assertEquals("VB", learner.discreteValue(new String[] {"record", "ignored"}));
        assertEquals(new HashSet<>(Arrays.asList("NN", "VB")), learner.allowableTags("record"));
        assertEquals(1, learner.observedCount("other"));
    }

    @Test
    public void tiedCountsChooseLexicographicallyFirstTag() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.setExtractor(new Classifier("forms") {
            public FeatureVector classify(Object example) { return new FeatureVector(); }
            public String discreteValue(Object example) { return ((String[]) example)[0]; }
        });
        learner.setLabeler(new Classifier("labels") {
            public FeatureVector classify(Object example) { return new FeatureVector(); }
            public String discreteValue(Object example) { return ((String[]) example)[1]; }
        });

        learner.learn((Object) new String[] {"lead", "VB"});
        learner.learn((Object) new String[] {"lead", "NN"});

        assertEquals("NN", learner.discreteValue(new String[] {"lead", "unused"}));
        assertEquals(2, learner.observedCount("lead"));
    }

    @Test
    public void observedNumericAndSemicolonFormsOverrideFallbackRules() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.setExtractor(new Classifier("forms") {
            public FeatureVector classify(Object example) { return new FeatureVector(); }
            public String discreteValue(Object example) { return ((String[]) example)[0]; }
        });
        learner.setLabeler(new Classifier("labels") {
            public FeatureVector classify(Object example) { return new FeatureVector(); }
            public String discreteValue(Object example) { return ((String[]) example)[1]; }
        });

        learner.learn((Object) new String[] {"42", "NN"});
        learner.learn((Object) new String[] {";", "PUNC"});

        assertEquals("NN", learner.discreteValue(new String[] {"42", "unused"}));
        assertEquals("PUNC", learner.discreteValue(new String[] {";", "unused"}));
        assertEquals(Collections.singleton("NN"), learner.allowableTags("42"));
        assertEquals(Collections.singleton("PUNC"), learner.allowableTags(";"));
    }

    @Test
    public void allowableTagsForUnseenFormsReflectFallbackPossibilities() {
        POSBaselineLearner learner = new POSBaselineLearner();

        assertEquals(Collections.singleton(":"), learner.allowableTags(";"));
        assertEquals(Collections.singleton("CD"), learner.allowableTags("12.5"));
        assertTrue(learner.allowableTags("word").isEmpty());
        assertTrue(learner.allowableTags("").isEmpty());
    }

    @Test
    public void forgetRemovesAllLearnedStatisticsAndRestoresFallback() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.setExtractor(new Classifier("forms") {
            public FeatureVector classify(Object example) { return new FeatureVector(); }
            public String discreteValue(Object example) { return ((String[]) example)[0]; }
        });
        learner.setLabeler(new Classifier("labels") {
            public FeatureVector classify(Object example) { return new FeatureVector(); }
            public String discreteValue(Object example) { return ((String[]) example)[1]; }
        });
        learner.learn((Object) new String[] {"15", "NN"});
        learner.learn((Object) new String[] {"word", "VB"});

        learner.forget();

        assertFalse(learner.observed("15"));
        assertFalse(learner.observed("word"));
        assertEquals(0, learner.observedCount("15"));
        assertEquals("CD", learner.discreteValue(new String[] {"15", "unused"}));
        assertEquals("UNKNOWN", learner.discreteValue(new String[] {"word", "unused"}));
    }

    @Test
    public void emptyCloneHasNoTrainingAndDoesNotShareFutureState() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.setExtractor(new Classifier("forms") {
            public FeatureVector classify(Object example) { return new FeatureVector(); }
            public String discreteValue(Object example) { return ((String[]) example)[0]; }
        });
        learner.setLabeler(new Classifier("labels") {
            public FeatureVector classify(Object example) { return new FeatureVector(); }
            public String discreteValue(Object example) { return ((String[]) example)[1]; }
        });
        learner.learn((Object) new String[] {"trained", "NN"});

        Learner rawClone = learner.emptyClone();
        POSBaselineLearner clone = (POSBaselineLearner) rawClone;
        clone.setExtractor(learner.getExtractor());
        clone.setLabeler(learner.getLabeler());
        clone.learn((Object) new String[] {"cloneOnly", "JJ"});

        assertFalse(clone.observed("trained"));
        assertTrue(clone.observed("cloneOnly"));
        assertTrue(learner.observed("trained"));
        assertFalse(learner.observed("cloneOnly"));
    }

    @Test
    public void featureValueCarriesPredictionAndClassifierIdentity() {
        POSBaselineLearner learner = new POSBaselineLearner("edu.demo.Baseline");
        learner.setExtractor(new Classifier("forms") {
            public FeatureVector classify(Object example) { return new FeatureVector(); }
            public String discreteValue(Object example) { return (String) example; }
        });

        Feature feature = learner.featureValue("88");

        assertTrue(feature.isDiscrete());
        assertTrue(feature.isPrimitive());
        assertEquals("CD", feature.getStringValue());
        assertEquals("", feature.getStringIdentifier());
        assertEquals("edu.demo", feature.getPackage());
        assertEquals("Baseline", feature.getGeneratingClassifier());
    }

    @Test
    public void classifyWrapsExactlyOnePredictionFeature() {
        POSBaselineLearner learner = new POSBaselineLearner("Wrapper");
        learner.setExtractor(new Classifier("forms") {
            public FeatureVector classify(Object example) { return new FeatureVector(); }
            public String discreteValue(Object example) { return (String) example; }
        });

        FeatureVector vector = learner.classify("ordinary");

        assertEquals(1, vector.size());
        assertEquals(1, vector.featuresSize());
        assertEquals(0, vector.labelsSize());
        assertEquals("UNKNOWN", vector.firstFeature().getStringValue());
        assertSame(vector.firstFeature(), vector.getFeature(0));
    }

    @Test
    public void primitiveArrayMethodsAreIntentionalNoOps() {
        POSBaselineLearner learner = new POSBaselineLearner();

        learner.learn(new int[] {1, 2}, new double[] {0.5, 1.0}, new int[] {3},
                new double[] {1.0});
        FeatureVector result = learner.classify(new int[] {1, 2}, new double[] {0.5, 1.0});

        assertEquals(0, result.size());
        assertNull(learner.scores(new int[] {1}, new double[] {1.0}));
        assertEquals(0, learner.observedCount("anything"));
    }

    @Test
    public void instanceTextWriteSortsWordsAndRanksTagsByCount() throws Exception {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.setExtractor(new Classifier("forms") {
            public FeatureVector classify(Object example) { return new FeatureVector(); }
            public String discreteValue(Object example) { return ((String[]) example)[0]; }
        });
        learner.setLabeler(new Classifier("labels") {
            public FeatureVector classify(Object example) { return new FeatureVector(); }
            public String discreteValue(Object example) { return ((String[]) example)[1]; }
        });
        learner.learn((Object) new String[] {"zebra", "NN"});
        learner.learn((Object) new String[] {"apple", "VB"});
        learner.learn((Object) new String[] {"apple", "NN"});
        learner.learn((Object) new String[] {"apple", "NN"});
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        learner.write(new PrintStream(bytes, true, StandardCharsets.UTF_8.name()));

        assertEquals("apple: NN(2) VB(1)\nzebra: NN(1)\n",
                bytes.toString(StandardCharsets.UTF_8.name()).replace("\r\n", "\n"));
    }

    @Test
    public void staticTextWriteUsesStableTagOrderWhenCountsTie() throws Exception {
        HashMap<String, TreeMap<String, Integer>> table = new HashMap<>();
        TreeMap<String, Integer> counts = new TreeMap<>();
        counts.put("VB", 2);
        counts.put("JJ", 2);
        counts.put("NN", 4);
        table.put("same", counts);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        POSBaselineLearner.write(new PrintStream(bytes, true, StandardCharsets.UTF_8.name()), table);

        assertEquals("same: NN(4) JJ(2) VB(2)\n",
                bytes.toString(StandardCharsets.UTF_8.name()).replace("\r\n", "\n"));
    }

    @Test
    public void binaryRoundTripPreservesLearnedCountsAndPredictions() {
        POSBaselineLearner learner = new POSBaselineLearner("edu.demo.SerializedTagger");
        Classifier extractor = new Classifier("forms") {
            public FeatureVector classify(Object example) { return new FeatureVector(); }
            public String discreteValue(Object example) { return ((String[]) example)[0]; }
        };
        Classifier labeler = new Classifier("labels") {
            public FeatureVector classify(Object example) { return new FeatureVector(); }
            public String discreteValue(Object example) { return ((String[]) example)[1]; }
        };
        learner.setExtractor(extractor);
        learner.setLabeler(labeler);
        learner.learn((Object) new String[] {"run", "NN"});
        learner.learn((Object) new String[] {"run", "VB"});
        learner.learn((Object) new String[] {"run", "VB"});
        learner.learn((Object) new String[] {"blue", "JJ"});
        learner.setExtractor(null);
        learner.setLabeler(null);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ExceptionlessOutputStream output = new ExceptionlessOutputStream(bytes);
        learner.write(output);

        ExceptionlessInputStream input = new ExceptionlessInputStream(
                new ByteArrayInputStream(bytes.toByteArray()));
        POSBaselineLearner restored = (POSBaselineLearner) Learner.readLearner(input);
        restored.setExtractor(extractor);
        restored.setLabeler(labeler);

        assertEquals(3, restored.observedCount("run"));
        assertEquals(1, restored.observedCount("blue"));
        assertEquals("VB", restored.discreteValue(new String[] {"run", "unused"}));
        assertEquals("JJ", restored.discreteValue(new String[] {"blue", "unused"}));
        assertEquals(new HashSet<>(Arrays.asList("NN", "VB")), restored.allowableTags("run"));
        assertEquals("edu.demo", restored.featureValue(new String[] {"run", "unused"}).getPackage());
        assertEquals("SerializedTagger",
                restored.featureValue(new String[] {"run", "unused"}).getGeneratingClassifier());
    }

    @Test
    public void readingModelOverwritesExistingLearnedTable() {
        POSBaselineLearner saved = new POSBaselineLearner();
        saved.setExtractor(null);
        saved.setLabeler(null);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ExceptionlessOutputStream output = new ExceptionlessOutputStream(bytes);
        saved.write(output);

        POSBaselineLearner target = new POSBaselineLearner();
        target.setExtractor(new Classifier("forms") {
            public FeatureVector classify(Object example) { return new FeatureVector(); }
            public String discreteValue(Object example) { return ((String[]) example)[0]; }
        });
        target.setLabeler(new Classifier("labels") {
            public FeatureVector classify(Object example) { return new FeatureVector(); }
            public String discreteValue(Object example) { return ((String[]) example)[1]; }
        });
        target.learn((Object) new String[] {"stale", "NN"});
        ExceptionlessInputStream input = new ExceptionlessInputStream(
                new ByteArrayInputStream(bytes.toByteArray()));
        input.readString();

        target.read(input);

        assertFalse(target.observed("stale"));
        assertEquals(0, target.observedCount("stale"));
    }

    @Test(expected = NullPointerException.class)
    public void learningWithoutExtractorFailsFast() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.learn("word");
    }

    @Test(expected = NullPointerException.class)
    public void learningWithoutLabelerFailsFastAfterExtraction() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.setExtractor(new Classifier("forms") {
            public FeatureVector classify(Object example) { return new FeatureVector(); }
            public String discreteValue(Object example) { return (String) example; }
        });
        learner.learn("word");
    }
}
