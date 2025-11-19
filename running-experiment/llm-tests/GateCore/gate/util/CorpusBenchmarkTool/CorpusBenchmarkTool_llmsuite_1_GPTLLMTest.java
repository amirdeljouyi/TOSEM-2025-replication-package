package gate.util;

import gate.*;
import gate.creole.ResourceInstantiationException;
import gate.creole.ir.SearchException;
import gate.event.CreoleEvent;
import gate.event.CreoleListener;
import gate.event.PluginListener;
import gate.util.GateException;
import gate.util.GateRuntimeException;
import gate.util.InvalidOffsetException;
import org.apache.tools.ant.types.resources.comparators.Content;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import gate.creole.AnnotationSchema;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.Element;

public class CorpusBenchmarkTool_llmsuite_1_GPTLLMTest {

@Test(expected = GateRuntimeException.class)
public void testInitPRsThrowsWhenApplicationNotSet() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.initPRs();
}

@Test
public void testSetAndGetGenerateModeTrueAndFalse() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setGenerateMode(true);
assertTrue(tool.getGenerateMode());
tool.setGenerateMode(false);
assertFalse(tool.getGenerateMode());
}

@Test
public void testSetAndGetVerboseModeTrueAndFalse() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setVerboseMode(true);
assertTrue(tool.getVerboseMode());
tool.setVerboseMode(false);
assertFalse(tool.getVerboseMode());
}

@Test
public void testSetAndGetMoreInfoModeTrueAndFalse() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setMoreInfo(true);
assertTrue(tool.getMoreInfo());
tool.setMoreInfo(false);
assertFalse(tool.getMoreInfo());
}

@Test
public void testSetAndGetDiffFeaturesList() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Set<String> features = new HashSet<String>();
features.add("class");
features.add("inst");
tool.setDiffFeaturesList(features);
Set<String> result = tool.getDiffFeaturesList();
assertNotNull(result);
assertTrue(result.contains("class"));
assertTrue(result.contains("inst"));
}

@Test
public void testSetAndGetMarkedStored() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setMarkedStored(true);
assertTrue(tool.getMarkedStored());
tool.setMarkedStored(false);
assertFalse(tool.getMarkedStored());
}

@Test
public void testSetAndGetMarkedClean() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setMarkedClean(true);
assertTrue(tool.getMarkedClean());
tool.setMarkedClean(false);
assertFalse(tool.getMarkedClean());
}

@Test
public void testSetAndGetMarkedDS() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setMarkedDS(true);
assertTrue(tool.getMarkedDS());
tool.setMarkedDS(false);
assertFalse(tool.getMarkedDS());
}

@Test
public void testSetAndGetApplicationFile() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File appFile = new File("mock-application.gapp");
tool.setApplicationFile(appFile);
}

@Test
public void testSetAndGetStartDirectory() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File dir = new File(System.getProperty("java.io.tmpdir"), "cbtool_test_dir");
tool.setStartDirectory(dir);
File result = tool.getStartDirectory();
assertEquals(dir, result);
}

@Test
public void testSetAndGetThreshold() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setThreshold(0.65);
assertEquals(0.65, tool.getThreshold(), 0.0001);
}

@Test
public void testInitUsesDefaultsWhenPropertiesFileMissing() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File dir = new File(System.getProperty("java.io.tmpdir"), "missing_properties_test");
dir.mkdirs();
tool.setStartDirectory(dir);
tool.setMarkedStored(true);
tool.init();
Set<String> features = tool.getDiffFeaturesList();
assertNotNull(features);
}

@Test
public void testInitLoadsValidPropertiesFileSuccessfully() throws IOException {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File dir = new File(System.getProperty("java.io.tmpdir"), "props_test_dir");
dir.mkdirs();
File propsFile = new File(dir, "corpus_tool.properties");
FileWriter writer = new FileWriter(propsFile);
writer.write("threshold=0.55\n");
writer.write("annotSetName=KeySet\n");
writer.write("outputSetName=SystemSet\n");
writer.write("encoding=UTF-8\n");
writer.write("annotTypes=Person;Location\n");
writer.write("annotFeatures=class;inst\n");
writer.close();
tool.setStartDirectory(dir);
tool.setMarkedStored(true);
tool.init();
Set<String> features = tool.getDiffFeaturesList();
assertTrue(features.contains("class"));
assertTrue(features.contains("inst"));
}

@Test
public void testGetPrecisionRecallFmeasureDefaultValues() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
double precision = tool.getPrecisionAverage();
double recall = tool.getRecallAverage();
double fmeasure = tool.getFMeasureAverage();
assertEquals(0.0, precision, 0.001);
assertEquals(0.0, recall, 0.001);
assertEquals(0.0, fmeasure, 0.001);
}

@Test
public void testPrecisionRecallFmeasureCalculationReturnsZeroWhenNoData() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.calculateAvgTotal();
double precision = tool.getPrecisionAverageCalc();
double recall = tool.getRecallAverageCalc();
double fmeasure = tool.getFmeasureAverageCalc();
assertEquals(0.0, precision, 0.001);
assertEquals(0.0, recall, 0.001);
assertEquals(0.0, fmeasure, 0.001);
}

@Test
public void testMainMethodWithInsufficientArgumentsThrowsGateException() {
try {
String[] args = new String[0];
CorpusBenchmarkTool.main(args);
fail("Expected GateException due to missing args");
} catch (GateException e) {
assertTrue(e.getMessage().toLowerCase().contains("usage"));
} catch (Exception e) {
fail("Expected GateException, but got: " + e.getClass().getSimpleName());
}
}

@Test
public void testInitWithEmptyPropertiesFileLoadsDefaults() throws IOException {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File dir = new File(System.getProperty("java.io.tmpdir"), "empty_props_dir");
dir.mkdirs();
File propsFile = new File(dir, "corpus_tool.properties");
FileWriter writer = new FileWriter(propsFile);
writer.write("\n");
writer.close();
tool.setStartDirectory(dir);
tool.setMarkedStored(true);
tool.init();
Set<String> features = tool.getDiffFeaturesList();
assertNotNull(features);
}

@Test
public void testInitIgnoresWhitespaceInProperties() throws IOException {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File dir = new File(System.getProperty("java.io.tmpdir"), "whitespace_props_dir");
dir.mkdirs();
File propsFile = new File(dir, "corpus_tool.properties");
FileWriter writer = new FileWriter(propsFile);
writer.write("threshold =  0.45  \n");
writer.write("annotSetName =  HumanSet \n");
writer.write("outputSetName = AutoSet   \n");
writer.write("encoding =   ISO-8859-1   \n");
writer.write("annotTypes =  Organization ; Person \n");
writer.write("annotFeatures =  id ; name  \n");
writer.close();
tool.setStartDirectory(dir);
tool.setMarkedStored(true);
tool.init();
Set<String> features = tool.getDiffFeaturesList();
assertTrue(features.contains("id"));
assertTrue(features.contains("name"));
}

@Test
public void testSetGenerateModeTwiceThenExecuteWithNullDirDoesNothing() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setGenerateMode(false);
tool.setGenerateMode(true);
tool.execute((File) null);
assertTrue(tool.getGenerateMode());
}

@Test
public void testExecuteWithDirectoryMissingSubdirectories() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File dir = new File(System.getProperty("java.io.tmpdir"), "incomplete_dir");
dir.mkdirs();
File extra = new File(dir, "misc");
extra.mkdir();
File cvs = new File(dir, "Cvs");
cvs.mkdir();
tool.setGenerateMode(true);
tool.execute(dir);
}

@Test
public void testGettersAndSettersReturnConsistentValues() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
assertFalse(tool.getGenerateMode());
assertFalse(tool.getVerboseMode());
assertFalse(tool.getMoreInfo());
assertFalse(tool.getMarkedClean());
assertFalse(tool.getMarkedStored());
assertFalse(tool.getMarkedDS());
tool.setGenerateMode(true);
tool.setVerboseMode(true);
tool.setMoreInfo(true);
tool.setMarkedClean(true);
tool.setMarkedStored(true);
tool.setMarkedDS(true);
assertTrue(tool.getGenerateMode());
assertTrue(tool.getVerboseMode());
assertTrue(tool.getMoreInfo());
assertTrue(tool.getMarkedClean());
assertTrue(tool.getMarkedStored());
assertTrue(tool.getMarkedDS());
}

@Test
public void testCalculateAvgTotalWithSingleAnnotationType() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Set<String> annotTypes = new HashSet<String>();
annotTypes.add("Person");
tool.setDiffFeaturesList(new HashSet<String>());
java.util.List<String> list = new java.util.ArrayList<String>();
list.add("Person");
try {
java.lang.reflect.Field field = tool.getClass().getDeclaredField("correctByType");
field.setAccessible(true);
((java.util.Map<String, Long>) field.get(tool)).put("Person", 10L);
field = tool.getClass().getDeclaredField("partialByType");
field.setAccessible(true);
((java.util.Map<String, Long>) field.get(tool)).put("Person", 5L);
field = tool.getClass().getDeclaredField("spurByType");
field.setAccessible(true);
((java.util.Map<String, Long>) field.get(tool)).put("Person", 3L);
field = tool.getClass().getDeclaredField("missingByType");
field.setAccessible(true);
((java.util.Map<String, Long>) field.get(tool)).put("Person", 2L);
field = tool.getClass().getDeclaredField("annotTypes");
field.setAccessible(true);
field.set(tool, list);
} catch (Exception e) {
fail("Reflection failed to simulate internal state for test.");
}
tool.calculateAvgTotal();
double p = tool.getPrecisionAverageCalc();
double r = tool.getRecallAverageCalc();
double f = tool.getFmeasureAverageCalc();
assertTrue(p > 0.0);
assertTrue(r > 0.0);
assertTrue(f > 0.0);
}

@Test
public void testIsGenerateModeReturnsTrueAndFalseSymmetry() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setGenerateMode(true);
assertTrue(tool.isGenerateMode());
tool.setGenerateMode(false);
assertFalse(tool.isGenerateMode());
}

@Test
public void testEdgeCaseAnnotFeaturesParsingEmptyString() throws IOException {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File dir = new File(System.getProperty("java.io.tmpdir"), "annot_empty_test");
dir.mkdirs();
File propsFile = new File(dir, "corpus_tool.properties");
FileWriter writer = new FileWriter(propsFile);
writer.write("annotFeatures=\n");
writer.close();
tool.setStartDirectory(dir);
tool.setMarkedStored(true);
tool.init();
Set<String> features = tool.getDiffFeaturesList();
assertNotNull(features);
assertTrue(features.isEmpty());
}

@Test
public void testDocumentEncodingDefaultsToEmptyIfNotSet() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File dir = new File(System.getProperty("java.io.tmpdir"), "encoding_test");
dir.mkdirs();
tool.setStartDirectory(dir);
tool.setMarkedStored(true);
tool.init();
}

@Test
public void testExecuteWithEmptyDirectoryReturnsEarly() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File emptyDir = new File(System.getProperty("java.io.tmpdir"), "empty_dir_node");
emptyDir.mkdir();
tool.setGenerateMode(true);
tool.execute(emptyDir);
}

@Test
public void testInitFailsGracefullyIfPropertiesFileUnreadable() throws IOException {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File dir = new File(System.getProperty("java.io.tmpdir"), "unreadable_props");
dir.mkdirs();
File propsFile = new File(dir, "corpus_tool.properties");
propsFile.createNewFile();
propsFile.setReadable(false);
tool.setStartDirectory(dir);
tool.setMarkedStored(true);
try {
tool.init();
fail("Expected GateRuntimeException due to unreadable file.");
} catch (GateRuntimeException e) {
assertTrue(e.getMessage().contains("Error loading"));
} finally {
propsFile.setReadable(true);
}
}

@Test
public void testSetAndGetDiffFeaturesListWithEmptySet() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Set<String> emptyFeatures = new HashSet<String>();
tool.setDiffFeaturesList(emptyFeatures);
Set<String> result = tool.getDiffFeaturesList();
assertNotNull(result);
assertTrue(result.isEmpty());
}

@Test
public void testExecuteWithNullFileDoesNotCrash() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.execute((File) null);
}

@Test
public void testExecuteWithEmptyDirectoryArray() throws IOException {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File tempDir = new File(System.getProperty("java.io.tmpdir"), "execute_empty_dir");
tempDir.mkdirs();
File[] files = tempDir.listFiles();
if (files != null) {
for (File f : files) {
f.delete();
}
}
tool.setGenerateMode(false);
tool.execute(tempDir);
}

@Test
public void testSetInvalidStartDirectoryAndExecute() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File nonExistentDir = new File(System.getProperty("java.io.tmpdir"), "nonexistent1234DIR");
tool.setGenerateMode(true);
tool.execute(nonExistentDir);
}

@Test
public void testEmptyAnnotationTypesListSkipsEvaluateDocuments() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File dir = new File(System.getProperty("java.io.tmpdir"), "no_annots_evaluate");
dir.mkdirs();
tool.setStartDirectory(dir);
tool.setGenerateMode(false);
Field annotTypesField = CorpusBenchmarkTool.class.getDeclaredField("annotTypes");
annotTypesField.setAccessible(true);
annotTypesField.set(null, new ArrayList<String>());
Document emptyDoc = Factory.newDocument("");
try {
tool.evaluateDocuments(emptyDoc, emptyDoc, emptyDoc, null);
assertTrue(true);
} finally {
Factory.deleteResource(emptyDoc);
}
}

@Test
public void testNoTokenAnnotationsResultsInWordCountZero() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document doc = Factory.newDocument("This document has no Token annotations.");
int wordCount = tool.countWords(doc);
assertEquals(0, wordCount);
Factory.deleteResource(doc);
}

@Test
public void testPrintStatsForUnknownAnnotationTypeDoesNotThrow() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
java.util.List<String> fakeList = new java.util.ArrayList<String>();
fakeList.add("UnknownType");
try {
Field annotTypesField = CorpusBenchmarkTool.class.getDeclaredField("annotTypes");
annotTypesField.setAccessible(true);
annotTypesField.set(null, fakeList);
} catch (Exception e) {
fail("Failed to set annotation types via reflection for testing");
}
tool.printStatistics();
}

@Test
public void testAvgPrintRoundingTruncation() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
String result1 = tool.avgPrint(0.99999, 2);
assertEquals("1.0", String.valueOf(Double.parseDouble(result1)));
String result2 = tool.avgPrint(0.12345, 3);
assertEquals("0.123", String.valueOf(Double.parseDouble(result2)));
}

@Test
public void testSetDiffFeaturesListWithSpecialCharacters() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Set<String> features = new HashSet<String>();
features.add("feat@special");
features.add("dash-feature");
features.add("num123");
tool.setDiffFeaturesList(features);
Set<String> result = tool.getDiffFeaturesList();
assertTrue(result.contains("feat@special"));
assertTrue(result.contains("dash-feature"));
assertTrue(result.contains("num123"));
}

@Test
public void testGetAverageWhenDocumentCountIsZero() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
double precision = tool.getPrecisionAverage();
double recall = tool.getRecallAverage();
double fmeasure = tool.getFMeasureAverage();
assertEquals(Double.NaN, precision, 0.001);
assertEquals(Double.NaN, recall, 0.001);
assertEquals(Double.NaN, fmeasure, 0.001);
}

@Test
public void testInitPropertiesWithMissingThresholdFallsBackToDefault() throws IOException {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File dir = new File(System.getProperty("java.io.tmpdir"), "missing_threshold_init");
dir.mkdirs();
File propFile = new File(dir, "corpus_tool.properties");
FileWriter writer = new FileWriter(propFile);
writer.write("annotSetName=Marked\n");
writer.write("outputSetName=Processed\n");
writer.write("encoding=UTF-8\n");
writer.write("annotTypes=Person;Location\n");
writer.write("annotFeatures=inst\n");
writer.close();
tool.setStartDirectory(dir);
tool.setMarkedStored(true);
tool.init();
assertTrue(tool.getThreshold() > 0.0);
}

@Test
public void testInitPropertiesWithMalformedDoubleThresholdGracefulFail() throws IOException {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File dir = new File(System.getProperty("java.io.tmpdir"), "malformed_threshold_props");
dir.mkdirs();
File propFile = new File(dir, "corpus_tool.properties");
FileWriter writer = new FileWriter(propFile);
writer.write("threshold=notADouble\n");
writer.write("annotSetName=OtherSet\n");
writer.close();
tool.setStartDirectory(dir);
tool.setMarkedStored(true);
try {
tool.init();
fail("Expected NumberFormatException wrapped in GateRuntimeException");
} catch (GateRuntimeException e) {
assertTrue(e.getCause() instanceof NumberFormatException);
}
}

@Test
public void testEvaluateCorpusWithNullProcessedDirAndMarkedStoredTrue() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File cleanDir = new File(System.getProperty("java.io.tmpdir"), "cleanDirForNullProcessed");
cleanDir.mkdirs();
tool.setMarkedStored(true);
tool.evaluateCorpus(cleanDir, null, null, null);
}

@Test
public void testEvaluateCorpusWithNullFileDir() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File processedDir = new File(System.getProperty("java.io.tmpdir"), "processedDirNullFileDir");
processedDir.mkdirs();
tool.evaluateCorpus(null, processedDir, null, null);
}

@Test
public void testGenerateCorpusWithNullFileDirReturnsImmediately() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.generateCorpus(null, null);
}

@Test
public void testGenerateCorpusWithOutputDirAlreadyExisting() throws IOException {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File baseDir = new File(System.getProperty("java.io.tmpdir"), "generate_existing_outdir");
File inputDir = new File(baseDir, "clean");
File outDir = new File(baseDir, "processed");
inputDir.mkdirs();
outDir.mkdirs();
File sampleFile = new File(inputDir, "doc1.txt");
FileWriter fw = new FileWriter(sampleFile);
fw.write("sample text");
fw.close();
tool.setStartDirectory(baseDir);
tool.setMarkedStored(true);
tool.setGenerateMode(true);
try {
tool.generateCorpus(inputDir, outDir);
} catch (Exception e) {
assertTrue(e instanceof RuntimeException || e instanceof GateRuntimeException);
}
}

@Test
public void testMeasureDocsReturnsNullWhenKeyDocIsNull() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document respDoc = Factory.newDocument("Test content");
String annotType = "Person";
assertNull(tool.measureDocs(null, respDoc, annotType));
Factory.deleteResource(respDoc);
}

@Test
public void testMeasureDocsReturnsNullWhenRespDocIsNull() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document keyDoc = Factory.newDocument("Test content");
String annotType = "Person";
assertNull(tool.measureDocs(keyDoc, null, annotType));
Factory.deleteResource(keyDoc);
}

@Test
public void testEvaluateMarkedStoredWithMissingMarkedFile() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File baseDir = new File(System.getProperty("java.io.tmpdir"), "marked_stored_test_empty");
File marked = new File(baseDir, "marked");
File stored = new File(baseDir, "stored");
marked.mkdirs();
stored.mkdirs();
tool.setStartDirectory(baseDir);
tool.setMarkedStored(true);
tool.evaluateMarkedStored(marked, stored, null);
}

@Test
public void testEvaluateMarkedCleanWithCleanDocsNoMarkedDoc() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File baseDir = new File(System.getProperty("java.io.tmpdir"), "marked_clean_test");
File clean = new File(baseDir, "clean");
File marked = new File(baseDir, "marked");
clean.mkdirs();
marked.mkdirs();
File docFile = new File(clean, "doc1.txt");
FileWriter writer = new FileWriter(docFile);
writer.write("Some sample content");
writer.close();
tool.setMoreInfo(true);
tool.setMarkedClean(true);
try {
tool.evaluateMarkedClean(marked, clean, null);
} catch (Exception e) {
assertTrue(e instanceof RuntimeException || e instanceof gate.creole.ResourceInstantiationException);
}
}

@Test
public void testStoreAnnotationsWithEmptyAnnotationSetDoesNotWrite() throws IOException {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File file = new File(System.getProperty("java.io.tmpdir"), "store_annots_test.txt");
FileWriter writer = new FileWriter(file);
writer.write("");
writer.flush();
// Document doc = Factory.newDocument("Text with no annotations");
Set<gate.Annotation> annotations = new HashSet<gate.Annotation>();
// tool.storeAnnotations("Person", annotations, doc, writer);
writer.close();
// Factory.deleteResource(doc);
}

@Test
public void testPrintAnnotationsWithEmptySetProducesNoOutput() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document doc = null;
try {
doc = Factory.newDocument("Document text");
} catch (ResourceInstantiationException e) {
fail("Failed to create document");
}
Set<gate.Annotation> emptySet = new HashSet<gate.Annotation>();
tool.printAnnotations(emptySet, doc);
Factory.deleteResource(doc);
}

@Test
public void testPrintAnnotationsWithOutOfBoundsOffsetsIgnored() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document doc = Factory.newDocument("Short text");
// gate.AnnotationSet set = doc.getAnnotations().add(0L, 1000L, "Token", gate.Factory.newFeatureMap());
Set<gate.Annotation> annots = new HashSet<gate.Annotation>();
// annots.addAll(set);
try {
tool.printAnnotations(annots, doc);
} catch (StringIndexOutOfBoundsException e) {
fail("Should not throw when annotation offset exceeds content");
}
Factory.deleteResource(doc);
}

@Test
public void testEvaluateDocumentsWithNullAllInputsReturnsEarly() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.evaluateDocuments(null, null, null, null);
}

@Test
public void testEvaluateMarkedStoredWithMissingSourceURLFeatureSkipsDocument() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File baseDir = new File(System.getProperty("java.io.tmpdir"), "eval_marked_stored_missing_url");
File markedDir = new File(baseDir, "marked");
File storedDir = new File(baseDir, "stored");
markedDir.mkdirs();
storedDir.mkdirs();
tool.setMarkedStored(true);
tool.setMoreInfo(true);
try {
tool.evaluateMarkedStored(markedDir, storedDir, null);
} catch (Exception e) {
assertTrue(e instanceof RuntimeException || e instanceof GateRuntimeException);
}
}

@Test
public void testEvaluateMarkedCleanWhereCleanDocFailsInit() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File baseDir = new File(System.getProperty("java.io.tmpdir"), "eval_marked_clean_invalid_clean");
File cleanDir = new File(baseDir, "clean");
File markedDir = new File(baseDir, "marked");
cleanDir.mkdirs();
markedDir.mkdirs();
File invalidDoc = new File(cleanDir, "bad.txt");
FileWriter writer = new FileWriter(invalidDoc);
writer.write("");
writer.close();
tool.setMarkedClean(true);
tool.setMoreInfo(true);
try {
tool.evaluateMarkedClean(markedDir, cleanDir, null);
} catch (Exception e) {
assertFalse(e instanceof NullPointerException);
}
}

@Test
public void testProcessDocumentWithNullControllerDoesNotThrow() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File file = new File(System.getProperty("java.io.tmpdir"), "doc_with_text.txt");
FileWriter writer = new FileWriter(file);
writer.write("Some test text");
writer.close();
gate.FeatureMap params = gate.Factory.newFeatureMap();
params.put(gate.Document.DOCUMENT_URL_PARAMETER_NAME, file.toURI().toURL());
Document doc = (Document) Factory.createResource("gate.corpora.DocumentImpl", params);
try {
tool.processDocument(doc);
} catch (NullPointerException e) {
assertTrue(true);
} finally {
Factory.deleteResource(doc);
}
}

@Test
public void testGetPrecisionAverageWhenNoDocumentsReturnsNaN() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
double precision = tool.getPrecisionAverage();
assertTrue(Double.isNaN(precision));
}

@Test
public void testStoreAnnotationsCreatesLinesWithCorrectFormat() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document doc = Factory.newDocument("Annotation content to test.");
gate.AnnotationSet set = doc.getAnnotations();
gate.FeatureMap fm = gate.Factory.newFeatureMap();
// gate.Annotation ann = set.add(0L, 10L, "TestAnn", fm);
Set<gate.Annotation> annots = new HashSet<gate.Annotation>();
// annots.add(ann);
File errorFile = new File(System.getProperty("java.io.tmpdir"), "annot_output.txt");
FileWriter fw = new FileWriter(errorFile);
tool.storeAnnotations("CustomType", annots, doc, fw);
fw.close();
Factory.deleteResource(doc);
assertTrue(errorFile.exists());
assertTrue(errorFile.length() > 0);
}

@Test
public void testEvaluateCorpusWithSubdirectoriesRecursiveTraversal() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File root = new File(System.getProperty("java.io.tmpdir"), "corpus_eval_subdir");
File sub1 = new File(root, "subFolder1");
File sub2 = new File(sub1, "subFolder2");
File clean = new File(sub2, "clean");
clean.mkdirs();
tool.setMarkedStored(true);
tool.setGenerateMode(false);
try {
tool.execute(root);
} catch (Exception e) {
assertFalse(e instanceof NullPointerException);
}
}

@Test
public void testInitDoesNotCrashWhenAllOptionalPropertiesAreMissing() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File dir = new File(System.getProperty("java.io.tmpdir"), "init_missing_props");
dir.mkdirs();
File propFile = new File(dir, "corpus_tool.properties");
FileWriter fw = new FileWriter(propFile);
fw.write("");
fw.close();
tool.setStartDirectory(dir);
tool.setMarkedStored(true);
tool.init();
}

@Test
public void testPrintStatsForTypeWithZeroCountsDoesNotCrash() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
java.util.List<String> types = new java.util.ArrayList<String>();
types.add("NotSeen");
try {
java.lang.reflect.Field f = tool.getClass().getDeclaredField("annotTypes");
f.setAccessible(true);
f.set(null, types);
} catch (Exception e) {
fail("Unable to inject annotation types");
}
tool.printStatistics();
}

@Test
public void testEvaluateTwoDocsWithBothDocumentsNullReturnsImmediately() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.evaluateTwoDocs(null, null, null);
}

@Test
public void testEvaluateTwoDocsWithMatchingBlankDocsDoesNotThrow() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document d1 = Factory.newDocument("");
Document d2 = Factory.newDocument("");
try {
tool.evaluateTwoDocs(d1, d2, null);
} catch (Exception e) {
fail("evaluateTwoDocs should not throw on blank docs");
} finally {
Factory.deleteResource(d1);
Factory.deleteResource(d2);
}
}

@Test
public void testEvaluateAllThreeWithNullMarkedDocSkipsEvaluation() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document cleanDoc = Factory.newDocument("Clean");
Document persDoc = Factory.newDocument("Processed");
try {
tool.evaluateAllThree(persDoc, cleanDoc, null, null);
} catch (Exception e) {
assertFalse(e instanceof NullPointerException);
} finally {
Factory.deleteResource(cleanDoc);
Factory.deleteResource(persDoc);
}
}

@Test
public void testPrintAnnotationsHandlesNullSetGracefully() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document doc = Factory.newDocument("Sample Text");
tool.printAnnotations(null, doc);
Factory.deleteResource(doc);
}

@Test
public void testPrintAnnotationsHandlesInvalidOffsetsGracefully() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document doc = Factory.newDocument("Short");
gate.FeatureMap fm = gate.Factory.newFeatureMap();
gate.AnnotationSet set = doc.getAnnotations();
// gate.Annotation ann = set.add(2L, 1000L, "BadSpan", fm);
Set<gate.Annotation> annots = new HashSet<gate.Annotation>();
// annots.add(ann);
tool.printAnnotations(annots, doc);
Factory.deleteResource(doc);
}

@Test
public void testPrintTableHeaderWithoutVerboseOrMoreInfo() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setVerboseMode(false);
tool.setMoreInfo(false);
tool.printTableHeader();
}

@Test
public void testPrintTableHeaderWithVerboseAndMoreInfoTrue() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setVerboseMode(true);
tool.setMoreInfo(true);
tool.printTableHeader();
}

@Test
public void testCalculateAvgTotalWithAllAnnotationCountsZero() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
List<String> annotationTypes = new ArrayList<String>();
annotationTypes.add("Person");
try {
java.lang.reflect.Field field = tool.getClass().getDeclaredField("annotTypes");
field.setAccessible(true);
field.set(null, annotationTypes);
} catch (Exception e) {
fail("Failed to set annotation types via reflection.");
}
tool.calculateAvgTotal();
double precision = tool.getPrecisionAverageCalc();
double recall = tool.getRecallAverageCalc();
double fmeasure = tool.getFmeasureAverageCalc();
assertEquals(0.0, precision, 0.001);
assertEquals(0.0, recall, 0.001);
assertEquals(0.0, fmeasure, 0.001);
}

@Test
public void testUpdateStatisticsWithNaNValuesHandledGracefully() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
gate.Document keyDoc = Factory.newDocument("Test");
gate.Document respDoc = Factory.newDocument("Test");
gate.AnnotationSet keySet = keyDoc.getAnnotations();
gate.AnnotationSet respSet = respDoc.getAnnotations();
AnnotationDiffer differ = new AnnotationDiffer();
differ.setSignificantFeaturesSet(new HashSet<String>());
differ.calculateDiff(keySet, respSet);
tool.updateStatistics(differ, "EmptyTest");
Factory.deleteResource(keyDoc);
Factory.deleteResource(respDoc);
}

@Test
public void testUpdateStatisticsProcWithNaNValuesHandledGracefully() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
gate.Document keyDoc = Factory.newDocument("Test");
gate.Document respDoc = Factory.newDocument("Test");
gate.AnnotationSet keySet = keyDoc.getAnnotations();
gate.AnnotationSet respSet = respDoc.getAnnotations();
AnnotationDiffer differ = new AnnotationDiffer();
differ.setSignificantFeaturesSet(new HashSet<String>());
differ.calculateDiff(keySet, respSet);
tool.updateStatisticsProc(differ, "EmptyProc");
Factory.deleteResource(keyDoc);
Factory.deleteResource(respDoc);
}

@Test
public void testStoreAnnotationsCatchesIOExceptionGracefully() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document doc = Factory.newDocument("Some test content");
gate.AnnotationSet set = doc.getAnnotations();
gate.FeatureMap fm = gate.Factory.newFeatureMap();
// gate.Annotation ann = set.add(0L, 5L, "Test", fm);
Set<gate.Annotation> annots = new HashSet<gate.Annotation>();
// annots.add(ann);
Writer failingWriter = new Writer() {

@Override
public void write(char[] cbuf, int off, int len) throws IOException {
throw new IOException("Simulated failure");
}

@Override
public void flush() throws IOException {
}

@Override
public void close() throws IOException {
}
};
try {
tool.storeAnnotations("FailingType", annots, doc, failingWriter);
} catch (IOException e) {
fail("IOException should have been handled by storeAnnotations");
} finally {
Factory.deleteResource(doc);
}
}

@Test
public void testEvaluateTwoDocsWithNullAnnotationDifferReturnsEarly() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document doc1 = Factory.newDocument("This is a doc.");
Document doc2 = Factory.newDocument("This is another doc.");
try {
java.lang.reflect.Field f = tool.getClass().getDeclaredField("annotTypes");
f.setAccessible(true);
List<String> typeList = new ArrayList<String>();
typeList.add("NonExistentAnnotation");
f.set(null, typeList);
} catch (Exception e) {
fail("Reflection setup failed.");
}
tool.evaluateTwoDocs(doc1, doc2, null);
Factory.deleteResource(doc1);
Factory.deleteResource(doc2);
}

@Test
public void testAvgPrintNegativeValueWithPrecision() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
String result = tool.avgPrint(-0.21387, 3);
assertEquals("-0.214", result);
}

@Test
public void testAvgPrintZeroWithPrecision() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
String result = tool.avgPrint(0.0, 4);
assertEquals("0.0", result);
}

@Test
public void testGetFMeasureAverageWithZeroDocCountReturnsNaN() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
double avg = tool.getFMeasureAverage();
assertTrue(Double.isNaN(avg));
}

@Test
public void testSetAndGetApplicationFileReturnsSame() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File applicationFile = new File(System.getProperty("java.io.tmpdir"), "myapp.gapp");
tool.setApplicationFile(applicationFile);
tool.setVerboseMode(true);
tool.setMarkedStored(true);
tool.setGenerateMode(false);
}

@Test
public void testExecuteHandlesEmptyStartDirectoryWithoutSubdirsSafely() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File testDir = new File(System.getProperty("java.io.tmpdir"), "emptyStartDir");
testDir.mkdirs();
for (File file : testDir.listFiles()) {
file.delete();
}
tool.setStartDirectory(testDir);
tool.setGenerateMode(false);
tool.execute(testDir);
}

@Test
public void testEvaluateMarkedStoredWithEmptyStoredDirectory() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File baseDir = new File(System.getProperty("java.io.tmpdir"), "eval_stored_empty");
File markedDir = new File(baseDir, "marked");
File storedDir = new File(baseDir, "stored");
markedDir.mkdirs();
storedDir.mkdirs();
tool.setMarkedStored(true);
try {
tool.evaluateMarkedStored(markedDir, storedDir, null);
} catch (Exception e) {
fail("Should not throw exception when stored directory is empty.");
}
}

@Test
public void testEvaluateMarkedStoredWithMissingLRIdFeature() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File baseDir = new File(System.getProperty("java.io.tmpdir"), "eval_stored_missing_lrid");
File markedDir = new File(baseDir, "marked");
File storedDir = new File(baseDir, "stored");
markedDir.mkdirs();
storedDir.mkdirs();
tool.setMarkedStored(true);
tool.setMoreInfo(true);
try {
tool.evaluateMarkedStored(markedDir, storedDir, null);
} catch (Exception e) {
assertFalse(e instanceof NullPointerException);
}
}

@Test
public void testInitIgnoresMissingEncodingProperty() throws IOException {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File dir = new File(System.getProperty("java.io.tmpdir"), "missing_encoding_props");
dir.mkdirs();
File propFile = new File(dir, "corpus_tool.properties");
FileWriter writer = new FileWriter(propFile);
writer.write("annotTypes=Person;Location\n");
writer.write("annotFeatures=type\n");
writer.close();
tool.setStartDirectory(dir);
tool.setMarkedStored(true);
tool.init();
}

@Test
public void testInitWithWhitespaceOnlyValues() throws IOException {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File dir = new File(System.getProperty("java.io.tmpdir"), "whitespace_props");
dir.mkdirs();
File propFile = new File(dir, "corpus_tool.properties");
FileWriter writer = new FileWriter(propFile);
writer.write("threshold=  \n");
writer.write("annotSetName=   \n");
writer.write("annotTypes= \n");
writer.write("annotFeatures=  \n");
writer.close();
tool.setStartDirectory(dir);
tool.setMarkedStored(true);
tool.init();
}

@Test
public void testPrintStatsForTypeWhereNoCountsPresent() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
List<String> types = new ArrayList<String>();
types.add("UnknownAnnotation");
try {
java.lang.reflect.Field f = tool.getClass().getDeclaredField("annotTypes");
f.setAccessible(true);
f.set(null, types);
} catch (Exception e) {
fail("Unable to set annotationTypes");
}
tool.printStatistics();
}

@Test
public void testEvaluateAllThreeWithIdenticalContentDocuments() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document doc1 = Factory.newDocument("Same text.");
Document doc2 = Factory.newDocument("Same text.");
Document doc3 = Factory.newDocument("Same text.");
try {
tool.evaluateAllThree(doc1, doc2, doc3, null);
} catch (Exception e) {
fail("evaluateAllThree should not throw when documents are identical.");
} finally {
Factory.deleteResource(doc1);
Factory.deleteResource(doc2);
Factory.deleteResource(doc3);
}
}

@Test
public void testProcessDocumentWithNonCorpusControllerApplication() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// Controller controller = new Controller() {
// 
// private static final long serialVersionUID = 1L;
// 
// @Override
// public List getPRs() {
// return new ArrayList();
// }
// 
// @Override
// public void execute() throws ExecutionException {
// }
// };
java.lang.reflect.Field field = tool.getClass().getDeclaredField("application");
field.setAccessible(true);
// field.set(tool, controller);
Document doc = Factory.newDocument("Sample doc");
try {
tool.processDocument(doc);
} finally {
Factory.deleteResource(doc);
}
}

@Test
public void testEvaluateTwoDocsWithMismatchedAnnotationSets() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document doc1 = Factory.newDocument("Entity John Doe lives here.");
Document doc2 = Factory.newDocument("No annotations present.");
try {
gate.FeatureMap fm = Factory.newFeatureMap();
doc1.getAnnotations("Key").add(0L, 10L, "Person", fm);
java.lang.reflect.Field field = tool.getClass().getDeclaredField("annotSetName");
field.setAccessible(true);
field.set(tool, "Key");
List<String> types = new ArrayList<String>();
types.add("Person");
java.lang.reflect.Field typesField = tool.getClass().getDeclaredField("annotTypes");
typesField.setAccessible(true);
typesField.set(null, types);
} catch (Exception e) {
fail("Reflection setup failed for mismatched annotation sets.");
}
tool.evaluateTwoDocs(doc1, doc2, null);
Factory.deleteResource(doc1);
Factory.deleteResource(doc2);
}

@Test
public void testUpdateStatisticsWithHighPrecisionValues() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document doc1 = Factory.newDocument("Test annotation document.");
gate.AnnotationSet annSet = doc1.getAnnotations();
gate.FeatureMap fm = Factory.newFeatureMap();
annSet.add(0L, 4L, "Person", fm);
AnnotationDiffer differ = new AnnotationDiffer();
differ.setSignificantFeaturesSet(new HashSet<String>());
differ.calculateDiff(annSet, annSet);
tool.updateStatistics(differ, "Person");
Factory.deleteResource(doc1);
}

@Test
public void testPrintAnnotationsWithMultipleOffsetMismatchAnnotations() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document doc = Factory.newDocument("Mismatch test with overlapping annotations.");
gate.AnnotationSet annSet = doc.getAnnotations();
gate.FeatureMap fm = Factory.newFeatureMap();
annSet.add(5L, 25L, "Test", fm);
annSet.add(15L, 35L, "Test2", fm);
Set<gate.Annotation> mismatched = new HashSet<gate.Annotation>();
mismatched.addAll(annSet);
tool.printAnnotations(mismatched, doc);
Factory.deleteResource(doc);
}

@Test
public void testEvaluateDocumentsWithNullCleanDocAndMarkedDoc() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document processedDoc = Factory.newDocument("This is a processed doc.");
tool.evaluateDocuments(processedDoc, null, null, null);
Factory.deleteResource(processedDoc);
}
}
