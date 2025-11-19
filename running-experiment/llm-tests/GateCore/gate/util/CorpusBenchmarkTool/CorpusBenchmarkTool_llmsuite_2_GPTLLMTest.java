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

public class CorpusBenchmarkTool_llmsuite_2_GPTLLMTest {

@Test(expected = GateRuntimeException.class)
public void testInitPRsThrowsExceptionWhenNoAppFile() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.initPRs();
}

@Test
public void testSetAndGetGenerateModeTrue() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setGenerateMode(true);
assertTrue(tool.getGenerateMode());
}

@Test
public void testSetAndGetGenerateModeFalse() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setGenerateMode(false);
assertFalse(tool.getGenerateMode());
}

@Test
public void testSetAndGetVerboseModeTrue() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setVerboseMode(true);
assertTrue(tool.getVerboseMode());
}

@Test
public void testSetAndGetVerboseModeFalse() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setVerboseMode(false);
assertFalse(tool.getVerboseMode());
}

@Test
public void testSetAndGetMoreInfoTrue() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setMoreInfo(true);
assertTrue(tool.getMoreInfo());
}

@Test
public void testSetAndGetMoreInfoFalse() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setMoreInfo(false);
assertFalse(tool.getMoreInfo());
}

@Test
public void testSetAndGetThreshold() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setThreshold(0.75);
assertEquals(0.75, tool.getThreshold(), 0.00001);
}

@Test
public void testSetAndGetMarkedStored() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setMarkedStored(true);
assertTrue(tool.getMarkedStored());
}

@Test
public void testSetAndGetMarkedClean() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setMarkedClean(true);
assertTrue(tool.getMarkedClean());
}

@Test
public void testSetAndGetMarkedDS() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setMarkedDS(true);
assertTrue(tool.getMarkedDS());
}

@Test
public void testSetAndGetDiffFeaturesList() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Set<String> features = new HashSet<String>();
features.add("gender");
features.add("number");
tool.setDiffFeaturesList(features);
assertTrue(tool.getDiffFeaturesList().contains("gender"));
assertTrue(tool.getDiffFeaturesList().contains("number"));
assertEquals(2, tool.getDiffFeaturesList().size());
}

@Test
public void testSetAndGetStartDirectory() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File dir = new File(System.getProperty("java.io.tmpdir"), "corpus-tool-start");
dir.mkdirs();
tool.setStartDirectory(dir);
File result = tool.getStartDirectory();
assertEquals(dir, result);
result.delete();
}

@Test
public void testInitLoadDefaultPropertiesWhenPropFileDoesNotExist() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File startDir = new File(System.getProperty("java.io.tmpdir"), "start_" + System.nanoTime());
startDir.mkdir();
tool.setStartDirectory(startDir);
tool.setMarkedStored(true);
tool.init();
Set<String> features = tool.getDiffFeaturesList();
assertNotNull(features);
startDir.delete();
}

@Test
public void testInitLoadsPropertiesFromFile() throws Exception {
Gate.init();
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File startDir = new File(System.getProperty("java.io.tmpdir"), "start_" + System.nanoTime());
startDir.mkdir();
File propFile = new File(startDir, "corpus_tool.properties");
FileWriter fw = new FileWriter(propFile);
fw.write("threshold=0.75\n");
fw.write("annotSetName=MyKey\n");
fw.write("outputSetName=MyOutput\n");
fw.write("encoding=UTF-8\n");
fw.write("annotTypes=Person;Location\n");
fw.write("annotFeatures=number;gender\n");
fw.close();
tool.setStartDirectory(startDir);
tool.setMarkedStored(true);
tool.init();
assertEquals(0.75, tool.getThreshold(), 0.0001);
Set<String> featureList = tool.getDiffFeaturesList();
assertTrue(featureList.contains("number"));
assertTrue(featureList.contains("gender"));
propFile.delete();
startDir.delete();
}

@Test
public void testExecuteWithNullDirectoryDoesNotFail() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.execute((File) null);
}

@Test
public void testDefaultAveragePrecisionRecallAndFMeasureAreZero() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
double precision = tool.getPrecisionAverage();
double recall = tool.getRecallAverage();
double f1 = tool.getFMeasureAverage();
assertEquals(0.0, precision, 0.00001);
assertEquals(0.0, recall, 0.00001);
assertEquals(0.0, f1, 0.00001);
}

@Test(expected = GateException.class)
public void testMainThrowsExceptionOnMissingArgs() throws Exception {
String[] args = {};
CorpusBenchmarkTool.main(args);
}

@Test
public void testMainWithValidMinimalArgs() throws Exception {
Gate.init();
File dir = new File(System.getProperty("java.io.tmpdir"), "corpus_dir_" + System.nanoTime());
dir.mkdir();
File app = File.createTempFile("app", ".gapp");
FileWriter appWriter = new FileWriter(app);
appWriter.write("dummy");
appWriter.close();
String[] args = new String[] { "-generate", dir.getAbsolutePath(), app.getAbsolutePath() };
try {
CorpusBenchmarkTool.main(args);
} catch (SecurityException se) {
}
app.delete();
dir.delete();
}

@Test
public void testInitWithEmptyPropertiesFile() throws Exception {
Gate.init();
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File tmpDir = new File(System.getProperty("java.io.tmpdir"), "corpus_init_empty_" + System.nanoTime());
tmpDir.mkdir();
File propFile = new File(tmpDir, "corpus_tool.properties");
FileWriter writer = new FileWriter(propFile);
writer.write("");
writer.close();
tool.setStartDirectory(tmpDir);
tool.setMarkedStored(true);
tool.init();
assertEquals(0.5, tool.getThreshold(), 0.00001);
propFile.delete();
tmpDir.delete();
}

@Test
public void testInitWithMalformedThresholdProperty() throws Exception {
Gate.init();
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File tmpDir = new File(System.getProperty("java.io.tmpdir"), "corpus_init_threshold_" + System.nanoTime());
tmpDir.mkdir();
File propFile = new File(tmpDir, "corpus_tool.properties");
FileWriter writer = new FileWriter(propFile);
writer.write("threshold=abc\n");
writer.close();
tool.setStartDirectory(tmpDir);
tool.setMarkedStored(true);
try {
tool.init();
} catch (Exception e) {
assertTrue(e instanceof NumberFormatException || e.getCause() instanceof NumberFormatException);
}
propFile.delete();
tmpDir.delete();
}

@Test
public void testSetDiffFeaturesListWithEmptySet() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Set<String> emptySet = Collections.emptySet();
tool.setDiffFeaturesList(emptySet);
assertTrue(tool.getDiffFeaturesList().isEmpty());
}

@Test
public void testSetDiffFeaturesListWithNullLikeStrings() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Set<String> set = new HashSet<String>();
set.add("");
set.add(" ");
set.add("null");
tool.setDiffFeaturesList(set);
assertTrue(tool.getDiffFeaturesList().contains("null"));
assertTrue(tool.getDiffFeaturesList().contains(""));
}

@Test
public void testApplicationFileSetAndGetNull() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setApplicationFile(null);
}

@Test
public void testExecuteHandlesDirectoryWithoutSubfolders() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File dir = new File(System.getProperty("java.io.tmpdir"), "corpus_emptyDir_" + System.nanoTime());
dir.mkdir();
tool.execute(dir);
dir.delete();
}

@Test
public void testPrecisionRecallF1WhenNoAnnotationsPresent() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
double precision = tool.getPrecisionAverage();
double recall = tool.getRecallAverage();
double fMeasure = tool.getFMeasureAverage();
assertEquals(0.0, precision, 0.00001);
assertEquals(0.0, recall, 0.00001);
assertEquals(0.0, fMeasure, 0.00001);
}

@Test
public void testMarkModesDefaultsToFalse() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
assertFalse(tool.getMarkedClean());
assertFalse(tool.getMarkedStored());
assertFalse(tool.getMarkedDS());
}

@Test
public void testRunCalculateAvgTotalWithZeroAnnotations() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.calculateAvgTotal();
assertEquals(0.0, tool.getPrecisionAverageCalc(), 0.00001);
assertEquals(0.0, tool.getRecallAverageCalc(), 0.00001);
assertEquals(0.0, tool.getFmeasureAverageCalc(), 0.00001);
}

@Test
public void testAvgPrintHandlesZeroCorrectly() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
String result = tool.avgPrint(0.0, 3);
assertEquals("0.0", result);
}

@Test
public void testAvgPrintHandlesRounding() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
String value = tool.avgPrint(0.87654321, 2);
assertEquals("0.88", value);
}

@Test
public void testSetStartDirectoryNull() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setStartDirectory(null);
assertNull(tool.getStartDirectory());
}

@Test
public void testSetOutputSetNameToNull_UsedInternallyByReflection() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
assertNull(tool.getDiffFeaturesList().contains("OutputSetName"));
}

@Test
public void testInitWithPartiallyEmptyPropertiesValues() throws Exception {
Gate.init();
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File tempDir = new File(System.getProperty("java.io.tmpdir"), "testEmptyValues" + System.nanoTime());
tempDir.mkdir();
File propFile = new File(tempDir, "corpus_tool.properties");
FileWriter writer = new FileWriter(propFile);
writer.write("threshold=\n");
writer.write("encoding=\n");
writer.write("annotTypes=\n");
writer.write("annotFeatures=\n");
writer.close();
tool.setStartDirectory(tempDir);
tool.setMarkedStored(true);
tool.init();
assertEquals(0.5, tool.getThreshold(), 0.0001);
propFile.delete();
tempDir.delete();
}

@Test
public void testInitWithOnlyAnnotTypesProperty() throws Exception {
Gate.init();
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File tempDir = new File(System.getProperty("java.io.tmpdir"), "testOnlyType" + System.nanoTime());
tempDir.mkdir();
File propFile = new File(tempDir, "corpus_tool.properties");
FileWriter writer = new FileWriter(propFile);
writer.write("annotTypes=Date;Amount\n");
writer.close();
tool.setStartDirectory(tempDir);
tool.setMarkedStored(true);
tool.init();
assertEquals(0.5, tool.getThreshold(), 0.0001);
File delete = new File(tempDir, "corpus_tool.properties");
delete.delete();
tempDir.delete();
}

@Test
public void testInitFallsBackToDefaultsWhenFileNotPresentAnywhere() throws Exception {
Gate.init();
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File missingDir = new File(System.getProperty("java.io.tmpdir"), "missing_" + System.nanoTime());
missingDir.mkdir();
tool.setStartDirectory(missingDir);
tool.setMarkedStored(true);
tool.init();
assertNotNull(tool.getDiffFeaturesList());
assertEquals(0.5, tool.getThreshold(), 0.0001);
missingDir.delete();
}

@Test
public void testEmptyAnnotationFeaturesDoNotThrow() throws Exception {
Gate.init();
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File tempDir = new File(System.getProperty("java.io.tmpdir"), "emptyfeatures_" + System.nanoTime());
tempDir.mkdir();
File propFile = new File(tempDir, "corpus_tool.properties");
FileWriter writer = new FileWriter(propFile);
writer.write("annotFeatures=\n");
writer.close();
tool.setStartDirectory(tempDir);
tool.setMarkedStored(true);
tool.init();
Set<String> features = tool.getDiffFeaturesList();
assertTrue(features.isEmpty());
propFile.delete();
tempDir.delete();
}

@Test
public void testInitWithPropertiesFileContainingOnlyUnknownKeys() throws Exception {
Gate.init();
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File tempDir = new File(System.getProperty("java.io.tmpdir"), "unknownProps" + System.nanoTime());
tempDir.mkdir();
File propFile = new File(tempDir, "corpus_tool.properties");
FileWriter writer = new FileWriter(propFile);
writer.write("foobar=123\n");
writer.write("baz=456\n");
writer.close();
tool.setStartDirectory(tempDir);
tool.setMarkedStored(true);
tool.init();
assertEquals(0.5, tool.getThreshold(), 0.0001);
propFile.delete();
tempDir.delete();
}

@Test
public void testMultipleModeFlagsSetTogether() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setGenerateMode(true);
tool.setMarkedStored(true);
tool.setMarkedClean(true);
tool.setMarkedDS(true);
tool.setVerboseMode(true);
tool.setMoreInfo(true);
assertTrue(tool.getGenerateMode());
assertTrue(tool.getMarkedStored());
assertTrue(tool.getMarkedClean());
assertTrue(tool.getMarkedDS());
assertTrue(tool.getVerboseMode());
assertTrue(tool.getMoreInfo());
}

@Test
public void testSetNegativeThreshold() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setThreshold(-1.0);
assertEquals(-1.0, tool.getThreshold(), 0.0001);
}

@Test(expected = GateException.class)
public void testMainWithSingleDirectoryArgShouldThrow() throws Exception {
Gate.init();
File dir = new File(System.getProperty("java.io.tmpdir"), "argsSingle" + System.nanoTime());
dir.mkdir();
String[] args = new String[] { dir.getAbsolutePath() };
CorpusBenchmarkTool.main(args);
}

@Test(expected = GateException.class)
public void testMainWithNonexistentFileAsApp() throws Exception {
Gate.init();
File dir = new File(System.getProperty("java.io.tmpdir"), "argsDirOk" + System.nanoTime());
dir.mkdir();
String[] args = new String[] { dir.getAbsolutePath(), new File("NON_EXISTENT_APP_FILE.gapp").getAbsolutePath() };
CorpusBenchmarkTool.main(args);
}

@Test
public void testSetThresholdVeryHighValue() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setThreshold(5000.123);
assertEquals(5000.123, tool.getThreshold(), 0.0001);
}

@Test
public void testSetThresholdVerySmallValue() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setThreshold(0.0000001);
assertEquals(0.0000001, tool.getThreshold(), 0.0);
}

@Test
public void testInterpretEmptyDiffFeaturesAsEmptySet() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Set<String> set = new HashSet<String>();
tool.setDiffFeaturesList(set);
assertTrue(tool.getDiffFeaturesList().isEmpty());
}

@Test
public void testSetStartDirectoryToNonExistingPath() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File file = new File(System.getProperty("java.io.tmpdir"), "nonexisting/subdir");
tool.setStartDirectory(file);
assertEquals(file, tool.getStartDirectory());
}

@Test
public void testExecuteSkipsNonDirectoryChildren() throws Exception {
Gate.init();
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File rootDir = new File(System.getProperty("java.io.tmpdir"), "exec_flat_" + System.nanoTime());
rootDir.mkdir();
File aFile = new File(rootDir, "someFile.txt");
aFile.createNewFile();
File cvsDir = new File(rootDir, "Cvs");
cvsDir.mkdir();
tool.setStartDirectory(rootDir);
tool.setMarkedStored(true);
tool.execute(rootDir);
aFile.delete();
cvsDir.delete();
rootDir.delete();
}

@Test
public void testExecuteWithNullListFilesSafeguard() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File dir = new File("does_not_exist_" + System.nanoTime());
tool.execute(dir);
}

@Test
public void testCountWordsReturnsZeroWhenTokensNull() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
int result = tool.countWords(null);
assertEquals(0, result);
}

@Test
public void testPrintTableHeaderProducesOutput() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setVerboseMode(true);
tool.setMoreInfo(true);
tool.printTableHeader();
}

@Test
public void testEvaluateCorpusIgnoresWhenCleanDirMissing() throws Exception {
Gate.init();
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File rootDir = new File(System.getProperty("java.io.tmpdir"), "no_clean_" + System.nanoTime());
rootDir.mkdir();
File processedDir = new File(rootDir, "processed");
processedDir.mkdir();
tool.setStartDirectory(rootDir);
tool.setMarkedStored(true);
tool.execute(rootDir);
processedDir.delete();
rootDir.delete();
}

@Test
public void testThresholdUsedWhenVerboseModeEnabled() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File propsDirectory = new File(System.getProperty("java.io.tmpdir"), "propsVerbose_" + System.nanoTime());
propsDirectory.mkdir();
File propFile = new File(propsDirectory, "corpus_tool.properties");
FileWriter writer = new FileWriter(propFile);
writer.write("threshold=0.33\n");
writer.write("verbose=true\n");
writer.close();
tool.setStartDirectory(propsDirectory);
tool.setMarkedStored(true);
tool.init();
assertEquals(0.33, tool.getThreshold(), 0.0001);
propFile.delete();
propsDirectory.delete();
}

@Test
public void testErrorDuringInitDueToUnreadablePropertiesFile() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File propsDirectory = new File(System.getProperty("java.io.tmpdir"), "not_read_" + System.nanoTime());
propsDirectory.mkdir();
File propFile = new File(propsDirectory, "corpus_tool.properties");
FileWriter writer = new FileWriter(propFile);
writer.write("annotSetName=Key\n");
writer.close();
propFile.setReadable(false);
tool.setStartDirectory(propsDirectory);
tool.setMarkedStored(true);
try {
tool.init();
fail("Expected exception due to unreadable properties file");
} catch (RuntimeException ex) {
assertTrue(ex.getMessage().contains("Error loading"));
}
propFile.setReadable(true);
propFile.delete();
propsDirectory.delete();
}

@Test
public void testSetStartDirectoryAfterInit() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File dir = new File(System.getProperty("java.io.tmpdir"), "afterInit_" + System.nanoTime());
dir.mkdir();
tool.setStartDirectory(dir);
assertEquals(dir, tool.getStartDirectory());
dir.delete();
}

@Test
public void testMainWithUnknownSwitchArgumentsIsIgnored() throws Exception {
Gate.init();
File dummyDir = new File(System.getProperty("java.io.tmpdir"), "corpus_main_args_" + System.nanoTime());
dummyDir.mkdir();
File dummyApp = File.createTempFile("app", ".gapp");
FileWriter w = new FileWriter(dummyApp);
w.write("dummy content");
w.close();
String[] args = new String[] { "-unknownFlag", dummyDir.getAbsolutePath(), dummyApp.getAbsolutePath() };
try {
CorpusBenchmarkTool.main(args);
} catch (SecurityException ex) {
}
dummyApp.delete();
dummyDir.delete();
}

@Test
public void testExecuteSkipsWhenNullReturnedFromListFiles() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File fakeDir = new File("nonexistent_folder_" + System.nanoTime());
tool.execute(fakeDir);
}

@Test
public void testApplicationFileSetToNonReadableFile() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File f = File.createTempFile("inaccessible", ".tmp");
f.setReadable(false);
tool.setApplicationFile(f);
try {
tool.initPRs();
fail("Expected exception due to unreadable file");
} catch (RuntimeException ex) {
assertTrue(ex.getMessage().contains("Corpus Benchmark Tool"));
} finally {
f.setReadable(true);
f.delete();
}
}

@Test
public void testApplicationFileIsDirectory() throws Exception {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File dir = new File(System.getProperty("java.io.tmpdir"), "appfileIsDir_" + System.nanoTime());
dir.mkdir();
tool.setApplicationFile(dir);
try {
tool.initPRs();
fail("Expected exception due to applicationFile being a directory");
} catch (RuntimeException ex) {
assertTrue(ex.getMessage().contains("Corpus Benchmark Tool"));
} finally {
dir.delete();
}
}

@Test
public void testSetDiffFeaturesListWithDuplicates() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Set<String> features = new HashSet<String>();
features.add("class");
features.add("class");
features.add("category");
tool.setDiffFeaturesList(features);
assertTrue(tool.getDiffFeaturesList().contains("class"));
assertTrue(tool.getDiffFeaturesList().contains("category"));
assertEquals(2, tool.getDiffFeaturesList().size());
}

@Test
public void testInitWithWhitespaceOnlyPropertiesValues() throws Exception {
Gate.init();
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File dir = new File(System.getProperty("java.io.tmpdir"), "testWhiteSpaceProp" + System.nanoTime());
dir.mkdir();
File propFile = new File(dir, "corpus_tool.properties");
FileWriter writer = new FileWriter(propFile);
writer.write("threshold=   \n");
writer.write("annotSetName=  \n");
writer.write("annotTypes= \n");
writer.write("annotFeatures= ; ;\n");
writer.close();
tool.setStartDirectory(dir);
tool.setMarkedStored(true);
tool.init();
assertNotNull(tool.getDiffFeaturesList());
assertFalse(tool.getDiffFeaturesList().contains(null));
propFile.delete();
dir.delete();
}

@Test
public void testSetAndGetStartDirectoryToNull() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setStartDirectory(null);
assertNull(tool.getStartDirectory());
}

@Test
public void testSetDiffFeaturesListToNullReference() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setDiffFeaturesList(null);
assertNull(tool.getDiffFeaturesList());
}

@Test
public void testSetMarkedStoredRepeatedly() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setMarkedStored(false);
tool.setMarkedStored(false);
assertFalse(tool.getMarkedStored());
tool.setMarkedStored(true);
assertTrue(tool.getMarkedStored());
}

@Test
public void testSetMarkedCleanRepeatedly() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setMarkedClean(true);
tool.setMarkedClean(true);
assertTrue(tool.getMarkedClean());
tool.setMarkedClean(false);
assertFalse(tool.getMarkedClean());
}

@Test
public void testSetAndReadEmptyFeatureSet() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Set<String> features = new HashSet<String>();
tool.setDiffFeaturesList(features);
Set<String> returned = tool.getDiffFeaturesList();
assertTrue(returned != null && returned.isEmpty());
}

@Test
public void testSetApplicationToDirectoryInsteadOfFile_Throws() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File dir = new File(System.getProperty("java.io.tmpdir"), "appAsDir" + System.nanoTime());
dir.mkdir();
tool.setApplicationFile(dir);
try {
tool.initPRs();
fail("Expected GateRuntimeException due to directory instead of .gapp file");
} catch (GateRuntimeException e) {
assertTrue(e.getMessage().contains("Corpus Benchmark Tool"));
}
dir.delete();
}

@Test
public void testInitPRsWithMissingFile_Throws() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File invalidFile = new File(System.getProperty("java.io.tmpdir"), "missingFile" + System.nanoTime() + ".gapp");
tool.setApplicationFile(invalidFile);
try {
tool.initPRs();
fail("Expected GateRuntimeException due to missing file");
} catch (GateRuntimeException e) {
assertTrue(e.getMessage().contains("Corpus Benchmark Tool"));
}
}

@Test
public void testInitLoadsDefaultTypesIfannotTypesMissingFromPropsFile() throws Exception {
Gate.init();
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File testDir = new File(System.getProperty("java.io.tmpdir"), "missingAnnotTypes" + System.nanoTime());
testDir.mkdir();
File propFile = new File(testDir, "corpus_tool.properties");
FileWriter fw = new FileWriter(propFile);
fw.write("threshold=0.7\n");
fw.close();
tool.setStartDirectory(testDir);
tool.setMarkedStored(true);
tool.init();
propFile.delete();
testDir.delete();
}

@Test
public void testSetVeryHighThreshold() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setThreshold(99999.999);
assertEquals(99999.999, tool.getThreshold(), 0.000001);
}

@Test
public void testSetVeryNegativeThreshold() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setThreshold(-999.555);
assertEquals(-999.555, tool.getThreshold(), 0.000001);
}

@Test
public void testGetPrecisionRecallF1AverageProcBeforeUpdateReturnsZero() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
assertEquals(0.0, tool.getPrecisionAverageProc(), 0.0001);
assertEquals(0.0, tool.getRecallAverageProc(), 0.0001);
assertEquals(0.0, tool.getFMeasureAverageProc(), 0.0001);
}

@Test
public void testSetAndUnsetVerboseMode() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setVerboseMode(true);
assertTrue(tool.getVerboseMode());
tool.setVerboseMode(false);
assertFalse(tool.getVerboseMode());
}

@Test
public void testMultipleFlagsFlippedTogether() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setMarkedClean(true);
tool.setMarkedStored(false);
tool.setMarkedDS(true);
tool.setMoreInfo(true);
tool.setVerboseMode(false);
assertTrue(tool.getMarkedClean());
assertFalse(tool.getMarkedStored());
assertTrue(tool.getMarkedDS());
assertTrue(tool.getMoreInfo());
assertFalse(tool.getVerboseMode());
}

@Test
public void testMainWithUnrecognizedFlagFollowedByValidArgs() throws Exception {
Gate.init();
File dir = new File(System.getProperty("java.io.tmpdir"), "main_args" + System.nanoTime());
dir.mkdir();
File appFile = File.createTempFile("dummy_app", ".gapp");
FileWriter fw = new FileWriter(appFile);
fw.write("dummy content");
fw.close();
String[] args = new String[] { "-unexpected", dir.getAbsolutePath(), appFile.getAbsolutePath() };
try {
CorpusBenchmarkTool.main(args);
} catch (SecurityException e) {
}
appFile.delete();
dir.delete();
}

@Test
public void testExecuteGeneratesCorpusWhenGenerateModeEnabled() throws Exception {
Gate.init();
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File rootDir = new File(System.getProperty("java.io.tmpdir"), "test_generate_" + System.nanoTime());
rootDir.mkdir();
File clean = new File(rootDir, "clean");
clean.mkdir();
File doc = new File(clean, "sample.txt");
FileWriter w = new FileWriter(doc);
w.write("Simple text.");
w.close();
File dummyApp = File.createTempFile("dummy", ".gapp");
FileWriter fw = new FileWriter(dummyApp);
fw.write("content");
fw.close();
tool.setStartDirectory(rootDir);
tool.setApplicationFile(dummyApp);
tool.setGenerateMode(true);
try {
tool.execute();
} catch (Exception e) {
assertTrue(e.getMessage().contains("CorpusBenchmark"));
}
dummyApp.delete();
doc.delete();
clean.delete();
rootDir.delete();
}

@Test
public void testPrintStatisticsWithoutAnnotationTypesDoesNotFail() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.printStatistics();
}

@Test
public void testPrintStatsForAnnotationTypeWithZeroCounts() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
String type = "CustomType";
tool.printStatsForType(type);
}

@Test
public void testCalculateAvgTotalWithAnnotationTypeButEmptyCounts() throws Exception {
Gate.init();
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Set<String> features = new HashSet<String>();
tool.setDiffFeaturesList(features);
File dir = new File(System.getProperty("java.io.tmpdir"), "propsEmptyCounts_" + System.nanoTime());
dir.mkdir();
File propFile = new File(dir, "corpus_tool.properties");
FileWriter fw = new FileWriter(propFile);
fw.write("annotTypes=Entity\n");
fw.close();
tool.setStartDirectory(dir);
tool.setMarkedStored(true);
tool.init();
tool.calculateAvgTotal();
assertEquals(0.0, tool.getPrecisionAverageCalc(), 0.0001);
assertEquals(0.0, tool.getRecallAverageCalc(), 0.0001);
assertEquals(0.0, tool.getFmeasureAverageCalc(), 0.0001);
propFile.delete();
dir.delete();
}

@Test
public void testIsGenerateModeMethodConsistentWithSetter() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setGenerateMode(true);
assertTrue(tool.isGenerateMode());
tool.setGenerateMode(false);
assertFalse(tool.isGenerateMode());
}

@Test
public void testSetDiffFeaturesWithSpecialCharacters() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
Set<String> features = new HashSet<String>();
features.add("@type");
features.add("$$");
features.add("class");
tool.setDiffFeaturesList(features);
assertTrue(tool.getDiffFeaturesList().contains("class"));
assertTrue(tool.getDiffFeaturesList().contains("@type"));
assertTrue(tool.getDiffFeaturesList().contains("$$"));
}

@Test
public void testSetAndGetUnusualEncodingString() throws IOException {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File propsDir = new File(System.getProperty("java.io.tmpdir"), "encodingTest" + System.nanoTime());
propsDir.mkdir();
File propFile = new File(propsDir, "corpus_tool.properties");
FileWriter writer = new FileWriter(propFile);
writer.write("encoding=🎉UTF8🎉\n");
writer.close();
tool.setStartDirectory(propsDir);
tool.setMarkedStored(true);
tool.init();
propFile.delete();
propsDir.delete();
}

@Test
public void testMultiplePropertyValuesParsedCorrectly() throws Exception {
Gate.init();
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File dir = new File(System.getProperty("java.io.tmpdir"), "multiValues_" + System.nanoTime());
dir.mkdir();
File props = new File(dir, "corpus_tool.properties");
FileWriter fw = new FileWriter(props);
fw.write("annotTypes=Type1;Type2;Type3\n");
fw.write("annotFeatures=feat1;feat2;feat3\n");
fw.close();
tool.setStartDirectory(dir);
tool.setMarkedStored(true);
tool.init();
Set<String> features = tool.getDiffFeaturesList();
assertTrue(features.contains("feat1"));
assertTrue(features.contains("feat2"));
assertTrue(features.contains("feat3"));
props.delete();
dir.delete();
}

@Test
public void testPrintAnnotationsWithEmptyAnnotationSetDoesNotThrow() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.printAnnotations((Set) null, null);
}

@Test
public void testSetStartDirectoryTwiceReplacesValue() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File dir1 = new File(System.getProperty("java.io.tmpdir"), "start1_" + System.nanoTime());
File dir2 = new File(System.getProperty("java.io.tmpdir"), "start2_" + (System.nanoTime() + 1));
dir1.mkdir();
dir2.mkdir();
tool.setStartDirectory(dir1);
assertEquals(dir1, tool.getStartDirectory());
tool.setStartDirectory(dir2);
assertEquals(dir2, tool.getStartDirectory());
dir1.delete();
dir2.delete();
}

@Test
public void testInitWithNullEncodingAndThresholdStillUsesDefaults() throws Exception {
Gate.init();
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File dir = new File(System.getProperty("java.io.tmpdir"), "nullEncodingProps_" + System.nanoTime());
dir.mkdir();
File propFile = new File(dir, "corpus_tool.properties");
FileWriter fw = new FileWriter(propFile);
fw.write("encoding=\n");
fw.write("threshold=\n");
fw.close();
tool.setStartDirectory(dir);
tool.setMarkedStored(true);
tool.init();
assertEquals(0.5, tool.getThreshold(), 0.0001);
propFile.delete();
dir.delete();
}

@Test
public void testInitWithPropertiesHavingTrailingSemicolonsParsesCorrectly() throws Exception {
Gate.init();
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File dir = new File(System.getProperty("java.io.tmpdir"), "trailingSemicolonProps_" + System.nanoTime());
dir.mkdir();
File propFile = new File(dir, "corpus_tool.properties");
FileWriter fw = new FileWriter(propFile);
fw.write("annotTypes=Person;Location;\n");
fw.write("annotFeatures=type;color;\n");
fw.close();
tool.setStartDirectory(dir);
tool.setMarkedStored(true);
tool.init();
assertEquals(0.5, tool.getThreshold(), 0.0001);
assertTrue(tool.getDiffFeaturesList().contains("type"));
assertTrue(tool.getDiffFeaturesList().contains("color"));
propFile.delete();
dir.delete();
}

@Test
public void testDoubleInitializationDoesNotThrow() throws Exception {
Gate.init();
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File tempDir = new File(System.getProperty("java.io.tmpdir"), "initTwice_" + System.nanoTime());
tempDir.mkdir();
File propFile = new File(tempDir, "corpus_tool.properties");
FileWriter fw = new FileWriter(propFile);
fw.write("threshold=0.7\nannotSetName=Test\n");
fw.close();
tool.setStartDirectory(tempDir);
tool.setMarkedStored(true);
tool.init();
tool.init();
propFile.delete();
tempDir.delete();
}

@Test
public void testNullApplicationFileTriggersExceptionInInitPRs() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
try {
tool.initPRs();
fail("Expected exception due to null application file");
} catch (GateRuntimeException e) {
assertTrue(e.getMessage().contains("Application not set"));
}
}

@Test
public void testMainFailsGracefullyWithOneValidParameterOnly() throws Exception {
Gate.init();
File dir = new File(System.getProperty("java.io.tmpdir"), "onlyDirArg_" + System.nanoTime());
dir.mkdir();
String[] args = { dir.getAbsolutePath() };
try {
CorpusBenchmarkTool.main(args);
fail("Expected GateException due to missing application argument");
} catch (GateException e) {
assertTrue(e.getMessage().contains("usage:"));
}
dir.delete();
}

@Test
public void testSetApplicationFileToNonexistentFileCausesInitError() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File file = new File(System.getProperty("java.io.tmpdir") + "/nonexistent_" + System.nanoTime() + ".gapp");
tool.setApplicationFile(file);
try {
tool.initPRs();
fail("Expected GateRuntimeException for missing file");
} catch (GateRuntimeException e) {
assertTrue(e.getMessage().toLowerCase().contains("corpus benchmark tool"));
}
}

@Test
public void testSetEncodingViaPropertiesOverridesDefault() throws Exception {
Gate.init();
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
File dir = new File(System.getProperty("java.io.tmpdir"), "setEncodingTest_" + System.nanoTime());
dir.mkdir();
File propFile = new File(dir, "corpus_tool.properties");
FileWriter fw = new FileWriter(propFile);
fw.write("encoding=UTF-16\n");
fw.close();
tool.setStartDirectory(dir);
tool.setMarkedStored(true);
tool.init();
propFile.delete();
dir.delete();
}

@Test
public void testSettingNullStartDirectoryAndReadingDoesNotThrow() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setStartDirectory(null);
assertNull(tool.getStartDirectory());
}

@Test
public void testNoAnnotationTypesPrintStatisticsDoesNotCrash() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.printStatistics();
}

@Test
public void testSetThresholdToMinimumDoubleAcceptsValue() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setThreshold(Double.MIN_VALUE);
assertEquals(Double.MIN_VALUE, tool.getThreshold(), 0.0);
}

@Test
public void testSetDiffFeaturesContainsDuplicateFeatures() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
HashSet<String> features = new HashSet<String>();
features.add("name");
features.add("name");
tool.setDiffFeaturesList(features);
assertEquals(1, tool.getDiffFeaturesList().size());
assertTrue(tool.getDiffFeaturesList().contains("name"));
}

@Test
public void testMarkedCleanEvaluationFlagSwitching() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setMarkedClean(true);
assertTrue(tool.getMarkedClean());
tool.setMarkedClean(false);
assertFalse(tool.getMarkedClean());
}

@Test
public void testRecallAndPrecisionAverageReturnZeroWhenNoDocsProcessed() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
assertEquals(0.0, tool.getPrecisionAverage(), 0.00001);
assertEquals(0.0, tool.getRecallAverage(), 0.00001);
assertEquals(0.0, tool.getFMeasureAverage(), 0.00001);
}

@Test
public void testPrecisionRecallF1ProcDefaultZeroBeforeEvaluation() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
assertEquals(0.0, tool.getPrecisionAverageProc(), 0.00001);
assertEquals(0.0, tool.getRecallAverageProc(), 0.00001);
assertEquals(0.0, tool.getFMeasureAverageProc(), 0.00001);
}

@Test
public void testSetThresholdWithLargeDoubleValueAccepted() {
CorpusBenchmarkTool tool = new CorpusBenchmarkTool();
tool.setThreshold(987654321.123);
assertEquals(987654321.123, tool.getThreshold(), 0.0001);
}
}
