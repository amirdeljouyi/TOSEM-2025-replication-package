public class NERFeatureFactory_wosr_3_GPTLLMTest { 

 @Test
  public void testDescribeDistsimLexicon_noLexicon() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    String description = factory.describeDistsimLexicon();
    assertEquals("No distsim lexicon", description);
  }
@Test
  public void testInit_cachesLexiconIfUseDistSim() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useDistSim = false;
    factory.init(flags);
    String description = factory.describeDistsimLexicon();
    assertEquals("No distsim lexicon", description);
  }
@Test
  public void testGetCliqueFeatures_throwsExceptionOnInvalidClique() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    CoreLabel word = new CoreLabel();
    word.set(CoreAnnotations.DomainAnnotation.class, "test");
    List<CoreLabel> list = new ArrayList<>();
    list.add(word);
    PaddedList<CoreLabel> paddedList = new PaddedList<>(list, CoreLabel.factory(), 1);
    paddedList.add(word);

    try {
      factory.getCliqueFeatures(paddedList, 0, new Clique("INVALID"));
      fail("Expected IllegalArgumentException for invalid clique");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("Unknown clique"));
    }
  }
@Test
  public void testGetCliqueFeatures_cliqueC_basic() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();

    SeqClassifierFlags flags = new SeqClassifierFlags();
    factory.init(flags);

    CoreLabel token = new CoreLabel();
    token.setWord("Stanford");
    token.set(CoreAnnotations.DomainAnnotation.class, "test");
    token.set(CoreAnnotations.ShapeAnnotation.class, "Xxxxxx");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");

    CoreLabel pad = new CoreLabel();
    pad.setWord("<PAD>");
    pad.set(CoreAnnotations.ShapeAnnotation.class, "XXXX");

    List<CoreLabel> list = new ArrayList<>();
    list.add(pad);
    list.add(pad);
    list.add(token);
    list.add(pad);
    list.add(pad);
    PaddedList<CoreLabel> paddedList = new PaddedList<>(list, CoreLabel.factory(), 2);

    Collection<String> features = factory.getCliqueFeatures(paddedList, 2, NERFeatureFactory.cliqueC);

    assertNotNull(features);
    assertTrue(features.contains("Xxxxxx|C"));
  }
@Test
  public void testDistSimAnnotate_assignsUnknownWhenWordNotInLexicon() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.useDistSim = true;
    flags.distSimLexicon = "dummy.txt";
    flags.inputEncoding = "UTF-8";
    flags.unknownWordDistSimClass = "UNK";

    CoreLabel token = new CoreLabel();
    token.setWord("UnknownWord");
    token.set(CoreAnnotations.ShapeAnnotation.class, "Xxxxxx");

    PaddedList<CoreLabel> padded = new PaddedList<>(Collections.singletonList(token));
    factory.init(flags);
    factory.clearMemory();

    token.setWord("foo");
    factory.init(flags);
    factory.clearMemory();
    token.set(CoreAnnotations.ShapeAnnotation.class, "Xxxx");

    
    factory.init(flags);
    token.remove(CoreAnnotations.DistSimAnnotation.class);
    factory.distSimAnnotate(padded);

    assertEquals("UNK", token.get(CoreAnnotations.DistSimAnnotation.class));
  }
@Test
  public void testGreekifyReplacesGreekWordsWithSymbol() throws Exception {
    java.lang.reflect.Method greekify = NERFeatureFactory.class.getDeclaredMethod("greekify", String.class);
    greekify.setAccessible(true);

    String input = "alphaWave";
    String output = (String) greekify.invoke(null, input);
    assertEquals("~Wave", output);
  }
@Test
  public void testDehyphenate_removesInnerHyphensOnly() throws Exception {
    java.lang.reflect.Method dehyphenate = NERFeatureFactory.class.getDeclaredMethod("dehyphenate", String.class);
    dehyphenate.setAccessible(true);

    String word = "<pre-hyphen-ated>";
    String result = (String) dehyphenate.invoke(null, word);
    assertEquals("<prehyphenated>", result);
  }
@Test
  public void testIsNameCase_acceptsCapitalizedLowerRest() throws Exception {
    java.lang.reflect.Method isNameCase = NERFeatureFactory.class.getDeclaredMethod("isNameCase", String.class);
    isNameCase.setAccessible(true);

    boolean result = (Boolean) isNameCase.invoke(null, "Stanford");
    assertTrue(result);
  }
@Test
  public void testIsNameCase_rejectsAllUpper() throws Exception {
    java.lang.reflect.Method isNameCase = NERFeatureFactory.class.getDeclaredMethod("isNameCase", String.class);
    isNameCase.setAccessible(true);

    boolean result = (Boolean) isNameCase.invoke(null, "USA");
    assertFalse(result);
  }
@Test
  public void testNoUpperCase_allLowercase() throws Exception {
    java.lang.reflect.Method noUpperCase = NERFeatureFactory.class.getDeclaredMethod("noUpperCase", String.class);
    noUpperCase.setAccessible(true);

    boolean result = (Boolean) noUpperCase.invoke(null, "lowercase");
    assertTrue(result);
  }
@Test
  public void testNoUpperCase_hasUppercase() throws Exception {
    java.lang.reflect.Method noUpperCase = NERFeatureFactory.class.getDeclaredMethod("noUpperCase", String.class);
    noUpperCase.setAccessible(true);

    boolean result = (Boolean) noUpperCase.invoke(null, "HasUpper");
    assertFalse(result);
  }
@Test
  public void testHasLetter_pureNumber_returnsFalse() throws Exception {
    java.lang.reflect.Method hasLetter = NERFeatureFactory.class.getDeclaredMethod("hasLetter", String.class);
    hasLetter.setAccessible(true);

    boolean result = (Boolean) hasLetter.invoke(null, "123456");
    assertFalse(result);
  }
@Test
  public void testHasLetter_containsLetter_returnsTrue() throws Exception {
    java.lang.reflect.Method hasLetter = NERFeatureFactory.class.getDeclaredMethod("hasLetter", String.class);
    hasLetter.setAccessible(true);

    boolean result = (Boolean) hasLetter.invoke(null, "A123");
    assertTrue(result);
  }
@Test
  public void testClearMemory_resetsLexiconAndSubstrings() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    factory.clearMemory();

    factory.wordToSubstrings.put("key", new ArrayList<>());
    factory.lexicon = new HashMap<>();
    factory.lexicon.put("foo", "bar");

    factory.clearMemory();

    assertTrue(factory.wordToSubstrings.isEmpty());
    assertNull(factory.lexicon);
  }
@Test
  public void testGetCliqueFeatures_cliqueCpC_addsCpCFeatures() {
    NERFeatureFactory<CoreLabel> factory = new NERFeatureFactory<>();
    SeqClassifierFlags flags = new SeqClassifierFlags();
    flags.usePrev = true;
    flags.useSequences = true;
    flags.usePrevSequences = true;
    flags.useInternal = true;
    flags.useExternal = true;
    factory.init(flags);

    CoreLabel token = new CoreLabel();
    token.setWord("word");
    token.set(CoreAnnotations.DomainAnnotation.class, "test");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
    token.set(CoreAnnotations.ShapeAnnotation.class, "Xxxxx");

    List<CoreLabel> tokens = Arrays.asList(
        new CoreLabel(), new CoreLabel(), token, new CoreLabel(), new CoreLabel()
    );

    for (CoreLabel cl : tokens) {
      cl.set(CoreAnnotations.PartOfSpeechAnnotation.class, "NNP");
      cl.setWord("word");
      cl.set(CoreAnnotations.ShapeAnnotation.class, "Xxxxx");
    }

    PaddedList<CoreLabel> padded = new PaddedList<>(tokens, CoreLabel.factory(), 2);

    Collection<String> features = factory.getCliqueFeatures(padded, 2, NERFeatureFactory.cliqueCpC);

    assertTrue(features.contains("PSEQ|CpC"));
    assertTrue(features.contains("word|PSEQW"));
  } 
}