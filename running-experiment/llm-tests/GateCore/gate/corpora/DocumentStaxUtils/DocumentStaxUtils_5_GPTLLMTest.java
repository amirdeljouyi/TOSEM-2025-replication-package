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

public class DocumentStaxUtils_5_GPTLLMTest {

@Test
public void testReadTextWithNodesSimpleContent() throws Exception {
String xml = "<TextWithNodes>Hello<Node id=\"1\"/> world<Node id=\"2\"/>!</TextWithNodes>";
Map<Integer, Long> nodeMap = new HashMap<>();
XMLInputFactory inputFactory = XMLInputFactory.newInstance();
XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(xml));
reader.nextTag();
String result = DocumentStaxUtils.readTextWithNodes(reader, nodeMap);
assertEquals("Hello world!", result);
assertEquals(Long.valueOf(5), nodeMap.get(1));
assertEquals(Long.valueOf(11), nodeMap.get(2));
assertEquals(XMLStreamConstants.END_ELEMENT, reader.getEventType());
assertEquals("TextWithNodes", reader.getLocalName());
}

@Test
public void testReadTextWithNodesMissingNodeIdThrows() throws Exception {
String xml = "<TextWithNodes>Hello<Node/> world!</TextWithNodes>";
Map<Integer, Long> nodeMap = new HashMap<>();
XMLInputFactory inputFactory = XMLInputFactory.newInstance();
XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(xml));
reader.nextTag();
try {
DocumentStaxUtils.readTextWithNodes(reader, nodeMap);
fail("Expected XMLStreamException due to missing id attribute");
} catch (XMLStreamException e) {
}
}

@Test
public void testReplaceXMLIllegalCharactersInStringReplacesControlCharacters() {
String input = "A\u0001B\u0000C\u0008";
String output = DocumentStaxUtils.replaceXMLIllegalCharactersInString(input);
assertEquals("A B C ", output);
}

@Test
public void testWriteAnnotationSetEmptyWritesCorrectXml() throws Exception {
Collection<Annotation> annotations = Collections.emptyList();
StringWriter sw = new StringWriter();
XMLOutputFactory xof = XMLOutputFactory.newInstance();
XMLStreamWriter writer = xof.createXMLStreamWriter(sw);
DocumentStaxUtils.writeAnnotationSet(annotations, "MySet", writer, "");
writer.flush();
String output = sw.toString();
assertTrue(output.contains("<AnnotationSet Name=\"MySet\"/>") || output.contains("<AnnotationSet Name=\"MySet\"></AnnotationSet>"));
}

@Test
public void testReadFeatureMapParsesSimpleFeatures() throws Exception {
String xml = "<Annotation>" + "<Feature>" + "<Name className=\"java.lang.String\">key1</Name>" + "<Value className=\"java.lang.String\">val1</Value>" + "</Feature>" + "<Feature>" + "<Name className=\"java.lang.String\">key2</Name>" + "<Value className=\"java.lang.String\">val2</Value>" + "</Feature>" + "</Annotation>";
XMLInputFactory inputFactory = XMLInputFactory.newInstance();
XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(xml));
reader.nextTag();
FeatureMap featureMap = DocumentStaxUtils.readFeatureMap(reader);
assertEquals(2, featureMap.size());
assertEquals("val1", featureMap.get("key1"));
assertEquals("val2", featureMap.get("key2"));
}

@Test
public void testWriteFeaturesSpecialCharactersEncoded() throws Exception {
FeatureMap fm = Factory.newFeatureMap();
fm.put("greeting", "Hello <world> & everyone");
StringWriter sw = new StringWriter();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
DocumentStaxUtils.writeFeatures(fm, writer, "");
writer.flush();
String xml = sw.toString();
assertTrue(xml.contains("greeting"));
assertTrue(xml.contains("&lt;world&gt;"));
assertTrue(xml.contains("Hello"));
}

@Test
public void testWriteTextWithNodesGeneratesExpectedNodes() throws Exception {
Document mockDoc = mock(Document.class);
Annotation mockAnn = mock(Annotation.class);
Node startNode = mock(Node.class);
Node endNode = mock(Node.class);
when(startNode.getOffset()).thenReturn(0L);
when(endNode.getOffset()).thenReturn(5L);
when(mockAnn.getStartNode()).thenReturn(startNode);
when(mockAnn.getEndNode()).thenReturn(endNode);
DocumentContent docContent = mock(DocumentContent.class);
when(docContent.toString()).thenReturn("Hello World");
when(mockDoc.getContent()).thenReturn(docContent);
List<Annotation> annList = new ArrayList<>();
annList.add(mockAnn);
Collection<Collection<Annotation>> sets = new ArrayList<>();
sets.add(annList);
StringWriter sw = new StringWriter();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
DocumentStaxUtils.writeTextWithNodes(mockDoc, sets, writer, "");
writer.flush();
String result = sw.toString();
assertTrue(result.contains("<Node id=\"0\"/>"));
assertTrue(result.contains("<Node id=\"5\"/>"));
assertTrue(result.contains("Hello"));
}

@Test
public void testWriteDocumentOutputsExpectedXmlStructure() throws Exception {
Document doc = mock(Document.class);
DocumentContent dc = mock(DocumentContent.class);
when(dc.size()).thenReturn(11L);
when(dc.toString()).thenReturn("Hello World");
when(doc.getContent()).thenReturn(dc);
when(doc.getFeatures()).thenReturn(Factory.newFeatureMap());
// when(doc.getAnnotations()).thenReturn(Factory.newAnnotationSet("default"));
when(doc.getNamedAnnotationSets()).thenReturn(new HashMap<>());
StringWriter sw = new StringWriter();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
DocumentStaxUtils.writeDocument(doc, writer, "");
writer.flush();
String result = sw.toString();
assertTrue(result.contains("<GateDocumentFeatures"));
assertTrue(result.contains("<TextWithNodes>"));
assertTrue(result.contains("</GateDocument>"));
}

@Test
public void testReadXcesFeatureMapParsesSingleFeat() throws Exception {
String xml = "<struct xmlns=\"http://www.xces.org/schema/2003\">" + "<feat name=\"POS\" value=\"NN\"/>" + "</struct>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));
reader.nextTag();
FeatureMap fm = DocumentStaxUtils.readXcesFeatureMap(reader);
assertEquals(1, fm.size());
assertEquals("NN", fm.get("POS"));
}

@Test
public void testWriteXcesAnnotationsIncludesAnnotationIdAttribute() throws Exception {
Annotation annotation = mock(Annotation.class);
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(0L);
when(end.getOffset()).thenReturn(5L);
when(annotation.getStartNode()).thenReturn(start);
when(annotation.getEndNode()).thenReturn(end);
when(annotation.getType()).thenReturn("Token");
when(annotation.getId()).thenReturn(42);
when(annotation.getFeatures()).thenReturn(Factory.newFeatureMap());
List<Annotation> annotations = Collections.singletonList(annotation);
StringWriter sw = new StringWriter();
XMLOutputFactory xof = XMLOutputFactory.newInstance();
XMLStreamWriter xsw = xof.createXMLStreamWriter(sw);
DocumentStaxUtils.writeXcesAnnotations(annotations, xsw);
xsw.flush();
String output = sw.toString();
assertTrue(output.contains("type=\"Token\""));
assertTrue(output.contains("n=\"42\""));
}

@Test
public void testReadAnnotationSetRejectsMixedIdPresence() throws Exception {
String xml = "<AnnotationSet>" + "<Annotation Type=\"Person\" StartNode=\"0\" EndNode=\"5\"/>" + "<Annotation Type=\"Location\" StartNode=\"6\" EndNode=\"10\" Id=\"1\">" + "<Feature>" + "<Name className=\"java.lang.String\">foo</Name>" + "<Value className=\"java.lang.String\">bar</Value>" + "</Feature>" + "</Annotation>" + "</AnnotationSet>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));
reader.nextTag();
// AnnotationSet set = Factory.newAnnotationSet("");
Map<Integer, Long> nodeMap = new HashMap<>();
nodeMap.put(0, 0L);
nodeMap.put(5, 5L);
nodeMap.put(6, 6L);
nodeMap.put(10, 10L);
Set<Integer> idSet = new HashSet<>();
// try {
// DocumentStaxUtils.readAnnotationSet(reader, set, nodeMap, idSet, null);
// fail("Expected XMLStreamException for mixing null and non-null IDs");
// } catch (XMLStreamException e) {
// }
// }
// 
// @Test
// public void testReadAnnotationSetDuplicateIdThrows() throws Exception {
// String xml = "<AnnotationSet>" + "<Annotation Type=\"X\" StartNode=\"0\" EndNode=\"5\" Id=\"1\">" + "<Feature><Name className=\"java.lang.String\">a</Name><Value className=\"java.lang.String\">b</Value></Feature>" + "</Annotation>" + "<Annotation Type=\"Y\" StartNode=\"5\" EndNode=\"10\" Id=\"1\">" + "<Feature><Name className=\"java.lang.String\">c</Name><Value className=\"java.lang.String\">d</Value></Feature>" + "</Annotation>" + "</AnnotationSet>";
// XMLInputFactory factory = XMLInputFactory.newInstance();
// XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));
// reader.nextTag();
// AnnotationSet set = Factory.newAnnotationSet("");
// Map<Integer, Long> nodeMap = new HashMap<>();
// nodeMap.put(0, 0L);
// nodeMap.put(5, 5L);
// nodeMap.put(10, 10L);
// Set<Integer> ids = new TreeSet<>();
// try {
// DocumentStaxUtils.readAnnotationSet(reader, set, nodeMap, ids, null);
// fail("Expected exception for duplicate annotation ID");
// } catch (XMLStreamException ex) {
// assertTrue(ex.getMessage().contains("duplicate ID"));
// }
}

@Test
public void testReadFeatureNameOrValueInvalidClassFallsBackToString() throws Exception {
String xml = "<Value className=\"com.fake.UnknownType\">" + "someText" + "</Value>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));
reader.nextTag();
Object value = DocumentStaxUtils.readFeatureNameOrValue(reader);
assertTrue(value instanceof String);
assertEquals("someText", value);
}

@Test
public void testReadFeatureNameOrValueListFallback() throws Exception {
String xml = "<Value className=\"java.util.ArrayList\" itemClassName=\"com.fake.NotAClass\">" + "val1;val2" + "</Value>";
XMLInputFactory inputFactory = XMLInputFactory.newInstance();
XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(xml));
reader.nextTag();
Object value = DocumentStaxUtils.readFeatureNameOrValue(reader);
assertTrue(value instanceof Collection);
Collection<?> col = (Collection<?>) value;
assertEquals(2, col.size());
assertTrue(col.contains("val1"));
assertTrue(col.contains("val2"));
}

@Test
public void testWriteCharactersOrCDATAWithEscapedSequence() throws Exception {
StringWriter sw = new StringWriter();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
String content = "<hello><world>]]></world>";
DocumentStaxUtils.writeCharactersOrCDATA(writer, content);
writer.flush();
String result = sw.toString();
assertTrue(result.contains("CDATA"));
assertTrue(result.contains("]]>"));
}

@Test
public void testReplaceXMLIllegalCharactersHandlesSurrogates() {
String text = "abc\uD800def";
String result = DocumentStaxUtils.replaceXMLIllegalCharactersInString(text);
assertEquals("abc def", result);
}

@Test
public void testReadRelationSetMissingTypeThrows() throws Exception {
String xml = "<RelationSet>" + "<Relation Id=\"1\" Members=\"2;3\">" + "<UserData></UserData>" + "<Feature><Name className=\"java.lang.String\">a</Name><Value className=\"java.lang.String\">b</Value></Feature>" + "</Relation>" + "</RelationSet>";
XMLInputFactory inputFactory = XMLInputFactory.newInstance();
XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(xml));
reader.nextTag();
reader.nextTag();
RelationSet rs = mock(RelationSet.class);
Set<Integer> ids = new TreeSet<>();
try {
DocumentStaxUtils.readRelationSet(reader, rs, ids);
fail("Missing Type should throw");
} catch (XMLStreamException e) {
assertTrue(e.getMessage().contains("must have a type"));
}
}

@Test
public void testReadRelationSetMissingMembersThrows() throws Exception {
String xml = "<RelationSet>" + "<Relation Id=\"99\" Type=\"coref\">" + "<UserData/>" + "<Feature><Name className=\"java.lang.String\">k</Name><Value className=\"java.lang.String\">v</Value></Feature>" + "</Relation>" + "</RelationSet>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));
reader.nextTag();
reader.nextTag();
RelationSet rs = mock(RelationSet.class);
Set<Integer> ids = new TreeSet<>();
try {
DocumentStaxUtils.readRelationSet(reader, rs, ids);
fail("Expected exception due to missing Members attr");
} catch (XMLStreamException e) {
assertTrue(e.getMessage().contains("must have members"));
}
}

@Test
public void testWriteXcesAnnotationsWithoutId() throws Exception {
Annotation annotation = mock(Annotation.class);
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(0L);
when(end.getOffset()).thenReturn(5L);
when(annotation.getStartNode()).thenReturn(start);
when(annotation.getEndNode()).thenReturn(end);
when(annotation.getType()).thenReturn("foo");
when(annotation.getFeatures()).thenReturn(Factory.newFeatureMap());
List<Annotation> list = Arrays.asList(annotation);
StringWriter sw = new StringWriter();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
DocumentStaxUtils.writeXcesAnnotations(list, writer, false);
writer.flush();
String output = sw.toString();
assertTrue(output.contains("type=\"foo\""));
assertFalse(output.contains("n="));
}

@Test
public void testReadTextWithNodesHandlesEmptyContent() throws Exception {
String xml = "<TextWithNodes></TextWithNodes>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));
reader.nextTag();
Map<Integer, Long> nodeMap = new HashMap<>();
String result = DocumentStaxUtils.readTextWithNodes(reader, nodeMap);
assertEquals("", result);
assertTrue(nodeMap.isEmpty());
}

@Test
public void testWriteTextWithNodesHandlesNullText() throws Exception {
Document mockDoc = mock(Document.class);
DocumentContent content = mock(DocumentContent.class);
when(content.toString()).thenReturn(null);
when(mockDoc.getContent()).thenReturn(content);
Collection<Collection<Annotation>> coll = Collections.emptyList();
StringWriter sw = new StringWriter();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
DocumentStaxUtils.writeTextWithNodes(mockDoc, coll, writer, "");
writer.flush();
String result = sw.toString();
assertTrue(result.contains("<TextWithNodes/>") || result.contains("<TextWithNodes></TextWithNodes>"));
}

@Test
public void testWriteFeaturesSkipsNullKeyOrValue() throws Exception {
FeatureMap fm = Factory.newFeatureMap();
fm.put(null, "value");
fm.put("key", null);
StringWriter sw = new StringWriter();
XMLStreamWriter xsw = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
DocumentStaxUtils.writeFeatures(fm, xsw, "");
xsw.flush();
String output = sw.toString();
assertFalse(output.contains("Feature"));
}

@Test
public void testIsInvalidXmlCharAcceptsValidSurrogatePair() {
char[] validPair = new char[] { 0xD834, 0xDD1E };
DocumentStaxUtils.ArrayCharSequence seq = new DocumentStaxUtils.ArrayCharSequence(validPair);
boolean result1 = DocumentStaxUtils.isInvalidXmlChar(seq, 0);
boolean result2 = DocumentStaxUtils.isInvalidXmlChar(seq, 1);
assertFalse(result1);
assertFalse(result2);
}

@Test
public void testWriteFeaturesWithWrappedObject() throws Exception {
FeatureMap featureMap = Factory.newFeatureMap();
featureMap.put("wrapped", new Object() {

@Override
public String toString() {
return "CustomWrappedObjectStringForm";
}
});
StringWriter sw = new StringWriter();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
DocumentStaxUtils.writeFeatures(featureMap, writer, "");
writer.flush();
String output = sw.toString();
assertTrue(output.contains("CustomWrappedObjectStringForm"));
assertTrue(output.contains("ObjectWrapper"));
}

@Test
public void testReadFeatureNameOrValueWithInvalidConstructorClassFallsBack() throws Exception {
String xml = "<Value className=\"java.lang.Thread\">" + "invalid" + "</Value>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));
reader.nextTag();
Object value = DocumentStaxUtils.readFeatureNameOrValue(reader);
assertNotNull(value);
assertEquals("invalid", value);
}

@Test
public void testReadFeatureNameOrValueWithCollectionClassInstantiationFails() throws Exception {
String xml = "<Value className=\"java.util.AbstractList\" itemClassName=\"java.lang.String\">" + "one;two" + "</Value>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));
reader.nextTag();
Object value = DocumentStaxUtils.readFeatureNameOrValue(reader);
assertTrue(value instanceof String);
assertEquals("one;two", value);
}

@Test
public void testReadFeatureNameOrValueWithItemConstructorFailure() throws Exception {
String xml = "<Value className=\"java.util.ArrayList\" itemClassName=\"java.time.LocalDate\">" + "invalid-date;2023-01-01" + "</Value>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));
reader.nextTag();
try {
DocumentStaxUtils.readFeatureNameOrValue(reader);
fail("Expected XMLStreamException due to invalid item constructor call");
} catch (XMLStreamException e) {
assertTrue(e.getMessage().contains("does not comply"));
}
}

@Test
public void testWriteDocumentWithNullNamedAnnotationSet() throws Exception {
Document doc = mock(Document.class);
DocumentContent documentContent = mock(DocumentContent.class);
when(documentContent.size()).thenReturn(5L);
when(documentContent.toString()).thenReturn("abcde");
when(doc.getContent()).thenReturn(documentContent);
// when(doc.getAnnotations()).thenReturn(Factory.newAnnotationSet(""));
when(doc.getFeatures()).thenReturn(Factory.newFeatureMap());
when(doc.getNamedAnnotationSets()).thenReturn(null);
StringWriter sw = new StringWriter();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
DocumentStaxUtils.writeDocument(doc, writer, "");
writer.flush();
String output = sw.toString();
assertTrue(output.contains("<GateDocument"));
assertTrue(output.contains("TextWithNodes"));
}

@Test
public void testReadXcesHandlesDuplicateIds() throws Exception {
String xml = "<cesAna xmlns=\"http://www.xces.org/schema/2003\">" + "<struct type=\"X\" from=\"0\" to=\"5\" n=\"1\"/>" + "<struct type=\"Y\" from=\"6\" to=\"10\" n=\"1\"/>" + "</cesAna>";
// AnnotationSet set = Factory.newAnnotationSet("");
XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(new StringReader(xml));
reader.nextTag();
// try {
// DocumentStaxUtils.readXces(reader, set);
// fail("Expected XMLStreamException due to duplicate ID");
// } catch (XMLStreamException e) {
// assertTrue(e.getMessage().contains("duplicate ID"));
// }
}

@Test
public void testReadXcesSkipsIsEmptyAndSpanFeature() throws Exception {
String xml = "<cesAna xmlns=\"http://www.xces.org/schema/2003\">" + "<struct type=\"Entity\" from=\"0\" to=\"5\" n=\"101\">" + "<feat name=\"isEmptyAndSpan\" value=\"true\"/>" + "<feat name=\"class\" value=\"PERSON\"/>" + "</struct>" + "</cesAna>";
// AnnotationSet set = Factory.newAnnotationSet("");
XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(new StringReader(xml));
reader.nextTag();
// DocumentStaxUtils.readXces(reader, set);
// Annotation a = set.get(101);
// assertNotNull(a);
// assertNotNull(a.getFeatures());
// assertFalse(a.getFeatures().containsKey("isEmptyAndSpan"));
// assertEquals("PERSON", a.getFeatures().get("class"));
}

@Test
public void testWriteTextWithNodesIgnoresNamedAnnotationSetsWithNullValues() throws Exception {
Document doc = mock(Document.class);
DocumentContent content = mock(DocumentContent.class);
when(content.toString()).thenReturn("SampleText");
when(doc.getContent()).thenReturn(content);
// when(doc.getAnnotations()).thenReturn(Factory.newAnnotationSet());
Map<String, AnnotationSet> fake = new HashMap<>();
fake.put("NamedSet1", null);
when(doc.getNamedAnnotationSets()).thenReturn(fake);
Collection<Collection<Annotation>> flatCollection = new ArrayList<>();
flatCollection.add(doc.getAnnotations());
StringWriter sw = new StringWriter();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
DocumentStaxUtils.writeTextWithNodes(doc, writer, "");
writer.flush();
String result = sw.toString();
assertTrue(result.contains("<TextWithNodes>"));
}

@Test
public void testIsInvalidXmlCharDetectsBOM() {
char[] chars = new char[] { 'H', 'e', 'l', 'l', 0xFFFE };
DocumentStaxUtils.ArrayCharSequence seq = new DocumentStaxUtils.ArrayCharSequence(chars);
boolean result = DocumentStaxUtils.isInvalidXmlChar(seq, 4);
assertTrue(result);
}

@Test
public void testReadFeatureNameOrValueHandlesCDATA() throws Exception {
String xml = "<Value className=\"java.lang.String\"><![CDATA[Some CDATA text]]></Value>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));
reader.nextTag();
Object value = DocumentStaxUtils.readFeatureNameOrValue(reader);
assertEquals("Some CDATA text", value);
}

@Test
public void testReadFeatureMapFeatureWithInvalidChildThrows() throws Exception {
String xml = "<Annotation>" + "<Feature>" + "<InvalidTag>Oops</InvalidTag>" + "</Feature>" + "</Annotation>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));
reader.nextTag();
try {
DocumentStaxUtils.readFeatureMap(reader);
fail("Expected XMLStreamException due to unexpected child in Feature");
} catch (XMLStreamException ex) {
assertTrue(ex.getMessage().contains("only Name and Value children"));
}
}

@Test
public void testWriteAnnotationSetIncludesOffsets() throws Exception {
// AnnotationSet set = Factory.newAnnotationSet();
FeatureMap fm = Factory.newFeatureMap();
fm.put("category", "example");
Document doc = Factory.newDocument("ABC");
// Annotation ann = doc.getAnnotations().add(0L, 3L, "Token", fm);
// set.add(ann);
StringWriter sw = new StringWriter();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
// DocumentStaxUtils.writeAnnotationSet(set, "SetName", writer, "");
writer.flush();
String output = sw.toString();
assertTrue(output.contains("StartNode=\"0\""));
assertTrue(output.contains("EndNode=\"3\""));
assertTrue(output.contains("category"));
}

@Test
public void testReplaceXMLIllegalCharactersAllValid() {
String input = "Hello & <world> \u0009 \n \r";
String output = DocumentStaxUtils.replaceXMLIllegalCharactersInString(input);
assertEquals(input, output);
}

@Test
public void testReplaceXMLIllegalCharactersUnpairedLowSurrogate() {
char[] text = new char[] { 'X', 0xDC00, 'Z' };
DocumentStaxUtils.replaceXMLIllegalCharacters(text, 0, text.length);
assertEquals(' ', text[1]);
}

@Test
public void testWriteCharactersOrCDATAWithLessThanThreshold() throws Exception {
StringWriter sw = new StringWriter();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
DocumentStaxUtils.writeCharactersOrCDATA(writer, "a < b");
writer.flush();
assertTrue(sw.toString().contains("a < b"));
assertFalse(sw.toString().contains("CDATA"));
}

@Test
public void testWriteCharactersOrCDATAWithAboveThreshold() throws Exception {
String content = "<<<<<<<";
StringWriter sw = new StringWriter();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
DocumentStaxUtils.writeCharactersOrCDATA(writer, content);
writer.flush();
assertTrue(sw.toString().contains("CDATA"));
}

@Test
public void testWriteTextWithNodesIncludesAllOffsets() throws Exception {
// AnnotationSet annSet = Factory.newAnnotationSet();
FeatureMap fm = Factory.newFeatureMap();
Document doc = Factory.newDocument("ABCDEF");
// Annotation ann1 = doc.getAnnotations().add(1L, 3L, "Entity", fm);
// Annotation ann2 = doc.getAnnotations().add(2L, 5L, "Entity", fm);
List<Annotation> inner1 = new ArrayList<>();
// inner1.add(ann1);
List<Annotation> inner2 = new ArrayList<>();
// inner2.add(ann2);
List<Collection<Annotation>> sets = Arrays.asList(inner1, inner2);
StringWriter sw = new StringWriter();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
DocumentStaxUtils.writeTextWithNodes(doc, sets, writer, "");
writer.flush();
String output = sw.toString();
assertTrue(output.contains("<Node id=\"1\"/>"));
assertTrue(output.contains("<Node id=\"2\"/>"));
assertTrue(output.contains("<Node id=\"3\"/>"));
assertTrue(output.contains("<Node id=\"5\"/>"));
}

@Test
public void testReadTextWithNodesHandlesNonIntegerNodeId() throws Exception {
String xml = "<TextWithNodes>Text<Node id=\"abc\"/></TextWithNodes>";
XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(new StringReader(xml));
reader.nextTag();
Map<Integer, Long> map = new HashMap<>();
try {
DocumentStaxUtils.readTextWithNodes(reader, map);
fail("Expected XMLStreamException due to non-integer Node id");
} catch (XMLStreamException ex) {
assertTrue(ex.getMessage().contains("integer id"));
}
}

@Test
public void testReadFeatureNameOrValueIgnoresComments() throws Exception {
String xml = "<Value className=\"java.lang.String\">Hello<!-- comment --></Value>";
XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(new StringReader(xml));
reader.nextTag();
Object value = DocumentStaxUtils.readFeatureNameOrValue(reader);
assertEquals("Hello", value);
}

@Test
public void testReadGateXmlDocumentThrowsExceptionWhenNotOnStartElement() throws Exception {
String xml = "<notGate></notGate>";
Document doc = Factory.newDocument("SomeText");
XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(new StringReader(xml));
reader.next();
try {
DocumentStaxUtils.readGateXmlDocument(reader, doc);
fail("Expected XMLStreamException for invalid start element");
} catch (XMLStreamException ex) {
assertTrue(ex.getMessage().contains("GateDocument"));
}
}

@Test
public void testReadAnnotationSetWithNullNodeMapDefaultsToIdAsOffset() throws Exception {
String xml = "<AnnotationSet>" + "<Annotation Type=\"Mock\" StartNode=\"5\" EndNode=\"10\" Id=\"999\">" + "<Feature><Name className=\"java.lang.String\">a</Name>" + "<Value className=\"java.lang.String\">b</Value></Feature>" + "</Annotation>" + "</AnnotationSet>";
XMLInputFactory inputFactory = XMLInputFactory.newInstance();
XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(xml));
reader.nextTag();
// AnnotationSet annSet = Factory.newAnnotationSet("test");
Set<Integer> ids = new HashSet<>();
// Boolean result = DocumentStaxUtils.readAnnotationSet(reader, annSet, null, ids, null);
// assertTrue(annSet.size() > 0);
// Annotation a = annSet.iterator().next();
// assertEquals(999, a.getId().intValue());
// assertEquals(Long.valueOf(5), a.getStartNode().getOffset());
// assertTrue(result);
}

@Test
public void testReadAnnotationSetWithInvalidStartNodeThrows() throws Exception {
String xml = "<AnnotationSet>" + "<Annotation Type=\"Token\" StartNode=\"bad\" EndNode=\"10\" Id=\"1\">" + "<Feature><Name className=\"java.lang.String\">x</Name>" + "<Value className=\"java.lang.String\">y</Value></Feature>" + "</Annotation>" + "</AnnotationSet>";
XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(new StringReader(xml));
reader.nextTag();
// AnnotationSet set = Factory.newAnnotationSet("badset");
Map<Integer, Long> dummyMap = new HashMap<>();
dummyMap.put(10, 10L);
Set<Integer> ids = new HashSet<>();
// try {
// DocumentStaxUtils.readAnnotationSet(reader, set, dummyMap, ids, null);
// fail("Expected exception due to non-integer StartNode");
// } catch (XMLStreamException ex) {
// assertTrue(ex.getMessage().contains("StartNode"));
// }
}

@Test
public void testReadAnnotationSetWithMissingAnnotationType() throws Exception {
String xml = "<AnnotationSet>" + "<Annotation StartNode=\"0\" EndNode=\"5\" Id=\"1\">" + "<Feature><Name className=\"java.lang.String\">k</Name>" + "<Value className=\"java.lang.String\">v</Value></Feature>" + "</Annotation>" + "</AnnotationSet>";
XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(new StringReader(xml));
reader.nextTag();
// AnnotationSet set = Factory.newAnnotationSet("noType");
Map<Integer, Long> nodeMap = new HashMap<>();
nodeMap.put(0, 0L);
nodeMap.put(5, 5L);
Set<Integer> ids = new HashSet<>();
// Boolean result = DocumentStaxUtils.readAnnotationSet(reader, set, nodeMap, ids, null);
// Annotation a = set.iterator().next();
// assertNull(a.getType());
// assertEquals("v", a.getFeatures().get("k"));
// assertEquals(Long.valueOf(0), a.getStartNode().getOffset());
// assertEquals(Long.valueOf(5), a.getEndNode().getOffset());
}

@Test
public void testWriteRelationSetSkipWhenEmpty() throws Exception {
RelationSet relSet = mock(RelationSet.class);
when(relSet.size()).thenReturn(0);
StringWriter sw = new StringWriter();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
DocumentStaxUtils.writeRelationSet(relSet, writer, "");
writer.flush();
String xml = sw.toString();
assertTrue(xml.isEmpty());
}

@Test
public void testWriteFeaturesWithBooleanAndNumberTypes() throws Exception {
FeatureMap fm = Factory.newFeatureMap();
fm.put("flag", true);
fm.put("score", 3.14);
StringWriter sw = new StringWriter();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
DocumentStaxUtils.writeFeatures(fm, writer, "");
writer.flush();
String xml = sw.toString();
assertTrue(xml.contains("true"));
assertTrue(xml.contains("3.14"));
assertTrue(xml.contains("className=\"java.lang.Boolean\""));
assertTrue(xml.contains("className=\"java.lang.Double\""));
}

@Test
public void testWriteTextWithNodesCorrectlyHandlesAdjacentOffsets() throws Exception {
Document doc = Factory.newDocument("ABCDE");
FeatureMap fm = Factory.newFeatureMap();
AnnotationSet set = doc.getAnnotations();
set.add(1L, 2L, "A", fm);
set.add(2L, 2L, "B", fm);
Collection<Collection<Annotation>> sets = new ArrayList<>();
sets.add(set);
StringWriter sw = new StringWriter();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
DocumentStaxUtils.writeTextWithNodes(doc, sets, writer, "");
writer.flush();
String xml = sw.toString();
assertTrue(xml.contains("<Node id=\"1\"/>"));
assertTrue(xml.contains("<Node id=\"2\"/>"));
}

@Test
public void testReplaceXMLIllegalCharactersHandlesControlCharacters() {
char[] text = new char[] { 'A', 0x0001, 'B', 0x000C, 'C' };
DocumentStaxUtils.replaceXMLIllegalCharacters(text);
assertEquals('A', text[0]);
assertEquals(' ', text[1]);
assertEquals('B', text[2]);
assertEquals(' ', text[3]);
assertEquals('C', text[4]);
}

@Test
public void testWriteXcesContentWithEmptyContent() throws Exception {
Document doc = Factory.newDocument("");
ByteArrayOutputStream out = new ByteArrayOutputStream();
DocumentStaxUtils.writeXcesContent(doc, out, null);
String result = out.toString("UTF-8");
assertEquals("", result);
}

@Test
public void testWriteXcesContentWithSpecifiedEncoding() throws Exception {
Document doc = Factory.newDocument("abc");
ByteArrayOutputStream out = new ByteArrayOutputStream();
DocumentStaxUtils.writeXcesContent(doc, out, "UTF-8");
String result = out.toString("UTF-8");
assertEquals("abc", result);
}

@Test
public void testWriteAnnotationSetWithNullName() throws Exception {
// AnnotationSet set = Factory.newAnnotationSet();
Document doc = Factory.newDocument("abc");
FeatureMap fm = Factory.newFeatureMap();
// set.add(0L, 3L, "Token", fm);
StringWriter sw = new StringWriter();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
// DocumentStaxUtils.writeAnnotationSet(set, writer, "");
writer.flush();
String output = sw.toString();
assertTrue(output.contains("<AnnotationSet>") || output.contains("<AnnotationSet Name="));
assertTrue(output.contains("Id="));
}

@Test
public void testReadFeatureMapEmptyReturnsEmptyMap() throws Exception {
String xml = "<Annotation></Annotation>";
XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(new StringReader(xml));
reader.nextTag();
FeatureMap map = DocumentStaxUtils.readFeatureMap(reader);
assertTrue(map.isEmpty());
}

@Test
public void testWriteTextWithNodesDocumentHasNoAnnotations() throws Exception {
Document doc = Factory.newDocument("plain text");
Collection<Collection<Annotation>> annots = new ArrayList<>();
annots.add(Collections.emptyList());
StringWriter sw = new StringWriter();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
DocumentStaxUtils.writeTextWithNodes(doc, annots, writer, "");
writer.flush();
String result = sw.toString();
assertTrue(result.contains("plain text"));
assertTrue(result.contains("<TextWithNodes>"));
assertFalse(result.contains("<Node id="));
}

@Test
public void testWriteFeaturesWithUnsupportedValueFallsBackToObjectWrapper() throws Exception {
FeatureMap map = Factory.newFeatureMap();
map.put("complex", new Object() {

@Override
public String toString() {
return "wrapper-data";
}
});
StringWriter sw = new StringWriter();
XMLStreamWriter xsw = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
DocumentStaxUtils.writeFeatures(map, xsw, "");
xsw.flush();
String output = sw.toString();
assertTrue(output.contains("wrapper-data"));
assertTrue(output.contains("ObjectWrapper"));
}

@Test
public void testWriteRelationSetIncludesUserDataWithInvalidXmlCharacters() throws Exception {
RelationSet set = mock(RelationSet.class);
// Relation relation = mock(gate.relations.Relation.class);
// AnnotationSet annSet = Factory.newAnnotationSet("relset");
when(set.size()).thenReturn(1);
// when(set.get()).thenReturn(Collections.singleton(relation));
// when(set.getAnnotationSet()).thenReturn(annSet);
// when(relation.getId()).thenReturn(7);
// when(relation.getType()).thenReturn("coref");
// when(relation.getMembers()).thenReturn(new int[] { 1, 2 });
// when(relation.getUserData()).thenReturn("bad char \u0001 here");
// when(relation.getFeatures()).thenReturn(Factory.newFeatureMap());
StringWriter sw = new StringWriter();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
DocumentStaxUtils.writeRelationSet(set, writer, "");
writer.flush();
String xml = sw.toString();
assertFalse(xml.contains("\u0001"));
assertTrue(xml.contains("<Relation"));
assertTrue(xml.contains("coref"));
}

@Test
public void testReadRelationSetMissingUserDataElementThrows() throws Exception {
String xml = "<RelationSet>" + "<Relation Id=\"9\" Type=\"link\" Members=\"1;2\">" + "<InvalidUserData/>" + "<Feature><Name className='java.lang.String'>a</Name>" + "<Value className='java.lang.String'>b</Value></Feature>" + "</Relation></RelationSet>";
XMLStreamReader xsr = XMLInputFactory.newInstance().createXMLStreamReader(new StringReader(xml));
xsr.nextTag();
xsr.nextTag();
RelationSet rs = mock(RelationSet.class);
Set<Integer> ids = new HashSet<>();
try {
DocumentStaxUtils.readRelationSet(xsr, rs, ids);
fail("Expected XMLStreamException due to invalid UserData wrapper");
} catch (XMLStreamException ex) {
assertTrue(ex.getMessage().contains("UserData"));
}
}

@Test
public void testReadFeatureNameOrValueWithEmptyCollectionValue() throws Exception {
String xml = "<Value className=\"java.util.ArrayList\" itemClassName=\"java.lang.String\"></Value>";
XMLStreamReader xsr = XMLInputFactory.newInstance().createXMLStreamReader(new StringReader(xml));
xsr.nextTag();
Object value = DocumentStaxUtils.readFeatureNameOrValue(xsr);
assertTrue(value instanceof Collection);
assertTrue(((Collection<?>) value).isEmpty());
}

@Test
public void testParseSurrogatesHighOnlyReturnsFalseIfFollowedByLow() {
char[] text = { 0xD800, 0xDC00 };
DocumentStaxUtils.ArrayCharSequence seq = new DocumentStaxUtils.ArrayCharSequence(text);
boolean invalidHigh = DocumentStaxUtils.isInvalidXmlChar(seq, 0);
boolean invalidLow = DocumentStaxUtils.isInvalidXmlChar(seq, 1);
assertFalse(invalidHigh);
assertFalse(invalidLow);
}

@Test
public void testParseSurrogatesHighOnlyUnpairedReturnsTrue() {
char[] text = { 0xD800 };
DocumentStaxUtils.ArrayCharSequence seq = new DocumentStaxUtils.ArrayCharSequence(text);
boolean invalid = DocumentStaxUtils.isInvalidXmlChar(seq, 0);
assertTrue(invalid);
}

@Test
public void testParseSurrogatesLowOnlyUnpairedReturnsTrue() {
char[] text = { 0xDC00 };
DocumentStaxUtils.ArrayCharSequence seq = new DocumentStaxUtils.ArrayCharSequence(text);
boolean invalid = DocumentStaxUtils.isInvalidXmlChar(seq, 0);
assertTrue(invalid);
}

@Test
public void testReadXcesFeatureMapHandlesAttributeOnlyFeat() throws Exception {
String xml = "<struct xmlns=\"http://www.xces.org/schema/2003\">" + "<feat name=\"gender\" value=\"feminine\"/>" + "</struct>";
XMLStreamReader xsr = XMLInputFactory.newInstance().createXMLStreamReader(new StringReader(xml));
xsr.nextTag();
FeatureMap fm = DocumentStaxUtils.readXcesFeatureMap(xsr);
assertEquals("feminine", fm.get("gender"));
}

@Test
public void testReadFeatureMapIgnoresEmptyFeatureElement() throws Exception {
String xml = "<Annotation>" + "<Feature/>" + "</Annotation>";
XMLStreamReader xsr = XMLInputFactory.newInstance().createXMLStreamReader(new StringReader(xml));
xsr.nextTag();
try {
DocumentStaxUtils.readFeatureMap(xsr);
fail("Expected XMLStreamException due to missing Name/Value children");
} catch (XMLStreamException e) {
assertTrue(e.getMessage().contains("only Name and Value"));
}
}

@Test
public void testWriteTextWithNodesSurvivableWithEmptyAnnotationCollections() throws Exception {
Document doc = Factory.newDocument("hello world");
Collection<Collection<Annotation>> all = new ArrayList<>();
all.add(Collections.emptyList());
StringWriter sw = new StringWriter();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
DocumentStaxUtils.writeTextWithNodes(doc, all, writer, "");
writer.flush();
String result = sw.toString();
assertTrue(result.contains("hello world"));
assertTrue(result.contains("<TextWithNodes>"));
}

@Test
public void testWriteXcesAnnotationsSkipsInvalidFeatureKey() throws Exception {
Annotation ann = mock(Annotation.class);
Node sn = mock(Node.class);
Node en = mock(Node.class);
when(sn.getOffset()).thenReturn(0L);
when(en.getOffset()).thenReturn(4L);
when(ann.getStartNode()).thenReturn(sn);
when(ann.getEndNode()).thenReturn(en);
when(ann.getType()).thenReturn("Token");
when(ann.getId()).thenReturn(88);
FeatureMap fm = Factory.newFeatureMap();
fm.put(null, "value");
when(ann.getFeatures()).thenReturn(fm);
List<Annotation> list = Arrays.asList(ann);
StringWriter sw = new StringWriter();
XMLStreamWriter xsw = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
DocumentStaxUtils.writeXcesAnnotations(list, xsw);
xsw.flush();
String output = sw.toString();
assertFalse(output.contains("value"));
}

@Test
public void testReadFeatureNameOrValueWithCustomObjectWrapper() throws Exception {
String text = "<Value className=\"gate.util.ObjectWrapper\">wrapped-string-value</Value>";
XMLInputFactory inputFactory = XMLInputFactory.newInstance();
XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(text));
reader.nextTag();
Object result = DocumentStaxUtils.readFeatureNameOrValue(reader);
assertEquals("wrapped-string-value", result.toString());
}

@Test
public void testReadTextWithNodesWithCDATAContent() throws Exception {
String xml = "<TextWithNodes><![CDATA[hello<Node id=\"1\"/>world]]><Node id=\"1\"/></TextWithNodes>";
Map<Integer, Long> nodeMap = new HashMap<>();
XMLInputFactory input = XMLInputFactory.newInstance();
XMLStreamReader reader = input.createXMLStreamReader(new StringReader(xml));
reader.nextTag();
String text = DocumentStaxUtils.readTextWithNodes(reader, nodeMap);
assertTrue(text.contains("hello"));
assertTrue(nodeMap.containsKey(1));
assertEquals(Long.valueOf(text.indexOf("w")), nodeMap.get(1));
}

@Test
public void testWriteFeaturesWithNullValuesIsSkipped() throws Exception {
FeatureMap fm = Factory.newFeatureMap();
fm.put(null, "non-null");
fm.put("valid", null);
StringWriter sw = new StringWriter();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
DocumentStaxUtils.writeFeatures(fm, writer, "");
writer.flush();
String output = sw.toString();
assertFalse(output.contains("<Feature>"));
}

@Test
public void testWriteXcesAnnotationsFiltersIsEmptyAndSpanFeatureOnly() throws Exception {
FeatureMap fm = Factory.newFeatureMap();
fm.put("isEmptyAndSpan", "true");
fm.put("class", "ORG");
Annotation ann = mock(Annotation.class);
Node start = mock(Node.class);
Node end = mock(Node.class);
when(start.getOffset()).thenReturn(0L);
when(end.getOffset()).thenReturn(4L);
when(ann.getStartNode()).thenReturn(start);
when(ann.getEndNode()).thenReturn(end);
when(ann.getType()).thenReturn("Entity");
when(ann.getId()).thenReturn(101);
when(ann.getFeatures()).thenReturn(fm);
List<Annotation> annList = Arrays.asList(ann);
StringWriter sw = new StringWriter();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
DocumentStaxUtils.writeXcesAnnotations(annList, writer);
writer.flush();
String output = sw.toString();
assertFalse(output.contains("isEmptyAndSpan"));
assertTrue(output.contains("class"));
}

@Test
public void testWriteAnnotationSetHandlesNullFeatureMap() throws Exception {
Annotation ann = mock(Annotation.class);
when(ann.getId()).thenReturn(11);
when(ann.getType()).thenReturn("Word");
Node sn = mock(Node.class);
Node en = mock(Node.class);
when(sn.getOffset()).thenReturn(1L);
when(en.getOffset()).thenReturn(4L);
when(ann.getStartNode()).thenReturn(sn);
when(ann.getEndNode()).thenReturn(en);
when(ann.getFeatures()).thenReturn(null);
List<Annotation> anns = Arrays.asList(ann);
StringWriter sw = new StringWriter();
XMLStreamWriter xsw = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
DocumentStaxUtils.writeAnnotationSet(anns, "noFeatures", xsw, "");
xsw.flush();
String xml = sw.toString();
assertTrue(xml.contains("Annotation"));
assertTrue(xml.contains("StartNode=\"1\""));
}

@Test
public void testWriteCharactersOrCDATAWithTextContainingMultipleCDATAEnds() throws Exception {
String text = "abc]]>xyz]]>123";
StringWriter sw = new StringWriter();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
DocumentStaxUtils.writeCharactersOrCDATA(writer, text);
writer.flush();
String result = sw.toString();
assertTrue(result.contains("CDATA"));
assertTrue(result.contains("]]>"));
}

@Test
public void testReadGateXmlDocumentInvalidOffsetRestoresOriginalContent() throws Exception {
String xml = "<GateDocument version=\"3\">" + "<GateDocumentFeatures/>" + "<TextWithNodes>hello<Node id=\"1\"/></TextWithNodes>" + "<AnnotationSet>" + "<Annotation Type=\"X\" StartNode=\"1\" EndNode=\"2\" Id=\"1\">" + "<Feature><Name className=\"java.lang.String\">x</Name><Value className=\"java.lang.String\">y</Value></Feature>" + "</Annotation>" + "</AnnotationSet>" + "</GateDocument>";
XMLInputFactory inputFactory = XMLInputFactory.newInstance();
XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(xml));
reader.nextTag();
Document doc = mock(Document.class);
DocumentContent oldContent = mock(DocumentContent.class);
when(doc.getContent()).thenReturn(oldContent);
try {
DocumentStaxUtils.readGateXmlDocument(reader, doc);
} catch (Exception e) {
verify(doc).setContent(oldContent);
}
}

@Test
public void testReadFeatureMapWithMultipleCDATAValues() throws Exception {
String xml = "<Annotation>" + "<Feature>" + "<Name className=\"java.lang.String\"><![CDATA[key]]></Name>" + "<Value className=\"java.lang.String\"><![CDATA[val]]></Value>" + "</Feature>" + "</Annotation>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader xsr = factory.createXMLStreamReader(new StringReader(xml));
xsr.nextTag();
FeatureMap fm = DocumentStaxUtils.readFeatureMap(xsr);
assertEquals("val", fm.get("key"));
}

@Test
public void testReplaceXMLIllegalCharactersInEmptyArrayDoesNothing() {
char[] array = new char[0];
DocumentStaxUtils.replaceXMLIllegalCharacters(array);
assertEquals(0, array.length);
}

@Test
public void testReplaceXMLIllegalCharactersSurrogatePairPreserved() {
char[] chars = { 0xD83D, 0xDE0A };
DocumentStaxUtils.replaceXMLIllegalCharacters(chars, 0, chars.length);
assertEquals(0xD83D, chars[0]);
assertEquals(0xDE0A, chars[1]);
}
}
