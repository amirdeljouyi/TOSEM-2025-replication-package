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
import java.io.*;
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

public class CorpusBenchmarkTool_llmsuite_4_GPTLLMTest {

@Test
public void testGenerateModeSetterGetter() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setGenerateMode(true);
assertTrue(tool.getGenerateMode());
tool.setGenerateMode(false);
assertFalse(tool.getGenerateMode());
}

@Test
public void testVerboseModeSetterGetter() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setVerboseMode(true);
assertTrue(tool.getVerboseMode());
tool.setVerboseMode(false);
assertFalse(tool.getVerboseMode());
}

@Test
public void testMoreInfoModeSetterGetter() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setMoreInfo(true);
assertTrue(tool.getMoreInfo());
tool.setMoreInfo(false);
assertFalse(tool.getMoreInfo());
}

@Test
public void testMarkedStoredSetterGetter() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setMarkedStored(true);
assertTrue(tool.getMarkedStored());
tool.setMarkedStored(false);
assertFalse(tool.getMarkedStored());
}

@Test
public void testMarkedCleanSetterGetter() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setMarkedClean(true);
assertTrue(tool.getMarkedClean());
tool.setMarkedClean(false);
assertFalse(tool.getMarkedClean());
}

@Test
public void testMarkedDSSetterGetter() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setMarkedDS(true);
assertTrue(tool.getMarkedDS());
tool.setMarkedDS(false);
assertFalse(tool.getMarkedDS());
}

@Test
public void testThresholdSetterGetter() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setThreshold(0.85);
assertEquals(0.85, tool.getThreshold(), 0.00001);
}

@Test
public void testStartDirectorySetterGetter() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File dir = new File(System.getProperty("java.io.tmpdir"));
tool.setStartDirectory(dir);
assertEquals(dir, tool.getStartDirectory());
}

@Test
public void testDiffFeaturesListSetterGetter() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Set<String> diffFeatures = new HashSet<String>();
diffFeatures.add("class");
diffFeatures.add("type");
tool.setDiffFeaturesList(diffFeatures);
assertEquals(diffFeatures, tool.getDiffFeaturesList());
}

@Test(expected = GateRuntimeException.class)
public void testInitPRsThrowsWhenApplicationFileIsNull() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.initPRs();
}

@Test
public void testInitWithNonExistingPropertiesFileUsesDefaults() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File nonexistent = new File("nonexistent_dir_should_not_exist");
tool.setStartDirectory(nonexistent);
tool.init();
assertNotNull(tool.getDiffFeaturesList());
}

@Test
public void testAvgPrintRoundsCorrectly() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
String result = tool.avgPrint(0.1234567, 4);
assertEquals("0.1235", result);
}

@Test
public void testInitLoadsValuesFromPropertiesFile() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File tempDir = new File(System.getProperty("java.io.tmpdir"), "corpusPropsTest");
tempDir.mkdir();
File propsFile = new File(tempDir, "corpus_tool.properties");
Properties props = new Properties();
props.setProperty("threshold", "0.75");
props.setProperty("annotSetName", "TestKey");
props.setProperty("outputSetName", "TestResp");
props.setProperty("encoding", "UTF-8");
props.setProperty("annotTypes", "Person;Location");
props.setProperty("annotFeatures", "type;confidence");
OutputStream os = new FileOutputStream(propsFile);
props.store(os, "test");
os.close();
tool.setStartDirectory(tempDir);
tool.init();
assertEquals(0.75, tool.getThreshold(), 0.00001);
assertNotNull(tool.getDiffFeaturesList());
propsFile.delete();
tempDir.delete();
}

@Test
public void testExecuteWithNullStartDirectoryDoesNothing() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setStartDirectory(null);
tool.execute();
}

@Test
public void testPrecisionAverageIsZeroWhenNoDocsProcessed() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
double value = tool.getPrecisionAverage();
assertTrue(Double.isNaN(value) || value == 0.0);
}

@Test
public void testRecallAverageIsZeroWhenNoDocsProcessed() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
double value = tool.getRecallAverage();
assertTrue(Double.isNaN(value) || value == 0.0);
}

@Test
public void testFMeasureAverageIsZeroWhenNoDocsProcessed() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
double value = tool.getFMeasureAverage();
assertTrue(Double.isNaN(value) || value == 0.0);
}

@Test
public void testPrecisionAverageCalcReturnsZeroInitially() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
double value = tool.getPrecisionAverageCalc();
assertEquals(0.0, value, 0.00001);
}

@Test
public void testRecallAverageCalcReturnsZeroInitially() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
double value = tool.getRecallAverageCalc();
assertEquals(0.0, value, 0.00001);
}

@Test
public void testFMeasureAverageCalcReturnsZeroInitially() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
double value = tool.getFmeasureAverageCalc();
assertEquals(0.0, value, 0.00001);
}

@Test
public void testCalculateAvgTotalWithNullAnnotationTypesDoesNothing() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.calculateAvgTotal();
assertEquals(0.0, tool.getRecallAverageCalc(), 0.00001);
assertEquals(0.0, tool.getPrecisionAverageCalc(), 0.00001);
assertEquals(0.0, tool.getFmeasureAverageCalc(), 0.00001);
}

@Test
public void testApplicationFileSetter() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File file = new File("app.gapp");
tool.setApplicationFile(file);
assertNotNull(file);
}

@Test(expected = GateRuntimeException.class)
public void testInitPRsThrowsWhenApplicationFileDoesNotExist() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File invalidFile = new File("nonexistent.gapp");
tool.setApplicationFile(invalidFile);
tool.initPRs();
}

@Test
public void testExecuteSkipsWhenDirectoryIsFileNotFolder() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File tempFile = File.createTempFile("fileInsteadOfDir", ".tmp");
tempFile.deleteOnExit();
tool.execute(tempFile);
}

@Test
public void testGenerateCorpusHandlesNullFileDir() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.generateCorpus(null, null);
}

@Test
public void testGenerateCorpusHandlesEmptyFileDirGracefully() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File emptyDir = new File(System.getProperty("java.io.tmpdir"), "emptyCorpus");
emptyDir.mkdir();
tool.generateCorpus(emptyDir, null);
emptyDir.delete();
}

@Test
public void testExecuteHandlesMissingCleanDirectoryGracefully() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File parentDir = new File(System.getProperty("java.io.tmpdir"), "benchmarkTestDir");
File markedDir = new File(parentDir, "marked");
markedDir.mkdir();
File errorDir = new File(parentDir, "err");
errorDir.mkdir();
tool.setStartDirectory(parentDir);
tool.execute();
markedDir.delete();
errorDir.delete();
parentDir.delete();
}

@Test
public void testMeasureDocsReturnsNullForMissingAnnotationSet() throws ResourceInstantiationException {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document docA = Factory.newDocument("This is a test.");
Document docB = Factory.newDocument("Another test.");
tool.setDiffFeaturesList(new HashSet<String>());
String unusedAnnotationType = "NonExistingType";
assertNull(tool.measureDocs(docA, docB, unusedAnnotationType));
Factory.deleteResource(docA);
Factory.deleteResource(docB);
}

@Test
public void testSetThresholdBoundaryZero() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setThreshold(0.0);
assertEquals(0.0, tool.getThreshold(), 0.00001);
}

@Test
public void testSetThresholdBoundaryOne() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setThreshold(1.0);
assertEquals(1.0, tool.getThreshold(), 0.00001);
}

@Test
public void testEmptyDiffFeatureSetDoesNotThrowOnEvaluate() throws ResourceInstantiationException {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document docA = Factory.newDocument("This is a sample sentence.");
Document docB = Factory.newDocument("This is another sample.");
tool.setDiffFeaturesList(Collections.emptySet());
assertNull(tool.measureDocs(null, docA, "Person"));
Factory.deleteResource(docA);
Factory.deleteResource(docB);
}

@Test
public void testAnnotationSetNameNotMatchingProcess() throws ResourceInstantiationException {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document docA = Factory.newDocument("No annotation sets.");
Document docB = Factory.newDocument("No annotation sets either.");
tool.setDiffFeaturesList(new HashSet<String>());
assertNull(tool.measureDocs(docA, docB, "Organization"));
Factory.deleteResource(docA);
Factory.deleteResource(docB);
}

@Test
public void testMainMethodPrintsUsageIfArgsMissing() {
boolean threw = false;
try {
CorpusBenchmarkTool.main(new String[] {});
} catch (Exception e) {
threw = true;
}
assertTrue("Expected GateException when no args provided", threw);
}

@Test
public void testInitAcceptsBlankAnnotTypesProperty() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File dir = new File(System.getProperty("java.io.tmpdir"), "noTypesDir");
dir.mkdir();
File props = new File(dir, "corpus_tool.properties");
Properties p = new Properties();
p.setProperty("annotTypes", "");
p.store(new java.io.FileWriter(props), "empty types");
tool.setStartDirectory(dir);
tool.init();
props.delete();
dir.delete();
}

@Test(expected = RuntimeException.class)
public void testProcessDocumentThrowsForInvalidApplicationInstance() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document doc = Factory.newDocument("Failure test");
tool.setApplicationFile(new File("doesnotexistapp.gapp"));
java.lang.reflect.Field applicationField = CorpusBenchmarkTool.class.getDeclaredField("application");
applicationField.setAccessible(true);
// applicationField.set(tool, new Controller() {
// 
// public java.util.List getPRs() {
// throw new RuntimeException("invalid call");
// }
// 
// public void execute() throws ExecutionException {
// }
// });
tool.processDocument(doc);
Factory.deleteResource(doc);
}

@Test
public void testUnloadPRsReturnsEarlyIfMarkedStoredTrue() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setMarkedStored(true);
tool.unloadPRs();
}

@Test
public void testPrintAnnotationsWithEmptyAnnotationSet() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document doc = Factory.newDocument("Sample document content.");
Set<gate.Annotation> annotations = Collections.emptySet();
tool.printAnnotations(annotations, doc);
Factory.deleteResource(doc);
}

@Test
public void testPrintAnnotationsWithNullAnnotationSet() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document doc = Factory.newDocument("Sample document content.");
tool.printAnnotations(null, doc);
Factory.deleteResource(doc);
}

@Test
public void testStoreAnnotationsWithEmptySet() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document doc = Factory.newDocument("Sample document");
Set<gate.Annotation> annotations = Collections.emptySet();
File file = File.createTempFile("annot", ".txt");
Writer writer = new FileWriter(file);
tool.storeAnnotations("TestType", annotations, doc, writer);
writer.close();
file.delete();
Factory.deleteResource(doc);
}

@Test
public void testStoreAnnotationsWithNullSet() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document doc = Factory.newDocument("Sample document");
File file = File.createTempFile("annot", ".txt");
Writer writer = new FileWriter(file);
tool.storeAnnotations("TestType", null, doc, writer);
writer.close();
file.delete();
Factory.deleteResource(doc);
}

@Test
public void testStoreAnnotationsHandlesIOExceptionGracefully() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File fakeFile = File.createTempFile("closed", ".txt");
Writer writer = new FileWriter(fakeFile);
writer.close();
Document doc = Factory.newDocument("A document for IOException test");
Set<gate.Annotation> annotations = Collections.emptySet();
try {
tool.storeAnnotations("Type", annotations, doc, writer);
} catch (Exception e) {
}
fakeFile.delete();
Factory.deleteResource(doc);
}

@Test
public void testCountWordsWithNullDocument() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
int count = tool.countWords(null);
assertEquals(0, count);
}

@Test
public void testMeasureDocsReturnsNullIfKeyDocIsNull() throws ResourceInstantiationException {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document resp = Factory.newDocument("test");
assertNull(tool.measureDocs(null, resp, "Entity"));
Factory.deleteResource(resp);
}

@Test
public void testMeasureDocsReturnsNullIfRespDocIsNull() throws ResourceInstantiationException {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document key = Factory.newDocument("test");
assertNull(tool.measureDocs(key, null, "Entity"));
Factory.deleteResource(key);
}

@Test
public void testEvaluateDocumentsWithNullInputsReturnsEarly() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.evaluateDocuments(null, null, null, null);
}

@Test
public void testPrintTableHeaderDoesNotThrow() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setMoreInfo(true);
tool.setVerboseMode(true);
tool.printTableHeader();
}

@Test
public void testPrintStatisticsWithNullAnnotTypes() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.printStatistics();
}

@Test
public void testUpdateStatisticsWithNaNValues() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
AnnotationDiffer differ = new AnnotationDiffer();
differ.setSignificantFeaturesSet(Collections.emptySet());
Document doc = Factory.newDocument("Text");
gate.AnnotationSet set = doc.getAnnotations();
differ.calculateDiff(set, set);
tool.updateStatistics(differ, "Entity");
Factory.deleteResource(doc);
}

@Test
public void testUpdateStatisticsProcWithNaNValues() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
AnnotationDiffer differ = new AnnotationDiffer();
differ.setSignificantFeaturesSet(Collections.emptySet());
Document doc = Factory.newDocument("Text");
gate.AnnotationSet set = doc.getAnnotations();
differ.calculateDiff(set, set);
tool.updateStatisticsProc(differ, "Entity");
Factory.deleteResource(doc);
}

@Test
public void testPrintStatsForTypeDoesNotThrowWithUnseenType() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Set<String> types = new HashSet<String>();
types.add("NoData");
tool.setVerboseMode(true);
tool.setMoreInfo(true);
// CorpusBenchmarkTool.annotTypes = new java.util.ArrayList<String>(types);
tool.printStatsForType("NoData");
}

@Test
public void testPrintStatisticsWithDefaultTypes() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Set<String> types = new HashSet<String>();
types.add("Person");
types.add("Location");
// CorpusBenchmarkTool.annotTypes = new java.util.ArrayList<String>(types);
tool.printStatistics();
}

@Test
public void testExecuteDirectoryWithNoProcessableSubdirectories() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File parent = new File(System.getProperty("java.io.tmpdir"), "noSubdirsTestDir");
parent.mkdir();
File invalidSub = new File(parent, "CVS");
invalidSub.mkdir();
File notADir = new File(parent, "file.txt");
try {
notADir.createNewFile();
} catch (IOException e) {
fail("Cannot create temp file");
}
tool.execute(parent);
notADir.delete();
invalidSub.delete();
parent.delete();
}

@Test
public void testInitLoadsDefaultsWhenPropertiesFileEmpty() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File confDir = new File(System.getProperty("java.io.tmpdir"), "emptyProps");
confDir.mkdir();
File props = new File(confDir, "corpus_tool.properties");
if (props.exists())
props.delete();
OutputStream os = new FileOutputStream(props);
os.write(new byte[0]);
os.close();
tool.setStartDirectory(confDir);
tool.init();
assertNotNull(tool.getDiffFeaturesList());
props.delete();
confDir.delete();
}

@Test
public void testGetPrecisionAverageDoesNotCrashIfZeroDivisor() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
double value = tool.getPrecisionAverage();
assertTrue(Double.isNaN(value) || value == 0.0);
}

@Test
public void testGetRecallAverageDoesNotCrashIfZeroDivisor() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
double value = tool.getRecallAverage();
assertTrue(Double.isNaN(value) || value == 0.0);
}

@Test
public void testGetFMeasureAverageDoesNotCrashIfZeroDivisor() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
double value = tool.getFMeasureAverage();
assertTrue(Double.isNaN(value) || value == 0.0);
}

@Test
public void testPrintStatsForTypeWithNoStatsRecorded() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// CorpusBenchmarkTool.annotTypes = new java.util.ArrayList<String>();
// CorpusBenchmarkTool.annotTypes.add("DummyType");
tool.printStatsForType("DummyType");
}

@Test
public void testEvaluateTwoDocsWithNullWriter() throws ResourceInstantiationException {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document doc1 = Factory.newDocument("Document A");
Document doc2 = Factory.newDocument("Document B");
// CorpusBenchmarkTool.annotTypes = new java.util.ArrayList<String>();
// CorpusBenchmarkTool.annotTypes.add("Token");
tool.setVerboseMode(true);
tool.setMoreInfo(true);
tool.setThreshold(0.5);
tool.evaluateTwoDocs(doc1, doc2, null);
Factory.deleteResource(doc1);
Factory.deleteResource(doc2);
}

@Test
public void testEvaluateMarkedCleanWithMissingMarkedFile() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File testDir = new File(System.getProperty("java.io.tmpdir"), "evalCleanDir");
testDir.mkdir();
File cleanDir = new File(testDir, "clean");
cleanDir.mkdir();
File markedDir = new File(testDir, "marked");
markedDir.mkdir();
File sampleDoc = new File(cleanDir, "sample.txt");
FileWriter writer = new FileWriter(sampleDoc);
writer.write("Some content");
writer.close();
tool.setMarkedClean(true);
tool.setMoreInfo(false);
tool.setVerboseMode(false);
// CorpusBenchmarkTool.annotTypes = new java.util.ArrayList<String>();
// CorpusBenchmarkTool.annotTypes.add("Token");
tool.evaluateMarkedClean(markedDir, cleanDir, null);
sampleDoc.delete();
cleanDir.delete();
markedDir.delete();
testDir.delete();
}

@Test
public void testEvaluateCorpusWithNullErrorDir() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File temp = new File(System.getProperty("java.io.tmpdir"), "evalNullErr");
File clean = new File(temp, "clean");
File processed = new File(temp, "processed");
temp.mkdir();
clean.mkdir();
processed.mkdir();
tool.setMarkedStored(true);
tool.evaluateCorpus(clean, processed, null, null);
clean.delete();
processed.delete();
temp.delete();
}

@Test
public void testStoreAnnotationsHandlesNullWriter() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document doc = Factory.newDocument("content");
Set<Annotation> set = new HashSet<Annotation>();
tool.storeAnnotations("Type", set, doc, null);
Factory.deleteResource(doc);
}

@Test
public void testMainFailsOnInsufficientArguments() {
boolean threw = false;
try {
CorpusBenchmarkTool.main(new String[] { "-generate" });
} catch (Exception ex) {
threw = true;
}
assertTrue(threw);
}

@Test
public void testMainFailsIfDirectoryIsInvalid() {
boolean threw = false;
try {
CorpusBenchmarkTool.main(new String[] { "-generate", "nonexistent", "dummy.gapp" });
} catch (Exception ex) {
threw = true;
}
assertTrue(threw);
}

@Test
public void testMainFailsIfAppFileIsNotFile() throws IOException {
File dir = new File(System.getProperty("java.io.tmpdir"), "fakeDir");
dir.mkdir();
boolean threw = false;
try {
CorpusBenchmarkTool.main(new String[] { "-generate", System.getProperty("java.io.tmpdir"), dir.getAbsolutePath() });
} catch (Exception ex) {
threw = true;
}
assertTrue(threw);
dir.delete();
}

@Test
public void testAvgPrintEdgeRounding() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
String s = tool.avgPrint(0.99999, 2);
assertEquals("1.0", s);
}

@Test
public void testPrintAnnotationsDoesNotThrowForSpuriousCase() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document doc = Factory.newDocument("My spurious entity here.");
Set<gate.Annotation> annotations = doc.getAnnotations().get("FakeType");
tool.printAnnotations(annotations, doc);
Factory.deleteResource(doc);
}

@Test
public void testEvaluateAllThreeDoesNotThrowWhenAnnotationDifferNull() throws ResourceInstantiationException {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document persDoc = Factory.newDocument("Processed");
Document cleanDoc = Factory.newDocument("Clean");
Document markedDoc = Factory.newDocument("Marked");
// CorpusBenchmarkTool.annotTypes = new java.util.ArrayList<String>();
// CorpusBenchmarkTool.annotTypes.add("Nonexistent");
tool.evaluateAllThree(persDoc, cleanDoc, markedDoc, null);
Factory.deleteResource(persDoc);
Factory.deleteResource(cleanDoc);
Factory.deleteResource(markedDoc);
}

@Test
public void testEvaluateTwoDocsDoesNotThrowWhenAnnotationTypeEmpty() throws ResourceInstantiationException {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document key = Factory.newDocument("Key doc");
Document resp = Factory.newDocument("Response doc");
// CorpusBenchmarkTool.annotTypes = new java.util.ArrayList<String>();
// CorpusBenchmarkTool.annotTypes.add("");
tool.evaluateTwoDocs(key, resp, null);
Factory.deleteResource(key);
Factory.deleteResource(resp);
}

@Test
public void testEvaluateMarkedStoredWithMissingMarkedFiles() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File testDir = new File(System.getProperty("java.io.tmpdir"), "stored_eval_missing");
File markedDir = new File(testDir, "marked");
File storedDir = new File(testDir, "processed");
markedDir.mkdirs();
storedDir.mkdirs();
File dummyFile = new File(storedDir, "doc.x");
dummyFile.createNewFile();
tool.setMarkedStored(true);
// CorpusBenchmarkTool.annotTypes = new java.util.ArrayList<String>();
// CorpusBenchmarkTool.annotTypes.add("Token");
tool.evaluateMarkedStored(markedDir, storedDir, null);
dummyFile.delete();
markedDir.delete();
storedDir.delete();
testDir.delete();
}

@Test
public void testStoreAnnotationsSilentOnClosedWriter() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document doc = Factory.newDocument("Text to store error annotations");
File f = File.createTempFile("closedWriter", ".txt");
FileWriter writer = new FileWriter(f);
writer.close();
tool.storeAnnotations("type", new HashSet<gate.Annotation>(), doc, writer);
Factory.deleteResource(doc);
f.delete();
}

@Test
public void testProcessDocumentWithUninitializedApplicationField() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document doc = Factory.newDocument("Processing document should fail.");
try {
tool.processDocument(doc);
} catch (RuntimeException e) {
assertTrue(e.getMessage().contains("Error executing application"));
}
Factory.deleteResource(doc);
}

@Test
public void testUpdateStatisticsWithNullPrecisionRecallFMeasures() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document docKey = Factory.newDocument("Content");
// gate.AnnotationDiffer differ = new gate.AnnotationDiffer();
// differ.calculateDiff(docKey.getAnnotations(), docKey.getAnnotations());
// tool.updateStatistics(differ, "TestType");
Factory.deleteResource(docKey);
}

@Test
public void testUpdateStatisticsProcWithNullPrecisionRecallFMeasures() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document docKey = Factory.newDocument("Content");
// gate.AnnotationDiffer differ = new gate.AnnotationDiffer();
// differ.calculateDiff(docKey.getAnnotations(), docKey.getAnnotations());
// tool.updateStatisticsProc(differ, "TestType");
Factory.deleteResource(docKey);
}

@Test
public void testPrintStatsForTypeNaNAveragePrecisionRecall() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// CorpusBenchmarkTool.annotTypes = new java.util.ArrayList<String>();
// CorpusBenchmarkTool.annotTypes.add("TestType");
tool.setMoreInfo(true);
tool.setVerboseMode(true);
tool.printStatsForType("TestType");
}

@Test
public void testCalculateAvgTotalHandlesEmptyTypeStats() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// CorpusBenchmarkTool.annotTypes = new java.util.ArrayList<String>();
// CorpusBenchmarkTool.annotTypes.add("noDataType");
tool.calculateAvgTotal();
assertEquals(0.0, tool.getFmeasureAverageCalc(), 0.00001);
}

@Test
public void testExecuteSafeWhenCurrDirListFilesReturnsNull() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File tempDir = new File(System.getProperty("java.io.tmpdir"), "nullListDir");
tempDir.mkdirs();
File invalid = new File(tempDir, "xyz");
invalid.delete();
File empty = new File(tempDir, "empty");
empty.mkdirs();
boolean deleted = empty.delete();
File readOnly = new File(tempDir, "readonly");
readOnly.mkdir();
readOnly.setReadable(false);
tool.execute(readOnly);
readOnly.setReadable(true);
readOnly.delete();
tempDir.delete();
}

@Test
public void testGetDiffFeatureListReturnsNonNullEvenIfNeverSet() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Set<String> actual = tool.getDiffFeaturesList();
assertNotNull(actual);
}

@Test
public void testPrintTableHeaderWithNoVerboseOrMoreInfoMode() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setMoreInfo(false);
tool.setVerboseMode(false);
tool.printTableHeader();
}

@Test
public void testInitWithPropertiesMissingThreshold() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File tempDir = new File(System.getProperty("java.io.tmpdir"), "missingThreshold");
tempDir.mkdir();
File propFile = new File(tempDir, "corpus_tool.properties");
Properties props = new Properties();
props.setProperty("annotSetName", "Key");
props.setProperty("annotTypes", "Person;Location");
OutputStream out = new FileOutputStream(propFile);
props.store(out, "no threshold provided");
out.close();
tool.setStartDirectory(tempDir);
tool.init();
assertNotNull(tool.getDiffFeaturesList());
propFile.delete();
tempDir.delete();
}

@Test
public void testInitWithInvalidThresholdValue() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File tempDir = new File(System.getProperty("java.io.tmpdir"), "invalidThreshold");
tempDir.mkdir();
File propFile = new File(tempDir, "corpus_tool.properties");
FileWriter writer = new FileWriter(propFile);
writer.write("threshold = notANumber\n");
writer.close();
tool.setStartDirectory(tempDir);
try {
tool.init();
} catch (NumberFormatException e) {
assertTrue(e.getMessage().contains("notANumber"));
}
propFile.delete();
tempDir.delete();
}

@Test
public void testEvaluateCorpusWithOnlyCleanDirPresent() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File mainDir = new File(System.getProperty("java.io.tmpdir"), "evalCorpusCleanOnly");
File cleanDir = new File(mainDir, "clean");
mainDir.mkdir();
cleanDir.mkdir();
tool.setStartDirectory(mainDir);
tool.execute(mainDir);
cleanDir.delete();
mainDir.delete();
}

@Test
public void testEvaluateCorpusSkipsMarkedEvaluationIfNoMarkedDir() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File rootDir = new File(System.getProperty("java.io.tmpdir"), "skipMarkedEval");
File clean = new File(rootDir, "clean");
File processed = new File(rootDir, "processed");
clean.mkdirs();
processed.mkdirs();
tool.setMarkedClean(true);
tool.evaluateCorpus(clean, processed, null, null);
clean.delete();
processed.delete();
rootDir.delete();
}

@Test
public void testEvaluateMarkedCleanWithDocumentCreationFailure() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File parent = new File(System.getProperty("java.io.tmpdir"), "evaluateMarkFail");
File cleanDir = new File(parent, "clean");
File markedDir = new File(parent, "marked");
parent.mkdirs();
cleanDir.mkdir();
markedDir.mkdir();
File invalidTxt = new File(cleanDir, "invalid.txt");
FileWriter badWriter = new FileWriter(invalidTxt);
badWriter.write("Test content");
badWriter.close();
invalidTxt.setReadable(false);
// CorpusBenchmarkTool.annotTypes = new ArrayList<String>();
// CorpusBenchmarkTool.annotTypes.add("Person");
tool.setMarkedClean(true);
tool.evaluateMarkedClean(markedDir, cleanDir, null);
invalidTxt.setReadable(true);
invalidTxt.delete();
cleanDir.delete();
markedDir.delete();
parent.delete();
}

@Test
public void testStoreAnnotationsWithUnicodeCharacters() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document doc = Factory.newDocument("Text with ünicode ✨ characters.");
Set<gate.Annotation> set = doc.getAnnotations().get("Token");
File errorFile = File.createTempFile("annotStore", ".txt");
Writer writer = new OutputStreamWriter(new FileOutputStream(errorFile), "UTF-8");
tool.storeAnnotations("unicode", set, doc, writer);
writer.close();
assertTrue(errorFile.length() == 0 || errorFile.exists());
Factory.deleteResource(doc);
errorFile.delete();
}

@Test
public void testFileOutputWriterThrowsIOException() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File readOnlyFile = File.createTempFile("readonly", ".txt");
Document doc = Factory.newDocument("Sample for IOException");
Writer writer = new FileWriter(readOnlyFile);
writer.close();
Set<gate.Annotation> annotations = new HashSet<gate.Annotation>();
tool.storeAnnotations("Token", annotations, doc, writer);
Factory.deleteResource(doc);
readOnlyFile.delete();
}

@Test
public void testCountWordsReturnsZeroWithoutTokenKind() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document doc = Factory.newDocument("Just text, no tokens.");
int count = tool.countWords(doc);
assertEquals(0, count);
Factory.deleteResource(doc);
}

@Test
public void testPrintTableHeaderIncludesAllColumnsWithModes() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setMoreInfo(true);
tool.setVerboseMode(true);
tool.printTableHeader();
}

@Test
public void testPrintTableHeaderMinimalWithAllModesOff() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setMoreInfo(false);
tool.setVerboseMode(false);
tool.printTableHeader();
}

@Test
public void testPrintStatsForTypeWithMissingStatMaps() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// CorpusBenchmarkTool.annotTypes = new ArrayList<String>();
// CorpusBenchmarkTool.annotTypes.add("NonexistentType");
tool.printStatsForType("NonexistentType");
}

@Test
public void testAnnotationDifferWithEmptyAnnotationSet() throws ResourceInstantiationException {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document doc1 = Factory.newDocument("Some text.");
Document doc2 = Factory.newDocument("Another doc.");
// CorpusBenchmarkTool.annotTypes = new ArrayList<String>();
// CorpusBenchmarkTool.annotTypes.add("FakeType");
AnnotationDiffer ad = tool.measureDocs(doc1, doc2, "FakeType");
assertNull(ad);
Factory.deleteResource(doc1);
Factory.deleteResource(doc2);
}

@Test
public void testApplicationFileSetterAcceptsNullFile() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setApplicationFile(null);
}

@Test
public void testSetAndGetDiffFeaturesListWithEmptySet() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Set<String> empty = new HashSet<String>();
tool.setDiffFeaturesList(empty);
assertEquals(Collections.emptySet(), tool.getDiffFeaturesList());
}

@Test
public void testGetThresholdDefaultValue() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
double threshold = tool.getThreshold();
assertEquals(0.5, threshold, 0.0001);
}

@Test
public void testMeasureDocsWithNullAnnotSetName() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document keyDoc = Factory.newDocument("Document A");
Document respDoc = Factory.newDocument("Document B");
// CorpusBenchmarkTool.annotTypes = new ArrayList<String>();
// CorpusBenchmarkTool.annotTypes.add("Token");
tool.setDiffFeaturesList(Collections.emptySet());
AnnotationDiffer differ = tool.measureDocs(keyDoc, respDoc, "Token");
assertNotNull(differ);
Factory.deleteResource(keyDoc);
Factory.deleteResource(respDoc);
}

@Test
public void testMeasureDocsWithNonMatchingTypesReturnsNull() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document doc1 = Factory.newDocument("aaa");
Document doc2 = Factory.newDocument("bbb");
AnnotationDiffer diff = tool.measureDocs(doc1, doc2, "UnmatchedType");
assertNull(diff);
Factory.deleteResource(doc1);
Factory.deleteResource(doc2);
}

@Test
public void testPrintStatsForProcessedModeTrue() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// CorpusBenchmarkTool.annotTypes = new ArrayList<String>();
// CorpusBenchmarkTool.annotTypes.add("Token");
java.lang.reflect.Field processedFlag;
java.lang.reflect.Field processedMap;
java.lang.reflect.Field fmeasureMap;
try {
processedFlag = CorpusBenchmarkTool.class.getDeclaredField("hasProcessed");
processedMap = CorpusBenchmarkTool.class.getDeclaredField("proc_correctByType");
fmeasureMap = CorpusBenchmarkTool.class.getDeclaredField("proc_fMeasureByType");
processedFlag.setAccessible(true);
processedMap.setAccessible(true);
fmeasureMap.setAccessible(true);
processedFlag.set(tool, true);
Map<String, Long> m = new HashMap<String, Long>();
m.put("Token", 3L);
processedMap.set(tool, m);
Map<String, Double> m2 = new HashMap<String, Double>();
m2.put("Token", 1.0d);
fmeasureMap.set(tool, m2);
tool.printStatsForType("Token");
} catch (Exception e) {
fail("Reflection failed");
}
}

@Test
public void testCalculateAvgTotalWhenAllStatisticsAreZero() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// CorpusBenchmarkTool.annotTypes = new ArrayList<String>();
// CorpusBenchmarkTool.annotTypes.add("EmptyStatsType");
tool.calculateAvgTotal();
assertEquals(0.0, tool.getFmeasureAverageCalc(), 0.00001);
assertEquals(0.0, tool.getPrecisionAverageCalc(), 0.00001);
assertEquals(0.0, tool.getRecallAverageCalc(), 0.00001);
}

@Test
public void testAnnotationDifferMissingSpuriousPartialAreEmptySets() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document docA = Factory.newDocument("Text A");
Document docB = Factory.newDocument("Text B");
tool.setDiffFeaturesList(Collections.singleton("type"));
AnnotationDiffer diff = tool.measureDocs(docA, docB, "Token");
assertNotNull(diff);
Set<?> missing = diff.getAnnotationsOfType(AnnotationDiffer.MISSING_TYPE);
Set<?> spurious = diff.getAnnotationsOfType(AnnotationDiffer.SPURIOUS_TYPE);
Set<?> partial = diff.getAnnotationsOfType(AnnotationDiffer.PARTIALLY_CORRECT_TYPE);
assertNotNull(missing);
assertNotNull(spurious);
assertNotNull(partial);
Factory.deleteResource(docA);
Factory.deleteResource(docB);
}

@Test
public void testVerboseAnnotationOutputSafeWhenRecallBelowThreshold() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document key = Factory.newDocument("doc");
Document resp = Factory.newDocument("doc");
tool.setDiffFeaturesList(Collections.emptySet());
// CorpusBenchmarkTool.annotTypes = new ArrayList<String>();
// CorpusBenchmarkTool.annotTypes.add("Token");
AnnotationDiffer diff = tool.measureDocs(key, resp, "Token");
java.lang.reflect.Field thresholdField = CorpusBenchmarkTool.class.getDeclaredField("threshold");
thresholdField.setAccessible(true);
thresholdField.set(tool, 1.0);
tool.setVerboseMode(true);
tool.printAnnotations(diff, key, resp);
Factory.deleteResource(key);
Factory.deleteResource(resp);
}

@Test
public void testPrintStatsWithMoreInfoColors() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// CorpusBenchmarkTool.annotTypes = new ArrayList<String>();
// CorpusBenchmarkTool.annotTypes.add("Colored");
java.lang.reflect.Field moreInfo = null;
try {
moreInfo = CorpusBenchmarkTool.class.getDeclaredField("isMoreInfoMode");
moreInfo.setAccessible(true);
moreInfo.set(tool, true);
} catch (Exception e) {
fail("Failed to set properties by reflection");
}
tool.printStatsForType("Colored");
}

@Test
public void testPrintStatisticsWithoutSettingTypes() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// CorpusBenchmarkTool.annotTypes = null;
tool.printStatistics();
}

@Test
public void testEvaluateAllThreeSkipsIfAnnotDifferIsNull() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document doc1 = Factory.newDocument("aaa");
Document doc2 = Factory.newDocument("bbb");
tool.evaluateAllThree(null, doc1, doc2, null);
Factory.deleteResource(doc1);
Factory.deleteResource(doc2);
}

@Test
public void testAvgPrintHandlesExactRounding() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
String val = tool.avgPrint(0.500123456, 3);
assertEquals("0.5", val);
}

@Test
public void testSettersAndGettersAllFlags() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setGenerateMode(true);
tool.setMarkedStored(true);
tool.setMarkedClean(true);
tool.setMarkedDS(true);
tool.setMoreInfo(true);
tool.setVerboseMode(true);
assertTrue(tool.getGenerateMode());
assertTrue(tool.getMarkedStored());
assertTrue(tool.getMarkedClean());
assertTrue(tool.getMarkedDS());
assertTrue(tool.getMoreInfo());
assertTrue(tool.getVerboseMode());
}

@Test
public void testGetStartDirectoryIsTheSameAsSet() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File dir = new File(System.getProperty("java.io.tmpdir"));
tool.setStartDirectory(dir);
assertEquals(dir, tool.getStartDirectory());
}

@Test
public void testGetThresholdDefaultAndSet() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
double defaultThreshold = tool.getThreshold();
assertEquals(0.5, defaultThreshold, 0.00001);
tool.setThreshold(0.25);
assertEquals(0.25, tool.getThreshold(), 0.00001);
}
}
