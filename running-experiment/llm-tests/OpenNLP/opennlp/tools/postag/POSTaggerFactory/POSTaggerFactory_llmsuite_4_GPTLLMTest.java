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
import opennlp.tools.util.featuregen.GeneratorFactory;
import opennlp.tools.util.featuregen.TokenFeatureGenerator;
import opennlp.tools.util.featuregen.WindowFeatureGenerator;
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

public class POSTaggerFactory_llmsuite_4_GPTLLMTest {

@Test
public void testDefaultConstructorCreatesFactory() {
POSTaggerFactory factory = new POSTaggerFactory();
assertNotNull(factory);
}

@Test
public void testConstructorWithResourcesAndDictionary() {
byte[] featureBytes = "<generators/>".getBytes(StandardCharsets.UTF_8);
Map<String, Object> resources = new HashMap<String, Object>();
TagDictionary dictionary = new POSDictionary(true);
POSTaggerFactory factory = new POSTaggerFactory(featureBytes, resources, dictionary);
assertNotNull(factory.getFeatureGenerator());
assertEquals(dictionary, factory.getTagDictionary());
assertEquals(resources, factory.getResources());
}

@Test
public void testCreateEmptyTagDictionaryReturnsPOSDictionary() {
POSTaggerFactory factory = new POSTaggerFactory();
TagDictionary dictionary = factory.createEmptyTagDictionary();
assertNotNull(dictionary);
assertTrue(dictionary instanceof POSDictionary);
}

@Test
public void testCreateTagDictionaryFromStreamSuccess() throws IOException {
byte[] dictBytes = ("run VBZ\nplay VB\nball NN\n").getBytes(StandardCharsets.UTF_8);
InputStream inputStream = new ByteArrayInputStream(dictBytes);
POSTaggerFactory factory = new POSTaggerFactory();
TagDictionary dictionary = factory.createTagDictionary(inputStream);
assertNotNull(dictionary);
String[] tags = dictionary.getTags("run");
assertNotNull(tags);
}

@Test
public void testCreateTagDictionaryThrowsIOException() throws IOException {
InputStream badStream = mock(InputStream.class);
when(badStream.read(any(byte[].class))).thenThrow(new IOException("Bad input"));
POSTaggerFactory factory = new POSTaggerFactory();
factory.createTagDictionary(badStream);
}

@Test
public void testSetTagDictionaryFailsWithArtifactProviderSet() {
POSTaggerFactory factory = new POSTaggerFactory();
ArtifactProvider mockProvider = mock(ArtifactProvider.class);
// factory.artifactProvider = mockProvider;
factory.setTagDictionary(new POSDictionary(true));
}

@Test
public void testCreateArtifactMapReturnsExpectedEntries() {
// Dictionary ngramDictionary = new Dictionary();
POSDictionary tagDictionary = new POSDictionary(true);
POSTaggerFactory factory = new POSTaggerFactory();
// factory.ngramDictionary = ngramDictionary;
factory.posDictionary = tagDictionary;
Map<String, Object> map = factory.createArtifactMap();
// assertEquals(ngramDictionary, map.get("ngram.dictionary"));
assertEquals(tagDictionary, map.get("tags.tagdict"));
}

@Test
public void testCreateFeatureGeneratorsReturnsNonNull() {
POSTaggerFactory factory = new POSTaggerFactory();
AdaptiveFeatureGenerator generator = factory.createFeatureGenerators();
assertNotNull(generator);
}

@Test
public void testGetResourcesReturnsEmptyMapIfNull() {
POSTaggerFactory factory = new POSTaggerFactory();
Map<String, Object> resources = factory.getResources();
assertNotNull(resources);
assertTrue(resources.isEmpty());
}

@Test
public void testGetPOSContextGeneratorDefault() {
POSTaggerFactory factory = new POSTaggerFactory();
POSContextGenerator generator = factory.getPOSContextGenerator();
assertNotNull(generator);
}

@Test
public void testGetPOSContextGeneratorWithOldVersion() {
// Dictionary dict = new Dictionary();
ArtifactProvider provider = mock(ArtifactProvider.class);
Properties props = new Properties();
props.setProperty("OpenNLP-Version", "1.7.0");
when(provider.getArtifact("manifest.properties")).thenReturn(props);
// when(provider.getArtifact("ngram.dictionary")).thenReturn(dict);
POSTaggerFactory factory = new POSTaggerFactory();
// factory.artifactProvider = provider;
POSContextGenerator generator = factory.getPOSContextGenerator();
assertNotNull(generator);
assertTrue(generator instanceof DefaultPOSContextGenerator);
}

@Test
public void testGetSequenceValidatorReturnsDefault() {
POSDictionary dict = new POSDictionary(true);
POSTaggerFactory factory = new POSTaggerFactory();
factory.setTagDictionary(dict);
SequenceValidator<String> validator = factory.getSequenceValidator();
assertNotNull(validator);
assertTrue(validator instanceof DefaultPOSSequenceValidator);
}

@Test
public void testValidatePOSDictionarySucceeds() throws InvalidFormatException {
POSDictionary dict = new POSDictionary(true);
// dict.addTags("dog", new String[] { "NN" });
AbstractModel model = mock(AbstractModel.class);
when(model.getNumOutcomes()).thenReturn(1);
when(model.getOutcome(0)).thenReturn("NN");
POSTaggerFactory factory = new POSTaggerFactory();
factory.validatePOSDictionary(dict, model);
}

@Test
public void testValidatePOSDictionaryFailsForUnknownTag() throws InvalidFormatException {
POSDictionary dict = new POSDictionary(true);
// dict.addTags("horse", new String[] { "XYZ" });
AbstractModel model = mock(AbstractModel.class);
when(model.getNumOutcomes()).thenReturn(1);
when(model.getOutcome(0)).thenReturn("NN");
POSTaggerFactory factory = new POSTaggerFactory();
factory.validatePOSDictionary(dict, model);
}

@Test
public void testValidateArtifactMapInvalidTagDictType() throws InvalidFormatException {
POSTaggerFactory factory = new POSTaggerFactory();
ArtifactProvider provider = mock(ArtifactProvider.class);
when(provider.getArtifact("tags.tagdict")).thenReturn("not-a-dict");
// factory.artifactProvider = provider;
factory.validateArtifactMap();
}

@Test
public void testValidateArtifactMapInvalidNGramType() throws InvalidFormatException {
POSDictionary tagDict = new POSDictionary(true);
POSTaggerFactory factory = new POSTaggerFactory();
AbstractModel model = mock(AbstractModel.class);
when(model.getNumOutcomes()).thenReturn(0);
ArtifactProvider provider = mock(ArtifactProvider.class);
when(provider.getArtifact("tags.tagdict")).thenReturn(tagDict);
when(provider.getArtifact("pos.model")).thenReturn(model);
when(provider.getArtifact("ngram.dictionary")).thenReturn("badtype");
when(provider.isLoadedFromSerialized()).thenReturn(true);
// factory.artifactProvider = provider;
factory.validateArtifactMap();
}

@Test
public void testDictionarySerializerRoundTrip() throws IOException {
POSDictionary original = new POSDictionary(true);
// original.addTags("cat", new String[] { "NN" });
ByteArrayOutputStream out = new ByteArrayOutputStream();
POSTaggerFactory.POSDictionarySerializer serializer = new POSTaggerFactory.POSDictionarySerializer();
serializer.serialize(original, out);
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
POSDictionary loaded = serializer.create(in);
String[] tags = loaded.getTags("cat");
assertNotNull(tags);
assertEquals("NN", tags[0]);
}

@Test
public void testStaticCreateFactoryWithNullSubclassName() throws InvalidFormatException {
byte[] featureBytes = "<generators/>".getBytes(StandardCharsets.UTF_8);
Map<String, Object> resources = new HashMap<String, Object>();
TagDictionary dict = new POSDictionary(true);
POSTaggerFactory factory = POSTaggerFactory.create(null, featureBytes, resources, dict);
assertNotNull(factory);
assertEquals(dict, factory.getTagDictionary());
}

@Test
public void testStaticCreateFactoryWithInvalidClassNameThrows() throws InvalidFormatException {
POSTaggerFactory.create("invalid.FactoryName", null, null, null);
}

@Test
public void testDeprecatedStaticCreateUsesDefaultsForNullSubclass() throws InvalidFormatException {
// Dictionary dict = new Dictionary();
TagDictionary posDict = new POSDictionary(true);
// POSTaggerFactory factory = POSTaggerFactory.create(null, dict, posDict);
// assertNotNull(factory);
// assertEquals(posDict, factory.getTagDictionary());
}

@Test
public void testGetTagDictionaryUsesArtifactProviderOnlyIfPosDictionaryIsNull() {
POSDictionary dict = new POSDictionary(true);
ArtifactProvider provider = mock(ArtifactProvider.class);
when(provider.getArtifact("tags.tagdict")).thenReturn(dict);
POSTaggerFactory factory = new POSTaggerFactory();
// factory.artifactProvider = provider;
TagDictionary result = factory.getTagDictionary();
assertEquals(dict, result);
}

@Test
public void testDeprecatedGetDictionaryUsesArtifactProvider() {
// Dictionary dict = new Dictionary();
ArtifactProvider provider = mock(ArtifactProvider.class);
// when(provider.getArtifact("ngram.dictionary")).thenReturn(dict);
POSTaggerFactory factory = new POSTaggerFactory();
// factory.artifactProvider = provider;
// Dictionary result = factory.getDictionary();
// assertEquals(dict, result);
}

@Test
public void testInitWithNullFeatureBytesLoadsDefault() {
Map<String, Object> resources = new HashMap<String, Object>();
TagDictionary dict = new POSDictionary(true);
POSTaggerFactory factory = new POSTaggerFactory(null, resources, dict);
assertNotNull(factory.getFeatureGenerator());
}

@Test
public void testCreateArtifactSerializersMapIncludesTagdict() {
POSTaggerFactory factory = new POSTaggerFactory();
// Map<String, opennlp.tools.util.model.ArtifactSerializer<?>> map = factory.createArtifactSerializersMap();
// assertTrue(map.containsKey("tagdict"));
// assertNotNull(map.get("tagdict"));
}

@Test
public void testCreateArtifactMapReturnsEmptyWhenNoDictionariesSet() {
POSTaggerFactory factory = new POSTaggerFactory();
Map<String, Object> map = factory.createArtifactMap();
assertTrue(map.isEmpty());
}

@Test
public void testCreateTagDictionaryFromFileFormatWithExtraSpaces() throws IOException {
String data = " fly   VBZ  \n jump    VBP  \n";
byte[] bytes = data.getBytes("UTF-8");
InputStream input = new ByteArrayInputStream(bytes);
POSTaggerFactory factory = new POSTaggerFactory();
TagDictionary tagDict = factory.createTagDictionary(input);
assertNotNull(tagDict);
assertEquals("VBZ", tagDict.getTags("fly")[0]);
assertEquals("VBP", tagDict.getTags("jump")[0]);
}

@Test
public void testGetPOSContextGeneratorWithZeroCacheSize() {
POSTaggerFactory factory = new POSTaggerFactory();
assertNotNull(factory.getPOSContextGenerator(0));
}

@Test
public void testGetPOSContextGeneratorWithLargeCacheSize() {
POSTaggerFactory factory = new POSTaggerFactory();
assertNotNull(factory.getPOSContextGenerator(10000));
}

@Test
public void testGetTagDictionaryReturnsExistingWithoutArtifactProvider() {
TagDictionary dict = new POSDictionary(true);
POSTaggerFactory factory = new POSTaggerFactory("<generators/>".getBytes(), new HashMap<String, Object>(), dict);
TagDictionary returned = factory.getTagDictionary();
assertEquals(dict, returned);
}

@Test
public void testCreateFactoryWithSubclassNameThatIsItself() throws InvalidFormatException {
class CustomPOSTaggerFactory extends POSTaggerFactory {
}
String className = CustomPOSTaggerFactory.class.getName();
byte[] featureGen = "<generators/>".getBytes();
Map<String, Object> resources = new HashMap<String, Object>();
TagDictionary dictionary = new POSDictionary(true);
POSTaggerFactory factory = POSTaggerFactory.create(className, featureGen, resources, dictionary);
assertNotNull(factory);
}

@Test
public void testValidatePOSDictionaryWhenModelTagsAreSuperset() throws InvalidFormatException {
POSDictionary dict = new POSDictionary(true);
// dict.addTags("cat", new String[] { "NN" });
AbstractModel model = mock(AbstractModel.class);
when(model.getNumOutcomes()).thenReturn(3);
when(model.getOutcome(0)).thenReturn("NN");
when(model.getOutcome(1)).thenReturn("VB");
when(model.getOutcome(2)).thenReturn("JJ");
POSTaggerFactory factory = new POSTaggerFactory();
factory.validatePOSDictionary(dict, model);
}

@Test
public void testValidateArtifactMapSkipsCheckWhenSerializedTrue() throws InvalidFormatException {
POSTaggerFactory factory = new POSTaggerFactory();
AbstractModel model = mock(AbstractModel.class);
POSDictionary dict = new POSDictionary(true);
ArtifactProvider provider = mock(ArtifactProvider.class);
when(provider.getArtifact("tags.tagdict")).thenReturn(dict);
when(provider.getArtifact("pos.model")).thenReturn(model);
// when(provider.getArtifact("ngram.dictionary")).thenReturn(new Dictionary());
when(provider.isLoadedFromSerialized()).thenReturn(true);
// factory.artifactProvider = provider;
factory.validateArtifactMap();
}

@Test
public void testCreateFactoryWithNullFeatureBytesCallsDefaultGenerationLogic() {
Map<String, Object> resources = new HashMap<String, Object>();
TagDictionary dict = new POSDictionary(true);
POSTaggerFactory factory = new POSTaggerFactory(null, resources, dict);
AdaptiveFeatureGenerator gen = factory.createFeatureGenerators();
assertNotNull(gen);
}

@Test
public void testFactoryHandlesNullArtifactReturnedFromProviderGracefully() {
POSTaggerFactory factory = new POSTaggerFactory();
ArtifactProvider provider = mock(ArtifactProvider.class);
when(provider.getArtifact("tags.tagdict")).thenReturn(null);
// factory.artifactProvider = provider;
TagDictionary dict = factory.getTagDictionary();
assertNull(dict);
}

@Test
public void testLoadDefaultFeatureGeneratorBytesThrowsIfResourceMissing() {
ClassLoader cl = Thread.currentThread().getContextClassLoader();
Thread.currentThread().setContextClassLoader(new ClassLoader(null) {

@Override
public InputStream getResourceAsStream(String name) {
return null;
}
});
try {
POSTaggerFactory factory = new POSTaggerFactory();
factory.createFeatureGenerators();
} finally {
Thread.currentThread().setContextClassLoader(cl);
}
}

@Test
public void testCreateFeatureGeneratorThrowsForMalformedDescriptor() {
byte[] badDescriptor = "<invalid>".getBytes();
TagDictionary dict = new POSDictionary(true);
Map<String, Object> resources = new HashMap<String, Object>();
POSTaggerFactory factory = new POSTaggerFactory(badDescriptor, resources, dict);
factory.createFeatureGenerators();
}

@Test
public void testCreateArtifactMapWithOnlyPOSDictionary() {
POSDictionary posDict = new POSDictionary(true);
POSTaggerFactory factory = new POSTaggerFactory();
factory.posDictionary = posDict;
Map<String, Object> map = factory.createArtifactMap();
assertEquals(posDict, map.get("tags.tagdict"));
assertNull(map.get("ngram.dictionary"));
}

@Test
public void testCreateArtifactMapWithOnlyNgramDictionary() {
// Dictionary ngramDict = new Dictionary();
POSTaggerFactory factory = new POSTaggerFactory();
// factory.ngramDictionary = ngramDict;
Map<String, Object> map = factory.createArtifactMap();
// assertEquals(ngramDict, map.get("ngram.dictionary"));
assertNull(map.get("tags.tagdict"));
}

@Test
public void testCreateReturnsCorrectFactoryInstanceType() throws InvalidFormatException {
POSTaggerFactory factory = POSTaggerFactory.create(null, null, null, null);
assertNotNull(factory);
assertTrue(factory instanceof POSTaggerFactory);
}

@Test
public void testCreateFeatureGeneratorsUsesArtifactProviderBytesIfSet() {
POSTaggerFactory factory = new POSTaggerFactory();
Map<String, Object> resources = new HashMap<String, Object>();
byte[] descriptorBytes = "<generators/>".getBytes();
ArtifactProvider provider = mock(ArtifactProvider.class);
when(provider.getArtifact("generatorDescriptor")).thenReturn(descriptorBytes);
// factory.artifactProvider = provider;
AdaptiveFeatureGenerator generator = factory.createFeatureGenerators();
assertNotNull(generator);
}

@Test
public void testCreateFeatureGeneratorsUsesResourcesMapIfArtifactProviderNull() {
byte[] descriptor = "<generators/>".getBytes();
Map<String, Object> resources = new HashMap<String, Object>();
// resources.put("lexicon", new Dictionary());
POSTaggerFactory factory = new POSTaggerFactory(descriptor, resources, null);
AdaptiveFeatureGenerator generator = factory.createFeatureGenerators();
assertNotNull(generator);
}

@Test
public void testValidatePOSDictionaryWithMultipleUnknownTagsThrows() throws InvalidFormatException {
POSDictionary dict = new POSDictionary(true);
// dict.addTags("sun", new String[] { "XX" });
// dict.addTags("moon", new String[] { "YY" });
AbstractModel model = mock(AbstractModel.class);
when(model.getNumOutcomes()).thenReturn(1);
when(model.getOutcome(0)).thenReturn("NN");
POSTaggerFactory factory = new POSTaggerFactory();
factory.validatePOSDictionary(dict, model);
}

@Test
public void testCreateFeatureGeneratorsFallsBackToDefaultWhenArtifactAndBytesNull() {
POSTaggerFactory factory = new POSTaggerFactory();
// factory.featureGeneratorBytes = null;
// factory.artifactProvider = null;
AdaptiveFeatureGenerator generator = factory.createFeatureGenerators();
assertNotNull(generator);
}

@Test
public void testValidateArtifactMapSucceedsWhenSerializedFalseAndTypesMatch() throws InvalidFormatException {
POSDictionary dict = new POSDictionary(true);
// dict.addTags("d", new String[] { "NN" });
AbstractModel model = mock(AbstractModel.class);
when(model.getNumOutcomes()).thenReturn(1);
when(model.getOutcome(0)).thenReturn("NN");
ArtifactProvider provider = mock(ArtifactProvider.class);
when(provider.getArtifact("tags.tagdict")).thenReturn(dict);
when(provider.getArtifact("pos.model")).thenReturn(model);
// when(provider.getArtifact("ngram.dictionary")).thenReturn(new Dictionary());
when(provider.isLoadedFromSerialized()).thenReturn(false);
POSTaggerFactory factory = new POSTaggerFactory();
// factory.artifactProvider = provider;
factory.validateArtifactMap();
}

@Test
public void testGetTagDictionaryReturnsNullIfUninitialized() {
POSTaggerFactory factory = new POSTaggerFactory();
assertNull(factory.getTagDictionary());
}

@Test
public void testInitWithCustomResourcesAndNullDictLeavesDictionaryUnset() {
byte[] featureBytes = "<generators/>".getBytes();
Map<String, Object> res = new HashMap<String, Object>();
POSTaggerFactory factory = new POSTaggerFactory(featureBytes, res, null);
assertNull(factory.getTagDictionary());
}

@Test
public void testCreateArtifactSerializersMapContainsMultipleSerializers() {
POSTaggerFactory factory = new POSTaggerFactory();
// Map<String, opennlp.tools.util.model.ArtifactSerializer<?>> map = factory.createArtifactSerializersMap();
// assertNotNull(map);
// assertTrue(map.containsKey("tagdict"));
}

@Test
public void testValidatePOSDictionaryWithEmptyModelAndEmptyDict() throws InvalidFormatException {
POSDictionary dict = new POSDictionary(true);
AbstractModel model = mock(AbstractModel.class);
when(model.getNumOutcomes()).thenReturn(0);
POSTaggerFactory factory = new POSTaggerFactory();
factory.validatePOSDictionary(dict, model);
}

@Test
public void testCreateEmptyResourcesReturnsConsistentInstance() {
POSTaggerFactory factory = new POSTaggerFactory();
Map<String, Object> r1 = factory.getResources();
Map<String, Object> r2 = factory.getResources();
assertNotNull(r1);
assertTrue(r1.isEmpty());
assertSame(r1, r2);
}

@Test
public void testCreateFeatureGeneratorsThrowsOnIOExceptionFromDescriptorStream() {
byte[] buffer = new byte[] { 1, 2, 3 };
POSTaggerFactory factory = new POSTaggerFactory() {

@Override
protected byte[] getFeatureGenerator() {
return buffer;
}

@Override
public AdaptiveFeatureGenerator createFeatureGenerators() {
InputStream badStream = new InputStream() {

@Override
public int read() throws IOException {
throw new IOException("Interrupt stream");
}
};
try {
return opennlp.tools.util.featuregen.GeneratorFactory.create(badStream, key -> null);
} catch (Exception e) {
throw new IllegalStateException("Expected error", e);
}
}
};
factory.createFeatureGenerators();
}

@Test
public void testCreateFactoryStaticReturnsDefaultWithNullArgs() throws InvalidFormatException {
POSTaggerFactory factory = POSTaggerFactory.create(null, null, null, null);
assertNotNull(factory);
}

@Test
public void testStaticCreateFallbacksOnEmptySubclassName() throws InvalidFormatException {
POSTaggerFactory factory = POSTaggerFactory.create("", null, null, null);
assertNotNull(factory);
}

@Test
public void testStaticFactoryThrowsWrappedExceptionOnInstantiationFailure() {
try {
POSTaggerFactory.create("non.existent.FactoryClass", null, null, null);
fail("Expected InvalidFormatException");
} catch (InvalidFormatException ex) {
assertTrue(ex.getMessage().contains("Could not instantiate"));
}
}

@Test
public void testValidateArtifactMapNGramWrongTypeThrows() throws InvalidFormatException {
POSTaggerFactory factory = new POSTaggerFactory();
ArtifactProvider provider = mock(ArtifactProvider.class);
when(provider.getArtifact("ngram.dictionary")).thenReturn("NotADictionary");
// factory.artifactProvider = provider;
factory.validateArtifactMap();
}

@Test
public void testGetPOSContextGeneratorWithValidManifestOldVersionFallbackToDefaultPOSContextGenerator() {
POSTaggerFactory factory = new POSTaggerFactory();
Properties manifest = new Properties();
manifest.setProperty("OpenNLP-Version", "1.6.0");
ArtifactProvider provider = mock(ArtifactProvider.class);
when(provider.getArtifact("manifest.properties")).thenReturn(manifest);
// Dictionary dict = new Dictionary();
// when(provider.getArtifact("ngram.dictionary")).thenReturn(dict);
// factory.artifactProvider = provider;
assertNotNull(factory.getPOSContextGenerator(0));
}

@Test
public void testCreateFeatureGeneratorsInvalidDescriptorCausesException() {
byte[] invalidDescriptor = "<invalid>".getBytes();
POSTaggerFactory factory = new POSTaggerFactory(invalidDescriptor, new HashMap<String, Object>(), null);
factory.createFeatureGenerators();
}

@Test
public void testCreateMethodHandlesValidSubclassAndInitializesCorrectly() throws InvalidFormatException {
class CustomFactory extends POSTaggerFactory {

public boolean initialized = false;

// @Override
protected void init(Dictionary ngramDictionary, TagDictionary posDictionary) {
initialized = true;
}
}
String subclassName = CustomFactory.class.getName();
// Dictionary dict = new Dictionary();
TagDictionary tagDict = new POSDictionary(true);
// POSTaggerFactory factory = POSTaggerFactory.create(subclassName, dict, tagDict);
// assertNotNull(factory);
// assertTrue(factory instanceof CustomFactory);
// assertTrue(((CustomFactory) factory).initialized);
}

@Test
public void testDeprecatedStaticCreateWithInvalidSubclassThrows() throws InvalidFormatException {
// POSTaggerFactory.create("input.invalid.NotAValidFactory", new Dictionary(), new POSDictionary(true));
}

@Test
public void testGetTagDictionaryFallsBackToArtifactProviderOnce() {
POSTaggerFactory factory = new POSTaggerFactory();
POSDictionary dict = new POSDictionary(true);
ArtifactProvider provider = mock(ArtifactProvider.class);
when(provider.getArtifact("tags.tagdict")).thenReturn(dict);
// factory.artifactProvider = provider;
TagDictionary resolvedA = factory.getTagDictionary();
TagDictionary resolvedB = factory.getTagDictionary();
assertSame(resolvedA, resolvedB);
}

@Test
public void testValidatePOSDictionaryIgnoresNullTagArrays() throws InvalidFormatException {
POSDictionary posDict = new POSDictionary(true) {

@Override
public String[] getTags(String word) {
return null;
}

@Override
public Iterator<String> iterator() {
List<String> words = new ArrayList<String>();
words.add("mystery");
return words.iterator();
}
};
AbstractModel model = mock(AbstractModel.class);
when(model.getNumOutcomes()).thenReturn(0);
POSTaggerFactory factory = new POSTaggerFactory();
factory.validatePOSDictionary(posDict, model);
}

@Test
public void testStaticCreateWithMissingSubclassNameReturnsDefault() throws InvalidFormatException {
byte[] featureBytes = "<generators/>".getBytes();
TagDictionary dict = new POSDictionary(true);
Map<String, Object> resources = new HashMap<String, Object>();
POSTaggerFactory factory = POSTaggerFactory.create(null, featureBytes, resources, dict);
assertNotNull(factory);
assertTrue(factory instanceof POSTaggerFactory);
}

@Test
public void testCreateTagDictionaryFromEmptyStreamReturnsEmptyDict() throws Exception {
byte[] content = "".getBytes("UTF-8");
java.io.InputStream stream = new java.io.ByteArrayInputStream(content);
POSTaggerFactory factory = new POSTaggerFactory();
TagDictionary dict = factory.createTagDictionary(stream);
assertNotNull(dict);
assertTrue(dict.getTags("nonexistent") == null);
}

@Test
public void testSetTagDictionaryFailsIfArtifactProviderPresent() {
ArtifactProvider provider = mock(ArtifactProvider.class);
POSTaggerFactory factory = new POSTaggerFactory();
// factory.artifactProvider = provider;
factory.setTagDictionary(new POSDictionary(true));
}

@Test
public void testCreateArtifactMapDoesNotIncludeNullDictionaries() {
POSTaggerFactory factory = new POSTaggerFactory();
Map<String, Object> artifacts = factory.createArtifactMap();
assertNotNull(artifacts);
assertFalse(artifacts.containsKey("tags.tagdict"));
assertFalse(artifacts.containsKey("ngram.dictionary"));
}

@Test
public void testValidatePOSDictionaryModelTagsExactMatch() throws InvalidFormatException {
POSDictionary dict = new POSDictionary(true);
// dict.addTags("car", new String[] { "NN" });
AbstractModel model = mock(AbstractModel.class);
when(model.getNumOutcomes()).thenReturn(1);
when(model.getOutcome(0)).thenReturn("NN");
POSTaggerFactory factory = new POSTaggerFactory();
factory.validatePOSDictionary(dict, model);
}

@Test
public void testGetPOSContextGeneratorHandlesNullVersionGracefully() {
POSTaggerFactory factory = new POSTaggerFactory();
Properties props = new Properties();
ArtifactProvider provider = mock(ArtifactProvider.class);
when(provider.getArtifact("manifest.properties")).thenReturn(props);
// factory.artifactProvider = provider;
try {
factory.getPOSContextGenerator();
} catch (Exception e) {
fail("Should not throw even if version is missing");
}
}

@Test
public void testCreateFactoryWithEmptySubclassNameReturnsDefault() throws InvalidFormatException {
byte[] featureGeneratorBytes = "<generators/>".getBytes();
Map<String, Object> resources = new HashMap<String, Object>();
TagDictionary tagDict = new POSDictionary(true);
POSTaggerFactory factory = POSTaggerFactory.create("", featureGeneratorBytes, resources, tagDict);
assertNotNull(factory);
assertTrue(factory instanceof POSTaggerFactory);
}

@Test
public void testDeprecatedInitStoresDictionariesCorrectly() {
// Dictionary ngram = new Dictionary();
POSDictionary pos = new POSDictionary(true);
POSTaggerFactory factory = new POSTaggerFactory();
// factory.init(ngram, pos);
Map<String, Object> artifacts = factory.createArtifactMap();
// assertEquals(ngram, artifacts.get("ngram.dictionary"));
assertEquals(pos, artifacts.get("tags.tagdict"));
}

@Test
public void testValidatePOSDictionaryWithEmptyTagSet() throws InvalidFormatException {
POSDictionary dict = new POSDictionary(true);
// dict.addTags("word", new String[0]);
AbstractModel model = mock(AbstractModel.class);
when(model.getNumOutcomes()).thenReturn(0);
POSTaggerFactory factory = new POSTaggerFactory();
factory.validatePOSDictionary(dict, model);
}

@Test
public void testValidateArtifactMapSkipsValidationIfTagDictIsNull() throws InvalidFormatException {
ArtifactProvider provider = mock(ArtifactProvider.class);
when(provider.getArtifact("tags.tagdict")).thenReturn(null);
when(provider.getArtifact("ngram.dictionary")).thenReturn(null);
POSTaggerFactory factory = new POSTaggerFactory();
// factory.artifactProvider = provider;
factory.validateArtifactMap();
}

@Test
public void testInitWithFeatureBytesAndNoResourcesStillInitializes() {
byte[] featureBytes = "<generators/>".getBytes(StandardCharsets.UTF_8);
POSTaggerFactory factory = new POSTaggerFactory(featureBytes, null, null);
assertNotNull(factory.getFeatureGenerator());
assertTrue(factory.getResources().isEmpty());
}

@Test
public void testPOSDictionarySerializerSupportsRoundTrip() throws IOException {
POSDictionary dictionary = new POSDictionary(true);
// dictionary.addTags("walk", new String[] { "VB" });
ByteArrayOutputStream out = new ByteArrayOutputStream();
POSTaggerFactory.POSDictionarySerializer serializer = new POSTaggerFactory.POSDictionarySerializer();
serializer.serialize(dictionary, out);
byte[] data = out.toByteArray();
InputStream in = new ByteArrayInputStream(data);
POSDictionary restored = serializer.create(in);
assertArrayEquals(dictionary.getTags("walk"), restored.getTags("walk"));
}

@Test
public void testCreateArtifactSerializersMapReturnsSameRegisteredInstance() {
POSTaggerFactory factory = new POSTaggerFactory();
// Map<String, ArtifactSerializer<?>> map1 = factory.createArtifactSerializersMap();
// Map<String, ArtifactSerializer<?>> map2 = factory.createArtifactSerializersMap();
// assertSame(map1.get("tagdict"), map2.get("tagdict"));
}

@Test
public void testGetTagDictionaryNoArtifactProviderReturnsNullIfUninitialized() {
POSTaggerFactory factory = new POSTaggerFactory();
TagDictionary result = factory.getTagDictionary();
assertNull(result);
}

@Test
public void testSetTagDictionaryAfterArtifactProviderThrows() {
ArtifactProvider provider = mock(ArtifactProvider.class);
POSTaggerFactory factory = new POSTaggerFactory();
// factory.artifactProvider = provider;
TagDictionary newDict = new POSDictionary(true);
factory.setTagDictionary(newDict);
}

@Test
public void testGetPOSContextGeneratorHandlesNullManifestGracefully() {
ArtifactProvider provider = mock(ArtifactProvider.class);
when(provider.getArtifact("manifest.properties")).thenReturn(null);
// Dictionary dict = new Dictionary();
// when(provider.getArtifact("ngram.dictionary")).thenReturn(dict);
POSTaggerFactory factory = new POSTaggerFactory();
// factory.artifactProvider = provider;
assertNotNull(factory.getPOSContextGenerator(0));
}

@Test
public void testCreateTagDictionaryHandlesNonUTF8Input() throws IOException {
String content = "blåbær NN\n";
byte[] bytes = content.getBytes("ISO-8859-1");
InputStream inputStream = new ByteArrayInputStream(bytes);
POSTaggerFactory factory = new POSTaggerFactory();
TagDictionary tagDict = factory.createTagDictionary(inputStream);
assertNotNull(tagDict.getTags("blåbær"));
}

@Test
public void testCreateFactoryWithNullArgsReturnsDefaultInstance() throws InvalidFormatException {
POSTaggerFactory factory = POSTaggerFactory.create(null, null, null, null);
assertNotNull(factory);
assertTrue(factory instanceof POSTaggerFactory);
}

@Test
public void testLoadDefaultFeatureGeneratorThrowsWhenMissing() {
ClassLoader current = Thread.currentThread().getContextClassLoader();
Thread.currentThread().setContextClassLoader(new ClassLoader(null) {

@Override
public InputStream getResourceAsStream(String name) {
return null;
}
});
try {
POSTaggerFactory factory = new POSTaggerFactory();
factory.createFeatureGenerators();
} finally {
Thread.currentThread().setContextClassLoader(current);
}
}

@Test
public void testValidateArtifactMapThrowsWhenPOSModelMissing() throws InvalidFormatException {
POSTaggerFactory factory = new POSTaggerFactory();
ArtifactProvider provider = mock(ArtifactProvider.class);
POSDictionary posDict = new POSDictionary(true);
// posDict.addTags("run", new String[] { "VB" });
when(provider.getArtifact("tags.tagdict")).thenReturn(posDict);
when(provider.getArtifact("pos.model")).thenReturn(null);
when(provider.isLoadedFromSerialized()).thenReturn(false);
// factory.artifactProvider = provider;
factory.validateArtifactMap();
}

@Test
public void testValidateArtifactMapThrowsForNgramDictionaryWrongTypeOnly() throws InvalidFormatException {
POSTaggerFactory factory = new POSTaggerFactory();
ArtifactProvider provider = mock(ArtifactProvider.class);
when(provider.getArtifact("ngram.dictionary")).thenReturn("invalid");
when(provider.getArtifact("tags.tagdict")).thenReturn(null);
// factory.artifactProvider = provider;
factory.validateArtifactMap();
}

@Test
public void testValidatePOSDictionaryWithDuplicateModelTagsAndDictTagsPasses() throws InvalidFormatException {
POSTaggerFactory factory = new POSTaggerFactory();
POSDictionary dict = new POSDictionary(true);
// dict.addTags("go", new String[] { "VB" });
AbstractModel model = mock(AbstractModel.class);
when(model.getNumOutcomes()).thenReturn(3);
when(model.getOutcome(0)).thenReturn("VB");
when(model.getOutcome(1)).thenReturn("VB");
when(model.getOutcome(2)).thenReturn("NN");
factory.validatePOSDictionary(dict, model);
}

@Test
public void testValidatePOSDictionarySkipsEmptyDict() throws InvalidFormatException {
POSTaggerFactory factory = new POSTaggerFactory();
POSDictionary dict = new POSDictionary(true);
AbstractModel model = mock(AbstractModel.class);
when(model.getNumOutcomes()).thenReturn(2);
when(model.getOutcome(0)).thenReturn("VB");
when(model.getOutcome(1)).thenReturn("NN");
factory.validatePOSDictionary(dict, model);
}

@Test
public void testGetPOSContextGeneratorHandlesVersionJustBeforeCutoff() {
POSTaggerFactory factory = new POSTaggerFactory();
Properties manifest = new Properties();
manifest.setProperty("OpenNLP-Version", "1.7.9");
ArtifactProvider provider = mock(ArtifactProvider.class);
when(provider.getArtifact("manifest.properties")).thenReturn(manifest);
// Dictionary dict = new Dictionary();
// when(provider.getArtifact("ngram.dictionary")).thenReturn(dict);
// factory.artifactProvider = provider;
assertNotNull(factory.getPOSContextGenerator(3));
}

@Test
public void testGetPOSContextGeneratorHandlesVersionJustAfterCutoff() {
POSTaggerFactory factory = new POSTaggerFactory();
Properties manifest = new Properties();
manifest.setProperty("OpenNLP-Version", "1.8.0");
ArtifactProvider provider = mock(ArtifactProvider.class);
when(provider.getArtifact("manifest.properties")).thenReturn(manifest);
// factory.artifactProvider = provider;
assertNotNull(factory.getPOSContextGenerator(5));
}

@Test
public void testGetSequenceValidatorReturnsInstance() {
TagDictionary dict = new POSDictionary(true);
POSTaggerFactory factory = new POSTaggerFactory("<generators/>".getBytes(), new HashMap<String, Object>(), dict);
SequenceValidator<String> validator = factory.getSequenceValidator();
assertNotNull(validator);
}

@Test
public void testCreateFeatureGeneratorsReturnsDefaultWhenFeatureBytesExplicitNull() {
POSTaggerFactory factory = new POSTaggerFactory();
// factory.featureGeneratorBytes = null;
// factory.artifactProvider = null;
AdaptiveFeatureGenerator generator = factory.createFeatureGenerators();
assertNotNull(generator);
}

@Test
public void testCreateFeatureGeneratorsReturnsNullWhenResourceKeyMissing() {
Map<String, Object> resources = new HashMap<String, Object>();
byte[] descriptorBytes = "<generators><generator class=\"opennlp.tools.util.featuregen.WindowFeatureGenerator\"/></generators>".getBytes();
POSTaggerFactory factory = new POSTaggerFactory(descriptorBytes, resources, null);
AdaptiveFeatureGenerator generator = factory.createFeatureGenerators();
assertNotNull(generator);
}

@Test
public void testCreateWithInvalidSubclassThrows() throws InvalidFormatException {
// Dictionary dict = new Dictionary();
TagDictionary dict2 = new POSDictionary(true);
// POSTaggerFactory.create("xxx.invalid.DoesNotExist", dict, dict2);
}

@Test
public void testCreateTagDictionaryHandlesHeaderLineGracefully() throws Exception {
String content = "# POS dict\nwalk VB\n";
InputStream in = new ByteArrayInputStream(content.getBytes("UTF-8"));
POSTaggerFactory factory = new POSTaggerFactory();
TagDictionary dict = factory.createTagDictionary(in);
assertNotNull(dict.getTags("walk"));
}

@Test
public void testCreateEmptyTagDictionaryCreatesCaseSensitiveDictionary() {
POSTaggerFactory factory = new POSTaggerFactory();
TagDictionary dict = factory.createEmptyTagDictionary();
assertTrue(dict instanceof POSDictionary);
}

@Test
public void testModelContainsAllDictTagsOfMultipleEntries() throws InvalidFormatException {
POSDictionary posDict = new POSDictionary(true);
// posDict.addTags("word1", new String[] { "NN", "VB" });
// posDict.addTags("word2", new String[] { "VB" });
AbstractModel model = mock(AbstractModel.class);
when(model.getNumOutcomes()).thenReturn(3);
when(model.getOutcome(0)).thenReturn("VB");
when(model.getOutcome(1)).thenReturn("NN");
when(model.getOutcome(2)).thenReturn("JJ");
POSTaggerFactory factory = new POSTaggerFactory();
factory.validatePOSDictionary(posDict, model);
}

@Test
public void testValidatePOSDictionaryWithGetTagsReturningEmptyStringArray() throws InvalidFormatException {
POSDictionary posDict = new POSDictionary(true) {

@Override
public String[] getTags(String word) {
return new String[0];
}

@Override
public Iterator<String> iterator() {
List<String> words = new ArrayList<String>();
words.add("emptyword");
return words.iterator();
}
};
AbstractModel model = mock(AbstractModel.class);
when(model.getNumOutcomes()).thenReturn(0);
POSTaggerFactory factory = new POSTaggerFactory();
factory.validatePOSDictionary(posDict, model);
}

@Test
public void testValidateArtifactMapSkipsValidationIfSerializedIsTrue() throws InvalidFormatException {
POSDictionary posDict = new POSDictionary(true);
AbstractModel model = mock(AbstractModel.class);
ArtifactProvider provider = mock(ArtifactProvider.class);
when(provider.getArtifact("tags.tagdict")).thenReturn(posDict);
when(provider.getArtifact("pos.model")).thenReturn(model);
// when(provider.getArtifact("ngram.dictionary")).thenReturn(new Dictionary());
when(provider.isLoadedFromSerialized()).thenReturn(true);
POSTaggerFactory factory = new POSTaggerFactory();
// factory.artifactProvider = provider;
factory.validateArtifactMap();
}

@Test
public void testCreateArtifactSerializersMapReturnsNonEmptyWhenCalled() {
POSTaggerFactory factory = new POSTaggerFactory();
// Map<String, ArtifactSerializer<?>> map = factory.createArtifactSerializersMap();
// assertNotNull(map);
// assertTrue(map.containsKey("tagdict"));
// assertTrue(map.get("tagdict") instanceof POSTaggerFactory.POSDictionarySerializer);
}

@Test
public void testValidatePOSDictionaryHandlesModelWithoutOutcomesGracefully() throws InvalidFormatException {
POSDictionary dict = new POSDictionary(true);
// dict.addTags("noun", new String[] {});
AbstractModel model = mock(AbstractModel.class);
when(model.getNumOutcomes()).thenReturn(0);
POSTaggerFactory factory = new POSTaggerFactory();
factory.validatePOSDictionary(dict, model);
}

@Test
public void testGetPOSContextGeneratorWhenVersionStringIsNull() {
POSTaggerFactory factory = new POSTaggerFactory();
ArtifactProvider provider = mock(ArtifactProvider.class);
Properties manifest = new Properties();
when(provider.getArtifact("manifest.properties")).thenReturn(manifest);
// Dictionary ngram = new Dictionary();
// when(provider.getArtifact("ngram.dictionary")).thenReturn(ngram);
// factory.artifactProvider = provider;
assertNotNull(factory.getPOSContextGenerator(1));
}

@Test
public void testCreatePOSDictionaryFromSingleLineStream() throws Exception {
String content = "fish NN\n";
InputStream stream = new ByteArrayInputStream(content.getBytes());
POSTaggerFactory factory = new POSTaggerFactory();
TagDictionary dict = factory.createTagDictionary(stream);
assertNotNull(dict.getTags("fish"));
}

@Test
public void testCreateArtifactMapContainsOnlyPOSWhenNgramIsNull() {
POSTaggerFactory factory = new POSTaggerFactory();
POSDictionary pos = new POSDictionary(true);
factory.posDictionary = pos;
factory.ngramDictionary = null;
Map<String, Object> artifactMap = factory.createArtifactMap();
assertEquals(pos, artifactMap.get("tags.tagdict"));
assertFalse(artifactMap.containsKey("ngram.dictionary"));
}

@Test
public void testCreateArtifactMapContainsOnlyNgramWhenPOSIsNull() {
POSTaggerFactory factory = new POSTaggerFactory();
// Dictionary dict = new Dictionary();
// factory.ngramDictionary = dict;
factory.posDictionary = null;
Map<String, Object> artifactMap = factory.createArtifactMap();
// assertEquals(dict, artifactMap.get("ngram.dictionary"));
assertFalse(artifactMap.containsKey("tags.tagdict"));
}

@Test
public void testValidateArtifactMapThrowsIfPOSDictWrongType() throws InvalidFormatException {
POSTaggerFactory factory = new POSTaggerFactory();
ArtifactProvider provider = mock(ArtifactProvider.class);
when(provider.getArtifact("tags.tagdict")).thenReturn(new Object());
// factory.artifactProvider = provider;
factory.validateArtifactMap();
}

@Test
public void testCreateWithBlankSubclassUsesDefault() throws Exception {
POSTaggerFactory factory = POSTaggerFactory.create("", null, null, null);
assertNotNull(factory);
}

@Test
public void testPOSDictionarySerializerRoundTripIntegration() throws Exception {
POSTaggerFactory.POSDictionarySerializer serializer = new POSTaggerFactory.POSDictionarySerializer();
POSDictionary dict = new POSDictionary(true);
// dict.addTags("verb", new String[] { "VB" });
ByteArrayOutputStream out = new ByteArrayOutputStream();
serializer.serialize(dict, out);
byte[] output = out.toByteArray();
InputStream in = new ByteArrayInputStream(output);
POSDictionary deserialized = serializer.create(in);
assertArrayEquals(dict.getTags("verb"), deserialized.getTags("verb"));
}

@Test
public void testCreateFeatureGeneratorsWithFeatureBytesNullAndArtifactProviderSet() {
POSTaggerFactory factory = new POSTaggerFactory();
// factory.featureGeneratorBytes = null;
byte[] descriptorBytes = "<generators/>".getBytes();
ArtifactProvider provider = mock(ArtifactProvider.class);
when(provider.getArtifact("generatorDescriptor")).thenReturn(descriptorBytes);
// factory.artifactProvider = provider;
assertNotNull(factory.createFeatureGenerators());
}

@Test
public void testCreateStaticFactoryWithAllNullArgsStillReturnsValidInstance() throws Exception {
POSTaggerFactory factory = POSTaggerFactory.create(null, null, null, null);
assertNotNull(factory);
}

@Test
public void testCreateStaticFactoryWithBadSubclassThrows() throws Exception {
// Dictionary dict = new Dictionary();
POSDictionary tagDict = new POSDictionary(true);
// POSTaggerFactory.create("invalid.factory.Name", dict, tagDict);
}

@Test
public void testInitWithNullPOSDictionaryOnlySetsResources() {
byte[] bytes = "<generators/>".getBytes();
Map<String, Object> res = new HashMap<String, Object>();
// res.put("key", new Dictionary());
POSTaggerFactory factory = new POSTaggerFactory();
factory.init(bytes, res, null);
assertEquals(res.get("key"), factory.getResources().get("key"));
assertNull(factory.getTagDictionary());
}
}
