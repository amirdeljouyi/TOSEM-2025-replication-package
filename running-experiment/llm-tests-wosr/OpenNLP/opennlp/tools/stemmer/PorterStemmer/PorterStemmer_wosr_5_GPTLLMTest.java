public class PorterStemmer_wosr_5_GPTLLMTest { 

 @Test
  public void testStemPluralNouns() {
    PorterStemmer stemmer = new PorterStemmer();
    String result = stemmer.stem("caresses");
    assertEquals("caress", result);

    result = new PorterStemmer().stem("cats");
    assertEquals("cat", result);

    result = new PorterStemmer().stem("ponies");
    assertEquals("poni", result);

    result = new PorterStemmer().stem("ties");
    assertEquals("ti", result);
  }
@Test
  public void testStemEdAndIngEndings() {
    PorterStemmer stemmer = new PorterStemmer();
    String result = stemmer.stem("mating");
    assertEquals("mate", result);

    result = new PorterStemmer().stem("meeting");
    assertEquals("meet", result);

    result = new PorterStemmer().stem("milling");
    assertEquals("mill", result);

    result = new PorterStemmer().stem("agreed");
    assertEquals("agree", result);

    result = new PorterStemmer().stem("disabled");
    assertEquals("disable", result);
  }
@Test
  public void testStemReturnOriginalIfUnchanged() {
    PorterStemmer stemmer = new PorterStemmer();
    String result = stemmer.stem("feed");
    assertEquals("feed", result);

    result = stemmer.stem("caress");
    assertEquals("caress", result);
  }
@Test
  public void testStemMeetingsBecomesMeet() {
    PorterStemmer stemmer = new PorterStemmer();
    String result = stemmer.stem("meetings");
    assertEquals("meet", result);
  }
@Test
  public void testStemYChangesToI() {
    PorterStemmer stemmer = new PorterStemmer();
    String result = stemmer.stem("happy");
    assertEquals("happi", result);
  }
@Test
  public void testStemStep3Mappings() {
    PorterStemmer stemmer1 = new PorterStemmer();
    String result1 = stemmer1.stem("relational");
    assertEquals("relate", result1);

    PorterStemmer stemmer2 = new PorterStemmer();
    String result2 = stemmer2.stem("conditional");
    assertEquals("condition", result2);

    PorterStemmer stemmer3 = new PorterStemmer();
    String result3 = stemmer3.stem("rational");
    assertEquals("ration", result3);
  }
@Test
  public void testStemStep3MoreMappings() {
    PorterStemmer stemmer1 = new PorterStemmer();
    String result1 = stemmer1.stem("valenci");
    assertEquals("valence", result1);

    PorterStemmer stemmer2 = new PorterStemmer();
    String result2 = stemmer2.stem("hesitanci");
    assertEquals("hesitance", result2);

    PorterStemmer stemmer3 = new PorterStemmer();
    String result3 = stemmer3.stem("digitizer");
    assertEquals("digitize", result3);
  }
@Test
  public void testStemStep4HandlingSuffixes() {
    PorterStemmer stemmer1 = new PorterStemmer();
    String result1 = stemmer1.stem("formalize");
    assertEquals("formal", result1);

    PorterStemmer stemmer2 = new PorterStemmer();
    String result2 = stemmer2.stem("electricicate");
    assertEquals("electric", result2);

    PorterStemmer stemmer3 = new PorterStemmer();
    String result3 = stemmer3.stem("hopefulness");
    assertEquals("hope", result3);
  }
@Test
  public void testStemStep5RemovalBasedOnMeasure() {
    PorterStemmer stemmer1 = new PorterStemmer();
    String result1 = stemmer1.stem("adjustment");
    assertEquals("adjust", result1);

    PorterStemmer stemmer2 = new PorterStemmer();
    String result2 = stemmer2.stem("replacement");
    assertEquals("replac", result2);
  }
@Test
  public void testStemStep6FinalEAndDoubleLHandling() {
    PorterStemmer stemmer1 = new PorterStemmer();
    String result1 = stemmer1.stem("rate");
    assertEquals("rate", result1);

    PorterStemmer stemmer2 = new PorterStemmer();
    String result2 = stemmer2.stem("controll");
    assertEquals("control", result2);
  }
@Test
  public void testStemViaCharArrayWithOffset() {
    PorterStemmer stemmer = new PorterStemmer();
    char[] chars = "abcdefgh meetings xyz".toCharArray();
    boolean changed = stemmer.stem(chars, 9, 8);  
    assertTrue(changed);
    assertEquals("meet", stemmer.toString());
  }
@Test
  public void testStemWithAddCharThenStemMethod() {
    PorterStemmer stemmer = new PorterStemmer();
    stemmer.add('r');
    stemmer.add('e');
    stemmer.add('l');
    stemmer.add('a');
    stemmer.add('t');
    stemmer.add('i');
    stemmer.add('o');
    stemmer.add('n');
    stemmer.add('a');
    stemmer.add('l');
    boolean changed = stemmer.stem();
    assertTrue(changed);
    assertEquals("relate", stemmer.toString());
  }
@Test
  public void testStemSingleCharacterWord() {
    PorterStemmer stemmer = new PorterStemmer();
    String result = stemmer.stem("a");
    assertEquals("a", result);
  }
@Test
  public void testStemEmptyString() {
    PorterStemmer stemmer = new PorterStemmer();
    String result = stemmer.stem("");
    assertEquals("", result);
  }
@Test
  public void testStemNonAlphaCharacters() {
    PorterStemmer stemmer = new PorterStemmer();
    String result = stemmer.stem("123abc");
    assertEquals("123abc", result);
  }
@Test
  public void testStemWithCharSequence() {
    PorterStemmer stemmer = new PorterStemmer();
    CharSequence result = stemmer.stem((CharSequence)"universality");
    assertEquals("univers", result);
  }
@Test
  public void testMultipleConsecutiveStemsWithReset() {
    PorterStemmer stemmer = new PorterStemmer();
    stemmer.add('d');
    stemmer.add('e');
    stemmer.add('c');
    stemmer.add('i');
    stemmer.add('s');
    stemmer.add('i');
    stemmer.add('v');
    stemmer.add('e');
    stemmer.stem();
    assertEquals("decis", stemmer.toString());

    stemmer.reset();
    stemmer.add('r');
    stemmer.add('a');
    stemmer.add('t');
    stemmer.add('i');
    stemmer.add('o');
    stemmer.add('n');
    stemmer.add('a');
    stemmer.add('l');
    stemmer.stem();
    assertEquals("ration", stemmer.toString());
  }
@Test
  public void testStemUnchangedWordReturnsFalse() {
    PorterStemmer stemmer = new PorterStemmer();
    boolean changed = stemmer.stem("dog".toCharArray(), 3);
    assertFalse(changed);
    assertEquals("dog", stemmer.toString());
  }
@Test
  public void testInternalResultBufferAndLength() {
    PorterStemmer stemmer = new PorterStemmer();
    stemmer.add('s');
    stemmer.add('t');
    stemmer.add('a');
    stemmer.add('t');
    stemmer.add('e');
    stemmer.stem();

    char[] buffer = stemmer.getResultBuffer();
    int length = stemmer.getResultLength();
    assertEquals("state", new String(buffer, 0, length));
  }
@Test
  public void testStemLongerThanInternalBuffer() {
    PorterStemmer stemmer = new PorterStemmer();
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 100; i++) {
      sb.append('a');
    }
    boolean changed = stemmer.stem(sb.toString().toCharArray(), sb.length());
    assertFalse(changed);
    assertEquals(sb.toString(), stemmer.toString());
  } 
}