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

public class LabeledChunkIdentifier_3_GPTLLMTest {

  @Test
  public void testSingleIOBEntity() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "Barack");
    t1.set(CoreAnnotations.AnswerAnnotation.class, "B-PER");
    t1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    t1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "Obama");
    t2.set(CoreAnnotations.AnswerAnnotation.class, "I-PER");
    t2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 7);
    t2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);
    CoreLabel t3 = new CoreLabel();
    t3.set(CoreAnnotations.TextAnnotation.class, "was");
    t3.set(CoreAnnotations.AnswerAnnotation.class, "O");
    CoreLabel t4 = new CoreLabel();
    t4.set(CoreAnnotations.TextAnnotation.class, "president");
    t4.set(CoreAnnotations.AnswerAnnotation.class, "O");
    List<CoreLabel> tokens = Arrays.asList(t1, t2, t3, t4);
    List<CoreMap> result =
        chunkIdentifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, result.size());
    assertEquals("Barack Obama", result.get(0).get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testMultipleEntities() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "Barack");
    t1.set(CoreAnnotations.AnswerAnnotation.class, "B-PER");
    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "Obama");
    t2.set(CoreAnnotations.AnswerAnnotation.class, "I-PER");
    CoreLabel t3 = new CoreLabel();
    t3.set(CoreAnnotations.TextAnnotation.class, "met");
    t3.set(CoreAnnotations.AnswerAnnotation.class, "O");
    CoreLabel t4 = new CoreLabel();
    t4.set(CoreAnnotations.TextAnnotation.class, "Angela");
    t4.set(CoreAnnotations.AnswerAnnotation.class, "B-PER");
    CoreLabel t5 = new CoreLabel();
    t5.set(CoreAnnotations.TextAnnotation.class, "Merkel");
    t5.set(CoreAnnotations.AnswerAnnotation.class, "I-PER");
    CoreLabel t6 = new CoreLabel();
    t6.set(CoreAnnotations.TextAnnotation.class, "in");
    t6.set(CoreAnnotations.AnswerAnnotation.class, "O");
    CoreLabel t7 = new CoreLabel();
    t7.set(CoreAnnotations.TextAnnotation.class, "Berlin");
    t7.set(CoreAnnotations.AnswerAnnotation.class, "B-LOC");
    List<CoreLabel> tokens = Arrays.asList(t1, t2, t3, t4, t5, t6, t7);
    List<CoreMap> result =
        chunkIdentifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(3, result.size());
    assertEquals("Barack Obama", result.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("Angela Merkel", result.get(1).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("Berlin", result.get(2).get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testNullLabels() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "New");
    t1.set(CoreAnnotations.AnswerAnnotation.class, null);
    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "York");
    t2.set(CoreAnnotations.AnswerAnnotation.class, null);
    List<CoreLabel> tokens = Arrays.asList(t1, t2);
    List<CoreMap> result =
        chunkIdentifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(0, result.size());
  }

  @Test
  public void testNonMatchingLabelFormat() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "Alpha");
    t1.set(CoreAnnotations.AnswerAnnotation.class, "ORG");
    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "Beta");
    t2.set(CoreAnnotations.AnswerAnnotation.class, "ORG");
    CoreLabel t3 = new CoreLabel();
    t3.set(CoreAnnotations.TextAnnotation.class, "Gamma");
    t3.set(CoreAnnotations.AnswerAnnotation.class, "O");
    List<CoreLabel> tokens = Arrays.asList(t1, t2, t3);
    List<CoreMap> result =
        chunkIdentifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, result.size());
    assertEquals("Alpha Beta", result.get(0).get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testSetters() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    chunkIdentifier.setDefaultNegTag("Z");
    chunkIdentifier.setDefaultPosTag("X");
    chunkIdentifier.setNegLabel("Y");
    chunkIdentifier.setIgnoreProvidedTag(true);
    assertEquals("Z", chunkIdentifier.getDefaultNegTag());
    assertEquals("X", chunkIdentifier.getDefaultPosTag());
    assertEquals("Y", chunkIdentifier.getNegLabel());
    assertTrue(chunkIdentifier.isIgnoreProvidedTag());
  }

  @Test
  public void testOverlappingChunksThrowsRuntimeException() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "President");
    t1.set(CoreAnnotations.AnswerAnnotation.class, "B-TITLE");
    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "Barack");
    t2.set(CoreAnnotations.AnswerAnnotation.class, "B-PER");
    CoreLabel t3 = new CoreLabel();
    t3.set(CoreAnnotations.TextAnnotation.class, "Obama");
    t3.set(CoreAnnotations.AnswerAnnotation.class, "I-PER");
    List<CoreLabel> tokens = Arrays.asList(t1, t2, t3);
    try {
      chunkIdentifier.getAnnotatedChunks(
          tokens, 0, CoreAnnotations.TextAnnotation.class, CoreAnnotations.AnswerAnnotation.class);
      fail("Expected exception was not thrown.");
    } catch (RuntimeException ex) {
      assertTrue(ex.getMessage().contains("New chunk started"));
    }
  }

  @Test
  public void testCompatibilityCheckFalse() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "New");
    t1.set(CoreAnnotations.AnswerAnnotation.class, "B-LOC");
    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "York");
    t2.set(CoreAnnotations.AnswerAnnotation.class, "I-LOC");
    List<CoreLabel> tokens = Arrays.asList(t1, t2);
    Predicate<Pair<CoreLabel, CoreLabel>> incompatible = pair -> false;
    List<CoreMap> result =
        chunkIdentifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class,
            incompatible);
    assertEquals(2, result.size());
    assertEquals("New", result.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("York", result.get(1).get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testTrailingChunkAtEndHandled() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "San");
    t1.set(CoreAnnotations.AnswerAnnotation.class, "B-LOC");
    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "Francisco");
    t2.set(CoreAnnotations.AnswerAnnotation.class, "I-LOC");
    List<CoreLabel> tokens = Arrays.asList(t1, t2);
    List<CoreMap> result =
        chunkIdentifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, result.size());
    assertEquals("San Francisco", result.get(0).get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testEmptyInputReturnsEmptyList() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    List<CoreLabel> tokens = new ArrayList<>();
    List<CoreMap> result =
        chunkIdentifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testSingleWordUnitChunkBILOUEncoding() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "Apple");
    t1.set(CoreAnnotations.AnswerAnnotation.class, "U-ORG");
    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "announced");
    t2.set(CoreAnnotations.AnswerAnnotation.class, "O");
    CoreLabel t3 = new CoreLabel();
    t3.set(CoreAnnotations.TextAnnotation.class, "iPhone");
    t3.set(CoreAnnotations.AnswerAnnotation.class, "U-PROD");
    List<CoreLabel> tokens = Arrays.asList(t1, t2, t3);
    List<CoreMap> result =
        chunkIdentifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(2, result.size());
    assertEquals("Apple", result.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("iPhone", result.get(1).get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testLabelWithoutHyphenUsesDefaultPosTag() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Company");
    token1.set(CoreAnnotations.AnswerAnnotation.class, "ORG");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "Inc.");
    token2.set(CoreAnnotations.AnswerAnnotation.class, "ORG");
    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    List<CoreMap> chunks =
        chunkIdentifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, chunks.size());
    assertEquals("Company Inc.", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testChunkSeparatorOToBCreatesTwoChunks() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Director");
    token1.set(CoreAnnotations.AnswerAnnotation.class, "B-TITLE");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "resigned");
    token2.set(CoreAnnotations.AnswerAnnotation.class, "O");
    CoreLabel token3 = new CoreLabel();
    token3.set(CoreAnnotations.TextAnnotation.class, "Michael");
    token3.set(CoreAnnotations.AnswerAnnotation.class, "B-PER");
    CoreLabel token4 = new CoreLabel();
    token4.set(CoreAnnotations.TextAnnotation.class, "Fox");
    token4.set(CoreAnnotations.AnswerAnnotation.class, "I-PER");
    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4);
    List<CoreMap> chunks =
        chunkIdentifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(2, chunks.size());
    assertEquals("Director", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("Michael Fox", chunks.get(1).get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testIsEndOfChunkWithDifferentTypes() {
    LabeledChunkIdentifier.LabelTagType prev =
        new LabeledChunkIdentifier.LabelTagType("B-PER", "B", "PER");
    LabeledChunkIdentifier.LabelTagType curr =
        new LabeledChunkIdentifier.LabelTagType("B-ORG", "B", "ORG");
    boolean result = LabeledChunkIdentifier.isEndOfChunk(prev, curr);
    assertTrue(result);
  }

  @Test
  public void testIsStartOfChunkSetsOToITransition() {
    LabeledChunkIdentifier.LabelTagType prev =
        new LabeledChunkIdentifier.LabelTagType("O", "O", "O");
    LabeledChunkIdentifier.LabelTagType curr =
        new LabeledChunkIdentifier.LabelTagType("I-ORG", "I", "ORG");
    boolean result = LabeledChunkIdentifier.isStartOfChunk(prev, curr);
    assertTrue(result);
  }

  @Test
  public void testGetTagTypeWithIgnoreProvidedTagTrue() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    chunkIdentifier.setIgnoreProvidedTag(true);
    chunkIdentifier.setNegLabel("O");
    chunkIdentifier.setDefaultPosTag("X");
    chunkIdentifier.setDefaultNegTag("Y");
    LabeledChunkIdentifier.LabelTagType ltt1 = chunkIdentifier.getTagType("B-ORG");
    assertEquals("ORG", ltt1.type);
    assertEquals("X", ltt1.tag);
    LabeledChunkIdentifier.LabelTagType ltt2 = chunkIdentifier.getTagType("O");
    assertEquals("O", ltt2.type);
    assertEquals("Y", ltt2.tag);
  }

  @Test
  public void testBracketEncodingIsHandledAsEndAndStart() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "[Martin]");
    token1.set(CoreAnnotations.AnswerAnnotation.class, "[ORG");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "[Luther]");
    token2.set(CoreAnnotations.AnswerAnnotation.class, "]ORG");
    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    List<CoreMap> chunks =
        chunkIdentifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(2, chunks.size());
    assertEquals("[Martin]", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("[Luther]", chunks.get(1).get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testChunkWithDotTagIsIgnored() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "...");
    token1.set(CoreAnnotations.AnswerAnnotation.class, ".-PER");
    List<CoreLabel> tokens = Arrays.asList(token1);
    List<CoreMap> chunks =
        chunkIdentifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(0, chunks.size());
  }

  @Test
  public void testSingleTokenChunkWhenTypeChangesWithoutTag() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Alpha");
    token1.set(CoreAnnotations.AnswerAnnotation.class, "ORG");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "Beta");
    token2.set(CoreAnnotations.AnswerAnnotation.class, "PER");
    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    List<CoreMap> chunks =
        chunkIdentifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(2, chunks.size());
    assertEquals("Alpha", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("Beta", chunks.get(1).get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testTransitionFromETagToITagStartsNewChunk() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "New");
    token1.set(CoreAnnotations.AnswerAnnotation.class, "E-LOC");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "York");
    token2.set(CoreAnnotations.AnswerAnnotation.class, "I-LOC");
    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    List<CoreMap> chunks =
        chunkIdentifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(2, chunks.size());
    assertEquals("New", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("York", chunks.get(1).get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testTransitionFromSEncodingCreatesSeparateChunks() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "IBM");
    token1.set(CoreAnnotations.AnswerAnnotation.class, "S-ORG");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "Apple");
    token2.set(CoreAnnotations.AnswerAnnotation.class, "S-ORG");
    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    List<CoreMap> chunks =
        chunkIdentifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(2, chunks.size());
    assertEquals("IBM", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("Apple", chunks.get(1).get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testMultipleChunksWithBILOUEncoding() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "UCLA");
    token1.set(CoreAnnotations.AnswerAnnotation.class, "U-ORG");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "and");
    token2.set(CoreAnnotations.AnswerAnnotation.class, "O");
    CoreLabel token3 = new CoreLabel();
    token3.set(CoreAnnotations.TextAnnotation.class, "Harvard");
    token3.set(CoreAnnotations.AnswerAnnotation.class, "U-ORG");
    CoreLabel token4 = new CoreLabel();
    token4.set(CoreAnnotations.TextAnnotation.class, "University");
    token4.set(CoreAnnotations.AnswerAnnotation.class, "O");
    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4);
    List<CoreMap> chunks =
        chunkIdentifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(2, chunks.size());
    assertEquals("UCLA", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("Harvard", chunks.get(1).get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testTypeMismatchCausesChunkBoundaryBetweenSameTags() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "John");
    token1.set(CoreAnnotations.AnswerAnnotation.class, "I-PER");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "CompanyX");
    token2.set(CoreAnnotations.AnswerAnnotation.class, "I-ORG");
    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    List<CoreMap> chunks =
        chunkIdentifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(2, chunks.size());
    assertEquals("John", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("CompanyX", chunks.get(1).get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testNullLabelKeyThrowsClassCastException() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Hello");
    List<CoreLabel> tokens = Arrays.asList(token1);
    try {
      chunkIdentifier.getAnnotatedChunks(tokens, 0, CoreAnnotations.TextAnnotation.class, null);
      fail("Expected exception due to null labelKey");
    } catch (ClassCastException e) {
      assertTrue(true);
    }
  }

  @Test
  public void testCustomDefaultNegAndPosTagsUsedWhenNoTagInLabel() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    chunkIdentifier.setDefaultNegTag("Z");
    chunkIdentifier.setDefaultPosTag("X");
    chunkIdentifier.setNegLabel("O");
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Corp");
    token1.set(CoreAnnotations.AnswerAnnotation.class, "ORG");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "Ltd");
    token2.set(CoreAnnotations.AnswerAnnotation.class, "ORG");
    CoreLabel token3 = new CoreLabel();
    token3.set(CoreAnnotations.TextAnnotation.class, "is");
    token3.set(CoreAnnotations.AnswerAnnotation.class, "O");
    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);
    List<CoreMap> chunks =
        chunkIdentifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, chunks.size());
    assertEquals("Corp Ltd", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testIsChunkReturnsFalseForOTagAndDotTag() {
    LabeledChunkIdentifier.LabelTagType typeO =
        new LabeledChunkIdentifier.LabelTagType("O", "O", "O");
    LabeledChunkIdentifier.LabelTagType typeDot =
        new LabeledChunkIdentifier.LabelTagType(".-ORG", ".", "ORG");
    LabeledChunkIdentifier.LabelTagType typeB =
        new LabeledChunkIdentifier.LabelTagType("B-LOC", "B", "LOC");
    boolean resultO = LabeledChunkIdentifier.isStartOfChunk(null, typeO);
    boolean resultDot = LabeledChunkIdentifier.isStartOfChunk(null, typeDot);
    boolean resultB = LabeledChunkIdentifier.isStartOfChunk(null, typeB);
    assertFalse(resultO);
    assertFalse(resultDot);
    assertTrue(resultB);
  }

  @Test
  public void testMixedEncodingSBEIOHandledCorrectly() {
    LabeledChunkIdentifier chunkIdentifier = new LabeledChunkIdentifier();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Google");
    token1.set(CoreAnnotations.AnswerAnnotation.class, "S-ORG");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "was");
    token2.set(CoreAnnotations.AnswerAnnotation.class, "O");
    CoreLabel token3 = new CoreLabel();
    token3.set(CoreAnnotations.TextAnnotation.class, "founded");
    token3.set(CoreAnnotations.AnswerAnnotation.class, "B-EVENT");
    CoreLabel token4 = new CoreLabel();
    token4.set(CoreAnnotations.TextAnnotation.class, "in");
    token4.set(CoreAnnotations.AnswerAnnotation.class, "I-EVENT");
    CoreLabel token5 = new CoreLabel();
    token5.set(CoreAnnotations.TextAnnotation.class, "1998");
    token5.set(CoreAnnotations.AnswerAnnotation.class, "E-EVENT");
    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5);
    List<CoreMap> chunks =
        chunkIdentifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(2, chunks.size());
    assertEquals("Google", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("founded in 1998", chunks.get(1).get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testLastTokenIsItsOwnChunk() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Hello");
    token1.set(CoreAnnotations.AnswerAnnotation.class, "O");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "World");
    token2.set(CoreAnnotations.AnswerAnnotation.class, "B-GREET");
    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, chunks.size());
    assertEquals("World", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testSameBTagWithDifferentTypesForcesChunkBreak() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "San");
    token1.set(CoreAnnotations.AnswerAnnotation.class, "B-LOC");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "Diego");
    token2.set(CoreAnnotations.AnswerAnnotation.class, "B-ORG");
    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(2, chunks.size());
    assertEquals("San", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("Diego", chunks.get(1).get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testStartOfChunkReturnsTrueForUnmatchedType() {
    LabeledChunkIdentifier.LabelTagType prev =
        new LabeledChunkIdentifier.LabelTagType("I-PER", "I", "PER");
    LabeledChunkIdentifier.LabelTagType curr =
        new LabeledChunkIdentifier.LabelTagType("I-LOC", "I", "LOC");
    boolean isStart = LabeledChunkIdentifier.isStartOfChunk(prev, curr);
    assertTrue(isStart);
  }

  @Test
  public void testEndOfChunkReturnsTrueForUnmatchedType() {
    LabeledChunkIdentifier.LabelTagType prev =
        new LabeledChunkIdentifier.LabelTagType("I-PER", "I", "PER");
    LabeledChunkIdentifier.LabelTagType curr =
        new LabeledChunkIdentifier.LabelTagType("I-ORG", "I", "ORG");
    boolean isEnd = LabeledChunkIdentifier.isEndOfChunk(prev, curr);
    assertTrue(isEnd);
  }

  @Test
  public void testIOE2Encoding() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Bank");
    token1.set(CoreAnnotations.AnswerAnnotation.class, "I-ORG");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "America");
    token2.set(CoreAnnotations.AnswerAnnotation.class, "E-ORG");
    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, chunks.size());
    assertEquals("Bank America", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testEmptyLabelValueIsInterpretedAsChunk() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    identifier.setNegLabel("O");
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Foo");
    token1.set(CoreAnnotations.AnswerAnnotation.class, "");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "Bar");
    token2.set(CoreAnnotations.AnswerAnnotation.class, "O");
    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, chunks.size());
  }

  @Test
  public void testNullAnswerTreatsAsNegLabelWithCustomTag() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    identifier.setNegLabel("N/A");
    identifier.setDefaultNegTag("Z");
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Unknown");
    token1.set(CoreAnnotations.AnswerAnnotation.class, null);
    List<CoreLabel> tokens = Arrays.asList(token1);
    List<CoreMap> result =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(0, result.size());
  }

  @Test
  public void testOneTokenProperlyAnnotatedWithChunkKeyAndLabelKey() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "NASA");
    token1.set(CoreAnnotations.AnswerAnnotation.class, "B-ORG");
    List<CoreLabel> tokens = Arrays.asList(token1);
    List<CoreMap> result =
        identifier.getAnnotatedChunks(
            tokens,
            10,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class,
            CoreAnnotations.NamedEntityTagAnnotation.class,
            CoreAnnotations.NormalizedNamedEntityTagAnnotation.class);
    assertEquals(1, result.size());
    Object val1 = result.get(0).get(CoreAnnotations.NamedEntityTagAnnotation.class);
    Object val2 = result.get(0).get(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class);
    assertNotNull(val1);
    assertNotNull(val2);
  }

  @Test
  public void testIncompatibleTokensForceChunkBreak() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "New");
    token1.set(CoreAnnotations.AnswerAnnotation.class, "B-LOC");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "York");
    token2.set(CoreAnnotations.AnswerAnnotation.class, "I-LOC");
    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    Predicate<Pair<CoreLabel, CoreLabel>> failPredicate = pair -> false;
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class,
            CoreAnnotations.NamedEntityTagAnnotation.class,
            CoreAnnotations.NormalizedNamedEntityTagAnnotation.class,
            failPredicate);
    assertEquals(2, chunks.size());
    assertEquals("New", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("York", chunks.get(1).get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testChunkWithValidEndButNoStartThrowsException() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Alpha");
    token1.set(CoreAnnotations.AnswerAnnotation.class, "I-ORG");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "Beta");
    token2.set(CoreAnnotations.AnswerAnnotation.class, "B-ORG");
    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    try {
      identifier.getAnnotatedChunks(
          tokens, 0, CoreAnnotations.TextAnnotation.class, CoreAnnotations.AnswerAnnotation.class);
      fail("Expected RuntimeException not thrown");
    } catch (RuntimeException ex) {
      assertTrue(ex.getMessage().contains("New chunk started"));
    }
  }

  @Test
  public void testUnlabeledTokensGroupedAsChunkUsingDefaultPosTag() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    identifier.setNegLabel("O");
    identifier.setDefaultPosTag("I");
    identifier.setDefaultNegTag("O");
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Quantum");
    token1.set(CoreAnnotations.AnswerAnnotation.class, "ENTITY");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "Computing");
    token2.set(CoreAnnotations.AnswerAnnotation.class, "ENTITY");
    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, chunks.size());
    assertEquals("Quantum Computing", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testMisformattedLabelSingleHyphenOnlyFirstGroupUsed() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "X123");
    token.set(CoreAnnotations.AnswerAnnotation.class, "-TYPE");
    List<CoreLabel> tokens = Collections.singletonList(token);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, chunks.size());
    assertEquals("X123", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testInvalidPrefixSymbolTagChunkSplit() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Open");
    token1.set(CoreAnnotations.AnswerAnnotation.class, "[-ORG");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "AI");
    token2.set(CoreAnnotations.AnswerAnnotation.class, "]ORG");
    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(2, chunks.size());
    assertEquals("Open", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("AI", chunks.get(1).get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testChunkStartedButNotEndedIsFinalizedAtEnd() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "New");
    token1.set(CoreAnnotations.AnswerAnnotation.class, "B-LOC");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "York");
    token2.set(CoreAnnotations.AnswerAnnotation.class, "I-LOC");
    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            5,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, chunks.size());
    assertEquals("New York", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testTypeMatchesReturnsFalseOnDifferentTypes() {
    LabeledChunkIdentifier.LabelTagType a =
        new LabeledChunkIdentifier.LabelTagType("B-PER", "B", "PER");
    LabeledChunkIdentifier.LabelTagType b =
        new LabeledChunkIdentifier.LabelTagType("B-ORG", "B", "ORG");
    boolean result = a.typeMatches(b);
    assertFalse(result);
  }

  @Test
  public void testGetTagTypeWithNullDefaultsToNegTag() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    identifier.setNegLabel("O");
    identifier.setDefaultNegTag("Z");
    LabeledChunkIdentifier.LabelTagType tagType = identifier.getTagType(null);
    assertEquals("Z", tagType.tag);
    assertEquals("O", tagType.type);
    assertEquals("O", tagType.label);
  }

  @Test
  public void testPredicateFailsOnNullPreviousToken() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel current = new CoreLabel();
    current.set(CoreAnnotations.TextAnnotation.class, "Only");
    current.set(CoreAnnotations.AnswerAnnotation.class, "B-VAL");
    Predicate<Pair<CoreLabel, CoreLabel>> predicate =
        pair -> {
          CoreLabel prev = pair.second;
          return prev != null && prev.get(CoreAnnotations.TextAnnotation.class).equals("X");
        };
    List<CoreLabel> tokens = Collections.singletonList(current);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            3,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class,
            predicate);
    assertEquals(1, chunks.size());
    assertEquals("Only", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testChunkSplitOnIncompatiblePredicateEvenWithMatchingLabels() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel first = new CoreLabel();
    first.set(CoreAnnotations.TextAnnotation.class, "Foo");
    first.set(CoreAnnotations.AnswerAnnotation.class, "I-COMP");
    CoreLabel second = new CoreLabel();
    second.set(CoreAnnotations.TextAnnotation.class, "Bar");
    second.set(CoreAnnotations.AnswerAnnotation.class, "I-COMP");
    Predicate<Pair<CoreLabel, CoreLabel>> incompatible = pair -> false;
    List<CoreLabel> tokens = Arrays.asList(first, second);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class,
            incompatible);
    assertEquals(2, chunks.size());
    assertEquals("Foo", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("Bar", chunks.get(1).get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testIsEndOfChunkReturnsFalseIfSameTypeIToI() {
    LabeledChunkIdentifier.LabelTagType a =
        new LabeledChunkIdentifier.LabelTagType("I-ORG", "I", "ORG");
    LabeledChunkIdentifier.LabelTagType b =
        new LabeledChunkIdentifier.LabelTagType("I-ORG", "I", "ORG");
    boolean end = LabeledChunkIdentifier.isEndOfChunk(a, b);
    assertFalse(end);
  }

  @Test
  public void testIsStartOfChunkFalseWhenTypesMatchAndTagIsI() {
    LabeledChunkIdentifier.LabelTagType prev =
        new LabeledChunkIdentifier.LabelTagType("I-ORG", "I", "ORG");
    LabeledChunkIdentifier.LabelTagType curr =
        new LabeledChunkIdentifier.LabelTagType("I-ORG", "I", "ORG");
    boolean start = LabeledChunkIdentifier.isStartOfChunk(prev, curr);
    assertFalse(start);
  }

  @Test
  public void testGetAnnotatedChunksWithCustomChunkAndLabelKeys() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "X");
    token.set(CoreAnnotations.AnswerAnnotation.class, "B-SYM");
    List<CoreLabel> tokens = Arrays.asList(token);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class,
            CoreAnnotations.NamedEntityTagAnnotation.class,
            CoreAnnotations.NormalizedNamedEntityTagAnnotation.class);
    assertEquals(1, chunks.size());
    Object tokenChunk = chunks.get(0).get(CoreAnnotations.NamedEntityTagAnnotation.class);
    Object tokenLabel = chunks.get(0).get(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class);
    assertNotNull(tokenChunk);
    assertNotNull(tokenLabel);
  }

  @Test
  public void testBtoOTransitionEndsChunk() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Chairman");
    token1.set(CoreAnnotations.AnswerAnnotation.class, "B-TITLE");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "resigned");
    token2.set(CoreAnnotations.AnswerAnnotation.class, "O");
    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, chunks.size());
    assertEquals("Chairman", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testNullTextKeyThrowsException() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.AnswerAnnotation.class, "B-PER");
    List<CoreLabel> tokens = Collections.singletonList(token);
    try {
      identifier.getAnnotatedChunks(tokens, 0, null, CoreAnnotations.AnswerAnnotation.class);
      fail("Expected NullPointerException due to null text key");
    } catch (NullPointerException expected) {
      assertTrue(true);
    }
  }

  @Test
  public void testMalformedLabelDoubleHyphenStillParses() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Acme");
    token.set(CoreAnnotations.AnswerAnnotation.class, "B-ORG-extra");
    List<CoreLabel> tokens = Collections.singletonList(token);
    List<CoreMap> result =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, result.size());
    assertEquals("Acme", result.get(0).get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testDotTagSuppressedByLogicReturnsFalseIsChunk() {
    LabeledChunkIdentifier.LabelTagType type =
        new LabeledChunkIdentifier.LabelTagType(".-PER", ".", "PER");
    assertFalse(type.tag.equals("O"));
    assertFalse(type.tag.equals("."));
    assertFalse(!".".equals(type.tag));
    assertTrue(!"O".equals(type.tag));
    boolean actual = LabeledChunkIdentifier.isStartOfChunk(null, type);
    assertFalse(actual);
  }

  @Test
  public void testCustomNegLabelRecognizedProperly() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    identifier.setNegLabel("NONE");
    identifier.setDefaultNegTag("Z");
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "x");
    token.set(CoreAnnotations.AnswerAnnotation.class, "NONE");
    List<CoreLabel> tokens = Collections.singletonList(token);
    LabeledChunkIdentifier.LabelTagType tagType = identifier.getTagType("NONE");
    assertEquals("NONE", tagType.label);
    assertEquals("Z", tagType.tag);
    assertEquals("NONE", tagType.type);
  }

  @Test
  public void testStartOfChunkBtoBWithSameType() {
    LabeledChunkIdentifier.LabelTagType prev =
        new LabeledChunkIdentifier.LabelTagType("B-ORG", "B", "ORG");
    LabeledChunkIdentifier.LabelTagType curr =
        new LabeledChunkIdentifier.LabelTagType("B-ORG", "B", "ORG");
    boolean result = LabeledChunkIdentifier.isStartOfChunk(prev, curr);
    assertTrue(result);
  }

  @Test
  public void testIToODoesEndChunk() {
    LabeledChunkIdentifier.LabelTagType prev =
        new LabeledChunkIdentifier.LabelTagType("I-LOCATION", "I", "LOCATION");
    LabeledChunkIdentifier.LabelTagType curr =
        new LabeledChunkIdentifier.LabelTagType("O", "O", "O");
    boolean result = LabeledChunkIdentifier.isEndOfChunk(prev, curr);
    assertTrue(result);
  }

  @Test
  public void testDefaultLabelsUsedForMalformedLabelNoHyphen() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    identifier.setDefaultPosTag("POS");
    identifier.setDefaultNegTag("NEG");
    identifier.setNegLabel("NONE");
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Item");
    token.set(CoreAnnotations.AnswerAnnotation.class, "THING");
    List<CoreLabel> tokens = Collections.singletonList(token);
    LabeledChunkIdentifier.LabelTagType tagType = identifier.getTagType("THING");
    assertEquals("THING", tagType.label);
    assertEquals("POS", tagType.tag);
    assertEquals("THING", tagType.type);
  }

  @Test
  public void testMultipleIndependentSingleTokenChunks() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "Alpha");
    t1.set(CoreAnnotations.AnswerAnnotation.class, "S-ORG");
    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "Bravo");
    t2.set(CoreAnnotations.AnswerAnnotation.class, "S-ORG");
    CoreLabel t3 = new CoreLabel();
    t3.set(CoreAnnotations.TextAnnotation.class, "Charlie");
    t3.set(CoreAnnotations.AnswerAnnotation.class, "O");
    List<CoreLabel> tokens = Arrays.asList(t1, t2, t3);
    List<CoreMap> result =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(2, result.size());
    assertEquals("Alpha", result.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("Bravo", result.get(1).get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testEmptyStringAsLabelIsParsedWithDefault() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    identifier.setNegLabel("O");
    identifier.setDefaultPosTag("X");
    identifier.setDefaultNegTag("Z");
    LabeledChunkIdentifier.LabelTagType tagType = identifier.getTagType("");
    assertEquals("", tagType.label);
    assertEquals("X", tagType.tag);
    assertEquals("", tagType.type);
  }

  @Test
  public void testEndWithBOnlyTokenFormsSingleChunk() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Solo");
    token.set(CoreAnnotations.AnswerAnnotation.class, "B-THEME");
    List<CoreLabel> tokens = Collections.singletonList(token);
    List<CoreMap> result =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, result.size());
    assertEquals("Solo", result.get(0).get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testBackToBackDifferentChunkTypesWithoutOAreIdentifiedSeparately() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "New");
    t1.set(CoreAnnotations.AnswerAnnotation.class, "B-LOC");
    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "Google");
    t2.set(CoreAnnotations.AnswerAnnotation.class, "B-ORG");
    List<CoreLabel> tokens = Arrays.asList(t1, t2);
    List<CoreMap> result =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(2, result.size());
    assertEquals("New", result.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("Google", result.get(1).get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testChunkInterruptWithOThenContinueChunk() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "John");
    t1.set(CoreAnnotations.AnswerAnnotation.class, "B-PER");
    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "and");
    t2.set(CoreAnnotations.AnswerAnnotation.class, "O");
    CoreLabel t3 = new CoreLabel();
    t3.set(CoreAnnotations.TextAnnotation.class, "Doe");
    t3.set(CoreAnnotations.AnswerAnnotation.class, "I-PER");
    List<CoreLabel> tokens = Arrays.asList(t1, t2, t3);
    try {
      identifier.getAnnotatedChunks(
          tokens, 0, CoreAnnotations.TextAnnotation.class, CoreAnnotations.AnswerAnnotation.class);
      fail("Expected a RuntimeException due to I-PER without starting chunk");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("New chunk started"));
    }
  }

  @Test
  public void testIsStartOfChunkWithNullPrev() {
    LabeledChunkIdentifier.LabelTagType curr =
        new LabeledChunkIdentifier.LabelTagType("I-LOC", "I", "LOC");
    boolean result = LabeledChunkIdentifier.isStartOfChunk(null, curr);
    assertTrue(result);
  }

  @Test
  public void testIsEndOfChunkWithNullPrev() {
    LabeledChunkIdentifier.LabelTagType curr =
        new LabeledChunkIdentifier.LabelTagType("B-LOC", "B", "LOC");
    boolean result = LabeledChunkIdentifier.isEndOfChunk(null, curr);
    assertFalse(result);
  }

  @Test
  public void testMalformedLabelMissingTagPrefix() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    identifier.setDefaultPosTag("I");
    identifier.setDefaultNegTag("O");
    identifier.setNegLabel("NONE");
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Foo");
    token.set(CoreAnnotations.AnswerAnnotation.class, "-ORG");
    LabeledChunkIdentifier.LabelTagType parsed = identifier.getTagType("-ORG");
    assertEquals("-ORG", parsed.label);
    assertEquals("I", parsed.tag);
    assertEquals("-ORG", parsed.type);
  }

  @Test
  public void testUnclosedChunkIsCorrectlyFinalized() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "New");
    token1.set(CoreAnnotations.AnswerAnnotation.class, "B-LOC");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "York");
    token2.set(CoreAnnotations.AnswerAnnotation.class, "I-LOC");
    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, chunks.size());
    assertEquals("New York", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testIncompatiblePredicateForcesNewChunk() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel first = new CoreLabel();
    first.set(CoreAnnotations.TextAnnotation.class, "John");
    first.set(CoreAnnotations.AnswerAnnotation.class, "B-PER");
    CoreLabel second = new CoreLabel();
    second.set(CoreAnnotations.TextAnnotation.class, "Smith");
    second.set(CoreAnnotations.AnswerAnnotation.class, "I-PER");
    Predicate<Pair<CoreLabel, CoreLabel>> predicate = pair -> false;
    List<CoreLabel> tokens = Arrays.asList(first, second);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class,
            predicate);
    assertEquals(2, chunks.size());
    assertEquals("John", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("Smith", chunks.get(1).get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testOverlappingChunksWithoutIntermediateOThrows() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Dr.");
    token1.set(CoreAnnotations.AnswerAnnotation.class, "B-TITLE");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "John");
    token2.set(CoreAnnotations.AnswerAnnotation.class, "B-PER");
    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    try {
      identifier.getAnnotatedChunks(
          tokens, 0, CoreAnnotations.TextAnnotation.class, CoreAnnotations.AnswerAnnotation.class);
      fail("Expected RuntimeException due to overlapping chunks");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("New chunk started"));
    }
  }

  @Test
  public void testEmptyLabelContainsNoHyphenParsedWithDefaultPosTag() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    identifier.setNegLabel("O");
    identifier.setDefaultPosTag("X");
    identifier.setDefaultNegTag("Y");
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "Corporation");
    token.set(CoreAnnotations.AnswerAnnotation.class, "ORG");
    LabeledChunkIdentifier.LabelTagType parsed = identifier.getTagType("ORG");
    assertEquals("X", parsed.tag);
    assertEquals("ORG", parsed.type);
  }

  @Test
  public void testSingleTokenWithBracketEncodingFormsChunk() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "EntityX");
    token.set(CoreAnnotations.AnswerAnnotation.class, "[ORG");
    List<CoreLabel> tokens = Collections.singletonList(token);
    List<CoreMap> result =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, result.size());
    assertEquals("EntityX", result.get(0).get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testUnknownEncodingHandledAsChunkByDefault() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    identifier.setDefaultPosTag("I");
    identifier.setNegLabel("O");
    identifier.setDefaultNegTag("O");
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Arcade");
    token1.set(CoreAnnotations.AnswerAnnotation.class, "WHATEVER");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "Fire");
    token2.set(CoreAnnotations.AnswerAnnotation.class, "WHATEVER");
    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    List<CoreMap> result =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(1, result.size());
    assertEquals("Arcade Fire", result.get(0).get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testSameTypeWithRepeatedBPrefixCreatesSeparateChunks() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Alpha");
    token1.set(CoreAnnotations.AnswerAnnotation.class, "B-ORG");
    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "Beta");
    token2.set(CoreAnnotations.AnswerAnnotation.class, "B-ORG");
    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(2, chunks.size());
    assertEquals("Alpha", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("Beta", chunks.get(1).get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testSAndUEncodingsBothCreateIndependentChunks() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel t1 = new CoreLabel();
    t1.set(CoreAnnotations.TextAnnotation.class, "Delta");
    t1.set(CoreAnnotations.AnswerAnnotation.class, "S-COMP");
    CoreLabel t2 = new CoreLabel();
    t2.set(CoreAnnotations.TextAnnotation.class, "Epsilon");
    t2.set(CoreAnnotations.AnswerAnnotation.class, "U-COMP");
    List<CoreLabel> tokens = Arrays.asList(t1, t2);
    List<CoreMap> chunks =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(2, chunks.size());
    assertEquals("Delta", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("Epsilon", chunks.get(1).get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void testPreviousChunkFinalizedAcrossTypeMismatchWithSameTag() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel one = new CoreLabel();
    one.set(CoreAnnotations.TextAnnotation.class, "Amazon");
    one.set(CoreAnnotations.AnswerAnnotation.class, "I-ORG");
    CoreLabel two = new CoreLabel();
    two.set(CoreAnnotations.TextAnnotation.class, "Rainforest");
    two.set(CoreAnnotations.AnswerAnnotation.class, "I-LOC");
    List<CoreLabel> tokens = Arrays.asList(one, two);
    List<CoreMap> result =
        identifier.getAnnotatedChunks(
            tokens,
            0,
            CoreAnnotations.TextAnnotation.class,
            CoreAnnotations.AnswerAnnotation.class);
    assertEquals(2, result.size());
    assertEquals("Amazon", result.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("Rainforest", result.get(1).get(CoreAnnotations.TextAnnotation.class));
  }
}
