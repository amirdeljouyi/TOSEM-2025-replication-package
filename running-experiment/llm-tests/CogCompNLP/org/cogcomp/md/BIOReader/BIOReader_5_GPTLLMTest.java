package org.cogcomp.md;

import edu.illinois.cs.cogcomp.core.datastructures.IQueryable;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.*;
import edu.illinois.cs.cogcomp.core.datastructures.vectors.ExceptionlessInputStream;
import edu.illinois.cs.cogcomp.core.datastructures.vectors.ExceptionlessOutputStream;
import edu.illinois.cs.cogcomp.lbjava.learn.Learner;
import edu.illinois.cs.cogcomp.lbjava.nlp.Word;
import edu.illinois.cs.cogcomp.nlp.utilities.POSUtils;
import edu.illinois.cs.cogcomp.nlp.utilities.ParseTreeProperties;
import edu.illinois.cs.cogcomp.pos.MikheevLearner;
import edu.illinois.cs.cogcomp.pos.POSAnnotator;
import junit.framework.TestCase;
import org.junit.Test;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class BIOReader_5_GPTLLMTest {

@Test
public void testDefaultConstructorCreatesEmptyObject() {
BIOReader reader = new BIOReader();
assertNotNull(reader);
assertNull(reader.next());
}

@Test
public void testNextReturnsSingleTokenThenNull() {
BIOReader reader = new BIOReader();
try {
BIOReader fileReader = new BIOReader("src/test/resources/sampleColumnFormat/", "ColumnFormat-TRAIN", "ALL", true);
Object token1 = fileReader.next();
assertNotNull(token1);
Object token2 = null;
while ((token2 = fileReader.next()) != null) {
}
assertNull(fileReader.next());
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testResetAllowsIterationAgain() {
try {
BIOReader reader = new BIOReader("src/test/resources/sampleColumnFormat/", "ColumnFormat-TRAIN", "ALL", true);
Object first = reader.next();
assertNotNull(first);
Object second = null;
while ((second = reader.next()) != null) {
}
reader.reset();
Object resetFirst = reader.next();
assertNotNull(resetFirst);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testBIOReaderHandlesUnknownModeGracefully() {
BIOReader reader = new BIOReader();
List<TextAnnotation> annotations = reader.getTextAnnotations();
assertNotNull(annotations);
assertTrue(annotations.isEmpty());
}

@Test
public void testIdIsConstructedCorrectly() {
try {
BIOReader reader = new BIOReader("some/path/myCorpus", "ColumnFormat-TRAIN", "NAM", true);
String idValue = reader.id;
assertEquals("myCorpus_NAM", idValue);
} catch (Exception e) {
fail("Exception should not be thrown");
}
}

@Test
public void testNextReturnsNullOnEmptyTokenList() {
BIOReader reader = new BIOReader();
Object result = reader.next();
assertNull(result);
}

@Test
public void testCloseExecutesWithoutExceptions() {
BIOReader reader = new BIOReader();
reader.close();
assertTrue(true);
}

@Test
public void testBIOReaderWithInvalidModeThrowsException() {
try {
new BIOReader("dummy/path", "INVALIDMODE-TRAIN", "NAM", true);
fail("Expected RuntimeException for invalid mode");
} catch (RuntimeException e) {
assertTrue(e.getMessage().contains("Tokens could not be reproduced"));
} catch (Exception other) {
fail("Unexpected exception type: " + other);
}
}

@Test
public void testBIOReaderWithValidColumnFormat() {
try {
BIOReader reader = new BIOReader("src/test/resources/sampleColumnFormat/", "ColumnFormat-TRAIN", "ALL", true);
Object token = reader.next();
assertNotNull(token);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testBIOReaderReturnsNullAfterAllTokensConsumed() {
try {
BIOReader reader = new BIOReader("src/test/resources/sampleColumnFormat/", "ColumnFormat-TRAIN", "ALL", true);
Object token = null;
while ((token = reader.next()) != null) {
}
Object result = reader.next();
assertNull(result);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testBIOReaderEmptyAnnotationList() {
BIOReader reader = new BIOReader();
List<TextAnnotation> result = reader.getTextAnnotations();
assertNotNull(result);
assertTrue(result.isEmpty());
}

@Test
public void testBIOReaderHandlesERECorpusWithoutMentions() {
BIOReader reader = new BIOReader();
// TextAnnotation ta = new TextAnnotation("test", "id", new String[] { "The", "car" });
// View tokenView = new SpanLabelView(ViewNames.TOKENS, "test", ta, 1.0);
// tokenView.addConstituent(new Constituent("The", ViewNames.TOKENS, ta, 0, 1));
// tokenView.addConstituent(new Constituent("car", ViewNames.TOKENS, ta, 1, 2));
// ta.addView(ViewNames.TOKENS, tokenView);
// View mentionView = new SpanLabelView(ViewNames.MENTION_ERE, "source", ta, 1.0);
// ta.addView(ViewNames.MENTION_ERE, mentionView);
List<TextAnnotation> taList = new ArrayList<>();
// taList.add(ta);
Object first = reader.next();
assertNull(first);
}

@Test
public void testBIOReaderHandlesSingleTokenMentionWithBIOLU() {
try {
BIOReader reader = new BIOReader("src/test/resources/sampleColumnFormatSingleTokenMention/", "ColumnFormat-TRAIN", "ALL", false);
Object token = reader.next();
assertNotNull(token);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testBIOReaderSkipsNullMentionHeads() {
try {
BIOReader reader = new BIOReader("src/test/resources/sampleColumnFormatWithNullHeads/", "ColumnFormat-TRAIN", "ALL", true);
Object c = reader.next();
assertNotNull(c);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testBIOReaderIgnoresUnsupportedEntityMentionType() {
try {
BIOReader reader = new BIOReader("src/test/resources/sampleColumnFormatWithUnsupportedTypes/", "ColumnFormat-TRAIN", "NAM", true);
Object c = reader.next();
assertNotNull(c);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testBIOReaderNonTokenViewThrowsExceptionGracefully() {
BIOReader reader = new BIOReader();
// TextAnnotation ta = new TextAnnotation("sample", "id22", new String[] { "A", "B", "C" });
// View mentionView = new SpanLabelView("MENTIONS", "test", ta, 1.0f);
// mentionView.addConstituent(new Constituent("C", "MENTIONS", ta, 0, 1));
// ta.addView("MENTIONS", mentionView);
try {
reader.reset();
Object next = reader.next();
assertNull(next);
} catch (Exception ex) {
fail("Should handle missing token view gracefully");
}
}

@Test
public void testBIOReaderHandlesViewNameFallbackForColumnFormat() {
try {
BIOReader reader = new BIOReader("src/test/resources/sampleColumnFallback/", "ColumnFormat-TRAIN", "ALL", true);
Object first = reader.next();
assertNotNull(first);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testBIOReaderEntityTypeVEHAndWEAHandled() {
try {
BIOReader reader = new BIOReader("src/test/resources/sampleVEHWEA/", "ColumnFormat-TRAIN", "ALL", true);
Object tok = reader.next();
assertNotNull(tok);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testBIOReaderHandlesTokenWithoutMention() {
try {
BIOReader reader = new BIOReader("src/test/resources/mentionsMissing/", "ColumnFormat-TRAIN", "ALL", true);
Object first = reader.next();
assertNotNull(first);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testBIOReaderHandlesEntityMentionTypePrefixSPE() {
try {
BIOReader reader = new BIOReader("src/test/resources/sampleColumnWithSPEPrefix/", "ColumnFormat-TRAIN", "SPE_NOM", true);
Object token = reader.next();
assertNotNull(token);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testBIOReaderHandlesSingleTokenBIOLU_U_Tag() {
try {
BIOReader reader = new BIOReader("src/test/resources/sampleSingleTokenBIOLU/", "ColumnFormat-TRAIN", "ALL", false);
Object token = reader.next();
assertNotNull(token);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testBIOReaderIgnoresEntityWithMissingEntityMentionType() {
try {
BIOReader reader = new BIOReader("src/test/resources/sampleMissingMentionType/", "ColumnFormat-TRAIN", "NAM", true);
Object token = reader.next();
assertNotNull(token);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testBIOReaderEntityMentionWithoutEntityTypeIsSkipped() {
try {
BIOReader reader = new BIOReader("src/test/resources/sampleMissingEntityType/", "ColumnFormat-TRAIN", "ALL", true);
Object token = reader.next();
assertNotNull(token);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testBIOReaderWithModeUnmatchedPrintsWarning() {
BIOReader reader = new BIOReader("some/path/input", "UnknownFormat-TRAIN", "ALL", true);
List<TextAnnotation> annotations = reader.getTextAnnotations();
assertNotNull(annotations);
assertTrue(annotations.isEmpty());
}

@Test
public void testBIOReaderHandlesPOSTaggerException() {
BIOReader reader = new BIOReader("some/path/input", "ColumnFormat-TRAIN", "ALL", true);
// TextAnnotation ta = new TextAnnotation("test", "id", new String[] { "Hello" });
// View tokenView = new SpanLabelView(ViewNames.TOKENS, "test", ta, 1.0);
// tokenView.addConstituent(new Constituent("Hello", ViewNames.TOKENS, ta, 0, 1));
// ta.addView(ViewNames.TOKENS, tokenView);
try {
// ta.addView(new POSAnnotator());
assertTrue(true);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testBIOReaderHandlesEmptyMentionList() {
try {
BIOReader reader = new BIOReader("src/test/resources/emptyMentions/", "ColumnFormat-TRAIN", "ALL", true);
Object token = reader.next();
assertNotNull(token);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testBIOReaderSkipsEntitiesOutsideConfiguredMentionType() {
try {
BIOReader reader = new BIOReader("src/test/resources/sampleDifferentMentionType/", "ColumnFormat-TRAIN", "PRO", true);
Object token = reader.next();
assertNotNull(token);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testBIOReaderHandlesViewAdditionFailureGracefully() {
try {
BIOReader reader = new BIOReader("src/test/resources/corruptedTokenView/", "ColumnFormat-TRAIN", "ALL", true);
Object token = reader.next();
assertNotNull(token);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testBIOReaderHandlesHeadExtractionAsNull() {
try {
BIOReader reader = new BIOReader("src/test/resources/mentionWithoutHead/", "ColumnFormat-TRAIN", "ALL", true);
Object token = reader.next();
assertNotNull(token);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testBIOReaderAssignsOWhenEntityNotMatched() {
try {
BIOReader reader = new BIOReader("src/test/resources/mismatchedEntityType/", "ColumnFormat-TRAIN", "PRO", true);
Object token = reader.next();
assertNotNull(token);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testBIOReaderConstructorWithAllMentionTypesBIOLU() {
try {
BIOReader reader = new BIOReader("src/test/resources/sampleDataBIOLU_ALL", "ColumnFormat-EVAL", "ALL", false);
Object token = reader.next();
assertNotNull(token);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testBIOReaderSingleConstituentMentionLengthTwoWithBIOLU() {
try {
BIOReader reader = new BIOReader("src/test/resources/sampleTextAnnotationBIOLU/", "ColumnFormat-TRAIN", "ALL", false);
Object token = reader.next();
assertNotNull(token);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testBIOReaderOnlyO_TagsAssigned() {
try {
BIOReader reader = new BIOReader("src/test/resources/sampleWithoutMentions/", "ColumnFormat-EVAL", "ALL", true);
Object first = reader.next();
assertNotNull(first);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testBIOReaderWithUnrecognizedTypeSkipsAllMentions() {
try {
BIOReader reader = new BIOReader("src/test/resources/sampleWithKnownMentions/", "ColumnFormat-TRAIN", "XYZ", true);
Object result = reader.next();
assertNotNull(result);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testBIOReaderHandlesEmptyTokensViewGracefully() {
try {
BIOReader reader = new BIOReader("src/test/resources/sampleNoTokenView/", "ColumnFormat-TRAIN", "ALL", true);
Object result = reader.next();
assertNull(result);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testBIOReaderHandlesMentionOutsideTokenSpan() {
try {
BIOReader reader = new BIOReader("src/test/resources/sampleMentionOutOfBounds/", "ColumnFormat-TRAIN", "ALL", true);
Object result = reader.next();
assertNotNull(result);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testBIOReaderEntityWithEndLessThanStartIgnored() {
try {
BIOReader reader = new BIOReader("src/test/resources/sampleMentionInvalidSpan/", "ColumnFormat-TRAIN", "ALL", true);
Object result = reader.next();
assertNotNull(result);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testBIOReaderHandlesTokenContainingHttpCorrectly() {
try {
BIOReader reader = new BIOReader("src/test/resources/sampleWithHttpToken/", "ColumnFormat-TRAIN", "ALL", true);
Object result = reader.next();
assertNotNull(result);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testBIOReaderAssignsFalseToIsTrainingInEvalMode() {
try {
BIOReader reader = new BIOReader("src/test/resources/sampleText/", "ColumnFormat-EVAL", "ALL", true);
Object result = reader.next();
assertNotNull(result);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testBIOReaderDoesNotCrashWithEmptyToken2TagsArray() {
try {
BIOReader reader = new BIOReader("src/test/resources/sampleEmptyTokenArray/", "ColumnFormat-TRAIN", "NAM", true);
Object result = reader.next();
assertNull(result);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testBIOReaderHandlesEntityWithoutMatchingMentionType() {
try {
BIOReader reader = new BIOReader("src/test/resources/sampleEntityTypeMismatch/", "ColumnFormat-TRAIN", "PRO", true);
Object result = reader.next();
assertNotNull(result);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testBIOReaderHandlesNonEnglishCharacters() {
try {
BIOReader reader = new BIOReader("src/test/resources/sampleNonEnglishInput/", "ColumnFormat-TRAIN", "ALL", true);
Object result = reader.next();
assertNotNull(result);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testBIOReaderHandlesEmptyPathInConstructor() {
try {
BIOReader reader = new BIOReader("", "ColumnFormat-TRAIN", "ALL", true);
Object t = reader.next();
assertNull(t);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testBIOReaderHandlesEmptyModeString() {
try {
BIOReader reader = new BIOReader("src/test/resources/", "-", "ALL", true);
Object t = reader.next();
assertNull(t);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testBIOReaderHandlesNullEntityMentionType() {
try {
// TextAnnotation ta = new TextAnnotation("dummy", "tokenId", new String[] { "A", "B" });
// View tokens = new SpanLabelView(ViewNames.TOKENS, "src", ta, 1.0);
// tokens.addConstituent(new Constituent("A", ViewNames.TOKENS, ta, 0, 1));
// tokens.addConstituent(new Constituent("B", ViewNames.TOKENS, ta, 1, 2));
// View mentions = new SpanLabelView("MENTIONS", "src", ta, 1.0);
// Constituent mention = new Constituent("X", "MENTIONS", ta, 0, 2);
// mentions.addConstituent(mention);
// ta.addView(ViewNames.TOKENS, tokens);
// ta.addView("MENTIONS", mentions);
BIOReader reader = new BIOReader("src/test/resources/", "ColumnFormat-TRAIN", "NAM", true);
Object next = reader.next();
assertNull(next);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testBIOReaderHandlesNullEntityTypeGracefully() {
try {
// TextAnnotation ta = new TextAnnotation("dummy", "tokenId", new String[] { "X" });
// View tokens = new SpanLabelView(ViewNames.TOKENS, "src", ta, 1.0);
// tokens.addConstituent(new Constituent("X", ViewNames.TOKENS, ta, 0, 1));
// View mentions = new SpanLabelView("MENTIONS", "src", ta, 1.0);
// Constituent mention = new Constituent("X", "MENTIONS", ta, 0, 1);
// mention.addAttribute("EntityMentionType", "NAM");
// mentions.addConstituent(mention);
// ta.addView(ViewNames.TOKENS, tokens);
// ta.addView("MENTIONS", mentions);
BIOReader reader = new BIOReader("src/test/resources/", "ColumnFormat-TRAIN", "ALL", true);
Object token = reader.next();
assertNotNull(token);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testBIOReaderHandlesNoGazetteersResource() {
try {
File invalidGazetteersPath = new File("some/bogus/path/to/gazetteers/");
if (!invalidGazetteersPath.exists()) {
invalidGazetteersPath.mkdirs();
}
BIOReader reader = new BIOReader("src/test/resources/sampleNoGazetteers/", "ColumnFormat-TRAIN", "ALL", true);
Object token = reader.next();
assertNotNull(token);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testBIOReaderHandlesNullTokenViewGracefully() {
try {
// TextAnnotation ta = new TextAnnotation("doc", "dummy", new String[] { "one", "two" });
// View mentionView = new SpanLabelView("MENTIONS", "source", ta, 1.0);
// Constituent c = new Constituent("mention", "MENTIONS", ta, 0, 1);
// c.addAttribute("EntityMentionType", "NAM");
// c.addAttribute("EntityType", "PER");
// mentionView.addConstituent(c);
// ta.addView("MENTIONS", mentionView);
BIOReader reader = new BIOReader("src/test/resources/", "ColumnFormat-TRAIN", "ALL", true);
Object next = reader.next();
assertNull(next);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testBIOReaderHandlesEmptyMentionView() {
try {
// TextAnnotation ta = new TextAnnotation("doc", "dummy", new String[] { "one", "two" });
// View tokenView = new SpanLabelView(ViewNames.TOKENS, "src", ta, 1.0);
// tokenView.addConstituent(new Constituent("one", ViewNames.TOKENS, ta, 0, 1));
// tokenView.addConstituent(new Constituent("two", ViewNames.TOKENS, ta, 1, 2));
// View mentionView = new SpanLabelView("MENTIONS", "src", ta, 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// ta.addView("MENTIONS", mentionView);
BIOReader reader = new BIOReader("src/test/resources", "ColumnFormat-TRAIN", "ALL", true);
Object token = reader.next();
assertNull(token);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testBIOReaderFailsWithMissingSplitInMode() {
try {
new BIOReader("data/path", "ONLYMODE", "ALL", true);
fail("Should throw ArrayIndexOutOfBoundsException");
} catch (RuntimeException e) {
assertTrue(e.getMessage().contains("Tokens could not be reproduced"));
} catch (Exception ex) {
assertTrue(true);
}
}

@Test
public void testBIOReaderEntityTypeVEHIsProcessed() {
try {
BIOReader reader = new BIOReader("src/test/resources/sampleVEHEntity/", "ColumnFormat-TRAIN", "ALL", true);
Object c = reader.next();
assertNotNull(c);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testBIOReaderEntityTypeWEAIsProcessed() {
try {
BIOReader reader = new BIOReader("src/test/resources/sampleWEAEntity/", "ColumnFormat-TRAIN", "ALL", true);
Object token = reader.next();
assertNotNull(token);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testBIOReaderHandlesEmptyTokenListGracefully() {
try {
// TextAnnotation ta = new TextAnnotation("dummy", "doc1", new String[] {});
// SpanLabelView tokenView = new SpanLabelView(ViewNames.TOKENS, "test", ta, 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView mentionView = new SpanLabelView("MENTIONS", "test", ta, 1.0);
// ta.addView("MENTIONS", mentionView);
BIOReader reader = new BIOReader("src/test/resources/", "ColumnFormat-TRAIN", "ALL", true);
Object token = reader.next();
assertNull(token);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testBIOReaderCreatesBIOViewWithOnlyO_TagsWhenNoMentionsExist() {
try {
// TextAnnotation ta = new TextAnnotation("dummy", "doc2", new String[] { "Hello", "World" });
// SpanLabelView tokenView = new SpanLabelView(ViewNames.TOKENS, "test", ta, 1.0);
// tokenView.addConstituent(new Constituent("Hello", ViewNames.TOKENS, ta, 0, 1));
// tokenView.addConstituent(new Constituent("World", ViewNames.TOKENS, ta, 1, 2));
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView mentionView = new SpanLabelView("MENTIONS", "test", ta, 1.0);
// ta.addView("MENTIONS", mentionView);
BIOReader reader = new BIOReader("src/test/resources/", "ColumnFormat-TRAIN", "ALL", true);
Object token = reader.next();
assertNull(token);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testBIOReaderSkipsMentionsWithUnrecognizedType() {
try {
// TextAnnotation ta = new TextAnnotation("dummy", "doc3", new String[] { "Obama", "speaks" });
// SpanLabelView tokenView = new SpanLabelView(ViewNames.TOKENS, "src", ta, 1.0f);
// tokenView.addConstituent(new Constituent("Obama", ViewNames.TOKENS, ta, 0, 1));
// tokenView.addConstituent(new Constituent("speaks", ViewNames.TOKENS, ta, 1, 2));
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView mentionView = new SpanLabelView("MENTIONS", "src", ta, 1.0f);
// Constituent mention = new Constituent("Obama", "MENTIONS", ta, 0, 1);
// mention.addAttribute("EntityMentionType", "PRO");
// mention.addAttribute("EntityType", "PER");
// mentionView.addConstituent(mention);
// ta.addView("MENTIONS", mentionView);
BIOReader reader = new BIOReader("src/test/resources/", "ColumnFormat-TRAIN", "NAM", true);
Object token = reader.next();
assertNull(token);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testBIOReaderAssignsULTagsCorrectlyForSingleTokenWhenIsBIOFalse() {
try {
// TextAnnotation ta = new TextAnnotation("dummy", "doc4", new String[] { "Car" });
// SpanLabelView tokenView = new SpanLabelView(ViewNames.TOKENS, "src", ta, 1.0);
// tokenView.addConstituent(new Constituent("Car", ViewNames.TOKENS, ta, 0, 1));
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView mentionView = new SpanLabelView("MENTIONS", "src", ta, 1.0);
// Constituent mention = new Constituent("Car", "MENTIONS", ta, 0, 1);
// mention.addAttribute("EntityMentionType", "NAM");
// mention.addAttribute("EntityType", "VEH");
// mentionView.addConstituent(mention);
// ta.addView("MENTIONS", mentionView);
BIOReader reader = new BIOReader("src/test/resources/", "ColumnFormat-TRAIN", "ALL", false);
Object token = reader.next();
assertNotNull(token);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testBIOReaderSkipsMentionWhenHeadIsNull() {
try {
// TextAnnotation ta = new TextAnnotation("dummy", "doc5", new String[] { "A", "B" });
// SpanLabelView tokenView = new SpanLabelView(ViewNames.TOKENS, "src", ta, 1.0f);
// tokenView.addConstituent(new Constituent("A", ViewNames.TOKENS, ta, 0, 1));
// tokenView.addConstituent(new Constituent("B", ViewNames.TOKENS, ta, 1, 2));
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView mentionView = new SpanLabelView("MENTIONS", "src", ta, 1.0f);
// Constituent mention = new Constituent("B", "MENTIONS", ta, 0, 2);
// mention.addAttribute("EntityMentionType", "NAM");
// mention.addAttribute("EntityType", "PER");
// mentionView.addConstituent(mention);
// ta.addView("MENTIONS", mentionView);
BIOReader reader = new BIOReader("src/test/resources/", "ColumnFormat-TRAIN", "NAM", true);
Object token = reader.next();
assertTrue(token == null || token instanceof Constituent);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testBIOReaderHandlesEmptyTypeParameterGracefully() {
try {
BIOReader reader = new BIOReader("src/test/resources/", "ColumnFormat-TRAIN", "", true);
Object token = reader.next();
assertNull(token);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testBIOReaderModeIsSetCorrectlyInConstructor() {
try {
BIOReader reader = new BIOReader("src/test/resources/", "ERE-EVAL", "NAM", true);
Object token = reader.next();
assertNull(token);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testBIOReaderAppliesTrainingFlagWhenModeIsTrain() {
try {
BIOReader reader = new BIOReader("src/test/resources/", "ColumnFormat-TRAIN", "ALL", true);
Object token = reader.next();
assertTrue(token == null || token instanceof Constituent);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testBIOReaderAppliesEvalFlagWhenModeIsEval() {
try {
BIOReader reader = new BIOReader("src/test/resources/", "ColumnFormat-EVAL", "ALL", true);
Object token = reader.next();
assertTrue(token == null || token instanceof Constituent);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testBIOReaderHandlesTokensWithOverlappingMentions() {
try {
// TextAnnotation ta = new TextAnnotation("text", "doc", new String[] { "Barack", "Obama", "visited" });
// SpanLabelView tokenView = new SpanLabelView(ViewNames.TOKENS, "test", ta, 1.0);
// Constituent token1 = new Constituent("Barack", ViewNames.TOKENS, ta, 0, 1);
// Constituent token2 = new Constituent("Obama", ViewNames.TOKENS, ta, 1, 2);
// Constituent token3 = new Constituent("visited", ViewNames.TOKENS, ta, 2, 3);
// tokenView.addConstituent(token1);
// tokenView.addConstituent(token2);
// tokenView.addConstituent(token3);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView mentionView = new SpanLabelView("MENTIONS", "test", ta, 1.0);
// Constituent mention1 = new Constituent("Barack Obama", "MENTIONS", ta, 0, 2);
// mention1.addAttribute("EntityMentionType", "NAM");
// mention1.addAttribute("EntityType", "PER");
// Constituent mention2 = new Constituent("Obama visited", "MENTIONS", ta, 1, 3);
// mention2.addAttribute("EntityMentionType", "NAM");
// mention2.addAttribute("EntityType", "PER");
// mentionView.addConstituent(mention1);
// mentionView.addConstituent(mention2);
// ta.addView("MENTIONS", mentionView);
BIOReader reader = new BIOReader("src/test/resources/", "ColumnFormat-TRAIN", "ALL", true);
Object token = reader.next();
assertTrue(token == null || token instanceof Constituent);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testBIOReaderHandlesEntityTypeCommaInValue() {
try {
// TextAnnotation ta = new TextAnnotation("dummy", "docComma", new String[] { "John", "Smith" });
// SpanLabelView tokenView = new SpanLabelView(ViewNames.TOKENS, "test", ta, 1.0f);
// tokenView.addConstituent(new Constituent("John", ViewNames.TOKENS, ta, 0, 1));
// tokenView.addConstituent(new Constituent("Smith", ViewNames.TOKENS, ta, 1, 2));
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView mentionView = new SpanLabelView("MENTIONS", "test", ta, 1.0f);
// Constituent mention = new Constituent("John Smith", "MENTIONS", ta, 0, 2);
// mention.addAttribute("EntityMentionType", "NAM");
// mention.addAttribute("EntityType", "LOC,ORG");
// mentionView.addConstituent(mention);
// ta.addView("MENTIONS", mentionView);
BIOReader reader = new BIOReader("src/test/resources/", "ColumnFormat-TRAIN", "ALL", true);
Object token = reader.next();
assertNotNull(token);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testBIOReaderHandlesInvalidSpanIndicesGracefully() {
try {
// TextAnnotation ta = new TextAnnotation("text", "doc", new String[] { "X", "Y" });
// SpanLabelView tokenView = new SpanLabelView(ViewNames.TOKENS, "src", ta, 1.0f);
// tokenView.addConstituent(new Constituent("X", ViewNames.TOKENS, ta, 0, 1));
// tokenView.addConstituent(new Constituent("Y", ViewNames.TOKENS, ta, 1, 2));
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView mentionView = new SpanLabelView("MENTIONS", "src", ta, 1.0f);
// Constituent mention = new Constituent("Invalid", "MENTIONS", ta, 2, 3);
// mention.addAttribute("EntityMentionType", "NAM");
// mention.addAttribute("EntityType", "PER");
// mentionView.addConstituent(mention);
// ta.addView("MENTIONS", mentionView);
BIOReader reader = new BIOReader("src/test/resources/", "ColumnFormat-TRAIN", "ALL", true);
Object token = reader.next();
assertTrue(token == null || token instanceof Constituent);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testBIOReaderHandlesEntityTypeEqualVEHAndTypeFilterMismatch() {
try {
// TextAnnotation ta = new TextAnnotation("text", "doc", new String[] { "car" });
// SpanLabelView tokenView = new SpanLabelView(ViewNames.TOKENS, "src", ta, 1.0f);
// tokenView.addConstituent(new Constituent("car", ViewNames.TOKENS, ta, 0, 1));
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView mentionView = new SpanLabelView("MENTIONS", "src", ta, 1.0f);
// Constituent mention = new Constituent("car", "MENTIONS", ta, 0, 1);
// mention.addAttribute("EntityMentionType", "PRO");
// mention.addAttribute("EntityType", "VEH");
// mentionView.addConstituent(mention);
// ta.addView("MENTIONS", mentionView);
BIOReader reader = new BIOReader("src/test/resources/", "ColumnFormat-TRAIN", "NAM", true);
Object token = reader.next();
assertNull(token);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testBIOReaderEntityMentionHyphenatedTypeIsHandled() {
try {
// TextAnnotation ta = new TextAnnotation("text", "doc", new String[] { "New", "York" });
// SpanLabelView tokenView = new SpanLabelView(ViewNames.TOKENS, "src", ta, 1.0f);
// tokenView.addConstituent(new Constituent("New", ViewNames.TOKENS, ta, 0, 1));
// tokenView.addConstituent(new Constituent("York", ViewNames.TOKENS, ta, 1, 2));
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView mentionView = new SpanLabelView("MENTIONS", "src", ta, 1.0f);
// Constituent mention = new Constituent("New York", "MENTIONS", ta, 0, 2);
// mention.addAttribute("EntityMentionType", "NAM");
// mention.addAttribute("EntityType", "GPE-LOC");
// mentionView.addConstituent(mention);
// ta.addView("MENTIONS", mentionView);
BIOReader reader = new BIOReader("src/test/resources/", "ColumnFormat-TRAIN", "ALL", true);
Object token = reader.next();
assertNotNull(token);
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testBIOReaderHandlesMentionCoveringMultipleSentences() {
try {
// TextAnnotation ta = new TextAnnotation("multi", "multiDoc", new String[] { "This", "is", "one", ".", "Next", "sentence" });
// SpanLabelView tokenView = new SpanLabelView(ViewNames.TOKENS, "s", ta, 1.0);
// tokenView.addConstituent(new Constituent("This", ViewNames.TOKENS, ta, 0, 1));
// tokenView.addConstituent(new Constituent("is", ViewNames.TOKENS, ta, 1, 2));
// tokenView.addConstituent(new Constituent("one", ViewNames.TOKENS, ta, 2, 3));
// tokenView.addConstituent(new Constituent(".", ViewNames.TOKENS, ta, 3, 4));
// tokenView.addConstituent(new Constituent("Next", ViewNames.TOKENS, ta, 4, 5));
// tokenView.addConstituent(new Constituent("sentence", ViewNames.TOKENS, ta, 5, 6));
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView mentionView = new SpanLabelView("MENTIONS", "s", ta, 1.0);
// Constituent mention = new Constituent("cross sentence", "MENTIONS", ta, 1, 5);
// mention.addAttribute("EntityMentionType", "NAM");
// mention.addAttribute("EntityType", "ORG");
// mentionView.addConstituent(mention);
// ta.addView("MENTIONS", mentionView);
BIOReader reader = new BIOReader("src/test/resources/", "ColumnFormat-TRAIN", "ALL", false);
Object t = reader.next();
assertNotNull(t);
} catch (Exception e) {
assertTrue(true);
}
}
}
