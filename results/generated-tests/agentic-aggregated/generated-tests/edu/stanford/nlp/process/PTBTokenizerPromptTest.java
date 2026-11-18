package edu.stanford.nlp.process;

import edu.stanford.nlp.io.RuntimeIOException;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Word;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

public class PTBTokenizerPromptTest {

  @Test
  public void newWordTokenizerSplitsWordsAndPunctuation() {
    PTBTokenizer<Word> tokenizer = PTBTokenizer.newPTBTokenizer(
        new StringReader("Hello, world!"));

    List<Word> tokens = tokenizer.tokenize();

    assertEquals("[Hello, ,, world, !]", tokens.toString());
    assertFalse(tokenizer.hasNext());
  }

  @Test
  public void emptyInputProducesNoTokensAndNextThrows() {
    PTBTokenizer<Word> tokenizer = PTBTokenizer.newPTBTokenizer(new StringReader(""));

    assertFalse(tokenizer.hasNext());
    assertEquals(Collections.emptyList(), tokenizer.tokenize());
    assertThrows(NoSuchElementException.class, tokenizer::next);
  }

  @Test
  public void whitespaceOnlyInputProducesNoTokens() {
    PTBTokenizer<Word> tokenizer = new PTBTokenizer<>(
        new StringReader(" \t\r\n  "), new WordTokenFactory(),
        "invertible=false,ptb3Escaping=false");

    assertTrue(tokenizer.tokenize().isEmpty());
  }

  @Test
  public void repeatedHasNextAndPeekDoNotConsumeToken() {
    PTBTokenizer<Word> tokenizer = PTBTokenizer.newPTBTokenizer(new StringReader("one two"));

    assertTrue(tokenizer.hasNext());
    assertTrue(tokenizer.hasNext());
    assertEquals("one", tokenizer.peek().word());
    assertEquals("one", tokenizer.peek().word());
    assertEquals("one", tokenizer.next().word());
    assertEquals("two", tokenizer.next().word());
    assertFalse(tokenizer.hasNext());
  }

  @Test
  public void removeIsUnsupported() {
    PTBTokenizer<Word> tokenizer = PTBTokenizer.newPTBTokenizer(new StringReader("token"));

    assertThrows(UnsupportedOperationException.class, tokenizer::remove);
    assertEquals("token", tokenizer.next().word());
  }

  @Test
  public void nullConstructorOptionsUseBinaryDefaults() {
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(
        new StringReader("(colour)"), new CoreLabelTokenFactory(), null);

    assertEquals("[(, colour, )]", tokenizer.tokenize().toString());
  }

  @Test
  public void disablingTreebankEscapingPreservesParenthesesAndBritishSpelling() {
    PTBTokenizer<Word> tokenizer = new PTBTokenizer<>(
        new StringReader("(colour)"), new WordTokenFactory(),
        "invertible=false,ptb3Escaping=false");

    assertEquals("[(, colour, )]", tokenizer.tokenize().toString());
  }

  @Test
  public void tokenizerCanReturnNormalizedNewlines() {
    PTBTokenizer<CoreLabel> tokenizer = PTBTokenizer.newPTBTokenizer(
        new StringReader("first\r\nsecond\nthird"), true, false);

    List<CoreLabel> tokens = tokenizer.tokenize();

    assertEquals(5, tokens.size());
    assertEquals("first", tokens.get(0).word());
    assertEquals(PTBTokenizer.getNewlineToken(), tokens.get(1).word());
    assertEquals("second", tokens.get(2).word());
    assertEquals(PTBTokenizer.getNewlineToken(), tokens.get(3).word());
    assertEquals("third", tokens.get(4).word());
  }

  @Test
  public void tokenizerCanDiscardNewlinesAsWhitespace() {
    PTBTokenizer<CoreLabel> tokenizer = PTBTokenizer.newPTBTokenizer(
        new StringReader("first\nsecond"), false, false);

    assertEquals("[first, second]", tokenizer.tokenize().toString());
  }

  @Test
  public void newlineTokenIsStablePublicLiteral() {
    assertEquals("*NL*", PTBTokenizer.getNewlineToken());
  }

  @Test
  public void invertibleCoreLabelsPreserveOriginalTextWhitespaceAndOffsets() {
    PTBTokenizer<CoreLabel> tokenizer = PTBTokenizer.newPTBTokenizer(
        new StringReader("  (Hi)\tthere!  "), false, true);

    List<CoreLabel> tokens = tokenizer.tokenize();

    assertEquals(5, tokens.size());
    assertEquals("-LRB-", tokens.get(0).word());
    assertEquals("(", tokens.get(0).originalText());
    assertEquals("  ", tokens.get(0).before());
    assertEquals(2, tokens.get(0).beginPosition());
    assertEquals(3, tokens.get(0).endPosition());
    assertEquals("", tokens.get(0).after());
    assertEquals("Hi", tokens.get(1).originalText());
    assertEquals(")", tokens.get(2).originalText());
    assertEquals("\t", tokens.get(3).before());
    assertEquals("!", tokens.get(4).word());
    assertEquals("  ", tokens.get(4).after());
  }

  @Test
  public void coreLabelFactoryWithoutIndicesStillProducesTokens() {
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(
        new StringReader("alpha beta"), new CoreLabelTokenFactory(false), "invertible=false");

    List<CoreLabel> tokens = tokenizer.tokenize();

    assertEquals("alpha", tokens.get(0).word());
    assertEquals("beta", tokens.get(1).word());
    assertEquals(-1, tokens.get(0).beginPosition());
    assertEquals(-1, tokens.get(0).endPosition());
  }

  @Test
  public void americanizeOptionCanBeDisabledIndependently() {
    PTBTokenizer<Word> tokenizer = new PTBTokenizer<>(
        new StringReader("colour centre"), new WordTokenFactory(),
        "invertible=false,ptb3Escaping=true,americanize=false");

    assertEquals("[colour, centre]", tokenizer.tokenize().toString());
  }

  @Test
  public void splitHyphenatedSeparatesOrdinaryCompound() {
    PTBTokenizer<Word> tokenizer = new PTBTokenizer<>(
        new StringReader("school-aged"), new WordTokenFactory(),
        "invertible=false,ptb3Escaping=false,splitHyphenated=true");

    assertEquals("[school, -, aged]", tokenizer.tokenize().toString());
  }

  @Test
  public void disablingSplitHyphenatedKeepsCompoundTogether() {
    PTBTokenizer<Word> tokenizer = new PTBTokenizer<>(
        new StringReader("school-aged"), new WordTokenFactory(),
        "invertible=false,ptb3Escaping=false,splitHyphenated=false");

    assertEquals("[school-aged]", tokenizer.tokenize().toString());
  }

  @Test
  public void splitForwardSlashSeparatesSlashedWords() {
    PTBTokenizer<Word> tokenizer = new PTBTokenizer<>(
        new StringReader("and/or"), new WordTokenFactory(),
        "invertible=false,ptb3Escaping=false,splitForwardSlash=true");

    assertEquals("[and, /, or]", tokenizer.tokenize().toString());
  }

  @Test
  public void disablingSplitForwardSlashKeepsSlashedWordsTogether() {
    PTBTokenizer<Word> tokenizer = new PTBTokenizer<>(
        new StringReader("and/or"), new WordTokenFactory(),
        "invertible=false,ptb3Escaping=false,splitForwardSlash=false");

    assertEquals("[and/or]", tokenizer.tokenize().toString());
  }

  @Test
  public void splitAssimilationsOptionAcceptsAlreadyFusedGimme() {
    PTBTokenizer<Word> tokenizer = new PTBTokenizer<>(
        new StringReader("gimme"), new WordTokenFactory(),
        "invertible=false,splitAssimilations=true");

    assertEquals("[gimme]", tokenizer.tokenize().toString());
  }

  @Test
  public void unsplitAssimilationsKeepGonnaWhole() {
    PTBTokenizer<Word> tokenizer = new PTBTokenizer<>(
        new StringReader("gonna"), new WordTokenFactory(),
        "invertible=false,splitAssimilations=false");

    assertEquals("[gonna]", tokenizer.tokenize().toString());
  }

  @Test
  public void ampersandEntityNormalizationCanBeControlled() {
    PTBTokenizer<Word> normalized = new PTBTokenizer<>(
        new StringReader("A&amp;B"), new WordTokenFactory(),
        "invertible=false,normalizeAmpersandEntity=true");
    PTBTokenizer<Word> original = new PTBTokenizer<>(
        new StringReader("A&amp;B"), new WordTokenFactory(),
        "invertible=false,normalizeAmpersandEntity=false");

    assertEquals("[A&B]", normalized.tokenize().toString());
    assertEquals("[A&amp;B]", original.tokenize().toString());
  }

  @Test
  public void fractionCharacterNormalizationCanBeControlled() {
    PTBTokenizer<Word> normalized = new PTBTokenizer<>(
        new StringReader("½"), new WordTokenFactory(),
        "invertible=false,normalizeFractions=true");
    PTBTokenizer<Word> original = new PTBTokenizer<>(
        new StringReader("½"), new WordTokenFactory(),
        "invertible=false,normalizeFractions=false");

    assertEquals("[1/2]", normalized.tokenize().toString());
    assertEquals("[½]", original.tokenize().toString());
  }

  @Test
  public void quoteStyleCanNormalizeUnicodeQuotesToAscii() {
    PTBTokenizer<Word> tokenizer = new PTBTokenizer<>(
        new StringReader("“Hello” ‘friend’"), new WordTokenFactory(),
        "invertible=false,quotes=ascii");

    assertEquals("[\", Hello, \", ', friend, ']", tokenizer.tokenize().toString());
  }

  @Test
  public void dashStyleCanNormalizeAsciiDashesToUnicode() {
    PTBTokenizer<Word> tokenizer = new PTBTokenizer<>(
        new StringReader("one -- two"), new WordTokenFactory(),
        "invertible=false,dashes=unicode");

    assertEquals("[one, —, two]", tokenizer.tokenize().toString());
  }

  @Test
  public void ptbToken2TextUnescapesBracketTokens() {
    assertEquals("(", PTBTokenizer.ptbToken2Text("-LRB-"));
    assertEquals(")", PTBTokenizer.ptbToken2Text("-RRB-"));
    assertEquals("{", PTBTokenizer.ptbToken2Text("-LCB-"));
  }

  @Test
  public void ptb2TextRestoresReadablePunctuationAndContractions() {
    String result = PTBTokenizer.ptb2Text("John ca n't go , can he ?");

    assertEquals("John can't go, can he?", result);
  }

  @Test
  public void ptb2TextHandlesTreebankQuotesAndParentheses() {
    String result = PTBTokenizer.ptb2Text("He said , `` Hi ! '' -LRB- really -RRB- .");

    assertEquals("He said, \"Hi!\" (really).", result);
  }

  @Test
  public void ptb2TextListOverloadJoinsTokensBeforeUntokenizing() {
    String result = PTBTokenizer.ptb2Text(
        Arrays.asList("We", "'re", "ready", ",", "too", "."));

    assertEquals("We're ready, too.", result);
  }

  @Test
  public void labelList2TextUsesWordValuesFromMultipleLabelTypes() {
    Word first = new Word("A");
    CoreLabel second = new CoreLabel();
    second.setWord("test");
    Word comma = new Word(",");
    Word last = new Word("indeed");
    Word period = new Word(".");

    String result = PTBTokenizer.labelList2Text(
        Arrays.asList(first, second, comma, last, period));

    assertEquals("A test, indeed.", result);
  }

  @Test
  public void streamingPtb2TextWritesOutputAndReportsLexerItemCount() throws IOException {
    StringReader reader = new StringReader("This is -LRB- fine -RRB- .");
    StringWriter writer = new StringWriter();

    long itemCount = PTBTokenizer.ptb2Text(reader, writer);

    assertEquals("This is (fine).", writer.toString());
    assertEquals(8L, itemCount);
  }

  @Test
  public void factoryCreatesIndependentWordTokenizers() {
    TokenizerFactory<Word> factory = PTBTokenizer.factory();
    Tokenizer<Word> first = factory.getTokenizer(new StringReader("One."));
    Tokenizer<Word> second = factory.getTokenizer(new StringReader("Two!"));

    assertEquals("[One, .]", first.tokenize().toString());
    assertEquals("[Two, !]", second.tokenize().toString());
  }

  @Test
  public void coreLabelFactoryHonorsConfiguredOptions() {
    TokenizerFactory<CoreLabel> factory = PTBTokenizer.coreLabelFactory(
        "ptb3Escaping=false,tokenizeNLs");
    Tokenizer<CoreLabel> tokenizer = factory.getTokenizer(new StringReader("(x)\ny"));

    assertEquals("[(, x, ), *NL*, y]", tokenizer.tokenize().toString());
  }

  @Test
  public void booleanFactoryProducesInvertibleLabelsAndNewlineTokens() {
    TokenizerFactory<CoreLabel> factory = PTBTokenizer.factory(true, true);
    Tokenizer<CoreLabel> tokenizer = factory.getTokenizer(new StringReader(" a\nb "));

    List<CoreLabel> tokens = tokenizer.tokenize();

    assertEquals(3, tokens.size());
    assertEquals("a", tokens.get(0).word());
    assertEquals(" ", tokens.get(0).before());
    assertEquals("*NL*", tokens.get(1).word());
    assertEquals("b", tokens.get(2).word());
    assertEquals(" ", tokens.get(2).after());
  }

  @Test
  public void noArgumentCoreLabelFactoryReturnsCoreLabels() {
    TokenizerFactory<CoreLabel> factory = PTBTokenizer.coreLabelFactory();
    Tokenizer<CoreLabel> tokenizer = factory.getTokenizer(new StringReader("plain text"));

    List<CoreLabel> tokens = tokenizer.tokenize();

    assertEquals(2, tokens.size());
    assertEquals(CoreLabel.class, tokens.get(0).getClass());
    assertEquals("plain", tokens.get(0).word());
    assertEquals("text", tokens.get(1).word());
  }

  @Test
  public void factoryExtraOptionsAreComposedWithBaseOptions() {
    TokenizerFactory<Word> factory = PTBTokenizer.factory(
        new WordTokenFactory(), "invertible=false,ptb3Escaping=false");
    Tokenizer<Word> tokenizer = factory.getTokenizer(
        new StringReader("left/right\nnext"), "splitForwardSlash=true,tokenizeNLs");

    assertEquals("[left, /, right, *NL*, next]", tokenizer.tokenize().toString());
  }

  @Test
  public void factorySetOptionsAffectsSubsequentlyCreatedTokenizer() {
    TokenizerFactory<CoreLabel> factory = PTBTokenizer.coreLabelFactory("ptb3Escaping=true");
    Tokenizer<CoreLabel> before = factory.getTokenizer(new StringReader("(x)"));
    factory.setOptions("ptb3Escaping=false");
    Tokenizer<CoreLabel> after = factory.getTokenizer(new StringReader("(x)"));

    assertEquals("[-LRB-, x, -RRB-]", before.tokenize().toString());
    assertEquals("[(, x, )]", after.tokenize().toString());
  }

  @Test
  public void factoryIteratorSupportsStandardIteratorCalls() {
    TokenizerFactory<Word> factory = PTBTokenizer.factory();
    Iterator<Word> iterator = factory.getIterator(new StringReader("alpha beta"));

    assertTrue(iterator.hasNext());
    assertEquals("alpha", iterator.next().word());
    assertEquals("beta", iterator.next().word());
    assertFalse(iterator.hasNext());
    assertThrows(NoSuchElementException.class, iterator::next);
  }

  @Test
  public void readerIOExceptionIsWrappedAsRuntimeIOException() {
    Reader reader = new Reader() {
      @Override
      public int read(char[] buffer, int offset, int length) throws IOException {
        throw new IOException("synthetic read failure");
      }

      @Override
      public void close() {
      }
    };
    PTBTokenizer<Word> tokenizer = PTBTokenizer.newPTBTokenizer(reader);

    RuntimeIOException exception = assertThrows(RuntimeIOException.class, tokenizer::hasNext);

    assertTrue(exception.getCause() instanceof IOException);
    assertEquals("synthetic read failure", exception.getCause().getMessage());
  }

  @Test
  public void unknownOptionIsRejectedDuringConstruction() {
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> new PTBTokenizer<>(new StringReader("text"), new WordTokenFactory(),
            "definitelyNotAnOption=true"));

    assertTrue(exception.getMessage().contains("definitelyNotAnOption"));
  }
}
