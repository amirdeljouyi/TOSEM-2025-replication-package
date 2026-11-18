package opennlp.tools.postag;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import opennlp.tools.util.BaseToolFactory;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.SequenceValidator;
import opennlp.tools.util.featuregen.AdaptiveFeatureGenerator;
import opennlp.tools.util.model.ArtifactProvider;
import opennlp.tools.util.model.ArtifactSerializer;

public class POSTaggerFactoryPromptTest {

  @Test
  public void defaultFactoryStartsWithoutTagDictionaryOrArtifacts() {
    POSTaggerFactory factory = new POSTaggerFactory();

    assertNull(factory.getTagDictionary());
    assertTrue(factory.createArtifactMap().isEmpty());
    assertNotSame(factory.createArtifactMap(), factory.createArtifactMap());
  }

  @Test
  public void constructorPublishesProvidedTagDictionary() {
    POSDictionary dictionary = new POSDictionary();
    dictionary.put("bank", "NN", "VB");
    byte[] descriptor = ("<featureGenerators>"
        + "<generator class=\"opennlp.tools.util.featuregen.TokenFeatureGeneratorFactory\"/>"
        + "</featureGenerators>").getBytes(StandardCharsets.UTF_8);
    POSTaggerFactory factory = new POSTaggerFactory(descriptor,
        Collections.singletonMap("unused", "resource"), dictionary);

    assertSame(dictionary, factory.getTagDictionary());
    assertSame(dictionary, factory.createArtifactMap().get("tags.tagdict"));
    assertEquals(1, factory.createArtifactMap().size());
  }

  @Test
  public void setTagDictionaryCanSetAndReplaceDictionary() {
    POSTaggerFactory factory = new POSTaggerFactory();
    POSDictionary first = new POSDictionary();
    POSDictionary second = new POSDictionary(false);
    first.put("lead", "NN");
    second.put("Lead", "VB");

    factory.setTagDictionary(first);
    assertSame(first, factory.getTagDictionary());
    factory.setTagDictionary(second);

    assertSame(second, factory.getTagDictionary());
    assertArrayEquals(new String[] {"VB"}, factory.getTagDictionary().getTags("LEAD"));
  }

  @Test
  public void createEmptyTagDictionaryReturnsAndInstallsCaseSensitiveDictionary() {
    POSTaggerFactory factory = new POSTaggerFactory();

    TagDictionary dictionary = factory.createEmptyTagDictionary();

    assertSame(dictionary, factory.getTagDictionary());
    assertTrue(dictionary.isCaseSensitive());
    assertNull(dictionary.getTags("missing"));
    assertSame(dictionary, factory.createArtifactMap().get("tags.tagdict"));
  }

  @Test
  public void artifactSerializerMapContainsUsableTagDictionarySerializer() throws Exception {
    POSTaggerFactory factory = new POSTaggerFactory();
    Map<String, ArtifactSerializer> serializers = factory.createArtifactSerializersMap();
    POSDictionary original = new POSDictionary(false);
    original.put("MiXeD", "JJ", "NN");
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    @SuppressWarnings("unchecked")
    ArtifactSerializer<POSDictionary> serializer =
        (ArtifactSerializer<POSDictionary>) serializers.get("tagdict");

    serializer.serialize(original, output);
    output.write(' ');
    byte[] serialized = output.toByteArray();
    POSDictionary restored = serializer.create(
        new ByteArrayInputStream(serialized, 0, serialized.length - 1));

    assertEquals(1, serializers.size());
    assertNotNull(serializer);
    assertFalse(restored.isCaseSensitive());
    assertArrayEquals(new String[] {"JJ", "NN"}, restored.getTags("mixed"));
    assertArrayEquals(new String[] {"JJ", "NN"}, restored.getTags("MIXED"));
  }

  @Test
  public void createTagDictionaryFromInputStreamRoundTripsEntries() throws Exception {
    POSDictionary source = new POSDictionary();
    source.put("run", "VB", "NN");
    source.put("quickly", "RB");
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    source.serialize(output);
    POSTaggerFactory factory = new POSTaggerFactory();

    TagDictionary parsed = factory.createTagDictionary(
        new ByteArrayInputStream(output.toByteArray()));

    assertTrue(parsed.isCaseSensitive());
    assertArrayEquals(new String[] {"VB", "NN"}, parsed.getTags("run"));
    assertArrayEquals(new String[] {"RB"}, parsed.getTags("quickly"));
    assertNull(parsed.getTags("RUN"));
  }

  @Test
  public void createTagDictionaryFromFileRoundTripsEntries() throws Exception {
    POSDictionary source = new POSDictionary(false);
    source.put("Apple", "NNP");
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    source.serialize(output);
    File file = Files.createTempFile("pos-dictionary-", ".xml").toFile();
    Files.write(file.toPath(), output.toByteArray());
    POSTaggerFactory factory = new POSTaggerFactory();

    TagDictionary parsed = factory.createTagDictionary(file);

    assertFalse(parsed.isCaseSensitive());
    assertArrayEquals(new String[] {"NNP"}, parsed.getTags("APPLE"));
    assertTrue(file.delete());
  }

  @Test
  public void createTagDictionaryRejectsMissingFile() {
    POSTaggerFactory factory = new POSTaggerFactory();
    File missing = new File("definitely-missing-pos-dictionary-37f4f81.xml");

    assertThrows(FileNotFoundException.class, () -> factory.createTagDictionary(missing));
  }

  @Test
  public void createTagDictionaryRejectsMalformedContent() {
    POSTaggerFactory factory = new POSTaggerFactory();
    ByteArrayInputStream malformed = new ByteArrayInputStream(
        "not a serialized dictionary".getBytes(StandardCharsets.UTF_8));

    assertThrows(IOException.class, () -> factory.createTagDictionary(malformed));
  }

  @Test
  public void sequenceValidatorWithoutDictionaryAcceptsEveryOutcome() {
    POSTaggerFactory factory = new POSTaggerFactory();
    SequenceValidator<String> validator = factory.getSequenceValidator();

    assertTrue(validator.validSequence(0, new String[] {"anything"},
        new String[0], "ANY-TAG"));
  }

  @Test
  public void sequenceValidatorUsesInstalledDictionaryAcrossTokens() {
    POSDictionary dictionary = new POSDictionary();
    dictionary.put("flies", "NNS", "VBZ");
    dictionary.put("fast", "RB");
    POSTaggerFactory factory = new POSTaggerFactory(null, new HashMap<>(), dictionary);
    SequenceValidator<String> validator = factory.getSequenceValidator();
    String[] sentence = {"flies", "fast", "today"};

    assertTrue(validator.validSequence(0, sentence, new String[0], "NNS"));
    assertTrue(validator.validSequence(0, sentence, new String[0], "VBZ"));
    assertFalse(validator.validSequence(0, sentence, new String[0], "JJ"));
    assertTrue(validator.validSequence(1, sentence, new String[] {"VBZ"}, "RB"));
    assertFalse(validator.validSequence(1, sentence, new String[] {"VBZ"}, "NN"));
    assertTrue(validator.validSequence(2, sentence, new String[] {"VBZ", "RB"}, "NN"));
  }

  @Test
  public void explicitFeatureDescriptorProducesTokenFeaturesAndFreshGenerators() {
    byte[] descriptor = ("<featureGenerators>"
        + "<generator class=\"opennlp.tools.util.featuregen.TokenFeatureGeneratorFactory\">"
        + "<bool name=\"lowercase\">false</bool>"
        + "</generator></featureGenerators>").getBytes(StandardCharsets.UTF_8);
    POSTaggerFactory factory = new POSTaggerFactory(descriptor, new HashMap<>(), null);

    AdaptiveFeatureGenerator first = factory.createFeatureGenerators();
    AdaptiveFeatureGenerator second = factory.createFeatureGenerators();
    POSContextGenerator contextGenerator = factory.getPOSContextGenerator(2);
    String[] context = contextGenerator.getContext(0, new String[] {"MixedCase"},
        new String[0], null);

    assertNotNull(first);
    assertNotNull(second);
    assertNotSame(first, second);
    assertArrayEquals(new String[] {"w=MixedCase"}, context);
  }

  @Test
  public void defaultFeatureDescriptorSupportsContextGeneration() {
    POSTaggerFactory factory = new POSTaggerFactory();

    AdaptiveFeatureGenerator generator = factory.createFeatureGenerators();
    POSContextGenerator defaultContextGenerator = factory.getPOSContextGenerator();
    POSContextGenerator uncachedContextGenerator = factory.getPOSContextGenerator(0);
    String[] context = defaultContextGenerator.getContext(1,
        new String[] {"A", "test", "."}, new String[] {"DT"}, null);

    assertNotNull(generator);
    assertNotNull(defaultContextGenerator);
    assertNotNull(uncachedContextGenerator);
    assertTrue(context.length > 0);
    assertNotSame(defaultContextGenerator, uncachedContextGenerator);
  }

  @Test
  public void malformedFeatureDescriptorIsReportedAsIllegalState() {
    POSTaggerFactory factory = new POSTaggerFactory(
        "<featureGenerators>".getBytes(StandardCharsets.UTF_8), new HashMap<>(), null);

    IllegalStateException failure = assertThrows(IllegalStateException.class,
        factory::createFeatureGenerators);

    assertTrue(failure.getCause() instanceof InvalidFormatException);
  }

  @Test
  public void emptyFeatureDescriptorIsReportedAsIllegalState() {
    byte[] descriptor = "<featureGenerators/>".getBytes(StandardCharsets.UTF_8);
    POSTaggerFactory factory = new POSTaggerFactory(descriptor, new HashMap<>(), null);

    IllegalStateException failure = assertThrows(IllegalStateException.class,
        factory::createFeatureGenerators);

    assertTrue(failure.getCause() instanceof InvalidFormatException);
    assertTrue(failure.getCause().getMessage().contains("one or more generators"));
  }

  @Test
  public void staticCreateWithNullSubclassInitializesAllInputs() throws Exception {
    POSDictionary dictionary = new POSDictionary();
    dictionary.put("books", "NNS");
    byte[] descriptor = ("<featureGenerators>"
        + "<generator class=\"opennlp.tools.util.featuregen.TokenFeatureGeneratorFactory\"/>"
        + "</featureGenerators>").getBytes(StandardCharsets.UTF_8);
    Map<String, Object> resources = new HashMap<>();
    resources.put("marker", "value");

    POSTaggerFactory factory = POSTaggerFactory.create(null, descriptor, resources, dictionary);

    assertEquals(POSTaggerFactory.class, factory.getClass());
    assertSame(dictionary, factory.getTagDictionary());
    assertArrayEquals(new String[] {"w=books"}, factory.getPOSContextGenerator()
        .getContext(0, new String[] {"books"}, new String[0], null));
  }

  @Test
  public void staticCreateLoadsNamedFactoryClass() throws Exception {
    POSDictionary dictionary = new POSDictionary();
    byte[] descriptor = ("<featureGenerators>"
        + "<generator class=\"opennlp.tools.util.featuregen.TokenFeatureGeneratorFactory\"/>"
        + "</featureGenerators>").getBytes(StandardCharsets.UTF_8);

    POSTaggerFactory factory = POSTaggerFactory.create(POSTaggerFactory.class.getName(),
        descriptor, Collections.emptyMap(), dictionary);

    assertEquals(POSTaggerFactory.class, factory.getClass());
    assertSame(dictionary, factory.getTagDictionary());
    assertNotNull(factory.createFeatureGenerators());
  }

  @Test
  public void staticCreateWrapsUnknownClassFailure() {
    InvalidFormatException failure = assertThrows(InvalidFormatException.class,
        () -> POSTaggerFactory.create("no.such.Factory", new byte[] {1},
            Collections.emptyMap(), null));

    assertTrue(failure.getMessage().contains("no.such.Factory"));
    assertNotNull(failure.getCause());
  }

  @Test
  public void staticCreateWrapsWrongTypeFailure() {
    InvalidFormatException failure = assertThrows(InvalidFormatException.class,
        () -> POSTaggerFactory.create(String.class.getName(), new byte[] {1},
            Collections.emptyMap(), null));

    assertTrue(failure.getMessage().contains(String.class.getName()));
    assertNotNull(failure.getCause());
  }

  @Test
  public void validateArtifactMapAcceptsSerializedValidArtifacts() throws Exception {
    POSDictionary dictionary = new POSDictionary();
    dictionary.put("word", "NN");
    Map<String, Object> artifacts = new HashMap<>();
    artifacts.put("tags.tagdict", dictionary);
    ArtifactProvider provider = (ArtifactProvider) Proxy.newProxyInstance(
        ArtifactProvider.class.getClassLoader(), new Class<?>[] {ArtifactProvider.class},
        (proxy, method, arguments) -> {
          if (method.getName().equals("getArtifact")) {
            return artifacts.get(arguments[0]);
          }
          if (method.getName().equals("isLoadedFromSerialized")) {
            return true;
          }
          if (method.getName().equals("getLanguage")) {
            return "en";
          }
          return null;
        });
    POSTaggerFactory factory = (POSTaggerFactory) BaseToolFactory.create(
        POSTaggerFactory.class, provider);

    factory.validateArtifactMap();

    assertSame(dictionary, factory.getTagDictionary());
  }

  @Test
  public void validateArtifactMapRejectsWrongTagDictionaryType() throws Exception {
    Map<String, Object> artifacts = new HashMap<>();
    artifacts.put("tags.tagdict", "not-a-dictionary");
    ArtifactProvider provider = (ArtifactProvider) Proxy.newProxyInstance(
        ArtifactProvider.class.getClassLoader(), new Class<?>[] {ArtifactProvider.class},
        (proxy, method, arguments) -> {
          if (method.getName().equals("getArtifact")) {
            return artifacts.get(arguments[0]);
          }
          if (method.getName().equals("isLoadedFromSerialized")) {
            return true;
          }
          return null;
        });
    POSTaggerFactory factory = (POSTaggerFactory) BaseToolFactory.create(
        POSTaggerFactory.class, provider);

    InvalidFormatException failure = assertThrows(InvalidFormatException.class,
        factory::validateArtifactMap);

    assertEquals("POSTag dictionary has wrong type!", failure.getMessage());
  }

  @Test
  public void validateArtifactMapRejectsWrongNgramDictionaryType() throws Exception {
    Map<String, Object> artifacts = new HashMap<>();
    artifacts.put("ngram.dictionary", Integer.valueOf(42));
    ArtifactProvider provider = (ArtifactProvider) Proxy.newProxyInstance(
        ArtifactProvider.class.getClassLoader(), new Class<?>[] {ArtifactProvider.class},
        (proxy, method, arguments) -> {
          if (method.getName().equals("getArtifact")) {
            return artifacts.get(arguments[0]);
          }
          if (method.getName().equals("isLoadedFromSerialized")) {
            return true;
          }
          return null;
        });
    POSTaggerFactory factory = (POSTaggerFactory) BaseToolFactory.create(
        POSTaggerFactory.class, provider);

    InvalidFormatException failure = assertThrows(InvalidFormatException.class,
        factory::validateArtifactMap);

    assertEquals("NGram dictionary has wrong type!", failure.getMessage());
  }

  @Test
  public void artifactBackedFactoryRejectsManualTagDictionaryReplacement() throws Exception {
    ArtifactProvider provider = (ArtifactProvider) Proxy.newProxyInstance(
        ArtifactProvider.class.getClassLoader(), new Class<?>[] {ArtifactProvider.class},
        (proxy, method, arguments) -> {
          if (method.getName().equals("isLoadedFromSerialized")) {
            return true;
          }
          return null;
        });
    POSTaggerFactory factory = (POSTaggerFactory) BaseToolFactory.create(
        POSTaggerFactory.class, provider);

    IllegalStateException failure = assertThrows(IllegalStateException.class,
        () -> factory.setTagDictionary(new POSDictionary()));

    assertEquals("Can not set tag dictionary while using artifact provider.",
        failure.getMessage());
  }
}
