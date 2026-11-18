package opennlp.tools.stemmer.snowball;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.nio.CharBuffer;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import opennlp.tools.stemmer.Stemmer;

public class SnowballStemmerPromptTest {

  @Test
  public void defaultConstructorUsesOneEnglishPass() {
    SnowballStemmer defaultStemmer =
        new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
    SnowballStemmer explicitStemmer =
        new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 1);

    assertEquals("run", defaultStemmer.stem("running").toString());
    assertEquals(explicitStemmer.stem("relational").toString(),
        defaultStemmer.stem("relational").toString());
  }

  @Test
  public void implementsStemmerAndReturnsAString() {
    Stemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);

    CharSequence result = stemmer.stem("jumping");

    assertNotNull(result);
    assertTrue(result instanceof String);
    assertEquals("jump", result.toString());
  }

  @Test
  public void englishStemmerHandlesSeveralSuffixRulesAcrossCalls() {
    SnowballStemmer stemmer =
        new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);

    assertEquals("relat", stemmer.stem("relational").toString());
    assertEquals("condit", stemmer.stem("conditional").toString());
    assertEquals("digit", stemmer.stem("digitizer").toString());
    assertEquals("hope", stemmer.stem("hopefulness").toString());
    assertEquals("triplic", stemmer.stem("triplicate").toString());
  }

  @Test
  public void porterAndEnglishCanBeUsedInOnePipeline() {
    Stemmer english = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
    Stemmer porter = new SnowballStemmer(SnowballStemmer.ALGORITHM.PORTER);

    CharSequence englishResult = english.stem(new StringBuilder("generalizations"));
    CharSequence porterResult = porter.stem(englishResult);

    assertEquals("general", englishResult.toString());
    assertEquals("gener", porterResult.toString());
    assertNotSame(englishResult, porterResult);
  }

  @Test
  public void zeroRepeatsPreservesTextExactly() {
    SnowballStemmer stemmer =
        new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 0);

    assertEquals("Running", stemmer.stem("Running").toString());
    assertEquals("relational", stemmer.stem("relational").toString());
    assertEquals("", stemmer.stem("").toString());
  }

  @Test
  public void negativeRepeatsAlsoPreservesTextExactly() {
    SnowballStemmer stemmer =
        new SnowballStemmer(SnowballStemmer.ALGORITHM.FRENCH, -3);

    assertEquals("chevaux", stemmer.stem("chevaux").toString());
    assertEquals("été", stemmer.stem("été").toString());
  }

  @Test
  public void multipleRepeatsApplyStemmerAgainToPreviousResult() {
    SnowballStemmer once =
        new SnowballStemmer(SnowballStemmer.ALGORITHM.PORTER, 1);
    SnowballStemmer twice =
        new SnowballStemmer(SnowballStemmer.ALGORITHM.PORTER, 2);
    String firstPass = once.stem("generously").toString();
    String secondPassBySeparateCall = once.stem(firstPass).toString();

    assertEquals(secondPassBySeparateCall, twice.stem("generously").toString());
  }

  @Test
  public void acceptsStringBuilderAndSnapshotsItsText() {
    SnowballStemmer stemmer =
        new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
    StringBuilder input = new StringBuilder("ponies");

    CharSequence result = stemmer.stem(input);
    input.replace(0, input.length(), "changed");

    assertEquals("poni", result.toString());
    assertEquals("changed", input.toString());
  }

  @Test
  public void acceptsACharBufferSlice() {
    SnowballStemmer stemmer =
        new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
    CharBuffer input = CharBuffer.wrap("xxhappinessyy");
    input.position(2);
    input.limit(11);

    assertEquals("happi", stemmer.stem(input).toString());
    assertEquals(2, input.position());
    assertEquals(11, input.limit());
  }

  @Test
  public void invokesCustomCharSequenceToStringExactlyOnce() {
    SnowballStemmer stemmer =
        new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
    AtomicInteger calls = new AtomicInteger();
    CharSequence input = new CharSequence() {
      @Override
      public int length() {
        throw new AssertionError("length should not be called");
      }

      @Override
      public char charAt(int index) {
        throw new AssertionError("charAt should not be called");
      }

      @Override
      public CharSequence subSequence(int start, int end) {
        throw new AssertionError("subSequence should not be called");
      }

      @Override
      public String toString() {
        calls.incrementAndGet();
        return "skies";
      }
    };

    assertEquals("sky", stemmer.stem(input).toString());
    assertEquals(1, calls.get());
  }

  @Test(expected = NullPointerException.class)
  public void stemRejectsNullInput() {
    SnowballStemmer stemmer =
        new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);

    stemmer.stem(null);
  }

  @Test
  public void nullAlgorithmReportsUnexpectedAlgorithm() {
    try {
      new SnowballStemmer(null);
    } catch (IllegalStateException expected) {
      assertEquals("Unexpected stemmer algorithm: null", expected.getMessage());
      return;
    }
    throw new AssertionError("Expected IllegalStateException");
  }

  @Test
  public void allAlgorithmConstantsArePresentInDeclaredOrder() {
    SnowballStemmer.ALGORITHM[] values = SnowballStemmer.ALGORITHM.values();

    assertArrayEquals(new SnowballStemmer.ALGORITHM[] {
        SnowballStemmer.ALGORITHM.ARABIC,
        SnowballStemmer.ALGORITHM.DANISH,
        SnowballStemmer.ALGORITHM.DUTCH,
        SnowballStemmer.ALGORITHM.CATALAN,
        SnowballStemmer.ALGORITHM.ENGLISH,
        SnowballStemmer.ALGORITHM.FINNISH,
        SnowballStemmer.ALGORITHM.FRENCH,
        SnowballStemmer.ALGORITHM.GERMAN,
        SnowballStemmer.ALGORITHM.GREEK,
        SnowballStemmer.ALGORITHM.HUNGARIAN,
        SnowballStemmer.ALGORITHM.INDONESIAN,
        SnowballStemmer.ALGORITHM.IRISH,
        SnowballStemmer.ALGORITHM.ITALIAN,
        SnowballStemmer.ALGORITHM.NORWEGIAN,
        SnowballStemmer.ALGORITHM.PORTER,
        SnowballStemmer.ALGORITHM.PORTUGUESE,
        SnowballStemmer.ALGORITHM.ROMANIAN,
        SnowballStemmer.ALGORITHM.RUSSIAN,
        SnowballStemmer.ALGORITHM.SPANISH,
        SnowballStemmer.ALGORITHM.SWEDISH,
        SnowballStemmer.ALGORITHM.TURKISH
    }, values);
  }

  @Test
  public void valueOfResolvesRepresentativeAlgorithms() {
    assertEquals(SnowballStemmer.ALGORITHM.ARABIC,
        SnowballStemmer.ALGORITHM.valueOf("ARABIC"));
    assertEquals(SnowballStemmer.ALGORITHM.ENGLISH,
        SnowballStemmer.ALGORITHM.valueOf("ENGLISH"));
    assertEquals(SnowballStemmer.ALGORITHM.TURKISH,
        SnowballStemmer.ALGORITHM.valueOf("TURKISH"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void valueOfRejectsWrongCase() {
    SnowballStemmer.ALGORITHM.valueOf("english");
  }

  @Test
  public void everyAlgorithmCanStemAnEmptyInput() {
    assertEquals("", new SnowballStemmer(SnowballStemmer.ALGORITHM.ARABIC).stem("").toString());
    assertEquals("", new SnowballStemmer(SnowballStemmer.ALGORITHM.DANISH).stem("").toString());
    assertEquals("", new SnowballStemmer(SnowballStemmer.ALGORITHM.DUTCH).stem("").toString());
    assertEquals("", new SnowballStemmer(SnowballStemmer.ALGORITHM.CATALAN).stem("").toString());
    assertEquals("", new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH).stem("").toString());
    assertEquals("", new SnowballStemmer(SnowballStemmer.ALGORITHM.FINNISH).stem("").toString());
    assertEquals("", new SnowballStemmer(SnowballStemmer.ALGORITHM.FRENCH).stem("").toString());
    assertEquals("", new SnowballStemmer(SnowballStemmer.ALGORITHM.GERMAN).stem("").toString());
    assertEquals("", new SnowballStemmer(SnowballStemmer.ALGORITHM.GREEK).stem("").toString());
    assertEquals("", new SnowballStemmer(SnowballStemmer.ALGORITHM.HUNGARIAN).stem("").toString());
    assertEquals("", new SnowballStemmer(SnowballStemmer.ALGORITHM.INDONESIAN).stem("").toString());
    assertEquals("", new SnowballStemmer(SnowballStemmer.ALGORITHM.IRISH).stem("").toString());
    assertEquals("", new SnowballStemmer(SnowballStemmer.ALGORITHM.ITALIAN).stem("").toString());
    assertEquals("", new SnowballStemmer(SnowballStemmer.ALGORITHM.NORWEGIAN).stem("").toString());
    assertEquals("", new SnowballStemmer(SnowballStemmer.ALGORITHM.PORTER).stem("").toString());
    assertEquals("", new SnowballStemmer(SnowballStemmer.ALGORITHM.PORTUGUESE).stem("").toString());
    assertEquals("", new SnowballStemmer(SnowballStemmer.ALGORITHM.ROMANIAN).stem("").toString());
    assertEquals("", new SnowballStemmer(SnowballStemmer.ALGORITHM.RUSSIAN).stem("").toString());
    assertEquals("", new SnowballStemmer(SnowballStemmer.ALGORITHM.SPANISH).stem("").toString());
    assertEquals("", new SnowballStemmer(SnowballStemmer.ALGORITHM.SWEDISH).stem("").toString());
    assertEquals("", new SnowballStemmer(SnowballStemmer.ALGORITHM.TURKISH).stem("").toString());
  }

  @Test
  public void languageSpecificStemmersProduceDistinctResults() {
    SnowballStemmer english = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
    SnowballStemmer french = new SnowballStemmer(SnowballStemmer.ALGORITHM.FRENCH);
    SnowballStemmer german = new SnowballStemmer(SnowballStemmer.ALGORITHM.GERMAN);

    String englishResult = english.stem("chevaux").toString();
    String frenchResult = french.stem("chevaux").toString();
    String germanResult = german.stem("häusern").toString();

    assertEquals("chevaux", englishResult);
    assertEquals("cheval", frenchResult);
    assertEquals("haus", germanResult);
    assertFalse(englishResult.equals(frenchResult));
  }

  @Test
  public void stemmerStateIsReplacedBetweenCalls() {
    SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);

    CharSequence first = stemmer.stem("running");
    CharSequence second = stemmer.stem("ponies");
    CharSequence third = stemmer.stem("running");

    assertEquals("run", first.toString());
    assertEquals("poni", second.toString());
    assertEquals("run", third.toString());
  }

  @Test
  public void separateInstancesDoNotShareState() {
    SnowballStemmer first = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
    SnowballStemmer second = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);

    CharSequence firstResult = first.stem("hopping");
    CharSequence secondResult = second.stem("filing");

    assertEquals("hop", firstResult.toString());
    assertEquals("file", secondResult.toString());
    assertEquals("hop", firstResult.toString());
  }

  @Test
  public void punctuationAndDigitsSurviveEnglishStemming() {
    SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);

    assertEquals("12345", stemmer.stem("12345").toString());
    assertEquals("!!!", stemmer.stem("!!!").toString());
    assertEquals("running!", stemmer.stem("running!").toString());
  }

  @Test
  public void supplementaryUnicodeInputIsAcceptedWithoutCorruption() {
    SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
    String input = "\uD83D\uDE03";

    assertEquals(input, stemmer.stem(input).toString());
  }
}
