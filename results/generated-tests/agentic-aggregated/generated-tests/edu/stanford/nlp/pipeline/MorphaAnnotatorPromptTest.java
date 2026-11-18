package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.CoreMap;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MorphaAnnotatorPromptTest {

  @Test
  public void annotatesRegularVerbAndPluralNounInOneSentence() {
    CoreLabel verb = new CoreLabel();
    verb.setWord("running");
    verb.setTag("VBG");
    CoreLabel noun = new CoreLabel();
    noun.setWord("mice");
    noun.setTag("NNS");
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(verb, noun));
    Annotation document = new Annotation(Collections.singletonList(sentence));

    new MorphaAnnotator(false).annotate(document);

    assertEquals("run", verb.lemma());
    assertEquals("mouse", noun.lemma());
  }

  @Test
  public void annotatesIrregularPastTenseAndComparativeAdjective() {
    CoreLabel verb = new CoreLabel();
    verb.setWord("went");
    verb.setTag("VBD");
    CoreLabel adjective = new CoreLabel();
    adjective.setWord("better");
    adjective.setTag("JJR");
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(verb, adjective));
    Annotation document = new Annotation(Collections.singletonList(sentence));

    new MorphaAnnotator().annotate(document);

    assertEquals("go", verb.lemma());
    assertEquals("good", adjective.lemma());
  }

  @Test
  public void emptyPartOfSpeechUsesUnannotatedStemmer() {
    CoreLabel token = new CoreLabel();
    token.setWord("running");
    token.setTag("");
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    Annotation document = new Annotation(Collections.singletonList(sentence));

    new MorphaAnnotator(false).annotate(document);

    assertEquals("run", token.lemma());
  }

  @Test
  public void lemmatizesRecognizedPhrasalVerbAndPreservesParticle() {
    CoreLabel token = new CoreLabel();
    token.setWord("running_up");
    token.setTag("VBG");
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    Annotation document = new Annotation(Collections.singletonList(sentence));

    new MorphaAnnotator(false).annotate(document);

    assertEquals("run_up", token.lemma());
  }

  @Test
  public void lemmatizesIrregularPhrasalVerb() {
    CoreLabel token = new CoreLabel();
    token.setWord("went_out");
    token.setTag("VBD");
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    Annotation document = new Annotation(Collections.singletonList(sentence));

    new MorphaAnnotator(false).annotate(document);

    assertEquals("go_out", token.lemma());
  }

  @Test
  public void nonParticleSuffixIsHandledByOrdinaryMorphology() {
    CoreLabel token = new CoreLabel();
    token.setWord("running_sideways");
    token.setTag("VBG");
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    Annotation document = new Annotation(Collections.singletonList(sentence));

    new MorphaAnnotator(false).annotate(document);

    assertEquals("running_sideways", token.lemma());
  }

  @Test
  public void threePartUnderscoreVerbIsNotTreatedAsPhrasalVerb() {
    CoreLabel token = new CoreLabel();
    token.setWord("running_up_again");
    token.setTag("VBG");
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    Annotation document = new Annotation(Collections.singletonList(sentence));

    new MorphaAnnotator(false).annotate(document);

    assertEquals("running_up_again", token.lemma());
  }

  @Test
  public void underscoreNounDoesNotEnterPhrasalVerbPath() {
    CoreLabel token = new CoreLabel();
    token.setWord("data_bases");
    token.setTag("NNS");
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    Annotation document = new Annotation(Collections.singletonList(sentence));

    new MorphaAnnotator(false).annotate(document);

    assertEquals("data_bases", token.lemma());
  }

  @Test
  public void annotatesEverySentenceAndRetainsTokenIdentity() {
    CoreLabel first = new CoreLabel();
    first.setWord("children");
    first.setTag("NNS");
    CoreLabel second = new CoreLabel();
    second.setWord("ate");
    second.setTag("VBD");
    CoreMap firstSentence = new ArrayCoreMap();
    firstSentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(first));
    CoreMap secondSentence = new ArrayCoreMap();
    secondSentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(second));
    Annotation document = new Annotation(Arrays.asList(firstSentence, secondSentence));

    new MorphaAnnotator(false).annotate(document);

    assertEquals("child", first.lemma());
    assertEquals("eat", second.lemma());
    assertSame(first, document.get(CoreAnnotations.SentencesAnnotation.class).get(0)
        .get(CoreAnnotations.TokensAnnotation.class).get(0));
    assertSame(second, document.get(CoreAnnotations.SentencesAnnotation.class).get(1)
        .get(CoreAnnotations.TokensAnnotation.class).get(0));
  }

  @Test
  public void annotationPipelineCanInvokeAnnotatorAndMutateOriginalToken() {
    CoreLabel token = new CoreLabel();
    token.setWord("geese");
    token.setTag("NNS");
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TextAnnotation.class, "geese");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    Annotation document = new Annotation(Collections.singletonList(sentence));
    AnnotationPipeline pipeline = new AnnotationPipeline();
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    pipeline.addAnnotator(annotator);

    pipeline.annotate(document);

    assertEquals("goose", token.lemma());
    assertEquals("geese", document.toString());
  }

  @Test
  public void secondAnnotationPassReplacesStaleLemma() {
    CoreLabel token = new CoreLabel();
    token.setWord("was");
    token.setTag("VBD");
    token.setLemma("incorrect");
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    Annotation document = new Annotation(Collections.singletonList(sentence));
    MorphaAnnotator annotator = new MorphaAnnotator(false);

    annotator.annotate(document);
    assertEquals("be", token.lemma());
    token.setWord("cars");
    token.setTag("NNS");
    token.setLemma("also-incorrect");
    annotator.annotate(document);

    assertEquals("car", token.lemma());
  }

  @Test
  public void emptySentenceListIsAcceptedWithoutAddingTokens() {
    Annotation document = new Annotation(Collections.<CoreMap>emptyList());

    new MorphaAnnotator(false).annotate(document);

    assertTrue(document.get(CoreAnnotations.SentencesAnnotation.class).isEmpty());
    assertTrue(document.get(CoreAnnotations.TokensAnnotation.class).isEmpty());
  }

  @Test
  public void missingSentencesProducesDescriptiveFailure() {
    Annotation document = new Annotation("untokenized input");

    try {
      new MorphaAnnotator(false).annotate(document);
      fail("Expected an exception for an annotation without sentences");
    } catch (RuntimeException expected) {
      assertTrue(expected.getMessage().startsWith("Unable to find words/tokens in:"));
      assertTrue(expected.getMessage().contains("untokenized input"));
    }
  }

  @Test
  public void sentenceWithoutTokensFailsFast() {
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TextAnnotation.class, "missing tokens");
    Annotation document = new Annotation("missing tokens");
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    try {
      new MorphaAnnotator(false).annotate(document);
      fail("Expected missing token data to fail");
    } catch (NullPointerException expected) {
      assertNull(sentence.get(CoreAnnotations.TokensAnnotation.class));
    }
  }

  @Test
  public void tokenWithoutPartOfSpeechFailsFastAndGetsNoLemma() {
    CoreLabel token = new CoreLabel();
    token.setWord("walking");
    CoreMap sentence = new ArrayCoreMap();
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    Annotation document = new Annotation(Collections.singletonList(sentence));

    try {
      new MorphaAnnotator(false).annotate(document);
      fail("Expected missing part of speech to fail");
    } catch (NullPointerException expected) {
      assertNull(token.lemma());
    }
  }

  @Test
  public void requiresReturnsAllFourInputsAndIsUnmodifiable() {
    Set<Class<? extends CoreAnnotation>> requirements = new MorphaAnnotator(false).requires();

    assertEquals(4, requirements.size());
    assertTrue(requirements.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.SentencesAnnotation.class));
    assertTrue(requirements.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertFalse(requirements.contains(CoreAnnotations.LemmaAnnotation.class));
    try {
      requirements.add(CoreAnnotations.LemmaAnnotation.class);
      fail("Requirements must be immutable");
    } catch (UnsupportedOperationException expected) {
      assertEquals(4, requirements.size());
    }
  }

  @Test
  public void requirementsSatisfiedContainsOnlyLemmaAndIsImmutable() {
    MorphaAnnotator annotator = new MorphaAnnotator(false);
    Set<Class<? extends CoreAnnotation>> first = annotator.requirementsSatisfied();
    Set<Class<? extends CoreAnnotation>> second = annotator.requirementsSatisfied();

    assertEquals(Collections.singleton(CoreAnnotations.LemmaAnnotation.class), first);
    assertEquals(first, second);
    assertNotSame(first, annotator.requires());
    try {
      first.clear();
      fail("Satisfied requirements must be immutable");
    } catch (UnsupportedOperationException expected) {
      assertEquals(1, first.size());
    }
  }
}
