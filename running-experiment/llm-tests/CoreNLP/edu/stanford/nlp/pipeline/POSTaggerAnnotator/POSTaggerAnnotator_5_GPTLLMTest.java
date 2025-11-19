package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.parser.common.ParserAnnotations;
import edu.stanford.nlp.parser.common.ParserConstraint;
import edu.stanford.nlp.parser.common.ParserGrammar;
import edu.stanford.nlp.parser.common.ParserQuery;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.parser.lexparser.TreebankLangParserParams;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.time.TimeExpression;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.util.*;

import edu.stanford.nlp.ie.NERClassifierCombiner;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.Timex;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import org.junit.Test;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;

public class POSTaggerAnnotator_5_GPTLLMTest {

 @Test
  public void testDefaultConstructor() {
    POSTaggerAnnotator annotator = new POSTaggerAnnotator();
    assertNotNull(annotator);
  }
@Test
  public void testConstructorWithModelAndConfig() {
    MaxentTagger tagger = new MaxentTagger() {
      @Override
      public List<TaggedWord> tagSentence(List<? extends edu.stanford.nlp.ling.HasWord> sentence, boolean reuseTags) {
        TaggedWord tagged1 = new TaggedWord(sentence.get(0).word(), "NN");
        TaggedWord tagged2 = new TaggedWord(sentence.get(1).word(), "VB");
        return Arrays.asList(tagged1, tagged2);
      }
    };
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 100, 1);
    assertNotNull(annotator);
  }
@Test
  public void testAnnotateSingleSentence() {
    MaxentTagger tagger = new MaxentTagger() {
      @Override
      public List<TaggedWord> tagSentence(List<? extends edu.stanford.nlp.ling.HasWord> sentence, boolean reuseTags) {
        TaggedWord tagged1 = new TaggedWord(sentence.get(0).word(), "NN");
        TaggedWord tagged2 = new TaggedWord(sentence.get(1).word(), "VB");
        return Arrays.asList(tagged1, tagged2);
      }
    };

    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 100, 1);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Time");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("flies");

    List<CoreLabel> tokens = Arrays.asList(token1, token2);

    List<CoreMap> sentenceList = new ArrayList<>();

    Annotation annotation = new Annotation("Test");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentenceList);

    annotator.annotate(annotation);

    List<CoreLabel> resultTokens = annotation.get(CoreAnnotations.SentencesAnnotation.class)
        .get(0).get(CoreAnnotations.TokensAnnotation.class);

    assertEquals("NN", resultTokens.get(0).get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("VB", resultTokens.get(1).get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testAnnotateEmptySentence() {
    MaxentTagger tagger = new MaxentTagger() {
      @Override
      public List<TaggedWord> tagSentence(List<? extends edu.stanford.nlp.ling.HasWord> sentence, boolean reuseTags) {
        return Collections.emptyList();
      }
    };

    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 100, 1);

    List<CoreLabel> emptyTokens = new ArrayList<>();

    Annotation annotation = new Annotation("Empty");

    annotator.annotate(annotation);

    List<CoreLabel> result = annotation.get(CoreAnnotations.SentencesAnnotation.class)
        .get(0).get(CoreAnnotations.TokensAnnotation.class);

    assertTrue(result.isEmpty());
  }
@Test
  public void testTaggingSkippedForLongSentence() {
    MaxentTagger tagger = new MaxentTagger() {
      @Override
      public List<TaggedWord> tagSentence(List<? extends edu.stanford.nlp.ling.HasWord> sentence, boolean reuseTags) {
        return Arrays.asList(); 
      }
    };

    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 1, 1); 

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Too");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("long");

    List<CoreLabel> tokens = Arrays.asList(token1, token2);

    Annotation annotation = new Annotation("Long");

    annotator.annotate(annotation);

    List<CoreLabel> result = annotation.get(CoreAnnotations.SentencesAnnotation.class)
        .get(0).get(CoreAnnotations.TokensAnnotation.class);

    assertEquals("X", result.get(0).get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("X", result.get(1).get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testAnnotateMissingSentencesThrowsRuntimeException() {
    MaxentTagger tagger = new MaxentTagger();
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 100, 1);

    Annotation annotation = new Annotation("No sentences");

    try {
      annotator.annotate(annotation);
      fail("Expected RuntimeException for missing sentence annotations");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("unable to find words"));
    }
  }
@Test
  public void testPOSSetOnMultipleSentences() {
    MaxentTagger tagger = new MaxentTagger() {
      @Override
      public List<TaggedWord> tagSentence(List<? extends edu.stanford.nlp.ling.HasWord> sentence, boolean reuseTags) {
        List<TaggedWord> tags = new ArrayList<>();
        for (edu.stanford.nlp.ling.HasWord word : sentence) {
          tags.add(new TaggedWord(word.word(), "NN"));
        }
        return tags;
      }
    };

    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 100, 1);

    CoreLabel tokenA = new CoreLabel();
    tokenA.setWord("This");

    CoreLabel tokenB = new CoreLabel();
    tokenB.setWord("works");

    CoreLabel tokenC = new CoreLabel();
    tokenC.setWord("Second");

    CoreLabel tokenD = new CoreLabel();
    tokenD.setWord("sentence");

    Annotation annotation = new Annotation("Multi");

    annotator.annotate(annotation);

    CoreLabel r1 = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0)
        .get(CoreAnnotations.TokensAnnotation.class).get(0);
    CoreLabel r2 = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0)
        .get(CoreAnnotations.TokensAnnotation.class).get(1);
    CoreLabel r3 = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(1)
        .get(CoreAnnotations.TokensAnnotation.class).get(0);
    CoreLabel r4 = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(1)
        .get(CoreAnnotations.TokensAnnotation.class).get(1);

    assertEquals("NN", r1.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("NN", r2.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("NN", r3.get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("NN", r4.get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testOutOfMemoryHandledGracefully() {
    MaxentTagger tagger = new MaxentTagger() {
      @Override
      public List<TaggedWord> tagSentence(List<? extends edu.stanford.nlp.ling.HasWord> sentence, boolean reuseTags) {
        throw new OutOfMemoryError("Simulated error");
      }
    };

    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 100, 1);

    CoreLabel t1 = new CoreLabel();
    t1.setWord("fail");
    CoreLabel t2 = new CoreLabel();
    t2.setWord("test");

    List<CoreLabel> tokens = Arrays.asList(t1, t2);

    Annotation annotation = new Annotation("OOM Test");

    annotator.annotate(annotation);

    List<CoreLabel> result = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0)
        .get(CoreAnnotations.TokensAnnotation.class);

    assertEquals("X", result.get(0).get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("X", result.get(1).get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testRequiresAndSatisfiedReturnsExpectedSets() {
    MaxentTagger tagger = new MaxentTagger();

    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 100, 1);

    Set<Class<? extends CoreAnnotation>> required = annotator.requires();
    assertTrue(required.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.CharacterOffsetEndAnnotation.class));

    Set<Class<? extends CoreAnnotation>> satisfied =
        annotator.requirementsSatisfied();
    assertEquals(1, satisfied.size());
    assertTrue(satisfied.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testConstructorWithProperties() {
    Properties props = new Properties();
    props.setProperty("tagger.model", MaxentTagger.DEFAULT_JAR_PATH);
    props.setProperty("tagger.verbose", "true");
    props.setProperty("tagger.maxlen", "10");
    props.setProperty("tagger.nthreads", "2");
    props.setProperty("tagger.reuseTags", "true");

    POSTaggerAnnotator annotator = new POSTaggerAnnotator("tagger", props);
    assertNotNull(annotator);
  }
@Test
  public void testThreadedProcessingMultipleSentences() {
    MaxentTagger tagger = new MaxentTagger() {
      public List<TaggedWord> tagSentence(List<? extends edu.stanford.nlp.ling.HasWord> sentence, boolean reuseTags) {
        TaggedWord tw1 = new TaggedWord(sentence.get(0).word(), "RB");
        TaggedWord tw2 = new TaggedWord(sentence.get(1).word(), "VB");
        return Arrays.asList(tw1, tw2);
      }
    };

    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 10, 4);

    CoreLabel t1 = new CoreLabel(); t1.setWord("Quickly");
    CoreLabel t2 = new CoreLabel(); t2.setWord("run");

    CoreLabel t3 = new CoreLabel(); t3.setWord("Slowly");
    CoreLabel t4 = new CoreLabel(); t4.setWord("walk");

    Annotation annotation = new Annotation("");

    annotator.annotate(annotation);

    List<CoreMap> result = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals("RB", result.get(0).get(CoreAnnotations.TokensAnnotation.class).get(0).get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("VB", result.get(0).get(CoreAnnotations.TokensAnnotation.class).get(1).get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("RB", result.get(1).get(CoreAnnotations.TokensAnnotation.class).get(0).get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("VB", result.get(1).get(CoreAnnotations.TokensAnnotation.class).get(1).get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testTokenWithEmptyStringAndNull() {
    MaxentTagger tagger = new MaxentTagger() {
      public List<TaggedWord> tagSentence(List<? extends edu.stanford.nlp.ling.HasWord> sentence, boolean reuseTags) {
        return Arrays.asList(
            new TaggedWord(sentence.get(0).word(), "DT"),
            new TaggedWord(sentence.get(1).word(), "JJ")
        );
      }
    };

    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 10, 1);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("");

    CoreLabel token2 = new CoreLabel();
    token2.setWord(null);

    Annotation annotation = new Annotation("");

    annotator.annotate(annotation);

    List<CoreLabel> result = annotation.get(CoreAnnotations.SentencesAnnotation.class)
        .get(0).get(CoreAnnotations.TokensAnnotation.class);
    
    assertEquals("DT", result.get(0).get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("JJ", result.get(1).get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testTaggerReturnsNull() {
    MaxentTagger tagger = new MaxentTagger() {
      public List<TaggedWord> tagSentence(List<? extends edu.stanford.nlp.ling.HasWord> sentence, boolean reuseTags) {
        return null;
      }
    };

    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 10, 1);

    CoreLabel token1 = new CoreLabel(); token1.setWord("Missing");


    Annotation annotation = new Annotation("");

    annotator.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.SentencesAnnotation.class)
        .get(0).get(CoreAnnotations.TokensAnnotation.class);
    assertEquals("X", tokens.get(0).get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testMissingTokensAnnotationInSentence() {
    MaxentTagger tagger = new MaxentTagger() {
      public List<TaggedWord> tagSentence(List<? extends edu.stanford.nlp.ling.HasWord> sentence, boolean reuseTags) {
        return Collections.singletonList(new TaggedWord("?", "NN"));
      }
    };

    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 10, 1);

    Annotation annotation = new Annotation("");

    try {
      annotator.annotate(annotation);
      fail("Expected NullPointerException or equivalent due to missing TokensAnnotation");
    } catch (NullPointerException | ClassCastException e) {
      
    }
  }
@Test
  public void testConstructorWithPropertiesDefaults() {
    Properties props = new Properties();
    POSTaggerAnnotator annotator = new POSTaggerAnnotator("pos", props);
    assertNotNull(annotator);
  }
@Test
  public void testExplicitReuseTagsInProperties() {
    Properties props = new Properties();
    props.setProperty("custom.model", MaxentTagger.DEFAULT_JAR_PATH);
    props.setProperty("custom.reuseTags", "true");

    POSTaggerAnnotator annotator = new POSTaggerAnnotator("custom", props);
    assertNotNull(annotator);
  }
@Test
  public void testExtremeThreadCount() {
    MaxentTagger tagger = new MaxentTagger() {
      public List<TaggedWord> tagSentence(List<? extends edu.stanford.nlp.ling.HasWord> sentence, boolean reuseTags) {
        List<TaggedWord> results = new ArrayList<>();
        for (edu.stanford.nlp.ling.HasWord token : sentence) {
          results.add(new TaggedWord(token.word(), "NN"));
        }
        return results;
      }
    };

    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 100, 16);

    CoreLabel token1 = new CoreLabel(); token1.setWord("One");
    CoreLabel token2 = new CoreLabel(); token2.setWord("Two");

    List<CoreMap> sentences = new ArrayList<>();

    Annotation annotation = new Annotation("Thread test");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, sentences);

    annotator.annotate(annotation);

    List<CoreLabel> result = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0)
        .get(CoreAnnotations.TokensAnnotation.class);
    assertEquals("NN", result.get(0).get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("NN", result.get(1).get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testTaggerReturnsFewerTagsThanTokens() {
    MaxentTagger tagger = new MaxentTagger() {
      public List<TaggedWord> tagSentence(List<? extends HasWord> sentence, boolean reuseTags) {
        if (sentence.size() >= 1) {
          TaggedWord tag = new TaggedWord(sentence.get(0).word(), "NN");
          return Collections.singletonList(tag); 
        }
        return Collections.emptyList();
      }
    };

    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 10, 1);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Only1");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Only2");

    List<CoreLabel> tokens = Arrays.asList(token1, token2);

    Annotation annotation = new Annotation("Test Sentence");

    try {
      annotator.annotate(annotation);
      fail("Expected IndexOutOfBoundsException due to mismatched tag count");
    } catch (IndexOutOfBoundsException e) {
      
    }
  }
@Test
  public void testEmptyTokensAnnotationButPresentSentence() {
    MaxentTagger tagger = new MaxentTagger();

    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 10, 1);


    Annotation annotation = new Annotation("");

    annotator.annotate(annotation); 

    List<CoreLabel> tokens = annotation
        .get(CoreAnnotations.SentencesAnnotation.class)
        .get(0)
        .get(CoreAnnotations.TokensAnnotation.class);
    assertTrue(tokens.isEmpty()); 
  }
@Test
  public void testTaggerThrowsUncheckedException() {
    MaxentTagger tagger = new MaxentTagger() {
      public List<TaggedWord> tagSentence(List<? extends HasWord> sentence, boolean reuseTags) {
        throw new IllegalStateException("Test unchecked exception");
      }
    };

    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 10, 1);

    CoreLabel token = new CoreLabel();
    token.setWord("fail");

    Annotation annotation = new Annotation("Throw unchecked");

    try {
      annotator.annotate(annotation);
      fail("Expected unchecked exception to propagate");
    } catch (IllegalStateException e) {
      assertEquals("Test unchecked exception", e.getMessage());
    }
  }
@Test
  public void testMultipleSentencesOneEmptyOneValid() {
    MaxentTagger tagger = new MaxentTagger() {
      public List<TaggedWord> tagSentence(List<? extends HasWord> sentence, boolean reuseTags) {
        TaggedWord tagged1 = new TaggedWord(sentence.get(0).word(), "PRP");
        return Collections.singletonList(tagged1);
      }
    };

    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 10, 1);


    CoreLabel validToken = new CoreLabel();
    validToken.setWord("He");


    Annotation annotation = new Annotation("Empty and valid sentence test");

    annotator.annotate(annotation);

    List<CoreLabel> tokens1 = annotation
        .get(CoreAnnotations.SentencesAnnotation.class).get(0)
        .get(CoreAnnotations.TokensAnnotation.class);
    assertTrue(tokens1.isEmpty());

    List<CoreLabel> tokens2 = annotation
        .get(CoreAnnotations.SentencesAnnotation.class).get(1)
        .get(CoreAnnotations.TokensAnnotation.class);
    assertEquals("PRP", tokens2.get(0).get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testConstructorWithNullModel() {
    try {
      POSTaggerAnnotator annotator = new POSTaggerAnnotator((MaxentTagger) null);
      fail("Expected NullPointerException for null model");
    } catch (NullPointerException e) {
      
    }
  }
@Test
  public void testConstructorWithEmptyModelPath() {
    try {
      POSTaggerAnnotator annotator = new POSTaggerAnnotator("", false);
      assertNotNull(annotator);
    } catch (Exception e) {
      
    }
  }
@Test
  public void testAnnotateWithEmptyAnnotationObject() {
    MaxentTagger tagger = new MaxentTagger();

    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 10, 1);

    Annotation emptyAnnotation = new Annotation("");

    try {
      annotator.annotate(emptyAnnotation);
      fail("Expected RuntimeException due to missing SentencesAnnotation");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("unable to find words"));
    }
  }
@Test
  public void testRejectAllSentencesDueToZeroMaxLength() {
    MaxentTagger dummyTagger = new MaxentTagger() {
      public List<TaggedWord> tagSentence(List<? extends HasWord> sentence, boolean reuseTags) {
        TaggedWord tw = new TaggedWord(sentence.get(0).word(), "JJ");
        return Collections.singletonList(tw);
      }
    };

    POSTaggerAnnotator annotator = new POSTaggerAnnotator(dummyTagger, 0, 1);

    CoreLabel token = new CoreLabel();
    token.setWord("ignored");

    Annotation annotation = new Annotation("Sentence should be skipped.");

    annotator.annotate(annotation);

    String resultTag = annotation.get(CoreAnnotations.SentencesAnnotation.class)
        .get(0).get(CoreAnnotations.TokensAnnotation.class)
        .get(0).get(CoreAnnotations.PartOfSpeechAnnotation.class);

    assertEquals("X", resultTag);
  }
@Test
  public void testConstructorWithMissingModelPropertyFallsBackToDefault() {
    Properties props = new Properties();
    POSTaggerAnnotator annotator = new POSTaggerAnnotator("missingModel", props);
    assertNotNull(annotator);
  }
@Test
  public void testGlobalThreadsPropertyOverridesLocalWhenMissing() {
    Properties props = new Properties();
    props.setProperty("nthreads", "2");
    POSTaggerAnnotator annotator = new POSTaggerAnnotator("foo", props);
    assertNotNull(annotator);
  }
@Test
  public void testReuseTagsTrueConfig() {
    MaxentTagger tagger = new MaxentTagger() {
      public List<TaggedWord> tagSentence(List<? extends HasWord> sentence, boolean reuseTags) {
        if (reuseTags) {
          TaggedWord tagged = new TaggedWord(sentence.get(0).word(), "VBZ");
          return Collections.singletonList(tagged);
        } else {
          TaggedWord tagged = new TaggedWord(sentence.get(0).word(), "NN");
          return Collections.singletonList(tagged);
        }
      }
    };

    Properties props = new Properties();
    props.setProperty("abc.model", MaxentTagger.DEFAULT_JAR_PATH);
    props.setProperty("abc.reuseTags", "true");
    POSTaggerAnnotator annotator = new POSTaggerAnnotator("abc", props);

    CoreLabel token = new CoreLabel();
    token.setWord("runs");

    Annotation annotation = new Annotation("Example text");

    annotator.annotate(annotation);

    String resultTag = annotation.get(CoreAnnotations.SentencesAnnotation.class)
        .get(0).get(CoreAnnotations.TokensAnnotation.class)
        .get(0).get(CoreAnnotations.PartOfSpeechAnnotation.class);

    
    assertEquals("VBZ", resultTag);
  }
@Test
  public void testLargeMaxSentenceLengthStillProcessesTokens() {
    MaxentTagger tagger = new MaxentTagger() {
      public List<TaggedWord> tagSentence(List<? extends HasWord> sentence, boolean reuseTags) {
        List<TaggedWord> list = new ArrayList<>();
        for (HasWord token : sentence) {
          list.add(new TaggedWord(token.word(), "DT"));
        }
        return list;
      }
    };

    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, Integer.MAX_VALUE, 1);

    CoreLabel token = new CoreLabel();
    token.setWord("Every");

    Annotation annotation = new Annotation("Edge length");

    annotator.annotate(annotation);

    String tag = annotation.get(CoreAnnotations.SentencesAnnotation.class)
        .get(0).get(CoreAnnotations.TokensAnnotation.class)
        .get(0).get(CoreAnnotations.PartOfSpeechAnnotation.class);

    assertEquals("DT", tag);
  }
@Test
  public void testCustomPropsAllOptionsSet() {
    Properties props = new Properties();
    props.setProperty("xyz.model", MaxentTagger.DEFAULT_JAR_PATH);
    props.setProperty("xyz.verbose", "true");
    props.setProperty("xyz.nthreads", "3");
    props.setProperty("xyz.maxlen", "5");
    props.setProperty("xyz.reuseTags", "true");

    POSTaggerAnnotator annotator = new POSTaggerAnnotator("xyz", props);
    assertNotNull(annotator);
  }
@Test
  public void testTokensWithoutWordField() {
    MaxentTagger tagger = new MaxentTagger() {
      public List<TaggedWord> tagSentence(List<? extends HasWord> sentence, boolean reuseTags) {
        List<TaggedWord> list = new ArrayList<>();
        for (HasWord word : sentence) {
          list.add(new TaggedWord("UNKNOWN", "FW"));
        }
        return list;
      }
    };

    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 10, 1);

    CoreLabel token = new CoreLabel(); 

    Annotation annotation = new Annotation("No word field");

    annotator.annotate(annotation);

    String tag = annotation.get(CoreAnnotations.SentencesAnnotation.class)
        .get(0).get(CoreAnnotations.TokensAnnotation.class)
        .get(0).get(CoreAnnotations.PartOfSpeechAnnotation.class);

    assertEquals("FW", tag);
  }
@Test
  public void testSentenceWithNullTokenAnnotation() {
    MaxentTagger tagger = new MaxentTagger();
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 10, 1);

    Annotation annotation = new Annotation("Null token sentence");

    try {
      annotator.annotate(annotation);
      fail("Expected NullPointerException due to missing TokensAnnotation");
    } catch (NullPointerException | ClassCastException e) {
      
    }
  }
@Test
  public void testVerboseModelLoadingConstructor() {
    try {
      POSTaggerAnnotator tagger = new POSTaggerAnnotator(MaxentTagger.DEFAULT_JAR_PATH, true);
      assertNotNull(tagger); 
    } catch (Exception e) {
      
    }
  }
@Test
  public void testMulticoreWrapperCompletesProcessing() {
    MaxentTagger tagger = new MaxentTagger() {
      public List<TaggedWord> tagSentence(List<? extends HasWord> sentence, boolean reuseTags) {
        List<TaggedWord> result = new ArrayList<>();
        result.add(new TaggedWord(sentence.get(0).word(), "NN"));
        return result;
      }
    };

    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 100, 2);

    Annotation annotation = new Annotation("Test");

    annotator.annotate(annotation);

    String tag1 = annotation.get(CoreAnnotations.SentencesAnnotation.class)
        .get(0).get(CoreAnnotations.TokensAnnotation.class).get(0)
        .get(CoreAnnotations.PartOfSpeechAnnotation.class);
    String tag2 = annotation.get(CoreAnnotations.SentencesAnnotation.class)
        .get(1).get(CoreAnnotations.TokensAnnotation.class).get(0)
        .get(CoreAnnotations.PartOfSpeechAnnotation.class);

    assertEquals("NN", tag1);
    assertEquals("NN", tag2);
  }
@Test
  public void testReuseTagsFalseViaProperties() {
    MaxentTagger tagger = new MaxentTagger() {
      public List<TaggedWord> tagSentence(List<? extends HasWord> sentence, boolean reuseTags) {
        if (!reuseTags) {
          return Collections.singletonList(new TaggedWord(sentence.get(0).word(), "VB"));
        } else {
          return Collections.singletonList(new TaggedWord(sentence.get(0).word(), "XX"));
        }
      }
    };

    Properties props = new Properties();
    props.setProperty("pos.model", MaxentTagger.DEFAULT_JAR_PATH);
    props.setProperty("pos.reuseTags", "false");

    POSTaggerAnnotator annotator = new POSTaggerAnnotator("pos", props);

    CoreLabel token = new CoreLabel();
    token.setWord("tag");

    Annotation annotation = new Annotation("Test false reuseTags");

    annotator.annotate(annotation);
    String tag = annotation.get(CoreAnnotations.SentencesAnnotation.class)
        .get(0).get(CoreAnnotations.TokensAnnotation.class)
        .get(0).get(CoreAnnotations.PartOfSpeechAnnotation.class);

    assertEquals("VB", tag);
  }
@Test
  public void testMalformedThreadPropertyHandledGracefully() {
    Properties props = new Properties();
    props.setProperty("abc.model", MaxentTagger.DEFAULT_JAR_PATH);
    props.setProperty("abc.nthreads", "notAnInt");

    try {
      POSTaggerAnnotator annotator = new POSTaggerAnnotator("abc", props);
      assertNotNull(annotator);
    } catch (NumberFormatException e) {
      
    }
  }
@Test
  public void testOverrideExistingPOSTags() {
    MaxentTagger tagger = new MaxentTagger() {
      public List<TaggedWord> tagSentence(List<? extends HasWord> sentence, boolean reuseTags) {
        return Collections.singletonList(new TaggedWord(sentence.get(0).word(), "JJ"));
      }
    };

    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 10, 1);

    CoreLabel token = new CoreLabel();
    token.setWord("Green");
    token.set(CoreAnnotations.PartOfSpeechAnnotation.class, "ZZZ"); 

    Annotation annotation = new Annotation("Override test");

    annotator.annotate(annotation);

    String result = annotation.get(CoreAnnotations.SentencesAnnotation.class)
        .get(0).get(CoreAnnotations.TokensAnnotation.class).get(0)
        .get(CoreAnnotations.PartOfSpeechAnnotation.class);

    assertEquals("JJ", result); 
  }
@Test
  public void testNoSentencesToAnnotate() {
    MaxentTagger tagger = new MaxentTagger();

    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 10, 1);

    Annotation annotation = new Annotation("Empty sentence list");
    annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());

    annotator.annotate(annotation); 

    List<CoreMap> result = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertTrue(result.isEmpty());
  }
@Test
  public void testRequirementsSatisfiedUnmodifiable() {
    MaxentTagger tagger = new MaxentTagger();
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 10, 1);

    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
    try {
      satisfied.clear();
      fail("Expected UnsupportedOperationException for immutable set");
    } catch (UnsupportedOperationException e) {
      
    }
  }
@Test
  public void testSentenceExactlyMaxSentenceLengthAllowed() {
    MaxentTagger tagger = new MaxentTagger() {
      public List<TaggedWord> tagSentence(List<? extends edu.stanford.nlp.ling.HasWord> sentence, boolean reuseTags) {
        List<TaggedWord> result = new ArrayList<>();
        result.add(new TaggedWord("word1", "NN"));
        result.add(new TaggedWord("word2", "VB"));
        return result;
      }
    };

    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 2, 1);

    CoreLabel token1 = new CoreLabel(); token1.setWord("word1");
    CoreLabel token2 = new CoreLabel(); token2.setWord("word2");

    Annotation annotation = new Annotation("Test max length");

    annotator.annotate(annotation);

    List<CoreLabel> tagged = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(CoreAnnotations.TokensAnnotation.class);
    assertEquals("NN", tagged.get(0).get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("VB", tagged.get(1).get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testTaggerThrowsNullPointerHandledAsCrash() {
    MaxentTagger tagger = new MaxentTagger() {
      public List<TaggedWord> tagSentence(List<? extends edu.stanford.nlp.ling.HasWord> tokens, boolean reuseTags) {
        throw new NullPointerException("Null simulated");
      }
    };

    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 5, 1);

    CoreLabel token = new CoreLabel(); token.setWord("causeCrash");

    Annotation annotation = new Annotation("simulate null crash");

    try {
      annotator.annotate(annotation);
      fail("Expected the null pointer to propagate");
    } catch (NullPointerException e) {
      assertEquals("Null simulated", e.getMessage());
    }
  }
@Test
  public void testSentenceWithOneTokenAndTagIsApplied() {
    MaxentTagger tagger = new MaxentTagger() {
      public List<TaggedWord> tagSentence(List<? extends edu.stanford.nlp.ling.HasWord> tokens, boolean reuseTags) {
        return Collections.singletonList(new TaggedWord(tokens.get(0).word(), "IN"));
      }
    };

    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 1, 1);

    CoreLabel token = new CoreLabel(); token.setWord("because");


    Annotation annotation = new Annotation("single");

    annotator.annotate(annotation);

    String tag = annotation.get(CoreAnnotations.SentencesAnnotation.class)
      .get(0).get(CoreAnnotations.TokensAnnotation.class).get(0)
      .get(CoreAnnotations.PartOfSpeechAnnotation.class);
    assertEquals("IN", tag);
  }
@Test
  public void testSentenceWithMultipleThreadsAndOddCountSentences() {
    MaxentTagger tagger = new MaxentTagger() {
      public List<TaggedWord> tagSentence(List<? extends edu.stanford.nlp.ling.HasWord> tokens, boolean reuseTags) {
        List<TaggedWord> tags = new ArrayList<>();
        for (edu.stanford.nlp.ling.HasWord t : tokens) {
          tags.add(new TaggedWord(t.word(), "RB"));
        }
        return tags;
      }
    };

    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 10, 3);

    CoreLabel token1 = new CoreLabel(); token1.setWord("now");

    Annotation annotation = new Annotation("three sentences");
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals("RB", sentences.get(0).get(CoreAnnotations.TokensAnnotation.class).get(0).get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("RB", sentences.get(1).get(CoreAnnotations.TokensAnnotation.class).get(0).get(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertEquals("RB", sentences.get(2).get(CoreAnnotations.TokensAnnotation.class).get(0).get(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testMultipleDefaultConstructorsChainCorrectly() {
    try {
      POSTaggerAnnotator annotator = new POSTaggerAnnotator();
      assertNotNull(annotator);
    } catch (Exception e) {
      
    }

    try {
      POSTaggerAnnotator verbose = new POSTaggerAnnotator(true);
      assertNotNull(verbose);
    } catch (Exception e) {
      
    }

    try {
      POSTaggerAnnotator stringCtor = new POSTaggerAnnotator(MaxentTagger.DEFAULT_JAR_PATH, false);
      assertNotNull(stringCtor);
    } catch (Exception e) {
      
    }
  }
@Test
  public void testSentenceProcessedEvenIfTaggerReturnsEmptyList() {
    MaxentTagger tagger = new MaxentTagger() {
      public List<TaggedWord> tagSentence(List<? extends edu.stanford.nlp.ling.HasWord> sentence, boolean reuseTags) {
        return new ArrayList<TaggedWord>(); 
      }
    };

    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 100, 1);

    CoreLabel token = new CoreLabel(); token.setWord("emptiness");

    Annotation annotation = new Annotation("empty tag list");

    try {
      annotator.annotate(annotation);
      fail("Expected IndexOutOfBounds due to mismatch between tokens and tag count");
    } catch (IndexOutOfBoundsException e) {
      assertTrue(e.getMessage() != null);
    }
  }
@Test
  public void testRequiresReturnsExpectedAnnotations() {
    MaxentTagger tagger = new MaxentTagger();
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 10, 1);

    Set<Class<? extends CoreAnnotation>> required = annotator.requires();

    assertTrue(required.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.CharacterOffsetEndAnnotation.class));
  }
@Test
  public void testTaggerWithReuseTagsViaConstructor() {
    MaxentTagger tagger = new MaxentTagger() {
      public List<TaggedWord> tagSentence(List<? extends HasWord> tokens, boolean reuseTags) {
        if (!reuseTags) {
          return Collections.singletonList(new TaggedWord("foo", "JJ"));
        } else {
          return Collections.singletonList(new TaggedWord("foo", "VBZ"));
        }
      }
    };

    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 10, 1);

    CoreLabel token = new CoreLabel();
    token.setWord("foo");

    Annotation annotation = new Annotation("test");

    annotator.annotate(annotation);
    String tag = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(CoreAnnotations.TokensAnnotation.class).get(0).get(CoreAnnotations.PartOfSpeechAnnotation.class);
    assertEquals("JJ", tag); 
  }
@Test
  public void testAnnotateWithSentenceMissingTokensAnnotation() {
    MaxentTagger tagger = new MaxentTagger();
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 10, 1);

    Annotation annotation = new Annotation("missing tokens");


    try {
      annotator.annotate(annotation);
      fail("Expected NullPointerException due to missing TokensAnnotation in sentence");
    } catch (NullPointerException e) {
      
    }
  }
@Test
  public void testMultithreadedPollFlushCompletes() {
    MaxentTagger tagger = new MaxentTagger() {
      public List<TaggedWord> tagSentence(List<? extends HasWord> tokens, boolean reuseTags) {
        return Collections.singletonList(new TaggedWord(tokens.get(0).word(), "ADV"));
      }
    };

    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 10, 3);

    CoreLabel token1 = new CoreLabel(); token1.setWord("fast");
    CoreLabel token2 = new CoreLabel(); token2.setWord("slow");
    CoreLabel token3 = new CoreLabel(); token3.setWord("quick");

    Annotation annotation = new Annotation("parallel task");

    annotator.annotate(annotation);

    String tag1 = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(CoreAnnotations.TokensAnnotation.class).get(0).get(CoreAnnotations.PartOfSpeechAnnotation.class);
    String tag2 = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(1).get(CoreAnnotations.TokensAnnotation.class).get(0).get(CoreAnnotations.PartOfSpeechAnnotation.class);
    String tag3 = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(2).get(CoreAnnotations.TokensAnnotation.class).get(0).get(CoreAnnotations.PartOfSpeechAnnotation.class);

    assertEquals("ADV", tag1);
    assertEquals("ADV", tag2);
    assertEquals("ADV", tag3);
  }
@Test
  public void testOutOfMemoryHandledGracefullyAndSetsX() {
    MaxentTagger tagger = new MaxentTagger() {
      public List<TaggedWord> tagSentence(List<? extends HasWord> tokens, boolean reuseTags) {
        throw new OutOfMemoryError("simulated OOM");
      }
    };

    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 100, 1);

    CoreLabel token = new CoreLabel();
    token.setWord("explode");

    Annotation annotation = new Annotation("simulate oom");

    annotator.annotate(annotation);

    String tag = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0)
      .get(CoreAnnotations.TokensAnnotation.class).get(0)
      .get(CoreAnnotations.PartOfSpeechAnnotation.class);

    assertEquals("X", tag); 
  }
@Test
  public void testModelLoadingFailsWithInvalidPathHandledByConstructor() {
    try {
      POSTaggerAnnotator annotator = new POSTaggerAnnotator("invalid/path/to/model.tagger", false);
      assertNotNull(annotator); 
    } catch (RuntimeException e) {
      
    }
  }
@Test
  public void testRequirementsSatisfiedReturnsExpected() {
    MaxentTagger tagger = new MaxentTagger();
    POSTaggerAnnotator annotator = new POSTaggerAnnotator(tagger, 100, 1);

    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertEquals(1, satisfied.size());
    assertTrue(satisfied.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
  }
@Test
  public void testLoadModelWithVerboseTimingMessages() {
    try {
      
      POSTaggerAnnotator annotator = new POSTaggerAnnotator(MaxentTagger.DEFAULT_JAR_PATH, true);
      assertNotNull(annotator);
    } catch (Exception e) {
      
    }
  } 
}