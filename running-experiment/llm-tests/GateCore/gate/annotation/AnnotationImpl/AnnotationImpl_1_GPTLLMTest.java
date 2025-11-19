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

public class AnnotationImpl_1_GPTLLMTest {

@Test
public void testGetId() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap features = new SimpleFeatureMapImpl();
Annotation annotation = new AnnotationImpl(123, start, end, "Token", features);
assertEquals(Integer.valueOf(123), annotation.getId());
}

@Test
public void testGetType() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap features = new SimpleFeatureMapImpl();
Annotation annotation = new AnnotationImpl(123, start, end, "Person", features);
assertEquals("Person", annotation.getType());
}

@Test
public void testGetStartNode() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap features = new SimpleFeatureMapImpl();
Annotation annotation = new AnnotationImpl(123, start, end, "Person", features);
assertSame(start, annotation.getStartNode());
}

@Test
public void testGetEndNode() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap features = new SimpleFeatureMapImpl();
Annotation annotation = new AnnotationImpl(123, start, end, "Person", features);
assertSame(end, annotation.getEndNode());
}

@Test
public void testToStringContainsFields() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap features = new SimpleFeatureMapImpl();
Annotation annotation = new AnnotationImpl(1, start, end, "Entity", features);
String result = annotation.toString();
assertTrue(result.contains("id=1"));
assertTrue(result.contains("type=Entity"));
assertTrue(result.contains("features="));
assertTrue(result.contains("start="));
assertTrue(result.contains("end="));
}

@Test
public void testCompareToEqual() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap features = new SimpleFeatureMapImpl();
Annotation a1 = new AnnotationImpl(10, start, end, "Tag", features);
Annotation a2 = new AnnotationImpl(10, start, end, "Tag", features);
assertEquals(0, a1.compareTo(a2));
}

@Test
public void testCompareToSmaller() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap features = new SimpleFeatureMapImpl();
Annotation a1 = new AnnotationImpl(5, start, end, "Tag", features);
Annotation a2 = new AnnotationImpl(10, start, end, "Tag", features);
assertTrue(a1.compareTo(a2) < 0);
}

@Test
public void testCompareToGreater() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap features = new SimpleFeatureMapImpl();
Annotation a1 = new AnnotationImpl(20, start, end, "Tag", features);
Annotation a2 = new AnnotationImpl(10, start, end, "Tag", features);
assertTrue(a1.compareTo(a2) > 0);
}

@Test
public void testEqualsSameFields() {
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(5L);
when(end.getOffset()).thenReturn(10L);
FeatureMap features = new SimpleFeatureMapImpl();
features.put("key", "value");
Annotation a1 = new AnnotationImpl(1, start, end, "Entity", features);
Annotation a2 = new AnnotationImpl(1, start, end, "Entity", features);
assertEquals(a1, a2);
}

@Test
public void testEqualsDifferentId() {
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(5L);
when(end.getOffset()).thenReturn(10L);
FeatureMap features = new SimpleFeatureMapImpl();
features.put("key", "value");
Annotation a1 = new AnnotationImpl(1, start, end, "Entity", features);
Annotation a2 = new AnnotationImpl(2, start, end, "Entity", features);
assertNotEquals(a1, a2);
}

@Test
public void testHashCodeConsistency() {
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(100L);
when(end.getOffset()).thenReturn(200L);
FeatureMap features = new SimpleFeatureMapImpl();
features.put("x", "y");
Annotation a1 = new AnnotationImpl(42, start, end, "Type", features);
Annotation a2 = new AnnotationImpl(42, start, end, "Type", features);
assertEquals(a1.hashCode(), a2.hashCode());
}

@Test
public void testIsCompatibleTrue() {
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(0L);
when(end.getOffset()).thenReturn(10L);
FeatureMap subset = new SimpleFeatureMapImpl();
subset.put("a", 1);
FeatureMap superset = new SimpleFeatureMapImpl();
superset.put("a", 1);
superset.put("b", 2);
Annotation a1 = new AnnotationImpl(1, start, end, "T", subset);
Annotation a2 = new AnnotationImpl(2, start, end, "T", superset);
assertTrue(a1.isCompatible(a2));
}

@Test
public void testWithinSpanOfTrue() {
Node outerStart = mock(Node.class);
Node outerEnd = mock(Node.class);
when(outerStart.getOffset()).thenReturn(5L);
when(outerEnd.getOffset()).thenReturn(20L);
Node innerStart = mock(Node.class);
Node innerEnd = mock(Node.class);
when(innerStart.getOffset()).thenReturn(10L);
when(innerEnd.getOffset()).thenReturn(15L);
FeatureMap fm = new SimpleFeatureMapImpl();
Annotation inner = new AnnotationImpl(1, innerStart, innerEnd, "X", fm);
Annotation outer = new AnnotationImpl(2, outerStart, outerEnd, "X", fm);
assertTrue(inner.withinSpanOf(outer));
}

@Test
public void testOverlapsTrue() {
Node startA = mock(Node.class);
Node endA = mock(Node.class);
Node startB = mock(Node.class);
Node endB = mock(Node.class);
when(startA.getOffset()).thenReturn(5L);
when(endA.getOffset()).thenReturn(15L);
when(startB.getOffset()).thenReturn(10L);
when(endB.getOffset()).thenReturn(20L);
FeatureMap fm = new SimpleFeatureMapImpl();
Annotation a = new AnnotationImpl(1, startA, endA, "A", fm);
Annotation b = new AnnotationImpl(2, startB, endB, "B", fm);
assertTrue(a.overlaps(b));
}

@Test
public void testSetFeaturesDispatchesEvent() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap initialFeatures = new SimpleFeatureMapImpl();
FeatureMap updatedFeatures = new SimpleFeatureMapImpl();
updatedFeatures.put("new", "value");
AnnotationImpl annotation = new AnnotationImpl(1, start, end, "Test", initialFeatures);
AnnotationListener listener = mock(AnnotationListener.class);
annotation.addAnnotationListener(listener);
annotation.setFeatures(updatedFeatures);
verify(listener, atLeastOnce()).annotationUpdated(any(AnnotationEvent.class));
}

@Test
public void testRemoveAnnotationListenerDoesNotReceiveEvent() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap original = new SimpleFeatureMapImpl();
FeatureMap replacement = new SimpleFeatureMapImpl();
replacement.put("changed", true);
AnnotationImpl annotation = new AnnotationImpl(5, start, end, "Entity", original);
AnnotationListener listener = mock(AnnotationListener.class);
annotation.addAnnotationListener(listener);
annotation.removeAnnotationListener(listener);
annotation.setFeatures(replacement);
verify(listener, never()).annotationUpdated(any(AnnotationEvent.class));
}

@Test
public void testEqualsNull() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap features = new SimpleFeatureMapImpl();
Annotation annotation = new AnnotationImpl(1, start, end, "Type", features);
assertFalse(annotation.equals(null));
}

@Test
public void testEqualsDifferentObjectType() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationImpl annotation = new AnnotationImpl(1, start, end, "Type", features);
String notAnAnnotation = "Not an annotation";
assertFalse(annotation.equals(notAnAnnotation));
}

@Test
public void testEqualsNullOffsetStartNode() {
Node start1 = mock(Node.class);
Node end1 = mock(Node.class);
when(start1.getOffset()).thenReturn(null);
when(end1.getOffset()).thenReturn(20L);
Node start2 = mock(Node.class);
Node end2 = mock(Node.class);
when(start2.getOffset()).thenReturn(null);
when(end2.getOffset()).thenReturn(20L);
FeatureMap features = new SimpleFeatureMapImpl();
Annotation a1 = new AnnotationImpl(1, start1, end1, "Type", features);
Annotation a2 = new AnnotationImpl(1, start2, end2, "Type", features);
assertTrue(a1.equals(a2));
}

@Test
public void testEqualsDifferentFeatureMaps() {
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(0L);
when(end.getOffset()).thenReturn(10L);
FeatureMap fm1 = new SimpleFeatureMapImpl();
fm1.put("a", 1);
FeatureMap fm2 = new SimpleFeatureMapImpl();
fm2.put("b", 2);
Annotation a1 = new AnnotationImpl(1, start, end, "Type", fm1);
Annotation a2 = new AnnotationImpl(1, start, end, "Type", fm2);
assertFalse(a1.equals(a2));
}

@Test
public void testIsCompatibleNullAnnotation() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap fm = new SimpleFeatureMapImpl();
AnnotationImpl annotation = new AnnotationImpl(1, start, end, "X", fm);
assertFalse(annotation.isCompatible(null));
}

@Test
public void testIsCompatibleWithSubsetFeatureSet_NotSubsumed() {
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(0L);
when(end.getOffset()).thenReturn(10L);
FeatureMap a1Map = new SimpleFeatureMapImpl();
a1Map.put("x", "1");
a1Map.put("y", "common");
FeatureMap a2Map = new SimpleFeatureMapImpl();
a2Map.put("y", "common");
AnnotationImpl a1 = new AnnotationImpl(1, start, end, "X", a1Map);
AnnotationImpl a2 = new AnnotationImpl(2, start, end, "X", a2Map);
Set<Object> keys = new HashSet<>();
keys.add("x");
assertFalse(a1.isCompatible(a2, keys));
}

@Test
public void testIsPartiallyCompatibleNullAnnotation() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap fm = new SimpleFeatureMapImpl();
AnnotationImpl annotation = new AnnotationImpl(5, start, end, "T", fm);
assertFalse(annotation.isPartiallyCompatible(null));
}

@Test
public void testIsPartiallyCompatibleFeatureKeySubsetNoMatch() {
Node aStart = mock(Node.class);
Node aEnd = mock(Node.class);
when(aStart.getOffset()).thenReturn(0L);
when(aEnd.getOffset()).thenReturn(20L);
Node bStart = mock(Node.class);
Node bEnd = mock(Node.class);
when(bStart.getOffset()).thenReturn(10L);
when(bEnd.getOffset()).thenReturn(30L);
FeatureMap sub = new SimpleFeatureMapImpl();
sub.put("a", 1);
FeatureMap sup = new SimpleFeatureMapImpl();
sup.put("b", 2);
AnnotationImpl a = new AnnotationImpl(1, aStart, aEnd, "T", sub);
AnnotationImpl b = new AnnotationImpl(2, bStart, bEnd, "T", sup);
Set<Object> keySet = new HashSet<>();
keySet.add("a");
assertFalse(a.isPartiallyCompatible(b, keySet));
}

@Test
public void testCoextensiveWithNullStartInOther() {
Node thisStart = mock(Node.class);
Node thisEnd = mock(Node.class);
when(thisStart.getOffset()).thenReturn(1L);
when(thisEnd.getOffset()).thenReturn(10L);
Node otherEnd = mock(Node.class);
when(otherEnd.getOffset()).thenReturn(10L);
Annotation other = mock(Annotation.class);
when(other.getStartNode()).thenReturn(null);
when(other.getEndNode()).thenReturn(otherEnd);
AnnotationImpl annotation = new AnnotationImpl(10, thisStart, thisEnd, "X", new SimpleFeatureMapImpl());
assertFalse(annotation.coextensive(other));
}

@Test
public void testOverlapsTouchingBoundsNoOverlap() {
Node aStart = mock(Node.class);
Node aEnd = mock(Node.class);
when(aStart.getOffset()).thenReturn(0L);
when(aEnd.getOffset()).thenReturn(10L);
Node bStart = mock(Node.class);
Node bEnd = mock(Node.class);
when(bStart.getOffset()).thenReturn(10L);
when(bEnd.getOffset()).thenReturn(20L);
AnnotationImpl a = new AnnotationImpl(1, aStart, aEnd, "T", new SimpleFeatureMapImpl());
AnnotationImpl b = new AnnotationImpl(2, bStart, bEnd, "T", new SimpleFeatureMapImpl());
assertFalse(a.overlaps(b));
}

@Test
public void testOverlapsWithNullOffsetsInOtherFails() {
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(0L);
when(end.getOffset()).thenReturn(10L);
Node nullStart = mock(Node.class);
Node nullEnd = mock(Node.class);
when(nullStart.getOffset()).thenReturn(null);
when(nullEnd.getOffset()).thenReturn(null);
Annotation a = new AnnotationImpl(1, start, end, "T", new SimpleFeatureMapImpl());
Annotation b = new AnnotationImpl(2, nullStart, nullEnd, "T", new SimpleFeatureMapImpl());
assertFalse(a.overlaps(b));
}

@Test
public void testWithinSpanOfWithEqualBoundaries() {
Node outerStart = mock(Node.class);
Node outerEnd = mock(Node.class);
when(outerStart.getOffset()).thenReturn(100L);
when(outerEnd.getOffset()).thenReturn(200L);
Node innerStart = mock(Node.class);
Node innerEnd = mock(Node.class);
when(innerStart.getOffset()).thenReturn(100L);
when(innerEnd.getOffset()).thenReturn(200L);
FeatureMap fm = new SimpleFeatureMapImpl();
Annotation inside = new AnnotationImpl(1, innerStart, innerEnd, "X", fm);
Annotation outer = new AnnotationImpl(2, outerStart, outerEnd, "X", fm);
assertTrue(inside.withinSpanOf(outer));
}

@Test
public void testWithinSpanOfFailsWhenOutside() {
Node aStart = mock(Node.class);
Node aEnd = mock(Node.class);
Node bStart = mock(Node.class);
Node bEnd = mock(Node.class);
when(aStart.getOffset()).thenReturn(50L);
when(aEnd.getOffset()).thenReturn(60L);
when(bStart.getOffset()).thenReturn(100L);
when(bEnd.getOffset()).thenReturn(200L);
Annotation small = new AnnotationImpl(1, aStart, aEnd, "T", new SimpleFeatureMapImpl());
Annotation large = new AnnotationImpl(2, bStart, bEnd, "T", new SimpleFeatureMapImpl());
assertFalse(small.withinSpanOf(large));
}

@Test
public void testIsCompatibleWithNullFeatureMapOnOtherAnnotation() {
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(0L);
when(end.getOffset()).thenReturn(10L);
FeatureMap selfFeatures = new SimpleFeatureMapImpl();
selfFeatures.put("key", "value");
AnnotationImpl self = new AnnotationImpl(5, start, end, "X", selfFeatures);
Annotation other = mock(Annotation.class);
when(other.getStartNode()).thenReturn(start);
when(other.getEndNode()).thenReturn(end);
when(other.getFeatures()).thenReturn(null);
assertTrue(self.isCompatible(other));
}

@Test
public void testIsCompatibleFeatureMapSubsumesEmpty() {
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(10L);
when(end.getOffset()).thenReturn(20L);
FeatureMap fmEmpty = new SimpleFeatureMapImpl();
FeatureMap fmSuperset = new SimpleFeatureMapImpl();
fmSuperset.put("a", "b");
AnnotationImpl a = new AnnotationImpl(1, start, end, "T", fmEmpty);
AnnotationImpl b = new AnnotationImpl(2, start, end, "T", fmSuperset);
assertTrue(a.isCompatible(b));
}

@Test
public void testIsCompatibleFailsWhenFeatureMapDoesNotSubsume() {
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(5L);
when(end.getOffset()).thenReturn(10L);
FeatureMap fm1 = new SimpleFeatureMapImpl();
fm1.put("x", "1");
FeatureMap fm2 = new SimpleFeatureMapImpl();
fm2.put("x", "2");
AnnotationImpl a1 = new AnnotationImpl(1, start, end, "P", fm1);
AnnotationImpl a2 = new AnnotationImpl(2, start, end, "P", fm2);
assertFalse(a1.isCompatible(a2));
}

@Test
public void testIsCompatibleFeatureSubsetIgnoresNonIncludedKeys() {
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(100L);
when(end.getOffset()).thenReturn(200L);
FeatureMap selfFeatures = new SimpleFeatureMapImpl();
selfFeatures.put("k1", "v1");
selfFeatures.put("ignored", "fail");
FeatureMap otherFeatures = new SimpleFeatureMapImpl();
otherFeatures.put("k1", "v1");
AnnotationImpl self = new AnnotationImpl(1, start, end, "A", selfFeatures);
AnnotationImpl other = new AnnotationImpl(2, start, end, "A", otherFeatures);
Set<Object> keys = new HashSet<Object>();
keys.add("k1");
assertTrue(self.isCompatible(other, keys));
}

@Test
public void testIsPartiallyCompatibleWithNullSubsetReturnsFallbackResult() {
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(1L);
when(end.getOffset()).thenReturn(5L);
Node overlapStart = mock(Node.class);
Node overlapEnd = mock(Node.class);
when(overlapStart.getOffset()).thenReturn(4L);
when(overlapEnd.getOffset()).thenReturn(10L);
FeatureMap features = new SimpleFeatureMapImpl();
features.put("topic", "news");
AnnotationImpl base = new AnnotationImpl(1, start, end, "T", features);
AnnotationImpl overlapping = new AnnotationImpl(2, overlapStart, overlapEnd, "T", features);
assertTrue(base.isPartiallyCompatible(overlapping, null));
}

@Test
public void testIsPartiallyCompatibleFailsWhenOverlappingButNotSubsumed() {
Node start1 = mock(Node.class);
Node end1 = mock(Node.class);
when(start1.getOffset()).thenReturn(0L);
when(end1.getOffset()).thenReturn(10L);
Node start2 = mock(Node.class);
Node end2 = mock(Node.class);
when(start2.getOffset()).thenReturn(8L);
when(end2.getOffset()).thenReturn(15L);
FeatureMap fm1 = new SimpleFeatureMapImpl();
fm1.put("f1", 1);
FeatureMap fm2 = new SimpleFeatureMapImpl();
fm2.put("f1", 2);
AnnotationImpl a = new AnnotationImpl(1, start1, end1, "T", fm1);
AnnotationImpl b = new AnnotationImpl(2, start2, end2, "T", fm2);
assertFalse(a.isPartiallyCompatible(b));
}

@Test
public void testIsCompatibleWithEmptyFeatureKeySetShouldReturnTrue() {
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(10L);
when(end.getOffset()).thenReturn(100L);
FeatureMap selfFM = new SimpleFeatureMapImpl();
selfFM.put("irrelevant", 1);
FeatureMap otherFM = new SimpleFeatureMapImpl();
otherFM.put("something", 2);
AnnotationImpl a = new AnnotationImpl(1, start, end, "ZZ", selfFM);
AnnotationImpl b = new AnnotationImpl(2, start, end, "ZZ", otherFM);
Set<Object> keySet = new HashSet<Object>();
assertTrue(a.isCompatible(b, keySet));
}

@Test
public void testSetFeaturesDoesNotThrowWhenNoListeners() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap initial = new SimpleFeatureMapImpl();
initial.put("x", 1);
FeatureMap updated = new SimpleFeatureMapImpl();
updated.put("y", 2);
AnnotationImpl annotation = new AnnotationImpl(1, start, end, "A", initial);
annotation.setFeatures(updated);
assertEquals(updated, annotation.getFeatures());
}

@Test
public void testRemoveAnnotationListenerThatWasNeverAdded() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap fm = new SimpleFeatureMapImpl();
AnnotationImpl annotation = new AnnotationImpl(1, start, end, "X", fm);
gate.event.AnnotationListener neverAdded = mock(gate.event.AnnotationListener.class);
annotation.removeAnnotationListener(neverAdded);
FeatureMap newFm = new SimpleFeatureMapImpl();
annotation.setFeatures(newFm);
verify(neverAdded, never()).annotationUpdated(any(gate.event.AnnotationEvent.class));
}

@Test
public void testAddAnnotationListenerMultipleTimesOnlyAddedOnce() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap fm = new SimpleFeatureMapImpl();
AnnotationImpl annotation = new AnnotationImpl(1, start, end, "X", fm);
gate.event.AnnotationListener listener = mock(gate.event.AnnotationListener.class);
annotation.addAnnotationListener(listener);
annotation.addAnnotationListener(listener);
FeatureMap newFm = new SimpleFeatureMapImpl();
newFm.put("flag", true);
annotation.setFeatures(newFm);
verify(listener, atLeastOnce()).annotationUpdated(any(gate.event.AnnotationEvent.class));
}

@Test
public void testEqualsWithDifferentStartOffset() {
Node start1 = mock(Node.class);
Node end1 = mock(Node.class);
Node start2 = mock(Node.class);
Node end2 = mock(Node.class);
when(start1.getOffset()).thenReturn(1L);
when(end1.getOffset()).thenReturn(10L);
when(start2.getOffset()).thenReturn(2L);
when(end2.getOffset()).thenReturn(10L);
FeatureMap fm = new SimpleFeatureMapImpl();
AnnotationImpl a1 = new AnnotationImpl(1, start1, end1, "Type", fm);
AnnotationImpl a2 = new AnnotationImpl(1, start2, end2, "Type", fm);
assertFalse(a1.equals(a2));
}

@Test
public void testCoextensiveWithNullEndNodeInOther() {
Node thisStart = mock(Node.class);
Node thisEnd = mock(Node.class);
when(thisStart.getOffset()).thenReturn(10L);
when(thisEnd.getOffset()).thenReturn(20L);
Node otherStart = mock(Node.class);
when(otherStart.getOffset()).thenReturn(10L);
AnnotationImpl thisAnnot = new AnnotationImpl(1, thisStart, thisEnd, "T", new SimpleFeatureMapImpl());
Annotation other = mock(Annotation.class);
when(other.getStartNode()).thenReturn(otherStart);
when(other.getEndNode()).thenReturn(null);
assertFalse(thisAnnot.coextensive(other));
}

@Test
public void testCoextensiveFalseDueToOffsetMismatch() {
Node s1 = mock(Node.class);
Node e1 = mock(Node.class);
when(s1.getOffset()).thenReturn(1L);
when(e1.getOffset()).thenReturn(10L);
Node s2 = mock(Node.class);
Node e2 = mock(Node.class);
when(s2.getOffset()).thenReturn(1L);
when(e2.getOffset()).thenReturn(15L);
FeatureMap fm = new SimpleFeatureMapImpl();
AnnotationImpl a1 = new AnnotationImpl(1, s1, e1, "T", fm);
AnnotationImpl a2 = new AnnotationImpl(2, s2, e2, "T", fm);
assertFalse(a1.coextensive(a2));
}

@Test
public void testOverlapsWithNullStartNodeInTarget() {
Node thisStart = mock(Node.class);
Node thisEnd = mock(Node.class);
when(thisStart.getOffset()).thenReturn(1L);
when(thisEnd.getOffset()).thenReturn(10L);
Node otherEnd = mock(Node.class);
when(otherEnd.getOffset()).thenReturn(5L);
AnnotationImpl thisAnnot = new AnnotationImpl(1, thisStart, thisEnd, "X", new SimpleFeatureMapImpl());
Annotation other = mock(Annotation.class);
when(other.getStartNode()).thenReturn(null);
when(other.getEndNode()).thenReturn(otherEnd);
assertFalse(thisAnnot.overlaps(other));
}

@Test
public void testWithinSpanOfWithNullStartOffset() {
Node thisStart = mock(Node.class);
Node thisEnd = mock(Node.class);
when(thisStart.getOffset()).thenReturn(5L);
when(thisEnd.getOffset()).thenReturn(8L);
Node outerStart = mock(Node.class);
Node outerEnd = mock(Node.class);
when(outerStart.getOffset()).thenReturn(null);
when(outerEnd.getOffset()).thenReturn(10L);
AnnotationImpl self = new AnnotationImpl(1, thisStart, thisEnd, "A", new SimpleFeatureMapImpl());
Annotation outer = mock(Annotation.class);
when(outer.getStartNode()).thenReturn(outerStart);
when(outer.getEndNode()).thenReturn(outerEnd);
assertFalse(self.withinSpanOf(outer));
}

@Test
public void testIsCompatibleWithMismatchedType() {
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(5L);
when(end.getOffset()).thenReturn(10L);
FeatureMap features = new SimpleFeatureMapImpl();
features.put("key", "value");
AnnotationImpl a = new AnnotationImpl(1, start, end, "A", features);
AnnotationImpl b = new AnnotationImpl(2, start, end, "B", features);
assertTrue(a.isCompatible(b));
}

@Test
public void testIsCompatibleWithNullSubsumingFeatureSet() {
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(2L);
when(end.getOffset()).thenReturn(5L);
FeatureMap selfFM = new SimpleFeatureMapImpl();
selfFM.put("a", 1);
AnnotationImpl self = new AnnotationImpl(1, start, end, "T", selfFM);
Annotation other = mock(Annotation.class);
when(other.getStartNode()).thenReturn(start);
when(other.getEndNode()).thenReturn(end);
when(other.getFeatures()).thenReturn(null);
assertTrue(self.isCompatible(other, Collections.singleton("a")));
}

@Test
public void testOverlapsWhenOtherEndsBeforeThisStarts() {
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(10L);
when(end.getOffset()).thenReturn(20L);
Node otherStart = mock(Node.class);
Node otherEnd = mock(Node.class);
when(otherStart.getOffset()).thenReturn(0L);
when(otherEnd.getOffset()).thenReturn(9L);
Annotation thisAnnotation = new AnnotationImpl(1, start, end, "Z", new SimpleFeatureMapImpl());
Annotation other = new AnnotationImpl(2, otherStart, otherEnd, "Z", new SimpleFeatureMapImpl());
assertFalse(thisAnnotation.overlaps(other));
}

@Test
public void testOverlapsWhenOtherStartsAfterThisEnds() {
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(0L);
when(end.getOffset()).thenReturn(10L);
Node otherStart = mock(Node.class);
Node otherEnd = mock(Node.class);
when(otherStart.getOffset()).thenReturn(15L);
when(otherEnd.getOffset()).thenReturn(20L);
Annotation a = new AnnotationImpl(1, start, end, "T", new SimpleFeatureMapImpl());
Annotation b = new AnnotationImpl(2, otherStart, otherEnd, "T", new SimpleFeatureMapImpl());
assertFalse(a.overlaps(b));
}

@Test
public void testAddAnnotationListenerThenFireUpdates() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap fm = new SimpleFeatureMapImpl();
AnnotationImpl annotation = new AnnotationImpl(1, start, end, "X", fm);
AnnotationListener listener = mock(AnnotationListener.class);
annotation.addAnnotationListener(listener);
fm.put("addedFeature", "yes");
verify(listener, atLeastOnce()).annotationUpdated(any());
}

@Test
public void testAddRemoveAddSameListener() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap fm = new SimpleFeatureMapImpl();
AnnotationImpl annotation = new AnnotationImpl(5, start, end, "ReAddType", fm);
AnnotationListener listener = mock(AnnotationListener.class);
annotation.addAnnotationListener(listener);
annotation.removeAnnotationListener(listener);
annotation.addAnnotationListener(listener);
FeatureMap newFM = new SimpleFeatureMapImpl();
newFM.put("k", "v");
annotation.setFeatures(newFM);
verify(listener, atLeastOnce()).annotationUpdated(any());
}

@Test
public void testSetFeaturesWithNullMapStillTriggersEvent() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap orig = new SimpleFeatureMapImpl();
orig.put("foo", "bar");
AnnotationImpl annotation = new AnnotationImpl(1, start, end, "T", orig);
AnnotationListener listener = mock(AnnotationListener.class);
annotation.addAnnotationListener(listener);
FeatureMap nullMap = new SimpleFeatureMapImpl();
annotation.setFeatures(nullMap);
assertSame(nullMap, annotation.getFeatures());
verify(listener, atLeastOnce()).annotationUpdated(any());
}

@Test
public void testWithinSpanOfWithEqualSpanBounds() {
Node startA = mock(Node.class);
Node endA = mock(Node.class);
when(startA.getOffset()).thenReturn(0L);
when(endA.getOffset()).thenReturn(20L);
FeatureMap fm = new SimpleFeatureMapImpl();
AnnotationImpl a = new AnnotationImpl(1, startA, endA, "T", fm);
AnnotationImpl b = new AnnotationImpl(2, startA, endA, "T", fm);
assertTrue(a.withinSpanOf(b));
}

@Test
public void testEqualsWhenFeatureMapNullMismatch() {
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(0L);
when(end.getOffset()).thenReturn(10L);
FeatureMap map = new SimpleFeatureMapImpl();
map.put("key", "value");
AnnotationImpl a = new AnnotationImpl(1, start, end, "X", map);
AnnotationImpl b = new AnnotationImpl(1, start, end, "X", null);
assertFalse(a.equals(b));
}

@Test
public void testEqualityWithAllNullProperties() {
AnnotationImpl a = new AnnotationImpl(null, null, null, null, null);
AnnotationImpl b = new AnnotationImpl(null, null, null, null, null);
assertTrue(a.equals(b));
assertEquals(a.hashCode(), b.hashCode());
}

@Test
public void testEqualsWithNullEndNode() {
Node start = mock(Node.class);
when(start.getOffset()).thenReturn(0L);
Node end = null;
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationImpl a1 = new AnnotationImpl(1, start, end, "T", features);
AnnotationImpl a2 = new AnnotationImpl(1, start, end, "T", features);
assertTrue(a1.equals(a2));
}

@Test
public void testEqualsWithOneEndNodeNull() {
Node start = mock(Node.class);
Node end1 = mock(Node.class);
when(start.getOffset()).thenReturn(1L);
when(end1.getOffset()).thenReturn(5L);
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationImpl a1 = new AnnotationImpl(1, start, end1, "T", features);
AnnotationImpl a2 = new AnnotationImpl(1, start, null, "T", features);
assertFalse(a1.equals(a2));
}

@Test
public void testCoextensiveWithBothOffsetsNull() {
Node thisStart = mock(Node.class);
Node thisEnd = mock(Node.class);
when(thisStart.getOffset()).thenReturn(null);
when(thisEnd.getOffset()).thenReturn(null);
Node otherStart = mock(Node.class);
Node otherEnd = mock(Node.class);
when(otherStart.getOffset()).thenReturn(null);
when(otherEnd.getOffset()).thenReturn(null);
AnnotationImpl a = new AnnotationImpl(1, thisStart, thisEnd, "T", new SimpleFeatureMapImpl());
AnnotationImpl b = new AnnotationImpl(1, otherStart, otherEnd, "T", new SimpleFeatureMapImpl());
assertTrue(a.coextensive(b));
}

@Test
public void testOverlapsExactSameStartAndEnd() {
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(10L);
when(end.getOffset()).thenReturn(20L);
AnnotationImpl a1 = new AnnotationImpl(1, start, end, "T", new SimpleFeatureMapImpl());
AnnotationImpl a2 = new AnnotationImpl(2, start, end, "T", new SimpleFeatureMapImpl());
assertTrue(a1.overlaps(a2));
}

@Test
public void testOverlapsIdenticalSinglePointSpan() {
Node start1 = mock(Node.class);
Node end1 = mock(Node.class);
when(start1.getOffset()).thenReturn(5L);
when(end1.getOffset()).thenReturn(5L);
Node start2 = mock(Node.class);
Node end2 = mock(Node.class);
when(start2.getOffset()).thenReturn(5L);
when(end2.getOffset()).thenReturn(5L);
AnnotationImpl a = new AnnotationImpl(1, start1, end1, "T", new SimpleFeatureMapImpl());
AnnotationImpl b = new AnnotationImpl(2, start2, end2, "T", new SimpleFeatureMapImpl());
assertFalse(a.overlaps(b));
}

@Test
public void testAddListenerFiresEventOnSetFeatures() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap initial = new SimpleFeatureMapImpl();
initial.put("x", 1);
FeatureMap updated = new SimpleFeatureMapImpl();
updated.put("y", 2);
AnnotationImpl annotation = new AnnotationImpl(1, start, end, "T", initial);
AnnotationListener listener = mock(AnnotationListener.class);
annotation.addAnnotationListener(listener);
annotation.setFeatures(updated);
verify(listener, atLeastOnce()).annotationUpdated(any(AnnotationEvent.class));
assertEquals(updated, annotation.getFeatures());
}

@Test
public void testAddTwoListenersBothReceiveEvent() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap fm = new SimpleFeatureMapImpl();
FeatureMap newFm = new SimpleFeatureMapImpl();
newFm.put("changed", true);
AnnotationImpl annotation = new AnnotationImpl(1, start, end, "T", fm);
AnnotationListener listener1 = mock(AnnotationListener.class);
AnnotationListener listener2 = mock(AnnotationListener.class);
annotation.addAnnotationListener(listener1);
annotation.addAnnotationListener(listener2);
annotation.setFeatures(newFm);
verify(listener1, atLeastOnce()).annotationUpdated(any(AnnotationEvent.class));
verify(listener2, atLeastOnce()).annotationUpdated(any(AnnotationEvent.class));
}

@Test
public void testIsPartiallyCompatibleWithNullEndOffsetReturnsFalse() {
Node thisStart = mock(Node.class);
Node thisEnd = mock(Node.class);
when(thisStart.getOffset()).thenReturn(10L);
when(thisEnd.getOffset()).thenReturn(20L);
Node otherStart = mock(Node.class);
Node otherEnd = mock(Node.class);
when(otherStart.getOffset()).thenReturn(15L);
when(otherEnd.getOffset()).thenReturn(null);
FeatureMap fm = new SimpleFeatureMapImpl();
fm.put("x", "1");
AnnotationImpl a = new AnnotationImpl(1, thisStart, thisEnd, "T", fm);
AnnotationImpl b = new AnnotationImpl(2, otherStart, otherEnd, "T", fm);
assertFalse(a.isPartiallyCompatible(b));
}

@Test
public void testIsPartiallyCompatibleWithEmptyFeatureKeySet() {
Node startA = mock(Node.class);
Node endA = mock(Node.class);
when(startA.getOffset()).thenReturn(5L);
when(endA.getOffset()).thenReturn(20L);
Node startB = mock(Node.class);
Node endB = mock(Node.class);
when(startB.getOffset()).thenReturn(10L);
when(endB.getOffset()).thenReturn(30L);
FeatureMap fmA = new SimpleFeatureMapImpl();
fmA.put("feature1", "value1");
FeatureMap fmB = new SimpleFeatureMapImpl();
fmB.put("feature2", "value2");
AnnotationImpl annotA = new AnnotationImpl(1, startA, endA, "Type", fmA);
AnnotationImpl annotB = new AnnotationImpl(2, startB, endB, "Type", fmB);
Set<Object> emptyKeys = new HashSet<Object>();
assertTrue(annotA.isPartiallyCompatible(annotB, emptyKeys));
}

@Test
public void testIsCompatibleWithNullSelfFeatureMapAndNonNullOther() {
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(1L);
when(end.getOffset()).thenReturn(2L);
AnnotationImpl a = new AnnotationImpl(1, start, end, "T", null);
FeatureMap otherFM = new SimpleFeatureMapImpl();
otherFM.put("k", "v");
AnnotationImpl b = new AnnotationImpl(2, start, end, "T", otherFM);
assertTrue(a.isCompatible(b));
}

@Test
public void testEqualsDifferentTypeNullVsNonNull() {
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(0L);
when(end.getOffset()).thenReturn(10L);
FeatureMap fm = new SimpleFeatureMapImpl();
AnnotationImpl a1 = new AnnotationImpl(1, start, end, null, fm);
AnnotationImpl a2 = new AnnotationImpl(1, start, end, "Person", fm);
assertFalse(a1.equals(a2));
}

@Test
public void testFeatureMapListenerAttachedOnlyOnce() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap fm = spy(new SimpleFeatureMapImpl());
AnnotationImpl annotation = new AnnotationImpl(1, start, end, "Entity", fm);
AnnotationListener listener = mock(AnnotationListener.class);
annotation.addAnnotationListener(listener);
annotation.addAnnotationListener(listener);
FeatureMap newFm = new SimpleFeatureMapImpl();
newFm.put("test", "value");
annotation.setFeatures(newFm);
assertSame(newFm, annotation.getFeatures());
verify(listener, atLeastOnce()).annotationUpdated(any(AnnotationEvent.class));
}

@Test
public void testSetFeaturesSwapsFeatureMapListeners() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap oldFm = spy(new SimpleFeatureMapImpl());
oldFm.put("a", "b");
FeatureMap newFm = spy(new SimpleFeatureMapImpl());
newFm.put("x", "y");
AnnotationImpl annotation = new AnnotationImpl(100, start, end, "X", oldFm);
AnnotationListener listener = mock(AnnotationListener.class);
annotation.addAnnotationListener(listener);
annotation.setFeatures(newFm);
verify(oldFm).removeFeatureMapListener(any());
verify(newFm).addFeatureMapListener(any());
newFm.put("newFeature", "v");
verify(listener, atLeastOnce()).annotationUpdated(any());
}

@Test
public void testIsCompatibleSelfFeatureMapNullOtherNonNullSubsetSuccess() {
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(0L);
when(end.getOffset()).thenReturn(5L);
AnnotationImpl a = new AnnotationImpl(1, start, end, "T", null);
FeatureMap otherFm = new SimpleFeatureMapImpl();
otherFm.put("x", 1);
AnnotationImpl b = new AnnotationImpl(2, start, end, "T", otherFm);
assertTrue(a.isCompatible(b));
}

@Test
public void testIsCompatibleSubsetKeyMismatchFails() {
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(10L);
when(end.getOffset()).thenReturn(20L);
FeatureMap self = new SimpleFeatureMapImpl();
self.put("a", 1);
self.put("b", 2);
FeatureMap other = new SimpleFeatureMapImpl();
other.put("a", 1);
other.put("b", 99);
AnnotationImpl a = new AnnotationImpl(1, start, end, "T", self);
AnnotationImpl b = new AnnotationImpl(2, start, end, "T", other);
Set<Object> keySubset = new HashSet<Object>();
keySubset.add("b");
assertFalse(a.isCompatible(b, keySubset));
}

@Test
public void testIsPartiallyCompatibleOverlappingSpanNullSubsetInclusion() {
Node start1 = mock(Node.class);
Node end1 = mock(Node.class);
when(start1.getOffset()).thenReturn(10L);
when(end1.getOffset()).thenReturn(20L);
Node start2 = mock(Node.class);
Node end2 = mock(Node.class);
when(start2.getOffset()).thenReturn(15L);
when(end2.getOffset()).thenReturn(25L);
FeatureMap fm1 = new SimpleFeatureMapImpl();
fm1.put("case", "1");
FeatureMap fm2 = new SimpleFeatureMapImpl();
fm2.put("case", "1");
fm2.put("extra", "x");
AnnotationImpl a = new AnnotationImpl(1, start1, end1, "T", fm1);
AnnotationImpl b = new AnnotationImpl(2, start2, end2, "T", fm2);
Set<Object> keys = null;
assertTrue(a.isPartiallyCompatible(b, keys));
}

@Test
public void testListenerNotifiedOnFeatureMapDirectChange() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap fm = new SimpleFeatureMapImpl();
AnnotationImpl annotation = new AnnotationImpl(99, start, end, "Type", fm);
AnnotationListener listener = mock(AnnotationListener.class);
annotation.addAnnotationListener(listener);
fm.put("added", "value");
verify(listener, atLeastOnce()).annotationUpdated(any(AnnotationEvent.class));
}

@Test
public void testEqualsWhenOffsetsAreNullInBothAnnotations() {
Node startA = mock(Node.class);
Node endA = mock(Node.class);
Node startB = mock(Node.class);
Node endB = mock(Node.class);
when(startA.getOffset()).thenReturn(null);
when(endA.getOffset()).thenReturn(null);
when(startB.getOffset()).thenReturn(null);
when(endB.getOffset()).thenReturn(null);
FeatureMap fm = new SimpleFeatureMapImpl();
fm.put("k", 1);
AnnotationImpl a = new AnnotationImpl(1, startA, endA, "T", fm);
AnnotationImpl b = new AnnotationImpl(1, startB, endB, "T", fm);
assertTrue(a.equals(b));
}

@Test
public void testCompareToWithNullThrowsExpectedException() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap fm = new SimpleFeatureMapImpl();
AnnotationImpl a = new AnnotationImpl(1, start, end, "X", fm);
try {
a.compareTo(null);
fail("Expected ClassCastException");
} catch (ClassCastException e) {
}
}

@Test
public void testCompareToWithNonAnnotationTypeThrowsException() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap fm = new SimpleFeatureMapImpl();
AnnotationImpl a = new AnnotationImpl(1, start, end, "X", fm);
Object obj = new Object();
try {
a.compareTo(obj);
fail("Expected ClassCastException");
} catch (ClassCastException e) {
}
}

@Test
public void testFireAnnotationUpdatedWithNoListenersDoesNothing() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap fm = new SimpleFeatureMapImpl();
AnnotationImpl annotation = new AnnotationImpl(1, start, end, "X", fm);
AnnotationEvent event = new AnnotationEvent(annotation, AnnotationEvent.FEATURES_UPDATED);
annotation.fireAnnotationUpdated(event);
}

@Test
public void testIsCompatibleWithNullSubsetReturnsBaseMethod() {
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(0L);
when(end.getOffset()).thenReturn(5L);
FeatureMap fmSelf = new SimpleFeatureMapImpl();
fmSelf.put("k", "v");
FeatureMap fmOther = new SimpleFeatureMapImpl();
fmOther.put("k", "v");
fmOther.put("extra", "field");
AnnotationImpl self = new AnnotationImpl(1, start, end, "Tag", fmSelf);
AnnotationImpl other = new AnnotationImpl(2, start, end, "Tag", fmOther);
assertTrue(self.isCompatible(other, null));
}

@Test
public void testPartialCompatibilityWithMismatchedKeySubsetFails() {
Node startA = mock(Node.class);
Node endA = mock(Node.class);
when(startA.getOffset()).thenReturn(0L);
when(endA.getOffset()).thenReturn(10L);
Node startB = mock(Node.class);
Node endB = mock(Node.class);
when(startB.getOffset()).thenReturn(5L);
when(endB.getOffset()).thenReturn(15L);
FeatureMap fm1 = new SimpleFeatureMapImpl();
fm1.put("common", "yes");
FeatureMap fm2 = new SimpleFeatureMapImpl();
fm2.put("common", "no");
AnnotationImpl a = new AnnotationImpl(1, startA, endA, "Type", fm1);
AnnotationImpl b = new AnnotationImpl(2, startB, endB, "Type", fm2);
Set<Object> keys = new HashSet<Object>();
keys.add("common");
assertFalse(a.isPartiallyCompatible(b, keys));
}

@Test
public void testCoextensiveStartNodeOneNull() {
Node startA = mock(Node.class);
Node endA = mock(Node.class);
when(startA.getOffset()).thenReturn(1L);
when(endA.getOffset()).thenReturn(10L);
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationImpl a = new AnnotationImpl(1, startA, endA, "Type", features);
Annotation b = mock(Annotation.class);
when(b.getStartNode()).thenReturn(null);
when(b.getEndNode()).thenReturn(endA);
assertFalse(a.coextensive(b));
}

@Test
public void testCoextensiveBothStartNodesNull() {
Node endA = mock(Node.class);
Node endB = mock(Node.class);
when(endA.getOffset()).thenReturn(50L);
when(endB.getOffset()).thenReturn(50L);
AnnotationImpl a = new AnnotationImpl(1, null, endA, "X", new SimpleFeatureMapImpl());
Annotation b = mock(Annotation.class);
when(b.getStartNode()).thenReturn(null);
when(b.getEndNode()).thenReturn(endB);
assertTrue(a.coextensive(b));
}

@Test
public void testCoextensiveStartOffsetMismatch() {
Node startA = mock(Node.class);
Node end = mock(Node.class);
when(startA.getOffset()).thenReturn(0L);
Node startB = mock(Node.class);
when(startB.getOffset()).thenReturn(10L);
AnnotationImpl a = new AnnotationImpl(1, startA, end, "Type", new SimpleFeatureMapImpl());
Annotation b = mock(Annotation.class);
when(b.getStartNode()).thenReturn(startB);
when(b.getEndNode()).thenReturn(end);
assertFalse(a.coextensive(b));
}

@Test
public void testOverlapsZeroLengthTargetFalse() {
Node startA = mock(Node.class);
Node endA = mock(Node.class);
when(startA.getOffset()).thenReturn(10L);
when(endA.getOffset()).thenReturn(20L);
Node startB = mock(Node.class);
Node endB = mock(Node.class);
when(startB.getOffset()).thenReturn(15L);
when(endB.getOffset()).thenReturn(15L);
AnnotationImpl a = new AnnotationImpl(1, startA, endA, "X", new SimpleFeatureMapImpl());
AnnotationImpl b = new AnnotationImpl(2, startB, endB, "X", new SimpleFeatureMapImpl());
assertFalse(a.overlaps(b));
}

@Test
public void testOverlapsSinglePointMatchFalse() {
Node startA = mock(Node.class);
Node endA = mock(Node.class);
when(startA.getOffset()).thenReturn(0L);
when(endA.getOffset()).thenReturn(5L);
Node startB = mock(Node.class);
Node endB = mock(Node.class);
when(startB.getOffset()).thenReturn(5L);
when(endB.getOffset()).thenReturn(5L);
AnnotationImpl a = new AnnotationImpl(1, startA, endA, "Entity", new SimpleFeatureMapImpl());
AnnotationImpl b = new AnnotationImpl(2, startB, endB, "Entity", new SimpleFeatureMapImpl());
assertFalse(a.overlaps(b));
}

@Test
public void testIsCompatibleSubsetWithNullSelfFeaturesReturnsTrue() {
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(10L);
when(end.getOffset()).thenReturn(20L);
AnnotationImpl a = new AnnotationImpl(1, start, end, "Type", null);
FeatureMap otherFm = new SimpleFeatureMapImpl();
otherFm.put("k", "v");
AnnotationImpl b = new AnnotationImpl(2, start, end, "Type", otherFm);
Set<Object> subset = new HashSet<Object>();
subset.add("k");
assertTrue(a.isCompatible(b, subset));
}

@Test
public void testIsPartiallyCompatibleKeySubsetFailsDueToMissingKey() {
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(0L);
when(end.getOffset()).thenReturn(10L);
Node start2 = mock(Node.class);
Node end2 = mock(Node.class);
when(start2.getOffset()).thenReturn(5L);
when(end2.getOffset()).thenReturn(15L);
FeatureMap base = new SimpleFeatureMapImpl();
base.put("x", 1);
FeatureMap target = new SimpleFeatureMapImpl();
target.put("y", 1);
AnnotationImpl a = new AnnotationImpl(1, start, end, "A", base);
AnnotationImpl b = new AnnotationImpl(2, start2, end2, "A", target);
Set<Object> featureKeys = new HashSet<Object>();
featureKeys.add("x");
assertFalse(a.isPartiallyCompatible(b, featureKeys));
}

@Test
public void testWithinSpanOfWithNullStartNodeReturnsFalse() {
Node selfStart = mock(Node.class);
Node selfEnd = mock(Node.class);
when(selfStart.getOffset()).thenReturn(50L);
when(selfEnd.getOffset()).thenReturn(60L);
AnnotationImpl self = new AnnotationImpl(5, selfStart, selfEnd, "Tag", new SimpleFeatureMapImpl());
Node otherEnd = mock(Node.class);
when(otherEnd.getOffset()).thenReturn(100L);
Annotation outer = mock(Annotation.class);
when(outer.getStartNode()).thenReturn(null);
when(outer.getEndNode()).thenReturn(otherEnd);
assertFalse(self.withinSpanOf(outer));
}

@Test
public void testWithinSpanOfWithNullEndOffsetReturnsFalse() {
Node selfStart = mock(Node.class);
Node selfEnd = mock(Node.class);
when(selfStart.getOffset()).thenReturn(0L);
when(selfEnd.getOffset()).thenReturn(15L);
Node otherStart = mock(Node.class);
Node otherEnd = mock(Node.class);
when(otherStart.getOffset()).thenReturn(0L);
when(otherEnd.getOffset()).thenReturn(null);
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationImpl self = new AnnotationImpl(1, selfStart, selfEnd, "T", features);
Annotation outer = mock(Annotation.class);
when(outer.getStartNode()).thenReturn(otherStart);
when(outer.getEndNode()).thenReturn(otherEnd);
assertFalse(self.withinSpanOf(outer));
}

@Test
public void testRemoveListenerWhenNotAddedDoesNotFail() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap fm = new SimpleFeatureMapImpl();
AnnotationImpl annotation = new AnnotationImpl(1, start, end, "X", fm);
gate.event.AnnotationListener listener = mock(gate.event.AnnotationListener.class);
annotation.removeAnnotationListener(listener);
}

@Test
public void testAddListenerStartsTrackingFeatureMapMutations() {
Node start = mock(Node.class);
Node end = mock(Node.class);
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationImpl annotation = new AnnotationImpl(1, start, end, "TestType", features);
gate.event.AnnotationListener listener = mock(gate.event.AnnotationListener.class);
annotation.addAnnotationListener(listener);
features.put("added", "value");
verify(listener, atLeastOnce()).annotationUpdated(any());
}
}
