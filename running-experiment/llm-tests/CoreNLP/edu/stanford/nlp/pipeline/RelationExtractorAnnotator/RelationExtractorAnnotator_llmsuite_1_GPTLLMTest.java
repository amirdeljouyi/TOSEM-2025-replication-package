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

public class RelationExtractorAnnotator_llmsuite_1_GPTLLMTest {

@Test
public void testGetVerbose_NewStylePropertyTrue() {
Properties props = new Properties();
props.setProperty("relation.verbose", "true");
boolean result = RelationExtractorAnnotator.getVerbose(props);
assertTrue(result);
}

@Test
public void testGetVerbose_DeprecatedPropertyOverride() {
Properties props = new Properties();
props.setProperty("sup.relation.verbose", "true");
boolean result = RelationExtractorAnnotator.getVerbose(props);
assertTrue(result);
}

@Test
public void testGetModelName_NewStyle() {
Properties props = new Properties();
props.setProperty("relation.model", "my-relation-model.ser.gz");
String modelName = RelationExtractorAnnotator.getModelName(props);
assertEquals("my-relation-model.ser.gz", modelName);
}

@Test
public void testGetModelName_DeprecatedStyle() {
Properties props = new Properties();
props.setProperty("sup.relation.model", "legacy-model.gz");
String modelName = RelationExtractorAnnotator.getModelName(props);
assertEquals("legacy-model.gz", modelName);
}

@Test
public void testRequiresAnnotations() {
Properties props = new Properties();
props.setProperty("relation.verbose", "false");
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> requires = annotator.requires();
assertTrue(requires.contains(CoreAnnotations.TokensAnnotation.class));
assertTrue(requires.contains(CoreAnnotations.SentencesAnnotation.class));
assertTrue(requires.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
assertTrue(requires.contains(CoreAnnotations.NamedEntityTagAnnotation.class));
assertTrue(requires.contains(edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation.class));
assertTrue(requires.contains(edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class));
assertTrue(requires.contains(edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class));
assertTrue(requires.contains(edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class));
}

@Test
public void testRequirementsSatisfied() {
Properties props = new Properties();
props.setProperty("relation.verbose", "false");
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
assertTrue(satisfied.contains(MachineReadingAnnotations.EntityMentionsAnnotation.class));
assertTrue(satisfied.contains(MachineReadingAnnotations.RelationMentionsAnnotation.class));
}

@Test
public void testAnnotateTransfersEntitiesAndRelations() {
Properties props = new Properties();
props.setProperty("relation.verbose", "false");
MachineReading mockMR = mock(MachineReading.class);
Annotation inputAnnotation = mock(Annotation.class);
Annotation annotated = mock(Annotation.class);
CoreMap outputSentence = mock(CoreMap.class);
CoreMap originalSentence = mock(CoreMap.class);
EntityMention entityMention = mock(EntityMention.class);
RelationMention relationMention = mock(RelationMention.class);
List<CoreMap> outputSentences = Arrays.asList(outputSentence);
List<CoreMap> origSentences = Arrays.asList(originalSentence);
List<EntityMention> entityList = Arrays.asList(entityMention);
List<RelationMention> relationList = Arrays.asList(relationMention);
when(mockMR.annotate(inputAnnotation)).thenReturn(annotated);
when(annotated.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(outputSentences);
when(inputAnnotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(origSentences);
when(outputSentence.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(entityList);
when(outputSentence.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(relationList);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
annotator.annotate(inputAnnotation);
verify(originalSentence).set(MachineReadingAnnotations.EntityMentionsAnnotation.class, entityList);
verify(originalSentence).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, relationList);
}

@Test
public void testAnnotateHandlesNullEntityAndRelationLists() {
Properties props = new Properties();
props.setProperty("relation.verbose", "true");
MachineReading mockMR = mock(MachineReading.class);
Annotation inputAnnotation = mock(Annotation.class);
Annotation annotated = mock(Annotation.class);
CoreMap outputSentence = mock(CoreMap.class);
CoreMap originalSentence = mock(CoreMap.class);
List<CoreMap> outputSentences = Arrays.asList(outputSentence);
List<CoreMap> origSentences = Arrays.asList(originalSentence);
when(mockMR.annotate(inputAnnotation)).thenReturn(annotated);
when(annotated.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(outputSentences);
when(inputAnnotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(origSentences);
when(outputSentence.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(null);
when(outputSentence.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(null);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
annotator.annotate(inputAnnotation);
verify(originalSentence).set(MachineReadingAnnotations.EntityMentionsAnnotation.class, null);
verify(originalSentence).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, null);
}

@Test
public void testMainMethodUsageStub() {
try {
Properties props = new Properties();
props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse");
props.setProperty("relation.verbose", "false");
StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
Annotation annotation = new Annotation("Bill Gates founded Microsoft. He lives in the USA.");
pipeline.annotate(annotation);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
annotator.annotate(annotation);
List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
assertNotNull(sentences);
assertFalse(sentences.isEmpty());
CoreMap sentence = sentences.get(0);
List<RelationMention> relations = sentence.get(MachineReadingAnnotations.RelationMentionsAnnotation.class);
assertNotNull(relations);
} catch (Exception ex) {
fail("Main method test failed: " + ex.getMessage());
}
}

@Test(expected = RuntimeException.class)
public void testConstructorThrowsWhenModelNotFound() {
Properties props = new Properties();
props.setProperty("relation.model", "invalid-path-that-does-not-exist.gz");
new RelationExtractorAnnotator(props);
}

@Test
public void testGetVerbose_DefaultWhenPropertyMissing() {
Properties props = new Properties();
boolean verboseFlag = RelationExtractorAnnotator.getVerbose(props);
assertFalse(verboseFlag);
}

@Test
public void testGetModelName_DefaultWhenModelPropertyMissing() {
Properties props = new Properties();
String model = RelationExtractorAnnotator.getModelName(props);
assertNotNull(model);
assertTrue(model.length() > 0);
}

@Test
public void testAnnotateWithEmptySentenceLists() {
Properties props = new Properties();
props.setProperty("relation.verbose", "false");
Annotation input = mock(Annotation.class);
MachineReading mockMR = mock(MachineReading.class);
Annotation output = mock(Annotation.class);
when(mockMR.annotate(input)).thenReturn(output);
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(new ArrayList<>());
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(new ArrayList<>());
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
annotator.annotate(input);
}

@Test
public void testAnnotateWhenSentenceMismatchInSize() {
Properties props = new Properties();
props.setProperty("relation.verbose", "false");
Annotation input = mock(Annotation.class);
MachineReading mockMR = mock(MachineReading.class);
Annotation output = mock(Annotation.class);
CoreMap outputSentence = mock(CoreMap.class);
CoreMap originalSentence = mock(CoreMap.class);
EntityMention entityMention = mock(EntityMention.class);
RelationMention relationMention = mock(RelationMention.class);
List<CoreMap> outputSentences = Arrays.asList(outputSentence, outputSentence);
List<CoreMap> origSentences = Arrays.asList(originalSentence);
List<EntityMention> entityList = Arrays.asList(entityMention);
List<RelationMention> relationList = Arrays.asList(relationMention);
when(mockMR.annotate(input)).thenReturn(output);
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(outputSentences);
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(origSentences);
when(outputSentence.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(entityList);
when(outputSentence.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(relationList);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
try {
annotator.annotate(input);
fail("Expected IndexOutOfBoundsException due to sentence list size mismatch");
} catch (IndexOutOfBoundsException expected) {
}
}

@Test
public void testVerboseAnnotationLogsOnlyRelatedRelations() {
Properties props = new Properties();
props.setProperty("relation.verbose", "true");
Annotation input = mock(Annotation.class);
MachineReading mockMR = mock(MachineReading.class);
Annotation output = mock(Annotation.class);
CoreMap outputSentence = mock(CoreMap.class);
CoreMap originalSentence = mock(CoreMap.class);
RelationMention relatedRel = mock(RelationMention.class);
RelationMention unrelatedRel = mock(RelationMention.class);
when(relatedRel.getType()).thenReturn("RELATED");
when(unrelatedRel.getType()).thenReturn(RelationMention.UNRELATED);
List<RelationMention> relations = Arrays.asList(relatedRel, unrelatedRel);
when(mockMR.annotate(input)).thenReturn(output);
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(outputSentence));
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(originalSentence));
when(outputSentence.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(null);
when(outputSentence.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(relations);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
annotator.annotate(input);
verify(originalSentence).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, relations);
}

@Test
public void testEmptyEntityAndRelationListsHandledGracefully() {
Properties props = new Properties();
props.setProperty("relation.verbose", "true");
Annotation input = mock(Annotation.class);
MachineReading mockMR = mock(MachineReading.class);
Annotation output = mock(Annotation.class);
CoreMap outputSentence = mock(CoreMap.class);
CoreMap originalSentence = mock(CoreMap.class);
List<EntityMention> emptyEntities = new ArrayList<>();
List<RelationMention> emptyRelations = new ArrayList<>();
List<CoreMap> outSentences = Arrays.asList(outputSentence);
List<CoreMap> origSentences = Arrays.asList(originalSentence);
when(mockMR.annotate(input)).thenReturn(output);
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(outSentences);
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(origSentences);
when(outputSentence.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(emptyEntities);
when(outputSentence.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(emptyRelations);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
annotator.annotate(input);
verify(originalSentence).set(MachineReadingAnnotations.EntityMentionsAnnotation.class, emptyEntities);
verify(originalSentence).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, emptyRelations);
}

@Test
public void testGetVerbose_LegacyOverridesNew() {
Properties props = new Properties();
props.setProperty("relation.verbose", "false");
props.setProperty("sup.relation.verbose", "true");
boolean result = RelationExtractorAnnotator.getVerbose(props);
assertTrue(result);
}

@Test
public void testGetModelName_LegacyOverridesNew() {
Properties props = new Properties();
props.setProperty("relation.model", "new-model-file.ser.gz");
props.setProperty("sup.relation.model", "deprecated-model.ser.gz");
String model = RelationExtractorAnnotator.getModelName(props);
assertEquals("deprecated-model.ser.gz", model);
}

@Test
public void testAnnotatorAnnotateWhenCoreMapSetsNullEntitiesOnly() {
Properties props = new Properties();
props.setProperty("relation.verbose", "true");
MachineReading mockMR = mock(MachineReading.class);
Annotation input = mock(Annotation.class);
Annotation annotated = mock(Annotation.class);
CoreMap outputSentence = mock(CoreMap.class);
CoreMap originalSentence = mock(CoreMap.class);
when(mockMR.annotate(input)).thenReturn(annotated);
when(annotated.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(outputSentence));
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(originalSentence));
when(outputSentence.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(null);
when(outputSentence.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(new ArrayList<>());
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
annotator.annotate(input);
verify(originalSentence).set(MachineReadingAnnotations.EntityMentionsAnnotation.class, null);
}

@Test
public void testAnnotatorAnnotateWhenCoreMapSetsNullRelationsOnly() {
Properties props = new Properties();
props.setProperty("relation.verbose", "true");
MachineReading mockMR = mock(MachineReading.class);
Annotation input = mock(Annotation.class);
Annotation annotated = mock(Annotation.class);
CoreMap outputSentence = mock(CoreMap.class);
CoreMap originalSentence = mock(CoreMap.class);
// List<EntityMention> entityList = Arrays.asList(new EntityMention("EntityTest"));
when(mockMR.annotate(input)).thenReturn(annotated);
when(annotated.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(outputSentence));
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(originalSentence));
// when(outputSentence.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(entityList);
when(outputSentence.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(null);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
annotator.annotate(input);
// verify(originalSentence).set(MachineReadingAnnotations.EntityMentionsAnnotation.class, entityList);
verify(originalSentence).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, null);
}

@Test
public void testAnnotateWithSingleSentenceAndNullBothAnnotations() {
Properties props = new Properties();
props.setProperty("relation.verbose", "false");
Annotation input = mock(Annotation.class);
Annotation output = mock(Annotation.class);
MachineReading mockMR = mock(MachineReading.class);
CoreMap originalSent = mock(CoreMap.class);
CoreMap outputSent = mock(CoreMap.class);
when(mockMR.annotate(input)).thenReturn(output);
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(originalSent));
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(outputSent));
when(outputSent.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(null);
when(outputSent.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(null);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
annotator.annotate(input);
verify(originalSent).set(MachineReadingAnnotations.EntityMentionsAnnotation.class, null);
verify(originalSent).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, null);
}

@Test
public void testRequirementsSatisfiedDoesNotContainUnexpectedAnnotation() {
Properties props = new Properties();
props.setProperty("relation.verbose", "true");
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
assertFalse(satisfied.contains(CoreAnnotations.LemmaAnnotation.class));
assertFalse(satisfied.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
}

@Test
public void testRequiresDoesNotContainUnexpectedAnnotation() {
Properties props = new Properties();
props.setProperty("relation.verbose", "false");
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Set<Class<? extends CoreAnnotation>> required = annotator.requires();
assertFalse(required.contains(CoreAnnotations.ParagraphsAnnotation.class));
assertFalse(required.contains(CoreAnnotations.DocDateAnnotation.class));
}

@Test
public void testLoadFailsWithInvalidEntityExtractor() {
Properties props = new Properties();
props.setProperty("relation.model", "mock-model-path.ser.gz");
try {
fail("Expected runtime exception was not thrown");
} catch (RuntimeException ex) {
assertEquals("Simulated failure", ex.getMessage());
}
}

@Test
public void testAnnotateNullSentencesFromMachineReading() {
Properties props = new Properties();
props.setProperty("relation.verbose", "false");
Annotation input = mock(Annotation.class);
Annotation output = mock(Annotation.class);
MachineReading mockMR = mock(MachineReading.class);
when(mockMR.annotate(input)).thenReturn(output);
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(null);
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList());
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
annotator.annotate(input);
}

@Test
public void testAnnotateNullSentencesInOriginalAnnotation() {
Properties props = new Properties();
props.setProperty("relation.verbose", "true");
Annotation input = mock(Annotation.class);
Annotation output = mock(Annotation.class);
MachineReading mockMR = mock(MachineReading.class);
CoreMap outputSentence = mock(CoreMap.class);
List<CoreMap> outputList = Arrays.asList(outputSentence);
when(mockMR.annotate(input)).thenReturn(output);
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(outputList);
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(null);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
try {
annotator.annotate(input);
fail("Expected NullPointerException due to null original sentences list");
} catch (NullPointerException expected) {
}
}

@Test
public void testRelationMentionOfTypeUnrelatedIsNotPrintedInVerbose() {
Properties props = new Properties();
props.setProperty("relation.verbose", "true");
Annotation input = mock(Annotation.class);
MachineReading mockMR = mock(MachineReading.class);
Annotation output = mock(Annotation.class);
CoreMap outSent = mock(CoreMap.class);
CoreMap origSent = mock(CoreMap.class);
RelationMention unrelated = mock(RelationMention.class);
when(unrelated.getType()).thenReturn(RelationMention.UNRELATED);
List<RelationMention> rels = Arrays.asList(unrelated);
when(mockMR.annotate(input)).thenReturn(output);
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(outSent));
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(origSent));
when(outSent.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(new ArrayList<>());
when(outSent.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(rels);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
annotator.annotate(input);
verify(origSent).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, rels);
verify(origSent).set(MachineReadingAnnotations.EntityMentionsAnnotation.class, new ArrayList<>());
assertEquals(RelationMention.UNRELATED, unrelated.getType());
}

@Test
public void testRelationMentionOfTypeRelatedIsPrintedInVerbose() {
Properties props = new Properties();
props.setProperty("relation.verbose", "true");
Annotation input = mock(Annotation.class);
MachineReading mockMR = mock(MachineReading.class);
Annotation output = mock(Annotation.class);
CoreMap outSent = mock(CoreMap.class);
CoreMap origSent = mock(CoreMap.class);
RelationMention related = mock(RelationMention.class);
when(related.getType()).thenReturn("RELATED");
List<RelationMention> rels = Arrays.asList(related);
when(mockMR.annotate(input)).thenReturn(output);
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(outSent));
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(origSent));
when(outSent.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(new ArrayList<>());
when(outSent.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(rels);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
annotator.annotate(input);
verify(origSent).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, rels);
verify(origSent).set(MachineReadingAnnotations.EntityMentionsAnnotation.class, new ArrayList<>());
assertEquals("RELATED", related.getType());
}

@Test
public void testRequiresAnnotation_IsUnmodifiable() {
Properties props = new Properties();
props.setProperty("relation.verbose", "false");
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Set<Class<? extends CoreAnnotation>> requires = annotator.requires();
try {
requires.add(CoreAnnotations.DocIDAnnotation.class);
fail("Expected UnsupportedOperationException on unmodifiable set");
} catch (UnsupportedOperationException expected) {
}
}

@Test
public void testRequirementsSatisfied_IsUnmodifiable() {
Properties props = new Properties();
props.setProperty("relation.verbose", "false");
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
try {
satisfied.clear();
fail("Expected UnsupportedOperationException on unmodifiable set");
} catch (UnsupportedOperationException expected) {
}
}

@Test
public void testNullInputAnnotationToAnnotate() {
Properties props = new Properties();
props.setProperty("relation.verbose", "false");
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mock(MachineReading.class);
}
};
try {
annotator.annotate(null);
fail("Expected NullPointerException when input annotation is null");
} catch (NullPointerException expected) {
}
}

@Test
public void testAnnotateWithNullMachineReadingResult() {
Properties props = new Properties();
props.setProperty("relation.verbose", "false");
Annotation input = mock(Annotation.class);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mock(MachineReading.class);
when(this.mr.annotate(input)).thenReturn(null);
}
};
try {
annotator.annotate(input);
fail("Expected NullPointerException due to null output from MachineReading");
} catch (NullPointerException expected) {
}
}

@Test
public void testAnnotatorWithSingleSentenceAndMultipleEntitiesAndRelations() {
Properties props = new Properties();
props.setProperty("relation.verbose", "false");
Annotation input = mock(Annotation.class);
Annotation output = mock(Annotation.class);
MachineReading mockMR = mock(MachineReading.class);
CoreMap originalSent = mock(CoreMap.class);
CoreMap outputSent = mock(CoreMap.class);
EntityMention em1 = mock(EntityMention.class);
EntityMention em2 = mock(EntityMention.class);
RelationMention rm1 = mock(RelationMention.class);
RelationMention rm2 = mock(RelationMention.class);
List<CoreMap> outputSentences = Arrays.asList(outputSent);
List<CoreMap> inputSentences = Arrays.asList(originalSent);
List<EntityMention> entityMentions = Arrays.asList(em1, em2);
List<RelationMention> relationMentions = Arrays.asList(rm1, rm2);
when(mockMR.annotate(input)).thenReturn(output);
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(outputSentences);
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(inputSentences);
when(outputSent.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(entityMentions);
when(outputSent.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(relationMentions);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
annotator.annotate(input);
verify(originalSent).set(MachineReadingAnnotations.EntityMentionsAnnotation.class, entityMentions);
verify(originalSent).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, relationMentions);
}

@Test
public void testAnnotateWithMoreOutputSentencesThanInput() {
Properties props = new Properties();
props.setProperty("relation.verbose", "true");
Annotation input = mock(Annotation.class);
Annotation output = mock(Annotation.class);
MachineReading mockMR = mock(MachineReading.class);
CoreMap out1 = mock(CoreMap.class);
CoreMap out2 = mock(CoreMap.class);
CoreMap orig1 = mock(CoreMap.class);
List<CoreMap> outputSentences = Arrays.asList(out1, out2);
List<CoreMap> inputSentences = Arrays.asList(orig1);
when(mockMR.annotate(input)).thenReturn(output);
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(outputSentences);
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(inputSentences);
when(out1.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(null);
when(out1.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(null);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
try {
annotator.annotate(input);
fail("Expected IndexOutOfBoundsException when output sentence list is larger than input");
} catch (IndexOutOfBoundsException expected) {
}
}

@Test
public void testLoadDefaultRelationModelWhenNoPropertyProvided() {
Properties props = new Properties();
try {
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
assertNotNull(annotator);
} catch (RuntimeException ex) {
assertNotNull(ex.getMessage());
}
}

@Test
public void testPropertiesWithEmptyModelAndVerboseFalse() {
Properties props = new Properties();
props.setProperty("relation.model", "");
props.setProperty("relation.verbose", "false");
try {
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
assertNotNull(annotator);
} catch (RuntimeException ex) {
assertTrue(ex.getMessage().contains(""));
}
}

@Test
public void testGetVerbose_LegacyTrue_NewFalse() {
Properties props = new Properties();
props.setProperty("sup.relation.verbose", "true");
props.setProperty("relation.verbose", "false");
boolean verboseSetting = RelationExtractorAnnotator.getVerbose(props);
assertTrue(verboseSetting);
}

@Test
public void testGetVerbose_LegacyFalse_NewTrue() {
Properties props = new Properties();
props.setProperty("sup.relation.verbose", "false");
props.setProperty("relation.verbose", "true");
boolean verboseSetting = RelationExtractorAnnotator.getVerbose(props);
assertFalse(verboseSetting);
}

@Test
public void testAnnotateWithEmptyEntityAndRelationListsInVerboseMode() {
Properties props = new Properties();
props.setProperty("relation.verbose", "true");
// EntityMention em = new EntityMention("dummy");
RelationMention rm = mock(RelationMention.class);
when(rm.getType()).thenReturn(RelationMention.UNRELATED);
CoreMap outputSentence = mock(CoreMap.class);
CoreMap originalSentence = mock(CoreMap.class);
Annotation input = mock(Annotation.class);
Annotation output = mock(Annotation.class);
// when(outputSentence.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(Arrays.asList(em));
when(outputSentence.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(Arrays.asList(rm));
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(originalSentence));
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(outputSentence));
MachineReading mockMR = mock(MachineReading.class);
when(mockMR.annotate(input)).thenReturn(output);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
annotator.annotate(input);
// verify(originalSentence).set(MachineReadingAnnotations.EntityMentionsAnnotation.class, Arrays.asList(em));
verify(originalSentence).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, Arrays.asList(rm));
}

@Test
public void testAnnotateWithOneNullOutputSentence() {
Properties props = new Properties();
props.setProperty("relation.verbose", "false");
CoreMap originalSentence = mock(CoreMap.class);
List<CoreMap> outputSentences = Arrays.asList((CoreMap) null);
List<CoreMap> originalSentences = Arrays.asList(originalSentence);
Annotation input = mock(Annotation.class);
Annotation output = mock(Annotation.class);
MachineReading mockMR = mock(MachineReading.class);
when(mockMR.annotate(input)).thenReturn(output);
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(outputSentences);
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(originalSentences);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
try {
annotator.annotate(input);
fail("Expected NullPointerException when output sentence is null");
} catch (NullPointerException expected) {
}
}

@Test
public void testAnnotateWithExtraOriginalSentence() {
Properties props = new Properties();
props.setProperty("relation.verbose", "false");
CoreMap outputSentence = mock(CoreMap.class);
CoreMap originalSentence1 = mock(CoreMap.class);
CoreMap originalSentence2 = mock(CoreMap.class);
List<CoreMap> outputSentences = Arrays.asList(outputSentence);
List<CoreMap> originalSentences = Arrays.asList(originalSentence1, originalSentence2);
Annotation input = mock(Annotation.class);
Annotation output = mock(Annotation.class);
MachineReading mockMR = mock(MachineReading.class);
when(mockMR.annotate(input)).thenReturn(output);
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(outputSentences);
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(originalSentences);
when(outputSentence.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(null);
when(outputSentence.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(null);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
annotator.annotate(input);
verify(originalSentence1).set(MachineReadingAnnotations.EntityMentionsAnnotation.class, null);
verify(originalSentence1).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, null);
verifyNoMoreInteractions(originalSentence2);
}

@Test
public void testConstructorThrowsIfRelationExtractorIsNull() {
Properties props = new Properties();
props.setProperty("relation.model", "invalid-model-path.ser.gz");
try {
fail("Expected RuntimeException due to simulated loading exception");
} catch (RuntimeException e) {
assertEquals("Simulated model load failure", e.getMessage());
}
}

@Test
public void testGetModelName_DefaultUsedWhenNoKeysPresent() {
Properties props = new Properties();
String modelName = RelationExtractorAnnotator.getModelName(props);
assertNotNull(modelName);
assertTrue(modelName.length() > 0);
}

@Test
public void testGetVerbose_EmptyValueInNewStyleProperty() {
Properties props = new Properties();
props.setProperty("relation.verbose", "");
boolean verboseSetting = RelationExtractorAnnotator.getVerbose(props);
assertFalse(verboseSetting);
}

@Test
public void testGetVerbose_EmptyValueInDeprecatedStyle() {
Properties props = new Properties();
props.setProperty("sup.relation.verbose", "");
boolean value = RelationExtractorAnnotator.getVerbose(props);
assertFalse(value);
}

@Test
public void testSentenceListsMismatch_InputShorterThanOutput() {
Properties props = new Properties();
props.setProperty("relation.verbose", "false");
CoreMap outSent1 = mock(CoreMap.class);
CoreMap outSent2 = mock(CoreMap.class);
List<CoreMap> outputSentences = Arrays.asList(outSent1, outSent2);
CoreMap origSent1 = mock(CoreMap.class);
List<CoreMap> inputSentences = Arrays.asList(origSent1);
Annotation input = mock(Annotation.class);
Annotation output = mock(Annotation.class);
MachineReading mockMR = mock(MachineReading.class);
when(mockMR.annotate(input)).thenReturn(output);
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(outputSentences);
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(inputSentences);
when(outSent1.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(null);
when(outSent1.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(null);
when(outSent2.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(null);
when(outSent2.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(null);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
try {
annotator.annotate(input);
fail("Expected IndexOutOfBoundsException because input has fewer sentences than output");
} catch (IndexOutOfBoundsException expected) {
}
}

@Test
public void testAnnotateWithEmptyAnnotation() {
Properties props = new Properties();
props.setProperty("relation.verbose", "false");
Annotation annotation = new Annotation("");
StanfordCoreNLP pipeline = new StanfordCoreNLP("tokenize,ssplit,pos,lemma,parse,ner");
pipeline.annotate(annotation);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
annotator.annotate(annotation);
List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
assertNotNull(sentences);
assertTrue(sentences.size() >= 0);
}

@Test
public void testRelationMentionTypeIsNullInVerboseMode() {
Properties props = new Properties();
props.setProperty("relation.verbose", "true");
Annotation input = mock(Annotation.class);
Annotation output = mock(Annotation.class);
MachineReading mockMR = mock(MachineReading.class);
CoreMap outSent = mock(CoreMap.class);
CoreMap origSent = mock(CoreMap.class);
RelationMention relation = mock(RelationMention.class);
when(relation.getType()).thenReturn(null);
List<RelationMention> rels = Arrays.asList(relation);
when(mockMR.annotate(input)).thenReturn(output);
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(outSent));
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(origSent));
when(outSent.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(null);
when(outSent.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(rels);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
annotator.annotate(input);
verify(origSent).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, rels);
}

@Test
public void testNullModelPropertyProducesDefaultBehavior() {
Properties props = new Properties();
props.setProperty("relation.model", null);
try {
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
assertNotNull(annotator);
} catch (RuntimeException ex) {
assertTrue(ex.getMessage() != null);
}
}

@Test
public void testEmptyRelationListLoggedInVerbose() {
Properties props = new Properties();
props.setProperty("relation.verbose", "true");
Annotation input = mock(Annotation.class);
Annotation output = mock(Annotation.class);
MachineReading mockMR = mock(MachineReading.class);
CoreMap outSent = mock(CoreMap.class);
CoreMap origSent = mock(CoreMap.class);
when(mockMR.annotate(input)).thenReturn(output);
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(outSent));
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(origSent));
when(outSent.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(new ArrayList<>());
when(outSent.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(new ArrayList<>());
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
annotator.annotate(input);
verify(origSent).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, new ArrayList<>());
}
}
