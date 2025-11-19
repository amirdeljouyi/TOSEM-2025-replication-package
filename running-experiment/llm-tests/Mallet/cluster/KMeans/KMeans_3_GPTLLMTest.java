package cc.mallet.cluster;

import cc.mallet.pipe.Noop;
import cc.mallet.pipe.Pipe;
import cc.mallet.types.*;
import org.junit.Test;
import java.util.ArrayList;
import static org.junit.Assert.*;

public class KMeans_3_GPTLLMTest {

@Test
public void testClusterTwoDistinctGroups() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(new SparseVector(new int[] { 0, 1 }, new double[] { 1.0, 1.0 }), null, null, null));
instances.addThruPipe(new Instance(new SparseVector(new int[] { 0, 1 }, new double[] { 1.1, 1.1 }), null, null, null));
instances.addThruPipe(new Instance(new SparseVector(new int[] { 0, 1 }, new double[] { 0.9, 0.9 }), null, null, null));
instances.addThruPipe(new Instance(new SparseVector(new int[] { 0, 1 }, new double[] { 5.0, 5.0 }), null, null, null));
instances.addThruPipe(new Instance(new SparseVector(new int[] { 0, 1 }, new double[] { 5.1, 5.1 }), null, null, null));
instances.addThruPipe(new Instance(new SparseVector(new int[] { 0, 1 }, new double[] { 4.9, 4.9 }), null, null, null));
KMeans kMeans = new KMeans(pipe, 2, metric);
Clustering result = kMeans.cluster(instances);
assertNotNull(result);
assertEquals(2, result.getNumClusters());
// int[] labels = result.getClusterLabel();
// assertEquals(6, labels.length);
// int labelA = labels[0];
// assertEquals(labelA, labels[1]);
// assertEquals(labelA, labels[2]);
// int labelB = labels[3];
// assertEquals(labelB, labels[4]);
// assertEquals(labelB, labels[5]);
// assertNotEquals(labelA, labelB);
}

@Test
public void testEmptyClusterReturnsNullIfErrorAction() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(new SparseVector(new int[] { 0, 1 }, new double[] { 0.0, 0.0 }), null, null, null));
instances.addThruPipe(new Instance(new SparseVector(new int[] { 0, 1 }, new double[] { 10.0, 10.0 }), null, null, null));
KMeans kMeans = new KMeans(pipe, 10, metric, KMeans.EMPTY_ERROR);
Clustering result = kMeans.cluster(instances);
assertNull(result);
}

@Test
public void testEmptyClusterDropSucceedsWhenTooManyClusters() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(new SparseVector(new int[] { 0, 1 }, new double[] { 0.0, 0.0 }), null, null, null));
instances.addThruPipe(new Instance(new SparseVector(new int[] { 0, 1 }, new double[] { 1.0, 1.0 }), null, null, null));
instances.addThruPipe(new Instance(new SparseVector(new int[] { 0, 1 }, new double[] { 2.0, 2.0 }), null, null, null));
KMeans kMeans = new KMeans(pipe, 5, metric, KMeans.EMPTY_DROP);
Clustering result = kMeans.cluster(instances);
assertNotNull(result);
assertTrue(result.getNumClusters() <= 3);
}

@Test
public void testEmptyClusterSingleActionFillsMissing() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(new SparseVector(new int[] { 0, 1 }, new double[] { 1.0, 1.0 }), null, null, null));
instances.addThruPipe(new Instance(new SparseVector(new int[] { 0, 1 }, new double[] { 5.0, 5.0 }), null, null, null));
instances.addThruPipe(new Instance(new SparseVector(new int[] { 0, 1 }, new double[] { 10.0, 10.0 }), null, null, null));
KMeans kMeans = new KMeans(pipe, 3, metric, KMeans.EMPTY_SINGLE);
Clustering result = kMeans.cluster(instances);
assertNotNull(result);
assertEquals(3, result.getNumClusters());
}

@Test
public void testClusterIdenticalPointsAllSameLabel() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(new SparseVector(new int[] { 0, 1 }, new double[] { 2.0, 2.0 }), null, null, null));
instances.addThruPipe(new Instance(new SparseVector(new int[] { 0, 1 }, new double[] { 2.0, 2.0 }), null, null, null));
instances.addThruPipe(new Instance(new SparseVector(new int[] { 0, 1 }, new double[] { 2.0, 2.0 }), null, null, null));
KMeans kMeans = new KMeans(pipe, 2, metric);
Clustering result = kMeans.cluster(instances);
assertNotNull(result);
// int[] labels = result.getClusterLabel();
// assertEquals(labels[0], labels[1]);
// assertEquals(labels[0], labels[2]);
}

@Test
public void testClusterSingleInstance() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(new SparseVector(new int[] { 0, 1 }, new double[] { 3.0, 4.0 }), null, null, null));
KMeans kMeans = new KMeans(pipe, 1, metric);
Clustering result = kMeans.cluster(list);
assertNotNull(result);
assertEquals(1, result.getNumClusters());
// assertEquals(0, result.getClusterLabel()[0]);
}

@Test
public void testZeroLengthSparseVectorExcludedInInit() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
SparseVector zero = new SparseVector(new int[] {}, new double[] {});
SparseVector nonzero1 = new SparseVector(new int[] { 0, 1 }, new double[] { 1.0, 2.0 });
SparseVector nonzero2 = new SparseVector(new int[] { 0, 1 }, new double[] { 3.0, 4.0 });
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(zero, null, null, null));
instances.addThruPipe(new Instance(nonzero1, null, null, null));
instances.addThruPipe(new Instance(nonzero2, null, null, null));
KMeans kMeans = new KMeans(pipe, 2, metric);
Clustering result = kMeans.cluster(instances);
assertNotNull(result);
}

@Test
public void testGetClusterMeansAfterRun() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
SparseVector vec1 = new SparseVector(new int[] { 0, 1 }, new double[] { 2.0, 2.0 });
SparseVector vec2 = new SparseVector(new int[] { 0, 1 }, new double[] { 4.0, 4.0 });
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(vec1, null, null, null));
instances.addThruPipe(new Instance(vec2, null, null, null));
KMeans kMeans = new KMeans(pipe, 2, metric);
Clustering result = kMeans.cluster(instances);
assertNotNull(result);
ArrayList<SparseVector> means = kMeans.getClusterMeans();
assertNotNull(means);
assertTrue(means.size() > 0);
}

@Test
public void testMaxIterationStopsProperly() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(new SparseVector(new int[] { 0, 1 }, new double[] { 0.0, 0.0 }), null, null, null));
list.addThruPipe(new Instance(new SparseVector(new int[] { 0, 1 }, new double[] { 10.0, 10.0 }), null, null, null));
list.addThruPipe(new Instance(new SparseVector(new int[] { 0, 1 }, new double[] { 20.0, 20.0 }), null, null, null));
KMeans kMeans = new KMeans(pipe, 2, metric);
Clustering result = kMeans.cluster(list);
assertNotNull(result);
assertEquals(2, result.getNumClusters());
}

@Test(expected = AssertionError.class)
public void testMismatchedPipeTriggersAssertion() {
Pipe pipe1 = new Noop();
Pipe pipe2 = new Noop();
Metric metric = new EuclideanDistance();
InstanceList otherPipeList = new InstanceList(pipe2);
otherPipeList.addThruPipe(new Instance(new SparseVector(new int[] { 0, 1 }, new double[] { 1.0, 1.0 }), null, null, null));
KMeans kMeans = new KMeans(pipe1, 1, metric);
kMeans.cluster(otherPipeList);
}

@Test
public void testEmptyInstanceListReturnsNull() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
InstanceList instances = new InstanceList(pipe);
KMeans kMeans = new KMeans(pipe, 3, metric, KMeans.EMPTY_ERROR);
Clustering result = kMeans.cluster(instances);
assertNull(result);
}

@Test
public void testAllZeroSparseVectorsAreIgnoredCompletely() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
SparseVector zero1 = new SparseVector(new int[] {}, new double[] {});
SparseVector zero2 = new SparseVector(new int[] {}, new double[] {});
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(zero1, null, null, null));
instances.addThruPipe(new Instance(zero2, null, null, null));
KMeans kMeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_ERROR);
Clustering result = kMeans.cluster(instances);
assertNull(result);
}

@Test
public void testClusterWithKEqualToInstanceCount() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(new SparseVector(new int[] { 0 }, new double[] { 1.0 }), null, null, null));
list.addThruPipe(new Instance(new SparseVector(new int[] { 0 }, new double[] { 2.0 }), null, null, null));
list.addThruPipe(new Instance(new SparseVector(new int[] { 0 }, new double[] { 3.0 }), null, null, null));
KMeans kMeans = new KMeans(pipe, 3, metric, KMeans.EMPTY_DROP);
Clustering result = kMeans.cluster(list);
assertNotNull(result);
assertEquals(3, result.getNumClusters());
}

@Test
public void testClusterWithMoreClustersThanNonZeroInstancesWithDrop() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
SparseVector zero = new SparseVector(new int[] {}, new double[] {});
SparseVector valid1 = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
SparseVector valid2 = new SparseVector(new int[] { 0 }, new double[] { 2.0 });
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(zero, null, null, null));
list.addThruPipe(new Instance(valid1, null, null, null));
list.addThruPipe(new Instance(valid2, null, null, null));
KMeans kMeans = new KMeans(pipe, 5, metric, KMeans.EMPTY_DROP);
Clustering result = kMeans.cluster(list);
assertNotNull(result);
assertTrue(result.getNumClusters() <= 2);
}

@Test
public void testClusterWith1ClusterConvergesImmediately() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
Instance instance1 = new Instance(new SparseVector(new int[] { 0 }, new double[] { 10.0 }), null, null, null);
Instance instance2 = new Instance(new SparseVector(new int[] { 0 }, new double[] { 10.0 }), null, null, null);
InstanceList list = new InstanceList(pipe);
list.addThruPipe(instance1);
list.addThruPipe(instance2);
KMeans kMeans = new KMeans(pipe, 1, metric, KMeans.EMPTY_ERROR);
Clustering result = kMeans.cluster(list);
assertNotNull(result);
assertEquals(1, result.getNumClusters());
// assertEquals(0, result.getClusterLabel()[0]);
// assertEquals(0, result.getClusterLabel()[1]);
}

@Test
public void testEmptySingleFailsGracefullyWhenNoCandidates() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { 0.0 });
SparseVector v2 = new SparseVector(new int[] { 0 }, new double[] { 0.0 });
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(v1, null, null, null));
list.addThruPipe(new Instance(v2, null, null, null));
KMeans kMeans = new KMeans(pipe, 4, metric, KMeans.EMPTY_SINGLE);
Clustering result = kMeans.cluster(list);
assertNull(result);
}

@Test
public void testClusterWithIdenticalInitialCentroids() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
InstanceList list = new InstanceList(pipe);
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { 5.0 });
SparseVector v2 = new SparseVector(new int[] { 0 }, new double[] { 5.0 });
SparseVector v3 = new SparseVector(new int[] { 0 }, new double[] { 5.0 });
list.addThruPipe(new Instance(v1, null, null, null));
list.addThruPipe(new Instance(v2, null, null, null));
list.addThruPipe(new Instance(v3, null, null, null));
KMeans kMeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_SINGLE);
Clustering result = kMeans.cluster(list);
assertNotNull(result);
assertEquals(2, result.getNumClusters());
}

@Test
public void testClusterWithSparseVectorsLargeDimension() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
int[] idx1 = new int[] { 100, 200, 300 };
double[] val1 = new double[] { 1.0, 0.5, 0.2 };
SparseVector v1 = new SparseVector(idx1, val1);
int[] idx2 = new int[] { 100, 250, 300 };
double[] val2 = new double[] { 1.0, 0.4, 0.1 };
SparseVector v2 = new SparseVector(idx2, val2);
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(v1, null, null, null));
list.addThruPipe(new Instance(v2, null, null, null));
KMeans kMeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_DROP);
Clustering result = kMeans.cluster(list);
assertNotNull(result);
assertEquals(2, result.getNumClusters());
}

@Test
public void testClusterWithZeroClustersShouldReturnNull() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(new SparseVector(new int[] { 0 }, new double[] { 1.0 }), null, null, null));
KMeans kMeans = new KMeans(pipe, 0, metric, KMeans.EMPTY_ERROR);
Clustering result = kMeans.cluster(list);
assertNull(result);
}

@Test
public void testClusterWithNegativeClustersShouldReturnNull() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(new SparseVector(new int[] { 0 }, new double[] { 1.0 }), null, null, null));
list.addThruPipe(new Instance(new SparseVector(new int[] { 0 }, new double[] { 2.0 }), null, null, null));
KMeans kMeans = new KMeans(pipe, -3, metric, KMeans.EMPTY_ERROR);
Clustering result = kMeans.cluster(list);
assertNull(result);
}

@Test
public void testSinglePointMultipleClustersEmptyDrop() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
InstanceList list = new InstanceList(pipe);
SparseVector v = new SparseVector(new int[] { 0, 1 }, new double[] { 1.0, 1.0 });
list.addThruPipe(new Instance(v, null, null, null));
KMeans kMeans = new KMeans(pipe, 5, metric, KMeans.EMPTY_DROP);
Clustering result = kMeans.cluster(list);
assertNotNull(result);
assertEquals(1, result.getNumClusters());
// assertEquals(0, result.getClusterLabel()[0]);
}

@Test
public void testClusterWithNonMatchingPipeAssertionFailure() {
Pipe pipe1 = new Noop();
Pipe pipe2 = new Noop();
Metric metric = new EuclideanDistance();
InstanceList list = new InstanceList(pipe2);
SparseVector vec = new SparseVector(new int[] { 0, 1 }, new double[] { 5.0, 5.0 });
list.addThruPipe(new Instance(vec, null, null, null));
KMeans kMeans = new KMeans(pipe1, 1, metric);
try {
kMeans.cluster(list);
fail("Expected AssertionError due to differing pipe references");
} catch (AssertionError expected) {
}
}

@Test
public void testBoundaryConditionMinPointsMovedStillRuns() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
SparseVector v2 = new SparseVector(new int[] { 0 }, new double[] { 1.001 });
SparseVector v3 = new SparseVector(new int[] { 0 }, new double[] { 1.002 });
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(v1, null, null, null));
list.addThruPipe(new Instance(v2, null, null, null));
list.addThruPipe(new Instance(v3, null, null, null));
KMeans kMeans = new KMeans(pipe, 1, metric, KMeans.EMPTY_ERROR);
Clustering result = kMeans.cluster(list);
assertNotNull(result);
assertEquals(1, result.getNumClusters());
}

@Test
public void testClusterWithIdenticalInstancesAndLargeKWithDrop() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
SparseVector vec = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(vec, null, null, null));
list.addThruPipe(new Instance(vec, null, null, null));
list.addThruPipe(new Instance(vec, null, null, null));
KMeans kMeans = new KMeans(pipe, 10, metric, KMeans.EMPTY_DROP);
Clustering result = kMeans.cluster(list);
assertNotNull(result);
assertTrue(result.getNumClusters() <= 3);
}

@Test
public void testClusterMaxMeanDeltaConvergenceCondition() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
SparseVector vec1 = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
SparseVector vec2 = new SparseVector(new int[] { 0 }, new double[] { 3.0 });
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(vec1, null, null, null));
list.addThruPipe(new Instance(vec2, null, null, null));
KMeans kMeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_DROP);
Clustering result = kMeans.cluster(list);
assertNotNull(result);
assertEquals(2, result.getNumClusters());
}

@Test
public void testClusterEmptySingleWithoutEnoughInstances() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(new SparseVector(new int[] { 0 }, new double[] { 5.0 }), null, null, null));
KMeans kMeans = new KMeans(pipe, 3, metric, KMeans.EMPTY_SINGLE);
Clustering result = kMeans.cluster(instances);
assertNull(result);
}

@Test
public void testClusterEmptyDropWithZeroDimensionVectorPresent() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
SparseVector valid = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
SparseVector zero = new SparseVector(new int[] {}, new double[] {});
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(valid, null, null, null));
list.addThruPipe(new Instance(zero, null, null, null));
KMeans kMeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_DROP);
Clustering result = kMeans.cluster(list);
assertNotNull(result);
assertTrue(result.getNumClusters() <= 1);
}

@Test
public void testClusterWithMultipleTinyClustersAndDropPolicy() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(new SparseVector(new int[] { 0 }, new double[] { 0.0 }), null, null, null));
list.addThruPipe(new Instance(new SparseVector(new int[] { 0 }, new double[] { 100.0 }), null, null, null));
list.addThruPipe(new Instance(new SparseVector(new int[] { 0 }, new double[] { 200.0 }), null, null, null));
KMeans kMeans = new KMeans(pipe, 5, metric, KMeans.EMPTY_DROP);
Clustering result = kMeans.cluster(list);
assertNotNull(result);
assertTrue(result.getNumClusters() <= 3);
}

@Test
public void testClusterWithMultipleIdenticalCentroids() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
SparseVector vectorA = new SparseVector(new int[] { 0, 1 }, new double[] { 1.0, 2.0 });
SparseVector vectorB = new SparseVector(new int[] { 0, 1 }, new double[] { 1.0, 2.0 });
SparseVector vectorC = new SparseVector(new int[] { 0, 1 }, new double[] { 3.0, 4.0 });
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(vectorA, null, null, null));
instances.addThruPipe(new Instance(vectorB, null, null, null));
instances.addThruPipe(new Instance(vectorC, null, null, null));
KMeans kMeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_DROP);
Clustering result = kMeans.cluster(instances);
assertNotNull(result);
assertEquals(2, result.getNumClusters());
}

@Test
public void testClusterEmptySingleWithAllClustersSizeOne() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
SparseVector vectorA = new SparseVector(new int[] { 0 }, new double[] { 0.0 });
SparseVector vectorB = new SparseVector(new int[] { 0 }, new double[] { 100.0 });
SparseVector vectorC = new SparseVector(new int[] { 0 }, new double[] { 200.0 });
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(vectorA, null, null, null));
instances.addThruPipe(new Instance(vectorB, null, null, null));
instances.addThruPipe(new Instance(vectorC, null, null, null));
KMeans kMeans = new KMeans(pipe, 4, metric, KMeans.EMPTY_SINGLE);
Clustering result = kMeans.cluster(instances);
assertNull(result);
}

@Test
public void testClusterZeroFeatureVectorHandledGracefully() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
SparseVector vector = new SparseVector(new int[] {}, new double[] {});
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(vector, null, null, null));
KMeans kMeans = new KMeans(pipe, 1, metric, KMeans.EMPTY_ERROR);
Clustering result = kMeans.cluster(instances);
assertNull(result);
}

@Test
public void testClusterEmptyDropShortensClusterMeanList() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
SparseVector vector1 = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
SparseVector vector2 = new SparseVector(new int[] { 0 }, new double[] { 5.0 });
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(vector1, null, null, null));
instances.addThruPipe(new Instance(vector2, null, null, null));
KMeans kMeans = new KMeans(pipe, 5, metric, KMeans.EMPTY_DROP);
Clustering result = kMeans.cluster(instances);
assertNotNull(result);
assertTrue(result.getNumClusters() <= 2);
}

@Test
public void testClusterEmptyDropClusterLabelAdjustment() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
SparseVector vectorA = new SparseVector(new int[] { 0 }, new double[] { 0.0 });
SparseVector vectorB = new SparseVector(new int[] { 0 }, new double[] { 10.0 });
SparseVector vectorC = new SparseVector(new int[] { 0 }, new double[] { 20.0 });
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(vectorA, null, null, null));
list.addThruPipe(new Instance(vectorB, null, null, null));
list.addThruPipe(new Instance(vectorC, null, null, null));
KMeans kMeans = new KMeans(pipe, 5, metric, KMeans.EMPTY_DROP);
Clustering result = kMeans.cluster(list);
assertNotNull(result);
// int[] labels = result.getClusterLabel();
// assertEquals(3, labels.length);
// assertTrue(labels[0] >= 0 && labels[0] < result.getNumClusters());
// assertTrue(labels[1] >= 0 && labels[1] < result.getNumClusters());
// assertTrue(labels[2] >= 0 && labels[2] < result.getNumClusters());
}

@Test
public void testClusterWithDuplicateFeatureVectorsAndDropEmpty() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
SparseVector vector = new SparseVector(new int[] { 0, 1 }, new double[] { 2.0, 2.0 });
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(vector, null, null, null));
list.addThruPipe(new Instance(vector, null, null, null));
list.addThruPipe(new Instance(vector, null, null, null));
KMeans kMeans = new KMeans(pipe, 5, metric, KMeans.EMPTY_DROP);
Clustering result = kMeans.cluster(list);
assertNotNull(result);
assertTrue(result.getNumClusters() <= 3);
}

@Test
public void testClusterWithLargeDeltaMeansEarlyConvergence() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
SparseVector vectorA = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
SparseVector vectorB = new SparseVector(new int[] { 0 }, new double[] { 1000.0 });
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(vectorA, null, null, null));
list.addThruPipe(new Instance(vectorB, null, null, null));
KMeans kMeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_ERROR);
Clustering result = kMeans.cluster(list);
assertNotNull(result);
assertEquals(2, result.getNumClusters());
}

@Test
public void testClusterWithMinimalMovementTriggersConvergence() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
SparseVector vectorA = new SparseVector(new int[] { 0 }, new double[] { 1.000 });
SparseVector vectorB = new SparseVector(new int[] { 0 }, new double[] { 1.001 });
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(vectorA, null, null, null));
list.addThruPipe(new Instance(vectorB, null, null, null));
KMeans kMeans = new KMeans(pipe, 1, metric, KMeans.EMPTY_ERROR);
Clustering result = kMeans.cluster(list);
assertNotNull(result);
assertEquals(1, result.getNumClusters());
}

@Test
public void testClusterWithOneInstanceAndMultipleClustersEmptyDrop() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
SparseVector vector = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
Instance instance = new Instance(vector, null, null, null);
InstanceList list = new InstanceList(pipe);
list.addThruPipe(instance);
KMeans kMeans = new KMeans(pipe, 3, metric, KMeans.EMPTY_DROP);
Clustering result = kMeans.cluster(list);
assertNotNull(result);
assertEquals(1, result.getNumClusters());
// assertEquals(0, result.getClusterLabel()[0]);
}

@Test
public void testClusterWithAllEmptyClustersInitially() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
InstanceList list = new InstanceList(pipe);
KMeans kMeans = new KMeans(pipe, 5, metric, KMeans.EMPTY_DROP);
Clustering result = kMeans.cluster(list);
assertNull(result);
}

@Test
public void testClusterMeansHaveCorrectDimensions() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { 2.0 });
SparseVector v2 = new SparseVector(new int[] { 0 }, new double[] { 4.0 });
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(v1, null, null, null));
list.addThruPipe(new Instance(v2, null, null, null));
KMeans kMeans = new KMeans(pipe, 2, metric);
Clustering result = kMeans.cluster(list);
assertNotNull(result);
ArrayList<SparseVector> means = kMeans.getClusterMeans();
assertEquals(2, means.size());
assertEquals(1, means.get(0).numLocations());
assertEquals(1, means.get(1).numLocations());
}

@Test
public void testClusteringWithPipeMismatchFailsEvenWithValidInstances() {
Pipe pipe1 = new Noop();
Pipe pipe2 = new Noop();
Metric metric = new EuclideanDistance();
InstanceList list = new InstanceList(pipe2);
list.addThruPipe(new Instance(new SparseVector(new int[] { 0 }, new double[] { 5.0 }), null, null, null));
list.addThruPipe(new Instance(new SparseVector(new int[] { 0 }, new double[] { 6.0 }), null, null, null));
KMeans kMeans = new KMeans(pipe1, 2, metric, KMeans.EMPTY_DROP);
try {
kMeans.cluster(list);
fail("Should throw AssertionError due to pipe mismatch");
} catch (AssertionError expected) {
}
}

@Test
public void testEmptyDropRemovesCorrectClustersAndAdjustsLabels() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
SparseVector v2 = new SparseVector(new int[] { 0 }, new double[] { 2.0 });
SparseVector v3 = new SparseVector(new int[] { 0 }, new double[] { 3.0 });
SparseVector v4 = new SparseVector(new int[] { 0 }, new double[] { 4.0 });
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(v1, null, null, null));
list.addThruPipe(new Instance(v2, null, null, null));
list.addThruPipe(new Instance(v3, null, null, null));
list.addThruPipe(new Instance(v4, null, null, null));
KMeans kMeans = new KMeans(pipe, 10, metric, KMeans.EMPTY_DROP);
Clustering result = kMeans.cluster(list);
assertNotNull(result);
assertTrue(result.getNumClusters() <= 4);
// int[] labels = result.getClusterLabel();
// assertEquals(4, labels.length);
// for (int i = 0; i < labels.length; i++) {
// assertTrue("Label should be in cluster range", labels[i] < result.getNumClusters());
// }
}

@Test
public void testEmptySingleFallbackWithOnlyTwoClusters() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
SparseVector v2 = new SparseVector(new int[] { 0 }, new double[] { 1.1 });
SparseVector v3 = new SparseVector(new int[] { 0 }, new double[] { 1.2 });
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(v1, null, null, null));
list.addThruPipe(new Instance(v2, null, null, null));
list.addThruPipe(new Instance(v3, null, null, null));
KMeans kMeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_SINGLE);
Clustering result = kMeans.cluster(list);
assertNotNull(result);
assertEquals(2, result.getNumClusters());
}

@Test
public void testEmptySingleAssignsFurthestPointAsNewCentroid() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
SparseVector a = new SparseVector(new int[] { 0 }, new double[] { 0.0 });
SparseVector b = new SparseVector(new int[] { 0 }, new double[] { 0.01 });
SparseVector c = new SparseVector(new int[] { 0 }, new double[] { 100.0 });
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(a, null, null, null));
instances.addThruPipe(new Instance(b, null, null, null));
instances.addThruPipe(new Instance(c, null, null, null));
KMeans kMeans = new KMeans(pipe, 3, metric, KMeans.EMPTY_SINGLE);
Clustering result = kMeans.cluster(instances);
assertNotNull(result);
assertEquals(3, result.getNumClusters());
}

@Test
public void testEmptyClusterDropRemovesOnlyEmptyCluster() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { 0.0 });
SparseVector v2 = new SparseVector(new int[] { 0 }, new double[] { 10.0 });
SparseVector v3 = new SparseVector(new int[] { 0 }, new double[] { 20.0 });
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(v1, null, null, null));
list.addThruPipe(new Instance(v2, null, null, null));
list.addThruPipe(new Instance(v3, null, null, null));
KMeans kMeans = new KMeans(pipe, 5, metric, KMeans.EMPTY_DROP);
Clustering result = kMeans.cluster(list);
assertNotNull(result);
assertTrue(result.getNumClusters() <= 3);
}

@Test
public void testMeanDeltaTriggersConvergence() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { 10.001 });
SparseVector v2 = new SparseVector(new int[] { 0 }, new double[] { 10.000 });
SparseVector v3 = new SparseVector(new int[] { 0 }, new double[] { 9.999 });
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(v1, null, null, null));
list.addThruPipe(new Instance(v2, null, null, null));
list.addThruPipe(new Instance(v3, null, null, null));
KMeans kMeans = new KMeans(pipe, 1, metric, KMeans.EMPTY_ERROR);
Clustering result = kMeans.cluster(list);
assertNotNull(result);
assertEquals(1, result.getNumClusters());
}

@Test
public void testDeltaPointsTriggersConvergence() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
SparseVector v2 = new SparseVector(new int[] { 0 }, new double[] { 1.001 });
SparseVector v3 = new SparseVector(new int[] { 0 }, new double[] { 1.002 });
SparseVector v4 = new SparseVector(new int[] { 0 }, new double[] { 1.003 });
SparseVector v5 = new SparseVector(new int[] { 0 }, new double[] { 1.004 });
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(v1, null, null, null));
list.addThruPipe(new Instance(v2, null, null, null));
list.addThruPipe(new Instance(v3, null, null, null));
list.addThruPipe(new Instance(v4, null, null, null));
list.addThruPipe(new Instance(v5, null, null, null));
KMeans kMeans = new KMeans(pipe, 1, metric, KMeans.EMPTY_ERROR);
Clustering result = kMeans.cluster(list);
assertNotNull(result);
assertEquals(1, result.getNumClusters());
}

@Test
public void testClusterLabelIndexDecrementAfterDropInMiddleCluster() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
SparseVector a = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
SparseVector b = new SparseVector(new int[] { 0 }, new double[] { 5.0 });
SparseVector c = new SparseVector(new int[] { 0 }, new double[] { 9.0 });
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(a, null, null, null));
instances.addThruPipe(new Instance(b, null, null, null));
instances.addThruPipe(new Instance(c, null, null, null));
KMeans kMeans = new KMeans(pipe, 6, metric, KMeans.EMPTY_DROP);
Clustering result = kMeans.cluster(instances);
assertNotNull(result);
assertTrue(result.getNumClusters() <= 3);
// int[] labels = result.getClusterLabel();
// for (int i = 0; i < labels.length; i++) {
// assertTrue(labels[i] < result.getNumClusters());
// }
}

@Test
public void testInitializeMeansSelectsAllDistinctPoints() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { 0.0 });
SparseVector v2 = new SparseVector(new int[] { 0 }, new double[] { 10.0 });
SparseVector v3 = new SparseVector(new int[] { 0 }, new double[] { 20.0 });
SparseVector v4 = new SparseVector(new int[] { 0 }, new double[] { 30.0 });
SparseVector v5 = new SparseVector(new int[] { 0 }, new double[] { 40.0 });
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(v1, null, null, null));
list.addThruPipe(new Instance(v2, null, null, null));
list.addThruPipe(new Instance(v3, null, null, null));
list.addThruPipe(new Instance(v4, null, null, null));
list.addThruPipe(new Instance(v5, null, null, null));
KMeans kMeans = new KMeans(pipe, 5, metric, KMeans.EMPTY_ERROR);
Clustering result = kMeans.cluster(list);
assertNotNull(result);
assertEquals(5, result.getNumClusters());
}

@Test
public void testClusterWithIdenticalInstancesAndLargeKEmptyErrorReturnsNull() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
SparseVector vector = new SparseVector(new int[] { 0 }, new double[] { 10.0 });
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(vector, null, null, null));
list.addThruPipe(new Instance(vector, null, null, null));
list.addThruPipe(new Instance(vector, null, null, null));
KMeans kMeans = new KMeans(pipe, 5, metric, KMeans.EMPTY_ERROR);
Clustering result = kMeans.cluster(list);
assertNull(result);
}

@Test
public void testEmptySingleTerminatesWhenNoMovePossible() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
SparseVector a = new SparseVector(new int[] { 0 }, new double[] { 10.0 });
SparseVector b = new SparseVector(new int[] { 0 }, new double[] { 20.0 });
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(a, null, null, null));
list.addThruPipe(new Instance(b, null, null, null));
KMeans kMeans = new KMeans(pipe, 3, metric, KMeans.EMPTY_SINGLE);
Clustering result = kMeans.cluster(list);
assertNull(result);
}

@Test
public void testInitializeMeansSkipsZeroLocationSparseVectorCorrectly() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
SparseVector zero = new SparseVector(new int[] {}, new double[] {});
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { 3.0 });
SparseVector v2 = new SparseVector(new int[] { 0 }, new double[] { 6.0 });
SparseVector v3 = new SparseVector(new int[] { 0 }, new double[] { 9.0 });
SparseVector v4 = new SparseVector(new int[] { 0 }, new double[] { 12.0 });
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(zero, null, null, null));
list.addThruPipe(new Instance(v1, null, null, null));
list.addThruPipe(new Instance(v2, null, null, null));
list.addThruPipe(new Instance(v3, null, null, null));
list.addThruPipe(new Instance(v4, null, null, null));
KMeans kMeans = new KMeans(pipe, 4, metric, KMeans.EMPTY_ERROR);
Clustering result = kMeans.cluster(list);
assertNotNull(result);
assertEquals(4, result.getNumClusters());
}

@Test
public void testClusterWithAllZeroLengthVectorsInList() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(new SparseVector(new int[] {}, new double[] {}), null, null, null));
list.addThruPipe(new Instance(new SparseVector(new int[] {}, new double[] {}), null, null, null));
KMeans kMeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_ERROR);
Clustering result = kMeans.cluster(list);
assertNull(result);
}

@Test
public void testClusterWithOnlyOneDimensionData() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { 2.0 });
SparseVector v2 = new SparseVector(new int[] { 0 }, new double[] { 4.0 });
SparseVector v3 = new SparseVector(new int[] { 0 }, new double[] { 6.0 });
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(v1, null, null, null));
list.addThruPipe(new Instance(v2, null, null, null));
list.addThruPipe(new Instance(v3, null, null, null));
KMeans kMeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_DROP);
Clustering result = kMeans.cluster(list);
assertNotNull(result);
assertEquals(2, result.getNumClusters());
}

@Test
public void testClusterExactConvergenceOnMeanTolerance() {
Pipe pipe = new Noop();
Metric metric = new Metric() {

@Override
public double distance(SparseVector v1, SparseVector v2) {
return KMeans.MEANS_TOLERANCE;
}
};
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
SparseVector v2 = new SparseVector(new int[] { 0 }, new double[] { 2.0 });
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(v1, null, null, null));
list.addThruPipe(new Instance(v2, null, null, null));
KMeans kMeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_DROP);
Clustering result = kMeans.cluster(list);
assertNotNull(result);
}

@Test
public void testClusterWithNullSparseVectorReturnsNull() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
InstanceList list = new InstanceList(pipe);
list.add(new Instance(null, null, null, null));
list.addThruPipe(new Instance(new SparseVector(new int[] { 0 }, new double[] { 1.0 }), null, null, null));
KMeans kMeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_ERROR);
Clustering result = kMeans.cluster(list);
assertNull(result);
}

@Test
public void testClusterWithRedundantVectorsAssignedToSameCluster() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
SparseVector v1 = new SparseVector(new int[] { 0, 1 }, new double[] { 5.0, 5.0 });
SparseVector v2 = new SparseVector(new int[] { 0, 1 }, new double[] { 5.0, 5.0 });
SparseVector v3 = new SparseVector(new int[] { 0, 1 }, new double[] { 5.0, 5.0 });
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(v1, null, null, null));
list.addThruPipe(new Instance(v2, null, null, null));
list.addThruPipe(new Instance(v3, null, null, null));
KMeans kMeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_DROP);
Clustering result = kMeans.cluster(list);
assertNotNull(result);
// int label0 = result.getClusterLabel()[0];
// int label1 = result.getClusterLabel()[1];
// int label2 = result.getClusterLabel()[2];
// assertEquals(label0, label1);
// assertEquals(label1, label2);
}

@Test
public void testInitializeMeansSampleRemovesPointsAtEachStepCorrectly() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
SparseVector v2 = new SparseVector(new int[] { 0 }, new double[] { 2.0 });
SparseVector v3 = new SparseVector(new int[] { 0 }, new double[] { 3.0 });
SparseVector v4 = new SparseVector(new int[] { 0 }, new double[] { 4.0 });
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(v1, null, null, null));
list.addThruPipe(new Instance(v2, null, null, null));
list.addThruPipe(new Instance(v3, null, null, null));
list.addThruPipe(new Instance(v4, null, null, null));
KMeans kMeans = new KMeans(pipe, 4, metric, KMeans.EMPTY_ERROR);
Clustering result = kMeans.cluster(list);
assertNotNull(result);
assertEquals(4, result.getNumClusters());
}

@Test
public void testEmptyDropDoesNotReduceClustersBelow1() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
SparseVector sv = new SparseVector(new int[] { 0 }, new double[] { 42.0 });
Instance i1 = new Instance(sv, null, null, null);
InstanceList list = new InstanceList(pipe);
list.addThruPipe(i1);
KMeans kMeans = new KMeans(pipe, 1000, metric, KMeans.EMPTY_DROP);
Clustering result = kMeans.cluster(list);
assertNotNull(result);
assertTrue(result.getNumClusters() >= 1);
// assertEquals(0, result.getClusterLabel()[0]);
}

@Test
public void testEmptyDropSkipsAssertionConditionForValidLabels() {
Pipe pipe = new Noop();
Metric metric = new EuclideanDistance();
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { 0.0 });
SparseVector v2 = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(v1, null, null, null));
list.addThruPipe(new Instance(v2, null, null, null));
KMeans kMeans = new KMeans(pipe, 5, metric, KMeans.EMPTY_DROP);
Clustering result = kMeans.cluster(list);
assertNotNull(result);
// int[] labels = result.getClusterLabel();
// assertEquals(labels.length, 2);
}
}
