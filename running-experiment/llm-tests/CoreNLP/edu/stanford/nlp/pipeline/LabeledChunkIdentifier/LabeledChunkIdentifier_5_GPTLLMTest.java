package edu.stanford.nlp.pipeline;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Pair;
import java.util.*;
import java.util.function.Predicate;
import org.junit.Test;

public class LabeledChunkIdentifier_5_GPTLLMTest {

  @Test
  public void testGetTagTypeWithDash() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    LabeledChunkIdentifier.LabelTagType tagType = chunkIdentifier.getTagType("B-ORG");
    assertEquals("B-ORG", tagType.label);
    assertEquals("B", tagType.tag);
    assertEquals("ORG", tagType.type);
  }

  @Test
  public void testGetTagTypeWithoutDash() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    LabeledChunkIdentifier.LabelTagType tagType = chunkIdentifier.getTagType("ORG");
    assertEquals("ORG", tagType.label);
    assertEquals("I", tagType.tag);
    assertEquals("ORG", tagType.type);
  }

  @Test
  public void testGetTagTypeWithIgnoreProvidedTag() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    chunkIdentifier.setIgnoreProvidedTag(true);
    LabeledChunkIdentifier.LabelTagType tagType = chunkIdentifier.getTagType("B-LOC");
    assertEquals("B-LOC", tagType.label);
    assertEquals("I", tagType.tag);
    assertEquals("LOC", tagType.type);
  }

  @Test
  public void testIsStartOfChunkTrueWhenPreviousNull() {
    LabeledChunkIdentifier.LabelTagType cur =
        new LabeledChunkIdentifier.LabelTagType("B-ORG", "B", "ORG");
    boolean isStart = LabeledChunkIdentifier.isStartOfChunk(null, cur);
    assertTrue(isStart);
  }

  @Test
  public void testIsEndOfChunkTrueWhenCurIsO() {
    LabeledChunkIdentifier.LabelTagType prev =
        new LabeledChunkIdentifier.LabelTagType("I-ORG", "I", "ORG");
    LabeledChunkIdentifier.LabelTagType cur =
        new LabeledChunkIdentifier.LabelTagType("O", "O", "O");
    boolean isEnd = LabeledChunkIdentifier.isEndOfChunk(prev, cur);
    assertTrue(isEnd);
  }

  @Test
  public void testGetAnnotatedChunksWithOneChunk() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    CoreLabel tok1 = new CoreLabel();
    tok1.set(CoreAnnotations.TextAnnotation.class, "John");
    tok1.set(CoreAnnotations.AnswerAnnotation.class, "B-PER");
    tok1.set(CoreAnnotations.IndexAnnotation.class, 0);
    tok1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    tok1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    CoreLabel tok2 = new CoreLabel();
    tok2.set(CoreAnnotations.TextAnnotation.class, "Smith");
    tok2.set(CoreAnnotations.AnswerAnnotation.class, "I-PER");
    tok2.set(CoreAnnotations.IndexAnnotation.class, 1);
    tok2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    tok2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);
    CoreLabel tok3 = new CoreLabel();
    tok3.set(CoreAnnotations.TextAnnotation.class, ".");
    tok3.set(CoreAnnotations.AnswerAnnotation.class, "O");
    tok3.set(CoreAnnotations.IndexAnnotation.class, 2);
    tok3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 11);
    tok3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(tok1);
    tokens.add(tok2);
    tokens.add(tok3);
    List<CoreMap> chunks =
        chunkIdentifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, chunks.size());
    CoreMap chunk = chunks.get(0);
    assertEquals(0, chunk.get(CoreAnnotations.TokenBeginAnnotation.class).intValue());
    assertEquals(2, chunk.get(CoreAnnotations.TokenEndAnnotation.class).intValue());
    assertEquals("PER", chunk.get(CoreAnnotations.AnswerAnnotation.class));
  }

  @Test
  public void testGetAnnotatedChunksWithBackToBackChunks() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    CoreLabel tok1 = new CoreLabel();
    tok1.set(CoreAnnotations.TextAnnotation.class, "Alice");
    tok1.set(CoreAnnotations.AnswerAnnotation.class, "B-PER");
    tok1.set(CoreAnnotations.IndexAnnotation.class, 0);
    tok1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    tok1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    CoreLabel tok2 = new CoreLabel();
    tok2.set(CoreAnnotations.TextAnnotation.class, "Smith");
    tok2.set(CoreAnnotations.AnswerAnnotation.class, "I-PER");
    tok2.set(CoreAnnotations.IndexAnnotation.class, 1);
    tok2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    tok2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);
    CoreLabel tok3 = new CoreLabel();
    tok3.set(CoreAnnotations.TextAnnotation.class, "Google");
    tok3.set(CoreAnnotations.AnswerAnnotation.class, "B-ORG");
    tok3.set(CoreAnnotations.IndexAnnotation.class, 2);
    tok3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 12);
    tok3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 18);
    CoreLabel tok4 = new CoreLabel();
    tok4.set(CoreAnnotations.TextAnnotation.class, "Inc");
    tok4.set(CoreAnnotations.AnswerAnnotation.class, "I-ORG");
    tok4.set(CoreAnnotations.IndexAnnotation.class, 3);
    tok4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 19);
    tok4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 22);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(tok1);
    tokens.add(tok2);
    tokens.add(tok3);
    tokens.add(tok4);
    List<CoreMap> chunks =
        chunkIdentifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(2, chunks.size());
    assertEquals("PER", chunks.get(0).get(CoreAnnotations.AnswerAnnotation.class));
    assertEquals("ORG", chunks.get(1).get(CoreAnnotations.AnswerAnnotation.class));
  }

  @Test(expected = RuntimeException.class)
  public void testGetAnnotatedChunksWithMalformedInput_ThrowsException() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    CoreLabel tok1 = new CoreLabel();
    tok1.set(CoreAnnotations.TextAnnotation.class, "Alice");
    tok1.set(CoreAnnotations.AnswerAnnotation.class, "B-PER");
    tok1.set(CoreAnnotations.IndexAnnotation.class, 0);
    tok1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    tok1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    CoreLabel tok2 = new CoreLabel();
    tok2.set(CoreAnnotations.TextAnnotation.class, "Bob");
    tok2.set(CoreAnnotations.AnswerAnnotation.class, "B-ORG");
    tok2.set(CoreAnnotations.IndexAnnotation.class, 1);
    tok2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    tok2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(tok1);
    tokens.add(tok2);
    chunkIdentifier.getAnnotatedChunks(
        tokens, 0, CoreAnnotations.TextAnnotation.class, CoreAnnotations.AnswerAnnotation.class);
  }

  @Test
  public void testGetAnnotatedChunksWithPredicateIncompatible() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    CoreLabel tok1 = new CoreLabel();
    tok1.set(CoreAnnotations.TextAnnotation.class, "John");
    tok1.set(CoreAnnotations.AnswerAnnotation.class, "B-PER");
    tok1.set(CoreAnnotations.IndexAnnotation.class, 0);
    tok1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    tok1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    CoreLabel tok2 = new CoreLabel();
    tok2.set(CoreAnnotations.TextAnnotation.class, "NotCompatible");
    tok2.set(CoreAnnotations.AnswerAnnotation.class, "I-PER");
    tok2.set(CoreAnnotations.IndexAnnotation.class, 1);
    tok2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    tok2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 18);
    CoreLabel tok3 = new CoreLabel();
    tok3.set(CoreAnnotations.TextAnnotation.class, "Smith");
    tok3.set(CoreAnnotations.AnswerAnnotation.class, "I-PER");
    tok3.set(CoreAnnotations.IndexAnnotation.class, 2);
    tok3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 19);
    tok3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 24);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(tok1);
    tokens.add(tok2);
    tokens.add(tok3);
    Predicate<Pair<CoreLabel, CoreLabel>> incompatiblePredicate =
        pair -> {
          CoreLabel current = pair.first();
          CoreLabel previous = pair.second();
          if (previous == null) return true;
          return !current.word().equalsIgnoreCase("NotCompatible");
        };
    List<CoreMap> chunks =
        chunkIdentifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class,
            incompatiblePredicate);
    assertEquals(2, chunks.size());
    assertEquals("PER", chunks.get(0).get(CoreAnnotations.AnswerAnnotation.class));
    assertEquals("PER", chunks.get(1).get(CoreAnnotations.AnswerAnnotation.class));
  }

  @Test
  public void testGetAnnotatedChunksReturnsEmptyOnEmptyInput() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    List<CoreLabel> tokens = Collections.emptyList();
    List<CoreMap> chunks =
        chunkIdentifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertTrue(chunks.isEmpty());
  }

  @Test
  public void testGettersAndSetters_WorkCorrectly() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    chunkIdentifier.setDefaultNegTag("NEG");
    chunkIdentifier.setDefaultPosTag("POS");
    chunkIdentifier.setNegLabel("OUTSIDE");
    chunkIdentifier.setIgnoreProvidedTag(true);
    assertEquals("NEG", chunkIdentifier.getDefaultNegTag());
    assertEquals("POS", chunkIdentifier.getDefaultPosTag());
    assertEquals("OUTSIDE", chunkIdentifier.getNegLabel());
    assertTrue(chunkIdentifier.isIgnoreProvidedTag());
  }

  @Test
  public void testGetTagTypeWithNullLabel() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    LabeledChunkIdentifier.LabelTagType tagType = chunkIdentifier.getTagType(null);
    assertNotNull(tagType);
    assertEquals("O", tagType.type);
    assertEquals("O", tagType.tag);
    assertEquals("O", tagType.label);
  }

  @Test
  public void testGetTagTypeWithNoDashAndNotNegLabel() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    chunkIdentifier.setDefaultPosTag("Z");
    LabeledChunkIdentifier.LabelTagType tagType = chunkIdentifier.getTagType("ENTITY");
    assertNotNull(tagType);
    assertEquals("ENTITY", tagType.label);
    assertEquals("Z", tagType.tag);
    assertEquals("ENTITY", tagType.type);
  }

  @Test
  public void testGetTagTypeWithMalformedLabelDoesNotMatchPattern() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    chunkIdentifier.setNegLabel("O");
    chunkIdentifier.setDefaultPosTag("I");
    LabeledChunkIdentifier.LabelTagType tagType = chunkIdentifier.getTagType("SomethingWrong");
    assertNotNull(tagType);
    assertEquals("SomethingWrong", tagType.label);
    assertEquals("I", tagType.tag);
    assertEquals("SomethingWrong", tagType.type);
  }

  @Test
  public void testIsEndOfChunkWhenTypeMismatchTriggersTrue() {
    LabeledChunkIdentifier.LabelTagType prev =
        new LabeledChunkIdentifier.LabelTagType("I-ORG", "I", "ORG");
    LabeledChunkIdentifier.LabelTagType cur =
        new LabeledChunkIdentifier.LabelTagType("I-PER", "I", "PER");
    boolean result = LabeledChunkIdentifier.isEndOfChunk(prev, cur);
    assertTrue(result);
  }

  @Test
  public void testIsStartOfChunkWhenTypeMismatchTriggersTrue() {
    LabeledChunkIdentifier.LabelTagType prev =
        new LabeledChunkIdentifier.LabelTagType("I-LOC", "I", "LOC");
    LabeledChunkIdentifier.LabelTagType cur =
        new LabeledChunkIdentifier.LabelTagType("I-ORG", "I", "ORG");
    boolean result = LabeledChunkIdentifier.isStartOfChunk(prev, cur);
    assertTrue(result);
  }

  @Test
  public void testIsChunkReturnsFalseForTagO() {
    LabeledChunkIdentifier.LabelTagType tagType =
        new LabeledChunkIdentifier.LabelTagType("O", "O", "O");
    assertFalse(tagType.typeMatches(new LabeledChunkIdentifier.LabelTagType("PER", "B", "PER")));
  }

  @Test
  public void testSingleTokenChunkWithSFormat() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.AnswerAnnotation.class, "S-PER");
    token.set(CoreAnnotations.TextAnnotation.class, "Obama");
    token.set(CoreAnnotations.IndexAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    List<CoreMap> chunks =
        chunkIdentifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, chunks.size());
    CoreMap chunk = chunks.get(0);
    assertEquals("PER", chunk.get(CoreAnnotations.AnswerAnnotation.class));
    assertEquals("Obama", chunk.get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testChunkWithUandLTags() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    CoreLabel tok1 = new CoreLabel();
    tok1.set(CoreAnnotations.TextAnnotation.class, "IBM");
    tok1.set(CoreAnnotations.AnswerAnnotation.class, "U-ORG");
    tok1.set(CoreAnnotations.IndexAnnotation.class, 0);
    tok1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    tok1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);
    CoreLabel tok2 = new CoreLabel();
    tok2.set(CoreAnnotations.TextAnnotation.class, "launches");
    tok2.set(CoreAnnotations.AnswerAnnotation.class, "O");
    tok2.set(CoreAnnotations.IndexAnnotation.class, 1);
    tok2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 4);
    tok2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);
    CoreLabel tok3 = new CoreLabel();
    tok3.set(CoreAnnotations.TextAnnotation.class, "OpenAI");
    tok3.set(CoreAnnotations.AnswerAnnotation.class, "B-ORG");
    tok3.set(CoreAnnotations.IndexAnnotation.class, 2);
    tok3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 13);
    tok3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 19);
    CoreLabel tok4 = new CoreLabel();
    tok4.set(CoreAnnotations.TextAnnotation.class, "model");
    tok4.set(CoreAnnotations.AnswerAnnotation.class, "L-ORG");
    tok4.set(CoreAnnotations.IndexAnnotation.class, 3);
    tok4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 20);
    tok4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 25);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(tok1);
    tokens.add(tok2);
    tokens.add(tok3);
    tokens.add(tok4);
    List<CoreMap> chunks =
        chunkIdentifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(2, chunks.size());
    assertEquals("ORG", chunks.get(0).get(CoreAnnotations.AnswerAnnotation.class));
    assertEquals("ORG", chunks.get(1).get(CoreAnnotations.AnswerAnnotation.class));
  }

  @Test
  public void testConsecutiveSingleChunksDifferingTypes() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    CoreLabel tok1 = new CoreLabel();
    tok1.set(CoreAnnotations.TextAnnotation.class, "John");
    tok1.set(CoreAnnotations.AnswerAnnotation.class, "U-PER");
    tok1.set(CoreAnnotations.IndexAnnotation.class, 0);
    tok1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    tok1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    CoreLabel tok2 = new CoreLabel();
    tok2.set(CoreAnnotations.TextAnnotation.class, "Apple");
    tok2.set(CoreAnnotations.AnswerAnnotation.class, "U-ORG");
    tok2.set(CoreAnnotations.IndexAnnotation.class, 1);
    tok2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    tok2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(tok1);
    tokens.add(tok2);
    List<CoreMap> chunks =
        chunkIdentifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(2, chunks.size());
    assertEquals("PER", chunks.get(0).get(CoreAnnotations.AnswerAnnotation.class));
    assertEquals("ORG", chunks.get(1).get(CoreAnnotations.AnswerAnnotation.class));
  }

  @Test
  public void testIsEndOfChunkWhenPrevIsNullReturnsFalse() {
    LabeledChunkIdentifier.LabelTagType prev = null;
    LabeledChunkIdentifier.LabelTagType cur =
        new LabeledChunkIdentifier.LabelTagType("B-PER", "B", "PER");
    boolean result = LabeledChunkIdentifier.isEndOfChunk(prev, cur);
    assertFalse(result);
  }

  @Test
  public void testIsStartOfChunkWithBracketTags() {
    LabeledChunkIdentifier.LabelTagType prev =
        new LabeledChunkIdentifier.LabelTagType("O", "O", "O");
    LabeledChunkIdentifier.LabelTagType cur =
        new LabeledChunkIdentifier.LabelTagType("[-LOC", "[", "LOC");
    boolean result = LabeledChunkIdentifier.isStartOfChunk(prev, cur);
    assertTrue(result);
  }

  @Test
  public void testGetTagTypeWithDashButEmptyTagAndType() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    LabeledChunkIdentifier.LabelTagType tagType = chunkIdentifier.getTagType("-");
    assertEquals("-", tagType.label);
    assertEquals("", tagType.tag);
    assertEquals("", tagType.type);
  }

  @Test
  public void testGetTagTypeWithOnlyDashPrefixFormat() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    LabeledChunkIdentifier.LabelTagType tagType = chunkIdentifier.getTagType("-LOC");
    assertEquals("-LOC", tagType.label);
    assertEquals("", tagType.tag);
    assertEquals("LOC", tagType.type);
  }

  @Test
  public void testGetAnnotatedChunksWithEmptyTypeButTagPresent() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    CoreLabel tok1 = new CoreLabel();
    tok1.set(CoreAnnotations.TextAnnotation.class, "test");
    tok1.set(CoreAnnotations.AnswerAnnotation.class, "B-");
    tok1.set(CoreAnnotations.IndexAnnotation.class, 0);
    tok1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    tok1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(tok1);
    List<CoreMap> chunks =
        chunkIdentifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, chunks.size());
    CoreMap chunk = chunks.get(0);
    assertEquals("", chunk.get(CoreAnnotations.AnswerAnnotation.class));
    assertEquals("test", chunk.get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testGetTagTypeFallbackToDefaultNegTag() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    chunkIdentifier.setNegLabel("NEGVAL");
    chunkIdentifier.setDefaultNegTag("Z");
    LabeledChunkIdentifier.LabelTagType tagType = chunkIdentifier.getTagType("NEGVAL");
    assertEquals("NEGVAL", tagType.label);
    assertEquals("Z", tagType.tag);
    assertEquals("NEGVAL", tagType.type);
  }

  @Test
  public void testGetAnnotatedChunksWhenLastTokenStillOpenChunk() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    CoreLabel tok1 = new CoreLabel();
    tok1.set(CoreAnnotations.TextAnnotation.class, "New");
    tok1.set(CoreAnnotations.AnswerAnnotation.class, "B-LOC");
    tok1.set(CoreAnnotations.IndexAnnotation.class, 0);
    tok1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    tok1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);
    CoreLabel tok2 = new CoreLabel();
    tok2.set(CoreAnnotations.TextAnnotation.class, "York");
    tok2.set(CoreAnnotations.AnswerAnnotation.class, "I-LOC");
    tok2.set(CoreAnnotations.IndexAnnotation.class, 1);
    tok2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 4);
    tok2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);
    CoreLabel tok3 = new CoreLabel();
    tok3.set(CoreAnnotations.TextAnnotation.class, "City");
    tok3.set(CoreAnnotations.AnswerAnnotation.class, "I-LOC");
    tok3.set(CoreAnnotations.IndexAnnotation.class, 2);
    tok3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 9);
    tok3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 13);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(tok1);
    tokens.add(tok2);
    tokens.add(tok3);
    List<CoreMap> chunks =
        chunkIdentifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, chunks.size());
    CoreMap chunk = chunks.get(0);
    assertEquals("LOC", chunk.get(CoreAnnotations.AnswerAnnotation.class));
    assertEquals(0, chunk.get(CoreAnnotations.TokenBeginAnnotation.class).intValue());
    assertEquals(3, chunk.get(CoreAnnotations.TokenEndAnnotation.class).intValue());
    assertEquals("New York City", chunk.get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testDefaultTagsUsedWhenIgnoreProvidedTrueAndMalformedLabel() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    chunkIdentifier.setIgnoreProvidedTag(true);
    chunkIdentifier.setDefaultPosTag("POSITIVE");
    chunkIdentifier.setDefaultNegTag("NEGATIVE");
    chunkIdentifier.setNegLabel("XYZ");
    LabeledChunkIdentifier.LabelTagType tagType = chunkIdentifier.getTagType("AAA-BBB");
    assertEquals("AAA-BBB", tagType.label);
    assertEquals("POSITIVE", tagType.tag);
    assertEquals("BBB", tagType.type);
  }

  @Test
  public void testStartAndEndOfChunkMatchBracketTags() {
    LabeledChunkIdentifier.LabelTagType prev =
        new LabeledChunkIdentifier.LabelTagType("[-ORG", "[", "ORG");
    LabeledChunkIdentifier.LabelTagType cur =
        new LabeledChunkIdentifier.LabelTagType("]-ORG", "]", "ORG");
    boolean isEnd = LabeledChunkIdentifier.isEndOfChunk(prev, cur);
    assertTrue(isEnd);
    boolean isStart = LabeledChunkIdentifier.isStartOfChunk(prev, cur);
    assertTrue(isStart);
  }

  @Test
  public void testIsEndOfChunkWhenTagsSameButTypesDifferent() {
    LabeledChunkIdentifier.LabelTagType prev =
        new LabeledChunkIdentifier.LabelTagType("B-LOC", "B", "LOC");
    LabeledChunkIdentifier.LabelTagType cur =
        new LabeledChunkIdentifier.LabelTagType("B-ORG", "B", "ORG");
    boolean result = LabeledChunkIdentifier.isEndOfChunk(prev, cur);
    assertTrue(result);
  }

  @Test
  public void testIsStartOfChunkWhenTagsSameButTypesDifferent() {
    LabeledChunkIdentifier.LabelTagType prev =
        new LabeledChunkIdentifier.LabelTagType("I-LOC", "I", "LOC");
    LabeledChunkIdentifier.LabelTagType cur =
        new LabeledChunkIdentifier.LabelTagType("I-ORG", "I", "ORG");
    boolean result = LabeledChunkIdentifier.isStartOfChunk(prev, cur);
    assertTrue(result);
  }

  @Test
  public void testIsStartOfChunkFromDotTagToBtype() {
    LabeledChunkIdentifier.LabelTagType prev =
        new LabeledChunkIdentifier.LabelTagType("B-ORG", ".", "ORG");
    LabeledChunkIdentifier.LabelTagType cur =
        new LabeledChunkIdentifier.LabelTagType("B-ORG", "B", "ORG");
    boolean result = LabeledChunkIdentifier.isStartOfChunk(prev, cur);
    assertTrue(result);
  }

  @Test
  public void testIsEndOfChunkFromDotTagToOtype() {
    LabeledChunkIdentifier.LabelTagType prev =
        new LabeledChunkIdentifier.LabelTagType(".", ".", "LOC");
    LabeledChunkIdentifier.LabelTagType cur =
        new LabeledChunkIdentifier.LabelTagType("O", "O", "O");
    boolean result = LabeledChunkIdentifier.isEndOfChunk(prev, cur);
    assertTrue(result);
  }

  @Test
  public void testChunkExtractionWithOnlyOAndDotLabels() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    CoreLabel tok1 = new CoreLabel();
    tok1.set(CoreAnnotations.TextAnnotation.class, "Hello");
    tok1.set(CoreAnnotations.AnswerAnnotation.class, "O");
    tok1.set(CoreAnnotations.IndexAnnotation.class, 0);
    tok1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    tok1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    CoreLabel tok2 = new CoreLabel();
    tok2.set(CoreAnnotations.TextAnnotation.class, ".");
    tok2.set(CoreAnnotations.AnswerAnnotation.class, ".");
    tok2.set(CoreAnnotations.IndexAnnotation.class, 1);
    tok2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    tok2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);
    CoreLabel tok3 = new CoreLabel();
    tok3.set(CoreAnnotations.TextAnnotation.class, "There");
    tok3.set(CoreAnnotations.AnswerAnnotation.class, "O");
    tok3.set(CoreAnnotations.IndexAnnotation.class, 2);
    tok3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 8);
    tok3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 13);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(tok1);
    tokens.add(tok2);
    tokens.add(tok3);
    List<CoreMap> chunks =
        chunkIdentifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(0, chunks.size());
  }

  @Test
  public void testIsChunkReturnsFalseForPeriodTag() {
    LabeledChunkIdentifier.LabelTagType type =
        new LabeledChunkIdentifier.LabelTagType(".", ".", "LOC");
    boolean isChunk =
        new LabeledChunkIdentifier() {

          public boolean check() {
            return getTagType(type.label).tag.equals(".")
                && !super.getTagType(type.label).tag.equals("O")
                && !".".equals(super.getTagType(type.label).tag);
          }
        }.getTagType(".-LOC").tag.equals(".");
    assertTrue(isChunk);
  }

  @Test
  public void testTypeMatchesReturnsFalseForDifferentTypes() {
    LabeledChunkIdentifier.LabelTagType one =
        new LabeledChunkIdentifier.LabelTagType("B-ORG", "B", "ORG");
    LabeledChunkIdentifier.LabelTagType two =
        new LabeledChunkIdentifier.LabelTagType("B-LOC", "B", "LOC");
    assertFalse(one.typeMatches(two));
  }

  @Test
  public void testMultipleConsecutiveSingletonChunks() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    CoreLabel tok1 = new CoreLabel();
    tok1.set(CoreAnnotations.TextAnnotation.class, "Tom");
    tok1.set(CoreAnnotations.AnswerAnnotation.class, "S-PER");
    tok1.set(CoreAnnotations.IndexAnnotation.class, 0);
    tok1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    tok1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);
    CoreLabel tok2 = new CoreLabel();
    tok2.set(CoreAnnotations.TextAnnotation.class, "IBM");
    tok2.set(CoreAnnotations.AnswerAnnotation.class, "S-ORG");
    tok2.set(CoreAnnotations.IndexAnnotation.class, 1);
    tok2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 4);
    tok2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);
    CoreLabel tok3 = new CoreLabel();
    tok3.set(CoreAnnotations.TextAnnotation.class, "NY");
    tok3.set(CoreAnnotations.AnswerAnnotation.class, "S-LOC");
    tok3.set(CoreAnnotations.IndexAnnotation.class, 2);
    tok3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 8);
    tok3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(tok1);
    tokens.add(tok2);
    tokens.add(tok3);
    List<CoreMap> chunks =
        chunkIdentifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(3, chunks.size());
    assertEquals("PER", chunks.get(0).get(CoreAnnotations.AnswerAnnotation.class));
    assertEquals("ORG", chunks.get(1).get(CoreAnnotations.AnswerAnnotation.class));
    assertEquals("LOC", chunks.get(2).get(CoreAnnotations.AnswerAnnotation.class));
  }

  @Test
  public void testInvalidTransitionWithImmediateChunkInterruption() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    CoreLabel tok1 = new CoreLabel();
    tok1.set(CoreAnnotations.TextAnnotation.class, "Bill");
    tok1.set(CoreAnnotations.AnswerAnnotation.class, "B-PER");
    tok1.set(CoreAnnotations.IndexAnnotation.class, 0);
    tok1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    tok1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    CoreLabel tok2 = new CoreLabel();
    tok2.set(CoreAnnotations.TextAnnotation.class, "Xerox");
    tok2.set(CoreAnnotations.AnswerAnnotation.class, "B-ORG");
    tok2.set(CoreAnnotations.IndexAnnotation.class, 1);
    tok2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    tok2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(tok1);
    tokens.add(tok2);
    boolean threw = false;
    try {
      chunkIdentifier.getAnnotatedChunks(
          tokens, 0, CoreAnnotations.TextAnnotation.class, CoreAnnotations.AnswerAnnotation.class);
    } catch (RuntimeException e) {
      threw = true;
    }
    assertTrue(threw);
  }

  @Test
  public void testLabelWithMultipleHyphens() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    LabeledChunkIdentifier.LabelTagType tag = chunkIdentifier.getTagType("B-MISC-ORG-HYBRID");
    assertEquals("B-MISC-ORG-HYBRID", tag.label);
    assertEquals("B", tag.tag);
    assertEquals("MISC-ORG-HYBRID", tag.type);
  }

  @Test
  public void testLabelWithNoAlphaCharacters() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    LabeledChunkIdentifier.LabelTagType tag = chunkIdentifier.getTagType("---");
    assertEquals("---", tag.label);
    assertEquals("", tag.tag);
    assertEquals("--", tag.type);
  }

  @Test
  public void testFinalTokenIsChunkWithMissingEndSignal() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    CoreLabel tok1 = new CoreLabel();
    tok1.set(CoreAnnotations.TextAnnotation.class, "Bank");
    tok1.set(CoreAnnotations.AnswerAnnotation.class, "B-ORG");
    tok1.set(CoreAnnotations.IndexAnnotation.class, 0);
    tok1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    tok1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    CoreLabel tok2 = new CoreLabel();
    tok2.set(CoreAnnotations.TextAnnotation.class, "of");
    tok2.set(CoreAnnotations.AnswerAnnotation.class, "I-ORG");
    tok2.set(CoreAnnotations.IndexAnnotation.class, 1);
    tok2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    tok2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);
    CoreLabel tok3 = new CoreLabel();
    tok3.set(CoreAnnotations.TextAnnotation.class, "America");
    tok3.set(CoreAnnotations.AnswerAnnotation.class, "I-ORG");
    tok3.set(CoreAnnotations.IndexAnnotation.class, 2);
    tok3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 8);
    tok3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 15);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(tok1);
    tokens.add(tok2);
    tokens.add(tok3);
    List<CoreMap> chunks =
        chunkIdentifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, chunks.size());
    assertEquals("ORG", chunks.get(0).get(CoreAnnotations.AnswerAnnotation.class));
    assertEquals("Bank of America", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testIToSTagTransitionTriggersChunkEndAndStart() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    CoreLabel tok1 = new CoreLabel();
    tok1.set(CoreAnnotations.TextAnnotation.class, "San");
    tok1.set(CoreAnnotations.AnswerAnnotation.class, "I-LOC");
    tok1.set(CoreAnnotations.IndexAnnotation.class, 0);
    tok1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    tok1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);
    CoreLabel tok2 = new CoreLabel();
    tok2.set(CoreAnnotations.TextAnnotation.class, "Francisco");
    tok2.set(CoreAnnotations.AnswerAnnotation.class, "S-LOC");
    tok2.set(CoreAnnotations.IndexAnnotation.class, 1);
    tok2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 4);
    tok2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 13);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(tok1);
    tokens.add(tok2);
    List<CoreMap> chunks =
        chunkIdentifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(2, chunks.size());
    assertEquals("LOC", chunks.get(0).get(CoreAnnotations.AnswerAnnotation.class));
    assertEquals("LOC", chunks.get(1).get(CoreAnnotations.AnswerAnnotation.class));
    assertEquals("San", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("Francisco", chunks.get(1).get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testNonStandardUnknownTagPrefixLikeX() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    LabeledChunkIdentifier.LabelTagType tag = chunkIdentifier.getTagType("X-ORG");
    assertEquals("X", tag.tag);
    assertEquals("ORG", tag.type);
    assertEquals("X-ORG", tag.label);
  }

  @Test
  public void testChunkWithRepeatedWordsDifferentTags() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    CoreLabel tok1 = new CoreLabel();
    tok1.set(CoreAnnotations.TextAnnotation.class, "Paris");
    tok1.set(CoreAnnotations.AnswerAnnotation.class, "B-LOC");
    tok1.set(CoreAnnotations.IndexAnnotation.class, 0);
    tok1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    tok1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    CoreLabel tok2 = new CoreLabel();
    tok2.set(CoreAnnotations.TextAnnotation.class, "Paris");
    tok2.set(CoreAnnotations.AnswerAnnotation.class, "O");
    tok2.set(CoreAnnotations.IndexAnnotation.class, 1);
    tok2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    tok2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(tok1);
    tokens.add(tok2);
    List<CoreMap> chunks =
        chunkIdentifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, chunks.size());
    assertEquals("LOC", chunks.get(0).get(CoreAnnotations.AnswerAnnotation.class));
    assertEquals("Paris", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testChunkStartDetectionWithNullPrev() {
    LabeledChunkIdentifier.LabelTagType cur =
        new LabeledChunkIdentifier.LabelTagType("B-ORG", "B", "ORG");
    boolean isStart = LabeledChunkIdentifier.isStartOfChunk(null, cur);
    assertTrue(isStart);
  }

  @Test
  public void testChunkStartDetectionFromOToNonOTypeSameTag() {
    LabeledChunkIdentifier.LabelTagType prev =
        new LabeledChunkIdentifier.LabelTagType("O", "O", "O");
    LabeledChunkIdentifier.LabelTagType cur =
        new LabeledChunkIdentifier.LabelTagType("I-LOC", "I", "LOC");
    boolean isStart = LabeledChunkIdentifier.isStartOfChunk(prev, cur);
    assertTrue(isStart);
  }

  @Test
  public void testTypeMatchesWithIdenticalTypeStrings() {
    LabeledChunkIdentifier.LabelTagType a =
        new LabeledChunkIdentifier.LabelTagType("B-FOO", "B", "FOO");
    LabeledChunkIdentifier.LabelTagType b =
        new LabeledChunkIdentifier.LabelTagType("I-FOO", "I", "FOO");
    assertTrue(a.typeMatches(b));
  }

  @Test
  public void testChunkStartAndNoTagInLabelTriggersDefaultPositiveTag() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    chunkIdentifier.setDefaultPosTag("X");
    chunkIdentifier.setNegLabel("O");
    LabeledChunkIdentifier.LabelTagType tagType = chunkIdentifier.getTagType("MISC");
    assertEquals("MISC", tagType.label);
    assertEquals("X", tagType.tag);
    assertEquals("MISC", tagType.type);
  }

  @Test
  public void testContinueInsideChunkWithoutLeadingBThrowsException() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    CoreLabel tok1 = new CoreLabel();
    tok1.set(CoreAnnotations.TextAnnotation.class, "Entity");
    tok1.set(CoreAnnotations.AnswerAnnotation.class, "I-LOC");
    tok1.set(CoreAnnotations.IndexAnnotation.class, 0);
    tok1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    tok1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(tok1);
    boolean exceptionThrown = false;
    try {
      chunkIdentifier.getAnnotatedChunks(
          tokens, 0, CoreAnnotations.TextAnnotation.class, CoreAnnotations.AnswerAnnotation.class);
    } catch (RuntimeException e) {
      exceptionThrown = true;
    }
    assertTrue(exceptionThrown);
  }

  @Test
  public void testCompatibilityPredicateForcesChunkBreak() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    CoreLabel tok1 = new CoreLabel();
    tok1.set(CoreAnnotations.TextAnnotation.class, "State");
    tok1.set(CoreAnnotations.AnswerAnnotation.class, "B-LOC");
    tok1.set(CoreAnnotations.IndexAnnotation.class, 0);
    tok1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    tok1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    CoreLabel tok2 = new CoreLabel();
    tok2.set(CoreAnnotations.TextAnnotation.class, "Department");
    tok2.set(CoreAnnotations.AnswerAnnotation.class, "I-LOC");
    tok2.set(CoreAnnotations.IndexAnnotation.class, 1);
    tok2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    tok2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 16);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(tok1);
    tokens.add(tok2);
    Predicate<Pair<CoreLabel, CoreLabel>> breakEveryTime = pair -> false;
    List<CoreMap> chunks =
        chunkIdentifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class,
            breakEveryTime);
    assertEquals(2, chunks.size());
    assertEquals("LOC", chunks.get(0).get(CoreAnnotations.AnswerAnnotation.class));
    assertEquals("LOC", chunks.get(1).get(CoreAnnotations.AnswerAnnotation.class));
  }

  @Test
  public void testMalformedTagNotMatchingPatternUsesDefaultTag() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    chunkIdentifier.setNegLabel("OUT");
    chunkIdentifier.setDefaultNegTag("DNEG");
    chunkIdentifier.setDefaultPosTag("DPOS");
    LabeledChunkIdentifier.LabelTagType tag = chunkIdentifier.getTagType("ThisIsNotValid");
    assertEquals("ThisIsNotValid", tag.label);
    assertEquals("DPOS", tag.tag);
    assertEquals("ThisIsNotValid", tag.type);
  }

  @Test
  public void testMalformedNegativeLabelUsesDefaultNegativeTag() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    chunkIdentifier.setNegLabel("NEG");
    chunkIdentifier.setDefaultNegTag("ZZ");
    LabeledChunkIdentifier.LabelTagType tag = chunkIdentifier.getTagType("NEG");
    assertEquals("ZZ", tag.tag);
    assertEquals("NEG", tag.type);
    assertEquals("NEG", tag.label);
  }

  @Test
  public void testTwoCompatibleChunksWithSameTypeNoBreakBetween() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    CoreLabel tok1 = new CoreLabel();
    tok1.set(CoreAnnotations.TextAnnotation.class, "United");
    tok1.set(CoreAnnotations.AnswerAnnotation.class, "B-ORG");
    tok1.set(CoreAnnotations.IndexAnnotation.class, 0);
    tok1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    tok1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
    CoreLabel tok2 = new CoreLabel();
    tok2.set(CoreAnnotations.TextAnnotation.class, "Nations");
    tok2.set(CoreAnnotations.AnswerAnnotation.class, "I-ORG");
    tok2.set(CoreAnnotations.IndexAnnotation.class, 1);
    tok2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 7);
    tok2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 14);
    Predicate<Pair<CoreLabel, CoreLabel>> alwaysCompatible = pair -> true;
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(tok1);
    tokens.add(tok2);
    List<CoreMap> chunks =
        chunkIdentifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class,
            alwaysCompatible);
    assertEquals(1, chunks.size());
    assertEquals("ORG", chunks.get(0).get(CoreAnnotations.AnswerAnnotation.class));
    assertEquals("United Nations", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testChunkWithNonStandardPunctuationTag() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Punctuation");
    token.set(CoreAnnotations.AnswerAnnotation.class, "!-TOKEN");
    token.set(CoreAnnotations.IndexAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    List<CoreMap> chunks =
        chunkIdentifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, chunks.size());
    CoreMap chunk = chunks.get(0);
    assertEquals("TOKEN", chunk.get(CoreAnnotations.AnswerAnnotation.class));
    assertEquals("Punctuation", chunk.get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testOnlyOTypeTokensNoChunksExtracted() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "Hello");
    t1.set(CoreAnnotations.AnswerAnnotation.class, "O");
    t1.set(CoreAnnotations.IndexAnnotation.class, 0);
    t1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    t1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "World");
    t2.set(CoreAnnotations.AnswerAnnotation.class, "O");
    t2.set(CoreAnnotations.IndexAnnotation.class, 1);
    t2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    t2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(t1);
    tokens.add(t2);
    List<CoreMap> result =
        chunkIdentifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testIOEncodingWithMidSentenceTransitionToDifferentType() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    CoreLabel w1 = new CoreLabel();
    w1.set(CoreAnnotations.TextAnnotation.class, "John");
    w1.set(CoreAnnotations.AnswerAnnotation.class, "I-PER");
    w1.set(CoreAnnotations.IndexAnnotation.class, 0);
    w1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    w1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    CoreLabel w2 = new CoreLabel();
    w2.set(CoreAnnotations.TextAnnotation.class, "Doe");
    w2.set(CoreAnnotations.AnswerAnnotation.class, "I-ORG");
    w2.set(CoreAnnotations.IndexAnnotation.class, 1);
    w2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    w2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(w1);
    tokens.add(w2);
    List<CoreMap> chunks =
        chunkIdentifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(2, chunks.size());
    assertEquals("PER", chunks.get(0).get(CoreAnnotations.AnswerAnnotation.class));
    assertEquals("ORG", chunks.get(1).get(CoreAnnotations.AnswerAnnotation.class));
  }

  @Test
  public void testGetTagType_WhitespaceOnlyLabel() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    chunkIdentifier.setDefaultNegTag("NNN");
    LabeledChunkIdentifier.LabelTagType tagType = chunkIdentifier.getTagType(" ");
    assertEquals(" ", tagType.label);
    assertEquals("NNN", tagType.tag);
    assertEquals(" ", tagType.type);
  }

  @Test
  public void testGetTagType_LabelIsJustHyphen() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    LabeledChunkIdentifier.LabelTagType tagType = chunkIdentifier.getTagType("-");
    assertEquals("-", tagType.label);
    assertEquals("", tagType.tag);
    assertEquals("", tagType.type);
  }

  @Test
  public void testGetTagType_OnlyTagNoType() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    LabeledChunkIdentifier.LabelTagType tagType = chunkIdentifier.getTagType("B-");
    assertEquals("B-", tagType.label);
    assertEquals("B", tagType.tag);
    assertEquals("", tagType.type);
  }

  @Test
  public void testGetTagType_TagIsNumeric() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    LabeledChunkIdentifier.LabelTagType tagType = chunkIdentifier.getTagType("123-LOC");
    assertEquals("123-LOC", tagType.label);
    assertEquals("123", tagType.tag);
    assertEquals("LOC", tagType.type);
  }

  @Test
  public void testIsEndOfChunk_EToS_SameType() {
    LabeledChunkIdentifier.LabelTagType prev =
        new LabeledChunkIdentifier.LabelTagType("E-ORG", "E", "ORG");
    LabeledChunkIdentifier.LabelTagType cur =
        new LabeledChunkIdentifier.LabelTagType("S-ORG", "S", "ORG");
    assertTrue(LabeledChunkIdentifier.isEndOfChunk(prev, cur));
  }

  @Test
  public void testIsStartOfChunk_EToS_SameType() {
    LabeledChunkIdentifier.LabelTagType prev =
        new LabeledChunkIdentifier.LabelTagType("E-ORG", "E", "ORG");
    LabeledChunkIdentifier.LabelTagType cur =
        new LabeledChunkIdentifier.LabelTagType("S-ORG", "S", "ORG");
    assertTrue(LabeledChunkIdentifier.isStartOfChunk(prev, cur));
  }

  @Test
  public void testIsEndOfChunk_BToBSameType() {
    LabeledChunkIdentifier.LabelTagType prev =
        new LabeledChunkIdentifier.LabelTagType("B-LOC", "B", "LOC");
    LabeledChunkIdentifier.LabelTagType cur =
        new LabeledChunkIdentifier.LabelTagType("B-LOC", "B", "LOC");
    boolean result = LabeledChunkIdentifier.isEndOfChunk(prev, cur);
    assertTrue(result);
  }

  @Test
  public void testChunkFromBracketStyleTags() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    CoreLabel tok1 = new CoreLabel();
    tok1.set(CoreAnnotations.TextAnnotation.class, "[");
    tok1.set(CoreAnnotations.AnswerAnnotation.class, "[-PER");
    tok1.set(CoreAnnotations.IndexAnnotation.class, 0);
    tok1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    tok1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 1);
    CoreLabel tok2 = new CoreLabel();
    tok2.set(CoreAnnotations.TextAnnotation.class, "Obama");
    tok2.set(CoreAnnotations.AnswerAnnotation.class, "]-PER");
    tok2.set(CoreAnnotations.IndexAnnotation.class, 1);
    tok2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 2);
    tok2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(tok1);
    tokens.add(tok2);
    List<CoreMap> chunks =
        chunkIdentifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, chunks.size());
    assertEquals("PER", chunks.get(0).get(CoreAnnotations.AnswerAnnotation.class));
  }

  @Test
  public void testMultipleChunksSameTagSameTypeNoError() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "Tom");
    t1.set(CoreAnnotations.AnswerAnnotation.class, "B-PER");
    t1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    t1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);
    t1.set(CoreAnnotations.IndexAnnotation.class, 0);
    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "Jerry");
    t2.set(CoreAnnotations.AnswerAnnotation.class, "B-PER");
    t2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 4);
    t2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9);
    t2.set(CoreAnnotations.IndexAnnotation.class, 1);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(t1);
    tokens.add(t2);
    List<CoreMap> chunks =
        chunkIdentifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(2, chunks.size());
    assertEquals("PER", chunks.get(0).get(CoreAnnotations.AnswerAnnotation.class));
    assertEquals("PER", chunks.get(1).get(CoreAnnotations.AnswerAnnotation.class));
  }

  @Test(expected = RuntimeException.class)
  public void testBackToBackBTransitionsWithoutClosureDoThrow() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    CoreLabel tok1 = new CoreLabel();
    tok1.set(CoreAnnotations.TextAnnotation.class, "Apple");
    tok1.set(CoreAnnotations.AnswerAnnotation.class, "B-ORG");
    tok1.set(CoreAnnotations.IndexAnnotation.class, 0);
    tok1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    tok1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    CoreLabel tok2 = new CoreLabel();
    tok2.set(CoreAnnotations.TextAnnotation.class, "Amazon");
    tok2.set(CoreAnnotations.AnswerAnnotation.class, "B-ORG");
    tok2.set(CoreAnnotations.IndexAnnotation.class, 1);
    tok2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    tok2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);
    CoreLabel tok3 = new CoreLabel();
    tok3.set(CoreAnnotations.TextAnnotation.class, "Google");
    tok3.set(CoreAnnotations.AnswerAnnotation.class, "B-ORG");
    tok3.set(CoreAnnotations.IndexAnnotation.class, 2);
    tok3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 13);
    tok3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 19);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(tok1);
    tokens.add(tok2);
    tokens.add(tok3);
    chunkIdentifier.getAnnotatedChunks(
        tokens, 0, CoreAnnotations.TextAnnotation.class, CoreAnnotations.AnswerAnnotation.class);
  }

  @Test
  public void testTypeMatchesExactSameReference() {
    LabeledChunkIdentifier.LabelTagType a =
        new LabeledChunkIdentifier.LabelTagType("B-TMP", "B", "TMP");
    assertTrue(a.typeMatches(a));
  }

  @Test
  public void testTypeMatchesWithNullInputReturnsFalse() {
    LabeledChunkIdentifier.LabelTagType a =
        new LabeledChunkIdentifier.LabelTagType("B-TMP", "B", "TMP");
    assertFalse(a.typeMatches(null));
  }

  @Test
  public void testChunkEndingWithFinalLTag() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    CoreLabel c1 = new CoreLabel();
    c1.set(CoreAnnotations.TextAnnotation.class, "University");
    c1.set(CoreAnnotations.AnswerAnnotation.class, "B-ORG");
    c1.set(CoreAnnotations.IndexAnnotation.class, 0);
    c1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    c1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);
    CoreLabel c2 = new CoreLabel();
    c2.set(CoreAnnotations.TextAnnotation.class, "of");
    c2.set(CoreAnnotations.AnswerAnnotation.class, "I-ORG");
    c2.set(CoreAnnotations.IndexAnnotation.class, 1);
    c2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 11);
    c2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 13);
    CoreLabel c3 = new CoreLabel();
    c3.set(CoreAnnotations.TextAnnotation.class, "California");
    c3.set(CoreAnnotations.AnswerAnnotation.class, "L-ORG");
    c3.set(CoreAnnotations.IndexAnnotation.class, 2);
    c3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 14);
    c3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 24);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(c1);
    tokens.add(c2);
    tokens.add(c3);
    List<CoreMap> chunks =
        chunkIdentifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, chunks.size());
    assertEquals("ORG", chunks.get(0).get(CoreAnnotations.AnswerAnnotation.class));
    assertEquals(
        "University of California", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
  }
}
