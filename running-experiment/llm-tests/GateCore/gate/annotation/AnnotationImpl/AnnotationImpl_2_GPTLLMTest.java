package gate.annotation;

import gate.Annotation;
import gate.FeatureMap;
import gate.Node;
import gate.event.AnnotationEvent;
import gate.event.AnnotationListener;
import gate.util.SimpleFeatureMapImpl;
import org.junit.Test;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AnnotationImpl_2_GPTLLMTest {

@Test
public void testGetId() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationImpl annotation = new AnnotationImpl(42, start, end, "TypeX", features);
assertEquals(Integer.valueOf(42), annotation.getId());
}

@Test
public void testGetType() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationImpl annotation = new AnnotationImpl(100, start, end, "Entity", features);
assertEquals("Entity", annotation.getType());
}

@Test
public void testGetStartNode() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationImpl annotation = new AnnotationImpl(10, start, end, "Token", features);
assertEquals(start, annotation.getStartNode());
}

@Test
public void testGetEndNode() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationImpl annotation = new AnnotationImpl(10, start, end, "Name", features);
assertEquals(end, annotation.getEndNode());
}

@Test
public void testToStringProducesExpectedContent() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationImpl annotation = new AnnotationImpl(1, start, end, "Person", features);
String str = annotation.toString();
assertTrue(str.contains("AnnotationImpl"));
assertTrue(str.contains("id=1"));
assertTrue(str.contains("type=Person"));
}

@Test
public void testEqualsIdenticalAnnotations() {
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(5L);
when(end.getOffset()).thenReturn(10L);
FeatureMap features = new SimpleFeatureMapImpl();
features.put("gender", "male");
AnnotationImpl a1 = new AnnotationImpl(99, start, end, "Pronoun", features);
AnnotationImpl a2 = new AnnotationImpl(99, start, end, "Pronoun", features);
assertTrue(a1.equals(a2));
}

@Test
public void testEqualsWithNullShouldReturnFalse() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationImpl annotation = new AnnotationImpl(1, start, end, "Test", features);
assertFalse(annotation.equals(null));
}

@Test
public void testEqualsDifferentTypeShouldReturnFalse() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationImpl annotation = new AnnotationImpl(1, start, end, "X", features);
assertFalse(annotation.equals("NotAnAnnotation"));
}

@Test
public void testHashCodeConsistency() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationImpl annotation = new AnnotationImpl(1, start, end, "Type", features);
int first = annotation.hashCode();
int second = annotation.hashCode();
assertEquals(first, second);
}

@Test
public void testCompareToSmaller() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationImpl a1 = new AnnotationImpl(1, start, end, "A", features);
AnnotationImpl a2 = new AnnotationImpl(10, start, end, "A", features);
assertTrue(a1.compareTo(a2) < 0);
}

@Test
public void testCompareToLarger() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationImpl a1 = new AnnotationImpl(10, start, end, "A", features);
AnnotationImpl a2 = new AnnotationImpl(5, start, end, "A", features);
assertTrue(a1.compareTo(a2) > 0);
}

@Test
public void testCompareToEqual() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationImpl a1 = new AnnotationImpl(7, start, end, "X", features);
AnnotationImpl a2 = new AnnotationImpl(7, start, end, "Y", features);
assertEquals(0, a1.compareTo(a2));
}

@Test
public void testSetFeaturesWillTriggerListeners() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap f1 = new SimpleFeatureMapImpl();
AnnotationImpl annotation = new AnnotationImpl(5, start, end, "Ann", f1);
final boolean[] triggered = { false };
annotation.addAnnotationListener(new AnnotationListener() {

public void annotationUpdated(AnnotationEvent e) {
triggered[0] = true;
}
});
FeatureMap f2 = new SimpleFeatureMapImpl();
annotation.setFeatures(f2);
assertSame(f2, annotation.getFeatures());
assertTrue(triggered[0]);
}

@Test
public void testOverlapsTrueWhenSpanIntersects() {
Node baseStart = mock(Node.class);
Node baseEnd = mock(Node.class);
when(baseStart.getOffset()).thenReturn(10L);
when(baseEnd.getOffset()).thenReturn(20L);
Node otherStart = mock(Node.class);
Node otherEnd = mock(Node.class);
when(otherStart.getOffset()).thenReturn(15L);
when(otherEnd.getOffset()).thenReturn(25L);
FeatureMap fm = new SimpleFeatureMapImpl();
AnnotationImpl base = new AnnotationImpl(1, baseStart, baseEnd, "A", fm);
AnnotationImpl other = new AnnotationImpl(2, otherStart, otherEnd, "B", fm);
assertTrue(base.overlaps(other));
}

@Test
public void testOverlapsFalseWhenNoIntersection() {
Node baseStart = mock(Node.class);
Node baseEnd = mock(Node.class);
when(baseStart.getOffset()).thenReturn(10L);
when(baseEnd.getOffset()).thenReturn(15L);
Node otherStart = mock(Node.class);
Node otherEnd = mock(Node.class);
when(otherStart.getOffset()).thenReturn(20L);
when(otherEnd.getOffset()).thenReturn(30L);
FeatureMap fm = new SimpleFeatureMapImpl();
AnnotationImpl base = new AnnotationImpl(1, baseStart, baseEnd, "A", fm);
AnnotationImpl other = new AnnotationImpl(2, otherStart, otherEnd, "B", fm);
assertFalse(base.overlaps(other));
}

@Test
public void testCoextensiveTrueWhenSameOffsets() {
Node start1 = mock(Node.class);
Node end1 = mock(Node.class);
when(start1.getOffset()).thenReturn(5L);
when(end1.getOffset()).thenReturn(25L);
Node start2 = mock(Node.class);
Node end2 = mock(Node.class);
when(start2.getOffset()).thenReturn(5L);
when(end2.getOffset()).thenReturn(25L);
AnnotationImpl a1 = new AnnotationImpl(1, start1, end1, "T", new SimpleFeatureMapImpl());
AnnotationImpl a2 = new AnnotationImpl(2, start2, end2, "T", new SimpleFeatureMapImpl());
assertTrue(a1.coextensive(a2));
}

@Test
public void testCoextensiveFalseWhenOffsetsDiffer() {
Node start1 = mock(Node.class);
Node end1 = mock(Node.class);
when(start1.getOffset()).thenReturn(5L);
when(end1.getOffset()).thenReturn(15L);
Node start2 = mock(Node.class);
Node end2 = mock(Node.class);
when(start2.getOffset()).thenReturn(6L);
when(end2.getOffset()).thenReturn(15L);
AnnotationImpl a1 = new AnnotationImpl(1, start1, end1, "T", new SimpleFeatureMapImpl());
AnnotationImpl a2 = new AnnotationImpl(2, start2, end2, "T", new SimpleFeatureMapImpl());
assertFalse(a1.coextensive(a2));
}

@Test
public void testWithinSpanOfTrue() {
Node innerStart = mock(Node.class);
Node innerEnd = mock(Node.class);
when(innerStart.getOffset()).thenReturn(10L);
when(innerEnd.getOffset()).thenReturn(20L);
AnnotationImpl inner = new AnnotationImpl(1, innerStart, innerEnd, "Inner", new SimpleFeatureMapImpl());
Node outerStart = mock(Node.class);
Node outerEnd = mock(Node.class);
when(outerStart.getOffset()).thenReturn(5L);
when(outerEnd.getOffset()).thenReturn(25L);
AnnotationImpl outer = new AnnotationImpl(2, outerStart, outerEnd, "Outer", new SimpleFeatureMapImpl());
assertTrue(inner.withinSpanOf(outer));
}

@Test
public void testWithinSpanOfFalse() {
Node innerStart = mock(Node.class);
Node innerEnd = mock(Node.class);
when(innerStart.getOffset()).thenReturn(5L);
when(innerEnd.getOffset()).thenReturn(30L);
AnnotationImpl inner = new AnnotationImpl(1, innerStart, innerEnd, "Inner", new SimpleFeatureMapImpl());
Node outerStart = mock(Node.class);
Node outerEnd = mock(Node.class);
when(outerStart.getOffset()).thenReturn(10L);
when(outerEnd.getOffset()).thenReturn(20L);
AnnotationImpl outer = new AnnotationImpl(2, outerStart, outerEnd, "Outer", new SimpleFeatureMapImpl());
assertFalse(inner.withinSpanOf(outer));
}

@Test
public void testIsCompatibleTrueWhenSubsumedFeatures() {
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(0L);
when(end.getOffset()).thenReturn(5L);
FeatureMap f1 = new SimpleFeatureMapImpl();
f1.put("x", "y");
FeatureMap f2 = new SimpleFeatureMapImpl();
f2.put("x", "y");
f2.put("extra", "value");
AnnotationImpl a1 = new AnnotationImpl(1, start, end, "T", f1);
AnnotationImpl a2 = new AnnotationImpl(2, start, end, "T", f2);
assertTrue(a1.isCompatible(a2));
}

@Test
public void testIsPartiallyCompatibleTrueWithOverlapAndSubsumed() {
Node start1 = mock(Node.class);
Node end1 = mock(Node.class);
when(start1.getOffset()).thenReturn(10L);
when(end1.getOffset()).thenReturn(20L);
Node start2 = mock(Node.class);
Node end2 = mock(Node.class);
when(start2.getOffset()).thenReturn(18L);
when(end2.getOffset()).thenReturn(30L);
FeatureMap fm1 = new SimpleFeatureMapImpl();
fm1.put("x", "z");
FeatureMap fm2 = new SimpleFeatureMapImpl();
fm2.put("x", "z");
fm2.put("y", "x");
AnnotationImpl a1 = new AnnotationImpl(1, start1, end1, "A", fm1);
AnnotationImpl a2 = new AnnotationImpl(2, start2, end2, "B", fm2);
assertTrue(a1.isPartiallyCompatible(a2));
}

@Test
public void testAddAndRemoveAnnotationListener() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationImpl annotation = new AnnotationImpl(50, start, end, "CustomType", features);
AnnotationListener listener = mock(AnnotationListener.class);
annotation.addAnnotationListener(listener);
annotation.fireAnnotationUpdated(new AnnotationEvent(annotation, AnnotationEvent.FEATURES_UPDATED));
verify(listener, times(1)).annotationUpdated(any(AnnotationEvent.class));
annotation.removeAnnotationListener(listener);
annotation.fireAnnotationUpdated(new AnnotationEvent(annotation, AnnotationEvent.FEATURES_UPDATED));
verifyNoMoreInteractions(listener);
}

@Test
public void testEqualsDifferentFeatureSet() {
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(10L);
when(end.getOffset()).thenReturn(20L);
FeatureMap features1 = new SimpleFeatureMapImpl();
FeatureMap features2 = new SimpleFeatureMapImpl();
features1.put("a", "1");
features2.put("a", "2");
AnnotationImpl a1 = new AnnotationImpl(1, start, end, "Type", features1);
AnnotationImpl a2 = new AnnotationImpl(1, start, end, "Type", features2);
assertFalse(a1.equals(a2));
}

@Test
public void testHashCodeDifferenceOnType() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationImpl a1 = new AnnotationImpl(1, start, end, "Person", features);
AnnotationImpl a2 = new AnnotationImpl(1, start, end, "Organization", features);
assertNotEquals(a1.hashCode(), a2.hashCode());
}

@Test
public void testIsCompatibleWithNullFeatureMapInTargetAnnotation() {
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(0L);
when(end.getOffset()).thenReturn(10L);
FeatureMap features = new SimpleFeatureMapImpl();
features.put("x", "y");
AnnotationImpl a1 = new AnnotationImpl(1, start, end, "T", features);
AnnotationImpl a2 = new AnnotationImpl(2, start, end, "T", null);
assertTrue(a1.isCompatible(a2));
}

@Test
public void testIsPartiallyCompatibleWithNullFeatureMapInTarget() {
Node start1 = mock(Node.class);
Node end1 = mock(Node.class);
Node start2 = mock(Node.class);
Node end2 = mock(Node.class);
when(start1.getOffset()).thenReturn(10L);
when(end1.getOffset()).thenReturn(30L);
when(start2.getOffset()).thenReturn(20L);
when(end2.getOffset()).thenReturn(40L);
FeatureMap features = new SimpleFeatureMapImpl();
features.put("key", "value");
AnnotationImpl base = new AnnotationImpl(1, start1, end1, "X", features);
AnnotationImpl target = new AnnotationImpl(2, start2, end2, "X", null);
assertTrue(base.isPartiallyCompatible(target));
}

@Test
public void testIsCompatibleWithPartialFeatureKeySubsetThatDoesNotMatch() {
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(0L);
when(end.getOffset()).thenReturn(10L);
FeatureMap baseFeatures = new SimpleFeatureMapImpl();
baseFeatures.put("alpha", "1");
FeatureMap otherFeatures = new SimpleFeatureMapImpl();
otherFeatures.put("alpha", "something-else");
Set<Object> keys = new HashSet<Object>();
keys.add("alpha");
AnnotationImpl a1 = new AnnotationImpl(10, start, end, "Type", baseFeatures);
AnnotationImpl a2 = new AnnotationImpl(11, start, end, "Type", otherFeatures);
assertFalse(a1.isCompatible(a2, keys));
}

@Test
public void testCoextensiveOneSideNullStartNode() {
Node node = mock(Node.class);
when(node.getOffset()).thenReturn(10L);
AnnotationImpl a1 = new AnnotationImpl(1, null, node, "X", new SimpleFeatureMapImpl());
AnnotationImpl a2 = new AnnotationImpl(2, node, node, "X", new SimpleFeatureMapImpl());
assertFalse(a1.coextensive(a2));
}

@Test
public void testCoextensiveEndNodeMismatchOffset() {
Node start = mock(Node.class);
Node end1 = mock(Node.class);
Node end2 = mock(Node.class);
when(start.getOffset()).thenReturn(10L);
when(end1.getOffset()).thenReturn(20L);
when(end2.getOffset()).thenReturn(25L);
AnnotationImpl a1 = new AnnotationImpl(1, start, end1, "Type", new SimpleFeatureMapImpl());
AnnotationImpl a2 = new AnnotationImpl(2, start, end2, "Type", new SimpleFeatureMapImpl());
assertFalse(a1.coextensive(a2));
}

@Test
public void testOverlapsNullStartOffsetInTarget() {
Node baseStart = mock(Node.class);
Node baseEnd = mock(Node.class);
Node otherStart = mock(Node.class);
Node otherEnd = mock(Node.class);
when(baseStart.getOffset()).thenReturn(10L);
when(baseEnd.getOffset()).thenReturn(20L);
when(otherStart.getOffset()).thenReturn(null);
when(otherEnd.getOffset()).thenReturn(15L);
AnnotationImpl base = new AnnotationImpl(1, baseStart, baseEnd, "Test", new SimpleFeatureMapImpl());
AnnotationImpl other = new AnnotationImpl(2, otherStart, otherEnd, "Test", new SimpleFeatureMapImpl());
assertFalse(base.overlaps(other));
}

@Test
public void testWithinSpanOfWithNullOffsets() {
Node targetStart = mock(Node.class);
Node targetEnd = mock(Node.class);
when(targetStart.getOffset()).thenReturn(null);
when(targetEnd.getOffset()).thenReturn(30L);
Node innerStart = mock(Node.class);
Node innerEnd = mock(Node.class);
when(innerStart.getOffset()).thenReturn(10L);
when(innerEnd.getOffset()).thenReturn(20L);
AnnotationImpl inner = new AnnotationImpl(1, innerStart, innerEnd, "Inner", new SimpleFeatureMapImpl());
AnnotationImpl target = new AnnotationImpl(2, targetStart, targetEnd, "Outer", new SimpleFeatureMapImpl());
assertFalse(inner.withinSpanOf(target));
}

@Test
public void testAddAnnotationListenerTwice_DoesNotDuplicate() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationImpl annotation = new AnnotationImpl(100, start, end, "T", features);
AnnotationListener listener = mock(AnnotationListener.class);
annotation.addAnnotationListener(listener);
annotation.addAnnotationListener(listener);
annotation.fireAnnotationUpdated(new AnnotationEvent(annotation, AnnotationEvent.ANNOTATION_UPDATED));
verify(listener, times(1)).annotationUpdated(any(AnnotationEvent.class));
}

@Test
public void testRemoveNonexistentAnnotationListenerHasNoEffect() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationImpl annotation = new AnnotationImpl(200, start, end, "Event", features);
AnnotationListener listener1 = mock(AnnotationListener.class);
AnnotationListener listener2 = mock(AnnotationListener.class);
annotation.addAnnotationListener(listener1);
annotation.removeAnnotationListener(listener2);
annotation.fireAnnotationUpdated(new AnnotationEvent(annotation, AnnotationEvent.ANNOTATION_UPDATED));
verify(listener1, times(1)).annotationUpdated(any(AnnotationEvent.class));
verify(listener2, never()).annotationUpdated(any(AnnotationEvent.class));
}

@Test
public void testEqualsWithNullStartNodeInOneAnnotation() {
Node end = mock(Node.class);
when(end.getOffset()).thenReturn(10L);
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationImpl a1 = new AnnotationImpl(1, null, end, "Type", features);
Node start2 = mock(Node.class);
when(start2.getOffset()).thenReturn(0L);
AnnotationImpl a2 = new AnnotationImpl(1, start2, end, "Type", features);
assertFalse(a1.equals(a2));
}

@Test
public void testEqualsWithNullEndNodeInOneAnnotation() {
Node start = mock(Node.class);
when(start.getOffset()).thenReturn(0L);
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationImpl a1 = new AnnotationImpl(1, start, null, "Type", features);
Node end2 = mock(Node.class);
when(end2.getOffset()).thenReturn(10L);
AnnotationImpl a2 = new AnnotationImpl(1, start, end2, "Type", features);
assertFalse(a1.equals(a2));
}

@Test
public void testEqualsWithStartNodeOffsetMismatchDueToNull() {
Node start1 = mock(Node.class);
Node end = mock(Node.class);
when(start1.getOffset()).thenReturn(null);
when(end.getOffset()).thenReturn(10L);
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationImpl a1 = new AnnotationImpl(1, start1, end, "Type", features);
Node start2 = mock(Node.class);
when(start2.getOffset()).thenReturn(0L);
AnnotationImpl a2 = new AnnotationImpl(1, start2, end, "Type", features);
assertFalse(a1.equals(a2));
}

@Test
public void testCompareToThrowsClassCastExceptionForInvalidType() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationImpl annotation = new AnnotationImpl(1, start, end, "Person", features);
boolean exceptionThrown = false;
try {
annotation.compareTo("NotAnAnnotation");
} catch (ClassCastException e) {
exceptionThrown = true;
}
assertTrue(exceptionThrown);
}

@Test
public void testSetFeaturesDisablesOldFeatureMapListener() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap original = mock(FeatureMap.class);
FeatureMap replacement = new SimpleFeatureMapImpl();
AnnotationImpl annotation = new AnnotationImpl(1, start, end, "Event", original);
annotation.addAnnotationListener(new AnnotationListener() {

public void annotationUpdated(AnnotationEvent e) {
}
});
annotation.setFeatures(replacement);
verify(original, times(1)).removeFeatureMapListener(any());
}

@Test
public void testIsCompatibleWithPartialFeatureSubsetMissingKeys() {
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(0L);
when(end.getOffset()).thenReturn(10L);
FeatureMap baseFeatures = new SimpleFeatureMapImpl();
baseFeatures.put("k1", "v1");
baseFeatures.put("k2", "v2");
FeatureMap otherFeatures = new SimpleFeatureMapImpl();
otherFeatures.put("k1", "v1");
Set<Object> relevantKeys = new HashSet<Object>();
relevantKeys.add("k2");
AnnotationImpl base = new AnnotationImpl(1, start, end, "Type", baseFeatures);
AnnotationImpl other = new AnnotationImpl(2, start, end, "Type", otherFeatures);
assertFalse(base.isCompatible(other, relevantKeys));
}

@Test
public void testIsPartiallyCompatibleWithPartialFeatureSubsetMissingValue() {
Node start1 = mock(Node.class);
Node end1 = mock(Node.class);
Node start2 = mock(Node.class);
Node end2 = mock(Node.class);
when(start1.getOffset()).thenReturn(0L);
when(end1.getOffset()).thenReturn(15L);
when(start2.getOffset()).thenReturn(10L);
when(end2.getOffset()).thenReturn(25L);
FeatureMap features1 = new SimpleFeatureMapImpl();
features1.put("x", "MATCH");
FeatureMap features2 = new SimpleFeatureMapImpl();
features2.put("x", "DIFFERENT");
Set<Object> subset = new HashSet<Object>();
subset.add("x");
AnnotationImpl a1 = new AnnotationImpl(1, start1, end1, "T", features1);
AnnotationImpl a2 = new AnnotationImpl(2, start2, end2, "T", features2);
assertFalse(a1.isPartiallyCompatible(a2, subset));
}

@Test
public void testOverlapsWithEqualBoundariesShouldReturnTrue() {
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(10L);
when(end.getOffset()).thenReturn(15L);
AnnotationImpl a1 = new AnnotationImpl(1, start, end, "X", new SimpleFeatureMapImpl());
AnnotationImpl a2 = new AnnotationImpl(2, start, end, "X", new SimpleFeatureMapImpl());
assertTrue(a1.overlaps(a2));
}

@Test
public void testOverlapsWithIdenticalZeroLengthAnnotationsShouldReturnFalse() {
Node start1 = mock(Node.class);
Node end1 = mock(Node.class);
when(start1.getOffset()).thenReturn(10L);
when(end1.getOffset()).thenReturn(10L);
Node start2 = mock(Node.class);
Node end2 = mock(Node.class);
when(start2.getOffset()).thenReturn(10L);
when(end2.getOffset()).thenReturn(10L);
AnnotationImpl a1 = new AnnotationImpl(1, start1, end1, "X", new SimpleFeatureMapImpl());
AnnotationImpl a2 = new AnnotationImpl(2, start2, end2, "X", new SimpleFeatureMapImpl());
assertFalse(a1.overlaps(a2));
}

@Test
public void testWithinSpanOfWhereStartEqualsOuterStartAndEndEqualsOuterEnd() {
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(0L);
when(end.getOffset()).thenReturn(20L);
AnnotationImpl inner = new AnnotationImpl(1, start, end, "Same", new SimpleFeatureMapImpl());
AnnotationImpl outer = new AnnotationImpl(2, start, end, "Same", new SimpleFeatureMapImpl());
assertTrue(inner.withinSpanOf(outer));
}

@Test
public void testAddAnnotationListenerStartTrackingOnFirstAdd() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap features = mock(FeatureMap.class);
AnnotationImpl annotation = new AnnotationImpl(1, start, end, "Track", features);
AnnotationListener listener = mock(AnnotationListener.class);
annotation.addAnnotationListener(listener);
verify(features, times(1)).addFeatureMapListener(any());
}

@Test
public void testFeatureMapEventsTriggerAnnotationEvent() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationImpl annotation = new AnnotationImpl(1, start, end, "Event", features);
final boolean[] triggered = { false };
annotation.addAnnotationListener(new AnnotationListener() {

public void annotationUpdated(AnnotationEvent e) {
triggered[0] = true;
}
});
features.put("test", "value");
assertTrue(triggered[0]);
}

@Test
public void testIsCompatibleReturnsFalseWhenOffsetsMismatch() {
Node start1 = mock(Node.class);
Node end1 = mock(Node.class);
when(start1.getOffset()).thenReturn(0L);
when(end1.getOffset()).thenReturn(10L);
Node start2 = mock(Node.class);
Node end2 = mock(Node.class);
when(start2.getOffset()).thenReturn(5L);
when(end2.getOffset()).thenReturn(15L);
FeatureMap f1 = new SimpleFeatureMapImpl();
FeatureMap f2 = new SimpleFeatureMapImpl();
f1.put("x", "1");
f2.put("x", "1");
AnnotationImpl a1 = new AnnotationImpl(1, start1, end1, "A", f1);
AnnotationImpl a2 = new AnnotationImpl(2, start2, end2, "A", f2);
assertFalse(a1.isCompatible(a2));
}

@Test
public void testEqualsWithNullTypeInOneAnnotation() {
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(5L);
when(end.getOffset()).thenReturn(15L);
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationImpl a1 = new AnnotationImpl(1, start, end, null, features);
AnnotationImpl a2 = new AnnotationImpl(1, start, end, "Person", features);
assertFalse(a1.equals(a2));
}

@Test
public void testEqualsWithBothTypesNull() {
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(5L);
when(end.getOffset()).thenReturn(15L);
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationImpl a1 = new AnnotationImpl(1, start, end, null, features);
AnnotationImpl a2 = new AnnotationImpl(1, start, end, null, features);
assertTrue(a1.equals(a2));
}

@Test
public void testEqualsWithNullIdInOneAnnotation() {
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(5L);
when(end.getOffset()).thenReturn(15L);
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationImpl a1 = new AnnotationImpl(null, start, end, "Name", features);
AnnotationImpl a2 = new AnnotationImpl(1, start, end, "Name", features);
assertFalse(a1.equals(a2));
}

@Test
public void testEqualsWithBothIdsNull() {
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(5L);
when(end.getOffset()).thenReturn(15L);
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationImpl a1 = new AnnotationImpl(null, start, end, "Name", features);
AnnotationImpl a2 = new AnnotationImpl(null, start, end, "Name", features);
assertTrue(a1.equals(a2));
}

@Test
public void testEqualsWithStartNodeOffsetMismatchDueToOneNull() {
Node start1 = mock(Node.class);
Node end1 = mock(Node.class);
when(start1.getOffset()).thenReturn(null);
when(end1.getOffset()).thenReturn(15L);
Node start2 = mock(Node.class);
Node end2 = mock(Node.class);
when(start2.getOffset()).thenReturn(5L);
when(end2.getOffset()).thenReturn(15L);
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationImpl a1 = new AnnotationImpl(1, start1, end1, "X", features);
AnnotationImpl a2 = new AnnotationImpl(1, start2, end2, "X", features);
assertFalse(a1.equals(a2));
}

@Test
public void testEqualsWithDifferentEndOffsetsOnly() {
Node start = mock(Node.class);
Node end1 = mock(Node.class);
Node end2 = mock(Node.class);
when(start.getOffset()).thenReturn(0L);
when(end1.getOffset()).thenReturn(10L);
when(end2.getOffset()).thenReturn(20L);
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationImpl a1 = new AnnotationImpl(1, start, end1, "Item", features);
AnnotationImpl a2 = new AnnotationImpl(1, start, end2, "Item", features);
assertFalse(a1.equals(a2));
}

@Test
public void testHashCodeWithNullTypeAndNullId() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationImpl annotation = new AnnotationImpl(null, start, end, null, features);
int hash = annotation.hashCode();
assertEquals(17, hash);
}

@Test
public void testCoextensiveWhenOneEndNodeNull() {
Node start1 = mock(Node.class);
Node end1 = null;
Node start2 = mock(Node.class);
Node end2 = mock(Node.class);
when(start1.getOffset()).thenReturn(5L);
when(start2.getOffset()).thenReturn(5L);
when(end2.getOffset()).thenReturn(15L);
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationImpl a1 = new AnnotationImpl(1, start1, end1, "Item", features);
AnnotationImpl a2 = new AnnotationImpl(1, start2, end2, "Item", features);
assertFalse(a1.coextensive(a2));
}

@Test
public void testOverlapsWithStartEqualsOtherEnd() {
Node start1 = mock(Node.class);
Node end1 = mock(Node.class);
Node start2 = mock(Node.class);
Node end2 = mock(Node.class);
when(start1.getOffset()).thenReturn(20L);
when(end1.getOffset()).thenReturn(30L);
when(start2.getOffset()).thenReturn(10L);
when(end2.getOffset()).thenReturn(20L);
AnnotationImpl a1 = new AnnotationImpl(1, start1, end1, "A", new SimpleFeatureMapImpl());
AnnotationImpl a2 = new AnnotationImpl(2, start2, end2, "B", new SimpleFeatureMapImpl());
assertFalse(a1.overlaps(a2));
}

@Test
public void testOverlapsWithEndEqualsOtherStart() {
Node start1 = mock(Node.class);
Node end1 = mock(Node.class);
Node start2 = mock(Node.class);
Node end2 = mock(Node.class);
when(start1.getOffset()).thenReturn(10L);
when(end1.getOffset()).thenReturn(20L);
when(start2.getOffset()).thenReturn(20L);
when(end2.getOffset()).thenReturn(30L);
AnnotationImpl a1 = new AnnotationImpl(1, start1, end1, "A", new SimpleFeatureMapImpl());
AnnotationImpl a2 = new AnnotationImpl(2, start2, end2, "B", new SimpleFeatureMapImpl());
assertFalse(a1.overlaps(a2));
}

@Test
public void testIsCompatibleWithEmptyFeatureSubset() {
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(0L);
when(end.getOffset()).thenReturn(10L);
FeatureMap baseMap = new SimpleFeatureMapImpl();
baseMap.put("a", "b");
FeatureMap targetMap = new SimpleFeatureMapImpl();
targetMap.put("a", "b");
Set<Object> emptyFeatureSet = new HashSet<Object>();
AnnotationImpl base = new AnnotationImpl(1, start, end, "Test", baseMap);
AnnotationImpl target = new AnnotationImpl(2, start, end, "Test", targetMap);
assertTrue(base.isCompatible(target, emptyFeatureSet));
}

@Test
public void testIsPartiallyCompatibleWithNullTargetAnnotation() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap fm = new SimpleFeatureMapImpl();
fm.put("k", "v");
AnnotationImpl annotation = new AnnotationImpl(1, start, end, "Type", fm);
assertFalse(annotation.isPartiallyCompatible(null));
}

@Test
public void testIsCompatibleWithNullFeatureSubsetDelegatesToOtherMethod() {
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(0L);
when(end.getOffset()).thenReturn(10L);
FeatureMap f1 = new SimpleFeatureMapImpl();
f1.put("key", "value");
FeatureMap f2 = new SimpleFeatureMapImpl();
f2.put("key", "value");
f2.put("extra", "yes");
AnnotationImpl a1 = new AnnotationImpl(1, start, end, "X", f1);
AnnotationImpl a2 = new AnnotationImpl(2, start, end, "X", f2);
assertTrue(a1.isCompatible(a2, null));
}

@Test
public void testIsPartiallyCompatibleWithNullFeatureSubsetDelegatesToOtherMethod() {
Node start1 = mock(Node.class);
Node end1 = mock(Node.class);
Node start2 = mock(Node.class);
Node end2 = mock(Node.class);
when(start1.getOffset()).thenReturn(0L);
when(end1.getOffset()).thenReturn(20L);
when(start2.getOffset()).thenReturn(15L);
when(end2.getOffset()).thenReturn(25L);
FeatureMap f1 = new SimpleFeatureMapImpl();
f1.put("x", "v");
FeatureMap f2 = new SimpleFeatureMapImpl();
f2.put("x", "v");
AnnotationImpl a1 = new AnnotationImpl(1, start1, end1, "A", f1);
AnnotationImpl a2 = new AnnotationImpl(2, start2, end2, "A", f2);
assertTrue(a1.isPartiallyCompatible(a2, null));
}

@Test
public void testAddAnnotationListenerWithNullListenerDoesNotThrow() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationImpl annotation = new AnnotationImpl(101, start, end, "Test", features);
annotation.addAnnotationListener(null);
annotation.fireAnnotationUpdated(new AnnotationEvent(annotation, AnnotationEvent.FEATURES_UPDATED));
}

@Test
public void testRemoveAnnotationListenerWhenNoneAdded() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationImpl annotation = new AnnotationImpl(101, start, end, "Test", features);
AnnotationListener listener = mock(AnnotationListener.class);
annotation.removeAnnotationListener(listener);
annotation.fireAnnotationUpdated(new AnnotationEvent(annotation, AnnotationEvent.FEATURES_UPDATED));
verify(listener, never()).annotationUpdated(any(AnnotationEvent.class));
}

@Test
public void testRemoveAnnotationListenerWithNullDoesNotThrow() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationImpl annotation = new AnnotationImpl(101, start, end, "Test", features);
annotation.removeAnnotationListener(null);
annotation.fireAnnotationUpdated(new AnnotationEvent(annotation, AnnotationEvent.FEATURES_UPDATED));
}

@Test
public void testAddMultipleDistinctAnnotationListenersAllTriggered() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationImpl annotation = new AnnotationImpl(201, start, end, "Type", features);
AnnotationListener listener1 = mock(AnnotationListener.class);
AnnotationListener listener2 = mock(AnnotationListener.class);
annotation.addAnnotationListener(listener1);
annotation.addAnnotationListener(listener2);
annotation.fireAnnotationUpdated(new AnnotationEvent(annotation, AnnotationEvent.FEATURES_UPDATED));
verify(listener1, times(1)).annotationUpdated(any(AnnotationEvent.class));
verify(listener2, times(1)).annotationUpdated(any(AnnotationEvent.class));
}

@Test
public void testSetFeaturesWithNullFeaturesRemovesOldListenersAndSkipsAdd() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap currentFeatures = mock(FeatureMap.class);
AnnotationImpl annotation = new AnnotationImpl(301, start, end, "T", currentFeatures);
AnnotationListener listener = mock(AnnotationListener.class);
annotation.addAnnotationListener(listener);
FeatureMap newFeatures = mock(FeatureMap.class);
annotation.setFeatures(newFeatures);
verify(currentFeatures, times(1)).removeFeatureMapListener(any());
verify(newFeatures, times(1)).addFeatureMapListener(any());
}

@Test
public void testToStringHandlesNullComponentsSafely() {
AnnotationImpl annotation = new AnnotationImpl(null, null, null, null, null);
String output = annotation.toString();
assertNotNull(output);
assertTrue(output.contains("AnnotationImpl"));
}

@Test
public void testEqualsWithBothFeaturesNullReturnsTrue() {
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(1L);
when(end.getOffset()).thenReturn(5L);
AnnotationImpl a1 = new AnnotationImpl(1, start, end, "T", null);
AnnotationImpl a2 = new AnnotationImpl(1, start, end, "T", null);
assertTrue(a1.equals(a2));
}

@Test
public void testEqualsWithFeaturesNullInOneOnlyReturnsFalse() {
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(1L);
when(end.getOffset()).thenReturn(5L);
FeatureMap f = new SimpleFeatureMapImpl();
f.put("k", "v");
AnnotationImpl a1 = new AnnotationImpl(1, start, end, "T", null);
AnnotationImpl a2 = new AnnotationImpl(1, start, end, "T", f);
assertFalse(a1.equals(a2));
}

@Test
public void testOverlapsWhenTargetHasNullNodes() {
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(0L);
when(end.getOffset()).thenReturn(10L);
AnnotationImpl base = new AnnotationImpl(1, start, end, "Type", new SimpleFeatureMapImpl());
AnnotationImpl target = new AnnotationImpl(2, null, null, "Other", new SimpleFeatureMapImpl());
assertFalse(base.overlaps(target));
}

@Test
public void testOverlapsWhenEndBeforeStartReturnsFalse() {
Node start = mock(Node.class);
Node end = mock(Node.class);
Node targetStart = mock(Node.class);
Node targetEnd = mock(Node.class);
when(start.getOffset()).thenReturn(20L);
when(end.getOffset()).thenReturn(30L);
when(targetStart.getOffset()).thenReturn(0L);
when(targetEnd.getOffset()).thenReturn(10L);
AnnotationImpl base = new AnnotationImpl(1, start, end, "Base", new SimpleFeatureMapImpl());
AnnotationImpl target = new AnnotationImpl(2, targetStart, targetEnd, "Target", new SimpleFeatureMapImpl());
assertFalse(base.overlaps(target));
}

@Test
public void testFeatureMapEventDelegationViaEventsHandler() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationImpl annotation = new AnnotationImpl(1, start, end, "Event", features);
final boolean[] called = { false };
annotation.addAnnotationListener(new AnnotationListener() {

public void annotationUpdated(AnnotationEvent e) {
called[0] = true;
}
});
features.put("newFeature", "value");
assertTrue(called[0]);
}

@Test
public void testSetFeaturesDoesNotAddListenerWhenNoAnnotationListenersPresent() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap oldFeatures = mock(FeatureMap.class);
FeatureMap newFeatures = mock(FeatureMap.class);
AnnotationImpl annotation = new AnnotationImpl(1, start, end, "TypeX", oldFeatures);
annotation.setFeatures(newFeatures);
verify(oldFeatures, times(0)).removeFeatureMapListener(any());
verify(newFeatures, times(0)).addFeatureMapListener(any());
}

@Test
public void testSetFeaturesReplacesFeatureMapWithoutAnnotationListeners() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap oldFeatures = new SimpleFeatureMapImpl();
oldFeatures.put("k1", "v1");
FeatureMap newFeatures = new SimpleFeatureMapImpl();
newFeatures.put("k2", "v2");
AnnotationImpl annotation = new AnnotationImpl(1, start, end, "TypeX", oldFeatures);
annotation.setFeatures(newFeatures);
assertEquals("v2", annotation.getFeatures().get("k2"));
assertNull(annotation.getFeatures().get("k1"));
}

@Test
public void testEventsHandlerSerialVersionUIDDefined() {
long expectedSerialVersion = 2608156420244752907L;
assertEquals(2608156420244752907L, AnnotationImpl.EventsHandler.serialVersionUID);
}

@Test
public void testCompareToWithNullIdInThisThrowsNullPointerException() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationImpl a1 = new AnnotationImpl(null, start, end, "Type", features);
AnnotationImpl a2 = new AnnotationImpl(2, start, end, "Type", features);
boolean exceptionThrown = false;
try {
a1.compareTo(a2);
} catch (NullPointerException e) {
exceptionThrown = true;
}
assertTrue(exceptionThrown);
}

@Test
public void testCompareToWithNullIdInOtherThrowsNullPointerException() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationImpl a1 = new AnnotationImpl(1, start, end, "Type", features);
AnnotationImpl a2 = new AnnotationImpl(null, start, end, "Type", features);
boolean exceptionThrown = false;
try {
a1.compareTo(a2);
} catch (NullPointerException e) {
exceptionThrown = true;
}
assertTrue(exceptionThrown);
}

@Test
public void testEqualsWithMismatchedStartNodeObjectsWithSameOffset() {
Node start1 = mock(Node.class);
Node start2 = mock(Node.class);
Node end = mock(Node.class);
when(start1.getOffset()).thenReturn(0L);
when(start2.getOffset()).thenReturn(0L);
when(end.getOffset()).thenReturn(10L);
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationImpl a1 = new AnnotationImpl(1, start1, end, "Label", features);
AnnotationImpl a2 = new AnnotationImpl(1, start2, end, "Label", features);
assertTrue(a1.equals(a2));
}

@Test
public void testEventsHandlerFeatureMapUpdatedFiresEvent() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationImpl annotation = new AnnotationImpl(1, start, end, "Event", features);
final boolean[] eventFired = { false };
annotation.addAnnotationListener(new AnnotationListener() {

public void annotationUpdated(AnnotationEvent e) {
eventFired[0] = true;
}
});
annotation.eventHandler.featureMapUpdated();
assertTrue(eventFired[0]);
}

@Test
public void testFireAnnotationUpdatedWithNoListenersDoesNotThrow() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationImpl annotation = new AnnotationImpl(1, start, end, "Test", features);
AnnotationEvent event = new AnnotationEvent(annotation, AnnotationEvent.ANNOTATION_UPDATED);
annotation.fireAnnotationUpdated(event);
}

@Test
public void testEqualsWithAllNullFields() {
AnnotationImpl a1 = new AnnotationImpl(null, null, null, null, null);
AnnotationImpl a2 = new AnnotationImpl(null, null, null, null, null);
assertTrue(a1.equals(a2));
}

@Test
public void testToStringIncludesNullValuesGracefully() {
AnnotationImpl annotation = new AnnotationImpl(null, null, null, null, null);
String text = annotation.toString();
assertNotNull(text);
assertTrue(text.contains("AnnotationImpl"));
assertTrue(text.contains("id=null"));
assertTrue(text.contains("type=null"));
}

@Test
public void testIsPartiallyCompatibleWithStartEqualsEndOffsetOverlap() {
Node start1 = mock(Node.class);
Node end1 = mock(Node.class);
Node start2 = mock(Node.class);
Node end2 = mock(Node.class);
when(start1.getOffset()).thenReturn(10L);
when(end1.getOffset()).thenReturn(20L);
when(start2.getOffset()).thenReturn(20L);
when(end2.getOffset()).thenReturn(30L);
FeatureMap baseMap = new SimpleFeatureMapImpl();
baseMap.put("key", "value");
FeatureMap otherMap = new SimpleFeatureMapImpl();
otherMap.put("key", "value");
AnnotationImpl a1 = new AnnotationImpl(1, start1, end1, "Test", baseMap);
AnnotationImpl a2 = new AnnotationImpl(2, start2, end2, "Test", otherMap);
assertFalse(a1.isPartiallyCompatible(a2));
}

@Test
public void testIsCompatibleWithSameOffsetButSubsetFeatureKeysMissing() {
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(5L);
when(end.getOffset()).thenReturn(15L);
FeatureMap features1 = new SimpleFeatureMapImpl();
features1.put("a", "1");
features1.put("b", "2");
FeatureMap features2 = new SimpleFeatureMapImpl();
features2.put("a", "1");
Set<Object> keys = new HashSet<Object>();
keys.add("b");
AnnotationImpl a1 = new AnnotationImpl(1, start, end, "T", features1);
AnnotationImpl a2 = new AnnotationImpl(2, start, end, "T", features2);
assertFalse(a1.isCompatible(a2, keys));
}

@Test
public void testEqualsWithDifferentEndNodeOffsetsShouldReturnFalse() {
Node start1 = mock(Node.class);
Node end1 = mock(Node.class);
Node start2 = mock(Node.class);
Node end2 = mock(Node.class);
when(start1.getOffset()).thenReturn(0L);
when(end1.getOffset()).thenReturn(10L);
when(start2.getOffset()).thenReturn(0L);
when(end2.getOffset()).thenReturn(20L);
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationImpl a1 = new AnnotationImpl(1, start1, end1, "Type", features);
AnnotationImpl a2 = new AnnotationImpl(1, start2, end2, "Type", features);
assertFalse(a1.equals(a2));
}

@Test
public void testEqualsWhenOffsetsAreBothNullShouldReturnTrue() {
Node start1 = mock(Node.class);
Node end1 = mock(Node.class);
Node start2 = mock(Node.class);
Node end2 = mock(Node.class);
when(start1.getOffset()).thenReturn(null);
when(end1.getOffset()).thenReturn(null);
when(start2.getOffset()).thenReturn(null);
when(end2.getOffset()).thenReturn(null);
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationImpl a1 = new AnnotationImpl(1, start1, end1, "Entity", features);
AnnotationImpl a2 = new AnnotationImpl(1, start2, end2, "Entity", features);
assertTrue(a1.equals(a2));
}

@Test
public void testCoextensiveReturnsFalseWhenStartOffsetNullOnlyForOneAnnotation() {
Node start1 = mock(Node.class);
Node end1 = mock(Node.class);
Node start2 = mock(Node.class);
Node end2 = mock(Node.class);
when(start1.getOffset()).thenReturn(null);
when(end1.getOffset()).thenReturn(10L);
when(start2.getOffset()).thenReturn(0L);
when(end2.getOffset()).thenReturn(10L);
AnnotationImpl a1 = new AnnotationImpl(1, start1, end1, "Type", new SimpleFeatureMapImpl());
AnnotationImpl a2 = new AnnotationImpl(2, start2, end2, "Type", new SimpleFeatureMapImpl());
assertFalse(a1.coextensive(a2));
}

@Test
public void testCompareToWhenIdsAreNullInBothAnnotationsThrowsNullPointerException() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationImpl a1 = new AnnotationImpl(null, start, end, "X", features);
AnnotationImpl a2 = new AnnotationImpl(null, start, end, "X", features);
boolean exceptionThrown = false;
try {
a1.compareTo(a2);
} catch (NullPointerException ex) {
exceptionThrown = true;
}
assertTrue(exceptionThrown);
}

@Test
public void testHashCodeWithOnlyTypeNonNull() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationImpl annotation = new AnnotationImpl(null, start, end, "TestType", features);
int expectedHash = 17;
expectedHash = 31 * expectedHash + "TestType".hashCode();
assertEquals(expectedHash, annotation.hashCode());
}

@Test
public void testIsPartiallyCompatibleReturnsFalseWhenNoOverlapDespiteSubsumedFeatures() {
Node start1 = mock(Node.class);
Node end1 = mock(Node.class);
Node start2 = mock(Node.class);
Node end2 = mock(Node.class);
when(start1.getOffset()).thenReturn(0L);
when(end1.getOffset()).thenReturn(10L);
when(start2.getOffset()).thenReturn(20L);
when(end2.getOffset()).thenReturn(30L);
FeatureMap f1 = new SimpleFeatureMapImpl();
f1.put("a", "b");
FeatureMap f2 = new SimpleFeatureMapImpl();
f2.put("a", "b");
f2.put("x", "y");
AnnotationImpl a1 = new AnnotationImpl(1, start1, end1, "A", f1);
AnnotationImpl a2 = new AnnotationImpl(2, start2, end2, "A", f2);
assertFalse(a1.isPartiallyCompatible(a2));
}

@Test
public void testAddAnnotationListenerMultipleTimesPreventsDuplicates() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationImpl annotation = new AnnotationImpl(1, start, end, "Test", features);
AnnotationListener listener = mock(AnnotationListener.class);
annotation.addAnnotationListener(listener);
annotation.addAnnotationListener(listener);
annotation.fireAnnotationUpdated(new AnnotationEvent(annotation, AnnotationEvent.FEATURES_UPDATED));
verify(listener, times(1)).annotationUpdated(any(AnnotationEvent.class));
}

@Test
public void testWithinSpanOfWithExactSameStartAndEndOffsets() {
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(5L);
when(end.getOffset()).thenReturn(15L);
AnnotationImpl a1 = new AnnotationImpl(1, start, end, "X", new SimpleFeatureMapImpl());
AnnotationImpl a2 = new AnnotationImpl(2, start, end, "X", new SimpleFeatureMapImpl());
assertTrue(a1.withinSpanOf(a2));
}

@Test
public void testWithinSpanOfReturnsFalseWhenStartOffsetGreaterThanOuterStart() {
Node innerStart = mock(Node.class);
Node innerEnd = mock(Node.class);
Node outerStart = mock(Node.class);
Node outerEnd = mock(Node.class);
when(innerStart.getOffset()).thenReturn(15L);
when(innerEnd.getOffset()).thenReturn(20L);
when(outerStart.getOffset()).thenReturn(16L);
when(outerEnd.getOffset()).thenReturn(25L);
AnnotationImpl inner = new AnnotationImpl(1, innerStart, innerEnd, "In", new SimpleFeatureMapImpl());
AnnotationImpl outer = new AnnotationImpl(2, outerStart, outerEnd, "Out", new SimpleFeatureMapImpl());
assertFalse(inner.withinSpanOf(outer));
}

@Test
public void testIsCompatibleWithNullAnnotationReturnsFalse() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap features = new SimpleFeatureMapImpl();
features.put("a", "1");
AnnotationImpl a = new AnnotationImpl(1, start, end, "Type", features);
assertFalse(a.isCompatible(null, null));
}

@Test
public void testIsPartiallyCompatibleWithNullAnnotationReturnsFalseEvenWithSubset() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap features = new SimpleFeatureMapImpl();
features.put("x", "y");
Set<Object> subset = new HashSet<Object>();
subset.add("x");
AnnotationImpl a = new AnnotationImpl(1, start, end, "X", features);
assertFalse(a.isPartiallyCompatible(null, subset));
}

@Test
public void testSetFeaturesSkipsListenerUpdateIfListenersIsEmpty() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap f1 = mock(FeatureMap.class);
FeatureMap f2 = mock(FeatureMap.class);
AnnotationImpl annotation = new AnnotationImpl(1, start, end, "X", f1);
annotation.setFeatures(f2);
verify(f1, never()).removeFeatureMapListener(any());
verify(f2, never()).addFeatureMapListener(any());
}
}
