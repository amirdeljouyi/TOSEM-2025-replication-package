package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class LabeledChunkIdentifierPromptTest {

  @Test
  public void defaultsAreExposedAndParseConventionalLabels() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();

    assertEquals("I", identifier.getDefaultPosTag());
    assertEquals("O", identifier.getDefaultNegTag());
    assertEquals("O", identifier.getNegLabel());
    assertFalse(identifier.isIgnoreProvidedTag());
    assertEquals("(B-PER,B,PER)", identifier.getTagType("B-PER").toString());
    assertEquals("(I-ORG,I,ORG)", identifier.getTagType("I-ORG").toString());
  }

  @Test
  public void parsingWithoutPrefixUsesPositiveAndNegativeDefaults() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();

    assertEquals("(PERSON,I,PERSON)", identifier.getTagType("PERSON").toString());
    assertEquals("(O,O,O)", identifier.getTagType("O").toString());
    assertEquals("(O,O,O)", identifier.getTagType(null).toString());
  }

  @Test
  public void parsingPreservesEmptyAndAdditionalHyphenComponents() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();

    assertEquals("(-ORG,,ORG)", identifier.getTagType("-ORG").toString());
    assertEquals("(B-,B,)", identifier.getTagType("B-").toString());
    assertEquals("(B-ORG-SUBTYPE,B,ORG-SUBTYPE)",
        identifier.getTagType("B-ORG-SUBTYPE").toString());
    assertEquals("(-,,)", identifier.getTagType("-").toString());
  }

  @Test
  public void settersChangeParsingPolicyAndCanBeReadBack() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    identifier.setDefaultPosTag("B");
    identifier.setDefaultNegTag(".");
    identifier.setNegLabel("NONE");
    identifier.setIgnoreProvidedTag(true);

    assertEquals("B", identifier.getDefaultPosTag());
    assertEquals(".", identifier.getDefaultNegTag());
    assertEquals("NONE", identifier.getNegLabel());
    assertTrue(identifier.isIgnoreProvidedTag());
    assertEquals("(I-PER,B,PER)", identifier.getTagType("I-PER").toString());
    assertEquals("(B-NONE,.,NONE)", identifier.getTagType("B-NONE").toString());
    assertEquals("(NONE,.,NONE)", identifier.getTagType("NONE").toString());
    assertEquals("(NONE,.,NONE)", identifier.getTagType(null).toString());
  }

  @Test
  public void labelTagTypeComparesTypesAndFormatsValue() {
    LabeledChunkIdentifier.LabelTagType first =
        new LabeledChunkIdentifier.LabelTagType("B-PER", "B", "PER");
    LabeledChunkIdentifier.LabelTagType sameType =
        new LabeledChunkIdentifier.LabelTagType("L-PER", "L", "PER");
    LabeledChunkIdentifier.LabelTagType otherType =
        new LabeledChunkIdentifier.LabelTagType("U-ORG", "U", "ORG");

    assertTrue(first.typeMatches(sameType));
    assertFalse(first.typeMatches(otherType));
    assertEquals("(B-PER,B,PER)", first.toString());
  }

  @Test
  public void startOfChunkHandlesInitialOutsideAndExplicitStarts() {
    LabeledChunkIdentifier.LabelTagType outside =
        new LabeledChunkIdentifier.LabelTagType("O", "O", "O");
    LabeledChunkIdentifier.LabelTagType inside =
        new LabeledChunkIdentifier.LabelTagType("I-PER", "I", "PER");
    LabeledChunkIdentifier.LabelTagType begin =
        new LabeledChunkIdentifier.LabelTagType("B-PER", "B", "PER");
    LabeledChunkIdentifier.LabelTagType unit =
        new LabeledChunkIdentifier.LabelTagType("U-PER", "U", "PER");

    assertTrue(LabeledChunkIdentifier.isStartOfChunk(null, inside));
    assertFalse(LabeledChunkIdentifier.isStartOfChunk(null, outside));
    assertTrue(LabeledChunkIdentifier.isStartOfChunk(inside, begin));
    assertTrue(LabeledChunkIdentifier.isStartOfChunk(outside, unit));
    assertFalse(LabeledChunkIdentifier.isStartOfChunk(inside, inside));
  }

  @Test
  public void startOfChunkRecognizesTypeChangesAndEndStyleTransitions() {
    LabeledChunkIdentifier.LabelTagType personInside =
        new LabeledChunkIdentifier.LabelTagType("I-PER", "I", "PER");
    LabeledChunkIdentifier.LabelTagType orgInside =
        new LabeledChunkIdentifier.LabelTagType("I-ORG", "I", "ORG");
    LabeledChunkIdentifier.LabelTagType personEnd =
        new LabeledChunkIdentifier.LabelTagType("E-PER", "E", "PER");
    LabeledChunkIdentifier.LabelTagType personLast =
        new LabeledChunkIdentifier.LabelTagType("L-PER", "L", "PER");

    assertTrue(LabeledChunkIdentifier.isStartOfChunk(personInside, orgInside));
    assertTrue(LabeledChunkIdentifier.isStartOfChunk(personEnd, personInside));
    assertTrue(LabeledChunkIdentifier.isStartOfChunk(personEnd, personLast));
    assertFalse(LabeledChunkIdentifier.isStartOfChunk(personInside, personEnd));
  }

  @Test
  public void endOfChunkHandlesOutsideExplicitEndsAndTypeChanges() {
    LabeledChunkIdentifier.LabelTagType outside =
        new LabeledChunkIdentifier.LabelTagType("O", "O", "O");
    LabeledChunkIdentifier.LabelTagType personInside =
        new LabeledChunkIdentifier.LabelTagType("I-PER", "I", "PER");
    LabeledChunkIdentifier.LabelTagType orgInside =
        new LabeledChunkIdentifier.LabelTagType("I-ORG", "I", "ORG");
    LabeledChunkIdentifier.LabelTagType personEnd =
        new LabeledChunkIdentifier.LabelTagType("E-PER", "E", "PER");
    LabeledChunkIdentifier.LabelTagType personBegin =
        new LabeledChunkIdentifier.LabelTagType("B-PER", "B", "PER");

    assertFalse(LabeledChunkIdentifier.isEndOfChunk(null, outside));
    assertTrue(LabeledChunkIdentifier.isEndOfChunk(personInside, outside));
    assertTrue(LabeledChunkIdentifier.isEndOfChunk(personInside, orgInside));
    assertTrue(LabeledChunkIdentifier.isEndOfChunk(personEnd, personInside));
    assertTrue(LabeledChunkIdentifier.isEndOfChunk(personBegin, personBegin));
    assertFalse(LabeledChunkIdentifier.isEndOfChunk(personInside, personInside));
  }

  @Test
  public void nullCurrentBoundaryValuesAreRejected() {
    LabeledChunkIdentifier.LabelTagType inside =
        new LabeledChunkIdentifier.LabelTagType("I-PER", "I", "PER");

    assertThrows(NullPointerException.class,
        () -> LabeledChunkIdentifier.isStartOfChunk(inside, null));
    assertThrows(NullPointerException.class,
        () -> LabeledChunkIdentifier.isEndOfChunk(inside, null));
  }

  @Test
  public void emptyAndEntirelyOutsideInputProduceNoChunks() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel outside = new CoreLabel();
    outside.set(CoreAnnotations.TextAnnotation.class, "plain");
    outside.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    CoreLabel missingLabel = new CoreLabel();
    missingLabel.set(CoreAnnotations.TextAnnotation.class, "text");

    List<CoreMap> empty = identifier.getAnnotatedChunks(
        Collections.emptyList(), 0, CoreAnnotations.TextAnnotation.class,
        CoreAnnotations.NamedEntityTagAnnotation.class);
    List<CoreMap> outsideOnly = identifier.getAnnotatedChunks(
        Arrays.asList(outside, missingLabel), 0, CoreAnnotations.TextAnnotation.class,
        CoreAnnotations.NamedEntityTagAnnotation.class);

    assertTrue(empty.isEmpty());
    assertTrue(outsideOnly.isEmpty());
  }

  @Test
  public void ioChunksSplitOnTypeChangeAndExposeCompleteMetadata() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel alice = new CoreLabel();
    alice.set(CoreAnnotations.TextAnnotation.class, "Alice");
    alice.set(CoreAnnotations.NamedEntityTagAnnotation.class, "I-PER");
    alice.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 4);
    alice.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9);
    CoreLabel smith = new CoreLabel();
    smith.set(CoreAnnotations.TextAnnotation.class, "Smith");
    smith.set(CoreAnnotations.NamedEntityTagAnnotation.class, "I-PER");
    smith.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
    smith.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 15);
    CoreLabel stanford = new CoreLabel();
    stanford.set(CoreAnnotations.TextAnnotation.class, "Stanford");
    stanford.set(CoreAnnotations.NamedEntityTagAnnotation.class, "I-ORG");
    stanford.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 19);
    stanford.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 27);

    List<CoreMap> chunks = identifier.getAnnotatedChunks(
        Arrays.asList(alice, smith, stanford), 20,
        CoreAnnotations.TextAnnotation.class, CoreAnnotations.NamedEntityTagAnnotation.class);
    CoreMap person = chunks.get(0);
    CoreMap organization = chunks.get(1);

    assertEquals(2, chunks.size());
    assertEquals("Alice Smith", person.get(CoreAnnotations.TextAnnotation.class));
    assertEquals("PER", person.get(CoreAnnotations.NamedEntityTagAnnotation.class));
    assertEquals(Integer.valueOf(20), person.get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals(Integer.valueOf(22), person.get(CoreAnnotations.TokenEndAnnotation.class));
    assertEquals(Integer.valueOf(4), person.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertEquals(Integer.valueOf(15), person.get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
    assertEquals(Arrays.asList(alice, smith), person.get(CoreAnnotations.TokensAnnotation.class));
    assertEquals("Stanford", organization.get(CoreAnnotations.TextAnnotation.class));
    assertEquals("ORG", organization.get(CoreAnnotations.NamedEntityTagAnnotation.class));
    assertEquals(Integer.valueOf(22), organization.get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals(Integer.valueOf(23), organization.get(CoreAnnotations.TokenEndAnnotation.class));
  }

  @Test
  public void iobSequenceSeparatesAdjacentChunksOfSameType() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel first = CoreLabel.wordFromString("New");
    first.set(CoreAnnotations.NamedEntityTagAnnotation.class, "B-LOC");
    CoreLabel second = CoreLabel.wordFromString("York");
    second.set(CoreAnnotations.NamedEntityTagAnnotation.class, "I-LOC");
    CoreLabel third = CoreLabel.wordFromString("Paris");
    third.set(CoreAnnotations.NamedEntityTagAnnotation.class, "B-LOC");
    CoreLabel fourth = CoreLabel.wordFromString("today");
    fourth.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreMap> chunks = identifier.getAnnotatedChunks(
        Arrays.asList(first, second, third, fourth), 0,
        CoreAnnotations.TextAnnotation.class, CoreAnnotations.NamedEntityTagAnnotation.class);

    assertEquals(2, chunks.size());
    assertEquals("New York", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals(Integer.valueOf(0), chunks.get(0).get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals(Integer.valueOf(2), chunks.get(0).get(CoreAnnotations.TokenEndAnnotation.class));
    assertEquals("Paris", chunks.get(1).get(CoreAnnotations.TextAnnotation.class));
    assertEquals(Integer.valueOf(2), chunks.get(1).get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals(Integer.valueOf(3), chunks.get(1).get(CoreAnnotations.TokenEndAnnotation.class));
  }

  @Test
  public void bilouSequenceCreatesUnitAndMultiTokenChunks() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel bill = CoreLabel.wordFromString("Bill");
    bill.set(CoreAnnotations.NamedEntityTagAnnotation.class, "U-PER");
    CoreLabel joined = CoreLabel.wordFromString("joined");
    joined.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");
    CoreLabel bank = CoreLabel.wordFromString("Bank");
    bank.set(CoreAnnotations.NamedEntityTagAnnotation.class, "B-ORG");
    CoreLabel of = CoreLabel.wordFromString("of");
    of.set(CoreAnnotations.NamedEntityTagAnnotation.class, "I-ORG");
    CoreLabel america = CoreLabel.wordFromString("America");
    america.set(CoreAnnotations.NamedEntityTagAnnotation.class, "L-ORG");

    List<CoreMap> chunks = identifier.getAnnotatedChunks(
        Arrays.asList(bill, joined, bank, of, america), 3,
        CoreAnnotations.TextAnnotation.class, CoreAnnotations.NamedEntityTagAnnotation.class);

    assertEquals(2, chunks.size());
    assertEquals("Bill", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("PER", chunks.get(0).get(CoreAnnotations.NamedEntityTagAnnotation.class));
    assertEquals("Bank of America", chunks.get(1).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("ORG", chunks.get(1).get(CoreAnnotations.NamedEntityTagAnnotation.class));
    assertEquals(Integer.valueOf(5), chunks.get(1).get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals(Integer.valueOf(8), chunks.get(1).get(CoreAnnotations.TokenEndAnnotation.class));
  }

  @Test
  public void ioeAndSbeioEndTagsCloseChunksBeforeFollowingInsideTags() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel one = CoreLabel.wordFromString("Alpha");
    one.set(CoreAnnotations.NamedEntityTagAnnotation.class, "E-X");
    CoreLabel two = CoreLabel.wordFromString("Beta");
    two.set(CoreAnnotations.NamedEntityTagAnnotation.class, "I-X");
    CoreLabel three = CoreLabel.wordFromString("Gamma");
    three.set(CoreAnnotations.NamedEntityTagAnnotation.class, "E-X");
    CoreLabel four = CoreLabel.wordFromString("Delta");
    four.set(CoreAnnotations.NamedEntityTagAnnotation.class, "S-X");

    List<CoreMap> chunks = identifier.getAnnotatedChunks(
        Arrays.asList(one, two, three, four), 0,
        CoreAnnotations.TextAnnotation.class, CoreAnnotations.NamedEntityTagAnnotation.class);

    assertEquals(3, chunks.size());
    assertEquals("Alpha", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("Beta Gamma", chunks.get(1).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("Delta", chunks.get(2).get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void unprefixedLabelsBecomeIoChunksWithCustomOutsideLabel() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    identifier.setNegLabel("NONE");
    CoreLabel first = CoreLabel.wordFromString("red");
    first.set(CoreAnnotations.NamedEntityTagAnnotation.class, "COLOR");
    CoreLabel second = CoreLabel.wordFromString("blue");
    second.set(CoreAnnotations.NamedEntityTagAnnotation.class, "COLOR");
    CoreLabel outside = CoreLabel.wordFromString("none");
    outside.set(CoreAnnotations.NamedEntityTagAnnotation.class, "NONE");

    List<CoreMap> chunks = identifier.getAnnotatedChunks(
        Arrays.asList(first, second, outside), 0,
        CoreAnnotations.TextAnnotation.class, CoreAnnotations.NamedEntityTagAnnotation.class);

    assertEquals(1, chunks.size());
    assertEquals("red blue", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("COLOR", chunks.get(0).get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }

  @Test
  public void ignoreProvidedTagsMergesDifferentPrefixesOfSameType() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    identifier.setIgnoreProvidedTag(true);
    CoreLabel first = CoreLabel.wordFromString("San");
    first.set(CoreAnnotations.NamedEntityTagAnnotation.class, "B-LOC");
    CoreLabel second = CoreLabel.wordFromString("Francisco");
    second.set(CoreAnnotations.NamedEntityTagAnnotation.class, "U-LOC");
    CoreLabel outside = CoreLabel.wordFromString("is");
    outside.set(CoreAnnotations.NamedEntityTagAnnotation.class, "O");

    List<CoreMap> chunks = identifier.getAnnotatedChunks(
        Arrays.asList(first, second, outside), 0,
        CoreAnnotations.TextAnnotation.class, CoreAnnotations.NamedEntityTagAnnotation.class);

    assertEquals(1, chunks.size());
    assertEquals("San Francisco", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("LOC", chunks.get(0).get(CoreAnnotations.NamedEntityTagAnnotation.class));
  }

  @Test
  public void compatibilityPredicateCanSplitOtherwiseContinuousChunk() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel alpha = CoreLabel.wordFromString("alpha");
    alpha.set(CoreAnnotations.NamedEntityTagAnnotation.class, "I-X");
    CoreLabel beta = CoreLabel.wordFromString("beta");
    beta.set(CoreAnnotations.NamedEntityTagAnnotation.class, "I-X");
    CoreLabel gamma = CoreLabel.wordFromString("gamma");
    gamma.set(CoreAnnotations.NamedEntityTagAnnotation.class, "I-X");
    AtomicInteger calls = new AtomicInteger();
    AtomicReference<CoreLabel> previousSeenForBeta = new AtomicReference<>();

    List<CoreMap> chunks = identifier.getAnnotatedChunks(
        Arrays.asList(alpha, beta, gamma), 0,
        CoreAnnotations.TextAnnotation.class, CoreAnnotations.NamedEntityTagAnnotation.class,
        pair -> {
          calls.incrementAndGet();
          if ("beta".equals(pair.first().get(CoreAnnotations.TextAnnotation.class))) {
            previousSeenForBeta.set(pair.second());
            return false;
          }
          return true;
        });

    assertEquals(3, calls.get());
    assertSame(alpha, previousSeenForBeta.get());
    assertEquals(2, chunks.size());
    assertEquals("alpha", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("beta gamma", chunks.get(1).get(CoreAnnotations.TextAnnotation.class));
  }

  @Test
  public void compatibilityPredicateReceivesNullPreviousForFirstToken() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel only = CoreLabel.wordFromString("solo");
    only.set(CoreAnnotations.NamedEntityTagAnnotation.class, "I-X");
    AtomicReference<CoreLabel> currentSeen = new AtomicReference<>();
    AtomicReference<CoreLabel> previousSeen = new AtomicReference<>(only);

    List<CoreMap> chunks = identifier.getAnnotatedChunks(
        Collections.singletonList(only), 0,
        CoreAnnotations.TextAnnotation.class, CoreAnnotations.NamedEntityTagAnnotation.class,
        pair -> {
          currentSeen.set(pair.first());
          previousSeen.set(pair.second());
          return true;
        });

    assertSame(only, currentSeen.get());
    assertNull(previousSeen.get());
    assertEquals(1, chunks.size());
  }

  @Test
  public void sixArgumentOverloadAnnotatesTokensWithChunkAndChunkText() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel newToken = new CoreLabel();
    newToken.set(CoreAnnotations.TextAnnotation.class, "New");
    newToken.set(CoreAnnotations.NamedEntityTagAnnotation.class, "B-LOC");
    newToken.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
    newToken.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 13);
    CoreLabel yorkToken = new CoreLabel();
    yorkToken.set(CoreAnnotations.TextAnnotation.class, "York");
    yorkToken.set(CoreAnnotations.NamedEntityTagAnnotation.class, "L-LOC");
    yorkToken.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 14);
    yorkToken.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 18);

    List<CoreMap> chunks = identifier.getAnnotatedChunks(
        Arrays.asList(newToken, yorkToken), 7,
        CoreAnnotations.TextAnnotation.class, CoreAnnotations.NamedEntityTagAnnotation.class,
        CoreAnnotations.CoNLLDepAnnotation.class,
        CoreAnnotations.CoarseNamedEntityTagAnnotation.class);
    CoreMap chunk = chunks.get(0);

    assertEquals(1, chunks.size());
    assertEquals("New York", chunk.get(CoreAnnotations.TextAnnotation.class));
    assertSame(chunk, newToken.get(CoreAnnotations.CoNLLDepAnnotation.class));
    assertSame(chunk, yorkToken.get(CoreAnnotations.CoNLLDepAnnotation.class));
    assertEquals("New York", newToken.get(CoreAnnotations.CoarseNamedEntityTagAnnotation.class));
    assertEquals("New York", yorkToken.get(CoreAnnotations.CoarseNamedEntityTagAnnotation.class));
  }

  @Test
  public void sevenArgumentOverloadCombinesCompatibilityAndTokenAnnotations() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel left = CoreLabel.wordFromString("left");
    left.set(CoreAnnotations.NamedEntityTagAnnotation.class, "I-SIDE");
    CoreLabel right = CoreLabel.wordFromString("right");
    right.set(CoreAnnotations.NamedEntityTagAnnotation.class, "I-SIDE");

    List<CoreMap> chunks = identifier.getAnnotatedChunks(
        Arrays.asList(left, right), 2,
        CoreAnnotations.TextAnnotation.class, CoreAnnotations.NamedEntityTagAnnotation.class,
        CoreAnnotations.CoNLLDepAnnotation.class,
        CoreAnnotations.CoarseNamedEntityTagAnnotation.class,
        pair -> pair.second() == null);

    assertEquals(2, chunks.size());
    assertEquals("left", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("right", chunks.get(1).get(CoreAnnotations.TextAnnotation.class));
    assertSame(chunks.get(0), left.get(CoreAnnotations.CoNLLDepAnnotation.class));
    assertSame(chunks.get(1), right.get(CoreAnnotations.CoNLLDepAnnotation.class));
    assertEquals(Integer.valueOf(2), chunks.get(0).get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals(Integer.valueOf(4), chunks.get(1).get(CoreAnnotations.TokenEndAnnotation.class));
  }

  @Test
  public void adjacentCharacterOffsetsSuppressSpaceInChunkText() {
    LabeledChunkIdentifier identifier = new LabeledChunkIdentifier();
    CoreLabel hash = new CoreLabel();
    hash.set(CoreAnnotations.TextAnnotation.class, "#");
    hash.set(CoreAnnotations.NamedEntityTagAnnotation.class, "B-TAG");
    hash.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    hash.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 1);
    CoreLabel topic = new CoreLabel();
    topic.set(CoreAnnotations.TextAnnotation.class, "topic");
    topic.set(CoreAnnotations.NamedEntityTagAnnotation.class, "I-TAG");
    topic.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 1);
    topic.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);

    List<CoreMap> chunks = identifier.getAnnotatedChunks(
        Arrays.asList(hash, topic), 0,
        CoreAnnotations.TextAnnotation.class, CoreAnnotations.NamedEntityTagAnnotation.class);

    assertEquals(1, chunks.size());
    assertEquals("#topic", chunks.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals(Integer.valueOf(0), chunks.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertEquals(Integer.valueOf(6), chunks.get(0).get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
  }
}
