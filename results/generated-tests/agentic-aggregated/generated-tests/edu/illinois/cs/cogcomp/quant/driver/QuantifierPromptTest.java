package edu.illinois.cs.cogcomp.quant.driver;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.SpanLabelView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.View;
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager;
import edu.illinois.cs.cogcomp.nlp.tokenizer.StatefulTokenizer;
import edu.illinois.cs.cogcomp.nlp.utility.TokenizerTextAnnotationBuilder;
import java.util.List;
import java.util.Properties;
import org.junit.Test;

import static org.junit.Assert.*;

public class QuantifierPromptTest {

    @Test
    public void defaultConstructorIsLazyAndDeclaresQuantitiesView() {
        Quantifier quantifier = new Quantifier();

        assertFalse(quantifier.isInitialized());
        assertEquals("QUANTITIES", quantifier.getViewName());
        assertArrayEquals(new String[0], quantifier.getRequiredViews());
        assertTrue(quantifier.isSentenceLevel());
    }

    @Test
    public void explicitLazyConstructorDefersInitialization() {
        Quantifier quantifier = new Quantifier(true);

        assertFalse(quantifier.isInitialized());
        assertEquals("QUANTITIES", quantifier.getViewName());
    }

    @Test
    public void eagerConstructorInitializesModel() {
        Quantifier quantifier = new Quantifier(false);

        assertTrue(quantifier.isInitialized());
        assertEquals("QUANTITIES", quantifier.getViewName());
    }

    @Test
    public void doInitializeTransitionsLazyInstanceAndIsRepeatable() {
        Quantifier quantifier = new Quantifier(true);

        quantifier.doInitialize();
        assertTrue(quantifier.isInitialized());
        quantifier.doInitialize();
        assertTrue(quantifier.isInitialized());
    }

    @Test
    public void wordSplitSeparatesAlphabeticHyphen() {
        Quantifier quantifier = new Quantifier(true);
        quantifier.initialize(new ResourceManager(new Properties()));

        assertEquals("well - known", Quantifier.wordsplitSentence("well-known"));
    }

    @Test
    public void wordSplitHandlesLeadingAndTrailingHyphens() {
        Quantifier quantifier = new Quantifier(true);
        quantifier.initialize(new ResourceManager(new Properties()));

        assertEquals("- word word -", Quantifier.wordsplitSentence("-word word-"));
    }

    @Test
    public void wordSplitPreservesNegativeNumber() {
        Quantifier quantifier = new Quantifier(true);
        quantifier.initialize(new ResourceManager(new Properties()));

        assertEquals("temperature -5", Quantifier.wordsplitSentence("temperature -5"));
    }

    @Test
    public void wordSplitSeparatesNumericHyphenAccordingToOrderedRules() {
        Quantifier quantifier = new Quantifier(true);
        quantifier.initialize(new ResourceManager(new Properties()));

        assertEquals("5 -7", Quantifier.wordsplitSentence("5-7"));
    }

    @Test
    public void wordSplitRemovesAllInternalThousandsCommas() {
        Quantifier quantifier = new Quantifier(true);
        quantifier.initialize(new ResourceManager(new Properties()));

        assertEquals("Population 1234567.", Quantifier.wordsplitSentence("Population 1,234,567."));
    }

    @Test
    public void wordSplitKeepsCommaThatIsNotBetweenDigits() {
        Quantifier quantifier = new Quantifier(true);
        quantifier.initialize(new ResourceManager(new Properties()));

        assertEquals("one, two and 12,", Quantifier.wordsplitSentence("one, two and 1,2,"));
    }

    @Test
    public void wordSplitSeparatesPrefixDollarSign() {
        Quantifier quantifier = new Quantifier(true);
        quantifier.initialize(new ResourceManager(new Properties()));

        assertEquals("It costs $ 25.", Quantifier.wordsplitSentence("It costs $25."));
    }

    @Test
    public void wordSplitSeparatesSuffixDollarSign() {
        Quantifier quantifier = new Quantifier(true);
        quantifier.initialize(new ResourceManager(new Properties()));

        assertEquals("It costs 25 $.", Quantifier.wordsplitSentence("It costs 25$."));
    }

    @Test
    public void wordSplitSeparatesPercentSign() {
        Quantifier quantifier = new Quantifier(true);
        quantifier.initialize(new ResourceManager(new Properties()));

        assertEquals("Growth was 12 %.", Quantifier.wordsplitSentence("Growth was 12%."));
    }

    @Test
    public void wordSplitAppliesSeveralTransformationsInOneSentence() {
        Quantifier quantifier = new Quantifier(true);
        quantifier.initialize(new ResourceManager(new Properties()));

        assertEquals("A well - known item costs $ 1234 and rose 5 %.",
                Quantifier.wordsplitSentence("A well-known item costs $1,234 and rose 5%."));
    }

    @Test
    public void wordSplitLeavesPlainTextAndEmptyStringUnchanged() {
        Quantifier quantifier = new Quantifier(true);
        quantifier.initialize(new ResourceManager(new Properties()));

        assertEquals("plain words", Quantifier.wordsplitSentence("plain words"));
        assertEquals("", Quantifier.wordsplitSentence(""));
    }

    @Test(expected = NullPointerException.class)
    public void wordSplitRejectsNullSentence() {
        Quantifier quantifier = new Quantifier(true);
        quantifier.initialize(new ResourceManager(new Properties()));

        Quantifier.wordsplitSentence(null);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getSpansWithEmptyInternallyBuiltAnnotationExposesPosBoundaryFailure()
            throws Exception {
        Quantifier quantifier = new Quantifier(false);

        quantifier.getSpans("", true, null);
    }

    @Test
    public void getSpansUsesAnnotationBuiltByAnotherComponentInsteadOfText() throws Exception {
        Quantifier quantifier = new Quantifier(false);
        TokenizerTextAnnotationBuilder builder =
                new TokenizerTextAnnotationBuilder(new StatefulTokenizer());
        TextAnnotation annotation = builder.createTextAnnotation("");
        annotation.addView("POS", new SpanLabelView("POS", "test", annotation, 1.0));

        List<QuantSpan> spans = quantifier.getSpans("ignored alternate text", true, annotation);

        assertNotNull(spans);
        assertTrue(spans.isEmpty());
    }

    @Test
    public void getSpansWithoutStandardizationHandlesEmptyText() throws Exception {
        Quantifier quantifier = new Quantifier(false);
        TokenizerTextAnnotationBuilder builder =
                new TokenizerTextAnnotationBuilder(new StatefulTokenizer());
        TextAnnotation annotation = builder.createTextAnnotation("");
        annotation.addView("POS", new SpanLabelView("POS", "test", annotation, 1.0));

        List<QuantSpan> spans = quantifier.getSpans("", false, annotation);

        assertTrue(spans.isEmpty());
    }

    @Test(expected = NullPointerException.class)
    public void getSpansReportsUnavailableClassifierForNonEmptyText() throws Exception {
        Quantifier quantifier = new Quantifier(false);

        quantifier.getSpans("The price is $25.", true, null);
    }

    @Test
    public void addViewThroughAnnotatorApiAddsEmptyQuantitiesView() throws Exception {
        Quantifier quantifier = new Quantifier(true);
        TokenizerTextAnnotationBuilder builder =
                new TokenizerTextAnnotationBuilder(new StatefulTokenizer());
        TextAnnotation annotation = builder.createTextAnnotation("");
        annotation.addView("POS", new SpanLabelView("POS", "test", annotation, 1.0));

        View view = quantifier.getView(annotation);

        assertTrue(quantifier.isInitialized());
        assertSame(view, annotation.getView("QUANTITIES"));
        assertEquals("QUANTITIES", view.getViewName());
        assertTrue(view.getConstituents().isEmpty());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getAnnotatedStringExposesEmptyInputPosBoundaryFailure() throws Exception {
        Quantifier quantifier = new Quantifier(false);

        quantifier.getAnnotatedString("", true);
    }

    @Test(expected = NullPointerException.class)
    public void getAnnotatedStringPropagatesClassifierFailureForNonEmptyInput() throws Exception {
        Quantifier quantifier = new Quantifier(false);

        quantifier.getAnnotatedString("Nothing was measured.", true);
    }
}
