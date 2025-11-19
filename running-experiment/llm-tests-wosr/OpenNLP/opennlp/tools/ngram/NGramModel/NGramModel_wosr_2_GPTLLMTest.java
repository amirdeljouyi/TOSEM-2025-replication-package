public class NGramModel_wosr_2_GPTLLMTest { 

 @Test
  public void testAddSingleNGram() {
    NGramModel model = new NGramModel();
    StringList ngram = new StringList("hello");

    model.add(ngram);

    assertEquals(1, model.getCount(ngram));
    assertTrue(model.contains(ngram));
    assertEquals(1, model.size());
    assertEquals(1, model.numberOfGrams());
  }
@Test
  public void testAddDuplicateNGramIncrementsCount() {
    NGramModel model = new NGramModel();
    StringList ngram = new StringList("hello");

    model.add(ngram);
    model.add(ngram);

    assertEquals(2, model.getCount(ngram));
    assertEquals(1, model.size());
    assertEquals(2, model.numberOfGrams());
  }
@Test(expected = NoSuchElementException.class)
  public void testSetCountForNonExistentNGramThrowsException() {
    NGramModel model = new NGramModel();
    StringList ngram = new StringList("notexists");

    model.setCount(ngram, 2);
  }
@Test
  public void testSetCountForExistingNGram() {
    NGramModel model = new NGramModel();
    StringList ngram = new StringList("update");

    model.add(ngram);
    model.setCount(ngram, 5);

    assertEquals(5, model.getCount(ngram));
  }
@Test
  public void testAddNGramWithMinMaxLength() {
    NGramModel model = new NGramModel();
    StringList tokens = new StringList("a", "b", "c");

    model.add(tokens, 1, 2);

    assertEquals(5, model.numberOfGrams()); 
    assertEquals(5, model.size());
  }
@Test(expected = IllegalArgumentException.class)
  public void testAddNGramWithInvalidMinZero() {
    NGramModel model = new NGramModel();
    StringList tokens = new StringList("a", "b");

    model.add(tokens, 0, 2);
  }
@Test(expected = IllegalArgumentException.class)
  public void testAddNGramWithMinGreaterThanMax() {
    NGramModel model = new NGramModel();
    StringList tokens = new StringList("a", "b");

    model.add(tokens, 3, 2);
  }
@Test
  public void testAddCharSequenceNGram() {
    NGramModel model = new NGramModel();
    String chars = "abc";

    model.add(chars, 1, 2);

    assertEquals(5, model.numberOfGrams());
    assertTrue(model.contains(new StringList("a")));
    assertTrue(model.contains(new StringList("ab")));
    assertTrue(model.contains(new StringList("b")));
    assertTrue(model.contains(new StringList("bc")));
    assertTrue(model.contains(new StringList("c")));
  }
@Test
  public void testRemoveExistingNGram() {
    NGramModel model = new NGramModel();
    StringList ngram = new StringList("remove-me");

    model.add(ngram);
    model.remove(ngram);

    assertFalse(model.contains(ngram));
    assertEquals(0, model.size());
  }
@Test
  public void testRemoveNonExistingNGramDoesNothing() {
    NGramModel model = new NGramModel();
    StringList ngram = new StringList("nothing");

    model.remove(ngram);

    assertEquals(0, model.size());
  }
@Test
  public void testCutoffUnderLimit() {
    NGramModel model = new NGramModel();
    StringList low = new StringList("low");
    StringList high = new StringList("high");

    model.add(low);
    model.add(high);
    model.add(high);
    model.cutoff(2, Integer.MAX_VALUE);

    assertFalse(model.contains(low));
    assertTrue(model.contains(high));
  }
@Test
  public void testCutoffOverLimit() {
    NGramModel model = new NGramModel();
    StringList n1 = new StringList("one");
    model.add(n1);
    model.add(n1);
    model.add(n1);

    model.cutoff(0, 2); 

    assertFalse(model.contains(n1));
  }
@Test
  public void testToDictionaryCaseInsensitiveMerge() {
    NGramModel model = new NGramModel();
    model.add(new StringList("CASE"));
    model.add(new StringList("case"));

    Dictionary dict = model.toDictionary();
    assertEquals(1, dict.size());
  }
@Test
  public void testToDictionaryCaseSensitive() {
    NGramModel model = new NGramModel();
    model.add(new StringList("Hi"));
    model.add(new StringList("hi"));

    Dictionary dict = model.toDictionary(true);
    assertEquals(2, dict.size());
  }
@Test
  public void testSerializeThenDeserialize() throws IOException {
    NGramModel sourceModel = new NGramModel();
    sourceModel.add(new StringList("one"));
    sourceModel.add(new StringList("two"));
    sourceModel.add(new StringList("two"));

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    sourceModel.serialize(out);

    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
    NGramModel deserializedModel = new NGramModel(in);

    assertEquals(sourceModel.size(), deserializedModel.size());
    assertEquals(sourceModel.numberOfGrams(), deserializedModel.numberOfGrams());
    assertEquals(sourceModel.getCount(new StringList("two")), deserializedModel.getCount(new StringList("two")));
  }
@Test(expected = InvalidFormatException.class)
  public void testDeserializeInvalidMissingCountThrows() throws IOException {
    String entryData = "one\n"; 
    ByteArrayInputStream in = new ByteArrayInputStream(entryData.getBytes(StandardCharsets.UTF_8));
    new NGramModel(in);
  }
@Test(expected = InvalidFormatException.class)
  public void testDeserializeInvalidNonNumberCountThrows() throws IOException {
    String entry = "<entry><w>abc</w><a c=\"notnumber\"/></entry>";
    ByteArrayInputStream in = new ByteArrayInputStream(entry.getBytes(StandardCharsets.UTF_8));
    new NGramModel(in);
  }
@Test
  public void testEqualsAndHashCode() {
    NGramModel model1 = new NGramModel();
    NGramModel model2 = new NGramModel();

    StringList list = new StringList("equal");

    model1.add(list);
    model2.add(list);

    assertEquals(model1, model2);
    assertEquals(model1.hashCode(), model2.hashCode());
  }
@Test
  public void testToString() {
    NGramModel model = new NGramModel();
    model.add(new StringList("x"));

    String result = model.toString();
    assertTrue(result.contains("Size: 1"));
  }
@Test
  public void testIteratorOrderAndCount() {
    NGramModel model = new NGramModel();
    StringList a = new StringList("a");
    StringList b = new StringList("b");

    model.add(a);
    model.add(b);

    int count = 0;
    int sum = 0;
    for (StringList sl : model) {
      assertTrue(model.contains(sl));
      sum += model.getCount(sl);
      count++;
    }

    assertEquals(2, count);
    assertEquals(2, sum);
  } 
}