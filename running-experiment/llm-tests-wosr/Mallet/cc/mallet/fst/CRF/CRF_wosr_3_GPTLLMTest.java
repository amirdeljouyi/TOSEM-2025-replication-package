public class CRF_wosr_3_GPTLLMTest { 

 @Test
  public void testDefaultConstructorCreatesEmptyFactors() {
    CRF.Factors factors = new CRF.Factors();
    assertNotNull(factors.weightAlphabet);
    assertEquals(0, factors.weightAlphabet.size());
    assertEquals(0, factors.initialWeights.length);
    assertEquals(0, factors.finalWeights.length);
    assertNull(factors.defaultWeights);
    assertNull(factors.weights);
    assertNull(factors.weightsFrozen);
  }
@Test
  public void testCloneStructureConstructorSharesAlphabet() {
    CRF.Factors original = new CRF.Factors();
    original.weightAlphabet.lookupIndex("w1");
    original.weights = new SparseVector[] { new IndexedSparseVector() };
    original.defaultWeights = new double[] { 3.0 };
    original.weightsFrozen = new boolean[] { false };
    original.initialWeights = new double[] { 1.0 };
    original.finalWeights = new double[] { 2.0 };

    CRF.Factors cloned = new CRF.Factors(original);

    assertSame(original.weightAlphabet, cloned.weightAlphabet);
    assertEquals(original.defaultWeights.length, cloned.defaultWeights.length);
    assertEquals(0, cloned.defaultWeights[0], 0.00001);
    assertEquals(original.weights.length, cloned.weights.length);
    assertEquals(original.weights[0].numLocations(), cloned.weights[0].numLocations());
    assertEquals(original.initialWeights.length, cloned.initialWeights.length);
    assertEquals(0.0, cloned.initialWeights[0], 0.00001);
    assertEquals(0.0, cloned.finalWeights[0], 0.00001);
  }
@Test
  public void testZeroMethodClearsWeights() {
    CRF.Factors original = new CRF.Factors();
    original.weightAlphabet.lookupIndex("w0");
    original.weights = new SparseVector[] {
        new IndexedSparseVector(new int[] { 1, 2 }, new double[] { 0.5, -1.0 }) };
    original.defaultWeights = new double[] { 1.5 };
    original.initialWeights = new double[] { 1.0 };
    original.finalWeights = new double[] { 2.0 };
    original.weightsFrozen = new boolean[] { false };

    original.zero();

    assertEquals(0.0, original.weights[0].value(1), 0.0001);
    assertEquals(0.0, original.weights[0].value(2), 0.0001);
    assertEquals(0.0, original.defaultWeights[0], 0.00001);
    assertEquals(0.0, original.initialWeights[0], 0.00001);
    assertEquals(0.0, original.finalWeights[0], 0.00001);
  }
@Test
  public void testStructureMatchesReturnsTrueOnIdentical() {
    CRF.Factors f1 = new CRF.Factors();
    Alphabet a = new Alphabet();
    a.lookupIndex("w");
    f1.weightAlphabet = a;
    f1.weights = new SparseVector[] { new IndexedSparseVector(new int[] { 0, 1 }, new double[] { 1.0, 2.0 }) };
    f1.defaultWeights = new double[] { 0.0 };
    f1.weightsFrozen = new boolean[] { false };
    f1.initialWeights = new double[] { 0.0 };
    f1.finalWeights = new double[] { 0.0 };

    CRF.Factors f2 = new CRF.Factors();
    f2.weightAlphabet = a;
    f2.weights = new SparseVector[] { new IndexedSparseVector(new int[] { 0, 1 }, new double[] { 0.0, 0.0 }) };
    f2.defaultWeights = new double[] { 0.0 };
    f2.weightsFrozen = new boolean[] { false };
    f2.initialWeights = new double[] { 0.0 };
    f2.finalWeights = new double[] { 0.0 };

    assertTrue(f1.structureMatches(f2));
  }
@Test
  public void testStructureMatchesReturnsFalseOnDifferentWeightSize() {
    CRF.Factors f1 = new CRF.Factors();
    f1.weightAlphabet = new Alphabet();
    f1.weightAlphabet.lookupIndex("w1");
    f1.weights = new SparseVector[] {
        new IndexedSparseVector(new int[] { 0, 1 }, new double[] { 1.0, 2.0 }) };
    f1.defaultWeights = new double[] { 0.0 };
    f1.weightsFrozen = new boolean[] { false };
    f1.initialWeights = new double[] { 0.0 };
    f1.finalWeights = new double[] { 0.0 };

    CRF.Factors f2 = new CRF.Factors();
    f2.weightAlphabet = new Alphabet();
    f2.weightAlphabet.lookupIndex("w1");
    f2.weights = new SparseVector[] {
        new IndexedSparseVector(new int[] { 0 }, new double[] { 1 }) };
    f2.defaultWeights = new double[] { 0.0 };
    f2.weightsFrozen = new boolean[] { false };
    f2.initialWeights = new double[] { 0.0 };
    f2.finalWeights = new double[] { 0.0 };

    assertFalse(f1.structureMatches(f2));
  }
@Test
  public void testPlusEqualsAddsCorrectly() {
    CRF.Factors f1 = new CRF.Factors();
    f1.weightAlphabet = new Alphabet();
    f1.weightAlphabet.lookupIndex("w");
    f1.weights = new SparseVector[] {
        new IndexedSparseVector(new int[] { 0 }, new double[] { 1.0 }) };
    f1.defaultWeights = new double[] { 2.0 };
    f1.weightsFrozen = new boolean[] { false };
    f1.initialWeights = new double[] { 0.1 };
    f1.finalWeights = new double[] { 0.2 };

    CRF.Factors f2 = new CRF.Factors(f1.weightAlphabet);
    f2.weights = new SparseVector[] {
        new IndexedSparseVector(new int[] { 0 }, new double[] { 3.0 }) };
    f2.defaultWeights = new double[] { 1.0 };
    f2.weightsFrozen = new boolean[] { false };
    f2.initialWeights = new double[] { 1.0 };
    f2.finalWeights = new double[] { 2.0 };

    f1.plusEquals(f2, 2.0);

    assertEquals(7.0, f1.weights[0].value(0), 0.00001);
    assertEquals(4.0, f1.defaultWeights[0], 0.00001);
    assertEquals(2.1, f1.initialWeights[0], 0.00001);
    assertEquals(4.2, f1.finalWeights[0], 0.00001);
  }
@Test
  public void testGetAndSetParameter() {
    CRF.Factors f = new CRF.Factors();
    f.weightAlphabet = new Alphabet();
    f.weightAlphabet.lookupIndex("w");
    f.weights = new SparseVector[] {
        new IndexedSparseVector(new int[] { 1 }, new double[] { 0.0 }) };
    f.defaultWeights = new double[] { 0.0 };
    f.weightsFrozen = new boolean[] { false };
    f.initialWeights = new double[] { 5.0 };
    f.finalWeights = new double[] { 10.0 };

    double[] values = new double[3];
    f.getParameters(values);
    assertEquals(5.0, values[0], 0.00001);
    assertEquals(10.0, values[1], 0.00001);

    f.setParameter(1, -9.8);
    assertEquals(-9.8, f.finalWeights[0], 0.0001);

    double p = f.getParameter(1);
    assertEquals(-9.8, p, 0.0001);
  }
@Test
  public void testSerializationRoundTrip() throws Exception {
    CRF.Factors f1 = new CRF.Factors();
    f1.weightAlphabet.lookupIndex("w");
    f1.weights = new SparseVector[] {
        new IndexedSparseVector(new int[] { 0 }, new double[] { 3.14 }) };
    f1.defaultWeights = new double[] { 2.71 };
    f1.weightsFrozen = new boolean[] { true };
    f1.initialWeights = new double[] { 1.0 };
    f1.finalWeights = new double[] { 2.0 };

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream out = new ObjectOutputStream(bos);
    out.writeObject(f1);
    out.flush();
    out.close();

    ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
    CRF.Factors f2 = (CRF.Factors) in.readObject();
    in.close();

    assertEquals(3.14, f2.weights[0].value(0), 0.00001);
    assertEquals(2.71, f2.defaultWeights[0], 0.00001);
    assertEquals(1.0, f2.initialWeights[0], 0.00001);
    assertEquals(2.0, f2.finalWeights[0], 0.00001);
    assertTrue(f2.weightsFrozen[0]);
  }
@Test
  public void testGaussianPriorComputesCorrectly() {
    CRF.Factors f = new CRF.Factors();
    f.weightAlphabet.lookupIndex("w");
    f.weights = new SparseVector[] {
        new IndexedSparseVector(new int[] { 0 }, new double[] { 1.0 }) };
    f.defaultWeights = new double[] { 2.0 };
    f.weightsFrozen = new boolean[] { false };
    f.initialWeights = new double[] { 3.0 };
    f.finalWeights = new double[] { 4.0 };

    double result = f.gaussianPrior(2.0);
    double expected = -((3.0 * 3.0 + 4.0 * 4.0 + 2.0 * 2.0 + 1.0 * 1.0) / 4.0);
    assertEquals(expected, result, 0.0001);
  }
@Test
  public void testGetParametersAbsNormCorrectlyAggregates() {
    CRF.Factors f = new CRF.Factors();
    f.weightAlphabet.lookupIndex("w");
    f.weights = new SparseVector[] {
        new IndexedSparseVector(new int[] { 1, 2 }, new double[] { -3.0, 4.0 }) };
    f.defaultWeights = new double[] { -5.0 };
    f.initialWeights = new double[] { -2.0 };
    f.finalWeights = new double[] { 6.0 };
    f.weightsFrozen = new boolean[] { false };

    double expected = Math.abs(-2.0) + Math.abs(6.0)
      + Math.abs(-5.0) + Math.abs(-3.0) + Math.abs(4.0);
    double result = f.getParametersAbsNorm();
    assertEquals(expected, result, 0.0001);
  } 
}