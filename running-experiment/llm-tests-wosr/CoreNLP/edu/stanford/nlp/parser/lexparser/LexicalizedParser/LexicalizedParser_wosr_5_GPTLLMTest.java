public class LexicalizedParser_wosr_5_GPTLLMTest { 

 @Test
  public void testGetOpReturnsOptionsInstance() {
    Options options = new Options();
    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
            null, null, null, options);
    assertSame(options, parser.getOp());
  }
@Test
  public void testCopyParserProducesEquivalentInstance() {
    Options options = new Options();
    Lexicon lex = new EduLexiconStub();
    BinaryGrammar bg = new BinaryGrammar(new HashIndex<>());
    UnaryGrammar ug = new UnaryGrammar(new HashIndex<>());
    DependencyGrammar dg = null;
    Index<String> stateIndex = new HashIndex<>();
    Index<String> wordIndex = new HashIndex<>();
    Index<String> tagIndex = new HashIndex<>();

    LexicalizedParser original = new LexicalizedParser(lex, bg, ug, dg,
            stateIndex, wordIndex, tagIndex, options);
    LexicalizedParser copy = LexicalizedParser.copyLexicalizedParser(original);

    assertNotNull(copy);
    assertSame(original.getOp(), copy.getOp());
    assertSame(original.lex, copy.lex);
    assertSame(original.bg, copy.bg);
    assertSame(original.ug, copy.ug);
    assertSame(original.dg, copy.dg);
  }
@Test
  public void testParseStringsReturnsNonNullTree() {
    Options options = new Options();
    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
            null, null, null, options) {
      @Override
      public Tree parse(List<? extends HasWord> lst) {
        Tree t = new Tree("S");
        t.setScore(1.0);
        return t;
      }
    };

    List<String> input = new ArrayList<>();
    input.add("This");
    input.add("is");
    input.add("a");
    input.add("test");

    Tree result = parser.parseStrings(input);
    assertNotNull(result);
    assertEquals("S", result.value());
  }
@Test
  public void testParseReturnsFallbackTreeOnException() {
    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
            null, null, null, new Options()) {
      @Override
      public ParserQuery parserQuery() {
        throw new RuntimeException("Simulated failure");
      }
    };

    List<HasWord> sentence = new ArrayList<>();
    sentence.add(new Word("Test"));

    Tree result = parser.parse(sentence);
    assertNotNull(result);
    assertEquals("X", result.value());
  }
@Test
  public void testParseReturnsTreeWithCorrectScore() {
    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
            null, null, null, new Options()) {
      @Override
      public ParserQuery parserQuery() {
        return new ParserQuery() {
          @Override
          public boolean parse(List<? extends HasWord> sentence) {
            return true;
          }

          @Override
          public Tree getBestParse() {
            Tree tree = new Tree("ROOT");
            tree.setScore(12345.0);
            return tree;
          }

          @Override
          public double getPCFGScore() {
            return -12345.0;
          }
        };
      }
    };

    List<HasWord> input = new ArrayList<>();
    input.add(new Word("example"));

    Tree result = parser.parse(input);
    assertNotNull(result);
    assertEquals("ROOT", result.value());
    assertEquals(0.0, result.score(), 0.01); 
  }
@Test
  public void testParseTreeReturnsNullOnFailure() {
    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
            null, null, null, new Options()) {
      @Override
      public ParserQuery parserQuery() {
        return new ParserQuery() {
          @Override
          public boolean parse(List<? extends HasWord> sentence) {
            return false;
          }

          @Override
          public Tree getBestParse() {
            return null;
          }
        };
      }
    };

    List<HasWord> input = new ArrayList<>();
    input.add(new Word("fail"));

    Tree result = parser.parseTree(input);
    assertNull(result);
  }
@Test
  public void testParseMultipleSingleThreaded() {
    LexicalizedParser parser = new LexicalizedParser(null, null, null, null,
            null, null, null, new Options()) {
      @Override
      public Tree parse(List<? extends HasWord> lst) {
        Tree t = new Tree("X");
        t.setScore(1.0);
        return t;
      }
    };

    List<List<HasWord>> batches = new ArrayList<>();

    List<HasWord> s1 = new ArrayList<>();
    s1.add(new Word("Hello"));
    batches.add(s1);

    List<HasWord> s2 = new ArrayList<>();
    s2.add(new Word("World"));
    batches.add(s2);

    List<Tree> results = parser.parseMultiple(batches);

    assertEquals(2, results.size());
    assertEquals("X", results.get(0).value());
    assertEquals("X", results.get(1).value());
  }
@Test
  public void testGetTreePrintReturnsNonNull() {
    Options options = new Options();
    LexicalizedParser parser = LexicalizedParser.loadModel(options);
    assertNotNull(parser.getTreePrint());
  }
@Test
  public void testSerializationAndDeserialization() throws Exception {
    Options options = new Options();
    LexicalizedParser original = new LexicalizedParser(null, null, null, null,
            null, null, null, options);

    File tempFile = File.createTempFile("lexParser", ".ser");
    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(tempFile));
    oos.writeObject(original);
    oos.close();

    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(tempFile));
    LexicalizedParser loaded = LexicalizedParser.loadModel(ois);
    ois.close();

    assertNotNull(loaded);
    assertTrue(loaded instanceof LexicalizedParser);

    tempFile.delete();
  }
@Test
  public void testLoadModelReturnsNonNullParser() {
    LexicalizedParser parser = LexicalizedParser.loadModel();
    assertNotNull(parser);
    assertTrue(parser instanceof LexicalizedParser);
  } 
}