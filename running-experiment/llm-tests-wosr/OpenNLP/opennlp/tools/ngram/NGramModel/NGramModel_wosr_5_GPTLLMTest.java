public class NGramModel_wosr_5_GPTLLMTest { 

 @Test
  public void testAddSingleNGram() {
    NGramModel model = new NGramModel();
    StringList ngram = new StringList("a", "b");
    model.add(ngram);
    assertTrue(model.contains(ngram));
    assertEquals(1, model.getCount(ngram));
  }
@Test
  public void testAddSameNGramTwiceIncrementsCount() {
    NGramModel model = new NGramModel();
    StringList ngram = new StringList("x", "y");
    model.add(ngram);
    model.add(ngram);
    assertEquals(2, model.getCount(ngram));
  }
@Test
  public void testSetCountUpdatesValue() {
    NGramModel model = new NGramModel();
    StringList ngram = new StringList("a", "b", "c");
    model.add(ngram);
    model.setCount(ngram, 5);
    assertEquals(5, model.getCount(ngram));
  }
@Test(expected = NoSuchElementException.class)
  public void testSetCountOnMissingNGramThrows() {
    NGramModel model = new NGramModel();
    StringList ngram = new StringList("does", "not", "exist");
    model.setCount(ngram, 3);
  }
@Test
  public void testGetCountReturnsZeroForMissingGram() {
    NGramModel model = new NGramModel();
    StringList ngram = new StringList("not", "present");
    assertEquals(0, model.getCount(ngram));
  }
@Test
  public void testAddRangeOfNGrams() {
    NGramModel model = new NGramModel();
    StringList input = new StringList("i", "love", "nlp");
    model.add(input, 1, 2);
    assertEquals(4, model.size()); 
  }
@Test(expected = IllegalArgumentException.class)
  public void testAddRangeInvalidMinMaxLength() {
    NGramModel model = new NGramModel();
    StringList input = new StringList("a", "b", "c");
    model.add(input, 0, 3);
  }
@Test(expected = IllegalArgumentException.class)
  public void testAddRangeMinGreaterThanMaxThrows() {
    NGramModel model = new NGramModel();
    StringList input = new StringList("a", "b", "c");
    model.add(input, 4, 3);
  }
@Test
  public void testAddCharNGrams() {
    NGramModel model = new NGramModel();
    model.add("hello", 2, 3);
    assertTrue(model.contains(new StringList("he")));
    assertTrue(model.contains(new StringList("ell")));
    assertTrue(model.contains(new StringList("hel")));
    assertTrue(model.contains(new StringList("ell")));
    assertTrue(model.contains(new StringList("llo")));
    assertEquals(7, model.size());
  }
@Test
  public void testRemoveNGram() {
    NGramModel model = new NGramModel();
    StringList ngram = new StringList("a", "b");
    model.add(ngram);
    assertTrue(model.contains(ngram));
    model.remove(ngram);
    assertFalse(model.contains(ngram));
  }
@Test
  public void testSizeAfterAdd() {
    NGramModel model = new NGramModel();
    model.add(new StringList("x"));
    model.add(new StringList("y"));
    assertEquals(2, model.size());
  }
@Test
  public void testSizeAfterDuplicateAdds() {
    NGramModel model = new NGramModel();
    model.add(new StringList("x", "y"));
    model.add(new StringList("x", "y"));
    assertEquals(1, model.size());
  }
@Test
  public void testNumberOfGramsAggregateCount() {
    NGramModel model = new NGramModel();
    model.add(new StringList("a"));
    model.add(new StringList("b"));
    model.add(new StringList("b"));
    assertEquals(3, model.numberOfGrams());
  }
@Test
  public void testCutoffRemovesLowAndHighFrequency() {
    NGramModel model = new NGramModel();
    model.add(new StringList("one"));
    model.add(new StringList("two"));
    model.add(new StringList("two"));
    model.add(new StringList("three"));
    for (int i = 0; i < 10; i++) {
      model.add(new StringList("high"));
    }
    model.cutoff(2, 5);
    assertFalse(model.contains(new StringList("one")));
    assertTrue(model.contains(new StringList("two")));
    assertFalse(model.contains(new StringList("three")));
    assertFalse(model.contains(new StringList("high")));
  }
@Test
  public void testToDictionaryCaseInsensitive() {
    NGramModel model = new NGramModel();
    model.add(new StringList("HeLLo"));
    Dictionary dict = model.toDictionary(false);
    assertTrue(dict.contains(new StringList("hello")));
  }
@Test
  public void testToDictionaryCaseSensitive() {
    NGramModel model = new NGramModel();
    model.add(new StringList("HeLLo"));
    Dictionary dict = model.toDictionary(true);
    assertTrue(dict.contains(new StringList("HeLLo")));
    assertFalse(dict.contains(new StringList("hello")));
  }
@Test
  public void testToStringReturnsSize() {
    NGramModel model = new NGramModel();
    model.add(new StringList("a"));
    String str = model.toString();
    assertTrue(str.contains("Size: 1"));
  }
@Test
  public void testEqualsAndHashCode() {
    NGramModel model1 = new NGramModel();
    NGramModel model2 = new NGramModel();
    StringList ngram = new StringList("equal");
    model1.add(ngram);
    model2.add(ngram);
    assertEquals(model1, model2);
    assertEquals(model1.hashCode(), model2.hashCode());
  }
@Test
  public void testNotEqualsDifferentCounts() {
    NGramModel model1 = new NGramModel();
    NGramModel model2 = new NGramModel();
    StringList ngram = new StringList("diff");
    model1.add(ngram);
    model2.add(ngram);
    model2.add(ngram);
    assertNotEquals(model1, model2);
  }
@Test
  public void testSerializeDeserialize() throws IOException {
    NGramModel model = new NGramModel();
    model.add(new StringList("a", "b"));
    model.add(new StringList("c"));

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    model.serialize(out);
    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
    NGramModel deserialized = new NGramModel(in);

    assertEquals(model, deserialized);
  }
@Test(expected = InvalidFormatException.class)
  public void testInvalidEntryMissingCountThrows() throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    DictionaryEntryPersistor.serialize(out, new Iterator<>() {
      private boolean hasEmitted = false;

      @Override
      public boolean hasNext() {
        return !hasEmitted;
      }

      @Override
      public opennlp.tools.dictionary.serializer.Entry next() {
        hasEmitted = true;
        return new opennlp.tools.dictionary.serializer.Entry(new StringList("oops"), new opennlp.tools.dictionary.serializer.Attributes());
      }
    }, false);

    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
    new NGramModel(in);
  }
@Test(expected = InvalidFormatException.class)
  public void testInvalidEntryNonIntegerCountThrows() throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    DictionaryEntryPersistor.serialize(out, new Iterator<>() {
      private boolean hasEmitted = false;

      @Override
      public boolean hasNext() {
        return !hasEmitted;
      }

      @Override
      public opennlp.tools.dictionary.serializer.Entry next() {
        hasEmitted = true;
        opennlp.tools.dictionary.serializer.Attributes attr = new opennlp.tools.dictionary.serializer.Attributes();
        attr.setValue("count", "NaN");
        return new opennlp.tools.dictionary.serializer.Entry(new StringList("a"), attr);
      }
    }, false);

    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
    new NGramModel(in);
  }
@Test(expected = UnsupportedOperationException.class)
  public void testSerializeIteratorRemoveThrows() throws IOException {
    NGramModel model = new NGramModel();
    model.add(new StringList("test"));

    Iterator<opennlp.tools.dictionary.serializer.Entry> it = new Iterator<>() {
      private final Iterator<StringList> mDictionaryIterator = model.iterator();

      @Override
      public boolean hasNext() {
        return mDictionaryIterator.hasNext();
      }

      @Override
      public opennlp.tools.dictionary.serializer.Entry next() {
        StringList tokens = mDictionaryIterator.next();
        opennlp.tools.dictionary.serializer.Attributes attributes = new opennlp.tools.dictionary.serializer.Attributes();
        attributes.setValue("count", Integer.toString(model.getCount(tokens)));
        return new opennlp.tools.dictionary.serializer.Entry(tokens, attributes);
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };

    it.remove();
  } 
}