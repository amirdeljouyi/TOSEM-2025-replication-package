package opennlp.tools.stemmer.snowball;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import opennlp.tools.stemmer.Stemmer;
import org.junit.Test;

public class greekStemmerPromptTest {

  @Test
  public void testNewInstanceStartsWithEmptyCurrentValue() {
    greekStemmer stemmer = new greekStemmer();

    assertEquals("", stemmer.getCurrent());
    assertFalse(stemmer.stem());
    assertEquals("", stemmer.getCurrent());
  }

  @Test
  public void testInputsShorterThanThreeCodeUnitsAreNotStemmed() {
    greekStemmer stemmer = new greekStemmer();
    stemmer.setCurrent("α");
    assertFalse(stemmer.stem());
    assertEquals("α", stemmer.getCurrent());

    stemmer.setCurrent("ΑΒ");
    assertFalse(stemmer.stem());
    assertEquals("αβ", stemmer.getCurrent());

    stemmer.setCurrent("😀");
    assertFalse(stemmer.stem());
    assertEquals("😀", stemmer.getCurrent());
  }

  @Test
  public void testMinimumLengthAndNonGreekTextBehavior() {
    greekStemmer stemmer = new greekStemmer();
    stemmer.setCurrent("ABC");
    assertTrue(stemmer.stem());
    assertEquals("ABC", stemmer.getCurrent());

    stemmer.setCurrent("abc123");
    assertTrue(stemmer.stem());
    assertEquals("abc123", stemmer.getCurrent());
  }

  @Test
  public void testUppercaseAndAccentedGreekAreNormalized() {
    greekStemmer stemmer = new greekStemmer();
    stemmer.setCurrent("ΣΠΙΤΙ");
    assertTrue(stemmer.stem());
    assertEquals("σπιτ", stemmer.getCurrent());

    stemmer.setCurrent("ΆΈΉΊΌΎΏ");
    assertTrue(stemmer.stem());
    assertEquals("αεηιου", stemmer.getCurrent());

    stemmer.setCurrent("ΐΰϊϋς");
    assertTrue(stemmer.stem());
    assertEquals("ηυη", stemmer.getCurrent());
  }

  @Test
  public void testExceptionalNounSuffixReplacements() {
    greekStemmer stemmer = new greekStemmer();
    stemmer.setCurrent("φαγια");
    assertTrue(stemmer.stem());
    assertEquals("φα", stemmer.getCurrent());

    stemmer.setCurrent("βιολογιων");
    assertTrue(stemmer.stem());
    assertEquals("βιολο", stemmer.getCurrent());

    stemmer.setCurrent("καθεστωτων");
    assertTrue(stemmer.stem());
    assertEquals("καθεστ", stemmer.getCurrent());
  }

  @Test
  public void testExceptionalNeuterNounFamilies() {
    greekStemmer stemmer = new greekStemmer();
    stemmer.setCurrent("κρεατων");
    assertTrue(stemmer.stem());
    assertEquals("κρε", stemmer.getCurrent());

    stemmer.setCurrent("περατος");
    assertTrue(stemmer.stem());
    assertEquals("περ", stemmer.getCurrent());

    stemmer.setCurrent("γεγονοτος");
    assertTrue(stemmer.stem());
    assertEquals("γεγον", stemmer.getCurrent());
  }

  @Test
  public void testIzoVerbSuffixes() {
    greekStemmer stemmer = new greekStemmer();
    stemmer.setCurrent("παριζα");
    assertTrue(stemmer.stem());
    assertEquals("παρ", stemmer.getCurrent());

    stemmer.setCurrent("βαθριζουμε");
    assertTrue(stemmer.stem());
    assertEquals("βαθρ", stemmer.getCurrent());

    stemmer.setCurrent("χαλιζεις");
    assertTrue(stemmer.stem());
    assertEquals("χαλ", stemmer.getCurrent());
  }

  @Test
  public void testPassiveAndIsaVerbSuffixes() {
    greekStemmer stemmer = new greekStemmer();
    stemmer.setCurrent("βιωθηκα");
    assertTrue(stemmer.stem());
    assertEquals("β", stemmer.getCurrent());

    stemmer.setCurrent("σωθηκες");
    assertTrue(stemmer.stem());
    assertEquals("σων", stemmer.getCurrent());

    stemmer.setCurrent("παρισα");
    assertTrue(stemmer.stem());
    assertEquals("παρ", stemmer.getCurrent());
  }

  @Test
  public void testIsoAndIstSuffixFamilies() {
    greekStemmer stemmer = new greekStemmer();
    stemmer.setCurrent("κλεισαμε");
    assertTrue(stemmer.stem());
    assertEquals("κλει", stemmer.getCurrent());

    stemmer.setCurrent("επαλισω");
    assertTrue(stemmer.stem());
    assertEquals("επαλ", stemmer.getCurrent());

    stemmer.setCurrent("παιριστα");
    assertTrue(stemmer.stem());
    assertEquals("παιρ", stemmer.getCurrent());
  }

  @Test
  public void testIsmosSuffixWithSpecialPredecessors() {
    greekStemmer stemmer = new greekStemmer();
    stemmer.setCurrent("αγνωστικισμος");
    assertTrue(stemmer.stem());
    assertEquals("αγνωστ", stemmer.getCurrent());

    stemmer.setCurrent("εθνικισμοι");
    assertTrue(stemmer.stem());
    assertEquals("εθν", stemmer.getCurrent());

    stemmer.setCurrent("θεατρινισμου");
    assertTrue(stemmer.stem());
    assertEquals("θεατρ", stemmer.getCurrent());
  }

  @Test
  public void testDiminutiveSuffixFamilies() {
    greekStemmer stemmer = new greekStemmer();
    stemmer.setCurrent("σαρακια");
    assertTrue(stemmer.stem());
    assertEquals("σαρακ", stemmer.getCurrent());

    stemmer.setCurrent("καρδακια");
    assertTrue(stemmer.stem());
    assertEquals("καρδιτσ", stemmer.getCurrent());

    stemmer.setCurrent("λυκισκος");
    assertTrue(stemmer.stem());
    assertEquals("λυκισκ", stemmer.getCurrent());
  }

  @Test
  public void testIdiaAndGeneralNominalSuffixes() {
    greekStemmer stemmer = new greekStemmer();
    stemmer.setCurrent("ψαλιδια");
    assertTrue(stemmer.stem());
    assertEquals("ψαλιδ", stemmer.getCurrent());

    stemmer.setCurrent("παιχνιδιων");
    assertTrue(stemmer.stem());
    assertEquals("παιχνιδ", stemmer.getCurrent());

    stemmer.setCurrent("πατεραδων");
    assertTrue(stemmer.stem());
    assertEquals("πατερ", stemmer.getCurrent());
  }

  @Test
  public void testAdesEdesOudesAndEonSuffixes() {
    greekStemmer stemmer = new greekStemmer();
    stemmer.setCurrent("φιλαδες");
    assertTrue(stemmer.stem());
    assertEquals("φιλαδ", stemmer.getCurrent());

    stemmer.setCurrent("μιλεδων");
    assertTrue(stemmer.stem());
    assertEquals("μιλεδ", stemmer.getCurrent());

    stemmer.setCurrent("τραγουδες");
    assertTrue(stemmer.stem());
    assertEquals("τραγουδ", stemmer.getCurrent());

    stemmer.setCurrent("δεων");
    assertTrue(stemmer.stem());
    assertEquals("δε", stemmer.getCurrent());
  }

  @Test
  public void testIaAndIkaSuffixes() {
    greekStemmer stemmer = new greekStemmer();
    stemmer.setCurrent("παρια");
    assertTrue(stemmer.stem());
    assertEquals("παρ", stemmer.getCurrent());

    stemmer.setCurrent("καρικα");
    assertTrue(stemmer.stem());
    assertEquals("καρ", stemmer.getCurrent());
  }

  @Test
  public void testFirstPersonPluralVerbSuffixes() {
    greekStemmer stemmer = new greekStemmer();
    stemmer.setCurrent("αγαπησαμε");
    assertTrue(stemmer.stem());
    assertEquals("αγαπ", stemmer.getCurrent());

    stemmer.setCurrent("αγαπαμε");
    assertTrue(stemmer.stem());
    assertEquals("αγαπ", stemmer.getCurrent());

    stemmer.setCurrent("βουβαμε");
    assertTrue(stemmer.stem());
    assertEquals("βουβαμ", stemmer.getCurrent());
  }

  @Test
  public void testThirdPersonPluralAndSecondPersonVerbSuffixes() {
    greekStemmer stemmer = new greekStemmer();
    stemmer.setCurrent("αγαπησανε");
    assertTrue(stemmer.stem());
    assertEquals("αγαπ", stemmer.getCurrent());

    stemmer.setCurrent("μαθανε");
    assertTrue(stemmer.stem());
    assertEquals("μαθ", stemmer.getCurrent());

    stemmer.setCurrent("γραφετε");
    assertTrue(stemmer.stem());
    assertEquals("γραφ", stemmer.getCurrent());
  }

  @Test
  public void testParticipleAndReflexiveSuffixes() {
    greekStemmer stemmer = new greekStemmer();
    stemmer.setCurrent("αρχοντας");
    assertTrue(stemmer.stem());
    assertEquals("αρχοντ", stemmer.getCurrent());

    stemmer.setCurrent("κρεωντας");
    assertTrue(stemmer.stem());
    assertEquals("κρεωντ", stemmer.getCurrent());

    stemmer.setCurrent("ονιομαστε");
    assertTrue(stemmer.stem());
    assertEquals("ονομαστ", stemmer.getCurrent());
  }

  @Test
  public void testEsteAndPastTenseSuffixes() {
    greekStemmer stemmer = new greekStemmer();
    stemmer.setCurrent("απιεστε");
    assertTrue(stemmer.stem());
    assertEquals("απιεστ", stemmer.getCurrent());

    stemmer.setCurrent("προεστε");
    assertTrue(stemmer.stem());
    assertEquals("προιεστ", stemmer.getCurrent());

    stemmer.setCurrent("πειθηκα");
    assertTrue(stemmer.stem());
    assertEquals("πειθ", stemmer.getCurrent());

    stemmer.setCurrent("διαθηκες");
    assertTrue(stemmer.stem());
    assertEquals("διαθηκ", stemmer.getCurrent());
  }

  @Test
  public void testOusaAgaAndIsaSuffixes() {
    greekStemmer stemmer = new greekStemmer();
    stemmer.setCurrent("φαγουσα");
    assertTrue(stemmer.stem());
    assertEquals("φαγουσ", stemmer.getCurrent());

    stemmer.setCurrent("φαρμακουσες");
    assertTrue(stemmer.stem());
    assertEquals("φαρμακουσ", stemmer.getCurrent());

    stemmer.setCurrent("κολλαγα");
    assertTrue(stemmer.stem());
    assertEquals("κολλαγ", stemmer.getCurrent());

    stemmer.setCurrent("μεγαλονησου");
    assertTrue(stemmer.stem());
    assertEquals("μεγαλονησ", stemmer.getCurrent());
  }

  @Test
  public void testUneAndUmeSuffixes() {
    greekStemmer stemmer = new greekStemmer();
    stemmer.setCurrent("σπιουνε");
    assertTrue(stemmer.stem());
    assertEquals("σπιουν", stemmer.getCurrent());

    stemmer.setCurrent("εξωνουνε");
    assertTrue(stemmer.stem());
    assertEquals("εξωνουν", stemmer.getCurrent());

    stemmer.setCurrent("παρασουσουμε");
    assertTrue(stemmer.stem());
    assertEquals("παρασουσουμ", stemmer.getCurrent());
  }

  @Test
  public void testMataAndGeneralInflectionRemoval() {
    greekStemmer stemmer = new greekStemmer();
    stemmer.setCurrent("κυματα");
    assertTrue(stemmer.stem());
    assertEquals("κυμ", stemmer.getCurrent());

    stemmer.setCurrent("κυματος");
    assertTrue(stemmer.stem());
    assertEquals("κυμ", stemmer.getCurrent());

    stemmer.setCurrent("κυμαιουμαι");
    assertTrue(stemmer.stem());
    assertEquals("κυμαι", stemmer.getCurrent());
  }

  @Test
  public void testComparativeAndSuperlativeSuffixes() {
    greekStemmer stemmer = new greekStemmer();
    stemmer.setCurrent("καλυτερος");
    assertTrue(stemmer.stem());
    assertEquals("καλ", stemmer.getCurrent());

    stemmer.setCurrent("μεγαλυτατη");
    assertTrue(stemmer.stem());
    assertEquals("μεγαλ", stemmer.getCurrent());

    stemmer.setCurrent("ωραιοτατων");
    assertTrue(stemmer.stem());
    assertEquals("ωραι", stemmer.getCurrent());

    stemmer.setCurrent("δυνατοτερη");
    assertTrue(stemmer.stem());
    assertEquals("δυνατ", stemmer.getCurrent());
  }

  @Test
  public void testCommonNounInflectionsConvergeOnSameStem() {
    greekStemmer nominative = new greekStemmer();
    greekStemmer genitive = new greekStemmer();
    greekStemmer plural = new greekStemmer();
    nominative.setCurrent("λογος");
    genitive.setCurrent("λογου");
    plural.setCurrent("λογων");

    assertTrue(nominative.stem());
    assertTrue(genitive.stem());
    assertTrue(plural.stem());
    assertEquals("λογ", nominative.getCurrent());
    assertEquals(nominative.getCurrent(), genitive.getCurrent());
    assertEquals(genitive.getCurrent(), plural.getCurrent());
  }

  @Test
  public void testOneInstanceCanBeReusedAcrossUnrelatedWords() {
    greekStemmer stemmer = new greekStemmer();
    stemmer.setCurrent("γυναικες");
    assertTrue(stemmer.stem());
    assertEquals("γυναικ", stemmer.getCurrent());

    stemmer.setCurrent("ΑΝΘΡΩΠΩΝ");
    assertTrue(stemmer.stem());
    assertEquals("ανθρωπ", stemmer.getCurrent());

    stemmer.setCurrent("ABC");
    assertTrue(stemmer.stem());
    assertEquals("ABC", stemmer.getCurrent());
  }

  @Test
  public void testRepeatedStemmingIsStableForResultingStem() {
    greekStemmer stemmer = new greekStemmer();
    stemmer.setCurrent("λογων");
    assertTrue(stemmer.stem());
    assertEquals("λογ", stemmer.getCurrent());
    assertTrue(stemmer.stem());
    assertEquals("λογ", stemmer.getCurrent());
  }

  @Test
  public void testEqualsUsesStemmerTypeRatherThanMutableState() {
    greekStemmer first = new greekStemmer();
    greekStemmer second = new greekStemmer();
    first.setCurrent("λογος");
    second.setCurrent("εντελωςδιαφορετικο");

    assertTrue(first.equals(first));
    assertTrue(first.equals(second));
    assertTrue(second.equals(first));
    assertFalse(first.equals(null));
    assertFalse(first.equals("λογος"));
    assertFalse(first.equals(new englishStemmer()));
  }

  @Test
  public void testEqualInstancesHaveStableEqualHashCodes() {
    greekStemmer first = new greekStemmer();
    greekStemmer second = new greekStemmer();
    int initialHash = first.hashCode();
    first.setCurrent("λογων");
    first.stem();
    second.setCurrent("γυναικες");
    second.stem();

    assertEquals(first, second);
    assertEquals(initialHash, first.hashCode());
    assertEquals(first.hashCode(), second.hashCode());
    assertEquals(greekStemmer.class.getName().hashCode(), first.hashCode());
  }

  @Test(expected = NullPointerException.class)
  public void testSetCurrentRejectsNull() {
    greekStemmer stemmer = new greekStemmer();
    stemmer.setCurrent(null);
  }

  @Test
  public void testGenericSnowballFacadeAgreesWithDirectStemmerCalls() {
    Stemmer facade = new SnowballStemmer(SnowballStemmer.ALGORITHM.GREEK);
    greekStemmer direct = new greekStemmer();
    StringBuilder token = new StringBuilder("ΑΝΘΡΩΠΩΝ");
    direct.setCurrent(token.toString());

    CharSequence facadeResult = facade.stem(token);
    assertTrue(direct.stem());
    assertEquals("ανθρωπ", facadeResult.toString());
    assertEquals(direct.getCurrent(), facadeResult.toString());
    assertEquals("ΑΝΘΡΩΠΩΝ", token.toString());
  }

}
