public class KMeans_wosr_1_GPTLLMTest { 

 @Test
  public void testClusterWithTwoDistinctClusters() {
    Pipe pipe = new Noop();
    Metric metric = new EuclideanDistance();
    KMeans kMeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_ERROR);

    InstanceList instances = new InstanceList(pipe);
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{1.0}, 1), null, "inst1", null));
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{1.1}, 1), null, "inst2", null));
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{5.0}, 1), null, "inst3", null));
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{5.2}, 1), null, "inst4", null));

    Clustering clustering = kMeans.cluster(instances);
    assertNotNull(clustering);
    assertEquals(2, clustering.getNumClusters());

    int[] clusterAssignmentCounts = new int[2];
    for (int i = 0; i < instances.size(); i++) {
      int cluster = clustering.getClusterLabel(i);
      assertTrue(cluster == 0 || cluster == 1);
      clusterAssignmentCounts[cluster]++;
    }

    assertTrue(clusterAssignmentCounts[0] == 2 || clusterAssignmentCounts[1] == 2);
  }
@Test
  public void testClusterWithSingleCluster() {
    Pipe pipe = new Noop();
    Metric metric = new EuclideanDistance();
    KMeans kMeans = new KMeans(pipe, 1, metric, KMeans.EMPTY_ERROR);

    InstanceList instances = new InstanceList(pipe);
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{1.0}, 1), null, "a", null));
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{1.2}, 1), null, "b", null));

    Clustering clustering = kMeans.cluster(instances);
    assertNotNull(clustering);
    assertEquals(1, clustering.getNumClusters());
    assertEquals(0, clustering.getClusterLabel(0));
    assertEquals(0, clustering.getClusterLabel(1));
  }
@Test
  public void testEmptyClusterReturnsNullWithErrorAction() {
    Pipe pipe = new Noop();
    Metric metric = new EuclideanDistance();
    KMeans kMeans = new KMeans(pipe, 5, metric, KMeans.EMPTY_ERROR);

    InstanceList instances = new InstanceList(pipe);
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{0.0}, 1), null, "1", null));
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{0.1}, 1), null, "2", null));

    Clustering result = kMeans.cluster(instances);
    assertNull(result);
  }
@Test
  public void testEmptyClustersAreDropped() {
    Pipe pipe = new Noop();
    Metric metric = new EuclideanDistance();
    KMeans kMeans = new KMeans(pipe, 3, metric, KMeans.EMPTY_DROP);

    InstanceList instances = new InstanceList(pipe);
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{1.0}, 1), null, "1", null));
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{1.1}, 1), null, "2", null));

    Clustering result = kMeans.cluster(instances);
    assertNotNull(result);
    assertTrue(result.getNumClusters() < 3);
  }
@Test
  public void testEmptyClusterActionSingleAssignsNewCentroid() {
    Pipe pipe = new Noop();
    Metric metric = new EuclideanDistance();
    KMeans kMeans = new KMeans(pipe, 3, metric, KMeans.EMPTY_SINGLE);

    InstanceList instances = new InstanceList(pipe);
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{1.0}, 1), null, "1", null));
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{3.0}, 1), null, "2", null));
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{5.0}, 1), null, "3", null));

    Clustering result = kMeans.cluster(instances);
    assertNotNull(result);
    assertTrue(result.getNumClusters() <= 3);
  }
@Test
  public void testIdenticalInstancesClusteredTogether() {
    Pipe pipe = new Noop();
    Metric metric = new EuclideanDistance();
    KMeans kMeans = new KMeans(pipe, 2, metric);

    SparseVector identicalVector = new SparseVector(new int[]{0}, new double[]{2.0}, 1);
    InstanceList instances = new InstanceList(pipe);
    instances.addThruPipe(new Instance(identicalVector, null, "a", null));
    instances.addThruPipe(new Instance(identicalVector, null, "b", null));
    instances.addThruPipe(new Instance(identicalVector, null, "c", null));

    Clustering clustering = kMeans.cluster(instances);
    assertNotNull(clustering);

    int firstCluster = clustering.getClusterLabel(0);
    for (int i = 1; i < 3; i++) {
      assertEquals(firstCluster, clustering.getClusterLabel(i));
    }
  }
@Test
  public void testClusterMeansAccessAfterClustering() {
    Pipe pipe = new Noop();
    Metric metric = new EuclideanDistance();
    KMeans kMeans = new KMeans(pipe, 2, metric);

    InstanceList instances = new InstanceList(pipe);
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{0.0}, 1), null, "X", null));
    instances.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{10.0}, 1), null, "Y", null));

    Clustering clustering = kMeans.cluster(instances);
    assertNotNull(clustering);

    ArrayList<SparseVector> means = kMeans.getClusterMeans();
    assertNotNull(means);
    assertEquals(clustering.getNumClusters(), means.size());
  }
@Test
  public void testClusterWithZeroSparseVectorsIsIgnored() {
    Pipe pipe = new Noop();
    Metric metric = new EuclideanDistance();
    KMeans kMeans = new KMeans(pipe, 2, metric);

    Instance good1 = new Instance(new SparseVector(new int[]{0}, new double[]{3.0}, 1), null, "good1", null);
    Instance good2 = new Instance(new SparseVector(new int[]{0}, new double[]{7.0}, 1), null, "good2", null);
    Instance zeroVectorInstance = new Instance(new SparseVector(new int[]{}, new double[]{}, 0), null, "zero", null);

    InstanceList instances = new InstanceList(pipe);
    instances.addThruPipe(good1);
    instances.addThruPipe(good2);
    instances.addThruPipe(zeroVectorInstance);

    Clustering clustering = kMeans.cluster(instances);
    assertNotNull(clustering);
    assertEquals(2, clustering.getNumClusters());
  }
@Test
  public void testClusterWithIncompatiblePipeFailsAssertion() {
    Pipe originalPipe = new Noop();
    Pipe differentPipe = new Noop();
    Metric metric = new EuclideanDistance();
    KMeans kMeans = new KMeans(originalPipe, 2, metric);

    InstanceList instancesWithDifferentPipe = new InstanceList(differentPipe);
    instancesWithDifferentPipe.addThruPipe(new Instance(new SparseVector(new int[]{0}, new double[]{1.0}, 1), null, "bad", null));

    boolean assertionErrorCaught = false;
    try {
      kMeans.cluster(instancesWithDifferentPipe);
    } catch (AssertionError e) {
      assertionErrorCaught = true;
    }
    assertTrue(assertionErrorCaught);
  } 
}