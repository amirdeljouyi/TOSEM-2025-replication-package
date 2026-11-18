package opennlp.tools.stemmer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PorterStemmerPromptTest {

  @Test
  public void stemStringHandlesPluralForms() {
    PorterStemmer stemmer = new PorterStemmer();

    assertEquals("caress", stemmer.stem("caresses"));
    assertEquals("poni", stemmer.stem("ponies"));
    assertEquals("ti", stemmer.stem("ties"));
    assertEquals("caress", stemmer.stem("caress"));
    assertEquals("cat", stemmer.stem("cats"));
  }

  @Test
  public void stemStringHandlesPastTenseAndGerunds() {
    PorterStemmer stemmer = new PorterStemmer();

    assertEquals("feed", stemmer.stem("feed"));
    assertEquals("agre", stemmer.stem("agreed"));
    assertEquals("disabl", stemmer.stem("disabled"));
    assertEquals("mat", stemmer.stem("matting"));
    assertEquals("mate", stemmer.stem("mating"));
    assertEquals("meet", stemmer.stem("meeting"));
    assertEquals("mill", stemmer.stem("milling"));
    assertEquals("mess", stemmer.stem("messing"));
  }

  @Test
  public void stemStringAppliesTerminalYRules() {
    PorterStemmer stemmer = new PorterStemmer();

    assertEquals("happi", stemmer.stem("happy"));
    assertEquals("sky", stemmer.stem("sky"));
    assertEquals("toi", stemmer.stem("toy"));
    assertEquals("y", stemmer.stem("y"));
  }

  @Test
  public void stemStringHandlesStepThreeMappings() {
    PorterStemmer stemmer = new PorterStemmer();

    assertEquals("relat", stemmer.stem("relational"));
    assertEquals("condit", stemmer.stem("conditional"));
    assertEquals("ration", stemmer.stem("rational"));
    assertEquals("valenc", stemmer.stem("valenci"));
    assertEquals("hesit", stemmer.stem("hesitanci"));
    assertEquals("digit", stemmer.stem("digitizer"));
    assertEquals("conform", stemmer.stem("conformabli"));
    assertEquals("radic", stemmer.stem("radicalli"));
    assertEquals("differ", stemmer.stem("differentli"));
    assertEquals("vile", stemmer.stem("vileli"));
    assertEquals("analog", stemmer.stem("analogousli"));
  }

  @Test
  public void stemStringHandlesAdditionalStepThreeMappings() {
    PorterStemmer stemmer = new PorterStemmer();

    assertEquals("vietnam", stemmer.stem("vietnamization"));
    assertEquals("predic", stemmer.stem("predication"));
    assertEquals("oper", stemmer.stem("operator"));
    assertEquals("feudal", stemmer.stem("feudalism"));
    assertEquals("decis", stemmer.stem("decisiveness"));
    assertEquals("hope", stemmer.stem("hopefulness"));
    assertEquals("callous", stemmer.stem("callousness"));
    assertEquals("formal", stemmer.stem("formaliti"));
    assertEquals("sensit", stemmer.stem("sensitiviti"));
    assertEquals("sensibl", stemmer.stem("sensibiliti"));
  }

  @Test
  public void stemStringHandlesStepFourMappings() {
    PorterStemmer stemmer = new PorterStemmer();

    assertEquals("triplic", stemmer.stem("triplicate"));
    assertEquals("form", stemmer.stem("formative"));
    assertEquals("formal", stemmer.stem("formalize"));
    assertEquals("electr", stemmer.stem("electriciti"));
    assertEquals("electr", stemmer.stem("electrical"));
    assertEquals("hope", stemmer.stem("hopeful"));
    assertEquals("good", stemmer.stem("goodness"));
  }

  @Test
  public void stemStringHandlesStepFiveSuffixRemoval() {
    PorterStemmer stemmer = new PorterStemmer();

    assertEquals("reviv", stemmer.stem("revival"));
    assertEquals("allow", stemmer.stem("allowance"));
    assertEquals("infer", stemmer.stem("inference"));
    assertEquals("airlin", stemmer.stem("airliner"));
    assertEquals("gyroscop", stemmer.stem("gyroscopic"));
    assertEquals("adjust", stemmer.stem("adjustable"));
    assertEquals("defens", stemmer.stem("defensible"));
    assertEquals("irrit", stemmer.stem("irritant"));
    assertEquals("replac", stemmer.stem("replacement"));
    assertEquals("adjust", stemmer.stem("adjustment"));
    assertEquals("depend", stemmer.stem("dependent"));
  }

  @Test
  public void stemStringHandlesStepFiveIonAndOtherSuffixes() {
    PorterStemmer stemmer = new PorterStemmer();

    assertEquals("adopt", stemmer.stem("adoption"));
    assertEquals("homolog", stemmer.stem("homologou"));
    assertEquals("commun", stemmer.stem("communism"));
    assertEquals("activ", stemmer.stem("activate"));
    assertEquals("angular", stemmer.stem("angulariti"));
    assertEquals("homolog", stemmer.stem("homologous"));
    assertEquals("effect", stemmer.stem("effective"));
    assertEquals("bowdler", stemmer.stem("bowdlerize"));
  }

  @Test
  public void stemStringHandlesFinalEAndDoubleLRules() {
    PorterStemmer stemmer = new PorterStemmer();

    assertEquals("probat", stemmer.stem("probate"));
    assertEquals("rate", stemmer.stem("rate"));
    assertEquals("ceas", stemmer.stem("cease"));
    assertEquals("control", stemmer.stem("controll"));
    assertEquals("roll", stemmer.stem("roll"));
  }

  @Test
  public void stemCharSequenceAcceptsNonStringImplementations() {
    PorterStemmer stemmer = new PorterStemmer();
    StringBuilder builder = new StringBuilder("meetings");
    StringBuffer buffer = new StringBuffer("relational");

    CharSequence first = stemmer.stem((CharSequence) builder);
    CharSequence second = stemmer.stem((CharSequence) buffer);

    assertEquals("meet", first.toString());
    assertEquals("relat", second.toString());
    assertEquals("meetings", builder.toString());
    assertEquals("relational", buffer.toString());
  }

  @Test
  public void unchangedStringIsReturnedByIdentity() {
    PorterStemmer stemmer = new PorterStemmer();
    String unchanged = new String("sky");
    String changed = new String("ponies");

    String unchangedResult = stemmer.stem(unchanged);
    String changedResult = stemmer.stem(changed);

    assertSame(unchanged, unchangedResult);
    assertNotSame(changed, changedResult);
    assertEquals("poni", changedResult);
  }

  @Test
  public void wholeCharacterArrayReportsWhetherResultChanged() {
    PorterStemmer stemmer = new PorterStemmer();
    char[] changed = {'c', 'a', 'r', 'e', 's', 's', 'e', 's'};
    char[] unchanged = {'s', 'k', 'y'};

    assertTrue(stemmer.stem(changed));
    assertEquals("caress", stemmer.toString());
    assertFalse(stemmer.stem(unchanged));
    assertEquals("sky", stemmer.toString());
  }

  @Test
  public void leadingCharacterArrayLengthLimitsInput() {
    PorterStemmer stemmer = new PorterStemmer();
    char[] input = {'m', 'e', 'e', 't', 'i', 'n', 'g', 's', 'X', 'X'};

    boolean changed = stemmer.stem(input, 8);

    assertTrue(changed);
    assertEquals("meet", stemmer.toString());
    assertEquals(4, stemmer.getResultLength());
    assertEquals('X', input[8]);
    assertEquals('X', input[9]);
  }

  @Test
  public void offsetCharacterArrayStemsOnlySelectedSlice() {
    PorterStemmer stemmer = new PorterStemmer();
    char[] input = {'#', '#', 'r', 'e', 'l', 'a', 't', 'i', 'o', 'n', 'a', 'l', '!', '!'};

    boolean changed = stemmer.stem(input, 2, 10);

    assertTrue(changed);
    assertEquals("relat", stemmer.toString());
    assertEquals("##relational!!", new String(input));
  }

  @Test
  public void resultBufferAndLengthExposeOnlyTheLogicalResult() {
    PorterStemmer stemmer = new PorterStemmer();
    char[] input = "disabled".toCharArray();

    assertTrue(stemmer.stem(input));
    char[] resultBuffer = stemmer.getResultBuffer();
    int resultLength = stemmer.getResultLength();

    assertEquals(6, resultLength);
    assertEquals("disabl", new String(resultBuffer, 0, resultLength));
    assertSame(resultBuffer, stemmer.getResultBuffer());
    assertEquals(stemmer.toString(), new String(resultBuffer, 0, resultLength));
  }

  @Test
  public void incrementalAddStemAndResetSupportSequentialWords() {
    PorterStemmer stemmer = new PorterStemmer();
    stemmer.add('m');
    stemmer.add('a');
    stemmer.add('t');
    stemmer.add('i');
    stemmer.add('n');
    stemmer.add('g');

    assertTrue(stemmer.stem());
    assertEquals("mate", stemmer.toString());

    stemmer.reset();
    stemmer.add('s');
    stemmer.add('k');
    stemmer.add('y');

    assertFalse(stemmer.stem());
    assertEquals("sky", stemmer.toString());
  }

  @Test
  public void addExpandsInternalCapacityWithoutLosingCharacters() {
    PorterStemmer stemmer = new PorterStemmer();
    stemmer.add('a'); stemmer.add('a'); stemmer.add('a'); stemmer.add('a'); stemmer.add('a');
    stemmer.add('a'); stemmer.add('a'); stemmer.add('a'); stemmer.add('a'); stemmer.add('a');
    stemmer.add('a'); stemmer.add('a'); stemmer.add('a'); stemmer.add('a'); stemmer.add('a');
    stemmer.add('a'); stemmer.add('a'); stemmer.add('a'); stemmer.add('a'); stemmer.add('a');
    stemmer.add('a'); stemmer.add('a'); stemmer.add('a'); stemmer.add('a'); stemmer.add('a');
    stemmer.add('a'); stemmer.add('a'); stemmer.add('a'); stemmer.add('a'); stemmer.add('a');
    stemmer.add('a'); stemmer.add('a'); stemmer.add('a'); stemmer.add('a'); stemmer.add('a');
    stemmer.add('a'); stemmer.add('a'); stemmer.add('a'); stemmer.add('a'); stemmer.add('a');
    stemmer.add('a'); stemmer.add('a'); stemmer.add('a'); stemmer.add('a'); stemmer.add('a');
    stemmer.add('a'); stemmer.add('a'); stemmer.add('a'); stemmer.add('a'); stemmer.add('a');
    stemmer.add('s');

    assertTrue(stemmer.stem());
    assertEquals(50, stemmer.getResultLength());
    assertEquals("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", stemmer.toString());
  }

  @Test
  public void stemWithNonzeroStartPreservesPrefix() {
    PorterStemmer stemmer = new PorterStemmer();
    stemmer.add('p');
    stemmer.add('r');
    stemmer.add('e');
    stemmer.add('c');
    stemmer.add('a');
    stemmer.add('r');
    stemmer.add('e');
    stemmer.add('s');
    stemmer.add('s');
    stemmer.add('e');
    stemmer.add('s');

    boolean changed = stemmer.stem(3);

    assertTrue(changed);
    assertEquals("precaress", stemmer.toString());
  }

  @Test
  public void emptyAndVeryShortInputsRemainUnchanged() {
    PorterStemmer stemmer = new PorterStemmer();
    String empty = new String("");
    String one = new String("a");
    String two = new String("is");

    assertSame(empty, stemmer.stem(empty));
    assertSame(one, stemmer.stem(one));
    assertSame(two, stemmer.stem(two));
    assertEquals("is", stemmer.toString());
    assertEquals(2, stemmer.getResultLength());
  }

  @Test
  public void bugRegressionInputsDoNotReadBeforeBufferStart() {
    PorterStemmer stemmer = new PorterStemmer();

    assertEquals("a", stemmer.stem("aed"));
    assertEquals("eed", stemmer.stem("eed"));
    assertEquals("o", stemmer.stem("oed"));
    assertEquals("ion", stemmer.stem("ion"));
  }

  @Test
  public void caseAndPunctuationAreNotNormalized() {
    PorterStemmer stemmer = new PorterStemmer();

    assertEquals("CATS", stemmer.stem("CATS"));
    assertEquals("cats!", stemmer.stem("cats!"));
    assertEquals("Cat", stemmer.stem("Cats"));
  }

  @Test
  public void nullInputsFailFast() {
    PorterStemmer stemmer = new PorterStemmer();

    assertThrows(NullPointerException.class, () -> stemmer.stem((String) null));
    assertThrows(NullPointerException.class, () -> stemmer.stem((CharSequence) null));
    assertThrows(NullPointerException.class, () -> stemmer.stem((char[]) null));
  }

  @Test
  public void invalidArrayRangesAreRejected() {
    PorterStemmer stemmer = new PorterStemmer();
    char[] input = {'c', 'a', 't', 's'};

    assertThrows(IndexOutOfBoundsException.class, () -> stemmer.stem(input, -1, 2));
    assertThrows(IndexOutOfBoundsException.class, () -> stemmer.stem(input, 3, 2));
    assertThrows(IndexOutOfBoundsException.class, () -> stemmer.stem(input, 5));
  }
}
