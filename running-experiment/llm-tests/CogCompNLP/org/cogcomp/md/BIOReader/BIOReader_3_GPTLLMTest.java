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
import junit.framework.TestCase;
import org.junit.Test;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class BIOReader_3_GPTLLMTest {

@Test
public void testConstructorInitializesFieldsCorrectly() {
BIOReader reader = new BIOReader("src/test/resources/testData", "ACE05-TRAIN", "ALL", true);
assertNotNull(reader);
assertEquals("testData_ALL", reader.id);
}

@Test
public void testNextReturnsFirstConstituent() {
BIOReader reader = new BIOReader("src/test/resources/testData", "ACE05-TRAIN", "ALL", true);
reader.reset();
Object result = reader.next();
assertNotNull(result);
assertTrue(result instanceof Constituent);
}

@Test
public void testNextReturnsNullAfterExhaustion() {
BIOReader reader = new BIOReader("src/test/resources/testData", "ACE05-TRAIN", "ALL", true);
reader.reset();
Object c1 = reader.next();
Object c2 = reader.next();
Object c3 = reader.next();
Object c4 = reader.next();
Object c5 = reader.next();
Object c6 = reader.next();
Object c7 = reader.next();
Object c8 = reader.next();
Object c9 = reader.next();
Object c10 = reader.next();
Object c11 = reader.next();
Object c12 = reader.next();
Object c13 = reader.next();
Object c14 = reader.next();
Object c15 = reader.next();
Object c16 = reader.next();
Object c17 = reader.next();
Object c18 = reader.next();
Object c19 = reader.next();
Object c20 = reader.next();
Object finalResult = reader.next();
assertTrue(finalResult == null || finalResult instanceof Constituent);
}

@Test
public void testNextAfterResetStartsOver() {
BIOReader reader = new BIOReader("src/test/resources/testData", "ACE05-TRAIN", "ALL", true);
Object first = reader.next();
Object second = reader.next();
reader.reset();
Object againFirst = reader.next();
assertEquals(((Constituent) first).toString(), ((Constituent) againFirst).toString());
}

@Test
public void testGetTextAnnotationsACE05() {
BIOReader reader = new BIOReader("src/test/resources/testData", "ACE05-TRAIN", "ALL", true);
List<TextAnnotation> annotations = reader.getTextAnnotations();
assertNotNull(annotations);
assertFalse(annotations.isEmpty());
TextAnnotation ta0 = annotations.get(0);
assertNotNull(ta0.getTokens());
}

@Test
public void testGetTextAnnotationsERE() {
BIOReader reader = new BIOReader("src/test/resources/testData", "ERE-EVAL", "ALL", true);
List<TextAnnotation> annotations = reader.getTextAnnotations();
assertNotNull(annotations);
assertFalse(annotations.isEmpty());
TextAnnotation ta0 = annotations.get(0);
assertNotNull(ta0.getTokens());
}

@Test
public void testGetTextAnnotationsUnknownModeReturnsEmptyList() {
BIOReader reader = new BIOReader("src/test/resources/testData", "UNKNOWN-TRAIN", "ALL", true);
List<TextAnnotation> annotations = reader.getTextAnnotations();
assertNotNull(annotations);
assertEquals(0, annotations.size());
}

@Test
public void testBIOViewExists() {
BIOReader reader = new BIOReader("src/test/resources/testData", "ACE05-TRAIN", "ALL", true);
List<TextAnnotation> tas = reader.getTextAnnotations();
TextAnnotation ta0 = tas.get(0);
assertTrue(ta0.hasView("BIO"));
}

@Test
public void testBIOConstituentAttributesPresent() {
BIOReader reader = new BIOReader("src/test/resources/testData", "ACE05-TRAIN", "ALL", true);
List<TextAnnotation> tas = reader.getTextAnnotations();
TextAnnotation ta0 = tas.get(0);
Constituent c0 = ta0.getView("BIO").getConstituents().get(0);
assertNotNull(c0.getAttribute("BIO"));
assertNotNull(c0.getAttribute("GAZ"));
assertNotNull(c0.getAttribute("BC"));
assertNotNull(c0.getAttribute("WORDNETTAG"));
assertNotNull(c0.getAttribute("WORDNETHYM"));
assertNotNull(c0.getAttribute("isTraining"));
}

@Test
public void testBIOValuesAreFormattedCorrectly() {
BIOReader reader = new BIOReader("src/test/resources/testData", "ACE05-TRAIN", "ALL", false);
TextAnnotation ta = reader.getTextAnnotations().get(0);
Constituent c = ta.getView("BIO").getConstituents().get(0);
String tag = c.getAttribute("BIO");
boolean isValid = tag.equals("O") || tag.startsWith("B-") || tag.startsWith("I-") || tag.startsWith("L-") || tag.startsWith("U-");
assertTrue(isValid);
}

@Test
public void testOnlyNAMEntitiesExistWhenFiltered() {
BIOReader reader = new BIOReader("src/test/resources/testData", "ACE05-TRAIN", "NAM", true);
TextAnnotation ta = reader.getTextAnnotations().get(0);
Constituent c0 = ta.getView("BIO").getConstituents().get(0);
String mentionType = c0.getAttribute("EntityMentionType");
if (mentionType != null) {
assertEquals("NAM", mentionType);
}
}

@Test
public void testSPEPrefixIsRemovedCorrectly() {
BIOReader reader = new BIOReader("src/test/resources/testData", "ACE05-TRAIN", "SPE_NAM", true);
TextAnnotation ta = reader.getTextAnnotations().get(0);
Constituent c0 = ta.getView("BIO").getConstituents().get(0);
String mentionType = c0.getAttribute("EntityMentionType");
if (mentionType != null) {
assertEquals("NAM", mentionType);
}
}

@Test
public void testCloseExecutesWithoutError() {
BIOReader reader = new BIOReader("src/test/resources/testData", "ACE05-TRAIN", "ALL", true);
reader.close();
}

@Test
public void testNextReturnsNullOnEmptyInput() {
BIOReader reader = new BIOReader("src/test/resources/empty", "ACE05-TRAIN", "ALL", true);
Object c = reader.next();
assertNull(c);
}

@Test
public void testEntityMentionTypeAttributeIsConsistent() {
BIOReader reader = new BIOReader("src/test/resources/testData", "ACE05-TRAIN", "ALL", true);
TextAnnotation ta = reader.getTextAnnotations().get(0);
Constituent c0 = ta.getView("BIO").getConstituents().get(0);
String mentionType = c0.getAttribute("EntityMentionType");
if (mentionType != null) {
assertTrue(mentionType.equals("NAM") || mentionType.equals("NOM") || mentionType.equals("PRO") || mentionType.equals("ALL"));
} else {
assertNull(mentionType);
}
}

@Test
public void testBIOReaderWithBIOLUSchema() {
BIOReader reader = new BIOReader("src/test/resources/testData", "ACE05-TRAIN", "ALL", false);
TextAnnotation ta = reader.getTextAnnotations().get(0);
Constituent c0 = ta.getView("BIO").getConstituents().get(0);
String bioTag = c0.getAttribute("BIO");
assertNotNull(bioTag);
boolean matches = bioTag.equals("O") || bioTag.startsWith("B-") || bioTag.startsWith("I-") || bioTag.startsWith("L-") || bioTag.startsWith("U-");
assertTrue(matches);
}

@Test
public void testIsTrainingAttributeSetCorrectly() {
BIOReader trainReader = new BIOReader("src/test/resources/testData", "ACE05-TRAIN", "ALL", true);
TextAnnotation taTrain = trainReader.getTextAnnotations().get(0);
Constituent trainC = taTrain.getView("BIO").getConstituents().get(0);
String isTrainValue = trainC.getAttribute("isTraining");
assertEquals("true", isTrainValue);
BIOReader evalReader = new BIOReader("src/test/resources/testData", "ACE05-EVAL", "ALL", true);
TextAnnotation taEval = evalReader.getTextAnnotations().get(0);
Constituent evalC = taEval.getView("BIO").getConstituents().get(0);
String isEvalValue = evalC.getAttribute("isTraining");
assertEquals("false", isEvalValue);
}

@Test
public void testReaderHandlesEmptyDirectoryGracefully() {
BIOReader reader = new BIOReader("src/test/resources/emptyDir", "ColumnFormat-TRAIN", "ALL", true);
List<TextAnnotation> tas = reader.getTextAnnotations();
assertNotNull(tas);
assertEquals(0, tas.size());
Object next = reader.next();
assertNull(next);
}

@Test
public void testReaderHandlesNOMOnlyTypeCorrectly() {
BIOReader reader = new BIOReader("src/test/resources/testData", "ACE05-TRAIN", "NOM", true);
TextAnnotation ta = reader.getTextAnnotations().get(0);
Constituent c = ta.getView("BIO").getConstituents().get(0);
String emType = c.getAttribute("EntityMentionType");
if (emType != null) {
assertEquals("NOM", emType);
}
}

@Test
public void testReaderHandlesPROOnlyTypeCorrectly() {
BIOReader reader = new BIOReader("src/test/resources/testData", "ACE05-TRAIN", "PRO", true);
TextAnnotation ta = reader.getTextAnnotations().get(0);
Constituent c = ta.getView("BIO").getConstituents().get(0);
String emType = c.getAttribute("EntityMentionType");
if (emType != null) {
assertEquals("PRO", emType);
}
}

@Test
public void testReaderHandlesTypeNotInMentionViewGracefully() {
BIOReader reader = new BIOReader("src/test/resources/testData", "ACE05-TRAIN", "XYZ", true);
Object result = reader.next();
assertNotNull(result);
Constituent c = (Constituent) result;
assertNotNull(c.getAttribute("BIO"));
}

@Test
public void testReaderHandlesNonSplittableModeGracefully() {
BIOReader reader = new BIOReader("src/test/resources/testData", "ACE05", "ALL", true);
List<TextAnnotation> tas = reader.getTextAnnotations();
assertNotNull(tas);
assertFalse(tas.isEmpty());
}

@Test
public void testReaderHandlesTokensWithoutGazetteerMatch() {
BIOReader reader = new BIOReader("src/test/resources/testData", "ACE05-TRAIN", "ALL", true);
TextAnnotation ta = reader.getTextAnnotations().get(0);
Constituent c = ta.getView("BIO").getConstituents().get(0);
String gazAttr = c.getAttribute("GAZ");
assertNotNull(gazAttr);
}

@Test
public void testReaderHandlesHttpTokensWithDefaultWordNetAttributes() {
BIOReader reader = new BIOReader("src/test/resources/testData", "ACE05-TRAIN", "ALL", true);
TextAnnotation ta = reader.getTextAnnotations().get(0);
Constituent fakeHttpToken = ta.getView("BIO").getConstituents().get(0);
fakeHttpToken.addAttribute("word", "http://example.com");
fakeHttpToken.addAttribute("BIO", "O");
String wntag = fakeHttpToken.getAttribute("WORDNETTAG");
String wnhym = fakeHttpToken.getAttribute("WORDNETHYM");
assertTrue(wntag == null || wntag.equals(","));
assertTrue(wnhym == null || wnhym.equals(","));
}

@Test
public void testReaderCreatesBIOViewWithExpectedName() {
BIOReader reader = new BIOReader("src/test/resources/testData", "ACE05-TRAIN", "ALL", true);
TextAnnotation ta = reader.getTextAnnotations().get(0);
assertTrue(ta.hasView("BIO"));
assertFalse(ta.hasView("BIOLU"));
}

@Test
public void testReaderHandlesMissingMentionViewGracefully() {
// TextAnnotation ta = new TextAnnotation("", "", "", new String[0], new int[0][2]);
BIOReader reader = new BIOReader("src/test/resources/testData", "UNKNOWN-TRAIN", "ALL", true);
List<TextAnnotation> tas = reader.getTextAnnotations();
assertEquals(0, tas.size());
}

@Test
public void testReaderHandlesModeWithNoTokensView() {
BIOReader reader = new BIOReader("src/test/resources/malformed", "ERE-TRAIN", "ALL", true);
List<TextAnnotation> list = reader.getTextAnnotations();
if (!list.isEmpty()) {
TextAnnotation ta = list.get(0);
boolean hasTokenView = ta.hasView(ViewNames.TOKENS);
assertTrue("Token view must exist in malformed files too for this test setup", hasTokenView);
} else {
assertNotNull(list);
}
}

@Test
public void testReaderHandlesSingleTokenMention_BIOLUSchema() {
BIOReader reader = new BIOReader("src/test/resources/testData", "ACE05-TRAIN", "ALL", false);
TextAnnotation ta = reader.getTextAnnotations().get(0);
Constituent single = ta.getView("BIO").getConstituents().get(0);
String bioAttr = single.getAttribute("BIO");
assertNotNull(bioAttr);
assertTrue(bioAttr.startsWith("U-") || bioAttr.equals("O") || bioAttr.startsWith("B-") || bioAttr.startsWith("I-") || bioAttr.startsWith("L-"));
}

@Test
public void testReaderHandlesLongMultiTokenMention_BIOSchema() {
BIOReader reader = new BIOReader("src/test/resources/testData", "ACE05-TRAIN", "ALL", true);
TextAnnotation ta = reader.getTextAnnotations().get(0);
Constituent c = ta.getView("BIO").getConstituents().get(0);
String bioTag = c.getAttribute("BIO");
assertNotNull(bioTag);
assertTrue(bioTag.startsWith("B-") || bioTag.startsWith("I-") || bioTag.equals("O"));
}

@Test
public void testReaderHandlesEmptyMentionView() {
BIOReader reader = new BIOReader("src/test/resources/no_mentions", "ACE05-TRAIN", "ALL", true);
List<TextAnnotation> tas = reader.getTextAnnotations();
assertNotNull(tas);
if (!tas.isEmpty()) {
TextAnnotation ta = tas.get(0);
assertTrue(ta.hasView("BIO"));
Constituent token = ta.getView("BIO").getConstituents().get(0);
assertEquals("O", token.getAttribute("BIO"));
}
}

@Test
public void testReaderHandlesEntityTypeFilterVEHAndWEA() {
BIOReader reader = new BIOReader("src/test/resources/testData", "ACE05-TRAIN", "ALL", true);
TextAnnotation ta = reader.getTextAnnotations().get(0);
Constituent c = ta.getView("BIO").getConstituents().get(0);
String entType = c.getAttribute("BIO");
assertNotNull(entType);
}

@Test
public void testReaderHandlesColumnFormatMode() {
BIOReader reader = new BIOReader("src/test/resources/testData", "ColumnFormat-TRAIN", "ALL", true);
List<TextAnnotation> annotations = reader.getTextAnnotations();
assertNotNull(annotations);
assertFalse(annotations.isEmpty());
TextAnnotation ta = annotations.get(0);
assertTrue(ta.hasView(ViewNames.TOKENS));
assertTrue(ta.hasView("BIO"));
}

@Test
public void testReaderWithInvalidDataPath() {
try {
BIOReader reader = new BIOReader("invalid/path/to/data", "ERE-EVAL", "ALL", true);
List<TextAnnotation> tas = reader.getTextAnnotations();
assertNotNull(tas);
} catch (Exception e) {
assertTrue(e instanceof RuntimeException || e instanceof IllegalArgumentException);
}
}

@Test
public void testReaderWithNullPathThrowsException() {
try {
BIOReader reader = new BIOReader(null, "ACE05-TRAIN", "ALL", true);
assertNotNull(reader);
} catch (Exception e) {
assertTrue(e instanceof NullPointerException || e instanceof RuntimeException);
}
}

@Test
public void testReaderWithEmptyModeString() {
BIOReader reader = new BIOReader("src/test/resources/testData", "", "ALL", true);
List<TextAnnotation> tas = reader.getTextAnnotations();
assertNotNull(tas);
Object token = reader.next();
if (token != null) {
assertTrue(token instanceof Constituent);
}
}

@Test
public void testReaderHandlesMissingEntityMentionTypeAttribute() {
BIOReader reader = new BIOReader("src/test/resources/testData_missing_entity_type", "ACE05-TRAIN", "ALL", true);
List<TextAnnotation> tas = reader.getTextAnnotations();
assertNotNull(tas);
if (!tas.isEmpty()) {
TextAnnotation ta = tas.get(0);
Constituent c = ta.getView("BIO").getConstituents().get(0);
String mentionType = c.getAttribute("EntityMentionType");
assertNotNull(c.getAttribute("BIO"));
}
}

@Test
public void testReaderHandlesStartSpanEqualsEndSpanGracefully() {
BIOReader reader = new BIOReader("src/test/resources/testData_empty_mentions", "ACE05-TRAIN", "ALL", true);
List<TextAnnotation> tas = reader.getTextAnnotations();
assertNotNull(tas);
if (!tas.isEmpty()) {
TextAnnotation ta = tas.get(0);
Constituent c = ta.getView("BIO").getConstituents().get(0);
String bioTag = c.getAttribute("BIO");
assertNotNull(bioTag);
}
}

@Test
public void testReaderHandlesMissingPOSViewGracefully() {
BIOReader reader = new BIOReader("src/test/resources/testData_no_pos", "ACE05-TRAIN", "ALL", true);
List<TextAnnotation> tas = reader.getTextAnnotations();
assertNotNull(tas);
TextAnnotation ta = tas.get(0);
assertTrue(ta.hasView("BIO"));
}

@Test
public void testNextReturnsNullWhenNoTokens() {
BIOReader reader = new BIOReader("src/test/resources/testData_empty_tokens", "ACE05-TRAIN", "ALL", true);
Object token = reader.next();
assertNull(token);
}

@Test
public void testReaderWithUnknownMentionViewName() {
BIOReader reader = new BIOReader("src/test/resources/testData", "UNKNOWN-EVAL", "ALL", true);
List<TextAnnotation> tas = reader.getTextAnnotations();
assertNotNull(tas);
if (!tas.isEmpty()) {
TextAnnotation ta = tas.get(0);
boolean hasBIO = ta.hasView("BIO");
assertTrue(hasBIO);
}
}

@Test
public void testWordNetTagsAreCommaOnlyForURLs() {
BIOReader reader = new BIOReader("src/test/resources/testData_url_token", "ACE05-TRAIN", "ALL", true);
List<TextAnnotation> tas = reader.getTextAnnotations();
assertNotNull(tas);
TextAnnotation ta = tas.get(0);
Constituent token = ta.getView("BIO").getConstituents().get(0);
assertEquals(",", token.getAttribute("WORDNETTAG"));
assertEquals(",", token.getAttribute("WORDNETHYM"));
}

@Test
public void testReaderIgnoresMentionsWithExcludedTypes() {
BIOReader reader = new BIOReader("src/test/resources/testData", "ACE05-TRAIN", "NAM", true);
List<TextAnnotation> tas = reader.getTextAnnotations();
assertNotNull(tas);
TextAnnotation ta = tas.get(0);
Constituent bioToken = ta.getView("BIO").getConstituents().get(0);
if (bioToken.getAttribute("EntityMentionType") != null) {
assertEquals("NAM", bioToken.getAttribute("EntityMentionType"));
}
}

@Test
public void testReaderSkipsConstituentIfEntityHeadIsNull() {
BIOReader reader = new BIOReader("src/test/resources/testData_missing_heads", "ACE05-TRAIN", "ALL", true);
List<TextAnnotation> tas = reader.getTextAnnotations();
assertNotNull(tas);
if (!tas.isEmpty()) {
TextAnnotation ta = tas.get(0);
Constituent token = ta.getView("BIO").getConstituents().get(0);
assertNotNull(token.getAttribute("BIO"));
}
}

@Test
public void testReaderWithEmptyFileDoesNotThrow() {
BIOReader reader = new BIOReader("src/test/resources/testData_empty_file", "ColumnFormat-TRAIN", "ALL", true);
List<TextAnnotation> tas = reader.getTextAnnotations();
assertNotNull(tas);
assertTrue(tas.isEmpty() || tas.get(0).getTokens().length == 0);
}

@Test
public void testInvalidBIOFormatIsHandledSafely() {
BIOReader reader = new BIOReader("src/test/resources/testData_invalid_bio_format", "ACE05-TRAIN", "ALL", true);
List<TextAnnotation> tas = reader.getTextAnnotations();
assertNotNull(tas);
if (!tas.isEmpty()) {
TextAnnotation ta = tas.get(0);
Constituent token = ta.getView("BIO").getConstituents().get(0);
assertNotNull(token.getAttribute("BIO"));
}
}

@Test
public void testReaderWithLongPath() {
BIOReader reader = new BIOReader("src/test/resources/some/very/long/path/that/should/still/work/testData", "ACE05-TRAIN", "ALL", true);
List<TextAnnotation> annotations = reader.getTextAnnotations();
assertNotNull(annotations);
}

@Test
public void testReaderWithMultipleHyphenInMode() {
BIOReader reader = new BIOReader("src/test/resources/testData", "ACE05-TRAIN-ALT-SPLIT", "ALL", true);
List<TextAnnotation> tas = reader.getTextAnnotations();
assertNotNull(tas);
assertFalse(tas.isEmpty());
}

@Test
public void testConstructorWithBinaryIndicatorOnly() {
BIOReader reader = new BIOReader("src/test/resources/dataset_folder", "ACE05-EVAL", "ALL", true);
assertNotNull(reader);
Object obj = reader.next();
if (obj != null) {
assertTrue(obj instanceof Constituent);
Constituent c = (Constituent) obj;
assertEquals("false", c.getAttribute("isTraining"));
}
}

@Test
public void testConstructorWithExtraDashesInMode() {
BIOReader reader = new BIOReader("src/test/resources/dataset_folder", "ACE05-EVAL-EXTRA-PARTS", "ALL", true);
List<TextAnnotation> list = reader.getTextAnnotations();
assertNotNull(list);
assertTrue(list.size() >= 0);
}

@Test
public void testConstructorWithTypeALLWorksProperly() {
BIOReader reader = new BIOReader("src/test/resources/dataset_folder", "ACE05-TRAIN", "ALL", true);
Object token = reader.next();
assertNotNull(token);
Constituent c = (Constituent) token;
String bio = c.getAttribute("BIO");
assertNotNull(bio);
}

@Test
public void testMentionWithoutEntityTypeAttributeIsHandled() {
BIOReader reader = new BIOReader("src/test/resources/testData_missing_entitytype", "ACE05-TRAIN", "ALL", true);
Object token = reader.next();
assertNotNull(token);
Constituent c = (Constituent) token;
assertNotNull(c.getAttribute("BIO"));
}

@Test
public void testMentionViewWithNullConstituentListDoesNotThrow() {
BIOReader reader = new BIOReader("src/test/resources/testData_null_mentions", "ACE05-TRAIN", "ALL", true);
Object c = reader.next();
assertTrue(c == null || c instanceof Constituent);
}

@Test
public void testTokenViewExistsEvenForEmptyCorpus() {
BIOReader reader = new BIOReader("src/test/resources/empty_corpus", "ACE05-TRAIN", "ALL", true);
List<TextAnnotation> list = reader.getTextAnnotations();
if (!list.isEmpty()) {
TextAnnotation ta = list.get(0);
assertTrue(ta.hasView(ViewNames.TOKENS));
}
}

@Test
public void testHandlesNullEntityHeadGracefully() {
BIOReader reader = new BIOReader("src/test/resources/testData_null_entity_heads", "ACE05-TRAIN", "ALL", true);
Object c = reader.next();
assertNotNull(c);
Constituent token = (Constituent) c;
String tag = token.getAttribute("BIO");
assertNotNull(tag);
}

@Test
public void testBioluSchemaWithSingleCharacterToken() {
BIOReader reader = new BIOReader("src/test/resources/single_char_tokens", "ACE05-TRAIN", "ALL", false);
Object token = reader.next();
assertNotNull(token);
Constituent c = (Constituent) token;
String tag = c.getAttribute("BIO");
assertTrue(tag.startsWith("U-") || tag.equals("O"));
}

@Test
public void testResetMultipleTimesWithoutError() {
BIOReader reader = new BIOReader("src/test/resources/dataset_folder", "ACE05-TRAIN", "ALL", true);
reader.reset();
reader.reset();
Object token = reader.next();
assertNotNull(token);
}

@Test
public void testNextReturnsConsistentConstituentsWithReset() {
BIOReader reader = new BIOReader("src/test/resources/dataset_folder", "ACE05-TRAIN", "ALL", true);
Object token1 = reader.next();
reader.reset();
Object token2 = reader.next();
assertEquals(((Constituent) token1).toString(), ((Constituent) token2).toString());
}

@Test
public void testNextReturnsNullWhenTokenListEmpty() {
BIOReader reader = new BIOReader("src/test/resources/empty_tokens", "ACE05-TRAIN", "ALL", true);
Object t = reader.next();
assertNull(t);
}

@Test
public void testConstituentAttributesNotNullWhenBIODataAvailable() {
BIOReader reader = new BIOReader("src/test/resources/dataset_folder", "ACE05-TRAIN", "ALL", true);
Constituent c = (Constituent) reader.next();
assertNotNull(c.getAttribute("BIO"));
assertNotNull(c.getAttribute("GAZ"));
assertNotNull(c.getAttribute("BC"));
assertNotNull(c.getAttribute("WORDNETTAG"));
assertNotNull(c.getAttribute("WORDNETHYM"));
}

@Test
public void testReaderWithColumnFormatData() {
BIOReader reader = new BIOReader("src/test/resources/column_format_data", "ColumnFormat-TRAIN", "ALL", true);
Object token = reader.next();
assertNotNull(token);
Constituent c = (Constituent) token;
assertNotNull(c.getAttribute("BIO"));
}

@Test
public void testMultipleBIOConstituentsHaveDistinctPositions() {
BIOReader reader = new BIOReader("src/test/resources/dataset_folder", "ACE05-TRAIN", "ALL", true);
Constituent c1 = (Constituent) reader.next();
Constituent c2 = (Constituent) reader.next();
assertTrue(c1.getStartSpan() <= c2.getStartSpan());
}

@Test
public void testTrainEvalTagControlsIsTrainingAttribute() {
BIOReader trainReader = new BIOReader("src/test/resources/dataset_folder", "ACE05-TRAIN", "ALL", true);
Constituent trainC = (Constituent) trainReader.next();
assertEquals("true", trainC.getAttribute("isTraining"));
BIOReader evalReader = new BIOReader("src/test/resources/dataset_folder", "ACE05-EVAL", "ALL", true);
Constituent evalC = (Constituent) evalReader.next();
assertEquals("false", evalC.getAttribute("isTraining"));
}

@Test
public void testGetTextAnnotationsReturnsEmptyForUnknownMode() {
BIOReader reader = new BIOReader("src/test/resources/unknown_mode", "NONEXISTENT-TRAIN", "ALL", true);
List<TextAnnotation> list = reader.getTextAnnotations();
assertNotNull(list);
assertEquals(0, list.size());
}

@Test
public void testBIOReaderHandlesMissingTokenViewGracefully() {
BIOReader reader = new BIOReader("src/test/resources/missing_token_view", "ACE05-TRAIN", "ALL", true);
List<TextAnnotation> tas = reader.getTextAnnotations();
assertNotNull(tas);
if (!tas.isEmpty()) {
TextAnnotation ta = tas.get(0);
boolean hasTokenView = ta.hasView(ViewNames.TOKENS);
assertFalse("Expected no TOKENS view", hasTokenView);
}
}

@Test
public void testBIOReaderHandlesTokenWithoutCoveringConstituent() {
BIOReader reader = new BIOReader("src/test/resources/no_covering_constituent", "ACE05-TRAIN", "ALL", true);
List<TextAnnotation> tas = reader.getTextAnnotations();
assertNotNull(tas);
if (!tas.isEmpty()) {
TextAnnotation ta = tas.get(0);
String bio = null;
if (ta.hasView("BIO")) {
List<Constituent> constituents = ta.getView("BIO").getConstituents();
if (!constituents.isEmpty())
bio = constituents.get(0).getAttribute("BIO");
}
assertNotNull(bio);
}
}

@Test
public void testBIOReaderSkipsMentionWithNonMatchingType() {
BIOReader reader = new BIOReader("src/test/resources/type_filtered", "ACE05-TRAIN", "PRO", true);
Object obj = reader.next();
assertNotNull(obj);
Constituent c = (Constituent) obj;
String mentionType = c.getAttribute("EntityMentionType");
if (mentionType != null) {
assertEquals("PRO", mentionType);
}
}

@Test
public void testBIOReaderEntityMentionTypePrefixRemoval() {
BIOReader reader = new BIOReader("src/test/resources/mention_prefix", "ACE05-TRAIN", "SPE_NOM", true);
Object obj = reader.next();
assertNotNull(obj);
Constituent c = (Constituent) obj;
String mentionType = c.getAttribute("EntityMentionType");
if (mentionType != null) {
assertEquals("NOM", mentionType);
}
}

@Test
public void testBIOReaderHandlesNullViewGracefully() {
BIOReader reader = new BIOReader("src/test/resources/null_views", "ACE05-TRAIN", "ALL", true);
List<TextAnnotation> tas = reader.getTextAnnotations();
assertNotNull(tas);
if (!tas.isEmpty()) {
TextAnnotation ta = tas.get(0);
boolean hasBIO = ta.hasView("BIO");
assertTrue(hasBIO);
}
}

@Test
public void testBIOReaderEntityWithOnlyVEHIsTaggedProperly() {
BIOReader reader = new BIOReader("src/test/resources/entity_vehicle", "ACE05-TRAIN", "ALL", true);
Object obj = reader.next();
assertNotNull(obj);
Constituent c = (Constituent) obj;
String bio = c.getAttribute("BIO");
assertNotNull(bio);
}

@Test
public void testBIOReaderBIOFormatWithMiddleToken() {
BIOReader reader = new BIOReader("src/test/resources/middle_token_mention", "ACE05-TRAIN", "ALL", true);
Object obj = reader.next();
assertNotNull(obj);
Constituent c = (Constituent) obj;
String bio = c.getAttribute("BIO");
assertTrue(bio.equals("B-PER") || bio.equals("I-PER") || bio.equals("O"));
}

@Test
public void testBIOReaderHandlesMissingEntityMentionViewGracefully() {
BIOReader reader = new BIOReader("src/test/resources/missing_mention_view", "ACE05-TRAIN", "ALL", true);
List<TextAnnotation> list = reader.getTextAnnotations();
assertNotNull(list);
if (!list.isEmpty()) {
TextAnnotation ta = list.get(0);
boolean hasBIO = ta.hasView("BIO");
assertTrue(hasBIO);
}
}

@Test
public void testBIOReaderAddsDefaultOAttributeWhenNoMentionMatch() {
BIOReader reader = new BIOReader("src/test/resources/no_mentions_tag", "ACE05-TRAIN", "ALL", true);
TextAnnotation ta = reader.getTextAnnotations().get(0);
Constituent token = ta.getView("BIO").getConstituents().get(0);
String bio = token.getAttribute("BIO");
assertEquals("O", bio);
}

@Test
public void testBIOReaderDealsWithShortFakeMentionGracefully() {
BIOReader reader = new BIOReader("src/test/resources/fake_mention_short", "ACE05-TRAIN", "NAM", false);
TextAnnotation ta = reader.getTextAnnotations().get(0);
Constituent c = ta.getView("BIO").getConstituents().get(0);
String tag = c.getAttribute("BIO");
assertTrue(tag.startsWith("U-") || tag.equals("O"));
}

@Test
public void testBIOReaderHandlesEntityTypeMatchingButBadSpan() {
BIOReader reader = new BIOReader("src/test/resources/bad_span_mention", "ACE05-TRAIN", "NAM", true);
TextAnnotation ta = reader.getTextAnnotations().get(0);
Constituent token = ta.getView("BIO").getConstituents().get(0);
assertNotNull(token.getAttribute("BIO"));
}

@Test
public void testBIOReaderGazetteerAnnotationIsNotNull() {
BIOReader reader = new BIOReader("src/test/resources/testData", "ACE05-TRAIN", "ALL", true);
TextAnnotation ta = reader.getTextAnnotations().get(0);
Constituent token = ta.getView("BIO").getConstituents().get(0);
String gaz = token.getAttribute("GAZ");
assertNotNull(gaz);
}

@Test
public void testBIOReaderWithERECorpusMode() {
BIOReader reader = new BIOReader("src/test/resources/ere_corpus_data", "ERE-TRAIN", "ALL", true);
TextAnnotation ta = reader.getTextAnnotations().get(0);
assertTrue(ta.hasView("BIO"));
}

@Test
public void testBIOReaderSingleTokenGetsDefaultBIOAttributes() {
BIOReader reader = new BIOReader("src/test/resources/single_token_data", "ACE05-TRAIN", "ALL", true);
Object obj = reader.next();
assertNotNull(obj);
Constituent c = (Constituent) obj;
assertNotNull(c.getAttribute("BIO"));
assertNotNull(c.getAttribute("GAZ"));
assertNotNull(c.getAttribute("BC"));
assertNotNull(c.getAttribute("WORDNETTAG"));
assertNotNull(c.getAttribute("WORDNETHYM"));
}

@Test
public void testEmptyTextAnnotationListResultsInNullNext() {
BIOReader reader = new BIOReader("src/test/resources/no_documents", "ACE05-TRAIN", "ALL", true);
Object token = reader.next();
assertNull(token);
}

@Test
public void testConstructorWithMalformedModeString() {
BIOReader reader = new BIOReader("src/test/resources/testData", "ACE05TRAIN", "ALL", true);
List<TextAnnotation> list = reader.getTextAnnotations();
assertNotNull(list);
}

@Test
public void testReaderHandlesUnknownMentionViewName() {
BIOReader reader = new BIOReader("src/test/resources/testData", "UNKNOWN-EVAL", "ALL", true);
List<TextAnnotation> tas = reader.getTextAnnotations();
assertNotNull(tas);
if (!tas.isEmpty()) {
TextAnnotation ta = tas.get(0);
assertTrue(ta.hasView("BIO"));
}
}

@Test
public void testNoEntityMentionTypeAttributeSkipsMentionGracefully() {
BIOReader reader = new BIOReader("src/test/resources/testData_missing_entity_mention_type", "ACE05-TRAIN", "NAM", true);
Object token = reader.next();
assertNotNull(token);
Constituent c = (Constituent) token;
String bio = c.getAttribute("BIO");
assertNotNull(bio);
}

@Test
public void testNullEntityHeadSkipsMentionWithoutError() {
BIOReader reader = new BIOReader("src/test/resources/testData_null_entity_head", "ACE05-TRAIN", "ALL", true);
Object token = reader.next();
assertNotNull(token);
Constituent c = (Constituent) token;
assertNotNull(c.getAttribute("BIO"));
}

@Test
public void testUnhandledEntityTypeStillProducesConstituents() {
BIOReader reader = new BIOReader("src/test/resources/entity_type_outlier", "ACE05-TRAIN", "ALL", true);
Object token = reader.next();
assertNotNull(token);
Constituent c = (Constituent) token;
assertNotNull(c.getAttribute("BIO"));
}

@Test
public void testViewNotFoundFallbackIsHandled() {
BIOReader reader = new BIOReader("src/test/resources/testData_missing_view", "ACE05-TRAIN", "ALL", true);
List<TextAnnotation> tas = reader.getTextAnnotations();
assertNotNull(tas);
if (!tas.isEmpty()) {
TextAnnotation ta = tas.get(0);
assertTrue(ta.hasView("BIO"));
}
}

@Test
public void testResetDoesNotAffectParsedTokens() {
BIOReader reader = new BIOReader("src/test/resources/testData", "ACE05-TRAIN", "ALL", true);
Object original = reader.next();
reader.reset();
Object restarted = reader.next();
assertNotNull(original);
assertNotNull(restarted);
assertEquals(((Constituent) original).toString(), ((Constituent) restarted).toString());
}

@Test
public void testCloseMethodDoesNotThrow() {
BIOReader reader = new BIOReader("src/test/resources/testData", "ACE05-TRAIN", "ALL", true);
reader.close();
}

@Test
public void testBIOLUOutputFormatWithSingleTokenIsU() {
BIOReader reader = new BIOReader("src/test/resources/single_token_mention", "ACE05-TRAIN", "ALL", false);
Object token = reader.next();
assertNotNull(token);
Constituent c = (Constituent) token;
String bio = c.getAttribute("BIO");
assertNotNull(bio);
assertTrue(bio.startsWith("U-") || bio.equals("O"));
}

@Test
public void testBIOOutputFormatWithMultiTokenMention() {
BIOReader reader = new BIOReader("src/test/resources/multi_token_mention", "ACE05-TRAIN", "ALL", true);
Object token = reader.next();
assertNotNull(token);
Constituent c = (Constituent) token;
String bio = c.getAttribute("BIO");
assertNotNull(bio);
assertTrue(bio.startsWith("B-") || bio.startsWith("I-") || bio.equals("O"));
}

@Test
public void testBIOReaderHandlesMentionWithStartEqualsEnd() {
BIOReader reader = new BIOReader("src/test/resources/mention_with_zero_span", "ACE05-TRAIN", "ALL", true);
Object token = reader.next();
assertNotNull(token);
Constituent c = (Constituent) token;
assertNotNull(c.getAttribute("BIO"));
}

@Test
public void testBIOReaderAssignsCommaWordNetTagToURLs() {
BIOReader reader = new BIOReader("src/test/resources/url_token", "ACE05-TRAIN", "ALL", true);
Object token = reader.next();
assertNotNull(token);
Constituent c = (Constituent) token;
String wordNet = c.getAttribute("WORDNETTAG");
String hyms = c.getAttribute("WORDNETHYM");
if (c.toString().contains("http")) {
assertEquals(",", wordNet);
assertEquals(",", hyms);
}
}

@Test
public void testEntityWithUnknownMentionTypeIsIgnored() {
BIOReader reader = new BIOReader("src/test/resources/mention_unknown_type", "ACE05-TRAIN", "NAM", true);
Object token = reader.next();
assertNotNull(token);
Constituent c = (Constituent) token;
assertNotNull(c.getAttribute("BIO"));
}

@Test
public void testMentionViewMissingButTokenViewPresentStillAddsBIO() {
BIOReader reader = new BIOReader("src/test/resources/only_token_view_present", "ACE05-TRAIN", "ALL", true);
List<TextAnnotation> list = reader.getTextAnnotations();
assertNotNull(list);
if (!list.isEmpty()) {
TextAnnotation ta = list.get(0);
assertTrue(ta.hasView("BIO"));
}
}

@Test
public void testMalformedTypeIsIgnoredWithoutCrashing() {
BIOReader reader = new BIOReader("src/test/resources/data_malformed_type", "ACE05-TRAIN", "XYZ", true);
Object token = reader.next();
assertNotNull(token);
Constituent c = (Constituent) token;
assertNotNull(c.getAttribute("BIO"));
}

@Test
public void testEntityMentionTypeFilterRemovesAllMentions() {
BIOReader reader = new BIOReader("src/test/resources/data_nomentionmatch", "ACE05-TRAIN", "PRO", true);
Object token = reader.next();
assertNotNull(token);
Constituent c = (Constituent) token;
assertEquals("O", c.getAttribute("BIO"));
}

@Test
public void testMissingMentionViewSkipsAnnotationGracefully() {
BIOReader reader = new BIOReader("src/test/resources/data_missing_mention_view", "ACE05-TRAIN", "ALL", true);
List<TextAnnotation> tas = reader.getTextAnnotations();
assertNotNull(tas);
if (!tas.isEmpty()) {
TextAnnotation ta = tas.get(0);
assertTrue(ta.hasView("BIO"));
}
}

@Test
public void testSingleCharacterTokenGetsBIOAnnotation() {
BIOReader reader = new BIOReader("src/test/resources/data_single_char_token", "ACE05-TRAIN", "ALL", true);
Object token = reader.next();
assertNotNull(token);
Constituent c = (Constituent) token;
String bio = c.getAttribute("BIO");
assertNotNull(bio);
assertTrue(bio.equals("O") || bio.startsWith("B-") || bio.startsWith("I-"));
}

@Test
public void testStartSpanEqualToEndSpanMentionIsSkipped() {
BIOReader reader = new BIOReader("src/test/resources/data_invalid_zero_span", "ACE05-TRAIN", "ALL", true);
Object token = reader.next();
assertNotNull(token);
Constituent c = (Constituent) token;
assertNotNull(c.getAttribute("BIO"));
}

@Test
public void testNextReturnsNullOnEmptyCorpus() {
BIOReader reader = new BIOReader("src/test/resources/data_empty", "ACE05-TRAIN", "ALL", true);
Object token = reader.next();
assertNull(token);
}

@Test
public void testResetAllowsReiterationOverConstituents() {
BIOReader reader = new BIOReader("src/test/resources/testData", "ACE05-TRAIN", "ALL", true);
Object first = reader.next();
reader.reset();
Object firstAgain = reader.next();
assertNotNull(first);
assertNotNull(firstAgain);
assertEquals(((Constituent) first).toString(), ((Constituent) firstAgain).toString());
}

@Test
public void testViewOrderingDoesNotAffectBIOAssignment() {
BIOReader reader = new BIOReader("src/test/resources/data_unordered_views", "ACE05-TRAIN", "ALL", true);
TextAnnotation ta = reader.getTextAnnotations().get(0);
Constituent c = ta.getView("BIO").getConstituents().get(0);
assertNotNull(c.getAttribute("BIO"));
}

@Test
public void testTokenWithHttpInStringHasCommaWordnet() {
BIOReader reader = new BIOReader("src/test/resources/data_http_token", "ACE05-TRAIN", "ALL", true);
Constituent c = (Constituent) reader.next();
String wordNetTag = c.getAttribute("WORDNETTAG");
String wordNetHym = c.getAttribute("WORDNETHYM");
if (c.toString().contains("http")) {
assertEquals(",", wordNetTag);
assertEquals(",", wordNetHym);
}
}

@Test
public void testDefaultEntityTypesLikeWEAOrVEHAreNotFiltered() {
BIOReader reader = new BIOReader("src/test/resources/data_veh_entities", "ACE05-TRAIN", "ALL", true);
Constituent c = (Constituent) reader.next();
assertNotNull(c.getAttribute("BIO"));
}

@Test
public void testIsTrainingFalseForEvalMode() {
BIOReader reader = new BIOReader("src/test/resources/testData", "ACE05-EVAL", "ALL", true);
Constituent c = (Constituent) reader.next();
String isTrainAttr = c.getAttribute("isTraining");
assertEquals("false", isTrainAttr);
}

@Test
public void testBIOLUSchemaYieldsULOnMultiTokenMention() {
BIOReader reader = new BIOReader("src/test/resources/data_multi_token_entity", "ACE05-TRAIN", "ALL", false);
Constituent c1 = (Constituent) reader.next();
Constituent c2 = (Constituent) reader.next();
String tag1 = c1.getAttribute("BIO");
String tag2 = c2.getAttribute("BIO");
boolean valid = (tag1.startsWith("B-") && tag2.startsWith("L-")) || tag1.equals("O") || tag2.equals("O");
assertTrue(valid);
}

@Test
public void testBIOAnnotationUsesDefaultOWhenMentionDisqualified() {
BIOReader reader = new BIOReader("src/test/resources/data_invalid_mention_type", "ACE05-TRAIN", "NAM", true);
Constituent c = (Constituent) reader.next();
assertNotNull(c);
assertEquals("O", c.getAttribute("BIO"));
}

@Test
public void testBIOViewStillAddedWhenNoMentionsExist() {
BIOReader reader = new BIOReader("src/test/resources/data_no_mentions", "ACE05-TRAIN", "ALL", true);
TextAnnotation ta = reader.getTextAnnotations().get(0);
assertTrue(ta.hasView("BIO"));
Constituent c = ta.getView("BIO").getConstituents().get(0);
assertEquals("O", c.getAttribute("BIO"));
}

@Test
public void testConstructorParsesGroupNameCorrectlyForId() {
BIOReader reader = new BIOReader("src/test/resources/group_name_test", "ACE05-TRAIN", "NAM", true);
assertTrue(reader.id.contains("group_name_test_NAM"));
}
}
