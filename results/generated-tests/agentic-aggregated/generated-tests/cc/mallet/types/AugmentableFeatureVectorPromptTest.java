package cc.mallet.types;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;

import cc.mallet.util.PropertyList;

public class AugmentableFeatureVectorPromptTest {

    @Test
    public void sparseWeightedAddSortsCoalescesDuplicatesAndGrows() {
        Alphabet alphabet = new Alphabet();
        int zero = alphabet.lookupIndex("zero");
        int one = alphabet.lookupIndex("one");
        int two = alphabet.lookupIndex("two");
        int three = alphabet.lookupIndex("three");
        AugmentableFeatureVector vector = new AugmentableFeatureVector(alphabet, 1, false);

        vector.add(three, 4.0);
        vector.add(one, 2.0);
        vector.add(three, -1.5);
        vector.add(zero, 7.0);
        vector.add(two, 0.5);

        assertEquals(4, vector.numLocations());
        assertEquals(zero, vector.indexAtLocation(0));
        assertEquals(one, vector.indexAtLocation(1));
        assertEquals(two, vector.indexAtLocation(2));
        assertEquals(three, vector.indexAtLocation(3));
        assertEquals(2.5, vector.value(three), 0.0);
        assertEquals(-1, vector.location(9));
    }

    @Test
    public void binaryVectorAcceptsOnlyUnitValuesAndWeightedVectorRejectsBinaryAdd() {
        Alphabet alphabet = new Alphabet();
        int feature = alphabet.lookupIndex("feature");
        AugmentableFeatureVector binary = new AugmentableFeatureVector(alphabet, true);
        binary.add(feature);
        assertEquals(1.0, binary.value(feature), 0.0);

        try {
            binary.add(feature, 2.0);
            fail("A non-unit value must not be added to a binary vector");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("non-1.0"));
        }

        AugmentableFeatureVector weighted = new AugmentableFeatureVector(alphabet, false);
        try {
            weighted.add(feature);
            fail("A binary feature must not be added to a weighted vector");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("binary feature"));
        }
    }

    @Test
    public void denseVectorCanExpandAndAccumulateAtExistingIndex() {
        Alphabet alphabet = new Alphabet();
        AugmentableFeatureVector vector = new AugmentableFeatureVector(alphabet, new double[] { 1.0, 2.0 });

        vector.add(1, 3.5);
        vector.add(5, 7.0);

        assertEquals(6, vector.numLocations());
        assertEquals(1.0, vector.value(0), 0.0);
        assertEquals(5.5, vector.value(1), 0.0);
        assertEquals(0.0, vector.value(4), 0.0);
        assertEquals(7.0, vector.valueAtLocation(5), 0.0);
        assertEquals(5, vector.indexAtLocation(5));
    }

    @Test
    public void featureSequenceConstructorCountsOrCollapsesRepeatedFeatures() {
        Alphabet alphabet = new Alphabet();
        int alpha = alphabet.lookupIndex("alpha");
        int beta = alphabet.lookupIndex("beta");
        FeatureSequence sequence = new FeatureSequence(alphabet);
        sequence.add(beta);
        sequence.add(alpha);
        sequence.add(beta);

        AugmentableFeatureVector weighted = new AugmentableFeatureVector(sequence, false);
        AugmentableFeatureVector binary = new AugmentableFeatureVector(sequence, true);

        assertEquals(2, weighted.numLocations());
        assertEquals(1.0, weighted.value(alpha), 0.0);
        assertEquals(2.0, weighted.value(beta), 0.0);
        assertEquals(2, binary.numLocations());
        assertEquals(1.0, binary.value(alpha), 0.0);
        assertEquals(1.0, binary.value(beta), 0.0);
    }

    @Test
    public void propertyListConstructorHonorsAlphabetGrowthAndNumericEntries() {
        Alphabet alphabet = new Alphabet();
        int known = alphabet.lookupIndex("known");
        PropertyList properties = PropertyList.add("unknown", 9.0,
                PropertyList.add("known", -2.5, null));

        AugmentableFeatureVector fixed = new AugmentableFeatureVector(alphabet, properties, false, false);
        assertEquals(1, fixed.numLocations());
        assertEquals(-2.5, fixed.value(known), 0.0);
        assertEquals(-1, alphabet.lookupIndex("unknown", false));

        AugmentableFeatureVector growing = new AugmentableFeatureVector(alphabet, properties, false);
        int unknown = alphabet.lookupIndex("unknown", false);
        assertEquals(2, growing.numLocations());
        assertEquals(9.0, growing.value(unknown), 0.0);
        assertEquals(-2.5, growing.value(known), 0.0);
    }

    @Test
    public void addFeatureVectorCopiesValuesOnlyForPreviouslyAbsentIndices() {
        Alphabet alphabet = new Alphabet();
        int a = alphabet.lookupIndex("a");
        int b = alphabet.lookupIndex("b");
        int c = alphabet.lookupIndex("c");
        AugmentableFeatureVector target = new AugmentableFeatureVector(alphabet);
        target.add(b, 10.0);
        FeatureVector source = new FeatureVector(alphabet, new int[] { c, a, b }, new double[] { 3.0, 1.0, 2.0 });

        target.add(source);

        assertEquals(3, target.numLocations());
        assertEquals(1.0, target.value(a), 0.0);
        assertEquals(10.0, target.value(b), 0.0);
        assertEquals(3.0, target.value(c), 0.0);
    }

    @Test
    public void prefixedAddsWorkAcrossAlphabetsForBinaryAndWeightedSources() {
        Alphabet sourceAlphabet = new Alphabet();
        int red = sourceAlphabet.lookupIndex("red");
        int blue = sourceAlphabet.lookupIndex("blue");
        FeatureVector source = new FeatureVector(sourceAlphabet, new int[] { red, blue }, new double[] { 2.5, -4.0 });
        Alphabet targetAlphabet = new Alphabet();
        AugmentableFeatureVector target = new AugmentableFeatureVector(targetAlphabet);

        target.add(source, "present:");
        target.add(source, "weighted:", false);

        assertEquals(1.0, target.value(targetAlphabet.lookupIndex("present:red", false)), 0.0);
        assertEquals(1.0, target.value(targetAlphabet.lookupIndex("present:blue", false)), 0.0);
        assertEquals(2.5, target.value(targetAlphabet.lookupIndex("weighted:red", false)), 0.0);
        assertEquals(-4.0, target.value(targetAlphabet.lookupIndex("weighted:blue", false)), 0.0);
    }

    @Test
    public void accessMutationAndScaledAccumulationUseLogicalSparseEntries() {
        Alphabet alphabet = new Alphabet();
        AugmentableFeatureVector vector = new AugmentableFeatureVector(alphabet,
                new int[] { 4, 1 }, new double[] { 2.0, -3.0 }, 2);

        vector.setValue(4, 6.0);
        vector.setValueAtLocation(0, -7.0);
        double[] accumulator = new double[] { 1.0, 1.0, 1.0, 1.0, 1.0, 1.0 };
        vector.addTo(accumulator, 0.5);

        assertEquals(1, vector.indexAtLocation(0));
        assertEquals(-7.0, vector.valueAtLocation(0), 0.0);
        assertEquals(6.0, vector.value(4), 0.0);
        assertArrayEquals(new double[] { 1.0, -2.5, 1.0, 1.0, 4.0, 1.0 }, accumulator, 0.0);
    }

    @Test
    public void addToWithoutScaleSupportsDenseAndBinaryVectors() {
        Alphabet alphabet = new Alphabet();
        AugmentableFeatureVector dense = new AugmentableFeatureVector(alphabet, null,
                new double[] { 2.0, -1.0, 3.0 }, 3, 3, true, false, false);
        AugmentableFeatureVector binary = new AugmentableFeatureVector(alphabet, new int[] { 3, 0 }, null, 2);
        double[] denseAccumulator = new double[] { 10.0, 10.0, 10.0 };
        double[] binaryAccumulator = new double[] { 0.0, 0.0, 0.0, 0.0 };

        dense.addTo(denseAccumulator);
        binary.addTo(binaryAccumulator);

        assertArrayEquals(new double[] { 12.0, 9.0, 13.0 }, denseAccumulator, 0.0);
        assertArrayEquals(new double[] { 1.0, 0.0, 0.0, 1.0 }, binaryAccumulator, 0.0);
    }

    @Test
    public void dotProductsCoverDenseSparseWeightedAndBinaryOperands() {
        Alphabet alphabet = new Alphabet();
        AugmentableFeatureVector weighted = new AugmentableFeatureVector(alphabet,
                new int[] { 3, 1 }, new double[] { 2.0, -4.0 }, 2);
        AugmentableFeatureVector binary = new AugmentableFeatureVector(alphabet,
                new int[] { 0, 3, 1 }, null, 3);
        AugmentableFeatureVector dense = new AugmentableFeatureVector(alphabet, null,
                new double[] { 5.0, 6.0, 7.0, 8.0 }, 4, 4, true, false, false);
        DenseVector ordinaryDense = new DenseVector(new double[] { 1.0, 2.0, 3.0, 4.0 });
        SparseVector ordinarySparse = new SparseVector(new int[] { 1, 3 }, new double[] { 10.0, -2.0 });

        assertEquals(0.0, weighted.dotProduct(ordinaryDense), 0.0);
        assertEquals(-44.0, weighted.dotProduct(ordinarySparse), 0.0);
        assertEquals(-2.0, weighted.dotProduct(binary), 0.0);
        assertEquals(19.0, dense.dotProduct(binary), 0.0);
        assertEquals(3.0, binary.dotProduct(binary), 0.0);
    }

    @Test
    public void plusEqualsAugmentableHandlesSparseAndDenseCombinations() {
        Alphabet alphabet = new Alphabet();
        AugmentableFeatureVector sparse = new AugmentableFeatureVector(alphabet,
                new int[] { 1, 3 }, new double[] { 10.0, 20.0 }, 2);
        AugmentableFeatureVector binary = new AugmentableFeatureVector(alphabet,
                new int[] { 0, 3 }, null, 2);
        sparse.plusEquals(binary, -2.0);
        assertEquals(10.0, sparse.value(1), 0.0);
        assertEquals(18.0, sparse.value(3), 0.0);

        AugmentableFeatureVector dense = new AugmentableFeatureVector(alphabet, null,
                new double[] { 1.0, 2.0, 3.0, 4.0 }, 4, 4, true, false, false);
        AugmentableFeatureVector additions = new AugmentableFeatureVector(alphabet,
                new int[] { 0, 2 }, new double[] { 5.0, -1.0 }, 2);
        dense.plusEquals(additions, 3.0);
        assertEquals(16.0, dense.value(0), 0.0);
        assertEquals(0.0, dense.value(2), 0.0);
    }

    @Test
    public void plusEqualsDenseAugmentableUsesItsDocumentedUnscaledDenseBranch() {
        Alphabet alphabet = new Alphabet();
        AugmentableFeatureVector left = new AugmentableFeatureVector(alphabet, null,
                new double[] { 1.0, 2.0, 3.0 }, 3, 3, true, false, false);
        AugmentableFeatureVector right = new AugmentableFeatureVector(alphabet, null,
                new double[] { 4.0, 5.0 }, 2, 2, true, false, false);

        left.plusEquals(right, 100.0);

        assertEquals(5.0, left.value(0), 0.0);
        assertEquals(7.0, left.value(1), 0.0);
        assertEquals(3.0, left.value(2), 0.0);
    }

    @Test
    public void plusEqualsOrdinarySparseUpdatesOnlyExistingLocations() {
        Alphabet alphabet = new Alphabet();
        AugmentableFeatureVector target = new AugmentableFeatureVector(alphabet,
                new int[] { 1, 4 }, new double[] { 2.0, 8.0 }, 2);
        SparseVector source = new SparseVector(new int[] { 0, 1, 4, 6 }, new double[] { 9.0, 3.0, -2.0, 7.0 });

        target.plusEquals(source, 2.0);

        assertEquals(8.0, target.value(1), 0.0);
        assertEquals(4.0, target.value(4), 0.0);
        assertEquals(0.0, target.value(6), 0.0);
        target.plusEquals(new SparseVector(new int[] { 1 }, new double[] { 1.5 }));
        assertEquals(9.5, target.value(1), 0.0);
    }

    @Test
    public void normsAndSetAllReflectWeightedAndBinaryRepresentations() {
        Alphabet alphabet = new Alphabet();
        AugmentableFeatureVector weighted = new AugmentableFeatureVector(alphabet,
                new int[] { 0, 2, 5 }, new double[] { -3.0, 4.0, -12.0 }, 3);
        assertEquals(-11.0, weighted.oneNorm(), 0.0);
        assertEquals(13.0, weighted.twoNorm(), 0.0);
        assertEquals(12.0, weighted.infinityNorm(), 0.0);

        weighted.setAll(2.0);
        assertEquals(6.0, weighted.oneNorm(), 0.0);
        assertEquals(Math.sqrt(12.0), weighted.twoNorm(), 0.0);

        AugmentableFeatureVector binary = new AugmentableFeatureVector(alphabet,
                new int[] { 5, 2, 0 }, null, 3);
        assertEquals(3.0, binary.oneNorm(), 0.0);
        assertEquals(Math.sqrt(3.0), binary.twoNorm(), 0.0);
        assertEquals(1.0, binary.infinityNorm(), 0.0);
    }

    @Test
    public void conversionsAndClonesAreIndependentAndPreserveLogicalValues() {
        Alphabet alphabet = new Alphabet();
        AugmentableFeatureVector original = new AugmentableFeatureVector(alphabet,
                new int[] { 4, 1 }, new double[] { 8.0, 3.0 }, 2);
        AugmentableFeatureVector clone = (AugmentableFeatureVector) original.cloneMatrix();
        AugmentableFeatureVector zeroed = (AugmentableFeatureVector) original.cloneMatrixZeroed();
        SparseVector sparse = original.toSparseVector();
        FeatureVector feature = original.toFeatureVector();

        original.setValue(1, 99.0);

        assertEquals(3.0, clone.value(1), 0.0);
        assertEquals(0.0, zeroed.value(1), 0.0);
        assertEquals(0.0, zeroed.value(4), 0.0);
        assertEquals(3.0, sparse.value(1), 0.0);
        assertEquals(8.0, feature.value(4), 0.0);
        assertEquals(4, original.singleSize());
    }

    @Test
    public void featureVectorCopyConstructorDoesNotAliasSourceStorage() {
        Alphabet alphabet = new Alphabet();
        int first = alphabet.lookupIndex("first");
        int second = alphabet.lookupIndex("second");
        FeatureVector source = new FeatureVector(alphabet, new int[] { first, second }, new double[] { 2.0, 7.0 });
        AugmentableFeatureVector copy = new AugmentableFeatureVector(source);

        copy.setValue(first, -5.0);

        assertEquals(2.0, source.value(first), 0.0);
        assertEquals(-5.0, copy.value(first), 0.0);
        assertSame(alphabet, copy.getAlphabet());
    }

    @Test
    public void serializationRoundTripRetainsDataAndRemainsAugmentable() throws Exception {
        Alphabet alphabet = new Alphabet();
        int first = alphabet.lookupIndex("first");
        int second = alphabet.lookupIndex("second");
        int third = alphabet.lookupIndex("third");
        AugmentableFeatureVector original = new AugmentableFeatureVector(alphabet, 1, false);
        original.add(second, 2.0);
        original.add(first, 1.0);

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream output = new ObjectOutputStream(bytes);
        output.writeObject(original);
        output.close();
        ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()));
        AugmentableFeatureVector restored = (AugmentableFeatureVector) input.readObject();
        input.close();
        restored.add(third, 3.0);

        assertEquals(3, restored.numLocations());
        assertEquals(1.0, restored.value(first), 0.0);
        assertEquals(2.0, restored.value(second), 0.0);
        assertEquals(3.0, restored.value(third), 0.0);
        assertEquals("third", restored.getAlphabet().lookupObject(third));
    }

    @Test
    public void emptyVectorsReportEmptyShapeAndRepresentationSpecificNorms() {
        Alphabet alphabet = new Alphabet();
        AugmentableFeatureVector weighted = new AugmentableFeatureVector(alphabet);
        AugmentableFeatureVector binary = new AugmentableFeatureVector(alphabet, true);

        assertEquals(0, weighted.numLocations());
        assertEquals(0, weighted.singleSize());
        assertEquals(0.0, weighted.oneNorm(), 0.0);
        assertEquals(0.0, weighted.twoNorm(), 0.0);
        assertEquals(Double.NEGATIVE_INFINITY, weighted.infinityNorm(), 0.0);
        assertEquals(0, binary.numLocations());
        assertEquals(0.0, binary.oneNorm(), 0.0);
        assertEquals(1.0, binary.infinityNorm(), 0.0);
    }

    @Test
    public void explicitSizeConstructorIgnoresUnusedCapacitySlots() {
        Alphabet alphabet = new Alphabet();
        int[] indices = new int[] { 1, 3, 99, 98 };
        double[] values = new double[] { 2.0, 6.0, 1000.0, 1000.0 };
        AugmentableFeatureVector vector = new AugmentableFeatureVector(
                alphabet, indices, values, 6, 2, true, false, false);

        assertEquals(2, vector.numLocations());
        assertEquals(1, vector.indexAtLocation(0));
        assertEquals(3, vector.indexAtLocation(1));
        assertEquals(2.0, vector.value(1), 0.0);
        assertEquals(6.0, vector.value(3), 0.0);
    }
}
