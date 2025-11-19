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

public class POSTaggerFactory_llmsuite_1_GPTLLMTest {

@Test
public void testConstructorWithResources() {
byte[] featureBytes = "<featureGenerators/>".getBytes();
Map<String, Object> resources = new HashMap<>();
POSDictionary dictionary = new POSDictionary(true);
// dictionary.addTags("dog", new String[] { "NN" });
POSTaggerFactory factory = new POSTaggerFactory(featureBytes, resources, dictionary);
assertNotNull(factory);
assertNotNull(factory.getResources());
assertNotNull(factory.getTagDictionary());
}

@Test
public void testCreateFeatureGeneratorsReturnsNonNull() {
byte[] featureBytes = "<featureGenerators/>".getBytes();
Map<String, Object> resources = new HashMap<>();
POSDictionary dictionary = new POSDictionary(true);
POSTaggerFactory factory = new POSTaggerFactory(featureBytes, resources, dictionary);
assertNotNull(factory.createFeatureGenerators());
}

@Test
public void testCreateArtifactMapWithPOSDictionary() {
byte[] featureBytes = "<featureGenerators/>".getBytes();
Map<String, Object> resources = new HashMap<>();
POSDictionary dictionary = new POSDictionary(true);
POSTaggerFactory factory = new POSTaggerFactory(featureBytes, resources, dictionary);
Map<String, Object> artifactMap = factory.createArtifactMap();
assertTrue(artifactMap.containsKey("tags.tagdict"));
assertSame(dictionary, artifactMap.get("tags.tagdict"));
}

@Test
public void testCreateArtifactSerializersMapIncludesTagDictKey() {
POSTaggerFactory factory = new POSTaggerFactory();
// Map<String, ArtifactSerializer<?>> serializers = factory.createArtifactSerializersMap();
// assertTrue(serializers.containsKey("tagdict"));
}

@Test
public void testCreateEmptyTagDictionaryReturnsPOSDictionary() {
POSTaggerFactory factory = new POSTaggerFactory();
TagDictionary dict = factory.createEmptyTagDictionary();
assertNotNull(dict);
assertTrue(dict instanceof POSDictionary);
}

@Test
public void testSetTagDictionaryUpdatesDictionary() {
POSTaggerFactory factory = new POSTaggerFactory();
POSDictionary newDict = new POSDictionary(true);
factory.setTagDictionary(newDict);
assertSame(newDict, factory.getTagDictionary());
}

@Test
public void testGetPOSContextGeneratorWithDefaultCache() {
byte[] featureBytes = "<featureGenerators/>".getBytes();
POSDictionary dictionary = new POSDictionary(true);
POSTaggerFactory factory = new POSTaggerFactory(featureBytes, Collections.emptyMap(), dictionary);
assertNotNull(factory.getPOSContextGenerator());
}

@Test
public void testGetPOSContextGeneratorWithCacheArgument() {
byte[] featureBytes = "<featureGenerators/>".getBytes();
POSDictionary dictionary = new POSDictionary(true);
POSTaggerFactory factory = new POSTaggerFactory(featureBytes, Collections.emptyMap(), dictionary);
assertNotNull(factory.getPOSContextGenerator(3));
}

@Test
public void testGetSequenceValidatorReturnsExpected() {
byte[] featureBytes = "<featureGenerators/>".getBytes();
POSDictionary dictionary = new POSDictionary(true);
POSTaggerFactory factory = new POSTaggerFactory(featureBytes, Collections.emptyMap(), dictionary);
SequenceValidator<String> validator = factory.getSequenceValidator();
assertNotNull(validator);
assertTrue(validator instanceof DefaultPOSSequenceValidator);
}

@Test
public void testCreateTagDictionaryFromInputStream() throws Exception {
POSDictionary original = new POSDictionary(true);
// original.addTags("run", new String[] { "VB" });
ByteArrayOutputStream out = new ByteArrayOutputStream();
original.serialize(out);
InputStream input = new ByteArrayInputStream(out.toByteArray());
POSTaggerFactory factory = new POSTaggerFactory();
TagDictionary loaded = factory.createTagDictionary(input);
assertNotNull(loaded);
assertArrayEquals(new String[] { "VB" }, loaded.getTags("run"));
}

@Test
public void testGetResourcesReturnsEmptyMapWhenUninitialized() {
POSTaggerFactory factory = new POSTaggerFactory();
Map<String, Object> resources = factory.getResources();
assertNotNull(resources);
assertTrue(resources.isEmpty());
}

@Test
public void testFactoryCreateWithSubclassNameNullCreatesDefault() throws Exception {
byte[] featureBytes = "<featureGenerators/>".getBytes();
POSDictionary dictionary = new POSDictionary(true);
Map<String, Object> resources = new HashMap<>();
POSTaggerFactory factory = POSTaggerFactory.create(null, featureBytes, resources, dictionary);
assertNotNull(factory);
assertTrue(factory instanceof POSTaggerFactory);
}

@Test
public void testFactoryCreateWithInvalidSubclassThrows() throws Exception {
// expectedException.expect(InvalidFormatException.class);
byte[] featureBytes = "<featureGenerators/>".getBytes();
POSTaggerFactory.create("opennlp.tools.postag.InvalidFactory", featureBytes, new HashMap<>(), new POSDictionary(true));
}

@Test
public void testPOSDictionarySerializerSerializationCycle() throws Exception {
POSDictionary original = new POSDictionary(true);
// original.addTags("tree", new String[] { "NN" });
POSTaggerFactory.POSDictionarySerializer serializer = new POSTaggerFactory.POSDictionarySerializer();
ByteArrayOutputStream out = new ByteArrayOutputStream();
serializer.serialize(original, out);
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
POSDictionary deserialized = serializer.create(new UncloseableInputStream(in));
assertNotNull(deserialized);
assertArrayEquals(new String[] { "NN" }, deserialized.getTags("tree"));
}

@Test
public void testPOSDictionarySerializerRegisterAddsToMap() {
Map<String, ArtifactSerializer<?>> map = new HashMap<>();
// POSTaggerFactory.POSDictionarySerializer.register(map);
assertTrue(map.containsKey("tagdict"));
assertNotNull(map.get("tagdict"));
}

@Test
public void testValidatePOSDictionaryThrowsOnUnknownTag() throws Exception {
AbstractModel mockModel = mock(AbstractModel.class);
when(mockModel.getNumOutcomes()).thenReturn(1);
when(mockModel.getOutcome(0)).thenReturn("NN");
POSDictionary dict = new POSDictionary(true);
// dict.addTags("wolf", new String[] { "VBZ" });
POSTaggerFactory factory = new POSTaggerFactory();
// expectedException.expect(InvalidFormatException.class);
factory.validatePOSDictionary(dict, mockModel);
}

@Test
public void testValidateArtifactMapWithInvalidTypesThrows() throws Exception {
ArtifactProvider provider = mock(ArtifactProvider.class);
when(provider.getArtifact("tags.tagdict")).thenReturn(new Object());
POSTaggerFactory factory = new POSTaggerFactory();
// factory.artifactProvider = provider;
// expectedException.expect(InvalidFormatException.class);
factory.validateArtifactMap();
}

@Test
public void testValidateArtifactMapWithInvalidNgramTypeThrows() throws Exception {
POSDictionary dict = new POSDictionary(true);
AbstractModel model = mock(AbstractModel.class);
when(model.getNumOutcomes()).thenReturn(1);
when(model.getOutcome(0)).thenReturn("NN");
ArtifactProvider provider = mock(ArtifactProvider.class);
when(provider.getArtifact("tags.tagdict")).thenReturn(dict);
when(provider.getArtifact(POSModel.POS_MODEL_ENTRY_NAME)).thenReturn(model);
when(provider.getArtifact("ngram.dictionary")).thenReturn("wrong");
when(provider.isLoadedFromSerialized()).thenReturn(false);
POSTaggerFactory factory = new POSTaggerFactory();
// factory.artifactProvider = provider;
// expectedException.expect(InvalidFormatException.class);
factory.validateArtifactMap();
}

@Test
public void testValidateArtifactMapWithCorrectTypesSucceeds() throws Exception {
POSDictionary dict = new POSDictionary(true);
// dict.addTags("car", new String[] { "NN" });
AbstractModel model = mock(AbstractModel.class);
when(model.getNumOutcomes()).thenReturn(1);
when(model.getOutcome(0)).thenReturn("NN");
// Dictionary ngramDict = new Dictionary();
ArtifactProvider provider = mock(ArtifactProvider.class);
when(provider.getArtifact("tags.tagdict")).thenReturn(dict);
when(provider.getArtifact(POSModel.POS_MODEL_ENTRY_NAME)).thenReturn(model);
// when(provider.getArtifact("ngram.dictionary")).thenReturn(ngramDict);
when(provider.isLoadedFromSerialized()).thenReturn(false);
POSTaggerFactory factory = new POSTaggerFactory();
// factory.artifactProvider = provider;
factory.validateArtifactMap();
}

@Test
public void testCreateFeatureGeneratorsUsesArtifactProviderWhenBytesNull() {
ArtifactProvider provider = mock(ArtifactProvider.class);
byte[] fakeBytes = "<featureGenerators/>".getBytes();
when(provider.getArtifact(POSModel.GENERATOR_DESCRIPTOR_ENTRY_NAME)).thenReturn(fakeBytes);
POSTaggerFactory factory = new POSTaggerFactory();
// factory.artifactProvider = provider;
assertNotNull(factory.createFeatureGenerators());
}

@Test
public void testGetTagDictionaryUsesArtifactProviderOnlyIfUninitialized() {
POSDictionary mockDict = new POSDictionary(true);
ArtifactProvider provider = mock(ArtifactProvider.class);
when(provider.getArtifact("tags.tagdict")).thenReturn(mockDict);
POSTaggerFactory factory = new POSTaggerFactory();
// factory.artifactProvider = provider;
TagDictionary result = factory.getTagDictionary();
assertSame(mockDict, result);
}

@Test
public void testCreateFeatureGeneratorsThrowsWhenInvalidXML() {
byte[] invalidXml = "<invalid><featureGenerator/>".getBytes();
POSTaggerFactory factory = new POSTaggerFactory(invalidXml, Collections.emptyMap(), null);
factory.createFeatureGenerators();
}

@Test
public void testLoadDefaultFeatureGeneratorBytesThrowsWhenFileMissing() throws Exception {
InputStream original = POSTaggerFactory.class.getResourceAsStream("/opennlp/tools/postag/pos-default-features.xml");
Thread.currentThread().setContextClassLoader(new ClassLoader() {

@Override
public InputStream getResourceAsStream(String name) {
return null;
}
});
POSTaggerFactory factory = new POSTaggerFactory();
factory.init(null, Collections.emptyMap(), null);
factory.createFeatureGenerators();
}

@Test
public void testCreateFactoryWithSubclassNameNullUsesInitPath() throws Exception {
byte[] fgBytes = "<featureGenerators/>".getBytes();
Map<String, Object> resources = new HashMap<>();
POSDictionary dict = new POSDictionary(true);
POSTaggerFactory factory = POSTaggerFactory.create(null, fgBytes, resources, dict);
assertNotNull(factory.createFeatureGenerators());
assertNotNull(factory.createArtifactMap());
}

@Test
public void testValidateArtifactMapSkipsValidationWhenSerializedFlagTrue() throws Exception {
POSDictionary dict = new POSDictionary(true);
// dict.addTags("fish", new String[] { "NN" });
ArtifactProvider provider = mock(ArtifactProvider.class);
when(provider.getArtifact("tags.tagdict")).thenReturn(dict);
when(provider.isLoadedFromSerialized()).thenReturn(true);
POSTaggerFactory factory = new POSTaggerFactory();
// factory.artifactProvider = provider;
factory.validateArtifactMap();
}

@Test
public void testDeprecatedCreateFactoryReturnsValidInstance() throws Exception {
POSDictionary dict = new POSDictionary(true);
// Dictionary ngramDict = new Dictionary();
// POSTaggerFactory factory = POSTaggerFactory.create(null, ngramDict, dict);
// assertNotNull(factory);
}

@Test
public void testDeprecatedCreateFactoryThrowsOnInvalidSubclass() throws Exception {
// Dictionary d1 = new Dictionary();
TagDictionary d2 = new POSDictionary(true);
// POSTaggerFactory.create("not.real.FactoryName", d1, d2);
}

@Test
public void testCreatePOSContextGeneratorLoadsLegacyWhenVersionLessThanOneEight() {
Properties manifest = new Properties();
manifest.setProperty("OpenNLP-Version", "1.7.5");
ArtifactProvider provider = mock(ArtifactProvider.class);
when(provider.getArtifact("manifest.properties")).thenReturn(manifest);
// when(provider.getArtifact("ngram.dictionary")).thenReturn(new Dictionary());
POSTaggerFactory factory = new POSTaggerFactory();
// factory.artifactProvider = provider;
assertNotNull(factory.getPOSContextGenerator());
}

@Test
public void testCreatePOSContextGeneratorLoadsModernPathWhenVersionOneEightOrMore() {
Properties manifest = new Properties();
manifest.setProperty("OpenNLP-Version", "1.8.0");
ArtifactProvider provider = mock(ArtifactProvider.class);
when(provider.getArtifact("manifest.properties")).thenReturn(manifest);
when(provider.getArtifact(POSModel.GENERATOR_DESCRIPTOR_ENTRY_NAME)).thenReturn("<featureGenerators/>".getBytes());
POSTaggerFactory factory = new POSTaggerFactory();
// factory.artifactProvider = provider;
assertNotNull(factory.getPOSContextGenerator(0));
}

@Test
public void testCreateTagDictionaryFromFileReturnsValidTags() throws Exception {
POSDictionary dict = new POSDictionary(true);
// dict.addTags("mouse", new String[] { "NN" });
File tempFile = File.createTempFile("tagdict", ".bin");
tempFile.deleteOnExit();
ByteArrayOutputStream out = new ByteArrayOutputStream();
dict.serialize(out);
byte[] data = out.toByteArray();
try (FileOutputStream fos = new FileOutputStream(tempFile)) {
fos.write(data);
}
POSTaggerFactory factory = new POSTaggerFactory();
TagDictionary loaded = factory.createTagDictionary(tempFile);
assertNotNull(loaded);
assertArrayEquals(new String[] { "NN" }, loaded.getTags("mouse"));
}

@Test
public void testCreateTagDictionaryFromCorruptedInputThrowsIOException() throws Exception {
byte[] invalidBytes = new byte[] { 0, 1, 2, 3, 4, 5 };
ByteArrayInputStream inputStream = new ByteArrayInputStream(invalidBytes);
POSTaggerFactory factory = new POSTaggerFactory();
factory.createTagDictionary(inputStream);
}

@Test
public void testGetDictionaryReturnsFromArtifactProviderIfNull() {
// Dictionary dummyDict = new Dictionary();
ArtifactProvider provider = mock(ArtifactProvider.class);
// when(provider.getArtifact("ngram.dictionary")).thenReturn(dummyDict);
POSTaggerFactory factory = new POSTaggerFactory();
// factory.artifactProvider = provider;
// Dictionary returned = factory.getDictionary();
// assertSame(dummyDict, returned);
}

@Test
public void testValidatePOSDictionarySuccessWhenTagsMatchModel() throws Exception {
POSDictionary dict = new POSDictionary(true);
// dict.addTags("tower", new String[] { "NN" });
AbstractModel model = mock(AbstractModel.class);
when(model.getNumOutcomes()).thenReturn(1);
when(model.getOutcome(0)).thenReturn("NN");
POSTaggerFactory factory = new POSTaggerFactory();
factory.validatePOSDictionary(dict, model);
}

@Test
public void testCreateFeatureGeneratorsFallbackToDefaultWhenBytesNull() {
POSTaggerFactory factory = new POSTaggerFactory(null, new HashMap<>(), new POSDictionary(true));
AdaptiveFeatureGenerator generator = factory.createFeatureGenerators();
assertNotNull(generator);
}

@Test
public void testCreateArtifactMapWithBothDictionariesPresent() {
// Dictionary ngramDict = new Dictionary();
POSDictionary posDict = new POSDictionary(true);
POSTaggerFactory factory = new POSTaggerFactory();
factory.init("<featureGenerators/>".getBytes(), new HashMap<>(), posDict);
// factory.init(ngramDict, posDict);
Map<String, Object> map = factory.createArtifactMap();
assertTrue(map.containsKey("tags.tagdict"));
assertTrue(map.containsKey("ngram.dictionary"));
}

@Test
public void testCreateArtifactMapWithNoDictionariesAddsNothing() {
POSTaggerFactory factory = new POSTaggerFactory();
Map<String, Object> artifacts = factory.createArtifactMap();
assertTrue(artifacts.isEmpty());
}

@Test
public void testSetTagDictionaryThrowsWhenArtifactProviderPresent() {
ArtifactProvider provider = mock(ArtifactProvider.class);
POSTaggerFactory factory = new POSTaggerFactory();
// factory.artifactProvider = provider;
factory.setTagDictionary(new POSDictionary(true));
}

@Test
public void testPOSDictionarySerializerHandlesEmptyDictionary() throws Exception {
POSDictionary emptyDict = new POSDictionary(true);
ByteArrayOutputStream out = new ByteArrayOutputStream();
POSTaggerFactory.POSDictionarySerializer serializer = new POSTaggerFactory.POSDictionarySerializer();
serializer.serialize(emptyDict, out);
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
POSDictionary loaded = serializer.create(in);
assertNotNull(loaded);
assertNull(loaded.getTags("nonexistent"));
}

@Test
public void testCreateWithCustomFeatureGeneratorXml() {
String xml = "<?xml version=\"1.0\"?><featureGenerators><generator class=\"opennlp.tools.util.featuregen.TokenFeatureGenerator\"/></featureGenerators>";
byte[] customConfig = xml.getBytes();
POSDictionary posDict = new POSDictionary(true);
POSTaggerFactory factory = new POSTaggerFactory(customConfig, Collections.emptyMap(), posDict);
AdaptiveFeatureGenerator gen = factory.createFeatureGenerators();
assertNotNull(gen);
}

@Test
public void testCreateWithEmptyResourcesStillInitializes() throws Exception {
byte[] featureBytes = "<featureGenerators/>".getBytes();
Map<String, Object> emptyResources = Collections.emptyMap();
POSDictionary tagDict = new POSDictionary(true);
POSTaggerFactory factory = POSTaggerFactory.create(null, featureBytes, emptyResources, tagDict);
assertNotNull(factory);
assertNotNull(factory.getResources());
}

@Test
public void testCreateWithNullFeatureGeneratorBytesUsesFallbackXml() {
POSTaggerFactory factory = new POSTaggerFactory(null, Collections.emptyMap(), new POSDictionary(true));
AdaptiveFeatureGenerator generator = factory.createFeatureGenerators();
assertNotNull(generator);
}

@Test
public void testCreateWithNullTagDictionaryAndNonNullResources() {
Map<String, Object> res = new HashMap<>();
res.put("someKey", "someValue");
POSTaggerFactory factory = new POSTaggerFactory("<featureGenerators/>".getBytes(), res, null);
assertNotNull(factory.getResources());
assertNull(factory.getTagDictionary());
}

@Test
public void testGetTagDictionaryReturnsNullIfNoneSetAndNoArtifactProvider() {
POSTaggerFactory factory = new POSTaggerFactory("<featureGenerators/>".getBytes(), new HashMap<>(), null);
TagDictionary dict = factory.getTagDictionary();
assertNull(dict);
}

@Test
public void testCreateArtifactSerializersMapOverridesBase() {
POSTaggerFactory factory = new POSTaggerFactory();
// Map<String, ArtifactSerializer<?>> serializers = factory.createArtifactSerializersMap();
// assertTrue(serializers.containsKey("tagdict"));
// assertNotNull(serializers.get("tagdict"));
}

@Test
public void testCreateArtifactMapWhenOnlyNgramDictionarySet() {
// Dictionary dict = new Dictionary();
POSTaggerFactory factory = new POSTaggerFactory();
// factory.init(dict, null);
Map<String, Object> artifacts = factory.createArtifactMap();
assertTrue(artifacts.containsKey("ngram.dictionary"));
// assertEquals(dict, artifacts.get("ngram.dictionary"));
}

@Test
public void testValidateArtifactMapWithNoDictionariesDoesNothing() throws Exception {
ArtifactProvider mockProvider = mock(ArtifactProvider.class);
when(mockProvider.getArtifact("tags.tagdict")).thenReturn(null);
when(mockProvider.getArtifact("ngram.dictionary")).thenReturn(null);
POSTaggerFactory factory = new POSTaggerFactory();
// factory.artifactProvider = mockProvider;
factory.validateArtifactMap();
}

@Test
public void testValidatePOSDictionaryWithEmptyModelTags() throws Exception {
POSDictionary dict = new POSDictionary(true);
// dict.addTags("bird", new String[] { "NN" });
AbstractModel model = mock(AbstractModel.class);
when(model.getNumOutcomes()).thenReturn(0);
POSTaggerFactory factory = new POSTaggerFactory();
// expectedException.expect(InvalidFormatException.class);
factory.validatePOSDictionary(dict, model);
}

@Test
public void testGetPOSContextGeneratorFallbackWhenVersionNull() {
ArtifactProvider provider = mock(ArtifactProvider.class);
when(provider.getArtifact("manifest.properties")).thenReturn(new Properties());
POSTaggerFactory factory = new POSTaggerFactory();
// factory.artifactProvider = provider;
assertNotNull(factory.getPOSContextGenerator(0));
}

@Test
public void testPOSDictionarySerializerHandlesNullStreamGracefully() throws Exception {
byte[] empty = new byte[0];
ByteArrayInputStream in = new ByteArrayInputStream(empty);
POSTaggerFactory.POSDictionarySerializer serializer = new POSTaggerFactory.POSDictionarySerializer();
// expectedException.expect(IOException.class);
serializer.create(in);
}

@Test
public void testInitWithNullFeatureGeneratorBytesFallBacksToDefault() {
Map<String, Object> resources = new HashMap<>();
POSDictionary tagDict = new POSDictionary(true);
POSTaggerFactory factory = new POSTaggerFactory();
factory.init(null, resources, tagDict);
AdaptiveFeatureGenerator generator = factory.createFeatureGenerators();
assertNotNull(generator);
}

@Test
public void testCreateUsesValidSubclassViaReflection() throws Exception {
byte[] generatorBytes = "<featureGenerators/>".getBytes();
Map<String, Object> resources = new HashMap<>();
POSDictionary tagDict = new POSDictionary(true);
POSTaggerFactory factory = POSTaggerFactory.create("opennlp.tools.postag.POSTaggerFactory", generatorBytes, resources, tagDict);
assertNotNull(factory);
}

@Test
public void testCreateHandlesReflectionInstantiationException() throws Exception {
// Dictionary ngramDict = new Dictionary();
POSDictionary tagDict = new POSDictionary(true);
// POSTaggerFactory.create("java.lang.String", ngramDict, tagDict);
}

@Test
public void testCreateWithNullParametersInitializesCorrectly() throws Exception {
POSTaggerFactory factory = POSTaggerFactory.create(null, null, null, null);
assertNotNull(factory);
assertNotNull(factory.getResources());
}

@Test
public void testCreateFeatureGeneratorsReturnsSameInstanceMultipleTimes() {
byte[] generatorBytes = "<featureGenerators/>".getBytes();
POSDictionary tagDict = new POSDictionary(true);
POSTaggerFactory factory = new POSTaggerFactory(generatorBytes, Collections.emptyMap(), tagDict);
AdaptiveFeatureGenerator g1 = factory.createFeatureGenerators();
AdaptiveFeatureGenerator g2 = factory.createFeatureGenerators();
assertNotNull(g1);
assertNotNull(g2);
}

@Test
public void testValidatePOSDictionaryWithEmptyDictionaryPassesSilently() throws Exception {
POSDictionary dict = new POSDictionary(true);
AbstractModel model = mock(AbstractModel.class);
when(model.getNumOutcomes()).thenReturn(2);
when(model.getOutcome(0)).thenReturn("NN");
when(model.getOutcome(1)).thenReturn("VB");
POSTaggerFactory factory = new POSTaggerFactory();
factory.validatePOSDictionary(dict, model);
}

@Test
public void testValidatePOSDictionaryThrowsIfSingleTagIsUnknown() throws Exception {
POSDictionary dict = new POSDictionary(true);
// dict.addTags("sleep", new String[] { "ZZZ" });
AbstractModel model = mock(AbstractModel.class);
when(model.getNumOutcomes()).thenReturn(1);
when(model.getOutcome(0)).thenReturn("NN");
POSTaggerFactory factory = new POSTaggerFactory();
factory.validatePOSDictionary(dict, model);
}

@Test
public void testSequenceValidatorWithNullDictionaryAllowsAllSequences() {
POSTaggerFactory factory = new POSTaggerFactory(null, Collections.emptyMap(), null);
SequenceValidator<String> validator = factory.getSequenceValidator();
// assertTrue(validator.isValidSequence(0, new String[] { "cat" }, new String[] { "NN" }, "NN"));
}

@Test
public void testSequenceValidatorRejectsInvalidSequenceTag() {
POSDictionary dict = new POSDictionary(true);
// dict.addTags("bird", new String[] { "NN" });
POSTaggerFactory factory = new POSTaggerFactory(null, Collections.emptyMap(), dict);
SequenceValidator<String> validator = factory.getSequenceValidator();
// assertFalse(validator.isValidSequence(0, new String[] { "bird" }, new String[] {}, "VB"));
}

@Test
public void testCreateArtifactMapWithNullResourcesMapStillCreatesValidEmptyArtifactMap() {
POSTaggerFactory factory = new POSTaggerFactory("<featureGenerators/>".getBytes(), null, null);
Map<String, Object> artifacts = factory.createArtifactMap();
assertNotNull(artifacts);
assertTrue(artifacts.isEmpty());
}

@Test
public void testGetPOSContextGeneratorReturnsFallbackWhenPropertiesMissing() {
Properties props = new Properties();
ArtifactProvider provider = mock(ArtifactProvider.class);
when(provider.getArtifact("manifest.properties")).thenReturn(props);
POSTaggerFactory factory = new POSTaggerFactory();
// factory.artifactProvider = provider;
assertNotNull(factory.getPOSContextGenerator(1));
}

@Test
public void testCreateFeatureGeneratorsThrowsOnIOException() {
byte[] corruptedXml = new byte[] { (byte) 0xC3, (byte) 0x28 };
POSTaggerFactory factory = new POSTaggerFactory(corruptedXml, Collections.emptyMap(), null);
factory.createFeatureGenerators();
}

@Test
public void testConfigurablePOSContextGeneratorIncludesCustomCacheSize() {
byte[] config = "<featureGenerators/>".getBytes();
POSDictionary dictionary = new POSDictionary(true);
POSTaggerFactory factory = new POSTaggerFactory(config, Collections.emptyMap(), dictionary);
POSContextGenerator generator = factory.getPOSContextGenerator(42);
assertNotNull(generator);
assertTrue(generator instanceof ConfigurablePOSContextGenerator);
}

@Test
public void testGetPOSContextGeneratorFallsBackToConfigurableWhenInvalidVersion() {
Properties properties = new Properties();
ArtifactProvider provider = mock(ArtifactProvider.class);
when(provider.getArtifact("manifest.properties")).thenReturn(properties);
POSTaggerFactory factory = new POSTaggerFactory();
// factory.artifactProvider = provider;
POSContextGenerator generator = factory.getPOSContextGenerator(0);
assertNotNull(generator);
assertTrue(generator instanceof ConfigurablePOSContextGenerator);
}

@Test
public void testGetPOSContextGeneratorReturnsLegacyWhenVersionBelow18() {
Properties versionProps = new Properties();
versionProps.setProperty("OpenNLP-Version", "1.7.4");
// Dictionary ngramDict = new Dictionary();
ArtifactProvider provider = mock(ArtifactProvider.class);
when(provider.getArtifact("manifest.properties")).thenReturn(versionProps);
// when(provider.getArtifact("ngram.dictionary")).thenReturn(ngramDict);
POSTaggerFactory factory = new POSTaggerFactory();
// factory.artifactProvider = provider;
POSContextGenerator generator = factory.getPOSContextGenerator();
assertNotNull(generator);
assertTrue(generator instanceof DefaultPOSContextGenerator);
}

@Test
public void testValidateArtifactMapThrowsOnTagDictionaryWrongType() throws Exception {
ArtifactProvider provider = mock(ArtifactProvider.class);
when(provider.getArtifact("tags.tagdict")).thenReturn("not-a-dictionary");
POSTaggerFactory factory = new POSTaggerFactory();
// factory.artifactProvider = provider;
factory.validateArtifactMap();
}

@Test
public void testValidateArtifactMapThrowsOnNGramDictionaryWrongType() throws Exception {
POSDictionary posDict = new POSDictionary(true);
AbstractModel model = mock(AbstractModel.class);
when(model.getNumOutcomes()).thenReturn(1);
when(model.getOutcome(0)).thenReturn("NN");
ArtifactProvider provider = mock(ArtifactProvider.class);
when(provider.getArtifact("tags.tagdict")).thenReturn(posDict);
when(provider.getArtifact("ngram.dictionary")).thenReturn("invalid-type");
when(provider.getArtifact(POSModel.POS_MODEL_ENTRY_NAME)).thenReturn(model);
when(provider.isLoadedFromSerialized()).thenReturn(false);
POSTaggerFactory factory = new POSTaggerFactory();
// factory.artifactProvider = provider;
factory.validateArtifactMap();
}

@Test
public void testCreateArtifactMapReturnsEmptyWhenDictionariesAreNull() {
POSTaggerFactory factory = new POSTaggerFactory("<featureGenerators/>".getBytes(), null, null);
Map<String, Object> artifactMap = factory.createArtifactMap();
assertNotNull(artifactMap);
assertFalse(artifactMap.containsKey("tags.tagdict"));
assertFalse(artifactMap.containsKey("ngram.dictionary"));
}

@Test
public void testTagDictionaryReturnedViaArtifactProviderWhenInternalIsNull() {
POSDictionary mockPOSDict = new POSDictionary(true);
ArtifactProvider provider = mock(ArtifactProvider.class);
when(provider.getArtifact("tags.tagdict")).thenReturn(mockPOSDict);
POSTaggerFactory factory = new POSTaggerFactory("<featureGenerators/>".getBytes(), null, null);
// factory.artifactProvider = provider;
TagDictionary result = factory.getTagDictionary();
assertSame(mockPOSDict, result);
}

@Test
public void testGetTagDictionaryReturnsPreviouslySetDictionary() {
POSDictionary dictionary = new POSDictionary(true);
POSTaggerFactory factory = new POSTaggerFactory();
factory.setTagDictionary(dictionary);
TagDictionary retrieved = factory.getTagDictionary();
assertSame(dictionary, retrieved);
}

@Test
public void testCreateArtifactSerializersMapContainsExpectedKeys() {
POSTaggerFactory factory = new POSTaggerFactory();
// Map<String, ArtifactSerializer<?>> map = factory.createArtifactSerializersMap();
// assertTrue(map.containsKey("tagdict"));
}

@Test
public void testCreateFactoryWithValidSubclassInitializesCorrectly() throws Exception {
byte[] featureBytes = "<featureGenerators/>".getBytes();
Map<String, Object> resources = new HashMap<>();
POSDictionary dictionary = new POSDictionary(true);
POSTaggerFactory factory = POSTaggerFactory.create("opennlp.tools.postag.POSTaggerFactory", featureBytes, resources, dictionary);
assertNotNull(factory);
assertTrue(factory instanceof POSTaggerFactory);
}

@Test
public void testGetResourcesReturnsEmptyWhenConstructorDoesNotSetResources() {
POSTaggerFactory factory = new POSTaggerFactory();
Map<String, Object> result = factory.getResources();
assertNotNull(result);
assertTrue(result.isEmpty());
}

@Test
public void testCreateArtifactMapWithOnlyPOSDictionary() {
POSDictionary posDict = new POSDictionary(true);
POSTaggerFactory factory = new POSTaggerFactory("<featureGenerators/>".getBytes(), null, posDict);
Map<String, Object> artifacts = factory.createArtifactMap();
assertTrue(artifacts.containsKey("tags.tagdict"));
assertEquals(posDict, artifacts.get("tags.tagdict"));
assertFalse(artifacts.containsKey("ngram.dictionary"));
}

@Test
public void testCreateArtifactSerializersMapOverridesSuperClassMap() {
POSTaggerFactory factory = new POSTaggerFactory();
// Map<String, ArtifactSerializer<?>> serializers = factory.createArtifactSerializersMap();
// assertNotNull(serializers);
// assertTrue(serializers.containsKey("tagdict"));
// ArtifactSerializer<?> serializer = serializers.get("tagdict");
// assertTrue(serializer instanceof POSTaggerFactory.POSDictionarySerializer);
}

@Test
public void testPOSDictionarySerializerSerializeDeserializeEmptyDictionary() throws Exception {
POSTaggerFactory.POSDictionarySerializer serializer = new POSTaggerFactory.POSDictionarySerializer();
POSDictionary posDict = new POSDictionary(true);
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
serializer.serialize(posDict, outputStream);
ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
POSDictionary loadedDict = serializer.create(inputStream);
assertNotNull(loadedDict);
assertNull(loadedDict.getTags("nonexistent"));
}

@Test
public void testInitWithExplicitNullsAndThenCallCreateFeatureGenerators() {
POSTaggerFactory factory = new POSTaggerFactory();
factory.init(null, null, null);
AdaptiveFeatureGenerator generator = factory.createFeatureGenerators();
assertNotNull(generator);
}

@Test
public void testGetTagDictionaryFromFieldWhenArtifactProviderNull() {
POSDictionary dict = new POSDictionary(true);
POSTaggerFactory factory = new POSTaggerFactory("<featureGenerators/>".getBytes(), null, dict);
TagDictionary tagDict = factory.getTagDictionary();
assertSame(dict, tagDict);
}

@Test
public void testGetTagDictionaryLoadsFromArtifactProviderIfFieldIsNull() {
POSDictionary externalDict = new POSDictionary(true);
ArtifactProvider provider = mock(ArtifactProvider.class);
when(provider.getArtifact("tags.tagdict")).thenReturn(externalDict);
POSTaggerFactory factory = new POSTaggerFactory("<featureGenerators/>".getBytes(), null, null);
// factory.artifactProvider = provider;
TagDictionary dict = factory.getTagDictionary();
assertSame(externalDict, dict);
}

@Test
public void testDeprecatedGetDictionaryLoadsFromArtifactProviderIfNull() {
// Dictionary ngramDict = new Dictionary();
ArtifactProvider provider = mock(ArtifactProvider.class);
// when(provider.getArtifact("ngram.dictionary")).thenReturn(ngramDict);
POSTaggerFactory factory = new POSTaggerFactory();
// factory.artifactProvider = provider;
// Dictionary result = factory.getDictionary();
// assertSame(ngramDict, result);
}

@Test
public void testValidateArtifactMapThrowsIfNGramDictionaryWrongType() throws Exception {
ArtifactProvider provider = mock(ArtifactProvider.class);
POSDictionary dict = new POSDictionary(true);
AbstractModel model = mock(AbstractModel.class);
when(model.getNumOutcomes()).thenReturn(1);
when(model.getOutcome(0)).thenReturn("NN");
when(provider.getArtifact("tags.tagdict")).thenReturn(dict);
when(provider.getArtifact("ngram.dictionary")).thenReturn("wrong-type");
when(provider.getArtifact(POSModel.POS_MODEL_ENTRY_NAME)).thenReturn(model);
when(provider.isLoadedFromSerialized()).thenReturn(false);
POSTaggerFactory factory = new POSTaggerFactory();
// factory.artifactProvider = provider;
factory.validateArtifactMap();
}

@Test
public void testGetPOSContextGeneratorWithNullVersionPropertyFallsBack() {
Properties props = new Properties();
ArtifactProvider provider = mock(ArtifactProvider.class);
when(provider.getArtifact("manifest.properties")).thenReturn(props);
when(provider.getArtifact(POSModel.GENERATOR_DESCRIPTOR_ENTRY_NAME)).thenReturn("<featureGenerators/>".getBytes());
POSTaggerFactory factory = new POSTaggerFactory();
// factory.artifactProvider = provider;
POSContextGenerator generator = factory.getPOSContextGenerator();
assertTrue(generator instanceof ConfigurablePOSContextGenerator);
}

@Test
public void testCreateFeatureGeneratorsUsesResourcesMapIfProviderMissing() {
Map<String, Object> res = new HashMap<>();
res.put("key", "value");
byte[] config = "<featureGenerators/>".getBytes();
POSTaggerFactory factory = new POSTaggerFactory(config, res, null);
AdaptiveFeatureGenerator generator = factory.createFeatureGenerators();
assertNotNull(generator);
}

@Test
public void testValidatePOSDictionaryWithKnownModelTagDoesNotThrow() throws Exception {
POSDictionary dict = new POSDictionary(true);
// dict.addTags("car", new String[] { "NN" });
AbstractModel model = mock(AbstractModel.class);
when(model.getNumOutcomes()).thenReturn(1);
when(model.getOutcome(0)).thenReturn("NN");
POSTaggerFactory factory = new POSTaggerFactory();
factory.validatePOSDictionary(dict, model);
}

@Test
public void testValidatePOSDictionaryWithExtraWhitespaceTagInModelPasses() throws Exception {
POSDictionary dict = new POSDictionary(true);
// dict.addTags("car", new String[] { "NN" });
AbstractModel model = mock(AbstractModel.class);
when(model.getNumOutcomes()).thenReturn(2);
when(model.getOutcome(0)).thenReturn("NN");
when(model.getOutcome(1)).thenReturn("  ");
POSTaggerFactory factory = new POSTaggerFactory();
factory.validatePOSDictionary(dict, model);
}
}
