package edu.stanford.nlp.ie;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.sequences.Clique;
import edu.stanford.nlp.sequences.SeqClassifierFlags;
import edu.stanford.nlp.util.PaddedList;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NERFeatureFactoryPromptTest {

  @Test
  public void defaultConfigurationProducesCurrentWordFeature() {
    Properties properties = new Properties();
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(new SeqClassifierFlags(properties, false));
    CoreLabel token = CoreLabel.wordFromString("Stanford");
    CoreLabel pad = CoreLabel.wordFromString("<PAD>");
    PaddedList<CoreLabel> sentence = new PaddedList<>(Collections.singletonList(token), pad);

    Collection<String> features = factory.getCliqueFeatures(sentence, 0, Clique.valueOf(new int[]{0}));

    assertEquals(Collections.singleton("Stanford-WORD|C"), features);
    assertEquals("No distsim lexicon", factory.describeDistsimLexicon());
  }

  @Test
  public void domainDuplicatesCurrentFeaturesWithDomainQualifiedSuffix() {
    Properties properties = new Properties();
    properties.setProperty("useClassFeature", "true");
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(new SeqClassifierFlags(properties, false));
    CoreLabel token = CoreLabel.wordFromString("Paris");
    token.set(CoreAnnotations.DomainAnnotation.class, "news");
    CoreLabel pad = CoreLabel.wordFromString("<PAD>");
    PaddedList<CoreLabel> sentence = new PaddedList<>(Collections.singletonList(token), pad);

    Collection<String> features = factory.getCliqueFeatures(sentence, 0, Clique.valueOf(new int[]{0}));

    assertEquals(4, features.size());
    assertTrue(features.contains("Paris-WORD|C"));
    assertTrue(features.contains("Paris-WORD|news-C"));
    assertTrue(features.contains("###|C"));
    assertTrue(features.contains("###|news-C"));
  }

  @Test
  public void contextualWordTagAndPairFeaturesUseBothNeighbors() {
    Properties properties = new Properties();
    properties.setProperty("usePrev", "true");
    properties.setProperty("useNext", "true");
    properties.setProperty("useTags", "true");
    properties.setProperty("useWordPairs", "true");
    properties.setProperty("useSymWordPairs", "true");
    properties.setProperty("useWordTag", "true");
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(new SeqClassifierFlags(properties, false));
    CoreLabel previous = CoreLabel.wordFromString("Dr.");
    previous.setTag("NNP");
    CoreLabel current = CoreLabel.wordFromString("Ada");
    current.setTag("NNP");
    CoreLabel next = CoreLabel.wordFromString("worked");
    next.setTag("VBD");
    CoreLabel pad = CoreLabel.wordFromString("<PAD>");
    pad.setTag("PAD");
    PaddedList<CoreLabel> sentence = new PaddedList<>(Arrays.asList(previous, current, next), pad);

    Collection<String> features = factory.getCliqueFeatures(sentence, 1, Clique.valueOf(new int[]{0}));

    assertTrue(features.contains("Ada-WORD|C"));
    assertTrue(features.contains("NNP-TAG|C"));
    assertTrue(features.contains("Dr.-PW|C"));
    assertTrue(features.contains("worked-NW|C"));
    assertTrue(features.contains("Ada-Dr.-W-PW|C"));
    assertTrue(features.contains("Ada-worked-W-NW|C"));
    assertTrue(features.contains("Dr.-worked-SWORDS|C"));
    assertTrue(features.contains("Ada-NNP-W-T|C"));
    assertTrue(features.contains("Ada-NNP-W-PT|C"));
    assertTrue(features.contains("Ada-VBD-W-NT|C"));
  }

  @Test
  public void shapePositionLengthTitleAndIntegerFeaturesCoverIndependentBranches() {
    Properties properties = new Properties();
    properties.setProperty("useShapeStrings", "true");
    properties.setProperty("usePosition", "true");
    properties.setProperty("useBeginSent", "true");
    properties.setProperty("binnedLengths", "2,5");
    properties.setProperty("useTitle2", "true");
    properties.setProperty("useIfInteger", "true");
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(new SeqClassifierFlags(properties, false));
    CoreLabel title = CoreLabel.wordFromString("Dr.");
    title.set(CoreAnnotations.ShapeAnnotation.class, "Xx.");
    title.set(CoreAnnotations.PositionAnnotation.class, "0");
    CoreLabel number = CoreLabel.wordFromString("-17");
    number.set(CoreAnnotations.ShapeAnnotation.class, "-dd");
    number.set(CoreAnnotations.PositionAnnotation.class, "1");
    CoreLabel pad = CoreLabel.wordFromString("<PAD>");
    pad.set(CoreAnnotations.ShapeAnnotation.class, "PAD");
    PaddedList<CoreLabel> sentence = new PaddedList<>(Arrays.asList(title, number), pad);

    Collection<String> titleFeatures = factory.getCliqueFeatures(sentence, 0, Clique.valueOf(new int[]{0}));
    Collection<String> numberFeatures = factory.getCliqueFeatures(sentence, 1, Clique.valueOf(new int[]{0}));

    assertTrue(titleFeatures.contains("IS_TITLE|C"));
    assertTrue(titleFeatures.contains("Len-2-5|C"));
    assertTrue(titleFeatures.contains("0-POSITION|C"));
    assertTrue(titleFeatures.contains("BEGIN-SENT|C"));
    assertTrue(titleFeatures.contains("Xx.-BEGIN-SENT|C"));
    assertTrue(titleFeatures.contains("Xx.-TYPE|C"));
    assertTrue(numberFeatures.contains("NEGATIVE_INTEGER|C"));
    assertTrue(numberFeatures.contains("END-SENT|C"));
    assertFalse(numberFeatures.contains("IS_TITLE|C"));
  }

  @Test
  public void slashHyphenBothModeCreatesFragmentAndWordFeatures() {
    Properties properties = new Properties();
    properties.setProperty("splitSlashHyphenWords", "both");
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(new SeqClassifierFlags(properties, false));
    CoreLabel token = CoreLabel.wordFromString("alpha/beta-gamma");
    CoreLabel pad = CoreLabel.wordFromString("<PAD>");
    PaddedList<CoreLabel> sentence = new PaddedList<>(Collections.singletonList(token), pad);

    Collection<String> features = factory.getCliqueFeatures(sentence, 0, Clique.valueOf(new int[]{0}));

    assertTrue(features.contains("alpha-WFRAG|C"));
    assertTrue(features.contains("alpha-WORD|C"));
    assertTrue(features.contains("beta-WFRAG|C"));
    assertTrue(features.contains("gamma-WORD|C"));
    assertTrue(features.contains("alpha/beta-gamma-WORD|C"));
  }

  @Test
  public void constrainedNGramsRespectCaseLengthAndNoMiddleSetting() {
    Properties properties = new Properties();
    properties.setProperty("useNGrams", "true");
    properties.setProperty("lowercaseNGrams", "true");
    properties.setProperty("noMidNGrams", "true");
    properties.setProperty("maxNGramLeng", "2");
    properties.setProperty("cacheNGrams", "true");
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(new SeqClassifierFlags(properties, false));
    CoreLabel token = CoreLabel.wordFromString("Ab");
    CoreLabel pad = CoreLabel.wordFromString("<PAD>");
    PaddedList<CoreLabel> sentence = new PaddedList<>(Collections.singletonList(token), pad);

    Collection<String> first = factory.getCliqueFeatures(sentence, 0, Clique.valueOf(new int[]{0}));
    Collection<String> second = factory.getCliqueFeatures(sentence, 0, Clique.valueOf(new int[]{0}));
    factory.clearMemory();
    Collection<String> afterClear = factory.getCliqueFeatures(sentence, 0, Clique.valueOf(new int[]{0}));

    assertTrue(first.contains("#<a#|C"));
    assertTrue(first.contains("#b>#|C"));
    assertFalse(first.contains("#ab#|C"));
    assertEquals(first, second);
    assertEquals(first, afterClear);
  }

  @Test
  public void previousCliqueCombinesPreviousAndNextSequenceFeatureFamilies() {
    Properties properties = new Properties();
    properties.setProperty("usePrev", "true");
    properties.setProperty("useNext", "true");
    properties.setProperty("usePrevSequences", "true");
    properties.setProperty("useNextSequences", "true");
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(new SeqClassifierFlags(properties, false));
    CoreLabel first = CoreLabel.wordFromString("Alice");
    CoreLabel second = CoreLabel.wordFromString("arrived");
    CoreLabel pad = CoreLabel.wordFromString("<PAD>");
    PaddedList<CoreLabel> sentence = new PaddedList<>(Arrays.asList(first, second), pad);

    Collection<String> features = factory.getCliqueFeatures(sentence, 1, Clique.valueOf(new int[]{-1, 0}));

    assertTrue(features.contains("PSEQ|CpC"));
    assertTrue(features.contains("arrived-PSEQW|CpC"));
    assertTrue(features.contains("NSEQ|CnC"));
    assertTrue(features.contains("Alice-NSEQW|CnC"));
  }

  @Test
  public void transitionEdgeOnlyReturnsOnlyTransitionFeature() {
    Properties properties = new Properties();
    properties.setProperty("transitionEdgeOnly", "true");
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(new SeqClassifierFlags(properties, false));
    CoreLabel first = CoreLabel.wordFromString("one");
    CoreLabel second = CoreLabel.wordFromString("two");
    CoreLabel pad = CoreLabel.wordFromString("<PAD>");
    PaddedList<CoreLabel> sentence = new PaddedList<>(Arrays.asList(first, second), pad);

    Collection<String> features = factory.getCliqueFeatures(sentence, 1, Clique.valueOf(new int[]{-1, 0}));

    assertEquals(Collections.singleton("PSEQ|CpC"), features);
  }

  @Test
  public void disabledEdgeProducesNoPreviousCliqueFeatures() {
    Properties properties = new Properties();
    properties.setProperty("noEdgeFeature", "true");
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(new SeqClassifierFlags(properties, false));
    CoreLabel token = CoreLabel.wordFromString("only");
    CoreLabel pad = CoreLabel.wordFromString("<PAD>");
    PaddedList<CoreLabel> sentence = new PaddedList<>(Collections.singletonList(token), pad);

    Collection<String> features = factory.getCliqueFeatures(sentence, 0, Clique.valueOf(new int[]{-1, 0}));

    assertTrue(features.isEmpty());
  }

  @Test
  public void neighborNGramsAreLimitedByMaximumLength() {
    Properties properties = new Properties();
    properties.setProperty("useNeighborNGrams", "true");
    properties.setProperty("maxNGramLeng", "2");
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(new SeqClassifierFlags(properties, false));
    CoreLabel previous = CoreLabel.wordFromString("abcd");
    CoreLabel current = CoreLabel.wordFromString("wxyz");
    CoreLabel pad = CoreLabel.wordFromString("<PAD>");
    PaddedList<CoreLabel> sentence = new PaddedList<>(Arrays.asList(previous, current), pad);

    Collection<String> features = factory.getCliqueFeatures(sentence, 1, Clique.valueOf(new int[]{-1, 0}));

    assertTrue(features.contains("a-PREVIOUS-PREFIX|CpC"));
    assertTrue(features.contains("ab-PREVIOUS-PREFIX|CpC"));
    assertTrue(features.contains("cd-PREVIOUS-SUFFIX|CpC"));
    assertTrue(features.contains("wx-CURRENT-PREFIX|CpC"));
    assertTrue(features.contains("yz-CURRENT-SUFFIX|CpC"));
    assertFalse(features.contains("abc-PREVIOUS-PREFIX|CpC"));
  }

  @Test
  public void parenthesisMatchingSupportsDistancesTwoThroughFive() {
    Properties properties = new Properties();
    properties.setProperty("useParenMatching", "true");
    properties.setProperty("maxLeft", "5");
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(new SeqClassifierFlags(properties, false));
    CoreLabel open = CoreLabel.wordFromString("(");
    CoreLabel a = CoreLabel.wordFromString("a");
    CoreLabel b = CoreLabel.wordFromString("b");
    CoreLabel c = CoreLabel.wordFromString("c");
    CoreLabel d = CoreLabel.wordFromString("d");
    CoreLabel close = CoreLabel.wordFromString(")");
    CoreLabel pad = CoreLabel.wordFromString("<PAD>");
    PaddedList<CoreLabel> distanceTwo = new PaddedList<>(Arrays.asList(open, a, close), pad);
    PaddedList<CoreLabel> distanceThree = new PaddedList<>(Arrays.asList(open, a, b, close), pad);
    PaddedList<CoreLabel> distanceFour = new PaddedList<>(Arrays.asList(open, a, b, c, close), pad);
    PaddedList<CoreLabel> distanceFive = new PaddedList<>(Arrays.asList(open, a, b, c, d, close), pad);

    Collection<String> two = factory.getCliqueFeatures(distanceTwo, 2, Clique.valueOf(new int[]{-2, 0}));
    Collection<String> three = factory.getCliqueFeatures(distanceThree, 3, Clique.valueOf(new int[]{-3, 0}));
    Collection<String> four = factory.getCliqueFeatures(distanceFour, 4, Clique.valueOf(new int[]{-4, 0}));
    Collection<String> five = factory.getCliqueFeatures(distanceFive, 5, Clique.valueOf(new int[]{-5, 0}));

    assertEquals(Collections.singleton("PAREN-MATCH|Cp2C"), two);
    assertEquals(Collections.singleton("PAREN-MATCH|Cp3C"), three);
    assertEquals(Collections.singleton("PAREN-MATCH|Cp4C"), four);
    assertEquals(Collections.singleton("PAREN-MATCH|Cp5C"), five);
  }

  @Test
  public void reverseParenthesisMatchingRecognizesClosingTokenBeforeOpeningToken() {
    Properties properties = new Properties();
    properties.setProperty("useParenMatching", "true");
    properties.setProperty("useReverse", "true");
    properties.setProperty("maxLeft", "3");
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(new SeqClassifierFlags(properties, false));
    CoreLabel close = CoreLabel.wordFromString("]");
    CoreLabel middleOne = CoreLabel.wordFromString("x");
    CoreLabel middleTwo = CoreLabel.wordFromString("y");
    CoreLabel open = CoreLabel.wordFromString("[");
    CoreLabel pad = CoreLabel.wordFromString("<PAD>");
    PaddedList<CoreLabel> sentence = new PaddedList<>(Arrays.asList(close, middleOne, middleTwo, open), pad);

    Collection<String> features = factory.getCliqueFeatures(sentence, 3, Clique.valueOf(new int[]{-3, 0}));

    assertEquals(Collections.singleton("PAREN-MATCH|Cp3C"), features);
  }

  @Test
  public void longAndBoundarySequencesAppearOnHigherOrderCliques() {
    Properties properties = new Properties();
    properties.setProperty("useLongSequences", "true");
    properties.setProperty("useBoundarySequences", "true");
    properties.setProperty("maxLeft", "4");
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(new SeqClassifierFlags(properties, false));
    CoreLabel earlier = CoreLabel.wordFromString("earlier");
    CoreLabel boundary = CoreLabel.wordFromString("*BOUNDARY*");
    CoreLabel current = CoreLabel.wordFromString("current");
    CoreLabel pad = CoreLabel.wordFromString("<PAD>");
    PaddedList<CoreLabel> sentence = new PaddedList<>(Arrays.asList(earlier, boundary, current), pad);

    Collection<String> orderTwo = factory.getCliqueFeatures(sentence, 2, Clique.valueOf(new int[]{-2, -1, 0}));
    Collection<String> orderThree = factory.getCliqueFeatures(sentence, 2, Clique.valueOf(new int[]{-3, -2, -1, 0}));
    Collection<String> orderFour = factory.getCliqueFeatures(sentence, 2, Clique.valueOf(new int[]{-4, -3, -2, -1, 0}));

    assertTrue(orderTwo.contains("PPSEQ|CpCp2C"));
    assertTrue(orderTwo.contains("BNDRY-SPAN-PPSEQ|CpCp2C"));
    assertTrue(orderThree.contains("PPPSEQ|CpCp2Cp3C"));
    assertTrue(orderThree.contains("BNDRY-SPAN-PPPSEQ|CpCp2Cp3C"));
    assertTrue(orderFour.contains("PPPPSEQ|CpCp2Cp3Cp4C"));
    assertTrue(orderFour.contains("BNDRY-SPAN-PPPPSEQ|CpCp2Cp3Cp4C"));
  }

  @Test
  public void combinedThreeNodeCliqueAlsoAddsBidirectionalSequenceFeatures() {
    Properties properties = new Properties();
    properties.setProperty("usePrev", "true");
    properties.setProperty("useNext", "true");
    properties.setProperty("usePrevSequences", "true");
    properties.setProperty("useNextSequences", "true");
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(new SeqClassifierFlags(properties, false));
    CoreLabel first = CoreLabel.wordFromString("first");
    CoreLabel middle = CoreLabel.wordFromString("middle");
    CoreLabel last = CoreLabel.wordFromString("last");
    CoreLabel pad = CoreLabel.wordFromString("<PAD>");
    PaddedList<CoreLabel> sentence = new PaddedList<>(Arrays.asList(first, middle, last), pad);

    Collection<String> features = factory.getCliqueFeatures(sentence, 2, Clique.valueOf(new int[]{-2, -1, 0}));

    assertTrue(features.contains("PNSEQ|CpCnC"));
    assertTrue(features.contains("middle-PNSEQW|CpCnC"));
  }

  @Test
  public void gazetteSupportsSloppyTokenAndCleanPhraseMatching() throws Exception {
    Path gazette = Files.createTempFile("ner-gazette", ".txt");
    Files.write(gazette, Arrays.asList("CITY New York", "ORG Stanford"), StandardCharsets.UTF_8);
    Properties cleanProperties = new Properties();
    cleanProperties.setProperty("gazette", gazette.toString());
    cleanProperties.setProperty("cleanGazette", "true");
    NERFeatureFactory<CoreLabel> cleanFactory = new NERFeatureFactory<>();
    cleanFactory.init(new SeqClassifierFlags(cleanProperties, false));
    Properties sloppyProperties = new Properties();
    sloppyProperties.setProperty("gazette", gazette.toString());
    sloppyProperties.setProperty("sloppyGazette", "true");
    NERFeatureFactory<CoreLabel> sloppyFactory = new NERFeatureFactory<>();
    sloppyFactory.init(new SeqClassifierFlags(sloppyProperties, false));
    CoreLabel newToken = CoreLabel.wordFromString("New");
    CoreLabel yorkToken = CoreLabel.wordFromString("York");
    CoreLabel mismatch = CoreLabel.wordFromString("Jersey");
    CoreLabel pad = CoreLabel.wordFromString("<PAD>");
    PaddedList<CoreLabel> matching = new PaddedList<>(Arrays.asList(newToken, yorkToken), pad);
    PaddedList<CoreLabel> nonMatching = new PaddedList<>(Arrays.asList(newToken, mismatch), pad);

    Collection<String> matchingFeatures = cleanFactory.getCliqueFeatures(matching, 0, Clique.valueOf(new int[]{0}));
    Collection<String> nonMatchingCleanFeatures = cleanFactory.getCliqueFeatures(nonMatching, 0, Clique.valueOf(new int[]{0}));
    Collection<String> nonMatchingSloppyFeatures = sloppyFactory.getCliqueFeatures(nonMatching, 0, Clique.valueOf(new int[]{0}));

    assertTrue(matchingFeatures.contains("CITY-GAZ|C"));
    assertTrue(matchingFeatures.contains("CITY-GAZ2|C"));
    assertFalse(nonMatchingCleanFeatures.contains("CITY-GAZ|C"));
    assertFalse(nonMatchingCleanFeatures.contains("CITY-GAZ2|C"));
    assertTrue(nonMatchingSloppyFeatures.contains("CITY-GAZ|C"));
    assertTrue(nonMatchingSloppyFeatures.contains("CITY-GAZ2|C"));
  }

  @Test
  public void distSimLexiconAnnotatesKnownAndUnknownWordsAndClearResetsDescription() throws Exception {
    Path lexicon = Files.createTempFile("ner-distsim", ".txt");
    Files.write(lexicon, Arrays.asList("stanford CLASS_A", "university CLASS_B"), StandardCharsets.UTF_8);
    Properties properties = new Properties();
    properties.setProperty("useDistSim", "true");
    properties.setProperty("distSimLexicon", lexicon.toString());
    properties.setProperty("unknownWordDistSimClass", "UNKNOWN_CLASS");
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(new SeqClassifierFlags(properties, false));
    CoreLabel known = CoreLabel.wordFromString("Stanford");
    CoreLabel unknown = CoreLabel.wordFromString("Elsewhere");
    CoreLabel pad = CoreLabel.wordFromString("<PAD>");
    PaddedList<CoreLabel> sentence = new PaddedList<>(Arrays.asList(known, unknown), pad);

    Collection<String> knownFeatures = factory.getCliqueFeatures(sentence, 0, Clique.valueOf(new int[]{0}));
    Collection<String> unknownFeatures = factory.getCliqueFeatures(sentence, 1, Clique.valueOf(new int[]{0}));

    assertEquals("Distsim lexicon of size 2", factory.describeDistsimLexicon());
    assertTrue(knownFeatures.contains("CLASS_A-DISTSIM|C"));
    assertTrue(unknownFeatures.contains("UNKNOWN_CLASS-DISTSIM|C"));
    factory.clearMemory();
    assertEquals("No distsim lexicon", factory.describeDistsimLexicon());
  }

  @Test(expected = IllegalArgumentException.class)
  public void unknownCliqueIsRejected() {
    Properties properties = new Properties();
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(new SeqClassifierFlags(properties, false));
    CoreLabel token = CoreLabel.wordFromString("token");
    CoreLabel pad = CoreLabel.wordFromString("<PAD>");
    PaddedList<CoreLabel> sentence = new PaddedList<>(Collections.singletonList(token), pad);

    factory.getCliqueFeatures(sentence, 0, Clique.valueOf(new int[]{1}));
  }

  @Test(expected = NullPointerException.class)
  public void callingFeatureExtractionBeforeInitializationFailsFast() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    CoreLabel token = CoreLabel.wordFromString("token");
    CoreLabel pad = CoreLabel.wordFromString("<PAD>");
    PaddedList<CoreLabel> sentence = new PaddedList<>(Collections.singletonList(token), pad);

    factory.getCliqueFeatures(sentence, 0, Clique.valueOf(new int[]{0}));
  }
}
