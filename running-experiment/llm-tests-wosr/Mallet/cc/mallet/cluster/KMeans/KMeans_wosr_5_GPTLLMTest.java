public class KMeans_wosr_5_GPTLLMTest { 

 @Test
  public void testConstructorWithAllParameters() {
    Pipe pipe = new Noop();
    Metric metric = new EuclideanDistance();
    KMeans kMeans = new KMeans(pipe, 3, metric, KMeans.EMPTY_DROP);
    assertNotNull(kMeans);
    assertEquals(0, kMeans.getClusterMeans().size());
  }
@Test
  public void testConstructorWithEmptyErrorDefault() {
    Pipe pipe = new Noop();
    Metric metric = new EuclideanDistance();
    KMeans kMeans = new KMeans(pipe, 2, metric);
    assertNotNull(kMeans);
    assertEquals(0, kMeans.getClusterMeans().size());
  }
@Test
  public void testClusterWithIdenticalInstanceVectors() {
    Pipe pipe = new Noop();
    Metric metric = new EuclideanDistance();
    KMeans kMeans = new KMeans(pipe, 1, metric);

    InstanceList instances = new InstanceList(pipe);
    double[] values = { 1.0, 2.0, 3.0 };
    SparseVector vector = new SparseVector(values);
    Instance instance1 = new Instance(vector, null, null, null);
    Instance instance2 = new Instance(vector, null, null, null);

    instances.addThruPipe(instance1);
    instances.addThruPipe(instance2);

    Clustering clustering = kMeans.cluster(instances);

    assertNotNull(clustering);
    assertEquals(1, clustering.getNumClusters());
    assertEquals(2, clustering.getCluster(0).size());

    ArrayList<SparseVector> means = kMeans.getClusterMeans();
    assertEquals(1, means.size());
    assertNotNull(means.get(0));
  }
@Test
  public void testClusterWithTwoDistinctClusters() {
    Pipe pipe = new Noop();
    Metric metric = new EuclideanDistance();
    KMeans kMeans = new KMeans(pipe, 2, metric);

    InstanceList instances = new InstanceList(pipe);
    double[] v1 = { 1.0, 2.0, 3.0 };
    double[] v2 = { 50.0, 50.0, 50.0 };
    SparseVector vector1 = new SparseVector(v1);
    SparseVector vector2 = new SparseVector(v2);

    instances.addThruPipe(new Instance(vector1, null, "inst1", null));
    instances.addThruPipe(new Instance(vector1, null, "inst2", null));
    instances.addThruPipe(new Instance(vector2, null, "inst3", null));
    instances.addThruPipe(new Instance(vector2, null, "inst4", null));

    Clustering clustering = kMeans.cluster(instances);

    assertNotNull(clustering);
    assertEquals(2, clustering.getNumClusters());
    int totalSize = clustering.getCluster(0).size() + clustering.getCluster(1).size();
    assertEquals(4, totalSize);

    ArrayList<SparseVector> means = kMeans.getClusterMeans();
    assertEquals(2, means.size());
  }
@Test
  public void testClusterWithEmptyClusterErrorAction() {
    Pipe pipe = new Noop();
    Metric metric = new EuclideanDistance();
    KMeans kMeans = new KMeans(pipe, 5, metric, KMeans.EMPTY_ERROR);

    InstanceList instances = new InstanceList(pipe);
    double[] v = { 1.0, 2.0, 3.0 };
    for (int i = 0; i < 4; i++) {
      instances.addThruPipe(new Instance(new SparseVector(v), null, "inst" + i, null));
    }

    Clustering clustering = kMeans.cluster(instances);

    assertNull(clustering);
  }
@Test
  public void testClusterWithEmptyClusterDropAction() {
    Pipe pipe = new Noop();
    Metric metric = new EuclideanDistance();
    KMeans kMeans = new KMeans(pipe, 4, metric, KMeans.EMPTY_DROP);

    InstanceList instances = new InstanceList(pipe);
    double[] v1 = { 1.0, 2.0, 3.0 };
    for (int i = 0; i < 3; i++) {
      instances.addThruPipe(new Instance(new SparseVector(v1), null, "inst" + i, null));
    }

    Clustering clustering = kMeans.cluster(instances);

    assertNotNull(clustering);
    assertTrue(clustering.getNumClusters() <= 4);
  }
@Test
  public void testClusterWithEmptyClusterSingleAction() {
    Pipe pipe = new Noop();
    Metric metric = new EuclideanDistance();
    KMeans kMeans = new KMeans(pipe, 3, metric, KMeans.EMPTY_SINGLE);

    InstanceList instances = new InstanceList(pipe);
    double[] v1 = { 1.0, 2.0, 3.0 };
    double[] v2 = { 100.0, 100.0, 100.0 };
    double[] v3 = { -100.0, -100.0, -100.0 };
    instances.addThruPipe(new Instance(new SparseVector(v1), null, "inst1", null));
    instances.addThruPipe(new Instance(new SparseVector(v2), null, "inst2", null));
    instances.addThruPipe(new Instance(new SparseVector(v3), null, "inst3", null));

    Clustering clustering = kMeans.cluster(instances);

    assertNotNull(clustering);
    assertEquals(3, clustering.getNumClusters());
  }
@Test
  public void testClusterWithZeroInstances() {
    Pipe pipe = new Noop();
    Metric metric = new EuclideanDistance();
    KMeans kMeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_ERROR);

    InstanceList instances = new InstanceList(pipe);

    Clustering clustering = kMeans.cluster(instances);

    assertNull(clustering);
  }
@Test
  public void testGetClusterMeansReturnsCorrectSizeAfterClustering() {
    Pipe pipe = new Noop();
    Metric metric = new EuclideanDistance();
    KMeans kMeans = new KMeans(pipe, 2, metric);

    InstanceList instances = new InstanceList(pipe);
    instances.addThruPipe(new Instance(new SparseVector(new double[] { 1.0, 2.0 }), null, null, null));
    instances.addThruPipe(new Instance(new SparseVector(new double[] { 5.0, 6.0 }), null, null, null));

    kMeans.cluster(instances);

    ArrayList<SparseVector> means = kMeans.getClusterMeans();
    assertEquals(2, means.size());
    assertNotNull(means.get(0));
    assertNotNull(means.get(1));
  }
@Test
  public void testClusterConvergesQuicklyWhenAllVectorsAreTheSame() {
    Pipe pipe = new Noop();
    Metric metric = new EuclideanDistance();
    KMeans kMeans = new KMeans(pipe, 1, metric);

    InstanceList instances = new InstanceList(pipe);
    SparseVector v = new SparseVector(new double[] { 1.0, 2.0, 3.0 });
    instances.addThruPipe(new Instance(v, null, "a", null));
    instances.addThruPipe(new Instance(v, null, "b", null));
    instances.addThruPipe(new Instance(v, null, "c", null));

    Clustering clustering = kMeans.cluster(instances);

    assertNotNull(clustering);
    assertEquals(1, clustering.getNumClusters());
    assertEquals(3, clustering.getCluster(0).size());
  }
@Test
  public void testClusterWithVaryingDensityDataPoints() {
    Pipe pipe = new Noop();
    Metric metric = new EuclideanDistance();
    KMeans kMeans = new KMeans(pipe, 3, metric);

    InstanceList instances = new InstanceList(pipe);
    instances.addThruPipe(new Instance(new SparseVector(new double[] { 1.0, 1.0 }), null, null, null));
    instances.addThruPipe(new Instance(new SparseVector(new double[] { 1.1, 1.1 }), null, null, null));
    instances.addThruPipe(new Instance(new SparseVector(new double[] { 50.0, 50.0 }), null, null, null));
    instances.addThruPipe(new Instance(new SparseVector(new double[] { 100.0, 100.0 }), null, null, null));

    Clustering clustering = kMeans.cluster(instances);

    assertNotNull(clustering);
    assertEquals(3, clustering.getNumClusters());
  } 
}