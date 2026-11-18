package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;

import static org.junit.Assert.*;

public class TokensRegexNERAnnotatorPromptTest {

  @Test
  public void annotatesSingleTokenInDocumentTokenList() throws Exception {
    Path mapping = Files.createTempFile("tokensregexner-single", ".tab");
    Files.write(mapping, Collections.singletonList("Stanford\tSCHOOL\tO"), StandardCharsets.UTF_8);
    mapping.toFile().deleteOnExit();
    CoreLabel token = new CoreLabel();
    token.setWord("Stanford");
    token.setNER("O");
    Annotation document = new Annotation("Stanford");
    document.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(mapping.toString());
    annotator.annotate(document);

    assertEquals("SCHOOL", token.ner());
  }

  @Test
  public void annotatesMultiTokenPhraseAndLeavesNeighborAlone() throws Exception {
    Path mapping = Files.createTempFile("tokensregexner-phrase", ".tab");
    Files.write(mapping, Collections.singletonList("New York\tCITY\tO"), StandardCharsets.UTF_8);
    mapping.toFile().deleteOnExit();
    CoreLabel newToken = new CoreLabel();
    newToken.setWord("New");
    newToken.setNER("O");
    CoreLabel yorkToken = new CoreLabel();
    yorkToken.setWord("York");
    yorkToken.setNER("O");
    CoreLabel todayToken = new CoreLabel();
    todayToken.setWord("today");
    todayToken.setNER("O");
    Annotation document = new Annotation("New York today");
    document.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(newToken, yorkToken, todayToken));
    Properties properties = new Properties();
    properties.setProperty("rules.mapping", mapping.toString());
    properties.setProperty("rules.noDefaultOverwriteLabels", "");

    new TokensRegexNERAnnotator("rules", properties).annotate(document);

    assertEquals("CITY", newToken.ner());
    assertEquals("CITY", yorkToken.ner());
    assertEquals("O", todayToken.ner());
  }

  @Test
  public void processesTokensInsideEachSentence() throws Exception {
    Path mapping = Files.createTempFile("tokensregexner-sentences", ".tab");
    Files.write(mapping, Arrays.asList("Alice\tPERSON\tO", "Paris\tPLACE\tO"), StandardCharsets.UTF_8);
    mapping.toFile().deleteOnExit();
    CoreLabel alice = new CoreLabel();
    alice.setWord("Alice");
    alice.setNER("O");
    CoreLabel paris = new CoreLabel();
    paris.setWord("Paris");
    paris.setNER("O");
    Annotation firstSentence = new Annotation("Alice");
    firstSentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(alice));
    Annotation secondSentence = new Annotation("Paris");
    secondSentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(paris));
    Annotation document = new Annotation("Alice. Paris.");
    document.set(CoreAnnotations.SentencesAnnotation.class,
        Arrays.<CoreMap>asList(firstSentence, secondSentence));

    new TokensRegexNERAnnotator(mapping.toString()).annotate(document);

    assertEquals("PERSON", alice.ner());
    assertEquals("PLACE", paris.ner());
  }

  @Test
  public void ignoreCaseConstructorMatchesDifferentCase() throws Exception {
    Path mapping = Files.createTempFile("tokensregexner-case", ".tab");
    Files.write(mapping, Collections.singletonList("stanford\tSCHOOL\tO"), StandardCharsets.UTF_8);
    mapping.toFile().deleteOnExit();
    CoreLabel token = new CoreLabel();
    token.setWord("STANFORD");
    token.setNER("O");
    Annotation document = new Annotation("STANFORD");
    document.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    new TokensRegexNERAnnotator(mapping.toString(), true).annotate(document);

    assertEquals("SCHOOL", token.ner());
  }

  @Test
  public void caseSensitiveConstructorDoesNotMatchDifferentCase() throws Exception {
    Path mapping = Files.createTempFile("tokensregexner-sensitive", ".tab");
    Files.write(mapping, Collections.singletonList("stanford\tSCHOOL\tO"), StandardCharsets.UTF_8);
    mapping.toFile().deleteOnExit();
    CoreLabel token = new CoreLabel();
    token.setWord("Stanford");
    token.setNER("O");
    Annotation document = new Annotation("Stanford");
    document.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    new TokensRegexNERAnnotator(mapping.toString(), false).annotate(document);

    assertEquals("O", token.ner());
  }

  @Test
  public void higherPriorityMatchWinsEvenWhenItIsShorter() throws Exception {
    Path mapping = Files.createTempFile("tokensregexner-priority", ".tab");
    Files.write(mapping, Arrays.asList(
        "New York\tCITY\tO\t1",
        "New York University\tSCHOOL\tO\t5"), StandardCharsets.UTF_8);
    mapping.toFile().deleteOnExit();
    CoreLabel one = new CoreLabel();
    one.setWord("New");
    one.setNER("O");
    CoreLabel two = new CoreLabel();
    two.setWord("York");
    two.setNER("O");
    CoreLabel three = new CoreLabel();
    three.setWord("University");
    three.setNER("O");
    Annotation document = new Annotation("New York University");
    document.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(one, two, three));

    new TokensRegexNERAnnotator(mapping.toString()).annotate(document);

    assertEquals("SCHOOL", one.ner());
    assertEquals("SCHOOL", two.ner());
    assertEquals("SCHOOL", three.ner());
  }

  @Test
  public void explicitOverwriteTypeReplacesExistingEntity() throws Exception {
    Path mapping = Files.createTempFile("tokensregexner-overwrite", ".tab");
    Files.write(mapping, Collections.singletonList("Acme Corp\tCOMPANY\tORG"), StandardCharsets.UTF_8);
    mapping.toFile().deleteOnExit();
    CoreLabel acme = new CoreLabel();
    acme.setWord("Acme");
    acme.setNER("ORG");
    CoreLabel corp = new CoreLabel();
    corp.setWord("Corp");
    corp.setNER("ORG");
    Annotation document = new Annotation("Acme Corp");
    document.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(acme, corp));

    new TokensRegexNERAnnotator(mapping.toString()).annotate(document);

    assertEquals("COMPANY", acme.ner());
    assertEquals("COMPANY", corp.ner());
  }

  @Test
  public void consistentProtectedEntityIsNotOverwritten() throws Exception {
    Path mapping = Files.createTempFile("tokensregexner-protected", ".tab");
    Files.write(mapping, Collections.singletonList("Acme Corp\tCOMPANY\tO"), StandardCharsets.UTF_8);
    mapping.toFile().deleteOnExit();
    CoreLabel acme = new CoreLabel();
    acme.setWord("Acme");
    acme.setNER("ORG");
    CoreLabel corp = new CoreLabel();
    corp.setWord("Corp");
    corp.setNER("ORG");
    Annotation document = new Annotation("Acme Corp");
    document.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(acme, corp));

    new TokensRegexNERAnnotator(mapping.toString()).annotate(document);

    assertEquals("ORG", acme.ner());
    assertEquals("ORG", corp.ner());
  }

  @Test
  public void inconsistentExistingTagsAreOverwritten() throws Exception {
    Path mapping = Files.createTempFile("tokensregexner-inconsistent", ".tab");
    Files.write(mapping, Collections.singletonList("Acme Corp\tCOMPANY"), StandardCharsets.UTF_8);
    mapping.toFile().deleteOnExit();
    CoreLabel acme = new CoreLabel();
    acme.setWord("Acme");
    acme.setNER("PERSON");
    CoreLabel corp = new CoreLabel();
    corp.setWord("Corp");
    corp.setNER("ORG");
    Annotation document = new Annotation("Acme Corp");
    document.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(acme, corp));

    new TokensRegexNERAnnotator(mapping.toString()).annotate(document);

    assertEquals("COMPANY", acme.ner());
    assertEquals("COMPANY", corp.ner());
  }

  @Test
  public void matchCuttingAcrossExistingEntityBoundaryIsRejected() throws Exception {
    Path mapping = Files.createTempFile("tokensregexner-boundary", ".tab");
    Files.write(mapping, Collections.singletonList("ABC\tCODE"), StandardCharsets.UTF_8);
    mapping.toFile().deleteOnExit();
    CoreLabel the = new CoreLabel();
    the.setWord("The");
    the.setNER("ORG");
    CoreLabel abc = new CoreLabel();
    abc.setWord("ABC");
    abc.setNER("ORG");
    Annotation document = new Annotation("The ABC");
    document.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(the, abc));

    new TokensRegexNERAnnotator(mapping.toString()).annotate(document);

    assertEquals("ORG", the.ner());
    assertEquals("ORG", abc.ner());
  }

  @Test
  public void locationAndGpeSequenceUsesSpecialOverwriteRule() throws Exception {
    Path mapping = Files.createTempFile("tokensregexner-location", ".tab");
    Files.write(mapping, Collections.singletonList("New York\tMETRO"), StandardCharsets.UTF_8);
    mapping.toFile().deleteOnExit();
    CoreLabel one = new CoreLabel();
    one.setWord("New");
    one.setNER("LOCATION");
    CoreLabel two = new CoreLabel();
    two.setWord("York");
    two.setNER("GPE");
    Annotation document = new Annotation("New York");
    document.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(one, two));

    new TokensRegexNERAnnotator(mapping.toString()).annotate(document);

    assertEquals("METRO", one.ner());
    assertEquals("METRO", two.ner());
  }

  @Test
  public void tokensRegexGroupAnnotatesOnlyCapturedTokens() throws Exception {
    Path mapping = Files.createTempFile("tokensregexner-group", ".tab");
    Files.write(mapping, Collections.singletonList(
        "( /University/ ( /of/ /Oxford/ ) )\tSCHOOL\tO\t1\t1"), StandardCharsets.UTF_8);
    mapping.toFile().deleteOnExit();
    CoreLabel university = new CoreLabel();
    university.setWord("University");
    university.setNER("O");
    CoreLabel of = new CoreLabel();
    of.setWord("of");
    of.setNER("O");
    CoreLabel oxford = new CoreLabel();
    oxford.setWord("Oxford");
    oxford.setNER("O");
    Annotation document = new Annotation("University of Oxford");
    document.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(university, of, oxford));

    new TokensRegexNERAnnotator(mapping.toString()).annotate(document);

    assertEquals("O", university.ner());
    assertEquals("SCHOOL", of.ner());
    assertEquals("SCHOOL", oxford.ner());
  }

  @Test
  public void atLeastOnePosTagMustMatchByDefault() throws Exception {
    Path mapping = Files.createTempFile("tokensregexner-pos-one", ".tab");
    Files.write(mapping, Collections.singletonList("red fox\tANIMAL\tO"), StandardCharsets.UTF_8);
    mapping.toFile().deleteOnExit();
    CoreLabel red = new CoreLabel();
    red.setWord("red");
    red.setTag("JJ");
    red.setNER("O");
    CoreLabel fox = new CoreLabel();
    fox.setWord("fox");
    fox.setTag("NN");
    fox.setNER("O");
    Annotation document = new Annotation("red fox");
    document.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(red, fox));

    new TokensRegexNERAnnotator(mapping.toString(), false, "NN.*").annotate(document);

    assertEquals("ANIMAL", red.ner());
    assertEquals("ANIMAL", fox.ner());
  }

  @Test
  public void atLeastOnePosPolicyRejectsPhraseWithNoMatchingTag() throws Exception {
    Path mapping = Files.createTempFile("tokensregexner-pos-none", ".tab");
    Files.write(mapping, Collections.singletonList("red fast\tATTRIBUTE\tO"), StandardCharsets.UTF_8);
    mapping.toFile().deleteOnExit();
    CoreLabel red = new CoreLabel();
    red.setWord("red");
    red.setTag("JJ");
    red.setNER("O");
    CoreLabel fast = new CoreLabel();
    fast.setWord("fast");
    fast.setTag("RB");
    fast.setNER("O");
    Annotation document = new Annotation("red fast");
    document.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(red, fast));

    new TokensRegexNERAnnotator(mapping.toString(), false, "NN.*").annotate(document);

    assertEquals("O", red.ner());
    assertEquals("O", fast.ner());
  }

  @Test
  public void allTokensPosPolicyRejectsPartialPosMatch() throws Exception {
    Path mapping = Files.createTempFile("tokensregexner-pos-all", ".tab");
    Files.write(mapping, Collections.singletonList("red fox\tANIMAL\tO"), StandardCharsets.UTF_8);
    mapping.toFile().deleteOnExit();
    CoreLabel red = new CoreLabel();
    red.setWord("red");
    red.setTag("JJ");
    red.setNER("O");
    CoreLabel fox = new CoreLabel();
    fox.setWord("fox");
    fox.setTag("NN");
    fox.setNER("O");
    Annotation document = new Annotation("red fox");
    document.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(red, fox));
    Properties properties = new Properties();
    properties.setProperty("custom.mapping", mapping.toString());
    properties.setProperty("custom.validpospattern", "NN.*");
    properties.setProperty("custom.posmatchtype", "MATCH_ALL_TOKENS");

    new TokensRegexNERAnnotator("custom", properties).annotate(document);

    assertEquals("O", red.ner());
    assertEquals("O", fox.ner());
  }

  @Test
  public void oneTokenPhraseOnlyPolicySkipsPosCheckForMultiTokenPhrase() throws Exception {
    Path mapping = Files.createTempFile("tokensregexner-pos-single-policy", ".tab");
    Files.write(mapping, Collections.singletonList("very fast\tATTRIBUTE\tO"), StandardCharsets.UTF_8);
    mapping.toFile().deleteOnExit();
    CoreLabel very = new CoreLabel();
    very.setWord("very");
    very.setTag("RB");
    very.setNER("O");
    CoreLabel fast = new CoreLabel();
    fast.setWord("fast");
    fast.setTag("RB");
    fast.setNER("O");
    Annotation document = new Annotation("very fast");
    document.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(very, fast));
    Properties properties = new Properties();
    properties.setProperty("custom.mapping", mapping.toString());
    properties.setProperty("custom.validpospattern", "NN.*");
    properties.setProperty("custom.posmatchtype", "MATCH_ONE_TOKEN_PHRASE_ONLY");

    new TokensRegexNERAnnotator("custom", properties).annotate(document);

    assertEquals("ATTRIBUTE", very.ner());
    assertEquals("ATTRIBUTE", fast.ner());
  }

  @Test
  public void commonWordFileSuppressesExactMatchedPhrase() throws Exception {
    Path mapping = Files.createTempFile("tokensregexner-common-map", ".tab");
    Files.write(mapping, Collections.singletonList("May\tMONTH\tO"), StandardCharsets.UTF_8);
    mapping.toFile().deleteOnExit();
    Path commonWords = Files.createTempFile("tokensregexner-common", ".txt");
    Files.write(commonWords, Collections.singletonList("May"), StandardCharsets.UTF_8);
    commonWords.toFile().deleteOnExit();
    CoreLabel may = new CoreLabel();
    may.setWord("May");
    may.setNER("O");
    Annotation document = new Annotation("May");
    document.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(may));
    Properties properties = new Properties();
    properties.setProperty("custom.mapping", mapping.toString());
    properties.setProperty("custom.commonWords", commonWords.toString());

    new TokensRegexNERAnnotator("custom", properties).annotate(document);

    assertEquals("O", may.ner());
  }

  @Test
  public void headerFromFileAndCustomAnswerFieldAreSupported() throws Exception {
    Path mapping = Files.createTempFile("tokensregexner-fields", ".tab");
    Files.write(mapping, Arrays.asList("pattern\tanswer\toverwrite", "Stanford\tUNIVERSITY\tO"),
        StandardCharsets.UTF_8);
    mapping.toFile().deleteOnExit();
    CoreLabel token = new CoreLabel();
    token.setWord("Stanford");
    token.setNER("O");
    token.set(CoreAnnotations.AnswerAnnotation.class, "OLD");
    Annotation document = new Annotation("Stanford");
    document.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    Properties properties = new Properties();
    properties.setProperty("custom.mapping", mapping.toString());
    properties.setProperty("custom.mapping.header", "true");

    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator("custom", properties);
    annotator.annotate(document);

    assertEquals("UNIVERSITY", token.get(CoreAnnotations.AnswerAnnotation.class));
    assertEquals("O", token.ner());
    assertTrue(annotator.requirementsSatisfied().contains(CoreAnnotations.AnswerAnnotation.class));
  }

  @Test
  public void requirementsDescribeInputsAndOutput() throws Exception {
    Path mapping = Files.createTempFile("tokensregexner-requirements", ".tab");
    Files.write(mapping, Collections.singletonList("x\tTHING\tO"), StandardCharsets.UTF_8);
    mapping.toFile().deleteOnExit();
    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(mapping.toString());

    Set<Class<? extends CoreAnnotation>> required = annotator.requires();
    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertEquals(5, required.size());
    assertTrue(required.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.CharacterOffsetEndAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
    assertEquals(Collections.<Class<? extends CoreAnnotation>>singleton(
        CoreAnnotations.NamedEntityTagAnnotation.class), satisfied);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void requirementsSetIsUnmodifiable() throws Exception {
    Path mapping = Files.createTempFile("tokensregexner-unmodifiable", ".tab");
    Files.write(mapping, Collections.singletonList("x\tTHING\tO"), StandardCharsets.UTF_8);
    mapping.toFile().deleteOnExit();
    TokensRegexNERAnnotator annotator = new TokensRegexNERAnnotator(mapping.toString());

    annotator.requires().clear();
  }

  @Test(expected = RuntimeException.class)
  public void annotateRejectsDocumentWithoutSentencesOrTokens() throws Exception {
    Path mapping = Files.createTempFile("tokensregexner-empty-document", ".tab");
    Files.write(mapping, Collections.singletonList("x\tTHING\tO"), StandardCharsets.UTF_8);
    mapping.toFile().deleteOnExit();

    new TokensRegexNERAnnotator(mapping.toString()).annotate(new Annotation(""));
  }

  @Test(expected = IllegalArgumentException.class)
  public void constructorRejectsHeaderWithoutPattern() throws Exception {
    Path mapping = Files.createTempFile("tokensregexner-no-pattern", ".tab");
    Files.write(mapping, Collections.singletonList("THING"), StandardCharsets.UTF_8);
    mapping.toFile().deleteOnExit();
    Properties properties = new Properties();
    properties.setProperty("custom.mapping", mapping.toString());
    properties.setProperty("custom.mapping.header", "ner");

    new TokensRegexNERAnnotator("custom", properties);
  }

  @Test(expected = IllegalArgumentException.class)
  public void constructorRejectsDuplicateHeaderField() throws Exception {
    Path mapping = Files.createTempFile("tokensregexner-duplicate-header", ".tab");
    Files.write(mapping, Collections.singletonList("x\tTHING"), StandardCharsets.UTF_8);
    mapping.toFile().deleteOnExit();
    Properties properties = new Properties();
    properties.setProperty("custom.mapping", mapping.toString());
    properties.setProperty("custom.mapping.header", "pattern,ner,ner");

    new TokensRegexNERAnnotator("custom", properties);
  }

  @Test(expected = IllegalArgumentException.class)
  public void constructorRejectsInvalidPriority() throws Exception {
    Path mapping = Files.createTempFile("tokensregexner-priority-invalid", ".tab");
    Files.write(mapping, Collections.singletonList("x\tTHING\tO\thigh"), StandardCharsets.UTF_8);
    mapping.toFile().deleteOnExit();

    new TokensRegexNERAnnotator(mapping.toString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void constructorRejectsTooManyMappingColumns() throws Exception {
    Path mapping = Files.createTempFile("tokensregexner-columns", ".tab");
    Files.write(mapping, Collections.singletonList("x\tTHING\tO\t1\t0\textra"), StandardCharsets.UTF_8);
    mapping.toFile().deleteOnExit();

    new TokensRegexNERAnnotator(mapping.toString());
  }

  @Test(expected = RuntimeException.class)
  public void constructorRejectsGroupBeyondPatternGroups() throws Exception {
    Path mapping = Files.createTempFile("tokensregexner-bad-group", ".tab");
    Files.write(mapping, Collections.singletonList("x\tTHING\tO\t1\t2"), StandardCharsets.UTF_8);
    mapping.toFile().deleteOnExit();

    new TokensRegexNERAnnotator(mapping.toString());
  }

  @Test
  public void perFileOptionsAllowDifferentCasePolicies() throws Exception {
    Path firstMapping = Files.createTempFile("tokensregexner-first", ".tab");
    Files.write(firstMapping, Collections.singletonList("alpha\tFIRST\tO"), StandardCharsets.UTF_8);
    firstMapping.toFile().deleteOnExit();
    Path secondMapping = Files.createTempFile("tokensregexner-second", ".tab");
    Files.write(secondMapping, Collections.singletonList("beta\tSECOND\tO"), StandardCharsets.UTF_8);
    secondMapping.toFile().deleteOnExit();
    CoreLabel alpha = new CoreLabel();
    alpha.setWord("ALPHA");
    alpha.setNER("O");
    CoreLabel beta = new CoreLabel();
    beta.setWord("BETA");
    beta.setNER("O");
    Annotation document = new Annotation("ALPHA BETA");
    document.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(alpha, beta));
    Properties properties = new Properties();
    properties.setProperty("custom.mapping", "ignorecase=true, " + firstMapping +
        ";ignorecase=false, " + secondMapping);

    new TokensRegexNERAnnotator("custom", properties).annotate(document);

    assertEquals("FIRST", alpha.ner());
    assertEquals("O", beta.ner());
  }
}
