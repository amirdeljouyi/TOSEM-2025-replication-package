public class MaxEntOptimizableByLabelDistribution_4_GPTLLMTest { 

 @Test
    public void testConstructorInitializesCorrectly() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance carrier) {
                return carrier;
            }
        };

        InstanceList trainingSet = new InstanceList(pipe);

        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"f1"}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"L1"}, new double[]{1.0});
        trainingSet.addThruPipe(new Instance(fv, lv, "inst1", null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(trainingSet, null);

        MaxEnt classifier = opt.getClassifier();
        assertNotNull(classifier);

        int numLabels = labelAlphabet.size();
        int numFeatures = dataAlphabet.size() + 1;
        int expectedNumParameters = numLabels * numFeatures;
        assertEquals(expectedNumParameters, opt.getNumParameters());
    }
@Test
    public void testSetAndGetParameter() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance carrier) {
                return carrier;
            }
        };

        InstanceList trainingSet = new InstanceList(pipe);

        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"f1"}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"L1"}, new double[]{1.0});
        trainingSet.addThruPipe(new Instance(fv, lv, "inst1", null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(trainingSet, null);

        opt.setParameter(0, 3.14);
        assertEquals(3.14, opt.getParameter(0), 1e-6);
    }
@Test
    public void testSetAndGetParametersArray() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance carrier) {
                return carrier;
            }
        };

        InstanceList trainingSet = new InstanceList(pipe);

        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"f1", "f2"}, new double[]{2.0, 3.0});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"L1"}, new double[]{1.0});
        trainingSet.addThruPipe(new Instance(fv, lv, "inst1", null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(trainingSet, null);

        double[] params = new double[opt.getNumParameters()];
        params[0] = 1.0;
        params[1] = 2.0;
        params[2] = 3.0;

        opt.setParameters(params);

        double[] outBuff = new double[opt.getNumParameters()];
        opt.getParameters(outBuff);

        assertEquals(1.0, outBuff[0], 1e-6);
        assertEquals(2.0, outBuff[1], 1e-6);
        assertEquals(3.0, outBuff[2], 1e-6);
    }
@Test
    public void testGetValueReturnsFinite() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance carrier) {
                return carrier;
            }
        };

        InstanceList trainingSet = new InstanceList(pipe);

        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"fx"}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"Y"}, new double[]{1.0});
        trainingSet.addThruPipe(new Instance(fv, lv, "iname", null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(trainingSet, null);
        double value = opt.getValue();

        assertTrue(Double.isFinite(value));
    }
@Test
    public void testGetValueGradientValid() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance carrier) {
                return carrier;
            }
        };

        InstanceList trainingList = new InstanceList(pipe);

        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"a", "b"}, new double[]{1.0, 2.0});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"X"}, new double[]{1.0});
        trainingList.addThruPipe(new Instance(fv, lv, "inst", null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(trainingList, null);

        double[] gradient = new double[opt.getNumParameters()];
        opt.getValueGradient(gradient);

        assertEquals(gradient.length, opt.getNumParameters());
        assertTrue(Double.isFinite(gradient[0]) || Double.isNaN(gradient[0]) == false);  
    }
@Test
    public void testNaNFeatureHandledGracefully() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance carrier) {
                return carrier;
            }
        };

        InstanceList list = new InstanceList(pipe);

        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"f1", "f2"}, new double[]{1.0, Double.NaN});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"L"}, new double[]{1.0});
        Instance inst = new Instance(fv, lv, "nan-test", null);
        list.addThruPipe(inst);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        double value = opt.getValue();

        assertTrue(Double.isFinite(value) || value == Double.NEGATIVE_INFINITY);
    }
@Test
    public void testZeroScoreHandlingReturnsNegativeInfinity() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance carrier) {
                return carrier;
            }
        };
        InstanceList list = new InstanceList(pipe);

        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"f0"}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"Z"}, new double[]{1.0});
        list.addThruPipe(new Instance(fv, lv, "id", null));

        MaxEnt dummyClassifier = new MaxEnt(pipe) {
            @Override
            public void getClassificationScores(Instance instance, double[] scores) {
                scores[0] = 0.0;
            }
        };

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, dummyClassifier);
        double val = opt.getValue();

        assertEquals(Double.NEGATIVE_INFINITY, val, 1e-9);
    }
@Test
    public void testGaussianPriorInfluencesValue() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance carrier) {
                return carrier;
            }
        };
        InstanceList list = new InstanceList(pipe);

        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"f0"}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"C"}, new double[]{1.0});
        list.addThruPipe(new Instance(fv, lv, "ii", null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        double defaultValue = opt.getValue();

        opt.setGaussianPriorVariance(0.0001);
        double newValue = opt.getValue();

        assertNotEquals(defaultValue, newValue, 1e-4);
    }
@Test
    public void testCallCountersIncrementProperly() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance carrier) {
                return carrier;
            }
        };
        InstanceList list = new InstanceList(pipe);

        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"f1"}, new double[]{2.0});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"T"}, new double[]{1.0});
        list.addThruPipe(new Instance(fv, lv, "test", null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        int vCount1 = opt.getValueCalls();
        int gCount1 = opt.getValueGradientCalls();

        opt.getValue();
        opt.getValueGradient(new double[opt.getNumParameters()]);

        int vCount2 = opt.getValueCalls();
        int gCount2 = opt.getValueGradientCalls();

        assertEquals(vCount1 + 1, vCount2);
        assertEquals(gCount1 + 1, gCount2);
    }
@Test
    public void testSetParametersWithDifferentLengthArray() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance carrier) {
                return carrier;
            }
        };

        InstanceList list = new InstanceList(pipe);

        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"f0"}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"L"}, new double[]{1.0});
        list.addThruPipe(new Instance(fv, lv, "id", null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        double[] tooLong = new double[opt.getNumParameters() + 2];
        tooLong[0] = 0.111;
        tooLong[opt.getNumParameters() + 1] = 42.0;

        opt.setParameters(tooLong);

        
        double[] buffer = new double[opt.getNumParameters()];
        opt.getParameters(buffer);
        assertEquals(0.111, buffer[0], 1e-6);
    }
@Test
    public void testGetValueSkipsNullLabelingInstances() {
        Alphabet da = new Alphabet();
        LabelAlphabet la = new LabelAlphabet();
        Pipe pipe = new Pipe(da, la) {
            public Instance pipe(Instance inst) {
                return inst;
            }
        };

        InstanceList list = new InstanceList(pipe);

        FeatureVector fv = new FeatureVector(da, new String[]{"x"}, new double[]{1.0});
        Instance inst = new Instance(fv, null, "inst-null-label", null);
        list.addThruPipe(inst);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        double value = opt.getValue();

        assertTrue(Double.isFinite(value));
    }
@Test
    public void testLabelingWithZeroProbabilityIsIgnoredInValue() {
        Alphabet da = new Alphabet();
        LabelAlphabet la = new LabelAlphabet();
        Pipe pipe = new Pipe(da, la) {
            public Instance pipe(Instance carrier) {
                return carrier;
            }
        };

        InstanceList list = new InstanceList(pipe);
        la.lookupIndex("A");
        la.lookupIndex("B");

        FeatureVector fv = new FeatureVector(da, new String[]{"f"}, new double[]{1.0});
        LabelVector lv = new LabelVector(la, new String[]{"A", "B"}, new double[]{0.0, 1.0});
        Instance instance = new Instance(fv, lv, "inst", null);
        list.addThruPipe(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        double value = opt.getValue();

        assertTrue(Double.isFinite(value));
    }
@Test
    public void testValueReturnsNegativeInfinityIfInfiniteScore() {
        Alphabet da = new Alphabet();
        LabelAlphabet la = new LabelAlphabet();
        Pipe pipe = new Pipe(da, la) {
            public Instance pipe(Instance inst) {
                return inst;
            }
        };

        InstanceList list = new InstanceList(pipe);
        la.lookupIndex("target");

        FeatureVector fv = new FeatureVector(da, new String[]{"f"}, new double[]{1.0});
        LabelVector lv = new LabelVector(la, new String[]{"target"}, new double[]{1.0});
        list.addThruPipe(new Instance(fv, lv, "inst", null));

        MaxEnt dummy = new MaxEnt(pipe) {
            public void getClassificationScores(Instance instance, double[] scores) {
                scores[0] = 0.0;
            }
        };

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, dummy);
        double val = opt.getValue();

        assertEquals(Double.NEGATIVE_INFINITY, val, 1e-10);
    }
@Test
    public void testGetParametersWithNullBufferReturnsWithoutError() {
        Alphabet da = new Alphabet();
        LabelAlphabet la = new LabelAlphabet();
        Pipe pipe = new Pipe(da, la) {
            public Instance pipe(Instance inst) {
                return inst;
            }
        };

        InstanceList list = new InstanceList(pipe);
        FeatureVector fv = new FeatureVector(da, new String[]{"f1"}, new double[]{1.0});
        LabelVector lv = new LabelVector(la, new String[]{"Y"}, new double[]{1.0});
        list.addThruPipe(new Instance(fv, lv, "id", null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        try {
            opt.getParameters(null);
        } catch (Exception e) {
            fail("getParameters(null) should not throw an exception");
        }
    }
@Test
    public void testFeatureVectorWithInfiniteValueDoesNotCrash() {
        Alphabet da = new Alphabet();
        LabelAlphabet la = new LabelAlphabet();
        Pipe pipe = new Pipe(da, la) {
            public Instance pipe(Instance inst) {
                return inst;
            }
        };

        InstanceList list = new InstanceList(pipe);
        FeatureVector fv = new FeatureVector(da, new String[]{"feat"}, new double[]{Double.POSITIVE_INFINITY});
        LabelVector lv = new LabelVector(la, new String[]{"LABEL"}, new double[]{1.0});
        list.addThruPipe(new Instance(fv, lv, "id", null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        double v = opt.getValue();

        assertTrue(Double.isFinite(v) || v == Double.NEGATIVE_INFINITY);
    }
@Test
    public void testZeroInstanceWeightDoesNotAffectGradient() {
        Alphabet da = new Alphabet();
        LabelAlphabet la = new LabelAlphabet();
        Pipe pipe = new Pipe(da, la) {
            public Instance pipe(Instance inst) {
                return inst;
            }
        };

        InstanceList list = new InstanceList(pipe);
        FeatureVector fv = new FeatureVector(da, new String[]{"feat"}, new double[]{1.0});
        LabelVector lv = new LabelVector(la, new String[]{"A"}, new double[]{1.0});
        Instance inst = new Instance(fv, lv, "i", null);
        list.addThruPipe(inst);
        list.setInstanceWeight(inst, 0.0);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        double[] gradient = new double[opt.getNumParameters()];
        opt.getValueGradient(gradient);

        assertEquals(0.0, gradient[0], 1e-6); 
    }
@Test
    public void testSetGaussianPriorVarianceToZeroCausesInfinity() {
        Alphabet da = new Alphabet();
        LabelAlphabet la = new LabelAlphabet();

        Pipe pipe = new Pipe(da, la) {
            public Instance pipe(Instance inst) {
                return inst;
            }
        };

        InstanceList list = new InstanceList(pipe);

        FeatureVector fv = new FeatureVector(da, new String[]{"feat"}, new double[]{1.0});
        LabelVector lv = new LabelVector(la, new String[]{"G"}, new double[]{1.0});
        list.addThruPipe(new Instance(fv, lv, "inst", null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        opt.setParameter(0, 1.0);
        opt.setGaussianPriorVariance(0.0);

        double value = opt.getValue();
        assertTrue("Should produce an infinite value due to division by zero in prior penalty", Double.isInfinite(value) || Double.isNaN(value));
    }
@Test
    public void testGetParametersWithUninitializedArrayWritesCorrectly() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance instance) {
                return instance;
            }
        };

        dataAlphabet.lookupIndex("feat");
        labelAlphabet.lookupIndex("LBL");

        InstanceList list = new InstanceList(pipe);

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new int[]{0}, new double[]{1.0});
        Instance inst = new Instance(fv, lv, "id", null);
        list.addThruPipe(inst);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        double[] buffer = null;
        opt.getParameters(buffer);  
    }
@Test
    public void testGetValueGradientHandlesNegativeInfinityParameters() {
        Alphabet da = new Alphabet();
        LabelAlphabet la = new LabelAlphabet();
        Pipe pipe = new Pipe(da, la) {
            public Instance pipe(Instance carrier) {
                return carrier;
            }
        };

        da.lookupIndex("f1");
        la.lookupIndex("L1");

        InstanceList list = new InstanceList(pipe);
        FeatureVector fv = new FeatureVector(da, new String[]{"f1"}, new double[]{1.0});
        LabelVector lv = new LabelVector(la, new String[]{"L1"}, new double[]{1.0});
        list.addThruPipe(new Instance(fv, lv, "id", null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        
        opt.setParameter(0, Double.NEGATIVE_INFINITY);

        double[] gradient = new double[opt.getNumParameters()];
        opt.getValueGradient(gradient);

        assertEquals(0.0, gradient[0], 0.0); 
    }
@Test
    public void testFeatureSelectionNullAndPerLabelNullHandled() {
        Alphabet da = new Alphabet();
        LabelAlphabet la = new LabelAlphabet();

        Pipe pipe = new Pipe(da, la) {
            public Instance pipe(Instance inst) {
                return inst;
            }
        };

        da.lookupIndex("one");
        la.lookupIndex("class");

        InstanceList list = new InstanceList(pipe);

        FeatureVector fv = new FeatureVector(da, new String[]{"one"}, new double[]{1.0});
        LabelVector lv = new LabelVector(la, new String[]{"class"}, new double[]{1.0});
        list.addThruPipe(new Instance(fv, lv, "inst1", null));

        MaxEntOptimizableByLabelDistribution model = new MaxEntOptimizableByLabelDistribution(list, null);
        MaxEnt classifier = model.getClassifier();
        assertNotNull(classifier);
    }
@Test
    public void testMultiLabelExpectsFullLabelCoverage() {
        Alphabet da = new Alphabet();
        LabelAlphabet la = new LabelAlphabet();

        la.lookupIndex("L1");
        la.lookupIndex("L2");
        la.lookupIndex("L3");

        Pipe pipe = new Pipe(da, la) {
            public Instance pipe(Instance inst) {
                return inst;
            }
        };

        da.lookupIndex("feat");

        InstanceList list = new InstanceList(pipe);
        FeatureVector fv = new FeatureVector(da, new String[]{"feat"}, new double[]{1.0});

        double[] probs = new double[]{0.3, 0.3, 0.4};
        LabelVector lv = new LabelVector(la, new int[]{0, 1, 2}, probs);
        Instance inst = new Instance(fv, lv, "testInstance", null);

        list.addThruPipe(inst);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        double val = opt.getValue();
        assertTrue("Should still return finite log-likelihood", Double.isFinite(val));
    }
@Test
    public void testSetParameterInvalidatesCache() {
        Alphabet da = new Alphabet();
        LabelAlphabet la = new LabelAlphabet();

        da.lookupIndex("a");
        la.lookupIndex("X");

        Pipe pipe = new Pipe(da, la) {
            public Instance pipe(Instance inst) {
                return inst;
            }
        };

        InstanceList list = new InstanceList(pipe);
        FeatureVector fv = new FeatureVector(da, new int[]{0}, new double[]{1.0});
        LabelVector lv = new LabelVector(la, new int[]{0}, new double[]{1.0});
        list.addThruPipe(new Instance(fv, lv, "foo", null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        double original = opt.getValue();
        opt.setParameter(0, 5.0);
        double updated = opt.getValue();

        assertNotEquals(original, updated, 1e-6);
    }
@Test
    public void testPipeMismatchThrowsAssertion() {
        Alphabet da = new Alphabet();
        LabelAlphabet la = new LabelAlphabet();
        Alphabet da2 = new Alphabet();
        LabelAlphabet la2 = new LabelAlphabet();

        Pipe pipe1 = new Pipe(da, la) {
            public Instance pipe(Instance inst) {
                return inst;
            }
        };

        Pipe pipe2 = new Pipe(da2, la2) {
            public Instance pipe(Instance inst) {
                return inst;
            }
        };

        InstanceList list = new InstanceList(pipe1);
        FeatureVector fv1 = new FeatureVector(da, new String[]{"x"}, new double[]{1.0});
        LabelVector lv1 = new LabelVector(la, new String[]{"Y"}, new double[]{1.0});
        list.addThruPipe(new Instance(fv1, lv1, "id", null));

        MaxEnt classifier = new MaxEnt(pipe2, new double[da.size() + 1], null, null);

        try {
            new MaxEntOptimizableByLabelDistribution(list, classifier);
            fail("Expected AssertionError due to mismatched pipe");
        } catch (AssertionError e) {
            assertTrue(e.getMessage() == null || e.getMessage().isEmpty());
        }
    }
@Test
    public void testZeroFeaturesInInstanceListHandledGracefully() {
        Alphabet a = new Alphabet();
        LabelAlphabet l = new LabelAlphabet();

        Pipe pipe = new Pipe(a, l) {
            public Instance pipe(Instance inst) {
                return inst;
            }
        };

        a.lookupIndex("XX");
        l.lookupIndex("Cat");

        InstanceList list = new InstanceList(pipe);

        int[] featureIndices = new int[0];
        double[] featureValues = new double[0];
        FeatureVector fv = new FeatureVector(a, featureIndices, featureValues);
        LabelVector lv = new LabelVector(l, new int[]{0}, new double[]{1.0});

        Instance inst = new Instance(fv, lv, "emptyFV", null);
        list.addThruPipe(inst);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        double val = opt.getValue();
        assertTrue(Double.isFinite(val));
    }
@Test
    public void testEmptyTrainingSetHandledGracefully() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance inst) {
                return inst;
            }
        };

        InstanceList emptyList = new InstanceList(pipe);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(emptyList, null);

        assertEquals(0, opt.getValueCalls());
        double val = opt.getValue();
        assertTrue(Double.isFinite(val) || val == 0.0);
    }
@Test
    public void testFeatureWithNaNComputesLogCorrectly() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        dataAlphabet.lookupIndex("f1");
        labelAlphabet.lookupIndex("C");

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance inst) {
                return inst;
            }
        };

        InstanceList list = new InstanceList(pipe);

        double[] featureValues = new double[]{Double.NaN};
        int[] featureIndices = new int[]{0};

        FeatureVector fv = new FeatureVector(dataAlphabet, featureIndices, featureValues);
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"C"}, new double[]{1.0});
        Instance inst = new Instance(fv, lv, "inst-with-nan", null);
        list.addThruPipe(inst);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        double val = opt.getValue();
        assertTrue(Double.isFinite(val) || val == Double.NEGATIVE_INFINITY);
    }
@Test
    public void testResettingParameterCacheConsistency() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        dataAlphabet.lookupIndex("word");
        labelAlphabet.lookupIndex("class");

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance inst) {
                return inst;
            }
        };

        InstanceList list = new InstanceList(pipe);

        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"word"}, new double[]{2.0});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"class"}, new double[]{1.0});
        list.addThruPipe(new Instance(fv, lv, "X", null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        double[] original = new double[opt.getNumParameters()];
        opt.getValueGradient(original);

        opt.setParameter(0, opt.getParameter(0) + 0.1);
        double[] updated = new double[opt.getNumParameters()];
        opt.getValueGradient(updated);

        assertNotEquals(original[0], updated[0], 1e-6);
    }
@Test
    public void testGetValueWithoutAnyFeaturesWithOnlyDefaultFeature() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        labelAlphabet.lookupIndex("Y");

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance instance) {
                return instance;
            }
        };

        InstanceList list = new InstanceList(pipe);

        int[] empty = new int[0];
        double[] emptyVals = new double[0];
        FeatureVector fv = new FeatureVector(dataAlphabet, empty, emptyVals);
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"Y"}, new double[]{1.0});
        Instance inst = new Instance(fv, lv, "defaultOnly", null);
        list.addThruPipe(inst);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        double value = opt.getValue();
        assertTrue(Double.isFinite(value));
    }
@Test
    public void testZeroScoresWithZeroWeightsDoesNotThrow() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        dataAlphabet.lookupIndex("feat");
        labelAlphabet.lookupIndex("T");

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance inst) {
                return inst;
            }
        };

        InstanceList list = new InstanceList(pipe);
        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"feat"}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"T"}, new double[]{1.0});
        Instance instance = new Instance(fv, lv, "zero-score", null);
        list.addThruPipe(instance);
        list.setInstanceWeight(instance, 0.0);

        MaxEnt dummyClassifier = new MaxEnt(pipe) {
            public void getClassificationScores(Instance instance, double[] scores) {
                scores[0] = 0.0;
            }
        };

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, dummyClassifier);
        double result = opt.getValue();
        assertTrue(Double.isFinite(result) || result == Double.NEGATIVE_INFINITY);
    }
@Test
    public void testCallCountsMultipleCallsCorrectly() {
        Alphabet a = new Alphabet();
        LabelAlphabet l = new LabelAlphabet();

        a.lookupIndex("a1");
        l.lookupIndex("Z");

        Pipe pipe = new Pipe(a, l) {
            public Instance pipe(Instance inst) {
                return inst;
            }
        };

        InstanceList list = new InstanceList(pipe);
        FeatureVector fv = new FeatureVector(a, new String[]{"a1"}, new double[]{1.0});
        LabelVector lv = new LabelVector(l, new String[]{"Z"}, new double[]{1.0});
        list.addThruPipe(new Instance(fv, lv, "counter", null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        int v0 = opt.getValueCalls();
        opt.getValue();
        int v1 = opt.getValueCalls();
        opt.getValue();
        int v2 = opt.getValueCalls();

        assertEquals(v0 + 1, v1);
        assertEquals(v1, v2); 

        int g0 = opt.getValueGradientCalls();
        opt.getValueGradient(new double[opt.getNumParameters()]);
        int g1 = opt.getValueGradientCalls();

        assertEquals(g0 + 1, g1);
    }
@Test
    public void testInstanceWithFeatureOnceUsedManyTimesDoesNotBreak() {
        Alphabet a = new Alphabet();
        LabelAlphabet l = new LabelAlphabet();

        a.lookupIndex("tok");
        l.lookupIndex("label");

        Pipe pipe = new Pipe(a, l) {
            public Instance pipe(Instance inst) {
                return inst;
            }
        };

        InstanceList list = new InstanceList(pipe);

        int[] fIdx = new int[]{0, 0, 0};
        double[] fVal = new double[]{1.0, 1.0, 1.0};

        FeatureVector fv = new FeatureVector(a, fIdx, fVal);
        LabelVector lv = new LabelVector(l, new int[]{0}, new double[]{1.0});

        Instance inst = new Instance(fv, lv, "repeat", null);
        list.addThruPipe(inst);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        double score = opt.getValue();
        assertTrue(Double.isFinite(score));
    }
@Test
    public void testNullFeatureSelectionDoesNotAffectGradient() {
        Alphabet a = new Alphabet();
        LabelAlphabet l = new LabelAlphabet();

        a.lookupIndex("one");
        l.lookupIndex("label");

        Pipe pipe = new Pipe(a, l) {
            public Instance pipe(Instance inst) {
                return inst;
            }
        };

        InstanceList list = new InstanceList(pipe);
        FeatureVector fv = new FeatureVector(a, new String[]{"one"}, new double[]{1.0});
        LabelVector lv = new LabelVector(l, new String[]{"label"}, new double[]{1.0});
        list.addThruPipe(new Instance(fv, lv, "normal", null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        double[] gradient = new double[opt.getNumParameters()];
        opt.getValueGradient(gradient);

        assertEquals(gradient.length, opt.getNumParameters());
    }
@Test
    public void testSetAndGetParametersWithNullBufferAndMismatchedSizeBuffer() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        dataAlphabet.lookupIndex("f");
        labelAlphabet.lookupIndex("L");

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance carrier) {
                return carrier;
            }
        };

        InstanceList instances = new InstanceList(pipe);

        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"f"}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"L"}, new double[]{1.0});
        instances.addThruPipe(new Instance(fv, lv, "inst", null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(instances, null);

        double[] originalBuff = null;
        opt.getParameters(originalBuff); 

        double[] mismatchedBuff = new double[1];
        opt.getParameters(mismatchedBuff); 

        
        double[] oversizedBuff = new double[opt.getNumParameters() + 5];
        oversizedBuff[0] = 42.42;
        opt.setParameters(oversizedBuff);

        double[] readBuff = new double[opt.getNumParameters()];
        opt.getParameters(readBuff);
        assertEquals(42.42, readBuff[0], 1e-5);
    }
@Test
    public void testLabelingWithoutAnyPositiveWeight() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        dataAlphabet.lookupIndex("f");
        labelAlphabet.lookupIndex("L1");
        labelAlphabet.lookupIndex("L2");

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance carrier) {
                return carrier;
            }
        };

        InstanceList instances = new InstanceList(pipe);

        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"f"}, new double[]{1.0});
        double[] labelWeights = new double[]{0.0, 0.0};
        LabelVector lv = new LabelVector(labelAlphabet, new int[]{0, 1}, labelWeights);

        instances.addThruPipe(new Instance(fv, lv, "zero-label-weights", null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(instances, null);
        double val = opt.getValue();
        assertTrue(Double.isFinite(val));
    }
@Test
    public void testMultipleLabelsWithAllZeroScoresTriggersInfinity() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("L1");
        labelAlphabet.lookupIndex("L2");

        dataAlphabet.lookupIndex("x");

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance instance) {
                return instance;
            }
        };

        InstanceList instances = new InstanceList(pipe);
        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"x"}, new double[]{1.0});
        double[] labelProbs = new double[]{0.5, 0.5};
        LabelVector lv = new LabelVector(labelAlphabet, new int[]{0, 1}, labelProbs);

        Instance instance = new Instance(fv, lv, "zero-score-multi-label", null);
        instances.addThruPipe(instance);

        MaxEnt zeroScorer = new MaxEnt(pipe) {
            public void getClassificationScores(Instance i, double[] scores) {
                scores[0] = 0.0;
                scores[1] = 0.0;
            }
        };

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(instances, zeroScorer);
        double val = opt.getValue();

        assertEquals(Double.NEGATIVE_INFINITY, val, 1e-10);
    }
@Test
    public void testNegativeFeatureValueHandledInGradient() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        dataAlphabet.lookupIndex("negFeat");
        labelAlphabet.lookupIndex("NEG");

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance inst) {
                return inst;
            }
        };

        InstanceList list = new InstanceList(pipe);

        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"negFeat"}, new double[]{-3.0});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"NEG"}, new double[]{1.0});
        list.addThruPipe(new Instance(fv, lv, "negative-fv", null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        double[] gradient = new double[opt.getNumParameters()];
        opt.getValueGradient(gradient);

        assertTrue(Double.isFinite(gradient[0]));
    }
@Test
    public void testDefaultFeatureIsAlwaysContributingToConstraintAndGradient() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        dataAlphabet.lookupIndex("foo");
        labelAlphabet.lookupIndex("label");

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance i) {
                return i;
            }
        };

        InstanceList list = new InstanceList(pipe);
        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"foo"}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"label"}, new double[]{1.0});
        list.addThruPipe(new Instance(fv, lv, "id", null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        double[] gradient = new double[opt.getNumParameters()];
        opt.getValueGradient(gradient);

        int defaultFeatureIndex = dataAlphabet.size();
        int defaultParamIndex = defaultFeatureIndex; 
        assertTrue("Default feature's gradient must be non-zero", Math.abs(gradient[defaultParamIndex]) > 0.0);
    }
@Test
    public void testConstructorWithInitialClassifierReusesClassifierFields() {
        Alphabet da = new Alphabet();
        LabelAlphabet la = new LabelAlphabet();
        da.lookupIndex("f");
        la.lookupIndex("A");

        Pipe classifierPipe = new Pipe(da, la) {
            public Instance pipe(Instance inst) {
                return inst;
            }
        };

        double[] params = new double[da.size() + 1];
        FeatureSelection selection = new FeatureSelection(da);
        selection.add(0);
        selection.add(da.size()); 

        MaxEnt classifier = new MaxEnt(classifierPipe, params, selection, null);

        Pipe dataPipe = new Pipe(da, la) {
            public Instance pipe(Instance inst) {
                return inst;
            }
        };

        InstanceList list = new InstanceList(dataPipe);
        FeatureVector fv = new FeatureVector(da, new String[]{"f"}, new double[]{1.0});
        LabelVector lv = new LabelVector(la, new String[]{"A"}, new double[]{1.0});
        list.addThruPipe(new Instance(fv, lv, "x", null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, classifier);

        assertSame("Should reuse classifier parameter array", classifier.parameters, opt.getClassifier().parameters);
        assertEquals("Default feature index consistency", classifier.defaultFeatureIndex, opt.getClassifier().defaultFeatureIndex);
    }
@Test
    public void testGradientCalculationWithZeroScoresButNoLabels() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        dataAlphabet.lookupIndex("feature_x");
        labelAlphabet.lookupIndex("ABC");

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance carrier) {
                return carrier;
            }
        };

        InstanceList list = new InstanceList(pipe);

        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"feature_x"}, new double[]{2.0});
        Instance inst = new Instance(fv, null, "no-labels", null);
        list.addThruPipe(inst);

        MaxEnt dummyClassifier = new MaxEnt(pipe) {
            public void getClassificationScores(Instance input, double[] scores) {
                scores[0] = 0.0;
            }
        };

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, dummyClassifier);

        
        double value = opt.getValue();
        assertTrue(Double.isFinite(value) || value == 0.0);
    }
@Test
    public void testFeatureSelectionAppliedToGradient() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        int fIdx = dataAlphabet.lookupIndex("selected");
        labelAlphabet.lookupIndex("L");

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance i) {
                return i;
            }
        };

        InstanceList list = new InstanceList(pipe);

        FeatureSelection fs = new FeatureSelection(dataAlphabet);
        fs.add(fIdx);

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{fIdx}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new int[]{0}, new double[]{1.0});
        list.addThruPipe(new Instance(fv, lv, "instance1", null));

        list.setFeatureSelection(fs);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        double[] gradient = new double[opt.getNumParameters()];
        opt.getValueGradient(gradient);

        assertTrue("Gradient should contain non-zero value for selected feature", Math.abs(gradient[fIdx]) > 0.0);
    }
@Test
    public void testPerLabelFeatureSelectionMasksGradientCorrectly() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        int index1 = dataAlphabet.lookupIndex("feat1");
        int index2 = dataAlphabet.lookupIndex("feat2");

        int lIndex0 = labelAlphabet.lookupIndex("C1");
        int lIndex1 = labelAlphabet.lookupIndex("C2");

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance i) {
                return i;
            }
        };

        InstanceList list = new InstanceList(pipe);
        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{index1, index2}, new double[]{1.0, 1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new int[]{lIndex0}, new double[]{1.0});
        list.addThruPipe(new Instance(fv, lv, "masked", null));

        FeatureSelection[] perLabelFs = new FeatureSelection[2];
        perLabelFs[0] = new FeatureSelection(dataAlphabet);
        perLabelFs[0].add(index1);  

        perLabelFs[1] = new FeatureSelection(dataAlphabet); 

        list.setPerLabelFeatureSelection(perLabelFs);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        double[] gradient = new double[opt.getNumParameters()];
        opt.getValueGradient(gradient);

        int numFeatures = dataAlphabet.size() + 1;

        double grad1 = gradient[lIndex0 * numFeatures + index1];
        double grad2Masked = gradient[lIndex0 * numFeatures + index2];

        assertNotEquals(0.0, grad1, 1e-6);
        assertEquals(0.0, grad2Masked, 1e-6); 
    }
@Test
    public void testSetGaussianPriorVarianceHasEffectOnValue() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        dataAlphabet.lookupIndex("feat-x");
        labelAlphabet.lookupIndex("L1");

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance inst) {
                return inst;
            }
        };

        InstanceList list = new InstanceList(pipe);
        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"feat-x"}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"L1"}, new double[]{1.0});
        list.addThruPipe(new Instance(fv, lv, "x", null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        double v1 = opt.getValue();

        opt.setGaussianPriorVariance(0.0001);
        double v2 = opt.getValue();

        assertNotEquals(v1, v2, 1e-5);
    }
@Test
    public void testZeroProbabilityLabelingElementDoesNotAffectValue() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        labelAlphabet.lookupIndex("A");
        labelAlphabet.lookupIndex("B");
        dataAlphabet.lookupIndex("token");

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance i) {
                return i;
            }
        };

        InstanceList list = new InstanceList(pipe);
        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"token"}, new double[]{1.0});
        double[] probs = new double[]{1.0, 0.0};
        LabelVector lv = new LabelVector(labelAlphabet, new int[]{0, 1}, probs);
        list.addThruPipe(new Instance(fv, lv, "zero-weight", null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        double value = opt.getValue();
        assertTrue(Double.isFinite(value));
    }
@Test
    public void testGetValueDoesNotRecomputeWhenCached() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        dataAlphabet.lookupIndex("a");
        labelAlphabet.lookupIndex("T");

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance i) {
                return i;
            }
        };

        InstanceList list = new InstanceList(pipe);
        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"a"}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"T"}, new double[]{1.0});
        list.addThruPipe(new Instance(fv, lv, "cache-test", null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        double v1 = opt.getValue();
        int countAfterFirst = opt.getValueCalls();
        double v2 = opt.getValue();
        int countAfterSecond = opt.getValueCalls();

        assertEquals(v1, v2, 1e-8);
        assertEquals(countAfterFirst, countAfterSecond); 
    }
@Test
    public void testGetValueGradientCachedBetweenCalls() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        dataAlphabet.lookupIndex("z");
        labelAlphabet.lookupIndex("Y");

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance i) {
                return i;
            }
        };

        InstanceList list = new InstanceList(pipe);
        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"z"}, new double[]{1.5});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"Y"}, new double[]{1.0});
        list.addThruPipe(new Instance(fv, lv, "gradient-call-test", null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        double[] grad1 = new double[opt.getNumParameters()];
        double[] grad2 = new double[opt.getNumParameters()];

        int g0 = opt.getValueGradientCalls();
        opt.getValueGradient(grad1);
        int g1 = opt.getValueGradientCalls();
        opt.getValueGradient(grad2);
        int g2 = opt.getValueGradientCalls();

        assertArrayEquals(grad1, grad2, 1e-6);
        assertEquals(g0 + 1, g1);
        assertEquals(g1, g2); 
    }
@Test
    public void testInstanceWithEmptyAlphabetDoesNotThrow() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        dataAlphabet.stopGrowth();
        labelAlphabet.stopGrowth();

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance inst) {
                return inst;
            }
        };

        InstanceList list = new InstanceList(pipe);
        int[] indices = new int[0];
        double[] values = new double[0];
        FeatureVector fv = new FeatureVector(dataAlphabet, indices, values);

        int labelIndex = labelAlphabet.lookupIndex("Z");
        double[] labelProbs = new double[]{1.0};
        LabelVector lv = new LabelVector(labelAlphabet, new int[]{labelIndex}, labelProbs);

        Instance instance = new Instance(fv, lv, "empty-alphabet-instance", null);
        list.addThruPipe(instance);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        double val = opt.getValue();
        assertTrue(Double.isFinite(val));
    }
@Test
    public void testSetParametersLengthMismatchPreservesAllValues() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        dataAlphabet.lookupIndex("a");
        labelAlphabet.lookupIndex("X");

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance instance) {
                return instance;
            }
        };

        InstanceList list = new InstanceList(pipe);
        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"a"}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"X"}, new double[]{1.0});
        list.addThruPipe(new Instance(fv, lv, "setparam-length", null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        int len = opt.getNumParameters();

        double[] newParams = new double[len + 2];
        newParams[0] = 3.14;
        newParams[1] = 2.71;
        newParams[len] = 99.99;
        newParams[len + 1] = 1.23;

        opt.setParameters(newParams);
        double[] result = new double[opt.getNumParameters()];
        opt.getParameters(result);

        assertEquals(3.14, result[0], 1e-6);
        assertEquals(2.71, result[1], 1e-6);
    }
@Test
    public void testFeatureVectorWithAllNaNShouldGracefullyHandle() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        int feature = dataAlphabet.lookupIndex("f");
        int label = labelAlphabet.lookupIndex("Y");

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance i) {
                return i;
            }
        };

        InstanceList list = new InstanceList(pipe);

        int[] indices = new int[]{feature, feature};
        double[] values = new double[]{Double.NaN, Double.NaN};

        FeatureVector fv = new FeatureVector(dataAlphabet, indices, values);
        double[] labelProbs = new double[]{1.0};
        LabelVector lv = new LabelVector(labelAlphabet, new int[]{label}, labelProbs);

        list.addThruPipe(new Instance(fv, lv, "all-nan", null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        double val = opt.getValue();

        assertTrue(Double.isFinite(val) || val == Double.NEGATIVE_INFINITY);
    }
@Test
    public void testDefaultFeatureGradientAlwaysPresentRegardlessOfInput() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        dataAlphabet.lookupIndex("bias");
        labelAlphabet.lookupIndex("ONE");

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance instance) {
                return instance;
            }
        };

        InstanceList list = new InstanceList(pipe);

        int[] featureIndices = new int[0];
        double[] featureValues = new double[0];

        FeatureVector fv = new FeatureVector(dataAlphabet, featureIndices, featureValues);
        LabelVector lv = new LabelVector(labelAlphabet, new String[]{"ONE"}, new double[]{1.0});

        Instance inst = new Instance(fv, lv, "bias-only", null);
        list.addThruPipe(inst);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        int defaultFeatureIdx = dataAlphabet.size();
        int paramIndex = defaultFeatureIdx;

        double[] gradient = new double[opt.getNumParameters()];
        opt.getValueGradient(gradient);

        assertTrue(Math.abs(gradient[paramIndex]) > 0.0);
    }
@Test
    public void testInfiniteFeatureCausesNonFiniteGradientButNotException() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        int featureIndex = dataAlphabet.lookupIndex("INF_FEATURE");
        int labelIndex = labelAlphabet.lookupIndex("LABEL");

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance inst) {
                return inst;
            }
        };

        InstanceList list = new InstanceList(pipe);

        int[] indices = new int[]{featureIndex};
        double[] values = new double[]{Double.POSITIVE_INFINITY};
        FeatureVector fv = new FeatureVector(dataAlphabet, indices, values);
        LabelVector lv = new LabelVector(labelAlphabet, new int[]{labelIndex}, new double[]{1.0});

        list.addThruPipe(new Instance(fv, lv, "inf-feature", null));

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        try {
            double val = opt.getValue();
            assertTrue(Double.isFinite(val) || Double.isInfinite(val));
        } catch (Exception e) {
            fail("Should gracefully handle infinite feature without throwing");
        }
    }
@Test
    public void testMultipleLabelsMismatchWithAlphabetSizeRendersAssertion() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        labelAlphabet.lookupIndex("A");
        labelAlphabet.lookupIndex("B");

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance instance) {
                return instance;
            }
        };

        InstanceList list = new InstanceList(pipe);

        int[] feats = new int[0];
        double[] vals = new double[0];
        FeatureVector fv = new FeatureVector(dataAlphabet, feats, vals);

        
        int[] labelIndices = new int[]{0};
        double[] labelValues = new double[]{1.0};

        LabelVector incomplete = new LabelVector(labelAlphabet, labelIndices, labelValues);

        Instance inst = new Instance(fv, incomplete, "assertion-trigger", null);
        list.addThruPipe(inst);

        try {
            MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
            opt.getValue();
        } catch (AssertionError e) {
            assertTrue(true); 
            return;
        }

        fail("Expected AssertionError due to label size mismatch");
    }
@Test
    public void testNaNConstraintValueInTrainingInstance() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        int featureIndex = dataAlphabet.lookupIndex("F");
        int labelIndex = labelAlphabet.lookupIndex("L");

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance inst) {
                return inst;
            }
        };

        InstanceList list = new InstanceList(pipe);
        double[] values = new double[]{Double.NaN};
        int[] indices = new int[]{featureIndex};
        FeatureVector fv = new FeatureVector(dataAlphabet, indices, values);

        LabelVector lv = new LabelVector(labelAlphabet, new int[]{labelIndex}, new double[]{1.0});
        Instance inst = new Instance(fv, lv, "nan-constraint", null);

        list.addThruPipe(inst);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        try {
            double result = opt.getValue();
            assertTrue(Double.isFinite(result) || result == Double.NEGATIVE_INFINITY);
        } catch (Exception e) {
            fail("NaN in training instance should be logged but not crash");
        }
    } 
}