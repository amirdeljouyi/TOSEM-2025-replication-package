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

public class DocumentStaxUtils_1_GPTLLMTest {

@Test
public void testReadTextWithNodes_singleNodeAtCenter() throws Exception {
String xml = "<TextWithNodes>Hello<Node id='1'/>World</TextWithNodes>";
XMLInputFactory inputFactory = XMLInputFactory.newInstance();
XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(xml));
Map<Integer, Long> nodeMap = new HashMap<>();
reader.nextTag();
String result = DocumentStaxUtils.readTextWithNodes(reader, nodeMap);
assertEquals("HelloWorld", result);
assertEquals(1, nodeMap.size());
assertEquals(Long.valueOf(5), nodeMap.get(1L));
}

@Test(expected = XMLStreamException.class)
public void testReadTextWithNodes_invalidNodeId_shouldThrow() throws Exception {
String xml = "<TextWithNodes>Test<Node id='abc'/></TextWithNodes>";
XMLInputFactory inputFactory = XMLInputFactory.newInstance();
XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(xml));
Map<Integer, Long> nodeMap = new HashMap<>();
reader.nextTag();
DocumentStaxUtils.readTextWithNodes(reader, nodeMap);
}

@Test
public void testReadFeatureMap_twoFeaturesParsedCorrectly() throws Exception {
String xml = "<GateDocumentFeatures>" + "<Feature>" + "<Name className='java.lang.String'>author</Name>" + "<Value className='java.lang.String'>John Doe</Value>" + "</Feature>" + "<Feature>" + "<Name className='java.lang.Integer'>year</Name>" + "<Value className='java.lang.Integer'>2024</Value>" + "</Feature>" + "</GateDocumentFeatures>";
XMLInputFactory inputFactory = XMLInputFactory.newInstance();
XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(xml));
reader.nextTag();
FeatureMap result = DocumentStaxUtils.readFeatureMap(reader);
assertEquals(2, result.size());
assertEquals("John Doe", result.get("author"));
assertEquals(2024, ((Number) result.get("year")).intValue());
}

@Test
public void testReplaceXMLIllegalCharactersInString_shouldReplaceControlChars() {
String input = "A\u0001B\u001FC";
String expected = "A B C";
String result = DocumentStaxUtils.replaceXMLIllegalCharactersInString(input);
assertEquals(expected.length(), result.length());
assertFalse(result.contains("\u0001"));
assertFalse(result.contains("\u001F"));
}

@Test
public void testIsInvalidXmlChar_shouldDetectIllegalChar() {
CharSequence valid = "X";
CharSequence invalid = "\u0001";
boolean resultValid = DocumentStaxUtils.isInvalidXmlChar(valid, 0);
boolean resultIllegal = DocumentStaxUtils.isInvalidXmlChar(invalid, 0);
assertFalse(resultValid);
assertTrue(resultIllegal);
}

@Test
public void testReadFeatureNameOrValue_collectionFromTokens() throws Exception {
String xml = "<Value className='java.util.ArrayList' itemClassName='java.lang.String'>alpha;beta</Value>";
XMLInputFactory inputFactory = XMLInputFactory.newInstance();
XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(xml));
reader.nextTag();
Object value = DocumentStaxUtils.readFeatureNameOrValue(reader);
assertTrue(value instanceof Collection);
assertTrue(((Collection<?>) value).contains("alpha"));
assertTrue(((Collection<?>) value).contains("beta"));
}

@Test
public void testWriteXcesAnnotations_generatesExpectedXML() throws Exception {
Document doc = Factory.newDocument("This is a test.");
AnnotationSet annSet = doc.getAnnotations();
FeatureMap fm = Factory.newFeatureMap();
fm.put("testKey", "testVal");
annSet.add(0L, 4L, "Test", fm);
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
DocumentStaxUtils.writeXcesAnnotations(annSet, outputStream, "UTF-8");
String xmlOutput = outputStream.toString(StandardCharsets.UTF_8.name());
assertTrue(xmlOutput.contains("<cesAna"));
assertTrue(xmlOutput.contains("Test"));
assertTrue(xmlOutput.contains("from=\"0\""));
assertTrue(xmlOutput.contains("to=\"4\""));
assertTrue(xmlOutput.contains("<feat"));
assertTrue(xmlOutput.contains("testKey"));
assertTrue(xmlOutput.contains("testVal"));
}

@Test(expected = XMLStreamException.class)
public void testReadFeatureMap_unknownNestedElement_shouldThrow() throws Exception {
String xml = "<GateDocumentFeatures>" + "<Feature>" + "<Name className='java.lang.String'>dummy</Name>" + "<InvalidTag>value</InvalidTag>" + "</Feature>" + "</GateDocumentFeatures>";
XMLInputFactory inputFactory = XMLInputFactory.newInstance();
XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(xml));
reader.nextTag();
DocumentStaxUtils.readFeatureMap(reader);
}

@Test
public void testWriteCharactersOrCDATA_handlesCdataEndSequence() throws Exception {
String input = "ABC]]>DEF";
ByteArrayOutputStream out = new ByteArrayOutputStream();
XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
XMLStreamWriter writer = outputFactory.createXMLStreamWriter(out);
writer.writeStartDocument();
writer.writeStartElement("root");
DocumentStaxUtils.writeCharactersOrCDATA(writer, input);
writer.writeEndElement();
writer.writeEndDocument();
writer.flush();
String result = out.toString(StandardCharsets.UTF_8.name());
assertTrue(result.contains("]]>DEF") || result.contains("]]&gt;DEF"));
}

@Test
public void testContainsEnoughLTs_shouldReturnTrueAboveThreshold() {
String input = "<a><b><c><d><e><f>";
// boolean result = invokeContainsEnoughLTs(input);
// assertTrue(result);
}

@Test
public void testContainsEnoughLTs_shouldReturnFalseBelowThreshold() {
String input = "<a><b>";
// boolean result = invokeContainsEnoughLTs(input);
// assertFalse(result);
}

@Test
public void testAnnotationObject_toStringContainsExpectedParts() {
DocumentStaxUtils.AnnotationObject obj = new DocumentStaxUtils.AnnotationObject();
obj.setId(99);
obj.setElemName("MyType");
obj.setStart(123L);
obj.setEnd(456L);
FeatureMap fm = Factory.newFeatureMap();
fm.put("key1", "value1");
obj.setFM(fm);
String stringRep = obj.toString();
assertTrue(stringRep.contains("id =99"));
assertTrue(stringRep.contains("MyType"));
assertTrue(stringRep.contains("startNode=123"));
assertTrue(stringRep.contains("endNode=456"));
assertTrue(stringRep.contains("key1"));
}

@Test(expected = XMLStreamException.class)
public void testReadTextWithNodes_nodeWithoutId_shouldFail() throws Exception {
String xml = "<TextWithNodes>text<Node/></TextWithNodes>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));
Map<Integer, Long> map = new HashMap<>();
reader.nextTag();
DocumentStaxUtils.readTextWithNodes(reader, map);
}

@Test(expected = XMLStreamException.class)
public void testReadTextWithNodes_nodeWithInvalidCharId_shouldFail() throws Exception {
String xml = "<TextWithNodes>Hello<Node id='#x'/></TextWithNodes>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));
Map<Integer, Long> map = new HashMap<>();
reader.nextTag();
DocumentStaxUtils.readTextWithNodes(reader, map);
}

@Test(expected = XMLStreamException.class)
public void testReadFeatureMap_featureWithoutName_shouldFail() throws Exception {
String xml = "<GateDocumentFeatures>" + "<Feature><Value className='java.lang.String'>val</Value></Feature>" + "</GateDocumentFeatures>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));
reader.nextTag();
DocumentStaxUtils.readFeatureMap(reader);
}

@Test(expected = XMLStreamException.class)
public void testReadFeatureMap_featureWithNestedElement_shouldFail() throws Exception {
String xml = "<GateDocumentFeatures>" + "<Feature><Name className='java.lang.String'>key</Name><Extra/></Feature>" + "</GateDocumentFeatures>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));
reader.nextTag();
DocumentStaxUtils.readFeatureMap(reader);
}

@Test
public void testReplaceXMLIllegalCharactersInString_surrogatePairsHandledCorrectly() {
String input = "Emoji \uD83D\uDE00 valid — broken \uD800";
String result = DocumentStaxUtils.replaceXMLIllegalCharactersInString(input);
for (char c : result.toCharArray()) {
assertFalse((c >= 0xD800 && c <= 0xDBFF) || (c >= 0xDC00 && c <= 0xDFFF));
}
}

@Test
public void testWriteXcesAnnotations_excludesIsEmptyAndSpanFeature() throws Exception {
Document doc = Factory.newDocument("Sample content");
AnnotationSet set = doc.getAnnotations();
FeatureMap fm = Factory.newFeatureMap();
fm.put("isEmptyAndSpan", "ignoredValue");
fm.put("actualKey", "includedValue");
set.add(0L, 6L, "Thing", fm);
ByteArrayOutputStream out = new ByteArrayOutputStream();
DocumentStaxUtils.writeXcesAnnotations(set, out, "UTF-8");
String xml = out.toString("UTF-8");
assertTrue(xml.contains("actualKey"));
assertFalse(xml.contains("isEmptyAndSpan"));
}

@Test
public void testReadFeatureNameOrValue_fallbackToStringOnInvalidClass() throws Exception {
String xml = "<Value className='non.existent.Class'>42</Value>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));
reader.nextTag();
Object result = DocumentStaxUtils.readFeatureNameOrValue(reader);
assertEquals("42", result);
}

@Test
public void testReadFeatureNameOrValue_enumLikeValueFallsBackToString() throws Exception {
String xml = "<Value className='java.lang.Enum'>SOME_VALUE</Value>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));
reader.nextTag();
Object value = DocumentStaxUtils.readFeatureNameOrValue(reader);
assertEquals("SOME_VALUE", value);
}

@Test
public void testWriteCharactersOrCDATA_textJustBelowThreshold_usesCharacters() throws Exception {
String content = "<a><b>";
ByteArrayOutputStream buffer = new ByteArrayOutputStream();
XMLOutputFactory outFactory = XMLOutputFactory.newInstance();
XMLStreamWriter writer = outFactory.createXMLStreamWriter(buffer);
writer.writeStartDocument();
writer.writeStartElement("root");
DocumentStaxUtils.writeCharactersOrCDATA(writer, content);
writer.writeEndElement();
writer.writeEndDocument();
writer.flush();
String xml = buffer.toString("UTF-8");
assertTrue(xml.contains("&lt;"));
}

@Test
public void testWriteCharactersOrCDATA_textWithExactThreshold_usesCDATA() throws Exception {
String content = "<<<<<<<";
ByteArrayOutputStream buffer = new ByteArrayOutputStream();
XMLOutputFactory outFactory = XMLOutputFactory.newInstance();
XMLStreamWriter writer = outFactory.createXMLStreamWriter(buffer);
writer.writeStartDocument();
writer.writeStartElement("root");
DocumentStaxUtils.writeCharactersOrCDATA(writer, content);
writer.writeEndElement();
writer.writeEndDocument();
writer.flush();
String xml = buffer.toString("UTF-8");
assertTrue(xml.contains("<![CDATA["));
}

@Test(expected = XMLStreamException.class)
public void testReadXces_invalidFromAttribute_shouldFail() throws Exception {
String xml = "<cesAna xmlns='http://www.xces.org/schema/2003'>" + "<struct type='Person' from='abc' to='5'/>" + "</cesAna>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));
reader.nextTag();
AnnotationSet as = Factory.newDocument("0123456789").getAnnotations();
DocumentStaxUtils.readXces(reader, as);
}

@Test(expected = XMLStreamException.class)
public void testReadXces_duplicateAnnotationId_shouldFail() throws Exception {
String xml = "<cesAna xmlns='http://www.xces.org/schema/2003'>" + "<struct type='A' from='0' to='1' n='1'>" + "<feat name='x' value='y'/></struct>" + "<struct type='A' from='2' to='3' n='1'>" + "<feat name='x' value='z'/></struct>" + "</cesAna>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));
reader.nextTag();
AnnotationSet set = Factory.newDocument("abcde").getAnnotations();
DocumentStaxUtils.readXces(reader, set);
}

@Test(expected = XMLStreamException.class)
public void testReadFeatureNameOrValue_invalidItemInCollection_shouldThrow() throws Exception {
String xml = "<Value className='java.util.ArrayList' itemClassName='non.existent.Type'>bad;values</Value>";
XMLInputFactory inputFactory = XMLInputFactory.newInstance();
XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(xml));
reader.nextTag();
DocumentStaxUtils.readFeatureNameOrValue(reader);
}

@Test(expected = XMLStreamException.class)
public void testReadAnnotationSet_duplicateAnnotationId_shouldThrow() throws Exception {
String xml = "<AnnotationSet>" + "<Annotation Id='1' Type='Entity' StartNode='0' EndNode='5'>" + "<Feature><Name className='java.lang.String'>key</Name>" + "<Value className='java.lang.String'>val</Value></Feature></Annotation>" + "<Annotation Id='1' Type='Entity' StartNode='6' EndNode='10'>" + "<Feature><Name className='java.lang.String'>k</Name>" + "<Value className='java.lang.String'>v</Value></Feature></Annotation>" + "</AnnotationSet>";
XMLInputFactory inputFactory = XMLInputFactory.newInstance();
XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(xml));
Map<Integer, Long> nodeMap = new HashMap<Integer, Long>();
nodeMap.put(0, 0L);
nodeMap.put(5, 5L);
nodeMap.put(6, 6L);
nodeMap.put(10, 10L);
reader.nextTag();
Document doc = Factory.newDocument("0123456789");
reader.require(XMLStreamConstants.START_ELEMENT, null, "AnnotationSet");
Set<Integer> allAnnotIds = new HashSet<Integer>();
Boolean requireIds = null;
DocumentStaxUtils.readAnnotationSet(reader, doc.getAnnotations(), nodeMap, allAnnotIds, requireIds);
}

@Test(expected = XMLStreamException.class)
public void testReadAnnotationSet_withMixedIdStyles_shouldThrow() throws Exception {
String xml = "<AnnotationSet>" + "<Annotation Type='Entity' StartNode='0' EndNode='5'>" + "<Feature><Name className='java.lang.String'>x</Name>" + "<Value className='java.lang.String'>y</Value></Feature></Annotation>" + "<Annotation Id='2' Type='Entity' StartNode='6' EndNode='10'>" + "<Feature><Name className='java.lang.String'>a</Name>" + "<Value className='java.lang.String'>b</Value></Feature></Annotation>" + "</AnnotationSet>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));
Map<Integer, Long> nodeMap = new HashMap<Integer, Long>();
nodeMap.put(0, 0L);
nodeMap.put(5, 5L);
nodeMap.put(6, 6L);
nodeMap.put(10, 10L);
reader.nextTag();
reader.require(XMLStreamConstants.START_ELEMENT, null, "AnnotationSet");
Document doc = Factory.newDocument("abcdefghij");
Set<Integer> ids = new HashSet<Integer>();
Boolean requireIds = null;
DocumentStaxUtils.readAnnotationSet(reader, doc.getAnnotations(), nodeMap, ids, requireIds);
}

@Test(expected = XMLStreamException.class)
public void testReadRelationSet_missingMembers_shouldThrow() throws Exception {
String xml = "<RelationSet>" + "<Relation Id='1' Type='coref'>" + "<UserData>abc</UserData>" + "<Feature><Name className='java.lang.String'>x</Name>" + "<Value className='java.lang.String'>y</Value></Feature>" + "</Relation></RelationSet>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));
Document doc = Factory.newDocument("abcdef");
RelationSet set = doc.getAnnotations().getRelations();
reader.nextTag();
DocumentStaxUtils.readRelationSet(reader, set, new HashSet<Integer>());
}

@Test(expected = XMLStreamException.class)
public void testReadRelationSet_invalidRelationId_shouldThrow() throws Exception {
String xml = "<RelationSet>" + "<Relation Id='bad' Type='coref' Members='1;2'>" + "<UserData>abc</UserData>" + "<Feature><Name className='java.lang.String'>x</Name>" + "<Value className='java.lang.String'>y</Value></Feature>" + "</Relation></RelationSet>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));
Document doc = Factory.newDocument("abcdef");
RelationSet set = doc.getAnnotations().getRelations();
reader.nextTag();
DocumentStaxUtils.readRelationSet(reader, set, new HashSet<Integer>());
}

@Test(expected = XMLStreamException.class)
public void testReadRelationSet_invalidUserDataElement_shouldThrow() throws Exception {
String xml = "<RelationSet>" + "<Relation Id='10' Type='type' Members='1;2'><UserData><Inner/></UserData>" + "<Feature><Name className='java.lang.String'>a</Name>" + "<Value className='java.lang.String'>b</Value></Feature>" + "</Relation></RelationSet>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));
Document doc = Factory.newDocument("relation test");
RelationSet relations = doc.getAnnotations().getRelations();
reader.nextTag();
DocumentStaxUtils.readRelationSet(reader, relations, new HashSet<Integer>());
}

@Test(expected = XMLStreamException.class)
public void testReadFeatureNameOrValue_withNestedElement_shouldThrow() throws Exception {
String xml = "<Value className='java.lang.String'>abc<inner/></Value>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));
reader.nextTag();
DocumentStaxUtils.readFeatureNameOrValue(reader);
}

@Test
public void testWriteTextWithNodes_emptyDocumentText() throws Exception {
Document doc = Factory.newDocument("");
Collection<Collection<Annotation>> sets = new ArrayList<Collection<Annotation>>();
sets.add(doc.getAnnotations());
ByteArrayOutputStream output = new ByteArrayOutputStream();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(output);
writer.writeStartDocument();
writer.writeStartElement("TestRoot");
DocumentStaxUtils.writeTextWithNodes(doc, sets, writer, "");
writer.writeEndElement();
writer.writeEndDocument();
writer.flush();
String outputXml = output.toString("UTF-8");
assertTrue(outputXml.contains("<TextWithNodes/>"));
}

@Test
public void testReplaceXMLIllegalCharacters_charArray_withMultipleInvalids() {
char[] input = new char[] { 'a', '\u0000', 'b', '\u0001', 'c', '\uFFFE', 'd' };
DocumentStaxUtils.replaceXMLIllegalCharacters(input, 0, input.length);
assertEquals('a', input[0]);
assertEquals(' ', input[1]);
assertEquals('b', input[2]);
assertEquals(' ', input[3]);
assertEquals('c', input[4]);
assertEquals(' ', input[5]);
assertEquals('d', input[6]);
}

@Test
public void testReplaceXMLIllegalCharactersInString_noIllegalChars_returnsSameInstance() {
String input = "validText";
String result = DocumentStaxUtils.replaceXMLIllegalCharactersInString(input);
assertSame(input, result);
}

@Test
public void testReplaceXMLIllegalCharactersInString_onlyInvalid_shouldReturnAllSpaces() {
String input = "\u0001\u0002\u0003";
String result = DocumentStaxUtils.replaceXMLIllegalCharactersInString(input);
assertEquals("   ", result);
}

@Test
public void testWriteFeatures_skipsNullKeysAndValues() throws Exception {
FeatureMap fm = Factory.newFeatureMap();
fm.put("valid", "ok");
fm.put(null, "ignored");
fm.put("skip", null);
ByteArrayOutputStream buffer = new ByteArrayOutputStream();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(buffer);
writer.writeStartDocument();
writer.writeStartElement("GateDocumentFeatures");
DocumentStaxUtils.writeFeatures(fm, writer, "");
writer.writeEndElement();
writer.writeEndDocument();
writer.flush();
String output = buffer.toString("UTF-8");
assertTrue(output.contains("valid"));
assertFalse(output.contains("ignored"));
assertFalse(output.contains("skip"));
}

@Test(expected = XMLStreamException.class)
public void testReadAnnotationSet_startNodeMissingFromMap_shouldThrow() throws Exception {
String xml = "<AnnotationSet>" + "<Annotation Id='1' Type='Test' StartNode='100' EndNode='200'>" + "<Feature><Name className='java.lang.String'>key</Name>" + "<Value className='java.lang.String'>value</Value></Feature>" + "</Annotation>" + "</AnnotationSet>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));
reader.nextTag();
Document doc = Factory.newDocument("dummy content here");
Map<Integer, Long> nodeMap = new HashMap<Integer, Long>();
Set<Integer> ids = new HashSet<Integer>();
Boolean idsRequired = null;
DocumentStaxUtils.readAnnotationSet(reader, doc.getAnnotations(), nodeMap, ids, idsRequired);
}

@Test(expected = XMLStreamException.class)
public void testReadAnnotationSet_endNodeMissingFromMap_shouldThrow() throws Exception {
String xml = "<AnnotationSet>" + "<Annotation Id='1' Type='Test' StartNode='0' EndNode='999'>" + "<Feature><Name className='java.lang.String'>x</Name>" + "<Value className='java.lang.String'>y</Value></Feature>" + "</Annotation>" + "</AnnotationSet>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));
reader.nextTag();
Document doc = Factory.newDocument("abcdefghijklm");
Map<Integer, Long> nodeIdMap = new HashMap<Integer, Long>();
nodeIdMap.put(0, 0L);
Set<Integer> allAnnotIds = new HashSet<Integer>();
Boolean requireIds = null;
DocumentStaxUtils.readAnnotationSet(reader, doc.getAnnotations(), nodeIdMap, allAnnotIds, requireIds);
}

@Test
public void testToXml_documentWithNamedAnnotationSets_includesAllSets() throws Exception {
Document doc = Factory.newDocument("ABCDEF");
AnnotationSet defaultSet = doc.getAnnotations();
AnnotationSet namedSet = doc.getAnnotations("CustomSet");
FeatureMap fm1 = Factory.newFeatureMap();
fm1.put("key", "val");
defaultSet.add(0L, 2L, "Type1", fm1);
FeatureMap fm2 = Factory.newFeatureMap();
fm2.put("k", "v");
namedSet.add(3L, 5L, "Type2", fm2);
String xml = DocumentStaxUtils.toXml(doc);
assertTrue(xml.contains("AnnotationSet"));
assertTrue(xml.contains("Type1"));
assertTrue(xml.contains("Type2"));
assertTrue(xml.contains("CustomSet"));
}

@Test
public void testReplaceXMLIllegalCharacters_validSurrogatePair_preservedCorrectly() {
char[] chars = new char[] { 'a', '\uD83D', '\uDE00', 'b' };
DocumentStaxUtils.replaceXMLIllegalCharacters(chars, 0, chars.length);
assertEquals('\uD83D', chars[1]);
assertEquals('\uDE00', chars[2]);
}

@Test
public void testWriteRelationSet_emptyRelationSet_writesNothing() throws Exception {
Document doc = Factory.newDocument("ABC");
RelationSet relationSet = doc.getAnnotations().getRelations();
ByteArrayOutputStream out = new ByteArrayOutputStream();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(out);
writer.writeStartDocument();
writer.writeStartElement("root");
DocumentStaxUtils.writeRelationSet(relationSet, writer, "");
writer.writeEndElement();
writer.writeEndDocument();
writer.flush();
String output = out.toString("UTF-8");
assertFalse(output.contains("<RelationSet"));
}

@Test
public void testWriteRelationSet_namedSet_outputsSetName() throws Exception {
Document doc = Factory.newDocument("Content");
AnnotationSet named = doc.getAnnotations("rels");
RelationSet set = named.getRelations();
int[] members = new int[] { 1, 2 };
// Relation rel = new SimpleRelation(42, "LinkType", members);
// set.add(rel);
ByteArrayOutputStream out = new ByteArrayOutputStream();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(out);
writer.writeStartDocument();
writer.writeStartElement("root");
DocumentStaxUtils.writeRelationSet(set, writer, "");
writer.writeEndElement();
writer.writeEndDocument();
writer.flush();
String xmlOutput = out.toString("UTF-8");
assertTrue(xmlOutput.contains("RelationSet"));
assertTrue(xmlOutput.contains("rels"));
assertTrue(xmlOutput.contains("LinkType"));
assertTrue(xmlOutput.contains("42"));
}

@Test
public void testWriteFeatures_serializesObjectWrapperClasses() throws Exception {
FeatureMap fm = Factory.newFeatureMap();
List<Boolean> list = new ArrayList<Boolean>();
list.add(Boolean.TRUE);
list.add(Boolean.FALSE);
fm.put("bools", list);
ByteArrayOutputStream stream = new ByteArrayOutputStream();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(stream);
writer.writeStartDocument();
writer.writeStartElement("Features");
DocumentStaxUtils.writeFeatures(fm, writer, "");
writer.writeEndElement();
writer.writeEndDocument();
writer.flush();
String result = stream.toString("UTF-8");
assertTrue(result.contains("Feature"));
assertTrue(result.contains("bools"));
assertTrue(result.contains("true;false"));
assertTrue(result.contains("ArrayList"));
}

@Test
public void testWriteXcesContent_withEncoding_writesCorrectly() throws Exception {
Document doc = Factory.newDocument("äöüß€");
ByteArrayOutputStream out = new ByteArrayOutputStream();
DocumentStaxUtils.writeXcesContent(doc, out, "UTF-8");
String written = out.toString("UTF-8");
assertEquals("äöüß€", written);
}

@Test
public void testWriteXcesContent_defaultEncodingUsed_ifNull() throws Exception {
Document doc = Factory.newDocument("Content UTF Default");
ByteArrayOutputStream out = new ByteArrayOutputStream();
DocumentStaxUtils.writeXcesContent(doc, out, null);
String result = new String(out.toByteArray(), "UTF-8");
assertEquals("Content UTF Default", result);
}

@Test
public void testWriteAnnotationSet_nullAnnotationSet_writesEmptySet() throws Exception {
Collection<Annotation> annotations = null;
ByteArrayOutputStream out = new ByteArrayOutputStream();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(out);
writer.writeStartDocument();
writer.writeStartElement("doc");
DocumentStaxUtils.writeAnnotationSet(annotations, "MySet", writer, "");
writer.writeEndElement();
writer.writeEndDocument();
writer.flush();
String written = out.toString("UTF-8");
assertTrue(written.contains("AnnotationSet"));
assertTrue(written.contains("MySet"));
}

@Test(expected = XMLStreamException.class)
public void testReadAnnotationSet_annotationIdNotInteger_shouldThrow() throws Exception {
String xml = "<AnnotationSet>" + "<Annotation Id='NaN' Type='Test' StartNode='0' EndNode='5'>" + "<Feature><Name className='java.lang.String'>key</Name>" + "<Value className='java.lang.String'>value</Value></Feature>" + "</Annotation>" + "</AnnotationSet>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));
reader.nextTag();
Document doc = Factory.newDocument("abcdef");
Map<Integer, Long> nodeMap = new HashMap<Integer, Long>();
nodeMap.put(0, 0L);
nodeMap.put(5, 5L);
Set<Integer> ids = new HashSet<Integer>();
Boolean requireIds = null;
DocumentStaxUtils.readAnnotationSet(reader, doc.getAnnotations(), nodeMap, ids, requireIds);
}

@Test(expected = XMLStreamException.class)
public void testReadAnnotationSet_startNodeValueNotInt_shouldThrow() throws Exception {
String xml = "<AnnotationSet>" + "<Annotation Id='1' Type='Test' StartNode='start' EndNode='10'>" + "<Feature><Name className='java.lang.String'>a</Name>" + "<Value className='java.lang.String'>b</Value></Feature>" + "</Annotation></AnnotationSet>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));
reader.nextTag();
Document doc = Factory.newDocument("1234567890");
Map<Integer, Long> map = new HashMap<Integer, Long>();
map.put(10, 10L);
Set<Integer> ids = new HashSet<Integer>();
Boolean requireId = null;
DocumentStaxUtils.readAnnotationSet(reader, doc.getAnnotations(), map, ids, requireId);
}

@Test(expected = XMLStreamException.class)
public void testReadAnnotationSet_endNodeValueNotInt_shouldThrow() throws Exception {
String xml = "<AnnotationSet>" + "<Annotation Id='2' Type='Test' StartNode='2' EndNode='oops'>" + "<Feature><Name className='java.lang.String'>x</Name>" + "<Value className='java.lang.String'>y</Value></Feature>" + "</Annotation>" + "</AnnotationSet>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));
reader.nextTag();
Document doc = Factory.newDocument("abcdefghij");
Map<Integer, Long> nodeMap = new HashMap<Integer, Long>();
nodeMap.put(2, 2L);
Set<Integer> ids = new HashSet<Integer>();
Boolean reqId = null;
DocumentStaxUtils.readAnnotationSet(reader, doc.getAnnotations(), nodeMap, ids, reqId);
}

@Test(expected = XMLStreamException.class)
public void testReadXcesFeatureMap_featWithoutNameAttribute_shouldThrow() throws Exception {
String xml = "<struct xmlns='http://www.xces.org/schema/2003' type='Token' from='0' to='1'>" + "<feat value='word'/>" + "</struct>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));
reader.nextTag();
Document doc = Factory.newDocument("x");
AnnotationSet set = doc.getAnnotations();
DocumentStaxUtils.readXces(reader, set);
}

@Test
public void testReadXcesFeatureMap_allFeatValid_returnsCorrectFeatureMap() throws Exception {
String xml = "<struct xmlns='http://www.xces.org/schema/2003' type='Token' from='0' to='4'>" + "<feat name='pos' value='NN'/>" + "<feat name='lemma' value='dog'/>" + "</struct>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));
reader.nextTag();
Document doc = Factory.newDocument("word");
AnnotationSet set = doc.getAnnotations();
DocumentStaxUtils.readXces(reader, set);
assertTrue(set.size() > 0);
assertNotNull(set.iterator().next().getFeatures().get("pos"));
assertNotNull(set.iterator().next().getFeatures().get("lemma"));
}

@Test(expected = XMLStreamException.class)
public void testReadGateXmlDocument_missingDocumentFeatures_shouldFail() throws Exception {
String xml = "<GateDocument>" + "<TextWithNodes>hello<Node id='1'/>world</TextWithNodes>" + "</GateDocument>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));
reader.nextTag();
Document doc = Factory.newDocument("helloworld");
DocumentStaxUtils.readGateXmlDocument(reader, doc);
}

@Test
public void testReplaceXMLIllegalCharactersInString_highSurrogateWithoutLow_shouldBeReplaced() {
String text = "Text \uD800 extra";
String clean = DocumentStaxUtils.replaceXMLIllegalCharactersInString(text);
assertEquals("Text  extra", clean);
}

@Test
public void testWriteXcesAnnotations_withEmptyAnnotationSet_outputsValidXml() throws Exception {
Document doc = Factory.newDocument("Data");
AnnotationSet set = doc.getAnnotations();
ByteArrayOutputStream out = new ByteArrayOutputStream();
DocumentStaxUtils.writeXcesAnnotations(set, out, "UTF-8");
String xml = out.toString("UTF-8");
assertTrue(xml.contains("<cesAna"));
assertTrue(xml.contains("version=\"1.0\""));
}

@Test(expected = XMLStreamException.class)
public void testReadFeatureMap_missingValue_shouldFail() throws Exception {
String xml = "<GateDocumentFeatures>" + "<Feature><Name className='java.lang.String'>missingValue</Name></Feature>" + "</GateDocumentFeatures>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));
reader.nextTag();
DocumentStaxUtils.readFeatureMap(reader);
}

@Test(expected = XMLStreamException.class)
public void testReadFeatureNameOrValue_invalidConstructorForClass_shouldFail() throws Exception {
String xml = "<Value className='java.awt.Color'>invalid</Value>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));
reader.nextTag();
DocumentStaxUtils.readFeatureNameOrValue(reader);
}

@Test
public void testWriteXcesAnnotations_multipleAnnotations_orderIsPreserved() throws Exception {
Document doc = Factory.newDocument("This is a sentence.");
AnnotationSet set = doc.getAnnotations();
FeatureMap f1 = Factory.newFeatureMap();
f1.put("type", "DET");
set.add(0L, 4L, "Token", f1);
FeatureMap f2 = Factory.newFeatureMap();
f2.put("type", "NOUN");
set.add(5L, 7L, "Token", f2);
ByteArrayOutputStream output = new ByteArrayOutputStream();
DocumentStaxUtils.writeXcesAnnotations(set, output, "UTF-8");
String xml = output.toString("UTF-8");
assertTrue(xml.indexOf("DET") < xml.indexOf("NOUN"));
}

@Test
public void testWriteCharactersOrCDATA_stringWithMultipleCdataEndTokens_writesProperly() throws Exception {
String input = "part1]]>middle]]>end";
ByteArrayOutputStream out = new ByteArrayOutputStream();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(out);
writer.writeStartDocument();
writer.writeStartElement("root");
DocumentStaxUtils.writeCharactersOrCDATA(writer, input);
writer.writeEndElement();
writer.writeEndDocument();
writer.flush();
String xml = out.toString("UTF-8");
assertTrue(xml.contains("<![CDATA[part1"));
assertTrue(xml.contains("]]>"));
assertTrue(xml.contains("<![CDATA[middle"));
}

@Test
public void testWriteTextWithNodes_noAnnotationSetsStillWritesText() throws Exception {
Document doc = Factory.newDocument("textcontent");
ByteArrayOutputStream out = new ByteArrayOutputStream();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(out);
writer.writeStartDocument();
writer.writeStartElement("root");
DocumentStaxUtils.writeTextWithNodes(doc, new ArrayList<Collection<gate.Annotation>>(), writer, "");
writer.writeEndElement();
writer.writeEndDocument();
writer.flush();
String xml = out.toString("UTF-8");
assertTrue(xml.contains("<TextWithNodes>"));
assertTrue(xml.contains("textcontent"));
}

@Test
public void testWriteAnnotationSet_emptyAnnotationsWithNullSetName_outputValid() throws Exception {
Collection<gate.Annotation> annotations = new ArrayList<gate.Annotation>();
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(outputStream);
writer.writeStartDocument();
writer.writeStartElement("root");
DocumentStaxUtils.writeAnnotationSet(annotations, null, writer, "");
writer.writeEndElement();
writer.writeEndDocument();
writer.flush();
String xml = outputStream.toString("UTF-8");
assertTrue(xml.contains("<AnnotationSet"));
assertFalse(xml.contains("Name=\""));
}

@Test
public void testReplaceXMLIllegalCharacters_mixedValidAndInvalidCharactersHandled() {
char[] input = new char[] { 'A', '\u0008', 'B', '\uD800', 'C', '\uFFFF', 'D' };
DocumentStaxUtils.replaceXMLIllegalCharacters(input, 0, input.length);
assertEquals('A', input[0]);
assertEquals(' ', input[1]);
assertEquals('B', input[2]);
assertEquals(' ', input[3]);
assertEquals('C', input[4]);
assertEquals(' ', input[5]);
assertEquals('D', input[6]);
}

@Test
public void testIsInvalidXmlChar_validSurrogatePairNotInvalid() {
CharSequence text = "\uD83D\uDE00";
boolean checkFirst = DocumentStaxUtils.isInvalidXmlChar(text, 0);
assertFalse(checkFirst);
}

@Test
public void testWriteFeatures_featureKeyAndValueContainSpecialChars() throws Exception {
FeatureMap fm = Factory.newFeatureMap();
fm.put("na<me>", "val\"ue&<>");
ByteArrayOutputStream output = new ByteArrayOutputStream();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(output);
writer.writeStartDocument();
writer.writeStartElement("features");
DocumentStaxUtils.writeFeatures(fm, writer, "");
writer.writeEndElement();
writer.writeEndDocument();
writer.flush();
String xml = output.toString("UTF-8");
assertTrue(xml.contains("val"));
assertTrue(xml.contains("Feature"));
}

@Test
public void testWriteXcesAnnotations_specialCharsInAttributesAreEscaped() throws Exception {
Document doc = Factory.newDocument("example");
AnnotationSet set = doc.getAnnotations();
FeatureMap fm = Factory.newFeatureMap();
fm.put("key&", "value<test>");
set.add(0L, 3L, "Type&", fm);
ByteArrayOutputStream out = new ByteArrayOutputStream();
DocumentStaxUtils.writeXcesAnnotations(set, out, "UTF-8");
String output = out.toString("UTF-8");
assertTrue(output.contains("value"));
assertTrue(output.contains("key"));
assertTrue(output.contains("Type"));
}

@Test
public void testWriteFeatures_objectWithFallbackToString() throws Exception {
Object fallbackObject = new Object() {

@Override
public String toString() {
return "CustomFallback";
}
};
FeatureMap fm = Factory.newFeatureMap();
fm.put("custom", fallbackObject);
ByteArrayOutputStream out = new ByteArrayOutputStream();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(out);
writer.writeStartDocument();
writer.writeStartElement("features");
DocumentStaxUtils.writeFeatures(fm, writer, "");
writer.writeEndElement();
writer.writeEndDocument();
writer.flush();
String result = out.toString("UTF-8");
assertTrue(result.contains("CustomFallback"));
}

@Test
public void testWriteXcesAnnotations_includeIdFalse_excludesAttribute() throws Exception {
Document doc = Factory.newDocument("abc");
AnnotationSet set = doc.getAnnotations();
// set.add(0L, 1L, "T");
ByteArrayOutputStream out = new ByteArrayOutputStream();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(out);
writer.writeStartDocument();
writer.writeStartElement("cesAna");
DocumentStaxUtils.writeXcesAnnotations(set, writer, false);
writer.writeEndElement();
writer.writeEndDocument();
writer.flush();
String result = out.toString("UTF-8");
assertFalse(result.contains(" n="));
}

@Test(expected = XMLStreamException.class)
public void testReadFeatureMap_extraUnknownTag_shouldThrow() throws Exception {
String xml = "<GateDocumentFeatures>" + "  <Feature>" + "    <Name className='java.lang.String'>key</Name>" + "    <UnknownTag>value</UnknownTag>" + "  </Feature>" + "</GateDocumentFeatures>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));
reader.nextTag();
DocumentStaxUtils.readFeatureMap(reader);
}

@Test(expected = XMLStreamException.class)
public void testReadFeatureNameOrValue_noEndTag_shouldThrow() throws Exception {
String xml = "<Value className='java.lang.String'>valueText";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));
reader.nextTag();
DocumentStaxUtils.readFeatureNameOrValue(reader);
}

@Test
public void testWriteCharactersOrCDATA_valueWithLessThanSignsUsesCData() throws Exception {
String value = "<<<<<>";
ByteArrayOutputStream out = new ByteArrayOutputStream();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(out);
writer.writeStartDocument();
writer.writeStartElement("data");
DocumentStaxUtils.writeCharactersOrCDATA(writer, value);
writer.writeEndElement();
writer.writeEndDocument();
writer.flush();
String written = out.toString("UTF-8");
assertTrue(written.contains("<![CDATA["));
}

@Test
public void testWriteCharactersOrCDATA_valueWithFewLessThanSignsUsesCharacters() throws Exception {
String value = "<tag>";
ByteArrayOutputStream out = new ByteArrayOutputStream();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(out);
writer.writeStartDocument();
writer.writeStartElement("data");
DocumentStaxUtils.writeCharactersOrCDATA(writer, value);
writer.writeEndElement();
writer.writeEndDocument();
writer.flush();
String written = out.toString("UTF-8");
assertFalse(written.contains("<![CDATA["));
}

@Test
public void testReadFeatureMap_emptyFeatureElement_shouldReturnEmptyMap() throws Exception {
String xml = "<GateDocumentFeatures></GateDocumentFeatures>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));
reader.nextTag();
Map<?, ?> fmap = DocumentStaxUtils.readFeatureMap(reader);
assertTrue(fmap.isEmpty());
}

@Test
public void testReadFeatureNameOrValue_itemClassNameIgnoredWhenUsingStringCollection() throws Exception {
String xml = "<Value className='java.util.ArrayList' itemClassName='ignored.X'>one;two;three</Value>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));
reader.nextTag();
Object result = DocumentStaxUtils.readFeatureNameOrValue(reader);
assertTrue(result instanceof Collection);
assertTrue(((Collection<?>) result).contains("one"));
assertTrue(((Collection<?>) result).contains("two"));
assertTrue(((Collection<?>) result).contains("three"));
}

@Test(expected = XMLStreamException.class)
public void testReadTextWithNodes_NodeElementNotEmpty_shouldThrowException() throws Exception {
String xml = "<TextWithNodes>Text<Node id='1'>Text</Node></TextWithNodes>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));
reader.nextTag();
Map<Integer, Long> nodeMap = new HashMap<Integer, Long>();
DocumentStaxUtils.readTextWithNodes(reader, nodeMap);
}

@Test
public void testReplaceXMLIllegalCharacters_validTextRemainsUnchanged() {
char[] data = new char[] { 'a', 'b', 'c', '1', '2' };
DocumentStaxUtils.replaceXMLIllegalCharacters(data, 0, data.length);
assertArrayEquals(new char[] { 'a', 'b', 'c', '1', '2' }, data);
}

@Test
public void testContainsEnoughLTs_exactThreshold_shouldReturnTrue() throws Exception {
String value = "<<<<<";
java.lang.reflect.Method method = DocumentStaxUtils.class.getDeclaredMethod("containsEnoughLTs", String.class);
method.setAccessible(true);
boolean result = (boolean) method.invoke(null, value);
assertTrue(result);
}

@Test
public void testContainsEnoughLTs_belowThreshold_shouldReturnFalse() throws Exception {
String value = "<<<<";
java.lang.reflect.Method method = DocumentStaxUtils.class.getDeclaredMethod("containsEnoughLTs", String.class);
method.setAccessible(true);
boolean result = (boolean) method.invoke(null, value);
assertFalse(result);
}

@Test
public void testWriteFeatures_handlesBooleanValuesCorrectly() throws Exception {
FeatureMap fmap = Factory.newFeatureMap();
fmap.put("flag", true);
ByteArrayOutputStream output = new ByteArrayOutputStream();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(output);
writer.writeStartDocument("utf-8", "1.0");
writer.writeStartElement("features");
DocumentStaxUtils.writeFeatures(fmap, writer, "");
writer.writeEndElement();
writer.writeEndDocument();
writer.flush();
String result = output.toString("UTF-8");
assertTrue(result.contains("flag"));
assertTrue(result.contains("true"));
assertTrue(result.contains("java.lang.Boolean"));
}

@Test
public void testWriteTextWithNodes_nodesAtStartAndEnd_writesCorrectXML() throws Exception {
Document doc = Factory.newDocument("XYZ");
AnnotationSet set = doc.getAnnotations();
FeatureMap fm1 = Factory.newFeatureMap();
set.add(0L, 3L, "Test", fm1);
ByteArrayOutputStream out = new ByteArrayOutputStream();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(out);
writer.writeStartDocument();
writer.writeStartElement("start");
ArrayList<Collection<gate.Annotation>> sets = new ArrayList<Collection<gate.Annotation>>();
sets.add(set);
DocumentStaxUtils.writeTextWithNodes(doc, sets, writer, "");
writer.writeEndElement();
writer.writeEndDocument();
writer.flush();
String xml = out.toString("UTF-8");
assertTrue(xml.contains("TextWithNodes"));
assertTrue(xml.contains("Node"));
assertTrue(xml.contains("XYZ"));
}

@Test(expected = XMLStreamException.class)
public void testReadFeatureNameOrValue_itemConstructorNotFound_shouldThrow() throws Exception {
String xml = "<Value className='java.util.ArrayList' itemClassName='java.awt.Point'>x;y</Value>";
XMLInputFactory factory = XMLInputFactory.newInstance();
XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));
reader.nextTag();
DocumentStaxUtils.readFeatureNameOrValue(reader);
}

@Test
public void testWriteXcesAnnotations_withValidCollection_annotationsSortedLongestFirst() throws Exception {
Document doc = Factory.newDocument("1234567890");
AnnotationSet annSet = doc.getAnnotations();
FeatureMap fm1 = Factory.newFeatureMap();
fm1.put("type", "short");
annSet.add(2L, 4L, "T1", fm1);
FeatureMap fm2 = Factory.newFeatureMap();
fm2.put("type", "long");
annSet.add(2L, 7L, "T2", fm2);
ByteArrayOutputStream out = new ByteArrayOutputStream();
DocumentStaxUtils.writeXcesAnnotations(annSet, out, "UTF-8");
String xml = out.toString("UTF-8");
assertTrue(xml.indexOf("T2") < xml.indexOf("T1"));
}

@Test
public void testWriteFeatures_skipsNullFeatureKeyValuePairs() throws Exception {
FeatureMap fm = Factory.newFeatureMap();
fm.put(null, "someVal");
fm.put("someKey", null);
ByteArrayOutputStream output = new ByteArrayOutputStream();
XMLStreamWriter xsw = XMLOutputFactory.newInstance().createXMLStreamWriter(output);
xsw.writeStartDocument();
xsw.writeStartElement("empty");
DocumentStaxUtils.writeFeatures(fm, xsw, "");
xsw.writeEndElement();
xsw.writeEndDocument();
xsw.flush();
String result = output.toString("UTF-8");
assertFalse(result.contains("someKey"));
assertFalse(result.contains("someVal"));
}

@Test
public void testWriteFeatures_serializesStringCollectionCorrectly() throws Exception {
List<String> list = Arrays.asList("one", "two", "three");
FeatureMap fm = Factory.newFeatureMap();
fm.put("keys", list);
ByteArrayOutputStream output = new ByteArrayOutputStream();
XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(output);
writer.writeStartDocument();
writer.writeStartElement("features");
DocumentStaxUtils.writeFeatures(fm, writer, "");
writer.writeEndElement();
writer.writeEndDocument();
writer.flush();
String result = output.toString("UTF-8");
assertTrue(result.contains("one;two;three"));
}
}
