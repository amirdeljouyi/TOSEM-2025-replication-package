public class POSBaselineLearner_4_GPTLLMTest { 

 @Test
    public void testConstructorInitializesEmptyLearner() {
        POSBaselineLearner learner = new POSBaselineLearner();
        assertNotNull(learner);
        assertFalse(learner.observed("test"));
        assertEquals(0, learner.observedCount("test"));
    }
@Test
    public void testLearnSingleEntryAndObservedCount() {
        POSBaselineLearner learner = new POSBaselineLearner("test");

        Object example = mock(Object.class);
        Learner extractor = mock(Learner.class);
        Learner labeler = mock(Learner.class);

        when(extractor.discreteValue(example)).thenReturn("apple");
        when(labeler.discreteValue(example)).thenReturn("NN");

        learner.extractor = extractor;
        learner.labeler = labeler;

        learner.learn(example);

        assertTrue(learner.observed("apple"));
        assertEquals(1, learner.observedCount("apple"));
    }
@Test
    public void testLearnMultipleEntriesSameFormDifferentLabels() {
        POSBaselineLearner learner = new POSBaselineLearner("test");

        Object ex1 = mock(Object.class);
        Object ex2 = mock(Object.class);

        Learner extractor = mock(Learner.class);
        Learner labeler = mock(Learner.class);

        when(extractor.discreteValue(ex1)).thenReturn("bank");
        when(labeler.discreteValue(ex1)).thenReturn("NN");

        when(extractor.discreteValue(ex2)).thenReturn("bank");
        when(labeler.discreteValue(ex2)).thenReturn("VB");

        learner.extractor = extractor;
        learner.labeler = labeler;

        learner.learn(ex1);
        learner.learn(ex2);

        assertEquals(2, learner.observedCount("bank"));
        Set<String> tags = learner.allowableTags("bank");

        assertTrue(tags.contains("NN"));
        assertTrue(tags.contains("VB"));
        assertEquals(2, tags.size());
    }
@Test
    public void testForgetClearsLearner() {
        POSBaselineLearner learner = new POSBaselineLearner("test");

        Object example = mock(Object.class);
        Learner extractor = mock(Learner.class);
        Learner labeler = mock(Learner.class);

        when(extractor.discreteValue(example)).thenReturn("run");
        when(labeler.discreteValue(example)).thenReturn("VB");

        learner.extractor = extractor;
        learner.labeler = labeler;

        learner.learn(example);
        assertTrue(learner.observed("run"));
        learner.forget();
        assertFalse(learner.observed("run"));
        assertEquals(0, learner.observedCount("run"));
    }
@Test
    public void testComputePredictionKnownWord() {
        POSBaselineLearner learner = new POSBaselineLearner("test");

        Object example = mock(Object.class);
        Learner extractor = mock(Learner.class);
        Learner labeler = mock(Learner.class);

        when(extractor.discreteValue(example)).thenReturn("dog");
        when(labeler.discreteValue(example)).thenReturn("NN");

        learner.extractor = extractor;
        learner.labeler = labeler;

        learner.learn(example);

        String predicted = learner.discreteValue(example);
        assertEquals("NN", predicted);
    }
@Test
    public void testComputePredictionUnknownSemicolon() {
        POSBaselineLearner learner = new POSBaselineLearner("test");

        Object example = mock(Object.class);
        Learner extractor = mock(Learner.class);
        Learner labeler = mock(Learner.class);

        when(extractor.discreteValue(example)).thenReturn(";");
        when(labeler.discreteValue(example)).thenReturn(null);

        learner.extractor = extractor;
        learner.labeler = labeler;

        String predicted = learner.discreteValue(example);
        assertEquals(":", predicted);
    }
@Test
    public void testComputePredictionUnknownNumberFormat() {
        POSBaselineLearner learner = new POSBaselineLearner("test");

        Object example = mock(Object.class);
        Learner extractor = mock(Learner.class);
        Learner labeler = mock(Learner.class);

        when(extractor.discreteValue(example)).thenReturn("123.45");
        when(labeler.discreteValue(example)).thenReturn(null);

        learner.extractor = extractor;
        learner.labeler = labeler;

        String predicted = learner.discreteValue(example);
        assertEquals("CD", predicted);
    }
@Test
    public void testComputePredictionUnknownWord() {
        POSBaselineLearner learner = new POSBaselineLearner("test");

        Object example = mock(Object.class);
        Learner extractor = mock(Learner.class);
        Learner labeler = mock(Learner.class);

        when(extractor.discreteValue(example)).thenReturn("foobarbaz");
        when(labeler.discreteValue(example)).thenReturn(null);

        learner.extractor = extractor;
        learner.labeler = labeler;

        String predicted = learner.discreteValue(example);
        assertEquals("UNKNOWN", predicted);
    }
@Test
    public void testFeatureValueReturnsCorrectFeature() {
        POSBaselineLearner learner = new POSBaselineLearner("test");

        Object example = mock(Object.class);
        Learner extractor = mock(Learner.class);
        Learner labeler = mock(Learner.class);

        when(extractor.discreteValue(example)).thenReturn("tree");
        when(labeler.discreteValue(example)).thenReturn("NN");

        learner.extractor = extractor;
        learner.labeler = labeler;

        learner.learn(example);

        Feature f = learner.featureValue(example);
        assertTrue(f instanceof DiscretePrimitiveStringFeature);
        assertEquals("NN", ((DiscretePrimitiveStringFeature) f).getStringValue());
    }
@Test
    public void testClassifyReturnsFeatureVector() {
        POSBaselineLearner learner = new POSBaselineLearner("test");

        Object example = mock(Object.class);
        Learner extractor = mock(Learner.class);
        Learner labeler = mock(Learner.class);

        when(extractor.discreteValue(example)).thenReturn("table");
        when(labeler.discreteValue(example)).thenReturn("NN");

        learner.extractor = extractor;
        learner.labeler = labeler;

        learner.learn(example);

        FeatureVector fv = learner.classify(example);

        assertEquals(1, fv.featuresSize());
        Feature f = fv.getFeature(0);
        assertTrue(f instanceof DiscretePrimitiveStringFeature);
        assertEquals("NN", ((DiscretePrimitiveStringFeature) f).getStringValue());
    }
@Test
    public void testAllowableTagsForKnownNumberFormat() {
        POSBaselineLearner learner = new POSBaselineLearner("test");

        Set<String> tags = learner.allowableTags("36,000.55");

        assertEquals(1, tags.size());
        assertTrue(tags.contains("CD"));
    }
@Test
    public void testWriteTextStreamContentIncludesTag() {
        POSBaselineLearner learner = new POSBaselineLearner("test");

        Object example = mock(Object.class);
        Learner extractor = mock(Learner.class);
        Learner labeler = mock(Learner.class);

        when(extractor.discreteValue(example)).thenReturn("load");
        when(labeler.discreteValue(example)).thenReturn("VB");

        learner.extractor = extractor;
        learner.labeler = labeler;

        learner.learn(example);

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(buffer);

        learner.write(ps);

        String output = buffer.toString();
        assertTrue(output.contains("load"));
        assertTrue(output.contains("VB(1)"));
    }
@Test
    public void testBinaryReadWriteRoundTripMaintainsState() {
        POSBaselineLearner learner = new POSBaselineLearner("test");

        Object example = mock(Object.class);
        Learner extractor = mock(Learner.class);
        Learner labeler = mock(Learner.class);

        when(extractor.discreteValue(example)).thenReturn("sing");
        when(labeler.discreteValue(example)).thenReturn("VB");

        learner.extractor = extractor;
        learner.labeler = labeler;

        learner.learn(example);

        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ExceptionlessOutputStream exOut = new ExceptionlessOutputStream(byteOut);
        learner.write(exOut);
        exOut.close();

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ExceptionlessInputStream exIn = new ExceptionlessInputStream(byteIn);

        POSBaselineLearner loadedLearner = new POSBaselineLearner("loaded");
        loadedLearner.read(exIn);

        Object testExample = mock(Object.class);
        Learner testExtractor = mock(Learner.class);
        Learner testLabeler = mock(Learner.class);

        when(testExtractor.discreteValue(testExample)).thenReturn("sing");
        when(testLabeler.discreteValue(testExample)).thenReturn("VB");

        loadedLearner.extractor = testExtractor;
        loadedLearner.labeler = testLabeler;

        String predicted = loadedLearner.discreteValue(testExample);
        assertEquals("VB", predicted);
    }
@Test
    public void testEmptyCloneReturnsFreshInstance() {
        POSBaselineLearner learner = new POSBaselineLearner("origin");

        Object example = mock(Object.class);
        Learner extractor = mock(Learner.class);
        Learner labeler = mock(Learner.class);

        when(extractor.discreteValue(example)).thenReturn("jump");
        when(labeler.discreteValue(example)).thenReturn("VB");

        learner.extractor = extractor;
        learner.labeler = labeler;
        learner.learn(example);

        Learner cloned = learner.emptyClone();

        assertTrue(cloned instanceof POSBaselineLearner);
        POSBaselineLearner clonedBaseline = (POSBaselineLearner) cloned;

        Object testExample = mock(Object.class);
        Learner testExtractor = mock(Learner.class);
        Learner testLabeler = mock(Learner.class);

        when(testExtractor.discreteValue(testExample)).thenReturn("jump");
        when(testLabeler.discreteValue(testExample)).thenReturn(null);

        clonedBaseline.extractor = testExtractor;
        clonedBaseline.labeler = testLabeler;

        assertEquals("UNKNOWN", clonedBaseline.discreteValue(testExample));
    }
@Test
    public void testLooksLikeNumberSingleDigitOnly() {
        assertTrue(POSBaselineLearner.looksLikeNumber("7"));
    }
@Test
    public void testLooksLikeNumberWithComma() {
        assertTrue(POSBaselineLearner.looksLikeNumber("1,000"));
    }
@Test
    public void testLooksLikeNumberReturnsFalseForAlphabetic() {
        assertFalse(POSBaselineLearner.looksLikeNumber("abc123"));
    }
@Test
    public void testGetInputTypeStringConstant() {
        POSBaselineLearner learner = new POSBaselineLearner("test");
        String type = learner.getInputType();
        assertEquals("edu.illinois.cs.cogcomp.lbjava.nlp.Word", type);
    }
@Test
    public void testScoresMethodReturnsNullAsExpected() {
        POSBaselineLearner learner = new POSBaselineLearner("test");
        ScoreSet scores = learner.scores(new int[0], new double[0]);
        assertNull(scores);
    }
@Test
    public void testLearnWithNullLabelAddsDefaultCount() {
        POSBaselineLearner learner = new POSBaselineLearner("nullLabel");

        Object example = mock(Object.class);
        Learner extractor = mock(Learner.class);
        Learner labeler = mock(Learner.class);

        when(extractor.discreteValue(example)).thenReturn("unknownword");
        when(labeler.discreteValue(example)).thenReturn(null);

        learner.extractor = extractor;
        learner.labeler = labeler;

        learner.learn(example);

        assertTrue(learner.observed("unknownword"));
        Set<String> tags = learner.allowableTags("unknownword");
        assertTrue(tags.contains(null));
        assertEquals(1, learner.observedCount("unknownword"));
    }
@Test
    public void testAllowableTagsReturnsEmptyForNonNumberNonPunctuation() {
        POSBaselineLearner learner = new POSBaselineLearner("test");
        Set<String> tags = learner.allowableTags("abcd$%^");
        assertNotNull(tags);
        assertTrue(tags.isEmpty());
    }
@Test
    public void testLooksLikeNumberOnlyPunctuationNoDigits() {
        boolean result = POSBaselineLearner.looksLikeNumber("..,,--");
        assertFalse(result);
    }
@Test
    public void testWritePrintStreamHandlesEmptyTable() {
        POSBaselineLearner learner = new POSBaselineLearner("empty");

        ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(outBuffer);

        learner.write(ps);

        String output = outBuffer.toString();
        assertEquals("", output.trim());
    }
@Test
    public void testWriteStaticHandlesMultipleTagsSortedCorrectly() {
        POSBaselineLearner learner = new POSBaselineLearner("manualInsert");

        Object example1 = mock(Object.class);
        Object example2 = mock(Object.class);
        Object example3 = mock(Object.class);

        Learner extractor = mock(Learner.class);
        Learner labeler = mock(Learner.class);

        when(extractor.discreteValue(example1)).thenReturn("run");
        when(extractor.discreteValue(example2)).thenReturn("run");
        when(extractor.discreteValue(example3)).thenReturn("run");

        when(labeler.discreteValue(example1)).thenReturn("VB");
        when(labeler.discreteValue(example2)).thenReturn("NN");
        when(labeler.discreteValue(example3)).thenReturn("VB");

        learner.extractor = extractor;
        learner.labeler = labeler;

        learner.learn(example1);
        learner.learn(example2);
        learner.learn(example3);

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream stream = new PrintStream(buffer);

        learner.write(stream);

        String result = buffer.toString();
        int vbIndex = result.indexOf("VB(2)");
        int nnIndex = result.indexOf("NN(1)");
        assertTrue(vbIndex < nnIndex);
    }
@Test
    public void testObservedReturnsFalseForWordNeverAdded() {
        POSBaselineLearner learner = new POSBaselineLearner("notAdded");
        assertFalse(learner.observed("ghost"));
    }
@Test
    public void testObservedCountReturnsZeroForUnknownWord() {
        POSBaselineLearner learner = new POSBaselineLearner("notCounted");
        assertEquals(0, learner.observedCount("ghost"));
    }
@Test
    public void testFeatureValueForUnseenNumberWordReturnsCD() {
        POSBaselineLearner learner = new POSBaselineLearner("number");

        Object example = mock(Object.class);
        Learner extractor = mock(Learner.class);
        when(extractor.discreteValue(example)).thenReturn("1,000.00");

        learner.extractor = extractor;

        Feature f = learner.featureValue(example);
        assertTrue(f instanceof DiscretePrimitiveStringFeature);
        assertEquals("CD", ((DiscretePrimitiveStringFeature) f).getStringValue());
    }
@Test
    public void testFeatureValueForSemicolonReturnsColon() {
        POSBaselineLearner learner = new POSBaselineLearner("semi");

        Object example = mock(Object.class);
        Learner extractor = mock(Learner.class);
        when(extractor.discreteValue(example)).thenReturn(";");

        learner.extractor = extractor;

        Feature f = learner.featureValue(example);
        assertTrue(f instanceof DiscretePrimitiveStringFeature);
        assertEquals(":", ((DiscretePrimitiveStringFeature) f).getStringValue());
    }
@Test
    public void testComputePredictionSelectsMostFrequentTag() {
        POSBaselineLearner learner = new POSBaselineLearner("frequency");

        Object ex1 = mock(Object.class);
        Object ex2 = mock(Object.class);
        Object ex3 = mock(Object.class);

        Learner extractor = mock(Learner.class);
        Learner labeler = mock(Learner.class);

        when(extractor.discreteValue(ex1)).thenReturn("testword");
        when(labeler.discreteValue(ex1)).thenReturn("A");

        when(extractor.discreteValue(ex2)).thenReturn("testword");
        when(labeler.discreteValue(ex2)).thenReturn("A");

        when(extractor.discreteValue(ex3)).thenReturn("testword");
        when(labeler.discreteValue(ex3)).thenReturn("B");

        learner.extractor = extractor;
        learner.labeler = labeler;

        learner.learn(ex1);
        learner.learn(ex2);
        learner.learn(ex3);

        String predicted = learner.discreteValue(ex1);
        assertEquals("A", predicted);
    }
@Test
    public void testClassifyReturnsFeatureVectorProducedFromUnknownWord() {
        POSBaselineLearner learner = new POSBaselineLearner("unknown");

        Object example = mock(Object.class);
        Learner extractor = mock(Learner.class);
        when(extractor.discreteValue(example)).thenReturn("qxzplmn");

        learner.extractor = extractor;

        FeatureVector fv = learner.classify(example);
        assertEquals(1, fv.featuresSize());
        assertEquals("UNKNOWN", ((DiscretePrimitiveStringFeature) fv.getFeature(0)).getStringValue());
    }
@Test
    public void testClassifyIntVersionWithNonEmptyInputReturnsEmptyVector() {
        POSBaselineLearner learner = new POSBaselineLearner("test");

        int[] features = new int[]{1, 2};
        double[] values = new double[]{0.5, 0.7};

        FeatureVector fv = learner.classify(features, values);
        assertNotNull(fv);
        assertEquals(0, fv.featuresSize());
    }
@Test
    public void testWriteBinaryWithEmptyTable() throws Exception {
        POSBaselineLearner learner = new POSBaselineLearner("binary");

        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ExceptionlessOutputStream eos = new ExceptionlessOutputStream(byteOut);

        learner.write(eos);
        eos.close();

        byte[] data = byteOut.toByteArray();
        assertNotNull(data);
        assertTrue(data.length > 0); 
    }
@Test
    public void testReadBinaryPopulatesEntriesCorrectly() {
        POSBaselineLearner learner = new POSBaselineLearner("rw");

        Object example = mock(Object.class);
        Learner extractor = mock(Learner.class);
        Learner labeler = mock(Learner.class);

        when(extractor.discreteValue(example)).thenReturn("go");
        when(labeler.discreteValue(example)).thenReturn("VB");

        learner.extractor = extractor;
        learner.labeler = labeler;

        learner.learn(example);

        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ExceptionlessOutputStream eos = new ExceptionlessOutputStream(byteOut);
        learner.write(eos);
        eos.close();

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ExceptionlessInputStream eis = new ExceptionlessInputStream(byteIn);

        POSBaselineLearner reloaded = new POSBaselineLearner("reloaded");
        reloaded.extractor = extractor;
        reloaded.labeler = labeler;

        reloaded.read(eis);

        assertTrue(reloaded.observed("go"));
        assertEquals(1, reloaded.observedCount("go"));
        Set<String> tags = reloaded.allowableTags("go");
        assertTrue(tags.contains("VB"));
    }
@Test
    public void testLearnSameWordSameLabelMultipleTimes() {
        POSBaselineLearner learner = new POSBaselineLearner("repeat");

        Object example1 = mock(Object.class);
        Object example2 = mock(Object.class);

        Learner extractor = mock(Learner.class);
        Learner labeler = mock(Learner.class);

        when(extractor.discreteValue(example1)).thenReturn("repeatWord");
        when(labeler.discreteValue(example1)).thenReturn("VB");

        when(extractor.discreteValue(example2)).thenReturn("repeatWord");
        when(labeler.discreteValue(example2)).thenReturn("VB");

        learner.extractor = extractor;
        learner.labeler = labeler;

        learner.learn(example1);
        learner.learn(example2);

        assertEquals(2, learner.observedCount("repeatWord"));
        Set<String> tags = learner.allowableTags("repeatWord");
        assertEquals(1, tags.size());
        assertTrue(tags.contains("VB"));
    }
@Test
    public void testLearnSameWordWithNullAndNonNullLabels() {
        POSBaselineLearner learner = new POSBaselineLearner("nullAndLiteral");

        Object example1 = mock(Object.class);
        Object example2 = mock(Object.class);

        Learner extractor = mock(Learner.class);
        Learner labeler = mock(Learner.class);

        when(extractor.discreteValue(example1)).thenReturn("mixedCase");
        when(labeler.discreteValue(example1)).thenReturn(null);

        when(extractor.discreteValue(example2)).thenReturn("mixedCase");
        when(labeler.discreteValue(example2)).thenReturn("NN");

        learner.extractor = extractor;
        learner.labeler = labeler;

        learner.learn(example1);
        learner.learn(example2);

        assertEquals(2, learner.observedCount("mixedCase"));
        Set<String> tags = learner.allowableTags("mixedCase");
        assertTrue(tags.contains(null));
        assertTrue(tags.contains("NN"));
    }
@Test
    public void testAllowableTagsCaseSensitivity() {
        POSBaselineLearner learner = new POSBaselineLearner("caseSensitivity");

        Object exLower = mock(Object.class);
        Object exUpper = mock(Object.class);

        Learner extractor = mock(Learner.class);
        Learner labeler = mock(Learner.class);

        when(extractor.discreteValue(exLower)).thenReturn("walk");
        when(labeler.discreteValue(exLower)).thenReturn("VB");

        when(extractor.discreteValue(exUpper)).thenReturn("Walk");
        when(labeler.discreteValue(exUpper)).thenReturn("NN");

        learner.extractor = extractor;
        learner.labeler = labeler;

        learner.learn(exLower);
        learner.learn(exUpper);

        Set<String> tagsLower = learner.allowableTags("walk");
        Set<String> tagsUpper = learner.allowableTags("Walk");

        assertEquals(1, tagsLower.size());
        assertTrue(tagsLower.contains("VB"));
        assertEquals(1, tagsUpper.size());
        assertTrue(tagsUpper.contains("NN"));
    }
@Test
    public void testLooksLikeNumberEdgeHyphenWithDigit() {
        boolean isNumber = POSBaselineLearner.looksLikeNumber("-5");
        assertTrue(isNumber);
    }
@Test
    public void testLooksLikeNumberHyphenWithoutDigitReturnsFalse() {
        boolean isNumber = POSBaselineLearner.looksLikeNumber("-");
        assertFalse(isNumber);
    }
@Test
    public void testEmptyCloneNameIsEmpty() {
        POSBaselineLearner learner = new POSBaselineLearner("nameful");
        POSBaselineLearner cloned = (POSBaselineLearner) learner.emptyClone();

        Object example = mock(Object.class);
        Learner extractor = mock(Learner.class);
        when(extractor.discreteValue(example)).thenReturn("zero");
        cloned.extractor = extractor;

        Feature f = cloned.featureValue(example);
        assertNotNull(f);
        assertEquals("UNKNOWN", ((DiscretePrimitiveStringFeature) f).getStringValue());
    }
@Test
    public void testWriteTextOutputSortsKeysAlphabetically() {
        POSBaselineLearner learner = new POSBaselineLearner("sortTest");

        Object exA = mock(Object.class);
        Object exZ = mock(Object.class);
        Object exM = mock(Object.class);

        Learner extractor = mock(Learner.class);
        Learner labeler = mock(Learner.class);

        when(extractor.discreteValue(exA)).thenReturn("apple");
        when(labeler.discreteValue(exA)).thenReturn("NN");

        when(extractor.discreteValue(exZ)).thenReturn("zebra");
        when(labeler.discreteValue(exZ)).thenReturn("NN");

        when(extractor.discreteValue(exM)).thenReturn("mango");
        when(labeler.discreteValue(exM)).thenReturn("NN");

        learner.extractor = extractor;
        learner.labeler = labeler;

        learner.learn(exZ);
        learner.learn(exM);
        learner.learn(exA);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(out);

        learner.write(ps);
        String result = out.toString();

        int indexA = result.indexOf("apple");
        int indexM = result.indexOf("mango");
        int indexZ = result.indexOf("zebra");

        assertTrue(indexA < indexM);
        assertTrue(indexM < indexZ);
    }
@Test
    public void testClassifyObjectReturnsFeatureVectorWithUnknownPrediction() {
        POSBaselineLearner learner = new POSBaselineLearner("basic");

        Object example = mock(Object.class);
        Learner extractor = mock(Learner.class);
        when(extractor.discreteValue(example)).thenReturn("abcdefghijk");

        learner.extractor = extractor;

        FeatureVector fv = learner.classify(example);
        assertNotNull(fv);
        assertEquals(1, fv.featuresSize());
        Feature f = fv.getFeature(0);
        assertTrue(f instanceof DiscretePrimitiveStringFeature);
        assertEquals("UNKNOWN", ((DiscretePrimitiveStringFeature) f).getStringValue());
    }
@Test
    public void testLearnEmptyStringAsWordStillAddsCount() {
        POSBaselineLearner learner = new POSBaselineLearner("blank");

        Object example = mock(Object.class);
        Learner extractor = mock(Learner.class);
        Learner labeler = mock(Learner.class);

        when(extractor.discreteValue(example)).thenReturn("");
        when(labeler.discreteValue(example)).thenReturn("NN");

        learner.extractor = extractor;
        learner.labeler = labeler;

        learner.learn(example);
        assertTrue(learner.observed(""));
        Set<String> tags = learner.allowableTags("");
        assertTrue(tags.contains("NN"));
    }
@Test
    public void testLearnWhitespaceWordStillAddsCount() {
        POSBaselineLearner learner = new POSBaselineLearner("space");

        Object example = mock(Object.class);
        Learner extractor = mock(Learner.class);
        Learner labeler = mock(Learner.class);

        when(extractor.discreteValue(example)).thenReturn(" ");
        when(labeler.discreteValue(example)).thenReturn("SP");

        learner.extractor = extractor;
        learner.labeler = labeler;

        learner.learn(example);
        assertTrue(learner.observed(" "));
        assertEquals(1, learner.observedCount(" "));
        Set<String> tags = learner.allowableTags(" ");
        assertTrue(tags.contains("SP"));
    }
@Test
    public void testReadBinaryIntoPreloadedLearnerOverwritesTable() {
        POSBaselineLearner original = new POSBaselineLearner("test");

        Object ex1 = mock(Object.class);
        Learner extractor1 = mock(Learner.class);
        Learner labeler1 = mock(Learner.class);

        when(extractor1.discreteValue(ex1)).thenReturn("jump");
        when(labeler1.discreteValue(ex1)).thenReturn("VB");

        original.extractor = extractor1;
        original.labeler = labeler1;
        original.learn(ex1);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ExceptionlessOutputStream eos = new ExceptionlessOutputStream(out);
        original.write(eos);
        eos.close();

        POSBaselineLearner target = new POSBaselineLearner("overwrite");
        Object old = mock(Object.class);
        Learner extractor2 = mock(Learner.class);
        Learner labeler2 = mock(Learner.class);
        when(extractor2.discreteValue(old)).thenReturn("oldword");
        when(labeler2.discreteValue(old)).thenReturn("OLD");
        target.extractor = extractor2;
        target.labeler = labeler2;
        target.learn(old);

        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        ExceptionlessInputStream eis = new ExceptionlessInputStream(in);
        target.read(eis);

        assertFalse(target.observed("oldword"));
        assertTrue(target.observed("jump"));
        assertEquals(1, target.observedCount("jump"));
    }
@Test
    public void testWriteBinaryHandlesEmptyTableCorrectly() throws IOException {
        POSBaselineLearner learner = new POSBaselineLearner("emptyBinary");

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ExceptionlessOutputStream eos = new ExceptionlessOutputStream(buffer);

        learner.write(eos);
        eos.close();

        byte[] data = buffer.toByteArray();
        assertTrue(data.length > 0);
    }
@Test
    public void testReadBinaryWithSingleEntryRestoresCorrectly() throws IOException {
        POSBaselineLearner learner = new POSBaselineLearner("writer");

        Object example = mock(Object.class);
        Learner extractor = mock(Learner.class);
        Learner labeler = mock(Learner.class);
        when(extractor.discreteValue(example)).thenReturn("restore");
        when(labeler.discreteValue(example)).thenReturn("VB");
        learner.extractor = extractor;
        learner.labeler = labeler;
        learner.learn(example);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ExceptionlessOutputStream eos = new ExceptionlessOutputStream(out);
        learner.write(eos);
        eos.close();

        POSBaselineLearner loaded = new POSBaselineLearner("reader");

        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        ExceptionlessInputStream eis = new ExceptionlessInputStream(in);
        loaded.read(eis);

        Object test = mock(Object.class);
        Learner newExtractor = mock(Learner.class);
        when(newExtractor.discreteValue(test)).thenReturn("restore");
        loaded.extractor = newExtractor;

        FeatureVector fv = loaded.classify(test);
        assertEquals("VB", ((DiscretePrimitiveStringFeature) fv.getFeature(0)).getStringValue());
    }
@Test
    public void testWriteStaticWithMultipleSameFreqTagsSortsLexically() {
        HashMap<String, TreeMap<String, Integer>> map = new HashMap<>();

        TreeMap<String, Integer> counts = new TreeMap<>();
        counts.put("JJ", 2);
        counts.put("CC", 2);
        counts.put("RB", 2);
        map.put("lexsort", counts);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(out);
        POSBaselineLearner.write(ps, map);

        String result = out.toString();
        int ccIndex = result.indexOf("CC(2)");
        int jjIndex = result.indexOf("JJ(2)");
        int rbIndex = result.indexOf("RB(2)");

        assertTrue(ccIndex < jjIndex || ccIndex < rbIndex); 
        assertTrue(result.contains("CC(2)"));
        assertTrue(result.contains("JJ(2)"));
        assertTrue(result.contains("RB(2)"));
    }
@Test
    public void testLooksLikeNumberWithLettersReturnsFalse() {
        boolean result = POSBaselineLearner.looksLikeNumber("12a45");
        assertFalse(result);
    }
@Test
    public void testLooksLikeNumberWithDotAndHyphenOnlyReturnsFalse() {
        boolean result = POSBaselineLearner.looksLikeNumber(".-");
        assertFalse(result);
    }
@Test
    public void testClassifyReturnsCorrectFeatureType() {
        POSBaselineLearner learner = new POSBaselineLearner("fvt");

        Object example = mock(Object.class);
        Learner extractor = mock(Learner.class);
        Learner labeler = mock(Learner.class);
        when(extractor.discreteValue(example)).thenReturn("sun");
        when(labeler.discreteValue(example)).thenReturn("NN");
        learner.extractor = extractor;
        learner.labeler = labeler;

        learner.learn(example);
        Feature f = learner.featureValue(example);

        assertTrue(f instanceof DiscretePrimitiveStringFeature);
        DiscretePrimitiveStringFeature dfsf = (DiscretePrimitiveStringFeature) f;
        assertEquals("NN", dfsf.getStringValue());
    }
@Test
    public void testReadBinaryOverwritesExistingState() throws IOException {
        POSBaselineLearner learner1 = new POSBaselineLearner("original");

        Object ex1 = mock(Object.class);
        Learner extractor1 = mock(Learner.class);
        Learner labeler1 = mock(Learner.class);
        when(extractor1.discreteValue(ex1)).thenReturn("alpha");
        when(labeler1.discreteValue(ex1)).thenReturn("NN");
        learner1.extractor = extractor1;
        learner1.labeler = labeler1;
        learner1.learn(ex1);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ExceptionlessOutputStream eos = new ExceptionlessOutputStream(out);
        learner1.write(eos);
        eos.close();

        POSBaselineLearner learner2 = new POSBaselineLearner("receiver");

        Object old = mock(Object.class);
        Learner extractor2 = mock(Learner.class);
        Learner labeler2 = mock(Learner.class);
        when(extractor2.discreteValue(old)).thenReturn("oldword");
        when(labeler2.discreteValue(old)).thenReturn("OLD");
        learner2.extractor = extractor2;
        learner2.labeler = labeler2;
        learner2.learn(old);
        assertTrue(learner2.observed("oldword"));

        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        ExceptionlessInputStream eis = new ExceptionlessInputStream(in);
        learner2.read(eis);

        assertFalse(learner2.observed("oldword"));
        assertTrue(learner2.observed("alpha"));
    }
@Test
    public void testAllowableTagsForMultipleTagsPreservesTagSet() {
        POSBaselineLearner learner = new POSBaselineLearner("tags");

        Object ex1 = mock(Object.class);
        Object ex2 = mock(Object.class);
        Object ex3 = mock(Object.class);

        Learner extractor = mock(Learner.class);
        Learner labeler = mock(Learner.class);

        when(extractor.discreteValue(ex1)).thenReturn("multi");
        when(labeler.discreteValue(ex1)).thenReturn("VB");

        when(extractor.discreteValue(ex2)).thenReturn("multi");
        when(labeler.discreteValue(ex2)).thenReturn("JJ");

        when(extractor.discreteValue(ex3)).thenReturn("multi");
        when(labeler.discreteValue(ex3)).thenReturn("NN");

        learner.extractor = extractor;
        learner.labeler = labeler;

        learner.learn(ex1);
        learner.learn(ex2);
        learner.learn(ex3);

        Set<String> tags = learner.allowableTags("multi");
        assertEquals(3, tags.size());
        assertTrue(tags.contains("VB"));
        assertTrue(tags.contains("JJ"));
        assertTrue(tags.contains("NN"));
    }
@Test
    public void testEmptyWordFormReturnsUnknownPrediction() {
        POSBaselineLearner learner = new POSBaselineLearner("emptyForm");

        Object example = mock(Object.class);
        Learner extractor = mock(Learner.class);
        when(extractor.discreteValue(example)).thenReturn("");
        learner.extractor = extractor;

        String prediction = learner.discreteValue(example);
        assertEquals("UNKNOWN", prediction);
    }
@Test
    public void testAllowableTagsWithFormEqualsSemicolonReturnsColon() {
        POSBaselineLearner learner = new POSBaselineLearner("semicolon");

        Set<String> tags = learner.allowableTags(";");
        assertEquals(1, tags.size());
        assertTrue(tags.contains(":"));
    }
@Test
    public void testLearnNullFormDoesNotThrow() {
        POSBaselineLearner learner = new POSBaselineLearner("nullForm");

        Object example = mock(Object.class);
        Learner extractor = mock(Learner.class);
        Learner labeler = mock(Learner.class);

        when(extractor.discreteValue(example)).thenReturn(null);
        when(labeler.discreteValue(example)).thenReturn("NN");

        learner.extractor = extractor;
        learner.labeler = labeler;

        try {
            learner.learn(example);
        } catch (Exception e) {
            fail("Learning with null form should not throw exception");
        }
        assertTrue(true);
    }
@Test
    public void testLearnNullFormAndNullLabelDoesNotThrow() {
        POSBaselineLearner learner = new POSBaselineLearner("nullAll");

        Object example = mock(Object.class);
        Learner extractor = mock(Learner.class);
        Learner labeler = mock(Learner.class);

        when(extractor.discreteValue(example)).thenReturn(null);
        when(labeler.discreteValue(example)).thenReturn(null);

        learner.extractor = extractor;
        learner.labeler = labeler;

        try {
            learner.learn(example);
        } catch (Exception e) {
            fail("Null form and null label should not throw exception");
        }
        assertTrue(true);
    }
@Test
    public void testLooksLikeNumberNullInputReturnsFalse() {
        try {
            POSBaselineLearner.looksLikeNumber(null);
            fail("Should throw NullPointerException for null input");
        } catch (NullPointerException e) {
            assertTrue(true);
        }
    }
@Test
    public void testEmptyFeatureValueFromEmptyInputString() {
        POSBaselineLearner learner = new POSBaselineLearner("emptyFeature");

        Object example = mock(Object.class);
        Learner extractor = mock(Learner.class);
        when(extractor.discreteValue(example)).thenReturn("");

        learner.extractor = extractor;

        Feature feature = learner.featureValue(example);
        assertNotNull(feature);
        DiscretePrimitiveStringFeature dp = (DiscretePrimitiveStringFeature) feature;
        assertEquals("UNKNOWN", dp.getStringValue());
    }
@Test
    public void testMultipleWordsWithSameBestScoreReturnsFirst() {
        POSBaselineLearner learner = new POSBaselineLearner("tieBreak");

        Object ex1 = mock(Object.class);
        Object ex2 = mock(Object.class);
        Object ex3 = mock(Object.class);

        Learner extractor = mock(Learner.class);
        Learner labeler = mock(Learner.class);

        when(extractor.discreteValue(ex1)).thenReturn("multi");
        when(labeler.discreteValue(ex1)).thenReturn("NN");

        when(extractor.discreteValue(ex2)).thenReturn("multi");
        when(labeler.discreteValue(ex2)).thenReturn("VB");

        when(extractor.discreteValue(ex3)).thenReturn("multi");
        when(labeler.discreteValue(ex3)).thenReturn("NN");

        learner.extractor = extractor;
        learner.labeler = labeler;

        learner.learn(ex1);
        learner.learn(ex2); 
        learner.learn(ex3); 

        Object test = mock(Object.class);
        when(extractor.discreteValue(test)).thenReturn("multi");
        learner.extractor = extractor;

        String predicted = learner.discreteValue(test);
        assertEquals("NN", predicted);
    }
@Test
    public void testWriteStaticHandlesIdenticalCountsWithStableOrder() {
        HashMap<String, TreeMap<String, Integer>> table = new HashMap<>();
        TreeMap<String, Integer> counts = new TreeMap<>();

        counts.put("Z", 1);
        counts.put("A", 1);
        counts.put("M", 1);
        table.put("token", counts);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(out);
        POSBaselineLearner.write(ps, table);

        String output = out.toString();
        assertTrue(output.contains("Z(1)"));
        assertTrue(output.contains("A(1)"));
        assertTrue(output.contains("M(1)"));
        assertTrue(output.contains("token:"));
    }
@Test
    public void testWriteBinaryWithMultipleEntriesWritesData() throws Exception {
        POSBaselineLearner learner = new POSBaselineLearner("multiBinary");

        Object ex1 = mock(Object.class);
        Object ex2 = mock(Object.class);
        Learner extractor = mock(Learner.class);
        Learner labeler = mock(Learner.class);

        when(extractor.discreteValue(ex1)).thenReturn("one");
        when(labeler.discreteValue(ex1)).thenReturn("NN");

        when(extractor.discreteValue(ex2)).thenReturn("two");
        when(labeler.discreteValue(ex2)).thenReturn("VB");

        learner.extractor = extractor;
        learner.labeler = labeler;

        learner.learn(ex1);
        learner.learn(ex2);

        ByteArrayOutputStream bones = new ByteArrayOutputStream();
        ExceptionlessOutputStream eos = new ExceptionlessOutputStream(bones);

        learner.write(eos);
        eos.close();

        byte[] bytes = bones.toByteArray();
        assertTrue(bytes.length > 0);
    }
@Test
    public void testReadBinaryHandlesZeroEntries() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ExceptionlessOutputStream eos = new ExceptionlessOutputStream(out);

        eos.writeInt(0); 
        eos.close();

        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        ExceptionlessInputStream eis = new ExceptionlessInputStream(in);

        POSBaselineLearner learner = new POSBaselineLearner("reader");
        learner.read(eis);

        assertFalse(learner.observed("anything"));
    }
@Test
    public void testObservedWithEmptyString() {
        POSBaselineLearner learner = new POSBaselineLearner("emptyString");

        Object example = mock(Object.class);
        Learner extractor = mock(Learner.class);
        Learner labeler = mock(Learner.class);

        when(extractor.discreteValue(example)).thenReturn("");
        when(labeler.discreteValue(example)).thenReturn("SYM");

        learner.extractor = extractor;
        learner.labeler = labeler;

        learner.learn(example);
        assertTrue(learner.observed(""));
        assertEquals(1, learner.observedCount(""));
    }
@Test
    public void testScoresReturnsNull() {
        POSBaselineLearner learner = new POSBaselineLearner("nullScores");
        ScoreSet result = learner.scores(new int[] {1}, new double[] {0.5});
        assertNull(result);
    }
@Test
    public void testDiscretValueOnNeverSeenButLooksLikeNumberReturnsCD() {
        POSBaselineLearner learner = new POSBaselineLearner("looksLikeNumber");

        Object example = mock(Object.class);
        Learner extractor = mock(Learner.class);
        when(extractor.discreteValue(example)).thenReturn("1,234.56");
        learner.extractor = extractor;

        String result = learner.discreteValue(example);
        assertEquals("CD", result);
    }
@Test
    public void testLearnWithRepeatedNullLabels() {
        POSBaselineLearner learner = new POSBaselineLearner("nullRepeat");

        Object example1 = mock(Object.class);
        Object example2 = mock(Object.class);

        Learner extractor = mock(Learner.class);
        Learner labeler = mock(Learner.class);

        when(extractor.discreteValue(example1)).thenReturn("X");
        when(labeler.discreteValue(example1)).thenReturn(null);

        when(extractor.discreteValue(example2)).thenReturn("X");
        when(labeler.discreteValue(example2)).thenReturn(null);

        learner.extractor = extractor;
        learner.labeler = labeler;

        learner.learn(example1);
        learner.learn(example2);

        assertEquals(2, learner.observedCount("X"));
        Set<String> tags = learner.allowableTags("X");
        assertTrue(tags.contains(null));
    }
@Test
    public void testObservedCountAfterForgetReturnsZero() {
        POSBaselineLearner learner = new POSBaselineLearner("forgetTest");

        Object example = mock(Object.class);
        Learner extractor = mock(Learner.class);
        Learner labeler = mock(Learner.class);

        when(extractor.discreteValue(example)).thenReturn("forgetme");
        when(labeler.discreteValue(example)).thenReturn("NN");

        learner.extractor = extractor;
        learner.labeler = labeler;

        learner.learn(example);
        assertEquals(1, learner.observedCount("forgetme"));

        learner.forget();
        assertEquals(0, learner.observedCount("forgetme"));
        assertFalse(learner.observed("forgetme"));
    }
@Test
    public void testDiscretValueTiebreakerSameFrequencyPreservesFirstInserted() {
        POSBaselineLearner learner = new POSBaselineLearner("tieBreakSameFreq");

        Object ex1 = mock(Object.class);
        Object ex2 = mock(Object.class);

        Learner extractor = mock(Learner.class);
        Learner labeler = mock(Learner.class);

        when(extractor.discreteValue(ex1)).thenReturn("tieWord");
        when(labeler.discreteValue(ex1)).thenReturn("A");

        when(extractor.discreteValue(ex2)).thenReturn("tieWord");
        when(labeler.discreteValue(ex2)).thenReturn("B");

        learner.extractor = extractor;
        learner.labeler = labeler;

        learner.learn(ex1);
        learner.learn(ex2);

        Object test = mock(Object.class);
        when(extractor.discreteValue(test)).thenReturn("tieWord");

        learner.extractor = extractor;
        String predicted = learner.discreteValue(test);

        assertNotNull(predicted);
        assertTrue(predicted.equals("A") || predicted.equals("B"));
        assertTrue(learner.allowableTags("tieWord").contains("A"));
        assertTrue(learner.allowableTags("tieWord").contains("B"));
    }
@Test
    public void testEmptyCloneProducesNewInstanceWithoutData() {
        POSBaselineLearner learner = new POSBaselineLearner("cloneTest");

        Object example = mock(Object.class);
        Learner extractor = mock(Learner.class);
        Learner labeler = mock(Learner.class);

        when(extractor.discreteValue(example)).thenReturn("eat");
        when(labeler.discreteValue(example)).thenReturn("VB");

        learner.extractor = extractor;
        learner.labeler = labeler;
        learner.learn(example);

        POSBaselineLearner clone = (POSBaselineLearner) learner.emptyClone();

        Object test = mock(Object.class);
        Learner testExtractor = mock(Learner.class);
        when(testExtractor.discreteValue(test)).thenReturn("eat");
        clone.extractor = testExtractor;

        String prediction = clone.discreteValue(test);
        assertEquals("UNKNOWN", prediction);
    }
@Test
    public void testWriteToTextStreamOrderForMultipleWords() {
        POSBaselineLearner learner = new POSBaselineLearner("orderTest");

        Object ex1 = mock(Object.class);
        Object ex2 = mock(Object.class);
        Object ex3 = mock(Object.class);

        Learner extractor = mock(Learner.class);
        Learner labeler = mock(Learner.class);

        when(extractor.discreteValue(ex1)).thenReturn("banana");
        when(labeler.discreteValue(ex1)).thenReturn("NN");

        when(extractor.discreteValue(ex2)).thenReturn("apple");
        when(labeler.discreteValue(ex2)).thenReturn("NN");

        when(extractor.discreteValue(ex3)).thenReturn("carrot");
        when(labeler.discreteValue(ex3)).thenReturn("NN");

        learner.extractor = extractor;
        learner.labeler = labeler;

        learner.learn(ex1);
        learner.learn(ex2);
        learner.learn(ex3);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream stream = new PrintStream(output);
        learner.write(stream);
        stream.close();

        String out = output.toString();
        int indexApple = out.indexOf("apple:");
        int indexBanana = out.indexOf("banana:");
        int indexCarrot = out.indexOf("carrot:");

        assertTrue(indexApple < indexBanana);
        assertTrue(indexBanana < indexCarrot);
    }
@Test
    public void testLooksLikeNumberWithMixedCharactersReturnsFalse() {
        String value = "12.3a";
        boolean result = POSBaselineLearner.looksLikeNumber(value);
        assertFalse(result);
    }
@Test
    public void testClassifierClassifyWithNullPrediction() {
        POSBaselineLearner learner = new POSBaselineLearner("predictNull");

        Object example = mock(Object.class);
        Learner extractor = mock(Learner.class);
        when(extractor.discreteValue(example)).thenReturn(null);
        learner.extractor = extractor;

        FeatureVector fv = learner.classify(example);
        assertNotNull(fv);
        assertEquals(1, fv.featuresSize());
        assertEquals("UNKNOWN", ((DiscretePrimitiveStringFeature) fv.getFeature(0)).getStringValue());
    }
@Test
    public void testBinaryReadWithMalformedDataThrowsException() throws Exception {
        byte[] invalidData = new byte[]{0, 0, 0, 5, 1, 2};  
        ByteArrayInputStream in = new ByteArrayInputStream(invalidData);
        ExceptionlessInputStream eis = new ExceptionlessInputStream(in);

        POSBaselineLearner learner = new POSBaselineLearner("garbage");

        try {
            learner.read(eis);
            fail("Expected exception due to malformed binary input");
        } catch (Exception e) {
            
            assertTrue(true);
        }
    }
@Test
    public void testWriteReadEmptyLearnerBinaryPreservesNothing() throws Exception {
        POSBaselineLearner learner = new POSBaselineLearner("emptyBin");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ExceptionlessOutputStream eos = new ExceptionlessOutputStream(bos);
        learner.write(eos);
        eos.close();

        POSBaselineLearner restored = new POSBaselineLearner("restoredBin");

        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ExceptionlessInputStream eis = new ExceptionlessInputStream(bis);
        restored.read(eis);

        assertFalse(restored.observed("random"));
        assertEquals(0, restored.observedCount("random"));
    }
@Test
    public void testLearnWithEmptyFeatureAndEmptyLabel() {
        POSBaselineLearner learner = new POSBaselineLearner("emptyInput");

        Object example = mock(Object.class);
        Learner extractor = mock(Learner.class);
        Learner labeler = mock(Learner.class);

        when(extractor.discreteValue(example)).thenReturn("");
        when(labeler.discreteValue(example)).thenReturn("");

        learner.extractor = extractor;
        learner.labeler = labeler;

        learner.learn(example);

        assertTrue(learner.observed(""));
        assertEquals(1, learner.observedCount(""));
        Set<String> tags = learner.allowableTags("");
        assertEquals(1, tags.size());
        assertTrue(tags.contains(""));
    }
@Test
    public void testClassifyFeatureVectorOnUnseenSemicolon() {
        POSBaselineLearner learner = new POSBaselineLearner("semicolonUnknown");

        Object example = mock(Object.class);
        Learner extractor = mock(Learner.class);
        when(extractor.discreteValue(example)).thenReturn(";");

        learner.extractor = extractor;

        FeatureVector fv = learner.classify(example);
        assertNotNull(fv);
        assertEquals(1, fv.featuresSize());
        Feature f = fv.getFeature(0);
        assertTrue(f instanceof DiscretePrimitiveStringFeature);
        DiscretePrimitiveStringFeature dfsf = (DiscretePrimitiveStringFeature) f;
        assertEquals(":", dfsf.getStringValue());
    }
@Test
    public void testAllowableTagsReturnsEmptySetOnUnknownNonNumericSymbol() {
        POSBaselineLearner learner = new POSBaselineLearner("nonNumericSymbol");

        Set<String> tags = learner.allowableTags("@$%^&");
        assertNotNull(tags);
        assertTrue(tags.isEmpty());
    }
@Test
    public void testClassifyIntDoubleArrayWithNonZeroLengthReturnsEmptyVector() {
        POSBaselineLearner learner = new POSBaselineLearner("intArrayTest");

        int[] features = new int[] {1, 2, 3};
        double[] values = new double[] {0.1, 0.2, 0.3};

        FeatureVector fv = learner.classify(features, values);
        assertNotNull(fv);
        assertEquals(0, fv.featuresSize());
    }
@Test
    public void testLooksLikeNumberWithIndividualPunctuationCharacters() {
        assertFalse(POSBaselineLearner.looksLikeNumber("."));
        assertFalse(POSBaselineLearner.looksLikeNumber(","));
        assertFalse(POSBaselineLearner.looksLikeNumber("-"));
    }
@Test
    public void testLooksLikeNumberWithLeadingAndTrailingPunctuation() {
        assertTrue(POSBaselineLearner.looksLikeNumber("-.123,456."));
    }
@Test
    public void testWriteStaticOutputFormatWithSingleEntry() {
        HashMap<String, TreeMap<String, Integer>> table = new HashMap<>();
        TreeMap<String, Integer> tags = new TreeMap<>();
        tags.put("NN", 3);
        table.put("lion", tags);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(out);
        POSBaselineLearner.write(ps, table);
        ps.close();

        String output = out.toString();
        assertTrue(output.contains("lion:"));
        assertTrue(output.contains("NN(3)"));
    }
@Test
    public void testBinaryWriteThenReadPreservesMultipleTags() throws Exception {
        POSBaselineLearner learner = new POSBaselineLearner("multiTagRW");

        Object ex1 = mock(Object.class);
        Object ex2 = mock(Object.class);
        Learner extractor = mock(Learner.class);
        Learner labeler = mock(Learner.class);

        when(extractor.discreteValue(ex1)).thenReturn("fly");
        when(labeler.discreteValue(ex1)).thenReturn("VB");

        when(extractor.discreteValue(ex2)).thenReturn("fly");
        when(labeler.discreteValue(ex2)).thenReturn("NN");

        learner.extractor = extractor;
        learner.labeler = labeler;

        learner.learn(ex1);
        learner.learn(ex2);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ExceptionlessOutputStream eos = new ExceptionlessOutputStream(bos);
        learner.write(eos);
        eos.close();

        POSBaselineLearner other = new POSBaselineLearner("loaded");
        other.extractor = extractor;
        other.labeler = labeler;

        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ExceptionlessInputStream eis = new ExceptionlessInputStream(bis);
        other.read(eis);

        assertTrue(other.observed("fly"));
        Set<String> tags = other.allowableTags("fly");
        assertEquals(2, tags.size());
        assertTrue(tags.contains("NN"));
        assertTrue(tags.contains("VB"));
    }
@Test
    public void testObservedCountForWordWithMultipleTags() {
        POSBaselineLearner learner = new POSBaselineLearner("countTags");

        Object ex1 = mock(Object.class);
        Object ex2 = mock(Object.class);
        Object ex3 = mock(Object.class);

        Learner extractor = mock(Learner.class);
        Learner labeler = mock(Learner.class);

        when(extractor.discreteValue(ex1)).thenReturn("play");
        when(labeler.discreteValue(ex1)).thenReturn("VB");

        when(extractor.discreteValue(ex2)).thenReturn("play");
        when(labeler.discreteValue(ex2)).thenReturn("NN");

        when(extractor.discreteValue(ex3)).thenReturn("play");
        when(labeler.discreteValue(ex3)).thenReturn("NN");

        learner.extractor = extractor;
        learner.labeler = labeler;

        learner.learn(ex1);
        learner.learn(ex2);
        learner.learn(ex3);

        assertEquals(3, learner.observedCount("play"));
        Set<String> tags = learner.allowableTags("play");
        assertEquals(2, tags.size());
    }
@Test
    public void testFeatureValueForUnseenWordThatLooksNumeric() {
        POSBaselineLearner learner = new POSBaselineLearner("unseenNumeric");

        Object example = mock(Object.class);
        Learner extractor = mock(Learner.class);
        when(extractor.discreteValue(example)).thenReturn("1000");
        learner.extractor = extractor;

        Feature f = learner.featureValue(example);
        assertTrue(f instanceof DiscretePrimitiveStringFeature);
        assertEquals("CD", ((DiscretePrimitiveStringFeature) f).getStringValue());
    }
@Test
    public void testWriteToPrintStreamWithNoEntriesProducesNoOutput() {
        POSBaselineLearner learner = new POSBaselineLearner("emptyPrint");

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(buffer);

        learner.write(ps);
        ps.close();

        String result = buffer.toString();
        assertTrue(result.trim().isEmpty());
    } 
}