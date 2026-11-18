package cc.mallet.cluster;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import cc.mallet.pipe.Noop;
import cc.mallet.pipe.Pipe;
import cc.mallet.types.Alphabet;
import cc.mallet.types.EuclideanDistance;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Metric;
import cc.mallet.types.SparseVector;

public class KMeansPromptTest {

    @Test
    public void constructorRetainsPipeAndStartsWithoutMeans() {
        Alphabet alphabet = new Alphabet();
        Pipe pipe = new Noop(alphabet, null);

        KMeans kMeans = new KMeans(pipe, 3, new EuclideanDistance());

        assertSame(pipe, kMeans.getPipe());
        assertNotNull(kMeans.getClusterMeans());
        assertTrue(kMeans.getClusterMeans().isEmpty());
    }

    @Test
    public void oneClusterComputesDenseArithmeticMean() {
        Alphabet alphabet = new Alphabet();
        Pipe pipe = new Noop(alphabet, null);
        InstanceList instances = new InstanceList(pipe);
        Instance first = new Instance(new FeatureVector(alphabet, new double[] { 2.0, 4.0 }), null, "first", null);
        Instance second = new Instance(new FeatureVector(alphabet, new double[] { 4.0, 8.0 }), null, "second", null);
        Instance third = new Instance(new FeatureVector(alphabet, new double[] { 6.0, 0.0 }), null, "third", null);
        instances.add(first);
        instances.add(second);
        instances.add(third);
        KMeans kMeans = new KMeans(pipe, 1, new EuclideanDistance());

        Clustering result = kMeans.cluster(instances);

        assertNotNull(result);
        assertEquals(1, result.getNumClusters());
        assertEquals(3, result.getNumInstances());
        assertArrayEquals(new int[] { 0, 0, 0 }, result.getLabels());
        assertEquals(4.0, kMeans.getClusterMeans().get(0).value(0), 0.000001);
        assertEquals(4.0, kMeans.getClusterMeans().get(0).value(1), 0.000001);
    }

    @Test
    public void twoWellSeparatedGroupsReceiveStableAssignmentsAndMeans() {
        Alphabet alphabet = new Alphabet();
        Pipe pipe = new Noop(alphabet, null);
        InstanceList instances = new InstanceList(pipe);
        instances.add(new Instance(new FeatureVector(alphabet, new double[] { 0.0, 0.0 }), null, "a", null));
        instances.add(new Instance(new FeatureVector(alphabet, new double[] { 0.0, 2.0 }), null, "b", null));
        instances.add(new Instance(new FeatureVector(alphabet, new double[] { 10.0, 10.0 }), null, "c", null));
        instances.add(new Instance(new FeatureVector(alphabet, new double[] { 12.0, 10.0 }), null, "d", null));
        KMeans kMeans = new KMeans(pipe, 2, new EuclideanDistance());

        Clustering result = kMeans.cluster(instances);

        assertNotNull(result);
        assertArrayEquals(new int[] { 0, 0, 1, 1 }, result.getLabels());
        assertEquals(2, result.size(0));
        assertEquals(2, result.size(1));
        assertEquals(0.0, kMeans.getClusterMeans().get(0).value(0), 0.000001);
        assertEquals(1.0, kMeans.getClusterMeans().get(0).value(1), 0.000001);
        assertEquals(11.0, kMeans.getClusterMeans().get(1).value(0), 0.000001);
        assertEquals(10.0, kMeans.getClusterMeans().get(1).value(1), 0.000001);
    }

    @Test
    public void sparseFeatureVectorsAreClusteredAndAveraged() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("zero");
        alphabet.lookupIndex("one");
        alphabet.lookupIndex("two");
        Pipe pipe = new Noop(alphabet, null);
        InstanceList instances = new InstanceList(pipe);
        instances.add(new Instance(new FeatureVector(alphabet, new int[] { 0, 2 }, new double[] { 2.0, 6.0 }), null, "left", null));
        instances.add(new Instance(new FeatureVector(alphabet, new int[] { 0, 2 }, new double[] { 4.0, 2.0 }), null, "right", null));
        KMeans kMeans = new KMeans(pipe, 1, new EuclideanDistance());

        Clustering result = kMeans.cluster(instances);

        assertNotNull(result);
        SparseVector mean = kMeans.getClusterMeans().get(0);
        assertEquals(3.0, mean.value(0), 0.000001);
        assertEquals(0.0, mean.value(1), 0.000001);
        assertEquals(4.0, mean.value(2), 0.000001);
    }

    @Test
    public void pointEquidistantFromInitialCentroidsUsesLowerClusterIndex() {
        Alphabet alphabet = new Alphabet();
        Pipe pipe = new Noop(alphabet, null);
        InstanceList instances = new InstanceList(pipe);
        instances.add(new Instance(new FeatureVector(alphabet, new double[] { 0.0 }), null, "low", null));
        instances.add(new Instance(new FeatureVector(alphabet, new double[] { 10.0 }), null, "high", null));
        instances.add(new Instance(new FeatureVector(alphabet, new double[] { 5.0 }), null, "middle", null));
        KMeans kMeans = new KMeans(pipe, 2, new EuclideanDistance());

        Clustering result = kMeans.cluster(instances);

        assertNotNull(result);
        assertArrayEquals(new int[] { 0, 1, 0 }, result.getLabels());
        assertEquals(2.5, kMeans.getClusterMeans().get(0).value(0), 0.000001);
        assertEquals(10.0, kMeans.getClusterMeans().get(1).value(0), 0.000001);
    }

    @Test
    public void clusteringResultPreservesInstancesAndSupportsClusterViews() {
        Alphabet alphabet = new Alphabet();
        Pipe pipe = new Noop(alphabet, null);
        InstanceList instances = new InstanceList(pipe);
        Instance near = new Instance(new FeatureVector(alphabet, new double[] { 1.0 }), null, "near", "source-a");
        Instance far = new Instance(new FeatureVector(alphabet, new double[] { 100.0 }), null, "far", "source-b");
        instances.add(near);
        instances.add(far);
        KMeans kMeans = new KMeans(pipe, 2, new EuclideanDistance());

        Clustering result = kMeans.cluster(instances);

        assertSame(instances, result.getInstances());
        assertSame(near, result.getCluster(0).get(0));
        assertSame(far, result.getCluster(1).get(0));
        assertEquals("near", result.getCluster(0).get(0).getName());
        assertEquals("source-b", result.getCluster(1).get(0).getSource());
    }

    @Test
    public void emptyErrorPolicyReturnsNullForDuplicateCentroids() {
        Alphabet alphabet = new Alphabet();
        Pipe pipe = new Noop(alphabet, null);
        InstanceList instances = new InstanceList(pipe);
        instances.add(new Instance(new FeatureVector(alphabet, new double[] { 3.0 }), null, "a", null));
        instances.add(new Instance(new FeatureVector(alphabet, new double[] { 3.0 }), null, "b", null));
        KMeans kMeans = new KMeans(pipe, 2, new EuclideanDistance(), 0);

        Clustering result = kMeans.cluster(instances);

        assertNull(result);
        assertEquals(2, kMeans.getClusterMeans().size());
    }

    @Test
    public void emptyDropPolicyRemovesDuplicateCluster() {
        Alphabet alphabet = new Alphabet();
        Pipe pipe = new Noop(alphabet, null);
        InstanceList instances = new InstanceList(pipe);
        instances.add(new Instance(new FeatureVector(alphabet, new double[] { 7.0 }), null, "a", null));
        instances.add(new Instance(new FeatureVector(alphabet, new double[] { 7.0 }), null, "b", null));
        instances.add(new Instance(new FeatureVector(alphabet, new double[] { 7.0 }), null, "c", null));
        KMeans kMeans = new KMeans(pipe, 2, new EuclideanDistance(), 1);

        Clustering result = kMeans.cluster(instances);

        assertNotNull(result);
        assertEquals(1, result.getNumClusters());
        assertArrayEquals(new int[] { 0, 0, 0 }, result.getLabels());
        assertEquals(1, kMeans.getClusterMeans().size());
        assertEquals(7.0, kMeans.getClusterMeans().get(0).value(0), 0.000001);
    }

    @Test
    public void emptySinglePolicyReturnsNullWhenNoMovableDistantPointExists() {
        Alphabet alphabet = new Alphabet();
        Pipe pipe = new Noop(alphabet, null);
        InstanceList instances = new InstanceList(pipe);
        instances.add(new Instance(new FeatureVector(alphabet, new double[] { 9.0 }), null, "a", null));
        instances.add(new Instance(new FeatureVector(alphabet, new double[] { 9.0 }), null, "b", null));
        instances.add(new Instance(new FeatureVector(alphabet, new double[] { 9.0 }), null, "c", null));
        KMeans kMeans = new KMeans(pipe, 2, new EuclideanDistance(), 2);

        assertNull(kMeans.cluster(instances));
    }

    @Test
    public void unknownEmptyPolicyReturnsNullWhenAnEmptyClusterOccurs() {
        Alphabet alphabet = new Alphabet();
        Pipe pipe = new Noop(alphabet, null);
        InstanceList instances = new InstanceList(pipe);
        instances.add(new Instance(new FeatureVector(alphabet, new double[] { 1.0 }), null, "a", null));
        instances.add(new Instance(new FeatureVector(alphabet, new double[] { 1.0 }), null, "b", null));
        KMeans kMeans = new KMeans(pipe, 2, new EuclideanDistance(), 99);

        assertNull(kMeans.cluster(instances));
    }

    @Test
    public void zeroLocationDenseVectorIsIgnoredForSeedingButIncludedInMean() {
        Alphabet alphabet = new Alphabet();
        Pipe pipe = new Noop(alphabet, null);
        InstanceList instances = new InstanceList(pipe);
        instances.add(new Instance(new FeatureVector(alphabet, new double[0]), null, "empty", null));
        instances.add(new Instance(new FeatureVector(alphabet, new double[] { 4.0 }), null, "nonempty", null));
        KMeans kMeans = new KMeans(pipe, 1, new EuclideanDistance());

        Clustering result = kMeans.cluster(instances);

        assertNotNull(result);
        assertArrayEquals(new int[] { 0, 0 }, result.getLabels());
        assertEquals(2.0, kMeans.getClusterMeans().get(0).value(0), 0.000001);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void allZeroLocationVectorsCannotSeedACluster() {
        Alphabet alphabet = new Alphabet();
        Pipe pipe = new Noop(alphabet, null);
        InstanceList instances = new InstanceList(pipe);
        instances.add(new Instance(new FeatureVector(alphabet, new double[0]), null, "empty", null));
        KMeans kMeans = new KMeans(pipe, 1, new EuclideanDistance());

        kMeans.cluster(instances);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void requestingMoreClustersThanSeedableInstancesFailsDuringInitialization() {
        Alphabet alphabet = new Alphabet();
        Pipe pipe = new Noop(alphabet, null);
        InstanceList instances = new InstanceList(pipe);
        instances.add(new Instance(new FeatureVector(alphabet, new double[] { 2.0 }), null, "only", null));
        KMeans kMeans = new KMeans(pipe, 2, new EuclideanDistance());

        kMeans.cluster(instances);
    }

    @Test(expected = ClassCastException.class)
    public void nonSparseInstanceDataIsRejected() {
        Pipe pipe = new Noop();
        InstanceList instances = new InstanceList(pipe);
        instances.add(new Instance("not a vector", null, "bad", null));
        KMeans kMeans = new KMeans(pipe, 1, new EuclideanDistance());

        kMeans.cluster(instances);
    }

    @Test(expected = NullPointerException.class)
    public void nullInstanceListIsRejected() {
        Pipe pipe = new Noop();
        KMeans kMeans = new KMeans(pipe, 1, new EuclideanDistance());

        kMeans.cluster(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeClusterCountIsRejectedByConstruction() {
        Pipe pipe = new Noop();

        new KMeans(pipe, -1, new EuclideanDistance());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void zeroClusterCountCannotAssignANonemptyInput() {
        Alphabet alphabet = new Alphabet();
        Pipe pipe = new Noop(alphabet, null);
        InstanceList instances = new InstanceList(pipe);
        instances.add(new Instance(new FeatureVector(alphabet, new double[] { 1.0 }), null, "point", null));
        KMeans kMeans = new KMeans(pipe, 0, new EuclideanDistance());

        kMeans.cluster(instances);
    }

    @Test
    public void suppliedMetricParticipatesInSeedingAssignmentAndConvergence() {
        Alphabet alphabet = new Alphabet();
        Pipe pipe = new Noop(alphabet, null);
        InstanceList instances = new InstanceList(pipe);
        instances.add(new Instance(new FeatureVector(alphabet, new double[] { 1.0 }), null, "a", null));
        instances.add(new Instance(new FeatureVector(alphabet, new double[] { 6.0 }), null, "b", null));
        instances.add(new Instance(new FeatureVector(alphabet, new double[] { 9.0 }), null, "c", null));
        AtomicInteger calls = new AtomicInteger();
        Metric metric = new Metric() {
            private static final long serialVersionUID = 1L;

            @Override
            public double distance(SparseVector left, SparseVector right) {
                calls.incrementAndGet();
                return Math.abs(left.value(0) - right.value(0));
            }
        };
        KMeans kMeans = new KMeans(pipe, 2, metric);

        Clustering result = kMeans.cluster(instances);

        assertNotNull(result);
        assertArrayEquals(new int[] { 0, 1, 1 }, result.getLabels());
        assertTrue(calls.get() >= 10);
    }

    @Test
    public void metricReturningNaNProducesAnEmptyClusterFailure() {
        Alphabet alphabet = new Alphabet();
        Pipe pipe = new Noop(alphabet, null);
        InstanceList instances = new InstanceList(pipe);
        instances.add(new Instance(new FeatureVector(alphabet, new double[] { 1.0 }), null, "a", null));
        instances.add(new Instance(new FeatureVector(alphabet, new double[] { 2.0 }), null, "b", null));
        Metric metric = new Metric() {
            private static final long serialVersionUID = 1L;

            @Override
            public double distance(SparseVector left, SparseVector right) {
                return Double.NaN;
            }
        };
        KMeans kMeans = new KMeans(pipe, 2, metric);

        assertNull(kMeans.cluster(instances));
    }

    @Test
    public void repeatedClusteringOnSameObjectRetainsPreviouslyAllocatedMeanEntry() {
        Alphabet alphabet = new Alphabet();
        Pipe pipe = new Noop(alphabet, null);
        InstanceList firstBatch = new InstanceList(pipe);
        firstBatch.add(new Instance(new FeatureVector(alphabet, new double[] { 2.0 }), null, "first-a", null));
        firstBatch.add(new Instance(new FeatureVector(alphabet, new double[] { 4.0 }), null, "first-b", null));
        InstanceList secondBatch = new InstanceList(pipe);
        secondBatch.add(new Instance(new FeatureVector(alphabet, new double[] { 10.0 }), null, "second-a", null));
        secondBatch.add(new Instance(new FeatureVector(alphabet, new double[] { 14.0 }), null, "second-b", null));
        KMeans kMeans = new KMeans(pipe, 1, new EuclideanDistance());

        Clustering firstResult = kMeans.cluster(firstBatch);
        Clustering secondResult = kMeans.cluster(secondBatch);

        assertNotNull(firstResult);
        assertNotNull(secondResult);
        assertArrayEquals(new int[] { 0, 0 }, secondResult.getLabels());
        assertEquals(2, kMeans.getClusterMeans().size());
        assertEquals(12.0, kMeans.getClusterMeans().get(0).value(0), 0.000001);
        assertEquals(14.0, kMeans.getClusterMeans().get(1).value(0), 0.000001);
    }

    @Test
    public void serializedClustererCanClusterAfterRoundTrip() throws Exception {
        Alphabet alphabet = new Alphabet();
        Pipe pipe = new Noop(alphabet, null);
        Metric serializableMetric = (Metric & java.io.Serializable)
                (left, right) -> Math.abs(left.value(0) - right.value(0));
        KMeans original = new KMeans(pipe, 1, serializableMetric);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream output = new ObjectOutputStream(bytes);
        output.writeObject(original);
        output.close();
        ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()));
        KMeans restored = (KMeans) input.readObject();
        input.close();
        InstanceList instances = new InstanceList(restored.getPipe());
        instances.add(new Instance(new FeatureVector(restored.getPipe().getDataAlphabet(), new double[] { 3.0 }), null, "a", null));
        instances.add(new Instance(new FeatureVector(restored.getPipe().getDataAlphabet(), new double[] { 5.0 }), null, "b", null));

        Clustering result = restored.cluster(instances);

        assertNotNull(result);
        assertArrayEquals(new int[] { 0, 0 }, result.getLabels());
        assertEquals(4.0, restored.getClusterMeans().get(0).value(0), 0.000001);
    }
}
