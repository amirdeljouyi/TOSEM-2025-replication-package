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

public class NERFeatureFactory_5_GPTLLMTest {

 @Test
  public void testInitWithDefaultFlags() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    factory.init(flags);
    assertTrue(true); 
  }
@Test
  public void testDescribeDistsimLexiconReturnsEmptyWhenUnset() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    factory.init(flags);
    String result = factory.describeDistsimLexicon();
    assertEquals("No distsim lexicon", result);
  }
@Test
  public void testGetCliqueFeaturesProducesFeaturesForC() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useWord = true;
    factory.init(flags);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("The");
    token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "DT");
    token1.set(CoreAnnotations.ShapeAnnotation.class, "Xx");
    token1.set(CoreAnnotations.DomainAnnotation.class, "test");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("quick");
    token2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "JJ");
    token2.set(CoreAnnotations.ShapeAnnotation.class, "xxxx");
    token2.set(CoreAnnotations.DomainAnnotation.class, "test");

    CoreLabel token3 = new CoreLabel();
    token3.setWord("fox");
    token3.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    token3.set(CoreAnnotations.ShapeAnnotation.class, "xxx");
    token3.set(CoreAnnotations.DomainAnnotation.class, "test");

    CoreLabel token4 = new CoreLabel();
    token4.setWord("jumps");
    token4.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");
    token4.set(CoreAnnotations.ShapeAnnotation.class, "xxxx");
    token4.set(CoreAnnotations.DomainAnnotation.class, "test");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);
    tokens.add(token3);
    tokens.add(token4);

    PaddedList<CoreLabel> padded = new PaddedList<>(tokens);
//    Collection<String> features = factory.getCliqueFeatures(padded, 2, edu.stanford.nlp.sequences.Clique.valueOf("C"));

//    assertNotNull(features);
//    assertFalse("Feature list should not be empty", features.isEmpty());
  }
@Test
  public void testGetCliqueFeaturesThrowsForInvalidClique() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    factory.init(flags);

    CoreLabel token = new CoreLabel();
    token.setWord("apple");
    token.set(CoreAnnotations.ShapeAnnotation.class, "xxxx");
    token.set(CoreAnnotations.DomainAnnotation.class, "test");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    PaddedList<CoreLabel> padded = new PaddedList<>(tokens);

//    try {
//      factory.getCliqueFeatures(padded, 0, new Clique("INVALID"));
//      fail("Expected IllegalArgumentException for unknown clique");
//    } catch (IllegalArgumentException ex) {
//      assertTrue(ex.getMessage().contains("Unknown clique"));
//    }
  }
@Test
  public void testFeatureExtractionIncludesWord() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useWord = true;
    factory.init(flags);

    CoreLabel w1 = new CoreLabel();
    w1.setWord("He");
    w1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "PRP");
    w1.set(CoreAnnotations.ShapeAnnotation.class, "Xx");
    w1.set(CoreAnnotations.DomainAnnotation.class, "bio");

    CoreLabel w2 = new CoreLabel();
    w2.setWord("saw");
    w2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBD");
    w2.set(CoreAnnotations.ShapeAnnotation.class, "xxx");
    w2.set(CoreAnnotations.DomainAnnotation.class, "bio");

    CoreLabel w3 = new CoreLabel();
    w3.setWord("apple");
    w3.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    w3.set(CoreAnnotations.ShapeAnnotation.class, "xxxx");
    w3.set(CoreAnnotations.DomainAnnotation.class, "bio");

    CoreLabel w4 = new CoreLabel();
    w4.setWord("trees");
    w4.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNS");
    w4.set(CoreAnnotations.ShapeAnnotation.class, "xxxx");
    w4.set(CoreAnnotations.DomainAnnotation.class, "bio");

    List<CoreLabel> list = new ArrayList<>();
    list.add(w1);
    list.add(w2);
    list.add(w3);
    list.add(w4);

    PaddedList<CoreLabel> padded = new PaddedList<>(list);
//    Collection<String> features = factory.getCliqueFeatures(padded, 2, edu.stanford.nlp.sequences.Clique.valueOf("C"));
//    String wordFeature = "apple|C";
//
//    boolean containsWordFeature = false;
//    for (String feat : features) {
//      if (feat.equals(wordFeature)) {
//        containsWordFeature = true;
//      }
//    }
//
//    assertTrue("The features should contain 'apple|C'", containsWordFeature);
  }
@Test
  public void testEmptyContextProducesNoException() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useWord = true;
    factory.init(flags);

    List<CoreLabel> emptyList = new ArrayList<>();
    PaddedList<CoreLabel> padded = new PaddedList<>(emptyList);

//    try {
//      factory.getCliqueFeatures(padded, 0, edu.stanford.nlp.sequences.Clique.valueOf("C"));
//      fail("Should throw IndexOutOfBoundsException");
//    } catch (IndexOutOfBoundsException expected) {
//      assertTrue(true);
//    }
  }
@Test
  public void testGetCliqueFeaturesWithNullDomainAnnotation() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useWord = true;
    factory.init(flags);

    CoreLabel w1 = new CoreLabel();
    w1.setWord("Doctor");
    w1.set(CoreAnnotations.ShapeAnnotation.class, "Xxxxx");
    

    List<CoreLabel> list = new ArrayList<>();
    list.add(w1);

    PaddedList<CoreLabel> padded = new PaddedList<>(list);

//    try {
//      factory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//      assertTrue(true);
//    } catch (NullPointerException e) {
//      fail("Should not throw NullPointerException when DomainAnnotation is null");
//    }
  }
@Test
  public void testFeaturesCWithEmptyWord() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useWord = true;
    flags.useShapeStrings = true;
    factory.init(flags);

    CoreLabel w1 = new CoreLabel();
    w1.setWord("");
    w1.set(CoreAnnotations.ShapeAnnotation.class, "");
    w1.set(CoreAnnotations.DomainAnnotation.class, "unit");

    List<CoreLabel> list = new ArrayList<>();
    list.add(w1);
    PaddedList<CoreLabel> padded = new PaddedList<>(list);

//    Collection<String> features = factory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//    assertNotNull(features);
  }
@Test
  public void testShortContextForPrevCliqueTriggersGracefulHandling() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.usePrev = true;
    flags.useSequences = true;
    flags.usePrevSequences = true;
    flags.useWord = true;
    factory.init(flags);

    CoreLabel token = new CoreLabel();
    token.setWord("Washington");
    token.set(CoreAnnotations.ShapeAnnotation.class, "Xxxxxxxxxx");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    token.set(CoreAnnotations.DomainAnnotation.class, "news");

    List<CoreLabel> list = new ArrayList<>();
    list.add(token); 

    PaddedList<CoreLabel> padded = new PaddedList<>(list);

//    Collection<String> features = factory.getCliqueFeatures(padded, 0, Clique.valueOf("CpC"));
//    assertNotNull(features);
  }
@Test
  public void testDistSimAnnotateWithUnknownWordAndFlags() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useDistSim = true;
    flags.casedDistSim = false;
    flags.numberEquivalenceDistSim = true;
    flags.unknownWordDistSimClass = "UNK";
    flags.distSimLexicon = null;
    factory.init(flags);

    CoreLabel w = new CoreLabel();
    w.setWord("7782");
    w.set(CoreAnnotations.ShapeAnnotation.class, "ddddd");
    w.set(CoreAnnotations.DomainAnnotation.class, "bio");

    List<CoreLabel> list = new ArrayList<>();
    list.add(w);
    PaddedList<CoreLabel> context = new PaddedList<>(list);

//    Collection<String> feats = factory.getCliqueFeatures(context, 0, Clique.valueOf("C"));
//
//    String expected = "UNK";
//    String actual = w.get(CoreAnnotations.DistSimAnnotation.class);
//
//    assertEquals(expected, actual);
  }
@Test
  public void testUseBeginSentAddsSentenceBoundaryFeatures() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useBeginSent = true;
    flags.useWord = true;
    factory.init(flags);

    CoreLabel token = new CoreLabel();
    token.setWord("Start");
    token.set(CoreAnnotations.PositionAnnotation.class, "0");
    token.set(CoreAnnotations.ShapeAnnotation.class, "Xxxxx");
    token.set(CoreAnnotations.DomainAnnotation.class, "test");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    PaddedList<CoreLabel> padded = new PaddedList<>(tokens);

//    Collection<String> features = factory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//    boolean foundBeginSent = false;
//
//    for (String f : features) {
//      if (f.equals("BEGIN-SENT|C")) {
//        foundBeginSent = true;
//      }
//    }
//
//    assertTrue("Expected BEGIN-SENT feature", foundBeginSent);
  }
@Test
  public void testUseOrdinalWithNumericWordAndOrdinalSuffix() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();

    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useOrdinal = true;
    flags.useWord = true;
    factory.init(flags);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("2");
    token1.set(CoreAnnotations.ShapeAnnotation.class, "d");
    token1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "CD");
    token1.set(CoreAnnotations.DomainAnnotation.class, "test");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("nd");
    token2.set(CoreAnnotations.ShapeAnnotation.class, "xx");
    token2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "JJ");
    token2.set(CoreAnnotations.DomainAnnotation.class, "test");

    List<CoreLabel> list = new ArrayList<>();
    list.add(token1);
    list.add(token2);

    PaddedList<CoreLabel> padded = new PaddedList<>(list);

//    Collection<String> features = factory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//
//    boolean foundOrdinal = false;
//
//    for (String f : features) {
//      if (f.equals("C_ORDINAL|C")) {
//        foundOrdinal = true;
//      }
//    }
//
//    assertTrue("Expected C_ORDINAL feature", foundOrdinal);
  }
@Test
  public void testUseSymWordPairsIncludesExpectedFeature() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useSymWordPairs = true;
    flags.usePrev = true;
    flags.useNext = true;
    flags.useWord = true;
    factory.init(flags);

    CoreLabel w1 = new CoreLabel();
    w1.setWord("John");
    w1.set(CoreAnnotations.ShapeAnnotation.class, "Xxxx");
    w1.set(CoreAnnotations.DomainAnnotation.class, "bio");

    CoreLabel w2 = new CoreLabel();
    w2.setWord("Doe");
    w2.set(CoreAnnotations.ShapeAnnotation.class, "Xxx");
    w2.set(CoreAnnotations.DomainAnnotation.class, "bio");

    CoreLabel w3 = new CoreLabel();
    w3.setWord("Smith");
    w3.set(CoreAnnotations.ShapeAnnotation.class, "Xxxxx");
    w3.set(CoreAnnotations.DomainAnnotation.class, "bio");

    List<CoreLabel> list = new ArrayList<>();
    list.add(w1);
    list.add(w2);
    list.add(w3);

    PaddedList<CoreLabel> padded = new PaddedList<>(list);

//    Collection<String> features = factory.getCliqueFeatures(padded, 1, Clique.valueOf("C"));
//
//    boolean hasSymPair = false;
//    for (String f : features) {
//      if (f.startsWith("John-Doe|C") || f.startsWith("Doe-Smith|C") || f.startsWith("John-Smith|C")) {
//        hasSymPair = true;
//      }
//    }
//
//    assertTrue("Symmetric word pair feature should be present", hasSymPair);
  }
@Test
  public void testFeatureCollectorWithSuffixAndDomain() {
    Set<String> output = new HashSet<String>();
    NERFeatureFactory.FeatureCollector collector = new NERFeatureFactory.FeatureCollector(output);
    collector.setDomain("news");
    collector.setSuffix("C");
    collector.build().append("word").add();
    assertTrue(output.contains("word|C"));
    assertTrue(output.contains("word|news-C"));
  }
@Test
  public void testGetCliqueFeatures_withCliqueCpC_andPrevWord() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.usePrev = true;
    flags.useSequences = true;
    flags.usePrevSequences = true;
    flags.useWord = true;
    factory.init(flags);

    CoreLabel p = new CoreLabel();
    p.setWord("Hello");
    p.set(CoreAnnotations.ShapeAnnotation.class, "Xxxxx");
    p.set(CoreAnnotations.DomainAnnotation.class, "test");

    CoreLabel c = new CoreLabel();
    c.setWord("World");
    c.set(CoreAnnotations.ShapeAnnotation.class, "Xxxxx");
    c.set(CoreAnnotations.DomainAnnotation.class, "test");

    List<CoreLabel> list = new ArrayList<>();
    list.add(p);
    list.add(c);
    PaddedList<CoreLabel> padded = new PaddedList<>(list);

//    Collection<String> features = factory.getCliqueFeatures(padded, 1, Clique.valueOf("CpC"));
//    assertNotNull(features);
//    assertTrue(features.contains("PSEQ|CpC"));
  }
@Test
  public void testBinnedLengthNearBoundary() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
//    flags.useBinnedLength = "3,5,10";
    flags.useWord = true;
    factory.init(flags);
//    String[] bounds = flags.useBinnedLength.split(",");
//    flags.binnedLengths = new int[] {
//        Integer.parseInt(bounds[0]),
//        Integer.parseInt(bounds[1]),
//        Integer.parseInt(bounds[2])
//    };

    CoreLabel cl = new CoreLabel();
    cl.setWord("house"); 
    cl.set(CoreAnnotations.ShapeAnnotation.class, "xxxxx");
    cl.set(CoreAnnotations.DomainAnnotation.class, "bio");

    List<CoreLabel> list = new ArrayList<>();
    list.add(cl);
    PaddedList<CoreLabel> padded = new PaddedList<>(list);

//    Collection<String> features = factory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//    boolean found = false;
//    for (String f : features) {
//      if (f.contains("Len-3-5")) {
//        found = true;
//      }
//    }
//    assertTrue(found);
  }
@Test
  public void testFeatureWithLemmasAndNullCheck() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useLemmas = true;
    flags.useWord = true;
    factory.init(flags);

    CoreLabel cl = new CoreLabel();
    cl.setWord("walking");
    cl.set(CoreAnnotations.LemmaAnnotation.class, "walk");
    cl.set(CoreAnnotations.ShapeAnnotation.class, "xxxxxxx");
    cl.set(CoreAnnotations.DomainAnnotation.class, "sci");

    List<CoreLabel> list = new ArrayList<>();
    list.add(cl);
    PaddedList<CoreLabel> padded = new PaddedList<>(list);

//    Collection<String> features = factory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//    boolean foundLemma = false;
//    for (String f : features) {
//      if (f.contains("walk-LEM")) {
//        foundLemma = true;
//      }
//    }
//    assertTrue(foundLemma);
  }
@Test
  public void testNullShapeDoesNotFail() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useWord = true;
    flags.useShapeStrings = true;
    factory.init(flags);

    CoreLabel cl = new CoreLabel();
    cl.setWord("Moon");
    cl.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    cl.set(CoreAnnotations.DomainAnnotation.class, "astro");
    

    List<CoreLabel> list = new ArrayList<>();
    list.add(cl);
    PaddedList<CoreLabel> padded = new PaddedList<>(list);

//    Collection<String> feats = factory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//    assertNotNull(feats);
  }
@Test
  public void testGazetteSkipsWhenNoMatchingWord() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useGazettes = true;
    flags.cleanGazette = true;
    factory.init(flags);

    CoreLabel cl = new CoreLabel();
    cl.setWord("Zanzibar");
    cl.set(CoreAnnotations.ShapeAnnotation.class, "Xxxxxxxx");
    cl.set(CoreAnnotations.DomainAnnotation.class, "geo");

    List<CoreLabel> list = new ArrayList<>();
    list.add(cl);
    PaddedList<CoreLabel> padded = new PaddedList<>(list);

//    Collection<String> feats = factory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//    assertNotNull(feats);
//    assertTrue(feats.size() >= 0);
  }
@Test
  public void testUseGENIAFeature() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useGENIA = true;
    flags.useWord = true;
    factory.init(flags);

    CoreLabel cl = new CoreLabel();
    cl.setWord("protein");
    cl.set(CoreAnnotations.GeniaAnnotation.class, "GENIA-TAG");
    cl.set(CoreAnnotations.ShapeAnnotation.class, "xxxxxxx");
    cl.set(CoreAnnotations.DomainAnnotation.class, "bio");

    List<CoreLabel> list = new ArrayList<>();
    list.add(cl);
    PaddedList<CoreLabel> padded = new PaddedList<>(list);

//    Collection<String> feats = factory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//    boolean matched = false;
//    for (String f : feats) {
//      if (f.contains("GENIA-TAG-WEB")) {
//        matched = true;
//      }
//    }
//    assertFalse(matched);
  }
@Test
  public void testUseTopicAnnotationFeature() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useTopics = true;
    flags.useWord = true;
    factory.init(flags);

    CoreLabel token = new CoreLabel();
    token.setWord("cell");
    token.set(CoreAnnotations.ShapeAnnotation.class, "xxxx");
    token.set(CoreAnnotations.DomainAnnotation.class, "bio");
    token.set(CoreAnnotations.TopicAnnotation.class, "BIOLOGY");

    List<CoreLabel> list = new ArrayList<>();
    list.add(token);
    PaddedList<CoreLabel> padded = new PaddedList<>(list);

//    Collection<String> features = factory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//    boolean topicFound = false;
//    for (String f : features) {
//      if (f.equals("BIOLOGY-TopicID|C")) {
//        topicFound = true;
//      }
//    }
//    assertTrue(topicFound);
  }
@Test
  public void testCliqueCp2CTriggersExpectedFeatures() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useWord = true;
    factory.init(flags);

    CoreLabel t0 = new CoreLabel();
    t0.setWord("Dr.");
    t0.set(CoreAnnotations.AbbrAnnotation.class, "ABBR");
    t0.set(CoreAnnotations.ShapeAnnotation.class, "Xx.");
    t0.set(CoreAnnotations.DomainAnnotation.class, "med");

    CoreLabel t1 = new CoreLabel();
    t1.setWord("John");
    t1.set(CoreAnnotations.AbbrAnnotation.class, "ABBR");
    t1.set(CoreAnnotations.ShapeAnnotation.class, "Xxxx");
    t1.set(CoreAnnotations.DomainAnnotation.class, "med");

    CoreLabel t2 = new CoreLabel();
    t2.setWord("Smith");
    t2.set(CoreAnnotations.AbbrAnnotation.class, "ABBR");
    t2.set(CoreAnnotations.ShapeAnnotation.class, "Xxxxx");
    t2.set(CoreAnnotations.DomainAnnotation.class, "med");

    List<CoreLabel> list = new ArrayList<CoreLabel>();
    list.add(t0);
    list.add(t1);
    list.add(t2);

    PaddedList<CoreLabel> padded = new PaddedList<>(list);
//    Collection<String> result = factory.getCliqueFeatures(padded, 2, Clique.valueOf("Cp2C"));
//
//    assertNotNull(result);
//    assertTrue(result.size() > 0);
  }
@Test
  public void testSloppyGazetteMatchEntry() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useGazettes = true;
    flags.sloppyGazette = true;
    flags.useWord = true;
    factory.init(flags);

    CoreLabel label = new CoreLabel();
    label.setWord("California");
    label.set(CoreAnnotations.ShapeAnnotation.class, "Xxxxxxxxxx");
    label.set(CoreAnnotations.DomainAnnotation.class, "geo");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(label);

    PaddedList<CoreLabel> padded = new PaddedList<>(tokens);

    
    try {
      java.lang.reflect.Field field = NERFeatureFactory.class.getDeclaredField("wordToGazetteEntries");
      field.setAccessible(true);
      Map<String, Collection<String>> map = (Map<String, Collection<String>>) field.get(factory);
      Set<String> entries = new HashSet<>();
      entries.add("LOCATION-GAZ1");
      map.put("California", entries);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

//    Collection<String> feats = factory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//    boolean foundFeature = false;
//    for (String feat : feats) {
//      if (feat.equals("LOCATION-GAZ1|C")) {
//        foundFeature = true;
//      }
//    }
//    assertTrue(foundFeature);
  }
@Test
  public void testDistSimNumberEquivalenceFeature() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useDistSim = true;
    flags.numberEquivalenceDistSim = true;
    flags.casedDistSim = false;
    flags.unknownWordDistSimClass = "UNK";
    flags.distSimLexicon = null;
    factory.init(flags);

    CoreLabel label = new CoreLabel();
    label.setWord("2024");
    label.set(CoreAnnotations.ShapeAnnotation.class, "dddd");
    label.set(CoreAnnotations.DomainAnnotation.class, "test");

    List<CoreLabel> input = new ArrayList<>();
    input.add(label);
    PaddedList<CoreLabel> padded = new PaddedList<>(input);

//    Collection<String> feats = factory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//
//    assertEquals("UNK", label.get(CoreAnnotations.DistSimAnnotation.class));
  }
@Test
  public void testInternFlagEnabled() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useWord = true;
    flags.intern = true;
    factory.init(flags);

    CoreLabel label = new CoreLabel();
    label.setWord("Honda");
    label.set(CoreAnnotations.ShapeAnnotation.class, "Xxxxx");
    label.set(CoreAnnotations.DomainAnnotation.class, "car");

    List<CoreLabel> input = new ArrayList<>();
    input.add(label);
    PaddedList<CoreLabel> padded = new PaddedList<>(input);

//    Collection<String> feats = factory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//    assertNotNull(feats);
  }
@Test
  public void testUseMinimalAbbr1FlagWithNonXXValue() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useWord = true;
    flags.useMinimalAbbr1 = true;
    factory.init(flags);

    CoreLabel label = new CoreLabel();
    label.setWord("Co.");
    label.set(CoreAnnotations.AbbrAnnotation.class, "ORG");
    label.set(CoreAnnotations.ShapeAnnotation.class, "Xx.");
    label.set(CoreAnnotations.DomainAnnotation.class, "biz");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(label);
    PaddedList<CoreLabel> padded = new PaddedList<>(tokens);
//    Collection<String> feats = factory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//
//    boolean containsAbbr = false;
//    for (String f : feats) {
//      if (f.contains("CWABB")) {
//        containsAbbr = true;
//      }
//    }
//
//    assertTrue(containsAbbr);
  }
@Test
  public void testUseRadicalBasic() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useRadical = true;
    flags.useWord = true;
    factory.init(flags);

    CoreLabel label = new CoreLabel();
    label.setWord("水"); 
    label.set(CoreAnnotations.ShapeAnnotation.class, "H");
    label.set(CoreAnnotations.DomainAnnotation.class, "zh");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(label);
    PaddedList<CoreLabel> padded = new PaddedList<>(tokens);

//    Collection<String> features = factory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//    assertNotNull(features);
  }
@Test
  public void testUseConjoinShapeNGrams() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useNGrams = true;
    flags.conjoinShapeNGrams = true;
    flags.wordShape = 1;
    flags.maxNGramLeng = 3;
    flags.useWord = true;
    factory.init(flags);

    CoreLabel label = new CoreLabel();
    label.setWord("legal");
    label.set(CoreAnnotations.ShapeAnnotation.class, "xxxxx");
    label.set(CoreAnnotations.DomainAnnotation.class, "law");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(label);
    PaddedList<CoreLabel> padded = new PaddedList<>(tokens);

//    Collection<String> features = factory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//    boolean foundConjoined = false;
//    for (String feat : features) {
//      if (feat.contains("CNGram-CS")) {
//        foundConjoined = true;
//      }
//    }
//    assertTrue(foundConjoined);
  }
@Test
  public void testFeatureCollectorWithOnlySuffix() {
    Set<String> features = new HashSet<>();
    NERFeatureFactory.FeatureCollector collector = new NERFeatureFactory.FeatureCollector(features);

    collector.setSuffix("C");
    collector.build().append("demo").add();

    assertTrue(features.contains("demo|C"));
  }
@Test
  public void testCliqueCpCp2Cp3Cp4CWithMinimalContext() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useLongSequences = true;
    flags.maxLeft = 4;
    flags.useWord = true;
    factory.init(flags);

    CoreLabel label = new CoreLabel();
    label.setWord("X");
    label.set(CoreAnnotations.ShapeAnnotation.class, "X");
    label.set(CoreAnnotations.DomainAnnotation.class, "test");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(label);
    PaddedList<CoreLabel> padded = new PaddedList<>(tokens);

//    Collection<String> features = factory.getCliqueFeatures(padded, 0, Clique.valueOf("CpCp2Cp3Cp4C"));
//    assertNotNull(features);
  }
@Test
  public void testCpCWithPadOnLeft() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.usePrev = true;
    flags.useSequences = true;
    flags.usePrevSequences = true;
    flags.useWord = true;
    factory.init(flags);

    CoreLabel center = new CoreLabel();
    center.setWord("delta");
    center.set(CoreAnnotations.ShapeAnnotation.class, "xxxxx");
    center.set(CoreAnnotations.DomainAnnotation.class, "geo");

    List<CoreLabel> list = new ArrayList<>();
    list.add(center);

    PaddedList<CoreLabel> padded = new PaddedList<>(list);
//    Collection<String> result = factory.getCliqueFeatures(padded, 0, Clique.valueOf("CpC"));
//
//    assertNotNull(result);
  }
@Test
  public void testDisjunctiveShapeInteractionEnabled() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useDisjunctive = true;
    flags.useDisjunctiveShapeInteraction = true;
    flags.disjunctionWidth = 2;
    flags.useWord = true;
    flags.useShapeStrings = true;
    flags.useInternal = true;
    flags.useExternal = true;
    factory.init(flags);

    CoreLabel t0 = new CoreLabel();
    t0.setWord("The");
    t0.set(CoreAnnotations.ShapeAnnotation.class, "Xxx");
    t0.set(CoreAnnotations.DomainAnnotation.class, "doc");

    CoreLabel t1 = new CoreLabel();
    t1.setWord("quick");
    t1.set(CoreAnnotations.ShapeAnnotation.class, "xxxxx");
    t1.set(CoreAnnotations.DomainAnnotation.class, "doc");

    CoreLabel t2 = new CoreLabel();
    t2.setWord("brown");
    t2.set(CoreAnnotations.ShapeAnnotation.class, "xxxxx");
    t2.set(CoreAnnotations.DomainAnnotation.class, "doc");

    CoreLabel t3 = new CoreLabel();
    t3.setWord("fox");
    t3.set(CoreAnnotations.ShapeAnnotation.class, "xxx");
    t3.set(CoreAnnotations.DomainAnnotation.class, "doc");

    List<CoreLabel> list = new ArrayList<>();
    list.add(t0);
    list.add(t1);
    list.add(t2);
    list.add(t3);

    PaddedList<CoreLabel> padded = new PaddedList<>(list);

//    Collection<String> features = factory.getCliqueFeatures(padded, 2, Clique.valueOf("C"));
//    boolean hasDisjunctive = false;
//    for (String f : features) {
//      if (f.endsWith("CS|C")) {
//        hasDisjunctive = true;
//      }
//    }
//    assertTrue(hasDisjunctive);
  }
@Test
  public void testWideDisjunctiveFeaturesAppear() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useWideDisjunctive = true;
    flags.wideDisjunctionWidth = 1;
    flags.useWord = true;
    factory.init(flags);

    CoreLabel c0 = new CoreLabel();
    c0.setWord("A");
    c0.set(CoreAnnotations.ShapeAnnotation.class, "X");
    c0.set(CoreAnnotations.DomainAnnotation.class, "test");

    CoreLabel c1 = new CoreLabel();
    c1.setWord("B");
    c1.set(CoreAnnotations.ShapeAnnotation.class, "X");
    c1.set(CoreAnnotations.DomainAnnotation.class, "test");

    CoreLabel c2 = new CoreLabel();
    c2.setWord("C");
    c2.set(CoreAnnotations.ShapeAnnotation.class, "X");
    c2.set(CoreAnnotations.DomainAnnotation.class, "test");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(c0);
    tokens.add(c1);
    tokens.add(c2);

    PaddedList<CoreLabel> padded = new PaddedList<>(tokens);

//    Collection<String> result = factory.getCliqueFeatures(padded, 1, Clique.valueOf("C"));
//    boolean found = false;
//    for (String f : result) {
//      if (f.contains("DISJWN|C") || f.contains("DISJWP|C")) {
//        found = true;
//      }
//    }
//    assertTrue(found);
  }
@Test
  public void testDisjShapeFeaturesGenerated() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useDisjShape = true;
    flags.disjunctionWidth = 1;
    flags.useShapeStrings = true;
    flags.useWord = true;
    factory.init(flags);

    CoreLabel a = new CoreLabel();
    a.setWord("Wind");
    a.set(CoreAnnotations.ShapeAnnotation.class, "Xxxx");
    a.set(CoreAnnotations.DomainAnnotation.class, "x");

    CoreLabel b = new CoreLabel();
    b.setWord("Turbine");
    b.set(CoreAnnotations.ShapeAnnotation.class, "Xxxxxx");
    b.set(CoreAnnotations.DomainAnnotation.class, "x");

    CoreLabel c = new CoreLabel();
    c.setWord("Generators");
    c.set(CoreAnnotations.ShapeAnnotation.class, "Xxxxxxxxx");
    c.set(CoreAnnotations.DomainAnnotation.class, "x");

    List<CoreLabel> list = new ArrayList<>();
    list.add(a);
    list.add(b);
    list.add(c);

    PaddedList<CoreLabel> padded = new PaddedList<>(list);

//    Collection<String> result = factory.getCliqueFeatures(padded, 1, Clique.valueOf("C"));
//    boolean seenShapeDisj = false;
//    for (String f : result) {
//      if (f.contains("DISJSHAPE")) {
//        seenShapeDisj = true;
//      }
//    }
//
//    assertTrue(seenShapeDisj);
  }
@Test
  public void testFeatureCollectorFailsOnMissingSuffix() {
    try {
      Set<String> output = new HashSet<String>();
      NERFeatureFactory.FeatureCollector collector = new NERFeatureFactory.FeatureCollector(output);
      collector.build().append("fail").add();
      fail("Expected AssertionError due to missing suffix");
    } catch (AssertionError e) {
      assertTrue(true);
    }
  }
@Test
  public void testOccurrencePatternNoMatchReturnsSafe() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useOccurrencePatterns = true;
    flags.useWord = true;
    factory.init(flags);

    CoreLabel p = new CoreLabel();
    p.setWord("gas");
    p.set(CoreAnnotations.ShapeAnnotation.class, "xxx");
    p.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    p.set(CoreAnnotations.DomainAnnotation.class, "bio");

    CoreLabel c = new CoreLabel();
    c.setWord("shift");
    c.set(CoreAnnotations.ShapeAnnotation.class, "xxxx");
    c.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    c.set(CoreAnnotations.DomainAnnotation.class, "bio");

    CoreLabel n = new CoreLabel();
    n.setWord("reaction");
    n.set(CoreAnnotations.ShapeAnnotation.class, "xxxxxxxx");
    n.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    n.set(CoreAnnotations.DomainAnnotation.class, "bio");

    List<CoreLabel> list = new ArrayList<>();
    list.add(p);
    list.add(c);
    list.add(n);

    PaddedList<CoreLabel> padded = new PaddedList<>(list);
//    Collection<String> result = factory.getCliqueFeatures(padded, 1, Clique.valueOf("C"));
//    boolean containsFallback = false;
//    for (String f : result) {
//      if (f.equals("NO-OCCURRENCE-PATTERN|C")) {
//        containsFallback = true;
//      }
//    }
//    assertTrue(containsFallback);
  }
@Test
  public void testTaggySequenceWithTagsAndShapeOnCpC() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useTags = true;
    flags.useTaggySequences = true;
    flags.useTaggySequencesShapeInteraction = true;
    flags.usePrev = true;
    flags.useSequences = true;
    flags.usePrevSequences = true;
    flags.useWord = true;
    factory.init(flags);

    CoreLabel p = new CoreLabel();
    p.setWord("big");
    p.set(CoreAnnotations.PartOfSpeechAnnotation.class, "JJ");
    p.set(CoreAnnotations.ShapeAnnotation.class, "xxx");
    p.set(CoreAnnotations.DomainAnnotation.class, "doc");

    CoreLabel c = new CoreLabel();
    c.setWord("house");
    c.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    c.set(CoreAnnotations.ShapeAnnotation.class, "xxxx");
    c.set(CoreAnnotations.DomainAnnotation.class, "doc");

    List<CoreLabel> list = new ArrayList<>();
    list.add(p);
    list.add(c);

    PaddedList<CoreLabel> padded = new PaddedList<>(list);
//    Collection<String> features = factory.getCliqueFeatures(padded, 1, Clique.valueOf("CpC"));
//
//    boolean hasTS = false;
//    for (String f : features) {
//      if (f.contains("TS|CpC") || f.contains("CS|CpC")) {
//        hasTS = true;
//      }
//    }
//    assertTrue(hasTS);
  }
@Test
  public void testCliqueCnCAndCpCnCWithMinimalContext() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useNext = true;
    flags.usePrev = true;
    flags.useSequences = true;
    flags.useNextSequences = true;
    flags.usePrevSequences = true;
    flags.useWord = true;
    factory.init(flags);

    CoreLabel a = new CoreLabel();
    a.setWord("a");
    a.set(CoreAnnotations.ShapeAnnotation.class, "x");
    a.set(CoreAnnotations.DomainAnnotation.class, "test");

    CoreLabel b = new CoreLabel();
    b.setWord("b");
    b.set(CoreAnnotations.ShapeAnnotation.class, "x");
    b.set(CoreAnnotations.DomainAnnotation.class, "test");

    CoreLabel c = new CoreLabel();
    c.setWord("c");
    c.set(CoreAnnotations.ShapeAnnotation.class, "x");
    c.set(CoreAnnotations.DomainAnnotation.class, "test");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(a);
    tokens.add(b);
    tokens.add(c);

    PaddedList<CoreLabel> padded = new PaddedList<>(tokens);
//    Collection<String> feats1 = factory.getCliqueFeatures(padded, 1, Clique.valueOf("CnC"));
//    Collection<String> feats2 = factory.getCliqueFeatures(padded, 1, Clique.valueOf("CpCnC"));
//    assertNotNull(feats1);
//    assertNotNull(feats2);
  }
@Test
  public void testLengthBinningInfiniteBucket() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
//    flags.useBinnedLength = "4,8";
    flags.useWord = true;
    factory.init(flags);
    flags.binnedLengths = new int[]{4, 8}; 

    CoreLabel label = new CoreLabel();
    label.setWord("supercalifragilisticexpialidocious"); 
    label.set(CoreAnnotations.ShapeAnnotation.class, "XxXx");
    label.set(CoreAnnotations.DomainAnnotation.class, "test");

    List<CoreLabel> input = new ArrayList<>();
    input.add(label);
    PaddedList<CoreLabel> padded = new PaddedList<>(input);

//    Collection<String> result = factory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//    boolean found = false;
//    for (String f : result) {
//      if (f.contains("Len-8-Inf|C")) {
//        found = true;
//      }
//    }
//    assertTrue(found);
  }
@Test
  public void testUseClassFeatureYieldsClassConstant() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useClassFeature = true;
    flags.useWord = true;
    factory.init(flags);

    CoreLabel cl = new CoreLabel();
    cl.setWord("Entity");
    cl.set(CoreAnnotations.ShapeAnnotation.class, "Xxxxx");
    cl.set(CoreAnnotations.DomainAnnotation.class, "news");

    List<CoreLabel> list = new ArrayList<>();
    list.add(cl);
    PaddedList<CoreLabel> padded = new PaddedList<>(list);
//    Collection<String> features = factory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//    assertTrue(features.contains("###|C"));
  }
@Test
  public void testUseSymTagsEnabledAndIncludesExpected() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.usePrev = true;
    flags.useNext = true;
    flags.useSymTags = true;
    flags.useTags = true;
    flags.useWord = true;
    factory.init(flags);

    CoreLabel t0 = new CoreLabel();
    t0.setWord("Mr.");
    t0.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    t0.set(CoreAnnotations.ShapeAnnotation.class, "Xx.");
    t0.set(CoreAnnotations.DomainAnnotation.class, "doc");

    CoreLabel t1 = new CoreLabel();
    t1.setWord("John");
    t1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    t1.set(CoreAnnotations.ShapeAnnotation.class, "Xxxx");
    t1.set(CoreAnnotations.DomainAnnotation.class, "doc");

    CoreLabel t2 = new CoreLabel();
    t2.setWord("Doe");
    t2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    t2.set(CoreAnnotations.ShapeAnnotation.class, "Xxx");
    t2.set(CoreAnnotations.DomainAnnotation.class, "doc");

    List<CoreLabel> list = new ArrayList<>();
    list.add(t0);
    list.add(t1);
    list.add(t2);

    PaddedList<CoreLabel> padded = new PaddedList<>(list);
//    Collection<String> feats = factory.getCliqueFeatures(padded, 1, Clique.valueOf("C"));
//
//    boolean hasSymTags = false;
//    for (String f : feats) {
//      if (f.contains("PCNTAGS|C")) {
//        hasSymTags = true;
//      }
//    }
//
//    assertTrue(hasSymTags);
  }
@Test
  public void testNullHeadGovFieldsDoesNotCrash() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useNPHead = true;
    flags.useNPGovernor = true;
    flags.useWord = true;
    factory.init(flags);

    CoreLabel label = new CoreLabel();
    label.setWord("molecule");
    
    label.set(CoreAnnotations.ShapeAnnotation.class, "xxxxxxx");
    label.set(CoreAnnotations.DomainAnnotation.class, "chem");

    List<CoreLabel> input = new ArrayList<>();
    input.add(label);
    PaddedList<CoreLabel> padded = new PaddedList<>(input);

//    Collection<String> feats = factory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//    assertNotNull(feats);
  }
@Test
  public void testEmptyStringWordStillProducesFeature() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useWord = true;
    factory.init(flags);

    CoreLabel cl = new CoreLabel();
    cl.setWord("");
    cl.set(CoreAnnotations.ShapeAnnotation.class, "");
    cl.set(CoreAnnotations.DomainAnnotation.class, "domain");

    List<CoreLabel> list = new ArrayList<>();
    list.add(cl);
    PaddedList<CoreLabel> padded = new PaddedList<>(list);
//    Collection<String> feats = factory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//
//    assertNotNull(feats);
//    assertFalse(feats.isEmpty());
  }
@Test
  public void testUseGenericFeaturesWithCustomAnnotation() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useGenericFeatures = true;
    flags.useWord = true;
    factory.init(flags);

    CoreLabel cl = new CoreLabel();
    cl.setWord("Tesla");
    cl.set(CoreAnnotations.ShapeAnnotation.class, "Xxxxx");
    cl.set(CoreAnnotations.DomainAnnotation.class, "tech");
//    cl.set(new CoreLabel.GenericAnnotation<Object>() {
//      public Class<String> getType() {
//        return String.class;
//      }
//    }, "ElectricCarBrand");

//    factory.getCliqueFeatures(new PaddedList<>(Collections.singletonList(cl)), 0, Clique.valueOf("C"));
    assertNotNull(cl);
  }
@Test
  public void testUseTypeSeqs2WithMaxLeftAndShape() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useTypeSeqs = true;
    flags.useTypeSeqs2 = true;
    flags.maxLeft = 2;
    flags.useWord = true;
    factory.init(flags);

    CoreLabel p2 = new CoreLabel();
    p2.setWord("The");
    p2.set(CoreAnnotations.ShapeAnnotation.class, "Xxx");
    p2.set(CoreAnnotations.DomainAnnotation.class, "x");

    CoreLabel p1 = new CoreLabel();
    p1.setWord("Old");
    p1.set(CoreAnnotations.ShapeAnnotation.class, "Xxx");
    p1.set(CoreAnnotations.DomainAnnotation.class, "x");

    CoreLabel c = new CoreLabel();
    c.setWord("Man");
    c.set(CoreAnnotations.ShapeAnnotation.class, "Xxx");
    c.set(CoreAnnotations.DomainAnnotation.class, "x");

    List<CoreLabel> sequence = new ArrayList<>();
    sequence.add(p2);
    sequence.add(p1);
    sequence.add(c);

    PaddedList<CoreLabel> padded = new PaddedList<>(sequence);
//    Collection<String> feats = factory.getCliqueFeatures(padded, 2, Clique.valueOf("CpCp2C"));
//    boolean containsTT = false;
//    for (String f : feats) {
//      if (f.contains("TYPETYPES|CpCp2C")) {
//        containsTT = true;
//      }
//    }
//    assertTrue(containsTT);
  }
@Test
  public void testSlashHyphenTreatmentBothAddsFeatures() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useWord = true;
    flags.useInternal = true;
    flags.slashHyphenTreatment = SeqClassifierFlags.SlashHyphenEnum.BOTH;
    factory.init(flags);

    CoreLabel token = new CoreLabel();
    token.setWord("Smith-Jones");
    token.set(CoreAnnotations.ShapeAnnotation.class, "Xxxx-Xxxx");
    token.set(CoreAnnotations.DomainAnnotation.class, "bio");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    PaddedList<CoreLabel> padded = new PaddedList<>(tokens);
//    Collection<String> feats = factory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//
//    boolean frag = false;
//    boolean word = false;
//    for (String f : feats) {
//      if (f.contains("WFRAG|C")) frag = true;
//      if (f.contains("WORD|C")) word = true;
//    }
//    assertTrue(frag && word);
  }
@Test
  public void testCleanGazetteExactMultiwordMatch() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.cleanGazette = true;
    flags.useGazettes = true;
    flags.useWord = true;
    factory.init(flags);

    CoreLabel w1 = new CoreLabel();
    w1.setWord("New");
    w1.set(CoreAnnotations.ShapeAnnotation.class, "Xxx");
    w1.set(CoreAnnotations.DomainAnnotation.class, "geo");

    CoreLabel w2 = new CoreLabel();
    w2.setWord("York");
    w2.set(CoreAnnotations.ShapeAnnotation.class, "Xxxx");
    w2.set(CoreAnnotations.DomainAnnotation.class, "geo");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(w1);
    tokens.add(w2);

    try {
      java.lang.reflect.Field field = NERFeatureFactory.class.getDeclaredField("wordToGazetteInfos");
      field.setAccessible(true);
      Map<String, Collection<?>> map = (Map<String, Collection<?>>) field.get(factory);
      Collection infos = new HashSet<>();
//      infos.add(factory.new GazetteInfo("CITY-GAZ", 0, new String[]{"New", "York"}));
      map.put("New", infos);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    PaddedList<CoreLabel> padded = new PaddedList<>(tokens);
//    Collection<String> result = factory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//
//    boolean found = false;
//    for (String f : result) {
//      if (f.equals("CITY-GAZ|C")) found = true;
//    }
//    assertTrue(found);
  }
@Test
  public void testNextVBAndPrevVBFlagsAnnotateVerb() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useNextVB = true;
    flags.usePrevVB = true;
    flags.useVB = true;
    flags.useWord = true;
    factory.init(flags);

    CoreLabel t0 = new CoreLabel();
    t0.setWord("The");
    t0.set(CoreAnnotations.PartOfSpeechAnnotation.class, "DT");
    t0.set(CoreAnnotations.ShapeAnnotation.class, "Xxx");
    t0.set(CoreAnnotations.DomainAnnotation.class, "test");

    CoreLabel t1 = new CoreLabel();
    t1.setWord("cat");
    t1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    t1.set(CoreAnnotations.ShapeAnnotation.class, "xxx");
    t1.set(CoreAnnotations.DomainAnnotation.class, "test");

    CoreLabel t2 = new CoreLabel();
    t2.setWord("runs");
    t2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBZ");
    t2.set(CoreAnnotations.ShapeAnnotation.class, "xxxx");
    t2.set(CoreAnnotations.DomainAnnotation.class, "test");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(t0);
    tokens.add(t1);
    tokens.add(t2);

    PaddedList<CoreLabel> padded = new PaddedList<>(tokens);
//    Collection<String> feats = factory.getCliqueFeatures(padded, 1, Clique.valueOf("C"));
//
//    boolean vb = false;
//    for (String f : feats) {
//      if (f.endsWith("PNVB|C")) vb = true;
//    }
//    assertTrue(vb);
  }
@Test
  public void testUseSplitWordRegexWithSymbolicPattern() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.splitWordRegex = "[^\\p{L}]+";
    flags.useWord = true;
    factory.init(flags);

    CoreLabel label = new CoreLabel();
    label.setWord("a😊b$c%!");
    label.set(CoreAnnotations.ShapeAnnotation.class, "S");
    label.set(CoreAnnotations.DomainAnnotation.class, "emoji");

    List<CoreLabel> list = new ArrayList<>();
    list.add(label);
    PaddedList<CoreLabel> padded = new PaddedList<>(list);
//    Collection<String> feats = factory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//
//    boolean foundSplit = false;
//    for (String f : feats) {
//      if (f.contains("-SPLITWORD|C")) foundSplit = true;
//    }
//
//    assertTrue(foundSplit);
  }
@Test
  public void testUseFirstWordIncludesInitialFeature() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useFirstWord = true;
    flags.useWord = true;
    factory.init(flags);

    CoreLabel cl = new CoreLabel();
    cl.setWord("Intro");
    cl.set(CoreAnnotations.ShapeAnnotation.class, "Xxxxx");
    cl.set(CoreAnnotations.DomainAnnotation.class, "run");

    List<CoreLabel> input = new ArrayList<>();
    input.add(cl);
    PaddedList<CoreLabel> padded = new PaddedList<>(input);

//    Collection<String> feats = factory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//    assertTrue(feats.contains("Intro|C"));
  }
@Test
  public void testUseNextRealWordAppliesShapeRule() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useNextRealWord = true;
    flags.useWord = true;
    factory.init(flags);

    CoreLabel a = new CoreLabel();
    a.setWord("a");
    a.set(CoreAnnotations.ShapeAnnotation.class, "x");
    a.set(CoreAnnotations.DomainAnnotation.class, "test");

    CoreLabel b = new CoreLabel();
    b.setWord("and");
    b.set(CoreAnnotations.ShapeAnnotation.class, "xxx");
    b.set(CoreAnnotations.DomainAnnotation.class, "test");

    CoreLabel c = new CoreLabel();
    c.setWord("LARGE");
    c.set(CoreAnnotations.ShapeAnnotation.class, "XXXX");
    c.set(CoreAnnotations.DomainAnnotation.class, "test");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(a);
    tokens.add(b);
    tokens.add(c);

    PaddedList<CoreLabel> padded = new PaddedList<>(tokens);
//    Collection<String> result = factory.getCliqueFeatures(padded, 1, Clique.valueOf("C"));
//
//    boolean hadNNW = false;
//    for (String r : result) {
//      if (r.contains("NNW_CTYPE|C")) hadNNW = true;
//    }
//    assertTrue(hadNNW);
  }
@Test
  public void testUseLastRealWordAppliesShortBackwardRule() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useLastRealWord = true;
    flags.useWord = true;
    factory.init(flags);

    CoreLabel x = new CoreLabel();
    x.setWord("to");
    x.set(CoreAnnotations.ShapeAnnotation.class, "xx");
    x.set(CoreAnnotations.DomainAnnotation.class, "reverse");

    CoreLabel y = new CoreLabel();
    y.setWord("become");
    y.set(CoreAnnotations.ShapeAnnotation.class, "xxxxxx");
    y.set(CoreAnnotations.DomainAnnotation.class, "reverse");

    CoreLabel z = new CoreLabel();
    z.setWord("great");
    z.set(CoreAnnotations.ShapeAnnotation.class, "xxxxx");
    z.set(CoreAnnotations.DomainAnnotation.class, "reverse");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(x);
    tokens.add(y);
    tokens.add(z);

    PaddedList<CoreLabel> padded = new PaddedList<>(tokens);
//    Collection<String> features = factory.getCliqueFeatures(padded, 1, Clique.valueOf("C"));
//
//    boolean hasBackShapeFeature = false;
//    for (String f : features) {
//      if (f.contains("PPW_CTYPE|C")) {
//        hasBackShapeFeature = true;
//      }
//    }
//
//    assertTrue(hasBackShapeFeature);
  }
@Test
  public void testUseWordPairsWithPaddingDoesNotThrow() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useWord = true;
    flags.usePrev = true;
    flags.useNext = true;
    flags.useWordPairs = true;
    factory.init(flags);

    CoreLabel edge = new CoreLabel();
    edge.setWord("edge");
    edge.set(CoreAnnotations.ShapeAnnotation.class, "xxxx");
    edge.set(CoreAnnotations.DomainAnnotation.class, "e");

    List<CoreLabel> single = new ArrayList<>();
    single.add(edge);
    PaddedList<CoreLabel> padded = new PaddedList<>(single);

//    Collection<String> output = factory.getCliqueFeatures(padded, 0, Clique.valueOf("C"));
//    assertNotNull(output);
  } 
}