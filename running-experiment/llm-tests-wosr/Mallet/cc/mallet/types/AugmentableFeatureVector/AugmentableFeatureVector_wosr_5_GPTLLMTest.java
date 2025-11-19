public class AugmentableFeatureVector_wosr_5_GPTLLMTest { 

 @Test
  public void testAddIndexWithBinaryVectorAndDefaultCapacity() {
    Alphabet alphabet = new Alphabet();
    AugmentableFeatureVector vector = new AugmentableFeatureVector(alphabet, true);
    int index = alphabet.lookupIndex("feature1");
    vector.add(index);
    assertEquals(1.0, vector.value(index), 0.0001);
  }
@Test
  public void testAddIndexWithRealValuedVectorThrowsException() {
    Alphabet alphabet = new Alphabet();
    AugmentableFeatureVector vector = new AugmentableFeatureVector(alphabet, false);
    int index = alphabet.lookupIndex("feature1");
    try {
      vector.add(index);
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("Trying to add binary feature to real-valued vector"));
    }
  }
@Test
  public void testAddByKeyAndValue() {
    Alphabet alphabet = new Alphabet();
    alphabet.lookupIndex("f");
    AugmentableFeatureVector vector = new AugmentableFeatureVector(alphabet, true);
    vector.add("f", 1.0);
    int index = alphabet.lookupIndex("f");
    assertEquals(1.0, vector.value(index), 0.0001);
  }
@Test
  public void testAddWithExistingValueAddsUpInDenseMode() {
    Alphabet alphabet = new Alphabet();
    double[] values = new double[10];
    values[4] = 1.0;
    AugmentableFeatureVector vector = new AugmentableFeatureVector(alphabet, values, 10);
    vector.add(4, 2.0);
    assertEquals(3.0, vector.value(4), 0.0001);
  }
@Test
  public void testAddDuplicateIndexTriggersAggregation() {
    Alphabet alphabet = new Alphabet();
    int index = alphabet.lookupIndex("a");
    int[] indices = {index, index};
    double[] values = {1.0, 2.0};
    AugmentableFeatureVector vector = new AugmentableFeatureVector(alphabet, indices, values, 2, 2, true, false, true);
    assertEquals(1, vector.numLocations());
    assertEquals(3.0, vector.value(index), 0.0001);
  }
@Test
  public void testSetValueDense() {
    Alphabet alphabet = new Alphabet();
    double[] values = {0.2, 0.3, 0.4};
    AugmentableFeatureVector vector = new AugmentableFeatureVector(alphabet, values);
    vector.setValue(1, 2.5);
    assertEquals(2.5, vector.value(1), 0.0001);
  }
@Test
  public void testSetValueSparse() {
    Alphabet alphabet = new Alphabet();
    int index = alphabet.lookupIndex("a");
    int[] indices = {index};
    double[] values = {1.0};
    AugmentableFeatureVector vector = new AugmentableFeatureVector(alphabet, indices, values, 1, 1, true, false, false);
    vector.setValue(index, 7.5);
    assertEquals(7.5, vector.value(index), 0.0001);
  }
@Test
  public void testSetValueAtLocation() {
    Alphabet alphabet = new Alphabet();
    double[] values = {0.1, 0.2, 0.3};
    AugmentableFeatureVector vector = new AugmentableFeatureVector(alphabet, values);
    vector.setValueAtLocation(2, 5.0);
    assertEquals(5.0, vector.valueAtLocation(2), 0.0001);
  }
@Test
  public void testDotProductDenseVector() {
    Alphabet alphabet = new Alphabet();
    double[] values = {1.0, 2.0, 3.0};
    AugmentableFeatureVector vector = new AugmentableFeatureVector(alphabet, values);
    double[] dvalues = {2.0, 2.0, 2.0};
    DenseVector denseVector = new DenseVector(dvalues);
    assertEquals(12.0, vector.dotProduct(denseVector), 0.0001);
  }
@Test
  public void testDotProductSparseVector() {
    Alphabet alphabet = new Alphabet();
    int indexA = alphabet.lookupIndex("a");
    int indexB = alphabet.lookupIndex("b");
    int[] indices = {indexA, indexB};
    double[] values = {1.0, 2.0};
    AugmentableFeatureVector vector = new AugmentableFeatureVector(alphabet, indices, values, 2);
    SparseVector other = new SparseVector(indices, new double[]{2.0, 3.0}, 2);
    assertEquals(8.0, vector.dotProduct(other), 0.0001);
  }
@Test
  public void testPlusEqualsDense() {
    Alphabet alphabet = new Alphabet();
    double[] values = {1.0, 2.0, 3.0};
    AugmentableFeatureVector vector = new AugmentableFeatureVector(alphabet, values);
    double[] otherValues = {0.5, 0.5, 0.5};
    AugmentableFeatureVector v2 = new AugmentableFeatureVector(alphabet, otherValues);
    vector.plusEquals(v2, 1.0);
    assertEquals(1.5, vector.value(0), 0.0001);
    assertEquals(2.5, vector.value(1), 0.0001);
    assertEquals(3.5, vector.value(2), 0.0001);
  }
@Test
  public void testCloneMatrix() {
    Alphabet alphabet = new Alphabet();
    int index = alphabet.lookupIndex("test");
    int[] indices = {index};
    double[] values = {2.0};
    AugmentableFeatureVector vector = new AugmentableFeatureVector(alphabet, indices, values, 1);
    AugmentableFeatureVector clone = (AugmentableFeatureVector) vector.cloneMatrix();
    assertEquals(2.0, clone.value(index), 0.0001);
    assertNotSame(vector, clone);
  }
@Test
  public void testCloneMatrixZeroed() {
    Alphabet alphabet = new Alphabet();
    int index = alphabet.lookupIndex("z");
    int[] indices = {index};
    double[] values = {5.0};
    AugmentableFeatureVector vector = new AugmentableFeatureVector(alphabet, indices, values, 1);
    AugmentableFeatureVector clone = (AugmentableFeatureVector) vector.cloneMatrixZeroed();
    assertEquals(0.0, clone.value(index), 0.0001);
    assertNotSame(vector, clone);
  }
@Test
  public void testOneNorm() {
    Alphabet alphabet = new Alphabet();
    double[] values = {1.0, 2.0, 3.0};
    AugmentableFeatureVector vector = new AugmentableFeatureVector(alphabet, values);
    assertEquals(6.0, vector.oneNorm(), 0.0001);
  }
@Test
  public void testTwoNorm() {
    Alphabet alphabet = new Alphabet();
    double[] values = {1.0, 2.0};
    AugmentableFeatureVector vector = new AugmentableFeatureVector(alphabet, values);
    assertEquals(Math.sqrt(5), vector.twoNorm(), 0.0001);
  }
@Test
  public void testInfinityNorm() {
    Alphabet alphabet = new Alphabet();
    double[] values = {1.0, -2.5, 0.3};
    AugmentableFeatureVector vector = new AugmentableFeatureVector(alphabet, values);
    assertEquals(2.5, vector.infinityNorm(), 0.0001);
  }
@Test
  public void testAddToAccumulator() {
    Alphabet alphabet = new Alphabet();
    double[] values = {1.0, 2.0};
    AugmentableFeatureVector vector = new AugmentableFeatureVector(alphabet, values);
    double[] accumulator = {0.0, 0.0};
    vector.addTo(accumulator, 2.0);
    assertArrayEquals(new double[]{2.0, 4.0}, accumulator, 0.0001);
  }
@Test
  public void testFeatureVectorConversion() {
    Alphabet alphabet = new Alphabet();
    int index = alphabet.lookupIndex("x");
    int[] indices = {index};
    double[] values = {1.0};
    AugmentableFeatureVector aug = new AugmentableFeatureVector(alphabet, indices, values, 1);
    FeatureVector fv = aug.toFeatureVector();
    assertEquals(1, fv.numLocations());
    assertEquals(1.0, fv.valueAtLocation(0), 0.0001);
    assertEquals(index, fv.indexAtLocation(0));
  }
@Test
  public void testAddByPrefix() {
    Alphabet dict1 = new Alphabet();
    int idxA = dict1.lookupIndex("a");
    double[] values = {1.0};
    FeatureVector sourceFv = new FeatureVector(dict1, new int[]{idxA}, values);
    Alphabet dict2 = new Alphabet();
    dict2.lookupIndex("prefix_a"); 
    AugmentableFeatureVector aug = new AugmentableFeatureVector(dict2, true);
    aug.add(sourceFv, "prefix_");
    int idxPrefA = dict2.lookupIndex("prefix_a");
    assertEquals(1.0, aug.value(idxPrefA), 0.0001);
  }
@Test
  public void testAddFromFeatureSequence() {
    Alphabet alphabet = new Alphabet();
    int index = alphabet.lookupIndex("t");
    FeatureSequence fs = new FeatureSequence(alphabet, new String[]{"t"});
    AugmentableFeatureVector vector = new AugmentableFeatureVector(fs, true);
    assertEquals(1.0, vector.value(index), 0.0001);
  }
@Test
  public void testAddFromPropertyList() {
    Alphabet dict = new Alphabet();
    PropertyList pl = PropertyList.add("key", 3.0, null);
    AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, pl, false);
    int index = dict.lookupIndex("key");
    assertEquals(3.0, afv.value(index), 0.0001);
  } 
}