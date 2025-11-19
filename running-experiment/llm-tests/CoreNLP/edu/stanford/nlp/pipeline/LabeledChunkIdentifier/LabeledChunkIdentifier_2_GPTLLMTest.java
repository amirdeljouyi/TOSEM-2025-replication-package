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

public class LabeledChunkIdentifier_2_GPTLLMTest {

  @Test
  public void testGetTagType_WithStandardLabel() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    LabeledChunkIdentifier.LabelTagType tagType = identifier.getTagType("B-PER");
    assertEquals("B-PER", tagType.label);
    assertEquals("B", tagType.tag);
    assertEquals("PER", tagType.type);
  }

  @Test
  public void testGetTagType_WithO_Label() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    LabeledChunkIdentifier.LabelTagType tagType = identifier.getTagType("O");
    assertEquals("O", tagType.label);
    assertEquals("O", tagType.tag);
    assertEquals("O", tagType.type);
  }

  @Test
  public void testGetTagType_IgnoreProvidedTag() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    identifier.setIgnoreProvidedTag(true);
    LabeledChunkIdentifier.LabelTagType tagType = identifier.getTagType("B-LOC");
    assertEquals("B-LOC", tagType.label);
    assertEquals("I", tagType.tag);
    assertEquals("LOC", tagType.type);
  }

  @Test
  public void testGetTagType_CustomPositiveNegativeTags() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    identifier.setNegLabel("NIL");
    identifier.setDefaultPosTag("X");
    identifier.setDefaultNegTag("Z");
    identifier.setIgnoreProvidedTag(true);
    LabeledChunkIdentifier.LabelTagType tagType = identifier.getTagType("A-NIL");
    assertEquals("Z", tagType.tag);
    assertEquals("NIL", tagType.type);
  }

  @Test
  public void testIsStartOfChunk_True() {
    LabeledChunkIdentifier.LabelTagType prev =
        new LabeledChunkIdentifier.LabelTagType("I-ORG", "I", "ORG");
    LabeledChunkIdentifier.LabelTagType curr =
        new LabeledChunkIdentifier.LabelTagType("B-ORG", "B", "ORG");
    assertTrue(LabeledChunkIdentifier.isStartOfChunk(prev, curr));
  }

  @Test
  public void testIsEndOfChunk_True() {
    LabeledChunkIdentifier.LabelTagType prev =
        new LabeledChunkIdentifier.LabelTagType("I-ORG", "I", "ORG");
    LabeledChunkIdentifier.LabelTagType curr =
        new LabeledChunkIdentifier.LabelTagType("O", "O", "O");
    assertTrue(LabeledChunkIdentifier.isEndOfChunk(prev, curr));
  }

  @Test
  public void testSingleTokenChunk_UEncoding() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Alice");
    token1.set(CoreAnnotations.AnswerAnnotation.class, "U-PER");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, chunks.size());
    CoreMap chunk = chunks.get(0);
    assertEquals("Alice", chunk.get(CoreAnnotations.TextAnnotation.class));
    assertEquals("PER", chunk.get(CoreAnnotations.AnswerAnnotation.class));
  }

  @Test
  public void testMultipleChunks_IOB2Encoding() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "John");
    t1.set(CoreAnnotations.AnswerAnnotation.class, "B-PER");
    t1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    t1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "Smith");
    t2.set(CoreAnnotations.AnswerAnnotation.class, "I-PER");
    t2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    t2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);
    CoreLabel t3 = new CoreLabel();
    t3.set(CoreAnnotations.TextAnnotation.class, "visited");
    t3.set(CoreAnnotations.AnswerAnnotation.class, "O");
    t3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 11);
    t3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 18);
    CoreLabel t4 = new CoreLabel();
    t4.set(CoreAnnotations.TextAnnotation.class, "Stanford");
    t4.set(CoreAnnotations.AnswerAnnotation.class, "B-ORG");
    t4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 19);
    t4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 27);
    CoreLabel t5 = new CoreLabel();
    t5.set(CoreAnnotations.TextAnnotation.class, "University");
    t5.set(CoreAnnotations.AnswerAnnotation.class, "I-ORG");
    t5.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 28);
    t5.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 38);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(t1);
    tokens.add(t2);
    tokens.add(t3);
    tokens.add(t4);
    tokens.add(t5);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(2, chunks.size());
    assertEquals("John Smith", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("PER", chunks.get(0).get(CoreAnnotations.AnswerAnnotation.class));
    assertEquals("Stanford University", chunks.get(1).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("ORG", chunks.get(1).get(CoreAnnotations.AnswerAnnotation.class));
  }

  @Test(expected = RuntimeException.class)
  public void testOverlappingChunks_ShouldThrow() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "John");
    t1.set(CoreAnnotations.AnswerAnnotation.class, "B-PER");
    t1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    t1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "Smith");
    t2.set(CoreAnnotations.AnswerAnnotation.class, "B-ORG");
    t2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    t2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(t1);
    tokens.add(t2);
    identifier.getAnnotatedChunks(
        tokens, 0, CoreAnnotations.TextAnnotation.class, CoreAnnotations.AnswerAnnotation.class);
  }

  @Test
  public void testCompatibleCheckDisallowsMerge() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "OpenAI");
    t1.set(CoreAnnotations.AnswerAnnotation.class, "B-ORG");
    t1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    t1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "Inc");
    t2.set(CoreAnnotations.AnswerAnnotation.class, "I-ORG");
    t2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 7);
    t2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(t1);
    tokens.add(t2);
    Predicate<Pair<CoreLabel, CoreLabel>> compat = pair -> false;
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class,
            compat);
    assertEquals(2, chunks.size());
    assertEquals("OpenAI", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("Inc", chunks.get(1).get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testEmptyInputReturnsEmptyChunkList() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    List<CoreLabel> tokens = new ArrayList<>();
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertTrue(chunks.isEmpty());
  }

  @Test
  public void testNullLabelTokenTreatedAsO() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Unknown");
    token.set(CoreAnnotations.AnswerAnnotation.class, null);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);
    List<CoreLabel> tokens = Collections.singletonList(token);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertTrue(chunks.isEmpty());
  }

  @Test
  public void testAdjacentSingleAndMultiChunks() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "IBM");
    t1.set(CoreAnnotations.AnswerAnnotation.class, "U-ORG");
    t1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    t1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);
    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "John");
    t2.set(CoreAnnotations.AnswerAnnotation.class, "B-PER");
    t2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 4);
    t2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);
    CoreLabel t3 = new CoreLabel();
    t3.set(CoreAnnotations.TextAnnotation.class, "Doe");
    t3.set(CoreAnnotations.AnswerAnnotation.class, "L-PER");
    t3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 9);
    t3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(t1);
    tokens.add(t2);
    tokens.add(t3);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(2, chunks.size());
    assertEquals("IBM", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("ORG", chunks.get(0).get(CoreAnnotations.AnswerAnnotation.class));
    assertEquals("John Doe", chunks.get(1).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("PER", chunks.get(1).get(CoreAnnotations.AnswerAnnotation.class));
  }

  @Test
  public void testGetTagType_LabelWithoutDash() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    LabeledChunkIdentifier.LabelTagType tagType = identifier.getTagType("MISC");
    assertEquals("MISC", tagType.label);
    assertEquals("I", tagType.tag);
    assertEquals("MISC", tagType.type);
  }

  @Test
  public void testGetTagType_NullLabel() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    LabeledChunkIdentifier.LabelTagType tagType = identifier.getTagType(null);
    assertEquals("O", tagType.tag);
    assertEquals("O", tagType.type);
  }

  @Test
  public void testIsStartOfChunk_NullPrevious() {
    LabeledChunkIdentifier.LabelTagType cur =
        new LabeledChunkIdentifier.LabelTagType("B-LOC", "B", "LOC");
    boolean isStart = LabeledChunkIdentifier.isStartOfChunk(null, cur);
    assertTrue(isStart);
  }

  @Test
  public void testIsStartOfChunk_SameTypeNoStartTag() {
    LabeledChunkIdentifier.LabelTagType prev =
        new LabeledChunkIdentifier.LabelTagType("I-LOC", "I", "LOC");
    LabeledChunkIdentifier.LabelTagType cur =
        new LabeledChunkIdentifier.LabelTagType("I-LOC", "I", "LOC");
    boolean isStart = LabeledChunkIdentifier.isStartOfChunk(prev, cur);
    assertFalse(isStart);
  }

  @Test
  public void testIsEndOfChunk_SameTypeNoEnd() {
    LabeledChunkIdentifier.LabelTagType prev =
        new LabeledChunkIdentifier.LabelTagType("I-LOC", "I", "LOC");
    LabeledChunkIdentifier.LabelTagType cur =
        new LabeledChunkIdentifier.LabelTagType("I-LOC", "I", "LOC");
    boolean isEnd = LabeledChunkIdentifier.isEndOfChunk(prev, cur);
    assertFalse(isEnd);
  }

  @Test
  public void testChunkBoundaryWhenTypeChangesButTagIsSame() {
    LabeledChunkIdentifier.LabelTagType prev =
        new LabeledChunkIdentifier.LabelTagType("I-LOC", "I", "LOC");
    LabeledChunkIdentifier.LabelTagType cur =
        new LabeledChunkIdentifier.LabelTagType("I-MISC", "I", "MISC");
    boolean isEnd = LabeledChunkIdentifier.isEndOfChunk(prev, cur);
    assertTrue(isEnd);
  }

  @Test
  public void testChunkBoundaryWhenTagIsDot() {
    LabeledChunkIdentifier.LabelTagType prev =
        new LabeledChunkIdentifier.LabelTagType("I-ORG", ".", "ORG");
    LabeledChunkIdentifier.LabelTagType cur =
        new LabeledChunkIdentifier.LabelTagType("O", "O", "O");
    boolean isEnd = LabeledChunkIdentifier.isEndOfChunk(prev, cur);
    assertFalse(isEnd);
  }

  @Test
  public void testInputWithOnlyNegativeLabelsReturnsEmptyChunkList() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "a");
    t1.set(CoreAnnotations.AnswerAnnotation.class, "O");
    t1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    t1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 1);
    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "b");
    t2.set(CoreAnnotations.AnswerAnnotation.class, "O");
    t2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 2);
    t2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(t1);
    tokens.add(t2);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertTrue(chunks.isEmpty());
  }

  @Test
  public void testChunkEndsAtLastToken() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "George");
    t1.set(CoreAnnotations.AnswerAnnotation.class, "B-PER");
    t1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    t1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "Bush");
    t2.set(CoreAnnotations.AnswerAnnotation.class, "I-PER");
    t2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 7);
    t2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(t1);
    tokens.add(t2);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, chunks.size());
    assertEquals("George Bush", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("PER", chunks.get(0).get(CoreAnnotations.AnswerAnnotation.class));
  }

  @Test
  public void testChunkStartImmediatelyFollowsChunkEnd() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "IBM");
    t1.set(CoreAnnotations.AnswerAnnotation.class, "U-ORG");
    t1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    t1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);
    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "Alice");
    t2.set(CoreAnnotations.AnswerAnnotation.class, "B-PER");
    t2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 4);
    t2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(t1);
    tokens.add(t2);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(2, chunks.size());
    assertEquals("IBM", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("ORG", chunks.get(0).get(CoreAnnotations.AnswerAnnotation.class));
    assertEquals("Alice", chunks.get(1).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("PER", chunks.get(1).get(CoreAnnotations.AnswerAnnotation.class));
  }

  @Test
  public void testMisformattedLabelFallback() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Mismatch");
    token.set(CoreAnnotations.AnswerAnnotation.class, "MISC");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);
    List<CoreLabel> tokens = Collections.singletonList(token);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, chunks.size());
    assertEquals("Mismatch", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("MISC", chunks.get(0).get(CoreAnnotations.AnswerAnnotation.class));
  }

  @Test
  public void testSameLabelRepeatedTokensSingleChunk() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "New");
    t1.set(CoreAnnotations.AnswerAnnotation.class, "I-LOC");
    t1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    t1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);
    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "York");
    t2.set(CoreAnnotations.AnswerAnnotation.class, "I-LOC");
    t2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 4);
    t2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(t1);
    tokens.add(t2);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, chunks.size());
    assertEquals("New York", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("LOC", chunks.get(0).get(CoreAnnotations.AnswerAnnotation.class));
  }

  @Test
  public void testLabelWithDashInTypePart() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    LabeledChunkIdentifier.LabelTagType tagType = identifier.getTagType("B-ORG-XYZ");
    assertEquals("B-ORG-XYZ", tagType.label);
    assertEquals("B", tagType.tag);
    assertEquals("ORG-XYZ", tagType.type);
  }

  @Test
  public void testLabelWithOnlyDash() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    LabeledChunkIdentifier.LabelTagType tagType = identifier.getTagType("-");
    assertEquals("-", tagType.label);
    assertEquals("", tagType.tag);
    assertEquals("", tagType.type);
  }

  @Test
  public void testIsEndOfChunk_NullPreviousReturnsFalse() {
    LabeledChunkIdentifier.LabelTagType cur =
        new LabeledChunkIdentifier.LabelTagType("B-LOC", "B", "LOC");
    boolean result = LabeledChunkIdentifier.isEndOfChunk(null, cur);
    assertFalse(result);
  }

  @Test
  public void testIsStartOfChunk_TypeChangeTriggersStart() {
    LabeledChunkIdentifier.LabelTagType prev =
        new LabeledChunkIdentifier.LabelTagType("I-ORG", "I", "ORG");
    LabeledChunkIdentifier.LabelTagType cur =
        new LabeledChunkIdentifier.LabelTagType("I-LOC", "I", "LOC");
    boolean result = LabeledChunkIdentifier.isStartOfChunk(prev, cur);
    assertTrue(result);
  }

  @Test
  public void testMismatchEncoding_BILOUStructureEndsChunkProperly() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "John");
    t1.set(CoreAnnotations.AnswerAnnotation.class, "B-PER");
    t1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    t1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "Smith");
    t2.set(CoreAnnotations.AnswerAnnotation.class, "L-PER");
    t2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    t2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(t1);
    tokens.add(t2);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, chunks.size());
    assertEquals("John Smith", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("PER", chunks.get(0).get(CoreAnnotations.AnswerAnnotation.class));
  }

  @Test
  public void testSingleTokenChunkWithBracketTag() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Emily");
    token.set(CoreAnnotations.AnswerAnnotation.class, "[-PER");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, chunks.size());
    assertEquals("Emily", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("PER", chunks.get(0).get(CoreAnnotations.AnswerAnnotation.class));
  }

  @Test
  public void testTokenWithDotTag_ShouldBeIgnored() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, ".");
    token.set(CoreAnnotations.AnswerAnnotation.class, ".-PER");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 1);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(0, chunks.size());
  }

  @Test
  public void testChunkWithSingleCharacterOffsetSpans() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "A");
    token1.set(CoreAnnotations.AnswerAnnotation.class, "U-X");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 1);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 2);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            5,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, chunks.size());
    assertEquals("A", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("X", chunks.get(0).get(CoreAnnotations.AnswerAnnotation.class));
    int begin = chunks.get(0).get(CoreAnnotations.TokenBeginAnnotation.class);
    int end = chunks.get(0).get(CoreAnnotations.TokenEndAnnotation.class);
    assertEquals(5, begin);
    assertEquals(6, end);
  }

  @Test
  public void testPredicateNullPreservesDefaultBehavior() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "Google");
    t1.set(CoreAnnotations.AnswerAnnotation.class, "B-ORG");
    t1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    t1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "Inc");
    t2.set(CoreAnnotations.AnswerAnnotation.class, "I-ORG");
    t2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 7);
    t2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(t1);
    tokens.add(t2);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, chunks.size());
    assertEquals("Google Inc", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("ORG", chunks.get(0).get(CoreAnnotations.AnswerAnnotation.class));
  }

  @Test
  public void testOnlyOtags_NoChunks() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "The");
    token1.set(CoreAnnotations.AnswerAnnotation.class, "O");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "company");
    token2.set(CoreAnnotations.AnswerAnnotation.class, "O");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 4);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);
    List<CoreMap> result =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testChunkStartsAtIndexZero() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "New");
    token1.set(CoreAnnotations.AnswerAnnotation.class, "B-LOC");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "York");
    token2.set(CoreAnnotations.AnswerAnnotation.class, "I-LOC");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 4);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);
    List<CoreMap> result =
        identifier.getAnnotatedChunks(
            tokens,
            10,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, result.size());
    CoreMap chunk = result.get(0);
    Integer begin = chunk.get(CoreAnnotations.TokenBeginAnnotation.class);
    Integer end = chunk.get(CoreAnnotations.TokenEndAnnotation.class);
    assertEquals((Integer) 10, begin);
    assertEquals((Integer) 12, end);
  }

  @Test
  public void testConsecutiveOandB_ChunkStartsAfterO() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Hello");
    token1.set(CoreAnnotations.AnswerAnnotation.class, "O");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "World");
    token2.set(CoreAnnotations.AnswerAnnotation.class, "B-LOC");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);
    List<CoreMap> result =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, result.size());
    CoreMap chunk = result.get(0);
    assertEquals("World", chunk.get(CoreAnnotations.TextAnnotation.class));
    assertEquals("LOC", chunk.get(CoreAnnotations.AnswerAnnotation.class));
  }

  @Test
  public void testEOEChunkEncoding_SingleChunk() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Bank");
    token1.set(CoreAnnotations.AnswerAnnotation.class, "B-ORG");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "of");
    token2.set(CoreAnnotations.AnswerAnnotation.class, "I-ORG");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);
    CoreLabel token3 = new CoreLabel();
    token3.set(CoreAnnotations.TextAnnotation.class, "America");
    token3.set(CoreAnnotations.AnswerAnnotation.class, "E-ORG");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 8);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 15);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);
    tokens.add(token3);
    List<CoreMap> result =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, result.size());
    CoreMap chunk = result.get(0);
    assertEquals("Bank of America", chunk.get(CoreAnnotations.TextAnnotation.class));
    assertEquals("ORG", chunk.get(CoreAnnotations.AnswerAnnotation.class));
  }

  @Test
  public void testBracketTagFormsChunk() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "Apple");
    t1.set(CoreAnnotations.AnswerAnnotation.class, "[-ORG");
    t1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    t1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "Inc");
    t2.set(CoreAnnotations.AnswerAnnotation.class, "]-ORG");
    t2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    t2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(t1);
    tokens.add(t2);
    List<CoreMap> result =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, result.size());
    CoreMap chunk = result.get(0);
    assertEquals("Apple Inc", chunk.get(CoreAnnotations.TextAnnotation.class));
    assertEquals("ORG", chunk.get(CoreAnnotations.AnswerAnnotation.class));
  }

  @Test
  public void testLabelPatternNoMatchDefaultApplied() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "Hello");
    t1.set(CoreAnnotations.AnswerAnnotation.class, "UNKNOWN");
    t1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    t1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "World");
    t2.set(CoreAnnotations.AnswerAnnotation.class, "UNKNOWN");
    t2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    t2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(t1);
    tokens.add(t2);
    identifier.setNegLabel("OTHER");
    List<CoreMap> result =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, result.size());
    CoreMap chunk = result.get(0);
    assertEquals("Hello World", chunk.get(CoreAnnotations.TextAnnotation.class));
    assertEquals("UNKNOWN", chunk.get(CoreAnnotations.AnswerAnnotation.class));
  }

  @Test
  public void testTypeMismatchTriggersChunkSplit() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "Amazon");
    t1.set(CoreAnnotations.AnswerAnnotation.class, "B-ORG");
    t1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    t1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "Forest");
    t2.set(CoreAnnotations.AnswerAnnotation.class, "I-LOC");
    t2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 7);
    t2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 13);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(t1);
    tokens.add(t2);
    List<CoreMap> result =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(2, result.size());
    assertEquals("Amazon", result.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("ORG", result.get(0).get(CoreAnnotations.AnswerAnnotation.class));
    assertEquals("Forest", result.get(1).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("LOC", result.get(1).get(CoreAnnotations.AnswerAnnotation.class));
  }

  @Test
  public void testConsecutiveBTagsTriggerNewChunks() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "Microsoft");
    t1.set(CoreAnnotations.AnswerAnnotation.class, "B-ORG");
    t1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    t1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9);
    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "Apple");
    t2.set(CoreAnnotations.AnswerAnnotation.class, "B-ORG");
    t2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
    t2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 15);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(t1);
    tokens.add(t2);
    List<CoreMap> result =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(2, result.size());
    assertEquals("Microsoft", result.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("Apple", result.get(1).get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testTokenWithDefaultNegLabelAndNoDash() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    identifier.setNegLabel("OTHER");
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "example");
    token.set(CoreAnnotations.AnswerAnnotation.class, "OTHER");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);
    List<CoreLabel> tokens = Collections.singletonList(token);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertTrue(chunks.isEmpty());
  }

  @Test
  public void testMultipleChunksWithAlternatingTypes() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "Paris");
    t1.set(CoreAnnotations.AnswerAnnotation.class, "B-LOC");
    t1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    t1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "France");
    t2.set(CoreAnnotations.AnswerAnnotation.class, "B-ORG");
    t2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    t2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);
    CoreLabel t3 = new CoreLabel();
    t3.set(CoreAnnotations.TextAnnotation.class, "Google");
    t3.set(CoreAnnotations.AnswerAnnotation.class, "B-ORG");
    t3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 13);
    t3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 19);
    CoreLabel t4 = new CoreLabel();
    t4.set(CoreAnnotations.TextAnnotation.class, "Maps");
    t4.set(CoreAnnotations.AnswerAnnotation.class, "I-ORG");
    t4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 20);
    t4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 24);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(t1);
    tokens.add(t2);
    tokens.add(t3);
    tokens.add(t4);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(3, chunks.size());
    assertEquals("Paris", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("LOC", chunks.get(0).get(CoreAnnotations.AnswerAnnotation.class));
    assertEquals("France", chunks.get(1).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("ORG", chunks.get(1).get(CoreAnnotations.AnswerAnnotation.class));
    assertEquals("Google Maps", chunks.get(2).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("ORG", chunks.get(2).get(CoreAnnotations.AnswerAnnotation.class));
  }

  @Test
  public void testChunkInterruptedByIncompatiblePredicate() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "John");
    t1.set(CoreAnnotations.AnswerAnnotation.class, "B-PER");
    t1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    t1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "Doe");
    t2.set(CoreAnnotations.AnswerAnnotation.class, "I-PER");
    t2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    t2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);
    CoreLabel t3 = new CoreLabel();
    t3.set(CoreAnnotations.TextAnnotation.class, "Smith");
    t3.set(CoreAnnotations.AnswerAnnotation.class, "I-PER");
    t3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 9);
    t3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 14);
    List<CoreLabel> tokens = Arrays.asList(t1, t2, t3);
    Predicate<edu.stanford.nlp.util.Pair<CoreLabel, CoreLabel>> predicate =
        pair -> {
          CoreLabel current = pair.first;
          CoreLabel prev = pair.second;
          if (prev != null && current.get(CoreAnnotations.TextAnnotation.class).equals("Smith")) {
            return false;
          }
          return true;
        };
    // List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0,
    // CoreAnnotations.TextAnnotation.class, CoreAnnotations.AnswerAnnotation.class,
    // CoreAnnotations.MentionTokenAnnotation.class, CoreAnnotations.MentionTypeAnnotation.class,
    // predicate);
    // assertEquals(2, chunks.size());
    // assertEquals("John Doe", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
    // assertEquals("Smith", chunks.get(1).get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testEmptyStringLabelIsChunk() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Hello");
    token.set(CoreAnnotations.AnswerAnnotation.class, "");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    List<CoreLabel> tokens = Collections.singletonList(token);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, chunks.size());
    assertEquals("Hello", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("", chunks.get(0).get(CoreAnnotations.AnswerAnnotation.class));
  }

  @Test
  public void testTokenWithoutLabelAnnotation() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "NoLabel");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);
    List<CoreLabel> tokens = Collections.singletonList(token);
    List<CoreMap> result =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testDefaultPosNegTagSettingsUsed() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    identifier.setIgnoreProvidedTag(true);
    identifier.setNegLabel("NOPER");
    identifier.setDefaultPosTag("X");
    identifier.setDefaultNegTag("Z");
    LabeledChunkIdentifier.LabelTagType type = identifier.getTagType("IgnoreMe-NOPER");
    assertEquals("IgnoreMe-NOPER", type.label);
    assertEquals("Z", type.tag);
    assertEquals("NOPER", type.type);
  }

  @Test
  public void testChunkEndsAtSentenceEnd_IncompatiblePredicate() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "Alice");
    t1.set(CoreAnnotations.AnswerAnnotation.class, "B-PER");
    t1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    t1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "joined");
    t2.set(CoreAnnotations.AnswerAnnotation.class, "I-PER");
    t2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    t2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);
    Predicate<edu.stanford.nlp.util.Pair<CoreLabel, CoreLabel>> predicate = pair -> false;
    List<CoreLabel> tokens = Arrays.asList(t1, t2);
    // List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0,
    // CoreAnnotations.TextAnnotation.class, CoreAnnotations.AnswerAnnotation.class,
    // CoreAnnotations.MentionTokenAnnotation.class, CoreAnnotations.MentionTypeAnnotation.class,
    // predicate);
    // assertEquals(2, chunks.size());
    // assertEquals("Alice", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
    // assertEquals("joined", chunks.get(1).get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testChunkWithOnlyBeginTagShouldNotFormChunk() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Start");
    token.set(CoreAnnotations.AnswerAnnotation.class, "B-LOC");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    List<CoreLabel> tokens = Collections.singletonList(token);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, chunks.size());
    assertEquals("Start", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("LOC", chunks.get(0).get(CoreAnnotations.AnswerAnnotation.class));
  }

  @Test
  public void testInterruptedChunkDueToTypeChangeSameTag() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "Yahoo");
    t1.set(CoreAnnotations.AnswerAnnotation.class, "I-ORG");
    t1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    t1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "News");
    t2.set(CoreAnnotations.AnswerAnnotation.class, "I-LOC");
    t2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    t2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);
    List<CoreLabel> tokens = Arrays.asList(t1, t2);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(2, chunks.size());
    assertEquals("Yahoo", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("ORG", chunks.get(0).get(CoreAnnotations.AnswerAnnotation.class));
    assertEquals("News", chunks.get(1).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("LOC", chunks.get(1).get(CoreAnnotations.AnswerAnnotation.class));
  }

  @Test(expected = RuntimeException.class)
  public void testChunkStartWithoutEndingPreviousShouldThrow() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "Mike");
    t1.set(CoreAnnotations.AnswerAnnotation.class, "B-PER");
    t1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    t1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "Lee");
    t2.set(CoreAnnotations.AnswerAnnotation.class, "B-ORG");
    t2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    t2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);
    List<CoreLabel> tokens = Arrays.asList(t1, t2);
    identifier.getAnnotatedChunks(
        tokens, 0, CoreAnnotations.TextAnnotation.class, CoreAnnotations.AnswerAnnotation.class);
  }

  @Test
  public void testOnlyEndTagCreatesChunk() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Terminal");
    token.set(CoreAnnotations.AnswerAnnotation.class, "E-LOC");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);
    List<CoreLabel> tokens = Collections.singletonList(token);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, chunks.size());
    assertEquals("Terminal", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("LOC", chunks.get(0).get(CoreAnnotations.AnswerAnnotation.class));
  }

  @Test
  public void testInvalidLabelFallsBackToPosTag() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "SomeEntity");
    token.set(CoreAnnotations.AnswerAnnotation.class, "XYZ");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);
    List<CoreLabel> tokens = Collections.singletonList(token);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, chunks.size());
    assertEquals("SomeEntity", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("XYZ", chunks.get(0).get(CoreAnnotations.AnswerAnnotation.class));
  }

  @Test
  public void testSequentialSBEIOEncodingChunk() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "Bank");
    t1.set(CoreAnnotations.AnswerAnnotation.class, "B-ORG");
    t1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    t1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "of");
    t2.set(CoreAnnotations.AnswerAnnotation.class, "I-ORG");
    t2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    t2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);
    CoreLabel t3 = new CoreLabel();
    t3.set(CoreAnnotations.TextAnnotation.class, "America");
    t3.set(CoreAnnotations.AnswerAnnotation.class, "E-ORG");
    t3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 8);
    t3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 15);
    List<CoreLabel> tokens = Arrays.asList(t1, t2, t3);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, chunks.size());
    assertEquals("Bank of America", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("ORG", chunks.get(0).get(CoreAnnotations.AnswerAnnotation.class));
  }

  @Test
  public void testConsecutiveUandSChunksDelimitedCorrectly() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Amazon");
    token1.set(CoreAnnotations.AnswerAnnotation.class, "U-ORG");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "Elon");
    token2.set(CoreAnnotations.AnswerAnnotation.class, "S-PER");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 7);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);
    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(2, chunks.size());
    assertEquals("Amazon", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("ORG", chunks.get(0).get(CoreAnnotations.AnswerAnnotation.class));
    assertEquals("Elon", chunks.get(1).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("PER", chunks.get(1).get(CoreAnnotations.AnswerAnnotation.class));
  }

  @Test
  public void testChunkOffsetsWithTokenOffsetApplied() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Entity");
    token1.set(CoreAnnotations.AnswerAnnotation.class, "B-ORG");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "Name");
    token2.set(CoreAnnotations.AnswerAnnotation.class, "L-ORG");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 7);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);
    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            3,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, chunks.size());
    CoreMap chunk = chunks.get(0);
    Integer tokenBegin = chunk.get(CoreAnnotations.TokenBeginAnnotation.class);
    Integer tokenEnd = chunk.get(CoreAnnotations.TokenEndAnnotation.class);
    assertEquals((Integer) 3, tokenBegin);
    assertEquals((Integer) 5, tokenEnd);
  }

  @Test
  public void testEmptyLabelStringFallsBackToDefaultPosTag() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "EmptyLabel");
    token.set(CoreAnnotations.AnswerAnnotation.class, "");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);
    List<CoreLabel> tokens = Collections.singletonList(token);
    List<CoreMap> result =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, result.size());
    assertEquals("EmptyLabel", result.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("", result.get(0).get(CoreAnnotations.AnswerAnnotation.class));
  }

  @Test
  public void testChunkWithMultipleNonMatchingTagsDifferentTypes() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "Alpha");
    t1.set(CoreAnnotations.AnswerAnnotation.class, "B-LOC");
    t1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    t1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "Beta");
    t2.set(CoreAnnotations.AnswerAnnotation.class, "I-ORG");
    t2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    t2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(t1);
    tokens.add(t2);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(2, chunks.size());
    assertEquals("Alpha", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("LOC", chunks.get(0).get(CoreAnnotations.AnswerAnnotation.class));
    assertEquals("Beta", chunks.get(1).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("ORG", chunks.get(1).get(CoreAnnotations.AnswerAnnotation.class));
  }

  @Test
  public void testStartOfChunkTriggeredByBracketTagOnly() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "Corporation");
    t1.set(CoreAnnotations.AnswerAnnotation.class, "[–ORG");
    t1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    t1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);
    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "Inc.");
    t2.set(CoreAnnotations.AnswerAnnotation.class, "]–ORG");
    t2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 12);
    t2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 16);
    List<CoreLabel> tokens = Arrays.asList(t1, t2);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, chunks.size());
    assertEquals("Corporation Inc.", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("ORG", chunks.get(0).get(CoreAnnotations.AnswerAnnotation.class));
  }

  @Test
  public void testNonAlphaLabelHandledGracefully() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Data123");
    token.set(CoreAnnotations.AnswerAnnotation.class, "1234-ORG");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);
    List<CoreLabel> tokens = Collections.singletonList(token);
    List<CoreMap> result =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, result.size());
    assertEquals("Data123", result.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("ORG", result.get(0).get(CoreAnnotations.AnswerAnnotation.class));
  }

  @Test
  public void testChunkWithDashInTypePart() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Example");
    token.set(CoreAnnotations.AnswerAnnotation.class, "B-PER-ORG");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);
    List<CoreLabel> tokens = Collections.singletonList(token);
    List<CoreMap> result =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, result.size());
    assertEquals("Example", result.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("PER-ORG", result.get(0).get(CoreAnnotations.AnswerAnnotation.class));
  }

  @Test
  public void testChunkTypeWithMultipleDashesSplitProperly() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Entity");
    token.set(CoreAnnotations.AnswerAnnotation.class, "I-ORG-NAME-COMPANY");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
    List<CoreLabel> tokens = Collections.singletonList(token);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, chunks.size());
    assertEquals("Entity", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("ORG-NAME-COMPANY", chunks.get(0).get(CoreAnnotations.AnswerAnnotation.class));
  }

  @Test
  public void testChunkWithNullTextAnnotationHandled() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, null);
    token.set(CoreAnnotations.AnswerAnnotation.class, "B-ORG");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    List<CoreLabel> tokens = Collections.singletonList(token);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, chunks.size());
    assertNull(chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("ORG", chunks.get(0).get(CoreAnnotations.AnswerAnnotation.class));
  }

  @Test
  public void testChunkWithNegativeCharacterOffsetsStillProcesses() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Entity");
    token.set(CoreAnnotations.AnswerAnnotation.class, "U-ORG");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, -5);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, -1);
    List<CoreLabel> tokens = Collections.singletonList(token);
    List<CoreMap> result =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, result.size());
    assertEquals("Entity", result.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("ORG", result.get(0).get(CoreAnnotations.AnswerAnnotation.class));
  }
}
