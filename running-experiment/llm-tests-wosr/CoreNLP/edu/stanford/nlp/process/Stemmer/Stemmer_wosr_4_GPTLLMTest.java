public class Stemmer_wosr_4_GPTLLMTest { 

 @Test
  public void testBasicPluralForms() {
    Stemmer stemmer1 = new Stemmer();
    String result1 = stemmer1.stem("cats");
    assertEquals("cat", result1);

    Stemmer stemmer2 = new Stemmer();
    String result2 = stemmer2.stem("dogs");
    assertEquals("dog", result2);

    Stemmer stemmer3 = new Stemmer();
    String result3 = stemmer3.stem("foxes");
    assertEquals("fox", result3);
  }
@Test
  public void testPluralsEndingInIes() {
    Stemmer stemmer1 = new Stemmer();
    String result1 = stemmer1.stem("ponies");
    assertEquals("poni", result1);

    Stemmer stemmer2 = new Stemmer();
    String result2 = stemmer2.stem("ties");
    assertEquals("ti", result2);
  }
@Test
  public void testPastTenseForms() {
    Stemmer stemmer1 = new Stemmer();
    String result1 = stemmer1.stem("agreed");
    assertEquals("agree", result1);

    Stemmer stemmer2 = new Stemmer();
    String result2 = stemmer2.stem("disabled");
    assertEquals("disable", result2);

    Stemmer stemmer3 = new Stemmer();
    String result3 = stemmer3.stem("feed");
    assertEquals("feed", result3);
  }
@Test
  public void testVerbIngForms() {
    Stemmer stemmer1 = new Stemmer();
    String result1 = stemmer1.stem("matting");
    assertEquals("mat", result1);

    Stemmer stemmer2 = new Stemmer();
    String result2 = stemmer2.stem("mating");
    assertEquals("mate", result2);

    Stemmer stemmer3 = new Stemmer();
    String result3 = stemmer3.stem("messing");
    assertEquals("mess", result3);

    Stemmer stemmer4 = new Stemmer();
    String result4 = stemmer4.stem("milling");
    assertEquals("mill", result4);

    Stemmer stemmer5 = new Stemmer();
    String result5 = stemmer5.stem("meeting");
    assertEquals("meet", result5);
  }
@Test
  public void testComplexSuffixRemoval() {
    Stemmer stemmer1 = new Stemmer();
    String result1 = stemmer1.stem("relational");
    assertEquals("relate", result1);

    Stemmer stemmer2 = new Stemmer();
    String result2 = stemmer2.stem("conditional");
    assertEquals("condition", result2);

    Stemmer stemmer3 = new Stemmer();
    String result3 = stemmer3.stem("rational");
    assertEquals("rational", result3);

    Stemmer stemmer4 = new Stemmer();
    String result4 = stemmer4.stem("valenci");
    assertEquals("valence", result4);

    Stemmer stemmer5 = new Stemmer();
    String result5 = stemmer5.stem("hesitanci");
    assertEquals("hesitance", result5);
  }
@Test
  public void testSuffixConversionStep3() {
    Stemmer stemmer1 = new Stemmer();
    String result1 = stemmer1.stem("digitizer");
    assertEquals("digitize", result1);

    Stemmer stemmer2 = new Stemmer();
    String result2 = stemmer2.stem("rationalize");
    assertEquals("rational", result2);

    Stemmer stemmer3 = new Stemmer();
    String result3 = stemmer3.stem("activator");
    assertEquals("activate", result3);
  }
@Test
  public void testSuffixConversionStep4() {
    Stemmer stemmer1 = new Stemmer();
    String result1 = stemmer1.stem("electricity");
    assertEquals("electric", result1);

    Stemmer stemmer2 = new Stemmer();
    String result2 = stemmer2.stem("nationalism");
    assertEquals("nation", result2);

    Stemmer stemmer3 = new Stemmer();
    String result3 = stemmer3.stem("hopefulness");
    assertEquals("hope", result3);
  }
@Test
  public void testStep5Removals() {
    Stemmer stemmer1 = new Stemmer();
    String result1 = stemmer1.stem("adjustment");
    assertEquals("adjust", result1);

    Stemmer stemmer2 = new Stemmer();
    String result2 = stemmer2.stem("replacement");
    assertEquals("replac", result2);

    Stemmer stemmer3 = new Stemmer();
    String result3 = stemmer3.stem("experimental");
    assertEquals("experiment", result3);

    Stemmer stemmer4 = new Stemmer();
    String result4 = stemmer4.stem("activation");
    assertEquals("activ", result4);
  }
@Test
  public void testStep6FinalEHandling() {
    Stemmer stemmer1 = new Stemmer();
    String result1 = stemmer1.stem("probate");
    assertEquals("probat", result1);

    Stemmer stemmer2 = new Stemmer();
    String result2 = stemmer2.stem("rate");
    assertEquals("rate", result2);

    Stemmer stemmer3 = new Stemmer();
    String result3 = stemmer3.stem("lovable");
    assertEquals("lovabl", result3);
  }
@Test
  public void testEdgeCasesEmptyString() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("");
    assertEquals("", result);
  }
@Test
  public void testShortWords() {
    Stemmer stemmer1 = new Stemmer();
    String result1 = stemmer1.stem("no");
    assertEquals("no", result1);

    Stemmer stemmer2 = new Stemmer();
    String result2 = stemmer2.stem("on");
    assertEquals("on", result2);
  }
@Test
  public void testRepeatedCharactersAndDoubleConsonants() {
    Stemmer stemmer1 = new Stemmer();
    String result1 = stemmer1.stem("falling");
    assertEquals("fall", result1);

    Stemmer stemmer2 = new Stemmer();
    String result2 = stemmer2.stem("misses");
    assertEquals("miss", result2);
  }
@Test
  public void testApplyMethodWithWord() {
    Word input = new Word("running");
    Stemmer stemmer = new Stemmer();
    Word output = stemmer.apply(input);
    assertEquals(new Word("run"), output);
  }
@Test
  public void testStemWordMethod() {
    Word input = new Word("fishing");
    Stemmer stemmer = new Stemmer();
    Word output = stemmer.stem(input);
    assertEquals(new Word("fish"), output);
  }
@Test
  public void testToStringAfterStemming() {
    Stemmer stemmer = new Stemmer();
    stemmer.stem("singing");
    String result = stemmer.toString();
    assertEquals("sing", result);
  } 
}