public class KMeans_wosr_2_GPTLLMTest { 

 @Test
  public void testBasicClusteringWithTwoClusters() {
    Pipe pipe = new Noop();
    Metric metric = new EuclideanMetric();
    KMeans kMeans = new KMeans(pipe, 2, metric);

    InstanceList instances = new InstanceList(pipe);
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{0.0}, 1), null, null, null));
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{0.1}, 1), null, null, null));
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{10.0}, 1), null, null, null));
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{10.1}, 1), null, null, null));

    Clustering result = kMeans.cluster(instances);

    assertNotNull(result);
    assertEquals(2, result.getNumClusters());
    assertEquals(instances.size(), result.getClustersArray().length);
  }
@Test
  public void testEmptyClusterWithEmptyErrorFails() {
    Pipe pipe = new Noop();
    Metric metric = new EuclideanMetric();
    KMeans kMeans = new KMeans(pipe, 10, metric, KMeans.EMPTY_ERROR);

    InstanceList instances = new InstanceList(pipe);
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{1.0}, 1), null, null, null));
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{2.0}, 1), null, null, null));
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{3.0}, 1), null, null, null));

    Clustering result = kMeans.cluster(instances);
    assertNull(result);
  }
@Test
  public void testEmptyClusterWithDropPreservesValidClustering() {
    Pipe pipe = new Noop();
    Metric metric = new EuclideanMetric();
    KMeans kMeans = new KMeans(pipe, 5, metric, KMeans.EMPTY_DROP);

    InstanceList instances = new InstanceList(pipe);
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{1.0}, 1), null, null, null));
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{2.0}, 1), null, null, null));
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{3.0}, 1), null, null, null));

    Clustering result = kMeans.cluster(instances);

    assertNotNull(result);
    assertTrue(result.getNumClusters() <= 5); 
  }
@Test
  public void testEmptyClusterWithSingleFallbackSucceeds() {
    Pipe pipe = new Noop();
    Metric metric = new EuclideanMetric();
    KMeans kMeans = new KMeans(pipe, 3, metric, KMeans.EMPTY_SINGLE);

    InstanceList instances = new InstanceList(pipe);
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{0.0}, 1), null, null, null));
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{5.0}, 1), null, null, null));
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{10.0}, 1), null, null, null));

    Clustering result = kMeans.cluster(instances);

    assertNotNull(result);
    assertEquals(3, result.getNumClusters());
  }
@Test
  public void testIdenticalVectorsGetsSingleCluster() {
    Pipe pipe = new Noop();
    Metric metric = new EuclideanMetric();
    KMeans kMeans = new KMeans(pipe, 1, metric);

    InstanceList instances = new InstanceList(pipe);
    for (int i = 0; i < 3; i++) {
      instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{1.0}, 1), null, null, null));
    }

    Clustering result = kMeans.cluster(instances);

    assertNotNull(result);
    assertEquals(1, result.getNumClusters());
    int[] labels = result.getClustersArray();
    for (int label : labels) {
      assertEquals(0, label);
    }
  }
@Test
  public void testClusterMeansReturnsNonNullAfterClustering() {
    Pipe pipe = new Noop();
    Metric metric = new EuclideanMetric();
    KMeans kMeans = new KMeans(pipe, 2, metric);

    InstanceList instances = new InstanceList(pipe);
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{1.0}, 1), null, null, null));
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{5.0}, 1), null, null, null));
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{9.0}, 1), null, null, null));
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{10.0}, 1), null, null, null));

    Clustering result = kMeans.cluster(instances);
    ArrayList<SparseVector> means = kMeans.getClusterMeans();

    assertNotNull(means);
    assertTrue(means.size() > 0);
  }
@Test
  public void testClusterFailsWhenAllVectorsAreEmpty() {
    Pipe pipe = new Noop();
    Metric metric = new EuclideanMetric();
    KMeans kMeans = new KMeans(pipe, 2, metric);

    InstanceList instances = new InstanceList(pipe);
    instances.addThruPipe(new Instance(new SparseVector(10, new int[]{}, new double[]{}), null, null, null));
    instances.addThruPipe(new Instance(new SparseVector(10, new int[]{}, new double[]{}), null, null, null));

    Clustering result = kMeans.cluster(instances);
    assertNull(result);
  }
@Test
  public void testClusterWithSingleInstanceAndMultipleClusters() {
    Pipe pipe = new Noop();
    Metric metric = new EuclideanMetric();
    KMeans kMeans = new KMeans(pipe, 3, metric, KMeans.EMPTY_SINGLE);

    InstanceList instances = new InstanceList(pipe);
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{42.0}, 1), null, null, null));

    Clustering result = kMeans.cluster(instances);

    assertNotNull(result);
    assertTrue(result.getNumClusters() >= 1 && result.getNumClusters() <= 3);
    assertEquals(1, result.getClustersArray().length);
  }
@Test
  public void testClusterProducesDifferentResultsWithDifferentK() {
    Pipe pipe = new Noop();
    Metric metric = new EuclideanMetric();

    KMeans kMeans2 = new KMeans(pipe, 2, metric);
    KMeans kMeans3 = new KMeans(pipe, 3, metric);

    InstanceList instances = new InstanceList(pipe);
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{0.0}, 1), null, null, null));
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{1.0}, 1), null, null, null));
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{10.0}, 1), null, null, null));
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{11.0}, 1), null, null, null));
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{20.0}, 1), null, null, null));

    Clustering result2 = kMeans2.cluster(instances);
    Clustering result3 = kMeans3.cluster(instances);

    assertNotNull(result2);
    assertNotNull(result3);
    assertEquals(2, result2.getNumClusters());
    assertEquals(3, result3.getNumClusters());
  }
@Test
  public void testClusterWithZeroVectorsAddedOnlyOnce() {
    Pipe pipe = new Noop();
    Metric metric = new EuclideanMetric();
    KMeans kMeans = new KMeans(pipe, 1, metric);

    SparseVector zeroVector = new SparseVector(5);
    InstanceList instances = new InstanceList(pipe);
    instances.addThruPipe(new Instance(zeroVector, null, "zero", null));

    Clustering result = kMeans.cluster(instances);

    assertNull(result);
  } 
}