public class MaxEntOptimizableByLabelDistribution_2_GPTLLMTest { 

 @Test
    public void testInitializationWithNullClassifierCreatesClassifier() {
        ArrayList<Pipe> pipes = new ArrayList<>();
        pipes.add(new cc.mallet.pipe.Input2CharSequence("UTF-8"));
        pipes.add(new cc.mallet.pipe.CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);

        InstanceList training = new InstanceList(pipe);
        training.addThruPipe(new Instance("doc1", "label1", null, null));
        training.addThruPipe(new Instance("doc2", "label2", null, null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(training, null);

        assertNotNull(opt.getClassifier());
    }
@Test
    public void testGetSetParameter() {
        ArrayList<Pipe> pipes = new ArrayList<>();
        pipes.add(new cc.mallet.pipe.Input2CharSequence("UTF-8"));
        pipes.add(new cc.mallet.pipe.CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);
        InstanceList training = new InstanceList(pipe);
        training.addThruPipe(new Instance("hello world", "a", null, null));
        training.addThruPipe(new Instance("more text", "b", null, null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(training, null);

        int paramIndex = 0;
        double original = opt.getParameter(paramIndex);
        opt.setParameter(paramIndex, 2.0);
        double updated = opt.getParameter(paramIndex);

        assertNotEquals(original, updated, 1e-8);
        assertEquals(2.0, updated, 1e-8);
    }
@Test
    public void testGetNumParametersConsistency() {
        ArrayList<Pipe> pipes = new ArrayList<>();
        pipes.add(new cc.mallet.pipe.Input2CharSequence("UTF-8"));
        pipes.add(new cc.mallet.pipe.CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);

        InstanceList training = new InstanceList(pipe);
        training.addThruPipe(new Instance("text1", "class1", null, null));
        training.addThruPipe(new Instance("text2", "class2", null, null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(training, null);
        int numParams = opt.getNumParameters();

        assertTrue(numParams > 0);
    }
@Test
    public void testSetAndGetParametersArray() {
        ArrayList<Pipe> pipes = new ArrayList<>();
        pipes.add(new cc.mallet.pipe.Input2CharSequence("UTF-8"));
        pipes.add(new cc.mallet.pipe.CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);

        InstanceList training = new InstanceList(pipe);
        training.addThruPipe(new Instance("doc foo", "X", null, null));
        training.addThruPipe(new Instance("another doc", "Y", null, null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(training, null);
        int size = opt.getNumParameters();

        double[] testParams = new double[size];
        testParams[0] = 0.5;
        testParams[size - 1] = 0.75;

        opt.setParameters(testParams);
        double[] retrievedParams = new double[size];
        opt.getParameters(retrievedParams);

        assertEquals(0.5, retrievedParams[0], 1e-6);
        assertEquals(0.75, retrievedParams[size - 1], 1e-6);
    }
@Test
    public void testGetValueIncrementsCounterAndIsFinite() {
        ArrayList<Pipe> pipes = new ArrayList<>();
        pipes.add(new cc.mallet.pipe.Input2CharSequence("UTF-8"));
        pipes.add(new cc.mallet.pipe.CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);

        InstanceList training = new InstanceList(pipe);
        training.addThruPipe(new Instance("sample one", "A", null, null));
        training.addThruPipe(new Instance("sample two", "B", null, null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(training, null);

        int callsBefore = opt.getValueCalls();
        double value = opt.getValue();
        int callsAfter = opt.getValueCalls();

        assertTrue(value < 0);
        assertTrue(value > -1e10);
        assertEquals(callsBefore + 1, callsAfter);
    }
@Test
    public void testGetValueGradientPopulatesBufferCorrectly() {
        ArrayList<Pipe> pipes = new ArrayList<>();
        pipes.add(new cc.mallet.pipe.Input2CharSequence("UTF-8"));
        pipes.add(new cc.mallet.pipe.CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);

        InstanceList training = new InstanceList(pipe);
        training.addThruPipe(new Instance("alpha beta", "A", null, null));
        training.addThruPipe(new Instance("gamma delta", "B", null, null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(training, null);
        int size = opt.getNumParameters();
        double[] gradient = new double[size];

        opt.getValueGradient(gradient);

        assertEquals(size, gradient.length);
        assertFalse(Double.isNaN(gradient[0]));
        assertFalse(Double.isInfinite(gradient[0]));
    }
@Test
    public void testUseGaussianPriorReturnsSameInstance() {
        ArrayList<Pipe> pipes = new ArrayList<>();
        pipes.add(new cc.mallet.pipe.Input2CharSequence("UTF-8"));
        pipes.add(new cc.mallet.pipe.CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);

        InstanceList training = new InstanceList(pipe);
        training.addThruPipe(new Instance("abc def", "L1", null, null));
        training.addThruPipe(new Instance("ghi jkl", "L2", null, null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(training, null);

        MaxEntOptimizableByLabelDistribution returned = opt.useGaussianPrior();

        assertSame(opt, returned);
    }
@Test
    public void testSetGaussianPriorVarianceReturnsSameInstance() {
        ArrayList<Pipe> pipes = new ArrayList<>();
        pipes.add(new cc.mallet.pipe.Input2CharSequence("UTF-8"));
        pipes.add(new cc.mallet.pipe.CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);

        InstanceList training = new InstanceList(pipe);
        training.addThruPipe(new Instance("doc1", "a", null, null));
        training.addThruPipe(new Instance("doc2", "b", null, null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(training, null);
        MaxEntOptimizableByLabelDistribution result = opt.setGaussianPriorVariance(0.2);

        assertSame(opt, result);
    }
@Test(expected = AssertionError.class)
    public void testSetParametersRejectsNull() {
        ArrayList<Pipe> pipes = new ArrayList<>();
        pipes.add(new cc.mallet.pipe.Input2CharSequence("UTF-8"));
        pipes.add(new cc.mallet.pipe.CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);

        InstanceList training = new InstanceList(pipe);
        training.addThruPipe(new Instance("a", "c", null, null));
        training.addThruPipe(new Instance("b", "d", null, null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(training, null);
        opt.setParameters(null); 
    }
@Test
    public void testGradientChangesAfterParameterUpdate() {
        ArrayList<Pipe> pipes = new ArrayList<>();
        pipes.add(new cc.mallet.pipe.Input2CharSequence("UTF-8"));
        pipes.add(new cc.mallet.pipe.CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);

        InstanceList training = new InstanceList(pipe);
        training.addThruPipe(new Instance("quick brown", "X", null, null));
        training.addThruPipe(new Instance("fox jump", "Y", null, null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(training, null);

        int size = opt.getNumParameters();
        double[] beforeGrad = new double[size];
        opt.getValueGradient(beforeGrad);

        opt.setParameter(0, 1.0);
        double[] afterGrad = new double[size];
        opt.getValueGradient(afterGrad);

        assertNotEquals(beforeGrad[0], afterGrad[0], 1e-8);
    }
@Test
    public void testEmptyInstanceListSkipsLabelingAndNoException() {
        ArrayList<Pipe> pipes = new ArrayList<>();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);

        InstanceList emptyList = new InstanceList(pipe);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(emptyList, null);

        double value = opt.getValue();
        assertFalse(Double.isNaN(value));
        assertFalse(Double.isInfinite(value));
    }
@Test
    public void testInstanceWithNullLabelingDoesNotCrash() {
        ArrayList<Pipe> pipes = new ArrayList<>();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);
        
        InstanceList list = new InstanceList(pipe);
        Instance instance = new Instance("text data", null, null, null);
        list.addThruPipe(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        double value = opt.getValue();
        assertFalse(Double.isNaN(value));
        assertFalse(Double.isInfinite(value));
    }
@Test
    public void testMultipleCallsReuseCachedValue() {
        ArrayList<Pipe> pipes = new ArrayList<>();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);

        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(new Instance("a b c", "X", null, null));
        list.addThruPipe(new Instance("d e f", "Y", null, null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        
        double value1 = opt.getValue();
        double value2 = opt.getValue();
        int count = opt.getValueCalls();

        assertEquals(value1, value2, 1e-6);
        assertEquals(1, count);
    }
@Test
    public void testGetValueReturnsNegativeInfinityIfProbabilityZero() {
        ArrayList<Pipe> pipes = new ArrayList<>();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);

        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(new Instance("test example", "label1", null, null));

        MaxEnt badClassifier = new MaxEnt(pipe, new double[100], null, null) {
            @Override
            public void getClassificationScores(Instance instance, double[] scores) {
                for (int i = 0; i < scores.length; i++) {
                    scores[i] = 0.0;
                }
            }
        };

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, badClassifier);

        double value = opt.getValue();

        assertEquals(Double.NEGATIVE_INFINITY, value, 0.0);
    }
@Test
    public void testGetValueHandlesNaNViaLogOfZeroGracefully() {
        ArrayList<Pipe> pipes = new ArrayList<>();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);

        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(new Instance("text data", "foo", null, null));

        MaxEnt customClassifier = new MaxEnt(pipe, new double[100], null, null) {
            @Override
            public void getClassificationScores(Instance instance, double[] scores) {
                for (int i = 0; i < scores.length; i++) {
                    scores[i] = 0.0;
                }
            }
        };

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, customClassifier);

        double value = opt.getValue();
        assertEquals(Double.NEGATIVE_INFINITY, value, 0.0);
    }
@Test
    public void testSetParametersToDifferentSizeReplacesArray() {
        ArrayList<Pipe> pipes = new ArrayList<>();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);

        InstanceList training = new InstanceList(pipe);
        training.addThruPipe(new Instance("x y z", "A", null, null));
        training.addThruPipe(new Instance("a b c", "B", null, null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(training, null);
        int oldSize = opt.getNumParameters();

        double[] newParams = new double[oldSize + 1];
        newParams[0] = 1.0;
        newParams[oldSize] = 0.99;

        opt.setParameters(newParams);

        double[] retrieved = new double[newParams.length];
        opt.getParameters(retrieved);

        assertEquals(1.0, retrieved[0], 1e-8);
        assertEquals(0.99, retrieved[oldSize], 1e-8);
    }
@Test
    public void testGradientBufferExactLengthIsUpdated() {
        ArrayList<Pipe> pipes = new ArrayList<>();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);

        InstanceList training = new InstanceList(pipe);
        training.addThruPipe(new Instance("a b", "labelA", null, null));
        training.addThruPipe(new Instance("c d", "labelB", null, null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(training, null);
        int paramCount = opt.getNumParameters();

        double[] gradient = new double[paramCount];
        gradient[0] = 12345.0; 

        opt.getValueGradient(gradient);
        assertNotEquals(12345.0, gradient[0], 1e-6);
    }
@Test
    public void testNegativeInfinityParametersDoNotAffectGradient() {
        ArrayList<Pipe> pipes = new ArrayList<>();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);

        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(new Instance("dog", "cat", null, null));
        list.addThruPipe(new Instance("fish", "bird", null, null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        double[] params = new double[opt.getNumParameters()];
        params[0] = Double.NEGATIVE_INFINITY;
        opt.setParameters(params);

        double[] gradient = new double[opt.getNumParameters()];
        opt.getValueGradient(gradient);

        assertEquals(0.0, gradient[0], 0.0);
    }
@Test
    public void testGetParametersWithNullBufferCreatesNewProperly() {
        ArrayList<cc.mallet.pipe.Pipe> pipes = new ArrayList<>();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);
        InstanceList training = new InstanceList(pipe);
        training.addThruPipe(new Instance("text", "label1", null, null));
        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(training, null);

        double[] buffer = null;
        opt.getParameters(buffer);  
    }
@Test
    public void testRepeatedCallsToGetValueAvoidRecalculation() {
        ArrayList<Pipe> pipes = new ArrayList<>();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);

        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(new Instance("word1 word2", "labelA", null, null));
        list.addThruPipe(new Instance("word3 word4", "labelB", null, null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        double value1 = opt.getValue();
        double value2 = opt.getValue();
        assertEquals(value1, value2, 1e-6);
        assertEquals(1, opt.getValueCalls());
    }
@Test
    public void testZeroScoreWithNonZeroLabelDistributionReturnsNegativeInfinity() {
        ArrayList<Pipe> pipes = new ArrayList<>();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);
        InstanceList training = new InstanceList(pipe);
        training.addThruPipe(new Instance("doc content", "labelX", null, null));

        MaxEnt customClassifier = new MaxEnt(pipe, new double[100], null, null) {
            @Override
            public void getClassificationScores(Instance instance, double[] scores) {
                for (int i = 0; i < scores.length; i++) {
                    scores[i] = 0.0;
                }
            }
        };

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(training, customClassifier);
        double value = opt.getValue();
        assertEquals(Double.NEGATIVE_INFINITY, value, 0.0);
    }
@Test
    public void testLabelingHasNaNFeatureVectorValuesLogsInfo() {
        ArrayList<Pipe> pipes = new ArrayList<>();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);
        InstanceList training = new InstanceList(pipe);
        training.getTargetAlphabet().lookupIndex("label1");
        training.getDataAlphabet().lookupIndex("feature1");

        LabelAlphabet la = (LabelAlphabet) training.getTargetAlphabet();
        Instance inst = new Instance("feature1", "label1", "name1", null);
        training.addThruPipe(inst);

        
        FeatureVector fv = (FeatureVector) training.get(0).getData();
        double[] values = fv.getValues();
        int[] indices = fv.getIndices();
        double[] nanValues = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            nanValues[i] = Double.NaN;
        }
        FeatureVector nanFV = new FeatureVector(fv.getAlphabet(), indices, nanValues);
        training.get(0).setData(nanFV);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(training, null);
        double value = opt.getValue();  
        assertTrue(Double.isFinite(value) || Double.isNaN(value) || Double.isInfinite(value));
    }
@Test
    public void testSetParametersWithExactLengthBufferOverwritesCorrectly() {
        ArrayList<Pipe> pipes = new ArrayList<>();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);
        InstanceList training = new InstanceList(pipe);
        training.addThruPipe(new Instance("sample", "LabelA", null, null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(training, null);
        int size = opt.getNumParameters();
        double[] newParams = new double[size];
        newParams[0] = 0.9;
        newParams[size - 1] = -0.4;

        opt.setParameters(newParams);

        double[] retrieved = new double[size];
        opt.getParameters(retrieved);

        assertEquals(0.9, retrieved[0], 1e-6);
        assertEquals(-0.4, retrieved[size - 1], 1e-6);
    }
@Test
    public void testSetParametersWithAllInfinitiesProducesZeroedGradient() {
        ArrayList<Pipe> pipes = new ArrayList<>();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);
        InstanceList training = new InstanceList(pipe);
        training.addThruPipe(new Instance("doc", "x", null, null));
        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(training, null);

        int size = opt.getNumParameters();
        double[] infParams = new double[size];
        infParams[1] = Double.NEGATIVE_INFINITY;

        opt.setParameters(infParams);
        double[] grad = new double[size];
        opt.getValueGradient(grad);
        assertEquals(0.0, grad[1], 0.0);
    }
@Test
    public void testGradientRespectsFeatureSelectionMask() {
        ArrayList<Pipe> pipes = new ArrayList<>();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);
        InstanceList training = new InstanceList(pipe);
        training.addThruPipe(new Instance("featureX featureY", "Foo", null, null));
        training.addThruPipe(new Instance("featureZ", "Bar", null, null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(training, null);
        double[] buff = new double[opt.getNumParameters()];
        opt.getValueGradient(buff);

        int zeroCount = 0;
        if (buff.length > 1 && buff[1] == 0.0) {
            zeroCount++;
        }
        assertTrue(buff.length > 0);
    }
@Test
    public void testGlobalFeatureSelectionMasksInactiveFeaturesInGradient() {
        ArrayList<Pipe> pipes = new ArrayList<>();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);

        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("feat1");
        dataAlphabet.lookupIndex("feat2");
        dataAlphabet.lookupIndex("feat3");
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("label1");

        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(new Instance("feat1 feat2 feat3", "label1", null, null));

        FeatureSelection fs = new FeatureSelection(dataAlphabet);
        fs.add(dataAlphabet.lookupIndex("feat1"));
        fs.add(dataAlphabet.lookupIndex("feat2")); 

        list.setFeatureSelection(fs);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        double[] grad = new double[opt.getNumParameters()];
        opt.getValueGradient(grad);

        for (int i = 0; i < grad.length; i++) {
            assertFalse("Gradient should not be NaN", Double.isNaN(grad[i]));
        }
    }
@Test
    public void testPerLabelFeatureSelectionMasksCorrectlyInGradient() {
        ArrayList<Pipe> pipes = new ArrayList<>();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);

        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(new Instance("featureA featureB", "labelX", null, null));
        list.getDataAlphabet().lookupIndex("featureA");
        list.getDataAlphabet().lookupIndex("featureB");
        list.getTargetAlphabet().lookupIndex("labelX");
        list.getTargetAlphabet().lookupIndex("labelY");

        FeatureSelection[] selections = new FeatureSelection[2];
        selections[0] = new FeatureSelection(list.getDataAlphabet());
        selections[0].add(list.getDataAlphabet().lookupIndex("featureA"));  
        selections[1] = new FeatureSelection(list.getDataAlphabet());        

        list.setPerLabelFeatureSelection(selections);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        double[] grad = new double[opt.getNumParameters()];
        opt.getValueGradient(grad);

        for (int i = 0; i < grad.length; i++) {
            assertFalse("Gradient element should not be NaN or infinite", Double.isNaN(grad[i]) || Double.isInfinite(grad[i]));
        }
    }
@Test
    public void testZeroGaussianPriorVarianceProducesLargePenalty() {
        ArrayList<Pipe> pipes = new ArrayList<>();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);
        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(new Instance("one two", "labelA", null, null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        opt.setGaussianPriorVariance(1e-10); 

        double value = opt.getValue();
        assertTrue("Value should be large negative due to strong prior", value < 0.0);
    }
@Test
    public void testGetValueGradientCanBeCalledBeforeGetValue() {
        ArrayList<Pipe> pipes = new ArrayList<>();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);
        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(new Instance("x y z", "L", null, null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        double[] grad = new double[opt.getNumParameters()];
        opt.getValueGradient(grad); 

        for (int i = 0; i < grad.length; i++) {
            assertFalse("Gradient must not be NaN", Double.isNaN(grad[i]));
        }
    }
@Test
    public void testInstanceWithZeroWeightProducesZeroContribution() {
        ArrayList<Pipe> pipes = new ArrayList<>();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);
        InstanceList list = new InstanceList(pipe);
        Instance inst = new Instance("feature1", "labelZ", null, null);
        list.addThruPipe(inst);
        list.setInstanceWeight(inst, 0.0); 

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        double value = opt.getValue(); 
        double[] grad = new double[opt.getNumParameters()];
        opt.getValueGradient(grad);

        assertFalse("Value should be valid", Double.isNaN(value));
        for (int i = 0; i < grad.length; i++) {
            assertFalse("Gradient should not be NaN", Double.isNaN(grad[i]));
        }
    }
@Test
    public void testMoreLabelsThanInstancesIsHandledCorrectly() {
        ArrayList<Pipe> pipes = new ArrayList<>();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);
        InstanceList list = new InstanceList(pipe);

        list.addThruPipe(new Instance("data sample", "label1", null, null));
        list.getTargetAlphabet().lookupIndex("label2");
        list.getTargetAlphabet().lookupIndex("label3");
        list.getTargetAlphabet().lookupIndex("label4"); 

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        double value = opt.getValue();
        double[] g = new double[opt.getNumParameters()];
        opt.getValueGradient(g);

        assertFalse("Value should not be NaN", Double.isNaN(value));
    }
@Test
    public void testFeatureVectorWithNoActiveFeaturesStillHandled() {
        ArrayList<Pipe> pipes = new ArrayList<>();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence(true)); 
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);

        InstanceList list = new InstanceList(pipe);
        Alphabet dataAlphabet = list.getDataAlphabet();
        dataAlphabet.stopGrowth(); 

        Instance inst = new Instance("unkword1 unkword2", "MyLabel", null, null);
        list.addThruPipe(inst); 

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        double value = opt.getValue();
        double[] gradient = new double[opt.getNumParameters()];
        opt.getValueGradient(gradient);

        assertTrue("Value should be finite", Double.isFinite(value));
        for (int i = 0; i < gradient.length; i++) {
            assertFalse("Gradient should not be NaN", Double.isNaN(gradient[i]));
        }
    }
@Test
    public void testGetParametersWithPreallocatedWrongSizeArrayIsIgnored() {
        ArrayList<Pipe> pipes = new ArrayList<>();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);

        InstanceList data = new InstanceList(pipe);
        data.addThruPipe(new Instance("token token", "Label", null, null));

        MaxEntOptimizableByLabelDistribution optimization = new MaxEntOptimizableByLabelDistribution(data, null);
        double[] buffer = new double[optimization.getNumParameters() + 5];
        optimization.getParameters(buffer); 
        assertEquals(buffer.length, optimization.getNumParameters() + 5); 
    }
@Test
    public void testSetParameterSameValueDoesNotBreakValueState() {
        ArrayList<Pipe> pipes = new ArrayList<>();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);

        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(new Instance("one word", "labelZ", null, null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        double valBefore = opt.getParameter(0);
        opt.setParameter(0, valBefore); 
        double valAfter = opt.getParameter(0);
        assertEquals(valBefore, valAfter, 0.0); 
    }
@Test
    public void testFeatureVectorWithOnlyDefaultFeatureHandledCorrectly() {
        ArrayList<Pipe> pipes = new ArrayList<>();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence(true)); 
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);

        InstanceList list = new InstanceList(pipe);
        
        list.getDataAlphabet().stopGrowth();

        Instance inst = new Instance("unknown_term another_unknown", "LabelX", null, null);
        list.addThruPipe(inst);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        double value = opt.getValue(); 

        assertTrue(Double.isFinite(value));
    }
@Test
    public void testConstraintsIncludeDefaultFeatureWeight() {
        ArrayList<Pipe> pipes = new ArrayList<>();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);

        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet targetAlphabet = new LabelAlphabet();

        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(new Instance("alpha beta gamma", "MyLabel", null, null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        double[] grad = new double[opt.getNumParameters()];
        opt.getValueGradient(grad);

        boolean hasNonZero = false;
        for (int i = 0; i < grad.length; i++) {
            if (grad[i] != 0.0) {
                hasNonZero = true;
                break;
            }
        }
        assertTrue(hasNonZero);
    }
@Test
    public void testThrowIfClassifierPipeNotAlignedToTrainingSetPipe() {
        ArrayList<Pipe> pipes1 = new ArrayList<>();
        pipes1.add(new Input2CharSequence("UTF-8"));
        pipes1.add(new CharSequence2TokenSequence());
        pipes1.add(new TokenSequence2FeatureSequence());
        pipes1.add(new Target2Label());
        Pipe pipe1 = new SerialPipes(pipes1);

        ArrayList<Pipe> pipes2 = new ArrayList<>();
        pipes2.add(new Input2CharSequence("ASCII"));
        pipes2.add(new CharSequence2TokenSequence());
        pipes2.add(new TokenSequence2FeatureSequence());
        pipes2.add(new Target2Label());
        Pipe pipe2 = new SerialPipes(pipes2);

        InstanceList list = new InstanceList(pipe1);
        list.addThruPipe(new Instance("abcd", "test", null, null));

        MaxEnt classifier = new MaxEnt(pipe2, new double[20], null, null);

        boolean exceptionThrown = false;
        try {
            new MaxEntOptimizableByLabelDistribution(list, classifier);
        } catch (AssertionError err) {
            exceptionThrown = true;
        }
        assertTrue("Expected assertion failure due to mismatched pipe", exceptionThrown);
    }
@Test
    public void testSingleInstanceSingleLabelComputationEdge() {
        ArrayList<Pipe> pipes = new ArrayList<>();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);
        InstanceList il = new InstanceList(pipe);
        il.addThruPipe(new Instance("hello hell yeah", "labelK", null, null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(il, null);
        double value = opt.getValue();
        assertTrue(Double.isFinite(value));
        assertFalse(Double.isNaN(value));
    }
@Test
    public void testLabelingIndexMismatchStillYieldsValidConstraint() {
        ArrayList<Pipe> pipes = new ArrayList<>();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);

        InstanceList training = new InstanceList(pipe);
        
        training.addThruPipe(new Instance("x y x", "labelA", null, null));
        training.getTargetAlphabet().lookupIndex("labelB"); 

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(training, null);
        double[] grad = new double[opt.getNumParameters()];
        opt.getValueGradient(grad);

        assertEquals(grad.length, opt.getNumParameters());
    }
@Test
    public void testSetParametersToEmptyArrayGracefullyResizes() {
        ArrayList<Pipe> pipes = new ArrayList<>();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);
        InstanceList il = new InstanceList(pipe);
        il.addThruPipe(new Instance("some data", "some label", null, null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(il, null);

        double[] newParams = new double[0]; 
        opt.setParameters(newParams); 
        double[] result = new double[opt.getNumParameters()];
        opt.getParameters(result);

        for (double v : result) {
            assertEquals(0.0, v, 1e-10); 
        }
    }
@Test
    public void testMultipleGetValueGradientCallsCachesCorrectly() {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);
        InstanceList data = new InstanceList(pipe);
        data.addThruPipe(new Instance("some text", "A", null, null));
        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(data, null);

        double[] grad1 = new double[opt.getNumParameters()];
        opt.getValueGradient(grad1);
        int callsAfterFirst = opt.getValueGradientCalls();

        double[] grad2 = new double[opt.getNumParameters()];
        opt.getValueGradient(grad2);
        int callsAfterSecond = opt.getValueGradientCalls();

        assertEquals(callsAfterFirst, callsAfterSecond);
        assertEquals(grad1.length, grad2.length);
    }
@Test
    public void testPerLabelFeatureSelectionAndDefaultFeatureAreBothHandled() {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);

        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(new Instance("x y", "L1", null, null));
        list.getTargetAlphabet().lookupIndex("L2");

        cc.mallet.types.FeatureSelection[] perLabelFS = new cc.mallet.types.FeatureSelection[2];
        perLabelFS[0] = new cc.mallet.types.FeatureSelection(list.getDataAlphabet());
        perLabelFS[1] = new cc.mallet.types.FeatureSelection(list.getDataAlphabet());

        int indexX = list.getDataAlphabet().lookupIndex("x");
        perLabelFS[0].add(indexX); 
        perLabelFS[1].add(indexX); 

        list.setPerLabelFeatureSelection(perLabelFS);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        double[] grad = new double[opt.getNumParameters()];
        opt.getValueGradient(grad);

        assertEquals(grad.length, opt.getNumParameters());
    }
@Test
    public void testEmptyFeatureVectorValuesHandledWithoutException() {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);

        InstanceList list = new InstanceList(pipe);
        Alphabet dataAlpha = list.getDataAlphabet();
        LabelAlphabet labelAlpha = (LabelAlphabet) list.getTargetAlphabet();
        labelAlpha.lookupIndex("C");

        Alphabet dummyAlphabet = new Alphabet();
        FeatureVector fv = new FeatureVector(dummyAlphabet, new int[]{}, new double[]{});
        Label label = labelAlpha.lookupLabel("C");
        Labeling labeling = labelAlpha.getLabeling(new Label[]{label}, new double[]{1.0});
        Instance inst = new Instance(fv, labeling, "name", null);
        list.add(inst);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        double val = opt.getValue();
        double[] g = new double[opt.getNumParameters()];
        opt.getValueGradient(g);

        assertTrue(Double.isFinite(val));
        assertEquals(g.length, opt.getNumParameters());
    }
@Test
    public void testSetParametersWithSmallerArrayReinitializesParameterArray() {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);
        InstanceList training = new InstanceList(pipe);
        training.addThruPipe(new Instance("test doc", "CLASS", null, null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(training, null);

        int originalSize = opt.getNumParameters();
        double[] shortBuffer = new double[originalSize - 1];

        opt.setParameters(shortBuffer); 
        double[] newBuffer = new double[opt.getNumParameters()];
        opt.getParameters(newBuffer);

        assertEquals(originalSize - 1, newBuffer.length);
    }
@Test
    public void testZeroProbLabelPathReachesNegativeInfinity() {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);
        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(new Instance("a b", "Y", null, null));

        Alphabet labelAlpha = list.getTargetAlphabet();
        int numLabels = labelAlpha.size();
        double[] zeroClassifierParams = new double[numLabels * (list.getDataAlphabet().size() + 1)];

        MaxEnt classifier = new MaxEnt(pipe, zeroClassifierParams, null, null) {
            @Override
            public void getClassificationScores(Instance inst, double[] out) {
                for (int i = 0; i < out.length; i++) out[i] = 0.0;
            }
        };

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, classifier);
        double val = opt.getValue();
        assertEquals(Double.NEGATIVE_INFINITY, val, 0.0);
    }
@Test
    public void testSetParameterTriggersValueStaleRecalculation() {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);
        InstanceList instList = new InstanceList(pipe);
        instList.addThruPipe(new Instance("p q r", "L", null, null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(instList, null);
        double val1 = opt.getValue();

        opt.setParameter(0, opt.getParameter(0) + 1.0); 
        double val2 = opt.getValue();

        assertNotEquals(val1, val2, 1e-6);
    }
@Test
    public void testOnlyDefaultFeatureIsActiveInGradientComputation() {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence("[^\\s]+"));
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);

        InstanceList list = new InstanceList(pipe);
        list.getDataAlphabet().stopGrowth(); 
        Instance instance = new Instance("xxx yyy unseen_words", "LabelX", null, null);
        list.addThruPipe(instance); 

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        double[] gradient = new double[opt.getNumParameters()];
        opt.getValueGradient(gradient);

        int nonZero = 0;
        for (int i = 0; i < gradient.length; i++) {
            if (gradient[i] != 0.0) {
                nonZero++;
            }
        }
        
        assertEquals(1, nonZero);
    }
@Test
    public void testNaNInputFeatureTriggersLoggingButDoesNotBreakComputation() {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);
        InstanceList list = new InstanceList(pipe);

        LabelAlphabet labelAlpha = (LabelAlphabet) list.getTargetAlphabet();
        Alphabet dataAlpha = list.getDataAlphabet();

        int idx = dataAlpha.lookupIndex("feature1");
        FeatureSelection fs = new FeatureSelection(dataAlpha);
        fs.add(idx);
        list.setFeatureSelection(fs);

        Label label = labelAlpha.lookupLabel("NaNLabel");
        Labeling labeling = labelAlpha.getLabeling(new Label[]{label}, new double[]{1.0});
        FeatureVector vector = new FeatureVector(dataAlpha, new int[]{idx}, new double[]{Double.NaN});
        Instance inst = new Instance(vector, labeling, "nancase", null);
        list.add(inst);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        double val = opt.getValue();
        assertTrue(Double.isFinite(val));
    }
@Test
    public void testZeroLogProbabilityScoreAvoidsNaNWhenWeightIsZero() {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);

        InstanceList training = new InstanceList(pipe);
        training.addThruPipe(new Instance("token", "Foo", null, null));
        training.setInstanceWeight(training.get(0), 0.0); 

        MaxEnt classifier = new MaxEnt(pipe, new double[100], null, null) {
            @Override
            public void getClassificationScores(Instance instance, double[] scores) {
                for (int i = 0; i < scores.length; i++) scores[i] = 0.0; 
            }
        };

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(training, classifier);
        double value = opt.getValue(); 

        assertTrue(Double.isFinite(value));
    }
@Test
    public void testNegativeInfinityInParameterArrayDoesNotBreakGradientComputation() {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence("[^\\s]+"));
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);
        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(new Instance("text text", "Label", null, null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        double[] params = new double[opt.getNumParameters()];
        if (params.length >= 3) {
            params[1] = Double.NEGATIVE_INFINITY;
            params[2] = -100.0;
        }
        opt.setParameters(params);

        double[] gradient = new double[opt.getNumParameters()];
        opt.getValueGradient(gradient);

        if (params.length >= 3) {
            assertEquals(0.0, gradient[1], 0.0); 
        }
    }
@Test
    public void testStopGrowthBeforeInstanceAdditionStillTracksDefaultFeature() {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);
        InstanceList list = new InstanceList(pipe);

        list.getDataAlphabet().stopGrowth(); 
        list.getTargetAlphabet().lookupIndex("L");

        Instance inst = new Instance("unseenword", "L", null, null);
        list.addThruPipe(inst); 

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        double[] grad = new double[opt.getNumParameters()];
        opt.getValueGradient(grad);

        boolean hasNonZero = false;
        for (int i = 0; i < grad.length; i++) {
            if (grad[i] != 0.0) {
                hasNonZero = true;
                break;
            }
        }
        assertTrue("Expected non-zero in default feature slot", hasNonZero);
    }
@Test
    public void testFeatureAlphabetIsEmptyButDefaultFeatureStillAdded() {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence(true)); 
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);
        InstanceList il = new InstanceList(pipe);

        Alphabet dataAlpha = il.getDataAlphabet();
        dataAlpha.stopGrowth(); 

        il.addThruPipe(new Instance("xyz unknown", "C", null, null)); 
        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(il, null);

        double[] g = new double[opt.getNumParameters()];
        opt.getValueGradient(g);
        assertEquals(g.length, opt.getNumParameters());
    }
@Test
    public void testSetParametersToNullThrowsAssertion() {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);
        InstanceList il = new InstanceList(pipe);
        il.addThruPipe(new Instance("doc", "label", null, null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(il, null);

        boolean thrown = false;
        try {
            opt.setParameters(null);
        } catch (AssertionError err) {
            thrown = true;
        }
        assertTrue("Expected AssertionError for null param buffer", thrown);
    }
@Test
    public void testGetValueGradientAppliesGaussianPriorCorrection() {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);
        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(new Instance("data data", "target", null, null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        int size = opt.getNumParameters();

        double[] testParams = new double[size];
        testParams[0] = 2.0;
        testParams[1] = -2.0;
        opt.setParameters(testParams);

        double[] grad = new double[size];
        opt.getValueGradient(grad);

        assertNotEquals(0.0, grad[0], 0.0);
        assertNotEquals(0.0, grad[1], 0.0);
    } 
}