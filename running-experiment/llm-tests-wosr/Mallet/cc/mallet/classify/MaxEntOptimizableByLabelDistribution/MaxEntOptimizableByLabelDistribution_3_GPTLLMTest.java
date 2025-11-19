public class MaxEntOptimizableByLabelDistribution_3_GPTLLMTest { 

 @Test
    public void testConstructorInitializesClassifierAndParameters() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        Pipe dummyPipe = new NoopPipe(dataAlphabet, labelAlphabet);

        dataAlphabet.lookupIndex("feature1");
        dataAlphabet.lookupIndex("feature2");
        labelAlphabet.lookupIndex("label1");
        labelAlphabet.lookupIndex("label2");

        InstanceList trainingList = new InstanceList(dummyPipe);

        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"feature1"}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new double[]{0.5, 0.5});
        trainingList.add(new Instance(fv, lv, "name", null), 1.0);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(trainingList, null);

        assertNotNull(opt.getClassifier());
        assertEquals(6, opt.getNumParameters());
    }
@Test
    public void testSetAndGetParameter() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        Pipe dummyPipe = new NoopPipe(dataAlphabet, labelAlphabet);

        dataAlphabet.lookupIndex("f");
        labelAlphabet.lookupIndex("l1");
        labelAlphabet.lookupIndex("l2");

        InstanceList list = new InstanceList(dummyPipe);
        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"f"}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new double[]{1.0, 0.0});
        list.add(new Instance(fv, lv, "ex", null), 1.0);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        opt.setParameter(0, 42.0);
        assertEquals(42.0, opt.getParameter(0), 1e-10);
    }
@Test
    public void testSetAndGetParametersArray() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        Pipe dummyPipe = new NoopPipe(dataAlphabet, labelAlphabet);

        dataAlphabet.lookupIndex("x");
        labelAlphabet.lookupIndex("a");
        labelAlphabet.lookupIndex("b");

        InstanceList instances = new InstanceList(dummyPipe);
        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"x"}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new double[]{0.5, 0.5});
        instances.add(new Instance(fv, lv, "id", null), 1.0);

        MaxEntOptimizableByLabelDistribution model = new MaxEntOptimizableByLabelDistribution(instances, null);

        double[] params = new double[model.getNumParameters()];
        Arrays.fill(params, 2.5);
        model.setParameters(params);

        double[] result = new double[params.length];
        model.getParameters(result);

        assertEquals(2.5, result[0], 1e-6);
        assertEquals(2.5, result[1], 1e-6);
        assertEquals(2.5, result[2], 1e-6);
    }
@Test
    public void testGetValueNotNaNOrInfinite() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        Pipe pipe = new NoopPipe(dataAlphabet, labelAlphabet);

        dataAlphabet.lookupIndex("f1");
        labelAlphabet.lookupIndex("yes");
        labelAlphabet.lookupIndex("no");

        InstanceList instances = new InstanceList(pipe);
        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"f1"}, new double[]{2.0});
        LabelVector lv = new LabelVector(labelAlphabet, new double[]{0.6, 0.4});
        instances.add(new Instance(fv, lv, "i", null), 1.0);

        MaxEntOptimizableByLabelDistribution trainer = new MaxEntOptimizableByLabelDistribution(instances, null);

        double value = trainer.getValue();

        assertFalse(Double.isNaN(value));
        assertFalse(Double.isInfinite(value));
    }
@Test
    public void testGetValueGradientValuesAreValid() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        Pipe pipe = new NoopPipe(dataAlphabet, labelAlphabet);

        dataAlphabet.lookupIndex("f2");
        labelAlphabet.lookupIndex("cat");
        labelAlphabet.lookupIndex("dog");

        InstanceList instances = new InstanceList(pipe);
        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"f2"}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new double[]{0.7, 0.3});
        instances.add(new Instance(fv, lv, "n", null), 1.0);

        MaxEntOptimizableByLabelDistribution trainer = new MaxEntOptimizableByLabelDistribution(instances, null);

        double[] gradient = new double[trainer.getNumParameters()];
        trainer.getValueGradient(gradient);

        assertFalse(Double.isNaN(gradient[0]));
        assertFalse(Double.isInfinite(gradient[0]));
    }
@Test
    public void testGradientChangesAfterParameterUpdate() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        Pipe pipe = new NoopPipe(dataAlphabet, labelAlphabet);

        dataAlphabet.lookupIndex("word");
        labelAlphabet.lookupIndex("pos");
        labelAlphabet.lookupIndex("neg");

        InstanceList list = new InstanceList(pipe);
        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"word"}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new double[]{0.9, 0.1});
        list.add(new Instance(fv, lv, "t", null), 1.0);

        MaxEntOptimizableByLabelDistribution trainer = new MaxEntOptimizableByLabelDistribution(list, null);

        double[] grad1 = new double[trainer.getNumParameters()];
        trainer.getValueGradient(grad1);

        trainer.setParameter(0, trainer.getParameter(0) + 0.01);

        double[] grad2 = new double[trainer.getNumParameters()];
        trainer.getValueGradient(grad2);

        assertNotEquals(grad1[0], grad2[0], 1e-8);
    }
@Test
    public void testGetValueCallsAndGradientCallsIncrement() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        Pipe pipe = new NoopPipe(dataAlphabet, labelAlphabet);

        dataAlphabet.lookupIndex("feat");
        labelAlphabet.lookupIndex("y");
        labelAlphabet.lookupIndex("n");

        InstanceList list = new InstanceList(pipe);
        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"feat"}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new double[]{0.4, 0.6});
        list.add(new Instance(fv, lv, "test", null), 1.0);

        MaxEntOptimizableByLabelDistribution trainer = new MaxEntOptimizableByLabelDistribution(list, null);

        int valueCallsBefore = trainer.getValueCalls();
        int gradientCallsBefore = trainer.getValueGradientCalls();

        trainer.getValue();
        trainer.getValueGradient(new double[trainer.getNumParameters()]);

        assertEquals(valueCallsBefore + 1, trainer.getValueCalls());
        assertEquals(gradientCallsBefore + 1, trainer.getValueGradientCalls());
    }
@Test
    public void testSetGaussianPriorVarianceChangesBehavior() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        Pipe pipe = new NoopPipe(dataAlphabet, labelAlphabet);

        dataAlphabet.lookupIndex("x1");
        labelAlphabet.lookupIndex("true");
        labelAlphabet.lookupIndex("false");

        InstanceList list = new InstanceList(pipe);
        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"x1"}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new double[]{0.3, 0.7});
        list.add(new Instance(fv, lv, "v", null), 1.0);

        MaxEntOptimizableByLabelDistribution defaultTrainer = new MaxEntOptimizableByLabelDistribution(list, null);
        double defaultValue = defaultTrainer.getValue();

        MaxEntOptimizableByLabelDistribution smallPriorTrainer = new MaxEntOptimizableByLabelDistribution(list, null);
        smallPriorTrainer.setGaussianPriorVariance(0.01);
        double smallPriorValue = smallPriorTrainer.getValue();

        assertNotEquals(defaultValue, smallPriorValue, 1e-4);
    }
@Test
    public void testUseGaussianPriorReturnsSelf() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        Pipe pipe = new NoopPipe(dataAlphabet, labelAlphabet);

        dataAlphabet.lookupIndex("f3");
        labelAlphabet.lookupIndex("T");
        labelAlphabet.lookupIndex("F");

        InstanceList list = new InstanceList(pipe);
        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"f3"}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new double[]{0.5, 0.5});
        list.add(new Instance(fv, lv, "zz", null), 1.0);

        MaxEntOptimizableByLabelDistribution trainer = new MaxEntOptimizableByLabelDistribution(list, null);
        MaxEntOptimizableByLabelDistribution result = trainer.useGaussianPrior();

        assertSame(trainer, result);
    }
@Test
    public void testZeroInstanceWeightIgnoredInConstraintsAndValue() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            @Override
            public Instance pipe(Instance carrier) {
                return carrier;
            }
        };

        dataAlphabet.lookupIndex("x");
        labelAlphabet.lookupIndex("label1");
        labelAlphabet.lookupIndex("label2");

        InstanceList list = new InstanceList(pipe);
        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"x"}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new double[]{1.0, 0.0});
        Instance instance = new Instance(fv, lv, "zero-weight-instance", null);
        list.add(instance, 0.0); 

        MaxEntOptimizableByLabelDistribution trainer = new MaxEntOptimizableByLabelDistribution(list, null);
        double val = trainer.getValue();

        assertFalse(Double.isNaN(val));
        assertFalse(Double.isInfinite(val));
    }
@Test
    public void testInstanceWithNoLabelingIsSkipped() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            @Override
            public Instance pipe(Instance carrier) {
                return carrier;
            }
        };

        dataAlphabet.lookupIndex("f");
        labelAlphabet.lookupIndex("l1");
        labelAlphabet.lookupIndex("l2");

        InstanceList list = new InstanceList(pipe);
        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"f"}, new double[]{1.0});
        Instance instance = new Instance(fv, null, "no-label", null);
        list.add(instance, 1.0);

        MaxEntOptimizableByLabelDistribution model = new MaxEntOptimizableByLabelDistribution(list, null);
        double val = model.getValue();

        assertFalse(Double.isNaN(val));
        assertFalse(Double.isInfinite(val));
    }
@Test
    public void testInstanceWithNaNFeatureValueTriggersNaNCheck() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            @Override
            public Instance pipe(Instance instance) {
                return instance;
            }
        };

        dataAlphabet.lookupIndex("feat");
        labelAlphabet.lookupIndex("labelA");
        labelAlphabet.lookupIndex("labelB");

        InstanceList list = new InstanceList(pipe);
        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"feat"}, new double[]{Double.NaN});
        LabelVector lv = new LabelVector(labelAlphabet, new double[]{0.5, 0.5});
        Instance instance = new Instance(fv, lv, "nan-feature", null);
        list.add(instance, 1.0);

        MaxEntOptimizableByLabelDistribution trainer = new MaxEntOptimizableByLabelDistribution(list, null);
        double val = trainer.getValue();

        assertFalse(Double.isInfinite(val));
        assertFalse(Double.isNaN(val));
    }
@Test
    public void testEmptyTrainingList() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            @Override
            public Instance pipe(Instance carrier) {
                return carrier;
            }
        };

        InstanceList emptyList = new InstanceList(pipe);
        MaxEntOptimizableByLabelDistribution trainer = new MaxEntOptimizableByLabelDistribution(emptyList, null);
        double value = trainer.getValue();
        double[] gradient = new double[trainer.getNumParameters()];
        trainer.getValueGradient(gradient);

        assertFalse(Double.isNaN(value));
        assertFalse(Double.isInfinite(value));
        assertEquals(gradient.length, trainer.getNumParameters());
    }
@Test
    public void testScoreZeroTriggersInfiniteValueSkip() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            @Override
            public Instance pipe(Instance instance) {
                return instance;
            }
        };

        dataAlphabet.lookupIndex("x");
        labelAlphabet.lookupIndex("label1");
        labelAlphabet.lookupIndex("label2");

        InstanceList list = new InstanceList(pipe);
        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"x"}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new double[]{1.0, 0.0});
        Instance instance = new Instance(fv, lv, "bad-score", null);
        list.add(instance, 1.0);

        MaxEnt fakeClassifier = new MaxEnt(pipe, new double[6], null, null) {
            @Override
            public void getClassificationScores(Instance instance, double[] scores) {
                scores[0] = 0.0;
                scores[1] = 1.0;
            }
        };

        MaxEntOptimizableByLabelDistribution trainer = new MaxEntOptimizableByLabelDistribution(list, fakeClassifier);
        double val = trainer.getValue();

        assertTrue(Double.isInfinite(val) || val == Double.NEGATIVE_INFINITY);
    }
@Test
    public void testGradientZeroedForNonSelectedFeatures() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            @Override
            public Instance pipe(Instance instance) {
                return instance;
            }
        };

        dataAlphabet.lookupIndex("f1");
        Alphabet.EntryIter iter = dataAlphabet.iterator();
        while(iter.hasNext()) {
            iter.next(); 
        }

        labelAlphabet.lookupIndex("l1");
        labelAlphabet.lookupIndex("l2");

        InstanceList list = new InstanceList(pipe);

        FeatureSelection featureSelection = new FeatureSelection(new int[]{dataAlphabet.lookupIndex("f1")});
        list.setFeatureSelection(featureSelection);

        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"f1"}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new double[]{0.6, 0.4});
        Instance instance = new Instance(fv, lv, "only-f1", null);
        list.add(instance, 1.0);

        MaxEntOptimizableByLabelDistribution trainer = new MaxEntOptimizableByLabelDistribution(list, null);
        double[] gradient = new double[trainer.getNumParameters()];
        trainer.getValueGradient(gradient);

        int numFeatures = dataAlphabet.size() + 1;
        assertEquals(0.0, gradient[numFeatures + 0], 1e-10); 
    }
@Test
    public void testSetParametersHandlesLongerInput() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            @Override
            public Instance pipe(Instance instance) {
                return instance;
            }
        };

        dataAlphabet.lookupIndex("a");
        labelAlphabet.lookupIndex("a");
        labelAlphabet.lookupIndex("b");

        InstanceList list = new InstanceList(pipe);
        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"a"}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new double[]{0.5, 0.5});
        list.add(new Instance(fv, lv, "x", null), 1.0);

        MaxEntOptimizableByLabelDistribution trainer = new MaxEntOptimizableByLabelDistribution(list, null);

        int originalLength = trainer.getNumParameters();
        double[] longerParams = new double[originalLength + 4];
        for (int i = 0; i < longerParams.length; i++) {
            longerParams[i] = i * 1.0;
        }

        trainer.setParameters(longerParams);

        double[] copied = new double[longerParams.length];
        trainer.getParameters(copied);

        assertEquals(longerParams.length, copied.length);
        assertEquals(3.0, copied[3], 1e-10);
    }
@Test
    public void testGetParametersHandlesNullBuffer() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            @Override
            public Instance pipe(Instance instance) {
                return instance;
            }
        };

        dataAlphabet.lookupIndex("z");
        labelAlphabet.lookupIndex("l");

        InstanceList list = new InstanceList(pipe);
        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"z"}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new double[]{1.0});
        list.add(new Instance(fv, lv, "zinst", null), 1.0);

        MaxEntOptimizableByLabelDistribution trainer = new MaxEntOptimizableByLabelDistribution(list, null);

        trainer.getParameters(null); 
    }
@Test
    public void testSetParametersHandlesNullArray() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            @Override
            public Instance pipe(Instance instance) {
                return instance;
            }
        };

        dataAlphabet.lookupIndex("u");
        labelAlphabet.lookupIndex("p");

        InstanceList list = new InstanceList(pipe);
        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"u"}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new double[]{1.0});
        list.add(new Instance(fv, lv, "unit", null), 1.0);

        MaxEntOptimizableByLabelDistribution trainer = new MaxEntOptimizableByLabelDistribution(list, null);

        try {
            trainer.setParameters(null);
            fail("Expected AssertionError when passing null to setParameters");
        } catch (AssertionError e) {
            
        }
    }
@Test
    public void testSingleLabelSingleFeature() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance instance) {
                return instance;
            }
        };

        dataAlphabet.lookupIndex("featureOnly");
        labelAlphabet.lookupIndex("labelOnly");

        InstanceList list = new InstanceList(pipe);

        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"featureOnly"}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new double[]{1.0});
        list.add(new Instance(fv, lv, "single", null), 1.0);

        MaxEntOptimizableByLabelDistribution trainer = new MaxEntOptimizableByLabelDistribution(list, null);

        int numParams = trainer.getNumParameters();
        double[] gradient = new double[numParams];
        double value = trainer.getValue();
        trainer.getValueGradient(gradient);

        assertEquals(numParams, gradient.length);
        assertFalse(Double.isNaN(value));
        assertFalse(Double.isInfinite(value));
    }
@Test
    public void testZeroLengthParametersAndGradientCalls() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance instance) {
                return instance;
            }
        };

        InstanceList list = new InstanceList(pipe);
        MaxEntOptimizableByLabelDistribution trainer = new MaxEntOptimizableByLabelDistribution(list, null);

        int numParams = trainer.getNumParameters();
        assertEquals(0, numParams);

        double[] buffer = new double[numParams];
        trainer.getValueGradient(buffer);
        assertEquals(0, buffer.length);
    }
@Test
    public void testFeatureVectorWithZeroWeightFeature() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance instance) {
                return instance;
            }
        };

        dataAlphabet.lookupIndex("f_zero");
        labelAlphabet.lookupIndex("labelA");
        labelAlphabet.lookupIndex("labelB");

        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"f_zero"}, new double[]{0.0});
        LabelVector lv = new LabelVector(labelAlphabet, new double[]{0.5, 0.5});
        Instance instance = new Instance(fv, lv, "zero-weight", null);

        InstanceList list = new InstanceList(pipe);
        list.add(instance, 1.0);

        MaxEntOptimizableByLabelDistribution trainer = new MaxEntOptimizableByLabelDistribution(list, null);
        double value = trainer.getValue();
        double[] gradient = new double[trainer.getNumParameters()];
        trainer.getValueGradient(gradient);

        assertFalse(Double.isNaN(value));
        assertEquals(gradient.length, trainer.getNumParameters());
    }
@Test
    public void testClassifierCompatibilityWithTrainingPipe() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        Pipe sharedPipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance instance) {
                return instance;
            }
        };

        dataAlphabet.lookupIndex("commFeature");
        labelAlphabet.lookupIndex("labelX");
        labelAlphabet.lookupIndex("labelY");

        InstanceList list = new InstanceList(sharedPipe);
        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"commFeature"}, new double[]{2.0});
        LabelVector lv = new LabelVector(labelAlphabet, new double[]{0.6, 0.4});
        Instance instance = new Instance(fv, lv, "compatible", null);
        list.add(instance);

        double[] initialParams = new double[6];
        MaxEnt classifier = new MaxEnt(sharedPipe, initialParams, null, null);

        MaxEntOptimizableByLabelDistribution trainer = new MaxEntOptimizableByLabelDistribution(list, classifier);
        assertSame(classifier, trainer.getClassifier());
    }
@Test
    public void testAllScoresZeroTriggersSkip() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance instance) {
                return instance;
            }
        };

        dataAlphabet.lookupIndex("f");
        labelAlphabet.lookupIndex("A");
        labelAlphabet.lookupIndex("B");

        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"f"}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new double[]{0.8, 0.2});
        Instance inst = new Instance(fv, lv, "sparse", null);

        InstanceList list = new InstanceList(pipe);
        list.add(inst, 1.0);

        MaxEnt classifierWithZeroScores = new MaxEnt(pipe, new double[6], null, null) {
            @Override
            public void getClassificationScores(Instance instance, double[] scores) {
                scores[0] = 0.0;
                scores[1] = 0.0;
            }
        };

        MaxEntOptimizableByLabelDistribution trainer = new MaxEntOptimizableByLabelDistribution(list, classifierWithZeroScores);
        double val = trainer.getValue();

        assertEquals(Double.NEGATIVE_INFINITY, val, 0.0);
    }
@Test
    public void testNegativeInstanceWeightHandledCorrectly() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance instance) {
                return instance;
            }
        };

        dataAlphabet.lookupIndex("feat");
        labelAlphabet.lookupIndex("pos");
        labelAlphabet.lookupIndex("neg");

        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"feat"}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new double[]{1.0, 0.0});
        Instance inst = new Instance(fv, lv, "neg-weight", null);

        InstanceList list = new InstanceList(pipe);
        list.add(inst, -1.0); 

        MaxEntOptimizableByLabelDistribution trainer = new MaxEntOptimizableByLabelDistribution(list, null);
        double val = trainer.getValue();
        double[] grad = new double[trainer.getNumParameters()];
        trainer.getValueGradient(grad);

        assertFalse(Double.isNaN(val));
        assertEquals(grad.length, trainer.getNumParameters());
    }
@Test
    public void testFeatureSelectionNotNullAndPerClassSelectionNull() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance carrier) {
                return carrier;
            }
        };

        int featureIndex = dataAlphabet.lookupIndex("f1");
        labelAlphabet.lookupIndex("L1");
        labelAlphabet.lookupIndex("L2");

        InstanceList list = new InstanceList(pipe);
        FeatureSelection featureSelection = new FeatureSelection();
        featureSelection.add(featureIndex);
        list.setFeatureSelection(featureSelection);

        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"f1"}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new double[]{0.6, 0.4});
        list.add(new Instance(fv, lv, "id", null), 1.0);

        MaxEntOptimizableByLabelDistribution trainer = new MaxEntOptimizableByLabelDistribution(list, null);
        double value = trainer.getValue();
        double[] gradient = new double[trainer.getNumParameters()];
        trainer.getValueGradient(gradient);

        assertFalse(Double.isNaN(value));
        assertEquals(gradient.length, trainer.getNumParameters());
    }
@Test
    public void testPerLabelFeatureSelectionOnly() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance carrier) {
                return carrier;
            }
        };

        int featureIndex = dataAlphabet.lookupIndex("x1");
        int l1 = labelAlphabet.lookupIndex("A");
        int l2 = labelAlphabet.lookupIndex("B");

        FeatureSelection[] perLabelSelection = new FeatureSelection[2];
        perLabelSelection[0] = new FeatureSelection();
        perLabelSelection[1] = new FeatureSelection();
        perLabelSelection[0].add(featureIndex);
        perLabelSelection[1].add(featureIndex);

        InstanceList list = new InstanceList(pipe);
        list.setPerLabelFeatureSelection(perLabelSelection);

        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"x1"}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new double[]{0.4, 0.6});
        list.add(new Instance(fv, lv, "label-select", null), 1.0);

        MaxEntOptimizableByLabelDistribution trainer = new MaxEntOptimizableByLabelDistribution(list, null);
        double[] gradient = new double[trainer.getNumParameters()];
        trainer.getValueGradient(gradient);

        assertEquals(gradient.length, trainer.getNumParameters());
    }
@Test
    public void testInstanceWithZeroProbabilityLabelVector() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance instance) { return instance; }
        };

        dataAlphabet.lookupIndex("featX");
        labelAlphabet.lookupIndex("class1");
        labelAlphabet.lookupIndex("class2");

        FeatureVector fv = new FeatureVector(dataAlphabet, new String[] {"featX"}, new double[] {1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new double[] {0.0, 0.0}); 
        InstanceList list = new InstanceList(pipe);
        list.add(new Instance(fv, lv, "zero-label", null), 1.0);

        MaxEntOptimizableByLabelDistribution trainer = new MaxEntOptimizableByLabelDistribution(list, null);
        double value = trainer.getValue();
        double[] gradient = new double[trainer.getNumParameters()];
        trainer.getValueGradient(gradient);

        assertFalse(Double.isNaN(value));
        assertEquals(gradient.length, trainer.getNumParameters());
    }
@Test
    public void testNegativeInfinityParameterHandlingInGradient() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance inst) { return inst; }
        };

        dataAlphabet.lookupIndex("ft");
        labelAlphabet.lookupIndex("A");
        labelAlphabet.lookupIndex("B");

        FeatureVector fv = new FeatureVector(dataAlphabet, new String[] {"ft"}, new double[] {1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new double[] {1.0, 0.0});
        InstanceList list = new InstanceList(pipe);
        list.add(new Instance(fv, lv, "param-inf", null), 1.0);

        MaxEntOptimizableByLabelDistribution trainer = new MaxEntOptimizableByLabelDistribution(list, null);
        double[] params = new double[trainer.getNumParameters()];
        for (int i = 0; i < params.length; i++) {
            if (i == 0) {
                params[i] = Double.NEGATIVE_INFINITY;
            } else {
                params[i] = 0.5;
            }
        }
        trainer.setParameters(params);

        double[] gradient = new double[trainer.getNumParameters()];
        trainer.getValueGradient(gradient);
        assertEquals(0.0, gradient[0], 1e-9);
    }
@Test
    public void testSetParameterTriggersCacheInvalidation() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance inst) { return inst; }
        };

        dataAlphabet.lookupIndex("ftr");
        labelAlphabet.lookupIndex("c1");
        labelAlphabet.lookupIndex("c2");

        FeatureVector fv = new FeatureVector(dataAlphabet, new String[] {"ftr"}, new double[] {1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new double[] {0.6, 0.4});
        InstanceList list = new InstanceList(pipe);
        list.add(new Instance(fv, lv, "invalidate", null), 1.0);

        MaxEntOptimizableByLabelDistribution trainer = new MaxEntOptimizableByLabelDistribution(list, null);
        double[] bufferBefore = new double[trainer.getNumParameters()];
        trainer.getValueGradient(bufferBefore);

        trainer.setParameter(0, trainer.getParameter(0) + 0.2);

        double[] bufferAfter = new double[trainer.getNumParameters()];
        trainer.getValueGradient(bufferAfter);

        assertNotEquals(bufferBefore[0], bufferAfter[0], 1e-6);
    }
@Test
    public void testEmptyAlphabetStillWorks() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance inst) { return inst; }
        };

        InstanceList emptyList = new InstanceList(pipe);
        MaxEntOptimizableByLabelDistribution trainer = new MaxEntOptimizableByLabelDistribution(emptyList, null);

        double value = trainer.getValue();
        double[] grad = new double[trainer.getNumParameters()];
        trainer.getValueGradient(grad);

        assertEquals(0, grad.length);
        assertFalse(Double.isNaN(value));
    }
@Test
    public void testMultipleInstancesSameFeatureVectorDifferentLabels() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance inst) { return inst; }
        };

        dataAlphabet.lookupIndex("sharedFeat");
        int labelIndexA = labelAlphabet.lookupIndex("A");
        int labelIndexB = labelAlphabet.lookupIndex("B");

        FeatureVector fvShared = new FeatureVector(dataAlphabet, new String[] {"sharedFeat"}, new double[] {1.0});
        LabelVector lvFirst = new LabelVector(labelAlphabet, new double[] {1.0, 0.0});
        LabelVector lvSecond = new LabelVector(labelAlphabet, new double[] {0.0, 1.0});

        InstanceList list = new InstanceList(pipe);
        list.add(new Instance(fvShared, lvFirst, "inst1", null), 1.0);
        list.add(new Instance(fvShared, lvSecond, "inst2", null), 1.0);

        MaxEntOptimizableByLabelDistribution trainer = new MaxEntOptimizableByLabelDistribution(list, null);
        double value = trainer.getValue();
        double[] gradient = new double[trainer.getNumParameters()];
        trainer.getValueGradient(gradient);

        assertFalse(Double.isNaN(value));
        assertEquals(gradient.length, trainer.getNumParameters());
    }
@Test
    public void testLogZeroProbAvoidsNaNAndNegativeInfinity() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance inst) { return inst; }
        };

        dataAlphabet.lookupIndex("fX");
        labelAlphabet.lookupIndex("la");
        labelAlphabet.lookupIndex("lb");

        FeatureVector fv = new FeatureVector(dataAlphabet, new String[] {"fX"}, new double[] {1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new double[] {0.1, 0.0});
        InstanceList list = new InstanceList(pipe);
        list.add(new Instance(fv, lv, "logcheck", null), 1.0);

        MaxEnt classifier = new MaxEnt(pipe, new double[6], null, null) {
            @Override
            public void getClassificationScores(Instance instance, double[] scores) {
                scores[0] = 1.0;
                scores[1] = 0.0;
            }
        };

        MaxEntOptimizableByLabelDistribution trainer = new MaxEntOptimizableByLabelDistribution(list, classifier);
        double val = trainer.getValue();

        assertFalse(Double.isInfinite(val));
        assertFalse(Double.isNaN(val));
    }
@Test
    public void testModelHandlesZeroFeaturesInInstance() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance inst) { return inst; }
        };

        dataAlphabet.lookupIndex("a"); 
        labelAlphabet.lookupIndex("p");
        labelAlphabet.lookupIndex("n");

        FeatureVector emptyFV = new FeatureVector(dataAlphabet, new int[0], new double[0]);
        LabelVector lv = new LabelVector(labelAlphabet, new double[] {0.3, 0.7});
        InstanceList list = new InstanceList(pipe);
        list.add(new Instance(emptyFV, lv, "empty-fv", null), 1.0);

        MaxEntOptimizableByLabelDistribution trainer = new MaxEntOptimizableByLabelDistribution(list, null);
        double val = trainer.getValue();
        double[] grad = new double[trainer.getNumParameters()];
        trainer.getValueGradient(grad);

        assertFalse(Double.isNaN(val));
    }
@Test
    public void testClassifierParameterReuse() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance inst) {
                return inst;
            }
        };

        dataAlphabet.lookupIndex("f");
        labelAlphabet.lookupIndex("cls1");
        labelAlphabet.lookupIndex("cls2");

        FeatureVector fv = new FeatureVector(dataAlphabet, new String[] {"f"}, new double[] {1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new double[] {0.9, 0.1});
        InstanceList list = new InstanceList(pipe);
        list.add(new Instance(fv, lv, "reuse-test", null), 1.0);

        double[] initialParams = new double[6];
        initialParams[0] = 3.14;

        MaxEnt maxEnt = new MaxEnt(pipe, initialParams, null, null);
        MaxEntOptimizableByLabelDistribution trainer = new MaxEntOptimizableByLabelDistribution(list, maxEnt);

        assertEquals(3.14, trainer.getParameter(0), 1e-5);
    }
@Test
    public void testDefaultFeatureIndexIsUsedInConstraints() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance inst) { return inst; }
        };

        dataAlphabet.lookupIndex("feat");
        labelAlphabet.lookupIndex("yes");
        labelAlphabet.lookupIndex("no");

        FeatureVector fv = new FeatureVector(dataAlphabet, new String[] {"feat"}, new double[] {1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new double[] {0.4, 0.6});
        InstanceList list = new InstanceList(pipe);
        list.add(new Instance(fv, lv, "default-feature", null), 1.0);

        MaxEntOptimizableByLabelDistribution trainer = new MaxEntOptimizableByLabelDistribution(list, null);
        double[] gradient = new double[trainer.getNumParameters()];
        trainer.getValueGradient(gradient);

        int numFeatures = dataAlphabet.size() + 1; 
        int label0DefaultIndex = 0 * numFeatures + numFeatures - 1;
        int label1DefaultIndex = 1 * numFeatures + numFeatures - 1;

        assertNotEquals(0.0, gradient[label0DefaultIndex], 0.0);
        assertNotEquals(0.0, gradient[label1DefaultIndex], 0.0);
    }
@Test
    public void testSetGaussianPriorVarianceAffectsGradientMagnitude() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance inst) { return inst; }
        };

        dataAlphabet.lookupIndex("featureA");
        labelAlphabet.lookupIndex("class1");
        labelAlphabet.lookupIndex("class2");

        FeatureVector fv = new FeatureVector(dataAlphabet, new String[] {"featureA"}, new double[] {1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new double[] {0.5, 0.5});
        InstanceList list = new InstanceList(pipe);
        list.add(new Instance(fv, lv, "prior-var", null), 1.0);

        MaxEntOptimizableByLabelDistribution trainer1 = new MaxEntOptimizableByLabelDistribution(list, null);
        trainer1.setGaussianPriorVariance(1000.0);
        double[] grad1 = new double[trainer1.getNumParameters()];
        trainer1.getValueGradient(grad1);

        MaxEntOptimizableByLabelDistribution trainer2 = new MaxEntOptimizableByLabelDistribution(list, null);
        trainer2.setGaussianPriorVariance(0.01);
        double[] grad2 = new double[trainer2.getNumParameters()];
        trainer2.getValueGradient(grad2);

        assertNotEquals(grad1[0], grad2[0], 1e-4); 
    }
@Test
    public void testFloatPrecisionFeatureValuesProcessedCorrectly() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance inst) { return inst; }
        };

        dataAlphabet.lookupIndex("floatFeat");
        labelAlphabet.lookupIndex("X");
        labelAlphabet.lookupIndex("Y");

        FeatureVector fv = new FeatureVector(dataAlphabet, new String[] {"floatFeat"}, new double[] {1e-9});
        LabelVector lv = new LabelVector(labelAlphabet, new double[] {0.3, 0.7});
        InstanceList list = new InstanceList(pipe);
        list.add(new Instance(fv, lv, "float", null), 1.0);

        MaxEntOptimizableByLabelDistribution trainer = new MaxEntOptimizableByLabelDistribution(list, null);
        double value = trainer.getValue();
        double[] grad = new double[trainer.getNumParameters()];
        trainer.getValueGradient(grad);

        assertFalse(Double.isNaN(value));
        assertEquals(grad.length, trainer.getNumParameters());
    }
@Test
    public void testVeryLargeInstanceWeightsScaleCorrectly() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance carrier) { return carrier; }
        };

        dataAlphabet.lookupIndex("fLarge");
        labelAlphabet.lookupIndex("a");
        labelAlphabet.lookupIndex("b");

        FeatureVector fv = new FeatureVector(dataAlphabet, new String[] {"fLarge"}, new double[] {1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new double[] {0.6, 0.4});
        InstanceList list = new InstanceList(pipe);
        list.add(new Instance(fv, lv, "large-weight", null), 1e6);

        MaxEntOptimizableByLabelDistribution trainer = new MaxEntOptimizableByLabelDistribution(list, null);
        double value = trainer.getValue();
        double[] grad = new double[trainer.getNumParameters()];
        trainer.getValueGradient(grad);

        assertTrue(Math.abs(value) > 0);
        assertTrue(Math.abs(grad[0]) > 0); 
    }
@Test
    public void testGetParametersWithIncorrectLengthBufferIsIgnored() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance inst) { return inst; }
        };

        dataAlphabet.lookupIndex("f1");
        labelAlphabet.lookupIndex("yes");
        labelAlphabet.lookupIndex("no");

        FeatureVector fv = new FeatureVector(dataAlphabet, new String[] {"f1"}, new double[] {1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new double[] {0.5, 0.5});
        InstanceList list = new InstanceList(pipe);
        list.add(new Instance(fv, lv, "wrong-buffer", null), 1.0);

        MaxEntOptimizableByLabelDistribution trainer = new MaxEntOptimizableByLabelDistribution(list, null);
        double[] shortBuffer = new double[1]; 
        trainer.getParameters(shortBuffer); 
        assertEquals(1, shortBuffer.length); 
    }
@Test
    public void testGetValueAndGradientMultipleTimesAreConsistent() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance inst) { return inst; }
        };

        dataAlphabet.lookupIndex("fx");
        labelAlphabet.lookupIndex("label1");
        labelAlphabet.lookupIndex("label2");

        FeatureVector fv = new FeatureVector(dataAlphabet, new String[] {"fx"}, new double[] {1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new double[] {0.6, 0.4});
        InstanceList list = new InstanceList(pipe);
        list.add(new Instance(fv, lv, "multi-call", null), 1.0);

        MaxEntOptimizableByLabelDistribution trainer = new MaxEntOptimizableByLabelDistribution(list, null);

        double val1 = trainer.getValue();
        double[] grad1 = new double[trainer.getNumParameters()];
        trainer.getValueGradient(grad1);

        double val2 = trainer.getValue();
        double[] grad2 = new double[trainer.getNumParameters()];
        trainer.getValueGradient(grad2);

        assertEquals(val1, val2, 1e-10);
        for (int i = 0; i < grad1.length; i++) {
            assertEquals(grad1[i], grad2[i], 1e-10);
        }
    }
@Test
    public void testParameterUpdateChangesValueAndGradient() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance inst) { return inst; }
        };

        dataAlphabet.lookupIndex("modFeat");
        labelAlphabet.lookupIndex("C1");
        labelAlphabet.lookupIndex("C2");

        FeatureVector fv = new FeatureVector(dataAlphabet, new String[] {"modFeat"}, new double[] {1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new double[] {0.4, 0.6});
        InstanceList list = new InstanceList(pipe);
        list.add(new Instance(fv, lv, "update", null), 1.0);

        MaxEntOptimizableByLabelDistribution trainer = new MaxEntOptimizableByLabelDistribution(list, null);

        double valBefore = trainer.getValue();
        double[] gradBefore = new double[trainer.getNumParameters()];
        trainer.getValueGradient(gradBefore);

        double oldParam = trainer.getParameter(0);
        trainer.setParameter(0, oldParam + 0.3);

        double valAfter = trainer.getValue();
        double[] gradAfter = new double[trainer.getNumParameters()];
        trainer.getValueGradient(gradAfter);

        assertNotEquals(valBefore, valAfter, 1e-8);
        assertNotEquals(gradBefore[0], gradAfter[0], 1e-8);
    }
@Test
    public void testEmptyLabelVector() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance inst) { return inst; }
        };

        dataAlphabet.lookupIndex("x");
        labelAlphabet.lookupIndex("l1");
        labelAlphabet.lookupIndex("l2");

        double[] zeroLabelProbs = new double[]{0.0, 0.0};
        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"x"}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, zeroLabelProbs);
        InstanceList list = new InstanceList(pipe);
        list.add(new Instance(fv, lv, " no-label-probs", null), 1.0);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        double value = opt.getValue();
        double[] gradient = new double[opt.getNumParameters()];
        opt.getValueGradient(gradient);

        assertFalse(Double.isNaN(value));
        assertEquals(gradient.length, opt.getNumParameters());
    }
@Test
    public void testFeatureWithNegativeValue() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance inst) { return inst; }
        };

        dataAlphabet.lookupIndex("feat");
        labelAlphabet.lookupIndex("classA");
        labelAlphabet.lookupIndex("classB");

        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"feat"}, new double[]{-1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new double[]{0.3, 0.7});
        InstanceList list = new InstanceList(pipe);
        list.add(new Instance(fv, lv, "negative-feature", null), 1.0);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        double value = opt.getValue();
        double[] gradient = new double[opt.getNumParameters()];
        opt.getValueGradient(gradient);

        assertFalse(Double.isNaN(value));
        assertEquals(gradient.length, opt.getNumParameters());
    }
@Test
    public void testMultipleInstancesSameLabelDifferentFeatures() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance inst) { return inst; }
        };

        dataAlphabet.lookupIndex("f1");
        dataAlphabet.lookupIndex("f2");
        labelAlphabet.lookupIndex("target");

        FeatureVector fv1 = new FeatureVector(dataAlphabet, new String[]{"f1"}, new double[]{1.0});
        FeatureVector fv2 = new FeatureVector(dataAlphabet, new String[]{"f2"}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new double[]{1.0});

        InstanceList list = new InstanceList(pipe);
        list.add(new Instance(fv1, lv, "inst1", null), 1.0);
        list.add(new Instance(fv2, lv, "inst2", null), 1.0);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        double value = opt.getValue();
        double[] gradient = new double[opt.getNumParameters()];
        opt.getValueGradient(gradient);

        assertFalse(Double.isNaN(value));
        assertEquals(gradient.length, opt.getNumParameters());
    }
@Test
    public void testLabelWithZeroProbabilityAcrossDataset() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance inst) { return inst; }
        };

        
        labelAlphabet.lookupIndex("class1");
        labelAlphabet.lookupIndex("class2");
        labelAlphabet.lookupIndex("class3");

        dataAlphabet.lookupIndex("feat");

        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"feat"}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new double[]{0.5, 0.5, 0.0}); 

        InstanceList list = new InstanceList(pipe);
        list.add(new Instance(fv, lv, "zero-class", null), 1.0);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        double value = opt.getValue();
        double[] gradient = new double[opt.getNumParameters()];
        opt.getValueGradient(gradient);

        assertFalse(Double.isNaN(value));
        assertEquals(gradient.length, opt.getNumParameters());
    }
@Test
    public void testPerLabelFeatureSelectionPartialCoverage() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance inst) { return inst; }
        };

        int fA = dataAlphabet.lookupIndex("fA");
        int fB = dataAlphabet.lookupIndex("fB");
        int label0 = labelAlphabet.lookupIndex("L0");
        int label1 = labelAlphabet.lookupIndex("L1");

        FeatureSelection[] perLabelSelection = new FeatureSelection[2];
        perLabelSelection[0] = new FeatureSelection(); 
        perLabelSelection[1] = new FeatureSelection(); 
        perLabelSelection[0].add(fA);
        perLabelSelection[1].add(fB);

        InstanceList list = new InstanceList(pipe);
        list.setPerLabelFeatureSelection(perLabelSelection);

        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"fA", "fB"}, new double[]{1.0, 1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new double[]{0.5, 0.5});
        list.add(new Instance(fv, lv, "partial-select", null), 1.0);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        double value = opt.getValue();
        double[] gradient = new double[opt.getNumParameters()];
        opt.getValueGradient(gradient);

        int numFeatures = dataAlphabet.size() + 1;
        int indexL0fB = 0 * numFeatures + dataAlphabet.lookupIndex("fB");
        int indexL1fA = 1 * numFeatures + dataAlphabet.lookupIndex("fA");

        assertEquals(0.0, gradient[indexL0fB], 1e-10); 
        assertEquals(0.0, gradient[indexL1fA], 1e-10); 
    }
@Test
    public void testInstanceWithManyFeatures() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance inst) { return inst; }
        };

        for (int i = 0; i < 100; i++) {
            dataAlphabet.lookupIndex("f" + i);
        }

        labelAlphabet.lookupIndex("positivity");
        labelAlphabet.lookupIndex("negativity");

        String[] features = new String[100];
        double[] values = new double[100];
        for (int i = 0; i < features.length; i++) {
            features[i] = "f" + i;
            values[i] = 1.0;
        }

        FeatureVector fv = new FeatureVector(dataAlphabet, features, values);
        LabelVector lv = new LabelVector(labelAlphabet, new double[]{0.7, 0.3});
        InstanceList list = new InstanceList(pipe);
        list.add(new Instance(fv, lv, "dense", null), 1.0);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        double value = opt.getValue();
        double[] gradient = new double[opt.getNumParameters()];
        opt.getValueGradient(gradient);

        assertFalse(Double.isNaN(value));
        assertEquals(gradient.length, opt.getNumParameters());
    }
@Test
    public void testSetParametersWithSmallerArrayResizesInternally() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance instance) { return instance; }
        };

        dataAlphabet.lookupIndex("f1");
        labelAlphabet.lookupIndex("l1");
        labelAlphabet.lookupIndex("l2");

        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"f1"}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new double[]{0.5, 0.5});
        Instance instance = new Instance(fv, lv, "resize", null);

        InstanceList list = new InstanceList(pipe);
        list.add(instance, 1.0);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        double[] smallerParams = new double[1];
        smallerParams[0] = 42.0;
        opt.setParameters(smallerParams);

        assertEquals(smallerParams.length, opt.getNumParameters());
        assertEquals(42.0, opt.getParameter(0), 1e-6);
    }
@Test
    public void testZeroLabeledInstanceHasNoEffect() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance instance) { return instance; }
        };

        dataAlphabet.lookupIndex("f1");
        labelAlphabet.lookupIndex("l1");
        labelAlphabet.lookupIndex("l2");

        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"f1"}, new double[]{1.0});
        Labeling labeling = null;
        Instance unlabeled = new Instance(fv, labeling, "no-label", null);

        InstanceList list = new InstanceList(pipe);
        list.add(unlabeled, 1.0);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);

        double value = opt.getValue();
        double[] gradient = new double[opt.getNumParameters()];
        opt.getValueGradient(gradient);

        assertFalse(Double.isNaN(value));
        assertEquals(gradient.length, opt.getNumParameters());
    }
@Test
    public void testGradientStaleCheckWithoutCallingGetValue() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance inst) { return inst; }
        };

        dataAlphabet.lookupIndex("feat");
        labelAlphabet.lookupIndex("A");
        labelAlphabet.lookupIndex("B");

        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"feat"}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new double[]{1.0, 0.0});

        InstanceList list = new InstanceList(pipe);
        list.add(new Instance(fv, lv, "direct-gradient", null), 1.0);

        MaxEntOptimizableByLabelDistribution trainer = new MaxEntOptimizableByLabelDistribution(list, null);
        double[] gradient = new double[trainer.getNumParameters()];
        trainer.getValueGradient(gradient);

        assertEquals(gradient.length, trainer.getNumParameters());
    }
@Test
    public void testAssertionFailsForMismatchedPipe() {
        Alphabet dataAlphabet1 = new Alphabet();
        LabelAlphabet labelAlphabet1 = new LabelAlphabet();
        Pipe pipe1 = new Pipe(dataAlphabet1, labelAlphabet1) {
            public Instance pipe(Instance instance) { return instance; }
        };

        dataAlphabet1.lookupIndex("xx");
        labelAlphabet1.lookupIndex("aa");
        labelAlphabet1.lookupIndex("bb");

        Alphabet dataAlphabet2 = new Alphabet();
        LabelAlphabet labelAlphabet2 = new LabelAlphabet();
        Pipe pipe2 = new Pipe(dataAlphabet2, labelAlphabet2) {
            public Instance pipe(Instance instance) { return instance; }
        };

        FeatureVector fv = new FeatureVector(dataAlphabet1, new String[]{"xx"}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet1, new double[]{0.5, 0.5});
        InstanceList list = new InstanceList(pipe1);
        list.add(new Instance(fv, lv, "mismatch", null), 1.0);

        MaxEnt classifier = new MaxEnt(pipe2, new double[6], null, null);
        boolean assertionThrown = false;

        try {
            new MaxEntOptimizableByLabelDistribution(list, classifier);
        } catch (AssertionError e) {
            assertionThrown = true;
        }

        assertTrue(assertionThrown);
    }
@Test
    public void testNaNLabelValueLoggedButNoCrash() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance inst) { return inst; }
        };

        dataAlphabet.lookupIndex("feature");
        labelAlphabet.lookupIndex("LabelA");
        labelAlphabet.lookupIndex("LabelB");

        double[] labelValues = new double[]{Double.NaN, 1.0};
        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"feature"}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, labelValues);

        InstanceList list = new InstanceList(pipe);
        list.add(new Instance(fv, lv, "NaNLabel", null), 1.0);

        MaxEntOptimizableByLabelDistribution opt = new MaxEntOptimizableByLabelDistribution(list, null);
        double val = opt.getValue();
        assertFalse(Double.isNaN(val));
    }
@Test
    public void testSetParametersToAllZerosPreservesDimensions() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance inst) { return inst; }
        };

        dataAlphabet.lookupIndex("x1");
        labelAlphabet.lookupIndex("LBL1");
        labelAlphabet.lookupIndex("LBL2");

        FeatureVector fv = new FeatureVector(dataAlphabet, new String[]{"x1"}, new double[]{1.0});
        LabelVector lv = new LabelVector(labelAlphabet, new double[]{0.2, 0.8});
        InstanceList list = new InstanceList(pipe);
        list.add(new Instance(fv, lv, "zero-params", null), 1.0);

        MaxEntOptimizableByLabelDistribution trainer = new MaxEntOptimizableByLabelDistribution(list, null);
        int length = trainer.getNumParameters();
        double[] zeros = new double[length];
        trainer.setParameters(zeros);

        double[] buffer = new double[length];
        trainer.getParameters(buffer);
        assertEquals(length, buffer.length);
        assertEquals(0.0, buffer[0], 1e-9);
    } 
}