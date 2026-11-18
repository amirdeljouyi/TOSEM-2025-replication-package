package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.SegmenterCoreAnnotations;
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.CoreMap;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import static org.junit.Assert.*;

public class ChineseSegmenterAnnotatorPromptTest {

  @Test
  public void constructorRequiresNamespacedModelProperty() {
    Properties properties = new Properties();
    properties.setProperty("model", "ignored.ser.gz");

    try {
      new ChineseSegmenterAnnotator("custom", properties);
      fail("A model outside the annotator namespace must not be accepted");
    } catch (RuntimeException expected) {
      assertEquals("Expected a property custom.model", expected.getMessage());
    }
  }

  @Test
  public void constructorReportsMissingModelForEmptyProperties() {
    try {
      new ChineseSegmenterAnnotator("segment", new Properties());
      fail("A model is mandatory");
    } catch (RuntimeException expected) {
      assertEquals("Expected a property segment.model", expected.getMessage());
    }
  }

  @Test
  public void constructorReportsMissingModelForEmptyAnnotatorName() {
    Properties properties = new Properties();
    properties.setProperty("segment.model", "irrelevant");

    try {
      new ChineseSegmenterAnnotator("", properties);
      fail("The requested namespace has no model");
    } catch (RuntimeException expected) {
      assertEquals("Expected a property .model", expected.getMessage());
    }
  }

  @Test
  public void constructorRejectsNonexistentModel() {
    Properties properties = new Properties();
    properties.setProperty("unit.model", "definitely-not-a-real-model-for-this-test.ser.gz");

    try {
      new ChineseSegmenterAnnotator("unit", properties);
      fail("A nonexistent model must not produce an annotator");
    } catch (RuntimeException expected) {
      assertNotNull(expected);
    }
  }

  @Test
  public void constructorIgnoresModelBelongingToAnotherNamespace() {
    Properties properties = new Properties();
    properties.setProperty("first.model", "jar:file:/Users/amirdeljouyi/.m2/repository/edu/stanford/nlp/stanford-corenlp/4.5.7/stanford-corenlp-4.5.7-models.jar!/edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz");
    properties.setProperty("second.verbose", "true");

    try {
      new ChineseSegmenterAnnotator("second", properties);
      fail("Properties from another annotator must be isolated");
    } catch (RuntimeException expected) {
      assertEquals("Expected a property second.model", expected.getMessage());
    }
  }

  @Test
  public void requiresIsEmptyAndImmutable() {
    Properties properties = new Properties();
    properties.setProperty("unit.model", "jar:file:/Users/amirdeljouyi/.m2/repository/edu/stanford/nlp/stanford-corenlp/4.5.7/stanford-corenlp-4.5.7-models.jar!/edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz");
    properties.setProperty("unit.readerAndWriter", "edu.stanford.nlp.wordseg.Sighan2005DocumentReaderAndWriter");
    properties.setProperty("unit.wordShape", "none");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("unit", properties);

    Set<Class<? extends CoreAnnotation>> required = annotator.requires();

    assertTrue(required.isEmpty());
    try {
      required.add(CoreAnnotations.TextAnnotation.class);
      fail("The empty requirement set should be immutable");
    } catch (UnsupportedOperationException expected) {
      assertTrue(required.isEmpty());
    }
  }

  @Test
  public void requirementsSatisfiedContainsCompletePublicContract() {
    Properties properties = new Properties();
    properties.setProperty("unit.model", "jar:file:/Users/amirdeljouyi/.m2/repository/edu/stanford/nlp/stanford-corenlp/4.5.7/stanford-corenlp-4.5.7-models.jar!/edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz");
    properties.setProperty("unit.readerAndWriter", "edu.stanford.nlp.wordseg.Sighan2005DocumentReaderAndWriter");
    properties.setProperty("unit.wordShape", "none");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("unit", properties);

    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertEquals(12, satisfied.size());
    assertTrue(satisfied.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.CharacterOffsetEndAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.BeforeAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.AfterAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.TokenBeginAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.TokenEndAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.PositionAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.IndexAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.OriginalTextAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.ValueAnnotation.class));
  }

  @Test
  public void requirementsSatisfiedReturnsIndependentMutableSets() {
    Properties properties = new Properties();
    properties.setProperty("unit.model", "jar:file:/Users/amirdeljouyi/.m2/repository/edu/stanford/nlp/stanford-corenlp/4.5.7/stanford-corenlp-4.5.7-models.jar!/edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz");
    properties.setProperty("unit.readerAndWriter", "edu.stanford.nlp.wordseg.Sighan2005DocumentReaderAndWriter");
    properties.setProperty("unit.wordShape", "none");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("unit", properties);

    Set<Class<? extends CoreAnnotation>> first = annotator.requirementsSatisfied();
    Set<Class<? extends CoreAnnotation>> second = annotator.requirementsSatisfied();
    first.clear();

    assertNotSame(first, second);
    assertTrue(first.isEmpty());
    assertEquals(12, second.size());
  }

  @Test
  public void annotatePlainTextCreatesCharactersAndTokenOffsets() {
    Properties properties = new Properties();
    properties.setProperty("unit.model", "jar:file:/Users/amirdeljouyi/.m2/repository/edu/stanford/nlp/stanford-corenlp/4.5.7/stanford-corenlp-4.5.7-models.jar!/edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz");
    properties.setProperty("unit.readerAndWriter", "edu.stanford.nlp.wordseg.Sighan2005DocumentReaderAndWriter");
    properties.setProperty("unit.wordShape", "none");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("unit", properties);
    Annotation document = new Annotation("Alpha");

    annotator.annotate(document);

    List<CoreLabel> characters = document.get(SegmenterCoreAnnotations.CharactersAnnotation.class);
    List<CoreLabel> tokens = document.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(5, characters.size());
    assertEquals("A", characters.get(0).get(CoreAnnotations.ChineseCharAnnotation.class));
    assertEquals("1", characters.get(0).get(CoreAnnotations.ChineseSegAnnotation.class));
    assertEquals(Integer.valueOf(0), characters.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertEquals(Integer.valueOf(1), characters.get(0).get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
    assertEquals("a", characters.get(4).get(CoreAnnotations.ChineseCharAnnotation.class));
    assertEquals(Integer.valueOf(4), characters.get(4).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertEquals(Integer.valueOf(5), characters.get(4).get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
    assertEquals(1, tokens.size());
    assertEquals("Alpha", tokens.get(0).word());
    assertEquals("Alpha", tokens.get(0).value());
    assertEquals("Alpha", tokens.get(0).originalText());
    assertEquals(0, tokens.get(0).beginPosition());
    assertEquals(5, tokens.get(0).endPosition());
  }

  @Test
  public void annotateWhitespaceMarksNextCharacterAsBoundaryAndPreservesOffsets() {
    Properties properties = new Properties();
    properties.setProperty("unit.model", "jar:file:/Users/amirdeljouyi/.m2/repository/edu/stanford/nlp/stanford-corenlp/4.5.7/stanford-corenlp-4.5.7-models.jar!/edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz");
    properties.setProperty("unit.readerAndWriter", "edu.stanford.nlp.wordseg.Sighan2005DocumentReaderAndWriter");
    properties.setProperty("unit.wordShape", "none");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("unit", properties);
    Annotation document = new Annotation("A B");

    annotator.annotate(document);

    List<CoreLabel> characters = document.get(SegmenterCoreAnnotations.CharactersAnnotation.class);
    List<CoreLabel> tokens = document.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(2, characters.size());
    assertEquals("A", characters.get(0).get(CoreAnnotations.ChineseCharAnnotation.class));
    assertEquals("B", characters.get(1).get(CoreAnnotations.ChineseCharAnnotation.class));
    assertEquals("1", characters.get(0).get(CoreAnnotations.ChineseSegAnnotation.class));
    assertEquals("1", characters.get(1).get(CoreAnnotations.ChineseSegAnnotation.class));
    assertEquals(Integer.valueOf(2), characters.get(1).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertEquals(1, tokens.size());
    assertEquals("AB", tokens.get(0).word());
    assertEquals(0, tokens.get(0).beginPosition());
    assertEquals(3, tokens.get(0).endPosition());
  }

  @Test
  public void annotateSupplementaryCodePointKeepsOneCharacterAndUtf16Offsets() {
    Properties properties = new Properties();
    properties.setProperty("unit.model", "jar:file:/Users/amirdeljouyi/.m2/repository/edu/stanford/nlp/stanford-corenlp/4.5.7/stanford-corenlp-4.5.7-models.jar!/edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz");
    properties.setProperty("unit.readerAndWriter", "edu.stanford.nlp.wordseg.Sighan2005DocumentReaderAndWriter");
    properties.setProperty("unit.wordShape", "none");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("unit", properties);
    Annotation document = new Annotation("A\uD83D\uDE00B");

    annotator.annotate(document);

    List<CoreLabel> characters = document.get(SegmenterCoreAnnotations.CharactersAnnotation.class);
    assertEquals(3, characters.size());
    assertEquals("A", characters.get(0).get(CoreAnnotations.ChineseCharAnnotation.class));
    assertEquals("\uD83D\uDE00", characters.get(1).get(CoreAnnotations.ChineseCharAnnotation.class));
    assertEquals(Integer.valueOf(1), characters.get(1).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertEquals(Integer.valueOf(3), characters.get(1).get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
    assertEquals("B", characters.get(2).get(CoreAnnotations.ChineseCharAnnotation.class));
    assertEquals(Integer.valueOf(3), characters.get(2).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertEquals(Integer.valueOf(4), characters.get(2).get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
  }

  @Test
  public void annotateRoutesWorkToProvidedSentencesInsteadOfDocumentText() {
    Properties properties = new Properties();
    properties.setProperty("unit.model", "jar:file:/Users/amirdeljouyi/.m2/repository/edu/stanford/nlp/stanford-corenlp/4.5.7/stanford-corenlp-4.5.7-models.jar!/edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz");
    properties.setProperty("unit.readerAndWriter", "edu.stanford.nlp.wordseg.Sighan2005DocumentReaderAndWriter");
    properties.setProperty("unit.wordShape", "none");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("unit", properties);
    Annotation document = new Annotation("ignored");
    CoreMap first = new ArrayCoreMap();
    first.set(CoreAnnotations.TextAnnotation.class, "One");
    CoreMap second = new ArrayCoreMap();
    second.set(CoreAnnotations.TextAnnotation.class, "Two");
    document.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(first, second));

    annotator.annotate(document);

    assertNull(document.get(SegmenterCoreAnnotations.CharactersAnnotation.class));
    assertNull(document.get(CoreAnnotations.TokensAnnotation.class));
    assertEquals("One", first.get(CoreAnnotations.TokensAnnotation.class).get(0).word());
    assertEquals("Two", second.get(CoreAnnotations.TokensAnnotation.class).get(0).word());
    assertEquals(3, first.get(SegmenterCoreAnnotations.CharactersAnnotation.class).size());
    assertEquals(3, second.get(SegmenterCoreAnnotations.CharactersAnnotation.class).size());
  }

  @Test
  public void annotateWithEmptySentenceListLeavesDocumentUntouched() {
    Properties properties = new Properties();
    properties.setProperty("unit.model", "jar:file:/Users/amirdeljouyi/.m2/repository/edu/stanford/nlp/stanford-corenlp/4.5.7/stanford-corenlp-4.5.7-models.jar!/edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz");
    properties.setProperty("unit.readerAndWriter", "edu.stanford.nlp.wordseg.Sighan2005DocumentReaderAndWriter");
    properties.setProperty("unit.wordShape", "none");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("unit", properties);
    Annotation document = new Annotation("ignored");
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.emptyList());

    annotator.annotate(document);

    assertEquals("ignored", document.get(CoreAnnotations.TextAnnotation.class));
    assertNull(document.get(SegmenterCoreAnnotations.CharactersAnnotation.class));
    assertNull(document.get(CoreAnnotations.TokensAnnotation.class));
  }

  @Test
  public void annotateWithoutNewlineTokenizationDropsLineBreakButRetainsSourceOffsets() {
    Properties properties = new Properties();
    properties.setProperty("unit.model", "jar:file:/Users/amirdeljouyi/.m2/repository/edu/stanford/nlp/stanford-corenlp/4.5.7/stanford-corenlp-4.5.7-models.jar!/edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz");
    properties.setProperty("unit.readerAndWriter", "edu.stanford.nlp.wordseg.Sighan2005DocumentReaderAndWriter");
    properties.setProperty("unit.wordShape", "none");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("unit", properties);
    Annotation document = new Annotation("A\nB");

    annotator.annotate(document);

    List<CoreLabel> characters = document.get(SegmenterCoreAnnotations.CharactersAnnotation.class);
    List<CoreLabel> tokens = document.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(2, characters.size());
    assertEquals(Integer.valueOf(0), characters.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertEquals(Integer.valueOf(2), characters.get(1).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertEquals(1, tokens.size());
    assertEquals("AB", tokens.get(0).word());
    assertEquals(0, tokens.get(0).beginPosition());
    assertEquals(3, tokens.get(0).endPosition());
  }

  @Test
  public void annotateWithNewlineSplittingEmitsCoreNlpNewlineToken() {
    Properties properties = new Properties();
    properties.setProperty("unit.model", "jar:file:/Users/amirdeljouyi/.m2/repository/edu/stanford/nlp/stanford-corenlp/4.5.7/stanford-corenlp-4.5.7-models.jar!/edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz");
    properties.setProperty("unit.readerAndWriter", "edu.stanford.nlp.wordseg.Sighan2005DocumentReaderAndWriter");
    properties.setProperty("unit.wordShape", "none");
    properties.setProperty("ssplit.newlineIsSentenceBreak", "always");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("unit", properties);
    Annotation document = new Annotation("A\nB");

    annotator.annotate(document);

    List<CoreLabel> characters = document.get(SegmenterCoreAnnotations.CharactersAnnotation.class);
    List<CoreLabel> tokens = document.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(3, characters.size());
    assertEquals("\n", characters.get(1).get(CoreAnnotations.ChineseCharAnnotation.class));
    assertEquals(3, tokens.size());
    assertEquals("A", tokens.get(0).word());
    assertEquals("*NL*", tokens.get(1).word());
    assertEquals("\n", tokens.get(1).originalText());
    assertEquals(1, tokens.get(1).beginPosition());
    assertEquals(2, tokens.get(1).endPosition());
    assertEquals("B", tokens.get(2).word());
  }

  @Test
  public void annotateTwoNewlineModeDropsSingleNewline() {
    Properties properties = new Properties();
    properties.setProperty("unit.model", "jar:file:/Users/amirdeljouyi/.m2/repository/edu/stanford/nlp/stanford-corenlp/4.5.7/stanford-corenlp-4.5.7-models.jar!/edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz");
    properties.setProperty("unit.readerAndWriter", "edu.stanford.nlp.wordseg.Sighan2005DocumentReaderAndWriter");
    properties.setProperty("unit.wordShape", "none");
    properties.setProperty("ssplit.newlineIsSentenceBreak", "two");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("unit", properties);
    Annotation document = new Annotation("A\nB");

    annotator.annotate(document);

    List<CoreLabel> characters = document.get(SegmenterCoreAnnotations.CharactersAnnotation.class);
    List<CoreLabel> tokens = document.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(2, characters.size());
    assertEquals("A", characters.get(0).get(CoreAnnotations.ChineseCharAnnotation.class));
    assertEquals("B", characters.get(1).get(CoreAnnotations.ChineseCharAnnotation.class));
    assertEquals(1, tokens.size());
    assertEquals("AB", tokens.get(0).word());
    assertEquals(3, tokens.get(0).endPosition());
  }

  @Test
  public void annotateAlwaysDropsLeadingAndTrailingNewlines() {
    Properties properties = new Properties();
    properties.setProperty("unit.model", "jar:file:/Users/amirdeljouyi/.m2/repository/edu/stanford/nlp/stanford-corenlp/4.5.7/stanford-corenlp-4.5.7-models.jar!/edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz");
    properties.setProperty("unit.readerAndWriter", "edu.stanford.nlp.wordseg.Sighan2005DocumentReaderAndWriter");
    properties.setProperty("unit.wordShape", "none");
    properties.setProperty("ssplit.eolonly", "true");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("unit", properties);
    Annotation document = new Annotation("\nA\r\n");

    annotator.annotate(document);

    List<CoreLabel> characters = document.get(SegmenterCoreAnnotations.CharactersAnnotation.class);
    List<CoreLabel> tokens = document.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(1, characters.size());
    assertEquals("A", characters.get(0).get(CoreAnnotations.ChineseCharAnnotation.class));
    assertEquals(Integer.valueOf(1), characters.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertEquals(1, tokens.size());
    assertEquals("A", tokens.get(0).word());
    assertEquals(1, tokens.get(0).beginPosition());
    assertEquals(2, tokens.get(0).endPosition());
  }

  @Test
  public void annotateXmlCombinesTagAndMarksItsCharacters() {
    Properties properties = new Properties();
    properties.setProperty("unit.model", "jar:file:/Users/amirdeljouyi/.m2/repository/edu/stanford/nlp/stanford-corenlp/4.5.7/stanford-corenlp-4.5.7-models.jar!/edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz");
    properties.setProperty("unit.readerAndWriter", "edu.stanford.nlp.wordseg.Sighan2005DocumentReaderAndWriter");
    properties.setProperty("unit.wordShape", "none");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("unit", properties);
    Annotation document = new Annotation("<p>A</p>");

    annotator.annotate(document);

    List<CoreLabel> characters = document.get(SegmenterCoreAnnotations.CharactersAnnotation.class);
    List<CoreLabel> tokens = document.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(8, characters.size());
    assertEquals("beginning", characters.get(0).get(SegmenterCoreAnnotations.XMLCharAnnotation.class));
    assertEquals("1", characters.get(1).get(SegmenterCoreAnnotations.XMLCharAnnotation.class));
    assertEquals("0", characters.get(3).get(SegmenterCoreAnnotations.XMLCharAnnotation.class));
    assertEquals("beginning", characters.get(4).get(SegmenterCoreAnnotations.XMLCharAnnotation.class));
    assertEquals(1, tokens.size());
    assertEquals("<p>A</p>", tokens.get(0).word());
    assertEquals(0, tokens.get(0).beginPosition());
    assertEquals(8, tokens.get(0).endPosition());
  }

  @Test
  public void normalizeSpaceDoesNotChangeOrdinaryTextTokens() {
    Properties properties = new Properties();
    properties.setProperty("unit.model", "jar:file:/Users/amirdeljouyi/.m2/repository/edu/stanford/nlp/stanford-corenlp/4.5.7/stanford-corenlp-4.5.7-models.jar!/edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz");
    properties.setProperty("unit.readerAndWriter", "edu.stanford.nlp.wordseg.Sighan2005DocumentReaderAndWriter");
    properties.setProperty("unit.wordShape", "none");
    properties.setProperty("unit.normalizeSpace", "true");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("unit", properties);
    Annotation document = new Annotation("A B");

    annotator.annotate(document);

    List<CoreLabel> tokens = document.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(1, tokens.size());
    assertEquals("AB", tokens.get(0).word());
    assertEquals("AB", tokens.get(0).originalText());
    assertEquals(0, tokens.get(0).beginPosition());
    assertEquals(3, tokens.get(0).endPosition());
  }

  @Test
  public void annotateNullSentenceListFallsBackToDocument() {
    Properties properties = new Properties();
    properties.setProperty("unit.model", "jar:file:/Users/amirdeljouyi/.m2/repository/edu/stanford/nlp/stanford-corenlp/4.5.7/stanford-corenlp-4.5.7-models.jar!/edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz");
    properties.setProperty("unit.readerAndWriter", "edu.stanford.nlp.wordseg.Sighan2005DocumentReaderAndWriter");
    properties.setProperty("unit.wordShape", "none");
    ChineseSegmenterAnnotator annotator = new ChineseSegmenterAnnotator("unit", properties);
    Annotation document = new Annotation("Fallback");
    document.set(CoreAnnotations.SentencesAnnotation.class, null);

    annotator.annotate(document);

    assertEquals(8, document.get(SegmenterCoreAnnotations.CharactersAnnotation.class).size());
    assertEquals(1, document.get(CoreAnnotations.TokensAnnotation.class).size());
    assertEquals("Fallback", document.get(CoreAnnotations.TokensAnnotation.class).get(0).word());
  }
}
