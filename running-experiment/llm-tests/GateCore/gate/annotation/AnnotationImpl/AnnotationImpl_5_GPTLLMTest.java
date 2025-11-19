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

public class AnnotationImpl_5_GPTLLMTest {

@Test
public void testGettersReturnCorrectValues() {
Node startNode = mock(Node.class);
Node endNode = mock(Node.class);
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationImpl annotation = new AnnotationImpl(10, startNode, endNode, "Token", features);
assertEquals(Integer.valueOf(10), annotation.getId());
assertEquals("Token", annotation.getType());
assertEquals(startNode, annotation.getStartNode());
assertEquals(endNode, annotation.getEndNode());
assertEquals(features, annotation.getFeatures());
}

@Test
public void testToStringIncludesFields() {
Node startNode = mock(Node.class);
Node endNode = mock(Node.class);
when(startNode.toString()).thenReturn("StartNode");
when(endNode.toString()).thenReturn("EndNode");
FeatureMap features = new SimpleFeatureMapImpl();
features.put("pos", "NN");
AnnotationImpl annotation = new AnnotationImpl(1, startNode, endNode, "Word", features);
String result = annotation.toString();
assertTrue(result.contains("id=1"));
assertTrue(result.contains("type=Word"));
assertTrue(result.contains("features="));
assertTrue(result.contains("StartNode"));
assertTrue(result.contains("EndNode"));
}

@Test
public void testCompareToReturnsExpectedOrder() {
Node node = mock(Node.class);
FeatureMap fm = new SimpleFeatureMapImpl();
AnnotationImpl a1 = new AnnotationImpl(1, node, node, "A", fm);
AnnotationImpl a2 = new AnnotationImpl(2, node, node, "B", fm);
assertTrue(a1.compareTo(a2) < 0);
assertTrue(a2.compareTo(a1) > 0);
assertTrue(a1.compareTo(a1) == 0);
}

@Test(expected = ClassCastException.class)
public void testCompareToThrowsOnInvalidType() {
Node node = mock(Node.class);
FeatureMap fm = new SimpleFeatureMapImpl();
AnnotationImpl a1 = new AnnotationImpl(1, node, node, "A", fm);
a1.compareTo("InvalidType");
}

@Test
public void testEqualsAndHashCodeAreConsistent() {
Node startNode = mock(Node.class);
Node endNode = mock(Node.class);
when(startNode.getOffset()).thenReturn(1L);
when(endNode.getOffset()).thenReturn(5L);
FeatureMap features1 = new SimpleFeatureMapImpl();
features1.put("key", "val");
FeatureMap features2 = new SimpleFeatureMapImpl();
features2.put("key", "val");
AnnotationImpl a1 = new AnnotationImpl(100, startNode, endNode, "Entity", features1);
AnnotationImpl a2 = new AnnotationImpl(100, startNode, endNode, "Entity", features2);
assertTrue(a1.equals(a2));
assertEquals(a1.hashCode(), a2.hashCode());
}

@Test
public void testIsCompatibleReturnsTrueWhenSubsumed() {
Node n1 = mock(Node.class);
Node n2 = mock(Node.class);
when(n1.getOffset()).thenReturn(0L);
when(n2.getOffset()).thenReturn(10L);
FeatureMap mapA = new SimpleFeatureMapImpl();
mapA.put("feature1", "yes");
FeatureMap mapB = new SimpleFeatureMapImpl();
mapB.put("feature1", "yes");
mapB.put("feature2", "no");
AnnotationImpl a = new AnnotationImpl(1, n1, n2, "type", mapA);
AnnotationImpl b = new AnnotationImpl(2, n1, n2, "type", mapB);
assertTrue(a.isCompatible(b));
}

@Test
public void testIsPartiallyCompatibleTrueOnOverlapAndSubsumption() {
Node aStart = mock(Node.class);
Node aEnd = mock(Node.class);
Node bStart = mock(Node.class);
Node bEnd = mock(Node.class);
when(aStart.getOffset()).thenReturn(5L);
when(aEnd.getOffset()).thenReturn(15L);
when(bStart.getOffset()).thenReturn(10L);
when(bEnd.getOffset()).thenReturn(20L);
FeatureMap f1 = new SimpleFeatureMapImpl();
f1.put("f", "v");
FeatureMap f2 = new SimpleFeatureMapImpl();
f2.put("f", "v");
f2.put("extra", "x");
AnnotationImpl a = new AnnotationImpl(1, aStart, aEnd, "t", f1);
AnnotationImpl b = new AnnotationImpl(2, bStart, bEnd, "t", f2);
assertTrue(a.isPartiallyCompatible(b));
}

@Test
public void testIsCompatibleWithFeatureSetTrue() {
Node node = mock(Node.class);
when(node.getOffset()).thenReturn(0L);
FeatureMap innerFeatures = new SimpleFeatureMapImpl();
innerFeatures.put("x", "y");
innerFeatures.put("extra", "z");
FeatureMap baseFeatures = new SimpleFeatureMapImpl();
baseFeatures.put("x", "y");
AnnotationImpl annot1 = new AnnotationImpl(1, node, node, "T", baseFeatures);
AnnotationImpl annot2 = new AnnotationImpl(2, node, node, "T", innerFeatures);
Set<Object> keys = new HashSet<Object>();
keys.add("x");
assertTrue(annot1.isCompatible(annot2, keys));
}

@Test
public void testCoextensiveReturnsTrueIfOffsetsMatch() {
Node node1 = mock(Node.class);
Node node2 = mock(Node.class);
when(node1.getOffset()).thenReturn(2L);
when(node2.getOffset()).thenReturn(8L);
FeatureMap fm = new SimpleFeatureMapImpl();
AnnotationImpl a = new AnnotationImpl(1, node1, node2, "X", fm);
AnnotationImpl b = new AnnotationImpl(2, node1, node2, "X", fm);
assertTrue(a.coextensive(b));
}

@Test
public void testOverlapsReturnsTrue() {
Node s1 = mock(Node.class);
Node e1 = mock(Node.class);
Node s2 = mock(Node.class);
Node e2 = mock(Node.class);
when(s1.getOffset()).thenReturn(5L);
when(e1.getOffset()).thenReturn(20L);
when(s2.getOffset()).thenReturn(10L);
when(e2.getOffset()).thenReturn(30L);
Annotation a = new AnnotationImpl(1, s1, e1, "type", new SimpleFeatureMapImpl());
Annotation b = new AnnotationImpl(2, s2, e2, "type", new SimpleFeatureMapImpl());
assertTrue(a.overlaps(b));
}

@Test
public void testWithinSpanOfReturnsTrueWhenEnclosed() {
Node innerStart = mock(Node.class);
Node innerEnd = mock(Node.class);
Node outerStart = mock(Node.class);
Node outerEnd = mock(Node.class);
when(innerStart.getOffset()).thenReturn(10L);
when(innerEnd.getOffset()).thenReturn(20L);
when(outerStart.getOffset()).thenReturn(5L);
when(outerEnd.getOffset()).thenReturn(25L);
AnnotationImpl inner = new AnnotationImpl(1, innerStart, innerEnd, "i", new SimpleFeatureMapImpl());
AnnotationImpl outer = new AnnotationImpl(2, outerStart, outerEnd, "o", new SimpleFeatureMapImpl());
assertTrue(inner.withinSpanOf(outer));
}

@Test
public void testListenerIsNotifiedOnFeatureChange() {
Node node = mock(Node.class);
when(node.getOffset()).thenReturn(0L);
FeatureMap original = new SimpleFeatureMapImpl();
FeatureMap updated = new SimpleFeatureMapImpl();
AnnotationImpl ann = new AnnotationImpl(1, node, node, "T", original);
AnnotationListener listener = mock(AnnotationListener.class);
ann.addAnnotationListener(listener);
ann.setFeatures(updated);
verify(listener, times(1)).annotationUpdated(any(AnnotationEvent.class));
}

@Test
public void testRemoveListenerPreventsFurtherUpdates() {
Node node = mock(Node.class);
when(node.getOffset()).thenReturn(0L);
FeatureMap fm1 = new SimpleFeatureMapImpl();
FeatureMap fm2 = new SimpleFeatureMapImpl();
AnnotationImpl ann = new AnnotationImpl(1, node, node, "Type", fm1);
AnnotationListener listener = mock(AnnotationListener.class);
ann.addAnnotationListener(listener);
ann.setFeatures(fm2);
ann.removeAnnotationListener(listener);
ann.setFeatures(new SimpleFeatureMapImpl());
verify(listener, atMost(1)).annotationUpdated(any(AnnotationEvent.class));
}

@Test
public void testEqualsHandlesNullsGracefully() {
AnnotationImpl ann = new AnnotationImpl(null, null, null, null, null);
assertFalse(ann.equals(null));
assertTrue(ann.equals(ann));
}

@Test
public void testEqualsReturnsFalseWhenDifferent() {
Node s1 = mock(Node.class);
Node e1 = mock(Node.class);
Node s2 = mock(Node.class);
Node e2 = mock(Node.class);
when(s1.getOffset()).thenReturn(1L);
when(e1.getOffset()).thenReturn(3L);
when(s2.getOffset()).thenReturn(1L);
when(e2.getOffset()).thenReturn(4L);
FeatureMap fm = new SimpleFeatureMapImpl();
AnnotationImpl a = new AnnotationImpl(1, s1, e1, "A", fm);
AnnotationImpl b = new AnnotationImpl(2, s2, e2, "A", fm);
assertFalse(a.equals(b));
}

@Test
public void testHashCodeWithNullFields() {
AnnotationImpl annotation = new AnnotationImpl(null, null, null, null, null);
int hash = annotation.hashCode();
assertEquals(17, hash);
}

@Test
public void testEqualsReturnsFalseWhenTypeMismatch() {
Node node = mock(Node.class);
when(node.getOffset()).thenReturn(1L);
FeatureMap fm = new SimpleFeatureMapImpl();
AnnotationImpl a = new AnnotationImpl(1, node, node, "A", fm);
Object other = "NotAnnotation";
assertFalse(a.equals(other));
}

@Test
public void testEqualsReturnsFalseWhenTypeIsNullOnOneSide() {
Node node = mock(Node.class);
when(node.getOffset()).thenReturn(1L);
FeatureMap fm = new SimpleFeatureMapImpl();
AnnotationImpl a = new AnnotationImpl(1, node, node, "Text", fm);
AnnotationImpl b = new AnnotationImpl(1, node, node, null, fm);
assertFalse(a.equals(b));
}

@Test
public void testEqualsReturnsFalseWhenIdIsNullOnOneSide() {
Node node = mock(Node.class);
when(node.getOffset()).thenReturn(1L);
FeatureMap fm = new SimpleFeatureMapImpl();
AnnotationImpl a = new AnnotationImpl(null, node, node, "X", fm);
AnnotationImpl b = new AnnotationImpl(2, node, node, "X", fm);
assertFalse(a.equals(b));
}

@Test
public void testEqualsReturnsFalseWhenStartOffsetDiffers() {
Node start1 = mock(Node.class);
Node start2 = mock(Node.class);
Node end = mock(Node.class);
when(start1.getOffset()).thenReturn(1L);
when(start2.getOffset()).thenReturn(2L);
when(end.getOffset()).thenReturn(5L);
FeatureMap fm = new SimpleFeatureMapImpl();
AnnotationImpl a = new AnnotationImpl(1, start1, end, "Tag", fm);
AnnotationImpl b = new AnnotationImpl(1, start2, end, "Tag", fm);
assertFalse(a.equals(b));
}

@Test
public void testEqualsReturnsFalseWhenEndOffsetDiffers() {
Node start = mock(Node.class);
Node end1 = mock(Node.class);
Node end2 = mock(Node.class);
when(start.getOffset()).thenReturn(0L);
when(end1.getOffset()).thenReturn(5L);
when(end2.getOffset()).thenReturn(6L);
FeatureMap fm = new SimpleFeatureMapImpl();
AnnotationImpl a = new AnnotationImpl(1, start, end1, "T", fm);
AnnotationImpl b = new AnnotationImpl(1, start, end2, "T", fm);
assertFalse(a.equals(b));
}

@Test
public void testEqualsReturnsFalseWhenFeatureMapsDiffer() {
Node node = mock(Node.class);
when(node.getOffset()).thenReturn(1L);
FeatureMap fm1 = new SimpleFeatureMapImpl();
fm1.put("a", 1);
FeatureMap fm2 = new SimpleFeatureMapImpl();
fm2.put("a", 2);
AnnotationImpl a = new AnnotationImpl(1, node, node, "T", fm1);
AnnotationImpl b = new AnnotationImpl(1, node, node, "T", fm2);
assertFalse(a.equals(b));
}

@Test
public void testIsCompatibleWhenOtherFeatureMapIsNull() {
Node node = mock(Node.class);
when(node.getOffset()).thenReturn(5L);
FeatureMap map = new SimpleFeatureMapImpl();
map.put("k", "v");
AnnotationImpl a = new AnnotationImpl(1, node, node, "T", map);
AnnotationImpl b = new AnnotationImpl(2, node, node, "T", null);
assertTrue(a.isCompatible(b));
}

@Test
public void testIsPartiallyCompatibleReturnsTrueWhenOtherFeatureMapIsNull() {
Node s1 = mock(Node.class);
Node e1 = mock(Node.class);
Node s2 = mock(Node.class);
Node e2 = mock(Node.class);
when(s1.getOffset()).thenReturn(10L);
when(e1.getOffset()).thenReturn(20L);
when(s2.getOffset()).thenReturn(15L);
when(e2.getOffset()).thenReturn(25L);
FeatureMap fm = new SimpleFeatureMapImpl();
fm.put("f", "v");
AnnotationImpl a = new AnnotationImpl(1, s1, e1, "T", fm);
AnnotationImpl b = new AnnotationImpl(2, s2, e2, "T", null);
assertTrue(a.isPartiallyCompatible(b));
}

@Test
public void testIsCompatibleWithKeySetNullFallsBackToSingleParamVersion() {
Node node = mock(Node.class);
when(node.getOffset()).thenReturn(0L);
FeatureMap fm1 = new SimpleFeatureMapImpl();
fm1.put("k", "v");
FeatureMap fm2 = new SimpleFeatureMapImpl();
fm2.put("k", "v");
AnnotationImpl a1 = new AnnotationImpl(1, node, node, "X", fm1);
AnnotationImpl a2 = new AnnotationImpl(2, node, node, "X", fm2);
assertTrue(a1.isCompatible(a2, null));
}

@Test
public void testIsPartiallyCompatibleWithKeySetNullFallsBack() {
Node s1 = mock(Node.class);
Node e1 = mock(Node.class);
Node s2 = mock(Node.class);
Node e2 = mock(Node.class);
when(s1.getOffset()).thenReturn(10L);
when(e1.getOffset()).thenReturn(30L);
when(s2.getOffset()).thenReturn(20L);
when(e2.getOffset()).thenReturn(40L);
FeatureMap fm1 = new SimpleFeatureMapImpl();
fm1.put("x", 1);
FeatureMap fm2 = new SimpleFeatureMapImpl();
fm2.put("x", 1);
fm2.put("y", 2);
AnnotationImpl a = new AnnotationImpl(1, s1, e1, "T", fm1);
AnnotationImpl b = new AnnotationImpl(2, s2, e2, "T", fm2);
assertTrue(a.isPartiallyCompatible(b, null));
}

@Test
public void testOverlapsReturnsFalseWhenEndBeforeOtherStart() {
Node s1 = mock(Node.class);
Node e1 = mock(Node.class);
Node s2 = mock(Node.class);
Node e2 = mock(Node.class);
when(s1.getOffset()).thenReturn(0L);
when(e1.getOffset()).thenReturn(10L);
when(s2.getOffset()).thenReturn(15L);
when(e2.getOffset()).thenReturn(20L);
Annotation a = new AnnotationImpl(1, s1, e1, "T", new SimpleFeatureMapImpl());
Annotation b = new AnnotationImpl(2, s2, e2, "T", new SimpleFeatureMapImpl());
assertFalse(a.overlaps(b));
}

@Test
public void testOverlapsReturnsFalseWhenStartAfterOtherEnd() {
Node s1 = mock(Node.class);
Node e1 = mock(Node.class);
Node s2 = mock(Node.class);
Node e2 = mock(Node.class);
when(s1.getOffset()).thenReturn(25L);
when(e1.getOffset()).thenReturn(30L);
when(s2.getOffset()).thenReturn(10L);
when(e2.getOffset()).thenReturn(20L);
Annotation a = new AnnotationImpl(1, s1, e1, "Tag", new SimpleFeatureMapImpl());
Annotation b = new AnnotationImpl(2, s2, e2, "Tag", new SimpleFeatureMapImpl());
assertFalse(a.overlaps(b));
}

@Test
public void testWithinSpanOfReturnsFalseIfArgumentIsNull() {
Node node = mock(Node.class);
when(node.getOffset()).thenReturn(5L);
AnnotationImpl annot = new AnnotationImpl(1, node, node, "Type", new SimpleFeatureMapImpl());
assertFalse(annot.withinSpanOf(null));
}

@Test
public void testWithinSpanOfReturnsFalseIfOffsetsAreNull() {
Node sA = mock(Node.class);
Node eA = mock(Node.class);
Node sB = mock(Node.class);
Node eB = mock(Node.class);
when(sA.getOffset()).thenReturn(null);
when(eA.getOffset()).thenReturn(null);
when(sB.getOffset()).thenReturn(5L);
when(eB.getOffset()).thenReturn(25L);
AnnotationImpl a = new AnnotationImpl(1, sA, eA, "T", new SimpleFeatureMapImpl());
AnnotationImpl b = new AnnotationImpl(2, sB, eB, "T", new SimpleFeatureMapImpl());
assertFalse(a.withinSpanOf(b));
}

@Test
public void testAddAnnotationListenerFiresEventOnceOnly() {
Node node = mock(Node.class);
when(node.getOffset()).thenReturn(0L);
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationImpl annotation = new AnnotationImpl(1, node, node, "type", features);
AnnotationListener listener = mock(AnnotationListener.class);
annotation.addAnnotationListener(listener);
annotation.addAnnotationListener(listener);
FeatureMap newFeatures = new SimpleFeatureMapImpl();
newFeatures.put("k", "v");
annotation.setFeatures(newFeatures);
verify(listener, times(1)).annotationUpdated(any());
}

@Test
public void testRemoveAnnotationListenerBeforeAddDoesNothing() {
Node node = mock(Node.class);
when(node.getOffset()).thenReturn(0L);
FeatureMap map = new SimpleFeatureMapImpl();
AnnotationImpl annot = new AnnotationImpl(1, node, node, "T", map);
AnnotationListener listener = mock(AnnotationListener.class);
annot.removeAnnotationListener(listener);
FeatureMap newMap = new SimpleFeatureMapImpl();
newMap.put("x", "y");
annot.setFeatures(newMap);
verify(listener, never()).annotationUpdated(any());
}

@Test
public void testSetFeaturesNullToNonNullAndBack() {
Node node = mock(Node.class);
when(node.getOffset()).thenReturn(1L);
FeatureMap initial = new SimpleFeatureMapImpl();
initial.put("a", 1);
AnnotationImpl ann = new AnnotationImpl(1, node, node, "T", initial);
FeatureMap updated = new SimpleFeatureMapImpl();
updated.put("b", 2);
ann.setFeatures(updated);
assertEquals(updated, ann.getFeatures());
}

@Test
public void testCoextensiveReturnsFalseWhenOneStartNodeIsNull() {
Node endNode = mock(Node.class);
when(endNode.getOffset()).thenReturn(20L);
Node validStart = mock(Node.class);
when(validStart.getOffset()).thenReturn(10L);
FeatureMap fm = new SimpleFeatureMapImpl();
AnnotationImpl a1 = new AnnotationImpl(1, null, endNode, "T", fm);
AnnotationImpl a2 = new AnnotationImpl(2, validStart, endNode, "T", fm);
assertFalse(a1.coextensive(a2));
}

@Test
public void testCoextensiveReturnsFalseWhenOneEndNodeIsNull() {
Node startNode = mock(Node.class);
when(startNode.getOffset()).thenReturn(10L);
Node validEnd = mock(Node.class);
when(validEnd.getOffset()).thenReturn(20L);
FeatureMap fm = new SimpleFeatureMapImpl();
AnnotationImpl a1 = new AnnotationImpl(1, startNode, null, "T", fm);
AnnotationImpl a2 = new AnnotationImpl(2, startNode, validEnd, "T", fm);
assertFalse(a1.coextensive(a2));
}

@Test
public void testOverlapsReturnsFalseWhenStartOrEndOffsetIsNull() {
Node start1 = mock(Node.class);
Node end1 = mock(Node.class);
Node start2 = mock(Node.class);
Node end2 = mock(Node.class);
when(start1.getOffset()).thenReturn(null);
when(end1.getOffset()).thenReturn(20L);
when(start2.getOffset()).thenReturn(15L);
when(end2.getOffset()).thenReturn(25L);
FeatureMap fm = new SimpleFeatureMapImpl();
Annotation a = new AnnotationImpl(1, start1, end1, "T", fm);
Annotation b = new AnnotationImpl(2, start2, end2, "T", fm);
assertFalse(a.overlaps(b));
}

@Test
public void testEqualsReturnsFalseWhenStartNodeOffsetIsNullOnOneSide() {
Node start1 = mock(Node.class);
Node end = mock(Node.class);
Node start2 = mock(Node.class);
when(start1.getOffset()).thenReturn(null);
when(start2.getOffset()).thenReturn(5L);
when(end.getOffset()).thenReturn(10L);
FeatureMap fm = new SimpleFeatureMapImpl();
AnnotationImpl a1 = new AnnotationImpl(1, start1, end, "A", fm);
AnnotationImpl a2 = new AnnotationImpl(1, start2, end, "A", fm);
assertFalse(a1.equals(a2));
}

@Test
public void testEqualsReturnsFalseWhenEndNodeOffsetIsNullOnOneSide() {
Node start = mock(Node.class);
Node end1 = mock(Node.class);
Node end2 = mock(Node.class);
when(start.getOffset()).thenReturn(0L);
when(end1.getOffset()).thenReturn(null);
when(end2.getOffset()).thenReturn(10L);
FeatureMap fm = new SimpleFeatureMapImpl();
AnnotationImpl a1 = new AnnotationImpl(1, start, end1, "Test", fm);
AnnotationImpl a2 = new AnnotationImpl(1, start, end2, "Test", fm);
assertFalse(a1.equals(a2));
}

@Test
public void testIsCompatibleReturnsFalseIfNotCoextensive() {
Node start1 = mock(Node.class);
Node end1 = mock(Node.class);
Node start2 = mock(Node.class);
Node end2 = mock(Node.class);
when(start1.getOffset()).thenReturn(5L);
when(end1.getOffset()).thenReturn(10L);
when(start2.getOffset()).thenReturn(15L);
when(end2.getOffset()).thenReturn(20L);
FeatureMap fm1 = new SimpleFeatureMapImpl();
fm1.put("a", 1);
FeatureMap fm2 = new SimpleFeatureMapImpl();
fm2.put("a", 1);
AnnotationImpl a = new AnnotationImpl(1, start1, end1, "X", fm1);
AnnotationImpl b = new AnnotationImpl(2, start2, end2, "X", fm2);
assertFalse(a.isCompatible(b));
}

@Test
public void testIsCompatibleReturnsFalseWhenFeaturesDoNotSubsumed() {
Node node = mock(Node.class);
when(node.getOffset()).thenReturn(5L);
FeatureMap thisMap = new SimpleFeatureMapImpl();
thisMap.put("k", 1);
FeatureMap otherMap = new SimpleFeatureMapImpl();
otherMap.put("k", 2);
AnnotationImpl a = new AnnotationImpl(1, node, node, "A", thisMap);
AnnotationImpl b = new AnnotationImpl(2, node, node, "A", otherMap);
assertFalse(a.isCompatible(b));
}

@Test
public void testIsCompatibleWithFeatureSetReturnsFalseIfNotCoextensive() {
Node sA = mock(Node.class);
Node eA = mock(Node.class);
Node sB = mock(Node.class);
Node eB = mock(Node.class);
when(sA.getOffset()).thenReturn(5L);
when(eA.getOffset()).thenReturn(15L);
when(sB.getOffset()).thenReturn(0L);
when(eB.getOffset()).thenReturn(10L);
FeatureMap fm1 = new SimpleFeatureMapImpl();
fm1.put("x", "y");
FeatureMap fm2 = new SimpleFeatureMapImpl();
fm2.put("x", "y");
Set<Object> keys = new HashSet<Object>();
keys.add("x");
AnnotationImpl a = new AnnotationImpl(1, sA, eA, "X", fm1);
AnnotationImpl b = new AnnotationImpl(2, sB, eB, "X", fm2);
assertFalse(a.isCompatible(b, keys));
}

@Test
public void testIsPartiallyCompatibleWithFeatureSetReturnsFalseIfNotOverlapping() {
Node sA = mock(Node.class);
Node eA = mock(Node.class);
Node sB = mock(Node.class);
Node eB = mock(Node.class);
when(sA.getOffset()).thenReturn(0L);
when(eA.getOffset()).thenReturn(10L);
when(sB.getOffset()).thenReturn(15L);
when(eB.getOffset()).thenReturn(20L);
FeatureMap fmapA = new SimpleFeatureMapImpl();
fmapA.put("a", "b");
FeatureMap fmapB = new SimpleFeatureMapImpl();
fmapB.put("a", "b");
Set<Object> featureKeys = new HashSet<Object>();
featureKeys.add("a");
AnnotationImpl a = new AnnotationImpl(1, sA, eA, "Type", fmapA);
AnnotationImpl b = new AnnotationImpl(2, sB, eB, "Type", fmapB);
assertFalse(a.isPartiallyCompatible(b, featureKeys));
}

@Test
public void testWithinSpanOfReturnsFalseIfThisIsNotFullyContained() {
Node sA = mock(Node.class);
Node eA = mock(Node.class);
Node sB = mock(Node.class);
Node eB = mock(Node.class);
when(sA.getOffset()).thenReturn(10L);
when(eA.getOffset()).thenReturn(30L);
when(sB.getOffset()).thenReturn(5L);
when(eB.getOffset()).thenReturn(25L);
FeatureMap fm = new SimpleFeatureMapImpl();
AnnotationImpl a = new AnnotationImpl(1, sA, eA, "X", fm);
AnnotationImpl b = new AnnotationImpl(2, sB, eB, "X", fm);
assertFalse(a.withinSpanOf(b));
}

@Test
public void testAddAnnotationListenerDoesNotAddDuplicate() {
Node node = mock(Node.class);
when(node.getOffset()).thenReturn(0L);
FeatureMap fm = new SimpleFeatureMapImpl();
AnnotationImpl annotation = new AnnotationImpl(1, node, node, "Tag", fm);
AnnotationListener listener = mock(AnnotationListener.class);
annotation.addAnnotationListener(listener);
annotation.addAnnotationListener(listener);
FeatureMap newFm = new SimpleFeatureMapImpl();
newFm.put("x", 42);
annotation.setFeatures(newFm);
verify(listener, times(1)).annotationUpdated(any());
}

@Test
public void testRemoveAnnotationListenerTwiceHasNoEffect() {
Node node = mock(Node.class);
when(node.getOffset()).thenReturn(1L);
FeatureMap fm = new SimpleFeatureMapImpl();
AnnotationImpl annotation = new AnnotationImpl(1, node, node, "Tag", fm);
AnnotationListener listener = mock(AnnotationListener.class);
annotation.addAnnotationListener(listener);
annotation.removeAnnotationListener(listener);
annotation.removeAnnotationListener(listener);
FeatureMap newFm = new SimpleFeatureMapImpl();
newFm.put("a", "b");
annotation.setFeatures(newFm);
verify(listener, never()).annotationUpdated(any());
}

@Test
public void testOverlapsReturnsFalseIfBothStartOrEndOffsetsAreNull() {
Node s1 = mock(Node.class);
Node e1 = mock(Node.class);
Node s2 = mock(Node.class);
Node e2 = mock(Node.class);
when(s1.getOffset()).thenReturn(null);
when(e1.getOffset()).thenReturn(null);
when(s2.getOffset()).thenReturn(null);
when(e2.getOffset()).thenReturn(null);
AnnotationImpl a = new AnnotationImpl(1, s1, e1, "T", new SimpleFeatureMapImpl());
AnnotationImpl b = new AnnotationImpl(2, s2, e2, "T", new SimpleFeatureMapImpl());
assertFalse(a.overlaps(b));
}

@Test
public void testHashCodeWhenIdAndTypeAreNull() {
AnnotationImpl annotation = new AnnotationImpl(null, null, null, null, null);
int expected = 17;
assertEquals(expected, annotation.hashCode());
}

@Test
public void testHashCodeWithOnlyTypeSet() {
AnnotationImpl annotation = new AnnotationImpl(null, null, null, "TestType", null);
int expected = 17;
expected = 31 * expected + "TestType".hashCode();
assertEquals(expected, annotation.hashCode());
}

@Test
public void testHashCodeWithOnlyIdSet() {
AnnotationImpl annotation = new AnnotationImpl(99, null, null, null, null);
int expected = 17;
expected = 31 * expected + 0;
expected = 31 * expected + 99;
assertEquals(expected, annotation.hashCode());
}

@Test
public void testSetFeaturesWhenEventHandlerIsNull() {
Node node = mock(Node.class);
when(node.getOffset()).thenReturn(5L);
FeatureMap initial = new SimpleFeatureMapImpl();
FeatureMap updated = new SimpleFeatureMapImpl();
updated.put("newFeature", 1);
AnnotationImpl annotation = new AnnotationImpl(1, node, node, "Type", initial);
annotation.setFeatures(updated);
assertEquals(updated, annotation.getFeatures());
}

@Test
public void testSetFeaturesWhenOldFeaturesIsNullAndNewFeaturesIsNotNull() {
Node node = mock(Node.class);
when(node.getOffset()).thenReturn(5L);
FeatureMap newFeatures = new SimpleFeatureMapImpl();
newFeatures.put("f1", "val");
AnnotationImpl annotation = new AnnotationImpl(101, node, node, "Entity", null);
annotation.setFeatures(newFeatures);
assertEquals(newFeatures, annotation.getFeatures());
}

@Test
public void testCoextensiveWhenStartOffsetIsNullOnBothSides() {
Node s1 = mock(Node.class);
Node e1 = mock(Node.class);
Node s2 = mock(Node.class);
Node e2 = mock(Node.class);
when(s1.getOffset()).thenReturn(null);
when(s2.getOffset()).thenReturn(null);
when(e1.getOffset()).thenReturn(100L);
when(e2.getOffset()).thenReturn(100L);
AnnotationImpl a = new AnnotationImpl(1, s1, e1, "X", new SimpleFeatureMapImpl());
AnnotationImpl b = new AnnotationImpl(2, s2, e2, "X", new SimpleFeatureMapImpl());
assertTrue(a.coextensive(b));
}

@Test
public void testCoextensiveWhenEndOffsetIsNullOnBothSides() {
Node s1 = mock(Node.class);
Node e1 = mock(Node.class);
Node s2 = mock(Node.class);
Node e2 = mock(Node.class);
when(s1.getOffset()).thenReturn(10L);
when(s2.getOffset()).thenReturn(10L);
when(e1.getOffset()).thenReturn(null);
when(e2.getOffset()).thenReturn(null);
AnnotationImpl a = new AnnotationImpl(10, s1, e1, "T", new SimpleFeatureMapImpl());
AnnotationImpl b = new AnnotationImpl(11, s2, e2, "T", new SimpleFeatureMapImpl());
assertTrue(a.coextensive(b));
}

@Test
public void testFireAnnotationUpdatedWithMultipleListeners() {
Node node = mock(Node.class);
when(node.getOffset()).thenReturn(10L);
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationImpl annotation = new AnnotationImpl(44, node, node, "Type", features);
AnnotationListener listener1 = mock(AnnotationListener.class);
AnnotationListener listener2 = mock(AnnotationListener.class);
annotation.addAnnotationListener(listener1);
annotation.addAnnotationListener(listener2);
FeatureMap newMap = new SimpleFeatureMapImpl();
newMap.put("updated", true);
annotation.setFeatures(newMap);
verify(listener1, times(1)).annotationUpdated(any(AnnotationEvent.class));
verify(listener2, times(1)).annotationUpdated(any(AnnotationEvent.class));
}

@Test
public void testAddAnnotationListenerThenChangeFeatureValueFiresEvent() {
Node node = mock(Node.class);
when(node.getOffset()).thenReturn(0L);
FeatureMap fm = new SimpleFeatureMapImpl();
fm.put("a", "b");
AnnotationImpl annotation = new AnnotationImpl(1, node, node, "Type", fm);
AnnotationListener listener = mock(AnnotationListener.class);
annotation.addAnnotationListener(listener);
fm.put("a", "c");
annotation.getFeatures().put("x", "y");
annotation.setFeatures(fm);
verify(listener, atLeastOnce()).annotationUpdated(any(AnnotationEvent.class));
}

@Test
public void testSetFeaturesWhenListenersPresentOldFeatureTriggersRemoveListener() {
Node node = mock(Node.class);
when(node.getOffset()).thenReturn(0L);
FeatureMap oldMap = spy(new SimpleFeatureMapImpl());
FeatureMap newMap = spy(new SimpleFeatureMapImpl());
newMap.put("k", "v");
AnnotationImpl annotation = new AnnotationImpl(1, node, node, "T", oldMap);
AnnotationListener listener = mock(AnnotationListener.class);
annotation.addAnnotationListener(listener);
annotation.setFeatures(newMap);
verify(oldMap, atLeastOnce()).removeFeatureMapListener(any());
verify(newMap, atLeastOnce()).addFeatureMapListener(any());
}

@Test
public void testEqualsReturnsFalseWhenFeatureMapsAreNotEqual() {
Node node = mock(Node.class);
when(node.getOffset()).thenReturn(1L);
FeatureMap map1 = new SimpleFeatureMapImpl();
map1.put("key", "value1");
FeatureMap map2 = new SimpleFeatureMapImpl();
map2.put("key", "value2");
AnnotationImpl a = new AnnotationImpl(10, node, node, "Entity", map1);
AnnotationImpl b = new AnnotationImpl(10, node, node, "Entity", map2);
assertFalse(a.equals(b));
}

@Test
public void testOverlapsReturnsFalseWhenStartEqualsOtherEnd() {
Node s1 = mock(Node.class);
Node e1 = mock(Node.class);
Node s2 = mock(Node.class);
Node e2 = mock(Node.class);
when(s1.getOffset()).thenReturn(10L);
when(e1.getOffset()).thenReturn(20L);
when(s2.getOffset()).thenReturn(20L);
when(e2.getOffset()).thenReturn(30L);
AnnotationImpl a = new AnnotationImpl(1, s1, e1, "Type", new SimpleFeatureMapImpl());
AnnotationImpl b = new AnnotationImpl(2, s2, e2, "Type", new SimpleFeatureMapImpl());
assertFalse(a.overlaps(b));
}

@Test
public void testOverlapsReturnsFalseWhenEndEqualsOtherStart() {
Node s1 = mock(Node.class);
Node e1 = mock(Node.class);
Node s2 = mock(Node.class);
Node e2 = mock(Node.class);
when(s1.getOffset()).thenReturn(50L);
when(e1.getOffset()).thenReturn(60L);
when(s2.getOffset()).thenReturn(40L);
when(e2.getOffset()).thenReturn(50L);
AnnotationImpl a = new AnnotationImpl(1, s1, e1, "T", new SimpleFeatureMapImpl());
AnnotationImpl b = new AnnotationImpl(2, s2, e2, "T", new SimpleFeatureMapImpl());
assertFalse(a.overlaps(b));
}

@Test
public void testEqualsWithAllFieldsNullOnBothSides() {
AnnotationImpl a1 = new AnnotationImpl(null, null, null, null, null);
AnnotationImpl a2 = new AnnotationImpl(null, null, null, null, null);
assertTrue(a1.equals(a2));
}

@Test
public void testEqualsWhenOneFeatureMapIsNull() {
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(1L);
when(end.getOffset()).thenReturn(10L);
FeatureMap features = new SimpleFeatureMapImpl();
features.put("f1", "v1");
AnnotationImpl a1 = new AnnotationImpl(1, start, end, "type", features);
AnnotationImpl a2 = new AnnotationImpl(1, start, end, "type", null);
assertFalse(a1.equals(a2));
}

@Test
public void testCompareToWithEqualIds() {
Node node = mock(Node.class);
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationImpl a1 = new AnnotationImpl(1, node, node, "type", features);
AnnotationImpl a2 = new AnnotationImpl(1, node, node, "type", features);
assertEquals(0, a1.compareTo(a2));
}

@Test
public void testIsCompatibleExactFeatureMatch() {
Node node = mock(Node.class);
when(node.getOffset()).thenReturn(5L);
FeatureMap mapA = new SimpleFeatureMapImpl();
mapA.put("x", 1);
FeatureMap mapB = new SimpleFeatureMapImpl();
mapB.put("x", 1);
AnnotationImpl a = new AnnotationImpl(1, node, node, "T", mapA);
AnnotationImpl b = new AnnotationImpl(2, node, node, "T", mapB);
assertTrue(a.isCompatible(b));
}

@Test
public void testIsCompatibleWithNullThisFeatureMap() {
Node node = mock(Node.class);
when(node.getOffset()).thenReturn(5L);
FeatureMap otherMap = new SimpleFeatureMapImpl();
otherMap.put("x", 1);
AnnotationImpl a = new AnnotationImpl(1, node, node, "T", null);
AnnotationImpl b = new AnnotationImpl(2, node, node, "T", otherMap);
assertTrue(a.isCompatible(b));
}

@Test
public void testIsCompatibleFeatureKeyPartialSubset() {
Node node = mock(Node.class);
when(node.getOffset()).thenReturn(7L);
FeatureMap thisMap = new SimpleFeatureMapImpl();
thisMap.put("a", 1);
thisMap.put("b", 2);
FeatureMap otherMap = new SimpleFeatureMapImpl();
otherMap.put("a", 1);
otherMap.put("b", 2);
otherMap.put("c", 3);
Set<Object> keys = new HashSet<Object>();
keys.add("b");
AnnotationImpl a = new AnnotationImpl(1, node, node, "Test", thisMap);
AnnotationImpl b = new AnnotationImpl(2, node, node, "Test", otherMap);
assertTrue(a.isCompatible(b, keys));
}

@Test
public void testIsPartiallyCompatibleWithNoSharedFeatures() {
Node s1 = mock(Node.class);
Node e1 = mock(Node.class);
Node s2 = mock(Node.class);
Node e2 = mock(Node.class);
when(s1.getOffset()).thenReturn(10L);
when(e1.getOffset()).thenReturn(20L);
when(s2.getOffset()).thenReturn(15L);
when(e2.getOffset()).thenReturn(25L);
FeatureMap fm1 = new SimpleFeatureMapImpl();
fm1.put("a", "1");
FeatureMap fm2 = new SimpleFeatureMapImpl();
fm2.put("b", "2");
AnnotationImpl a = new AnnotationImpl(1, s1, e1, "T", fm1);
AnnotationImpl b = new AnnotationImpl(2, s2, e2, "T", fm2);
assertFalse(a.isPartiallyCompatible(b));
}

@Test
public void testIsPartiallyCompatibleFeatureSubsetFalseDueToMismatch() {
Node s1 = mock(Node.class);
Node e1 = mock(Node.class);
Node s2 = mock(Node.class);
Node e2 = mock(Node.class);
when(s1.getOffset()).thenReturn(0L);
when(e1.getOffset()).thenReturn(20L);
when(s2.getOffset()).thenReturn(10L);
when(e2.getOffset()).thenReturn(30L);
FeatureMap aMap = new SimpleFeatureMapImpl();
aMap.put("x", "val1");
FeatureMap bMap = new SimpleFeatureMapImpl();
bMap.put("x", "val2");
Set<Object> subset = new HashSet<Object>();
subset.add("x");
AnnotationImpl a = new AnnotationImpl(1, s1, e1, "Tag", aMap);
AnnotationImpl b = new AnnotationImpl(2, s2, e2, "Tag", bMap);
assertFalse(a.isPartiallyCompatible(b, subset));
}

@Test
public void testWithinSpanOfWithEqualOffsets() {
Node nodeStart = mock(Node.class);
Node nodeEnd = mock(Node.class);
when(nodeStart.getOffset()).thenReturn(5L);
when(nodeEnd.getOffset()).thenReturn(15L);
AnnotationImpl inner = new AnnotationImpl(1, nodeStart, nodeEnd, "T", new SimpleFeatureMapImpl());
AnnotationImpl outer = new AnnotationImpl(2, nodeStart, nodeEnd, "T", new SimpleFeatureMapImpl());
assertTrue(inner.withinSpanOf(outer));
}

@Test
public void testWithinSpanOfFalseWhenThisStartBeforeOther() {
Node sA = mock(Node.class);
Node eA = mock(Node.class);
Node sB = mock(Node.class);
Node eB = mock(Node.class);
when(sA.getOffset()).thenReturn(4L);
when(eA.getOffset()).thenReturn(15L);
when(sB.getOffset()).thenReturn(5L);
when(eB.getOffset()).thenReturn(25L);
AnnotationImpl a = new AnnotationImpl(1, sA, eA, "X", new SimpleFeatureMapImpl());
AnnotationImpl b = new AnnotationImpl(2, sB, eB, "X", new SimpleFeatureMapImpl());
assertFalse(a.withinSpanOf(b));
}

@Test
public void testWithinSpanOfFalseWhenThisEndAfterOther() {
Node sA = mock(Node.class);
Node eA = mock(Node.class);
Node sB = mock(Node.class);
Node eB = mock(Node.class);
when(sA.getOffset()).thenReturn(10L);
when(eA.getOffset()).thenReturn(30L);
when(sB.getOffset()).thenReturn(5L);
when(eB.getOffset()).thenReturn(25L);
AnnotationImpl a = new AnnotationImpl(1, sA, eA, "X", new SimpleFeatureMapImpl());
AnnotationImpl b = new AnnotationImpl(2, sB, eB, "X", new SimpleFeatureMapImpl());
assertFalse(a.withinSpanOf(b));
}

@Test
public void testRemoveAnnotationListenerWithoutAdding() {
Node node = mock(Node.class);
when(node.getOffset()).thenReturn(0L);
FeatureMap features = new SimpleFeatureMapImpl();
AnnotationImpl ann = new AnnotationImpl(1, node, node, "T", features);
AnnotationListener listener = mock(AnnotationListener.class);
ann.removeAnnotationListener(listener);
features.put("modified", true);
ann.setFeatures(features);
verify(listener, never()).annotationUpdated(any());
}

@Test
public void testEqualsReturnsFalseWhenStartNodeItselfIsNullOnOneSide() {
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(1L);
when(end.getOffset()).thenReturn(10L);
FeatureMap fm = new SimpleFeatureMapImpl();
AnnotationImpl a = new AnnotationImpl(1, start, end, "X", fm);
AnnotationImpl b = new AnnotationImpl(1, null, end, "X", fm);
assertFalse(a.equals(b));
}

@Test
public void testEqualsReturnsFalseWhenEndNodeItselfIsNullOnOneSide() {
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(2L);
when(end.getOffset()).thenReturn(20L);
FeatureMap fm = new SimpleFeatureMapImpl();
AnnotationImpl a = new AnnotationImpl(2, start, end, "Y", fm);
AnnotationImpl b = new AnnotationImpl(2, start, null, "Y", fm);
assertFalse(a.equals(b));
}

@Test
public void testEqualsReturnsFalseWhenStartOffsetIsNullOnOneSide() {
Node start1 = mock(Node.class);
Node start2 = mock(Node.class);
Node end = mock(Node.class);
when(start1.getOffset()).thenReturn(null);
when(start2.getOffset()).thenReturn(5L);
when(end.getOffset()).thenReturn(15L);
FeatureMap fm = new SimpleFeatureMapImpl();
AnnotationImpl a = new AnnotationImpl(3, start1, end, "Z", fm);
AnnotationImpl b = new AnnotationImpl(3, start2, end, "Z", fm);
assertFalse(a.equals(b));
}

@Test
public void testEqualsReturnsFalseWhenEndOffsetIsNullOnOneSide() {
Node start = mock(Node.class);
Node end1 = mock(Node.class);
Node end2 = mock(Node.class);
when(start.getOffset()).thenReturn(5L);
when(end1.getOffset()).thenReturn(null);
when(end2.getOffset()).thenReturn(25L);
FeatureMap fm = new SimpleFeatureMapImpl();
AnnotationImpl a = new AnnotationImpl(4, start, end1, "T", fm);
AnnotationImpl b = new AnnotationImpl(4, start, end2, "T", fm);
assertFalse(a.equals(b));
}

@Test
public void testIsCompatibleReturnsFalseWhenOtherFeaturesDoNotSubsumeThis() {
Node node = mock(Node.class);
when(node.getOffset()).thenReturn(1L);
FeatureMap aMap = new SimpleFeatureMapImpl();
aMap.put("key", "A");
FeatureMap bMap = new SimpleFeatureMapImpl();
bMap.put("key", "B");
AnnotationImpl a = new AnnotationImpl(1, node, node, "X", aMap);
AnnotationImpl b = new AnnotationImpl(2, node, node, "X", bMap);
assertFalse(a.isCompatible(b));
}

@Test
public void testIsCompatibleReturnsFalseWhenKeysProvidedDoNotMatch() {
Node node = mock(Node.class);
when(node.getOffset()).thenReturn(0L);
FeatureMap aMap = new SimpleFeatureMapImpl();
aMap.put("x", 1);
FeatureMap bMap = new SimpleFeatureMapImpl();
bMap.put("x", 2);
Set<Object> keys = new HashSet<Object>();
keys.add("x");
AnnotationImpl a = new AnnotationImpl(1, node, node, "T", aMap);
AnnotationImpl b = new AnnotationImpl(2, node, node, "T", bMap);
assertFalse(a.isCompatible(b, keys));
}

@Test
public void testIsPartiallyCompatibleReturnsFalseWhenOverlapsTrueButFeatureMismatch() {
Node start1 = mock(Node.class);
Node end1 = mock(Node.class);
Node start2 = mock(Node.class);
Node end2 = mock(Node.class);
when(start1.getOffset()).thenReturn(10L);
when(end1.getOffset()).thenReturn(35L);
when(start2.getOffset()).thenReturn(20L);
when(end2.getOffset()).thenReturn(40L);
FeatureMap fm1 = new SimpleFeatureMapImpl();
fm1.put("k", "v1");
FeatureMap fm2 = new SimpleFeatureMapImpl();
fm2.put("k", "v2");
AnnotationImpl a = new AnnotationImpl(1, start1, end1, "Overlap", fm1);
AnnotationImpl b = new AnnotationImpl(2, start2, end2, "Overlap", fm2);
assertFalse(a.isPartiallyCompatible(b));
}

@Test
public void testOverlapsReturnsTrueWhenOverlapIsPartialFromRight() {
Node s1 = mock(Node.class);
Node e1 = mock(Node.class);
Node s2 = mock(Node.class);
Node e2 = mock(Node.class);
when(s1.getOffset()).thenReturn(10L);
when(e1.getOffset()).thenReturn(20L);
when(s2.getOffset()).thenReturn(15L);
when(e2.getOffset()).thenReturn(30L);
Annotation a = new AnnotationImpl(1, s1, e1, "A", new SimpleFeatureMapImpl());
Annotation b = new AnnotationImpl(2, s2, e2, "A", new SimpleFeatureMapImpl());
assertTrue(a.overlaps(b));
}

@Test
public void testOverlapsReturnsTrueWhenOverlapIsPartialFromLeft() {
Node s1 = mock(Node.class);
Node e1 = mock(Node.class);
Node s2 = mock(Node.class);
Node e2 = mock(Node.class);
when(s1.getOffset()).thenReturn(15L);
when(e1.getOffset()).thenReturn(30L);
when(s2.getOffset()).thenReturn(10L);
when(e2.getOffset()).thenReturn(20L);
Annotation a = new AnnotationImpl(1, s1, e1, "Tag", new SimpleFeatureMapImpl());
Annotation b = new AnnotationImpl(2, s2, e2, "Tag", new SimpleFeatureMapImpl());
assertTrue(a.overlaps(b));
}

@Test
public void testCompareToWithHigherId() {
Node node = mock(Node.class);
FeatureMap fm = new SimpleFeatureMapImpl();
AnnotationImpl a = new AnnotationImpl(5, node, node, "Tag", fm);
AnnotationImpl b = new AnnotationImpl(10, node, node, "Tag", fm);
assertTrue(a.compareTo(b) < 0);
}

@Test
public void testCompareToWithLowerId() {
Node node = mock(Node.class);
FeatureMap fm = new SimpleFeatureMapImpl();
AnnotationImpl a = new AnnotationImpl(10, node, node, "Tag", fm);
AnnotationImpl b = new AnnotationImpl(5, node, node, "Tag", fm);
assertTrue(a.compareTo(b) > 0);
}

@Test
public void testSetFeaturesTriggersEventsOnlyIfListenerPresent() {
Node node = mock(Node.class);
when(node.getOffset()).thenReturn(5L);
FeatureMap fm1 = new SimpleFeatureMapImpl();
fm1.put("x", "1");
FeatureMap fm2 = new SimpleFeatureMapImpl();
fm2.put("x", "2");
AnnotationImpl annotation = new AnnotationImpl(1, node, node, "T", fm1);
AnnotationListener listener = mock(AnnotationListener.class);
annotation.setFeatures(fm2);
verify(listener, never()).annotationUpdated(any());
}

@Test
public void testIsPartiallyCompatibleWithNullFeatureSetCallsFallback() {
Node s = mock(Node.class);
Node e = mock(Node.class);
when(s.getOffset()).thenReturn(10L);
when(e.getOffset()).thenReturn(30L);
FeatureMap fm1 = new SimpleFeatureMapImpl();
fm1.put("a", 1);
FeatureMap fm2 = new SimpleFeatureMapImpl();
fm2.put("a", 1);
AnnotationImpl annot1 = new AnnotationImpl(1, s, e, "Type", fm1);
AnnotationImpl annot2 = new AnnotationImpl(2, s, e, "Type", fm2);
assertTrue(annot1.isPartiallyCompatible(annot2, null));
}

@Test
public void testEqualsReturnsFalseWhenOneTypeIsNullAndOtherNot() {
Node node = mock(Node.class);
when(node.getOffset()).thenReturn(1L);
FeatureMap fm = new SimpleFeatureMapImpl();
AnnotationImpl a = new AnnotationImpl(1, node, node, null, fm);
AnnotationImpl b = new AnnotationImpl(1, node, node, "Type", fm);
assertFalse(a.equals(b));
}

@Test
public void testEqualsReturnsFalseWhenOneIdIsNullAndOtherNot() {
Node node = mock(Node.class);
when(node.getOffset()).thenReturn(1L);
FeatureMap fm = new SimpleFeatureMapImpl();
AnnotationImpl a = new AnnotationImpl(null, node, node, "X", fm);
AnnotationImpl b = new AnnotationImpl(99, node, node, "X", fm);
assertFalse(a.equals(b));
}

@Test
public void testEqualsReturnsFalseWhenFeatureMapsAreDifferentSizes() {
Node node = mock(Node.class);
when(node.getOffset()).thenReturn(1L);
FeatureMap fm1 = new SimpleFeatureMapImpl();
fm1.put("a", "1");
FeatureMap fm2 = new SimpleFeatureMapImpl();
fm2.put("a", "1");
fm2.put("b", "2");
AnnotationImpl a = new AnnotationImpl(1, node, node, "Type", fm1);
AnnotationImpl b = new AnnotationImpl(1, node, node, "Type", fm2);
assertFalse(a.equals(b));
}

@Test
public void testCoextensiveReturnsFalseIfStartOffsetDifferent() {
Node startA = mock(Node.class);
Node endA = mock(Node.class);
Node startB = mock(Node.class);
Node endB = mock(Node.class);
when(startA.getOffset()).thenReturn(10L);
when(endA.getOffset()).thenReturn(20L);
when(startB.getOffset()).thenReturn(5L);
when(endB.getOffset()).thenReturn(20L);
AnnotationImpl a = new AnnotationImpl(1, startA, endA, "T", new SimpleFeatureMapImpl());
AnnotationImpl b = new AnnotationImpl(2, startB, endB, "T", new SimpleFeatureMapImpl());
assertFalse(a.coextensive(b));
}

@Test
public void testCoextensiveReturnsFalseIfEndOffsetDifferent() {
Node startA = mock(Node.class);
Node endA = mock(Node.class);
Node startB = mock(Node.class);
Node endB = mock(Node.class);
when(startA.getOffset()).thenReturn(10L);
when(endA.getOffset()).thenReturn(20L);
when(startB.getOffset()).thenReturn(10L);
when(endB.getOffset()).thenReturn(25L);
AnnotationImpl a = new AnnotationImpl(1, startA, endA, "T", new SimpleFeatureMapImpl());
AnnotationImpl b = new AnnotationImpl(2, startB, endB, "T", new SimpleFeatureMapImpl());
assertFalse(a.coextensive(b));
}

@Test
public void testWithinSpanOfFalseWhenOuterHasNullOffsets() {
Node startA = mock(Node.class);
Node endA = mock(Node.class);
Node startB = mock(Node.class);
Node endB = mock(Node.class);
when(startA.getOffset()).thenReturn(10L);
when(endA.getOffset()).thenReturn(20L);
when(startB.getOffset()).thenReturn(null);
when(endB.getOffset()).thenReturn(null);
AnnotationImpl a = new AnnotationImpl(1, startA, endA, "x", new SimpleFeatureMapImpl());
AnnotationImpl b = new AnnotationImpl(2, startB, endB, "x", new SimpleFeatureMapImpl());
assertFalse(a.withinSpanOf(b));
}

@Test
public void testOverlapsFalseWhenInputAnnotationIsNull() {
Node s1 = mock(Node.class);
Node e1 = mock(Node.class);
when(s1.getOffset()).thenReturn(10L);
when(e1.getOffset()).thenReturn(20L);
AnnotationImpl a = new AnnotationImpl(1, s1, e1, "X", new SimpleFeatureMapImpl());
assertFalse(a.overlaps(null));
}

@Test
public void testOverlapsFalseWhenOtherStartNodeIsNull() {
Node s1 = mock(Node.class);
Node e1 = mock(Node.class);
when(s1.getOffset()).thenReturn(10L);
when(e1.getOffset()).thenReturn(20L);
Node end = mock(Node.class);
when(end.getOffset()).thenReturn(15L);
AnnotationImpl a = new AnnotationImpl(1, s1, e1, "X", new SimpleFeatureMapImpl());
AnnotationImpl b = new AnnotationImpl(2, null, end, "X", new SimpleFeatureMapImpl());
assertFalse(a.overlaps(b));
}

@Test
public void testOverlapsFalseWhenOtherEndNodeIsNull() {
Node s1 = mock(Node.class);
Node e1 = mock(Node.class);
when(s1.getOffset()).thenReturn(10L);
when(e1.getOffset()).thenReturn(20L);
Node start = mock(Node.class);
when(start.getOffset()).thenReturn(15L);
AnnotationImpl a = new AnnotationImpl(1, s1, e1, "X", new SimpleFeatureMapImpl());
AnnotationImpl b = new AnnotationImpl(2, start, null, "X", new SimpleFeatureMapImpl());
assertFalse(a.overlaps(b));
}

@Test
public void testIsCompatibleReturnsTrueWhenFeatureSubsumesWithExtraKeys() {
Node node = mock(Node.class);
when(node.getOffset()).thenReturn(1L);
FeatureMap thisMap = new SimpleFeatureMapImpl();
thisMap.put("x", "A");
FeatureMap otherMap = new SimpleFeatureMapImpl();
otherMap.put("x", "A");
otherMap.put("extra", "B");
AnnotationImpl a = new AnnotationImpl(1, node, node, "Type", thisMap);
AnnotationImpl b = new AnnotationImpl(2, node, node, "Type", otherMap);
assertTrue(a.isCompatible(b));
}

@Test
public void testIsCompatibleFalseWhenOtherIsNull() {
Node node = mock(Node.class);
when(node.getOffset()).thenReturn(1L);
FeatureMap thisMap = new SimpleFeatureMapImpl();
thisMap.put("x", "val");
AnnotationImpl a = new AnnotationImpl(1, node, node, "Type", thisMap);
assertFalse(a.isCompatible(null));
}

@Test
public void testIsCompatibleWithKeySetReturnsFalseIfSubsetMismatch() {
Node node = mock(Node.class);
when(node.getOffset()).thenReturn(1L);
FeatureMap aFeatures = new SimpleFeatureMapImpl();
aFeatures.put("x", "1");
aFeatures.put("y", "2");
FeatureMap bFeatures = new SimpleFeatureMapImpl();
bFeatures.put("x", "1");
bFeatures.put("y", "999");
AnnotationImpl a = new AnnotationImpl(1, node, node, "T", aFeatures);
AnnotationImpl b = new AnnotationImpl(2, node, node, "T", bFeatures);
Set<Object> keySet = new HashSet<Object>();
keySet.add("y");
assertFalse(a.isCompatible(b, keySet));
}

@Test
public void testSetFeaturesWithNullInitialDoesNotThrowException() {
Node node = mock(Node.class);
when(node.getOffset()).thenReturn(0L);
AnnotationImpl annotation = new AnnotationImpl(1, node, node, "X", null);
FeatureMap features = new SimpleFeatureMapImpl();
features.put("test", 123);
annotation.setFeatures(features);
assertEquals(features, annotation.getFeatures());
}

@Test
public void testAddAnnotationListenerMultipleTimesOnlyNotifiesOnce() {
Node node = mock(Node.class);
when(node.getOffset()).thenReturn(1L);
FeatureMap fm = new SimpleFeatureMapImpl();
AnnotationImpl annotation = new AnnotationImpl(1, node, node, "T", fm);
AnnotationListener listener = mock(AnnotationListener.class);
annotation.addAnnotationListener(listener);
annotation.addAnnotationListener(listener);
FeatureMap newFm = new SimpleFeatureMapImpl();
newFm.put("x", "y");
annotation.setFeatures(newFm);
verify(listener, times(1)).annotationUpdated(any());
}

@Test
public void testRemoveAnnotationListenerBeforeAdd() {
Node node = mock(Node.class);
when(node.getOffset()).thenReturn(2L);
FeatureMap fm = new SimpleFeatureMapImpl();
AnnotationImpl annotation = new AnnotationImpl(1, node, node, "T", fm);
AnnotationListener listener = mock(AnnotationListener.class);
annotation.removeAnnotationListener(listener);
fm.put("newFeature", true);
annotation.setFeatures(fm);
verify(listener, never()).annotationUpdated(any());
}
}
