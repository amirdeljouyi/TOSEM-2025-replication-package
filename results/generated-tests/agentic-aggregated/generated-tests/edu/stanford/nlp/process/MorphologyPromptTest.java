package edu.stanford.nlp.process;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.ling.WordLemmaTag;
import edu.stanford.nlp.ling.WordTag;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.*;

public class MorphologyPromptTest {

  @Test
  public void stemStringRemovesCommonInflections() {
    Morphology morphology = new Morphology();

    assertEquals("run", morphology.stem("running"));
    assertEquals("walk", morphology.stem("walked"));
    assertEquals("box", morphology.stem("boxes"));
    assertEquals("happiest", morphology.stem("happiest"));
  }

  @Test
  public void stemWordReturnsNewWordWithoutChangingInput() {
    Morphology morphology = new Morphology();
    Word input = new Word("swimming");

    Word result = morphology.stem(input);

    assertEquals("swim", result.value());
    assertEquals("swimming", input.value());
    assertNotSame(input, result);
  }

  @Test
  public void lemmaUsesPartOfSpeechToResolveAmbiguity() {
    Morphology morphology = new Morphology();

    assertEquals("see", morphology.lemma("saw", "VBD"));
    assertEquals("saw", morphology.lemma("saw", "NN"));
    assertEquals("study", morphology.lemma("studies", "VBZ"));
    assertEquals("study", morphology.lemma("studies", "NNS"));
  }

  @Test
  public void lemmaHandlesIrregularNounsAndVerbs() {
    Morphology morphology = new Morphology();

    assertEquals("goose", morphology.lemma("geese", "NNS"));
    assertEquals("child", morphology.lemma("children", "NNS"));
    assertEquals("mouse", morphology.lemma("mice", "NNS"));
    assertEquals("go", morphology.lemma("went", "VBD"));
    assertEquals("be", morphology.lemma("was", "VBD"));
    assertEquals("have", morphology.lemma("had", "VBD"));
  }

  @Test
  public void lemmaHandlesComparativeAndSuperlativeForms() {
    Morphology morphology = new Morphology();

    assertEquals("good", morphology.lemma("better", "JJR"));
    assertEquals("good", morphology.lemma("best", "JJS"));
    assertEquals("happy", morphology.lemma("happier", "JJR"));
    assertEquals("quick", morphology.lemma("quickest", "JJS"));
  }

  @Test
  public void explicitLowercaseFlagControlsOrdinaryWords() {
    Morphology morphology = new Morphology();

    assertEquals("dog", morphology.lemma("DOGS", "NNS", true));
    assertEquals("DOG", morphology.lemma("DOGS", "NNS", false));
  }

  @Test
  public void properNounPreservesCaseEvenWhenLowercasingEnabled() {
    Morphology morphology = new Morphology();

    assertEquals("Stanford", morphology.lemma("Stanford", "NNP", true));
    assertEquals("McDonalds", morphology.lemma("McDonalds", "NNPS", true));
  }

  @Test
  public void forbiddenSeparatorCharactersAreRestoredAfterLemma() {
    Morphology morphology = new Morphology();

    assertEquals("ice_creams", morphology.lemma("ice_creams", "NNS"));
    assertEquals("New York", morphology.lemma("New York", "NNP"));
    assertEquals("line\nbreaks", morphology.lemma("line\nbreaks", "NNS"));
  }

  @Test
  public void stemCoreLabelAddsDefaultLemmaAndPreservesTokenData() {
    Morphology morphology = new Morphology();
    CoreLabel label = new CoreLabel();
    label.setWord("running");
    label.setTag("VBG");

    morphology.stem(label);

    assertEquals("run", label.lemma());
    assertEquals("running", label.word());
    assertEquals("VBG", label.tag());
  }

  @Test
  public void stemCoreLabelCanWriteARequestedAnnotation() {
    Morphology morphology = new Morphology();
    CoreLabel label = new CoreLabel();
    label.setWord("cars");
    label.setTag("NNS");
    label.setLemma("existing");

    morphology.stem(label, CoreAnnotations.TrueCaseTextAnnotation.class);

    assertEquals("car", label.get(CoreAnnotations.TrueCaseTextAnnotation.class));
    assertEquals("existing", label.lemma());
  }

  @Test
  public void applyTransformsWordTagAndKeepsTag() {
    Morphology morphology = new Morphology();
    WordTag input = new WordTag("driving", "VBG");

    Object value = morphology.apply(input);

    assertTrue(value instanceof WordTag);
    assertEquals("drive", ((WordTag) value).word());
    assertEquals("VBG", ((WordTag) value).tag());
    assertNotSame(input, value);
    assertEquals("driving", input.word());
  }

  @Test
  public void applyTransformsWordAndReturnsNewObject() {
    Morphology morphology = new Morphology();
    Word input = new Word("jumped");

    Object value = morphology.apply(input);

    assertTrue(value instanceof Word);
    assertEquals("jump", ((Word) value).value());
    assertNotSame(input, value);
  }

  @Test
  public void applyReturnsUnsupportedObjectUnchanged() {
    Morphology morphology = new Morphology();
    Object input = new StringBuilder("untouched");

    Object value = morphology.apply(input);

    assertSame(input, value);
    assertNull(morphology.apply(null));
  }

  @Test
  public void lemmatizeBuildsCompleteWordLemmaTag() {
    Morphology morphology = new Morphology();
    WordTag input = new WordTag("written", "VBN");

    WordLemmaTag result = morphology.lemmatize(input);

    assertEquals("written", result.word());
    assertEquals("write", result.lemma());
    assertEquals("VBN", result.tag());
    assertEquals("written/write/VBN", result.toString());
  }

  @Test
  public void staticStemOverloadsProduceEquivalentWordTags() {
    WordTag fromStrings = Morphology.stemStatic("feet", "NNS");
    WordTag fromObject = Morphology.stemStatic(new WordTag("feet", "NNS"));

    assertEquals(new WordTag("foot", "NNS"), fromStrings);
    assertEquals(fromStrings, fromObject);
  }

  @Test
  public void staticLemmaHonorsDefaultAndExplicitCasePolicies() {
    assertEquals("cat", Morphology.lemmaStatic("CATS", "NNS"));
    assertEquals("cat", Morphology.lemmaStatic("CATS", "NNS", true));
    assertEquals("CAT", Morphology.lemmaStatic("CATS", "NNS", false));
    assertEquals("London", Morphology.lemmaStatic("London", "NNP"));
  }

  @Test
  public void staticLemmatizePreservesSurfaceWordAndTag() {
    WordTag input = new WordTag("bought", "VBD");

    WordLemmaTag result = Morphology.lemmatizeStatic(input);

    assertEquals("bought", result.word());
    assertEquals("buy", result.lemma());
    assertEquals("VBD", result.tag());
    assertEquals("bought", input.word());
  }

  @Test
  public void callsAcrossInstanceAndStaticApisDoNotLeakLexerState() {
    Morphology first = new Morphology();
    Morphology second = new Morphology();

    assertEquals("run", first.lemma("RUNNING", "VBG", true));
    assertEquals("WALK", second.lemma("WALKED", "VBD", false));
    assertEquals("mouse", Morphology.stemStatic("mice", "NNS").word());
    assertEquals("DRIVe", first.lemma("DRIVING", "VBG", false));
    assertEquals("jump", second.lemma("JUMPED", "VBD", true));
    assertEquals("child", Morphology.lemmaStatic("CHILDREN", "NNS"));
  }

  @Test
  public void readerConstructorProducesStemmedTaggedStream() throws Exception {
    Morphology morphology = new Morphology(new StringReader("dogs_NNS ran_VBD"));

    assertEquals("dog", morphology.next().value());
    assertEquals("_NNS", morphology.next().value());
    assertEquals(" ", morphology.next().value());
    assertEquals("run", morphology.next().value());
    assertEquals("_VBD", morphology.next().value());
    assertNull(morphology.next());
  }

  @Test
  public void readerConstructorWithNoTagOutputReturnsWordsAndSpacing() throws Exception {
    Morphology morphology = new Morphology(new StringReader("DOGS_NNS walked_VBD"), 2);

    assertEquals("dog", morphology.next().value());
    assertEquals(" ", morphology.next().value());
    assertEquals("walk", morphology.next().value());
    assertNull(morphology.next());
  }

  @Test
  public void readerFlagsCanPreserveCaseAndPrintAffixes() throws Exception {
    Morphology morphology = new Morphology(new StringReader("CATS_NNS"), 5);

    assertEquals("CAT+s", morphology.next().value());
    assertEquals("_NNS", morphology.next().value());
    assertNull(morphology.next());
  }

  @Test
  public void emptyReaderImmediatelyReachesEndOfInput() throws Exception {
    Morphology morphology = new Morphology(new StringReader(""));

    assertNull(morphology.next());
    assertNull(morphology.next());
  }

  @Test(expected = NullPointerException.class)
  public void stemWordRejectsNullInput() {
    Morphology morphology = new Morphology();

    morphology.stem((Word) null);
  }

  @Test(expected = NullPointerException.class)
  public void lemmaRejectsNullSurfaceWord() {
    Morphology morphology = new Morphology();

    morphology.lemma(null, "NN");
  }

  @Test(expected = NullPointerException.class)
  public void stemCoreLabelRequiresTextAnnotation() {
    Morphology morphology = new Morphology();
    CoreLabel label = new CoreLabel();
    label.setTag("NNS");

    morphology.stem(label);
  }

  @Test
  public void mainStemModeProcessesTaggedAndUntaggedArguments() throws Exception {
    PrintStream original = System.out;
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    try {
      System.setOut(new PrintStream(bytes, true, StandardCharsets.UTF_8.name()));
      Morphology.main(new String[]{"-stem", "running_VBG", "geese_NNS", "walked"});
    } finally {
      System.setOut(original);
    }

    String output = bytes.toString(StandardCharsets.UTF_8.name());
    assertTrue(output.contains("running_VBG --> run/VBG"));
    assertTrue(output.contains("geese_NNS --> goose/NNS"));
    assertTrue(output.contains("walked --> walked"));
  }

  @Test
  public void mainRebuildVerbTableFormatsInputWords() throws Exception {
    Path input = Files.createTempFile("morphology-verbs", ".txt");
    Files.write(input, "run swim fly\nwrite".getBytes(StandardCharsets.UTF_8));
    PrintStream original = System.out;
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    try {
      System.setOut(new PrintStream(bytes, true, StandardCharsets.UTF_8.name()));
      Morphology.main(new String[]{"-rebuildVerbTable", input.toString()});
    } finally {
      System.setOut(original);
      Files.deleteIfExists(input);
    }

    assertEquals(" private static final String[] verbStems = { \"run\", " + System.lineSeparator() +
        "    \"swim\", \"fly\", \"write\" };" +
        System.lineSeparator(), bytes.toString(StandardCharsets.UTF_8.name()));
  }

  @Test
  public void mainFileModeAppliesNumericFlags() throws Exception {
    Path input = Files.createTempFile("morphology-input", ".txt");
    Files.write(input, "CATS_NNS".getBytes(StandardCharsets.UTF_8));
    PrintStream original = System.out;
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    try {
      System.setOut(new PrintStream(bytes, true, StandardCharsets.UTF_8.name()));
      Morphology.main(new String[]{"-5", input.toString()});
    } finally {
      System.setOut(original);
      Files.deleteIfExists(input);
    }

    assertEquals("CAT+s_NNS", bytes.toString(StandardCharsets.UTF_8.name()));
  }
}
