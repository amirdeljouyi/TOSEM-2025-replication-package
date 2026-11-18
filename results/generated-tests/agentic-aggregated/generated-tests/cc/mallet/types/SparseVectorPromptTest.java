package cc.mallet.types;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;

import org.junit.Test;

import cc.mallet.util.PropertyList;

public class SparseVectorPromptTest {

    @Test
    public void denseConstructionAndAccessorsExposeExpectedShape() {
        double[] source = {1.5, -2.0, 0.0, 4.25};
        SparseVector vector = new SparseVector(source);
        int[] dimensions = {-1};

        assertFalse(vector.isBinary());
        assertNull(vector.getIndices());
        assertArrayEquals(source, vector.getValues(), 0.0);
        assertEquals(1, vector.getNumDimensions());
        assertEquals(1, vector.getDimensions(dimensions));
        assertArrayEquals(new int[] {4}, dimensions);
        assertEquals(4, vector.numLocations());
        assertEquals(2, vector.location(2));
        assertEquals(4.25, vector.valueAtLocation(3), 0.0);
        assertEquals(3, vector.indexAtLocation(3));
        assertEquals(-2.0, vector.value(1), 0.0);
        assertEquals(0.0, vector.value(99), 0.0);
    }

    @Test
    public void denseCopyFlagControlsAliasing() {
        double[] copiedSource = {2.0, 3.0};
        double[] sharedSource = {5.0, 7.0};
        SparseVector copied = new SparseVector(copiedSource, true);
        SparseVector shared = new SparseVector(sharedSource, false);

        copiedSource[0] = 20.0;
        sharedSource[1] = 70.0;

        assertEquals(2.0, copied.value(0), 0.0);
        assertEquals(70.0, shared.value(1), 0.0);
    }

    @Test
    public void fillConstructorCreatesMutableDenseVector() {
        SparseVector vector = new SparseVector(3, -1.25);

        assertArrayEquals(new double[] {-1.25, -1.25, -1.25}, vector.getValues(), 0.0);
        vector.incrementValue(1, 2.0);
        vector.setValue(2, 9.0);

        assertArrayEquals(new double[] {-1.25, 0.75, 9.0}, vector.getValues(), 0.0);
    }

    @Test
    public void sparseNumericConstructorSortsAndCombinesDuplicates() {
        SparseVector vector = new SparseVector(
                new int[] {5, 1, 5, 3, 1},
                new double[] {2.0, 4.0, -0.5, 8.0, 1.5});

        assertArrayEquals(new int[] {1, 3, 5}, vector.getIndices());
        assertArrayEquals(new double[] {5.5, 8.0, 1.5}, vector.getValues(), 0.0);
        assertEquals(3, vector.numLocations());
        assertEquals(1, vector.indexAtLocation(0));
        assertEquals(8.0, vector.valueAtLocation(1), 0.0);
        assertEquals(1, vector.location(3));
        assertTrue(vector.location(4) < 0);
        assertEquals(0.0, vector.value(4), 0.0);
        assertEquals(1.5, vector.value(5), 0.0);
    }

    @Test
    public void sparseBinaryConstructorSortsAndRemovesDuplicates() {
        SparseVector vector = new SparseVector(new int[] {7, 2, 7, 4, 2});

        assertTrue(vector.isBinary());
        assertNull(vector.getValues());
        assertArrayEquals(new int[] {2, 4, 7}, vector.getIndices());
        assertEquals(1.0, vector.value(2), 0.0);
        assertEquals(1.0, vector.valueAtLocation(2), 0.0);
        assertEquals(0.0, vector.value(3), 0.0);
        assertEquals(3.0, vector.oneNorm(), 0.0);
        assertEquals(Math.sqrt(3.0), vector.twoNorm(), 0.0);
        assertEquals(1.0, vector.infinityNorm(), 0.0);
    }

    @Test
    public void nonBinaryIndicesOnlyConstructorCreatesUnitValues() {
        SparseVector vector = new SparseVector(
                new int[] {6, 2}, true, true, true, false);

        assertFalse(vector.isBinary());
        assertArrayEquals(new int[] {2, 6}, vector.getIndices());
        assertArrayEquals(new double[] {1.0, 1.0}, vector.getValues(), 0.0);
    }

    @Test
    public void constructorRejectsMismatchedSparseArrays() {
        try {
            new SparseVector(new int[] {1, 2}, new double[] {3.0});
            fail("Expected mismatched arrays to be rejected");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("mismatching"));
        }
    }

    @Test
    public void emptyVectorHasNoLocationsAndUnknownSizeZero() {
        SparseVector vector = new SparseVector();

        assertFalse(vector.isBinary());
        assertEquals(0, vector.numLocations());
        assertEquals(0, vector.singleSize());
        assertEquals(0.0, vector.value(12), 0.0);
        assertEquals("", vector.toString());
    }

    @Test
    public void indexConversionAndSingleValueFollowOneDimensionalContract() {
        SparseVector vector = new SparseVector(new int[] {2, 9}, new double[] {3.0, 11.0});
        int[] converted = {-1};

        assertEquals(9, vector.singleIndex(new int[] {9}));
        vector.singleToIndices(6, converted);

        assertArrayEquals(new int[] {6}, converted);
        assertEquals(3.0, vector.singleValue(2), 0.0);
        assertEquals(0.0, vector.singleValue(8), 0.0);
        assertEquals(9, vector.singleSize());
        assertEquals(11.0, vector.value(new int[] {9}), 0.0);
    }

    @Test
    public void addToSupportsDenseScaledAndUnscaledAccumulation() {
        SparseVector vector = new SparseVector(new double[] {1.0, -2.0, 3.5});
        double[] accumulator = {10.0, 20.0, 30.0};

        vector.addTo(accumulator, 2.0);
        vector.addTo(accumulator);

        assertArrayEquals(new double[] {13.0, 14.0, 40.5}, accumulator, 0.0);
    }

    @Test
    public void addToSupportsSparseNumericAndBinaryVectors() {
        SparseVector numeric = new SparseVector(new int[] {1, 4}, new double[] {2.5, -3.0});
        SparseVector binary = new SparseVector(new int[] {0, 4});
        double[] accumulator = {1.0, 1.0, 1.0, 1.0, 1.0};

        numeric.addTo(accumulator, 2.0);
        binary.addTo(accumulator);

        assertArrayEquals(new double[] {2.0, 6.0, 1.0, 1.0, -4.0}, accumulator, 0.0);
    }

    @Test
    public void stringRepresentationsUseIndicesAndRequestedLayout() {
        SparseVector vector = new SparseVector(new int[] {4, 1}, new double[] {2.0, -3.5});

        assertEquals("1=-3.5\n4=2.0\n", vector.toString());
        assertEquals("1=-3.5 4=2.0 ", vector.toString(true));
    }

    @Test
    public void clonesAreIndependentAndZeroedClonePreservesSparseLocations() {
        SparseVector original = new SparseVector(new int[] {1, 5}, new double[] {2.0, 7.0});
        SparseVector clone = (SparseVector) original.cloneMatrix();
        SparseVector zeroed = (SparseVector) original.cloneMatrixZeroed();

        original.setValue(1, 20.0);
        clone.setValueAtLocation(1, 70.0);

        assertArrayEquals(new double[] {2.0, 70.0}, clone.getValues(), 0.0);
        assertArrayEquals(new int[] {1, 5}, zeroed.getIndices());
        assertArrayEquals(new double[] {0.0, 0.0}, zeroed.getValues(), 0.0);
        assertEquals(20.0, original.value(1), 0.0);
        assertEquals(7.0, original.value(5), 0.0);
    }

    @Test
    public void plusEqualsSparseUpdatesOnlyLocationsPresentInReceiver() {
        SparseVector receiver = new SparseVector(
                new int[] {1, 3, 8}, new double[] {10.0, 20.0, 30.0});
        SparseVector operand = new SparseVector(
                new int[] {0, 1, 5, 8, 10}, new double[] {9.0, 2.0, 4.0, -3.0, 7.0});

        receiver.plusEqualsSparse(operand, 2.0);
        receiver.plusEqualsSparse(new SparseVector(new int[] {3}, new double[] {1.5}));

        assertArrayEquals(new double[] {14.0, 21.5, 24.0}, receiver.getValues(), 0.0);
    }

    @Test
    public void densePlusEqualsSparseHandlesBinaryAndIgnoresOutOfRangeTail() {
        SparseVector receiver = new SparseVector(new double[] {1.0, 2.0, 3.0, 4.0});
        SparseVector operand = new SparseVector(new int[] {1, 3, 7});

        receiver.plusEqualsSparse(operand, -2.0);

        assertArrayEquals(new double[] {1.0, 0.0, 3.0, 2.0}, receiver.getValues(), 0.0);
    }

    @Test
    public void timesEqualsSparseLeavesUnmatchedSparseLocationsUnchanged() {
        SparseVector receiver = new SparseVector(
                new int[] {1, 3, 6}, new double[] {2.0, 4.0, 8.0});
        SparseVector operand = new SparseVector(
                new int[] {0, 3, 6}, new double[] {10.0, -2.0, 0.5});

        receiver.timesEqualsSparse(operand, 3.0);

        assertArrayEquals(new double[] {2.0, -24.0, 12.0}, receiver.getValues(), 0.0);
    }

    @Test
    public void timesEqualsSparseZeroClearsMissingLocationsBetweenMatches() {
        SparseVector receiver = new SparseVector(
                new int[] {1, 3, 5}, new double[] {2.0, 4.0, 8.0});
        SparseVector operand = new SparseVector(
                new int[] {1, 4, 5}, new double[] {10.0, 9.0, 0.5});

        receiver.timesEqualsSparseZero(operand, 2.0);

        assertArrayEquals(new double[] {40.0, 0.0, 8.0}, receiver.getValues(), 0.0);
    }

    @Test
    public void scalarMutationSetAllAndArrayCopiesComposeCorrectly() {
        SparseVector vector = new SparseVector(new double[] {1.0, 2.0, 3.0});
        double[] destination = {-1.0, -1.0, -1.0, -1.0, -1.0};

        vector.timesEquals(-2.0);
        vector.setAll(4.0);
        assertEquals(5, vector.arrayCopyFrom(new double[] {8.0, 9.0, 10.0, 11.0, 12.0}, 2));
        assertEquals(4, vector.arrayCopyInto(destination, 1));

        assertArrayEquals(new double[] {10.0, 11.0, 12.0}, vector.getValues(), 0.0);
        assertArrayEquals(new double[] {-1.0, 10.0, 11.0, 12.0, -1.0}, destination, 0.0);
    }

    @Test
    public void settingAbsentSparseIndexFailsWithoutChangingVector() {
        SparseVector vector = new SparseVector(new int[] {2, 4}, new double[] {1.0, 3.0});

        try {
            vector.setValue(3, 99.0);
            fail("Expected insertion into sparse vector to fail");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("insert"));
        }

        try {
            vector.incrementValue(7, 2.0);
            fail("Expected increment of absent index to fail");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("isn't present"));
        }

        assertArrayEquals(new double[] {1.0, 3.0}, vector.getValues(), 0.0);
    }

    @Test
    public void unsupportedBinaryConversionsReportExplicitly() {
        SparseVector vector = new SparseVector(new int[] {1, 2});

        try {
            vector.makeNonBinary();
            fail("Expected unsupported conversion");
        } catch (UnsupportedOperationException expected) {
            assertEquals("Not yet implemented", expected.getMessage());
        }

        try {
            vector.makeBinary();
            fail("Expected unsupported conversion");
        } catch (UnsupportedOperationException expected) {
            assertEquals("Not yet implemented", expected.getMessage());
        }
    }

    @Test
    public void mapRejectsBinaryVectorAndWrongMethodSignature() throws Throwable {
        SparseVector binary = new SparseVector(new int[] {1, 3});
        SparseVector numeric = new SparseVector(new double[] {1.0, 2.0});
        Method wrong = Math.class.getMethod("abs", double.class);

        try {
            binary.map(wrong);
            fail("Expected binary map to fail first");
        } catch (UnsupportedOperationException expected) {
            assertTrue(expected.getMessage().contains("Binary"));
        }

        try {
            numeric.map(wrong);
            fail("Expected primitive signature to be rejected");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("Method signature"));
        }
    }

    @Test
    public void dotProductsCoverArrayDenseSparseBinaryAndDispatch() {
        SparseVector numeric = new SparseVector(
                new int[] {0, 2, 4}, new double[] {2.0, -1.0, 3.0});
        SparseVector binary = new SparseVector(new int[] {1, 2, 4});
        DenseVector dense = new DenseVector(new double[] {5.0, 7.0, 11.0, 13.0, 17.0});

        assertEquals(50.0, numeric.dotProduct(new double[] {5.0, 7.0, 11.0, 13.0, 17.0}), 0.0);
        assertEquals(50.0, numeric.dotProduct(dense), 0.0);
        assertEquals(50.0, numeric.dotProduct((ConstantMatrix) dense), 0.0);
        assertEquals(35.0, binary.dotProduct(dense), 0.0);
        assertEquals(2.0, numeric.dotProduct(binary), 0.0);
        assertEquals(2.0, binary.dotProduct(numeric), 0.0);
        assertEquals(2.0, numeric.dotProduct((ConstantMatrix) binary), 0.0);
    }

    @Test
    public void extendedDotProductsTreatInfinityTimesZeroAsZero() {
        double infinity = 1.0 / 0.0;
        SparseVector sparse = new SparseVector(new int[] {0, 2}, new double[] {infinity, 4.0});
        SparseVector otherSparse = new SparseVector(new int[] {0, 2}, new double[] {0.0, 3.0});
        DenseVector dense = new DenseVector(new double[] {0.0, 9.0, 3.0});

        assertEquals(12.0, sparse.dotProduct(otherSparse), 0.0);
        assertEquals(12.0, sparse.extendedDotProduct(otherSparse), 0.0);
        assertEquals(12.0, sparse.dotProduct(dense), 0.0);
        assertEquals(12.0, sparse.extendedDotProduct(dense), 0.0);
        assertTrue(sparse.isInfinite());
        assertTrue(sparse.isNaNOrInfinite());
    }

    @Test
    public void vectorAddMergesOverlapsAndRetainsNonOverlappingIndices() {
        SparseVector left = new SparseVector(new int[] {1, 4}, new double[] {2.0, 3.0});
        SparseVector right = new SparseVector(new int[] {0, 4, 7}, new double[] {5.0, -2.0, 6.0});

        SparseVector result = left.vectorAdd(right, 0.5);

        assertArrayEquals(new int[] {0, 1, 4, 7}, result.getIndices());
        assertArrayEquals(new double[] {2.5, 2.0, 2.0, 3.0}, result.getValues(), 0.0);
        assertArrayEquals(new double[] {2.0, 3.0}, left.getValues(), 0.0);
    }

    @Test
    public void denseVectorAddProducesSparseResultForNonzeroSums() {
        SparseVector dense = new SparseVector(new double[] {1.0, -2.0, 0.0, 4.0});
        SparseVector sparse = new SparseVector(
                new int[] {0, 1, 3}, new double[] {-1.0, 1.0, 2.0});

        SparseVector result = dense.vectorAdd(sparse, 1.0);

        assertArrayEquals(new int[] {0, 1, 3}, result.getIndices());
        assertEquals(-1.0, result.value(1), 0.0);
        assertEquals(6.0, result.value(3), 0.0);
        assertEquals(0.0, result.value(0), 0.0);
    }

    @Test
    public void normsAndSpecialValuePredicatesDistinguishCases() {
        SparseVector ordinary = new SparseVector(new double[] {-3.0, 4.0, -12.0});
        SparseVector nan = new SparseVector(new double[] {1.0, 0.0 / 0.0});
        SparseVector binary = new SparseVector(new int[] {2});

        assertEquals(-11.0, ordinary.oneNorm(), 0.0);
        assertEquals(19.0, ordinary.absNorm(), 0.0);
        assertEquals(13.0, ordinary.twoNorm(), 0.0);
        assertEquals(12.0, ordinary.infinityNorm(), 0.0);
        assertFalse(ordinary.isNaNOrInfinite());
        assertTrue(nan.isNaN());
        assertFalse(nan.isInfinite());
        assertTrue(nan.isNaNOrInfinite());
        assertFalse(binary.isNaN());
        assertFalse(binary.isInfinite());
    }

    @Test
    public void propertyListConstructorUsesAlphabetAndChoosesBinaryRepresentation() {
        Alphabet alphabet = new Alphabet();
        PropertyList properties = PropertyList.add("gamma", 1.0,
                PropertyList.add("alpha", 1.0, PropertyList.add("beta", 1.0, null)));
        SparseVector vector = new SparseVector(alphabet, properties, false);

        assertTrue(vector.isBinary());
        assertEquals(3, vector.numLocations());
        assertEquals(1.0, vector.value(alphabet.lookupIndex("alpha", false)), 0.0);
        assertEquals(1.0, vector.value(alphabet.lookupIndex("beta", false)), 0.0);
        assertEquals(1.0, vector.value(alphabet.lookupIndex("gamma", false)), 0.0);
    }

    @Test
    public void propertyListConstructorKeepsNumericValuesAndHonorsFrozenAlphabet() {
        Alphabet alphabet = new Alphabet(new Object[] {"known"});
        PropertyList properties = PropertyList.add("unknown", 9.0,
                PropertyList.add("known", -2.5, null));
        SparseVector vector = new SparseVector(alphabet, properties, false, false);

        assertFalse(vector.isBinary());
        assertEquals(1, vector.numLocations());
        assertEquals(-2.5, vector.value(alphabet.lookupIndex("known", false)), 0.0);
        assertEquals(-1, alphabet.lookupIndex("unknown", false));
    }

    @Test
    public void nullPropertyListCreatesEmptyBinaryVector() {
        SparseVector vector = new SparseVector(new Alphabet(), null, false, true);

        assertTrue(vector.isBinary());
        assertEquals(0, vector.numLocations());
        assertEquals(0, vector.singleSize());
    }

    @Test
    public void serializationRoundTripPreservesDenseValues() throws Exception {
        SparseVector original = new SparseVector(new double[] {2.0, -4.5, 8.0});
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream output = new ObjectOutputStream(bytes);
        output.writeObject(original);
        output.close();
        ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()));
        SparseVector restored = (SparseVector) input.readObject();
        input.close();

        assertNull(restored.getIndices());
        assertArrayEquals(new double[] {2.0, -4.5, 8.0}, restored.getValues(), 0.0);
        assertEquals(-4.5, restored.value(1), 0.0);
    }

    @Test
    public void serializationRoundTripPreservesSparseBinaryVector() throws Exception {
        SparseVector original = new SparseVector(new int[] {9, 2, 5});
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream output = new ObjectOutputStream(bytes);
        output.writeObject(original);
        output.close();
        ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()));
        SparseVector restored = (SparseVector) input.readObject();
        input.close();

        assertTrue(restored.isBinary());
        assertArrayEquals(new int[] {2, 5, 9}, restored.getIndices());
        assertEquals(1.0, restored.value(5), 0.0);
        assertEquals(0.0, restored.value(6), 0.0);
    }
}
