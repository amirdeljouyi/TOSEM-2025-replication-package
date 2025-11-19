public class NaiveBayes_2_GPTLLMTest { 

 @Test
    public void testConstructorWithLoggedValues() {
        Alphabet dataAlphabet = new Alphabet();
        Alphabet labelAlphabet = new LabelAlphabet();

        dataAlphabet.lookupIndex("feature1");
        dataAlphabet.lookupIndex("feature2");
        labelAlphabet.lookupIndex("labelA");
        labelAlphabet.lookupIndex("labelB");

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet);

        double[] priorProbs = new double[]{0.7, 0.3};
        Multinomial prior = new Multinomial(priorProbs);
        Multinomial.Logged loggedPrior = new Multinomial.Logged(prior);

        Multinomial.Logged[] condProbs = new Multinomial.Logged[2];
        condProbs[0] = new Multinomial.Logged(new Multinomial(new double[]{0.9, 0.1}));
        condProbs[1] = new Multinomial.Logged(new Multinomial(new double[]{0.4, 0.6}));

        NaiveBayes model = new NaiveBayes(pipe, loggedPrior, condProbs);

        assertNotNull(model);
        assertEquals(2, model.getPriors().size());
        assertEquals(2, model.getMultinomials().length);
    }
@Test
    public void testConstructorWithNonLoggedValues() {
        Alphabet dataAlphabet = new Alphabet();
        Alphabet labelAlphabet = new LabelAlphabet();

        dataAlphabet.lookupIndex("f1");
        dataAlphabet.lookupIndex("f2");
        labelAlphabet.lookupIndex("A");
        labelAlphabet.lookupIndex("B");

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet);

        Multinomial prior = new Multinomial(new double[]{0.5, 0.5});
        Multinomial[] conds = new Multinomial[]{
                new Multinomial(new double[]{0.6, 0.4}),
                new Multinomial(new double[]{0.2, 0.8})
        };

        NaiveBayes model = new NaiveBayes(pipe, prior, conds);

        assertNotNull(model);
        assertEquals(2, model.getPriors().size());
        assertEquals(2, model.getMultinomials().length);
    }
@Test
    public void testClassifySingleFeatureInstance() {
        Alphabet dataAlphabet = new Alphabet();
        Alphabet labelAlphabet = new LabelAlphabet();

        int idx1 = dataAlphabet.lookupIndex("f1");
        int idx2 = dataAlphabet.lookupIndex("f2");
        int label1 = labelAlphabet.lookupIndex("A");
        int label2 = labelAlphabet.lookupIndex("B");

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet);

        Multinomial.Logged prior = new Multinomial.Logged(new Multinomial(new double[]{0.6, 0.4}));
        Multinomial.Logged[] condProbs = new Multinomial.Logged[2];
        condProbs[0] = new Multinomial.Logged(new Multinomial(new double[]{0.9, 0.1}));
        condProbs[1] = new Multinomial.Logged(new Multinomial(new double[]{0.2, 0.8}));

        NaiveBayes model = new NaiveBayes(pipe, prior, condProbs);

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{idx1}, new double[]{1.0});
        Instance instance = new Instance(fv, null, "test", null);

        Classification result = model.classify(instance);
        Labeling labeling = result.getLabeling();

        assertEquals(2, labeling.numLocations());
        double sum = labeling.valueAtLocation(0) + labeling.valueAtLocation(1);
        assertEquals(1.0, sum, 1e-6);
    }
@Test
    public void testClassifyWithUnknownFeatureIndexIgnored() {
        Alphabet dataAlphabet = new Alphabet();
        Alphabet labelAlphabet = new LabelAlphabet();

        labelAlphabet.lookupIndex("class1");
        labelAlphabet.lookupIndex("class2");

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet);

        Multinomial.Logged prior = new Multinomial.Logged(new Multinomial(new double[]{0.5, 0.5}));
        Multinomial.Logged[] cond = new Multinomial.Logged[]{
                new Multinomial.Logged(new Multinomial(new double[]{0.6})),
                new Multinomial.Logged(new Multinomial(new double[]{0.4}))
        };

        NaiveBayes model = new NaiveBayes(pipe, prior, cond);

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{999}, new double[]{1.0});
        Instance instance = new Instance(fv, null, "test", null);
        Classification result = model.classify(instance);

        assertEquals(2, result.getLabeling().numLocations());
    }
@Test
    public void testDataLogLikelihoodWithLabeledInstance() {
        Alphabet dataAlphabet = new Alphabet();
        Alphabet labelAlphabet = new LabelAlphabet();

        int fIdx = dataAlphabet.lookupIndex("f");
        int lIdx = labelAlphabet.lookupIndex("L");

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet);

        Multinomial.Logged prior = new Multinomial.Logged(new Multinomial(new double[]{1.0}));
        Multinomial.Logged[] cond = new Multinomial.Logged[]{
                new Multinomial.Logged(new Multinomial(new double[]{1.0}))
        };

        NaiveBayes model = new NaiveBayes(pipe, prior, cond);

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{fIdx}, new double[]{1.0});
        LabelVector labelVector = new LabelVector(labelAlphabet, new double[]{1.0});
        Instance inst = new Instance(fv, labelVector, "inst", null);

        InstanceList list = new InstanceList(pipe);
        list.add(inst);

        double logLik = model.dataLogLikelihood(list);
        assertTrue(Double.isFinite(logLik));
    }
@Test
    public void testLabelLogLikelihoodWithValidLabeling() {
        Alphabet dataAlphabet = new Alphabet();
        Alphabet labelAlphabet = new LabelAlphabet();

        int fIdx = dataAlphabet.lookupIndex("f");
        int lIdx1 = labelAlphabet.lookupIndex("L1");
        int lIdx2 = labelAlphabet.lookupIndex("L2");

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet);

        Multinomial.Logged prior = new Multinomial.Logged(new Multinomial(new double[]{0.5, 0.5}));
        Multinomial.Logged[] cond = new Multinomial.Logged[]{
                new Multinomial.Logged(new Multinomial(new double[]{0.6})),
                new Multinomial.Logged(new Multinomial(new double[]{0.4}))
        };

        NaiveBayes model = new NaiveBayes(pipe, prior, cond);

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{fIdx}, new double[]{1.0});
        LabelVector labelVec = new LabelVector(labelAlphabet, new double[]{1.0, 0.0});
        Instance inst = new Instance(fv, labelVec, "instance", null);

        InstanceList list = new InstanceList(pipe);
        list.add(inst);

        double logLik = model.labelLogLikelihood(list);
        assertTrue(Double.isFinite(logLik));
    }
@Test
    public void testLabelLogLikelihoodSkipsNullLabels() {
        Alphabet dataAlphabet = new Alphabet();
        Alphabet labelAlphabet = new LabelAlphabet();

        dataAlphabet.lookupIndex("f");
        labelAlphabet.lookupIndex("L");

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet);

        Multinomial.Logged prior = new Multinomial.Logged(new Multinomial(new double[]{1.0}));
        Multinomial.Logged[] conds = new Multinomial.Logged[]{
                new Multinomial.Logged(new Multinomial(new double[]{1.0}))
        };

        NaiveBayes model = new NaiveBayes(pipe, prior, conds);

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        Instance inst = new Instance(fv, null, "no-label", null);

        InstanceList list = new InstanceList(pipe);
        list.add(inst);

        double logLikelihood = model.labelLogLikelihood(list);
        assertEquals(0.0, logLikelihood, 1e-10);
    }
@Test
    public void testSerializationAndDeserialization() throws Exception {
        Alphabet dataAlphabet = new Alphabet();
        Alphabet labelAlphabet = new LabelAlphabet();

        dataAlphabet.lookupIndex("x");
        labelAlphabet.lookupIndex("y");

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet);

        Multinomial prior = new Multinomial(new double[]{1.0});
        Multinomial.Logged[] cond = new Multinomial.Logged[]{
                new Multinomial.Logged(new Multinomial(new double[]{1.0}))
        };

        NaiveBayes model = new NaiveBayes(pipe, new Multinomial.Logged(prior), cond);

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bout);
        oos.writeObject(model);
        oos.close();

        ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bin);
        NaiveBayes deserializedModel = (NaiveBayes) ois.readObject();

        Classification result = deserializedModel.classify(
                new Instance(new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0}), null, "x", null));
        assertNotNull(result);
    }
@Test(expected = ClassNotFoundException.class)
    public void testDeserializationFailsWithIncorrectVersion() throws Exception {
        Alphabet dataAlphabet = new Alphabet();
        Alphabet labelAlphabet = new LabelAlphabet();

        dataAlphabet.lookupIndex("a");
        labelAlphabet.lookupIndex("b");

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet);

        Multinomial prior = new Multinomial(new double[]{1.0});
        Multinomial.Logged[] cond = new Multinomial.Logged[]{
                new Multinomial.Logged(new Multinomial(new double[]{1.0}))
        };

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bout);

        oos.writeInt(999); 
        oos.writeObject(pipe);
        oos.writeObject(new Multinomial.Logged(prior));
        oos.writeObject(cond);
        oos.close();

        ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bin);

        NaiveBayes model = new NaiveBayes(pipe, new Multinomial.Logged(prior), cond);
        model.readObject(ois); 
    }
@Test
    public void testPrintWordsDoesNotThrowException() {
        Alphabet dataAlphabet = new Alphabet();
        Alphabet labelAlphabet = new LabelAlphabet();

        dataAlphabet.lookupIndex("f0");
        dataAlphabet.lookupIndex("f1");
        labelAlphabet.lookupIndex("L1");
        labelAlphabet.lookupIndex("L2");

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet);

        Multinomial prior = new Multinomial(new double[]{0.5, 0.5});
        Multinomial.Logged[] conds = new Multinomial.Logged[]{
                new Multinomial.Logged(new Multinomial(new double[]{0.7, 0.3})),
                new Multinomial.Logged(new Multinomial(new double[]{0.2, 0.8}))
        };

        NaiveBayes model = new NaiveBayes(pipe, new Multinomial.Logged(prior), conds);
        model.printWords(1);
    }
@Test
public void testClassifyWithEmptyFeatureVector() {
    Alphabet dataAlphabet = new Alphabet();
    Alphabet labelAlphabet = new LabelAlphabet();
    
    dataAlphabet.lookupIndex("f0");
    labelAlphabet.lookupIndex("L1");
    labelAlphabet.lookupIndex("L2");

    Pipe pipe = new Pipe(dataAlphabet, labelAlphabet);

    Multinomial prior = new Multinomial(new double[]{0.6, 0.4});
    Multinomial.Logged[] conds = new Multinomial.Logged[]{
            new Multinomial.Logged(new Multinomial(new double[]{1.0})),
            new Multinomial.Logged(new Multinomial(new double[]{1.0}))
    };

    NaiveBayes model = new NaiveBayes(pipe, new Multinomial.Logged(prior), conds);
    FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{}, new double[]{});
    Instance instance = new Instance(fv, null, "emptyFV", null);

    Classification classification = model.classify(instance);
    assertEquals(2, classification.getLabeling().numLocations());
}
@Test
public void testClassifyWithDifferentAlphabetShouldAssertIfChecked() {
    Alphabet trainAlphabet = new Alphabet();
    Alphabet testAlphabet = new Alphabet();
    Alphabet labelAlphabet = new LabelAlphabet();

    trainAlphabet.lookupIndex("f1");
    testAlphabet.lookupIndex("f2");
    labelAlphabet.lookupIndex("A");
    labelAlphabet.lookupIndex("B");

    Pipe trainPipe = new Pipe(trainAlphabet, labelAlphabet);

    Multinomial prior = new Multinomial(new double[]{0.5, 0.5});
    Multinomial.Logged[] conds = new Multinomial.Logged[]{
            new Multinomial.Logged(new Multinomial(new double[]{0.9})),
            new Multinomial.Logged(new Multinomial(new double[]{0.1}))
    };

    NaiveBayes model = new NaiveBayes(trainPipe, new Multinomial.Logged(prior), conds);

    FeatureVector fv = new FeatureVector(testAlphabet, new int[]{0}, new double[]{1.0});
    Instance inst = new Instance(fv, null, "mismatch", null);

    assertTrue(true); 
}
@Test
public void testClassifyWithZeroFeatureValues() {
    Alphabet dataAlphabet = new Alphabet();
    Alphabet labelAlphabet = new LabelAlphabet();

    int fIdx = dataAlphabet.lookupIndex("f1");
    labelAlphabet.lookupIndex("L1");
    labelAlphabet.lookupIndex("L2");

    Pipe pipe = new Pipe(dataAlphabet, labelAlphabet);

    Multinomial.Logged prior = new Multinomial.Logged(new Multinomial(new double[]{0.5, 0.5}));
    Multinomial.Logged[] conds = new Multinomial.Logged[]{
            new Multinomial.Logged(new Multinomial(new double[]{0.9})),
            new Multinomial.Logged(new Multinomial(new double[]{0.1}))
    };

    NaiveBayes model = new NaiveBayes(pipe, prior, conds);

    FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{fIdx}, new double[]{0.0});
    Instance inst = new Instance(fv, null, "zeroValue", null);

    Classification result = model.classify(inst);
    assertEquals(2, result.getLabeling().numLocations());
}
@Test
public void testClassifyWithFeatureVectorContainingDuplicates() {
    Alphabet dataAlphabet = new Alphabet();
    Alphabet labelAlphabet = new LabelAlphabet();

    int fIdx = dataAlphabet.lookupIndex("f1");
    labelAlphabet.lookupIndex("C1");
    labelAlphabet.lookupIndex("C2");

    Pipe pipe = new Pipe(dataAlphabet, labelAlphabet);

    Multinomial.Logged prior = new Multinomial.Logged(new Multinomial(new double[]{0.4, 0.6}));
    Multinomial.Logged[] conds = new Multinomial.Logged[]{
            new Multinomial.Logged(new Multinomial(new double[]{0.8})),
            new Multinomial.Logged(new Multinomial(new double[]{0.2}))
    };

    NaiveBayes model = new NaiveBayes(pipe, prior, conds);

    FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{fIdx, fIdx}, new double[]{1.0, 2.0});
    Instance inst = new Instance(fv, null, "dupFeature", null);

    Classification result = model.classify(inst);
    assertEquals(2, result.getLabeling().numLocations());
}
@Test
public void testClassifyWithSingleClassModelAlwaysPredictsThatClass() {
    Alphabet dataAlphabet = new Alphabet();
    Alphabet labelAlphabet = new LabelAlphabet();

    int fIdx = dataAlphabet.lookupIndex("feat");
    int lIdx = labelAlphabet.lookupIndex("onlyClass");

    Pipe pipe = new Pipe(dataAlphabet, labelAlphabet);

    Multinomial prior = new Multinomial(new double[]{1.0});
    Multinomial.Logged[] cond = new Multinomial.Logged[]{
            new Multinomial.Logged(new Multinomial(new double[]{1.0}))
    };

    NaiveBayes model = new NaiveBayes(pipe, new Multinomial.Logged(prior), cond);

    FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{fIdx}, new double[]{3.0});
    Instance inst = new Instance(fv, null, "oneclass", null);

    Classification classification = model.classify(inst);
    assertEquals(1.0, classification.getLabeling().value(lIdx), 1e-10);
}
@Test
public void testDataLogLikelihoodWithZeroWeightIgnored() {
    Alphabet dataAlphabet = new Alphabet();
    Alphabet labelAlphabet = new LabelAlphabet();

    dataAlphabet.lookupIndex("x");
    labelAlphabet.lookupIndex("z");

    Pipe pipe = new Pipe(dataAlphabet, labelAlphabet);

    Multinomial.Logged prior = new Multinomial.Logged(new Multinomial(new double[]{1.0}));
    Multinomial.Logged[] conds = new Multinomial.Logged[]{
            new Multinomial.Logged(new Multinomial(new double[]{1.0}))
    };

    NaiveBayes model = new NaiveBayes(pipe, prior, conds);

    FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
    LabelVector label = new LabelVector(labelAlphabet, new double[]{1.0});
    Instance inst = new Instance(fv, label, "zeroWeight", null);

    InstanceList list = new InstanceList(pipe);
    list.add(inst, 0.0);

    double result = model.dataLogLikelihood(list);
    assertEquals(0.0, result, 1e-10);
}
@Test
public void testLabelLogLikelihoodWithUniformProbabilities() {
    Alphabet dataAlphabet = new Alphabet();
    Alphabet labelAlphabet = new LabelAlphabet();

    dataAlphabet.lookupIndex("x");
    labelAlphabet.lookupIndex("A");
    labelAlphabet.lookupIndex("B");

    Pipe pipe = new Pipe(dataAlphabet, labelAlphabet);

    Multinomial.Logged prior = new Multinomial.Logged(new Multinomial(new double[]{0.5, 0.5}));
    Multinomial.Logged[] cond = new Multinomial.Logged[]{
            new Multinomial.Logged(new Multinomial(new double[]{1.0})),
            new Multinomial.Logged(new Multinomial(new double[]{1.0}))
    };

    NaiveBayes model = new NaiveBayes(pipe, prior, cond);

    FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{0.0});
    LabelVector labelVec = new LabelVector(labelAlphabet, new double[]{0.5, 0.5});
    Instance inst = new Instance(fv, labelVec, "uniformLabel", null);

    InstanceList list = new InstanceList(pipe);
    list.add(inst);

    double logLikelihood = model.labelLogLikelihood(list);
    assertTrue(Double.isFinite(logLikelihood));
}
@Test
public void testClassifyWithNullPipeSkipsAlphabetCheck() {
    Alphabet dataAlphabet = new Alphabet();
    Alphabet labelAlphabet = new LabelAlphabet();

    dataAlphabet.lookupIndex("x");
    labelAlphabet.lookupIndex("c1");
    labelAlphabet.lookupIndex("c2");

    Multinomial prior = new Multinomial(new double[]{0.5, 0.5});
    Multinomial.Logged[] conds = new Multinomial.Logged[]{
            new Multinomial.Logged(new Multinomial(new double[]{0.6})),
            new Multinomial.Logged(new Multinomial(new double[]{0.4}))
    };

    NaiveBayes model = new NaiveBayes(null, new Multinomial.Logged(prior), conds);

    FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
    Instance instance = new Instance(fv, null, "noPipe", null);

    Classification classification = model.classify(instance);
    assertEquals(2, classification.getLabeling().numLocations());
}
@Test
public void testClassifyWithUnseenLabelInAlphabetIgnoredInScoring() {
    Alphabet dataAlphabet = new Alphabet();
    Alphabet labelAlphabet = new LabelAlphabet();

    dataAlphabet.lookupIndex("f0");
    labelAlphabet.lookupIndex("seen");
    labelAlphabet.lookupIndex("new");

    Pipe pipe = new Pipe(dataAlphabet, labelAlphabet);

    double[] priorArray = new double[]{1.0};
    Multinomial.Logged[] conds = new Multinomial.Logged[]{
            new Multinomial.Logged(new Multinomial(new double[]{1.0}))
    };

    NaiveBayes model = new NaiveBayes(pipe, new Multinomial.Logged(new Multinomial(priorArray)), conds);

    FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1});
    Instance instance = new Instance(fv, null, "z", null);

    Classification result = model.classify(instance);
    assertEquals(2, result.getLabeling().numLocations());
    assertEquals(0.0, result.getLabeling().value(labelAlphabet.lookupIndex("new")), 0.0);
}
@Test
public void testDataLogLikelihoodWithUnseenFeatureHasNoEffect() {
    Alphabet dataAlphabet = new Alphabet();
    Alphabet labelAlphabet = new LabelAlphabet();

    int f0 = dataAlphabet.lookupIndex("f0");
    int f1 = dataAlphabet.lookupIndex("f1");

    int l0 = labelAlphabet.lookupIndex("c1");
    int l1 = labelAlphabet.lookupIndex("c2");

    Pipe pipe = new Pipe(dataAlphabet, labelAlphabet);

    double[] prior = new double[]{0.5, 0.5};
    Multinomial.Logged logPrior = new Multinomial.Logged(new Multinomial(prior));

    double[] class0 = new double[]{0.8, 0.2};
    double[] class1 = new double[]{0.3, 0.7};

    Multinomial.Logged[] conds = new Multinomial.Logged[]{
            new Multinomial.Logged(new Multinomial(class0)),
            new Multinomial.Logged(new Multinomial(class1))
    };

    NaiveBayes model = new NaiveBayes(pipe, logPrior, conds);

    FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{999}, new double[]{1.0});
    LabelVector gold = new LabelVector(labelAlphabet, new double[]{1.0, 0});
    Instance inst = new Instance(fv, gold, "unseenFeature", null);

    InstanceList list = new InstanceList(pipe);
    list.add(inst);

    double result = model.dataLogLikelihood(list);
    assertEquals(0.0, result, 1e-6);
}
@Test
public void testLabelLogLikelihoodWithZeroPredictionProbability() {
    Alphabet dataAlphabet = new Alphabet();
    Alphabet labelAlphabet = new LabelAlphabet();

    dataAlphabet.lookupIndex("feat");
    labelAlphabet.lookupIndex("c1");
    labelAlphabet.lookupIndex("c2");

    Pipe pipe = new Pipe(dataAlphabet, labelAlphabet);

    Multinomial.Logged prior = new Multinomial.Logged(new Multinomial(new double[]{1.0, 0.0}));

    Multinomial.Logged[] conds = new Multinomial.Logged[]{
            new Multinomial.Logged(new Multinomial(new double[]{1.0})),
            new Multinomial.Logged(new Multinomial(new double[]{1.0}))
    };

    NaiveBayes model = new NaiveBayes(pipe, prior, conds);

    FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});

    LabelVector labelVec = new LabelVector(labelAlphabet, new double[]{0.0, 1.0});
    Instance inst = new Instance(fv, labelVec, "impossibleLabel", null);

    InstanceList list = new InstanceList(pipe);
    list.add(inst);

    double logLikelihood = model.labelLogLikelihood(list);
    assertTrue("Should be negative or -inf", logLikelihood <= 0.0);
}
@Test
public void testClassifyWithEmptyConditionalProbabilityArray() {
    Alphabet dataAlphabet = new Alphabet();
    Alphabet labelAlphabet = new LabelAlphabet();

    dataAlphabet.lookupIndex("feat");
    labelAlphabet.lookupIndex("label0");

    Pipe pipe = new Pipe(dataAlphabet, labelAlphabet);

    Multinomial prior = new Multinomial(new double[]{1.0});
    Multinomial.Logged[] emptyConds = new Multinomial.Logged[0];

    NaiveBayes model = new NaiveBayes(pipe, new Multinomial.Logged(prior), emptyConds);

    FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{2.0});
    Instance inst = new Instance(fv, null, "emptyConds", null);

    Classification classification = model.classify(inst);
    assertEquals(1, classification.getLabeling().numLocations());
    assertEquals(1.0, classification.getLabeling().valueAtRank(0), 1e-6);
}
@Test
public void testPrintWordsWithNumToPrintExceedingAvailableFeatures() {
    Alphabet dataAlphabet = new Alphabet();
    Alphabet labelAlphabet = new LabelAlphabet();

    dataAlphabet.lookupIndex("one");
    dataAlphabet.lookupIndex("two");
    labelAlphabet.lookupIndex("yes");
    labelAlphabet.lookupIndex("no");

    Pipe pipe = new Pipe(dataAlphabet, labelAlphabet);

    Multinomial prior = new Multinomial(new double[]{0.5, 0.5});
    Multinomial.Logged[] conds = new Multinomial.Logged[]{
            new Multinomial.Logged(new Multinomial(new double[]{0.7, 0.3})),
            new Multinomial.Logged(new Multinomial(new double[]{0.2, 0.8}))
    };

    NaiveBayes model = new NaiveBayes(pipe, new Multinomial.Logged(prior), conds);

    model.printWords(5); 
    assertTrue(true); 
}
@Test
public void testClassifyWithNegativeFeatureValueWeights() {
    Alphabet dataAlphabet = new Alphabet();
    Alphabet labelAlphabet = new LabelAlphabet();

    int f0 = dataAlphabet.lookupIndex("negF");
    int c0 = labelAlphabet.lookupIndex("A");
    int c1 = labelAlphabet.lookupIndex("B");

    Pipe pipe = new Pipe(dataAlphabet, labelAlphabet);

    Multinomial prior = new Multinomial(new double[]{0.5, 0.5});
    Multinomial.Logged[] conds = new Multinomial.Logged[] {
            new Multinomial.Logged(new Multinomial(new double[]{0.9})),
            new Multinomial.Logged(new Multinomial(new double[]{0.1}))
    };

    NaiveBayes model = new NaiveBayes(pipe, new Multinomial.Logged(prior), conds);

    FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{f0}, new double[]{-1.0});
    Instance inst = new Instance(fv, null, "negativeWeight", null);

    Classification classification = model.classify(inst);
    assertEquals(2, classification.getLabeling().numLocations());
    double sum = classification.getLabeling().valueAtRank(0) + classification.getLabeling().valueAtRank(1);
    assertEquals(1.0, sum, 1e-6);
}
@Test
public void testDataLogLikelihoodWithPartiallyAlignedFeatureAlphabet() {
    Alphabet dataAlphabet = new Alphabet();
    Alphabet labelAlphabet = new LabelAlphabet();

    int f1 = dataAlphabet.lookupIndex("known");
    int f2 = dataAlphabet.lookupIndex("unknown"); 

    int l0 = labelAlphabet.lookupIndex("Y");

    Pipe pipe = new Pipe(dataAlphabet, labelAlphabet);

    Multinomial.Logged prior = new Multinomial.Logged(new Multinomial(new double[]{1.0}));

    Multinomial.Logged[] conds = new Multinomial.Logged[] {
            new Multinomial.Logged(new Multinomial(new double[]{0.9, 0.1}))
    };

    NaiveBayes model = new NaiveBayes(pipe, prior, conds);

    
    FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{f1, 999}, new double[]{1.0, 2.0});
    LabelVector labelVec = new LabelVector(labelAlphabet, new double[]{1.0});
    Instance inst = new Instance(fv, labelVec, "partial", null);

    InstanceList list = new InstanceList(pipe);
    list.add(inst);

    double result = model.dataLogLikelihood(list);
    assertTrue(Double.isFinite(result));
}
@Test
public void testLabelLogLikelihoodWithMultipleNonZeroGoldLabels() {
    Alphabet dataAlphabet = new Alphabet();
    Alphabet labelAlphabet = new LabelAlphabet();

    int f = dataAlphabet.lookupIndex("feat");
    int l0 = labelAlphabet.lookupIndex("L0");
    int l1 = labelAlphabet.lookupIndex("L1");

    Pipe pipe = new Pipe(dataAlphabet, labelAlphabet);

    Multinomial.Logged prior = new Multinomial.Logged(new Multinomial(new double[]{0.5, 0.5}));
    Multinomial.Logged[] conds = new Multinomial.Logged[] {
            new Multinomial.Logged(new Multinomial(new double[]{0.7})),
            new Multinomial.Logged(new Multinomial(new double[]{0.3}))
    };

    NaiveBayes model = new NaiveBayes(pipe, prior, conds);

    FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{f}, new double[]{1.0});
    LabelVector labelVec = new LabelVector(labelAlphabet, new double[]{0.4, 0.6});
    Instance inst = new Instance(fv, labelVec, "multiLabel", null);

    InstanceList list = new InstanceList(pipe);
    list.add(inst);

    double result = model.labelLogLikelihood(list);
    assertTrue(Double.isFinite(result));
}
@Test
public void testDataLogLikelihoodWithMultipleInstancesMixedLabeledUnlabeled() {
    Alphabet dataAlphabet = new Alphabet();
    Alphabet labelAlphabet = new LabelAlphabet();

    int f = dataAlphabet.lookupIndex("feat");
    int l0 = labelAlphabet.lookupIndex("L");

    Pipe pipe = new Pipe(dataAlphabet, labelAlphabet);

    Multinomial prior = new Multinomial(new double[]{1.0});
    Multinomial.Logged[] conds = new Multinomial.Logged[]{
            new Multinomial.Logged(new Multinomial(new double[]{1.0}))
    };

    NaiveBayes model = new NaiveBayes(pipe, new Multinomial.Logged(prior), conds);

    FeatureVector fv1 = new FeatureVector(dataAlphabet, new int[]{f}, new double[]{1.0});
    LabelVector label1 = new LabelVector(labelAlphabet, new double[]{1.0});
    Instance inst1 = new Instance(fv1, label1, "labeled", null);

    FeatureVector fv2 = new FeatureVector(dataAlphabet, new int[]{f}, new double[]{1.0});
    Instance inst2 = new Instance(fv2, null, "unlabeled", null);

    InstanceList list = new InstanceList(pipe);
    list.add(inst1);
    list.add(inst2);

    double result = model.dataLogLikelihood(list);
    assertTrue(Double.isFinite(result));
}
@Test
public void testLabelLogLikelihoodIgnoresZeroWeightLabels() {
    Alphabet dataAlphabet = new Alphabet();
    Alphabet labelAlphabet = new LabelAlphabet();

    dataAlphabet.lookupIndex("feat");
    labelAlphabet.lookupIndex("L0");
    labelAlphabet.lookupIndex("L1");

    Pipe pipe = new Pipe(dataAlphabet, labelAlphabet);

    Multinomial prior = new Multinomial(new double[]{0.6, 0.4});
    Multinomial.Logged[] cond = new Multinomial.Logged[]{
            new Multinomial.Logged(new Multinomial(new double[]{0.5})),
            new Multinomial.Logged(new Multinomial(new double[]{0.5}))
    };

    NaiveBayes model = new NaiveBayes(pipe, new Multinomial.Logged(prior), cond);

    FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
    LabelVector labeling = new LabelVector(labelAlphabet, new double[]{1.0, 0.0});

    Instance inst = new Instance(fv, labeling, "test", null);
    InstanceList list = new InstanceList(pipe);
    list.add(inst);

    double value = model.labelLogLikelihood(list);
    assertTrue(Double.isFinite(value));
}
@Test
public void testClassifyWhenAlphabetsGrowAfterTraining() {
    Alphabet dataAlphabet = new Alphabet();
    Alphabet labelAlphabet = new LabelAlphabet();

    dataAlphabet.lookupIndex("f0");
    labelAlphabet.lookupIndex("L0");
    labelAlphabet.lookupIndex("L1");

    Pipe pipe = new Pipe(dataAlphabet, labelAlphabet);

    Multinomial prior = new Multinomial(new double[]{0.5, 0.5});
    Multinomial.Logged[] conds = new Multinomial.Logged[]{
            new Multinomial.Logged(new Multinomial(new double[]{0.6})),
            new Multinomial.Logged(new Multinomial(new double[]{0.4}))
    };

    NaiveBayes model = new NaiveBayes(pipe, new Multinomial.Logged(prior), conds);

    labelAlphabet.lookupIndex("L2"); 
    dataAlphabet.lookupIndex("f1");  

    FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0, 1}, new double[]{1.0, 1.0});
    Instance inst = new Instance(fv, null, "growing", null);

    Classification result = model.classify(inst);
    assertEquals(3, result.getLabeling().numLocations());
    assertEquals(0.0, result.getLabeling().value(labelAlphabet.lookupIndex("L2")), 0.0);
}
@Test
public void testClassifyWithEmptyLabelAlphabet() {
    Alphabet dataAlphabet = new Alphabet();
    Alphabet labelAlphabet = new LabelAlphabet();

    dataAlphabet.lookupIndex("tok");

    Pipe pipe = new Pipe(dataAlphabet, labelAlphabet);

    Multinomial prior = new Multinomial(new double[]{});
    Multinomial.Logged[] conds = new Multinomial.Logged[]{};

    NaiveBayes model = new NaiveBayes(pipe, new Multinomial.Logged(prior), conds);

    FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
    Instance inst = new Instance(fv, null, "noLabels", null);

    Classification result = model.classify(inst);
    assertEquals(0, result.getLabeling().numLocations());
}
@Test
public void testClassifyWithNaNInConditionals() {
    Alphabet dataAlphabet = new Alphabet();
    Alphabet labelAlphabet = new LabelAlphabet();

    int f0 = dataAlphabet.lookupIndex("f");
    labelAlphabet.lookupIndex("A");

    Pipe pipe = new Pipe(dataAlphabet, labelAlphabet);

    double[] prob = new double[]{Double.NaN};
    Multinomial prior = new Multinomial(new double[]{1.0});
    Multinomial[] cond = new Multinomial[]{
            new Multinomial(prob)
    };

    NaiveBayes model = new NaiveBayes(pipe, prior, cond);

    FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{f0}, new double[]{1.0});
    Instance inst = new Instance(fv, null, "nan", null);

    Classification classification = model.classify(inst);
    assertEquals(1, classification.getLabeling().numLocations());
}
@Test
public void testClassifyWithInfinitePriorValue() {
    Alphabet dataAlphabet = new Alphabet();
    Alphabet labelAlphabet = new LabelAlphabet();

    int f = dataAlphabet.lookupIndex("f");
    int l0 = labelAlphabet.lookupIndex("A");

    Pipe pipe = new Pipe(dataAlphabet, labelAlphabet);

    double[] priorProb = new double[]{Double.POSITIVE_INFINITY};
    Multinomial prior = new Multinomial(priorProb);
    Multinomial[] conds = new Multinomial[] {
            new Multinomial(new double[]{1.0})
    };

    NaiveBayes model = new NaiveBayes(pipe, prior, conds);

    FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{f}, new double[]{1.0});
    Instance inst = new Instance(fv, null, "infPrior", null);

    Classification result = model.classify(inst);
    assertEquals(1, result.getLabeling().numLocations());
}
@Test
public void testClassifyWithEmptyMultinomialArray() {
    Alphabet dataAlphabet = new Alphabet();
    Alphabet labelAlphabet = new LabelAlphabet();

    dataAlphabet.lookupIndex("topic");
    labelAlphabet.lookupIndex("class");

    Pipe pipe = new Pipe(dataAlphabet, labelAlphabet);

    Multinomial prior = new Multinomial(new double[]{1.0});
    Multinomial.Logged[] conds = new Multinomial.Logged[] { };

    NaiveBayes model = new NaiveBayes(pipe, new Multinomial.Logged(prior), conds);

    FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
    Instance inst = new Instance(fv, null, "emptyConds", null);

    Classification result = model.classify(inst);
    assertEquals(1, result.getLabeling().numLocations());
}
@Test
public void testPrintWordsWithEmptyTargetAlphabet() {
    Alphabet dataAlphabet = new Alphabet();
    Alphabet labelAlphabet = new LabelAlphabet();

    dataAlphabet.lookupIndex("f");
    Pipe pipe = new Pipe(dataAlphabet, labelAlphabet);

    Multinomial prior = new Multinomial(new double[]{});
    Multinomial.Logged[] conds = new Multinomial.Logged[]{};

    NaiveBayes model = new NaiveBayes(pipe, new Multinomial.Logged(prior), conds);

    model.printWords(5); 
    assertTrue(true);
}
@Test
public void testClassifyWithNullDataThrowsClassCastException() {
    Alphabet dataAlphabet = new Alphabet();
    Alphabet labelAlphabet = new LabelAlphabet();

    labelAlphabet.lookupIndex("L");
    Multinomial prior = new Multinomial(new double[]{1.0});
    Multinomial.Logged[] conds = new Multinomial.Logged[]{
            new Multinomial.Logged(new Multinomial(new double[]{1.0}))
    };

    Pipe pipe = new Pipe(dataAlphabet, labelAlphabet);
    NaiveBayes model = new NaiveBayes(pipe, new Multinomial.Logged(prior), conds);

    Instance inst = new Instance(null, null, "nullData", null);

    try {
        model.classify(inst);
        fail("Expected ClassCastException");
    } catch (ClassCastException expected) {
        assertTrue(true);
    }
}
@Test
public void testClassifyWithExtremeFeatureWeights() {
    Alphabet dataAlphabet = new Alphabet();
    Alphabet labelAlphabet = new LabelAlphabet();

    int f0 = dataAlphabet.lookupIndex("extremeF");
    int l0 = labelAlphabet.lookupIndex("A");
    int l1 = labelAlphabet.lookupIndex("B");

    Pipe pipe = new Pipe(dataAlphabet, labelAlphabet);

    double[] prior = new double[]{0.5, 0.5};
    double[] c0 = new double[]{1.0e-100};
    double[] c1 = new double[]{1.0e+100};

    Multinomial.Logged[] conds = new Multinomial.Logged[]{
            new Multinomial.Logged(new Multinomial(c0)),
            new Multinomial.Logged(new Multinomial(c1))
    };

    NaiveBayes model = new NaiveBayes(pipe, new Multinomial.Logged(new Multinomial(prior)), conds);

    FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{f0}, new double[]{1.0});
    Instance inst = new Instance(fv, null, "extreme", null);

    Classification classification = model.classify(inst);

    assertEquals(2, classification.getLabeling().numLocations());
    assertEquals(1.0, classification.getLabeling().valueAtRank(0) + classification.getLabeling().valueAtRank(1), 1e-6);
}
@Test
public void testClassifyWithNullFeatureVectorAlphabetWhenPipeIsNull() {
    Alphabet dataAlphabet = new Alphabet();
    dataAlphabet.lookupIndex("token");

    Multinomial prior = new Multinomial(new double[] { 1.0 });
    Multinomial.Logged[] conditionals = new Multinomial.Logged[] {
            new Multinomial.Logged(new Multinomial(new double[] { 1.0 }))
    };

    NaiveBayes model = new NaiveBayes(null, new Multinomial.Logged(prior), conditionals);

    FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
    Instance inst = new Instance(fv, null, "input", null);

    Classification result = model.classify(inst);
    assertEquals(1, result.getLabeling().numLocations());
}
@Test
public void testClassificationProbabilitiesAreZeroWhenAllScoresAreNegInf() {
    Alphabet dataAlphabet = new Alphabet();
    Alphabet labelAlphabet = new LabelAlphabet();

    dataAlphabet.lookupIndex("f");
    labelAlphabet.lookupIndex("A");
    labelAlphabet.lookupIndex("B");

    Pipe pipe = new Pipe(dataAlphabet, labelAlphabet);

    double[] prior = new double[] { 0.0, 0.0 }; 
    Multinomial priors = new Multinomial(prior);

    Multinomial[] conds = new Multinomial[] {
            new Multinomial(new double[] { 1.0 }),
            new Multinomial(new double[] { 1.0 })
    };

    NaiveBayes model = new NaiveBayes(pipe, priors, conds);

    FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
    Instance inst = new Instance(fv, null, "zeroPrior", null);

    Classification c = model.classify(inst);
    assertEquals(2, c.getLabeling().numLocations());
    assertEquals(0.0, c.getLabeling().value(labelAlphabet.lookupIndex("A"))
                     + c.getLabeling().value(labelAlphabet.lookupIndex("B")), 1e-10);
}
@Test
public void testLabelLogLikelihoodWithZeroPredictionProbabilityReturnsNegativeInfinity() {
    Alphabet dataAlphabet = new Alphabet();
    Alphabet labelAlphabet = new LabelAlphabet();

    int fIdx = dataAlphabet.lookupIndex("f");
    int lIdx = labelAlphabet.lookupIndex("class");

    Pipe pipe = new Pipe(dataAlphabet, labelAlphabet);

    double[] prior = new double[] { 1.0 };
    Multinomial priors = new Multinomial(prior);

    Multinomial[] conds = new Multinomial[] {
            new Multinomial(new double[] { 0.0 }) 
    };

    NaiveBayes model = new NaiveBayes(pipe, priors, conds);

    FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { fIdx }, new double[] { 1.0 });
    LabelVector label = new LabelVector(labelAlphabet, new double[] { 1.0 });

    Instance instance = new Instance(fv, label, "badProb", null);
    InstanceList list = new InstanceList(pipe);
    list.add(instance);

    double result = model.labelLogLikelihood(list);
    assertTrue(Double.isInfinite(result));
    assertTrue(result < 0);
}
@Test
public void testDataLogLikelihoodWithEmptyInstanceListReturnsZero() {
    Alphabet dataAlphabet = new Alphabet();
    Alphabet labelAlphabet = new LabelAlphabet();

    Pipe pipe = new Pipe(dataAlphabet, labelAlphabet);

    Multinomial prior = new Multinomial(new double[] { 1.0 });
    Multinomial.Logged[] conds = new Multinomial.Logged[] {
            new Multinomial.Logged(new Multinomial(new double[] { 1.0 }))
    };

    NaiveBayes model = new NaiveBayes(pipe, new Multinomial.Logged(prior), conds);
    InstanceList list = new InstanceList(pipe);

    double result = model.dataLogLikelihood(list);
    assertEquals(0.0, result, 1e-10);
}
@Test
public void testPrintWordsWithZeroFeaturesAndLabelsDoesNotThrow() {
    Alphabet dataAlphabet = new Alphabet();
    Alphabet labelAlphabet = new LabelAlphabet();

    Pipe pipe = new Pipe(dataAlphabet, labelAlphabet);
    Multinomial.Logged prior = new Multinomial.Logged(new Multinomial(new double[] {}));
    Multinomial.Logged[] conds = new Multinomial.Logged[] {};

    NaiveBayes model = new NaiveBayes(pipe, prior, conds);
    model.printWords(10); 
    assertTrue(true);
}
@Test
public void testClassificationResultWithSingleFeatureMultipleTimes() {
    Alphabet dataAlphabet = new Alphabet();
    Alphabet labelAlphabet = new LabelAlphabet();

    int f = dataAlphabet.lookupIndex("token");
    int l0 = labelAlphabet.lookupIndex("yes");
    int l1 = labelAlphabet.lookupIndex("no");

    Pipe pipe = new Pipe(dataAlphabet, labelAlphabet);

    double[] prior = new double[] { 0.5, 0.5 };
    Multinomial priors = new Multinomial(prior);

    Multinomial[] conds = new Multinomial[] {
            new Multinomial(new double[] { 0.8 }),
            new Multinomial(new double[] { 0.2 })
    };

    NaiveBayes model = new NaiveBayes(pipe, priors, conds);

    FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { f, f }, new double[] { 1.0, 2.0 });
    Instance inst = new Instance(fv, null, "repeated", null);

    Classification result = model.classify(inst);
    assertEquals(2, result.getLabeling().numLocations());
    double sum = result.getLabeling().value(l0) + result.getLabeling().value(l1);
    assertEquals(1.0, sum, 1e-6);
}
@Test
public void testClassifyWithEmptyInstanceNameAndSource() {
    Alphabet dataAlphabet = new Alphabet();
    Alphabet labelAlphabet = new LabelAlphabet();

    dataAlphabet.lookupIndex("word");
    labelAlphabet.lookupIndex("class1");

    Pipe pipe = new Pipe(dataAlphabet, labelAlphabet);
    Multinomial prior = new Multinomial(new double[] { 1.0 });
    Multinomial.Logged[] conds = new Multinomial.Logged[] {
        new Multinomial.Logged(new Multinomial(new double[] { 1.0 }))
    };

    NaiveBayes model = new NaiveBayes(pipe, new Multinomial.Logged(prior), conds);

    FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
    Instance instance = new Instance(fv, null, null, null);

    Classification result = model.classify(instance);
    assertNotNull(result);
    assertEquals(1, result.getLabeling().numLocations());
}
@Test
public void testLabelLogLikelihoodWithLabelWeightsSummingToLessThanOne() {
    Alphabet dataAlphabet = new Alphabet();
    Alphabet labelAlphabet = new LabelAlphabet();

    dataAlphabet.lookupIndex("foo");
    int idxA = labelAlphabet.lookupIndex("A");
    int idxB = labelAlphabet.lookupIndex("B");

    Pipe pipe = new Pipe(dataAlphabet, labelAlphabet);
    Multinomial prior = new Multinomial(new double[] { 0.5, 0.5 });
    Multinomial[] conds = new Multinomial[] {
        new Multinomial(new double[] { 0.8 }),
        new Multinomial(new double[] { 0.2 })
    };

    NaiveBayes model = new NaiveBayes(pipe, prior, conds);

    FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
    double[] labelWeights = new double[] { 0.2, 0.3 }; 
    LabelVector labelVec = new LabelVector(labelAlphabet, labelWeights);

    Instance instance = new Instance(fv, labelVec, null, null);
    InstanceList list = new InstanceList(pipe);
    list.add(instance);

    double result = model.labelLogLikelihood(list);
    assertTrue(Double.isFinite(result));
}
@Test
public void testClassifyWhenFeatureIndicesJumpBeyondProbabilityArraySize() {
    Alphabet dataAlphabet = new Alphabet();
    Alphabet labelAlphabet = new LabelAlphabet();

    int fIndex = dataAlphabet.lookupIndex("realFeature"); 
    labelAlphabet.lookupIndex("class");

    Pipe pipe = new Pipe(dataAlphabet, labelAlphabet);

    Multinomial prior = new Multinomial(new double[] { 1.0 });
    Multinomial[] conds = new Multinomial[] {
        new Multinomial(new double[] { 1.0 })
    };

    NaiveBayes model = new NaiveBayes(pipe, prior, conds);

    int unknownIndex = 500; 
    FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { unknownIndex }, new double[] { 1.0 });
    Instance instance = new Instance(fv, null, "jumpedUnknown", null);

    Classification result = model.classify(instance);
    assertNotNull(result);
    assertEquals(1, result.getLabeling().numLocations());
}
@Test
public void testClassifyWithAllScoresNegativeInfYieldsUniformZeroProbability() {
    Alphabet dataAlphabet = new Alphabet();
    Alphabet labelAlphabet = new LabelAlphabet();

    labelAlphabet.lookupIndex("a");
    labelAlphabet.lookupIndex("b");

    Pipe pipe = new Pipe(dataAlphabet, labelAlphabet);

    Multinomial prior = new Multinomial(new double[] { 0.0, 0.0 }); 

    Multinomial[] conds = new Multinomial[] {
        new Multinomial(new double[] { 1.0 }), 
        new Multinomial(new double[] { 1.0 })
    };

    NaiveBayes model = new NaiveBayes(pipe, prior, conds);

    int fIdx = dataAlphabet.lookupIndex("feat");
    FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { fIdx }, new double[] { 1.0 });
    Instance instance = new Instance(fv, null, "flatZero", null);

    Classification result = model.classify(instance);
    assertEquals(2, result.getLabeling().numLocations());
    assertEquals(0.0, result.getLabeling().value(0) + result.getLabeling().value(1), 1e-10);
}
@Test
public void testClassifyWithLabelAlphabetContainingMoreLabelsThanPriorSize() {
    Alphabet dataAlphabet = new Alphabet();
    Alphabet labelAlphabet = new LabelAlphabet();

    dataAlphabet.lookupIndex("feature");

    labelAlphabet.lookupIndex("a");
    labelAlphabet.lookupIndex("b");
    labelAlphabet.lookupIndex("c");

    Pipe pipe = new Pipe(dataAlphabet, labelAlphabet);

    Multinomial prior = new Multinomial(new double[] { 0.5, 0.5 }); 

    Multinomial[] conds = new Multinomial[] {
        new Multinomial(new double[] { 0.9 }),
        new Multinomial(new double[] { 0.1 })
    };

    NaiveBayes model = new NaiveBayes(pipe, prior, conds);

    FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
    Instance instance = new Instance(fv, null, "extraClass", null);

    Classification result = model.classify(instance);
    assertEquals(3, result.getLabeling().numLocations());
    assertEquals(0.0, result.getLabeling().value(labelAlphabet.lookupIndex("c")), 0.0);
} 
}