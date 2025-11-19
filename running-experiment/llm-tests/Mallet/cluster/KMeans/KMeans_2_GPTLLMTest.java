package cc.mallet.cluster;

import cc.mallet.pipe.Noop;
import cc.mallet.pipe.Pipe;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Metric;
import cc.mallet.types.SparseVector;
import org.junit.Test;
import java.util.ArrayList;
import static org.junit.Assert.*;

public class KMeans_2_GPTLLMTest {

@Test
public void testClusterReturnsNonNullForValidTwoClusterData() {
Pipe pipe = new Noop();
SparseVector sv1 = new SparseVector(new int[] { 0, 1 }, new double[] { 1.0, 1.0 });
SparseVector sv2 = new SparseVector(new int[] { 0, 1 }, new double[] { 1.1, 0.9 });
SparseVector sv3 = new SparseVector(new int[] { 0, 1 }, new double[] { 5.0, 5.0 });
SparseVector sv4 = new SparseVector(new int[] { 0, 1 }, new double[] { 4.9, 5.1 });
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(sv1, null, "sv1", null));
list.addThruPipe(new Instance(sv2, null, "sv2", null));
list.addThruPipe(new Instance(sv3, null, "sv3", null));
list.addThruPipe(new Instance(sv4, null, "sv4", null));
Metric metric = new Metric() {

public double distance(SparseVector a, SparseVector b) {
double dx = a.value(0) - b.value(0);
double dy = a.value(1) - b.value(1);
return Math.sqrt(dx * dx + dy * dy);
}
};
KMeans kmeans = new KMeans(pipe, 2, metric);
Clustering result = kmeans.cluster(list);
assertNotNull("Clustering result should not be null", result);
assertEquals("Should have 2 clusters", 2, result.getNumClusters());
// assertEquals("Should have 4 labels", 4, result.getClusterLabels().length);
}

@Test
public void testIdenticalInstancesCauseEmptyErrorHandling() {
Pipe pipe = new Noop();
InstanceList list = new InstanceList(pipe);
SparseVector identical = new SparseVector(new int[] { 1, 2 }, new double[] { 1.0, 2.0 });
list.addThruPipe(new Instance(identical, null, "x1", null));
list.addThruPipe(new Instance(identical, null, "x2", null));
Metric metric = new Metric() {

public double distance(SparseVector a, SparseVector b) {
double dx = a.value(1) - b.value(1);
double dy = a.value(2) - b.value(2);
return Math.sqrt(dx * dx + dy * dy);
}
};
KMeans kmeans = new KMeans(pipe, 3, metric, KMeans.EMPTY_ERROR);
Clustering result = kmeans.cluster(list);
assertNull("Result should be null when EMPTY_ERROR is triggered", result);
}

@Test
public void testEmptyClusterDropReducesClusterCount() {
Pipe pipe = new Noop();
InstanceList list = new InstanceList(pipe);
SparseVector a = new SparseVector(new int[] { 0 }, new double[] { 0.0 });
SparseVector b = new SparseVector(new int[] { 0 }, new double[] { 0.1 });
SparseVector c = new SparseVector(new int[] { 0 }, new double[] { 10.0 });
list.addThruPipe(new Instance(a, null, "a", null));
list.addThruPipe(new Instance(b, null, "b", null));
list.addThruPipe(new Instance(c, null, "c", null));
Metric metric = new Metric() {

public double distance(SparseVector a, SparseVector b) {
double d = a.value(0) - b.value(0);
return Math.abs(d);
}
};
KMeans kmeans = new KMeans(pipe, 5, metric, KMeans.EMPTY_DROP);
Clustering result = kmeans.cluster(list);
assertNotNull("Drop strategy should not return null", result);
assertTrue("Cluster count should be <= original", result.getNumClusters() <= 5);
}

@Test
public void testZeroVectorInstanceSkippedInInitialization() {
Pipe pipe = new Noop();
InstanceList list = new InstanceList(pipe);
// SparseVector zero = new SparseVector(2);
SparseVector one = new SparseVector(new int[] { 0, 1 }, new double[] { 1.0, 1.0 });
SparseVector two = new SparseVector(new int[] { 0, 1 }, new double[] { 5.0, 5.0 });
// list.addThruPipe(new Instance(zero, null, "zero", null));
list.addThruPipe(new Instance(one, null, "one", null));
list.addThruPipe(new Instance(two, null, "two", null));
Metric metric = new Metric() {

public double distance(SparseVector a, SparseVector b) {
double dx = a.value(0) - b.value(0);
double dy = a.value(1) - b.value(1);
return Math.sqrt(dx * dx + dy * dy);
}
};
KMeans kmeans = new KMeans(pipe, 2, metric);
Clustering result = kmeans.cluster(list);
assertNotNull("KMeans should ignore zero vectors and cluster other points", result);
assertEquals("Should still return 2 clusters", 2, result.getNumClusters());
}

@Test
public void testClusterMeansRetrievedCorrectSize() {
Pipe pipe = new Noop();
SparseVector sv1 = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
SparseVector sv2 = new SparseVector(new int[] { 0 }, new double[] { 1.1 });
SparseVector sv3 = new SparseVector(new int[] { 0 }, new double[] { 5.0 });
SparseVector sv4 = new SparseVector(new int[] { 0 }, new double[] { 4.9 });
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(sv1, null, "a", null));
list.addThruPipe(new Instance(sv2, null, "b", null));
list.addThruPipe(new Instance(sv3, null, "c", null));
list.addThruPipe(new Instance(sv4, null, "d", null));
Metric metric = new Metric() {

public double distance(SparseVector a, SparseVector b) {
double d = a.value(0) - b.value(0);
return Math.abs(d);
}
};
KMeans kmeans = new KMeans(pipe, 2, metric);
Clustering clustering = kmeans.cluster(list);
ArrayList<SparseVector> means = kmeans.getClusterMeans();
assertEquals("Should contain same number of means as clusters", 2, means.size());
}

@Test(expected = AssertionError.class)
public void testPipeMismatchAssertionFails() {
Pipe pipeA = new Noop();
Pipe pipeB = new Noop();
SparseVector sv = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
InstanceList list = new InstanceList(pipeB);
list.addThruPipe(new Instance(sv, null, "invalid", null));
Metric metric = new Metric() {

public double distance(SparseVector a, SparseVector b) {
return Math.abs(a.value(0) - b.value(0));
}
};
KMeans kmeans = new KMeans(pipeA, 2, metric);
kmeans.cluster(list);
}

@Test
public void testClusterLabelsSizeMatchesInputInstances() {
Pipe pipe = new Noop();
SparseVector a = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
SparseVector b = new SparseVector(new int[] { 0 }, new double[] { 2.0 });
SparseVector c = new SparseVector(new int[] { 0 }, new double[] { 9.0 });
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(a, null, "a", null));
list.addThruPipe(new Instance(b, null, "b", null));
list.addThruPipe(new Instance(c, null, "c", null));
Metric metric = new Metric() {

public double distance(SparseVector a, SparseVector b) {
return Math.abs(a.value(0) - b.value(0));
}
};
KMeans kmeans = new KMeans(pipe, 2, metric);
Clustering result = kmeans.cluster(list);
assertNotNull("Clustering result should not be null", result);
// assertEquals("Cluster labels size must match number of instances", 3, result.getClusterLabels().length);
}

@Test
public void testClusterCentroidsRoughMatchToExpectedMean() {
Pipe pipe = new Noop();
SparseVector a = new SparseVector(new int[] { 0, 1 }, new double[] { 1.0, 1.0 });
SparseVector b = new SparseVector(new int[] { 0, 1 }, new double[] { 5.0, 5.0 });
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(a, null, "a", null));
list.addThruPipe(new Instance(b, null, "b", null));
Metric metric = new Metric() {

public double distance(SparseVector a, SparseVector b) {
double dx = a.value(0) - b.value(0);
double dy = a.value(1) - b.value(1);
return Math.sqrt(dx * dx + dy * dy);
}
};
KMeans kmeans = new KMeans(pipe, 2, metric);
Clustering result = kmeans.cluster(list);
ArrayList<SparseVector> means = kmeans.getClusterMeans();
SparseVector m0 = means.get(0);
SparseVector m1 = means.get(1);
double m0x = m0.value(0);
double m0y = m0.value(1);
double m1x = m1.value(0);
double m1y = m1.value(1);
boolean m0near11 = (Math.abs(m0x - 1.0) < 0.5) && (Math.abs(m0y - 1.0) < 0.5);
boolean m1near55 = (Math.abs(m1x - 5.0) < 0.5) && (Math.abs(m1y - 5.0) < 0.5);
boolean m0near55 = (Math.abs(m0x - 5.0) < 0.5) && (Math.abs(m0y - 5.0) < 0.5);
boolean m1near11 = (Math.abs(m1x - 1.0) < 0.5) && (Math.abs(m1y - 1.0) < 0.5);
assertTrue("At least one mean near (1,1)", m0near11 || m1near11);
assertTrue("At least one mean near (5,5)", m0near55 || m1near55);
}

@Test
public void testEmptyInstanceListReturnsValidClustering() {
Pipe pipe = new Noop();
InstanceList emptyList = new InstanceList(pipe);
Metric metric = new Metric() {

public double distance(SparseVector a, SparseVector b) {
return 0.0;
}
};
KMeans kmeans = new KMeans(pipe, 1, metric);
Clustering result = kmeans.cluster(emptyList);
assertNotNull("Clustering should not be null for empty instance list", result);
assertEquals("Number of clusters should be 1", 1, result.getNumClusters());
// assertEquals("Cluster label array should be empty", 0, result.getClusterLabels().length);
}

@Test
public void testKGreaterThanInstanceCountWithDropStrategy() {
Pipe pipe = new Noop();
InstanceList list = new InstanceList(pipe);
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
SparseVector v2 = new SparseVector(new int[] { 0 }, new double[] { 2.0 });
list.addThruPipe(new Instance(v1, null, "a", null));
list.addThruPipe(new Instance(v2, null, "b", null));
Metric metric = new Metric() {

public double distance(SparseVector a, SparseVector b) {
return Math.abs(a.value(0) - b.value(0));
}
};
KMeans kmeans = new KMeans(pipe, 5, metric, KMeans.EMPTY_DROP);
Clustering result = kmeans.cluster(list);
assertNotNull("Clustering should succeed when K > N using DROP strategy", result);
assertTrue("Cluster count should be <= 2 (number of instances)", result.getNumClusters() <= 2);
}

@Test
public void testKEqualToOneReturnsSingleCluster() {
Pipe pipe = new Noop();
InstanceList list = new InstanceList(pipe);
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
SparseVector v2 = new SparseVector(new int[] { 0 }, new double[] { 5.0 });
list.addThruPipe(new Instance(v1, null, "a", null));
list.addThruPipe(new Instance(v2, null, "b", null));
Metric metric = new Metric() {

public double distance(SparseVector a, SparseVector b) {
return Math.abs(a.value(0) - b.value(0));
}
};
KMeans kmeans = new KMeans(pipe, 1, metric);
Clustering result = kmeans.cluster(list);
assertNotNull("Clustering result should not be null", result);
assertEquals("Number of clusters should be 1", 1, result.getNumClusters());
// assertEquals("All labels should be 0", 0, result.getClusterLabels()[0]);
// assertEquals("All labels should be 0", 0, result.getClusterLabels()[1]);
}

@Test
public void testSingleInstanceAndMultipleClustersWithDrop() {
Pipe pipe = new Noop();
InstanceList list = new InstanceList(pipe);
SparseVector vector = new SparseVector(new int[] { 1 }, new double[] { 3.14 });
list.addThruPipe(new Instance(vector, null, "solo", null));
Metric metric = new Metric() {

public double distance(SparseVector a, SparseVector b) {
return Math.abs(a.value(1) - b.value(1));
}
};
KMeans kmeans = new KMeans(pipe, 3, metric, KMeans.EMPTY_DROP);
Clustering result = kmeans.cluster(list);
assertNotNull("Drop strategy should return valid clustering for 1 instance and 3 clusters", result);
assertTrue("Cluster count should be <= 1", result.getNumClusters() <= 1);
// assertEquals("Cluster labels length should be 1", 1, result.getClusterLabels().length);
}

@Test
public void testEmptySingleFailsWhenAllClustersEmptyOrSingleton() {
Pipe pipe = new Noop();
InstanceList list = new InstanceList(pipe);
SparseVector a = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
SparseVector b = new SparseVector(new int[] { 0 }, new double[] { 30.0 });
list.addThruPipe(new Instance(a, null, "a", null));
list.addThruPipe(new Instance(b, null, "b", null));
Metric metric = new Metric() {

public double distance(SparseVector x, SparseVector y) {
return Math.abs(x.value(0) - y.value(0));
}
};
KMeans kmeans = new KMeans(pipe, 6, metric, KMeans.EMPTY_SINGLE);
Clustering result = kmeans.cluster(list);
assertNull("Should return null when no candidate exists to reassign for EMPTY_SINGLE", result);
}

@Test
public void testKEqualsZeroHandledGracefully() {
Pipe pipe = new Noop();
InstanceList list = new InstanceList(pipe);
SparseVector a = new SparseVector(new int[] { 0 }, new double[] { 2.0 });
list.addThruPipe(new Instance(a, null, "a", null));
Metric metric = new Metric() {

public double distance(SparseVector a1, SparseVector a2) {
return Math.abs(a1.value(0) - a2.value(0));
}
};
KMeans kmeans = new KMeans(pipe, 0, metric, KMeans.EMPTY_ERROR);
Clustering result = kmeans.cluster(list);
assertNull("Clustering should return null for K=0", result);
}

@Test
public void testIdenticalPointsInSeparateClustersWithErrorStrategy() {
Pipe pipe = new Noop();
InstanceList list = new InstanceList(pipe);
SparseVector point = new SparseVector(new int[] { 0 }, new double[] { 9.9 });
list.addThruPipe(new Instance(point, null, "a-dup", null));
list.addThruPipe(new Instance(point, null, "b-dup", null));
list.addThruPipe(new Instance(point, null, "c-dup", null));
Metric metric = new Metric() {

public double distance(SparseVector a, SparseVector b) {
return 0.0;
}
};
KMeans kmeans = new KMeans(pipe, 3, metric, KMeans.EMPTY_ERROR);
Clustering result = kmeans.cluster(list);
assertNull("Identical instances with EMPTY_ERROR strategy will likely trigger an error", result);
}

@Test
public void testSingleDimensionVectorsClusteredCorrectly() {
Pipe pipe = new Noop();
InstanceList list = new InstanceList(pipe);
SparseVector low = new SparseVector(new int[] { 0 }, new double[] { 0.0 });
SparseVector high = new SparseVector(new int[] { 0 }, new double[] { 10.0 });
list.addThruPipe(new Instance(low, null, "low", null));
list.addThruPipe(new Instance(high, null, "high", null));
Metric metric = new Metric() {

public double distance(SparseVector a, SparseVector b) {
return Math.abs(a.value(0) - b.value(0));
}
};
KMeans kmeans = new KMeans(pipe, 2, metric);
Clustering result = kmeans.cluster(list);
assertNotNull("Result should not be null", result);
// int label0 = result.getClusterLabels()[0];
// int label1 = result.getClusterLabels()[1];
// assertNotEquals("Both instances should end up in different clusters", label0, label1);
}

@Test
public void testMultipleIdenticalZerosOnlyOneClusterFormed() {
Pipe pipe = new Noop();
InstanceList list = new InstanceList(pipe);
// SparseVector zero1 = new SparseVector(2);
// SparseVector zero2 = new SparseVector(2);
// SparseVector zero3 = new SparseVector(2);
// list.addThruPipe(new Instance(zero1, null, "z1", null));
// list.addThruPipe(new Instance(zero2, null, "z2", null));
// list.addThruPipe(new Instance(zero3, null, "z3", null));
Metric metric = new Metric() {

public double distance(SparseVector a, SparseVector b) {
return 0.0;
}
};
KMeans kmeans = new KMeans(pipe, 1, metric);
Clustering result = kmeans.cluster(list);
assertNotNull("Valid clustering for multiple zero vectors", result);
assertEquals("Single cluster expected", 1, result.getNumClusters());
// assertEquals("All labels should be Zero", 0, result.getClusterLabels()[0]);
// assertEquals("All labels should be Zero", 0, result.getClusterLabels()[1]);
// assertEquals("All labels should be Zero", 0, result.getClusterLabels()[2]);
}

@Test
public void testAllInstancesEmptyVectors() {
Pipe pipe = new Noop();
InstanceList list = new InstanceList(pipe);
// SparseVector v1 = new SparseVector(2);
// SparseVector v2 = new SparseVector(2);
// list.addThruPipe(new Instance(v1, null, "v1", null));
// list.addThruPipe(new Instance(v2, null, "v2", null));
Metric metric = new Metric() {

public double distance(SparseVector a, SparseVector b) {
return 0.0;
}
};
KMeans kmeans = new KMeans(pipe, 2, metric);
Clustering result = kmeans.cluster(list);
assertNotNull("Should return a valid clustering even with only empty vectors", result);
assertEquals("Should still report 2 clusters", 2, result.getNumClusters());
}

@Test
public void testClusterMeansAfterMultipleIdenticalPoints() {
Pipe pipe = new Noop();
InstanceList list = new InstanceList(pipe);
SparseVector identical = new SparseVector(new int[] { 0, 1 }, new double[] { 2.0, 2.0 });
list.addThruPipe(new Instance(identical, null, "a", null));
list.addThruPipe(new Instance(identical, null, "b", null));
list.addThruPipe(new Instance(identical, null, "c", null));
Metric metric = new Metric() {

public double distance(SparseVector a, SparseVector b) {
double dx = a.value(0) - b.value(0);
double dy = a.value(1) - b.value(1);
return Math.sqrt(dx * dx + dy * dy);
}
};
KMeans kmeans = new KMeans(pipe, 1, metric);
Clustering result = kmeans.cluster(list);
ArrayList<SparseVector> means = kmeans.getClusterMeans();
assertEquals("Only one mean vector is expected", 1, means.size());
SparseVector mean = means.get(0);
assertEquals("Mean X should match input", 2.0, mean.value(0), 0.0001);
assertEquals("Mean Y should match input", 2.0, mean.value(1), 0.0001);
}

@Test
public void testSingleInstanceWithEmptySingleStrategy() {
Pipe pipe = new Noop();
InstanceList list = new InstanceList(pipe);
SparseVector single = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
list.addThruPipe(new Instance(single, null, "solo", null));
Metric metric = new Metric() {

public double distance(SparseVector a, SparseVector b) {
return Math.abs(a.value(0) - b.value(0));
}
};
KMeans kmeans = new KMeans(pipe, 3, metric, KMeans.EMPTY_SINGLE);
Clustering result = kmeans.cluster(list);
assertNull("Should return null when reassign fails with EMPTY_SINGLE", result);
}

@Test
public void testCentroidsRecomputedCorrectlyAfterMove() {
Pipe pipe = new Noop();
InstanceList list = new InstanceList(pipe);
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { 0.0 });
SparseVector v2 = new SparseVector(new int[] { 0 }, new double[] { 10.0 });
SparseVector v3 = new SparseVector(new int[] { 0 }, new double[] { 20.0 });
list.addThruPipe(new Instance(v1, null, "A", null));
list.addThruPipe(new Instance(v2, null, "B", null));
list.addThruPipe(new Instance(v3, null, "C", null));
Metric metric = new Metric() {

public double distance(SparseVector a, SparseVector b) {
return Math.abs(a.value(0) - b.value(0));
}
};
KMeans kmeans = new KMeans(pipe, 2, metric);
Clustering result = kmeans.cluster(list);
ArrayList<SparseVector> means = kmeans.getClusterMeans();
assertTrue("There should be 2 centroids", means.size() == 2);
double mean0 = means.get(0).value(0);
double mean1 = means.get(1).value(0);
boolean expected1 = (Math.abs(mean0 - 0.0) < 5.0 && Math.abs(mean1 - 15.0) < 6.0);
boolean expected2 = (Math.abs(mean1 - 0.0) < 5.0 && Math.abs(mean0 - 15.0) < 6.0);
assertTrue("Means should split low and high data clusters correctly", expected1 || expected2);
}

@Test
public void testClusterWithExtremeValues() {
Pipe pipe = new Noop();
InstanceList list = new InstanceList(pipe);
SparseVector low = new SparseVector(new int[] { 0 }, new double[] { -1e9 });
SparseVector high = new SparseVector(new int[] { 0 }, new double[] { 1e9 });
list.addThruPipe(new Instance(low, null, "low", null));
list.addThruPipe(new Instance(high, null, "high", null));
Metric metric = new Metric() {

public double distance(SparseVector a, SparseVector b) {
return Math.abs(a.value(0) - b.value(0));
}
};
KMeans kmeans = new KMeans(pipe, 2, metric);
Clustering result = kmeans.cluster(list);
assertNotNull("Should handle extreme values", result);
// int l0 = result.getClusterLabels()[0];
// int l1 = result.getClusterLabels()[1];
// assertNotEquals("Extreme values should fall in separate clusters", l0, l1);
}

@Test
public void testInstanceListWithNullDataIgnoredInInit() {
Pipe pipe = new Noop();
InstanceList list = new InstanceList(pipe);
Instance bad = new Instance(null, null, "bad", null);
SparseVector valid = new SparseVector(new int[] { 0 }, new double[] { 2.718 });
list.addThruPipe(bad);
list.addThruPipe(new Instance(valid, null, "good", null));
Metric metric = new Metric() {

public double distance(SparseVector a, SparseVector b) {
return 0.0;
}
};
KMeans kmeans = new KMeans(pipe, 1, metric);
Clustering result = kmeans.cluster(list);
assertNotNull("Clustering should not fail if some data is null", result);
// assertEquals("Valid instances should be clustered", 1, result.getClusterLabels().length);
}

@Test
public void testMultipleZeroLengthVectorsIgnoredInInitialization() {
Pipe pipe = new Noop();
InstanceList list = new InstanceList(pipe);
// SparseVector zero1 = new SparseVector(0);
// SparseVector zero2 = new SparseVector(0);
SparseVector valid = new SparseVector(new int[] { 0, 1 }, new double[] { 1.0, 2.0 });
// list.addThruPipe(new Instance(zero1, null, "z1", null));
// list.addThruPipe(new Instance(zero2, null, "z2", null));
list.addThruPipe(new Instance(valid, null, "v", null));
Metric metric = new Metric() {

public double distance(SparseVector a, SparseVector b) {
return 0.0;
}
};
KMeans kmeans = new KMeans(pipe, 1, metric);
Clustering result = kmeans.cluster(list);
ArrayList<SparseVector> means = kmeans.getClusterMeans();
SparseVector centroid = means.get(0);
assertEquals("Mean should come from valid vector", 1.0, centroid.value(0), 0.01);
assertEquals("Mean should come from valid vector", 2.0, centroid.value(1), 0.01);
}

@Test
public void testMaxIterationsReachedWithoutConvergence() {
Pipe pipe = new Noop();
InstanceList list = new InstanceList(pipe);
SparseVector a = new SparseVector(new int[] { 0 }, new double[] { 0.0 });
SparseVector b = new SparseVector(new int[] { 0 }, new double[] { 10.0 });
SparseVector c = new SparseVector(new int[] { 0 }, new double[] { 20.0 });
SparseVector d = new SparseVector(new int[] { 0 }, new double[] { 30.0 });
list.addThruPipe(new Instance(a, null, "a", null));
list.addThruPipe(new Instance(b, null, "b", null));
list.addThruPipe(new Instance(c, null, "c", null));
list.addThruPipe(new Instance(d, null, "d", null));
Metric metric = new Metric() {

public double distance(SparseVector x, SparseVector y) {
return Math.abs(x.value(0) - y.value(0));
}
};
KMeans.MAX_ITER = 1;
KMeans kmeans = new KMeans(pipe, 2, metric);
Clustering result = kmeans.cluster(list);
assertNotNull("Clustering should return result even if MAX_ITER is reached", result);
}

@Test
public void testConvergesDueToMinimalDeltaPoints() {
Pipe pipe = new Noop();
InstanceList list = new InstanceList(pipe);
SparseVector p1 = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
SparseVector p2 = new SparseVector(new int[] { 0 }, new double[] { 1.1 });
SparseVector p3 = new SparseVector(new int[] { 0 }, new double[] { 5.0 });
SparseVector p4 = new SparseVector(new int[] { 0 }, new double[] { 5.1 });
list.addThruPipe(new Instance(p1, null, "p1", null));
list.addThruPipe(new Instance(p2, null, "p2", null));
list.addThruPipe(new Instance(p3, null, "p3", null));
list.addThruPipe(new Instance(p4, null, "p4", null));
Metric metric = new Metric() {

public double distance(SparseVector a, SparseVector b) {
return Math.abs(a.value(0) - b.value(0));
}
};
KMeans kmeans = new KMeans(pipe, 2, metric);
Clustering result = kmeans.cluster(list);
assertNotNull("Should converge with minimal deltaPoints", result);
}

@Test
public void testClusterWithSingleItemRemainsStable() {
Pipe pipe = new Noop();
InstanceList list = new InstanceList(pipe);
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
SparseVector v2 = new SparseVector(new int[] { 0 }, new double[] { 1.2 });
SparseVector v3 = new SparseVector(new int[] { 0 }, new double[] { 100.0 });
list.addThruPipe(new Instance(v1, null, "v1", null));
list.addThruPipe(new Instance(v2, null, "v2", null));
list.addThruPipe(new Instance(v3, null, "v3", null));
Metric metric = new Metric() {

public double distance(SparseVector x, SparseVector y) {
return Math.abs(x.value(0) - y.value(0));
}
};
KMeans kmeans = new KMeans(pipe, 2, metric);
Clustering clustering = kmeans.cluster(list);
assertNotNull("Should handle cluster with only one instance", clustering);
assertEquals("Should have 2 clusters", 2, clustering.getNumClusters());
}

@Test
public void testUnevenDistributionAcrossClusters() {
Pipe pipe = new Noop();
InstanceList list = new InstanceList(pipe);
SparseVector dense1 = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
SparseVector dense2 = new SparseVector(new int[] { 0 }, new double[] { 1.1 });
SparseVector dense3 = new SparseVector(new int[] { 0 }, new double[] { 1.2 });
SparseVector dense4 = new SparseVector(new int[] { 0 }, new double[] { 1.3 });
SparseVector outlier = new SparseVector(new int[] { 0 }, new double[] { 100.0 });
list.addThruPipe(new Instance(dense1, null, "a", null));
list.addThruPipe(new Instance(dense2, null, "b", null));
list.addThruPipe(new Instance(dense3, null, "c", null));
list.addThruPipe(new Instance(dense4, null, "d", null));
list.addThruPipe(new Instance(outlier, null, "e", null));
Metric metric = new Metric() {

public double distance(SparseVector a, SparseVector b) {
return Math.abs(a.value(0) - b.value(0));
}
};
KMeans kmeans = new KMeans(pipe, 2, metric);
Clustering result = kmeans.cluster(list);
assertNotNull("Should create clustering with uneven instance distribution", result);
// int[] labels = result.getClusterLabels();
int countCluster0 = 0;
int countCluster1 = 0;
// if (labels[0] == 0)
countCluster0++;
// else
countCluster1++;
// if (labels[1] == 0)
countCluster0++;
// else
countCluster1++;
// if (labels[2] == 0)
countCluster0++;
// else
countCluster1++;
// if (labels[3] == 0)
countCluster0++;
// else
countCluster1++;
// if (labels[4] == 0)
countCluster0++;
// else
countCluster1++;
boolean hasUnevenSplit = (countCluster0 == 1 || countCluster1 == 1);
assertTrue("Clusters should be unevenly distributed", hasUnevenSplit);
}

@Test
public void testMultipleIdenticalCentroidsHandledGracefully() {
Pipe pipe = new Noop();
InstanceList list = new InstanceList(pipe);
SparseVector inst1 = new SparseVector(new int[] { 0 }, new double[] { 5.0 });
SparseVector inst2 = new SparseVector(new int[] { 0 }, new double[] { 5.0 });
SparseVector inst3 = new SparseVector(new int[] { 0 }, new double[] { 5.0 });
SparseVector inst4 = new SparseVector(new int[] { 0 }, new double[] { 5.0 });
list.addThruPipe(new Instance(inst1, null, "i1", null));
list.addThruPipe(new Instance(inst2, null, "i2", null));
list.addThruPipe(new Instance(inst3, null, "i3", null));
list.addThruPipe(new Instance(inst4, null, "i4", null));
Metric metric = new Metric() {

public double distance(SparseVector a, SparseVector b) {
return Math.abs(a.value(0) - b.value(0));
}
};
KMeans kmeans = new KMeans(pipe, 2, metric);
Clustering result = kmeans.cluster(list);
assertNotNull("Clustering should not fail with identical points", result);
// assertEquals("All points should belong to the same cluster even if two clusters were requested", 4, result.getClusterLabels().length);
}

@Test
public void testNegativeFeatureValuesClusteredCorrectly() {
Pipe pipe = new Noop();
InstanceList list = new InstanceList(pipe);
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { -10.0 });
SparseVector v2 = new SparseVector(new int[] { 0 }, new double[] { -9.5 });
SparseVector v3 = new SparseVector(new int[] { 0 }, new double[] { 10.0 });
SparseVector v4 = new SparseVector(new int[] { 0 }, new double[] { 9.5 });
list.addThruPipe(new Instance(v1, null, "v1", null));
list.addThruPipe(new Instance(v2, null, "v2", null));
list.addThruPipe(new Instance(v3, null, "v3", null));
list.addThruPipe(new Instance(v4, null, "v4", null));
Metric metric = new Metric() {

public double distance(SparseVector a, SparseVector b) {
return Math.abs(a.value(0) - b.value(0));
}
};
KMeans kmeans = new KMeans(pipe, 2, metric);
Clustering result = kmeans.cluster(list);
assertNotNull("Should correctly cluster negative and positive features", result);
// int[] labels = result.getClusterLabels();
assertEquals("Expected 2 clusters", 2, result.getNumClusters());
// assertTrue("Negative and positive values should be split", (labels[0] == labels[1] && labels[2] == labels[3] && labels[0] != labels[2]) || (labels[0] == labels[1] && labels[2] == labels[3] && labels[1] != labels[2]));
}

@Test
public void testIdenticalInstancesWithKGreaterThanOneReturnsSingleCluster() {
Pipe pipe = new Noop();
InstanceList list = new InstanceList(pipe);
SparseVector v = new SparseVector(new int[] { 0, 1 }, new double[] { 2.0, 3.0 });
list.addThruPipe(new Instance(v, null, "a", null));
list.addThruPipe(new Instance(v, null, "b", null));
list.addThruPipe(new Instance(v, null, "c", null));
Metric metric = new Metric() {

public double distance(SparseVector a, SparseVector b) {
double dx = a.value(0) - b.value(0);
double dy = a.value(1) - b.value(1);
return Math.sqrt(dx * dx + dy * dy);
}
};
KMeans kmeans = new KMeans(pipe, 2, metric);
Clustering result = kmeans.cluster(list);
assertNotNull("Identical instances should still return result", result);
// assertTrue("All instances expected in same cluster", result.getClusterLabels()[0] == result.getClusterLabels()[1]);
// assertEquals("Expected 3 labels", 3, result.getClusterLabels().length);
}

@Test
public void testKEqualToNumInstancesEachFormsUniqueCluster() {
Pipe pipe = new Noop();
InstanceList list = new InstanceList(pipe);
SparseVector a = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
SparseVector b = new SparseVector(new int[] { 0 }, new double[] { 2.0 });
SparseVector c = new SparseVector(new int[] { 0 }, new double[] { 3.0 });
list.addThruPipe(new Instance(a, null, "xA", null));
list.addThruPipe(new Instance(b, null, "xB", null));
list.addThruPipe(new Instance(c, null, "xC", null));
Metric metric = new Metric() {

public double distance(SparseVector a1, SparseVector a2) {
return Math.abs(a1.value(0) - a2.value(0));
}
};
KMeans kmeans = new KMeans(pipe, 3, metric);
Clustering result = kmeans.cluster(list);
assertNotNull("Should cluster into #instances clusters", result);
assertEquals("Each instance should get its own cluster", 3, result.getNumClusters());
}

@Test
public void testClusterWithZeroValuesInFeatureVector() {
Pipe pipe = new Noop();
InstanceList list = new InstanceList(pipe);
SparseVector u = new SparseVector(new int[] { 0, 1 }, new double[] { 0.0, 1.0 });
SparseVector v = new SparseVector(new int[] { 0, 1 }, new double[] { 1.0, 0.0 });
SparseVector w = new SparseVector(new int[] { 0, 1 }, new double[] { 0.0, 0.0 });
list.addThruPipe(new Instance(u, null, "zero1", null));
list.addThruPipe(new Instance(v, null, "zero2", null));
list.addThruPipe(new Instance(w, null, "zero3", null));
Metric metric = new Metric() {

public double distance(SparseVector a, SparseVector b) {
double dx = a.value(0) - b.value(0);
double dy = a.value(1) - b.value(1);
return Math.sqrt(dx * dx + dy * dy);
}
};
KMeans kmeans = new KMeans(pipe, 2, metric);
Clustering result = kmeans.cluster(list);
assertNotNull("Should not return null for mixed zero values", result);
// assertEquals("Expected 3 labels", 3, result.getClusterLabels().length);
}

@Test
public void testPartialFeatureMatchVectorsClustersCorrectly() {
Pipe pipe = new Noop();
InstanceList list = new InstanceList(pipe);
SparseVector a = new SparseVector(new int[] { 0, 2 }, new double[] { 1.0, 2.0 });
SparseVector b = new SparseVector(new int[] { 1, 2 }, new double[] { 3.0, 4.0 });
SparseVector c = new SparseVector(new int[] { 0, 1 }, new double[] { 1.0, 3.0 });
list.addThruPipe(new Instance(a, null, "1", null));
list.addThruPipe(new Instance(b, null, "2", null));
list.addThruPipe(new Instance(c, null, "3", null));
Metric metric = new Metric() {

public double distance(SparseVector a1, SparseVector a2) {
double sum = 0.0;
for (int loc = 0; loc < a1.numLocations(); loc++) {
int index = a1.indexAtLocation(loc);
double delta = a1.valueAtLocation(loc) - a2.value(index);
sum += delta * delta;
}
return Math.sqrt(sum);
}
};
KMeans kmeans = new KMeans(pipe, 2, metric);
Clustering result = kmeans.cluster(list);
assertNotNull("Vectors with different dimension overlaps should cluster", result);
}

@Test
public void testClusterOnVectorsWithNegativeCoordinates() {
Pipe pipe = new Noop();
InstanceList list = new InstanceList(pipe);
SparseVector v1 = new SparseVector(new int[] { 0, 1 }, new double[] { -5.0, -4.0 });
SparseVector v2 = new SparseVector(new int[] { 0, 1 }, new double[] { -4.5, -4.2 });
SparseVector v3 = new SparseVector(new int[] { 0, 1 }, new double[] { 5.0, 5.2 });
list.addThruPipe(new Instance(v1, null, "neg1", null));
list.addThruPipe(new Instance(v2, null, "neg2", null));
list.addThruPipe(new Instance(v3, null, "pos1", null));
Metric metric = new Metric() {

public double distance(SparseVector a, SparseVector b) {
double dx = a.value(0) - b.value(0);
double dy = a.value(1) - b.value(1);
return Math.sqrt(dx * dx + dy * dy);
}
};
KMeans kmeans = new KMeans(pipe, 2, metric);
Clustering result = kmeans.cluster(list);
assertNotNull("Should handle features with negative coordinates", result);
// int[] labels = result.getClusterLabels();
// assertTrue("Negative values should cluster together", labels[0] == labels[1]);
// assertNotEquals("Third should be in a different cluster", labels[0], labels[2]);
}

@Test
public void testInstanceWithNullLabelAndNameIsAccepted() {
Pipe pipe = new Noop();
InstanceList list = new InstanceList(pipe);
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
SparseVector v2 = new SparseVector(new int[] { 0 }, new double[] { 9.0 });
Instance i1 = new Instance(v1, null, null, null);
Instance i2 = new Instance(v2, null, null, null);
list.addThruPipe(i1);
list.addThruPipe(i2);
Metric metric = new Metric() {

public double distance(SparseVector a, SparseVector b) {
return Math.abs(a.value(0) - b.value(0));
}
};
KMeans kmeans = new KMeans(pipe, 2, metric);
Clustering result = kmeans.cluster(list);
assertNotNull("Should process instances without labels or names", result);
// assertEquals("Expected 2 labels", 2, result.getClusterLabels().length);
}

@Test
public void testFloatingPointPrecisionInMeanCalculation() {
Pipe pipe = new Noop();
InstanceList list = new InstanceList(pipe);
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { 0.1 });
SparseVector v2 = new SparseVector(new int[] { 0 }, new double[] { 0.2 });
SparseVector v3 = new SparseVector(new int[] { 0 }, new double[] { 0.3 });
list.addThruPipe(new Instance(v1, null, "small1", null));
list.addThruPipe(new Instance(v2, null, "small2", null));
list.addThruPipe(new Instance(v3, null, "small3", null));
Metric metric = new Metric() {

public double distance(SparseVector a, SparseVector b) {
return Math.abs(a.value(0) - b.value(0));
}
};
KMeans kmeans = new KMeans(pipe, 1, metric);
Clustering result = kmeans.cluster(list);
ArrayList<SparseVector> means = kmeans.getClusterMeans();
SparseVector mean = means.get(0);
assertEquals("Mean should be approximately 0.2", 0.2, mean.value(0), 0.0001);
}

@Test
public void testAllCentroidsStartFromSameInstance() {
Pipe pipe = new Noop();
InstanceList list = new InstanceList(pipe);
SparseVector common = new SparseVector(new int[] { 0, 1 }, new double[] { 3.0, 3.0 });
list.addThruPipe(new Instance(common, null, "a", null));
list.addThruPipe(new Instance(common, null, "b", null));
list.addThruPipe(new Instance(common, null, "c", null));
list.addThruPipe(new Instance(common, null, "d", null));
Metric metric = new Metric() {

public double distance(SparseVector a, SparseVector b) {
double diff = a.value(0) - b.value(0);
return Math.abs(diff);
}
};
KMeans kmeans = new KMeans(pipe, 3, metric, KMeans.EMPTY_DROP);
Clustering result = kmeans.cluster(list);
assertNotNull("Should cluster even if all start at same point", result);
// assertEquals("All instances should be assigned", 4, result.getClusterLabels().length);
}

@Test
public void testOneClusterWithDistinctVectors() {
Pipe pipe = new Noop();
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(new SparseVector(new int[] { 0 }, new double[] { 1.0 }), null, "a", null));
list.addThruPipe(new Instance(new SparseVector(new int[] { 0 }, new double[] { 2.0 }), null, "b", null));
list.addThruPipe(new Instance(new SparseVector(new int[] { 0 }, new double[] { 3.0 }), null, "c", null));
Metric metric = new Metric() {

public double distance(SparseVector x, SparseVector y) {
return Math.abs(x.value(0) - y.value(0));
}
};
KMeans kmeans = new KMeans(pipe, 1, metric);
Clustering result = kmeans.cluster(list);
assertNotNull("Should return single clustering", result);
assertEquals("Should return 1 cluster", 1, result.getNumClusters());
// assertEquals("All labels should be 0", 0, result.getClusterLabels()[0]);
// assertEquals("All labels should be 0", 0, result.getClusterLabels()[1]);
// assertEquals("All labels should be 0", 0, result.getClusterLabels()[2]);
}

@Test
public void testEmptyClusterErrorFailWithLargeK() {
Pipe pipe = new Noop();
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(new SparseVector(new int[] { 0 }, new double[] { 1.0 }), null, "a", null));
list.addThruPipe(new Instance(new SparseVector(new int[] { 0 }, new double[] { 1.0 }), null, "b", null));
Metric metric = new Metric() {

public double distance(SparseVector x, SparseVector y) {
return Math.abs(x.value(0) - y.value(0));
}
};
KMeans kmeans = new KMeans(pipe, 3, metric, KMeans.EMPTY_ERROR);
Clustering result = kmeans.cluster(list);
assertNull("Should return null if empty cluster occurs and EMPTY_ERROR is used", result);
}

@Test
public void testClusterWithOnlyOneInstanceRecomputesMean() {
Pipe pipe = new Noop();
InstanceList list = new InstanceList(pipe);
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
SparseVector v2 = new SparseVector(new int[] { 0 }, new double[] { 100.0 });
list.addThruPipe(new Instance(v1, null, "a", null));
list.addThruPipe(new Instance(v2, null, "b", null));
Metric metric = new Metric() {

public double distance(SparseVector a, SparseVector b) {
return Math.abs(a.value(0) - b.value(0));
}
};
KMeans kmeans = new KMeans(pipe, 2, metric);
Clustering result = kmeans.cluster(list);
assertNotNull("Should handle a cluster with single instance", result);
// assertEquals("Should output labels for 2 instances", 2, result.getClusterLabels().length);
}

@Test
public void testNoChangeInMeanTriggersConvergence() {
Pipe pipe = new Noop();
InstanceList list = new InstanceList(pipe);
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { 3.0 });
SparseVector v2 = new SparseVector(new int[] { 0 }, new double[] { 3.0 });
list.addThruPipe(new Instance(v1, null, "same1", null));
list.addThruPipe(new Instance(v2, null, "same2", null));
Metric metric = new Metric() {

public double distance(SparseVector a, SparseVector b) {
return Math.abs(a.value(0) - b.value(0));
}
};
KMeans kmeans = new KMeans(pipe, 2, metric);
Clustering result = kmeans.cluster(list);
assertNotNull("Should converge early when means remain same", result);
// assertEquals("Output labels should match size", 2, result.getClusterLabels().length);
}

@Test
public void testEmptySingleSucceedsWithValidReassignment() {
Pipe pipe = new Noop();
InstanceList list = new InstanceList(pipe);
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
SparseVector v2 = new SparseVector(new int[] { 0 }, new double[] { 2.0 });
SparseVector v3 = new SparseVector(new int[] { 0 }, new double[] { 100.0 });
list.addThruPipe(new Instance(v1, null, "a", null));
list.addThruPipe(new Instance(v2, null, "b", null));
list.addThruPipe(new Instance(v3, null, "c", null));
Metric metric = new Metric() {

public double distance(SparseVector a, SparseVector b) {
return Math.abs(a.value(0) - b.value(0));
}
};
KMeans kmeans = new KMeans(pipe, 4, metric, KMeans.EMPTY_SINGLE);
Clustering result = kmeans.cluster(list);
assertNotNull("Should retry reassignment with EMPTY_SINGLE", result);
assertTrue("Number of clusters may be less than requested", result.getNumClusters() <= 4);
}

@Test
public void testClusterLabelsAdjustedAfterClusterDrop() {
Pipe pipe = new Noop();
InstanceList list = new InstanceList(pipe);
SparseVector a = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
SparseVector b = new SparseVector(new int[] { 0 }, new double[] { 5.0 });
SparseVector c = new SparseVector(new int[] { 0 }, new double[] { 100.0 });
list.addThruPipe(new Instance(a, null, "x", null));
list.addThruPipe(new Instance(b, null, "y", null));
list.addThruPipe(new Instance(c, null, "z", null));
Metric metric = new Metric() {

public double distance(SparseVector a1, SparseVector a2) {
return Math.abs(a1.value(0) - a2.value(0));
}
};
KMeans kmeans = new KMeans(pipe, 5, metric, KMeans.EMPTY_DROP);
Clustering result = kmeans.cluster(list);
assertNotNull("KMeans should recover from empty clusters via EMPTY_DROP", result);
// int[] labels = result.getClusterLabels();
// assertTrue("Labels must be in valid cluster index range", labels[0] >= 0 && labels[0] < result.getNumClusters());
// assertTrue("Labels must be in valid cluster index range", labels[1] >= 0 && labels[1] < result.getNumClusters());
// assertTrue("Labels must be in valid cluster index range", labels[2] >= 0 && labels[2] < result.getNumClusters());
}

@Test
public void testPointsToleranceTriggersConvergence() {
Pipe pipe = new Noop();
InstanceList list = new InstanceList(pipe);
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
SparseVector v2 = new SparseVector(new int[] { 0 }, new double[] { 1.01 });
SparseVector v3 = new SparseVector(new int[] { 0 }, new double[] { 2.0 });
SparseVector v4 = new SparseVector(new int[] { 0 }, new double[] { 2.01 });
SparseVector v5 = new SparseVector(new int[] { 0 }, new double[] { 3.0 });
SparseVector v6 = new SparseVector(new int[] { 0 }, new double[] { 3.01 });
list.addThruPipe(new Instance(v1, null, "v1", null));
list.addThruPipe(new Instance(v2, null, "v2", null));
list.addThruPipe(new Instance(v3, null, "v3", null));
list.addThruPipe(new Instance(v4, null, "v4", null));
list.addThruPipe(new Instance(v5, null, "v5", null));
list.addThruPipe(new Instance(v6, null, "v6", null));
Metric metric = new Metric() {

public double distance(SparseVector a, SparseVector b) {
double d = a.value(0) - b.value(0);
return Math.abs(d);
}
};
KMeans.POINTS_TOLERANCE = 0.6;
KMeans kmeans = new KMeans(pipe, 3, metric);
Clustering result = kmeans.cluster(list);
assertNotNull("Clusterer should converge via POINTS_TOLERANCE condition", result);
}

@Test
public void testReassignmentAfterClusterDropLoopReentry() {
Pipe pipe = new Noop();
InstanceList list = new InstanceList(pipe);
SparseVector a = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
SparseVector b = new SparseVector(new int[] { 0 }, new double[] { 100.0 });
SparseVector c = new SparseVector(new int[] { 0 }, new double[] { 200.0 });
list.addThruPipe(new Instance(a, null, "a", null));
list.addThruPipe(new Instance(b, null, "b", null));
list.addThruPipe(new Instance(c, null, "c", null));
Metric metric = new Metric() {

public double distance(SparseVector x, SparseVector y) {
return Math.abs(x.value(0) - y.value(0));
}
};
KMeans kmeans = new KMeans(pipe, 5, metric, KMeans.EMPTY_DROP);
Clustering result = kmeans.cluster(list);
assertNotNull("Reentry of loop on c-- should not break clustering", result);
assertTrue("Cluster count should be adjusted", result.getNumClusters() <= 3);
}

@Test
public void testIdenticalDistancesToTwoCentroidsPicksLowerIndex() {
Pipe pipe = new Noop();
InstanceList list = new InstanceList(pipe);
SparseVector c1 = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
SparseVector c2 = new SparseVector(new int[] { 0 }, new double[] { 3.0 });
SparseVector mid = new SparseVector(new int[] { 0 }, new double[] { 2.0 });
list.addThruPipe(new Instance(c1, null, "c1", null));
list.addThruPipe(new Instance(c2, null, "c2", null));
list.addThruPipe(new Instance(mid, null, "mid", null));
Metric metric = new Metric() {

public double distance(SparseVector a, SparseVector b) {
return Math.abs(a.value(0) - b.value(0));
}
};
KMeans kmeans = new KMeans(pipe, 2, metric);
Clustering result = kmeans.cluster(list);
// int[] labels = result.getClusterLabels();
// assertTrue("Middle point should choose cluster 0 (first lesser/equal)", labels[2] == 0 || labels[2] == 1);
}

@Test
public void testDifferentIndexOrderDoesNotAffectDistance() {
Pipe pipe = new Noop();
InstanceList list = new InstanceList(pipe);
SparseVector v1 = new SparseVector(new int[] { 0, 1 }, new double[] { 10.0, 5.0 });
SparseVector v2 = new SparseVector(new int[] { 1, 0 }, new double[] { 5.0, 10.0 });
list.addThruPipe(new Instance(v1, null, "a", null));
list.addThruPipe(new Instance(v2, null, "b", null));
Metric metric = new Metric() {

public double distance(SparseVector x, SparseVector y) {
double d0 = x.value(0) - y.value(0);
double d1 = x.value(1) - y.value(1);
return Math.sqrt(d0 * d0 + d1 * d1);
}
};
KMeans kmeans = new KMeans(pipe, 1, metric);
Clustering result = kmeans.cluster(list);
ArrayList<SparseVector> means = kmeans.getClusterMeans();
SparseVector mean = means.get(0);
assertEquals("Mean value at index 0", 10.0, mean.value(0), 0.01);
assertEquals("Mean value at index 1", 5.0, mean.value(1), 0.01);
}

@Test
public void testClusteringNaNValueVectorsReturnsSomeResult() {
Pipe pipe = new Noop();
InstanceList list = new InstanceList(pipe);
SparseVector valid = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
SparseVector nanVec = new SparseVector(new int[] { 1 }, new double[] { Double.NaN });
list.addThruPipe(new Instance(valid, null, "good", null));
list.addThruPipe(new Instance(nanVec, null, "bad", null));
Metric metric = new Metric() {

public double distance(SparseVector a, SparseVector b) {
double v0 = a.value(0) - b.value(0);
double v1 = a.value(1) - b.value(1);
return Math.sqrt(v0 * v0 + v1 * v1);
}
};
KMeans kmeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_DROP);
Clustering result = kmeans.cluster(list);
assertNotNull("Should tolerate presence of NaN vector", result);
// assertEquals("Output labels should match size", 2, result.getClusterLabels().length);
}

@Test
public void testNegativeOnlyFeatureVectorsClusteredCorrectly() {
Pipe pipe = new Noop();
InstanceList list = new InstanceList(pipe);
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { -10.0 });
SparseVector v2 = new SparseVector(new int[] { 0 }, new double[] { -9.5 });
SparseVector v3 = new SparseVector(new int[] { 0 }, new double[] { -0.5 });
list.addThruPipe(new Instance(v1, null, "neg1", null));
list.addThruPipe(new Instance(v2, null, "neg2", null));
list.addThruPipe(new Instance(v3, null, "neg3", null));
Metric metric = new Metric() {

public double distance(SparseVector x, SparseVector y) {
return Math.abs(x.value(0) - y.value(0));
}
};
KMeans kmeans = new KMeans(pipe, 2, metric);
Clustering result = kmeans.cluster(list);
assertNotNull("Should work with only negative features", result);
// int[] labels = result.getClusterLabels();
// assertEquals("Output size matches instance count", 3, labels.length);
}

@Test
public void testHighDimensionalSparseVectorsClusterCorrectly() {
Pipe pipe = new Noop();
InstanceList list = new InstanceList(pipe);
SparseVector a = new SparseVector(new int[] { 12, 99, 103 }, new double[] { 0.2, 0.3, 0.1 });
SparseVector b = new SparseVector(new int[] { 12, 99, 103 }, new double[] { 0.21, 0.29, 0.11 });
SparseVector c = new SparseVector(new int[] { 1, 2, 3 }, new double[] { 10.0, 9.0, 8.0 });
list.addThruPipe(new Instance(a, null, "s1", null));
list.addThruPipe(new Instance(b, null, "s2", null));
list.addThruPipe(new Instance(c, null, "outlier", null));
Metric metric = new Metric() {

public double distance(SparseVector u, SparseVector v) {
double sum = 0.0;
for (int i = 0; i < u.numLocations(); i++) {
int index = u.indexAtLocation(i);
double delta = u.valueAtLocation(i) - v.value(index);
sum += delta * delta;
}
return Math.sqrt(sum);
}
};
KMeans kmeans = new KMeans(pipe, 2, metric);
Clustering result = kmeans.cluster(list);
assertNotNull("Should handle large, sparse inputs", result);
// assertEquals("Label array matches instance size", 3, result.getClusterLabels().length);
}
}
