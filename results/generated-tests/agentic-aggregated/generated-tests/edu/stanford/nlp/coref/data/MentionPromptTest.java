package edu.stanford.nlp.coref.data;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.trees.Tree;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class MentionPromptTest {

  @Test
  public void getTypeIdentifiesMentionAnnotationValue() {
    Mention mention = new Mention();

    assertSame(Mention.class, mention.getType());
  }

  @Test
  public void freshMentionIsNotPronominal() {
    Mention mention = new Mention();

    assertFalse(mention.isPronominal());
  }

  @Test
  public void spanToStringJoinsTokenTextAndToStringDelegatesToIt() {
    CoreLabel first = new CoreLabel();
    first.setWord("New");
    CoreLabel second = new CoreLabel();
    second.setWord("York");
    Mention mention = new Mention(7, 2, 4, Arrays.asList(first, second),
        new SemanticGraph(), new SemanticGraph(), Arrays.asList(first, second));

    assertEquals("New York", mention.spanToString());
    assertEquals("New York", mention.toString());
  }

  @Test
  public void spanToStringHandlesEmptySpan() {
    Mention mention = new Mention(1, 0, 0, Collections.emptyList(),
        new SemanticGraph(), new SemanticGraph(), Collections.emptyList());

    assertEquals("", mention.spanToString());
    assertEquals("", mention.lowercaseNormalizedSpanString());
  }

  @Test
  public void spanToStringCachesItsFirstResult() {
    CoreLabel token = new CoreLabel();
    token.setWord("Before");
    Mention mention = new Mention(1, 0, 1, Collections.singletonList(token),
        new SemanticGraph(), new SemanticGraph(), Collections.singletonList(token));

    assertEquals("Before", mention.spanToString());
    token.setWord("After");
    assertEquals("Before", mention.spanToString());
  }

  @Test
  public void lowercaseNormalizedSpanStringLowercasesAndCaches() {
    CoreLabel token = new CoreLabel();
    token.setWord("MiXeD");
    Mention mention = new Mention(1, 0, 1, Collections.singletonList(token),
        new SemanticGraph(), new SemanticGraph(), Collections.singletonList(token));

    assertEquals("mixed", mention.lowercaseNormalizedSpanString());
    token.setWord("Changed");
    assertEquals("mixed", mention.lowercaseNormalizedSpanString());
  }

  @Test(expected = NullPointerException.class)
  public void spanToStringRejectsMissingOriginalSpan() {
    Mention mention = new Mention(1, 0, 1, Collections.emptyList(),
        new SemanticGraph(), new SemanticGraph());

    mention.spanToString();
  }

  @Test
  public void nerMethodsReturnNullBeforeProcessing() {
    CoreLabel token = new CoreLabel();
    token.setWord("Stanford");
    token.setNER("ORGANIZATION");
    Mention mention = new Mention(1, 0, 1, Collections.singletonList(token),
        new SemanticGraph(), new SemanticGraph(), Collections.singletonList(token));

    assertNull(mention.nerTokens());
    assertNull(mention.nerName());
  }

  @Test
  public void stringWithoutArticleRemovesSupportedLowercaseArticles() {
    CoreLabel token = new CoreLabel();
    token.setWord("fallback");
    Mention mention = new Mention(1, 0, 1, Collections.singletonList(token),
        new SemanticGraph(), new SemanticGraph(), Collections.singletonList(token));

    assertEquals("cat", mention.stringWithoutArticle("a cat"));
    assertEquals("owl", mention.stringWithoutArticle("an owl"));
    assertEquals("team", mention.stringWithoutArticle("the team"));
  }

  @Test
  public void stringWithoutArticleRemovesSupportedCapitalizedArticlesOnlyAtStart() {
    CoreLabel token = new CoreLabel();
    token.setWord("fallback");
    Mention mention = new Mention(1, 0, 1, Collections.singletonList(token),
        new SemanticGraph(), new SemanticGraph(), Collections.singletonList(token));

    assertEquals("Person", mention.stringWithoutArticle("A Person"));
    assertEquals("Example", mention.stringWithoutArticle("An Example"));
    assertEquals("Beatles", mention.stringWithoutArticle("The Beatles"));
    assertEquals("THE Beatles", mention.stringWithoutArticle("THE Beatles"));
    assertEquals("breathe", mention.stringWithoutArticle("breathe"));
  }

  @Test
  public void stringWithoutArticleUsesMentionSpanForNullInput() {
    CoreLabel first = new CoreLabel();
    first.setWord("the");
    CoreLabel second = new CoreLabel();
    second.setWord("company");
    Mention mention = new Mention(1, 0, 2, Arrays.asList(first, second),
        new SemanticGraph(), new SemanticGraph(), Arrays.asList(first, second));

    assertEquals("company", mention.stringWithoutArticle(null));
  }

  @Test
  public void buildQueryTextJoinsTermsWithSingleSpaces() {
    assertEquals("alpha beta gamma", Mention.buildQueryText(Arrays.asList("alpha", "beta", "gamma")));
  }

  @Test
  public void buildQueryTextHandlesEmptyAndEmbeddedWhitespace() {
    assertEquals("", Mention.buildQueryText(Collections.emptyList()));
    assertEquals("alpha  beta", Mention.buildQueryText(Arrays.asList("alpha", " beta")));
  }

  @Test(expected = NullPointerException.class)
  public void buildQueryTextRejectsNullList() {
    Mention.buildQueryText(null);
  }

  @Test
  public void removeParenthesisKeepsOnlyTrimmedPrefix() {
    assertEquals("Acme", Mention.removeParenthesis("  Acme (US) Holdings"));
    assertEquals("plain text", Mention.removeParenthesis("  plain text  "));
    assertEquals("", Mention.removeParenthesis("(alias)"));
  }

  @Test(expected = NullPointerException.class)
  public void removeParenthesisRejectsNull() {
    Mention.removeParenthesis(null);
  }

  @Test
  public void removePhraseAfterHeadCutsAtFirstComma() {
    CoreLabel company = new CoreLabel();
    company.setWord("Acme");
    company.setTag("NNP");
    CoreLabel comma = new CoreLabel();
    comma.setWord(",");
    comma.setTag(",");
    CoreLabel suffix = new CoreLabel();
    suffix.setWord("Inc.");
    suffix.setTag("NNP");
    List<CoreLabel> tokens = Arrays.asList(company, comma, suffix);
    Mention mention = new Mention(1, 0, 3, tokens,
        new SemanticGraph(), new SemanticGraph(), tokens);

    assertEquals("Acme", mention.removePhraseAfterHead());
  }

  @Test
  public void removePhraseAfterHeadCutsAtWhTokenWhenNoCommaExists() {
    CoreLabel person = new CoreLabel();
    person.setWord("engineer");
    person.setTag("NN");
    CoreLabel who = new CoreLabel();
    who.setWord("who");
    who.setTag("WP");
    CoreLabel arrived = new CoreLabel();
    arrived.setWord("arrived");
    arrived.setTag("VBD");
    List<CoreLabel> tokens = Arrays.asList(person, who, arrived);
    Mention mention = new Mention(1, 0, 3, tokens,
        new SemanticGraph(), new SemanticGraph(), tokens);

    assertEquals("engineer", mention.removePhraseAfterHead());
  }

  @Test
  public void removePhraseAfterHeadReturnsWholeSpanWithoutBoundary() {
    CoreLabel first = new CoreLabel();
    first.setWord("research");
    first.setTag("NN");
    CoreLabel second = new CoreLabel();
    second.setWord("team");
    second.setTag("NN");
    List<CoreLabel> tokens = Arrays.asList(first, second);
    Mention mention = new Mention(1, 0, 2, tokens,
        new SemanticGraph(), new SemanticGraph(), tokens);

    assertEquals("research team", mention.removePhraseAfterHead());
  }

  @Test
  public void longestNNPEndsWithHeadReturnsInitialProperNounAtDefaultHead() {
    CoreLabel stanford = new CoreLabel();
    stanford.setWord("Stanford");
    stanford.setTag("NNP");
    CoreLabel university = new CoreLabel();
    university.setWord("University");
    university.setTag("NNP");
    List<CoreLabel> words = Arrays.asList(stanford, university);
    Mention mention = new Mention(1, 0, 2, words,
        new SemanticGraph(), new SemanticGraph(), words);

    assertEquals("Stanford", mention.longestNNPEndsWithHead());
  }

  @Test
  public void longestNNPEndsWithHeadReturnsEmptyForNonProperHead() {
    CoreLabel token = new CoreLabel();
    token.setWord("company");
    token.setTag("NN");
    Mention mention = new Mention(1, 0, 1, Collections.singletonList(token),
        new SemanticGraph(), new SemanticGraph(), Collections.singletonList(token));

    assertEquals("", mention.longestNNPEndsWithHead());
  }

  @Test
  public void sameSentenceUsesSentenceListIdentity() {
    CoreLabel token = new CoreLabel();
    token.setWord("word");
    List<CoreLabel> shared = Collections.singletonList(token);
    Mention first = new Mention(1, 0, 1, shared, new SemanticGraph(), new SemanticGraph(), shared);
    Mention second = new Mention(2, 0, 1, shared, new SemanticGraph(), new SemanticGraph(), shared);
    Mention copied = new Mention(3, 0, 1, Collections.singletonList(token),
        new SemanticGraph(), new SemanticGraph(), shared);

    assertTrue(first.sameSentence(second));
    assertFalse(first.sameSentence(copied));
  }

  @Test
  public void includedInRequiresSharedSentenceAndContainedOffsets() {
    CoreLabel token = new CoreLabel();
    token.setWord("x");
    List<CoreLabel> sentence = Collections.singletonList(token);
    Mention inner = new Mention(1, 2, 4, sentence, new SemanticGraph(), new SemanticGraph(), sentence);
    Mention outer = new Mention(2, 1, 5, sentence, new SemanticGraph(), new SemanticGraph(), sentence);
    Mention overlapping = new Mention(3, 3, 5, sentence, new SemanticGraph(), new SemanticGraph(), sentence);

    assertTrue(inner.includedIn(outer));
    assertFalse(outer.includedIn(inner));
    assertFalse(overlapping.includedIn(inner));
  }

  @Test
  public void includedInRejectsEqualOffsetsFromDifferentSentenceObjects() {
    CoreLabel token = new CoreLabel();
    token.setWord("x");
    Mention first = new Mention(1, 0, 1, Collections.singletonList(token),
        new SemanticGraph(), new SemanticGraph(), Collections.singletonList(token));
    Mention second = new Mention(2, 0, 1, Collections.singletonList(token),
        new SemanticGraph(), new SemanticGraph(), Collections.singletonList(token));

    assertFalse(first.includedIn(second));
  }

  @Test
  public void insideInUsesDefaultSentenceAndOffsets() {
    List<CoreLabel> empty = Collections.emptyList();
    Mention inner = new Mention(1, 3, 5, empty, new SemanticGraph(), new SemanticGraph(), empty);
    Mention outer = new Mention(2, 1, 8, empty, new SemanticGraph(), new SemanticGraph(), empty);
    Mention disjoint = new Mention(3, 7, 9, empty, new SemanticGraph(), new SemanticGraph(), empty);

    assertTrue(inner.insideIn(outer));
    assertFalse(outer.insideIn(inner));
    assertFalse(disjoint.insideIn(outer));
  }

  @Test
  public void appearEarlierThanComparesStartThenEndOffsets() {
    List<CoreLabel> empty = Collections.emptyList();
    Mention early = new Mention(1, 1, 2, empty, new SemanticGraph(), new SemanticGraph(), empty);
    Mention late = new Mention(2, 3, 4, empty, new SemanticGraph(), new SemanticGraph(), empty);
    Mention containing = new Mention(3, 1, 5, empty, new SemanticGraph(), new SemanticGraph(), empty);

    assertTrue(early.appearEarlierThan(late));
    assertFalse(late.appearEarlierThan(early));
    assertTrue(containing.appearEarlierThan(early));
    assertFalse(early.appearEarlierThan(containing));
  }

  @Test
  public void appositionCanBeRegisteredAndQueried() {
    List<CoreLabel> empty = Collections.emptyList();
    Mention title = new Mention(1, 0, 1, empty, new SemanticGraph(), new SemanticGraph(), empty);
    Mention name = new Mention(2, 1, 2, empty, new SemanticGraph(), new SemanticGraph(), empty);
    Mention unrelated = new Mention(3, 2, 3, empty, new SemanticGraph(), new SemanticGraph(), empty);

    assertFalse(title.isApposition(name));
    title.addApposition(name);
    assertTrue(title.isApposition(name));
    assertFalse(title.isApposition(unrelated));
  }

  @Test
  public void predicateNominativeCanBeRegisteredAndQueried() {
    List<CoreLabel> empty = Collections.emptyList();
    Mention subject = new Mention(1, 0, 1, empty, new SemanticGraph(), new SemanticGraph(), empty);
    Mention complement = new Mention(2, 2, 3, empty, new SemanticGraph(), new SemanticGraph(), empty);

    assertFalse(subject.isPredicateNominatives(complement));
    subject.addPredicateNominatives(complement);
    assertTrue(subject.isPredicateNominatives(complement));
    assertFalse(complement.isPredicateNominatives(subject));
  }

  @Test
  public void relativePronounCanBeRegisteredAndQueried() {
    List<CoreLabel> empty = Collections.emptyList();
    Mention antecedent = new Mention(1, 0, 1, empty, new SemanticGraph(), new SemanticGraph(), empty);
    Mention relative = new Mention(2, 1, 2, empty, new SemanticGraph(), new SemanticGraph(), empty);

    assertFalse(antecedent.isRelativePronoun(relative));
    antecedent.addRelativePronoun(relative);
    assertTrue(antecedent.isRelativePronoun(relative));
    assertFalse(relative.isRelativePronoun(antecedent));
  }

  @Test
  public void mentionsRecognizeMembershipInSameList() {
    List<CoreLabel> empty = Collections.emptyList();
    Mention list = new Mention(10, 0, 4, empty, new SemanticGraph(), new SemanticGraph(), empty);
    Mention first = new Mention(1, 0, 1, empty, new SemanticGraph(), new SemanticGraph(), empty);
    Mention second = new Mention(2, 2, 3, empty, new SemanticGraph(), new SemanticGraph(), empty);
    Mention third = new Mention(3, 5, 6, empty, new SemanticGraph(), new SemanticGraph(), empty);

    first.addBelongsToList(list);
    second.addBelongsToList(list);
    assertTrue(first.isMemberOfSameList(second));
    assertFalse(first.isMemberOfSameList(third));
    assertFalse(third.isMemberOfSameList(second));
  }

  @Test
  public void addingListMemberDoesNotImplyReverseBelongingRelationship() {
    List<CoreLabel> empty = Collections.emptyList();
    Mention list = new Mention(10, 0, 4, empty, new SemanticGraph(), new SemanticGraph(), empty);
    Mention member = new Mention(1, 0, 1, empty, new SemanticGraph(), new SemanticGraph(), empty);

    list.addListMember(member);
    assertFalse(list.isMemberOfSameList(member));
    assertFalse(member.isMemberOfSameList(list));
  }

  @Test
  public void unprocessedMentionIsNotDetectedAsListMember() {
    List<CoreLabel> empty = Collections.emptyList();
    Mention possibleMember = new Mention(1, 1, 2, empty, new SemanticGraph(), new SemanticGraph(), empty);
    Mention possibleList = new Mention(2, 0, 3, empty, new SemanticGraph(), new SemanticGraph(), empty);

    assertFalse(possibleMember.isListMemberOf(possibleList));
    assertFalse(possibleMember.isListMemberOf(possibleMember));
  }

  @Test
  public void positionReportsFirstForConstructorDefaultHeadIndex() {
    CoreLabel first = new CoreLabel();
    first.setWord("first");
    CoreLabel second = new CoreLabel();
    second.setWord("second");
    Mention mention = new Mention(1, 0, 1, Arrays.asList(first, second),
        new SemanticGraph(), new SemanticGraph(), Collections.singletonList(first));

    assertEquals("first", mention.getPosition());
  }

  @Test
  public void dependencyBasedMethodsUseSafeFallbackWithoutHeadNode() {
    CoreLabel token = new CoreLabel();
    token.setWord("item");
    Mention mention = new Mention(1, 0, 1, Collections.singletonList(token),
        new SemanticGraph(), new SemanticGraph(), Collections.singletonList(token));

    assertNull(mention.getRelation());
    assertEquals(0, mention.getPremodifiers().size());
    assertEquals(0, mention.getPostmodifiers().size());
    assertFalse(mention.isCoordinated());
    assertEquals(0, mention.getCoordination());
  }

  @Test
  public void contextExtractsContiguousNamedEntity() {
    CoreLabel newToken = new CoreLabel();
    newToken.setWord("New");
    newToken.setNER("LOCATION");
    CoreLabel york = new CoreLabel();
    york.setWord("York");
    york.setNER("LOCATION");
    CoreLabel verb = new CoreLabel();
    verb.setWord("grew");
    verb.setNER("O");
    List<CoreLabel> sentence = Arrays.asList(newToken, york, verb);
    Mention mention = new Mention(1, 0, 2, sentence,
        new SemanticGraph(), new SemanticGraph(), Arrays.asList(newToken, york));

    assertEquals(Collections.singletonList("New York"), mention.getContext());
  }

  @Test
  public void contextDeduplicatesRepeatedNamedEntityStrings() {
    CoreLabel first = new CoreLabel();
    first.setWord("Alice");
    first.setNER("PERSON");
    CoreLabel separator = new CoreLabel();
    separator.setWord("met");
    separator.setNER("O");
    CoreLabel repeated = new CoreLabel();
    repeated.setWord("Alice");
    repeated.setNER("PERSON");
    List<CoreLabel> sentence = Arrays.asList(first, separator, repeated);
    Mention mention = new Mention(1, 0, 1, sentence,
        new SemanticGraph(), new SemanticGraph(), Collections.singletonList(first));

    assertEquals(Collections.singletonList("Alice"), mention.getContext());
  }

  @Test
  public void contextIsEmptyWhenSentenceHasNoNamedEntities() {
    CoreLabel token = new CoreLabel();
    token.setWord("ordinary");
    token.setNER("O");
    Mention mention = new Mention(1, 0, 1, Collections.singletonList(token),
        new SemanticGraph(), new SemanticGraph(), Collections.singletonList(token));

    assertTrue(mention.getContext().isEmpty());
    assertTrue(mention.getPremodifierContext().isEmpty());
  }

  @Test
  public void equalMentionsBuiltFromSameCollaboratorsHaveEqualHashes() {
    CoreLabel token = new CoreLabel();
    token.setWord("same");
    List<CoreLabel> words = Collections.singletonList(token);
    SemanticGraph basic = new SemanticGraph();
    SemanticGraph enhanced = new SemanticGraph();
    Mention first = new Mention(4, 2, 3, words, basic, enhanced, words);
    Mention second = new Mention(4, 2, 3, words, basic, enhanced, words);

    assertEquals(first, second);
    assertEquals(first.hashCode(), second.hashCode());
    assertEquals(first, first);
  }

  @Test
  public void equalityRejectsNullOtherTypeAndDifferentIdentityData() {
    List<CoreLabel> empty = Collections.emptyList();
    SemanticGraph basic = new SemanticGraph();
    SemanticGraph enhanced = new SemanticGraph();
    Mention first = new Mention(1, 0, 1, empty, basic, enhanced, empty);
    Mention differentId = new Mention(2, 0, 1, empty, basic, enhanced, empty);
    Mention differentEnd = new Mention(1, 0, 2, empty, basic, enhanced, empty);

    assertNotEquals(first, null);
    assertNotEquals(first, "mention");
    assertNotEquals(first, differentId);
    assertNotEquals(first, differentEnd);
    assertNotEquals(first.hashCode(), differentEnd.hashCode());
  }

  @Test
  public void treeConstructorStillParticipatesInValueEquality() {
    CoreLabel token = new CoreLabel();
    token.setWord("node");
    List<CoreLabel> words = Collections.singletonList(token);
    SemanticGraph basic = new SemanticGraph();
    SemanticGraph enhanced = new SemanticGraph();
    Tree firstTree = Tree.valueOf("(NP node)");
    Tree secondTree = Tree.valueOf("(VP node)");
    Mention first = new Mention(1, 0, 1, words, basic, enhanced, words, firstTree);
    Mention second = new Mention(1, 0, 1, words, basic, enhanced, words, secondTree);

    assertEquals(first, second);
  }

  @Test
  public void serializationRoundTripPreservesValueAndRebuildsTransientSpanCache() throws Exception {
    CoreLabel first = new CoreLabel();
    first.setWord("Core");
    CoreLabel second = new CoreLabel();
    second.setWord("NLP");
    List<CoreLabel> words = Arrays.asList(first, second);
    Mention original = new Mention(9, 0, 2, words,
        new SemanticGraph(), new SemanticGraph(), words);
    assertEquals("Core NLP", original.spanToString());
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    ObjectOutputStream output = new ObjectOutputStream(bytes);
    output.writeObject(original);
    output.close();
    ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()));
    Mention restored = (Mention) input.readObject();
    input.close();

    assertEquals("Core NLP", restored.spanToString());
    assertEquals("core nlp", restored.lowercaseNormalizedSpanString());
    assertEquals(original, restored);
    assertEquals(original.hashCode(), restored.hashCode());
  }
}
