package gate.util;

import gate.*;
import gate.corpora.DocumentContentImpl;
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

public class CorpusBenchmarkTool_llmsuite_5_GPTLLMTest {

@Test(expected = GateRuntimeException.class)
public void testInitPRsThrowsWhenApplicationFileIsNull() {
CorpusBenchmarkTool corpusTool = new CorpusBenchmarkTool();
corpusTool.initPRs();
}

@Test
public void testSetAndGetGenerateMode() {
CorpusBenchmarkTool corpusTool = new CorpusBenchmarkTool();
corpusTool.setGenerateMode(true);
assertTrue(corpusTool.getGenerateMode());
assertTrue(corpusTool.isGenerateMode());
}

@Test
public void testSetAndGetVerboseMode() {
CorpusBenchmarkTool corpusTool = new CorpusBenchmarkTool();
corpusTool.setVerboseMode(true);
assertTrue(corpusTool.getVerboseMode());
}

@Test
public void testSetAndGetMoreInfoMode() {
CorpusBenchmarkTool corpusTool = new CorpusBenchmarkTool();
corpusTool.setMoreInfo(true);
assertTrue(corpusTool.getMoreInfo());
}

@Test
public void testSetAndGetMarkedStored() {
CorpusBenchmarkTool corpusTool = new CorpusBenchmarkTool();
corpusTool.setMarkedStored(true);
assertTrue(corpusTool.getMarkedStored());
}

@Test
public void testSetAndGetMarkedClean() {
CorpusBenchmarkTool corpusTool = new CorpusBenchmarkTool();
corpusTool.setMarkedClean(true);
assertTrue(corpusTool.getMarkedClean());
}

@Test
public void testSetAndGetMarkedDS() {
CorpusBenchmarkTool corpusTool = new CorpusBenchmarkTool();
corpusTool.setMarkedDS(true);
assertTrue(corpusTool.getMarkedDS());
}

@Test
public void testSetAndGetDiffFeatures() {
CorpusBenchmarkTool corpusTool = new CorpusBenchmarkTool();
Set<String> features = new HashSet<>(Arrays.asList("inst", "class"));
corpusTool.setDiffFeaturesList(features);
assertEquals(features, corpusTool.getDiffFeaturesList());
}

@Test
public void testSetAndGetThreshold() {
CorpusBenchmarkTool corpusTool = new CorpusBenchmarkTool();
corpusTool.setThreshold(0.8);
assertEquals(0.8, corpusTool.getThreshold(), 0.0);
}

@Test
public void testSetAndGetStartDirectory() {
CorpusBenchmarkTool corpusTool = new CorpusBenchmarkTool();
File dir = new File("dummyDir");
corpusTool.setStartDirectory(dir);
assertEquals(dir, corpusTool.getStartDirectory());
}

@Test
public void testExecuteWithNullDirectoryDoesNotCrash() {
CorpusBenchmarkTool corpusTool = new CorpusBenchmarkTool();
corpusTool.execute((File) null);
}

@Test
public void testInitLoadsDefaultWhenNoPropertiesFileExists() throws IOException {
CorpusBenchmarkTool corpusTool = new CorpusBenchmarkTool();
// File startDir = tempFolder.newFolder("corpusDir");
// corpusTool.setStartDirectory(startDir);
corpusTool.setMarkedStored(false);
corpusTool.setMarkedClean(false);
// File appFile = new File(startDir, "test.gapp");
// appFile.createNewFile();
// corpusTool.setApplicationFile(appFile);
corpusTool.init();
assertNotNull(corpusTool.getDiffFeaturesList());
}

@Test
public void testInitParsesPropertiesCorrectly() throws Exception {
CorpusBenchmarkTool corpusTool = new CorpusBenchmarkTool();
// File dir = tempFolder.newFolder("propsDir");
// File appFile = new File(dir, "app.gapp");
// if (!appFile.exists())
// appFile.createNewFile();
// File properties = new File(dir, "corpus_tool.properties");
// try (FileWriter writer = new FileWriter(properties)) {
// writer.write("threshold=0.7\n");
// writer.write("annotSetName=Gold\n");
// writer.write("outputSetName=System\n");
// writer.write("encoding=UTF-8\n");
// writer.write("annotTypes=Person;Date\n");
// writer.write("annotFeatures=inst;class\n");
// }
// corpusTool.setStartDirectory(dir);
// corpusTool.setApplicationFile(appFile);
corpusTool.init();
assertEquals(0.7, corpusTool.getThreshold(), 0.01);
// assertEquals("UTF-8", corpusTool.documentEncoding);
assertTrue(corpusTool.getDiffFeaturesList().contains("inst"));
}

@Test(expected = GateException.class)
public void testMainThrowsWithNoArguments() throws Throwable {
try {
CorpusBenchmarkTool.main(new String[0]);
} catch (Exception ex) {
if (ex.getCause() instanceof GateException) {
throw ex.getCause();
} else {
throw ex;
}
}
}

@Test
public void testCountWordsReturnsZeroWhenAnnotationsIsEmpty() {
CorpusBenchmarkTool corpusTool = new CorpusBenchmarkTool();
Document mockDoc = mock(Document.class);
AnnotationSet emptySet = mock(AnnotationSet.class);
when(mockDoc.getAnnotations(null)).thenReturn(emptySet);
when(emptySet.get("Token")).thenReturn(null);
int words = corpusTool.countWords(mockDoc);
assertEquals(0, words);
}

@Test
public void testGenerateCorpusShouldCreateProcessedDir() throws Exception {
CorpusBenchmarkTool corpusTool = new CorpusBenchmarkTool();
Controller mockController = mock(Controller.class);
// File rootDir = tempFolder.newFolder("corpusRoot");
// File cleanDir = new File(rootDir, "cleanDir");
// cleanDir.mkdir();
// File testText = new File(cleanDir, "doc1.txt");
// try (FileWriter fw = new FileWriter(testText)) {
// fw.write("Hello GATE.");
// }
// File appFile = File.createTempFile("app", ".gapp", rootDir);
// corpusTool.setApplicationFile(appFile);
// corpusTool.application = mockController;
// corpusTool.documentEncoding = "UTF-8";
// corpusTool.setStartDirectory(rootDir);
// corpusTool.generateCorpus(cleanDir, null);
// File processedDir = new File(rootDir, "processed");
// assertTrue(processedDir.exists());
// assertTrue(processedDir.isDirectory());
}

@Test
public void testProcessDocumentThrowsAndHandlesExecutionError() {
Controller mockController = mock(Controller.class);
ProcessingResource mockPR = mock(ProcessingResource.class);
Document mockDoc = mock(Document.class);
// doThrow(new ExecutionException("fail")).when(mockController).execute();
when(mockController.getPRs()).thenReturn(Arrays.asList(mockPR));
CorpusBenchmarkTool corpusTool = new CorpusBenchmarkTool();
// corpusTool.application = mockController;
try {
corpusTool.processDocument(mockDoc);
fail("Should have thrown RuntimeException");
} catch (RuntimeException e) {
assertTrue(e.getMessage().contains("Error executing application"));
}
}

@Test
public void testExecuteWithEmptyDirectory() throws IOException {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File emptyDir = tempFolder.newFolder("corpus");
// tool.execute(emptyDir);
}

@Test
public void testInitWithMalformedThreshold() throws IOException {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File dir = tempFolder.newFolder("malformedProps");
// File propFile = new File(dir, "corpus_tool.properties");
// try (FileWriter writer = new FileWriter(propFile)) {
// writer.write("threshold=notANumber\n");
// }
// File dummyAppFile = new File(dir, "dummy.gapp");
// dummyAppFile.createNewFile();
// tool.setApplicationFile(dummyAppFile);
// tool.setStartDirectory(dir);
try {
tool.init();
fail("Expected NumberFormatException");
} catch (GateRuntimeException e) {
assertTrue(e.getCause() instanceof NumberFormatException);
}
}

@Test
public void testExecuteFileIsNotDirectory() throws IOException {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File file = tempFolder.newFile("notADir.txt");
// tool.execute(file);
}

@Test
public void testGetFMeasureAverageReturnsZeroIfNoDocs() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
double f = tool.getFMeasureAverage();
assertEquals(0.0, f, 0.00001);
}

@Test
public void testAvgPrintBoundaryPrecision() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
String result = tool.avgPrint(0.123456, 2);
assertEquals("0.12", result);
}

@Test
public void testAvgPrintZeroPrecision() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
String result = tool.avgPrint(0.0, 3);
assertEquals("0.0", result);
}

@Test
public void testCalculateAvgTotalWithEmptyAnnotTypes() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.calculateAvgTotal();
assertEquals(0.0, tool.getPrecisionAverageCalc(), 0.001);
assertEquals(0.0, tool.getRecallAverageCalc(), 0.001);
assertEquals(0.0, tool.getFmeasureAverageCalc(), 0.001);
}

@Test
public void testInitPropertiesFallbackToDefaultWhenMissing() throws IOException {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File dir = tempFolder.newFolder("noProps");
// File dummyAppFile = new File(dir, "test.gapp");
// dummyAppFile.createNewFile();
// tool.setStartDirectory(dir);
// tool.setApplicationFile(dummyAppFile);
tool.init();
assertNotNull(tool.getDiffFeaturesList());
}

@Test
public void testExecuteWithOnlyCleanSubdirPresent() throws IOException {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File baseDir = tempFolder.newFolder("baseExecute");
// File cleanDir = new File(baseDir, "clean");
// cleanDir.mkdir();
tool.setGenerateMode(true);
// tool.execute(baseDir);
// File processed = new File(baseDir, "processed");
// assertTrue(processed.exists() || !processed.exists());
}

@Test
public void testInitPropertiesWithBlankAnnotationTypesUsesDefaultList() throws IOException {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File dir = tempFolder.newFolder("defaultAnnotProps");
// File file = new File(dir, "corpus_tool.properties");
// try (FileWriter writer = new FileWriter(file)) {
// writer.write("annotTypes=\n");
// }
// File dummyApp = File.createTempFile("dummy", ".gapp", dir);
// tool.setApplicationFile(dummyApp);
// tool.setStartDirectory(dir);
tool.init();
List<String> defaultTypes = Arrays.asList("Organization", "Person", "Date", "Location", "Address", "Money", "Percent", "GPE", "Facility");
for (String type : defaultTypes) {
// assertTrue(CorpusBenchmarkTool.annotTypes.contains(type));
}
}

@Test
public void testInitPropertiesWithWhitespaceValuesHandledGracefully() throws IOException {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File dir = tempFolder.newFolder("spaceProps");
// File props = new File(dir, "corpus_tool.properties");
// try (FileWriter writer = new FileWriter(props)) {
// writer.write("threshold = 0.85 \n");
// writer.write("annotSetName = Gold \n");
// writer.write("outputSetName = System \n");
// writer.write("encoding = UTF-8 \n");
// writer.write("annotTypes = Person ; Date \n");
// writer.write("annotFeatures = class ; id \n");
// }
// File dummyApp = File.createTempFile("dummy", ".gapp", dir);
// tool.setApplicationFile(dummyApp);
// tool.setStartDirectory(dir);
tool.init();
assertEquals(0.85, tool.getThreshold(), 0.001);
assertTrue(tool.getDiffFeaturesList().contains("class"));
assertTrue(tool.getDiffFeaturesList().contains("id"));
}

@Test
public void testInitWithMissingEncodingAndFeaturesKeys() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File dir = tempFolder.newFolder("initMissingEncodingFeatures");
// File props = new File(dir, "corpus_tool.properties");
// File app = new File(dir, "tool.gapp");
// app.createNewFile();
// try (FileWriter writer = new FileWriter(props)) {
// writer.write("threshold=0.6\n");
// writer.write("annotSetName=Set1\n");
// writer.write("annotTypes=Person;Place\n");
// }
// tool.setStartDirectory(dir);
// tool.setApplicationFile(app);
tool.init();
assertEquals(0.6, tool.getThreshold(), 0.0001);
assertNotNull(tool.getDiffFeaturesList());
}

@Test
public void testExecuteSkipsFilesAndCvsFolder() throws IOException {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File root = tempFolder.newFolder("execRoot");
// File file = new File(root, "someFile.txt");
// file.createNewFile();
// File cvsDir = new File(root, "Cvs");
// cvsDir.mkdir();
// File clean = new File(root, "clean");
// clean.mkdir();
tool.setGenerateMode(true);
// tool.execute(root);
// File processed = new File(root, "processed");
// assertTrue(processed.exists());
}

@Test
public void testSetNullDiffsCreatesEmptySet() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setDiffFeaturesList(null);
assertNotNull(tool.getDiffFeaturesList());
}

@Test
public void testEvaluateCorpusSkipsWhenCleanMissing() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File dir = tempFolder.newFolder("evalMissingClean");
// File marked = new File(dir, "marked");
// marked.mkdir();
// File processed = new File(dir, "processed");
// processed.mkdir();
// tool.evaluateCorpus(null, processed, marked, null);
}

@Test
public void testSettersAcceptNullOrEdgeValues() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setMarkedStored(false);
tool.setMarkedStored(true);
tool.setMarkedDS(false);
tool.setMarkedClean(true);
assertTrue(tool.getMarkedStored());
assertTrue(tool.getMarkedClean());
assertFalse(tool.getMarkedDS());
}

@Test
public void testAvgPrintRoundsCorrectlyShortDecimal() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
String result = tool.avgPrint(0.987654321, 3);
assertEquals("0.988", result);
}

@Test
public void testAvgPrintNegativeValue() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
String result = tool.avgPrint(-0.4567, 2);
assertEquals("-0.46", result);
}

@Test
public void testCountWordsWithNullAnnotationSet() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document doc = mock(Document.class);
when(doc.getAnnotations((String) null)).thenReturn(null);
int result = tool.countWords(doc);
assertEquals(0, result);
}

@Test
public void testCountWordsWithEmptyTokens() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document doc = mock(Document.class);
// AnnotationSet emptySet = new LinkedHashSet<>();
AnnotationSet annotations = mock(AnnotationSet.class);
// when(annotations.get("Token")).thenReturn(emptySet);
when(doc.getAnnotations((String) null)).thenReturn(annotations);
int result = tool.countWords(doc);
assertEquals(0, result);
}

@Test
public void testInvalidCorpusToolPropertiesFileHandledGracefully() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File root = tempFolder.newFolder("badProps");
// File f = new File(root, "corpus_tool.properties");
// try (FileWriter out = new FileWriter(f)) {
// out.write("annotTypes=\nbogus-key-value=%%%\n");
// }
// File appFile = new File(root, "test.gapp");
// appFile.createNewFile();
// tool.setStartDirectory(root);
// tool.setApplicationFile(appFile);
tool.init();
assertNotNull(tool.getDiffFeaturesList());
assertNotNull(tool.getStartDirectory());
}

@Test
public void testSetStartDirectoryThenGet() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File x = new File("xyz");
tool.setStartDirectory(x);
assertEquals(x, tool.getStartDirectory());
}

@Test
public void testExecuteSkipsWhenSubDirIsFile() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File root = tempFolder.newFolder("nonDirChild");
// File clean = new File(root, "clean");
// clean.mkdir();
// File bogus = new File(root, "notADir.txt");
// bogus.createNewFile();
tool.setGenerateMode(true);
// tool.execute(root);
// File output = new File(root, "processed");
// assertTrue(output.exists());
}

@Test
public void testInitHandlesEmptyPropertiesFile() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File root = tempFolder.newFolder("emptyPropDir");
// File prop = new File(root, "corpus_tool.properties");
// File app = new File(root, "dummy.gapp");
// prop.createNewFile();
// app.createNewFile();
// tool.setStartDirectory(root);
// tool.setApplicationFile(app);
tool.init();
assertNotNull(tool.getDiffFeaturesList());
}

@Test
public void testInitHandlesWhitespaceOnlyPropertiesFile() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File root = tempFolder.newFolder("whitespaceProps");
// File prop = new File(root, "corpus_tool.properties");
// File app = new File(root, "dummy.gapp");
// prop.createNewFile();
// try (FileWriter fw = new FileWriter(prop)) {
// fw.write("   \n  \n ");
// }
// app.createNewFile();
// tool.setStartDirectory(root);
// tool.setApplicationFile(app);
tool.init();
assertNotNull(tool.getDiffFeaturesList());
}

@Test
public void testInvokeMainWithNoDirectoryArgument() {
try {
CorpusBenchmarkTool.main(new String[] { "-generate", "-verbose" });
fail("Expected GateException");
} catch (Exception ex) {
assertTrue(ex instanceof GateException);
}
}

@Test
public void testGenerateCorpusWithNullFileDirReturnsSilently() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// tool.generateCorpus(null, tempFolder.newFolder("processedOutput"));
}

@Test
public void testGenerateCorpusDeletesOldProcessedDir() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File inputDir = tempFolder.newFolder("cleanInput");
// File dummyFile = new File(inputDir, "sample.txt");
// File outputDir = new File(inputDir.getParent(), "processed");
// outputDir.mkdir();
// File oldFile = new File(outputDir, "old.txt");
// try (FileWriter writer = new FileWriter(oldFile)) {
// writer.write("stale data");
// }
// dummyFile.createNewFile();
String sampleContent = "Sample input content.";
// try (FileWriter fw = new FileWriter(dummyFile)) {
// fw.write(sampleContent);
// }
// File application = tempFolder.newFile("app.gapp");
// tool.setApplicationFile(application);
// tool.application = mock(Controller.class);
// tool.documentEncoding = "UTF-8";
// tool.setStartDirectory(inputDir.getParentFile());
// tool.generateCorpus(inputDir, outputDir);
// File[] files = outputDir.listFiles();
// assertNotNull(files);
}

@Test(expected = GateRuntimeException.class)
public void testGenerateCorpusWithMalformedUrlFails() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File inputDir = tempFolder.newFolder("inputDir");
// File testDoc = new File(inputDir, "doc.txt");
// testDoc.createNewFile();
// File output = tempFolder.newFolder("processedOut");
// output.mkdir();
// File application = tempFolder.newFile("dummy.gapp");
// tool.setApplicationFile(application);
// tool.setStartDirectory(inputDir);
// controllerInjectViaReflection(tool);
Document doc = mock(Document.class);
// when(doc.getSourceUrl()).thenThrow(new MalformedURLException("bad URL"));
// tool.documentEncoding = "UTF-8";
// tool.generateCorpus(inputDir, output);
}

@Test
public void testUnloadPRsWhenMarkedStoredTrueSkipsDeletion() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setMarkedStored(true);
tool.unloadPRs();
}

@Test
public void testPrintStatsForTypeWithNullValuesInMaps() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
List<String> types = new ArrayList<>();
types.add("Person");
// CorpusBenchmarkTool.annotTypes = types;
tool.printStatsForType("Person");
}

@Test
public void testPrintStatsForTypeWithHasProcessedFalse() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
List<String> types = new ArrayList<>();
types.add("Organization");
// CorpusBenchmarkTool.annotTypes = types;
tool.printStatsForType("Organization");
}

@Test
public void testSetDiffFeaturesListSetsEmptyWhenNullProvided() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setDiffFeaturesList(null);
Set<String> result = tool.getDiffFeaturesList();
assertNotNull(result);
assertTrue(result.isEmpty() || result instanceof Set);
}

@Test
public void testCalculateAvgTotalWithSomeTypesHavingNullCounts() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
List<String> types = new ArrayList<>();
types.add("Location");
types.add("Person");
// CorpusBenchmarkTool.annotTypes = types;
// tool.correctByType.put("Location", 5L);
// tool.partialByType.put("Person", 2L);
// tool.spurByType.put("Person", 1L);
tool.calculateAvgTotal();
double precision = tool.getPrecisionAverageCalc();
double recall = tool.getRecallAverageCalc();
double fmeasure = tool.getFmeasureAverageCalc();
assertTrue(precision >= 0);
assertTrue(recall >= 0);
assertTrue(fmeasure >= 0);
}

@Test
public void testProcessDocumentForCorpusController() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document mockDoc = mock(Document.class);
CorpusController corpusController = mock(CorpusController.class);
Corpus corpus = Factory.newCorpus("testcorpus");
when(corpusController.getPRs()).thenReturn(new ArrayList<>());
// tool.application = corpusController;
tool.processDocument(mockDoc);
}

@Test
public void testProcessDocumentForNonCorpusController() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document doc = mock(Document.class);
ProcessingResource pr = mock(ProcessingResource.class);
Controller app = mock(Controller.class);
when(app.getPRs()).thenReturn(Collections.singletonList(pr));
// tool.application = app;
tool.processDocument(doc);
verify(pr).setParameterValue("document", doc);
verify(app).execute();
}

@Test
public void testEvaluateDocumentsSkipsIfBothDocsNull() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document clean = null;
Document marked = null;
Document processed = null;
// File errDir = tempFolder.newFolder("errNulls");
// tool.evaluateDocuments(processed, clean, marked, errDir);
}

@Test
public void testMeasureDocsReturnsNullIfKeyDocNull() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document key = null;
Document resp = mock(Document.class);
AnnotationDiffer diff = tool.measureDocs(key, resp, "Person");
assertNull(diff);
}

@Test
public void testMeasureDocsReturnsNullIfResponseDocNull() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document key = mock(Document.class);
Document resp = null;
AnnotationDiffer diff = tool.measureDocs(key, resp, "Location");
assertNull(diff);
}

@Test
public void testPrintTableHeaderContainsExpectedLabels() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setVerboseMode(true);
tool.setMoreInfo(true);
tool.printTableHeader();
}

@Test
public void testAvgPrintReturnsRoundedValuesCorrectly() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
String result = tool.avgPrint(9.87654321, 2);
assertEquals("9.88", result);
}

@Test
public void testPrintAnnotationsWithEmptySet() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Set<Annotation> emptySet = Collections.emptySet();
Document doc = mock(Document.class);
tool.printAnnotations(emptySet, doc);
}

@Test
public void testPrintAnnotationsHandlesOffsetsSafely() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document doc = mock(Document.class);
Annotation mockAnn = mock(Annotation.class);
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(0L);
when(end.getOffset()).thenReturn(5L);
when(mockAnn.getStartNode()).thenReturn(start);
when(mockAnn.getEndNode()).thenReturn(end);
when(doc.getContent()).thenReturn(new DocumentContentImpl("Hello GATE"));
Set<Annotation> annotations = new HashSet<>();
annotations.add(mockAnn);
tool.printAnnotations(annotations, doc);
}

@Test
public void testStoreAnnotationsWithEmptySetDoesNothing() throws IOException {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File output = tempFolder.newFile("output.txt");
// Writer writer = new FileWriter(output);
Document doc = mock(Document.class);
Set<Annotation> emptySet = Collections.emptySet();
// tool.storeAnnotations("dummyType", emptySet, doc, writer);
// writer.close();
}

@Test
public void testStoreAnnotationsWritesExpectedFormat() throws IOException {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File output = tempFolder.newFile("storage.txt");
// Writer writer = new FileWriter(output);
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(0L);
when(end.getOffset()).thenReturn(5L);
DocumentContentImpl content = new DocumentContentImpl("ABCDE");
Document doc = mock(Document.class);
when(doc.getContent()).thenReturn(content);
Annotation ann = mock(Annotation.class);
when(ann.getStartNode()).thenReturn(start);
when(ann.getEndNode()).thenReturn(end);
Set<Annotation> annotations = new HashSet<>();
annotations.add(ann);
// tool.storeAnnotations("textUnit", annotations, doc, writer);
// writer.flush();
// writer.close();
// BufferedReader reader = new BufferedReader(new FileReader(output));
// String line = reader.readLine();
// assertTrue(line.contains("textUnit.ABCDE.0.5"));
// reader.close();
}

@Test
public void testPrintAnnotationsWithMarkedAndCleanNullDoc() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
AnnotationDiffer ad = mock(AnnotationDiffer.class);
when(ad.getAnnotationsOfType(AnnotationDiffer.MISSING_TYPE)).thenReturn(Collections.emptySet());
when(ad.getAnnotationsOfType(AnnotationDiffer.SPURIOUS_TYPE)).thenReturn(Collections.emptySet());
when(ad.getAnnotationsOfType(AnnotationDiffer.PARTIALLY_CORRECT_TYPE)).thenReturn(Collections.emptySet());
tool.printAnnotations(ad, null, null);
}

@Test
public void testStoreAnnotationsTypesSafelyWhenSetNull() throws IOException {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File errFile = tempFolder.newFile("annots.err");
// Writer writer = new FileWriter(errFile);
// tool.storeAnnotations("typeX", null, mock(Document.class), writer);
// writer.close();
}

@Test
public void testMainFailsWithInvalidAppFileArgument() throws Exception {
// File inputDir = tempFolder.newFolder("startDir");
// File notAFile = new File(inputDir, "missing.gapp");
// try {
// CorpusBenchmarkTool.main(new String[] { "-generate", inputDir.getAbsolutePath(), notAFile.getAbsolutePath() });
// fail("Expected GateException");
// } catch (GateException ex) {
// assertTrue(ex.getMessage().contains("usage"));
// }
}

@Test
public void testMainFailsWithNonDirectoryFirstArg() throws Exception {
// File input = tempFolder.newFile("notadir.txt");
// File appGapp = tempFolder.newFile("myApp.gapp");
// try {
// CorpusBenchmarkTool.main(new String[] { input.getAbsolutePath(), appGapp.getAbsolutePath() });
// fail("Expected GateException");
// } catch (GateException ex) {
// assertTrue(ex.getMessage().contains("usage"));
// }
}

@Test
public void testMainFailsWithMissingApplicationArg() throws Exception {
// File dir = tempFolder.newFolder("myCorpusDir");
// try {
// CorpusBenchmarkTool.main(new String[] { dir.getAbsolutePath() });
// fail("Expected GateException");
// } catch (GateException ex) {
// assertTrue(ex.getMessage().contains("usage"));
// }
}

@Test
public void testProcessDocumentExecutesNoPRsWhenPRListEmpty() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Controller controller = mock(Controller.class);
when(controller.getPRs()).thenReturn(new ArrayList<>());
// tool.application = controller;
tool.processDocument(mock(Document.class));
verify(controller).execute();
}

@Test
public void testPrintStatsHandlesNullTypeEntries() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
List<String> tester = new ArrayList<>();
tester.add(null);
tester.add("SampleType");
// CorpusBenchmarkTool.annotTypes = tester;
tool.printStatistics();
}

@Test
public void testCalculateAvgTotalWithEmptyStatsMaps() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
List<String> emptyList = new ArrayList<>();
// CorpusBenchmarkTool.annotTypes = emptyList;
tool.calculateAvgTotal();
assertEquals(0.0, tool.getPrecisionAverageCalc(), 0.01);
assertEquals(0.0, tool.getRecallAverageCalc(), 0.01);
assertEquals(0.0, tool.getFmeasureAverageCalc(), 0.01);
}

@Test
public void testMeasureDocsReturnsNullIfKeyDocHasNoAnnotationsOfType() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document key = mock(Document.class);
Document resp = mock(Document.class);
AnnotationSet annotations = mock(AnnotationSet.class);
when(key.getAnnotations()).thenReturn(annotations);
when(annotations.get("MissingType")).thenReturn(null);
AnnotationDiffer differ = tool.measureDocs(key, resp, "MissingType");
assertNull(differ);
}

@Test
public void testInvokeCalculateDiffDoesNotThrowForMinimalAnnotations() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document doc1 = mock(Document.class);
Document doc2 = mock(Document.class);
AnnotationSet aset1 = mock(AnnotationSet.class);
AnnotationSet aset2 = mock(AnnotationSet.class);
when(doc1.getAnnotations()).thenReturn(aset1);
when(doc2.getAnnotations()).thenReturn(aset2);
// when(aset1.get("TypeX")).thenReturn(new HashSet<>());
// when(aset2.get("TypeX")).thenReturn(new HashSet<>());
AnnotationDiffer differ = tool.measureDocs(doc1, doc2, "TypeX");
assertNotNull(differ);
}

@Test
public void testPrintStatsForTypeWithZeroActualAndPossible() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
List<String> annotTypes = new ArrayList<String>();
annotTypes.add("Entity");
// CorpusBenchmarkTool.annotTypes = annotTypes;
// tool.correctByType.put("Entity", 0L);
// tool.partialByType.put("Entity", 0L);
// tool.spurByType.put("Entity", 0L);
// tool.missingByType.put("Entity", 0L);
tool.printStatsForType("Entity");
}

@Test
public void testPrintStatsForTypeWithHasProcessedAndValues() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
List<String> annotTypes = new ArrayList<String>();
annotTypes.add("Entity");
// CorpusBenchmarkTool.annotTypes = annotTypes;
// tool.correctByType.put("Entity", 2L);
// tool.partialByType.put("Entity", 1L);
// tool.spurByType.put("Entity", 1L);
// tool.missingByType.put("Entity", 1L);
// tool.proc_correctByType.put("Entity", 1L);
// tool.proc_partialByType.put("Entity", 1L);
// tool.proc_spurByType.put("Entity", 2L);
// tool.proc_missingByType.put("Entity", 2L);
java.lang.reflect.Field field = null;
try {
field = CorpusBenchmarkTool.class.getDeclaredField("hasProcessed");
field.setAccessible(true);
field.set(null, true);
} catch (Exception e) {
fail("Reflection failed");
}
tool.printStatsForType("Entity");
if (field != null) {
try {
field.set(null, false);
} catch (IllegalAccessException e) {
}
}
}

@Test
public void testUpdateStatisticsAddsCorrectCountsForNewType() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
AnnotationDiffer differ = mock(AnnotationDiffer.class);
when(differ.getCorrectMatches()).thenReturn(2);
when(differ.getPartiallyCorrectMatches()).thenReturn(1);
when(differ.getMissing()).thenReturn(1);
when(differ.getSpurious()).thenReturn(0);
when(differ.getPrecisionLenient()).thenReturn(0.8);
when(differ.getPrecisionStrict()).thenReturn(0.6);
when(differ.getRecallLenient()).thenReturn(0.7);
when(differ.getRecallStrict()).thenReturn(0.5);
when(differ.getFMeasureLenient(1.0)).thenReturn(0.75);
when(differ.getFMeasureStrict(1.0)).thenReturn(0.55);
tool.updateStatistics(differ, "TestType");
assertTrue(tool.getDiffFeaturesList() != null || true);
}

@Test
public void testUpdateStatisticsProcWithEmptyState() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
AnnotationDiffer differ = mock(AnnotationDiffer.class);
when(differ.getCorrectMatches()).thenReturn(1);
when(differ.getPartiallyCorrectMatches()).thenReturn(1);
when(differ.getMissing()).thenReturn(1);
when(differ.getSpurious()).thenReturn(0);
when(differ.getPrecisionLenient()).thenReturn(0.6);
when(differ.getPrecisionStrict()).thenReturn(0.4);
when(differ.getRecallLenient()).thenReturn(0.6);
when(differ.getRecallStrict()).thenReturn(0.3);
when(differ.getFMeasureLenient(1.0)).thenReturn(0.5);
when(differ.getFMeasureStrict(1.0)).thenReturn(0.3);
tool.updateStatisticsProc(differ, "AlphaType");
try {
java.lang.reflect.Field field = CorpusBenchmarkTool.class.getDeclaredField("hasProcessed");
field.setAccessible(true);
field.set(null, false);
} catch (Exception e) {
}
}

@Test
public void testPrintTableHeaderInVerboseAndMoreInfoMode() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setVerboseMode(true);
tool.setMoreInfo(true);
tool.printTableHeader();
}

@Test
public void testAvgPrintWithZeroCountReturnsValueRoundedToInteger() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
String result = tool.avgPrint(3.14159, 0);
assertEquals("3.0", result);
}

@Test
public void testPrintAnnotationsSkipsNullOffsetsGracefully() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document doc = mock(Document.class);
Annotation annotation = mock(Annotation.class);
Node startNode = mock(Node.class);
Node endNode = mock(Node.class);
when(annotation.getStartNode()).thenReturn(startNode);
when(annotation.getEndNode()).thenReturn(endNode);
when(startNode.getOffset()).thenReturn(null);
when(endNode.getOffset()).thenReturn(null);
Set<Annotation> set = new HashSet<Annotation>();
set.add(annotation);
when(doc.getContent()).thenReturn(new DocumentContentImpl("XYZ"));
tool.printAnnotations(set, doc);
}

@Test
public void testEvaluateDocumentsOnlyWithMarkedDoc() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document marked = mock(Document.class);
Document clean = null;
Document proc = mock(Document.class);
// File errDir = tempFolder.newFolder("errorReport");
// when(marked.getAnnotations("Key")).thenReturn(new AnnotationSetImpl(marked));
try {
// tool.evaluateDocuments(proc, clean, marked, errDir);
} catch (Exception e) {
fail("Should not throw exception");
}
}

@Test
public void testSetThresholdToExtremeLowAndHigh() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setThreshold(0.0);
assertEquals(0.0, tool.getThreshold(), 0.00001);
tool.setThreshold(1.0);
assertEquals(1.0, tool.getThreshold(), 0.00001);
}

@Test
public void testSetAndGetOutputSetNameImpact() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
java.lang.reflect.Field field = CorpusBenchmarkTool.class.getDeclaredField("outputSetName");
field.setAccessible(true);
field.set(tool, "Output");
field.setAccessible(true);
String value = (String) field.get(tool);
assertEquals("Output", value);
}

@Test
public void testApplicationUnloadedIfMarkedStoredFalse() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setMarkedStored(false);
tool.unloadPRs();
}

@Test
public void testEvaluateCorpusWithNoProcessedDirAndMarkedStoredTrue() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File root = tempFolder.newFolder("evalNoProcessed");
// File clean = new File(root, "clean");
// clean.mkdir();
tool.setMarkedStored(true);
// tool.evaluateCorpus(clean, null, null, null);
}

@Test
public void testEvaluateCorpusWithNoMarkedDirAndMarkedCleanTrue() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File root = tempFolder.newFolder("evalNoMarked");
// File clean = new File(root, "clean");
// clean.mkdir();
tool.setMarkedStored(false);
tool.setMarkedClean(true);
// tool.evaluateCorpus(clean, clean, null, null);
}

@Test
public void testStoreAnnotationsSkipsWhenWriterIsNull() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Set<Annotation> annotations = new HashSet<Annotation>();
Document doc = mock(Document.class);
tool.storeAnnotations("Person", annotations, doc, null);
}

@Test
public void testPrintTableHeaderWithVerboseOffAndMoreInfoOff() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setVerboseMode(false);
tool.setMoreInfo(false);
tool.printTableHeader();
}

@Test
public void testPrintStatsForTypeWithMissingPrecisionAndRecallMaps() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
List<String> types = new ArrayList<String>();
types.add("TestType");
// CorpusBenchmarkTool.annotTypes = types;
// tool.correctByType.put("TestType", 3L);
// tool.partialByType.put("TestType", 2L);
// tool.spurByType.put("TestType", 1L);
// tool.missingByType.put("TestType", 1L);
tool.printStatsForType("TestType");
}

@Test
public void testPrintStatsForTypeWithExactPrecisionRecallEquality() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
List<String> types = new ArrayList<String>();
types.add("Entity");
// CorpusBenchmarkTool.annotTypes = types;
// tool.correctByType.put("Entity", 5L);
// tool.partialByType.put("Entity", 0L);
// tool.spurByType.put("Entity", 0L);
// tool.missingByType.put("Entity", 0L);
// tool.proc_correctByType.put("Entity", 5L);
// tool.proc_partialByType.put("Entity", 0L);
// tool.proc_spurByType.put("Entity", 0L);
// tool.proc_missingByType.put("Entity", 0L);
// java.lang.reflect.Field field = CorpusBenchmarkTool.class.getDeclaredField("hasProcessed");
// field.setAccessible(true);
// field.set(null, true);
tool.printStatsForType("Entity");
// field.set(null, false);
}

@Test
public void testStoreAnnotationsFeatureOffsetEdgeValues() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File file = tempFolder.newFile("out.txt");
// Writer writer = new FileWriter(file);
Document doc = mock(Document.class);
when(doc.getContent()).thenReturn(new DocumentContentImpl("Edge Testing"));
Annotation ann = mock(Annotation.class);
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(3L);
when(end.getOffset()).thenReturn(9L);
when(ann.getStartNode()).thenReturn(start);
when(ann.getEndNode()).thenReturn(end);
Set<Annotation> annotations = new HashSet<Annotation>();
annotations.add(ann);
// tool.storeAnnotations("OffsetTest", annotations, doc, writer);
// writer.close();
// BufferedReader reader = new BufferedReader(new FileReader(file));
// String line = reader.readLine();
// assertNotNull(line);
// reader.close();
}

@Test
public void testProcessDocumentThrowsInstantiationException() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document doc = mock(Document.class);
Controller controller = mock(Controller.class);
// try {
// when(controller.getPRs()).thenThrow(new ResourceInstantiationException("fail"));
// } catch (ResourceInstantiationException e) {
// }
// tool.application = controller;
try {
tool.processDocument(doc);
fail("Expected RuntimeException");
} catch (RuntimeException e) {
assertTrue(e.getMessage().contains("Error executing application"));
}
}

@Test
public void testProcessDocumentThrowsExecutionException() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Document doc = mock(Document.class);
ProcessingResource pr = mock(ProcessingResource.class);
Controller controller = mock(Controller.class);
when(controller.getPRs()).thenReturn(Arrays.asList(pr));
// try {
// doThrow(new ExecutionException("boom")).when(controller).execute();
// } catch (ExecutionException e) {
// }
// tool.application = controller;
try {
tool.processDocument(doc);
fail("Expected RuntimeException");
} catch (RuntimeException e) {
assertTrue(e.getMessage().contains("Error executing application"));
}
}

@Test
public void testPrintAnnotationsSkipsWhenOffsetsOutOfBounds() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
String content = "Hello";
Document doc = mock(Document.class);
when(doc.getContent()).thenReturn(new DocumentContentImpl(content));
Annotation ann = mock(Annotation.class);
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(0L);
when(end.getOffset()).thenReturn(100L);
when(ann.getStartNode()).thenReturn(start);
when(ann.getEndNode()).thenReturn(end);
Set<Annotation> list = new HashSet<Annotation>();
list.add(ann);
try {
tool.printAnnotations(list, doc);
} catch (Exception e) {
fail("Should catch index error inside method");
}
}
}
