public class GISTrainer_wosr_4_GPTLLMTest { 

 @Test
  public void testIsSortAndMerge() {
    GISTrainer trainer = new GISTrainer();
    Assert.assertTrue(trainer.isSortAndMerge());
  }
@Test
  public void testSetSmoothingFlagEffect() {
    GISTrainer trainer = new GISTrainer();
    trainer.setSmoothing(true);
    ObjectStream<Event> stream = new ObjectStream<Event>() {
      private boolean used = false;

      @Override
      public Event read() {
        if (used) return null;
        used = true;
        return new Event("A", new String[]{"feature1"});
      }

      @Override
      public void reset() { used = false; }

      @Override
      public void close() {}
    };

    try {
      MaxentModel model = trainer.trainModel(stream);
      Assert.assertNotNull(model);
    } catch (IOException e) {
      Assert.fail("IOException thrown: " + e.getMessage());
    }
  }
@Test
  public void testSetGaussianSigmaFlagEffect() {
    GISTrainer trainer = new GISTrainer();
    trainer.setGaussianSigma(1.5);
    ObjectStream<Event> stream = new ObjectStream<Event>() {
      private boolean used = false;

      @Override
      public Event read() {
        if (used) return null;
        used = true;
        return new Event("B", new String[]{"feature1"});
      }

      @Override
      public void reset() { used = false; }

      @Override
      public void close() {}
    };

    try {
      MaxentModel model = trainer.trainModel(stream);
      Assert.assertNotNull(model);
    } catch (IOException e) {
      Assert.fail("IOException thrown: " + e.getMessage());
    }
  }
@Test
  public void testInitWithSimpleSmoothing() {
    TrainingParameters params = new TrainingParameters();
    params.put("AlgorithmName", "MAXENT");
    params.put("Smoothing", "true");
    params.put("SmoothingObservation", "0.2");

    GISTrainer trainer = new GISTrainer();

    Map<String, String> reportMap = new HashMap<String, String>();
    trainer.init(params, reportMap);

    ObjectStream<Event> stream = new ObjectStream<Event>() {
      private boolean used = false;

      @Override
      public Event read() {
        if (used) return null;
        used = true;
        return new Event("C", new String[]{"feat"});
      }

      @Override
      public void reset() { used = false; }

      @Override
      public void close() {}
    };

    try {
      MaxentModel model = trainer.trainModel(stream);
      Assert.assertNotNull(model);
    } catch (IOException e) {
      Assert.fail("IOException thrown: " + e.getMessage());
    }
  }
@Test(expected = RuntimeException.class)
  public void testInitWithConflictingSmoothingParams() {
    TrainingParameters params = new TrainingParameters();
    params.put("Smoothing", "true");
    params.put("GaussianSmoothing", "true");

    GISTrainer trainer = new GISTrainer();
    Map<String, String> reportMap = new HashMap<String, String>();
    trainer.init(params, reportMap);
  }
@Test
  public void testTrainModelWithThreads() {
    GISTrainer trainer = new GISTrainer();
    ObjectStream<Event> stream = new ObjectStream<Event>() {
      private boolean used = false;

      @Override
      public Event read() {
        if (used) return null;
        used = true;
        return new Event("D", new String[]{"threadedFeature"});
      }

      @Override
      public void reset() { used = false; }

      @Override
      public void close() {}
    };

    try {
      TrainingParameters trainParams = new TrainingParameters();
      trainParams.put(TrainingParameters.ITERATIONS_PARAM, "5");
      trainParams.put(TrainingParameters.THREADS_PARAM, "2");

      trainer.init(trainParams, new HashMap<String, String>());
      MaxentModel model = trainer.doTrain(new OnePassDataIndexer() {{
        try {
          init(new TrainingParameters(), new HashMap<String, String>());
          index(stream);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }});
      Assert.assertNotNull(model);
    } catch (IOException e) {
      Assert.fail("IOException thrown: " + e.getMessage());
    }
  }
@Test(expected = IllegalArgumentException.class)
  public void testTrainModelWithInvalidThreadCount() {
    GISTrainer trainer = new GISTrainer();
    trainer.trainModel(10, new OnePassDataIndexer(), new UniformPrior(), 0);
  }
@Test
  public void testTrainModelMinimalUseCase() {
    GISTrainer trainer = new GISTrainer();
    ObjectStream<Event> stream = new ObjectStream<Event>() {
      private boolean used = false;

      @Override
      public Event read() {
        if (used) return null;
        used = true;
        return new Event("Label", new String[]{"f1"});
      }

      @Override
      public void reset() { used = false; }

      @Override
      public void close() {}
    };

    try {
      MaxentModel model = trainer.trainModel(stream, 3, 0);
      Assert.assertNotNull(model);
      Assert.assertTrue(model.getNumOutcomes() > 0);
    } catch (IOException e) {
      Assert.fail("Exception during trainModel: " + e.getMessage());
    }
  }
@Test
  public void testTrainModelWithCustomPrior() {
    GISTrainer trainer = new GISTrainer();
    ObjectStream<Event> stream = new ObjectStream<Event>() {
      private boolean used = false;

      @Override
      public Event read() {
        if (used) return null;
        used = true;
        return new Event("L", new String[]{"t"});
      }

      @Override
      public void reset() { used = false; }

      @Override
      public void close() {}
    };

    Prior prior = new UniformPrior();
    try {
      OnePassDataIndexer dataIndexer = new OnePassDataIndexer();
      TrainingParameters indexParams = new TrainingParameters();
      indexParams.put(TrainingParameters.CUTOFF_PARAM, "1");
      indexParams.put(TrainingParameters.ITERATIONS_PARAM, "3");
      Map<String, String> reportMap = new HashMap<String, String>();
      dataIndexer.init(indexParams, reportMap);
      dataIndexer.index(stream);

      MaxentModel model = trainer.trainModel(3, dataIndexer, prior, 1);
      Assert.assertNotNull(model);
    } catch (IOException e) {
      Assert.fail("IOException: " + e.getMessage());
    }
  }
@Test
  public void testSetSmoothingObservation() {
    GISTrainer trainer = new GISTrainer();
    trainer.setSmoothing(true);
    trainer.setSmoothingObservation(0.5);

    ObjectStream<Event> stream = new ObjectStream<Event>() {
      private boolean used = false;

      @Override
      public Event read() {
        if (used) return null;
        used = true;
        return new Event("YES", new String[]{"x"});
      }

      @Override
      public void reset() { used = false; }

      @Override
      public void close() {}
    };

    try {
      MaxentModel model = trainer.trainModel(stream, 5, 0);
      Assert.assertNotNull(model);
    } catch (IOException e) {
      Assert.fail("IOException: " + e.getMessage());
    }
  }
@Test
  public void testTrainModelMultipleFeatures() {
    GISTrainer trainer = new GISTrainer();
    ObjectStream<Event> stream = new ObjectStream<Event>() {
      private boolean used = false;

      @Override
      public Event read() {
        if (used) return null;
        used = true;
        return new Event("yes", new String[]{"feat1", "feat2", "feat3"});
      }

      @Override
      public void reset() { used = false; }

      @Override
      public void close() {}
    };

    try {
      MaxentModel model = trainer.trainModel(stream, 10, 0);
      Assert.assertNotNull(model);
      Assert.assertTrue(model.getNumOutcomes() > 0);
    } catch (IOException e) {
      Assert.fail("IOException: " + e.getMessage());
    }
  } 
}