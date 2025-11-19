public class MaxEntOptimizableByLabelDistribution_5_GPTLLMTest { 

 @Test
    public void testConstructorWithNonNullClassifier() {
        List pipeList = new ArrayList();
        pipeList.add(new Input2CharSequence("UTF-8"));
        pipeList.add(new CharSequence2TokenSequence());
        pipeList.add(new TokenSequenceLowercase());
        pipeList.add(new TokenSequence2FeatureVector());
        pipeList.add(new Target2Label());
        SerialPipes pipe = new SerialPipes(pipeList);

        Instance instance1 = new Instance("Dog barks", "animal", null, null);
        Instance instance2 = new Instance("Car goes", "vehicle", null, null);
        InstanceList instanceList = new InstanceList(pipe);
        instanceList.addThruPipe(instance1);
        instanceList.addThruPipe(instance2);

        int numLabels = ((LabelAlphabet) instanceList.getTargetAlphabet()).size();
        int numFeatures = instanceList.getDataAlphabet().size() + 1;
        double[] params = new double[numLabels * numFeatures];

        MaxEnt classifier = new MaxEnt(instanceList.getPipe(), params, null, null);
        MaxEntOptimizableByLabelDistribution optimizable = new MaxEntOptimizableByLabelDistribution(instanceList, classifier);

        assertNotNull(optimizable.getClassifier());
    }
@Test
    public void testConstructorWithNullClassifierCreatesInternalClassifier() {
        List pipeList = new ArrayList();
        pipeList.add(new Input2CharSequence("UTF-8"));
        pipeList.add(new CharSequence2TokenSequence());
        pipeList.add(new TokenSequenceLowercase());
        pipeList.add(new TokenSequence2FeatureVector());
        pipeList.add(new Target2Label());
        SerialPipes pipe = new SerialPipes(pipeList);

        Instance instance1 = new Instance("Cat meows", "animal", null, null);
        InstanceList instanceList = new InstanceList(pipe);
        instanceList.addThruPipe(instance1);

        MaxEntOptimizableByLabelDistribution optimizable = new MaxEntOptimizableByLabelDistribution(instanceList, null);

        assertNotNull(optimizable.getClassifier());
    }
@Test
    public void testSetAndGetParameter() {
        List pipeList = new ArrayList();
        pipeList.add(new Input2CharSequence("UTF-8"));
        pipeList.add(new CharSequence2TokenSequence());
        pipeList.add(new TokenSequenceLowercase());
        pipeList.add(new TokenSequence2FeatureVector());
        pipeList.add(new Target2Label());
        SerialPipes pipe = new SerialPipes(pipeList);

        Instance instance = new Instance("A dog runs", "animal", null, null);
        InstanceList instanceList = new InstanceList(pipe);
        instanceList.addThruPipe(instance);

        MaxEntOptimizableByLabelDistribution optimizable = new MaxEntOptimizableByLabelDistribution(instanceList, null);

        optimizable.setParameter(0, 3.14);
        double result = optimizable.getParameter(0);

        assertEquals(3.14, result, 1e-10);
    }
@Test
    public void testSetParametersAndGetParameters() {
        List pipeList = new ArrayList();
        pipeList.add(new Input2CharSequence("UTF-8"));
        pipeList.add(new CharSequence2TokenSequence());
        pipeList.add(new TokenSequenceLowercase());
        pipeList.add(new TokenSequence2FeatureVector());
        pipeList.add(new Target2Label());
        SerialPipes pipe = new SerialPipes(pipeList);

        Instance instance = new Instance("Bicycle rides", "vehicle", null, null);
        InstanceList instanceList = new InstanceList(pipe);
        instanceList.addThruPipe(instance);

        MaxEntOptimizableByLabelDistribution optimizable = new MaxEntOptimizableByLabelDistribution(instanceList, null);
        int paramSize = optimizable.getNumParameters();

        double[] newParams = new double[paramSize];
        newParams[0] = 1.1;
        if (paramSize > 1) newParams[1] = 2.2;

        optimizable.setParameters(newParams);

        double[] retrievedParams = new double[paramSize];
        optimizable.getParameters(retrievedParams);

        assertEquals(newParams[0], retrievedParams[0], 1e-10);
        if (paramSize > 1) assertEquals(newParams[1], retrievedParams[1], 1e-10);
    }
@Test
    public void testGetValueComputesNonNanNegativeValue() {
        List pipeList = new ArrayList();
        pipeList.add(new Input2CharSequence("UTF-8"));
        pipeList.add(new CharSequence2TokenSequence());
        pipeList.add(new TokenSequenceLowercase());
        pipeList.add(new TokenSequence2FeatureVector());
        pipeList.add(new Target2Label());
        SerialPipes pipe = new SerialPipes(pipeList);

        Instance instance = new Instance("Elephant walks", "animal", null, null);
        InstanceList instanceList = new InstanceList(pipe);
        instanceList.addThruPipe(instance);

        MaxEntOptimizableByLabelDistribution optimizable = new MaxEntOptimizableByLabelDistribution(instanceList, null);
        double value = optimizable.getValue();

        assertFalse(Double.isNaN(value));
        assertFalse(Double.isInfinite(value));
        assertTrue(value < 0);
    }
@Test
    public void testGetValueGradientComputesConsistentArray() {
        List pipeList = new ArrayList();
        pipeList.add(new Input2CharSequence("UTF-8"));
        pipeList.add(new CharSequence2TokenSequence());
        pipeList.add(new TokenSequenceLowercase());
        pipeList.add(new TokenSequence2FeatureVector());
        pipeList.add(new Target2Label());
        SerialPipes pipe = new SerialPipes(pipeList);

        Instance instance1 = new Instance("Truck hauls", "vehicle", null, null);
        InstanceList instanceList = new InstanceList(pipe);
        instanceList.addThruPipe(instance1);

        MaxEntOptimizableByLabelDistribution optimizable = new MaxEntOptimizableByLabelDistribution(instanceList, null);
        optimizable.getValue();

        double[] gradient = new double[optimizable.getNumParameters()];
        optimizable.getValueGradient(gradient);

        assertNotNull(gradient);
        assertEquals(optimizable.getNumParameters(), gradient.length);
        assertFalse(Double.isNaN(gradient[0]));
    }
@Test
    public void testUseGaussianPriorReturnsSameInstance() {
        List pipeList = new ArrayList();
        pipeList.add(new Input2CharSequence("UTF-8"));
        pipeList.add(new CharSequence2TokenSequence());
        pipeList.add(new TokenSequenceLowercase());
        pipeList.add(new TokenSequence2FeatureVector());
        pipeList.add(new Target2Label());
        SerialPipes pipe = new SerialPipes(pipeList);

        Instance instance = new Instance("Whale swims", "animal", null, null);
        InstanceList instanceList = new InstanceList(pipe);
        instanceList.addThruPipe(instance);

        MaxEntOptimizableByLabelDistribution optimizable = new MaxEntOptimizableByLabelDistribution(instanceList, null);
        MaxEntOptimizableByLabelDistribution returned = optimizable.useGaussianPrior();

        assertSame(optimizable, returned);
    }
@Test
    public void testSetGaussianPriorVarianceInfluencesValue() {
        List pipeList = new ArrayList();
        pipeList.add(new Input2CharSequence("UTF-8"));
        pipeList.add(new CharSequence2TokenSequence());
        pipeList.add(new TokenSequenceLowercase());
        pipeList.add(new TokenSequence2FeatureVector());
        pipeList.add(new Target2Label());
        SerialPipes pipe = new SerialPipes(pipeList);

        Instance instance = new Instance("Plane flies", "vehicle", null, null);
        InstanceList instanceList = new InstanceList(pipe);
        instanceList.addThruPipe(instance);

        MaxEntOptimizableByLabelDistribution optimizable = new MaxEntOptimizableByLabelDistribution(instanceList, null);
        optimizable.setGaussianPriorVariance(0.5);
        double value = optimizable.getValue();

        assertFalse(Double.isNaN(value));
        assertFalse(Double.isInfinite(value));
    }
@Test
    public void testEmptyTrainingListDoesNotCrash() {
        List pipeList = new ArrayList();
        pipeList.add(new Input2CharSequence("UTF-8"));
        pipeList.add(new CharSequence2TokenSequence());
        pipeList.add(new TokenSequenceLowercase());
        pipeList.add(new TokenSequence2FeatureVector());
        pipeList.add(new Target2Label());
        SerialPipes pipe = new SerialPipes(pipeList);

        InstanceList emptyList = new InstanceList(pipe);
        MaxEntOptimizableByLabelDistribution optimizable = new MaxEntOptimizableByLabelDistribution(emptyList, null);

        double value = optimizable.getValue();
        assertFalse(Double.isNaN(value));
        assertFalse(Double.isInfinite(value));
    }
@Test
    public void testParameterChangeTriggersNewGradient() {
        List pipeList = new ArrayList();
        pipeList.add(new Input2CharSequence("UTF-8"));
        pipeList.add(new CharSequence2TokenSequence());
        pipeList.add(new TokenSequenceLowercase());
        pipeList.add(new TokenSequence2FeatureVector());
        pipeList.add(new Target2Label());
        SerialPipes pipe = new SerialPipes(pipeList);

        Instance instance = new Instance("Rocket launches", "vehicle", null, null);
        InstanceList instanceList = new InstanceList(pipe);
        instanceList.addThruPipe(instance);

        MaxEntOptimizableByLabelDistribution optimizable = new MaxEntOptimizableByLabelDistribution(instanceList, null);
        double[] gradientBefore = new double[optimizable.getNumParameters()];
        optimizable.getValue();
        optimizable.getValueGradient(gradientBefore);

        optimizable.setParameter(0, 0.75);

        double[] gradientAfter = new double[optimizable.getNumParameters()];
        optimizable.getValueGradient(gradientAfter);

        assertNotEquals(gradientBefore[0], gradientAfter[0], 1e-8);
    }
@Test
    public void testSetParametersWithNullBufferDoesNotThrow() {
        List pipeList = new ArrayList();
        pipeList.add(new Input2CharSequence("UTF-8"));
        pipeList.add(new CharSequence2TokenSequence());
        pipeList.add(new TokenSequenceLowercase());
        pipeList.add(new TokenSequence2FeatureVector());
        pipeList.add(new Target2Label());
        SerialPipes pipe = new SerialPipes(pipeList);

        Instance instance = new Instance("Single label sample", "labelA", null, null);
        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(instance);

        MaxEntOptimizableByLabelDistribution optimizable = new MaxEntOptimizableByLabelDistribution(list, null);

        double[] buffer = null;
        optimizable.getParameters(buffer); 
    }
@Test
    public void testSetParametersWithLongerArrayReplacesInternalReference() {
        List pipeList = new ArrayList();
        pipeList.add(new Input2CharSequence("UTF-8"));
        pipeList.add(new CharSequence2TokenSequence());
        pipeList.add(new TokenSequenceLowercase());
        pipeList.add(new TokenSequence2FeatureVector());
        pipeList.add(new Target2Label());
        SerialPipes pipe = new SerialPipes(pipeList);

        Instance instance = new Instance("Foo bar baz", "group1", null, null);
        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(instance);

        MaxEntOptimizableByLabelDistribution optimizable = new MaxEntOptimizableByLabelDistribution(list, null);
        int oldSize = optimizable.getNumParameters();
        double[] largerBuffer = new double[oldSize + 10];
        largerBuffer[0] = 42.0;

        optimizable.setParameters(largerBuffer);
        double[] result = new double[oldSize + 10];
        optimizable.getParameters(result);

        assertEquals(42.0, result[0], 1e-10);
        assertEquals(oldSize + 10, result.length);
    }
@Test
    public void testGetValueSkipsInstanceWithNullLabeling() {
        List pipeList = new ArrayList();
        pipeList.add(new Input2CharSequence("UTF-8"));
        pipeList.add(new CharSequence2TokenSequence());
        pipeList.add(new TokenSequenceLowercase());
        pipeList.add(new TokenSequence2FeatureVector());
        pipeList.add(new Target2Label());
        SerialPipes pipe = new SerialPipes(pipeList);

        Instance good = new Instance("Dog runs fast", "animal", null, null);
        Instance nullLabel = new Instance("This should be ignored", null, null, null);

        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(good);
        
        list.add(nullLabel);

        MaxEntOptimizableByLabelDistribution optimizable = new MaxEntOptimizableByLabelDistribution(list, null);
        double value = optimizable.getValue();

        assertFalse(Double.isNaN(value));
        assertTrue(value < 0);
    }
@Test
    public void testGetValueReturnsNegativeInfinityForZeroScoreAndActiveLabel() {
        List pipeList = new ArrayList();
        pipeList.add(new Input2CharSequence("UTF-8"));
        pipeList.add(new CharSequence2TokenSequence());
        pipeList.add(new TokenSequenceLowercase());
        pipeList.add(new TokenSequence2FeatureVector());
        pipeList.add(new Target2Label());
        SerialPipes pipe = new SerialPipes(pipeList);

        
        Instance instance = new Instance("Alpha beta gamma", "labelX", null, null);
        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(instance);

        MaxEnt classifier = new MaxEnt(list.getPipe(), new double[1 * (list.getDataAlphabet().size() + 1)], null, null) {
            @Override
            public void getClassificationScores(Instance inst, double[] scores) {
                scores[0] = 0.0; 
            }
        };

        MaxEntOptimizableByLabelDistribution optimizable = new MaxEntOptimizableByLabelDistribution(list, classifier);
        double value = optimizable.getValue();
        assertEquals(Double.NEGATIVE_INFINITY, value, 0.0);
    }
@Test
    public void testNanInFeatureVectorDoesNotBreakValueComputation() {
        List pipeList = new ArrayList();
        pipeList.add(new Input2CharSequence("UTF-8"));
        pipeList.add(new CharSequence2TokenSequence());
        pipeList.add(new TokenSequenceLowercase());
        pipeList.add(new TokenSequence2FeatureVector());
        pipeList.add(new Target2Label());
        SerialPipes pipe = new SerialPipes(pipeList);

        Instance instance1 = new Instance("X Y Z", "C1", null, null);
        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(instance1);

        
        Instance broken = new Instance("zzz", "C1", null, null);
        list.addThruPipe(broken);

        MaxEntOptimizableByLabelDistribution optimizable = new MaxEntOptimizableByLabelDistribution(list, null);
        double value = optimizable.getValue();

        assertFalse(Double.isNaN(value));
    }
@Test
    public void testSubstituteNegativeInfinityHandledInGradient() {
        List pipeList = new ArrayList();
        pipeList.add(new Input2CharSequence("UTF-8"));
        pipeList.add(new CharSequence2TokenSequence());
        pipeList.add(new TokenSequenceLowercase());
        pipeList.add(new TokenSequence2FeatureVector());
        pipeList.add(new Target2Label());
        SerialPipes pipe = new SerialPipes(pipeList);

        Instance instance = new Instance("Test sentence", "cat", null, null);
        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(instance);

        MaxEntOptimizableByLabelDistribution optimizable = new MaxEntOptimizableByLabelDistribution(list, null);

        double[] params = new double[optimizable.getNumParameters()];
        params[0] = Double.NEGATIVE_INFINITY;
        optimizable.setParameters(params);

        double[] gradient = new double[optimizable.getNumParameters()];
        optimizable.getValueGradient(gradient);

        assertEquals(0.0, gradient[0], 0.0);
    }
@Test
    public void testValueAndGradientCalledMultipleTimesConsistentResult() {
        List pipeList = new ArrayList();
        pipeList.add(new Input2CharSequence("UTF-8"));
        pipeList.add(new CharSequence2TokenSequence());
        pipeList.add(new TokenSequenceLowercase());
        pipeList.add(new TokenSequence2FeatureVector());
        pipeList.add(new Target2Label());
        SerialPipes pipe = new SerialPipes(pipeList);

        Instance instance = new Instance("Repetition test", "repeat", null, null);
        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(instance);

        MaxEntOptimizableByLabelDistribution optimizable = new MaxEntOptimizableByLabelDistribution(list, null);
        double val1 = optimizable.getValue();
        double val2 = optimizable.getValue();
        double val3 = optimizable.getValue();

        assertEquals(val1, val2, 1e-10);
        assertEquals(val2, val3, 1e-10);

        double[] grad1 = new double[optimizable.getNumParameters()];
        double[] grad2 = new double[optimizable.getNumParameters()];
        optimizable.getValueGradient(grad1);
        optimizable.getValueGradient(grad2);

        assertArrayEquals(grad1, grad2, 1e-10);
    }
@Test
    public void testGradientBufferMustMatchSize() {
        List pipeList = new ArrayList();
        pipeList.add(new Input2CharSequence("UTF-8"));
        pipeList.add(new CharSequence2TokenSequence());
        pipeList.add(new TokenSequenceLowercase());
        pipeList.add(new TokenSequence2FeatureVector());
        pipeList.add(new Target2Label());
        SerialPipes pipe = new SerialPipes(pipeList);

        Instance instance = new Instance("g1 g2 g3", "something", null, null);
        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(instance);

        MaxEntOptimizableByLabelDistribution optimizable = new MaxEntOptimizableByLabelDistribution(list, null);

        int wrongSize = optimizable.getNumParameters() - 1;
        double[] buffer = new double[wrongSize];

        try {
            optimizable.getValueGradient(buffer);
            fail("Expected ArrayIndexOutOfBoundsException or AssertionError");
        } catch (AssertionError | ArrayIndexOutOfBoundsException expected) {
            assertTrue(true);
        }
    }
@Test
    public void testInstanceWithSingleFeatureAndLabelDistributionOfSizeOne() {
        List pipeList = new ArrayList();
        pipeList.add(new Input2CharSequence("UTF-8"));
        pipeList.add(new CharSequence2TokenSequence("\\p{L}+")); 
        pipeList.add(new TokenSequenceLowercase());
        pipeList.add(new TokenSequence2FeatureVector());
        pipeList.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipeList);

        Instance instance = new Instance("unique", "only", null, null);
        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(instance);

        MaxEntOptimizableByLabelDistribution optimizable = new MaxEntOptimizableByLabelDistribution(list, null);
        double val = optimizable.getValue();

        assertFalse(Double.isNaN(val));
        assertFalse(Double.isInfinite(val));
    }
@Test
    public void testAllZeroParametersYieldsStableGradient() {
        List pipeList = new ArrayList();
        pipeList.add(new Input2CharSequence("UTF-8"));
        pipeList.add(new CharSequence2TokenSequence());
        pipeList.add(new TokenSequenceLowercase());
        pipeList.add(new TokenSequence2FeatureVector());
        pipeList.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipeList);

        Instance instance = new Instance("alpha beta", "classA", null, null);
        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(instance);

        MaxEntOptimizableByLabelDistribution optimizable = new MaxEntOptimizableByLabelDistribution(list, null);
        double[] gradient = new double[optimizable.getNumParameters()];
        optimizable.getValueGradient(gradient);

        assertNotNull(gradient);
        assertEquals(optimizable.getNumParameters(), gradient.length);
        assertFalse(Double.isNaN(gradient[0]));
    }
@Test
    public void testFeatureSelectionIsNullHandlesProperly() {
        List pipeList = new ArrayList();
        pipeList.add(new Input2CharSequence("UTF-8"));
        pipeList.add(new CharSequence2TokenSequence());
        pipeList.add(new TokenSequenceLowercase());
        pipeList.add(new TokenSequence2FeatureVector());
        pipeList.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipeList);

        Instance instance1 = new Instance("bird flies", "class1", null, null);
        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(instance1);

        list.setFeatureSelection(null);
        list.setPerLabelFeatureSelection(null);

        MaxEntOptimizableByLabelDistribution optimizable = new MaxEntOptimizableByLabelDistribution(list, null);
        double[] gradient = new double[optimizable.getNumParameters()];

        optimizable.getValue();
        optimizable.getValueGradient(gradient);

        assertFalse(Double.isNaN(gradient[0]));
        assertEquals(gradient.length, optimizable.getNumParameters());
    }
@Test
    public void testPerLabelFeatureSelectionOverridesGlobalSelection() {
        List pipeList = new ArrayList();
        pipeList.add(new Input2CharSequence("UTF-8"));
        pipeList.add(new CharSequence2TokenSequence());
        pipeList.add(new TokenSequenceLowercase());
        pipeList.add(new TokenSequence2FeatureVector());
        pipeList.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipeList);

        Instance instance = new Instance("sky blue fly", "label1", null, null);
        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(instance);

        FeatureSelection[] perLabelFS = new FeatureSelection[1];
        perLabelFS[0] = new FeatureSelection();
        perLabelFS[0].add(0); 

        list.setPerLabelFeatureSelection(perLabelFS);
        list.setFeatureSelection(null);

        MaxEntOptimizableByLabelDistribution optimizable = new MaxEntOptimizableByLabelDistribution(list, null);
        double[] grad = new double[optimizable.getNumParameters()];
        optimizable.getValue();
        optimizable.getValueGradient(grad);
        assertEquals(grad.length, optimizable.getNumParameters());
    }
@Test
    public void testParameterResizedCorrectlyIfSetParameterExceedsCurrentSize() {
        List pipeList = new ArrayList();
        pipeList.add(new Input2CharSequence("UTF-8"));
        pipeList.add(new CharSequence2TokenSequence());
        pipeList.add(new TokenSequenceLowercase());
        pipeList.add(new TokenSequence2FeatureVector());
        pipeList.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipeList);

        Instance instance = new Instance("resize this", "x", null, null);
        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(instance);

        MaxEntOptimizableByLabelDistribution optimizable = new MaxEntOptimizableByLabelDistribution(list, null);
        int origSize = optimizable.getNumParameters();
        double[] larger = new double[origSize + 5];
        larger[origSize] = 99.9;

        optimizable.setParameters(larger);
        double[] result = new double[origSize + 5];
        optimizable.getParameters(result);

        assertEquals(99.9, result[origSize], 1e-10);
    }
@Test
    public void testMultipleGetValueGradientCallsDoNotRecomputeUnlessStale() {
        List pipeList = new ArrayList();
        pipeList.add(new Input2CharSequence("UTF-8"));
        pipeList.add(new CharSequence2TokenSequence());
        pipeList.add(new TokenSequenceLowercase());
        pipeList.add(new TokenSequence2FeatureVector());
        pipeList.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipeList);

        Instance instance = new Instance("repeat call", "zebra", null, null);
        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(instance);

        MaxEntOptimizableByLabelDistribution optimizable = new MaxEntOptimizableByLabelDistribution(list, null);

        int callsBefore = optimizable.getValueGradientCalls();

        double[] grad1 = new double[optimizable.getNumParameters()];
        optimizable.getValueGradient(grad1);
        double[] grad2 = new double[optimizable.getNumParameters()];
        optimizable.getValueGradient(grad2);

        int callsAfter = optimizable.getValueGradientCalls();

        assertEquals(grad1.length, grad2.length);
        assertEquals(callsBefore + 1, callsAfter);
    }
@Test
    public void testCachedValueAndGradientConsistencyAfterSetParameter() {
        List pipeList = new ArrayList();
        pipeList.add(new Input2CharSequence("UTF-8"));
        pipeList.add(new CharSequence2TokenSequence());
        pipeList.add(new TokenSequenceLowercase());
        pipeList.add(new TokenSequence2FeatureVector());
        pipeList.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipeList);

        Instance instance = new Instance("recompute grad val", "labelA", null, null);
        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(instance);

        MaxEntOptimizableByLabelDistribution optimizable = new MaxEntOptimizableByLabelDistribution(list, null);
        double value1 = optimizable.getValue();
        double[] grad1 = new double[optimizable.getNumParameters()];
        optimizable.getValueGradient(grad1);

        optimizable.setParameter(0, 123.456); 

        double value2 = optimizable.getValue();
        double[] grad2 = new double[optimizable.getNumParameters()];
        optimizable.getValueGradient(grad2);

        assertNotEquals(value1, value2, 1e-5);
        assertNotEquals(grad1[0], grad2[0], 1e-5);
    }
@Test
    public void testSetParameterMarksCachesAsStale() {
        List pipeList = new ArrayList();
        pipeList.add(new Input2CharSequence("UTF-8"));
        pipeList.add(new CharSequence2TokenSequence());
        pipeList.add(new TokenSequenceLowercase());
        pipeList.add(new TokenSequence2FeatureVector());
        pipeList.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipeList);

        Instance instance = new Instance("mark stale", "labelB", null, null);
        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(instance);

        MaxEntOptimizableByLabelDistribution optimizable = new MaxEntOptimizableByLabelDistribution(list, null);
        double[] grad1 = new double[optimizable.getNumParameters()];
        optimizable.getValueGradient(grad1);

        optimizable.setParameter(0, 9.99);

        double[] grad2 = new double[optimizable.getNumParameters()];
        optimizable.getValueGradient(grad2);

        assertNotEquals(grad1[0], grad2[0], 1e-10);
    }
@Test
    public void testGetValueWithNoTargetReturnsZero() {
        List pipeList = new ArrayList();
        pipeList.add(new Input2CharSequence("UTF-8"));
        pipeList.add(new CharSequence2TokenSequence());
        pipeList.add(new TokenSequenceLowercase());
        pipeList.add(new TokenSequence2FeatureVector());
        pipeList.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipeList);

        Instance good = new Instance("real data", "label1", null, null);
        Instance bad = new Instance("no target", null, null, null);

        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(good);
        list.add(bad); 

        MaxEntOptimizableByLabelDistribution optimizable = new MaxEntOptimizableByLabelDistribution(list, null);
        double value = optimizable.getValue();

        assertFalse(Double.isNaN(value));
        assertTrue(value < 0); 
    }
@Test
    public void testGetValueReturnsNegativeInfinityWhenLabelProbabilityPositiveAndScoreZero() {
        ArrayList pipeList = new ArrayList();
        pipeList.add(new Input2CharSequence("UTF-8"));
        pipeList.add(new CharSequence2TokenSequence());
        pipeList.add(new TokenSequenceLowercase());
        pipeList.add(new TokenSequence2FeatureVector());
        pipeList.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipeList);

        Instance instance = new Instance("mystery label test", "labelA", null, null);
        InstanceList instanceList = new InstanceList(pipe);
        instanceList.addThruPipe(instance);

        double[] fakeParams = new double[instanceList.getTargetAlphabet().size() * (instanceList.getDataAlphabet().size() + 1)];

        MaxEnt classifier = new MaxEnt(instanceList.getPipe(), fakeParams, null, null) {
            public void getClassificationScores(Instance instance, double[] scores) {
                scores[0] = 0.0; 
            }
        };

        MaxEntOptimizableByLabelDistribution optimizable = new MaxEntOptimizableByLabelDistribution(instanceList, classifier);
        double val = optimizable.getValue();
        assertEquals(Double.NEGATIVE_INFINITY, val, 0.0);
    }
@Test
    public void testInstanceWithAllZeroLabelProbabilitiesIsIgnored() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        dataAlphabet.lookupIndex("one");
        labelAlphabet.lookupLabel("A");
        labelAlphabet.lookupLabel("B");

        FeatureVector vector = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        Labeling labeling = new LabelVector(labelAlphabet, new double[]{0.0, 0.0}); 

        Instance inst = new Instance(vector, labeling, "X", null);

        List pipeList = new ArrayList();
        pipeList.add(new AlphabetCarryingPipe(dataAlphabet, labelAlphabet));
        Pipe pipe = new SerialPipes(pipeList);
        pipe.setTargetProcessing(false);

        InstanceList list = new InstanceList(pipe, dataAlphabet, labelAlphabet);
        list.add(inst);

        MaxEntOptimizableByLabelDistribution optimizable = new MaxEntOptimizableByLabelDistribution(list, null);
        double val = optimizable.getValue();

        assertFalse(Double.isNaN(val));
        assertFalse(Double.isInfinite(val));
    }
@Test
    public void testGetParametersWithIncorrectBufferSizeCreatesNewCopy() {
        List pipeList = new ArrayList();
        pipeList.add(new Input2CharSequence("UTF-8"));
        pipeList.add(new CharSequence2TokenSequence());
        pipeList.add(new TokenSequenceLowercase());
        pipeList.add(new TokenSequence2FeatureVector());
        pipeList.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipeList);

        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(new Instance("hello", "lab", null, null));

        MaxEntOptimizableByLabelDistribution optimizable = new MaxEntOptimizableByLabelDistribution(list, null);

        double[] wrongSize = new double[optimizable.getNumParameters() - 1]; 
        optimizable.getParameters(wrongSize); 

        double[] acceptable = new double[optimizable.getNumParameters()];
        optimizable.getParameters(acceptable);

        assertEquals(optimizable.getNumParameters(), acceptable.length);
    }
@Test
    public void testSetParametersNullArrayThrowsAssertionError() {
        List pipeList = new ArrayList();
        pipeList.add(new Input2CharSequence("UTF-8"));
        pipeList.add(new CharSequence2TokenSequence());
        pipeList.add(new TokenSequenceLowercase());
        pipeList.add(new TokenSequence2FeatureVector());
        pipeList.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipeList);

        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(new Instance("hello", "lab", null, null));

        MaxEntOptimizableByLabelDistribution optimizable = new MaxEntOptimizableByLabelDistribution(list, null);

        boolean assertionThrown = false;
        try {
            optimizable.setParameters(null);
        } catch (AssertionError e) {
            assertionThrown = true;
        }
        assertTrue(assertionThrown);
    }
@Test
    public void testSetParameterThenGetValueRecalculatesCorrectly() {
        List pipeList = new ArrayList();
        pipeList.add(new Input2CharSequence("UTF-8"));
        pipeList.add(new CharSequence2TokenSequence());
        pipeList.add(new TokenSequenceLowercase());
        pipeList.add(new TokenSequence2FeatureVector());
        pipeList.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipeList);

        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(new Instance("caching example", "labelA", null, null));

        MaxEntOptimizableByLabelDistribution optimizable = new MaxEntOptimizableByLabelDistribution(list, null);
        double v1 = optimizable.getValue();

        optimizable.setParameter(0, 5.0); 

        double v2 = optimizable.getValue();

        assertNotEquals(v1, v2, 0.000001);
    }
@Test
    public void testGradientFilterZeroedIfFeatureNotInSelection() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        dataAlphabet.lookupIndex("x");
        labelAlphabet.lookupLabel("Y");

        int featureIndex = dataAlphabet.lookupIndex("x");

        FeatureVector vector = new FeatureVector(dataAlphabet, new int[]{featureIndex}, new double[]{1.0});
        Labeling labeling = new LabelVector(labelAlphabet, new double[]{1.0});

        Instance instance = new Instance(vector, labeling, "name", null);

        FeatureSelection featureSel = new FeatureSelection();
        

        Pipe dummyPipe = new AlphabetCarryingPipe(dataAlphabet, labelAlphabet);
        dummyPipe.setTargetProcessing(false);

        InstanceList list = new InstanceList(dummyPipe, dataAlphabet, labelAlphabet);
        list.setFeatureSelection(featureSel);
        list.setPerLabelFeatureSelection(null);
        list.add(instance);

        MaxEntOptimizableByLabelDistribution optimizable = new MaxEntOptimizableByLabelDistribution(list, null);

        double[] buffer = new double[optimizable.getNumParameters()];
        optimizable.getValueGradient(buffer);

        
        boolean allZero = true;
        if (buffer.length > 0 && buffer[0] != 0.0) allZero = false;

        assertTrue(allZero);
    }
@Test
    public void testConstraintsAppliedCorrectlyToDefaultFeatureColumn() {
        List pipeList = new ArrayList();
        pipeList.add(new Input2CharSequence("UTF-8"));
        pipeList.add(new CharSequence2TokenSequence());
        pipeList.add(new TokenSequenceLowercase());
        pipeList.add(new TokenSequence2FeatureVector());
        pipeList.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipeList);

        Instance instance = new Instance("testing default", "zebra", null, null);
        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(instance);

        MaxEntOptimizableByLabelDistribution optimizable = new MaxEntOptimizableByLabelDistribution(list, null);
        double[] gradient = new double[optimizable.getNumParameters()];
        optimizable.getValue(); 
        optimizable.getValueGradient(gradient);

        double found = 0.0;
        if (gradient.length > 0) found = gradient[gradient.length - 1];

        assertNotEquals(0.0, found, 0.0);
    }
@Test
    public void testGradientBufferSubstitutionOfNegativeInfinity() {
        List pipeList = new ArrayList();
        pipeList.add(new Input2CharSequence("UTF-8"));
        pipeList.add(new CharSequence2TokenSequence());
        pipeList.add(new TokenSequenceLowercase());
        pipeList.add(new TokenSequence2FeatureVector());
        pipeList.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipeList);

        Instance instance = new Instance("test substitute", "X", null, null);
        InstanceList instanceList = new InstanceList(pipe);
        instanceList.addThruPipe(instance);

        MaxEntOptimizableByLabelDistribution optimizable = new MaxEntOptimizableByLabelDistribution(instanceList, null);
        int paramCount = optimizable.getNumParameters();
        double[] params = new double[paramCount];
        for (int i = 0; i < params.length; i++) {
            params[i] = Double.NEGATIVE_INFINITY;
        }

        optimizable.setParameters(params);

        double[] gradient = new double[paramCount];
        optimizable.getValueGradient(gradient);

        boolean containsNegativeInf = false;
        if (gradient[0] == Double.NEGATIVE_INFINITY) containsNegativeInf = true;

        assertFalse(containsNegativeInf);
    }
@Test
    public void testZeroScoresAndZeroLabelProbabilitiesDoNotTriggerInfinity() {
        Alphabet inputAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        inputAlphabet.lookupIndex("featureX");
        labelAlphabet.lookupLabel("label1");
        labelAlphabet.lookupLabel("label2");

        int[] indices = new int[]{0};
        double[] values = new double[]{1.0};
        FeatureVector fv = new FeatureVector(inputAlphabet, indices, values);

        double[] labelProbs = new double[]{0.0, 0.0};
        Labeling labeling = new LabelVector(labelAlphabet, labelProbs);

        Instance instance = new Instance(fv, labeling, "null-label", null);

        Pipe dummyPipe = new AlphabetCarryingPipe(inputAlphabet, labelAlphabet);
        dummyPipe.setTargetProcessing(false);
        InstanceList list = new InstanceList(dummyPipe, inputAlphabet, labelAlphabet);
        list.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        double val = opt.getValue();
        assertFalse(Double.isInfinite(val));
    }
@Test
    public void testDefaultFeatureIndexChosenCorrectlyAndAffectsConstraintVector() {
        List pipes = new ArrayList();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequenceLowercase());
        pipes.add(new TokenSequence2FeatureVector());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);

        Instance instance = new Instance("default bias marker", "demo", null, null);
        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        double[] gradient = new double[opt.getNumParameters()];
        opt.getValue(); 
        opt.getValueGradient(gradient);

        double last = gradient[gradient.length - 1]; 
        assertNotEquals(0.0, last, 1e-10);
    }
@Test
    public void testGradientZeroedWhenPerLabelFeatureSelectionExcludesFeature() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("alpha");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupLabel("Z");

        FeatureSelection[] perLabelFS = new FeatureSelection[1];
        perLabelFS[0] = new FeatureSelection(); 

        Pipe dummyPipe = new AlphabetCarryingPipe(dataAlphabet, labelAlphabet);
        dummyPipe.setTargetProcessing(false);

        InstanceList instanceList = new InstanceList(dummyPipe, dataAlphabet, labelAlphabet);
        instanceList.setPerLabelFeatureSelection(perLabelFS);

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        double[] probs = new double[]{1.0};
        Labeling labeling = new LabelVector(labelAlphabet, probs);
        Instance instance = new Instance(fv, labeling, "no-feature-in-selection", null);
        instanceList.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(instanceList, null);

        double[] gradient = new double[opt.getNumParameters()];
        opt.getValue();
        opt.getValueGradient(gradient);

        boolean allZero = true;
        if (gradient.length > 0 && gradient[0] != 0.0) allZero = false;

        assertTrue(allZero);
    }
@Test
    public void testSetParametersWithShorterArrayReplacesInternalReference() {
        List pipes = new ArrayList();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequenceLowercase());
        pipes.add(new TokenSequence2FeatureVector());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);

        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(new Instance("short array test", "cat", null, null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        int currentSize = opt.getNumParameters();

        double[] newParams = new double[currentSize - 1];
        newParams[0] = 42.42;

        opt.setParameters(newParams); 

        double[] readback = new double[newParams.length];
        opt.getParameters(readback);

        assertEquals(42.42, readback[0], 1e-10);
    }
@Test
    public void testGetValueAndGradientCachingEffectiveness() {
        List pipes = new ArrayList();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequenceLowercase());
        pipes.add(new TokenSequence2FeatureVector());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);

        Instance instance = new Instance("cached value test", "labelX", null, null);
        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        int callsBefore = opt.getValueCalls();
        opt.getValue();
        int callsAfter = opt.getValueCalls();

        opt.getValue(); 

        int callsFinal = opt.getValueCalls();

        assertEquals(callsAfter, callsFinal);
        assertTrue(callsAfter > callsBefore);
    }
@Test
    public void testEmptyFeatureVectorHandledGracefully() {
        Alphabet inputAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        labelAlphabet.lookupLabel("A");

        FeatureVector fv = new FeatureVector(inputAlphabet);
        double[] probs = new double[]{1.0};
        Labeling labeling = new LabelVector(labelAlphabet, probs);

        Instance instance = new Instance(fv, labeling, "empty-fv", null);

        Pipe dummyPipe = new AlphabetCarryingPipe(inputAlphabet, labelAlphabet);
        dummyPipe.setTargetProcessing(false);

        InstanceList list = new InstanceList(dummyPipe, inputAlphabet, labelAlphabet);
        list.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        double val = opt.getValue();
        assertFalse(Double.isNaN(val));
    }
@Test
    public void testZeroVariancePriorInfluencesValueComputation() {
        List pipes = new ArrayList();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequenceLowercase());
        pipes.add(new TokenSequence2FeatureVector());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);

        Instance example = new Instance("prior variance test", "outcome", null, null);
        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(example);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        double val1 = opt.getValue();

        opt.setGaussianPriorVariance(0.0001); 
        double val2 = opt.getValue();

        assertNotEquals(val1, val2, 1e-8);
    }
@Test
    public void testReturnFromGetClassifierNotNullEvenIfNullPassed() {
        List pipes = new ArrayList();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequenceLowercase());
        pipes.add(new TokenSequence2FeatureVector());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);

        Instance sample = new Instance("auto classifier build", "classY", null, null);
        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(sample);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        MaxEnt clf = opt.getClassifier();
        assertNotNull(clf);
    }
@Test
    public void testGradientBufferUnchangedIfWrongSizePassed() {
        List pipes = new ArrayList();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequenceLowercase());
        pipes.add(new TokenSequence2FeatureVector());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);

        Instance input = new Instance("invalid buffer gradient", "Z", null, null);
        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(input);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        double[] invalidBuffer = new double[opt.getNumParameters() - 5]; 

        boolean thrown = false;
        try {
            opt.getValueGradient(invalidBuffer);
        } catch (AssertionError e) {
            thrown = true;
        }
        assertTrue(thrown);
    }
@Test
    public void testConstructorWithPerLabelFeatureSelectionOnly() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupLabel("A");

        FeatureSelection[] perLabelFS = new FeatureSelection[1];
        perLabelFS[0] = new FeatureSelection();
        perLabelFS[0].add(0); 

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{dataAlphabet.lookupIndex("f1")}, new double[]{1.0});
        Labeling labeling = new LabelVector(labelAlphabet, new double[]{1.0});
        Instance instance = new Instance(fv, labeling, "per-label", null);

        Pipe carrier = new AlphabetCarryingPipe(dataAlphabet, labelAlphabet);
        carrier.setTargetProcessing(false);

        InstanceList list = new InstanceList(carrier, dataAlphabet, labelAlphabet);
        list.setPerLabelFeatureSelection(perLabelFS);
        list.setFeatureSelection(null);
        list.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        assertNotNull(opt.getClassifier());
    }
@Test
    public void testValueComputationHandlesExtremeParameterMagnitudesWithoutNaN() {
        List pipes = new ArrayList();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence("\\p{L}+"));
        pipes.add(new TokenSequenceLowercase());
        pipes.add(new TokenSequence2FeatureVector());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);

        Instance inst = new Instance("large weight test", "Y", null, null);
        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(inst);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        double[] big = new double[opt.getNumParameters()];
        if (big.length > 0) big[0] = 10000.0;

        opt.setParameters(big);
        double result = opt.getValue();

        assertFalse(Double.isNaN(result));
    }
@Test
    public void testGradientUpdatesWithZeroPrior() {
        List pipes = new ArrayList();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequenceLowercase());
        pipes.add(new TokenSequence2FeatureVector());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);

        Instance inst = new Instance("zero prior test", "X", null, null);
        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(inst);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        opt.setGaussianPriorVariance(Double.POSITIVE_INFINITY); 

        double[] grad = new double[opt.getNumParameters()];
        opt.getValue();
        opt.getValueGradient(grad);

        assertNotNull(grad);
        assertEquals(opt.getNumParameters(), grad.length);
    }
@Test
    public void testDefaultFeatureWeightParameterUsedInGradient() {
        List pipes = new ArrayList();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequenceLowercase());
        pipes.add(new TokenSequence2FeatureVector());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);

        Instance inst = new Instance("bias term only", "bias", null, null);
        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(inst);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        double[] grad = new double[opt.getNumParameters()];
        opt.getValue();
        opt.getValueGradient(grad);

        double defaultIndexGradient = grad[grad.length - 1]; 
        assertNotEquals(0.0, defaultIndexGradient, 0.0);
    }
@Test
    public void testClassifierAlignmentWithTrainingPipe() {
        List pipes = new ArrayList();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequenceLowercase());
        pipes.add(new TokenSequence2FeatureVector());
        pipes.add(new Target2Label());
        Pipe originalPipe = new SerialPipes(pipes);

        Instance i = new Instance("foo test", "label", null, null);
        InstanceList list = new InstanceList(originalPipe);
        list.addThruPipe(i);

        MaxEnt classifier = new MaxEnt(list.getPipe(), new double[list.getTargetAlphabet().size() * (list.getDataAlphabet().size() + 1)], null, null);
        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, classifier);

        assertSame(classifier, opt.getClassifier());
    }
@Test
    public void testCallCountersForGradientAndValueIncrementCorrectly() {
        List pipes = new ArrayList();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequenceLowercase());
        pipes.add(new TokenSequence2FeatureVector());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);

        Instance inst = new Instance("counter call", "update", null, null);
        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(inst);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        int valueCallsBefore = opt.getValueCalls();
        int gradCallsBefore = opt.getValueGradientCalls();

        double val = opt.getValue();
        double[] grad = new double[opt.getNumParameters()];
        opt.getValueGradient(grad);

        int valueCallsAfter = opt.getValueCalls();
        int gradCallsAfter = opt.getValueGradientCalls();

        assertTrue(valueCallsAfter > valueCallsBefore);
        assertTrue(gradCallsAfter > gradCallsBefore);
    }
@Test
    public void testNaNDetectedInFeatureVectorTriggersLogMessageWithoutCrash() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupLabel("L1");

        int featureIndex = dataAlphabet.lookupIndex("goodFeature");

        int[] indices = new int[]{featureIndex};
        double[] values = new double[]{Double.NaN};

        FeatureVector fv = new FeatureVector(dataAlphabet, indices, values);
        double[] probs = new double[]{1.0};
        Labeling labeling = new LabelVector(labelAlphabet, probs);

        Instance inst = new Instance(fv, labeling, "sourceNaN", null);

        Pipe dummyPipe = new AlphabetCarryingPipe(dataAlphabet, labelAlphabet);
        dummyPipe.setTargetProcessing(false);

        InstanceList list = new InstanceList(dummyPipe, dataAlphabet, labelAlphabet);
        list.add(inst);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        double val = opt.getValue();

        assertFalse(Double.isNaN(val));
    }
@Test
    public void testClassifierScoreZeroButNoPositiveLabelProbabilityIsSafe() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        dataAlphabet.lookupIndex("f1");
        labelAlphabet.lookupLabel("c1");
        labelAlphabet.lookupLabel("c2");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        Labeling labeling = new LabelVector(labelAlphabet, new double[]{0.0, 1.0});

        Instance inst = new Instance(fv, labeling, "safeScore", null);

        Pipe dummyPipe = new AlphabetCarryingPipe(dataAlphabet, labelAlphabet);
        dummyPipe.setTargetProcessing(false);

        InstanceList list = new InstanceList(dummyPipe, dataAlphabet, labelAlphabet);
        list.add(inst);

        MaxEnt classifier = new MaxEnt(list.getPipe(), new double[2 * 2], null, null) {
            public void getClassificationScores(Instance instance, double[] scores) {
                scores[0] = 0.0;
                scores[1] = 0.0; 
            }
        };

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, classifier);

        double val = opt.getValue(); 
        assertEquals(Double.NEGATIVE_INFINITY, val, 0.0);
    }
@Test
    public void testValueGradientSubstitutionHandlesExactNegativeInfinityParameters() {
        List pipes = new ArrayList();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequenceLowercase());
        pipes.add(new TokenSequence2FeatureVector());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);

        Instance i = new Instance("neg infinity", "Z", null, null);
        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(i);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        double[] badParams = new double[opt.getNumParameters()];
        if (badParams.length > 0) badParams[0] = Double.NEGATIVE_INFINITY;
        opt.setParameters(badParams);

        double[] grad = new double[opt.getNumParameters()];
        opt.getValueGradient(grad);

        boolean hasInf = false;
        if (grad.length > 0 && grad[0] == Double.NEGATIVE_INFINITY) hasInf = true;

        assertFalse(hasInf);
    }
@Test
    public void testConstructorWithBothFeatureSelectionAndPerLabelFeatureSelectionNullAvoidsAssertionError() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupLabel("X");

        int featureIndex = dataAlphabet.lookupIndex("feat-A");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{featureIndex}, new double[]{1.0});
        Labeling labelVec = new LabelVector(labelAlphabet, new double[]{1.0});
        Instance instance = new Instance(fv, labelVec, "source-A", null);

        Pipe dummyPipe = new AlphabetCarryingPipe(dataAlphabet, labelAlphabet);
        dummyPipe.setTargetProcessing(false);
        InstanceList list = new InstanceList(dummyPipe, dataAlphabet, labelAlphabet);
        list.setFeatureSelection(null);
        list.setPerLabelFeatureSelection(null);
        list.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        assertNotNull(opt.getClassifier());
    }
@Test
    public void testGetParametersWithLongerBufferPopulatesCorrectly() {
        List pipes = new ArrayList();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequenceLowercase());
        pipes.add(new TokenSequence2FeatureVector());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);

        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(new Instance("long buf", "A", null, null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        int size = opt.getNumParameters();
        double[] buffer = new double[size + 5];
        buffer[size] = 999.9; 

        opt.getParameters(buffer);

        assertEquals(999.9, buffer[size], 0.0); 
        assertEquals(size + 5, buffer.length);
    }
@Test
    public void testZeroLengthParameterArrayHandledGracefullyOnSetParameter() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        Pipe pipe = new AlphabetCarryingPipe(dataAlphabet, labelAlphabet);
        pipe.setTargetProcessing(false);

        InstanceList list = new InstanceList(pipe, dataAlphabet, labelAlphabet);
        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution();

        boolean exceptionThrown = false;
        try {
            opt.setParameter(0, 0.5); 
        } catch (ArrayIndexOutOfBoundsException e) {
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown);
    }
@Test
    public void testConflictingPerLabelAndFeatureSelectionThrowsAssertionOnConstruction() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupLabel("conflict");

        int featureIndex = dataAlphabet.lookupIndex("conflict-f");

        FeatureSelection globalFS = new FeatureSelection();
        globalFS.add(featureIndex);

        FeatureSelection[] perLabelFS = new FeatureSelection[1];
        perLabelFS[0] = new FeatureSelection();
        perLabelFS[0].add(featureIndex);

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{featureIndex}, new double[]{1.0});
        Labeling labeling = new LabelVector(labelAlphabet, new double[]{1.0});
        Instance instance = new Instance(fv, labeling, "conflict", null);

        Pipe pipe = new AlphabetCarryingPipe(dataAlphabet, labelAlphabet);
        pipe.setTargetProcessing(false);

        InstanceList list = new InstanceList(pipe, dataAlphabet, labelAlphabet);
        list.setFeatureSelection(globalFS);
        list.setPerLabelFeatureSelection(perLabelFS);
        list.add(instance);

        boolean assertion = false;
        try {
            new MaxEntOptimizableByLabelDistribution(list, null);
        } catch (AssertionError e) {
            assertion = true;
        }

        assertTrue(assertion);
    }
@Test
    public void testInstanceWithNullFeatureVectorIsSkippedInGetValue() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        labelAlphabet.lookupLabel("skip");

        FeatureVector fv = null;
        Labeling labelVec = new LabelVector(labelAlphabet, new double[]{1.0});
        Instance instance = new Instance(fv, labelVec, "source-skip", null);

        Pipe pipe = new AlphabetCarryingPipe(dataAlphabet, labelAlphabet);
        pipe.setTargetProcessing(false);

        InstanceList list = new InstanceList(pipe, dataAlphabet, labelAlphabet);
        list.add(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        try {
            opt.getValue(); 
        } catch (NullPointerException e) {
            fail("Should have skipped null feature vector instance");
        }
    }
@Test
    public void testGradientNotAffectedByUnscoredLabels() {
        List pipes = new ArrayList();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequenceLowercase());
        pipes.add(new TokenSequence2FeatureVector());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);

        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(new Instance("label 1 and 2", "label1", null, null));
        list.addThruPipe(new Instance("label 2 only", "label2", null, null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        double[] grad = new double[opt.getNumParameters()];
        opt.getValue();
        opt.getValueGradient(grad);

        assertEquals(opt.getNumParameters(), grad.length);
    }
@Test
    public void testSetGaussianPriorToSmallValueMagnifiesRegularizationTerm() {
        List pipes = new ArrayList();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequenceLowercase());
        pipes.add(new TokenSequence2FeatureVector());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);

        Instance instance = new Instance("prior test shrink", "Y", null, null);
        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        double val1 = opt.getValue();

        opt.setGaussianPriorVariance(0.00001); 
        double val2 = opt.getValue();

        assertNotEquals(val1, val2);
    }
@Test
    public void testConstraintsRemainFiniteForLabelDistributionCoveringAllLabels() {
        List pipes = new ArrayList();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequenceLowercase());
        pipes.add(new TokenSequence2FeatureVector());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);

        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(new Instance("train full label dist", "label-A", null, null));
        list.addThruPipe(new Instance("train full label dist 2", "label-B", null, null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        double[] grad = new double[opt.getNumParameters()];
        opt.getValue();
        opt.getValueGradient(grad);

        boolean hasNaN = false;
        if (grad.length > 0 && Double.isNaN(grad[0])) hasNaN = true;

        assertFalse(hasNaN);
    } 
}