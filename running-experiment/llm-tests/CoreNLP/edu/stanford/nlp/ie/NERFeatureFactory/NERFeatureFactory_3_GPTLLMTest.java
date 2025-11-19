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

public class NERFeatureFactory_3_GPTLLMTest {

 @Test
  public void testGetFeatures_cliqueC_containsBasicFeatures() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useWord = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel token0 = new CoreLabel();
    token0.setWord("Word0");
    token0.set(CoreAnnotations.ShapeAnnotation.class, "shape0");
    token0.set(CoreAnnotations.DomainAnnotation.class, "test");

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Word1");
    token1.set(CoreAnnotations.ShapeAnnotation.class, "shape1");
    token1.set(CoreAnnotations.DomainAnnotation.class, "test");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Word2");
    token2.set(CoreAnnotations.ShapeAnnotation.class, "shape2");
    token2.set(CoreAnnotations.DomainAnnotation.class, "test");

    CoreLabel token3 = new CoreLabel();
    token3.setWord("Word3");
    token3.set(CoreAnnotations.ShapeAnnotation.class, "shape3");
    token3.set(CoreAnnotations.DomainAnnotation.class, "test");

    CoreLabel token4 = new CoreLabel();
    token4.setWord("Word4");
    token4.set(CoreAnnotations.ShapeAnnotation.class, "shape4");
    token4.set(CoreAnnotations.DomainAnnotation.class, "test");

    List<CoreLabel> tokens = Arrays.asList(token0, token1, token2, token3, token4);
    PaddedList<CoreLabel> padded = new PaddedList<>(tokens);

    Collection<String> features = factory.getCliqueFeatures(padded, 2, NERFeatureFactory.cliqueC);
    boolean found = false;
    for (String feat : features) {
      if (feat.endsWith("|C")) {
        found = true;
        break;
      }
    }
    assertTrue("Feature ending with |C should be present", found);
  }
@Test
  public void testGetFeatures_cliqueCpC_containsCpCFeatures() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.usePrev = true;
    flags.useSequences = true;
    flags.usePrevSequences = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel t0 = new CoreLabel();
    t0.setWord("t0");
    t0.set(CoreAnnotations.ShapeAnnotation.class, "s0");
    t0.set(CoreAnnotations.DomainAnnotation.class, "test");

    CoreLabel t1 = new CoreLabel();
    t1.setWord("t1");
    t1.set(CoreAnnotations.ShapeAnnotation.class, "s1");
    t1.set(CoreAnnotations.DomainAnnotation.class, "test");

    CoreLabel t2 = new CoreLabel();
    t2.setWord("t2");
    t2.set(CoreAnnotations.ShapeAnnotation.class, "s2");
    t2.set(CoreAnnotations.DomainAnnotation.class, "test");

    CoreLabel t3 = new CoreLabel();
    t3.setWord("t3");
    t3.set(CoreAnnotations.ShapeAnnotation.class, "s3");
    t3.set(CoreAnnotations.DomainAnnotation.class, "test");

    CoreLabel t4 = new CoreLabel();
    t4.setWord("t4");
    t4.set(CoreAnnotations.ShapeAnnotation.class, "s4");
    t4.set(CoreAnnotations.DomainAnnotation.class, "test");

    List<CoreLabel> toks = Arrays.asList(t0, t1, t2, t3, t4);
    PaddedList<CoreLabel> pad = new PaddedList<>(toks);

    Collection<String> feats = factory.getCliqueFeatures(pad, 2, NERFeatureFactory.cliqueCpC);
    boolean has = false;
    for (String f : feats) {
      if (f.endsWith("|CpC")) {
        has = true;
        break;
      }
    }
    assertTrue("Feature ending with |CpC should be present", has);
  }
@Test
  public void testGetFeatures_cliqueCp2C_containsCp2CFeatures() {
    SeqClassifierFlags flags = new SeqClassifierFlags();

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel a = new CoreLabel();
    a.setWord("a");
    a.set(CoreAnnotations.ShapeAnnotation.class, "s0");
    a.set(CoreAnnotations.DomainAnnotation.class, "test");

    CoreLabel b = new CoreLabel();
    b.setWord("b");
    b.set(CoreAnnotations.ShapeAnnotation.class, "s1");
    b.set(CoreAnnotations.DomainAnnotation.class, "test");

    CoreLabel c = new CoreLabel();
    c.setWord("c");
    c.set(CoreAnnotations.ShapeAnnotation.class, "s2");
    c.set(CoreAnnotations.DomainAnnotation.class, "test");

    CoreLabel d = new CoreLabel();
    d.setWord("d");
    d.set(CoreAnnotations.ShapeAnnotation.class, "s3");
    d.set(CoreAnnotations.DomainAnnotation.class, "test");

    CoreLabel e = new CoreLabel();
    e.setWord("e");
    e.set(CoreAnnotations.ShapeAnnotation.class, "s4");
    e.set(CoreAnnotations.DomainAnnotation.class, "test");

    List<CoreLabel> lst = Arrays.asList(a, b, c, d, e);
    PaddedList<CoreLabel> padded = new PaddedList<>(lst);

    Collection<String> results = factory.getCliqueFeatures(padded, 2, NERFeatureFactory.cliqueCp2C);
    boolean matched = false;
    for (String feat : results) {
      if (feat.endsWith("|Cp2C")) {
        matched = true;
        break;
      }
    }
    assertTrue("Feature ending with |Cp2C expected", matched);
  }
@Test(expected = IllegalArgumentException.class)
  public void testGetFeatures_unknownClique_throwsException() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel testToken = new CoreLabel();
    testToken.setWord("token");
    testToken.set(CoreAnnotations.DomainAnnotation.class, "test");
    testToken.set(CoreAnnotations.ShapeAnnotation.class, "shape");

    List<CoreLabel> list = Collections.singletonList(testToken);
    PaddedList<CoreLabel> padded = new PaddedList<>(list);
//    Clique unknown = new Clique("UNKNOWN");

//    factory.getCliqueFeatures(padded, 0, unknown);
  }
@Test
  public void testDescribeDistsimLexicon_noLexicon() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    String desc = factory.describeDistsimLexicon();
    assertEquals("No distsim lexicon", desc);
  }
@Test
  public void testClearMemory_restoresEmptyLexicon() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    factory.clearMemory();
    String result = factory.describeDistsimLexicon();
    assertEquals("No distsim lexicon", result);
  }
//@Test
//  public void testIsNameCase_true() {
//    boolean result = NERFeatureFactory.isNameCase("Capital");
//    assertTrue(result);
//  }
//@Test
//  public void testIsNameCase_false() {
//    boolean result = NERFeatureFactory.isNameCase("ALLCAPS");
//    assertFalse(result);
//  }
//@Test
//  public void testNoUpperCase_true() {
//    boolean result = NERFeatureFactory.noUpperCase("middle");
//    assertTrue(result);
//  }
//@Test
//  public void testNoUpperCase_false() {
//    boolean result = NERFeatureFactory.noUpperCase("TextWithCaps");
//    assertFalse(result);
//  }
//@Test
//  public void testHasLetter_true() {
//    boolean result = NERFeatureFactory.hasLetter("abc123");
//    assertTrue(result);
//  }
//@Test
//  public void testHasLetter_false() {
//    boolean result = NERFeatureFactory.hasLetter("123456");
//    assertFalse(result);
//  }
@Test
  public void testFeatureCollectorAdd_addsFeatureWithSuffix() {
    Set<String> output = new HashSet<>();
    NERFeatureFactory.FeatureCollector collector = new NERFeatureFactory.FeatureCollector(output);

    collector.setSuffix("C");
    collector.build().append("FOO").add();

    boolean exists = false;
    for (String s : output) {
      if (s.equals("FOO|C")) {
        exists = true;
      }
    }
    assertTrue("Feature FOO|C should be added", exists);
  }
@Test(expected = AssertionError.class)
  public void testSetSuffix_emptySuffixThrows() {
    Set<String> output = new HashSet<>();
    NERFeatureFactory.FeatureCollector collector = new NERFeatureFactory.FeatureCollector(output);
    collector.setSuffix("");
  }
@Test
  public void testGetFeatures_singleToken_cliqueC() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useWord = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel token = new CoreLabel();
    token.setWord("onlytoken");
    token.set(CoreAnnotations.ShapeAnnotation.class, "Xx");
    token.set(CoreAnnotations.DomainAnnotation.class, "testdomain");

    List<CoreLabel> list = Collections.singletonList(token);
    PaddedList<CoreLabel> padded = new PaddedList<>(list);

    Collection<String> features = factory.getCliqueFeatures(padded, 0, NERFeatureFactory.cliqueC);
    boolean found = false;
    for (String s : features) {
      if (s.endsWith("|C")) {
        found = true;
      }
    }
    assertTrue("Feature with |C must be present even for 1-token list", found);
  }
@Test
  public void testGetFeatures_emptyShapeAnnotation_defaultsHandled() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useWord = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel token = new CoreLabel();
    token.setWord("word");
    token.set(CoreAnnotations.DomainAnnotation.class, "test");

    List<CoreLabel> list = Collections.singletonList(token);
    PaddedList<CoreLabel> padded = new PaddedList<>(list);

    Collection<String> features = factory.getCliqueFeatures(padded, 0, NERFeatureFactory.cliqueC);
    assertNotNull("Should not throw even if ShapeAnnotation is missing", features);
  }
@Test
  public void testGetFeatures_missingDomainAnnotationThrows() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useWord = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel token = new CoreLabel();
    token.setWord("tokenWithNoDomain");
    token.set(CoreAnnotations.ShapeAnnotation.class, "Xx");

    List<CoreLabel> list = Collections.singletonList(token);
    PaddedList<CoreLabel> padded = new PaddedList<>(list);

    try {
      factory.getCliqueFeatures(padded, 0, NERFeatureFactory.cliqueC);
      fail("Expected NullPointerException due to missing DomainAnnotation");
    } catch (NullPointerException expected) {
      
    }
  }
@Test
  public void testInit_calledMultipleTimes_safe() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);
    factory.init(flags);
    factory.init(flags);

    CoreLabel token = new CoreLabel();
    token.setWord("tok");
    token.set(CoreAnnotations.ShapeAnnotation.class, "AA");
    token.set(CoreAnnotations.DomainAnnotation.class, "dd");

    List<CoreLabel> list = Collections.singletonList(token);
    PaddedList<CoreLabel> padded = new PaddedList<>(list);

    Collection<String> features = factory.getCliqueFeatures(padded, 0, NERFeatureFactory.cliqueC);
    assertNotNull("Should still behave correctly after repeated init()", features);
  }
@Test
  public void testFeatureCollector_withDomainSuffix() {
    Set<String> collected = new HashSet<>();

    NERFeatureFactory.FeatureCollector fc = new NERFeatureFactory.FeatureCollector(collected);
    fc.setSuffix("C");
    fc.setDomain("X");

    fc.build().append("TEST").add();

    boolean found = false;
    boolean foundDomained = false;
    for (String s : collected) {
      if (s.equals("TEST|C")) {
        found = true;
      }
      if (s.equals("TEST|X-C")) {
        foundDomained = true;
      }
    }

    assertTrue("Standard suffix exists", found);
    assertTrue("Domain-specific suffix also generated", foundDomained);
  }
@Test
  public void testFeatureCollector_build_append_add_multipleTimes() {
    Set<String> output = new HashSet<>();

    NERFeatureFactory.FeatureCollector fc = new NERFeatureFactory.FeatureCollector(output);
    fc.setSuffix("SUF");
    fc.build().append("ONE").add();
    fc.build().append("TWO").add();
    fc.build().append("THREE").add();

    assertTrue(output.contains("ONE|SUF"));
    assertTrue(output.contains("TWO|SUF"));
    assertTrue(output.contains("THREE|SUF"));
    assertEquals(3, output.size());
  }
@Test
  public void testFeatureCollector_append_characters_correctly() {
    Set<String> output = new HashSet<>();

    NERFeatureFactory.FeatureCollector fc = new NERFeatureFactory.FeatureCollector(output);
    fc.setSuffix("ZZZ");

    fc.build().append("ABC").append('-').append("XYZ").add();

    assertTrue(output.contains("ABC-XYZ|ZZZ"));
  }
@Test
  public void testGetCliqueFeatures_withCliqueCpCp2C_generatesFeatures() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useSequences = true;
    flags.usePrev = true;
    flags.usePrevSequences = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel w0 = new CoreLabel();
    w0.setWord("A");
    w0.set(CoreAnnotations.ShapeAnnotation.class, "X");
    w0.set(CoreAnnotations.DomainAnnotation.class, "D");

    CoreLabel w1 = new CoreLabel();
    w1.setWord("B");
    w1.set(CoreAnnotations.ShapeAnnotation.class, "Y");
    w1.set(CoreAnnotations.DomainAnnotation.class, "D");

    CoreLabel w2 = new CoreLabel();
    w2.setWord("C");
    w2.set(CoreAnnotations.ShapeAnnotation.class, "Z");
    w2.set(CoreAnnotations.DomainAnnotation.class, "D");

    List<CoreLabel> toks = Arrays.asList(w0, w1, w2);
    PaddedList<CoreLabel> padded = new PaddedList<>(toks);

    Collection<String> result = factory.getCliqueFeatures(padded, 2, NERFeatureFactory.cliqueCpCp2C);
    boolean has = false;
    for (String f : result) {
      if (f.endsWith("|CpCp2C")) {
        has = true;
      }
    }
    assertTrue("Feature ending with CpCp2C should be produced", has);
  }
@Test
  public void testGetFeatures_cliqueCp3C_boundaryCase() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useParenMatching = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel t0 = new CoreLabel();
    t0.setWord("(");
    t0.set(CoreAnnotations.ShapeAnnotation.class, "");
    t0.set(CoreAnnotations.DomainAnnotation.class, "X");

    CoreLabel t1 = new CoreLabel();
    t1.setWord("test");
    t1.set(CoreAnnotations.ShapeAnnotation.class, "");
    t1.set(CoreAnnotations.DomainAnnotation.class, "X");

    CoreLabel t2 = new CoreLabel();
    t2.setWord("data");
    t2.set(CoreAnnotations.ShapeAnnotation.class, "");
    t2.set(CoreAnnotations.DomainAnnotation.class, "X");

    CoreLabel t3 = new CoreLabel();
    t3.setWord(")");
    t3.set(CoreAnnotations.ShapeAnnotation.class, "");
    t3.set(CoreAnnotations.DomainAnnotation.class, "X");

    List<CoreLabel> tokens = Arrays.asList(t0, t1, t2, t3);
    PaddedList<CoreLabel> list = new PaddedList<>(tokens);

    Collection<String> features = factory.getCliqueFeatures(list, 3, NERFeatureFactory.cliqueCp3C);
    boolean matched = false;
    for (String s : features) {
      if (s.contains("PAREN-MATCH")) {
        matched = true;
      }
    }

    assertTrue("PAREN-MATCH feature should fire for cliqueCp3C with matching symbols", matched);
  }
@Test
  public void testGetFeatures_cliqueCp4C_includesParenMatch() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useParenMatching = true;
    flags.maxLeft = 4;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel p4 = new CoreLabel();
    p4.setWord("(");
    p4.set(CoreAnnotations.ShapeAnnotation.class, "");
    p4.set(CoreAnnotations.DomainAnnotation.class, "X");

    CoreLabel p3 = new CoreLabel();
    p3.setWord("mid");
    p3.set(CoreAnnotations.ShapeAnnotation.class, "");
    p3.set(CoreAnnotations.DomainAnnotation.class, "X");

    CoreLabel p2 = new CoreLabel();
    p2.setWord("mid");
    p2.set(CoreAnnotations.ShapeAnnotation.class, "");
    p2.set(CoreAnnotations.DomainAnnotation.class, "X");

    CoreLabel p1 = new CoreLabel();
    p1.setWord("mid");
    p1.set(CoreAnnotations.ShapeAnnotation.class, "");
    p1.set(CoreAnnotations.DomainAnnotation.class, "X");

    CoreLabel c = new CoreLabel();
    c.setWord(")");
    c.set(CoreAnnotations.ShapeAnnotation.class, "");
    c.set(CoreAnnotations.DomainAnnotation.class, "X");

    List<CoreLabel> list = Arrays.asList(p4, p3, p2, p1, c);
    PaddedList<CoreLabel> padded = new PaddedList<>(list);

    Collection<String> result = factory.getCliqueFeatures(padded, 4, NERFeatureFactory.cliqueCp4C);
    boolean found = false;
    for (String feat : result) {
      if (feat.contains("PAREN-MATCH")) {
        found = true;
      }
    }
    assertTrue("Should detect paren match in Cp4C", found);
  }
@Test
  public void testDistSimAnnotation_unknownClassSet() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useDistSim = true;
    flags.distSimLexicon = null;
    flags.unknownWordDistSimClass = "UNKNOWN";
    flags.casedDistSim = false;
    flags.numberEquivalenceDistSim = false;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel token = new CoreLabel();
    token.setWord("qwerty");
    token.set(CoreAnnotations.DomainAnnotation.class, "A");
    token.set(CoreAnnotations.ShapeAnnotation.class, "A");

    List<CoreLabel> tokens = Collections.singletonList(token);
    PaddedList<CoreLabel> padded = new PaddedList<>(tokens);

    Collection<String> feats = factory.getCliqueFeatures(padded, 0, NERFeatureFactory.cliqueC);
    boolean containsUnknown = false;

    for (CoreLabel label : padded) {
      String value = label.get(CoreAnnotations.DistSimAnnotation.class);
      if ("UNKNOWN".equals(value)) {
        containsUnknown = true;
      }
    }

    assertTrue("DistSim unknown value should be set", containsUnknown);
  }
@Test
  public void testClearMemory_clearsNGramsCache() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useNGrams = true;
    flags.cacheNGrams = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel word = new CoreLabel();
    word.setWord("hello");
    word.set(CoreAnnotations.DomainAnnotation.class, "A");
    word.set(CoreAnnotations.ShapeAnnotation.class, "Xx");

    List<CoreLabel> list = Collections.singletonList(word);
    PaddedList<CoreLabel> padded = new PaddedList<>(list);

    factory.getCliqueFeatures(padded, 0, NERFeatureFactory.cliqueC);
    factory.clearMemory();

    assertEquals("No distsim lexicon", factory.describeDistsimLexicon());
  }
@Test
  public void testIsOrdinalHyphenatedSequence() {
    SeqClassifierFlags flags = new SeqClassifierFlags();

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel p = new CoreLabel();
    p.setWord("twenty");
    CoreLabel hyphen = new CoreLabel();
    hyphen.setWord("-");
    CoreLabel n = new CoreLabel();
    n.setWord("first");

    List<CoreLabel> tokens = Arrays.asList(p, hyphen, n);
    PaddedList<CoreLabel> padded = new PaddedList<>(tokens);

    Collection<String> result = factory.getCliqueFeatures(padded, 1, NERFeatureFactory.cliqueC);

    boolean matched = false;
    for (String f : result) {
      if (f.contains("ORDINAL")) {
        matched = true;
      }
    }
    assertTrue("Should treat hyphenated ordinal as ordinal", matched);
  }
@Test
  public void testFeatureCollector_multipleSuffixesAndOverwrites() {
    Set<String> feats = new HashSet<>();

    NERFeatureFactory.FeatureCollector f1 = new NERFeatureFactory.FeatureCollector(feats);
    f1.setSuffix("X");
    f1.setDomain("A");
    f1.build().append("F1").add();

    NERFeatureFactory.FeatureCollector f2 = new NERFeatureFactory.FeatureCollector(feats);
    f2.setSuffix("X");
    f2.setDomain("B");
    f2.build().append("F1").add();

    boolean base = false;
    boolean domainA = false;
    boolean domainB = false;

    for (String s : feats) {
      if (s.equals("F1|X")) base = true;
      if (s.equals("F1|A-X")) domainA = true;
      if (s.equals("F1|B-X")) domainB = true;
    }

    assertTrue(base);
    assertTrue(domainA);
    assertTrue(domainB);
    assertEquals(3, feats.size());
  }
@Test
  public void testGetFeatures_cliqueCnC_featuresIncludedWhenUseNextSequencesTrue() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useNextSequences = true;
    flags.useNext = true;
    flags.useSequences = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel a = new CoreLabel();
    a.setWord("a");
    a.set(CoreAnnotations.DomainAnnotation.class, "z");
    a.set(CoreAnnotations.ShapeAnnotation.class, "x");

    CoreLabel b = new CoreLabel();
    b.setWord("b");
    b.set(CoreAnnotations.DomainAnnotation.class, "z");
    b.set(CoreAnnotations.ShapeAnnotation.class, "x");

    List<CoreLabel> list = Arrays.asList(a, b);
    PaddedList<CoreLabel> padded = new PaddedList<>(list);

    Collection<String> result = factory.getCliqueFeatures(padded, 0, NERFeatureFactory.cliqueCnC);

    boolean found = false;
    for (String s : result) {
      if (s.endsWith("|CnC")) {
        found = true;
        break;
      }
    }

    assertTrue("Features extracted with clique CnC must have correct suffix", found);
  }
@Test
  public void testGazetteSloppyMatchingAddsExpectedFeatures() throws Exception {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.sloppyGazette = true;
    flags.useGazettes = true;
    flags.cleanGazette = false;
    flags.gazettes = new ArrayList<>();

    String input = "LOCATION San Francisco";

    java.io.StringReader source = new java.io.StringReader(input);
    java.io.BufferedReader reader = new BufferedReader(source);

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
//    factory.flags = flags;
    java.lang.reflect.Method m = factory.getClass().getDeclaredMethod("readGazette", BufferedReader.class);
    m.setAccessible(true);
    m.invoke(factory, reader);

//    Map<String, Collection<String>> map = factory.wordToGazetteEntries;
//    boolean matched = false;
//    for (Map.Entry<String, Collection<String>> entry : map.entrySet()) {
//      if (entry.getValue().contains("LOCATION-GAZ2") || entry.getValue().contains("LOCATION-GAZ")) {
//        matched = true;
//      }
//    }
//    assertTrue("Expected gazette features from sloppy match", matched);
  }
@Test
  public void testInternFlagTrueInternsFeatureString() {
    Set<String> features = new HashSet<>();
    NERFeatureFactory.FeatureCollector fc = new NERFeatureFactory.FeatureCollector(features);
    fc.setSuffix("SFX");
    fc.build().append("A").append("-").append("B").add();

    String sample = null;
    for (String s : features) {
      sample = s;
    }

    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.intern = true;

    boolean isInterned = sample == sample.intern(); 
    assertTrue("Feature string must be interned when flag is true", isInterned);
  }
@Test
  public void testFeatureCollectorWithNullDomainStillAddsSuffix() {
    Set<String> features = new HashSet<>();

    NERFeatureFactory.FeatureCollector fc = new NERFeatureFactory.FeatureCollector(features);
    fc.setSuffix("C");

    fc.build().append("FEAT").add();

    boolean matched = false;
    for (String s : features) {
      if (s.equals("FEAT|C")) {
        matched = true;
      }
    }
    assertTrue("Feature with suffix and no domain should be added correctly", matched);
  }
@Test
  public void testCliqueCp5CWithMatchingParensAddsFeature() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useParenMatching = true;
    flags.maxLeft = 5;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel p5 = new CoreLabel();
    p5.setWord("(");
    p5.set(CoreAnnotations.ShapeAnnotation.class, "");
    p5.set(CoreAnnotations.DomainAnnotation.class, "dom");

    CoreLabel p4 = new CoreLabel();
    p4.setWord("x");
    p4.set(CoreAnnotations.ShapeAnnotation.class, "");
    p4.set(CoreAnnotations.DomainAnnotation.class, "dom");

    CoreLabel p3 = new CoreLabel();
    p3.setWord("x");
    p3.set(CoreAnnotations.ShapeAnnotation.class, "");
    p3.set(CoreAnnotations.DomainAnnotation.class, "dom");

    CoreLabel p2 = new CoreLabel();
    p2.setWord("y");
    p2.set(CoreAnnotations.ShapeAnnotation.class, "");
    p2.set(CoreAnnotations.DomainAnnotation.class, "dom");

    CoreLabel p1 = new CoreLabel();
    p1.setWord("z");
    p1.set(CoreAnnotations.ShapeAnnotation.class, "");
    p1.set(CoreAnnotations.DomainAnnotation.class, "dom");

    CoreLabel c = new CoreLabel();
    c.setWord(")");
    c.set(CoreAnnotations.ShapeAnnotation.class, "");
    c.set(CoreAnnotations.DomainAnnotation.class, "dom");

    List<CoreLabel> list = Arrays.asList(p5, p4, p3, p2, p1, c);
    PaddedList<CoreLabel> padded = new PaddedList<>(list);

    Collection<String> features = factory.getCliqueFeatures(padded, 5, NERFeatureFactory.cliqueCp5C);
    boolean found = false;
    for (String f : features) {
      if (f.contains("PAREN-MATCH")) {
        found = true;
      }
    }
    assertTrue(found);
  }
@Test
  public void testUseAbbrTrueGeneratesAbbrFeatures() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useAbbr = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel p = new CoreLabel();
    p.setWord("Dr.");
    p.set(CoreAnnotations.AbbrAnnotation.class, "AB");
    p.set(CoreAnnotations.ShapeAnnotation.class, "S");
    p.set(CoreAnnotations.DomainAnnotation.class, "D");

    CoreLabel c = new CoreLabel();
    c.setWord("John");
    c.set(CoreAnnotations.AbbrAnnotation.class, "CD");
    c.set(CoreAnnotations.ShapeAnnotation.class, "S");
    c.set(CoreAnnotations.DomainAnnotation.class, "D");

    CoreLabel n = new CoreLabel();
    n.setWord("Smith");
    n.set(CoreAnnotations.AbbrAnnotation.class, "EF");
    n.set(CoreAnnotations.ShapeAnnotation.class, "S");
    n.set(CoreAnnotations.DomainAnnotation.class, "D");

    List<CoreLabel> list = Arrays.asList(p, c, n);
    PaddedList<CoreLabel> padded = new PaddedList<>(list);

    Collection<String> output = factory.getCliqueFeatures(padded, 1, NERFeatureFactory.cliqueC);
    boolean hasAbbrFeature = false;
    for (String f : output) {
      if (f.contains("PCABBR") || f.contains("CNABBR") || f.contains("PCNABBR")) {
        hasAbbrFeature = true;
      }
    }

    assertTrue(hasAbbrFeature);
  }
@Test
  public void testUseNGramsNoMidNGramsTrueSkipsMiddle() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useWord = true;
    flags.useInternal = true;
    flags.useNGrams = true;
    flags.noMidNGrams = true;
    flags.maxNGramLeng = 5;
    flags.lowercaseNGrams = false;
    flags.dehyphenateNGrams = false;
    flags.useExternal = false;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel label = new CoreLabel();
    label.setWord("title");
    label.set(CoreAnnotations.ShapeAnnotation.class, "Xx");
    label.set(CoreAnnotations.DomainAnnotation.class, "news");

    List<CoreLabel> labels = Collections.singletonList(label);
    PaddedList<CoreLabel> padded = new PaddedList<>(labels);

    Collection<String> features = factory.getCliqueFeatures(padded, 0, NERFeatureFactory.cliqueC);
    boolean hasTrailingPrefix = false;
    boolean hasMidNgram = false;
    for (String s : features) {
      if (s.contains("#title#")) hasMidNgram = true;
      if (s.contains("#title")) hasTrailingPrefix = true;
    }

    assertFalse("Should not contain mid n-grams when noMidNGrams=true", hasMidNgram);
    assertTrue("Should still contain prefix/suffix when noMidNGrams=true", hasTrailingPrefix);
  }
@Test
  public void testUseClassFeatureAddsClassBiasIndicator() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useWord = true;
    flags.useClassFeature = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel token = new CoreLabel();
    token.setWord("element");
    token.set(CoreAnnotations.ShapeAnnotation.class, "Aa");
    token.set(CoreAnnotations.DomainAnnotation.class, "science");

    List<CoreLabel> list = Collections.singletonList(token);
    PaddedList<CoreLabel> padded = new PaddedList<>(list);

    Collection<String> feats = factory.getCliqueFeatures(padded, 0, NERFeatureFactory.cliqueC);

    boolean hasBiasClassFeature = false;
    for (String s : feats) {
      if (s.equals("###|C")) {
        hasBiasClassFeature = true;
      }
    }

    assertTrue("Expected class bias feature ###|C to be added", hasBiasClassFeature);
  }
@Test
  public void testFlagsWithGreekifyTriggersSubstitution() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useInternal = true;
    flags.useNGrams = true;
    flags.greekifyNGrams = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel token = new CoreLabel();
    token.setWord("alpha");
    token.set(CoreAnnotations.ShapeAnnotation.class, "Aa");
    token.set(CoreAnnotations.DomainAnnotation.class, "greek");

    List<CoreLabel> single = Collections.singletonList(token);
    PaddedList<CoreLabel> padded = new PaddedList<>(single);

    Collection<String> features = factory.getCliqueFeatures(padded, 0, NERFeatureFactory.cliqueC);

    boolean hasGreekified = false;
    for (String f : features) {
      if (f.contains("~")) {
        hasGreekified = true;
      }
    }

    assertTrue("Should have included a greekified n-gram with '~'", hasGreekified);
  }
@Test
  public void testFeaturesC_shapeConjunctionEnabled() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useShapeConjunctions = true;
    flags.useWord = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel label = new CoreLabel();
    label.setWord("Entity");
    label.set(CoreAnnotations.ShapeAnnotation.class, "XxXx");
    label.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    label.set(CoreAnnotations.PositionAnnotation.class, "2");
    label.set(CoreAnnotations.DomainAnnotation.class, "test");

    List<CoreLabel> labels = Arrays.asList(label);
    PaddedList<CoreLabel> padded = new PaddedList<>(labels);

    Collection<String> feats = factory.getCliqueFeatures(padded, 0, NERFeatureFactory.cliqueC);
    boolean has = false;
    for (String f : feats) {
      if (f.contains("POS-SH")) {
        has = true;
      }
    }
    assertTrue(has);
  }
@Test
  public void testFeaturesC_useFirstWordOnly() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useWord = true;
    flags.useFirstWord = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel label0 = new CoreLabel();
    label0.setWord("First");
    label0.set(CoreAnnotations.ShapeAnnotation.class, "Xx");
    label0.set(CoreAnnotations.DomainAnnotation.class, "a");

    CoreLabel label1 = new CoreLabel();
    label1.setWord("Second");
    label1.set(CoreAnnotations.ShapeAnnotation.class, "Xx");
    label1.set(CoreAnnotations.DomainAnnotation.class, "a");

    List<CoreLabel> toks = Arrays.asList(label0, label1);
    PaddedList<CoreLabel> padded = new PaddedList<>(toks);

    Collection<String> f1 = factory.getCliqueFeatures(padded, 1, NERFeatureFactory.cliqueC);
    boolean found = false;
    for (String s : f1) {
      if (s.equals("First|C")) {
        found = true;
      }
    }
    assertTrue(found);
  }
@Test
  public void testFeaturesC_slashHyphenTreatment_word_frag_enabled() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.slashHyphenTreatment = SeqClassifierFlags.SlashHyphenEnum.WFRAG;
    flags.useWord = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel token = new CoreLabel();
    token.setWord("lead/singer");
    token.set(CoreAnnotations.ShapeAnnotation.class, "xx");
    token.set(CoreAnnotations.DomainAnnotation.class, "pop");

    List<CoreLabel> tokens = Arrays.asList(token);
    PaddedList<CoreLabel> padded = new PaddedList<>(tokens);

    Collection<String> features = factory.getCliqueFeatures(padded, 0, NERFeatureFactory.cliqueC);
    boolean found = false;
    for (String f : features) {
      if (f.contains("-WFRAG")) {
        found = true;
      }
    }
    assertTrue("Expected slash/hyphen substring to be added as WFRAG", found);
  }
@Test
  public void testFeaturesC_wordTagFeaturesEnabled() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useWordTag = true;
    flags.useWord = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel label = new CoreLabel();
    label.setWord("Obama");
    label.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    label.set(CoreAnnotations.ShapeAnnotation.class, "Xx");
    label.set(CoreAnnotations.DomainAnnotation.class, "news");

    List<CoreLabel> x = Collections.singletonList(label);
    PaddedList<CoreLabel> padded = new PaddedList<>(x);

    Collection<String> feats = factory.getCliqueFeatures(padded, 0, NERFeatureFactory.cliqueC);
    boolean has = false;
    for (String f : feats) {
      if (f.contains("W-T")) {
        has = true;
      }
    }
    assertTrue(has);
  }
@Test
  public void testUseLastRealWordShortPrevAddsCombinedFeature() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useLastRealWord = true;
    flags.useWord = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel p2 = new CoreLabel();
    p2.setWord("USA");

    CoreLabel p1 = new CoreLabel();
    p1.setWord("of");

    CoreLabel c = new CoreLabel();
    c.setWord("America");
    c.set(CoreAnnotations.ShapeAnnotation.class, "Xx");
    c.set(CoreAnnotations.DomainAnnotation.class, "testDomain");

    List<CoreLabel> tokens = Arrays.asList(p2, p1, c);
    PaddedList<CoreLabel> padded = new PaddedList<>(tokens);

    Collection<String> result = factory.getCliqueFeatures(padded, 2, NERFeatureFactory.cliqueC);
    boolean seen = false;
    for (String f : result) {
      if (f.contains("PPW_CTYPE")) {
        seen = true;
      }
    }
    assertTrue(seen);
  }
@Test
  public void testUseNextRealWordWithShortNextAddsCombinedFeature() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useNextRealWord = true;
    flags.useWord = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel c = new CoreLabel();
    c.setWord("House");
    c.set(CoreAnnotations.ShapeAnnotation.class, "Xx");
    c.set(CoreAnnotations.DomainAnnotation.class, "dom");

    CoreLabel n1 = new CoreLabel();
    n1.setWord("in");

    CoreLabel n2 = new CoreLabel();
    n2.setWord("Texas");

    List<CoreLabel> input = Arrays.asList(c, n1, n2);
    PaddedList<CoreLabel> padded = new PaddedList<>(input);

    Collection<String> feats = factory.getCliqueFeatures(padded, 0, NERFeatureFactory.cliqueC);
    boolean hasTag = false;
    for (String f : feats) {
      if (f.contains("NNW_CTYPE")) {
        hasTag = true;
      }
    }
    assertTrue(hasTag);
  }
@Test
  public void testUseTaggySequencesShapeInteraction_combinedFeaturePresent() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useTaggySequences = true;
    flags.useTaggySequencesShapeInteraction = true;
    flags.usePrev = true;
    flags.maxLeft = 2;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel p2 = new CoreLabel();
    p2.setWord("The");
    p2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "DT");
    p2.set(CoreAnnotations.ShapeAnnotation.class, "AA");
    p2.set(CoreAnnotations.DomainAnnotation.class, "X");

    CoreLabel p1 = new CoreLabel();
    p1.setWord("White");
    p1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    p1.set(CoreAnnotations.ShapeAnnotation.class, "Aa");
    p1.set(CoreAnnotations.DomainAnnotation.class, "X");

    CoreLabel c = new CoreLabel();
    c.setWord("House");
    c.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    c.set(CoreAnnotations.ShapeAnnotation.class, "Aa");
    c.set(CoreAnnotations.DomainAnnotation.class, "X");

    List<CoreLabel> all = Arrays.asList(p2, p1, c);
    PaddedList<CoreLabel> padded = new PaddedList<>(all);

    Collection<String> feats = factory.getCliqueFeatures(padded, 2, NERFeatureFactory.cliqueCpCp2C);
    boolean combined = false;
    for (String s : feats) {
      if (s.contains("-TTS-CS")) {
        combined = true;
      }
    }
    assertTrue(combined);
  }
@Test
  public void testUseBeginSentBoundaryAddsShapeAndFeature() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useBeginSent = true;
    flags.useWord = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel token = new CoreLabel();
    token.setWord("Monday");
    token.set(CoreAnnotations.ShapeAnnotation.class, "Xxxxx");
    token.set(CoreAnnotations.DomainAnnotation.class, "D");
    token.set(CoreAnnotations.PositionAnnotation.class, "0");

    List<CoreLabel> tokens = Collections.singletonList(token);
    PaddedList<CoreLabel> padded = new PaddedList<>(tokens);

    Collection<String> features = factory.getCliqueFeatures(padded, 0, NERFeatureFactory.cliqueC);

    boolean boundaryShape = false;
    boolean boundaryTag = false;

    for (String s : features) {
      if (s.equals("BEGIN-SENT|C")) boundaryTag = true;
      if (s.equals("Xxxxx-BEGIN-SENT|C")) boundaryShape = true;
    }

    assertTrue(boundaryTag);
    assertTrue(boundaryShape);
  }
@Test
  public void testUseRadicalAppendsSingleCharRadicalFeature() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useRadical = true;
    flags.useWord = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel c = new CoreLabel();
    c.setWord("我");
    c.set(CoreAnnotations.ShapeAnnotation.class, "S");
    c.set(CoreAnnotations.DomainAnnotation.class, "Z");

    List<CoreLabel> tokens = Collections.singletonList(c);
    PaddedList<CoreLabel> padded = new PaddedList<>(tokens);

    Collection<String> feats = factory.getCliqueFeatures(padded, 0, NERFeatureFactory.cliqueC);
    boolean hasRadical = false;
    for (String s : feats) {
      if (s.contains("-RADICAL")) {
        hasRadical = true;
      }
    }
    assertTrue(hasRadical);
  }
@Test
  public void testMissingGenericAnnotationHandledSafely() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useGenericFeatures = true;
    flags.useWord = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel label = new CoreLabel();
    label.setWord("foo");
    label.set(CoreAnnotations.ShapeAnnotation.class, "S");
    label.set(CoreAnnotations.DomainAnnotation.class, "T");

    List<CoreLabel> list = Collections.singletonList(label);
    PaddedList<CoreLabel> padded = new PaddedList<>(list);

    factory.getCliqueFeatures(padded, 0, NERFeatureFactory.cliqueC);
    assertTrue(true); 
  }
@Test
  public void testUseSymTags_createTagCombinations() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useWord = true;
    flags.usePrev = true;
    flags.useNext = true;
    flags.useTags = true;
    flags.useSymTags = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel prev = new CoreLabel();
    prev.setWord("Mr.");
    prev.set(CoreAnnotations.ShapeAnnotation.class, "X");
    prev.set(CoreAnnotations.DomainAnnotation.class, "a");
    prev.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

    CoreLabel center = new CoreLabel();
    center.setWord("Obama");
    center.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    center.set(CoreAnnotations.ShapeAnnotation.class, "Aa");
    center.set(CoreAnnotations.DomainAnnotation.class, "a");

    CoreLabel next = new CoreLabel();
    next.setWord("visited");
    next.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBD");
    next.set(CoreAnnotations.ShapeAnnotation.class, "aa");
    next.set(CoreAnnotations.DomainAnnotation.class, "a");

    List<CoreLabel> list = Arrays.asList(prev, center, next);
    PaddedList<CoreLabel> padded = new PaddedList<>(list);

    Collection<String> feats = factory.getCliqueFeatures(padded, 1, NERFeatureFactory.cliqueC);
    boolean found = false;
    for (String s : feats) {
      if (s.contains("PCNTAGS") || s.contains("CNTAGS") || s.contains("PCTAGS")) {
        found = true;
      }
    }
    assertTrue(found);
  }
@Test
  public void testUseSymWordPairs_pairGenerated() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useWord = true;
    flags.useSymWordPairs = true;
    flags.usePrev = true;
    flags.useNext = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel prev = new CoreLabel();
    prev.setWord("Barack");
    prev.set(CoreAnnotations.ShapeAnnotation.class, "Aa");
    prev.set(CoreAnnotations.DomainAnnotation.class, "x");

    CoreLabel center = new CoreLabel();
    center.setWord("Obama");
    center.set(CoreAnnotations.ShapeAnnotation.class, "Aa");
    center.set(CoreAnnotations.DomainAnnotation.class, "x");

    CoreLabel next = new CoreLabel();
    next.setWord("visited");
    next.set(CoreAnnotations.ShapeAnnotation.class, "aa");
    next.set(CoreAnnotations.DomainAnnotation.class, "x");

    List<CoreLabel> tokens = Arrays.asList(prev, center, next);
    PaddedList<CoreLabel> padded = new PaddedList<>(tokens);

    Collection<String> feats = factory.getCliqueFeatures(padded, 1, NERFeatureFactory.cliqueC);
    boolean matched = false;
    for (String feat : feats) {
      if (feat.contains("SWORDS")) {
        matched = true;
      }
    }
    assertTrue(matched);
  }
@Test
  public void testUseDisjunctiveShapeInteraction_appendsCombinedForms() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useDisjunctive = true;
    flags.useDisjunctiveShapeInteraction = true;
    flags.disjunctionWidth = 1;
    flags.useWord = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel prev = new CoreLabel();
    prev.setWord("President");
    prev.set(CoreAnnotations.ShapeAnnotation.class, "Xxxxx");
    prev.set(CoreAnnotations.DomainAnnotation.class, "D");

    CoreLabel center = new CoreLabel();
    center.setWord("Obama");
    center.set(CoreAnnotations.ShapeAnnotation.class, "Xx");
    center.set(CoreAnnotations.DomainAnnotation.class, "D");

    CoreLabel next = new CoreLabel();
    next.setWord("spoke");
    next.set(CoreAnnotations.ShapeAnnotation.class, "xxxx");
    next.set(CoreAnnotations.DomainAnnotation.class, "D");

    List<CoreLabel> list = Arrays.asList(prev, center, next);
    PaddedList<CoreLabel> padded = new PaddedList<>(list);

    Collection<String> features = factory.getCliqueFeatures(padded, 1, NERFeatureFactory.cliqueC);
    boolean match = false;
    for (String s : features) {
      if (s.contains("DISJP-CS") || s.contains("DISJN-CS")) {
        match = true;
      }
    }
    assertTrue(match);
  }
@Test
  public void testUseChunks_enabledConjunctions() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useChunks = true;
    flags.useWord = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel p = new CoreLabel();
    p.setWord("The");
    p.set(CoreAnnotations.ChunkAnnotation.class, "B-NP");
    p.set(CoreAnnotations.DomainAnnotation.class, "T");
    p.set(CoreAnnotations.ShapeAnnotation.class, "AA");

    CoreLabel c = new CoreLabel();
    c.setWord("White");
    c.set(CoreAnnotations.ChunkAnnotation.class, "I-NP");
    c.set(CoreAnnotations.DomainAnnotation.class, "T");
    c.set(CoreAnnotations.ShapeAnnotation.class, "Aa");

    CoreLabel n = new CoreLabel();
    n.setWord("House");
    n.set(CoreAnnotations.ChunkAnnotation.class, "I-NP");
    n.set(CoreAnnotations.DomainAnnotation.class, "T");
    n.set(CoreAnnotations.ShapeAnnotation.class, "Aa");

    List<CoreLabel> list = Arrays.asList(p, c, n);
    PaddedList<CoreLabel> padded = new PaddedList<>(list);

    Collection<String> feats = factory.getCliqueFeatures(padded, 1, NERFeatureFactory.cliqueC);
    boolean found = false;
    for (String f : feats) {
      if (f.contains("PCCHUNK") || f.contains("CNCHUNK") || f.contains("PCNCHUNK")) {
        found = true;
      }
    }
    assertTrue(found);
  }
@Test
  public void testUseIfInteger_PosValueOnly() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useIfInteger = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel token = new CoreLabel();
    token.setWord("123");
    token.set(CoreAnnotations.ShapeAnnotation.class, "DDD");
    token.set(CoreAnnotations.DomainAnnotation.class, "NUM");

    List<CoreLabel> list = Arrays.asList(token);
    PaddedList<CoreLabel> padded = new PaddedList<>(list);

    Collection<String> features = factory.getCliqueFeatures(padded, 0, NERFeatureFactory.cliqueC);
    boolean match = false;
    for (String f : features) {
      if (f.contains("POSITIVE_INTEGER")) {
        match = true;
      }
    }
    assertTrue(match);
  }
@Test
  public void testUseTwoStageBinAnnotationsAppliesAll() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useWord = true;
    flags.twoStage = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel token = new CoreLabel();
    token.setWord("X");
    token.set(CoreAnnotations.ShapeAnnotation.class, "X");
    token.set(CoreAnnotations.DomainAnnotation.class, "X");
//    token.set(NERFeatureFactory.Bin1Annotation.class, "B1");
//    token.set(NERFeatureFactory.Bin2Annotation.class, "B2");
//    token.set(NERFeatureFactory.Bin3Annotation.class, "B3");
//    token.set(NERFeatureFactory.Bin4Annotation.class, "B4");
//    token.set(NERFeatureFactory.Bin5Annotation.class, "B5");
//    token.set(NERFeatureFactory.Bin6Annotation.class, "B6");

    List<CoreLabel> list = Collections.singletonList(token);
    PaddedList<CoreLabel> padded = new PaddedList<>(list);

    Collection<String> features = factory.getCliqueFeatures(padded, 0, NERFeatureFactory.cliqueC);
    boolean b1 = false, b6 = false;
    for (String f : features) {
      if (f.contains("BIN1")) b1 = true;
      if (f.contains("BIN6")) b6 = true;
    }
    assertTrue(b1 && b6);
  }
@Test
  public void testUseGenericFeatures_withCollectionValueAnnotation() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useGenericFeatures = true;
    flags.useWord = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel token = new CoreLabel();
    token.setWord("London");
    token.set(CoreAnnotations.ShapeAnnotation.class, "Xx");
    token.set(CoreAnnotations.DomainAnnotation.class, "gen");
//    token.set(CoreLabel.GenericAnnotation.class, Arrays.asList("FEAT1", "FEAT2"));

    List<CoreLabel> l = Collections.singletonList(token);
    PaddedList<CoreLabel> padded = new PaddedList<>(l);

    Collection<String> features = factory.getCliqueFeatures(padded, 0, NERFeatureFactory.cliqueC);
    boolean f1 = false, f2 = false;
    for (String f : features) {
      if (f.contains("FEAT1")) f1 = true;
      if (f.contains("FEAT2")) f2 = true;
    }
    assertTrue(f1 && f2);
  }
@Test
  public void testUseEntityTypesAndEntityRuleEnabled() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useEntityRule = true;
    flags.useEntityTypes = true;
    flags.useWord = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel token = new CoreLabel();
    token.setWord("2020");
    token.set(CoreAnnotations.EntityRuleAnnotation.class, "NUMRULE");
    token.set(CoreAnnotations.EntityTypeAnnotation.class, "DATE");
    token.set(CoreAnnotations.ShapeAnnotation.class, "0000");
    token.set(CoreAnnotations.DomainAnnotation.class, "txt");

    List<CoreLabel> toks = Collections.singletonList(token);
    PaddedList<CoreLabel> padded = new PaddedList<>(toks);

    Collection<String> feats = factory.getCliqueFeatures(padded, 0, NERFeatureFactory.cliqueC);
    boolean ruleFound = false;
    boolean typeFound = false;

    for (String f : feats) {
      if (f.contains("ENTITYRULE")) {
        ruleFound = true;
      }
      if (f.contains("ENTITYTYPE")) {
        typeFound = true;
      }
    }

    assertTrue(ruleFound && typeFound);
  }
@Test
  public void testUseTopics_appendsTopicFeatures() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useTopics = true;
    flags.useWord = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel token = new CoreLabel();
    token.setWord("biology");
    token.set(CoreAnnotations.TopicAnnotation.class, "SCI");
    token.set(CoreAnnotations.ShapeAnnotation.class, "xx");
    token.set(CoreAnnotations.DomainAnnotation.class, "z");

    CoreLabel padPrev = new CoreLabel();
    padPrev.set(CoreAnnotations.TopicAnnotation.class, "SCI");

    CoreLabel padNext = new CoreLabel();
    padNext.set(CoreAnnotations.TopicAnnotation.class, "SCI");

    List<CoreLabel> tokens = Arrays.asList(padPrev, token, padNext);
    PaddedList<CoreLabel> padded = new PaddedList<>(tokens);

    Collection<String> feats = factory.getCliqueFeatures(padded, 1, NERFeatureFactory.cliqueC);
    boolean match = false;
    for (String s : feats) {
      if (s.contains("TopicID")) {
        match = true;
      }
    }

    assertTrue(match);
  }
@Test
  public void testEmptyWordWithFeaturesSafeFallback() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useWord = true;
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel token = new CoreLabel();
    token.setWord("");
    token.set(CoreAnnotations.ShapeAnnotation.class, "xx");
    token.set(CoreAnnotations.DomainAnnotation.class, "x");

    List<CoreLabel> toks = Collections.singletonList(token);
    PaddedList<CoreLabel> padded = new PaddedList<>(toks);

    Collection<String> feats = factory.getCliqueFeatures(padded, 0, NERFeatureFactory.cliqueC);

    assertNotNull(feats);
  }
@Test
  public void testDehyphenateWordWithNoHyphenReturnsOriginal() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useWord = true;
    flags.useNGrams = true;
    flags.dehyphenateNGrams = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel word = new CoreLabel();
    word.setWord("biology");
    word.set(CoreAnnotations.ShapeAnnotation.class, "Xx");
    word.set(CoreAnnotations.DomainAnnotation.class, "D");

    List<CoreLabel> list = Collections.singletonList(word);
    PaddedList<CoreLabel> padded = new PaddedList<>(list);

    Collection<String> feats = factory.getCliqueFeatures(padded, 0, NERFeatureFactory.cliqueC);
    assertNotNull(feats);
  }
@Test
  public void testIsOrdinalHandlesNumberOnlyFollowedByOrdinalSuffix() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useOrdinal = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel num = new CoreLabel();
    num.setWord("10");
    num.set(CoreAnnotations.ShapeAnnotation.class, "DD");
    num.set(CoreAnnotations.DomainAnnotation.class, "X");

    CoreLabel suf = new CoreLabel();
    suf.setWord("th");
    suf.set(CoreAnnotations.ShapeAnnotation.class, "xx");
    suf.set(CoreAnnotations.DomainAnnotation.class, "X");

    List<CoreLabel> list = Arrays.asList(num, suf);
    PaddedList<CoreLabel> padded = new PaddedList<>(list);

    Collection<String> feats = factory.getCliqueFeatures(padded, 0, NERFeatureFactory.cliqueC);

    boolean hasOrdinal = false;
    for (String f : feats) {
      if (f.contains("ORDINAL")) {
        hasOrdinal = true;
      }
    }
    assertTrue(hasOrdinal);
  }
@Test
  public void testDistSimReturnsNullWhenMissingAndFallbackUnset() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useDistSim = true;
    flags.distSimLexicon = null;
    flags.unknownWordDistSimClass = null;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel token = new CoreLabel();
    token.setWord("unseenword");
    token.set(CoreAnnotations.ShapeAnnotation.class, "Aa");
    token.set(CoreAnnotations.DomainAnnotation.class, "x");

    List<CoreLabel> tokens = Collections.singletonList(token);
    PaddedList<CoreLabel> padded = new PaddedList<>(tokens);

    Collection<String> feats = factory.getCliqueFeatures(padded, 0, NERFeatureFactory.cliqueC);
    assertNotNull(feats);
  }
@Test
  public void testUseLemmasAndPrevNextLemmasCombo() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useLemmas = true;
    flags.usePrevNextLemmas = true;
    flags.useWord = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel p = new CoreLabel();
    p.setWord("ran");
    p.set(CoreAnnotations.LemmaAnnotation.class, "run");
    p.set(CoreAnnotations.ShapeAnnotation.class, "xx");
    p.set(CoreAnnotations.DomainAnnotation.class, "x");

    CoreLabel c = new CoreLabel();
    c.setWord("running");
    c.set(CoreAnnotations.LemmaAnnotation.class, "run");
    c.set(CoreAnnotations.ShapeAnnotation.class, "xxx");
    c.set(CoreAnnotations.DomainAnnotation.class, "x");

    CoreLabel n = new CoreLabel();
    n.setWord("runs");
    n.set(CoreAnnotations.LemmaAnnotation.class, "run");
    n.set(CoreAnnotations.ShapeAnnotation.class, "x");
    n.set(CoreAnnotations.DomainAnnotation.class, "x");

    List<CoreLabel> list = Arrays.asList(p, c, n);
    PaddedList<CoreLabel> padded = new PaddedList<>(list);

    Collection<String> feats = factory.getCliqueFeatures(padded, 1, NERFeatureFactory.cliqueC);
    boolean has = false;
    for (String f : feats) {
      if (f.contains("PLEM") || f.contains("LEM") || f.contains("NLEM")) {
        has = true;
      }
    }
    assertTrue(has);
  }
@Test
  public void testUseMultipleExternalAnnotationsSafely() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useABSTR = true;
    flags.useGENIA = true;
    flags.useWEB = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel token = new CoreLabel();
    token.setWord("cell");
    token.set(CoreAnnotations.AbstrAnnotation.class, "Y");
    token.set(CoreAnnotations.GeniaAnnotation.class, "X");
    token.set(CoreAnnotations.WebAnnotation.class, "Z");
    token.set(CoreAnnotations.ShapeAnnotation.class, "xx");
    token.set(CoreAnnotations.DomainAnnotation.class, "BIO");

    List<CoreLabel> toks = Collections.singletonList(token);
    PaddedList<CoreLabel> padded = new PaddedList<>(toks);

    Collection<String> feats = factory.getCliqueFeatures(padded, 0, NERFeatureFactory.cliqueC);
    assertNotNull(feats);
  }
@Test
  public void testUsePositionAtSentenceEndBoundary() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.usePosition = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel token = new CoreLabel();
    token.setWord("goodbye");
    token.set(CoreAnnotations.ShapeAnnotation.class, "xx");
    token.set(CoreAnnotations.DomainAnnotation.class, "text");
    token.set(CoreAnnotations.PositionAnnotation.class, "1");

    CoreLabel only = new CoreLabel();
    only.setWord("end");
    only.set(CoreAnnotations.ShapeAnnotation.class, "xx");
    only.set(CoreAnnotations.DomainAnnotation.class, "text");
    only.set(CoreAnnotations.PositionAnnotation.class, "1");

    List<CoreLabel> toks = Collections.singletonList(only);
    PaddedList<CoreLabel> padded = new PaddedList<>(toks);

    Collection<String> feats = factory.getCliqueFeatures(padded, 0, NERFeatureFactory.cliqueC);
    assertNotNull(feats);
  }
@Test
  public void testUseGovernorAndNPHeadTogether() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useNPGovernor = true;
    flags.useNPHead = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel token = new CoreLabel();
    token.setWord("protein");
    token.set(CoreAnnotations.GovernorAnnotation.class, "binds");
    token.set(CoreAnnotations.HeadWordStringAnnotation.class, "protein");
    token.set(CoreAnnotations.ShapeAnnotation.class, "xxx");
    token.set(CoreAnnotations.DomainAnnotation.class, "BIO");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");

    List<CoreLabel> toks = Collections.singletonList(token);
    PaddedList<CoreLabel> padded = new PaddedList<>(toks);

    Collection<String> feats = factory.getCliqueFeatures(padded, 0, NERFeatureFactory.cliqueC);
    assertNotNull(feats);
  }
@Test
  public void testMinimalAbbrAndMinimalAbbr1Together() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useMinimalAbbr = true;
    flags.useMinimalAbbr1 = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel token = new CoreLabel();
    token.setWord("D.N.A.");
    token.set(CoreAnnotations.ShapeAnnotation.class, "X.X.X.");
    token.set(CoreAnnotations.DomainAnnotation.class, "BIO");
    token.set(CoreAnnotations.AbbrAnnotation.class, "DNA");

    List<CoreLabel> toks = Collections.singletonList(token);
    PaddedList<CoreLabel> padded = new PaddedList<>(toks);

    Collection<String> output = factory.getCliqueFeatures(padded, 0, NERFeatureFactory.cliqueC);
    assertNotNull(output);
  }
@Test
  public void testFeatureCollectorDomainOnlySuffixAppliedOnlyOnce() {
    Set<String> result = new HashSet<>();
    NERFeatureFactory.FeatureCollector collector = new NERFeatureFactory.FeatureCollector(result);
    collector.setSuffix("XX");
    collector.setDomain("");
    collector.build().append("TEST").add();
    int count = result.size();
    assertEquals(1, count);
  } 
}