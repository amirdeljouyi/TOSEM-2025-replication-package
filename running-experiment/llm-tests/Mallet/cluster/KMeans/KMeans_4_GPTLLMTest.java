package cc.mallet.cluster;

public class KMeans_4_GPTLLMTest {

// @Test
public void testTwoDistinctClusters() {
// Pipe pipe = new Pipe() {
// 
// @Override
// public Instance pipe(Instance carrier) {
// return carrier;
// }
// };
// Metric metric = new Metric() {
// 
// @Override
// public double distance(SparseVector v1, SparseVector v2) {
// double diff = v1.valueAtLocation(0) - v2.valueAtLocation(0);
// return Math.sqrt(diff * diff);
// }
// };
// Instance instance1 = new Instance(new SparseVector(new double[] { 1.0 }), null, null, null);
// Instance instance2 = new Instance(new SparseVector(new double[] { 1.2 }), null, null, null);
// Instance instance3 = new Instance(new SparseVector(new double[] { 10.0 }), null, null, null);
// Instance instance4 = new Instance(new SparseVector(new double[] { 10.3 }), null, null, null);
// InstanceList instances = new InstanceList(pipe);
// instances.addThruPipe(instance1);
// instances.addThruPipe(instance2);
// instances.addThruPipe(instance3);
// instances.addThruPipe(instance4);
// KMeans kMeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_SINGLE);
// Clustering clustering = kMeans.cluster(instances);
// assertNotNull(clustering);
// assertEquals(2, clustering.getNumClusters());
// int[] labels = clustering.getClusterLabel();
// assertEquals(labels[0], labels[1]);
// assertEquals(labels[2], labels[3]);
// assertNotEquals(labels[0], labels[2]);
}

// @Test
public void testIdenticalPointsSingleCluster() {
// Pipe pipe = new Pipe() {
// 
// @Override
// public Instance pipe(Instance carrier) {
// return carrier;
// }
// };
// Metric metric = new Metric() {
// 
// @Override
// public double distance(SparseVector v1, SparseVector v2) {
// double sum = 0.0;
// for (int i = 0; i < v1.numLocations(); i++) {
// double diff = v1.valueAtLocation(i) - v2.valueAtLocation(i);
// sum += diff * diff;
// }
// return Math.sqrt(sum);
// }
// };
// SparseVector sv = new SparseVector(new double[] { 1.0, 2.0, 3.0 });
// Instance instance1 = new Instance(sv, null, null, null);
// Instance instance2 = new Instance(sv, null, null, null);
// Instance instance3 = new Instance(sv, null, null, null);
// InstanceList instances = new InstanceList(pipe);
// instances.addThruPipe(instance1);
// instances.addThruPipe(instance2);
// instances.addThruPipe(instance3);
// KMeans kMeans = new KMeans(pipe, 1, metric);
// Clustering clustering = kMeans.cluster(instances);
// assertNotNull(clustering);
// assertEquals(1, clustering.getNumClusters());
// int[] labels = clustering.getClusterLabel();
// assertEquals(0, labels[0]);
// assertEquals(0, labels[1]);
// assertEquals(0, labels[2]);
// ArrayList<SparseVector> means = kMeans.getClusterMeans();
// assertNotNull(means);
// assertEquals(1, means.size());
}

// @Test
public void testEmptyErrorReturnsNull() {
// Pipe pipe = new Pipe() {
// 
// @Override
// public Instance pipe(Instance carrier) {
// return carrier;
// }
// };
// Metric metric = new Metric() {
// 
// @Override
// public double distance(SparseVector v1, SparseVector v2) {
// double diff = v1.valueAtLocation(0) - v2.valueAtLocation(0);
// return Math.sqrt(diff * diff);
// }
// };
// InstanceList instances = new InstanceList(pipe);
// instances.addThruPipe(new Instance(new SparseVector(new double[] { 1.0 }), null, null, null));
// instances.addThruPipe(new Instance(new SparseVector(new double[] { 2.0 }), null, null, null));
// KMeans kMeans = new KMeans(pipe, 5, metric, KMeans.EMPTY_ERROR);
// Clustering clustering = kMeans.cluster(instances);
// assertNull(clustering);
}

// @Test
public void testEmptyDropReducesClusters() {
// Pipe pipe = new Pipe() {
// 
// @Override
// public Instance pipe(Instance carrier) {
// return carrier;
// }
// };
// Metric metric = new Metric() {
// 
// @Override
// public double distance(SparseVector v1, SparseVector v2) {
// double diff = v1.valueAtLocation(0) - v2.valueAtLocation(0);
// return Math.abs(diff);
// }
// };
// InstanceList instances = new InstanceList(pipe);
// instances.addThruPipe(new Instance(new SparseVector(new double[] { 0.1 }), null, null, null));
// instances.addThruPipe(new Instance(new SparseVector(new double[] { 0.2 }), null, null, null));
// instances.addThruPipe(new Instance(new SparseVector(new double[] { 100.0 }), null, null, null));
// KMeans kMeans = new KMeans(pipe, 3, metric, KMeans.EMPTY_DROP);
// Clustering clustering = kMeans.cluster(instances);
// assertNotNull(clustering);
// assertTrue(clustering.getNumClusters() <= 3);
}

// @Test
public void testClusterWithMaxIterationsReachedStillReturnsResult() {
// Pipe pipe = new Pipe() {
// 
// @Override
// public Instance pipe(Instance carrier) {
// return carrier;
// }
// };
// Metric metric = new Metric() {
// 
// @Override
// public double distance(SparseVector v1, SparseVector v2) {
// double diff = v1.valueAtLocation(0) - v2.valueAtLocation(0);
// return Math.abs(diff);
// }
// };
// Instance instance1 = new Instance(new SparseVector(new double[] { 1.0 }), null, null, null);
// Instance instance2 = new Instance(new SparseVector(new double[] { 10.0 }), null, null, null);
// InstanceList instances = new InstanceList(pipe);
// instances.addThruPipe(instance1);
// instances.addThruPipe(instance2);
int prevMaxIter = KMeans.MAX_ITER;
KMeans.MAX_ITER = 1;
// KMeans kMeans = new KMeans(pipe, 2, metric);
// Clustering clustering = kMeans.cluster(instances);
KMeans.MAX_ITER = prevMaxIter;
// assertNotNull(clustering);
// assertEquals(2, clustering.getNumClusters());
}

// @Test
public void testSingleClusterAssignedToAll() {
// Pipe pipe = new Pipe() {
// 
// @Override
// public Instance pipe(Instance carrier) {
// return carrier;
// }
// };
// Metric metric = new Metric() {
// 
// @Override
// public double distance(SparseVector v1, SparseVector v2) {
// double diff = v1.valueAtLocation(0) - v2.valueAtLocation(0);
// return diff * diff;
// }
// };
// Instance i1 = new Instance(new SparseVector(new double[] { 0.0 }), null, null, null);
// Instance i2 = new Instance(new SparseVector(new double[] { 0.1 }), null, null, null);
// Instance i3 = new Instance(new SparseVector(new double[] { 0.2 }), null, null, null);
// InstanceList instances = new InstanceList(pipe);
// instances.addThruPipe(i1);
// instances.addThruPipe(i2);
// instances.addThruPipe(i3);
// KMeans kMeans = new KMeans(pipe, 1, metric);
// Clustering clustering = kMeans.cluster(instances);
// assertNotNull(clustering);
// int[] labels = clustering.getClusterLabel();
// assertEquals(1, clustering.getNumClusters());
// assertEquals(labels[0], 0);
// assertEquals(labels[1], 0);
// assertEquals(labels[2], 0);
}

// @Test
public void testEmptyInstanceListReturnsNullClustering() {
// Pipe pipe = new Pipe() {
// 
// @Override
// public Instance pipe(Instance carrier) {
// return carrier;
// }
// };
// Metric metric = new Metric() {
// 
// @Override
// public double distance(SparseVector v1, SparseVector v2) {
// return 1.0;
// }
// };
// InstanceList instances = new InstanceList(pipe);
// KMeans kMeans = new KMeans(pipe, 3, metric);
// Clustering clustering = kMeans.cluster(instances);
// assertNull(clustering);
}

// @Test
public void testMissingPipeThrowsAssertion() {
// Pipe pipe = new Pipe() {
// 
// @Override
// public Instance pipe(Instance carrier) {
// return carrier;
// }
// };
// Metric metric = new Metric() {
// 
// @Override
// public double distance(SparseVector v1, SparseVector v2) {
// return 1.0;
// }
// };
// Pipe otherPipe = new Pipe() {
// 
// @Override
// public Instance pipe(Instance carrier) {
// return carrier;
// }
// };
// Instance instance = new Instance(new SparseVector(new double[] { 1.0 }), null, null, null);
// InstanceList instances = new InstanceList(otherPipe);
// instances.addThruPipe(instance);
// KMeans kMeans = new KMeans(pipe, 2, metric);
boolean assertionThrown = false;
try {
// kMeans.cluster(instances);
} catch (AssertionError e) {
assertionThrown = true;
}
// assertTrue(assertionThrown);
}

// @Test
public void testInitializationWithAllZeroSparseVectorsSkipsThem() {
// Pipe pipe = new Pipe() {
// 
// @Override
// public Instance pipe(Instance carrier) {
// return carrier;
// }
// };
// Metric metric = new Metric() {
// 
// @Override
// public double distance(SparseVector v1, SparseVector v2) {
// return 1.0;
// }
// };
// SparseVector zeroVector = new SparseVector(new double[] { 0.0, 0.0 });
// Instance instance1 = new Instance(zeroVector, null, null, null);
// Instance instance2 = new Instance(new SparseVector(new double[] { 10.0, 20.0 }), null, null, null);
// InstanceList instances = new InstanceList(pipe);
// instances.addThruPipe(instance1);
// instances.addThruPipe(instance2);
// KMeans kMeans = new KMeans(pipe, 1, metric);
// Clustering clustering = kMeans.cluster(instances);
// assertNotNull(clustering);
// assertEquals(1, clustering.getNumClusters());
}

// @Test
public void testClusterWithSingleInstanceList() {
// Pipe pipe = new Pipe() {
// 
// @Override
// public Instance pipe(Instance carrier) {
// return carrier;
// }
// };
// Metric metric = new Metric() {
// 
// @Override
// public double distance(SparseVector v1, SparseVector v2) {
// return 0.0;
// }
// };
// SparseVector vector = new SparseVector(new double[] { 5.0 });
// Instance instance = new Instance(vector, null, null, null);
// InstanceList instances = new InstanceList(pipe);
// instances.addThruPipe(instance);
// KMeans kMeans = new KMeans(pipe, 1, metric);
// Clustering clustering = kMeans.cluster(instances);
// assertNotNull(clustering);
// assertEquals(1, clustering.getNumClusters());
// int[] labels = clustering.getClusterLabel();
// assertEquals(1, labels.length);
// assertEquals(0, labels[0]);
}

// @Test
public void testEmptySingleWhenNoInstanceCanBeMovedReturnsNull() {
// Pipe pipe = new Pipe() {
// 
// @Override
// public Instance pipe(Instance carrier) {
// return carrier;
// }
// };
// Metric metric = new Metric() {
// 
// @Override
// public double distance(SparseVector v1, SparseVector v2) {
// double diff = v1.valueAtLocation(0) - v2.valueAtLocation(0);
// return Math.abs(diff);
// }
// };
// Instance instance1 = new Instance(new SparseVector(new double[] { 5.0 }), null, null, null);
// Instance instance2 = new Instance(new SparseVector(new double[] { 5.0 }), null, null, null);
// InstanceList instances = new InstanceList(pipe);
// instances.addThruPipe(instance1);
// instances.addThruPipe(instance2);
// KMeans kMeans = new KMeans(pipe, 3, metric, KMeans.EMPTY_SINGLE);
// Clustering clustering = kMeans.cluster(instances);
// assertNull(clustering);
}

// @Test
public void testMultipleIdenticalDistancesChoosesFirstMin() {
// Pipe pipe = new Pipe() {
// 
// @Override
// public Instance pipe(Instance carrier) {
// return carrier;
// }
// };
// Metric metric = new Metric() {
// 
// @Override
// public double distance(SparseVector v1, SparseVector v2) {
// return 1.0;
// }
// };
// Instance i1 = new Instance(new SparseVector(new double[] { 1.0 }), null, null, null);
// Instance i2 = new Instance(new SparseVector(new double[] { 2.0 }), null, null, null);
// Instance i3 = new Instance(new SparseVector(new double[] { 3.0 }), null, null, null);
// InstanceList instances = new InstanceList(pipe);
// instances.addThruPipe(i1);
// instances.addThruPipe(i2);
// instances.addThruPipe(i3);
// KMeans kMeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_SINGLE);
// Clustering clustering = kMeans.cluster(instances);
// assertNotNull(clustering);
// assertEquals(2, clustering.getNumClusters());
// int[] labels = clustering.getClusterLabel();
// assertEquals(3, labels.length);
}

// @Test
public void testDropEmptyClusterDecrementsClusterCountAndRemapsLabelsCorrectly() {
// Pipe pipe = new Pipe() {
// 
// @Override
// public Instance pipe(Instance carrier) {
// return carrier;
// }
// };
// Metric metric = new Metric() {
// 
// @Override
// public double distance(SparseVector v1, SparseVector v2) {
// return Math.abs(v1.valueAtLocation(0) - v2.valueAtLocation(0));
// }
// };
// Instance i1 = new Instance(new SparseVector(new double[] { 1.0 }), null, null, null);
// Instance i2 = new Instance(new SparseVector(new double[] { 50.0 }), null, null, null);
// Instance i3 = new Instance(new SparseVector(new double[] { 100.0 }), null, null, null);
// InstanceList instances = new InstanceList(pipe);
// instances.addThruPipe(i1);
// instances.addThruPipe(i2);
// instances.addThruPipe(i3);
// KMeans kMeans = new KMeans(pipe, 5, metric, KMeans.EMPTY_DROP);
// Clustering clustering = kMeans.cluster(instances);
// assertNotNull(clustering);
// assertTrue(clustering.getNumClusters() <= 5);
// int[] labels = clustering.getClusterLabel();
// assertEquals(3, labels.length);
// for (int i = 0; i < labels.length; i++) {
// assertTrue(labels[i] >= 0 && labels[i] < clustering.getNumClusters());
// }
}

// @Test
public void testClusterWithNegativeValuesInVectors() {
// Pipe pipe = new Pipe() {
// 
// @Override
// public Instance pipe(Instance carrier) {
// return carrier;
// }
// };
// Metric metric = new Metric() {
// 
// @Override
// public double distance(SparseVector v1, SparseVector v2) {
// double diff = v1.valueAtLocation(0) - v2.valueAtLocation(0);
// return Math.abs(diff);
// }
// };
// Instance instance1 = new Instance(new SparseVector(new double[] { -5.0 }), null, null, null);
// Instance instance2 = new Instance(new SparseVector(new double[] { -10.0 }), null, null, null);
// Instance instance3 = new Instance(new SparseVector(new double[] { 20.0 }), null, null, null);
// InstanceList instances = new InstanceList(pipe);
// instances.addThruPipe(instance1);
// instances.addThruPipe(instance2);
// instances.addThruPipe(instance3);
// KMeans kMeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_SINGLE);
// Clustering clustering = kMeans.cluster(instances);
// assertNotNull(clustering);
// assertEquals(2, clustering.getNumClusters());
// int[] labels = clustering.getClusterLabel();
// assertEquals(3, labels.length);
}

// @Test
public void testClusterWhenAllInstancesEquidistantToCenters() {
// Pipe pipe = new Pipe() {
// 
// @Override
// public Instance pipe(Instance carrier) {
// return carrier;
// }
// };
// Metric metric = new Metric() {
// 
// @Override
// public double distance(SparseVector v1, SparseVector v2) {
// return 1.0;
// }
// };
// Instance instance1 = new Instance(new SparseVector(new double[] { 1.0 }), null, null, null);
// Instance instance2 = new Instance(new SparseVector(new double[] { 2.0 }), null, null, null);
// InstanceList instances = new InstanceList(pipe);
// instances.addThruPipe(instance1);
// instances.addThruPipe(instance2);
// KMeans kMeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_SINGLE);
// Clustering clustering = kMeans.cluster(instances);
// assertNotNull(clustering);
// assertEquals(2, clustering.getNumClusters());
// int[] labels = clustering.getClusterLabel();
// assertEquals(2, labels.length);
}

// @Test
public void testClusterWithHighDimensionalData() {
// Pipe pipe = new Pipe() {
// 
// @Override
// public Instance pipe(Instance carrier) {
// return carrier;
// }
// };
// Metric metric = new Metric() {
// 
// @Override
// public double distance(SparseVector v1, SparseVector v2) {
// double sum = 0.0;
// int len = v1.numLocations();
// for (int i = 0; i < len; i++) {
// double diff = v1.valueAtLocation(i) - v2.valueAtLocation(i);
// sum += diff * diff;
// }
// return Math.sqrt(sum);
// }
// };
double[] dims1 = new double[100];
double[] dims2 = new double[100];
dims2[0] = 100.0;
// SparseVector highDim1 = new SparseVector(dims1);
// SparseVector highDim2 = new SparseVector(dims2);
// Instance instance1 = new Instance(highDim1, null, null, null);
// Instance instance2 = new Instance(highDim2, null, null, null);
// InstanceList instances = new InstanceList(pipe);
// instances.addThruPipe(instance1);
// instances.addThruPipe(instance2);
// KMeans kMeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_DROP);
// Clustering clustering = kMeans.cluster(instances);
// assertNotNull(clustering);
// assertEquals(2, clustering.getNumClusters());
// int[] labels = clustering.getClusterLabel();
// assertEquals(2, labels.length);
// assertNotEquals(labels[0], labels[1]);
}

// @Test
public void testClusterToleranceEarlyStopCondition() {
// Pipe pipe = new Pipe() {
// 
// @Override
// public Instance pipe(Instance carrier) {
// return carrier;
// }
// };
// Metric metric = new Metric() {
// 
// @Override
// public double distance(SparseVector v1, SparseVector v2) {
// double diff = v1.valueAtLocation(0) - v2.valueAtLocation(0);
// return Math.abs(diff);
// }
// };
// Instance instance1 = new Instance(new SparseVector(new double[] { 1.0 }), null, null, null);
// Instance instance2 = new Instance(new SparseVector(new double[] { 1.001 }), null, null, null);
// Instance instance3 = new Instance(new SparseVector(new double[] { 1.002 }), null, null, null);
// InstanceList instances = new InstanceList(pipe);
// instances.addThruPipe(instance1);
// instances.addThruPipe(instance2);
// instances.addThruPipe(instance3);
int originalMaxIter = KMeans.MAX_ITER;
double originalTol = KMeans.POINTS_TOLERANCE;
KMeans.POINTS_TOLERANCE = 0.5;
KMeans.MAX_ITER = 100;
// KMeans kMeans = new KMeans(pipe, 1, metric);
// Clustering clustering = kMeans.cluster(instances);
KMeans.POINTS_TOLERANCE = originalTol;
KMeans.MAX_ITER = originalMaxIter;
// assertNotNull(clustering);
// assertEquals(1, clustering.getNumClusters());
}

// @Test
public void testClusterMeansNotModifiedAfterNoChangeInPoints() {
// Pipe pipe = new Pipe() {
// 
// @Override
// public Instance pipe(Instance carrier) {
// return carrier;
// }
// };
// Metric metric = new Metric() {
// 
// @Override
// public double distance(SparseVector v1, SparseVector v2) {
// return 0.0;
// }
// };
// Instance instance1 = new Instance(new SparseVector(new double[] { 5.0 }), null, null, null);
// Instance instance2 = new Instance(new SparseVector(new double[] { 5.0 }), null, null, null);
// Instance instance3 = new Instance(new SparseVector(new double[] { 5.0 }), null, null, null);
// InstanceList instances = new InstanceList(pipe);
// instances.addThruPipe(instance1);
// instances.addThruPipe(instance2);
// instances.addThruPipe(instance3);
// KMeans kMeans = new KMeans(pipe, 1, metric);
// Clustering clustering = kMeans.cluster(instances);
// assertNotNull(clustering);
// ArrayList<SparseVector> means = kMeans.getClusterMeans();
// assertEquals(1, means.size());
// assertEquals(5.0, means.get(0).valueAtLocation(0), 0.0);
}

// @Test
public void testClusterWithTooFewValidVectorsForRequestedClusters() {
// Pipe pipe = new Pipe() {
// 
// @Override
// public Instance pipe(Instance carrier) {
// return carrier;
// }
// };
// Metric metric = new Metric() {
// 
// @Override
// public double distance(SparseVector v1, SparseVector v2) {
// return 1.0;
// }
// };
// SparseVector sv1 = new SparseVector(new double[] { 0.0, 0.0 });
// SparseVector sv2 = new SparseVector(new double[] { 10.0, 20.0 });
// Instance instance1 = new Instance(sv1, null, null, null);
// Instance instance2 = new Instance(sv2, null, null, null);
// InstanceList instances = new InstanceList(pipe);
// instances.addThruPipe(instance1);
// instances.addThruPipe(instance2);
// KMeans kMeans = new KMeans(pipe, 4, metric, KMeans.EMPTY_DROP);
// Clustering clustering = kMeans.cluster(instances);
// assertNotNull(clustering);
// assertTrue(clustering.getNumClusters() <= 4);
}

// @Test
public void testClusterWithAllZeroVectorsAndValidClusters() {
// Pipe pipe = new Pipe() {
// 
// @Override
// public Instance pipe(Instance carrier) {
// return carrier;
// }
// };
// Metric metric = new Metric() {
// 
// @Override
// public double distance(SparseVector v1, SparseVector v2) {
// return 0.0;
// }
// };
// SparseVector zeroVector = new SparseVector(new double[] { 0.0, 0.0 });
// Instance instance1 = new Instance(zeroVector, null, null, null);
// Instance instance2 = new Instance(zeroVector, null, null, null);
// InstanceList instances = new InstanceList(pipe);
// instances.addThruPipe(instance1);
// instances.addThruPipe(instance2);
// KMeans kMeans = new KMeans(pipe, 1, metric, KMeans.EMPTY_SINGLE);
// Clustering clustering = kMeans.cluster(instances);
// assertNotNull(clustering);
// assertEquals(1, clustering.getNumClusters());
// int[] labels = clustering.getClusterLabel();
// assertEquals(2, labels.length);
// assertEquals(labels[0], labels[1]);
}

// @Test
public void testClusterWithMoreClustersThanInstancesUsingDrop() {
// Pipe pipe = new Pipe() {
// 
// @Override
// public Instance pipe(Instance carrier) {
// return carrier;
// }
// };
// Metric metric = new Metric() {
// 
// @Override
// public double distance(SparseVector v1, SparseVector v2) {
// return Math.abs(v1.valueAtLocation(0) - v2.valueAtLocation(0));
// }
// };
// Instance instance1 = new Instance(new SparseVector(new double[] { 5.0 }), null, null, null);
// Instance instance2 = new Instance(new SparseVector(new double[] { 10.0 }), null, null, null);
// InstanceList instances = new InstanceList(pipe);
// instances.addThruPipe(instance1);
// instances.addThruPipe(instance2);
// KMeans kMeans = new KMeans(pipe, 5, metric, KMeans.EMPTY_DROP);
// Clustering clustering = kMeans.cluster(instances);
// assertNotNull(clustering);
// assertTrue(clustering.getNumClusters() <= 5);
// int[] labels = clustering.getClusterLabel();
// assertEquals(2, labels.length);
}

// @Test
public void testClusterWithIdenticalInstancesAndMultipleClusters() {
// Pipe pipe = new Pipe() {
// 
// @Override
// public Instance pipe(Instance carrier) {
// return carrier;
// }
// };
// Metric metric = new Metric() {
// 
// @Override
// public double distance(SparseVector v1, SparseVector v2) {
// return 0.0;
// }
// };
// SparseVector vec = new SparseVector(new double[] { 3.3 });
// Instance instance1 = new Instance(vec, null, null, null);
// Instance instance2 = new Instance(vec, null, null, null);
// Instance instance3 = new Instance(vec, null, null, null);
// InstanceList instances = new InstanceList(pipe);
// instances.addThruPipe(instance1);
// instances.addThruPipe(instance2);
// instances.addThruPipe(instance3);
// KMeans kMeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_DROP);
// Clustering clustering = kMeans.cluster(instances);
// assertNotNull(clustering);
// assertTrue(clustering.getNumClusters() <= 2);
// int[] labels = clustering.getClusterLabel();
// assertEquals(3, labels.length);
// assertEquals(labels[0], labels[1]);
// assertEquals(labels[1], labels[2]);
}

// @Test
public void testClusterWithEmptyClusterDropAdjustsLabelsCorrectly() {
// Pipe pipe = new Pipe() {
// 
// @Override
// public Instance pipe(Instance carrier) {
// return carrier;
// }
// };
// Metric metric = new Metric() {
// 
// @Override
// public double distance(SparseVector v1, SparseVector v2) {
// return Math.abs(v1.valueAtLocation(0) - v2.valueAtLocation(0));
// }
// };
// Instance instance1 = new Instance(new SparseVector(new double[] { 1.0 }), null, null, null);
// Instance instance2 = new Instance(new SparseVector(new double[] { 2.0 }), null, null, null);
// Instance instance3 = new Instance(new SparseVector(new double[] { 1000.0 }), null, null, null);
// InstanceList instances = new InstanceList(pipe);
// instances.addThruPipe(instance1);
// instances.addThruPipe(instance2);
// instances.addThruPipe(instance3);
// KMeans kMeans = new KMeans(pipe, 3, metric, KMeans.EMPTY_DROP);
// Clustering clustering = kMeans.cluster(instances);
// assertNotNull(clustering);
// int[] labels = clustering.getClusterLabel();
// assertEquals(3, labels.length);
// for (int label : labels) {
// assertTrue(label >= 0 && label < clustering.getNumClusters());
// }
}

// @Test
public void testClusterWithSingleFeatureVectorsDifferentDimsShouldNotFailWhenCastable() {
// Pipe pipe = new Pipe() {
// 
// @Override
// public Instance pipe(Instance carrier) {
// return carrier;
// }
// };
// Metric metric = new Metric() {
// 
// @Override
// public double distance(SparseVector v1, SparseVector v2) {
// int minLen = Math.min(v1.numLocations(), v2.numLocations());
// double sum = 0.0;
// for (int i = 0; i < minLen; i++) {
// double diff = v1.valueAtLocation(i) - v2.valueAtLocation(i);
// sum += diff * diff;
// }
// return Math.sqrt(sum);
// }
// };
// Instance instance1 = new Instance(new SparseVector(new double[] { 1.0, 2.0, 3.0 }), null, null, null);
// Instance instance2 = new Instance(new SparseVector(new double[] { 1.0 }), null, null, null);
// InstanceList instances = new InstanceList(pipe);
// instances.addThruPipe(instance1);
// instances.addThruPipe(instance2);
// KMeans kMeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_DROP);
// Clustering clustering = kMeans.cluster(instances);
// assertNotNull(clustering);
// assertEquals(2, clustering.getNumClusters());
// int[] labels = clustering.getClusterLabel();
// assertEquals(2, labels.length);
}

// @Test
public void testClusterWithNullDataInInstanceShouldThrowClassCastException() {
// Pipe pipe = new Pipe() {
// 
// @Override
// public Instance pipe(Instance carrier) {
// return carrier;
// }
// };
// Metric metric = new Metric() {
// 
// @Override
// public double distance(SparseVector v1, SparseVector v2) {
// return 1.0;
// }
// };
// Instance validInstance = new Instance(new SparseVector(new double[] { 1.0 }), null, null, null);
// Instance nullDataInstance = new Instance(null, null, null, null);
// InstanceList instances = new InstanceList(pipe);
// instances.addThruPipe(validInstance);
// instances.addThruPipe(nullDataInstance);
// KMeans kMeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_SINGLE);
boolean exceptionThrown = false;
try {
// kMeans.cluster(instances);
} catch (ClassCastException e) {
exceptionThrown = true;
}
// assertTrue("Expected ClassCastException for null data", exceptionThrown);
}

// @Test
public void testClusterWithOneDimensionVectorsAllDifferent() {
// Pipe pipe = new Pipe() {
// 
// @Override
// public Instance pipe(Instance carrier) {
// return carrier;
// }
// };
// Metric metric = new Metric() {
// 
// @Override
// public double distance(SparseVector v1, SparseVector v2) {
// return Math.abs(v1.valueAtLocation(0) - v2.valueAtLocation(0));
// }
// };
// Instance instanceA = new Instance(new SparseVector(new double[] { 1.0 }), null, null, null);
// Instance instanceB = new Instance(new SparseVector(new double[] { 2.0 }), null, null, null);
// Instance instanceC = new Instance(new SparseVector(new double[] { 3.0 }), null, null, null);
// InstanceList instances = new InstanceList(pipe);
// instances.addThruPipe(instanceA);
// instances.addThruPipe(instanceB);
// instances.addThruPipe(instanceC);
// KMeans kMeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_DROP);
// Clustering clustering = kMeans.cluster(instances);
// assertNotNull(clustering);
// assertEquals(2, clustering.getNumClusters());
// int[] labels = clustering.getClusterLabel();
// assertEquals(3, labels.length);
}

// @Test
public void testClusterWithSilentEmptySingleBehaviorReturningNull() {
// Pipe pipe = new Pipe() {
// 
// @Override
// public Instance pipe(Instance carrier) {
// return carrier;
// }
// };
// Metric metric = new Metric() {
// 
// @Override
// public double distance(SparseVector v1, SparseVector v2) {
// return Math.abs(v1.valueAtLocation(0) - v2.valueAtLocation(0));
// }
// };
// Instance instance1 = new Instance(new SparseVector(new double[] { 1.0 }), null, null, null);
// Instance instance2 = new Instance(new SparseVector(new double[] { 2.0 }), null, null, null);
// InstanceList instances = new InstanceList(pipe);
// instances.addThruPipe(instance1);
// instances.addThruPipe(instance2);
// KMeans kMeans = new KMeans(pipe, 10, metric, KMeans.EMPTY_SINGLE);
// Clustering clustering = kMeans.cluster(instances);
// assertNull("Expected KMeans to return null when it cannot recover from empty cluster using EMPTY_SINGLE", clustering);
}

// @Test
public void testClusterWithLargeNumberOfIdenticalInstances() {
// Pipe pipe = new Pipe() {
// 
// @Override
// public Instance pipe(Instance carrier) {
// return carrier;
// }
// };
// Metric metric = new Metric() {
// 
// @Override
// public double distance(SparseVector v1, SparseVector v2) {
// return Math.abs(v1.valueAtLocation(0) - v2.valueAtLocation(0));
// }
// };
// SparseVector identicalVector = new SparseVector(new double[] { 5.0 });
// InstanceList instances = new InstanceList(pipe);
// instances.addThruPipe(new Instance(identicalVector, null, null, null));
// instances.addThruPipe(new Instance(identicalVector, null, null, null));
// instances.addThruPipe(new Instance(identicalVector, null, null, null));
// instances.addThruPipe(new Instance(identicalVector, null, null, null));
// instances.addThruPipe(new Instance(identicalVector, null, null, null));
// KMeans kMeans = new KMeans(pipe, 3, metric, KMeans.EMPTY_DROP);
// Clustering clustering = kMeans.cluster(instances);
// assertNotNull(clustering);
// assertTrue(clustering.getNumClusters() <= 3);
// int[] labels = clustering.getClusterLabel();
// assertEquals(5, labels.length);
}

// @Test
public void testClusterWithInstanceListContainingMixtureOfSparseVectorsAndEmptyVectors() {
// Pipe pipe = new Pipe() {
// 
// @Override
// public Instance pipe(Instance carrier) {
// return carrier;
// }
// };
// Metric metric = new Metric() {
// 
// @Override
// public double distance(SparseVector v1, SparseVector v2) {
// double sum = 0;
// int len = Math.min(v1.numLocations(), v2.numLocations());
// for (int i = 0; i < len; i++) {
// double diff = v1.valueAtLocation(i) - v2.valueAtLocation(i);
// sum += diff * diff;
// }
// return Math.sqrt(sum);
// }
// };
// SparseVector nonEmptyVector = new SparseVector(new double[] { 1.0 });
// SparseVector emptyVector = new SparseVector(new double[] {});
// InstanceList instances = new InstanceList(pipe);
// instances.addThruPipe(new Instance(nonEmptyVector, null, null, null));
// instances.addThruPipe(new Instance(emptyVector, null, null, null));
// instances.addThruPipe(new Instance(nonEmptyVector, null, null, null));
// KMeans kMeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_DROP);
// Clustering clustering = kMeans.cluster(instances);
// assertNotNull(clustering);
// int[] labels = clustering.getClusterLabel();
// assertEquals(3, labels.length);
}

// @Test
public void testClusterWithDuplicateInitialSeedSelection() {
// Pipe pipe = new Pipe() {
// 
// @Override
// public Instance pipe(Instance carrier) {
// return carrier;
// }
// };
// Metric metric = new Metric() {
// 
// @Override
// public double distance(SparseVector v1, SparseVector v2) {
// return Math.abs(v1.valueAtLocation(0) - v2.valueAtLocation(0));
// }
// };
// SparseVector vector = new SparseVector(new double[] { 5.0 });
// Instance instance1 = new Instance(vector, null, null, null);
// Instance instance2 = new Instance(vector, null, null, null);
// Instance instance3 = new Instance(vector, null, null, null);
// InstanceList instances = new InstanceList(pipe);
// instances.addThruPipe(instance1);
// instances.addThruPipe(instance2);
// instances.addThruPipe(instance3);
// KMeans kMeans = new KMeans(pipe, 3, metric, KMeans.EMPTY_SINGLE);
// Clustering clustering = kMeans.cluster(instances);
// assertNotNull(clustering);
// assertTrue(clustering.getNumClusters() <= 3);
// int[] labels = clustering.getClusterLabel();
// assertEquals(3, labels.length);
}

// @Test
public void testClusterWithZeroClustersRequestedShouldReturnNull() {
// Pipe pipe = new Pipe() {
// 
// @Override
// public Instance pipe(Instance carrier) {
// return carrier;
// }
// };
// Metric metric = new Metric() {
// 
// @Override
// public double distance(SparseVector v1, SparseVector v2) {
// return 1.0;
// }
// };
// SparseVector vector = new SparseVector(new double[] { 1.0 });
// Instance instance = new Instance(vector, null, null, null);
// InstanceList instances = new InstanceList(pipe);
// instances.addThruPipe(instance);
// KMeans kMeans = new KMeans(pipe, 0, metric, KMeans.EMPTY_DROP);
// Clustering clustering = kMeans.cluster(instances);
// assertNull(clustering);
}

// @Test
public void testClusterWithOneClusterAndMultipleIdenticalInstances() {
// Pipe pipe = new Pipe() {
// 
// @Override
// public Instance pipe(Instance carrier) {
// return carrier;
// }
// };
// Metric metric = new Metric() {
// 
// @Override
// public double distance(SparseVector v1, SparseVector v2) {
// return 0.0;
// }
// };
// SparseVector vector = new SparseVector(new double[] { 2.2 });
// Instance instance1 = new Instance(vector, null, null, null);
// Instance instance2 = new Instance(vector, null, null, null);
// Instance instance3 = new Instance(vector, null, null, null);
// InstanceList instances = new InstanceList(pipe);
// instances.addThruPipe(instance1);
// instances.addThruPipe(instance2);
// instances.addThruPipe(instance3);
// KMeans kMeans = new KMeans(pipe, 1, metric, KMeans.EMPTY_DROP);
// Clustering clustering = kMeans.cluster(instances);
// assertNotNull(clustering);
// assertEquals(1, clustering.getNumClusters());
// int[] labels = clustering.getClusterLabel();
// assertEquals(3, labels.length);
// assertEquals(0, labels[0]);
// assertEquals(0, labels[1]);
// assertEquals(0, labels[2]);
}

// @Test
public void testClusterWithSingleInstanceAndMultipleClustersShouldReturnSingleCluster() {
// Pipe pipe = new Pipe() {
// 
// @Override
// public Instance pipe(Instance carrier) {
// return carrier;
// }
// };
// Metric metric = new Metric() {
// 
// @Override
// public double distance(SparseVector v1, SparseVector v2) {
// return 0.0;
// }
// };
// SparseVector vector = new SparseVector(new double[] { 9.0 });
// Instance instance = new Instance(vector, null, null, null);
// InstanceList instances = new InstanceList(pipe);
// instances.addThruPipe(instance);
// KMeans kMeans = new KMeans(pipe, 5, metric, KMeans.EMPTY_DROP);
// Clustering clustering = kMeans.cluster(instances);
// assertNotNull(clustering);
// assertEquals(1, clustering.getNumClusters());
// int[] labels = clustering.getClusterLabel();
// assertEquals(1, labels.length);
// assertEquals(0, labels[0]);
}

// @Test
public void testClusterWithNullCentroidFromEmptyClusterShouldFallThroughDefaultAndReturnNull() {
// Pipe pipe = new Pipe() {
// 
// @Override
// public Instance pipe(Instance carrier) {
// return carrier;
// }
// };
// Metric metric = new Metric() {
// 
// @Override
// public double distance(SparseVector v1, SparseVector v2) {
// return 1.0;
// }
// };
// Instance instance = new Instance(new SparseVector(new double[] { 3.0 }), null, null, null);
// InstanceList instances = new InstanceList(pipe);
// instances.addThruPipe(instance);
// KMeans kMeans = new KMeans(pipe, 5, metric, 99);
// Clustering clustering = kMeans.cluster(instances);
// assertNull(clustering);
}

// @Test
public void testClusterWithAllInstancesEquidistantShouldAssignToFirstCluster() {
// Pipe pipe = new Pipe() {
// 
// @Override
// public Instance pipe(Instance carrier) {
// return carrier;
// }
// };
// Metric metric = new Metric() {
// 
// @Override
// public double distance(SparseVector v1, SparseVector v2) {
// return 1.0;
// }
// };
// Instance a = new Instance(new SparseVector(new double[] { 1.0 }), null, null, null);
// Instance b = new Instance(new SparseVector(new double[] { 2.0 }), null, null, null);
// Instance c = new Instance(new SparseVector(new double[] { 3.0 }), null, null, null);
// InstanceList instances = new InstanceList(pipe);
// instances.addThruPipe(a);
// instances.addThruPipe(b);
// instances.addThruPipe(c);
// KMeans kMeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_SINGLE);
// Clustering clustering = kMeans.cluster(instances);
// assertNotNull(clustering);
// assertEquals(2, clustering.getNumClusters());
// int[] labels = clustering.getClusterLabel();
// assertEquals(3, labels.length);
}

// @Test
public void testClusterWithVeryHighNumberOfClustersAndFewInstancesEmptyDrop() {
// Pipe pipe = new Pipe() {
// 
// @Override
// public Instance pipe(Instance carrier) {
// return carrier;
// }
// };
// Metric metric = new Metric() {
// 
// @Override
// public double distance(SparseVector v1, SparseVector v2) {
// double diff = v1.valueAtLocation(0) - v2.valueAtLocation(0);
// return Math.abs(diff);
// }
// };
// InstanceList instances = new InstanceList(pipe);
// instances.addThruPipe(new Instance(new SparseVector(new double[] { 1.0 }), null, null, null));
// instances.addThruPipe(new Instance(new SparseVector(new double[] { 2.0 }), null, null, null));
// KMeans kMeans = new KMeans(pipe, 100, metric, KMeans.EMPTY_DROP);
// Clustering clustering = kMeans.cluster(instances);
// assertNotNull(clustering);
// assertTrue(clustering.getNumClusters() <= 100);
// int[] labels = clustering.getClusterLabel();
// assertEquals(2, labels.length);
}

// @Test
public void testClusterWithOnlyEmptySparseVectorsShouldIgnoreThemInInitialization() {
// Pipe pipe = new Pipe() {
// 
// @Override
// public Instance pipe(Instance carrier) {
// return carrier;
// }
// };
// Metric metric = new Metric() {
// 
// @Override
// public double distance(SparseVector v1, SparseVector v2) {
// return 0.0;
// }
// };
// SparseVector empty1 = new SparseVector(new double[] {});
// SparseVector empty2 = new SparseVector(new double[] {});
// InstanceList instances = new InstanceList(pipe);
// instances.addThruPipe(new Instance(empty1, null, null, null));
// instances.addThruPipe(new Instance(empty2, null, null, null));
// KMeans kMeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_DROP);
// Clustering clustering = kMeans.cluster(instances);
// assertNull(clustering);
}

// @Test
public void testClusterWithNullVectorCastShouldThrowException() {
// Pipe pipe = new Pipe() {
// 
// @Override
// public Instance pipe(Instance carrier) {
// return carrier;
// }
// };
// Metric metric = new Metric() {
// 
// @Override
// public double distance(SparseVector v1, SparseVector v2) {
// return 1.0;
// }
// };
// InstanceList instances = new InstanceList(pipe);
// instances.addThruPipe(new Instance(null, null, null, null));
// instances.addThruPipe(new Instance(null, null, null, null));
// KMeans kMeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_DROP);
boolean exceptionThrown = false;
try {
// kMeans.cluster(instances);
} catch (ClassCastException e) {
exceptionThrown = true;
}
// assertTrue(exceptionThrown);
}

// @Test
public void testClusterWithMultipleEmptyClustersTriggeredInSameIterationShouldHandleGracefully() {
// Pipe pipe = new Pipe() {
// 
// @Override
// public Instance pipe(Instance carrier) {
// return carrier;
// }
// };
// Metric metric = new Metric() {
// 
// @Override
// public double distance(SparseVector v1, SparseVector v2) {
// return Math.abs(v1.valueAtLocation(0) - v2.valueAtLocation(0));
// }
// };
// Instance a = new Instance(new SparseVector(new double[] { 10.0 }), null, null, null);
// Instance b = new Instance(new SparseVector(new double[] { 100.0 }), null, null, null);
// InstanceList instances = new InstanceList(pipe);
// instances.addThruPipe(a);
// instances.addThruPipe(b);
// KMeans kMeans = new KMeans(pipe, 5, metric, KMeans.EMPTY_DROP);
// Clustering clustering = kMeans.cluster(instances);
// assertNotNull(clustering);
// assertTrue(clustering.getNumClusters() <= 5);
// int[] labels = clustering.getClusterLabel();
// assertEquals(2, labels.length);
// for (int label : labels) {
// assertTrue(label >= 0 && label < clustering.getNumClusters());
// }
}

// @Test
public void testClusterWithMaxIterationsExceededReturnsProperClustering() {
int originalMaxIter = KMeans.MAX_ITER;
KMeans.MAX_ITER = 1;
// Pipe pipe = new Pipe() {
// 
// @Override
// public Instance pipe(Instance carrier) {
// return carrier;
// }
// };
// Metric metric = new Metric() {
// 
// @Override
// public double distance(SparseVector v1, SparseVector v2) {
// return Math.abs(v1.valueAtLocation(0) - v2.valueAtLocation(0));
// }
// };
// Instance instance1 = new Instance(new SparseVector(new double[] { 1.0 }), null, null, null);
// Instance instance2 = new Instance(new SparseVector(new double[] { 10.0 }), null, null, null);
// InstanceList instances = new InstanceList(pipe);
// instances.addThruPipe(instance1);
// instances.addThruPipe(instance2);
// KMeans kMeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_SINGLE);
// Clustering clustering = kMeans.cluster(instances);
// assertNotNull(clustering);
// assertEquals(2, clustering.getNumClusters());
KMeans.MAX_ITER = originalMaxIter;
}
}
