public class WikidictAnnotator_wosr_4_GPTLLMTest { 

 @Test
  public void testLink_dateTimexShouldNormalize() {
    Properties props = new Properties();
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

    Timex timex = new Timex("2023-07-01T00:00"); 

    CoreMap mention = new CoreMap() {
      private final Map<Class<? extends CoreAnnotation>, Object> map = new HashMap<>();

      {
        map.put(TextAnnotation.class, "July 1st");
        map.put(NamedEntityTagAnnotation.class, "DATE");
        map.put(TimeAnnotations.TimexAnnotation.class, timex);
      }

      @Override public <VALUE> void set(Class<? extends CoreAnnotation<VALUE>> key, VALUE value) { map.put(key, value); }
      @Override public <VALUE> VALUE get(Class<? extends CoreAnnotation<VALUE>> key) { return (VALUE) map.get(key); }
      @Override public Set<Class<? extends CoreAnnotation>> keySet() { return map.keySet(); }
      @Override public CoreMap makeCopy() { return null; }
    };

    Optional<String> result = annotator.link(mention);
    Assert.assertTrue(result.isPresent());
    Assert.assertEquals("2023-07-01", result.get());
  }
@Test
  public void testLink_dateTimexPresentShouldReturnEmpty() {
    Properties props = new Properties();
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

    Timex timex = new Timex("PRESENT");

    CoreMap mention = new CoreMap() {
      private final Map<Class<? extends CoreAnnotation>, Object> map = new HashMap<>();

      {
        map.put(TextAnnotation.class, "Today");
        map.put(NamedEntityTagAnnotation.class, "DATE");
        map.put(TimeAnnotations.TimexAnnotation.class, timex);
      }

      @Override public <VALUE> void set(Class<? extends CoreAnnotation<VALUE>> key, VALUE value) { map.put(key, value); }
      @Override public <VALUE> VALUE get(Class<? extends CoreAnnotation<VALUE>> key) { return (VALUE) map.get(key); }
      @Override public Set<Class<? extends CoreAnnotation>> keySet() { return map.keySet(); }
      @Override public CoreMap makeCopy() { return null; }
    };

    Optional<String> result = annotator.link(mention);
    Assert.assertFalse(result.isPresent());
  }
@Test
  public void testLink_ordinalMentionShouldExtractNumericValue() {
    Properties props = new Properties();
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

    CoreMap mention = new CoreMap() {
      private final Map<Class<? extends CoreAnnotation>, Object> map = new HashMap<>();

      {
        map.put(TextAnnotation.class, "First");
        map.put(NamedEntityTagAnnotation.class, "ORDINAL");
        map.put(NumericValueAnnotation.class, 1);
      }

      @Override public <VALUE> void set(Class<? extends CoreAnnotation<VALUE>> key, VALUE value) { map.put(key, value); }
      @Override public <VALUE> VALUE get(Class<? extends CoreAnnotation<VALUE>> key) { return (VALUE) map.get(key); }
      @Override public Set<Class<? extends CoreAnnotation>> keySet() { return map.keySet(); }
      @Override public CoreMap makeCopy() { return null; }
    };

    Optional<String> result = annotator.link(mention);
    Assert.assertTrue(result.isPresent());
    Assert.assertEquals("1", result.get());
  }
@Test
  public void testLink_numericStringShouldReturnItself() {
    Properties props = new Properties();
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

    CoreMap mention = new CoreMap() {
      private final Map<Class<? extends CoreAnnotation>, Object> map = new HashMap<>();

      {
        map.put(TextAnnotation.class, "42");
      }

      @Override public <VALUE> void set(Class<? extends CoreAnnotation<VALUE>> key, VALUE value) { map.put(key, value); }
      @Override public <VALUE> VALUE get(Class<? extends CoreAnnotation<VALUE>> key) { return (VALUE) map.get(key); }
      @Override public Set<Class<? extends CoreAnnotation>> keySet() { return map.keySet(); }
      @Override public CoreMap makeCopy() { return null; }
    };

    Optional<String> result = annotator.link(mention);
    Assert.assertTrue(result.isPresent());
    Assert.assertEquals("42", result.get());
  }
@Test
  public void testLink_surfaceFormFoundInDictionaryShouldReturnLink() {
    Properties props = new Properties();
    props.setProperty("wikidict", "wikidict.tsv");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

    annotator.dictionary.put("New York", "New_York_City");

    CoreMap mention = new CoreMap() {
      private final Map<Class<? extends CoreAnnotation>, Object> map = new HashMap<>();

      {
        map.put(OriginalTextAnnotation.class, "New York");
        map.put(NamedEntityTagAnnotation.class, "LOCATION");
      }

      @Override public <VALUE> void set(Class<? extends CoreAnnotation<VALUE>> key, VALUE value) { map.put(key, value); }
      @Override public <VALUE> VALUE get(Class<? extends CoreAnnotation<VALUE>> key) { return (VALUE) map.get(key); }
      @Override public Set<Class<? extends CoreAnnotation>> keySet() { return map.keySet(); }
      @Override public CoreMap makeCopy() { return null; }
    };

    Optional<String> result = annotator.link(mention);
    Assert.assertTrue(result.isPresent());
    Assert.assertEquals("New_York_City", result.get());
  }
@Test
  public void testLink_caselessMatchWorksWhenEnabled() {
    Properties props = new Properties();
    props.setProperty("wikidict", "wikidict.tsv");
    props.setProperty("caseless", "true");
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);
    annotator.wikidictCaseless = true;

    annotator.dictionary.put("paris", "Paris_France");

    CoreMap mention = new CoreMap() {
      private final Map<Class<? extends CoreAnnotation>, Object> map = new HashMap<>();

      {
        map.put(OriginalTextAnnotation.class, "Paris");
        map.put(NamedEntityTagAnnotation.class, "LOCATION");
      }

      @Override public <VALUE> void set(Class<? extends CoreAnnotation<VALUE>> key, VALUE value) { map.put(key, value); }
      @Override public <VALUE> VALUE get(Class<? extends CoreAnnotation<VALUE>> key) { return (VALUE) map.get(key); }
      @Override public Set<Class<? extends CoreAnnotation>> keySet() { return map.keySet(); }
      @Override public CoreMap makeCopy() { return null; }
    };

    Optional<String> result = annotator.link(mention);
    Assert.assertTrue(result.isPresent());
    Assert.assertEquals("Paris_France", result.get());
  }
@Test
  public void testLink_nerTagIsOAndNotInDictionaryReturnsEmpty() {
    Properties props = new Properties();
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

    CoreMap mention = new CoreMap() {
      private final Map<Class<? extends CoreAnnotation>, Object> map = new HashMap<>();

      {
        map.put(OriginalTextAnnotation.class, "Apple");
        map.put(NamedEntityTagAnnotation.class, "O");
      }

      @Override public <VALUE> void set(Class<? extends CoreAnnotation<VALUE>> key, VALUE value) { map.put(key, value); }
      @Override public <VALUE> VALUE get(Class<? extends CoreAnnotation<VALUE>> key) { return (VALUE) map.get(key); }
      @Override public Set<Class<? extends CoreAnnotation>> keySet() { return map.keySet(); }
      @Override public CoreMap makeCopy() { return null; }
    };

    Optional<String> result = annotator.link(mention);
    Assert.assertFalse(result.isPresent());
  }
@Test
  public void testNormalizeTimex_withT_returnsStripped() {
    String input = "2023-06-01T10:00";
    String result = WikidictAnnotator.normalizeTimex(input);
    Assert.assertEquals("2023-06-01", result);
  }
@Test
  public void testNormalizeTimex_noT_returnsSameString() {
    String input = "2024-01-01";
    String result = WikidictAnnotator.normalizeTimex(input);
    Assert.assertEquals("2024-01-01", result);
  }
@Test
  public void testNormalizeTimex_presentValue_returnsOriginal() {
    String input = "PRESENT";
    String result = WikidictAnnotator.normalizeTimex(input);
    Assert.assertEquals("PRESENT", result);
  }
@Test
  public void testNThreads_returnDefault() {
    Properties props = new Properties();
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);
    Assert.assertEquals(1, annotator.nThreads());
  }
@Test
  public void testMaxTime_returnDefault() {
    Properties props = new Properties();
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);
    Assert.assertEquals(-1L, annotator.maxTime());
  }
@Test
  public void testRequirementsSatisfied_containsWikipediaEntityAnnotation() {
    Properties props = new Properties();
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);
    Set<Class<? extends CoreAnnotation>> result = annotator.requirementsSatisfied();
    Assert.assertTrue(result.contains(CoreAnnotations.WikipediaEntityAnnotation.class));
    Assert.assertEquals(1, result.size());
  }
@Test
  public void testRequires_containsExpectedAnnotations() {
    Properties props = new Properties();
    WikidictAnnotator annotator = new WikidictAnnotator("entitylink", props);

    Set<Class<? extends CoreAnnotation>> requirements = annotator.requires();

    Assert.assertTrue(requirements.contains(TextAnnotation.class));
    Assert.assertTrue(requirements.contains(TokensAnnotation.class));
    Assert.assertTrue(requirements.contains(SentencesAnnotation.class));
    Assert.assertTrue(requirements.contains(OriginalTextAnnotation.class));
    Assert.assertTrue(requirements.contains(CoreAnnotations.MentionsAnnotation.class));
    Assert.assertEquals(5, requirements.size());
  } 
}