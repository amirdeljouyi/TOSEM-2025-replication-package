public class NGramModel_wosr_4_GPTLLMTest { 

 @Test
  public void testAddAndContainsSingleNGram() {
    NGramModel model = new NGramModel();
    StringList ngram = new StringList("the", "cat");

    model.add(ngram);

    assertTrue(model.contains(ngram));
    assertEquals(1, model.getCount(ngram));
  }
@Test
  public void testAddIncrementsCount() {
    NGramModel model = new NGramModel();
    StringList ngram = new StringList("jumped");

    model.add(ngram);
    model.add(ngram);
    model.add(ngram);

    assertEquals(3, model.getCount(ngram));
  }
@Test
  public void testSetCountUpdatesValue() {
    NGramModel model = new NGramModel();
    StringList ngram = new StringList("fox");

    model.add(ngram);
    model.setCount(ngram, 8);

    assertEquals(8, model.getCount(ngram));
  }
@Test(expected = NoSuchElementException.class)
  public void testSetCountFailsIfNotPresent() {
    NGramModel model = new NGramModel();
    StringList ngram = new StringList("dog");

    model.setCount(ngram, 10);
  }
@Test
  public void testRemoveNGram() {
    NGramModel model = new NGramModel();
    StringList ngram = new StringList("house");

    model.add(ngram);
    assertTrue(model.contains(ngram));
    model.remove(ngram);
    assertFalse(model.contains(ngram));
    assertEquals(0, model.getCount(ngram));
  }
@Test
  public void testAddNGramInRange() {
    NGramModel model = new NGramModel();
    StringList text = new StringList("a", "b", "c");

    model.add(text, 1, 2);

    assertEquals(5, model.numberOfGrams());
    assertTrue(model.contains(new StringList("a")));
    assertTrue(model.contains(new StringList("b")));
    assertTrue(model.contains(new StringList("c")));
    assertTrue(model.contains(new StringList("a", "b")));
    assertTrue(model.contains(new StringList("b", "c")));
  }
@Test(expected = IllegalArgumentException.class)
  public void testAddNGramMinLessThanOne() {
    NGramModel model = new NGramModel();
    StringList text = new StringList("x", "y");

    model.add(text, 0, 2);
  }
@Test(expected = IllegalArgumentException.class)
  public void testAddNGramMinGreaterThanMax() {
    NGramModel model = new NGramModel();
    StringList text = new StringList("a", "b");

    model.add(text, 3, 1);
  }
@Test
  public void testAddCharSequenceAsNGram() {
    NGramModel model = new NGramModel();
    model.add("test", 1, 2);

    assertTrue(model.contains(new StringList("t")));
    assertTrue(model.contains(new StringList("e")));
    assertTrue(model.contains(new StringList("s")));
    assertTrue(model.contains(new StringList("te")));
    assertTrue(model.contains(new StringList("es")));
    assertTrue(model.contains(new StringList("st")));
  }
@Test
  public void testSizeReturnsCorrectValue() {
    NGramModel model = new NGramModel();
    model.add(new StringList("x"));
    model.add(new StringList("y", "z"));

    assertEquals(2, model.size());
  }
@Test
  public void testNumberOfGramsCorrectSum() {
    NGramModel model = new NGramModel();
    StringList first = new StringList("first");
    StringList second = new StringList("second");
    StringList third = new StringList("third");

    model.add(first);
    model.add(second);
    model.add(second);
    model.add(third);
    model.setCount(third, 5);

    assertEquals(1 + 2 + 5, model.numberOfGrams());
  }
@Test
  public void testCutoffUnderThreshold() {
    NGramModel model = new NGramModel();
    model.add(new StringList("low"));
    model.add(new StringList("mid"));
    model.add(new StringList("mid"));
    model.add(new StringList("high"));
    model.add(new StringList("high"));
    model.add(new StringList("high"));

    model.cutoff(2, Integer.MAX_VALUE);

    assertFalse(model.contains(new StringList("low")));
    assertTrue(model.contains(new StringList("mid")));
    assertTrue(model.contains(new StringList("high")));
  }
@Test
  public void testCutoffOverThreshold() {
    NGramModel model = new NGramModel();
    model.add(new StringList("alpha"));
    model.add(new StringList("beta"));
    model.add(new StringList("beta"));
    model.add(new StringList("gamma"));
    model.add(new StringList("gamma"));
    model.add(new StringList("gamma"));

    model.cutoff(0, 2);

    assertTrue(model.contains(new StringList("alpha")));
    assertTrue(model.contains(new StringList("beta")));
    assertFalse(model.contains(new StringList("gamma")));
  }
@Test
  public void testToDictionaryCaseSensitiveFalse() {
    NGramModel model = new NGramModel();
    model.add(new StringList("Word"));

    Dictionary dict = model.toDictionary(false);

    assertTrue(dict.contains(new StringList("word")));
  }
@Test
  public void testToDictionaryCaseSensitiveTrue() {
    NGramModel model = new NGramModel();
    model.add(new StringList("Word"));

    Dictionary dict = model.toDictionary(true);

    assertTrue(dict.contains(new StringList("Word")));
    assertFalse(dict.contains(new StringList("word")));
  }
@Test
  public void testSerializeDeserializeRoundTrip() throws IOException {
    NGramModel model = new NGramModel();
    model.add(new StringList("hello"));
    model.add(new StringList("world"));
    model.add(new StringList("world"));

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    model.serialize(baos);

    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    NGramModel loaded = new NGramModel(bais);

    assertTrue(loaded.contains(new StringList("hello")));
    assertTrue(loaded.contains(new StringList("world")));
    assertEquals(1, loaded.getCount(new StringList("hello")));
    assertEquals(2, loaded.getCount(new StringList("world")));
  }
@Test(expected = UnsupportedOperationException.class)
  public void testSerializeEntryIteratorRemoveThrowsException() throws IOException {
    NGramModel model = new NGramModel();
    model.add(new StringList("x"));

    OutputStream out = new OutputStream() {
      @Override
      public void write(int b) throws IOException {
        
      }
    };

    Iterator<StringList> iterator = model.iterator();
    if (iterator.hasNext()) {
      StringList gram = iterator.next();
      model.serialize(out); 
      Iterator<Entry> entryIterator = new Iterator<Entry>() {
        private final Iterator<StringList> internalIt = model.iterator();
        public boolean hasNext() {
          return internalIt.hasNext();
        }
        public Entry next() {
          StringList tokens = internalIt.next();
          return new Entry(tokens, null); 
        }
        public void remove() {
          throw new UnsupportedOperationException();
        }
      };
      entryIterator.remove(); 
    }
  }
@Test
  public void testEqualsAndHashCode() {
    NGramModel model1 = new NGramModel();
    NGramModel model2 = new NGramModel();

    model1.add(new StringList("data"));
    model2.add(new StringList("data"));

    assertEquals(model1, model2);
    assertEquals(model1.hashCode(), model2.hashCode());
  }
@Test
  public void testNotEqualsForDifferentModels() {
    NGramModel model1 = new NGramModel();
    NGramModel model2 = new NGramModel();

    model1.add(new StringList("x"));
    model2.add(new StringList("y"));

    assertNotEquals(model1, model2);
  }
@Test
  public void testToStringReturnsSizeInfo() {
    NGramModel model = new NGramModel();
    model.add(new StringList("unit", "test"));

    String desc = model.toString();
    assertTrue(desc.contains("Size:"));
  }
@Test(expected = IOException.class)
  public void testInputStreamConstructorWithInvalidFormat() throws IOException {
    byte[] invalidData = "invalid format".getBytes();
    InputStream stream = new ByteArrayInputStream(invalidData);
    new NGramModel(stream);
  }
@Test(expected = NullPointerException.class)
  public void testAddNullNGramThrowsException() {
    NGramModel model = new NGramModel();
    model.add((StringList) null);
  }
@Test
  public void testIteratorReturnsAllEntries() {
    NGramModel model = new NGramModel();
    model.add(new StringList("a"));
    model.add(new StringList("b"));

    Iterator<StringList> it = model.iterator();
    int count = 0;
    if (it.hasNext()) {
      it.next();
      count++;
    }
    if (it.hasNext()) {
      it.next();
      count++;
    }
    assertEquals(2, count);
  } 
}