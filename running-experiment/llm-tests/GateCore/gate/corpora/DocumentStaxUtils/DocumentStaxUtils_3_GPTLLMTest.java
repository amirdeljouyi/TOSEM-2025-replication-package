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

public class DocumentStaxUtils_3_GPTLLMTest {

@Test
public void testWriteAndReadDocumentWithAnnotations() throws Exception {
Gate.init();
DocumentImpl doc = new DocumentImpl();
doc.setContent(new DocumentContentImpl("Hello GATE!"));
FeatureMap features = Factory.newFeatureMap();
features.put("type", "noun");
doc.getAnnotations().add(0L, 5L, "Token", features);
doc.getAnnotations().add(6L, 10L, "Token", Factory.newFeatureMap());
String resultXml = DocumentStaxUtils.toXml(doc);
assertNotNull(resultXml);
assertTrue(resultXml.contains("GateDocument"));
assertTrue(resultXml.contains("AnnotationSet"));
XMLInputFactory inputFactory = XMLInputFactory.newInstance();
XMLStreamReader reader = inputFactory.createXMLStreamReader(new ByteArrayInputStream(resultXml.getBytes(StandardCharsets.UTF_8)));
while (reader.hasNext()) {
int type = reader.next();
if (type == XMLStreamConstants.START_ELEMENT && "GateDocument".equals(reader.getLocalName())) {
break;
}
}
DocumentImpl newDoc = new DocumentImpl();
DocumentStaxUtils.readGateXmlDocument(reader, newDoc);
String content = newDoc.getContent().toString();
assertEquals("Hello GATE!", content);
assertEquals(2, newDoc.getAnnotations().size());
}

@Test(expected = XMLStreamException.class)
public void testReadInvalidAnnotationNodeIdThrowsException() throws Exception {
Gate.init();
String xml = "<GateDocument version=\"3\">" + "<GateDocumentFeatures/>" + "<TextWithNodes>" + "<Node id=\"1\"/>" + "</TextWithNodes>" + "<AnnotationSet>" + "<Annotation Type=\"Dummy\" StartNode=\"2\" EndNode=\"1\">" + "<Feature><Name className=\"java.lang.String\">x</Name><Value className=\"java.lang.String\">y</Value></Feature>" + "</Annotation>" + "</AnnotationSet>" + "</GateDocument>";
XMLInputFactory inputFactory = XMLInputFactory.newInstance();
XMLStreamReader reader = inputFactory.createXMLStreamReader(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
while (reader.hasNext()) {
int type = reader.next();
if (type == XMLStreamConstants.START_ELEMENT && "GateDocument".equals(reader.getLocalName())) {
break;
}
}
DocumentImpl doc = new DocumentImpl();
DocumentStaxUtils.readGateXmlDocument(reader, doc);
}

@Test
public void testWriteXcesContent() throws Exception {
Gate.init();
DocumentImpl doc = new DocumentImpl();
doc.setContent(new DocumentContentImpl("XCES output test."));
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
DocumentStaxUtils.writeXcesContent(doc, outputStream, "UTF-8");
String output = outputStream.toString("UTF-8");
assertEquals("XCES output test.", output);
}

@Test
public void testWriteXcesAnnotations() throws Exception {
Gate.init();
DocumentImpl doc = new DocumentImpl();
doc.setContent(new DocumentContentImpl("Tokenize this."));
AnnotationSet annSet = doc.getAnnotations();
FeatureMap features = Factory.newFeatureMap();
features.put("pos", "NN");
annSet.add(0L, 8L, "Token", features);
annSet.add(9L, 13L, "Token", Factory.newFeatureMap());
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
DocumentStaxUtils.writeXcesAnnotations(annSet, outputStream, "UTF-8");
String xml = outputStream.toString("UTF-8");
assertTrue(xml.contains("cesAna"));
assertTrue(xml.contains("struct"));
assertTrue(xml.contains("Token"));
}

@Test
public void testReplaceXMLIllegalCharactersInString() {
String original = "Valid\u0000Text\uFFFEInvalid\uFFFFCharacters";
String result = DocumentStaxUtils.replaceXMLIllegalCharactersInString(original);
assertEquals("Valid Text Invalid Characters", result);
}

@Test
public void testWriteAnnotationSetProducesExpectedXML() throws Exception {
Gate.init();
DocumentImpl doc = new DocumentImpl();
doc.setContent(new DocumentContentImpl("Test"));
FeatureMap features = Factory.newFeatureMap();
features.put("tag", "testValue");
AnnotationSet annSet = doc.getAnnotations();
annSet.add(0L, 4L, "TestTag", features);
StringWriter sw = new StringWriter();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
writer.writeStartDocument("UTF-8", "1.0");
DocumentStaxUtils.writeAnnotationSet(annSet, "MainSet", writer, "");
writer.writeEndDocument();
writer.flush();
String output = sw.toString();
assertTrue(output.contains("AnnotationSet"));
assertTrue(output.contains("MainSet"));
assertTrue(output.contains("TestTag"));
assertTrue(output.contains("testValue"));
}

@Test
public void testReadFeatureMapParsesMultipleEntries() throws Exception {
Gate.init();
String xml = "<GateDocumentFeatures>" + "<Feature><Name className=\"java.lang.String\">key1</Name><Value className=\"java.lang.String\">value1</Value></Feature>" + "<Feature><Name className=\"java.lang.String\">key2</Name><Value className=\"java.lang.String\">value2</Value></Feature>" + "</GateDocumentFeatures>";
XMLInputFactory inputFactory = XMLInputFactory.newInstance();
XMLStreamReader reader = inputFactory.createXMLStreamReader(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
while (reader.hasNext()) {
int eventType = reader.next();
if (eventType == XMLStreamConstants.START_ELEMENT && "GateDocumentFeatures".equals(reader.getLocalName())) {
break;
}
}
FeatureMap map = DocumentStaxUtils.readFeatureMap(reader);
assertEquals(2, map.size());
assertEquals("value1", map.get("key1"));
assertEquals("value2", map.get("key2"));
}

@Test(expected = XMLStreamException.class)
public void testReadXcesWithDuplicateIdsThrowsException() throws Exception {
Gate.init();
String xcesXml = "<cesAna xmlns=\"" + DocumentStaxUtils.XCES_NAMESPACE + "\" version=\"1.0\">" + "<struct type=\"word\" from=\"0\" to=\"4\" n=\"1\">" + "<feat name=\"pos\" value=\"NN\"/>" + "</struct>" + "<struct type=\"word\" from=\"5\" to=\"10\" n=\"1\">" + "<feat name=\"pos\" value=\"VB\"/>" + "</struct>" + "</cesAna>";
XMLInputFactory inputFactory = XMLInputFactory.newInstance();
XMLStreamReader reader = inputFactory.createXMLStreamReader(new ByteArrayInputStream(xcesXml.getBytes(StandardCharsets.UTF_8)));
while (reader.hasNext()) {
int type = reader.next();
if (type == XMLStreamConstants.START_ELEMENT && "cesAna".equals(reader.getLocalName())) {
break;
}
}
DocumentImpl doc = new DocumentImpl();
AnnotationSet annotationSet = doc.getAnnotations();
DocumentStaxUtils.readXces(reader, annotationSet);
}

@Test(expected = XMLStreamException.class)
public void testReadAnnotationSetMixedIdStylesThrowsException() throws Exception {
Gate.init();
String xml = "<AnnotationSet>" + "<Annotation Type=\"Token\" StartNode=\"0\" EndNode=\"4\">" + "<Feature><Name className=\"java.lang.String\">x</Name><Value className=\"java.lang.String\">y</Value></Feature>" + "</Annotation>" + "<Annotation Type=\"Token\" StartNode=\"5\" EndNode=\"10\" Id=\"1\">" + "<Feature><Name className=\"java.lang.String\">x</Name><Value className=\"java.lang.String\">z</Value></Feature>" + "</Annotation>" + "</AnnotationSet>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new ByteArrayInputStream(xml.getBytes("UTF-8")));
while (reader.hasNext()) {
int type = reader.next();
if (type == XMLStreamConstants.START_ELEMENT && "AnnotationSet".equals(reader.getLocalName())) {
break;
}
}
DocumentImpl doc = new DocumentImpl();
AnnotationSet set = doc.getAnnotations();
Map<Integer, Long> nodeMap = new HashMap<Integer, Long>();
nodeMap.put(0, 0L);
nodeMap.put(4, 4L);
nodeMap.put(5, 5L);
nodeMap.put(10, 10L);
Set<Integer> ids = new HashSet<Integer>();
DocumentStaxUtils.readAnnotationSet(reader, set, nodeMap, ids, null);
}

@Test(expected = XMLStreamException.class)
public void testReadAnnotationSetWithDuplicateIdsThrowsException() throws Exception {
Gate.init();
String xml = "<AnnotationSet>" + "<Annotation Type=\"Token\" StartNode=\"0\" EndNode=\"4\" Id=\"1\">" + "<Feature><Name className=\"java.lang.String\">x</Name><Value className=\"java.lang.String\">y</Value></Feature>" + "</Annotation>" + "<Annotation Type=\"Token\" StartNode=\"5\" EndNode=\"10\" Id=\"1\">" + "<Feature><Name className=\"java.lang.String\">a</Name><Value className=\"java.lang.String\">b</Value></Feature>" + "</Annotation>" + "</AnnotationSet>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new ByteArrayInputStream(xml.getBytes("UTF-8")));
while (reader.hasNext()) {
int type = reader.next();
if (type == XMLStreamConstants.START_ELEMENT && "AnnotationSet".equals(reader.getLocalName())) {
break;
}
}
DocumentImpl doc = new DocumentImpl();
AnnotationSet set = doc.getAnnotations();
Map<Integer, Long> nodeMap = new HashMap<Integer, Long>();
nodeMap.put(0, 0L);
nodeMap.put(4, 4L);
nodeMap.put(5, 5L);
nodeMap.put(10, 10L);
Set<Integer> ids = new HashSet<Integer>();
DocumentStaxUtils.readAnnotationSet(reader, set, nodeMap, ids, true);
}

@Test
public void testWriteTextWithNodesNoAnnotations() throws Exception {
Gate.init();
DocumentImpl doc = new DocumentImpl();
doc.setContent(new DocumentContentImpl("Just plain text."));
StringWriter writer = new StringWriter();
XMLStreamWriter xmlWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(writer);
DocumentStaxUtils.writeTextWithNodes(doc, xmlWriter, "");
xmlWriter.flush();
String output = writer.toString();
assertTrue(output.contains("TextWithNodes"));
assertFalse(output.contains("Node"));
}

@Test(expected = XMLStreamException.class)
public void testReadTextWithNodesThrowsIfNodeLacksId() throws Exception {
Gate.init();
String xml = "<TextWithNodes>" + "<Node/>" + "</TextWithNodes>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new ByteArrayInputStream(xml.getBytes("UTF-8")));
while (reader.hasNext()) {
int type = reader.next();
if (type == XMLStreamConstants.START_ELEMENT && "TextWithNodes".equals(reader.getLocalName())) {
break;
}
}
DocumentStaxUtils.readTextWithNodes(reader, new HashMap<Integer, Long>());
}

@Test(expected = XMLStreamException.class)
public void testReadRelationSetMissingTypeAttribute() throws Exception {
Gate.init();
String xml = "<RelationSet>" + "<Relation Id=\"100\" Members=\"1;2\">" + "<UserData>info</UserData>" + "<Feature><Name className=\"java.lang.String\">f</Name><Value className=\"java.lang.String\">v</Value></Feature>" + "</Relation>" + "</RelationSet>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new ByteArrayInputStream(xml.getBytes("UTF-8")));
while (reader.hasNext()) {
int type = reader.next();
if (type == XMLStreamConstants.START_ELEMENT && "RelationSet".equals(reader.getLocalName())) {
break;
}
}
RelationSet relations = Factory.newDocument("").getAnnotations().getRelations();
Set<Integer> ids = new HashSet<Integer>();
DocumentStaxUtils.readRelationSet(reader, relations, ids);
}

@Test
public void testCDataEscapingInWriteCharactersOrCDATA() throws Exception {
StringWriter sw = new StringWriter();
XMLStreamWriter xsw = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
xsw.writeStartDocument("UTF-8", "1.0");
xsw.writeStartElement("root");
DocumentStaxUtils.writeCharactersOrCDATA(xsw, "text with < five < signs < < < < < that should trigger CDATA ]]> end");
xsw.writeEndElement();
xsw.writeEndDocument();
xsw.flush();
String result = sw.toString();
assertTrue(result.contains("<![CDATA["));
assertTrue(result.contains("]]>"));
}

@Test
public void testReplaceXMLIllegalCharactersHandlesSurrogates() {
String input = new String(new char[] { '\uD800', 'A', '\uDC00', 'B' });
String output = DocumentStaxUtils.replaceXMLIllegalCharactersInString(input);
assertEquals(" A B", output);
}

@Test
public void testWriteEmptyAnnotationSet() throws Exception {
Gate.init();
DocumentImpl doc = new DocumentImpl();
doc.setContent(new DocumentContentImpl("Nothing here"));
AnnotationSet annSet = doc.getAnnotations();
StringWriter sw = new StringWriter();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
writer.writeStartDocument("UTF-8", "1.0");
DocumentStaxUtils.writeAnnotationSet(annSet, "EmptySet", writer, "");
writer.writeEndDocument();
writer.flush();
String xml = sw.toString();
assertTrue(xml.contains("AnnotationSet"));
assertTrue(xml.contains("EmptySet"));
assertFalse(xml.contains("<Annotation "));
}

@Test
public void testWriteFeatureMapWithObjectWrapper() throws Exception {
Gate.init();
StringWriter sw = new StringWriter();
XMLStreamWriter xsw = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
xsw.writeStartDocument("UTF-8", "1.0");
xsw.writeStartElement("Features");
FeatureMap fm = Factory.newFeatureMap();
fm.put("structured", new ObjectWrapper(new ArrayList<String>(Arrays.asList("a", "b", "c"))));
DocumentStaxUtils.writeFeatures(fm, xsw, "");
xsw.writeEndElement();
xsw.writeEndDocument();
xsw.flush();
String output = sw.toString();
assertTrue(output.contains("structured"));
assertTrue(output.contains("a;b;c"));
}

@Test
public void testReadFeatureMapWithNullValue() throws Exception {
String xml = "<GateDocumentFeatures>" + "<Feature>" + "<Name className=\"java.lang.String\">featureName</Name>" + "<Value className=\"java.lang.String\"></Value>" + "</Feature>" + "</GateDocumentFeatures>";
XMLInputFactory inputFactory = XMLInputFactory.newInstance();
XMLStreamReader reader = inputFactory.createXMLStreamReader(new ByteArrayInputStream(xml.getBytes("UTF-8")));
while (reader.hasNext()) {
int type = reader.next();
if (type == XMLStreamConstants.START_ELEMENT && "GateDocumentFeatures".equals(reader.getLocalName())) {
break;
}
}
FeatureMap map = DocumentStaxUtils.readFeatureMap(reader);
assertTrue(map.containsKey("featureName"));
assertEquals("", map.get("featureName"));
}

@Test
public void testReadFeatureNameOrValueWithUnknownClassFallbacksToString() throws Exception {
String xml = "<Value className=\"non.existent.Class\">fallback</Value>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new ByteArrayInputStream(xml.getBytes("UTF-8")));
while (reader.hasNext()) {
int type = reader.next();
if (type == XMLStreamConstants.START_ELEMENT && "Value".equals(reader.getLocalName())) {
break;
}
}
Object result = DocumentStaxUtils.readFeatureNameOrValue(reader);
assertEquals("fallback", result);
}

@Test(expected = XMLStreamException.class)
public void testUnexpectedElementInFeatureMapThrowsException() throws Exception {
String xml = "<GateDocumentFeatures>" + "<Feature><BADTAG className=\"java.lang.String\">data</BADTAG></Feature>" + "</GateDocumentFeatures>";
XMLInputFactory inputFactory = XMLInputFactory.newInstance();
XMLStreamReader reader = inputFactory.createXMLStreamReader(new ByteArrayInputStream(xml.getBytes("UTF-8")));
while (reader.hasNext()) {
int type = reader.next();
if (type == XMLStreamConstants.START_ELEMENT && "GateDocumentFeatures".equals(reader.getLocalName())) {
break;
}
}
DocumentStaxUtils.readFeatureMap(reader);
}

@Test(expected = XMLStreamException.class)
public void testReadRelationSetFailsOnNestedStartElementInUserData() throws Exception {
String xml = "<RelationSet>" + "<Relation Id=\"1\" Type=\"rel\" Members=\"100\">" + "<UserData><badTag>should fail</badTag></UserData>" + "<Feature><Name className=\"java.lang.String\">k</Name><Value className=\"java.lang.String\">v</Value></Feature>" + "</Relation>" + "</RelationSet>";
XMLInputFactory inputFactory = XMLInputFactory.newInstance();
XMLStreamReader reader = inputFactory.createXMLStreamReader(new ByteArrayInputStream(xml.getBytes("UTF-8")));
while (reader.hasNext()) {
int type = reader.next();
if (type == XMLStreamConstants.START_ELEMENT && "RelationSet".equals(reader.getLocalName())) {
break;
}
}
RelationSet rs = Factory.newDocument("").getAnnotations().getRelations();
DocumentStaxUtils.readRelationSet(reader, rs, new HashSet<Integer>());
}

@Test
public void testWriteXcesAnnotationsWithoutIds() throws Exception {
Gate.init();
DocumentImpl doc = new DocumentImpl();
doc.setContent(new DocumentContentImpl("xces content"));
AnnotationSet set = doc.getAnnotations();
set.add(0L, 5L, "word", Factory.newFeatureMap());
StringWriter out = new StringWriter();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(out);
writer.writeStartDocument("UTF-8", "1.0");
DocumentStaxUtils.writeXcesAnnotations(set, writer, false);
writer.writeEndDocument();
writer.flush();
String xmlOut = out.toString();
assertTrue(xmlOut.contains("<struct"));
assertFalse(xmlOut.contains("n="));
}

@Test
public void testWriteDocumentIncludesNamedAnnotationSets() throws Exception {
Gate.init();
DocumentImpl doc = new DocumentImpl();
doc.setContent(new DocumentContentImpl("text"));
FeatureMap fm = Factory.newFeatureMap();
fm.put("x", "y");
AnnotationSet defaultSet = doc.getAnnotations();
AnnotationSet mySet = doc.getAnnotations("customSet");
defaultSet.add(0L, 1L, "A", fm);
mySet.add(1L, 4L, "B", fm);
StringWriter out = new StringWriter();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(out);
writer.writeStartDocument("UTF-8", "1.0");
DocumentStaxUtils.writeDocument(doc, writer, "");
writer.writeEndDocument();
writer.flush();
String xml = out.toString();
assertTrue(xml.contains("AnnotationSet"));
assertTrue(xml.contains("customSet"));
assertTrue(xml.contains("A"));
assertTrue(xml.contains("B"));
}

@Test
public void testFeatureMapWithCollectionNonStringItemClass() throws Exception {
String xml = "<GateDocumentFeatures>" + "<Feature>" + "<Name className=\"java.lang.String\">list</Name>" + "<Value className=\"java.util.ArrayList\" itemClassName=\"java.lang.Integer\">1;2;3</Value>" + "</Feature>" + "</GateDocumentFeatures>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new ByteArrayInputStream(xml.getBytes("UTF-8")));
while (reader.hasNext()) {
if (reader.next() == XMLStreamConstants.START_ELEMENT && "GateDocumentFeatures".equals(reader.getLocalName())) {
break;
}
}
FeatureMap fm = DocumentStaxUtils.readFeatureMap(reader);
assertTrue(fm.containsKey("list"));
Object value = fm.get("list");
assertTrue(value instanceof Collection);
assertTrue(((Collection<?>) value).contains(1));
}

@Test
public void testWriteFeaturesWithSpecialCharactersAndCDATAEndMarker() throws Exception {
FeatureMap features = Factory.newFeatureMap();
features.put("xmlFragment", "this </b> contains ]]> CDATA-end token");
StringWriter out = new StringWriter();
XMLStreamWriter xmlWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(out);
xmlWriter.writeStartDocument("UTF-8", "1.0");
xmlWriter.writeStartElement("Features");
DocumentStaxUtils.writeFeatures(features, xmlWriter, "");
xmlWriter.writeEndElement();
xmlWriter.writeEndDocument();
xmlWriter.flush();
String result = out.toString();
assertTrue(result.contains("CDATA"));
assertTrue(result.contains("]]>"));
}

@Test(expected = XMLStreamException.class)
public void testWriteXcesAnnotationsFailsOnInvalidOffset() throws Exception {
Gate.init();
DocumentImpl doc = new DocumentImpl();
doc.setContent(new DocumentContentImpl("test"));
AnnotationSet set = doc.getAnnotations();
set.add(4L, 2L, "Bad", Factory.newFeatureMap());
StringWriter out = new StringWriter();
XMLStreamWriter xsw = XMLOutputFactory.newInstance().createXMLStreamWriter(out);
DocumentStaxUtils.writeXcesAnnotations(set, xsw);
}

@Test
public void testFeatureMapWithNullKeyOrValueIsIgnored() throws Exception {
Gate.init();
FeatureMap features = Factory.newFeatureMap();
features.put("validKey", "validValue");
features.put(null, "something");
features.put("somethingElse", null);
StringWriter writer = new StringWriter();
XMLStreamWriter xsw = XMLOutputFactory.newInstance().createXMLStreamWriter(writer);
xsw.writeStartDocument("UTF-8", "1.0");
xsw.writeStartElement("Features");
DocumentStaxUtils.writeFeatures(features, xsw, "");
xsw.writeEndElement();
xsw.writeEndDocument();
xsw.flush();
String xml = writer.toString();
assertTrue(xml.contains("validKey"));
assertTrue(xml.contains("validValue"));
assertFalse(xml.contains("somethingElse"));
}

@Test
public void testReplaceXMLIllegalCharactersLeavesValidTextUnchanged() {
String str = "normal safe string";
String result = DocumentStaxUtils.replaceXMLIllegalCharactersInString(str);
assertSame(str, result);
}

@Test
public void testIsInvalidXmlCharSurrogatePair() {
char[] buf = new char[] { '\uD83D', '\uDE00' };
DocumentStaxUtils.ArrayCharSequence seq = new DocumentStaxUtils.ArrayCharSequence(buf);
boolean firstInvalid = DocumentStaxUtils.isInvalidXmlChar(seq, 0);
boolean secondInvalid = DocumentStaxUtils.isInvalidXmlChar(seq, 1);
assertFalse(firstInvalid);
assertFalse(secondInvalid);
}

@Test
public void testIsInvalidXmlCharUnpairedSurrogates() {
char[] buf = new char[] { '\uD800' };
DocumentStaxUtils.ArrayCharSequence seq = new DocumentStaxUtils.ArrayCharSequence(buf);
boolean invalid = DocumentStaxUtils.isInvalidXmlChar(seq, 0);
assertTrue(invalid);
}

@Test(expected = XMLStreamException.class)
public void testReadFeatureNameOrValueThrowsOnInvalidInnerElement() throws Exception {
String xml = "<Value className=\"java.lang.String\">" + "<badTag>invalid</badTag>" + "</Value>";
XMLInputFactory inputFactory = XMLInputFactory.newInstance();
XMLStreamReader reader = inputFactory.createXMLStreamReader(new ByteArrayInputStream(xml.getBytes("UTF-8")));
while (reader.hasNext()) {
if (reader.next() == XMLStreamConstants.START_ELEMENT && "Value".equals(reader.getLocalName())) {
break;
}
}
DocumentStaxUtils.readFeatureNameOrValue(reader);
}

@Test
public void testWriteCharactersWithLessThanThresholdDoesNotUseCData() throws Exception {
StringWriter sw = new StringWriter();
XMLStreamWriter xsw = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
xsw.writeStartDocument("UTF-8", "1.0");
xsw.writeStartElement("data");
DocumentStaxUtils.writeCharactersOrCDATA(xsw, "A < B");
xsw.writeEndElement();
xsw.writeEndDocument();
xsw.flush();
String result = sw.toString();
assertFalse(result.contains("<![CDATA["));
assertTrue(result.contains("&lt;"));
}

@Test
public void testFeatureMapWithBooleanValue() throws Exception {
Gate.init();
FeatureMap features = Factory.newFeatureMap();
features.put("flag", Boolean.TRUE);
StringWriter writer = new StringWriter();
XMLStreamWriter xsw = XMLOutputFactory.newInstance().createXMLStreamWriter(writer);
xsw.writeStartDocument("UTF-8", "1.0");
xsw.writeStartElement("Document");
DocumentStaxUtils.writeFeatures(features, xsw, "");
xsw.writeEndElement();
xsw.writeEndDocument();
xsw.flush();
String result = writer.toString();
assertTrue(result.contains("flag"));
assertTrue(result.contains("true"));
}

@Test
public void testWriteXcesAnnotationsIgnoresIsEmptyAndSpan() throws Exception {
Gate.init();
DocumentImpl doc = new DocumentImpl();
doc.setContent(new DocumentContentImpl("testing!"));
FeatureMap fm = Factory.newFeatureMap();
fm.put("tag", "X");
fm.put("isEmptyAndSpan", "shouldBeIgnored");
AnnotationSet set = doc.getAnnotations();
set.add(0L, 3L, "X", fm);
StringWriter writer = new StringWriter();
XMLStreamWriter xsw = XMLOutputFactory.newInstance().createXMLStreamWriter(writer);
xsw.writeStartDocument("UTF-8", "1.0");
DocumentStaxUtils.writeXcesAnnotations(set, xsw);
xsw.writeEndDocument();
xsw.flush();
String result = writer.toString();
assertTrue(result.contains("tag"));
assertFalse(result.contains("isEmptyAndSpan"));
}

@Test
public void testArrayCharSequenceToStringEqualsSubstring() {
char[] array = new char[] { 'h', 'e', 'l', 'l', 'o', '!', '!' };
DocumentStaxUtils.ArrayCharSequence seq = new DocumentStaxUtils.ArrayCharSequence(array, 1, 4);
String result = seq.toString();
assertEquals("ello", result);
}

@Test
public void testWriteAnnotationSetAnnotationWithNoFeatures() throws Exception {
Gate.init();
DocumentImpl doc = new DocumentImpl();
doc.setContent(new DocumentContentImpl("abc"));
AnnotationSet set = doc.getAnnotations();
set.add(0L, 3L, "Token", Factory.newFeatureMap());
StringWriter output = new StringWriter();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(output);
writer.writeStartDocument("UTF-8", "1.0");
DocumentStaxUtils.writeAnnotationSet(set, "Tokens", writer, "");
writer.writeEndDocument();
writer.flush();
String xml = output.toString();
assertTrue(xml.contains("Annotation"));
assertTrue(xml.contains("Tokens"));
assertFalse(xml.contains("Feature"));
}

@Test
public void testWriteXcesContentWithNullEncodingDefaultsToUtf8() throws Exception {
Gate.init();
DocumentImpl doc = new DocumentImpl();
doc.setContent(new DocumentContentImpl("XCES Default Encoding"));
ByteArrayOutputStream baos = new ByteArrayOutputStream();
DocumentStaxUtils.writeXcesContent(doc, baos, null);
String result = baos.toString("UTF-8");
assertEquals("XCES Default Encoding", result);
}

@Test
public void testCDataEscapingWithEmbeddedEndToken() throws Exception {
StringWriter sw = new StringWriter();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
writer.writeStartDocument("UTF-8", "1.0");
writer.writeStartElement("Text");
DocumentStaxUtils.writeCharactersOrCDATA(writer, "content ]]> content ]]> final");
writer.writeEndElement();
writer.writeEndDocument();
writer.flush();
String xml = sw.toString();
assertTrue(xml.contains("<![CDATA["));
assertTrue(xml.contains("]]>"));
assertTrue(xml.contains("content"));
}

@Test
public void testReadFeatureMapWithUninstantiableCollectionFallBacksToString() throws Exception {
String xml = "<GateDocumentFeatures>" + "<Feature>" + "<Name className=\"java.lang.String\">numbers</Name>" + "<Value className=\"java.util.AbstractList\" itemClassName=\"java.lang.String\">1;2;3</Value>" + "</Feature>" + "</GateDocumentFeatures>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new ByteArrayInputStream(xml.getBytes("UTF-8")));
while (reader.hasNext()) {
if (reader.next() == XMLStreamConstants.START_ELEMENT && "GateDocumentFeatures".equals(reader.getLocalName())) {
break;
}
}
FeatureMap map = DocumentStaxUtils.readFeatureMap(reader);
assertTrue(map.containsKey("numbers"));
Object value = map.get("numbers");
assertEquals("1;2;3", value);
}

@Test
public void testWriteFeaturesWithIllegalCharactersInValue() throws Exception {
FeatureMap features = Factory.newFeatureMap();
features.put("corrupt", "legal \u0000 illegal \uFFFE value");
StringWriter sw = new StringWriter();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
writer.writeStartDocument("UTF-8", "1.0");
writer.writeStartElement("Features");
DocumentStaxUtils.writeFeatures(features, writer, "");
writer.writeEndElement();
writer.writeEndDocument();
writer.flush();
String output = sw.toString();
assertTrue(output.contains("legal"));
assertFalse(output.contains("\u0000"));
assertFalse(output.contains("\uFFFE"));
}

@Test
public void testWriteAnnotationSetWithNullDefaultSetName() throws Exception {
Gate.init();
DocumentImpl doc = new DocumentImpl();
doc.setContent(new DocumentContentImpl("text"));
FeatureMap fm = Factory.newFeatureMap();
doc.getAnnotations().add(0L, 4L, "Tag", fm);
StringWriter sw = new StringWriter();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
writer.writeStartDocument("UTF-8", "1.0");
DocumentStaxUtils.writeAnnotationSet(doc.getAnnotations(), writer, "");
writer.writeEndDocument();
writer.flush();
String result = sw.toString();
assertTrue(result.contains("AnnotationSet"));
assertTrue(result.contains("Tag"));
}

@Test(expected = XMLStreamException.class)
public void testReadRelationSetMissingTypeAttributeThrowsException() throws Exception {
String xml = "<RelationSet>" + "<Relation Id=\"1\" Members=\"1;2\">" + "<UserData></UserData>" + "<Feature><Name className=\"java.lang.String\">f</Name><Value className=\"java.lang.String\">v</Value></Feature>" + "</Relation>" + "</RelationSet>";
XMLInputFactory inputFactory = XMLInputFactory.newInstance();
XMLStreamReader reader = inputFactory.createXMLStreamReader(new ByteArrayInputStream(xml.getBytes("UTF-8")));
while (reader.hasNext()) {
if (reader.next() == XMLStreamConstants.START_ELEMENT && "RelationSet".equals(reader.getLocalName())) {
break;
}
}
RelationSet rs = Factory.newDocument("content").getAnnotations().getRelations();
DocumentStaxUtils.readRelationSet(reader, rs, new HashSet<Integer>());
}

@Test
public void testReadGateDocumentFeaturesElementWithoutFeatureChildren() throws Exception {
String xml = "<GateDocumentFeatures></GateDocumentFeatures>";
XMLInputFactory inputFactory = XMLInputFactory.newInstance();
XMLStreamReader reader = inputFactory.createXMLStreamReader(new ByteArrayInputStream(xml.getBytes("UTF-8")));
while (reader.hasNext()) {
if (reader.next() == XMLStreamConstants.START_ELEMENT && "GateDocumentFeatures".equals(reader.getLocalName())) {
break;
}
}
FeatureMap map = DocumentStaxUtils.readFeatureMap(reader);
assertTrue(map.isEmpty());
}

@Test(expected = XMLStreamException.class)
public void testAnnotationWithNonIntegerStartNodeThrowsException() throws Exception {
String xml = "<AnnotationSet>" + "<Annotation Type=\"X\" StartNode=\"abc\" EndNode=\"4\">" + "<Feature><Name className=\"java.lang.String\">k</Name><Value className=\"java.lang.String\">v</Value></Feature>" + "</Annotation>" + "</AnnotationSet>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new ByteArrayInputStream(xml.getBytes("UTF-8")));
while (reader.hasNext()) {
if (reader.next() == XMLStreamConstants.START_ELEMENT && "AnnotationSet".equals(reader.getLocalName())) {
break;
}
}
Map<Integer, Long> nodeToOffset = new HashMap<Integer, Long>();
nodeToOffset.put(4, 4L);
Set<Integer> annIds = new HashSet<Integer>();
DocumentImpl doc = new DocumentImpl();
AnnotationSet annotationSet = doc.getAnnotations();
DocumentStaxUtils.readAnnotationSet(reader, annotationSet, nodeToOffset, annIds, null);
}

@Test(expected = XMLStreamException.class)
public void testAnnotationWithNonIntegerEndNodeThrowsException() throws Exception {
String xml = "<AnnotationSet>" + "<Annotation Type=\"X\" StartNode=\"1\" EndNode=\"N/A\">" + "<Feature><Name className=\"java.lang.String\">k</Name><Value className=\"java.lang.String\">v</Value></Feature>" + "</Annotation>" + "</AnnotationSet>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new ByteArrayInputStream(xml.getBytes("UTF-8")));
while (reader.hasNext()) {
if (reader.next() == XMLStreamConstants.START_ELEMENT && "AnnotationSet".equals(reader.getLocalName())) {
break;
}
}
Map<Integer, Long> nodeToOffset = new HashMap<Integer, Long>();
nodeToOffset.put(1, 1L);
Set<Integer> annIds = new HashSet<Integer>();
DocumentImpl doc = new DocumentImpl();
AnnotationSet annotationSet = doc.getAnnotations();
DocumentStaxUtils.readAnnotationSet(reader, annotationSet, nodeToOffset, annIds, null);
}

@Test(expected = XMLStreamException.class)
public void testRelationSetWithEmptyMembersAttributeThrows() throws Exception {
String xml = "<RelationSet>" + "<Relation Id=\"1\" Type=\"coRef\" Members=\"\">" + "<UserData></UserData>" + "</Relation>" + "</RelationSet>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new ByteArrayInputStream(xml.getBytes("UTF-8")));
while (reader.hasNext()) {
if (reader.next() == XMLStreamConstants.START_ELEMENT && "RelationSet".equals(reader.getLocalName())) {
break;
}
}
RelationSet relSet = Factory.newDocument("sample").getAnnotations().getRelations();
DocumentStaxUtils.readRelationSet(reader, relSet, new HashSet<Integer>());
}

@Test(expected = XMLStreamException.class)
public void testFeatureMapMissingValueElement() throws Exception {
String xml = "<GateDocumentFeatures>" + "<Feature>" + "<Name className=\"java.lang.String\">aKey</Name>" + "</Feature></GateDocumentFeatures>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new ByteArrayInputStream(xml.getBytes("UTF-8")));
while (reader.hasNext()) {
if (reader.next() == XMLStreamConstants.START_ELEMENT && "GateDocumentFeatures".equals(reader.getLocalName())) {
break;
}
}
DocumentStaxUtils.readFeatureMap(reader);
}

@Test(expected = XMLStreamException.class)
public void testAnnotationSetMissingStartNodeAttributeThrows() throws Exception {
String xml = "<AnnotationSet>" + "<Annotation Type=\"Entity\" EndNode=\"4\">" + "<Feature><Name className=\"java.lang.String\">id</Name><Value className=\"java.lang.String\">42</Value></Feature>" + "</Annotation></AnnotationSet>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new ByteArrayInputStream(xml.getBytes("UTF-8")));
while (reader.hasNext()) {
if (reader.next() == XMLStreamConstants.START_ELEMENT && "AnnotationSet".equals(reader.getLocalName())) {
break;
}
}
DocumentImpl doc = new DocumentImpl();
Map<Integer, Long> nodeMap = new HashMap<Integer, Long>();
nodeMap.put(4, 4L);
Set<Integer> ids = new HashSet<Integer>();
DocumentStaxUtils.readAnnotationSet(reader, doc.getAnnotations(), nodeMap, ids, null);
}

@Test
public void testWriteFeaturesWithUnsupportedCustomObjectFallsToString() throws Exception {
class UnknownClass {

@Override
public String toString() {
return "custom-object";
}
}
FeatureMap fm = Factory.newFeatureMap();
fm.put("object", new UnknownClass());
StringWriter sw = new StringWriter();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
writer.writeStartDocument("UTF-8", "1.0");
writer.writeStartElement("F");
DocumentStaxUtils.writeFeatures(fm, writer, "");
writer.writeEndElement();
writer.writeEndDocument();
writer.flush();
String xml = sw.toString();
assertTrue(xml.contains("object"));
assertTrue(xml.contains("custom-object"));
}

@Test(expected = XMLStreamException.class)
public void testReadAnnotationSetMissingTypeAttributeFails() throws Exception {
String xml = "<AnnotationSet>" + "<Annotation StartNode=\"0\" EndNode=\"4\">" + "<Feature><Name className=\"java.lang.String\">key</Name>" + "<Value className=\"java.lang.String\">val</Value></Feature>" + "</Annotation>" + "</AnnotationSet>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new ByteArrayInputStream(xml.getBytes("UTF-8")));
while (reader.hasNext()) {
if (reader.next() == XMLStreamConstants.START_ELEMENT && "AnnotationSet".equals(reader.getLocalName())) {
break;
}
}
DocumentImpl doc = new DocumentImpl();
Map<Integer, Long> nodeMap = new HashMap<Integer, Long>();
nodeMap.put(0, 0L);
nodeMap.put(4, 4L);
Set<Integer> ids = new HashSet<Integer>();
DocumentStaxUtils.readAnnotationSet(reader, doc.getAnnotations(), nodeMap, ids, null);
}

@Test
public void testWriteTextWithNodesHandlesSequentialOffsets() throws Exception {
Gate.init();
DocumentImpl doc = new DocumentImpl();
doc.setContent(new DocumentContentImpl("ab"));
AnnotationSet set = doc.getAnnotations();
set.add(0L, 1L, "X", Factory.newFeatureMap());
set.add(1L, 2L, "Y", Factory.newFeatureMap());
Collection<Collection<Annotation>> sets = new ArrayList<Collection<Annotation>>();
sets.add(set);
StringWriter sw = new StringWriter();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
DocumentStaxUtils.writeTextWithNodes(doc, sets, writer, "");
writer.flush();
String xml = sw.toString();
assertTrue(xml.contains("Node"));
assertTrue(xml.contains("id=\"0\""));
assertTrue(xml.contains("id=\"1\""));
assertTrue(xml.contains("id=\"2\""));
}

@Test
public void testWriteFeaturesWithNewlineCharacters() throws Exception {
FeatureMap fm = Factory.newFeatureMap();
fm.put("key", "multi\nline\nvalue");
StringWriter sw = new StringWriter();
XMLStreamWriter xsw = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
xsw.writeStartDocument("UTF-8", "1.0");
xsw.writeStartElement("FeatBlock");
DocumentStaxUtils.writeFeatures(fm, xsw, "");
xsw.writeEndElement();
xsw.writeEndDocument();
xsw.flush();
String result = sw.toString();
assertTrue(result.contains("multi"));
assertTrue(result.contains("\n") || result.contains("&#10;"));
}

@Test(expected = XMLStreamException.class)
public void testReadXcesFeatureMapWithMissingNameAttributeFails() throws Exception {
String xml = "<cesAna xmlns=\"" + DocumentStaxUtils.XCES_NAMESPACE + "\" version=\"1.0\">" + "<struct type=\"x\" from=\"0\" to=\"3\">" + "<feat value=\"v\"/>" + "</struct>" + "</cesAna>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new ByteArrayInputStream(xml.getBytes("UTF-8")));
while (reader.hasNext()) {
if (reader.next() == XMLStreamConstants.START_ELEMENT && "cesAna".equals(reader.getLocalName())) {
break;
}
}
DocumentImpl doc = new DocumentImpl();
AnnotationSet set = doc.getAnnotations();
DocumentStaxUtils.readXces(reader, set);
}

@Test
public void testWriteXcesAnnotationsHandlesIdenticalSpans() throws Exception {
Gate.init();
DocumentImpl doc = new DocumentImpl();
doc.setContent(new DocumentContentImpl("hello"));
FeatureMap fm1 = Factory.newFeatureMap();
fm1.put("type", "A");
FeatureMap fm2 = Factory.newFeatureMap();
fm2.put("type", "B");
AnnotationSet set = doc.getAnnotations();
set.add(0L, 5L, "word", fm1);
set.add(0L, 5L, "phrase", fm2);
StringWriter sw = new StringWriter();
XMLStreamWriter xsw = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
xsw.writeStartDocument("UTF-8", "1.0");
DocumentStaxUtils.writeXcesAnnotations(set, xsw);
xsw.writeEndDocument();
xsw.flush();
String xml = sw.toString();
assertTrue(xml.contains("word"));
assertTrue(xml.contains("phrase"));
assertTrue(xml.contains("<struct"));
}

@Test(expected = XMLStreamException.class)
public void testTextWithNodesNodeElementWithNonIntegerIdFails() throws Exception {
String xml = "<TextWithNodes>" + "<Node id=\"abc\"/>" + "</TextWithNodes>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new ByteArrayInputStream(xml.getBytes("UTF-8")));
while (reader.hasNext()) {
if (reader.next() == XMLStreamConstants.START_ELEMENT && "TextWithNodes".equals(reader.getLocalName())) {
break;
}
}
DocumentStaxUtils.readTextWithNodes(reader, new HashMap<Integer, Long>());
}

@Test
public void testWriteAnnotationSetWithNullFeatures() throws Exception {
Gate.init();
DocumentImpl doc = new DocumentImpl();
doc.setContent(new DocumentContentImpl("abc"));
AnnotationSet annotationSet = doc.getAnnotations();
annotationSet.add(0L, 1L, "Token", null);
StringWriter sw = new StringWriter();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
writer.writeStartDocument("UTF-8", "1.0");
DocumentStaxUtils.writeAnnotationSet(annotationSet, writer, "");
writer.writeEndDocument();
writer.flush();
String output = sw.toString();
assertTrue(output.contains("Annotation"));
assertTrue(output.contains("Token"));
}

@Test
public void testReadFeatureMapWithEmptyFeatureElement() throws Exception {
String xml = "<GateDocumentFeatures>" + "<Feature></Feature>" + "</GateDocumentFeatures>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new ByteArrayInputStream(xml.getBytes("UTF-8")));
while (reader.hasNext()) {
if (reader.next() == XMLStreamConstants.START_ELEMENT && "GateDocumentFeatures".equals(reader.getLocalName())) {
break;
}
}
FeatureMap fm = DocumentStaxUtils.readFeatureMap(reader);
assertEquals(0, fm.size());
}

@Test
public void testWriteFeaturesWithNullValuesAreSkipped() throws Exception {
FeatureMap fm = Factory.newFeatureMap();
fm.put("x", null);
fm.put(null, "value");
StringWriter sw = new StringWriter();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
writer.writeStartDocument("UTF-8", "1.0");
writer.writeStartElement("Data");
DocumentStaxUtils.writeFeatures(fm, writer, "");
writer.writeEndElement();
writer.writeEndDocument();
writer.flush();
String output = sw.toString();
assertFalse(output.contains("Feature"));
}

@Test
public void testReadXcesHandlesEmptyAnnotationBlock() throws Exception {
String xml = "<cesAna xmlns=\"" + DocumentStaxUtils.XCES_NAMESPACE + "\" version=\"1.0\">" + "</cesAna>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new ByteArrayInputStream(xml.getBytes("UTF-8")));
while (reader.hasNext()) {
if (reader.next() == XMLStreamConstants.START_ELEMENT && "cesAna".equals(reader.getLocalName())) {
break;
}
}
DocumentImpl doc = new DocumentImpl();
AnnotationSet as = doc.getAnnotations();
DocumentStaxUtils.readXces(reader, as);
assertEquals(0, as.size());
}

@Test
public void testReadXcesWithStructWithoutFeatures() throws Exception {
String xml = "<cesAna xmlns=\"" + DocumentStaxUtils.XCES_NAMESPACE + "\" version=\"1.0\">" + "<struct type=\"Entity\" from=\"0\" to=\"5\"/>" + "</cesAna>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new ByteArrayInputStream(xml.getBytes("UTF-8")));
while (reader.hasNext()) {
if (reader.next() == XMLStreamConstants.START_ELEMENT && "cesAna".equals(reader.getLocalName())) {
break;
}
}
DocumentImpl doc = new DocumentImpl();
AnnotationSet as = doc.getAnnotations();
DocumentStaxUtils.readXces(reader, as);
assertEquals(1, as.size());
Annotation ann = as.iterator().next();
assertEquals("Entity", ann.getType());
}

@Test
public void testWriteDocumentToStringIncludesXMLDeclaration() throws Exception {
Gate.init();
DocumentImpl doc = new DocumentImpl();
doc.setContent(new DocumentContentImpl("data"));
doc.getAnnotations().add(0L, 4L, "Tag", Factory.newFeatureMap());
String xmlOutput = DocumentStaxUtils.toXml(doc);
assertTrue(xmlOutput.startsWith("<?xml"));
}

@Test
public void testReplaceXMLIllegalCharactersPreservesBOMCharacters() {
String str = "\uFEFFvalid\uFFFEinvalid";
String cleaned = DocumentStaxUtils.replaceXMLIllegalCharactersInString(str);
assertFalse(cleaned.contains("\uFFFE"));
assertTrue(cleaned.contains("valid"));
}

@Test(expected = XMLStreamException.class)
public void testReadFeatureNameOrValueMissingClassFailsGracefully() throws Exception {
String xml = "<Name></Name>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new ByteArrayInputStream(xml.getBytes("UTF-8")));
while (reader.hasNext()) {
if (reader.next() == XMLStreamConstants.START_ELEMENT && "Name".equals(reader.getLocalName())) {
break;
}
}
Object val = DocumentStaxUtils.readFeatureNameOrValue(reader);
assertEquals("", val.toString());
}

@Test
public void testWriteTextWithNodesWhenNoOffsetsExistGeneratesValidXML() throws Exception {
Gate.init();
DocumentImpl doc = new DocumentImpl();
doc.setContent(new DocumentContentImpl("Hello"));
StringWriter xmlWriter = new StringWriter();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(xmlWriter);
writer.writeStartDocument("UTF-8", "1.0");
DocumentStaxUtils.writeTextWithNodes(doc, writer, "");
writer.writeEndDocument();
writer.flush();
String xml = xmlWriter.toString();
assertTrue(xml.contains("TextWithNodes"));
}

@Test(expected = XMLStreamException.class)
public void testNextTagSkipDTDThrowsOnInvalidEvent() throws Exception {
String xml = "<!DOCTYPE x [ ]> <root>text</root>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new ByteArrayInputStream(xml.getBytes("UTF-8")));
while (reader.hasNext() && reader.next() != XMLStreamConstants.DTD) {
}
reader.next();
DocumentStaxUtils.class.getDeclaredMethod("nextTagSkipDTD", XMLStreamReader.class).setAccessible(true);
DocumentStaxUtils.class.getDeclaredMethod("nextTagSkipDTD", XMLStreamReader.class).invoke(null, (Object) reader);
}
}
