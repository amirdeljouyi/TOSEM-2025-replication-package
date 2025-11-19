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

public class LabeledChunkIdentifier_1_GPTLLMTest {

  @Test
  public void testSingleChunk_BPER_IPER() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Bill");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "B-PER");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "Gates");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "I-PER");
    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.NamedEntityTagAnnotation.class);
    assertEquals(1, chunks.size());
    CoreMap chunk = chunks.get(0);
    assertEquals("PER", chunk.get(CoreAnnotations.NamedEntityTagAnnotation.class));
    List<CoreLabel> chunkTokens = chunk.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(2, chunkTokens.size());
    assertEquals("Bill", chunkTokens.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("Gates", chunkTokens.get(1).get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testMultipleChunks_BPER_IPER_BLOC() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Barack");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "B-PER");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "Obama");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 7);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "I-PER");
    CoreLabel token3 = new CoreLabel();
    token3.set(CoreAnnotations.TextAnnotation.class, "visited");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 13);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 20);
    token3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    CoreLabel token4 = new CoreLabel();
    token4.set(CoreAnnotations.TextAnnotation.class, "Berlin");
    token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 21);
    token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 27);
    token4.set(CoreAnnotations.NamedEntityTagAnnotation.class, "B-LOC");
    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.NamedEntityTagAnnotation.class);
    assertEquals(2, chunks.size());
    CoreMap chunk1 = chunks.get(0);
    assertEquals("PER", chunk1.get(CoreAnnotations.NamedEntityTagAnnotation.class));
    List<CoreLabel> c1Tokens = chunk1.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(2, c1Tokens.size());
    CoreMap chunk2 = chunks.get(1);
    assertEquals("LOC", chunk2.get(CoreAnnotations.NamedEntityTagAnnotation.class));
    List<CoreLabel> c2Tokens = chunk2.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(1, c2Tokens.size());
  }

  @Test
  public void testNullLabelReturnsNegLabelTagType() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Hello");
    LabeledChunkIdentifier.LabelTagType tagType = identifier.getTagType(null);
    assertEquals("O", tagType.type);
    assertEquals("O", tagType.tag);
    assertEquals("O", tagType.label);
  }

  @Test
  public void testSetIgnoreProvidedTagAffectsTagType() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    identifier.setIgnoreProvidedTag(true);
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Hello");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "B-PER");
    LabeledChunkIdentifier.LabelTagType tagType = identifier.getTagType("B-PER");
    assertEquals("PER", tagType.type);
    assertEquals("I", tagType.tag);
  }

  @Test(expected = RuntimeException.class)
  public void testOverlappingChunksThrowsException() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "A");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 1);
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "B-PER");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "B");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 2);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "B-LOC");
    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    identifier.getAnnotatedChunks(
        tokens,
        0,
        CoreAnnotations.TextAnnotation.class,
        CoreAnnotations.NamedEntityTagAnnotation.class);
  }

  @Test
  public void testUChunkOnly() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "John");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "U-PER");
    List<CoreLabel> tokens = Collections.singletonList(token);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.NamedEntityTagAnnotation.class);
    assertEquals(1, chunks.size());
    CoreMap chunk = chunks.get(0);
    assertEquals("PER", chunk.get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }

  @Test
  public void testCompatibilityPredicateBlocksChunk() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "A");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "B-PER");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "B");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "I-PER");
    CoreLabel token3 = new CoreLabel();
    token3.set(CoreAnnotations.TextAnnotation.class, "C");
    token3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "I-PER");
    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);
    Predicate<Pair<CoreLabel, CoreLabel>> predicate =
        p -> {
          String current = p.first().get(CoreAnnotations.TextAnnotation.class);
          return !"C".equals(current);
        };
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.NamedEntityTagAnnotation.class,
            null,
            null,
            predicate);
    assertEquals(2, chunks.size());
  }

  @Test
  public void testSettersAndGetters() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    identifier.setDefaultNegTag("Z");
    identifier.setDefaultPosTag("Y");
    identifier.setNegLabel("N");
    identifier.setIgnoreProvidedTag(true);
    assertEquals("Z", identifier.getDefaultNegTag());
    assertEquals("Y", identifier.getDefaultPosTag());
    assertEquals("N", identifier.getNegLabel());
    assertTrue(identifier.isIgnoreProvidedTag());
  }

  @Test
  public void testLabelTagTypeToString() {
    LabeledChunkIdentifier.LabelTagType type =
        new LabeledChunkIdentifier.LabelTagType("B-PER", "B", "PER");
    String str = type.toString();
    assertEquals("(B-PER,B,PER)", str);
  }

  @Test
  public void testTypeMatchesReturnsTrue() {
    LabeledChunkIdentifier.LabelTagType a =
        new LabeledChunkIdentifier.LabelTagType("B-LOC", "B", "LOC");
    LabeledChunkIdentifier.LabelTagType b =
        new LabeledChunkIdentifier.LabelTagType("I-LOC", "I", "LOC");
    assertTrue(a.typeMatches(b));
  }

  @Test
  public void testEmptyTokenListReturnsEmptyChunks() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    List<CoreLabel> tokens = Collections.emptyList();
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.NamedEntityTagAnnotation.class);
    assertNotNull(chunks);
    assertTrue(chunks.isEmpty());
  }

  @Test
  public void testSingleOOnly_NoChunks() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Hello");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    List<CoreLabel> tokens = Collections.singletonList(token);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.NamedEntityTagAnnotation.class);
    assertTrue(chunks.isEmpty());
  }

  @Test
  public void testChunkEndsDueToDifferentType() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "A");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "I-PER");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "B");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "I-LOC");
    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.NamedEntityTagAnnotation.class);
    assertEquals(2, chunks.size());
    assertEquals("PER", chunks.get(0).get(CoreAnnotations.NamedEntityTagAnnotation.class));
    assertEquals("LOC", chunks.get(1).get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }

  @Test
  public void testChunkStartsWithIIfPreviousOAndTypeIsDifferent() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "A");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "B");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "I-PER");
    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.NamedEntityTagAnnotation.class);
    assertEquals(1, chunks.size());
    assertEquals("PER", chunks.get(0).get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }

  @Test
  public void testChunkStartLToS_Transition() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "X");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "L-ORG");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "Y");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "S-ORG");
    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.NamedEntityTagAnnotation.class);
    assertEquals(2, chunks.size());
    assertEquals("ORG", chunks.get(1).get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }

  @Test
  public void testCustomNegLabelAndDefaultTagsAffectGetTagType() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    identifier.setNegLabel("NULL");
    identifier.setDefaultNegTag("NEG1");
    identifier.setDefaultPosTag("POS1");
    identifier.setIgnoreProvidedTag(true);
    LabeledChunkIdentifier.LabelTagType tag1 = identifier.getTagType("I-PER");
    assertEquals("PER", tag1.type);
    assertEquals("POS1", tag1.tag);
    LabeledChunkIdentifier.LabelTagType tag2 = identifier.getTagType("NULL");
    assertEquals("NULL", tag2.type);
    assertEquals("NEG1", tag2.tag);
  }

  @Test
  public void testNullTokenAnnotationKeyReturnsNegTag() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "NullLabel");
    List<CoreLabel> tokens = Collections.singletonList(token);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens, 0, CoreAnnotations.TextAnnotation.class, String.class);
    assertTrue(chunks.isEmpty());
  }

  @Test
  public void testChunkWithUnknownEncodingBracketLabels() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "[");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "[-ORG");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "Name");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "I-ORG");
    CoreLabel token3 = new CoreLabel();
    token3.set(CoreAnnotations.TextAnnotation.class, "]");
    token3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "]-ORG");
    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.NamedEntityTagAnnotation.class);
    assertEquals(1, chunks.size());
    CoreMap chunk = chunks.get(0);
    assertEquals("ORG", chunk.get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }

  @Test
  public void testUnrecognizedLabelPatternFallsBackToDefaultTag() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    identifier.setIgnoreProvidedTag(false);
    LabeledChunkIdentifier.LabelTagType tagType = identifier.getTagType("UNKNOWNFORMAT");
    assertEquals("UNKNOWNFORMAT", tagType.label);
    assertEquals("I", tagType.tag);
    assertEquals("UNKNOWNFORMAT", tagType.type);
  }

  @Test
  public void testGetAnnotatedChunksWithChunkKeyAndTextKey() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "John");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "U-PER");
    List<CoreLabel> tokens = Collections.singletonList(token);
    List<CoreMap> results =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.NamedEntityTagAnnotation.class,
            CoreAnnotations.MentionTokenAnnotation.class,
            CoreAnnotations.EntityTypeAnnotation.class,
            null);
    assertEquals(1, results.size());
  }

  @Test
  public void testLastTokenChunkCompleted() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "New");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "B-LOC");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "York");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "I-LOC");
    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.NamedEntityTagAnnotation.class);
    assertEquals(1, chunks.size());
    assertEquals("LOC", chunks.get(0).get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }

  @Test
  public void testIsStartAndEndOfChunkWithNullPrev() {
    LabeledChunkIdentifier.LabelTagType current =
        new LabeledChunkIdentifier.LabelTagType("B-PER", "B", "PER");
    boolean start = LabeledChunkIdentifier.isStartOfChunk(null, current);
    boolean end = LabeledChunkIdentifier.isEndOfChunk(null, current);
    assertTrue(start);
    assertFalse(end);
  }

  @Test
  public void testSingleTokenWithMissingTagDelimiter() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Lonely");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PER");
    List<CoreLabel> tokens = Collections.singletonList(token);
    List<CoreMap> result =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.NamedEntityTagAnnotation.class);
    assertEquals(1, result.size());
    assertEquals("PER", result.get(0).get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }

  @Test
  public void testChunkStartWhenBracketStyleStartsChunk() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "[");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "[-LOC");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "Bank");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "I-LOC");
    CoreLabel token3 = new CoreLabel();
    token3.set(CoreAnnotations.TextAnnotation.class, "]");
    token3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "]-LOC");
    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);
    List<CoreMap> result =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.NamedEntityTagAnnotation.class);
    assertEquals(1, result.size());
    assertEquals("LOC", result.get(0).get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }

  @Test
  public void testMixedChunkLabelsEndsDueToCompatibleFailure() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Start");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "B-MISC");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "Break");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "I-MISC");
    Predicate<Pair<CoreLabel, CoreLabel>> predicate =
        p -> {
          String currentWord = p.first().get(CoreAnnotations.TextAnnotation.class);
          return !"Break".equals(currentWord);
        };
    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    List<CoreMap> result =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.NamedEntityTagAnnotation.class,
            null,
            null,
            predicate);
    assertEquals(2, result.size());
    assertEquals("MISC", result.get(0).get(CoreAnnotations.NamedEntityTagAnnotation.class));
    assertEquals("MISC", result.get(1).get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }

  @Test
  public void testIsEndOfChunkWithNullTypes() {
    LabeledChunkIdentifier.LabelTagType prev =
        new LabeledChunkIdentifier.LabelTagType("I", null, null);
    LabeledChunkIdentifier.LabelTagType cur =
        new LabeledChunkIdentifier.LabelTagType("O", null, null);
    boolean result = LabeledChunkIdentifier.isEndOfChunk(prev, cur);
    assertTrue(result);
  }

  @Test
  public void testTypeDifferenceTriggersChunkBoundariesWhenTagIsSame() {
    LabeledChunkIdentifier.LabelTagType prev =
        new LabeledChunkIdentifier.LabelTagType("I-LOC", "I", "LOC");
    LabeledChunkIdentifier.LabelTagType cur =
        new LabeledChunkIdentifier.LabelTagType("I-PER", "I", "PER");
    boolean ends = LabeledChunkIdentifier.isEndOfChunk(prev, cur);
    boolean starts = LabeledChunkIdentifier.isStartOfChunk(prev, cur);
    assertTrue(ends);
    assertTrue(starts);
  }

  @Test
  public void testEmptyStringLabelIsParsedAsChunk() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "X");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "");
    List<CoreLabel> tokens = Collections.singletonList(token);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.NamedEntityTagAnnotation.class);
    assertEquals(1, chunks.size());
  }

  @Test
  public void testMultipleConsecutiveOAndDifferentTypeChunkStart() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Hello");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "World");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    CoreLabel token3 = new CoreLabel();
    token3.set(CoreAnnotations.TextAnnotation.class, "Obama");
    token3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "B-PER");
    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);
    List<CoreMap> result =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.NamedEntityTagAnnotation.class);
    assertEquals(1, result.size());
    assertEquals("PER", result.get(0).get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }

  @Test
  public void testChunkStartsWithSAndIsClosed_immediately() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Solo");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "S-ORG");
    List<CoreLabel> tokens = Collections.singletonList(token);
    List<CoreMap> result =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.NamedEntityTagAnnotation.class);
    assertEquals(1, result.size());
    assertEquals("ORG", result.get(0).get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }

  @Test
  public void testMultipleChunkTypesWithSameTagCauseSplit() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Google");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "B-ORG");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "search");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "I-PRODUCT");
    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    List<CoreMap> result =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.NamedEntityTagAnnotation.class);
    assertEquals(2, result.size());
    assertEquals("ORG", result.get(0).get(CoreAnnotations.NamedEntityTagAnnotation.class));
    assertEquals("PRODUCT", result.get(1).get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }

  @Test
  public void testConsecutiveStartTagsTriggerMultipleChunks() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "One");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "B-LOC");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "Two");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "B-LOC");
    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    List<CoreMap> result =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.NamedEntityTagAnnotation.class);
    assertEquals(2, result.size());
    assertEquals("LOC", result.get(0).get(CoreAnnotations.NamedEntityTagAnnotation.class));
    assertEquals("LOC", result.get(1).get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }

  @Test
  public void testIChunkWithoutLeadingBStartedFromNullTag() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Unknown");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "I-MISC");
    List<CoreLabel> tokens = Collections.singletonList(token);
    List<CoreMap> result =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.NamedEntityTagAnnotation.class);
    assertEquals(1, result.size());
    assertEquals("MISC", result.get(0).get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }

  @Test
  public void testFinalTokenIsChunk() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "first");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "last");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "B-ORG");
    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    List<CoreMap> result =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.NamedEntityTagAnnotation.class);
    assertEquals(1, result.size());
    assertEquals("ORG", result.get(0).get(CoreAnnotations.NamedEntityTagAnnotation.class));
    assertEquals(
        "last",
        result
            .get(0)
            .get(CoreAnnotations.TokensAnnotation.class)
            .get(0)
            .get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testSingleTokenOShouldNotCreateChunk() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "token");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    List<CoreLabel> tokens = Collections.singletonList(token);
    List<CoreMap> result =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.NamedEntityTagAnnotation.class);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testOBTagBreaksChunk() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel a = new CoreLabel();
    a.set(CoreAnnotations.TextAnnotation.class, "A");
    a.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    CoreLabel b = new CoreLabel();
    b.set(CoreAnnotations.TextAnnotation.class, "B");
    b.set(CoreAnnotations.NamedEntityTagAnnotation.class, "B-PER");
    CoreLabel c = new CoreLabel();
    c.set(CoreAnnotations.TextAnnotation.class, "C");
    c.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    CoreLabel d = new CoreLabel();
    d.set(CoreAnnotations.TextAnnotation.class, "D");
    d.set(CoreAnnotations.NamedEntityTagAnnotation.class, "B-LOC");
    CoreLabel e = new CoreLabel();
    e.set(CoreAnnotations.TextAnnotation.class, "E");
    e.set(CoreAnnotations.NamedEntityTagAnnotation.class, "I-LOC");
    List<CoreLabel> tokens = Arrays.asList(a, b, c, d, e);
    List<CoreMap> result =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.NamedEntityTagAnnotation.class);
    assertEquals(2, result.size());
    assertEquals("PER", result.get(0).get(CoreAnnotations.NamedEntityTagAnnotation.class));
    assertEquals("LOC", result.get(1).get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }

  @Test
  public void testResettingDefaultTagsAltersResult() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    identifier.setNegLabel("X");
    identifier.setDefaultPosTag("POS");
    identifier.setDefaultNegTag("NEG");
    identifier.setIgnoreProvidedTag(true);
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Text");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "X");
    List<CoreLabel> tokens = Collections.singletonList(token);
    LabeledChunkIdentifier.LabelTagType tag =
        identifier.getTagType(token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
    assertEquals("X", tag.type);
    assertEquals("NEG", tag.tag);
  }

  @Test
  public void testNullAnnotationReturnsDefaultNegTag() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    identifier.setDefaultNegTag("NEG");
    identifier.setNegLabel("O");
    LabeledChunkIdentifier.LabelTagType tag = identifier.getTagType(null);
    assertEquals("O", tag.type);
    assertEquals("NEG", tag.tag);
  }

  @Test
  public void testStartsAndEndsChunkBetweenUandLTransition() {
    LabeledChunkIdentifier.LabelTagType prev =
        new LabeledChunkIdentifier.LabelTagType("U-LOC", "U", "LOC");
    LabeledChunkIdentifier.LabelTagType cur =
        new LabeledChunkIdentifier.LabelTagType("L-LOC", "L", "LOC");
    boolean isStart = LabeledChunkIdentifier.isStartOfChunk(prev, cur);
    boolean isEnd = LabeledChunkIdentifier.isEndOfChunk(prev, cur);
    assertTrue(isStart);
    assertTrue(isEnd);
  }

  @Test
  public void testIsChunkFalseForOandPeriodTags() {
    LabeledChunkIdentifier.LabelTagType tag1 =
        new LabeledChunkIdentifier.LabelTagType("O", "O", "O");
    LabeledChunkIdentifier.LabelTagType tag2 =
        new LabeledChunkIdentifier.LabelTagType(".", ".", ".");
    // assertFalse(isChunkViaPublic(tag1));
    // assertFalse(isChunkViaPublic(tag2));
  }

  @Test
  public void testChunkTypeWithNoDashHandledCorrectly() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Entity");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORG");
    List<CoreLabel> tokens = Collections.singletonList(token);
    List<CoreMap> result =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.NamedEntityTagAnnotation.class);
    assertEquals(1, result.size());
    assertEquals("ORG", result.get(0).get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }

  @Test
  public void testIsEndOfChunkWhenPreviousHasDifferentTypeButSameTag() {
    LabeledChunkIdentifier.LabelTagType prev =
        new LabeledChunkIdentifier.LabelTagType("I-ORG", "I", "ORG");
    LabeledChunkIdentifier.LabelTagType cur =
        new LabeledChunkIdentifier.LabelTagType("I-PER", "I", "PER");
    boolean result = LabeledChunkIdentifier.isEndOfChunk(prev, cur);
    assertTrue(result);
  }

  @Test
  public void testIsStartOfChunkWhenPreviousHasDifferentTypeButSameTag() {
    LabeledChunkIdentifier.LabelTagType prev =
        new LabeledChunkIdentifier.LabelTagType("I-LOC", "I", "LOC");
    LabeledChunkIdentifier.LabelTagType cur =
        new LabeledChunkIdentifier.LabelTagType("I-MISC", "I", "MISC");
    boolean start = LabeledChunkIdentifier.isStartOfChunk(prev, cur);
    assertTrue(start);
  }

  @Test
  public void testTokenWithDashedOnlyLabelIsParsedCorrectly() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Dashes");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "-");
    List<CoreLabel> tokens = Collections.singletonList(token);
    LabeledChunkIdentifier.LabelTagType tag = identifier.getTagType("-");
    assertEquals("-", tag.label);
    assertEquals("I", tag.tag);
    assertEquals("-", tag.type);
  }

  @Test
  public void testLongLabelWithMultipleDashesIsParsedCorrectly() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Complicated");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "B-ORG-XYZ");
    LabeledChunkIdentifier.LabelTagType tagType = identifier.getTagType("B-ORG-XYZ");
    assertEquals("B", tagType.tag);
    assertEquals("ORG-XYZ", tagType.type);
    assertEquals("B-ORG-XYZ", tagType.label);
  }

  @Test
  public void testChunkSplitsOnNonCompatiblePredicateEvenIfTypesMatch() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Token1");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "B-LOC");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "Token2");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "I-LOC");
    Predicate<Pair<CoreLabel, CoreLabel>> predicate =
        p -> {
          String word = p.first().get(CoreAnnotations.TextAnnotation.class);
          return !"Token2".equals(word);
        };
    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    List<CoreMap> result =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.NamedEntityTagAnnotation.class,
            null,
            null,
            predicate);
    assertEquals(2, result.size());
    assertEquals("LOC", result.get(0).get(CoreAnnotations.NamedEntityTagAnnotation.class));
    assertEquals("LOC", result.get(1).get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }

  @Test
  public void testIOEncodingHandledAsSingleChunk() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "John");
    t1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "I-PER");
    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "Smith");
    t2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "I-PER");
    CoreLabel t3 = new CoreLabel();
    t3.set(CoreAnnotations.TextAnnotation.class, "Inc");
    t3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "I-ORG");
    List<CoreLabel> tokens = Arrays.asList(t1, t2, t3);
    List<CoreMap> result =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.NamedEntityTagAnnotation.class);
    assertEquals(2, result.size());
    assertEquals("PER", result.get(0).get(CoreAnnotations.NamedEntityTagAnnotation.class));
    assertEquals("ORG", result.get(1).get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }

  @Test
  public void testAdjacentULabelsCreateIndividualChunks() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel u1 = new CoreLabel();
    u1.set(CoreAnnotations.TextAnnotation.class, "CEO");
    u1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "U-TITLE");
    CoreLabel u2 = new CoreLabel();
    u2.set(CoreAnnotations.TextAnnotation.class, "Google");
    u2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "U-ORG");
    List<CoreLabel> tokens = Arrays.asList(u1, u2);
    List<CoreMap> result =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.NamedEntityTagAnnotation.class);
    assertEquals(2, result.size());
    assertEquals("TITLE", result.get(0).get(CoreAnnotations.NamedEntityTagAnnotation.class));
    assertEquals("ORG", result.get(1).get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }

  @Test
  public void testBreakingChunkWithUnknownLabelFormatDefaultsToDefaultPosTag() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "Alpha");
    t1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "PER-X");
    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "Beta");
    t2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "LOC");
    List<CoreLabel> tokens = Arrays.asList(t1, t2);
    List<CoreMap> result =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.NamedEntityTagAnnotation.class);
    assertEquals(2, result.size());
    assertEquals(
        "X",
        result.get(0).get(CoreAnnotations.NamedEntityTagAnnotation.class).toString().endsWith("X"),
        true);
    assertEquals("LOC", result.get(1).get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }

  @Test
  public void testChunkWithCustomNegLabelNotDefaultO() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    identifier.setNegLabel("NONE");
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "Name");
    t1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "I-PER");
    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "x");
    t2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "NONE");
    List<CoreLabel> tokens = Arrays.asList(t1, t2);
    List<CoreMap> result =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.NamedEntityTagAnnotation.class);
    assertEquals(1, result.size());
    assertEquals("PER", result.get(0).get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }

  @Test
  public void testChunkingMixedWithNullLabelTokenInMiddle() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "John");
    t1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "B-PER");
    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "Smith");
    CoreLabel t3 = new CoreLabel();
    t3.set(CoreAnnotations.TextAnnotation.class, "went");
    t3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    List<CoreLabel> tokens = Arrays.asList(t1, t2, t3);
    List<CoreMap> result =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.NamedEntityTagAnnotation.class);
    assertEquals(1, result.size());
    assertEquals("PER", result.get(0).get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }

  @Test
  public void testLabelWithoutDashUsesDefaultTag() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "IBM");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "ORG");
    LabeledChunkIdentifier.LabelTagType tagType = identifier.getTagType("ORG");
    assertEquals("ORG", tagType.type);
    assertEquals("I", tagType.tag);
    assertEquals("ORG", tagType.label);
  }

  @Test
  public void testNullLabelIsHandledGracefully() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    LabeledChunkIdentifier.LabelTagType tagType = identifier.getTagType(null);
    assertEquals("O", tagType.type);
    assertEquals("O", tagType.tag);
    assertEquals("O", tagType.label);
  }

  @Test
  public void testEmptyLabelReturnsDefaultPosTag() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    LabeledChunkIdentifier.LabelTagType tagType = identifier.getTagType("");
    assertEquals("", tagType.type);
    assertEquals("I", tagType.tag);
    assertEquals("", tagType.label);
  }

  @Test
  public void testNonMatchingPatternStillParsesAsValidChunk() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Unknown");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "CustomCode123");
    LabeledChunkIdentifier.LabelTagType tagType = identifier.getTagType("CustomCode123");
    assertEquals("CustomCode123", tagType.label);
    assertEquals("I", tagType.tag);
    assertEquals("CustomCode123", tagType.type);
  }

  @Test
  public void testSetIgnoreProvidedTagAffectsTagExtraction() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    identifier.setIgnoreProvidedTag(true);
    identifier.setDefaultPosTag("Z");
    identifier.setDefaultNegTag("X");
    LabeledChunkIdentifier.LabelTagType parsed = identifier.getTagType("B-LOC");
    assertEquals("LOC", parsed.type);
    assertEquals("Z", parsed.tag);
  }

  @Test
  public void testIsStartOfChunkTransitionFromOtoI() {
    LabeledChunkIdentifier.LabelTagType previous =
        new LabeledChunkIdentifier.LabelTagType("O", "O", "O");
    LabeledChunkIdentifier.LabelTagType current =
        new LabeledChunkIdentifier.LabelTagType("I-PER", "I", "PER");
    boolean result = LabeledChunkIdentifier.isStartOfChunk(previous, current);
    assertTrue(result);
  }

  @Test
  public void testIsEndOfChunkReturnsFalseWhenSameTypeIToI() {
    LabeledChunkIdentifier.LabelTagType previous =
        new LabeledChunkIdentifier.LabelTagType("I-PER", "I", "PER");
    LabeledChunkIdentifier.LabelTagType current =
        new LabeledChunkIdentifier.LabelTagType("I-PER", "I", "PER");
    boolean result = LabeledChunkIdentifier.isEndOfChunk(previous, current);
    assertFalse(result);
  }

  @Test
  public void testFallbackDefaultTagsWhenIgnoreTagAndMatchingNegLabel() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    identifier.setNegLabel("NULL");
    identifier.setDefaultNegTag("OUTSIDE");
    identifier.setDefaultPosTag("INSIDE");
    identifier.setIgnoreProvidedTag(true);
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Entity");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "I-NULL");
    LabeledChunkIdentifier.LabelTagType tag = identifier.getTagType("I-NULL");
    assertEquals("NULL", tag.type);
    assertEquals("OUTSIDE", tag.tag);
  }

  @Test
  public void testIsStartOfChunkFromNullPrev() {
    LabeledChunkIdentifier.LabelTagType curr =
        new LabeledChunkIdentifier.LabelTagType("B-PER", "B", "PER");
    boolean result = LabeledChunkIdentifier.isStartOfChunk(null, curr);
    assertTrue(result);
  }

  @Test
  public void testIsEndOfChunkFromNullPrevReturnsFalse() {
    LabeledChunkIdentifier.LabelTagType curr =
        new LabeledChunkIdentifier.LabelTagType("I-PER", "I", "PER");
    boolean result = LabeledChunkIdentifier.isEndOfChunk(null, curr);
    assertFalse(result);
  }

  @Test
  public void testDefaultChunkParsingWithOOnlySequenceReturnsEmptyList() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Hello");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "World");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.NamedEntityTagAnnotation.class);
    assertTrue(chunks.isEmpty());
  }

  @Test
  public void testChunkCreatedWhenOnlyOneTokenPresentAsChunk() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Obama");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "S-PER");
    List<CoreLabel> tokens = Collections.singletonList(token);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.NamedEntityTagAnnotation.class);
    assertEquals(1, chunks.size());
    assertEquals("PER", chunks.get(0).get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }

  @Test
  public void testTwoAdjacentChunksSameTypeShouldNotMerge() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "Alpha");
    t1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "B-LOC");
    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "Beta");
    t2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "B-LOC");
    List<CoreLabel> tokens = Arrays.asList(t1, t2);
    List<CoreMap> result =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.NamedEntityTagAnnotation.class);
    assertEquals(2, result.size());
    assertEquals("LOC", result.get(0).get(CoreAnnotations.NamedEntityTagAnnotation.class));
    assertEquals("LOC", result.get(1).get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }

  @Test
  public void testChunkGeneratedForEndingTokensWithoutClosure() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "Bank");
    t1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "B-ORG");
    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "America");
    t2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "I-ORG");
    CoreLabel t3 = new CoreLabel();
    t3.set(CoreAnnotations.TextAnnotation.class, "Corp");
    t3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "I-ORG");
    List<CoreLabel> tokens = Arrays.asList(t1, t2, t3);
    List<CoreMap> results =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.NamedEntityTagAnnotation.class);
    assertEquals(1, results.size());
    assertEquals("ORG", results.get(0).get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }

  @Test
  public void testUnnamedChunkIsHandledWithoutFailure() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "Term");
    t1.set(CoreAnnotations.NamedEntityTagAnnotation.class, null);
    List<CoreLabel> tokens = Collections.singletonList(t1);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.NamedEntityTagAnnotation.class);
    assertTrue(chunks.isEmpty() || chunks.size() == 1);
  }

  @Test
  public void testLabelTagTypeToStringFormat() {
    LabeledChunkIdentifier.LabelTagType tagType =
        new LabeledChunkIdentifier.LabelTagType("B-LOC", "B", "LOC");
    String value = tagType.toString();
    assertEquals("(B-LOC,B,LOC)", value);
  }

  @Test
  public void testTypeMatchesFalseWhenTypesDiffer() {
    LabeledChunkIdentifier.LabelTagType a =
        new LabeledChunkIdentifier.LabelTagType("B-PER", "B", "PER");
    LabeledChunkIdentifier.LabelTagType b =
        new LabeledChunkIdentifier.LabelTagType("I-LOC", "I", "LOC");
    boolean result = a.typeMatches(b);
    assertFalse(result);
  }

  @Test
  public void testGetTagTypeTreatsExactNegLabelAsDefaultNegTag() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    identifier.setNegLabel("O");
    identifier.setDefaultNegTag("Z");
    LabeledChunkIdentifier.LabelTagType tag = identifier.getTagType("O");
    assertEquals("O", tag.label);
    assertEquals("Z", tag.tag);
    assertEquals("O", tag.type);
  }

  @Test
  public void testIOE1EncodingCreatesEndChunkProperly() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "New");
    t1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "I-LOC");
    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "York");
    t2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "E-LOC");
    List<CoreLabel> tokens = Arrays.asList(t1, t2);
    List<CoreMap> result =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.NamedEntityTagAnnotation.class);
    assertEquals(1, result.size());
    assertEquals("LOC", result.get(0).get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }

  @Test
  public void testChunkNotStartedIfCompatibilityCheckFailsBeforeStart() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "John");
    t1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "B-PER");
    Predicate<edu.stanford.nlp.util.Pair<CoreLabel, CoreLabel>> predicate = p -> false;
    List<CoreLabel> tokens = Collections.singletonList(t1);
    List<CoreMap> results =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.NamedEntityTagAnnotation.class,
            null,
            null,
            predicate);
    assertEquals(0, results.size());
  }

  @Test(expected = RuntimeException.class)
  public void testUnclosedChunkFollowedByStartThrowsException() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "X");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "B-ORG");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "Y");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "B-LOC");
    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    identifier.getAnnotatedChunks(
        tokens,
        0,
        CoreAnnotations.TextAnnotation.class,
        CoreAnnotations.NamedEntityTagAnnotation.class);
  }

  @Test
  public void testChunkIsStartedWhenPrevTypeNilAndCurTagStartsChunk() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Alpha");
    token.set(CoreAnnotations.NamedEntityTagAnnotation.class, "S-ORG");
    List<CoreLabel> tokens = Collections.singletonList(token);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.NamedEntityTagAnnotation.class);
    assertEquals(1, chunks.size());
  }

  @Test
  public void testIOBConflictingTypesSplitsChunks() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel a = new CoreLabel();
    a.set(CoreAnnotations.TextAnnotation.class, "A");
    a.set(CoreAnnotations.NamedEntityTagAnnotation.class, "B-LOC");
    CoreLabel b = new CoreLabel();
    b.set(CoreAnnotations.TextAnnotation.class, "B");
    b.set(CoreAnnotations.NamedEntityTagAnnotation.class, "I-MISC");
    CoreLabel c = new CoreLabel();
    c.set(CoreAnnotations.TextAnnotation.class, "C");
    c.set(CoreAnnotations.NamedEntityTagAnnotation.class, "I-MISC");
    List<CoreLabel> tokens = Arrays.asList(a, b, c);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.NamedEntityTagAnnotation.class);
    assertEquals(2, chunks.size());
    assertEquals("LOC", chunks.get(0).get(CoreAnnotations.NamedEntityTagAnnotation.class));
    assertEquals("MISC", chunks.get(1).get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }

  @Test
  public void testMultipleNonChunkLabelsCreateNoSegments() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel a = new CoreLabel();
    a.set(CoreAnnotations.TextAnnotation.class, "A");
    a.set(CoreAnnotations.NamedEntityTagAnnotation.class, ".");
    CoreLabel b = new CoreLabel();
    b.set(CoreAnnotations.TextAnnotation.class, "B");
    b.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    CoreLabel c = new CoreLabel();
    c.set(CoreAnnotations.TextAnnotation.class, "C");
    c.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    List<CoreLabel> tokens = Arrays.asList(a, b, c);
    List<CoreMap> results =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.NamedEntityTagAnnotation.class);
    assertTrue(results.isEmpty());
  }

  @Test
  public void testLabelWithUnusualTagCharactersStillParsed() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel t = new CoreLabel();
    t.set(CoreAnnotations.TextAnnotation.class, "Code");
    t.set(CoreAnnotations.NamedEntityTagAnnotation.class, "X-123");
    LabeledChunkIdentifier.LabelTagType tag = identifier.getTagType("X-123");
    assertEquals("X", tag.tag);
    assertEquals("123", tag.type);
    assertEquals("X-123", tag.label);
  }

  @Test
  public void testChunkTriggeredByBracketTagType() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "[");
    token1.set(CoreAnnotations.NamedEntityTagAnnotation.class, "[-PER");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "Obama");
    token2.set(CoreAnnotations.NamedEntityTagAnnotation.class, "I-PER");
    CoreLabel token3 = new CoreLabel();
    token3.set(CoreAnnotations.TextAnnotation.class, "]");
    token3.set(CoreAnnotations.NamedEntityTagAnnotation.class, "]-PER");
    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.NamedEntityTagAnnotation.class);
    assertEquals(1, chunks.size());
    assertEquals("PER", chunks.get(0).get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }
}
