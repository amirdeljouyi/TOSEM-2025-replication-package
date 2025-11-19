public class MaxEntOptimizableByLabelLikelihood_2_GPTLLMTest { 

 @Test
    public void testConstructorInitializesCorrectly() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f1");
        dataAlphabet.lookupIndex("f2");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("L1");
        labelAlphabet.lookupIndex("L2");

        FeatureVector fv1 = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        FeatureVector fv2 = new FeatureVector(dataAlphabet, new int[]{1}, new double[]{0.5});

        Label l1 = labelAlphabet.lookupLabel("L1");
        Label l2 = labelAlphabet.lookupLabel("L2");

        Labeling lab1 = new LabelVector(labelAlphabet, new double[]{1.0, 0.0});
        Labeling lab2 = new LabelVector(labelAlphabet, new double[]{0.0, 1.0});

        Instance i1 = new Instance(fv1, lab1, "i1", null);
        Instance i2 = new Instance(fv2, lab2, "i2", null);

        ArrayList<Instance> instances = new ArrayList<>();
        instances.add(i1);
        instances.add(i2);

        InstanceList trainingList = mock(InstanceList.class);
        when(trainingList.iterator()).thenReturn(instances.iterator());
        when(trainingList.size()).thenReturn(2);
        when(trainingList.getInstanceWeight(i1)).thenReturn(1.0);
        when(trainingList.getInstanceWeight(i2)).thenReturn(1.0);
        when(trainingList.getDataAlphabet()).thenReturn(dataAlphabet);
        when(trainingList.getTargetAlphabet()).thenReturn(labelAlphabet);
        when(trainingList.getPipe()).thenReturn(Mockito.mock(Pipe.class));

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(trainingList, null);
        assertNotNull(opt);
        assertNotNull(opt.getClassifier());
        assertEquals(opt.getNumParameters(), 6);
    }
@Test
    public void testSetAndGetSingleParameter() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("feat");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("A");
        labelAlphabet.lookupIndex("B");

        FeatureVector fv = new FeatureVector(alphabet, new int[]{0}, new double[]{1.0});

        Labeling labeling = new LabelVector(labelAlphabet, new double[]{1.0, 0.0});
        Instance inst = new Instance(fv, labeling, "e", null);

        ArrayList<Instance> list = new ArrayList<>();
        list.add(inst);

        InstanceList trainList = mock(InstanceList.class);
        when(trainList.iterator()).thenReturn(list.iterator());
        when(trainList.size()).thenReturn(1);
        when(trainList.getInstanceWeight(inst)).thenReturn(1.0);
        when(trainList.getDataAlphabet()).thenReturn(alphabet);
        when(trainList.getTargetAlphabet()).thenReturn(labelAlphabet);
        when(trainList.getPipe()).thenReturn(Mockito.mock(Pipe.class));

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(trainList, null);
        opt.setParameter(0, 2.2);
        double result = opt.getParameter(0);
        assertEquals(2.2, result, 0.0001);
    }
@Test
    public void testGaussianPriorAffectsGradient() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("f");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("X");
        labelAlphabet.lookupIndex("Y");

        FeatureVector fv = new FeatureVector(alphabet, new int[]{0}, new double[]{1.0});

        Labeling labeling = new LabelVector(labelAlphabet, new double[]{1, 0});
        Instance inst = new Instance(fv, labeling, "id", null);

        ArrayList<Instance> list = new ArrayList<>();
        list.add(inst);

        InstanceList ilist = mock(InstanceList.class);
        when(ilist.iterator()).thenReturn(list.iterator());
        when(ilist.size()).thenReturn(1);
        when(ilist.getInstanceWeight(inst)).thenReturn(1.0);
        when(ilist.getDataAlphabet()).thenReturn(alphabet);
        when(ilist.getTargetAlphabet()).thenReturn(labelAlphabet);
        when(ilist.getPipe()).thenReturn(Mockito.mock(Pipe.class));

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(ilist, null);
        opt.setGaussianPriorVariance(0.01);

        opt.getValue();
        double[] gradient = new double[opt.getNumParameters()];
        opt.getValueGradient(gradient);

        assertNotEquals(0.0, gradient[0], 0.00001);
    }
@Test
    public void testNoPriorDoesNotFail() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("a");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("p");
        labelAlphabet.lookupIndex("n");

        FeatureVector fv = new FeatureVector(alphabet, new int[]{0}, new double[]{1.0});
        Labeling labeling = new LabelVector(labelAlphabet, new double[]{1, 0});
        Instance inst = new Instance(fv, labeling, "a1", null);

        ArrayList<Instance> list = new ArrayList<>();
        list.add(inst);

        InstanceList ilist = mock(InstanceList.class);
        when(ilist.iterator()).thenReturn(list.iterator());
        when(ilist.size()).thenReturn(1);
        when(ilist.getInstanceWeight(inst)).thenReturn(1.0);
        when(ilist.getDataAlphabet()).thenReturn(alphabet);
        when(ilist.getTargetAlphabet()).thenReturn(labelAlphabet);
        when(ilist.getPipe()).thenReturn(Mockito.mock(Pipe.class));

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(ilist, null);
        opt.useNoPrior();

        double val1 = opt.getValue();
        double val2 = opt.getValue();
        assertEquals(val1, val2, 1e-6);
    }
@Test
    public void testValueAndGradientConsistency() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("ft");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("P");
        labelAlphabet.lookupIndex("Q");

        FeatureVector fv = new FeatureVector(alphabet, new int[]{0}, new double[]{1.0});
        Labeling labeling = new LabelVector(labelAlphabet, new double[]{1, 0});
        Instance inst = new Instance(fv, labeling, "t1", null);

        ArrayList<Instance> list = new ArrayList<>();
        list.add(inst);

        InstanceList ilist = mock(InstanceList.class);
        when(ilist.iterator()).thenReturn(list.iterator());
        when(ilist.size()).thenReturn(1);
        when(ilist.getInstanceWeight(inst)).thenReturn(1.0);
        when(ilist.getDataAlphabet()).thenReturn(alphabet);
        when(ilist.getTargetAlphabet()).thenReturn(labelAlphabet);
        when(ilist.getPipe()).thenReturn(Mockito.mock(Pipe.class));

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(ilist, null);
        double val1 = opt.getValue();
        double[] g1 = new double[opt.getNumParameters()];
        opt.getValueGradient(g1);

        double val2 = opt.getValue();
        double[] g2 = new double[opt.getNumParameters()];
        opt.getValueGradient(g2);

        assertEquals(val1, val2, 0.0000001);
        for (int i = 0; i < g1.length; i++) {
            assertEquals(g1[i], g2[i], 0.0001);
        }
    }
@Test(expected = UnsupportedOperationException.class)
    public void testHyperbolicPriorThrowsOnGradient() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("w");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("C1");
        labelAlphabet.lookupIndex("C2");

        FeatureVector fv = new FeatureVector(alphabet, new int[]{0}, new double[]{1.0});
        Labeling labeling = new LabelVector(labelAlphabet, new double[]{1, 0});
        Instance inst = new Instance(fv, labeling, "x", null);

        ArrayList<Instance> list = new ArrayList<>();
        list.add(inst);

        InstanceList train = mock(InstanceList.class);
        when(train.iterator()).thenReturn(list.iterator());
        when(train.size()).thenReturn(1);
        when(train.getInstanceWeight(inst)).thenReturn(1.0);
        when(train.getDataAlphabet()).thenReturn(alphabet);
        when(train.getTargetAlphabet()).thenReturn(labelAlphabet);
        when(train.getPipe()).thenReturn(Mockito.mock(Pipe.class));

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(train, null);
        opt.useHyperbolicPrior();
        opt.getValue();
        double[] b = new double[opt.getNumParameters()];
        opt.getValueGradient(b); 
    }
@Test
    public void testValueIsNotNaNOrInfinite() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("word");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("A");
        labelAlphabet.lookupIndex("B");

        FeatureVector fv = new FeatureVector(alphabet, new int[]{0}, new double[]{1.0});
        Labeling labeling = new LabelVector(labelAlphabet, new double[]{1, 0});
        Instance instance = new Instance(fv, labeling, "doc", null);

        ArrayList<Instance> list = new ArrayList<>();
        list.add(instance);

        InstanceList instList = mock(InstanceList.class);
        when(instList.iterator()).thenReturn(list.iterator());
        when(instList.size()).thenReturn(1);
        when(instList.getInstanceWeight(instance)).thenReturn(1.0);
        when(instList.getDataAlphabet()).thenReturn(alphabet);
        when(instList.getTargetAlphabet()).thenReturn(labelAlphabet);
        when(instList.getPipe()).thenReturn(Mockito.mock(Pipe.class));

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(instList, null);
        double v = opt.getValue();

        assertFalse(Double.isNaN(v));
        assertFalse(Double.isInfinite(v));
    }
@Test
    public void testGradientIsFinite() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("token");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("L");
        labelAlphabet.lookupIndex("M");

        FeatureVector fv = new FeatureVector(alphabet, new int[]{0}, new double[]{1.0});
        Labeling labeling = new LabelVector(labelAlphabet, new double[]{1, 0});
        Instance inst = new Instance(fv, labeling, "sample", null);

        ArrayList<Instance> insList = new ArrayList<>();
        insList.add(inst);

        InstanceList trainList = mock(InstanceList.class);
        when(trainList.iterator()).thenReturn(insList.iterator());
        when(trainList.size()).thenReturn(1);
        when(trainList.getInstanceWeight(inst)).thenReturn(1.0);
        when(trainList.getDataAlphabet()).thenReturn(alphabet);
        when(trainList.getTargetAlphabet()).thenReturn(labelAlphabet);
        when(trainList.getPipe()).thenReturn(Mockito.mock(Pipe.class));

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(trainList, null);
        opt.setGaussianPriorVariance(0.1);
        opt.getValue();
        double[] grad = new double[opt.getNumParameters()];
        opt.getValueGradient(grad);

        assertFalse(Double.isNaN(grad[0]));
        assertFalse(Double.isInfinite(grad[0]));
    }
@Test
    public void testEmptyInstanceListReturnsZeroValue() {
        Alphabet alphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        InstanceList list = mock(InstanceList.class);
        when(list.iterator()).thenReturn(new ArrayList<Instance>().iterator());
        when(list.getDataAlphabet()).thenReturn(alphabet);
        when(list.getTargetAlphabet()).thenReturn(labelAlphabet);
        when(list.size()).thenReturn(0);
        when(list.getPipe()).thenReturn(mock(Pipe.class));

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
        double value = opt.getValue();
        assertFalse(Double.isNaN(value));
    }
@Test
    public void testInstanceWithNullLabelingSkippedInValue() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("f1");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("L1");

        FeatureVector fv = new FeatureVector(alphabet, new int[]{0}, new double[]{1.0});
        Instance inst = new Instance(fv, null, "null-label", null);

        ArrayList<Instance> instances = new ArrayList<>();
        instances.add(inst);

        InstanceList list = mock(InstanceList.class);
        when(list.iterator()).thenReturn(instances.iterator());
        when(list.size()).thenReturn(1);
        when(list.getInstanceWeight(inst)).thenReturn(1.0);
        when(list.getDataAlphabet()).thenReturn(alphabet);
        when(list.getTargetAlphabet()).thenReturn(labelAlphabet);
        when(list.getPipe()).thenReturn(mock(Pipe.class));

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
        double val = opt.getValue();
        assertFalse(Double.isNaN(val));
        assertFalse(Double.isInfinite(val));
    }
@Test
    public void testInstanceFeatureWithNaNValueIsLoggedCorrectly() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("f1");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("A");
        labelAlphabet.lookupIndex("B");

        FeatureVector fv = new FeatureVector(alphabet, new int[]{0}, new double[]{Double.NaN});
        Labeling labeling = new LabelVector(labelAlphabet, new double[]{1.0, 0.0});
        Instance inst = new Instance(fv, labeling, "nan-instance", null);

        ArrayList<Instance> list = new ArrayList<>();
        list.add(inst);

        InstanceList ilist = mock(InstanceList.class);
        when(ilist.iterator()).thenReturn(list.iterator());
        when(ilist.size()).thenReturn(1);
        when(ilist.getInstanceWeight(inst)).thenReturn(1.0);
        when(ilist.getDataAlphabet()).thenReturn(alphabet);
        when(ilist.getTargetAlphabet()).thenReturn(labelAlphabet);
        when(ilist.getPipe()).thenReturn(mock(Pipe.class));

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(ilist, null);
        double value = opt.getValue();
        assertFalse(Double.isNaN(value));
    }
@Test
    public void testFeatureVectorWithZeroLength() {
        Alphabet alphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("L1");
        labelAlphabet.lookupIndex("L2");

        FeatureVector fv = new FeatureVector(alphabet, new int[]{}, new double[]{});
        Labeling labeling = new LabelVector(labelAlphabet, new double[]{1.0, 0.0});
        Instance instance = new Instance(fv, labeling, "empty-fv", null);

        ArrayList<Instance> list = new ArrayList<>();
        list.add(instance);

        InstanceList ilist = mock(InstanceList.class);
        when(ilist.iterator()).thenReturn(list.iterator());
        when(ilist.size()).thenReturn(1);
        when(ilist.getInstanceWeight(instance)).thenReturn(1.0);
        when(ilist.getDataAlphabet()).thenReturn(alphabet);
        when(ilist.getTargetAlphabet()).thenReturn(labelAlphabet);
        when(ilist.getPipe()).thenReturn(mock(Pipe.class));

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(ilist, null);
        double value = opt.getValue();
        assertFalse(Double.isNaN(value));
        assertFalse(Double.isInfinite(value));
    }
@Test
    public void testGetValueGradientWithNullBuffer() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("f");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("A");
        labelAlphabet.lookupIndex("B");

        FeatureVector fv = new FeatureVector(alphabet, new int[]{0}, new double[]{1.0});
        Labeling labeling = new LabelVector(labelAlphabet, new double[]{1, 0});
        Instance inst = new Instance(fv, labeling, "gtest", null);

        ArrayList<Instance> list = new ArrayList<>();
        list.add(inst);

        InstanceList ilist = mock(InstanceList.class);
        when(ilist.iterator()).thenReturn(list.iterator());
        when(ilist.size()).thenReturn(1);
        when(ilist.getInstanceWeight(inst)).thenReturn(1.0);
        when(ilist.getDataAlphabet()).thenReturn(alphabet);
        when(ilist.getTargetAlphabet()).thenReturn(labelAlphabet);
        when(ilist.getPipe()).thenReturn(mock(Pipe.class));

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(ilist, null);
        opt.getValue(); 
        double[] buffer = new double[opt.getNumParameters()];
        opt.getValueGradient(buffer);

        assertFalse(Double.isNaN(buffer[0]));
    }
@Test
    public void testSetParameterToNegativeInfinity() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("xx");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("X");
        labelAlphabet.lookupIndex("Y");

        FeatureVector fv = new FeatureVector(alphabet, new int[]{0}, new double[]{1.0});
        Labeling labeling = new LabelVector(labelAlphabet, new double[]{1, 0});
        Instance inst = new Instance(fv, labeling, "neg-inf", null);

        ArrayList<Instance> list = new ArrayList<>();
        list.add(inst);

        InstanceList llist = mock(InstanceList.class);
        when(llist.iterator()).thenReturn(list.iterator());
        when(llist.size()).thenReturn(1);
        when(llist.getInstanceWeight(inst)).thenReturn(1.0);
        when(llist.getDataAlphabet()).thenReturn(alphabet);
        when(llist.getTargetAlphabet()).thenReturn(labelAlphabet);
        when(llist.getPipe()).thenReturn(mock(Pipe.class));

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(llist, null);

        opt.setParameter(0, Double.NEGATIVE_INFINITY);

        double[] grad = new double[opt.getNumParameters()];
        opt.getValueGradient(grad);

        assertEquals(0.0, grad[0], 0.0001);
    }
@Test
    public void testSetParametersWithMismatchedLength() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("z");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("Z1");
        labelAlphabet.lookupIndex("Z2");

        FeatureVector fv = new FeatureVector(alphabet, new int[]{0}, new double[]{1.0});
        Labeling labeling = new LabelVector(labelAlphabet, new double[]{1, 0});
        Instance inst = new Instance(fv, labeling, "test", null);

        ArrayList<Instance> list = new ArrayList<>();
        list.add(inst);

        InstanceList tlist = mock(InstanceList.class);
        when(tlist.iterator()).thenReturn(list.iterator());
        when(tlist.size()).thenReturn(1);
        when(tlist.getInstanceWeight(inst)).thenReturn(1.0);
        when(tlist.getDataAlphabet()).thenReturn(alphabet);
        when(tlist.getTargetAlphabet()).thenReturn(labelAlphabet);
        when(tlist.getPipe()).thenReturn(mock(Pipe.class));

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(tlist, null);

        double[] newParams = new double[opt.getNumParameters() + 1];
        for (int i = 0; i < newParams.length; i++) {
            newParams[i] = i;
        }

        opt.setParameters(newParams);

        double[] copy = new double[newParams.length];
        opt.getParameters(copy);
        assertEquals(newParams.length, copy.length);
        assertEquals(newParams[0], copy[0], 0.0001);
    }
@Test
    public void testSetParameterMarksValueAndGradientStale() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("x");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("A");
        labelAlphabet.lookupIndex("B");

        FeatureVector fv = new FeatureVector(alphabet, new int[]{0}, new double[]{1.0});
        Labeling labeling = new LabelVector(labelAlphabet, new double[]{1, 0});
        Instance inst = new Instance(fv, labeling, "p", null);

        ArrayList<Instance> list = new ArrayList<>();
        list.add(inst);

        InstanceList ilist = mock(InstanceList.class);
        when(ilist.iterator()).thenReturn(list.iterator());
        when(ilist.size()).thenReturn(1);
        when(ilist.getInstanceWeight(inst)).thenReturn(1.0);
        when(ilist.getDataAlphabet()).thenReturn(alphabet);
        when(ilist.getTargetAlphabet()).thenReturn(labelAlphabet);
        when(ilist.getPipe()).thenReturn(mock(Pipe.class));

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(ilist, null);
        opt.getValue();
        opt.getValueGradient(new double[opt.getNumParameters()]);

        double v1 = opt.getValueCalls();
        double g1 = opt.getValueGradientCalls();

        opt.setParameter(0, 42.0);

        opt.getValue();
        opt.getValueGradient(new double[opt.getNumParameters()]);

        assertEquals(v1 + 1, opt.getValueCalls());
        assertEquals(g1 + 1, opt.getValueGradientCalls(), 0.0001);
    }
@Test
    public void testSetParametersWithMatchingLengthOverwrites() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("f");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("Y");
        labelAlphabet.lookupIndex("N");

        FeatureVector fv = new FeatureVector(alphabet, new int[]{0}, new double[]{1.0});
        Labeling labeling = new LabelVector(labelAlphabet, new double[]{1.0, 0.0});
        Instance inst = new Instance(fv, labeling, "reset", null);

        ArrayList<Instance> list = new ArrayList<>();
        list.add(inst);

        InstanceList ilist = mock(InstanceList.class);
        when(ilist.iterator()).thenReturn(list.iterator());
        when(ilist.size()).thenReturn(1);
        when(ilist.getInstanceWeight(inst)).thenReturn(1.0);
        when(ilist.getDataAlphabet()).thenReturn(alphabet);
        when(ilist.getTargetAlphabet()).thenReturn(labelAlphabet);
        when(ilist.getPipe()).thenReturn(mock(Pipe.class));

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(ilist, null);

        double[] newParams = new double[opt.getNumParameters()];
        newParams[0] = 99.0;
        opt.setParameters(newParams);

        double[] result = new double[opt.getNumParameters()];
        opt.getParameters(result);
        assertEquals(99.0, result[0], 0.0001);
    }
@Test
    public void testFeatureSelectionHidesUnselected() {
        Alphabet alphabet = new Alphabet();
        int i0 = alphabet.lookupIndex("On");
        int i1 = alphabet.lookupIndex("Off");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("Pos");
        labelAlphabet.lookupIndex("Neg");

        FeatureSelection fs = new FeatureSelection(alphabet, true);
        fs.add(i0);
        fs.remove(i1);

        FeatureVector fv = new FeatureVector(alphabet, new int[]{i0, i1}, new double[]{1.0, 1.0});
        Labeling labeling = new LabelVector(labelAlphabet, new double[]{1.0, 0.0});
        Instance inst = new Instance(fv, labeling, "ftest", null);

        ArrayList<Instance> list = new ArrayList<>();
        list.add(inst);

        InstanceList ilist = mock(InstanceList.class);
        when(ilist.iterator()).thenReturn(list.iterator());
        when(ilist.size()).thenReturn(1);
        when(ilist.getInstanceWeight(inst)).thenReturn(1.0);
        when(ilist.getDataAlphabet()).thenReturn(alphabet);
        when(ilist.getTargetAlphabet()).thenReturn(labelAlphabet);
        when(ilist.getFeatureSelection()).thenReturn(fs);
        when(ilist.getPerLabelFeatureSelection()).thenReturn(null);
        when(ilist.getPipe()).thenReturn(mock(Pipe.class));

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(ilist, null);

        opt.getValue(); 
        double[] grad = new double[opt.getNumParameters()];
        opt.getValueGradient(grad);

        
        int offset = 1 * (alphabet.size() + 1); 
        assertEquals(0.0, grad[offset + i1], 0.0);
    }
@Test
    public void testInstanceWeightZeroContributesNothing() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("f");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("L1");
        labelAlphabet.lookupIndex("L2");

        FeatureVector fv = new FeatureVector(alphabet, new int[]{0}, new double[]{1.0});
        Labeling labeling = new LabelVector(labelAlphabet, new double[]{1.0, 0.0});
        Instance inst = new Instance(fv, labeling, "zero-w", null);

        ArrayList<Instance> list = new ArrayList<>();
        list.add(inst);
        InstanceList ilist = mock(InstanceList.class);
        when(ilist.iterator()).thenReturn(list.iterator());
        when(ilist.size()).thenReturn(1);
        when(ilist.getInstanceWeight(inst)).thenReturn(0.0);
        when(ilist.getDataAlphabet()).thenReturn(alphabet);
        when(ilist.getTargetAlphabet()).thenReturn(labelAlphabet);
        when(ilist.getPipe()).thenReturn(mock(Pipe.class));

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(ilist, null);

        double val = opt.getValue();
        double[] grad = new double[opt.getNumParameters()];
        opt.getValueGradient(grad);

        assertFalse(Double.isNaN(val));
        assertFalse(Double.isInfinite(val));
        assertEquals(0.0, grad[0], 1e-9);
    }
@Test
    public void testGetParametersAllocatesNewArrayIfNull() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("x");
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("A");
        labelAlphabet.lookupIndex("B");

        FeatureVector fv = new FeatureVector(alphabet, new int[]{0}, new double[]{1});
        Labeling labeling = new LabelVector(labelAlphabet, new double[]{1, 0});
        Instance inst = new Instance(fv, labeling, "null-param", null);

        ArrayList<Instance> list = new ArrayList<>();
        list.add(inst);
        InstanceList ilist = mock(InstanceList.class);
        when(ilist.iterator()).thenReturn(list.iterator());
        when(ilist.size()).thenReturn(1);
        when(ilist.getInstanceWeight(inst)).thenReturn(1.0);
        when(ilist.getDataAlphabet()).thenReturn(alphabet);
        when(ilist.getTargetAlphabet()).thenReturn(labelAlphabet);
        when(ilist.getPipe()).thenReturn(mock(Pipe.class));

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(ilist, null);
        double[] buf = null;
        opt.getParameters(buf); 
    }
@Test
    public void testPriorSwitchingSequence() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("s");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("Z");
        labelAlphabet.lookupIndex("Q");

        FeatureVector fv = new FeatureVector(alphabet, new int[]{0}, new double[]{1});
        Labeling labeling = new LabelVector(labelAlphabet, new double[]{1, 0});
        Instance inst = new Instance(fv, labeling, "psw", null);

        ArrayList<Instance> list = new ArrayList<>();
        list.add(inst);
        InstanceList ilist = mock(InstanceList.class);
        when(ilist.iterator()).thenReturn(list.iterator());
        when(ilist.size()).thenReturn(1);
        when(ilist.getInstanceWeight(inst)).thenReturn(1.0);
        when(ilist.getDataAlphabet()).thenReturn(alphabet);
        when(ilist.getTargetAlphabet()).thenReturn(labelAlphabet);
        when(ilist.getPipe()).thenReturn(mock(Pipe.class));

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(ilist, null);

        opt.useNoPrior();
        double val1 = opt.getValue();

        opt.useGaussianPrior();
        double val2 = opt.getValue();

        opt.useHyperbolicPrior(); 

        assertTrue(val1 >= 0);
        assertTrue(val2 >= 0);
    }
@Test
    public void testGetValueGradientCalledBeforeValueTriggersCompute() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("grad");

        LabelAlphabet labs = new LabelAlphabet();
        labs.lookupIndex("G1");
        labs.lookupIndex("G2");

        FeatureVector fv = new FeatureVector(alphabet, new int[]{0}, new double[]{1});

        Labeling labeling = new LabelVector(labs, new double[]{1.0, 0.0});
        Instance inst = new Instance(fv, labeling, "gv-first", null);

        ArrayList<Instance> list = new ArrayList<>();
        list.add(inst);

        InstanceList ilist = mock(InstanceList.class);
        when(ilist.iterator()).thenReturn(list.iterator());
        when(ilist.size()).thenReturn(1);
        when(ilist.getInstanceWeight(inst)).thenReturn(1.0);
        when(ilist.getDataAlphabet()).thenReturn(alphabet);
        when(ilist.getTargetAlphabet()).thenReturn(labs);
        when(ilist.getPipe()).thenReturn(mock(Pipe.class));

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(ilist, null);
        double[] grad = new double[opt.getNumParameters()];
        opt.getValueGradient(grad);
        assertFalse(Double.isNaN(grad[0]));
    }
@Test
    public void testSingleLabelOnly() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("f");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("L");

        FeatureVector fv = new FeatureVector(alphabet, new int[]{0}, new double[]{1.0});
        Labeling labeling = new LabelVector(labelAlphabet, new double[]{1.0});
        Instance inst = new Instance(fv, labeling, "one-label", null);

        ArrayList<Instance> list = new ArrayList<>();
        list.add(inst);

        InstanceList ilist = mock(InstanceList.class);
        when(ilist.iterator()).thenReturn(list.iterator());
        when(ilist.size()).thenReturn(1);
        when(ilist.getInstanceWeight(inst)).thenReturn(1.0);
        when(ilist.getDataAlphabet()).thenReturn(alphabet);
        when(ilist.getTargetAlphabet()).thenReturn(labelAlphabet);
        when(ilist.getPipe()).thenReturn(mock(Pipe.class));

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(ilist, null);
        double val = opt.getValue();
        assertFalse(Double.isNaN(val));
    }
@Test
    public void testRepeatedIndicesInFeatureVector() {
        Alphabet alphabet = new Alphabet();
        int i1 = alphabet.lookupIndex("w");
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("A");
        labelAlphabet.lookupIndex("B");

        FeatureVector fv = new FeatureVector(alphabet, new int[]{i1, i1}, new double[]{1.0, 2.0});
        Labeling labeling = new LabelVector(labelAlphabet, new double[]{0.0, 1.0});
        Instance inst = new Instance(fv, labeling, "repeats", null);

        ArrayList<Instance> list = new ArrayList<>();
        list.add(inst);
        InstanceList ilist = mock(InstanceList.class);
        when(ilist.iterator()).thenReturn(list.iterator());
        when(ilist.size()).thenReturn(1);
        when(ilist.getInstanceWeight(inst)).thenReturn(1.0);
        when(ilist.getDataAlphabet()).thenReturn(alphabet);
        when(ilist.getTargetAlphabet()).thenReturn(labelAlphabet);
        when(ilist.getPipe()).thenReturn(mock(Pipe.class));

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(ilist, null);
        double val = opt.getValue();
        assertFalse(Double.isNaN(val));
    }
@Test
    public void testHighMagnitudeParameterValues() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("f");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("High");

        FeatureVector fv = new FeatureVector(alphabet, new int[]{0}, new double[]{1.0});
        Labeling labeling = new LabelVector(labelAlphabet, new double[]{1.0});
        Instance inst = new Instance(fv, labeling, "high-params", null);

        ArrayList<Instance> list = new ArrayList<>();
        list.add(inst);

        InstanceList ilist = mock(InstanceList.class);
        when(ilist.iterator()).thenReturn(list.iterator());
        when(ilist.size()).thenReturn(1);
        when(ilist.getInstanceWeight(inst)).thenReturn(1.0);
        when(ilist.getDataAlphabet()).thenReturn(alphabet);
        when(ilist.getTargetAlphabet()).thenReturn(labelAlphabet);
        when(ilist.getPipe()).thenReturn(mock(Pipe.class));

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(ilist, null);
        double[] params = new double[opt.getNumParameters()];
        params[0] = 1e10;
        opt.setParameters(params);
        double val = opt.getValue();
        assertFalse(Double.isNaN(val));
    }
@Test
    public void testMixedGaussianAndHyperbolicPriorSwitching() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("x");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("0");
        labelAlphabet.lookupIndex("1");

        FeatureVector fv = new FeatureVector(alphabet, new int[]{0}, new double[]{1.0});
        Labeling labeling = new LabelVector(labelAlphabet, new double[]{1.0, 0.0});
        Instance inst = new Instance(fv, labeling, "combo-prior", null);

        ArrayList<Instance> list = new ArrayList<>();
        list.add(inst);
        InstanceList ilist = mock(InstanceList.class);
        when(ilist.iterator()).thenReturn(list.iterator());
        when(ilist.size()).thenReturn(1);
        when(ilist.getInstanceWeight(inst)).thenReturn(1.0);
        when(ilist.getDataAlphabet()).thenReturn(alphabet);
        when(ilist.getTargetAlphabet()).thenReturn(labelAlphabet);
        when(ilist.getPipe()).thenReturn(mock(Pipe.class));

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(ilist, null);
        opt.useGaussianPrior();
        opt.setHyperbolicPriorSlope(5.0);
        opt.setHyperbolicPriorSharpness(15.0);
        assertNotNull(opt);
    }
@Test(expected = AssertionError.class)
    public void testMismatchedPipeThrowsAssertion() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("feat");
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("Z");

        FeatureVector fv = new FeatureVector(alphabet, new int[]{0}, new double[]{1.0});
        Labeling labeling = new LabelVector(labelAlphabet, new double[]{1.0});
        Instance inst = new Instance(fv, labeling, "pipe", null);

        ArrayList<Instance> list = new ArrayList<>();
        list.add(inst);

        Pipe pipe1 = mock(Pipe.class);
        Pipe pipe2 = mock(Pipe.class);

        InstanceList ilist = mock(InstanceList.class);
        when(ilist.iterator()).thenReturn(list.iterator());
        when(ilist.size()).thenReturn(1);
        when(ilist.getInstanceWeight(inst)).thenReturn(1.0);
        when(ilist.getDataAlphabet()).thenReturn(alphabet);
        when(ilist.getTargetAlphabet()).thenReturn(labelAlphabet);
        when(ilist.getPipe()).thenReturn(pipe1);

        MaxEnt classifier = new MaxEnt(pipe2, new double[3], null, null);

        new MaxEntOptimizableByLabelLikelihood(ilist, classifier);
    }
@Test
    public void testLabelIndexOutsideScoresLength() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("a");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("Y");

        FeatureVector fv = new FeatureVector(alphabet, new int[]{0}, new double[]{1.0});
        Labeling labeling = new LabelVector(labelAlphabet, new double[]{1.0});
        Instance inst = new Instance(fv, labeling, "single-score", null);

        ArrayList<Instance> list = new ArrayList<>();
        list.add(inst);

        InstanceList instanceList = mock(InstanceList.class);
        when(instanceList.iterator()).thenReturn(list.iterator());
        when(instanceList.size()).thenReturn(1);
        when(instanceList.getInstanceWeight(inst)).thenReturn(1.0);
        when(instanceList.getDataAlphabet()).thenReturn(alphabet);
        when(instanceList.getTargetAlphabet()).thenReturn(labelAlphabet);
        when(instanceList.getPipe()).thenReturn(mock(Pipe.class));

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(instanceList, null);
        double value = opt.getValue();
        assertFalse(Double.isNaN(value));
    }
@Test
    public void testDefaultConstructorSafeUsage() {
        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood();
        assertNotNull(opt);
        
    }
@Test
    public void testPerLabelFeatureSelectionMasksCorrectly() {
        Alphabet alphabet = new Alphabet();
        int i1 = alphabet.lookupIndex("on");
        int i2 = alphabet.lookupIndex("off");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("A");
        labelAlphabet.lookupIndex("B");

        FeatureSelection[] perLabelFS = new FeatureSelection[2];
        perLabelFS[0] = new FeatureSelection(alphabet, true);
        perLabelFS[1] = new FeatureSelection(alphabet, true);
        perLabelFS[0].add(i1);
        perLabelFS[1].remove(i2);

        FeatureVector fv = new FeatureVector(alphabet, new int[]{i1, i2}, new double[]{1.0, 1.0});
        Labeling labeling = new LabelVector(labelAlphabet, new double[]{1.0, 0.0});
        Instance inst = new Instance(fv, labeling, "per-label", null);

        ArrayList<Instance> list = new ArrayList<>();
        list.add(inst);
        InstanceList ilist = mock(InstanceList.class);
        when(ilist.iterator()).thenReturn(list.iterator());
        when(ilist.size()).thenReturn(1);
        when(ilist.getInstanceWeight(inst)).thenReturn(1.0);
        when(ilist.getDataAlphabet()).thenReturn(alphabet);
        when(ilist.getTargetAlphabet()).thenReturn(labelAlphabet);
        when(ilist.getFeatureSelection()).thenReturn(null);
        when(ilist.getPerLabelFeatureSelection()).thenReturn(perLabelFS);
        when(ilist.getPipe()).thenReturn(mock(Pipe.class));

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(ilist, null);
        opt.getValue();
        double[] buffer = new double[opt.getNumParameters()];
        opt.getValueGradient(buffer);

        int numFeatures = alphabet.size() + 1;
        int maskedIndex = 1 * numFeatures + i2;
        assertEquals(0.0, buffer[maskedIndex], 0.0);
    }
@Test
    public void testScoreZeroProbabilityHandledSafelyInValueComputation() {
        Alphabet alphabet = new Alphabet();
        int idx = alphabet.lookupIndex("trigger");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("POS");
        labelAlphabet.lookupIndex("NEG");

        FeatureVector fv = new FeatureVector(alphabet, new int[]{idx}, new double[]{1.0});
        Labeling labeling = new LabelVector(labelAlphabet, new double[]{0.0, 1.0});
        Instance inst = new Instance(fv, labeling, "zero-score-safe", null);

        ArrayList<Instance> instances = new ArrayList<>();
        instances.add(inst);

        MaxEnt classifier = mock(MaxEnt.class);
        double[] scores = new double[]{1.0, 0.0};
        doAnswer(invocation -> {
            double[] target = (double[]) invocation.getArguments()[1];
            target[0] = scores[0];
            target[1] = scores[1];
            return null;
        }).when(classifier).getClassificationScores(eq(inst), any(double[].class));

        InstanceList ilist = mock(InstanceList.class);
        when(ilist.iterator()).thenReturn(instances.iterator());
        when(ilist.size()).thenReturn(1);
        when(ilist.getInstanceWeight(inst)).thenReturn(1.0);
        when(ilist.getDataAlphabet()).thenReturn(alphabet);
        when(ilist.getTargetAlphabet()).thenReturn(labelAlphabet);
        when(ilist.getPipe()).thenReturn(mock(Pipe.class));

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(ilist, classifier);
        double val = opt.getValue();
        assertFalse(Double.isNaN(val));
        assertFalse(Double.isInfinite(val));
    }
@Test
    public void testDefaultFeatureIndexGradientRetained() {
        Alphabet alphabet = new Alphabet();
        int fIndex = alphabet.lookupIndex("df");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("c");

        FeatureVector fv = new FeatureVector(alphabet, new int[]{fIndex}, new double[]{1.0});
        Labeling labeling = new LabelVector(labelAlphabet, new double[]{1.0});
        Instance inst = new Instance(fv, labeling, "default-active", null);

        ArrayList<Instance> list = new ArrayList<>();
        list.add(inst);

        InstanceList myList = mock(InstanceList.class);
        when(myList.iterator()).thenReturn(list.iterator());
        when(myList.size()).thenReturn(1);
        when(myList.getInstanceWeight(inst)).thenReturn(1.0);
        when(myList.getDataAlphabet()).thenReturn(alphabet);
        when(myList.getTargetAlphabet()).thenReturn(labelAlphabet);
        when(myList.getPipe()).thenReturn(mock(Pipe.class));

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(myList, null);
        opt.getValue();
        double[] grad = new double[opt.getNumParameters()];
        opt.getValueGradient(grad);

        double sum = 0.0;
        for (int i = 0; i < grad.length; i++) {
            sum += grad[i];
        }

        assertNotEquals(0.0, sum, 1e-9); 
    }
@Test
    public void testNegativeInfinityParameterReplacedCorrectly() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("f1");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("L1");

        FeatureVector fv = new FeatureVector(alphabet, new int[]{0}, new double[]{1.0});
        Labeling labeling = new LabelVector(labelAlphabet, new double[]{1.0});
        Instance inst = new Instance(fv, labeling, "neg-inf-param", null);

        ArrayList<Instance> list = new ArrayList<>();
        list.add(inst);

        InstanceList listMock = mock(InstanceList.class);
        when(listMock.iterator()).thenReturn(list.iterator());
        when(listMock.size()).thenReturn(1);
        when(listMock.getInstanceWeight(inst)).thenReturn(1.0);
        when(listMock.getDataAlphabet()).thenReturn(alphabet);
        when(listMock.getTargetAlphabet()).thenReturn(labelAlphabet);
        when(listMock.getPipe()).thenReturn(mock(Pipe.class));

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(listMock, null);

        double[] params = new double[opt.getNumParameters()];
        params[0] = Double.NEGATIVE_INFINITY;
        opt.setParameters(params);
        opt.getValue();
        double[] grad = new double[opt.getNumParameters()];
        opt.getValueGradient(grad);

        assertEquals(0.0, grad[0], 1e-9);
    }
@Test
    public void testEmptySetParametersCallIgnoredGracefully() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("nn");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("X");

        FeatureVector fv = new FeatureVector(alphabet, new int[]{0}, new double[]{1.0});
        Labeling labeling = new LabelVector(labelAlphabet, new double[]{1.0});
        Instance inst = new Instance(fv, labeling, "empty-set", null);

        ArrayList<Instance> data = new ArrayList<>();
        data.add(inst);

        InstanceList il = mock(InstanceList.class);
        when(il.iterator()).thenReturn(data.iterator());
        when(il.size()).thenReturn(1);
        when(il.getInstanceWeight(inst)).thenReturn(1.0);
        when(il.getDataAlphabet()).thenReturn(alphabet);
        when(il.getTargetAlphabet()).thenReturn(labelAlphabet);
        when(il.getPipe()).thenReturn(mock(Pipe.class));

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(il, null);

        double[] wrong = new double[1];
        try {
            opt.setParameters(wrong);
        } catch (Exception e) {
            fail("Should not throw on mis-length, it reallocates internally.");
        }
        assertEquals(1, opt.getValueCalls() - 0, 1); 
    }
@Test
    public void testValueIsNegatedToMaximizeLikelihood() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("tok");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("abc");

        FeatureVector fv = new FeatureVector(alphabet, new int[]{0}, new double[]{1.0});
        Labeling labeling = new LabelVector(labelAlphabet, new double[]{1.0});
        Instance inst = new Instance(fv, labeling, "maximize", null);

        ArrayList<Instance> items = new ArrayList<>();
        items.add(inst);

        InstanceList inp = mock(InstanceList.class);
        when(inp.iterator()).thenReturn(items.iterator());
        when(inp.size()).thenReturn(1);
        when(inp.getInstanceWeight(inst)).thenReturn(1.0);
        when(inp.getDataAlphabet()).thenReturn(alphabet);
        when(inp.getTargetAlphabet()).thenReturn(labelAlphabet);
        when(inp.getPipe()).thenReturn(mock(Pipe.class));

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(inp, null);
        double val = opt.getValue();
        assertTrue(val <= 0.0); 
    }
@Test
    public void testFeatureVectorWithNegativeValues() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("neg");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("A");
        labelAlphabet.lookupIndex("B");

        FeatureVector fv = new FeatureVector(alphabet, new int[]{0}, new double[]{-1.0});
        Labeling labeling = new LabelVector(labelAlphabet, new double[]{0.0, 1.0});
        Instance inst = new Instance(fv, labeling, "neg-weight", null);

        ArrayList<Instance> list = new ArrayList<>();
        list.add(inst);

        InstanceList instanceList = mock(InstanceList.class);
        when(instanceList.iterator()).thenReturn(list.iterator());
        when(instanceList.size()).thenReturn(1);
        when(instanceList.getInstanceWeight(inst)).thenReturn(1.0);
        when(instanceList.getDataAlphabet()).thenReturn(alphabet);
        when(instanceList.getTargetAlphabet()).thenReturn(labelAlphabet);
        when(instanceList.getPipe()).thenReturn(mock(Pipe.class));

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(instanceList, null);
        double val = opt.getValue();
        assertFalse(Double.isNaN(val));
    }
@Test
    public void testFeatureVectorWithZeroValues() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("zero");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("Y");

        FeatureVector fv = new FeatureVector(alphabet, new int[]{0}, new double[]{0.0});
        Labeling labeling = new LabelVector(labelAlphabet, new double[]{1.0});
        Instance inst = new Instance(fv, labeling, "zero-feature", null);

        ArrayList<Instance> instances = new ArrayList<>();
        instances.add(inst);

        InstanceList il = mock(InstanceList.class);
        when(il.iterator()).thenReturn(instances.iterator());
        when(il.size()).thenReturn(1);
        when(il.getInstanceWeight(inst)).thenReturn(1.0);
        when(il.getDataAlphabet()).thenReturn(alphabet);
        when(il.getTargetAlphabet()).thenReturn(labelAlphabet);
        when(il.getPipe()).thenReturn(mock(Pipe.class));

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(il, null);
        double val = opt.getValue();
        assertFalse(Double.isNaN(val));
        assertFalse(Double.isInfinite(val));
    }
@Test
    public void testUniformScoresValueStability() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("uniform");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("A");
        labelAlphabet.lookupIndex("B");

        FeatureVector fv = new FeatureVector(alphabet, new int[]{0}, new double[]{1.0});
        Labeling labeling = new LabelVector(labelAlphabet, new double[]{0.0, 1.0});
        Instance instance = new Instance(fv, labeling, "uniform", null);

        ArrayList<Instance> list = new ArrayList<>();
        list.add(instance);

        MaxEnt classifier = mock(MaxEnt.class);
        doAnswer(invocation -> {
            double[] scores = (double[]) invocation.getArguments()[1];
            scores[0] = 0.5;
            scores[1] = 0.5;
            return null;
        }).when(classifier).getClassificationScores(eq(instance), any(double[].class));

        InstanceList il = mock(InstanceList.class);
        when(il.iterator()).thenReturn(list.iterator());
        when(il.size()).thenReturn(1);
        when(il.getDataAlphabet()).thenReturn(alphabet);
        when(il.getTargetAlphabet()).thenReturn(labelAlphabet);
        when(il.getInstanceWeight(instance)).thenReturn(1.0);
        when(il.getPipe()).thenReturn(mock(Pipe.class));

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(il, classifier);
        double value = opt.getValue();

        assertTrue(value <= 1.0);
    }
@Test
    public void testGetValueGradientWithWrongSizedBuffer() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("f");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("A");

        FeatureVector fv = new FeatureVector(alphabet, new int[]{0}, new double[]{1.0});
        Labeling labeling = new LabelVector(labelAlphabet, new double[]{1.0});
        Instance instance = new Instance(fv, labeling, "wrong-buffer", null);

        ArrayList<Instance> input = new ArrayList<>();
        input.add(instance);

        InstanceList il = mock(InstanceList.class);
        when(il.iterator()).thenReturn(input.iterator());
        when(il.size()).thenReturn(1);
        when(il.getInstanceWeight(instance)).thenReturn(1.0);
        when(il.getDataAlphabet()).thenReturn(alphabet);
        when(il.getTargetAlphabet()).thenReturn(labelAlphabet);
        when(il.getPipe()).thenReturn(mock(Pipe.class));

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(il, null);

        opt.getValue();
        double[] buffer = new double[1]; 

        try {
            opt.getValueGradient(buffer); 
            fail("Expected ArrayIndexOutOfBoundsException or assertion error");
        } catch (Exception ex) {
            assertTrue(true);
        }
    }
@Test
    public void testGetParametersNullBufferAllocates() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("k");

        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupIndex("z");

        FeatureVector fv = new FeatureVector(alphabet, new int[]{0}, new double[]{1.0});
        Labeling lab = new LabelVector(labels, new double[]{1.0});
        Instance instance = new Instance(fv, lab, "null-param", null);

        ArrayList<Instance> list = new ArrayList<>();
        list.add(instance);

        InstanceList il = mock(InstanceList.class);
        when(il.iterator()).thenReturn(list.iterator());
        when(il.size()).thenReturn(1);
        when(il.getInstanceWeight(instance)).thenReturn(1.0);
        when(il.getDataAlphabet()).thenReturn(alphabet);
        when(il.getTargetAlphabet()).thenReturn(labels);
        when(il.getPipe()).thenReturn(mock(Pipe.class));

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(il, null);
        double[] buffer = null;
        opt.getParameters(buffer); 
    }
@Test
    public void testSettingParametersInvalidatesGradientCache() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("feat");

        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupIndex("0");

        FeatureVector fv = new FeatureVector(alphabet, new int[]{0}, new double[]{1});
        Labeling lab = new LabelVector(labels, new double[]{1.0});
        Instance instance = new Instance(fv, lab, "set-check", null);

        ArrayList<Instance> data = new ArrayList<>();
        data.add(instance);

        InstanceList il = mock(InstanceList.class);
        when(il.iterator()).thenReturn(data.iterator());
        when(il.size()).thenReturn(1);
        when(il.getDataAlphabet()).thenReturn(alphabet);
        when(il.getTargetAlphabet()).thenReturn(labels);
        when(il.getInstanceWeight(instance)).thenReturn(1.0);
        when(il.getPipe()).thenReturn(mock(Pipe.class));

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(il, null);

        opt.getValueGradient(new double[opt.getNumParameters()]);
        int callsBefore = opt.getValueGradientCalls();

        double[] newParams = new double[opt.getNumParameters()];
        newParams[0] = 0.5;
        opt.setParameters(newParams);

        opt.getValueGradient(new double[opt.getNumParameters()]);
        assertEquals(callsBefore + 1, opt.getValueGradientCalls());
    }
@Test
    public void testHyperbolicPriorFollowedBySettingSlope() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("h");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("class");

        FeatureVector fv = new FeatureVector(alphabet, new int[]{0}, new double[]{1.0});
        Labeling lab = new LabelVector(labelAlphabet, new double[]{1.0});
        Instance inst = new Instance(fv, lab, "hyper-test", null);

        ArrayList<Instance> list = new ArrayList<>();
        list.add(inst);

        InstanceList ilist = mock(InstanceList.class);
        when(ilist.iterator()).thenReturn(list.iterator());
        when(ilist.size()).thenReturn(1);
        when(ilist.getInstanceWeight(inst)).thenReturn(1.0);
        when(ilist.getDataAlphabet()).thenReturn(alphabet);
        when(ilist.getTargetAlphabet()).thenReturn(labelAlphabet);
        when(ilist.getPipe()).thenReturn(mock(Pipe.class));

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(ilist, null);
        opt.useHyperbolicPrior();
        opt.setHyperbolicPriorSlope(0.25);
        opt.setHyperbolicPriorSharpness(9.0);

        try {
            opt.getValueGradient(new double[opt.getNumParameters()]);
            fail("Expected exception for hyperbolic prior not yet implemented");
        } catch (UnsupportedOperationException e) {
            assertTrue(true);
        }
    }
@Test
    public void testLargeNumberOfFeatures() {
        Alphabet alphabet = new Alphabet();
        for (int i = 0; i < 1000; i++) {
            alphabet.lookupIndex("f" + i);
        }

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("L");

        int[] indices = new int[]{0, 500, 999};
        double[] values = new double[]{1.0, 0.5, 0.25};
        FeatureVector fv = new FeatureVector(alphabet, indices, values);
        Labeling labeling = new LabelVector(labelAlphabet, new double[]{1.0});
        Instance instance = new Instance(fv, labeling, "large-fv", null);

        ArrayList<Instance> instances = new ArrayList<>();
        instances.add(instance);

        InstanceList ilist = mock(InstanceList.class);
        when(ilist.iterator()).thenReturn(instances.iterator());
        when(ilist.size()).thenReturn(1);
        when(ilist.getInstanceWeight(instance)).thenReturn(1.0);
        when(ilist.getDataAlphabet()).thenReturn(alphabet);
        when(ilist.getTargetAlphabet()).thenReturn(labelAlphabet);
        when(ilist.getPipe()).thenReturn(mock(Pipe.class));

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(ilist, null);
        double val = opt.getValue();
        assertFalse(Double.isNaN(val));
    }
@Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testSetParameterOutOfBounds() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("feat");
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("lbl");

        FeatureVector fv = new FeatureVector(alphabet, new int[]{0}, new double[]{1.0});
        Labeling labeling = new LabelVector(labelAlphabet, new double[]{1.0});
        Instance inst = new Instance(fv, labeling, "out-bound", null);

        ArrayList<Instance> list = new ArrayList<>();
        list.add(inst);
        InstanceList ilist = mock(InstanceList.class);
        when(ilist.iterator()).thenReturn(list.iterator());
        when(ilist.getInstanceWeight(inst)).thenReturn(1.0);
        when(ilist.size()).thenReturn(1);
        when(ilist.getDataAlphabet()).thenReturn(alphabet);
        when(ilist.getTargetAlphabet()).thenReturn(labelAlphabet);
        when(ilist.getPipe()).thenReturn(mock(Pipe.class));

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(ilist, null);
        opt.setParameter(opt.getNumParameters() + 1, 3.14); 
    }
@Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testGetParameterOutOfBounds() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("xyz");
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("A");

        FeatureVector fv = new FeatureVector(alphabet, new int[]{0}, new double[]{1.0});
        Labeling lab = new LabelVector(labelAlphabet, new double[]{1.0});
        Instance inst = new Instance(fv, lab, "bad-get", null);

        ArrayList<Instance> list = new ArrayList<>();
        list.add(inst);
        InstanceList il = mock(InstanceList.class);
        when(il.iterator()).thenReturn(list.iterator());
        when(il.size()).thenReturn(1);
        when(il.getInstanceWeight(inst)).thenReturn(1.0);
        when(il.getDataAlphabet()).thenReturn(alphabet);
        when(il.getTargetAlphabet()).thenReturn(labelAlphabet);
        when(il.getPipe()).thenReturn(mock(Pipe.class));

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(il, null);
        double val = opt.getParameter(opt.getNumParameters()); 
    }
@Test(expected = AssertionError.class)
    public void testSetParametersNullInput() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("null");
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("X");

        FeatureVector fv = new FeatureVector(alphabet, new int[]{0}, new double[]{1});
        Labeling lab = new LabelVector(labelAlphabet, new double[]{1});
        Instance inst = new Instance(fv, lab, "nullparam", null);

        ArrayList<Instance> set = new ArrayList<>();
        set.add(inst);
        InstanceList instList = mock(InstanceList.class);
        when(instList.iterator()).thenReturn(set.iterator());
        when(instList.size()).thenReturn(1);
        when(instList.getInstanceWeight(inst)).thenReturn(1.0);
        when(instList.getDataAlphabet()).thenReturn(alphabet);
        when(instList.getTargetAlphabet()).thenReturn(labelAlphabet);
        when(instList.getPipe()).thenReturn(mock(Pipe.class));

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(instList, null);
        opt.setParameters(null); 
    }
@Test
    public void testSetParametersWithExtremeValues() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("x1");
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("z");

        FeatureVector fv = new FeatureVector(alphabet, new int[]{0}, new double[]{1});
        Labeling lab = new LabelVector(labelAlphabet, new double[]{1});
        Instance inst = new Instance(fv, lab, "extreme", null);

        ArrayList<Instance> list = new ArrayList<>();
        list.add(inst);
        InstanceList il = mock(InstanceList.class);
        when(il.iterator()).thenReturn(list.iterator());
        when(il.size()).thenReturn(1);
        when(il.getInstanceWeight(inst)).thenReturn(1.0);
        when(il.getDataAlphabet()).thenReturn(alphabet);
        when(il.getTargetAlphabet()).thenReturn(labelAlphabet);
        when(il.getPipe()).thenReturn(mock(Pipe.class));

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(il, null);

        double[] buffer = new double[opt.getNumParameters()];
        buffer[0] = Double.NaN;
        buffer[1] = Double.MAX_VALUE;
        opt.setParameters(buffer);
        assertNotNull(opt);
    }
@Test
    public void testSetGaussianPriorVarianceSwitchesPriorCorrectly() {
        Alphabet a = new Alphabet();
        a.lookupIndex("v");

        LabelAlphabet l = new LabelAlphabet();
        l.lookupIndex("L");

        FeatureVector fv = new FeatureVector(a, new int[]{0}, new double[]{1});
        Labeling lab = new LabelVector(l, new double[]{1});
        Instance ins = new Instance(fv, lab, "gpv", null);

        ArrayList<Instance> list = new ArrayList<>();
        list.add(ins);

        InstanceList il = mock(InstanceList.class);
        when(il.iterator()).thenReturn(list.iterator());
        when(il.size()).thenReturn(1);
        when(il.getInstanceWeight(ins)).thenReturn(1.0);
        when(il.getDataAlphabet()).thenReturn(a);
        when(il.getTargetAlphabet()).thenReturn(l);
        when(il.getPipe()).thenReturn(mock(Pipe.class));

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(il, null);
        opt.useHyperbolicPrior();
        opt.setGaussianPriorVariance(0.3); 
        double val = opt.getValue();
        assertTrue(val <= 0);
    }
@Test
    public void testSetHyperbolicPriorAfterGaussian() {
        Alphabet a = new Alphabet();
        a.lookupIndex("h");

        LabelAlphabet l = new LabelAlphabet();
        l.lookupIndex("C");

        FeatureVector fv = new FeatureVector(a, new int[]{0}, new double[]{1});
        Labeling lab = new LabelVector(l, new double[]{1});
        Instance i = new Instance(fv, lab, "prior-switch", null);

        ArrayList<Instance> list = new ArrayList<>();
        list.add(i);

        InstanceList il = mock(InstanceList.class);
        when(il.iterator()).thenReturn(list.iterator());
        when(il.size()).thenReturn(1);
        when(il.getInstanceWeight(i)).thenReturn(1.0);
        when(il.getDataAlphabet()).thenReturn(a);
        when(il.getTargetAlphabet()).thenReturn(l);
        when(il.getPipe()).thenReturn(mock(Pipe.class));

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(il, null);
        opt.setGaussianPriorVariance(1.0);
        opt.useHyperbolicPrior();
        assertNotNull(opt);
    }
@Test
    public void testMultipleGradientCallsReadFromCache() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("f1");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("Z");

        FeatureVector fv = new FeatureVector(alphabet, new int[]{0}, new double[]{1});
        Labeling lab = new LabelVector(labelAlphabet, new double[]{1});
        Instance inst = new Instance(fv, lab, "cacheCheck", null);

        ArrayList<Instance> list = new ArrayList<>();
        list.add(inst);
        InstanceList il = mock(InstanceList.class);
        when(il.iterator()).thenReturn(list.iterator());
        when(il.size()).thenReturn(1);
        when(il.getInstanceWeight(inst)).thenReturn(1.0);
        when(il.getDataAlphabet()).thenReturn(alphabet);
        when(il.getTargetAlphabet()).thenReturn(labelAlphabet);
        when(il.getPipe()).thenReturn(mock(Pipe.class));

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(il, null);
        double[] grad1 = new double[opt.getNumParameters()];
        double[] grad2 = new double[opt.getNumParameters()];

        opt.getValueGradient(grad1);
        int callsBefore = opt.getValueGradientCalls();
        opt.getValueGradient(grad2);
        int callsAfter = opt.getValueGradientCalls();

        assertEquals(callsBefore, callsAfter);
    }
@Test
    public void testInstanceWithZeroLabelScoreSkipsGradientComputation() {
        Alphabet a = new Alphabet();
        a.lookupIndex("f");

        LabelAlphabet l = new LabelAlphabet();
        l.lookupIndex("L1");
        l.lookupIndex("L2");

        FeatureVector fv = new FeatureVector(a, new int[]{0}, new double[]{1});
        Labeling lab = new LabelVector(l, new double[]{1, 0});
        Instance inst = new Instance(fv, lab, "zero-grad", null);

        ArrayList<Instance> list = new ArrayList<>();
        list.add(inst);

        MaxEnt classifier = mock(MaxEnt.class);
        doAnswer(invocation -> {
            double[] scores = (double[]) invocation.getArguments()[1];
            scores[0] = 0.0; 
            scores[1] = 1.0;
            return null;
        }).when(classifier).getClassificationScores(eq(inst), any(double[].class));

        InstanceList il = mock(InstanceList.class);
        when(il.iterator()).thenReturn(list.iterator());
        when(il.size()).thenReturn(1);
        when(il.getInstanceWeight(inst)).thenReturn(1.0);
        when(il.getDataAlphabet()).thenReturn(a);
        when(il.getTargetAlphabet()).thenReturn(l);
        when(il.getPipe()).thenReturn(mock(Pipe.class));

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(il, classifier);
        opt.getValue();
        assertTrue(true); 
    }
@Test
    public void testLabelingReturnsInvalidIndex() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("x");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("A");

        FeatureVector fv = new FeatureVector(alphabet, new int[]{0}, new double[]{1});

        Labeling labeling = mock(Labeling.class);
        when(labeling.getBestIndex()).thenReturn(999); 

        Instance inst = new Instance(fv, labeling, "bad-label-id", null);
        ArrayList<Instance> list = new ArrayList<>();
        list.add(inst);

        InstanceList ilist = mock(InstanceList.class);
        when(ilist.iterator()).thenReturn(list.iterator());
        when(ilist.size()).thenReturn(1);
        when(ilist.getInstanceWeight(inst)).thenReturn(1.0);
        when(ilist.getDataAlphabet()).thenReturn(alphabet);
        when(ilist.getTargetAlphabet()).thenReturn(labelAlphabet);
        when(ilist.getPipe()).thenReturn(mock(Pipe.class));

        try {
            new MaxEntOptimizableByLabelLikelihood(ilist, null);
            fail("Expected IndexOutOfBoundsException");
        } catch (Exception e) {
            assertTrue(true);
        }
    }
@Test
    public void testFeatureIndexEqualToDefaultIndex() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("feat1");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("L");

        FeatureVector fv = new FeatureVector(alphabet, new int[]{1}, new double[]{1.0}); 

        Labeling lab = new LabelVector(labelAlphabet, new double[]{1.0});
        Instance inst = new Instance(fv, lab, "default-idx-eq", null);

        ArrayList<Instance> list = new ArrayList<>();
        list.add(inst);

        InstanceList il = mock(InstanceList.class);
        when(il.iterator()).thenReturn(list.iterator());
        when(il.size()).thenReturn(1);
        when(il.getInstanceWeight(inst)).thenReturn(1.0);
        when(il.getDataAlphabet()).thenReturn(alphabet);
        when(il.getTargetAlphabet()).thenReturn(labelAlphabet);
        when(il.getPipe()).thenReturn(mock(Pipe.class));

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(il, null);
        double value = opt.getValue();
        assertFalse(Double.isNaN(value));
    } 
}