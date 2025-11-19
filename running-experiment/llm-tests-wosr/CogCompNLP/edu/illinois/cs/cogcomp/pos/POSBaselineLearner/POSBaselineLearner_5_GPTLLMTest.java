public class POSBaselineLearner_5_GPTLLMTest { 

 @Test
    public void testLearnAndPredictSingle() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
            public String discreteValue(Object example) {
                return ((Word) example).getValue();
            }
        };
        learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
            public String discreteValue(Object example) {
                return ((Word) example).tag();
            }
        };

        Word word = new Word("dog");
        word.setTag("NN");

        learner.learn(word);
        String prediction = learner.discreteValue(word);
        assertEquals("NN", prediction);
    }
@Test
    public void testLearnAndPredictMultipleTags() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
            public String discreteValue(Object example) {
                return ((Word) example).getValue();
            }
        };
        learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
            public String discreteValue(Object example) {
                return ((Word) example).tag();
            }
        };

        Word word1 = new Word("run");
        word1.setTag("VB");
        Word word2 = new Word("run");
        word2.setTag("NN");
        Word word3 = new Word("run");
        word3.setTag("VB");

        learner.learn(word1);
        learner.learn(word2);
        learner.learn(word3);

        String prediction = learner.discreteValue(word1);
        assertEquals("VB", prediction);

        Set<String> tags = learner.allowableTags("run");
        assertTrue(tags.contains("NN"));
        assertTrue(tags.contains("VB"));

        int count = learner.observedCount("run");
        assertEquals(3, count);
    }
@Test
    public void testLooksLikeNumberTrue() {
        boolean result = POSBaselineLearner.looksLikeNumber("123.45");
        assertTrue(result);
    }
@Test
    public void testLooksLikeNumberFalse() {
        boolean result = POSBaselineLearner.looksLikeNumber("hello123");
        assertFalse(result);
    }
@Test
    public void testPredictionForSemicolon() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
            public String discreteValue(Object example) {
                return ((Word) example).getValue();
            }
        };
        learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
            public String discreteValue(Object example) {
                return ((Word) example).tag();
            }
        };

        Word word = new Word(";");
        word.setTag(".");
        String prediction = learner.discreteValue(word);
        assertEquals(":", prediction);
    }
@Test
    public void testPredictionForUnseenNumber() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
            public String discreteValue(Object example) {
                return ((Word) example).getValue();
            }
        };
        learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
            public String discreteValue(Object example) {
                return ((Word) example).tag();
            }
        };

        Word word = new Word("1234");
        word.setTag("CD");
        String prediction = learner.discreteValue(word);
        assertEquals("CD", prediction);
    }
@Test
    public void testPredictionForUnseenOther() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
            public String discreteValue(Object example) {
                return ((Word) example).getValue();
            }
        };
        learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
            public String discreteValue(Object example) {
                return ((Word) example).tag();
            }
        };

        Word word = new Word("foobar");
        word.setTag("NN");
        String prediction = learner.discreteValue(word);
        assertEquals("UNKNOWN", prediction);
    }
@Test
    public void testForgetClearsTable() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
            public String discreteValue(Object example) {
                return ((Word) example).getValue();
            }
        };
        learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
            public String discreteValue(Object example) {
                return ((Word) example).tag();
            }
        };

        Word word = new Word("river");
        word.setTag("NN");
        learner.learn(word);
        assertTrue(learner.observed("river"));
        learner.forget();
        assertFalse(learner.observed("river"));
    }
@Test
    public void testFeatureValueReturnsCorrectTag() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
            public String discreteValue(Object example) {
                return ((Word) example).getValue();
            }
        };
        learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
            public String discreteValue(Object example) {
                return ((Word) example).tag();
            }
        };

        Word word = new Word("tree");
        word.setTag("NN");
        learner.learn(word);
        Feature feature = learner.featureValue(word);
        assertEquals("NN", feature.getStringValue());
    }
@Test
    public void testClassifyReturnsSingleFeature() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
            public String discreteValue(Object example) {
                return ((Word) example).getValue();
            }
        };
        learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
            public String discreteValue(Object example) {
                return ((Word) example).tag();
            }
        };

        Word word = new Word("sky");
        word.setTag("NN");
        learner.learn(word);
        FeatureVector fv = learner.classify(word);
        assertEquals(1, fv.featuresSize());
        assertEquals("NN", fv.getFeature(0).getStringValue());
    }
@Test
    public void testEmptyCloneCreatesNewInstance() {
        POSBaselineLearner original = new POSBaselineLearner();
        Learner clone = original.emptyClone();
        assertNotNull(clone);
        assertTrue(clone instanceof POSBaselineLearner);
        assertNotSame(original, clone);
    }
@Test
    public void testObservedCountReturnsZeroForUnseen() {
        POSBaselineLearner learner = new POSBaselineLearner();
        int count = learner.observedCount("notSeenWord");
        assertEquals(0, count);
    }
@Test
    public void testReadAndWriteBinaryPreservesLearner() throws IOException {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
            public String discreteValue(Object example) {
                return ((Word) example).getValue();
            }
        };
        learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
            public String discreteValue(Object example) {
                return ((Word) example).tag();
            }
        };

        Word word = new Word("sun");
        word.setTag("NN");
        learner.learn(word);

        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ExceptionlessOutputStream eos = new ExceptionlessOutputStream(byteStream);
        learner.write(eos);
        eos.close();

        byte[] bytes = byteStream.toByteArray();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        ExceptionlessInputStream eis = new ExceptionlessInputStream(inputStream);

        POSBaselineLearner loaded = new POSBaselineLearner();
        loaded.extractor = learner.extractor;
        loaded.labeler = learner.labeler;
        loaded.read(eis);
        eis.close();

        assertEquals("NN", loaded.discreteValue(word));
        assertEquals(1, loaded.observedCount("sun"));
    }
@Test
    public void testWriteTextOutputContainsExpected() {
        POSBaselineLearner learner = new POSBaselineLearner();
        learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
            public String discreteValue(Object example) {
                return ((Word) example).getValue();
            }
        };
        learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
            public String discreteValue(Object example) {
                return ((Word) example).tag();
            }
        };

        Word word = new Word("moon");
        word.setTag("NN");
        learner.learn(word);
        learner.learn(word);
        Word word2 = new Word("moon");
        word2.setTag("VB");
        learner.learn(word2);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(out);
        learner.write(printStream);
        printStream.flush();
        String output = out.toString();

        assertTrue(output.contains("moon:"));
        assertTrue(output.contains("NN(2)"));
        assertTrue(output.contains("VB(1)"));
    }
@Test
    public void testScoresReturnsNull() {
        POSBaselineLearner learner = new POSBaselineLearner();
        assertNull(learner.scores(new int[0], new double[0]));
    }
@Test
    public void testClassifyPrimitiveReturnsEmptyVector() {
        POSBaselineLearner learner = new POSBaselineLearner();
        FeatureVector vector = learner.classify(new int[0], new double[0]);
        assertNotNull(vector);
        assertEquals(0, vector.featuresSize());
    }
@Test
    public void testAllowableTagsForSemicolon() {
        POSBaselineLearner learner = new POSBaselineLearner();
        Set<String> tags = learner.allowableTags(";");
        assertTrue(tags.contains(":"));
    }
@Test
    public void testAllowableTagsForNumber() {
        POSBaselineLearner learner = new POSBaselineLearner();
        Set<String> tags = learner.allowableTags("42.0");
        assertTrue(tags.contains("CD"));
    }
@Test
    public void testGetInputTypeReturnsExpected() {
        POSBaselineLearner learner = new POSBaselineLearner();
        assertEquals("edu.illinois.cs.cogcomp.lbjava.nlp.Word", learner.getInputType());
    }
@Test
public void testLearnOverwritesTagCountCorrectly() {
    POSBaselineLearner learner = new POSBaselineLearner();
    learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return ((Word) example).getValue();
        }
    };
    learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return ((Word) example).tag();
        }
    };

    Word word = new Word("bank");
    word.setTag("NN");
    learner.learn(word);

    word.setTag("VB");
    learner.learn(word);

    String prediction = learner.discreteValue(word);
    assertEquals("NN", prediction); 
    Set<String> tags = learner.allowableTags("bank");
    assertTrue(tags.contains("NN"));
    assertTrue(tags.contains("VB"));
    assertEquals(2, learner.observedCount("bank"));
}
@Test
public void testAllowableTagsUnseenWordReturnsEmptySet() {
    POSBaselineLearner learner = new POSBaselineLearner();
    Set<String> tags = learner.allowableTags("unobservedWord");
    assertNotNull(tags);
    assertTrue(tags.isEmpty() || tags.contains("CD") || tags.contains(":")); 
}
@Test
public void testObservedFalseForUnseenWord() {
    POSBaselineLearner learner = new POSBaselineLearner();
    boolean observed = learner.observed("nonexistent");
    assertFalse(observed);
}
@Test
public void testWritePreservesMultipleWordsSortedOutput() {
    POSBaselineLearner learner = new POSBaselineLearner();
    learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return ((Word) example).getValue();
        }
    };
    learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return ((Word) example).tag();
        }
    };

    Word word1 = new Word("zebra");
    word1.setTag("NN");
    learner.learn(word1);

    Word word2 = new Word("apple");
    word2.setTag("NN");
    learner.learn(word2);

    Word word3 = new Word("apple");
    word3.setTag("VB");
    learner.learn(word3);

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(out);
    learner.write(ps);

    String output = out.toString();
    int indexApple = output.indexOf("apple:");
    int indexZebra = output.indexOf("zebra:");

    assertTrue(indexApple < indexZebra); 
    assertTrue(output.contains("apple:"));
    assertTrue(output.contains("VB(1)"));
}
@Test
public void testWriteBinaryWithEmptyTable() throws IOException {
    POSBaselineLearner learner = new POSBaselineLearner();
    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
    ExceptionlessOutputStream eos = new ExceptionlessOutputStream(byteStream);
    learner.write(eos);
    eos.close();

    byte[] data = byteStream.toByteArray();

    POSBaselineLearner loaded = new POSBaselineLearner();
    loaded.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return ((Word) example).getValue();
        }
    };
    loaded.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return ((Word) example).tag();
        }
    };
    ExceptionlessInputStream eis = new ExceptionlessInputStream(new ByteArrayInputStream(data));
    loaded.read(eis);
    eis.close();

    assertEquals(0, loaded.observedCount("anything"));
}
@Test
public void testLearnWithNullLabelReturnsDefaultCount() {
    POSBaselineLearner learner = new POSBaselineLearner();
    learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return ((Word) example).getValue();
        }
    };
    learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return null;
        }
    };

    Word word = new Word("unknownForm");
    word.setTag(null);

    learner.learn(word);
    int count = learner.observedCount("unknownForm");
    assertEquals(1, count);
}
@Test
public void testDiscreteValueUsesFallbackWhenExtractorReturnsNull() {
    POSBaselineLearner learner = new POSBaselineLearner();
    learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return null;
        }
    };
    learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return ((Word) example).tag();
        }
    };

    Word word = new Word("invalid");
    word.setTag("JJ");

    String result = learner.discreteValue(word);
    assertEquals("UNKNOWN", result); 
}
@Test
public void testObservedCountAccurateWithMultipleTags() {
    POSBaselineLearner learner = new POSBaselineLearner();
    learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return ((Word) example).getValue();
        }
    };
    learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return ((Word) example).tag();
        }
    };

    Word word1 = new Word("live");
    word1.setTag("VB");
    learner.learn(word1);

    Word word2 = new Word("live");
    word2.setTag("JJ");
    learner.learn(word2);

    Word word3 = new Word("live");
    word3.setTag("JJ");
    learner.learn(word3);

    String prediction = learner.discreteValue(word1);
    assertEquals("JJ", prediction);
    int count = learner.observedCount("live");
    assertEquals(3, count);
}
@Test
public void testLearnWithEmptyStringWordForm() {
    POSBaselineLearner learner = new POSBaselineLearner();
    learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return "";
        }
    };
    learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return "NN";
        }
    };

    Word word = new Word("");
    word.setTag("NN");

    learner.learn(word);
    String prediction = learner.discreteValue(word);
    assertEquals("NN", prediction);
    assertTrue(learner.observed(""));
}
@Test
public void testLooksLikeNumberReturnsFalseOnEmptyString() {
    boolean result = POSBaselineLearner.looksLikeNumber("");
    assertFalse(result);
}
@Test
public void testLooksLikeNumberWithOnlySymbolsAndNoDigits() {
    boolean result = POSBaselineLearner.looksLikeNumber(".,-");
    assertFalse(result);
}
@Test
public void testLooksLikeNumberWithNonAllowedSymbol() {
    boolean result = POSBaselineLearner.looksLikeNumber("12$4");
    assertFalse(result);
}
@Test
public void testEmptyParametersConstructorCopy() {
    POSBaselineLearner.Parameters original = new POSBaselineLearner.Parameters();
    POSBaselineLearner.Parameters copy = new POSBaselineLearner.Parameters(original);
    assertNotNull(copy);
}
@Test
public void testFeatureValueWithNoLearningReturnsUnknown() {
    POSBaselineLearner learner = new POSBaselineLearner();
    learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return "xyz";
        }
    };
    learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return "UNK";
        }
    };

    Word word = new Word("xyz");
    word.setTag("UNK");

    Feature feature = learner.featureValue(word);
    assertEquals("UNKNOWN", feature.getStringValue());
}
@Test
public void testReadBinaryWithUnexpectedMapEntryDoesNotThrow() throws IOException {
    POSBaselineLearner learner = new POSBaselineLearner();
    learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return ((Word) example).getValue();
        }
    };
    learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return ((Word) example).tag();
        }
    };

    
    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    ExceptionlessOutputStream eos = new ExceptionlessOutputStream(byteOut);
    eos.writeInt(1); 
    eos.writeString("abnormal");
    eos.writeInt(1); 
    eos.writeString("RB");
    eos.writeInt(5); 
    eos.writeString("extra"); 
    eos.close();

    ExceptionlessInputStream eis = new ExceptionlessInputStream(new ByteArrayInputStream(byteOut.toByteArray()));
    learner.read(eis);
    eis.close();

    assertTrue(learner.observed("abnormal"));
    assertEquals(5, learner.observedCount("abnormal"));
    assertTrue(learner.allowableTags("abnormal").contains("RB"));
}
@Test
public void testComputePredictionWithTiedCountsChoosesFirstLexicographically() {
    POSBaselineLearner learner = new POSBaselineLearner();
    learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return ((Word) example).getValue();
        }
    };
    learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return ((Word) example).tag();
        }
    };

    Word word = new Word("cake");
    word.setTag("NN");
    learner.learn(word);

    word.setTag("VB");
    learner.learn(word);  

    String prediction = learner.discreteValue(word);
    assertTrue(prediction.equals("NN") || prediction.equals("VB")); 
    Set<String> tags = learner.allowableTags("cake");
    assertEquals(2, tags.size());
}
@Test
public void testWriteWithDuplicateCountsButDifferentTagsOrdering() {
    POSBaselineLearner learner = new POSBaselineLearner();
    learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return ((Word) example).getValue();
        }
    };
    learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return ((Word) example).tag();
        }
    };

    Word word1 = new Word("cake");
    word1.setTag("VB");
    learner.learn(word1);

    Word word2 = new Word("cake");
    word2.setTag("NN");
    learner.learn(word2);

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    PrintStream printStream = new PrintStream(out);
    learner.write(printStream);
    printStream.flush();
    String output = out.toString();

    assertTrue(output.contains("cake:"));
    assertTrue(output.contains("VB(1)"));
    assertTrue(output.contains("NN(1)"));
}
@Test
public void testLearnWithNullExample() {
    POSBaselineLearner learner = new POSBaselineLearner();
    learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return example == null ? "NULL_FORM" : ((Word) example).getValue();
        }
    };
    learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return example == null ? "NULL_TAG" : ((Word) example).tag();
        }
    };

    learner.learn(null);
    int count = learner.observedCount("NULL_FORM");
    assertEquals(1, count);
    Set<String> tags = learner.allowableTags("NULL_FORM");
    assertTrue(tags.contains("NULL_TAG"));
}
@Test
public void testDiscreteValueWithNullExtractorReturnsNullSafeFallback() {
    POSBaselineLearner learner = new POSBaselineLearner();
    learner.extractor = null;
    learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return "tag";
        }
    };

    Word word = new Word("test");
    word.setTag("tag");

    try {
        learner.discreteValue(word);
        fail("Expected NullPointerException due to null extractor");
    } catch (NullPointerException e) {
        
    }
}
@Test
public void testLearnWithDuplicateTagIncrementsCount() {
    POSBaselineLearner learner = new POSBaselineLearner();
    learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return ((Word) example).getValue();
        }
    };
    learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return ((Word) example).tag();
        }
    };

    Word word = new Word("apple");
    word.setTag("NN");
    learner.learn(word);
    learner.learn(word);
    int count = learner.observedCount("apple");
    assertEquals(2, count);
    String prediction = learner.discreteValue(word);
    assertEquals("NN", prediction);
}
@Test
public void testAllowableTagsWithNoTagAndNotNumberOrSemicolon() {
    POSBaselineLearner learner = new POSBaselineLearner();

    Set<String> tags = learner.allowableTags("unobtainium");
    assertNotNull(tags);
    assertTrue(tags.isEmpty() || tags.contains("UNKNOWN"));
}
@Test
public void testFeatureVectorFromUntrainedWordReturnsUnknown() {
    POSBaselineLearner learner = new POSBaselineLearner();
    learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return ((Word) example).getValue();
        }
    };
    learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return ((Word) example).tag();
        }
    };

    Word word = new Word("alien");
    word.setTag("NN");

    FeatureVector fv = learner.classify(word);
    assertEquals(1, fv.featuresSize());
    String label = fv.getFeature(0).getStringValue();
    assertEquals("UNKNOWN", label);
}
@Test
public void testDiscreetValuePredictionFallbackWithEmptyTable() {
    POSBaselineLearner learner = new POSBaselineLearner();
    learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return ((Word) example).getValue();
        }
    };
    learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return ((Word) example).tag();
        }
    };

    Word word = new Word("!@#");
    word.setTag("SYM");
    String prediction = learner.discreteValue(word);
    assertEquals("UNKNOWN", prediction);
}
@Test
public void testLearnWithWhitespaceWordForm() {
    POSBaselineLearner learner = new POSBaselineLearner();
    learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return "   ";
        }
    };
    learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return "WS";
        }
    };

    Word word = new Word("   ");
    word.setTag("WS");
    learner.learn(word);
    assertTrue(learner.observed("   "));
    assertEquals("WS", learner.discreteValue(word));
}
@Test
public void testWriteDoesNotThrowWhenEmpty() {
    POSBaselineLearner learner = new POSBaselineLearner();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(out);
    learner.write(ps);
    String output = out.toString();
    assertNotNull(output);
    assertEquals("", output.trim());
}
@Test
public void testAllowableTagsWithMixedCaseNumber() {
    POSBaselineLearner learner = new POSBaselineLearner();
    Set<String> tags = learner.allowableTags("1,000.00USD");
    assertTrue(tags.isEmpty());
}
@Test
public void testLooksLikeNumberWithJustDigit() {
    boolean result = POSBaselineLearner.looksLikeNumber("5");
    assertTrue(result);
}
@Test
public void testLooksLikeNumberWithMultipleDotsAndDashes() {
    boolean result = POSBaselineLearner.looksLikeNumber("12.3.4");
    assertTrue(result); 
}
@Test
public void testDiscreetValueWithMultipleHighestCountsChoosesOne() {
    POSBaselineLearner learner = new POSBaselineLearner();
    learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return ((Word) example).getValue();
        }
    };
    learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return ((Word) example).tag();
        }
    };

    Word input = new Word("lead");
    input.setTag("NN");
    learner.learn(input);
    input.setTag("VB");
    learner.learn(input);
    String prediction = learner.discreteValue(input);
    assertTrue(prediction.equals("NN") || prediction.equals("VB"));
}
@Test
public void testObservedReturnsFalseImmediatelyAfterConstruction() {
    POSBaselineLearner learner = new POSBaselineLearner();
    assertFalse(learner.observed("word"));
}
@Test
public void testObservedCountWithMixedCasing() {
    POSBaselineLearner learner = new POSBaselineLearner();
    learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return ((Word) example).getValue();
        }
    };
    learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return ((Word) example).tag();
        }
    };

    Word lower = new Word("abc");
    lower.setTag("NN");

    Word upper = new Word("ABC");
    upper.setTag("VB");

    learner.learn(lower);
    learner.learn(upper);

    int countLower = learner.observedCount("abc");
    int countUpper = learner.observedCount("ABC");

    assertEquals(1, countLower);
    assertEquals(1, countUpper);
}
@Test
public void testClassifyFeatureVectorIsNotNullForUnseenWord() {
    POSBaselineLearner learner = new POSBaselineLearner();
    learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return ((Word) example).getValue();
        }
    };
    learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return ((Word) example).tag();
        }
    };

    Word word = new Word("newword");
    word.setTag("NN");

    FeatureVector fv = learner.classify(word);
    assertNotNull(fv);
    assertEquals(1, fv.featuresSize());
    assertEquals("UNKNOWN", fv.getFeature(0).getStringValue());
}
@Test
public void testDiscreetValueReturnsCDForNegativeNumber() {
    POSBaselineLearner learner = new POSBaselineLearner();
    learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return "-100";
        }
    };
    learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return "CD";
        }
    };

    Word word = new Word("-100");
    word.setTag("CD");
    String prediction = learner.discreteValue(word);
    assertEquals("CD", prediction);
}
@Test
public void testClassifyPrimitiveMethodWithNonEmptyArraysStillReturnsEmptyVector() {
    POSBaselineLearner learner = new POSBaselineLearner();
    int[] features = new int[] {1, 2, 3};
    double[] values = new double[] {1.0, 2.0, 3.0};
    FeatureVector result = learner.classify(features, values);
    assertNotNull(result);
    assertEquals(0, result.featuresSize());
}
@Test
public void testWriteBinaryAndReloadPreservesMultipleForms() throws IOException {
    POSBaselineLearner learner = new POSBaselineLearner();
    learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return ((Word) example).getValue();
        }
    };
    learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return ((Word) example).tag();
        }
    };

    Word word1 = new Word("car");
    word1.setTag("NN");
    learner.learn(word1);

    Word word2 = new Word("cars");
    word2.setTag("NNS");
    learner.learn(word2);

    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    ExceptionlessOutputStream eos = new ExceptionlessOutputStream(byteOut);
    learner.write(eos);
    eos.close();

    byte[] data = byteOut.toByteArray();
    ExceptionlessInputStream eis = new ExceptionlessInputStream(new ByteArrayInputStream(data));
    POSBaselineLearner loaded = new POSBaselineLearner();
    loaded.extractor = learner.extractor;
    loaded.labeler = learner.labeler;
    loaded.read(eis);
    eis.close();

    assertEquals(1, loaded.observedCount("car"));
    assertEquals(1, loaded.observedCount("cars"));
    assertTrue(loaded.allowableTags("car").contains("NN"));
    assertTrue(loaded.allowableTags("cars").contains("NNS"));
}
@Test
public void testObservedCountWithZeroAfterForget() {
    POSBaselineLearner learner = new POSBaselineLearner();
    learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return "time";
        }
    };
    learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return "NN";
        }
    };

    Word word = new Word("time");
    word.setTag("NN");
    learner.learn(word);
    learner.forget();
    int count = learner.observedCount("time");
    assertEquals(0, count);
}
@Test
public void testWriteToTextStreamWithEmptyTableProducesEmptyOutput() {
    POSBaselineLearner learner = new POSBaselineLearner();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(baos);
    learner.write(out);
    out.flush();
    String written = baos.toString();
    assertTrue(written.isEmpty());
}
@Test
public void testWriteBinaryWithMultipleTagsPreservesHighestTagOnReload() throws IOException {
    POSBaselineLearner learner = new POSBaselineLearner();
    learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return ((Word) example).getValue();
        }
    };
    learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return ((Word) example).tag();
        }
    };

    Word w1 = new Word("plane");
    w1.setTag("NN");
    learner.learn(w1);
    learner.learn(w1);

    Word w2 = new Word("plane");
    w2.setTag("VB");
    learner.learn(w2);

    ByteArrayOutputStream output = new ByteArrayOutputStream();
    ExceptionlessOutputStream eos = new ExceptionlessOutputStream(output);
    learner.write(eos);
    eos.close();

    byte[] bytes = output.toByteArray();

    ExceptionlessInputStream eis = new ExceptionlessInputStream(new ByteArrayInputStream(bytes));
    POSBaselineLearner reloaded = new POSBaselineLearner();
    reloaded.extractor = learner.extractor;
    reloaded.labeler = learner.labeler;
    reloaded.read(eis);
    eis.close();

    Word plane = new Word("plane");
    plane.setTag("NN");
    String prediction = reloaded.discreteValue(plane);
    assertEquals("NN", prediction);
}
@Test
public void testEmptyCloneHasEmptyTable() {
    POSBaselineLearner original = new POSBaselineLearner();
    original.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return "hello";
        }
    };
    original.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return "NN";
        }
    };

    Word w = new Word("hello");
    w.setTag("NN");
    original.learn(w);

    POSBaselineLearner clone = (POSBaselineLearner) original.emptyClone();
    clone.extractor = original.extractor;
    clone.labeler = original.labeler;

    int count = clone.observedCount("hello");
    assertEquals(0, count);
}
@Test
public void testLearnWithDifferentWordsAndSameTagDoesNotOverwriteEachOther() {
    POSBaselineLearner learner = new POSBaselineLearner();
    learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return ((Word) example).getValue();
        }
    };
    learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return ((Word) example).tag();
        }
    };

    Word alpha = new Word("alpha");
    alpha.setTag("JJ");

    Word beta = new Word("beta");
    beta.setTag("JJ");

    learner.learn(alpha);
    learner.learn(beta);

    int countAlpha = learner.observedCount("alpha");
    int countBeta = learner.observedCount("beta");

    assertEquals(1, countAlpha);
    assertEquals(1, countBeta);
    assertNotEquals(learner.discreteValue(alpha), "UNKNOWN");
    assertNotEquals(learner.discreteValue(beta), "UNKNOWN");
}
@Test
public void testScoresWithNullArraysReturnsNull() {
    POSBaselineLearner learner = new POSBaselineLearner();
    assertNull(learner.scores(null, null));
}
@Test
public void testWritePrintStreamOutputOrderingWithMultipleKeys() {
    POSBaselineLearner learner = new POSBaselineLearner();
    learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return ((Word) example).getValue();
        }
    };
    learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return ((Word) example).tag();
        }
    };

    Word apple = new Word("apple");
    apple.setTag("NN");

    Word banana = new Word("banana");
    banana.setTag("NN");

    learner.learn(banana);
    learner.learn(apple); 

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(out);
    learner.write(ps);
    String output = out.toString();

    int appleIndex = output.indexOf("apple:");
    int bananaIndex = output.indexOf("banana:");
    assertTrue(appleIndex < bananaIndex);
}
@Test
public void testWritePrintStreamWithMultipleTagsSortedByCount() {
    POSBaselineLearner learner = new POSBaselineLearner();
    learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return ((Word) example).getValue();
        }
    };
    learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return ((Word) example).tag();
        }
    };

    Word wordX = new Word("x");
    wordX.setTag("JJ");
    learner.learn(wordX);
    learner.learn(wordX);

    wordX.setTag("VB");
    learner.learn(wordX);
    wordX.setTag("NN");
    learner.learn(wordX);

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(out);
    learner.write(ps);
    String output = out.toString();

    int jjIndex = output.indexOf("JJ(2)");
    int vbIndex = output.indexOf("VB(1)");
    int nnIndex = output.indexOf("NN(1)");

    assertTrue(jjIndex < vbIndex || jjIndex < nnIndex);
}
@Test
public void testLearnWithNullFormReturnsSafeResult() {
    POSBaselineLearner learner = new POSBaselineLearner();
    learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return null;
        }
    };
    learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return "NN";
        }
    };

    Word word = new Word(null);
    word.setTag("NN");

    try {
        learner.learn(word);
        String prediction = learner.discreteValue(word);
        assertEquals("UNKNOWN", prediction);
    } catch (Exception e) {
        fail("Should not throw exception on null form.");
    }
}
@Test
public void testLearnWithNullTagDefaultsToZeroAndIncrementsOnce() {
    POSBaselineLearner learner = new POSBaselineLearner();
    learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return "testword";
        }
    };
    learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return null;
        }
    };

    Word word = new Word("testword");
    word.setTag(null);

    learner.learn(word);
    int count = learner.observedCount("testword");
    assertEquals(1, count);
    Set<String> tags = learner.allowableTags("testword");
    assertTrue(tags.contains(null));
}
@Test
public void testForgetOnEmptyTableDoesNotThrow() {
    POSBaselineLearner learner = new POSBaselineLearner();
    try {
        learner.forget();
    } catch (Exception e) {
        fail("Forget should not throw on empty table.");
    }
}
@Test
public void testObservedWithWhitespaceForm() {
    POSBaselineLearner learner = new POSBaselineLearner();
    learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return " ";
        }
    };
    learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return "SP";
        }
    };

    Word word = new Word(" ");
    word.setTag("SP");

    learner.learn(word);
    assertTrue(learner.observed(" "));
    int count = learner.observedCount(" ");
    assertEquals(1, count);
}
@Test
public void testAllowableTagsAfterForgetReturnsFallback() {
    POSBaselineLearner learner = new POSBaselineLearner();
    learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return "semicolon";
        }
    };
    learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return ":";
        }
    };

    Word word = new Word(";");
    word.setTag(":");

    learner.learn(word);
    learner.forget();
    Set<String> tags = learner.allowableTags(";");
    assertTrue(tags.contains(":"));
}
@Test
public void testFeatureValueWithUnseenSymbolReturnsUnknown() {
    POSBaselineLearner learner = new POSBaselineLearner();
    learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return "#$%";
        }
    };
    learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return "SYM";
        }
    };

    Word word = new Word("#$%");
    word.setTag("SYM");
    Feature feature = learner.featureValue(word);
    assertEquals("UNKNOWN", feature.getStringValue());
}
@Test
public void testClassifyReturnsUnknownForSymbolFormWithoutTraining() {
    POSBaselineLearner learner = new POSBaselineLearner();
    learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return "#";
        }
    };
    learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return "SYM";
        }
    };

    Word word = new Word("#");
    word.setTag("SYM");

    FeatureVector fv = learner.classify(word);
    assertEquals("UNKNOWN", fv.getFeature(0).getStringValue());
}
@Test
public void testWriteSortsTagsByDescendingCountEvenIfEqualLexOrderIsMaintained() {
    POSBaselineLearner learner = new POSBaselineLearner();
    learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return "mixed";
        }
    };
    learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return ((Word) example).tag();
        }
    };

    Word w1 = new Word("mixed");
    w1.setTag("CC");
    learner.learn(w1);
    learner.learn(w1);

    Word w2 = new Word("mixed");
    w2.setTag("CD");
    learner.learn(w2);

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(out);
    learner.write(ps);
    String result = out.toString();

    int ccIndex = result.indexOf("CC(2)");
    int cdIndex = result.indexOf("CD(1)");
    assertTrue(ccIndex < cdIndex);
}
@Test
public void testClassifyWithSameFormDifferentCasesHandledSeparately() {
    POSBaselineLearner learner = new POSBaselineLearner();
    learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return ((Word) example).getValue();
        }
    };
    learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return ((Word) example).tag();
        }
    };

    Word lower = new Word("case");
    lower.setTag("NN");

    Word upper = new Word("CASE");
    upper.setTag("VB");

    learner.learn(lower);
    learner.learn(upper);

    String predLower = learner.discreteValue(lower);
    String predUpper = learner.discreteValue(upper);

    assertEquals("NN", predLower);
    assertEquals("VB", predUpper);
}
@Test
public void testObservingSameTagMultipleTimesIncreasesOnlyThatTag() {
    POSBaselineLearner learner = new POSBaselineLearner();
    learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return "token";
        }
    };
    learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
        public String discreteValue(Object example) {
            return "JJ";
        }
    };

    Word word = new Word("token");
    word.setTag("JJ");

    learner.learn(word);
    learner.learn(word);
    learner.learn(word);

    Set<String> tags = learner.allowableTags("token");
    assertEquals(1, tags.size());
    assertTrue(tags.contains("JJ"));
} 
}