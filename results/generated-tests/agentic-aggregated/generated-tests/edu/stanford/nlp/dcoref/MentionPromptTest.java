package edu.stanford.nlp.dcoref;

import edu.stanford.nlp.ling.AbstractCoreLabel;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.trees.Tree;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class MentionPromptTest {

  @Test
  public void coreAnnotationTypeAndDefaultPronounStateAreExposedThroughMethods() {
    Mention mention = new Mention();

    assertSame(Mention.class, mention.getType());
    assertFalse(mention.isPronominal());
  }

  @Test
  public void spanRenderingJoinsCoreLabelTextAndBacksToString() {
    CoreLabel first = new CoreLabel();
    first.setWord("New");
    CoreLabel second = new CoreLabel();
    second.setWord("York");
    Mention mention = new Mention(17, 2, 4, new SemanticGraph(), Arrays.asList(first, second));

    assertEquals("New York", mention.spanToString());
    assertEquals("New York", mention.toString());
    assertEquals("new york", mention.lowercaseNormalizedSpanString());
  }

  @Test
  public void spanRenderingSupportsEmptyAndSingleTokenSpans() {
    Mention empty = new Mention(1, 0, 0, new SemanticGraph(), Collections.emptyList());
    CoreLabel punctuation = new CoreLabel();
    punctuation.setWord("!");
    Mention single = new Mention(2, 0, 1, new SemanticGraph(), Collections.singletonList(punctuation));

    assertEquals("", empty.spanToString());
    assertEquals("", empty.lowercaseNormalizedSpanString());
    assertEquals("!", single.spanToString());
  }

  @Test
  public void renderedSpanIsCachedAcrossTokenMutation() {
    CoreLabel token = new CoreLabel();
    token.setWord("Before");
    Mention mention = new Mention(3, 0, 1, new SemanticGraph(), Collections.singletonList(token));

    assertEquals("Before", mention.spanToString());
    token.setWord("After");
    assertEquals("Before", mention.spanToString());
    assertEquals("before", mention.lowercaseNormalizedSpanString());
  }

  @Test(expected = NullPointerException.class)
  public void lowercaseNormalizationBeforeSpanRenderingReportsUninitializedCache() {
    CoreLabel token = new CoreLabel();
    token.setWord("MixedCase");
    Mention mention = new Mention(4, 0, 1, new SemanticGraph(), Collections.singletonList(token));

    mention.lowercaseNormalizedSpanString();
  }

  @Test(expected = NullPointerException.class)
  public void spanRenderingRejectsMissingSpan() {
    Mention mention = new Mention(5, 0, 0, new SemanticGraph());

    mention.spanToString();
  }

  @Test
  public void unprocessedMentionHasNoNamedEntityTokensOrName() {
    CoreLabel token = new CoreLabel();
    token.setWord("Stanford");
    token.setNER("ORGANIZATION");
    Mention mention = new Mention(6, 0, 1, new SemanticGraph(), Collections.singletonList(token));

    assertNull(mention.nerTokens());
    assertNull(mention.nerName());
  }

  @Test
  public void serializationPreservesSpanDataAndRebuildsTransientRenderingCache() throws Exception {
    CoreLabel first = new CoreLabel();
    first.setWord("San");
    CoreLabel second = new CoreLabel();
    second.setWord("Francisco");
    Mention original = new Mention(7, 0, 2, new SemanticGraph(), Arrays.asList(first, second));
    assertEquals("San Francisco", original.spanToString());
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    ObjectOutputStream output = new ObjectOutputStream(bytes);
    output.writeObject(original);
    output.close();
    ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()));
    Mention restored = (Mention) input.readObject();
    input.close();

    assertEquals("San Francisco", restored.spanToString());
    assertEquals("san francisco", restored.lowercaseNormalizedSpanString());
    assertSame(Mention.class, restored.getType());
  }

  @Test
  public void listMembershipRelationshipRequiresACommonList() {
    Mention first = new Mention();
    Mention second = new Mention();
    Mention commonList = new Mention();
    Mention otherList = new Mention();

    assertFalse(first.isMemberOfSameList(second));
    first.addBelongsToList(commonList);
    assertFalse(first.isMemberOfSameList(second));
    second.addBelongsToList(commonList);
    assertTrue(first.isMemberOfSameList(second));
    assertTrue(second.isMemberOfSameList(first));
    second.addBelongsToList(otherList);
    assertTrue(first.isMemberOfSameList(second));
  }

  @Test
  public void separateListMembershipsDoNotMatchAndDuplicateAddsAreHarmless() {
    Mention first = new Mention();
    Mention second = new Mention();
    Mention firstList = new Mention();
    Mention secondList = new Mention();

    first.addBelongsToList(firstList);
    first.addBelongsToList(firstList);
    second.addBelongsToList(secondList);

    assertFalse(first.isMemberOfSameList(second));
    assertTrue(first.isMemberOfSameList(first));
  }

  @Test
  public void appositionRelationshipIsDirectionalAndIdempotent() {
    Mention title = new Mention();
    Mention person = new Mention();
    Mention stranger = new Mention();

    assertFalse(title.isApposition(person));
    title.addApposition(person);
    title.addApposition(person);

    assertTrue(title.isApposition(person));
    assertFalse(person.isApposition(title));
    assertFalse(title.isApposition(stranger));
  }

  @Test
  public void predicateNominativeRelationshipIsDirectional() {
    Mention subject = new Mention();
    Mention complement = new Mention();

    assertFalse(subject.isPredicateNominatives(complement));
    subject.addPredicateNominatives(complement);

    assertTrue(subject.isPredicateNominatives(complement));
    assertFalse(complement.isPredicateNominatives(subject));
  }

  @Test
  public void relativePronounRelationshipIsDirectional() {
    Mention antecedent = new Mention();
    Mention relative = new Mention();

    assertFalse(antecedent.isRelativePronoun(relative));
    antecedent.addRelativePronoun(relative);

    assertTrue(antecedent.isRelativePronoun(relative));
    assertFalse(relative.isRelativePronoun(antecedent));
  }

  @Test
  public void nullAttributesAgreeNonStrictlyAndStrictly() {
    Mention first = new Mention();
    Mention second = new Mention();

    assertTrue(first.numbersAgree(second));
    assertTrue(first.gendersAgree(second));
    assertTrue(first.gendersAgree(second, true));
    assertTrue(first.animaciesAgree(second));
    assertTrue(first.animaciesAgree(second, true));
  }

  @Test
  public void constructorIndicesDriveDocumentOrdering() {
    Mention earlier = new Mention(1, 2, 4, new SemanticGraph());
    Mention later = new Mention(2, 5, 7, new SemanticGraph());
    Mention containingAtSameStart = new Mention(3, 2, 6, new SemanticGraph());

    assertTrue(earlier.appearEarlierThan(later));
    assertFalse(later.appearEarlierThan(earlier));
    assertTrue(containingAtSameStart.appearEarlierThan(earlier));
    assertFalse(earlier.appearEarlierThan(containingAtSameStart));
  }

  @Test
  public void constructorIndicesDriveGeometricContainment() {
    Mention outer = new Mention(1, 2, 9, new SemanticGraph());
    Mention inner = new Mention(2, 3, 7, new SemanticGraph());
    Mention overlapping = new Mention(3, 1, 4, new SemanticGraph());

    assertTrue(inner.insideIn(outer));
    assertTrue(outer.insideIn(outer));
    assertFalse(outer.insideIn(inner));
    assertFalse(overlapping.insideIn(outer));
  }

  @Test
  public void treeIdentityAndBoundsDetermineSyntacticInclusion() {
    Tree outerTree = Tree.valueOf("(NP (DT The) (NP (NN cat)))");
    Tree nestedTree = outerTree.getChild(1);
    Mention outer = new Mention(1, 0, 2, new SemanticGraph(), Collections.emptyList(), outerTree);
    Mention nested = new Mention(2, 1, 2, new SemanticGraph(), Collections.emptyList(), nestedTree);
    Mention sameShapeDifferentIdentity = new Mention(3, 1, 2, new SemanticGraph(), Collections.emptyList(), Tree.valueOf("(NP (NN cat))"));

    assertTrue(nested.includedIn(outer));
    assertFalse(sameShapeDifferentIdentity.includedIn(outer));
    assertFalse(outer.includedIn(nested));
  }

  @Test
  public void outOfBoundsMentionIsNotIncludedEvenWhenItUsesNestedTree() {
    Tree outerTree = Tree.valueOf("(NP (JJ red) (NN car))");
    Tree nestedTree = outerTree.getChild(0);
    Mention outer = new Mention(1, 3, 5, new SemanticGraph(), Collections.emptyList(), outerTree);
    Mention invalid = new Mention(2, 2, 4, new SemanticGraph(), Collections.emptyList(), nestedTree);

    assertFalse(invalid.includedIn(outer));
  }

  @Test
  public void untypedMentionsAreNotRecognizedAsListMembers() {
    Mention candidate = new Mention(1, 1, 2, new SemanticGraph());
    Mention container = new Mention(2, 0, 3, new SemanticGraph());

    assertFalse(candidate.isListMemberOf(container));
    assertFalse(candidate.isListMemberOf(candidate));
  }

  @Test
  public void articleRemovalHandlesAllSupportedCasesAndNullFallback() {
    CoreLabel first = new CoreLabel();
    first.setWord("The");
    CoreLabel second = new CoreLabel();
    second.setWord("owl");
    Mention mention = new Mention(8, 0, 2, new SemanticGraph(), Arrays.asList(first, second));

    assertEquals("cat", mention.stringWithoutArticle("a cat"));
    assertEquals("idea", mention.stringWithoutArticle("An idea"));
    assertEquals("answer", mention.stringWithoutArticle("the answer"));
    assertEquals("THE answer", mention.stringWithoutArticle("THE answer"));
    assertEquals("Theodore", mention.stringWithoutArticle("Theodore"));
    assertEquals("owl", mention.stringWithoutArticle(null));
  }

  @Test
  public void queryBuilderJoinsTermsWithoutExtraBoundaryWhitespace() {
    assertEquals("alpha beta gamma", Mention.buildQueryText(Arrays.asList("alpha", "beta", "gamma")));
    assertEquals("", Mention.buildQueryText(Collections.emptyList()));
    assertEquals("one", Mention.buildQueryText(Collections.singletonList("one")));
  }

  @Test(expected = NullPointerException.class)
  public void queryBuilderRejectsNullList() {
    Mention.buildQueryText(null);
  }

  @Test
  public void parenthesisRemovalHandlesAbsentLeadingAndMultipleParentheses() {
    assertEquals("plain text", Mention.removeParenthesis("plain text"));
    assertEquals("name", Mention.removeParenthesis(" name (qualification) (extra)"));
    assertEquals("", Mention.removeParenthesis("(qualification)"));
    assertEquals("", Mention.removeParenthesis(""));
  }

  @Test(expected = NullPointerException.class)
  public void parenthesisRemovalRejectsNull() {
    Mention.removeParenthesis(null);
  }

  @Test
  public void unprocessedDependencyFeaturesUseDocumentedNeutralResults() {
    Mention mention = new Mention(9, 0, 1, new SemanticGraph());

    assertTrue(mention.getPremodifiers().isEmpty());
    assertTrue(mention.getPostmodifiers().isEmpty());
    assertFalse(mention.isCoordinated());
    assertNull(mention.getRelation());
    assertEquals(0, mention.getModifiers(null));
    assertNull(mention.getQuantification(null));
    assertEquals(0, mention.getNegation(null));
    assertEquals(0, mention.getModal(null));
    assertEquals(0, mention.getReportEmbedding(null));
    assertEquals(0, mention.getCoordination());
  }

  @Test
  public void explicitEmptyPatternListProducesEmptyPattern() {
    Mention mention = new Mention();
    List<AbstractCoreLabel> labels = new ArrayList<>();

    assertEquals("", mention.getPattern(labels));
  }

  @Test
  public void nullCandidateIsAlwaysLessRepresentative() {
    Mention mention = new Mention();

    assertTrue(mention.moreRepresentativeThan(null));
  }

  @Test(expected = NullPointerException.class)
  public void processingAnUninitializedMentionFailsAtRequiredHeadWord() throws Exception {
    Mention mention = new Mention();

    mention.process(null, null, null);
  }

  @Test(expected = NullPointerException.class)
  public void contextRequiresSentenceWordsFromMentionExtraction() {
    Mention mention = new Mention();

    mention.getContext();
  }
}
