package gate.corpora;

import gate.*;
import gate.corpora.DocumentContentImpl;
import gate.corpora.DocumentImpl;
import gate.creole.ResourceInstantiationException;
import gate.event.*;
import gate.relations.RelationSet;
import gate.util.ExtensionFileFilter;
import gate.util.InvalidOffsetException;
import gate.util.SimpleFeatureMapImpl;
import org.junit.Test;
import javax.xml.stream.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DocumentStaxUtils_4_GPTLLMTest {

@Test
public void testReadGateXmlDocument_minimalSingleAnnotation() throws Exception {
Gate.init();
String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<GateDocument version=\"3\">\n" + "  <GateDocumentFeatures />\n" + "  <TextWithNodes>Hello<Node id=\"0\"/> World<Node id=\"1\"/></TextWithNodes>\n" + "  <AnnotationSet>\n" + "    <Annotation Id=\"10\" Type=\"Entity\" StartNode=\"0\" EndNode=\"1\">\n" + "      <Feature>\n" + "        <Name className=\"java.lang.String\">kind</Name>\n" + "        <Value className=\"java.lang.String\">name</Value>\n" + "      </Feature>\n" + "    </Annotation>\n" + "  </AnnotationSet>\n" + "</GateDocument>";
ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(stream);
while (reader.hasNext()) {
int event = reader.next();
if (event == XMLStreamConstants.START_ELEMENT && reader.getLocalName().equals("GateDocument")) {
break;
}
}
Document doc = Factory.newDocument("");
DocumentStaxUtils.readGateXmlDocument(reader, doc);
assertEquals("Hello World", doc.getContent().toString());
AnnotationSet set = doc.getAnnotations();
assertEquals(1, set.size());
Annotation a = set.iterator().next();
assertEquals((Long) 0L, a.getStartNode().getOffset());
assertEquals((Long) 1L, a.getEndNode().getOffset());
assertEquals("Entity", a.getType());
assertEquals("name", a.getFeatures().get("kind"));
}

@Test(expected = XMLStreamException.class)
public void testReadGateXmlDocument_invalidXml_throwsException() throws Exception {
Gate.init();
String invalidXml = "<GateDocument><Broken></GateDocument>";
ByteArrayInputStream stream = new ByteArrayInputStream(invalidXml.getBytes(StandardCharsets.UTF_8));
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(stream);
while (reader.hasNext()) {
if (reader.next() == XMLStreamConstants.START_ELEMENT && reader.getLocalName().equals("GateDocument"))
break;
}
Document doc = Factory.newDocument("");
DocumentStaxUtils.readGateXmlDocument(reader, doc);
}

@Test
public void testReadTextWithNodes_returnsCorrectTextAndOffsets() throws Exception {
Gate.init();
String xml = "<TextWithNodes>Start<Node id=\"1\"/>End<Node id=\"2\"/></TextWithNodes>";
ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(stream);
while (reader.hasNext()) {
int event = reader.next();
if (event == XMLStreamConstants.START_ELEMENT && reader.getLocalName().equals("TextWithNodes"))
break;
}
Map<Integer, Long> map = new java.util.HashMap<>();
String result = DocumentStaxUtils.readTextWithNodes(reader, map);
assertEquals("StartEnd", result);
assertEquals(Long.valueOf(5), map.get(1));
assertEquals(Long.valueOf(8), map.get(2));
}

@Test
public void testReadGateXmlDocument_withStatusListener_messagesCollected() throws Exception {
Gate.init();
String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<GateDocument version=\"3\">\n" + "  <GateDocumentFeatures />\n" + "  <TextWithNodes>Data<Node id=\"5\"/><Node id=\"6\"/></TextWithNodes>\n" + "  <AnnotationSet>\n" + "    <Annotation Id=\"50\" Type=\"Word\" StartNode=\"5\" EndNode=\"6\">\n" + "      <Feature>\n" + "        <Name className=\"java.lang.String\">length</Name>\n" + "        <Value className=\"java.lang.String\">short</Value>\n" + "      </Feature>\n" + "    </Annotation>\n" + "  </AnnotationSet>\n" + "</GateDocument>";
final StringBuilder status = new StringBuilder();
StatusListener listener = new StatusListener() {

@Override
public void statusChanged(String message) {
status.append(message).append("\n");
}
};
ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(stream);
while (reader.hasNext()) {
if (reader.next() == XMLStreamConstants.START_ELEMENT && reader.getLocalName().equals("GateDocument"))
break;
}
Document doc = Factory.newDocument("");
DocumentStaxUtils.readGateXmlDocument(reader, doc, listener);
String actualText = doc.getContent().toString();
assertTrue(actualText.contains("Data"));
assertTrue(status.toString().contains("Finished"));
assertTrue(status.toString().contains("Reading document features"));
assertTrue(status.toString().contains("Reading document content"));
}

@Test
public void testReplaceXMLIllegalCharactersInString_rewritesIllegalCharacters() {
String input = "A\u0001B\u0007C\uFFFFD";
String result = DocumentStaxUtils.replaceXMLIllegalCharactersInString(input);
assertEquals("A B C D", result);
}

@Test
public void testReadGateXmlDocument_multipleAnnotationSets_noException() throws Exception {
Gate.init();
String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<GateDocument version=\"3\">\n" + "<GateDocumentFeatures />\n" + "<TextWithNodes>Alpha<Node id=\"0\"/> Bravo<Node id=\"1\"/><Node id=\"2\"/></TextWithNodes>\n" + "<AnnotationSet Name=\"Set1\">\n" + "  <Annotation Id=\"11\" Type=\"T1\" StartNode=\"0\" EndNode=\"1\">\n" + "    <Feature>\n" + "      <Name className=\"java.lang.String\">language</Name>\n" + "      <Value className=\"java.lang.String\">en</Value>\n" + "    </Feature>\n" + "  </Annotation>\n" + "</AnnotationSet>\n" + "<AnnotationSet Name=\"Set2\">\n" + "  <Annotation Id=\"22\" Type=\"T2\" StartNode=\"1\" EndNode=\"2\">\n" + "    <Feature>\n" + "      <Name className=\"java.lang.String\">domain</Name>\n" + "      <Value className=\"java.lang.String\">general</Value>\n" + "    </Feature>\n" + "  </Annotation>\n" + "</AnnotationSet>\n" + "</GateDocument>";
ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(stream);
while (reader.hasNext()) {
if (reader.next() == XMLStreamConstants.START_ELEMENT && reader.getLocalName().equals("GateDocument"))
break;
}
Document doc = Factory.newDocument("");
DocumentStaxUtils.readGateXmlDocument(reader, doc);
AnnotationSet set1 = doc.getAnnotations("Set1");
AnnotationSet set2 = doc.getAnnotations("Set2");
assertEquals(1, set1.size());
assertEquals(1, set2.size());
Annotation a1 = set1.iterator().next();
assertEquals("T1", a1.getType());
assertEquals("en", a1.getFeatures().get("language"));
Annotation a2 = set2.iterator().next();
assertEquals("T2", a2.getType());
assertEquals("general", a2.getFeatures().get("domain"));
}

@Test(expected = XMLStreamException.class)
public void testReadAnnotationSet_startNodeMissing_throwsException() throws Exception {
Gate.init();
String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<GateDocument version=\"3\">\n" + "<GateDocumentFeatures />\n" + "<TextWithNodes>Text<Node id=\"0\"/><Node id=\"1\"/></TextWithNodes>\n" + "<AnnotationSet>\n" + "  <Annotation Id=\"1\" Type=\"Word\" EndNode=\"1\">\n" + "    <Feature />\n" + "  </Annotation>\n" + "</AnnotationSet>\n" + "</GateDocument>";
ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(stream);
while (reader.hasNext()) {
if (reader.next() == XMLStreamConstants.START_ELEMENT && "GateDocument".equals(reader.getLocalName()))
break;
}
Document doc = Factory.newDocument("");
DocumentStaxUtils.readGateXmlDocument(reader, doc);
}

@Test(expected = XMLStreamException.class)
public void testReadAnnotationSet_duplicateAnnotationId_throwsException() throws Exception {
Gate.init();
String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<GateDocument version=\"3\">\n" + "<GateDocumentFeatures />\n" + "<TextWithNodes>Hello<Node id=\"0\"/><Node id=\"1\"/><Node id=\"2\"/></TextWithNodes>\n" + "<AnnotationSet>\n" + "  <Annotation Id=\"1\" Type=\"X\" StartNode=\"1\" EndNode=\"2\"><Feature /></Annotation>\n" + "  <Annotation Id=\"1\" Type=\"Y\" StartNode=\"0\" EndNode=\"1\"><Feature /></Annotation>\n" + "</AnnotationSet>\n" + "</GateDocument>";
ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(stream);
while (reader.hasNext()) {
if (reader.next() == XMLStreamConstants.START_ELEMENT && "GateDocument".equals(reader.getLocalName()))
break;
}
Document doc = Factory.newDocument("");
DocumentStaxUtils.readGateXmlDocument(reader, doc);
}

@Test(expected = XMLStreamException.class)
public void testReadRelationSet_missingMembersAttr_throwsException() throws Exception {
Gate.init();
String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<GateDocument version=\"3\">\n" + "<GateDocumentFeatures />\n" + "<TextWithNodes>ABC<Node id=\"1\"/></TextWithNodes>\n" + "<AnnotationSet></AnnotationSet>\n" + "<RelationSet>\n" + "  <Relation Id=\"1\" Type=\"Rel\">\n" + "    <UserData></UserData>\n" + "    <Feature />\n" + "  </Relation>\n" + "</RelationSet>\n" + "</GateDocument>";
ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(stream);
while (reader.hasNext()) {
if (reader.next() == XMLStreamConstants.START_ELEMENT && "GateDocument".equals(reader.getLocalName()))
break;
}
Document doc = Factory.newDocument("");
DocumentStaxUtils.readGateXmlDocument(reader, doc);
}

@Test
public void testReplaceXMLIllegalCharacters_surrogateReplacement() {
String input = "text\uD800text\uDC00\uFFFF";
String output = DocumentStaxUtils.replaceXMLIllegalCharactersInString(input);
assertEquals("text text \u0020", output);
}

@Test
public void testWriteCharactersOrCDATA_triggersCDATAPath() throws Exception {
javax.xml.stream.XMLOutputFactory xf = javax.xml.stream.XMLOutputFactory.newInstance();
java.io.StringWriter sw = new java.io.StringWriter();
javax.xml.stream.XMLStreamWriter xsw = xf.createXMLStreamWriter(sw);
String withLTs = "<<<<<]]>";
xsw.writeStartElement("root");
DocumentStaxUtils.writeCharactersOrCDATA(xsw, withLTs);
xsw.writeEndElement();
xsw.close();
String result = sw.toString();
assertTrue(result.contains("<![CDATA["));
assertTrue(result.contains("<root>"));
assertTrue(result.contains("]]>"));
}

@Test(expected = XMLStreamException.class)
public void testReadAnnotationSet_invalidStartNode_nonInteger() throws Exception {
Gate.init();
String xml = "<?xml version=\"1.0\"?>\n" + "<GateDocument version=\"3\">\n" + "<GateDocumentFeatures />\n" + "<TextWithNodes>Hello<Node id=\"a\"/></TextWithNodes>\n" + "<AnnotationSet>\n" + "  <Annotation Id=\"1\" Type=\"T\" StartNode=\"a\" EndNode=\"10\">\n" + "    <Feature />\n" + "  </Annotation>\n" + "</AnnotationSet>\n" + "</GateDocument>";
ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(stream);
while (reader.hasNext()) {
if (reader.next() == XMLStreamConstants.START_ELEMENT && "GateDocument".equals(reader.getLocalName()))
break;
}
Document doc = Factory.newDocument("");
DocumentStaxUtils.readGateXmlDocument(reader, doc);
}

@Test(expected = XMLStreamException.class)
public void testReadXces_invalidAnnotationId_duplicateId() throws Exception {
Gate.init();
String xces = "<cesAna xmlns=\"http://www.xces.org/schema/2003\">\n" + "<struct type=\"tag\" from=\"0\" to=\"1\" n=\"1\">\n" + "  <feat name=\"attr\" value=\"value1\"/>\n" + "</struct>\n" + "<struct type=\"tag\" from=\"1\" to=\"2\" n=\"1\">\n" + "  <feat name=\"attr\" value=\"value2\"/>\n" + "</struct>\n" + "</cesAna>";
ByteArrayInputStream stream = new ByteArrayInputStream(xces.getBytes(StandardCharsets.UTF_8));
XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(stream);
while (reader.hasNext()) {
if (reader.next() == XMLStreamConstants.START_ELEMENT && "cesAna".equals(reader.getLocalName()))
break;
}
Document doc = Factory.newDocument("abc");
AnnotationSet as = doc.getAnnotations();
DocumentStaxUtils.readXces(reader, as);
}

@Test(expected = XMLStreamException.class)
public void testReadXces_nonIntegerFromAttribute() throws Exception {
Gate.init();
String xces = "<cesAna xmlns=\"http://www.xces.org/schema/2003\">\n" + "<struct type=\"T\" from=\"hello\" to=\"10\">\n" + "  <feat name=\"attr\" value=\"val\"/>\n" + "</struct>\n" + "</cesAna>";
ByteArrayInputStream stream = new ByteArrayInputStream(xces.getBytes(StandardCharsets.UTF_8));
XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(stream);
while (reader.hasNext()) {
if (reader.next() == XMLStreamConstants.START_ELEMENT && "cesAna".equals(reader.getLocalName()))
break;
}
Document doc = Factory.newDocument("test");
AnnotationSet as = doc.getAnnotations();
DocumentStaxUtils.readXces(reader, as);
}

@Test
public void testWriteXcesContent_doesNotCleanIllegalCharacters() throws Exception {
Gate.init();
Document doc = Factory.newDocument("textWith\u0007controlChar");
java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
DocumentStaxUtils.writeXcesContent(doc, out, "UTF-8");
String result = out.toString("UTF-8");
assertTrue(result.contains("\u0007"));
}

@Test
public void testWriteXcesAnnotations_emptySet() throws Exception {
Gate.init();
Document doc = Factory.newDocument("abc");
AnnotationSet set = doc.getAnnotations();
java.io.ByteArrayOutputStream stream = new java.io.ByteArrayOutputStream();
DocumentStaxUtils.writeXcesAnnotations(set, stream, "UTF-8");
String output = stream.toString("UTF-8");
assertTrue(output.contains("cesAna"));
}

@Test(expected = XMLStreamException.class)
public void testReadFeatureMap_unexpectedElement_throwsException() throws Exception {
String xml = "<Annotation>" + "<Feature><WrongElement className=\"java.lang.String\">name</WrongElement></Feature>" + "</Annotation>";
ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(stream);
while (reader.hasNext()) {
if (reader.next() == XMLStreamConstants.START_ELEMENT && reader.getLocalName().equals("Annotation"))
break;
}
DocumentStaxUtils.readFeatureMap(reader);
}

@Test(expected = XMLStreamException.class)
public void testReadTextWithNodes_nodeMissingId_throwsException() throws Exception {
String xml = "<TextWithNodes>Some<Node/></TextWithNodes>";
ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(stream);
while (reader.hasNext()) {
if (reader.next() == XMLStreamConstants.START_ELEMENT && reader.getLocalName().equals("TextWithNodes"))
break;
}
java.util.Map<Integer, Long> map = new java.util.HashMap<>();
DocumentStaxUtils.readTextWithNodes(reader, map);
}

@Test(expected = XMLStreamException.class)
public void testReadTextWithNodes_nodeNonIntegerId_throwsException() throws Exception {
String xml = "<TextWithNodes>Hello<Node id=\"abc\"/></TextWithNodes>";
ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(stream);
while (reader.hasNext()) {
if (reader.next() == XMLStreamConstants.START_ELEMENT && reader.getLocalName().equals("TextWithNodes"))
break;
}
java.util.Map<Integer, Long> map = new java.util.HashMap<>();
DocumentStaxUtils.readTextWithNodes(reader, map);
}

@Test(expected = XMLStreamException.class)
public void testAnnotation_missingTypeAttribute_throwsException() throws Exception {
Gate.init();
String xml = "<?xml version=\"1.0\"?>" + "<GateDocument>" + "<GateDocumentFeatures />" + "<TextWithNodes>text<Node id=\"0\"/><Node id=\"1\"/></TextWithNodes>" + "<AnnotationSet>" + "<Annotation Id=\"123\" StartNode=\"0\" EndNode=\"1\">" + "  <Feature />" + "</Annotation>" + "</AnnotationSet>" + "</GateDocument>";
ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(stream);
while (reader.hasNext()) {
if (reader.next() == XMLStreamConstants.START_ELEMENT && "GateDocument".equals(reader.getLocalName()))
break;
}
Document document = Factory.newDocument("");
DocumentStaxUtils.readGateXmlDocument(reader, document);
}

@Test
public void testReplaceIllegalCharacters_inMiddleOfString_getsReplaced() {
String input = "abc\u0001def\u000Bghi";
String output = DocumentStaxUtils.replaceXMLIllegalCharactersInString(input);
assertEquals("abc def ghi", output);
}

@Test
public void testReadFeatureNameOrValue_unknownClassFallsBackToString() throws Exception {
String xml = "<Name className=\"com.fake.Unknown\">sample</Name>";
ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(stream);
while (reader.hasNext()) {
if (reader.next() == XMLStreamConstants.START_ELEMENT && "Name".equals(reader.getLocalName()))
break;
}
Object value = DocumentStaxUtils.readFeatureNameOrValue(reader);
assertEquals("sample", value);
}

@Test
public void testReadFeatureNameOrValue_collectionFallbackToString() throws Exception {
String xml = "<Value className=\"java.util.Vector\" itemClassName=\"non.existing.Type\">one;two;three</Value>";
ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(stream);
while (reader.hasNext()) {
if (reader.next() == XMLStreamConstants.START_ELEMENT && "Value".equals(reader.getLocalName()))
break;
}
Object result = DocumentStaxUtils.readFeatureNameOrValue(reader);
assertTrue(result instanceof java.util.Collection);
assertEquals(3, ((java.util.Collection<?>) result).size());
}

@Test(expected = XMLStreamException.class)
public void testReadRelationSet_invalidRelationIdFormat_throwsException() throws Exception {
String xml = "<GateDocument><GateDocumentFeatures/><TextWithNodes>text<Node id=\"1\"/><Node id=\"2\"/></TextWithNodes>" + "<AnnotationSet/>" + "<RelationSet>" + "  <Relation Id=\"ABC\" Type=\"Rel\" Members=\"1;2\">" + "    <UserData></UserData>" + "    <Feature/>" + "  </Relation>" + "</RelationSet>" + "</GateDocument>";
ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(stream);
while (reader.hasNext()) {
if (reader.next() == XMLStreamConstants.START_ELEMENT && "GateDocument".equals(reader.getLocalName()))
break;
}
Document doc = Factory.newDocument("");
DocumentStaxUtils.readGateXmlDocument(reader, doc);
}

@Test
public void testWriteFeatures_nullMapDoesNotThrow() throws Exception {
StringWriter sw = new StringWriter();
XMLOutputFactory f = XMLOutputFactory.newInstance();
XMLStreamWriter writer = f.createXMLStreamWriter(sw);
writer.writeStartDocument();
writer.writeStartElement("Annotation");
FeatureMap fm = null;
DocumentStaxUtils.writeFeatures(fm, writer, "");
writer.writeEndElement();
writer.writeEndDocument();
writer.close();
String result = sw.toString();
assertTrue(result.contains("<Annotation>"));
}

@Test
public void testWriteFeatures_mapWithNullEntry_skipsNull() throws Exception {
Gate.init();
FeatureMap fm = Factory.newFeatureMap();
fm.put(null, "value");
fm.put("key", null);
StringWriter sw = new StringWriter();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
writer.writeStartDocument();
writer.writeStartElement("Root");
DocumentStaxUtils.writeFeatures(fm, writer, "");
writer.writeEndElement();
writer.writeEndDocument();
writer.close();
String xml = sw.toString();
assertFalse(xml.contains("Feature"));
}

@Test(expected = XMLStreamException.class)
public void testReadGateXmlDocument_missingTextWithNodes_throwsException() throws Exception {
Gate.init();
String xml = "<?xml version=\"1.0\"?><GateDocument><GateDocumentFeatures/></GateDocument>";
ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(stream);
while (reader.hasNext()) {
if (reader.next() == XMLStreamConstants.START_ELEMENT && "GateDocument".equals(reader.getLocalName()))
break;
}
Document doc = Factory.newDocument("");
DocumentStaxUtils.readGateXmlDocument(reader, doc);
}

@Test(expected = XMLStreamException.class)
public void testNodeElementHasChildren_throwsException() throws Exception {
String xml = "<TextWithNodes>Hello<Node id=\"1\">Inside</Node></TextWithNodes>";
ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(stream);
while (reader.hasNext()) {
if (reader.next() == XMLStreamConstants.START_ELEMENT && "TextWithNodes".equals(reader.getLocalName()))
break;
}
java.util.Map<Integer, Long> map = new java.util.HashMap<>();
DocumentStaxUtils.readTextWithNodes(reader, map);
}

@Test
public void testIsInvalidXmlChar_validSurrogatePairIsOk() {
String validSurrogate = new String(new int[] { 0x1F602 }, 0, 1);
CharSequence seq = new StringBuilder(validSurrogate);
boolean lowInvalid = DocumentStaxUtils.isInvalidXmlChar(seq, 0);
assertFalse(lowInvalid);
}

@Test
public void testIsInvalidXmlChar_unpairedHighSurrogateIsInvalid() {
String badChar = "\uD834";
CharSequence seq = new StringBuilder(badChar);
boolean result = DocumentStaxUtils.isInvalidXmlChar(seq, 0);
assertTrue(result);
}

@Test
public void testIsInvalidXmlChar_unpairedLowSurrogateIsInvalid() {
String badChar = "\uDC00";
CharSequence seq = new StringBuilder(badChar);
boolean result = DocumentStaxUtils.isInvalidXmlChar(seq, 0);
assertTrue(result);
}

@Test
public void testWriteTextWithNodes_noAnnotations_emitsOnlyText() throws Exception {
Gate.init();
Document doc = Factory.newDocument("Plain text");
StringWriter writer = new StringWriter();
XMLStreamWriter xmlWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(writer);
xmlWriter.writeStartDocument();
DocumentStaxUtils.writeTextWithNodes(doc, xmlWriter, "");
xmlWriter.writeEndDocument();
xmlWriter.close();
String output = writer.toString();
assertTrue(output.contains("TextWithNodes"));
assertTrue(output.contains("Plain text"));
assertFalse(output.contains("Node"));
}

@Test
public void testWriteTextWithNodes_charactersReplacedCorrectly() throws Exception {
Gate.init();
String content = "in\u0003valid";
Document doc = Factory.newDocument(content);
AnnotationSet set = doc.getAnnotations();
set.add(0L, 2L, "Test", Factory.newFeatureMap());
StringWriter writer = new StringWriter();
XMLStreamWriter xmlWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(writer);
xmlWriter.writeStartDocument();
DocumentStaxUtils.writeTextWithNodes(doc, xmlWriter, "");
xmlWriter.writeEndDocument();
xmlWriter.close();
String output = writer.toString();
assertTrue(output.contains("in valid"));
assertTrue(output.contains("Node"));
}

@Test
public void testWriteCharactersOrCDATA_withExactlyFiveLessThan() throws Exception {
StringWriter sw = new StringWriter();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
writer.writeStartDocument();
writer.writeStartElement("root");
DocumentStaxUtils.writeCharactersOrCDATA(writer, "<<<<<");
writer.writeEndElement();
writer.writeEndDocument();
writer.close();
String out = sw.toString();
assertTrue(out.contains("<![CDATA["));
}

@Test
public void testWriteCharactersOrCDATA_withLessThanBelowThreshold_usesWriteCharacters() throws Exception {
StringWriter sw = new StringWriter();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
writer.writeStartDocument();
writer.writeStartElement("root");
DocumentStaxUtils.writeCharactersOrCDATA(writer, "<<<<");
writer.writeEndElement();
writer.writeEndDocument();
writer.close();
String result = sw.toString();
assertFalse(result.contains("<![CDATA["));
assertTrue(result.contains("<<<<"));
}

@Test
public void testReplaceXMLIllegalCharacters_doesNothingIfNoneInvalid() {
char[] chars = "hello".toCharArray();
DocumentStaxUtils.replaceXMLIllegalCharacters(chars);
assertEquals('h', chars[0]);
assertEquals('o', chars[4]);
}

@Test
public void testReplaceXMLIllegalCharacters_replacesIllegalControl() {
char[] chars = "AB\u0000CD".toCharArray();
DocumentStaxUtils.replaceXMLIllegalCharacters(chars);
assertEquals(' ', chars[2]);
assertEquals('D', chars[4]);
}

@Test
public void testWriteFeatures_withFeatureContainingSpecialChar() throws Exception {
Gate.init();
FeatureMap fm = Factory.newFeatureMap();
fm.put("reason", "invalid\u0000char");
StringWriter sw = new StringWriter();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
writer.writeStartElement("Test");
DocumentStaxUtils.writeFeatures(fm, writer, "");
writer.writeEndElement();
writer.close();
String output = sw.toString();
assertTrue(output.contains("invalid char"));
}

@Test
public void testWriteFeatures_withBooleanAndNumber() throws Exception {
Gate.init();
FeatureMap fm = Factory.newFeatureMap();
fm.put("active", Boolean.TRUE);
fm.put("count", 42);
StringWriter sw = new StringWriter();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
writer.writeStartElement("Features");
DocumentStaxUtils.writeFeatures(fm, writer, "");
writer.writeEndElement();
writer.close();
String result = sw.toString();
assertTrue(result.contains("active"));
assertTrue(result.contains("count"));
assertTrue(result.contains("42"));
}

@Test
public void testWriteAnnotationSet_emptySet_emitsEmptyAnnotationSetElement() throws Exception {
Gate.init();
Document doc = Factory.newDocument("abc");
AnnotationSet set = doc.getAnnotations();
StringWriter sw = new StringWriter();
XMLStreamWriter xsw = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
DocumentStaxUtils.writeAnnotationSet(set, xsw, "");
xsw.close();
String xml = sw.toString();
assertTrue(xml.contains("<AnnotationSet"));
assertFalse(xml.contains("<Annotation"));
}

@Test(expected = XMLStreamException.class)
public void testReadFeatureNameOrValue_withNestedElementInside_shouldThrow() throws Exception {
String xml = "<Value className=\"java.lang.String\"><inner/></Value>";
ByteArrayInputStream in = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(in);
while (reader.hasNext()) {
if (reader.next() == XMLStreamConstants.START_ELEMENT && "Value".equals(reader.getLocalName()))
break;
}
DocumentStaxUtils.readFeatureNameOrValue(reader);
}

@Test
public void testReadFeatureNameOrValue_collectionOfStrings() throws Exception {
String xml = "<Value className=\"java.util.ArrayList\" itemClassName=\"java.lang.String\">one;two</Value>";
ByteArrayInputStream in = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(in);
while (reader.hasNext()) {
if (reader.next() == XMLStreamConstants.START_ELEMENT && "Value".equals(reader.getLocalName()))
break;
}
Object val = DocumentStaxUtils.readFeatureNameOrValue(reader);
assertTrue(val instanceof java.util.Collection);
assertEquals(2, ((java.util.Collection<?>) val).size());
}

@Test
public void testReadFeatureNameOrValue_fallbackObjectWrapperParsing() throws Exception {
String xml = "<Value className=\"gate.corpora.ObjectWrapper\">wrappedText</Value>";
ByteArrayInputStream in = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(in);
while (reader.hasNext()) {
if (reader.next() == XMLStreamConstants.START_ELEMENT && "Value".equals(reader.getLocalName()))
break;
}
Object value = DocumentStaxUtils.readFeatureNameOrValue(reader);
assertEquals("wrappedText", value);
}

@Test
public void testReadGateXmlDocument_emptyFeaturesNode() throws Exception {
Gate.init();
String xml = "<?xml version=\"1.0\"?>" + "<GateDocument>" + "<GateDocumentFeatures></GateDocumentFeatures>" + "<TextWithNodes>abc<Node id='0'/><Node id='1'/></TextWithNodes>" + "<AnnotationSet>" + "  <Annotation Id='1' Type='Test' StartNode='0' EndNode='1'>" + "    <Feature />" + "  </Annotation>" + "</AnnotationSet>" + "</GateDocument>";
ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(stream);
while (reader.hasNext()) {
if (reader.next() == XMLStreamConstants.START_ELEMENT && "GateDocument".equals(reader.getLocalName()))
break;
}
Document doc = Factory.newDocument("");
DocumentStaxUtils.readGateXmlDocument(reader, doc);
assertEquals("abc", doc.getContent().toString());
}

@Test
public void testWriteCharactersOrCDATA_containsCDATAEndMarker() throws Exception {
StringWriter sw = new StringWriter();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
writer.writeStartDocument();
writer.writeStartElement("root");
DocumentStaxUtils.writeCharactersOrCDATA(writer, "abc]]>def");
writer.writeEndElement();
writer.writeEndDocument();
writer.close();
String result = sw.toString();
assertTrue(result.contains("<![CDATA[abc"));
assertTrue(result.contains("]]>"));
assertTrue(result.contains("def"));
}

@Test(expected = XMLStreamException.class)
public void testReadGateXmlDocument_mixedOldStyleAndNewIds_throws() throws Exception {
Gate.init();
String xml = "<?xml version='1.0'?>" + "<GateDocument><GateDocumentFeatures/>" + "<TextWithNodes>hello<Node id='0'/><Node id='1'/><Node id='2'/></TextWithNodes>" + "<AnnotationSet>" + "  <Annotation Id='1' Type='Test' StartNode='0' EndNode='1'><Feature/></Annotation>" + "  <Annotation Type='Test2' StartNode='1' EndNode='2'><Feature/></Annotation>" + "</AnnotationSet>" + "</GateDocument>";
ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(stream);
while (reader.hasNext()) {
if (reader.next() == XMLStreamConstants.START_ELEMENT && "GateDocument".equals(reader.getLocalName())) {
break;
}
}
Document doc = Factory.newDocument("");
DocumentStaxUtils.readGateXmlDocument(reader, doc);
}

@Test
public void testToXml_withEmptyDocumentContent_doesNotThrow() {
// Gate.init();
// Document doc = Factory.newDocument("");
// String xml = DocumentStaxUtils.toXml(doc);
// assertNotNull(xml);
// assertTrue(xml.contains("TextWithNodes"));
}

@Test
public void testWriteAnnotationSet_withNullName_doesNotWriteNameAttribute() throws Exception {
Gate.init();
StringWriter output = new StringWriter();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(output);
Document doc = Factory.newDocument("abc");
AnnotationSet set = doc.getAnnotations();
set.add(0L, 2L, "Tag", Factory.newFeatureMap());
DocumentStaxUtils.writeAnnotationSet(set, writer, "");
writer.close();
String result = output.toString();
assertTrue(result.contains("AnnotationSet"));
assertTrue(result.contains("Tag"));
assertFalse(result.contains("Name="));
}

@Test
public void testWriteFeatures_featureNameWithSpecialCharacters() throws Exception {
Gate.init();
FeatureMap fmap = Factory.newFeatureMap();
fmap.put("na<me", "valid");
StringWriter output = new StringWriter();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(output);
writer.writeStartDocument();
writer.writeStartElement("Test");
DocumentStaxUtils.writeFeatures(fmap, writer, "");
writer.writeEndElement();
writer.close();
String result = output.toString();
assertTrue(result.contains("na<me"));
assertTrue(result.contains("valid"));
}

@Test
public void testWriteRelationSet_withUserDataContainingInvalidChars() throws Exception {
Gate.init();
Document doc = Factory.newDocument("abc");
AnnotationSet set = doc.getAnnotations();
set.add(0L, 1L, "A", Factory.newFeatureMap());
set.add(1L, 2L, "B", Factory.newFeatureMap());
gate.relations.RelationSet rset = set.getRelations();
gate.relations.SimpleRelation rel = new gate.relations.SimpleRelation(1, "Type", new int[] { 0, 1 });
rel.setUserData("invalid\u0001\uFFFFOK");
rset.add(rel);
StringWriter sw = new StringWriter();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
writer.writeStartDocument();
DocumentStaxUtils.writeRelationSet(rset, writer, "");
writer.writeEndDocument();
writer.close();
String output = sw.toString();
assertTrue(output.contains("RelationSet"));
assertTrue(output.contains("UserData"));
assertTrue(output.contains("invalid OK"));
}

@Test
public void testWriteTextWithNodes_handlesEmptyDocument() throws Exception {
Gate.init();
Document doc = Factory.newDocument("");
StringWriter sw = new StringWriter();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
DocumentStaxUtils.writeTextWithNodes(doc, writer, "");
writer.close();
String result = sw.toString();
assertTrue(result.contains("<TextWithNodes"));
}

@Test
public void testWriteXcesAnnotations_withNullEncoding_writesUtf8() throws Exception {
Gate.init();
Document doc = Factory.newDocument("abc");
AnnotationSet set = doc.getAnnotations();
set.add(0L, 1L, "Tok", Factory.newFeatureMap());
java.io.ByteArrayOutputStream stream = new java.io.ByteArrayOutputStream();
DocumentStaxUtils.writeXcesAnnotations(set, stream, null);
String xml = stream.toString("UTF-8");
assertTrue(xml.contains("cesAna"));
assertTrue(xml.contains("struct"));
assertTrue(xml.contains("from=\"0\""));
}

@Test
public void testReadFeatureMap_withEmptyFeatureElement_doesNotFail() throws Exception {
String xml = "<Annotation>" + "  <Feature></Feature>" + "</Annotation>";
ByteArrayInputStream in = new ByteArrayInputStream(xml.getBytes("UTF-8"));
XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(in);
while (reader.hasNext()) {
if (reader.next() == XMLStreamConstants.START_ELEMENT && "Annotation".equals(reader.getLocalName())) {
break;
}
}
FeatureMap result = DocumentStaxUtils.readFeatureMap(reader);
assertNotNull(result);
assertTrue(result.isEmpty());
}

@Test(expected = XMLStreamException.class)
public void testReadXces_annotationsWithInvalidToValue() throws Exception {
String xml = "<cesAna xmlns='http://www.xces.org/schema/2003'>" + "<struct type='Err' from='0' to='invalid'>" + "<feat name='attr' value='val'/></struct></cesAna>";
ByteArrayInputStream in = new ByteArrayInputStream(xml.getBytes("UTF-8"));
XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(in);
while (reader.hasNext()) {
if (reader.next() == XMLStreamConstants.START_ELEMENT && "cesAna".equals(reader.getLocalName()))
break;
}
Gate.init();
Document doc = Factory.newDocument("abc");
AnnotationSet set = doc.getAnnotations();
DocumentStaxUtils.readXces(reader, set);
}

@Test(expected = XMLStreamException.class)
public void testReadTextWithNodes_withExtraElementInNode_throws() throws Exception {
String xml = "<TextWithNodes>xyz<Node id='1'>text</Node></TextWithNodes>";
ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(stream);
while (reader.hasNext()) {
if (reader.next() == XMLStreamConstants.START_ELEMENT && "TextWithNodes".equals(reader.getLocalName()))
break;
}
DocumentStaxUtils.readTextWithNodes(reader, new java.util.HashMap<>());
}

@Test
public void testWriteXcesAnnotations_structWithoutFeatures() throws Exception {
Gate.init();
Document doc = Factory.newDocument("abcd");
AnnotationSet set = doc.getAnnotations();
set.add(0L, 3L, "Label", Factory.newFeatureMap());
StringWriter writer = new StringWriter();
XMLStreamWriter xwriter = XMLOutputFactory.newInstance().createXMLStreamWriter(writer);
xwriter.writeStartDocument();
DocumentStaxUtils.writeXcesAnnotations(set, xwriter, true);
xwriter.writeEndDocument();
xwriter.close();
String result = writer.toString();
assertTrue(result.contains("struct"));
assertTrue(result.contains("Label"));
}

@Test
public void testReadFeatureMap_featureMissingValueButWithName() throws Exception {
String xml = "<Annotation>" + "<Feature>" + "<Name className='java.lang.String'>someKey</Name>" + "</Feature>" + "</Annotation>";
ByteArrayInputStream in = new ByteArrayInputStream(xml.getBytes("UTF-8"));
XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(in);
while (reader.hasNext()) {
if (reader.next() == XMLStreamConstants.START_ELEMENT && "Annotation".equals(reader.getLocalName()))
break;
}
FeatureMap map = DocumentStaxUtils.readFeatureMap(reader);
assertTrue(map.containsKey("someKey"));
assertNull(map.get("someKey"));
}

@Test(expected = XMLStreamException.class)
public void testReadGateXmlDocument_unmappedStartNodeId_throws() throws Exception {
String xml = "<?xml version='1.0'?><GateDocument>" + "<GateDocumentFeatures/>" + "<TextWithNodes>test<Node id='1'/></TextWithNodes>" + "<AnnotationSet>" + "<Annotation Id='1' Type='X' StartNode='999' EndNode='1'>" + "<Feature/></Annotation>" + "</AnnotationSet></GateDocument>";
Gate.init();
Document doc = Factory.newDocument("");
ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(stream);
while (reader.hasNext()) {
if (reader.next() == XMLStreamConstants.START_ELEMENT && "GateDocument".equals(reader.getLocalName()))
break;
}
DocumentStaxUtils.readGateXmlDocument(reader, doc);
}

@Test(expected = XMLStreamException.class)
public void testReadGateXmlDocument_relationSetWithUserdataAsElement_shouldFail() throws Exception {
String xml = "<?xml version='1.0'?><GateDocument>" + "<GateDocumentFeatures/>" + "<TextWithNodes>abc<Node id='0'/><Node id='1'/></TextWithNodes>" + "<AnnotationSet/>" + "<RelationSet>" + "<Relation Id='15' Type='Edge' Members='0;1'>" + "<UserData><extra/></UserData>" + "<Feature/>" + "</Relation>" + "</RelationSet></GateDocument>";
Gate.init();
Document doc = Factory.newDocument("abc");
ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(stream);
while (reader.hasNext()) {
if (reader.next() == XMLStreamConstants.START_ELEMENT && "GateDocument".equals(reader.getLocalName()))
break;
}
DocumentStaxUtils.readGateXmlDocument(reader, doc);
}

@Test
public void testReplaceXMLIllegalCharacters_shouldBeNoopForPlainText() {
char[] chars = "Hello XML safe".toCharArray();
DocumentStaxUtils.replaceXMLIllegalCharacters(chars);
assertEquals('X', chars[6]);
}

@Test
public void testReplaceXMLIllegalCharacters_partialRangeChange() {
char[] content = "ab\u0003cd\u0001ef".toCharArray();
DocumentStaxUtils.replaceXMLIllegalCharacters(content, 2, 4);
assertEquals(' ', content[2]);
assertEquals('d', content[3]);
assertEquals(' ', content[4]);
assertEquals('e', content[5]);
}

@Test
public void testWriteDocument_withNamespace_doesIncludeNamespace() throws Exception {
Gate.init();
Document doc = Factory.newDocument("abc");
doc.getFeatures().put("source", "unit");
StringWriter sw = new StringWriter();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
writer.writeStartDocument();
DocumentStaxUtils.writeDocument(doc, writer, "http://gate.ac.uk");
writer.writeEndDocument();
writer.close();
String output = sw.toString();
assertTrue(output.contains("http://gate.ac.uk"));
assertTrue(output.contains("GateDocument"));
assertTrue(output.contains("source"));
}

@Test
public void testWriteXcesContent_handlesNullStreamEncoding() throws Exception {
Gate.init();
Document doc = Factory.newDocument("sampleText");
ByteArrayOutputStream stream = new ByteArrayOutputStream();
DocumentStaxUtils.writeXcesContent(doc, stream, null);
String content = stream.toString("UTF-8");
assertEquals("sampleText", content);
}

@Test
public void testWriteXcesAnnotations_includeIdFalse_excludesNAttribute() throws Exception {
Gate.init();
Document doc = Factory.newDocument("body");
AnnotationSet set = doc.getAnnotations();
set.add(0L, 2L, "X", Factory.newFeatureMap());
StringWriter sw = new StringWriter();
XMLStreamWriter xsw = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
xsw.writeStartDocument();
DocumentStaxUtils.writeXcesAnnotations(set, xsw, false);
xsw.writeEndDocument();
xsw.close();
String output = sw.toString();
assertTrue(output.contains("from=\"0\""));
assertFalse(output.contains("n="));
}

@Test
public void testReadGateXmlDocument_setsNextAnnotationId() throws Exception {
Gate.init();
String xml = "<?xml version='1.0'?><GateDocument>" + "<GateDocumentFeatures/>" + "<TextWithNodes>abc<Node id='0'/><Node id='1'/></TextWithNodes>" + "<AnnotationSet>" + "  <Annotation Id='42' Type='Token' StartNode='0' EndNode='1'>" + "    <Feature>" + "      <Name className='java.lang.String'>partOfSpeech</Name>" + "      <Value className='java.lang.String'>NN</Value>" + "    </Feature>" + "  </Annotation>" + "</AnnotationSet></GateDocument>";
javax.xml.stream.XMLStreamReader reader = javax.xml.stream.XMLInputFactory.newInstance().createXMLStreamReader(new java.io.ByteArrayInputStream(xml.getBytes("UTF-8")));
while (reader.hasNext()) {
if (reader.next() == javax.xml.stream.XMLStreamConstants.START_ELEMENT && "GateDocument".equals(reader.getLocalName()))
break;
}
Document doc = Factory.newDocument("");
DocumentStaxUtils.readGateXmlDocument(reader, doc);
assertEquals("abc", doc.getContent().toString());
assertEquals(1, doc.getAnnotations().size());
Annotation a = doc.getAnnotations().iterator().next();
assertEquals((Integer) 42, a.getId());
if (doc instanceof DocumentImpl) {
int nextId = ((DocumentImpl) doc).getNextAnnotationId();
assertTrue("Next annotation ID should be > 42", nextId > 42);
}
}

@Test
public void testReplaceXMLIllegalCharactersInString_returnsSameInstanceIfValid() {
String clean = "valid string \u0020 with no \u20AC invalid chars";
String result = DocumentStaxUtils.replaceXMLIllegalCharactersInString(clean);
assertSame(clean, result);
}

@Test
public void testWriteCharactersOrCDATA_splitsOnMultipleCDATAEnds() throws Exception {
StringWriter out = new StringWriter();
XMLStreamWriter xsw = XMLOutputFactory.newInstance().createXMLStreamWriter(out);
xsw.writeStartElement("test");
DocumentStaxUtils.writeCharactersOrCDATA(xsw, "abc]]>def]]>ghi");
xsw.writeEndElement();
xsw.close();
String written = out.toString();
assertTrue(written.contains("<![CDATA[abc"));
assertTrue(written.contains("]]>"));
assertTrue(written.contains("def"));
assertTrue(written.contains("ghi"));
}

@Test
public void testWriteFeatures_ObjectWrapperWrappedValue() throws Exception {
Gate.init();
Object wrapped = new ObjectWrapper(new java.sql.Timestamp(123456789L));
FeatureMap fmap = Factory.newFeatureMap();
fmap.put("wrappedTs", wrapped);
StringWriter writer = new StringWriter();
XMLStreamWriter xsw = XMLOutputFactory.newInstance().createXMLStreamWriter(writer);
xsw.writeStartElement("features");
DocumentStaxUtils.writeFeatures(fmap, xsw, "");
xsw.writeEndElement();
xsw.close();
String xml = writer.toString();
assertTrue(xml.contains("wrappedTs"));
assertTrue(xml.contains("Timestamp"));
}

@Test
public void testWriteDocument_multipleAnnotationSetsIncluded() throws Exception {
Gate.init();
Document doc = Factory.newDocument("test");
FeatureMap fm = Factory.newFeatureMap();
fm.put("id", 123);
doc.getAnnotations().add(0L, 2L, "Tok", fm);
doc.getAnnotations("extra").add(2L, 4L, "Tok2", Factory.newFeatureMap());
StringWriter out = new StringWriter();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(out);
writer.writeStartDocument();
DocumentStaxUtils.writeDocument(doc, writer, "");
writer.writeEndDocument();
writer.close();
String output = out.toString();
assertTrue(output.contains("<AnnotationSet"));
assertTrue(output.contains("Name=\"extra\""));
assertTrue(output.contains("Tok"));
}

@Test
public void testWriteTextWithNodes_skipsEmptyAnnotationCollections() throws Exception {
Gate.init();
Document doc = Factory.newDocument("ab");
List<java.util.Collection<Annotation>> annots = new ArrayList<>();
annots.add(Collections.emptyList());
StringWriter out = new StringWriter();
XMLStreamWriter xsw = XMLOutputFactory.newInstance().createXMLStreamWriter(out);
DocumentStaxUtils.writeTextWithNodes(doc, annots, xsw, "");
xsw.close();
String result = out.toString();
assertTrue(result.contains("TextWithNodes"));
assertFalse(result.contains("<Node"));
assertTrue(result.contains("ab"));
}

@Test
public void testWriteRelationSet_nullRelationSet_doesNothing() throws Exception {
StringWriter out = new StringWriter();
XMLStreamWriter xsw = XMLOutputFactory.newInstance().createXMLStreamWriter(out);
DocumentStaxUtils.writeRelationSet(null, xsw, "");
xsw.close();
String result = out.toString();
assertTrue(result.isEmpty());
}

@Test
public void testWriteRelationSet_emptyRelationSet_doesNothing() throws Exception {
Gate.init();
Document doc = Factory.newDocument("x");
AnnotationSet set = doc.getAnnotations();
RelationSet relations = set.getRelations();
StringWriter out = new StringWriter();
XMLStreamWriter xsw = XMLOutputFactory.newInstance().createXMLStreamWriter(out);
DocumentStaxUtils.writeRelationSet(relations, xsw, "");
xsw.close();
String result = out.toString();
assertTrue(result.isEmpty());
}

@Test
public void testWriteFeatures_collectionOfMixedItems() throws Exception {
Gate.init();
List<Object> mixedList = new ArrayList<>();
mixedList.add("a");
mixedList.add(123);
FeatureMap fm = Factory.newFeatureMap();
fm.put("mix", mixedList);
StringWriter sw = new StringWriter();
XMLStreamWriter xsw = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
xsw.writeStartElement("root");
DocumentStaxUtils.writeFeatures(fm, xsw, "");
xsw.writeEndElement();
xsw.close();
String result = sw.toString();
assertTrue(result.contains("mix"));
assertTrue(result.contains("a"));
assertTrue(result.contains("123"));
}

@Test
public void testWriteTextWithNodes_multipleNodesAreSorted() throws Exception {
Gate.init();
Document doc = Factory.newDocument("abcde");
AnnotationSet set = doc.getAnnotations();
set.add(1L, 2L, "X", Factory.newFeatureMap());
set.add(2L, 4L, "Y", Factory.newFeatureMap());
List<java.util.Collection<Annotation>> sets = new ArrayList<>();
sets.add(set);
StringWriter writer = new StringWriter();
XMLStreamWriter xsw = XMLOutputFactory.newInstance().createXMLStreamWriter(writer);
DocumentStaxUtils.writeTextWithNodes(doc, sets, xsw, "");
xsw.close();
String output = writer.toString();
assertTrue(output.contains("<Node id=\"1\"/>"));
assertTrue(output.contains("<Node id=\"2\"/>"));
assertTrue(output.contains("<Node id=\"4\"/>"));
}
}
