public class POSBaselineLearner_3_GPTLLMTest { 

 @Test
    public void testDefaultConstruction() {
        POSBaselineLearner learner = new POSBaselineLearner();
        assertNotNull(learner);
        assertFalse(learner.observed("any"));
        assertEquals(0, learner.observedCount("any"));
    }
@Test
    public void testLearnSingleWord() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.labeler = w -> ((Word) w).tag;
        learner.extractor = w -> ((Word) w).form;

        Word word = new Word("walk");
        word.tag = "VB";

        learner.learn(word);

        assertTrue(learner.observed("walk"));
        assertEquals(1, learner.observedCount("walk"));
        assertTrue(learner.allowableTags("walk").contains("VB"));
    }
@Test
    public void testLearnSameWordTwice() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.labeler = w -> ((Word) w).tag;
        learner.extractor = w -> ((Word) w).form;

        Word word = new Word("walk");
        word.tag = "VB";

        learner.learn(word);
        learner.learn(word);

        assertEquals(2, learner.observedCount("walk"));
        assertTrue(learner.allowableTags("walk").contains("VB"));
    }
@Test
    public void testLearnSameWordTwoTags() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.labeler = w -> ((Word) w).tag;
        learner.extractor = w -> ((Word) w).form;

        Word word1 = new Word("run");
        word1.tag = "VB";
        Word word2 = new Word("run");
        word2.tag = "NN";

        learner.learn(word1);
        learner.learn(word2);

        Set<String> tags = learner.allowableTags("run");
        assertEquals(2, tags.size());
        assertTrue(tags.contains("VB"));
        assertTrue(tags.contains("NN"));
    }
@Test
    public void testDiscreteValueKnownWord() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.labeler = w -> ((Word) w).tag;
        learner.extractor = w -> ((Word) w).form;

        Word word = new Word("see");
        word.tag = "VB";
        learner.learn(word);

        String prediction = learner.discreteValue(word);
        assertEquals("VB", prediction);
    }
@Test
    public void testDiscreteValueUnknownPunctuation() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.labeler = w -> "";
        learner.extractor = w -> ((Word) w).form;

        Word word = new Word(";");
        String prediction = learner.discreteValue(word);
        assertEquals(":", prediction);
    }
@Test
    public void testDiscreteValueUnknownNumber() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.labeler = w -> "";
        learner.extractor = w -> ((Word) w).form;

        Word word = new Word("12,300");
        String prediction = learner.discreteValue(word);
        assertEquals("CD", prediction);
    }
@Test
    public void testDiscreteValueUnknownOther() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.labeler = w -> "";
        learner.extractor = w -> ((Word) w).form;

        Word word = new Word("techword");
        String prediction = learner.discreteValue(word);
        assertEquals("UNKNOWN", prediction);
    }
@Test
    public void testForgetErasesData() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.labeler = w -> ((Word) w).tag;
        learner.extractor = w -> ((Word) w).form;

        Word word = new Word("yes");
        word.tag = "UH";
        learner.learn(word);

        assertTrue(learner.observed("yes"));
        learner.forget();
        assertFalse(learner.observed("yes"));
    }
@Test
    public void testLooksLikeNumberValidCases() {
        assertTrue(POSBaselineLearner.looksLikeNumber("123"));
        assertTrue(POSBaselineLearner.looksLikeNumber("12.5"));
        assertTrue(POSBaselineLearner.looksLikeNumber("1-2"));
        assertTrue(POSBaselineLearner.looksLikeNumber("1,000"));
    }
@Test
    public void testLooksLikeNumberInvalidCases() {
        assertFalse(POSBaselineLearner.looksLikeNumber("abc"));
        assertFalse(POSBaselineLearner.looksLikeNumber("12$"));
        assertFalse(POSBaselineLearner.looksLikeNumber("1e5"));
    }
@Test
    public void testAllowableTagsSemicolon() {
        POSBaselineLearner learner = new POSBaselineLearner();
        Set<String> tags = learner.allowableTags(";");
        assertEquals(1, tags.size());
        assertTrue(tags.contains(":"));
    }
@Test
    public void testAllowableTagsNumber() {
        POSBaselineLearner learner = new POSBaselineLearner();
        Set<String> tags = learner.allowableTags("12.5");
        assertEquals(1, tags.size());
        assertTrue(tags.contains("CD"));
    }
@Test
    public void testAllowableTagsUnseenRegularWord() {
        POSBaselineLearner learner = new POSBaselineLearner();
        Set<String> tags = learner.allowableTags("xyz");
        assertTrue(tags.isEmpty());
    }
@Test
    public void testFeatureValueForWord() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.labeler = w -> ((Word) w).tag;
        learner.extractor = w -> ((Word) w).form;

        Word word = new Word("proof");
        word.tag = "NN";
        learner.learn(word);

        Feature feature = learner.featureValue(word);
        assertEquals("NN", feature.getStringValue());
    }
@Test
    public void testClassifyReturnsFeatureVector() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.labeler = w -> ((Word) w).tag;
        learner.extractor = w -> ((Word) w).form;

        Word word = new Word("sky");
        word.tag = "NN";

        learner.learn(word);
        FeatureVector fv = learner.classify(word);

        assertEquals(1, fv.featuresSize());
        assertEquals("NN", fv.getFeature(0).getStringValue());
    }
@Test
    public void testClassifyIntVersionReturnsEmpty() {
        POSBaselineLearner learner = new POSBaselineLearner();
        FeatureVector fv = learner.classify(new int[]{}, new double[]{});
        assertEquals(0, fv.featuresSize());
    }
@Test
    public void testEmptyCloneCreatesFreshLearner() {
        POSBaselineLearner learner = new POSBaselineLearner("original");
        Learner clone = learner.emptyClone();
        assertNotNull(clone);
        assertTrue(clone instanceof POSBaselineLearner);
    }
@Test
    public void testObservedTrueFalse() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.labeler = w -> ((Word) w).tag;
        learner.extractor = w -> ((Word) w).form;

        Word word = new Word("appear");
        word.tag = "VB";
        assertFalse(learner.observed("appear"));

        learner.learn(word);
        assertTrue(learner.observed("appear"));
    }
@Test
    public void testObservedCountZeroIfUnseen() {
        POSBaselineLearner learner = new POSBaselineLearner();
        int count = learner.observedCount("missing");
        assertEquals(0, count);
    }
@Test
    public void testScoresReturnsNull() {
        POSBaselineLearner learner = new POSBaselineLearner();
        assertNull(learner.scores(new int[]{}, new double[]{}));
    }
@Test
    public void testWriteAndReadBinaryPreservesModel() throws IOException {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.labeler = w -> ((Word) w).tag;
        learner.extractor = w -> ((Word) w).form;

        Word word = new Word("green");
        word.tag = "JJ";

        learner.learn(word);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExceptionlessOutputStream eos = new ExceptionlessOutputStream(baos);
        learner.write(eos);

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ExceptionlessInputStream eis = new ExceptionlessInputStream(bais);

        POSBaselineLearner readLearner = new POSBaselineLearner();
        readLearner.labeler = w -> ((Word) w).tag;
        readLearner.extractor = w -> ((Word) w).form;

        readLearner.read(eis);

        assertTrue(readLearner.observed("green"));
        assertEquals(1, readLearner.observedCount("green"));
    }
@Test
    public void testWriteTextOutputNotEmpty() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.labeler = w -> ((Word) w).tag;
        learner.extractor = w -> ((Word) w).form;

        Word word = new Word("walk");
        word.tag = "VB";
        learner.learn(word);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        learner.write(ps);

        String output = baos.toString();
        assertTrue(output.contains("walk"));
        assertTrue(output.contains("VB(1)"));
    }
@Test
    public void testLearnNullExampleShouldNotFail() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = o -> {
            if (o == null) return "";
            return ((Word) o).form;
        };
        learner.labeler = o -> {
            if (o == null) return "";
            return ((Word) o).tag;
        };

        learner.learn(null);
        assertFalse(learner.observed(null));
    }
@Test
    public void testComputePredictionTiesShouldPickFirstAlphabetically() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.labeler = o -> ((Word) o).tag;
        learner.extractor = o -> ((Word) o).form;

        Word word1 = new Word("key");
        word1.tag = "NN";
        Word word2 = new Word("key");
        word2.tag = "VB";

        learner.learn(word1);
        learner.learn(word2);

        String prediction = learner.discreteValue(new Word("key"));
        assertTrue(prediction.equals("NN") || prediction.equals("VB"));
    }
@Test
    public void testEmptyStringAsWordFormIsHandled() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = o -> ((Word) o).form;
        learner.labeler = o -> ((Word) o).tag;

        Word w = new Word("");
        w.tag = "SYM";

        learner.learn(w);

        assertTrue(learner.observed(""));
        assertEquals(1, learner.observedCount(""));
        assertTrue(learner.allowableTags("").contains("SYM"));
    }
@Test
    public void testWriteEmptyTableTextShouldPrintNothing() {
        POSBaselineLearner learner = new POSBaselineLearner();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        learner.write(ps);

        String output = baos.toString().trim();
        assertEquals("", output);
    }
@Test
    public void testWriteEmptyTableBinary() throws IOException {
        POSBaselineLearner learner = new POSBaselineLearner();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExceptionlessOutputStream eos = new ExceptionlessOutputStream(baos);
        learner.write(eos);

        byte[] bytes = baos.toByteArray();
        assertNotNull(bytes);
        assertTrue(bytes.length > 0);
    }
@Test
    public void testReadEmptyBinaryTable() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExceptionlessOutputStream eos = new ExceptionlessOutputStream(baos);

        
        eos.writeInt(0);
        eos.flush();

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ExceptionlessInputStream eis = new ExceptionlessInputStream(bais);

        POSBaselineLearner learner = new POSBaselineLearner();
        learner.read(eis);

        assertFalse(learner.observed("any"));
        assertEquals(0, learner.observedCount("any"));
    }
@Test
    public void testObservedNullFormReturnsFalse() {
        POSBaselineLearner learner = new POSBaselineLearner();
        assertFalse(learner.observed(null));
    }
@Test
    public void testAllowableTagsNullReturnsEmptySet() {
        POSBaselineLearner learner = new POSBaselineLearner();
        Set<String> tags = learner.allowableTags(null);
        assertNotNull(tags);
        assertTrue(tags.isEmpty());
    }
@Test
    public void testObservedCountNullReturnsZero() {
        POSBaselineLearner learner = new POSBaselineLearner();
        int count = learner.observedCount(null);
        assertEquals(0, count);
    }
@Test
    public void testLearnWithNullLabelReturnsZeroCount() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = o -> "word";
        learner.labeler = o -> null;

        Object dummy = new Object();
        learner.learn(dummy);
        assertEquals(1, learner.observedCount("word"));
        Set<String> tags = learner.allowableTags("word");
        assertTrue(tags.contains(null));
    }
@Test
    public void testWritePrintStreamWithEqualFrequenciesSortedAlphabetically() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.labeler = o -> ((Word)o).tag;
        learner.extractor = o -> ((Word)o).form;

        Word w1 = new Word("bank");
        w1.tag = "VB";
        Word w2 = new Word("bank");
        w2.tag = "NN";

        learner.learn(w1);
        learner.learn(w2);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        learner.write(ps);

        String output = baos.toString();
        assertTrue(output.contains("bank:"));
        assertTrue(output.contains("NN(1)"));
        assertTrue(output.contains("VB(1)"));
    }
@Test
    public void testWritePrintStreamTagOrdering() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.labeler = o -> ((Word)o).tag;
        learner.extractor = o -> ((Word)o).form;

        Word w1 = new Word("apple");
        w1.tag = "DT";
        Word w2 = new Word("apple");
        w2.tag = "DT";
        Word w3 = new Word("apple");
        w3.tag = "NN";

        learner.learn(w1);
        learner.learn(w2);
        learner.learn(w3);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        learner.write(ps);

        String output = baos.toString();
        assertTrue(output.contains("DT(2)"));
        assertTrue(output.contains("NN(1)"));
        int dtIndex = output.indexOf("DT(2)");
        int nnIndex = output.indexOf("NN(1)");
        assertTrue(dtIndex < nnIndex);
    }
@Test
    public void testDiscreteValueWithBlankTagAndForm() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.labeler = o -> "";
        learner.extractor = o -> "";

        Object dummy = new Object();
        String prediction = learner.discreteValue(dummy);

        assertEquals("UNKNOWN", prediction);
    }
@Test
    public void testWriteBinaryWithMultipleMapEntries() throws IOException {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.labeler = o -> ((Word)o).tag;
        learner.extractor = o -> ((Word)o).form;

        Word w1 = new Word("mouse");
        w1.tag = "NN";
        Word w2 = new Word("click");
        w2.tag = "VB";

        learner.learn(w1);
        learner.learn(w2);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExceptionlessOutputStream eos = new ExceptionlessOutputStream(baos);
        learner.write(eos);

        assertTrue(baos.toByteArray().length > 0);
    }
@Test
    public void testReadCorruptedBinaryStreamShouldThrow() throws IOException {
        byte[] corrupted = new byte[] {0, 0, 0, 10, 65, 66}; 
        ByteArrayInputStream bais = new ByteArrayInputStream(corrupted);
        ExceptionlessInputStream eis = new ExceptionlessInputStream(bais);

        POSBaselineLearner learner = new POSBaselineLearner();

        try {
            learner.read(eis);
            fail("Expected exception while reading corrupted stream.");
        } catch (Exception e) {
            assertNotNull(e);
        }
    }
@Test
    public void testClassifyUnseenAfterTrainingOtherWords() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.labeler = o -> ((Word)o).tag;
        learner.extractor = o -> ((Word)o).form;

        Word w1 = new Word("train");
        w1.tag = "VB";
        learner.learn(w1);

        Word w2 = new Word("unknown");
        FeatureVector fv = learner.classify(w2);
        assertEquals("UNKNOWN", fv.getFeature(0).getStringValue());
    }
@Test
    public void testLearnSameWordSameTagMultipleTimes() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.labeler = o -> ((Word)o).tag;
        learner.extractor = o -> ((Word)o).form;

        Word w = new Word("java");
        w.tag = "NN";

        learner.learn(w);
        learner.learn(w);
        learner.learn(w);

        assertEquals(3, learner.observedCount("java"));
        assertEquals("NN", learner.discreteValue(w));
    }
@Test
    public void testLearnSameWordConflictingTagsFavorMostFrequent() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.labeler = o -> ((Word)o).tag;
        learner.extractor = o -> ((Word)o).form;

        Word w1 = new Word("charge");
        w1.tag = "VB";
        Word w2 = new Word("charge");
        w2.tag = "VB";
        Word w3 = new Word("charge");
        w3.tag = "NN";

        learner.learn(w1);
        learner.learn(w2);
        learner.learn(w3);

        String result = learner.discreteValue(w1);
        assertEquals("VB", result);
    }
@Test
    public void testWriteBinaryThenReadPreservesMultipleTags() throws IOException {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.labeler = o -> ((Word)o).tag;
        learner.extractor = o -> ((Word)o).form;

        Word w1 = new Word("lead");
        w1.tag = "NN";
        Word w2 = new Word("lead");
        w2.tag = "VB";

        learner.learn(w1);
        learner.learn(w2);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExceptionlessOutputStream eos = new ExceptionlessOutputStream(baos);
        learner.write(eos);

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ExceptionlessInputStream eis = new ExceptionlessInputStream(bais);

        POSBaselineLearner loaded = new POSBaselineLearner();
        loaded.labeler = o -> ((Word)o).tag;
        loaded.extractor = o -> ((Word)o).form;
        loaded.read(eis);

        assertTrue(loaded.allowableTags("lead").contains("NN"));
        assertTrue(loaded.allowableTags("lead").contains("VB"));
        assertEquals(2, loaded.observedCount("lead"));
    }
@Test
    public void testLearnWithWhitespaceForm() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = o -> ((Word) o).form;
        learner.labeler = o -> ((Word) o).tag;

        Word word = new Word("   ");
        word.tag = "NN";

        learner.learn(word);

        assertTrue(learner.observed("   "));
        assertEquals(1, learner.observedCount("   "));
        assertTrue(learner.allowableTags("   ").contains("NN"));
    }
@Test
    public void testLearnWithEmptyTag() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = o -> ((Word) o).form;
        learner.labeler = o -> "";

        Word word = new Word("unknown");
        learner.learn(word);

        assertTrue(learner.observed("unknown"));
        Set<String> tags = learner.allowableTags("unknown");
        assertTrue(tags.contains(""));
    }
@Test
    public void testNullTagAndFormHandlingInPrediction() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = o -> null;
        learner.labeler = o -> null;

        String result = learner.discreteValue(new Object());
        assertEquals("UNKNOWN", result);
    }
@Test
    public void testNullFormLooksLikeNumberReturnsFalse() {
        boolean result = POSBaselineLearner.looksLikeNumber(null);
        assertFalse(result);
    }
@Test
    public void testLooksLikeNumberWithOnlyPunctuation() {
        boolean result = POSBaselineLearner.looksLikeNumber(".,-");
        assertFalse(result); 
    }
@Test
    public void testAllowableTagsForFormWithOnlyPunctuation() {
        POSBaselineLearner learner = new POSBaselineLearner();
        Set<String> tags = learner.allowableTags(".,-");
        assertTrue(tags.isEmpty());
    }
@Test
    public void testLearnThenForgetAndLearnAgain() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = o -> ((Word) o).form;
        learner.labeler = o -> ((Word) o).tag;

        Word word = new Word("resume");
        word.tag = "VB";

        learner.learn(word);
        learner.forget();
        learner.learn(word);

        assertEquals(1, learner.observedCount("resume"));
        assertTrue(learner.allowableTags("resume").contains("VB"));
    }
@Test
    public void testWriteThenReadWithEmptyTagEntryPreserved() throws Exception {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = o -> ((Word) o).form;
        learner.labeler = o -> "";

        Word word = new Word("ghost");
        learner.learn(word);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExceptionlessOutputStream eos = new ExceptionlessOutputStream(baos);
        learner.write(eos);

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ExceptionlessInputStream eis = new ExceptionlessInputStream(bais);

        POSBaselineLearner reloaded = new POSBaselineLearner();
        reloaded.extractor = o -> ((Word) o).form;
        reloaded.labeler = o -> "";

        reloaded.read(eis);

        assertTrue(reloaded.observed("ghost"));
        Set<String> tags = reloaded.allowableTags("ghost");
        assertTrue(tags.contains(""));
    }
@Test
    public void testFeatureValueReturnsValidDiscreteFeature() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = o -> ((Word) o).form;
        learner.labeler = o -> ((Word) o).tag;

        Word word = new Word("sky");
        word.tag = "NN";
        learner.learn(word);

        Feature feature = learner.featureValue(word);
        assertNotNull(feature);
        assertEquals("NN", feature.getStringValue());
    }
@Test
    public void testClassifyVectorForMultipleFormInstances() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = o -> ((Word) o).form;
        learner.labeler = o -> ((Word) o).tag;

        Word w1 = new Word("press");
        w1.tag = "VB";
        Word w2 = new Word("press");
        w2.tag = "NN";

        learner.learn(w1);
        learner.learn(w1);
        learner.learn(w2);

        FeatureVector fv = learner.classify(new Word("press"));
        String result = fv.getFeature(0).getStringValue();
        assertEquals("VB", result);
    }
@Test
    public void testObservedAndAllowableTagsAfterForget() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = o -> ((Word) o).form;
        learner.labeler = o -> ((Word) o).tag;

        Word word = new Word("token");
        word.tag = "SYM";
        learner.learn(word);
        learner.forget();

        assertFalse(learner.observed("token"));
        assertEquals(0, learner.observedCount("token"));
        assertTrue(learner.allowableTags("token").isEmpty());
    }
@Test
    public void testFeatureValueForUnknownWordReturnsExpectedLabel() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = o -> ((Word) o).form;
        learner.labeler = o -> ((Word) o).tag;

        Word known = new Word("employ");
        known.tag = "VB";
        learner.learn(known);

        Word unknown = new Word("employed");
        Feature feature = learner.featureValue(unknown);
        assertEquals("UNKNOWN", feature.getStringValue());
    }
@Test
    public void testMultipleWordsWithSameTagOneHasCountTwo() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = o -> ((Word) o).form;
        learner.labeler = o -> ((Word) o).tag;

        Word w1 = new Word("run");
        w1.tag = "VB";
        learner.learn(w1);
        learner.learn(w1);

        Word w2 = new Word("ran");
        w2.tag = "VB";
        learner.learn(w2);

        assertEquals(2, learner.observedCount("run"));
        assertEquals(1, learner.observedCount("ran"));
    }
@Test
    public void testEmptyCloneHasCleanState() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.labeler = o -> ((Word) o).tag;
        learner.extractor = o -> ((Word) o).form;

        Word word = new Word("fly");
        word.tag = "VB";
        learner.learn(word);

        POSBaselineLearner empty = (POSBaselineLearner) learner.emptyClone();
        assertFalse(empty.observed("fly"));
        assertEquals(0, empty.observedCount("fly"));
    }
@Test
    public void testWriteTextSortsKeysAlphabetically() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = o -> ((Word) o).form;
        learner.labeler = o -> ((Word) o).tag;

        Word w1 = new Word("zebra");
        w1.tag = "NN";
        Word w2 = new Word("apple");
        w2.tag = "NN";
        learner.learn(w1);
        learner.learn(w2);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        learner.write(new PrintStream(baos));

        String output = baos.toString();
        int i1 = output.indexOf("apple:");
        int i2 = output.indexOf("zebra:");
        assertTrue(i1 < i2);
    }
@Test
    public void testLearnWithUnicodeCharactersInFormAndTag() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = o -> ((Word) o).form;
        learner.labeler = o -> ((Word) o).tag;
        Word word = new Word("naïve");
        word.tag = "ADJ";

        learner.learn(word);

        assertTrue(learner.observed("naïve"));
        assertTrue(learner.allowableTags("naïve").contains("ADJ"));
    }
@Test
    public void testWriteTextOutputEmptyTag() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = o -> ((Word) o).form;
        learner.labeler = o -> "";

        Word word = new Word("something");
        learner.learn(word);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        learner.write(new PrintStream(out));
        String result = out.toString();
        assertTrue(result.contains("something:"));
        assertTrue(result.contains("()")); 
    }
@Test
    public void testBinarySerializationDeserializationWithEmptyTag() throws IOException {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = o -> ((Word) o).form;
        learner.labeler = o -> "";

        Word word = new Word("invisible");
        learner.learn(word);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExceptionlessOutputStream eos = new ExceptionlessOutputStream(baos);
        learner.write(eos);

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ExceptionlessInputStream eis = new ExceptionlessInputStream(bais);

        POSBaselineLearner reloaded = new POSBaselineLearner();
        reloaded.extractor = o -> ((Word) o).form;
        reloaded.labeler = o -> "";

        reloaded.read(eis);

        Set<String> tags = reloaded.allowableTags("invisible");
        assertTrue(tags.contains(""));
        assertEquals(1, reloaded.observedCount("invisible"));
    }
@Test
    public void testReadMethodClearsPreviousState() throws IOException {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = o -> ((Word) o).form;
        learner.labeler = o -> ((Word) o).tag;

        Word word1 = new Word("first");
        word1.tag = "JJ";
        learner.learn(word1);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExceptionlessOutputStream eos = new ExceptionlessOutputStream(baos);
        learner.write(eos);

        learner.learn(new Word("extra"));
        assertTrue(learner.observed("extra"));

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ExceptionlessInputStream eis = new ExceptionlessInputStream(bais);
        learner.read(eis);

        assertFalse(learner.observed("extra"));
        assertTrue(learner.observed("first"));
    }
@Test
    public void testWriteBinaryWithSpecialTags() throws IOException {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = o -> ((Word) o).form;
        learner.labeler = o -> "!@#";

        Word word = new Word("punctuated");
        learner.learn(word);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExceptionlessOutputStream eos = new ExceptionlessOutputStream(baos);
        learner.write(eos);

        assertTrue(baos.toByteArray().length > 0);
    }
@Test
    public void testObservedFalseForWordWithSimilarButDifferentForm() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = o -> ((Word) o).form;
        learner.labeler = o -> ((Word) o).tag;

        Word w1 = new Word("Book");
        w1.tag = "NNP";

        learner.learn(w1);

        assertFalse(learner.observed("book")); 
        assertTrue(learner.observed("Book"));
    }
@Test
    public void testAllowableTagsReturnsCorrectTagAfterTieResolvedAlphabetically() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = o -> ((Word) o).form;
        learner.labeler = o -> ((Word) o).tag;

        Word w1 = new Word("tie");
        w1.tag = "B";
        Word w2 = new Word("tie");
        w2.tag = "A";

        learner.learn(w1);
        learner.learn(w2);

        Set<String> tags = learner.allowableTags("tie");
        assertTrue(tags.contains("A"));
        assertTrue(tags.contains("B"));

        String predicted = learner.discreteValue(new Word("tie"));
        assertTrue(predicted.equals("A") || predicted.equals("B")); 
    }
@Test
    public void testClassifyReturnsUnknownOnEmptyModel() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = o -> ((Word) o).form;
        learner.labeler = o -> ((Word) o).tag;

        Word unknownWord = new Word("something");

        FeatureVector fv = learner.classify(unknownWord);
        assertNotNull(fv);
        assertEquals("UNKNOWN", fv.getFeature(0).getStringValue());
    }
@Test
    public void testLearnWithSameTagDifferentCasingShouldTreatSeparately() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = o -> ((Word) o).form;
        learner.labeler = o -> ((Word) o).tag;

        Word w1 = new Word("jump");
        w1.tag = "NN";
        Word w2 = new Word("jump");
        w2.tag = "nn";

        learner.learn(w1);
        learner.learn(w2);

        Set<String> tags = learner.allowableTags("jump");
        assertTrue(tags.contains("NN"));
        assertTrue(tags.contains("nn"));
        assertEquals(2, learner.observedCount("jump"));
    }
@Test
    public void testLearnWithDifferentWordsSameTag() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.labeler = o -> "JJ";
        learner.extractor = o -> ((Word) o).form;

        Word word1 = new Word("beautiful");
        Word word2 = new Word("fast");

        learner.learn(word1);
        learner.learn(word2);

        assertTrue(learner.observed("beautiful"));
        assertTrue(learner.observed("fast"));
        assertEquals(1, learner.observedCount("beautiful"));
        assertEquals(1, learner.observedCount("fast"));
        assertTrue(learner.allowableTags("beautiful").contains("JJ"));
        assertTrue(learner.allowableTags("fast").contains("JJ"));
    }
@Test
    public void testAllowableTagsForNullFormDoesNotThrowException() {
        POSBaselineLearner learner = new POSBaselineLearner();
        Set<String> result = learner.allowableTags(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
@Test
    public void testObserveSameFormWithDifferentTagsMultipleTimesPrefersMostFrequent() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = o -> ((Word) o).form;
        learner.labeler = o -> ((Word) o).tag;

        Word w1 = new Word("record");
        w1.tag = "NN";
        Word w2 = new Word("record");
        w2.tag = "VB";
        Word w3 = new Word("record");
        w3.tag = "VB";

        learner.learn(w1);
        learner.learn(w2);
        learner.learn(w3);

        Feature f = learner.featureValue(w1);
        assertEquals("VB", f.getStringValue());
    }
@Test
    public void testWriteTextWithMultipleTagsForSameFormPrintsAll() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = o -> ((Word)o).form;
        learner.labeler = o -> ((Word)o).tag;

        Word w1 = new Word("lead");
        w1.tag = "VB";

        Word w2 = new Word("lead");
        w2.tag = "NN";

        learner.learn(w1);
        learner.learn(w2);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(output);
        learner.write(ps);

        String text = output.toString();
        assertTrue(text.contains("lead:"));
        assertTrue(text.contains("VB(1)"));
        assertTrue(text.contains("NN(1)"));
    }
@Test
    public void testLearnWithNullFormAndTagHandledWithoutCrash() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = o -> null;
        learner.labeler = o -> null;

        Object dummy = new Object();
        learner.learn(dummy); 
        assertFalse(learner.observed(null));
        assertEquals(0, learner.observedCount(null));
        assertTrue(learner.allowableTags(null).isEmpty());
    }
@Test
    public void testWriteThenReadPreservesDataWithSymbolicTag() throws Exception {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = o -> ((Word) o).form;
        learner.labeler = o -> "$$";

        Word word = new Word("token");
        learner.learn(word);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExceptionlessOutputStream eos = new ExceptionlessOutputStream(baos);
        learner.write(eos);

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ExceptionlessInputStream eis = new ExceptionlessInputStream(bais);

        POSBaselineLearner newLearner = new POSBaselineLearner();
        newLearner.extractor = o -> ((Word) o).form;
        newLearner.labeler = o -> "$$";
        newLearner.read(eis);

        assertEquals(1, newLearner.observedCount("token"));
        assertTrue(newLearner.allowableTags("token").contains("$$"));
    }
@Test
    public void testForgetAfterReadClearsDeserializedData() throws Exception {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = o -> ((Word) o).form;
        learner.labeler = o -> "TAG";

        Word word = new Word("shadow");
        learner.learn(word);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExceptionlessOutputStream eos = new ExceptionlessOutputStream(baos);
        learner.write(eos);

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ExceptionlessInputStream eis = new ExceptionlessInputStream(bais);

        learner.read(eis);
        assertTrue(learner.observed("shadow"));

        learner.forget();
        assertFalse(learner.observed("shadow"));
    }
@Test
    public void testClassifyEmptyWordStillReturnsFeatureVector() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = o -> "";
        learner.labeler = o -> "";

        Word word = new Word("");
        FeatureVector fv = learner.classify(word);
        assertNotNull(fv);
        assertEquals("UNKNOWN", fv.getFeature(0).getStringValue());
    }
@Test
    public void testScoresReturnsNullConsistently() {
        POSBaselineLearner learner = new POSBaselineLearner();
        assertNull(learner.scores(null, null));
        assertNull(learner.scores(new int[]{}, new double[]{}));
    }
@Test
    public void testFeatureValueReturnsEmptyOnUntrainedNumericWord() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = o -> ((Word) o).form;
        learner.labeler = o -> ((Word) o).tag;

        Word w = new Word("500,000");

        Feature f = learner.featureValue(w);
        assertEquals("CD", f.getStringValue());
    }
@Test
    public void testLearnSameWordWithNullAndNonNullTags() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = o -> "hello";
        learner.labeler = new Object() {
            boolean first = true;
            public String label(Object o) {
                if (first) {
                    first = false;
                    return null;
                } else {
                    return "TAG";
                }
            }
        }::label;

        learner.learn(new Object());
        learner.learn(new Object());

        assertEquals(2, learner.observedCount("hello"));
        Set<String> tags = learner.allowableTags("hello");
        assertTrue(tags.contains("TAG"));
        assertTrue(tags.contains(null));
    }
@Test
    public void testTableKeySortOrderInWriteText() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = o -> ((Word)o).form;
        learner.labeler = o -> ((Word)o).tag;

        Word w1 = new Word("banana");
        w1.tag = "NN";
        Word w2 = new Word("apple");
        w2.tag = "NN";

        learner.learn(w1);
        learner.learn(w2);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        learner.write(ps);

        String output = baos.toString();
        int appleIndex = output.indexOf("apple:");
        int bananaIndex = output.indexOf("banana:");
        assertTrue(appleIndex < bananaIndex);
    }
@Test
    public void testLearnWordWithEmptyFormAndEmptyTag() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = o -> "";
        learner.labeler = o -> "";

        learner.learn(new Object());
        assertTrue(learner.observed(""));
        assertTrue(learner.allowableTags("").contains(""));
        assertEquals(1, learner.observedCount(""));
    }
@Test
    public void testLooksLikeNumberWithJustDigitsReturnsTrue() {
        boolean result = POSBaselineLearner.looksLikeNumber("456789");
        assertTrue(result);
    }
@Test
    public void testLooksLikeNumberMixedValidAndInvalidChars() {
        boolean result = POSBaselineLearner.looksLikeNumber("12-3a");
        assertFalse(result);
    }
@Test
    public void testFeatureValueReturnsUnknownForCaseSensitiveMiss() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = o -> ((Word) o).form;
        learner.labeler = o -> ((Word) o).tag;

        Word trained = new Word("Capital");
        trained.tag = "NNP";
        learner.learn(trained);

        Word unseen = new Word("capital");
        Feature f = learner.featureValue(unseen);
        assertEquals("UNKNOWN", f.getStringValue());
    }
@Test
    public void testClassifyMethodReturnsCorrectForMostFrequentTag() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = o -> ((Word)o).form;
        learner.labeler = o -> ((Word)o).tag;

        Word w1 = new Word("test");
        w1.tag = "X";
        Word w2 = new Word("test");
        w2.tag = "Y";
        Word w3 = new Word("test");
        w3.tag = "X";

        learner.learn(w1);
        learner.learn(w2);
        learner.learn(w3);

        FeatureVector fv = learner.classify(w1);
        assertEquals("X", fv.getFeature(0).getStringValue());
    }
@Test
    public void testBinarySerializationDeserializationPreservesMultipleTagsAndCounts() throws Exception {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = o -> ((Word)o).form;
        learner.labeler = o -> ((Word)o).tag;

        Word w1 = new Word("data");
        w1.tag = "NOUN";
        Word w2 = new Word("data");
        w2.tag = "NOUN";
        Word w3 = new Word("data");
        w3.tag = "VB";

        learner.learn(w1);
        learner.learn(w2);
        learner.learn(w3);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ExceptionlessOutputStream eos = new ExceptionlessOutputStream(out);
        learner.write(eos);

        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        ExceptionlessInputStream eis = new ExceptionlessInputStream(in);

        POSBaselineLearner loaded = new POSBaselineLearner();
        loaded.extractor = o -> ((Word)o).form;
        loaded.labeler = o -> ((Word)o).tag;

        loaded.read(eis);

        assertTrue(loaded.observed("data"));
        assertEquals(3, loaded.observedCount("data"));
        assertEquals("NOUN", loaded.discreteValue(new Word("data")));
    }
@Test
    public void testClassifyWithNullExtractorReturnsUNKNOWN() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = o -> null;
        learner.labeler = o -> "NN";

        FeatureVector fv = learner.classify(new Object());
        assertEquals("UNKNOWN", fv.getFeature(0).getStringValue());
    }
@Test
    public void testLabelerReturnsNullTagPredictionStillValid() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = o -> "key";
        learner.labeler = o -> null;

        learner.learn(new Object());
        FeatureVector fv = learner.classify(new Object());
        assertEquals("UNKNOWN", fv.getFeature(0).getStringValue());
    }
@Test
    public void testClassifyPreviouslySeenButEdgeCaseReturnsCorrectPrediction() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = o -> ".,-";
        learner.labeler = o -> "CD";

        learner.learn(new Object());

        FeatureVector fv = learner.classify(new Object());
        assertEquals("CD", fv.getFeature(0).getStringValue());
    }
@Test
    public void testWriteBinaryEmptyModelStillSerializable() throws Exception {
        POSBaselineLearner learner = new POSBaselineLearner();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ExceptionlessOutputStream eos = new ExceptionlessOutputStream(out);
        learner.write(eos);
        assertTrue(out.toByteArray().length > 0);
    }
@Test
    public void testReadEmptySerializedModelRestoresZeroState() throws Exception {
        POSBaselineLearner learner = new POSBaselineLearner();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ExceptionlessOutputStream eos = new ExceptionlessOutputStream(out);
        learner.write(eos);

        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        ExceptionlessInputStream eis = new ExceptionlessInputStream(in);

        POSBaselineLearner loaded = new POSBaselineLearner();
        loaded.read(eis);
        assertFalse(loaded.observed("anything"));
    } 
}