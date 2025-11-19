public class KMeans_wosr_4_GPTLLMTest { 

 @Test
  public void testClusterBasicTwoSimpleClustersEuclidean() {
    Pipe pipe = new Noop();
    Metric metric = new Metric() {
      @Override
      public double distance(SparseVector a, SparseVector b) {
        double sum = 0.0;
        for (int i = 0; i < a.numLocations(); i++) {
          int index = a.indexAtLocation(i);
          double aVal = a.valueAtLocation(i);
          double bVal = b.value(index);
          double diff = aVal - bVal;
          sum += diff * diff;
        }
        for (int j = 0; j < b.numLocations(); j++) {
          int index = b.indexAtLocation(j);
          if (a.value(index) == 0.0) {
            double diff = b.valueAtLocation(j);
            sum += diff * diff;
          }
        }
        return Math.sqrt(sum);
      }
    };

    SparseVector vec1 = new SparseVector(new int[]{0}, new double[]{1.0}, 1);
    SparseVector vec2 = new SparseVector(new int[]{0}, new double[]{1.1}, 1);
    SparseVector vec3 = new SparseVector(new int[]{0}, new double[]{9.9}, 1);
    SparseVector vec4 = new SparseVector(new int[]{0}, new double[]{10.0}, 1);

    InstanceList instances = new InstanceList(pipe);
    instances.add(new Instance(vec1, null, null, null));
    instances.add(new Instance(vec2, null, null, null));
    instances.add(new Instance(vec3, null, null, null));
    instances.add(new Instance(vec4, null, null, null));

    KMeans kMeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_DROP);
    Clustering clustering = kMeans.cluster(instances);

    Assert.assertNotNull(clustering);
    Assert.assertEquals(2, clustering.getNumClusters());

    int[] sizes = new int[2];
    for (int i = 0; i < clustering.getClusterAssignments().length; i++) {
      int cluster = clustering.getClusterAssignments()[i];
      Assert.assertTrue(cluster >= 0 && cluster < 2);
      sizes[cluster]++;
    }

    Assert.assertEquals(2, sizes[0] + sizes[1]);
    Assert.assertTrue(sizes[0] == 2 || sizes[1] == 2);
  }
@Test
  public void testClusterWithOneEmptyFeatureVector() {
    Pipe pipe = new Noop();
    Metric metric = new Metric() {
      @Override
      public double distance(SparseVector a, SparseVector b) {
        return 0.0;
      }
    };

    SparseVector vec1 = new SparseVector(new int[]{0}, new double[]{1.0}, 1);
    SparseVector emptyVec = new SparseVector(new int[]{}, new double[]{}, 0);

    InstanceList instances = new InstanceList(pipe);
    instances.add(new Instance(vec1, null, null, null));
    instances.add(new Instance(emptyVec, null, null, null));

    KMeans kMeans = new KMeans(pipe, 1, metric, KMeans.EMPTY_ERROR);
    Clustering clustering = kMeans.cluster(instances);

    Assert.assertNotNull(clustering);
    Assert.assertEquals(1, clustering.getNumClusters());
    Assert.assertEquals(2, clustering.getClusterAssignments().length);
  }
@Test
  public void testClusterAllIdenticalPoints() {
    Pipe pipe = new Noop();
    Metric metric = new Metric() {
      @Override
      public double distance(SparseVector a, SparseVector b) {
        return 0.0;
      }
    };

    SparseVector vec = new SparseVector(new int[]{0}, new double[]{5.0}, 1);

    InstanceList instances = new InstanceList(pipe);
    instances.add(new Instance(vec, null, null, null));
    instances.add(new Instance(vec, null, null, null));
    instances.add(new Instance(vec, null, null, null));

    KMeans kMeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_SINGLE);
    Clustering clustering = kMeans.cluster(instances);

    Assert.assertNotNull(clustering);
    Assert.assertEquals(2, clustering.getNumClusters());
    Assert.assertEquals(3, clustering.getClusterAssignments().length);
  }
@Test
  public void testClusterWithEmptyActionErrorReturnsNull() {
    Pipe pipe = new Noop();
    Metric metric = new Metric() {
      @Override
      public double distance(SparseVector a, SparseVector b) {
        return Math.abs(a.value(0) - b.value(0));
      }
    };

    SparseVector vec = new SparseVector(new int[]{0}, new double[]{0.0}, 1);

    InstanceList instances = new InstanceList(pipe);
    instances.add(new Instance(vec, null, null, null));
    instances.add(new Instance(vec, null, null, null));

    KMeans kMeans = new KMeans(pipe, 3, metric, KMeans.EMPTY_ERROR);
    Clustering clustering = kMeans.cluster(instances);

    Assert.assertNull(clustering);
  }
@Test
  public void testGetClusterMeansAfterClustering() {
    Pipe pipe = new Noop();
    Metric metric = new Metric() {
      @Override
      public double distance(SparseVector a, SparseVector b) {
        double diff = a.value(0) - b.value(0);
        return diff * diff;
      }
    };

    SparseVector vec1 = new SparseVector(new int[]{0}, new double[]{2.0}, 1);
    SparseVector vec2 = new SparseVector(new int[]{0}, new double[]{2.2}, 1);
    SparseVector vec3 = new SparseVector(new int[]{0}, new double[]{5.0}, 1);

    InstanceList instances = new InstanceList(pipe);
    instances.add(new Instance(vec1, null, null, null));
    instances.add(new Instance(vec2, null, null, null));
    instances.add(new Instance(vec3, null, null, null));

    KMeans kMeans = new KMeans(pipe, 2, metric, KMeans.EMPTY_SINGLE);
    Clustering clustering = kMeans.cluster(instances);

    Assert.assertNotNull(clustering);
    ArrayList<SparseVector> means = kMeans.getClusterMeans();
    Assert.assertNotNull(means);
    Assert.assertTrue(means.size() <= 2);
    for (SparseVector mean : means) {
      Assert.assertNotNull(mean);
    }
  }
@Test
  public void testClusterWithEmptyDropReducesClusterCount() {
    Pipe pipe = new Noop();
    Metric metric = new Metric() {
      @Override
      public double distance(SparseVector a, SparseVector b) {
        return Math.abs(a.value(0) - b.value(0));
      }
    };

    SparseVector vec1 = new SparseVector(new int[]{0}, new double[]{1.0}, 1);
    SparseVector vec2 = new SparseVector(new int[]{0}, new double[]{1.1}, 1);

    InstanceList instances = new InstanceList(pipe);
    instances.add(new Instance(vec1, null, null, null));
    instances.add(new Instance(vec2, null, null, null));

    KMeans kMeans = new KMeans(pipe, 3, metric, KMeans.EMPTY_DROP);
    Clustering clustering = kMeans.cluster(instances);

    Assert.assertNotNull(clustering);
    Assert.assertTrue(clustering.getNumClusters() < 3);
  }
@Test
  public void testClusterEmptySingleWithMinimalPoints() {
    Pipe pipe = new Noop();
    Metric metric = new Metric() {
      @Override
      public double distance(SparseVector a, SparseVector b) {
        return Math.abs(a.value(0) - b.value(0));
      }
    };

    SparseVector vec1 = new SparseVector(new int[]{0}, new double[]{1.0}, 1);
    SparseVector vec2 = new SparseVector(new int[]{0}, new double[]{4.0}, 1);
    SparseVector vec3 = new SparseVector(new int[]{0}, new double[]{10.0}, 1);

    InstanceList instances = new InstanceList(pipe);
    instances.add(new Instance(vec1, null, null, null));
    instances.add(new Instance(vec2, null, null, null));
    instances.add(new Instance(vec3, null, null, null));

    KMeans kMeans = new KMeans(pipe, 3, metric, KMeans.EMPTY_SINGLE);
    Clustering clustering = kMeans.cluster(instances);

    Assert.assertNotNull(clustering);
    Assert.assertEquals(3, clustering.getNumClusters());
    Assert.assertEquals(3, clustering.getClusterAssignments().length);
  }
@Test
  public void testClusterReturnsNullWhenNoValidCentroidCanBeChosen() {
    Pipe pipe = new Noop();
    Metric metric = new Metric() {
      @Override
      public double distance(SparseVector a, SparseVector b) {
        return 1.0;
      }
    };

    SparseVector vec = new SparseVector(new int[]{0}, new double[]{5.0}, 1);

    InstanceList instances = new InstanceList(pipe);
    instances.add(new Instance(vec, null, null, null));

    KMeans kMeans = new KMeans(pipe, 3, metric, KMeans.EMPTY_SINGLE);
    Clustering clustering = kMeans.cluster(instances);

    Assert.assertNull(clustering);
  } 
}