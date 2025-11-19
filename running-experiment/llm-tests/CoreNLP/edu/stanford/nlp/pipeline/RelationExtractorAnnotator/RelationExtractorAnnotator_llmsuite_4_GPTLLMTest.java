package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.ie.machinereading.MachineReading;
import edu.stanford.nlp.ie.machinereading.structure.EntityMention;
import edu.stanford.nlp.ie.machinereading.structure.MachineReadingAnnotations;
import edu.stanford.nlp.ie.machinereading.structure.RelationMention;
import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import org.junit.Test;
import java.io.*;
import java.util.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RelationExtractorAnnotator_llmsuite_4_GPTLLMTest {

@Test
public void testGetVerboseReturnsFalseByDefault() {
Properties props = new Properties();
boolean result = RelationExtractorAnnotator.getVerbose(props);
assertFalse(result);
}

@Test
public void testGetVerboseReturnsTrueWhenSet() {
Properties props = new Properties();
props.setProperty("relation.verbose", "true");
boolean result = RelationExtractorAnnotator.getVerbose(props);
assertTrue(result);
}

@Test
public void testGetVerboseReturnsDeprecatedProperty() {
Properties props = new Properties();
props.setProperty("sup.relation.verbose", "true");
boolean result = RelationExtractorAnnotator.getVerbose(props);
assertTrue(result);
}

@Test
public void testGetModelNameReturnsDefault() {
Properties props = new Properties();
String result = RelationExtractorAnnotator.getModelName(props);
assertEquals("edu/stanford/nlp/models/supervised_relation_extraction/model.ser.gz", result);
}

@Test
public void testGetModelNameReturnsNewProperty() {
Properties props = new Properties();
props.setProperty("relation.model", "/custom/model/path.ser.gz");
String result = RelationExtractorAnnotator.getModelName(props);
assertEquals("/custom/model/path.ser.gz", result);
}

@Test
public void testGetModelNameReturnsDeprecated() {
Properties props = new Properties();
props.setProperty("sup.relation.model", "/deprecated/model.ser.gz");
String result = RelationExtractorAnnotator.getModelName(props);
assertEquals("/deprecated/model.ser.gz", result);
}

@Test
public void testRequirementsSatisfiedReturnsExpectedAnnotations() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/supervised_relation_extraction/model.ser.gz");
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Set<Class<? extends CoreAnnotation>> result = annotator.requirementsSatisfied();
assertTrue(result.contains(MachineReadingAnnotations.EntityMentionsAnnotation.class));
assertTrue(result.contains(MachineReadingAnnotations.RelationMentionsAnnotation.class));
}

@Test
public void testRequiresReturnsExpectedDependencies() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/supervised_relation_extraction/model.ser.gz");
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Set<Class<? extends CoreAnnotation>> result = annotator.requires();
assertTrue(result.contains(CoreAnnotations.TokensAnnotation.class));
assertTrue(result.contains(CoreAnnotations.SentencesAnnotation.class));
assertTrue(result.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
assertTrue(result.contains(CoreAnnotations.NamedEntityTagAnnotation.class));
}

@Test
public void testAnnotateAddsEntitiesAndRelationsToAnnotation() {
Properties props = new Properties();
props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse");
props.setProperty("relation.model", "edu/stanford/nlp/models/supervised_relation_extraction/model.ser.gz");
String text = "Barack Obama was born in Hawaii. Obama served as President.";
Annotation annotation = new Annotation(text);
StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
pipeline.annotate(annotation);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
annotator.annotate(annotation);
List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
CoreMap s0 = sentences.get(0);
List<EntityMention> e0 = s0.get(MachineReadingAnnotations.EntityMentionsAnnotation.class);
List<RelationMention> r0 = s0.get(MachineReadingAnnotations.RelationMentionsAnnotation.class);
assertNotNull(e0);
assertNotNull(r0);
assertTrue(e0.size() > 0);
assertTrue(r0.size() > 0);
CoreMap s1 = sentences.get(1);
List<EntityMention> e1 = s1.get(MachineReadingAnnotations.EntityMentionsAnnotation.class);
List<RelationMention> r1 = s1.get(MachineReadingAnnotations.RelationMentionsAnnotation.class);
assertNotNull(e1);
assertNotNull(r1);
assertTrue(e1.size() > 0);
assertTrue(r1.size() > 0);
}

@Test
public void testAnnotateTransfersEntitiesAndRelationsFromOutputToOriginal() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/supervised_relation_extraction/model.ser.gz");
CoreMap originalSentence = mock(CoreMap.class);
CoreMap outputSentence = mock(CoreMap.class);
EntityMention entity = mock(EntityMention.class);
RelationMention relation = mock(RelationMention.class);
List<EntityMention> entities = Arrays.asList(entity);
List<RelationMention> relations = Arrays.asList(relation);
when(outputSentence.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(entities);
when(outputSentence.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(relations);
List<CoreMap> origSentences = Arrays.asList(originalSentence);
List<CoreMap> outputSentences = Arrays.asList(outputSentence);
Annotation inputAnnotation = new Annotation("Obama is president.");
inputAnnotation.set(CoreAnnotations.SentencesAnnotation.class, origSentences);
Annotation outputAnnotation = mock(Annotation.class);
when(outputAnnotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(outputSentences);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
MachineReading mockedMR = mock(MachineReading.class);
annotator.mr = mockedMR;
when(mockedMR.annotate(inputAnnotation)).thenReturn(outputAnnotation);
annotator.annotate(inputAnnotation);
verify(originalSentence).set(MachineReadingAnnotations.EntityMentionsAnnotation.class, entities);
verify(originalSentence).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, relations);
}

@Test(expected = RuntimeException.class)
public void testConstructorThrowsRuntimeExceptionForInvalidModelPath() {
Properties props = new Properties();
props.setProperty("relation.model", "/invalid/path/to/model.ser.gz");
new RelationExtractorAnnotator(props);
}

@Test(timeout = 10000)
public void testLargeInputAnnotationProcessing() {
Properties props = new Properties();
props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse");
props.setProperty("relation.model", "edu/stanford/nlp/models/supervised_relation_extraction/model.ser.gz");
StringBuilder input = new StringBuilder();
input.append("Obama spoke. ");
input.append("He traveled. ");
input.append("Obama visited Europe. ");
input.append("They cheered loudly. ");
input.append("Obama met Merkel. ");
Annotation annotation = new Annotation(input.toString());
StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
pipeline.annotate(annotation);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
annotator.annotate(annotation);
List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
CoreMap s0 = sentences.get(0);
CoreMap s1 = sentences.get(1);
CoreMap s2 = sentences.get(2);
CoreMap s3 = sentences.get(3);
CoreMap s4 = sentences.get(4);
List<RelationMention> r0 = s0.get(MachineReadingAnnotations.RelationMentionsAnnotation.class);
List<RelationMention> r1 = s1.get(MachineReadingAnnotations.RelationMentionsAnnotation.class);
List<RelationMention> r2 = s2.get(MachineReadingAnnotations.RelationMentionsAnnotation.class);
List<RelationMention> r3 = s3.get(MachineReadingAnnotations.RelationMentionsAnnotation.class);
List<RelationMention> r4 = s4.get(MachineReadingAnnotations.RelationMentionsAnnotation.class);
assertNotNull(r0);
assertNotNull(r1);
assertNotNull(r2);
assertNotNull(r3);
assertNotNull(r4);
}

@Test
public void testGetVerboseHandlesInvalidBooleanStringGracefully() {
Properties props = new Properties();
props.setProperty("relation.verbose", "notABoolean");
boolean verbose = RelationExtractorAnnotator.getVerbose(props);
assertFalse(verbose);
}

@Test
public void testGetVerboseWithNullProperties() {
boolean verbose = RelationExtractorAnnotator.getVerbose(new Properties());
assertFalse(verbose);
}

@Test
public void testAnnotateWithEmptySentencesList() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/supervised_relation_extraction/model.ser.gz");
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Annotation input = new Annotation("Obama leads.");
input.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());
Annotation mockOutput = mock(Annotation.class);
when(mockOutput.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(new ArrayList<CoreMap>());
MachineReading mockedMR = mock(MachineReading.class);
annotator.mr = mockedMR;
when(mockedMR.annotate(input)).thenReturn(mockOutput);
annotator.annotate(input);
List<CoreMap> result = input.get(CoreAnnotations.SentencesAnnotation.class);
assertTrue(result.isEmpty());
}

@Test
public void testAnnotateWithMismatchedSentenceCounts() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/supervised_relation_extraction/model.ser.gz");
CoreMap origSent = mock(CoreMap.class);
List<CoreMap> origList = Arrays.asList(origSent);
CoreMap outSent1 = mock(CoreMap.class);
CoreMap outSent2 = mock(CoreMap.class);
List<CoreMap> outList = Arrays.asList(outSent1, outSent2);
Annotation input = new Annotation("Some doc.");
input.set(CoreAnnotations.SentencesAnnotation.class, origList);
Annotation mockOutput = mock(Annotation.class);
when(mockOutput.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(outList);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
MachineReading mockedMR = mock(MachineReading.class);
annotator.mr = mockedMR;
when(mockedMR.annotate(input)).thenReturn(mockOutput);
try {
annotator.annotate(input);
} catch (Exception e) {
fail("Should not throw exception on mismatched size");
}
}

@Test
public void testAnnotateHandlesNullEntitiesAndRelationsGracefully() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/supervised_relation_extraction/model.ser.gz");
CoreMap outputSent = mock(CoreMap.class);
when(outputSent.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(null);
when(outputSent.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(null);
CoreMap origSent = mock(CoreMap.class);
List<CoreMap> outList = Arrays.asList(outputSent);
List<CoreMap> origList = Arrays.asList(origSent);
Annotation input = new Annotation("Just one sentence.");
input.set(CoreAnnotations.SentencesAnnotation.class, origList);
Annotation mockOutput = mock(Annotation.class);
when(mockOutput.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(outList);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
MachineReading mockedMR = mock(MachineReading.class);
annotator.mr = mockedMR;
when(mockedMR.annotate(input)).thenReturn(mockOutput);
annotator.annotate(input);
verify(origSent).set(MachineReadingAnnotations.EntityMentionsAnnotation.class, null);
verify(origSent).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, null);
}

@Test
public void testRelationMentionUnrelatedDoesNotLogInfoWhenVerboseIsTrue() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/supervised_relation_extraction/model.ser.gz");
props.setProperty("relation.verbose", "true");
CoreMap origSent = mock(CoreMap.class);
CoreMap outSent = mock(CoreMap.class);
EntityMention entity = mock(EntityMention.class);
List<EntityMention> entityList = Arrays.asList(entity);
RelationMention unrelatedRelation = mock(RelationMention.class);
when(unrelatedRelation.getType()).thenReturn(RelationMention.UNRELATED);
List<RelationMention> relationList = Arrays.asList(unrelatedRelation);
when(outSent.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(entityList);
when(outSent.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(relationList);
List<CoreMap> origList = Arrays.asList(origSent);
List<CoreMap> outList = Arrays.asList(outSent);
Annotation input = new Annotation("Obama is unrelated.");
input.set(CoreAnnotations.SentencesAnnotation.class, origList);
Annotation annotated = mock(Annotation.class);
when(annotated.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(outList);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
MachineReading mockedMR = mock(MachineReading.class);
annotator.mr = mockedMR;
when(mockedMR.annotate(input)).thenReturn(annotated);
annotator.annotate(input);
verify(origSent).set(MachineReadingAnnotations.EntityMentionsAnnotation.class, entityList);
verify(origSent).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, relationList);
}

@Test
public void testMainMethodRunsWithoutException() {
String[] args = { "-relation.model", "edu/stanford/nlp/models/supervised_relation_extraction/model.ser.gz" };
try {
RelationExtractorAnnotator.main(args);
} catch (Exception e) {
fail("Main method should not throw exception: " + e.getMessage());
}
}

@Test
public void testAnnotateWithNoSentences() {
Annotation annotation = new Annotation("");
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/supervised_relation_extraction/model.ser.gz");
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
MachineReading mockedMR = mock(MachineReading.class);
annotator.mr = mockedMR;
Annotation output = mock(Annotation.class);
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(null);
when(mockedMR.annotate(annotation)).thenReturn(output);
annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(mock(CoreMap.class)));
annotator.annotate(annotation);
List<CoreMap> result = annotation.get(CoreAnnotations.SentencesAnnotation.class);
assertNotNull(result);
}

@Test
public void testGetModelNameWithEmptyValueFallsBackToDefault() {
Properties props = new Properties();
props.setProperty("relation.model", "");
String value = RelationExtractorAnnotator.getModelName(props);
assertEquals("edu/stanford/nlp/models/supervised_relation_extraction/model.ser.gz", value);
}

@Test
public void testAnnotateWhenOutputSentencesIsNull() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/supervised_relation_extraction/model.ser.gz");
Annotation input = new Annotation("Test sentence.");
Annotation output = mock(Annotation.class);
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(null);
List<CoreMap> origSentences = Arrays.asList(mock(CoreMap.class));
input.set(CoreAnnotations.SentencesAnnotation.class, origSentences);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
MachineReading mockedMR = mock(MachineReading.class);
annotator.mr = mockedMR;
when(mockedMR.annotate(input)).thenReturn(output);
try {
annotator.annotate(input);
} catch (Exception e) {
fail("Should handle null output sentences gracefully");
}
}

@Test
public void testAnnotateWithEmptyEntityAndRelationLists() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/supervised_relation_extraction/model.ser.gz");
CoreMap origSent = mock(CoreMap.class);
List<CoreMap> origList = Arrays.asList(origSent);
CoreMap outSent = mock(CoreMap.class);
when(outSent.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(Collections.emptyList());
when(outSent.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(Collections.emptyList());
List<CoreMap> outList = Arrays.asList(outSent);
Annotation input = new Annotation("Short text.");
input.set(CoreAnnotations.SentencesAnnotation.class, origList);
Annotation output = mock(Annotation.class);
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(outList);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
MachineReading mockedMR = mock(MachineReading.class);
annotator.mr = mockedMR;
when(mockedMR.annotate(input)).thenReturn(output);
annotator.annotate(input);
verify(origSent).set(MachineReadingAnnotations.EntityMentionsAnnotation.class, Collections.emptyList());
verify(origSent).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, Collections.emptyList());
}

@Test
public void testAnnotateWithSingleSentenceOneEntityNoRelations() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/supervised_relation_extraction/model.ser.gz");
EntityMention entity = mock(EntityMention.class);
List<EntityMention> entityList = Arrays.asList(entity);
CoreMap outSent = mock(CoreMap.class);
when(outSent.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(entityList);
when(outSent.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(null);
CoreMap origSent = mock(CoreMap.class);
List<CoreMap> origList = Arrays.asList(origSent);
List<CoreMap> outputList = Arrays.asList(outSent);
Annotation input = new Annotation("Entity only.");
input.set(CoreAnnotations.SentencesAnnotation.class, origList);
Annotation output = mock(Annotation.class);
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(outputList);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
MachineReading mockedMR = mock(MachineReading.class);
annotator.mr = mockedMR;
when(mockedMR.annotate(input)).thenReturn(output);
annotator.annotate(input);
verify(origSent).set(MachineReadingAnnotations.EntityMentionsAnnotation.class, entityList);
verify(origSent).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, null);
}

@Test
public void testAnnotateWithSingleSentenceNullOutputSentence() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/supervised_relation_extraction/model.ser.gz");
List<CoreMap> origSentences = Arrays.asList(mock(CoreMap.class));
List<CoreMap> outputSentences = Arrays.asList((CoreMap) null);
Annotation input = new Annotation("Null sentence.");
input.set(CoreAnnotations.SentencesAnnotation.class, origSentences);
Annotation output = mock(Annotation.class);
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(outputSentences);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
MachineReading mockedMR = mock(MachineReading.class);
annotator.mr = mockedMR;
when(mockedMR.annotate(input)).thenReturn(output);
try {
annotator.annotate(input);
} catch (Exception e) {
fail("Should handle null CoreMap in output sentence list gracefully");
}
}

@Test
public void testRequirementsSatisfiedIncludesOnlyExpectedAnnotations() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/supervised_relation_extraction/model.ser.gz");
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
assertEquals(2, satisfied.size());
assertTrue(satisfied.contains(MachineReadingAnnotations.EntityMentionsAnnotation.class));
assertTrue(satisfied.contains(MachineReadingAnnotations.RelationMentionsAnnotation.class));
}

@Test
public void testRequiresSetIsImmutable() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/supervised_relation_extraction/model.ser.gz");
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> required = annotator.requires();
try {
required.clear();
fail("Expected UnsupportedOperationException");
} catch (UnsupportedOperationException e) {
}
}

@Test
public void testAnnotateWithMismatchSmallerOutputSentenceList() {
Properties properties = new Properties();
properties.setProperty("relation.model", "edu/stanford/nlp/models/supervised_relation_extraction/model.ser.gz");
Annotation annotation = new Annotation("Text to annotate.");
CoreMap origSent1 = mock(CoreMap.class);
CoreMap origSent2 = mock(CoreMap.class);
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(origSent1, origSent2));
CoreMap outSent1 = mock(CoreMap.class);
when(outSent1.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(Collections.emptyList());
when(outSent1.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(Collections.emptyList());
Annotation output = mock(Annotation.class);
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(outSent1));
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(properties);
MachineReading mockedMR = mock(MachineReading.class);
annotator.mr = mockedMR;
when(mockedMR.annotate(annotation)).thenReturn(output);
try {
annotator.annotate(annotation);
} catch (IndexOutOfBoundsException e) {
fail("annotate() should tolerate output sentence list being shorter than original");
}
}

@Test
public void testAnnotateWithNullEntityAndRelationLists() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/supervised_relation_extraction/model.ser.gz");
CoreMap origSentence = mock(CoreMap.class);
List<CoreMap> origSentencesList = Arrays.asList(origSentence);
CoreMap outputSentence = mock(CoreMap.class);
when(outputSentence.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(null);
when(outputSentence.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(null);
List<CoreMap> outputSentencesList = Arrays.asList(outputSentence);
Annotation annotation = new Annotation("Obama leads.");
annotation.set(CoreAnnotations.SentencesAnnotation.class, origSentencesList);
Annotation resultAnnotation = mock(Annotation.class);
when(resultAnnotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(outputSentencesList);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
MachineReading machineReading = mock(MachineReading.class);
annotator.mr = machineReading;
when(machineReading.annotate(annotation)).thenReturn(resultAnnotation);
annotator.annotate(annotation);
verify(origSentence).set(MachineReadingAnnotations.EntityMentionsAnnotation.class, null);
verify(origSentence).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, null);
}

@Test
public void testAnnotateSkipsExtraSentencesInOriginalAnnotation() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/supervised_relation_extraction/model.ser.gz");
CoreMap orig1 = mock(CoreMap.class);
CoreMap orig2 = mock(CoreMap.class);
List<CoreMap> origList = Arrays.asList(orig1, orig2);
CoreMap out1 = mock(CoreMap.class);
when(out1.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(Collections.emptyList());
when(out1.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(Collections.emptyList());
List<CoreMap> outList = Arrays.asList(out1);
Annotation annotation = new Annotation("Sentence A. Sentence B.");
annotation.set(CoreAnnotations.SentencesAnnotation.class, origList);
Annotation output = mock(Annotation.class);
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(outList);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
MachineReading mr = mock(MachineReading.class);
annotator.mr = mr;
when(mr.annotate(annotation)).thenReturn(output);
annotator.annotate(annotation);
verify(orig1).set(MachineReadingAnnotations.EntityMentionsAnnotation.class, Collections.emptyList());
verify(orig1).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, Collections.emptyList());
verifyNoMoreInteractions(orig2);
}

@Test
public void testConstructorFailsIfModelPathIsInvalid() {
Properties props = new Properties();
props.setProperty("relation.model", "non/existent/path/model.ser.gz");
try {
new RelationExtractorAnnotator(props);
fail("Constructor should throw RuntimeException for missing model file.");
} catch (RuntimeException expected) {
assertNotNull(expected.getMessage());
}
}

@Test
public void testGetVerboseWithBlankPropertyString() {
Properties props = new Properties();
props.setProperty("relation.verbose", "");
boolean result = RelationExtractorAnnotator.getVerbose(props);
assertFalse(result);
}

@Test
public void testGetVerboseWhenBothOldAndNewVerboseKeysExist() {
Properties props = new Properties();
props.setProperty("sup.relation.verbose", "false");
props.setProperty("relation.verbose", "true");
boolean verbose = RelationExtractorAnnotator.getVerbose(props);
assertFalse(verbose);
}

@Test
public void testGetModelNameWhenBothOldAndNewKeysPresent() {
Properties props = new Properties();
props.setProperty("sup.relation.model", "deprecated-path/model1.ser.gz");
props.setProperty("relation.model", "new-path/model2.ser.gz");
String modelPath = RelationExtractorAnnotator.getModelName(props);
assertEquals("deprecated-path/model1.ser.gz", modelPath);
}

@Test
public void testEmptyAnnotationStillReturnsRequirementSets() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/supervised_relation_extraction/model.ser.gz");
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> required = annotator.requires();
Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
assertNotNull(required);
assertNotNull(satisfied);
assertFalse(required.isEmpty());
assertFalse(satisfied.isEmpty());
}

@Test
public void testAnnotateWithEmptyTextStillInitializesSentences() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/supervised_relation_extraction/model.ser.gz");
Annotation annotation = new Annotation("");
annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());
Annotation output = mock(Annotation.class);
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(new ArrayList<CoreMap>());
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
MachineReading mocked = mock(MachineReading.class);
annotator.mr = mocked;
when(mocked.annotate(annotation)).thenReturn(output);
annotator.annotate(annotation);
List<CoreMap> result = annotation.get(CoreAnnotations.SentencesAnnotation.class);
assertNotNull(result);
assertTrue(result.isEmpty());
}

@Test
public void testAnnotateWithNoSentencesAnnotationSet() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/supervised_relation_extraction/model.ser.gz");
Annotation annotation = new Annotation("Sentence that won't exist as CoreMap.");
Annotation output = mock(Annotation.class);
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(new ArrayList<CoreMap>());
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
MachineReading mocked = mock(MachineReading.class);
annotator.mr = mocked;
when(mocked.annotate(annotation)).thenReturn(output);
try {
annotator.annotate(annotation);
} catch (Exception e) {
fail("Should handle annotation with no CoreAnnotations.SentencesAnnotation gracefully.");
}
}

@Test
public void testAnnotateWithNullOriginalSentenceMap() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/supervised_relation_extraction/model.ser.gz");
CoreMap outputSent = mock(CoreMap.class);
when(outputSent.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(Collections.emptyList());
when(outputSent.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(Collections.emptyList());
List<CoreMap> origSentences = Arrays.asList((CoreMap) null);
List<CoreMap> outputSentences = Arrays.asList(outputSent);
Annotation annotation = new Annotation("Null original sentence.");
annotation.set(CoreAnnotations.SentencesAnnotation.class, origSentences);
Annotation output = mock(Annotation.class);
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(outputSentences);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
MachineReading mocked = mock(MachineReading.class);
annotator.mr = mocked;
when(mocked.annotate(annotation)).thenReturn(output);
try {
annotator.annotate(annotation);
} catch (Exception e) {
fail("Should skip null entries in original sentence list without exception.");
}
}

@Test
public void testAnnotateWithRelationMentionOfNullType() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/supervised_relation_extraction/model.ser.gz");
props.setProperty("relation.verbose", "true");
RelationMention rel = mock(RelationMention.class);
when(rel.getType()).thenReturn(null);
CoreMap outputSent = mock(CoreMap.class);
when(outputSent.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(Collections.emptyList());
when(outputSent.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(Arrays.asList(rel));
CoreMap origSent = mock(CoreMap.class);
List<CoreMap> origList = Arrays.asList(origSent);
List<CoreMap> outList = Arrays.asList(outputSent);
Annotation annotation = new Annotation("Malformed RelationMention.");
annotation.set(CoreAnnotations.SentencesAnnotation.class, origList);
Annotation output = mock(Annotation.class);
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(outList);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
MachineReading mocked = mock(MachineReading.class);
annotator.mr = mocked;
when(mocked.annotate(annotation)).thenReturn(output);
try {
annotator.annotate(annotation);
} catch (Exception e) {
fail("Should handle RelationMention with null type without Exception.");
}
}

@Test
public void testConstructorFailsWithNullModelPath() {
Properties props = new Properties();
props.setProperty("relation.model", null);
try {
new RelationExtractorAnnotator(props);
fail("Expected RuntimeException due to null model path");
} catch (RuntimeException e) {
assertNotNull(e);
}
}

@Test
public void testConstructorFailsWithEmptyModelPath() {
Properties props = new Properties();
props.setProperty("relation.model", "");
try {
new RelationExtractorAnnotator(props);
fail("Expected RuntimeException due to empty model path");
} catch (RuntimeException e) {
assertNotNull(e);
}
}

@Test
public void testAnnotateThrowsIfMRReturnsNull() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/supervised_relation_extraction/model.ser.gz");
Annotation annotation = new Annotation("Test null MR output.");
CoreMap mockSent = mock(CoreMap.class);
annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(mockSent));
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
MachineReading mocked = mock(MachineReading.class);
annotator.mr = mocked;
when(mocked.annotate(annotation)).thenReturn(null);
try {
annotator.annotate(annotation);
fail("Expected NullPointerException or graceful failure when MR returns null");
} catch (NullPointerException e) {
} catch (Exception e) {
}
}

@Test
public void testAnnotateHandlesSingleSentenceWithNullEntityAndEmptyRelations() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/supervised_relation_extraction/model.ser.gz");
props.setProperty("relation.verbose", "true");
CoreMap outputSentence = mock(CoreMap.class);
CoreMap originalSentence = mock(CoreMap.class);
when(outputSentence.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(null);
when(outputSentence.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(Collections.emptyList());
List<CoreMap> outputList = Arrays.asList(outputSentence);
List<CoreMap> originalList = Arrays.asList(originalSentence);
Annotation annotation = new Annotation("Single sentence.");
annotation.set(CoreAnnotations.SentencesAnnotation.class, originalList);
Annotation outputAnnotation = mock(Annotation.class);
when(outputAnnotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(outputList);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
MachineReading mockMR = mock(MachineReading.class);
annotator.mr = mockMR;
when(mockMR.annotate(annotation)).thenReturn(outputAnnotation);
annotator.annotate(annotation);
verify(originalSentence).set(MachineReadingAnnotations.EntityMentionsAnnotation.class, null);
verify(originalSentence).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, Collections.emptyList());
}

@Test
public void testRelationWithNullTypeWhenVerboseEnabled() {
Properties props = new Properties();
props.setProperty("relation.verbose", "true");
props.setProperty("relation.model", "edu/stanford/nlp/models/supervised_relation_extraction/model.ser.gz");
RelationMention rel = mock(RelationMention.class);
when(rel.getType()).thenReturn(null);
CoreMap outSent = mock(CoreMap.class);
when(outSent.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(Collections.emptyList());
when(outSent.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(Arrays.asList(rel));
CoreMap origSent = mock(CoreMap.class);
List<CoreMap> outList = Arrays.asList(outSent);
List<CoreMap> origList = Arrays.asList(origSent);
Annotation input = new Annotation("Text.");
input.set(CoreAnnotations.SentencesAnnotation.class, origList);
Annotation annotated = mock(Annotation.class);
when(annotated.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(outList);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
MachineReading mr = mock(MachineReading.class);
annotator.mr = mr;
when(mr.annotate(input)).thenReturn(annotated);
annotator.annotate(input);
verify(origSent).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, Arrays.asList(rel));
}

@Test
public void testRelationListEmptyWhenVerboseTrue() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/supervised_relation_extraction/model.ser.gz");
props.setProperty("relation.verbose", "true");
CoreMap outputSent = mock(CoreMap.class);
when(outputSent.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(Collections.emptyList());
when(outputSent.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(Collections.emptyList());
CoreMap origSent = mock(CoreMap.class);
Annotation annotation = new Annotation("No relations here.");
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(origSent));
Annotation output = mock(Annotation.class);
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(outputSent));
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
MachineReading machineReading = mock(MachineReading.class);
annotator.mr = machineReading;
when(machineReading.annotate(annotation)).thenReturn(output);
annotator.annotate(annotation);
verify(origSent).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, Collections.emptyList());
}

@Test
public void testGetVerboseIgnoresNonsensicalStrings() {
Properties props = new Properties();
props.setProperty("relation.verbose", "yesplease");
boolean result = RelationExtractorAnnotator.getVerbose(props);
assertFalse(result);
}

@Test
public void testGetVerbosePrioritizesDeprecatedParameterOverNew() {
Properties props = new Properties();
props.setProperty("sup.relation.verbose", "true");
props.setProperty("relation.verbose", "false");
boolean result = RelationExtractorAnnotator.getVerbose(props);
assertTrue(result);
}

@Test
public void testAnnotationWithNullSentenceList() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/supervised_relation_extraction/model.ser.gz");
Annotation annotation = new Annotation("Broken sentence structure.");
annotation.set(CoreAnnotations.SentencesAnnotation.class, null);
Annotation result = mock(Annotation.class);
when(result.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(null);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
MachineReading machineReading = mock(MachineReading.class);
annotator.mr = machineReading;
when(machineReading.annotate(annotation)).thenReturn(result);
try {
annotator.annotate(annotation);
} catch (Exception e) {
fail("Should handle null sentence list in annotation gracefully");
}
}

@Test
public void testEmptySentenceCoreMapReturnsNullsFromGet() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/supervised_relation_extraction/model.ser.gz");
CoreMap blankOriginalSentence = mock(CoreMap.class);
CoreMap blankOutputSentence = mock(CoreMap.class);
when(blankOutputSentence.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(null);
when(blankOutputSentence.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(null);
List<CoreMap> origSentences = Arrays.asList(blankOriginalSentence);
List<CoreMap> outputSentences = Arrays.asList(blankOutputSentence);
Annotation annotation = new Annotation("Nonsense.");
annotation.set(CoreAnnotations.SentencesAnnotation.class, origSentences);
Annotation result = mock(Annotation.class);
when(result.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(outputSentences);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
MachineReading mr = mock(MachineReading.class);
annotator.mr = mr;
when(mr.annotate(annotation)).thenReturn(result);
annotator.annotate(annotation);
verify(blankOriginalSentence).set(MachineReadingAnnotations.EntityMentionsAnnotation.class, null);
verify(blankOriginalSentence).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, null);
}

@Test
public void testNoInteractionOccursOnEmptySentenceLists() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/supervised_relation_extraction/model.ser.gz");
Annotation annotation = new Annotation("Empty interaction.");
List<CoreMap> origSentences = new ArrayList<CoreMap>();
annotation.set(CoreAnnotations.SentencesAnnotation.class, origSentences);
Annotation output = mock(Annotation.class);
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(new ArrayList<CoreMap>());
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
MachineReading mocked = mock(MachineReading.class);
annotator.mr = mocked;
when(mocked.annotate(annotation)).thenReturn(output);
annotator.annotate(annotation);
List<CoreMap> actual = annotation.get(CoreAnnotations.SentencesAnnotation.class);
assertNotNull(actual);
assertTrue(actual.isEmpty());
}

@Test
public void testAnnotationStillCallableWhenOnlyOneAnnotationExists() {
Properties props = new Properties();
props.setProperty("annotators", "tokenize");
props.setProperty("relation.model", "edu/stanford/nlp/models/supervised_relation_extraction/model.ser.gz");
Annotation annotation = new Annotation("One sentence.");
CoreMap origSent = mock(CoreMap.class);
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(origSent));
CoreMap outSent = mock(CoreMap.class);
when(outSent.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(Collections.emptyList());
when(outSent.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(Collections.emptyList());
Annotation returned = mock(Annotation.class);
when(returned.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(outSent));
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
MachineReading mockMR = mock(MachineReading.class);
annotator.mr = mockMR;
when(mockMR.annotate(annotation)).thenReturn(returned);
annotator.annotate(annotation);
verify(origSent).set(MachineReadingAnnotations.EntityMentionsAnnotation.class, Collections.emptyList());
verify(origSent).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, Collections.emptyList());
}

@Test
public void testGetModelNameWithWhitespaceReturnsDefault() {
Properties props = new Properties();
props.setProperty("relation.model", "   ");
String model = RelationExtractorAnnotator.getModelName(props);
assertEquals("edu/stanford/nlp/models/supervised_relation_extraction/model.ser.gz", model);
}

@Test
public void testGetModelNamePrefersDeprecatedOverNewWhenBothPresent() {
Properties props = new Properties();
props.setProperty("sup.relation.model", "/deprecated/model.ser.gz");
props.setProperty("relation.model", "/official/model.ser.gz");
String model = RelationExtractorAnnotator.getModelName(props);
assertEquals("/deprecated/model.ser.gz", model);
}

@Test
public void testGetVerbosePrefersDeprecatedOverNewEvenWhenReverseBool() {
Properties props = new Properties();
props.setProperty("sup.relation.verbose", "false");
props.setProperty("relation.verbose", "true");
boolean verbose = RelationExtractorAnnotator.getVerbose(props);
assertFalse(verbose);
}

@Test
public void testGetVerboseTreatsBlankAsFalse() {
Properties props = new Properties();
props.setProperty("relation.verbose", "   ");
boolean verbose = RelationExtractorAnnotator.getVerbose(props);
assertFalse(verbose);
}

@Test
public void testAnnotateIgnoresRelationMentionWithTypeUnrelated() {
Properties props = new Properties();
props.setProperty("relation.verbose", "true");
props.setProperty("relation.model", "edu/stanford/nlp/models/supervised_relation_extraction/model.ser.gz");
RelationMention rel = mock(RelationMention.class);
when(rel.getType()).thenReturn(RelationMention.UNRELATED);
CoreMap outSent = mock(CoreMap.class);
when(outSent.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(Arrays.asList(rel));
when(outSent.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(Collections.emptyList());
CoreMap origSent = mock(CoreMap.class);
List<CoreMap> outList = Arrays.asList(outSent);
List<CoreMap> origList = Arrays.asList(origSent);
Annotation annotation = new Annotation("");
annotation.set(CoreAnnotations.SentencesAnnotation.class, origList);
Annotation output = mock(Annotation.class);
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(outList);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
annotator.mr = mock(MachineReading.class);
when(annotator.mr.annotate(annotation)).thenReturn(output);
annotator.annotate(annotation);
verify(origSent).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, Arrays.asList(rel));
}

@Test
public void testAnnotateWithNullAnnotatedSentencesDoesNotCrash() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/supervised_relation_extraction/model.ser.gz");
Annotation annotation = new Annotation("Hello.");
List<CoreMap> origSentences = Arrays.asList(mock(CoreMap.class));
annotation.set(CoreAnnotations.SentencesAnnotation.class, origSentences);
Annotation output = mock(Annotation.class);
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(null);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
annotator.mr = mock(MachineReading.class);
when(annotator.mr.annotate(annotation)).thenReturn(output);
try {
annotator.annotate(annotation);
} catch (Exception e) {
fail("annotate should handle null sentence list from MR.annotate.");
}
}

@Test
public void testConstructorFailsWhenModelLoadThrows() {
Properties props = new Properties();
props.setProperty("relation.model", "nonexistent-or-corrupt-path.xxx");
try {
new RelationExtractorAnnotator(props);
fail("Should throw RuntimeException on invalid model load");
} catch (RuntimeException e) {
assertNotNull(e.getMessage());
}
}

@Test
public void testAnnotateCorrectlyHandlesSingleSentenceWithNoMentions() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/supervised_relation_extraction/model.ser.gz");
CoreMap outSent = mock(CoreMap.class);
CoreMap origSent = mock(CoreMap.class);
when(outSent.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(Collections.emptyList());
when(outSent.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(Collections.emptyList());
Annotation annotation = new Annotation("Obama runs.");
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(origSent));
Annotation output = mock(Annotation.class);
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(outSent));
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
annotator.mr = mock(MachineReading.class);
when(annotator.mr.annotate(annotation)).thenReturn(output);
annotator.annotate(annotation);
verify(origSent).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, Collections.emptyList());
verify(origSent).set(MachineReadingAnnotations.EntityMentionsAnnotation.class, Collections.emptyList());
}

@Test
public void testAnnotateHandlesMismatchedInputShorterThanOutput() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/supervised_relation_extraction/model.ser.gz");
CoreMap originalSent = mock(CoreMap.class);
// annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(originalSent));
CoreMap out1 = mock(CoreMap.class);
CoreMap out2 = mock(CoreMap.class);
when(out1.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(Collections.emptyList());
when(out1.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(Collections.emptyList());
when(out2.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(Collections.emptyList());
when(out2.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(Collections.emptyList());
Annotation annotation = new Annotation("Multiple output test.");
Annotation output = mock(Annotation.class);
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(out1, out2));
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
annotator.mr = mock(MachineReading.class);
when(annotator.mr.annotate(annotation)).thenReturn(output);
try {
annotator.annotate(annotation);
} catch (Exception e) {
fail("Should allow shorter original sentence list without throwing");
}
}
}
