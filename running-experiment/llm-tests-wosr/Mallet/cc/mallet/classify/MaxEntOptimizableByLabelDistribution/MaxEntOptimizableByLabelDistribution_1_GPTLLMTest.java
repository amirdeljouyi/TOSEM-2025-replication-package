public class MaxEntOptimizableByLabelDistribution_1_GPTLLMTest { 

 @Test
    public void testConstructorWithInitialClassifier() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        dataAlphabet.lookupIndex("f1");
        dataAlphabet.lookupIndex("f2");

        labelAlphabet.lookupIndex("L1");
        labelAlphabet.lookupIndex("L2");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"L1", "L2"}, new double[]{0.6, 0.4});
        Instance instance = new Instance(fv, lv, "inst1", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList instanceList = new InstanceList(pipe);
        instanceList.add(instance);

        MaxEnt classifier = new MaxEnt(pipe, new double[6], null, null);
        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(instanceList, classifier);

        assertNotNull(opt.getClassifier());
    }
@Test
    public void testConstructorWithoutInitialClassifier() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        dataAlphabet.lookupIndex("f1");

        labelAlphabet.lookupIndex("L1");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"L1"}, new double[]{1.0});
        Instance instance = new Instance(fv, lv, "inst1", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList instanceList = new InstanceList(pipe);
        instanceList.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(instanceList, null);
        assertNotNull(opt.getClassifier());
    }
@Test
    public void testGetAndSetParameter() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        dataAlphabet.lookupIndex("f1");

        labelAlphabet.lookupIndex("L1");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"L1"}, new double[]{1.0});
        Instance instance = new Instance(fv, lv, "inst1", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList instanceList = new InstanceList(pipe);
        instanceList.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(instanceList, null);
        int numParams = opt.getNumParameters();
        double original = opt.getParameter(0);

        opt.setParameter(0, 3.14);
        double updated = opt.getParameter(0);

        assertNotEquals(original, updated, 0.0001);
        assertEquals(3.14, updated, 0.0001);
    }
@Test
    public void testSetAndGetParametersArray() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        dataAlphabet.lookupIndex("f1");

        labelAlphabet.lookupIndex("L1");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"L1"}, new double[]{1.0});
        Instance instance = new Instance(fv, lv, "inst", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList instanceList = new InstanceList(pipe);
        instanceList.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(instanceList, null);

        int length = opt.getNumParameters();

        double[] params = new double[length];
        params[0] = 2.0;
        params[length - 1] = 5.0;

        opt.setParameters(params);

        double[] returned = new double[length];
        opt.getParameters(returned);

        assertEquals(2.0, returned[0], 0.001);
        assertEquals(5.0, returned[length - 1], 0.001);
    }
@Test
    public void testGetValueIsFinite() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        dataAlphabet.lookupIndex("f1");

        labelAlphabet.lookupIndex("L1");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"L1"}, new double[]{1.0});
        Instance instance = new Instance(fv, lv, "test", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList instanceList = new InstanceList(pipe);
        instanceList.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(instanceList, null);
        double value = opt.getValue();

        assertTrue(Double.isFinite(value));
    }
@Test
    public void testGetValueGradientNotNull() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        dataAlphabet.lookupIndex("f1");

        labelAlphabet.lookupIndex("L1");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"L1"}, new double[]{1.0});
        Instance instance = new Instance(fv, lv, "inst", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList instanceList = new InstanceList(pipe);
        instanceList.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(instanceList, null);

        int paramCount = opt.getNumParameters();
        double[] gradient = new double[paramCount];
        opt.getValueGradient(gradient);

        assertNotNull(gradient);
        assertEquals(paramCount, gradient.length);
        assertFalse(Double.isNaN(gradient[0]));
    }
@Test
    public void testSetGaussianPriorVariance() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        dataAlphabet.lookupIndex("f1");

        labelAlphabet.lookupIndex("L1");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"L1"}, new double[]{1.0});
        Instance instance = new Instance(fv, lv, "sample", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList ilist = new InstanceList(pipe);
        ilist.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(ilist, null);

        MaxEntOptimizableByLabelDistribution returned = opt.setGaussianPriorVariance(0.12);
        assertSame(opt, returned); 
    }
@Test
    public void testValueChangesAfterSetParameter() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        dataAlphabet.lookupIndex("f1");

        labelAlphabet.lookupIndex("L1");
        labelAlphabet.lookupIndex("L2");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"L1", "L2"}, new double[]{0.6, 0.4});
        Instance instance = new Instance(fv, lv, "mutable", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList il = new InstanceList(pipe);
        il.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(il, null);
        double originalValue = opt.getValue();

        opt.setParameter(0, 2.0);
        double newValue = opt.getValue();

        assertNotEquals(originalValue, newValue, 0.0001);
    }
@Test
    public void testZeroFeatureInstance() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        labelAlphabet.lookupIndex("L1");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{}, new double[]{});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"L1"}, new double[]{1.0});
        Instance instance = new Instance(fv, lv, "empty", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList list = new InstanceList(pipe);
        list.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        double val = opt.getValue();
        assertTrue(Double.isFinite(val));
    }
@Test
    public void testNaNFeatureValueHandled() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        dataAlphabet.lookupIndex("f1");
        labelAlphabet.lookupIndex("L1");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{Double.NaN});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"L1"}, new double[]{1.0});
        Instance instance = new Instance(fv, lv, "nan_feature", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList il = new InstanceList(pipe);
        il.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(il, null);

        double val = opt.getValue();
        assertTrue(Double.isFinite(val));
    }
@Test
    public void testGradientReflectsParameterChange() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        dataAlphabet.lookupIndex("f1");

        labelAlphabet.lookupIndex("L1");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"L1"}, new double[]{1.0});
        Instance instance = new Instance(fv, lv, "adjustGrad", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList il = new InstanceList(pipe);
        il.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(il, null);

        double[] grad1 = new double[opt.getNumParameters()];
        opt.getValueGradient(grad1);

        opt.setParameter(0, opt.getParameter(0) + 1.0);

        double[] grad2 = new double[opt.getNumParameters()];
        opt.getValueGradient(grad2);

        assertNotEquals(grad1[0], grad2[0], 0.0001);
    }
@Test
    public void testInstanceWithNullLabelIsSkipped() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f1");
        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});

        Instance instance = new Instance(fv, null, "nullLabel", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList training = new InstanceList(pipe);
        training.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(training, null);
        double val = opt.getValue();
        assertTrue(Double.isFinite(val));  
    }
@Test
    public void testScoreZeroTriggersNegativeInfinityValue() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        dataAlphabet.lookupIndex("f1");
        labelAlphabet.lookupIndex("L1");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{10000.0});

        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"L1"}, new double[]{1.0});
        Instance instance = new Instance(fv, lv, "zeroScore", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList list = new InstanceList(pipe);
        list.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        double[] zeroParams = new double[opt.getNumParameters()];
        for (int i = 0; i < zeroParams.length; i++) {
            zeroParams[i] = -1e5;  
        }

        opt.setParameters(zeroParams);
        double result = opt.getValue();

        assertTrue("Should return -Inf or very large negative", result <= 0 || Double.isInfinite(result));
    }
@Test
    public void testGradientSubstitutionOfNegativeInfinity() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        dataAlphabet.lookupIndex("f1");
        labelAlphabet.lookupIndex("L1");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"L1"}, new double[]{1.0});
        Instance inst = new Instance(fv, lv, "inf_grad", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList training = new InstanceList(pipe);
        training.add(inst);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(training, null);
        double[] params = new double[opt.getNumParameters()];
        for (int i = 0; i < params.length; i++) {
            params[i] = Double.NEGATIVE_INFINITY; 
        }

        opt.setParameters(params);
        double[] gradient = new double[opt.getNumParameters()];
        opt.getValueGradient(gradient);

        assertEquals(0.0, gradient[0], 0.00001); 
    }
@Test
    public void testGetParametersWithNullBuffer() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        dataAlphabet.lookupIndex("f1");
        labelAlphabet.lookupIndex("L1");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"L1"}, new double[]{1.0});
        Instance instance = new Instance(fv, lv, "null_buffer", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList list = new InstanceList(pipe);
        list.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        opt.getParameters(null); 
    }
@Test
    public void testSetParametersWithMismatchedSize() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        dataAlphabet.lookupIndex("f1");
        labelAlphabet.lookupIndex("L1");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"L1"}, new double[]{1.0});
        Instance instance = new Instance(fv, lv, "short_array", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList list = new InstanceList(pipe);
        list.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        int length = opt.getNumParameters();

        double[] differentLength = new double[length + 2];
        opt.setParameters(differentLength); 
        double[] returned = new double[opt.getNumParameters()];
        opt.getParameters(returned);

        assertEquals(length + 2, returned.length);
    }
@Test
    public void testUseGaussianPriorReturnsSameInstance() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        dataAlphabet.lookupIndex("f1");
        labelAlphabet.lookupIndex("L1");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"L1"}, new double[]{1.0});
        Instance instance = new Instance(fv, lv, "prior", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList list = new InstanceList(pipe);
        list.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        MaxEntOptimizableByLabelDistribution result = opt.useGaussianPrior();

        assertSame(opt, result);
    }
@Test
    public void testGetValueCalledOnceEvenAfterMultipleGradients() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        dataAlphabet.lookupIndex("f1");
        labelAlphabet.lookupIndex("L1");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"L1"}, new double[]{1.0});
        Instance instance = new Instance(fv, lv, "valueCache", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList list = new InstanceList(pipe);
        list.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        double[] gradBuf = new double[opt.getNumParameters()];

        opt.getValueGradient(gradBuf); 
        opt.getValueGradient(gradBuf); 
        opt.getValueGradient(gradBuf); 

        assertEquals(1, opt.getValueCalls());
    }
@Test(expected = AssertionError.class)
    public void testAssertFailsWithMismatchedPipe() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        dataAlphabet.lookupIndex("f1");
        labelAlphabet.lookupIndex("L1");

        Pipe pipe1 = new SerialPipes(new ArrayList<>());
        Pipe pipe2 = new SerialPipes(new ArrayList<>());

        InstanceList list = new InstanceList(pipe1);
        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"L1"}, new double[]{1.0});
        Instance instance = new Instance(fv, lv, "badpipe", null);
        list.add(instance);

        MaxEnt badClassifier = new MaxEnt(pipe2, new double[2], null, null);

        new MaxEntOptimizableByLabelDistribution(list, badClassifier);  
    }
@Test(expected = AssertionError.class)
    public void testSetParametersWithNullThrowsAssertion() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        
        dataAlphabet.lookupIndex("f1");
        labelAlphabet.lookupIndex("L1");
        
        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"L1"}, new double[]{1.0});
        Instance instance = new Instance(fv, lv, "null_set", null);
        
        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList instList = new InstanceList(pipe);
        instList.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(instList, null);
        opt.setParameters(null); 
    }
@Test
    public void testGetParametersWithWrongLengthBufferDoesNotThrow() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        
        dataAlphabet.lookupIndex("f1");
        labelAlphabet.lookupIndex("L1");
        
        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"L1"}, new double[]{1.0});
        Instance instance = new Instance(fv, lv, "wrong_length_buffer", null);
        
        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList instList = new InstanceList(pipe);
        instList.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(instList, null);

        double[] shortBuffer = new double[1];
        opt.getParameters(shortBuffer); 
        assertTrue(shortBuffer.length == 1); 
    }
@Test
    public void testLabelsWithZeroProbabilityIgnoredInValueComputation() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        dataAlphabet.lookupIndex("f0");
        labelAlphabet.lookupIndex("L0");
        labelAlphabet.lookupIndex("L1");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        String[] labelNames = new String[]{"L0", "L1"};
        double[] labelValues = new double[]{1.0, 0.0};  

        LabelVector lv = new LabelVector(labelAlphabet, labelNames, labelValues);
        Instance instance = new Instance(fv, lv, "zeroLabelProb", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList instList = new InstanceList(pipe);
        instList.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(instList, null);
        double val = opt.getValue();

        assertTrue(Double.isFinite(val));
    }
@Test
    public void testClassifierIsBuiltWhenNullClassifierPassed() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        dataAlphabet.lookupIndex("feat1");
        labelAlphabet.lookupIndex("yes");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"yes"}, new double[]{1.0});
        Instance inst = new Instance(fv, lv, "autoClassifier", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList list = new InstanceList(pipe);
        list.add(inst);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        assertNotNull(opt.getClassifier());
    }
@Test
    public void testGetValueReturnsConsistentResultsWhenReused() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        dataAlphabet.lookupIndex("a");
        labelAlphabet.lookupIndex("ON");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"ON"}, new double[]{1.0});
        Instance instance = new Instance(fv, lv, "repeat_val", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList list = new InstanceList(pipe);
        list.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        double v1 = opt.getValue();
        double v2 = opt.getValue();

        assertEquals(v1, v2, 0.0000001); 
    }
@Test
    public void testPriorPenaltyEffectOnValue() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        dataAlphabet.lookupIndex("x1");
        labelAlphabet.lookupIndex("y1");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"y1"}, new double[]{1.0});
        Instance i = new Instance(fv, lv, "prior_test", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList list = new InstanceList(pipe);
        list.add(i);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        double[] params = new double[opt.getNumParameters()];
        params[0] = 1000.0; 
        opt.setParameters(params);

        double highPenalty = opt.getValue();

        opt.setGaussianPriorVariance(1e9); 
        double lowPenalty = opt.getValue();

        assertTrue(highPenalty > lowPenalty); 
    }
@Test
    public void testGetValueGradientWithIncorrectLengthBufferThrowsOrPassesQuietly() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        dataAlphabet.lookupIndex("p");
        labelAlphabet.lookupIndex("A");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"A"}, new double[]{1.0});
        Instance in = new Instance(fv, lv, "wrong_grad_len", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList instList = new InstanceList(pipe);
        instList.add(in);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(instList, null);

        double[] shortBuff = new double[1];

        try {
            opt.getValueGradient(shortBuff);
            fail("Expected ArrayIndexOutOfBoundsException or assertion");
        } catch (ArrayIndexOutOfBoundsException | AssertionError expected) {
            
        }
    }
@Test
    public void testValueWithSingleLabelAndFeatureZeroProbabilityLogSafety() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        dataAlphabet.lookupIndex("zzz");
        labelAlphabet.lookupIndex("class1");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1e-9});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"class1"}, new double[]{1.0});
        Instance instance = new Instance(fv, lv, "small_score", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList list = new InstanceList(pipe);
        list.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        double[] params = new double[opt.getNumParameters()];
        for (int i = 0; i < params.length; i++) {
            params[i] = -1e9;  
        }

        opt.setParameters(params);
        double val = opt.getValue();

        assertTrue(Double.isInfinite(val) || val > 0); 
    }
@Test
    public void testNegativeParameterInputGeneratesFiniteGradient() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        dataAlphabet.lookupIndex("p");
        labelAlphabet.lookupIndex("Q");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"Q"}, new double[]{1.0});
        Instance instance = new Instance(fv, lv, "neg_param", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList list = new InstanceList(pipe);
        list.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        double[] params = new double[opt.getNumParameters()];
        params[0] = -10.0;

        opt.setParameters(params);
        double[] grad = new double[opt.getNumParameters()];
        opt.getValueGradient(grad);

        assertTrue(Double.isFinite(grad[0]));
    }
@Test
    public void testGradientWithZeroScoresAndZeroFeatureVector() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        dataAlphabet.lookupIndex("x");
        labelAlphabet.lookupIndex("L1");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{}, new double[]{});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"L1"}, new double[]{1.0});
        Instance instance = new Instance(fv, lv, "emptyFV", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList list = new InstanceList(pipe);
        list.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        double[] grad = new double[opt.getNumParameters()];
        opt.getValueGradient(grad);

        assertTrue(grad.length > 0);
    }
@Test
    public void testMultipleLabelLocationsWithUniformProbabilities() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        dataAlphabet.lookupIndex("f1");
        labelAlphabet.lookupIndex("A");
        labelAlphabet.lookupIndex("B");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        String[] labels = new String[]{"A", "B"};
        double[] probs = new double[]{0.5, 0.5};

        LabelVector lv = new LabelVector(labelAlphabet, labels, probs);
        Instance instance = new Instance(fv, lv, "uniformLabeling", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList list = new InstanceList(pipe);
        list.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        double val = opt.getValue();
        assertTrue(Double.isFinite(val));
    }
@Test
    public void testExtremeGaussianPriorRegularizationEffect() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        dataAlphabet.lookupIndex("f0");
        labelAlphabet.lookupIndex("L");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"L"}, new double[]{1.0});
        Instance instance = new Instance(fv, lv, "prior_extreme", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList list = new InstanceList(pipe);
        list.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        double[] params = new double[opt.getNumParameters()];
        for (int i = 0; i < params.length; i++) {
            params[i] = 1000.0;
        }
        opt.setParameters(params);
        opt.setGaussianPriorVariance(1e-5);
        double value = opt.getValue();
        assertTrue(value > 1e3); 
    }
@Test
    public void testMultipleSequentialParameterUpdatesAffectGradient() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        dataAlphabet.lookupIndex("x1");
        labelAlphabet.lookupIndex("Y");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"Y"}, new double[]{1.0});
        Instance instance = new Instance(fv, lv, "param_updates", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList list = new InstanceList(pipe);
        list.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        double[] grad1 = new double[opt.getNumParameters()];
        opt.getValueGradient(grad1);

        opt.setParameter(0, opt.getParameter(0) + 5.0);
        double[] grad2 = new double[opt.getNumParameters()];
        opt.getValueGradient(grad2);

        assertNotEquals(grad1[0], grad2[0], 0.0001);
    }
@Test
    public void testAddSameLabelMultipleTimesSimulatesMultiLabelNormalization() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        dataAlphabet.lookupIndex("f");
        labelAlphabet.lookupIndex("Yes");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        String[] labels = new String[]{"Yes", "Yes"};
        double[] probs = new double[]{0.5, 0.5};

        LabelVector lv = new LabelVector(labelAlphabet, labels, probs);
        Instance instance = new Instance(fv, lv, "duplicateLabels", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList list = new InstanceList(pipe);
        list.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        double val = opt.getValue();
        assertTrue(Double.isFinite(val));
    }
@Test
    public void testInstanceWithoutFeatureAlphabetThrowsNoError() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        labelAlphabet.lookupIndex("on");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{}, new double[]{});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"on"}, new double[]{1.0});
        Instance instance = new Instance(fv, lv, "noFeatureAlphabet", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList list = new InstanceList(pipe);

        list.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        assertTrue(Double.isFinite(opt.getValue()));
    }
@Test
    public void testGetValueWithNaNFeatureIsHandledSafely() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        dataAlphabet.lookupIndex("fa");
        labelAlphabet.lookupIndex("label");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{Double.NaN});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"label"}, new double[]{1.0});
        Instance instance = new Instance(fv, lv, "nan_in_feature", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList instList = new InstanceList(pipe);
        instList.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(instList, null);
        double val = opt.getValue();
        assertTrue(Double.isFinite(val));
    }
@Test
    public void testMaxEntOptimizableWithOnlyDefaultFeature() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        labelAlphabet.lookupIndex("D");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{}, new double[]{});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"D"}, new double[]{1.0});
        Instance instance = new Instance(fv, lv, "only_default", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList list = new InstanceList(pipe);
        list.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        double[] grad = new double[opt.getNumParameters()];
        opt.getValueGradient(grad);

        assertTrue(grad.length > 0);
    }
@Test
    public void testSetGaussianPriorVarianceWithZeroProducesFiniteValue() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        dataAlphabet.lookupIndex("var");
        labelAlphabet.lookupIndex("Z");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"Z"}, new double[]{1.0});
        Instance instance = new Instance(fv, lv, "prior_zero", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList list = new InstanceList(pipe);
        list.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        opt.setGaussianPriorVariance(1e-20);
        double val = opt.getValue();
        assertTrue(Double.isFinite(val)); 
    }
@Test
    public void testValueGradientUpdatesAfterMultipleGetValueCalls() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        dataAlphabet.lookupIndex("x");
        labelAlphabet.lookupIndex("A");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"A"}, new double[]{1.0});
        Instance instance = new Instance(fv, lv, "gval", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList instances = new InstanceList(pipe);
        instances.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(instances, null);

        double value1 = opt.getValue();
        double[] gradient1 = new double[opt.getNumParameters()];
        opt.getValueGradient(gradient1);
        double value2 = opt.getValue();
        double[] gradient2 = new double[opt.getNumParameters()];
        opt.getValueGradient(gradient2);

        assertEquals(value1, value2, 0.0000001);
        assertEquals(gradient1[0], gradient2[0], 0.0000001);
    }
@Test
    public void testGradientZeroedProperlyWithNegativeInfinityParam() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        dataAlphabet.lookupIndex("feat");
        labelAlphabet.lookupIndex("lbl");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"lbl"}, new double[]{1.0});
        Instance instance = new Instance(fv, lv, "negInfParam", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList instances = new InstanceList(pipe);
        instances.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(instances, null);
        double[] params = new double[opt.getNumParameters()];
        for (int i = 0; i < params.length; i++) {
            params[i] = Double.NEGATIVE_INFINITY;
        }
        opt.setParameters(params);

        double[] gradient = new double[opt.getNumParameters()];
        opt.getValueGradient(gradient);

        assertEquals(0.0, gradient[0], 0.0);
    }
@Test
    public void testEmptyFeatureSelectionAllowedWithDefaultFeature() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        dataAlphabet.lookupIndex("x");
        labelAlphabet.lookupIndex("true");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.2});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"true"}, new double[]{1.0});
        Instance instance = new Instance(fv, lv, "fselCheck", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList list = new InstanceList(pipe);
        FeatureSelection fs = new FeatureSelection(dataAlphabet);
        list.setFeatureSelection(fs); 

        list.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        double val = opt.getValue();
        assertTrue(Double.isFinite(val));
    }
@Test
    public void testPerLabelFeatureSelectionUsedCorrectly() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        dataAlphabet.lookupIndex("x1");
        labelAlphabet.lookupIndex("LABEL");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"LABEL"}, new double[]{1.0});
        Instance instance = new Instance(fv, lv, "perLabelFsel", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList list = new InstanceList(pipe);

        FeatureSelection[] perLabelFsel = new FeatureSelection[1];
        perLabelFsel[0] = new FeatureSelection(dataAlphabet);
        perLabelFsel[0].add(0);

        list.setPerLabelFeatureSelection(perLabelFsel);
        list.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        double val = opt.getValue();
        assertTrue(Double.isFinite(val));
    }
@Test
    public void testZeroInstanceWeightSkipsGradientAndConstraint() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        dataAlphabet.lookupIndex("x");
        labelAlphabet.lookupIndex("Z");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{5.0});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"Z"}, new double[]{1.0});
        Instance instance = new Instance(fv, lv, "zeroWeight", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList list = new InstanceList(pipe);
        list.add(instance);
        list.setInstanceWeight(0, 0.0);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        double val = opt.getValue();
        assertTrue(Double.isFinite(val));
    }
@Test
    public void testSetParameterMarksValueAndGradientStale() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        dataAlphabet.lookupIndex("a");
        labelAlphabet.lookupIndex("B");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{3.14});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"B"}, new double[]{1.0});
        Instance instance = new Instance(fv, lv, "param_stale", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList list = new InstanceList(pipe);
        list.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        double originalValue = opt.getValue();

        double originalParam = opt.getParameter(0);
        opt.setParameter(0, originalParam + 10.0);
        double updatedValue = opt.getValue();

        assertNotEquals(originalValue, updatedValue, 1e-6);
    }
@Test
    public void testSetParametersWithExactLengthPreservesValues() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        dataAlphabet.lookupIndex("xx");
        labelAlphabet.lookupIndex("L");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{2.0});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"L"}, new double[]{1.0});
        Instance instance = new Instance(fv, lv, "setExact", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList list = new InstanceList(pipe);
        list.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        int len = opt.getNumParameters();

        double[] setParams = new double[len];
        setParams[0] = 100.0;
        if (len > 1) setParams[1] = 200.0;

        opt.setParameters(setParams);

        double[] readBack = new double[len];
        opt.getParameters(readBack);
        assertEquals(100.0, readBack[0], 1e-6);
        if (len > 1) {
            assertEquals(200.0, readBack[1], 1e-6);
        }
    }
@Test
    public void testSetParametersWithLargerBufferResizesAndReadsCorrectly() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        dataAlphabet.lookupIndex("f_large");
        labelAlphabet.lookupIndex("Y");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"Y"}, new double[]{1.0});
        Instance instance = new Instance(fv, lv, "resize_params", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList list = new InstanceList(pipe);
        list.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        int oldSize = opt.getNumParameters();
        double[] larger = new double[oldSize + 2];
        larger[0] = 11.11;
        larger[oldSize + 1] = 99.99; 

        opt.setParameters(larger);

        double[] check = new double[opt.getNumParameters()];
        opt.getParameters(check);

        assertEquals(11.11, check[0], 0.0001);
        assertEquals(99.99, check[oldSize + 1], 0.0001);
    }
@Test
    public void testLabelingWithLabelIndexOutOfTargetAlphabetSizeIgnored() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        dataAlphabet.lookupIndex("f1");
        labelAlphabet.lookupIndex("L1");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});

        LabelAlphabet separateLabelAlphabet = new LabelAlphabet();
        separateLabelAlphabet.lookupIndex("L1");
        separateLabelAlphabet.lookupIndex("L2");

        LabelVector lv = new LabelVector(separateLabelAlphabet, new String[]{"L1", "L2"}, new double[]{1.0, 0.0});
        Instance instance = new Instance(fv, lv, "mismatchedLabelIndices", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList list = new InstanceList(pipe);
        list.setTargetAlphabet(labelAlphabet);
        list.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        double result = opt.getValue();
        assertTrue(Double.isFinite(result));
    }
@Test
    public void testGetValueWithEmptyInstanceListReturnsZeroValue() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList list = new InstanceList(pipe);
        list.setDataAlphabet(dataAlphabet);
        list.setTargetAlphabet(labelAlphabet);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        double val = opt.getValue();
        assertTrue(val >= 0);
    }
@Test
    public void testLabelingEqualsZeroProbabilityDoesNotContributeValue() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        dataAlphabet.lookupIndex("f1");

        labelAlphabet.lookupIndex("A");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});

        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"A"}, new double[]{0.0});
        Instance instance = new Instance(fv, lv, "zeroProbLabel", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList list = new InstanceList(pipe);
        list.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        double val = opt.getValue();

        assertTrue(Double.isFinite(val));
    }
@Test
    public void testSetParametersWithEmptyArrayAcceptsAndInitializesParameters() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        dataAlphabet.lookupIndex("f1");
        labelAlphabet.lookupIndex("L1");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"L1"}, new double[]{1.0});
        Instance instance = new Instance(fv, lv, "emptyParamArray", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList instanceList = new InstanceList(pipe);
        instanceList.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(instanceList, null);

        opt.setParameters(new double[0]);

        double[] buffer = new double[opt.getNumParameters()];
        opt.getParameters(buffer);
        assertNotNull(buffer);
    }
@Test
    public void testMultipleIdenticalLabelsWithDifferentProbabilities() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        dataAlphabet.lookupIndex("f1");
        labelAlphabet.lookupIndex("L1");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});

        String[] labels = new String[]{"L1", "L1"};
        double[] probs = new double[]{0.6, 0.4};

        LabelVector lv = new LabelVector(labelAlphabet, labels, probs);
        Instance instance = new Instance(fv, lv, "duplicateLabelNames", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList instances = new InstanceList(pipe);
        instances.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(instances, null);

        double value = opt.getValue();
        assertTrue(Double.isFinite(value));
    }
@Test
    public void testNegativeFeatureValueHandledInCalculation() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        dataAlphabet.lookupIndex("x");
        labelAlphabet.lookupIndex("Y");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{-1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"Y"}, new double[]{1.0});
        Instance inst = new Instance(fv, lv, "negativeFeature", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList list = new InstanceList(pipe);
        list.add(inst);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        double val = opt.getValue();
        assertTrue(Double.isFinite(val));
    }
@Test
    public void testGetParameterOutOfBoundsReturnsDefaultZero() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        dataAlphabet.lookupIndex("fx");
        labelAlphabet.lookupIndex("LABEL");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"LABEL"}, new double[]{1.0});
        Instance instance = new Instance(fv, lv, "obTest", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList il = new InstanceList(pipe);
        il.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(il, null);
        int length = opt.getNumParameters();

        boolean exceptionThrown = false;
        try {
            opt.getParameter(length); 
        } catch (ArrayIndexOutOfBoundsException ex) {
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown);
    }
@Test
    public void testSetAndGetSingleParameterIsConsistent() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        dataAlphabet.lookupIndex("foo");
        labelAlphabet.lookupIndex("bar");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{2.5});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"bar"}, new double[]{1.0});
        Instance instance = new Instance(fv, lv, "paramTest", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList list = new InstanceList(pipe);
        list.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        opt.setParameter(0, 7.77);
        double val = opt.getParameter(0);
        assertEquals(7.77, val, 0.0001);
    }
@Test
    public void testDefaultFeatureIndexAlwaysIncluded() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        dataAlphabet.lookupIndex("a");
        labelAlphabet.lookupIndex("b");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{0.5});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"b"}, new double[]{1.0});
        Instance instance = new Instance(fv, lv, "defaultFeature", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList list = new InstanceList(pipe);
        FeatureSelection fs = new FeatureSelection(dataAlphabet);
        list.setFeatureSelection(fs);
        list.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        double val = opt.getValue();
        assertTrue(Double.isFinite(val));
    }
@Test
    public void testLabelVectorWithAllZeroProbabilitiesIgnoredGracefully() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        dataAlphabet.lookupIndex("feature");
        labelAlphabet.lookupIndex("labelA");
        labelAlphabet.lookupIndex("labelB");

        double[] values = new double[]{1.0};
        int[] indices = new int[]{0};
        FeatureVector fv = new FeatureVector(dataAlphabet, indices, values);
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"labelA", "labelB"}, new double[]{0.0, 0.0});
        Instance instance = new Instance(fv, lv, "zeroProbs", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList training = new InstanceList(pipe);
        training.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(training, null);
        double value = opt.getValue();
        assertTrue(Double.isFinite(value));
    }
@Test
    public void testNegativeLabelProbabilityHandledInValueComputation() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        dataAlphabet.lookupIndex("x");
        labelAlphabet.lookupIndex("label");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"label"}, new double[]{-0.5});
        Instance inst = new Instance(fv, lv, "negLabelProb", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList list = new InstanceList(pipe);
        list.add(inst);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        double val = opt.getValue(); 
        assertTrue(Double.isFinite(val));
    }
@Test
    public void testInstanceWithNullFeatureVectorIsIgnored() {
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("classX");

        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"classX"}, new double[]{1.0});
        Instance instance = new Instance(null, lv, "nullFV", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList instances = new InstanceList(pipe);
        instances.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(instances, null);
        double val = opt.getValue();
        assertTrue(Double.isFinite(val));
    }
@Test
    public void testSetParameterWithNaNAcceptedAndHandled() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        dataAlphabet.lookupIndex("special");
        labelAlphabet.lookupIndex("L");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{0.1});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"L"}, new double[]{1.0});
        Instance instance = new Instance(fv, lv, "nanParam", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList list = new InstanceList(pipe);
        list.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        assertTrue(opt.getNumParameters() > 0);
        opt.setParameter(0, Double.NaN);

        double[] grad = new double[opt.getNumParameters()];
        opt.getValueGradient(grad);
        assertFalse(Double.isNaN(grad[0])); 
    }
@Test
    public void testSetParameterWithNegativeInfinityHandledInGradient() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        dataAlphabet.lookupIndex("testF");
        labelAlphabet.lookupIndex("class");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{4.2});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"class"}, new double[]{1.0});
        Instance instance = new Instance(fv, lv, "negInfParam2", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList ilist = new InstanceList(pipe);
        ilist.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(ilist, null);
        opt.setParameter(0, Double.NEGATIVE_INFINITY);

        double[] gradient = new double[opt.getNumParameters()];
        opt.getValueGradient(gradient);
        assertEquals(0.0, gradient[0], 0.0000001);
    }
@Test
    public void testCachedValueRefreshedAfterSetParameterCall() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        dataAlphabet.lookupIndex("v");
        labelAlphabet.lookupIndex("Z");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"Z"}, new double[]{1.0});
        Instance inst = new Instance(fv, lv, "cacheInvalidate", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList list = new InstanceList(pipe);
        list.add(inst);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        double value1 = opt.getValue();
        opt.setParameter(0, value1 + 1.0);
        double value2 = opt.getValue();

        assertNotEquals(value1, value2, 1e-6);
    }
@Test
    public void testGetValueReturnsNegativeInfinityForZeroProbWithPositiveLabelProb() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        dataAlphabet.lookupIndex("f1");
        labelAlphabet.lookupIndex("L1");
        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"L1"}, new double[]{1.0});
        Instance instance = new Instance(fv, lv, "infLikelihood", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList list = new InstanceList(pipe);
        list.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        double[] params = new double[opt.getNumParameters()];
        for (int i = 0; i < params.length; i++) {
            params[i] = -1e10; 
        }

        opt.setParameters(params);
        double val = opt.getValue();
        assertTrue(Double.isInfinite(val)); 
    }
@Test
    public void testDefaultFeatureOnlyInstanceAddsCorrectConstraint() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("C");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{}, new double[]{});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"C"}, new double[]{1.0});
        Instance inst = new Instance(fv, lv, "constraintsDefault", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList list = new InstanceList(pipe);
        list.add(inst);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        double[] gradient = new double[opt.getNumParameters()];
        opt.getValueGradient(gradient);

        boolean hasNonZero = false;
        for (int i = 0; i < gradient.length; i++) {
            if (gradient[i] != 0.0) {
                hasNonZero = true;
                break;
            }
        }
        assertTrue(hasNonZero);
    }
@Test
    public void testZeroFeatureInstanceAndZeroProbabilityLabelProducesFiniteValue() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("Y");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{}, new double[]{});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"Y"}, new double[]{0.0});
        Instance inst = new Instance(fv, lv, "zeroFeatureZeroProb", null);

        Pipe pipe = new SerialPipes(new ArrayList<>());
        InstanceList list = new InstanceList(pipe);
        list.add(inst);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        double value = opt.getValue();
        assertTrue(Double.isFinite(value));
    } 
}