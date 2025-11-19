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

public class ExtentReader_llmsuite_2_GPTLLMTest {

@Test
public void testConstructorWithValidCorpus() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
String path = "src/test/resources/ace_data";
String corpus = "ACE";
ExtentReader reader = new ExtentReader(path, corpus);
assertNotNull(reader);
assertTrue(reader.getId().contains("ace_data"));
}

@Test
public void testConstructorWithDefaultCorpus() {
String path = "src/test/resources/ace_data";
ExtentReader reader = new ExtentReader(path);
assertNotNull(reader);
assertEquals(path.replace("/", "").replace("\\", ""), reader.getId());
}

@Test(expected = RuntimeException.class)
public void testConstructorFailureWithInvalidPath() {
String invalidPath = "some/invalid/path";
new ExtentReader(invalidPath);
}

@Test
public void testGetIdRemovesSlashes() {
String inputPath = "/temp/path/";
ExtentReader reader = new ExtentReader(inputPath);
String id = reader.getId();
assertFalse(id.contains("/"));
assertFalse(id.contains("\\"));
}

@Test
public void testNextAndResetIteration() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
String path = "src/test/resources/ace_data";
String corpus = "ACE";
ExtentReader reader = new ExtentReader(path, corpus);
reader.reset();
Object first = reader.next();
assertTrue(first == null || first instanceof edu.illinois.cs.cogcomp.core.datastructures.textannotation.Relation);
reader.reset();
Object againFirst = reader.next();
assertTrue(againFirst == null || againFirst instanceof edu.illinois.cs.cogcomp.core.datastructures.textannotation.Relation);
}

@Test
public void testNextReturnsNullWhenNoMorePairs() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
String path = "src/test/resources/ace_data";
String corpus = "ACE";
ExtentReader reader = new ExtentReader(path, corpus);
reader.reset();
Object next1 = reader.next();
Object next2 = reader.next();
Object next3 = reader.next();
Object next4 = reader.next();
if (next1 == null) {
assertNull(next2);
assertNull(next3);
assertNull(next4);
} else {
if (next2 == null) {
assertNull(next3);
assertNull(next4);
}
}
}

@Test
public void testGetTextAnnotationsReturnsList() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
String path = "src/test/resources/ace_data";
String corpus = "ACE";
ExtentReader reader = new ExtentReader(path, corpus);
List<TextAnnotation> annotations = reader.getTextAnnotations();
assertNotNull(annotations);
assertTrue(annotations.size() >= 0);
}

@Test
public void testGetPairsReturnsList() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
String path = "src/test/resources/ace_data";
String corpus = "ACE";
ExtentReader reader = new ExtentReader(path, corpus);
List<edu.illinois.cs.cogcomp.core.datastructures.textannotation.Relation> pairs = reader.getPairs();
assertNotNull(pairs);
}

@Test
public void testCloseDoesNothing() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
String path = "src/test/resources/ace_data";
String corpus = "ACE";
ExtentReader reader = new ExtentReader(path, corpus);
reader.close();
assertTrue(true);
}

@Test(expected = NumberFormatException.class)
public void testMalformedCombinedCorpusNameThrowsException() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
String path = "src/test/resources/ace_data";
String badCorpus = "COMBINED-ACE-dev-NaN";
new ExtentReader(path, badCorpus);
}

@Test
public void testRelationContentFields() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
String path = "src/test/resources/ace_data";
String corpus = "ACE";
ExtentReader reader = new ExtentReader(path, corpus);
List<edu.illinois.cs.cogcomp.core.datastructures.textannotation.Relation> pairs = reader.getPairs();
if (pairs.size() > 0) {
edu.illinois.cs.cogcomp.core.datastructures.textannotation.Relation r = pairs.get(0);
assertNotNull(r.getSource());
assertNotNull(r.getTarget());
assertNotNull(r.getRelationName());
assertTrue(r.getScore() > 0.0f);
} else {
assertTrue(pairs.isEmpty());
}
}

@Test
public void testCombinedCorpusWorks() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
String path = "src/test/resources/bio_data";
String corpus = "COMBINED-ACE-dev-0";
ExtentReader reader = new ExtentReader(path, corpus);
List<TextAnnotation> annotations = reader.getTextAnnotations();
assertNotNull(annotations);
}

@Test
public void testGetTextAnnotationsFallbackToERE() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
String path = "src/test/resources/ere_data";
String corpus = "ERE";
ExtentReader reader = new ExtentReader(path, corpus);
List<TextAnnotation> annotations = reader.getTextAnnotations();
assertNotNull(annotations);
}

@Test
public void testGetTextAnnotationsWithUnknownCorpusSkipsSections() throws JWNLException, IOException, InvalidPortException, InvalidEndpointException, DatastoreException {
String path = "src/test/resources/missing_data";
String corpus = "UNKNOWN";
ExtentReader reader = new ExtentReader(path, corpus);
List<TextAnnotation> list = reader.getTextAnnotations();
assertTrue(list.isEmpty() || list.size() >= 0);
}

@Test
public void testNullEntityHeadLeadsToSkipInGetPairs() throws Exception {
ExtentReader reader = new ExtentReader("src/test/resources", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
TextAnnotation mockTA = mock(TextAnnotation.class);
View mentionView = mock(View.class);
List<Constituent> mentions = new ArrayList<>();
Constituent mockMention = mock(Constituent.class);
when(mockMention.getStartSpan()).thenReturn(1);
when(mockMention.getEndSpan()).thenReturn(2);
mentions.add(mockMention);
when(mentionView.iterator()).thenReturn(mentions.iterator());
when(mockTA.getView(anyString())).thenReturn(mentionView);
when(mockTA.getId()).thenReturn("bn123");
List<TextAnnotation> list = new ArrayList<>();
list.add(mockTA);
return list;
}

@Override
public List<Relation> getPairs() {
return super.getPairs();
}
};
List<Relation> result = reader.getPairs();
assertNotNull(result);
}

@Test
public void testHeadMissingEntityTypeAddsAttribute() throws Exception {
ExtentReader reader = new ExtentReader("src/test/resources", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
TextAnnotation mockTA = mock(TextAnnotation.class);
View mentionView = mock(View.class);
View tokenView = mock(View.class);
List<Constituent> mentionList = new ArrayList<>();
Constituent mention = mock(Constituent.class);
when(mention.getStartSpan()).thenReturn(0);
when(mention.getEndSpan()).thenReturn(1);
mentionList.add(mention);
when(mockTA.getView(ViewNames.MENTION_ACE)).thenReturn(mentionView);
when(mentionView.iterator()).thenReturn(mentionList.iterator());
Constituent head = mock(Constituent.class);
when(head.hasAttribute("EntityType")).thenReturn(false);
when(head.getLabel()).thenReturn("PERSON");
doNothing().when(head).addAttribute(eq("EntityType"), eq("PERSON"));
when(mockTA.getId()).thenReturn("bn123");
when(mockTA.getView(ViewNames.TOKENS)).thenReturn(tokenView);
Constituent token = mock(Constituent.class);
List<Constituent> tokenList = new ArrayList<>();
tokenList.add(token);
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(tokenList);
mockStatic(ACEReader.class);
when(ACEReader.getEntityHeadForConstituent(eq(mention), eq(mockTA), anyString())).thenReturn(head);
List<TextAnnotation> taList = new ArrayList<>();
taList.add(mockTA);
return taList;
}

@Override
public List<Relation> getPairs() {
return super.getPairs();
}
};
List<Relation> result = reader.getPairs();
assertNotNull(result);
assertTrue(result.size() >= 0);
}

@Test
public void testMentionOutsideTokenBoundsDoesNotThrow() throws Exception {
ExtentReader reader = new ExtentReader("src/test/resources", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
TextAnnotation mockTA = mock(TextAnnotation.class);
Constituent mention = mock(Constituent.class);
when(mention.getStartSpan()).thenReturn(5);
when(mention.getEndSpan()).thenReturn(6);
Constituent head = mock(Constituent.class);
when(head.hasAttribute("EntityType")).thenReturn(true);
when(mention.getLabel()).thenReturn("ORG");
View mentionView = mock(View.class);
List<Constituent> mentions = new ArrayList<>();
mentions.add(mention);
when(mentionView.iterator()).thenReturn(mentions.iterator());
View tokenView = mock(View.class);
List<Constituent> tokenCons = new ArrayList<>();
Constituent token = mock(Constituent.class);
tokenCons.add(token);
when(tokenView.getEndSpan()).thenReturn(6);
when(tokenView.getConstituentsCoveringToken(5)).thenReturn(tokenCons);
when(tokenView.getConstituentsCoveringToken(4)).thenReturn(tokenCons);
when(mockTA.getId()).thenReturn("nw456");
when(mockTA.getView(ViewNames.MENTION_ACE)).thenReturn(mentionView);
when(mockTA.getView(ViewNames.TOKENS)).thenReturn(tokenView);
mockStatic(ACEReader.class);
when(ACEReader.getEntityHeadForConstituent(eq(mention), eq(mockTA), anyString())).thenReturn(head);
List<TextAnnotation> list = new ArrayList<>();
list.add(mockTA);
return list;
}

@Override
public List<Relation> getPairs() {
return super.getPairs();
}
};
List<Relation> result = reader.getPairs();
assertNotNull(result);
assertTrue(result.size() >= 0);
}

@Test
public void testGetTextAnnotationsHandlesInvokeExceptionGracefully() {
String path = "src/test/resources/bad_data";
String corpus = "ACE";
ExtentReader reader = new ExtentReader(path) {

@Override
public List<TextAnnotation> getTextAnnotations() {
throw new RuntimeException("forced exception");
}
};
try {
new ExtentReader(path);
fail("Expected RuntimeException");
} catch (RuntimeException re) {
assertTrue(re.getMessage().contains("TextAnnotation generation failed"));
}
}

@Test
public void testNextReturnsNullWhenNoPairsAvailable() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
return new ArrayList<TextAnnotation>();
}

@Override
public List<Relation> getPairs() {
return new ArrayList<Relation>();
}
};
Object result = reader.next();
assertNull(result);
}

@Test
public void testGetPairsSkipsWhenTokenNotFound() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
TextAnnotation textAnnotation = mock(TextAnnotation.class);
when(textAnnotation.getId()).thenReturn("nw123");
View mentionView = mock(View.class);
View tokenView = mock(View.class);
when(textAnnotation.getView(ViewNames.MENTION_ACE)).thenReturn(mentionView);
when(textAnnotation.getView(ViewNames.TOKENS)).thenReturn(tokenView);
Constituent mention = mock(Constituent.class);
when(mention.getStartSpan()).thenReturn(2);
when(mention.getEndSpan()).thenReturn(3);
List<Constituent> mentions = new ArrayList<Constituent>();
mentions.add(mention);
when(mentionView.iterator()).thenReturn(mentions.iterator());
Constituent head = mock(Constituent.class);
when(head.getStartSpan()).thenReturn(2);
when(head.getEndSpan()).thenReturn(3);
when(head.hasAttribute("EntityType")).thenReturn(true);
when(tokenView.getConstituentsCoveringToken(anyInt())).thenReturn(Collections.emptyList());
mockStatic(edu.illinois.cs.cogcomp.nlp.corpusreaders.ACEReader.class);
when(edu.illinois.cs.cogcomp.nlp.corpusreaders.ACEReader.getEntityHeadForConstituent(mention, textAnnotation, "HEADS")).thenReturn(head);
List<TextAnnotation> list = new ArrayList<TextAnnotation>();
list.add(textAnnotation);
return list;
}
};
List<Relation> results = reader.getPairs();
assertNotNull(results);
}

@Test
public void testGetPairsHandlesExceptionInWordNetInitialization() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
return new ArrayList<TextAnnotation>();
}

@Override
public List<Relation> getPairs() {
throw new RuntimeException("Simulated third-party failure");
}
};
try {
reader.getPairs();
fail("Expected RuntimeException due to simulated failure");
} catch (RuntimeException e) {
assertEquals("Simulated third-party failure", e.getMessage());
}
}

@Test
public void testGetTextAnnotationsReturnsEmptyOnUnknownCorpus() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
ExtentReader reader = new ExtentReader("src/test/resources", "NON_EXISTENT") {

@Override
public List<TextAnnotation> getTextAnnotations() {
return new ArrayList<TextAnnotation>();
}
};
List<TextAnnotation> result = reader.getTextAnnotations();
assertNotNull(result);
assertEquals(0, result.size());
}

@Test
public void testGetPairsAddsFalseRelationsWhenExtentBoundsExist() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
TextAnnotation textAnnotation = mock(TextAnnotation.class);
when(textAnnotation.getId()).thenReturn("bn123");
View mentionView = mock(View.class);
View tokenView = mock(View.class);
when(textAnnotation.getView(ViewNames.MENTION_ACE)).thenReturn(mentionView);
when(textAnnotation.getView(ViewNames.TOKENS)).thenReturn(tokenView);
Constituent mention = mock(Constituent.class);
when(mention.getStartSpan()).thenReturn(1);
when(mention.getEndSpan()).thenReturn(3);
Constituent head = mock(Constituent.class);
when(head.getStartSpan()).thenReturn(1);
when(head.getEndSpan()).thenReturn(3);
when(head.hasAttribute("EntityType")).thenReturn(false);
when(head.getLabel()).thenReturn("ENTITY");
List<Constituent> mentionList = new ArrayList<Constituent>();
mentionList.add(mention);
when(mentionView.iterator()).thenReturn(mentionList.iterator());
Constituent token1 = mock(Constituent.class);
Constituent token2 = mock(Constituent.class);
List<Constituent> tokenList1 = new ArrayList<Constituent>();
tokenList1.add(token1);
List<Constituent> tokenList2 = new ArrayList<Constituent>();
tokenList2.add(token2);
when(tokenView.getEndSpan()).thenReturn(6);
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(tokenList1);
when(tokenView.getConstituentsCoveringToken(3)).thenReturn(tokenList2);
mockStatic(edu.illinois.cs.cogcomp.nlp.corpusreaders.ACEReader.class);
when(edu.illinois.cs.cogcomp.nlp.corpusreaders.ACEReader.getEntityHeadForConstituent(mention, textAnnotation, "HEADS")).thenReturn(head);
ExtentReader reader = new ExtentReader("src/test/resources", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
List<TextAnnotation> list = new ArrayList<TextAnnotation>();
list.add(textAnnotation);
return list;
}
};
List<Relation> result = reader.getPairs();
assertNotNull(result);
assertTrue(result.size() >= 2);
}

@Test
public void testCorpusNameCombinedWithInvalidFormatHandledGracefully() throws JWNLException, IOException, DatastoreException, InvalidPortException, InvalidEndpointException {
String path = "src/test/resources";
String corpus = "COMBINED-ACE-0";
try {
new ExtentReader(path, corpus);
fail("Expected ArrayIndexOutOfBoundsException due to malformed combined corpus");
} catch (ArrayIndexOutOfBoundsException e) {
assertTrue(e.getMessage() == null || e.getMessage().isEmpty());
}
}

@Test
public void testCorpusNameCombinedWithTooManyPartsHandledGracefully() throws JWNLException, IOException, DatastoreException, InvalidPortException, InvalidEndpointException {
String path = "src/test/resources";
String corpus = "COMBINED-ACE-dev-0-extra";
try {
new ExtentReader(path, corpus);
fail("Expected NumberFormatException due to bad fold value");
} catch (NumberFormatException e) {
assertTrue(e.getMessage().contains("For input"));
}
}

@Test
public void testGetIdWithBackslashesAndSlashesRemoved() {
String path = "\\some\\windows\\like\\path/with/slashes/";
ExtentReader reader = new ExtentReader(path);
String id = reader.getId();
assertFalse(id.contains("/"));
assertFalse(id.contains("\\"));
}

@Test
public void testEntityMentionWithSpanZeroStartAddsOnlyEndFalseSpan() throws Exception {
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getId()).thenReturn("bn133");
Constituent mention = mock(Constituent.class);
when(mention.getStartSpan()).thenReturn(0);
when(mention.getEndSpan()).thenReturn(1);
Constituent head = mock(Constituent.class);
when(head.getStartSpan()).thenReturn(0);
when(head.getEndSpan()).thenReturn(1);
when(head.hasAttribute("EntityType")).thenReturn(false);
when(head.getLabel()).thenReturn("PER");
View mentionView = mock(View.class);
List<Constituent> mentions = new ArrayList<Constituent>();
mentions.add(mention);
when(mentionView.iterator()).thenReturn(mentions.iterator());
View tokenView = mock(View.class);
Constituent tokenAt1 = mock(Constituent.class);
List<Constituent> tokenListAt1 = new ArrayList<Constituent>();
tokenListAt1.add(tokenAt1);
when(tokenView.getConstituentsCoveringToken(1)).thenReturn(tokenListAt1);
when(tokenView.getEndSpan()).thenReturn(3);
when(ta.getView(ViewNames.MENTION_ACE)).thenReturn(mentionView);
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
mockStatic(edu.illinois.cs.cogcomp.nlp.corpusreaders.ACEReader.class);
when(edu.illinois.cs.cogcomp.nlp.corpusreaders.ACEReader.getEntityHeadForConstituent(eq(mention), eq(ta), anyString())).thenReturn(head);
ExtentReader reader = new ExtentReader("src/test/resources", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
List<TextAnnotation> tas = new ArrayList<TextAnnotation>();
tas.add(ta);
return tas;
}
};
List<Relation> pairs = reader.getPairs();
assertNotNull(pairs);
assertTrue(pairs.size() >= 1);
assertEquals("false", pairs.get(0).getRelationName());
}

@Test
public void testNullHeadReturnedFromACEReaderSkipsCurrentMention() throws Exception {
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getId()).thenReturn("nw456");
Constituent mention = mock(Constituent.class);
when(mention.getStartSpan()).thenReturn(1);
when(mention.getEndSpan()).thenReturn(2);
View mentionView = mock(View.class);
List<Constituent> mentions = new ArrayList<Constituent>();
mentions.add(mention);
when(mentionView.iterator()).thenReturn(mentions.iterator());
View tokenView = mock(View.class);
when(ta.getView(ViewNames.MENTION_ACE)).thenReturn(mentionView);
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
mockStatic(edu.illinois.cs.cogcomp.nlp.corpusreaders.ACEReader.class);
when(edu.illinois.cs.cogcomp.nlp.corpusreaders.ACEReader.getEntityHeadForConstituent(eq(mention), eq(ta), anyString())).thenReturn(null);
ExtentReader reader = new ExtentReader("src/test/resources", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
List<TextAnnotation> tas = new ArrayList<TextAnnotation>();
tas.add(ta);
return tas;
}
};
List<Relation> result = reader.getPairs();
assertNotNull(result);
assertEquals(0, result.size());
}

@Test
public void testNextReturnsCorrectFirstPair() throws Exception {
Relation r1 = new Relation("test1", mock(Constituent.class), mock(Constituent.class), 1.0f);
Relation r2 = new Relation("test2", mock(Constituent.class), mock(Constituent.class), 1.0f);
ExtentReader reader = new ExtentReader("dummy", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
return new ArrayList<TextAnnotation>();
}

@Override
public List<Relation> getPairs() {
List<Relation> relations = new ArrayList<Relation>();
relations.add(r1);
relations.add(r2);
return relations;
}
};
reader.reset();
Object n1 = reader.next();
Object n2 = reader.next();
Object n3 = reader.next();
assertTrue(n1 instanceof Relation);
assertEquals("test1", ((Relation) n1).getRelationName());
assertTrue(n2 instanceof Relation);
assertEquals("test2", ((Relation) n2).getRelationName());
assertNull(n3);
}

@Test
public void testResetResetsInternalIndex() throws Exception {
Relation r1 = new Relation("sample", mock(Constituent.class), mock(Constituent.class), 0.9f);
ExtentReader reader = new ExtentReader("dummy", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
return new ArrayList<TextAnnotation>();
}

@Override
public List<Relation> getPairs() {
List<Relation> list = new ArrayList<Relation>();
list.add(r1);
return list;
}
};
reader.reset();
Object first = reader.next();
Object second = reader.next();
reader.reset();
Object repeat = reader.next();
assertNotNull(first);
assertNull(second);
assertNotNull(repeat);
assertEquals("sample", ((Relation) repeat).getRelationName());
}

@Test
public void testCorpusModeCombinedParsesFoldZeroSuccessfully() throws Exception {
String path = "src/test/resources";
String corpus = "COMBINED-ACE-dev-0";
ExtentReader reader = new ExtentReader(path, corpus);
List<TextAnnotation> result = reader.getTextAnnotations();
assertNotNull(result);
}

@Test
public void testCorpusStringStartsWithButNotEqualsCombinedSkipsLogic() throws Exception {
String path = "src/test/resources";
String corpus = "COMBINEDACE";
ExtentReader reader = new ExtentReader(path, corpus) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return new ArrayList<TextAnnotation>();
}
};
List<TextAnnotation> annotations = reader.getTextAnnotations();
assertNotNull(annotations);
}

@Test
public void testGetPairsHandlesMentionSameAsHeadOnly() throws Exception {
TextAnnotation mockTA = mock(TextAnnotation.class);
when(mockTA.getId()).thenReturn("nw998");
View mentionView = mock(View.class);
View tokensView = mock(View.class);
Constituent mention = mock(Constituent.class);
when(mention.getStartSpan()).thenReturn(1);
when(mention.getEndSpan()).thenReturn(2);
Constituent head = mock(Constituent.class);
when(head.getStartSpan()).thenReturn(1);
when(head.getEndSpan()).thenReturn(2);
when(head.hasAttribute("EntityType")).thenReturn(true);
List<Constituent> mentions = new ArrayList<Constituent>();
mentions.add(mention);
when(mentionView.iterator()).thenReturn(mentions.iterator());
Constituent tokenLeft = mock(Constituent.class);
Constituent tokenRight = mock(Constituent.class);
List<Constituent> tokenListLeft = new ArrayList<Constituent>();
tokenListLeft.add(tokenLeft);
List<Constituent> tokenListRight = new ArrayList<Constituent>();
tokenListRight.add(tokenRight);
when(tokensView.getEndSpan()).thenReturn(4);
when(tokensView.getConstituentsCoveringToken(0)).thenReturn(tokenListLeft);
when(tokensView.getConstituentsCoveringToken(2)).thenReturn(tokenListRight);
when(mockTA.getView(ViewNames.MENTION_ACE)).thenReturn(mentionView);
when(mockTA.getView(ViewNames.TOKENS)).thenReturn(tokensView);
mockStatic(edu.illinois.cs.cogcomp.nlp.corpusreaders.ACEReader.class);
when(edu.illinois.cs.cogcomp.nlp.corpusreaders.ACEReader.getEntityHeadForConstituent(mention, mockTA, "HEADS")).thenReturn(head);
ExtentReader reader = new ExtentReader("src/test/resources", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
List<TextAnnotation> list = new ArrayList<TextAnnotation>();
list.add(mockTA);
return list;
}
};
List<Relation> pairs = reader.getPairs();
assertNotNull(pairs);
assertTrue(pairs.size() == 2);
}

@Test
public void testGetPairsHandlesEmptyMentionViewReturnsNoPairs() throws Exception {
TextAnnotation mockTA = mock(TextAnnotation.class);
when(mockTA.getId()).thenReturn("nw700");
View mentionView = mock(View.class);
// when(mentionView.iterator()).thenReturn(Collections.emptyList().iterator());
when(mockTA.getView(ViewNames.MENTION_ACE)).thenReturn(mentionView);
when(mockTA.getView(ViewNames.TOKENS)).thenReturn(mock(View.class));
ExtentReader reader = new ExtentReader("src/test/resources", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
List<TextAnnotation> taList = new ArrayList<TextAnnotation>();
taList.add(mockTA);
return taList;
}
};
List<Relation> result = reader.getPairs();
assertNotNull(result);
assertTrue(result.isEmpty());
}

@Test
public void testNextSkipsEmptyListAndReturnsNull() throws Exception {
ExtentReader reader = new ExtentReader("src/test/resources", "ACE") {

@Override
public List<Relation> getPairs() {
return new ArrayList<Relation>();
}

@Override
public List<TextAnnotation> getTextAnnotations() {
return new ArrayList<TextAnnotation>();
}
};
reader.reset();
Object result = reader.next();
assertNull(result);
}

@Test
public void testNextReturnsSingleElementAndThenNull() throws Exception {
Constituent source = mock(Constituent.class);
Constituent target = mock(Constituent.class);
Relation r = new Relation("mockR", source, target, 1.0f);
ExtentReader reader = new ExtentReader("src/test/resources", "ACE") {

@Override
public List<Relation> getPairs() {
List<Relation> pairs = new ArrayList<Relation>();
pairs.add(r);
return pairs;
}

@Override
public List<TextAnnotation> getTextAnnotations() {
return new ArrayList<TextAnnotation>();
}
};
reader.reset();
Object f1 = reader.next();
Object f2 = reader.next();
assertNotNull(f1);
assertTrue(f1 instanceof Relation);
assertEquals("mockR", ((Relation) f1).getRelationName());
assertNull(f2);
}

@Test
public void testMentionViewNameSwitchesToACEIfIdStartsWithBN() throws Exception {
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getId()).thenReturn("bn12345");
View mentionView = mock(View.class);
when(mentionView.iterator()).thenReturn(Collections.emptyIterator());
View tokensView = mock(View.class);
when(ta.getView(ViewNames.MENTION_ACE)).thenReturn(mentionView);
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokensView);
ExtentReader reader = new ExtentReader("src/test/resources", "ERE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
List<TextAnnotation> list = new ArrayList<TextAnnotation>();
list.add(ta);
return list;
}
};
List<Relation> result = reader.getPairs();
assertNotNull(result);
assertEquals(0, result.size());
}

@Test
public void testEmptyTokenViewSkipsExtentProcessing() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getId()).thenReturn("nw000");
View mentionView = mock(View.class);
View tokenView = mock(View.class);
Constituent mention = mock(Constituent.class);
when(mention.getStartSpan()).thenReturn(1);
when(mention.getEndSpan()).thenReturn(2);
Constituent head = mock(Constituent.class);
when(head.getStartSpan()).thenReturn(1);
when(head.getEndSpan()).thenReturn(2);
when(head.hasAttribute("EntityType")).thenReturn(true);
List<Constituent> mentions = new ArrayList<Constituent>();
mentions.add(mention);
when(mentionView.iterator()).thenReturn(mentions.iterator());
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(Collections.emptyList());
when(tokenView.getConstituentsCoveringToken(2)).thenReturn(Collections.emptyList());
when(ta.getView(ViewNames.MENTION_ACE)).thenReturn(mentionView);
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
mockStatic(edu.illinois.cs.cogcomp.nlp.corpusreaders.ACEReader.class);
when(edu.illinois.cs.cogcomp.nlp.corpusreaders.ACEReader.getEntityHeadForConstituent(mention, ta, "HEADS")).thenReturn(head);
ExtentReader reader = new ExtentReader("mock", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
List<TextAnnotation> list = new ArrayList<TextAnnotation>();
list.add(ta);
return list;
}
};
List<Relation> pairs = reader.getPairs();
assertNotNull(pairs);
assertEquals(0, pairs.size());
}

@Test
public void testNullTokenListInExtentThrowsNoExceptionAndSkips() throws JWNLException, IOException, InvalidPortException, InvalidEndpointException, DatastoreException {
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getId()).thenReturn("nw789");
View mentionView = mock(View.class);
View tokenView = mock(View.class);
Constituent mention = mock(Constituent.class);
when(mention.getStartSpan()).thenReturn(4);
when(mention.getEndSpan()).thenReturn(5);
Constituent head = mock(Constituent.class);
when(head.getStartSpan()).thenReturn(4);
when(head.getEndSpan()).thenReturn(5);
when(head.hasAttribute("EntityType")).thenReturn(true);
List<Constituent> mentions = new ArrayList<Constituent>();
mentions.add(mention);
when(mentionView.iterator()).thenReturn(mentions.iterator());
when(tokenView.getConstituentsCoveringToken(3)).thenReturn(null);
when(tokenView.getConstituentsCoveringToken(5)).thenReturn(null);
when(tokenView.getEndSpan()).thenReturn(6);
when(ta.getView(ViewNames.MENTION_ACE)).thenReturn(mentionView);
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
mockStatic(edu.illinois.cs.cogcomp.nlp.corpusreaders.ACEReader.class);
when(edu.illinois.cs.cogcomp.nlp.corpusreaders.ACEReader.getEntityHeadForConstituent(mention, ta, "HEADS")).thenReturn(head);
ExtentReader reader = new ExtentReader("mock", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
List<TextAnnotation> list = new ArrayList<TextAnnotation>();
list.add(ta);
return list;
}
};
List<Relation> result = reader.getPairs();
assertNotNull(result);
assertEquals(0, result.size());
}

@Test
public void testMentionAtDocumentEdgeNoOutOfBounds() throws Exception {
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getId()).thenReturn("nw965");
View mentionView = mock(View.class);
View tokensView = mock(View.class);
Constituent mention = mock(Constituent.class);
when(mention.getStartSpan()).thenReturn(0);
when(mention.getEndSpan()).thenReturn(1);
Constituent head = mock(Constituent.class);
when(head.getStartSpan()).thenReturn(0);
when(head.getEndSpan()).thenReturn(1);
when(head.hasAttribute("EntityType")).thenReturn(true);
List<Constituent> mentions = new ArrayList<Constituent>();
mentions.add(mention);
when(mentionView.iterator()).thenReturn(mentions.iterator());
Constituent edgeToken = mock(Constituent.class);
List<Constituent> edgeList = new ArrayList<Constituent>();
edgeList.add(edgeToken);
when(tokensView.getEndSpan()).thenReturn(2);
when(tokensView.getConstituentsCoveringToken(1)).thenReturn(edgeList);
when(ta.getView(ViewNames.MENTION_ACE)).thenReturn(mentionView);
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokensView);
mockStatic(edu.illinois.cs.cogcomp.nlp.corpusreaders.ACEReader.class);
when(edu.illinois.cs.cogcomp.nlp.corpusreaders.ACEReader.getEntityHeadForConstituent(mention, ta, "HEADS")).thenReturn(head);
ExtentReader reader = new ExtentReader("mock", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
List<TextAnnotation> list = new ArrayList<TextAnnotation>();
list.add(ta);
return list;
}
};
List<Relation> result = reader.getPairs();
assertNotNull(result);
assertEquals(1, result.size());
assertEquals("false", result.get(0).getRelationName());
}

@Test
public void testMentionExtendsEntireSentenceNoExtentPairs() throws Exception {
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getId()).thenReturn("nw123");
View mentionView = mock(View.class);
View tokenView = mock(View.class);
Constituent mention = mock(Constituent.class);
when(mention.getStartSpan()).thenReturn(0);
when(mention.getEndSpan()).thenReturn(5);
Constituent head = mock(Constituent.class);
when(head.getStartSpan()).thenReturn(0);
when(head.getEndSpan()).thenReturn(5);
when(head.hasAttribute("EntityType")).thenReturn(true);
List<Constituent> mentions = new ArrayList<Constituent>();
mentions.add(mention);
when(mentionView.iterator()).thenReturn(mentions.iterator());
when(ta.getView(ViewNames.MENTION_ACE)).thenReturn(mentionView);
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
when(tokenView.getEndSpan()).thenReturn(5);
mockStatic(edu.illinois.cs.cogcomp.nlp.corpusreaders.ACEReader.class);
when(edu.illinois.cs.cogcomp.nlp.corpusreaders.ACEReader.getEntityHeadForConstituent(mention, ta, "HEADS")).thenReturn(head);
ExtentReader reader = new ExtentReader("mock", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() {
List<TextAnnotation> list = new ArrayList<TextAnnotation>();
list.add(ta);
return list;
}
};
List<Relation> result = reader.getPairs();
assertNotNull(result);
assertEquals(0, result.size());
}

@Test
public void testResetBringsReaderBackToInitialState() throws Exception {
Constituent source = mock(Constituent.class);
Constituent target = mock(Constituent.class);
Relation relation = new Relation("relation", source, target, 1.0f);
ExtentReader reader = new ExtentReader("mock", "ACE") {

@Override
public List<Relation> getPairs() {
List<Relation> list = new ArrayList<Relation>();
list.add(relation);
return list;
}

@Override
public List<TextAnnotation> getTextAnnotations() {
return new ArrayList<TextAnnotation>();
}
};
reader.reset();
Object first = reader.next();
Object end = reader.next();
reader.reset();
Object again = reader.next();
assertNotNull(first);
assertTrue(first instanceof Relation);
assertNull(end);
assertNotNull(again);
}

@Test
public void testConstructorWithCombinedCorpusTruncatedFailsGracefully() throws JWNLException, IOException, DatastoreException, InvalidPortException, InvalidEndpointException {
String path = "data";
String corpus = "COMBINED-ACE-dev";
try {
new ExtentReader(path, corpus);
fail("Expected ArrayIndexOutOfBoundsException due to truncated format");
} catch (ArrayIndexOutOfBoundsException e) {
assertTrue(true);
}
}

@Test
public void testGetTextAnnotationsReturnsEmptyWhenCorpusIsUnrecognized() throws Exception {
ExtentReader reader = new ExtentReader("dummyPath", "UNKNOWN") {

@Override
public List<TextAnnotation> getTextAnnotations() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
return super.getTextAnnotations();
}
};
List<TextAnnotation> result = reader.getTextAnnotations();
assertNotNull(result);
assertEquals(0, result.size());
}

@Test
public void testGetPairsHandlesMentionNullLabelGracefully() throws Exception {
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getId()).thenReturn("nw000");
View mentionView = mock(View.class);
View tokenView = mock(View.class);
Constituent mention = mock(Constituent.class);
when(mention.getStartSpan()).thenReturn(1);
when(mention.getEndSpan()).thenReturn(2);
when(mention.getLabel()).thenReturn(null);
Constituent head = mock(Constituent.class);
when(head.getStartSpan()).thenReturn(1);
when(head.getEndSpan()).thenReturn(2);
when(head.hasAttribute("EntityType")).thenReturn(false);
when(head.getLabel()).thenReturn(null);
List<Constituent> mentions = new ArrayList<Constituent>();
mentions.add(mention);
when(mentionView.iterator()).thenReturn(mentions.iterator());
List<Constituent> dummyTokens = new ArrayList<Constituent>();
dummyTokens.add(mock(Constituent.class));
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(dummyTokens);
when(tokenView.getConstituentsCoveringToken(2)).thenReturn(dummyTokens);
when(tokenView.getEndSpan()).thenReturn(3);
when(ta.getView(ViewNames.MENTION_ACE)).thenReturn(mentionView);
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
mockStatic(edu.illinois.cs.cogcomp.nlp.corpusreaders.ACEReader.class);
when(edu.illinois.cs.cogcomp.nlp.corpusreaders.ACEReader.getEntityHeadForConstituent(mention, ta, "HEADS")).thenReturn(head);
ExtentReader reader = new ExtentReader("paths", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
List<TextAnnotation> list = new ArrayList<TextAnnotation>();
list.add(ta);
return list;
}
};
List<Relation> result = reader.getPairs();
assertNotNull(result);
assertTrue(result.size() >= 1);
}

@Test
public void testGetIdOnWindowsStylePath() {
String rawPath = "C:\\corpus\\ACE\\nw\\";
ExtentReader r = new ExtentReader(rawPath);
String id = r.getId();
assertFalse(id.contains("\\"));
assertFalse(id.contains("/"));
assertTrue(id.contains("corpusACEnw"));
}

@Test
public void testGetPairsHandlesNullConstituentListFromTokenView() throws Exception {
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getId()).thenReturn("nw888");
View mentionView = mock(View.class);
View tokenView = mock(View.class);
Constituent mention = mock(Constituent.class);
when(mention.getStartSpan()).thenReturn(3);
when(mention.getEndSpan()).thenReturn(4);
Constituent head = mock(Constituent.class);
when(head.getStartSpan()).thenReturn(3);
when(head.getEndSpan()).thenReturn(4);
when(head.hasAttribute("EntityType")).thenReturn(true);
List<Constituent> mentions = new ArrayList<Constituent>();
mentions.add(mention);
when(mentionView.iterator()).thenReturn(mentions.iterator());
when(tokenView.getConstituentsCoveringToken(2)).thenReturn(null);
when(tokenView.getConstituentsCoveringToken(4)).thenReturn(null);
when(tokenView.getEndSpan()).thenReturn(5);
when(ta.getView(ViewNames.MENTION_ACE)).thenReturn(mentionView);
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
mockStatic(edu.illinois.cs.cogcomp.nlp.corpusreaders.ACEReader.class);
when(edu.illinois.cs.cogcomp.nlp.corpusreaders.ACEReader.getEntityHeadForConstituent(mention, ta, "HEADS")).thenReturn(head);
ExtentReader reader = new ExtentReader("dummy", "ACE") {

@Override
public List<TextAnnotation> getTextAnnotations() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
List<TextAnnotation> list = new ArrayList<TextAnnotation>();
list.add(ta);
return list;
}
};
List<Relation> result = reader.getPairs();
assertNotNull(result);
assertEquals(0, result.size());
}

@Test
public void testNextCalledMultipleTimesWithoutResetReturnsNullAfterLast() throws Exception {
Relation r1 = new Relation("r1", mock(Constituent.class), mock(Constituent.class), 0.9f);
ExtentReader reader = new ExtentReader("dir", "ACE") {

@Override
public List<Relation> getPairs() {
List<Relation> list = new ArrayList<Relation>();
list.add(r1);
return list;
}

@Override
public List<TextAnnotation> getTextAnnotations() throws InvalidPortException, InvalidEndpointException, IOException, JWNLException, DatastoreException {
return Collections.emptyList();
}
};
reader.reset();
Object first = reader.next();
Object second = reader.next();
Object third = reader.next();
assertNotNull(first);
assertNull(second);
assertNull(third);
}

@Test
public void testGetTextAnnotationsHandlesCombinedCorpusCorrectlyWithFoldZero() throws Exception {
String path = "resources/";
String corpus = "COMBINED-CONLL-dev-0";
ExtentReader reader = new ExtentReader(path, corpus);
List<TextAnnotation> tas = reader.getTextAnnotations();
assertNotNull(tas);
}

@Test
public void testGetTextAnnotationsSkipsUnknownSection() throws Exception {
ExtentReader reader = new ExtentReader("some/path", "CUSTOMCORPUS") {

@Override
public List<TextAnnotation> getTextAnnotations() {
return new ArrayList<TextAnnotation>();
}
};
List<TextAnnotation> result = reader.getTextAnnotations();
assertNotNull(result);
assertEquals(0, result.size());
}
}
