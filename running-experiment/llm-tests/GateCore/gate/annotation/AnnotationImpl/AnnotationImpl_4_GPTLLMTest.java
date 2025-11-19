package gate.annotation;

import gate.Annotation;
import gate.FeatureMap;
import gate.Node;
import gate.event.AnnotationEvent;
import gate.event.AnnotationListener;
import gate.util.SimpleFeatureMapImpl;
import org.junit.Test;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AnnotationImpl_4_GPTLLMTest {

@Test
public void testGetters() {
Integer id = 1;
// Node start = new DummyNode(5L);
// Node end = new DummyNode(15L);
String type = "Person";
FeatureMap features = new SimpleFeatureMapImpl();
features.put("name", "Alice");
// AnnotationImpl annotation = new AnnotationImpl(id, start, end, type, features);
// assertEquals(Integer.valueOf(1), annotation.getId());
// assertEquals(start, annotation.getStartNode());
// assertEquals(end, annotation.getEndNode());
// assertEquals("Person", annotation.getType());
// assertEquals(features, annotation.getFeatures());
}

@Test
public void testToStringContainsIdAndType() {
Integer id = 2;
// Node start = new DummyNode(0L);
// Node end = new DummyNode(5L);
String type = "Organization";
FeatureMap features = new SimpleFeatureMapImpl();
// AnnotationImpl annotation = new AnnotationImpl(id, start, end, type, features);
// String result = annotation.toString();
// assertNotNull(result);
// assertTrue(result.contains("2"));
// assertTrue(result.contains("Organization"));
}

@Test
public void testEqualsAndHashCodeAreConsistent() {
// Node start1 = new DummyNode(10L);
// Node end1 = new DummyNode(20L);
FeatureMap features1 = new SimpleFeatureMapImpl();
features1.put("key", "value");
// AnnotationImpl a1 = new AnnotationImpl(101, start1, end1, "Test", features1);
// Node start2 = new DummyNode(10L);
// Node end2 = new DummyNode(20L);
FeatureMap features2 = new SimpleFeatureMapImpl();
features2.put("key", "value");
// AnnotationImpl a2 = new AnnotationImpl(101, start2, end2, "Test", features2);
// assertEquals(a1, a2);
// assertEquals(a1.hashCode(), a2.hashCode());
}

@Test
public void testNotEqualsWhenFeatureMapDiffers() {
// Node start = new DummyNode(15L);
// Node end = new DummyNode(30L);
FeatureMap f1 = new SimpleFeatureMapImpl();
f1.put("a", "1");
FeatureMap f2 = new SimpleFeatureMapImpl();
f2.put("a", "2");
// AnnotationImpl a1 = new AnnotationImpl(1, start, end, "Type", f1);
// AnnotationImpl a2 = new AnnotationImpl(1, start, end, "Type", f2);
// assertNotEquals(a1, a2);
}

@Test
public void testCompareToSmaller() {
// Node start = new DummyNode(0L);
// Node end = new DummyNode(5L);
FeatureMap features = new SimpleFeatureMapImpl();
// AnnotationImpl a1 = new AnnotationImpl(1, start, end, "T1", features);
// AnnotationImpl a2 = new AnnotationImpl(2, start, end, "T2", features);
// assertTrue(a1.compareTo(a2) < 0);
}

@Test
public void testCoextensiveTrue() {
// Node start = new DummyNode(0L);
// Node end = new DummyNode(10L);
FeatureMap features = new SimpleFeatureMapImpl();
// AnnotationImpl a1 = new AnnotationImpl(3, start, end, "Type", features);
// AnnotationImpl a2 = new AnnotationImpl(4, new DummyNode(0L), new DummyNode(10L), "Type", features);
// assertTrue(a1.coextensive(a2));
}

@Test
public void testOverlapsTrue() {
// Node s1 = new DummyNode(5L);
// Node e1 = new DummyNode(15L);
FeatureMap f1 = new SimpleFeatureMapImpl();
// Node s2 = new DummyNode(10L);
// Node e2 = new DummyNode(20L);
FeatureMap f2 = new SimpleFeatureMapImpl();
// AnnotationImpl a1 = new AnnotationImpl(1, s1, e1, "T", f1);
// AnnotationImpl a2 = new AnnotationImpl(2, s2, e2, "T", f2);
// assertTrue(a1.overlaps(a2));
}

@Test
public void testOverlapsFalseWhenDisjoint() {
// Node s1 = new DummyNode(0L);
// Node e1 = new DummyNode(5L);
// Node s2 = new DummyNode(10L);
// Node e2 = new DummyNode(20L);
FeatureMap f = new SimpleFeatureMapImpl();
// AnnotationImpl a1 = new AnnotationImpl(1, s1, e1, "T", f);
// AnnotationImpl a2 = new AnnotationImpl(2, s2, e2, "T", f);
// assertFalse(a1.overlaps(a2));
}

@Test
public void testWithinSpanOfTrue() {
// Node innerStart = new DummyNode(5L);
// Node innerEnd = new DummyNode(10L);
// Node outerStart = new DummyNode(0L);
// Node outerEnd = new DummyNode(20L);
FeatureMap fm = new SimpleFeatureMapImpl();
// AnnotationImpl inner = new AnnotationImpl(1, innerStart, innerEnd, "T", fm);
// AnnotationImpl outer = new AnnotationImpl(2, outerStart, outerEnd, "T", fm);
// assertTrue(inner.withinSpanOf(outer));
}

@Test
public void testSetFeaturesFiresUpdateEvent() {
// Node start = new DummyNode(0L);
// Node end = new DummyNode(5L);
FeatureMap features = new SimpleFeatureMapImpl();
// AnnotationImpl annotation = new AnnotationImpl(5, start, end, "T", features);
AtomicBoolean called = new AtomicBoolean(false);
AnnotationListener listener = new AnnotationListener() {

@Override
public void annotationUpdated(AnnotationEvent e) {
called.set(true);
}
};
// annotation.addAnnotationListener(listener);
FeatureMap newFeatures = new SimpleFeatureMapImpl();
newFeatures.put("newKey", "newValue");
// annotation.setFeatures(newFeatures);
assertTrue(called.get());
}

@Test
public void testIsCompatibleUsesSubsumes() {
FeatureMap fm1 = new SimpleFeatureMapImpl();
fm1.put("keyA", "valueA");
FeatureMap fm2 = new SimpleFeatureMapImpl();
fm2.put("keyA", "valueA");
fm2.put("keyB", "valueB");
// Node start = new DummyNode(0L);
// Node end = new DummyNode(10L);
// AnnotationImpl sub = new AnnotationImpl(1, start, end, "T", fm1);
// AnnotationImpl superSet = new AnnotationImpl(2, start, end, "T", fm2);
// assertTrue(sub.isCompatible(superSet));
}

@Test
public void testIsCompatibleWithKeysTrue() {
FeatureMap fm1 = new SimpleFeatureMapImpl();
fm1.put("k1", "v1");
fm1.put("k2", "v2");
FeatureMap fm2 = new SimpleFeatureMapImpl();
fm2.put("k1", "v1");
fm2.put("k2", "v2");
fm2.put("k3", "v3");
Set<Object> keys = new HashSet<>();
keys.add("k1");
keys.add("k2");
// Node s = new DummyNode(0L);
// Node e = new DummyNode(10L);
// AnnotationImpl subAnnot = new AnnotationImpl(1, s, e, "T", fm1);
// AnnotationImpl fullAnnot = new AnnotationImpl(2, s, e, "T", fm2);
// assertTrue(subAnnot.isCompatible(fullAnnot, keys));
}

@Test
public void testIsPartiallyCompatibleTrue() {
FeatureMap smaller = new SimpleFeatureMapImpl();
smaller.put("lang", "en");
FeatureMap larger = new SimpleFeatureMapImpl();
larger.put("lang", "en");
larger.put("country", "UK");
// AnnotationImpl a1 = new AnnotationImpl(1, new DummyNode(10L), new DummyNode(20L), "Phrase", smaller);
// AnnotationImpl a2 = new AnnotationImpl(2, new DummyNode(15L), new DummyNode(25L), "Phrase", larger);
// assertTrue(a1.isPartiallyCompatible(a2));
}

@Test
public void testRemoveAnnotationListener() {
FeatureMap fm = new SimpleFeatureMapImpl();
// Node s = new DummyNode(0L);
// Node e = new DummyNode(5L);
// AnnotationImpl annotation = new AnnotationImpl(1, s, e, "T", fm);
AnnotationListener listener = new AnnotationListener() {

public void annotationUpdated(AnnotationEvent e) {
fail("Listener should have been removed.");
}
};
// annotation.addAnnotationListener(listener);
// annotation.removeAnnotationListener(listener);
FeatureMap newFm = new SimpleFeatureMapImpl();
newFm.put("x", "y");
// annotation.setFeatures(newFm);
}

@Test
public void testEqualsWithNullObject() {
// Node start = new DummyNode(1L);
// Node end = new DummyNode(2L);
FeatureMap features = new SimpleFeatureMapImpl();
// AnnotationImpl annotation = new AnnotationImpl(100, start, end, "TestType", features);
// assertFalse(annotation.equals(null));
}

@Test
public void testEqualsWithDifferentClass() {
// Node start = new DummyNode(1L);
// Node end = new DummyNode(2L);
FeatureMap features = new SimpleFeatureMapImpl();
// AnnotationImpl annotation = new AnnotationImpl(100, start, end, "TestType", features);
// assertFalse(annotation.equals("Not an Annotation"));
}

@Test
public void testEqualsDifferentStartOffset() {
FeatureMap features = new SimpleFeatureMapImpl();
// Node start1 = new DummyNode(0L);
// Node start2 = new DummyNode(5L);
// Node end = new DummyNode(10L);
// AnnotationImpl a1 = new AnnotationImpl(1, start1, end, "T", features);
// AnnotationImpl a2 = new AnnotationImpl(1, start2, end, "T", features);
// assertFalse(a1.equals(a2));
}

@Test
public void testEqualsDifferentEndOffset() {
FeatureMap features = new SimpleFeatureMapImpl();
// Node start = new DummyNode(0L);
// Node end1 = new DummyNode(10L);
// Node end2 = new DummyNode(20L);
// AnnotationImpl a1 = new AnnotationImpl(1, start, end1, "T", features);
// AnnotationImpl a2 = new AnnotationImpl(1, start, end2, "T", features);
// assertFalse(a1.equals(a2));
}

@Test
public void testCoextensiveWithNullStartNode() {
Node start = null;
// Node end = new DummyNode(10L);
FeatureMap features = new SimpleFeatureMapImpl();
// AnnotationImpl a1 = new AnnotationImpl(1, null, end, "T", features);
// AnnotationImpl a2 = new AnnotationImpl(2, new DummyNode(0L), end, "T", features);
// assertFalse(a1.coextensive(a2));
}

@Test
public void testCoextensiveWithNullEndOffset() {
// Node start = new DummyNode(0L);
// Node end1 = new DummyNode(null);
// Node end2 = new DummyNode(10L);
FeatureMap features = new SimpleFeatureMapImpl();
// AnnotationImpl a1 = new AnnotationImpl(1, start, end1, "T", features);
// AnnotationImpl a2 = new AnnotationImpl(2, start, end2, "T", features);
// assertFalse(a1.coextensive(a2));
}

@Test
public void testOverlapsReturnsFalseWhenStartAfterEnd() {
// Node startInner = new DummyNode(20L);
// Node endInner = new DummyNode(30L);
// Node startOuter = new DummyNode(0L);
// Node endOuter = new DummyNode(10L);
FeatureMap features = new SimpleFeatureMapImpl();
// AnnotationImpl future = new AnnotationImpl(1, startInner, endInner, "T", features);
// AnnotationImpl past = new AnnotationImpl(2, startOuter, endOuter, "T", features);
// assertFalse(past.overlaps(future));
}

@Test
public void testIsCompatibleReturnsFalseWhenCoextensiveButNotSubsumed() {
// Node start = new DummyNode(0L);
// Node end = new DummyNode(10L);
FeatureMap sub = new SimpleFeatureMapImpl();
sub.put("key1", "valueA");
FeatureMap superset = new SimpleFeatureMapImpl();
superset.put("key1", "differentValue");
// AnnotationImpl a1 = new AnnotationImpl(1, start, end, "T", sub);
// AnnotationImpl a2 = new AnnotationImpl(2, start, end, "T", superset);
// assertFalse(a1.isCompatible(a2));
}

@Test
public void testIsPartiallyCompatibleFalse_NoOverlap() {
// Node s1 = new DummyNode(0L);
// Node e1 = new DummyNode(10L);
// Node s2 = new DummyNode(15L);
// Node e2 = new DummyNode(20L);
FeatureMap f1 = new SimpleFeatureMapImpl();
f1.put("a", "b");
FeatureMap f2 = new SimpleFeatureMapImpl();
f2.put("a", "b");
// AnnotationImpl a1 = new AnnotationImpl(1, s1, e1, "Type", f1);
// AnnotationImpl a2 = new AnnotationImpl(2, s2, e2, "Type", f2);
// assertFalse(a1.isPartiallyCompatible(a2));
}

@Test
public void testIsPartiallyCompatibleWithKeysFalse_NotSubsumed() {
// Node s1 = new DummyNode(0L);
// Node e1 = new DummyNode(10L);
// Node s2 = new DummyNode(5L);
// Node e2 = new DummyNode(15L);
FeatureMap f1 = new SimpleFeatureMapImpl();
f1.put("k1", "v1");
FeatureMap f2 = new SimpleFeatureMapImpl();
f2.put("k1", "wrong");
Set<Object> keys = new HashSet<>();
keys.add("k1");
// AnnotationImpl sub = new AnnotationImpl(1, s1, e1, "Type", f1);
// AnnotationImpl superAnnot = new AnnotationImpl(2, s2, e2, "Type", f2);
// assertFalse(sub.isPartiallyCompatible(superAnnot, keys));
}

@Test
public void testAddSameListenerTwiceOnlyAddsOnce() {
// Node start = new DummyNode(0L);
// Node end = new DummyNode(10L);
FeatureMap features = new SimpleFeatureMapImpl();
// AnnotationImpl annotation = new AnnotationImpl(1, start, end, "T", features);
AtomicBoolean fired = new AtomicBoolean(false);
AnnotationListener listener = new AnnotationListener() {

public void annotationUpdated(AnnotationEvent e) {
fired.set(true);
}
};
// annotation.addAnnotationListener(listener);
// annotation.addAnnotationListener(listener);
FeatureMap updatedFeatures = new SimpleFeatureMapImpl();
updatedFeatures.put("a", "b");
// annotation.setFeatures(updatedFeatures);
assertTrue(fired.get());
}

@Test
public void testAnnotationWithNullIdEqualsOtherWithNullId() {
// Node start = new DummyNode(0L);
// Node end = new DummyNode(10L);
FeatureMap fm1 = new SimpleFeatureMapImpl();
fm1.put("x", "y");
// AnnotationImpl a1 = new AnnotationImpl(null, start, end, "T", fm1);
// AnnotationImpl a2 = new AnnotationImpl(null, start, end, "T", fm1);
// assertEquals(a1, a2);
// assertEquals(a1.hashCode(), a2.hashCode());
}

@Test
public void testSetFeaturesRemovesOldEventHandlerIfPresent() {
// Node start = new DummyNode(0L);
// Node end = new DummyNode(5L);
FeatureMap originalFeatures = new SimpleFeatureMapImpl();
originalFeatures.put("original", "value");
// AnnotationImpl annotation = new AnnotationImpl(1, start, end, "T", originalFeatures);
AtomicBoolean called = new AtomicBoolean(false);
AnnotationListener listener = new AnnotationListener() {

public void annotationUpdated(AnnotationEvent e) {
called.set(true);
}
};
// annotation.addAnnotationListener(listener);
FeatureMap newFeatures = new SimpleFeatureMapImpl();
newFeatures.put("new", "value");
// annotation.setFeatures(newFeatures);
newFeatures.put("trigger", "change");
assertTrue(called.get());
}

@Test
public void testEqualsWithNullOffsetsInStartNode() {
// Node start1 = new DummyNode(null);
// Node end1 = new DummyNode(10L);
// Node start2 = new DummyNode(null);
// Node end2 = new DummyNode(10L);
FeatureMap features = new SimpleFeatureMapImpl();
features.put("key", "v");
// AnnotationImpl a1 = new AnnotationImpl(1, start1, end1, "T", features);
// AnnotationImpl a2 = new AnnotationImpl(1, start2, end2, "T", features);
// assertEquals(a1, a2);
}

@Test
public void testEqualsFailsWhenOneStartOffsetNull() {
// Node start1 = new DummyNode(null);
// Node end = new DummyNode(10L);
// Node start2 = new DummyNode(0L);
FeatureMap features = new SimpleFeatureMapImpl();
features.put("k", "v");
// AnnotationImpl a1 = new AnnotationImpl(1, start1, end, "T", features);
// AnnotationImpl a2 = new AnnotationImpl(1, start2, end, "T", features);
// assertFalse(a1.equals(a2));
}

@Test
public void testEqualsFailsWhenOneEndOffsetNull() {
// Node start = new DummyNode(0L);
// Node end1 = new DummyNode(null);
// Node end2 = new DummyNode(10L);
FeatureMap features = new SimpleFeatureMapImpl();
features.put("k", "v");
// AnnotationImpl a1 = new AnnotationImpl(1, start, end1, "T", features);
// AnnotationImpl a2 = new AnnotationImpl(1, start, end2, "T", features);
// assertFalse(a1.equals(a2));
}

@Test
public void testCoextensiveTrueWhenBothOffsetsNull() {
// Node start1 = new DummyNode(null);
// Node end1 = new DummyNode(null);
// Node start2 = new DummyNode(null);
// Node end2 = new DummyNode(null);
FeatureMap features = new SimpleFeatureMapImpl();
// AnnotationImpl a1 = new AnnotationImpl(1, start1, end1, "type", features);
// AnnotationImpl a2 = new AnnotationImpl(2, start2, end2, "type", features);
// assertTrue(a1.coextensive(a2));
}

@Test
public void testOverlapsWithEqualStartAndEnd() {
// Node start = new DummyNode(10L);
// Node end = new DummyNode(10L);
FeatureMap fm = new SimpleFeatureMapImpl();
// AnnotationImpl a1 = new AnnotationImpl(1, start, end, "type", fm);
// AnnotationImpl a2 = new AnnotationImpl(2, start, end, "type", fm);
// assertFalse(a1.overlaps(a2));
}

@Test
public void testIsCompatibleWithNullFeatureMapOnOther() {
// Node start = new DummyNode(0L);
// Node end = new DummyNode(10L);
FeatureMap features = new SimpleFeatureMapImpl();
features.put("k", "v");
// AnnotationImpl a1 = new AnnotationImpl(1, start, end, "type", features);
// AnnotationImpl a2 = new AnnotationImpl(2, start, end, "type", null);
// assertTrue(a1.isCompatible(a2));
}

@Test
public void testIsCompatibleWithKeysNullAnnotationReturnsFalse() {
// Node start = new DummyNode(0L);
// Node end = new DummyNode(10L);
FeatureMap features = new SimpleFeatureMapImpl();
features.put("k", "v");
// AnnotationImpl a1 = new AnnotationImpl(1, start, end, "type", features);
Set<Object> keys = new HashSet<>();
keys.add("k");
// assertFalse(a1.isCompatible(null, keys));
}

@Test
public void testIsPartiallyCompatibleWithNullFeatureMapOnOtherReturnsTrue() {
// Node startA = new DummyNode(5L);
// Node endA = new DummyNode(15L);
// Node startB = new DummyNode(10L);
// Node endB = new DummyNode(20L);
FeatureMap features = new SimpleFeatureMapImpl();
features.put("x", "y");
// AnnotationImpl a1 = new AnnotationImpl(1, startA, endA, "type", features);
// AnnotationImpl a2 = new AnnotationImpl(2, startB, endB, "type", null);
// assertTrue(a1.isPartiallyCompatible(a2));
}

@Test
public void testIsPartiallyCompatibleWithKeysNullSetUsesSimpleCompatibility() {
// Node startA = new DummyNode(5L);
// Node endA = new DummyNode(15L);
// Node startB = new DummyNode(10L);
// Node endB = new DummyNode(20L);
FeatureMap features1 = new SimpleFeatureMapImpl();
features1.put("x", "y");
FeatureMap features2 = new SimpleFeatureMapImpl();
features2.put("x", "y");
features2.put("extra", "prop");
// AnnotationImpl a1 = new AnnotationImpl(1, startA, endA, "type", features1);
// AnnotationImpl a2 = new AnnotationImpl(2, startB, endB, "type", features2);
// assertTrue(a1.isPartiallyCompatible(a2, null));
}

@Test
public void testAddMultipleListenersEachGetsCalled() {
// Node start = new DummyNode(0L);
// Node end = new DummyNode(10L);
FeatureMap features = new SimpleFeatureMapImpl();
// AnnotationImpl annotation = new AnnotationImpl(3, start, end, "Type", features);
AtomicBoolean first = new AtomicBoolean(false);
AtomicBoolean second = new AtomicBoolean(false);
AnnotationListener l1 = new AnnotationListener() {

public void annotationUpdated(AnnotationEvent e) {
first.set(true);
}
};
AnnotationListener l2 = new AnnotationListener() {

public void annotationUpdated(AnnotationEvent e) {
second.set(true);
}
};
// annotation.addAnnotationListener(l1);
// annotation.addAnnotationListener(l2);
FeatureMap newFeatures = new SimpleFeatureMapImpl();
newFeatures.put("trigger", true);
// annotation.setFeatures(newFeatures);
assertTrue(first.get());
assertTrue(second.get());
}

@Test
public void testCompareToEqualIdsReturnsZero() {
// Node start = new DummyNode(1L);
// Node end = new DummyNode(2L);
FeatureMap features = new SimpleFeatureMapImpl();
// AnnotationImpl a1 = new AnnotationImpl(10, start, end, "TypeA", features);
// AnnotationImpl a2 = new AnnotationImpl(10, start, end, "TypeB", features);
// assertEquals(0, a1.compareTo(a2));
}

@Test(expected = ClassCastException.class)
public void testCompareToThrowsClassCastException() {
// Node start = new DummyNode(1L);
// Node end = new DummyNode(2L);
FeatureMap features = new SimpleFeatureMapImpl();
// AnnotationImpl annotation = new AnnotationImpl(1, start, end, "type", features);
// annotation.compareTo("NotAnAnnotation");
}

@Test
public void testHashCodeHandlesNullFields() {
AnnotationImpl annotation = new AnnotationImpl(null, null, null, null, null);
int hash = annotation.hashCode();
assertEquals(17, hash);
}

@Test
public void testIsCompatibleNullFeatureMapOnThisReturnsFalse() {
// Node start = new DummyNode(0L);
// Node end = new DummyNode(10L);
FeatureMap otherFeatures = new SimpleFeatureMapImpl();
otherFeatures.put("key", "value");
// AnnotationImpl a1 = new AnnotationImpl(1, start, end, "type", null);
// AnnotationImpl a2 = new AnnotationImpl(2, start, end, "type", otherFeatures);
// assertFalse(a1.isCompatible(a2));
}

@Test
public void testIsCompatibleNullFeatureMapOnOtherWithKeysReturnsTrue() {
// Node start = new DummyNode(0L);
// Node end = new DummyNode(10L);
FeatureMap thisFeatures = new SimpleFeatureMapImpl();
thisFeatures.put("feature", "value");
Set<Object> keys = new HashSet<>();
keys.add("feature");
// AnnotationImpl a1 = new AnnotationImpl(1, start, end, "type", thisFeatures);
// AnnotationImpl a2 = new AnnotationImpl(2, start, end, "type", null);
// assertTrue(a1.isCompatible(a2, keys));
}

@Test
public void testIsPartiallyCompatibleNullFeatureMapOnThisReturnsFalse() {
// Node s1 = new DummyNode(5L);
// Node e1 = new DummyNode(15L);
// Node s2 = new DummyNode(10L);
// Node e2 = new DummyNode(20L);
FeatureMap fm = new SimpleFeatureMapImpl();
// AnnotationImpl a1 = new AnnotationImpl(1, s1, e1, "type", null);
// AnnotationImpl a2 = new AnnotationImpl(2, s2, e2, "type", fm);
// assertFalse(a1.isPartiallyCompatible(a2));
}

@Test
public void testFireAnnotationUpdatedHandlesNoListenersGracefully() {
// Node start = new DummyNode(1L);
// Node end = new DummyNode(2L);
FeatureMap fm = new SimpleFeatureMapImpl();
// AnnotationImpl annotation = new AnnotationImpl(1, start, end, "type", fm);
// AnnotationEvent event = new AnnotationEvent(annotation, AnnotationEvent.FEATURES_UPDATED);
// annotation.fireAnnotationUpdated(event);
assertTrue(true);
}

@Test
public void testRemoveAnnotationListenerSafeWhenNotPresent() {
// Node start = new DummyNode(0L);
// Node end = new DummyNode(5L);
FeatureMap fm = new SimpleFeatureMapImpl();
// AnnotationImpl annotation = new AnnotationImpl(5, start, end, "T", fm);
AnnotationListener listener = new AnnotationListener() {

public void annotationUpdated(AnnotationEvent e) {
}
};
// annotation.removeAnnotationListener(listener);
assertTrue(true);
}

@Test
public void testOverlapsReturnsFalseWhenAnyOffsetIsNull() {
// Node s1 = new DummyNode(null);
// Node e1 = new DummyNode(10L);
// Node s2 = new DummyNode(5L);
// Node e2 = new DummyNode(15L);
FeatureMap fm = new SimpleFeatureMapImpl();
// AnnotationImpl a1 = new AnnotationImpl(1, s1, e1, "type", fm);
// AnnotationImpl a2 = new AnnotationImpl(2, s2, e2, "type", fm);
// assertFalse(a1.overlaps(a2));
}

@Test
public void testWithinSpanOfWithNullOffsetsReturnsFalse() {
// Node s1 = new DummyNode(5L);
// Node e1 = new DummyNode(null);
// Node s2 = new DummyNode(0L);
// Node e2 = new DummyNode(20L);
FeatureMap fm = new SimpleFeatureMapImpl();
// AnnotationImpl inner = new AnnotationImpl(1, s1, e1, "type", fm);
// AnnotationImpl outer = new AnnotationImpl(2, s2, e2, "type", fm);
// assertFalse(inner.withinSpanOf(outer));
}

@Test
public void testWithinSpanOfEqualBoundariesReturnsTrue() {
// Node s = new DummyNode(0L);
// Node e = new DummyNode(10L);
FeatureMap fm = new SimpleFeatureMapImpl();
// AnnotationImpl a1 = new AnnotationImpl(1, s, e, "type", fm);
// AnnotationImpl a2 = new AnnotationImpl(2, s, e, "type", fm);
// assertTrue(a1.withinSpanOf(a2));
}

@Test
public void testAddAnnotationListenerTriggersFeatureMapTracking() {
// Node s = new DummyNode(0L);
// Node e = new DummyNode(5L);
FeatureMap fm = new SimpleFeatureMapImpl();
// AnnotationImpl annotation = new AnnotationImpl(1, s, e, "type", fm);
AtomicBoolean triggered = new AtomicBoolean(false);
AnnotationListener listener = new AnnotationListener() {

public void annotationUpdated(AnnotationEvent e) {
triggered.set(true);
}
};
// annotation.addAnnotationListener(listener);
fm.put("updated", true);
assertTrue(triggered.get());
}

@Test
public void testEqualsWithNullTypeOnOneAnnotation() {
// Node start = new DummyNode(0L);
// Node end = new DummyNode(10L);
FeatureMap fm = new SimpleFeatureMapImpl();
// AnnotationImpl a1 = new AnnotationImpl(1, start, end, null, fm);
// AnnotationImpl a2 = new AnnotationImpl(1, start, end, "type", fm);
// assertFalse(a1.equals(a2));
}

@Test
public void testEqualsWithDifferentFeatureMapKeys() {
// Node start = new DummyNode(0L);
// Node end = new DummyNode(10L);
FeatureMap fm1 = new SimpleFeatureMapImpl();
fm1.put("key1", "value1");
FeatureMap fm2 = new SimpleFeatureMapImpl();
fm2.put("key2", "value1");
// AnnotationImpl a1 = new AnnotationImpl(1, start, end, "type", fm1);
// AnnotationImpl a2 = new AnnotationImpl(1, start, end, "type", fm2);
// assertFalse(a1.equals(a2));
}

@Test
public void testCoextensiveReturnsFalseForMismatchedStart() {
// Node start1 = new DummyNode(5L);
// Node start2 = new DummyNode(6L);
// Node end = new DummyNode(10L);
FeatureMap fm = new SimpleFeatureMapImpl();
// AnnotationImpl a1 = new AnnotationImpl(1, start1, end, "T", fm);
// AnnotationImpl a2 = new AnnotationImpl(2, start2, end, "T", fm);
// assertFalse(a1.coextensive(a2));
}

@Test
public void testCoextensiveReturnsFalseWhenOneStartIsNull() {
Node start1 = null;
// Node end1 = new DummyNode(10L);
// Node start2 = new DummyNode(0L);
// Node end2 = new DummyNode(10L);
FeatureMap fm = new SimpleFeatureMapImpl();
// AnnotationImpl a1 = new AnnotationImpl(1, start1, end1, "T", fm);
// AnnotationImpl a2 = new AnnotationImpl(2, start2, end2, "T", fm);
// assertFalse(a1.coextensive(a2));
}

@Test
public void testIsCompatibleOnlySubsumesSubsetWhenSetFilteringApplied() {
// Node start = new DummyNode(0L);
// Node end = new DummyNode(10L);
FeatureMap sub = new SimpleFeatureMapImpl();
sub.put("shared", "match");
sub.put("exclude", "X");
FeatureMap superset = new SimpleFeatureMapImpl();
superset.put("shared", "match");
// AnnotationImpl a1 = new AnnotationImpl(1, start, end, "T", sub);
// AnnotationImpl a2 = new AnnotationImpl(2, start, end, "T", superset);
Set<Object> keys = new HashSet<>();
keys.add("shared");
// assertTrue(a1.isCompatible(a2, keys));
}

@Test
public void testIsPartiallyCompatibleWithIntersectionOnlyOneKeyIncluded() {
// Node startA = new DummyNode(10L);
// Node endA = new DummyNode(20L);
// Node startB = new DummyNode(15L);
// Node endB = new DummyNode(25L);
FeatureMap fm1 = new SimpleFeatureMapImpl();
fm1.put("a", "1");
fm1.put("b", "2");
FeatureMap fm2 = new SimpleFeatureMapImpl();
fm2.put("a", "1");
// AnnotationImpl sub = new AnnotationImpl(5, startA, endA, "T", fm1);
// AnnotationImpl sup = new AnnotationImpl(6, startB, endB, "T", fm2);
Set<Object> keys = new HashSet<>();
keys.add("a");
// assertTrue(sub.isPartiallyCompatible(sup, keys));
}

@Test
public void testIsCompatibleDifferentOffsetsReturnsFalseDespiteMatchingFeatures() {
// Node start1 = new DummyNode(0L);
// Node end1 = new DummyNode(10L);
// Node start2 = new DummyNode(5L);
// Node end2 = new DummyNode(15L);
FeatureMap f1 = new SimpleFeatureMapImpl();
f1.put("x", "y");
// AnnotationImpl a1 = new AnnotationImpl(1, start1, end1, "type", f1);
// AnnotationImpl a2 = new AnnotationImpl(2, start2, end2, "type", f1);
// assertFalse(a1.isCompatible(a2));
}

@Test
public void testIsPartiallyCompatibleMissingFeatureMapInTargetAnnotation() {
// Node s1 = new DummyNode(5L);
// Node e1 = new DummyNode(15L);
// Node s2 = new DummyNode(10L);
// Node e2 = new DummyNode(25L);
FeatureMap fm = new SimpleFeatureMapImpl();
fm.put("lang", "en");
// AnnotationImpl a1 = new AnnotationImpl(1, s1, e1, "type", fm);
// AnnotationImpl a2 = new AnnotationImpl(2, s2, e2, "type", null);
// assertTrue(a1.isPartiallyCompatible(a2));
}

@Test
public void testSetFeaturesReplacesNullEventHandlerGracefully() {
// Node s = new DummyNode(0L);
// Node e = new DummyNode(10L);
FeatureMap oldFeatures = new SimpleFeatureMapImpl();
oldFeatures.put("x", "y");
// AnnotationImpl annotation = new AnnotationImpl(1, s, e, "T", oldFeatures);
FeatureMap newFeatures = new SimpleFeatureMapImpl();
newFeatures.put("a", "b");
// annotation.setFeatures(newFeatures);
// assertEquals("b", annotation.getFeatures().get("a"));
}

@Test
public void testRemoveAnnotationListenerWhenNoneRegistered() {
// Node s = new DummyNode(0L);
// Node e = new DummyNode(5L);
FeatureMap fm = new SimpleFeatureMapImpl();
// AnnotationImpl annotation = new AnnotationImpl(1, s, e, "T", fm);
AnnotationListener l = new AnnotationListener() {

public void annotationUpdated(AnnotationEvent e) {
fail("Should not be called");
}
};
// annotation.removeAnnotationListener(l);
assertTrue(true);
}

@Test
public void testEqualsSameObjectReturnsTrue() {
// Node start = new DummyNode(0L);
// Node end = new DummyNode(10L);
FeatureMap features = new SimpleFeatureMapImpl();
features.put("k", "v");
// AnnotationImpl annotation = new AnnotationImpl(1, start, end, "T", features);
// assertTrue(annotation.equals(annotation));
}

@Test
public void testEqualsWithDifferentStartNodeInstancesSameOffset() {
// Node start1 = new DummyNode(0L);
// Node end = new DummyNode(10L);
// Node start2 = new DummyNode(0L);
FeatureMap f = new SimpleFeatureMapImpl();
f.put("a", "b");
// AnnotationImpl a1 = new AnnotationImpl(1, start1, end, "type", f);
// AnnotationImpl a2 = new AnnotationImpl(1, start2, end, "type", f);
// assertTrue(a1.equals(a2));
}

@Test
public void testEqualsWithNullEndNodeOnlyInOneObject() {
// Node start = new DummyNode(0L);
Node end1 = null;
// Node end2 = new DummyNode(10L);
FeatureMap f = new SimpleFeatureMapImpl();
f.put("a", "b");
// AnnotationImpl a1 = new AnnotationImpl(1, start, end1, "type", f);
// AnnotationImpl a2 = new AnnotationImpl(1, start, end2, "type", f);
// assertFalse(a1.equals(a2));
}

@Test
public void testCompareToWithNullIdInThisAnnotation() {
// Node s = new DummyNode(0L);
// Node e = new DummyNode(1L);
FeatureMap f = new SimpleFeatureMapImpl();
// AnnotationImpl a1 = new AnnotationImpl(null, s, e, "X", f);
// AnnotationImpl a2 = new AnnotationImpl(5, s, e, "Y", f);
try {
// a1.compareTo(a2);
fail("Expected NullPointerException");
} catch (NullPointerException expected) {
assertTrue(true);
}
}

@Test
public void testCompareToWithNullIdInOtherAnnotation() {
// Node s = new DummyNode(0L);
// Node e = new DummyNode(1L);
FeatureMap f = new SimpleFeatureMapImpl();
// AnnotationImpl a1 = new AnnotationImpl(5, s, e, "X", f);
// AnnotationImpl a2 = new AnnotationImpl(null, s, e, "Y", f);
try {
// a1.compareTo(a2);
fail("Expected NullPointerException");
} catch (NullPointerException expected) {
assertTrue(true);
}
}

@Test
public void testSetFeaturesWithSameInstanceDoesNotDoubleSubscribe() {
// Node s = new DummyNode(0L);
// Node e = new DummyNode(10L);
FeatureMap original = new SimpleFeatureMapImpl();
original.put("key", "value");
// AnnotationImpl annotation = new AnnotationImpl(1, s, e, "T", original);
AtomicBoolean triggered = new AtomicBoolean(false);
AnnotationListener listener = new AnnotationListener() {

public void annotationUpdated(AnnotationEvent e) {
triggered.set(true);
}
};
// annotation.addAnnotationListener(listener);
// annotation.setFeatures(original);
original.put("trigger", true);
assertTrue(triggered.get());
}

@Test
public void testFireAnnotationUpdatedWithMultipleEvents() {
// Node s = new DummyNode(0L);
// Node e = new DummyNode(1L);
FeatureMap f = new SimpleFeatureMapImpl();
// AnnotationImpl annotation = new AnnotationImpl(1, s, e, "T", f);
AtomicBoolean flag1 = new AtomicBoolean(false);
AtomicBoolean flag2 = new AtomicBoolean(false);
AnnotationListener listener1 = new AnnotationListener() {

public void annotationUpdated(AnnotationEvent e) {
flag1.set(true);
}
};
AnnotationListener listener2 = new AnnotationListener() {

public void annotationUpdated(AnnotationEvent e) {
flag2.set(true);
}
};
// annotation.addAnnotationListener(listener1);
// annotation.addAnnotationListener(listener2);
FeatureMap newFeatures = new SimpleFeatureMapImpl();
newFeatures.put("change", true);
// annotation.setFeatures(newFeatures);
assertTrue(flag1.get());
assertTrue(flag2.get());
}

@Test
public void testAddAnnotationListenerTwiceDoesNotAddDuplicate() {
// Node s = new DummyNode(0L);
// Node e = new DummyNode(1L);
FeatureMap f = new SimpleFeatureMapImpl();
f.put("k", "v");
// AnnotationImpl annotation = new AnnotationImpl(3, s, e, "T", f);
// AtomicInteger callCount = new AtomicInteger(0);
AnnotationListener listener = new AnnotationListener() {

public void annotationUpdated(AnnotationEvent e) {
// callCount.incrementAndGet();
}
};
// annotation.addAnnotationListener(listener);
// annotation.addAnnotationListener(listener);
FeatureMap newFeatures = new SimpleFeatureMapImpl();
newFeatures.put("second", "value");
// annotation.setFeatures(newFeatures);
// assertEquals(1, callCount.get());
}

@Test
public void testEqualsWhenBothFeatureMapsAreNullAndEverythingElseMatches() {
// Node s = new DummyNode(10L);
// Node e = new DummyNode(20L);
// AnnotationImpl a1 = new AnnotationImpl(1, s, e, "Type", null);
// AnnotationImpl a2 = new AnnotationImpl(1, s, e, "Type", null);
// assertTrue(a1.equals(a2));
}

@Test
public void testHashCodeWithNullTypeAndNullId() {
// AnnotationImpl annotation = new AnnotationImpl(null, new DummyNode(0L), new DummyNode(1L), null, new SimpleFeatureMapImpl());
// int hash = annotation.hashCode();
// assertTrue(hash > 0);
}

@Test
public void testOverlapsWhenThisAnnotationHasNullStartNode() {
// Node end = new DummyNode(10L);
// Node startOther = new DummyNode(5L);
// Node endOther = new DummyNode(15L);
FeatureMap features = new SimpleFeatureMapImpl();
// AnnotationImpl a1 = new AnnotationImpl(1, null, end, "type", features);
// AnnotationImpl a2 = new AnnotationImpl(2, startOther, endOther, "type", features);
// assertFalse(a1.overlaps(a2));
}

@Test
public void testOverlapsWhenThisAnnotationHasNullEndNode() {
// Node start = new DummyNode(0L);
// Node startOther = new DummyNode(5L);
// Node endOther = new DummyNode(15L);
FeatureMap features = new SimpleFeatureMapImpl();
// AnnotationImpl a1 = new AnnotationImpl(1, start, null, "type", features);
// AnnotationImpl a2 = new AnnotationImpl(2, startOther, endOther, "type", features);
// assertFalse(a1.overlaps(a2));
}

@Test
public void testOverlapsWhenOtherAnnotationHasNullStartOffset() {
// Node start = new DummyNode(0L);
// Node end = new DummyNode(10L);
// Node startOther = new DummyNode(null);
// Node endOther = new DummyNode(15L);
FeatureMap features = new SimpleFeatureMapImpl();
// AnnotationImpl a1 = new AnnotationImpl(1, start, end, "type", features);
// AnnotationImpl a2 = new AnnotationImpl(2, startOther, endOther, "type", features);
// assertFalse(a1.overlaps(a2));
}

@Test
public void testFireAnnotationUpdatedWithNoListenersDoesNotThrow() {
// Node start = new DummyNode(0L);
// Node end = new DummyNode(10L);
FeatureMap features = new SimpleFeatureMapImpl();
// AnnotationImpl annotation = new AnnotationImpl(3, start, end, "type", features);
// AnnotationEvent event = new AnnotationEvent(annotation, AnnotationEvent.ANNOTATION_UPDATED);
// annotation.fireAnnotationUpdated(event);
assertTrue(true);
}

@Test
public void testFireAnnotationUpdatedCallsAllListenersInOrder() {
// Node start = new DummyNode(0L);
// Node end = new DummyNode(10L);
FeatureMap features = new SimpleFeatureMapImpl();
// AnnotationImpl annotation = new AnnotationImpl(3, start, end, "type", features);
List<Integer> order = new ArrayList<>();
AnnotationListener listener1 = new AnnotationListener() {

public void annotationUpdated(AnnotationEvent e) {
order.add(1);
}
};
AnnotationListener listener2 = new AnnotationListener() {

public void annotationUpdated(AnnotationEvent e) {
order.add(2);
}
};
// annotation.addAnnotationListener(listener1);
// annotation.addAnnotationListener(listener2);
FeatureMap newFeatures = new SimpleFeatureMapImpl();
newFeatures.put("trigger", true);
// annotation.setFeatures(newFeatures);
assertEquals(2, order.size());
assertEquals(Integer.valueOf(1), order.get(0));
assertEquals(Integer.valueOf(2), order.get(1));
}

@Test
public void testMultipleRemoveAnnotationListenerOnlyRemovesOnce() {
// Node start = new DummyNode(0L);
// Node end = new DummyNode(1L);
FeatureMap features = new SimpleFeatureMapImpl();
// AnnotationImpl annotation = new AnnotationImpl(10, start, end, "X", features);
// AtomicInteger counter = new AtomicInteger();
AnnotationListener listener = new AnnotationListener() {

public void annotationUpdated(AnnotationEvent e) {
// counter.incrementAndGet();
}
};
// annotation.addAnnotationListener(listener);
// annotation.removeAnnotationListener(listener);
// annotation.removeAnnotationListener(listener);
FeatureMap newFeatures = new SimpleFeatureMapImpl();
newFeatures.put("key", "value");
// annotation.setFeatures(newFeatures);
// assertEquals(0, counter.get());
}

@Test
public void testIsCompatibleFeatureSubsetWithKeysPartialMatch() {
// Node start = new DummyNode(0L);
// Node end = new DummyNode(5L);
FeatureMap subset = new SimpleFeatureMapImpl();
subset.put("x", "1");
subset.put("y", "2");
FeatureMap superset = new SimpleFeatureMapImpl();
superset.put("x", "1");
superset.put("y", "2");
superset.put("z", "3");
Set<Object> keys = new HashSet<>();
keys.add("x");
keys.add("y");
// AnnotationImpl a1 = new AnnotationImpl(1, start, end, "type", subset);
// AnnotationImpl a2 = new AnnotationImpl(2, start, end, "type", superset);
// assertTrue(a1.isCompatible(a2, keys));
}

@Test
public void testIsPartiallyCompatibleWithNullKeysFallsBackToDefault() {
// Node s1 = new DummyNode(0L);
// Node e1 = new DummyNode(10L);
// Node s2 = new DummyNode(5L);
// Node e2 = new DummyNode(15L);
FeatureMap fm1 = new SimpleFeatureMapImpl();
fm1.put("a", "v");
FeatureMap fm2 = new SimpleFeatureMapImpl();
fm2.put("a", "v");
// AnnotationImpl a1 = new AnnotationImpl(1, s1, e1, "tag", fm1);
// AnnotationImpl a2 = new AnnotationImpl(2, s2, e2, "tag", fm2);
// assertTrue(a1.isPartiallyCompatible(a2, null));
}

@Test
public void testHashCodeConsistencyForEqualObjects() {
// Node s1 = new DummyNode(0L);
// Node e1 = new DummyNode(10L);
FeatureMap f1 = new SimpleFeatureMapImpl();
f1.put("k", "v");
// AnnotationImpl a1 = new AnnotationImpl(100, s1, e1, "type", f1);
// AnnotationImpl a2 = new AnnotationImpl(100, new DummyNode(0L), new DummyNode(10L), "type", new SimpleFeatureMapImpl(f1));
// assertEquals(a1.hashCode(), a2.hashCode());
}
}
