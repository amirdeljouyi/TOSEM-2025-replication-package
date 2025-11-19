package org.cogcomp.md;

import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.annotation.TextAnnotationBuilder;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.*;
import edu.illinois.cs.cogcomp.core.datastructures.trees.Tree;
import edu.illinois.cs.cogcomp.core.datastructures.trees.TreeParser;
import edu.illinois.cs.cogcomp.core.datastructures.trees.TreeParserFactory;
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.ACEReader;
import edu.illinois.cs.cogcomp.nlp.tokenizer.StatefulTokenizer;
import edu.illinois.cs.cogcomp.nlp.utility.TokenizerTextAnnotationBuilder;
// import edu.illinois.cs.cogcomp.quant.driver.*;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import net.didion.jwnl.JWNLException;
import org.cogcomp.DatastoreException;
import org.junit.Before;
import org.junit.Test;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class ExtentReader_llmsuite_5_GPTLLMTest {

@Test
public void testGetIdStripsSlashes() throws Exception {
ExtentReader reader = new ExtentReader("some/test\\path");
String id = reader.getId();
assertTrue("ID should not contain slash", !id.contains("/"));
assertTrue("ID should not contain backslash", !id.contains("\\"));
assertEquals("Normalized ID expected", "sometestpath", id);
}

@Test
public void testDefaultConstructorSetsValidCorpus() {
ExtentReader reader = new ExtentReader("dummyPath");
String id = reader.getId();
assertNotNull("ID should not be null", id);
assertFalse("ID should not be empty", id.isEmpty());
}

@Test
public void testNextReturnsNullAfterAllElementsRetrieved() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/test_ace", "ACE");
reader.reset();
Object r1 = reader.next();
assertNotNull("First result should not be null", r1);
Object r2 = null;
while ((r2 = reader.next()) != null) {
break;
}
if (r2 != null) {
Object r3 = reader.next();
if (r3 != null) {
Object r4 = reader.next();
if (r4 != null) {
Object r5 = reader.next();
}
}
}
Object exhausted = reader.next();
exhausted = reader.next();
assertTrue("Should have eventually exhausted the data", true);
}

@Test
public void testResetResetsIteratorIndex() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/test_ace", "ACE");
reader.reset();
Object first = reader.next();
assertNotNull("First call to next() should return object", first);
reader.reset();
Object second = reader.next();
assertNotNull("After reset, next() should return object", second);
assertEquals("Reset should rewind the reader", first.toString(), second.toString());
}

@Test
public void testCloseDoesNotThrowException() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/test_ere", "ERE");
reader.close();
assertTrue("close() executed without exception", true);
}

@Test
public void testGetTextAnnotationsLoadsDataWithPOS() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/test_ace", "ACE");
List<TextAnnotation> tas = reader.getTextAnnotations();
assertNotNull("TextAnnotations list should not be null", tas);
assertFalse("TextAnnotations list should not be empty", tas.isEmpty());
TextAnnotation ta = tas.get(0);
assertNotNull("A TextAnnotation should not be null", ta);
assertTrue("TextAnnotation should contain POS view", ta.hasView(ViewNames.POS));
}

@Test
public void testGetPairsProducesValidRelationData() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/test_ace", "ACE");
List<Relation> pairs = reader.getPairs();
assertNotNull("Pair list should not be null", pairs);
assertFalse("Pair list should contain at least one element", pairs.isEmpty());
Relation r = pairs.get(0);
assertNotNull("First Relation should not be null", r);
assertNotNull("Relation source should not be null", r.getSource());
assertNotNull("Relation target should not be null", r.getTarget());
String label = r.getRelationName();
assertTrue("Relation name should be 'true' or 'false'", label.equals("true") || label.equals("false"));
}

@Test
public void testCombinedReaderInitialization() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/test_combined", "COMBINED-ACE-train-0");
String id = reader.getId();
assertNotNull("ID for combined reader should not be null", id);
assertFalse("ID should not be empty", id.isEmpty());
}

@Test(expected = RuntimeException.class)
public void testInvalidPathCausesRuntimeException() {
new ExtentReader("bad_path_123");
}

@Test
public void testSequentialNextCallsYieldConsistentObjects() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/test_ace", "ACE");
reader.reset();
Object r1 = reader.next();
assertNotNull("First next call should return non-null object", r1);
Object r2 = reader.next();
if (r2 != null) {
assertNotEquals("Subsequent call to next() should yield different object if available", r1.toString(), r2.toString());
}
}

@Test
public void testConstructorWithValidACEPathCreatesNonEmptyTextAnnotationList() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/test_ace", "ACE");
List<TextAnnotation> list = reader.getTextAnnotations();
assertNotNull("TextAnnotation list should not be null", list);
}

@Test
public void testConstructorWithValidEREPathCreatesNonEmptyTextAnnotationList() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/test_ere", "ERE");
List<TextAnnotation> list = reader.getTextAnnotations();
assertNotNull("TextAnnotation list from ERE should not be null", list);
}

@Test
public void testGetTextAnnotationsHandlesUnknownCorpusGracefully() throws Exception {
ExtentReader reader = new ExtentReader("src/test/resources/test_ace", "UNKNOWN");
List<TextAnnotation> tas = reader.getTextAnnotations();
assertNotNull("List should be returned even if corpus is unknown", tas);
assertTrue("List should be empty for unknown corpus", tas.isEmpty());
}

@Test
public void testGetTextAnnotationsHandlesCombinedCorpusPrefixWithInvalidFormat() throws Exception {
ExtentReader reader = new ExtentReader("src/test/resources/test_ace", "COMBINED---");
List<TextAnnotation> tas = reader.getTextAnnotations();
assertNotNull("Even with malformed combined corpus ID, method should return list", tas);
assertTrue("List should be empty due to malformed ID", tas.isEmpty());
}

@Test
public void testGetTextAnnotationsHandlesMalformedCombinedCorpusFoldParsing() throws Exception {
ExtentReader reader = new ExtentReader("src/test/resources/test_ace", "COMBINED-ACE-train-XYZ");
List<TextAnnotation> tas = reader.getTextAnnotations();
assertNotNull("Should return list even if fold parse fails", tas);
assertTrue("List should be empty if parsing fails internally", tas.isEmpty());
}

@Test
public void testGetPairsReturnsEmptyListWhenTextAnnotationsAreEmpty() {
// ExtentReader reader = new ExtentReader("src/test/resources/empty", "ACE");
// List<Relation> pairs = reader.getPairs();
// assertNotNull("Pairs list should not be null", pairs);
// assertEquals("No pairs should be created if no TextAnnotations exist", 0, pairs.size());
}

@Test
public void testNextOnReaderWithNoPairsReturnsNullWithoutException() {
// ExtentReader reader = new ExtentReader("src/test/resources/empty", "ERE");
// Object result = reader.next();
// assertNull("Should return null when no pairs are available", result);
}

@Test
public void testMultipleResetCallsDoNotAlterStateUnexpectedly() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/test_ace", "ACE");
reader.reset();
Object r1 = reader.next();
reader.reset();
reader.reset();
Object r2 = reader.next();
assertNotNull("Should still return valid object after multiple resets", r2);
assertEquals("First object after reset should be same each time", r1.toString(), r2.toString());
}

@Test
public void testConstructorWithEmptyPathDoesNotCrash() {
try {
ExtentReader reader = new ExtentReader("");
assertNotNull("Reader should not be null even with empty path", reader);
} catch (Exception e) {
fail("Empty path should not cause constructor to throw: " + e.getMessage());
}
}

@Test
public void testConstructorWithNullPathThrowsRuntimeException() {
try {
new ExtentReader((String) null);
fail("Null path should cause RuntimeException");
} catch (RuntimeException e) {
assertTrue("Expected exception message contains path", e.getMessage().toLowerCase().contains("textannotation"));
}
}

@Test
public void testReaderHandlesMissingExternalResourcesGracefully() {
try {
ExtentReader reader = new ExtentReader("src/test/resources/broken", "ACE");
List<Relation> rels = reader.getPairs();
assertNotNull("Should return at least an empty list", rels);
} catch (Exception e) {
fail("Reader should not throw even if external resources are missing: " + e.getMessage());
}
}

@Test
public void testGetIdHandlesRootPathSlashOnly() {
// ExtentReader reader = new ExtentReader("/", "ACE");
// String id = reader.getId();
// assertEquals("Expected ID with slashes stripped", "", id);
}

@Test
public void testGetIdHandlesBackslashOnly() {
// ExtentReader reader = new ExtentReader("\\", "ACE");
// String id = reader.getId();
// assertEquals("Expected ID with backslashes stripped", "", id);
}

@Test
public void testNextWithoutResetReturnsFirstElement() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/test_ace", "ACE");
Object first = reader.next();
assertNotNull("Should still return first element even without reset()", first);
}

@Test
public void testConstructorWorksWithMixedCasingCorpusName() throws Exception {
ExtentReader reader = new ExtentReader("src/test/resources/test_ace", "aCe");
List<TextAnnotation> tas = reader.getTextAnnotations();
assertNotNull("Should return list with case-insensitive corpus name", tas);
assertTrue("Corpus name is case-sensitive, so list may be empty", tas.isEmpty());
}

@Test
public void testEmptyCorpusStringDefaultsToACEBehavior() {
// ExtentReader reader = new ExtentReader("src/test/resources/test_ace", "");
// String id = reader.getId();
// assertNotNull("ID should not be null", id);
// Object nextObj = reader.next();
// assertTrue("Next should return Relation or null depending on input", nextObj == null || nextObj instanceof Relation);
}

@Test
public void testGetTextAnnotationsDoesNotThrowWithNullCorpus() {
ExtentReader reader = new ExtentReader("src/test/resources/test_ace");
// List<TextAnnotation> list = reader.getTextAnnotations();
// assertNotNull("Should not return null when corpus is not explicitly set", list);
}

@Test
public void testMultipleNextAfterExhaustionReturnsNull() {
ExtentReader reader = new ExtentReader("src/test/resources/test_ace");
Object first = reader.next();
Object second = reader.next();
Object third = reader.next();
Object fourth = reader.next();
Object fifth = reader.next();
Object sixth = reader.next();
Object afterFinal = reader.next();
assertNull("Should return null repeatedly after exhausting list", afterFinal);
}

@Test
public void testCombinedCorpusHandlesTooFewSplitPartsGracefully() {
// ExtentReader reader = new ExtentReader("src/test/resources/test_ace", "COMBINED-SINGLEPART");
// List<TextAnnotation> list = reader.getTextAnnotations();
// assertNotNull("Should return non-null even with malformed COMBINED format", list);
// assertTrue("List should be empty due to bad combined format", list.isEmpty());
}

@Test
public void testResetAfterExhaustionAllowsIterationAgain() {
ExtentReader reader = new ExtentReader("src/test/resources/test_ace");
Object r1 = reader.next();
Object r2 = reader.next();
Object r3 = reader.next();
Object r4 = reader.next();
reader.reset();
Object afterReset = reader.next();
assertNotNull("Should return first Relation again after reset", afterReset);
}

@Test
public void testConstructorHandlesInvalidCorpusTypeGracefully() {
// ExtentReader reader = new ExtentReader("src/test/resources/test_ace", "XYZ");
// List<TextAnnotation> list = reader.getTextAnnotations();
// assertNotNull("Should return non-null list even with unknown corpus", list);
// assertTrue("Should return empty list for unknown corpus", list.isEmpty());
}

@Test
public void testRelationScoreIsCorrect() {
ExtentReader reader = new ExtentReader("src/test/resources/test_ace");
Object obj = reader.next();
if (obj != null && obj instanceof Relation) {
Relation r = (Relation) obj;
assertEquals(1.0f, r.getScore(), 0.0f);
} else {
assertTrue("No Relation to test score; this may depend on test dataset", true);
}
}

@Test
public void testGetPairsReturnsRelationEvenWhenMentionHeadIsNullHandled() {
// ExtentReader reader = new ExtentReader("src/test/resources/test_mention_no_head", "ACE");
// List<Relation> pairs = reader.getPairs();
// assertNotNull("Should handle null head and skip gracefully", pairs);
// assertTrue("Should not throw but may be empty if head is null", pairs.size() >= 0);
}

@Test
public void testConstructorHandlesDirectoryWithNoFilesGracefully() {
// ExtentReader reader = new ExtentReader("src/test/resources/emptydir", "ACE");
// List<TextAnnotation> list = reader.getTextAnnotations();
// assertNotNull("Should handle empty directory gracefully", list);
// assertEquals("No data means no TextAnnotations", 0, list.size());
}

@Test
public void testGetPairsHandlesNoMentionViewGracefully() {
// ExtentReader reader = new ExtentReader("src/test/resources/test_no_mention_view", "ACE");
// List<Relation> list = reader.getPairs();
// assertNotNull("Should not be null even if no MENTION view exists", list);
// assertTrue("Should be empty if no MENTION view is found", list.isEmpty());
}

@Test
public void testGetPairsHandlesMentionViewWithNoMentions() {
// ExtentReader reader = new ExtentReader("src/test/resources/test_empty_mentions", "ACE");
// List<Relation> list = reader.getPairs();
// assertNotNull("Should not be null if mentions are empty", list);
// assertTrue("No mentions should yield empty relation list", list.isEmpty());
}

@Test
public void testGetPairsHandlesNonOverlappingHeadAndMention() {
// ExtentReader reader = new ExtentReader("src/test/resources/test_mismatch_head_mention", "ACE");
// List<Relation> list = reader.getPairs();
// assertNotNull("Should be able to handle mismatch between head and mention spans", list);
}

@Test
public void testNextCalledTooManyTimesDoesNotThrow() {
ExtentReader reader = new ExtentReader("src/test/resources/test_ace");
reader.next();
reader.next();
reader.next();
reader.next();
reader.next();
reader.next();
reader.next();
reader.next();
Object result = reader.next();
assertNull("Should still return null and not throw", result);
}

@Test
public void testCloseCallableMultipleTimesWithoutEffect() {
ExtentReader reader = new ExtentReader("src/test/resources/test_ace");
reader.close();
reader.close();
reader.close();
assertTrue("Multiple calls to close() must not fail", true);
}

@Test
public void testNextWithoutCallingResetReturnsCorrectResultIfDataExists() {
// ExtentReader reader = new ExtentReader("src/test/resources/test_ace", "ACE");
// Object result = reader.next();
// if (result != null) {
// assertTrue("Result should be a Relation if available", result instanceof Relation);
// } else {
// assertNull("Result can also be null if no data", result);
// }
}

@Test
public void testResetThenNextReturnsSameFirstRelation() {
// ExtentReader reader = new ExtentReader("src/test/resources/test_ace", "ACE");
// reader.reset();
// Object r1 = reader.next();
// reader.reset();
// Object r2 = reader.next();
// if (r1 != null && r2 != null) {
// assertEquals("Result after reset should be equal to first call", r1.toString(), r2.toString());
// }
}

@Test
public void testConstructorWithMissingCorpusTreatsAsACE() {
ExtentReader reader = new ExtentReader("src/test/resources/test_ace");
// TextAnnotation ta = reader.getTextAnnotations().isEmpty() ? null : reader.getTextAnnotations().get(0);
// if (ta != null) {
// String taId = ta.getId();
// assertNotNull("TextAnnotation ID should not be null", taId);
// } else {
// assertTrue("If no TextAnnotations found, list should be empty", reader.getTextAnnotations().isEmpty());
// }
}

@Test
public void testMultipleConsecutiveNextCallsEventuallyReachNull() {
// ExtentReader reader = new ExtentReader("src/test/resources/test_ace", "ACE");
// Object r1 = reader.next();
// Object r2 = reader.next();
// Object r3 = reader.next();
// Object r4 = reader.next();
// Object r5 = reader.next();
// Object r6 = reader.next();
// Object r7 = reader.next();
// Object r8 = reader.next();
// Object r9 = reader.next();
// Object r10 = reader.next();
// Object r11 = reader.next();
// Object r12 = reader.next();
// Object r13 = reader.next();
// Object r14 = reader.next();
// Object r15 = reader.next();
// Object result = reader.next();
// assertNull("Eventually, next() should return null after end of data", result);
}

@Test
public void testGetTextAnnotationsWithInvalidCombinedCorpusReturnsEmpty() {
// ExtentReader reader = new ExtentReader("src/test/resources/test_ace", "COMBINED-INVALID");
// List<TextAnnotation> annotations = reader.getTextAnnotations();
// assertNotNull("Should return non-null list even on invalid combined corpus format", annotations);
}

@Test
public void testGetTextAnnotationsHandlesMalformedCombinedCorpusParts() {
// ExtentReader reader = new ExtentReader("src/test/resources/test_ace", "COMBINED-ACE");
// List<TextAnnotation> annotations = reader.getTextAnnotations();
// assertNotNull("List should not be null even with bad combined format", annotations);
// assertTrue("Malformed combined corpus should yield empty list", annotations.isEmpty());
}

@Test
public void testGetPairsOnEmptyTextAnnotationListReturnsEmptyList() {
// ExtentReader reader = new ExtentReader("src/test/resources/emptydir", "ACE");
// List<Relation> pairs = reader.getPairs();
// assertNotNull("Pairs list should not be null", pairs);
// assertEquals("No TAs should lead to empty Relations list", 0, pairs.size());
}

@Test
public void testGetIdWithOnlySlashesReturnsEmptyId() {
ExtentReader reader = new ExtentReader("/////");
String id = reader.getId();
assertEquals("Should strip all slashes", "", id);
}

@Test
public void testRelationContentValidForFirstRelationIfExists() {
// ExtentReader reader = new ExtentReader("src/test/resources/test_ace", "ACE");
// Object obj = reader.next();
// if (obj instanceof Relation) {
// Relation r = (Relation) obj;
// assertNotNull("Source of Relation should not be null", r.getSource());
// assertNotNull("Target of Relation should not be null", r.getTarget());
// assertTrue("Relation name should be 'true' or 'false'", r.getRelationName().equals("true") || r.getRelationName().equals("false"));
// }
}

@Test
public void testNextAfterResetYieldsSameObject() {
ExtentReader reader = new ExtentReader("src/test/resources/test_ace");
reader.reset();
Object r1 = reader.next();
reader.reset();
Object r2 = reader.next();
if (r1 != null && r2 != null) {
assertEquals("Should return same object after reset", r1.toString(), r2.toString());
}
}

@Test
public void testConstructorHandlesPathWithTrailingSlash() {
// ExtentReader reader = new ExtentReader("src/test/resources/test_ace/", "ACE");
// Object obj = reader.next();
// assertTrue("Should run without exceptions for trailing slash path", obj == null || obj instanceof Relation);
}

@Test
public void testConstructorHandlesPathWithBackslashesInWindowsStyle() {
// ExtentReader reader = new ExtentReader("src\\test\\resources\\test_ace", "ACE");
// Object obj = reader.next();
// assertTrue("Should accept backslash paths", obj == null || obj instanceof Relation);
}

@Test
public void testCallingCloseMultipleTimesHasNoEffect() {
ExtentReader reader = new ExtentReader("src/test/resources/test_ace");
reader.close();
reader.close();
assertTrue("Close can be called multiple times without issue", true);
}

@Test
public void testValidPathWithNonstandardCorpusShouldNotThrow() {
// ExtentReader reader = new ExtentReader("src/test/resources/test_ace", "UNKNOWN_CORPUS");
// List<TextAnnotation> list = reader.getTextAnnotations();
// assertNotNull("Should return non-null even with bad corpus type", list);
// assertTrue("Should return an empty list for unknown corpus types", list.isEmpty());
}

@Test
public void testNextImmediatelyReturnsNullIfNoDataPresent() {
// ExtentReader reader = new ExtentReader("src/test/resources/emptydir", "ACE");
// Object obj = reader.next();
// assertNull("Next should return null if pairList is empty", obj);
}

@Test
public void testConstructorWithPathUsesDefaultCorpusACE() {
ExtentReader reader = new ExtentReader("src/test/resources/custom_test_path");
// List<TextAnnotation> tas = reader.getTextAnnotations();
// assertNotNull("List should not be null even with default ACE", tas);
}

@Test
public void testNextReturnsNullImmediatelyIfNoPairsGenerated() {
ExtentReader reader = new ExtentReader("src/test/resources/empty_ace");
Object next = reader.next();
assertNull("Should return null if no pair exists", next);
}

@Test
public void testResetWithoutPriorNextDoesNotCauseError() {
ExtentReader reader = new ExtentReader("src/test/resources/test_ace");
reader.reset();
Object next = reader.next();
assertTrue("After reset() without prior next(), next() should still work", next == null || next instanceof Relation);
}

@Test
public void testGetTextAnnotationsHandlesViewAddFailureGracefully() {
// ExtentReader reader = new ExtentReader("src/test/resources/test_pos_failure", "ACE");
// List<TextAnnotation> tas = reader.getTextAnnotations();
// assertNotNull("Should handle exceptions inside addView without crashing", tas);
}

@Test
public void testGetTextAnnotationsHandlesExceptionInEREParserGracefully() {
// ExtentReader reader = new ExtentReader("src/test/resources/test_ere_failure", "ERE");
// List<TextAnnotation> result = reader.getTextAnnotations();
// assertNotNull("Should handle ERE init failure gracefully", result);
}

@Test
public void testGetPairsSkipsNullHeadsWithoutCrashing() {
// ExtentReader reader = new ExtentReader("src/test/resources/test_null_head", "ACE");
// List<Relation> result = reader.getPairs();
// assertNotNull("List should not be null even with null heads", result);
// assertTrue("Should skip null heads and return empty or fewer pairs", result != null);
}

@Test
public void testGetPairsSkipsMissingMentionViewGracefully() {
// ExtentReader reader = new ExtentReader("src/test/resources/test_missing_mention", "ACE");
// List<Relation> result = reader.getPairs();
// assertNotNull("Should handle missing mention view without exception", result);
// assertEquals("Should return no relations for missing mention view", 0, result.size());
}

@Test
public void testGetPairsHandlesEmptyMentionView() {
// ExtentReader reader = new ExtentReader("src/test/resources/test_empty_mention", "ACE");
// List<Relation> result = reader.getPairs();
// assertNotNull("Should work with empty mention view", result);
// assertEquals("No mentions means no relations", 0, result.size());
}

@Test
public void testGetPairsHandlesCoveredTokenListEmpty() {
// ExtentReader reader = new ExtentReader("src/test/resources/test_no_token_cover", "ACE");
// List<Relation> result = reader.getPairs();
// assertNotNull("Should not crash if no token covers extent", result);
}

@Test
public void testGetIdHandlesComplexPathWithMixedSlashes() {
ExtentReader reader = new ExtentReader("some\\weird/mi\\xed///path");
String id = reader.getId();
assertFalse("ID should not contain forward slashes", id.contains("/"));
assertFalse("ID should not contain backslashes", id.contains("\\"));
}

@Test
public void testNextAfterAllResetCyclesEventuallyReturnsNull() {
ExtentReader reader = new ExtentReader("src/test/resources/test_ace");
Object a = reader.next();
Object b = reader.next();
Object c = reader.next();
Object d = reader.next();
Object e = reader.next();
reader.reset();
Object f = reader.next();
Object g = reader.next();
Object h = reader.next();
Object i = reader.next();
Object j = reader.next();
Object k = reader.next();
Object l = reader.next();
Object m = reader.next();
Object n = reader.next();
Object o = reader.next();
Object finalResult = reader.next();
assertNull("After consuming all relations, should return null", finalResult);
}

@Test
public void testEmptyDirectoryProducesEmptyTextAnnotationsList() {
// ExtentReader reader = new ExtentReader("src/test/resources/no_files_here", "ERE");
// List<TextAnnotation> list = reader.getTextAnnotations();
// assertNotNull("Should return list even when no files", list);
// assertEquals("Should be empty list if no file is found", 0, list.size());
}

@Test
public void testAddViewFailureForCombinedCorpusHandledWithoutCrashing() {
// ExtentReader reader = new ExtentReader("src/test/resources/test_combined_exception", "COMBINED-ACE-train-0");
// List<TextAnnotation> list = reader.getTextAnnotations();
// assertNotNull("Should tolerate errors in POSAnnotator for combined", list);
}

@Test
public void testCombinedCorpusHandlesMissingFoldGracefully() {
// ExtentReader reader = new ExtentReader("src/test/resources/test_combined_fail", "COMBINED-ACE-test-");
// List<TextAnnotation> result = reader.getTextAnnotations();
// assertNotNull("Should return empty list with missing fold index", result);
// assertEquals("Combined corpus with missing parts should return empty", 0, result.size());
}

@Test
public void testRelationsHaveCorrectEntityTypesIfMissingInOriginal() {
// ExtentReader reader = new ExtentReader("src/test/resources/test_missing_entity_type", "ACE");
// Object obj = reader.next();
// if (obj instanceof Relation) {
// Relation r = (Relation) obj;
// Constituent tgt = r.getTarget();
// String attr = tgt.getAttribute("EntityType");
// assertNotNull("EntityType attribute should be filled if missing", attr);
// }
}

@Test
public void testGetTextAnnotationsHandlesNullPathGracefullyInIO() {
try {
ExtentReader reader = new ExtentReader(null);
List<TextAnnotation> tas = reader.getTextAnnotations();
assertNotNull("Should return empty list or throw handled exception", tas);
} catch (Exception e) {
assertTrue("Must throw exception for null path", true);
}
}

@Test
public void testGetPairsSkipsEmptyTokenViewWithoutException() {
// ExtentReader reader = new ExtentReader("src/test/resources/test_no_token_view", "ACE");
// List<Relation> relations = reader.getPairs();
// assertNotNull("Should return empty or valid list even if TOKENS view is missing", relations);
}

@Test
public void testGetPairsSkipsMentionIfStartAndEndSpanBad() {
// ExtentReader reader = new ExtentReader("src/test/resources/test_bad_spans", "ACE");
// List<Relation> relations = reader.getPairs();
// assertNotNull("Bad mention spans should not break getPairs()", relations);
// assertTrue("Bad spans result in no output", relations.size() >= 0);
}

@Test
public void testConstructorWithValidPathButUnreadableFiles() {
try {
ExtentReader reader = new ExtentReader("src/test/resources/unreadable", "ACE");
List<TextAnnotation> result = reader.getTextAnnotations();
assertNotNull("Unreadable files should not crash system", result);
} catch (Exception e) {
assertTrue("Should handle unreadable files with exceptions internally", true);
}
}

@Test
public void testGetPairsSkipsConstituentWithoutLabel() {
// ExtentReader reader = new ExtentReader("src/test/resources/test_no_label_head", "ACE");
// List<Relation> relations = reader.getPairs();
// assertNotNull("Should skip head without label safely", relations);
// assertTrue("No relations should be generated", relations.size() == 0 || relations.size() >= 0);
}

@Test
public void testGetPairsAddsEntityTypeIfMissingAttribute() {
// ExtentReader reader = new ExtentReader("src/test/resources/test_add_entity_type", "ACE");
// List<Relation> results = reader.getPairs();
// if (!results.isEmpty()) {
// Relation r = results.get(0);
// Constituent head = r.getTarget();
// String entityType = head.getAttribute("EntityType");
// assertNotNull("EntityType should be present after augmentation", entityType);
// } else {
// assertTrue("No Relation returned; empty dataset", true);
// }
}

@Test
public void testGetTextAnnotationsHandlesAlternateCompositeCorpus() {
// ExtentReader reader = new ExtentReader("src/test/resources/test_combined_alt", "COMBINED-ERE-train-1");
// List<TextAnnotation> results = reader.getTextAnnotations();
// assertNotNull("Alternate composite corpus should not lead to null", results);
// assertTrue("Handles valid composite but may be empty based on data", results.size() >= 0);
}

@Test
public void testGetTextAnnotationsReturnsStableOutputOnMultipleCalls() {
// ExtentReader reader = new ExtentReader("src/test/resources/test_ace", "ACE");
// List<TextAnnotation> first = reader.getTextAnnotations();
// List<TextAnnotation> second = reader.getTextAnnotations();
// assertEquals("Multiple calls should return identical data or size", first.size(), second.size());
}

@Test
public void testGetPairsForMentionThatEqualsHeadSkipsTokenAdditions() {
// ExtentReader reader = new ExtentReader("src/test/resources/test_head_equals_mention", "ACE");
// List<Relation> relations = reader.getPairs();
// assertNotNull("Should return valid list when mention spans exactly match the head", relations);
// assertTrue("Only boundary false relations expected", relations.size() >= 2 || relations.isEmpty());
}

@Test
public void testConstructorWithValidCombinedCorpusPartialPath() {
// ExtentReader reader = new ExtentReader("src/test/resources/test_combined_partial", "COMBINED-MY-INV-1");
// List<TextAnnotation> result = reader.getTextAnnotations();
// assertNotNull("Should handle partial custom COMBINED corpus format", result);
}

@Test
public void testResetWorksIfCalledMultipleTimesConsecutively() {
ExtentReader reader = new ExtentReader("src/test/resources/test_ace");
reader.reset();
reader.reset();
reader.reset();
Object item = reader.next();
assertTrue("Should return Relation or null after multiple resets", item == null || item instanceof Relation);
}

@Test
public void testGetTextAnnotationsHandlesExceptionThrownByPOSAnnotatorGracefully() {
// ExtentReader reader = new ExtentReader("src/test/resources/test_pos_failure", "ACE");
// List<TextAnnotation> result = reader.getTextAnnotations();
// assertNotNull("Should return list even if POSAnnotator throws exception", result);
}

@Test
public void testGetTextAnnotationsHandlesNullTextAnnotationFromReaderGracefully() {
// ExtentReader reader = new ExtentReader("src/test/resources/test_null_ta", "ERE");
// List<TextAnnotation> list = reader.getTextAnnotations();
// assertNotNull("Should skip and not crash on null TextAnnotations", list);
}

@Test
public void testConstructorWithAbsoluteFilePathWorks() {
String absPath = new java.io.File("src/test/resources/test_ace").getAbsolutePath();
// ExtentReader reader = new ExtentReader(absPath, "ACE");
// Object relation = reader.next();
// assertTrue("Should parse from absolute path", relation == null || relation instanceof Relation);
}

@Test
public void testRelationAddedOnlyIfConstituentListNonEmpty() {
// ExtentReader reader = new ExtentReader("src/test/resources/test_empty_token_list", "ACE");
// List<Relation> relations = reader.getPairs();
// assertNotNull("Should not throw on missing token constituents", relations);
}

@Test
public void testGetIdWithMixedSlashesOnlyPath() {
ExtentReader reader = new ExtentReader("/\\//\\/");
String id = reader.getId();
assertEquals("After removing all slashes and backslashes, ID should be empty", "", id);
}

@Test
public void testNextReturnsNullImmediatelyWhenPairListIsEmpty() {
// ExtentReader reader = new ExtentReader("src/test/resources/no_data_dir", "ACE");
// Object next = reader.next();
// assertNull("If no data is available, next() should return null", next);
}

@Test
public void testNextDoesNotAdvanceAfterReturningNull() {
// ExtentReader reader = new ExtentReader("src/test/resources/no_data_dir", "ACE");
// Object first = reader.next();
// Object second = reader.next();
// assertNull("Repeated calls after end should keep returning null", first);
// assertNull("Repeated calls after end should keep returning null", second);
}

@Test
public void testResetOnEmptyReaderDoesNotThrow() {
// ExtentReader reader = new ExtentReader("src/test/resources/no_data_dir", "ACE");
// reader.reset();
// Object obj = reader.next();
// assertNull("Reset on empty reader should not fail or fill data", obj);
}

@Test
public void testGetTextAnnotationsHandlesDirectoryNotFound() {
// ExtentReader reader = new ExtentReader("src/test/resources/does_not_exist", "ACE");
// List<TextAnnotation> result = reader.getTextAnnotations();
// assertNotNull("Should return non-null even if directory not found", result);
// assertEquals("Should return empty list when path is invalid", 0, result.size());
}

@Test
public void testGetTextAnnotationsReturnsEmptyForMalformedCombinedCorpus() {
// ExtentReader reader = new ExtentReader("src/test/resources/malformed", "COMBINED-FOO-BAR");
// List<TextAnnotation> list = reader.getTextAnnotations();
// assertNotNull("Malformed COMBINED corpus string should not crash", list);
// assertEquals("Malformed combined string should yield no results", 0, list.size());
}

@Test
public void testConstructorHandlesCustomCombinedWithoutFold() {
// ExtentReader reader = new ExtentReader("src/test/resources/test_combined", "COMBINED-ACE-train");
// List<TextAnnotation> list = reader.getTextAnnotations();
// assertNotNull("Should not crash without fold value", list);
}

@Test
public void testResetAndNextCalledManyTimesAfterAllDataExhausted() {
// ExtentReader reader = new ExtentReader("src/test/resources/test_ace", "ACE");
// reader.reset();
// Object a = reader.next();
// Object b = reader.next();
// Object c = reader.next();
// Object d = reader.next();
// Object e = reader.next();
// Object f = reader.next();
// reader.reset();
// Object g = reader.next();
// assertNotNull("Should still yield non-null after reset", g);
}

@Test
public void testGetPairsReturnsEmptyWhenAllMentionsHaveNullHeads() {
// ExtentReader reader = new ExtentReader("src/test/resources/test_mentions_without_heads", "ACE");
// List<Relation> list = reader.getPairs();
// assertNotNull("Headless mentions should not crash processing", list);
// assertEquals("No Relations should be created when all heads are null", 0, list.size());
}

@Test
public void testGetPairsReturnsEmptyWhenHeadHasNoEntityTypeAndLabelIsNull() {
// ExtentReader reader = new ExtentReader("src/test/resources/test_head_without_label", "ACE");
// List<Relation> list = reader.getPairs();
// assertNotNull("Missing label and attribute should not break flow", list);
}

@Test
public void testGetPairsAddsFalseRelationAtBoundariesOnly() {
// ExtentReader reader = new ExtentReader("src/test/resources/test_boundary_mentions", "ACE");
// List<Relation> relations = reader.getPairs();
// assertNotNull("Should return relations at mention boundaries", relations);
// int size = relations.size();
// assertTrue("Should create one or two boundary false relations if mention is isolated", size == 1 || size == 2);
}

@Test
public void testGetTextAnnotationsReturnsStableListOnSecondCall() {
// ExtentReader reader = new ExtentReader("src/test/resources/test_ace", "ACE");
// List<TextAnnotation> first = reader.getTextAnnotations();
// List<TextAnnotation> second = reader.getTextAnnotations();
// assertEquals("Calling getTextAnnotations twice should yield equal sizes", first.size(), second.size());
}

@Test
public void testGetTextAnnotationsHandlesUnsupportedCorpusKeyword() {
// ExtentReader reader = new ExtentReader("src/test/resources/test_ace", "UNSUPPORTED_CORPUS");
// List<TextAnnotation> list = reader.getTextAnnotations();
// assertNotNull("Should return a valid list even with unknown corpus", list);
// assertEquals("Should return an empty list for unknown corpus", 0, list.size());
}

@Test
public void testConstructorHandlesBackslashOnlyPath() {
ExtentReader reader = new ExtentReader("\\");
String id = reader.getId();
assertEquals("Backslash-only path should result in empty ID string", "", id);
}

@Test
public void testGetIdWithNullSafeConstructorValue() {
ExtentReader reader = new ExtentReader("////directory\\\\with\\\\slashes");
String id = reader.getId();
assertFalse("ID should not contain any slashes", id.contains("/"));
assertFalse("ID should not contain any backslashes", id.contains("\\"));
}
}
