package edu.stanford.nlp.process;

import edu.stanford.nlp.ling.Word;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertThrows;

public class StemmerPromptTest {

  @Test
  public void stemsPluralSuffixVariants() {
    Stemmer stemmer = new Stemmer();

    assertEquals("caress", stemmer.stem("caresses"));
    assertEquals("poni", stemmer.stem("ponies"));
    assertEquals("ti", stemmer.stem("ties"));
    assertEquals("caress", stemmer.stem("caress"));
    assertEquals("cat", stemmer.stem("cats"));
    assertEquals("ga", stemmer.stem("gas"));
  }

  @Test
  public void stemsPastTenseAndEedSuffixes() {
    Stemmer stemmer = new Stemmer();

    assertEquals("feed", stemmer.stem("feed"));
    assertEquals("agre", stemmer.stem("agreed"));
    assertEquals("plaster", stemmer.stem("plastered"));
    assertEquals("bled", stemmer.stem("bled"));
    assertEquals("disabl", stemmer.stem("disabled"));
  }

  @Test
  public void stemsIngSuffixAndAppliesPostProcessingRules() {
    Stemmer stemmer = new Stemmer();

    assertEquals("mat", stemmer.stem("matting"));
    assertEquals("mate", stemmer.stem("mating"));
    assertEquals("meet", stemmer.stem("meeting"));
    assertEquals("mill", stemmer.stem("milling"));
    assertEquals("mess", stemmer.stem("messing"));
    assertEquals("meet", stemmer.stem("meetings"));
    assertEquals("sing", stemmer.stem("sing"));
  }

  @Test
  public void changesTerminalYOnlyWhenStemContainsVowel() {
    Stemmer stemmer = new Stemmer();

    assertEquals("happi", stemmer.stem("happy"));
    assertEquals("sky", stemmer.stem("sky"));
    assertEquals("cry", stemmer.stem("cry"));
    assertEquals("toi", stemmer.stem("toy"));
  }

  @Test
  public void mapsStepThreeSuffixFamilies() {
    Stemmer stemmer = new Stemmer();

    assertEquals("relat", stemmer.stem("relational"));
    assertEquals("condit", stemmer.stem("conditional"));
    assertEquals("ration", stemmer.stem("rational"));
    assertEquals("valenc", stemmer.stem("valenci"));
    assertEquals("hesit", stemmer.stem("hesitanci"));
    assertEquals("digit", stemmer.stem("digitizer"));
  }

  @Test
  public void mapsAdditionalStepThreeSuffixFamilies() {
    Stemmer stemmer = new Stemmer();

    assertEquals("conform", stemmer.stem("conformabli"));
    assertEquals("radic", stemmer.stem("radicalli"));
    assertEquals("differ", stemmer.stem("differentli"));
    assertEquals("vile", stemmer.stem("vileli"));
    assertEquals("analog", stemmer.stem("analogousli"));
    assertEquals("vietnam", stemmer.stem("vietnamization"));
  }

  @Test
  public void mapsNominalAndQualitySuffixFamilies() {
    Stemmer stemmer = new Stemmer();

    assertEquals("predic", stemmer.stem("predication"));
    assertEquals("oper", stemmer.stem("operator"));
    assertEquals("feudal", stemmer.stem("feudalism"));
    assertEquals("decis", stemmer.stem("decisiveness"));
    assertEquals("hope", stemmer.stem("hopefulness"));
    assertEquals("callous", stemmer.stem("callousness"));
  }

  @Test
  public void mapsAbilityAndFormSuffixFamilies() {
    Stemmer stemmer = new Stemmer();

    assertEquals("formal", stemmer.stem("formaliti"));
    assertEquals("sensit", stemmer.stem("sensitiviti"));
    assertEquals("sensibl", stemmer.stem("sensibiliti"));
    assertEquals("triplic", stemmer.stem("triplicate"));
    assertEquals("form", stemmer.stem("formative"));
    assertEquals("formal", stemmer.stem("formalize"));
  }

  @Test
  public void removesNessFulAndIcSuffixFamilies() {
    Stemmer stemmer = new Stemmer();

    assertEquals("electr", stemmer.stem("electriciti"));
    assertEquals("electr", stemmer.stem("electrical"));
    assertEquals("hope", stemmer.stem("hopeful"));
    assertEquals("good", stemmer.stem("goodness"));
  }

  @Test
  public void removesContextDependentSuffixes() {
    Stemmer stemmer = new Stemmer();

    assertEquals("reviv", stemmer.stem("revival"));
    assertEquals("allow", stemmer.stem("allowance"));
    assertEquals("infer", stemmer.stem("inference"));
    assertEquals("airlin", stemmer.stem("airliner"));
    assertEquals("gyroscop", stemmer.stem("gyroscopic"));
    assertEquals("adjust", stemmer.stem("adjustable"));
    assertEquals("defens", stemmer.stem("defensible"));
  }

  @Test
  public void removesMentIonAndOtherContextDependentSuffixes() {
    Stemmer stemmer = new Stemmer();

    assertEquals("irrit", stemmer.stem("irritant"));
    assertEquals("replac", stemmer.stem("replacement"));
    assertEquals("adjust", stemmer.stem("adjustment"));
    assertEquals("depend", stemmer.stem("dependent"));
    assertEquals("adopt", stemmer.stem("adoption"));
    assertEquals("homolog", stemmer.stem("homologou"));
    assertEquals("commun", stemmer.stem("communism"));
  }

  @Test
  public void removesAteItiIveAndIzeSuffixes() {
    Stemmer stemmer = new Stemmer();

    assertEquals("activ", stemmer.stem("activate"));
    assertEquals("angular", stemmer.stem("angulariti"));
    assertEquals("effect", stemmer.stem("effective"));
    assertEquals("bowdler", stemmer.stem("bowdlerize"));
  }

  @Test
  public void handlesFinalEAndDoubleLByMeasure() {
    Stemmer stemmer = new Stemmer();

    assertEquals("probat", stemmer.stem("probate"));
    assertEquals("rate", stemmer.stem("rate"));
    assertEquals("ceas", stemmer.stem("cease"));
    assertEquals("control", stemmer.stem("controll"));
    assertEquals("roll", stemmer.stem("roll"));
  }

  @Test
  public void handlesEmptyAndVeryShortInputs() {
    Stemmer stemmer = new Stemmer();

    assertEquals("", stemmer.stem(""));
    assertEquals("a", stemmer.stem("a"));
    assertEquals("I", stemmer.stem("I"));
    assertEquals("to", stemmer.stem("to"));
    assertEquals("be", stemmer.stem("be"));
  }

  @Test
  public void treatsCaseAndNonLettersLiterally() {
    Stemmer stemmer = new Stemmer();

    assertEquals("CATS", stemmer.stem("CATS"));
    assertEquals("Cat", stemmer.stem("Cats"));
    assertEquals("12345", stemmer.stem("12345"));
    assertEquals("can't", stemmer.stem("can't"));
    assertEquals("cats!", stemmer.stem("cats!"));
  }

  @Test
  public void expandsItsBufferForLongInputAndRemainsReusable() {
    Stemmer stemmer = new Stemmer();
    String longInput = "a".repeat(125);

    assertEquals(longInput, stemmer.stem(longInput));
    assertEquals("cat", stemmer.stem("cats"));
    assertEquals("cat", stemmer.toString());
    assertEquals("", stemmer.stem(""));
    assertEquals("", stemmer.toString());
  }

  @Test
  public void stemsWordWithoutMutatingAndSupportsFunctionInterface() {
    Stemmer stemmer = new Stemmer();
    Word original = new Word("meetings");
    Word directResult = stemmer.stem(original);
    Function<Word, Word> function = stemmer;
    Word functionResult = function.apply(new Word("ponies"));

    assertEquals("meetings", original.word());
    assertEquals("meet", directResult.word());
    assertNotSame(original, directResult);
    assertEquals("poni", functionResult.word());
    assertEquals("poni", stemmer.toString());
  }

  @Test
  public void returnsASeparateWordEvenWhenTextDoesNotChange() {
    Stemmer stemmer = new Stemmer();
    Word original = new Word("sky");
    Word result = stemmer.apply(original);

    assertEquals("sky", result.word());
    assertEquals("sky", original.word());
    assertNotSame(original, result);
  }

  @Test
  public void rejectsNullStringAndNullWords() {
    Stemmer stemmer = new Stemmer();

    assertThrows(NullPointerException.class, () -> stemmer.stem((String) null));
    assertThrows(NullPointerException.class, () -> stemmer.stem((Word) null));
    assertThrows(NullPointerException.class, () -> stemmer.apply(new Word()));
  }

  @Test
  public void commandLineModeStemsEachArgument() throws Exception {
    PrintStream originalOutput = System.out;
    ByteArrayOutputStream captured = new ByteArrayOutputStream();
    try {
      System.setOut(new PrintStream(captured, true, StandardCharsets.UTF_8.name()));
      Stemmer.main(new String[] {"caresses", "ponies", "meeting"});
    } finally {
      System.setOut(originalOutput);
    }

    assertEquals("caress poni meet " + System.lineSeparator(),
        captured.toString(StandardCharsets.UTF_8.name()));
  }

  @Test
  public void commandLineFileModeTokenizesAndStemsText() throws Exception {
    Path input = Files.createTempFile("stemmer-prompt-", ".txt");
    PrintStream originalOutput = System.out;
    ByteArrayOutputStream captured = new ByteArrayOutputStream();
    try {
      Files.write(input, "Caresses ponies meeting.".getBytes(StandardCharsets.UTF_8));
      System.setOut(new PrintStream(captured, true, StandardCharsets.UTF_8.name()));
      Stemmer.main(new String[] {"-file", input.toString()});
    } finally {
      System.setOut(originalOutput);
      Files.deleteIfExists(input);
    }

    assertEquals("Caress poni meet . " + System.lineSeparator(),
        captured.toString(StandardCharsets.UTF_8.name()));
  }

  @Test
  public void commandLineRejectsMissingArguments() {
    assertThrows(ArrayIndexOutOfBoundsException.class,
        () -> Stemmer.main(new String[0]));
    assertThrows(ArrayIndexOutOfBoundsException.class,
        () -> Stemmer.main(new String[] {"-file"}));
  }
}
