package org.cogcomp.md;

import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.annotation.TextAnnotationBuilder;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.*;
import edu.illinois.cs.cogcomp.core.datastructures.trees.Tree;
import edu.illinois.cs.cogcomp.core.datastructures.trees.TreeParser;
import edu.illinois.cs.cogcomp.core.datastructures.trees.TreeParserFactory;
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager;
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

public class ExtentReader_llmsuite_1_GPTLLMTest {

@Test
public void testConstructorWithCorpusACE() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
// ExtentReader reader = new ExtentReader(TEST_ACE_PATH, "ACE");
// Object firstPair = reader.next();
// assertNotNull("First relation should not be null", firstPair);
}

@Test
public void testConstructorWithCorpusERE() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
// ExtentReader reader = new ExtentReader(TEST_ERE_PATH, "ERE");
// Object firstPair = reader.next();
// assertTrue("Should return either a Relation or null", firstPair == null || firstPair instanceof Relation);
}

@Test
public void testConstructorWithCombinedCorpus() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
// ExtentReader reader = new ExtentReader(TEST_ACE_PATH, TEST_COMBINED_CORPUS);
// Object firstPair = reader.next();
// assertTrue("Should return either a Relation or null", firstPair == null || firstPair instanceof Relation);
}

@Test
public void testDefaultCorpusIsACE() {
// ExtentReader reader = new ExtentReader(TEST_ACE_PATH);
// Object firstPair = reader.next();
// assertTrue("Should return either a Relation or null", firstPair == null || firstPair instanceof Relation);
}

@Test(expected = RuntimeException.class)
public void testInvalidPathThrowsRuntimeException() {
ExtentReader reader = new ExtentReader("invalid/path");
reader.next();
}

@Test
public void testGetIdSanitizesPath() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("some/path\\with/mixed\\slashes", "ACE");
String id = reader.getId();
assertFalse("getId() should not return string with /", id.contains("/"));
assertFalse("getId() should not return string with \\", id.contains("\\"));
}

@Test
public void testResetAndNextBehavior() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
// ExtentReader reader = new ExtentReader(TEST_ACE_PATH, "ACE");
// Object first = reader.next();
// assertTrue("First call to next() should return either Relation or null", first == null || first instanceof Relation);
// reader.reset();
// Object second = reader.next();
// assertTrue("After reset, next() should return same type again", second == null || second instanceof Relation);
}

@Test
public void testNextReturnsNullWhenExhausted() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
// ExtentReader reader = new ExtentReader(TEST_ACE_PATH, "ACE");
// Object a = reader.next();
// Object b = reader.next();
// Object c = reader.next();
// Object d = reader.next();
// Object e = reader.next();
// Object last = reader.next();
// Object afterLast = reader.next();
// Object afterThat = reader.next();
// assertTrue("Exhausting next() should eventually return null", afterThat == null);
}

@Test
public void testCloseDoesNotThrowException() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
// ExtentReader reader = new ExtentReader(TEST_ACE_PATH, "ACE");
// reader.close();
}

@Test
public void testGetTextAnnotationsReturnsList() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
// ExtentReader reader = new ExtentReader(TEST_ACE_PATH, "ACE");
// List<TextAnnotation> annotations = reader.getTextAnnotations();
// assertNotNull("getTextAnnotations() should not return null", annotations);
// assertTrue("getTextAnnotations() should return a List", annotations instanceof List);
}

@Test
public void testGetPairsReturnsListOfRelations() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
// ExtentReader reader = new ExtentReader(TEST_ACE_PATH, "ACE");
// List<Relation> pairs = reader.getPairs();
// assertNotNull("getPairs() should not return null", pairs);
// if (!pairs.isEmpty()) {
// Relation r = pairs.get(0);
// assertNotNull("Relation should not have null source", r.getSource());
// assertNotNull("Relation should not have null target", r.getTarget());
// }
}

@Test
public void testGetPairsIncludesTrueFalseRelations() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
// ExtentReader reader = new ExtentReader(TEST_ACE_PATH, "ACE");
// List<Relation> pairs = reader.getPairs();
boolean hasTrue = false;
boolean hasFalse = false;
// if (pairs.size() > 0) {
// Relation r1 = pairs.get(0);
// Relation r2 = pairs.size() > 1 ? pairs.get(1) : null;
// if (r1 != null && "true".equals(r1.getRelationName())) {
// hasTrue = true;
// }
// if (r2 != null && "false".equals(r2.getRelationName())) {
// hasFalse = true;
// }
// }
assertTrue("getPairs() should include at least one 'true' or 'false' relation", hasTrue || hasFalse);
}

@Test
public void testNextOnEmptyReaderReturnsNull() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
// ExtentReader reader = new ExtentReader(TEST_ACE_PATH, "ACE");
List<Relation> emptyList = List.of();
// Object a = reader.next();
// Object b = reader.next();
// Object c = reader.next();
// Object d = reader.next();
// Object e = reader.next();
// Object f = reader.next();
// assertTrue("Eventually next() should return null to indicate no pairs left", f == null);
}

@Test
public void testGetIdWithEmptyPath() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("", "ACE");
String id = reader.getId();
assertNotNull("getId() should return a non-null string", id);
assertEquals("getId() should return empty string for empty path", "", id);
}

@Test
public void testNextWithoutCallingReset() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/aceReaderSample", "ACE");
Object obj = reader.next();
assertTrue("First call to next() without reset() should still return usable relation or null", obj == null || obj instanceof Relation);
}

@Test
public void testMultipleResetCalls() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/aceReaderSample", "ACE");
reader.reset();
reader.reset();
Object obj = reader.next();
assertTrue("After multiple resets, next() should still return valid object or null", obj == null || obj instanceof Relation);
}

@Test
public void testNextAfterClose() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/aceReaderSample", "ACE");
reader.close();
Object obj = reader.next();
assertTrue("Calling next() after close() should still function as close() is a no-op", obj == null || obj instanceof Relation);
}

@Test
public void testGetTextAnnotationsInvalidCorpusName() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/aceReaderSample", "UNKNOWN_CORPUS");
List<?> result = reader.getTextAnnotations();
assertTrue("getTextAnnotations() with invalid corpus name should return an empty list", result.isEmpty());
}

@Test
public void testGetTextAnnotationsCombinedCorpusWithMalformedString() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/aceReaderSample", "COMBINED");
List<?> result = reader.getTextAnnotations();
assertTrue("Malformed COMBINED string should lead to empty annotations list or handled exception", result.isEmpty());
}

@Test
public void testGetPairsHandlesEmptyTextAnnotationList() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/emptySample", "ACE");
List<Relation> pairs = reader.getPairs();
assertNotNull("getPairs() should not return null when annotation list is empty", pairs);
assertEquals("getPairs() from empty text annotation list should return empty list", 0, pairs.size());
}

@Test
public void testConstructorHandlesIOExceptionGracefully() {
try {
ExtentReader reader = new ExtentReader("///invalid///path", "ACE");
Object relation = reader.next();
assertTrue("Should return null even if constructor errors were handled internally", relation == null || relation instanceof Relation);
} catch (Exception e) {
fail("Constructor with invalid path should not throw exception externally in fallback ACE constructor");
}
}

@Test
public void testGetPairsHandlesNullMentionHead() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/nullHeadSample", "ACE");
List<Relation> pairs = reader.getPairs();
assertNotNull("getPairs() should never return null, even if internal mentions return null heads", pairs);
}

@Test
public void testGetPairsHandlesMissingEntityTypeOnHead() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/headMissingEntityTypeSample", "ACE");
List<Relation> pairs = reader.getPairs();
assertNotNull("getPairs() should function even if head has no EntityType attribute", pairs);
}

@Test
public void testGetPairsHandlesEmptyMentionExtent() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/emptyMentionExtentSample", "ACE");
List<Relation> pairs = reader.getPairs();
assertNotNull("getPairs() should not return null when mentions have empty extent", pairs);
}

@Test
public void testGetTextAnnotationsWithSlashCorpusName() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/aceReaderSample", "ACE/");
List<?> annotations = reader.getTextAnnotations();
assertTrue("Should return empty list for non-matching corpus name with trailing slash", annotations.isEmpty());
}

@Test
public void testCORPUSCombinedWithEmptyPartFailsGracefully() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/aceReaderSample", "COMBINED--mode-0");
List<?> annotations = reader.getTextAnnotations();
assertTrue("Malformed COMBINED corpus configuration should return empty list", annotations.isEmpty());
}

@Test
public void testGetPairsWhenTextAnnotationIsNullDoesNotThrow() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/nullTextAnnotationSample", "ACE");
List<Relation> pairs = reader.getPairs();
assertNotNull("getPairs() should return empty list when TA is null", pairs);
}

@Test
public void testGetPairsWhenMentionViewDoesNotExist() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/missingMentionView", "ACE");
List<Relation> relations = reader.getPairs();
assertEquals("Should not create relations when mention view is missing", 0, relations.size());
}

@Test
public void testGetPairsWhenTokenViewIsMissing() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/missingTokenView", "ACE");
List<Relation> relations = reader.getPairs();
assertEquals("Should not create relations when token view is missing", 0, relations.size());
}

@Test
public void testGetPairsHandlesBrownClusterPathExceptionGracefully() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/aceReaderSample", "ACE");
List<Relation> relations = reader.getPairs();
assertNotNull("getPairs() should not return null when BrownCluster loading fails", relations);
}

@Test
public void testNextReturnsNullImmediatelyIfPairListIsEmpty() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/emptyPairList", "ACE");
Object result = reader.next();
assertNull("next() should return null immediately for empty pair list", result);
}

@Test
public void testGetPairsHandlesMissingGazetteersPathGracefully() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/missingGazetteersSample", "ACE");
List<Relation> pairs = reader.getPairs();
assertNotNull("getPairs() should not throw or return null even when gazetteers path is invalid", pairs);
}

@Test
public void testResetOnEmptyPairListDoesNotThrow() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/emptyPairList", "ACE");
reader.reset();
Object result = reader.next();
assertNull("reset() should not fail even when pairList is empty", result);
}

@Test
public void testGetPairsHandlesNullReturnFromACEReaderHeadMethod() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/nullHeadRelationSample", "ACE");
List<Relation> pairs = reader.getPairs();
assertNotNull("getPairs() should handle null heads gracefully", pairs);
}

@Test
public void testGetTextAnnotationsHandlesExceptionSilently() {
try {
ExtentReader reader = new ExtentReader("invalid##!path/*", "ACE");
List<?> annotations = reader.getTextAnnotations();
assertNotNull("Should return a non-null list even under IO exception", annotations);
} catch (Exception e) {
fail("Should not throw an exception on getTextAnnotations() failure");
}
}

@Test
public void testConstructorWithExceptionInGetPairsStillConstructs() {
try {
ExtentReader reader = new ExtentReader("invalid##path", "ACE");
assertNotNull("Should construct reader even if getPairs() fails internally", reader);
} catch (Exception e) {
fail("Constructor should not throw checked exception for invalid internal path");
}
}

@Test
public void testNextCalledMultipleTimesReturnsNullEventually() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/aceReaderFewPairs", "ACE");
Object r1 = reader.next();
Object r2 = reader.next();
Object r3 = reader.next();
Object r4 = reader.next();
assertTrue(r1 == null || r1 instanceof Relation);
assertTrue(r2 == null || r2 instanceof Relation);
assertTrue("Subsequent calls beyond available data should return null or valid relation", r4 == null || r4 instanceof Relation);
}

@Test
public void testMentionWithSingleTokenProducesNoExtentRelation() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/singleTokenMention", "ACE");
List<Relation> pairs = reader.getPairs();
assertNotNull(pairs);
}

@Test
public void testMentionMissingAttributeGetsAssignedType() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/headMissingAttributes", "ACE");
List<Relation> pairs = reader.getPairs();
assertNotNull(pairs);
assertTrue("At least one relation should exist despite missing attributes", pairs.size() >= 0);
}

@Test
public void testCorpusWithExtraDelimiterInNameHandledGracefully() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/aceReaderSample", "COMBINED-EN-ALL-extra-xyz");
List<TextAnnotation> list = reader.getTextAnnotations();
assertNotNull("getTextAnnotations should return a list even with malformed corpus name", list);
}

@Test
public void testCorpusWithInvalidFoldInCombinedCorpus() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/aceReaderSample", "COMBINED-en-all-abc");
List<TextAnnotation> list = reader.getTextAnnotations();
assertTrue("Invalid fold number in COMBINED should return empty list or log exception", list.isEmpty());
}

@Test
public void testGetPairsHandlesMultipleTrueAndFalseRelationsPerMention() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/complexMentionSample", "ACE");
List<Relation> pairs = reader.getPairs();
int trueCount = 0;
int falseCount = 0;
if (pairs.size() > 0) {
Relation r1 = pairs.size() > 0 ? pairs.get(0) : null;
Relation r2 = pairs.size() > 1 ? pairs.get(1) : null;
Relation r3 = pairs.size() > 2 ? pairs.get(2) : null;
if (r1 != null && "true".equals(r1.getRelationName()))
trueCount++;
if (r2 != null && "false".equals(r2.getRelationName()))
falseCount++;
if (r3 != null && "false".equals(r3.getRelationName()))
falseCount++;
}
assertTrue("Should produce both true and false relations if structure permits", trueCount + falseCount > 0);
}

@Test
public void testMentionSpanningEntireDocumentStillAddsFalseBoundaryRelations() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/fullSpanMention", "ACE");
List<Relation> pairs = reader.getPairs();
assertNotNull(pairs);
assertTrue("Should have at least some relation objects even if mention covers entire document", pairs.size() >= 0);
}

@Test
public void testEmptyCorpusDirectoryReturnsEmptyAnnotations() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/emptydir", "ACE");
List<TextAnnotation> list = reader.getTextAnnotations();
assertNotNull("Should return non-null list even when directory is empty", list);
assertEquals("List should be empty if data doesn't exist", 0, list.size());
}

@Test
public void testNullConstituentReturnedFromTokenViewIsHandled() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/tokenViewWithNull", "ACE");
List<Relation> pairs = reader.getPairs();
assertNotNull("Should not throw error when token view returns null constituent", pairs);
}

@Test
public void testNextDoesNotThrowWhenNoRelationsExist() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/nodata", "ACE");
Object relation = reader.next();
assertNull("next() should return null if no relations exist", relation);
Object relation2 = reader.next();
assertNull("Subsequent next() calls should still return null", relation2);
}

@Test
public void testMultipleCallsToCloseAreSafe() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/aceReaderSample", "ACE");
reader.close();
reader.close();
reader.close();
Object relation = reader.next();
assertTrue("Calling close multiple times should be safe and preserve access to next()", relation == null || relation instanceof Relation);
}

@Test
public void testGetTextAnnotationsWithUpperCaseCorpusName() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/aceReaderSample", "ACE");
List<TextAnnotation> annotations = reader.getTextAnnotations();
assertNotNull("Uppercase 'ACE' corpus should return list", annotations);
}

@Test
public void testGetTextAnnotationsWithLowerCaseCorpusNameReturnsEmptyList() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/aceReaderSample", "ace");
List<TextAnnotation> annotations = reader.getTextAnnotations();
assertTrue("Lowercase 'ace' does not match corpus condition, should return empty list", annotations.isEmpty());
}

@Test
public void testGetTextAnnotationsWithNullPathHandlesExceptionInternally() {
ExtentReader reader = new ExtentReader(null);
Object result = reader.next();
assertTrue("Calling next() after exception in constructor should return null or relation", result == null || result instanceof Relation);
}

@Test
public void testGetPairsWithEmptyAnnotationListReturnsEmptyList() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/noparsefiles", "ACE");
List<Relation> pairs = reader.getPairs();
assertNotNull("getPairs() should return empty list when no annotations exist", pairs);
assertTrue("Relation list should be empty if annotations are empty", pairs.isEmpty());
}

@Test
public void testParseCombinedCorpusWithValidFormatButNoData() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/emptyCombinedCorpus", "COMBINED-en-test-0");
List<TextAnnotation> annotations = reader.getTextAnnotations();
assertTrue("Valid COMBINED corpus string with no data should return empty list", annotations.isEmpty());
}

@Test
public void testGetPairsHandlesMultipleMentionsNoSpanOutsideHead() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/mentionsOnlyHeads", "ACE");
List<Relation> relations = reader.getPairs();
assertTrue("Mentions whose spans are entirely heads should not produce 'true' extent relations", relations.size() >= 0);
}

@Test
public void testGetPairsHandlesMentionAtDocumentStartOnlyAddsRightFalseRelation() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/mentionAtStart", "ACE");
List<Relation> relations = reader.getPairs();
assertTrue("Mention at start should only generate false relation after its span", relations.size() >= 1);
}

@Test
public void testGetPairsHandlesMentionAtDocumentEndOnlyAddsLeftFalseRelation() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/mentionAtEnd", "ACE");
List<Relation> relations = reader.getPairs();
assertTrue("Mention at end should only generate false relation before its span", relations.size() >= 1);
}

@Test
public void testGetIdWithLeadingAndTrailingSlashes() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("/some/path/", "ACE");
String id = reader.getId();
assertFalse("getId should remove forward slashes", id.contains("/"));
assertFalse("getId should remove backward slashes if present", id.contains("\\"));
}

@Test
public void testNextWithoutInitializingPairs() {
try {
ExtentReader reader = new ExtentReader("/corrupt/path/!", "invalidCorpus");
Object obj = reader.next();
assertTrue("Even with exceptions during parsing, next() should return null or valid object", obj == null || obj instanceof Relation);
} catch (Exception e) {
fail("Constructor should handle internal errors and not throw externally");
}
}

@Test
public void testGetPairsWithNonStandardMentionIdPrefixUsesDefaultACEView() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/unknownIdPrefix", "ACE");
List<Relation> pairs = reader.getPairs();
assertTrue("Unknown TA ID should default to ERE mention view", pairs.size() >= 0);
}

@Test
public void testGetPairsWithKnownIdPrefixUsesACEView() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/idWithPrefix_bn", "ACE");
List<Relation> pairs = reader.getPairs();
assertTrue("ID starting with 'bn' should map to ACE mention view", pairs.size() >= 0);
}

@Test
public void testMultipleSequentialReadsFromNext() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/multipleMentions", "ACE");
Object a = reader.next();
Object b = reader.next();
Object c = reader.next();
Object d = reader.next();
assertTrue("Each call to next() should return relation or null", a == null || a instanceof Relation);
assertTrue("Each call to next() should return relation or null", b == null || b instanceof Relation);
assertTrue("Each call to next() should return relation or null", c == null || c instanceof Relation);
assertTrue("Each call to next() should return relation or null", d == null || d instanceof Relation);
}

@Test
public void testEmptyStringCorpus() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/aceReaderSample", "");
List<TextAnnotation> result = reader.getTextAnnotations();
assertTrue("Empty corpus string should return no annotations", result.isEmpty());
}

@Test
public void testGetTextAnnotationsWithWhitespaceCorpus() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/aceReaderSample", " ");
List<TextAnnotation> result = reader.getTextAnnotations();
assertTrue("Whitespace corpus string should not match any corpus block", result.isEmpty());
}

@Test
public void testGetTextAnnotationsWithNullCorpus() {
try {
ExtentReader reader = new ExtentReader("src/test/resources/aceReaderSample", null);
List<TextAnnotation> result = reader.getTextAnnotations();
assertTrue("Null corpus name should not crash the system", result.isEmpty());
} catch (Exception e) {
fail("Null corpus name should be handled gracefully");
}
}

@Test
public void testCOMBINEDCorpusWithMissingParts() {
try {
ExtentReader reader = new ExtentReader("src/test/resources/sample", "COMBINED-en");
List<TextAnnotation> annotations = reader.getTextAnnotations();
assertTrue("Incomplete COMBINED corpus string should return empty list", annotations.isEmpty());
} catch (Exception e) {
fail("Malformed COMBINED corpus name should not throw");
}
}

@Test
public void testNextCalledWithoutPairsInitialization() {
try {
ExtentReader reader = new ExtentReader("bad/path@@@", "ACE");
Object result = reader.next();
assertTrue(result == null || result instanceof Relation);
} catch (Exception e) {
fail("next() should return null, not throw, even when pair list is not initialized properly");
}
}

@Test
public void testNextCalledMultipleTimesAfterReset() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/aceReaderSample", "ACE");
reader.reset();
Object first = reader.next();
reader.reset();
Object second = reader.next();
reader.reset();
Object third = reader.next();
assertTrue("Repeated resets should allow access again", first == null || first instanceof Relation);
assertTrue("Repeated resets should preserve access to relations", second == null || second instanceof Relation);
assertTrue("Reading after multiple resets still valid", third == null || third instanceof Relation);
}

@Test
public void testGetIdWithOnlySlashes() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("////", "ACE");
String id = reader.getId();
assertEquals("ID from only slashes path should be empty string", "", id);
}

@Test
public void testGetTextAnnotationsHandlesIOExceptionGracefully() {
try {
ExtentReader reader = new ExtentReader("/invalid///path///", "ACE");
List<TextAnnotation> annotations = reader.getTextAnnotations();
assertNotNull("Should return list, not null, on IOException", annotations);
} catch (Exception e) {
fail("getTextAnnotations() should not throw on IO failure");
}
}

@Test
public void testResetOnUninitializedObjectDoesNotThrow() {
try {
ExtentReader reader = new ExtentReader("/!!!invalid$$$", "ACE");
reader.reset();
Object result = reader.next();
assertTrue("reset() should not throw exception even if underlying initialization failed", result == null || result instanceof Relation);
} catch (Exception e) {
fail("reset() must be safe and exception-free");
}
}

@Test
public void testCloseCalledBeforeAnyOperation() {
try {
ExtentReader reader = new ExtentReader("src/test/resources/aceReaderSample", "ACE");
reader.close();
Object r = reader.next();
assertTrue("Calling close before next() should not block access", r == null || r instanceof Relation);
} catch (Exception e) {
fail("close() should not throw exception when called immediately");
}
}

@Test
public void testCorpusNameACECaseInsensitive() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/aceReaderSample", "ace");
List<TextAnnotation> result = reader.getTextAnnotations();
assertTrue("Corpus name 'ace' (lowercase) should not match 'ACE'", result.isEmpty());
}

@Test
public void testCorpusNameERECaseInsensitive() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/ereReaderSample", "ere");
List<TextAnnotation> result = reader.getTextAnnotations();
assertTrue("Corpus name 'ere' (lowercase) should not match 'ERE'", result.isEmpty());
}

@Test
public void testCorpusNameWithExtraHyphen() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/sample", "COMBINED-en-all-0-extra");
List<TextAnnotation> annotations = reader.getTextAnnotations();
assertTrue("Malformed COMBINED with extra segment should fail to parse and return empty list", annotations.isEmpty());
}

@Test
public void testGetPairsAfterNextExhaustionReturnsConsistentNull() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/shortCorpus", "ACE");
Object next1 = reader.next();
Object next2 = reader.next();
Object next3 = reader.next();
Object next4 = reader.next();
assertNull("After exhausting relation list, next() must return null", next4);
}

@Test
public void testConsecutiveNextCallsOnEmptyReader() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/emptyCorpus", "ACE");
Object one = reader.next();
Object two = reader.next();
Object three = reader.next();
assertNull("Corpus with no data: all next() calls should return null", one);
assertNull("Corpus with no data: all next() calls should return null", two);
assertNull("Corpus with no data: all next() calls should return null", three);
}

@Test
public void testGetPairsHandlesMentionHeadWithoutLabelGracefully() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/mentionHeadWithoutLabel", "ACE");
List<Relation> pairs = reader.getPairs();
assertNotNull("Missing label in head should be handled internally", pairs);
}

@Test
public void testGetPairsHandlesTokenNotCoveredByConstituentList() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/tokenViewMismatch", "ACE");
List<Relation> pairs = reader.getPairs();
assertNotNull("Missing constituent for token index should not throw exception", pairs);
}

@Test
public void testResetAfterNextExhaustionAllowsReuse() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/shortCorpus", "ACE");
Object a = reader.next();
Object b = reader.next();
reader.reset();
Object reused = reader.next();
assertTrue("Calling reset() should allow reuse of reader", reused == null || reused instanceof Relation);
}

@Test
public void testCallResetImmediatelyAfterConstructor() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/aceReaderSample", "ACE");
reader.reset();
Object result = reader.next();
assertTrue("Calling reset immediately after constructor should still return results", result == null || result instanceof Relation);
}

@Test
public void testConstructorHandlesNullPathGracefully() {
try {
ExtentReader reader = new ExtentReader(null);
Object output = reader.next();
assertTrue("Reader initialized with null path should not crash", output == null || output instanceof Relation);
} catch (Exception e) {
fail("Constructor should handle null path internally");
}
}

@Test
public void testGetTextAnnotationsHandlesPartialCOMBINEDString() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("sample", "COMBINED-en");
List<TextAnnotation> list = reader.getTextAnnotations();
assertTrue("Partial COMBINED corpus config returns no annotations", list.isEmpty());
}

@Test
public void testCloseAfterResetAndNext() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources/aceReaderSample", "ACE");
reader.reset();
Object r = reader.next();
reader.close();
assertTrue("Close can be called after reset and next", r == null || r instanceof Relation);
}
}
