package gate.annotation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import gate.FeatureMap;
import gate.event.AnnotationEvent;
import gate.event.AnnotationListener;
import gate.event.GateEvent;
import gate.util.SimpleFeatureMapImpl;

import java.util.Collections;
import java.util.Set;

import org.junit.Test;

public class AnnotationImplPromptTest {

  @Test
  public void constructorValuesAreReturnedByAccessors() {
    NodeImpl start = new NodeImpl(11, 3L);
    NodeImpl end = new NodeImpl(12, 9L);
    FeatureMap features = new SimpleFeatureMapImpl();
    features.put("kind", "person");
    AnnotationImpl annotation = new AnnotationImpl(7, start, end, "Token", features);

    assertEquals(Integer.valueOf(7), annotation.getId());
    assertEquals("Token", annotation.getType());
    assertSame(start, annotation.getStartNode());
    assertSame(end, annotation.getEndNode());
    assertSame(features, annotation.getFeatures());
  }

  @Test
  public void toStringContainsCompleteRepresentationAndPlatformLineEnding() {
    NodeImpl start = new NodeImpl(1, 2L);
    NodeImpl end = new NodeImpl(2, 5L);
    FeatureMap features = new SimpleFeatureMapImpl();
    AnnotationImpl annotation = new AnnotationImpl(4, start, end, "Lookup", features);

    assertEquals("AnnotationImpl: id=4; type=Lookup; features=" + features
        + "; start=" + start + "; end=" + end + System.lineSeparator(),
        annotation.toString());
  }

  @Test
  public void compareToOrdersOnlyByAnnotationId() {
    FeatureMap features = new SimpleFeatureMapImpl();
    AnnotationImpl low = new AnnotationImpl(2, new NodeImpl(1, 100L),
        new NodeImpl(2, 200L), "Z", features);
    AnnotationImpl equalId = new AnnotationImpl(2, new NodeImpl(3, 0L),
        new NodeImpl(4, 1L), "A", new SimpleFeatureMapImpl());
    AnnotationImpl high = new AnnotationImpl(9, new NodeImpl(5, 0L),
        new NodeImpl(6, 1L), "A", new SimpleFeatureMapImpl());

    assertTrue(low.compareTo(high) < 0);
    assertEquals(0, low.compareTo(equalId));
    assertTrue(high.compareTo(low) > 0);
  }

  @Test(expected = ClassCastException.class)
  public void compareToRejectsNonAnnotationObject() {
    AnnotationImpl annotation = new AnnotationImpl(1, new NodeImpl(1, 0L),
        new NodeImpl(2, 1L), "T", new SimpleFeatureMapImpl());

    annotation.compareTo("not an annotation");
  }

  @Test
  public void equalAnnotationsUseOffsetsRatherThanNodeIdentityOrNodeId() {
    FeatureMap leftFeatures = new SimpleFeatureMapImpl();
    leftFeatures.put("category", "noun");
    FeatureMap rightFeatures = new SimpleFeatureMapImpl();
    rightFeatures.put("category", "noun");
    AnnotationImpl left = new AnnotationImpl(8, new NodeImpl(1, 4L),
        new NodeImpl(2, 10L), "Token", leftFeatures);
    AnnotationImpl right = new AnnotationImpl(8, new NodeImpl(100, 4L),
        new NodeImpl(200, 10L), "Token", rightFeatures);

    assertTrue(left.equals(left));
    assertTrue(left.equals(right));
    assertTrue(right.equals(left));
    assertEquals(left.hashCode(), right.hashCode());
  }

  @Test
  public void equalsRejectsNullAndObjectsOfOtherImplementations() {
    AnnotationImpl annotation = new AnnotationImpl(1, new NodeImpl(1, 0L),
        new NodeImpl(2, 1L), "T", new SimpleFeatureMapImpl());

    assertFalse(annotation.equals(null));
    assertFalse(annotation.equals("T"));
  }

  @Test
  public void equalsDetectsTypeAndIdDifferencesIncludingNulls() {
    FeatureMap features = new SimpleFeatureMapImpl();
    NodeImpl start = new NodeImpl(1, 0L);
    NodeImpl end = new NodeImpl(2, 1L);
    AnnotationImpl base = new AnnotationImpl(1, start, end, "T", features);
    AnnotationImpl otherType = new AnnotationImpl(1, start, end, "U", features);
    AnnotationImpl nullType = new AnnotationImpl(1, start, end, null, features);
    AnnotationImpl otherId = new AnnotationImpl(2, start, end, "T", features);
    AnnotationImpl nullId = new AnnotationImpl(null, start, end, "T", features);

    assertFalse(base.equals(otherType));
    assertFalse(base.equals(nullType));
    assertFalse(nullType.equals(base));
    assertFalse(base.equals(otherId));
    assertFalse(base.equals(nullId));
    assertFalse(nullId.equals(base));
    assertNotEquals(base.hashCode(), otherType.hashCode());
  }

  @Test
  public void equalsSupportsBothNullTypeAndId() {
    AnnotationImpl left = new AnnotationImpl(null, new NodeImpl(1, 0L),
        new NodeImpl(2, 1L), null, new SimpleFeatureMapImpl());
    AnnotationImpl right = new AnnotationImpl(null, new NodeImpl(3, 0L),
        new NodeImpl(4, 1L), null, new SimpleFeatureMapImpl());

    assertEquals(left, right);
    assertEquals(left.hashCode(), right.hashCode());
  }

  @Test
  public void equalsDetectsNullAndDifferentStartNodesAndOffsets() {
    FeatureMap features = new SimpleFeatureMapImpl();
    AnnotationImpl base = new AnnotationImpl(1, new NodeImpl(1, 4L),
        new NodeImpl(2, 8L), "T", features);
    AnnotationImpl nullStart = new AnnotationImpl(1, null,
        new NodeImpl(2, 8L), "T", features);
    AnnotationImpl nullStartOffset = new AnnotationImpl(1, new NodeImpl(3),
        new NodeImpl(2, 8L), "T", features);
    AnnotationImpl otherStart = new AnnotationImpl(1, new NodeImpl(4, 5L),
        new NodeImpl(2, 8L), "T", features);

    assertFalse(base.equals(nullStart));
    assertFalse(nullStart.equals(base));
    assertFalse(base.equals(nullStartOffset));
    assertFalse(nullStartOffset.equals(base));
    assertFalse(base.equals(otherStart));
  }

  @Test
  public void equalsSupportsNullStartNodesAndNullStartOffsetsOnBothSides() {
    AnnotationImpl nullNodeLeft = new AnnotationImpl(1, null,
        new NodeImpl(1, 8L), "T", new SimpleFeatureMapImpl());
    AnnotationImpl nullNodeRight = new AnnotationImpl(1, null,
        new NodeImpl(2, 8L), "T", new SimpleFeatureMapImpl());
    AnnotationImpl nullOffsetLeft = new AnnotationImpl(1, new NodeImpl(3),
        new NodeImpl(4, 8L), "T", new SimpleFeatureMapImpl());
    AnnotationImpl nullOffsetRight = new AnnotationImpl(1, new NodeImpl(5),
        new NodeImpl(6, 8L), "T", new SimpleFeatureMapImpl());

    assertEquals(nullNodeLeft, nullNodeRight);
    assertEquals(nullOffsetLeft, nullOffsetRight);
  }

  @Test
  public void equalsDetectsNullAndDifferentEndNodesAndOffsets() {
    FeatureMap features = new SimpleFeatureMapImpl();
    AnnotationImpl base = new AnnotationImpl(1, new NodeImpl(1, 4L),
        new NodeImpl(2, 8L), "T", features);
    AnnotationImpl nullEnd = new AnnotationImpl(1, new NodeImpl(1, 4L),
        null, "T", features);
    AnnotationImpl nullEndOffset = new AnnotationImpl(1, new NodeImpl(1, 4L),
        new NodeImpl(3), "T", features);
    AnnotationImpl otherEnd = new AnnotationImpl(1, new NodeImpl(1, 4L),
        new NodeImpl(4, 9L), "T", features);

    assertFalse(base.equals(nullEnd));
    assertFalse(nullEnd.equals(base));
    assertFalse(base.equals(nullEndOffset));
    assertFalse(nullEndOffset.equals(base));
    assertFalse(base.equals(otherEnd));
  }

  @Test
  public void equalsSupportsNullEndsButDetectsFeatureDifferences() {
    FeatureMap first = new SimpleFeatureMapImpl();
    first.put("x", 1);
    FeatureMap same = new SimpleFeatureMapImpl();
    same.put("x", 1);
    FeatureMap different = new SimpleFeatureMapImpl();
    different.put("x", 2);
    AnnotationImpl left = new AnnotationImpl(1, null, null, "T", first);
    AnnotationImpl equal = new AnnotationImpl(1, null, null, "T", same);
    AnnotationImpl unequal = new AnnotationImpl(1, null, null, "T", different);
    AnnotationImpl noFeatures = new AnnotationImpl(1, null, null, "T", null);

    assertEquals(left, equal);
    assertFalse(left.equals(unequal));
    assertFalse(left.equals(noFeatures));
    assertFalse(noFeatures.equals(left));
    assertEquals(noFeatures, new AnnotationImpl(1, null, null, "T", null));
  }

  @Test
  public void coextensiveDependsOnlyOnStartAndEndOffsets() {
    AnnotationImpl annotation = new AnnotationImpl(1, new NodeImpl(1, 3L),
        new NodeImpl(2, 7L), "A", new SimpleFeatureMapImpl());
    AnnotationImpl sameSpan = new AnnotationImpl(99, new NodeImpl(50, 3L),
        new NodeImpl(60, 7L), "B", null);
    AnnotationImpl otherStart = new AnnotationImpl(2, new NodeImpl(3, 2L),
        new NodeImpl(4, 7L), "A", new SimpleFeatureMapImpl());
    AnnotationImpl otherEnd = new AnnotationImpl(3, new NodeImpl(5, 3L),
        new NodeImpl(6, 8L), "A", new SimpleFeatureMapImpl());

    assertTrue(annotation.coextensive(sameSpan));
    assertFalse(annotation.coextensive(otherStart));
    assertFalse(annotation.coextensive(otherEnd));
  }

  @Test
  public void coextensiveHandlesNullNodesAndOffsetsSymmetrically() {
    AnnotationImpl nullNodes = new AnnotationImpl(1, null, null, "T", null);
    AnnotationImpl otherNullNodes = new AnnotationImpl(2, null, null, "U", null);
    AnnotationImpl nullOffsets = new AnnotationImpl(3, new NodeImpl(1),
        new NodeImpl(2), "T", null);
    AnnotationImpl otherNullOffsets = new AnnotationImpl(4, new NodeImpl(3),
        new NodeImpl(4), "U", null);
    AnnotationImpl anchored = new AnnotationImpl(5, new NodeImpl(5, 0L),
        new NodeImpl(6, 1L), "T", null);

    assertTrue(nullNodes.coextensive(otherNullNodes));
    assertTrue(nullOffsets.coextensive(otherNullOffsets));
    assertFalse(nullNodes.coextensive(nullOffsets));
    assertFalse(nullOffsets.coextensive(anchored));
  }

  @Test
  public void compatibleRequiresCoextensiveSpanAndFeatureSubsumption() {
    FeatureMap required = new SimpleFeatureMapImpl();
    required.put("kind", "name");
    FeatureMap superset = new SimpleFeatureMapImpl();
    superset.put("kind", "name");
    superset.put("language", "en");
    FeatureMap wrong = new SimpleFeatureMapImpl();
    wrong.put("kind", "place");
    AnnotationImpl annotation = new AnnotationImpl(1, new NodeImpl(1, 2L),
        new NodeImpl(2, 6L), "Mention", required);
    AnnotationImpl matching = new AnnotationImpl(2, new NodeImpl(3, 2L),
        new NodeImpl(4, 6L), "Other", superset);
    AnnotationImpl mismatching = new AnnotationImpl(3, new NodeImpl(5, 2L),
        new NodeImpl(6, 6L), "Other", wrong);
    AnnotationImpl shifted = new AnnotationImpl(4, new NodeImpl(7, 3L),
        new NodeImpl(8, 6L), "Other", superset);

    assertTrue(annotation.isCompatible(matching));
    assertFalse(annotation.isCompatible(mismatching));
    assertFalse(annotation.isCompatible(shifted));
    assertFalse(annotation.isCompatible(null));
  }

  @Test
  public void compatibleTreatsNullCandidateFeaturesAsCompatible() {
    FeatureMap required = new SimpleFeatureMapImpl();
    required.put("needed", true);
    AnnotationImpl annotation = new AnnotationImpl(1, new NodeImpl(1, 0L),
        new NodeImpl(2, 1L), "T", required);
    AnnotationImpl candidate = new AnnotationImpl(2, new NodeImpl(3, 0L),
        new NodeImpl(4, 1L), "U", null);

    assertTrue(annotation.isCompatible(candidate));
    assertTrue(annotation.isCompatible(candidate, Collections.singleton("needed")));
  }

  @Test
  public void compatibleWithSelectedKeysIgnoresUnselectedMismatch() {
    FeatureMap required = new SimpleFeatureMapImpl();
    required.put("kind", "name");
    required.put("language", "fr");
    FeatureMap offered = new SimpleFeatureMapImpl();
    offered.put("kind", "name");
    offered.put("language", "en");
    AnnotationImpl annotation = new AnnotationImpl(1, new NodeImpl(1, 0L),
        new NodeImpl(2, 4L), "T", required);
    AnnotationImpl candidate = new AnnotationImpl(2, new NodeImpl(3, 0L),
        new NodeImpl(4, 4L), "U", offered);
    Set<String> kindOnly = Collections.singleton("kind");
    Set<String> languageOnly = Collections.singleton("language");

    assertTrue(annotation.isCompatible(candidate, kindOnly));
    assertFalse(annotation.isCompatible(candidate, languageOnly));
    assertTrue(annotation.isCompatible(candidate, Collections.emptySet()));
    assertEquals(annotation.isCompatible(candidate),
        annotation.isCompatible(candidate, null));
    assertFalse(annotation.isCompatible(null, kindOnly));
  }

  @Test
  public void overlapsUsesHalfOpenIntervalsAndAcceptsContainment() {
    AnnotationImpl annotation = new AnnotationImpl(1, new NodeImpl(1, 10L),
        new NodeImpl(2, 20L), "T", new SimpleFeatureMapImpl());
    AnnotationImpl crossingLeft = new AnnotationImpl(2, new NodeImpl(3, 5L),
        new NodeImpl(4, 11L), "T", new SimpleFeatureMapImpl());
    AnnotationImpl inside = new AnnotationImpl(3, new NodeImpl(5, 12L),
        new NodeImpl(6, 18L), "T", new SimpleFeatureMapImpl());
    AnnotationImpl enclosing = new AnnotationImpl(4, new NodeImpl(7, 0L),
        new NodeImpl(8, 30L), "T", new SimpleFeatureMapImpl());

    assertTrue(annotation.overlaps(crossingLeft));
    assertTrue(annotation.overlaps(inside));
    assertTrue(annotation.overlaps(enclosing));
  }

  @Test
  public void overlapsRejectsTouchingAndSeparatedIntervals() {
    AnnotationImpl annotation = new AnnotationImpl(1, new NodeImpl(1, 10L),
        new NodeImpl(2, 20L), "T", new SimpleFeatureMapImpl());
    AnnotationImpl touchesStart = new AnnotationImpl(2, new NodeImpl(3, 5L),
        new NodeImpl(4, 10L), "T", new SimpleFeatureMapImpl());
    AnnotationImpl touchesEnd = new AnnotationImpl(3, new NodeImpl(5, 20L),
        new NodeImpl(6, 25L), "T", new SimpleFeatureMapImpl());
    AnnotationImpl before = new AnnotationImpl(4, new NodeImpl(7, 1L),
        new NodeImpl(8, 9L), "T", new SimpleFeatureMapImpl());

    assertFalse(annotation.overlaps(touchesStart));
    assertFalse(annotation.overlaps(touchesEnd));
    assertFalse(annotation.overlaps(before));
    assertFalse(annotation.overlaps(null));
  }

  @Test
  public void overlapsRejectsCandidateWithMissingNodesOrOffsets() {
    AnnotationImpl annotation = new AnnotationImpl(1, new NodeImpl(1, 10L),
        new NodeImpl(2, 20L), "T", new SimpleFeatureMapImpl());
    AnnotationImpl noStart = new AnnotationImpl(2, null,
        new NodeImpl(3, 15L), "T", null);
    AnnotationImpl noEnd = new AnnotationImpl(3, new NodeImpl(4, 15L),
        null, "T", null);
    AnnotationImpl noStartOffset = new AnnotationImpl(4, new NodeImpl(5),
        new NodeImpl(6, 15L), "T", null);
    AnnotationImpl noEndOffset = new AnnotationImpl(5, new NodeImpl(7, 15L),
        new NodeImpl(8), "T", null);

    assertFalse(annotation.overlaps(noStart));
    assertFalse(annotation.overlaps(noEnd));
    assertFalse(annotation.overlaps(noStartOffset));
    assertFalse(annotation.overlaps(noEndOffset));
  }

  @Test
  public void partiallyCompatibleCombinesOverlapAndFeatureSubsumption() {
    FeatureMap required = new SimpleFeatureMapImpl();
    required.put("pos", "NNP");
    FeatureMap offered = new SimpleFeatureMapImpl();
    offered.put("pos", "NNP");
    offered.put("source", "gazetteer");
    FeatureMap wrong = new SimpleFeatureMapImpl();
    wrong.put("pos", "VB");
    AnnotationImpl annotation = new AnnotationImpl(1, new NodeImpl(1, 10L),
        new NodeImpl(2, 20L), "T", required);
    AnnotationImpl matching = new AnnotationImpl(2, new NodeImpl(3, 15L),
        new NodeImpl(4, 25L), "U", offered);
    AnnotationImpl mismatching = new AnnotationImpl(3, new NodeImpl(5, 15L),
        new NodeImpl(6, 25L), "U", wrong);
    AnnotationImpl disjoint = new AnnotationImpl(4, new NodeImpl(7, 20L),
        new NodeImpl(8, 30L), "U", offered);

    assertTrue(annotation.isPartiallyCompatible(matching));
    assertFalse(annotation.isPartiallyCompatible(mismatching));
    assertFalse(annotation.isPartiallyCompatible(disjoint));
    assertFalse(annotation.isPartiallyCompatible(null));
  }

  @Test
  public void partiallyCompatibleSupportsSelectedAndNullFeatureSets() {
    FeatureMap required = new SimpleFeatureMapImpl();
    required.put("pos", "NNP");
    required.put("language", "fr");
    FeatureMap offered = new SimpleFeatureMapImpl();
    offered.put("pos", "NNP");
    offered.put("language", "en");
    AnnotationImpl annotation = new AnnotationImpl(1, new NodeImpl(1, 10L),
        new NodeImpl(2, 20L), "T", required);
    AnnotationImpl candidate = new AnnotationImpl(2, new NodeImpl(3, 15L),
        new NodeImpl(4, 25L), "U", offered);
    AnnotationImpl nullFeatures = new AnnotationImpl(3, new NodeImpl(5, 15L),
        new NodeImpl(6, 25L), "U", null);

    assertTrue(annotation.isPartiallyCompatible(candidate,
        Collections.singleton("pos")));
    assertFalse(annotation.isPartiallyCompatible(candidate,
        Collections.singleton("language")));
    assertEquals(annotation.isPartiallyCompatible(candidate),
        annotation.isPartiallyCompatible(candidate, null));
    assertTrue(annotation.isPartiallyCompatible(nullFeatures));
    assertTrue(annotation.isPartiallyCompatible(nullFeatures,
        Collections.singleton("pos")));
    assertFalse(annotation.isPartiallyCompatible(null,
        Collections.singleton("pos")));
  }

  @Test
  public void withinSpanAcceptsEqualAndStrictlyContainingOuterSpans() {
    AnnotationImpl annotation = new AnnotationImpl(1, new NodeImpl(1, 10L),
        new NodeImpl(2, 20L), "T", new SimpleFeatureMapImpl());
    AnnotationImpl equal = new AnnotationImpl(2, new NodeImpl(3, 10L),
        new NodeImpl(4, 20L), "U", null);
    AnnotationImpl outer = new AnnotationImpl(3, new NodeImpl(5, 1L),
        new NodeImpl(6, 30L), "U", null);
    AnnotationImpl clipsLeft = new AnnotationImpl(4, new NodeImpl(7, 11L),
        new NodeImpl(8, 30L), "U", null);
    AnnotationImpl clipsRight = new AnnotationImpl(5, new NodeImpl(9, 1L),
        new NodeImpl(10, 19L), "U", null);

    assertTrue(annotation.withinSpanOf(equal));
    assertTrue(annotation.withinSpanOf(outer));
    assertFalse(annotation.withinSpanOf(clipsLeft));
    assertFalse(annotation.withinSpanOf(clipsRight));
  }

  @Test
  public void withinSpanRejectsNullOrUnanchoredOuterAnnotation() {
    AnnotationImpl annotation = new AnnotationImpl(1, new NodeImpl(1, 10L),
        new NodeImpl(2, 20L), "T", new SimpleFeatureMapImpl());
    AnnotationImpl noStart = new AnnotationImpl(2, null,
        new NodeImpl(3, 30L), "U", null);
    AnnotationImpl noEnd = new AnnotationImpl(3, new NodeImpl(4, 1L),
        null, "U", null);
    AnnotationImpl noStartOffset = new AnnotationImpl(4, new NodeImpl(5),
        new NodeImpl(6, 30L), "U", null);
    AnnotationImpl noEndOffset = new AnnotationImpl(5, new NodeImpl(7, 1L),
        new NodeImpl(8), "U", null);

    assertFalse(annotation.withinSpanOf(null));
    assertFalse(annotation.withinSpanOf(noStart));
    assertFalse(annotation.withinSpanOf(noEnd));
    assertFalse(annotation.withinSpanOf(noStartOffset));
    assertFalse(annotation.withinSpanOf(noEndOffset));
  }

  @Test
  public void featureMutationNotifiesListenerWithAnnotationAsSource() {
    FeatureMap features = new SimpleFeatureMapImpl();
    AnnotationImpl annotation = new AnnotationImpl(1, new NodeImpl(1, 0L),
        new NodeImpl(2, 1L), "T", features);
    int[] calls = {0};
    AnnotationEvent[] received = {null};
    AnnotationListener listener = event -> {
      calls[0]++;
      received[0] = event;
    };

    annotation.addAnnotationListener(listener);
    features.put("changed", true);

    assertEquals(1, calls[0]);
    assertSame(annotation, received[0].getSource());
    assertEquals(GateEvent.FEATURES_UPDATED, received[0].getType());
  }

  @Test
  public void duplicateListenerIsRegisteredOnlyOnce() {
    FeatureMap features = new SimpleFeatureMapImpl();
    AnnotationImpl annotation = new AnnotationImpl(1, new NodeImpl(1, 0L),
        new NodeImpl(2, 1L), "T", features);
    int[] calls = {0};
    AnnotationListener listener = event -> calls[0]++;

    annotation.addAnnotationListener(listener);
    annotation.addAnnotationListener(listener);
    features.put("first", 1);
    features.remove("first");

    assertEquals(2, calls[0]);
  }

  @Test
  public void removingListenerStopsNotificationsAndIsIdempotent() {
    FeatureMap features = new SimpleFeatureMapImpl();
    AnnotationImpl annotation = new AnnotationImpl(1, new NodeImpl(1, 0L),
        new NodeImpl(2, 1L), "T", features);
    int[] firstCalls = {0};
    int[] secondCalls = {0};
    AnnotationListener first = event -> firstCalls[0]++;
    AnnotationListener second = event -> secondCalls[0]++;

    annotation.removeAnnotationListener(first);
    annotation.addAnnotationListener(first);
    annotation.addAnnotationListener(second);
    annotation.removeAnnotationListener(first);
    annotation.removeAnnotationListener(first);
    features.clear();

    assertEquals(0, firstCalls[0]);
    assertEquals(1, secondCalls[0]);
  }

  @Test
  public void setFeaturesNotifiesListenerAndMovesMutationTrackingToNewMap() {
    FeatureMap oldFeatures = new SimpleFeatureMapImpl();
    FeatureMap newFeatures = new SimpleFeatureMapImpl();
    AnnotationImpl annotation = new AnnotationImpl(1, new NodeImpl(1, 0L),
        new NodeImpl(2, 1L), "T", oldFeatures);
    int[] calls = {0};
    AnnotationEvent[] latest = {null};
    annotation.addAnnotationListener(event -> {
      calls[0]++;
      latest[0] = event;
    });

    annotation.setFeatures(newFeatures);
    oldFeatures.put("ignored", true);
    newFeatures.put("observed", true);

    assertSame(newFeatures, annotation.getFeatures());
    assertEquals(2, calls[0]);
    assertSame(annotation, latest[0].getSource());
    assertEquals(GateEvent.FEATURES_UPDATED, latest[0].getType());
  }

  @Test
  public void setFeaturesWithoutListenersReplacesMapWithoutSideEffects() {
    FeatureMap oldFeatures = new SimpleFeatureMapImpl();
    FeatureMap newFeatures = new SimpleFeatureMapImpl();
    newFeatures.put("ready", true);
    AnnotationImpl annotation = new AnnotationImpl(1, new NodeImpl(1, 0L),
        new NodeImpl(2, 1L), "T", oldFeatures);

    annotation.setFeatures(newFeatures);

    assertSame(newFeatures, annotation.getFeatures());
    assertEquals(Boolean.TRUE, annotation.getFeatures().get("ready"));
  }

  @Test
  public void removingLastListenerStillAllowsLaterListenerToReceiveEvents() {
    FeatureMap features = new SimpleFeatureMapImpl();
    AnnotationImpl annotation = new AnnotationImpl(1, new NodeImpl(1, 0L),
        new NodeImpl(2, 1L), "T", features);
    int[] oldCalls = {0};
    int[] newCalls = {0};
    AnnotationListener oldListener = event -> oldCalls[0]++;
    AnnotationListener newListener = event -> newCalls[0]++;

    annotation.addAnnotationListener(oldListener);
    annotation.removeAnnotationListener(oldListener);
    annotation.addAnnotationListener(newListener);
    features.put("value", 1);

    assertEquals(0, oldCalls[0]);
    assertEquals(1, newCalls[0]);
  }
}
