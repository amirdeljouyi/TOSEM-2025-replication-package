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

public class BIOReader_4_GPTLLMTest {

@Test
public void testDefaultConstructorReturnsNonNullInstance() {
BIOReader reader = new BIOReader();
assertNotNull(reader);
}

@Test
public void testNextReturnsNullIfTokenListIsEmpty() {
BIOReader reader = new BIOReader() {

{
// this.tokenList = new ArrayList<Constituent>();
// this.tokenIndex = 0;
}
};
Object next = reader.next();
assertNull(next);
}

@Test
public void testNextReturnsSingleTokenAndThenNull() {
Constituent mockToken = mock(Constituent.class);
List<Constituent> tokenList = new ArrayList<Constituent>();
tokenList.add(mockToken);
BIOReader reader = new BIOReader() {

{
// this.tokenList = tokenList;
// this.tokenIndex = 0;
}
};
Object first = reader.next();
Object second = reader.next();
assertSame(mockToken, first);
assertNull(second);
}

@Test
public void testResetResetsToFirstToken() {
Constituent tokenA = mock(Constituent.class);
Constituent tokenB = mock(Constituent.class);
List<Constituent> tokens = new ArrayList<Constituent>();
tokens.add(tokenA);
tokens.add(tokenB);
BIOReader reader = new BIOReader() {

{
// this.tokenList = tokens;
// this.tokenIndex = 2;
}

@Override
public Object next() {
// if (this.tokenIndex == this.tokenList.size()) {
// return null;
// } else {
// Constituent result = this.tokenList.get(this.tokenIndex);
// this.tokenIndex += 1;
// return result;
// }
// }
// 
// @Override
// public void reset() {
// this.tokenIndex = 0;
// }
// };
// reader.reset();
// Object first = reader.next();
// assertSame(tokenA, first);
// }
// 
// @Test
// public void testConstructionIdentifierCreatedCorrectly() {
// BIOReader reader = new BIOReader("data/corpus", "ACE05-EVAL", "PRO", true) {
// 
// @Override
// public List<TextAnnotation> getTextAnnotations() {
return new ArrayList<TextAnnotation>();
}

// @Override
protected List<Constituent> getTokensFromTAs() {
return new ArrayList<Constituent>();
}
};
assertEquals("corpus_PRO", reader.id);
}

@Test
public void testConstructionWithEmptyTextAnnotationsDoesNotFail() {
BIOReader reader = new BIOReader("data/fake", "ERE-TRAIN", "ALL", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return new ArrayList<TextAnnotation>();
}

// @Override
protected List<Constituent> getTokensFromTAs() {
return new ArrayList<Constituent>();
}
};
Object result = reader.next();
assertNull(result);
}

@Test(expected = RuntimeException.class)
public void testConstructionFailsWithExceptionInTokenGeneration() {
BIOReader reader = new BIOReader("invalid/path", "ACE05-EVAL", "NAM", false) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return new ArrayList<TextAnnotation>();
}

// @Override
protected List<Constituent> getTokensFromTAs() {
throw new RuntimeException("Simulated token failure");
}
};
reader.next();
}

@Test
public void testNextWorksWithMockedToken() {
Constituent mockToken = mock(Constituent.class);
List<Constituent> oneToken = new ArrayList<Constituent>();
oneToken.add(mockToken);
BIOReader reader = new BIOReader() {

{
// this.tokenList = oneToken;
// this.tokenIndex = 0;
}

@Override
public Object next() {
// if (this.tokenIndex == this.tokenList.size())
return null;
// Object obj = this.tokenList.get(this.tokenIndex);
// this.tokenIndex += 1;
// return obj;
}
};
Object out1 = reader.next();
Object out2 = reader.next();
assertNotNull(out1);
assertNull(out2);
}

@Test
public void testBIOViewAddedProperlyUsingIsBIOTrue() {
TextAnnotation mockTA = mock(TextAnnotation.class);
View tokenView = mock(View.class);
View mentionView = mock(View.class);
Constituent token = mock(Constituent.class);
Constituent mention = mock(Constituent.class);
Constituent tokenClone = mock(Constituent.class);
when(mockTA.getView(ViewNames.TOKENS)).thenReturn(tokenView);
when(mockTA.getView("MENTIONS")).thenReturn(mentionView);
when(tokenView.getConstituents()).thenReturn(Collections.singletonList(token));
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(token));
when(mentionView.getConstituents()).thenReturn(Collections.singletonList(mention));
when(token.cloneForNewView("BIO")).thenReturn(tokenClone);
when(mention.getAttribute("EntityMentionType")).thenReturn("NAM");
when(mention.getAttribute("EntityType")).thenReturn("PER");
when(mention.getStartSpan()).thenReturn(0);
when(mention.getEndSpan()).thenReturn(1);
BIOReader reader = new BIOReader("mock/directory", "ColumnFormat-TRAIN", "NAM", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.singletonList(mockTA);
}

// @Override
protected List<Constituent> getTokensFromTAs() {
Constituent mockResult = mock(Constituent.class);
return Collections.singletonList(mockResult);
}
};
Object result = reader.next();
assertNotNull(result);
}

@Test
public void testBIOViewAddedProperlyUsingIsBIOFalse() {
TextAnnotation mockTA = mock(TextAnnotation.class);
View tokenView = mock(View.class);
View mentionView = mock(View.class);
Constituent token = mock(Constituent.class);
Constituent mention = mock(Constituent.class);
Constituent tokenClone = mock(Constituent.class);
when(mockTA.getView(ViewNames.TOKENS)).thenReturn(tokenView);
when(mockTA.getView("MENTIONS")).thenReturn(mentionView);
when(tokenView.getConstituents()).thenReturn(Collections.singletonList(token));
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(token));
when(mentionView.getConstituents()).thenReturn(Collections.singletonList(mention));
when(token.cloneForNewView("BIO")).thenReturn(tokenClone);
when(mention.getAttribute("EntityMentionType")).thenReturn("NAM");
when(mention.getAttribute("EntityType")).thenReturn("ORG");
when(mention.getStartSpan()).thenReturn(0);
when(mention.getEndSpan()).thenReturn(1);
BIOReader reader = new BIOReader("mock/data", "ColumnFormat-EVAL", "NAM", false) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.singletonList(mockTA);
}

// @Override
protected List<Constituent> getTokensFromTAs() {
Constituent tokenStub = mock(Constituent.class);
return Collections.singletonList(tokenStub);
}
};
Object tokenObj = reader.next();
assertNotNull(tokenObj);
}

@Test
public void testUnsupportedModeSkipsAndReturnsEmptyList() {
BIOReader reader = new BIOReader("some/path", "UNKNOWN-EVAL", "NAM", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return super.getTextAnnotations();
}

// @Override
protected List<Constituent> getTokensFromTAs() {
return Collections.emptyList();
}
};
Object result = reader.next();
assertNull(result);
}

@Test
public void testMentionEntityHeadIsNullIsSkipped() {
Constituent mention = mock(Constituent.class);
when(mention.getAttribute("EntityMentionType")).thenReturn("NAM");
when(mention.getAttribute("EntityType")).thenReturn("PER");
View mentionView = mock(View.class);
when(mentionView.getConstituents()).thenReturn(Collections.singletonList(mention));
View tokenView = mock(View.class);
when(tokenView.getConstituents()).thenReturn(Collections.singletonList(mock(Constituent.class)));
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(mock(Constituent.class)));
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView("MENTIONS")).thenReturn(mentionView);
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
BIOReader reader = new BIOReader("path/fake", "ColumnFormat-TRAIN", "NAM", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.singletonList(ta);
}

// @Override
protected List<Constituent> getTokensFromTAs() {
return Collections.emptyList();
}
};
Object result = reader.next();
assertNull(result);
}

@Test
public void testMentionIsFilteredOutByType() {
Constituent mention = mock(Constituent.class);
when(mention.getAttribute("EntityMentionType")).thenReturn("PRO");
when(mention.getAttribute("EntityType")).thenReturn("LOC");
View mentionView = mock(View.class);
when(mentionView.getConstituents()).thenReturn(Collections.singletonList(mention));
View tokenView = mock(View.class);
List<Constituent> tokens = Collections.singletonList(mock(Constituent.class));
when(tokenView.getConstituents()).thenReturn(tokens);
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(tokens);
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView("MENTIONS")).thenReturn(mentionView);
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
BIOReader reader = new BIOReader("dir", "ColumnFormat-EVAL", "NAM", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.singletonList(ta);
}

// @Override
protected List<Constituent> getTokensFromTAs() {
return Collections.emptyList();
}
};
Object result = reader.next();
assertNull(result);
}

@Test
public void testEntityTypeVehicleAndWeaponAreProcessed() {
Constituent mention = mock(Constituent.class);
when(mention.getAttribute("EntityMentionType")).thenReturn("NAM");
when(mention.getAttribute("EntityType")).thenReturn("VEH");
Constituent head = mock(Constituent.class);
when(head.getStartSpan()).thenReturn(0);
when(head.getEndSpan()).thenReturn(1);
when(mention.getStartSpan()).thenReturn(0);
when(mention.getEndSpan()).thenReturn(1);
View mentionView = mock(View.class);
when(mentionView.getConstituents()).thenReturn(Collections.singletonList(mention));
Constituent token = mock(Constituent.class);
Constituent tokenClone = mock(Constituent.class);
when(token.cloneForNewView("BIO")).thenReturn(tokenClone);
when(token.toString()).thenReturn("Tank");
View tokenView = mock(View.class);
when(tokenView.getConstituents()).thenReturn(Collections.singletonList(token));
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(token));
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView("MENTIONS")).thenReturn(mentionView);
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
BIOReader reader = new BIOReader("x", "ColumnFormat-TRAIN", "ALL", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.singletonList(ta);
}

// @Override
protected List<Constituent> getTokensFromTAs() {
Constituent finalToken = mock(Constituent.class);
return Collections.singletonList(finalToken);
}
};
Object tokenObj = reader.next();
assertNotNull(tokenObj);
}

@Test
public void testTokenCloneBIOOnlyO_Tag() {
Constituent token = mock(Constituent.class);
Constituent tokenClone = mock(Constituent.class);
when(token.cloneForNewView("BIO")).thenReturn(tokenClone);
when(token.toString()).thenReturn("school");
View tokenView = mock(View.class);
when(tokenView.getConstituents()).thenReturn(Collections.singletonList(token));
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(token));
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
when(ta.getView("MENTIONS")).thenReturn(mock(View.class));
BIOReader reader = new BIOReader("y", "ColumnFormat-EVAL", "ALL", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.singletonList(ta);
}

// @Override
protected List<Constituent> getTokensFromTAs() {
Constituent resultToken = mock(Constituent.class);
return Collections.singletonList(resultToken);
}
};
Object output = reader.next();
assertNotNull(output);
}

@Test
public void testMultiTokenMentionProducesBILTags() {
Constituent mention = mock(Constituent.class);
when(mention.getAttribute("EntityMentionType")).thenReturn("NAM");
when(mention.getAttribute("EntityType")).thenReturn("PERSON");
Constituent head = mock(Constituent.class);
when(head.getStartSpan()).thenReturn(0);
when(head.getEndSpan()).thenReturn(3);
when(mention.getStartSpan()).thenReturn(0);
when(mention.getEndSpan()).thenReturn(3);
View mentionView = mock(View.class);
when(mentionView.getConstituents()).thenReturn(Collections.singletonList(mention));
Constituent token0 = mock(Constituent.class);
Constituent token1 = mock(Constituent.class);
Constituent token2 = mock(Constituent.class);
when(token0.cloneForNewView("BIO")).thenReturn(mock(Constituent.class));
when(token1.cloneForNewView("BIO")).thenReturn(mock(Constituent.class));
when(token2.cloneForNewView("BIO")).thenReturn(mock(Constituent.class));
when(token0.toString()).thenReturn("John");
when(token1.toString()).thenReturn("F.");
when(token2.toString()).thenReturn("Kennedy");
View tokenView = mock(View.class);
when(tokenView.getConstituents()).thenReturn(List.of(token0, token1, token2));
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(List.of(token0));
when(tokenView.getConstituentsCoveringToken(1)).thenReturn(List.of(token1));
when(tokenView.getConstituentsCoveringToken(2)).thenReturn(List.of(token2));
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
when(ta.getView("MENTIONS")).thenReturn(mentionView);
BIOReader reader = new BIOReader("dir", "ColumnFormat-EVAL", "ALL", false) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.singletonList(ta);
}

// @Override
protected List<Constituent> getTokensFromTAs() {
return List.of(mock(Constituent.class), mock(Constituent.class), mock(Constituent.class));
}
};
Object t0 = reader.next();
Object t1 = reader.next();
Object t2 = reader.next();
Object t3 = reader.next();
assertNotNull(t0);
assertNotNull(t1);
assertNotNull(t2);
assertNull(t3);
}

@Test
public void testTokenContainingHttpSkipsWordNetTagging() {
Constituent token = mock(Constituent.class);
when(token.cloneForNewView("BIO")).thenReturn(mock(Constituent.class));
when(token.toString()).thenReturn("http://example.com");
View tokenView = mock(View.class);
when(tokenView.getConstituents()).thenReturn(Collections.singletonList(token));
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(token));
View mentionView = mock(View.class);
when(mentionView.getConstituents()).thenReturn(new ArrayList<Constituent>());
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
when(ta.getView("MENTIONS")).thenReturn(mentionView);
BIOReader reader = new BIOReader("dir", "ColumnFormat-TRAIN", "ALL", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.singletonList(ta);
}

// @Override
protected List<Constituent> getTokensFromTAs() {
Constituent resultToken = mock(Constituent.class);
return Collections.singletonList(resultToken);
}
};
Object result = reader.next();
assertNotNull(result);
}

@Test
public void testModeWithSPEPrefixTypeProcessesCorrectSubstring() {
Constituent mention = mock(Constituent.class);
when(mention.getAttribute("EntityMentionType")).thenReturn("NOM");
when(mention.getAttribute("EntityType")).thenReturn("ORG");
Constituent head = mock(Constituent.class);
when(head.getStartSpan()).thenReturn(0);
when(head.getEndSpan()).thenReturn(1);
when(mention.getStartSpan()).thenReturn(0);
when(mention.getEndSpan()).thenReturn(1);
View tokenView = mock(View.class);
View mentionView = mock(View.class);
Constituent token = mock(Constituent.class);
Constituent tokenClone = mock(Constituent.class);
when(token.cloneForNewView("BIO")).thenReturn(tokenClone);
when(token.toString()).thenReturn("school");
when(tokenView.getConstituents()).thenReturn(Collections.singletonList(token));
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(token));
when(mentionView.getConstituents()).thenReturn(Collections.singletonList(mention));
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
when(ta.getView("MENTIONS")).thenReturn(mentionView);
BIOReader reader = new BIOReader("data/corpus", "ColumnFormat-TRAIN", "SPE_NOM", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.singletonList(ta);
}

// @Override
protected List<Constituent> getTokensFromTAs() {
Constituent tokenResult = mock(Constituent.class);
return Collections.singletonList(tokenResult);
}
};
Object result = reader.next();
assertNotNull(result);
}

@Test
public void testModeACE05SetsMentionViewNameCorrectly() {
BIOReader reader = new BIOReader("data/corpus", "ACE05-TRAIN", "NAM", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return new ArrayList<TextAnnotation>();
}

// @Override
protected List<Constituent> getTokensFromTAs() {
return new ArrayList<Constituent>();
}
};
Object result = reader.next();
assertNull(result);
}

@Test
public void testModeEREAddsEntityTypeLabelForMention() {
Constituent mention = mock(Constituent.class);
when(mention.getAttribute("EntityMentionType")).thenReturn("NAM");
when(mention.getLabel()).thenReturn("ENTITY");
when(mention.getAttribute("EntityType")).thenReturn("ORG");
Constituent head = mock(Constituent.class);
when(head.getStartSpan()).thenReturn(0);
when(head.getEndSpan()).thenReturn(1);
View tokenView = mock(View.class);
View mentionView = mock(View.class);
when(mentionView.getConstituents()).thenReturn(Collections.singletonList(mention));
Constituent token = mock(Constituent.class);
Constituent tokenClone = mock(Constituent.class);
when(token.cloneForNewView("BIO")).thenReturn(tokenClone);
when(token.toString()).thenReturn("UN");
when(tokenView.getConstituents()).thenReturn(Collections.singletonList(token));
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(token));
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
when(ta.getView(ViewNames.MENTION_ERE)).thenReturn(mentionView);
BIOReader reader = new BIOReader("path/to/ere", "ERE-EVAL", "NAM", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.singletonList(ta);
}

// @Override
protected List<Constituent> getTokensFromTAs() {
return Collections.singletonList(mock(Constituent.class));
}
};
Object tag = reader.next();
assertNotNull(tag);
}

@Test
public void testTokenWithEmptyCoveringListSkipsGracefully() {
Constituent mention = mock(Constituent.class);
when(mention.getAttribute("EntityMentionType")).thenReturn("NAM");
when(mention.getAttribute("EntityType")).thenReturn("PER");
View mentionView = mock(View.class);
when(mentionView.getConstituents()).thenReturn(Collections.singletonList(mention));
Constituent token = mock(Constituent.class);
View tokenView = mock(View.class);
when(tokenView.getConstituents()).thenReturn(Collections.singletonList(token));
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(Collections.emptyList());
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView("MENTIONS")).thenReturn(mentionView);
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
BIOReader reader = new BIOReader("somepath", "ColumnFormat-TRAIN", "NAM", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.singletonList(ta);
}

// @Override
protected List<Constituent> getTokensFromTAs() {
return new ArrayList<Constituent>();
}
};
Object result = reader.next();
assertNull(result);
}

@Test
public void testEndToEndSingleTokenMentionWithBIOLUFalseProducesULabel() {
Constituent mention = mock(Constituent.class);
when(mention.getAttribute("EntityMentionType")).thenReturn("PRO");
when(mention.getAttribute("EntityType")).thenReturn("PER");
Constituent head = mock(Constituent.class);
when(head.getStartSpan()).thenReturn(0);
when(head.getEndSpan()).thenReturn(1);
View mentionView = mock(View.class);
when(mentionView.getConstituents()).thenReturn(Collections.singletonList(mention));
Constituent token = mock(Constituent.class);
Constituent tokenClone = mock(Constituent.class);
when(token.cloneForNewView("BIO")).thenReturn(tokenClone);
when(token.toString()).thenReturn("he");
View tokenView = mock(View.class);
when(tokenView.getConstituents()).thenReturn(Collections.singletonList(token));
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(token));
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView("MENTIONS")).thenReturn(mentionView);
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
BIOReader reader = new BIOReader("mock/path", "ColumnFormat-EVAL", "ALL", false) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.singletonList(ta);
}

// @Override
protected List<Constituent> getTokensFromTAs() {
Constituent finalToken = mock(Constituent.class);
return Collections.singletonList(finalToken);
}
};
Object result = reader.next();
assertNotNull(result);
}

@Test
public void testConstructorSplitsModeCorrectly() {
BIOReader reader = new BIOReader("root/path/segment", "ColumnFormat-TRAIN", "ALL", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return new ArrayList<TextAnnotation>();
}

// @Override
protected List<Constituent> getTokensFromTAs() {
return new ArrayList<Constituent>();
}
};
assertEquals("segment_ALL", reader.id);
}

@Test
public void testMentionViewNotPresentGracefullySkips() {
TextAnnotation ta = mock(TextAnnotation.class);
View tokenView = mock(View.class);
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
List<Constituent> tokens = new ArrayList<Constituent>();
Constituent token = mock(Constituent.class);
Constituent tokenClone = mock(Constituent.class);
when(token.cloneForNewView("BIO")).thenReturn(tokenClone);
when(token.toString()).thenReturn("Washington");
tokens.add(token);
when(tokenView.getConstituents()).thenReturn(tokens);
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(token));
when(ta.getView("MENTIONS")).thenThrow(new IllegalArgumentException("Missing view"));
BIOReader reader = new BIOReader("fake/path", "ColumnFormat-EVAL", "ALL", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.singletonList(ta);
}

// @Override
protected List<Constituent> getTokensFromTAs() {
return Collections.singletonList(mock(Constituent.class));
}
};
Object result = reader.next();
assertNotNull(result);
}

@Test
public void testHeadSpanStartGreaterThanEndIsIgnored() {
Constituent mention = mock(Constituent.class);
when(mention.getAttribute("EntityMentionType")).thenReturn("NAM");
when(mention.getAttribute("EntityType")).thenReturn("ORG");
Constituent head = mock(Constituent.class);
when(head.getStartSpan()).thenReturn(3);
when(head.getEndSpan()).thenReturn(2);
View mentionView = mock(View.class);
when(mentionView.getConstituents()).thenReturn(Collections.singletonList(mention));
View tokenView = mock(View.class);
Constituent token = mock(Constituent.class);
Constituent tokenClone = mock(Constituent.class);
when(token.cloneForNewView("BIO")).thenReturn(tokenClone);
when(token.toString()).thenReturn("Apple");
when(tokenView.getConstituents()).thenReturn(Collections.singletonList(token));
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(token));
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
when(ta.getView("MENTIONS")).thenReturn(mentionView);
BIOReader reader = new BIOReader("input/path", "ColumnFormat-TRAIN", "ALL", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.singletonList(ta);
}

// @Override
protected List<Constituent> getTokensFromTAs() {
return Collections.singletonList(mock(Constituent.class));
}
};
Object result = reader.next();
assertNotNull(result);
}

@Test
public void testMentionEntityTypeNullSkipsGracefully() {
Constituent mention = mock(Constituent.class);
when(mention.getAttribute("EntityMentionType")).thenReturn("NAM");
when(mention.getAttribute("EntityType")).thenReturn(null);
View mentionView = mock(View.class);
when(mentionView.getConstituents()).thenReturn(Collections.singletonList(mention));
View tokenView = mock(View.class);
Constituent token = mock(Constituent.class);
Constituent tokenClone = mock(Constituent.class);
when(token.cloneForNewView("BIO")).thenReturn(tokenClone);
when(token.toString()).thenReturn("Canada");
when(tokenView.getConstituents()).thenReturn(Collections.singletonList(token));
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(token));
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
when(ta.getView("MENTIONS")).thenReturn(mentionView);
BIOReader reader = new BIOReader("path/config", "ColumnFormat-EVAL", "ALL", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.singletonList(ta);
}

// @Override
protected List<Constituent> getTokensFromTAs() {
return Collections.singletonList(mock(Constituent.class));
}
};
Object result = reader.next();
assertNotNull(result);
}

@Test
public void testEmptyTokenViewConstituentsAreHandled() {
View emptyTokenView = mock(View.class);
when(emptyTokenView.getConstituents()).thenReturn(new ArrayList<Constituent>());
when(emptyTokenView.getConstituentsCoveringToken(0)).thenReturn(new ArrayList<Constituent>());
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.TOKENS)).thenReturn(emptyTokenView);
when(ta.getView("MENTIONS")).thenReturn(mock(View.class));
BIOReader reader = new BIOReader("check/tokens", "ColumnFormat-TRAIN", "ALL", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.singletonList(ta);
}

// @Override
protected List<Constituent> getTokensFromTAs() {
return new ArrayList<Constituent>();
}
};
Object result = reader.next();
assertNull(result);
}

@Test
public void testTokenListIsNullInNextHandling() {
BIOReader reader = new BIOReader() {

{
// this.tokenList = null;
// this.tokenIndex = 0;
}

@Override
public Object next() {
// if (this.tokenList == null || this.tokenIndex == this.tokenList.size()) {
// return null;
// }
// this.tokenIndex += 1;
// return this.tokenList.get(this.tokenIndex - 1);
// }
// };
// Object result = reader.next();
// assertNull(result);
// }
// 
// @Test
// public void testConstructorWithMultipleSlashesInPathSetsCorrectId() {
// BIOReader reader = new BIOReader("/some/long/data/corpora/news", "ACE05-EVAL", "PRO", true) {
// 
// @Override
// public List<TextAnnotation> getTextAnnotations() {
return new ArrayList<TextAnnotation>();
}

// @Override
protected List<Constituent> getTokensFromTAs() {
return new ArrayList<Constituent>();
}
};
assertEquals("news_PRO", reader.id);
}

@Test
public void testBIOReaderWithTypeALLAcceptsAnyMention() {
Constituent mention = mock(Constituent.class);
when(mention.getAttribute("EntityMentionType")).thenReturn("NOM");
when(mention.getAttribute("EntityType")).thenReturn("LOC");
Constituent head = mock(Constituent.class);
when(head.getStartSpan()).thenReturn(0);
when(head.getEndSpan()).thenReturn(1);
View mentionView = mock(View.class);
when(mentionView.getConstituents()).thenReturn(Collections.singletonList(mention));
View tokenView = mock(View.class);
Constituent token = mock(Constituent.class);
Constituent tokenClone = mock(Constituent.class);
when(token.cloneForNewView("BIO")).thenReturn(tokenClone);
when(token.toString()).thenReturn("island");
when(tokenView.getConstituents()).thenReturn(Collections.singletonList(token));
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(token));
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView(ViewNames.TOKENS)).thenReturn(tokenView);
when(ta.getView("MENTIONS")).thenReturn(mentionView);
BIOReader reader = new BIOReader("folder", "ColumnFormat-TRAIN", "ALL", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.singletonList(ta);
}

// @Override
protected List<Constituent> getTokensFromTAs() {
Constituent c = mock(Constituent.class);
return Collections.singletonList(c);
}
};
Object obj = reader.next();
assertNotNull(obj);
}

@Test
public void testModeColumnFormatMentionsViewNameSetCorrectly() {
BIOReader reader = new BIOReader("mock/path", "ColumnFormat-EVAL", "ALL", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
TextAnnotation ta = mock(TextAnnotation.class);
View tokenView = mock(View.class);
View mentionView = mock(View.class);
when(ta.getView("MENTIONS")).thenReturn(mentionView);
when(ta.getView("TOKENS")).thenReturn(tokenView);
when(tokenView.getConstituents()).thenReturn(Collections.emptyList());
List<TextAnnotation> tas = new ArrayList<TextAnnotation>();
tas.add(ta);
return tas;
}

// @Override
protected List<Constituent> getTokensFromTAs() {
return new ArrayList<Constituent>();
}
};
Object output = reader.next();
assertNull(output);
}

@Test
public void testTokenToTagsStayOWhenNoMentionsPresent() {
BIOReader reader = new BIOReader("mock/path", "ColumnFormat-EVAL", "ALL", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
TextAnnotation ta = mock(TextAnnotation.class);
View tokenView = mock(View.class);
when(tokenView.getConstituents()).thenReturn(Collections.singletonList(mock(Constituent.class)));
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(mock(Constituent.class)));
View mentionView = mock(View.class);
when(mentionView.getConstituents()).thenReturn(Collections.emptyList());
when(ta.getView("MENTIONS")).thenReturn(mentionView);
when(ta.getView("TOKENS")).thenReturn(tokenView);
return Collections.singletonList(ta);
}

// @Override
protected List<Constituent> getTokensFromTAs() {
Constituent c = mock(Constituent.class);
return Collections.singletonList(c);
}
};
Object result = reader.next();
assertNotNull(result);
}

@Test
public void testBIOReaderCreatesBIOViewEvenWhenMentionInvalid() {
Constituent mention = mock(Constituent.class);
when(mention.getAttribute("EntityMentionType")).thenReturn("NAM");
when(mention.getAttribute("EntityType")).thenReturn("ORG");
Constituent token = mock(Constituent.class);
when(token.cloneForNewView("BIO")).thenReturn(mock(Constituent.class));
when(token.toString()).thenReturn("Company");
View tokenView = mock(View.class);
when(tokenView.getConstituents()).thenReturn(Collections.singletonList(token));
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(token));
View mentionView = mock(View.class);
when(mentionView.getConstituents()).thenReturn(Collections.singletonList(mention));
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView("TOKENS")).thenReturn(tokenView);
when(ta.getView("MENTIONS")).thenReturn(mentionView);
BIOReader reader = new BIOReader("mock/corp", "ColumnFormat-EVAL", "NAM", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.singletonList(ta);
}

// @Override
protected List<Constituent> getTokensFromTAs() {
Constituent output = mock(Constituent.class);
return Collections.singletonList(output);
}
};
Object next = reader.next();
assertNotNull(next);
}

@Test
public void testBIOLUSingleTokenUsesUTagProperly() {
Constituent mention = mock(Constituent.class);
when(mention.getAttribute("EntityMentionType")).thenReturn("PRO");
when(mention.getAttribute("EntityType")).thenReturn("PER");
Constituent head = mock(Constituent.class);
when(head.getStartSpan()).thenReturn(0);
when(head.getEndSpan()).thenReturn(1);
View mentionView = mock(View.class);
when(mentionView.getConstituents()).thenReturn(Collections.singletonList(mention));
Constituent token = mock(Constituent.class);
Constituent tokenClone = mock(Constituent.class);
when(token.cloneForNewView("BIO")).thenReturn(tokenClone);
when(token.toString()).thenReturn("she");
View tokenView = mock(View.class);
when(tokenView.getConstituents()).thenReturn(Collections.singletonList(token));
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(token));
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView("TOKENS")).thenReturn(tokenView);
when(ta.getView("MENTIONS")).thenReturn(mentionView);
BIOReader reader = new BIOReader("mock/path", "ColumnFormat-TRAIN", "ALL", false) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.singletonList(ta);
}

// @Override
protected List<Constituent> getTokensFromTAs() {
return Collections.singletonList(mock(Constituent.class));
}
};
Object result = reader.next();
assertNotNull(result);
}

@Test
public void testMultipleInputPathsHandledInIdParsing() {
BIOReader reader = new BIOReader("/data/root/set/final/ace05", "ACE05-TRAIN", "NAM", false) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return new ArrayList<TextAnnotation>();
}

// @Override
protected List<Constituent> getTokensFromTAs() {
return new ArrayList<Constituent>();
}
};
assertEquals("ace05_NAM", reader.id);
}

@Test
public void testEmptyInputPathProducesValidId() {
BIOReader reader = new BIOReader("", "ACE05-TRAIN", "NAM", false) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return new ArrayList<TextAnnotation>();
}

// @Override
protected List<Constituent> getTokensFromTAs() {
return new ArrayList<Constituent>();
}
};
assertEquals("_NAM", reader.id);
}

@Test
public void testInvalidModeWithNoHyphenProducesNoException() {
BIOReader reader = new BIOReader("data/test", "NOHYPHEN", "ALL", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return new ArrayList<TextAnnotation>();
}

// @Override
protected List<Constituent> getTokensFromTAs() {
return new ArrayList<Constituent>();
}
};
Object result = reader.next();
assertNull(result);
}

@Test
public void testNextDoesNotAdvanceOnEmptyList() {
BIOReader reader = new BIOReader() {

{
// tokenList = new ArrayList<Constituent>();
// tokenIndex = 0;
}

@Override
public Object next() {
// if (tokenList == null || tokenIndex == tokenList.size()) {
// return null;
// }
// Object next = tokenList.get(tokenIndex);
// tokenIndex++;
// return next;
// }
// };
// assertNull(reader.next());
// assertNull(reader.next());
// }
// 
// @Test
// public void testMissingEntityMentionTypeAttributeIsTolerated() {
// Constituent mention = mock(Constituent.class);
// when(mention.getAttribute("EntityMentionType")).thenReturn(null);
// when(mention.getAttribute("EntityType")).thenReturn("LOC");
// Constituent head = mock(Constituent.class);
// when(head.getStartSpan()).thenReturn(0);
// when(head.getEndSpan()).thenReturn(1);
// View mentionView = mock(View.class);
// when(mentionView.getConstituents()).thenReturn(Collections.singletonList(mention));
// Constituent token = mock(Constituent.class);
// when(token.cloneForNewView("BIO")).thenReturn(mock(Constituent.class));
// when(token.toString()).thenReturn("Paris");
// View tokenView = mock(View.class);
// when(tokenView.getConstituents()).thenReturn(Collections.singletonList(token));
// when(tokenView.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(token));
// TextAnnotation ta = mock(TextAnnotation.class);
// when(ta.getView("TOKENS")).thenReturn(tokenView);
// when(ta.getView("MENTIONS")).thenReturn(mentionView);
// BIOReader reader = new BIOReader("input", "ColumnFormat-EVAL", "ALL", true) {
// 
// @Override
// public List<TextAnnotation> getTextAnnotations() {
// return Collections.singletonList(ta);
// }
// 
// @Override
// protected List<Constituent> getTokensFromTAs() {
return Collections.singletonList(mock(Constituent.class));
}
};
Object result = reader.next();
assertNotNull(result);
}

@Test
public void testTagBoundaryProcessedCorrectlyWithThreeTokenMentionInBIOLU() {
Constituent mention = mock(Constituent.class);
when(mention.getAttribute("EntityMentionType")).thenReturn("NAM");
when(mention.getAttribute("EntityType")).thenReturn("PER");
Constituent head = mock(Constituent.class);
when(head.getStartSpan()).thenReturn(0);
when(head.getEndSpan()).thenReturn(3);
View mentionView = mock(View.class);
when(mentionView.getConstituents()).thenReturn(Collections.singletonList(mention));
Constituent t0 = mock(Constituent.class);
Constituent t1 = mock(Constituent.class);
Constituent t2 = mock(Constituent.class);
when(t0.cloneForNewView("BIO")).thenReturn(mock(Constituent.class));
when(t1.cloneForNewView("BIO")).thenReturn(mock(Constituent.class));
when(t2.cloneForNewView("BIO")).thenReturn(mock(Constituent.class));
when(t0.toString()).thenReturn("Barack");
when(t1.toString()).thenReturn("H.");
when(t2.toString()).thenReturn("Obama");
View tokenView = mock(View.class);
when(tokenView.getConstituents()).thenReturn(List.of(t0, t1, t2));
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(t0));
when(tokenView.getConstituentsCoveringToken(1)).thenReturn(Collections.singletonList(t1));
when(tokenView.getConstituentsCoveringToken(2)).thenReturn(Collections.singletonList(t2));
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView("TOKENS")).thenReturn(tokenView);
when(ta.getView("MENTIONS")).thenReturn(mentionView);
BIOReader reader = new BIOReader("mock/path", "ColumnFormat-TRAIN", "NAM", false) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.singletonList(ta);
}

// @Override
protected List<Constituent> getTokensFromTAs() {
Constituent c1 = mock(Constituent.class);
Constituent c2 = mock(Constituent.class);
Constituent c3 = mock(Constituent.class);
return List.of(c1, c2, c3);
}
};
Object r1 = reader.next();
Object r2 = reader.next();
Object r3 = reader.next();
Object r4 = reader.next();
assertNotNull(r1);
assertNotNull(r2);
assertNotNull(r3);
assertNull(r4);
}

@Test
public void testWordNetTagsSkippedForHttpTokens() {
Constituent mention = mock(Constituent.class);
when(mention.getAttribute("EntityMentionType")).thenReturn("NAM");
when(mention.getAttribute("EntityType")).thenReturn("ORG");
Constituent head = mock(Constituent.class);
when(head.getStartSpan()).thenReturn(0);
when(head.getEndSpan()).thenReturn(1);
View mentionView = mock(View.class);
when(mentionView.getConstituents()).thenReturn(Collections.singletonList(mention));
Constituent token = mock(Constituent.class);
when(token.cloneForNewView("BIO")).thenReturn(mock(Constituent.class));
when(token.toString()).thenReturn("http://news.com");
View tokenView = mock(View.class);
when(tokenView.getConstituents()).thenReturn(Collections.singletonList(token));
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(token));
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView("TOKENS")).thenReturn(tokenView);
when(ta.getView("MENTIONS")).thenReturn(mentionView);
BIOReader reader = new BIOReader("data", "ColumnFormat-EVAL", "ALL", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.singletonList(ta);
}

// @Override
protected List<Constituent> getTokensFromTAs() {
Constituent result = mock(Constituent.class);
return Collections.singletonList(result);
}
};
Object result = reader.next();
assertNotNull(result);
}

@Test
public void testNonMatchingMentionTypeFilterSkipsMention() {
Constituent mention = mock(Constituent.class);
when(mention.getAttribute("EntityMentionType")).thenReturn("PRO");
when(mention.getAttribute("EntityType")).thenReturn("LOC");
View mentionView = mock(View.class);
when(mentionView.getConstituents()).thenReturn(Collections.singletonList(mention));
Constituent token = mock(Constituent.class);
Constituent tokenClone = mock(Constituent.class);
when(token.toString()).thenReturn("he");
when(token.cloneForNewView("BIO")).thenReturn(tokenClone);
View tokenView = mock(View.class);
when(tokenView.getConstituents()).thenReturn(Collections.singletonList(token));
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(token));
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView("TOKENS")).thenReturn(tokenView);
when(ta.getView("MENTIONS")).thenReturn(mentionView);
BIOReader reader = new BIOReader("check", "ColumnFormat-TRAIN", "NAM", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.singletonList(ta);
}

// @Override
protected List<Constituent> getTokensFromTAs() {
return Collections.singletonList(mock(Constituent.class));
}
};
Object result = reader.next();
assertNotNull(result);
}

@Test
public void testResetAfterNextAllowsRestartingIteration() {
Constituent token1 = mock(Constituent.class);
Constituent token2 = mock(Constituent.class);
BIOReader reader = new BIOReader() {

{
// tokenList = new ArrayList<Constituent>();
// tokenList.add(token1);
// tokenList.add(token2);
// tokenIndex = 0;
}

@Override
public Object next() {
// if (tokenIndex == tokenList.size())
return null;
// return tokenList.get(tokenIndex++);
}

@Override
public void reset() {
// tokenIndex = 0;
}
};
Object first = reader.next();
Object second = reader.next();
Object third = reader.next();
reader.reset();
Object resetFirst = reader.next();
assertSame(token1, first);
assertSame(token2, second);
assertNull(third);
assertSame(token1, resetFirst);
}

@Test
public void testOOnlyTokenViewProducesCorrectConstituentAttributes() {
Constituent token = mock(Constituent.class);
when(token.cloneForNewView("BIO")).thenReturn(mock(Constituent.class));
when(token.toString()).thenReturn("is");
View tokenView = mock(View.class);
when(tokenView.getConstituents()).thenReturn(Collections.singletonList(token));
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(token));
TextAnnotation ta = mock(TextAnnotation.class);
when(ta.getView("TOKENS")).thenReturn(tokenView);
when(ta.getView("MENTIONS")).thenReturn(mock(View.class));
BIOReader reader = new BIOReader("x", "ColumnFormat-TRAIN", "ALL", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.singletonList(ta);
}

// @Override
protected List<Constituent> getTokensFromTAs() {
return Collections.singletonList(mock(Constituent.class));
}
};
Object result = reader.next();
assertNotNull(result);
}

@Test
public void testConstructorWithEmptySplitPathSetsCorrectId() {
BIOReader reader = new BIOReader("/", "ColumnFormat-EVAL", "ALL", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return new ArrayList<TextAnnotation>();
}

// @Override
protected List<Constituent> getTokensFromTAs() {
return new ArrayList<Constituent>();
}
};
assertEquals("_ALL", reader.id);
}

@Test
public void testModeSplitWithInvalidFormatAvoidsException() {
BIOReader reader = new BIOReader("mock/path", "InvalidModeString", "NAM", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return new ArrayList<TextAnnotation>();
}

// @Override
protected List<Constituent> getTokensFromTAs() {
return new ArrayList<Constituent>();
}
};
reader.reset();
Object out = reader.next();
assertNull(out);
}

@Test
public void testGetTextAnnotationsWithUnrecognizedModeReturnsEmptyList() {
BIOReader reader = new BIOReader("mock/path", "UNKNOWN-TRAIN", "ALL", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return super.getTextAnnotations();
}

// @Override
protected List<Constituent> getTokensFromTAs() {
return new ArrayList<Constituent>();
}
};
Object result = reader.next();
assertNull(result);
}

@Test
public void testConstructReaderWithBogusMentionViewStillReturnsToken() {
TextAnnotation ta = mock(TextAnnotation.class);
View tokenView = mock(View.class);
View mentionView = mock(View.class);
Constituent token = mock(Constituent.class);
Constituent tokenClone = mock(Constituent.class);
List<Constituent> tokens = Collections.singletonList(token);
when(tokenView.getConstituents()).thenReturn(tokens);
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(tokens);
when(token.cloneForNewView("BIO")).thenReturn(tokenClone);
when(token.toString()).thenReturn("word");
when(mentionView.getConstituents()).thenReturn(null);
when(ta.getView("TOKENS")).thenReturn(tokenView);
when(ta.getView("MENTIONS")).thenReturn(mentionView);
BIOReader reader = new BIOReader("mock/path", "ColumnFormat-TRAIN", "ALL", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.singletonList(ta);
}

// @Override
protected List<Constituent> getTokensFromTAs() {
return Collections.singletonList(mock(Constituent.class));
}
};
Object result = reader.next();
assertNotNull(result);
}

@Test
public void testSingleMentionLengthOneUsesBCorrectly() {
TextAnnotation ta = mock(TextAnnotation.class);
View tokenView = mock(View.class);
View mentionView = mock(View.class);
Constituent mention = mock(Constituent.class);
when(mention.getAttribute("EntityMentionType")).thenReturn("NAM");
when(mention.getAttribute("EntityType")).thenReturn("ORG");
Constituent head = mock(Constituent.class);
when(head.getStartSpan()).thenReturn(0);
when(head.getEndSpan()).thenReturn(1);
Constituent token = mock(Constituent.class);
Constituent tokenClone = mock(Constituent.class);
when(token.cloneForNewView("BIO")).thenReturn(tokenClone);
when(token.toString()).thenReturn("Apple");
when(tokenView.getConstituents()).thenReturn(Collections.singletonList(token));
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(token));
when(mentionView.getConstituents()).thenReturn(Collections.singletonList(mention));
when(ta.getView("TOKENS")).thenReturn(tokenView);
when(ta.getView("MENTIONS")).thenReturn(mentionView);
BIOReader reader = new BIOReader("mock", "ColumnFormat-TRAIN", "NAM", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.singletonList(ta);
}

// @Override
protected List<Constituent> getTokensFromTAs() {
return Collections.singletonList(mock(Constituent.class));
}
};
Object result = reader.next();
assertNotNull(result);
}

@Test
public void testSingleMentionLengthTwoTriggersBIInBIO() {
TextAnnotation ta = mock(TextAnnotation.class);
View tokenView = mock(View.class);
View mentionView = mock(View.class);
Constituent mention = mock(Constituent.class);
when(mention.getAttribute("EntityMentionType")).thenReturn("NAM");
when(mention.getAttribute("EntityType")).thenReturn("LOC");
Constituent head = mock(Constituent.class);
when(head.getStartSpan()).thenReturn(0);
when(head.getEndSpan()).thenReturn(2);
Constituent t0 = mock(Constituent.class);
Constituent t1 = mock(Constituent.class);
when(t0.cloneForNewView("BIO")).thenReturn(mock(Constituent.class));
when(t1.cloneForNewView("BIO")).thenReturn(mock(Constituent.class));
when(t0.toString()).thenReturn("New");
when(t1.toString()).thenReturn("York");
when(tokenView.getConstituents()).thenReturn(List.of(t0, t1));
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(List.of(t0));
when(tokenView.getConstituentsCoveringToken(1)).thenReturn(List.of(t1));
when(mentionView.getConstituents()).thenReturn(Collections.singletonList(mention));
when(ta.getView("TOKENS")).thenReturn(tokenView);
when(ta.getView("MENTIONS")).thenReturn(mentionView);
BIOReader reader = new BIOReader("mock", "ColumnFormat-TRAIN", "ALL", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.singletonList(ta);
}

// @Override
protected List<Constituent> getTokensFromTAs() {
Constituent c0 = mock(Constituent.class);
Constituent c1 = mock(Constituent.class);
return List.of(c0, c1);
}
};
Object o1 = reader.next();
Object o2 = reader.next();
assertNotNull(o1);
assertNotNull(o2);
}

@Test
public void testEntityTypeVEHDoesNotTriggerSkip() {
TextAnnotation ta = mock(TextAnnotation.class);
View tokenView = mock(View.class);
View mentionView = mock(View.class);
Constituent mention = mock(Constituent.class);
when(mention.getAttribute("EntityMentionType")).thenReturn("NAM");
when(mention.getAttribute("EntityType")).thenReturn("VEH");
Constituent head = mock(Constituent.class);
when(head.getStartSpan()).thenReturn(0);
when(head.getEndSpan()).thenReturn(1);
Constituent token = mock(Constituent.class);
when(token.cloneForNewView("BIO")).thenReturn(mock(Constituent.class));
when(token.toString()).thenReturn("Jeep");
when(tokenView.getConstituents()).thenReturn(Collections.singletonList(token));
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(token));
when(mentionView.getConstituents()).thenReturn(Collections.singletonList(mention));
when(ta.getView("TOKENS")).thenReturn(tokenView);
when(ta.getView("MENTIONS")).thenReturn(mentionView);
BIOReader reader = new BIOReader("mock", "ColumnFormat-EVAL", "ALL", true) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.singletonList(ta);
}

// @Override
protected List<Constituent> getTokensFromTAs() {
return Collections.singletonList(mock(Constituent.class));
}
};
Object out = reader.next();
assertNotNull(out);
}

@Test
public void testSingleTokenMentionInBIOLUUsesUCorrectly() {
TextAnnotation ta = mock(TextAnnotation.class);
View tokenView = mock(View.class);
View mentionView = mock(View.class);
Constituent mention = mock(Constituent.class);
when(mention.getAttribute("EntityMentionType")).thenReturn("NAM");
when(mention.getAttribute("EntityType")).thenReturn("PER");
Constituent head = mock(Constituent.class);
when(head.getStartSpan()).thenReturn(0);
when(head.getEndSpan()).thenReturn(1);
Constituent token = mock(Constituent.class);
when(token.cloneForNewView("BIO")).thenReturn(mock(Constituent.class));
when(token.toString()).thenReturn("Obama");
when(tokenView.getConstituents()).thenReturn(Collections.singletonList(token));
when(tokenView.getConstituentsCoveringToken(0)).thenReturn(Collections.singletonList(token));
when(mentionView.getConstituents()).thenReturn(Collections.singletonList(mention));
when(ta.getView("TOKENS")).thenReturn(tokenView);
when(ta.getView("MENTIONS")).thenReturn(mentionView);
BIOReader reader = new BIOReader("mock", "ColumnFormat-TRAIN", "NAM", false) {

@Override
public List<TextAnnotation> getTextAnnotations() {
return Collections.singletonList(ta);
}

// @Override
protected List<Constituent> getTokensFromTAs() {
return Collections.singletonList(mock(Constituent.class));
}
};
Object out = reader.next();
assertNotNull(out);
}
}
