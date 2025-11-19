public class MikheevLearner_1_GPTLLMTest { 

 @Test
    public void testCapitalizedFirstWordSuffixLearnedCorrectly() {
        MikheevLearner learner = new MikheevLearner("test");
        learner.extractor = mock(POSBaselineLearner.FeatureExtractor.class);
        learner.labeler = mock(POSBaselineLearner.Labeler.class);

        Word word = new Word("Capitalized");
        word.capitalized = true;
        word.previous = null;

        when(learner.extractor.discreteValue(word)).thenReturn("Capitalized");
        when(learner.labeler.discreteValue(word)).thenReturn("NNP");

        learner.learn(word);

        Set<String> tags = learner.allowableTags(word);
        assertTrue(tags.contains("NNP"));
    }
@Test
    public void testCapitalizedNotFirstWordSuffixLearnedCorrectly() {
        MikheevLearner learner = new MikheevLearner("test");
        learner.extractor = mock(POSBaselineLearner.FeatureExtractor.class);
        learner.labeler = mock(POSBaselineLearner.Labeler.class);

        Word word = new Word("Running");
        word.capitalized = true;
        word.previous = new Word("the");

        when(learner.extractor.discreteValue(word)).thenReturn("Running");
        when(learner.labeler.discreteValue(word)).thenReturn("VBG");

        learner.learn(word);

        Set<String> tags = learner.allowableTags(word);
        assertTrue(tags.contains("VBG"));
    }
@Test
    public void testUncapitalizedNonHyphenatedSuffixLearnedCorrectly() {
        MikheevLearner learner = new MikheevLearner("test");
        learner.extractor = mock(POSBaselineLearner.FeatureExtractor.class);
        learner.labeler = mock(POSBaselineLearner.Labeler.class);

        Word word = new Word("smiling");
        word.capitalized = false;
        word.previous = new Word("is");

        when(learner.extractor.discreteValue(word)).thenReturn("smiling");
        when(learner.labeler.discreteValue(word)).thenReturn("VBG");

        learner.learn(word);

        Set<String> tags = learner.allowableTags(word);
        assertTrue(tags.contains("VBG"));
    }
@Test
    public void testUncapitalizedHyphenatedWordReturnsDefaultTags() {
        MikheevLearner learner = new MikheevLearner("test");
        learner.extractor = mock(POSBaselineLearner.FeatureExtractor.class);
        learner.labeler = mock(POSBaselineLearner.Labeler.class);

        Word word = new Word("well-known");
        word.capitalized = false;
        word.previous = new Word("a");

        when(learner.extractor.discreteValue(word)).thenReturn("well-known");
        when(learner.labeler.discreteValue(word)).thenReturn("JJ");

        learner.learn(word);

        Set<String> tags = learner.allowableTags(word);
        assertEquals(2, tags.size());
        assertTrue(tags.contains("NN"));
        assertTrue(tags.contains("JJ"));
    }
@Test
    public void testShortWordIsIgnoredAndFallbackToNN() {
        MikheevLearner learner = new MikheevLearner("test");
        learner.extractor = mock(POSBaselineLearner.FeatureExtractor.class);
        learner.labeler = mock(POSBaselineLearner.Labeler.class);

        Word word = new Word("hi");
        word.capitalized = false;
        word.previous = new Word("say");

        when(learner.extractor.discreteValue(word)).thenReturn("hi");
        when(learner.labeler.discreteValue(word)).thenReturn("UH");

        learner.learn(word);

        Set<String> tags = learner.allowableTags(word);
        assertEquals(1, tags.size());
        assertTrue(tags.contains("NN"));
    }
@Test
    public void testFallbackDefaultNNPWhenSuffixUnseenCapitalized() {
        MikheevLearner learner = new MikheevLearner("test");

        Word word = new Word("UnseenWord");
        word.capitalized = true;
        word.previous = new Word("Mr");

        Set<String> tags = learner.allowableTags(word);
        assertEquals(1, tags.size());
        assertTrue(tags.contains("NNP"));
    }
@Test
    public void testFallbackDefaultNNWhenSuffixUnseenUncapitalized() {
        MikheevLearner learner = new MikheevLearner("test");

        Word word = new Word("unknownterm");
        word.capitalized = false;
        word.previous = new Word("an");

        Set<String> tags = learner.allowableTags(word);
        assertEquals(1, tags.size());
        assertTrue(tags.contains("NN"));
    }
@Test
    public void testDefaultConstructorInitializesCorrectly() {
        MikheevLearner learner = new MikheevLearner();
        assertNotNull(learner);
    }
@Test
    public void testCloneReturnsEmptyMikheevLearner() {
        MikheevLearner learner = new MikheevLearner("test");
        Learner clone = learner.emptyClone();
        assertNotNull(clone);
        assertTrue(clone instanceof MikheevLearner);
        assertNotSame(learner, clone);
    }
@Test
    public void testForgetClearsLearnedData() {
        MikheevLearner learner = new MikheevLearner("test");
        learner.extractor = mock(POSBaselineLearner.FeatureExtractor.class);
        learner.labeler = mock(POSBaselineLearner.Labeler.class);

        Word word = new Word("forgetting");
        word.capitalized = false;
        word.previous = new Word("keep");

        when(learner.extractor.discreteValue(word)).thenReturn("forgetting");
        when(learner.labeler.discreteValue(word)).thenReturn("VBG");

        learner.learn(word);
        learner.forget();
        Set<String> tags = learner.allowableTags(new Word("forgetting"));
        assertTrue(tags.contains("NN"));
    }
@Test
    public void testWriteToPrintStreamOutputsNonEmpty() {
        MikheevLearner learner = new MikheevLearner("test");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);

        learner.write(printStream);

        String output = outputStream.toString();
        assertNotNull(output);
        assertTrue(output.contains("main table"));
    }
@Test
    public void testBinaryReadWritePreservesData() {
        MikheevLearner learner = new MikheevLearner("test");
        learner.extractor = mock(POSBaselineLearner.FeatureExtractor.class);
        learner.labeler = mock(POSBaselineLearner.Labeler.class);

        Word word = new Word("Republic");
        word.capitalized = true;
        word.previous = null;

        when(learner.extractor.discreteValue(word)).thenReturn("Republic");
        when(learner.labeler.discreteValue(word)).thenReturn("NNP");

        learner.learn(word);

        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ExceptionlessOutputStream eos = new ExceptionlessOutputStream(byteOut);
        learner.write(eos);

        MikheevLearner loaded = new MikheevLearner("loaded");
        ExceptionlessInputStream eis = new ExceptionlessInputStream(new java.io.ByteArrayInputStream(byteOut.toByteArray()));
        loaded.read(eis);

        Set<String> tags = loaded.allowableTags(word);
        assertTrue(tags.contains("NNP"));
    }
@Test
    public void testConstructorWithParameters() {
        MikheevLearner.Parameters params = new MikheevLearner.Parameters();
        MikheevLearner learner = new MikheevLearner(params);
        assertNotNull(learner);
    }
@Test
    public void testLearnsBothSuffixTypes() {
        MikheevLearner learner = new MikheevLearner("test");
        learner.extractor = mock(POSBaselineLearner.FeatureExtractor.class);
        learner.labeler = mock(POSBaselineLearner.Labeler.class);

        Word word = new Word("determination");
        word.capitalized = false;
        word.previous = new Word("of");

        when(learner.extractor.discreteValue(word)).thenReturn("determination");
        when(learner.labeler.discreteValue(word)).thenReturn("NN");

        learner.learn(word);

        Set<String> tags = learner.allowableTags(word);
        assertTrue(tags.contains("NN"));
    }
@Test
public void testWordWithExactly5LettersLearnedOnly3LetterSuffix() {
    MikheevLearner learner = new MikheevLearner("test");
    learner.extractor = mock(POSBaselineLearner.FeatureExtractor.class);
    learner.labeler = mock(POSBaselineLearner.Labeler.class);

    Word word = new Word("plant");
    word.capitalized = false;
    word.previous = new Word("a");

    when(learner.extractor.discreteValue(word)).thenReturn("plant");
    when(learner.labeler.discreteValue(word)).thenReturn("NN");

    learner.learn(word);
    Set<String> tags = learner.allowableTags(word);

    assertTrue(tags.contains("NN"));
}
@Test
public void testWordWithExactly6LettersLearns3And4LetterSuffixes() {
    MikheevLearner learner = new MikheevLearner("test");
    learner.extractor = mock(POSBaselineLearner.FeatureExtractor.class);
    learner.labeler = mock(POSBaselineLearner.Labeler.class);

    Word word = new Word("planet");
    word.capitalized = false;
    word.previous = new Word("a");

    when(learner.extractor.discreteValue(word)).thenReturn("planet");
    when(learner.labeler.discreteValue(word)).thenReturn("NN");

    learner.learn(word);
    Set<String> tags = learner.allowableTags(word);

    assertTrue(tags.contains("NN"));
}
@Test
public void testWordSuffixIncludesNonLetter_noLearningOccurs() {
    MikheevLearner learner = new MikheevLearner("test");
    learner.extractor = mock(POSBaselineLearner.FeatureExtractor.class);
    learner.labeler = mock(POSBaselineLearner.Labeler.class);

    Word word = new Word("test9");
    word.capitalized = false;
    word.previous = new Word("a");

    when(learner.extractor.discreteValue(word)).thenReturn("test9");
    when(learner.labeler.discreteValue(word)).thenReturn("CD");

    learner.learn(word);
    Set<String> tags = learner.allowableTags(word);

    assertTrue(tags.contains("NN")); 
}
@Test
public void testAllowableTagsFallbackOnShortUnknownCapitalizedWord() {
    MikheevLearner learner = new MikheevLearner("test");

    Word word = new Word("A");
    word.capitalized = true;
    word.previous = null;

    Set<String> tags = learner.allowableTags(word);
    assertTrue(tags.contains("NNP"));
}
@Test
public void testAllowableTagsFallbackOnShortUnknownUncapitalizedWord() {
    MikheevLearner learner = new MikheevLearner("test");

    Word word = new Word("it");
    word.capitalized = false;
    word.previous = null;

    Set<String> tags = learner.allowableTags(word);
    assertTrue(tags.contains("NN"));
}
@Test
public void testAllowableTagsPrefers4LetterSuffixWhenPresent() {
    MikheevLearner learner = new MikheevLearner("test");
    learner.extractor = mock(POSBaselineLearner.FeatureExtractor.class);
    learner.labeler = mock(POSBaselineLearner.Labeler.class);

    Word word = new Word("strongest");
    word.capitalized = false;
    word.previous = new Word("the");

    when(learner.extractor.discreteValue(word)).thenReturn("strongest");
    when(learner.labeler.discreteValue(word)).thenReturn("JJS");

    learner.learn(word);

    Set<String> tags = learner.allowableTags(new Word("strongest"));
    assertTrue(tags.contains("JJS"));
}
@Test
public void testDoneLearningKeepsEntryWithJustEnoughCount() {
    MikheevLearner learner = new MikheevLearner("test");
    learner.extractor = mock(POSBaselineLearner.FeatureExtractor.class);
    learner.labeler = mock(POSBaselineLearner.Labeler.class);

    Word word = new Word("happiness");
    word.capitalized = false;
    word.previous = new Word("of");

    when(learner.extractor.discreteValue(word)).thenReturn("happiness");
    when(learner.labeler.discreteValue(word)).thenReturn("NN");

    learner.learn(word);
    learner.learn(word);
    learner.learn(word);
    learner.learn(word);
    learner.learn(word);
    learner.learn(word);
    learner.learn(word);
    learner.learn(word);
    learner.learn(word);
    learner.learn(word);
    learner.learn(word);

    learner.doneLearning();
    Set<String> tags = learner.allowableTags(word);

    assertTrue(tags.contains("NN"));
}
@Test
public void testPruneRemovesLowFrequencyTagButKeepsSuffix() {
    MikheevLearner learner = new MikheevLearner("test");

    
    learner.extractor = mock(POSBaselineLearner.FeatureExtractor.class);
    learner.labeler = mock(POSBaselineLearner.Labeler.class);

    Word word1 = new Word("confusion");
    word1.capitalized = false;
    word1.previous = new Word("in");

    Word word2 = new Word("confusion");
    word2.capitalized = false;
    word2.previous = new Word("in");

    when(learner.extractor.discreteValue(word1)).thenReturn("confusion");
    when(learner.labeler.discreteValue(word1)).thenReturn("NN");

    when(learner.extractor.discreteValue(word2)).thenReturn("confusion");
    when(learner.labeler.discreteValue(word2)).thenReturn("VB");

    
    learner.learn(word1); learner.learn(word1); learner.learn(word1); learner.learn(word1); learner.learn(word1);
    learner.learn(word1); learner.learn(word1); learner.learn(word1); learner.learn(word1); learner.learn(word1);
    learner.learn(word1); learner.learn(word1); learner.learn(word1); learner.learn(word1); learner.learn(word1);
    learner.learn(word1); learner.learn(word1); learner.learn(word1); learner.learn(word1); learner.learn(word1);

    learner.learn(word1); learner.learn(word1); learner.learn(word1); learner.learn(word1); learner.learn(word1);
    learner.learn(word1); learner.learn(word1); learner.learn(word1); learner.learn(word1); learner.learn(word1);
    learner.learn(word1); learner.learn(word1); learner.learn(word1); learner.learn(word1); learner.learn(word1);

    learner.learn(word1); learner.learn(word1); learner.learn(word1); learner.learn(word1); learner.learn(word1);
    learner.learn(word1); learner.learn(word1); learner.learn(word1); learner.learn(word1); learner.learn(word1);
    learner.learn(word1); learner.learn(word1); learner.learn(word1); learner.learn(word1); learner.learn(word1);

    learner.learn(word1); learner.learn(word1); learner.learn(word1); learner.learn(word1); learner.learn(word1);
    learner.learn(word1); learner.learn(word1); learner.learn(word1); learner.learn(word1); learner.learn(word1);

    learner.learn(word1); learner.learn(word1); learner.learn(word1); learner.learn(word1); learner.learn(word1);
    learner.learn(word1); learner.learn(word1); learner.learn(word1); learner.learn(word1); learner.learn(word1);

    learner.learn(word1); learner.learn(word1); learner.learn(word1); learner.learn(word1); learner.learn(word1);
    learner.learn(word1); learner.learn(word1); learner.learn(word1); learner.learn(word1); learner.learn(word1);

    learner.learn(word2); learner.learn(word2); learner.learn(word2); learner.learn(word2); learner.learn(word2);

    learner.doneLearning();

    Set<String> tags = learner.allowableTags(word1);
    assertTrue(tags.contains("NN"));
    assertFalse(tags.contains("VB"));
}
@Test
public void testEmptyCloneHasEmptyNameIfDefaultConstructorUsed() {
    MikheevLearner learner = new MikheevLearner();
    Learner cloned = learner.emptyClone();
    assertNotNull(cloned);
    assertTrue(cloned instanceof MikheevLearner);
}
@Test
    public void testLearnSkipsWordWithSuffixLengthNotAllLetters() {
        MikheevLearner learner = new MikheevLearner("test");
        learner.extractor = mock(POSBaselineLearner.FeatureExtractor.class);
        learner.labeler = mock(POSBaselineLearner.Labeler.class);

        Word word = new Word("abc2#");
        word.capitalized = false;
        word.previous = new Word("the");

        when(learner.extractor.discreteValue(word)).thenReturn("abc2#");
        when(learner.labeler.discreteValue(word)).thenReturn("NN");

        learner.learn(word);
        Set<String> tags = learner.allowableTags(word);
        assertTrue(tags.contains("NN")); 
    }
@Test
    public void testAllowableTagsReturnsFromFirstCapitalizedWhen3LetterSuffixMatches() {
        MikheevLearner learner = new MikheevLearner("test");
        learner.extractor = mock(POSBaselineLearner.FeatureExtractor.class);
        learner.labeler = mock(POSBaselineLearner.Labeler.class);

        Word word = new Word("Elephant");
        word.capitalized = true;
        word.previous = null;

        when(learner.extractor.discreteValue(word)).thenReturn("Elephant");
        when(learner.labeler.discreteValue(word)).thenReturn("NNP");

        learner.learn(word);

        Word testWord = new Word("Elephant");
        testWord.capitalized = true;
        testWord.previous = null;

        Set<String> tags = learner.allowableTags(testWord);
        assertTrue(tags.contains("NNP"));
    }
@Test
    public void testAllowableTagsReturnsFromNotFirstCapitalizedWhen4LetterSuffixMatches() {
        MikheevLearner learner = new MikheevLearner("test");
        learner.extractor = mock(POSBaselineLearner.FeatureExtractor.class);
        learner.labeler = mock(POSBaselineLearner.Labeler.class);

        Word word = new Word("President");
        word.capitalized = true;
        word.previous = new Word("the");

        when(learner.extractor.discreteValue(word)).thenReturn("President");
        when(learner.labeler.discreteValue(word)).thenReturn("NNP");

        learner.learn(word);

        Word testWord = new Word("President");
        testWord.capitalized = true;
        testWord.previous = new Word("the");

        Set<String> tags = learner.allowableTags(testWord);
        assertTrue(tags.contains("NNP"));
    }
@Test
    public void testAllowableTagsUncapitalizedNoMatchingSuffixReturnsFallbackNN() {
        MikheevLearner learner = new MikheevLearner("test");

        Word word = new Word("xyzabc");
        word.capitalized = false;
        word.previous = new Word("some");

        Set<String> tags = learner.allowableTags(word);
        assertTrue(tags.contains("NN"));
    }
@Test
    public void testLearnWithCapitalizedAddsBoth3And4LetterSuffixes() {
        MikheevLearner learner = new MikheevLearner("test");
        learner.extractor = mock(POSBaselineLearner.FeatureExtractor.class);
        learner.labeler = mock(POSBaselineLearner.Labeler.class);

        Word word = new Word("Administrator");
        word.capitalized = true;
        word.previous = null;

        when(learner.extractor.discreteValue(word)).thenReturn("Administrator");
        when(learner.labeler.discreteValue(word)).thenReturn("NNP");

        learner.learn(word);

        Word test = new Word("Administrator");
        test.capitalized = true;
        test.previous = null;

        Set<String> tags = learner.allowableTags(test);
        assertTrue(tags.contains("NNP"));
    }
@Test
    public void testLearnWithUncapitalizedWordWithHyphenSkipsLearning() {
        MikheevLearner learner = new MikheevLearner("test");
        learner.extractor = mock(POSBaselineLearner.FeatureExtractor.class);
        learner.labeler = mock(POSBaselineLearner.Labeler.class);

        Word word = new Word("co-operate");
        word.capitalized = false;
        word.previous = new Word("to");

        when(learner.extractor.discreteValue(word)).thenReturn("co-operate");
        when(learner.labeler.discreteValue(word)).thenReturn("VB");

        learner.learn(word);

        Set<String> tags = learner.allowableTags(word);
        assertTrue(tags.contains("NN"));
        assertTrue(tags.contains("JJ"));
    }
@Test
    public void testLearnSkipsWordsShorterThan5Characters() {
        MikheevLearner learner = new MikheevLearner("test");
        learner.extractor = mock(POSBaselineLearner.FeatureExtractor.class);
        learner.labeler = mock(POSBaselineLearner.Labeler.class);

        Word word = new Word("help");
        word.capitalized = false;
        word.previous = new Word("to");

        when(learner.extractor.discreteValue(word)).thenReturn("help");
        when(learner.labeler.discreteValue(word)).thenReturn("VB");

        learner.learn(word);

        Set<String> tags = learner.allowableTags(word);
        assertTrue(tags.contains("NN"));
    }
@Test
    public void testDoneLearningPrunesLowCountSuffixFromTable() {
        MikheevLearner learner = new MikheevLearner("test");
        learner.extractor = mock(POSBaselineLearner.FeatureExtractor.class);
        learner.labeler = mock(POSBaselineLearner.Labeler.class);

        Word word = new Word("balancing");
        word.capitalized = false;
        word.previous = new Word("on");

        when(learner.extractor.discreteValue(word)).thenReturn("balancing");
        when(learner.labeler.discreteValue(word)).thenReturn("VBG");

        learner.learn(word); 

        learner.doneLearning();

        Set<String> tags = learner.allowableTags(new Word("balancing"));
        assertTrue(tags.contains("NN")); 
    }
@Test
    public void testForgetClearsCapitalizedRules() {
        MikheevLearner learner = new MikheevLearner("test");
        learner.extractor = mock(POSBaselineLearner.FeatureExtractor.class);
        learner.labeler = mock(POSBaselineLearner.Labeler.class);

        Word word = new Word("Professor");
        word.capitalized = true;
        word.previous = null;

        when(learner.extractor.discreteValue(word)).thenReturn("Professor");
        when(learner.labeler.discreteValue(word)).thenReturn("NNP");

        learner.learn(word);

        learner.forget();

        Set<String> tags = learner.allowableTags(new Word("Professor"));
        assertTrue(tags.contains("NNP")); 
    }
@Test
    public void testAllowableTagsUncapitalizedFallbackAfterForget() {
        MikheevLearner learner = new MikheevLearner("test");
        learner.extractor = mock(POSBaselineLearner.FeatureExtractor.class);
        learner.labeler = mock(POSBaselineLearner.Labeler.class);

        Word word = new Word("discussion");
        word.capitalized = false;
        word.previous = new Word("about");

        when(learner.extractor.discreteValue(word)).thenReturn("discussion");
        when(learner.labeler.discreteValue(word)).thenReturn("NN");

        learner.learn(word);
        learner.forget();

        Set<String> tags = learner.allowableTags(new Word("discussion"));
        assertTrue(tags.contains("NN"));
    }
@Test
    public void testAllowableTagsWithEmptyFormUncapitalized() {
        MikheevLearner learner = new MikheevLearner("test");

        Word word = new Word("");
        word.capitalized = false;
        word.previous = new Word("the");

        Set<String> tags = learner.allowableTags(word);
        assertEquals(1, tags.size());
        assertTrue(tags.contains("NN"));
    }
@Test
    public void testAllowableTagsWithEmptyFormCapitalized() {
        MikheevLearner learner = new MikheevLearner("test");

        Word word = new Word("");
        word.capitalized = true;
        word.previous = null;

        Set<String> tags = learner.allowableTags(word);
        assertEquals(1, tags.size());
        assertTrue(tags.contains("NNP"));
    }
@Test
    public void testAllowableTagsWithNullPreviousAndUnknownSuffix() {
        MikheevLearner learner = new MikheevLearner("test");

        Word word = new Word("Xyzword");
        word.capitalized = true;
        word.previous = null;

        Set<String> tags = learner.allowableTags(word);
        assertEquals(1, tags.size());
        assertTrue(tags.contains("NNP"));
    }
@Test
    public void testAllowableTagsWithHyphenAndUppercase() {
        MikheevLearner learner = new MikheevLearner("test");

        Word word = new Word("Xyz-Word");
        word.capitalized = false;
        word.previous = new Word("some");

        Set<String> tags = learner.allowableTags(word);
        assertTrue(tags.contains("NN"));
        assertTrue(tags.contains("JJ"));
    }
@Test
    public void testLearnWithUncapitalizedAndFormContainsOnlyHyphenIsSkipped() {
        MikheevLearner learner = new MikheevLearner("test");
        learner.extractor = mock(POSBaselineLearner.FeatureExtractor.class);
        learner.labeler = mock(POSBaselineLearner.Labeler.class);

        Word word = new Word("-");
        word.capitalized = false;
        word.previous = new Word("a");

        when(learner.extractor.discreteValue(word)).thenReturn("-");
        when(learner.labeler.discreteValue(word)).thenReturn("SYM");

        learner.learn(word);

        Set<String> tags = learner.allowableTags(word);
        assertTrue(tags.contains("NN"));
    }
@Test
    public void testLearnWithSuffixThatHasDigitCharactersSkipsLearning() {
        MikheevLearner learner = new MikheevLearner("test");
        learner.extractor = mock(POSBaselineLearner.FeatureExtractor.class);
        learner.labeler = mock(POSBaselineLearner.Labeler.class);

        Word word = new Word("abc99");
        word.capitalized = false;
        word.previous = new Word("the");

        when(learner.extractor.discreteValue(word)).thenReturn("abc99");
        when(learner.labeler.discreteValue(word)).thenReturn("CD");

        learner.learn(word);

        Set<String> tags = learner.allowableTags(word);
        assertTrue(tags.contains("NN")); 
    }
@Test
    public void testLearnWithNullFormReturnsSilently() {
        MikheevLearner learner = new MikheevLearner("test");
        learner.extractor = mock(POSBaselineLearner.FeatureExtractor.class);
        learner.labeler = mock(POSBaselineLearner.Labeler.class);

        Object input = new Object();  

        when(learner.extractor.discreteValue(input)).thenReturn(null);
        when(learner.labeler.discreteValue(input)).thenReturn("NN");

        try {
            learner.learn(input);
        } catch (Exception e) {
            fail("Learn should not throw exception with null form.");
        }
    }
@Test
    public void testAllowableTagsFallbackWhenWordIsNotWordInstance() {
        MikheevLearner learner = new MikheevLearner("test");

        Object notAWord = new Object();
        try {
            learner.learn(notAWord);
        } catch (ClassCastException e) {
            
            assertTrue(e.getMessage() != null);
        }
    }
@Test
    public void testLearnAndDoneLearningWithExactly11EntriesRetainsSuffix() {
        MikheevLearner learner = new MikheevLearner("test");
        learner.extractor = mock(POSBaselineLearner.FeatureExtractor.class);
        learner.labeler = mock(POSBaselineLearner.Labeler.class);

        Word word = new Word("flexibility");
        word.capitalized = false;
        word.previous = new Word("of");

        when(learner.extractor.discreteValue(word)).thenReturn("flexibility");
        when(learner.labeler.discreteValue(word)).thenReturn("NN");

        learner.learn(word); learner.learn(word); learner.learn(word);
        learner.learn(word); learner.learn(word); learner.learn(word);
        learner.learn(word); learner.learn(word); learner.learn(word);
        learner.learn(word); learner.learn(word); 

        learner.doneLearning();

        Set<String> tags = learner.allowableTags(word);
        assertTrue(tags.contains("NN"));
    }
@Test
    public void testAllowableTagsReturnsOnlySingleTagWhenOneDominatesAfterPrune() {
        MikheevLearner learner = new MikheevLearner("test");
        learner.extractor = mock(POSBaselineLearner.FeatureExtractor.class);
        learner.labeler = mock(POSBaselineLearner.Labeler.class);

        Word word = new Word("engineers");
        word.capitalized = false;
        word.previous = new Word("of");

        when(learner.extractor.discreteValue(word)).thenReturn("engineers");
        when(learner.labeler.discreteValue(word)).thenReturn("NNS");

        Word rare = new Word("engineers");
        rare.capitalized = false;
        rare.previous = new Word("of");

        when(learner.extractor.discreteValue(rare)).thenReturn("engineers");
        when(learner.labeler.discreteValue(rare)).thenReturn("VB");

        learner.learn(word); learner.learn(word); learner.learn(word); learner.learn(word);
        learner.learn(word); learner.learn(word); learner.learn(word); learner.learn(word);
        learner.learn(word); learner.learn(word); 
        learner.learn(rare); 

        learner.doneLearning();

        Set<String> tags = learner.allowableTags(word);
        assertTrue(tags.contains("NNS"));
        assertFalse(tags.contains("VB")); 
    }
@Test
    public void testAllowableTagsReturnsOnlyLastFallbackForCapitalizedWithNoMatch() {
        MikheevLearner learner = new MikheevLearner("test");

        Word word = new Word("Unmatchable");
        word.capitalized = true;
        word.previous = null;

        Set<String> tags = learner.allowableTags(word);
        assertEquals(1, tags.size());
        assertTrue(tags.contains("NNP"));
    }
@Test
    public void testWordFormWithMixedCaseLetters() {
        MikheevLearner learner = new MikheevLearner("test");

        learner.extractor = mock(POSBaselineLearner.FeatureExtractor.class);
        learner.labeler = mock(POSBaselineLearner.Labeler.class);

        Word word = new Word("EdUcAtIoNaL");
        word.capitalized = true;
        word.previous = null;

        when(learner.extractor.discreteValue(word)).thenReturn("EdUcAtIoNaL");
        when(learner.labeler.discreteValue(word)).thenReturn("JJ");

        learner.learn(word);
        Set<String> tags = learner.allowableTags(word);
        assertTrue(tags.contains("JJ"));
    }
@Test
    public void testLearnWithSuffixesThatGetPrunedByFrequencyThreshold() {
        MikheevLearner learner = new MikheevLearner("test");

        learner.extractor = mock(POSBaselineLearner.FeatureExtractor.class);
        learner.labeler = mock(POSBaselineLearner.Labeler.class);

        Word common = new Word("deployer");
        common.capitalized = false;
        common.previous = new Word("the");

        Word rare = new Word("deployer");
        rare.capitalized = false;
        rare.previous = new Word("the");

        when(learner.extractor.discreteValue(common)).thenReturn("deployer");
        when(learner.labeler.discreteValue(common)).thenReturn("NN");

        when(learner.extractor.discreteValue(rare)).thenReturn("deployer");
        when(learner.labeler.discreteValue(rare)).thenReturn("VB");

        learner.learn(common); learner.learn(common); learner.learn(common);
        learner.learn(common); learner.learn(common); learner.learn(common);
        learner.learn(common); learner.learn(common); learner.learn(common); 
        learner.learn(rare); learner.learn(rare); 

        learner.doneLearning();

        Set<String> tags = learner.allowableTags(common);
        assertTrue(tags.contains("NN"));
        assertFalse(tags.contains("VB")); 
    }
@Test
    public void testWordEndsWithValid3LettersButInvalid4LetterPattern() {
        MikheevLearner learner = new MikheevLearner("test");

        learner.extractor = mock(POSBaselineLearner.FeatureExtractor.class);
        learner.labeler = mock(POSBaselineLearner.Labeler.class);

        Word word = new Word("ref!x");
        word.capitalized = false;
        word.previous = new Word("the");

        when(learner.extractor.discreteValue(word)).thenReturn("ref!x");
        when(learner.labeler.discreteValue(word)).thenReturn("NN");

        learner.learn(word);
        Set<String> tags = learner.allowableTags(word);

        assertTrue(tags.contains("NN")); 
    }
@Test
    public void testReadAndWriteEmptyLearnerDoesNotFail() {
        MikheevLearner learner = new MikheevLearner("test");

        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ExceptionlessOutputStream eos = new ExceptionlessOutputStream(byteOut);
        learner.write(eos);

        byte[] binary = byteOut.toByteArray();

        MikheevLearner loaded = new MikheevLearner("clone");
        ByteArrayInputStream byteIn = new ByteArrayInputStream(binary);
        ExceptionlessInputStream eis = new ExceptionlessInputStream(byteIn);
        loaded.read(eis);
    }
@Test
    public void testPruneEliminatesEntireSuffixEntryIfTotalBelowThreshold() {
        MikheevLearner learner = new MikheevLearner("test");

        learner.extractor = mock(POSBaselineLearner.FeatureExtractor.class);
        learner.labeler = mock(POSBaselineLearner.Labeler.class);

        Word word = new Word("affix");
        word.capitalized = false;
        word.previous = new Word("an");

        when(learner.extractor.discreteValue(word)).thenReturn("affix");
        when(learner.labeler.discreteValue(word)).thenReturn("RB");

        learner.learn(word);
        learner.learn(word); learner.learn(word); learner.learn(word);

        learner.doneLearning();

        Set<String> tags = learner.allowableTags(word);
        assertEquals(1, tags.size());
        assertTrue(tags.contains("NN")); 
    }
@Test
    public void testWriteNonEmptyLearnerTextOutputContainsCapitalizedTables() {
        MikheevLearner learner = new MikheevLearner();
        learner.extractor = mock(POSBaselineLearner.FeatureExtractor.class);
        learner.labeler = mock(POSBaselineLearner.Labeler.class);

        Word word = new Word("Commander");
        word.capitalized = true;
        word.previous = null;

        when(learner.extractor.discreteValue(word)).thenReturn("Commander");
        when(learner.labeler.discreteValue(word)).thenReturn("NNP");

        learner.learn(word);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        learner.write(printStream);

        String output = outputStream.toString();
        assertTrue(output.contains("first word"));
        assertTrue(output.contains("main table"));
    }
@Test
    public void testAllowableTagsFallbackForCapitalizedWithShortForm() {
        MikheevLearner learner = new MikheevLearner("test");
        Word word = new Word("XY");
        word.capitalized = true;
        word.previous = new Word("is");

        Set<String> tags = learner.allowableTags(word);
        assertEquals(1, tags.size());
        assertTrue(tags.contains("NNP"));
    }
@Test
    public void testLearnDoesNotCrashWithFormExactlyThreeLetters() {
        MikheevLearner learner = new MikheevLearner("test");

        learner.extractor = mock(POSBaselineLearner.FeatureExtractor.class);
        learner.labeler = mock(POSBaselineLearner.Labeler.class);

        Word word = new Word("cat");
        word.capitalized = false;
        word.previous = new Word("a");

        when(learner.extractor.discreteValue(word)).thenReturn("cat");
        when(learner.labeler.discreteValue(word)).thenReturn("NN");

        learner.learn(word);

        Set<String> tags = learner.allowableTags(word);
        assertTrue(tags.contains("NN")); 
    }
@Test
    public void testLearnIgnoresExampleWithNullLabel() {
        MikheevLearner learner = new MikheevLearner("test");

        learner.extractor = mock(POSBaselineLearner.FeatureExtractor.class);
        learner.labeler = mock(POSBaselineLearner.Labeler.class);

        Word word = new Word("anything");
        word.capitalized = false;
        word.previous = new Word("to");

        when(learner.extractor.discreteValue(word)).thenReturn("anything");
        when(learner.labeler.discreteValue(word)).thenReturn(null);

        learner.learn(word);

        Set<String> tags = learner.allowableTags(word);
        assertTrue(tags.contains("NN"));
    }
@Test
    public void testAllowableTagsCapitalizedWith4LetterSuffixNotFoundThenCheck3Letter() {
        MikheevLearner learner = new MikheevLearner("test");

        learner.extractor = mock(edu.illinois.cs.cogcomp.pos.POSBaselineLearner.FeatureExtractor.class);
        learner.labeler = mock(edu.illinois.cs.cogcomp.pos.POSBaselineLearner.Labeler.class);

        Word word = new Word("President");
        word.capitalized = true;
        word.previous = null;

        when(learner.extractor.discreteValue(word)).thenReturn("President");
        when(learner.labeler.discreteValue(word)).thenReturn("NNP");

        learner.learn(word);

        Word testWord = new Word("President");
        testWord.capitalized = true;
        testWord.previous = null;

        Set<String> tags = learner.allowableTags(testWord);
        assertTrue(tags.contains("NNP"));
    }
@Test
    public void testAllowableTagsLowerCaseNoSuffixMatchReturnsDefaultWithHyphen() {
        MikheevLearner learner = new MikheevLearner("test");
        Word word = new Word("well-known");
        word.capitalized = false;
        word.previous = new Word("a");

        Set<String> tags = learner.allowableTags(word);
        assertTrue(tags.contains("NN"));
        assertTrue(tags.contains("JJ"));
    }
@Test
    public void testAllowableTagsLowerCaseWithSuffixLength4Only() {
        MikheevLearner learner = new MikheevLearner("test");
        learner.extractor = mock(edu.illinois.cs.cogcomp.pos.POSBaselineLearner.FeatureExtractor.class);
        learner.labeler = mock(edu.illinois.cs.cogcomp.pos.POSBaselineLearner.Labeler.class);

        Word word = new Word("nominal");
        word.capitalized = false;
        word.previous = new Word("be");

        when(learner.extractor.discreteValue(word)).thenReturn("nominal");
        when(learner.labeler.discreteValue(word)).thenReturn("JJ");

        learner.learn(word);

        Word test = new Word("nominal");
        test.capitalized = false;
        test.previous = new Word("be");

        Set<String> tags = learner.allowableTags(test);
        assertTrue(tags.contains("JJ"));
    }
@Test
    public void testIncrementCreatesNewTreeMapAndIncrementsProperly() {
        MikheevLearner learner = new MikheevLearner("test");
        learner.extractor = mock(edu.illinois.cs.cogcomp.pos.POSBaselineLearner.FeatureExtractor.class);
        learner.labeler = mock(edu.illinois.cs.cogcomp.pos.POSBaselineLearner.Labeler.class);

        Word word = new Word("Administrator");
        word.capitalized = true;
        word.previous = null;

        when(learner.extractor.discreteValue(word)).thenReturn("Administrator");
        when(learner.labeler.discreteValue(word)).thenReturn("NNP");

        learner.learn(word);

        Word test = new Word("Administrator");
        test.capitalized = true;
        test.previous = null;

        Set<String> tags = learner.allowableTags(test);
        assertTrue(tags.contains("NNP"));
    }
@Test
    public void testLearnSkipsWhenSuffix3LettersContainNonLetterCharacters() {
        MikheevLearner learner = new MikheevLearner("test");
        learner.extractor = mock(edu.illinois.cs.cogcomp.pos.POSBaselineLearner.FeatureExtractor.class);
        learner.labeler = mock(edu.illinois.cs.cogcomp.pos.POSBaselineLearner.Labeler.class);

        Word word = new Word("test1!");
        word.capitalized = false;
        word.previous = new Word("a");

        when(learner.extractor.discreteValue(word)).thenReturn("test1!");
        when(learner.labeler.discreteValue(word)).thenReturn("SYM");

        learner.learn(word);

        Set<String> tags = learner.allowableTags(word);
        assertTrue(tags.contains("NN")); 
    }
@Test
    public void testAllowableTagsCapitalizedWithNoMatchAddsFallback() {
        MikheevLearner learner = new MikheevLearner("test");

        Word word = new Word("UnknownCapital");
        word.capitalized = true;
        word.previous = new Word("has");

        Set<String> tags = learner.allowableTags(word);
        assertEquals(1, tags.size());
        assertTrue(tags.contains("NNP")); 
    }
@Test
    public void testAllowableTagsLowerCaseWithNoHyphenNoMatchFallbackNN() {
        MikheevLearner learner = new MikheevLearner("test");

        Word word = new Word("exampleword");
        word.capitalized = false;
        word.previous = new Word("an");

        Set<String> tags = learner.allowableTags(word);
        assertEquals(1, tags.size());
        assertTrue(tags.contains("NN")); 
    }
@Test
    public void testLearnWithLengthBoundaryExactly5Triggers3LetterSuffixOnly() {
        MikheevLearner learner = new MikheevLearner();
        learner.extractor = mock(edu.illinois.cs.cogcomp.pos.POSBaselineLearner.FeatureExtractor.class);
        learner.labeler = mock(edu.illinois.cs.cogcomp.pos.POSBaselineLearner.Labeler.class);

        Word word = new Word("bored");
        word.capitalized = false;
        word.previous = new Word("was");

        when(learner.extractor.discreteValue(word)).thenReturn("bored");
        when(learner.labeler.discreteValue(word)).thenReturn("VBN");

        learner.learn(word);

        Word test = new Word("bored");
        test.capitalized = false;
        test.previous = new Word("was");

        Set<String> tags = learner.allowableTags(test);
        assertTrue(tags.contains("VBN"));
    }
@Test
    public void testLearnWithLengthBoundaryExactly6TriggersBothSuffixLengths() {
        MikheevLearner learner = new MikheevLearner();
        learner.extractor = mock(edu.illinois.cs.cogcomp.pos.POSBaselineLearner.FeatureExtractor.class);
        learner.labeler = mock(edu.illinois.cs.cogcomp.pos.POSBaselineLearner.Labeler.class);

        Word word = new Word("trying");
        word.capitalized = false;
        word.previous = new Word("was");

        when(learner.extractor.discreteValue(word)).thenReturn("trying");
        when(learner.labeler.discreteValue(word)).thenReturn("VBG");

        learner.learn(word);

        Word test = new Word("trying");
        test.capitalized = false;
        test.previous = new Word("was");

        Set<String> tags = learner.allowableTags(test);
        assertTrue(tags.contains("VBG"));
    }
@Test
    public void testAllowableTagsCapitalizedWithHyphenStillUsesCapitalRule() {
        MikheevLearner learner = new MikheevLearner();
        learner.extractor = mock(edu.illinois.cs.cogcomp.pos.POSBaselineLearner.FeatureExtractor.class);
        learner.labeler = mock(edu.illinois.cs.cogcomp.pos.POSBaselineLearner.Labeler.class);

        Word word = new Word("X-Ray");
        word.capitalized = true;
        word.previous = null;

        when(learner.extractor.discreteValue(word)).thenReturn("X-Ray");
        when(learner.labeler.discreteValue(word)).thenReturn("NNP");

        learner.learn(word);

        Word test = new Word("X-Ray");
        test.capitalized = true;
        test.previous = null;

        Set<String> tags = learner.allowableTags(test);
        assertTrue(tags.contains("NNP"));
    }
@Test
    public void testDoneLearningWhenTablesAreEmptyDoesNothing() {
        MikheevLearner learner = new MikheevLearner("test");
        learner.doneLearning(); 
    }
@Test
    public void testWriteAndReadPreserveCapitalizedSuffixRules() {
        MikheevLearner learner = new MikheevLearner("test");
        learner.extractor = mock(MikheevLearner.FeatureExtractor.class);
        learner.labeler = mock(MikheevLearner.Labeler.class);

        Word word = new Word("Witness");
        word.capitalized = true;
        word.previous = null;

        when(learner.extractor.discreteValue(word)).thenReturn("Witness");
        when(learner.labeler.discreteValue(word)).thenReturn("NNP");

        learner.learn(word);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ExceptionlessOutputStream eos = new ExceptionlessOutputStream(outputStream);
        learner.write(eos);

        byte[] serialized = outputStream.toByteArray();

        MikheevLearner reloaded = new MikheevLearner("reloaded");
        ExceptionlessInputStream eis = new ExceptionlessInputStream(new ByteArrayInputStream(serialized));
        reloaded.read(eis);

        Word test = new Word("Witness");
        test.capitalized = true;
        test.previous = null;
        Set<String> tags = reloaded.allowableTags(test);
        assertTrue(tags.contains("NNP"));
    }
@Test
    public void testForgetResetsAllSuffixRules() {
        MikheevLearner learner = new MikheevLearner("test");
        learner.extractor = mock(MikheevLearner.FeatureExtractor.class);
        learner.labeler = mock(MikheevLearner.Labeler.class);

        Word word = new Word("Reactor");
        word.capitalized = true;
        word.previous = null;

        when(learner.extractor.discreteValue(word)).thenReturn("Reactor");
        when(learner.labeler.discreteValue(word)).thenReturn("NNP");

        learner.learn(word);
        learner.forget();

        Word test = new Word("Reactor");
        test.capitalized = true;
        test.previous = null;
        Set<String> tags = learner.allowableTags(test);
        assertTrue(tags.contains("NNP")); 
    }
@Test
    public void testSuffixCaseNormalizationIsLowercaseInLearning() {
        MikheevLearner learner = new MikheevLearner("test");
        learner.extractor = mock(MikheevLearner.FeatureExtractor.class);
        learner.labeler = mock(MikheevLearner.Labeler.class);

        Word word = new Word("GROWING");
        word.capitalized = false;
        word.previous = new Word("be");

        when(learner.extractor.discreteValue(word)).thenReturn("GROWING");
        when(learner.labeler.discreteValue(word)).thenReturn("VBG");

        learner.learn(word);

        Word test = new Word("growing");
        test.capitalized = false;
        test.previous = new Word("be");

        Set<String> tags = learner.allowableTags(test);
        assertTrue(tags.contains("VBG"));
    }
@Test
    public void testCapitalizedHyphenatedFallbackCorrectlyReturnsNNP() {
        MikheevLearner learner = new MikheevLearner();
        Word word = new Word("X-Ray");
        word.capitalized = true;
        word.previous = null;

        Set<String> tags = learner.allowableTags(word);
        assertEquals(1, tags.size());
        assertTrue(tags.contains("NNP"));
    }
@Test
    public void testSuffixAppearsInMultipleCapitalizationTablesIsSeparated() {
        MikheevLearner learner = new MikheevLearner("test");

        learner.extractor = mock(MikheevLearner.FeatureExtractor.class);
        learner.labeler = mock(MikheevLearner.Labeler.class);

        Word capWord = new Word("Exploration");
        capWord.capitalized = true;
        capWord.previous = null;

        Word lowWord = new Word("exploration");
        lowWord.capitalized = false;
        lowWord.previous = new Word("on");

        when(learner.extractor.discreteValue(capWord)).thenReturn("Exploration");
        when(learner.labeler.discreteValue(capWord)).thenReturn("NNP");

        when(learner.extractor.discreteValue(lowWord)).thenReturn("exploration");
        when(learner.labeler.discreteValue(lowWord)).thenReturn("NN");

        learner.learn(capWord);
        learner.learn(lowWord);

        Set<String> tagsCap = learner.allowableTags(capWord);
        Set<String> tagsLow = learner.allowableTags(lowWord);

        assertTrue(tagsCap.contains("NNP"));
        assertTrue(tagsLow.contains("NN"));
        assertFalse(tagsCap.contains("NN"));
        assertFalse(tagsLow.contains("NNP"));
    }
@Test
    public void testLearnOnWordWithDigitSuffixDoesNotLearn() {
        MikheevLearner learner = new MikheevLearner();
        learner.extractor = mock(MikheevLearner.FeatureExtractor.class);
        learner.labeler = mock(MikheevLearner.Labeler.class);

        Word word = new Word("word123");
        word.capitalized = false;
        word.previous = new Word("some");

        when(learner.extractor.discreteValue(word)).thenReturn("word123");
        when(learner.labeler.discreteValue(word)).thenReturn("CD");

        learner.learn(word);

        Set<String> tags = learner.allowableTags(word);
        assertTrue(tags.contains("NN")); 
    }
@Test
    public void testAllowableTagsEmptySuffixLookupsResultInFallback() {
        MikheevLearner learner = new MikheevLearner("test");

        Word word = new Word("zzz");
        word.capitalized = false;
        word.previous = new Word("to");

        Set<String> tags = learner.allowableTags(word);
        assertEquals(1, tags.size());
        assertTrue(tags.contains("NN"));
    }
@Test
    public void testLearnOnMinimalAcceptableSuffixWordLengthBoundary() {
        MikheevLearner learner = new MikheevLearner();
        learner.extractor = mock(MikheevLearner.FeatureExtractor.class);
        learner.labeler = mock(MikheevLearner.Labeler.class);

        Word word = new Word("abcde");
        word.capitalized = false;
        word.previous = new Word("prefix");

        when(learner.extractor.discreteValue(word)).thenReturn("abcde");
        when(learner.labeler.discreteValue(word)).thenReturn("XX");

        learner.learn(word);

        Word test = new Word("abcde");
        test.capitalized = false;
        test.previous = new Word("prefix");

        Set<String> tags = learner.allowableTags(test);
        assertTrue(tags.contains("XX"));
    }
@Test
    public void testLearnIgnoresLexiconWhenFormIsNull() {
        MikheevLearner learner = new MikheevLearner();
        learner.extractor = mock(MikheevLearner.FeatureExtractor.class);
        learner.labeler = mock(MikheevLearner.Labeler.class);

        Object nonWord = new Object();

        when(learner.extractor.discreteValue(nonWord)).thenReturn(null);
        when(learner.labeler.discreteValue(nonWord)).thenReturn("NN");

        learner.learn(nonWord); 
    } 
}