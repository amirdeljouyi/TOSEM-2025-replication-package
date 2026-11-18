package opennlp.tools.formats;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

import opennlp.tools.namefind.NameSample;
import opennlp.tools.util.CollectionObjectStream;
import opennlp.tools.util.Span;

public class Conll03NameSampleStreamPromptTest {

  @Test
  public void readsEnglishSentenceWithEveryEntityType() throws Exception {
    CollectionObjectStream<String> lines = new CollectionObjectStream<>(Arrays.asList(
        "John NNP B-NP B-PER", "works VBZ B-VP O", "Acme NNP B-NP B-ORG",
        "in IN B-PP O", "Paris NNP B-NP B-LOC", "during IN B-PP O",
        "Expo NNP B-NP B-MISC", ""));
    int types = Conll02NameSampleStream.GENERATE_PERSON_ENTITIES
        | Conll02NameSampleStream.GENERATE_ORGANIZATION_ENTITIES
        | Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES
        | Conll02NameSampleStream.GENERATE_MISC_ENTITIES;
    Conll03NameSampleStream stream = new Conll03NameSampleStream(
        Conll03NameSampleStream.LANGUAGE.EN, lines, types);

    NameSample sample = stream.read();

    assertArrayEquals(new String[] {"John", "works", "Acme", "in", "Paris", "during", "Expo"},
        sample.getSentence());
    assertArrayEquals(new Span[] {new Span(0, 1, "person"),
        new Span(2, 3, "organization"), new Span(4, 5, "location"),
        new Span(6, 7, "misc")}, sample.getNames());
    assertFalse(sample.isClearAdaptiveDataSet());
    assertNull(stream.read());
  }

  @Test
  public void readsGermanFiveColumnInputAndEntityAtEndOfFile() throws Exception {
    CollectionObjectStream<String> lines = new CollectionObjectStream<>(Arrays.asList(
        "Angela Angela NE I-NP I-PER", "Merkel Merkel NE I-NP I-PER"));
    Conll03NameSampleStream stream = new Conll03NameSampleStream(
        Conll03NameSampleStream.LANGUAGE.DE, lines,
        Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);

    NameSample sample = stream.read();

    assertArrayEquals(new String[] {"Angela", "Merkel"}, sample.getSentence());
    assertArrayEquals(new Span[] {new Span(0, 2, "person")}, sample.getNames());
    assertNull(stream.read());
  }

  @Test
  public void startsEntityWhenInputBeginsWithInsideTag() throws Exception {
    CollectionObjectStream<String> lines = new CollectionObjectStream<>(Arrays.asList(
        "New NNP B-NP I-LOC", "York NNP I-NP I-LOC", ". . O O", ""));
    Conll03NameSampleStream stream = new Conll03NameSampleStream(
        Conll03NameSampleStream.LANGUAGE.EN, lines,
        Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES);

    NameSample sample = stream.read();

    assertArrayEquals(new Span[] {new Span(0, 2, "location")}, sample.getNames());
    assertArrayEquals(new String[] {"New", "York", "."}, sample.getSentence());
  }

  @Test
  public void beginTagSplitsAdjacentEntitiesOfSameType() throws Exception {
    CollectionObjectStream<String> lines = new CollectionObjectStream<>(Arrays.asList(
        "Alice NNP B-NP B-PER", "Smith NNP I-NP I-PER",
        "Bob NNP B-NP B-PER", ""));
    Conll03NameSampleStream stream = new Conll03NameSampleStream(
        Conll03NameSampleStream.LANGUAGE.EN, lines,
        Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);

    NameSample sample = stream.read();

    assertArrayEquals(new Span[] {new Span(0, 2, "person"), new Span(2, 3, "person")},
        sample.getNames());
  }

  @Test
  public void changingInsideTagTypeClosesPreviousEntity() throws Exception {
    CollectionObjectStream<String> lines = new CollectionObjectStream<>(Arrays.asList(
        "Jane NNP B-NP I-PER", "IBM NNP I-NP I-ORG", "Berlin NNP I-NP I-LOC", ""));
    int types = Conll02NameSampleStream.GENERATE_PERSON_ENTITIES
        | Conll02NameSampleStream.GENERATE_ORGANIZATION_ENTITIES
        | Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES;
    Conll03NameSampleStream stream = new Conll03NameSampleStream(
        Conll03NameSampleStream.LANGUAGE.EN, lines, types);

    NameSample sample = stream.read();

    assertArrayEquals(new Span[] {new Span(0, 1, "person"),
        new Span(1, 2, "organization"), new Span(2, 3, "location")}, sample.getNames());
  }

  @Test
  public void zeroTypeMaskFiltersAllRecognizedEntities() throws Exception {
    CollectionObjectStream<String> lines = new CollectionObjectStream<>(Arrays.asList(
        "P N B B-PER", "O N B B-ORG", "L N B B-LOC", "M N B B-MISC", ""));
    Conll03NameSampleStream stream = new Conll03NameSampleStream(
        Conll03NameSampleStream.LANGUAGE.EN, lines, 0);

    NameSample sample = stream.read();

    assertArrayEquals(new String[] {"P", "O", "L", "M"}, sample.getSentence());
    assertArrayEquals(new Span[0], sample.getNames());
  }

  @Test
  public void selectiveTypeMaskKeepsOnlyOrganizationAndMisc() throws Exception {
    CollectionObjectStream<String> lines = new CollectionObjectStream<>(Arrays.asList(
        "Ada N B B-PER", "at N B O", "OpenAI N B B-ORG", "from N B O",
        "London N B B-LOC", "event N B B-MISC", ""));
    int types = Conll02NameSampleStream.GENERATE_ORGANIZATION_ENTITIES
        | Conll02NameSampleStream.GENERATE_MISC_ENTITIES;
    Conll03NameSampleStream stream = new Conll03NameSampleStream(
        Conll03NameSampleStream.LANGUAGE.EN, lines, types);

    NameSample sample = stream.read();

    assertArrayEquals(new Span[] {new Span(2, 3, "organization"), new Span(5, 6, "misc")},
        sample.getNames());
  }

  @Test
  public void docStartMarksFollowingSampleForAdaptiveDataClearing() throws Exception {
    CollectionObjectStream<String> lines = new CollectionObjectStream<>(Arrays.asList(
        "-DOCSTART- -X- O O", "", "EU NNP B-NP B-ORG", "rejects VBZ B-VP O", ""));
    Conll03NameSampleStream stream = new Conll03NameSampleStream(
        Conll03NameSampleStream.LANGUAGE.EN, lines,
        Conll02NameSampleStream.GENERATE_ORGANIZATION_ENTITIES);

    NameSample sample = stream.read();

    assertArrayEquals(new String[] {"EU", "rejects"}, sample.getSentence());
    assertArrayEquals(new Span[] {new Span(0, 1, "organization")}, sample.getNames());
    assertTrue(sample.isClearAdaptiveDataSet());
  }

  @Test
  public void consecutiveEmptyLinesAreSkippedBetweenSamples() throws Exception {
    CollectionObjectStream<String> lines = new CollectionObjectStream<>(Arrays.asList(
        "First NN B-NP O", "", "", "", "Second NN B-NP O", "", ""));
    Conll03NameSampleStream stream = new Conll03NameSampleStream(
        Conll03NameSampleStream.LANGUAGE.EN, lines, 0);

    NameSample first = stream.read();
    NameSample second = stream.read();

    assertArrayEquals(new String[] {"First"}, first.getSentence());
    assertArrayEquals(new String[] {"Second"}, second.getSentence());
    assertNull(stream.read());
  }

  @Test
  public void resetRepeatsMultipleSamplesFromCollectionStream() throws Exception {
    CollectionObjectStream<String> lines = new CollectionObjectStream<>(Arrays.asList(
        "One NN B-NP O", "", "Two NN B-NP O", ""));
    Conll03NameSampleStream stream = new Conll03NameSampleStream(
        Conll03NameSampleStream.LANGUAGE.EN, lines, 0);

    assertArrayEquals(new String[] {"One"}, stream.read().getSentence());
    assertArrayEquals(new String[] {"Two"}, stream.read().getSentence());
    assertNull(stream.read());
    stream.reset();
    assertArrayEquals(new String[] {"One"}, stream.read().getSentence());
    assertArrayEquals(new String[] {"Two"}, stream.read().getSentence());
  }

  @Test
  public void inputStreamFactoryDecodesUtf8AndCanReset() throws Exception {
    byte[] data = "München NNP B-NP B-LOC\r\nist VAFIN B-VP O\r\n\r\n"
        .getBytes(StandardCharsets.UTF_8);
    Conll03NameSampleStream stream = new Conll03NameSampleStream(
        Conll03NameSampleStream.LANGUAGE.EN, () -> new ByteArrayInputStream(data),
        Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES);

    NameSample first = stream.read();
    stream.reset();
    NameSample repeated = stream.read();

    assertArrayEquals(new String[] {"München", "ist"}, first.getSentence());
    assertArrayEquals(new Span[] {new Span(0, 1, "location")}, first.getNames());
    assertEquals(first, repeated);
    stream.close();
  }

  @Test
  public void emptySourceAndSourceContainingOnlyEmptyLinesProduceNoSamples() throws Exception {
    Conll03NameSampleStream empty = new Conll03NameSampleStream(
        Conll03NameSampleStream.LANGUAGE.EN,
        new CollectionObjectStream<>(Collections.emptyList()), 0);
    Conll03NameSampleStream blanks = new Conll03NameSampleStream(
        Conll03NameSampleStream.LANGUAGE.EN,
        new CollectionObjectStream<>(Arrays.asList("", "", "")), 0);

    assertNull(empty.read());
    assertNull(blanks.read());
  }

  @Test
  public void rejectsEnglishLineWithWrongFieldCount() {
    Conll03NameSampleStream stream = new Conll03NameSampleStream(
        Conll03NameSampleStream.LANGUAGE.EN,
        new CollectionObjectStream<>(Collections.singletonList("Token POS CHUNK")), 0);

    IOException error = assertThrows(IOException.class, stream::read);

    assertEquals("Incorrect number of fields per line for language: 'Token POS CHUNK'!",
        error.getMessage());
  }

  @Test
  public void rejectsGermanLineWithWrongFieldCount() {
    Conll03NameSampleStream stream = new Conll03NameSampleStream(
        Conll03NameSampleStream.LANGUAGE.DE,
        new CollectionObjectStream<>(Collections.singletonList("Token POS CHUNK O")), 0);

    IOException error = assertThrows(IOException.class, stream::read);

    assertTrue(error.getMessage().contains("Incorrect number of fields per line"));
  }

  @Test
  public void rejectsTabsAndRepeatedSpacesBecauseColumnsAreSpaceDelimited() {
    Conll03NameSampleStream tabbed = new Conll03NameSampleStream(
        Conll03NameSampleStream.LANGUAGE.EN,
        new CollectionObjectStream<>(Collections.singletonList("Token\tPOS\tCHUNK\tO")), 0);
    Conll03NameSampleStream repeated = new Conll03NameSampleStream(
        Conll03NameSampleStream.LANGUAGE.EN,
        new CollectionObjectStream<>(Collections.singletonList("Token  POS CHUNK O")), 0);

    assertThrows(IOException.class, tabbed::read);
    assertThrows(IOException.class, repeated::read);
  }

  @Test
  public void rejectsInvalidTagWithoutBioPrefix() {
    Conll03NameSampleStream stream = new Conll03NameSampleStream(
        Conll03NameSampleStream.LANGUAGE.EN,
        new CollectionObjectStream<>(Arrays.asList("Token NN B-NP PER", "")),
        Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);

    IOException error = assertThrows(IOException.class, stream::read);

    assertEquals("Invalid tag: PER", error.getMessage());
  }

  @Test
  public void rejectsUnknownEntityTypeWhenSpanIsExtracted() {
    Conll03NameSampleStream stream = new Conll03NameSampleStream(
        Conll03NameSampleStream.LANGUAGE.EN,
        new CollectionObjectStream<>(Arrays.asList("Product NN B-NP B-PRODUCT", "")), -1);

    IOException error = assertThrows(IOException.class, stream::read);

    assertEquals("Unknown type: PRODUCT", error.getMessage());
  }

  @Test
  public void rejectsNonEmptyLineImmediatelyAfterDocStart() {
    Conll03NameSampleStream stream = new Conll03NameSampleStream(
        Conll03NameSampleStream.LANGUAGE.EN,
        new CollectionObjectStream<>(Arrays.asList(
            "-DOCSTART- -X- O O", "unexpected NN B-NP O")), 0);

    IOException error = assertThrows(IOException.class, stream::read);

    assertEquals("Empty line after -DOCSTART- not empty: 'unexpected NN B-NP O'!",
        error.getMessage());
  }

  @Test
  public void docStartAtEndOfSourceFailsBecauseRequiredBlankLineIsMissing() {
    Conll03NameSampleStream stream = new Conll03NameSampleStream(
        Conll03NameSampleStream.LANGUAGE.EN,
        new CollectionObjectStream<>(Collections.singletonList("-DOCSTART- -X- O O")), 0);

    assertThrows(NullPointerException.class, stream::read);
  }

  @Test
  public void nullLanguageRejectsOtherwiseValidRecord() {
    Conll03NameSampleStream stream = new Conll03NameSampleStream(null,
        new CollectionObjectStream<>(Collections.singletonList("Token NN B-NP O")), 0);

    IOException error = assertThrows(IOException.class, stream::read);

    assertTrue(error.getMessage().contains("Token NN B-NP O"));
  }
}
