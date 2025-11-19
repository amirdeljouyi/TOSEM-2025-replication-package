public class SumLatticeBeam_wosr_3_GPTLLMTest { 

 @Test
    public void testSetGetBeamWidth() {
        SumLatticeBeam lattice = new SumLatticeBeam(createDummyTransducer(), createInputSequence(), null, null);
        lattice.setBeamWidth(5);
        Assert.assertEquals(5, lattice.getBeamWidth());
    }
@Test
    public void testSetGetKLeps() {
        SumLatticeBeam lattice = new SumLatticeBeam(createDummyTransducer(), createInputSequence(), null, null);
        lattice.setKLeps(0.01);
        Assert.assertEquals(0.01, getPrivateFieldDouble(lattice, "KLeps"), 1e-9);
    }
@Test
    public void testSetGetRmin() {
        SumLatticeBeam lattice = new SumLatticeBeam(createDummyTransducer(), createInputSequence(), null, null);
        lattice.setRmin(0.5);
        Assert.assertEquals(0.5, getPrivateFieldDouble(lattice, "Rmin"), 1e-9);
    }
@Test
    public void testCurIterIncrementTctIter() {
        SumLatticeBeam lattice = new SumLatticeBeam(createDummyTransducer(), createInputSequence(), null, null);

        lattice.setCurIter(10);
        Assert.assertEquals(0, lattice.getTctIter());

        lattice.incIter();
        Assert.assertEquals(1, lattice.getTctIter());

        lattice.incIter();
        Assert.assertEquals(2, lattice.getTctIter());
    }
@Test
    public void testUseForwardBackwardBeamFlag() {
        SumLatticeBeam lattice = new SumLatticeBeam(createDummyTransducer(), createInputSequence(), null, null);

        lattice.setUseForwardBackwardBeam(true);
        Assert.assertTrue(lattice.getUseForwardBackwardBeam());
    }
@Test
    public void testInputSequenceReference() {
        Sequence input = createInputSequence();
        SumLatticeBeam lattice = new SumLatticeBeam(createDummyTransducer(), input, null, null);
        Assert.assertEquals(input, lattice.getInput());
    }
@Test
    public void testGammasNotNullAfterInit() {
        SumLatticeBeam lattice = new SumLatticeBeam(createDummyTransducer(), createInputSequence(), null, null);
        double[][] gammas = lattice.getGammas();
        Assert.assertNotNull(gammas);
        Assert.assertTrue(gammas.length > 0);
    }
@Test
    public void testGetTotalWeight() {
        SumLatticeBeam lattice = new SumLatticeBeam(createDummyTransducer(), createInputSequence(), null, null);
        double totalWeight = lattice.getTotalWeight();
        Assert.assertTrue(totalWeight <= 0.0); 
        Assert.assertFalse(Double.isNaN(totalWeight));
    }
@Test
    public void testGetGammaProbabilityNonNegative() {
        DummyState stateA = new DummyState("A", 0);
        DummyTransducer trans = createDummyTransducer();
        LabelAlphabet alphabet = new LabelAlphabet();
        SumLatticeBeam lattice = new SumLatticeBeam(trans, createInputSequence(), null, null, true, alphabet);
        double prob = lattice.getGammaProbability(0, stateA);
        Assert.assertTrue(prob >= 0.0);
    }
@Test(expected = IllegalStateException.class)
    public void testGetXiThrowsWithoutSavingXis() {
        DummyState stateA = new DummyState("A", 0);
        DummyState stateB = new DummyState("B", 1);
        SumLatticeBeam lattice = new SumLatticeBeam(createDummyTransducer(), createInputSequence(), null, null, false, null);
        lattice.getXiProbability(0, stateA, stateB);
    }
@Test
    public void testGetXiReturnsValueWhenSaved() {
        DummyState stateA = new DummyState("A", 0);
        DummyState stateB = new DummyState("B", 1);
        DummyTransducer trans = createDummyTransducer();
        SumLatticeBeam lattice = new SumLatticeBeam(trans, createInputSequence(), null, null, true, null);
        double val = lattice.getXiProbability(0, stateA, stateB);
        Assert.assertTrue(val >= 0.0);
    }
@Test
    public void testFactoryCreatesBeamLatticeWithSpecifiedWidth() {
        DummyTransducer t = createDummyTransducer();
        TokenSequence input = createInputSequence();
        LabelAlphabet alphabet = new LabelAlphabet();
        SumLatticeBeam.Factory factory = new SumLatticeBeam.Factory(7);
        SumLattice lattice = factory.newSumLattice(t, input, null, null, false, alphabet);
        Assert.assertTrue(lattice instanceof SumLatticeBeam);
        Assert.assertEquals(7, ((SumLatticeBeam) lattice).getBeamWidth());
    }
@Test
    public void testLengthEqualsInputSequencePlusOne() {
        TokenSequence input = createInputSequence();
        SumLatticeBeam lattice = new SumLatticeBeam(createDummyTransducer(), input, null, null);
        Assert.assertEquals(input.size() + 1, lattice.length());
    } 
}