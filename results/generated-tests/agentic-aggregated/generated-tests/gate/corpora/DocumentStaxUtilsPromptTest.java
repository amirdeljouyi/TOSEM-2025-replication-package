package gate.corpora;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.junit.Test;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Factory;
import gate.FeatureMap;
import gate.annotation.AnnotationSetImpl;
import gate.relations.Relation;
import gate.relations.SimpleRelation;

public class DocumentStaxUtilsPromptTest {

  @Test
  public void readTextWithNodesReturnsTextAndOffsets() throws Exception {
    String xml = "<TextWithNodes>A<Node id='7'/>bc<![CDATA[<d>]]><Node id='9'/>E</TextWithNodes>";
    XMLStreamReader reader = XMLInputFactory.newFactory().createXMLStreamReader(new StringReader(xml));
    assertEquals(XMLStreamConstants.START_ELEMENT, reader.nextTag());
    Map<Integer, Long> offsets = new HashMap<Integer, Long>();

    String text = DocumentStaxUtils.readTextWithNodes(reader, offsets);

    assertEquals("Abc<d>E", text);
    assertEquals(Long.valueOf(1), offsets.get(Integer.valueOf(7)));
    assertEquals(Long.valueOf(6), offsets.get(Integer.valueOf(9)));
    assertEquals(XMLStreamConstants.END_ELEMENT, reader.getEventType());
    assertEquals("TextWithNodes", reader.getLocalName());
    reader.close();
  }

  @Test
  public void readTextWithNodesRejectsNodeWithoutId() throws Exception {
    XMLStreamReader reader = XMLInputFactory.newFactory().createXMLStreamReader(
            new StringReader("<TextWithNodes>x<Node/>y</TextWithNodes>"));
    reader.nextTag();
    try {
      DocumentStaxUtils.readTextWithNodes(reader, new HashMap<Integer, Long>());
      fail("Expected missing node ID to be rejected");
    } catch(XMLStreamException expected) {
      assertTrue(expected.getMessage().contains("no id"));
    }
    reader.close();
  }

  @Test
  public void readTextWithNodesRejectsNonIntegerAndNonEmptyNodes() throws Exception {
    XMLStreamReader nonInteger = XMLInputFactory.newFactory().createXMLStreamReader(
            new StringReader("<TextWithNodes><Node id='x'/></TextWithNodes>"));
    nonInteger.nextTag();
    try {
      DocumentStaxUtils.readTextWithNodes(nonInteger, new HashMap<Integer, Long>());
      fail("Expected non-integer node ID to be rejected");
    } catch(XMLStreamException expected) {
      assertTrue(expected.getMessage().contains("integer id"));
    }
    nonInteger.close();

    XMLStreamReader nonEmpty = XMLInputFactory.newFactory().createXMLStreamReader(
            new StringReader("<TextWithNodes><Node id='1'>bad</Node></TextWithNodes>"));
    nonEmpty.nextTag();
    try {
      DocumentStaxUtils.readTextWithNodes(nonEmpty, new HashMap<Integer, Long>());
      fail("Expected non-empty node to be rejected");
    } catch(XMLStreamException expected) {
      assertTrue(expected.getMessage().contains("must be empty"));
    }
    nonEmpty.close();
  }

  @Test
  public void readFeatureMapRestoresScalarAndCollectionTypes() throws Exception {
    String xml = "<Annotation>"
            + "<Feature><Name>word</Name><Value>hello</Value></Feature>"
            + "<Feature><Name>count</Name><Value className='java.lang.Integer'>42</Value></Feature>"
            + "<Feature><Name>flags</Name><Value className='java.util.ArrayList' "
            + "itemClassName='java.lang.Boolean'>true;false</Value></Feature>"
            + "</Annotation>";
    XMLStreamReader reader = XMLInputFactory.newFactory().createXMLStreamReader(new StringReader(xml));
    reader.nextTag();

    FeatureMap features = DocumentStaxUtils.readFeatureMap(reader);

    assertEquals("hello", features.get("word"));
    assertEquals(Integer.valueOf(42), features.get("count"));
    assertEquals(Arrays.asList(Boolean.TRUE, Boolean.FALSE), features.get("flags"));
    assertEquals("Annotation", reader.getLocalName());
    reader.close();
  }

  @Test
  public void readFeatureMapFallsBackToStringForUnknownClass() throws Exception {
    String xml = "<GateDocumentFeatures><Feature><Name>mystery</Name>"
            + "<Value className='missing.example.Type'>payload</Value></Feature></GateDocumentFeatures>";
    XMLStreamReader reader = XMLInputFactory.newFactory().createXMLStreamReader(new StringReader(xml));
    reader.nextTag();

    FeatureMap features = DocumentStaxUtils.readFeatureMap(reader);

    assertEquals("payload", features.get("mystery"));
    reader.close();
  }

  @Test
  public void readFeatureMapRejectsNestedValueElements() throws Exception {
    String xml = "<Annotation><Feature><Name>key</Name><Value>before<b/>after</Value></Feature></Annotation>";
    XMLStreamReader reader = XMLInputFactory.newFactory().createXMLStreamReader(new StringReader(xml));
    reader.nextTag();
    try {
      DocumentStaxUtils.readFeatureMap(reader);
      fail("Expected nested feature content to be rejected");
    } catch(XMLStreamException expected) {
      assertTrue(expected.getMessage().contains("Elements not allowed"));
    }
    reader.close();
  }

  @Test
  public void readAnnotationSetUsesNodeMapIdsAndFeatures() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("abcdef"));
    AnnotationSet set = new AnnotationSetImpl(document, "parsed");
    String xml = "<AnnotationSet Name='parsed'>"
            + "<Annotation Id='12' Type='Token' StartNode='20' EndNode='21'>"
            + "<Feature><Name>kind</Name><Value>word</Value></Feature></Annotation>"
            + "<Annotation Id='15' Type='Sentence' StartNode='20' EndNode='22'></Annotation>"
            + "</AnnotationSet>";
    XMLStreamReader reader = XMLInputFactory.newFactory().createXMLStreamReader(new StringReader(xml));
    reader.nextTag();
    Map<Integer, Long> nodes = new HashMap<Integer, Long>();
    nodes.put(Integer.valueOf(20), Long.valueOf(0));
    nodes.put(Integer.valueOf(21), Long.valueOf(2));
    nodes.put(Integer.valueOf(22), Long.valueOf(6));
    Set<Integer> ids = new HashSet<Integer>();

    Boolean style = DocumentStaxUtils.readAnnotationSet(reader, set, nodes, ids, null);

    assertEquals(Boolean.TRUE, style);
    assertEquals(2, set.size());
    assertEquals(Long.valueOf(0), set.get(Integer.valueOf(12)).getStartNode().getOffset());
    assertEquals(Long.valueOf(2), set.get(Integer.valueOf(12)).getEndNode().getOffset());
    assertEquals("word", set.get(Integer.valueOf(12)).getFeatures().get("kind"));
    assertTrue(ids.contains(Integer.valueOf(12)));
    assertTrue(ids.contains(Integer.valueOf(15)));
    reader.close();
  }

  @Test
  public void readAnnotationSetSupportsOldStyleWithoutIds() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("abcd"));
    AnnotationSet set = document.getAnnotations();
    String xml = "<AnnotationSet><Annotation Type='Chunk' StartNode='1' EndNode='3'>"
            + "</Annotation></AnnotationSet>";
    XMLStreamReader reader = XMLInputFactory.newFactory().createXMLStreamReader(new StringReader(xml));
    reader.nextTag();
    Set<Integer> explicitIds = new HashSet<Integer>();

    Boolean style = DocumentStaxUtils.readAnnotationSet(reader, set, null, explicitIds, null);

    assertEquals(Boolean.FALSE, style);
    assertEquals(1, set.size());
    assertEquals(Long.valueOf(1), set.iterator().next().getStartNode().getOffset());
    assertTrue(explicitIds.isEmpty());
    reader.close();
  }

  @Test
  public void readAnnotationSetRejectsMixedIdStylesAndDuplicates() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("abcd"));
    String mixedXml = "<AnnotationSet><Annotation Id='1' Type='A' StartNode='0' EndNode='1'/><Annotation "
            + "Type='B' StartNode='1' EndNode='2'/></AnnotationSet>";
    XMLStreamReader mixed = XMLInputFactory.newFactory().createXMLStreamReader(new StringReader(mixedXml));
    mixed.nextTag();
    try {
      DocumentStaxUtils.readAnnotationSet(mixed, document.getAnnotations(), null,
              new HashSet<Integer>(), null);
      fail("Expected mixed ID styles to be rejected");
    } catch(XMLStreamException expected) {
      assertTrue(expected.getMessage().contains("every annotation"));
    }
    mixed.close();

    String duplicateXml = "<AnnotationSet><Annotation Id='4' Type='A' StartNode='0' EndNode='1'/>"
            + "<Annotation Id='4' Type='B' StartNode='1' EndNode='2'/></AnnotationSet>";
    XMLStreamReader duplicate = XMLInputFactory.newFactory().createXMLStreamReader(new StringReader(duplicateXml));
    duplicate.nextTag();
    try {
      DocumentStaxUtils.readAnnotationSet(duplicate, document.getAnnotations("other"), null,
              new HashSet<Integer>(), null);
      fail("Expected duplicate IDs to be rejected");
    } catch(XMLStreamException expected) {
      assertTrue(expected.getMessage().contains("unique"));
    }
    duplicate.close();
  }

  @Test
  public void readAnnotationSetRejectsUnknownNodeAndBadNumbers() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("ab"));
    Map<Integer, Long> nodes = new HashMap<Integer, Long>();
    nodes.put(Integer.valueOf(0), Long.valueOf(0));
    XMLStreamReader unknownNode = XMLInputFactory.newFactory().createXMLStreamReader(new StringReader(
            "<AnnotationSet><Annotation Id='1' Type='A' StartNode='0' EndNode='9'/></AnnotationSet>"));
    unknownNode.nextTag();
    try {
      DocumentStaxUtils.readAnnotationSet(unknownNode, document.getAnnotations(), nodes,
              new HashSet<Integer>(), null);
      fail("Expected unknown end node to be rejected");
    } catch(XMLStreamException expected) {
      assertTrue(expected.getMessage().contains("Invalid end node ID"));
    }
    unknownNode.close();

    XMLStreamReader badNumber = XMLInputFactory.newFactory().createXMLStreamReader(new StringReader(
            "<AnnotationSet><Annotation Id='not-an-id' Type='A' StartNode='0' EndNode='1'/></AnnotationSet>"));
    badNumber.nextTag();
    try {
      DocumentStaxUtils.readAnnotationSet(badNumber, document.getAnnotations("bad"), null,
              new HashSet<Integer>(), null);
      fail("Expected non-integer annotation ID to be rejected");
    } catch(XMLStreamException expected) {
      assertTrue(expected.getMessage().contains("Non-integer annotation ID"));
    }
    badNumber.close();
  }

  @Test
  public void readGateXmlDocumentReplacesContentFeaturesAndSets() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("old content"));
    document.getFeatures().put("old", "value");
    document.getAnnotations().add(Long.valueOf(0), Long.valueOf(3), "Old", Factory.newFeatureMap());
    String xml = "<GateDocument version='3'><GateDocumentFeatures>"
            + "<Feature><Name>source</Name><Value>manual</Value></Feature></GateDocumentFeatures>"
            + "<TextWithNodes><Node id='0'/>New <Node id='1'/>text<Node id='2'/></TextWithNodes>"
            + "<AnnotationSet><Annotation Id='30' Type='First' StartNode='0' EndNode='1'/></AnnotationSet>"
            + "<AnnotationSet Name='extra'><Annotation Id='31' Type='Second' StartNode='1' EndNode='2'/></AnnotationSet>"
            + "</GateDocument>";
    XMLStreamReader reader = XMLInputFactory.newFactory().createXMLStreamReader(new StringReader(xml));
    reader.nextTag();

    DocumentStaxUtils.readGateXmlDocument(reader, document);

    assertEquals("New text", document.getContent().toString());
    assertEquals("manual", document.getFeatures().get("source"));
    assertNull(document.getFeatures().get("old"));
    assertEquals(1, document.getAnnotations().size());
    assertEquals("First", document.getAnnotations().get(Integer.valueOf(30)).getType());
    assertEquals("Second", document.getAnnotations("extra").get(Integer.valueOf(31)).getType());
    assertEquals("GateDocument", reader.getLocalName());
    assertEquals(XMLStreamConstants.END_ELEMENT, reader.getEventType());
    reader.close();
  }

  @Test
  public void readGateXmlDocumentRestoresContentAfterLateFailure() throws Exception {
    DocumentImpl document = new DocumentImpl();
    DocumentContentImpl original = new DocumentContentImpl("keep me");
    document.setContent(original);
    String xml = "<GateDocument><GateDocumentFeatures/><TextWithNodes>replacement<Node id='0'/></TextWithNodes>"
            + "<AnnotationSet><Annotation Id='1' Type='Bad' StartNode='0' EndNode='99'/></AnnotationSet>"
            + "</GateDocument>";
    XMLStreamReader reader = XMLInputFactory.newFactory().createXMLStreamReader(new StringReader(xml));
    reader.nextTag();
    try {
      DocumentStaxUtils.readGateXmlDocument(reader, document);
      fail("Expected invalid annotation node to fail");
    } catch(XMLStreamException expected) {
      assertTrue(expected.getMessage().contains("Invalid end node ID"));
    }
    assertSame(original, document.getContent());
    assertEquals("keep me", document.getContent().toString());
    reader.close();
  }

  @Test
  public void gateXmlRoundTripPreservesUnicodeAnnotationsAndFeatures() throws Exception {
    DocumentImpl source = new DocumentImpl();
    source.setEncoding("UTF-8");
    source.setContent(new DocumentContentImpl("Café 😀 end"));
    source.getFeatures().put("language", "fr");
    FeatureMap tokenFeatures = Factory.newFeatureMap();
    tokenFeatures.put("lemma", "café");
    source.getAnnotations().add(Integer.valueOf(8), Long.valueOf(0), Long.valueOf(4), "Token", tokenFeatures);
    source.getAnnotations("analysis").add(Integer.valueOf(11), Long.valueOf(5), Long.valueOf(7), "Emoji",
            Factory.newFeatureMap());

    String serialized = DocumentStaxUtils.toXml(source);
    DocumentImpl target = new DocumentImpl();
    target.setContent(new DocumentContentImpl("unused"));
    XMLStreamReader reader = XMLInputFactory.newFactory().createXMLStreamReader(new StringReader(serialized));
    reader.nextTag();
    DocumentStaxUtils.readGateXmlDocument(reader, target);

    assertEquals(source.getContent().toString(), target.getContent().toString());
    assertEquals("fr", target.getFeatures().get("language"));
    assertEquals("café", target.getAnnotations().get(Integer.valueOf(8)).getFeatures().get("lemma"));
    assertEquals(Long.valueOf(7), target.getAnnotations("analysis").get(Integer.valueOf(11)).getEndNode().getOffset());
    reader.close();
  }

  @Test
  public void writeTextWithNodesCombinesSetsAndSanitizesSplitSurrogate() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("A😀B"));
    AnnotationSet first = document.getAnnotations();
    AnnotationSet second = document.getAnnotations("named");
    first.add(Integer.valueOf(1), Long.valueOf(0), Long.valueOf(1), "A", Factory.newFeatureMap());
    second.add(Integer.valueOf(2), Long.valueOf(2), Long.valueOf(4), "B", Factory.newFeatureMap());
    Collection<Collection<Annotation>> sets = new ArrayList<Collection<Annotation>>();
    sets.add(first);
    sets.add(second);
    StringWriter output = new StringWriter();
    XMLStreamWriter writer = XMLOutputFactory.newFactory().createXMLStreamWriter(output);

    DocumentStaxUtils.writeTextWithNodes(document, sets, writer, "");
    writer.close();

    String xml = output.toString();
    assertTrue(xml.contains("<Node id=\"0\""));
    assertTrue(xml.contains("<Node id=\"1\""));
    assertTrue(xml.contains("<Node id=\"2\""));
    assertTrue(xml.contains("<Node id=\"4\""));
    assertFalse(xml.contains("😀"));
    assertTrue(xml.contains("id=\"1\"/> <Node id=\"2\""));
  }

  @Test
  public void writeFeaturesSkipsNullEntriesSanitizesValuesAndUsesCdata() throws Exception {
    FeatureMap features = Factory.newFeatureMap();
    features.put("normal", Integer.valueOf(7));
    features.put("illegal", "a\u0001b");
    features.put("markup", "<a><b><c><d><e>tail]]>done");
    features.put("null-value", null);
    StringWriter output = new StringWriter();
    XMLStreamWriter writer = XMLOutputFactory.newFactory().createXMLStreamWriter(output);
    writer.writeStartElement("features");

    DocumentStaxUtils.writeFeatures(features, writer, "");
    writer.writeEndElement();
    writer.close();

    String xml = output.toString();
    assertTrue(xml.contains("java.lang.Integer"));
    assertTrue(xml.contains("a b"));
    assertTrue(xml.contains("<![CDATA["));
    assertTrue(xml.contains("tail"));
    assertTrue(xml.contains("done"));
    assertFalse(xml.contains("null-value"));
    XMLStreamReader reader = XMLInputFactory.newFactory().createXMLStreamReader(new StringReader(xml));
    reader.nextTag();
    FeatureMap restored = DocumentStaxUtils.readFeatureMap(reader);
    assertEquals(Integer.valueOf(7), restored.get("normal"));
    assertEquals("a b", restored.get("illegal"));
    assertEquals("<a><b><c><d><e>tail]]>done", restored.get("markup"));
    reader.close();
  }

  @Test
  public void writeDocumentToStreamUsesNamespaceAndDoesNotCloseStream() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setEncoding("UTF-8");
    document.setContent(new DocumentContentImpl("stream content"));
    ByteArrayOutputStream output = new ByteArrayOutputStream();

    DocumentStaxUtils.writeDocument(document, output, "urn:test:gate");
    output.write('!');

    String xml = output.toString("UTF-8");
    assertTrue(xml.contains("UTF-8"));
    assertTrue(xml.contains("xmlns=\"urn:test:gate\""));
    assertTrue(xml.contains("stream content"));
    assertTrue(xml.endsWith("!"));
  }

  @Test
  public void writeDocumentFileOverloadsProduceParseableGateXml() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("file text"));
    document.getAnnotations().add(Integer.valueOf(5), Long.valueOf(0), Long.valueOf(4), "Part",
            Factory.newFeatureMap());
    File plain = File.createTempFile("document-stax-plain", ".xml");
    File namespaced = File.createTempFile("document-stax-ns", ".xml");
    plain.deleteOnExit();
    namespaced.deleteOnExit();

    DocumentStaxUtils.writeDocument(document, plain);
    DocumentStaxUtils.writeDocument(document, namespaced, "urn:file:test");

    String plainXml = new String(Files.readAllBytes(plain.toPath()), StandardCharsets.UTF_8);
    String namespacedXml = new String(Files.readAllBytes(namespaced.toPath()), StandardCharsets.UTF_8);
    assertTrue(plainXml.contains("<GateDocument"));
    assertTrue(plainXml.contains("Type=\"Part\""));
    assertTrue(namespacedXml.contains("xmlns=\"urn:file:test\""));
  }

  @Test
  public void relationSetRoundTripPreservesMembersFeaturesAndUserData() throws Exception {
    DocumentImpl source = new DocumentImpl();
    source.setContent(new DocumentContentImpl("abcd"));
    source.getAnnotations().add(Integer.valueOf(1), Long.valueOf(0), Long.valueOf(1), "Token", Factory.newFeatureMap());
    source.getAnnotations().add(Integer.valueOf(2), Long.valueOf(2), Long.valueOf(3), "Token", Factory.newFeatureMap());
    Relation relation = new SimpleRelation(9, "depends", new int[] {1, 2});
    relation.getFeatures().put("weight", Integer.valueOf(3));
    relation.setUserData("metadata");
    source.getAnnotations().getRelations().add(relation);

    String xml = DocumentStaxUtils.toXml(source);
    DocumentImpl target = new DocumentImpl();
    XMLStreamReader reader = XMLInputFactory.newFactory().createXMLStreamReader(new StringReader(xml));
    reader.nextTag();
    DocumentStaxUtils.readGateXmlDocument(reader, target);

    Relation restored = target.getAnnotations().getRelations().get(Integer.valueOf(9));
    assertNotNull(restored);
    assertEquals("depends", restored.getType());
    assertEquals(2, restored.getMembers().length);
    assertEquals(1, restored.getMembers()[0]);
    assertEquals(2, restored.getMembers()[1]);
    assertEquals(Integer.valueOf(3), restored.getFeatures().get("weight"));
    assertEquals("metadata", restored.getUserData());
    reader.close();
  }

  @Test
  public void readRelationSetRejectsMissingRequiredAttributes() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("a"));
    XMLStreamReader reader = XMLInputFactory.newFactory().createXMLStreamReader(new StringReader(
            "<RelationSet><Relation Id='1' Type='link'><UserData/></Relation></RelationSet>"));
    reader.nextTag();
    try {
      DocumentStaxUtils.readRelationSet(reader, document.getAnnotations().getRelations(), new HashSet<Integer>());
      fail("Expected missing members to be rejected");
    } catch(XMLStreamException expected) {
      assertTrue(expected.getMessage().contains("must have members"));
    }
    reader.close();
  }

  @Test
  public void readXcesInputStreamAddsIdentifiedAndGeneratedAnnotations() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("abcdefghij"));
    AnnotationSet annotations = document.getAnnotations();
    annotations.add(Integer.valueOf(2), Long.valueOf(8), Long.valueOf(9), "Existing", Factory.newFeatureMap());
    String xml = "<?xml version='1.0'?><!DOCTYPE cesAna [<!ELEMENT cesAna ANY>]>"
            + "<cesAna xmlns='http://www.xces.org/schema/2003' version='1.0'>"
            + "<struct type='Long' from='0' to='5' n='10'><feat name='label' value='outer'/></struct>"
            + "<struct type='Short' from='1' to='3'><feat name='label' value='inner'/></struct>"
            + "</cesAna>";
    ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));

    DocumentStaxUtils.readXces(input, annotations);

    assertEquals(3, annotations.size());
    assertEquals("Long", annotations.get(Integer.valueOf(10)).getType());
    assertEquals("outer", annotations.get(Integer.valueOf(10)).getFeatures().get("label"));
    Annotation generated = annotations.get("Short").iterator().next();
    assertEquals(Long.valueOf(1), generated.getStartNode().getOffset());
    assertEquals("inner", generated.getFeatures().get("label"));
    assertTrue(input.available() >= 0);
  }

  @Test
  public void readXcesRejectsDuplicateIdAndInvalidOffset() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("abc"));
    document.getAnnotations().add(Integer.valueOf(7), Long.valueOf(0), Long.valueOf(1), "Existing",
            Factory.newFeatureMap());
    String duplicate = "<cesAna xmlns='http://www.xces.org/schema/2003'>"
            + "<struct type='X' from='0' to='1' n='7'/></cesAna>";
    XMLStreamReader duplicateReader = XMLInputFactory.newFactory().createXMLStreamReader(new StringReader(duplicate));
    duplicateReader.nextTag();
    try {
      DocumentStaxUtils.readXces(duplicateReader, document.getAnnotations());
      fail("Expected duplicate XCES annotation ID to fail");
    } catch(XMLStreamException expected) {
      assertTrue(expected.getMessage().contains("unique"));
    }
    duplicateReader.close();

    String invalidOffset = "<cesAna xmlns='http://www.xces.org/schema/2003'>"
            + "<struct type='X' from='0' to='99' n='8'/></cesAna>";
    XMLStreamReader offsetReader = XMLInputFactory.newFactory().createXMLStreamReader(new StringReader(invalidOffset));
    offsetReader.nextTag();
    try {
      DocumentStaxUtils.readXces(offsetReader, document.getAnnotations());
      fail("Expected invalid XCES annotation offset to fail");
    } catch(XMLStreamException expected) {
      assertTrue(expected.getMessage().contains("Invalid offset"));
    }
    offsetReader.close();
  }

  @Test
  public void writeXcesAnnotationsSortsLongestFirstAndCanOmitIds() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("0123456789"));
    AnnotationSet annotations = document.getAnnotations();
    annotations.add(Integer.valueOf(8), Long.valueOf(2), Long.valueOf(5), "Short", Factory.newFeatureMap());
    FeatureMap longFeatures = Factory.newFeatureMap();
    longFeatures.put("kind", "long");
    longFeatures.put("isEmptyAndSpan", Boolean.TRUE);
    annotations.add(Integer.valueOf(3), Long.valueOf(2), Long.valueOf(9), "Long", longFeatures);
    StringWriter output = new StringWriter();
    XMLStreamWriter writer = XMLOutputFactory.newFactory().createXMLStreamWriter(output);

    DocumentStaxUtils.writeXcesAnnotations(annotations, writer, false);
    writer.close();

    String xml = output.toString();
    assertTrue(xml.indexOf("type=\"Long\"") < xml.indexOf("type=\"Short\""));
    assertFalse(xml.contains(" n=\""));
    assertTrue(xml.contains("name=\"kind\""));
    assertFalse(xml.contains("isEmptyAndSpan"));
    assertTrue(xml.contains("version=\"1.0\""));
  }

  @Test
  public void xcesAnnotationStreamRoundTripPreservesIdsAndSanitizesFeatureValues() throws Exception {
    DocumentImpl source = new DocumentImpl();
    source.setContent(new DocumentContentImpl("abcdef"));
    FeatureMap features = Factory.newFeatureMap();
    features.put("value", "left\u0002right");
    source.getAnnotations().add(Integer.valueOf(14), Long.valueOf(1), Long.valueOf(5), "Span", features);
    ByteArrayOutputStream output = new ByteArrayOutputStream();

    DocumentStaxUtils.writeXcesAnnotations(source.getAnnotations(), output, "UTF-8");
    String xml = output.toString("UTF-8");
    DocumentImpl target = new DocumentImpl();
    target.setContent(new DocumentContentImpl("abcdef"));
    DocumentStaxUtils.readXces(new ByteArrayInputStream(output.toByteArray()), target.getAnnotations());

    assertTrue(xml.contains("n=\"14\""));
    assertFalse(xml.contains("\u0002"));
    assertEquals("left right", target.getAnnotations().get(Integer.valueOf(14)).getFeatures().get("value"));
  }

  @Test
  public void writeXcesContentHonorsEncodingAndLeavesStreamOpen() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("café \u0001"));
    ByteArrayOutputStream output = new ByteArrayOutputStream();

    DocumentStaxUtils.writeXcesContent(document, output, "UTF-16LE");
    output.write('!');

    byte[] bytes = output.toByteArray();
    String decodedContent = new String(bytes, 0, bytes.length - 1, StandardCharsets.UTF_16LE);
    assertEquals("café \u0001", decodedContent);
    assertEquals((byte)'!', bytes[bytes.length - 1]);
  }

  @Test
  public void writeAnnotationSetHandlesNullCollectionAndExplicitName() throws Exception {
    StringWriter output = new StringWriter();
    XMLStreamWriter writer = XMLOutputFactory.newFactory().createXMLStreamWriter(output);

    DocumentStaxUtils.writeAnnotationSet((Collection<Annotation>)null, "empty-set", writer, "");
    writer.close();

    String xml = output.toString();
    assertTrue(xml.contains("AnnotationSet"));
    assertTrue(xml.contains("Name=\"empty-set\""));
    XMLStreamReader reader = XMLInputFactory.newFactory().createXMLStreamReader(new StringReader(xml));
    assertEquals(XMLStreamConstants.START_ELEMENT, reader.nextTag());
    assertEquals("empty-set", reader.getAttributeValue(null, "Name"));
    assertEquals(XMLStreamConstants.END_ELEMENT, reader.nextTag());
    reader.close();
  }

  @Test
  public void writeDocumentCanSelectSpecificAnnotationCollections() throws Exception {
    DocumentImpl document = new DocumentImpl();
    document.setContent(new DocumentContentImpl("abcdef"));
    AnnotationSet defaults = document.getAnnotations();
    AnnotationSet named = document.getAnnotations("all");
    defaults.add(Integer.valueOf(1), Long.valueOf(0), Long.valueOf(1), "Keep", Factory.newFeatureMap());
    defaults.add(Integer.valueOf(2), Long.valueOf(1), Long.valueOf(2), "Drop", Factory.newFeatureMap());
    named.add(Integer.valueOf(3), Long.valueOf(2), Long.valueOf(3), "Named", Factory.newFeatureMap());
    Map<String, Collection<Annotation>> selected = new LinkedHashMap<String, Collection<Annotation>>();
    selected.put(null, defaults.get("Keep"));
    selected.put("all", named);
    StringWriter output = new StringWriter();
    XMLStreamWriter writer = XMLOutputFactory.newFactory().createXMLStreamWriter(output);

    DocumentStaxUtils.writeDocument(document, selected, writer, "");
    writer.close();

    String xml = output.toString();
    assertTrue(xml.contains("Type=\"Keep\""));
    assertFalse(xml.contains("Type=\"Drop\""));
    assertTrue(xml.contains("Type=\"Named\""));
    assertTrue(xml.contains("Name=\"all\""));
  }
}
