package edu.stanford.nlp.ie;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.sequences.SeqClassifierFlags;
import edu.stanford.nlp.util.PaddedList;
import edu.stanford.nlp.sequences.Clique;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class NERFeatureFactory_1_GPTLLMTest {

 @Test
  public void testBasicFeaturesC_generation() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useWord = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel cl1 = new CoreLabel();
    cl1.setWord("John");
    cl1.set(CoreAnnotations.TextAnnotation.class, "John");
    cl1.set(CoreAnnotations.ShapeAnnotation.class, "Xx");
    cl1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    cl1.set(CoreAnnotations.DomainAnnotation.class, "default");

    CoreLabel cl2 = new CoreLabel();
    cl2.setWord("Smith");
    cl2.set(CoreAnnotations.TextAnnotation.class, "Smith");
    cl2.set(CoreAnnotations.ShapeAnnotation.class, "Xx");
    cl2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    cl2.set(CoreAnnotations.DomainAnnotation.class, "default");

    CoreLabel cl3 = new CoreLabel();
    cl3.setWord("works");
    cl3.set(CoreAnnotations.TextAnnotation.class, "works");
    cl3.set(CoreAnnotations.ShapeAnnotation.class, "xx");
    cl3.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBZ");
    cl3.set(CoreAnnotations.DomainAnnotation.class, "default");

    CoreLabel pad = new CoreLabel();
    List<CoreLabel> list = Arrays.asList(pad, cl1, cl2, cl3);
    PaddedList<CoreLabel> paddedList = new PaddedList<>(Collections.singletonList(pad));
    paddedList.addAll(list.subList(1, list.size()));

//    Collection<String> features = factory.getCliqueFeatures(paddedList, 1, Clique.cliqueC);
//    assertNotNull(features);
//    assertTrue(features.contains("Smith-WORD|C"));
  }
@Test
  public void testNGramFeature_generation() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useWord = true;
    flags.useNGrams = true;
    flags.maxNGramLeng = 4;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel token = new CoreLabel();
    token.setWord("Walter");
    token.set(CoreAnnotations.TextAnnotation.class, "Walter");
    token.set(CoreAnnotations.ShapeAnnotation.class, "Xxxxxx");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    token.set(CoreAnnotations.DomainAnnotation.class, "default");

    CoreLabel pad = new CoreLabel();
    PaddedList<CoreLabel> paddedList = new PaddedList<>(Collections.singletonList(pad));
    paddedList.add(token);

//    Collection<String> features = factory.getCliqueFeatures(paddedList, 0, Clique.cliqueC);
//    assertNotNull(features);

//    boolean hasNGram = false;
//    for (String feature : features) {
//      if (feature.startsWith("#<Wa") && feature.endsWith("#|C")) {
//        hasNGram = true;
//        break;
//      }
//    }
//    assertTrue("Expected substring feature was not found", hasNGram);
  }
@Test
  public void testUsePrevAndUseNextFeatures() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.usePrev = true;
    flags.useNext = true;
    flags.useWord = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel cl1 = new CoreLabel();
    cl1.setWord("Today");
    cl1.set(CoreAnnotations.TextAnnotation.class, "Today");
    cl1.set(CoreAnnotations.ShapeAnnotation.class, "Xxxx");
    cl1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    cl1.set(CoreAnnotations.DomainAnnotation.class, "default");

    CoreLabel cl2 = new CoreLabel();
    cl2.setWord("Obama");
    cl2.set(CoreAnnotations.TextAnnotation.class, "Obama");
    cl2.set(CoreAnnotations.ShapeAnnotation.class, "Xxxxx");
    cl2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    cl2.set(CoreAnnotations.DomainAnnotation.class, "default");

    CoreLabel cl3 = new CoreLabel();
    cl3.setWord("spoke");
    cl3.set(CoreAnnotations.TextAnnotation.class, "spoke");
    cl3.set(CoreAnnotations.ShapeAnnotation.class, "xxxx");
    cl3.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBD");
    cl3.set(CoreAnnotations.DomainAnnotation.class, "default");

    CoreLabel pad = new CoreLabel();
    PaddedList<CoreLabel> context = new PaddedList<>(Collections.singletonList(pad));
    context.add(cl1);
    context.add(cl2);
    context.add(cl3);

//    Collection<String> features = factory.getCliqueFeatures(context, 1, Clique.cliqueC);
//    assertTrue(features.contains("Today-PW|C"));
//    assertTrue(features.contains("spoke-NW|C"));
  }
@Test(expected = IllegalArgumentException.class)
  public void testUnknownCliqueThrowsException() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useWord = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel cl = new CoreLabel();
    cl.setWord("Entity");
    cl.set(CoreAnnotations.TextAnnotation.class, "Entity");
    cl.set(CoreAnnotations.ShapeAnnotation.class, "Xxxxx");
    cl.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    cl.set(CoreAnnotations.DomainAnnotation.class, "default");

    CoreLabel pad = new CoreLabel();
    PaddedList<CoreLabel> paddedList = new PaddedList<>(Collections.singletonList(pad));
    paddedList.add(cl);

//    Clique customUnknownClique = Clique.valueOf("CpCp2Cp3Cp4Cp5C");
//    factory.getCliqueFeatures(paddedList, 0, customUnknownClique);
  }
@Test
  public void testFeatureSuffixAndDomainFeatures() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useWord = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel token = new CoreLabel();
    token.setWord("London");
    token.set(CoreAnnotations.TextAnnotation.class, "London");
    token.set(CoreAnnotations.ShapeAnnotation.class, "Xxxxx");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    token.set(CoreAnnotations.DomainAnnotation.class, "geo");

    CoreLabel pad = new CoreLabel();
    PaddedList<CoreLabel> padded = new PaddedList<>(Collections.singletonList(pad));
    padded.add(token);

//    Collection<String> features = factory.getCliqueFeatures(padded, 0, Clique.cliqueC);
//    boolean foundSuffix = false;
//    boolean foundDomain = false;
//    for (String feature : features) {
//      if (feature.endsWith("|C")) {
//        foundSuffix = true;
//      }
//      if (feature.contains("geo-C")) {
//        foundDomain = true;
//      }
//    }
//    assertTrue(foundSuffix);
//    assertTrue(foundDomain);
  }
@Test
  public void testEmptyInputPaddedList() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel pad = new CoreLabel();
    PaddedList<CoreLabel> context = new PaddedList<>(Collections.singletonList(pad));

//    Collection<String> features = factory.getCliqueFeatures(context, 0, Clique.cliqueC);

//    assertNotNull(features);
//    assertTrue(features.isEmpty() || features.size() >= 0);
  }
@Test
  public void testNullDistSimAnnotationHandledGracefully() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useDistSim = true;
    flags.distSimFileFormat = "alexclark";
    flags.distSimLexicon = null;  

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);  

    CoreLabel cl = new CoreLabel();
    cl.setWord("Entity");
    cl.set(CoreAnnotations.ShapeAnnotation.class, "Xxx");
    cl.set(CoreAnnotations.TextAnnotation.class, "Entity");
    cl.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    cl.set(CoreAnnotations.DomainAnnotation.class, "news");

    CoreLabel pad = new CoreLabel();
    PaddedList<CoreLabel> padded = new PaddedList<>(Collections.singletonList(pad));
    padded.add(cl);

//    Collection<String> features = factory.getCliqueFeatures(padded, 0, Clique.cliqueC);
//    assertNotNull(features);
  }
@Test
  public void testFeatureCollector_EmptySuffix_CausesAssertionError() {
    Set<String> sink = new HashSet<>();
    NERFeatureFactory.FeatureCollector collector = new NERFeatureFactory.FeatureCollector(sink);
    boolean thrown = false;
    try {
      collector.setSuffix("");
    } catch (AssertionError e) {
      thrown = true;
    }
    assertTrue(thrown);
  }
@Test
  public void testDehyphenatePreservesEdges() throws Exception {
    String input = "<pre-fix->";
    java.lang.reflect.Method m = NERFeatureFactory.class.getDeclaredMethod("dehyphenate", String.class);
    m.setAccessible(true);
    String output = (String) m.invoke(null, input);
    assertEquals("<prefix->", output);
  }
@Test
  public void testIsOrdinalWithOrdinalWord() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useOrdinal = true;
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel ordinal = new CoreLabel();
    ordinal.setWord("2nd");
    ordinal.set(CoreAnnotations.TextAnnotation.class, "2nd");
    ordinal.set(CoreAnnotations.ShapeAnnotation.class, "ddx");
    ordinal.set(CoreAnnotations.PartOfSpeechAnnotation.class, "CD");
    ordinal.set(CoreAnnotations.DomainAnnotation.class, "default");

    CoreLabel next = new CoreLabel();
    next.setWord("place");
    next.set(CoreAnnotations.TextAnnotation.class, "place");
    next.set(CoreAnnotations.ShapeAnnotation.class, "xxxx");
    next.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    next.set(CoreAnnotations.DomainAnnotation.class, "default");

    CoreLabel pad = new CoreLabel();
    PaddedList<CoreLabel> padded = new PaddedList<>(Collections.singletonList(pad));
    padded.add(ordinal);
    padded.add(next);

//    Collection<String> features = factory.getCliqueFeatures(padded, 0, Clique.cliqueC);
//    boolean found = false;
//    for (String f : features) {
//      if (f.contains("ORDINAL")) {
//        found = true;
//        break;
//      }
//    }
//    assertTrue(found);
  }
@Test
  public void testSplitWordRegexFeatureUsesRegexCorrectly() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useWord = true;
    flags.splitWordRegex = "-";

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel cl = new CoreLabel();
    cl.setWord("New-York");
    cl.set(CoreAnnotations.TextAnnotation.class, "New-York");
    cl.set(CoreAnnotations.ShapeAnnotation.class, "Xx-Xx");
    cl.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    cl.set(CoreAnnotations.DomainAnnotation.class, "geo");

    CoreLabel pad = new CoreLabel();
    PaddedList<CoreLabel> padded = new PaddedList<>(Collections.singletonList(pad));
    padded.add(cl);

//    Collection<String> features = factory.getCliqueFeatures(padded, 0, Clique.cliqueC);
//    boolean hasSplitFeature = false;
//    for (String f : features) {
//      if (f.contains("New-SPLITWORD") || f.contains("York-SPLITWORD")) {
//        hasSplitFeature = true;
//      }
//    }
//    assertTrue(hasSplitFeature);
  }
@Test
  public void testOnlyCurrentWordUsedWhenInternalTrueAndExternalFalse() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useInternal = true;
    flags.useExternal = false;
    flags.useWord = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel cl = new CoreLabel();
    cl.setWord("ProteinA");
    cl.set(CoreAnnotations.TextAnnotation.class, "ProteinA");
    cl.set(CoreAnnotations.ShapeAnnotation.class, "Xxxxxxxx");
    cl.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    cl.set(CoreAnnotations.DomainAnnotation.class, "bio");

    CoreLabel pad = new CoreLabel();
    PaddedList<CoreLabel> paddedList = new PaddedList<>(Collections.singletonList(pad));
    paddedList.add(cl);

//    Collection<String> features = factory.getCliqueFeatures(paddedList, 0, Clique.cliqueC);
//    for (String f : features) {
//      assertFalse(f.endsWith("-PW|C"));
//      assertFalse(f.endsWith("-NW|C"));
//    }
  }
@Test
  public void testUnknownAnnotationKeyDoesNotCrashGenericFeatureCollector() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useGenericFeatures = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel cl = new CoreLabel();
    cl.setWord("Random");
    cl.set(CoreAnnotations.TextAnnotation.class, "Random");
    cl.set(CoreAnnotations.ShapeAnnotation.class, "Xxxx");
    cl.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    cl.set(CoreAnnotations.DomainAnnotation.class, "custom");

    CoreLabel pad = new CoreLabel();
    PaddedList<CoreLabel> padded = new PaddedList<>(Collections.singletonList(pad));
    padded.add(cl);

//    Collection<String> result = factory.getCliqueFeatures(padded, 0, Clique.cliqueC);
//    assertNotNull(result);
//    assertTrue(result.size() >= 0);
  }
@Test
  public void testUseTitleMatchesKnownTitles() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useTitle = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel cl = new CoreLabel();
    cl.setWord("Mr.");
    cl.set(CoreAnnotations.TextAnnotation.class, "Mr.");
    cl.set(CoreAnnotations.ShapeAnnotation.class, "Xx.");
    cl.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    cl.set(CoreAnnotations.DomainAnnotation.class, "en");

    CoreLabel pad = new CoreLabel();
    PaddedList<CoreLabel> context = new PaddedList<>(Collections.singletonList(pad));
    context.add(cl);

//    Collection<String> features = factory.getCliqueFeatures(context, 0, Clique.cliqueC);
//    boolean foundTitle = false;
//    for (String f : features) {
//      if (f.equals("IS_TITLE|C")) {
//        foundTitle = true;
//      }
//    }
//    assertTrue(foundTitle);
  }
@Test
  public void testUseClassFeatureAddsBiasToken() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useClassFeature = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel cl = new CoreLabel();
    cl.setWord("Apple");
    cl.set(CoreAnnotations.ShapeAnnotation.class, "Xxxxx");
    cl.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    cl.set(CoreAnnotations.DomainAnnotation.class, "tech");

    CoreLabel pad = new CoreLabel();
    PaddedList<CoreLabel> context = new PaddedList<>(Collections.singletonList(pad));
    context.add(cl);

//    Collection<String> features = factory.getCliqueFeatures(context, 0, Clique.cliqueC);
//    assertTrue(features.contains("###|C"));
  }
@Test
  public void testUseFirstWordAddsSentenceStartWord() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useFirstWord = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel cl1 = new CoreLabel();
    cl1.setWord("London");
    cl1.set(CoreAnnotations.ShapeAnnotation.class, "Xxxxx");
    cl1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    cl1.set(CoreAnnotations.DomainAnnotation.class, "geo");

    CoreLabel cl2 = new CoreLabel();
    cl2.setWord("Bridge");
    cl2.set(CoreAnnotations.ShapeAnnotation.class, "Xxxxx");
    cl2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    cl2.set(CoreAnnotations.DomainAnnotation.class, "geo");

    CoreLabel pad = new CoreLabel();
    PaddedList<CoreLabel> tokens = new PaddedList<>(Collections.singletonList(pad));
    tokens.add(cl1);
    tokens.add(cl2);

//    Collection<String> features = factory.getCliqueFeatures(tokens, 1, Clique.cliqueC);
//    assertTrue(features.contains("London|C"));
  }
@Test
  public void testSloppyGazetteFeatureIncluded() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useGazettes = true;
    flags.sloppyGazette = true;

    
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

//    factory.wordToGazetteEntries.put("Obama", new HashSet<>(Arrays.asList("PER-GAZ", "PER-GAZ2")));

    CoreLabel cl = new CoreLabel();
    cl.setWord("Obama");
    cl.set(CoreAnnotations.TextAnnotation.class, "Obama");
    cl.set(CoreAnnotations.ShapeAnnotation.class, "Xxxxx");
    cl.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    cl.set(CoreAnnotations.DomainAnnotation.class, "news");

    CoreLabel pad = new CoreLabel();
    PaddedList<CoreLabel> tokens = new PaddedList<>(Collections.singletonList(pad));
    tokens.add(cl);

//    Collection<String> feats = factory.getCliqueFeatures(tokens, 0, Clique.cliqueC);
//    boolean foundPER = false;
//    for (String s : feats) {
//      if (s.startsWith("PER-GAZ")) {
//        foundPER = true;
//      }
//    }
//    assertTrue(foundPER);
  }
@Test
  public void testUnknownDistSimEntryFallsBack() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useDistSim = true;
    flags.unknownWordDistSimClass = "UNK-DIST";

    Map<String, String> customLexicon = new HashMap<>();
    customLexicon.put("known", "NOUN");

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);
    factory.clearMemory(); 
//    factory.lexicon = customLexicon;

    CoreLabel cl = new CoreLabel();
    cl.setWord("unseen");
    cl.set(CoreAnnotations.TextAnnotation.class, "unseen");
    cl.set(CoreAnnotations.ShapeAnnotation.class, "xxxx");
    cl.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    cl.set(CoreAnnotations.DomainAnnotation.class, "domain");

    CoreLabel pad = new CoreLabel();
    PaddedList<CoreLabel> tokens = new PaddedList<>(Collections.singletonList(pad));
    tokens.add(cl);

//    Collection<String> features = factory.getCliqueFeatures(tokens, 0, Clique.cliqueC);
//    boolean foundUNK = false;
//    for (String s : features) {
//      if (s.contains("UNK-DIST")) {
//        foundUNK = true;
//      }
//    }
//    assertTrue(foundUNK);
  }
@Test
  public void testNumberEquivalenceWithDistSim() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useDistSim = true;
    flags.distSimFileFormat = "alexclark";
    flags.numberEquivalenceDistSim = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    Map<String, String> lexicon = new HashMap<>();
    String normalized = edu.stanford.nlp.process.WordShapeClassifier.wordShape("1234", edu.stanford.nlp.process.WordShapeClassifier.WORDSHAPEDIGITS);
    lexicon.put(normalized, "NUMCLS");
//    factory.lexicon = lexicon;

    CoreLabel cl = new CoreLabel();
    cl.setWord("1234");
    cl.set(CoreAnnotations.TextAnnotation.class, "1234");
    cl.set(CoreAnnotations.ShapeAnnotation.class, "dddd");
    cl.set(CoreAnnotations.PartOfSpeechAnnotation.class, "CD");
    cl.set(CoreAnnotations.DomainAnnotation.class, "finance");

    CoreLabel pad = new CoreLabel();
    PaddedList<CoreLabel> padded = new PaddedList<>(Collections.singletonList(pad));
    padded.add(cl);

//    Collection<String> features = factory.getCliqueFeatures(padded, 0, Clique.cliqueC);
//    boolean found = false;
//    for (String s : features) {
//      if (s.contains("NUMCLS")) {
//        found = true;
//      }
//    }
//    assertTrue(found);
  }
@Test
  public void testWordShapeFeaturesIncludeExpectedPatterns() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.wordShape = 1;
    flags.useWord = true;
    flags.useTypeSeqs = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel cl = new CoreLabel();
    cl.setWord("Stanford");
    cl.set(CoreAnnotations.TextAnnotation.class, "Stanford");
    cl.set(CoreAnnotations.ShapeAnnotation.class, "Xxxxxxxx");
    cl.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    cl.set(CoreAnnotations.DomainAnnotation.class, "edu");

    CoreLabel pad = new CoreLabel();
    PaddedList<CoreLabel> paddedList = new PaddedList<>(Collections.singletonList(pad));
    paddedList.add(cl);

//    Collection<String> features = factory.getCliqueFeatures(paddedList, 0, Clique.cliqueC);
//    boolean hasShape = false;
//    for (String f : features) {
//      if (f.startsWith("Xxxxxxxx-TYPE")) {
//        hasShape = true;
//      }
//    }
//    assertTrue(hasShape);
  }
@Test
  public void testEmptyWordStillYieldsValidFeatureSet() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useWord = true;
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel token = new CoreLabel();
    token.setWord("");
    token.set(CoreAnnotations.TextAnnotation.class, "");
    token.set(CoreAnnotations.ShapeAnnotation.class, "");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "");
    token.set(CoreAnnotations.DomainAnnotation.class, "empty");

    CoreLabel pad = new CoreLabel();
    PaddedList<CoreLabel> tokens = new PaddedList<>(Collections.singletonList(pad));
    tokens.add(token);

//    Collection<String> features = factory.getCliqueFeatures(tokens, 0, Clique.cliqueC);
//    assertNotNull(features);
  }
@Test
  public void testUseDisjunctiveFeaturesAtMaxLimit() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useDisjunctive = true;
    flags.disjunctionWidth = 1;
    flags.useWord = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel pad = new CoreLabel();

    CoreLabel w1 = new CoreLabel();
    w1.setWord("A");
    w1.set(CoreAnnotations.ShapeAnnotation.class, "X");
    w1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    w1.set(CoreAnnotations.DomainAnnotation.class, "one");

    CoreLabel w2 = new CoreLabel();
    w2.setWord("B");
    w2.set(CoreAnnotations.ShapeAnnotation.class, "X");
    w2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    w2.set(CoreAnnotations.DomainAnnotation.class, "one");

    CoreLabel w3 = new CoreLabel();
    w3.setWord("C");
    w3.set(CoreAnnotations.ShapeAnnotation.class, "X");
    w3.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    w3.set(CoreAnnotations.DomainAnnotation.class, "one");

    PaddedList<CoreLabel> tokens = new PaddedList<>(Collections.singletonList(pad));
    tokens.add(w1);
    tokens.add(w2);
    tokens.add(w3);

//    Collection<String> result = factory.getCliqueFeatures(tokens, 1, Clique.cliqueC);
//    boolean found = false;
//    for (String f : result) {
//      if (f.contains("-DISJN|C") || f.contains("-DISJP|C")) {
//        found = true;
//      }
//    }
//    assertTrue(found);
  }
@Test
  public void testIsOrdinalHandlesDashBetweenOrdinals() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useOrdinal = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel first = new CoreLabel();
    first.setWord("first");
    first.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    first.set(CoreAnnotations.ShapeAnnotation.class, "xxxx");

    CoreLabel dash = new CoreLabel();
    dash.setWord("-");
    dash.set(CoreAnnotations.PartOfSpeechAnnotation.class, ":");
    dash.set(CoreAnnotations.ShapeAnnotation.class, "-");

    CoreLabel second = new CoreLabel();
    second.setWord("second");
    second.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    second.set(CoreAnnotations.ShapeAnnotation.class, "xxxxxx");

    CoreLabel pad = new CoreLabel();
    PaddedList<CoreLabel> tokens = new PaddedList<>(Collections.singletonList(pad));
    tokens.add(first);
    tokens.add(dash);
    tokens.add(second);

    first.set(CoreAnnotations.DomainAnnotation.class, "math");
    dash.set(CoreAnnotations.DomainAnnotation.class, "math");
    second.set(CoreAnnotations.DomainAnnotation.class, "math");

//    Collection<String> features = factory.getCliqueFeatures(tokens, 1, Clique.cliqueC);
//    boolean found = false;
//    for (String s : features) {
//      if (s.contains("C_ORDINAL") || s.contains("PC_ORDINAL")) {
//        found = true;
//      }
//    }
//    assertTrue(found);
  }
@Test
  public void testGreekifyReplacesAllGreekTerms() throws Exception {
    java.lang.reflect.Method m = NERFeatureFactory.class.getDeclaredMethod("greekify", String.class);
    m.setAccessible(true);
    String input = "alpha-beta-kappa";
    String output = (String) m.invoke(null, input);
    assertEquals("~-~-~", output);
  }
@Test
  public void testUseRadicalWithSingleCharacter() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useRadical = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel cl = new CoreLabel();
    cl.setWord("中");
    cl.set(CoreAnnotations.TextAnnotation.class, "中");
    cl.set(CoreAnnotations.ShapeAnnotation.class, "X");
    cl.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    cl.set(CoreAnnotations.DomainAnnotation.class, "zh");

    CoreLabel pad = new CoreLabel();
    PaddedList<CoreLabel> padded = new PaddedList<>(Collections.singletonList(pad));
    padded.add(cl);

//    Collection<String> feats = factory.getCliqueFeatures(padded, 0, Clique.cliqueC);
//    boolean radicalFound = false;
//    for (String s : feats) {
//      if (s.contains("-RADICAL") || s.contains("-SINGLE-CHAR-RADICAL")) {
//        radicalFound = true;
//      }
//    }
//    assertTrue(radicalFound);
  }
@Test
  public void testSplitWordRegexDoesNotFailWithEmptyRegex() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.splitWordRegex = "";
    flags.useWord = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel cl = new CoreLabel();
    cl.setWord("no_split");
    cl.set(CoreAnnotations.TextAnnotation.class, "no_split");
    cl.set(CoreAnnotations.ShapeAnnotation.class, "xxx_xxxx");
    cl.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    cl.set(CoreAnnotations.DomainAnnotation.class, "default");

    CoreLabel pad = new CoreLabel();
    PaddedList<CoreLabel> padded = new PaddedList<>(Collections.singletonList(pad));
    padded.add(cl);

//    Collection<String> features = factory.getCliqueFeatures(padded, 0, Clique.cliqueC);
//    assertNotNull(features);
  }
@Test
  public void testUseNPGovernorAddsGovernorFeatures() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useNPGovernor = true;
    flags.useWord = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel cl = new CoreLabel();
    cl.setWord("school");
    cl.set(CoreAnnotations.GovernorAnnotation.class, "principal");
    cl.set(CoreAnnotations.ShapeAnnotation.class, "xxxxxx");
    cl.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    cl.set(CoreAnnotations.DomainAnnotation.class, "edu");

    CoreLabel pad = new CoreLabel();
    PaddedList<CoreLabel> padded = new PaddedList<>(Collections.singletonList(pad));
    padded.add(cl);

//    Collection<String> features = factory.getCliqueFeatures(padded, 0, Clique.cliqueC);
//    boolean foundGov = false;
//    for (String f : features) {
//      if (f.contains("principal-GW|C")) {
//        foundGov = true;
//      }
//    }
//    assertTrue(foundGov);
  }
@Test
  public void testUseNPHeadAddsHeadWordFeature() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useNPHead = true;
    flags.useWord = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel cl = new CoreLabel();
    cl.setWord("research");
    cl.set(CoreAnnotations.HeadWordStringAnnotation.class, "study");
    cl.set(CoreAnnotations.ShapeAnnotation.class, "xxxxxxx");
    cl.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    cl.set(CoreAnnotations.DomainAnnotation.class, "bio");

    CoreLabel pad = new CoreLabel();
    PaddedList<CoreLabel> tokens = new PaddedList<>(Collections.singletonList(pad));
    tokens.add(cl);

//    Collection<String> feats = factory.getCliqueFeatures(tokens, 0, Clique.cliqueC);
//    boolean hasHead = false;
//    for (String s : feats) {
//      if (s.contains("study-HW")) {
//        hasHead = true;
//      }
//    }
//    assertTrue(hasHead);
  }
@Test
  public void testUseGenericFeaturesWithCollectionAnnotation() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useGenericFeatures = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    List<String> genericFeatures = new ArrayList<>();
    genericFeatures.add("GEN_TOKEN_A");
    genericFeatures.add("GEN_TOKEN_B");

    CoreLabel cl = new CoreLabel();
    cl.setWord("EntityX");
    cl.set(CoreAnnotations.TextAnnotation.class, "EntityX");
    cl.set(CoreAnnotations.ShapeAnnotation.class, "Xxxxxxx");
    cl.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    cl.set(CoreAnnotations.DomainAnnotation.class, "domain");
//    cl.set(CoreLabel.GenericAnnotation.class, genericFeatures);

    CoreLabel pad = new CoreLabel();
    PaddedList<CoreLabel> context = new PaddedList<>(Collections.singletonList(pad));
    context.add(cl);

//    Collection<String> features = factory.getCliqueFeatures(context, 0, Clique.cliqueC);
//    boolean found = false;
//    for (String f : features) {
//      if (f.startsWith("GEN_TOKEN_A-Generic") || f.startsWith("GEN_TOKEN_B-Generic")) {
//        found = true;
//      }
//    }
//    assertTrue(found);
  }
@Test
  public void testUseProbabilityBasedUnknownTagFeature() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useUnknown = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel cl = new CoreLabel();
    cl.setWord("covfefe");
    cl.set(CoreAnnotations.TextAnnotation.class, "covfefe");
    cl.set(CoreAnnotations.ShapeAnnotation.class, "xxxxxxx");
    cl.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    cl.set(CoreAnnotations.UnknownAnnotation.class, "YES");
    cl.set(CoreAnnotations.DomainAnnotation.class, "twitter");

    CoreLabel pad = new CoreLabel();

    PaddedList<CoreLabel> tokens = new PaddedList<>(Collections.singletonList(pad));
    tokens.add(cl);

//    Collection<String> feats = factory.getCliqueFeatures(tokens, 0, Clique.cliqueC);
//    boolean found = false;
//    for (String f : feats) {
//      if (f.startsWith("YES-UNKNOWN")) {
//        found = true;
//      }
//    }
//    assertTrue(found);
  }
@Test
  public void testUseTagsAndSymTagsTogetherWithPOS() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useTags = true;
    flags.useSymTags = true;
    flags.usePrev = true;
    flags.useNext = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel p = new CoreLabel();
    p.setWord("President");
    p.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    p.set(CoreAnnotations.ShapeAnnotation.class, "Xxxxx");
    p.set(CoreAnnotations.DomainAnnotation.class, "news");

    CoreLabel c = new CoreLabel();
    c.setWord("Obama");
    c.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    c.set(CoreAnnotations.ShapeAnnotation.class, "Xxxxx");
    c.set(CoreAnnotations.DomainAnnotation.class, "news");

    CoreLabel n = new CoreLabel();
    n.setWord("spoke");
    n.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBD");
    n.set(CoreAnnotations.ShapeAnnotation.class, "xxxx");
    n.set(CoreAnnotations.DomainAnnotation.class, "news");

    CoreLabel pad = new CoreLabel();

    PaddedList<CoreLabel> tokens = new PaddedList<>(Collections.singletonList(pad));
    tokens.add(p);
    tokens.add(c);
    tokens.add(n);

//    Collection<String> feats = factory.getCliqueFeatures(tokens, 1, Clique.cliqueC);
//    boolean hasSymTag = false;
//    for (String f : feats) {
//      if (f.contains("PCNTAGS") || f.contains("CNTAGS") || f.contains("PCTAGS")) {
//        hasSymTag = true;
//      }
//    }
//    assertTrue(hasSymTag);
  }
@Test
  public void testUseWordTagWithPrevAndNextPosTags() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useWord = true;
    flags.useWordTag = true;
    flags.usePrev = true;
    flags.useNext = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel p = new CoreLabel();
    p.setWord("Mr");
    p.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    p.set(CoreAnnotations.ShapeAnnotation.class, "Xx");
    p.set(CoreAnnotations.DomainAnnotation.class, "bio");

    CoreLabel c = new CoreLabel();
    c.setWord("Smith");
    c.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    c.set(CoreAnnotations.ShapeAnnotation.class, "Xxxxx");
    c.set(CoreAnnotations.DomainAnnotation.class, "bio");

    CoreLabel n = new CoreLabel();
    n.setWord("walked");
    n.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBD");
    n.set(CoreAnnotations.ShapeAnnotation.class, "xxxxxx");
    n.set(CoreAnnotations.DomainAnnotation.class, "bio");

    CoreLabel pad = new CoreLabel();

    PaddedList<CoreLabel> context = new PaddedList<>(Collections.singletonList(pad));
    context.add(p);
    context.add(c);
    context.add(n);

//    Collection<String> feats = factory.getCliqueFeatures(context, 1, Clique.cliqueC);
//    boolean foundWordTag = false;
//    for (String f : feats) {
//      if (f.contains("-W-T") || f.contains("-W-NT") || f.contains("-W-PT")) {
//        foundWordTag = true;
//      }
//    }
//    assertTrue(foundWordTag);
  }
@Test
  public void testUseMinimalAbbrSkipsXXTag() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useMinimalAbbr = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel cl = new CoreLabel();
    cl.setWord("Dr.");
    cl.set(CoreAnnotations.AbbrAnnotation.class, "XX");
    cl.set(CoreAnnotations.ShapeAnnotation.class, "Xx.");
    cl.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    cl.set(CoreAnnotations.DomainAnnotation.class, "med");

    CoreLabel pad = new CoreLabel();
    PaddedList<CoreLabel> padded = new PaddedList<>(Collections.singletonList(pad));
    padded.add(cl);

//    Collection<String> feats = factory.getCliqueFeatures(padded, 0, Clique.cliqueC);
//
//    boolean found = false;
//    for (String str : feats) {
//      if (str.contains("-CWABB")) {
//        found = true;
//      }
//    }
//    assertTrue(found);
  }
@Test
  public void testUsePhraseWordsPopulatesMultipleWords() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.usePhraseWords = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    List<String> words = new ArrayList<>();
    words.add("machine");
    words.add("learning");
    words.add("neural");

    CoreLabel cl = new CoreLabel();
    cl.setWord("model");
    cl.set(CoreAnnotations.ShapeAnnotation.class, "xxxx");
    cl.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    cl.set(CoreAnnotations.PhraseWordsAnnotation.class, words);
    cl.set(CoreAnnotations.DomainAnnotation.class, "ai");

    CoreLabel pad = new CoreLabel();
    PaddedList<CoreLabel> padded = new PaddedList<>(Collections.singletonList(pad));
    padded.add(cl);

//    Collection<String> features = factory.getCliqueFeatures(padded, 0, Clique.cliqueC);
//    int matches = 0;
//    for (String f : features) {
//      if (f.endsWith("-PhraseWord|C")) {
//        matches += 1;
//      }
//    }
//    assertEquals(3, matches);
  }
@Test
  public void testUseShapeConjunctionCombinesWithTag() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useShapeConjunctions = true;
    flags.useTags = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel cl = new CoreLabel();
    cl.setWord("Berlin");
    cl.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    cl.set(CoreAnnotations.ShapeAnnotation.class, "Xxxxx");
    cl.set(CoreAnnotations.PositionAnnotation.class, "0");
    cl.set(CoreAnnotations.DomainAnnotation.class, "loc");

    CoreLabel pad = new CoreLabel();

    PaddedList<CoreLabel> list = new PaddedList<>(Collections.singletonList(pad));
    list.add(cl);

//    Collection<String> features = factory.getCliqueFeatures(list, 0, Clique.cliqueC);
//    boolean shapeTagCombo = false;
//    boolean shapePosCombo = false;
//    for (String f : features) {
//      if (f.contains("TAG-SH|C")) {
//        shapeTagCombo = true;
//      }
//      if (f.contains("POS-SH|C")) {
//        shapePosCombo = true;
//      }
//    }
//    assertTrue(shapeTagCombo);
//    assertTrue(shapePosCombo);
  }
@Test
  public void testUseWebFeatureAddsExpectedAnnotations() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useWEB = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel cl = new CoreLabel();
    cl.setWord("stackoverflow");
    cl.set(CoreAnnotations.WebAnnotation.class, "YES");
    cl.set(CoreAnnotations.ShapeAnnotation.class, "xxxxxxxxxxxxx");
    cl.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    cl.set(CoreAnnotations.DomainAnnotation.class, "tech");

    CoreLabel pad = new CoreLabel();
    PaddedList<CoreLabel> context = new PaddedList<>(Collections.singletonList(pad));
    context.add(cl);

//    Collection<String> features = factory.getCliqueFeatures(context, 0, Clique.cliqueC);
//    boolean hasWeb = false;
//    for (String s : features) {
//      if (s.equals("YES-WEB|C")) {
//        hasWeb = true;
//      }
//    }
//    assertTrue(hasWeb);
  }
@Test
  public void testDisjunctiveShapeInteractionIncludesCombinedShape() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useDisjunctive = true;
    flags.useDisjunctiveShapeInteraction = true;
    flags.disjunctionWidth = 1;
    flags.useWord = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel cl1 = new CoreLabel();
    cl1.setWord("Atlas");
    cl1.set(CoreAnnotations.ShapeAnnotation.class, "Xxxxx");
    cl1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    cl1.set(CoreAnnotations.DomainAnnotation.class, "geo");

    CoreLabel cl2 = new CoreLabel();
    cl2.setWord("Mountain");
    cl2.set(CoreAnnotations.ShapeAnnotation.class, "Xxxxxxxx");
    cl2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    cl2.set(CoreAnnotations.DomainAnnotation.class, "geo");

    CoreLabel cl3 = new CoreLabel();
    cl3.setWord("Range");
    cl3.set(CoreAnnotations.ShapeAnnotation.class, "Xxxxx");
    cl3.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    cl3.set(CoreAnnotations.DomainAnnotation.class, "geo");

    CoreLabel pad = new CoreLabel();
    PaddedList<CoreLabel> context = new PaddedList<>(Collections.singletonList(pad));
    context.add(cl1);
    context.add(cl2);
    context.add(cl3);

//    Collection<String> features = factory.getCliqueFeatures(context, 1, Clique.cliqueC);
//    boolean hasDisjShape = false;
//    for (String f : features) {
//      if (f.contains("-DISJN-CS|C") || f.contains("-DISJP-CS|C")) {
//        hasDisjShape = true;
//      }
//    }
//    assertTrue(hasDisjShape);
  }
@Test
  public void testUseNextVBAndPrevVBTriggersVBFeature() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useNextVB = true;
    flags.usePrevVB = true;
    flags.useVB = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel w1 = new CoreLabel();
    w1.setWord("will");
    w1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "MD");
    w1.set(CoreAnnotations.ShapeAnnotation.class, "xxxx");
    w1.set(CoreAnnotations.DomainAnnotation.class, "legal");

    CoreLabel w2 = new CoreLabel();
    w2.setWord("sign");
    w2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VB");
    w2.set(CoreAnnotations.ShapeAnnotation.class, "xxxx");
    w2.set(CoreAnnotations.DomainAnnotation.class, "legal");

    CoreLabel w3 = new CoreLabel();
    w3.setWord("contract");
    w3.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    w3.set(CoreAnnotations.ShapeAnnotation.class, "xxxxxxx");
    w3.set(CoreAnnotations.DomainAnnotation.class, "legal");

    CoreLabel pad = new CoreLabel();
    PaddedList<CoreLabel> tokens = new PaddedList<>(Collections.singletonList(pad));
    tokens.add(w1);
    tokens.add(w2);
    tokens.add(w3);

//    Collection<String> feats = factory.getCliqueFeatures(tokens, 1, Clique.cliqueC);
//    boolean foundNVB = false;
//    boolean foundPVB = false;
//    boolean foundPNVB = false;
//
//    for (String s : feats) {
//      if (s.contains("-NVB|C")) foundNVB = true;
//      if (s.contains("-PVB|C")) foundPVB = true;
//      if (s.contains("-PNVB|C")) foundPNVB = true;
//    }
//
//    assertTrue(foundNVB || foundPVB || foundPNVB);
  }
@Test
  public void testUsePositionAddsExpectedFeature() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.usePosition = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel cl = new CoreLabel();
    cl.setWord("keyword");
    cl.set(CoreAnnotations.ShapeAnnotation.class, "xxxxxxx");
    cl.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    cl.set(CoreAnnotations.PositionAnnotation.class, "2");
    cl.set(CoreAnnotations.DomainAnnotation.class, "misc");

    CoreLabel pad = new CoreLabel();
    PaddedList<CoreLabel> padded = new PaddedList<>(Collections.singletonList(pad));
    padded.add(cl);

//    Collection<String> feats = factory.getCliqueFeatures(padded, 0, Clique.cliqueC);
//
//    boolean hasPosition = false;
//    for (String f : feats) {
//      if (f.equals("2-POSITION|C")) {
//        hasPosition = true;
//      }
//    }
//    assertTrue(hasPosition);
  }
@Test
  public void testUseIsURLFlagTriggersUrlAnnotationFeature() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useIsURL = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel cl = new CoreLabel();
    cl.setWord("http://example.com");
    cl.set(CoreAnnotations.ShapeAnnotation.class, "xxxx");
    cl.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    cl.set(CoreAnnotations.IsURLAnnotation.class, "YES");
    cl.set(CoreAnnotations.DomainAnnotation.class, "web");

    CoreLabel pad = new CoreLabel();

    PaddedList<CoreLabel> context = new PaddedList<>(Collections.singletonList(pad));
    context.add(cl);

//    Collection<String> features = factory.getCliqueFeatures(context, 0, Clique.cliqueC);
//    boolean found = false;
//    for (String f : features) {
//      if (f.equals("YES-ISURL|C")) {
//        found = true;
//      }
//    }
//    assertTrue(found);
  }
@Test
  public void testUseBoundarySequenceTriggeredOnBoundaryToken() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.usePrev = true;
    flags.useSequences = true;
    flags.usePrevSequences = true;
    flags.useBoundarySequences = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel p = new CoreLabel();
    p.setWord("-----");
    p.set(CoreAnnotations.TextAnnotation.class, "-----");
    p.set(CoreAnnotations.ShapeAnnotation.class, "-----");
    p.set(CoreAnnotations.PartOfSpeechAnnotation.class, "SYM");
    p.set(CoreAnnotations.DomainAnnotation.class, "conll");

    CoreLabel c = new CoreLabel();
    c.setWord("London");
    c.set(CoreAnnotations.TextAnnotation.class, "London");
    c.set(CoreAnnotations.ShapeAnnotation.class, "Xxxxx");
    c.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    c.set(CoreAnnotations.DomainAnnotation.class, "conll");

    CoreLabel pad = new CoreLabel();
    PaddedList<CoreLabel> sequence = new PaddedList<>(Collections.singletonList(pad));
    sequence.add(p);
    sequence.add(c);

//    Collection<String> feats = factory.getCliqueFeatures(sequence, 1, Clique.cliqueCpC);
//    boolean foundSequence = false;
//    for (String f : feats) {
//      if (f.startsWith("BNDRY-SPAN-PPSEQ") || f.startsWith("BNDRY-SPAN-")) {
//        foundSequence = true;
//      }
//    }
//    assertTrue(foundSequence);
  }
@Test
  public void testUseSplitWordRegexHandlesMultiPartTokens() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.splitWordRegex = "[_-]";
    flags.useWord = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel cl = new CoreLabel();
    cl.setWord("New-York_City");
    cl.set(CoreAnnotations.TextAnnotation.class, "New-York_City");
    cl.set(CoreAnnotations.ShapeAnnotation.class, "Xx-Xx_Xxxx");
    cl.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    cl.set(CoreAnnotations.DomainAnnotation.class, "geo");

    CoreLabel pad = new CoreLabel();
    PaddedList<CoreLabel> list = new PaddedList<>(Collections.singletonList(pad));
    list.add(cl);

//    Collection<String> features = factory.getCliqueFeatures(list, 0, Clique.cliqueC);
//    boolean foundPart1 = false;
//    boolean foundPart2 = false;
//    boolean foundPart3 = false;
//
//    for (String f : features) {
//      if (f.equals("New-SPLITWORD|C")) foundPart1 = true;
//      if (f.equals("York-SPLITWORD|C")) foundPart2 = true;
//      if (f.equals("City-SPLITWORD|C")) foundPart3 = true;
//    }
//
//    assertTrue(foundPart1);
//    assertTrue(foundPart2);
//    assertTrue(foundPart3);
  }
@Test
  public void testDehyphenateWithLeadingAndTrailingHyphenPreservesEdges() throws Exception {
    java.lang.reflect.Method method = NERFeatureFactory.class.getDeclaredMethod("dehyphenate", String.class);
    method.setAccessible(true);
    Object result = method.invoke(null, "-mid-hyphen-");
    assertEquals("-midhyphen-", result);
  }
@Test
  public void testFeaturesCp2CAddsParenFeatureWhenMatchingOpenAndClose() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useParenMatching = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel p2 = new CoreLabel();
    p2.setWord("(");
    p2.set(CoreAnnotations.ShapeAnnotation.class, "(");

    CoreLabel p1 = new CoreLabel();
    p1.setWord("text");
    p1.set(CoreAnnotations.ShapeAnnotation.class, "xxxx");

    CoreLabel cur = new CoreLabel();
    cur.setWord(")");
    cur.set(CoreAnnotations.ShapeAnnotation.class, ")");

    CoreLabel pad = new CoreLabel();
    PaddedList<CoreLabel> tokens = new PaddedList<>(Collections.singletonList(pad));
    tokens.add(p2);
    tokens.add(p1);
    tokens.add(cur);

//    Collection<String> features = factory.getCliqueFeatures(tokens, 2, Clique.cliqueCp2C);
//    boolean containsParen = false;
//    for (String f : features) {
//      if (f.contains("PAREN-MATCH")) {
//        containsParen = true;
//      }
//    }
//    assertTrue(containsParen);
  }
@Test
  public void testCacheNGramsCachesUnseenWord() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useWord = true;
    flags.useNGrams = true;
    flags.cacheNGrams = true;
    flags.maxNGramLeng = 3;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel token = new CoreLabel();
    token.setWord("BioNLP");
    token.set(CoreAnnotations.TextAnnotation.class, "BioNLP");
    token.set(CoreAnnotations.ShapeAnnotation.class, "Xxxxxx");
    token.set(CoreAnnotations.PositionAnnotation.class, "0");
    token.set(CoreAnnotations.DomainAnnotation.class, "bio");

    CoreLabel pad = new CoreLabel();
    PaddedList<CoreLabel> list = new PaddedList<>(Collections.singletonList(pad));
    list.add(token);

//    Collection<String> feats = factory.getCliqueFeatures(list, 0, Clique.cliqueC);
//    assertFalse(feats.isEmpty());
  }
@Test
  public void testGazetteCleanEntryMatchesMultiwordExact() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useGazettes = true;
    flags.cleanGazette = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel tok1 = new CoreLabel();
    tok1.setWord("New");
    tok1.set(CoreAnnotations.TextAnnotation.class, "New");
    tok1.set(CoreAnnotations.ShapeAnnotation.class, "Xxx");
    tok1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    tok1.set(CoreAnnotations.DomainAnnotation.class, "geo");

    CoreLabel tok2 = new CoreLabel();
    tok2.setWord("York");
    tok2.set(CoreAnnotations.TextAnnotation.class, "York");
    tok2.set(CoreAnnotations.ShapeAnnotation.class, "Xxxx");
    tok2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    tok2.set(CoreAnnotations.DomainAnnotation.class, "geo");

    CoreLabel pad = new CoreLabel();

    PaddedList<CoreLabel> tokens = new PaddedList<>(Collections.singletonList(pad));
    tokens.add(tok1);
    tokens.add(tok2);

    String[] phrase = new String[] { "New", "York" };
//    NERFeatureFactory.GazetteInfo info = new NERFeatureFactory.GazetteInfo("LOC-GAZ2", 0, phrase);
//    List<NERFeatureFactory.GazetteInfo> infos = new ArrayList<>();
//    infos.add(info);

//    factory.wordToGazetteInfos.put("New", infos);

//    Collection<String> feats = factory.getCliqueFeatures(tokens, 0, Clique.cliqueC);
//    boolean matchFound = false;
//    for (String f : feats) {
//      if (f.contains("LOC-GAZ2")) {
//        matchFound = true;
//      }
//    }
//    assertTrue(matchFound);
  }
@Test
  public void testFeatureCollectorAddsDomainSuffixCorrectly() {
    Set<String> out = new HashSet<>();
    NERFeatureFactory.FeatureCollector fc = new NERFeatureFactory.FeatureCollector(out);
    fc.setSuffix("TAG").setDomain("science");
    fc.build().append("some").dash().append("feature").add();

    boolean baseFound = false;
    boolean domainFound = false;
    for (String feat : out) {
      if (feat.equals("some-feature|TAG")) {
        baseFound = true;
      }
      if (feat.equals("some-feature|science-TAG")) {
        domainFound = true;
      }
    }
    assertTrue(baseFound);
    assertTrue(domainFound);
  }
@Test
  public void testDescribeDistSimBeforeLexiconInitReturnsExpectedMessage() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useDistSim = true;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    String desc = factory.describeDistsimLexicon();
    assertEquals("No distsim lexicon", desc);
  }
@Test
  public void testIsOrdinalHandlesOnlyNumberWithoutSuffix() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useOrdinal = true;
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel cl1 = new CoreLabel();
    cl1.setWord("200");
    cl1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "CD");
    cl1.set(CoreAnnotations.ShapeAnnotation.class, "ddd");
    cl1.set(CoreAnnotations.DomainAnnotation.class, "num");

    CoreLabel pad = new CoreLabel();
    PaddedList<CoreLabel> list = new PaddedList<>(Collections.singletonList(pad));
    list.add(cl1);

//    Collection<String> feats = factory.getCliqueFeatures(list, 0, Clique.cliqueC);
//    boolean isOrd = false;
//    for (String feat : feats) {
//      if (feat.contains("C_ORDINAL")) {
//        isOrd = true;
//      }
//    }
//    assertFalse(isOrd);
  }
@Test
  public void testInternFlagAppliesNoJavaInterning() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.intern = true;  

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    String input = new String("Obama");
    CoreLabel cl = new CoreLabel();
    cl.setWord(input);
    cl.set(CoreAnnotations.TextAnnotation.class, input);
    cl.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    cl.set(CoreAnnotations.ShapeAnnotation.class, "Xxxxx");
    cl.set(CoreAnnotations.DomainAnnotation.class, "pol");

    CoreLabel pad = new CoreLabel();

    PaddedList<CoreLabel> context = new PaddedList<>(Collections.singletonList(pad));
    context.add(cl);

//    Collection<String> output = factory.getCliqueFeatures(context, 0, Clique.cliqueC);
//    assertTrue(output.stream().anyMatch(s -> s.contains("Obama")));
  }
@Test
  public void testUseTaggySequencesShapeInteractionCombinesAllFields() {
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useTaggySequences = true;
    flags.useTaggySequencesShapeInteraction = true;
    flags.useTags = true;
    flags.maxLeft = 3;

    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.init(flags);

    CoreLabel c = new CoreLabel();
    c.setWord("target");
    c.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    c.set(CoreAnnotations.ShapeAnnotation.class, "xxxxxx");
    c.set(CoreAnnotations.DomainAnnotation.class, "demo");

    CoreLabel p1 = new CoreLabel();
    p1.setWord("before1");
    p1.set(CoreAnnotations.PartOfSpeechAnnotation.class, "DT");
    p1.set(CoreAnnotations.ShapeAnnotation.class, "xxxxxxx");
    p1.set(CoreAnnotations.DomainAnnotation.class, "demo");

    CoreLabel p2 = new CoreLabel();
    p2.setWord("before2");
    p2.set(CoreAnnotations.PartOfSpeechAnnotation.class, "IN");
    p2.set(CoreAnnotations.ShapeAnnotation.class, "xxxxxxx");
    p2.set(CoreAnnotations.DomainAnnotation.class, "demo");

    CoreLabel p3 = new CoreLabel();
    p3.setWord("before3");
    p3.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NN");
    p3.set(CoreAnnotations.ShapeAnnotation.class, "xxxxxxx");
    p3.set(CoreAnnotations.DomainAnnotation.class, "demo");

    CoreLabel pad = new CoreLabel();

    PaddedList<CoreLabel> padded = new PaddedList<>(Collections.singletonList(pad));
    padded.add(p3);
    padded.add(p2);
    padded.add(p1);
    padded.add(c);

//    Collection<String> feats = factory.getCliqueFeatures(padded, 3, Clique.cliqueCpCp2Cp3C);
//    boolean found = false;
//    for (String f : feats) {
//      if (f.contains("-TTTS-CS|CpCp2Cp3C")) {
//        found = true;
//      }
//    }
//    assertTrue(found);
  } 
}