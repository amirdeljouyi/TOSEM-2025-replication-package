package opennlp.tools.sentdetect;

import java.lang.reflect.Proxy;
import java.util.Collections;

import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.ml.model.MaxentModel;
import opennlp.tools.util.Span;
import opennlp.tools.util.StringList;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

public class SentenceDetectorMEPromptTest {

  @Test
  public void emptyInputProducesNoSentencesAndNoProbabilities() {
    MaxentModel maxent = (MaxentModel) Proxy.newProxyInstance(getClass().getClassLoader(),
        new Class<?>[] {MaxentModel.class}, (proxy, method, args) -> {
          if (method.getName().equals("getNumOutcomes")) return 2;
          if (method.getName().equals("getOutcome")) return ((Integer) args[0]) == 0 ? "s" : "n";
          if (method.getName().equals("getIndex")) return "s".equals(args[0]) ? 0 : 1;
          if (method.getName().equals("eval")) return new double[] {.8, .2};
          if (method.getName().equals("getBestOutcome")) return "s";
          return "";
        });
    SentenceDetectorFactory factory = new SentenceDetectorFactory("eng", false, null,
        new char[] {'.', '!', '?'});
    SentenceDetectorME detector = new SentenceDetectorME(
        new SentenceModel("eng", maxent, Collections.emptyMap(), factory));

    assertArrayEquals(new String[0], detector.sentDetect(""));
    assertArrayEquals(new double[0], detector.getSentenceProbabilities(), 0d);
    assertEquals(0, detector.sentPosDetect("").length);
  }

  @Test
  public void whitespaceOnlyInputProducesNoSpan() {
    MaxentModel maxent = (MaxentModel) Proxy.newProxyInstance(getClass().getClassLoader(),
        new Class<?>[] {MaxentModel.class}, (proxy, method, args) -> {
          if (method.getName().equals("getNumOutcomes")) return 2;
          if (method.getName().equals("getOutcome")) return ((Integer) args[0]) == 0 ? "s" : "n";
          if (method.getName().equals("getIndex")) return "s".equals(args[0]) ? 0 : 1;
          if (method.getName().equals("eval")) return new double[] {.8, .2};
          if (method.getName().equals("getBestOutcome")) return "s";
          return "";
        });
    SentenceDetectorME detector = new SentenceDetectorME(new SentenceModel("eng", maxent,
        Collections.emptyMap(), new SentenceDetectorFactory("eng", false, null, new char[] {'.'})));

    assertArrayEquals(new String[0], detector.sentDetect(" \t\n\r "));
    assertArrayEquals(new double[0], detector.getSentenceProbabilities(), 0d);
  }

  @Test
  public void textWithoutEndMarkerIsTrimmedAndAssignedCertainProbability() {
    MaxentModel maxent = (MaxentModel) Proxy.newProxyInstance(getClass().getClassLoader(),
        new Class<?>[] {MaxentModel.class}, (proxy, method, args) -> {
          if (method.getName().equals("getNumOutcomes")) return 2;
          if (method.getName().equals("getOutcome")) return ((Integer) args[0]) == 0 ? "s" : "n";
          if (method.getName().equals("getIndex")) return "s".equals(args[0]) ? 0 : 1;
          if (method.getName().equals("eval")) return new double[] {.8, .2};
          if (method.getName().equals("getBestOutcome")) return "s";
          return "";
        });
    SentenceDetectorME detector = new SentenceDetectorME(new SentenceModel("eng", maxent,
        Collections.emptyMap(), new SentenceDetectorFactory("eng", false, null, new char[] {'.'})));

    Span[] spans = detector.sentPosDetect("  only one sentence  ");

    assertEquals(1, spans.length);
    assertEquals(new Span(2, 19), spans[0]);
    assertEquals("only one sentence", spans[0].getCoveredText("  only one sentence  ").toString());
    assertArrayEquals(new double[] {1d}, detector.getSentenceProbabilities(), 0d);
  }

  @Test
  public void splitsSeveralSentenceTypesAndTrimsInterSentenceWhitespace() {
    MaxentModel maxent = (MaxentModel) Proxy.newProxyInstance(getClass().getClassLoader(),
        new Class<?>[] {MaxentModel.class}, (proxy, method, args) -> {
          if (method.getName().equals("getNumOutcomes")) return 2;
          if (method.getName().equals("getOutcome")) return ((Integer) args[0]) == 0 ? "s" : "n";
          if (method.getName().equals("getIndex")) return "s".equals(args[0]) ? 0 : 1;
          if (method.getName().equals("eval")) return new double[] {.82, .18};
          if (method.getName().equals("getBestOutcome")) return "s";
          return "";
        });
    SentenceDetectorME detector = new SentenceDetectorME(new SentenceModel("eng", maxent,
        Collections.emptyMap(), new SentenceDetectorFactory("eng", false, null,
            new char[] {'.', '!', '?'})));

    String[] sentences = detector.sentDetect(" First.  Second!\nThird?  ");

    assertArrayEquals(new String[] {"First.", "Second!", "Third?"}, sentences);
    assertArrayEquals(new double[] {.82, .82, .82}, detector.getSentenceProbabilities(), 0d);
  }

  @Test
  public void nonSplitDecisionLeavesWholeTextAsOneSentence() {
    MaxentModel maxent = (MaxentModel) Proxy.newProxyInstance(getClass().getClassLoader(),
        new Class<?>[] {MaxentModel.class}, (proxy, method, args) -> {
          if (method.getName().equals("getNumOutcomes")) return 2;
          if (method.getName().equals("getOutcome")) return ((Integer) args[0]) == 0 ? "s" : "n";
          if (method.getName().equals("getIndex")) return "s".equals(args[0]) ? 0 : 1;
          if (method.getName().equals("eval")) return new double[] {.04, .96};
          if (method.getName().equals("getBestOutcome")) return "n";
          return "";
        });
    SentenceDetectorME detector = new SentenceDetectorME(new SentenceModel("eng", maxent,
        Collections.emptyMap(), new SentenceDetectorFactory("eng", false, null,
            new char[] {'.', '!'})));

    assertArrayEquals(new String[] {"Alpha. Beta!"}, detector.sentDetect(" Alpha. Beta! "));
    assertArrayEquals(new double[] {1d}, detector.getSentenceProbabilities(), 0d);
  }

  @Test
  public void abbreviationDictionaryRejectsOtherwisePositiveBoundary() {
    Dictionary abbreviations = new Dictionary(true);
    abbreviations.put(new StringList("Dr."));
    MaxentModel maxent = (MaxentModel) Proxy.newProxyInstance(getClass().getClassLoader(),
        new Class<?>[] {MaxentModel.class}, (proxy, method, args) -> {
          if (method.getName().equals("getNumOutcomes")) return 2;
          if (method.getName().equals("getOutcome")) return ((Integer) args[0]) == 0 ? "s" : "n";
          if (method.getName().equals("getIndex")) return "s".equals(args[0]) ? 0 : 1;
          if (method.getName().equals("eval")) return new double[] {.75, .25};
          if (method.getName().equals("getBestOutcome")) return "s";
          return "";
        });
    SentenceDetectorFactory factory = new SentenceDetectorFactory("eng", false, abbreviations,
        new char[] {'.'});
    SentenceDetectorME detector = new SentenceDetectorME(
        new SentenceModel("eng", maxent, Collections.emptyMap(), factory));

    assertArrayEquals(new String[] {"Dr. Smith arrived.", "He waved."},
        detector.sentDetect("Dr. Smith arrived. He waved."));
    assertArrayEquals(new double[] {.75, .75}, detector.getSentenceProbabilities(), 0d);
  }

  @Test
  public void adjacentEndMarkersAreTreatedAsOneBoundary() {
    MaxentModel maxent = (MaxentModel) Proxy.newProxyInstance(getClass().getClassLoader(),
        new Class<?>[] {MaxentModel.class}, (proxy, method, args) -> {
          if (method.getName().equals("getNumOutcomes")) return 2;
          if (method.getName().equals("getOutcome")) return ((Integer) args[0]) == 0 ? "s" : "n";
          if (method.getName().equals("getIndex")) return "s".equals(args[0]) ? 0 : 1;
          if (method.getName().equals("eval")) return new double[] {.67, .33};
          if (method.getName().equals("getBestOutcome")) return "s";
          return "";
        });
    SentenceDetectorME detector = new SentenceDetectorME(new SentenceModel("eng", maxent,
        Collections.emptyMap(), new SentenceDetectorFactory("eng", false, null,
            new char[] {'.', '!', '?'})));

    assertArrayEquals(new String[] {"Really?!", "Yes...", "Fine."},
        detector.sentDetect("Really?! Yes... Fine."));
    assertArrayEquals(new double[] {.67, .67, .67}, detector.getSentenceProbabilities(), 0d);
  }

  @Test
  public void tokenEndModeKeepsClosingDelimiterWithPrecedingSentence() {
    MaxentModel maxent = (MaxentModel) Proxy.newProxyInstance(getClass().getClassLoader(),
        new Class<?>[] {MaxentModel.class}, (proxy, method, args) -> {
          if (method.getName().equals("getNumOutcomes")) return 2;
          if (method.getName().equals("getOutcome")) return ((Integer) args[0]) == 0 ? "s" : "n";
          if (method.getName().equals("getIndex")) return "s".equals(args[0]) ? 0 : 1;
          if (method.getName().equals("eval")) return new double[] {.7, .3};
          if (method.getName().equals("getBestOutcome")) return "s";
          return "";
        });
    SentenceModel tokenModel = new SentenceModel("eng", maxent, Collections.emptyMap(),
        new SentenceDetectorFactory("eng", true, null, new char[] {'.'}));
    SentenceModel characterModel = new SentenceModel("eng", maxent, Collections.emptyMap(),
        new SentenceDetectorFactory("eng", false, null, new char[] {'.'}));
    SentenceDetectorME tokenDetector = new SentenceDetectorME(tokenModel);
    SentenceDetectorME characterDetector = new SentenceDetectorME(characterModel);

    assertArrayEquals(new String[] {"One.\"", "Two."}, tokenDetector.sentDetect("One.\"  Two."));
    assertArrayEquals(new String[] {"One.", "\"  Two."},
        characterDetector.sentDetect("One.\"  Two."));
  }

  @Test
  public void supportsStringBuilderAndReportsExactSpans() {
    MaxentModel maxent = (MaxentModel) Proxy.newProxyInstance(getClass().getClassLoader(),
        new Class<?>[] {MaxentModel.class}, (proxy, method, args) -> {
          if (method.getName().equals("getNumOutcomes")) return 2;
          if (method.getName().equals("getOutcome")) return ((Integer) args[0]) == 0 ? "s" : "n";
          if (method.getName().equals("getIndex")) return "s".equals(args[0]) ? 0 : 1;
          if (method.getName().equals("eval")) return new double[] {.61, .39};
          if (method.getName().equals("getBestOutcome")) return "s";
          return "";
        });
    SentenceDetectorME detector = new SentenceDetectorME(new SentenceModel("eng", maxent,
        Collections.emptyMap(), new SentenceDetectorFactory("eng", false, null, new char[] {'!'})));
    StringBuilder input = new StringBuilder("  Go!   Stay");

    Span[] spans = detector.sentPosDetect(input);

    assertEquals(2, spans.length);
    assertEquals(new Span(2, 5), spans[0]);
    assertEquals(new Span(8, 12), spans[1]);
    assertEquals(.61, spans[0].getProb(), 0d);
    assertEquals(1d, spans[1].getProb(), 0d);
  }

  @Test
  public void laterCallReplacesEarlierProbabilityHistory() {
    MaxentModel maxent = (MaxentModel) Proxy.newProxyInstance(getClass().getClassLoader(),
        new Class<?>[] {MaxentModel.class}, (proxy, method, args) -> {
          if (method.getName().equals("getNumOutcomes")) return 2;
          if (method.getName().equals("getOutcome")) return ((Integer) args[0]) == 0 ? "s" : "n";
          if (method.getName().equals("getIndex")) return "s".equals(args[0]) ? 0 : 1;
          if (method.getName().equals("eval")) return new double[] {.73, .27};
          if (method.getName().equals("getBestOutcome")) return "s";
          return "";
        });
    SentenceDetectorME detector = new SentenceDetectorME(new SentenceModel("eng", maxent,
        Collections.emptyMap(), new SentenceDetectorFactory("eng", false, null, new char[] {'.'})));

    detector.sentDetect("A. B.");
    assertArrayEquals(new double[] {.73, .73}, detector.getSentenceProbabilities(), 0d);
    detector.sentPosDetect("unfinished");
    assertArrayEquals(new double[] {1d}, detector.getSentenceProbabilities(), 0d);
    detector.sentDetect("   ");
    assertArrayEquals(new double[0], detector.getSentenceProbabilities(), 0d);
  }

  @Test
  public void returnedProbabilityArraysAreIndependentSnapshots() {
    MaxentModel maxent = (MaxentModel) Proxy.newProxyInstance(getClass().getClassLoader(),
        new Class<?>[] {MaxentModel.class}, (proxy, method, args) -> {
          if (method.getName().equals("getNumOutcomes")) return 2;
          if (method.getName().equals("getOutcome")) return ((Integer) args[0]) == 0 ? "s" : "n";
          if (method.getName().equals("getIndex")) return "s".equals(args[0]) ? 0 : 1;
          if (method.getName().equals("eval")) return new double[] {.88, .12};
          if (method.getName().equals("getBestOutcome")) return "s";
          return "";
        });
    SentenceDetectorME detector = new SentenceDetectorME(new SentenceModel("eng", maxent,
        Collections.emptyMap(), new SentenceDetectorFactory("eng", false, null, new char[] {'.'})));
    detector.sentDetect("Done.");

    double[] first = detector.getSentenceProbabilities();
    double[] second = detector.getSentenceProbabilities();
    first[0] = 0d;

    assertNotSame(first, second);
    assertArrayEquals(new double[] {.88}, second, 0d);
    assertArrayEquals(new double[] {.88}, detector.getSentenceProbabilities(), 0d);
  }

  @Test(expected = NullPointerException.class)
  public void nullInputIsRejected() {
    MaxentModel maxent = (MaxentModel) Proxy.newProxyInstance(getClass().getClassLoader(),
        new Class<?>[] {MaxentModel.class}, (proxy, method, args) -> {
          if (method.getName().equals("getNumOutcomes")) return 2;
          if (method.getName().equals("getOutcome")) return ((Integer) args[0]) == 0 ? "s" : "n";
          if (method.getName().equals("getIndex")) return "s".equals(args[0]) ? 0 : 1;
          if (method.getName().equals("eval")) return new double[] {.8, .2};
          if (method.getName().equals("getBestOutcome")) return "s";
          return "";
        });
    SentenceDetectorME detector = new SentenceDetectorME(new SentenceModel("eng", maxent,
        Collections.emptyMap(), new SentenceDetectorFactory("eng", false, null, new char[] {'.'})));

    detector.sentDetect(null);
  }
}
