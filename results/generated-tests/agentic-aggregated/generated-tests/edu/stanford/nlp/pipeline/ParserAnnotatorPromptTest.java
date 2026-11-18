package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.parser.common.ParserAnnotations;
import edu.stanford.nlp.parser.common.ParserConstraint;
import edu.stanford.nlp.parser.common.ParserGrammar;
import edu.stanford.nlp.parser.common.ParserQuery;
import edu.stanford.nlp.parser.lexparser.EnglishTreebankParserParams;
import edu.stanford.nlp.parser.lexparser.Options;
import edu.stanford.nlp.parser.lexparser.SpanishTreebankParserParams;
import edu.stanford.nlp.parser.lexparser.TreebankLangParserParams;
import edu.stanford.nlp.parser.metrics.Eval;
import edu.stanford.nlp.parser.metrics.ParserQueryEval;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.RuntimeInterruptedException;
import org.junit.Test;

import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

public class ParserAnnotatorPromptTest {

  @Test
  public void signatureUsesDocumentedDefaults() {
    Properties props = new Properties();

    String signature = ParserAnnotator.signature("parse", props);

    assertTrue(signature.contains("parse.model:"));
    assertTrue(signature.contains("parse.debug:false"));
    assertTrue(signature.contains("parse.flags:"));
    assertTrue(signature.contains("parse.maxlen:-1"));
    assertTrue(signature.contains("parse.maxheight:80"));
    assertTrue(signature.contains("parse.maxtime:-1"));
    assertTrue(signature.contains("parse.originalDependencies:false"));
    assertTrue(signature.contains("parse.buildgraphs:true"));
    assertTrue(signature.contains("parse.nthreads:"));
    assertTrue(signature.contains("parse.nosquash:false"));
    assertTrue(signature.contains("parse.keepPunct:true"));
    assertTrue(signature.contains("parse.extradependencies:none"));
    assertTrue(signature.contains("parse.binaryTrees:false"));
  }

  @Test
  public void signatureIncludesEveryExplicitParserSetting() {
    Properties props = new Properties();
    props.setProperty("custom.model", "model.ser.gz");
    props.setProperty("custom.debug", "true");
    props.setProperty("custom.flags", "-foo -bar");
    props.setProperty("custom.maxlen", "17");
    props.setProperty("custom.maxheight", "24");
    props.setProperty("custom.treemap", "example.Mapper");
    props.setProperty("custom.maxtime", "900");
    props.setProperty("custom.originalDependencies", "true");
    props.setProperty("custom.buildgraphs", "false");
    props.setProperty("custom.nthreads", "3");
    props.setProperty("custom.nosquash", "true");
    props.setProperty("custom.keepPunct", "false");
    props.setProperty("custom.extradependencies", "MAXIMAL");
    props.setProperty("custom.binaryTrees", "true");

    String signature = ParserAnnotator.signature("custom", props);

    assertEquals("custom.model:model.ser.gz" +
        "custom.debug:true" +
        "custom.flags:-foo -bar" +
        "custom.maxlen:17" +
        "custom.maxheight:24" +
        "custom.treemap:example.Mapper" +
        "custom.maxtime:900" +
        "custom.originalDependencies:true" +
        "custom.buildgraphs:false" +
        "custom.nthreads:3" +
        "custom.nosquash:true" +
        "custom.keepPunct:false" +
        "custom.extradependencies:maximal" +
        "custom.binaryTrees:true", signature);
  }

  @Test
  public void signatureFallsBackToGlobalThreadCount() {
    Properties props = new Properties();
    props.setProperty("nthreads", "7");

    String signature = ParserAnnotator.signature("parse", props);

    assertTrue(signature.contains("parse.nthreads:7"));
  }

  @Test
  public void requirementsExcludePosWhenParserDoesNotRequireTags() {
    SpanishTreebankParserParams params = new SpanishTreebankParserParams();
    Options options = new Options(params);
    ParserGrammar grammar = new ParserGrammar() {
      public ParserQuery parserQuery() { return null; }
      public Tree parse(List<? extends HasWord> words) { return null; }
      public Tree parseTree(List<? extends HasWord> words) { return null; }
      public List<Eval> getExtraEvals() { return Collections.emptyList(); }
      public List<ParserQueryEval> getParserQueryEvals() { return Collections.emptyList(); }
      public Options getOp() { return options; }
      public TreebankLangParserParams getTLPParams() { return params; }
      public TreebankLanguagePack treebankLanguagePack() { return params.treebankLanguagePack(); }
      public String[] defaultCoreNLPFlags() { return new String[0]; }
      public void setOptionFlags(String... flags) { }
      public boolean requiresTags() { return false; }
    };
    ParserAnnotator annotator = new ParserAnnotator(grammar, false, -1);

    Set<Class<? extends CoreAnnotation>> requirements = annotator.requires();

    assertEquals(9, requirements.size());
    assertTrue(requirements.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.SentencesAnnotation.class));
    assertFalse(requirements.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
    try {
      requirements.add(CoreAnnotations.PartOfSpeechAnnotation.class);
      fail("Requirements must be immutable");
    } catch (UnsupportedOperationException expected) {
      assertNotNull(expected);
    }
  }

  @Test
  public void requirementsIncludePosWhenParserRequiresTags() {
    SpanishTreebankParserParams params = new SpanishTreebankParserParams();
    Options options = new Options(params);
    ParserGrammar grammar = new ParserGrammar() {
      public ParserQuery parserQuery() { return null; }
      public Tree parse(List<? extends HasWord> words) { return null; }
      public Tree parseTree(List<? extends HasWord> words) { return null; }
      public List<Eval> getExtraEvals() { return Collections.emptyList(); }
      public List<ParserQueryEval> getParserQueryEvals() { return Collections.emptyList(); }
      public Options getOp() { return options; }
      public TreebankLangParserParams getTLPParams() { return params; }
      public TreebankLanguagePack treebankLanguagePack() { return params.treebankLanguagePack(); }
      public String[] defaultCoreNLPFlags() { return new String[0]; }
      public void setOptionFlags(String... flags) { }
      public boolean requiresTags() { return true; }
    };

    Set<Class<? extends CoreAnnotation>> requirements =
        new ParserAnnotator(grammar, false, -1).requires();

    assertEquals(10, requirements.size());
    assertTrue(requirements.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.CharacterOffsetEndAnnotation.class));
  }

  @Test
  public void requirementsSatisfiedForNonDependencyParserAreMinimalAndImmutable() {
    SpanishTreebankParserParams params = new SpanishTreebankParserParams();
    Options options = new Options(params);
    ParserGrammar grammar = new ParserGrammar() {
      public ParserQuery parserQuery() { return null; }
      public Tree parse(List<? extends HasWord> words) { return null; }
      public Tree parseTree(List<? extends HasWord> words) { return null; }
      public List<Eval> getExtraEvals() { return Collections.emptyList(); }
      public List<ParserQueryEval> getParserQueryEvals() { return Collections.emptyList(); }
      public Options getOp() { return options; }
      public TreebankLangParserParams getTLPParams() { return params; }
      public TreebankLanguagePack treebankLanguagePack() { return params.treebankLanguagePack(); }
      public String[] defaultCoreNLPFlags() { return new String[0]; }
      public void setOptionFlags(String... flags) { }
      public boolean requiresTags() { return false; }
    };
    ParserAnnotator annotator = new ParserAnnotator(grammar, false, 10);

    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertEquals(3, satisfied.size());
    assertTrue(satisfied.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertTrue(satisfied.contains(TreeCoreAnnotations.TreeAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.CategoryAnnotation.class));
    assertFalse(satisfied.contains(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class));
    try {
      satisfied.clear();
      fail("Satisfied requirements must be immutable");
    } catch (UnsupportedOperationException expected) {
      assertNotNull(expected);
    }
  }

  @Test
  public void requirementsSatisfiedAdvertiseAllDependencyGraphsWhenSupported() {
    EnglishTreebankParserParams params = new EnglishTreebankParserParams();
    Options options = new Options(params);
    ParserGrammar grammar = new ParserGrammar() {
      public ParserQuery parserQuery() { return null; }
      public Tree parse(List<? extends HasWord> words) { return null; }
      public Tree parseTree(List<? extends HasWord> words) { return null; }
      public List<Eval> getExtraEvals() { return Collections.emptyList(); }
      public List<ParserQueryEval> getParserQueryEvals() { return Collections.emptyList(); }
      public Options getOp() { return options; }
      public TreebankLangParserParams getTLPParams() { return params; }
      public TreebankLanguagePack treebankLanguagePack() { return params.treebankLanguagePack(); }
      public String[] defaultCoreNLPFlags() { return new String[0]; }
      public void setOptionFlags(String... flags) { }
      public boolean requiresTags() { return false; }
    };

    Set<Class<? extends CoreAnnotation>> satisfied =
        new ParserAnnotator(grammar, false, 10).requirementsSatisfied();

    assertEquals(10, satisfied.size());
    assertTrue(satisfied.contains(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class));
    assertTrue(satisfied.contains(SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class));
    assertTrue(satisfied.contains(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class));
    assertTrue(satisfied.contains(SemanticGraphCoreAnnotations.EnhancedDependenciesAnnotation.class));
    assertTrue(satisfied.contains(SemanticGraphCoreAnnotations.EnhancedPlusPlusDependenciesAnnotation.class));
  }

  @Test
  public void successfulParseStoresTreeFillsTagsAndNormalizesScore() {
    SpanishTreebankParserParams params = new SpanishTreebankParserParams();
    Options options = new Options(params);
    Tree parserTree = Tree.valueOf("(ROOT (S (NNP Alice) (VBZ runs)))");
    ParserQuery query = (ParserQuery) Proxy.newProxyInstance(
        ParserQuery.class.getClassLoader(), new Class<?>[]{ParserQuery.class},
        (proxy, method, args) -> {
          if (method.getName().equals("parse")) return true;
          if (method.getName().equals("getBestParse")) return parserTree;
          if (method.getName().equals("getBestScore")) return -10005.0;
          if (method.getReturnType().equals(boolean.class)) return false;
          if (method.getReturnType().equals(double.class)) return 0.0;
          return null;
        });
    ParserGrammar grammar = new ParserGrammar() {
      public ParserQuery parserQuery() { return query; }
      public Tree parse(List<? extends HasWord> words) { return parserTree; }
      public Tree parseTree(List<? extends HasWord> words) { return parserTree; }
      public List<Eval> getExtraEvals() { return Collections.emptyList(); }
      public List<ParserQueryEval> getParserQueryEvals() { return Collections.emptyList(); }
      public Options getOp() { return options; }
      public TreebankLangParserParams getTLPParams() { return params; }
      public TreebankLanguagePack treebankLanguagePack() { return params.treebankLanguagePack(); }
      public String[] defaultCoreNLPFlags() { return new String[0]; }
      public void setOptionFlags(String... flags) { }
      public boolean requiresTags() { return false; }
    };
    CoreLabel alice = new CoreLabel();
    alice.setWord("Alice");
    CoreLabel runs = new CoreLabel();
    runs.setWord("runs");
    Annotation sentence = new Annotation("Alice runs");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(alice, runs));
    Annotation document = new Annotation("Alice runs");
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    new ParserAnnotator(grammar, false, 2).annotate(document);

    Tree result = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
    assertSame(parserTree, result);
    assertEquals(-5.0, result.score(), 0.0);
    assertEquals("NNP", alice.tag());
    assertEquals("VBZ", runs.tag());
    assertEquals(Integer.valueOf(0),
        ((CoreLabel) result.label()).get(CoreAnnotations.BeginIndexAnnotation.class));
    assertEquals(Integer.valueOf(2),
        ((CoreLabel) result.label()).get(CoreAnnotations.EndIndexAnnotation.class));
  }

  @Test
  public void constraintsAreForwardedToParserQuery() {
    SpanishTreebankParserParams params = new SpanishTreebankParserParams();
    Options options = new Options(params);
    Tree parserTree = Tree.valueOf("(ROOT (NP (NN cats)))");
    AtomicReference<Object> receivedConstraints = new AtomicReference<>();
    ParserQuery query = (ParserQuery) Proxy.newProxyInstance(
        ParserQuery.class.getClassLoader(), new Class<?>[]{ParserQuery.class},
        (proxy, method, args) -> {
          if (method.getName().equals("setConstraints")) {
            receivedConstraints.set(args[0]);
            return null;
          }
          if (method.getName().equals("parse")) return true;
          if (method.getName().equals("getBestParse")) return parserTree;
          if (method.getName().equals("getBestScore")) return 2.5;
          if (method.getReturnType().equals(boolean.class)) return false;
          if (method.getReturnType().equals(double.class)) return 0.0;
          return null;
        });
    ParserGrammar grammar = new ParserGrammar() {
      public ParserQuery parserQuery() { return query; }
      public Tree parse(List<? extends HasWord> words) { return parserTree; }
      public Tree parseTree(List<? extends HasWord> words) { return parserTree; }
      public List<Eval> getExtraEvals() { return Collections.emptyList(); }
      public List<ParserQueryEval> getParserQueryEvals() { return Collections.emptyList(); }
      public Options getOp() { return options; }
      public TreebankLangParserParams getTLPParams() { return params; }
      public TreebankLanguagePack treebankLanguagePack() { return params.treebankLanguagePack(); }
      public String[] defaultCoreNLPFlags() { return new String[0]; }
      public void setOptionFlags(String... flags) { }
      public boolean requiresTags() { return false; }
    };
    CoreLabel cats = new CoreLabel();
    cats.setWord("cats");
    ParserConstraint constraint = new ParserConstraint(0, 1, "NP.*");
    List<ParserConstraint> constraints = Collections.singletonList(constraint);
    Annotation sentence = new Annotation("cats");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(cats));
    sentence.set(ParserAnnotations.ConstraintAnnotation.class, constraints);

    new ParserAnnotator(grammar, false, 5).doOneSentence(new Annotation("cats"), sentence);

    assertSame(constraints, receivedConstraints.get());
    assertEquals("NP", sentence.get(TreeCoreAnnotations.TreeAnnotation.class).firstChild().value());
  }

  @Test
  public void overLengthSentenceUsesFallbackAndPreservesExistingTag() {
    SpanishTreebankParserParams params = new SpanishTreebankParserParams();
    Options options = new Options(params);
    AtomicInteger parserQueryCalls = new AtomicInteger();
    ParserGrammar grammar = new ParserGrammar() {
      public ParserQuery parserQuery() { parserQueryCalls.incrementAndGet(); return null; }
      public Tree parse(List<? extends HasWord> words) { return null; }
      public Tree parseTree(List<? extends HasWord> words) { return null; }
      public List<Eval> getExtraEvals() { return Collections.emptyList(); }
      public List<ParserQueryEval> getParserQueryEvals() { return Collections.emptyList(); }
      public Options getOp() { return options; }
      public TreebankLangParserParams getTLPParams() { return params; }
      public TreebankLanguagePack treebankLanguagePack() { return params.treebankLanguagePack(); }
      public String[] defaultCoreNLPFlags() { return new String[0]; }
      public void setOptionFlags(String... flags) { }
      public boolean requiresTags() { return false; }
    };
    CoreLabel first = new CoreLabel();
    first.setWord("too");
    first.setTag("RB");
    CoreLabel second = new CoreLabel();
    second.setWord("long");
    Annotation sentence = new Annotation("too long");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(first, second));

    new ParserAnnotator(grammar, false, 1).doOneSentence(new Annotation("too long"), sentence);

    assertEquals(0, parserQueryCalls.get());
    assertEquals("X", sentence.get(TreeCoreAnnotations.TreeAnnotation.class).value());
    assertEquals("RB", first.tag());
    assertEquals("XX", second.tag());
  }

  @Test
  public void nullBestParseUsesFallbackTree() {
    SpanishTreebankParserParams params = new SpanishTreebankParserParams();
    Options options = new Options(params);
    ParserQuery query = (ParserQuery) Proxy.newProxyInstance(
        ParserQuery.class.getClassLoader(), new Class<?>[]{ParserQuery.class},
        (proxy, method, args) -> {
          if (method.getName().equals("parse")) return true;
          if (method.getName().equals("getBestParse")) return null;
          if (method.getReturnType().equals(boolean.class)) return false;
          if (method.getReturnType().equals(double.class)) return 0.0;
          return null;
        });
    ParserGrammar grammar = new ParserGrammar() {
      public ParserQuery parserQuery() { return query; }
      public Tree parse(List<? extends HasWord> words) { return null; }
      public Tree parseTree(List<? extends HasWord> words) { return null; }
      public List<Eval> getExtraEvals() { return Collections.emptyList(); }
      public List<ParserQueryEval> getParserQueryEvals() { return Collections.emptyList(); }
      public Options getOp() { return options; }
      public TreebankLangParserParams getTLPParams() { return params; }
      public TreebankLanguagePack treebankLanguagePack() { return params.treebankLanguagePack(); }
      public String[] defaultCoreNLPFlags() { return new String[0]; }
      public void setOptionFlags(String... flags) { }
      public boolean requiresTags() { return false; }
    };
    CoreLabel token = new CoreLabel();
    token.setWord("unparsed");
    Annotation sentence = new Annotation("unparsed");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    new ParserAnnotator(grammar, false, -1).doOneSentence(new Annotation("unparsed"), sentence);

    assertEquals("X", sentence.get(TreeCoreAnnotations.TreeAnnotation.class).value());
    assertEquals("XX", token.tag());
  }

  @Test
  public void interruptedParseUsesFallbackTree() {
    SpanishTreebankParserParams params = new SpanishTreebankParserParams();
    Options options = new Options(params);
    ParserQuery query = (ParserQuery) Proxy.newProxyInstance(
        ParserQuery.class.getClassLoader(), new Class<?>[]{ParserQuery.class},
        (proxy, method, args) -> {
          if (method.getName().equals("parse")) throw new RuntimeInterruptedException();
          if (method.getReturnType().equals(boolean.class)) return false;
          if (method.getReturnType().equals(double.class)) return 0.0;
          return null;
        });
    ParserGrammar grammar = new ParserGrammar() {
      public ParserQuery parserQuery() { return query; }
      public Tree parse(List<? extends HasWord> words) { return null; }
      public Tree parseTree(List<? extends HasWord> words) { return null; }
      public List<Eval> getExtraEvals() { return Collections.emptyList(); }
      public List<ParserQueryEval> getParserQueryEvals() { return Collections.emptyList(); }
      public Options getOp() { return options; }
      public TreebankLangParserParams getTLPParams() { return params; }
      public TreebankLanguagePack treebankLanguagePack() { return params.treebankLanguagePack(); }
      public String[] defaultCoreNLPFlags() { return new String[0]; }
      public void setOptionFlags(String... flags) { }
      public boolean requiresTags() { return false; }
    };
    CoreLabel token = new CoreLabel();
    token.setWord("slow");
    Annotation sentence = new Annotation("slow");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    new ParserAnnotator(grammar, false, 5).doOneSentence(new Annotation("slow"), sentence);

    assertEquals("X", sentence.get(TreeCoreAnnotations.TreeAnnotation.class).value());
    assertEquals("XX", token.tag());
  }

  @Test
  public void treeMapperTransformsFallbackBeforeItIsStored() {
    SpanishTreebankParserParams params = new SpanishTreebankParserParams();
    Options options = new Options(params);
    ParserGrammar grammar = new ParserGrammar() {
      public ParserQuery parserQuery() { return null; }
      public Tree parse(List<? extends HasWord> words) { return null; }
      public Tree parseTree(List<? extends HasWord> words) { return null; }
      public List<Eval> getExtraEvals() { return Collections.emptyList(); }
      public List<ParserQueryEval> getParserQueryEvals() { return Collections.emptyList(); }
      public Options getOp() { return options; }
      public TreebankLangParserParams getTLPParams() { return params; }
      public TreebankLanguagePack treebankLanguagePack() { return params.treebankLanguagePack(); }
      public String[] defaultCoreNLPFlags() { return new String[0]; }
      public void setOptionFlags(String... flags) { }
      public boolean requiresTags() { return false; }
    };
    CoreLabel token = new CoreLabel();
    token.setWord("mapped");
    Annotation sentence = new Annotation("mapped");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    ParserAnnotator annotator = new ParserAnnotator(grammar, false, 0,
        tree -> Tree.valueOf("(MAPPED (JJ mapped))"));

    annotator.doOneFailedSentence(new Annotation("mapped"), sentence);

    assertEquals("MAPPED", sentence.get(TreeCoreAnnotations.TreeAnnotation.class).value());
    assertEquals("JJ", sentence.get(TreeCoreAnnotations.TreeAnnotation.class).firstChild().value());
    assertEquals("XX", token.tag());
  }

  @Test
  public void annotateProcessesMultipleSentencesThroughIndependentParserQueries() {
    SpanishTreebankParserParams params = new SpanishTreebankParserParams();
    Options options = new Options(params);
    AtomicInteger queryCount = new AtomicInteger();
    ParserGrammar grammar = new ParserGrammar() {
      public ParserQuery parserQuery() {
        int invocation = queryCount.incrementAndGet();
        Tree result = invocation == 1
            ? Tree.valueOf("(ROOT (NP (NNP One)))")
            : Tree.valueOf("(ROOT (VP (VB Runs)))");
        return (ParserQuery) Proxy.newProxyInstance(
            ParserQuery.class.getClassLoader(), new Class<?>[]{ParserQuery.class},
            (proxy, method, args) -> {
              if (method.getName().equals("parse")) return true;
              if (method.getName().equals("getBestParse")) return result;
              if (method.getName().equals("getBestScore")) return (double) invocation;
              if (method.getReturnType().equals(boolean.class)) return false;
              if (method.getReturnType().equals(double.class)) return 0.0;
              return null;
            });
      }
      public Tree parse(List<? extends HasWord> words) { return null; }
      public Tree parseTree(List<? extends HasWord> words) { return null; }
      public List<Eval> getExtraEvals() { return Collections.emptyList(); }
      public List<ParserQueryEval> getParserQueryEvals() { return Collections.emptyList(); }
      public Options getOp() { return options; }
      public TreebankLangParserParams getTLPParams() { return params; }
      public TreebankLanguagePack treebankLanguagePack() { return params.treebankLanguagePack(); }
      public String[] defaultCoreNLPFlags() { return new String[0]; }
      public void setOptionFlags(String... flags) { }
      public boolean requiresTags() { return false; }
    };
    CoreLabel one = new CoreLabel();
    one.setWord("One");
    Annotation first = new Annotation("One");
    first.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(one));
    CoreLabel runs = new CoreLabel();
    runs.setWord("Runs");
    Annotation second = new Annotation("Runs");
    second.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(runs));
    Annotation document = new Annotation("One. Runs.");
    document.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(first, second));

    new ParserAnnotator(grammar, false, -1).annotate(document);

    assertEquals(2, queryCount.get());
    assertEquals("NP", first.get(TreeCoreAnnotations.TreeAnnotation.class).firstChild().value());
    assertEquals("VP", second.get(TreeCoreAnnotations.TreeAnnotation.class).firstChild().value());
    assertEquals("NNP", one.tag());
    assertEquals("VB", runs.tag());
  }

  @Test(expected = IllegalArgumentException.class)
  public void annotateRejectsDocumentWithoutSentenceAnnotation() {
    SpanishTreebankParserParams params = new SpanishTreebankParserParams();
    Options options = new Options(params);
    ParserGrammar grammar = new ParserGrammar() {
      public ParserQuery parserQuery() { return null; }
      public Tree parse(List<? extends HasWord> words) { return null; }
      public Tree parseTree(List<? extends HasWord> words) { return null; }
      public List<Eval> getExtraEvals() { return Collections.emptyList(); }
      public List<ParserQueryEval> getParserQueryEvals() { return Collections.emptyList(); }
      public Options getOp() { return options; }
      public TreebankLangParserParams getTLPParams() { return params; }
      public TreebankLanguagePack treebankLanguagePack() { return params.treebankLanguagePack(); }
      public String[] defaultCoreNLPFlags() { return new String[0]; }
      public void setOptionFlags(String... flags) { }
      public boolean requiresTags() { return false; }
    };

    new ParserAnnotator(grammar, false, -1).annotate(new Annotation("No sentence list"));
  }
}
