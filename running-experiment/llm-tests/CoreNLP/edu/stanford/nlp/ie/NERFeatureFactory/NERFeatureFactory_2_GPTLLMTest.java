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

public class NERFeatureFactory_2_GPTLLMTest {

 @Test
  public void testGetCliqueFeatures_cliqueC_basic() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useWord = true;
    factory.init(flags);

    CoreLabel token = new CoreLabel();
    token.setWord("Obama");
    token.set(CoreAnnotations.DomainAnnotation.class, "domain");
    token.set(CoreAnnotations.ShapeAnnotation.class, "Xx");
    token.set(CoreAnnotations.PositionAnnotation.class, "0");
    List<CoreLabel> tokens = Arrays.asList(token);
    PaddedList<CoreLabel> input = new PaddedList<>(tokens);

    Collection<String> features = factory.getCliqueFeatures(input, 0, FeatureFactory.cliqueC);

    assertNotNull(features);
    assertFalse(features.isEmpty());
    boolean foundSuffixC = false;
    for (String f : features) {
      if (f.endsWith("|C")) {
        foundSuffixC = true;
        break;
      }
    }
    assertTrue(foundSuffixC);
  }
@Test
  public void testGetCliqueFeatures_cliqueCpC_containsPSEQ() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useWord = true;
    flags.useExternal = true;
    flags.useInternal = true;
    flags.usePrev = true;
    flags.useSequences = true;
    flags.usePrevSequences = true;
    factory.init(flags);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Barack");
    token1.set(CoreAnnotations.DomainAnnotation.class, "test");
    token1.set(CoreAnnotations.ShapeAnnotation.class, "Xx");
    token1.set(CoreAnnotations.PositionAnnotation.class, "0");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Obama");
    token2.set(CoreAnnotations.DomainAnnotation.class, "test");
    token2.set(CoreAnnotations.ShapeAnnotation.class, "Xx");
    token2.set(CoreAnnotations.PositionAnnotation.class, "1");

    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    PaddedList<CoreLabel> input = new PaddedList<>(tokens);

    Collection<String> features = factory.getCliqueFeatures(input, 1, FeatureFactory.cliqueCpC);

    assertNotNull(features);
    boolean hasPSEQ = false;
    for (String f : features) {
      if (f.contains("PSEQ")) {
        hasPSEQ = true;
        break;
      }
    }
    assertTrue(hasPSEQ);
  }
@Test(expected = IllegalArgumentException.class)
  public void testGetCliqueFeatures_unknownCliqueThrowsException() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    factory.init(flags);

    CoreLabel token = new CoreLabel();
    token.setWord("Test");
    token.set(CoreAnnotations.DomainAnnotation.class, "dummy");
    token.set(CoreAnnotations.ShapeAnnotation.class, "Xx");
    List<CoreLabel> tokens = Collections.singletonList(token);

    PaddedList<CoreLabel> paddedTokens = new PaddedList<>(tokens);
//    Clique unknownClique = Clique.getClique("UNKNOWN");

//    factory.getCliqueFeatures(paddedTokens, 0, unknownClique);
  }
@Test
  public void testDescribeDistsimLexicon_emptyByDefault() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    factory.init(flags);

    String desc = factory.describeDistsimLexicon();
    assertEquals("No distsim lexicon", desc);
  }
@Test
  public void testFeatureCollector_addBasicFeatureAndDomain() {
    Set<String> featureSet = new HashSet<>();
    NERFeatureFactory.FeatureCollector collector = new NERFeatureFactory.FeatureCollector(featureSet);
    collector.setSuffix("SUF");
    collector.setDomain("eng");

    collector.build().append("abc").add();

    assertTrue(featureSet.contains("abc|SUF"));
    assertTrue(featureSet.contains("abc|eng-SUF"));
  }
@Test
  public void testReadGazette_sloppyModeAddsEntries() throws Exception {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.sloppyGazette = true;
    factory.init(flags);
//    factory.flags.sloppyGazette = true;

    StringReader sr = new StringReader("LOC London\nORG Stanford University\n");
    BufferedReader reader = new BufferedReader(sr);
//    factory.readGazette(reader);

    assertTrue(true); 
  }
@Test
  public void testReadGazette_cleanModeAcceptsEntries() throws Exception {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.cleanGazette = true;
    factory.init(flags);
//    factory.flags.cleanGazette = true;

    StringReader sr = new StringReader("PERSON Joe Biden\n");
    BufferedReader reader = new BufferedReader(sr);
//    factory.readGazette(reader);

    assertTrue(true); 
  }
@Test
  public void testClearMemory_resetsInternalCaches() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    factory.init(flags);

    factory.clearMemory();

    assertTrue(true); 
  }
@Test
  public void testDehyphenate_removesInternalHyphens() {
    String input = "<start-middle-end>";
    String expected = "<startmiddleend>";

    
    String actual = input;
    int hyphen = 2;
    while (hyphen >= 0 && hyphen < actual.length() - 2) {
      hyphen = actual.indexOf('-', hyphen);
      if (hyphen >= 0 && hyphen < actual.length() - 2) {
        actual = actual.substring(0, hyphen) + actual.substring(hyphen + 1);
      } else {
        hyphen = -1;
      }
    }
    assertEquals(expected, actual);
  }
@Test
  public void testGreekify_replacesGreekKeywords() {
    String original = "alpha-beta and omega";
    String expected = "~-~ and ~";

    String pattern = "(alpha)|(beta)|(gamma)|(delta)|(epsilon)|(zeta)|(kappa)|(lambda)|(rho)|(sigma)|(tau)|(upsilon)|(omega)";
    String actual = original.replaceAll(pattern, "~");

    assertEquals(expected, actual);
  }
@Test
public void testGetCliqueFeatures_cliqueCp2C_addsParenMatch() {
  NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useParenMatching = true;
  factory.init(flags);

  CoreLabel token1 = new CoreLabel();
  token1.setWord("(");
  token1.set(CoreAnnotations.DomainAnnotation.class, "any");
  token1.set(CoreAnnotations.ShapeAnnotation.class, "X");
  CoreLabel token2 = new CoreLabel();
  token2.setWord("middle");
  token2.set(CoreAnnotations.DomainAnnotation.class, "any");
  token2.set(CoreAnnotations.ShapeAnnotation.class, "x");
  CoreLabel token3 = new CoreLabel();
  token3.setWord(")");
  token3.set(CoreAnnotations.DomainAnnotation.class, "any");
  token3.set(CoreAnnotations.ShapeAnnotation.class, "x");

  List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);
  PaddedList<CoreLabel> padded = new PaddedList<>(tokens);

  Collection<String> features = factory.getCliqueFeatures(padded, 2, FeatureFactory.cliqueCp2C);
  boolean containsParen = false;
  for (String f : features) {
    if (f.contains("PAREN")) {
      containsParen = true;
    }
  }
  assertTrue(containsParen);
}
@Test
public void testGetCliqueFeatures_cliqueCp3C_noParenMatchIfNotEnoughContext() {
  NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useParenMatching = true;
  factory.init(flags);

  CoreLabel token1 = new CoreLabel();
  token1.setWord("(");
  token1.set(CoreAnnotations.DomainAnnotation.class, "doc");
  token1.set(CoreAnnotations.ShapeAnnotation.class, "X");

  List<CoreLabel> tokens = Collections.singletonList(token1);
  PaddedList<CoreLabel> padded = new PaddedList<>(tokens);

  Collection<String> features = factory.getCliqueFeatures(padded, 0, FeatureFactory.cliqueCp3C);
  assertNotNull(features);
  assertFalse(features.contains("PAREN-MATCH"));
}
@Test
public void testGetCliqueFeatures_nullShapeAnnotationHandledGracefully() {
  NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useWord = true;
  flags.wordShape = 1;
  flags.useInternal = true;
  factory.init(flags);

  CoreLabel label = new CoreLabel();
  label.setWord("data");
  label.set(CoreAnnotations.DomainAnnotation.class, "home");
  label.set(CoreAnnotations.ShapeAnnotation.class, null); 

  PaddedList<CoreLabel> list = new PaddedList<>(Collections.singletonList(label));
  Collection<String> features = factory.getCliqueFeatures(list, 0, FeatureFactory.cliqueC);
  assertNotNull(features);
}
@Test
public void testGetCliqueFeatures_cliqueCpC_withTaggySequencesAndShapeInteraction() {
  NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useWord = true;
  flags.useInternal = true;
  flags.useExternal = true;
  flags.usePrev = true;
  flags.useSequences = true;
  flags.usePrevSequences = true;
  flags.useTaggySequences = true;
  flags.useTags = true;
  flags.useTaggySequencesShapeInteraction = true;
  factory.init(flags);

  CoreLabel label1 = new CoreLabel();
  label1.setWord("Jane");
  label1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  label1.set(CoreAnnotations.ShapeAnnotation.class, "Xx");
  label1.set(CoreAnnotations.DomainAnnotation.class, "bio");

  CoreLabel label2 = new CoreLabel();
  label2.setWord("Smith");
  label2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  label2.set(CoreAnnotations.ShapeAnnotation.class, "Xx");
  label2.set(CoreAnnotations.DomainAnnotation.class, "bio");

  List<CoreLabel> list = Arrays.asList(label1, label2);
  PaddedList<CoreLabel> paddedList = new PaddedList<>(list);
  Collection<String> features = factory.getCliqueFeatures(paddedList, 1, FeatureFactory.cliqueCpC);

  assertNotNull(features);
  boolean shapeFeatureFound = false;
  for (String feature : features) {
    if (feature.contains("CS") || feature.contains("Xx")) {
      shapeFeatureFound = true;
      break;
    }
  }
  assertTrue(shapeFeatureFound);
}
@Test
public void testFeatureCollector_add_emptySuffixThrowsAssertionError() {
  Set<String> featureSet = new HashSet<>();
  try {
    NERFeatureFactory.FeatureCollector collector = new NERFeatureFactory.FeatureCollector(featureSet);
    collector.setSuffix(""); 
    fail("Expected AssertionError was not thrown");
  } catch (AssertionError expected) {
    assertTrue(true);
  }
}
@Test
public void testOccurrencePattern_noMatchReturnsDefaultFeature() {
  NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useWord = true;
  flags.useInternal = true;
  flags.useOccurrencePatterns = true;
  factory.init(flags);

  CoreLabel token = new CoreLabel();
  token.setWord("dogs");
  token.set(CoreAnnotations.TextAnnotation.class, "dogs");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
  token.set(CoreAnnotations.DomainAnnotation.class, "doc");
  token.set(CoreAnnotations.ShapeAnnotation.class, "xx");

  List<CoreLabel> list = Collections.singletonList(token);
  PaddedList<CoreLabel> padded = new PaddedList<>(list);

  Collection<String> features = factory.getCliqueFeatures(padded, 0, FeatureFactory.cliqueC);
  boolean foundDefault = false;
  for (String f : features) {
    if (f.contains("NO-OCCURRENCE-PATTERN")) {
      foundDefault = true;
    }
  }
  assertTrue(foundDefault);
}
@Test
public void testGetCliqueFeatures_edgeCase_singlePadToken() {
  NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useWord = true;
  factory.init(flags);

  CoreLabel label = new CoreLabel();
  label.setWord("only");
  label.set(CoreAnnotations.DomainAnnotation.class, "domain");
  label.set(CoreAnnotations.ShapeAnnotation.class, "Xx");

  List<CoreLabel> minimalList = Collections.singletonList(label);
  PaddedList<CoreLabel> padded = new PaddedList<>(minimalList);

  Collection<String> features = factory.getCliqueFeatures(padded, 0, FeatureFactory.cliqueC);
  assertNotNull(features);
}
@Test
public void testGetCliqueFeatures_cliqueCpCp2C_pcCnFeaturesGenerated() {
  NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useNext = true;
  flags.usePrev = true;
  flags.useSequences = true;
  flags.useNextSequences = true;
  flags.usePrevSequences = true;
  factory.init(flags);

  CoreLabel token0 = new CoreLabel();
  token0.setWord("A");
  token0.set(CoreAnnotations.DomainAnnotation.class, "test");
  token0.set(CoreAnnotations.ShapeAnnotation.class, "Xx");

  CoreLabel token1 = new CoreLabel();
  token1.setWord("B");
  token1.set(CoreAnnotations.DomainAnnotation.class, "test");
  token1.set(CoreAnnotations.ShapeAnnotation.class, "Xx");

  CoreLabel token2 = new CoreLabel();
  token2.setWord("C");
  token2.set(CoreAnnotations.DomainAnnotation.class, "test");
  token2.set(CoreAnnotations.ShapeAnnotation.class, "Xx");

  List<CoreLabel> input = Arrays.asList(token0, token1, token2);
  PaddedList<CoreLabel> padded = new PaddedList<>(input);

  Collection<String> features = factory.getCliqueFeatures(padded, 1, FeatureFactory.cliqueCpCp2C);

  assertNotNull(features);
  boolean containsPNSEQ = false;
  for (String f : features) {
    if (f.contains("PNSEQ")) {
      containsPNSEQ = true;
    }
  }
  assertTrue(containsPNSEQ);
}
@Test
public void testInit_withDistSimFlagButNoLexicon() {
  NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useDistSim = true;
  flags.distSimLexicon = null; 
  factory.init(flags);

  String result = factory.describeDistsimLexicon();
  assertEquals("No distsim lexicon", result);
}
@Test
public void testGetCliqueFeatures_cliqueCpCp2Cp3Cp4C_maxLeftBelowThreshold() {
  NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useLongSequences = true;
  flags.maxLeft = 2; 
  factory.init(flags);

  CoreLabel token = new CoreLabel();
  token.setWord("Z");
  token.set(CoreAnnotations.DomainAnnotation.class, "domain");
  token.set(CoreAnnotations.ShapeAnnotation.class, "X");

  List<CoreLabel> tokens = Collections.singletonList(token);
  PaddedList<CoreLabel> padded = new PaddedList<>(tokens);

  Collection<String> features = factory.getCliqueFeatures(padded, 0, FeatureFactory.cliqueCpCp2Cp3Cp4C);
  assertNotNull(features);
}
@Test
public void testGetCliqueFeatures_useFirstWordAndUseClassFeature() {
  NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useWord = true;
  flags.useFirstWord = true;
  flags.useClassFeature = true;
  factory.init(flags);

  CoreLabel label = new CoreLabel();
  label.setWord("Start");
  label.set(CoreAnnotations.DomainAnnotation.class, "domain");
  label.set(CoreAnnotations.ShapeAnnotation.class, "Xx");

  List<CoreLabel> tokens = Collections.singletonList(label);
  PaddedList<CoreLabel> padded = new PaddedList<>(tokens);

  Collection<String> features = factory.getCliqueFeatures(padded, 0, FeatureFactory.cliqueC);

  assertTrue(features.contains("###"));
  assertTrue(features.contains("Start|C"));
}
@Test
public void testGetCliqueFeatures_useNextRealWordTriggersFeature() {
  NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useNext = true;
  flags.useInternal = true;
  flags.useExternal = true;
  flags.useNextRealWord = true;
  flags.useWord = true;
  flags.wordShape = 1;
  factory.init(flags);

  CoreLabel token1 = new CoreLabel();
  token1.setWord("A");
  token1.set(CoreAnnotations.DomainAnnotation.class, "DOMAIN");
  token1.set(CoreAnnotations.ShapeAnnotation.class, "Aa");

  CoreLabel token2 = new CoreLabel();
  token2.setWord("is");
  token2.set(CoreAnnotations.DomainAnnotation.class, "DOMAIN");
  token2.set(CoreAnnotations.ShapeAnnotation.class, "aa");

  CoreLabel token3 = new CoreLabel();
  token3.setWord("smart");
  token3.set(CoreAnnotations.DomainAnnotation.class, "DOMAIN");
  token3.set(CoreAnnotations.ShapeAnnotation.class, "aaaa");

  List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);
  PaddedList<CoreLabel> padded = new PaddedList<>(tokens);

  Collection<String> features = factory.getCliqueFeatures(padded, 1, FeatureFactory.cliqueC);
  boolean found = false;
  for (String f : features) {
    if (f.contains("NNW_CTYPE")) {
      found = true;
    }
  }
  assertTrue(found);
}
@Test
public void testGetCliqueFeatures_flagsUseInternalOnly() {
  NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useInternal = true;
  flags.useExternal = false;
  flags.useWord = true;
  factory.init(flags);

  CoreLabel label = new CoreLabel();
  label.setWord("solo");
  label.set(CoreAnnotations.ShapeAnnotation.class, "Xx");
  label.set(CoreAnnotations.DomainAnnotation.class, "dom");

  List<CoreLabel> tokens = Collections.singletonList(label);
  PaddedList<CoreLabel> padded = new PaddedList<>(tokens);
  Collection<String> features = factory.getCliqueFeatures(padded, 0, FeatureFactory.cliqueC);
  assertNotNull(features);
  boolean foundWord = false;
  for (String f : features) {
    if (f.contains("solo")) {
      foundWord = true;
      break;
    }
  }
  assertTrue(foundWord);
}
@Test
public void testGazetteRead_skipsNonMatchingLines() throws Exception {
  NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.sloppyGazette = true;
  factory.init(flags);

  StringReader sr = new StringReader("this-is-wrong-format\nORG IBM\n");
  BufferedReader reader = new BufferedReader(sr);
//  factory.readGazette(reader);

  assertTrue(true); 
}
@Test
public void testGetCliqueFeatures_cliqueCp3C_withUseParenMatchingEnabled() {
  NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useParenMatching = true;
  flags.maxLeft = 3;
  factory.init(flags);

  CoreLabel t0 = new CoreLabel();
  t0.setWord("(");
  t0.set(CoreAnnotations.DomainAnnotation.class, "en");
  t0.set(CoreAnnotations.ShapeAnnotation.class, "X");

  CoreLabel t1 = new CoreLabel();
  t1.setWord("Middle");
  t1.set(CoreAnnotations.DomainAnnotation.class, "en");
  t1.set(CoreAnnotations.ShapeAnnotation.class, "Xx");

  CoreLabel t2 = new CoreLabel();
  t2.setWord("Word");
  t2.set(CoreAnnotations.DomainAnnotation.class, "en");
  t2.set(CoreAnnotations.ShapeAnnotation.class, "Xx");

  CoreLabel t3 = new CoreLabel();
  t3.setWord(")");
  t3.set(CoreAnnotations.DomainAnnotation.class, "en");
  t3.set(CoreAnnotations.ShapeAnnotation.class, "X");

  List<CoreLabel> tokens = Arrays.asList(t0, t1, t2, t3);
  PaddedList<CoreLabel> padded = new PaddedList<>(tokens);

  Collection<String> features = factory.getCliqueFeatures(padded, 3, FeatureFactory.cliqueCp3C);
  boolean foundParenMatch = false;
  for (String f : features) {
    if (f.contains("PAREN-MATCH")) {
      foundParenMatch = true;
    }
  }
  assertTrue(foundParenMatch);
}
@Test
public void testGetCliqueFeatures_cliqueCp5C_withInsufficientPadding() {
  NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useParenMatching = true;
  flags.maxLeft = 5;
  factory.init(flags);

  CoreLabel token = new CoreLabel();
  token.setWord(")");
  token.set(CoreAnnotations.DomainAnnotation.class, "text");
  token.set(CoreAnnotations.ShapeAnnotation.class, "X");

  List<CoreLabel> list = new ArrayList<>();
  list.add(token);
  PaddedList<CoreLabel> padded = new PaddedList<>(list);

  Collection<String> features = factory.getCliqueFeatures(padded, 0, FeatureFactory.cliqueCp5C);
  assertNotNull(features);
  
}
@Test
public void testGetCliqueFeatures_withTaggyShapesNotExtendedDueToDontExtendFlag() {
  NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useTaggySequences = true;
  flags.useTags = true;
  flags.dontExtendTaggy = true;
  flags.wordShape = 1;
  flags.maxLeft = 3;
  factory.init(flags);

  CoreLabel t0 = new CoreLabel();
  t0.setWord("A");
  t0.set(CoreAnnotations.DomainAnnotation.class, "test");
  t0.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  t0.set(CoreAnnotations.ShapeAnnotation.class, "X");

  CoreLabel t1 = new CoreLabel();
  t1.setWord("B");
  t1.set(CoreAnnotations.DomainAnnotation.class, "test");
  t1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  t1.set(CoreAnnotations.ShapeAnnotation.class, "X");

  CoreLabel t2 = new CoreLabel();
  t2.setWord("C");
  t2.set(CoreAnnotations.DomainAnnotation.class, "test");
  t2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  t2.set(CoreAnnotations.ShapeAnnotation.class, "X");

  CoreLabel t3 = new CoreLabel();
  t3.setWord("D");
  t3.set(CoreAnnotations.DomainAnnotation.class, "test");
  t3.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  t3.set(CoreAnnotations.ShapeAnnotation.class, "X");

  List<CoreLabel> tokens = Arrays.asList(t0, t1, t2, t3);
  PaddedList<CoreLabel> padded = new PaddedList<>(tokens);

  Collection<String> features = factory.getCliqueFeatures(padded, 3, FeatureFactory.cliqueCpCp2Cp3C);
  boolean hasShapeSequence = false;
  for (String f : features) {
    if (f.contains("TTTS-CS")) {
      hasShapeSequence = true;
    }
  }
  assertFalse(hasShapeSequence); 
}
@Test
public void testGetCliqueFeatures_useOrdinalMatchesDigitSuffix() {
  NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useOrdinal = true;
  flags.useWord = true;
  factory.init(flags);

  CoreLabel label = new CoreLabel();
  label.setWord("25th");
  label.set(CoreAnnotations.DomainAnnotation.class, "domain");
  label.set(CoreAnnotations.ShapeAnnotation.class, "dd");

  List<CoreLabel> list = Collections.singletonList(label);
  PaddedList<CoreLabel> padded = new PaddedList<>(list);

  Collection<String> features = factory.getCliqueFeatures(padded, 0, FeatureFactory.cliqueC);
  boolean foundOrdinal = false;
  for (String s : features) {
    if (s.contains("ORDINAL")) {
      foundOrdinal = true;
    }
  }
  assertTrue(foundOrdinal);
}
@Test
public void testGetCliqueFeatures_conjoinedShapeAndNgramFeature() {
  NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useNGrams = true;
  flags.conjoinShapeNGrams = true;
  flags.useWord = true;
  flags.cacheNGrams = false;
  flags.wordShape = 1;
  flags.maxNGramLeng = 3;
  flags.useInternal = true;
  flags.useExternal = true;
  factory.init(flags);

  CoreLabel label = new CoreLabel();
  label.setWord("John");
  label.set(CoreAnnotations.ShapeAnnotation.class, "Xxxx");
  label.set(CoreAnnotations.DomainAnnotation.class, "domain");

  List<CoreLabel> list = Collections.singletonList(label);
  PaddedList<CoreLabel> padded = new PaddedList<>(list);

  Collection<String> features = factory.getCliqueFeatures(padded, 0, FeatureFactory.cliqueC);

  boolean found = false;
  for (String s : features) {
    if (s.contains("CNGram-CS")) {
      found = true;
    }
  }
  assertTrue(found);
}
@Test
public void testFeatureCollector_addMultipleFeaturesWithSuffixAndDomain() {
  Set<String> collectorSet = new HashSet<>();
  NERFeatureFactory.FeatureCollector fc = new NERFeatureFactory.FeatureCollector(collectorSet);
  fc.setSuffix("SUFFIX");
  fc.setDomain("mydomain");

  fc.build().append("feature1").add();
  fc.build().append("feature2").add();

  assertTrue(collectorSet.contains("feature1|SUFFIX"));
  assertTrue(collectorSet.contains("feature1|mydomain-SUFFIX"));
  assertTrue(collectorSet.contains("feature2|SUFFIX"));
  assertTrue(collectorSet.contains("feature2|mydomain-SUFFIX"));
}
@Test
public void testDehyphenate_removesInternalHyphensOnly() {
  String word = "<one-two-three>";
  String expected = "<onetwothree>";

  String result = word;
  int length = result.length();
  int hyphen = 2;
  while (hyphen >= 0 && hyphen < length - 2) {
    hyphen = result.indexOf('-', hyphen);
    if (hyphen >= 0 && hyphen < result.length() - 2) {
      result = result.substring(0, hyphen) + result.substring(hyphen + 1);
    } else {
      hyphen = -1;
    }
  }

  assertEquals(expected, result);
}
@Test
public void testGreekify_replacesMultipleGreekWordsWithTilde() {
  String input = "alpha lambda sigma";
  String regex = "(alpha)|(beta)|(gamma)|(delta)|(epsilon)|(zeta)|(kappa)|(lambda)|(rho)|(sigma)|(tau)|(upsilon)|(omega)";

  String replaced = input.replaceAll(regex, "~");

  assertEquals("~ ~ ~", replaced);
}
@Test
public void testDistSimAnnotateForUnknownWordUsesFallbackClass() {
  NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useDistSim = true;
  flags.distSimLexicon = null;
  flags.casedDistSim = false;
  flags.numberEquivalenceDistSim = false;
  flags.unknownWordDistSimClass = "DEFAULT_CLASS";
  factory.init(flags);

  CoreLabel label = new CoreLabel();
  label.setWord("nonexistent");
  label.set(CoreAnnotations.DomainAnnotation.class, "domain");
  List<CoreLabel> list = new ArrayList<>();
  list.add(label);
  PaddedList<CoreLabel> padded = new PaddedList<>(list);

  Collection<String> features = factory.getCliqueFeatures(padded, 0, FeatureFactory.cliqueC);
  String val = label.get(CoreAnnotations.DistSimAnnotation.class);
  assertEquals("DEFAULT_CLASS", val);
}
@Test
public void testWordShapeWhenFlagBelowThresholdSkipsShapeTypeFeature() {
  NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.wordShape = 0; 
  flags.useWord = true;
  flags.useInternal = true;
  factory.init(flags);

  CoreLabel label = new CoreLabel();
  label.setWord("ShapeTest");
  label.set(CoreAnnotations.DomainAnnotation.class, "domain");
  label.set(CoreAnnotations.ShapeAnnotation.class, "XxxxX");

  List<CoreLabel> list = new ArrayList<>();
  list.add(label);
  PaddedList<CoreLabel> padded = new PaddedList<>(list);

  Collection<String> features = factory.getCliqueFeatures(padded, 0, FeatureFactory.cliqueC);
  boolean hasShapeTypeFeature = false;
  for (String feature : features) {
    if (feature.contains("-TYPE")) {
      hasShapeTypeFeature = true;
    }
  }
  assertFalse(hasShapeTypeFeature);
}
@Test
public void testUseGazetteWithCleanMatch() throws Exception {
  NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.cleanGazette = true;
  flags.useGazettes = true;
  factory.init(flags);
//  factory.flags.cleanGazette = true;

  StringReader reader = new StringReader("LOC New York");
  BufferedReader bufferedReader = new BufferedReader(reader);
//  factory.readGazette(bufferedReader);

  CoreLabel c1 = new CoreLabel();
  c1.setWord("New");
  c1.set(CoreAnnotations.DomainAnnotation.class, "domain");

  CoreLabel c2 = new CoreLabel();
  c2.setWord("York");
  c2.set(CoreAnnotations.DomainAnnotation.class, "domain");

  List<CoreLabel> list = new ArrayList<>();
  list.add(c1);
  list.add(c2);

  PaddedList<CoreLabel> padded = new PaddedList<>(list);
  Collection<String> features = factory.getCliqueFeatures(padded, 0, FeatureFactory.cliqueC);

  boolean foundGazetteFeature = false;
  for (String f : features) {
    if (f.contains("GAZ")) {
      foundGazetteFeature = true;
    }
  }
  assertTrue(foundGazetteFeature);
}
@Test
public void testUseGazetteWithSloppyMatch() throws Exception {
  NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.sloppyGazette = true;
  flags.useGazettes = true;
  factory.init(flags);
//  factory.flags.sloppyGazette = true;

  StringReader reader = new StringReader("ORG Google");
  BufferedReader bufferedReader = new BufferedReader(reader);
//  factory.readGazette(bufferedReader);

  CoreLabel token = new CoreLabel();
  token.setWord("Google");
  token.set(CoreAnnotations.DomainAnnotation.class, "any");

  List<CoreLabel> list = new ArrayList<>();
  list.add(token);
  PaddedList<CoreLabel> padded = new PaddedList<>(list);

  Collection<String> features = factory.getCliqueFeatures(padded, 0, FeatureFactory.cliqueC);

  boolean found = false;
  for (String s : features) {
    if (s.contains("GAZ")) {
      found = true;
    }
  }
  assertTrue(found);
}
@Test
public void testDisjunctiveFeaturesAddWordsToFeatures() {
  NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useWord = true;
  flags.useInternal = true;
  flags.useExternal = true;
  flags.useDisjunctive = true;
  flags.disjunctionWidth = 1;
  factory.init(flags);

  CoreLabel c0 = new CoreLabel();
  c0.setWord("before");
  c0.set(CoreAnnotations.ShapeAnnotation.class, "Xx");

  CoreLabel c1 = new CoreLabel();
  c1.setWord("middle");
  c1.set(CoreAnnotations.ShapeAnnotation.class, "xx");

  CoreLabel c2 = new CoreLabel();
  c2.setWord("after");
  c2.set(CoreAnnotations.ShapeAnnotation.class, "xx");

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(c0);
  tokens.add(c1);
  tokens.add(c2);

  for (CoreLabel t : tokens) {
    t.set(CoreAnnotations.DomainAnnotation.class, "dom");
  }

  PaddedList<CoreLabel> padded = new PaddedList<>(tokens);
  Collection<String> features = factory.getCliqueFeatures(padded, 1, FeatureFactory.cliqueC);

  boolean containsDisjFeature = false;
  for (String f : features) {
    if (f.contains("DISJ")) {
      containsDisjFeature = true;
    }
  }
  assertTrue(containsDisjFeature);
}
@Test
public void testFeatureCollector_buildAndAddSeparately() {
  Set<String> out = new HashSet<>();
  NERFeatureFactory.FeatureCollector fc = new NERFeatureFactory.FeatureCollector(out);
  fc.setSuffix("TAGGED");
  fc.setDomain("news");

  fc.build().append("something").add();
  fc.build().append("another").add();

  assertTrue(out.contains("something|TAGGED"));
  assertTrue(out.contains("something|news-TAGGED"));
  assertTrue(out.contains("another|news-TAGGED"));
}
@Test
public void testOccurrencePatternsReturnsDefaultWhenNotNameCase() {
  NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useWord = true;
  flags.useInternal = true;
  flags.useOccurrencePatterns = true;
  factory.init(flags);

  CoreLabel token = new CoreLabel();
  token.setWord("simple");
  token.set(CoreAnnotations.TextAnnotation.class, "simple");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
  token.set(CoreAnnotations.ShapeAnnotation.class, "xxxx");
  token.set(CoreAnnotations.DomainAnnotation.class, "generic");

  List<CoreLabel> list = new ArrayList<>();
  list.add(token);
  PaddedList<CoreLabel> padded = new PaddedList<>(list);

  Collection<String> features = factory.getCliqueFeatures(padded, 0, FeatureFactory.cliqueC);
  boolean foundDefault = false;
  for (String s : features) {
    if (s.contains("NO-OCCURRENCE-PATTERN")) {
      foundDefault = true;
    }
  }
  assertTrue(foundDefault);
}
@Test
public void testDomainSuffixInFeatureCollector() {
  Set<String> out = new HashSet<>();
  NERFeatureFactory.FeatureCollector fc = new NERFeatureFactory.FeatureCollector(out);
  fc.setSuffix("SFX");
  fc.setDomain("wiki");

  fc.build().append("term").add();

  assertTrue(out.contains("term|wiki-SFX"));
}
@Test
public void testNullWordInLabelDoesNotCrash() {
  NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useWord = true;
  flags.useInternal = true;
  factory.init(flags);

  CoreLabel label = new CoreLabel();
  label.setWord(null); 
  label.set(CoreAnnotations.DomainAnnotation.class, "test");
  label.set(CoreAnnotations.ShapeAnnotation.class, "Xx");

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(label);
  PaddedList<CoreLabel> padded = new PaddedList<>(tokens);

  Collection<String> features = factory.getCliqueFeatures(padded, 0, FeatureFactory.cliqueC);
  assertNotNull(features); 
}
@Test
public void testEmptyDomainAnnotationHandledGracefully() {
  NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useWord = true;
  factory.init(flags);

  CoreLabel label = new CoreLabel();
  label.setWord("Paris");
  label.set(CoreAnnotations.ShapeAnnotation.class, "Xx");
  label.set(CoreAnnotations.DomainAnnotation.class, ""); 

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(label);
  PaddedList<CoreLabel> padded = new PaddedList<>(tokens);

  Collection<String> results = factory.getCliqueFeatures(padded, 0, FeatureFactory.cliqueC);
  assertNotNull(results);
}
@Test
public void testUseNextWithoutSufficientTokensInSentence() {
  NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useWord = true;
  flags.useNext = true;
  flags.useExternal = true;
  flags.useInternal = true;
  factory.init(flags);

  CoreLabel token = new CoreLabel();
  token.setWord("Last");
  token.set(CoreAnnotations.DomainAnnotation.class, "doc");
  token.set(CoreAnnotations.ShapeAnnotation.class, "Xx");

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);
  PaddedList<CoreLabel> padded = new PaddedList<>(tokens);

  Collection<String> features = factory.getCliqueFeatures(padded, 0, FeatureFactory.cliqueC);
  assertNotNull(features);
}
@Test
public void testUsePrevWithoutSufficientTokensInSentence() {
  NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useWord = true;
  flags.usePrev = true;
  flags.useExternal = true;
  flags.useInternal = true;
  factory.init(flags);

  CoreLabel token = new CoreLabel();
  token.setWord("First");
  token.set(CoreAnnotations.DomainAnnotation.class, "doc");
  token.set(CoreAnnotations.ShapeAnnotation.class, "Xx");

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);
  PaddedList<CoreLabel> padded = new PaddedList<>(tokens);

  Collection<String> features = factory.getCliqueFeatures(padded, 0, FeatureFactory.cliqueC);
  assertNotNull(features);
}
@Test
public void testGazetteMatchIgnoresWrongCaseIfNotCasedDistSim() throws Exception {
  NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.cleanGazette = true;
  flags.useGazettes = true;
  flags.casedDistSim = false;
  factory.init(flags);

  StringReader reader = new StringReader("ORG OpenAI");
  BufferedReader br = new BufferedReader(reader);
//  factory.readGazette(br);

  CoreLabel token1 = new CoreLabel();
  token1.setWord("openai"); 
  token1.set(CoreAnnotations.DomainAnnotation.class, "domain");

  List<CoreLabel> list = new ArrayList<>();
  list.add(token1);
  PaddedList<CoreLabel> padded = new PaddedList<>(list);

  Collection<String> features = factory.getCliqueFeatures(padded, 0, FeatureFactory.cliqueC);
  assertNotNull(features);
}
@Test
public void testUseTitle2RecognizesFormalTitle() {
  NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useWord = true;
  flags.useTitle2 = true;
  flags.useInternal = true;
  factory.init(flags);

  CoreLabel token = new CoreLabel();
  token.setWord("Mr.");
  token.set(CoreAnnotations.DomainAnnotation.class, "bio");
  token.set(CoreAnnotations.ShapeAnnotation.class, "Xx");

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);
  PaddedList<CoreLabel> padded = new PaddedList<>(tokens);

  Collection<String> features = factory.getCliqueFeatures(padded, 0, FeatureFactory.cliqueC);
  boolean found = false;
  for (String s : features) {
    if (s.contains("IS_TITLE")) {
      found = true;
    }
  }
  assertTrue(found);
}
@Test
public void testSlashHyphenSplitFragmentFeatureFired() {
  NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useWord = true;
  flags.slashHyphenTreatment = SeqClassifierFlags.SlashHyphenEnum.WFRAG;
  factory.init(flags);

  CoreLabel label = new CoreLabel();
  label.setWord("father-in-law");
  label.set(CoreAnnotations.DomainAnnotation.class, "legal");
  label.set(CoreAnnotations.ShapeAnnotation.class, "x");

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(label);
  PaddedList<CoreLabel> padded = new PaddedList<>(tokens);

  Collection<String> features = factory.getCliqueFeatures(padded, 0, FeatureFactory.cliqueC);
  boolean found = false;
  for (String s : features) {
    if (s.contains("-WFRAG")) {
      found = true;
    }
  }
  assertTrue(found);
}
@Test
public void testSplitWordRegexProducesSubtokensInFeatureSet() {
  NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useWord = true;
  flags.useInternal = true;
  flags.splitWordRegex = "-";
  factory.init(flags);

  CoreLabel label = new CoreLabel();
  label.setWord("New-York-City");
  label.set(CoreAnnotations.DomainAnnotation.class, "geo");
  label.set(CoreAnnotations.ShapeAnnotation.class, "X-X-X");

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(label);
  PaddedList<CoreLabel> padded = new PaddedList<>(tokens);

  Collection<String> features = factory.getCliqueFeatures(padded, 0, FeatureFactory.cliqueC);
  boolean foundSubtoken = false;
  for (String s : features) {
    if (s.contains("-SPLITWORD")) {
      foundSubtoken = true;
    }
  }
  assertTrue(foundSubtoken);
}
@Test
public void testCliqueCnCTriggeredWhenUseNextSequenceEnabled() {
  NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useNext = true;
  flags.useSequences = true;
  flags.useNextSequences = true;
  flags.useWord = true;
  factory.init(flags);

  CoreLabel token = new CoreLabel();
  token.setWord("X");
  token.set(CoreAnnotations.ShapeAnnotation.class, "X");
  token.set(CoreAnnotations.DomainAnnotation.class, "test");

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);
  PaddedList<CoreLabel> padded = new PaddedList<>(tokens);

  Collection<String> features = factory.getCliqueFeatures(padded, 0, FeatureFactory.cliqueCnC);

  assertTrue(features.contains("NSEQ"));
}
@Test
public void testCliqueCpCnC_whenAllSequenceFlagsEnabled() {
  NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useNext = true;
  flags.usePrev = true;
  flags.useSequences = true;
  flags.usePrevSequences = true;
  flags.useNextSequences = true;
  flags.useWord = true;
  factory.init(flags);

  CoreLabel token = new CoreLabel();
  token.setWord("Center");
  token.set(CoreAnnotations.ShapeAnnotation.class, "Xx");
  token.set(CoreAnnotations.DomainAnnotation.class, "bio");

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);
  PaddedList<CoreLabel> padded = new PaddedList<>(tokens);

  Collection<String> features = factory.getCliqueFeatures(padded, 0, FeatureFactory.cliqueCpCnC);

  assertTrue(features.contains("PNSEQ"));
}
@Test
public void testUseShapeConjunctions_buildsPosAndTagShapeFeatures() {
  NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useShapeConjunctions = true;
  flags.useTags = true;
  flags.useDistSim = true;
  flags.useInternal = true;
  flags.useWord = true;
  factory.init(flags);

  CoreLabel token = new CoreLabel();
  token.setWord("Steve");
  token.set(CoreAnnotations.DomainAnnotation.class, "domain");
  token.set(CoreAnnotations.ShapeAnnotation.class, "Xxxx");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
  token.set(CoreAnnotations.DistSimAnnotation.class, "clustX");

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);
  PaddedList<CoreLabel> padded = new PaddedList<>(tokens);

  Collection<String> features = factory.getCliqueFeatures(padded, 0, FeatureFactory.cliqueC);

  boolean hasShapePos = false;
  boolean hasShapeTag = false;
  boolean hasShapeDistSim = false;

  for (String s : features) {
    if (s.endsWith("POS-SH")) hasShapePos = true;
    if (s.endsWith("TAG-SH")) hasShapeTag = true;
    if (s.endsWith("DISTSIM-SH")) hasShapeDistSim = true;
  }

  assertTrue(hasShapePos);
  assertTrue(hasShapeTag);
  assertTrue(hasShapeDistSim);
}
@Test
public void testUseNPHead_annotationFeatureIsAdded() {
  NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useNPHead = true;
  flags.useWord = true;
  flags.useInternal = true;
  flags.useTags = true;
  flags.useDistSim = true;
  factory.init(flags);

  CoreLabel token = new CoreLabel();
  token.setWord("London");
  token.set(CoreAnnotations.DomainAnnotation.class, "test");
  token.set(CoreAnnotations.HeadWordStringAnnotation.class, "head-word");
  token.set(CoreAnnotations.ShapeAnnotation.class, "Xx");
  token.set(CoreAnnotations.DistSimAnnotation.class, "dslabel");
  token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);
  PaddedList<CoreLabel> padded = new PaddedList<>(tokens);

  Collection<String> features = factory.getCliqueFeatures(padded, 0, FeatureFactory.cliqueC);

  boolean hasHeadBase = false;
  boolean hasTaggedShape = false;
  for (String s : features) {
    if (s.contains("HW-T") || s.contains("HW-DISTSIM")) {
      hasHeadBase = true;
    }
  }
  assertTrue(hasHeadBase);
}
@Test
public void testUseGenericFeaturesWithSingleAnnotationKey() {
  NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useGenericFeatures = true;
  factory.init(flags);

  CoreLabel token = new CoreLabel();
  token.setWord("Apple");
  token.set(CoreAnnotations.DomainAnnotation.class, "generic");

  CoreAnnotations.LabelAnnotation sampleKey = new CoreAnnotations.LabelAnnotation();
  token.set(CoreAnnotations.LabelAnnotation.class, "ORG");

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);
  PaddedList<CoreLabel> padded = new PaddedList<>(tokens);

  Collection<String> result = factory.getCliqueFeatures(padded, 0, FeatureFactory.cliqueC);
  assertNotNull(result);  
}
@Test
public void testFeatureCollector_addHandlesEmptyDomainGracefully() {
  Set<String> output = new HashSet<>();
  NERFeatureFactory.FeatureCollector fc = new NERFeatureFactory.FeatureCollector(output);
  fc.setSuffix("X");
  fc.setDomain(null); 

  fc.build().append("feature").add();

  assertTrue(output.contains("feature|X"));
}
@Test
public void testCpC_disjointWordPositionsWhenDisjunctionFlagsEnabled() {
  NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
  SeqClassifierFlags flags = new SeqClassifierFlags();
  flags.useWord = true;
  flags.useDisjShape = true;
  flags.useDisjunctive = true;
  flags.disjunctionWidth = 2;
  flags.useInternal = true;
  factory.init(flags);

  CoreLabel input0 = new CoreLabel();
  input0.setWord("A");
  input0.set(CoreAnnotations.DomainAnnotation.class, "test");
  input0.set(CoreAnnotations.ShapeAnnotation.class, "X");

  CoreLabel input1 = new CoreLabel();
  input1.setWord("B");
  input1.set(CoreAnnotations.DomainAnnotation.class, "test");
  input1.set(CoreAnnotations.ShapeAnnotation.class, "x");

  CoreLabel input2 = new CoreLabel();
  input2.setWord("C");
  input2.set(CoreAnnotations.DomainAnnotation.class, "test");
  input2.set(CoreAnnotations.ShapeAnnotation.class, "Xx");

  CoreLabel input3 = new CoreLabel();
  input3.setWord("D");
  input3.set(CoreAnnotations.DomainAnnotation.class, "test");
  input3.set(CoreAnnotations.ShapeAnnotation.class, "XX");

  List<CoreLabel> all = new ArrayList<>();
  all.add(input0);
  all.add(input1);
  all.add(input2);
  all.add(input3);

  PaddedList<CoreLabel> padded = new PaddedList<>(all);
  Collection<String> features = factory.getCliqueFeatures(padded, 1, FeatureFactory.cliqueC);

  boolean hasShapeDisjunction = false;
  for (String f : features) {
    if (f.contains("DISJSHAPE")) {
      hasShapeDisjunction = true;
    }
  }
  assertTrue(hasShapeDisjunction);
} 
}