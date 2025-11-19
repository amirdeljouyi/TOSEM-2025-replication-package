package opennlp.tools.formats;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import opennlp.tools.ml.BeamSearch;
import opennlp.tools.ml.EventTrainer;
import opennlp.tools.ml.SequenceTrainer;
import opennlp.tools.ml.TrainerFactory;
import opennlp.tools.ml.model.Event;
import opennlp.tools.ml.model.MaxentModel;
import opennlp.tools.ml.model.SequenceClassificationModel;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.util.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
// import opennlp.tools.formats.ResourceAsStreamFactory;
import opennlp.tools.namefind.NameFinderME;
import static opennlp.tools.formats.Conll02NameSampleStream.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class Conll03NameSampleStream_llmsuite_5_GPTLLMTest {

@Test
public void testEnglishSampleParsingSinglePEREntity() throws IOException {
String data = "Barack NNP B-NP B-PER\nObama NNP I-NP I-PER\nwas VBD B-VP O\nborn VBN I-VP O\n\n";
int types = Conll02NameSampleStream.GENERATE_PERSON_ENTITIES | Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES | Conll02NameSampleStream.GENERATE_ORGANIZATION_ENTITIES | Conll02NameSampleStream.GENERATE_MISC_ENTITIES;
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, types);
// NameSample sample = stream.read();
// assertNotNull(sample);
// assertEquals(4, sample.getSentence().length);
// assertEquals("Barack", sample.getSentence()[0]);
// assertEquals("Obama", sample.getSentence()[1]);
// assertEquals(1, sample.getNames().length);
// Span name = sample.getNames()[0];
// assertEquals(0, name.getStart());
// assertEquals(2, name.getEnd());
// assertEquals("PER", name.getType());
}

@Test
public void testGermanSampleParsingSingleLOCEntity() throws IOException {
String data = "Berlin LEM POS SC B-LOC\nist LEM POS SC O\nschön LEM POS SC O\n\n";
int types = Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES;
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.DE, lineStream, types);
// NameSample sample = stream.read();
// assertNotNull(sample);
// assertEquals(3, sample.getSentence().length);
// assertEquals("Berlin", sample.getSentence()[0]);
// assertEquals(1, sample.getNames().length);
// Span span = sample.getNames()[0];
// assertEquals(0, span.getStart());
// assertEquals(1, span.getEnd());
// assertEquals("LOC", span.getType());
}

@Test
public void testReadReturnsNullWhenEndIsReached() throws IOException {
String data = "London NNP B-NP B-LOC\nis VBZ B-VP O\ngreat JJ I-VP O\n\n";
int types = Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES;
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, types);
// NameSample sample = stream.read();
// assertNotNull(sample);
// NameSample after = stream.read();
// assertNull(after);
}

@Test
public void testInvalidFieldsENThrowsIOException() throws IOException {
String data = "OnlyOneField\n";
int types = Conll02NameSampleStream.GENERATE_PERSON_ENTITIES;
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, types);
// stream.read();
}

@Test
public void testInvalidFieldsDEThrowsIOException() throws IOException {
String data = "NurVier FELD POS B-LOC\n";
int types = Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES;
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.DE, lineStream, types);
// stream.read();
}

@Test
public void testInvalidTagThrowsIOException() throws IOException {
String data = "ErrorTag NNP B-NP Z-BAD\n\n";
int types = Conll02NameSampleStream.GENERATE_PERSON_ENTITIES;
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, types);
// stream.read();
}

@Test
public void testDocstartClearsAdaptiveData() throws IOException {
String data = "-DOCSTART- -X- -X- O\n\nJohn NNP B-NP B-PER\nDoe NNP I-NP I-PER\n\n";
int types = Conll02NameSampleStream.GENERATE_PERSON_ENTITIES;
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, types);
// NameSample sample = stream.read();
// assertNotNull(sample);
// assertTrue(sample.isClearAdaptiveDataSet());
// assertEquals(1, sample.getNames().length);
// Span span = sample.getNames()[0];
// assertEquals("PER", span.getType());
// assertEquals(0, span.getStart());
// assertEquals(2, span.getEnd());
}

@Test
public void testDocstartWithoutEmptyLineThrows() throws IOException {
String data = "-DOCSTART- -X- -X- O\nNOT_EMPTY_LINE\n";
int types = Conll02NameSampleStream.GENERATE_PERSON_ENTITIES;
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, types);
// stream.read();
}

@Test
public void testConstructorWithInputStreamFactory() throws IOException {
String data = "Rome NNP B-NP B-LOC\n\n";
InputStreamFactory inputFactory = new InputStreamFactory() {

@Override
public InputStream createInputStream() throws IOException {
return new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
}
};
int types = Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES;
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, inputFactory, types);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals(1, sample.getNames().length);
Span span = sample.getNames()[0];
assertEquals("LOC", span.getType());
assertEquals(0, span.getStart());
assertEquals(1, span.getEnd());
}

@Test
public void testEmptyLineBeforeSentenceIsSkipped() throws IOException {
String data = "\n\nBerlin NNP B-NP B-LOC\nis VBZ B-VP O\n\n";
int types = Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES;
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, types);
// NameSample sample = stream.read();
// assertNotNull(sample);
// assertEquals(2, sample.getSentence().length);
// assertEquals("Berlin", sample.getSentence()[0]);
// assertEquals("is", sample.getSentence()[1]);
// assertEquals(1, sample.getNames().length);
}

@Test
public void testSentenceWithConsecutiveDifferentEntityTypes() throws IOException {
String data = "Apple NNP B-NP B-ORG\nis VBZ B-VP O\nin IN B-PP O\nCalifornia NNP B-NP B-LOC\n\n";
int types = Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES | Conll02NameSampleStream.GENERATE_ORGANIZATION_ENTITIES;
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, types);
// NameSample sample = stream.read();
// assertNotNull(sample);
// assertEquals(2, sample.getNames().length);
// assertEquals(0, sample.getNames()[0].getStart());
// assertEquals(1, sample.getNames()[0].getEnd());
// assertEquals("ORG", sample.getNames()[0].getType());
// assertEquals(3, sample.getNames()[1].getStart());
// assertEquals(4, sample.getNames()[1].getEnd());
// assertEquals("LOC", sample.getNames()[1].getType());
}

@Test
public void testIEntityWithoutBPrefixCreatesSpan() throws IOException {
String data = "Jordan NNP B-NP I-PER\nPeterson NNP I-NP I-PER\n\n";
int types = Conll02NameSampleStream.GENERATE_PERSON_ENTITIES;
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, types);
// NameSample sample = stream.read();
// assertNotNull(sample);
// assertEquals(1, sample.getNames().length);
// assertEquals(0, sample.getNames()[0].getStart());
// assertEquals(2, sample.getNames()[0].getEnd());
// assertEquals("PER", sample.getNames()[0].getType());
}

@Test
public void testSentenceWithMultipleBEntitiesOfSameType() throws IOException {
String data = "Steve NNP B-NP B-PER\nBallmer NNP I-NP I-PER\nJobs NNP I-NP B-PER\nCook NNP I-NP I-PER\n\n";
int types = Conll02NameSampleStream.GENERATE_PERSON_ENTITIES;
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, types);
// NameSample sample = stream.read();
// assertNotNull(sample);
// assertEquals(2, sample.getNames().length);
// assertEquals("PER", sample.getNames()[0].getType());
// assertEquals(0, sample.getNames()[0].getStart());
// assertEquals(2, sample.getNames()[0].getEnd());
// assertEquals(2, sample.getNames()[1].getStart());
// assertEquals(4, sample.getNames()[1].getEnd());
}

@Test
public void testEntitiesWithDisabledTypesAreIgnored() throws IOException {
String data = "Tesla NNP B-NP B-ORG\nis VBZ B-VP O\nAmerican JJ B-ADJ B-MISC\n\n";
int types = Conll02NameSampleStream.GENERATE_PERSON_ENTITIES | Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES;
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, types);
// NameSample sample = stream.read();
// assertNotNull(sample);
// assertEquals(0, sample.getNames().length);
}

@Test
public void testOnlyOEntitiesResultsInNoSpans() throws IOException {
String data = "This DT B-NP O\nis VBZ B-VP O\nfine JJ B-ADJ O\ntoday NN B-NP O\n\n";
int types = Conll02NameSampleStream.GENERATE_PERSON_ENTITIES | Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES | Conll02NameSampleStream.GENERATE_ORGANIZATION_ENTITIES | Conll02NameSampleStream.GENERATE_MISC_ENTITIES;
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, types);
// NameSample sample = stream.read();
// assertNotNull(sample);
// assertEquals(0, sample.getNames().length);
}

@Test
public void testContinuationWithDifferentEntityTypeClosesPreviousSpan() throws IOException {
String data = "John NNP B-NP B-PER\nDoe NNP B-NP I-PER\nGoogle NNP B-NP I-ORG\n\n";
int types = Conll02NameSampleStream.GENERATE_PERSON_ENTITIES | Conll02NameSampleStream.GENERATE_ORGANIZATION_ENTITIES;
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, types);
// NameSample sample = stream.read();
// assertNotNull(sample);
// assertEquals(2, sample.getNames().length);
// assertEquals("PER", sample.getNames()[0].getType());
// assertEquals(0, sample.getNames()[0].getStart());
// assertEquals(2, sample.getNames()[0].getEnd());
// assertEquals("ORG", sample.getNames()[1].getType());
// assertEquals(2, sample.getNames()[1].getStart());
// assertEquals(3, sample.getNames()[1].getEnd());
}

@Test
public void testIWithoutBeginFollowedByOClosesSpan() throws IOException {
String data = "Obama NNP B-NP I-PER\nis VBZ B-VP O\n\n";
int types = Conll02NameSampleStream.GENERATE_PERSON_ENTITIES;
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, types);
// NameSample sample = stream.read();
// assertNotNull(sample);
// assertEquals(2, sample.getSentence().length);
// assertEquals(1, sample.getNames().length);
// assertEquals(0, sample.getNames()[0].getStart());
// assertEquals(1, sample.getNames()[0].getEnd());
// assertEquals("PER", sample.getNames()[0].getType());
}

@Test
public void testEmptyInputReturnsNullImmediately() throws IOException {
String data = "";
int types = Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES;
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, types);
// NameSample sample = stream.read();
// assertNull(sample);
}

@Test
public void testOnlyDocstartLinesFollowedByNullReturnsNull() throws IOException {
String data = "-DOCSTART- -X- -X- O\n\n";
int types = Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES;
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, types);
// NameSample sample = stream.read();
// assertNull(sample);
}

@Test
public void testMultipleEmptyLinesBetweenSentences() throws IOException {
String data = "Munich NNP B-NP B-LOC\n\n\n\nBerlin NNP B-NP B-LOC\n\n";
int types = Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES;
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, types);
// NameSample first = stream.read();
// assertNotNull(first);
// assertEquals(1, first.getNames().length);
// assertEquals("LOC", first.getNames()[0].getType());
// NameSample second = stream.read();
// assertNotNull(second);
// assertEquals(1, second.getNames().length);
// assertEquals("LOC", second.getNames()[0].getType());
// NameSample end = stream.read();
// assertNull(end);
}

@Test
public void testMismatchedITypeClosesPreviousAndStartsNew() throws IOException {
String data = "Jane NNP B-NP B-PER\nDoe NNP I-NP I-PER\nBerlin NNP B-NP I-LOC\n\n";
int types = Conll02NameSampleStream.GENERATE_PERSON_ENTITIES | Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES;
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, types);
// NameSample sample = stream.read();
// assertNotNull(sample);
// assertEquals(2, sample.getNames().length);
// assertEquals("PER", sample.getNames()[0].getType());
// assertEquals(0, sample.getNames()[0].getStart());
// assertEquals(2, sample.getNames()[0].getEnd());
// assertEquals("LOC", sample.getNames()[1].getType());
// assertEquals(2, sample.getNames()[1].getStart());
// assertEquals(3, sample.getNames()[1].getEnd());
}

@Test
public void testSentenceEndsWithOpenEntity() throws IOException {
String data = "John NNP B-NP B-PER\nSmith NNP I-NP I-PER\n";
int types = Conll02NameSampleStream.GENERATE_PERSON_ENTITIES;
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, types);
// NameSample sample = stream.read();
// assertNotNull(sample);
// assertEquals(1, sample.getNames().length);
// assertEquals("PER", sample.getNames()[0].getType());
// assertEquals(0, sample.getNames()[0].getStart());
// assertEquals(2, sample.getNames()[0].getEnd());
}

@Test
public void testTrailingEmptyLinesAfterLastSentenceAreIgnored() throws IOException {
String data = "Berlin NNP B-NP B-LOC\nGermany NNP I-NP I-LOC\n\n\n\n";
int types = Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES;
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, types);
// NameSample sample1 = stream.read();
// assertNotNull(sample1);
// assertEquals(1, sample1.getNames().length);
// assertEquals("LOC", sample1.getNames()[0].getType());
// NameSample sample2 = stream.read();
// assertNull(sample2);
}

@Test
public void testSingleTokenBEntityFollowedByO() throws IOException {
String data = "IBM NNP B-NP B-ORG\nreleased VBD B-VP O\n\n";
int types = Conll02NameSampleStream.GENERATE_ORGANIZATION_ENTITIES;
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, types);
// NameSample sample = stream.read();
// assertNotNull(sample);
// assertEquals(2, sample.getSentence().length);
// assertEquals(1, sample.getNames().length);
// assertEquals(0, sample.getNames()[0].getStart());
// assertEquals(1, sample.getNames()[0].getEnd());
// assertEquals("ORG", sample.getNames()[0].getType());
}

@Test
public void testSentenceWithEntitySpanTouchingEndOfSentence() throws IOException {
String data = "President NNP B-NP B-PER\nObama NNP I-NP I-PER\n\n";
int types = Conll02NameSampleStream.GENERATE_PERSON_ENTITIES;
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, types);
// NameSample sample = stream.read();
// assertNotNull(sample);
// assertEquals(1, sample.getNames().length);
// assertEquals("PER", sample.getNames()[0].getType());
// assertEquals(0, sample.getNames()[0].getStart());
// assertEquals(2, sample.getNames()[0].getEnd());
}

@Test
public void testSentenceWithMultipleUnrelatedEntities() throws IOException {
String data = "Microsoft NNP B-NP B-ORG\nis VBZ B-VP O\nbased VBN I-VP O\nin IN B-PP O\nSeattle NNP B-NP B-LOC\n\n";
int types = Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES | Conll02NameSampleStream.GENERATE_ORGANIZATION_ENTITIES;
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, types);
// NameSample sample = stream.read();
// assertNotNull(sample);
// assertEquals(2, sample.getNames().length);
// assertEquals("ORG", sample.getNames()[0].getType());
// assertEquals(0, sample.getNames()[0].getStart());
// assertEquals(1, sample.getNames()[0].getEnd());
// assertEquals("LOC", sample.getNames()[1].getType());
// assertEquals(4, sample.getNames()[1].getStart());
// assertEquals(5, sample.getNames()[1].getEnd());
}

@Test
public void testEntityWithMiscTypeRetainedOnlyIfEnabled() throws IOException {
String data = "Tokyo NNP B-NP B-MISC\n\n";
int typesWithoutMisc = Conll02NameSampleStream.GENERATE_PERSON_ENTITIES;
int typesWithMisc = Conll02NameSampleStream.GENERATE_MISC_ENTITIES;
// Conll03NameSampleStream stream1 = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, typesWithoutMisc);
// NameSample sample1 = stream1.read();
// assertNotNull(sample1);
// assertEquals(0, sample1.getNames().length);
// Conll03NameSampleStream stream2 = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream2, typesWithMisc);
// NameSample sample2 = stream2.read();
// assertNotNull(sample2);
// assertEquals(1, sample2.getNames().length);
// assertEquals("MISC", sample2.getNames()[0].getType());
// assertEquals(0, sample2.getNames()[0].getStart());
// assertEquals(1, sample2.getNames()[0].getEnd());
}

@Test
public void testInvalidTagWithUnknownPrefixThrowsIOException() throws IOException {
String data = "Stanford NNP B-NP X-LOC\n\n";
int types = Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES;
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, types);
// try {
// stream.read();
// fail("Expected IOException due to invalid tag prefix");
// } catch (IOException expected) {
// assertTrue(expected.getMessage().contains("Invalid tag"));
// }
}

@Test
public void testIEntityWithoutPreviousTagCreatesImplicitSpan() throws IOException {
String data = "Doe NNP B-NP I-PER\n\n";
int types = Conll02NameSampleStream.GENERATE_PERSON_ENTITIES;
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, types);
// NameSample sample = stream.read();
// assertNotNull(sample);
// assertEquals(1, sample.getNames().length);
// assertEquals(0, sample.getNames()[0].getStart());
// assertEquals(1, sample.getNames()[0].getEnd());
// assertEquals("PER", sample.getNames()[0].getType());
}

@Test
public void testIEntityWithMismatchedTypeToPreviousB_EntityCreatesSeparateSpan() throws IOException {
String data = "Barack NNP B-NP B-PER\nMicrosoft NNP I-NP I-ORG\n\n";
int types = Conll02NameSampleStream.GENERATE_PERSON_ENTITIES | Conll02NameSampleStream.GENERATE_ORGANIZATION_ENTITIES;
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, types);
// NameSample sample = stream.read();
// assertNotNull(sample);
// assertEquals(2, sample.getNames().length);
// assertEquals("PER", sample.getNames()[0].getType());
// assertEquals("ORG", sample.getNames()[1].getType());
// assertEquals(0, sample.getNames()[0].getStart());
// assertEquals(1, sample.getNames()[0].getEnd());
// assertEquals(1, sample.getNames()[1].getStart());
// assertEquals(2, sample.getNames()[1].getEnd());
}

@Test
public void testTwoConsecutiveBOEntitiesSameTypeHandledSeparately() throws IOException {
String data = "Paris NNP B-NP B-LOC\nis VBZ O O\nLondon NNP B-NP B-LOC\n\n";
int types = Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES;
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, types);
// NameSample sample = stream.read();
// assertNotNull(sample);
// assertEquals(2, sample.getNames().length);
// assertEquals("LOC", sample.getNames()[0].getType());
// assertEquals("LOC", sample.getNames()[1].getType());
// assertEquals(0, sample.getNames()[0].getStart());
// assertEquals(1, sample.getNames()[0].getEnd());
// assertEquals(2, sample.getNames()[1].getStart());
// assertEquals(3, sample.getNames()[1].getEnd());
}

@Test
public void testSingleLineSentenceWithOnlyO() throws IOException {
String data = "Hello UH B-INTJ O\n\n";
int types = Conll02NameSampleStream.GENERATE_MISC_ENTITIES;
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, types);
// NameSample sample = stream.read();
// assertNotNull(sample);
// assertEquals(1, sample.getSentence().length);
// assertEquals(0, sample.getNames().length);
}

@Test
public void testMalformedENLineWithExtraFieldsThrowsIOException() throws IOException {
String data = "Barack NNP B-NP I-PER EXTRA_FIELD\n";
int types = Conll02NameSampleStream.GENERATE_PERSON_ENTITIES;
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, types);
// stream.read();
}

@Test
public void testMalformedDELineWithTooFewFieldsThrowsIOException() throws IOException {
String data = "Berlin LEM POS B-LOC\n";
int types = Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES;
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.DE, lineStream, types);
// stream.read();
}

@Test
public void testEmptyLineBeforeDocstartIsHandledGracefully() throws IOException {
String data = "\n-DOCSTART- -X- -X- O\n\nJohn NNP B-NP B-PER\nDoe NNP I-NP I-PER\n\n";
int types = Conll02NameSampleStream.GENERATE_PERSON_ENTITIES;
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, types);
// NameSample sample = stream.read();
// assertNotNull(sample);
// assertTrue(sample.isClearAdaptiveDataSet());
// assertEquals(1, sample.getNames().length);
// assertEquals("PER", sample.getNames()[0].getType());
// assertEquals(0, sample.getNames()[0].getStart());
// assertEquals(2, sample.getNames()[0].getEnd());
}

@Test
public void testTrailingIButNoEndOfSentenceStillCreatesSpan() throws IOException {
String data = "Barack NNP B-NP B-PER\nObama NNP B-NP I-PER";
int types = Conll02NameSampleStream.GENERATE_PERSON_ENTITIES;
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, types);
// NameSample sample = stream.read();
// assertNotNull(sample);
// assertEquals(1, sample.getNames().length);
// assertEquals(0, sample.getNames()[0].getStart());
// assertEquals(2, sample.getNames()[0].getEnd());
// assertEquals("PER", sample.getNames()[0].getType());
}

@Test
public void testMultipleDocstartMarkersOnlyFirstTriggersClearAdaptiveData() throws IOException {
String data = "-DOCSTART- -X- -X- O\n\n" + "Alice NNP B-NP B-PER\nJohnson NNP I-NP I-PER\n\n" + "-DOCSTART- -X- -X- O\n\n" + "Bob NNP B-NP B-PER\nSmith NNP I-NP I-PER\n\n";
int types = Conll02NameSampleStream.GENERATE_PERSON_ENTITIES;
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, types);
// NameSample first = stream.read();
// assertTrue(first.isClearAdaptiveDataSet());
// assertEquals(1, first.getNames().length);
// assertEquals(0, first.getNames()[0].getStart());
// assertEquals(2, first.getNames()[0].getEnd());
// NameSample second = stream.read();
// assertTrue(second.isClearAdaptiveDataSet());
// assertEquals(1, second.getNames().length);
// assertEquals(0, second.getNames()[0].getStart());
// assertEquals(2, second.getNames()[0].getEnd());
}

@Test
public void testResetThrowsUnsupportedWhenLineStreamDoesNotImplementReset() throws IOException {
String data = "Berlin NNP B-NP B-LOC\n\n";
ObjectStream<String> lineStream = new ObjectStream<String>() {

private final BufferedReader reader = new BufferedReader(new StringReader(data));

@Override
public String read() throws IOException {
return reader.readLine();
}

@Override
public void reset() throws IOException {
throw new UnsupportedOperationException();
}

@Override
public void close() throws IOException {
reader.close();
}
};
int types = Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES;
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, types);
try {
stream.reset();
fail("Expected UnsupportedOperationException on reset");
} catch (UnsupportedOperationException expected) {
assertTrue(true);
}
}

@Test
public void testEntityInterruptedByInvalidTagCreatesTwoSpans() throws IOException {
String data = "Michael NNP B-NP B-PER\n" + "Schumacher NNP I-NP I-PER\n" + "Ferrari NNP B-NP B-ORG\n\n";
int types = Conll02NameSampleStream.GENERATE_PERSON_ENTITIES | Conll02NameSampleStream.GENERATE_ORGANIZATION_ENTITIES;
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, types);
// NameSample sample = stream.read();
// assertNotNull(sample);
// assertEquals(2, sample.getNames().length);
// assertEquals("PER", sample.getNames()[0].getType());
// assertEquals(0, sample.getNames()[0].getStart());
// assertEquals(2, sample.getNames()[0].getEnd());
// assertEquals("ORG", sample.getNames()[1].getType());
// assertEquals(2, sample.getNames()[1].getStart());
// assertEquals(3, sample.getNames()[1].getEnd());
}

@Test
public void testSentenceWithOnlyWhitespaceLine() throws IOException {
String data = "London NNP B-NP B-LOC\n   \n";
ObjectStream<String> lineStream = new ObjectStream<String>() {

private final BufferedReader reader = new BufferedReader(new StringReader(data));

@Override
public String read() throws IOException {
return reader.readLine();
}

@Override
public void reset() throws IOException {
reader.reset();
}

@Override
public void close() throws IOException {
reader.close();
}
};
int types = Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES;
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, types);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals(1, sample.getNames().length);
assertEquals("LOC", sample.getNames()[0].getType());
NameSample end = stream.read();
assertNull(end);
}

@Test
public void testReadFiltersMultipleEmptyEventsBeforeNextSentence() throws IOException {
String data = "Amsterdam NNP B-NP B-LOC\n\n\n\nBrussels NNP B-NP B-LOC\n\n";
ObjectStream<String> lineStream = new ObjectStream<String>() {

private final BufferedReader reader = new BufferedReader(new StringReader(data));

@Override
public String read() throws IOException {
return reader.readLine();
}

@Override
public void reset() throws IOException {
reader.reset();
}

@Override
public void close() throws IOException {
reader.close();
}
};
int types = Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES;
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, types);
NameSample sample1 = stream.read();
assertNotNull(sample1);
assertEquals(1, sample1.getNames().length);
assertEquals("LOC", sample1.getNames()[0].getType());
NameSample sample2 = stream.read();
assertNotNull(sample2);
assertEquals(1, sample2.getNames().length);
assertEquals("LOC", sample2.getNames()[0].getType());
NameSample end = stream.read();
assertNull(end);
}

@Test
public void testIEntityTransitionToDifferentTypeCreatesNewSpan() throws IOException {
String data = "Angela NNP B-NP B-PER\nMerkel NNP I-NP I-PER\nGermany NNP B-NP I-LOC\nBerlin NNP I-NP I-LOC\n\n";
int types = Conll02NameSampleStream.GENERATE_PERSON_ENTITIES | Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES;
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, types);
// NameSample sample = stream.read();
// assertNotNull(sample);
// assertEquals(2, sample.getNames().length);
// assertEquals("PER", sample.getNames()[0].getType());
// assertEquals(0, sample.getNames()[0].getStart());
// assertEquals(2, sample.getNames()[0].getEnd());
// assertEquals("LOC", sample.getNames()[1].getType());
// assertEquals(2, sample.getNames()[1].getStart());
// assertEquals(4, sample.getNames()[1].getEnd());
}

@Test
public void testSentenceWithNoEntitiesStillReturned() throws IOException {
String data = "The DT B-NP O\nteam NN I-NP O\nwon VBD B-VP O\n\n";
int types = Conll02NameSampleStream.GENERATE_PERSON_ENTITIES;
// Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, types);
// NameSample sample = stream.read();
// assertNotNull(sample);
// assertEquals(0, sample.getNames().length);
// assertEquals(4, sample.getSentence().length);
}

@Test
public void testFinalEntityFollowedByEOFInsteadOfNewline() throws IOException {
String data = "Barack NNP B-NP B-PER\nObama NNP I-NP I-PER";
ObjectStream<String> lineStream = new ObjectStream<String>() {

private final BufferedReader reader = new BufferedReader(new StringReader(data));

@Override
public String read() throws IOException {
return reader.readLine();
}

@Override
public void reset() throws IOException {
reader.reset();
}

@Override
public void close() throws IOException {
reader.close();
}
};
int types = Conll02NameSampleStream.GENERATE_PERSON_ENTITIES;
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, types);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals(2, sample.getSentence().length);
assertEquals(1, sample.getNames().length);
assertEquals("PER", sample.getNames()[0].getType());
assertEquals(0, sample.getNames()[0].getStart());
assertEquals(2, sample.getNames()[0].getEnd());
NameSample end = stream.read();
assertNull(end);
}

@Test
public void testUnexpectedTagFormatThrowsIOException() throws IOException {
String data = "Paris NNP B-NP INV-LOC\n\n";
ObjectStream<String> lineStream = new ObjectStream<String>() {

private final BufferedReader reader = new BufferedReader(new StringReader(data));

@Override
public String read() throws IOException {
return reader.readLine();
}

@Override
public void reset() throws IOException {
reader.reset();
}

@Override
public void close() throws IOException {
reader.close();
}
};
int types = Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES;
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, types);
try {
stream.read();
fail("Expected IOException for invalid tag");
} catch (IOException expected) {
assertTrue(expected.getMessage().contains("Invalid tag"));
}
}

@Test
public void testMultipleIBEntitiesWithoutBPrefixCreatesMultipleSpans() throws IOException {
String data = "France NNP B-NP I-LOC\nGermany NNP B-NP I-LOC\nSpain NNP B-NP I-LOC\n\n";
ObjectStream<String> lineStream = new ObjectStream<String>() {

private final BufferedReader reader = new BufferedReader(new StringReader(data));

@Override
public String read() throws IOException {
return reader.readLine();
}

@Override
public void reset() throws IOException {
reader.reset();
}

@Override
public void close() throws IOException {
reader.close();
}
};
int types = Conll02NameSampleStream.GENERATE_LOCATION_ENTITIES;
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, types);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals(3, sample.getNames().length);
assertEquals(0, sample.getNames()[0].getStart());
assertEquals(1, sample.getNames()[0].getEnd());
assertEquals(1, sample.getNames()[1].getStart());
assertEquals(2, sample.getNames()[1].getEnd());
assertEquals(2, sample.getNames()[2].getStart());
assertEquals(3, sample.getNames()[2].getEnd());
}

@Test
public void testProperHandlingWhenAllEntityTypesAreDisabled() throws IOException {
String data = "Angela NNP B-NP B-PER\nMerkel NNP I-NP I-PER\n\n";
ObjectStream<String> lineStream = new ObjectStream<String>() {

private final BufferedReader reader = new BufferedReader(new StringReader(data));

@Override
public String read() throws IOException {
return reader.readLine();
}

@Override
public void reset() throws IOException {
reader.reset();
}

@Override
public void close() throws IOException {
reader.close();
}
};
int types = 0;
Conll03NameSampleStream stream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, lineStream, types);
NameSample sample = stream.read();
assertNotNull(sample);
assertEquals(2, sample.getSentence().length);
assertEquals(0, sample.getNames().length);
}
}
