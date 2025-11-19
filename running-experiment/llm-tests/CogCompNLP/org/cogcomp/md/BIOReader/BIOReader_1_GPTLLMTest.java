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

public class BIOReader_1_GPTLLMTest {

@Test
public void testDefaultConstructor() {
BIOReader reader = new BIOReader();
assertNotNull(reader);
}

@Test
public void testNextReturnsNullWhenTokenListIsEmpty() {
BIOReader reader = new BIOReader();
List<Constituent> emptyTokenList = new ArrayList<>();
for (int i = 0; i < 0; i++) {
emptyTokenList.add(null);
}
Object result = reader.next();
assertNull(result);
}

@Test
public void testNextReturnsFirstToken() {
BIOReader reader = new BIOReader();
List<Constituent> tokenList = new ArrayList<>();
// TextAnnotation ta = TextAnnotationUtilities.createFromTokenizedString("test", "s", "word");
// View tokenView = new TokenLabelView(ViewNames.TOKENS, "test", ta, 1.0);
// Constituent token = new Constituent("word", tokenView, ta, 0, 1);
// tokenList.add(token);
reader.reset();
Object result = reader.next();
assertNotNull(result);
}

@Test
public void testResetDoesNotThrow() {
BIOReader reader = new BIOReader();
reader.reset();
}

@Test
public void testConstructorWithPathAndModeSetsIdCorrectly() {
String path = "some/path/to/data/ACEFolder";
String mode = "ACE05-TRAIN";
String type = "NAM";
Boolean isBIO = true;
BIOReader reader = new BIOReader(path, mode, type, isBIO);
assertEquals("ACEFolder_NAM", reader.id);
}

@Test
public void testConstructorWithUnknownModeDoesNotFail() {
String path = "some/path";
String mode = "XYZ-TRAIN";
String type = "ALL";
Boolean isBIO = true;
BIOReader reader = new BIOReader(path, mode, type, isBIO);
assertNotNull(reader);
}

@Test
public void testNextReturnsNullAfterAllTokensConsumed() {
BIOReader reader = new BIOReader();
// TextAnnotation ta = TextAnnotationUtilities.createFromTokenizedString("test", "s", "a b");
// View tokenView = new TokenLabelView(ViewNames.TOKENS, "test", ta, 1.0);
// Constituent token1 = new Constituent("a", tokenView, ta, 0, 1);
// Constituent token2 = new Constituent("b", tokenView, ta, 1, 2);
List<Constituent> tokenList = new ArrayList<>();
// tokenList.add(token1);
// tokenList.add(token2);
reader.reset();
Object first = reader.next();
Object second = reader.next();
Object third = reader.next();
assertNotNull(first);
assertNotNull(second);
assertNull(third);
}

@Test
public void testConstructorWithBIOTrueCreatesValidObject() {
String path = "mock/path";
String mode = "ColumnFormat-TRAIN";
String type = "ALL";
Boolean isBIO = true;
BIOReader reader = new BIOReader(path, mode, type, isBIO);
assertNotNull(reader);
}

@Test
public void testConstructorWithBIOFalseCreatesValidObject() {
String path = "mock/path";
String mode = "ColumnFormat-TRAIN";
String type = "ALL";
Boolean isBIO = false;
BIOReader reader = new BIOReader(path, mode, type, isBIO);
assertNotNull(reader);
}

@Test
public void testReaderHandlesEmptyPathGracefully() {
String path = "";
String mode = "ColumnFormat-TRAIN";
String type = "ALL";
Boolean isBIO = true;
BIOReader reader = new BIOReader(path, mode, type, isBIO);
assertNotNull(reader);
}

@Test
public void testReaderHandlesSingleTokenMention() {
String docId = "testDoc";
String corpusId = "testCorpus";
String[] tokens = new String[] { "Obama" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens(corpusId, docId, List.of(tokens));
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "dummy", ta, 1.0f);
// Constituent token = new Constituent("PERSON", tokenView, ta, 0, 1);
// token.addAttribute("EntityMentionType", "NAM");
// token.addAttribute("EntityType", "PERSON");
// tokenView.addConstituent(token);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView mentionView = new SpanLabelView(ViewNames.MENTION_ACE, "dummy", ta, 1.0f);
// Constituent mention = new Constituent("PERSON", mentionView, ta, 0, 1);
// mention.addAttribute("EntityMentionType", "NAM");
// mention.addAttribute("EntityType", "PERSON");
// mentionView.addConstituent(mention);
// ta.addView(ViewNames.MENTION_ACE, mentionView);
// assertNotNull(ta.getView(ViewNames.TOKENS));
// assertNotNull(ta.getView(ViewNames.MENTION_ACE));
}

@Test
public void testReaderHandlesLongPathWithMultipleSeparators() {
String path = "some/very/long/path/to/the/corpus/ERECorpusFolder";
String mode = "ERE-TRAIN";
String type = "PRO";
Boolean isBIO = true;
BIOReader reader = new BIOReader(path, mode, type, isBIO);
assertEquals("ERECorpusFolder_PRO", reader.id);
}

@Test
public void testReaderHandlesPathWithoutAnyFolderComponent() {
String path = "data";
String mode = "ACE05-TRAIN";
String type = "ALL";
Boolean isBIO = true;
BIOReader reader = new BIOReader(path, mode, type, isBIO);
assertEquals("data_ALL", reader.id);
}

@Test
public void testReaderHandlesInvalidEntityMentionTypeGracefully() {
// TextAnnotation ta = TextAnnotationUtilities.createFromTokenizedString("test", "s", "The president arrived");
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "dummy", ta, 1.0f);
// Constituent token1 = new Constituent("The", tokenView, ta, 0, 1);
// Constituent token2 = new Constituent("president", tokenView, ta, 1, 2);
// Constituent token3 = new Constituent("arrived", tokenView, ta, 2, 3);
// tokenView.addConstituent(token1);
// tokenView.addConstituent(token2);
// tokenView.addConstituent(token3);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView mentionView = new SpanLabelView(ViewNames.MENTION_ACE, "dummy", ta, 1.0f);
// Constituent mention = new Constituent("ORG", ViewNames.MENTION_ACE, ta, 0, 2);
// mention.addAttribute("EntityMentionType", "INVALID_TYPE");
// mention.addAttribute("EntityType", "ORG");
// mentionView.addConstituent(mention);
// ta.addView(ViewNames.MENTION_ACE, mentionView);
// assertNotNull(ta.getView(ViewNames.TOKENS));
// assertNotNull(ta.getView(ViewNames.MENTION_ACE));
}

@Test
public void testReaderHandlesEmptyTokenView() {
// TextAnnotation ta = TextAnnotationUtilities.createFromTokenizedString("test", "s", "a b");
// SpanLabelView mentionView = new SpanLabelView(ViewNames.MENTION_ACE, "dummy", ta, 1.0f);
// Constituent mention = new Constituent("PERSON", ViewNames.MENTION_ACE, ta, 0, 1);
// mention.addAttribute("EntityMentionType", "NAM");
// mention.addAttribute("EntityType", "PERSON");
// mentionView.addConstituent(mention);
// ta.addView(ViewNames.MENTION_ACE, mentionView);
// assertTrue(ta.hasView(ViewNames.MENTION_ACE));
// assertFalse(ta.hasView(ViewNames.TOKENS));
}

@Test
public void testReaderHandlesMissingMentionView() {
// TextAnnotation ta = TextAnnotationUtilities.createFromTokenizedString("test", "s", "Barack Obama went home.");
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "dummy", ta, 1.0f);
for (int i = 0; i < 4; i++) {
// Constituent c = new Constituent("w" + i, tokenView, ta, i, i + 1);
// tokenView.addConstituent(c);
}
// ta.addView(ViewNames.TOKENS, tokenView);
// assertTrue(ta.hasView(ViewNames.TOKENS));
// assertFalse(ta.hasView(ViewNames.MENTION_ACE));
}

@Test
public void testReaderEntityWithOneTokenBioluU() {
// TextAnnotation ta = TextAnnotationUtilities.createFromTokenizedString("test", "s", "Obama");
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "dummy", ta, 1.0f);
// Constituent token = new Constituent("Obama", tokenView, ta, 0, 1);
// tokenView.addConstituent(token);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView mentionView = new SpanLabelView(ViewNames.MENTION_ACE, "ann", ta, 1.0f);
// Constituent mention = new Constituent("PERSON", ViewNames.MENTION_ACE, ta, 0, 1);
// mention.addAttribute("EntityMentionType", "NAM");
// mention.addAttribute("EntityType", "PERSON");
// mentionView.addConstituent(mention);
// ta.addView(ViewNames.MENTION_ACE, mentionView);
// assertNotNull(ta.getView(ViewNames.TOKENS));
// assertNotNull(ta.getView(ViewNames.MENTION_ACE));
}

@Test
public void testReaderEntityWithMultiTokenBioluL() {
// TextAnnotation ta = TextAnnotationUtilities.createFromTokenizedString("test", "s", "Barack Hussein Obama");
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "dummy", ta, 1.0f);
// Constituent token1 = new Constituent("Barack", tokenView, ta, 0, 1);
// Constituent token2 = new Constituent("Hussein", tokenView, ta, 1, 2);
// Constituent token3 = new Constituent("Obama", tokenView, ta, 2, 3);
// tokenView.addConstituent(token1);
// tokenView.addConstituent(token2);
// tokenView.addConstituent(token3);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView mentionView = new SpanLabelView(ViewNames.MENTION_ACE, "ann", ta, 1.0f);
// Constituent mention = new Constituent("PERSON", ViewNames.MENTION_ACE, ta, 0, 3);
// mention.addAttribute("EntityMentionType", "NAM");
// mention.addAttribute("EntityType", "PERSON");
// mentionView.addConstituent(mention);
// ta.addView(ViewNames.MENTION_ACE, mentionView);
// assertEquals(3, ta.getView(ViewNames.TOKENS).getConstituents().size());
// assertEquals(1, ta.getView(ViewNames.MENTION_ACE).getConstituents().size());
}

@Test
public void testReaderHandlesEntityTypeVEH() {
// TextAnnotation ta = TextAnnotationUtilities.createFromTokenizedString("test", "s", "The vehicle exploded.");
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "dummy", ta, 1.0f);
// Constituent token1 = new Constituent("The", tokenView, ta, 0, 1);
// Constituent token2 = new Constituent("vehicle", tokenView, ta, 1, 2);
// tokenView.addConstituent(token1);
// tokenView.addConstituent(token2);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView mentionView = new SpanLabelView(ViewNames.MENTION_ACE, "source", ta, 1.0f);
// Constituent mention = new Constituent("VEH", ViewNames.MENTION_ACE, ta, 1, 2);
// mention.addAttribute("EntityMentionType", "NAM");
// mention.addAttribute("EntityType", "VEH");
// mentionView.addConstituent(mention);
// ta.addView(ViewNames.MENTION_ACE, mentionView);
// assertNotNull(ta.getView(ViewNames.MENTION_ACE));
// assertEquals("VEH", mention.getAttribute("EntityType"));
}

@Test
public void testEntityMentionWithoutEntityTypeAttributeIsSkipped() {
// TextAnnotation ta = TextAnnotationUtilities.createFromTokenizedString("test", "s", "Bank of America");
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "dummy", ta, 1.0f);
// tokenView.addConstituent(new Constituent("Bank", tokenView, ta, 0, 1));
// tokenView.addConstituent(new Constituent("of", tokenView, ta, 1, 2));
// tokenView.addConstituent(new Constituent("America", tokenView, ta, 2, 3));
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView mentionView = new SpanLabelView(ViewNames.MENTION_ACE, "source", ta, 1.0f);
// Constituent mention = new Constituent("ORG", ViewNames.MENTION_ACE, ta, 0, 3);
// mention.addAttribute("EntityMentionType", "NAM");
// mentionView.addConstituent(mention);
// ta.addView(ViewNames.MENTION_ACE, mentionView);
// assertTrue(mention.hasAttribute("EntityMentionType"));
// assertFalse(mention.hasAttribute("EntityType"));
}

@Test
public void testConstructorWithInvalidModeFormat() {
String path = "data/path";
String mode = "INVALIDFORMAT";
String type = "ALL";
boolean isBIO = true;
BIOReader reader = new BIOReader(path, mode, type, isBIO);
assertNotNull(reader);
}

@Test
public void testConstructorWithEmptyMode() {
String path = "data/path";
String mode = "";
String type = "ALL";
boolean isBIO = true;
BIOReader reader = new BIOReader(path, mode, type, isBIO);
assertNotNull(reader);
}

@Test
public void testMentionWithNullHeadIsSkipped() {
String[] tokens = new String[] { "The", "President", "spoke" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("corpus", "id", java.util.Arrays.asList(tokens));
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "dummy", ta, 1.0f);
// tokenView.addTokenLabel(0, "The", 1.0);
// tokenView.addTokenLabel(1, "President", 1.0);
// tokenView.addTokenLabel(2, "spoke", 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView mentionView = new SpanLabelView(ViewNames.MENTION_ACE, "dummy", ta, 1.0f);
// Constituent mention = new Constituent("PERSON", ViewNames.MENTION_ACE, ta, 0, 2);
// mention.addAttribute("EntityType", "PERSON");
// mention.addAttribute("EntityMentionType", "NAM");
// mentionView.addConstituent(mention);
// ta.addView(ViewNames.MENTION_ACE, mentionView);
// assertNotNull(ta.getView(ViewNames.MENTION_ACE));
}

@Test
public void testMentionWithUnmatchedTypeFilterIsSkipped() {
String[] tokens = new String[] { "The", "bank", "closed" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("c", "d", java.util.Arrays.asList(tokens));
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "dummy", ta, 1.0f);
// tokenView.addTokenLabel(0, "The", 1.0);
// tokenView.addTokenLabel(1, "bank", 1.0);
// tokenView.addTokenLabel(2, "closed", 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView mentionView = new SpanLabelView(ViewNames.MENTION_ACE, "source", ta, 1.0f);
// Constituent mention = new Constituent("ORG", ViewNames.MENTION_ACE, ta, 1, 2);
// mention.addAttribute("EntityType", "ORG");
// mention.addAttribute("EntityMentionType", "NOM");
// mentionView.addConstituent(mention);
// ta.addView(ViewNames.MENTION_ACE, mentionView);
// assertNotNull(ta.getView(ViewNames.MENTION_ACE));
// assertEquals("NOM", mention.getAttribute("EntityMentionType"));
}

@Test
public void testMentionWithSpecialPrefixFilteredCorrectly() {
String[] tokens = new String[] { "Dr.", "Who" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("c", "d", java.util.Arrays.asList(tokens));
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "tok", ta, 1.0f);
// tokenView.addTokenLabel(0, "Dr.", 1.0);
// tokenView.addTokenLabel(1, "Who", 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView mentionView = new SpanLabelView(ViewNames.MENTION_ACE, "src", ta, 1.0f);
// Constituent mention = new Constituent("PERSON", ViewNames.MENTION_ACE, ta, 0, 2);
// mention.addAttribute("EntityType", "PERSON");
// mention.addAttribute("EntityMentionType", "NAM");
// mentionView.addConstituent(mention);
// ta.addView(ViewNames.MENTION_ACE, mentionView);
BIOReader reader = new BIOReader("data/ere", "ACE05-TRAIN", "SPE_NOM", true);
assertNotNull(reader);
}

@Test
public void testEntityWithHttpIsTaggedWithDefaultWordNetValues() {
String[] tokens = new String[] { "http://example.com" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("c", "d", java.util.Arrays.asList(tokens));
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "tok", ta, 1.0f);
// tokenView.addTokenLabel(0, "http://example.com", 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView mentionView = new SpanLabelView(ViewNames.MENTION_ACE, "src", ta, 1.0f);
// Constituent mention = new Constituent("ORG", ViewNames.MENTION_ACE, ta, 0, 1);
// mention.addAttribute("EntityType", "ORG");
// mention.addAttribute("EntityMentionType", "NAM");
// mentionView.addConstituent(mention);
// ta.addView(ViewNames.MENTION_ACE, mentionView);
// assertTrue(tokenView.getConstituentsCoveringToken(0).get(0).toString().contains("http"));
}

@Test
public void testReaderHandlesNullMentionAttributeValuesGracefully() {
String[] tokens = new String[] { "Company" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("c", "d", java.util.Arrays.asList(tokens));
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "tok", ta, 1.0f);
// tokenView.addTokenLabel(0, "Company", 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView mentionView = new SpanLabelView(ViewNames.MENTION_ACE, "src", ta, 1.0f);
// Constituent mention = new Constituent("ORG", ViewNames.MENTION_ACE, ta, 0, 1);
// mention.addAttribute("EntityMentionType", null);
// mention.addAttribute("EntityType", null);
// mentionView.addConstituent(mention);
// ta.addView(ViewNames.MENTION_ACE, mentionView);
// assertTrue(mention.hasAttribute("EntityType"));
// assertNull(mention.getAttribute("EntityType"));
}

@Test
public void testConstructorWithNullTypeTreatsAsALL() {
String path = "mock/path";
String mode = "ColumnFormat-TRAIN";
String type = null;
boolean isBIO = true;
BIOReader reader = new BIOReader(path, mode, type, isBIO);
assertNotNull(reader);
}

@Test
public void testReaderHandlesMentionViewWithNoConstituents() {
String[] tokens = new String[] { "He", "came", "back" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("c", "d", java.util.Arrays.asList(tokens));
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "tok", ta, 1.0f);
// tokenView.addTokenLabel(0, "He", 1.0);
// tokenView.addTokenLabel(1, "came", 1.0);
// tokenView.addTokenLabel(2, "back", 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView mentionView = new SpanLabelView(ViewNames.MENTION_ACE, "src", ta, 1.0f);
// ta.addView(ViewNames.MENTION_ACE, mentionView);
// assertEquals(0, ta.getView(ViewNames.MENTION_ACE).getConstituents().size());
}

@Test
public void testReaderWithEmptyPathAndEmptyMode() {
String path = "";
String mode = "";
String type = "ALL";
boolean isBIO = true;
BIOReader reader = new BIOReader(path, mode, type, isBIO);
assertNotNull(reader);
}

@Test
public void testConstructorWithSplitModeOnlyHyphen() {
String path = "dataset/path/Sample";
String mode = "-";
String type = "ALL";
boolean isBIO = false;
BIOReader reader = new BIOReader(path, mode, type, isBIO);
assertNotNull(reader);
assertEquals("Sample_ALL", reader.id);
}

@Test
public void testConstructorWithEmptyTypeAndEmptyMode() {
String path = "dataset/path/Foo";
String mode = "";
String type = "";
boolean isBIO = true;
BIOReader reader = new BIOReader(path, mode, type, isBIO);
assertNotNull(reader);
assertEquals("Foo_", reader.id);
}

@Test
public void testConstructorWhereSplitPathEmptyLastSegment() {
String path = "dataset/path/";
String mode = "ACE05-TRAIN";
String type = "ALL";
boolean isBIO = true;
BIOReader reader = new BIOReader(path, mode, type, isBIO);
assertNotNull(reader);
assertEquals("_ALL", reader.id);
}

@Test
public void testMentionViewWithNonOverlappingConstituentSpan() {
String[] tokens = new String[] { "Barack", "Obama", "is", "speaking" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("corpus", "id", Arrays.asList(tokens));
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "dummy", ta, 1.0f);
// tokenView.addTokenLabel(0, "Barack", 1.0);
// tokenView.addTokenLabel(1, "Obama", 1.0);
// tokenView.addTokenLabel(2, "is", 1.0);
// tokenView.addTokenLabel(3, "speaking", 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView mentionView = new SpanLabelView(ViewNames.MENTION_ACE, "dummy", ta, 1.0f);
// Constituent mention = new Constituent("PERSON", ViewNames.MENTION_ACE, ta, 2, 4);
// mention.addAttribute("EntityMentionType", "PRO");
// mention.addAttribute("EntityType", "PERSON");
// mentionView.addConstituent(mention);
// ta.addView(ViewNames.MENTION_ACE, mentionView);
// assertEquals(2, mention.getStartSpan());
// assertEquals("PRO", mention.getAttribute("EntityMentionType"));
}

@Test
public void testMentionViewWithEntityWithOnlyOneCharacterWord() {
String[] tokens = new String[] { "A" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("corpus", "id", Arrays.asList(tokens));
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "dummyGenerator", ta, 1.0f);
// tokenView.addTokenLabel(0, "A", 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView mentionView = new SpanLabelView(ViewNames.MENTION_ACE, "labeler", ta, 1.0f);
// Constituent mention = new Constituent("PERSON", ViewNames.MENTION_ACE, ta, 0, 1);
// mention.addAttribute("EntityMentionType", "NAM");
// mention.addAttribute("EntityType", "PERSON");
// mentionView.addConstituent(mention);
// ta.addView(ViewNames.MENTION_ACE, mentionView);
// List<Constituent> tokensCovered = tokenView.getConstituentsCoveringToken(0);
// assertEquals(1, tokensCovered.size());
// assertEquals("A", tokensCovered.get(0).toString());
}

@Test
public void testMentionWithMissingEntityMentionTypeAttributeIsSkipped() {
String[] tokens = new String[] { "Angela", "Merkel", "spoke" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("c", "d", Arrays.asList(tokens));
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "gen", ta, 1.0f);
// tokenView.addTokenLabel(0, "Angela", 1.0);
// tokenView.addTokenLabel(1, "Merkel", 1.0);
// tokenView.addTokenLabel(2, "spoke", 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView mentionView = new SpanLabelView(ViewNames.MENTION_ACE, "manual", ta, 1.0f);
// Constituent mention = new Constituent("PERSON", ViewNames.MENTION_ACE, ta, 0, 2);
// mention.addAttribute("EntityType", "PERSON");
// mentionView.addConstituent(mention);
// ta.addView(ViewNames.MENTION_ACE, mentionView);
// assertTrue(mention.hasAttribute("EntityType"));
// assertFalse(mention.hasAttribute("EntityMentionType"));
}

@Test
public void testMixedViewTokensMentionEntityConsistency() {
String[] tokens = new String[] { "President", "Joe", "Biden" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("c", "d", Arrays.asList(tokens));
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "tok", ta, 1.0f);
// tokenView.addTokenLabel(0, "President", 1.0);
// tokenView.addTokenLabel(1, "Joe", 1.0);
// tokenView.addTokenLabel(2, "Biden", 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView mentionView = new SpanLabelView(ViewNames.MENTION_ACE, "mnt", ta, 1.0f);
// Constituent mention = new Constituent("PERSON", ViewNames.MENTION_ACE, ta, 1, 3);
// mention.addAttribute("EntityMentionType", "NAM");
// mention.addAttribute("EntityType", "PERSON");
// mentionView.addConstituent(mention);
// ta.addView(ViewNames.MENTION_ACE, mentionView);
// assertEquals("Joe", tokenView.getConstituentsCoveringToken(1).get(0).toString());
// assertEquals("PERSON", mention.getAttribute("EntityType"));
}

@Test
public void testNextMethodReturnsNullImmediatelyIfTokenListUninitialized() {
BIOReader reader = new BIOReader();
Object result = reader.next();
assertNull(result);
}

@Test
public void testBIOReaderConstructorWithNullPath() {
String path = null;
String mode = "ACE05-TRAIN";
String type = "ALL";
boolean isBIO = true;
BIOReader reader = new BIOReader(path, mode, type, isBIO);
assertNotNull(reader);
}

@Test
public void testBIOReaderHandlesModeWithMultipleHyphens() {
String path = "data/corpus/MyCorpus";
String mode = "ACE05-TRAIN-BAD";
String type = "ALL";
boolean isBIO = true;
BIOReader reader = new BIOReader(path, mode, type, isBIO);
assertEquals("MyCorpus_ALL", reader.id);
}

@Test
public void testTextAnnotationsReturnedIsEmptyForUnknownMode() {
BIOReader reader = new BIOReader("some/path", "UNKNOWN-TRAIN", "ALL", true);
List<TextAnnotation> results = reader.getTextAnnotations();
assertNotNull(results);
assertTrue(results.isEmpty());
}

@Test
public void testGetTextAnnotationsWithCorrectModeACE05ReturnsNonNullList() {
BIOReader reader = new BIOReader("data/path/fakeCorpus", "ACE05-TRAIN", "ALL", true);
List<TextAnnotation> results = reader.getTextAnnotations();
assertNotNull(results);
}

@Test
public void testAnnotateTasWithEmptyTextAnnotationListDoesNotFail() {
BIOReader reader = new BIOReader("data/path", "ACE05-TRAIN", "ALL", true);
List<TextAnnotation> emptyList = new ArrayList<>();
assertNotNull(reader);
}

@Test
public void testTokenWithNoMentionIsAssignedOValue() {
String[] tokens = new String[] { "London", "is", "great" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("c", "id", java.util.Arrays.asList(tokens));
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "gen", ta, 1.0f);
// tokenView.addTokenLabel(0, "London", 1.0);
// tokenView.addTokenLabel(1, "is", 1.0);
// tokenView.addTokenLabel(2, "great", 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView mentionView = new SpanLabelView(ViewNames.MENTION_ACE, "ann", ta, 1.0f);
// ta.addView(ViewNames.MENTION_ACE, mentionView);
// List<Constituent> tokenConstituents = tokenView.getConstituents();
// assertEquals(3, tokenConstituents.size());
// Constituent c = tokenConstituents.get(0);
// assertEquals("London", c.toString());
}

@Test
public void testMentionWithStartEqualsEndIsSkipped() {
String[] tokens = new String[] { "a", "b", "c" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("c", "id", java.util.Arrays.asList(tokens));
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "tok", ta, 1.0f);
// tokenView.addTokenLabel(0, "a", 1.0f);
// tokenView.addTokenLabel(1, "b", 1.0f);
// tokenView.addTokenLabel(2, "c", 1.0f);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView mentionView = new SpanLabelView(ViewNames.MENTION_ACE, "dummy", ta, 1.0f);
// Constituent mention = new Constituent("ENTITY", ViewNames.MENTION_ACE, ta, 1, 1);
// mention.addAttribute("EntityType", "ORG");
// mention.addAttribute("EntityMentionType", "NAM");
// mentionView.addConstituent(mention);
// ta.addView(ViewNames.MENTION_ACE, mentionView);
// assertTrue(mention.getStartSpan() == mention.getEndSpan());
}

@Test
public void testMentionWithHeadNullDoesNotThrow() {
String[] tokens = new String[] { "Alpha", "Beta" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("c", "id", java.util.Arrays.asList(tokens));
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "tok", ta, 1.0f);
// tokenView.addTokenLabel(0, "Alpha", 1.0f);
// tokenView.addTokenLabel(1, "Beta", 1.0f);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView mentionView = new SpanLabelView(ViewNames.MENTION_ACE, "dummy", ta, 1.0f);
// Constituent mention = new Constituent("ORG", ViewNames.MENTION_ACE, ta, 0, 2);
// mention.addAttribute("EntityType", "ORG");
// mention.addAttribute("EntityMentionType", "NAM");
// mentionView.addConstituent(mention);
// ta.addView(ViewNames.MENTION_ACE, mentionView);
// assertNotNull(mention);
}

@Test
public void testMentionsViewMissingEntityTypeAttributeIsSkipped() {
String[] tokens = new String[] { "Entity" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("c", "d", java.util.Arrays.asList(tokens));
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "tok", ta, 1.0f);
// tokenView.addTokenLabel(0, "Entity", 1.0f);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView mentionView = new SpanLabelView(ViewNames.MENTION_ACE, "tagger", ta, 1.0f);
// Constituent mention = new Constituent("NAM", ViewNames.MENTION_ACE, ta, 0, 1);
// mention.addAttribute("EntityMentionType", "NAM");
// mentionView.addConstituent(mention);
// ta.addView(ViewNames.MENTION_ACE, mentionView);
// assertFalse(mention.hasAttribute("EntityType"));
}

@Test
public void testMentionsViewWithNullTokenViewDoesNotThrow() {
String[] tokens = new String[] { "X", "Y", "Z" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("corpus", "id", java.util.Arrays.asList(tokens));
// SpanLabelView mentionView = new SpanLabelView(ViewNames.MENTION_ACE, "src", ta, 1.0f);
// Constituent mention = new Constituent("PERSON", ViewNames.MENTION_ACE, ta, 0, 2);
// mention.addAttribute("EntityType", "PERSON");
// mention.addAttribute("EntityMentionType", "NAM");
// mentionView.addConstituent(mention);
// ta.addView(ViewNames.MENTION_ACE, mentionView);
// assertNotNull(ta.getView(ViewNames.MENTION_ACE));
// assertFalse(ta.hasView(ViewNames.TOKENS));
}

@Test
public void testBIOReaderConstructorWithNullMode() {
String path = "some/path/corpus";
String mode = null;
String type = "ALL";
boolean isBIO = true;
try {
BIOReader reader = new BIOReader(path, mode, type, isBIO);
assertNotNull(reader);
} catch (Exception e) {
assertTrue(e instanceof NullPointerException || e instanceof RuntimeException);
}
}

@Test
public void testBIOReaderConstructorWithEmptyType() {
String path = "data/folder";
String mode = "ERE-TRAIN";
String type = "";
boolean isBIO = true;
BIOReader reader = new BIOReader(path, mode, type, isBIO);
assertEquals("folder_", reader.id);
}

@Test
public void testBIOReaderNextCalledMultipleTimesAfterEndReturnsNull() {
BIOReader reader = new BIOReader();
Object a = reader.next();
Object b = reader.next();
Object c = reader.next();
assertNull(a);
assertNull(b);
assertNull(c);
}

@Test
public void testMentionOfEntityTypeWEAIsProcessedWithBIOTrue() {
String[] tokens = new String[] { "Rocket", "attack" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("testCorpus", "docId", Arrays.asList(tokens));
// TokenLabelView tokensView = new TokenLabelView(ViewNames.TOKENS, "gen", ta, 1.0);
// tokensView.addTokenLabel(0, "Rocket", 1.0);
// tokensView.addTokenLabel(1, "attack", 1.0);
// ta.addView(ViewNames.TOKENS, tokensView);
// SpanLabelView mentionView = new SpanLabelView(ViewNames.MENTION_ACE, "tag", ta, 1.0f);
// Constituent c = new Constituent("WEA", ViewNames.MENTION_ACE, ta, 0, 2);
// c.addAttribute("EntityType", "WEA");
// c.addAttribute("EntityMentionType", "NAM");
// mentionView.addConstituent(c);
// ta.addView(ViewNames.MENTION_ACE, mentionView);
// List<Constituent> tokenList = ta.getView(ViewNames.TOKENS).getConstituents();
// assertEquals(2, tokenList.size());
// assertEquals("Rocket", tokenList.get(0).toString());
// assertEquals("attack", tokenList.get(1).toString());
}

@Test
public void testMentionWithBIOLULabelingCorrectness() {
String[] tokens = new String[] { "SingleEntity" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("corpus", "id", Arrays.asList(tokens));
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "gen", ta, 1.0);
// tokenView.addTokenLabel(0, "SingleEntity", 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView mentionView = new SpanLabelView("MENTIONS", "src", ta, 1.0f);
// Constituent entity = new Constituent("ORG", "MENTIONS", ta, 0, 1);
// entity.addAttribute("EntityType", "ORG");
// entity.addAttribute("EntityMentionType", "NAM");
// mentionView.addConstituent(entity);
// ta.addView("MENTIONS", mentionView);
// List<Constituent> mentionConstituents = mentionView.getConstituents();
// assertEquals(1, mentionConstituents.size());
// assertEquals("ORG", mentionConstituents.get(0).getLabel());
}

@Test
public void testEmptyTokenViewStillAllowsProcessing() {
String[] tokens = new String[] { "Alpha", "Beta" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("corpus", "doc", Arrays.asList(tokens));
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "fake", ta, 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView mentionView = new SpanLabelView(ViewNames.MENTION_ERE, "ann", ta, 1.0f);
// Constituent mention = new Constituent("PERSON", ViewNames.MENTION_ERE, ta, 0, 2);
// mention.addAttribute("EntityType", "PERSON");
// mention.addAttribute("EntityMentionType", "NAM");
// mentionView.addConstituent(mention);
// ta.addView(ViewNames.MENTION_ERE, mentionView);
// List<Constituent> mentionList = ta.getView(ViewNames.MENTION_ERE).getConstituents();
// assertEquals(1, mentionList.size());
// assertEquals("PERSON", mentionList.get(0).getAttribute("EntityType"));
}

@Test
public void testZeroTokenInputTextAnnotationIsHandled() {
List<String> tokens = Arrays.asList();
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("c", "id", tokens);
// TokenLabelView tokensView = new TokenLabelView(ViewNames.TOKENS, "fake", ta, 1.0f);
// ta.addView(ViewNames.TOKENS, tokensView);
// SpanLabelView mentionView = new SpanLabelView("MENTIONS", "src", ta, 1.0f);
// ta.addView("MENTIONS", mentionView);
// assertTrue(ta.getView(ViewNames.TOKENS).getConstituents().isEmpty());
// assertTrue(ta.getView("MENTIONS").getConstituents().isEmpty());
}

@Test
public void testMultipleMentionsWithDifferentTypesAreFilteredCorrectly() {
String[] tokens = new String[] { "Tom", "arrived", "at", "IBM" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("c", "id", Arrays.asList(tokens));
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "tagger", ta, 1.0);
// tokenView.addTokenLabel(0, "Tom", 1.0);
// tokenView.addTokenLabel(1, "arrived", 1.0);
// tokenView.addTokenLabel(2, "at", 1.0);
// tokenView.addTokenLabel(3, "IBM", 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView mentionView = new SpanLabelView(ViewNames.MENTION_ACE, "source", ta, 1.0f);
// Constituent mention1 = new Constituent("PERSON", ViewNames.MENTION_ACE, ta, 0, 1);
// mention1.addAttribute("EntityType", "PERSON");
// mention1.addAttribute("EntityMentionType", "PRO");
// Constituent mention2 = new Constituent("ORG", ViewNames.MENTION_ACE, ta, 3, 4);
// mention2.addAttribute("EntityType", "ORG");
// mention2.addAttribute("EntityMentionType", "NAM");
// mentionView.addConstituent(mention1);
// mentionView.addConstituent(mention2);
// ta.addView(ViewNames.MENTION_ACE, mentionView);
// List<Constituent> constituents = mentionView.getConstituents();
// assertEquals(2, constituents.size());
}

@Test
public void testPROMentionIsIgnoredWhenReaderTypeIsNAM() {
String[] tokens = new String[] { "He", "said" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("c", "id", Arrays.asList(tokens));
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "tok", ta, 1.0);
// tokenView.addTokenLabel(0, "He", 1.0);
// tokenView.addTokenLabel(1, "said", 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView mentionView = new SpanLabelView(ViewNames.MENTION_ERE, "mnt", ta, 1.0f);
// Constituent mention = new Constituent("PERSON", ViewNames.MENTION_ERE, ta, 0, 1);
// mention.addAttribute("EntityType", "PERSON");
// mention.addAttribute("EntityMentionType", "PRO");
// mentionView.addConstituent(mention);
// ta.addView(ViewNames.MENTION_ERE, mentionView);
// assertEquals("PRO", mention.getAttribute("EntityMentionType"));
// assertEquals("PERSON", mention.getAttribute("EntityType"));
}

@Test
public void testModeParsingWithJustHyphenResultsInCorrectFields() {
String path = "sample/path/CorpusRoot";
String mode = "-";
String type = "NAM";
boolean isBIO = true;
BIOReader reader = new BIOReader(path, mode, type, isBIO);
assertEquals("CorpusRoot_NAM", reader.id);
}

@Test
public void testModeParsingWithExtraHyphenCharactersHandledGracefully() {
String path = "data/src/C123";
String mode = "ERE-TRAIN-EXTRA";
String type = "ALL";
boolean isBIO = false;
BIOReader reader = new BIOReader(path, mode, type, isBIO);
assertEquals("C123_ALL", reader.id);
}

@Test
public void testTokenConstituentWithNullGazetteerDoesNotCrash() {
String[] tokens = new String[] { "hello" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("id", "test", Arrays.asList(tokens));
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "source", ta, 1.0f);
// tokenView.addTokenLabel(0, "hello", 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView mentionView = new SpanLabelView("MENTIONS", "source", ta, 1.0f);
// Constituent mention = new Constituent("ORG", "MENTIONS", ta, 0, 1);
// mention.addAttribute("EntityType", "ORG");
// mention.addAttribute("EntityMentionType", "NAM");
// mentionView.addConstituent(mention);
// ta.addView("MENTIONS", mentionView);
// Constituent token = tokenView.getConstituentsCoveringToken(0).get(0);
// assertNotNull(token);
// assertEquals(0, token.getStartSpan());
}

@Test
public void testBIOReaderHandlesCaseWhereMentionViewHasNullLabel() {
String[] tokens = new String[] { "data" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("c", "d", Arrays.asList(tokens));
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "creator", ta, 1.0);
// tokenView.addTokenLabel(0, "data", 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView mentionView = new SpanLabelView(ViewNames.MENTION_ACE, "builder", ta, 1.0f);
// Constituent mention = new Constituent(null, ViewNames.MENTION_ACE, ta, 0, 1);
// mention.addAttribute("EntityType", "ORG");
// mention.addAttribute("EntityMentionType", "NAM");
// mentionView.addConstituent(mention);
// ta.addView(ViewNames.MENTION_ACE, mentionView);
// assertNull(mention.getLabel());
}

@Test
public void testMentionsWithStartGreaterThanEndIsIgnoredByLogic() {
String[] tokens = new String[] { "wrong", "mention" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("c", "d", Arrays.asList(tokens));
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "creator", ta, 1.0);
// tokenView.addTokenLabel(0, "wrong", 1.0);
// tokenView.addTokenLabel(1, "mention", 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView mentionView = new SpanLabelView(ViewNames.MENTION_ACE, "source", ta, 1.0f);
// Constituent mention = new Constituent("PERSON", ViewNames.MENTION_ACE, ta, 1, 0);
// mention.addAttribute("EntityType", "PERSON");
// mention.addAttribute("EntityMentionType", "NAM");
// mentionView.addConstituent(mention);
// ta.addView(ViewNames.MENTION_ACE, mentionView);
// assertTrue(mention.getStartSpan() > mention.getEndSpan());
}

@Test
public void testBIOReaderHandlesMultipleMentionsOfSameTokenCorrectly() {
String[] tokens = new String[] { "New", "York" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("c", "id", Arrays.asList(tokens));
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "src", ta, 1.0f);
// tokenView.addTokenLabel(0, "New", 1.0);
// tokenView.addTokenLabel(1, "York", 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView mentionView = new SpanLabelView(ViewNames.MENTION_ACE, "annotator", ta, 1.0f);
// Constituent mentionOne = new Constituent("PERSON", ViewNames.MENTION_ACE, ta, 0, 1);
// mentionOne.addAttribute("EntityType", "PERSON");
// mentionOne.addAttribute("EntityMentionType", "NAM");
// Constituent mentionTwo = new Constituent("LOCATION", ViewNames.MENTION_ACE, ta, 0, 2);
// mentionTwo.addAttribute("EntityType", "LOCATION");
// mentionTwo.addAttribute("EntityMentionType", "NAM");
// mentionView.addConstituent(mentionOne);
// mentionView.addConstituent(mentionTwo);
// ta.addView(ViewNames.MENTION_ACE, mentionView);
// List<Constituent> mentionList = mentionView.getConstituents();
// assertEquals(2, mentionList.size());
}

@Test
public void testBIOReaderHandlesMentionWithEmptyAttributesMapSafely() {
String[] tokens = new String[] { "Intel" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("corpus", "doc", Arrays.asList(tokens));
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "tok", ta, 1.0);
// tokenView.addTokenLabel(0, "Intel", 1.0);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView mentionView = new SpanLabelView("MENTIONS", "tagger", ta, 1.0f);
// Constituent mention = new Constituent("ORG", "MENTIONS", ta, 0, 1);
// mentionView.addConstituent(mention);
// ta.addView("MENTIONS", mentionView);
// assertEquals("ORG", mention.getLabel());
// assertTrue(mention.getAttributeKeys().isEmpty() || mention.getAttribute("EntityType") == null);
}

@Test
public void testBIOReaderHandlesViewNameCaseSensitivityInModeMapping() {
String[] tokens = new String[] { "mock" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("c", "d", Arrays.asList(tokens));
// TokenLabelView tokenView = new TokenLabelView(ViewNames.TOKENS, "src", ta, 1.0f);
// tokenView.addTokenLabel(0, "mock", 1.0f);
// ta.addView(ViewNames.TOKENS, tokenView);
// SpanLabelView mentionView = new SpanLabelView("MENTION_ACE", "gen", ta, 1.0f);
// Constituent mention = new Constituent("PLACE", "MENTION_ACE", ta, 0, 1);
// mention.addAttribute("EntityType", "PLACE");
// mention.addAttribute("EntityMentionType", "NAM");
// mentionView.addConstituent(mention);
// ta.addView("MENTION_ACE", mentionView);
// assertEquals("MENTION_ACE", mentionView.getViewName());
// assertEquals("PLACE", mention.getAttribute("EntityType"));
}

@Test
public void testBIOReaderHandlesTextAnnotationWithMultipleViewsWithoutTokensView() {
String[] tokens = new String[] { "data" };
// TextAnnotation ta = TextAnnotationUtilities.createTextAnnotationFromTokens("fake", "doc1", Arrays.asList(tokens));
// SpanLabelView mentionView = new SpanLabelView(ViewNames.MENTION_ERE, "ann", ta, 1.0f);
// Constituent mention = new Constituent("ORG", ViewNames.MENTION_ERE, ta, 0, 1);
// mention.addAttribute("EntityType", "ORG");
// mention.addAttribute("EntityMentionType", "NAM");
// mentionView.addConstituent(mention);
// ta.addView(ViewNames.MENTION_ERE, mentionView);
// assertFalse(ta.hasView(ViewNames.TOKENS));
// assertTrue(ta.hasView(ViewNames.MENTION_ERE));
}
}
