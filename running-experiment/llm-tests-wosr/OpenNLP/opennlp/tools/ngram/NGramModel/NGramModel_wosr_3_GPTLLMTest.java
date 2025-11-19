public class NGramModel_wosr_3_GPTLLMTest { 

 @Test
  public void testAddAndContainBasicNGram() {
    NGramModel model = new NGramModel();
    StringList ngram = new StringList("a", "b");
    model.add(ngram);
    assertTrue(model.contains(ngram));
    assertEquals(1, model.getCount(ngram));
  }
@Test
  public void testAddIncrementsCountIfAlreadyExists() {
    NGramModel model = new NGramModel();
    StringList ngram = new StringList("x", "y");
    model.add(ngram);
    model.add(ngram);
    assertEquals(2, model.getCount(ngram));
  }
@Test(expected = NoSuchElementException.class)
  public void testSetCountThrowsForNonExistentNGram() {
    NGramModel model = new NGramModel();
    StringList ngram = new StringList("missing");
    model.setCount(ngram, 5);
  }
@Test
  public void testGetCountReturnsZeroForNonExistentNGram() {
    NGramModel model = new NGramModel();
    StringList ngram = new StringList("not", "exists");
    assertEquals(0, model.getCount(ngram));
  }
@Test
  public void testRemoveNGram() {
    NGramModel model = new NGramModel();
    StringList ngram = new StringList("remove", "me");
    model.add(ngram);
    model.remove(ngram);
    assertFalse(model.contains(ngram));
  }
@Test
  public void testAddWithRange() {
    NGramModel model = new NGramModel();
    StringList list = new StringList("one", "two", "three");
    model.add(list, 1, 2);

    assertTrue(model.contains(new StringList("one")));
    assertTrue(model.contains(new StringList("two")));
    assertTrue(model.contains(new StringList("three")));
    assertTrue(model.contains(new StringList("one", "two")));
    assertTrue(model.contains(new StringList("two", "three")));

    assertFalse(model.contains(new StringList("one", "two", "three")));
  }
@Test(expected = IllegalArgumentException.class)
  public void testAddRangeThrowsMinLessThan1() {
    NGramModel model = new NGramModel();
    StringList tokens = new StringList("a", "b", "c");
    model.add(tokens, 0, 2);
  }
@Test(expected = IllegalArgumentException.class)
  public void testAddRangeThrowsMinGreaterThanMax() {
    NGramModel model = new NGramModel();
    StringList tokens = new StringList("a", "b", "c");
    model.add(tokens, 3, 2);
  }
@Test
  public void testAddCharSequence() {
    NGramModel model = new NGramModel();
    model.add("ab", 1, 2);

    assertEquals(1, model.getCount(new StringList("a")));
    assertEquals(1, model.getCount(new StringList("b")));
    assertEquals(1, model.getCount(new StringList("ab")));
  }
@Test
  public void testCutoffRemovesUnderCutoff() {
    NGramModel model = new NGramModel();
    model.add(new StringList("rare"));
    model.add(new StringList("frequent"));
    model.add(new StringList("frequent"));
    model.cutoff(2, Integer.MAX_VALUE);

    assertFalse(model.contains(new StringList("rare")));
    assertTrue(model.contains(new StringList("frequent")));
  }
@Test
  public void testCutoffRemovesOverThreshold() {
    NGramModel model = new NGramModel();
    model.add(new StringList("common"));
    model.add(new StringList("common"));
    model.add(new StringList("common"));
    model.cutoff(0, 2);

    assertFalse(model.contains(new StringList("common")));
  }
@Test
  public void testSizeAndNumberOfGrams() {
    NGramModel model = new NGramModel();
    model.add(new StringList("x"));
    model.add(new StringList("y"));
    model.add(new StringList("x"));

    assertEquals(2, model.size());
    assertEquals(3, model.numberOfGrams());
  }
@Test
  public void testToDictionaryCaseInsensitive() {
    NGramModel model = new NGramModel();
    model.add(new StringList("Hello"));
    model.add(new StringList("hello"));

    Dictionary dict = model.toDictionary();
    assertEquals(2, dict.size());
  }
@Test
  public void testToDictionaryCaseSensitive() {
    NGramModel model = new NGramModel();
    model.add(new StringList("Term"));
    model.add(new StringList("term"));

    Dictionary dict = model.toDictionary(true);
    assertTrue(dict.contains(new StringList("Term")));
    assertTrue(dict.contains(new StringList("term")));
  }
@Test
  public void testEqualsAndHashCode() {
    NGramModel modelA = new NGramModel();
    NGramModel modelB = new NGramModel();

    modelA.add(new StringList("equal"));
    modelB.add(new StringList("equal"));

    assertEquals(modelA, modelB);
    assertEquals(modelA.hashCode(), modelB.hashCode());
  }
@Test
  public void testToStringOutputsSize() {
    NGramModel model = new NGramModel();
    model.add(new StringList("a"));
    String output = model.toString();
    assertTrue(output.contains("Size: 1"));
  }
@Test
  public void testSerializeAndDeserializeNGramModel() throws IOException {
    NGramModel original = new NGramModel();
    original.add(new StringList("persist", "me"));
    original.add(new StringList("persist", "me"));

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    original.serialize(out);
    byte[] data = out.toByteArray();
    ByteArrayInputStream in = new ByteArrayInputStream(data);

    NGramModel loaded = new NGramModel(in);

    assertTrue(loaded.contains(new StringList("persist", "me")));
    assertEquals(2, loaded.getCount(new StringList("persist", "me")));
  }
@Test(expected = NoSuchElementException.class)
  public void testInvalidSetCountAfterAddRemoveCycle() {
    NGramModel model = new NGramModel();
    StringList ngram = new StringList("cycle");
    model.add(ngram);
    model.remove(ngram);
    model.setCount(ngram, 5);
  }
@Test
  public void testMultipleAdditionsWithCharNGrams() {
    NGramModel model = new NGramModel();
    model.add("banana", 2, 2);

    assertEquals(1, model.getCount(new StringList("ba")));
    assertEquals(1, model.getCount(new StringList("an")));
    assertEquals(1, model.getCount(new StringList("na")));
    assertEquals(1, model.getCount(new StringList("na"))); 
    assertEquals(5, model.numberOfGrams());
  } 
}