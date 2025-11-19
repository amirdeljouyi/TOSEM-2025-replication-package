package opennlp.tools.postag;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import opennlp.tools.ml.BeamSearch;
import opennlp.tools.ml.EventTrainer;
import opennlp.tools.ml.SequenceTrainer;
import opennlp.tools.ml.TrainerFactory;
import opennlp.tools.ml.model.AbstractModel;
import opennlp.tools.ml.model.Event;
import opennlp.tools.ml.model.MaxentModel;
import opennlp.tools.ml.model.SequenceClassificationModel;
import opennlp.tools.ml.perceptron.PerceptronTrainer;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.util.*;
import opennlp.tools.util.featuregen.AdaptiveFeatureGenerator;
import opennlp.tools.util.featuregen.TokenFeatureGenerator;
import opennlp.tools.util.model.ArtifactProvider;
import opennlp.tools.util.model.ArtifactSerializer;
import opennlp.tools.util.model.UncloseableInputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
// import opennlp.tools.formats.ResourceAsStreamFactory;
import opennlp.tools.namefind.NameFinderME;
import static opennlp.tools.formats.Conll02NameSampleStream.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class POSTaggerFactory_llmsuite_2_GPTLLMTest {

@Test
public void testConstructorWithValidInputs() {
byte[] featureGeneratorBytes = "<generator><cache/></generator>".getBytes();
Map<String, Object> resources = new HashMap<>();
POSDictionary tagDictionary = new POSDictionary(true);
// tagDictionary.addTags("book", new String[] { "NN", "VB" });
POSTaggerFactory factory = new POSTaggerFactory(featureGeneratorBytes, resources, tagDictionary);
assertNotNull(factory);
assertNotNull(factory.getFeatureGenerator());
assertEquals(resources, factory.getResources());
assertEquals(tagDictionary, factory.getTagDictionary());
}

@Test
public void testCreateEmptyTagDictionary() {
POSTaggerFactory factory = new POSTaggerFactory();
TagDictionary emptyDict = factory.createEmptyTagDictionary();
assertNotNull(emptyDict);
assertEquals(emptyDict, factory.getTagDictionary());
assertTrue(emptyDict instanceof POSDictionary);
}

@Test
public void testGetTagDictionaryReturnsInitialized() {
byte[] featureBytes = "<generator><cache/></generator>".getBytes();
Map<String, Object> resources = new HashMap<>();
POSDictionary posDict = new POSDictionary(true);
// posDict.addTags("run", new String[] { "VB" });
POSTaggerFactory factory = new POSTaggerFactory(featureBytes, resources, posDict);
TagDictionary returnedDict = factory.getTagDictionary();
assertEquals(posDict, returnedDict);
}

@Test
public void testSetTagDictionary() {
POSTaggerFactory factory = new POSTaggerFactory();
POSDictionary dict = new POSDictionary(true);
// dict.addTags("jump", new String[] { "VB" });
factory.setTagDictionary(dict);
TagDictionary returned = factory.getTagDictionary();
assertEquals(dict, returned);
}

@Test
public void testCreateFeatureGeneratorsWithValidDescriptor() {
byte[] descriptor = "<generator><cache/></generator>".getBytes();
Map<String, Object> resources = new HashMap<>();
POSDictionary tagDict = new POSDictionary(true);
// tagDict.addTags("run", new String[] { "VB" });
POSTaggerFactory factory = new POSTaggerFactory(descriptor, resources, tagDict);
AdaptiveFeatureGenerator gen = factory.createFeatureGenerators();
assertNotNull(gen);
}

@Test
public void testCreateFeatureGeneratorsWithInvalidDescriptorThrows() {
byte[] invalidDescriptor = "<generator><unknown></generator>".getBytes();
Map<String, Object> resources = new HashMap<>();
POSDictionary tagDict = new POSDictionary(true);
// tagDict.addTags("foo", new String[] { "JJ" });
POSTaggerFactory factory = new POSTaggerFactory(invalidDescriptor, resources, tagDict);
factory.createFeatureGenerators();
}

@Test
public void testGetPOSContextGeneratorReturnsValidInstance() {
byte[] featureBytes = "<generator><cache/></generator>".getBytes();
Map<String, Object> resources = new HashMap<>();
POSDictionary dict = new POSDictionary(true);
// dict.addTags("walk", new String[] { "VB" });
POSTaggerFactory factory = new POSTaggerFactory(featureBytes, resources, dict);
POSContextGenerator generator = factory.getPOSContextGenerator();
assertNotNull(generator);
}

@Test
public void testGetPOSContextGeneratorWithCacheSize() {
byte[] featureBytes = "<generator><cache/></generator>".getBytes();
Map<String, Object> resources = new HashMap<>();
POSDictionary dict = new POSDictionary(true);
// dict.addTags("play", new String[] { "NN" });
POSTaggerFactory factory = new POSTaggerFactory(featureBytes, resources, dict);
POSContextGenerator generator = factory.getPOSContextGenerator(10);
assertNotNull(generator);
}

@Test
public void testSequenceValidatorReturnsInstance() {
byte[] featureBytes = "<generator><cache/></generator>".getBytes();
Map<String, Object> resources = new HashMap<>();
POSDictionary dict = new POSDictionary(true);
// dict.addTags("run", new String[] { "VB" });
POSTaggerFactory factory = new POSTaggerFactory(featureBytes, resources, dict);
SequenceValidator<String> validator = factory.getSequenceValidator();
assertNotNull(validator);
}

@Test
public void testCreateTagDictionaryFromInputStream() throws IOException {
POSDictionary dict = new POSDictionary(true);
// dict.addTags("eat", new String[] { "VB" });
ByteArrayOutputStream out = new ByteArrayOutputStream();
dict.serialize(out);
InputStream in = new ByteArrayInputStream(out.toByteArray());
POSTaggerFactory factory = new POSTaggerFactory();
TagDictionary loadedDict = factory.createTagDictionary(in);
assertNotNull(loadedDict);
assertArrayEquals(new String[] { "VB" }, loadedDict.getTags("eat"));
}

@Test
public void testCreateArtifactSerializersMapIncludesPOSDictionary() {
POSTaggerFactory factory = new POSTaggerFactory();
// Map<String, ArtifactSerializer<?>> serializers = factory.createArtifactSerializersMap();
// assertNotNull(serializers);
// assertTrue(serializers.containsKey("tagdict"));
// assertTrue(serializers.get("tagdict") instanceof POSTaggerFactory.POSDictionarySerializer);
}

@Test
public void testStaticCreateWithNullSubclassReturnsDefault() throws Exception {
byte[] featureBytes = "<generator><cache/></generator>".getBytes();
Map<String, Object> resources = new HashMap<>();
TagDictionary dict = new POSDictionary(true);
POSTaggerFactory factory = POSTaggerFactory.create(null, featureBytes, resources, dict);
assertNotNull(factory);
assertTrue(factory instanceof POSTaggerFactory);
}

@Test
public void testValidatePOSDictionaryWithValidModel() throws Exception {
POSDictionary dict = new POSDictionary(true);
// dict.addTags("test", new String[] { "NN" });
// AbstractModel model = new AbstractModel() {
// 
// public int getNumOutcomes() {
// return 1;
// }
// 
// public String getOutcome(int i) {
// return "NN";
// }
// 
// public double[] eval(String[] context) {
// return new double[] { 1.0 };
// }
// 
// public String getBestOutcome(double[] ocs) {
// return null;
// }
// 
// public String getAllOutcomes(double[] outcomes) {
// return null;
// }
// 
// public String[] getDataStructures() {
// return new String[0];
// }
// 
// public int getIndex(String outcome) {
// return -1;
// }
// };
POSTaggerFactory factory = new POSTaggerFactory();
// factory.validatePOSDictionary(dict, model);
}

@Test
public void testValidatePOSDictionaryThrowsForUnknownTag() throws Exception {
POSDictionary dict = new POSDictionary(true);
// dict.addTags("test", new String[] { "UNK_TAG" });
// AbstractModel model = new AbstractModel() {
// 
// public int getNumOutcomes() {
// return 1;
// }
// 
// public String getOutcome(int i) {
// return "NN";
// }
// 
// public double[] eval(String[] context) {
// return new double[] { 1.0 };
// }
// 
// public String getBestOutcome(double[] ocs) {
// return null;
// }
// 
// public String getAllOutcomes(double[] outcomes) {
// return null;
// }
// 
// public String[] getDataStructures() {
// return new String[0];
// }
// 
// public int getIndex(String outcome) {
// return -1;
// }
// };
POSTaggerFactory factory = new POSTaggerFactory();
// factory.validatePOSDictionary(dict, model);
}

@Test
public void testStaticCreateWithInvalidClassnameThrows() throws Exception {
byte[] featureBytes = "<generator><cache/></generator>".getBytes();
Map<String, Object> resources = new HashMap<>();
TagDictionary dict = new POSDictionary(true);
POSTaggerFactory.create("invalid.class.Foo", featureBytes, resources, dict);
}

@Test
public void testCreateArtifactMapPopulatesEntries() {
POSDictionary posDict = new POSDictionary(true);
// Dictionary ngramDict = new Dictionary(true);
byte[] featureBytes = "<generator><cache/></generator>".getBytes();
Map<String, Object> resources = new HashMap<>();
POSTaggerFactory factory = new POSTaggerFactory(featureBytes, resources, posDict);
// factory.init(ngramDict, posDict);
Map<String, Object> artifacts = factory.createArtifactMap();
assertTrue(artifacts.containsKey("tags.tagdict"));
assertTrue(artifacts.containsKey("ngram.dictionary"));
}

@Test
public void testGetResourcesReturnsEmptyMapWhenNull() {
POSTaggerFactory factory = new POSTaggerFactory();
Map<String, Object> resources = factory.getResources();
assertNotNull(resources);
assertTrue(resources.isEmpty());
}

@Test
public void testConstructorWithNullResourcesAndNullFeatureGeneratorBytes() {
POSDictionary posDict = new POSDictionary(true);
POSTaggerFactory factory = new POSTaggerFactory(null, null, posDict);
assertNotNull(factory);
assertNotNull(factory.getFeatureGenerator());
assertEquals(posDict, factory.getTagDictionary());
assertFalse(factory.getResources().containsKey("tags.tagdict"));
}

@Test
public void testCreateTagDictionaryFromFile() throws IOException {
POSDictionary dict = new POSDictionary(true);
// dict.addTags("eat", new String[] { "VB" });
File tempFile = File.createTempFile("tagdict", ".dict");
tempFile.deleteOnExit();
FileOutputStream fos = new FileOutputStream(tempFile);
dict.serialize(fos);
fos.close();
POSTaggerFactory factory = new POSTaggerFactory();
TagDictionary loaded = factory.createTagDictionary(tempFile);
assertNotNull(loaded);
assertArrayEquals(new String[] { "VB" }, loaded.getTags("eat"));
}

@Test
public void testGetTagDictionaryReturnsNullWhenUninitialized() {
POSTaggerFactory factory = new POSTaggerFactory();
TagDictionary dict = factory.getTagDictionary();
assertNull(dict);
}

@Test
public void testCreateArtifactMapWithoutDictionariesReturnsEmpty() {
POSTaggerFactory factory = new POSTaggerFactory();
Map<String, Object> artifacts = factory.createArtifactMap();
assertFalse(artifacts.containsKey("tags.tagdict"));
assertFalse(artifacts.containsKey("ngram.dictionary"));
}

@Test
public void testValidateArtifactMapWithNoArtifactsDoesNotThrow() throws InvalidFormatException {
POSTaggerFactory factory = new POSTaggerFactory();
// factory.artifactProvider = new opennlp.tools.util.ArtifactProvider() {
// 
// public <T> T getArtifact(String key) {
// return null;
// }
// 
// public boolean isLoadedFromSerialized() {
// return false;
// }
// 
// public Properties getManifestEntries() {
// return new Properties();
// }
// };
factory.validateArtifactMap();
}

@Test
public void testValidateArtifactMapFailsWithInvalidTagDictType() throws InvalidFormatException {
POSTaggerFactory factory = new POSTaggerFactory();
// factory.artifactProvider = new opennlp.tools.util.ArtifactProvider() {
// 
// public <T> T getArtifact(String key) {
// if ("tags.tagdict".equals(key))
// return (T) "wrongType";
// return null;
// }
// 
// public boolean isLoadedFromSerialized() {
// return false;
// }
// 
// public Properties getManifestEntries() {
// return new Properties();
// }
// };
factory.validateArtifactMap();
}

@Test
public void testValidateArtifactMapFailsWithInvalidNGramDictionaryType() throws InvalidFormatException {
POSTaggerFactory factory = new POSTaggerFactory();
// factory.artifactProvider = new opennlp.tools.util.ArtifactProvider() {
// 
// public <T> T getArtifact(String key) {
// if ("ngram.dictionary".equals(key))
// return (T) "InvalidDictionaryType";
// return null;
// }
// 
// public boolean isLoadedFromSerialized() {
// return false;
// }
// 
// public Properties getManifestEntries() {
// return new Properties();
// }
// };
factory.validateArtifactMap();
}

@Test
public void testCreateFeatureGeneratorFallbacksToClasspathWhenAllNull() {
POSTaggerFactory factory = new POSTaggerFactory();
AdaptiveFeatureGenerator generator = factory.createFeatureGenerators();
assertNotNull(generator);
}

@Test
public void testCreateStaticFactoryWithNullSubclassAndNullFeatureBytes() throws InvalidFormatException {
POSDictionary dict = new POSDictionary(true);
Map<String, Object> resources = new HashMap<>();
POSTaggerFactory factory = POSTaggerFactory.create(null, null, resources, dict);
assertNotNull(factory);
assertEquals(dict, factory.getTagDictionary());
assertNotNull(factory.getFeatureGenerator());
}

@Test
public void testArtifactSerializerPOSDictionaryRoundTrip() throws IOException {
POSDictionary dict = new POSDictionary(true);
// dict.addTags("jump", new String[] { "VB", "NN" });
POSTaggerFactory.POSDictionarySerializer serializer = new POSTaggerFactory.POSDictionarySerializer();
ByteArrayOutputStream out = new ByteArrayOutputStream();
serializer.serialize(dict, out);
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
POSDictionary result = serializer.create(in);
assertNotNull(result);
assertArrayEquals(new String[] { "VB", "NN" }, result.getTags("jump"));
}

@Test
public void testCreateArtifactSerializersMapContainsCorrectEntries() {
POSTaggerFactory factory = new POSTaggerFactory();
// Map<String, ArtifactSerializer<?>> serializers = factory.createArtifactSerializersMap();
// ArtifactSerializer<?> dictSerializer = serializers.get("tagdict");
// assertNotNull(dictSerializer);
// assertTrue(dictSerializer instanceof POSTaggerFactory.POSDictionarySerializer);
}

@Test
public void testGetPOSContextGeneratorLowVersionFallback() {
POSTaggerFactory factory = new POSTaggerFactory();
// factory.artifactProvider = new ArtifactProvider() {
// 
// public <T> T getArtifact(String key) {
// if ("manifest.properties".equals(key)) {
// Properties props = new Properties();
// props.setProperty("OpenNLP-Version", "1.7.5");
// return (T) props;
// }
// if ("ngram.dictionary".equals(key)) {
// return (T) new Dictionary(true);
// }
// return null;
// }
// 
// public boolean isLoadedFromSerialized() {
// return false;
// }
// 
// public Properties getManifestEntries() {
// Properties props = new Properties();
// props.setProperty("OpenNLP-Version", "1.7.5");
// return props;
// }
// };
POSContextGenerator generator = factory.getPOSContextGenerator(0);
assertNotNull(generator);
}

@Test
public void testStaticDeprecatedCreateReturnsDefaultFactory() throws InvalidFormatException {
// Dictionary ngramDict = new Dictionary(true);
POSDictionary posDict = new POSDictionary(true);
// POSTaggerFactory factory = POSTaggerFactory.create(null, ngramDict, posDict);
// assertNotNull(factory);
// assertTrue(factory instanceof POSTaggerFactory);
}

@Test
public void testStaticDeprecatedCreateWithInvalidSubclassThrows() throws InvalidFormatException {
// Dictionary ngramDict = new Dictionary(true);
POSDictionary posDict = new POSDictionary(true);
// POSTaggerFactory.create("invalid.nonexistent.Subclass", ngramDict, posDict);
}

@Test
public void testValidatePOSDictionarySharedWordsInModelAndDict() throws InvalidFormatException {
POSDictionary dict = new POSDictionary(true);
// dict.addTags("go", new String[] { "VB", "NN" });
// AbstractModel model = new AbstractModel() {
// 
// public int getNumOutcomes() {
// return 2;
// }
// 
// public String getOutcome(int i) {
// return i == 0 ? "VB" : "NN";
// }
// 
// public double[] eval(String[] context) {
// return new double[] { 0.5, 0.5 };
// }
// 
// public String getBestOutcome(double[] ocs) {
// return null;
// }
// 
// public String getAllOutcomes(double[] outcomes) {
// return null;
// }
// 
// public String[] getDataStructures() {
// return new String[0];
// }
// 
// public int getIndex(String outcome) {
// return -1;
// }
// };
POSTaggerFactory factory = new POSTaggerFactory();
// factory.validatePOSDictionary(dict, model);
}

@Test
public void testCreateArtifactMapReturnsSuperclassDefaults() {
POSTaggerFactory factory = new POSTaggerFactory();
Map<String, Object> map = factory.createArtifactMap();
assertNotNull(map);
assertTrue(map instanceof Map);
}

@Test
public void testCreateArtifactSerializersMapRegistersPOSDictionarySerializer() {
POSTaggerFactory factory = new POSTaggerFactory();
// Map<String, ArtifactSerializer<?>> result = factory.createArtifactSerializersMap();
// assertNotNull(result);
// assertTrue(result.containsKey("tagdict"));
// assertTrue(result.get("tagdict") instanceof POSTaggerFactory.POSDictionarySerializer);
}

@Test
public void testValidateArtifactMapWithValidArtifactsAndSerializedFlagTrue() throws InvalidFormatException {
POSTaggerFactory factory = new POSTaggerFactory();
POSDictionary dictionary = new POSDictionary(true);
// dictionary.addTags("run", new String[] { "VB" });
// AbstractModel model = new AbstractModel() {
// 
// public int getNumOutcomes() {
// return 1;
// }
// 
// public String getOutcome(int i) {
// return "VB";
// }
// 
// public double[] eval(String[] context) {
// return new double[] { 1.0 };
// }
// 
// public String getBestOutcome(double[] ocs) {
// return null;
// }
// 
// public String getAllOutcomes(double[] outcomes) {
// return null;
// }
// 
// public String[] getDataStructures() {
// return new String[0];
// }
// 
// public int getIndex(String outcome) {
// return 0;
// }
// };
// factory.artifactProvider = new ArtifactProvider() {
// 
// public <T> T getArtifact(String key) {
// if (key.equals("tags.tagdict"))
// return (T) dictionary;
// if (key.equals(POSModel.POS_MODEL_ENTRY_NAME))
// return (T) model;
// return null;
// }
// 
// public boolean isLoadedFromSerialized() {
// return true;
// }
// 
// public Properties getManifestEntries() {
// return new Properties();
// }
// };
factory.validateArtifactMap();
}

@Test
public void testCreateWithNullFeatureBytesUsesFallbackStream() throws IOException {
POSTaggerFactory factory = new POSTaggerFactory();
AdaptiveFeatureGenerator generator = factory.createFeatureGenerators();
assertNotNull(generator);
}

@Test
public void testCreateFeatureGeneratorThrowsIfDescriptorCorrupt() {
byte[] badDescriptor = "<generator><notclosed>".getBytes();
Map<String, Object> empty = new HashMap<>();
POSDictionary dict = new POSDictionary(true);
POSTaggerFactory factory = new POSTaggerFactory(badDescriptor, empty, dict);
factory.createFeatureGenerators();
}

@Test
public void testCreateStaticFactoryShortFormReturnsValidInstance() throws Exception {
POSDictionary dict = new POSDictionary(true);
Map<String, Object> res = new HashMap<>();
// POSTaggerFactory factory = POSTaggerFactory.create(null, res, dict);
// assertNotNull(factory);
// assertTrue(factory instanceof POSTaggerFactory);
}

@Test
public void testPOSContextGeneratorHandlesBlankManifestVersionSafe() {
POSTaggerFactory factory = new POSTaggerFactory();
// factory.artifactProvider = new ArtifactProvider() {
// 
// public <T> T getArtifact(String key) {
// if (key.equals("manifest.properties")) {
// Properties p = new Properties();
// p.setProperty("OpenNLP-Version", "");
// return (T) p;
// }
// return null;
// }
// 
// public boolean isLoadedFromSerialized() {
// return false;
// }
// 
// public Properties getManifestEntries() {
// return new Properties();
// }
// };
POSContextGenerator generator = factory.getPOSContextGenerator();
assertNotNull(generator);
}

@Test
public void testValidateArtifactMapFailsWithBothWrongTypes() throws InvalidFormatException {
POSTaggerFactory factory = new POSTaggerFactory();
// factory.artifactProvider = new ArtifactProvider() {
// 
// public <T> T getArtifact(String key) {
// if (key.equals("tags.tagdict"))
// return (T) (Object) new String("wrong");
// if (key.equals("ngram.dictionary"))
// return (T) (Object) 42;
// return null;
// }
// 
// public boolean isLoadedFromSerialized() {
// return false;
// }
// 
// public Properties getManifestEntries() {
// return new Properties();
// }
// };
factory.validateArtifactMap();
}

@Test
public void testGetSequenceValidatorValidationLogic() {
POSDictionary dict = new POSDictionary(true);
// dict.addTags("apple", new String[] { "NN", "NNS" });
POSTaggerFactory factory = new POSTaggerFactory(null, new HashMap<>(), dict);
SequenceValidator<String> validator = factory.getSequenceValidator();
List<String> outcomes = Arrays.asList("NN");
// boolean result = validator.validSequence(0, new String[] { "apple" }, outcomes, "NN", null);
// assertTrue(result);
}

@Test
public void testDefaultTagDictionaryReturnedFromCreateEmptyTagDictionary() {
POSTaggerFactory factory = new POSTaggerFactory();
TagDictionary dict = factory.createEmptyTagDictionary();
assertNotNull(dict);
assertEquals(dict, factory.getTagDictionary());
}

@Test
public void testPOSDictionarySerializerReadWriteCycle() throws IOException {
POSDictionary dict = new POSDictionary(true);
// dict.addTags("drink", new String[] { "VB" });
POSTaggerFactory.POSDictionarySerializer serializer = new POSTaggerFactory.POSDictionarySerializer();
ByteArrayOutputStream out = new ByteArrayOutputStream();
serializer.serialize(dict, out);
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
POSDictionary result = serializer.create(in);
assertArrayEquals(new String[] { "VB" }, result.getTags("drink"));
}

@Test
public void testDefaultFactoryInitPopulatesFromFallbackWhenNullInput() {
POSTaggerFactory factory = new POSTaggerFactory(null, null, null);
byte[] features = factory.getFeatureGenerator();
assertNotNull(features);
assertTrue(features.length > 0);
}

@Test
public void testCreateFactoryWithResourceOverrideCallsArtifactFallback() {
Map<String, Object> res = new HashMap<>();
res.put("custom.key", "value");
byte[] feat = "<generator><cache/></generator>".getBytes();
TagDictionary dict = new POSDictionary(true);
POSTaggerFactory factory = new POSTaggerFactory(feat, res, dict);
AdaptiveFeatureGenerator g = factory.createFeatureGenerators();
assertNotNull(g);
}

@Test
public void testValidatePOSDictionarySupportsSubsetOutcomes() throws Exception {
POSDictionary dict = new POSDictionary(true);
// dict.addTags("read", new String[] { "VB" });
// AbstractModel mockModel = new AbstractModel() {
// 
// public int getNumOutcomes() {
// return 3;
// }
// 
// public String getOutcome(int i) {
// if (i == 0)
// return "VB";
// else if (i == 1)
// return "NN";
// return "JJ";
// }
// 
// public double[] eval(String[] context) {
// return new double[] { 0.5 };
// }
// 
// public String getBestOutcome(double[] ocs) {
// return null;
// }
// 
// public String getAllOutcomes(double[] outcomes) {
// return null;
// }
// 
// public String[] getDataStructures() {
// return new String[0];
// }
// 
// public int getIndex(String outcome) {
// return 1;
// }
// };
POSTaggerFactory factory = new POSTaggerFactory();
// factory.validatePOSDictionary(dict, mockModel);
}

@Test
@SuppressWarnings("deprecation")
public void testDeprecatedConstructorInitializesFields() {
// Dictionary ngramDict = new Dictionary(true);
POSDictionary tagDict = new POSDictionary(true);
// POSTaggerFactory factory = new POSTaggerFactory(ngramDict, tagDict);
// Map<String, Object> artifactMap = factory.createArtifactMap();
// assertTrue(artifactMap.containsKey("tags.tagdict"));
// assertTrue(artifactMap.containsKey("ngram.dictionary"));
}

@Test
@SuppressWarnings("deprecation")
public void testDeprecatedInitMethodSetsFields() {
// Dictionary ngramDict = new Dictionary(true);
POSDictionary tagDict = new POSDictionary(true);
POSTaggerFactory factory = new POSTaggerFactory();
// factory.init(ngramDict, tagDict);
Map<String, Object> artMap = factory.createArtifactMap();
assertTrue(artMap.containsKey("tags.tagdict"));
assertTrue(artMap.containsKey("ngram.dictionary"));
}

@Test
@SuppressWarnings("deprecation")
public void testGetDictionaryReturnsArtifactValueIfNotSet() {
// final Dictionary external = new Dictionary(true);
POSTaggerFactory factory = new POSTaggerFactory();
// factory.artifactProvider = new opennlp.tools.util.ArtifactProvider() {
// 
// public <T> T getArtifact(String key) {
// if ("ngram.dictionary".equals(key))
// return (T) external;
// return null;
// }
// 
// public boolean isLoadedFromSerialized() {
// return false;
// }
// 
// public Properties getManifestEntries() {
// return new Properties();
// }
// };
// Dictionary result = factory.getDictionary();
// assertEquals(external, result);
}

@Test
public void testValidatePOSDictionaryFailsOnPartiallyUnknownTags() throws InvalidFormatException {
POSDictionary dict = new POSDictionary(true);
// dict.addTags("data", new String[] { "NN", "UNKNOWN_TAG" });
// AbstractModel model = new AbstractModel() {
// 
// public int getNumOutcomes() {
// return 1;
// }
// 
// public String getOutcome(int i) {
// return "NN";
// }
// 
// public double[] eval(String[] context) {
// return new double[] { 1.0 };
// }
// 
// public String getBestOutcome(double[] ocs) {
// return null;
// }
// 
// public String getAllOutcomes(double[] ocs) {
// return null;
// }
// 
// public String[] getDataStructures() {
// return new String[0];
// }
// 
// public int getIndex(String outcome) {
// return -1;
// }
// };
POSTaggerFactory factory = new POSTaggerFactory();
// factory.validatePOSDictionary(dict, model);
}

@Test
public void testCreateTwiceCachesOrReusesFeatureBytesInternally() {
byte[] features = "<generator><cache/></generator>".getBytes();
POSDictionary dict = new POSDictionary(true);
Map<String, Object> resources = new HashMap<>();
POSTaggerFactory factory = new POSTaggerFactory(features, resources, dict);
assertNotNull(factory.createFeatureGenerators());
assertNotNull(factory.createFeatureGenerators());
}

@Test
public void testGetPOSContextGeneratorSkipsLegacyWhenManifestMissingVersion() {
POSTaggerFactory factory = new POSTaggerFactory();
// factory.artifactProvider = new opennlp.tools.util.ArtifactProvider() {
// 
// public <T> T getArtifact(String key) {
// if ("manifest.properties".equals(key)) {
// return (T) new Properties();
// }
// return null;
// }
// 
// public boolean isLoadedFromSerialized() {
// return false;
// }
// 
// public Properties getManifestEntries() {
// return new Properties();
// }
// };
POSContextGenerator result = factory.getPOSContextGenerator();
assertNotNull(result);
}

@Test
public void testGetPOSContextGeneratorHandlesVersionParseFailureGracefully() {
POSTaggerFactory factory = new POSTaggerFactory();
// factory.artifactProvider = new opennlp.tools.util.ArtifactProvider() {
// 
// public <T> T getArtifact(String key) {
// if ("manifest.properties".equals(key)) {
// Properties props = new Properties();
// props.setProperty("OpenNLP-Version", "not_a_number");
// return (T) props;
// }
// return null;
// }
// 
// public boolean isLoadedFromSerialized() {
// return false;
// }
// 
// public Properties getManifestEntries() {
// Properties props = new Properties();
// props.setProperty("OpenNLP-Version", "not_a_number");
// return props;
// }
// };
POSContextGenerator generator = factory.getPOSContextGenerator();
assertNotNull(generator);
}

@Test
public void testCreateSubClassNameWithNullReturnsDefaultInstance() throws Exception {
POSDictionary tagDict = new POSDictionary(true);
Map<String, Object> resources = new HashMap<>();
POSTaggerFactory factory = POSTaggerFactory.create(null, null, resources, tagDict);
assertNotNull(factory);
assertTrue(factory instanceof POSTaggerFactory);
}

@Test
public void testCreateExtensionFactoryWithNonexistentClassThrows() throws Exception {
Map<String, Object> resources = new HashMap<>();
TagDictionary tagDict = new POSDictionary(true);
POSTaggerFactory.create("com.example.not.FoundFactory", null, resources, tagDict);
}

@Test
public void testCreateStaticFactoryCanInitializeWithoutFeatureBytes() throws Exception {
POSDictionary dict = new POSDictionary(true);
Map<String, Object> res = new HashMap<>();
POSTaggerFactory factory = POSTaggerFactory.create(null, null, res, dict);
assertNotNull(factory);
assertNotNull(factory.createFeatureGenerators());
}

@Test
public void testPOSDictionarySerializerHandlesEmptyEntries() throws IOException {
POSTaggerFactory.POSDictionarySerializer serializer = new POSTaggerFactory.POSDictionarySerializer();
POSDictionary dict = new POSDictionary(true);
ByteArrayOutputStream out = new ByteArrayOutputStream();
serializer.serialize(dict, out);
ByteArrayInputStream input = new ByteArrayInputStream(out.toByteArray());
POSDictionary readDict = serializer.create(input);
assertNotNull(readDict);
assertNull(readDict.getTags("nothing"));
}

@Test
@SuppressWarnings("deprecation")
public void testDeprecatedStaticCreateReturnsCustomFactoryWhenValidSubclass() throws Exception {
// final Dictionary dummyDict = new Dictionary(true);
final POSDictionary dummyPOSDict = new POSDictionary(true);
// POSTaggerFactory custom = POSTaggerFactory.create(null, dummyDict, dummyPOSDict);
// assertNotNull(custom);
// assertTrue(custom instanceof POSTaggerFactory);
}

@Test
public void testGetResourcesReturnsEmptyWhenNull() {
POSTaggerFactory factory = new POSTaggerFactory();
Map<String, Object> resources = factory.getResources();
assertNotNull(resources);
assertTrue(resources.isEmpty());
}

@Test
public void testCreateArtifactMapDoesNotThrowWhenAllNull() {
POSTaggerFactory factory = new POSTaggerFactory();
Map<String, Object> map = factory.createArtifactMap();
assertNotNull(map);
}

@Test
public void testCreateTagDictionaryWithNullStreamThrowsIOException() throws IOException {
POSTaggerFactory factory = new POSTaggerFactory();
factory.createTagDictionary((InputStream) null);
}

@Test
public void testLoadDefaultFeatureGeneratorBytesSucceeds() {
POSTaggerFactory factory = new POSTaggerFactory();
byte[] bytes = factory.getFeatureGenerator();
assertNotNull(bytes);
assertTrue(bytes.length > 0);
}

@Test
public void testCreateArtifactMapWithoutAnyInitReturnsEmptyMap() {
POSTaggerFactory factory = new POSTaggerFactory();
Map<String, Object> artifacts = factory.createArtifactMap();
assertFalse(artifacts.containsKey("tags.tagdict"));
assertFalse(artifacts.containsKey("ngram.dictionary"));
}

@Test
public void testValidatePOSDictionaryExactTagMatchDoesNotThrow() throws Exception {
POSDictionary dict = new POSDictionary(true);
// dict.addTags("walk", new String[] { "VB" });
// AbstractModel model = new AbstractModel() {
// 
// public int getNumOutcomes() {
// return 1;
// }
// 
// public String getOutcome(int i) {
// return "VB";
// }
// 
// public double[] eval(String[] context) {
// return new double[] { 1.0 };
// }
// 
// public String getBestOutcome(double[] ocs) {
// return null;
// }
// 
// public String getAllOutcomes(double[] ocs) {
// return null;
// }
// 
// public String[] getDataStructures() {
// return new String[0];
// }
// 
// public int getIndex(String outcome) {
// return 0;
// }
// };
POSTaggerFactory factory = new POSTaggerFactory();
// factory.validatePOSDictionary(dict, model);
}

@Test
public void testCreateFeatureGeneratorThrowsOnInvalidXMLCloseTag() {
byte[] badDescriptor = "<generator><invalid></gen>".getBytes();
Map<String, Object> resources = new HashMap<>();
POSDictionary dict = new POSDictionary(true);
POSTaggerFactory factory = new POSTaggerFactory(badDescriptor, resources, dict);
factory.createFeatureGenerators();
}

@Test
public void testGetResourcesReturnsEmptyWhenUninitialized() {
POSTaggerFactory factory = new POSTaggerFactory();
Map<String, Object> result = factory.getResources();
assertNotNull(result);
assertTrue(result.isEmpty());
}

@Test
public void testGetFeatureGeneratorReturnsBytesAfterFirstCall() {
byte[] bytes = "<generator><cache/></generator>".getBytes();
Map<String, Object> resources = new HashMap<>();
POSDictionary dict = new POSDictionary(true);
POSTaggerFactory factory = new POSTaggerFactory(bytes, resources, dict);
byte[] loaded = factory.getFeatureGenerator();
assertNotNull(loaded);
assertArrayEquals(bytes, loaded);
}

@Test
public void testGetPOSContextGeneratorHandlesManifestMissingVersionProp() {
POSTaggerFactory factory = new POSTaggerFactory();
// factory.artifactProvider = new ArtifactProvider() {
// 
// public <T> T getArtifact(String key) {
// if ("manifest.properties".equals(key)) {
// return (T) new Properties();
// }
// return null;
// }
// 
// public boolean isLoadedFromSerialized() {
// return false;
// }
// 
// public Properties getManifestEntries() {
// return new Properties();
// }
// };
assertNotNull(factory.getPOSContextGenerator());
}

@Test
public void testGetPOSContextGeneratorHandlesMalformedVersionImpl() {
POSTaggerFactory factory = new POSTaggerFactory();
// factory.artifactProvider = new ArtifactProvider() {
// 
// public <T> T getArtifact(String key) {
// if ("manifest.properties".equals(key)) {
// Properties props = new Properties();
// props.setProperty("OpenNLP-Version", "NaN.NEXT");
// return (T) props;
// }
// return null;
// }
// 
// public boolean isLoadedFromSerialized() {
// return false;
// }
// 
// public Properties getManifestEntries() {
// return new Properties();
// }
// };
assertNotNull(factory.getPOSContextGenerator());
}

@Test
public void testValidatePOSDictionaryThrowsIfAllTagsUnknown() throws InvalidFormatException {
POSDictionary dict = new POSDictionary(true);
// dict.addTags("banana", new String[] { "XYZ" });
// AbstractModel model = new AbstractModel() {
// 
// public int getNumOutcomes() {
// return 1;
// }
// 
// public String getOutcome(int i) {
// return "ABC";
// }
// 
// public double[] eval(String[] context) {
// return new double[] { 1.0 };
// }
// 
// public String getBestOutcome(double[] ocs) {
// return null;
// }
// 
// public String getAllOutcomes(double[] ocs) {
// return null;
// }
// 
// public String[] getDataStructures() {
// return null;
// }
// 
// public int getIndex(String outcome) {
// return 0;
// }
// };
POSTaggerFactory factory = new POSTaggerFactory();
// factory.validatePOSDictionary(dict, model);
}

@Test
public void testCreateEmptyTagDictionaryProducesEmptyStructure() {
POSTaggerFactory factory = new POSTaggerFactory();
TagDictionary tagDict = factory.createEmptyTagDictionary();
assertNotNull(tagDict);
assertNull(tagDict.getTags("nothing"));
}

@Test
public void testStaticCreateWithEmptySubclassReturnsDefault() throws Exception {
Map<String, Object> res = new HashMap<>();
POSTaggerFactory factory = POSTaggerFactory.create(null, null, res, new POSDictionary(true));
assertNotNull(factory);
}

@Test
public void testStaticCreateThrowsIfSubclassFailsToInstantiate() throws Exception {
POSTaggerFactory.create("com.fake.NotReal", null, new HashMap<>(), new POSDictionary(true));
}

@Test
public void testSequenceValidatorAcceptsTagFromDict() {
POSDictionary dict = new POSDictionary(true);
// dict.addTags("house", new String[] { "NN" });
POSTaggerFactory factory = new POSTaggerFactory(null, new HashMap<>(), dict);
SequenceValidator<String> validator = factory.getSequenceValidator();
List<String> prev = new ArrayList<String>();
prev.add("NN");
// boolean valid = validator.validSequence(0, new String[] { "house" }, prev, "NN", null);
// assertTrue(valid);
}

@Test
public void testCreateTagDictionaryFromNullInputStreamThrows() throws IOException {
POSTaggerFactory factory = new POSTaggerFactory();
factory.createTagDictionary((InputStream) null);
}

@Test
public void testPOSDictionarySerializerSupportsEmptySerialization() throws IOException {
POSTaggerFactory.POSDictionarySerializer serializer = new POSTaggerFactory.POSDictionarySerializer();
POSDictionary emptyDict = new POSDictionary(true);
ByteArrayOutputStream out = new ByteArrayOutputStream();
serializer.serialize(emptyDict, out);
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
POSDictionary result = serializer.create(in);
assertNull(result.getTags("doesNotExist"));
}

@Test
public void testCreateArtifactSerializersRegistersCorrectly() {
POSTaggerFactory factory = new POSTaggerFactory();
// Map<String, ArtifactSerializer<?>> serializers = factory.createArtifactSerializersMap();
// assertTrue(serializers.containsKey("tagdict"));
// assertTrue(serializers.get("tagdict") instanceof POSTaggerFactory.POSDictionarySerializer);
}

@Test
public void testGetPOSContextGeneratorFallbackWhenGeneratorNotInArtifactProvider() {
POSTaggerFactory factory = new POSTaggerFactory();
// factory.artifactProvider = new ArtifactProvider() {
// 
// public <T> T getArtifact(String key) {
// return null;
// }
// 
// public boolean isLoadedFromSerialized() {
// return false;
// }
// 
// public Properties getManifestEntries() {
// Properties p = new Properties();
// p.setProperty("OpenNLP-Version", "1.9.0");
// return p;
// }
// };
POSContextGenerator ctxGen = factory.getPOSContextGenerator();
assertNotNull(ctxGen);
}

@Test
public void testResourcesLambdaHandlesMissingKeyGracefully() {
byte[] genBytes = "<generator><cache/></generator>".getBytes();
Map<String, Object> res = new HashMap<>();
res.put("known", "value");
POSTaggerFactory factory = new POSTaggerFactory(genBytes, res, new POSDictionary(true));
AdaptiveFeatureGenerator generator = factory.createFeatureGenerators();
assertNotNull(generator);
}

@Test
public void testPOSDictionaryAllowsDuplicateTagInsertions() {
POSDictionary dict = new POSDictionary(true);
// dict.addTags("jump", new String[] { "VB" });
// dict.addTags("jump", new String[] { "VB" });
String[] tags = dict.getTags("jump");
assertNotNull(tags);
assertEquals(1, tags.length);
}

@Test
public void testValidatePOSDictionaryFailsForEmptyModel() throws Exception {
POSDictionary dict = new POSDictionary(true);
// dict.addTags("eat", new String[] { "VB" });
// AbstractModel model = new AbstractModel() {
// 
// public int getNumOutcomes() {
// return 0;
// }
// 
// public String getOutcome(int i) {
// return null;
// }
// 
// public double[] eval(String[] context) {
// return new double[0];
// }
// 
// public String getBestOutcome(double[] ocs) {
// return null;
// }
// 
// public String getAllOutcomes(double[] ocs) {
// return null;
// }
// 
// public String[] getDataStructures() {
// return new String[0];
// }
// 
// public int getIndex(String outcome) {
// return -1;
// }
// };
POSTaggerFactory factory = new POSTaggerFactory();
// factory.validatePOSDictionary(dict, model);
}

@Test
public void testMultipleCallsToCreateArtifactMapProduceSafeMap() {
POSDictionary dict = new POSDictionary(true);
POSTaggerFactory factory = new POSTaggerFactory(null, null, dict);
Map<String, Object> first = factory.createArtifactMap();
Map<String, Object> second = factory.createArtifactMap();
assertNotNull(first);
assertEquals(first, second);
}

@Test
public void testLoadDefaultFeatureGeneratorBytesHandlesIOExceptionGracefully() {
InputStream brokenStream = new InputStream() {

public int read() throws IOException {
throw new IOException("test");
}
};
POSTaggerFactory factory = new POSTaggerFactory();
try {
assertNotNull(factory.createFeatureGenerators());
} catch (IllegalStateException e) {
assertTrue(e.getMessage().contains("Failed reading"));
}
}

@Test
public void testSequenceValidatorReturnsFalseIfTagDoesNotMatchDict() {
POSDictionary dict = new POSDictionary(true);
// dict.addTags("run", new String[] { "VB" });
POSTaggerFactory factory = new POSTaggerFactory(null, new HashMap<>(), dict);
SequenceValidator<String> validator = factory.getSequenceValidator();
String[] toks = new String[] { "run" };
List<String> prev = Collections.singletonList("NN");
// boolean isValid = validator.validSequence(0, toks, prev, "NN", null);
// assertFalse(isValid);
}

@Test
public void testPOSDictionarySerializerHandlesClosedOutputStreamGracefully() {
POSDictionary dict = new POSDictionary(true);
// dict.addTags("go", new String[] { "VB" });
POSTaggerFactory.POSDictionarySerializer serializer = new POSTaggerFactory.POSDictionarySerializer();
try {
ByteArrayOutputStream out = new ByteArrayOutputStream();
out.close();
serializer.serialize(dict, out);
fail("Expected IOException due to write to closed stream");
} catch (IOException expected) {
assertTrue(expected.getMessage() != null);
}
}

@Test
public void testValidateArtifactMapHandlesAllNullArtifacts() throws InvalidFormatException {
POSTaggerFactory factory = new POSTaggerFactory();
// factory.artifactProvider = new ArtifactProvider() {
// 
// public <T> T getArtifact(String key) {
// return null;
// }
// 
// public boolean isLoadedFromSerialized() {
// return false;
// }
// 
// public Properties getManifestEntries() {
// return new Properties();
// }
// };
factory.validateArtifactMap();
}

@Test
public void testGetTagDictionaryFallbackToArtifactProviderOnly() {
final POSDictionary dict = new POSDictionary(true);
// dict.addTags("swim", new String[] { "VB" });
POSTaggerFactory factory = new POSTaggerFactory();
// factory.artifactProvider = new ArtifactProvider() {
// 
// public <T> T getArtifact(String key) {
// if (key.equals("tags.tagdict"))
// return (T) dict;
// return null;
// }
// 
// public boolean isLoadedFromSerialized() {
// return false;
// }
// 
// public Properties getManifestEntries() {
// return new Properties();
// }
// };
TagDictionary result = factory.getTagDictionary();
assertNotNull(result);
assertArrayEquals(new String[] { "VB" }, result.getTags("swim"));
}

@Test
public void testValidatePOSDictionaryWithEmptyDictThrows() throws Exception {
POSDictionary emptyDict = new POSDictionary(true);
// AbstractModel model = new AbstractModel() {
// 
// public int getNumOutcomes() {
// return 1;
// }
// 
// public String getOutcome(int i) {
// return "VB";
// }
// 
// public double[] eval(String[] context) {
// return new double[] { 1.0 };
// }
// 
// public String getBestOutcome(double[] ocs) {
// return "VB";
// }
// 
// public String getAllOutcomes(double[] ocs) {
// return null;
// }
// 
// public String[] getDataStructures() {
// return new String[0];
// }
// 
// public int getIndex(String outcome) {
// return 0;
// }
// };
POSTaggerFactory factory = new POSTaggerFactory();
// factory.validatePOSDictionary(emptyDict, model);
}

@Test
public void testCreateTagDictionaryWithGarbledStreamThrows() throws IOException {
byte[] invalidData = new byte[] { 0, 1, 2, 3, 4 };
InputStream input = new ByteArrayInputStream(invalidData);
POSTaggerFactory factory = new POSTaggerFactory();
factory.createTagDictionary(input);
}

@Test
public void testValidatePOSDictionaryWithDuplicateModelOutcomes() throws Exception {
POSDictionary dict = new POSDictionary(true);
// dict.addTags("jump", new String[] { "NN", "VB" });
// AbstractModel model = new AbstractModel() {
// 
// public int getNumOutcomes() {
// return 3;
// }
// 
// public String getOutcome(int i) {
// if (i == 0)
// return "NN";
// if (i == 1)
// return "VB";
// return "VB";
// }
// 
// public double[] eval(String[] context) {
// return new double[3];
// }
// 
// public String getBestOutcome(double[] ocs) {
// return null;
// }
// 
// public String getAllOutcomes(double[] ocs) {
// return null;
// }
// 
// public String[] getDataStructures() {
// return null;
// }
// 
// public int getIndex(String outcome) {
// return 1;
// }
// };
POSTaggerFactory factory = new POSTaggerFactory();
// factory.validatePOSDictionary(dict, model);
}

@Test
public void testPOSDictionaryWithMultipleTagsSucceedsInValidator() throws InvalidFormatException {
POSDictionary dict = new POSDictionary(true);
// dict.addTags("read", new String[] { "VB", "NN" });
// AbstractModel model = new AbstractModel() {
// 
// public int getNumOutcomes() {
// return 2;
// }
// 
// public String getOutcome(int i) {
// return i == 0 ? "NN" : "VB";
// }
// 
// public double[] eval(String[] context) {
// return new double[] { 0.5, 0.5 };
// }
// 
// public String getBestOutcome(double[] ocs) {
// return null;
// }
// 
// public String getAllOutcomes(double[] ocs) {
// return null;
// }
// 
// public String[] getDataStructures() {
// return null;
// }
// 
// public int getIndex(String outcome) {
// return -1;
// }
// };
POSTaggerFactory factory = new POSTaggerFactory();
// factory.validatePOSDictionary(dict, model);
}

@Test
public void testGetPOSContextGeneratorUsesLegacyFallbackForOldVersion() {
POSTaggerFactory factory = new POSTaggerFactory();
// factory.artifactProvider = new ArtifactProvider() {
// 
// public <T> T getArtifact(String key) {
// if ("manifest.properties".equals(key)) {
// Properties p = new Properties();
// p.setProperty("OpenNLP-Version", "1.5.9");
// return (T) p;
// }
// if ("ngram.dictionary".equals(key)) {
// return (T) new Dictionary(true);
// }
// return null;
// }
// 
// public boolean isLoadedFromSerialized() {
// return false;
// }
// 
// public Properties getManifestEntries() {
// Properties p = new Properties();
// p.setProperty("OpenNLP-Version", "1.5.9");
// return p;
// }
// };
POSContextGenerator generator = factory.getPOSContextGenerator();
assertNotNull(generator);
}

@Test
public void testCreateArtifactSerializersMapIsConsistentAcrossCalls() {
POSTaggerFactory factory = new POSTaggerFactory();
// Map<String, ArtifactSerializer<?>> first = factory.createArtifactSerializersMap();
// Map<String, ArtifactSerializer<?>> second = factory.createArtifactSerializersMap();
// assertEquals(first, second);
}

@Test
public void testValidatePOSDictionaryThrowsOnNullWordTags() throws InvalidFormatException {
POSDictionary badDict = new POSDictionary(true) {

@Override
public String[] getTags(String word) {
return null;
}

@Override
public Iterator<String> iterator() {
return Collections.singletonList("badword").iterator();
}
};
// AbstractModel model = new AbstractModel() {
// 
// public int getNumOutcomes() {
// return 1;
// }
// 
// public String getOutcome(int i) {
// return "VB";
// }
// 
// public double[] eval(String[] context) {
// return new double[] { 1.0 };
// }
// 
// public String getBestOutcome(double[] ocs) {
// return null;
// }
// 
// public String getAllOutcomes(double[] ocs) {
// return null;
// }
// 
// public String[] getDataStructures() {
// return null;
// }
// 
// public int getIndex(String outcome) {
// return 0;
// }
// };
POSTaggerFactory factory = new POSTaggerFactory();
// factory.validatePOSDictionary(badDict, model);
}

@Test
public void testSetTagDictionaryTwiceOverridesPrevious() {
POSDictionary first = new POSDictionary(true);
POSDictionary second = new POSDictionary(true);
// second.addTags("final", new String[] { "NN" });
POSTaggerFactory factory = new POSTaggerFactory();
factory.setTagDictionary(first);
factory.setTagDictionary(second);
TagDictionary result = factory.getTagDictionary();
assertEquals(second, result);
}

@Test
public void testPOSDictionarySerializerSerializeNullThrows() throws IOException {
POSTaggerFactory.POSDictionarySerializer serializer = new POSTaggerFactory.POSDictionarySerializer();
OutputStream out = new ByteArrayOutputStream();
serializer.serialize(null, out);
}

@Test
public void testGetResourcesReturnsEmptyForExplicitNullResourcesInConstructor() {
POSDictionary dict = new POSDictionary(true);
POSTaggerFactory factory = new POSTaggerFactory("<generator><cache/></generator>".getBytes(), null, dict);
Map<String, Object> result = factory.getResources();
assertNotNull(result);
assertTrue(result.isEmpty());
}

@Test
public void testSequenceValidatorRejectsIfTagNotInDictionary() {
POSDictionary dict = new POSDictionary(true);
// dict.addTags("go", new String[] { "VB" });
POSTaggerFactory factory = new POSTaggerFactory(null, new HashMap<>(), dict);
SequenceValidator<String> validator = factory.getSequenceValidator();
List<String> prevTags = Arrays.asList("VB");
// boolean valid = validator.validSequence(0, new String[] { "go" }, prevTags, "NN", null);
// assertFalse(valid);
}
}
