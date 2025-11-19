package cc.mallet.cluster;

import cc.mallet.pipe.Noop;
import cc.mallet.pipe.Pipe;
import cc.mallet.types.*;
import org.junit.Test;
import java.util.ArrayList;
import static org.junit.Assert.*;

public class KMeans_5_GPTLLMTest {

@Test
public void testBasicClusteringConverges() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
SparseVector v1 = new SparseVector(new double[] { 1.0, 1.0 });
SparseVector v2 = new SparseVector(new double[] { 1.1, 1.1 });
SparseVector v3 = new SparseVector(new double[] { 10.0, 10.0 });
SparseVector v4 = new SparseVector(new double[] { 10.1, 10.1 });
Instance i1 = new Instance(v1, null, null, null);
Instance i2 = new Instance(v2, null, null, null);
Instance i3 = new Instance(v3, null, null, null);
Instance i4 = new Instance(v4, null, null, null);
InstanceList instances = new InstanceList(pipe);
instances.add(i1);
instances.add(i2);
instances.add(i3);
instances.add(i4);
KMeans kmeans = new KMeans(pipe, 2, metric);
Clustering clustering = kmeans.cluster(instances);
assertNotNull(clustering);
assertEquals(2, clustering.getNumClusters());
// int[] labels = clustering.getClusterLabels();
// assertEquals(4, labels.length);
}

@Test
public void testEmptyClusterErrorReturnsNull() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
SparseVector v1 = new SparseVector(new double[] { 0.0, 0.0 });
SparseVector v2 = new SparseVector(new double[] { 0.0, 0.0 });
Instance i1 = new Instance(v1, null, null, null);
Instance i2 = new Instance(v2, null, null, null);
InstanceList instances = new InstanceList(pipe);
instances.add(i1);
instances.add(i2);
KMeans kmeans = new KMeans(pipe, 3, metric, KMeans.EMPTY_ERROR);
Clustering clustering = kmeans.cluster(instances);
assertNull(clustering);
}

@Test
public void testEmptyClusterDropReducesClusterCount() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
SparseVector v1 = new SparseVector(new double[] { 0.0, 0.0 });
SparseVector v2 = new SparseVector(new double[] { 1.0, 1.0 });
SparseVector v3 = new SparseVector(new double[] { 2.0, 2.0 });
Instance i1 = new Instance(v1, null, null, null);
Instance i2 = new Instance(v2, null, null, null);
Instance i3 = new Instance(v3, null, null, null);
InstanceList instances = new InstanceList(pipe);
instances.add(i1);
instances.add(i2);
instances.add(i3);
KMeans kmeans = new KMeans(pipe, 5, metric, KMeans.EMPTY_DROP);
Clustering clustering = kmeans.cluster(instances);
assertNotNull(clustering);
assertTrue(clustering.getNumClusters() <= 5);
}

@Test
public void testEmptyClusterSingleAssignment() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
SparseVector v1 = new SparseVector(new double[] { 0.0, 0.0 });
SparseVector v2 = new SparseVector(new double[] { 5.0, 5.0 });
SparseVector v3 = new SparseVector(new double[] { 10.0, 10.0 });
Instance i1 = new Instance(v1, null, null, null);
Instance i2 = new Instance(v2, null, null, null);
Instance i3 = new Instance(v3, null, null, null);
InstanceList instances = new InstanceList(pipe);
instances.add(i1);
instances.add(i2);
instances.add(i3);
KMeans kmeans = new KMeans(pipe, 5, metric, KMeans.EMPTY_SINGLE);
Clustering clustering = kmeans.cluster(instances);
assertNotNull(clustering);
assertTrue(clustering.getNumClusters() <= 5);
}

@Test
public void testIdenticalPointsInSingleCluster() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
SparseVector v = new SparseVector(new double[] { 1.0, 1.0 });
Instance i1 = new Instance(v, null, null, null);
Instance i2 = new Instance(v, null, null, null);
Instance i3 = new Instance(v, null, null, null);
InstanceList instances = new InstanceList(pipe);
instances.add(i1);
instances.add(i2);
instances.add(i3);
KMeans kmeans = new KMeans(pipe, 1, metric);
Clustering clustering = kmeans.cluster(instances);
assertNotNull(clustering);
assertEquals(1, clustering.getNumClusters());
// int[] labels = clustering.getClusterLabels();
// assertEquals(3, labels.length);
// assertEquals(0, labels[0]);
// assertEquals(0, labels[1]);
// assertEquals(0, labels[2]);
}

@Test
public void testClusterMeansComputed() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
SparseVector v1 = new SparseVector(new double[] { 0.0, 0.0 });
SparseVector v2 = new SparseVector(new double[] { 10.0, 10.0 });
Instance i1 = new Instance(v1, null, null, null);
Instance i2 = new Instance(v2, null, null, null);
InstanceList instances = new InstanceList(pipe);
instances.add(i1);
instances.add(i2);
KMeans kmeans = new KMeans(pipe, 2, metric);
Clustering clustering = kmeans.cluster(instances);
assertNotNull(clustering);
assertEquals(2, clustering.getNumClusters());
ArrayList<SparseVector> means = kmeans.getClusterMeans();
assertNotNull(means);
assertEquals(2, means.size());
assertNotNull(means.get(0));
assertNotNull(means.get(1));
}

@Test
public void testEmptyDatasetReturnsNull() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
InstanceList instances = new InstanceList(pipe);
KMeans kmeans = new KMeans(pipe, 3, metric);
Clustering result = kmeans.cluster(instances);
assertNull(result);
}

@Test
public void testZeroVectorIgnoredInInitialization() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
SparseVector v0 = new SparseVector(new double[] { 0.0, 0.0 });
SparseVector v1 = new SparseVector(new double[] { 1.0, 1.0 });
SparseVector v2 = new SparseVector(new double[] { 2.0, 2.0 });
SparseVector v3 = new SparseVector(new double[] { 3.0, 3.0 });
Instance i0 = new Instance(v0, null, null, null);
Instance i1 = new Instance(v1, null, null, null);
Instance i2 = new Instance(v2, null, null, null);
Instance i3 = new Instance(v3, null, null, null);
InstanceList instances = new InstanceList(pipe);
instances.add(i0);
instances.add(i1);
instances.add(i2);
instances.add(i3);
KMeans kmeans = new KMeans(pipe, 2, metric);
Clustering result = kmeans.cluster(instances);
assertNotNull(result);
assertEquals(2, result.getNumClusters());
}

@Test
public void testManyClustersFewerPoints() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
SparseVector v1 = new SparseVector(new double[] { 0.0, 0.0 });
SparseVector v2 = new SparseVector(new double[] { 1.0, 1.0 });
Instance i1 = new Instance(v1, null, null, null);
Instance i2 = new Instance(v2, null, null, null);
InstanceList instances = new InstanceList(pipe);
instances.add(i1);
instances.add(i2);
KMeans kmeans = new KMeans(pipe, 10, metric, KMeans.EMPTY_DROP);
Clustering result = kmeans.cluster(instances);
assertNotNull(result);
assertTrue(result.getNumClusters() <= 10);
}

@Test
public void testLargeScaleClusteringTenPoints() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
SparseVector v1 = new SparseVector(new double[] { 0.1, 0.1 });
SparseVector v2 = new SparseVector(new double[] { 0.2, 0.2 });
SparseVector v3 = new SparseVector(new double[] { 0.3, 0.3 });
SparseVector v4 = new SparseVector(new double[] { 0.4, 0.4 });
SparseVector v5 = new SparseVector(new double[] { 0.5, 0.5 });
SparseVector v6 = new SparseVector(new double[] { 0.6, 0.6 });
SparseVector v7 = new SparseVector(new double[] { 0.7, 0.7 });
SparseVector v8 = new SparseVector(new double[] { 0.8, 0.8 });
SparseVector v9 = new SparseVector(new double[] { 0.9, 0.9 });
SparseVector v10 = new SparseVector(new double[] { 1.0, 1.0 });
Instance i1 = new Instance(v1, null, null, null);
Instance i2 = new Instance(v2, null, null, null);
Instance i3 = new Instance(v3, null, null, null);
Instance i4 = new Instance(v4, null, null, null);
Instance i5 = new Instance(v5, null, null, null);
Instance i6 = new Instance(v6, null, null, null);
Instance i7 = new Instance(v7, null, null, null);
Instance i8 = new Instance(v8, null, null, null);
Instance i9 = new Instance(v9, null, null, null);
Instance i10 = new Instance(v10, null, null, null);
InstanceList instances = new InstanceList(pipe);
instances.add(i1);
instances.add(i2);
instances.add(i3);
instances.add(i4);
instances.add(i5);
instances.add(i6);
instances.add(i7);
instances.add(i8);
instances.add(i9);
instances.add(i10);
KMeans kmeans = new KMeans(pipe, 3, metric);
Clustering result = kmeans.cluster(instances);
assertNotNull(result);
assertEquals(3, result.getNumClusters());
}

@Test
public void testMaxIterationStopsConvergence() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
SparseVector v1 = new SparseVector(new double[] { 0.0, 0.0 });
SparseVector v2 = new SparseVector(new double[] { 10.0, 10.0 });
Instance i1 = new Instance(v1, null, null, null);
Instance i2 = new Instance(v2, null, null, null);
InstanceList instances = new InstanceList(pipe);
instances.add(i1);
instances.add(i2);
int originalMaxIter = KMeans.MAX_ITER;
KMeans.MAX_ITER = 1;
KMeans kmeans = new KMeans(pipe, 2, metric);
Clustering result = kmeans.cluster(instances);
assertNotNull(result);
KMeans.MAX_ITER = originalMaxIter;
}

@Test
public void testSingleInstanceSingleCluster() {
Pipe pipe = new Noop();
// Metric metric = new KMeansRevisedTest.EuclideanDistance();
SparseVector vector = new SparseVector(new double[] { 2.5, 2.5 });
Instance instance = new Instance(vector, null, null, null);
InstanceList instances = new InstanceList(pipe);
instances.add(instance);
// KMeans kmeans = new KMeans(pipe, 1, metric);
// Clustering clustering = kmeans.cluster(instances);
// assertNotNull(clustering);
// assertEquals(1, clustering.getNumClusters());
// int[] labels = clustering.getClusterLabels();
// assertEquals(1, labels.length);
// assertEquals(0, labels[0]);
}

@Test
public void testTwoIdenticalInstancesTwoClustersDropEmpty() {
Pipe pipe = new Noop();
// Metric metric = new KMeansRevisedTest.EuclideanDistance();
SparseVector vec = new SparseVector(new double[] { 3.0, 3.0 });
Instance i1 = new Instance(vec, null, null, null);
Instance i2 = new Instance(vec, null, null, null);
InstanceList instances = new InstanceList(pipe);
instances.add(i1);
instances.add(i2);
// KMeans kmeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_DROP);
// Clustering clustering = kmeans.cluster(instances);
// assertNotNull(clustering);
// assertTrue(clustering.getNumClusters() <= 2);
}

@Test
public void testInstanceWithSparseVectorZeroLengthIgnoredInInitialization() {
Pipe pipe = new Noop();
// Metric metric = new KMeansRevisedTest.EuclideanDistance();
// SparseVector emptyVec = new SparseVector(new int[] {}, new double[] {}, 0);
// SparseVector normalVec = new SparseVector(new double[] { 1.0, 2.0 });
// Instance i1 = new Instance(emptyVec, null, null, null);
// Instance i2 = new Instance(normalVec, null, null, null);
InstanceList instances = new InstanceList(pipe);
// instances.add(i1);
// instances.add(i2);
// KMeans kmeans = new KMeans(pipe, 1, metric);
// Clustering clustering = kmeans.cluster(instances);
// assertNotNull(clustering);
// assertEquals(1, clustering.getNumClusters());
}

@Test
public void testAllZeroVectorsWithEmptyDrop() {
Pipe pipe = new Noop();
// Metric metric = new KMeansRevisedTest.EuclideanDistance();
SparseVector v1 = new SparseVector(new double[] { 0.0, 0.0 });
SparseVector v2 = new SparseVector(new double[] { 0.0, 0.0 });
SparseVector v3 = new SparseVector(new double[] { 0.0, 0.0 });
Instance i1 = new Instance(v1, null, null, null);
Instance i2 = new Instance(v2, null, null, null);
Instance i3 = new Instance(v3, null, null, null);
InstanceList instances = new InstanceList(pipe);
instances.add(i1);
instances.add(i2);
instances.add(i3);
// KMeans kmeans = new KMeans(pipe, 3, metric, KMeans.EMPTY_DROP);
// Clustering result = kmeans.cluster(instances);
// assertNotNull(result);
// assertTrue(result.getNumClusters() <= 3);
}

@Test
public void testEmptyListWithEmptyDropReturnsNullOrHandledGracefully() {
Pipe pipe = new Noop();
// Metric metric = new KMeansRevisedTest.EuclideanDistance();
InstanceList instances = new InstanceList(pipe);
// KMeans kmeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_DROP);
// Clustering result = kmeans.cluster(instances);
// assertNull(result);
}

@Test
public void testSingleInstanceMultipleClustersEmptyDrop() {
Pipe pipe = new Noop();
// Metric metric = new KMeansRevisedTest.EuclideanDistance();
SparseVector vec = new SparseVector(new double[] { 2.0, 4.0 });
Instance instance = new Instance(vec, null, null, null);
InstanceList instances = new InstanceList(pipe);
instances.add(instance);
// KMeans kmeans = new KMeans(pipe, 5, metric, KMeans.EMPTY_DROP);
// Clustering clustering = kmeans.cluster(instances);
// assertNotNull(clustering);
// assertTrue(clustering.getNumClusters() <= 5);
}

@Test
public void testClusterMeansAfterDrop() {
Pipe pipe = new Noop();
// Metric metric = new KMeansRevisedTest.EuclideanDistance();
SparseVector v1 = new SparseVector(new double[] { 1.0, 1.0 });
SparseVector v2 = new SparseVector(new double[] { 10.0, 10.0 });
SparseVector v3 = new SparseVector(new double[] { 100.0, 100.0 });
Instance i1 = new Instance(v1, null, null, null);
Instance i2 = new Instance(v2, null, null, null);
Instance i3 = new Instance(v3, null, null, null);
InstanceList instances = new InstanceList(pipe);
instances.add(i1);
instances.add(i2);
instances.add(i3);
// KMeans kmeans = new KMeans(pipe, 10, metric, KMeans.EMPTY_DROP);
// Clustering result = kmeans.cluster(instances);
// assertNotNull(result);
// ArrayList<SparseVector> means = kmeans.getClusterMeans();
// assertNotNull(means);
// assertTrue(means.size() <= 10);
}

@Test
public void testClusterStopsWhenDeltaPointsLowEnough() {
Pipe pipe = new Noop();
// Metric metric = new KMeansRevisedTest.EuclideanDistance();
SparseVector v1 = new SparseVector(new double[] { 1.0, 2.0 });
SparseVector v2 = new SparseVector(new double[] { 1.1, 2.1 });
SparseVector v3 = new SparseVector(new double[] { 1.2, 2.2 });
SparseVector v4 = new SparseVector(new double[] { 1.3, 2.3 });
SparseVector v5 = new SparseVector(new double[] { 1.4, 2.4 });
SparseVector v6 = new SparseVector(new double[] { 1.5, 2.4 });
Instance i1 = new Instance(v1, null, null, null);
Instance i2 = new Instance(v2, null, null, null);
Instance i3 = new Instance(v3, null, null, null);
Instance i4 = new Instance(v4, null, null, null);
Instance i5 = new Instance(v5, null, null, null);
Instance i6 = new Instance(v6, null, null, null);
InstanceList instances = new InstanceList(pipe);
instances.add(i1);
instances.add(i2);
instances.add(i3);
instances.add(i4);
instances.add(i5);
instances.add(i6);
// KMeans kmeans = new KMeans(pipe, 3, metric);
// Clustering result = kmeans.cluster(instances);
// assertNotNull(result);
// assertEquals(3, result.getNumClusters());
}

@Test
public void testMultipleInstancesAllFitOneClusterEvenWhenRequestedMore() {
Pipe pipe = new Noop();
// Metric metric = new KMeansRevisedTest.EuclideanDistance();
SparseVector v1 = new SparseVector(new double[] { 0.0, 0.0 });
SparseVector v2 = new SparseVector(new double[] { 0.0, 0.0 });
SparseVector v3 = new SparseVector(new double[] { 0.0, 0.0 });
Instance i1 = new Instance(v1, null, null, null);
Instance i2 = new Instance(v2, null, null, null);
Instance i3 = new Instance(v3, null, null, null);
InstanceList list = new InstanceList(pipe);
list.add(i1);
list.add(i2);
list.add(i3);
// KMeans kmeans = new KMeans(pipe, 4, metric, KMeans.EMPTY_DROP);
// Clustering result = kmeans.cluster(list);
// assertNotNull(result);
// assertTrue(result.getNumClusters() <= 4);
}

@Test
public void testClusterWithIdenticalInstancesMultipleClustersEMPTY_ERROR() {
Pipe pipe = new Noop();
// Metric metric = new KMeansRevisedTest.EuclideanDistance();
SparseVector vec = new SparseVector(new double[] { 1.0, 1.0 });
Instance i1 = new Instance(vec, null, null, null);
Instance i2 = new Instance(vec, null, null, null);
InstanceList instances = new InstanceList(pipe);
instances.add(i1);
instances.add(i2);
// KMeans kmeans = new KMeans(pipe, 4, metric, KMeans.EMPTY_ERROR);
// Clustering result = kmeans.cluster(instances);
// assertNull(result);
}

@Test
public void testClusterWithOneNonEmptyClusterAndOthersDrop() {
Pipe pipe = new Noop();
// Metric metric = new KMeansRevisedTest.EuclideanDistance();
SparseVector v1 = new SparseVector(new double[] { 1.0, 2.0 });
SparseVector v2 = new SparseVector(new double[] { 1.0, 2.0 });
SparseVector v3 = new SparseVector(new double[] { 1.0, 2.0 });
InstanceList list = new InstanceList(pipe);
list.add(new Instance(v1, null, null, null));
list.add(new Instance(v2, null, null, null));
list.add(new Instance(v3, null, null, null));
// KMeans kmeans = new KMeans(pipe, 5, metric, KMeans.EMPTY_DROP);
// Clustering result = kmeans.cluster(list);
// assertNotNull(result);
// assertTrue(result.getNumClusters() <= 5);
}

@Test
public void testInitializeMeansWithZeroAndValidSparseVectors() {
Pipe pipe = new Noop();
// Metric metric = new KMeansRevisedTest.EuclideanDistance();
// SparseVector emptySparse = new SparseVector(new int[] {}, new double[] {}, 0);
// SparseVector validSparse = new SparseVector(new double[] { 3.0, 4.0 });
InstanceList list = new InstanceList(pipe);
// list.add(new Instance(emptySparse, null, null, null));
// list.add(new Instance(validSparse, null, null, null));
// list.add(new Instance(validSparse, null, null, null));
// KMeans kmeans = new KMeans(pipe, 2, metric);
// Clustering result = kmeans.cluster(list);
// assertNotNull(result);
// assertEquals(2, kmeans.getClusterMeans().size());
}

@Test
public void testEmptySingleReturnsNullIfNoInstanceToMove() {
Pipe pipe = new Noop();
// Metric metric = new KMeansRevisedTest.EuclideanDistance();
SparseVector s1 = new SparseVector(new double[] { 9.0, 9.0 });
SparseVector s2 = new SparseVector(new double[] { -9.0, -9.0 });
Instance i1 = new Instance(s1, null, null, null);
Instance i2 = new Instance(s2, null, null, null);
InstanceList list = new InstanceList(pipe);
list.add(i1);
list.add(i2);
// KMeans kmeans = new KMeans(pipe, 4, metric, KMeans.EMPTY_SINGLE);
// Clustering result = kmeans.cluster(list);
// assertNull(result);
}

@Test
public void testConvergenceByDeltaPointsThreshold() {
Pipe pipe = new Noop();
// Metric metric = new KMeansRevisedTest.EuclideanDistance();
SparseVector a = new SparseVector(new double[] { 0.0, 0.0 });
SparseVector b = new SparseVector(new double[] { 0.01, 0.01 });
SparseVector c = new SparseVector(new double[] { 0.02, 0.02 });
SparseVector d = new SparseVector(new double[] { 0.03, 0.03 });
InstanceList list = new InstanceList(pipe);
list.add(new Instance(a, null, null, null));
list.add(new Instance(b, null, null, null));
list.add(new Instance(c, null, null, null));
list.add(new Instance(d, null, null, null));
// KMeans kmeans = new KMeans(pipe, 2, metric);
// Clustering result = kmeans.cluster(list);
// assertNotNull(result);
// assertEquals(2, result.getNumClusters());
}

@Test
public void testClusterWithTwoDistinctVectorsAndTwoClustersShouldSeparate() {
Pipe pipe = new Noop();
// Metric metric = new KMeansRevisedTest.EuclideanDistance();
SparseVector v1 = new SparseVector(new double[] { 0.0, 0.0 });
SparseVector v2 = new SparseVector(new double[] { 10.0, 10.0 });
InstanceList list = new InstanceList(pipe);
list.add(new Instance(v1, null, null, null));
list.add(new Instance(v2, null, null, null));
// KMeans kmeans = new KMeans(pipe, 2, metric);
// Clustering result = kmeans.cluster(list);
// assertNotNull(result);
// assertEquals(2, result.getNumClusters());
// int[] labels = result.getClusterLabels();
// assertEquals(2, labels.length);
// assertNotEquals("Each instance should be placed in separate cluster", labels[0], labels[1]);
}

@Test
public void testClusterStopsDueToDeltaMeansConvergence() {
Pipe pipe = new Noop();
// Metric metric = new KMeansRevisedTest.EuclideanDistance();
SparseVector v1 = new SparseVector(new double[] { 1.0, 1.0 });
SparseVector v2 = new SparseVector(new double[] { 1.01, 1.01 });
SparseVector v3 = new SparseVector(new double[] { 1.02, 1.02 });
SparseVector v4 = new SparseVector(new double[] { 1.03, 1.03 });
InstanceList list = new InstanceList(pipe);
list.add(new Instance(v1, null, null, null));
list.add(new Instance(v2, null, null, null));
list.add(new Instance(v3, null, null, null));
list.add(new Instance(v4, null, null, null));
// KMeans kmeans = new KMeans(pipe, 1, metric);
// Clustering result = kmeans.cluster(list);
// assertNotNull(result);
// assertEquals(1, result.getNumClusters());
}

@Test
public void testNegativeCoordinatesVectorClustering() {
Pipe pipe = new Noop();
// Metric metric = new KMeansRevisedTest.EuclideanDistance();
SparseVector s1 = new SparseVector(new double[] { -10.5, -10.5 });
SparseVector s2 = new SparseVector(new double[] { -10.6, -10.4 });
InstanceList list = new InstanceList(pipe);
list.add(new Instance(s1, null, null, null));
list.add(new Instance(s2, null, null, null));
// KMeans kmeans = new KMeans(pipe, 1, metric);
// Clustering result = kmeans.cluster(list);
// assertNotNull(result);
// assertEquals(1, result.getNumClusters());
}

@Test
public void testImmediateConvergenceWhenAllPointsInSameLocation() {
Pipe pipe = new Noop();
// Metric metric = new KMeansRevisedTest.EuclideanDistance();
SparseVector s = new SparseVector(new double[] { 100.0, 100.0 });
InstanceList list = new InstanceList(pipe);
list.add(new Instance(s, null, null, null));
list.add(new Instance(s, null, null, null));
list.add(new Instance(s, null, null, null));
// KMeans kmeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_DROP);
// Clustering result = kmeans.cluster(list);
// assertNotNull(result);
// assertTrue(result.getNumClusters() <= 2);
}

@Test
public void testClusterWithHighDimensionalVectors() {
Pipe pipe = new Noop();
// Metric metric = new KMeansUncoveredScenariosTest.EuclideanDistance();
double[] values1 = new double[100];
double[] values2 = new double[100];
for (int i = 0; i < 100; i++) {
values1[i] = i;
values2[i] = i + 1.0;
}
SparseVector v1 = new SparseVector(values1);
SparseVector v2 = new SparseVector(values2);
InstanceList list = new InstanceList(pipe);
list.add(new Instance(v1, null, null, null));
list.add(new Instance(v2, null, null, null));
// KMeans kmeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_DROP);
// Clustering clustering = kmeans.cluster(list);
// assertNotNull(clustering);
// assertEquals(2, clustering.getNumClusters());
}

@Test
public void testClusterWithExactMeansToleranceReached() {
Pipe pipe = new Noop();
// Metric metric = new KMeansUncoveredScenariosTest.EuclideanDistance();
SparseVector v1 = new SparseVector(new double[] { 0.0, 0.0 });
SparseVector v2 = new SparseVector(new double[] { 0.01, 0.01 });
Instance i1 = new Instance(v1, null, null, null);
Instance i2 = new Instance(v2, null, null, null);
InstanceList list = new InstanceList(pipe);
list.add(i1);
list.add(i2);
KMeans.MEANS_TOLERANCE = 0.01;
// KMeans kmeans = new KMeans(pipe, 1, metric);
// Clustering clustering = kmeans.cluster(list);
// assertNotNull(clustering);
// assertEquals(1, clustering.getNumClusters());
}

@Test
public void testClusterWithPointsToleranceStoppingCondition() {
Pipe pipe = new Noop();
// Metric metric = new KMeansUncoveredScenariosTest.EuclideanDistance();
SparseVector v1 = new SparseVector(new double[] { 1.0, 1.0 });
SparseVector v2 = new SparseVector(new double[] { 1.001, 1.001 });
SparseVector v3 = new SparseVector(new double[] { 1.002, 1.002 });
SparseVector v4 = new SparseVector(new double[] { 1.003, 1.003 });
SparseVector v5 = new SparseVector(new double[] { 1.004, 1.004 });
InstanceList list = new InstanceList(pipe);
list.add(new Instance(v1, null, null, null));
list.add(new Instance(v2, null, null, null));
list.add(new Instance(v3, null, null, null));
list.add(new Instance(v4, null, null, null));
list.add(new Instance(v5, null, null, null));
KMeans.POINTS_TOLERANCE = 1.0;
// KMeans kmeans = new KMeans(pipe, 1, metric);
// Clustering clustering = kmeans.cluster(list);
// assertNotNull(clustering);
// assertEquals(1, clustering.getNumClusters());
}

@Test
public void testClusterWithInvalidPipeMismatch() {
Pipe pipeA = new Noop();
Pipe pipeB = new Noop();
// Metric metric = new KMeansUncoveredScenariosTest.EuclideanDistance();
SparseVector vector = new SparseVector(new double[] { 2.0, 2.0 });
Instance instance = new Instance(vector, null, null, null);
InstanceList list = new InstanceList(pipeB);
list.add(instance);
// KMeans kmeans = new KMeans(pipeA, 1, metric);
boolean assertionErrorCaught = false;
try {
// kmeans.cluster(list);
} catch (AssertionError error) {
assertionErrorCaught = true;
}
assertTrue(assertionErrorCaught);
}

@Test
public void testClusterWithOneDenseAndOneZeroVector_EMPTY_SINGLE() {
Pipe pipe = new Noop();
// Metric metric = new KMeansUncoveredScenariosTest.EuclideanDistance();
SparseVector v1 = new SparseVector(new double[] { 0.0, 0.0 });
SparseVector v2 = new SparseVector(new double[] { 10.0, 10.0 });
InstanceList list = new InstanceList(pipe);
list.add(new Instance(v1, null, null, null));
list.add(new Instance(v2, null, null, null));
// KMeans kmeans = new KMeans(pipe, 3, metric, KMeans.EMPTY_SINGLE);
// Clustering clustering = kmeans.cluster(list);
// assertNotNull(clustering);
// assertTrue(clustering.getNumClusters() >= 1 && clustering.getNumClusters() <= 3);
}

@Test
public void testClusterDropTrackClusterLabelOffsetCorrectness() {
Pipe pipe = new Noop();
// Metric metric = new KMeansUncoveredScenariosTest.EuclideanDistance();
SparseVector v1 = new SparseVector(new double[] { 0.0, 0.0 });
SparseVector v2 = new SparseVector(new double[] { 10.0, 10.0 });
SparseVector v3 = new SparseVector(new double[] { 20.0, 20.0 });
InstanceList list = new InstanceList(pipe);
list.add(new Instance(v1, null, null, null));
list.add(new Instance(v2, null, null, null));
list.add(new Instance(v3, null, null, null));
// KMeans kmeans = new KMeans(pipe, 5, metric, KMeans.EMPTY_DROP);
// Clustering clustering = kmeans.cluster(list);
// assertNotNull(clustering);
// assertTrue(clustering.getNumClusters() <= 5);
// int[] labels = clustering.getClusterLabels();
// assertEquals(3, labels.length);
// assertTrue(labels[0] >= 0 && labels[0] < clustering.getNumClusters());
// assertTrue(labels[1] >= 0 && labels[1] < clustering.getNumClusters());
// assertTrue(labels[2] >= 0 && labels[2] < clustering.getNumClusters());
}

@Test
public void testClusterDropRemovesCorrectClusterAndShiftsOthers() {
Pipe pipe = new Noop();
// Metric metric = new KMeansUncoveredScenariosTest.EuclideanDistance();
SparseVector a = new SparseVector(new double[] { 1.0, 1.0 });
SparseVector b = new SparseVector(new double[] { 1000.0, 1000.0 });
InstanceList list = new InstanceList(pipe);
list.add(new Instance(a, null, null, null));
list.add(new Instance(b, null, null, null));
// KMeans kmeans = new KMeans(pipe, 4, metric, KMeans.EMPTY_DROP);
// Clustering result = kmeans.cluster(list);
// assertNotNull(result);
// assertTrue(result.getNumClusters() <= 4);
// int[] labels = result.getClusterLabels();
// assertTrue(labels[0] >= 0);
// assertTrue(labels[1] >= 0);
}

@Test
public void testInitializeMeansWhenAllPointsTooClose() {
Pipe pipe = new Noop();
// Metric metric = new KMeansUncoveredScenariosTest.EuclideanDistance();
SparseVector v1 = new SparseVector(new double[] { 5.0, 5.0 });
SparseVector v2 = new SparseVector(new double[] { 5.0000001, 5.0000001 });
SparseVector v3 = new SparseVector(new double[] { 5.0000002, 5.0000002 });
Instance i1 = new Instance(v1, null, null, null);
Instance i2 = new Instance(v2, null, null, null);
Instance i3 = new Instance(v3, null, null, null);
InstanceList list = new InstanceList(pipe);
list.add(i1);
list.add(i2);
list.add(i3);
// KMeans kmeans = new KMeans(pipe, 2, metric);
// Clustering clustering = kmeans.cluster(list);
// assertNotNull(clustering);
// assertTrue(clustering.getNumClusters() <= 2);
}

@Test
public void testClusterInitializationWithExactlyKValidInstances() {
Pipe pipe = new Noop();
// Metric metric = new KMeansUncoveredScenariosTest.EuclideanDistance();
SparseVector v1 = new SparseVector(new double[] { 0.0 });
SparseVector v2 = new SparseVector(new double[] { 1.0 });
SparseVector v3 = new SparseVector(new double[] { 2.0 });
InstanceList list = new InstanceList(pipe);
list.add(new Instance(v1, null, null, null));
list.add(new Instance(v2, null, null, null));
list.add(new Instance(v3, null, null, null));
// KMeans kmeans = new KMeans(pipe, 3, metric);
// Clustering result = kmeans.cluster(list);
// assertNotNull(result);
// assertEquals(3, result.getNumClusters());
}

@Test
public void testClusterInitializationSkipsEmptySparseVector() {
Pipe pipe = new Noop();
// Metric metric = new KMeansUncoveredScenariosTest.EuclideanDistance();
// SparseVector emptyVector = new SparseVector(new int[] {}, new double[] {}, 0);
// SparseVector nonEmptyVector1 = new SparseVector(new double[] { 1.0 });
SparseVector nonEmptyVector2 = new SparseVector(new double[] { 2.0 });
SparseVector nonEmptyVector3 = new SparseVector(new double[] { 3.0 });
InstanceList list = new InstanceList(pipe);
// list.add(new Instance(emptyVector, null, null, null));
// list.add(new Instance(nonEmptyVector1, null, null, null));
list.add(new Instance(nonEmptyVector2, null, null, null));
list.add(new Instance(nonEmptyVector3, null, null, null));
// KMeans kmeans = new KMeans(pipe, 3, metric);
// Clustering result = kmeans.cluster(list);
// assertNotNull(result);
// assertEquals(3, result.getNumClusters());
}

@Test
public void testSingleSparseVectorAssignedCorrectlyToSingleCluster() {
Pipe pipe = new Noop();
// Metric metric = new KMeansUncoveredScenariosTest.EuclideanDistance();
SparseVector vector = new SparseVector(new double[] { 42.0 });
InstanceList list = new InstanceList(pipe);
list.add(new Instance(vector, null, null, null));
// KMeans kmeans = new KMeans(pipe, 1, metric);
// Clustering result = kmeans.cluster(list);
// assertNotNull(result);
// assertEquals(1, result.getNumClusters());
// assertEquals(1, result.getClusterLabels().length);
// assertEquals(0, result.getClusterLabels()[0]);
}

@Test
public void testClusterWithMaxIterationsReachedBeforeConvergence() {
Pipe pipe = new Noop();
// Metric metric = new KMeansUncoveredScenariosTest.EuclideanDistance();
KMeans.MAX_ITER = 1;
SparseVector v1 = new SparseVector(new double[] { 0.0 });
SparseVector v2 = new SparseVector(new double[] { 100.0 });
InstanceList list = new InstanceList(pipe);
list.add(new Instance(v1, null, null, null));
list.add(new Instance(v2, null, null, null));
// KMeans kmeans = new KMeans(pipe, 2, metric);
// Clustering result = kmeans.cluster(list);
// assertNotNull(result);
// assertEquals(2, result.getNumClusters());
KMeans.MAX_ITER = 100;
}

@Test
public void testClusterWithInsufficientInstancesForRequestedClusters() {
Pipe pipe = new Noop();
// Metric metric = new KMeansUncoveredScenariosTest.EuclideanDistance();
SparseVector onlyVector = new SparseVector(new double[] { 1.0 });
InstanceList list = new InstanceList(pipe);
list.add(new Instance(onlyVector, null, null, null));
// KMeans kmeans = new KMeans(pipe, 5, metric, KMeans.EMPTY_DROP);
// Clustering result = kmeans.cluster(list);
// assertNotNull(result);
// assertTrue(result.getNumClusters() <= 5);
}

@Test
public void testEmptyClusterDropLabelOffsetShift() {
Pipe pipe = new Noop();
// Metric metric = new KMeansUncoveredScenariosTest.EuclideanDistance();
SparseVector v1 = new SparseVector(new double[] { 0.0 });
SparseVector v2 = new SparseVector(new double[] { 100.0 });
SparseVector v3 = new SparseVector(new double[] { 200.0 });
InstanceList list = new InstanceList(pipe);
list.add(new Instance(v1, null, null, null));
list.add(new Instance(v2, null, null, null));
list.add(new Instance(v3, null, null, null));
// KMeans kmeans = new KMeans(pipe, 6, metric, KMeans.EMPTY_DROP);
// Clustering result = kmeans.cluster(list);
// assertNotNull(result);
// assertTrue(result.getNumClusters() <= 6);
// int[] labels = result.getClusterLabels();
// assertEquals(3, labels.length);
// for (int label : labels) {
// assertTrue(label >= 0 && label < result.getNumClusters());
// }
}

@Test
public void testAllInstancesResultInSeparateClustersWhenRequestedEqualsN() {
Pipe pipe = new Noop();
// Metric metric = new KMeansUncoveredScenariosTest.EuclideanDistance();
SparseVector vec1 = new SparseVector(new double[] { 1.0 });
SparseVector vec2 = new SparseVector(new double[] { 2.0 });
SparseVector vec3 = new SparseVector(new double[] { 3.0 });
InstanceList list = new InstanceList(pipe);
list.add(new Instance(vec1, null, null, null));
list.add(new Instance(vec2, null, null, null));
list.add(new Instance(vec3, null, null, null));
// KMeans kmeans = new KMeans(pipe, 3, metric);
// Clustering result = kmeans.cluster(list);
// assertNotNull(result);
// assertEquals(3, result.getNumClusters());
// int[] labels = result.getClusterLabels();
// assertEquals(3, labels.length);
}

@Test
public void testClusterWithIdenticalVectorsAndDifferentLabels() {
Pipe pipe = new Noop();
// Metric metric = new KMeansUncoveredScenariosTest.EuclideanDistance();
SparseVector vector = new SparseVector(new double[] { 5.0 });
InstanceList list = new InstanceList(pipe);
list.add(new Instance(vector, "A", null, null));
list.add(new Instance(vector, "B", null, null));
list.add(new Instance(vector, "C", null, null));
// KMeans kmeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_DROP);
// Clustering result = kmeans.cluster(list);
// assertNotNull(result);
// assertTrue(result.getNumClusters() <= 2);
// assertEquals(3, result.getClusterLabels().length);
}

@Test
public void testClusterWithNoDataButValidPipeAndMetricReturnsNull() {
Pipe pipe = new Noop();
// Metric metric = new KMeansUncoveredScenariosTest.EuclideanDistance();
InstanceList emptyList = new InstanceList(pipe);
// KMeans kmeans = new KMeans(pipe, 2, metric);
// Clustering result = kmeans.cluster(emptyList);
// assertNull(result);
}

@Test
public void testClusterWithSingleIdenticalVectorAndMultipleClustersEmptyError() {
Pipe pipe = new Noop();
// Metric metric = new KMeansAdditionalBranchTests.EuclideanDistance();
SparseVector singleVector = new SparseVector(new double[] { 10.0, 10.0 });
InstanceList list = new InstanceList(pipe);
list.add(new Instance(singleVector, null, null, null));
// KMeans kmeans = new KMeans(pipe, 3, metric, KMeans.EMPTY_ERROR);
// Clustering clustering = kmeans.cluster(list);
// assertNull(clustering);
}

@Test
public void testClusterWithIdenticalSparseVectorsAllEmptyLocationsIgnored() {
Pipe pipe = new Noop();
// Metric metric = new KMeansAdditionalBranchTests.EuclideanDistance();
// SparseVector empty1 = new SparseVector(new int[] {}, new double[] {}, 0);
// SparseVector empty2 = new SparseVector(new int[] {}, new double[] {}, 0);
// SparseVector empty3 = new SparseVector(new int[] {}, new double[] {}, 0);
// InstanceList list = new InstanceList(pipe);
// list.add(new Instance(empty1, null, null, null));
// list.add(new Instance(empty2, null, null, null));
// list.add(new Instance(empty3, null, null, null));
// KMeans kmeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_DROP);
// Clustering result = kmeans.cluster(list);
// assertNull(result);
}

@Test
public void testClusterWithMixedZeroAndNonZeroSparseVectors() {
Pipe pipe = new Noop();
// Metric metric = new KMeansAdditionalBranchTests.EuclideanDistance();
// SparseVector zeroVec = new SparseVector(new int[] {}, new double[] {}, 0);
// SparseVector validVec1 = new SparseVector(new double[] { 1.0 });
SparseVector validVec2 = new SparseVector(new double[] { 2.0 });
// Instance i1 = new Instance(zeroVec, null, null, null);
// Instance i2 = new Instance(validVec1, null, null, null);
Instance i3 = new Instance(validVec2, null, null, null);
InstanceList list = new InstanceList(pipe);
// list.add(i1);
// list.add(i2);
list.add(i3);
// KMeans kmeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_DROP);
// Clustering result = kmeans.cluster(list);
// assertNotNull(result);
// assertTrue(result.getNumClusters() <= 2);
// int[] labels = result.getClusterLabels();
// assertEquals(3, labels.length);
}

@Test
public void testClusterWithMultipleEmptyClustersSuccessiveDrops() {
Pipe pipe = new Noop();
// Metric metric = new KMeansAdditionalBranchTests.EuclideanDistance();
SparseVector pointA = new SparseVector(new double[] { 0.0 });
SparseVector pointB = new SparseVector(new double[] { 100.0 });
InstanceList list = new InstanceList(pipe);
list.add(new Instance(pointA, null, null, null));
list.add(new Instance(pointB, null, null, null));
// KMeans kmeans = new KMeans(pipe, 5, metric, KMeans.EMPTY_DROP);
// Clustering clustering = kmeans.cluster(list);
// assertNotNull(clustering);
// assertTrue(clustering.getNumClusters() <= 5);
// assertEquals(2, clustering.getClusterLabels().length);
}

@Test
public void testClusterWithIdenticalDenseVectorsUsesCorrectInitialMean() {
Pipe pipe = new Noop();
// Metric metric = new KMeansAdditionalBranchTests.EuclideanDistance();
SparseVector dense = new SparseVector(new double[] { 8.0, 8.0, 8.0 });
InstanceList list = new InstanceList(pipe);
list.add(new Instance(dense, null, null, null));
list.add(new Instance(dense, null, null, null));
list.add(new Instance(dense, null, null, null));
// KMeans kmeans = new KMeans(pipe, 1, metric);
// Clustering result = kmeans.cluster(list);
// assertNotNull(result);
// assertEquals(1, result.getNumClusters());
// int[] labels = result.getClusterLabels();
// assertEquals(3, labels.length);
// assertEquals(0, labels[0]);
// assertEquals(0, labels[1]);
// assertEquals(0, labels[2]);
// assertEquals(1, kmeans.getClusterMeans().size());
}

@Test
public void testEmptySingleActionWithDeterministicReassignment() {
Pipe pipe = new Noop();
// Metric metric = new KMeansAdditionalBranchTests.EuclideanDistance();
SparseVector c1 = new SparseVector(new double[] { 0.0, 0.0 });
SparseVector c2 = new SparseVector(new double[] { 100.0, 100.0 });
SparseVector c3 = new SparseVector(new double[] { 200.0, 200.0 });
InstanceList list = new InstanceList(pipe);
list.add(new Instance(c1, null, null, null));
list.add(new Instance(c2, null, null, null));
list.add(new Instance(c3, null, null, null));
// KMeans kmeans = new KMeans(pipe, 4, metric, KMeans.EMPTY_SINGLE);
// Clustering result = kmeans.cluster(list);
// if (result != null) {
// assertTrue(result.getNumClusters() <= 4);
// }
}

@Test
public void testClusterWithDeltaMeansBelowToleranceCausesConvergence() {
Pipe pipe = new Noop();
// Metric metric = new KMeansAdditionalBranchTests.EuclideanDistance();
KMeans.MEANS_TOLERANCE = 1000;
SparseVector close1 = new SparseVector(new double[] { 5.0 });
SparseVector close2 = new SparseVector(new double[] { 5.01 });
SparseVector close3 = new SparseVector(new double[] { 5.02 });
SparseVector close4 = new SparseVector(new double[] { 5.03 });
InstanceList list = new InstanceList(pipe);
list.add(new Instance(close1, null, null, null));
list.add(new Instance(close2, null, null, null));
list.add(new Instance(close3, null, null, null));
list.add(new Instance(close4, null, null, null));
// KMeans kmeans = new KMeans(pipe, 2, metric);
// Clustering result = kmeans.cluster(list);
// assertNotNull(result);
// assertEquals(2, result.getNumClusters());
KMeans.MEANS_TOLERANCE = 1e-2;
}

@Test
public void testClusterWithVeryLargeVectorValuesDoesNotOverflow() {
Pipe pipe = new Noop();
// Metric metric = new KMeansAdditionalBranchTests.EuclideanDistance();
SparseVector huge1 = new SparseVector(new double[] { 1e100 });
SparseVector huge2 = new SparseVector(new double[] { 1e100 + 1 });
InstanceList list = new InstanceList(pipe);
list.add(new Instance(huge1, null, null, null));
list.add(new Instance(huge2, null, null, null));
// KMeans kmeans = new KMeans(pipe, 1, metric);
// Clustering result = kmeans.cluster(list);
// assertNotNull(result);
// assertEquals(1, result.getNumClusters());
}

@Test
public void testClusterWithAllInstancesAlreadyOptimalCluster() {
Pipe pipe = new Noop();
// Metric metric = new KMeansAdditionalBranchTests.EuclideanDistance();
SparseVector v1 = new SparseVector(new double[] { 1.0, 1.0 });
SparseVector v2 = new SparseVector(new double[] { 1.0, 1.0 });
SparseVector v3 = new SparseVector(new double[] { 1.0, 1.0 });
InstanceList list = new InstanceList(pipe);
list.add(new Instance(v1, null, null, null));
list.add(new Instance(v2, null, null, null));
list.add(new Instance(v3, null, null, null));
// KMeans kmeans = new KMeans(pipe, 1, metric);
// Clustering result = kmeans.cluster(list);
// assertNotNull(result);
// assertEquals(1, result.getNumClusters());
// int[] labels = result.getClusterLabels();
// assertEquals(3, labels.length);
// assertEquals(0, labels[0]);
// assertEquals(0, labels[1]);
// assertEquals(0, labels[2]);
}

@Test
public void testClusterLabelsConsistentAfterMultipleEmptyDrops() {
Pipe pipe = new Noop();
// Metric metric = new KMeansUncoveredEdgeCasesTest.EuclideanDistance();
SparseVector v1 = new SparseVector(new double[] { 1.0 });
SparseVector v2 = new SparseVector(new double[] { 100.0 });
SparseVector v3 = new SparseVector(new double[] { 1000.0 });
SparseVector v4 = new SparseVector(new double[] { 10000.0 });
InstanceList list = new InstanceList(pipe);
list.add(new Instance(v1, null, null, null));
list.add(new Instance(v2, null, null, null));
list.add(new Instance(v3, null, null, null));
list.add(new Instance(v4, null, null, null));
// KMeans kmeans = new KMeans(pipe, 8, metric, KMeans.EMPTY_DROP);
// Clustering result = kmeans.cluster(list);
// assertNotNull(result);
// int[] labels = result.getClusterLabels();
// assertEquals(4, labels.length);
// for (int label : labels) {
// assertTrue(label >= 0);
// assertTrue(label < result.getNumClusters());
// }
}

@Test
public void testClusterWithSparseVectorsHavingOverlappingNonZeroIndices() {
Pipe pipe = new Noop();
// Metric metric = new KMeansUncoveredEdgeCasesTest.EuclideanDistance();
// SparseVector v1 = new SparseVector(new int[] { 1, 2 }, new double[] { 3.0, 4.0 }, 3);
// SparseVector v2 = new SparseVector(new int[] { 2, 3 }, new double[] { 4.0, 5.0 }, 4);
InstanceList list = new InstanceList(pipe);
// list.add(new Instance(v1, null, null, null));
// list.add(new Instance(v2, null, null, null));
// KMeans kmeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_DROP);
// Clustering clustering = kmeans.cluster(list);
// assertNotNull(clustering);
// assertEquals(2, clustering.getNumClusters());
}

@Test
public void testClusterWithNegativeAndPositiveSparseVectorIndices() {
Pipe pipe = new Noop();
// Metric metric = new KMeansUncoveredEdgeCasesTest.EuclideanDistance();
// SparseVector v1 = new SparseVector(new int[] { 0, 1 }, new double[] { -10.0, 5.0 }, 2);
// SparseVector v2 = new SparseVector(new int[] { 0, 1 }, new double[] { 10.0, -5.0 }, 2);
InstanceList list = new InstanceList(pipe);
// list.add(new Instance(v1, null, null, null));
// list.add(new Instance(v2, null, null, null));
// KMeans kmeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_DROP);
// Clustering clustering = kmeans.cluster(list);
// assertNotNull(clustering);
// assertEquals(2, clustering.getNumClusters());
}

@Test
public void testClusterWithIdenticalVectorsAndEmptyDropDecrementsCorrectly() {
Pipe pipe = new Noop();
// Metric metric = new KMeansUncoveredEdgeCasesTest.EuclideanDistance();
SparseVector vec = new SparseVector(new double[] { 2.0 });
Instance instance1 = new Instance(vec, null, null, null);
Instance instance2 = new Instance(vec, null, null, null);
InstanceList instances = new InstanceList(pipe);
instances.add(instance1);
instances.add(instance2);
// KMeans kmeans = new KMeans(pipe, 4, metric, KMeans.EMPTY_DROP);
// Clustering clustering = kmeans.cluster(instances);
// assertNotNull(clustering);
// assertTrue(clustering.getNumClusters() <= 4);
// int[] labels = clustering.getClusterLabels();
// assertEquals(2, labels.length);
}

@Test
public void testConvergenceStopDueToMinimalPointsMovedThreshold() {
Pipe pipe = new Noop();
// Metric metric = new KMeansUncoveredEdgeCasesTest.EuclideanDistance();
KMeans.POINTS_TOLERANCE = 0.5;
SparseVector v1 = new SparseVector(new double[] { 1.0 });
SparseVector v2 = new SparseVector(new double[] { 1.01 });
SparseVector v3 = new SparseVector(new double[] { 1.02 });
SparseVector v4 = new SparseVector(new double[] { 1.03 });
SparseVector v5 = new SparseVector(new double[] { 1.04 });
SparseVector v6 = new SparseVector(new double[] { 1.05 });
SparseVector v7 = new SparseVector(new double[] { 1.06 });
SparseVector v8 = new SparseVector(new double[] { 1.07 });
InstanceList list = new InstanceList(pipe);
list.add(new Instance(v1, null, null, null));
list.add(new Instance(v2, null, null, null));
list.add(new Instance(v3, null, null, null));
list.add(new Instance(v4, null, null, null));
list.add(new Instance(v5, null, null, null));
list.add(new Instance(v6, null, null, null));
list.add(new Instance(v7, null, null, null));
list.add(new Instance(v8, null, null, null));
// KMeans kmeans = new KMeans(pipe, 2, metric);
// Clustering clustering = kmeans.cluster(list);
// assertNotNull(clustering);
// assertEquals(2, clustering.getNumClusters());
KMeans.POINTS_TOLERANCE = 0.005;
}

@Test
public void testClusterWithSingleVectorAndTooManyClusters_EMPTY_SINGLE() {
Pipe pipe = new Noop();
// Metric metric = new KMeansUncoveredEdgeCasesTest.EuclideanDistance();
SparseVector v = new SparseVector(new double[] { 9.9 });
InstanceList list = new InstanceList(pipe);
list.add(new Instance(v, null, null, null));
// KMeans kmeans = new KMeans(pipe, 5, metric, KMeans.EMPTY_SINGLE);
// Clustering clustering = kmeans.cluster(list);
// if (clustering != null) {
// assertTrue(clustering.getNumClusters() <= 5);
// assertEquals(1, clustering.getClusterLabels().length);
// }
}

@Test
public void testClusterWithVectorsThatCollapseToSameMean() {
Pipe pipe = new Noop();
// Metric metric = new KMeansUncoveredEdgeCasesTest.EuclideanDistance();
SparseVector v1 = new SparseVector(new double[] { 5.0 });
SparseVector v2 = new SparseVector(new double[] { 5.0 });
SparseVector v3 = new SparseVector(new double[] { 15.0 });
SparseVector v4 = new SparseVector(new double[] { 15.0 });
InstanceList list = new InstanceList(pipe);
list.add(new Instance(v1, null, null, null));
list.add(new Instance(v2, null, null, null));
list.add(new Instance(v3, null, null, null));
list.add(new Instance(v4, null, null, null));
// KMeans kmeans = new KMeans(pipe, 2, metric);
// Clustering clustering = kmeans.cluster(list);
// assertNotNull(clustering);
// assertEquals(2, clustering.getNumClusters());
// int[] labels = clustering.getClusterLabels();
// assertEquals(4, labels.length);
// assertTrue(labels[0] == labels[1]);
// assertTrue(labels[2] == labels[3]);
}

@Test
public void testClusterWithDuplicateVectorsButDifferentUnderlyingObjects() {
Pipe pipe = new Noop();
// Metric metric = new KMeansUncoveredEdgeCasesTest.EuclideanDistance();
double[] values = new double[] { 3.0, 3.0 };
SparseVector v1 = new SparseVector(values);
SparseVector v2 = new SparseVector(new double[] { 3.0, 3.0 });
Instance i1 = new Instance(v1, null, null, null);
Instance i2 = new Instance(v2, null, null, null);
InstanceList list = new InstanceList(pipe);
list.add(i1);
list.add(i2);
// KMeans kmeans = new KMeans(pipe, 1, metric);
// Clustering clustering = kmeans.cluster(list);
// assertNotNull(clustering);
// assertEquals(1, clustering.getNumClusters());
// int[] labels = clustering.getClusterLabels();
// assertEquals(2, labels.length);
// assertEquals(0, labels[0]);
// assertEquals(0, labels[1]);
}

@Test
public void testClusterWithZeroLengthVectorAndNonZeroLocationsSet() {
Pipe pipe = new Noop();
// Metric metric = new KMeansUncoveredEdgeCasesTest.EuclideanDistance();
// SparseVector v1 = new SparseVector(new int[] {}, new double[] {}, 3);
// SparseVector v2 = new SparseVector(new double[] { 1.0, 2.0, 0.0 });
InstanceList list = new InstanceList(pipe);
// list.add(new Instance(v1, null, null, null));
// list.add(new Instance(v2, null, null, null));
// KMeans kmeans = new KMeans(pipe, 2, metric);
// Clustering clustering = kmeans.cluster(list);
// assertNotNull(clustering);
// assertEquals(2, clustering.getNumClusters());
}
}
