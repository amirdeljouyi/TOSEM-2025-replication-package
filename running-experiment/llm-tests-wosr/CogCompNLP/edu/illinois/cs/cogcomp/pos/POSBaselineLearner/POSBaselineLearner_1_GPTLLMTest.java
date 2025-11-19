public class POSBaselineLearner_1_GPTLLMTest { 

 @Test
    public void testLearnAndPredictSingle() {
        POSBaselineLearner learner = new POSBaselineLearner("TestLearner");
        learner.extractor = new Classifier() {
            public String discreteValue(Object o) {
                return ((Word) o).form;
            }
        };
        learner.labeler = new Classifier() {
            public String discreteValue(Object o) {
                return ((Word) o).tag;
            }
        };

        Word word = new Word("run");
        word.tag = "VB";

        learner.learn(word);
        String prediction = learner.discreteValue(word);

        assertEquals("VB", prediction);
        assertTrue(learner.observed("run"));
        assertEquals(1, learner.observedCount("run"));
        assertTrue(learner.allowableTags("run").contains("VB"));
    }
@Test
    public void testLearnMultipleTagsSameWord() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = new Classifier() {
            public String discreteValue(Object o) {
                return ((Word) o).form;
            }
        };
        learner.labeler = new Classifier() {
            public String discreteValue(Object o) {
                return ((Word) o).tag;
            }
        };

        Word word1 = new Word("run");
        word1.tag = "VB";
        learner.learn(word1);

        Word word2 = new Word("run");
        word2.tag = "VB";
        learner.learn(word2);

        Word word3 = new Word("run");
        word3.tag = "NN";
        learner.learn(word3);

        String prediction = learner.discreteValue(word3);

        assertEquals("VB", prediction);
        Set<String> tags = learner.allowableTags("run");
        assertTrue(tags.contains("VB"));
        assertTrue(tags.contains("NN"));
        assertEquals(3, learner.observedCount("run"));
    }
@Test
    public void testUnseenWordPrediction() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = new Classifier() {
            public String discreteValue(Object o) {
                return ((Word) o).form;
            }
        };
        learner.labeler = new Classifier() {
            public String discreteValue(Object o) {
                return ((Word) o).tag;
            }
        };

        Word word = new Word("unknown");
        word.tag = "NN";

        String prediction = learner.discreteValue(word);

        assertEquals("UNKNOWN", prediction);
    }
@Test
    public void testLooksLikeNumberDigitOnly() {
        boolean result = POSBaselineLearner.looksLikeNumber("12345");
        assertTrue(result);
    }
@Test
    public void testLooksLikeNumberWithDotsAndCommas() {
        boolean result = POSBaselineLearner.looksLikeNumber("1,234.56");
        assertTrue(result);
    }
@Test
    public void testLooksLikeNumberNegative() {
        boolean result = POSBaselineLearner.looksLikeNumber("-123");
        assertTrue(result);
    }
@Test
    public void testLooksLikeNumberInvalid() {
        boolean result = POSBaselineLearner.looksLikeNumber("12a3");
        assertFalse(result);
    }
@Test
    public void testLooksLikeNumberOnlyPunctuation() {
        boolean result = POSBaselineLearner.looksLikeNumber("...,,--");
        assertFalse(result);
    }
@Test
    public void testLooksLikeNumberNoDigits() {
        boolean result = POSBaselineLearner.looksLikeNumber("abc");
        assertFalse(result);
    }
@Test
    public void testUnseenSemicolonReturnsColonTag() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = new Classifier() {
            public String discreteValue(Object o) {
                return ((Word) o).form;
            }
        };
        learner.labeler = new Classifier() {
            public String discreteValue(Object o) {
                return ((Word) o).tag;
            }
        };

        Word word = new Word(";");
        word.tag = ":";

        String prediction = learner.discreteValue(word);
        assertEquals(":", prediction);

        Set<String> tags = learner.allowableTags(";");
        assertTrue(tags.contains(":"));
        assertEquals(1, tags.size());
    }
@Test
    public void testUnseenNumberReturnsCDTag() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = new Classifier() {
            public String discreteValue(Object o) {
                return ((Word) o).form;
            }
        };
        learner.labeler = new Classifier() {
            public String discreteValue(Object o) {
                return ((Word) o).tag;
            }
        };

        Word word = new Word("1996");
        word.tag = "CD";

        String prediction = learner.discreteValue(word);
        assertEquals("CD", prediction);

        Set<String> tags = learner.allowableTags("1996");
        assertTrue(tags.contains("CD"));
        assertEquals(1, tags.size());
    }
@Test
    public void testForgetClearsObservedData() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = new Classifier() {
            public String discreteValue(Object o) {
                return ((Word) o).form;
            }
        };
        learner.labeler = new Classifier() {
            public String discreteValue(Object o) {
                return ((Word) o).tag;
            }
        };

        Word word = new Word("walk");
        word.tag = "VB";
        learner.learn(word);

        assertTrue(learner.observed("walk"));
        learner.forget();
        assertFalse(learner.observed("walk"));
        assertEquals(0, learner.observedCount("walk"));
    }
@Test
    public void testFeatureValueReturnsCorrectFeature() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = new Classifier() {
            public String discreteValue(Object o) {
                return ((Word) o).form;
            }
        };
        learner.labeler = new Classifier() {
            public String discreteValue(Object o) {
                return ((Word) o).tag;
            }
        };

        Word word = new Word("play");
        word.tag = "VB";
        learner.learn(word);

        Feature f = learner.featureValue(word);
        assertEquals("VB", f.getStringValue());
    }
@Test
    public void testClassifyReturnsCorrectFeatureVector() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = new Classifier() {
            public String discreteValue(Object o) {
                return ((Word) o).form;
            }
        };
        learner.labeler = new Classifier() {
            public String discreteValue(Object o) {
                return ((Word) o).tag;
            }
        };

        Word word = new Word("jump");
        word.tag = "VB";
        learner.learn(word);

        FeatureVector fv = learner.classify(word);
        assertEquals(1, fv.featuresSize());
        assertEquals("VB", fv.getFeature(0).getStringValue());
    }
@Test
    public void testEmptyCloneHasNoLearnedData() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = new Classifier() {
            public String discreteValue(Object o) {
                return ((Word) o).form;
            }
        };
        learner.labeler = new Classifier() {
            public String discreteValue(Object o) {
                return ((Word) o).tag;
            }
        };

        Word word = new Word("run");
        word.tag = "VB";
        learner.learn(word);

        POSBaselineLearner clone = (POSBaselineLearner) learner.emptyClone();
        clone.extractor = learner.extractor;
        clone.labeler = learner.labeler;

        assertFalse(clone.observed("run"));
        assertEquals("UNKNOWN", clone.discreteValue(word));
    }
@Test
    public void testGetInputTypeReturnsCorrectType() {
        POSBaselineLearner learner = new POSBaselineLearner();
        String type = learner.getInputType();
        assertEquals("edu.illinois.cs.cogcomp.lbjava.nlp.Word", type);
    }
@Test
    public void testClassifyWithIntFeatureReturnsEmptyVector() {
        POSBaselineLearner learner = new POSBaselineLearner();
        FeatureVector fv = learner.classify(new int[]{}, new double[]{});
        assertEquals(0, fv.featuresSize());
    }
@Test
    public void testScoresReturnsNull() {
        POSBaselineLearner learner = new POSBaselineLearner();
        ScoreSet scores = learner.scores(new int[]{}, new double[]{});
        assertNull(scores);
    }
@Test
    public void testTextualWriteStreamOutput() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = new Classifier() {
            public String discreteValue(Object o) {
                return ((Word) o).form;
            }
        };
        learner.labeler = new Classifier() {
            public String discreteValue(Object o) {
                return ((Word) o).tag;
            }
        };

        Word word = new Word("eat");
        word.tag = "VB";
        learner.learn(word);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream print = new PrintStream(outputStream);
        learner.write(print);

        String output = outputStream.toString();

        assertTrue(output.contains("eat:"));
        assertTrue(output.contains("VB(1)"));
    }
@Test
    public void testBinaryWriteAndReadPreservesData() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = new Classifier() {
            public String discreteValue(Object o) {
                return ((Word) o).form;
            }
        };
        learner.labeler = new Classifier() {
            public String discreteValue(Object o) {
                return ((Word) o).tag;
            }
        };

        Word word1 = new Word("book"); word1.tag = "VB";
        Word word2 = new Word("book"); word2.tag = "NN";

        learner.learn(word1);
        learner.learn(word2);

        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
        ExceptionlessOutputStream eos = new ExceptionlessOutputStream(outBytes);
        learner.write(eos);

        POSBaselineLearner newLearner = new POSBaselineLearner();
        newLearner.extractor = learner.extractor;
        newLearner.labeler = learner.labeler;

        ByteArrayInputStream inBytes = new ByteArrayInputStream(outBytes.toByteArray());
        ExceptionlessInputStream eis = new ExceptionlessInputStream(inBytes);
        newLearner.read(eis);

        assertTrue(newLearner.observed("book"));
        assertEquals(2, newLearner.observedCount("book"));

        String prediction = newLearner.discreteValue(word1);
        assertTrue(prediction.equals("VB") || prediction.equals("NN"));
    }
@Test
    public void testObservedReturnsFalseForUnseenWord() {
        POSBaselineLearner learner = new POSBaselineLearner();
        boolean result = learner.observed("unseen");
        assertFalse(result);
    }
@Test
    public void testObservedCountReturnsZeroForUnseenWord() {
        POSBaselineLearner learner = new POSBaselineLearner();
        int count = learner.observedCount("ghost");
        assertEquals(0, count);
    }
@Test
    public void testAllowableTagsEmptyForUnseenAndNonSpecialWord() {
        POSBaselineLearner learner = new POSBaselineLearner();
        Set<String> tags = learner.allowableTags("nonsense");
        assertTrue(tags.isEmpty());
    }
@Test
    public void testLearnEmptyFormAndTag() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = new Classifier() {
            public String discreteValue(Object o) {
                return "";
            }
        };
        learner.labeler = new Classifier() {
            public String discreteValue(Object o) {
                return "";
            }
        };

        Word word = new Word("");
        word.tag = "";

        learner.learn(word);
        String prediction = learner.discreteValue(word);

        assertEquals("", prediction);
        assertTrue(learner.observed(""));
        assertEquals(1, learner.observedCount(""));
        assertTrue(learner.allowableTags("").contains(""));
    }
@Test
    public void testLearnNullExtractorReturnsNull() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = new Classifier() {
            public String discreteValue(Object o) {
                return null;
            }
        };
        learner.labeler = new Classifier() {
            public String discreteValue(Object o) {
                return "NN";
            }
        };

        Word word = new Word("test");
        word.tag = "NN";

        learner.learn(word);
        String prediction = learner.discreteValue(word);
        assertEquals("UNKNOWN", prediction);
    }
@Test
    public void testLearnNullLabelerReturnsNull() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = new Classifier() {
            public String discreteValue(Object o) {
                return "hello";
            }
        };
        learner.labeler = new Classifier() {
            public String discreteValue(Object o) {
                return null;
            }
        };

        Word word = new Word("hello");
        word.tag = null;

        learner.learn(word);
        String prediction = learner.discreteValue(word);
        assertNull(prediction);
    }
@Test
    public void testLearnMultipleLabelsSameFrequency() {
        POSBaselineLearner learner = new POSBaselineLearner();

        learner.extractor = new Classifier() {
            public String discreteValue(Object o) {
                return "tieWord";
            }
        };
        learner.labeler = new Classifier() {
            private boolean toggle = false;
            public String discreteValue(Object o) {
                toggle = !toggle;
                return toggle ? "NN" : "VB";
            }
        };

        Word word1 = new Word("tieWord"); word1.tag = "NN";
        Word word2 = new Word("tieWord"); word2.tag = "VB";

        learner.learn(word1);
        learner.learn(word2);

        String prediction = learner.discreteValue(word1);

        
        assertTrue(prediction.equals("NN") || prediction.equals("VB"));
    }
@Test
    public void testComputePredictionWithNonWordInstance() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = new Classifier() {
            public String discreteValue(Object o) {
                return "Oops";
            }
        };
        learner.labeler = new Classifier() {
            public String discreteValue(Object o) {
                return "NN";
            }
        };

        Object nonWordObject = new Object();
        String prediction = learner.discreteValue(nonWordObject);
        assertEquals("UNKNOWN", prediction);
    }
@Test
    public void testTextWriteOrderingOfTagsByFrequency() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = new Classifier() {
            public String discreteValue(Object o) {
                return "walk";
            }
        };
        learner.labeler = new Classifier() {
            public String discreteValue(Object o) {
                return ((Word) o).tag;
            }
        };

        Word w1 = new Word("walk"); w1.tag = "VB";
        Word w2 = new Word("walk"); w2.tag = "VB";
        Word w3 = new Word("walk"); w3.tag = "NN";
        Word w4 = new Word("walk"); w4.tag = "TO";

        learner.learn(w1);
        learner.learn(w2);
        learner.learn(w3);
        learner.learn(w4);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(out);
        learner.write(ps);

        String output = out.toString();
        assertTrue(output.contains("walk:"));
        assertTrue(output.indexOf("VB(2)") < output.indexOf("NN(1)"));
        assertTrue(output.indexOf("NN(1)") < output.indexOf("TO(1)"));
    }
@Test
    public void testReadFromEmptyMapBinaryStream() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = new Classifier() {
            public String discreteValue(Object o) { return "x"; }
        };
        learner.labeler = new Classifier() {
            public String discreteValue(Object o) { return "y"; }
        };

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExceptionlessOutputStream eos = new ExceptionlessOutputStream(baos);
        learner.write(eos);

        POSBaselineLearner emptyLearner = new POSBaselineLearner();
        emptyLearner.extractor = learner.extractor;
        emptyLearner.labeler = learner.labeler;

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ExceptionlessInputStream eis = new ExceptionlessInputStream(bais);
        emptyLearner.read(eis);

        assertFalse(emptyLearner.observed("x"));
    }
@Test
    public void testAllowableTagsNullExtractorAndLabeler() {
        POSBaselineLearner learner = new POSBaselineLearner();

        learner.extractor = new Classifier() {
            public String discreteValue(Object o) {
                return null;
            }
        };
        learner.labeler = new Classifier() {
            public String discreteValue(Object o) {
                return null;
            }
        };

        Word word = new Word("something");
        word.tag = null;

        Set<String> tags = learner.allowableTags(null);
        assertTrue(tags.isEmpty());
    }
@Test
    public void testLearnWithNonWordObject() {
        POSBaselineLearner learner = new POSBaselineLearner();

        learner.extractor = new Classifier() {
            public String discreteValue(Object o) {
                return "other";
            }
        };
        learner.labeler = new Classifier() {
            public String discreteValue(Object o) {
                return "NN";
            }
        };

        Object obj = new Integer(42);
        learner.learn(obj);
        String prediction = learner.discreteValue(obj);

        assertEquals("NN", prediction);
        assertTrue(learner.observed("other"));
    }
@Test
    public void testFeatureValueReturnsUnknownTagForUnseenWord() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = new Classifier() {
            public String discreteValue(Object o) {
                return ((Word) o).form;
            }
        };
        learner.labeler = new Classifier() {
            public String discreteValue(Object o) {
                return ((Word) o).tag;
            }
        };

        Word word = new Word("unseenWord");
        word.tag = "VB";

        Feature f = learner.featureValue(word);
        assertEquals("UNKNOWN", ((DiscretePrimitiveStringFeature) f).getStringValue());
    }
@Test
    public void testAllowableTagsWithLooksLikeNumberButHasLetter() {
        POSBaselineLearner learner = new POSBaselineLearner();

        String input = "12a34";
        Set<String> tags = learner.allowableTags(input);

        assertNotNull(tags);
        assertTrue(tags.isEmpty());
    }
@Test
    public void testAllowableTagsWithNullInputReturnsEmptySet() {
        POSBaselineLearner learner = new POSBaselineLearner();
        Set<String> tags = learner.allowableTags(null);

        assertNotNull(tags);
        assertTrue(tags.isEmpty());
    }
@Test
    public void testWriteTextSerializationWithTagSortOrder() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = new Classifier() {
            public String discreteValue(Object o) {
                return ((Word) o).form;
            }
        };
        learner.labeler = new Classifier() {
            public String discreteValue(Object o) {
                return ((Word) o).tag;
            }
        };

        Word word1 = new Word("test"); word1.tag = "ZZZ";
        Word word2 = new Word("test"); word2.tag = "AAA";
        Word word3 = new Word("test"); word3.tag = "MMM";

        learner.learn(word1);
        learner.learn(word2);
        learner.learn(word2);
        learner.learn(word3);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        learner.write(ps);

        String output = baos.toString();

        assertTrue(output.contains("AAA(2)"));
        assertTrue(output.contains("MMM(1)"));
        assertTrue(output.contains("ZZZ(1)"));
        assertTrue(output.indexOf("AAA(2)") < output.indexOf("MMM(1)"));
        assertTrue(output.indexOf("MMM(1)") < output.indexOf("ZZZ(1)"));
    }
@Test
    public void testBinaryReadWithZeroEntries() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = new Classifier() {
            public String discreteValue(Object o) { return "x"; }
        };
        learner.labeler = new Classifier() {
            public String discreteValue(Object o) { return "y"; }
        };

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ExceptionlessOutputStream eos = new ExceptionlessOutputStream(bos);
        eos.writeInt(0);
        eos.close();

        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ExceptionlessInputStream eis = new ExceptionlessInputStream(bis);

        learner.read(eis);
        assertFalse(learner.observed("x"));
    }
@Test
    public void testLearnWithEmptyStringAsFormAndValidTag() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = new Classifier() {
            public String discreteValue(Object o) {
                return "";
            }
        };
        learner.labeler = new Classifier() {
            public String discreteValue(Object o) {
                return "NN";
            }
        };

        Word word = new Word("");
        word.tag = "NN";

        learner.learn(word);
        assertTrue(learner.observed(""));
        assertEquals(1, learner.observedCount(""));
        assertTrue(learner.allowableTags("").contains("NN"));
    }
@Test
    public void testLearnWithBothExtractorAndLabelerReturningEmptyString() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = new Classifier() {
            public String discreteValue(Object o) {
                return "";
            }
        };
        learner.labeler = new Classifier() {
            public String discreteValue(Object o) {
                return "";
            }
        };

        learner.learn(new Word(""));
        String prediction = learner.discreteValue(new Word(""));
        assertEquals("", prediction);
    }
@Test
    public void testLearnWithNullExampleObject() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = new Classifier() {
            public String discreteValue(Object o) {
                return null;
            }
        };
        learner.labeler = new Classifier() {
            public String discreteValue(Object o) {
                return null;
            }
        };

        learner.learn(null);
        String prediction = learner.discreteValue(null);
        assertNull(prediction);
    }
@Test
    public void testObservedFalseForEmptyStringKey() {
        POSBaselineLearner learner = new POSBaselineLearner();
        assertFalse(learner.observed(""));
    }
@Test
    public void testObservedCountForEmptyStringWhenUnlearned() {
        POSBaselineLearner learner = new POSBaselineLearner();
        assertEquals(0, learner.observedCount(""));
    }
@Test
    public void testLearnWithSpecialCharactersAsForm() {
        POSBaselineLearner learner = new POSBaselineLearner();

        learner.extractor = new Classifier() {
            public String discreteValue(Object o) {
                return "@$%!#";
            }
        };

        learner.labeler = new Classifier() {
            public String discreteValue(Object o) {
                return "SYM";
            }
        };

        Word word = new Word("@$%!#");
        word.tag = "SYM";

        learner.learn(word);
        String prediction = learner.discreteValue(word);

        assertEquals("SYM", prediction);
        assertTrue(learner.observed("@$%!#"));
        assertEquals(1, learner.observedCount("@$%!#"));
    }
@Test
    public void testComputePredictionReturnsCorrectTagWhenMultipleWithIdenticalCountsAndOrderingMatters() {
        POSBaselineLearner learner = new POSBaselineLearner();

        learner.extractor = new Classifier() {
            public String discreteValue(Object o) {
                return "balanced";
            }
        };
        learner.labeler = new Classifier() {
            private boolean flip = false;
            public String discreteValue(Object o) {
                flip = !flip;
                return flip ? "A" : "B";
            }
        };

        Word w1 = new Word("balanced"); w1.tag = "A";
        Word w2 = new Word("balanced"); w2.tag = "B";

        learner.learn(w1);
        learner.learn(w2);

        String prediction = learner.discreteValue(w1);
        assertTrue(prediction.equals("A") || prediction.equals("B"));
    }
@Test
    public void testFeatureValueReturnsNullPrediction() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = new Classifier() {
            public String discreteValue(Object o) {
                return null;
            }
        };
        learner.labeler = new Classifier() {
            public String discreteValue(Object o) {
                return null;
            }
        };

        Word word = new Word(null);
        word.tag = null;

        Feature feature = learner.featureValue(word);
        assertNull(((DiscretePrimitiveStringFeature) feature).getStringValue());
    }
@Test
    public void testWriteEmptyTableTextOutput() {
        POSBaselineLearner learner = new POSBaselineLearner();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(out);

        learner.write(printStream);
        String result = out.toString();

        assertNotNull(result);
        assertTrue(result.trim().isEmpty());
    }
@Test
    public void testWriteEmptyTableBinaryOutput() {
        POSBaselineLearner learner = new POSBaselineLearner();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ExceptionlessOutputStream eos = new ExceptionlessOutputStream(out);
        learner.write(eos);

        byte[] bytes = out.toByteArray();
        assertNotNull(bytes);
        assertTrue(bytes.length > 0); 
    }
@Test
    public void testReadRestoresTableFromBinary() {
        POSBaselineLearner learner1 = new POSBaselineLearner();
        learner1.extractor = new Classifier() {
            public String discreteValue(Object o) {
                return "rain";
            }
        };
        learner1.labeler = new Classifier() {
            public String discreteValue(Object o) {
                return "VB";
            }
        };

        Word word = new Word("rain");
        word.tag = "VB";
        learner1.learn(word);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ExceptionlessOutputStream eos = new ExceptionlessOutputStream(out);
        learner1.write(eos);

        POSBaselineLearner learner2 = new POSBaselineLearner();
        learner2.extractor = learner1.extractor;
        learner2.labeler = learner1.labeler;

        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        ExceptionlessInputStream eis = new ExceptionlessInputStream(in);
        learner2.read(eis);

        assertEquals("VB", learner2.discreteValue(new Word("rain")));
        assertTrue(learner2.observed("rain"));
        assertEquals(1, learner2.observedCount("rain"));
    }
@Test
    public void testNullExtractorInPredictionPath() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = new Classifier() {
            public String discreteValue(Object o) {
                return null;
            }
        };
        learner.labeler = new Classifier() {
            public String discreteValue(Object o) {
                return "NN";
            }
        };

        Word w = new Word("test");
        w.tag = "NN";

        learner.learn(w);
        String result = learner.discreteValue(w);
        assertEquals("UNKNOWN", result);
    }
@Test
    public void testNullFormInLooksLikeNumber() {
        boolean result = POSBaselineLearner.looksLikeNumber(null);
        assertFalse(result);
    }
@Test
    public void testEmptyStringInLooksLikeNumber() {
        boolean result = POSBaselineLearner.looksLikeNumber("");
        assertFalse(result);
    }
@Test
    public void testSpecialCharactersLooksLikeNumberOnlyDotsAndDigits() {
        boolean result = POSBaselineLearner.looksLikeNumber(".1.2-3,4");
        assertTrue(result);
    }
@Test
    public void testAllowableTagsForNullForm() {
        POSBaselineLearner learner = new POSBaselineLearner();
        Set<String> tags = learner.allowableTags(null);
        assertNotNull(tags);
        assertTrue(tags.isEmpty());
    }
@Test
    public void testClassifyWithNullExampleReturnsDefaultVector() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = new Classifier() {
            public String discreteValue(Object o) {
                return null;
            }
        };
        learner.labeler = new Classifier() {
            public String discreteValue(Object o) {
                return null;
            }
        };

        FeatureVector fv = learner.classify(null);
        assertNotNull(fv);
        assertEquals(1, fv.featuresSize());
        assertNull(fv.getFeature(0).getStringValue());
    }
@Test
    public void testFeatureValueForNullExample() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = new Classifier() {
            public String discreteValue(Object o) {
                return null;
            }
        };
        learner.labeler = new Classifier() {
            public String discreteValue(Object o) {
                return null;
            }
        };

        Feature f = learner.featureValue(null);
        assertNotNull(f);
        assertNull(f.getStringValue());
    }
@Test
    public void testScoresMethodReturnsNullAlways() {
        POSBaselineLearner learner = new POSBaselineLearner();
        ScoreSet result = learner.scores(new int[]{0, 1}, new double[]{1.0, 2.0});
        assertNull(result);
    }
@Test
    public void testObservedAndObservedCountCaseSensitivity() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = new Classifier() {
            public String discreteValue(Object o) {
                return "House";
            }
        };
        learner.labeler = new Classifier() {
            public String discreteValue(Object o) {
                return "NN";
            }
        };
        Word word = new Word("House");
        word.tag = "NN";

        learner.learn(word);
        assertTrue(learner.observed("House"));
        assertFalse(learner.observed("house"));
        assertEquals(1, learner.observedCount("House"));
        assertEquals(0, learner.observedCount("house"));
    }
@Test
    public void testLearnSameFormDifferentTagsEqualCountReturnsFirstByOrder() {
        POSBaselineLearner learner = new POSBaselineLearner();

        learner.extractor = new Classifier() {
            public String discreteValue(Object o) {
                return "bank";
            }
        };

        learner.labeler = new Classifier() {
            private boolean toggle = true;
            public String discreteValue(Object o) {
                toggle = !toggle;
                return toggle ? "NN" : "VB";
            }
        };

        Word w1 = new Word("bank"); w1.tag = "NN";
        Word w2 = new Word("bank"); w2.tag = "VB";

        learner.learn(w1);
        learner.learn(w2);

        String prediction = learner.discreteValue(w1);

        
        assertTrue(prediction.equals("NN") || prediction.equals("VB"));
    }
@Test
    public void testDiscrepantExtractorAndLabelerCaseSensitivityEffect() {
        POSBaselineLearner learner = new POSBaselineLearner();

        learner.extractor = new Classifier() {
            public String discreteValue(Object o) {
                return "Apple";
            }
        };

        learner.labeler = new Classifier() {
            public String discreteValue(Object o) {
                return "NNP";
            }
        };

        Word word = new Word("apple");
        word.tag = "NNP";

        learner.learn(word);

        learner.extractor = new Classifier() {
            public String discreteValue(Object o) {
                return "apple";
            }
        };

        String prediction = learner.discreteValue(word);

        assertEquals("UNKNOWN", prediction);
    }
@Test
    public void testBinarySerializationOfMultipleTagsInSortedOrder() {
        POSBaselineLearner learner = new POSBaselineLearner();

        learner.extractor = new Classifier() {
            public String discreteValue(Object o) {
                return "eat";
            }
        };

        learner.labeler = new Classifier() {
            public String discreteValue(Object o) {
                if (((Word) o).tag.equals("VBP")) return "VBP";
                if (((Word) o).tag.equals("NN")) return "NN";
                return "OTHER";
            }
        };

        Word w1 = new Word("eat"); w1.tag = "VBP";
        Word w2 = new Word("eat"); w2.tag = "NN";
        Word w3 = new Word("eat"); w3.tag = "NN";

        learner.learn(w1);
        learner.learn(w2);
        learner.learn(w3);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(out);
        learner.write(ps);
        String output = out.toString();

        assertTrue(output.contains("eat:"));
        assertTrue(output.indexOf("NN(2)") < output.indexOf("VBP(1)"));
    }
@Test
    public void testWriteAndReadBinaryEmptyTableIntegrityCheck() {
        POSBaselineLearner learner = new POSBaselineLearner();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ExceptionlessOutputStream eos = new ExceptionlessOutputStream(out);
        learner.write(eos);

        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        ExceptionlessInputStream eis = new ExceptionlessInputStream(in);

        POSBaselineLearner reader = new POSBaselineLearner();
        reader.extractor = new Classifier() {
            public String discreteValue(Object o) {
                return "irrelevant";
            }
        };
        reader.labeler = new Classifier() {
            public String discreteValue(Object o) {
                return "tag";
            }
        };

        reader.read(eis);
        assertEquals(0, reader.observedCount("irrelevant"));
    }
@Test
    public void testLearnNullFormWordHandledGracefully() {
        POSBaselineLearner learner = new POSBaselineLearner();

        learner.extractor = new Classifier() {
            public String discreteValue(Object o) {
                return null;
            }
        };

        learner.labeler = new Classifier() {
            public String discreteValue(Object o) {
                return "CD";
            }
        };

        Word word = new Word(null);
        word.tag = "CD";

        learner.learn(word);
        String prediction = learner.discreteValue(word);
        assertEquals("UNKNOWN", prediction);
    }
@Test
    public void testClassifyReturnsFeatureWithCorrectPackageAndName() {
        POSBaselineLearner learner = new POSBaselineLearner("BaseTagger");
        learner.extractor = new Classifier() {
            public String discreteValue(Object o) {
                return "taggedForm";
            }
        };
        learner.labeler = new Classifier() {
            public String discreteValue(Object o) {
                return "VBD";
            }
        };

        Word word = new Word("taggedForm");
        word.tag = "VBD";

        learner.learn(word);
        Feature feature = learner.featureValue(word);

        assertEquals("VBD", feature.getStringValue());
        assertEquals("edu.illinois.cs.cogcomp.pos", feature.containingPackage);
        assertEquals("BaseTagger", feature.identifier);
    }
@Test
    public void testClassifyWithEmptyWordReturnsUnknown() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = new Classifier() {
            public String discreteValue(Object o) {
                if (o instanceof Word) return ((Word) o).form;
                return null;
            }
        };
        learner.labeler = new Classifier() {
            public String discreteValue(Object o) {
                return "UNK";
            }
        };

        Word word = new Word("");
        word.tag = "UNK";

        String prediction = learner.discreteValue(word);
        assertEquals("UNKNOWN", prediction);
    }
@Test
    public void testDisjointLearnAndPredictSymbols() {
        POSBaselineLearner learner = new POSBaselineLearner();

        learner.extractor = new Classifier() {
            public String discreteValue(Object o) {
                return "::::";
            }
        };

        learner.labeler = new Classifier() {
            public String discreteValue(Object o) {
                return "SYM";
            }
        };

        Word w = new Word("::::");
        w.tag = "SYM";

        learner.learn(w);

        Word test = new Word("!!!!"); 
        test.tag = "SYM";

        learner.extractor = new Classifier() {
            public String discreteValue(Object o) {
                return "!!!!";
            }
        };

        String prediction = learner.discreteValue(test);
        assertEquals("UNKNOWN", prediction);
    }
@Test
    public void testFeatureValueWithPredictedCDForNumericString() {
        POSBaselineLearner learner = new POSBaselineLearner();

        learner.extractor = new Classifier() {
            public String discreteValue(Object o) {
                return "12345";
            }
        };

        learner.labeler = new Classifier() {
            public String discreteValue(Object o) {
                return "NUM";
            }
        };

        Word w = new Word("12345");
        w.tag = "NUM";

        Feature feature = learner.featureValue(w);
        assertEquals("CD", feature.getStringValue());
    }
@Test
    public void testLearnSameTagWithDifferentForms() {
        POSBaselineLearner learner = new POSBaselineLearner();
        
        learner.extractor = new Classifier() {
            public String discreteValue(Object o) {
                return ((Word) o).form;
            }
        };
        
        learner.labeler = new Classifier() {
            public String discreteValue(Object o) {
                return "VB";
            }
        };

        Word word1 = new Word("run");
        word1.tag = "VB";

        Word word2 = new Word("sprint");
        word2.tag = "VB";

        learner.learn(word1);
        learner.learn(word2);

        assertTrue(learner.observed("run"));
        assertTrue(learner.observed("sprint"));
        assertEquals(1, learner.observedCount("run"));
        assertEquals(1, learner.observedCount("sprint"));

        String prediction1 = learner.discreteValue(word1);
        String prediction2 = learner.discreteValue(word2);

        assertEquals("VB", prediction1);
        assertEquals("VB", prediction2);
    }
@Test
    public void testPredictionForOverlayedCountsEqualTagFrequencySorted() {
        POSBaselineLearner learner = new POSBaselineLearner();

        learner.extractor = new Classifier() {
            public String discreteValue(Object o) {
                return "formX";
            }
        };

        learner.labeler = new Classifier() {
            private boolean toggle = false;
            public String discreteValue(Object o) {
                toggle = !toggle;
                return toggle ? "JJ" : "NN";
            }
        };

        Word w1 = new Word("formX");
        w1.tag = "JJ";

        Word w2 = new Word("formX");
        w2.tag = "NN";

        learner.learn(w1);
        learner.learn(w2);

        String prediction = learner.discreteValue(w1);

        
        assertTrue(prediction.equals("JJ") || prediction.equals("NN"));
    }
@Test
    public void testLearnWordWithUnicodeCharacters() {
        POSBaselineLearner learner = new POSBaselineLearner();

        learner.extractor = new Classifier() {
            public String discreteValue(Object o) {
                return "💡📚";
            }
        };

        learner.labeler = new Classifier() {
            public String discreteValue(Object o) {
                return "EMOJI";
            }
        };

        Word word = new Word("💡📚");
        word.tag = "EMOJI";

        learner.learn(word);
        assertTrue(learner.observed("💡📚"));
        assertEquals(1, learner.observedCount("💡📚"));
        assertEquals("EMOJI", learner.discreteValue(word));
    }
@Test
    public void testFeatureVectorContainsSingleFeature() {
        POSBaselineLearner learner = new POSBaselineLearner("FTest");

        learner.extractor = new Classifier() {
            public String discreteValue(Object o) {
                return "wordX";
            }
        };

        learner.labeler = new Classifier() {
            public String discreteValue(Object o) {
                return "TAGX";
            }
        };

        Word word = new Word("wordX");
        word.tag = "TAGX";
        learner.learn(word);

        FeatureVector fv = learner.classify(word);

        assertNotNull(fv);
        assertEquals(1, fv.featuresSize());
        Feature feature = fv.getFeature(0);
        assertEquals("TAGX", feature.getStringValue());
        assertEquals("edu.illinois.cs.cogcomp.pos", feature.containingPackage);
        assertEquals("FTest", feature.identifier);
    }
@Test
    public void testParametersConstructorCreatesEmptyLearner() {
        POSBaselineLearner.Parameters params = new POSBaselineLearner.Parameters();
        POSBaselineLearner learner = new POSBaselineLearner(params);

        learner.extractor = new Classifier() {
            public String discreteValue(Object o) {
                return "par";
            }
        };

        learner.labeler = new Classifier() {
            public String discreteValue(Object o) {
                return "ABC";
            }
        };

        Word word = new Word("par");
        word.tag = "ABC";

        learner.learn(word);
        assertTrue(learner.observed("par"));
        assertEquals(1, learner.observedCount("par"));
        assertEquals("ABC", learner.discreteValue(word));
    }
@Test
    public void testMultipleLearnsSameFormDifferentTagsDifferentCounts() {
        POSBaselineLearner learner = new POSBaselineLearner();

        learner.extractor = new Classifier() {
            public String discreteValue(Object o) {
                return "walk";
            }
        };

        learner.labeler = new Classifier() {
            public String discreteValue(Object o) {
                return ((Word) o).tag;
            }
        };

        Word w1 = new Word("walk"); w1.tag = "VB";
        Word w2 = new Word("walk"); w2.tag = "VB";
        Word w3 = new Word("walk"); w3.tag = "NN";

        learner.learn(w1);
        learner.learn(w2);
        learner.learn(w3);

        String prediction = learner.discreteValue(w1);

        assertEquals("VB", prediction);
        assertTrue(learner.allowableTags("walk").contains("VB"));
        assertTrue(learner.allowableTags("walk").contains("NN"));
    }
@Test
    public void testLearnWithLabelerReturningEmptyString() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = new Classifier() {
            public String discreteValue(Object o) { return "data"; }
        };
        learner.labeler = new Classifier() {
            public String discreteValue(Object o) { return ""; }
        };

        Word word = new Word("data");
        word.tag = "";

        learner.learn(word);
        assertTrue(learner.observed("data"));
        assertEquals(1, learner.observedCount("data"));
        assertTrue(learner.allowableTags("data").contains(""));
        assertEquals("", learner.discreteValue(word));
    }
@Test
    public void testLearnCalledTwiceWithSameFormTagGivesSamePrediction() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = new Classifier() {
            public String discreteValue(Object o) { return "walk"; }
        };
        learner.labeler = new Classifier() {
            public String discreteValue(Object o) { return "VB"; }
        };

        Word word = new Word("walk");
        word.tag = "VB";

        learner.learn(word);
        learner.learn(word);

        String prediction = learner.discreteValue(word);
        assertEquals("VB", prediction);
        assertEquals(2, learner.observedCount("walk"));
    }
@Test
    public void testLearnWithObjectNotWordType() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = new Classifier() {
            public String discreteValue(Object o) {
                if (o instanceof String) return (String) o;
                return null;
            }
        };
        learner.labeler = new Classifier() {
            public String discreteValue(Object o) {
                return "NN";
            }
        };

        Object input = "customInput";
        learner.learn(input);

        assertTrue(learner.observed("customInput"));
        assertEquals(1, learner.observedCount("customInput"));
        assertEquals("NN", learner.discreteValue(input));
    }
@Test
    public void testAllowableTagsReturnsCorrectForUnlearnedNumberLikeString() {
        POSBaselineLearner learner = new POSBaselineLearner();
        String form = "9,999";

        Set<String> tags = learner.allowableTags(form);

        assertEquals(1, tags.size());
        assertTrue(tags.contains("CD"));
    }
@Test
    public void testAllowableTagsReturnsEmptyForNullForm() {
        POSBaselineLearner learner = new POSBaselineLearner();
        Set<String> tags = learner.allowableTags(null);
        assertNotNull(tags);
        assertTrue(tags.isEmpty());
    }
@Test
    public void testLooksLikeNumberWithSingleDigitOnly() {
        boolean result = POSBaselineLearner.looksLikeNumber("8");
        assertTrue(result);
    }
@Test
    public void testLooksLikeNumberWithDecimalAndNegativeSign() {
        boolean result = POSBaselineLearner.looksLikeNumber("-0.5");
        assertTrue(result);
    }
@Test
    public void testLooksLikeNumberWithMixedInvalidCharacters() {
        boolean result = POSBaselineLearner.looksLikeNumber("12a4");
        assertFalse(result);
    }
@Test
    public void testLooksLikeNumberWithWhitespaceCharacters() {
        boolean result = POSBaselineLearner.looksLikeNumber(" 123 ");
        assertFalse(result);
    }
@Test
    public void testWordFormContainingOnlyWhitespace() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = new Classifier() {
            public String discreteValue(Object o) { return "   "; }
        };
        learner.labeler = new Classifier() {
            public String discreteValue(Object o) { return "SPACE"; }
        };

        Word word = new Word("   ");
        word.tag = "SPACE";

        learner.learn(word);
        assertTrue(learner.observed("   "));
        assertEquals("SPACE", learner.discreteValue(word));
    }
@Test
    public void testFeatureVectorReturnedForUnseenWordHasUNKNOWNStringValue() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = new Classifier() {
            public String discreteValue(Object o) { return ((Word) o).form; }
        };
        learner.labeler = new Classifier() {
            public String discreteValue(Object o) { return ((Word) o).tag; }
        };

        Word word = new Word("newform");
        word.tag = "VB";

        FeatureVector fv = learner.classify(word);
        Feature feature = fv.getFeature(0);
        String value = feature.getStringValue();

        assertEquals("UNKNOWN", value);
    }
@Test
    public void testFeatureVectorReturnedForNumberWordHasCDTag() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = new Classifier() {
            public String discreteValue(Object o) { return ((Word) o).form; }
        };
        learner.labeler = new Classifier() {
            public String discreteValue(Object o) { return ((Word) o).tag; }
        };

        Word word = new Word("23.00");
        word.tag = "CD";

        FeatureVector fv = learner.classify(word);
        Feature feature = fv.getFeature(0);
        String value = feature.getStringValue();

        assertEquals("CD", value);
    }
@Test
    public void testObservedReturnsFalseWithoutTraining() {
        POSBaselineLearner learner = new POSBaselineLearner();

        boolean result = learner.observed("never_seen_before");
        assertFalse(result);
    }
@Test
    public void testObservedCountReturnsZeroWithoutTraining() {
        POSBaselineLearner learner = new POSBaselineLearner();

        int count = learner.observedCount("not_seen");
        assertEquals(0, count);
    } 
}