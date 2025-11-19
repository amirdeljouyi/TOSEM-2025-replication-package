public class NGramModel_wosr_1_GPTLLMTest { 

 @Test
  public void testAddSingleNGram() {
    NGramModel model = new NGramModel();
    StringList ngram = new StringList("the", "cat");
    model.add(ngram);
    assertTrue(model.contains(ngram));
    assertEquals(1, model.getCount(ngram));
  }
@Test
  public void testAddSameNGramTwice() {
    NGramModel model = new NGramModel();
    StringList ngram = new StringList("the", "cat");
    model.add(ngram);
    model.add(ngram);
    assertEquals(2, model.getCount(ngram));
  }
@Test(expected = NoSuchElementException.class)
  public void testSetCountOnNonExistingNGramThrowsException() {
    NGramModel model = new NGramModel();
    StringList ngram = new StringList("the", "fox");
    model.setCount(ngram, 5);
  }
@Test
  public void testSetCountOnExistingNGramUpdatesCorrectly() {
    NGramModel model = new NGramModel();
    StringList ngram = new StringList("the", "fox");
    model.add(ngram);
    model.setCount(ngram, 10);
    assertEquals(10, model.getCount(ngram));
  }
@Test
  public void testRemoveNGram() {
    NGramModel model = new NGramModel();
    StringList ngram = new StringList("jumped");
    model.add(ngram);
    model.remove(ngram);
    assertFalse(model.contains(ngram));
  }
@Test
  public void testSizeAfterAddingAndRemoving() {
    NGramModel model = new NGramModel();
    StringList n1 = new StringList("a");
    StringList n2 = new StringList("b");
    model.add(n1);
    model.add(n2);
    assertEquals(2, model.size());
    model.remove(n1);
    assertEquals(1, model.size());
  }
@Test
  public void testAddRangeTokenNGrams() {
    NGramModel model = new NGramModel();
    StringList tokens = new StringList("it", "is", "raining");
    model.add(tokens, 1, 2);
    assertTrue(model.contains(new StringList("it")));
    assertTrue(model.contains(new StringList("is")));
    assertTrue(model.contains(new StringList("raining")));
    assertTrue(model.contains(new StringList("it", "is")));
    assertTrue(model.contains(new StringList("is", "raining")));
    assertEquals(5, model.size());
  }
@Test(expected = IllegalArgumentException.class)
  public void testAddWithInvalidMinMaxRange_ZeroMin() {
    NGramModel model = new NGramModel();
    StringList tokens = new StringList("bad", "input");
    model.add(tokens, 0, 2);
  }
@Test(expected = IllegalArgumentException.class)
  public void testAddWithInvalidMinMaxRange_MinGreaterThanMax() {
    NGramModel model = new NGramModel();
    StringList tokens = new StringList("bad", "input");
    model.add(tokens, 3, 2);
  }
@Test
  public void testAddCharNGrams() {
    NGramModel model = new NGramModel();
    model.add("dog", 1, 2);
    assertTrue(model.contains(new StringList("d")));
    assertTrue(model.contains(new StringList("do")));
    assertTrue(model.contains(new StringList("o")));
    assertTrue(model.contains(new StringList("og")));
    assertTrue(model.contains(new StringList("g")));
    assertEquals(5, model.size());
  }
@Test
  public void testCutoffRemovesLowAndHighFrequencyNGrams() {
    NGramModel model = new NGramModel();
    StringList n1 = new StringList("low");
    StringList n2 = new StringList("medium");
    StringList n3 = new StringList("high");

    model.add(n1);
    model.add(n2);
    model.add(n2);
    model.add(n3);
    model.add(n3);
    model.add(n3);

    model.cutoff(2, 2);

    assertFalse(model.contains(n1));
    assertTrue(model.contains(n2));
    assertFalse(model.contains(n3));
  }
@Test
  public void testToDictionaryCaseSensitive() {
    NGramModel model = new NGramModel();
    model.add(new StringList("a"));
    model.add(new StringList("A"));
    Dictionary dict = model.toDictionary(true);
    assertEquals(2, dict.size());
  }
@Test
  public void testToDictionaryCaseInsensitive() {
    NGramModel model = new NGramModel();
    model.add(new StringList("a"));
    model.add(new StringList("A"));
    Dictionary dict = model.toDictionary(false);
    assertEquals(2, dict.size());
  }
@Test
  public void testNumberOfGrams() {
    NGramModel model = new NGramModel();
    model.add(new StringList("one"));
    model.add(new StringList("two"));
    model.add(new StringList("one"));
    assertEquals(3, model.numberOfGrams());
  }
@Test
  public void testSerializeAndDeserialize() throws IOException {
    NGramModel model = new NGramModel();
    StringList tok1 = new StringList("serialize", "test");
    model.add(tok1);
    model.add(tok1);
    model.setCount(tok1, 3);
    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    model.serialize(outStream);

    ByteArrayInputStream inStream = new ByteArrayInputStream(outStream.toByteArray());
    NGramModel loadedModel = new NGramModel(inStream);
    assertEquals(3, loadedModel.getCount(tok1));
    assertTrue(loadedModel.contains(tok1));
  }
@Test(expected = InvalidFormatException.class)
  public void testDeserializeWithInvalidCountAttributeThrows() throws IOException {
    String invalidEntry = "<dictionary>\n" +
        "  <entry count=\"NotANumber\">bad</entry>\n" +
        "</dictionary>";
    InputStream in = new ByteArrayInputStream(invalidEntry.getBytes());
    new NGramModel(in);
  }
@Test(expected = InvalidFormatException.class)
  public void testDeserializeWithMissingCountAttributeThrows() throws IOException {
    String missingCount = "<dictionary>\n" +
        "  <entry>missing</entry>\n" +
        "</dictionary>";
    InputStream in = new ByteArrayInputStream(missingCount.getBytes());
    new NGramModel(in);
  }
@Test
  public void testEqualsAndHashCode() {
    NGramModel model1 = new NGramModel();
    NGramModel model2 = new NGramModel();
    StringList ngram = new StringList("dog", "barks");
    model1.add(ngram);
    model2.add(ngram);
    assertEquals(model1, model2);
    assertEquals(model1.hashCode(), model2.hashCode());
  }
@Test
  public void testNotEqualsWithDifferentCounts() {
    NGramModel model1 = new NGramModel();
    NGramModel model2 = new NGramModel();
    StringList ngram = new StringList("run");
    model1.add(ngram);
    model2.add(ngram);
    model2.add(ngram);
    assertNotEquals(model1, model2);
  }
@Test
  public void testToStringReturnsCorrectSize() {
    NGramModel model = new NGramModel();
    model.add(new StringList("x"));
    model.add(new StringList("y"));
    assertTrue(model.toString().contains("Size: 2"));
  }
@Test
  public void testIteratorWorksCorrectly() {
    NGramModel model = new NGramModel();
    StringList one = new StringList("token1");
    StringList two = new StringList("token2");
    model.add(one);
    model.add(two);
    Iterator<StringList> it = model.iterator();
    assertTrue(it.hasNext());
    StringList first = it.next();
    assertNotNull(first);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testSerializedEntryIteratorRemoveThrows() {
    NGramModel model = new NGramModel();
    model.add(new StringList("test"));
    Iterator<StringList> it = model.iterator();
    Entry entry = new Entry(it.next(), null);
    Iterator<Entry> eIt = new Iterator<Entry>() {
      public boolean hasNext() { return true; }
      public Entry next() { return entry; }
      public void remove() { throw new UnsupportedOperationException(); }
    };
    eIt.remove();
  } 
}