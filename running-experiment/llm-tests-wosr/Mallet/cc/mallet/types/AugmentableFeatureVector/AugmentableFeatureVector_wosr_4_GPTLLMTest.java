public class AugmentableFeatureVector_wosr_4_GPTLLMTest { 

 @Test
  public void testBinaryConstructorAndAddIndex() {
    Alphabet dict = new Alphabet();
    dict.lookupIndex("word1");
    dict.lookupIndex("word2");

    AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, true);
    afv.add(dict.lookupIndex("word1"));

    assertEquals(1, afv.numLocations());
    assertEquals(1.0, afv.value(dict.lookupIndex("word1")), 0.0);
  }
@Test
  public void testDenseConstructorAndAddValue() {
    Alphabet dict = new Alphabet();
    dict.lookupIndex("feature1");
    dict.lookupIndex("feature2");

    double[] values = new double[]{0.5, 1.5};
    AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, values, 2);

    assertEquals(2, afv.numLocations());
    assertEquals(0.5, afv.value(0), 0.0001);
    assertEquals(1.5, afv.value(1), 0.0001);
  }
@Test
  public void testAddWithPrefix() {
    Alphabet dict1 = new Alphabet();
    int idx1 = dict1.lookupIndex("f1");
    FeatureVector fv = new FeatureVector(dict1, new int[]{idx1}, new double[]{3.0});
    
    Alphabet dict2 = new Alphabet();
    AugmentableFeatureVector afv = new AugmentableFeatureVector(dict2);
    afv.add(fv, "p_");

    assertEquals(1, afv.numLocations());
    Object name = dict2.lookupObject(afv.indexAtLocation(0));
    assertTrue(name.toString().startsWith("p_"));
  }
@Test
  public void testAddFeatureVectorBinaryEquality() {
    Alphabet dict = new Alphabet();
    dict.lookupIndex("x1");
    dict.lookupIndex("x2");

    AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, true);
    afv.add(dict.lookupIndex("x1"));

    FeatureVector fv = new FeatureVector(dict, new int[]{dict.lookupIndex("x2")}, null);

    afv.add(fv);

    assertEquals(2, afv.numLocations());
    assertEquals(1.0, afv.value(dict.lookupIndex("x1")), 0.0);
    assertEquals(1.0, afv.value(dict.lookupIndex("x2")), 0.0);
  }
@Test
  public void testAddSameIndexTwiceAndRemoveDuplicates() {
    Alphabet dict = new Alphabet();
    dict.lookupIndex("a");
    dict.lookupIndex("b");

    AugmentableFeatureVector afv = new AugmentableFeatureVector(dict);
    afv.add(dict.lookupIndex("a"), 1.0);
    afv.add(dict.lookupIndex("a"), 2.0);
    afv.sortIndices();

    assertEquals(1, afv.numLocations());
    assertEquals(3.0, afv.value(dict.lookupIndex("a")), 0.0);
  }
@Test
  public void testDotProductWithDenseVector() {
    Alphabet dict = new Alphabet();
    dict.lookupIndex("a");
    dict.lookupIndex("b");
    dict.lookupIndex("c");

    AugmentableFeatureVector afv = new AugmentableFeatureVector(dict);
    afv.add(dict.lookupIndex("a"), 2.0);
    afv.add(dict.lookupIndex("b"), 3.0);

    DenseVector dv = new DenseVector(new double[]{1.0, 2.0, 4.0});
    double result = afv.dotProduct(dv);

    assertEquals(2.0 * 1.0 + 3.0 * 2.0, result, 0.0001);
  }
@Test
  public void testPlusEqualsWithSameAlphabet() {
    Alphabet dict = new Alphabet();
    int a = dict.lookupIndex("f1");
    int b = dict.lookupIndex("f2");

    AugmentableFeatureVector afv1 = new AugmentableFeatureVector(dict);
    afv1.add(a, 1.0);
    afv1.add(b, 2.0);

    AugmentableFeatureVector afv2 = new AugmentableFeatureVector(dict);
    afv2.add(a, 4.0);
    afv2.add(b, 3.0);
    afv2.sortIndices();

    afv1.plusEquals(afv2, 1.0);

    afv1.sortIndices();
    assertEquals(5.0, afv1.value(a), 0.0);
    assertEquals(5.0, afv1.value(b), 0.0);
  }
@Test
  public void testCloneMatrixCreatesCopy() {
    Alphabet dict = new Alphabet();
    dict.lookupIndex("z");

    AugmentableFeatureVector afv = new AugmentableFeatureVector(dict);
    afv.add(0, 5.0);

    ConstantMatrix clone = afv.cloneMatrix();

    assertTrue(clone instanceof AugmentableFeatureVector);
    assertEquals(5.0, ((AugmentableFeatureVector) clone).value(0), 0.0);
  }
@Test
  public void testCloneMatrixZeroedClearsValues() {
    Alphabet dict = new Alphabet();
    dict.lookupIndex("z");

    AugmentableFeatureVector afv = new AugmentableFeatureVector(dict);
    afv.add(0, 5.0);

    ConstantMatrix cloneZeroed = afv.cloneMatrixZeroed();

    assertTrue(cloneZeroed instanceof AugmentableFeatureVector);
    assertEquals(0.0, ((AugmentableFeatureVector) cloneZeroed).value(0), 0.0);
  }
@Test
  public void testOneNorm() {
    Alphabet dict = new Alphabet();
    dict.lookupIndex("x");
    dict.lookupIndex("y");
    dict.lookupIndex("z");

    AugmentableFeatureVector afv = new AugmentableFeatureVector(dict);
    afv.add(0, 4.0);
    afv.add(1, 3.0);
    afv.add(2, 1.0);

    assertEquals(8.0, afv.oneNorm(), 0.0);
  }
@Test
  public void testTwoNorm() {
    Alphabet dict = new Alphabet();
    dict.lookupIndex("a");
    dict.lookupIndex("b");

    AugmentableFeatureVector afv = new AugmentableFeatureVector(dict);
    afv.add(0, 3.0);
    afv.add(1, 4.0);

    assertEquals(5.0, afv.twoNorm(), 0.0001);
  }
@Test
  public void testInfinityNorm() {
    Alphabet dict = new Alphabet();
    dict.lookupIndex("a");
    dict.lookupIndex("b");
    dict.lookupIndex("c");

    AugmentableFeatureVector afv = new AugmentableFeatureVector(dict);
    afv.add(0, 2.0);
    afv.add(1, 5.0);
    afv.add(2, -7.0);

    assertEquals(7.0, afv.infinityNorm(), 0.0);
  }
@Test
  public void testToSparseVector() {
    Alphabet dict = new Alphabet();
    int idx = dict.lookupIndex("a");

    AugmentableFeatureVector afv = new AugmentableFeatureVector(dict);
    afv.add(idx, 1.0);

    SparseVector v = afv.toSparseVector();

    assertEquals(1.0, v.value(idx), 0.0);
    assertEquals(idx, v.indexAtLocation(0));
    assertEquals(1, v.numLocations());
  }
@Test(expected = IllegalArgumentException.class)
  public void testAddIllegalRealValueToBinaryVector() {
    Alphabet dict = new Alphabet();
    dict.lookupIndex("bin");

    AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, true);
    afv.add(0, 2.0);
  }
@Test(expected = IllegalArgumentException.class)
  public void testAddIllegalBinaryToRealVector() {
    Alphabet dict = new Alphabet();
    dict.lookupIndex("real");

    AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, new double[1]);
    afv.add(0);
  }
@Test
  public void testAddToAccumulatorDense() {
    Alphabet dict = new Alphabet();
    dict.lookupIndex("a");
    dict.lookupIndex("b");

    AugmentableFeatureVector afv = new AugmentableFeatureVector(dict);
    afv.add(0, 2.0);
    afv.add(1, 3.0);

    double[] acc = new double[2];
    afv.addTo(acc, 2.0);

    assertEquals(4.0, acc[0], 0.0);
    assertEquals(6.0, acc[1], 0.0);
  }
@Test
  public void testSetValueAndRetrieve() {
    Alphabet dict = new Alphabet();
    dict.lookupIndex("x");

    double[] values = new double[]{1.0, 2.0};
    int[] indices = new int[]{0, 1};

    AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, indices, values, 2);
    afv.setValue(1, 9.0);

    assertEquals(9.0, afv.value(1), 0.0);
  }
@Test
  public void testSingleSizeWithSparseVector() {
    Alphabet dict = new Alphabet();
    dict.lookupIndex("a");
    dict.lookupIndex("b");

    AugmentableFeatureVector afv = new AugmentableFeatureVector(dict);
    afv.add(0, 1.0);
    afv.add(1, 1.0);

    assertEquals(1, afv.singleSize());
  }
@Test
  public void testSetAll() {
    Alphabet dict = new Alphabet();
    double[] values = new double[]{0.1, 0.2, 0.9};

    AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, values);

    afv.setAll(5.0);

    assertEquals(5.0, afv.value(0), 0.0);
    assertEquals(5.0, afv.value(1), 0.0);
    assertEquals(5.0, afv.value(2), 0.0);
  } 
}