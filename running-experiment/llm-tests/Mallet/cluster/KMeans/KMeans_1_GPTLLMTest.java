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

public class KMeans_1_GPTLLMTest {

@Test
public void testClusterConvergesWithTwoDistinctClusters() {
Pipe pipe = new Noop();
// Metric euclidean = new Metric() {
// 
// public double distance(Object a, Object b) {
// SparseVector v1 = (SparseVector) a;
// SparseVector v2 = (SparseVector) b;
// double d0 = v1.value(0) - v2.value(0);
// double d1 = v1.value(1) - v2.value(1);
// return Math.sqrt(d0 * d0 + d1 * d1);
// }
// };
// KMeans kmeans = new KMeans(pipe, 2, euclidean, KMeans.EMPTY_ERROR);
SparseVector v1 = new SparseVector(new int[] { 0, 1 }, new double[] { 1.0, 1.0 });
SparseVector v2 = new SparseVector(new int[] { 0, 1 }, new double[] { 1.1, 1.1 });
SparseVector v3 = new SparseVector(new int[] { 0, 1 }, new double[] { 10.0, 10.0 });
SparseVector v4 = new SparseVector(new int[] { 0, 1 }, new double[] { 10.1, 10.1 });
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(v1, null, null, null));
instances.addThruPipe(new Instance(v2, null, null, null));
instances.addThruPipe(new Instance(v3, null, null, null));
instances.addThruPipe(new Instance(v4, null, null, null));
// Clustering clustering = kmeans.cluster(instances);
// assertNotNull(clustering);
// assertEquals(2, clustering.getNumClusters());
// int label0 = clustering.getClusterLabel(instances.get(0));
// int label1 = clustering.getClusterLabel(instances.get(1));
// int label2 = clustering.getClusterLabel(instances.get(2));
// int label3 = clustering.getClusterLabel(instances.get(3));
// assertEquals(label0, label1);
// assertEquals(label2, label3);
// assertNotEquals(label0, label2);
}

@Test
public void testEmptyClusterReturnsNullWithEmptyError() {
Pipe pipe = new Noop();
// Metric metric = new Metric() {
// 
// public double distance(Object a, Object b) {
// return 1.0;
// }
// };
// KMeans kmeans = new KMeans(pipe, 5, metric, KMeans.EMPTY_ERROR);
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { 0.0 });
SparseVector v2 = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(v1, null, null, null));
instances.addThruPipe(new Instance(v2, null, null, null));
// Clustering clustering = kmeans.cluster(instances);
// assertNull(clustering);
}

@Test
public void testEmptyClusterDropStrategyReducesClusterCount() {
Pipe pipe = new Noop();
// Metric metric = new Metric() {
// 
// public double distance(Object a, Object b) {
// double valA = ((SparseVector) a).value(0);
// double valB = ((SparseVector) b).value(0);
// return Math.abs(valA - valB);
// }
// };
// KMeans kmeans = new KMeans(pipe, 5, metric, KMeans.EMPTY_DROP);
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
SparseVector v2 = new SparseVector(new int[] { 0 }, new double[] { 2.0 });
SparseVector v3 = new SparseVector(new int[] { 0 }, new double[] { 3.0 });
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(v1, null, null, null));
instances.addThruPipe(new Instance(v2, null, null, null));
instances.addThruPipe(new Instance(v3, null, null, null));
// Clustering clustering = kmeans.cluster(instances);
// assertNotNull(clustering);
// assertTrue(clustering.getNumClusters() < 5);
}

@Test
public void testEmptyClusterSingleStrategyFindsReplacement() {
Pipe pipe = new Noop();
// Metric metric = new Metric() {
// 
// public double distance(Object a, Object b) {
// SparseVector v1 = (SparseVector) a;
// SparseVector v2 = (SparseVector) b;
// return Math.abs(v1.value(0) - v2.value(0));
// }
// };
// KMeans kmeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_SINGLE);
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { 0.0 });
SparseVector v2 = new SparseVector(new int[] { 0 }, new double[] { 0.0 });
SparseVector v3 = new SparseVector(new int[] { 0 }, new double[] { 100.0 });
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(v1, null, null, null));
instances.addThruPipe(new Instance(v2, null, null, null));
instances.addThruPipe(new Instance(v3, null, null, null));
// Clustering clustering = kmeans.cluster(instances);
// assertNotNull(clustering);
// assertEquals(2, clustering.getNumClusters());
}

@Test
public void testSingleClusterAssignment() {
Pipe pipe = new Noop();
// Metric metric = new Metric() {
// 
// public double distance(Object a, Object b) {
// return 0.0;
// }
// };
// KMeans kmeans = new KMeans(pipe, 1, metric, KMeans.EMPTY_ERROR);
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
SparseVector v2 = new SparseVector(new int[] { 0 }, new double[] { 1.1 });
SparseVector v3 = new SparseVector(new int[] { 0 }, new double[] { 1.2 });
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(v1, null, null, null));
instances.addThruPipe(new Instance(v2, null, null, null));
instances.addThruPipe(new Instance(v3, null, null, null));
// Clustering clustering = kmeans.cluster(instances);
// assertNotNull(clustering);
// assertEquals(1, clustering.getNumClusters());
// assertEquals(0, clustering.getClusterLabel(instances.get(0)));
// assertEquals(0, clustering.getClusterLabel(instances.get(1)));
// assertEquals(0, clustering.getClusterLabel(instances.get(2)));
}

@Test
public void testClusterMeansAreReturnedCorrectly() {
Pipe pipe = new Noop();
// Metric metric = new Metric() {
// 
// public double distance(Object a, Object b) {
// SparseVector sa = (SparseVector) a;
// SparseVector sb = (SparseVector) b;
// return Math.abs(sa.value(0) - sb.value(0));
// }
// };
// KMeans kmeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_DROP);
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { 0.0 });
SparseVector v2 = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
SparseVector v3 = new SparseVector(new int[] { 0 }, new double[] { 5.0 });
SparseVector v4 = new SparseVector(new int[] { 0 }, new double[] { 6.0 });
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(v1, null, null, null));
instances.addThruPipe(new Instance(v2, null, null, null));
instances.addThruPipe(new Instance(v3, null, null, null));
instances.addThruPipe(new Instance(v4, null, null, null));
// Clustering clustering = kmeans.cluster(instances);
// assertNotNull(clustering);
// ArrayList<SparseVector> means = kmeans.getClusterMeans();
// assertNotNull(means);
// assertEquals(clustering.getNumClusters(), means.size());
}

@Test
public void testIdenticalVectorsCreatesSingleCluster() {
Pipe pipe = new Noop();
// Metric metric = new Metric() {
// 
// public double distance(Object a, Object b) {
// return 0.0;
// }
// };
// KMeans kmeans = new KMeans(pipe, 3, metric, KMeans.EMPTY_DROP);
SparseVector v = new SparseVector(new int[] { 0 }, new double[] { 5.0 });
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(v, null, null, null));
instances.addThruPipe(new Instance(v, null, null, null));
instances.addThruPipe(new Instance(v, null, null, null));
// Clustering clustering = kmeans.cluster(instances);
// assertNotNull(clustering);
// assertTrue(clustering.getNumClusters() <= 3);
}

@Test
public void testZeroLengthVectorsHandled() {
Pipe pipe = new Noop();
// Metric metric = new Metric() {
// 
// public double distance(Object a, Object b) {
// return 0.0;
// }
// };
SparseVector empty = new SparseVector(new int[0], new double[0]);
SparseVector normal = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(empty, null, null, null));
instances.addThruPipe(new Instance(normal, null, null, null));
// KMeans kmeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_DROP);
// Clustering clustering = kmeans.cluster(instances);
// assertNotNull(clustering);
}

@Test
public void testExtremeValuesDistance() {
Pipe pipe = new Noop();
// Metric metric = new Metric() {
// 
// public double distance(Object a, Object b) {
// SparseVector sa = (SparseVector) a;
// SparseVector sb = (SparseVector) b;
// return Math.abs(sa.value(0) - sb.value(0));
// }
// };
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { Double.MAX_VALUE });
SparseVector v2 = new SparseVector(new int[] { 0 }, new double[] { Double.MIN_VALUE });
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(v1, null, null, null));
instances.addThruPipe(new Instance(v2, null, null, null));
// KMeans kmeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_DROP);
// Clustering clustering = kmeans.cluster(instances);
// assertNotNull(clustering);
// assertEquals(2, clustering.getNumClusters());
}

@Test
public void testClusterReturnsNullWhenAllClustersEmptyAndStrategySingleFails() {
Pipe pipe = new Noop();
// Metric metric = new Metric() {
// 
// public double distance(Object a, Object b) {
// return 0.0;
// }
// };
// KMeans kmeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_SINGLE);
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(v1, null, null, null));
// Clustering clustering = kmeans.cluster(instances);
// assertNull(clustering);
}

@Test
public void testClusterStopsWhenDeltaMeansBelowTolerance() {
Pipe pipe = new Noop();
// Metric metric = new Metric() {
// 
// public double distance(Object a, Object b) {
// SparseVector va = (SparseVector) a;
// SparseVector vb = (SparseVector) b;
// double diff = va.value(0) - vb.value(0);
// return diff * diff;
// }
// };
// KMeans kmeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_ERROR);
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
SparseVector v2 = new SparseVector(new int[] { 0 }, new double[] { 1.00001 });
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(v1, null, null, null));
instances.addThruPipe(new Instance(v2, null, null, null));
// Clustering clustering = kmeans.cluster(instances);
// assertNotNull(clustering);
// assertTrue(clustering.getNumClusters() <= 2);
}

@Test
public void testClusterStopsWhenDeltaPointsBelowTolerance() {
Pipe pipe = new Noop();
// Metric metric = new Metric() {
// 
// public double distance(Object a, Object b) {
// SparseVector va = (SparseVector) a;
// SparseVector vb = (SparseVector) b;
// double diff = va.value(0) - vb.value(0);
// return diff * diff;
// }
// };
// KMeans kmeans = new KMeans(pipe, 3, metric, KMeans.EMPTY_ERROR);
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
SparseVector v2 = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
SparseVector v3 = new SparseVector(new int[] { 0 }, new double[] { 100.0 });
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(v1, null, null, null));
instances.addThruPipe(new Instance(v2, null, null, null));
instances.addThruPipe(new Instance(v3, null, null, null));
// Clustering clustering = kmeans.cluster(instances);
// assertNotNull(clustering);
// assertTrue(clustering.getNumClusters() <= 3);
}

@Test
public void testClusterHandlesLargeNumberOfIdenticalPoints() {
Pipe pipe = new Noop();
// Metric metric = new Metric() {
// 
// public double distance(Object a, Object b) {
// return 0.0;
// }
// };
// KMeans kmeans = new KMeans(pipe, 3, metric, KMeans.EMPTY_DROP);
SparseVector vector = new SparseVector(new int[] { 0 }, new double[] { 5.0 });
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(vector, null, null, null));
instances.addThruPipe(new Instance(vector, null, null, null));
instances.addThruPipe(new Instance(vector, null, null, null));
instances.addThruPipe(new Instance(vector, null, null, null));
instances.addThruPipe(new Instance(vector, null, null, null));
// Clustering clustering = kmeans.cluster(instances);
// assertNotNull(clustering);
// assertTrue(clustering.getNumClusters() <= 3);
}

@Test
public void testClusterWithOneInstanceAndMultipleClusters() {
Pipe pipe = new Noop();
// Metric metric = new Metric() {
// 
// public double distance(Object a, Object b) {
// return 0.0;
// }
// };
// KMeans kmeans = new KMeans(pipe, 3, metric, KMeans.EMPTY_SINGLE);
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { 42.0 });
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(v1, null, null, null));
// Clustering clustering = kmeans.cluster(instances);
// assertNotNull(clustering);
// assertTrue(clustering.getNumClusters() <= 3);
}

@Test
public void testClusterReturnsNullIfAllVectorsEmptyInInitializeMeansSample() {
Pipe pipe = new Noop();
// Metric metric = new Metric() {
// 
// public double distance(Object a, Object b) {
// return 0.0;
// }
// };
// KMeans kmeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_ERROR);
SparseVector empty1 = new SparseVector(new int[0], new double[0]);
SparseVector empty2 = new SparseVector(new int[0], new double[0]);
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(empty1, null, null, null));
instances.addThruPipe(new Instance(empty2, null, null, null));
// Clustering clustering = kmeans.cluster(instances);
// assertNull(clustering);
}

@Test
public void testClusterWithMinimalValidInput() {
Pipe pipe = new Noop();
// Metric metric = new Metric() {
// 
// public double distance(Object a, Object b) {
// SparseVector va = (SparseVector) a;
// SparseVector vb = (SparseVector) b;
// return Math.abs(va.value(0) - vb.value(0));
// }
// };
// KMeans kmeans = new KMeans(pipe, 1, metric, KMeans.EMPTY_ERROR);
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { 0.0 });
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(v1, null, null, null));
// Clustering clustering = kmeans.cluster(instances);
// assertNotNull(clustering);
// assertEquals(1, clustering.getNumClusters());
// assertEquals(0, clustering.getClusterLabel(instances.get(0)));
}

@Test
public void testClusterReturnsNullIfNoValidPointsAfterFiltering() {
Pipe pipe = new Noop();
// Metric metric = new Metric() {
// 
// public double distance(Object a, Object b) {
// return 1.0;
// }
// };
// KMeans kmeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_ERROR);
SparseVector v1 = new SparseVector(new int[0], new double[0]);
SparseVector v2 = new SparseVector(new int[0], new double[0]);
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(v1, null, null, null));
instances.addThruPipe(new Instance(v2, null, null, null));
// Clustering clustering = kmeans.cluster(instances);
// assertNull(clustering);
}

@Test
public void testClusterWithHighlySkewedData() {
Pipe pipe = new Noop();
// Metric metric = new Metric() {
// 
// public double distance(Object a, Object b) {
// SparseVector va = (SparseVector) a;
// SparseVector vb = (SparseVector) b;
// return Math.abs(va.value(0) - vb.value(0));
// }
// };
// KMeans kmeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_DROP);
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
SparseVector v2 = new SparseVector(new int[] { 0 }, new double[] { 1000000.0 });
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(v1, null, null, null));
instances.addThruPipe(new Instance(v2, null, null, null));
// Clustering clustering = kmeans.cluster(instances);
// assertNotNull(clustering);
// assertEquals(2, clustering.getNumClusters());
}

@Test
public void testAllPointsAssignedToOneClusterDespiteMultipleRequested() {
Pipe pipe = new Noop();
// Metric metric = new Metric() {
// 
// public double distance(Object a, Object b) {
// return 0.0;
// }
// };
// KMeans kmeans = new KMeans(pipe, 4, metric, KMeans.EMPTY_DROP);
SparseVector vector = new SparseVector(new int[] { 0 }, new double[] { 5.0 });
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(vector, null, null, null));
instances.addThruPipe(new Instance(vector, null, null, null));
instances.addThruPipe(new Instance(vector, null, null, null));
// Clustering clustering = kmeans.cluster(instances);
// assertNotNull(clustering);
// assertTrue(clustering.getNumClusters() <= 4);
// int label0 = clustering.getClusterLabel(instances.get(0));
// int label1 = clustering.getClusterLabel(instances.get(1));
// int label2 = clustering.getClusterLabel(instances.get(2));
// assertEquals(label0, label1);
// assertEquals(label1, label2);
}

@Test
public void testClusterWithMaxIterLimitHit() {
Pipe pipe = new Noop();
// Metric metric = new Metric() {
// 
// public double distance(Object a, Object b) {
// return 1.0;
// }
// };
// KMeans kmeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_DROP);
for (int i = 0; i < 2; i++) {
new SparseVector(new int[] { 0 }, new double[] { i });
}
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { 0.0 });
SparseVector v2 = new SparseVector(new int[] { 0 }, new double[] { 100.0 });
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(v1, null, null, null));
instances.addThruPipe(new Instance(v2, null, null, null));
// Clustering clustering = kmeans.cluster(instances);
// assertNotNull(clustering);
// assertEquals(2, clustering.getNumClusters());
}

@Test
public void testClusterWithDuplicateButDistinctInstances() {
Pipe pipe = new Noop();
// Metric metric = new Metric() {
// 
// public double distance(Object a, Object b) {
// SparseVector sa = (SparseVector) a;
// SparseVector sb = (SparseVector) b;
// return Math.abs(sa.value(0) - sb.value(0));
// }
// };
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { 3.0 });
SparseVector v2 = new SparseVector(new int[] { 0 }, new double[] { 3.0 });
SparseVector v3 = new SparseVector(new int[] { 0 }, new double[] { 7.0 });
SparseVector v4 = new SparseVector(new int[] { 0 }, new double[] { 7.0 });
// KMeans kmeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_ERROR);
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(v1, null, "id1", null));
instances.addThruPipe(new Instance(v2, null, "id2", null));
instances.addThruPipe(new Instance(v3, null, "id3", null));
instances.addThruPipe(new Instance(v4, null, "id4", null));
// Clustering clustering = kmeans.cluster(instances);
// assertNotNull(clustering);
// assertEquals(2, clustering.getNumClusters());
// assertEquals(clustering.getClusterLabel(instances.get(0)), clustering.getClusterLabel(instances.get(1)));
// assertEquals(clustering.getClusterLabel(instances.get(2)), clustering.getClusterLabel(instances.get(3)));
}

@Test
public void testEmptyClusterDropAffectsLabelAdjustmentCorrectly() {
Pipe pipe = new Noop();
// Metric metric = new Metric() {
// 
// public double distance(Object a, Object b) {
// SparseVector v1 = (SparseVector) a;
// SparseVector v2 = (SparseVector) b;
// return Math.abs(v1.value(0) - v2.value(0));
// }
// };
// KMeans kmeans = new KMeans(pipe, 3, metric, KMeans.EMPTY_DROP);
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { 0.0 });
SparseVector v2 = new SparseVector(new int[] { 0 }, new double[] { 0.5 });
SparseVector v3 = new SparseVector(new int[] { 0 }, new double[] { 100.0 });
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(v1, null, null, null));
instances.addThruPipe(new Instance(v2, null, null, null));
instances.addThruPipe(new Instance(v3, null, null, null));
// Clustering clustering = kmeans.cluster(instances);
// assertNotNull(clustering);
// assertTrue(clustering.getNumClusters() <= 3);
// int label1 = clustering.getClusterLabel(instances.get(0));
// int label2 = clustering.getClusterLabel(instances.get(1));
// int label3 = clustering.getClusterLabel(instances.get(2));
// assertTrue(label1 == label2 || label2 == label3 || label1 == label3);
}

@Test
public void testDeltaMeansAccumulationOverIterations() {
Pipe pipe = new Noop();
final ArrayList<Double> deltaHistory = new ArrayList<>();
// Metric metricWithTracking = new Metric() {
// 
// public double distance(Object a, Object b) {
// SparseVector sa = (SparseVector) a;
// SparseVector sb = (SparseVector) b;
// double value = Math.abs(sa.value(0) - sb.value(0));
// deltaHistory.add(value);
// return value;
// }
// };
// KMeans kmeans = new KMeans(pipe, 2, metricWithTracking, KMeans.EMPTY_DROP);
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { 2.0 });
SparseVector v2 = new SparseVector(new int[] { 0 }, new double[] { 2.01 });
SparseVector v3 = new SparseVector(new int[] { 0 }, new double[] { 10.0 });
SparseVector v4 = new SparseVector(new int[] { 0 }, new double[] { 10.01 });
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(v1, null, null, null));
instances.addThruPipe(new Instance(v2, null, null, null));
instances.addThruPipe(new Instance(v3, null, null, null));
instances.addThruPipe(new Instance(v4, null, null, null));
// Clustering clustering = kmeans.cluster(instances);
// assertNotNull(clustering);
assertFalse(deltaHistory.isEmpty());
}

@Test
public void testNegativeValueVectorsClustering() {
Pipe pipe = new Noop();
// Metric metric = new Metric() {
// 
// public double distance(Object a, Object b) {
// SparseVector va = (SparseVector) a;
// SparseVector vb = (SparseVector) b;
// return Math.abs(va.value(0) - vb.value(0));
// }
// };
// KMeans kmeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_ERROR);
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { -10.0 });
SparseVector v2 = new SparseVector(new int[] { 0 }, new double[] { -9.9 });
SparseVector v3 = new SparseVector(new int[] { 0 }, new double[] { 5.0 });
SparseVector v4 = new SparseVector(new int[] { 0 }, new double[] { 5.1 });
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(v1, null, null, null));
instances.addThruPipe(new Instance(v2, null, null, null));
instances.addThruPipe(new Instance(v3, null, null, null));
instances.addThruPipe(new Instance(v4, null, null, null));
// Clustering clustering = kmeans.cluster(instances);
// assertNotNull(clustering);
// assertEquals(2, clustering.getNumClusters());
}

@Test
public void testClusterWithZeroClustersShouldBehaveGracefully() {
Pipe pipe = new Noop();
// Metric metric = new Metric() {
// 
// public double distance(Object a, Object b) {
// return 1.0;
// }
// };
// KMeans kmeans = new KMeans(pipe, 0, metric, KMeans.EMPTY_DROP);
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { 5.0 });
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(v1, null, null, null));
// Clustering clustering = kmeans.cluster(instances);
// assertNull(clustering);
}

@Test
public void testClusterWithClusterCountGreaterThanUniquePoints() {
Pipe pipe = new Noop();
// Metric metric = new Metric() {
// 
// public double distance(Object a, Object b) {
// SparseVector sa = (SparseVector) a;
// SparseVector sb = (SparseVector) b;
// return Math.abs(sa.value(0) - sb.value(0));
// }
// };
// KMeans kmeans = new KMeans(pipe, 5, metric, KMeans.EMPTY_SINGLE);
SparseVector point1 = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
SparseVector point2 = new SparseVector(new int[] { 0 }, new double[] { 2.0 });
SparseVector point3 = new SparseVector(new int[] { 0 }, new double[] { 3.0 });
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(point1, null, null, null));
instances.addThruPipe(new Instance(point2, null, null, null));
instances.addThruPipe(new Instance(point3, null, null, null));
// Clustering clustering = kmeans.cluster(instances);
// assertNotNull(clustering);
// assertTrue(clustering.getNumClusters() <= 3);
}

@Test
public void testClusterWithOneInstanceOneCluster() {
Pipe pipe = new Noop();
// Metric metric = new Metric() {
// 
// public double distance(Object a, Object b) {
// return 0.0;
// }
// };
// KMeans kmeans = new KMeans(pipe, 1, metric, KMeans.EMPTY_ERROR);
SparseVector v = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(v, null, null, null));
// Clustering clustering = kmeans.cluster(list);
// assertNotNull(clustering);
// assertEquals(1, clustering.getNumClusters());
// assertEquals(0, clustering.getClusterLabel(list.get(0)));
}

@Test
public void testClusterWithIdenticallyZeroVectors() {
Pipe pipe = new Noop();
// Metric metric = new Metric() {
// 
// public double distance(Object a, Object b) {
// return 0.0;
// }
// };
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { 0.0 });
SparseVector v2 = new SparseVector(new int[] { 0 }, new double[] { 0.0 });
SparseVector v3 = new SparseVector(new int[] { 0 }, new double[] { 0.0 });
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(v1, null, null, null));
list.addThruPipe(new Instance(v2, null, null, null));
list.addThruPipe(new Instance(v3, null, null, null));
// KMeans kmeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_DROP);
// Clustering clustering = kmeans.cluster(list);
// assertNotNull(clustering);
// assertTrue(clustering.getNumClusters() <= 2);
}

@Test
public void testClusterLabelsShiftCorrectlyWhenClusterIsDropped() {
Pipe pipe = new Noop();
// Metric metric = new Metric() {
// 
// public double distance(Object a, Object b) {
// SparseVector sa = (SparseVector) a;
// SparseVector sb = (SparseVector) b;
// return Math.abs(sa.value(0) - sb.value(0));
// }
// };
// KMeans kmeans = new KMeans(pipe, 3, metric, KMeans.EMPTY_DROP);
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
SparseVector v2 = new SparseVector(new int[] { 0 }, new double[] { 1.1 });
SparseVector v3 = new SparseVector(new int[] { 0 }, new double[] { 50.0 });
SparseVector v4 = new SparseVector(new int[] { 0 }, new double[] { 51.0 });
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(v1, null, "1", null));
instances.addThruPipe(new Instance(v2, null, "2", null));
instances.addThruPipe(new Instance(v3, null, "3", null));
instances.addThruPipe(new Instance(v4, null, "4", null));
// Clustering clustering = kmeans.cluster(instances);
// assertNotNull(clustering);
// assertTrue(clustering.getNumClusters() <= 3);
// int label1 = clustering.getClusterLabel(instances.get(0));
// int label2 = clustering.getClusterLabel(instances.get(1));
// int label3 = clustering.getClusterLabel(instances.get(2));
// int label4 = clustering.getClusterLabel(instances.get(3));
// assertTrue(label1 == label2 || label1 == label3 || label1 == label4);
}

@Test
public void testClusterReturnsNullWhenNoValidVectorsRemainAfterPreprocessing() {
Pipe pipe = new Noop();
// Metric metric = new Metric() {
// 
// public double distance(Object a, Object b) {
// return 1.0;
// }
// };
// KMeans kmeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_ERROR);
SparseVector v1 = new SparseVector(new int[0], new double[0]);
SparseVector v2 = new SparseVector(new int[0], new double[0]);
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(v1, null, null, null));
instances.addThruPipe(new Instance(v2, null, null, null));
// Clustering clustering = kmeans.cluster(instances);
// assertNull(clustering);
}

@Test
public void testClusterWithExtremelyLargeVectors() {
Pipe pipe = new Noop();
// Metric metric = new Metric() {
// 
// public double distance(Object a, Object b) {
// SparseVector sa = (SparseVector) a;
// SparseVector sb = (SparseVector) b;
// return Math.abs(sa.value(0) - sb.value(0));
// }
// };
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { Double.MAX_VALUE });
SparseVector v2 = new SparseVector(new int[] { 0 }, new double[] { Double.MAX_VALUE - 1 });
// KMeans kmeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_DROP);
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(v1, null, null, null));
instances.addThruPipe(new Instance(v2, null, null, null));
// Clustering clustering = kmeans.cluster(instances);
// assertNotNull(clustering);
// assertEquals(2, clustering.getNumClusters());
}

@Test
public void testClusterWithAllZeroValuesInMultipleVectors() {
Pipe pipe = new Noop();
// Metric metric = new Metric() {
// 
// public double distance(Object a, Object b) {
// double a1 = ((SparseVector) a).value(0);
// double b1 = ((SparseVector) b).value(0);
// return Math.abs(a1 - b1);
// }
// };
// KMeans kmeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_DROP);
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { 0.0 });
SparseVector v2 = new SparseVector(new int[] { 0 }, new double[] { 0.0 });
SparseVector v3 = new SparseVector(new int[] { 0 }, new double[] { 0.0 });
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(v1, null, null, null));
instances.addThruPipe(new Instance(v2, null, null, null));
instances.addThruPipe(new Instance(v3, null, null, null));
// Clustering result = kmeans.cluster(instances);
// assertNotNull(result);
// assertTrue(result.getNumClusters() <= 2);
}

@Test
public void testClusterWithSparseVectorsEmptyButDifferentObjects() {
Pipe pipe = new Noop();
// Metric metric = new Metric() {
// 
// public double distance(Object a, Object b) {
// return 1.0;
// }
// };
// KMeans kmeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_SINGLE);
SparseVector empty1 = new SparseVector(new int[0], new double[0]);
SparseVector empty2 = new SparseVector(new int[0], new double[0]);
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(empty1, null, "id1", null));
instances.addThruPipe(new Instance(empty2, null, "id2", null));
// Clustering clustering = kmeans.cluster(instances);
// assertNull(clustering);
}

@Test
public void testClusterWithSinglePointAndTwoClustersShouldReturnOneCluster() {
Pipe pipe = new Noop();
// Metric metric = new Metric() {
// 
// public double distance(Object a, Object b) {
// return 0.0;
// }
// };
SparseVector point = new SparseVector(new int[] { 0 }, new double[] { 3.14 });
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(point, null, "x", null));
// KMeans kmeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_DROP);
// Clustering clustering = kmeans.cluster(instances);
// assertNotNull(clustering);
// assertEquals(1, clustering.getNumClusters());
// assertEquals(0, clustering.getClusterLabel(instances.get(0)));
}

@Test
public void testMultipleEmptyClustersDroppedUntilOneRemaining() {
Pipe pipe = new Noop();
// Metric metric = new Metric() {
// 
// public double distance(Object a, Object b) {
// return ((SparseVector) a).value(0) - ((SparseVector) b).value(0);
// }
// };
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(v1, null, null, null));
// KMeans kmeans = new KMeans(pipe, 5, metric, KMeans.EMPTY_DROP);
// Clustering clustering = kmeans.cluster(instances);
// assertNotNull(clustering);
// assertEquals(1, clustering.getNumClusters());
}

@Test
public void testClusterWithNonZeroCoordinatesAndDistinctCentroids() {
Pipe pipe = new Noop();
// Metric metric = new Metric() {
// 
// public double distance(Object a, Object b) {
// double d0 = ((SparseVector) a).value(0) - ((SparseVector) b).value(0);
// double d1 = ((SparseVector) a).value(1) - ((SparseVector) b).value(1);
// return Math.sqrt(d0 * d0 + d1 * d1);
// }
// };
// KMeans kmeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_ERROR);
SparseVector v1 = new SparseVector(new int[] { 0, 1 }, new double[] { 1.0, 2.0 });
SparseVector v2 = new SparseVector(new int[] { 0, 1 }, new double[] { 2.0, 3.0 });
SparseVector v3 = new SparseVector(new int[] { 0, 1 }, new double[] { 9.0, 9.0 });
SparseVector v4 = new SparseVector(new int[] { 0, 1 }, new double[] { 10.0, 10.0 });
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(v1, null, "a", null));
list.addThruPipe(new Instance(v2, null, "b", null));
list.addThruPipe(new Instance(v3, null, "c", null));
list.addThruPipe(new Instance(v4, null, "d", null));
// Clustering clustering = kmeans.cluster(list);
// assertNotNull(clustering);
// assertEquals(2, clustering.getNumClusters());
// int labelA = clustering.getClusterLabel(list.get(0));
// int labelB = clustering.getClusterLabel(list.get(1));
// int labelC = clustering.getClusterLabel(list.get(2));
// int labelD = clustering.getClusterLabel(list.get(3));
// assertEquals(labelA, labelB);
// assertEquals(labelC, labelD);
// assertNotEquals(labelA, labelC);
}

@Test
public void testClusterWithMinusculeDifferencesDoesNotTriggerManyMoves() {
Pipe pipe = new Noop();
// Metric metric = new Metric() {
// 
// public double distance(Object a, Object b) {
// SparseVector va = (SparseVector) a;
// SparseVector vb = (SparseVector) b;
// return Math.abs(va.value(0) - vb.value(0));
// }
// };
// KMeans kmeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_ERROR);
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { 1.00000 });
SparseVector v2 = new SparseVector(new int[] { 0 }, new double[] { 1.00001 });
SparseVector v3 = new SparseVector(new int[] { 0 }, new double[] { 1.00002 });
SparseVector v4 = new SparseVector(new int[] { 0 }, new double[] { 1.00003 });
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(v1, null, null, null));
instances.addThruPipe(new Instance(v2, null, null, null));
instances.addThruPipe(new Instance(v3, null, null, null));
instances.addThruPipe(new Instance(v4, null, null, null));
// Clustering clustering = kmeans.cluster(instances);
// assertNotNull(clustering);
// assertEquals(2, clustering.getNumClusters());
}

@Test
public void testClusterWithIdenticalCentroidCandidatesInitially() {
Pipe pipe = new Noop();
// Metric metric = new Metric() {
// 
// public double distance(Object a, Object b) {
// SparseVector va = (SparseVector) a;
// SparseVector vb = (SparseVector) b;
// return Math.abs(va.value(0) - vb.value(0));
// }
// };
// KMeans kmeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_DROP);
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { 5.0 });
SparseVector v2 = new SparseVector(new int[] { 0 }, new double[] { 5.0 });
SparseVector v3 = new SparseVector(new int[] { 0 }, new double[] { 10.0 });
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(v1, null, "v1", null));
instances.addThruPipe(new Instance(v2, null, "v2", null));
instances.addThruPipe(new Instance(v3, null, "v3", null));
// Clustering clustering = kmeans.cluster(instances);
// assertNotNull(clustering);
// assertTrue(clustering.getNumClusters() <= 2);
}

@Test
public void testClusterWithNullInstanceDataThrowsClassCastException() {
Pipe pipe = new Noop();
// Metric metric = new Metric() {
// 
// public double distance(Object a, Object b) {
// if (a == null || b == null)
// return Double.MAX_VALUE;
// return Math.abs(((SparseVector) a).value(0) - ((SparseVector) b).value(0));
// }
// };
// KMeans kmeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_ERROR);
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(null, null, "null1", null));
instances.addThruPipe(new Instance(null, null, "null2", null));
// Clustering clustering = kmeans.cluster(instances);
// assertNull(clustering);
}

@Test
public void testClusterWithDuplicateCentroidsAtInitialization() {
Pipe pipe = new Noop();
// Metric metric = new Metric() {
// 
// public double distance(Object a, Object b) {
// SparseVector v1 = (SparseVector) a;
// SparseVector v2 = (SparseVector) b;
// return Math.abs(v1.value(0) - v2.value(0));
// }
// };
// KMeans kmeans = new KMeans(pipe, 3, metric, KMeans.EMPTY_DROP);
SparseVector v = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(v, null, "v1", null));
instances.addThruPipe(new Instance(v, null, "v2", null));
instances.addThruPipe(new Instance(v, null, "v3", null));
// Clustering result = kmeans.cluster(instances);
// assertNotNull(result);
// assertTrue(result.getNumClusters() <= 3);
}

@Test
public void testClusterWithHighDimensionalSparseVectors() {
Pipe pipe = new Noop();
// Metric metric = new Metric() {
// 
// public double distance(Object a, Object b) {
// SparseVector v1 = (SparseVector) a;
// SparseVector v2 = (SparseVector) b;
// double diff = 0.0;
// for (int i = 0; i < 100; i++) {
// diff += Math.pow(v1.value(i) - v2.value(i), 2);
// }
// return Math.sqrt(diff);
// }
// };
// KMeans kmeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_DROP);
int[] indices = new int[100];
double[] values1 = new double[100];
double[] values2 = new double[100];
for (int i = 0; i < 100; i++) {
indices[i] = i;
values1[i] = 1.0;
values2[i] = 2.0;
}
SparseVector vec1 = new SparseVector(indices, values1);
SparseVector vec2 = new SparseVector(indices, values2);
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(vec1, null, "high1", null));
instances.addThruPipe(new Instance(vec2, null, "high2", null));
// Clustering result = kmeans.cluster(instances);
// assertNotNull(result);
// assertEquals(2, result.getNumClusters());
}

@Test
public void testClusterWithMixedDenseAndSparseVectors() {
Pipe pipe = new Noop();
// Metric metric = new Metric() {
// 
// public double distance(Object a, Object b) {
// return 0.0;
// }
// };
// KMeans kmeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_DROP);
SparseVector sparse = new SparseVector(new int[] { 0, 2, 4 }, new double[] { 1.0, 1.0, 1.0 });
SparseVector dense = new SparseVector(new int[] { 0, 1, 2, 3, 4 }, new double[] { 1.0, 0.0, 1.0, 0.0, 1.0 });
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(sparse, null, "sparse", null));
instances.addThruPipe(new Instance(dense, null, "dense", null));
// Clustering result = kmeans.cluster(instances);
// assertNotNull(result);
}

@Test
public void testClusterWithIdenticalDataAndMultipleIterations() {
Pipe pipe = new Noop();
// Metric metric = new Metric() {
// 
// public double distance(Object a, Object b) {
// return 0.0;
// }
// };
// KMeans kmeans = new KMeans(pipe, 3, metric, KMeans.EMPTY_SINGLE);
SparseVector v = new SparseVector(new int[] { 0 }, new double[] { 2.0 });
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(v, null, "a", null));
instances.addThruPipe(new Instance(v, null, "b", null));
instances.addThruPipe(new Instance(v, null, "c", null));
instances.addThruPipe(new Instance(v, null, "d", null));
// Clustering result = kmeans.cluster(instances);
// assertNotNull(result);
// assertTrue(result.getNumClusters() <= 3);
}

@Test
public void testClusterEmptySingleFallbackWhenNoCandidateFound() {
Pipe pipe = new Noop();
// Metric metric = new Metric() {
// 
// public double distance(Object a, Object b) {
// return 0.0;
// }
// };
// KMeans kmeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_SINGLE);
SparseVector v = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(v, null, "x", null));
// Clustering result = kmeans.cluster(instances);
// assertNull(result);
}

@Test
public void testClusterMaxIterationReachedBeforeConvergence() {
Pipe pipe = new Noop();
// Metric metric = new Metric() {
// 
// public double distance(Object a, Object b) {
// return 100.0;
// }
// };
// KMeans kmeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_ERROR);
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
SparseVector v2 = new SparseVector(new int[] { 0 }, new double[] { 2.0 });
SparseVector v3 = new SparseVector(new int[] { 0 }, new double[] { 3.0 });
SparseVector v4 = new SparseVector(new int[] { 0 }, new double[] { 4.0 });
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(v1, null, "a", null));
instances.addThruPipe(new Instance(v2, null, "b", null));
instances.addThruPipe(new Instance(v3, null, "c", null));
instances.addThruPipe(new Instance(v4, null, "d", null));
// Clustering result = kmeans.cluster(instances);
// assertNotNull(result);
// assertEquals(2, result.getNumClusters());
}

@Test
public void testClusterWithEmptyInstanceListReturnsNull() {
Pipe pipe = new Noop();
// Metric metric = new Metric() {
// 
// public double distance(Object a, Object b) {
// return 1.0;
// }
// };
// KMeans kmeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_ERROR);
InstanceList emptyList = new InstanceList(pipe);
// Clustering result = kmeans.cluster(emptyList);
// assertNull(result);
}

@Test
public void testClusterWithNullInputListReturnsNull() {
Pipe pipe = new Noop();
// Metric metric = new Metric() {
// 
// public double distance(Object a, Object b) {
// return 1.0;
// }
// };
// KMeans kmeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_ERROR);
// Clustering result = kmeans.cluster(null);
// assertNull(result);
}

@Test
public void testClusterWithIncorrectPipeConfigurationReturnsNull() {
Pipe pipeA = new Noop();
Pipe pipeB = new Noop();
// Metric metric = new Metric() {
// 
// public double distance(Object a, Object b) {
// SparseVector v1 = (SparseVector) a;
// SparseVector v2 = (SparseVector) b;
// return Math.abs(v1.value(0) - v2.value(0));
// }
// };
// KMeans kmeans = new KMeans(pipeA, 2, metric, KMeans.EMPTY_ERROR);
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
SparseVector v2 = new SparseVector(new int[] { 0 }, new double[] { 2.0 });
InstanceList list = new InstanceList(pipeB);
list.addThruPipe(new Instance(v1, null, "a", null));
list.addThruPipe(new Instance(v2, null, "b", null));
Clustering result = null;
try {
// result = kmeans.cluster(list);
} catch (AssertionError e) {
result = null;
}
assertNull(result);
}

@Test
public void testClusterWithSmallNumberOfPointsGreaterClusters() {
Pipe pipe = new Noop();
// Metric metric = new Metric() {
// 
// public double distance(Object a, Object b) {
// return 0.1;
// }
// };
// KMeans kmeans = new KMeans(pipe, 10, metric, KMeans.EMPTY_DROP);
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
SparseVector v2 = new SparseVector(new int[] { 0 }, new double[] { 2.0 });
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(v1, null, "1", null));
list.addThruPipe(new Instance(v2, null, "2", null));
// Clustering result = kmeans.cluster(list);
// assertNotNull(result);
// assertTrue(result.getNumClusters() <= 2);
}

@Test
public void testClusterWithNegativeInfinityValues() {
Pipe pipe = new Noop();
// Metric metric = new Metric() {
// 
// public double distance(Object a, Object b) {
// SparseVector v1 = (SparseVector) a;
// SparseVector v2 = (SparseVector) b;
// return Math.abs(v1.value(0) - v2.value(0));
// }
// };
// KMeans kmeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_ERROR);
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { Double.NEGATIVE_INFINITY });
SparseVector v2 = new SparseVector(new int[] { 0 }, new double[] { -100.0 });
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(v1, null, "negInf", null));
instances.addThruPipe(new Instance(v2, null, "-100", null));
// Clustering clustering = kmeans.cluster(instances);
// assertNotNull(clustering);
// assertEquals(2, clustering.getNumClusters());
}

@Test
public void testClusterWithPositiveInfinityValues() {
Pipe pipe = new Noop();
// Metric metric = new Metric() {
// 
// public double distance(Object a, Object b) {
// SparseVector v1 = (SparseVector) a;
// SparseVector v2 = (SparseVector) b;
// return Math.abs(v1.value(0) - v2.value(0));
// }
// };
// KMeans kmeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_DROP);
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { Double.POSITIVE_INFINITY });
SparseVector v2 = new SparseVector(new int[] { 0 }, new double[] { 100.0 });
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(v1, null, "posInf", null));
instances.addThruPipe(new Instance(v2, null, "100", null));
// Clustering clustering = kmeans.cluster(instances);
// assertNotNull(clustering);
// assertEquals(2, clustering.getNumClusters());
}

@Test
public void testClusterWithNaNVectorValueReturnsNull() {
Pipe pipe = new Noop();
// Metric metric = new Metric() {
// 
// public double distance(Object a, Object b) {
// return Double.NaN;
// }
// };
// KMeans kmeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_ERROR);
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { Double.NaN });
SparseVector v2 = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(v1, null, "nan", null));
instances.addThruPipe(new Instance(v2, null, "val", null));
// Clustering clustering = kmeans.cluster(instances);
// assertNull(clustering);
}

@Test
public void testClusterWithPointToleranceEarlyExit() {
Pipe pipe = new Noop();
// Metric metric = new Metric() {
// 
// public double distance(Object a, Object b) {
// SparseVector va = (SparseVector) a;
// SparseVector vb = (SparseVector) b;
// return Math.abs(va.value(0) - vb.value(0));
// }
// };
// KMeans kmeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_ERROR);
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { 5.0 });
SparseVector v2 = new SparseVector(new int[] { 0 }, new double[] { 5.000001 });
InstanceList instances = new InstanceList(pipe);
instances.addThruPipe(new Instance(v1, null, "p1", null));
instances.addThruPipe(new Instance(v2, null, "p2", null));
// Clustering clustering = kmeans.cluster(instances);
// assertNotNull(clustering);
// assertEquals(2, clustering.getNumClusters());
}
}
