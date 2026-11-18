package opennlp.tools.stemmer.snowball;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;

import org.junit.Test;

import opennlp.tools.stemmer.Stemmer;

public class arabicStemmerPromptTest {

  @Test
  public void stemsInitialEmptyValue() {
    arabicStemmer stemmer = new arabicStemmer();

    assertEquals("", stemmer.getCurrent());
    assertTrue(stemmer.stem());
    assertEquals("", stemmer.getCurrent());
  }

  @Test
  public void leavesAsciiAndPunctuationUntouched() {
    arabicStemmer stemmer = new arabicStemmer();
    stemmer.setCurrent("OpenNLP-2.3.3!");

    assertTrue(stemmer.stem());
    assertEquals("OpenNLP-2.3.3!", stemmer.getCurrent());
  }

  @Test
  public void removesArabicDiacriticsAndTatweelThroughoutWord() {
    arabicStemmer stemmer = new arabicStemmer();
    stemmer.setCurrent("عَلَـمٌ");

    assertTrue(stemmer.stem());
    assertEquals("علم", stemmer.getCurrent());
  }

  @Test
  public void convertsAllArabicIndicDigitsToAsciiDigits() {
    arabicStemmer stemmer = new arabicStemmer();
    stemmer.setCurrent("١٢٣٤٥٦٧٨٩٠");

    assertTrue(stemmer.stem());
    assertEquals("1234567890", stemmer.getCurrent());
  }

  @Test
  public void normalizesInternalAlefAndWawHamzaForms() {
    arabicStemmer stemmer = new arabicStemmer();
    stemmer.setCurrent("آثار");

    assertTrue(stemmer.stem());
    assertEquals("اثار", stemmer.getCurrent());

    stemmer.setCurrent("مؤمن");
    assertTrue(stemmer.stem());
    assertEquals("موم", stemmer.getCurrent());
  }

  @Test
  public void normalizesTerminalHamzaAfterSuffixProcessing() {
    arabicStemmer stemmer = new arabicStemmer();
    stemmer.setCurrent("مبدأ");

    assertTrue(stemmer.stem());
    assertEquals("مبدء", stemmer.getCurrent());

    stemmer.setCurrent("باء");
    assertTrue(stemmer.stem());
    assertEquals("باء", stemmer.getCurrent());
  }

  @Test
  public void normalizesYaHamzaAndRemovesFeminineSuffix() {
    arabicStemmer stemmer = new arabicStemmer();
    stemmer.setCurrent("بيئة");

    assertTrue(stemmer.stem());
    assertEquals("بيء", stemmer.getCurrent());
  }

  @Test
  public void convertsFinalAlefMaqsuraToYa() {
    arabicStemmer stemmer = new arabicStemmer();
    stemmer.setCurrent("هدى");

    assertTrue(stemmer.stem());
    assertEquals("هدي", stemmer.getCurrent());

    stemmer.setCurrent("الفتى");
    assertTrue(stemmer.stem());
    assertEquals("فتي", stemmer.getCurrent());
  }

  @Test
  public void normalizesPresentationFormsBeforeApplyingAffixes() {
    arabicStemmer stemmer = new arabicStemmer();
    stemmer.setCurrent("\uFE91\uFEAE\uFEE3\uFEA0\uFE94");

    assertTrue(stemmer.stem());
    assertEquals("رمج", stemmer.getCurrent());

    stemmer.setCurrent("\uFEFB\uFEDC\uFE98\uFE8E\uFE8F");
    assertTrue(stemmer.stem());
    assertEquals("لاكتاب", stemmer.getCurrent());
  }

  @Test
  public void preservesSupplementaryCharactersWhileNormalizingArabicText() {
    arabicStemmer stemmer = new arabicStemmer();
    stemmer.setCurrent("😀عَلَم😀");

    assertTrue(stemmer.stem());
    assertEquals("😀علم😀", stemmer.getCurrent());
  }

  @Test
  public void foldsDoubleAlefHamzaPrefixes() {
    arabicStemmer stemmer = new arabicStemmer();
    stemmer.setCurrent("أأكل");

    assertTrue(stemmer.stem());
    assertEquals("اكل", stemmer.getCurrent());

    stemmer.setCurrent("أآكل");
    assertTrue(stemmer.stem());
    assertEquals("اكل", stemmer.getCurrent());

    stemmer.setCurrent("أإكل");
    assertTrue(stemmer.stem());
    assertEquals("اكل", stemmer.getCurrent());
  }

  @Test
  public void removesSingleConjunctionPrefixWhenLongEnough() {
    arabicStemmer stemmer = new arabicStemmer();
    stemmer.setCurrent("فكتاب");

    assertTrue(stemmer.stem());
    assertEquals("كتاب", stemmer.getCurrent());

    stemmer.setCurrent("وكتاب");
    assertTrue(stemmer.stem());
    assertEquals("كتاب", stemmer.getCurrent());
  }

  @Test
  public void retainsConjunctionBeforeAlefAndAtLengthBoundary() {
    arabicStemmer stemmer = new arabicStemmer();
    stemmer.setCurrent("فارس");

    assertTrue(stemmer.stem());
    assertEquals("فارس", stemmer.getCurrent());

    stemmer.setCurrent("فكر");
    assertTrue(stemmer.stem());
    assertEquals("فكر", stemmer.getCurrent());

    stemmer.setCurrent("والكتاب");
    assertTrue(stemmer.stem());
    assertEquals("والكتاب", stemmer.getCurrent());
  }

  @Test
  public void removesDefiniteArticleVariants() {
    arabicStemmer stemmer = new arabicStemmer();
    stemmer.setCurrent("الكتاب");

    assertTrue(stemmer.stem());
    assertEquals("كتاب", stemmer.getCurrent());

    stemmer.setCurrent("بالكتاب");
    assertTrue(stemmer.stem());
    assertEquals("كتاب", stemmer.getCurrent());

    stemmer.setCurrent("كالكتاب");
    assertTrue(stemmer.stem());
    assertEquals("كتاب", stemmer.getCurrent());

    stemmer.setCurrent("للكتاب");
    assertTrue(stemmer.stem());
    assertEquals("كتاب", stemmer.getCurrent());
  }

  @Test
  public void honorsDefiniteArticleLengthThresholds() {
    arabicStemmer stemmer = new arabicStemmer();
    stemmer.setCurrent("باليد");

    assertTrue(stemmer.stem());
    assertEquals("باليد", stemmer.getCurrent());

    stemmer.setCurrent("بالكتب");
    assertTrue(stemmer.stem());
    assertEquals("كتب", stemmer.getCurrent());
  }

  @Test
  public void removesAndCollapsesNominalPrefixes() {
    arabicStemmer stemmer = new arabicStemmer();
    stemmer.setCurrent("بكتاب");

    assertTrue(stemmer.stem());
    assertEquals("كتاب", stemmer.getCurrent());

    stemmer.setCurrent("ببكتاب");
    assertTrue(stemmer.stem());
    assertEquals("بكتاب", stemmer.getCurrent());

    stemmer.setCurrent("ككتاب");
    assertTrue(stemmer.stem());
    assertEquals("كتاب", stemmer.getCurrent());
  }

  @Test
  public void processesFutureVerbPrefixes() {
    arabicStemmer stemmer = new arabicStemmer();
    stemmer.setCurrent("سيكتب");

    assertTrue(stemmer.stem());
    assertEquals("يكتب", stemmer.getCurrent());

    stemmer.setCurrent("ستكتب");
    assertTrue(stemmer.stem());
    assertEquals("تكتب", stemmer.getCurrent());

    stemmer.setCurrent("سنكتب");
    assertTrue(stemmer.stem());
    assertEquals("نكتب", stemmer.getCurrent());

    stemmer.setCurrent("سأكتب");
    assertTrue(stemmer.stem());
    assertEquals("اكتب", stemmer.getCurrent());
  }

  @Test
  public void normalizesInflectionalVerbPrefixesToIst() {
    arabicStemmer stemmer = new arabicStemmer();
    stemmer.setCurrent("يستعمل");

    assertTrue(stemmer.stem());
    assertEquals("استعمل", stemmer.getCurrent());

    stemmer.setCurrent("تستعمل");
    assertTrue(stemmer.stem());
    assertEquals("استعمل", stemmer.getCurrent());

    stemmer.setCurrent("نستعمل");
    assertTrue(stemmer.stem());
    assertEquals("استعمل", stemmer.getCurrent());
  }

  @Test
  public void removesSingularVerbObjectPronouns() {
    arabicStemmer stemmer = new arabicStemmer();
    stemmer.setCurrent("كتبك");

    assertTrue(stemmer.stem());
    assertEquals("كتب", stemmer.getCurrent());

    stemmer.setCurrent("كتبها");
    assertTrue(stemmer.stem());
    assertEquals("كتب", stemmer.getCurrent());

    stemmer.setCurrent("كتبني");
    assertTrue(stemmer.stem());
    assertEquals("كتب", stemmer.getCurrent());
  }

  @Test
  public void removesDualAndPluralVerbObjectPronouns() {
    arabicStemmer stemmer = new arabicStemmer();
    stemmer.setCurrent("كتبكما");

    assertTrue(stemmer.stem());
    assertEquals("كتب", stemmer.getCurrent());

    stemmer.setCurrent("كتبهما");
    assertTrue(stemmer.stem());
    assertEquals("كتب", stemmer.getCurrent());

    stemmer.setCurrent("كتبكم");
    assertTrue(stemmer.stem());
    assertEquals("كتب", stemmer.getCurrent());

    stemmer.setCurrent("كتبهن");
    assertTrue(stemmer.stem());
    assertEquals("كتب", stemmer.getCurrent());
  }

  @Test
  public void retainsTooShortVerbObjectSuffixCandidate() {
    arabicStemmer stemmer = new arabicStemmer();
    stemmer.setCurrent("كبه");

    assertTrue(stemmer.stem());
    assertEquals("كبه", stemmer.getCurrent());
  }

  @Test
  public void removesVerbPersonAndNumberSuffixes() {
    arabicStemmer stemmer = new arabicStemmer();
    stemmer.setCurrent("كتبت");

    assertTrue(stemmer.stem());
    assertEquals("كتب", stemmer.getCurrent());

    stemmer.setCurrent("كتبتم");
    assertTrue(stemmer.stem());
    assertEquals("كتب", stemmer.getCurrent());

    stemmer.setCurrent("كتبوا");
    assertTrue(stemmer.stem());
    assertEquals("كتب", stemmer.getCurrent());

    stemmer.setCurrent("كتبتما");
    assertTrue(stemmer.stem());
    assertEquals("كتب", stemmer.getCurrent());
  }

  @Test
  public void removesVerbNunSuffixes() {
    arabicStemmer stemmer = new arabicStemmer();
    stemmer.setCurrent("يكتبون");

    assertTrue(stemmer.stem());
    assertEquals("يكتب", stemmer.getCurrent());

    stemmer.setCurrent("تكتبين");
    assertTrue(stemmer.stem());
    assertEquals("تكتب", stemmer.getCurrent());

    stemmer.setCurrent("كتبن");
    assertTrue(stemmer.stem());
    assertEquals("كتب", stemmer.getCurrent());
  }

  @Test
  public void appliesOnlyEligiblePartOfCompoundVerbSuffix() {
    arabicStemmer stemmer = new arabicStemmer();
    stemmer.setCurrent("كتبتمو");

    assertTrue(stemmer.stem());
    assertEquals("كتبتم", stemmer.getCurrent());

    stemmer.setCurrent("كتبتموا");
    assertTrue(stemmer.stem());
    assertEquals("كتبتم", stemmer.getCurrent());
  }

  @Test
  public void removesFeminineNounSuffix() {
    arabicStemmer stemmer = new arabicStemmer();
    stemmer.setCurrent("مدرسة");

    assertTrue(stemmer.stem());
    assertEquals("مدرس", stemmer.getCurrent());

    stemmer.setCurrent("المدرسة");
    assertTrue(stemmer.stem());
    assertEquals("مدرس", stemmer.getCurrent());
  }

  @Test
  public void removesDefinedNounPluralSuffixes() {
    arabicStemmer stemmer = new arabicStemmer();
    stemmer.setCurrent("المعلمون");

    assertTrue(stemmer.stem());
    assertEquals("معلم", stemmer.getCurrent());

    stemmer.setCurrent("المعلمات");
    assertTrue(stemmer.stem());
    assertEquals("معلم", stemmer.getCurrent());
  }

  @Test
  public void chainsFeminineAndYaNounSuffixRemoval() {
    arabicStemmer stemmer = new arabicStemmer();
    stemmer.setCurrent("العربية");

    assertTrue(stemmer.stem());
    assertEquals("عرب", stemmer.getCurrent());
  }

  @Test
  public void resetsGrammaticalStateWhenInstanceIsReused() {
    arabicStemmer stemmer = new arabicStemmer();
    stemmer.setCurrent("الكتاب");
    assertTrue(stemmer.stem());
    assertEquals("كتاب", stemmer.getCurrent());

    stemmer.setCurrent("كتبها");
    assertTrue(stemmer.stem());
    assertEquals("كتب", stemmer.getCurrent());

    stemmer.setCurrent("المعلمون");
    assertTrue(stemmer.stem());
    assertEquals("معلم", stemmer.getCurrent());
  }

  @Test
  public void stemmingAlreadyStemmedValueIsIdempotent() {
    arabicStemmer stemmer = new arabicStemmer();
    stemmer.setCurrent("المعلمون");

    assertTrue(stemmer.stem());
    assertEquals("معلم", stemmer.getCurrent());
    assertTrue(stemmer.stem());
    assertEquals("معلم", stemmer.getCurrent());
  }

  @Test
  public void equalityIsBasedOnStemmerType() {
    arabicStemmer first = new arabicStemmer();
    arabicStemmer second = new arabicStemmer();
    first.setCurrent("الكتاب");
    second.setCurrent("كتبها");

    assertEquals(first, second);
    assertEquals(second, first);
    assertEquals(first.hashCode(), second.hashCode());
    assertFalse(first.equals(null));
    assertFalse(first.equals("arabicStemmer"));
  }

  @Test
  public void equalityAcceptsSubclassAndHashDiffersFromUnrelatedObject() {
    arabicStemmer stemmer = new arabicStemmer();
    arabicStemmer subclass = new arabicStemmer() {
    };

    assertTrue(stemmer.equals(subclass));
    assertTrue(subclass.equals(stemmer));
    assertEquals(stemmer.hashCode(), subclass.hashCode());
    assertNotEquals(stemmer, new Object());
  }

  @Test(expected = NotSerializableException.class)
  public void rejectsJavaSerializationInBenchmarkBinary() throws Exception {
    arabicStemmer stemmer = new arabicStemmer();
    stemmer.setCurrent("المدرسة");
    assertTrue(stemmer.stem());

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    ObjectOutputStream output = new ObjectOutputStream(bytes);
    output.writeObject(stemmer);
  }

  @Test
  public void agreesWithPublicSnowballStemmerAdapterAcrossCalls() {
    arabicStemmer direct = new arabicStemmer();
    Stemmer adapter = new SnowballStemmer(SnowballStemmer.ALGORITHM.ARABIC);
    direct.setCurrent("المعلمون");

    assertTrue(direct.stem());
    assertEquals(direct.getCurrent(), adapter.stem("المعلمون").toString());

    direct.setCurrent("عَلَـمٌ");
    assertTrue(direct.stem());
    assertEquals(direct.getCurrent(), adapter.stem("عَلَـمٌ").toString());
  }

  @Test(expected = NullPointerException.class)
  public void rejectsNullCurrentValue() {
    arabicStemmer stemmer = new arabicStemmer();

    stemmer.setCurrent(null);
  }
}
