package edu.stanford.nlp.pipeline;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.Pair;
import java.util.*;
import java.util.function.Predicate;
import org.junit.Test;

public class LabeledChunkIdentifier_4_GPTLLMTest {

  @Test
  public void testSingleChunkWithoutLoop() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    // DummyCoreLabel token1 = new DummyCoreLabel();
    // DummyCoreLabel token2 = new DummyCoreLabel();
    // DummyCoreLabel token3 = new DummyCoreLabel();
    // token1.set(DummyTextAnnotation.class, "John");
    // token1.set(DummyLabelAnnotation.class, "B-PER");
    // token2.set(DummyTextAnnotation.class, "Smith");
    // token2.set(DummyLabelAnnotation.class, "I-PER");
    // token3.set(DummyTextAnnotation.class, "works");
    // token3.set(DummyLabelAnnotation.class, "O");
    // List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);
    // List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0, DummyTextAnnotation.class,
    // DummyLabelAnnotation.class, null, null, null);
    // assertEquals(1, chunks.size());
    // CoreMap chunk = chunks.get(0);
    // assertEquals("PER", chunk.get(DummyLabelAnnotation.class));
    // assertEquals("John Smith", chunk.get(DummyTextAnnotation.class));
  }

  @Test
  public void testNullLabelWithoutLoop() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    LabeledChunkIdentifier.LabelTagType result = identifier.getTagType(null);
    assertEquals("O", result.type);
    assertEquals("O", result.tag);
    assertEquals("O", result.label);
  }

  @Test
  public void testIgnoreProvidedTagTrue() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    identifier.setIgnoreProvidedTag(true);
    LabeledChunkIdentifier.LabelTagType result = identifier.getTagType("B-LOC");
    assertEquals("LOC", result.type);
    assertEquals("I", result.tag);
    assertEquals("B-LOC", result.label);
  }

  @Test
  public void testChunkWithCompatibilityPredicateFalse() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    // DummyCoreLabel token1 = new DummyCoreLabel();
    // token1.set(DummyTextAnnotation.class, "John");
    // token1.set(DummyLabelAnnotation.class, "B-PER");
    // DummyCoreLabel token2 = new DummyCoreLabel();
    // token2.set(DummyTextAnnotation.class, "Smith");
    // token2.set(DummyLabelAnnotation.class, "I-PER");
    // DummyCoreLabel token3 = new DummyCoreLabel();
    // token3.set(DummyTextAnnotation.class, "CEO");
    // token3.set(DummyLabelAnnotation.class, "B-TITLE");
    // DummyCoreLabel token4 = new DummyCoreLabel();
    // token4.set(DummyTextAnnotation.class, "Apple");
    // token4.set(DummyLabelAnnotation.class, "I-TITLE");
    // List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4);
    Predicate<Pair<CoreLabel, CoreLabel>> blockAll =
        new Predicate<Pair<CoreLabel, CoreLabel>>() {

          public boolean test(Pair<CoreLabel, CoreLabel> pair) {
            return false;
          }
        };
    // List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0, DummyTextAnnotation.class,
    // DummyLabelAnnotation.class, null, null, blockAll);
    // assertEquals(2, chunks.size());
    // assertEquals("John Smith", chunks.get(0).get(DummyTextAnnotation.class));
    // assertEquals("CEO Apple", chunks.get(1).get(DummyTextAnnotation.class));
  }

  @Test
  public void testLabelWithoutHyphen() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    LabeledChunkIdentifier.LabelTagType result = identifier.getTagType("PERSON");
    assertEquals("PERSON", result.type);
    assertEquals("I", result.tag);
    assertEquals("PERSON", result.label);
  }

  @Test(expected = RuntimeException.class)
  public void testOverlappingChunksThrowingException() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    // DummyCoreLabel token1 = new DummyCoreLabel();
    // token1.set(DummyTextAnnotation.class, "A");
    // token1.set(DummyLabelAnnotation.class, "B-PER");
    // DummyCoreLabel token2 = new DummyCoreLabel();
    // token2.set(DummyTextAnnotation.class, "B");
    // token2.set(DummyLabelAnnotation.class, "B-PER");
    // List<CoreLabel> tokens = Arrays.asList(token1, token2);
    // identifier.getAnnotatedChunks(tokens, 0, DummyTextAnnotation.class,
    // DummyLabelAnnotation.class, null, null, null);
  }

  @Test
  public void testDefaultPosNegTagConfiguration() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    identifier.setIgnoreProvidedTag(true);
    identifier.setDefaultPosTag("X");
    identifier.setDefaultNegTag("Z");
    identifier.setNegLabel("O");
    LabeledChunkIdentifier.LabelTagType tagType = identifier.getTagType("B-GPE");
    assertEquals("X", tagType.tag);
    assertEquals("GPE", tagType.type);
    assertEquals("B-GPE", tagType.label);
  }

  @Test
  public void testChunkWithSingleToken() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    // DummyCoreLabel token1 = new DummyCoreLabel();
    // token1.set(DummyTextAnnotation.class, "London");
    // token1.set(DummyLabelAnnotation.class, "U-LOC");
    // List<CoreLabel> tokens = Arrays.asList(token1);
    // List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0, DummyTextAnnotation.class,
    // DummyLabelAnnotation.class, null, null, null);
    // assertEquals(1, chunks.size());
    // CoreMap chunk = chunks.get(0);
    // assertEquals("LOC", chunk.get(DummyLabelAnnotation.class));
    // assertEquals("London", chunk.get(DummyTextAnnotation.class));
  }

  @Test
  public void testEmptyInputReturnsNoChunks() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    // List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0, DummyTextAnnotation.class,
    // DummyLabelAnnotation.class, null, null, null);
    // assertTrue(chunks.isEmpty());
  }

  @Test
  public void testLabelWithEmptyString() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    LabeledChunkIdentifier.LabelTagType tagType = identifier.getTagType("");
    assertEquals("", tagType.label);
    assertEquals("I", tagType.tag);
    assertEquals("", tagType.type);
  }

  @Test
  public void testLabelWithOnlyHyphen() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    LabeledChunkIdentifier.LabelTagType tagType = identifier.getTagType("-");
    assertEquals("-", tagType.label);
    assertEquals("", tagType.tag);
    assertEquals("", tagType.type);
  }

  @Test
  public void testSingleOOnlyToken() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    // DummyCoreLabel token = new DummyCoreLabel();
    // token.set(DummyTextAnnotation.class, "foo");
    // token.set(DummyLabelAnnotation.class, "O");
    // List<CoreLabel> tokens = Arrays.asList(token);
    // List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0, DummyTextAnnotation.class,
    // DummyLabelAnnotation.class, null, null, null);
    // assertTrue(chunks.isEmpty());
  }

  @Test
  public void testConsecutiveOChunks() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    // DummyCoreLabel token1 = new DummyCoreLabel();
    // token1.set(DummyTextAnnotation.class, "the");
    // token1.set(DummyLabelAnnotation.class, "O");
    // DummyCoreLabel token2 = new DummyCoreLabel();
    // token2.set(DummyTextAnnotation.class, "quick");
    // token2.set(DummyLabelAnnotation.class, "O");
    // DummyCoreLabel token3 = new DummyCoreLabel();
    // token3.set(DummyTextAnnotation.class, "fox");
    // token3.set(DummyLabelAnnotation.class, "O");
    // List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);
    // List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0, DummyTextAnnotation.class,
    // DummyLabelAnnotation.class, null, null, null);
    // assertEquals(0, chunks.size());
  }

  @Test
  public void testConsecutiveSingleTokenChunks() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    // DummyCoreLabel token1 = new DummyCoreLabel();
    // token1.set(DummyTextAnnotation.class, "Alice");
    // token1.set(DummyLabelAnnotation.class, "S-PER");
    // DummyCoreLabel token2 = new DummyCoreLabel();
    // token2.set(DummyTextAnnotation.class, "Bob");
    // token2.set(DummyLabelAnnotation.class, "S-PER");
    // List<CoreLabel> tokens = Arrays.asList(token1, token2);
    // List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0, DummyTextAnnotation.class,
    // DummyLabelAnnotation.class, null, null, null);
    // assertEquals(2, chunks.size());
    // assertEquals("Alice", chunks.get(0).get(DummyTextAnnotation.class));
    // assertEquals("Bob", chunks.get(1).get(DummyTextAnnotation.class));
  }

  @Test
  public void testChunkEndWithoutChunkStart() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    // DummyCoreLabel token1 = new DummyCoreLabel();
    // token1.set(DummyTextAnnotation.class, "unknown");
    // token1.set(DummyLabelAnnotation.class, "L-MISC");
    // List<CoreLabel> tokens = Arrays.asList(token1);
    // List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0, DummyTextAnnotation.class,
    // DummyLabelAnnotation.class, null, null, null);
    // assertEquals(1, chunks.size());
    // assertEquals("unknown", chunks.get(0).get(DummyTextAnnotation.class));
    // assertEquals("MISC", chunks.get(0).get(DummyLabelAnnotation.class));
  }

  @Test
  public void testStartOfChunkWithMismatchedType() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    boolean result =
        LabeledChunkIdentifier.isStartOfChunk(
            new LabeledChunkIdentifier.LabelTagType("B-ORG", "B", "ORG"),
            new LabeledChunkIdentifier.LabelTagType("I-PER", "I", "PER"));
    assertTrue(result);
  }

  @Test
  public void testEndOfChunkWithMismatchedType() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    boolean result =
        LabeledChunkIdentifier.isEndOfChunk(
            new LabeledChunkIdentifier.LabelTagType("I-LOC", "I", "LOC"),
            new LabeledChunkIdentifier.LabelTagType("I-ORG", "I", "ORG"));
    assertTrue(result);
  }

  @Test
  public void testGetAnnotatedChunksWithTokenChunkKeyAndLabelKey() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    // DummyCoreLabel token1 = new DummyCoreLabel();
    // token1.set(DummyTextAnnotation.class, "Company");
    // token1.set(DummyLabelAnnotation.class, "B-ORG");
    // DummyCoreLabel token2 = new DummyCoreLabel();
    // token2.set(DummyTextAnnotation.class, "Inc");
    // token2.set(DummyLabelAnnotation.class, "I-ORG");
    // List<CoreLabel> tokens = Arrays.asList(token1, token2);
    // List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0, DummyTextAnnotation.class,
    // DummyLabelAnnotation.class, String.class, Integer.class, null);
    // assertEquals(1, chunks.size());
    // assertEquals("Company Inc", chunks.get(0).get(DummyTextAnnotation.class));
  }

  @Test
  public void testStartAndEndWithBracketTags() {
    LabeledChunkIdentifier.LabelTagType prev =
        new LabeledChunkIdentifier.LabelTagType("[", "[", "ORG");
    LabeledChunkIdentifier.LabelTagType cur =
        new LabeledChunkIdentifier.LabelTagType("]", "]", "ORG");
    boolean started = LabeledChunkIdentifier.isStartOfChunk(prev, cur);
    boolean ended = LabeledChunkIdentifier.isEndOfChunk(prev, cur);
    assertTrue(started);
    assertTrue(ended);
  }

  @Test
  public void testLabelTagTypeTypeMatchesTrue() {
    LabeledChunkIdentifier.LabelTagType t1 =
        new LabeledChunkIdentifier.LabelTagType("B-LOC", "B", "LOC");
    LabeledChunkIdentifier.LabelTagType t2 =
        new LabeledChunkIdentifier.LabelTagType("I-LOC", "I", "LOC");
    assertTrue(t1.typeMatches(t2));
  }

  @Test
  public void testLabelTagTypeTypeMatchesFalse() {
    LabeledChunkIdentifier.LabelTagType t1 =
        new LabeledChunkIdentifier.LabelTagType("B-LOC", "B", "LOC");
    LabeledChunkIdentifier.LabelTagType t2 =
        new LabeledChunkIdentifier.LabelTagType("B-MISC", "B", "MISC");
    assertFalse(t1.typeMatches(t2));
  }

  @Test
  public void testGetTagTypeWithInvalidShapedLabel() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    LabeledChunkIdentifier.LabelTagType result = identifier.getTagType("XYZ--GPE");
    assertEquals("I", result.tag);
    assertEquals("XYZ--GPE", result.label);
    assertEquals("XYZ--GPE", result.type);
  }

  @Test
  public void testGetTagTypeWithOnlyDash() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    LabeledChunkIdentifier.LabelTagType result = identifier.getTagType("-");
    assertEquals("I", result.tag);
    assertEquals("-", result.label);
    assertEquals("-", result.type);
  }

  @Test
  public void testMultipleDifferentChunkTypesInSequence() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    // DummyCoreLabel t1 = new DummyCoreLabel();
    // t1.set(DummyTextAnnotation.class, "Alice");
    // t1.set(DummyLabelAnnotation.class, "B-PER");
    // DummyCoreLabel t2 = new DummyCoreLabel();
    // t2.set(DummyTextAnnotation.class, "Inc");
    // t2.set(DummyLabelAnnotation.class, "B-ORG");
    // DummyCoreLabel t3 = new DummyCoreLabel();
    // t3.set(DummyTextAnnotation.class, "Tokyo");
    // t3.set(DummyLabelAnnotation.class, "B-LOC");
    // List<CoreLabel> tokens = Arrays.asList(t1, t2, t3);
    // List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0, DummyTextAnnotation.class,
    // DummyLabelAnnotation.class, null, null, null);
    // assertEquals(3, chunks.size());
    // assertEquals("Alice", chunks.get(0).get(DummyTextAnnotation.class));
    // assertEquals("Inc", chunks.get(1).get(DummyTextAnnotation.class));
    // assertEquals("Tokyo", chunks.get(2).get(DummyTextAnnotation.class));
  }

  @Test
  public void testLastTokenFormsSingleChunk() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    // DummyCoreLabel t1 = new DummyCoreLabel();
    // t1.set(DummyTextAnnotation.class, "Bob");
    // t1.set(DummyLabelAnnotation.class, "O");
    // DummyCoreLabel t2 = new DummyCoreLabel();
    // t2.set(DummyTextAnnotation.class, "BMW");
    // t2.set(DummyLabelAnnotation.class, "B-ORG");
    // List<CoreLabel> tokens = Arrays.asList(t1, t2);
    // List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0, DummyTextAnnotation.class,
    // DummyLabelAnnotation.class, null, null, null);
    // assertEquals(1, chunks.size());
    // assertEquals("BMW", chunks.get(0).get(DummyTextAnnotation.class));
    // assertEquals("ORG", chunks.get(0).get(DummyLabelAnnotation.class));
  }

  @Test
  public void testIsEndOfChunkWithNullPrevReturnsFalse() {
    boolean result =
        LabeledChunkIdentifier.isEndOfChunk(
            null, new LabeledChunkIdentifier.LabelTagType("B-ORG", "B", "ORG"));
    assertFalse(result);
  }

  @Test
  public void testIsStartOfChunkBAfterI() {
    boolean result =
        LabeledChunkIdentifier.isStartOfChunk(
            new LabeledChunkIdentifier.LabelTagType("I-ORG", "I", "ORG"),
            new LabeledChunkIdentifier.LabelTagType("B-ORG", "B", "ORG"));
    assertTrue(result);
  }

  @Test
  public void testIsStartOfChunkWithNullPrevTypeAndDifferentCurType() {
    boolean result =
        LabeledChunkIdentifier.isStartOfChunk(
            null, new LabeledChunkIdentifier.LabelTagType("B-MISC", "B", "MISC"));
    assertTrue(result);
  }

  @Test
  public void testIOEEncoding_ChunkFromIToE() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    // DummyCoreLabel t1 = new DummyCoreLabel();
    // t1.set(DummyTextAnnotation.class, "New");
    // t1.set(DummyLabelAnnotation.class, "I-LOC");
    // DummyCoreLabel t2 = new DummyCoreLabel();
    // t2.set(DummyTextAnnotation.class, "York");
    // t2.set(DummyLabelAnnotation.class, "E-LOC");
    // List<CoreLabel> tokens = Arrays.asList(t1, t2);
    // List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0, DummyTextAnnotation.class,
    // DummyLabelAnnotation.class, null, null, null);
    // assertEquals(1, chunks.size());
    // assertEquals("New York", chunks.get(0).get(DummyTextAnnotation.class));
    // assertEquals("LOC", chunks.get(0).get(DummyLabelAnnotation.class));
  }

  @Test
  public void testLabelWithMultipleHyphens() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    LabeledChunkIdentifier.LabelTagType tag = identifier.getTagType("X-Y-Z-LABEL");
    assertEquals("X", tag.tag);
    assertEquals("Y-Z-LABEL", tag.type);
    assertEquals("X-Y-Z-LABEL", tag.label);
  }

  @Test
  public void testMalformedTokenLabelTreatsWholeAsType() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    LabeledChunkIdentifier.LabelTagType tag = identifier.getTagType("MALFORMED_LABEL_NO_HYPHEN");
    assertEquals("MALFORMED_LABEL_NO_HYPHEN", tag.label);
    assertEquals("MALFORMED_LABEL_NO_HYPHEN", tag.type);
    assertEquals("I", tag.tag);
  }

  @Test
  public void testChunkWithOnlyStartAndNoMatchingEndStillStored() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    // DummyCoreLabel token1 = new DummyCoreLabel();
    // token1.set(DummyTextAnnotation.class, "Alpha");
    // token1.set(DummyLabelAnnotation.class, "B-ORG");
    // DummyCoreLabel token2 = new DummyCoreLabel();
    // token2.set(DummyTextAnnotation.class, "Omega");
    // token2.set(DummyLabelAnnotation.class, "I-PER");
    // List<CoreLabel> tokens = Arrays.asList(token1, token2);
    // List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0, DummyTextAnnotation.class,
    // DummyLabelAnnotation.class, null, null, null);
    // assertEquals(1, chunks.size());
    // assertEquals("Alpha", chunks.get(0).get(DummyTextAnnotation.class));
    // assertEquals("ORG", chunks.get(0).get(DummyLabelAnnotation.class));
  }

  @Test
  public void testOnlyOTypeTokensIgnoredDespiteWeirdTagFormat() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    // DummyCoreLabel token1 = new DummyCoreLabel();
    // token1.set(DummyTextAnnotation.class, "test");
    // token1.set(DummyLabelAnnotation.class, "X-O");
    // List<CoreLabel> tokens = Arrays.asList(token1);
    // List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0, DummyTextAnnotation.class,
    // DummyLabelAnnotation.class, null, null, null);
    // assertEquals(0, chunks.size());
  }

  @Test
  public void testIsChunkReturnsFalseForTagOAndDot() {
    LabeledChunkIdentifier.LabelTagType cur1 =
        new LabeledChunkIdentifier.LabelTagType("O", "O", "O");
    LabeledChunkIdentifier.LabelTagType cur2 =
        new LabeledChunkIdentifier.LabelTagType(".", ".", ".");
    assertFalse(LabeledChunkIdentifier.isStartOfChunk(null, cur1));
    assertFalse(LabeledChunkIdentifier.isStartOfChunk(null, cur2));
    assertFalse(LabeledChunkIdentifier.isEndOfChunk(null, cur1));
    assertFalse(LabeledChunkIdentifier.isEndOfChunk(null, cur2));
  }

  @Test
  public void testHandlingOfNullTokenInList() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    // DummyCoreLabel token1 = new DummyCoreLabel();
    // token1.set(DummyTextAnnotation.class, "Paris");
    // token1.set(DummyLabelAnnotation.class, "B-LOC");
    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
    // tokens.add(token1);
    tokens.add(null);
    try {
      // identifier.getAnnotatedChunks(tokens, 0, DummyTextAnnotation.class,
      // DummyLabelAnnotation.class, null, null, null);
      fail("Expected NullPointerException due to null token");
    } catch (NullPointerException expected) {
    }
  }

  @Test
  public void testIsStartOfChunkWithDifferentTypeAndTagI() {
    LabeledChunkIdentifier.LabelTagType prev =
        new LabeledChunkIdentifier.LabelTagType("I-ORG", "I", "ORG");
    LabeledChunkIdentifier.LabelTagType cur =
        new LabeledChunkIdentifier.LabelTagType("I-LOC", "I", "LOC");
    assertTrue(LabeledChunkIdentifier.isStartOfChunk(prev, cur));
  }

  @Test
  public void testStartAndEndOfChunkWithSquareBrackets() {
    LabeledChunkIdentifier.LabelTagType prev =
        new LabeledChunkIdentifier.LabelTagType("[", "[", "ORG");
    LabeledChunkIdentifier.LabelTagType cur =
        new LabeledChunkIdentifier.LabelTagType("]", "]", "ORG");
    assertTrue(LabeledChunkIdentifier.isStartOfChunk(prev, cur));
    assertTrue(LabeledChunkIdentifier.isEndOfChunk(prev, cur));
  }

  @Test
  public void testSBEIOEncodingRecognizedAsStartAndEnd() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    // DummyCoreLabel token1 = new DummyCoreLabel();
    // token1.set(DummyTextAnnotation.class, "John");
    // token1.set(DummyLabelAnnotation.class, "S-PER");
    // DummyCoreLabel token2 = new DummyCoreLabel();
    // token2.set(DummyTextAnnotation.class, "Developer");
    // token2.set(DummyLabelAnnotation.class, "B-TITLE");
    // DummyCoreLabel token3 = new DummyCoreLabel();
    // token3.set(DummyTextAnnotation.class, "Oracle");
    // token3.set(DummyLabelAnnotation.class, "E-TITLE");
    // List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);
    // List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0, DummyTextAnnotation.class,
    // DummyLabelAnnotation.class, null, null, null);
    // assertEquals(2, chunks.size());
    // assertEquals("John", chunks.get(0).get(DummyTextAnnotation.class));
    // assertEquals("PER", chunks.get(0).get(DummyLabelAnnotation.class));
    // assertEquals("Developer Oracle", chunks.get(1).get(DummyTextAnnotation.class));
    // assertEquals("TITLE", chunks.get(1).get(DummyLabelAnnotation.class));
  }

  @Test
  public void testDefaultNegTagAppliedWhenIgnoreProvidedTagAndNegLabelMatched() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    identifier.setDefaultNegTag("ZZ");
    identifier.setDefaultPosTag("XX");
    identifier.setIgnoreProvidedTag(true);
    identifier.setNegLabel("OFF");
    LabeledChunkIdentifier.LabelTagType tagType = identifier.getTagType("X-OFF");
    assertEquals("OFF", tagType.type);
    assertEquals("ZZ", tagType.tag);
    assertEquals("X-OFF", tagType.label);
  }

  @Test
  public void testEndOfChunkDiffTypeSameTag() {
    LabeledChunkIdentifier.LabelTagType prev =
        new LabeledChunkIdentifier.LabelTagType("I-PER", "I", "PER");
    LabeledChunkIdentifier.LabelTagType cur =
        new LabeledChunkIdentifier.LabelTagType("I-ORG", "I", "ORG");
    boolean ended = LabeledChunkIdentifier.isEndOfChunk(prev, cur);
    assertTrue(ended);
  }

  @Test
  public void testStartOfChunkSameTagDiffType() {
    LabeledChunkIdentifier.LabelTagType prev =
        new LabeledChunkIdentifier.LabelTagType("I-PER", "I", "PER");
    LabeledChunkIdentifier.LabelTagType cur =
        new LabeledChunkIdentifier.LabelTagType("I-LOC", "I", "LOC");
    boolean started = LabeledChunkIdentifier.isStartOfChunk(prev, cur);
    assertTrue(started);
  }

  @Test
  public void testDotsAndOFilteredByIsChunk() {
    LabeledChunkIdentifier.LabelTagType tagO =
        new LabeledChunkIdentifier.LabelTagType("O", "O", "O");
    LabeledChunkIdentifier.LabelTagType tagDot =
        new LabeledChunkIdentifier.LabelTagType(".", ".", ".");
    boolean resO = LabeledChunkIdentifier.isStartOfChunk(null, tagO);
    boolean resDot = LabeledChunkIdentifier.isStartOfChunk(null, tagDot);
    assertFalse(resO);
    assertFalse(resDot);
  }

  @Test
  public void testUnclosedChunkAtEndOfListIsCaptured() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    // DummyCoreLabel t1 = new DummyCoreLabel();
    // t1.set(DummyTextAnnotation.class, "hello");
    // t1.set(DummyLabelAnnotation.class, "B-GREET");
    // DummyCoreLabel t2 = new DummyCoreLabel();
    // t2.set(DummyTextAnnotation.class, "world");
    // t2.set(DummyLabelAnnotation.class, "I-GREET");
    // List<CoreLabel> tokens = Arrays.asList(t1, t2);
    // List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0, DummyTextAnnotation.class,
    // DummyLabelAnnotation.class, null, null, null);
    // assertEquals(1, chunks.size());
    // assertEquals("GREET", chunks.get(0).get(DummyLabelAnnotation.class));
    // assertEquals("hello world", chunks.get(0).get(DummyTextAnnotation.class));
  }

  @Test
  public void testSingleDotLabelIgnored() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    // DummyCoreLabel dot = new DummyCoreLabel();
    // dot.set(DummyTextAnnotation.class, ".");
    // dot.set(DummyLabelAnnotation.class, ".");
    // List<CoreLabel> tokens = Arrays.asList(dot);
    // List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0, DummyTextAnnotation.class,
    // DummyLabelAnnotation.class, null, null, null);
    // assertEquals(0, chunks.size());
  }

  @Test
  public void testConsecutiveChunkWithTypeSwitchMidChunk() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    // DummyCoreLabel t1 = new DummyCoreLabel();
    // t1.set(DummyTextAnnotation.class, "Steve");
    // t1.set(DummyLabelAnnotation.class, "B-PER");
    // DummyCoreLabel t2 = new DummyCoreLabel();
    // t2.set(DummyTextAnnotation.class, "Jobs");
    // t2.set(DummyLabelAnnotation.class, "I-PER");
    // DummyCoreLabel t3 = new DummyCoreLabel();
    // t3.set(DummyTextAnnotation.class, "Apple");
    // t3.set(DummyLabelAnnotation.class, "I-ORG");
    // List<CoreLabel> tokens = Arrays.asList(t1, t2, t3);
    // List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0, DummyTextAnnotation.class,
    // DummyLabelAnnotation.class, null, null, null);
    // assertEquals(1, chunks.size());
    // assertEquals("Steve Jobs", chunks.get(0).get(DummyTextAnnotation.class));
    // assertEquals("PER", chunks.get(0).get(DummyLabelAnnotation.class));
  }

  @Test
  public void testMultipleUChunkTagsInSequence() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    // DummyCoreLabel t1 = new DummyCoreLabel();
    // t1.set(DummyTextAnnotation.class, "IBM");
    // t1.set(DummyLabelAnnotation.class, "U-ORG");
    // DummyCoreLabel t2 = new DummyCoreLabel();
    // t2.set(DummyTextAnnotation.class, "Google");
    // t2.set(DummyLabelAnnotation.class, "U-ORG");
    // List<CoreLabel> tokens = Arrays.asList(t1, t2);
    // List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0, DummyTextAnnotation.class,
    // DummyLabelAnnotation.class, null, null, null);
    // assertEquals(2, chunks.size());
    // assertEquals("IBM", chunks.get(0).get(DummyTextAnnotation.class));
    // assertEquals("Google", chunks.get(1).get(DummyTextAnnotation.class));
  }

  @Test
  public void testMixedBracketStyleTags() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    // DummyCoreLabel token1 = new DummyCoreLabel();
    // token1.set(DummyTextAnnotation.class, "Open");
    // token1.set(DummyLabelAnnotation.class, "[--GROUP");
    // DummyCoreLabel token2 = new DummyCoreLabel();
    // token2.set(DummyTextAnnotation.class, "AI");
    // token2.set(DummyLabelAnnotation.class, "]--GROUP");
    LabeledChunkIdentifier.LabelTagType prev = identifier.getTagType("[--GROUP");
    LabeledChunkIdentifier.LabelTagType cur = identifier.getTagType("]--GROUP");
    boolean start = LabeledChunkIdentifier.isStartOfChunk(prev, cur);
    boolean end = LabeledChunkIdentifier.isEndOfChunk(prev, cur);
    assertTrue(start);
    assertTrue(end);
  }

  @Test
  public void testTokenWithNullTextStillReturnsChunk() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    // DummyCoreLabel token1 = new DummyCoreLabel();
    // token1.set(DummyTextAnnotation.class, null);
    // token1.set(DummyLabelAnnotation.class, "B-TYPE");
    // DummyCoreLabel token2 = new DummyCoreLabel();
    // token2.set(DummyTextAnnotation.class, "data");
    // token2.set(DummyLabelAnnotation.class, "I-TYPE");
    // List<CoreLabel> tokens = Arrays.asList(token1, token2);
    // List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0, DummyTextAnnotation.class,
    // DummyLabelAnnotation.class, null, null, null);
    // assertEquals(1, chunks.size());
    // Object label = chunks.get(0).get(DummyLabelAnnotation.class);
    // assertEquals("TYPE", label);
  }

  @Test
  public void testTypeChangeFromInsideChunkTriggersSplit() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    // DummyCoreLabel token1 = new DummyCoreLabel();
    // token1.set(DummyTextAnnotation.class, "United");
    // token1.set(DummyLabelAnnotation.class, "B-ORG");
    // DummyCoreLabel token2 = new DummyCoreLabel();
    // token2.set(DummyTextAnnotation.class, "Nations");
    // token2.set(DummyLabelAnnotation.class, "I-LOC");
    // List<CoreLabel> tokens = Arrays.asList(token1, token2);
    // List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0, DummyTextAnnotation.class,
    // DummyLabelAnnotation.class, null, null, null);
    // assertEquals(1, chunks.size());
    // assertEquals("United", chunks.get(0).get(DummyTextAnnotation.class));
    // assertEquals("ORG", chunks.get(0).get(DummyLabelAnnotation.class));
  }

  @Test
  public void testLabelOnlyTagDashDashFormat() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    LabeledChunkIdentifier.LabelTagType result = identifier.getTagType("-PERSON");
    assertEquals("-", result.tag);
    assertEquals("PERSON", result.type);
    assertEquals("-PERSON", result.label);
  }

  @Test
  public void testLabelOnlyWithTrailingDash() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    LabeledChunkIdentifier.LabelTagType result = identifier.getTagType("B-");
    assertEquals("B", result.tag);
    assertEquals("", result.type);
    assertEquals("B-", result.label);
  }

  @Test
  public void testEmptyLabelReturnsDefaultPosTagWhenNotNeg() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    identifier.setNegLabel("X");
    LabeledChunkIdentifier.LabelTagType result = identifier.getTagType("");
    assertEquals("", result.label);
    assertEquals("", result.type);
    assertEquals("I", result.tag);
  }

  @Test
  public void testChunkStartingWithOnlyIOWithoutBStillGroupsChunk() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    // DummyCoreLabel token1 = new DummyCoreLabel();
    // token1.set(DummyTextAnnotation.class, "New");
    // token1.set(DummyLabelAnnotation.class, "I-LOC");
    // DummyCoreLabel token2 = new DummyCoreLabel();
    // token2.set(DummyTextAnnotation.class, "York");
    // token2.set(DummyLabelAnnotation.class, "I-LOC");
    // List<CoreLabel> tokens = Arrays.asList(token1, token2);
    // List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0, DummyTextAnnotation.class,
    // DummyLabelAnnotation.class, null, null, null);
    // assertEquals(1, chunks.size());
    // assertEquals("New York", chunks.get(0).get(DummyTextAnnotation.class));
    // assertEquals("LOC", chunks.get(0).get(DummyLabelAnnotation.class));
  }

  @Test
  public void testChunkWithLeadingOAndFollowingICreatesChunk() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    // DummyCoreLabel token1 = new DummyCoreLabel();
    // token1.set(DummyTextAnnotation.class, "Hello");
    // token1.set(DummyLabelAnnotation.class, "O");
    // DummyCoreLabel token2 = new DummyCoreLabel();
    // token2.set(DummyTextAnnotation.class, "World");
    // token2.set(DummyLabelAnnotation.class, "I-GREET");
    // List<CoreLabel> tokens = Arrays.asList(token1, token2);
    // List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0, DummyTextAnnotation.class,
    // DummyLabelAnnotation.class, null, null, null);
    // assertEquals(1, chunks.size());
    // assertEquals("World", chunks.get(0).get(DummyTextAnnotation.class));
    // assertEquals("GREET", chunks.get(0).get(DummyLabelAnnotation.class));
  }

  @Test(expected = RuntimeException.class)
  public void testRejectsBackToBackBTagsWithoutClosingChunk() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    // DummyCoreLabel token1 = new DummyCoreLabel();
    // token1.set(DummyTextAnnotation.class, "Alpha");
    // token1.set(DummyLabelAnnotation.class, "B-PER");
    // DummyCoreLabel token2 = new DummyCoreLabel();
    // token2.set(DummyTextAnnotation.class, "Bravo");
    // token2.set(DummyLabelAnnotation.class, "B-LOC");
    // List<CoreLabel> tokens = Arrays.asList(token1, token2);
    // identifier.getAnnotatedChunks(tokens, 0, DummyTextAnnotation.class,
    // DummyLabelAnnotation.class, null, null, null);
  }

  @Test
  public void testChunkWithUnknownTagCharStillScans() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    // DummyCoreLabel token1 = new DummyCoreLabel();
    // token1.set(DummyTextAnnotation.class, "Label");
    // token1.set(DummyLabelAnnotation.class, "?-THING");
    // DummyCoreLabel token2 = new DummyCoreLabel();
    // token2.set(DummyTextAnnotation.class, "X");
    // token2.set(DummyLabelAnnotation.class, "?-THING");
    // List<CoreLabel> tokens = Arrays.asList(token1, token2);
    // List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0, DummyTextAnnotation.class,
    // DummyLabelAnnotation.class, null, null, null);
    // assertEquals(1, chunks.size());
    // assertEquals("Label X", chunks.get(0).get(DummyTextAnnotation.class));
    // assertEquals("THING", chunks.get(0).get(DummyLabelAnnotation.class));
  }

  @Test
  public void testIOPatternEndsChunkWhenNextChunkHasDifferentType() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    // DummyCoreLabel token1 = new DummyCoreLabel();
    // token1.set(DummyTextAnnotation.class, "A");
    // token1.set(DummyLabelAnnotation.class, "I-LOC");
    // DummyCoreLabel token2 = new DummyCoreLabel();
    // token2.set(DummyTextAnnotation.class, "B");
    // token2.set(DummyLabelAnnotation.class, "I-ORG");
    // List<CoreLabel> tokens = Arrays.asList(token1, token2);
    // List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0, DummyTextAnnotation.class,
    // DummyLabelAnnotation.class, null, null, null);
    // assertEquals(1, chunks.size());
    // assertEquals("A", chunks.get(0).get(DummyTextAnnotation.class));
    // assertEquals("LOC", chunks.get(0).get(DummyLabelAnnotation.class));
  }

  @Test
  public void testStartOfChunkWhenPrevIsOandCurTagIsI() {
    boolean result =
        LabeledChunkIdentifier.isStartOfChunk(
            new LabeledChunkIdentifier.LabelTagType("O", "O", "O"),
            new LabeledChunkIdentifier.LabelTagType("I-ENT", "I", "ENT"));
    assertTrue(result);
  }

  @Test
  public void testChunkEndsProperlyOnNextTokenOType() {
    LabeledChunkIdentifier.LabelTagType prev =
        new LabeledChunkIdentifier.LabelTagType("I-MISC", "I", "MISC");
    LabeledChunkIdentifier.LabelTagType cur =
        new LabeledChunkIdentifier.LabelTagType("O", "O", "O");
    boolean result = LabeledChunkIdentifier.isEndOfChunk(prev, cur);
    assertTrue(result);
  }

  @Test
  public void testGetTagTypeWhenProvidedTagIsIgnoredAndMatchesNegLabel() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    identifier.setNegLabel("Z");
    identifier.setDefaultNegTag("N");
    identifier.setDefaultPosTag("P");
    identifier.setIgnoreProvidedTag(true);
    LabeledChunkIdentifier.LabelTagType result = identifier.getTagType("X-Z");
    assertEquals("Z", result.type);
    assertEquals("N", result.tag);
    assertEquals("X-Z", result.label);
  }

  @Test
  public void testDoubleHyphenDelimitedLabelPatternMatch() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    LabeledChunkIdentifier.LabelTagType result = identifier.getTagType("U--LABEL");
    assertEquals("U", result.tag);
    assertEquals("-LABEL", result.type);
    assertEquals("U--LABEL", result.label);
  }

  @Test
  public void testChunkStartImmediatelyAfterDifferentTagDifferentType() {
    LabeledChunkIdentifier.LabelTagType prev =
        new LabeledChunkIdentifier.LabelTagType("I-ORG", "I", "ORG");
    LabeledChunkIdentifier.LabelTagType cur =
        new LabeledChunkIdentifier.LabelTagType("B-MISC", "B", "MISC");
    boolean result = LabeledChunkIdentifier.isStartOfChunk(prev, cur);
    assertTrue(result);
  }

  @Test
  public void testChunkEndBetweenMatchingTagsButDifferentTypes() {
    LabeledChunkIdentifier.LabelTagType prev =
        new LabeledChunkIdentifier.LabelTagType("I-LOC", "I", "LOC");
    LabeledChunkIdentifier.LabelTagType cur =
        new LabeledChunkIdentifier.LabelTagType("I-PER", "I", "PER");
    boolean result = LabeledChunkIdentifier.isEndOfChunk(prev, cur);
    assertTrue(result);
  }

  @Test
  public void testGetAnnotatedChunksWithNullCheckPredicateReturnsNoSplit() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    // DummyCoreLabel token1 = new DummyCoreLabel();
    // token1.set(DummyTextAnnotation.class, "Hello");
    // token1.set(DummyLabelAnnotation.class, "B-GREET");
    // DummyCoreLabel token2 = new DummyCoreLabel();
    // token2.set(DummyTextAnnotation.class, "World");
    // token2.set(DummyLabelAnnotation.class, "I-GREET");
    Predicate<edu.stanford.nlp.util.Pair<CoreLabel, CoreLabel>> alwaysTrue =
        new Predicate<edu.stanford.nlp.util.Pair<CoreLabel, CoreLabel>>() {

          public boolean test(edu.stanford.nlp.util.Pair<CoreLabel, CoreLabel> p) {
            return true;
          }
        };
    // List<CoreLabel> tokens = Arrays.asList(token1, token2);
    // List<CoreMap> chunks = identifier.getAnnotatedChunks(tokens, 0, DummyTextAnnotation.class,
    // DummyLabelAnnotation.class, null, null, alwaysTrue);
    // assertEquals(1, chunks.size());
    // assertEquals("Hello World", chunks.get(0).get(DummyTextAnnotation.class));
    // assertEquals("GREET", chunks.get(0).get(DummyLabelAnnotation.class));
  }

  @Test
  public void testChunkSplitsWhenCompatibilityCheckerFailsMidChunk() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    // DummyCoreLabel token1 = new DummyCoreLabel();
    // token1.set(DummyTextAnnotation.class, "Alpha");
    // token1.set(DummyLabelAnnotation.class, "B-Z");
    // DummyCoreLabel token2 = new DummyCoreLabel();
    // token2.set(DummyTextAnnotation.class, "Beta");
    // token2.set(DummyLabelAnnotation.class, "I-Z");
    Predicate<edu.stanford.nlp.util.Pair<CoreLabel, CoreLabel>> alwaysFalse =
        new Predicate<edu.stanford.nlp.util.Pair<CoreLabel, CoreLabel>>() {

          public boolean test(edu.stanford.nlp.util.Pair<CoreLabel, CoreLabel> p) {
            return false;
          }
        };
    // List<CoreLabel> tokens = Arrays.asList(token1, token2);
    // List<CoreMap> result = identifier.getAnnotatedChunks(tokens, 0, DummyTextAnnotation.class,
    // DummyLabelAnnotation.class, null, null, alwaysFalse);
    // assertEquals(1, result.size());
    // assertEquals("Alpha", result.get(0).get(DummyTextAnnotation.class));
  }

  @Test
  public void testLastTokenITypeIsNotFlushedIfIncompatible() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    // DummyCoreLabel token1 = new DummyCoreLabel();
    // token1.set(DummyTextAnnotation.class, "Gamma");
    // token1.set(DummyLabelAnnotation.class, "B-X");
    // DummyCoreLabel token2 = new DummyCoreLabel();
    // token2.set(DummyTextAnnotation.class, "Delta");
    // token2.set(DummyLabelAnnotation.class, "I-X");
    // DummyCoreLabel token3 = new DummyCoreLabel();
    // token3.set(DummyTextAnnotation.class, "Zeta");
    // token3.set(DummyLabelAnnotation.class, "I-X");
    Predicate<edu.stanford.nlp.util.Pair<CoreLabel, CoreLabel>> blockLast =
        new Predicate<edu.stanford.nlp.util.Pair<CoreLabel, CoreLabel>>() {

          public boolean test(edu.stanford.nlp.util.Pair<CoreLabel, CoreLabel> p) {
            CoreLabel cur = p.first();
            CoreLabel prev = p.second();
            // if (cur.get(DummyTextAnnotation.class).equals("Zeta")) {
            // return false;
            // }
            return true;
          }
        };
    // List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);
    // List<CoreMap> result = identifier.getAnnotatedChunks(tokens, 0, DummyTextAnnotation.class,
    // DummyLabelAnnotation.class, null, null, blockLast);
    // assertEquals(1, result.size());
    // assertEquals("Gamma Delta", result.get(0).get(DummyTextAnnotation.class));
  }

  @Test
  public void testTokenWithNullLabelUsesNegDefaults() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    identifier.setNegLabel("O");
    identifier.setDefaultNegTag("O");
    // DummyCoreLabel token = new DummyCoreLabel();
    // token.set(DummyTextAnnotation.class, "NullLabel");
    // token.set(DummyLabelAnnotation.class, null);
    // List<CoreLabel> tokens = Arrays.asList(token);
    // List<CoreMap> result = identifier.getAnnotatedChunks(tokens, 0, DummyTextAnnotation.class,
    // DummyLabelAnnotation.class, null, null, null);
    // assertEquals(0, result.size());
  }

  @Test
  public void testLongBILOUSequenceCorrectlyExtractsTwoChunks() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    // DummyCoreLabel t1 = new DummyCoreLabel();
    // t1.set(DummyTextAnnotation.class, "A");
    // t1.set(DummyLabelAnnotation.class, "B-ENT");
    // DummyCoreLabel t2 = new DummyCoreLabel();
    // t2.set(DummyTextAnnotation.class, "B");
    // t2.set(DummyLabelAnnotation.class, "I-ENT");
    // DummyCoreLabel t3 = new DummyCoreLabel();
    // t3.set(DummyTextAnnotation.class, "C");
    // t3.set(DummyLabelAnnotation.class, "L-ENT");
    // DummyCoreLabel t4 = new DummyCoreLabel();
    // t4.set(DummyTextAnnotation.class, "D");
    // t4.set(DummyLabelAnnotation.class, "U-ENT");
    // List<CoreLabel> tokens = Arrays.asList(t1, t2, t3, t4);
    // List<CoreMap> result = identifier.getAnnotatedChunks(tokens, 0, DummyTextAnnotation.class,
    // DummyLabelAnnotation.class, null, null, null);
    // assertEquals(2, result.size());
    // assertEquals("A B C", result.get(0).get(DummyTextAnnotation.class));
    // assertEquals("D", result.get(1).get(DummyTextAnnotation.class));
  }
}
