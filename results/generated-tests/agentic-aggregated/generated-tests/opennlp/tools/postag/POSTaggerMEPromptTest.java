package opennlp.tools.postag;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.junit.Test;

import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.util.CollectionObjectStream;
import opennlp.tools.util.Sequence;
import opennlp.tools.util.StringList;
import opennlp.tools.util.TrainingParameters;

public class POSTaggerMEPromptTest {

  @Test
  public void trainTagAndProbabilityMethodsWorkTogether() throws Exception {
    POSSample first = new POSSample(new String[] {"dogs", "run"}, new String[] {"NNS", "VBP"});
    POSSample second = new POSSample(new String[] {"cats", "sleep"}, new String[] {"NNS", "VBP"});
    POSSample third = new POSSample(new String[] {"birds", "fly"}, new String[] {"NNS", "VBP"});
    CollectionObjectStream<POSSample> samples = new CollectionObjectStream<>(
        Arrays.asList(first, second, third));
    TrainingParameters parameters = TrainingParameters.defaultParams();
    parameters.put("Cutoff", 1);
    parameters.put("Iterations", 30);
    POSModel model = POSTaggerME.train("eng", samples, parameters, new POSTaggerFactory());
    POSTaggerME tagger = new POSTaggerME(model);

    String[] tags = tagger.tag(new String[] {"dogs", "run"});
    double[] returnedProbabilities = tagger.probs();
    double[] copiedProbabilities = new double[2];
    tagger.probs(copiedProbabilities);

    assertArrayEquals(new String[] {"NNS", "VBP"}, tags);
    assertArrayEquals(returnedProbabilities, copiedProbabilities, 0.0);
    assertEquals(2, returnedProbabilities.length);
    assertTrue(returnedProbabilities[0] > 0.0);
    assertTrue(returnedProbabilities[0] <= 1.0);
    assertTrue(returnedProbabilities[1] > 0.0);
    assertTrue(returnedProbabilities[1] <= 1.0);
  }

  @Test
  public void defaultTaggerReturnsTheModelTags() throws Exception {
    POSSample first = new POSSample(new String[] {"the", "dog", "runs"},
        new String[] {"DT", "NN", "VBZ"});
    POSSample second = new POSSample(new String[] {"a", "cat", "sleeps"},
        new String[] {"DT", "NN", "VBZ"});
    CollectionObjectStream<POSSample> samples = new CollectionObjectStream<>(
        Arrays.asList(first, second));
    TrainingParameters parameters = TrainingParameters.defaultParams();
    parameters.put("Cutoff", 1);
    parameters.put("Iterations", 40);
    POSModel model = POSTaggerME.train("eng", samples, parameters, new POSTaggerFactory());
    POSTaggerME tagger = new POSTaggerME(model);

    assertArrayEquals(new String[] {"DT", "NN", "VBZ"},
        tagger.tag(new String[] {"the", "dog", "runs"}));
  }

  @Test
  public void tagPreservesFineGrainedPennOutcomes() throws Exception {
    POSSample first = new POSSample(new String[] {"bright", "stars"}, new String[] {"JJ", "NNS"});
    POSSample second = new POSSample(new String[] {"small", "boats"}, new String[] {"JJ", "NNS"});
    CollectionObjectStream<POSSample> samples = new CollectionObjectStream<>(
        Arrays.asList(first, second));
    TrainingParameters parameters = TrainingParameters.defaultParams();
    parameters.put("Cutoff", 1);
    parameters.put("Iterations", 35);
    POSModel model = POSTaggerME.train("eng", samples, parameters, new POSTaggerFactory());
    POSTaggerME tagger = new POSTaggerME(model);

    assertArrayEquals(new String[] {"JJ", "NNS"}, tagger.tag(new String[] {"bright", "stars"}));
  }

  @Test
  public void tagMultipleAndTopKSequencesReturnRankedAlternatives() throws Exception {
    POSSample noun = new POSSample(new String[] {"record"}, new String[] {"NN"});
    POSSample verb = new POSSample(new String[] {"record"}, new String[] {"VB"});
    POSSample nounAgain = new POSSample(new String[] {"album"}, new String[] {"NN"});
    CollectionObjectStream<POSSample> samples = new CollectionObjectStream<>(
        Arrays.asList(noun, verb, nounAgain));
    TrainingParameters parameters = TrainingParameters.defaultParams();
    parameters.put("Cutoff", 1);
    parameters.put("Iterations", 20);
    parameters.put("BeamSize", 2);
    POSModel model = POSTaggerME.train("eng", samples, parameters, new POSTaggerFactory());
    POSTaggerME tagger = new POSTaggerME(model);

    String[][] taggings = tagger.tag(2, new String[] {"record"});
    Sequence[] sequences = tagger.topKSequences(new String[] {"record"});
    Sequence[] contextualSequences = tagger.topKSequences(
        new String[] {"record"}, new Object[] {new String[] {"metadata"}});

    assertEquals(2, taggings.length);
    assertEquals(1, taggings[0].length);
    assertEquals(1, taggings[1].length);
    assertEquals(2, sequences.length);
    assertEquals(2, contextualSequences.length);
    assertArrayEquals(taggings[0], sequences[0].getOutcomes().toArray(new String[0]));
    assertTrue(sequences[0].getScore() >= sequences[1].getScore());
  }

  @Test
  public void getAllPosTagsExposesEveryLearnedOutcome() throws Exception {
    POSSample sample = new POSSample(new String[] {"quickly", "paint", "walls"},
        new String[] {"RB", "VB", "NNS"});
    CollectionObjectStream<POSSample> samples = new CollectionObjectStream<>(Arrays.asList(sample));
    TrainingParameters parameters = TrainingParameters.defaultParams();
    parameters.put("Cutoff", 1);
    parameters.put("Iterations", 15);
    POSModel model = POSTaggerME.train("eng", samples, parameters, new POSTaggerFactory());
    POSTaggerME tagger = new POSTaggerME(model);

    String[] outcomes = tagger.getAllPosTags();
    Arrays.sort(outcomes);

    assertArrayEquals(new String[] {"NNS", "RB", "VB"}, outcomes);
  }

  @Test
  public void getOrderedTagsReturnsDescendingProbabilitiesAndLabels() throws Exception {
    POSSample noun = new POSSample(new String[] {"work"}, new String[] {"NN"});
    POSSample nounAgain = new POSSample(new String[] {"job"}, new String[] {"NN"});
    POSSample verb = new POSSample(new String[] {"work"}, new String[] {"VB"});
    CollectionObjectStream<POSSample> samples = new CollectionObjectStream<>(
        Arrays.asList(noun, nounAgain, verb));
    TrainingParameters parameters = TrainingParameters.defaultParams();
    parameters.put("Cutoff", 1);
    parameters.put("Iterations", 25);
    POSModel model = POSTaggerME.train("eng", samples, parameters, new POSTaggerFactory());
    POSTaggerME tagger = new POSTaggerME(model);
    double[] probabilities = new double[2];

    String[] ordered = tagger.getOrderedTags(Arrays.asList("work"), Arrays.asList(), 0,
        probabilities);
    String[] orderedWithoutProbabilities = tagger.getOrderedTags(
        Arrays.asList("work"), Arrays.asList(), 0);

    assertEquals(2, ordered.length);
    assertArrayEquals(ordered, orderedWithoutProbabilities);
    assertTrue(Arrays.asList("NN", "VB").contains(ordered[0]));
    assertTrue(Arrays.asList("NN", "VB").contains(ordered[1]));
    assertFalse(ordered[0].equals(ordered[1]));
    assertTrue(probabilities[0] >= probabilities[1]);
    assertEquals(1.0, probabilities[0] + probabilities[1], 0.000001);
  }

  @Test
  public void probabilityMethodsRequireAPreviousTagging() throws Exception {
    POSSample noun = new POSSample(new String[] {"word"}, new String[] {"NN"});
    POSSample verb = new POSSample(new String[] {"act"}, new String[] {"VB"});
    CollectionObjectStream<POSSample> samples = new CollectionObjectStream<>(
        Arrays.asList(noun, verb));
    TrainingParameters parameters = TrainingParameters.defaultParams();
    parameters.put("Cutoff", 1);
    parameters.put("Iterations", 10);
    POSModel model = POSTaggerME.train("eng", samples, parameters, new POSTaggerFactory());
    POSTaggerME tagger = new POSTaggerME(model);

    try {
      tagger.probs();
      fail("Expected probabilities to be unavailable before tag() is called");
    } catch (NullPointerException expected) {
      assertNotNull(expected);
    }

    try {
      tagger.probs(new double[1]);
      fail("Expected copied probabilities to be unavailable before tag() is called");
    } catch (NullPointerException expected) {
      assertNotNull(expected);
    }
  }

  @Test
  public void emptySentenceProducesEmptyTagsAndProbabilities() throws Exception {
    POSSample noun = new POSSample(new String[] {"word"}, new String[] {"NN"});
    POSSample verb = new POSSample(new String[] {"act"}, new String[] {"VB"});
    CollectionObjectStream<POSSample> samples = new CollectionObjectStream<>(
        Arrays.asList(noun, verb));
    TrainingParameters parameters = TrainingParameters.defaultParams();
    parameters.put("Cutoff", 1);
    parameters.put("Iterations", 10);
    POSModel model = POSTaggerME.train("eng", samples, parameters, new POSTaggerFactory());
    POSTaggerME tagger = new POSTaggerME(model);

    assertArrayEquals(new String[0], tagger.tag(new String[0]));
    assertArrayEquals(new double[0], tagger.probs(), 0.0);
    Sequence[] emptySequences = tagger.topKSequences(new String[0]);
    assertEquals(1, emptySequences.length);
    assertEquals(0, emptySequences[0].getSize());
  }

  @Test
  public void buildNGramDictionaryHonorsFrequencyCutoffAndSentenceBoundaries() throws Exception {
    POSSample first = new POSSample(new String[] {"New", "York"}, new String[] {"NNP", "NNP"});
    POSSample second = new POSSample(new String[] {"New", "ideas"}, new String[] {"JJ", "NNS"});
    POSSample third = new POSSample(new String[] {"New"}, new String[] {"NNP"});
    CollectionObjectStream<POSSample> samples = new CollectionObjectStream<>(
        Arrays.asList(first, second, third));

    Dictionary dictionary = POSTaggerME.buildNGramDictionary(samples, 2);

    assertEquals(1, dictionary.size());
    assertTrue(dictionary.contains(new StringList("New")));
    assertFalse(dictionary.contains(new StringList("new")));
    assertFalse(dictionary.contains(new StringList("York")));
    assertFalse(dictionary.contains(new StringList("New", "York")));
  }

  @Test
  public void buildNGramDictionaryHandlesEmptyInputAndZeroCutoff() throws Exception {
    POSSample empty = new POSSample(new String[0], new String[0]);
    POSSample one = new POSSample(new String[] {"Only"}, new String[] {"RB"});
    CollectionObjectStream<POSSample> samples = new CollectionObjectStream<>(
        Arrays.asList(empty, one));

    Dictionary dictionary = POSTaggerME.buildNGramDictionary(samples, 0);

    assertEquals(1, dictionary.size());
    assertTrue(dictionary.contains(new StringList("Only")));
    assertFalse(dictionary.contains(new StringList("ONLY")));
    assertEquals(1, dictionary.getMinTokenCount());
    assertEquals(1, dictionary.getMaxTokenCount());
  }

  @Test
  public void populatePOSDictionaryMergesExistingAndObservedTagsAtCutoff() throws Exception {
    POSDictionary dictionary = new POSDictionary(true);
    dictionary.put("lead", "JJ");
    POSSample first = new POSSample(new String[] {"lead", "pipe"}, new String[] {"NN", "NN"});
    POSSample second = new POSSample(new String[] {"lead"}, new String[] {"NN"});
    CollectionObjectStream<POSSample> samples = new CollectionObjectStream<>(
        Arrays.asList(first, second));

    POSTaggerME.populatePOSDictionary(samples, dictionary, 2);

    String[] leadTags = dictionary.getTags("lead");
    Arrays.sort(leadTags);
    assertArrayEquals(new String[] {"JJ", "NN"}, leadTags);
    assertNull(dictionary.getTags("pipe"));
  }

  @Test
  public void populatePOSDictionaryNormalizesCaseAndIgnoresTokensContainingDigits() throws Exception {
    POSDictionary dictionary = new POSDictionary(false);
    POSSample first = new POSSample(new String[] {"Apple", "R2D2"}, new String[] {"NNP", "NNP"});
    POSSample second = new POSSample(new String[] {"APPLE", "abc123"}, new String[] {"NN", "NN"});
    CollectionObjectStream<POSSample> samples = new CollectionObjectStream<>(
        Arrays.asList(first, second));

    POSTaggerME.populatePOSDictionary(samples, dictionary, 1);

    String[] tags = dictionary.getTags("aPpLe");
    Arrays.sort(tags);
    assertArrayEquals(new String[] {"NN", "NNP"}, tags);
    assertNull(dictionary.getTags("R2D2"));
    assertNull(dictionary.getTags("abc123"));
  }

  @Test
  public void populatePOSDictionaryAccumulatesEachWordTagPairIndependently() throws Exception {
    POSDictionary dictionary = new POSDictionary();
    POSSample first = new POSSample(new String[] {"close", "close"}, new String[] {"JJ", "VB"});
    POSSample second = new POSSample(new String[] {"close", "rare"}, new String[] {"JJ", "NN"});
    POSSample third = new POSSample(new String[] {"close"}, new String[] {"VB"});
    CollectionObjectStream<POSSample> samples = new CollectionObjectStream<>(
        Arrays.asList(first, second, third));

    POSTaggerME.populatePOSDictionary(samples, dictionary, 2);

    String[] tags = dictionary.getTags("close");
    Arrays.sort(tags);
    assertArrayEquals(new String[] {"JJ", "VB"}, tags);
    assertNull(dictionary.getTags("rare"));
  }
}
