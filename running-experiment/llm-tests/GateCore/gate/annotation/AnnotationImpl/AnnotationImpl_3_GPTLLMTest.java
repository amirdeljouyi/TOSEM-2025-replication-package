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

public class AnnotationImpl_3_GPTLLMTest {

@Test
public void testGetIdReturnsCorrectValue() {
FeatureMap featureMap = mock(FeatureMap.class);
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(0L);
when(end.getOffset()).thenReturn(10L);
AnnotationImpl annotation = new AnnotationImpl(123, start, end, "TestType", featureMap);
Integer id = annotation.getId();
assertEquals(Integer.valueOf(123), id);
}

@Test
public void testGetTypeReturnsCorrectValue() {
FeatureMap featureMap = mock(FeatureMap.class);
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(2L);
when(end.getOffset()).thenReturn(7L);
AnnotationImpl annotation = new AnnotationImpl(10, start, end, "Sentence", featureMap);
String type = annotation.getType();
assertEquals("Sentence", type);
}

@Test
public void testCompareToReturnsNegativeForSmallerId() {
FeatureMap featureMap = mock(FeatureMap.class);
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(1L);
when(end.getOffset()).thenReturn(4L);
AnnotationImpl annotation1 = new AnnotationImpl(1, start, end, "Token", featureMap);
Annotation other = mock(Annotation.class);
when(other.getId()).thenReturn(2);
int result = annotation1.compareTo(other);
assertTrue(result < 0);
}

@Test
public void testToStringIncludesIdAndType() {
FeatureMap featureMap = mock(FeatureMap.class);
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(1L);
when(end.getOffset()).thenReturn(4L);
AnnotationImpl annotation = new AnnotationImpl(5, start, end, "Entity", featureMap);
String str = annotation.toString();
assertTrue(str.contains("id=5"));
assertTrue(str.contains("type=Entity"));
}

@Test
public void testEqualsReturnsTrueForIdenticalAnnotation() {
FeatureMap featureMap = mock(FeatureMap.class);
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(0L);
when(end.getOffset()).thenReturn(1L);
AnnotationImpl annotation1 = new AnnotationImpl(1, start, end, "Token", featureMap);
AnnotationImpl annotation2 = new AnnotationImpl(1, start, end, "Token", featureMap);
boolean result = annotation1.equals(annotation2);
assertTrue(result);
}

@Test
public void testEqualsReturnsFalseForDifferentType() {
FeatureMap featureMap = mock(FeatureMap.class);
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(3L);
when(end.getOffset()).thenReturn(9L);
AnnotationImpl annotation = new AnnotationImpl(1, start, end, "Token", featureMap);
boolean result = annotation.equals("not an annotation");
assertFalse(result);
}

@Test
public void testHashCodeConsistentWithEquals() {
FeatureMap featureMap = mock(FeatureMap.class);
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(0L);
when(end.getOffset()).thenReturn(10L);
AnnotationImpl annotation1 = new AnnotationImpl(1, start, end, "Token", featureMap);
AnnotationImpl annotation2 = new AnnotationImpl(1, start, end, "Token", featureMap);
int hash1 = annotation1.hashCode();
int hash2 = annotation2.hashCode();
assertEquals(hash1, hash2);
}

@Test
public void testIsCompatibleReturnsTrueWhenFeatureMapSubsumed() {
FeatureMap innerMap = mock(FeatureMap.class);
FeatureMap outerMap = mock(FeatureMap.class);
when(innerMap.subsumes(any(FeatureMap.class))).thenReturn(true);
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(10L);
when(end.getOffset()).thenReturn(20L);
AnnotationImpl a1 = new AnnotationImpl(1, start, end, "Type", outerMap);
AnnotationImpl a2 = new AnnotationImpl(2, start, end, "Type", innerMap);
boolean result = a2.isCompatible(a1);
assertTrue(result);
}

@Test
public void testIsPartiallyCompatibleReturnsTrueOnOverlapWithSubsumedFeatures() {
FeatureMap innerMap = mock(FeatureMap.class);
FeatureMap outerMap = mock(FeatureMap.class);
when(innerMap.subsumes(any(FeatureMap.class))).thenReturn(true);
Node start1 = mock(Node.class);
Node end1 = mock(Node.class);
Node start2 = mock(Node.class);
Node end2 = mock(Node.class);
when(start1.getOffset()).thenReturn(0L);
when(end1.getOffset()).thenReturn(20L);
when(start2.getOffset()).thenReturn(10L);
when(end2.getOffset()).thenReturn(30L);
AnnotationImpl a1 = new AnnotationImpl(1, start1, end1, "Type", innerMap);
AnnotationImpl a2 = new AnnotationImpl(2, start2, end2, "Type", outerMap);
boolean result = a1.isPartiallyCompatible(a2);
assertTrue(result);
}

@Test
public void testCoextensiveReturnsTrueWhenOffsetsMatch() {
FeatureMap f1 = mock(FeatureMap.class);
Node s1 = mock(Node.class);
Node e1 = mock(Node.class);
when(s1.getOffset()).thenReturn(10L);
when(e1.getOffset()).thenReturn(20L);
Node s2 = mock(Node.class);
Node e2 = mock(Node.class);
when(s2.getOffset()).thenReturn(10L);
when(e2.getOffset()).thenReturn(20L);
AnnotationImpl a1 = new AnnotationImpl(1, s1, e1, "T", f1);
AnnotationImpl a2 = new AnnotationImpl(2, s2, e2, "T", f1);
assertTrue(a1.coextensive(a2));
}

@Test
public void testOverlapsReturnsTrueWhenRangesOverlap() {
FeatureMap map = mock(FeatureMap.class);
Node s1 = mock(Node.class);
Node e1 = mock(Node.class);
Node s2 = mock(Node.class);
Node e2 = mock(Node.class);
when(s1.getOffset()).thenReturn(10L);
when(e1.getOffset()).thenReturn(20L);
when(s2.getOffset()).thenReturn(15L);
when(e2.getOffset()).thenReturn(25L);
AnnotationImpl a1 = new AnnotationImpl(1, s1, e1, "X", map);
AnnotationImpl a2 = new AnnotationImpl(2, s2, e2, "X", map);
assertTrue(a1.overlaps(a2));
}

@Test
public void testWithinSpanOfReturnsTrueWhenContained() {
FeatureMap map = mock(FeatureMap.class);
Node innerStart = mock(Node.class);
Node innerEnd = mock(Node.class);
Node outerStart = mock(Node.class);
Node outerEnd = mock(Node.class);
when(innerStart.getOffset()).thenReturn(10L);
when(innerEnd.getOffset()).thenReturn(15L);
when(outerStart.getOffset()).thenReturn(0L);
when(outerEnd.getOffset()).thenReturn(20L);
AnnotationImpl inner = new AnnotationImpl(1, innerStart, innerEnd, "Inner", map);
AnnotationImpl outer = new AnnotationImpl(2, outerStart, outerEnd, "Outer", map);
assertTrue(inner.withinSpanOf(outer));
}

@Test
public void testSetFeaturesFiresUpdateEventWhenListenerPresent() {
FeatureMap featureMap1 = mock(FeatureMap.class);
FeatureMap featureMap2 = mock(FeatureMap.class);
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(1L);
when(end.getOffset()).thenReturn(5L);
AnnotationImpl annotation = new AnnotationImpl(5, start, end, "Test", featureMap1);
AnnotationListener listener = mock(AnnotationListener.class);
annotation.addAnnotationListener(listener);
annotation.setFeatures(featureMap2);
}

@Test
public void testAddAndRemoveAnnotationListenerDoesNotFail() {
FeatureMap map = mock(FeatureMap.class);
Node s = mock(Node.class);
Node e = mock(Node.class);
when(s.getOffset()).thenReturn(1L);
when(e.getOffset()).thenReturn(2L);
AnnotationImpl a = new AnnotationImpl(1, s, e, "X", map);
AnnotationListener l = mock(AnnotationListener.class);
a.addAnnotationListener(l);
a.removeAnnotationListener(l);
}

@Test
public void testEventsHandlerFiresFeatureUpdateEvent() {
FeatureMap map = mock(FeatureMap.class);
Node s = mock(Node.class);
Node e = mock(Node.class);
when(s.getOffset()).thenReturn(0L);
when(e.getOffset()).thenReturn(10L);
AnnotationImpl a = new AnnotationImpl(42, s, e, "Y", map);
AnnotationListener listener = mock(AnnotationListener.class);
a.addAnnotationListener(listener);
AnnotationImpl.EventsHandler handler = a.new EventsHandler();
handler.featureMapUpdated();
verify(listener, times(1)).annotationUpdated(any(AnnotationEvent.class));
}

@Test
public void testEqualsReturnsFalseWhenTypeIsNullOnOneSide() {
FeatureMap map = mock(FeatureMap.class);
Node s = mock(Node.class);
Node e = mock(Node.class);
when(s.getOffset()).thenReturn(1L);
when(e.getOffset()).thenReturn(2L);
AnnotationImpl a1 = new AnnotationImpl(1, s, e, null, map);
AnnotationImpl a2 = new AnnotationImpl(1, s, e, "Type", map);
boolean result = a1.equals(a2);
assertFalse(result);
}

@Test
public void testEqualsReturnsFalseWhenIdIsNullOnOneSide() {
FeatureMap map = mock(FeatureMap.class);
Node s = mock(Node.class);
Node e = mock(Node.class);
when(s.getOffset()).thenReturn(1L);
when(e.getOffset()).thenReturn(2L);
AnnotationImpl a1 = new AnnotationImpl(null, s, e, "Type", map);
AnnotationImpl a2 = new AnnotationImpl(5, s, e, "Type", map);
boolean result = a1.equals(a2);
assertFalse(result);
}

@Test
public void testEqualsReturnsFalseWhenStartOffsetIsNullOnOneSide() {
FeatureMap map = mock(FeatureMap.class);
Node s1 = mock(Node.class);
Node s2 = mock(Node.class);
Node e = mock(Node.class);
when(s1.getOffset()).thenReturn(null);
when(s2.getOffset()).thenReturn(5L);
when(e.getOffset()).thenReturn(10L);
AnnotationImpl a1 = new AnnotationImpl(1, s1, e, "Type", map);
AnnotationImpl a2 = new AnnotationImpl(1, s2, e, "Type", map);
boolean result = a1.equals(a2);
assertFalse(result);
}

@Test
public void testEqualsReturnsFalseWhenEndOffsetIsNullOnOneSide() {
FeatureMap map = mock(FeatureMap.class);
Node s = mock(Node.class);
Node e1 = mock(Node.class);
Node e2 = mock(Node.class);
when(s.getOffset()).thenReturn(0L);
when(e1.getOffset()).thenReturn(null);
when(e2.getOffset()).thenReturn(10L);
AnnotationImpl a1 = new AnnotationImpl(1, s, e1, "Type", map);
AnnotationImpl a2 = new AnnotationImpl(1, s, e2, "Type", map);
boolean result = a1.equals(a2);
assertFalse(result);
}

@Test
public void testEqualsReturnsFalseWhenFeaturesAreNotEqual() {
FeatureMap map1 = mock(FeatureMap.class);
FeatureMap map2 = mock(FeatureMap.class);
when(map1.equals(map2)).thenReturn(false);
Node s = mock(Node.class);
Node e = mock(Node.class);
when(s.getOffset()).thenReturn(1L);
when(e.getOffset()).thenReturn(10L);
AnnotationImpl a1 = new AnnotationImpl(1, s, e, "Type", map1);
AnnotationImpl a2 = new AnnotationImpl(1, s, e, "Type", map2);
boolean result = a1.equals(a2);
assertFalse(result);
}

@Test
public void testOverlapsReturnsFalseWhenOtherAnnotationIsNull() {
FeatureMap map = mock(FeatureMap.class);
Node s = mock(Node.class);
Node e = mock(Node.class);
when(s.getOffset()).thenReturn(0L);
when(e.getOffset()).thenReturn(1L);
AnnotationImpl annotation = new AnnotationImpl(10, s, e, "T", map);
boolean result = annotation.overlaps(null);
assertFalse(result);
}

@Test
public void testOverlapsReturnsFalseWhenAnyOffsetOfOtherIsNull() {
FeatureMap map = mock(FeatureMap.class);
Node s1 = mock(Node.class);
Node e1 = mock(Node.class);
Node s2 = mock(Node.class);
Node e2 = mock(Node.class);
when(s1.getOffset()).thenReturn(0L);
when(e1.getOffset()).thenReturn(10L);
when(s2.getOffset()).thenReturn(null);
when(e2.getOffset()).thenReturn(5L);
AnnotationImpl a1 = new AnnotationImpl(1, s1, e1, "X", map);
AnnotationImpl a2 = new AnnotationImpl(2, s2, e2, "X", map);
boolean result = a1.overlaps(a2);
assertFalse(result);
}

@Test
public void testIsCompatibleWithNullFeatureSetReturnsFalse() {
FeatureMap map = mock(FeatureMap.class);
Node s = mock(Node.class);
Node e = mock(Node.class);
when(s.getOffset()).thenReturn(0L);
when(e.getOffset()).thenReturn(10L);
AnnotationImpl a1 = new AnnotationImpl(1, s, e, "Type", map);
Annotation other = mock(Annotation.class);
when(other.getStartNode()).thenReturn(s);
when(other.getEndNode()).thenReturn(e);
when(other.getType()).thenReturn("Type");
when(other.getId()).thenReturn(1);
when(other.getFeatures()).thenReturn(null);
boolean result = a1.isCompatible(other);
assertTrue(result);
}

@Test
public void testIsCompatibleWithFeatureSubsetAndFeatureKeySetNull() {
FeatureMap features1 = mock(FeatureMap.class);
FeatureMap features2 = mock(FeatureMap.class);
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(0L);
when(end.getOffset()).thenReturn(10L);
when(features1.subsumes(features2)).thenReturn(false);
AnnotationImpl a1 = new AnnotationImpl(1, start, end, "X", features2);
AnnotationImpl a2 = new AnnotationImpl(2, start, end, "X", features1);
boolean result = a1.isCompatible(a2, null);
assertFalse(result);
}

@Test
public void testIsPartiallyCompatibleWithNullFeatureMapReturnsTrue() {
FeatureMap map = mock(FeatureMap.class);
Node s1 = mock(Node.class);
Node e1 = mock(Node.class);
Node s2 = mock(Node.class);
Node e2 = mock(Node.class);
when(s1.getOffset()).thenReturn(0L);
when(e1.getOffset()).thenReturn(10L);
when(s2.getOffset()).thenReturn(5L);
when(e2.getOffset()).thenReturn(15L);
AnnotationImpl a1 = new AnnotationImpl(1, s1, e1, "T", map);
Annotation other = mock(Annotation.class);
when(other.getStartNode()).thenReturn(s2);
when(other.getEndNode()).thenReturn(e2);
when(other.getFeatures()).thenReturn(null);
boolean result = a1.isPartiallyCompatible(other);
assertTrue(result);
}

@Test
public void testIsPartiallyCompatibleWithKeySetAndSubsumesFalse() {
FeatureMap map1 = mock(FeatureMap.class);
FeatureMap map2 = mock(FeatureMap.class);
Node s1 = mock(Node.class);
Node e1 = mock(Node.class);
Node s2 = mock(Node.class);
Node e2 = mock(Node.class);
when(s1.getOffset()).thenReturn(2L);
when(e1.getOffset()).thenReturn(8L);
when(s2.getOffset()).thenReturn(1L);
when(e2.getOffset()).thenReturn(9L);
when(map1.subsumes(map2, Set.of("key1"))).thenReturn(false);
AnnotationImpl a1 = new AnnotationImpl(1, s1, e1, "Type", map2);
AnnotationImpl a2 = new AnnotationImpl(2, s2, e2, "Type", map1);
Set<Object> keySet = new HashSet<>();
keySet.add("key1");
boolean result = a1.isPartiallyCompatible(a2, keySet);
assertFalse(result);
}

@Test
public void testWithinSpanOfReturnsFalseWhenOffsetsAreOutside() {
FeatureMap map = mock(FeatureMap.class);
Node innerStart = mock(Node.class);
Node innerEnd = mock(Node.class);
Node outerStart = mock(Node.class);
Node outerEnd = mock(Node.class);
when(innerStart.getOffset()).thenReturn(20L);
when(innerEnd.getOffset()).thenReturn(25L);
when(outerStart.getOffset()).thenReturn(0L);
when(outerEnd.getOffset()).thenReturn(10L);
AnnotationImpl inner = new AnnotationImpl(1, innerStart, innerEnd, "Token", map);
AnnotationImpl outer = new AnnotationImpl(2, outerStart, outerEnd, "Sentence", map);
boolean result = inner.withinSpanOf(outer);
assertFalse(result);
}

@Test
public void testWithinSpanOfReturnsFalseWhenStartNodeIsNull() {
FeatureMap map = mock(FeatureMap.class);
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(5L);
when(end.getOffset()).thenReturn(10L);
Node nullNode = null;
AnnotationImpl inner = new AnnotationImpl(1, start, end, "X", map);
Annotation outer = mock(Annotation.class);
when(outer.getStartNode()).thenReturn(nullNode);
when(outer.getEndNode()).thenReturn(end);
boolean result = inner.withinSpanOf(outer);
assertFalse(result);
}

@Test
public void testIsCompatibleReturnsFalseWhenOffsetsAreDifferent() {
FeatureMap aFeatures = mock(FeatureMap.class);
FeatureMap bFeatures = mock(FeatureMap.class);
when(bFeatures.subsumes(any())).thenReturn(true);
Node aStart = mock(Node.class);
Node aEnd = mock(Node.class);
when(aStart.getOffset()).thenReturn(5L);
when(aEnd.getOffset()).thenReturn(10L);
Node bStart = mock(Node.class);
Node bEnd = mock(Node.class);
when(bStart.getOffset()).thenReturn(1L);
when(bEnd.getOffset()).thenReturn(10L);
AnnotationImpl a = new AnnotationImpl(1, aStart, aEnd, "X", aFeatures);
AnnotationImpl b = new AnnotationImpl(2, bStart, bEnd, "X", bFeatures);
boolean result = a.isCompatible(b);
assertFalse(result);
}

@Test
public void testIsCompatibleWithFeatureNameSetAndEqualOffsetsButSubsumesFalse() {
FeatureMap aFeatures = mock(FeatureMap.class);
FeatureMap bFeatures = mock(FeatureMap.class);
when(bFeatures.subsumes(eq(aFeatures), any(Set.class))).thenReturn(false);
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(5L);
when(end.getOffset()).thenReturn(15L);
Set<Object> keySubset = new HashSet<>();
keySubset.add("keyA");
AnnotationImpl a = new AnnotationImpl(1, start, end, "X", aFeatures);
AnnotationImpl b = new AnnotationImpl(2, start, end, "X", bFeatures);
boolean result = a.isCompatible(b, keySubset);
assertFalse(result);
}

@Test
public void testIsPartiallyCompatibleReturnsFalseWhenNoOverlap() {
FeatureMap f1 = mock(FeatureMap.class);
FeatureMap f2 = mock(FeatureMap.class);
when(f2.subsumes(any())).thenReturn(true);
Node start1 = mock(Node.class);
Node end1 = mock(Node.class);
when(start1.getOffset()).thenReturn(0L);
when(end1.getOffset()).thenReturn(5L);
Node start2 = mock(Node.class);
Node end2 = mock(Node.class);
when(start2.getOffset()).thenReturn(10L);
when(end2.getOffset()).thenReturn(15L);
AnnotationImpl a = new AnnotationImpl(1, start1, end1, "Tag", f1);
AnnotationImpl b = new AnnotationImpl(2, start2, end2, "Tag", f2);
boolean result = a.isPartiallyCompatible(b);
assertFalse(result);
}

@Test
public void testCoextensiveWhenBothStartAndEndNodesAreNull() {
FeatureMap map = mock(FeatureMap.class);
AnnotationImpl a = new AnnotationImpl(1, null, null, "X", map);
AnnotationImpl b = new AnnotationImpl(2, null, null, "X", map);
boolean result = a.coextensive(b);
assertTrue(result);
}

@Test
public void testOverlapsReturnsFalseWhenThisStartOffsetGreaterThanOtherEnd() {
FeatureMap map = mock(FeatureMap.class);
Node s1 = mock(Node.class);
Node e1 = mock(Node.class);
Node s2 = mock(Node.class);
Node e2 = mock(Node.class);
when(s1.getOffset()).thenReturn(20L);
when(e1.getOffset()).thenReturn(30L);
when(s2.getOffset()).thenReturn(5L);
when(e2.getOffset()).thenReturn(10L);
AnnotationImpl a = new AnnotationImpl(1, s1, e1, "X", map);
AnnotationImpl b = new AnnotationImpl(2, s2, e2, "X", map);
boolean result = a.overlaps(b);
assertFalse(result);
}

@Test
public void testHashCodeReturnsConsistentForDifferentFeaturesSameIDType() {
FeatureMap f1 = mock(FeatureMap.class);
FeatureMap f2 = mock(FeatureMap.class);
Node s = mock(Node.class);
Node e = mock(Node.class);
when(s.getOffset()).thenReturn(0L);
when(e.getOffset()).thenReturn(10L);
AnnotationImpl a = new AnnotationImpl(100, s, e, "T", f1);
AnnotationImpl b = new AnnotationImpl(100, s, e, "T", f2);
int hc1 = a.hashCode();
int hc2 = b.hashCode();
assertEquals(hc1, hc2);
}

@Test
public void testAddSameAnnotationListenerTwiceOnlyAddsOnce() {
FeatureMap map = mock(FeatureMap.class);
Node s = mock(Node.class);
Node e = mock(Node.class);
when(s.getOffset()).thenReturn(5L);
when(e.getOffset()).thenReturn(10L);
AnnotationImpl annotation = new AnnotationImpl(21, s, e, "X", map);
AnnotationListener listener = mock(AnnotationListener.class);
annotation.addAnnotationListener(listener);
annotation.addAnnotationListener(listener);
annotation.setFeatures(map);
}

@Test
public void testRemoveNonexistentAnnotationListenerDoesNotFail() {
FeatureMap map = mock(FeatureMap.class);
Node s = mock(Node.class);
Node e = mock(Node.class);
when(s.getOffset()).thenReturn(3L);
when(e.getOffset()).thenReturn(7L);
AnnotationImpl annotation = new AnnotationImpl(33, s, e, "X", map);
AnnotationListener listener = mock(AnnotationListener.class);
annotation.removeAnnotationListener(listener);
}

@Test
public void testSetFeaturesRemovesAndAddsEventHandlerWhenListenersExist() {
FeatureMap original = mock(FeatureMap.class);
FeatureMap updated = mock(FeatureMap.class);
Node s = mock(Node.class);
Node e = mock(Node.class);
when(s.getOffset()).thenReturn(1L);
when(e.getOffset()).thenReturn(9L);
AnnotationImpl annotation = new AnnotationImpl(5, s, e, "X", original);
AnnotationListener listener = mock(AnnotationListener.class);
annotation.addAnnotationListener(listener);
annotation.setFeatures(updated);
verify(original).removeFeatureMapListener(any());
verify(updated).addFeatureMapListener(any());
}

@Test
public void testFireAnnotationUpdatedDoesNothingIfNoListeners() {
FeatureMap map = mock(FeatureMap.class);
Node s = mock(Node.class);
Node e = mock(Node.class);
when(s.getOffset()).thenReturn(1L);
when(e.getOffset()).thenReturn(2L);
AnnotationImpl annotation = new AnnotationImpl(1, s, e, "X", map);
AnnotationEvent evt = new AnnotationEvent(annotation, AnnotationEvent.FEATURES_UPDATED);
annotation.fireAnnotationUpdated(evt);
}

@Test
public void testEqualsReturnsFalseIfOtherIsNotAnnotationImpl() {
FeatureMap featureMap = mock(FeatureMap.class);
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(10L);
when(end.getOffset()).thenReturn(20L);
AnnotationImpl annotation = new AnnotationImpl(1, start, end, "Type", featureMap);
String differentObject = "Not an AnnotationImpl";
boolean result = annotation.equals(differentObject);
assertFalse(result);
}

@Test
public void testCompareToReturnsZeroForSameIds() {
FeatureMap featureMap = mock(FeatureMap.class);
Node start1 = mock(Node.class);
Node end1 = mock(Node.class);
when(start1.getOffset()).thenReturn(0L);
when(end1.getOffset()).thenReturn(5L);
AnnotationImpl a1 = new AnnotationImpl(42, start1, end1, "A", featureMap);
Annotation other = mock(Annotation.class);
when(other.getId()).thenReturn(42);
int result = a1.compareTo(other);
assertEquals(0, result);
}

@Test
public void testCompareToReturnsPositiveForGreaterId() {
FeatureMap featureMap = mock(FeatureMap.class);
Node s = mock(Node.class);
Node e = mock(Node.class);
when(s.getOffset()).thenReturn(1L);
when(e.getOffset()).thenReturn(3L);
AnnotationImpl a = new AnnotationImpl(5, s, e, "X", featureMap);
Annotation other = mock(Annotation.class);
when(other.getId()).thenReturn(1);
int result = a.compareTo(other);
assertTrue(result > 0);
}

@Test
public void testCoextensiveReturnsFalseWhenStartOffsetsAreNotEqual() {
FeatureMap featureMap = mock(FeatureMap.class);
Node s1 = mock(Node.class);
Node e1 = mock(Node.class);
when(s1.getOffset()).thenReturn(1L);
when(e1.getOffset()).thenReturn(10L);
Node s2 = mock(Node.class);
Node e2 = mock(Node.class);
when(s2.getOffset()).thenReturn(2L);
when(e2.getOffset()).thenReturn(10L);
AnnotationImpl a1 = new AnnotationImpl(1, s1, e1, "Tag", featureMap);
AnnotationImpl a2 = new AnnotationImpl(2, s2, e2, "Tag", featureMap);
boolean result = a1.coextensive(a2);
assertFalse(result);
}

@Test
public void testCoextensiveReturnsFalseWhenEndOffsetsAreNotEqual() {
FeatureMap featureMap = mock(FeatureMap.class);
Node s1 = mock(Node.class);
Node e1 = mock(Node.class);
when(s1.getOffset()).thenReturn(5L);
when(e1.getOffset()).thenReturn(10L);
Node s2 = mock(Node.class);
Node e2 = mock(Node.class);
when(s2.getOffset()).thenReturn(5L);
when(e2.getOffset()).thenReturn(12L);
AnnotationImpl a1 = new AnnotationImpl(1, s1, e1, "Phrase", featureMap);
AnnotationImpl a2 = new AnnotationImpl(2, s2, e2, "Phrase", featureMap);
boolean result = a1.coextensive(a2);
assertFalse(result);
}

@Test
public void testOverlapsTrueAtExactStartOfThis() {
FeatureMap map = mock(FeatureMap.class);
Node s1 = mock(Node.class);
Node e1 = mock(Node.class);
when(s1.getOffset()).thenReturn(10L);
when(e1.getOffset()).thenReturn(20L);
Node s2 = mock(Node.class);
Node e2 = mock(Node.class);
when(s2.getOffset()).thenReturn(10L);
when(e2.getOffset()).thenReturn(15L);
AnnotationImpl a1 = new AnnotationImpl(1, s1, e1, "Type", map);
AnnotationImpl a2 = new AnnotationImpl(2, s2, e2, "Type", map);
boolean result = a1.overlaps(a2);
assertTrue(result);
}

@Test
public void testOverlapsFalseWhenOtherStartsAtThisEnd() {
FeatureMap map = mock(FeatureMap.class);
Node s1 = mock(Node.class);
Node e1 = mock(Node.class);
when(s1.getOffset()).thenReturn(0L);
when(e1.getOffset()).thenReturn(10L);
Node s2 = mock(Node.class);
Node e2 = mock(Node.class);
when(s2.getOffset()).thenReturn(10L);
when(e2.getOffset()).thenReturn(15L);
AnnotationImpl a1 = new AnnotationImpl(1, s1, e1, "Tag", map);
AnnotationImpl a2 = new AnnotationImpl(2, s2, e2, "Tag", map);
boolean result = a1.overlaps(a2);
assertFalse(result);
}

@Test
public void testWithinSpanOfReturnsFalseWhenThisStartBelowOtherStart() {
FeatureMap map = mock(FeatureMap.class);
Node s1 = mock(Node.class);
Node e1 = mock(Node.class);
when(s1.getOffset()).thenReturn(1L);
when(e1.getOffset()).thenReturn(9L);
Node s2 = mock(Node.class);
Node e2 = mock(Node.class);
when(s2.getOffset()).thenReturn(2L);
when(e2.getOffset()).thenReturn(10L);
AnnotationImpl inner = new AnnotationImpl(1, s1, e1, "X", map);
AnnotationImpl outer = new AnnotationImpl(2, s2, e2, "X", map);
boolean result = inner.withinSpanOf(outer);
assertFalse(result);
}

@Test
public void testHashCodeReturnsSameForNullTypeAndId() {
FeatureMap map1 = mock(FeatureMap.class);
FeatureMap map2 = mock(FeatureMap.class);
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(0L);
when(end.getOffset()).thenReturn(1L);
AnnotationImpl a1 = new AnnotationImpl(null, start, end, null, map1);
AnnotationImpl a2 = new AnnotationImpl(null, start, end, null, map2);
int hash1 = a1.hashCode();
int hash2 = a2.hashCode();
assertEquals(hash1, hash2);
}

@Test
public void testIsCompatibleReturnsTrueWithDifferentFeatureKeysWhenSubsumed() {
FeatureMap f1 = mock(FeatureMap.class);
FeatureMap f2 = mock(FeatureMap.class);
Set<Object> keySet = new HashSet<>();
keySet.add("label");
when(f2.subsumes(eq(f1), eq(keySet))).thenReturn(true);
Node s = mock(Node.class);
Node e = mock(Node.class);
when(s.getOffset()).thenReturn(0L);
when(e.getOffset()).thenReturn(10L);
AnnotationImpl small = new AnnotationImpl(1, s, e, "NamedEntity", f1);
AnnotationImpl large = new AnnotationImpl(2, s, e, "NamedEntity", f2);
boolean result = small.isCompatible(large, keySet);
assertTrue(result);
}

@Test
public void testPartiallyCompatibleWithNullKeySetCallsSingleParamOverload() {
FeatureMap map = mock(FeatureMap.class);
Node s1 = mock(Node.class);
Node e1 = mock(Node.class);
Node s2 = mock(Node.class);
Node e2 = mock(Node.class);
when(s1.getOffset()).thenReturn(0L);
when(e1.getOffset()).thenReturn(10L);
when(s2.getOffset()).thenReturn(5L);
when(e2.getOffset()).thenReturn(15L);
when(map.subsumes(map)).thenReturn(true);
AnnotationImpl a = new AnnotationImpl(1, s1, e1, "A", map);
AnnotationImpl b = new AnnotationImpl(2, s2, e2, "A", map);
boolean result = a.isPartiallyCompatible(b, null);
assertTrue(result);
}

@Test
public void testSetFeaturesDoesNotFailIfOldFeaturesNull() {
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(10L);
when(end.getOffset()).thenReturn(20L);
FeatureMap initial = mock(FeatureMap.class);
FeatureMap incoming = mock(FeatureMap.class);
AnnotationImpl annotation = new AnnotationImpl(1, start, end, "Test", initial);
AnnotationListener listener = mock(AnnotationListener.class);
annotation.addAnnotationListener(listener);
annotation.setFeatures(incoming);
verify(incoming, atLeastOnce()).addFeatureMapListener(any());
}

@Test
public void testEqualsReturnsFalseWhenOtherStartNodeIsNull() {
FeatureMap features = mock(FeatureMap.class);
Node thisStart = mock(Node.class);
Node thisEnd = mock(Node.class);
when(thisStart.getOffset()).thenReturn(1L);
when(thisEnd.getOffset()).thenReturn(10L);
AnnotationImpl thisAnnotation = new AnnotationImpl(1, thisStart, thisEnd, "Type", features);
AnnotationImpl otherAnnotation = new AnnotationImpl(1, null, thisEnd, "Type", features);
boolean result = thisAnnotation.equals(otherAnnotation);
assertFalse(result);
}

@Test
public void testEqualsReturnsFalseWhenOtherEndNodeIsNull() {
FeatureMap features = mock(FeatureMap.class);
Node thisStart = mock(Node.class);
Node thisEnd = mock(Node.class);
when(thisStart.getOffset()).thenReturn(1L);
when(thisEnd.getOffset()).thenReturn(10L);
AnnotationImpl thisAnnotation = new AnnotationImpl(1, thisStart, thisEnd, "Type", features);
AnnotationImpl otherAnnotation = new AnnotationImpl(1, thisStart, null, "Type", features);
boolean result = thisAnnotation.equals(otherAnnotation);
assertFalse(result);
}

@Test
public void testCoextensiveReturnsFalseWhenStartNodeOffsetMismatchedDueToNull() {
FeatureMap features = mock(FeatureMap.class);
Node node1 = mock(Node.class);
Node node2 = mock(Node.class);
when(node1.getOffset()).thenReturn(null);
when(node2.getOffset()).thenReturn(0L);
AnnotationImpl a1 = new AnnotationImpl(1, node1, node2, "Type", features);
AnnotationImpl a2 = new AnnotationImpl(2, node2, node2, "Type", features);
boolean result = a1.coextensive(a2);
assertFalse(result);
}

@Test
public void testCoextensiveReturnsFalseWhenEndNodeOffsetMismatchedDueToNull() {
FeatureMap features = mock(FeatureMap.class);
Node node1 = mock(Node.class);
Node node2 = mock(Node.class);
when(node1.getOffset()).thenReturn(10L);
when(node2.getOffset()).thenReturn(null);
AnnotationImpl a1 = new AnnotationImpl(1, node1, node2, "Type", features);
AnnotationImpl a2 = new AnnotationImpl(2, node1, node1, "Type", features);
boolean result = a1.coextensive(a2);
assertFalse(result);
}

@Test
public void testOverlapsReturnsFalseWhenThisStartNodeOffsetIsNull() {
FeatureMap f = mock(FeatureMap.class);
Node thisStart = mock(Node.class);
Node thisEnd = mock(Node.class);
when(thisStart.getOffset()).thenReturn(null);
when(thisEnd.getOffset()).thenReturn(20L);
Node otherStart = mock(Node.class);
Node otherEnd = mock(Node.class);
when(otherStart.getOffset()).thenReturn(5L);
when(otherEnd.getOffset()).thenReturn(15L);
AnnotationImpl a = new AnnotationImpl(1, thisStart, thisEnd, "X", f);
AnnotationImpl b = new AnnotationImpl(2, otherStart, otherEnd, "X", f);
boolean result = a.overlaps(b);
assertFalse(result);
}

@Test
public void testOverlapsReturnsFalseWhenThisEndNodeOffsetIsNull() {
FeatureMap f = mock(FeatureMap.class);
Node thisStart = mock(Node.class);
Node thisEnd = mock(Node.class);
when(thisStart.getOffset()).thenReturn(0L);
when(thisEnd.getOffset()).thenReturn(null);
Node otherStart = mock(Node.class);
Node otherEnd = mock(Node.class);
when(otherStart.getOffset()).thenReturn(1L);
when(otherEnd.getOffset()).thenReturn(5L);
AnnotationImpl a = new AnnotationImpl(1, thisStart, thisEnd, "X", f);
AnnotationImpl b = new AnnotationImpl(2, otherStart, otherEnd, "X", f);
boolean result = a.overlaps(b);
assertFalse(result);
}

@Test
public void testIsCompatibleReturnsFalseWhenFeatureMapSubsumesFails() {
FeatureMap f1 = mock(FeatureMap.class);
FeatureMap f2 = mock(FeatureMap.class);
when(f2.subsumes(eq(f1))).thenReturn(false);
Node s = mock(Node.class);
Node e = mock(Node.class);
when(s.getOffset()).thenReturn(10L);
when(e.getOffset()).thenReturn(20L);
AnnotationImpl a = new AnnotationImpl(1, s, e, "T", f1);
AnnotationImpl b = new AnnotationImpl(2, s, e, "T", f2);
boolean result = a.isCompatible(b);
assertFalse(result);
}

@Test
public void testIsCompatibleWithFeatureKeysNullOnBothMaps() {
FeatureMap f1 = mock(FeatureMap.class);
FeatureMap f2 = mock(FeatureMap.class);
Set<Object> keys = null;
when(f2.subsumes(eq(f1))).thenReturn(true);
Node s = mock(Node.class);
Node e = mock(Node.class);
when(s.getOffset()).thenReturn(5L);
when(e.getOffset()).thenReturn(10L);
AnnotationImpl a = new AnnotationImpl(1, s, e, "Person", f1);
AnnotationImpl b = new AnnotationImpl(2, s, e, "Person", f2);
boolean result = a.isCompatible(b, keys);
assertTrue(result);
}

@Test
public void testWithinSpanOfReturnsFalseWhenEndOffsetMissing() {
Node thisStart = mock(Node.class);
Node thisEnd = mock(Node.class);
Node otherStart = mock(Node.class);
Node otherEnd = mock(Node.class);
when(thisStart.getOffset()).thenReturn(5L);
when(thisEnd.getOffset()).thenReturn(8L);
when(otherStart.getOffset()).thenReturn(0L);
when(otherEnd.getOffset()).thenReturn(null);
FeatureMap f = mock(FeatureMap.class);
AnnotationImpl thisAnnotation = new AnnotationImpl(1, thisStart, thisEnd, "X", f);
AnnotationImpl otherAnnotation = new AnnotationImpl(2, otherStart, otherEnd, "X", f);
boolean result = thisAnnotation.withinSpanOf(otherAnnotation);
assertFalse(result);
}

@Test
public void testIsPartiallyCompatibleReturnsFalseWhenFeaturesDoNotSubsumes() {
FeatureMap sourceFeatures = mock(FeatureMap.class);
FeatureMap targetFeatures = mock(FeatureMap.class);
when(targetFeatures.subsumes(eq(sourceFeatures))).thenReturn(false);
Node s1 = mock(Node.class);
Node e1 = mock(Node.class);
Node s2 = mock(Node.class);
Node e2 = mock(Node.class);
when(s1.getOffset()).thenReturn(0L);
when(e1.getOffset()).thenReturn(10L);
when(s2.getOffset()).thenReturn(5L);
when(e2.getOffset()).thenReturn(15L);
AnnotationImpl a = new AnnotationImpl(1, s1, e1, "ORG", sourceFeatures);
AnnotationImpl b = new AnnotationImpl(2, s2, e2, "ORG", targetFeatures);
boolean result = a.isPartiallyCompatible(b);
assertFalse(result);
}

@Test
public void testIsPartiallyCompatibleWithKeysReturnsFalseWhenSubsumesFails() {
FeatureMap f1 = mock(FeatureMap.class);
FeatureMap f2 = mock(FeatureMap.class);
Set<Object> keys = new HashSet<>();
keys.add("key");
when(f2.subsumes(eq(f1), eq(keys))).thenReturn(false);
Node start = mock(Node.class);
Node end = mock(Node.class);
Node start2 = mock(Node.class);
Node end2 = mock(Node.class);
when(start.getOffset()).thenReturn(10L);
when(end.getOffset()).thenReturn(20L);
when(start2.getOffset()).thenReturn(15L);
when(end2.getOffset()).thenReturn(25L);
AnnotationImpl a = new AnnotationImpl(1, start, end, "X", f1);
AnnotationImpl b = new AnnotationImpl(2, start2, end2, "X", f2);
boolean result = a.isPartiallyCompatible(b, keys);
assertFalse(result);
}

@Test
public void testEqualsReturnsFalseWhenFeatureMapNullOnEitherSide() {
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(1L);
when(end.getOffset()).thenReturn(10L);
FeatureMap features1 = null;
FeatureMap features2 = mock(FeatureMap.class);
AnnotationImpl a1 = new AnnotationImpl(42, start, end, "X", features1);
AnnotationImpl a2 = new AnnotationImpl(42, start, end, "X", features2);
boolean result = a1.equals(a2);
assertFalse(result);
}

@Test
public void testEqualsReturnsTrueWhenAllFieldsNull() {
AnnotationImpl a1 = new AnnotationImpl(null, null, null, null, null);
AnnotationImpl a2 = new AnnotationImpl(null, null, null, null, null);
boolean result = a1.equals(a2);
assertTrue(result);
}

@Test
public void testCoextensiveReturnsFalseIfOneEndNodeIsNull() {
FeatureMap fm = mock(FeatureMap.class);
Node start = mock(Node.class);
when(start.getOffset()).thenReturn(0L);
Node validEnd = mock(Node.class);
when(validEnd.getOffset()).thenReturn(5L);
AnnotationImpl a = new AnnotationImpl(1, start, validEnd, "X", fm);
AnnotationImpl b = new AnnotationImpl(2, start, null, "X", fm);
boolean result = a.coextensive(b);
assertFalse(result);
}

@Test
public void testSetFeaturesDoesNotAddListenerIfNoListenersPresent() {
FeatureMap oldFeatures = mock(FeatureMap.class);
FeatureMap newFeatures = mock(FeatureMap.class);
Node s = mock(Node.class);
Node e = mock(Node.class);
when(s.getOffset()).thenReturn(0L);
when(e.getOffset()).thenReturn(1L);
AnnotationImpl a = new AnnotationImpl(11, s, e, "T", oldFeatures);
a.setFeatures(newFeatures);
verify(oldFeatures, never()).removeFeatureMapListener(any());
verify(newFeatures, never()).addFeatureMapListener(any());
}

@Test
public void testRemoveAnnotationListenerDoesNothingWhenNotAttached() {
FeatureMap features = mock(FeatureMap.class);
Node s = mock(Node.class);
Node e = mock(Node.class);
when(s.getOffset()).thenReturn(0L);
when(e.getOffset()).thenReturn(1L);
AnnotationImpl a = new AnnotationImpl(99, s, e, "T", features);
AnnotationListener listener = mock(AnnotationListener.class);
a.removeAnnotationListener(listener);
}

@Test
public void testCompareToThrowsClassCastExceptionIfWrongType() {
FeatureMap features = mock(FeatureMap.class);
Node s = mock(Node.class);
Node e = mock(Node.class);
when(s.getOffset()).thenReturn(0L);
when(e.getOffset()).thenReturn(1L);
AnnotationImpl annotation = new AnnotationImpl(5, s, e, "X", features);
try {
annotation.compareTo("NotAnnotation");
fail("ClassCastException was expected.");
} catch (ClassCastException ex) {
assertTrue(true);
}
}

@Test
public void testIsCompatibleReturnsFalseOnMismatchingOffsets() {
FeatureMap f1 = mock(FeatureMap.class);
FeatureMap f2 = mock(FeatureMap.class);
when(f2.subsumes(f1)).thenReturn(true);
Node aStart = mock(Node.class);
Node aEnd = mock(Node.class);
when(aStart.getOffset()).thenReturn(10L);
when(aEnd.getOffset()).thenReturn(20L);
Node bStart = mock(Node.class);
Node bEnd = mock(Node.class);
when(bStart.getOffset()).thenReturn(11L);
when(bEnd.getOffset()).thenReturn(20L);
AnnotationImpl a = new AnnotationImpl(1, aStart, aEnd, "T", f1);
AnnotationImpl b = new AnnotationImpl(2, bStart, bEnd, "T", f2);
boolean result = a.isCompatible(b);
assertFalse(result);
}

@Test
public void testIsPartiallyCompatibleWithNullAnnotation() {
FeatureMap f = mock(FeatureMap.class);
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(5L);
when(end.getOffset()).thenReturn(10L);
AnnotationImpl a = new AnnotationImpl(1, start, end, "TAG", f);
boolean result = a.isPartiallyCompatible(null);
assertFalse(result);
}

@Test
public void testOverlapsReturnsFalseWithNullEndNodeInOther() {
FeatureMap map = mock(FeatureMap.class);
Node s1 = mock(Node.class);
Node e1 = mock(Node.class);
when(s1.getOffset()).thenReturn(5L);
when(e1.getOffset()).thenReturn(10L);
Node s2 = mock(Node.class);
when(s2.getOffset()).thenReturn(7L);
AnnotationImpl a = new AnnotationImpl(1, s1, e1, "x", map);
AnnotationImpl b = new AnnotationImpl(2, s2, null, "x", map);
boolean result = a.overlaps(b);
assertFalse(result);
}

@Test
public void testWithinSpanOfReturnsFalseIfStartNodeOfOtherIsNull() {
FeatureMap f = mock(FeatureMap.class);
Node s = mock(Node.class);
Node e = mock(Node.class);
when(s.getOffset()).thenReturn(5L);
when(e.getOffset()).thenReturn(10L);
AnnotationImpl a = new AnnotationImpl(1, s, e, "A", f);
AnnotationImpl b = new AnnotationImpl(2, null, e, "A", f);
boolean result = a.withinSpanOf(b);
assertFalse(result);
}

@Test
public void testIsCompatibleWithNullOffsetInOtherStartReturnsFalse() {
FeatureMap f1 = mock(FeatureMap.class);
FeatureMap f2 = mock(FeatureMap.class);
Node start1 = mock(Node.class);
Node end1 = mock(Node.class);
when(start1.getOffset()).thenReturn(0L);
when(end1.getOffset()).thenReturn(10L);
Node start2 = mock(Node.class);
Node end2 = mock(Node.class);
when(start2.getOffset()).thenReturn(null);
when(end2.getOffset()).thenReturn(10L);
AnnotationImpl a = new AnnotationImpl(1, start1, end1, "Token", f1);
AnnotationImpl b = new AnnotationImpl(2, start2, end2, "Token", f2);
boolean result = a.isCompatible(b);
assertFalse(result);
}

@Test
public void testFireAnnotationUpdatedSkipsIfNoListenerSet() {
FeatureMap f = mock(FeatureMap.class);
Node s = mock(Node.class);
Node e = mock(Node.class);
when(s.getOffset()).thenReturn(0L);
when(e.getOffset()).thenReturn(1L);
AnnotationImpl a = new AnnotationImpl(1, s, e, "T", f);
AnnotationEvent evt = new AnnotationEvent(a, AnnotationEvent.FEATURES_UPDATED);
a.fireAnnotationUpdated(evt);
}

@Test
public void testAddAnnotationListenerDoesNotAddDuplicate() {
FeatureMap f = mock(FeatureMap.class);
Node s = mock(Node.class);
Node e = mock(Node.class);
when(s.getOffset()).thenReturn(1L);
when(e.getOffset()).thenReturn(10L);
AnnotationImpl a = new AnnotationImpl(1, s, e, "T", f);
AnnotationListener listener = mock(AnnotationListener.class);
a.addAnnotationListener(listener);
a.addAnnotationListener(listener);
a.setFeatures(f);
}

@Test
public void testEqualsReturnsFalseWhenEndOffsetDiffers() {
FeatureMap features = mock(FeatureMap.class);
Node start = mock(Node.class);
Node endA = mock(Node.class);
Node endB = mock(Node.class);
when(start.getOffset()).thenReturn(0L);
when(endA.getOffset()).thenReturn(10L);
when(endB.getOffset()).thenReturn(15L);
AnnotationImpl a1 = new AnnotationImpl(1, start, endA, "Entity", features);
AnnotationImpl a2 = new AnnotationImpl(1, start, endB, "Entity", features);
boolean result = a1.equals(a2);
assertFalse(result);
}

@Test
public void testIsCompatibleReturnsFalseWhenOtherFeaturesNotSuperset() {
FeatureMap thisFeatures = mock(FeatureMap.class);
FeatureMap otherFeatures = mock(FeatureMap.class);
when(otherFeatures.subsumes(thisFeatures)).thenReturn(false);
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(2L);
when(end.getOffset()).thenReturn(8L);
AnnotationImpl a1 = new AnnotationImpl(1, start, end, "Tag", thisFeatures);
AnnotationImpl a2 = new AnnotationImpl(2, start, end, "Tag", otherFeatures);
boolean result = a1.isCompatible(a2);
assertFalse(result);
}

@Test
public void testIsCompatibleWithKeysReturnsFalseWhenSubsumesFails() {
FeatureMap thisFeatures = mock(FeatureMap.class);
FeatureMap otherFeatures = mock(FeatureMap.class);
Set<Object> keySet = new HashSet<>();
keySet.add("key");
when(otherFeatures.subsumes(thisFeatures, keySet)).thenReturn(false);
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(5L);
when(end.getOffset()).thenReturn(15L);
AnnotationImpl a1 = new AnnotationImpl(1, start, end, "Token", thisFeatures);
AnnotationImpl a2 = new AnnotationImpl(2, start, end, "Token", otherFeatures);
boolean result = a1.isCompatible(a2, keySet);
assertFalse(result);
}

@Test
public void testIsCompatibleWithNullIdInOtherReturnsFalse() {
FeatureMap f = mock(FeatureMap.class);
Node s = mock(Node.class);
Node e = mock(Node.class);
when(s.getOffset()).thenReturn(10L);
when(e.getOffset()).thenReturn(20L);
AnnotationImpl a = new AnnotationImpl(42, s, e, "Document", f);
AnnotationImpl b = new AnnotationImpl(null, s, e, "Document", f);
boolean result = a.equals(b);
assertFalse(result);
}

@Test
public void testOverlapsWhenOtherExactlyEqualBoundaries() {
FeatureMap f = mock(FeatureMap.class);
Node s = mock(Node.class);
Node e = mock(Node.class);
when(s.getOffset()).thenReturn(5L);
when(e.getOffset()).thenReturn(15L);
AnnotationImpl a = new AnnotationImpl(1, s, e, "A", f);
AnnotationImpl b = new AnnotationImpl(2, s, e, "A", f);
boolean result = a.overlaps(b);
assertTrue(result);
}

@Test
public void testOverlapsReturnsFalseWhenOtherOffsetsEqualToOuter() {
FeatureMap f = mock(FeatureMap.class);
Node sOuter = mock(Node.class);
Node eOuter = mock(Node.class);
Node sInner = mock(Node.class);
Node eInner = mock(Node.class);
when(sOuter.getOffset()).thenReturn(10L);
when(eOuter.getOffset()).thenReturn(20L);
when(sInner.getOffset()).thenReturn(10L);
when(eInner.getOffset()).thenReturn(10L);
AnnotationImpl outer = new AnnotationImpl(1, sOuter, eOuter, "X", f);
AnnotationImpl inner = new AnnotationImpl(2, sInner, eInner, "X", f);
boolean result = outer.overlaps(inner);
assertFalse(result);
}

@Test
public void testWithinSpanOfReturnsTrueForExactMatch() {
FeatureMap features = mock(FeatureMap.class);
Node s = mock(Node.class);
Node e = mock(Node.class);
when(s.getOffset()).thenReturn(1L);
when(e.getOffset()).thenReturn(5L);
AnnotationImpl a = new AnnotationImpl(1, s, e, "Word", features);
AnnotationImpl b = new AnnotationImpl(2, s, e, "Word", features);
boolean result = a.withinSpanOf(b);
assertTrue(result);
}

@Test
public void testWithinSpanOfReturnsFalseWhenOtherStartIsAfterThisStart() {
FeatureMap features = mock(FeatureMap.class);
Node s1 = mock(Node.class);
Node e1 = mock(Node.class);
Node s2 = mock(Node.class);
Node e2 = mock(Node.class);
when(s1.getOffset()).thenReturn(5L);
when(e1.getOffset()).thenReturn(15L);
when(s2.getOffset()).thenReturn(7L);
when(e2.getOffset()).thenReturn(20L);
AnnotationImpl a = new AnnotationImpl(1, s1, e1, "Sentence", features);
AnnotationImpl b = new AnnotationImpl(2, s2, e2, "Sentence", features);
boolean result = a.withinSpanOf(b);
assertFalse(result);
}

@Test
public void testOverlapsReturnsTrueWhenPartialOverlapAtBeginning() {
FeatureMap f = mock(FeatureMap.class);
Node aStart = mock(Node.class);
Node aEnd = mock(Node.class);
Node bStart = mock(Node.class);
Node bEnd = mock(Node.class);
when(aStart.getOffset()).thenReturn(10L);
when(aEnd.getOffset()).thenReturn(20L);
when(bStart.getOffset()).thenReturn(5L);
when(bEnd.getOffset()).thenReturn(15L);
AnnotationImpl a = new AnnotationImpl(1, aStart, aEnd, "A", f);
AnnotationImpl b = new AnnotationImpl(2, bStart, bEnd, "A", f);
boolean result = a.overlaps(b);
assertTrue(result);
}

@Test
public void testOverlapsReturnsTrueWhenPartialOverlapAtEnd() {
FeatureMap f = mock(FeatureMap.class);
Node aStart = mock(Node.class);
Node aEnd = mock(Node.class);
Node bStart = mock(Node.class);
Node bEnd = mock(Node.class);
when(aStart.getOffset()).thenReturn(5L);
when(aEnd.getOffset()).thenReturn(15L);
when(bStart.getOffset()).thenReturn(10L);
when(bEnd.getOffset()).thenReturn(20L);
AnnotationImpl a = new AnnotationImpl(1, aStart, aEnd, "B", f);
AnnotationImpl b = new AnnotationImpl(2, bStart, bEnd, "B", f);
boolean result = a.overlaps(b);
assertTrue(result);
}

@Test
public void testEqualsFalseWhenIdsAreDifferent() {
FeatureMap features = mock(FeatureMap.class);
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(1L);
when(end.getOffset()).thenReturn(10L);
AnnotationImpl a = new AnnotationImpl(1, start, end, "Label", features);
AnnotationImpl b = new AnnotationImpl(2, start, end, "Label", features);
boolean result = a.equals(b);
assertFalse(result);
}

@Test
public void testEqualsFalseWhenTypesDiffer() {
FeatureMap features = mock(FeatureMap.class);
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(0L);
when(end.getOffset()).thenReturn(5L);
AnnotationImpl a = new AnnotationImpl(100, start, end, "NE", features);
AnnotationImpl b = new AnnotationImpl(100, start, end, "ORG", features);
boolean result = a.equals(b);
assertFalse(result);
}
}
