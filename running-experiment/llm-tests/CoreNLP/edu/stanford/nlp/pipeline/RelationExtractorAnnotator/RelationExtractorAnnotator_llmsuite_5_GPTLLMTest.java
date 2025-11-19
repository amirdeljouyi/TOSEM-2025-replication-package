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

public class RelationExtractorAnnotator_llmsuite_5_GPTLLMTest {

@Test
public void testGetVerboseWithLegacyKey() {
Properties props = new Properties();
props.setProperty("sup.relation.verbose", "true");
boolean result = RelationExtractorAnnotator.getVerbose(props);
assertTrue(result);
}

@Test
public void testGetVerboseWithModernKeyTrue() {
Properties props = new Properties();
props.setProperty("relation.verbose", "true");
boolean result = RelationExtractorAnnotator.getVerbose(props);
assertTrue(result);
}

@Test
public void testGetVerboseWithModernKeyFalse() {
Properties props = new Properties();
props.setProperty("relation.verbose", "false");
boolean result = RelationExtractorAnnotator.getVerbose(props);
assertFalse(result);
}

@Test
public void testGetModelNameLegacyKey() {
Properties props = new Properties();
props.setProperty("sup.relation.model", "legacy-path.model");
String result = RelationExtractorAnnotator.getModelName(props);
assertEquals("legacy-path.model", result);
}

@Test
public void testGetModelNameModernKey() {
Properties props = new Properties();
props.setProperty("relation.model", "new-path.model");
String result = RelationExtractorAnnotator.getModelName(props);
assertEquals("new-path.model", result);
}

@Test(expected = RuntimeException.class)
public void testConstructorThrowsWhenModelMissing() {
Properties props = new Properties();
props.setProperty("relation.model", "non/existent/model/path");
new RelationExtractorAnnotator(props);
}

@Test
public void testRequiresReturnsExpectedAnnotations() {
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> required = annotator.requires();
assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
assertTrue(required.contains(CoreAnnotations.NamedEntityTagAnnotation.class));
assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
assertTrue(required.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
}

@Test
public void testRequirementsSatisfiedReturnsExpectedSet() {
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
assertTrue(satisfied.contains(MachineReadingAnnotations.EntityMentionsAnnotation.class));
assertTrue(satisfied.contains(MachineReadingAnnotations.RelationMentionsAnnotation.class));
}

@Test
public void testAnnotateCopiesEntitiesAndRelations() {
CoreMap sentenceInput = new ArrayCoreMap();
List<CoreMap> origSentences = new ArrayList<CoreMap>();
origSentences.add(sentenceInput);
Annotation annotation = new Annotation("John works for IBM.");
annotation.set(CoreAnnotations.SentencesAnnotation.class, origSentences);
// EntityMention em1 = new EntityMention("John", 0, 1);
// EntityMention em2 = new EntityMention("IBM", 3, 4);
// RelationMention rm = new RelationMention("worksFor", em1, em2);
CoreMap sentenceOutput = new ArrayCoreMap();
// sentenceOutput.set(MachineReadingAnnotations.EntityMentionsAnnotation.class, Arrays.asList(em1, em2));
// sentenceOutput.set(MachineReadingAnnotations.RelationMentionsAnnotation.class, Arrays.asList(rm));
List<CoreMap> outputSentences = new ArrayList<CoreMap>();
outputSentences.add(sentenceOutput);
Annotation annotatedOutput = new Annotation("John works for IBM.");
annotatedOutput.set(CoreAnnotations.SentencesAnnotation.class, outputSentences);
MachineReading mockMachineReading = mock(MachineReading.class);
when(mockMachineReading.annotate(annotation)).thenReturn(annotatedOutput);
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMachineReading;
}
};
annotator.annotate(annotation);
List<CoreMap> resultSentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
CoreMap resultSentence = resultSentences.get(0);
List<EntityMention> resultEntities = resultSentence.get(MachineReadingAnnotations.EntityMentionsAnnotation.class);
List<RelationMention> resultRelations = resultSentence.get(MachineReadingAnnotations.RelationMentionsAnnotation.class);
assertNotNull(resultEntities);
assertEquals(2, resultEntities.size());
assertEquals("John", resultEntities.get(0).getValue());
assertEquals("IBM", resultEntities.get(1).getValue());
assertNotNull(resultRelations);
assertEquals(1, resultRelations.size());
assertEquals("worksFor", resultRelations.get(0).getType());
}

@Test
public void testAnnotateHandlesNullEntitiesAndRelations() {
CoreMap sentenceInput = new ArrayCoreMap();
List<CoreMap> origSentences = new ArrayList<CoreMap>();
origSentences.add(sentenceInput);
Annotation annotation = new Annotation("Nothing special.");
annotation.set(CoreAnnotations.SentencesAnnotation.class, origSentences);
CoreMap sentenceOutput = new ArrayCoreMap();
sentenceOutput.set(MachineReadingAnnotations.EntityMentionsAnnotation.class, null);
sentenceOutput.set(MachineReadingAnnotations.RelationMentionsAnnotation.class, null);
List<CoreMap> outputSentences = new ArrayList<CoreMap>();
outputSentences.add(sentenceOutput);
Annotation output = new Annotation("Nothing special.");
output.set(CoreAnnotations.SentencesAnnotation.class, outputSentences);
MachineReading mockMachineReading = mock(MachineReading.class);
when(mockMachineReading.annotate(annotation)).thenReturn(output);
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMachineReading;
}
};
annotator.annotate(annotation);
List<CoreMap> resultSentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
CoreMap resultSentence = resultSentences.get(0);
List<EntityMention> entities = resultSentence.get(MachineReadingAnnotations.EntityMentionsAnnotation.class);
List<RelationMention> relations = resultSentence.get(MachineReadingAnnotations.RelationMentionsAnnotation.class);
assertNull(entities);
assertNull(relations);
}

@Test
public void testAnnotateWithMultipleSentencesEachHavingResults() {
CoreMap sent1 = new ArrayCoreMap();
CoreMap sent2 = new ArrayCoreMap();
List<CoreMap> origSents = new ArrayList<CoreMap>();
origSents.add(sent1);
origSents.add(sent2);
Annotation annotation = new Annotation("Sentence 1. Sentence 2.");
annotation.set(CoreAnnotations.SentencesAnnotation.class, origSents);
// EntityMention em1 = new EntityMention("Apple", 0, 1);
// RelationMention rm1 = new RelationMention("company", em1, null);
CoreMap outSent1 = new ArrayCoreMap();
// outSent1.set(MachineReadingAnnotations.EntityMentionsAnnotation.class, Arrays.asList(em1));
// outSent1.set(MachineReadingAnnotations.RelationMentionsAnnotation.class, Arrays.asList(rm1));
// EntityMention em2 = new EntityMention("Google", 0, 1);
// RelationMention rm2 = new RelationMention("competitor", em2, null);
CoreMap outSent2 = new ArrayCoreMap();
// outSent2.set(MachineReadingAnnotations.EntityMentionsAnnotation.class, Arrays.asList(em2));
// outSent2.set(MachineReadingAnnotations.RelationMentionsAnnotation.class, Arrays.asList(rm2));
List<CoreMap> outputSents = new ArrayList<CoreMap>();
outputSents.add(outSent1);
outputSents.add(outSent2);
Annotation output = new Annotation("Sentence 1. Sentence 2.");
output.set(CoreAnnotations.SentencesAnnotation.class, outputSents);
MachineReading mockMR = mock(MachineReading.class);
when(mockMR.annotate(annotation)).thenReturn(output);
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
annotator.annotate(annotation);
List<CoreMap> results = annotation.get(CoreAnnotations.SentencesAnnotation.class);
CoreMap result1 = results.get(0);
CoreMap result2 = results.get(1);
List<EntityMention> res1Entities = result1.get(MachineReadingAnnotations.EntityMentionsAnnotation.class);
List<RelationMention> res1Rels = result1.get(MachineReadingAnnotations.RelationMentionsAnnotation.class);
assertEquals(1, res1Entities.size());
assertEquals("Apple", res1Entities.get(0).getValue());
assertEquals(1, res1Rels.size());
assertEquals("company", res1Rels.get(0).getType());
List<EntityMention> res2Entities = result2.get(MachineReadingAnnotations.EntityMentionsAnnotation.class);
List<RelationMention> res2Rels = result2.get(MachineReadingAnnotations.RelationMentionsAnnotation.class);
assertEquals(1, res2Entities.size());
assertEquals("Google", res2Entities.get(0).getValue());
assertEquals(1, res2Rels.size());
assertEquals("competitor", res2Rels.get(0).getType());
}

@Test
public void testGetVerboseWithNoPropertySet() {
Properties props = new Properties();
boolean verbose = RelationExtractorAnnotator.getVerbose(props);
assertFalse(verbose);
}

@Test
public void testGetModelNameWithNoPropertiesSet() {
Properties props = new Properties();
String model = RelationExtractorAnnotator.getModelName(props);
assertEquals(DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL, model);
}

@Test
public void testAnnotateWithMismatchedSentenceCount() {
Annotation annotation = new Annotation("Only one original sentence.");
CoreMap originalSentence = new ArrayCoreMap();
List<CoreMap> originalSentences = new ArrayList<CoreMap>();
originalSentences.add(originalSentence);
annotation.set(CoreAnnotations.SentencesAnnotation.class, originalSentences);
CoreMap outputSentence1 = new ArrayCoreMap();
CoreMap outputSentence2 = new ArrayCoreMap();
// EntityMention e1 = new EntityMention("X", 0, 1);
// RelationMention r1 = new RelationMention("relatedTo", e1, null);
// outputSentence1.set(MachineReadingAnnotations.EntityMentionsAnnotation.class, Arrays.asList(e1));
// outputSentence1.set(MachineReadingAnnotations.RelationMentionsAnnotation.class, Arrays.asList(r1));
// outputSentence2.set(MachineReadingAnnotations.EntityMentionsAnnotation.class, Arrays.asList(e1));
// outputSentence2.set(MachineReadingAnnotations.RelationMentionsAnnotation.class, Arrays.asList(r1));
List<CoreMap> outputSentences = new ArrayList<CoreMap>();
outputSentences.add(outputSentence1);
outputSentences.add(outputSentence2);
Annotation outputAnnotation = new Annotation("Multiple sentences output.");
outputAnnotation.set(CoreAnnotations.SentencesAnnotation.class, outputSentences);
MachineReading mockMR = mock(MachineReading.class);
when(mockMR.annotate(annotation)).thenReturn(outputAnnotation);
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
annotator.annotate(annotation);
List<CoreMap> resultSentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
assertNotNull(resultSentences);
CoreMap resultSentence = resultSentences.get(0);
List<EntityMention> em = resultSentence.get(MachineReadingAnnotations.EntityMentionsAnnotation.class);
List<RelationMention> rm = resultSentence.get(MachineReadingAnnotations.RelationMentionsAnnotation.class);
assertEquals(1, em.size());
assertEquals(1, rm.size());
}

@Test
public void testAnnotateWithEmptySentenceLists() {
Annotation annotation = new Annotation("Text exists but no sentences.");
annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());
Annotation output = new Annotation("Text exists but no sentences.");
output.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());
MachineReading mockMR = mock(MachineReading.class);
when(mockMR.annotate(annotation)).thenReturn(output);
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
annotator.annotate(annotation);
assertTrue(annotation.get(CoreAnnotations.SentencesAnnotation.class).isEmpty());
}

@Test
public void testAnnotateAnnotationWithNullSentenceList() {
Annotation annotation = new Annotation("Empty input.");
annotation.set(CoreAnnotations.SentencesAnnotation.class, null);
Annotation output = new Annotation("Empty input.");
output.set(CoreAnnotations.SentencesAnnotation.class, null);
MachineReading mockMR = mock(MachineReading.class);
when(mockMR.annotate(annotation)).thenReturn(output);
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
annotator.annotate(annotation);
assertNull(annotation.get(CoreAnnotations.SentencesAnnotation.class));
}

@Test
public void testRelationMentionWithTypeUnrelatedIsSkippedInVerboseFlow() {
CoreMap originalSentence = new ArrayCoreMap();
List<CoreMap> originalSentences = new ArrayList<CoreMap>();
originalSentences.add(originalSentence);
Annotation annotation = new Annotation("Testing verbose unrelated.");
annotation.set(CoreAnnotations.SentencesAnnotation.class, originalSentences);
// EntityMention em1 = new EntityMention("Alice", 0, 1);
// EntityMention em2 = new EntityMention("Bob", 1, 2);
// RelationMention unrelatedRelation = new RelationMention(RelationMention.UNRELATED, em1, em2);
CoreMap outputSentence = new ArrayCoreMap();
// outputSentence.set(MachineReadingAnnotations.EntityMentionsAnnotation.class, Arrays.asList(em1, em2));
// outputSentence.set(MachineReadingAnnotations.RelationMentionsAnnotation.class, Arrays.asList(unrelatedRelation));
Annotation output = new Annotation("Testing verbose unrelated.");
output.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(outputSentence));
MachineReading mockMR = mock(MachineReading.class);
when(mockMR.annotate(annotation)).thenReturn(output);
Properties props = new Properties();
props.setProperty("relation.verbose", "true");
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
annotator.annotate(annotation);
CoreMap result = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0);
List<RelationMention> resultRelations = result.get(MachineReadingAnnotations.RelationMentionsAnnotation.class);
assertEquals(1, resultRelations.size());
assertEquals(RelationMention.UNRELATED, resultRelations.get(0).getType());
}

@Test
public void testRelationMentionWithNullListAssignments() {
CoreMap sent = new ArrayCoreMap();
List<CoreMap> input = new ArrayList<CoreMap>();
input.add(sent);
Annotation annotation = new Annotation("null test");
annotation.set(CoreAnnotations.SentencesAnnotation.class, input);
CoreMap out = new ArrayCoreMap();
out.set(MachineReadingAnnotations.EntityMentionsAnnotation.class, null);
out.set(MachineReadingAnnotations.RelationMentionsAnnotation.class, null);
List<CoreMap> outSentences = new ArrayList<CoreMap>();
outSentences.add(out);
Annotation output = new Annotation("null test");
output.set(CoreAnnotations.SentencesAnnotation.class, outSentences);
MachineReading mockMR = mock(MachineReading.class);
when(mockMR.annotate(annotation)).thenReturn(output);
Properties props = new Properties();
props.setProperty("relation.verbose", "true");
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
annotator.annotate(annotation);
CoreMap result = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0);
assertNull(result.get(MachineReadingAnnotations.EntityMentionsAnnotation.class));
assertNull(result.get(MachineReadingAnnotations.RelationMentionsAnnotation.class));
}

@Test
public void testGetVerboseWithRandomTextValue() {
Properties props = new Properties();
props.setProperty("relation.verbose", "not-a-boolean");
boolean verbose = RelationExtractorAnnotator.getVerbose(props);
assertFalse(verbose);
}

@Test
public void testGetVerboseWithLegacyAndModernKeysBothSet() {
Properties props = new Properties();
props.setProperty("sup.relation.verbose", "true");
props.setProperty("relation.verbose", "false");
boolean verbose = RelationExtractorAnnotator.getVerbose(props);
assertTrue(verbose);
}

@Test
public void testGetModelNameWithLegacyAndModernKeysBothSet() {
Properties props = new Properties();
props.setProperty("sup.relation.model", "legacy-model.ser");
props.setProperty("relation.model", "modern-model.ser");
String model = RelationExtractorAnnotator.getModelName(props);
assertEquals("legacy-model.ser", model);
}

@Test
public void testAnnotateWithOutputSentenceMissingEntityAnnotation() {
Annotation annotation = new Annotation("Some text.");
CoreMap originalSentence = new ArrayCoreMap();
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(originalSentence));
CoreMap outputSentence = new ArrayCoreMap();
outputSentence.set(MachineReadingAnnotations.RelationMentionsAnnotation.class, new ArrayList<RelationMention>());
Annotation output = new Annotation("Some text.");
output.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(outputSentence));
MachineReading mockMR = mock(MachineReading.class);
when(mockMR.annotate(annotation)).thenReturn(output);
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
annotator.annotate(annotation);
List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
CoreMap result = sentences.get(0);
List<EntityMention> em = result.get(MachineReadingAnnotations.EntityMentionsAnnotation.class);
assertNull(em);
}

@Test
public void testAnnotateWithOutputSentenceMissingRelationAnnotation() {
Annotation annotation = new Annotation("Entity text.");
CoreMap originalSentence = new ArrayCoreMap();
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(originalSentence));
// EntityMention em = new EntityMention("Entity", 0, 1);
CoreMap outputSentence = new ArrayCoreMap();
// outputSentence.set(MachineReadingAnnotations.EntityMentionsAnnotation.class, Arrays.asList(em));
Annotation output = new Annotation("Entity text.");
output.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(outputSentence));
MachineReading mockMR = mock(MachineReading.class);
when(mockMR.annotate(annotation)).thenReturn(output);
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
annotator.annotate(annotation);
List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
CoreMap result = sentences.get(0);
List<RelationMention> rels = result.get(MachineReadingAnnotations.RelationMentionsAnnotation.class);
assertNull(rels);
}

@Test
public void testAnnotateWithNullReturnedFromMachineReading() {
Annotation annotation = new Annotation("Sentence.");
CoreMap sent = new ArrayCoreMap();
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sent));
MachineReading mockMR = mock(MachineReading.class);
when(mockMR.annotate(annotation)).thenReturn(null);
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
try {
annotator.annotate(annotation);
assertTrue(annotation.get(CoreAnnotations.SentencesAnnotation.class).contains(sent));
} catch (Exception e) {
fail("Annotate should handle null MachineReading output gracefully, but threw: " + e);
}
}

@Test
public void testAnnotateWithDifferentSentenceObjectsInInputAndOutput() {
Annotation annotation = new Annotation("Sentence.");
CoreMap originalSentence = new ArrayCoreMap();
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(originalSentence));
CoreMap outputSentence = new ArrayCoreMap();
// EntityMention em = new EntityMention("X", 0, 1);
// outputSentence.set(MachineReadingAnnotations.EntityMentionsAnnotation.class, Arrays.asList(em));
// RelationMention rm = new RelationMention("typeA", em, null);
// outputSentence.set(MachineReadingAnnotations.RelationMentionsAnnotation.class, Arrays.asList(rm));
Annotation output = new Annotation("Same sentence.");
output.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(outputSentence));
MachineReading mockMR = mock(MachineReading.class);
when(mockMR.annotate(annotation)).thenReturn(output);
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
annotator.annotate(annotation);
CoreMap target = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0);
List<RelationMention> rList = target.get(MachineReadingAnnotations.RelationMentionsAnnotation.class);
assertNotNull(rList);
assertEquals("typeA", rList.get(0).getType());
}

@Test
public void testAnnotateDoesNotCrashWhenNoOriginalSentencesExist() {
Annotation annotation = new Annotation("Nothing is annotated yet.");
annotation.set(CoreAnnotations.SentencesAnnotation.class, null);
MachineReading mockMR = mock(MachineReading.class);
when(mockMR.annotate(annotation)).thenReturn(annotation);
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
try {
annotator.annotate(annotation);
assertNull(annotation.get(CoreAnnotations.SentencesAnnotation.class));
} catch (Exception e) {
fail("Should not throw an exception when original sentences are null.");
}
}

@Test
public void testEmptyRelationMentionListAssignment() {
Annotation annotation = new Annotation("Sentence with no relation mentions.");
CoreMap sentence = new ArrayCoreMap();
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
CoreMap outputSentence = new ArrayCoreMap();
outputSentence.set(MachineReadingAnnotations.EntityMentionsAnnotation.class, new ArrayList<EntityMention>());
outputSentence.set(MachineReadingAnnotations.RelationMentionsAnnotation.class, new ArrayList<RelationMention>());
Annotation outputAnnotation = new Annotation("Sentence with no relation mentions.");
outputAnnotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(outputSentence));
MachineReading mockMR = mock(MachineReading.class);
when(mockMR.annotate(annotation)).thenReturn(outputAnnotation);
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
annotator.annotate(annotation);
CoreMap result = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0);
List<EntityMention> emList = result.get(MachineReadingAnnotations.EntityMentionsAnnotation.class);
List<RelationMention> relList = result.get(MachineReadingAnnotations.RelationMentionsAnnotation.class);
assertNotNull(emList);
assertTrue(emList.isEmpty());
assertNotNull(relList);
assertTrue(relList.isEmpty());
}

@Test
public void testAnnotateDoesNotReplaceOriginalSentenceObjects() {
Annotation annotation = new Annotation("Input sentence reused.");
CoreMap origSentence = new ArrayCoreMap();
String marker = "original";
origSentence.set(CoreAnnotations.TextAnnotation.class, marker);
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(origSentence));
CoreMap outputSentence = new ArrayCoreMap();
outputSentence.set(MachineReadingAnnotations.EntityMentionsAnnotation.class, new ArrayList<EntityMention>());
outputSentence.set(MachineReadingAnnotations.RelationMentionsAnnotation.class, new ArrayList<RelationMention>());
Annotation output = new Annotation("Input sentence reused.");
output.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(outputSentence));
MachineReading mockMR = mock(MachineReading.class);
when(mockMR.annotate(annotation)).thenReturn(output);
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
annotator.annotate(annotation);
CoreMap result = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0);
String resultMarker = result.get(CoreAnnotations.TextAnnotation.class);
assertEquals("original", resultMarker);
}

@Test
public void testOutputWithFewerSentencesThanOriginal() {
Annotation annotation = new Annotation("Multiple sentences.");
CoreMap sent1 = new ArrayCoreMap();
CoreMap sent2 = new ArrayCoreMap();
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sent1, sent2));
CoreMap outputSent = new ArrayCoreMap();
// EntityMention em = new EntityMention("Entity", 0, 1);
// RelationMention rm = new RelationMention("type", em, null);
// outputSent.set(MachineReadingAnnotations.EntityMentionsAnnotation.class, Arrays.asList(em));
// outputSent.set(MachineReadingAnnotations.RelationMentionsAnnotation.class, Arrays.asList(rm));
Annotation output = new Annotation("Multiple sentences.");
output.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(outputSent));
MachineReading mockMR = mock(MachineReading.class);
when(mockMR.annotate(annotation)).thenReturn(output);
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
annotator.annotate(annotation);
List<CoreMap> result = annotation.get(CoreAnnotations.SentencesAnnotation.class);
List<EntityMention> ems0 = result.get(0).get(MachineReadingAnnotations.EntityMentionsAnnotation.class);
List<RelationMention> rms0 = result.get(0).get(MachineReadingAnnotations.RelationMentionsAnnotation.class);
assertNotNull(ems0);
assertEquals(1, ems0.size());
assertNotNull(rms0);
assertEquals(1, rms0.size());
List<EntityMention> ems1 = result.get(1).get(MachineReadingAnnotations.EntityMentionsAnnotation.class);
List<RelationMention> rms1 = result.get(1).get(MachineReadingAnnotations.RelationMentionsAnnotation.class);
assertNull(ems1);
assertNull(rms1);
}

@Test
public void testEntityMentionWithEmptyStringHandledGracefully() {
Annotation annotation = new Annotation("Empty entity string.");
CoreMap sentence = new ArrayCoreMap();
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
// EntityMention em = new EntityMention("", 0, 0);
CoreMap out = new ArrayCoreMap();
// out.set(MachineReadingAnnotations.EntityMentionsAnnotation.class, Arrays.asList(em));
out.set(MachineReadingAnnotations.RelationMentionsAnnotation.class, new ArrayList<RelationMention>());
Annotation output = new Annotation("Empty entity string.");
output.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(out));
MachineReading mockMR = mock(MachineReading.class);
when(mockMR.annotate(annotation)).thenReturn(output);
Properties props = new Properties();
props.setProperty("relation.verbose", "true");
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
annotator.annotate(annotation);
CoreMap result = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0);
List<EntityMention> entities = result.get(MachineReadingAnnotations.EntityMentionsAnnotation.class);
assertEquals(1, entities.size());
assertEquals("", entities.get(0).getValue());
}

@Test
public void testRelationMentionNullTypeHandledGracefully() {
Annotation annotation = new Annotation("Null relation type.");
CoreMap sent = new ArrayCoreMap();
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sent));
// EntityMention em1 = new EntityMention("E1", 0, 1);
// EntityMention em2 = new EntityMention("E2", 1, 2);
// RelationMention rm = new RelationMention(null, em1, em2);
CoreMap out = new ArrayCoreMap();
// out.set(MachineReadingAnnotations.EntityMentionsAnnotation.class, Arrays.asList(em1, em2));
// out.set(MachineReadingAnnotations.RelationMentionsAnnotation.class, Arrays.asList(rm));
Annotation output = new Annotation("Null relation type.");
output.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(out));
MachineReading mockMR = mock(MachineReading.class);
when(mockMR.annotate(annotation)).thenReturn(output);
Properties props = new Properties();
props.setProperty("relation.verbose", "true");
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
annotator.annotate(annotation);
CoreMap result = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0);
List<RelationMention> rels = result.get(MachineReadingAnnotations.RelationMentionsAnnotation.class);
assertEquals(1, rels.size());
assertNull(rels.get(0).getType());
}

@Test
public void testNullEntityMentionListInOriginalSentence() {
Annotation annotation = new Annotation("Text.");
CoreMap originalSentence = new ArrayCoreMap();
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(originalSentence));
// EntityMention em = new EntityMention("Entity", 0, 1);
// RelationMention rm = new RelationMention("relType", em, null);
CoreMap outputSentence = new ArrayCoreMap();
// outputSentence.set(MachineReadingAnnotations.EntityMentionsAnnotation.class, Arrays.asList(em));
// outputSentence.set(MachineReadingAnnotations.RelationMentionsAnnotation.class, Arrays.asList(rm));
Annotation output = new Annotation("Text.");
output.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(outputSentence));
MachineReading mock = mock(MachineReading.class);
when(mock.annotate(annotation)).thenReturn(output);
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mock;
}
};
annotator.annotate(annotation);
CoreMap resultSent = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0);
List<EntityMention> entities = resultSent.get(MachineReadingAnnotations.EntityMentionsAnnotation.class);
List<RelationMention> relations = resultSent.get(MachineReadingAnnotations.RelationMentionsAnnotation.class);
assertNotNull(entities);
assertEquals("Entity", entities.get(0).getValue());
assertNotNull(relations);
assertEquals("relType", relations.get(0).getType());
}

@Test
public void testMissingOutputSentenceInMachineReadingAnnotationResult() {
Annotation annotation = new Annotation("Sentence.");
CoreMap orig = new ArrayCoreMap();
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(orig));
Annotation output = new Annotation("Sentence.");
output.set(CoreAnnotations.SentencesAnnotation.class, null);
MachineReading mr = mock(MachineReading.class);
when(mr.annotate(annotation)).thenReturn(output);
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mr;
}
};
annotator.annotate(annotation);
List<CoreMap> result = annotation.get(CoreAnnotations.SentencesAnnotation.class);
assertEquals(1, result.size());
assertNull(result.get(0).get(MachineReadingAnnotations.EntityMentionsAnnotation.class));
assertNull(result.get(0).get(MachineReadingAnnotations.RelationMentionsAnnotation.class));
}

@Test
public void testOutputSentenceWithDifferentEntityRelationListSizes() {
Annotation annotation = new Annotation("Some text.");
CoreMap original = new ArrayCoreMap();
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(original));
// EntityMention em1 = new EntityMention("E1", 0, 1);
// EntityMention em2 = new EntityMention("E2", 1, 2);
// RelationMention rm = new RelationMention("conflict", em1, em2);
CoreMap outputSentence = new ArrayCoreMap();
// outputSentence.set(MachineReadingAnnotations.EntityMentionsAnnotation.class, Arrays.asList(em1, em2));
// outputSentence.set(MachineReadingAnnotations.RelationMentionsAnnotation.class, Arrays.asList(rm));
Annotation output = new Annotation("Some text.");
output.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(outputSentence));
MachineReading mr = mock(MachineReading.class);
when(mr.annotate(annotation)).thenReturn(output);
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mr;
}
};
annotator.annotate(annotation);
CoreMap sentence = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0);
List<EntityMention> entities = sentence.get(MachineReadingAnnotations.EntityMentionsAnnotation.class);
List<RelationMention> relations = sentence.get(MachineReadingAnnotations.RelationMentionsAnnotation.class);
assertEquals(2, entities.size());
assertEquals(1, relations.size());
}

@Test
public void testAnnotationWithNoSentencesFieldAtAll() {
Annotation annotation = new Annotation("No sentence field set.");
MachineReading mockMR = mock(MachineReading.class);
when(mockMR.annotate(annotation)).thenReturn(new Annotation("No sentence field set."));
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
annotator.annotate(annotation);
assertNull(annotation.get(CoreAnnotations.SentencesAnnotation.class));
}

@Test
public void testAnnotateTwiceAndEnsureIdempotencyOnSecondCall() {
Annotation annotation = new Annotation("Double run.");
CoreMap sent = new ArrayCoreMap();
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sent));
// EntityMention em = new EntityMention("Foo", 0, 1);
// RelationMention rm = new RelationMention("type1", em, null);
CoreMap outputSentence = new ArrayCoreMap();
// outputSentence.set(MachineReadingAnnotations.EntityMentionsAnnotation.class, Arrays.asList(em));
// outputSentence.set(MachineReadingAnnotations.RelationMentionsAnnotation.class, Arrays.asList(rm));
Annotation output = new Annotation("Double run.");
output.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(outputSentence));
MachineReading mock = mock(MachineReading.class);
when(mock.annotate(annotation)).thenReturn(output);
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mock;
}
};
annotator.annotate(annotation);
List<EntityMention> entsFirstRun = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(MachineReadingAnnotations.EntityMentionsAnnotation.class);
annotator.annotate(annotation);
List<EntityMention> entsSecondRun = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(MachineReadingAnnotations.EntityMentionsAnnotation.class);
assertSame(entsFirstRun, entsSecondRun);
assertEquals("Foo", entsFirstRun.get(0).getValue());
}

@Test
public void testEmptyAnnotationObjectProvided() {
Annotation annotation = new Annotation("");
MachineReading mockMR = mock(MachineReading.class);
Annotation output = new Annotation("");
output.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());
when(mockMR.annotate(annotation)).thenReturn(output);
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
annotation.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<CoreMap>());
annotator.annotate(annotation);
List<CoreMap> result = annotation.get(CoreAnnotations.SentencesAnnotation.class);
assertTrue(result.isEmpty());
}

@Test
public void testNullAnnotationInputDoesNotCrash() {
Annotation annotation = null;
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
try {
annotator.annotate(annotation);
fail("Should throw NullPointerException due to null input.");
} catch (NullPointerException e) {
}
}

@Test
public void testRelationMentionWithSameEntityTwice() {
Annotation annotation = new Annotation("Sentence with double entity.");
CoreMap originalSentence = new ArrayCoreMap();
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(originalSentence));
// EntityMention em = new EntityMention("Company", 0, 1);
// RelationMention rm = new RelationMention("selfLink", em, em);
CoreMap outputSentence = new ArrayCoreMap();
// outputSentence.set(MachineReadingAnnotations.EntityMentionsAnnotation.class, Arrays.asList(em));
// outputSentence.set(MachineReadingAnnotations.RelationMentionsAnnotation.class, Arrays.asList(rm));
Annotation output = new Annotation("Sentence with double entity.");
output.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(outputSentence));
MachineReading mockMR = mock(MachineReading.class);
when(mockMR.annotate(annotation)).thenReturn(output);
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
props.setProperty("relation.verbose", "true");
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
annotator.annotate(annotation);
CoreMap result = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0);
List<RelationMention> rels = result.get(MachineReadingAnnotations.RelationMentionsAnnotation.class);
assertEquals(1, rels.size());
assertEquals("selfLink", rels.get(0).getType());
assertSame(rels.get(0).getEntityMentionArgs().get(0), rels.get(0).getEntityMentionArgs().get(1));
}

@Test
public void testEmptyPropertiesPassedToConstructor() {
Properties props = new Properties();
try {
new RelationExtractorAnnotator(props);
} catch (Exception e) {
assertTrue(e instanceof RuntimeException);
}
}

@Test
public void testModelPathSetToEmptyString() {
Properties props = new Properties();
props.setProperty("relation.model", "");
try {
new RelationExtractorAnnotator(props);
fail("Should throw RuntimeException due to invalid model path.");
} catch (RuntimeException e) {
assertNotNull(e);
}
}

@Test
public void testOriginalSentenceWithPreExistingAnnotationValues() {
Annotation annotation = new Annotation("Sentence.");
CoreMap sentence = new ArrayCoreMap();
List<EntityMention> existing = new ArrayList<EntityMention>();
// existing.add(new EntityMention("Old", 0, 1));
sentence.set(MachineReadingAnnotations.EntityMentionsAnnotation.class, existing);
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
// EntityMention emNew = new EntityMention("New", 0, 1);
// RelationMention rm = new RelationMention("override", emNew, null);
CoreMap out = new ArrayCoreMap();
// out.set(MachineReadingAnnotations.EntityMentionsAnnotation.class, Arrays.asList(emNew));
// out.set(MachineReadingAnnotations.RelationMentionsAnnotation.class, Arrays.asList(rm));
Annotation output = new Annotation("Sentence.");
output.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(out));
MachineReading mr = mock(MachineReading.class);
when(mr.annotate(annotation)).thenReturn(output);
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mr;
}
};
annotator.annotate(annotation);
List<EntityMention> ents = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(MachineReadingAnnotations.EntityMentionsAnnotation.class);
assertEquals(1, ents.size());
assertEquals("New", ents.get(0).getValue());
}

@Test
public void testExtraAnnotationsPreservedInOriginalSentence() {
Annotation annotation = new Annotation("Keep my data.");
CoreMap original = new ArrayCoreMap();
String customKey = "custom.annotation.key";
original.set(CoreAnnotations.TextAnnotation.class, "original-text");
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(original));
// EntityMention em = new EntityMention("X", 0, 1);
// RelationMention rm = new RelationMention("XtoX", em, em);
CoreMap output = new ArrayCoreMap();
// output.set(MachineReadingAnnotations.EntityMentionsAnnotation.class, Arrays.asList(em));
// output.set(MachineReadingAnnotations.RelationMentionsAnnotation.class, Arrays.asList(rm));
Annotation outputAnnotation = new Annotation("Keep my data.");
outputAnnotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(output));
MachineReading mock = mock(MachineReading.class);
when(mock.annotate(annotation)).thenReturn(outputAnnotation);
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mock;
}
};
annotator.annotate(annotation);
CoreMap resultSentence = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0);
String text = resultSentence.get(CoreAnnotations.TextAnnotation.class);
List<EntityMention> ents = resultSentence.get(MachineReadingAnnotations.EntityMentionsAnnotation.class);
assertEquals("original-text", text);
assertEquals(1, ents.size());
}

@Test
public void testAnnotateWhenMachineReadingReturnsAnnotationWithFewerSentences() {
Annotation annotation = new Annotation("Two input sentences.");
CoreMap input1 = new ArrayCoreMap();
CoreMap input2 = new ArrayCoreMap();
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(input1, input2));
// EntityMention em = new EntityMention("CEO", 0, 1);
// RelationMention rm = new RelationMention("worksAt", em, null);
CoreMap output1 = new ArrayCoreMap();
// output1.set(MachineReadingAnnotations.EntityMentionsAnnotation.class, Arrays.asList(em));
// output1.set(MachineReadingAnnotations.RelationMentionsAnnotation.class, Arrays.asList(rm));
Annotation output = new Annotation("Two input sentences.");
output.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(output1));
MachineReading mock = mock(MachineReading.class);
when(mock.annotate(annotation)).thenReturn(output);
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mock;
}
};
annotator.annotate(annotation);
List<CoreMap> result = annotation.get(CoreAnnotations.SentencesAnnotation.class);
List<EntityMention> list1 = result.get(0).get(MachineReadingAnnotations.EntityMentionsAnnotation.class);
List<EntityMention> list2 = result.get(1).get(MachineReadingAnnotations.EntityMentionsAnnotation.class);
assertEquals(1, list1.size());
assertNull(list2);
}

@Test
public void testConstructorHandlesExceptionFromRelationExtractorLoader() {
Properties props = new Properties();
props.setProperty("relation.model", "nonexistent-path");
try {
new RelationExtractorAnnotator(props);
fail("Expected RuntimeException due to loading model failure.");
} catch (RuntimeException e) {
assertNotNull(e.getCause());
}
}

@Test
public void testRelationMentionWithUnrelatedIsExcludedFromVerboseOutput() {
Annotation annotation = new Annotation("Sentence.");
CoreMap originalSent = new ArrayCoreMap();
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(originalSent));
// EntityMention em1 = new EntityMention("Org", 0, 1);
// EntityMention em2 = new EntityMention("Person", 1, 2);
// RelationMention unrelated = new RelationMention(RelationMention.UNRELATED, em1, em2);
CoreMap outputSent = new ArrayCoreMap();
// outputSent.set(MachineReadingAnnotations.EntityMentionsAnnotation.class, Arrays.asList(em1, em2));
// outputSent.set(MachineReadingAnnotations.RelationMentionsAnnotation.class, Arrays.asList(unrelated));
Annotation output = new Annotation("Sentence.");
output.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(outputSent));
MachineReading mock = mock(MachineReading.class);
when(mock.annotate(annotation)).thenReturn(output);
Properties props = new Properties();
props.setProperty("relation.verbose", "true");
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mock;
}
};
annotator.annotate(annotation);
CoreMap result = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0);
List<RelationMention> resultRels = result.get(MachineReadingAnnotations.RelationMentionsAnnotation.class);
assertEquals(1, resultRels.size());
assertEquals(RelationMention.UNRELATED, resultRels.get(0).getType());
}

@Test
public void testEmptyStringAnnotationDoesNotThrow() {
Annotation annotation = new Annotation("");
CoreMap sentence = new ArrayCoreMap();
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
CoreMap outputSent = new ArrayCoreMap();
outputSent.set(MachineReadingAnnotations.EntityMentionsAnnotation.class, new ArrayList<EntityMention>());
outputSent.set(MachineReadingAnnotations.RelationMentionsAnnotation.class, new ArrayList<RelationMention>());
Annotation output = new Annotation("");
output.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(outputSent));
MachineReading mock = mock(MachineReading.class);
when(mock.annotate(annotation)).thenReturn(output);
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mock;
}
};
annotator.annotate(annotation);
List<CoreMap> resultSentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
assertEquals(1, resultSentences.size());
List<EntityMention> entities = resultSentences.get(0).get(MachineReadingAnnotations.EntityMentionsAnnotation.class);
assertNotNull(entities);
assertTrue(entities.isEmpty());
}

@Test
public void testRelationMentionListMissingButEntityListPresent() {
Annotation annotation = new Annotation("Entity-only sentence.");
CoreMap sentence = new ArrayCoreMap();
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentence));
// EntityMention em = new EntityMention("Someone", 0, 1);
CoreMap outputSentence = new ArrayCoreMap();
// outputSentence.set(MachineReadingAnnotations.EntityMentionsAnnotation.class, Arrays.asList(em));
outputSentence.set(MachineReadingAnnotations.RelationMentionsAnnotation.class, null);
Annotation output = new Annotation("Entity-only sentence.");
output.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(outputSentence));
MachineReading mockMR = mock(MachineReading.class);
when(mockMR.annotate(annotation)).thenReturn(output);
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
annotator.annotate(annotation);
CoreMap result = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0);
List<EntityMention> ents = result.get(MachineReadingAnnotations.EntityMentionsAnnotation.class);
List<RelationMention> rels = result.get(MachineReadingAnnotations.RelationMentionsAnnotation.class);
assertEquals(1, ents.size());
assertEquals("Someone", ents.get(0).getValue());
assertNull(rels);
}

@Test
public void testRelationMentionPresentButEntityListMissing() {
Annotation annotation = new Annotation("Relation only.");
CoreMap inputSentence = new ArrayCoreMap();
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(inputSentence));
// EntityMention em1 = new EntityMention("E1", 0, 1);
// EntityMention em2 = new EntityMention("E2", 2, 3);
// RelationMention rel = new RelationMention("relationX", em1, em2);
CoreMap outputSent = new ArrayCoreMap();
outputSent.set(MachineReadingAnnotations.EntityMentionsAnnotation.class, null);
// outputSent.set(MachineReadingAnnotations.RelationMentionsAnnotation.class, Arrays.asList(rel));
Annotation output = new Annotation("Relation only.");
output.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(outputSent));
MachineReading mock = mock(MachineReading.class);
when(mock.annotate(annotation)).thenReturn(output);
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mock;
}
};
annotator.annotate(annotation);
CoreMap result = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0);
List<EntityMention> ents = result.get(MachineReadingAnnotations.EntityMentionsAnnotation.class);
List<RelationMention> rels = result.get(MachineReadingAnnotations.RelationMentionsAnnotation.class);
assertNull(ents);
assertNotNull(rels);
assertEquals(1, rels.size());
assertEquals("relationX", rels.get(0).getType());
}

@Test
public void testRelationExtractorModelFallbackToDefaultWhenKeyMissing() {
Properties props = new Properties();
String model = RelationExtractorAnnotator.getModelName(props);
assertEquals(DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL, model);
}

@Test
public void testGetVerboseWithFalseLegacyAndTrueModernShouldPreferModern() {
Properties props = new Properties();
props.setProperty("sup.relation.verbose", "false");
props.setProperty("relation.verbose", "true");
boolean verbose = RelationExtractorAnnotator.getVerbose(props);
assertFalse(verbose);
}

@Test
public void testMultipleSentencesWithNullsAndMixedEntityRelationData() {
Annotation annotation = new Annotation("Sentence A. Sentence B.");
CoreMap sentenceA = new ArrayCoreMap();
CoreMap sentenceB = new ArrayCoreMap();
annotation.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(sentenceA, sentenceB));
CoreMap outputA = new ArrayCoreMap();
// EntityMention em = new EntityMention("KeyX", 0, 1);
// outputA.set(MachineReadingAnnotations.EntityMentionsAnnotation.class, Arrays.asList(em));
outputA.set(MachineReadingAnnotations.RelationMentionsAnnotation.class, null);
CoreMap outputB = new ArrayCoreMap();
outputB.set(MachineReadingAnnotations.EntityMentionsAnnotation.class, null);
outputB.set(MachineReadingAnnotations.RelationMentionsAnnotation.class, null);
Annotation output = new Annotation("Sentence A. Sentence B.");
output.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(outputA, outputB));
MachineReading mockMR = mock(MachineReading.class);
when(mockMR.annotate(annotation)).thenReturn(output);
Properties props = new Properties();
props.setProperty("relation.verbose", "true");
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
annotator.annotate(annotation);
List<CoreMap> result = annotation.get(CoreAnnotations.SentencesAnnotation.class);
assertEquals(2, result.size());
assertEquals(1, result.get(0).get(MachineReadingAnnotations.EntityMentionsAnnotation.class).size());
assertNull(result.get(1).get(MachineReadingAnnotations.EntityMentionsAnnotation.class));
assertNull(result.get(1).get(MachineReadingAnnotations.RelationMentionsAnnotation.class));
}
}
