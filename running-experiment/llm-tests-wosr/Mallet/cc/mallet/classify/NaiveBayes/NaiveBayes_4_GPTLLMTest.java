public class NaiveBayes_4_GPTLLMTest { 

 @Test
    public void testConstructorAndGettersAreCorrect() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f1");
        dataAlphabet.lookupIndex("f2");

        Alphabet labelAlphabet = new Alphabet();
        labelAlphabet.lookupIndex("positive");
        labelAlphabet.lookupIndex("negative");

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            @Override
            public Instance pipe(Instance carrier) {
                return carrier;
            }
        };

        double[] priorProbs = new double[2];
        priorProbs[0] = 0.7;
        priorProbs[1] = 0.3;
        Multinomial prior = new Multinomial(labelAlphabet, priorProbs);
        Multinomial.Logged loggedPrior = new Multinomial.Logged(prior);

        double[] class0 = new double[2];
        class0[0] = 0.8;
        class0[1] = 0.2;
        double[] class1 = new double[2];
        class1[0] = 0.3;
        class1[1] = 0.7;

        Multinomial.Logged[] conditionals = new Multinomial.Logged[2];
        conditionals[0] = new Multinomial.Logged(new Multinomial(dataAlphabet, class0));
        conditionals[1] = new Multinomial.Logged(new Multinomial(dataAlphabet, class1));

        NaiveBayes nb = new NaiveBayes(pipe, loggedPrior, conditionals);

        assertNotNull(nb.getPriors());
        assertEquals(2, nb.getMultinomials().length);
    }
@Test
    public void testSimpleClassificationReturnsCorrectSizeLabeling() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("w1");
        dataAlphabet.lookupIndex("w2");

        Alphabet labelAlphabet = new Alphabet();
        labelAlphabet.lookupIndex("yes");
        labelAlphabet.lookupIndex("no");

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            @Override
            public Instance pipe(Instance carrier) {
                return carrier;
            }
        };

        Multinomial prior = new Multinomial(labelAlphabet, new double[]{0.5, 0.5});
        Multinomial.Logged[] conditionals = new Multinomial.Logged[2];
        conditionals[0] = new Multinomial.Logged(new Multinomial(dataAlphabet, new double[]{0.9, 0.1}));
        conditionals[1] = new Multinomial.Logged(new Multinomial(dataAlphabet, new double[]{0.2, 0.8}));

        NaiveBayes nb = new NaiveBayes(pipe, new Multinomial.Logged(prior), conditionals);

        int[] indices = new int[2];
        indices[0] = dataAlphabet.lookupIndex("w1");
        indices[1] = dataAlphabet.lookupIndex("w2");

        double[] values = new double[2];
        values[0] = 1.0;
        values[1] = 2.0;

        FeatureVector fv = new FeatureVector(dataAlphabet, indices, values);
        Instance instance = new Instance(fv, null, "test", null);

        Classification classification = nb.classify(instance);
        Labeling labeling = classification.getLabeling();
        assertEquals(2, labeling.numLocations());
        assertEquals(1.0, labeling.value(0) + labeling.value(1), 1e-6);
    }
@Test
    public void testClassificationWithUnknownFeatureIsGracefullyHandled() {
        Alphabet trainingAlphabet = new Alphabet();
        trainingAlphabet.lookupIndex("word1");

        Alphabet labelAlphabet = new Alphabet();
        labelAlphabet.lookupIndex("pos");
        labelAlphabet.lookupIndex("neg");

        Pipe trainingPipe = new Pipe(trainingAlphabet, labelAlphabet) {
            @Override
            public Instance pipe(Instance carrier) {
                return carrier;
            }
        };

        Multinomial prior = new Multinomial(labelAlphabet, new double[]{0.4, 0.6});
        Multinomial.Logged[] condProbs = new Multinomial.Logged[2];
        condProbs[0] = new Multinomial.Logged(new Multinomial(trainingAlphabet, new double[]{1.0}));
        condProbs[1] = new Multinomial.Logged(new Multinomial(trainingAlphabet, new double[]{1.0}));

        NaiveBayes nb = new NaiveBayes(trainingPipe, new Multinomial.Logged(prior), condProbs);

        Alphabet unseenAlphabet = new Alphabet();
        unseenAlphabet.lookupIndex("unknown_word");

        int[] indices = new int[1];
        indices[0] = unseenAlphabet.lookupIndex("unknown_word");
        double[] values = new double[1];
        values[0] = 10.0;

        FeatureVector fv = new FeatureVector(unseenAlphabet, indices, values);
        Instance testInstance = new Instance(fv, null, "unseen_feature", null);
        trainingPipe.setDataAlphabet(unseenAlphabet);

        Classification classification = nb.classify(testInstance);
        assertEquals(2, classification.getLabeling().numLocations());
    }
@Test
    public void testLabelLogLikelihoodWithSingleLabel() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("w1");

        Alphabet labelAlphabet = new Alphabet();
        labelAlphabet.lookupIndex("a");
        labelAlphabet.lookupIndex("b");

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            @Override
            public Instance pipe(Instance carrier) {
                return carrier;
            }
        };

        Multinomial.Logged prior = new Multinomial.Logged(new Multinomial(labelAlphabet, new double[]{0.5, 0.5}));
        Multinomial.Logged[] cond = new Multinomial.Logged[2];
        cond[0] = new Multinomial.Logged(new Multinomial(dataAlphabet, new double[]{1.0}));
        cond[1] = new Multinomial.Logged(new Multinomial(dataAlphabet, new double[]{1.0}));

        NaiveBayes nb = new NaiveBayes(pipe, prior, cond);

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        double[] labelProbs = new double[]{1.0, 0.0};
        Labeling label = new LabelVector(labelAlphabet, labelProbs);
        Instance instance = new Instance(fv, label, "labeled", null);

        InstanceList ilist = new InstanceList(pipe);
        ilist.add(instance);

        double logLikelihood = nb.labelLogLikelihood(ilist);
        assertTrue(Double.isFinite(logLikelihood));
    }
@Test
    public void testDataLogLikelihoodWithUnlabeledInstanceReturnsValidScore() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f1");

        Alphabet labelAlphabet = new Alphabet();
        labelAlphabet.lookupIndex("good");
        labelAlphabet.lookupIndex("bad");

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            @Override
            public Instance pipe(Instance carrier) {
                return carrier;
            }
        };

        double[] prior = new double[]{0.6, 0.4};
        double[] cond0 = new double[]{0.9};
        double[] cond1 = new double[]{0.1};

        Multinomial.Logged[] condProbs = new Multinomial.Logged[2];
        condProbs[0] = new Multinomial.Logged(new Multinomial(dataAlphabet, cond0));
        condProbs[1] = new Multinomial.Logged(new Multinomial(dataAlphabet, cond1));

        NaiveBayes nb = new NaiveBayes(pipe, new Multinomial.Logged(new Multinomial(labelAlphabet, prior)), condProbs);

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        Instance instance = new Instance(fv, null, "unlabeled", null);

        InstanceList ilist = new InstanceList(pipe);
        ilist.add(instance);

        double result = nb.dataLogLikelihood(ilist);
        assertTrue(Double.isFinite(result));
    }
@Test
    public void testSerializationRoundTripPreservesMultinomials() throws IOException, ClassNotFoundException {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("token");

        Alphabet labelAlphabet = new Alphabet();
        labelAlphabet.lookupIndex("yes");

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            @Override
            public Instance pipe(Instance instance) {
                return instance;
            }
        };

        Multinomial prior = new Multinomial(labelAlphabet, new double[]{1.0});
        Multinomial[] conditionals = new Multinomial[1];
        conditionals[0] = new Multinomial(dataAlphabet, new double[]{1.0});

        NaiveBayes original = new NaiveBayes(pipe, prior, conditionals);

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bout);
        out.writeObject(original);
        out.close();

        ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bin);
        NaiveBayes clone = (NaiveBayes) in.readObject();
        in.close();

        assertNotNull(clone.getPriors());
        assertEquals(1, clone.getMultinomials().length);
    }
@Test(expected = ClassNotFoundException.class)
    public void testReadObjectThrowsForVersionMismatch() throws IOException, ClassNotFoundException {
        Alphabet dummyAlphabet = new Alphabet();
        dummyAlphabet.lookupIndex("f");

        Pipe dummyPipe = new Pipe(dummyAlphabet, dummyAlphabet) {
            @Override
            public Instance pipe(Instance instance) {
                return instance;
            }
        };

        Multinomial.Logged prior = new Multinomial.Logged(new Multinomial(dummyAlphabet, new double[]{1.0}));
        Multinomial.Logged[] conds = new Multinomial.Logged[]{new Multinomial.Logged(new Multinomial(dummyAlphabet, new double[]{1.0}))};

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ObjectOutputStream writer = new ObjectOutputStream(outStream);
        writer.writeInt(999); 
        writer.writeObject(dummyPipe);
        writer.writeObject(prior);
        writer.writeObject(conds);
        writer.close();

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outStream.toByteArray());
        ObjectInputStream reader = new ObjectInputStream(inputStream);

        NaiveBayes nb = new NaiveBayes(dummyPipe, prior, conds);
        nb.readObject(reader); 
    }
@Test
    public void testPrintWordsDoesNotThrowException() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("apple");
        dataAlphabet.lookupIndex("banana");

        Alphabet labelAlphabet = new Alphabet();
        labelAlphabet.lookupIndex("fruit");
        labelAlphabet.lookupIndex("not-fruit");

        Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
            @Override
            public Instance pipe(Instance instance) {
                return instance;
            }
        };

        Multinomial.Logged prior = new Multinomial.Logged(new Multinomial(labelAlphabet, new double[]{0.5, 0.5}));
        Multinomial.Logged[] conds = new Multinomial.Logged[2];
        conds[0] = new Multinomial.Logged(new Multinomial(dataAlphabet, new double[]{0.6, 0.4}));
        conds[1] = new Multinomial.Logged(new Multinomial(dataAlphabet, new double[]{0.3, 0.7}));

        NaiveBayes nb = new NaiveBayes(pipe, prior, conds);
        nb.printWords(2); 
    }
@Test
public void testClassificationWithEmptyFeatureVector() {
    Alphabet dataAlphabet = new Alphabet();
    Alphabet labelAlphabet = new Alphabet();
    labelAlphabet.lookupIndex("A");
    labelAlphabet.lookupIndex("B");

    Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
        @Override
        public Instance pipe(Instance carrier) {
            return carrier;
        }
    };

    Multinomial prior = new Multinomial(labelAlphabet, new double[]{0.5, 0.5});
    Multinomial[] conds = new Multinomial[2];
    conds[0] = new Multinomial(dataAlphabet, new double[0]);
    conds[1] = new Multinomial(dataAlphabet, new double[0]);

    NaiveBayes nb = new NaiveBayes(pipe, prior, conds);

    FeatureVector fv = new FeatureVector(dataAlphabet, new int[0], new double[0]);
    Instance inst = new Instance(fv, null, "empty-feature", null);

    Classification c = nb.classify(inst);
    Labeling labeling = c.getLabeling();
    assertEquals(2, labeling.numLocations());
    assertEquals(1.0, labeling.value(0) + labeling.value(1), 1e-6);
}
@Test
public void testLabelAlphabetGrowsAfterTraining() {
    Alphabet dataAlphabet = new Alphabet();
    dataAlphabet.lookupIndex("f");

    Alphabet labelAlphabet = new Alphabet();
    labelAlphabet.lookupIndex("seen");

    Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
        @Override
        public Instance pipe(Instance c) {
            return c;
        }
    };

    Multinomial prior = new Multinomial(labelAlphabet, new double[]{1.0});
    Multinomial[] conds = new Multinomial[1];
    conds[0] = new Multinomial(dataAlphabet, new double[]{1.0});

    NaiveBayes nb = new NaiveBayes(pipe, prior, conds);

    
    labelAlphabet.lookupIndex("unseen");

    FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
    Instance inst = new Instance(fv, null, "grow-label", null);

    Classification c = nb.classify(inst);
    assertEquals(2, c.getLabeling().numLocations());
    assertEquals(1.0, c.getLabeling().value(0) + c.getLabeling().value(1), 1e-6);
}
@Test
public void testDataLogLikelihoodMixedLabels() {
    Alphabet dataAlphabet = new Alphabet();
    dataAlphabet.lookupIndex("word");

    Alphabet labelAlphabet = new Alphabet();
    labelAlphabet.lookupIndex("pos");
    labelAlphabet.lookupIndex("neg");

    Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
        @Override
        public Instance pipe(Instance i) {
            return i;
        }
    };

    Multinomial prior = new Multinomial(labelAlphabet, new double[]{0.6, 0.4});
    Multinomial[] conds = new Multinomial[2];
    conds[0] = new Multinomial(dataAlphabet, new double[]{0.9});
    conds[1] = new Multinomial(dataAlphabet, new double[]{0.1});

    NaiveBayes nb = new NaiveBayes(pipe, prior, conds);

    FeatureVector fv1 = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
    Instance inst1 = new Instance(fv1, null, "unlabeled", null);

    double[] label = new double[]{1.0, 0.0};
    FeatureVector fv2 = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{2.0});
    Labeling labeling = new LabelVector(labelAlphabet, label);
    Instance inst2 = new Instance(fv2, labeling, "labeled", null);

    InstanceList ilist = new InstanceList(pipe);
    ilist.add(inst1);
    ilist.add(inst2);

    double result = nb.dataLogLikelihood(ilist);
    assertTrue(Double.isFinite(result));
}
@Test
public void testAllUnseenFeaturesDuringClassification() {
    Alphabet trainAlpha = new Alphabet();
    trainAlpha.lookupIndex("trained");

    Alphabet labelAlpha = new Alphabet();
    labelAlpha.lookupIndex("yes");
    labelAlpha.lookupIndex("no");

    Pipe pipe = new Pipe(trainAlpha, labelAlpha) {
        @Override
        public Instance pipe(Instance x) {
            return x;
        }
    };

    Multinomial prior = new Multinomial(labelAlpha, new double[]{0.5, 0.5});
    Multinomial[] conds = new Multinomial[2];
    conds[0] = new Multinomial(trainAlpha, new double[]{1.0});
    conds[1] = new Multinomial(trainAlpha, new double[]{1.0});

    NaiveBayes nb = new NaiveBayes(pipe, prior, conds);

    Alphabet testAlpha = new Alphabet();
    int idx = testAlpha.lookupIndex("new");

    FeatureVector fv = new FeatureVector(testAlpha, new int[]{idx}, new double[]{1.0});
    pipe.setDataAlphabet(testAlpha);

    Instance inst = new Instance(fv, null, "unseen-feature", null);
    Classification c = nb.classify(inst);

    assertEquals(2, c.getLabeling().numLocations());
    assertEquals(1.0, c.getLabeling().value(0) + c.getLabeling().value(1), 1e-6);
}
@Test
public void testLabelLogLikelihoodAllNullLabels() {
    Alphabet featureAlphabet = new Alphabet();
    featureAlphabet.lookupIndex("tok");

    Alphabet labelAlphabet = new Alphabet();
    labelAlphabet.lookupIndex("class");

    Pipe pipe = new Pipe(featureAlphabet, labelAlphabet) {
        @Override
        public Instance pipe(Instance i) {
            return i;
        }
    };

    Multinomial prior = new Multinomial(labelAlphabet, new double[]{1.0});
    Multinomial[] conds = new Multinomial[]{new Multinomial(featureAlphabet, new double[]{1.0})};

    NaiveBayes nb = new NaiveBayes(pipe, prior, conds);

    FeatureVector fv = new FeatureVector(featureAlphabet, new int[]{0}, new double[]{1.0});
    Instance inst1 = new Instance(fv, null, "no-label-1", null);
    Instance inst2 = new Instance(fv, null, "no-label-2", null);

    InstanceList ilist = new InstanceList(pipe);
    ilist.add(inst1);
    ilist.add(inst2);

    double result = nb.labelLogLikelihood(ilist);
    assertEquals(0.0, result, 1e-9);
}
@Test
public void testSerializationWithEmptyConditionals() throws IOException, ClassNotFoundException {
    Alphabet featureAlphabet = new Alphabet();
    Alphabet labelAlphabet = new Alphabet();
    labelAlphabet.lookupIndex("a");

    Pipe pipe = new Pipe(featureAlphabet, labelAlphabet) {
        @Override
        public Instance pipe(Instance i) {
            return i;
        }
    };

    Multinomial prior = new Multinomial(labelAlphabet, new double[]{1.0});
    Multinomial[] conds = new Multinomial[1];
    conds[0] = new Multinomial(featureAlphabet, new double[0]);

    NaiveBayes original = new NaiveBayes(pipe, prior, conds);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream out = new ObjectOutputStream(baos);
    out.writeObject(original);
    out.close();

    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    ObjectInputStream in = new ObjectInputStream(bais);
    NaiveBayes clone = (NaiveBayes) in.readObject();
    in.close();

    assertNotNull(clone.getPriors());
    assertEquals(1, clone.getMultinomials().length);
}
@Test
public void testDataLogLikelihoodWithZeroFeatureVector() {
    Alphabet featureAlphabet = new Alphabet();
    Alphabet labelAlphabet = new Alphabet();
    labelAlphabet.lookupIndex("c1");

    Pipe pipe = new Pipe(featureAlphabet, labelAlphabet) {
        @Override
        public Instance pipe(Instance i) {
            return i;
        }
    };

    Multinomial prior = new Multinomial(labelAlphabet, new double[]{1.0});
    Multinomial[] conds = new Multinomial[1];
    conds[0] = new Multinomial(featureAlphabet, new double[0]);

    NaiveBayes nb = new NaiveBayes(pipe, prior, conds);

    FeatureVector fv = new FeatureVector(featureAlphabet, new int[0], new double[0]);
    Labeling labeling = new LabelVector(labelAlphabet, new double[]{1.0});
    Instance inst = new Instance(fv, labeling, "zero-vector", null);

    InstanceList ilist = new InstanceList(pipe);
    ilist.add(inst);

    double ll = nb.dataLogLikelihood(ilist);
    assertTrue(Double.isFinite(ll));
}
@Test
public void testClassificationWithZeroProbabilitiesInPrior() {
    Alphabet featureAlphabet = new Alphabet();
    featureAlphabet.lookupIndex("f");

    Alphabet labelAlphabet = new Alphabet();
    labelAlphabet.lookupIndex("label1");
    labelAlphabet.lookupIndex("label2");

    Pipe pipe = new Pipe(featureAlphabet, labelAlphabet) {
        @Override
        public Instance pipe(Instance instance) {
            return instance;
        }
    };

    double[] priorProbs = new double[]{1.0, 0.0};
    Multinomial prior = new Multinomial(labelAlphabet, priorProbs);

    double[] probs1 = new double[]{1.0};
    double[] probs2 = new double[]{1.0};

    Multinomial[] conditionals = new Multinomial[2];
    conditionals[0] = new Multinomial(featureAlphabet, probs1);
    conditionals[1] = new Multinomial(featureAlphabet, probs2);

    NaiveBayes nb = new NaiveBayes(pipe, prior, conditionals);

    int[] featureIndices = new int[]{0};
    double[] featureValues = new double[]{1.0};
    FeatureVector fv = new FeatureVector(featureAlphabet, featureIndices, featureValues);
    Instance instance = new Instance(fv, null, "test", null);

    Classification classification = nb.classify(instance);
    Labeling labeling = classification.getLabeling();
    assertEquals(2, labeling.numLocations());
    assertEquals(1.0, labeling.value(0) + labeling.value(1), 1e-6);
}
@Test
public void testClassificationWithZeroProbabilitiesForAllLabels() {
    Alphabet featureAlphabet = new Alphabet();
    featureAlphabet.lookupIndex("f");

    Alphabet labelAlphabet = new Alphabet();
    labelAlphabet.lookupIndex("l1");
    labelAlphabet.lookupIndex("l2");

    Pipe pipe = new Pipe(featureAlphabet, labelAlphabet) {
        @Override
        public Instance pipe(Instance i) {
            return i;
        }
    };

    double[] priorProbs = new double[]{0.0, 0.0};
    Multinomial prior = new Multinomial(labelAlphabet, priorProbs);

    double[] probs1 = new double[]{0.0};
    double[] probs2 = new double[]{0.0};

    Multinomial[] conditionals = new Multinomial[2];
    conditionals[0] = new Multinomial(featureAlphabet, probs1);
    conditionals[1] = new Multinomial(featureAlphabet, probs2);

    NaiveBayes nb = new NaiveBayes(pipe, prior, conditionals);

    int[] indices = new int[]{0};
    double[] values = new double[]{5.0};
    FeatureVector fv = new FeatureVector(featureAlphabet, indices, values);
    Instance instance = new Instance(fv, null, "zeroAll", null);

    Classification classification = nb.classify(instance);
    Labeling labeling = classification.getLabeling();
    assertEquals(2, labeling.numLocations());
    assertEquals(1.0, labeling.value(0) + labeling.value(1), 1e-6);
}
@Test
public void testPrintWordsWithZeroFeatures() {
    Alphabet featureAlphabet = new Alphabet();

    Alphabet labelAlphabet = new Alphabet();
    labelAlphabet.lookupIndex("X");

    Pipe pipe = new Pipe(featureAlphabet, labelAlphabet) {
        @Override
        public Instance pipe(Instance instance) {
            return instance;
        }
    };

    double[] priorProbs = new double[]{1.0};
    Multinomial prior = new Multinomial(labelAlphabet, priorProbs);

    Multinomial[] conditionals = new Multinomial[]{new Multinomial(featureAlphabet, new double[]{})};

    NaiveBayes nb = new NaiveBayes(pipe, prior, conditionals);
    nb.printWords(0);
}
@Test
public void testLabelLogLikelihoodWithZeroWeightInstance() {
    Alphabet featureAlphabet = new Alphabet();
    featureAlphabet.lookupIndex("f");

    Alphabet labelAlphabet = new Alphabet();
    labelAlphabet.lookupIndex("label");

    Pipe pipe = new Pipe(featureAlphabet, labelAlphabet) {
        @Override
        public Instance pipe(Instance i) {
            return i;
        }
    };

    double[] priorProbs = new double[]{1.0};
    double[] condProbs = new double[]{1.0};

    Multinomial prior = new Multinomial(labelAlphabet, priorProbs);
    Multinomial[] conditionals = new Multinomial[]{new Multinomial(featureAlphabet, condProbs)};

    NaiveBayes nb = new NaiveBayes(pipe, prior, conditionals);

    int[] indices = new int[]{0};
    double[] values = new double[]{1.0};
    FeatureVector fv = new FeatureVector(featureAlphabet, indices, values);

    double[] labelValues = new double[]{1.0};
    Labeling label = new LabelVector(labelAlphabet, labelValues);
    Instance instance = new Instance(fv, label, "zeroWeight", null);

    InstanceList list = new InstanceList(pipe);
    list.add(instance, 0.0); 

    double result = nb.labelLogLikelihood(list);
    assertEquals(0.0, result, 1e-9);
}
@Test
public void testDataLogLikelihoodWithZeroWeightInstance() {
    Alphabet featureAlphabet = new Alphabet();
    featureAlphabet.lookupIndex("f");

    Alphabet labelAlphabet = new Alphabet();
    labelAlphabet.lookupIndex("c");

    Pipe pipe = new Pipe(featureAlphabet, labelAlphabet) {
        @Override
        public Instance pipe(Instance i) {
            return i;
        }
    };

    double[] priorProbs = new double[]{1.0};
    double[] condProbs = new double[]{1.0};

    Multinomial prior = new Multinomial(labelAlphabet, priorProbs);
    Multinomial[] conditionals = new Multinomial[]{new Multinomial(featureAlphabet, condProbs)};

    NaiveBayes nb = new NaiveBayes(pipe, prior, conditionals);

    FeatureVector fv = new FeatureVector(featureAlphabet, new int[]{0}, new double[]{1.0});
    Instance instance = new Instance(fv, null, "unlabeled", null);

    InstanceList list = new InstanceList(pipe);
    list.add(instance, 0.0);

    double likelihood = nb.dataLogLikelihood(list);
    assertEquals(0.0, likelihood, 1e-9);
}
@Test
public void testClassificationWithLargeFeatureWeight() {
    Alphabet featureAlphabet = new Alphabet();
    featureAlphabet.lookupIndex("hello");

    Alphabet labelAlphabet = new Alphabet();
    labelAlphabet.lookupIndex("a");
    labelAlphabet.lookupIndex("b");

    Pipe pipe = new Pipe(featureAlphabet, labelAlphabet) {
        @Override
        public Instance pipe(Instance i) {
            return i;
        }
    };

    Multinomial prior = new Multinomial(labelAlphabet, new double[]{0.5, 0.5});
    Multinomial[] conds = new Multinomial[2];
    conds[0] = new Multinomial(featureAlphabet, new double[]{0.99});
    conds[1] = new Multinomial(featureAlphabet, new double[]{0.01});

    NaiveBayes nb = new NaiveBayes(pipe, prior, conds);

    FeatureVector fv = new FeatureVector(featureAlphabet, new int[]{0}, new double[]{10000.0});
    Instance instance = new Instance(fv, null, "skewed", null);

    Classification result = nb.classify(instance);
    Labeling labeling = result.getLabeling();
    assertEquals(2, labeling.numLocations());
    assertEquals(1.0, labeling.value(0) + labeling.value(1), 1e-6);
    assertTrue(labeling.value(0) > 0.999); 
}
@Test
public void testClassificationWithOnlyOneLabel() {
    Alphabet dataAlphabet = new Alphabet();
    dataAlphabet.lookupIndex("feature");

    Alphabet labelAlphabet = new Alphabet();
    labelAlphabet.lookupIndex("single_label");

    Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
        @Override
        public Instance pipe(Instance carrier) {
            return carrier;
        }
    };

    Multinomial prior = new Multinomial(labelAlphabet, new double[]{1.0});
    Multinomial[] conds = new Multinomial[1];
    conds[0] = new Multinomial(dataAlphabet, new double[]{1.0});

    NaiveBayes nb = new NaiveBayes(pipe, prior, conds);

    FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
    Instance instance = new Instance(fv, null, "single_label_test", null);

    Classification classification = nb.classify(instance);
    Labeling labeling = classification.getLabeling();

    assertEquals(1, labeling.numLocations());
    assertEquals(1.0, labeling.valueAtLocation(0), 1e-6);
}
@Test
public void testDataLogLikelihoodWithMultipleInstanceWeights() {
    Alphabet featureAlphabet = new Alphabet();
    featureAlphabet.lookupIndex("term");

    Alphabet labelAlphabet = new Alphabet();
    labelAlphabet.lookupIndex("A");
    labelAlphabet.lookupIndex("B");

    Pipe pipe = new Pipe(featureAlphabet, labelAlphabet) {
        @Override
        public Instance pipe(Instance carrier) {
            return carrier;
        }
    };

    Multinomial prior = new Multinomial(labelAlphabet, new double[]{0.5, 0.5});
    Multinomial[] conds = new Multinomial[2];
    conds[0] = new Multinomial(featureAlphabet, new double[]{0.7});
    conds[1] = new Multinomial(featureAlphabet, new double[]{0.3});

    NaiveBayes nb = new NaiveBayes(pipe, prior, conds);

    FeatureVector fv1 = new FeatureVector(featureAlphabet, new int[]{0}, new double[]{1.0});
    double[] label1 = new double[]{1.0, 0.0};
    Labeling lab1 = new LabelVector(labelAlphabet, label1);
    Instance inst1 = new Instance(fv1, lab1, "inst1", null);

    FeatureVector fv2 = new FeatureVector(featureAlphabet, new int[]{0}, new double[]{2.0});
    double[] label2 = new double[]{0.0, 1.0};
    Labeling lab2 = new LabelVector(labelAlphabet, label2);
    Instance inst2 = new Instance(fv2, lab2, "inst2", null);

    InstanceList list = new InstanceList(pipe);
    list.add(inst1, 2.0); 
    list.add(inst2, 0.5); 

    double ll = nb.dataLogLikelihood(list);
    assertTrue(Double.isFinite(ll));
}
@Test
public void testClassifyWithEmptyLabelAlphabetReturnsEmptyLabeling() {
    Alphabet dataAlphabet = new Alphabet();
    dataAlphabet.lookupIndex("feat");

    Alphabet labelAlphabet = new Alphabet(); 

    Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
        @Override
        public Instance pipe(Instance carrier) {
            return carrier;
        }
    };

    Multinomial prior = new Multinomial(labelAlphabet, new double[0]);
    Multinomial[] conds = new Multinomial[0];

    NaiveBayes nb = new NaiveBayes(pipe, prior, conds);

    FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
    Instance instance = new Instance(fv, null, "no-labels", null);

    Classification classification = nb.classify(instance);
    Labeling labeling = classification.getLabeling();

    assertEquals(0, labeling.numLocations());
}
@Test
public void testClassificationWhereFeatureVectorHasDuplicateIndicesIsHandled() {
    Alphabet dataAlphabet = new Alphabet();
    dataAlphabet.lookupIndex("word");

    Alphabet labelAlphabet = new Alphabet();
    labelAlphabet.lookupIndex("true");
    labelAlphabet.lookupIndex("false");

    Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
        @Override
        public Instance pipe(Instance i) {
            return i;
        }
    };

    Multinomial prior = new Multinomial(labelAlphabet, new double[]{0.5, 0.5});
    Multinomial[] conds = new Multinomial[2];
    conds[0] = new Multinomial(dataAlphabet, new double[]{0.9});
    conds[1] = new Multinomial(dataAlphabet, new double[]{0.1});

    NaiveBayes nb = new NaiveBayes(pipe, prior, conds);

    int[] indices = new int[]{0, 0};
    double[] values = new double[]{1.0, 1.0}; 
    FeatureVector fv = new FeatureVector(dataAlphabet, indices, values);

    Instance instance = new Instance(fv, null, "duplicate_feature", null);

    Classification result = nb.classify(instance);
    Labeling labeling = result.getLabeling();

    assertEquals(2, labeling.numLocations());
    assertEquals(1.0, labeling.value(0) + labeling.value(1), 1e-6);
}
@Test
public void testClassificationWhenFeatureValueIsZero() {
    Alphabet featureAlphabet = new Alphabet();
    featureAlphabet.lookupIndex("x");

    Alphabet labelAlphabet = new Alphabet();
    labelAlphabet.lookupIndex("one");
    labelAlphabet.lookupIndex("zero");

    Pipe pipe = new Pipe(featureAlphabet, labelAlphabet) {
        @Override
        public Instance pipe(Instance i) {
            return i;
        }
    };

    Multinomial prior = new Multinomial(labelAlphabet, new double[]{0.5, 0.5});
    Multinomial[] conds = new Multinomial[2];
    conds[0] = new Multinomial(featureAlphabet, new double[]{0.6});
    conds[1] = new Multinomial(featureAlphabet, new double[]{0.4});

    NaiveBayes nb = new NaiveBayes(pipe, prior, conds);

    int[] indices = new int[]{0};
    double[] values = new double[]{0.0}; 
    FeatureVector fv = new FeatureVector(featureAlphabet, indices, values);

    Instance instance = new Instance(fv, null, "zero-value", null);

    Classification result = nb.classify(instance);
    Labeling labeling = result.getLabeling();

    assertEquals(2, labeling.numLocations());
    assertEquals(1.0, labeling.value(0) + labeling.value(1), 1e-6);
}
@Test
public void testLabelLogLikelihoodWithSoftLabelsMultipleLocations() {
    Alphabet featureAlphabet = new Alphabet();
    featureAlphabet.lookupIndex("token");

    Alphabet labelAlphabet = new Alphabet();
    labelAlphabet.lookupIndex("yes");
    labelAlphabet.lookupIndex("no");

    Pipe pipe = new Pipe(featureAlphabet, labelAlphabet) {
        @Override
        public Instance pipe(Instance carrier) {
            return carrier;
        }
    };

    Multinomial prior = new Multinomial(labelAlphabet, new double[]{0.5, 0.5});
    Multinomial[] conds = new Multinomial[2];
    conds[0] = new Multinomial(featureAlphabet, new double[]{0.7});
    conds[1] = new Multinomial(featureAlphabet, new double[]{0.3});

    NaiveBayes nb = new NaiveBayes(pipe, prior, conds);

    int[] indices = new int[]{0};
    double[] values = new double[]{1.0};
    FeatureVector fv = new FeatureVector(featureAlphabet, indices, values);
    double[] labelProbs = new double[]{0.4, 0.6};
    Labeling label = new LabelVector(labelAlphabet, labelProbs);
    Instance instance = new Instance(fv, label, "soft-labeled", null);

    InstanceList list = new InstanceList(pipe);
    list.add(instance);

    double ll = nb.labelLogLikelihood(list);
    assertTrue(Double.isFinite(ll));
}
@Test
public void testClassificationWithNoFeatureIndices() {
    Alphabet dataAlphabet = new Alphabet();
    dataAlphabet.lookupIndex("unused");

    Alphabet labelAlphabet = new Alphabet();
    labelAlphabet.lookupIndex("c1");
    labelAlphabet.lookupIndex("c2");

    Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
        @Override
        public Instance pipe(Instance instance) {
            return instance;
        }
    };

    Multinomial prior = new Multinomial(labelAlphabet, new double[]{0.5, 0.5});
    Multinomial[] conds = new Multinomial[2];
    conds[0] = new Multinomial(dataAlphabet, new double[]{1.0});
    conds[1] = new Multinomial(dataAlphabet, new double[]{1.0});

    NaiveBayes nb = new NaiveBayes(pipe, prior, conds);

    FeatureVector fv = new FeatureVector(dataAlphabet, new int[0], new double[0]);
    Instance instance = new Instance(fv, null, "empty-fv", null);

    Classification cls = nb.classify(instance);
    assertEquals(2, cls.getLabeling().numLocations());
    assertEquals(1.0, cls.getLabeling().value(0) + cls.getLabeling().value(1), 1e-6);
}
@Test
public void testClassificationLabelIndexExceedsConditionalsLength() {
    Alphabet featureAlphabet = new Alphabet();
    featureAlphabet.lookupIndex("tok");

    Alphabet labelAlphabet = new Alphabet();
    labelAlphabet.lookupIndex("A");
    labelAlphabet.lookupIndex("B");
    labelAlphabet.lookupIndex("C");

    Pipe pipe = new Pipe(featureAlphabet, labelAlphabet) {
        @Override
        public Instance pipe(Instance instance) {
            return instance;
        }
    };

    double[] priorProbs = new double[]{0.4, 0.3, 0.3};
    Multinomial prior = new Multinomial(labelAlphabet, priorProbs);

    Multinomial[] conds = new Multinomial[2]; 
    conds[0] = new Multinomial(featureAlphabet, new double[]{1.0});
    conds[1] = new Multinomial(featureAlphabet, new double[]{1.0});

    NaiveBayes nb = new NaiveBayes(pipe, prior, conds);

    int[] idx = new int[]{0};
    double[] val = new double[]{1.0};
    FeatureVector fv = new FeatureVector(featureAlphabet, idx, val);
    Instance instance = new Instance(fv, null, "label-index-out-of-bounds", null);

    Classification result = nb.classify(instance);
    assertEquals(3, result.getLabeling().numLocations());
    assertEquals(1.0, result.getLabeling().value(0) + result.getLabeling().value(1) + result.getLabeling().value(2), 1e-6);
}
@Test
public void testClassificationWithNullPipeAllowsAnyAlphabet() {
    Alphabet features = new Alphabet();
    features.lookupIndex("x");

    Alphabet labels = new Alphabet();
    labels.lookupIndex("A");
    labels.lookupIndex("B");

    Multinomial prior = new Multinomial(labels, new double[]{0.5, 0.5});
    Multinomial[] conds = new Multinomial[2];
    conds[0] = new Multinomial(features, new double[]{1.0});
    conds[1] = new Multinomial(features, new double[]{1.0});

    NaiveBayes nb = new NaiveBayes(null, prior, conds); 

    Alphabet otherAlphabet = new Alphabet();
    int featureIndex = otherAlphabet.lookupIndex("x");

    FeatureVector fv = new FeatureVector(otherAlphabet, new int[]{featureIndex}, new double[]{1.0});
    Instance inst = new Instance(fv, null, "pipe-null", null);

    Classification result = nb.classify(inst);
    assertEquals(2, result.getLabeling().numLocations());
    assertEquals(1.0, result.getLabeling().value(0) + result.getLabeling().value(1), 1e-6);
}
@Test
public void testSerializationWithNullPipeSucceeds() throws IOException, ClassNotFoundException {
    Alphabet f = new Alphabet();
    f.lookupIndex("val");

    Alphabet l = new Alphabet();
    l.lookupIndex("A");

    Multinomial prior = new Multinomial(l, new double[]{1.0});
    Multinomial[] conds = new Multinomial[]{new Multinomial(f, new double[]{1.0})};

    NaiveBayes original = new NaiveBayes(null, prior, conds);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream out = new ObjectOutputStream(baos);
    out.writeObject(original);
    out.close();

    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    ObjectInputStream in = new ObjectInputStream(bais);
    NaiveBayes deserialized = (NaiveBayes) in.readObject();

    assertNotNull(deserialized.getPriors());
    assertEquals(1, deserialized.getMultinomials().length);
}
@Test
public void testLogLikelihoodWithEmptyInstanceListReturnsZero() {
    Alphabet featureAlphabet = new Alphabet();
    featureAlphabet.lookupIndex("feat");

    Alphabet labelAlphabet = new Alphabet();
    labelAlphabet.lookupIndex("c");

    Pipe pipe = new Pipe(featureAlphabet, labelAlphabet) {
        @Override
        public Instance pipe(Instance inst) {
            return inst;
        }
    };

    Multinomial prior = new Multinomial(labelAlphabet, new double[]{1.0});
    Multinomial[] conditionals = new Multinomial[]{new Multinomial(featureAlphabet, new double[]{1.0})};

    NaiveBayes classifier = new NaiveBayes(pipe, prior, conditionals);

    InstanceList emptyList = new InstanceList(pipe);

    double labelLogLikelihood = classifier.labelLogLikelihood(emptyList);
    double dataLogLikelihood = classifier.dataLogLikelihood(emptyList);

    assertEquals(0.0, labelLogLikelihood, 1e-9);
    assertEquals(0.0, dataLogLikelihood, 1e-9);
}
@Test
public void testClassificationWithNullLabelingInInstance() {
    Alphabet featureAlphabet = new Alphabet();
    featureAlphabet.lookupIndex("token");

    Alphabet labelAlphabet = new Alphabet();
    labelAlphabet.lookupIndex("yes");
    labelAlphabet.lookupIndex("no");

    Pipe pipe = new Pipe(featureAlphabet, labelAlphabet) {
        @Override
        public Instance pipe(Instance instance) {
            return instance;
        }
    };

    Multinomial prior = new Multinomial(labelAlphabet, new double[]{0.6, 0.4});
    Multinomial[] conditionals = new Multinomial[2];
    conditionals[0] = new Multinomial(featureAlphabet, new double[]{0.7});
    conditionals[1] = new Multinomial(featureAlphabet, new double[]{0.3});

    NaiveBayes classifier = new NaiveBayes(pipe, prior, conditionals);

    FeatureVector fv = new FeatureVector(featureAlphabet, new int[]{0}, new double[]{1.0});
    Instance instance = new Instance(fv, null, "no-label", null);

    Classification result = classifier.classify(instance);
    assertNotNull(result);
    assertEquals(2, result.getLabeling().numLocations());
}
@Test
public void testClassificationWithAllZeroFeatureValues() {
    Alphabet featureAlphabet = new Alphabet();
    featureAlphabet.lookupIndex("word");

    Alphabet labelAlphabet = new Alphabet();
    labelAlphabet.lookupIndex("A");
    labelAlphabet.lookupIndex("B");

    Pipe pipe = new Pipe(featureAlphabet, labelAlphabet) {
        @Override
        public Instance pipe(Instance inst) {
            return inst;
        }
    };

    double[] priorProbs = new double[]{0.5, 0.5};
    Multinomial prior = new Multinomial(labelAlphabet, priorProbs);

    double[] cond1 = new double[]{0.8};
    double[] cond2 = new double[]{0.2};

    Multinomial[] conds = new Multinomial[2];
    conds[0] = new Multinomial(featureAlphabet, cond1);
    conds[1] = new Multinomial(featureAlphabet, cond2);

    NaiveBayes classifier = new NaiveBayes(pipe, prior, conds);

    int[] indices = new int[]{0};
    double[] values = new double[]{0.0}; 

    FeatureVector fv = new FeatureVector(featureAlphabet, indices, values);
    Instance instance = new Instance(fv, null, "zero-values", null);

    Classification classification = classifier.classify(instance);
    Labeling result = classification.getLabeling();
    assertEquals(2, result.numLocations());
    assertEquals(1.0, result.value(0) + result.value(1), 1e-6);
}
@Test
public void testClassificationWithFeatureNotInConditionalMultinomial() {
    Alphabet trainAlphabet = new Alphabet();
    trainAlphabet.lookupIndex("known");

    Alphabet labelAlphabet = new Alphabet();
    labelAlphabet.lookupIndex("one");
    labelAlphabet.lookupIndex("two");

    Pipe pipe = new Pipe(trainAlphabet, labelAlphabet) {
        @Override
        public Instance pipe(Instance inst) {
            return inst;
        }
    };

    double[] priorProbs = new double[]{0.5, 0.5};
    Multinomial prior = new Multinomial(labelAlphabet, priorProbs);

    double[] cond1 = new double[]{1.0};
    double[] cond2 = new double[]{1.0};

    Multinomial[] conds = new Multinomial[2];
    conds[0] = new Multinomial(trainAlphabet, cond1);
    conds[1] = new Multinomial(trainAlphabet, cond2);

    NaiveBayes classifier = new NaiveBayes(pipe, prior, conds);

    Alphabet testAlphabet = new Alphabet();
    int unseenFeatureIdx = testAlphabet.lookupIndex("unseen");

    FeatureVector fv = new FeatureVector(testAlphabet, new int[]{unseenFeatureIdx}, new double[]{1.0});
    pipe.setDataAlphabet(testAlphabet);

    Instance instance = new Instance(fv, null, "unseen-feature", null);

    Classification result = classifier.classify(instance);
    Labeling labeling = result.getLabeling();

    assertEquals(2, labeling.numLocations());
    assertEquals(1.0, labeling.value(0) + labeling.value(1), 1e-6);
}
@Test
public void testLabelLogLikelihoodWithMultiLabelInstance() {
    Alphabet dataAlphabet = new Alphabet();
    dataAlphabet.lookupIndex("f");

    Alphabet labelAlphabet = new Alphabet();
    labelAlphabet.lookupIndex("x");
    labelAlphabet.lookupIndex("y");

    Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
        @Override
        public Instance pipe(Instance inst) {
            return inst;
        }
    };

    double[] priorProbs = new double[]{0.5, 0.5};
    Multinomial prior = new Multinomial(labelAlphabet, priorProbs);

    double[] condX = new double[]{0.7};
    double[] condY = new double[]{0.3};

    Multinomial[] conds = new Multinomial[2];
    conds[0] = new Multinomial(dataAlphabet, condX);
    conds[1] = new Multinomial(dataAlphabet, condY);

    NaiveBayes nb = new NaiveBayes(pipe, prior, conds);

    FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
    double[] labelProbs = new double[]{0.2, 0.8}; 
    Labeling label = new LabelVector(labelAlphabet, labelProbs);

    Instance instance = new Instance(fv, label, "soft", null);

    InstanceList list = new InstanceList(pipe);
    list.add(instance);

    double logLikelihood = nb.labelLogLikelihood(list);
    assertTrue(Double.isFinite(logLikelihood));
}
@Test
public void testClassificationWhenPriorIsUniformAndConditionalsEqual() {
    Alphabet dataAlphabet = new Alphabet();
    dataAlphabet.lookupIndex("input");

    Alphabet labelAlphabet = new Alphabet();
    labelAlphabet.lookupIndex("label1");
    labelAlphabet.lookupIndex("label2");

    Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
        @Override
        public Instance pipe(Instance i) {
            return i;
        }
    };

    double[] uniformPrior = new double[]{0.5, 0.5};
    Multinomial prior = new Multinomial(labelAlphabet, uniformPrior);

    double[] sameCond = new double[]{0.5};
    Multinomial[] conds = new Multinomial[2];
    conds[0] = new Multinomial(dataAlphabet, sameCond);
    conds[1] = new Multinomial(dataAlphabet, sameCond);

    NaiveBayes naiveBayes = new NaiveBayes(pipe, prior, conds);

    FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
    Instance instance = new Instance(fv, null, "equal-weights", null);
    Classification classification = naiveBayes.classify(instance);

    assertEquals(2, classification.getLabeling().numLocations());
    assertEquals(0.5, classification.getLabeling().value(0), 1e-6);
    assertEquals(0.5, classification.getLabeling().value(1), 1e-6);
}
@Test
public void testLabelLogLikelihoodReturnsNegativeValue() {
    Alphabet dataAlphabet = new Alphabet();
    dataAlphabet.lookupIndex("f");

    Alphabet labelAlphabet = new Alphabet();
    labelAlphabet.lookupIndex("A");
    labelAlphabet.lookupIndex("B");

    Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {
        @Override
        public Instance pipe(Instance i) {
            return i;
        }
    };

    Multinomial prior = new Multinomial(labelAlphabet, new double[]{0.5, 0.5});
    Multinomial[] conditionals = new Multinomial[2];
    conditionals[0] = new Multinomial(dataAlphabet, new double[]{0.7});
    conditionals[1] = new Multinomial(dataAlphabet, new double[]{0.3});

    NaiveBayes nb = new NaiveBayes(pipe, prior, conditionals);

    FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
    double[] trueLabels = new double[]{1.0, 0.0};
    Labeling labeling = new LabelVector(labelAlphabet, trueLabels);
    Instance instance = new Instance(fv, labeling, "neg-log-likelihood", null);
    InstanceList list = new InstanceList(pipe);
    list.add(instance);

    double result = nb.labelLogLikelihood(list);
    assertTrue(result < 0);
}
@Test
public void testDataLogLikelihoodWithUnseenFeatureIndexIgnored() {
    Alphabet trainAlphabet = new Alphabet();
    trainAlphabet.lookupIndex("train-only");

    Alphabet labelAlphabet = new Alphabet();
    labelAlphabet.lookupIndex("C1");

    Pipe pipe = new Pipe(trainAlphabet, labelAlphabet) {
        @Override
        public Instance pipe(Instance i) {
            return i;
        }
    };

    Multinomial prior = new Multinomial(labelAlphabet, new double[]{1.0});
    Multinomial[] p = new Multinomial[1];
    p[0] = new Multinomial(trainAlphabet, new double[]{1.0});

    NaiveBayes classifier = new NaiveBayes(pipe, prior, p);

    Alphabet testAlphabet = new Alphabet();
    int unseenFeature = testAlphabet.lookupIndex("unseen");

    FeatureVector fv = new FeatureVector(testAlphabet, new int[]{unseenFeature}, new double[]{3.0});
    Instance instance = new Instance(fv, null, "unseen-only", null);

    pipe.setDataAlphabet(testAlphabet);

    InstanceList list = new InstanceList(pipe);
    list.add(instance);

    double result = classifier.dataLogLikelihood(list);
    assertTrue(Double.isFinite(result));
}
@Test
public void testClassificationWithFeatureIndexGreaterThanConditionalSize() {
    Alphabet featureAlphabet = new Alphabet();
    featureAlphabet.lookupIndex("f0");

    Alphabet labelAlphabet = new Alphabet();
    labelAlphabet.lookupIndex("L1");
    labelAlphabet.lookupIndex("L2");

    Pipe pipe = new Pipe(featureAlphabet, labelAlphabet) {
        @Override
        public Instance pipe(Instance inst) {
            return inst;
        }
    };

    double[] priorProbs = new double[]{0.5, 0.5};
    Multinomial[] conds = new Multinomial[2];
    conds[0] = new Multinomial(featureAlphabet, new double[]{0.6});
    conds[1] = new Multinomial(featureAlphabet, new double[]{0.4});

    for (int i = 0; i < 10; i++) {
        featureAlphabet.lookupIndex("f" + (i + 1));
    }

    FeatureVector fv = new FeatureVector(featureAlphabet, new int[]{9}, new double[]{1.0});

    Multinomial prior = new Multinomial(labelAlphabet, priorProbs);
    NaiveBayes nb = new NaiveBayes(pipe, prior, conds);

    Instance inst = new Instance(fv, null, "out-of-bounds-feature", null);
    Classification result = nb.classify(inst);
    Labeling labeling = result.getLabeling();

    assertEquals(2, labeling.numLocations());
    assertEquals(1.0, labeling.value(0) + labeling.value(1), 1e-6);
}
@Test
public void testClassificationLabelAlphabetIsLargerThanPriorArray() {
    Alphabet featureAlphabet = new Alphabet();
    featureAlphabet.lookupIndex("term1");

    Alphabet labelAlphabet = new Alphabet();
    labelAlphabet.lookupIndex("a");
    labelAlphabet.lookupIndex("b");
    labelAlphabet.lookupIndex("c");

    Pipe pipe = new Pipe(featureAlphabet, labelAlphabet) {
        @Override
        public Instance pipe(Instance i) {
            return i;
        }
    };

    double[] priorProbs = new double[]{0.7, 0.3}; 
    Multinomial prior = new Multinomial(labelAlphabet, priorProbs);

    Multinomial[] p = new Multinomial[2];
    p[0] = new Multinomial(featureAlphabet, new double[]{1.0});
    p[1] = new Multinomial(featureAlphabet, new double[]{1.0});

    NaiveBayes nb = new NaiveBayes(pipe, prior, p);

    FeatureVector fv = new FeatureVector(featureAlphabet, new int[]{0}, new double[]{1.0});
    Instance inst = new Instance(fv, null, "labelOverflow", null);
    Classification cls = nb.classify(inst);

    assertEquals(3, cls.getLabeling().numLocations());
    assertEquals(1.0, cls.getLabeling().value(0) + cls.getLabeling().value(1) + cls.getLabeling().value(2), 1e-6);
} 
}