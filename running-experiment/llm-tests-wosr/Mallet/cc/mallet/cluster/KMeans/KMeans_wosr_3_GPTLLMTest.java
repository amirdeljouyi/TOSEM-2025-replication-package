public class KMeans_wosr_3_GPTLLMTest { 

 @Test
  public void testBasicClusteringWithTwoClusters() {
    Pipe pipe = new Noop();
    Metric metric = new EuclideanDistance();
    int numClusters = 2;

    InstanceList instanceList = new InstanceList(pipe);
    instanceList.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{1.0}, 1), null, null, null));
    instanceList.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{1.1}, 1), null, null, null));
    instanceList.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{5.0}, 1), null, null, null));
    instanceList.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{5.2}, 1), null, null, null));

    KMeans kMeans = new KMeans(pipe, numClusters, metric);
    Clustering clustering = kMeans.cluster(instanceList);

    assertNotNull(clustering);
    assertEquals(instanceList.size(), clustering.getClusterLabels().length);
    assertEquals(numClusters, clustering.getNumClusters());

    int[] labels = clustering.getClusterLabels();

    int firstClusterLabel = labels[0];
    for (int i = 0; i < 2; i++) {
      assertEquals(firstClusterLabel, labels[i]);
    }

    int secondClusterLabel = labels[2];
    for (int i = 2; i < 4; i++) {
      assertEquals(secondClusterLabel, labels[i]);
    }

    assertNotEquals(firstClusterLabel, secondClusterLabel);
  }
@Test
  public void testClusterMeansAfterClustering() {
    Pipe pipe = new Noop();
    Metric metric = new EuclideanDistance();
    int numClusters = 2;

    InstanceList instances = new InstanceList(pipe);
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{1.0}, 1), null, null, null));
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{1.1}, 1), null, null, null));
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{5.0}, 1), null, null, null));
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{5.1}, 1), null, null, null));

    KMeans kMeans = new KMeans(pipe, numClusters, metric);
    Clustering clustering = kMeans.cluster(instances);

    ArrayList<SparseVector> centroids = kMeans.getClusterMeans();
    assertEquals(numClusters, centroids.size());

    for (SparseVector centroid : centroids) {
      assertNotNull(centroid);
      assertTrue(centroid.numLocations() > 0);
    }
  }
@Test
  public void testEmptyClusterDropBehavior() {
    Pipe pipe = new Noop();
    Metric metric = new EuclideanDistance();
    int numClusters = 3;

    InstanceList instances = new InstanceList(pipe);
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{1.0}, 1), null, null, null));
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{1.2}, 1), null, null, null));
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{5.0}, 1), null, null, null));

    KMeans kMeans = new KMeans(pipe, numClusters, metric, KMeans.EMPTY_DROP);
    Clustering clustering = kMeans.cluster(instances);

    assertNotNull(clustering);
    int actualNumClusters = clustering.getNumClusters();
    assertTrue(actualNumClusters <= numClusters);
    assertTrue(clustering.getClusterLabels().length == instances.size());
  }
@Test
  public void testEmptyClusterErrorReturnsNull() {
    Pipe pipe = new Noop();
    Metric metric = new EuclideanDistance();
    int numClusters = 3;

    InstanceList instances = new InstanceList(pipe);
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{0}, 1), null, null, null));
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{0}, 1), null, null, null));
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{0}, 1), null, null, null));

    KMeans kMeans = new KMeans(pipe, numClusters, metric, KMeans.EMPTY_ERROR);
    Clustering clustering = kMeans.cluster(instances);

    assertNull(clustering);
  }
@Test
  public void testEmptyClusterSingleFallbackStrategy() {
    Pipe pipe = new Noop();
    Metric metric = new EuclideanDistance();
    int numClusters = 2;

    InstanceList instances = new InstanceList(pipe);
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{2.0}, 1), null, null, null));
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{2.1}, 1), null, null, null));
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{100.0}, 1), null, null, null));

    KMeans kMeans = new KMeans(pipe, numClusters, metric, KMeans.EMPTY_SINGLE);
    Clustering clustering = kMeans.cluster(instances);

    assertNotNull(clustering);
    assertEquals(instances.size(), clustering.getClusterLabels().length);
    assertTrue(clustering.getNumClusters() <= numClusters);
  }
@Test
  public void testClusterConvergenceCondition() {
    Pipe pipe = new Noop();
    Metric metric = new EuclideanDistance();
    int numClusters = 2;

    SparseVector v1 = new SparseVector(new int[]{0}, new double[]{10.0}, 1);
    SparseVector v2 = new SparseVector(new int[]{0}, new double[]{10.1}, 1);
    SparseVector v3 = new SparseVector(new int[]{0}, new double[]{10.2}, 1);
    SparseVector v4 = new SparseVector(new int[]{0}, new double[]{10.3}, 1);

    InstanceList instances = new InstanceList(pipe);
    instances.addThruPipe(new Instance(v1, null, null, null));
    instances.addThruPipe(new Instance(v2, null, null, null));
    instances.addThruPipe(new Instance(v3, null, null, null));
    instances.addThruPipe(new Instance(v4, null, null, null));

    KMeans kMeans = new KMeans(pipe, numClusters, metric);

    Clustering clustering = kMeans.cluster(instances);
    assertNotNull(clustering);
    assertEquals(4, clustering.getClusterLabels().length);
    assertTrue(clustering.getNumClusters() <= numClusters);
  }
@Test
  public void testIdenticalInstancesClusterTogether() {
    Pipe pipe = new Noop();
    Metric metric = new EuclideanDistance();
    int numClusters = 1;

    SparseVector v = new SparseVector(new int[]{0}, new double[]{5.0}, 1);

    InstanceList instances = new InstanceList(pipe);
    instances.addThruPipe(new Instance(v, null, null, null));
    instances.addThruPipe(new Instance(v, null, null, null));
    instances.addThruPipe(new Instance(v, null, null, null));

    KMeans kMeans = new KMeans(pipe, numClusters, metric);
    Clustering clustering = kMeans.cluster(instances);

    assertNotNull(clustering);
    assertEquals(instances.size(), clustering.getClusterLabels().length);
    assertEquals(1, clustering.getNumClusters());

    int clusterLabel = clustering.getClusterLabels()[0];
    assertEquals(clusterLabel, clustering.getClusterLabels()[1]);
    assertEquals(clusterLabel, clustering.getClusterLabels()[2]);
  }
@Test
  public void testClusterWithZeroDimensionVectorIgnored() {
    Pipe pipe = new Noop();
    Metric metric = new EuclideanDistance();
    int numClusters = 2;

    InstanceList instances = new InstanceList(pipe);
    instances.addThruPipe(new Instance(new SparseVector(new int[]{}, new double[]{}, 0), null, null, null));
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{4.0}, 1), null, null, null));
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{6.0}, 1), null, null, null));

    KMeans kMeans = new KMeans(pipe, numClusters, metric);
    Clustering clustering = kMeans.cluster(instances);

    assertNotNull(clustering);
    assertEquals(instances.size(), clustering.getClusterLabels().length);
  }
@Test
  public void testClusterersAreIndependent() {
    Pipe pipe = new Noop();
    Metric metric = new EuclideanDistance();

    InstanceList instances = new InstanceList(pipe);
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{1.0}, 1), null, null, null));
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{2.0}, 1), null, null, null));
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{3.0}, 1), null, null, null));

    KMeans kMeans1 = new KMeans(pipe, 2, metric);
    KMeans kMeans2 = new KMeans(pipe, 2, metric);

    Clustering clustering1 = kMeans1.cluster(instances);
    Clustering clustering2 = kMeans2.cluster(instances);

    assertNotNull(clustering1);
    assertNotNull(clustering2);
    assertNotSame(kMeans1.getClusterMeans(), kMeans2.getClusterMeans());
  } 
}