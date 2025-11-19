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

public class DocumentStaxUtils_2_GPTLLMTest {

@Test
public void testToXmlAndReadGateXmlDocumentRoundTrip() throws Exception {
Document originalDoc = Factory.newDocument("Simple XML document");
FeatureMap features = Factory.newFeatureMap();
features.put("source", "unit-test");
originalDoc.setFeatures(features);
originalDoc.getAnnotations().add(0L, 6L, "Token", Factory.newFeatureMap());
String xml = DocumentStaxUtils.toXml(originalDoc);
assertNotNull(xml);
assertTrue(xml.contains("GateDocument"));
Document targetDoc = Factory.newDocument("");
XMLInputFactory inputFactory = XMLInputFactory.newInstance();
XMLStreamReader reader = inputFactory.createXMLStreamReader(new ByteArrayInputStream(xml.getBytes("UTF-8")));
while (reader.hasNext() && !(reader.isStartElement() && "GateDocument".equals(reader.getLocalName()))) {
reader.next();
}
DocumentStaxUtils.readGateXmlDocument(reader, targetDoc);
assertEquals(originalDoc.getContent().toString(), targetDoc.getContent().toString());
assertEquals(originalDoc.getAnnotations().size(), targetDoc.getAnnotations().size());
assertEquals(originalDoc.getFeatures(), targetDoc.getFeatures());
Factory.deleteResource(originalDoc);
Factory.deleteResource(targetDoc);
}

@Test(expected = XMLStreamException.class)
public void testReadGateXmlDocumentThrowsWithoutGateDocumentElement() throws Exception {
String invalidXml = "<invalid></invalid>";
Document badDoc = Factory.newDocument("");
XMLInputFactory inputFactory = XMLInputFactory.newInstance();
XMLStreamReader reader = inputFactory.createXMLStreamReader(new ByteArrayInputStream(invalidXml.getBytes("UTF-8")));
reader.nextTag();
DocumentStaxUtils.readGateXmlDocument(reader, badDoc);
}

@Test
public void testToXmlEmptyDocument() throws Exception {
Document emptyDoc = Factory.newDocument("");
String xml = DocumentStaxUtils.toXml(emptyDoc);
assertNotNull(xml);
assertTrue(xml.contains("<GateDocument"));
XMLInputFactory inputFactory = XMLInputFactory.newInstance();
XMLStreamReader reader = inputFactory.createXMLStreamReader(new ByteArrayInputStream(xml.getBytes("UTF-8")));
while (reader.hasNext() && !(reader.isStartElement() && "GateDocument".equals(reader.getLocalName()))) {
reader.next();
}
Document newDoc = Factory.newDocument("");
DocumentStaxUtils.readGateXmlDocument(reader, newDoc);
assertEquals("", newDoc.getContent().toString());
assertTrue(newDoc.getAnnotations().isEmpty());
Factory.deleteResource(emptyDoc);
Factory.deleteResource(newDoc);
}

@Test
public void testReplaceXMLIllegalCharactersInString() {
String input = "valid\u0001text\uFFFF";
String result = DocumentStaxUtils.replaceXMLIllegalCharactersInString(input);
assertEquals("valid text ", result);
}

@Test(expected = XMLStreamException.class)
public void testReadXcesFailsOnDuplicateIds() throws Exception {
String xces = "<?xml version='1.0'?><cesAna xmlns='http://www.xces.org/schema/2003' version='1.0'>" + "<struct type='Token' from='0' to='5' n='1'><feat name='pos' value='NN'/></struct>" + "<struct type='Token' from='6' to='10' n='1'><feat name='pos' value='VB'/></struct></cesAna>";
Document doc = Factory.newDocument("test text content");
XMLInputFactory inputFactory = XMLInputFactory.newInstance();
XMLStreamReader xsr = inputFactory.createXMLStreamReader(new ByteArrayInputStream(xces.getBytes("UTF-8")));
DocumentStaxUtils.readXces(xsr, doc.getAnnotations());
}

@Test
public void testWriteXcesAnnotationsIncludesStructTag() throws Exception {
Document doc = Factory.newDocument("test1234");
AnnotationSet annSet = doc.getAnnotations();
annSet.add(0L, 4L, "Person", Factory.newFeatureMap());
ByteArrayOutputStream outStream = new ByteArrayOutputStream();
DocumentStaxUtils.writeXcesAnnotations(annSet, outStream, "UTF-8");
String result = outStream.toString("UTF-8");
assertTrue(result.contains("cesAna"));
assertTrue(result.contains("struct"));
assertTrue(result.contains("type=\"Person\""));
Factory.deleteResource(doc);
}

@Test
public void testReadTextWithNodesCorrectOffsets() throws Exception {
String xml = "<TextWithNodes>Hello<Node id='1'/>World<Node id='2'/>!</TextWithNodes>";
ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes("UTF-8"));
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(input);
reader.nextTag();
Map<Integer, Long> nodeMap = new HashMap<Integer, Long>();
String resultText = DocumentStaxUtils.readTextWithNodes(reader, nodeMap);
assertEquals("HelloWorld!", resultText);
assertEquals(Long.valueOf(5), nodeMap.get(1));
assertEquals(Long.valueOf(10), nodeMap.get(2));
}

@Test
public void testReplaceXMLIllegalCharactersArray() {
char[] chars = new char[] { 'a', '\u0001', 'b', '\uFFFE' };
DocumentStaxUtils.replaceXMLIllegalCharacters(chars);
assertArrayEquals(new char[] { 'a', ' ', 'b', ' ' }, chars);
}

@Test
public void testWriteXcesContent() throws Exception {
Document doc = Factory.newDocument("My test text");
ByteArrayOutputStream output = new ByteArrayOutputStream();
DocumentStaxUtils.writeXcesContent(doc, output, "UTF-8");
String result = output.toString("UTF-8");
assertEquals("My test text", result);
Factory.deleteResource(doc);
}

@Test
public void testWriteAndReadFeatureMap() throws Exception {
Document doc = Factory.newDocument("AAAAABBBB");
FeatureMap features = Factory.newFeatureMap();
features.put("tag", "NNP");
AnnotationSet annotations = doc.getAnnotations();
// Annotation annotation = annotations.add(0L, 5L, "Token", features);
String xml = DocumentStaxUtils.toXml(doc);
Document parsedDoc = Factory.newDocument("");
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new ByteArrayInputStream(xml.getBytes("UTF-8")));
while (reader.hasNext()) {
if (reader.isStartElement() && "GateDocument".equals(reader.getLocalName()))
break;
reader.next();
}
DocumentStaxUtils.readGateXmlDocument(reader, parsedDoc);
Annotation parsedAnn = parsedDoc.getAnnotations().get(0);
assertEquals("NNP", parsedAnn.getFeatures().get("tag"));
}

@Test(expected = XMLStreamException.class)
public void testReadTextWithNodesThrowsIfNodeMissingId() throws Exception {
String xml = "<TextWithNodes>Hello<Node/>World</TextWithNodes>";
ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes("UTF-8"));
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(input);
reader.nextTag();
Map<Integer, Long> nodeMap = new HashMap<Integer, Long>();
DocumentStaxUtils.readTextWithNodes(reader, nodeMap);
}

@Test(expected = XMLStreamException.class)
public void testReadTextWithNodesThrowsIfNodeIdNotInteger() throws Exception {
String xml = "<TextWithNodes>Hello<Node id='abc'/>World</TextWithNodes>";
ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes());
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(input);
reader.nextTag();
Map<Integer, Long> nodeMap = new HashMap<Integer, Long>();
DocumentStaxUtils.readTextWithNodes(reader, nodeMap);
}

@Test(expected = XMLStreamException.class)
public void testReadAnnotationSetThrowsWhenMissingStartNode() throws Exception {
String xml = "<AnnotationSet><Annotation EndNode='5' Type='Token'><Feature><Name>class</Name><Value>noun</Value></Feature></Annotation></AnnotationSet>";
ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes("UTF-8"));
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(input);
reader.nextTag();
Document doc = Factory.newDocument("hello");
AnnotationSet set = doc.getAnnotations();
Map<Integer, Long> nodeMap = new HashMap<Integer, Long>();
Set<Integer> allIds = new HashSet<Integer>();
DocumentStaxUtils.readAnnotationSet(reader, set, nodeMap, allIds, null);
}

@Test(expected = XMLStreamException.class)
public void testReadAnnotationSetThrowsWhenAnnotationIdNotInteger() throws Exception {
String xml = "<AnnotationSet><Annotation StartNode='0' EndNode='5' Type='Token' Id='abc'><Feature><Name>f</Name><Value>v</Value></Feature></Annotation></AnnotationSet>";
ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes());
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(input);
reader.nextTag();
Document doc = Factory.newDocument("hello");
AnnotationSet set = doc.getAnnotations();
Map<Integer, Long> nodeMap = new HashMap<Integer, Long>();
nodeMap.put(0, 0L);
nodeMap.put(5, 5L);
Set<Integer> allIds = new HashSet<Integer>();
DocumentStaxUtils.readAnnotationSet(reader, set, nodeMap, allIds, null);
}

@Test
public void testWriteFeaturesWithUnsupportedFeatureClassFallsBackToString() throws Exception {
Document doc = Factory.newDocument("example");
FeatureMap features = doc.getFeatures();
features.put("customObject", new Object());
ByteArrayOutputStream out = new ByteArrayOutputStream();
XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
XMLStreamWriter writer = outputFactory.createXMLStreamWriter(out, "UTF-8");
writer.writeStartDocument("UTF-8", "1.0");
writer.writeStartElement("GateDocumentFeatures");
DocumentStaxUtils.writeFeatures(features, writer, "");
writer.writeEndElement();
writer.writeEndDocument();
writer.flush();
writer.close();
String output = new String(out.toByteArray(), "UTF-8");
assertTrue(output.contains("customObject"));
assertTrue(output.contains("Feature"));
}

@Test
public void testWriteFeaturesWithCollection() throws Exception {
Document doc = Factory.newDocument("test");
FeatureMap features = doc.getFeatures();
List<String> list = new ArrayList<String>();
list.add("one");
list.add("two");
features.put("tags", list);
ByteArrayOutputStream out = new ByteArrayOutputStream();
XMLOutputFactory factory = XMLOutputFactory.newInstance();
XMLStreamWriter writer = factory.createXMLStreamWriter(out, "UTF-8");
writer.writeStartDocument();
writer.writeStartElement("GateDocumentFeatures");
DocumentStaxUtils.writeFeatures(features, writer, "");
writer.writeEndElement();
writer.writeEndDocument();
writer.flush();
writer.close();
String result = out.toString("UTF-8");
assertTrue(result.contains("one;two"));
assertTrue(result.contains("java.util"));
}

@Test
public void testWriteCharactersOrCdataWithEmbeddedCdataMarker() throws Exception {
String content = "Start with CDATA ]]><Tag>Content</Tag>";
ByteArrayOutputStream out = new ByteArrayOutputStream();
XMLOutputFactory factory = XMLOutputFactory.newInstance();
XMLStreamWriter writer = factory.createXMLStreamWriter(out, "UTF-8");
writer.writeStartDocument();
writer.writeStartElement("root");
DocumentStaxUtils.writeCharactersOrCDATA(writer, content);
writer.writeEndElement();
writer.writeEndDocument();
writer.flush();
writer.close();
String result = out.toString("UTF-8");
assertTrue(result.contains("CDATA"));
assertTrue(result.contains("]]>"));
}

@Test
public void testReadXcesFeatureMapHandlesEmptyFeatTag() throws Exception {
String xml = "<struct xmlns='http://www.xces.org/schema/2003'>" + "<feat name='pos' value='NN'/>" + "<feat name='lemma' value='dog'/>" + "</struct>";
ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes("UTF-8"));
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(input);
reader.nextTag();
FeatureMap fm = DocumentStaxUtils.readXcesFeatureMap(reader);
assertEquals("NN", fm.get("pos"));
assertEquals("dog", fm.get("lemma"));
}

@Test
public void testIsInvalidXmlCharReturnsTrueForControlChar() {
CharSequence seq = "abc\u0001";
boolean result = DocumentStaxUtils.isInvalidXmlChar(seq, 3);
assertTrue(result);
}

@Test
public void testIsInvalidXmlCharReturnsTrueForUnpairedHighSurrogate() {
CharSequence seq = "\uD800abc";
boolean result = DocumentStaxUtils.isInvalidXmlChar(seq, 0);
assertTrue(result);
}

@Test
public void testIsInvalidXmlCharReturnsTrueForUnpairedLowSurrogate() {
CharSequence seq = "a\uDC00b";
boolean result = DocumentStaxUtils.isInvalidXmlChar(seq, 1);
assertTrue(result);
}

@Test
public void testIsInvalidXmlCharReturnsTrueForFFFE() {
CharSequence seq = "abcd\uFFFE";
boolean result = DocumentStaxUtils.isInvalidXmlChar(seq, 4);
assertTrue(result);
}

@Test
public void testIsInvalidXmlCharReturnsFalseForValidChar() {
CharSequence seq = "valid";
boolean result = DocumentStaxUtils.isInvalidXmlChar(seq, 0);
assertFalse(result);
}

@Test(expected = XMLStreamException.class)
public void testReadFeatureMapUnexpectedElement() throws Exception {
String xml = "<Annotation><Feature><Foo/></Feature></Annotation>";
ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes("UTF-8"));
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(input);
while (reader.hasNext() && !"Annotation".equals(reader.getLocalName())) {
reader.next();
}
reader.nextTag();
DocumentStaxUtils.readFeatureMap(reader);
}

@Test(expected = XMLStreamException.class)
public void testReadFeatureMapNestedElementInValue() throws Exception {
String xml = "<Annotation><Feature><Name className='java.lang.String'>key</Name><Value><Inner/></Value></Feature></Annotation>";
ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes("UTF-8"));
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(input);
while (reader.hasNext()) {
if ("Annotation".equals(reader.getLocalName()))
break;
reader.next();
}
reader.nextTag();
DocumentStaxUtils.readFeatureMap(reader);
}

@Test(expected = XMLStreamException.class)
public void testReadTextWithNodesRejectsNonEmptyNode() throws Exception {
String xml = "<TextWithNodes>Hello<Node id='1'>abc</Node>World</TextWithNodes>";
ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader xsr = factory.createXMLStreamReader(stream);
xsr.nextTag();
Map<Integer, Long> nodeMap = new HashMap<Integer, Long>();
DocumentStaxUtils.readTextWithNodes(xsr, nodeMap);
}

@Test(expected = XMLStreamException.class)
public void testReadRelationSetMissingType() throws Exception {
String xml = "<RelationSet><Relation Id='10' Members='1;2'><UserData>data</UserData><Feature/></Relation></RelationSet>";
ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes("UTF-8"));
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader xsr = factory.createXMLStreamReader(input);
xsr.nextTag();
Document doc = Factory.newDocument("sample text");
AnnotationSet set = doc.getAnnotations();
DocumentStaxUtils.readRelationSet(xsr, set.getRelations(), new HashSet<Integer>());
}

@Test(expected = XMLStreamException.class)
public void testReadRelationSetMissingId() throws Exception {
String xml = "<RelationSet><Relation Type='coref' Members='1;2'><UserData>U</UserData><Feature/></Relation></RelationSet>";
ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes("UTF-8"));
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader xsr = factory.createXMLStreamReader(input);
xsr.nextTag();
Document doc = Factory.newDocument("text");
AnnotationSet set = doc.getAnnotations();
DocumentStaxUtils.readRelationSet(xsr, set.getRelations(), new HashSet<Integer>());
}

@Test(expected = XMLStreamException.class)
public void testReadRelationSetInvalidMember() throws Exception {
String xml = "<RelationSet><Relation Id='1' Type='dep' Members='x'><UserData></UserData><Feature/></Relation></RelationSet>";
ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes("UTF-8"));
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader xsr = factory.createXMLStreamReader(input);
xsr.nextTag();
Document doc = Factory.newDocument("abc");
AnnotationSet set = doc.getAnnotations();
DocumentStaxUtils.readRelationSet(xsr, set.getRelations(), new HashSet<Integer>());
}

@Test
public void testWriteFeaturesWithFeatureContainingCDATASectionMarker() throws Exception {
Document doc = Factory.newDocument("sample");
String cdata = "this ]]> breaks CDATA";
FeatureMap features = doc.getFeatures();
features.put("valueWithEnd", cdata);
ByteArrayOutputStream output = new ByteArrayOutputStream();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(output, "UTF-8");
writer.writeStartDocument("UTF-8", "1.0");
writer.writeStartElement("GateDocumentFeatures");
DocumentStaxUtils.writeFeatures(features, writer, "");
writer.writeEndElement();
writer.writeEndDocument();
writer.flush();
writer.close();
String result = output.toString("UTF-8");
assertTrue(result.contains("]]>"));
}

@Test
public void testWriteDocumentHandlesCDATAFallbackToCharacters() throws Exception {
Document doc = Factory.newDocument("foo<bar>baz");
ByteArrayOutputStream out = new ByteArrayOutputStream();
DocumentStaxUtils.writeDocument(doc, out, "");
String result = out.toString("UTF-8");
assertTrue(result.contains("<GateDocument"));
assertTrue(result.contains("TextWithNodes"));
assertTrue(result.contains("Node"));
assertTrue(result.contains("foo"));
}

@Test
public void testReadFeatureMapWithCollectionClassWithUnconstructableItem() throws Exception {
String xml = "<Annotation>" + "<Feature>" + "<Name className='java.lang.String'>myList</Name>" + "<Value className='java.util.ArrayList' itemClassName='java.io.File'>/tmp;/var</Value>" + "</Feature>" + "</Annotation>";
ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes("UTF-8"));
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(input);
while (reader.hasNext()) {
if (reader.isStartElement() && "Annotation".equals(reader.getLocalName()))
break;
reader.next();
}
reader.nextTag();
FeatureMap result = DocumentStaxUtils.readFeatureMap(reader);
assertTrue(result.containsKey("myList"));
assertTrue(result.get("myList") instanceof Collection);
}

@Test(expected = XMLStreamException.class)
public void testDuplicateAnnotationIdThrowsException() throws Exception {
String xml = "<AnnotationSet>" + "<Annotation StartNode='0' EndNode='4' Type='WORD' Id='100'>" + "<Feature><Name className='java.lang.String'>key</Name><Value className='java.lang.String'>val</Value></Feature></Annotation>" + "<Annotation StartNode='5' EndNode='9' Type='WORD' Id='100'>" + "<Feature><Name className='java.lang.String'>key</Name><Value className='java.lang.String'>val</Value></Feature></Annotation>" + "</AnnotationSet>";
ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes("UTF-8"));
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(input);
reader.nextTag();
Document doc = Factory.newDocument("textForTesting");
Map<Integer, Long> nodeMap = new HashMap<Integer, Long>();
nodeMap.put(0, 0L);
nodeMap.put(4, 4L);
nodeMap.put(5, 5L);
nodeMap.put(9, 9L);
Set<Integer> existingIds = new HashSet<Integer>();
AnnotationSet set = doc.getAnnotations();
DocumentStaxUtils.readAnnotationSet(reader, set, nodeMap, existingIds, null);
}

@Test
public void testReadAnnotationSetWithoutIdMode() throws Exception {
String xml = "<AnnotationSet>" + "<Annotation StartNode='0' EndNode='5' Type='TEXT'>" + "<Feature><Name>n</Name><Value>v</Value></Feature>" + "</Annotation></AnnotationSet>";
ByteArrayInputStream in = new ByteArrayInputStream(xml.getBytes());
XMLInputFactory xmlInFactory = XMLInputFactory.newInstance();
XMLStreamReader reader = xmlInFactory.createXMLStreamReader(in);
reader.nextTag();
Document doc = Factory.newDocument("abcde");
Map<Integer, Long> map = new HashMap<Integer, Long>();
map.put(0, 0L);
map.put(5, 5L);
Set<Integer> allIds = new HashSet<Integer>();
AnnotationSet ann = doc.getAnnotations();
Boolean requireId = DocumentStaxUtils.readAnnotationSet(reader, ann, map, allIds, null);
assertFalse(requireId);
assertEquals(1, ann.size());
}

@Test
public void testReadAnnotationSetWithAllPresentIdMode() throws Exception {
String xml = "<AnnotationSet>" + "<Annotation StartNode='0' EndNode='5' Type='TEXT' Id='250'>" + "<Feature><Name>n</Name><Value>v</Value></Feature>" + "</Annotation></AnnotationSet>";
ByteArrayInputStream in = new ByteArrayInputStream(xml.getBytes());
XMLInputFactory xmlInFactory = XMLInputFactory.newInstance();
XMLStreamReader reader = xmlInFactory.createXMLStreamReader(in);
reader.nextTag();
Document doc = Factory.newDocument("12345");
Map<Integer, Long> map = new HashMap<Integer, Long>();
map.put(0, 0L);
map.put(5, 5L);
Set<Integer> ids = new HashSet<Integer>();
AnnotationSet ann = doc.getAnnotations();
Boolean requireId = DocumentStaxUtils.readAnnotationSet(reader, ann, map, ids, null);
assertTrue(requireId);
assertEquals(Integer.valueOf(250), ann.iterator().next().getId());
}

@Test(expected = XMLStreamException.class)
public void testReadAnnotationSetInconsistentIdMixThrowsException() throws Exception {
String xml = "<AnnotationSet>" + "<Annotation StartNode='0' EndNode='5' Type='TEXT' Id='1'>" + "<Feature><Name>n</Name><Value>v</Value></Feature>" + "</Annotation>" + "<Annotation StartNode='6' EndNode='9' Type='TEXT'>" + "<Feature><Name>n</Name><Value>v</Value></Feature>" + "</Annotation></AnnotationSet>";
ByteArrayInputStream in = new ByteArrayInputStream(xml.getBytes("UTF-8"));
XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(in);
reader.nextTag();
Document doc = Factory.newDocument("textForExceptionTest");
Map<Integer, Long> nodeMap = new HashMap<Integer, Long>();
nodeMap.put(0, 0L);
nodeMap.put(5, 5L);
nodeMap.put(6, 6L);
nodeMap.put(9, 9L);
AnnotationSet set = doc.getAnnotations();
Set<Integer> ids = new HashSet<Integer>();
DocumentStaxUtils.readAnnotationSet(reader, set, nodeMap, ids, null);
}

@Test
public void testWriteXcesAnnotationsWithoutIds() throws Exception {
Document doc = Factory.newDocument("testing123");
AnnotationSet anset = doc.getAnnotations();
anset.add(0L, 4L, "Word", Factory.newFeatureMap());
ByteArrayOutputStream out = new ByteArrayOutputStream();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(out, "UTF-8");
DocumentStaxUtils.writeXcesAnnotations(anset, writer, false);
writer.flush();
String xml = out.toString("UTF-8");
assertTrue(xml.contains("<cesAna"));
assertTrue(xml.contains("struct"));
assertFalse(xml.contains("n="));
}

@Test
public void testReplaceXMLIllegalCharactersInStringWithAllLegalChars() {
String s = "AllFineText123";
String result = DocumentStaxUtils.replaceXMLIllegalCharactersInString(s);
assertSame(s, result);
}

@Test
public void testWriteDocumentHandlesEmptyNamedAnnotationSets() throws Exception {
String text = "This is a test.";
Document doc = Factory.newDocument(text);
Map<String, Collection<Annotation>> allSets = new HashMap<String, Collection<Annotation>>();
allSets.put(null, doc.getAnnotations());
allSets.put("emptySet", new ArrayList<Annotation>());
ByteArrayOutputStream out = new ByteArrayOutputStream();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(out, "UTF-8");
writer.writeStartDocument();
DocumentStaxUtils.writeDocument(doc, allSets, writer, "");
writer.writeEndDocument();
writer.flush();
String result = out.toString("UTF-8");
assertTrue(result.contains("AnnotationSet"));
assertTrue(result.contains("Name=\"emptySet\""));
}

@Test
public void testWriteTextWithNodesHandlesOverlappingOffsets() throws Exception {
Document doc = Factory.newDocument("supercalifragilistic");
FeatureMap fm = Factory.newFeatureMap();
AnnotationSet set = doc.getAnnotations();
set.add(0L, 5L, "Part", fm);
set.add(0L, 5L, "Again", fm);
Collection<Collection<Annotation>> sets = new ArrayList<Collection<Annotation>>();
sets.add(set);
ByteArrayOutputStream out = new ByteArrayOutputStream();
XMLStreamWriter xsw = XMLOutputFactory.newInstance().createXMLStreamWriter(out, "UTF-8");
xsw.writeStartDocument();
DocumentStaxUtils.writeTextWithNodes(doc, sets, xsw, "");
xsw.writeEndDocument();
xsw.flush();
String result = out.toString("UTF-8");
assertTrue(result.contains("Node"));
assertTrue(result.contains("TextWithNodes"));
}

@Test
public void testReplaceXMLIllegalCharactersHandlesValidSurrogatePair() {
String original = "Emoji: \uD83D\uDE03";
CharSequence seq = new StringBuilder(original);
boolean hasInvalid = DocumentStaxUtils.isInvalidXmlChar(seq, 0);
assertFalse(hasInvalid);
}

@Test
public void testWriteAnnotationSetWithNullFeatures() throws Exception {
Document doc = Factory.newDocument("Some content here");
AnnotationSet annotations = doc.getAnnotations();
annotations.add(0L, 4L, "Token", null);
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(outputStream, "UTF-8");
writer.writeStartDocument("UTF-8", "1.0");
DocumentStaxUtils.writeAnnotationSet(annotations, "TestSet", writer, "");
writer.writeEndDocument();
writer.flush();
writer.close();
String xml = outputStream.toString("UTF-8");
assertTrue(xml.contains("AnnotationSet"));
}

@Test
public void testWriteAnnotationSetWithEmptyCollection() throws Exception {
Document doc = Factory.newDocument("Test");
Collection<Annotation> emptyAnnotations = new ArrayList<Annotation>();
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(outputStream, "UTF-8");
writer.writeStartDocument("UTF-8", "1.0");
DocumentStaxUtils.writeAnnotationSet(emptyAnnotations, "EmptySet", writer, "");
writer.writeEndDocument();
writer.flush();
writer.close();
String xml = outputStream.toString("UTF-8");
assertTrue(xml.contains("AnnotationSet"));
}

@Test
public void testWriteFeaturesSkipsNullFeatureMap() throws Exception {
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(outputStream, "UTF-8");
writer.writeStartDocument("UTF-8", "1.0");
writer.writeStartElement("GateDocumentFeatures");
DocumentStaxUtils.writeFeatures(null, writer, "");
writer.writeEndElement();
writer.writeEndDocument();
writer.flush();
writer.close();
String result = outputStream.toString("UTF-8").trim();
assertTrue(result.endsWith("</GateDocumentFeatures>"));
}

@Test
public void testFeatureMapWithUnserializableCustomObjectFallsBackToString() throws Exception {
Document doc = Factory.newDocument("Example");
FeatureMap fm = doc.getFeatures();
Object obj = new Object();
fm.put("broken", obj);
ByteArrayOutputStream baos = new ByteArrayOutputStream();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(baos, "UTF-8");
writer.writeStartDocument("UTF-8", "1.0");
writer.writeStartElement("GateDocumentFeatures");
DocumentStaxUtils.writeFeatures(fm, writer, "");
writer.writeEndElement();
writer.writeEndDocument();
writer.flush();
writer.close();
String xml = baos.toString("UTF-8");
assertTrue(xml.contains("broken"));
assertTrue(xml.contains("Feature"));
}

@Test
public void testWriteTextWithNodesWhenNoText() throws Exception {
Document doc = Factory.newDocument("");
ByteArrayOutputStream out = new ByteArrayOutputStream();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(out, "UTF-8");
writer.writeStartDocument("UTF-8", "1.0");
DocumentStaxUtils.writeTextWithNodes(doc, writer, "");
writer.writeEndDocument();
writer.flush();
writer.close();
String result = out.toString("UTF-8");
assertTrue(result.contains("<TextWithNodes"));
}

@Test
public void testWriteTextWithNodesHandlesNoAnnotations() throws Exception {
Document doc = Factory.newDocument("This is content with no annotations.");
Collection<Collection<Annotation>> sets = new ArrayList<Collection<Annotation>>();
ByteArrayOutputStream out = new ByteArrayOutputStream();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(out, "UTF-8");
writer.writeStartDocument();
DocumentStaxUtils.writeTextWithNodes(doc, sets, writer, "");
writer.writeEndDocument();
writer.flush();
writer.close();
String xml = out.toString("UTF-8");
assertTrue(xml.contains("TextWithNodes"));
assertFalse(xml.contains("Node"));
}

@Test
public void testReplaceXMLIllegalCharactersReplacesAllControlChars() {
char[] chars = new char[] { 0x00, 0x01, 0x02, 0x03, 'a', 0x09, 0x0A, 0x0D, 'b', 0x1F };
DocumentStaxUtils.replaceXMLIllegalCharacters(chars, 0, chars.length);
assertEquals(' ', chars[0]);
assertEquals(' ', chars[1]);
assertEquals(' ', chars[2]);
assertEquals(' ', chars[3]);
assertEquals('a', chars[4]);
assertEquals(0x09, chars[5]);
assertEquals(0x0A, chars[6]);
assertEquals(0x0D, chars[7]);
assertEquals('b', chars[8]);
assertEquals(' ', chars[9]);
}

@Test
public void testWriteFeaturesWithStringValueWithoutClassName() throws Exception {
Document doc = Factory.newDocument("test");
FeatureMap fm = doc.getFeatures();
fm.put("simple", "value");
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
XMLStreamWriter xsw = XMLOutputFactory.newInstance().createXMLStreamWriter(outputStream, "UTF-8");
xsw.writeStartDocument("UTF-8", "1.0");
xsw.writeStartElement("GateDocumentFeatures");
DocumentStaxUtils.writeFeatures(fm, xsw, "");
xsw.writeEndElement();
xsw.writeEndDocument();
xsw.flush();
xsw.close();
String xml = outputStream.toString("UTF-8");
assertTrue(xml.contains("simple"));
assertTrue(xml.contains("value"));
}

@Test
public void testWriteRelationSetWithNullOrEmptySetSkipsOutput() throws Exception {
ByteArrayOutputStream out = new ByteArrayOutputStream();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(out, "UTF-8");
writer.writeStartDocument();
DocumentStaxUtils.writeRelationSet(null, writer, "");
DocumentStaxUtils.writeRelationSet(Factory.newDocument("x").getAnnotations().getRelations(), writer, "");
writer.writeEndDocument();
writer.flush();
writer.close();
String xml = out.toString("UTF-8");
assertFalse(xml.contains("RelationSet"));
assertFalse(xml.contains("Relation"));
}

@Test
public void testWriteCharactersOrCDATAWithLowLessThanCountUsesWriteCharacters() throws Exception {
String text = "Just a regular sentence.";
ByteArrayOutputStream out = new ByteArrayOutputStream();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(out, "UTF-8");
writer.writeStartDocument();
writer.writeStartElement("test");
DocumentStaxUtils.writeCharactersOrCDATA(writer, text);
writer.writeEndElement();
writer.writeEndDocument();
writer.flush();
writer.close();
String xml = new String(out.toByteArray(), "UTF-8");
assertTrue(xml.contains("Just a regular sentence."));
}

@Test
public void testWriteCharactersOrCDATAWithHighLTCountUsesCDATA() throws Exception {
String text = "<<<<<<<this might be XML";
ByteArrayOutputStream out = new ByteArrayOutputStream();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(out, "UTF-8");
writer.writeStartDocument();
writer.writeStartElement("test");
DocumentStaxUtils.writeCharactersOrCDATA(writer, text);
writer.writeEndElement();
writer.writeEndDocument();
writer.flush();
writer.close();
String xml = new String(out.toByteArray(), "UTF-8");
assertTrue(xml.contains("CDATA"));
}

@Test
public void testReadFeatureNameOrValueWithUnknownClassNameReturnsRawString() throws Exception {
String xml = "<Value className='com.fake.Unknown'>SomeValue</Value>";
ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(stream);
reader.nextTag();
Object result = DocumentStaxUtils.readFeatureNameOrValue(reader);
assertEquals("SomeValue", result);
}

@Test
public void testReadFeatureNameOrValueWithMissingClassAndItemClassAttrs() throws Exception {
String xml = "<Value>FallbackValue</Value>";
ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(stream);
reader.nextTag();
Object val = DocumentStaxUtils.readFeatureNameOrValue(reader);
assertEquals("FallbackValue", val);
}

@Test
public void testReadFeatureNameOrValueWithCollectionOfStringFallback() throws Exception {
String xml = "<Value className='java.util.ArrayList' itemClassName='invalid.Class'>one;two</Value>";
ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(stream);
reader.nextTag();
Object result = DocumentStaxUtils.readFeatureNameOrValue(reader);
assertTrue(result instanceof Collection);
Collection c = (Collection) result;
assertTrue(c.contains("one"));
assertTrue(c.contains("two"));
}

@Test
public void testReadFeatureNameOrValueWithValidCollectionItemClassConstructed() throws Exception {
String xml = "<Value className='java.util.ArrayList' itemClassName='java.lang.Integer'>123;456</Value>";
ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes());
XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(stream);
reader.nextTag();
Object val = DocumentStaxUtils.readFeatureNameOrValue(reader);
assertTrue(val instanceof Collection);
Collection list = (Collection) val;
assertEquals(2, list.size());
assertTrue(list.contains(Integer.valueOf(123)));
}

@Test
public void testReadFeatureNameOrValueReturnsWrappedObjectUnwrapped() throws Exception {
Object testValue = new Date();
gate.corpora.ObjectWrapper wrapped = new gate.corpora.ObjectWrapper(testValue);
String repr = wrapped.toString();
String xml = "<Value className='gate.util.ObjectWrapper'>" + repr + "</Value>";
ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes());
XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(stream);
reader.nextTag();
Object result = DocumentStaxUtils.readFeatureNameOrValue(reader);
assertTrue(result instanceof Date);
}

@Test(expected = XMLStreamException.class)
public void testNextTagSkipDTDThrowsForUnexpectedEventType() throws Exception {
String xml = "<?xml version='1.0'?><!-- comment --><!DOCTYPE note><note>text</note>";
ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes());
XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(input);
// DocumentStaxUtils.nextTagSkipDTD(reader);
}

@Test
public void testReplaceXMLIllegalCharactersHandlesSplitSurrogates() {
char[] content = new char[] { 'a', '\uD800', 'b', '\uDFFF', 'c' };
DocumentStaxUtils.replaceXMLIllegalCharacters(content, 0, content.length);
assertEquals(' ', content[1]);
assertEquals('b', content[2]);
assertEquals(' ', content[3]);
}

@Test
public void testReplaceXMLIllegalCharactersShortcutsWhenNoIllegalChars() {
char[] content = new char[] { 'x', 'y', 'z' };
char[] original = Arrays.copyOf(content, 3);
DocumentStaxUtils.replaceXMLIllegalCharacters(content, 0, 3);
assertArrayEquals(original, content);
}

@Test
public void testWriteDocumentWithNoAnnotationsAndNoNamedSets() throws Exception {
Document doc = Factory.newDocument("Only text");
doc.getAnnotations().clear();
Map<String, Collection<Annotation>> sets = new HashMap<String, Collection<Annotation>>();
ByteArrayOutputStream out = new ByteArrayOutputStream();
XMLStreamWriter xsw = XMLOutputFactory.newInstance().createXMLStreamWriter(out, "UTF-8");
xsw.writeStartDocument();
DocumentStaxUtils.writeDocument(doc, sets, xsw, "");
xsw.writeEndDocument();
xsw.flush();
xsw.close();
String xml = out.toString("UTF-8");
assertTrue(xml.contains("GateDocument"));
assertTrue(xml.contains("TextWithNodes"));
}

@Test
public void testWriteAnnotationWithNullType() throws Exception {
Document doc = Factory.newDocument("hello");
FeatureMap features = Factory.newFeatureMap();
AnnotationSet set = doc.getAnnotations();
set.add(0L, 2L, null, features);
ByteArrayOutputStream out = new ByteArrayOutputStream();
XMLStreamWriter xsw = XMLOutputFactory.newInstance().createXMLStreamWriter(out, "UTF-8");
xsw.writeStartDocument();
DocumentStaxUtils.writeAnnotationSet(set, null, xsw, "");
xsw.writeEndDocument();
xsw.flush();
xsw.close();
String xml = out.toString("UTF-8");
assertTrue(xml.contains("Annotation"));
}

@Test
public void testWriteTextWithNodesSplitAroundIllegalXMLCharacters() throws Exception {
Document doc = Factory.newDocument("abc\u0001def");
FeatureMap fm = Factory.newFeatureMap();
AnnotationSet set = doc.getAnnotations();
set.add(0L, 7L, "Token", fm);
Collection<Collection<Annotation>> allSets = new ArrayList<Collection<Annotation>>();
allSets.add(set);
ByteArrayOutputStream out = new ByteArrayOutputStream();
XMLStreamWriter xsw = XMLOutputFactory.newInstance().createXMLStreamWriter(out, "UTF-8");
xsw.writeStartDocument();
DocumentStaxUtils.writeTextWithNodes(doc, allSets, xsw, "");
xsw.writeEndDocument();
xsw.flush();
xsw.close();
String xml = out.toString("UTF-8");
assertTrue(xml.contains("TextWithNodes"));
assertTrue(xml.contains("Node"));
assertTrue(xml.contains("abc def"));
}

@Test
public void testReplaceXMLIllegalCharactersInStringReturnsModifiedString() {
String input = "Valid\u0003Text\uFFFF";
String sanitized = DocumentStaxUtils.replaceXMLIllegalCharactersInString(input);
assertNotSame(input, sanitized);
assertEquals("Valid Text ", sanitized);
}

@Test
public void testReadXcesFeatureMapWithNullOptionalAttributes() throws Exception {
String xml = "<struct xmlns='http://www.xces.org/schema/2003'>" + "<feat/><feat name='x' value='1'/><feat name='y'/><feat value='2'/></struct>";
ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes("UTF-8"));
XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(input);
reader.nextTag();
FeatureMap fm = DocumentStaxUtils.readXcesFeatureMap(reader);
assertEquals("1", fm.get("x"));
assertTrue(fm.containsKey("y") || fm.containsValue("2"));
}

@Test
public void testReadXcesHandlesEmptyCesAna() throws Exception {
String xml = "<cesAna xmlns='http://www.xces.org/schema/2003' version='1.0'></cesAna>";
ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes());
XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(input);
reader.nextTag();
Document doc = Factory.newDocument("abc");
AnnotationSet set = doc.getAnnotations();
DocumentStaxUtils.readXces(reader, set);
assertEquals(0, set.size());
}

@Test
public void testWriteXcesAnnotationsWithEmptyAnnotationSet() throws Exception {
Collection<Annotation> annotations = new ArrayList<Annotation>();
ByteArrayOutputStream out = new ByteArrayOutputStream();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(out, "UTF-8");
writer.writeStartDocument("UTF-8", "1.0");
DocumentStaxUtils.writeXcesAnnotations(annotations, writer);
writer.writeEndDocument();
writer.flush();
writer.close();
String result = out.toString("UTF-8");
assertTrue(result.contains("cesAna"));
assertFalse(result.contains("struct"));
}

@Test
public void testWriteXcesAnnotationsSkipsIsEmptyAndSpanFeature() throws Exception {
Document doc = Factory.newDocument("data");
FeatureMap fm = Factory.newFeatureMap();
fm.put("isEmptyAndSpan", Boolean.TRUE);
fm.put("included", "yes");
AnnotationSet set = doc.getAnnotations();
set.add(0L, 4L, "Num", fm);
ByteArrayOutputStream out = new ByteArrayOutputStream();
DocumentStaxUtils.writeXcesAnnotations(set, out, "UTF-8");
String xml = new String(out.toByteArray(), "UTF-8");
assertTrue(xml.contains("included"));
assertFalse(xml.contains("isEmptyAndSpan"));
}

@Test
public void testReadXcesHandlesValidAnnotationWithIdAndFeatures() throws Exception {
String xml = "<?xml version='1.0'?><cesAna xmlns='http://www.xces.org/schema/2003' version='1.0'>" + "<struct type='Token' from='0' to='4' n='51'><feat name='lemma' value='blue'/></struct></cesAna>";
ByteArrayInputStream in = new ByteArrayInputStream(xml.getBytes("UTF-8"));
XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(in);
reader.nextTag();
Document doc = Factory.newDocument("blue");
AnnotationSet set = doc.getAnnotations();
DocumentStaxUtils.readXces(reader, set);
Annotation a = set.get(51);
assertNotNull(a);
assertEquals("blue", a.getFeatures().get("lemma"));
}
}
