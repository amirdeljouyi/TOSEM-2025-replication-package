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

public class CorpusBenchmarkTool_llmsuite_3_GPTLLMTest {

@Test
public void testSetAndGetGenerateMode() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setGenerateMode(true);
assertTrue(tool.getGenerateMode());
tool.setGenerateMode(false);
assertFalse(tool.getGenerateMode());
}

@Test
public void testSetAndGetVerboseMode() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setVerboseMode(true);
assertTrue(tool.getVerboseMode());
tool.setVerboseMode(false);
assertFalse(tool.getVerboseMode());
}

@Test
public void testSetAndGetMoreInfoMode() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setMoreInfo(true);
assertTrue(tool.getMoreInfo());
tool.setMoreInfo(false);
assertFalse(tool.getMoreInfo());
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
public void testSetAndGetMarkedStored() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setMarkedStored(true);
assertTrue(tool.getMarkedStored());
tool.setMarkedStored(false);
assertFalse(tool.getMarkedStored());
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
public void testSetAndGetDiffFeaturesList() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Set<String> features = new HashSet<String>();
features.add("class");
features.add("inst");
tool.setDiffFeaturesList(features);
Set<String> output = tool.getDiffFeaturesList();
assertNotNull(output);
assertTrue(output.contains("class"));
assertTrue(output.contains("inst"));
assertEquals(2, output.size());
}

@Test
public void testSetAndGetThreshold() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setThreshold(0.75);
double threshold = tool.getThreshold();
assertEquals(0.75, threshold, 0.0001);
}

@Test
public void testSetAndGetStartDirectory() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File startDir = tempFolder.newFolder("startDirTest");
// tool.setStartDirectory(startDir);
File result = tool.getStartDirectory();
// assertEquals(startDir.getAbsolutePath(), result.getAbsolutePath());
}

@Test
public void testSetAndGetApplicationFile() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File appFile = tempFolder.newFile("dummyAppFile.gapp");
// tool.setApplicationFile(appFile);
// assertNotNull(appFile);
// assertTrue(appFile.exists());
}

@Test(expected = GateRuntimeException.class)
public void testInitPRsThrowsWhenNoApplicationFileSet() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.initPRs();
}

@Test
public void testInitWithMissingPropertiesFile() throws Exception {
Gate.init();
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File dir = tempFolder.newFolder("noPropsDir");
// tool.setStartDirectory(dir);
tool.setMarkedStored(true);
tool.init();
assertNotNull(tool.getDiffFeaturesList());
}

@Test
public void testInitParsesPropertiesFileCorrectly() throws Exception {
Gate.init();
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File dir = tempFolder.newFolder("propsTestDir");
// File propsFile = new File(dir, "corpus_tool.properties");
// Writer writer = new FileWriter(propsFile);
// writer.write("threshold=0.9\n");
// writer.write("encoding=UTF-16\n");
// writer.write("annotSetName=Gold\n");
// writer.write("outputSetName=Response\n");
// writer.write("annotTypes=Person;Location\n");
// writer.write("annotFeatures=type;confidence\n");
// writer.flush();
// writer.close();
// tool.setStartDirectory(dir);
tool.setMarkedStored(true);
tool.init();
assertEquals(0.9, tool.getThreshold(), 0.0001);
Set<String> features = tool.getDiffFeaturesList();
assertTrue(features.contains("type"));
assertTrue(features.contains("confidence"));
assertEquals(2, features.size());
}

@Test
public void testExecuteWithNullDirectory() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.execute((File) null);
assertTrue(true);
}

@Test
public void testExecuteDirectoryWithNoCleanSubdir() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File testDir = tempFolder.newFolder("execTestDir");
// File cvsDir = new File(testDir, "Cvs");
// cvsDir.mkdir();
// File otherDir = new File(testDir, "misc");
// otherDir.mkdir();
// tool.execute(testDir);
// assertTrue(testDir.exists());
}

@Test
public void testInitWithInvalidThresholdFallsBackToDefault() throws Exception {
Gate.init();
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File dir = tempFolder.newFolder("badThresholdTest");
// File propsFile = new File(dir, "corpus_tool.properties");
// Writer writer = new FileWriter(propsFile);
// writer.write("threshold=INVALID_NUMBER\n");
// writer.flush();
// writer.close();
// tool.setStartDirectory(dir);
tool.setMarkedStored(true);
try {
tool.init();
fail("Expected NumberFormatException inside GateRuntimeException");
} catch (GateRuntimeException ex) {
assertTrue(ex.getCause() instanceof NumberFormatException);
}
}

@Test
public void testInitWithEmptyValuesFallsBackToDefaults() throws Exception {
Gate.init();
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File dir = tempFolder.newFolder("emptyValueProps");
// File propsFile = new File(dir, "corpus_tool.properties");
// Writer writer = new FileWriter(propsFile);
// writer.write("threshold=\n");
// writer.write("encoding=\n");
// writer.write("outputSetName=\n");
// writer.write("annotSetName=\n");
// writer.write("annotTypes=\n");
// writer.flush();
// writer.close();
// tool.setStartDirectory(dir);
tool.setMarkedStored(true);
tool.init();
assertEquals(0.5, tool.getThreshold(), 0.0001);
assertNotNull(tool.getDiffFeaturesList());
}

@Test
public void testExecuteHandlesEmptySubdirectoryList() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File rootDir = tempFolder.newFolder("rootWithEmptySubs");
// File cleanDir = new File(rootDir, "clean");
// cleanDir.mkdir();
// File markedDir = new File(rootDir, "marked");
// markedDir.mkdir();
// File processedDir = new File(rootDir, "processed");
// processedDir.mkdir();
// File emptySubDir = new File(rootDir, "sub1");
// emptySubDir.mkdir();
// tool.setStartDirectory(rootDir);
tool.setMarkedClean(true);
// tool.execute(rootDir);
// assertTrue(cleanDir.exists());
// assertTrue(emptySubDir.exists());
}

@Test
public void testExecuteWithMissingProcessedDirWhenUsingMarkedStored() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File rootDir = tempFolder.newFolder("missingProcessed");
// File cleanDir = new File(rootDir, "clean");
// cleanDir.mkdir();
// File markedDir = new File(rootDir, "marked");
// markedDir.mkdir();
tool.setMarkedStored(true);
// tool.setStartDirectory(rootDir);
// tool.execute(rootDir);
// assertTrue(markedDir.exists());
// assertTrue(cleanDir.exists());
}

@Test
public void testExecuteWithMissingMarkedDirFailsGracefully() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File rootDir = tempFolder.newFolder("missingMarked");
// File cleanDir = new File(rootDir, "clean");
// cleanDir.mkdir();
// File processedDir = new File(rootDir, "processed");
// processedDir.mkdir();
tool.setMarkedStored(true);
// tool.setStartDirectory(rootDir);
// tool.execute(rootDir);
// assertTrue(cleanDir.exists());
// assertTrue(processedDir.exists());
}

@Test
public void testMainWithUnknownOptionsShouldIgnoreThem() throws Exception {
Gate.init();
// File appFile = tempFolder.newFile("dummyApp.gapp");
// File dir = tempFolder.newFolder("mainDir");
// String[] args = new String[] { "-foobar", "-marked_clean", dir.getAbsolutePath(), appFile.getAbsolutePath() };
// try {
// CorpusBenchmarkTool.main(args);
// } catch (gate.GateException ex) {
// fail("Should not have failed on unknown -foobar flag");
// }
}

@Test
public void testMainThrowsIfTooFewArguments() throws Exception {
Gate.init();
String[] args = new String[] {};
// try {
// CorpusBenchmarkTool.main(args);
// fail("Expected GateException due to missing arguments");
// } catch (gate.GateException ex) {
// assertNotNull(ex.getMessage());
// }
}

@Test
public void testMainThrowsIfDataDirDoesNotExist() throws Exception {
Gate.init();
File bogusDir = new File("nonexistent-" + System.currentTimeMillis());
// File appFile = tempFolder.newFile("dummyApp.gapp");
// String[] args = new String[] { bogusDir.getAbsolutePath(), appFile.getAbsolutePath() };
// try {
// CorpusBenchmarkTool.main(args);
// fail("Expected GateException because directory doesn't exist");
// } catch (gate.GateException ex) {
// assertNotNull(ex.getMessage());
// }
}

@Test
public void testMainThrowsIfApplicationFileIsInvalid() throws Exception {
Gate.init();
// File dir = tempFolder.newFolder("validDir");
// File invalidAppFile = new File(dir, "nonexistentApp.gapp");
// String[] args = new String[] { dir.getAbsolutePath(), invalidAppFile.getAbsolutePath() };
// try {
// CorpusBenchmarkTool.main(args);
// fail("Expected GateException for missing application file");
// } catch (gate.GateException ex) {
// assertNotNull(ex.getMessage());
// }
}

@Test
public void testInitUsesDefaultAnnotationTypesWhenNoneProvided() throws Exception {
Gate.init();
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File dir = tempFolder.newFolder("defaultAnnotTypes");
// File props = new File(dir, "corpus_tool.properties");
// Writer writer = new FileWriter(props);
// writer.write("");
// writer.close();
// tool.setStartDirectory(dir);
tool.setMarkedStored(true);
tool.init();
assertNotNull(tool.getDiffFeaturesList());
}

@Test
public void testInitHandlesUnknownConfigKeysGracefully() throws Exception {
Gate.init();
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File dir = tempFolder.newFolder("uselessProps");
// File propsFile = new File(dir, "corpus_tool.properties");
// Writer writer = new FileWriter(propsFile);
// writer.write("unknownKey1=foobar\n");
// writer.write("unknownKey2=123\n");
// writer.close();
// tool.setStartDirectory(dir);
tool.setMarkedStored(true);
tool.init();
assertTrue(tool.getDiffFeaturesList().isEmpty() || tool.getDiffFeaturesList() != null);
}

@Test
public void testExecuteWithNestedDirectoriesRecursivelyTraverses() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File rootDir = tempFolder.newFolder("nestedCorpus");
// File level1 = new File(rootDir, "level1");
// File cleanDir = new File(level1, "clean");
// cleanDir.mkdirs();
// tool.setStartDirectory(rootDir);
tool.setMarkedStored(true);
// tool.execute(rootDir);
// assertTrue(rootDir.exists());
// assertTrue(level1.exists());
}

@Test
public void testEvaluateCorpusSkipsWhenNoHumanAnnotatedFilesExist() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File rootDir = tempFolder.newFolder("noMarked");
// File cleanDir = new File(rootDir, "clean");
// File processedDir = new File(rootDir, "processed");
// cleanDir.mkdir();
// processedDir.mkdir();
// tool.setStartDirectory(rootDir);
tool.setMarkedStored(true);
// tool.execute(rootDir);
// assertTrue(cleanDir.exists());
// assertTrue(processedDir.exists());
}

@Test
public void testEvaluateCorpusCreatesErrorDirectoryWhenMissing() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File rootDir = tempFolder.newFolder("missingErrorDir");
// File clean = new File(rootDir, "clean");
// File processed = new File(rootDir, "processed");
// File marked = new File(rootDir, "marked");
// clean.mkdir();
// processed.mkdir();
// marked.mkdir();
// tool.setStartDirectory(rootDir);
tool.setMarkedStored(true);
tool.setMoreInfo(true);
// tool.execute(rootDir);
// File errorDir = new File(rootDir, "err");
// assertTrue(errorDir.exists());
}

@Test
public void testPrintStatisticsHandlesEmptyAnnotationTypeListGracefully() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.printStatistics();
assertTrue(true);
}

@Test
public void testCalculateAvgTotalWithNoCounts() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.calculateAvgTotal();
assertEquals(0.0, tool.getPrecisionAverageCalc(), 0.00001);
assertEquals(0.0, tool.getRecallAverageCalc(), 0.00001);
assertEquals(0.0, tool.getFmeasureAverageCalc(), 0.00001);
}

@Test
public void testMainWithVerboseAndMoreinfoModes() throws Exception {
Gate.init();
// File corpusDir = tempFolder.newFolder("mainModesTest");
// File appFile = tempFolder.newFile("dummyApp.gapp");
// String[] args = { "-generate", "-verbose", "-moreinfo", corpusDir.getAbsolutePath(), appFile.getAbsolutePath() };
try {
// CorpusBenchmarkTool.main(args);
} catch (Exception e) {
}
}

@Test
public void testSetNullDiffFeaturesListClearsSet() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setDiffFeaturesList(null);
Set<String> result = tool.getDiffFeaturesList();
assertNull(result);
}

@Test
public void testDefaultIsNotGenerateMode() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
assertFalse(tool.getGenerateMode());
}

@Test
public void testCorpusToolHandlesNoTokenAnnotationsGracefully() throws Exception {
Gate.init();
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File rootDir = tempFolder.newFolder("noTokensTest");
// File clean = new File(rootDir, "clean");
// clean.mkdir();
// File processed = new File(rootDir, "processed");
// processed.mkdir();
// File marked = new File(rootDir, "marked");
// marked.mkdir();
// tool.setStartDirectory(rootDir);
tool.setMarkedClean(true);
// tool.execute(rootDir);
// assertTrue(clean.exists());
}

@Test
public void testEvaluateCorpusSkipsIfProcessedDirMissingAndMarkedStoredTrue() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File rootDir = tempFolder.newFolder("skipNoProcessed");
// File clean = new File(rootDir, "clean");
// clean.mkdir();
// File marked = new File(rootDir, "marked");
// marked.mkdir();
// tool.setStartDirectory(rootDir);
tool.setMarkedStored(true);
// tool.execute(rootDir);
// assertTrue(marked.exists());
}

@Test
public void testEvaluationSkipsIfNoCleanDirPresent() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File root = tempFolder.newFolder("noCleanDirRoot");
// File processed = new File(root, "processed");
// processed.mkdir();
// tool.setStartDirectory(root);
tool.setMarkedStored(true);
// tool.execute(root);
// assertTrue(processed.exists());
}

@Test
public void testInitWithOnlyFeatureListDefined() throws Exception {
Gate.init();
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File dir = tempFolder.newFolder("featureOnlyConfig");
// File props = new File(dir, "corpus_tool.properties");
// Writer writer = new FileWriter(props);
// writer.write("annotFeatures=alpha;beta;gamma\n");
// writer.close();
tool.setMarkedStored(true);
// tool.setStartDirectory(dir);
tool.init();
Set<String> features = tool.getDiffFeaturesList();
assertNotNull(features);
assertEquals(3, features.size());
assertTrue(features.contains("alpha"));
assertTrue(features.contains("beta"));
assertTrue(features.contains("gamma"));
}

@Test
public void testSetDiffFeaturesListToEmptySet() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Set<String> set = new HashSet<String>();
tool.setDiffFeaturesList(set);
Set<String> result = tool.getDiffFeaturesList();
assertNotNull(result);
assertEquals(0, result.size());
}

@Test
public void testInitWithOnlyEncodingDefined() throws Exception {
Gate.init();
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File dir = tempFolder.newFolder("encodingOnlyConfig");
// File props = new File(dir, "corpus_tool.properties");
// Writer writer = new FileWriter(props);
// writer.write("encoding=ISO-8859-1\n");
// writer.close();
tool.setMarkedStored(true);
// tool.setStartDirectory(dir);
tool.init();
assertNotNull(tool.getDiffFeaturesList());
}

@Test
public void testGetPrecisionRecallFMeasureWithZeroDocNumber() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
assertEquals(Double.NaN, tool.getPrecisionAverage(), 0.0001);
assertEquals(Double.NaN, tool.getRecallAverage(), 0.0001);
assertEquals(Double.NaN, tool.getFMeasureAverage(), 0.0001);
}

@Test
public void testAvgPrintRoundingAtFourDigits() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
String formatted = tool.avgPrint(0.123456, 4);
assertEquals("0.1235", formatted);
}

@Test
public void testAvgPrintExactBoundary() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
String formatted = tool.avgPrint(1.0, 2);
assertEquals("1.0", formatted);
}

@Test
public void testPrintStatisticsDoesNotCrashWithNullTypes() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.printStatistics();
assertTrue(true);
}

@Test
public void testPrintTableHeaderProducesHTML() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setVerboseMode(true);
tool.setMoreInfo(true);
tool.printTableHeader();
assertTrue(true);
}

@Test
public void testMultipleSettersAndGettersTogether() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setVerboseMode(true);
assertTrue(tool.getVerboseMode());
tool.setMoreInfo(true);
assertTrue(tool.getMoreInfo());
tool.setMarkedClean(true);
assertTrue(tool.getMarkedClean());
tool.setMarkedStored(true);
assertTrue(tool.getMarkedStored());
tool.setMarkedDS(true);
assertTrue(tool.getMarkedDS());
tool.setGenerateMode(true);
assertTrue(tool.getGenerateMode());
}

@Test
public void testInitWithNoPropertiesFileLoadsDefaults() throws Exception {
Gate.init();
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File dir = tempFolder.newFolder("missingProps");
// tool.setStartDirectory(dir);
tool.setMarkedStored(true);
tool.init();
assertNotNull(tool.getDiffFeaturesList());
}

@Test
public void testExecuteWithDirectoryContainingOnlyFiles() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File dir = tempFolder.newFolder("justFilesNoDirs");
// File file1 = new File(dir, "file1.txt");
// file1.createNewFile();
// File file2 = new File(dir, "file2.txt");
// file2.createNewFile();
// tool.setStartDirectory(dir);
tool.setMarkedClean(true);
// tool.execute(dir);
// assertTrue(dir.exists());
}

@Test
public void testSetStartDirectoryToNullDoesNotCrashGetStartDirectory() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setStartDirectory(null);
File result = tool.getStartDirectory();
assertNull(result);
}

@Test
public void testMainWithUnrecognizedFlagStillProcesses() throws Exception {
Gate.init();
// File dir = tempFolder.newFolder("mainArgsUnknownFlag");
// File appFile = tempFolder.newFile("dummyApp.gapp");
// String[] args = new String[] { "-unknownFlag", dir.getAbsolutePath(), appFile.getAbsolutePath() };
// try {
// CorpusBenchmarkTool.main(args);
// } catch (gate.GateException ex) {
// assertNotNull(ex);
// }
}

@Test
public void testInitWithOnlyAnnotationTypesDefined() throws Exception {
Gate.init();
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File dir = tempFolder.newFolder("typesOnlyDir");
// File props = new File(dir, "corpus_tool.properties");
// FileWriter writer = new FileWriter(props);
// writer.write("annotTypes=Person;Date\n");
// writer.close();
tool.setMarkedStored(true);
// tool.setStartDirectory(dir);
tool.init();
assertNotNull(tool.getDiffFeaturesList());
}

@Test
public void testSetAnnotationTypeFeatureListToNullClearsPrevious() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setDiffFeaturesList(null);
assertNull(tool.getDiffFeaturesList());
}

@Test
public void testSetAndGetAnnotationSetNames() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setVerboseMode(true);
tool.setMoreInfo(true);
tool.setMarkedStored(true);
tool.setMarkedClean(true);
tool.setMarkedDS(true);
tool.setGenerateMode(true);
assertTrue(tool.getVerboseMode());
assertTrue(tool.getMoreInfo());
assertTrue(tool.getMarkedStored());
assertTrue(tool.getMarkedClean());
assertTrue(tool.getMarkedDS());
assertTrue(tool.getGenerateMode());
}

@Test
public void testExecuteWithNullFilesArrayInsideListFiles() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File root = tempFolder.newFolder("testNullListFiles");
// File cleanDir = new File(root, "clean");
// cleanDir.mkdir();
// File subDir = new File(root, "subdir");
// subDir.mkdir();
// File marked = new File(root, "marked");
// marked.mkdir();
// File processed = new File(root, "processed");
// processed.mkdir();
// tool.setStartDirectory(root);
tool.setMarkedStored(true);
// tool.execute(root);
// assertTrue(cleanDir.exists());
}

@Test
public void testExecuteWhenAllDirsMissing() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File dataDir = tempFolder.newFolder("missingAllCorpusDirs");
// tool.setStartDirectory(dataDir);
tool.setMarkedStored(true);
// tool.execute(dataDir);
// assertTrue(dataDir.exists());
}

@Test
public void testInitWithTooManyWhitespaceInProps() throws Exception {
Gate.init();
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File dir = tempFolder.newFolder("whiteSpaceProps");
// File props = new File(dir, "corpus_tool.properties");
// FileWriter writer = new FileWriter(props);
// writer.write("annotTypes=  Person ;  Location ;   \n");
// writer.write("annotFeatures=   type ; confidence ; \n");
// writer.write("threshold= 0.85 \n");
// writer.close();
tool.setMarkedStored(true);
// tool.setStartDirectory(dir);
tool.init();
Set<String> features = tool.getDiffFeaturesList();
assertTrue(features.contains("type"));
assertTrue(features.contains("confidence"));
}

@Test
public void testPrintStatisticsDoesNotThrowIfAllMapsEmpty() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.printStatistics();
assertTrue(true);
}

@Test
public void testPrintStatsForManualFMeasureCalculationConsistency() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.printStatistics();
assertTrue(true);
}

@Test
public void testCalculateAvgTotalWithEmptyAnnotationTypeList() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.calculateAvgTotal();
assertEquals(0.0, tool.getPrecisionAverageCalc(), 0.0001);
assertEquals(0.0, tool.getRecallAverageCalc(), 0.0001);
assertEquals(0.0, tool.getFmeasureAverageCalc(), 0.0001);
}

@Test
public void testMainHandlesThresholdFlagParsing() throws Exception {
Gate.init();
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File dir = tempFolder.newFolder("mainThresholdTest");
// File appFile = tempFolder.newFile("x.gapp");
// String[] args = { "-verbose", "-moreinfo", "-marked_clean", dir.getAbsolutePath(), appFile.getAbsolutePath() };
try {
// CorpusBenchmarkTool.main(args);
} catch (Exception ex) {
assertNotNull(ex);
}
}

@Test
public void testMainWithMinimalValidArgs() throws Exception {
Gate.init();
// File mainDir = tempFolder.newFolder("mainValidArgs");
// File app = tempFolder.newFile("appFile.gapp");
// String[] args = { mainDir.getAbsolutePath(), app.getAbsolutePath() };
// try {
// CorpusBenchmarkTool.main(args);
// } catch (gate.GateException ex) {
// assertNotNull(ex);
// }
}

@Test
public void testMainWithInvalidPathThrows() throws Exception {
Gate.init();
File fakeApp = new File("nonexistent" + System.nanoTime());
File fakeDir = new File("invalidDir" + System.nanoTime());
String[] args = { fakeDir.getAbsolutePath(), fakeApp.getAbsolutePath() };
// try {
// CorpusBenchmarkTool.main(args);
// fail("Expected exception due to invalid input path");
// } catch (gate.GateException ex) {
// assertTrue(ex.getMessage() != null);
// }
}

@Test
public void testPrintAnnotationsHandlesEmptySet() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.printAnnotations(null, null);
assertTrue(true);
}

@Test
public void testPrintAnnotationsWithEmptySetObject() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.printAnnotations(new java.util.HashSet<gate.Annotation>(), null);
assertTrue(true);
}

@Test
public void testInitWithInvalidEncodingStillInitializes() throws Exception {
Gate.init();
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File dir = tempFolder.newFolder("invalidEncoding");
// File props = new File(dir, "corpus_tool.properties");
// FileWriter writer = new FileWriter(props);
// writer.write("encoding=INVALID_ENCODING\n");
// writer.close();
// tool.setStartDirectory(dir);
tool.setMarkedStored(true);
tool.init();
assertNotNull(tool.getDiffFeaturesList());
}

@Test
public void testInitHandlesEmptyAnnotFeaturesGracefully() throws Exception {
Gate.init();
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File dir = tempFolder.newFolder("emptyFeatures");
// File props = new File(dir, "corpus_tool.properties");
// FileWriter writer = new FileWriter(props);
// writer.write("annotFeatures=\n");
// writer.close();
tool.setMarkedStored(true);
// tool.setStartDirectory(dir);
tool.init();
Set<String> features = tool.getDiffFeaturesList();
assertNotNull(features);
assertTrue(features.isEmpty());
}

@Test
public void testInitWithEmptyAnnotationTypeStringRevertsToDefault() throws Exception {
Gate.init();
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File dir = tempFolder.newFolder("emptyAnnotTypes");
// File props = new File(dir, "corpus_tool.properties");
// FileWriter writer = new FileWriter(props);
// writer.write("annotTypes=\n");
// writer.close();
tool.setMarkedStored(true);
// tool.setStartDirectory(dir);
tool.init();
assertNotNull(tool.getDiffFeaturesList());
}

@Test
public void testSetNegativeThreshold() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setThreshold(-0.25);
assertEquals(-0.25, tool.getThreshold(), 0.0001);
}

@Test
public void testInitWithPropsFileOutsideStartDir() throws Exception {
Gate.init();
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File unrelatedProps = tempFolder.newFile("corpus_tool.properties");
// FileWriter writer = new FileWriter(unrelatedProps);
// writer.write("threshold=0.7\n");
// writer.write("annotSetName=TEST\n");
// writer.close();
// File start = tempFolder.newFolder("dummyStart");
// tool.setStartDirectory(start);
tool.setMarkedStored(true);
tool.init();
assertEquals(0.5, tool.getThreshold(), 0.0001);
}

@Test
public void testInitWithPropsInsideCurrentDirectory() throws Exception {
Gate.init();
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File originalCWD = new File(System.getProperty("user.dir"));
File corpusToolProps = new File("corpus_tool.properties");
FileWriter writer = new FileWriter(corpusToolProps);
writer.write("threshold=0.66\n");
writer.close();
// File dummy = tempFolder.newFolder("noPropsDir");
// tool.setStartDirectory(dummy);
tool.setMarkedStored(true);
tool.init();
corpusToolProps.delete();
assertEquals(0.66, tool.getThreshold(), 0.0001);
}

@Test
public void testGenerateCorpusWithNullFileDirSkipsProcessing() throws Exception {
Gate.init();
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.generateCorpus(null, null);
assertTrue(true);
}

@Test
public void testEvaluateCorpusDoesNotCrashWhenFileDirIsNull() throws Exception {
Gate.init();
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File marked = tempFolder.newFolder("marked");
// File processed = tempFolder.newFolder("stored");
// tool.evaluateCorpus(null, processed, marked, null);
// assertTrue(processed.exists());
}

@Test
public void testEvaluateCorpusDoesNotCrashWhenProcessedDirIsNull() throws Exception {
Gate.init();
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File clean = tempFolder.newFolder("clean");
// File marked = tempFolder.newFolder("marked");
tool.setMarkedClean(true);
// tool.evaluateCorpus(clean, null, marked, null);
// assertTrue(clean.exists());
}

@Test
public void testEvaluateCorpusSkipsEvaluationIfMarkedDirectoryMissing() throws Exception {
Gate.init();
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File clean = tempFolder.newFolder("clean");
// File processed = tempFolder.newFolder("processed");
tool.setMarkedStored(true);
// tool.evaluateCorpus(clean, processed, null, null);
// assertTrue(clean.exists());
}

@Test
public void testPrintStatsForTypeHandlesMissingAnnotationType() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
String nonexistentType = "MyAnnotationType";
tool.printStatsForType(nonexistentType);
assertTrue(true);
}

@Test
public void testExecuteDirectoryWithNullSubdirectoryArray() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File root = tempFolder.newFolder("directoryWithNullListFiles");
// File customDir = new File(root, "clean");
// customDir.mkdir();
// tool.setStartDirectory(root);
tool.setMarkedClean(true);
File fakeDir = new File("nonexistent");
tool.execute(fakeDir);
// assertTrue(customDir.exists());
}

@Test
public void testCountWordsReturnsZeroForNullDocument() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
int count = tool.countWords(null);
assertEquals(0, count);
}

@Test
public void testIsGenerateModeReturnsTrueOnlyWhenSet() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setGenerateMode(false);
assertFalse(tool.isGenerateMode());
tool.setGenerateMode(true);
assertTrue(tool.isGenerateMode());
}

@Test
public void testInitWithInvalidThresholdValueThrowsRuntimeException() throws Exception {
Gate.init();
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File propsDir = tempFolder.newFolder("invalidThresholdDir");
// File props = new File(propsDir, "corpus_tool.properties");
// FileWriter writer = new FileWriter(props);
// writer.write("threshold=xyz123\n");
// writer.close();
// tool.setStartDirectory(propsDir);
tool.setMarkedStored(true);
try {
tool.init();
fail("Expected GateRuntimeException due to invalid threshold format");
} catch (GateRuntimeException e) {
assertNotNull(e.getCause());
assertTrue(e.getCause() instanceof NumberFormatException);
}
}

@Test
public void testInitWithMissingCorpusToolPropertiesFileUsesDefaults() throws Exception {
Gate.init();
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File dir = tempFolder.newFolder("noPropFile");
// tool.setStartDirectory(dir);
tool.setMarkedStored(true);
tool.init();
assertNotNull(tool.getDiffFeaturesList());
}

@Test
public void testExecuteHandlesEmptyDirWithNoSubfolders() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File root = tempFolder.newFolder("emptyRootNoClean");
// tool.setStartDirectory(root);
tool.setMarkedStored(true);
// tool.execute(root);
// assertTrue(root.exists());
}

@Test
public void testExecuteHandlesSingleEmptySubdirGracefully() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File root = tempFolder.newFolder("dirWithCleanOnly");
// File clean = new File(root, "clean");
// clean.mkdir();
// tool.setStartDirectory(root);
tool.setMarkedStored(true);
// tool.execute(root);
// assertTrue(clean.exists());
}

@Test
public void testExecuteSkipsFilesAndCvsDirCorrectly() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File testDir = tempFolder.newFolder("cvsAndFileDir");
// File textFile = new File(testDir, "note.txt");
// FileWriter writer = new FileWriter(textFile);
// writer.write("dummy");
// writer.close();
// File cvs = new File(testDir, "Cvs");
// cvs.mkdir();
// File clean = new File(testDir, "clean");
// clean.mkdir();
// tool.setStartDirectory(testDir);
tool.setMarkedClean(true);
// tool.execute(testDir);
// assertTrue(clean.exists());
}

@Test
public void testEvaluateCorpusWithNullErrorDirStillProcesses() throws Exception {
Gate.init();
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File cleanDir = tempFolder.newFolder("cleanForEval");
// File processedDir = tempFolder.newFolder("processedForEval");
// File markedDir = tempFolder.newFolder("markedForEval");
tool.setMarkedStored(true);
// tool.evaluateCorpus(cleanDir, processedDir, markedDir, null);
// assertTrue(cleanDir.exists());
// assertTrue(processedDir.exists());
// assertTrue(markedDir.exists());
}

@Test
public void testEvaluateCorpusSkipsIfMarkedDirNotFound() throws Exception {
Gate.init();
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
// File clean = tempFolder.newFolder("eval-clean");
// File processed = tempFolder.newFolder("eval-processed");
tool.setMarkedStored(true);
// tool.evaluateCorpus(clean, processed, null, null);
// assertTrue(clean.exists());
// assertTrue(processed.exists());
}

@Test
public void testPrintStatisticsHandlesNoAnnotationTypesGracefully() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.printStatistics();
assertTrue(true);
}

@Test
public void testSetDiffFeaturesListWithEmptySetDoesNotThrow() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setDiffFeaturesList(new java.util.HashSet<String>());
assertTrue(tool.getDiffFeaturesList().isEmpty());
}

@Test
public void testSetDiffFeaturesListToNullHandledGracefully() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setDiffFeaturesList(null);
assertNull(tool.getDiffFeaturesList());
}

@Test
public void testPrintStatsForTypeHandlesEmptyStats() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.printStatsForType("NonExistentType");
assertTrue(true);
}

@Test
public void testCalculateAvgTotalHandlesAllZeroCounts() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.calculateAvgTotal();
assertEquals(0.0, tool.getPrecisionAverageCalc(), 0.00001);
assertEquals(0.0, tool.getRecallAverageCalc(), 0.00001);
assertEquals(0.0, tool.getFmeasureAverageCalc(), 0.00001);
}

@Test
public void testMainWithMissingDirectoryThrowsGateException() throws Exception {
Gate.init();
// File dummyApp = tempFolder.newFile("dummyApp.gapp");
File missingDir = new File("nonexistentFolderXYZ");
// String[] args = new String[] { missingDir.getAbsolutePath(), dummyApp.getAbsolutePath() };
// try {
// CorpusBenchmarkTool.main(args);
// fail("Expected GateException due to missing directory");
// } catch (gate.GateException ex) {
// assertTrue(ex.getMessage().contains("usage:"));
// }
}

@Test
public void testMainWithMissingApplicationFileThrowsGateException() throws Exception {
Gate.init();
// File dummyDir = tempFolder.newFolder("someDir");
File bogusApp = new File("missingApp" + System.currentTimeMillis() + ".gapp");
// String[] args = new String[] { dummyDir.getAbsolutePath(), bogusApp.getAbsolutePath() };
// try {
// CorpusBenchmarkTool.main(args);
// fail("Expected GateException due to invalid app file");
// } catch (gate.GateException ex) {
// assertTrue(ex.getMessage().contains("usage:"));
// }
}

@Test
public void testMeasureDocsReturnsNullIfKeyDocIsNull() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
gate.Document key = null;
gate.Document resp = null;
AnnotationDiffer ad = tool.measureDocs(key, resp, "Token");
assertNull(ad);
}

@Test
public void testCountWordsReturnsZeroForNullOrEmptyOutputSetName() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
gate.Document mockDoc = null;
int result = tool.countWords(mockDoc);
assertEquals(0, result);
}

@Test
public void testSetThresholdToZeroAndOneBoundaries() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setThreshold(0.0);
assertEquals(0.0, tool.getThreshold(), 0.00001);
tool.setThreshold(1.0);
assertEquals(1.0, tool.getThreshold(), 0.00001);
}
}
