package gate.util;

import static org.junit.Assert.*;

import gate.Annotation;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Test;

public class CorpusBenchmarkToolPromptTest {

  @Test
  public void constructorProvidesDocumentedDefaultModesAndThreshold() {
    CorpusBenchmarkTool tool = new CorpusBenchmarkTool();

    assertFalse(tool.getGenerateMode());
    assertFalse(tool.isGenerateMode());
    assertFalse(tool.getVerboseMode());
    assertFalse(tool.getMoreInfo());
    assertFalse(tool.getMarkedStored());
    assertFalse(tool.getMarkedClean());
    assertFalse(tool.getMarkedDS());
    assertEquals(0.5, tool.getThreshold(), 0.0);
    assertNull(tool.getStartDirectory());
    assertNull(tool.getDiffFeaturesList());
  }

  @Test
  public void modePropertiesCanBeEnabledAndDisabledIndependently() {
    CorpusBenchmarkTool tool = new CorpusBenchmarkTool();

    tool.setGenerateMode(true);
    tool.setVerboseMode(true);
    tool.setMoreInfo(true);
    tool.setMarkedStored(true);
    tool.setMarkedClean(true);
    tool.setMarkedDS(true);

    assertTrue(tool.getGenerateMode());
    assertTrue(tool.isGenerateMode());
    assertTrue(tool.getVerboseMode());
    assertTrue(tool.getMoreInfo());
    assertTrue(tool.getMarkedStored());
    assertTrue(tool.getMarkedClean());
    assertTrue(tool.getMarkedDS());

    tool.setGenerateMode(false);
    tool.setVerboseMode(false);
    tool.setMoreInfo(false);
    tool.setMarkedStored(false);
    tool.setMarkedClean(false);
    tool.setMarkedDS(false);

    assertFalse(tool.getGenerateMode());
    assertFalse(tool.getVerboseMode());
    assertFalse(tool.getMoreInfo());
    assertFalse(tool.getMarkedStored());
    assertFalse(tool.getMarkedClean());
    assertFalse(tool.getMarkedDS());
  }

  @Test
  public void thresholdAcceptsBoundaryAndSpecialDoubleValues() {
    CorpusBenchmarkTool tool = new CorpusBenchmarkTool();

    tool.setThreshold(0.0);
    assertEquals(0.0, tool.getThreshold(), 0.0);
    tool.setThreshold(1.0);
    assertEquals(1.0, tool.getThreshold(), 0.0);
    tool.setThreshold(-0.25);
    assertEquals(-0.25, tool.getThreshold(), 0.0);
    tool.setThreshold(Double.POSITIVE_INFINITY);
    assertEquals(Double.POSITIVE_INFINITY, tool.getThreshold(), 0.0);
    tool.setThreshold(Double.NaN);
    assertTrue(Double.isNaN(tool.getThreshold()));
  }

  @Test
  public void startDirectoryRoundTripsExactFileObjectIncludingNull() {
    CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
    File directory = new File("relative-corpus-root");

    tool.setStartDirectory(directory);
    assertSame(directory, tool.getStartDirectory());
    tool.setStartDirectory(null);
    assertNull(tool.getStartDirectory());
  }

  @Test
  public void significantFeatureSetRoundTripsWithoutCopying() {
    CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
    Set<String> features = new LinkedHashSet<String>();
    features.add("class");
    features.add("inst");

    tool.setDiffFeaturesList(features);

    assertSame(features, tool.getDiffFeaturesList());
    features.add("gender");
    assertEquals(new LinkedHashSet<String>(Arrays.asList("class", "inst", "gender")),
        tool.getDiffFeaturesList());
    tool.setDiffFeaturesList(null);
    assertNull(tool.getDiffFeaturesList());
  }

  @Test
  public void averagesAreNaNBeforeAnyAnnotationTypeIsEvaluated() {
    CorpusBenchmarkTool tool = new CorpusBenchmarkTool();

    assertTrue(Double.isNaN(tool.getPrecisionAverage()));
    assertTrue(Double.isNaN(tool.getRecallAverage()));
    assertTrue(Double.isNaN(tool.getFMeasureAverage()));
    assertTrue(Double.isNaN(tool.getPrecisionAverageProc()));
    assertTrue(Double.isNaN(tool.getRecallAverageProc()));
    assertTrue(Double.isNaN(tool.getFMeasureAverageProc()));
    assertEquals(0.0, tool.getPrecisionAverageCalc(), 0.0);
    assertEquals(0.0, tool.getRecallAverageCalc(), 0.0);
    assertEquals(0.0, tool.getFmeasureAverageCalc(), 0.0);
  }

  @Test
  public void executeWithNullDirectoryIsANoOp() {
    CorpusBenchmarkTool tool = new CorpusBenchmarkTool();

    tool.execute((File)null);

    assertFalse(tool.getMarkedClean());
    assertTrue(Double.isNaN(tool.getPrecisionAverage()));
  }

  @Test
  public void executeWithOrdinaryFileIsANoOp() throws Exception {
    Path file = java.nio.file.Files.createTempFile("benchmark-not-dir", ".txt");
    CorpusBenchmarkTool tool = new CorpusBenchmarkTool();

    tool.execute(file.toFile());

    assertFalse(tool.getMarkedClean());
    assertTrue(Double.isNaN(tool.getRecallAverage()));
  }

  @Test
  public void executeIgnoresDirectoryWithoutCleanChild() throws Exception {
    Path root = java.nio.file.Files.createTempDirectory("benchmark-no-clean");
    java.nio.file.Files.createDirectory(root.resolve("unrelated"));
    CorpusBenchmarkTool tool = new CorpusBenchmarkTool();

    tool.execute(root.toFile());

    assertFalse(tool.getMarkedClean());
    assertFalse(java.nio.file.Files.exists(root.resolve("processed")));
  }

  @Test
  public void evaluationWithoutProcessedDirectorySwitchesToMarkedCleanMode() throws Exception {
    Path root = java.nio.file.Files.createTempDirectory("benchmark-clean-only");
    java.nio.file.Files.createDirectory(root.resolve("clean"));
    CorpusBenchmarkTool tool = new CorpusBenchmarkTool();

    tool.execute(root.toFile());

    assertTrue(tool.getMarkedClean());
    assertTrue(Double.isNaN(tool.getPrecisionAverage()));
  }

  @Test
  public void markedStoredEvaluationWithoutProcessedDirectoryReturnsWithoutChangingMode() throws Exception {
    Path root = java.nio.file.Files.createTempDirectory("benchmark-stored-no-output");
    java.nio.file.Files.createDirectory(root.resolve("clean"));
    java.nio.file.Files.createDirectory(root.resolve("marked"));
    CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
    tool.setMarkedStored(true);

    tool.execute(root.toFile());

    assertTrue(tool.getMarkedStored());
    assertFalse(tool.getMarkedClean());
    assertTrue(Double.isNaN(tool.getFMeasureAverage()));
  }

  @Test
  public void initWithoutPropertiesKeepsDefaultsWhenApplicationIsNotNeeded() throws Exception {
    Path root = java.nio.file.Files.createTempDirectory("benchmark-default-config");
    CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
    tool.setStartDirectory(root.toFile());
    tool.setMarkedStored(true);

    tool.init();

    assertEquals(0.5, tool.getThreshold(), 0.0);
    assertNull(tool.getDiffFeaturesList());
  }

  @Test
  public void initLoadsTrimmedThresholdAndFeatureNames() throws Exception {
    Path root = java.nio.file.Files.createTempDirectory("benchmark-config");
    java.nio.file.Files.write(root.resolve("corpus_tool.properties"),
        ("threshold =  0.875  \n" +
         "annotFeatures = class;inst;gender\n" +
         "annotTypes = Person;Location\n" +
         "annotSetName = KeySet\n" +
         "outputSetName = ResponseSet\n" +
         "encoding = UTF-8\n").getBytes(StandardCharsets.ISO_8859_1));
    CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
    tool.setStartDirectory(root.toFile());
    tool.setMarkedStored(true);

    tool.init();

    assertEquals(0.875, tool.getThreshold(), 0.0);
    assertEquals(new LinkedHashSet<String>(Arrays.asList("class", "inst", "gender")),
        tool.getDiffFeaturesList());
  }

  @Test(expected = NumberFormatException.class)
  public void initRejectsMalformedThreshold() throws Exception {
    Path root = java.nio.file.Files.createTempDirectory("benchmark-bad-threshold");
    java.nio.file.Files.write(root.resolve("corpus_tool.properties"),
        "threshold=not-a-number\n".getBytes(StandardCharsets.ISO_8859_1));
    CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
    tool.setStartDirectory(root.toFile());
    tool.setMarkedStored(true);

    tool.init();
  }

  @Test
  public void initPRsRequiresAnApplicationFile() {
    CorpusBenchmarkTool tool = new CorpusBenchmarkTool();

    try {
      tool.initPRs();
      fail("Expected an application configuration failure");
    } catch (GateRuntimeException expected) {
      assertEquals("Application not set!", expected.getMessage());
    }
  }

  @Test
  public void unloadPRsIsSafeBeforeAndAfterMarkedStoredMode() {
    CorpusBenchmarkTool tool = new CorpusBenchmarkTool();

    tool.unloadPRs();
    tool.setMarkedStored(true);
    tool.unloadPRs();

    assertTrue(tool.getMarkedStored());
  }

  @Test
  public void countWordsHandlesNullAndDocumentsWithoutTokens() throws Exception {
    Gate.init();
    CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
    Document document = Factory.newDocument("No annotations here");

    assertEquals(0, tool.countWords(null));
    assertEquals(0, tool.countWords(document));

    Factory.deleteResource(document);
  }

  @Test
  public void countWordsCountsOnlyCaseInsensitiveWordKind() throws Exception {
    Gate.init();
    CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
    Document document = Factory.newDocument("One two punctuation unknown");
    FeatureMap lowerWord = Factory.newFeatureMap();
    lowerWord.put("kind", "word");
    FeatureMap upperWord = Factory.newFeatureMap();
    upperWord.put("kind", "WORD");
    FeatureMap punctuation = Factory.newFeatureMap();
    punctuation.put("kind", "punctuation");
    document.getAnnotations().add(0L, 3L, "Token", lowerWord);
    document.getAnnotations().add(4L, 7L, "Token", upperWord);
    document.getAnnotations().add(7L, 8L, "Token", punctuation);
    document.getAnnotations().add(9L, 16L, "Token", Factory.newFeatureMap());

    assertEquals(2, tool.countWords(document));

    Factory.deleteResource(document);
  }

  @Test
  public void countWordsUsesConfiguredOutputAnnotationSet() throws Exception {
    Gate.init();
    Path root = java.nio.file.Files.createTempDirectory("benchmark-output-set");
    java.nio.file.Files.write(root.resolve("corpus_tool.properties"),
        "outputSetName=Automatic\n".getBytes(StandardCharsets.ISO_8859_1));
    CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
    tool.setStartDirectory(root.toFile());
    tool.setMarkedStored(true);
    tool.init();
    Document document = Factory.newDocument("alpha beta");
    FeatureMap word = Factory.newFeatureMap();
    word.put("kind", "word");
    document.getAnnotations().add(0L, 5L, "Token", word);
    document.getAnnotations("Automatic").add(6L, 10L, "Token", word);

    assertEquals(1, tool.countWords(document));

    Factory.deleteResource(document);
  }

  @Test
  public void measureDocsReturnsNullForMissingDocumentsAndEmptyDiffForMissingKeyType() throws Exception {
    Gate.init();
    CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
    tool.setDiffFeaturesList(Collections.<String>emptySet());
    Document key = Factory.newDocument("John works");
    Document response = Factory.newDocument("John works");

    assertNull(tool.measureDocs(null, response, "Person"));
    assertNull(tool.measureDocs(key, null, "Person"));
    AnnotationDiffer emptyDiffer = tool.measureDocs(key, response, "Person");
    assertNotNull(emptyDiffer);
    assertEquals(0, emptyDiffer.getCorrectMatches());
    assertEquals(0, emptyDiffer.getMissing());
    assertEquals(0, emptyDiffer.getSpurious());

    Factory.deleteResource(key);
    Factory.deleteResource(response);
  }

  @Test
  public void measureDocsFindsExactPartialMissingAndSpuriousAnnotations() throws Exception {
    Gate.init();
    CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
    tool.setDiffFeaturesList(Collections.<String>emptySet());
    Document key = Factory.newDocument("John Smith met Alice in Paris");
    Document response = Factory.newDocument("John Smith met Alice in Paris");
    key.getAnnotations("Key").add(0L, 10L, "Person", Factory.newFeatureMap());
    key.getAnnotations("Key").add(15L, 20L, "Person", Factory.newFeatureMap());
    key.getAnnotations("Key").add(24L, 29L, "Person", Factory.newFeatureMap());
    response.getAnnotations().add(0L, 10L, "Person", Factory.newFeatureMap());
    response.getAnnotations().add(15L, 19L, "Person", Factory.newFeatureMap());
    response.getAnnotations().add(21L, 23L, "Person", Factory.newFeatureMap());

    AnnotationDiffer differ = tool.measureDocs(key, response, "Person");

    assertNotNull(differ);
    assertEquals(1, differ.getCorrectMatches());
    assertEquals(1, differ.getPartiallyCorrectMatches());
    assertEquals(1, differ.getMissing());
    assertEquals(1, differ.getSpurious());
    Factory.deleteResource(key);
    Factory.deleteResource(response);
  }

  @Test
  public void measureDocsHonorsConfiguredSignificantFeatures() throws Exception {
    Gate.init();
    CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
    tool.setDiffFeaturesList(Collections.singleton("class"));
    Document key = Factory.newDocument("Acme");
    Document response = Factory.newDocument("Acme");
    FeatureMap keyFeatures = Factory.newFeatureMap();
    keyFeatures.put("class", "company");
    FeatureMap responseFeatures = Factory.newFeatureMap();
    responseFeatures.put("class", "government");
    key.getAnnotations("Key").add(0L, 4L, "Organization", keyFeatures);
    response.getAnnotations().add(0L, 4L, "Organization", responseFeatures);

    AnnotationDiffer differ = tool.measureDocs(key, response, "Organization");

    assertEquals(0, differ.getCorrectMatches());
    assertEquals(1, differ.getMissing());
    assertEquals(1, differ.getSpurious());
    Factory.deleteResource(key);
    Factory.deleteResource(response);
  }

  @Test
  public void storeAnnotationsWritesTypeTextAndOffsets() throws Exception {
    Gate.init();
    CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
    Document document = Factory.newDocument("Alpha Beta");
    Integer id = document.getAnnotations().add(6L, 10L, "Entity", Factory.newFeatureMap());
    Annotation annotation = document.getAnnotations().get(id);
    Set<Annotation> annotations = new LinkedHashSet<Annotation>();
    annotations.add(annotation);
    StringWriter output = new StringWriter();

    tool.storeAnnotations("Entity.miss", annotations, document, output);

    assertEquals("Entity.miss.Beta.6.10\n", output.toString());
    Factory.deleteResource(document);
  }

  @Test
  public void storeAnnotationsDoesNothingForNullOrEmptySets() throws Exception {
    Gate.init();
    CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
    Document document = Factory.newDocument("Alpha");
    StringWriter output = new StringWriter();

    tool.storeAnnotations("Entity", null, document, output);
    tool.storeAnnotations("Entity", Collections.<Annotation>emptySet(), document, output);

    assertEquals("", output.toString());
    Factory.deleteResource(document);
  }

  @Test
  public void averagePrinterRoundsToRequestedDecimalPlaces() {
    CorpusBenchmarkTool tool = new CorpusBenchmarkTool();

    assertEquals("0.3333", tool.avgPrint(1.0 / 3.0, 4));
    assertEquals("12.35", tool.avgPrint(12.3451, 2));
    assertEquals("-1.24", tool.avgPrint(-1.235, 2));
    assertEquals("8.0", tool.avgPrint(7.6, 0));
  }

  @Test
  public void printStatisticsReportsConfiguredTypesWithZeroCounts() throws Exception {
    Path root = java.nio.file.Files.createTempDirectory("benchmark-statistics");
    java.nio.file.Files.write(root.resolve("corpus_tool.properties"),
        "annotTypes=TestEntity\n".getBytes(StandardCharsets.ISO_8859_1));
    CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
    tool.setStartDirectory(root.toFile());
    tool.setMarkedStored(true);
    tool.init();
    StringWriter output = new StringWriter();
    PrintWriter original = Out.getPrintWriter();

    try {
      Out.setPrintWriter(new PrintWriter(output, true));
      tool.printStatistics();
    } finally {
      Out.setPrintWriter(original);
    }

    assertTrue(output.toString().contains("<TD>TestEntity</TD>"));
    assertTrue(output.toString().contains("<TD>0</TD>"));
    assertTrue(output.toString().contains("<TD>0.0</TD>"));
  }

  @Test
  public void evaluateTwoDocsUpdatesAveragesAcrossMultipleAnnotationTypes() throws Exception {
    Gate.init();
    Path root = java.nio.file.Files.createTempDirectory("benchmark-two-types");
    java.nio.file.Files.write(root.resolve("corpus_tool.properties"),
        ("annotTypes=Person;Location\n" +
         "annotFeatures=class\n").getBytes(StandardCharsets.ISO_8859_1));
    CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
    tool.setStartDirectory(root.toFile());
    tool.setMarkedStored(true);
    tool.init();
    Document key = Factory.newDocument("John visits Paris");
    Document response = Factory.newDocument("John visits Paris");
    FeatureMap feature = Factory.newFeatureMap();
    feature.put("class", "named");
    key.getAnnotations("Key").add(0L, 4L, "Person", feature);
    key.getAnnotations("Key").add(12L, 17L, "Location", feature);
    response.getAnnotations().add(0L, 4L, "Person", feature);
    response.getAnnotations().add(12L, 17L, "Location", feature);

    tool.evaluateTwoDocs(key, response, null);

    assertEquals(1.0, tool.getPrecisionAverage(), 0.0);
    assertEquals(1.0, tool.getRecallAverage(), 0.0);
    assertEquals(1.0, tool.getFMeasureAverage(), 0.0);
    tool.calculateAvgTotal();
    assertEquals(1.0, tool.getPrecisionAverageCalc(), 0.0);
    assertEquals(1.0, tool.getRecallAverageCalc(), 0.0);
    assertEquals(1.0, tool.getFmeasureAverageCalc(), 0.0);
    Factory.deleteResource(key);
    Factory.deleteResource(response);
  }

  @Test
  public void evaluateTwoDocsUsesPerfectPrecisionAndZeroRecallForEmptyResponse() throws Exception {
    Gate.init();
    Path root = java.nio.file.Files.createTempDirectory("benchmark-missing-only");
    java.nio.file.Files.write(root.resolve("corpus_tool.properties"),
        "annotTypes=Person\n".getBytes(StandardCharsets.ISO_8859_1));
    CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
    tool.setStartDirectory(root.toFile());
    tool.setMarkedStored(true);
    tool.init();
    Document key = Factory.newDocument("John");
    Document response = Factory.newDocument("John");
    key.getAnnotations("Key").add(0L, 4L, "Person", Factory.newFeatureMap());

    tool.evaluateTwoDocs(key, response, null);

    assertEquals(1.0, tool.getPrecisionAverage(), 0.0);
    assertEquals(0.0, tool.getRecallAverage(), 0.0);
    assertEquals(0.0, tool.getFMeasureAverage(), 0.0);
    Factory.deleteResource(key);
    Factory.deleteResource(response);
  }

  @Test
  public void verboseMoreInfoEvaluationCreatesDetailedErrorFile() throws Exception {
    Gate.init();
    Path root = java.nio.file.Files.createTempDirectory("benchmark-error-file");
    java.nio.file.Files.write(root.resolve("corpus_tool.properties"),
        "annotTypes=Person\n".getBytes(StandardCharsets.ISO_8859_1));
    Path errors = java.nio.file.Files.createDirectory(root.resolve("errors"));
    CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
    tool.setStartDirectory(root.toFile());
    tool.setMarkedStored(true);
    tool.setMoreInfo(true);
    tool.setVerboseMode(true);
    tool.setThreshold(1.0);
    tool.init();
    Document key = Factory.newDocument("John and Alice");
    key.setName("people.txt");
    Document response = Factory.newDocument("John and Alice");
    response.setName("people.txt");
    key.getAnnotations("Key").add(0L, 4L, "Person", Factory.newFeatureMap());
    response.getAnnotations().add(9L, 14L, "Person", Factory.newFeatureMap());

    tool.evaluateTwoDocs(key, response, errors.toFile());

    String details = new String(java.nio.file.Files.readAllBytes(errors.resolve("people.err")),
        StandardCharsets.UTF_8);
    assertTrue(details.contains("Person.miss.John.0.4"));
    assertTrue(details.contains("Person.spur.Alice.9.14"));
    Factory.deleteResource(key);
    Factory.deleteResource(response);
  }
}
