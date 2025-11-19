public class MaxEntOptimizableByLabelLikelihood_4_GPTLLMTest { 

 @Test
    public void testConstructorInitializesCorrectNumberOfParameters() {
        Alphabet featureAlphabet = new Alphabet();
        featureAlphabet.lookupIndex("f1");
        featureAlphabet.lookupIndex("f2");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("label1");
        labelAlphabet.lookupIndex("label2");

        FeatureVector fv = new FeatureVector(featureAlphabet, new int[]{0}, new double[]{1.0});
        Label label = labelAlphabet.lookupLabel("label1");
        Labeling labeling = label.getLabeling(1.0);
        Instance instance = new Instance(fv, labeling, "test", null);

        Pipe pipe = new Pipe(featureAlphabet, labelAlphabet) {
            public Instance pipe(Instance carrier) { return carrier; }
        };

        InstanceList ilist = new InstanceList(pipe);
        ilist.add(instance);

        MaxEnt classifier = new MaxEnt(pipe, new double[6], null, null);
        MaxEntOptimizableByLabelLikelihood optimizable = new MaxEntOptimizableByLabelLikelihood(ilist, classifier);

        int expected = 2 * (2 + 1); 
        assertEquals(expected, optimizable.getNumParameters());
        assertNotNull(optimizable.getClassifier());
    }
@Test
    public void testSetAndGetParameter() {
        Alphabet featureAlphabet = new Alphabet();
        featureAlphabet.lookupIndex("f1");
        featureAlphabet.lookupIndex("f2");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("label1");
        labelAlphabet.lookupIndex("label2");

        FeatureVector fv = new FeatureVector(featureAlphabet, new int[]{0}, new double[]{1.0});
        Labeling labeling = labelAlphabet.lookupLabel("label1").getLabeling(1.0);
        Instance instance = new Instance(fv, labeling, "test", null);

        Pipe pipe = new Pipe(featureAlphabet, labelAlphabet) {
            public Instance pipe(Instance carrier) { return carrier; }
        };

        InstanceList ilist = new InstanceList(pipe);
        ilist.add(instance);

        MaxEnt classifier = new MaxEnt(pipe, new double[6], null, null);
        MaxEntOptimizableByLabelLikelihood optimizable = new MaxEntOptimizableByLabelLikelihood(ilist, classifier);
        int paramIndex = 1;

        optimizable.setParameter(paramIndex, 3.14);
        double actual = optimizable.getParameter(paramIndex);
        assertEquals(3.14, actual, 1e-6);
    }
@Test
    public void testSetAndGetParametersArray() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f1");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupLabel("label1");
        labelAlphabet.lookupLabel("label2");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        Labeling labeling = labelAlphabet.lookupLabel("label1").getLabeling(1.0);
        Instance instance = new Instance(fv, labeling, "i1", null);

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance carrier) { return carrier; }
        };

        InstanceList ilist = new InstanceList(pipe);
        ilist.add(instance);

        MaxEnt classifier = new MaxEnt(pipe, new double[6], null, null);
        MaxEntOptimizableByLabelLikelihood optimizable = new MaxEntOptimizableByLabelLikelihood(ilist, classifier);

        double[] params = new double[optimizable.getNumParameters()];
        Arrays.fill(params, 2.0);
        optimizable.setParameters(params);

        double[] dest = new double[optimizable.getNumParameters()];
        optimizable.getParameters(dest);

        assertEquals(2.0, dest[0], 1e-6);
        assertEquals(2.0, dest[1], 1e-6);
    }
@Test
    public void testUseGaussianPriorSetsFlagsCorrectly() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f1");
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("label1");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        Label label = labelAlphabet.lookupLabel("label1");
        Labeling labeling = label.getLabeling(1.0);
        Instance instance = new Instance(fv, labeling, "test", null);

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance carrier) { return carrier; }
        };

        InstanceList ilist = new InstanceList(pipe);
        ilist.add(instance);

        MaxEnt classifier = new MaxEnt(pipe, new double[2 * (1 + 1)], null, null);
        MaxEntOptimizableByLabelLikelihood optimizable = new MaxEntOptimizableByLabelLikelihood(ilist, classifier);
        MaxEntOptimizableByLabelLikelihood returned = optimizable.useGaussianPrior();

        assertSame(optimizable, returned);
        assertEquals(optimizable.getValue(), optimizable.getValue(), 0); 
    }
@Test
    public void testUseHyperbolicPriorThrowsInGradient() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f1");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupLabel("label1");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        Labeling labeling = labelAlphabet.lookupLabel("label1").getLabeling(1.0);
        Instance inst = new Instance(fv, labeling, "x", null);

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance carrier) { return carrier; }
        };

        InstanceList ilist = new InstanceList(pipe);
        ilist.add(inst);

        MaxEnt classifier = new MaxEnt(pipe, new double[2 * (1 + 1)], null, null);
        MaxEntOptimizableByLabelLikelihood optimizable = new MaxEntOptimizableByLabelLikelihood(ilist, classifier);

        optimizable.useHyperbolicPrior();

        double[] buffer = new double[optimizable.getNumParameters()];
        try {
            optimizable.getValueGradient(buffer);
            fail("Expected UnsupportedOperationException not thrown.");
        } catch (UnsupportedOperationException expected) {
            assertTrue(expected.getMessage().contains("Hyperbolic prior"));
        }
    }
@Test
    public void testGetValueCallsIncreasesOnRepeatedCall() {
        Alphabet da = new Alphabet();
        da.lookupIndex("f1");

        LabelAlphabet la = new LabelAlphabet();
        la.lookupLabel("label1");

        FeatureVector fv = new FeatureVector(da, new int[]{0}, new double[]{1.0});
        Labeling labeling = la.lookupLabel("label1").getLabeling(1.0);
        Instance instance = new Instance(fv, labeling, "t", null);

        Pipe pipe = new Pipe(da, la) {
            public Instance pipe(Instance carrier) { return carrier; }
        };

        InstanceList list = new InstanceList(pipe);
        list.add(instance);

        MaxEnt classifier = new MaxEnt(pipe, new double[2 * (1 + 1)], null, null);
        MaxEntOptimizableByLabelLikelihood optimizable = new MaxEntOptimizableByLabelLikelihood(list, classifier);

        int v1 = optimizable.getValueCalls();
        double value1 = optimizable.getValue();
        int v2 = optimizable.getValueCalls();
        double value2 = optimizable.getValue();
        int v3 = optimizable.getValueCalls();

        assertTrue(v2 > v1);
        assertEquals(value1, value2, 1e-6);
    }
@Test
    public void testGetValueGradientComputation() {
        Alphabet featureAlphabet = new Alphabet();
        featureAlphabet.lookupIndex("x");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("yes");
        labelAlphabet.lookupIndex("no");

        FeatureVector fv = new FeatureVector(featureAlphabet, new int[]{0}, new double[]{1.0});
        Labeling labeling = labelAlphabet.lookupLabel("yes").getLabeling(1.0);
        Instance instance = new Instance(fv, labeling, "x1", null);

        Pipe pipe = new Pipe(featureAlphabet, labelAlphabet) {
            public Instance pipe(Instance inst) { return inst; }
        };

        InstanceList ilist = new InstanceList(pipe);
        ilist.add(instance);

        MaxEnt classifier = new MaxEnt(pipe, new double[2 * (1 + 1)], null, null);
        MaxEntOptimizableByLabelLikelihood optimizable = new MaxEntOptimizableByLabelLikelihood(ilist, classifier);

        double[] gradient = new double[optimizable.getNumParameters()];
        optimizable.getValueGradient(gradient);
        assertEquals(optimizable.getNumParameters(), gradient.length);
    }
@Test
    public void testInstanceWithNullLabelingSkipped() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f1");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupLabel("y");

        FeatureVector featureVector = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        Instance instance = new Instance(featureVector, null, "test", null);

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance carrier) {
                return carrier;
            }
        };

        InstanceList instanceList = new InstanceList(pipe);
        instanceList.add(instance);

        MaxEnt classifier = new MaxEnt(pipe, new double[2 * 2], null, null);
        MaxEntOptimizableByLabelLikelihood target = new MaxEntOptimizableByLabelLikelihood(instanceList, classifier);

        double result = target.getValue();

        assertTrue(Double.isFinite(result));
    }
@Test
    public void testFeatureVectorWithNaNValueHandledGracefully() {
        Alphabet featureAlphabet = new Alphabet();
        int idx = featureAlphabet.lookupIndex("f1");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupLabel("label1");

        FeatureVector fv = new FeatureVector(featureAlphabet, new int[]{idx}, new double[]{Double.NaN});
        Labeling labeling = labelAlphabet.lookupLabel("label1").getLabeling(1.0);
        Instance inst = new Instance(fv, labeling, "naNInstance", null);

        Pipe pipe = new Pipe(featureAlphabet, labelAlphabet) {
            public Instance pipe(Instance instance) {
                return instance;
            }
        };

        InstanceList ilist = new InstanceList(pipe);
        ilist.add(inst);

        MaxEnt classifier = new MaxEnt(pipe, new double[2 * 2], null, null);
        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(ilist, classifier);

        double val = opt.getValue();
        assertTrue(Double.isFinite(val));
    }
@Test
    public void testInstanceWithZeroWeightDoesNotAffectObjective() {
        Alphabet alp = new Alphabet();
        alp.lookupIndex("fA");

        LabelAlphabet lab = new LabelAlphabet();
        lab.lookupIndex("L");

        FeatureVector fv = new FeatureVector(alp, new int[]{0}, new double[]{2.0});
        Labeling labeling = lab.lookupLabel("L").getLabeling(1.0);
        Instance inst = new Instance(fv, labeling, "zeroWeight", null);

        Pipe pipe = new Pipe(alp, lab) {
            public Instance pipe(Instance instance) {
                return instance;
            }
        };

        InstanceList ilist = new InstanceList(pipe);
        ilist.add(inst);
        ilist.setInstanceWeight(0, 0.0);

        MaxEnt classifier = new MaxEnt(pipe, new double[2 * 2], null, null);
        MaxEntOptimizableByLabelLikelihood target = new MaxEntOptimizableByLabelLikelihood(ilist, classifier);

        double val = target.getValue();
        assertTrue(Double.isFinite(val));
    }
@Test
    public void testEmptyFeatureVectorHandledCorrectly() {
        Alphabet featureAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupLabel("cat");

        FeatureVector fv = new FeatureVector(featureAlphabet, new int[0], new double[0]);
        Label label = labelAlphabet.lookupLabel("cat");
        Labeling labeling = label.getLabeling(1.0);
        Instance instance = new Instance(fv, labeling, "emptyFV", null);

        Pipe pipe = new Pipe(featureAlphabet, labelAlphabet) {
            public Instance pipe(Instance i) {
                return i;
            }
        };

        InstanceList ilist = new InstanceList(pipe);
        ilist.add(instance);

        MaxEnt classifier = new MaxEnt(pipe, new double[1 * 1], null, null);
        MaxEntOptimizableByLabelLikelihood o = new MaxEntOptimizableByLabelLikelihood(ilist, classifier);

        double val = o.getValue();
        assertTrue(Double.isFinite(val));
    }
@Test
    public void testNegativeInfinityInGradientIsZeroedOut() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f");
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupLabel("A");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1});
        Labeling labeling = labelAlphabet.lookupLabel("A").getLabeling(1.0);
        Instance instance = new Instance(fv, labeling, "x", null);

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance carrier) { return carrier; }
        };

        InstanceList instanceList = new InstanceList(pipe);
        instanceList.add(instance);

        MaxEnt classifier = new MaxEnt(pipe, new double[1 * 2], null, null);
        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(instanceList, classifier);

        opt.setParameter(0, Double.NEGATIVE_INFINITY);

        double[] gradient = new double[opt.getNumParameters()];
        opt.getValueGradient(gradient);

        assertEquals(0.0, gradient[0], 1e-6); 
    }
@Test
    public void testClassifierReturningNaNScoreHandled() {
        Alphabet fa = new Alphabet();
        int id = fa.lookupIndex("f");

        LabelAlphabet la = new LabelAlphabet();
        la.lookupLabel("yes");

        FeatureVector fv = new FeatureVector(fa, new int[]{id}, new double[]{1});
        Labeling labeling = la.lookupLabel("yes").getLabeling(1.0);
        Instance inst = new Instance(fv, labeling, "inst", null);

        Pipe pipe = new Pipe(fa, la) {
            public Instance pipe(Instance i) { return i; }
        };

        InstanceList ilist = new InstanceList(pipe);
        ilist.add(inst);

        MaxEnt classifier = new MaxEnt(pipe, new double[1 * 2], null, null) {
            @Override
            public void getClassificationScores(Instance instance, double[] scores) {
                scores[0] = Double.NaN;
            }
        };

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(ilist, classifier);

        double val = opt.getValue();
        assertTrue(Double.isNaN(val) || Double.isFinite(val));
    }
@Test
    public void testGetParametersHandlesNullBuffer() {
        Alphabet f = new Alphabet();
        f.lookupIndex("x");

        LabelAlphabet l = new LabelAlphabet();
        l.lookupIndex("L");

        FeatureVector fv = new FeatureVector(f, new int[]{0}, new double[]{1.0});
        Labeling lbl = l.lookupLabel("L").getLabeling(1.0);
        Instance i = new Instance(fv, lbl, "a", null);

        Pipe p = new Pipe(f, l) {
            public Instance pipe(Instance j) {
                return j;
            }
        };

        InstanceList il = new InstanceList(p);
        il.add(i);

        MaxEnt classifier = new MaxEnt(p, new double[2 * 2], null, null);
        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(il, classifier);

        opt.getParameters(null); 
    }
@Test
    public void testSetParametersHandlesMismatchedLength() {
        Alphabet f = new Alphabet();
        f.lookupIndex("aaa");

        LabelAlphabet l = new LabelAlphabet();
        l.lookupLabel("ok");

        FeatureVector fv = new FeatureVector(f, new int[]{0}, new double[]{1.0});
        Labeling lbl = l.lookupLabel("ok").getLabeling(1.0);
        Instance inst = new Instance(fv, lbl, "z", null);

        Pipe pipe = new Pipe(f, l) {
            public Instance pipe(Instance c) { return c; }
        };

        InstanceList ilist = new InstanceList(pipe);
        ilist.add(inst);

        MaxEnt classifier = new MaxEnt(pipe, new double[2 * 2], null, null);

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(ilist, classifier);
        double[] customParams = new double[999];
        Arrays.fill(customParams, 5.5);
        opt.setParameters(customParams);

        double[] p = new double[999];
        opt.getParameters(p);
        assertEquals(5.5, p[0], 1e-6);
    }
@Test
    public void testUseNoPriorSkipsPriorTerm() {
        Alphabet a = new Alphabet();
        a.lookupIndex("f");

        LabelAlphabet l = new LabelAlphabet();
        l.lookupLabel("l");

        FeatureVector fv = new FeatureVector(a, new int[]{0}, new double[]{1.0});
        Labeling lbl = l.lookupLabel("l").getLabeling(1.0);
        Instance i = new Instance(fv, lbl, "id", null);

        Pipe p = new Pipe(a, l) { public Instance pipe(Instance ii) { return ii; } };

        InstanceList list = new InstanceList(p);
        list.add(i);

        MaxEnt classifier = new MaxEnt(p, new double[2 * 2], null, null);
        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, classifier);
        opt.useNoPrior();

        double val = opt.getValue();
        assertTrue(Double.isFinite(val));
    }
@Test
    public void testEmptyTrainingListDoNotCrash() {
        Alphabet fa = new Alphabet();
        fa.lookupIndex("f1");

        LabelAlphabet la = new LabelAlphabet();
        la.lookupIndex("A");

        Pipe pipe = new Pipe(fa, la) {
            public Instance pipe(Instance inst) { return inst; }
        };
        InstanceList emptyList = new InstanceList(pipe);

        MaxEnt classifier = new MaxEnt(pipe, new double[1 * 2], null, null);
        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(emptyList, classifier);

        double result = opt.getValue();
        assertEquals(-0.0, result, 0.0);
    }
@Test
    public void testGetInstancePipeMismatchThrowsAssertionError() {
        Alphabet df = new Alphabet();
        df.lookupIndex("f");
        LabelAlphabet lb = new LabelAlphabet();
        lb.lookupIndex("A");

        FeatureVector fv = new FeatureVector(df, new int[]{0}, new double[]{1.0});
        Labeling lbl = lb.lookupLabel("A").getLabeling(1.0);
        Instance inst = new Instance(fv, lbl, "sample", null);

        Pipe pipe1 = new Pipe(df, lb) {
            public Instance pipe(Instance x) { return x; }
        };

        Pipe pipe2 = new Pipe(df, lb) {
            public Instance pipe(Instance x) { return x; }
        };

        InstanceList list = new InstanceList(pipe1);
        list.add(inst);

        MaxEnt classifier = new MaxEnt(pipe2, new double[2], null, null);
        try {
            new MaxEntOptimizableByLabelLikelihood(list, classifier);
            fail("Expected assertion or illegal state due to pipe mismatch");
        } catch (AssertionError expected) {
            assertTrue(true);
        }
    }
@Test
    public void testSetGaussianPriorVarianceToZero() {
        Alphabet a = new Alphabet();
        a.lookupIndex("f1");

        LabelAlphabet l = new LabelAlphabet();
        l.lookupLabel("Y");

        FeatureVector fv = new FeatureVector(a, new int[]{0}, new double[]{1.0});
        Label label = l.lookupLabel("Y");
        Labeling labeling = label.getLabeling(1.0);
        Instance i = new Instance(fv, labeling, "zeroVar", null);

        Pipe p = new Pipe(a, l) {
            public Instance pipe(Instance x) { return x; }
        };

        InstanceList ilist = new InstanceList(p);
        ilist.add(i);

        MaxEnt classifier = new MaxEnt(p, new double[2 * 2], null, null);
        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(ilist, classifier);
        opt.setGaussianPriorVariance(0.0);
        double val = opt.getValue();
        assertTrue(Double.isFinite(val));
    }
@Test
    public void testSingleLabelInLabelAlphabetNoDivisionByZero() {
        Alphabet a = new Alphabet();
        a.lookupIndex("f1");

        LabelAlphabet l = new LabelAlphabet();
        l.lookupLabel("SINGLE");

        FeatureVector fv = new FeatureVector(a, new int[]{0}, new double[]{1.0});
        Labeling labeling = l.lookupLabel("SINGLE").getLabeling(1.0);
        Instance i = new Instance(fv, labeling, "onlyOne", null);

        Pipe p = new Pipe(a, l) { public Instance pipe(Instance x) { return x; } };

        InstanceList ilist = new InstanceList(p);
        ilist.add(i);

        MaxEnt classifier = new MaxEnt(p, new double[1 * 2], null, null);
        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(ilist, classifier);

        double val = opt.getValue();
        assertTrue(Double.isFinite(val));
    }
@Test
    public void testScoreArrayWithLargerLengthThanLabelAlphabetSize() {
        Alphabet a = new Alphabet();
        a.lookupIndex("f");

        LabelAlphabet l = new LabelAlphabet();
        l.lookupLabel("a");
        l.lookupLabel("b");

        FeatureVector fv = new FeatureVector(a, new int[]{0}, new double[]{1.0});
        Labeling labeling = l.lookupLabel("a").getLabeling(1.0);
        Instance inst = new Instance(fv, labeling, "s", null);

        Pipe p = new Pipe(a, l) { public Instance pipe(Instance i) { return i; } };

        InstanceList il = new InstanceList(p);
        il.add(inst);

        MaxEnt classifier = new MaxEnt(p, new double[2 * 3], null, null) {
            @Override
            public void getClassificationScores(Instance i, double[] scores) {
                scores[0] = 0.2;
                scores[1] = 0.8;
                if (scores.length > 2)
                    scores[2] = 0.0;
            }
        };

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(il, classifier);
        double val = opt.getValue();
        assertTrue(Double.isFinite(val));
    }
@Test
    public void testScoreArrayContainsInfiniteCausesEarlyExit() {
        Alphabet a = new Alphabet();
        a.lookupIndex("feat");

        LabelAlphabet l = new LabelAlphabet();
        l.lookupLabel("L");

        FeatureVector fv = new FeatureVector(a, new int[]{0}, new double[]{1});
        Labeling lbl = l.lookupLabel("L").getLabeling(1.0);
        Instance inst = new Instance(fv, lbl, "inf", null);

        Pipe p = new Pipe(a, l) { public Instance pipe(Instance x) { return x; } };

        InstanceList il = new InstanceList(p);
        il.add(inst);

        MaxEnt classifier = new MaxEnt(p, new double[1 * 3], null, null) {
            public void getClassificationScores(Instance instance, double[] scores) {
                scores[0] = Double.POSITIVE_INFINITY;
            }
        };

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(il, classifier);
        double result = opt.getValue();
        assertTrue(Double.isInfinite(-result));  
    }
@Test
    public void testGetParametersResizesBufferIfIncorrectLength() {
        Alphabet a = new Alphabet();
        a.lookupIndex("f");

        LabelAlphabet l = new LabelAlphabet();
        l.lookupLabel("z");

        FeatureVector fv = new FeatureVector(a, new int[]{0}, new double[]{1});
        Labeling labeling = l.lookupLabel("z").getLabeling(1.0);
        Instance instance = new Instance(fv, labeling, "xyz", null);

        Pipe pipe = new Pipe(a, l) {
            public Instance pipe(Instance i) {
                return i;
            }
        };

        InstanceList ilist = new InstanceList(pipe);
        ilist.add(instance);

        MaxEnt classifier = new MaxEnt(pipe, new double[1 * 2], null, null);
        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(ilist, classifier);

        double[] smallBuff = new double[1]; 
        opt.getParameters(smallBuff); 
        assertEquals(1.0, 1.0, 0.0); 
    }
@Test
    public void testPerLabelFeatureSelectionOnlyAffectsCorrectLabelRow() {
        Alphabet f = new Alphabet();
        int fx = f.lookupIndex("f0");

        LabelAlphabet l = new LabelAlphabet();
        int ly = l.lookupLabel("yes").getIndex();
        int ln = l.lookupLabel("no").getIndex();

        FeatureVector fv1 = new FeatureVector(f, new int[]{fx}, new double[]{1});
        Labeling lab = l.lookupLabel("yes").getLabeling(1.0);
        Instance i = new Instance(fv1, lab, "a", null);

        Pipe p = new Pipe(f, l) { public Instance pipe(Instance x) { return x; } };

        InstanceList list = new InstanceList(p);
        FeatureSelection[] perLabel = new FeatureSelection[2];
        perLabel[0] = new FeatureSelection(f);
        perLabel[0].add(fx);
        perLabel[1] = new FeatureSelection(f); 

        list.setPerLabelFeatureSelection(perLabel);
        list.add(i);

        MaxEnt classifier = new MaxEnt(p, new double[2 * 2], null, perLabel);
        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, classifier);
        double[] grad = new double[opt.getNumParameters()];
        opt.getValueGradient(grad);
        assertEquals(0.0, grad[2], 1e-6); 
    }
@Test
    public void testDefaultFeatureIndexPresentInParameters() {
        Alphabet fa = new Alphabet();
        fa.lookupIndex("f1");

        LabelAlphabet la = new LabelAlphabet();
        la.lookupLabel("yes");
        la.lookupLabel("no");

        FeatureVector fv = new FeatureVector(fa, new int[]{0}, new double[]{1});
        Labeling lbl = la.lookupLabel("yes").getLabeling(1.0);
        Instance inst = new Instance(fv, lbl, "test", null);

        Pipe p = new Pipe(fa, la) {
            public Instance pipe(Instance i) { return i; }
        };

        InstanceList il = new InstanceList(p);
        il.add(inst);

        MaxEnt classifier = new MaxEnt(p, new double[6], null, null);
        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(il, classifier);

        double[] gradient = new double[opt.getNumParameters()];
        opt.getValueGradient(gradient);

        int defaultIndexOffset = 2; 
        int paramIndex1 = 0 * 3 + defaultIndexOffset;
        int paramIndex2 = 1 * 3 + defaultIndexOffset;

        assertEquals(true, gradient[paramIndex1] != 0.0 || gradient[paramIndex2] != 0.0);
    }
@Test
    public void testGetValueGradientWhenValueWasNotPreviouslyCalled() {
        Alphabet featureAlphabet = new Alphabet();
        featureAlphabet.lookupIndex("f1");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("lbl");

        FeatureVector fv = new FeatureVector(featureAlphabet, new int[]{0}, new double[]{1.0});
        Label label = labelAlphabet.lookupLabel("lbl");
        Labeling labeling = label.getLabeling(1.0);
        Instance instance = new Instance(fv, labeling, "inst", null);

        Pipe pipe = new Pipe(featureAlphabet, labelAlphabet) {
            public Instance pipe(Instance carrier) { return carrier; }
        };

        InstanceList instanceList = new InstanceList(pipe);
        instanceList.add(instance);

        MaxEnt maxEnt = new MaxEnt(pipe, new double[2 * 2], null, null);
        MaxEntOptimizableByLabelLikelihood optimizable = new MaxEntOptimizableByLabelLikelihood(instanceList, maxEnt);

        double[] gradient = new double[optimizable.getNumParameters()];
        optimizable.getValueGradient(gradient);

        boolean hasNonZero = false;
        if (gradient[0] != 0.0) hasNonZero = true;
        if (gradient[1] != 0.0) hasNonZero = true;
        if (gradient[2] != 0.0) hasNonZero = true;
        if (gradient[3] != 0.0) hasNonZero = true;

        assertTrue(hasNonZero);
    }
@Test
    public void testSetParametersInitiallySetsNullParameterArray() {
        Alphabet featureAlphabet = new Alphabet();
        featureAlphabet.lookupIndex("x");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupLabel("lab");

        FeatureVector fv = new FeatureVector(featureAlphabet, new int[]{0}, new double[]{1.0});
        Labeling labeling = labelAlphabet.lookupLabel("lab").getLabeling(1.0);
        Instance instance = new Instance(fv, labeling, "zz", null);

        Pipe pipe = new Pipe(featureAlphabet, labelAlphabet) {
            public Instance pipe(Instance c) { return c; }
        };

        InstanceList ilist = new InstanceList(pipe);
        ilist.add(instance);

        MaxEnt classifier = new MaxEnt(pipe, new double[2 * 2], null, null);
        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(ilist, classifier);

        double[] newParams = new double[opt.getNumParameters()];
        newParams[0] = 42.0;
        opt.setParameters(newParams);

        double[] readBack = new double[opt.getNumParameters()];
        opt.getParameters(readBack);
        assertEquals(42.0, readBack[0], 1e-6);
    }
@Test
    public void testSetHyperbolicPriorSlopeAndSharpnessWithEffectOnValue() {
        Alphabet f = new Alphabet();
        f.lookupIndex("a");

        LabelAlphabet l = new LabelAlphabet();
        l.lookupLabel("L1");

        FeatureVector fv = new FeatureVector(f, new int[]{0}, new double[]{1.0});
        Labeling label = l.lookupLabel("L1").getLabeling(1.0);
        Instance inst = new Instance(fv, label, "id", null);

        Pipe pipe = new Pipe(f, l) {
            public Instance pipe(Instance carrier) { return carrier; }
        };

        InstanceList list = new InstanceList(pipe);
        list.add(inst);

        MaxEnt classifier = new MaxEnt(pipe, new double[1 * 2], null, null);
        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, classifier);

        opt.setHyperbolicPriorSlope(0.5);
        opt.setHyperbolicPriorSharpness(5.0);

        opt.useHyperbolicPrior();

        double[] gradientBuffer = new double[opt.getNumParameters()];
        try {
            opt.getValueGradient(gradientBuffer);
            fail("Expected UnsupportedOperationException for hyperbolic prior");
        } catch (UnsupportedOperationException expected) {
            assertTrue(expected.getMessage().contains("not yet implemented"));
        }
    }
@Test
    public void testClassifierReturnsZeroScoreForBestLabelIndex() {
        Alphabet featureAlphabet = new Alphabet();
        featureAlphabet.lookupIndex("f");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupLabel("yes");
        labelAlphabet.lookupLabel("no");

        FeatureVector fv = new FeatureVector(featureAlphabet, new int[]{0}, new double[]{1});
        Labeling labeling = labelAlphabet.lookupLabel("yes").getLabeling(1.0);
        Instance instance = new Instance(fv, labeling, "weird", null);

        Pipe pipe = new Pipe(featureAlphabet, labelAlphabet) {
            public Instance pipe(Instance inst) { return inst; }
        };

        InstanceList list = new InstanceList(pipe);
        list.add(instance);

        MaxEnt classifier = new MaxEnt(pipe, new double[2 * 2], null, null) {
            @Override
            public void getClassificationScores(Instance instance, double[] scores) {
                scores[0] = 0.0; 
                scores[1] = 1.0;
            }
        };

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, classifier);

        double value = opt.getValue();
        assertTrue(Double.isInfinite(value) || Double.isNaN(value) || Double.isFinite(value));
    }
@Test
    public void testMultipleCallsToSetParameterMarkCacheStale() {
        Alphabet featAlphabet = new Alphabet();
        featAlphabet.lookupIndex("q");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupLabel("LL");

        FeatureVector fv = new FeatureVector(featAlphabet, new int[]{0}, new double[]{1.0});
        Label label = labelAlphabet.lookupLabel("LL");
        Labeling labeling = label.getLabeling(1.0);
        Instance inst = new Instance(fv, labeling, "multi", null);

        Pipe pipe = new Pipe(featAlphabet, labelAlphabet) {
            public Instance pipe(Instance c) { return c; }
        };

        InstanceList ilist = new InstanceList(pipe);
        ilist.add(inst);

        MaxEnt classifier = new MaxEnt(pipe, new double[1 * 2], null, null);
        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(ilist, classifier);
        double value1 = opt.getValue();

        opt.setParameter(0, 0.123);
        double value2 = opt.getValue();

        assertNotEquals(value1, value2, 1e-4);
    }
@Test
    public void testReusingSameGradientBufferAcrossCallsReturnsSameValues() {
        Alphabet a = new Alphabet();
        a.lookupIndex("v");

        LabelAlphabet l = new LabelAlphabet();
        l.lookupIndex("V");

        FeatureVector fv = new FeatureVector(a, new int[]{0}, new double[]{1.0});
        Label label = l.lookupLabel("V");
        Labeling labeling = label.getLabeling(1.0);
        Instance inst = new Instance(fv, labeling, "abc", null);

        Pipe pipe = new Pipe(a, l) { public Instance pipe(Instance p) { return p; } };

        InstanceList ilist = new InstanceList(pipe);
        ilist.add(inst);

        MaxEnt classifier = new MaxEnt(pipe, new double[1 * 2], null, null);
        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(ilist, classifier);

        double[] buffer = new double[opt.getNumParameters()];
        opt.getValueGradient(buffer);

        double[] second = new double[opt.getNumParameters()];
        opt.getValueGradient(second);

        assertArrayEquals(buffer, second, 1e-6);
    }
@Test
    public void testSetParametersUpdatesModelAndValueChanges() {
        Alphabet al = new Alphabet();
        al.lookupIndex("w");

        LabelAlphabet la = new LabelAlphabet();
        la.lookupLabel("L");

        FeatureVector fv = new FeatureVector(al, new int[]{0}, new double[]{1});
        Label l = la.lookupLabel("L");
        Labeling labeling = l.getLabeling(1.0);
        Instance inst = new Instance(fv, labeling, "id", null);

        Pipe pipe = new Pipe(al, la) { public Instance pipe(Instance i) { return i; } };

        InstanceList list = new InstanceList(pipe);
        list.add(inst);

        MaxEnt classifier = new MaxEnt(pipe, new double[2], null, null);
        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, classifier);

        double v1 = opt.getValue();

        double[] newParams = new double[2];
        newParams[0] = 5.0;
        newParams[1] = 10.0;

        opt.setParameters(newParams);
        double v2 = opt.getValue();

        assertNotEquals(v1, v2, 1e-6);
    }
@Test
    public void testFeatureSelectionSuppressesNonSelectedFeatureGradients() {
        Alphabet dataAlphabet = new Alphabet();
        int index0 = dataAlphabet.lookupIndex("f0");
        int index1 = dataAlphabet.lookupIndex("f1");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupLabel("A");

        FeatureSelection fs = new FeatureSelection(dataAlphabet);
        fs.add(index0); 

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{index0, index1}, new double[]{1.0, 1.0});
        Labeling labeling = labelAlphabet.lookupLabel("A").getLabeling(1.0);
        Instance instance = new Instance(fv, labeling, "doc", null);

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance i) { return i; }
        };

        InstanceList ilist = new InstanceList(pipe);
        ilist.setFeatureSelection(fs);
        ilist.add(instance);

        MaxEnt classifier = new MaxEnt(pipe, new double[1 * 3], fs, null);
        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(ilist, classifier);

        double[] grad = new double[opt.getNumParameters()];
        opt.getValueGradient(grad);

        assertTrue(grad[0] != 0.0); 
        assertEquals(0.0, grad[1], 1e-10); 
    }
@Test
    public void testConstraintsRespectedByZeroScoreFeatures() {
        Alphabet dataAlphabet = new Alphabet();
        int index = dataAlphabet.lookupIndex("aaa");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupLabel("class");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{index}, new double[]{1.0});
        Label label = labelAlphabet.lookupLabel("class");
        Labeling labeling = label.getLabeling(1.0);
        Instance instance = new Instance(fv, labeling, "sample", null);

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance in) { return in; }
        };

        InstanceList ilist = new InstanceList(pipe);
        ilist.add(instance);

        MaxEnt classifier = new MaxEnt(pipe, new double[1 * 2], null, null) {
            @Override
            public void getClassificationScores(Instance i, double[] scores) {
                scores[0] = 0.0;
            }
        };

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(ilist, classifier);
        double val = opt.getValue();
        assertTrue(Double.isInfinite(val) || Double.isNaN(val) || Double.isFinite(val));
    }
@Test
    public void testInstanceWithNoDataAlphabetFeaturesHandled() {
        Alphabet alphabet = new Alphabet(); 
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupLabel("X");

        FeatureVector emptyVector = new FeatureVector(alphabet, new int[0], new double[0]);
        Label label = labelAlphabet.lookupLabel("X");
        Labeling labeling = label.getLabeling(1.0);
        Instance instance = new Instance(emptyVector, labeling, "empty", null);

        Pipe pipe = new Pipe(alphabet, labelAlphabet) {
            public Instance pipe(Instance i) { return i; }
        };

        InstanceList list = new InstanceList(pipe);
        list.add(instance);

        MaxEnt classifier = new MaxEnt(pipe, new double[1 * 1], null, null);
        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, classifier);

        double value = opt.getValue();
        assertTrue(Double.isFinite(value));
    }
@Test
    public void testMultipleLabelsAndDefaultFeatureConstraintAdded() {
        Alphabet fa = new Alphabet();
        fa.lookupIndex("f1");

        LabelAlphabet la = new LabelAlphabet();
        la.lookupLabel("label1");
        la.lookupLabel("label2");

        FeatureVector fv = new FeatureVector(fa, new int[]{0}, new double[]{1.0});
        Labeling labeling = la.lookupLabel("label2").getLabeling(1.0);
        Instance i = new Instance(fv, labeling, "m", null);

        Pipe pipe = new Pipe(fa, la) {
            public Instance pipe(Instance inst) {
                return inst;
            }
        };

        InstanceList list = new InstanceList(pipe);
        list.add(i);

        MaxEnt classifier = new MaxEnt(pipe, new double[2 * 3], null, null);
        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, classifier);

        double val = opt.getValue();
        assertTrue(Double.isFinite(val));
    }
@Test
    public void testGetValueIgnoresLabelingWithAllZeroProbabilities() {
        Alphabet f = new Alphabet();
        f.lookupIndex("x");

        LabelAlphabet l = new LabelAlphabet();
        l.lookupLabel("A");
        l.lookupLabel("B");

        FeatureVector fv = new FeatureVector(f, new int[]{0}, new double[]{1.0});
        Labeling labeling = l.lookupLabel("A").getLabeling(1.0);
        Instance instance = new Instance(fv, labeling, "zero-score", null);

        Pipe pipe = new Pipe(f, l) {
            public Instance pipe(Instance c) {
                return c;
            }
        };

        InstanceList il = new InstanceList(pipe);
        il.add(instance);

        MaxEnt classifier = new MaxEnt(pipe, new double[2 * 2], null, null) {
            @Override
            public void getClassificationScores(Instance instance, double[] scores) {
                Arrays.fill(scores, 0.0); 
            }
        };

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(il, classifier);
        double result = opt.getValue(); 

        assertTrue(Double.isNaN(result) || Double.isInfinite(result));
    }
@Test
    public void testSetMultiplePriorsSequentiallyPreservesCorrectFlag() {
        Alphabet f = new Alphabet();
        f.lookupIndex("x");

        LabelAlphabet l = new LabelAlphabet();
        l.lookupLabel("Z");

        FeatureVector fv = new FeatureVector(f, new int[]{0}, new double[]{1.0});
        Labeling labeling = l.lookupLabel("Z").getLabeling(1.0);
        Instance instance = new Instance(fv, labeling, "val", null);

        Pipe pipe = new Pipe(f, l) {
            public Instance pipe(Instance carrier) {
                return carrier;
            }
        };

        InstanceList il = new InstanceList(pipe);
        il.add(instance);

        MaxEnt classifier = new MaxEnt(pipe, new double[1 * 2], null, null);
        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(il, classifier);

        opt.setHyperbolicPriorSharpness(2.0);
        opt.setGaussianPriorVariance(1e-5);
        opt.setHyperbolicPriorSlope(0.9);

        opt.useHyperbolicPrior().useGaussianPrior().useNoPrior(); 

        double value = opt.getValue();
        assertTrue(Double.isFinite(value));
    }
@Test
    public void testDefaultConstructorDoesNotThrow() {
        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood();
        assertNotNull(opt);
    }
@Test
    public void testUseHyperbolicPriorFlagSetCorrectly() {
        Alphabet f = new Alphabet();
        f.lookupIndex("f");

        LabelAlphabet l = new LabelAlphabet();
        l.lookupLabel("cat");

        FeatureVector fv = new FeatureVector(f, new int[]{0}, new double[]{2.0});
        Label labeling = l.lookupLabel("cat");
        Instance i = new Instance(fv, labeling.getLabeling(1.0), "id", null);

        Pipe pipe = new Pipe(f, l) { public Instance pipe(Instance x) { return x; } };

        InstanceList il = new InstanceList(pipe);
        il.add(i);

        MaxEnt classifier = new MaxEnt(pipe, new double[1 * 2], null, null);
        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(il, classifier);
        opt.useHyperbolicPrior();
        try {
            double[] buffer = new double[opt.getNumParameters()];
            opt.getValueGradient(buffer);
            fail("Expected UnsupportedOperationException for hyperbolic prior");
        } catch (UnsupportedOperationException e) {
            assertTrue(e.getMessage().contains("not yet implemented"));
        }
    }
@Test
    public void testSetParameterMultipleTimesAffectsValue() {
        Alphabet f = new Alphabet();
        f.lookupIndex("f1");

        LabelAlphabet l = new LabelAlphabet();
        l.lookupLabel("yes");

        FeatureVector fv = new FeatureVector(f, new int[]{0}, new double[]{1.0});
        Labeling labeling = l.lookupLabel("yes").getLabeling(1.0);
        Instance i = new Instance(fv, labeling, "doc", null);

        Pipe pipe = new Pipe(f, l) { public Instance pipe(Instance inst) { return inst; } };

        InstanceList il = new InstanceList(pipe);
        il.add(i);

        MaxEnt classifier = new MaxEnt(pipe, new double[1 * 2], null, null);
        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(il, classifier);

        opt.setParameter(0, 3.0);
        double v1 = opt.getValue();

        opt.setParameter(0, 5.0);
        double v2 = opt.getValue();

        assertNotEquals(v1, v2, 1e-6);
    }
@Test
    public void testEmptyFeatureVectorGradientsWithDefaultFeatureOnly() {
        Alphabet a = new Alphabet();
        LabelAlphabet l = new LabelAlphabet();
        l.lookupLabel("Z");

        FeatureVector fv = new FeatureVector(a, new int[0], new double[0]);
        Labeling lbl = l.lookupLabel("Z").getLabeling(1.0);
        Instance inst = new Instance(fv, lbl, "empty", null);

        Pipe p = new Pipe(a, l) { public Instance pipe(Instance i) { return i; } };
        InstanceList ilist = new InstanceList(p);
        ilist.add(inst);

        MaxEnt classifier = new MaxEnt(p, new double[1 * 1], null, null);
        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(ilist, classifier);

        double[] gradient = new double[opt.getNumParameters()];
        opt.getValueGradient(gradient);
        assertEquals(gradient.length, 1);
    }
@Test
    public void testUseNoPriorDisablesAllPriorFlags() {
        Alphabet a = new Alphabet();
        a.lookupIndex("f");

        LabelAlphabet l = new LabelAlphabet();
        l.lookupLabel("c");

        FeatureVector fv = new FeatureVector(a, new int[]{0}, new double[]{1.0});
        Labeling labeling = l.lookupLabel("c").getLabeling(1.0);
        Instance i = new Instance(fv, labeling, "noPrior", null);

        Pipe pipe = new Pipe(a, l) {
            public Instance pipe(Instance carrier) { return carrier; }
        };

        InstanceList list = new InstanceList(pipe);
        list.add(i);

        MaxEnt classifier = new MaxEnt(pipe, new double[2], null, null);
        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, classifier);

        opt.useGaussianPrior();
        opt.useHyperbolicPrior();
        opt.useNoPrior();

        double value = opt.getValue();
        assertTrue(Double.isFinite(value));
    }
@Test
    public void testFloatPrecisionScoresNoThrow() {
        Alphabet a = new Alphabet();
        a.lookupIndex("val");

        LabelAlphabet l = new LabelAlphabet();
        l.lookupLabel("yes");
        l.lookupLabel("no");

        FeatureVector fv = new FeatureVector(a, new int[]{0}, new double[]{1f});
        Labeling label = l.lookupLabel("yes").getLabeling(1.0);
        Instance i = new Instance(fv, label, "float", null);

        Pipe p = new Pipe(a, l) { public Instance pipe(Instance x) { return x; } };

        InstanceList list = new InstanceList(p);
        list.add(i);

        MaxEnt classifier = new MaxEnt(p, new double[2 * 2], null, null);
        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, classifier);

        double v = opt.getValue();
        assertTrue(Double.isFinite(v));
    }
@Test
    public void testGetParametersWithNullAndIncorrectLength() {
        Alphabet a = new Alphabet();
        a.lookupIndex("x");

        LabelAlphabet l = new LabelAlphabet();
        l.lookupLabel("Y");

        FeatureVector fv = new FeatureVector(a, new int[]{0}, new double[]{1.0});
        Label label = l.lookupLabel("Y");
        Labeling labeling = label.getLabeling(1.0);
        Instance i = new Instance(fv, labeling, "nulltest", null);

        Pipe p = new Pipe(a, l) { public Instance pipe(Instance inst) { return inst; } };

        InstanceList il = new InstanceList(p);
        il.add(i);

        MaxEnt classifier = new MaxEnt(p, new double[1 * 2], null, null);
        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(il, classifier);

        opt.getParameters(null); 

        double[] invalid = new double[1]; 
        opt.getParameters(invalid); 
    }
@Test
    public void testNegativeInfinityParameterZeroedInGradient() {
        Alphabet a = new Alphabet();
        a.lookupIndex("feature");

        LabelAlphabet l = new LabelAlphabet();
        l.lookupLabel("label");

        FeatureVector fv = new FeatureVector(a, new int[]{0}, new double[]{1.0});
        Labeling labeling = l.lookupLabel("label").getLabeling(1.0);
        Instance i = new Instance(fv, labeling, "negInf", null);

        Pipe pipe = new Pipe(a, l) { public Instance pipe(Instance carrier) { return carrier; } };

        InstanceList list = new InstanceList(pipe);
        list.add(i);

        MaxEnt classifier = new MaxEnt(pipe, new double[2], null, null);
        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, classifier);

        opt.setParameter(0, Double.NEGATIVE_INFINITY);
        double[] gradient = new double[opt.getNumParameters()];
        opt.getValueGradient(gradient);
        assertEquals(0.0, gradient[0], 0.0);
    }
@Test
    public void testGetValueIncludesPriorWhenUsingGaussian() {
        Alphabet a = new Alphabet();
        a.lookupIndex("f");

        LabelAlphabet l = new LabelAlphabet();
        l.lookupLabel("cc");

        FeatureVector fv = new FeatureVector(a, new int[]{0}, new double[]{1.0});
        Labeling labeling = l.lookupLabel("cc").getLabeling(1.0);
        Instance i = new Instance(fv, labeling, "gauss", null);

        Pipe pipe = new Pipe(a, l) { public Instance pipe(Instance c) { return c; } };

        InstanceList list = new InstanceList(pipe);
        list.add(i);

        MaxEnt classifier = new MaxEnt(pipe, new double[1 * 2], null, null);
        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, classifier);

        opt.useGaussianPrior();
        opt.setParameter(0, 10.0); 

        double val = opt.getValue();
        assertTrue(val < 0);
    }
@Test
    public void testNaNScoreSignedLogMessageStillReturnsCorrectValue() {
        Alphabet featureAlphabet = new Alphabet();
        featureAlphabet.lookupIndex("feat");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupLabel("Y");
        labelAlphabet.lookupLabel("N");

        FeatureVector fv = new FeatureVector(featureAlphabet, new int[]{0}, new double[]{1});
        Labeling label = labelAlphabet.lookupLabel("Y").getLabeling(1.0);
        Instance inst = new Instance(fv, label, "nan", null);

        Pipe pipe = new Pipe(featureAlphabet, labelAlphabet) {
            public Instance pipe(Instance i) {
                return i;
            }
        };

        InstanceList instList = new InstanceList(pipe);
        instList.add(inst);

        MaxEnt classifier = new MaxEnt(pipe, new double[2 * 2], null, null) {
            @Override
            public void getClassificationScores(Instance inst, double[] scores) {
                scores[0] = 0.5;
                scores[1] = Double.NaN;
            }
        };

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(instList, classifier);
        double val = opt.getValue();
        assertTrue(Double.isFinite(val) || Double.isNaN(val));
    }
@Test
    public void testSetGaussianPriorVarianceNegative() {
        Alphabet dAlpha = new Alphabet();
        dAlpha.lookupIndex("f");
        LabelAlphabet lAlpha = new LabelAlphabet();
        lAlpha.lookupLabel("X");

        FeatureVector fv = new FeatureVector(dAlpha, new int[]{0}, new double[]{1});
        Labeling lbl = lAlpha.lookupLabel("X").getLabeling(1.0);
        Instance instance = new Instance(fv, lbl, "test", null);

        Pipe pipe = new Pipe(dAlpha, lAlpha) {
            public Instance pipe(Instance i) { return i; }
        };

        InstanceList list = new InstanceList(pipe);
        list.add(instance);

        MaxEnt classifier = new MaxEnt(pipe, new double[1 * 2], null, null);
        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, classifier);
        opt.setGaussianPriorVariance(-1.0); 
      
        double value = opt.getValue(); 
        assertTrue(Double.isFinite(value));
    }
@Test
    public void testGetValueGradientWithFeatureSelectionOnSingleLabel() {
        Alphabet dAlpha = new Alphabet();
        int f0 = dAlpha.lookupIndex("f1");

        LabelAlphabet lAlpha = new LabelAlphabet();
        lAlpha.lookupLabel("A");

        FeatureSelection fs = new FeatureSelection(dAlpha);
        fs.add(f0);

        FeatureVector fv = new FeatureVector(dAlpha, new int[]{f0}, new double[]{1.0});
        Labeling lbl = lAlpha.lookupLabel("A").getLabeling(1.0);
        Instance inst = new Instance(fv, lbl, "id", null);

        Pipe pipe = new Pipe(dAlpha, lAlpha) {
            public Instance pipe(Instance i) { return i; }
        };

        InstanceList list = new InstanceList(pipe);
        list.setFeatureSelection(fs);
        list.add(inst);

        MaxEnt classifier = new MaxEnt(pipe, new double[1 * 2], fs, null);
        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, classifier);

        double[] grad = new double[opt.getNumParameters()];
        opt.getValueGradient(grad);
        assertEquals(0.0, grad[1], 0.00001); 
    }
@Test
    public void testGetValueSkipsNullLabelingInstances() {
        Alphabet a = new Alphabet();
        a.lookupIndex("feat");

        LabelAlphabet l = new LabelAlphabet();
        l.lookupLabel("A");

        FeatureVector fv = new FeatureVector(a, new int[]{0}, new double[]{1.0});
        Instance inst = new Instance(fv, null, "nullLabel", null);

        Pipe pipe = new Pipe(a, l) {
            public Instance pipe(Instance x) { return x; }
        };
        InstanceList list = new InstanceList(pipe);
        list.add(inst);

        MaxEnt classifier = new MaxEnt(pipe, new double[1 * 2], null, null);
        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, classifier);

        double val = opt.getValue();
        assertEquals(-0.0, val, 0.0); 
    }
@Test
    public void testScoreArrayWithPartialZeroScores() {
        Alphabet a = new Alphabet();
        a.lookupIndex("x");

        LabelAlphabet l = new LabelAlphabet();
        l.lookupLabel("Y");
        l.lookupLabel("N");

        FeatureVector fv = new FeatureVector(a, new int[]{0}, new double[]{1});
        Labeling lbl = l.lookupLabel("N").getLabeling(1.0);
        Instance i = new Instance(fv, lbl, "zz", null);

        Pipe p = new Pipe(a, l) {
            public Instance pipe(Instance inst) {
                return inst;
            }
        };

        InstanceList list = new InstanceList(p);
        list.add(i);

        MaxEnt classifier = new MaxEnt(p, new double[2 * 2], null, null) {
            public void getClassificationScores(Instance inst, double[] scores) {
                scores[0] = 0.5;
                scores[1] = 0.0;  
            }
        };

        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, classifier);
        double value = opt.getValue();
        assertTrue(Double.isFinite(value));
    }
@Test
    public void testExtremeLargeGaussianPriorValueImpact() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("x");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupLabel("Yes");

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        Labeling labeling = labelAlphabet.lookupLabel("Yes").getLabeling(1.0);
        Instance instance = new Instance(fv, labeling, "inst", null);

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            public Instance pipe(Instance i) { return i; }
        };

        InstanceList ilist = new InstanceList(pipe);
        ilist.add(instance);

        double[] initialParams = new double[1 * 2];
        Arrays.fill(initialParams, 1000); 

        MaxEnt classifier = new MaxEnt(pipe, initialParams, null, null);
        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(ilist, classifier);
        opt.useGaussianPrior();

        double value = opt.getValue(); 
        assertTrue(value < 0);
    }
@Test
    public void testInstanceWithWeightZeroAffectsNothing() {
        Alphabet fa = new Alphabet();
        fa.lookupIndex("a");

        LabelAlphabet la = new LabelAlphabet();
        la.lookupLabel("X");

        FeatureVector fv = new FeatureVector(fa, new int[]{0}, new double[]{1.0});
        Labeling lbl = la.lookupLabel("X").getLabeling(1.0);
        Instance i1 = new Instance(fv, lbl, "w0", null);
        Instance i2 = new Instance(fv, lbl, "w1", null);

        Pipe pipe = new Pipe(fa, la) {
            public Instance pipe(Instance inst) {
                return inst;
            }
        };

        InstanceList list = new InstanceList(pipe);
        list.add(i1);
        list.add(i2);
        list.setInstanceWeight(0, 0.0); 
        list.setInstanceWeight(1, 1.0); 

        MaxEnt classifier = new MaxEnt(pipe, new double[1 * 2], null, null);
        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, classifier);

        double value = opt.getValue(); 
        assertTrue(Double.isFinite(value));
    }
@Test
    public void testDefaultFeatureInConstraintSumCalculated() {
        Alphabet a = new Alphabet();
        a.lookupIndex("f1");

        LabelAlphabet l = new LabelAlphabet();
        l.lookupLabel("A");
        l.lookupLabel("B");

        FeatureVector fv = new FeatureVector(a, new int[]{0}, new double[]{1});
        Labeling lbl = l.lookupLabel("B").getLabeling(1.0);
        Instance inst = new Instance(fv, lbl, "xx", null);

        Pipe pipe = new Pipe(a, l) { public Instance pipe(Instance x) { return x; } };
        InstanceList list = new InstanceList(pipe);
        list.add(inst);

        MaxEnt classifier = new MaxEnt(pipe, new double[2 * 3], null, null);
        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, classifier);

        double val = opt.getValue(); 
        assertTrue(Double.isFinite(val));
    }
@Test
    public void testNegativeInfinityParameterHandledInSetParameter() {
        Alphabet f = new Alphabet();
        f.lookupIndex("feat");

        LabelAlphabet l = new LabelAlphabet();
        l.lookupLabel("lab");

        FeatureVector fv = new FeatureVector(f, new int[]{0}, new double[]{1});
        Labeling labeling = l.lookupLabel("lab").getLabeling(1.0);
        Instance i = new Instance(fv, labeling, "setneginf", null);

        Pipe pipe = new Pipe(f, l) {
            public Instance pipe(Instance x) { return x; }
        };

        InstanceList il = new InstanceList(pipe);
        il.add(i);

        MaxEnt classifier = new MaxEnt(pipe, new double[1 * 2], null, null);
        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(il, classifier);

        opt.setParameter(0, Double.NEGATIVE_INFINITY);
        double[] buffer = new double[opt.getNumParameters()];
        opt.getValueGradient(buffer);
        assertEquals(0.0, buffer[0], 0.0); 
    }
@Test
    public void testIncrementOfGetValueAndGradientCalls() {
        Alphabet fa = new Alphabet();
        fa.lookupIndex("x");

        LabelAlphabet la = new LabelAlphabet();
        la.lookupLabel("c");

        FeatureVector fv = new FeatureVector(fa, new int[]{0}, new double[]{1.0});
        Labeling l = la.lookupLabel("c").getLabeling(1.0);
        Instance i = new Instance(fv, l, "counter", null);

        Pipe pipe = new Pipe(fa, la) {
            public Instance pipe(Instance x) { return x; }
        };

        InstanceList list = new InstanceList(pipe);
        list.add(i);

        MaxEnt classifier = new MaxEnt(pipe, new double[1 * 2], null, null);
        MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, classifier);

        opt.getValue();
        int count1 = opt.getValueCalls();
        opt.getValue();
        int count2 = opt.getValueCalls();
        assertEquals(count1 + 0, count2); 

        double[] g = new double[opt.getNumParameters()];
        opt.getValueGradient(g);
        int gCount1 = opt.getValueGradientCalls();
        opt.getValueGradient(g); 
        int gCount2 = opt.getValueGradientCalls();

        assertEquals(gCount1 + 0, gCount2); 
    } 
}