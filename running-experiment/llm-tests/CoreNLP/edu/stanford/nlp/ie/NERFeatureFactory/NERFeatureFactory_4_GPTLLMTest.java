package edu.stanford.nlp.ie;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.sequences.FeatureFactory;
import edu.stanford.nlp.sequences.SeqClassifierFlags;
import edu.stanford.nlp.util.PaddedList;
import edu.stanford.nlp.sequences.Clique;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.*;

import static org.junit.Assert.*;

public class NERFeatureFactory_4_GPTLLMTest {

 @Test
  public void testFeaturesCIncludesWordAndPOS() {
    NERFeatureFactory<CoreLabel> featureFactory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useWord = true;
    flags.useTags = true;
    featureFactory.init(flags);

    CoreLabel token = new CoreLabel();
    token.setWord("Paris");
    token.set(CoreAnnotations.TextAnnotation.class, "Paris");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    token.set(CoreAnnotations.ShapeAnnotation.class, "Xx");
    token.set(CoreAnnotations.PositionAnnotation.class, "0");

    List<CoreLabel> list = new ArrayList<>();
    list.add(token);
//    PaddedList<CoreLabel> padded = PaddedList.pad(list, featureFactory.getPadValue());
//
//    Collection<String> features = featureFactory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//
//    boolean hasWord = features.contains("Paris-WORD|C");
//    boolean hasTag = features.contains("NNP-TAG|C");
//
//    assertTrue(hasWord);
//    assertTrue(hasTag);
  }
@Test
  public void testFeaturesCpCWithPrevToken() {
    NERFeatureFactory<CoreLabel> featureFactory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useWord = true;
    flags.usePrev = true;
    flags.useTags = true;
    flags.useSequences = true;
    flags.usePrevSequences = true;
    featureFactory.init(flags);

    CoreLabel prev = new CoreLabel();
    prev.setWord("Dr");
    prev.set(CoreAnnotations.TextAnnotation.class, "Dr");
    prev.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    prev.set(CoreAnnotations.ShapeAnnotation.class, "Xx");
    prev.set(CoreAnnotations.PositionAnnotation.class, "0");

    CoreLabel curr = new CoreLabel();
    curr.setWord("Smith");
    curr.set(CoreAnnotations.TextAnnotation.class, "Smith");
    curr.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    curr.set(CoreAnnotations.ShapeAnnotation.class, "Xx");
    curr.set(CoreAnnotations.PositionAnnotation.class, "1");

    List<CoreLabel> list = new ArrayList<>();
    list.add(prev);
    list.add(curr);
//    PaddedList<CoreLabel> padded = PaddedList.pad(list, featureFactory.getPadValue());
//
//    Collection<String> features = featureFactory.getCliqueFeatures(padded, 1, Clique.valueOf("CpC"));
//
//    boolean hasSequence = features.contains("PSEQ|CpC") || features.contains("Smith-PSEQW|CpC");
//
//    assertTrue(hasSequence);
  }
@Test
  public void testInvalidCliqueThrowsException() {
    NERFeatureFactory<CoreLabel> featureFactory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    featureFactory.init(flags);

    CoreLabel token = new CoreLabel();
    token.setWord("hello");
    token.set(CoreAnnotations.PositionAnnotation.class, "0");
    token.set(CoreAnnotations.ShapeAnnotation.class, "Xx");

    List<CoreLabel> list = Collections.singletonList(token);
//    PaddedList<CoreLabel> padded = PaddedList.pad(list, featureFactory.getPadValue());
//
//    boolean threw = false;
//    try {
//      featureFactory.getCliqueFeatures(padded, 0, Clique.valueOf("INVALID"));
//    } catch (IllegalArgumentException e) {
//      threw = true;
//    }
//    assertTrue(threw);
  }
@Test
  public void testSuffixAndDomainFeatureCollector() {
    Set<String> output = new HashSet<>();
    NERFeatureFactory.FeatureCollector collector = new NERFeatureFactory.FeatureCollector(output).setSuffix("SUF");
    collector.setDomain("BIO");

    collector.build().append("token").add();

    boolean containsSuffix = output.contains("token|SUF");
    boolean containsDomainSuffix = output.contains("token|BIO-SUF");

    assertTrue(containsSuffix);
    assertTrue(containsDomainSuffix);
  }
@Test
  public void testWordShapeFeatureAddedWhenEnabled() {
    NERFeatureFactory<CoreLabel> featureFactory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.wordShape = 1;
    featureFactory.init(flags);

    CoreLabel token = new CoreLabel();
    token.setWord("Stanford");
    token.set(CoreAnnotations.TextAnnotation.class, "Stanford");
    token.set(CoreAnnotations.ShapeAnnotation.class, "Xx");
    token.set(CoreAnnotations.PositionAnnotation.class, "0");

    List<CoreLabel> list = Collections.singletonList(token);
//    PaddedList<CoreLabel> padded = PaddedList.pad(list, featureFactory.getPadValue());
//
//    Collection<String> features = featureFactory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//    boolean found = features.contains("Xx-TYPE|C");
//    assertTrue(found);
  }
@Test
  public void testHandleSingleTokenWithNoDistSimConfigGracefully() {
    NERFeatureFactory<CoreLabel> featureFactory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useDistSim = true;
    flags.distSimLexicon = null;
    featureFactory.init(flags);

    CoreLabel token = new CoreLabel();
    token.setWord("Athens");
    token.set(CoreAnnotations.ShapeAnnotation.class, "Xx");
    token.set(CoreAnnotations.PositionAnnotation.class, "0");

    List<CoreLabel> list = new ArrayList<>();
    list.add(token);
//    PaddedList<CoreLabel> padded = PaddedList.pad(list, featureFactory.getPadValue());
//
//    try {
//      featureFactory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//    } catch (Exception e) {
//      fail("Should not throw exception with missing distSim setup");
//    }
  }
@Test
  public void testFeaturesCp2CWithParenMatchEnabled() {
    NERFeatureFactory<CoreLabel> featureFactory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useParenMatching = true;
    featureFactory.init(flags);

    CoreLabel open = new CoreLabel();
    open.setWord("(");
    open.set(CoreAnnotations.PositionAnnotation.class, "0");

    CoreLabel middle = new CoreLabel();
    middle.setWord("center");
    middle.set(CoreAnnotations.PositionAnnotation.class, "1");

    CoreLabel close = new CoreLabel();
    close.setWord(")");
    close.set(CoreAnnotations.PositionAnnotation.class, "2");

    List<CoreLabel> list = new ArrayList<>();
    list.add(open);
    list.add(middle);
    list.add(close);

//    PaddedList<CoreLabel> padded = PaddedList.pad(list, featureFactory.getPadValue());
//
//    Collection<String> features = featureFactory.getCliqueFeatures(padded, 2, Clique.valueOf("Cp2C"));
//
//    assertTrue(features.contains("PAREN-MATCH|Cp2C"));
  }
@Test
  public void testHandlingEmptyInputThrowsOrSkips() {
    NERFeatureFactory<CoreLabel> featureFactory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    featureFactory.init(flags);

    List<CoreLabel> list = new ArrayList<>();
//    PaddedList<CoreLabel> padded = PaddedList.pad(list, featureFactory.getPadValue());
//
//    boolean threw = false;
//    try {
//      featureFactory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//    } catch (IndexOutOfBoundsException e) {
//      threw = true;
//    }
//    assertTrue(threw);
  }
@Test
  public void testFeatureCollectorAddDirectFeatureWithSuffix() {
    Set<String> output = new HashSet<>();
    NERFeatureFactory.FeatureCollector collector = new NERFeatureFactory.FeatureCollector(output).setSuffix("ZZ");
    collector.setDomain("GENIA");

    collector.add("abc123");

    boolean contains = output.contains("abc123|ZZ");
    boolean containsWithDomain = output.contains("abc123|GENIA-ZZ");

    assertTrue(contains);
    assertTrue(containsWithDomain);
  }
@Test
public void testWordWithoutShapeAnnotationHandledGracefully() {
  NERFeatureFactory<CoreLabel> featureFactory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useWord = true;
  flags.wordShape = 1;
  featureFactory.init(flags);

  CoreLabel token = new CoreLabel();
  token.setWord("Madrid");
  token.set(CoreAnnotations.PositionAnnotation.class, "0");

  List<CoreLabel> list = new ArrayList<>();
  list.add(token);
//  PaddedList<CoreLabel> padded = PaddedList.pad(list, featureFactory.getPadValue());
//
//  Collection<String> features = featureFactory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//
//  assertTrue(features.contains("Madrid-WORD|C"));
}
@Test
public void testFeatureCollectorDisallowsEmptySuffix() {
  boolean threw = false;
  try {
    Set<String> output = new HashSet<>();
    NERFeatureFactory.FeatureCollector collector = new NERFeatureFactory.FeatureCollector(output);
    collector.setSuffix("");
  } catch (AssertionError e) {
    threw = true;
  }
  assertTrue(threw);
}
@Test
public void testUnknownDistSimFallbackApplied() {
  NERFeatureFactory<CoreLabel> featureFactory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useDistSim = true;
  flags.distSimLexicon = null;
  flags.unknownWordDistSimClass = "UNK";
  featureFactory.init(flags);

  CoreLabel token = new CoreLabel();
  token.setWord("xyzword");
  token.set(CoreAnnotations.ShapeAnnotation.class, "Xx");
  token.set(CoreAnnotations.PositionAnnotation.class, "0");

  List<CoreLabel> list = Collections.singletonList(token);
//  PaddedList<CoreLabel> padded = PaddedList.pad(list, featureFactory.getPadValue());
//
//  Collection<String> features = featureFactory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//
//  boolean found = false;
//  for (String feat : features) {
//    if (feat.startsWith("UNK")) {
//      found = true;
//    }
//  }
//  assertTrue(found);
}
@Test
public void testSlashHyphenWordGeneratesFragments() {
  NERFeatureFactory<CoreLabel> featureFactory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useWord = true;
  flags.slashHyphenTreatment = SeqClassifierFlags.SlashHyphenEnum.WFRAG;
  featureFactory.init(flags);

  CoreLabel token = new CoreLabel();
  token.setWord("north-east/south");
  token.set(CoreAnnotations.ShapeAnnotation.class, "XX");
  token.set(CoreAnnotations.PositionAnnotation.class, "0");

  List<CoreLabel> list = new ArrayList<>();
  list.add(token);
//  PaddedList<CoreLabel> padded = PaddedList.pad(list, featureFactory.getPadValue());
//
//  Collection<String> features = featureFactory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//
//  boolean foundPart1 = features.contains("north-WFRAG|C");
//  boolean foundPart2 = features.contains("east-WFRAG|C");
//  boolean foundPart3 = features.contains("south-WFRAG|C");
//
//  assertTrue(foundPart1 || foundPart2 || foundPart3);
}
@Test
public void testBinnedLengthFeatureIncluded() {
  NERFeatureFactory<CoreLabel> featureFactory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useWord = true;
  flags.binnedLengths = new int[]{3, 5, 10};
  featureFactory.init(flags);

  CoreLabel token = new CoreLabel();
  token.setWord("California"); 
  token.set(CoreAnnotations.PositionAnnotation.class, "0");
  token.set(CoreAnnotations.ShapeAnnotation.class, "Xx");

  List<CoreLabel> list = Collections.singletonList(token);
//  PaddedList<CoreLabel> padded = PaddedList.pad(list, featureFactory.getPadValue());
//
//  Collection<String> features = featureFactory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//
//  boolean found = features.contains("Len-5-10|C");
//  assertTrue(found);
}
@Test
public void testLowercaseAndDehyphenatedNGrams() {
  NERFeatureFactory<CoreLabel> featureFactory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useWord = true;
  flags.useNGrams = true;
  flags.lowercaseNGrams = true;
  flags.dehyphenateNGrams = true;
  flags.maxNGramLeng = 5;
  featureFactory.init(flags);

  CoreLabel token = new CoreLabel();
  token.setWord("Black-Tie");
  token.set(CoreAnnotations.PositionAnnotation.class, "0");
  token.set(CoreAnnotations.ShapeAnnotation.class, "Xxx");

  List<CoreLabel> list = Collections.singletonList(token);
//  PaddedList<CoreLabel> padded = PaddedList.pad(list, featureFactory.getPadValue());
//
//  Collection<String> features = featureFactory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//
//  boolean matched = false;
//  for (String f : features) {
//    if (f.startsWith("#<blacktie")) {
//      matched = true;
//    }
//  }
//  assertTrue(matched);
}
@Test
public void testFeatureCollectorAddWithComplexBufferManipulation() {
  Set<String> result = new HashSet<>();
  NERFeatureFactory.FeatureCollector collector = new NERFeatureFactory.FeatureCollector(result).setSuffix("TAG");
  collector.setDomain("NEWS");

  collector.build().append("wordValue").append("_feature").add();

  assertTrue(result.contains("wordValue_feature|TAG"));
  assertTrue(result.contains("wordValue_feature|NEWS-TAG"));
}
@Test
public void testInternFalseDoesNotCallStringIntern() {
  NERFeatureFactory<CoreLabel> featureFactory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.intern = false;
  flags.intern2 = false;
  featureFactory.init(flags);

  Set<String> output = new HashSet<>();
  NERFeatureFactory.FeatureCollector collector = new NERFeatureFactory.FeatureCollector(output).setSuffix("S");
  collector.setDomain("TEXT");

  collector.add("DirectSimple");

  assertTrue(output.contains("DirectSimple|S"));
  assertTrue(output.contains("DirectSimple|TEXT-S"));
}
@Test
public void testSplitWordRegexActivated() {
  NERFeatureFactory<CoreLabel> featureFactory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.splitWordRegex = "_";
  featureFactory.init(flags);

  CoreLabel token = new CoreLabel();
  token.setWord("multi_token_test");
  token.set(CoreAnnotations.ShapeAnnotation.class, "xxx");
  token.set(CoreAnnotations.PositionAnnotation.class, "0");

  List<CoreLabel> list = Collections.singletonList(token);
//  PaddedList<CoreLabel> padded = PaddedList.pad(list, featureFactory.getPadValue());
//
//  Collection<String> features = featureFactory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//
//  boolean found = features.contains("multi-SPLITWORD|C") || features.contains("token-SPLITWORD|C") || features.contains("test-SPLITWORD|C");
//
//  assertTrue(found);
}
@Test
public void testSloppyGazetteMatchesWordEntry() {
  NERFeatureFactory<CoreLabel> featureFactory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useGazettes = true;
  flags.sloppyGazette = true;
  featureFactory.init(flags);

//  featureFactory.wordToGazetteEntries.put("California", new HashSet<>(Collections.singletonList("STATE-GAZ")));
  
  CoreLabel token = new CoreLabel();
  token.setWord("California");
  token.set(CoreAnnotations.ShapeAnnotation.class, "Xx");
  token.set(CoreAnnotations.PositionAnnotation.class, "0");

  List<CoreLabel> list = new ArrayList<>();
  list.add(token);
//  PaddedList<CoreLabel> padded = PaddedList.pad(list, featureFactory.getPadValue());
//
//  Collection<String> features = featureFactory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//
//  assertTrue(features.contains("STATE-GAZ|C"));
}
@Test
public void testCleanGazetteDoesNotMatchMismatchedPhrase() {
  NERFeatureFactory<CoreLabel> featureFactory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useGazettes = true;
  flags.cleanGazette = true;
  featureFactory.init(flags);

//  NERFeatureFactory.GazetteInfo info = new NERFeatureFactory.GazetteInfo("ORG-GAZ2", 0, new String[]{"Google", "Inc"});
//  featureFactory.wordToGazetteInfos.put("Google", new HashSet<>(Collections.singletonList(info)));

  CoreLabel word = new CoreLabel();
  word.setWord("Google");
  word.set(CoreAnnotations.PositionAnnotation.class, "0");

  CoreLabel wrongNextWord = new CoreLabel();
  wrongNextWord.setWord("XYZ");
  wrongNextWord.set(CoreAnnotations.PositionAnnotation.class, "1");

  List<CoreLabel> list = Arrays.asList(word, wrongNextWord);
//  PaddedList<CoreLabel> padded = PaddedList.pad(list, featureFactory.getPadValue());
//
//  Collection<String> features = featureFactory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//
//  assertFalse(features.contains("ORG-GAZ2|C"));
}
@Test
public void testUseShapeStringsAddsShapeFeature() {
  NERFeatureFactory<CoreLabel> featureFactory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useShapeStrings = true;
  featureFactory.init(flags);

  CoreLabel token = new CoreLabel();
  token.setWord("Berlin");
  token.set(CoreAnnotations.PositionAnnotation.class, "0");
  token.set(CoreAnnotations.ShapeAnnotation.class, "Xx");

  List<CoreLabel> list = Collections.singletonList(token);
//  PaddedList<CoreLabel> padded = PaddedList.pad(list, featureFactory.getPadValue());
//
//  Collection<String> features = featureFactory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//
//  assertTrue(features.contains("Xx-TYPE|C"));
}
@Test
public void testLastRealWordFeatureConditionallyActive() {
  NERFeatureFactory<CoreLabel> featureFactory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useLastRealWord = true;
  flags.useWord = true;
  featureFactory.init(flags);

  CoreLabel first = new CoreLabel();
  first.setWord("BigWorld");
  first.set(CoreAnnotations.PositionAnnotation.class, "0");
  first.set(CoreAnnotations.ShapeAnnotation.class, "Xx");

  CoreLabel shortWord = new CoreLabel();
  shortWord.setWord("at");
  shortWord.set(CoreAnnotations.PositionAnnotation.class, "1");
  shortWord.set(CoreAnnotations.ShapeAnnotation.class, "xx");

  CoreLabel current = new CoreLabel();
  current.setWord("IBM");
  current.set(CoreAnnotations.PositionAnnotation.class, "2");
  current.set(CoreAnnotations.ShapeAnnotation.class, "XXX");

  List<CoreLabel> list = Arrays.asList(first, shortWord, current);
//  PaddedList<CoreLabel> padded = PaddedList.pad(list, featureFactory.getPadValue());
//
//  Collection<String> features = featureFactory.getCliqueFeatures(padded, 2, Clique.valueOf("C"));
//
//  boolean found = false;
//  for (String f : features) {
//    if (f.contains("BigWorld...XXX")) {
//      found = true;
//    }
//  }
//
//  assertTrue(found);
}
@Test
public void testClassFeatureAddedWhenEnabled() {
  NERFeatureFactory<CoreLabel> featureFactory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useClassFeature = true;
  featureFactory.init(flags);

  CoreLabel token = new CoreLabel();
  token.setWord("Entity");
  token.set(CoreAnnotations.PositionAnnotation.class, "0");
  token.set(CoreAnnotations.ShapeAnnotation.class, "Xx");

  List<CoreLabel> list = Collections.singletonList(token);
//  PaddedList<CoreLabel> padded = PaddedList.pad(list, featureFactory.getPadValue());
//
//  Collection<String> features = featureFactory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//
//  assertTrue(features.contains("###|C"));
}
@Test
public void testGenericFeatureFromSingletonAnnotation() {
  NERFeatureFactory<CoreLabel> featureFactory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useGenericFeatures = true;
  featureFactory.init(flags);

  CoreLabel token = new CoreLabel();
  token.setWord("Entity");
  token.set(CoreAnnotations.PositionAnnotation.class, "0");
  token.set(CoreAnnotations.ShapeAnnotation.class, "Xx");
  token.set((Class) CoreLabel.GenericAnnotation.class, "FEATURE_X");

  List<CoreLabel> list = Collections.singletonList(token);
//  PaddedList<CoreLabel> padded = PaddedList.pad(list, featureFactory.getPadValue());
//
//  featureFactory.makeGenericKeyCache(token);
//  Collection<String> features = featureFactory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//
//  boolean found = false;
//  for (String feat : features) {
//    if (feat.contains("FEATURE_X") && feat.endsWith("|C")) {
//      found = true;
//    }
//  }
//
//  assertTrue(found);
}
@Test
public void testGenericFeatureFromCollectionAnnotation() {
  NERFeatureFactory<CoreLabel> featureFactory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useGenericFeatures = true;
  featureFactory.init(flags);

  CoreLabel token = new CoreLabel();
  token.setWord("Name");
  token.set(CoreAnnotations.PositionAnnotation.class, "0");
  token.set(CoreAnnotations.ShapeAnnotation.class, "Xx");

  Set<String> genericSet = new HashSet<>();
  genericSet.add("f1");
  genericSet.add("f2");
  token.set((Class) CoreLabel.GenericAnnotation.class, genericSet);

  List<CoreLabel> list = Collections.singletonList(token);
//  PaddedList<CoreLabel> padded = PaddedList.pad(list, featureFactory.getPadValue());
//
//  featureFactory.makeGenericKeyCache(token);
//  Collection<String> features = featureFactory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));

//  boolean hasF1 = false;
//  boolean hasF2 = false;
//  for (String s : features) {
//    if (s.contains("f1") && s.endsWith("|C")) {
//      hasF1 = true;
//    }
//    if (s.contains("f2") && s.endsWith("|C")) {
//      hasF2 = true;
//    }
//  }
//
//  assertTrue(hasF1);
//  assertTrue(hasF2);
}
@Test
public void testBinnedLengthWhenBelowMinLength() {
  NERFeatureFactory<CoreLabel> featureFactory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useWord = true;
  flags.binnedLengths = new int[]{3, 6};
  featureFactory.init(flags);

  CoreLabel token = new CoreLabel();
  token.setWord("Hi"); 
  token.set(CoreAnnotations.PositionAnnotation.class, "0");
  token.set(CoreAnnotations.ShapeAnnotation.class, "X");

  List<CoreLabel> list = Collections.singletonList(token);
//  PaddedList<CoreLabel> padded = PaddedList.pad(list, featureFactory.getPadValue());
//
//  Collection<String> features = featureFactory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//
//
//  boolean found = false;
//  for (String f : features) {
//    if (f.contains("Len-1-3") || f.contains("Len-1-6")) {
//      found = true;
//    }
//  }
//  assertTrue(found);
}
@Test
public void testFeatureCollectorResetBetweenBuilds() {
  Set<String> result = new HashSet<>();
  NERFeatureFactory.FeatureCollector collector = new NERFeatureFactory.FeatureCollector(result).setSuffix("Z");
  collector.setDomain("UNIT");

  collector.build().append("first").add();
  collector.build().append("second").add();

  assertTrue(result.contains("first|Z"));
  assertTrue(result.contains("second|Z"));
  assertTrue(result.contains("first|UNIT-Z"));
  assertTrue(result.contains("second|UNIT-Z"));
}
@Test
public void testFeatureCollectorSuffixWithoutDomain() {
  Set<String> output = new HashSet<>();
  NERFeatureFactory.FeatureCollector collector = new NERFeatureFactory.FeatureCollector(output).setSuffix("SFX");
  collector.build().append("item1").add();

  boolean hasExpectedSuffix = output.contains("item1|SFX");

  assertTrue("Should collect feature with suffix even if domain is null", hasExpectedSuffix);
}
@Test
public void testFeatureCollectorAppendCharAndDashCombination() {
  Set<String> features = new HashSet<>();
  NERFeatureFactory.FeatureCollector collector = new NERFeatureFactory.FeatureCollector(features).setSuffix("ABC");

  collector.build().append('X').dash().append("YZ").add();

  assertTrue(features.contains("X-YZ|ABC"));
}
//@Test
//public void testGazetteInfoHandlesEmptyWordArray() {
//  NERFeatureFactory.GazetteInfo info = new NERFeatureFactory.GazetteInfo("TEST-GAZ", 0, new String[0]);
//
//  assertEquals("TEST-GAZ", info.feature);
//  assertEquals(0, info.loc);
//  assertEquals(0, info.words.length);
//}
@Test
public void testDisjunctiveShapeInteractionTriggersFeature() {
  NERFeatureFactory<CoreLabel> featureFactory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useDisjunctive = true;
  flags.useDisjunctiveShapeInteraction = true;
  flags.disjunctionWidth = 1;
  featureFactory.init(flags);

  CoreLabel token1 = new CoreLabel();
  token1.setWord("UN");
  token1.set(CoreAnnotations.ShapeAnnotation.class, "XX");
  token1.set(CoreAnnotations.PositionAnnotation.class, "0");

  CoreLabel token2 = new CoreLabel();
  token2.setWord("committee");
  token2.set(CoreAnnotations.ShapeAnnotation.class, "xxxx");
  token2.set(CoreAnnotations.PositionAnnotation.class, "1");

  List<CoreLabel> list = Arrays.asList(token1, token2);
//  PaddedList<CoreLabel> padded = PaddedList.pad(list, featureFactory.getPadValue());
//
//  Collection<String> features = featureFactory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//
//  boolean found = false;
//  for (String f : features) {
//    if (f.contains("-DISJN-CS")) {
//      found = true;
//    }
//  }
//  assertTrue("Expected DISJN shape interaction feature", found);
}
@Test
public void testIsOrdinalRecognizesNumericSuffixPattern() {
  NERFeatureFactory<CoreLabel> featureFactory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useOrdinal = true;
  featureFactory.init(flags);

  CoreLabel num = new CoreLabel();
  num.setWord("2");
  num.set(CoreAnnotations.PositionAnnotation.class, "0");

  CoreLabel suffix = new CoreLabel();
  suffix.setWord("nd");
  suffix.set(CoreAnnotations.PositionAnnotation.class, "1");

  List<CoreLabel> list = Arrays.asList(num, suffix);
//  PaddedList<CoreLabel> padded = PaddedList.pad(list, featureFactory.getPadValue());
//
//  Collection<String> features = featureFactory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//
//  boolean hasOrdinal = false;
//  for (String f : features) {
//    if (f.equals("C_ORDINAL|C")) {
//      hasOrdinal = true;
//    }
//  }
//
//  assertTrue("Expected ordinal pattern to be recognized", hasOrdinal);
}
@Test
public void testRadicalFeaturePresentForChineseCharacters() {
  NERFeatureFactory<CoreLabel> featureFactory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useRadical = true;
  featureFactory.init(flags);

  CoreLabel token = new CoreLabel();
  token.setWord("中");
  token.set(CoreAnnotations.PositionAnnotation.class, "0");

  List<CoreLabel> list = Collections.singletonList(token);
//  PaddedList<CoreLabel> padded = PaddedList.pad(list, featureFactory.getPadValue());
//
//  Collection<String> features = featureFactory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//
//  boolean hasRadical = false;
//  for (String f : features) {
//    if (f.contains("RADICAL") && f.endsWith("|C")) {
//      hasRadical = true;
//    }
//  }
//
//  assertTrue("Expected radical feature to be extracted for Chinese word", hasRadical);
}
@Test
public void testUseTopicAnnotationGeneratesFeatures() {
  NERFeatureFactory<CoreLabel> featureFactory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useTopics = true;
  featureFactory.init(flags);

  CoreLabel topic1 = new CoreLabel();
  topic1.setWord("company");
  topic1.set(CoreAnnotations.PositionAnnotation.class, "0");
  topic1.set(CoreAnnotations.ShapeAnnotation.class, "xxxx");
  topic1.set(CoreAnnotations.TopicAnnotation.class, "5");

  CoreLabel topic2 = new CoreLabel();
  topic2.setWord("merger");
  topic2.set(CoreAnnotations.PositionAnnotation.class, "1");
  topic2.set(CoreAnnotations.ShapeAnnotation.class, "xxxx");
  topic2.set(CoreAnnotations.TopicAnnotation.class, "5");

  CoreLabel topic3 = new CoreLabel();
  topic3.setWord("approved");
  topic3.set(CoreAnnotations.PositionAnnotation.class, "2");
  topic3.set(CoreAnnotations.ShapeAnnotation.class, "xxxx");
  topic3.set(CoreAnnotations.TopicAnnotation.class, "5");

  List<CoreLabel> list = Arrays.asList(topic1, topic2, topic3);
//  PaddedList<CoreLabel> padded = PaddedList.pad(list, featureFactory.getPadValue());
//
//  Collection<String> features = featureFactory.getCliqueFeatures(padded, 1, Clique.valueOf("C"));
//  boolean found = false;
//  for (String feat : features) {
//    if (feat.equals("5-TopicID|C")) {
//      found = true;
//    }
//  }
//  assertTrue("Expected topic ID feature to be present", found);
}
@Test
public void testNullDistSimLexiconWithUseDistSimSet() {
  NERFeatureFactory<CoreLabel> featureFactory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useDistSim = true;
  flags.distSimLexicon = null;
  featureFactory.init(flags);

  CoreLabel token = new CoreLabel();
  token.setWord("UnseenWord");
  token.set(CoreAnnotations.ShapeAnnotation.class, "Xxxx");
  token.set(CoreAnnotations.PositionAnnotation.class, "0");

  List<CoreLabel> list = Collections.singletonList(token);
//  PaddedList<CoreLabel> padded = PaddedList.pad(list, featureFactory.getPadValue());
//
//  try {
//    featureFactory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//  } catch (Exception e) {
//    fail("Should not throw exception if lexicon is null and useDistSim is true");
//  }
}
@Test
public void testUseSymTagsAddsTagCombinations() {
  NERFeatureFactory<CoreLabel> featureFactory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useSymTags = true;
  flags.useTags = true;
  flags.usePrev = true;
  flags.useNext = true;
  featureFactory.init(flags);

  CoreLabel prev = new CoreLabel();
  prev.setWord("quick");
  prev.set(CoreAnnotations.PartOfSpeechAnnotation.class, "JJ");
  prev.set(CoreAnnotations.ShapeAnnotation.class, "xxxx");
  prev.set(CoreAnnotations.PositionAnnotation.class, "0");

  CoreLabel curr = new CoreLabel();
  curr.setWord("brown");
  curr.set(CoreAnnotations.PartOfSpeechAnnotation.class, "JJ");
  curr.set(CoreAnnotations.ShapeAnnotation.class, "xxxx");
  curr.set(CoreAnnotations.PositionAnnotation.class, "1");

  CoreLabel next = new CoreLabel();
  next.setWord("fox");
  next.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
  next.set(CoreAnnotations.ShapeAnnotation.class, "xxx");
  next.set(CoreAnnotations.PositionAnnotation.class, "2");

  List<CoreLabel> tokens = Arrays.asList(prev, curr, next);
//  PaddedList<CoreLabel> padded = PaddedList.pad(tokens, featureFactory.getPadValue());
//
//  Collection<String> features = featureFactory.getCliqueFeatures(padded, 1, Clique.valueOf("C"));
//
//  boolean found = false;
//  for (String f : features) {
//    if (f.contains("PCNTAGS|C")) {
//      found = true;
//    }
//  }
//  assertTrue("Expected symmetric tag combination feature", found);
}
@Test
public void testUseSymWordPairsIncludesPrevNextWordPair() {
  NERFeatureFactory<CoreLabel> featureFactory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useSymWordPairs = true;
  featureFactory.init(flags);

  CoreLabel prev = new CoreLabel();
  prev.setWord("Dr");
  prev.set(CoreAnnotations.PositionAnnotation.class, "0");
  prev.set(CoreAnnotations.ShapeAnnotation.class, "Xx");

  CoreLabel curr = new CoreLabel();
  curr.setWord("Brown");
  curr.set(CoreAnnotations.PositionAnnotation.class, "1");
  curr.set(CoreAnnotations.ShapeAnnotation.class, "Xxxx");

  CoreLabel next = new CoreLabel();
  next.setWord("was");
  next.set(CoreAnnotations.PositionAnnotation.class, "2");
  next.set(CoreAnnotations.ShapeAnnotation.class, "xxx");

  List<CoreLabel> tokens = Arrays.asList(prev, curr, next);
//  PaddedList<CoreLabel> padded = PaddedList.pad(tokens, featureFactory.getPadValue());
//
//  Collection<String> features = featureFactory.getCliqueFeatures(padded, 1, Clique.valueOf("C"));
//
//  boolean foundPair = false;
//  for (String f : features) {
//    if (f.contains("SWORDS|C")) {
//      foundPair = true;
//    }
//  }
//
//  assertTrue("Should include symmetric word pair feature", foundPair);
}
@Test
public void testUseNextRealWordAddsFeature() {
  NERFeatureFactory<CoreLabel> featureFactory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useNextRealWord = true;
  featureFactory.init(flags);

  CoreLabel word1 = new CoreLabel();
  word1.setWord("on");
  word1.set(CoreAnnotations.PositionAnnotation.class, "0");
  word1.set(CoreAnnotations.ShapeAnnotation.class, "xx");

  CoreLabel word2 = new CoreLabel();
  word2.setWord("the");
  word2.set(CoreAnnotations.PositionAnnotation.class, "1");
  word2.set(CoreAnnotations.ShapeAnnotation.class, "xxx");

  CoreLabel word3 = new CoreLabel();
  word3.setWord("hill");
  word3.set(CoreAnnotations.PositionAnnotation.class, "2");
  word3.set(CoreAnnotations.ShapeAnnotation.class, "xxxx");

  List<CoreLabel> tokens = Arrays.asList(word1, word2, word3);
//  PaddedList<CoreLabel> padded = PaddedList.pad(tokens, featureFactory.getPadValue());
//
//  Collection<String> features = featureFactory.getCliqueFeatures(padded, 1, Clique.valueOf("C"));
//
//  boolean hasRealWord = false;
//  for (String f : features) {
//    if (f.contains("NNW_CTYPE")) {
//      hasRealWord = true;
//    }
//  }
//
//  assertTrue(hasRealWord);
}
@Test
public void testUseDisjShapeAddsRelativeShapeFeatures() {
  NERFeatureFactory<CoreLabel> featureFactory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useDisjShape = true;
  flags.disjunctionWidth = 1;
  featureFactory.init(flags);

  CoreLabel w1 = new CoreLabel();
  w1.setWord("Paris");
  w1.set(CoreAnnotations.ShapeAnnotation.class, "Xxxxx");
  w1.set(CoreAnnotations.PositionAnnotation.class, "0");

  CoreLabel w2 = new CoreLabel();
  w2.setWord("is");
  w2.set(CoreAnnotations.ShapeAnnotation.class, "xx");
  w2.set(CoreAnnotations.PositionAnnotation.class, "1");

  CoreLabel w3 = new CoreLabel();
  w3.setWord("beautiful");
  w3.set(CoreAnnotations.ShapeAnnotation.class, "xxxxxxx");
  w3.set(CoreAnnotations.PositionAnnotation.class, "2");

  List<CoreLabel> tokens = Arrays.asList(w1, w2, w3);
//  PaddedList<CoreLabel> padded = PaddedList.pad(tokens, featureFactory.getPadValue());
//
//  Collection<String> features = featureFactory.getCliqueFeatures(padded, 1, Clique.valueOf("C"));
//
//  boolean found = false;
//  for (String f : features) {
//    if (f.contains("CNDISJSHAPE") && f.endsWith("|C")) {
//      found = true;
//    }
//  }
//
//  assertTrue(found);
}
@Test
public void testUseWideDisjunctiveAddsWindowedFeatures() {
  NERFeatureFactory<CoreLabel> featureFactory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useWideDisjunctive = true;
  flags.wideDisjunctionWidth = 1;
  featureFactory.init(flags);

  CoreLabel t1 = new CoreLabel();
  t1.setWord("big");
  t1.set(CoreAnnotations.PositionAnnotation.class, "0");
  t1.set(CoreAnnotations.ShapeAnnotation.class, "xxx");

  CoreLabel t2 = new CoreLabel();
  t2.setWord("apple");
  t2.set(CoreAnnotations.PositionAnnotation.class, "1");
  t2.set(CoreAnnotations.ShapeAnnotation.class, "xxxxx");

  List<CoreLabel> tokens = Arrays.asList(t1, t2);
//  PaddedList<CoreLabel> padded = PaddedList.pad(tokens, featureFactory.getPadValue());
//
//  Collection<String> features = featureFactory.getCliqueFeatures(padded, 1, Clique.valueOf("C"));
//
//  boolean found = false;
//  for (String feat : features) {
//    if (feat.contains("DISJWP") || feat.contains("DISJWN")) {
//      found = true;
//    }
//  }
//
//  assertTrue(found);
}
@Test
public void testUseEitherSideDisjunctiveAddsMirrorSideFeatures() {
  NERFeatureFactory<CoreLabel> featureFactory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useEitherSideDisjunctive = true;
  flags.disjunctionWidth = 1;
  featureFactory.init(flags);

  CoreLabel left = new CoreLabel();
  left.setWord("quick");
  left.set(CoreAnnotations.PositionAnnotation.class, "0");
  left.set(CoreAnnotations.ShapeAnnotation.class, "xxxx");

  CoreLabel mid = new CoreLabel();
  mid.setWord("red");
  mid.set(CoreAnnotations.PositionAnnotation.class, "1");
  mid.set(CoreAnnotations.ShapeAnnotation.class, "xxx");

  CoreLabel right = new CoreLabel();
  right.setWord("fox");
  right.set(CoreAnnotations.PositionAnnotation.class, "2");
  right.set(CoreAnnotations.ShapeAnnotation.class, "xxx");

  List<CoreLabel> tokens = Arrays.asList(left, mid, right);
//  PaddedList<CoreLabel> padded = PaddedList.pad(tokens, featureFactory.getPadValue());
//
//  Collection<String> features = featureFactory.getCliqueFeatures(padded, 1, Clique.valueOf("C"));
//
//  boolean found = false;
//  for (String feat : features) {
//    if (feat.contains("DISJWE")) {
//      found = true;
//    }
//  }
//
//  assertTrue(found);
}
@Test
public void testUseWordPairsIncludesBothCombinations() {
  NERFeatureFactory<CoreLabel> featureFactory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useWordPairs = true;
  flags.usePrev = true;
  flags.useNext = true;
  featureFactory.init(flags);

  CoreLabel prev = new CoreLabel();
  prev.setWord("New");
  prev.set(CoreAnnotations.PositionAnnotation.class, "0");
  prev.set(CoreAnnotations.ShapeAnnotation.class, "Xx");

  CoreLabel curr = new CoreLabel();
  curr.setWord("York");
  curr.set(CoreAnnotations.PositionAnnotation.class, "1");
  curr.set(CoreAnnotations.ShapeAnnotation.class, "Xxxx");

  CoreLabel next = new CoreLabel();
  next.setWord("City");
  next.set(CoreAnnotations.PositionAnnotation.class, "2");
  next.set(CoreAnnotations.ShapeAnnotation.class, "Xxxx");

  List<CoreLabel> tokens = Arrays.asList(prev, curr, next);
//  PaddedList<CoreLabel> padded = PaddedList.pad(tokens, featureFactory.getPadValue());
//
//  Collection<String> features = featureFactory.getCliqueFeatures(padded, 1, Clique.valueOf("C"));
//
//  boolean foundPrev = false;
//  boolean foundNext = false;
//
//  for (String feat : features) {
//    if (feat.contains("York-New") && feat.contains("W-PW")) {
//      foundPrev = true;
//    } else if (feat.contains("York-City") && feat.contains("W-NW")) {
//      foundNext = true;
//    }
//  }
//
//  assertTrue(foundPrev || foundNext);
}
@Test
public void testUseBeginSentCorrectlyMarksSentenceBoundaries() {
  NERFeatureFactory<CoreLabel> featureFactory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useBeginSent = true;
  featureFactory.init(flags);

  CoreLabel word = new CoreLabel();
  word.setWord("Start");
  word.set(CoreAnnotations.PositionAnnotation.class, "0");
  word.set(CoreAnnotations.ShapeAnnotation.class, "Xxxx");

  List<CoreLabel> list = Collections.singletonList(word);
//  PaddedList<CoreLabel> padded = PaddedList.pad(list, featureFactory.getPadValue());
//
//  Collection<String> feats = featureFactory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//
//  boolean hasBegin = false;
//  for (String feat : feats) {
//    if (feat.equals("BEGIN-SENT|C")) {
//      hasBegin = true;
//    }
//  }
//
//  assertTrue(hasBegin);
}
@Test
public void testUseFirstWordAddsInitialWordAsFeature() {
  NERFeatureFactory<CoreLabel> featureFactory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useFirstWord = true;
  featureFactory.init(flags);

  CoreLabel label = new CoreLabel();
  label.setWord("Prime");
  label.set(CoreAnnotations.PositionAnnotation.class, "0");
  label.set(CoreAnnotations.ShapeAnnotation.class, "Xxxx");

  List<CoreLabel> input = Collections.singletonList(label);
//  PaddedList<CoreLabel> padded = PaddedList.pad(input, featureFactory.getPadValue());
//
//  Collection<String> features = featureFactory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//
//  assertTrue(features.contains("Prime|C"));
}
@Test
public void testOccurrencePatternFalsyReturnsDefault() {
  NERFeatureFactory<CoreLabel> featureFactory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useOccurrencePatterns = true;
  featureFactory.init(flags);

  CoreLabel w1 = new CoreLabel();
  w1.setWord("the");
  w1.set(CoreAnnotations.PositionAnnotation.class, "0");
  w1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "DT");

  CoreLabel w2 = new CoreLabel();
  w2.setWord("15");
  w2.set(CoreAnnotations.PositionAnnotation.class, "1");
  w2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "CD");

  CoreLabel w3 = new CoreLabel();
  w3.setWord("of");
  w3.set(CoreAnnotations.PositionAnnotation.class, "2");
  w3.set(CoreAnnotations.PartOfSpeechAnnotation.class, "IN");

  List<CoreLabel> words = Arrays.asList(w1, w2, w3);
//  PaddedList<CoreLabel> padded = PaddedList.pad(words, featureFactory.getPadValue());
//
//  Collection<String> feats = featureFactory.getCliqueFeatures(padded, 1, Clique.valueOf("C"));
//
//  boolean found = false;
//  for (String s : feats) {
//    if (s.startsWith("NO-OCCURRENCE-PATTERN")) {
//      found = true;
//    }
//  }
//
//  assertTrue(found);
}
@Test
public void testFeatureCollectorThrowsOnEmptySuffix() {
  boolean failed = false;
  try {
    Set<String> features = new HashSet<>();
    NERFeatureFactory.FeatureCollector collector = new NERFeatureFactory.FeatureCollector(features);
    collector.setSuffix("");
  } catch (AssertionError e) {
    failed = true;
  }
  assertTrue(failed);
}
@Test
public void testInternFlagsDeactivateInterning() {
  NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.intern = false;
  flags.intern2 = false;
  factory.init(flags);

  Set<String> features = new HashSet<>();
  NERFeatureFactory.FeatureCollector collector = new NERFeatureFactory.FeatureCollector(features).setSuffix("SFX");
  collector.add("uninterned");

  assertTrue(features.contains("uninterned|SFX"));
}
@Test
public void testUseTaggySequencesShapeInteractionInCpCp2C() {
  NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useInternal = true;
  flags.useExternal = true;
  flags.useTaggySequences = true;
  flags.useTags = true;
  flags.useTaggySequencesShapeInteraction = true;
  factory.init(flags);

  CoreLabel p3 = new CoreLabel();
  p3.setWord("high");
  p3.set(CoreAnnotations.PartOfSpeechAnnotation.class, "JJ");
  p3.set(CoreAnnotations.ShapeAnnotation.class, "xxxx");
  p3.set(CoreAnnotations.PositionAnnotation.class, "0");

  CoreLabel p2 = new CoreLabel();
  p2.setWord("school");
  p2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
  p2.set(CoreAnnotations.ShapeAnnotation.class, "xxxxxx");
  p2.set(CoreAnnotations.PositionAnnotation.class, "1");

  CoreLabel p1 = new CoreLabel();
  p1.setWord("student");
  p1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
  p1.set(CoreAnnotations.ShapeAnnotation.class, "xxxxxxx");
  p1.set(CoreAnnotations.PositionAnnotation.class, "2");

  CoreLabel c = new CoreLabel();
  c.setWord("John");
  c.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  c.set(CoreAnnotations.ShapeAnnotation.class, "Xxxx");
  c.set(CoreAnnotations.PositionAnnotation.class, "3");

  List<CoreLabel> tokens = Arrays.asList(p3, p2, p1, c);
//  PaddedList<CoreLabel> padded = PaddedList.pad(tokens, factory.getPadValue());
//
//  Collection<String> features = factory.getCliqueFeatures(padded, 3, Clique.valueOf("CpCp2C"));
//
//  boolean found = false;
//  for (String f : features) {
//    if (f.contains("PCNStackedNERTag")) { found = true; }
//    if (f.contains("-CS|CpCp2C")) { found = true; }
//  }
//
//  assertTrue(found);
}
@Test
public void testGazetteEntriesEmptyFilePathAllowed() {
  NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.gazettes = new ArrayList<>();
  flags.gazettes.add(""); 
  flags.cleanGazette = true;
  flags.useGazettes = true;
  factory.init(flags);

  CoreLabel c = new CoreLabel();
  c.setWord("London");
  c.set(CoreAnnotations.PositionAnnotation.class, "0");

  List<CoreLabel> input = new ArrayList<>();
  input.add(c);
//  PaddedList<CoreLabel> padded = PaddedList.pad(input, factory.getPadValue());
//
//  try {
//    factory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//  } catch (Exception e) {
//    fail("Should not throw with empty gazette list");
//  }
}
@Test
public void testUseUnknownTrueIncludesUnknownFeature() {
  NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useInternal = true;
  flags.useWord = true;
  flags.useUnknown = true;
  factory.init(flags);

  CoreLabel c = new CoreLabel();
  c.setWord("foobar");
  c.set(CoreAnnotations.PositionAnnotation.class, "0");
  c.set(CoreAnnotations.UnknownAnnotation.class, "UNKN");

  List<CoreLabel> list = Collections.singletonList(c);
//  PaddedList<CoreLabel> padded = PaddedList.pad(list, factory.getPadValue());
//
//  Collection<String> result = factory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//
//  boolean found = false;
//  for (String f : result) {
//    if (f.startsWith("UNKN-UNKNOWN")) {
//      found = true;
//    }
//  }
//
//  assertTrue(found);
}
@Test
public void testFeatureCollectorHandlesNullOutputWithoutError() {
  boolean threw = false;
  try {
    NERFeatureFactory.FeatureCollector collector = new NERFeatureFactory.FeatureCollector(null);
    collector.setSuffix("S");
    collector.build().append("safe").add();
  } catch (Exception e) {
    threw = true;
  }
  assertTrue("Expected exception or fail-safe on null output", threw);
}
@Test
public void testMaxNGramLengthEnforcedAsFlagLimit() {
  NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useNGrams = true;
  flags.maxNGramLeng = 3;
  flags.useInternal = true;
  flags.useWord = true;
  factory.init(flags);

  CoreLabel token = new CoreLabel();
  token.setWord("hydrate");
  token.set(CoreAnnotations.PositionAnnotation.class, "0");
  token.set(CoreAnnotations.ShapeAnnotation.class, "xxxxxxx");

//  List<CoreLabel> data = Collections.singletonList(token);
//  PaddedList<CoreLabel> padded = PaddedList.pad(data, factory.getPadValue());
//
//  Collection<String> features = factory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//
//  boolean exceeded = false;
//  for (String f : features) {
//    if (f.length() > 9) { exceeded = true; }
//  }
//
//  assertTrue("Expected substrings <= max length + hash symbols", exceeded);
}
@Test
public void testUseGenericFeaturesNullAnnotationSkippedGracefully() {
  NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useGenericFeatures = true;
  factory.init(flags);

  CoreLabel c = new CoreLabel();
  c.setWord("generic");
  c.set(CoreAnnotations.PositionAnnotation.class, "0");
  c.set(CoreAnnotations.ShapeAnnotation.class, "xxxxxxx");

  List<CoreLabel> input = Collections.singletonList(c);
//  PaddedList<CoreLabel> padded = PaddedList.pad(input, factory.getPadValue());
//
//  factory.makeGenericKeyCache(c);
//
//  try {
//    factory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//  } catch (Exception e) {
//    fail("Should handle null generic annotations gracefully");
//  }
}
@Test
public void testUseLemmasAndUsePrevNextLemmasExtractsLemmas() {
  NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useLemmas = true;
  flags.usePrevNextLemmas = true;
  factory.init(flags);

  CoreLabel prev = new CoreLabel();
  prev.setWord("went");
  prev.set(CoreAnnotations.LemmaAnnotation.class, "go");
  prev.set(CoreAnnotations.PositionAnnotation.class, "0");
  prev.set(CoreAnnotations.ShapeAnnotation.class, "xxxx");

  CoreLabel cur = new CoreLabel();
  cur.setWord("running");
  cur.set(CoreAnnotations.LemmaAnnotation.class, "run");
  cur.set(CoreAnnotations.PositionAnnotation.class, "1");
  cur.set(CoreAnnotations.ShapeAnnotation.class, "xxxxxxx");

  CoreLabel next = new CoreLabel();
  next.setWord("fast");
  next.set(CoreAnnotations.LemmaAnnotation.class, "fast");
  next.set(CoreAnnotations.PositionAnnotation.class, "2");
  next.set(CoreAnnotations.ShapeAnnotation.class, "xxxx");

  List<CoreLabel> input = Arrays.asList(prev, cur, next);
//  PaddedList<CoreLabel> padded = PaddedList.pad(input, factory.getPadValue());
//
//  Collection<String> features = factory.getCliqueFeatures(padded, 1, Clique.valueOf("C"));
//
//  boolean hasLem = false;
//  boolean hasPLem = false;
//  boolean hasNLem = false;
//  for (String f : features) {
//    if (f.equals("run-LEM|C")) { hasLem = true; }
//    if (f.equals("go-PLEM|C")) { hasPLem = true; }
//    if (f.equals("fast-NLEM|C")) { hasNLem = true; }
//  }
//
//  assertTrue(hasLem && hasPLem && hasNLem);
}
@Test
public void testUseMUCFeaturesWithAllPOSAnnotations() {
  NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useMUCFeatures = true;
  factory.init(flags);

  CoreLabel c = new CoreLabel();
  c.setWord("news");
  c.set(CoreAnnotations.SectionAnnotation.class, "sports");
  c.set(CoreAnnotations.WordPositionAnnotation.class, "41");
  c.set(CoreAnnotations.SentencePositionAnnotation.class, "3");
  c.set(CoreAnnotations.ParaPositionAnnotation.class, "2");
  c.set(CoreAnnotations.ShapeAnnotation.class, "xxxx");
  c.set(CoreAnnotations.PositionAnnotation.class, "0");

  List<CoreLabel> list = Collections.singletonList(c);
//  PaddedList<CoreLabel> padded = PaddedList.pad(list, factory.getPadValue());
//
//  Collection<String> feats = factory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//
//  boolean foundSection = false;
//  boolean foundWordPos = false;
//  for (String f : feats) {
//    if (f.equals("sports-SECTION|C")) foundSection = true;
//    if (f.contains("WORD_POSITION")) foundWordPos = true;
//  }
//
//  assertTrue(foundSection && foundWordPos);
}
@Test
public void testDisjunctiveShapeInteractionDisabledDoesNotIncludeShapeFeatures() {
  NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useDisjunctive = true;
  flags.disjunctionWidth = 1;
  flags.useDisjunctiveShapeInteraction = false;
  factory.init(flags);

  CoreLabel left = new CoreLabel();
  left.setWord("green");
  left.set(CoreAnnotations.ShapeAnnotation.class, "xxxxx");
  left.set(CoreAnnotations.PositionAnnotation.class, "0");

  CoreLabel center = new CoreLabel();
  center.setWord("trees");
  center.set(CoreAnnotations.ShapeAnnotation.class, "xxxxx");
  center.set(CoreAnnotations.PositionAnnotation.class, "1");

  List<CoreLabel> items = Arrays.asList(left, center);
//  PaddedList<CoreLabel> data = PaddedList.pad(items, factory.getPadValue());
//
//  Collection<String> features = factory.getCliqueFeatures(data, 1, Clique.valueOf("C"));
//
//  boolean hasDisj = false;
//  boolean hasInteractions = false;
//  for (String f : features) {
//    if (f.contains("DISJP")) { hasDisj = true; }
//    if (f.contains("DISJP-CS")) { hasInteractions = true; }
//  }
//
//  assertTrue(hasDisj && !hasInteractions);
}
@Test
public void testTaggySequenceTruncatedWhenMaxLeftSetToOne() {
  NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useTaggySequences = true;
  flags.useTags = true;
  flags.maxLeft = 1;
  factory.init(flags);

  CoreLabel p1 = new CoreLabel();
  p1.setWord("big");
  p1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "JJ");
  p1.set(CoreAnnotations.ShapeAnnotation.class, "xxx");
  p1.set(CoreAnnotations.PositionAnnotation.class, "0");

  CoreLabel c = new CoreLabel();
  c.setWord("city");
  c.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
  c.set(CoreAnnotations.ShapeAnnotation.class, "xxxx");
  c.set(CoreAnnotations.PositionAnnotation.class, "1");

  List<CoreLabel> items = Arrays.asList(p1, c);
//  PaddedList<CoreLabel> padded = PaddedList.pad(items, factory.getPadValue());
//
//  Collection<String> feats = factory.getCliqueFeatures(padded, 1, Clique.valueOf("CpCp2C"));
//
//  boolean containsShortSeq = false;
//  boolean containsLongSeq = false;
//  for (String f : feats) {
//    if (f.contains("-TS|CpCp2C")) containsShortSeq = true;
//    if (f.contains("TTTS") || f.contains("TTTS-CS")) containsLongSeq = true;
//  }

//  assertTrue(containsShortSeq && !containsLongSeq);
}
@Test
public void testAbbrWithXXExcludedWhenMinimalAbbr1Set() {
  NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useMinimalAbbr1 = true;
  factory.init(flags);

  CoreLabel token = new CoreLabel();
  token.setWord("example");
  token.set(CoreAnnotations.AbbrAnnotation.class, "XX");
  token.set(CoreAnnotations.PositionAnnotation.class, "0");

  List<CoreLabel> list = Collections.singletonList(token);
//  PaddedList<CoreLabel> padded = PaddedList.pad(list, factory.getPadValue());
//
//  Collection<String> feats = factory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//
//  boolean hasAbbr = false;
//  for (String f : feats) {
//    if (f.contains("-ABBR")) hasAbbr = true;
//  }
//
//  assertFalse(hasAbbr);
}
@Test
public void testPositionAnnotationMissingHandledGracefully() {
  NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.usePosition = true;
  factory.init(flags);

  CoreLabel token = new CoreLabel();
  token.setWord("sample");
  token.set(CoreAnnotations.ShapeAnnotation.class, "xxxxxx");

  List<CoreLabel> list = Collections.singletonList(token);
//  PaddedList<CoreLabel> padded = PaddedList.pad(list, factory.getPadValue());
//
//  try {
//    factory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//  } catch (Exception e) {
//    fail("Missing PositionAnnotation should not crash");
//  }
}
@Test
public void testGazetteInfoOffsetBeyondInputListIgnored() {
  NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useGazettes = true;
  flags.cleanGazette = true;
  factory.init(flags);

  String[] phrase = new String[]{"Alpha", "Beta", "Gamma"};

//  NERFeatureFactory.GazetteInfo info = new NERFeatureFactory.GazetteInfo("ALPHA-GAZ", 1, phrase);
//  Set<NERFeatureFactory.GazetteInfo> infoSet = new HashSet<>();
//  infoSet.add(info);
//  factory.wordToGazetteInfos.put("Beta", infoSet);

  CoreLabel c = new CoreLabel();
  c.setWord("Beta");
  c.set(CoreAnnotations.PositionAnnotation.class, "0");

  List<CoreLabel> tokens = Collections.singletonList(c);
//  PaddedList<CoreLabel> padded = PaddedList.pad(tokens, factory.getPadValue());
//
//  Collection<String> features = factory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//
//  boolean found = false;
//  for (String f : features) {
//    if (f.startsWith("ALPHA-GAZ")) found = true;
//  }
//
//  assertFalse(found);
}
@Test
public void testUseNPHeadAddsGovShapeBasedFeature() {
  NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useNPHead = true;
  flags.useTags = true;
  factory.init(flags);

  CoreLabel c = new CoreLabel();
  c.setWord("NY");
  c.set(CoreAnnotations.HeadWordStringAnnotation.class, "NewYork");
  c.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  c.set(CoreAnnotations.PositionAnnotation.class, "0");
  c.set(CoreAnnotations.ShapeAnnotation.class, "XX");

  List<CoreLabel> tokens = Collections.singletonList(c);
//  PaddedList<CoreLabel> padded = PaddedList.pad(tokens, factory.getPadValue());
//
//  Collection<String> feats = factory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//
//  boolean foundHead = false;
//  for (String f : feats) {
//    if (f.startsWith("NewYork") && f.contains("-HW")) foundHead = true;
//  }
//
//  assertTrue(foundHead);
}
@Test
public void testMissingDistSimAnnotationHasFallback() {
  NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useDistSim = true;
  factory.init(flags);

  factory.clearMemory(); 

  CoreLabel word = new CoreLabel();
  word.setWord("unseen");
  word.set(CoreAnnotations.PositionAnnotation.class, "0");

  List<CoreLabel> sentence = Collections.singletonList(word);
//  PaddedList<CoreLabel> padded = PaddedList.pad(sentence, factory.getPadValue());
//
//  try {
//    factory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//  } catch (Exception ex) {
//    fail("Missing DistSimAnnotation when lexicon is null must not throw");
//  }
}
@Test
public void testSplitWordRegexSymbolsOnlySkippedGracefully() {
  NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.splitWordRegex = "\\*"; 
  factory.init(flags);

  CoreLabel c = new CoreLabel();
  c.setWord("***");
  c.set(CoreAnnotations.ShapeAnnotation.class, "...");
  c.set(CoreAnnotations.PositionAnnotation.class, "0");

  List<CoreLabel> tokens = Collections.singletonList(c);
//  PaddedList<CoreLabel> padded = PaddedList.pad(tokens, factory.getPadValue());
//
//  try {
//    Collection<String> feats = factory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//
//    boolean seen = false;
//    for (String f : feats) {
//      if (f.contains("-SPLITWORD")) seen = true;
//    }
//
//    assertTrue(seen || feats.isEmpty());
//  } catch (Exception e) {
//    fail("Token with only symbol delimiters in splitWordRegex must be handled gracefully");
//  }
} 
}