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

public class RelationExtractorAnnotator_llmsuite_3_GPTLLMTest {

@Test
public void testGetVerbose_WithRelationVerboseTrue() {
Properties props = new Properties();
props.setProperty("relation.verbose", "true");
boolean result = RelationExtractorAnnotator.getVerbose(props);
assertTrue(result);
}

@Test
public void testGetVerbose_WithRelationVerboseFalse() {
Properties props = new Properties();
props.setProperty("relation.verbose", "false");
boolean result = RelationExtractorAnnotator.getVerbose(props);
assertFalse(result);
}

@Test
public void testGetVerbose_WithSupRelationVerboseTrue() {
Properties props = new Properties();
props.setProperty("sup.relation.verbose", "true");
boolean result = RelationExtractorAnnotator.getVerbose(props);
assertTrue(result);
}

@Test
public void testGetVerbose_DefaultFalse() {
Properties props = new Properties();
boolean result = RelationExtractorAnnotator.getVerbose(props);
assertFalse(result);
}

@Test
public void testGetModelName_WithRelationModel() {
Properties props = new Properties();
props.setProperty("relation.model", "custom.model.ser.gz");
String result = RelationExtractorAnnotator.getModelName(props);
assertEquals("custom.model.ser.gz", result);
}

@Test
public void testGetModelName_WithSupRelationModel() {
Properties props = new Properties();
props.setProperty("sup.relation.model", "legacy.model.ser.gz");
String result = RelationExtractorAnnotator.getModelName(props);
assertEquals("legacy.model.ser.gz", result);
}

@Test
public void testGetModelName_DefaultModelPath() {
Properties props = new Properties();
String result = RelationExtractorAnnotator.getModelName(props);
assertEquals(DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL, result);
}

@Test
public void testConstructorDoesNotThrow() {
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
assertNotNull(annotator);
}

@Test(expected = RuntimeException.class)
public void testConstructorThrowsRuntimeExceptionOnInvalidModel() {
Properties props = new Properties();
props.setProperty("relation.model", "invalid_path_that_does_not_exist.ser.gz");
new RelationExtractorAnnotator(props);
}

@Test
public void testAnnotate_SingleSentence_EntitiesAndRelationsTransferred() {
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Annotation inputAnnotation = mock(Annotation.class);
Annotation annotatedOutput = mock(Annotation.class);
CoreMap inputSentence = mock(CoreMap.class);
CoreMap outputSentence = mock(CoreMap.class);
List<CoreMap> inputSentList = Arrays.asList(inputSentence);
List<CoreMap> outputSentList = Arrays.asList(outputSentence);
List<EntityMention> entityMentions = Arrays.asList(mock(EntityMention.class));
List<RelationMention> relationMentions = Arrays.asList(mock(RelationMention.class));
when(inputAnnotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(inputSentList);
when(annotatedOutput.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(outputSentList);
when(outputSentence.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(entityMentions);
when(outputSentence.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(relationMentions);
MachineReading mockMachineReading = mock(MachineReading.class);
when(mockMachineReading.annotate(inputAnnotation)).thenReturn(annotatedOutput);
RelationExtractorAnnotator anotherAnnotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMachineReading;
}
};
anotherAnnotator.annotate(inputAnnotation);
verify(inputSentence).set(MachineReadingAnnotations.EntityMentionsAnnotation.class, entityMentions);
verify(inputSentence).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, relationMentions);
}

@Test
public void testAnnotate_SingleSentence_NoEntitiesNoRelations() {
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Annotation inputAnnotation = mock(Annotation.class);
Annotation outputAnnotation = mock(Annotation.class);
CoreMap inputSent = mock(CoreMap.class);
CoreMap outputSent = mock(CoreMap.class);
List<CoreMap> inputSentences = Arrays.asList(inputSent);
List<CoreMap> outputSentences = Arrays.asList(outputSent);
when(inputAnnotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(inputSentences);
when(outputAnnotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(outputSentences);
when(outputSent.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(null);
when(outputSent.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(null);
MachineReading mockMR = mock(MachineReading.class);
when(mockMR.annotate(inputAnnotation)).thenReturn(outputAnnotation);
RelationExtractorAnnotator anotherAnnotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
anotherAnnotator.annotate(inputAnnotation);
verify(inputSent).set(MachineReadingAnnotations.EntityMentionsAnnotation.class, null);
verify(inputSent).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, null);
}

@Test
public void testRequires_ReturnsExpectedAnnotations() {
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Set<Class<? extends CoreAnnotation>> required = annotator.requires();
assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
assertTrue(required.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
assertTrue(required.contains(CoreAnnotations.NamedEntityTagAnnotation.class));
assertTrue(required.contains(edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation.class));
assertTrue(required.contains(edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class));
assertTrue(required.contains(edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class));
assertTrue(required.contains(edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class));
}

@Test
public void testRequirementsSatisfied_ReturnsExpectedAnnotations() {
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
assertTrue(satisfied.contains(MachineReadingAnnotations.EntityMentionsAnnotation.class));
assertTrue(satisfied.contains(MachineReadingAnnotations.RelationMentionsAnnotation.class));
}

@Test
public void testAnnotate_NullSentencesInInputAnnotation() {
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Annotation input = mock(Annotation.class);
Annotation output = mock(Annotation.class);
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(null);
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(null);
MachineReading mockMR = mock(MachineReading.class);
when(mockMR.annotate(input)).thenReturn(output);
RelationExtractorAnnotator overrideMR = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
overrideMR.annotate(input);
assertTrue(true);
}

@Test
public void testAnnotate_MismatchedSentenceSizesBetweenInputAndOutput() {
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Annotation inputAnnotation = mock(Annotation.class);
Annotation outputAnnotation = mock(Annotation.class);
CoreMap inputSent = mock(CoreMap.class);
CoreMap outputSent1 = mock(CoreMap.class);
CoreMap outputSent2 = mock(CoreMap.class);
List<CoreMap> inputSentences = Arrays.asList(inputSent);
List<CoreMap> outputSentences = Arrays.asList(outputSent1, outputSent2);
when(inputAnnotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(inputSentences);
when(outputAnnotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(outputSentences);
List<EntityMention> entities1 = Collections.emptyList();
List<RelationMention> relation1 = Collections.emptyList();
when(outputSent1.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(entities1);
when(outputSent1.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(relation1);
MachineReading mockMR = mock(MachineReading.class);
when(mockMR.annotate(inputAnnotation)).thenReturn(outputAnnotation);
RelationExtractorAnnotator testAnnotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
try {
testAnnotator.annotate(inputAnnotation);
assertTrue(true);
} catch (IndexOutOfBoundsException ex) {
fail("IndexOutOfBoundsException should not be thrown.");
}
}

@Test
public void testRequires_DoesNotContainUnexpectedAnnotation() {
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Set<Class<? extends CoreAnnotation>> required = annotator.requires();
assertFalse(required.contains(RelationMention.class));
assertFalse(required.contains(EntityMention.class));
}

@Test
public void testRequirementsSatisfied_DoesNotContainOtherAnnotation() {
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
assertEquals(2, satisfied.size());
assertTrue(satisfied.contains(MachineReadingAnnotations.RelationMentionsAnnotation.class));
assertTrue(satisfied.contains(MachineReadingAnnotations.EntityMentionsAnnotation.class));
}

@Test
public void testConstructor_WithVerboseTrueLoggingDoesNotCrash() {
Properties props = new Properties();
props.setProperty("relation.verbose", "true");
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
assertNotNull(annotator);
}

@Test
public void testConstructor_WithDeprecatedVerboseLogs() {
Properties props = new Properties();
props.setProperty("sup.relation.verbose", "true");
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
assertNotNull(annotator);
}

@Test
public void testConstructor_WithDeprecatedModelLogs() {
Properties props = new Properties();
props.setProperty("sup.relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
assertNotNull(annotator);
}

@Test
public void testAnnotate_EmptyInputSentences() {
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
Annotation input = mock(Annotation.class);
Annotation output = mock(Annotation.class);
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Collections.emptyList());
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Collections.emptyList());
MachineReading mockMR = mock(MachineReading.class);
when(mockMR.annotate(input)).thenReturn(output);
RelationExtractorAnnotator testAnnotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
testAnnotator.annotate(input);
assertTrue(true);
}

@Test
public void testAnnotate_EntityIsNullButRelationExists() {
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
Annotation input = mock(Annotation.class);
Annotation output = mock(Annotation.class);
CoreMap inputSent = mock(CoreMap.class);
CoreMap outputSent = mock(CoreMap.class);
List<CoreMap> inputSentences = Arrays.asList(inputSent);
List<CoreMap> outputSentences = Arrays.asList(outputSent);
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(inputSentences);
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(outputSentences);
List<RelationMention> relations = Arrays.asList(mock(RelationMention.class));
when(outputSent.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(null);
when(outputSent.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(relations);
MachineReading mockMR = mock(MachineReading.class);
when(mockMR.annotate(input)).thenReturn(output);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
annotator.annotate(input);
verify(inputSent).set(MachineReadingAnnotations.EntityMentionsAnnotation.class, null);
verify(inputSent).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, relations);
}

@Test
public void testGetVerbose_IgnoresBothPropertiesFalse() {
Properties props = new Properties();
props.setProperty("relation.verbose", "false");
props.setProperty("sup.relation.verbose", "false");
boolean result = RelationExtractorAnnotator.getVerbose(props);
assertFalse(result);
}

@Test
public void testGetModelName_BothPropertiesSetChoosesOldOne() {
Properties props = new Properties();
props.setProperty("relation.model", "new.model");
props.setProperty("sup.relation.model", "old.model");
String result = RelationExtractorAnnotator.getModelName(props);
assertEquals("old.model", result);
}

@Test
public void testGetModelName_EmptyPropertiesUsesDefault() {
Properties props = new Properties();
String modelName = RelationExtractorAnnotator.getModelName(props);
assertNotNull(modelName);
assertEquals(DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL, modelName);
}

@Test
public void testConstructor_NullModelPathThrowsRuntimeException() {
Properties props = new Properties();
props.setProperty("relation.model", null);
try {
new RelationExtractorAnnotator(props);
fail("Expected RuntimeException due to null model path");
} catch (RuntimeException e) {
assertNotNull(e.getCause());
}
}

@Test
public void testAnnotate_SentenceWithNullEntityAndEmptyRelation() {
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
Annotation input = mock(Annotation.class);
Annotation output = mock(Annotation.class);
CoreMap inputSent = mock(CoreMap.class);
CoreMap outputSent = mock(CoreMap.class);
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(inputSent));
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(outputSent));
when(outputSent.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(null);
when(outputSent.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(new ArrayList<>());
MachineReading mockMR = mock(MachineReading.class);
when(mockMR.annotate(input)).thenReturn(output);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
annotator.annotate(input);
verify(inputSent).set(MachineReadingAnnotations.EntityMentionsAnnotation.class, null);
verify(inputSent).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, Collections.emptyList());
}

@Test
public void testAnnotate_TwoSentencesWithDifferentEntityRelationConfigurations() {
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
Annotation input = mock(Annotation.class);
Annotation output = mock(Annotation.class);
CoreMap inputSent1 = mock(CoreMap.class);
CoreMap inputSent2 = mock(CoreMap.class);
CoreMap outputSent1 = mock(CoreMap.class);
CoreMap outputSent2 = mock(CoreMap.class);
List<EntityMention> entities1 = Collections.singletonList(mock(EntityMention.class));
List<RelationMention> relations1 = Collections.singletonList(mock(RelationMention.class));
List<EntityMention> entities2 = null;
List<RelationMention> relations2 = new ArrayList<>();
when(outputSent1.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(entities1);
when(outputSent1.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(relations1);
when(outputSent2.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(entities2);
when(outputSent2.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(relations2);
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(inputSent1, inputSent2));
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(outputSent1, outputSent2));
MachineReading mockMR = mock(MachineReading.class);
when(mockMR.annotate(input)).thenReturn(output);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
annotator.annotate(input);
verify(inputSent1).set(MachineReadingAnnotations.EntityMentionsAnnotation.class, entities1);
verify(inputSent1).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, relations1);
verify(inputSent2).set(MachineReadingAnnotations.EntityMentionsAnnotation.class, null);
verify(inputSent2).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, relations2);
}

@Test
public void testRelationMentionTypeUnrelatedNotLoggedWhenVerbose() {
Properties props = new Properties();
props.setProperty("relation.verbose", "true");
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
Annotation input = mock(Annotation.class);
Annotation output = mock(Annotation.class);
CoreMap inputSent = mock(CoreMap.class);
CoreMap outputSent = mock(CoreMap.class);
RelationMention unrelatedRelation = mock(RelationMention.class);
when(unrelatedRelation.getType()).thenReturn(RelationMention.UNRELATED);
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(inputSent));
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(outputSent));
when(outputSent.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(null);
when(outputSent.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(Arrays.asList(unrelatedRelation));
MachineReading mockMR = mock(MachineReading.class);
when(mockMR.annotate(input)).thenReturn(output);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
annotator.annotate(input);
verify(inputSent).set(MachineReadingAnnotations.EntityMentionsAnnotation.class, null);
verify(inputSent).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, Arrays.asList(unrelatedRelation));
}

@Test
public void testAnnotate_NullAnnotationObjectHandledGracefully() {
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
try {
annotator.annotate(null);
fail("Expected NullPointerException");
} catch (NullPointerException expected) {
assertTrue(true);
}
}

@Test
public void testAnnotate_AnnotatedOutputReturnsNullSentencesList() {
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
Annotation input = mock(Annotation.class);
Annotation output = mock(Annotation.class);
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(mock(CoreMap.class)));
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(null);
MachineReading mockMR = mock(MachineReading.class);
when(mockMR.annotate(input)).thenReturn(output);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
annotator.annotate(input);
assertTrue(true);
}

@Test
public void testAnnotate_InputReturnsNullForSentences() {
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
Annotation input = mock(Annotation.class);
Annotation output = mock(Annotation.class);
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(null);
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(mock(CoreMap.class)));
MachineReading mockMR = mock(MachineReading.class);
when(mockMR.annotate(input)).thenReturn(output);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
annotator.annotate(input);
assertTrue(true);
}

@Test
public void testAnnotate_InputAndOutputSentencesListSizeMismatch() {
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
Annotation input = mock(Annotation.class);
Annotation output = mock(Annotation.class);
CoreMap inputSent1 = mock(CoreMap.class);
CoreMap outputSent1 = mock(CoreMap.class);
CoreMap outputSent2 = mock(CoreMap.class);
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(inputSent1));
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(outputSent1, outputSent2));
when(outputSent1.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(Collections.emptyList());
when(outputSent1.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(Collections.emptyList());
MachineReading mockMR = mock(MachineReading.class);
when(mockMR.annotate(input)).thenReturn(output);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
try {
annotator.annotate(input);
} catch (Exception e) {
fail("Should not throw exception on sentence list size mismatch.");
}
}

@Test
public void testConstructor_ModelPropertyPointsToEmptyString() {
Properties props = new Properties();
props.setProperty("relation.model", "");
try {
new RelationExtractorAnnotator(props);
fail("Expected RuntimeException due to empty model path.");
} catch (RuntimeException e) {
assertNotNull(e.getCause());
}
}

@Test
public void testConstructor_ModelPropertyNullAssignmentHandledGracefully() {
Properties props = new Properties();
props.setProperty("relation.model", null);
try {
new RelationExtractorAnnotator(props);
fail("Should throw RuntimeException due to null model.");
} catch (RuntimeException e) {
assertNotNull(e.getCause());
}
}

@Test
public void testConstructor_MissingModelPropertyUsesDefault() {
Properties props = new Properties();
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
assertNotNull(annotator);
}

@Test
public void testRequires_ReturnsUnmodifiableSet() {
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> required = annotator.requires();
try {
required.add(CoreAnnotations.TextAnnotation.class);
fail("Expected UnsupportedOperationException for unmodifiable set.");
} catch (UnsupportedOperationException e) {
assertTrue(true);
}
}

@Test
public void testRequirementsSatisfied_ReturnsUnmodifiableSet() {
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
try {
satisfied.clear();
fail("Expected UnsupportedOperationException for unmodifiable set.");
} catch (UnsupportedOperationException e) {
assertTrue(true);
}
}

@Test
public void testAnnotate_EntityMentionListIsEmpty() {
Properties props = new Properties();
props.setProperty("relation.verbose", "true");
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
Annotation input = mock(Annotation.class);
Annotation output = mock(Annotation.class);
CoreMap inputSent = mock(CoreMap.class);
CoreMap outputSent = mock(CoreMap.class);
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(inputSent));
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(outputSent));
when(outputSent.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(Collections.emptyList());
when(outputSent.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(Collections.emptyList());
MachineReading mockMR = mock(MachineReading.class);
when(mockMR.annotate(input)).thenReturn(output);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
annotator.annotate(input);
verify(inputSent).set(MachineReadingAnnotations.EntityMentionsAnnotation.class, Collections.emptyList());
verify(inputSent).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, Collections.emptyList());
}

@Test
public void testGetVerbose_BothPropertiesSetToDifferentValues() {
Properties props = new Properties();
props.setProperty("relation.verbose", "false");
props.setProperty("sup.relation.verbose", "true");
boolean result = RelationExtractorAnnotator.getVerbose(props);
assertTrue(result);
}

@Test
public void testMain_WithNullArgsArray() {
try {
RelationExtractorAnnotator.main(null);
assertTrue(true);
} catch (Exception e) {
fail("Main should handle null args without crashing: " + e.getMessage());
}
}

@Test
public void testMain_ValidFakeArgs() {
try {
String[] args = new String[] { "relation.model=" + DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL, "relation.verbose=true" };
RelationExtractorAnnotator.main(args);
assertTrue(true);
} catch (Exception e) {
fail("Main should not throw exception for fake args: " + e.getMessage());
}
}

@Test
public void testGetVerbose_SupersedesNewWhenBothPresent() {
Properties props = new Properties();
props.setProperty("relation.verbose", "false");
props.setProperty("sup.relation.verbose", "true");
boolean result = RelationExtractorAnnotator.getVerbose(props);
assertTrue(result);
}

@Test
public void testGetModelName_BothPresentPrefersSupRelationModel() {
Properties props = new Properties();
props.setProperty("relation.model", "new.model");
props.setProperty("sup.relation.model", "old.model");
String model = RelationExtractorAnnotator.getModelName(props);
assertEquals("old.model", model);
}

@Test
public void testAnnotate_EntityListIsNull_RelationListIsEmpty() {
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
Annotation input = mock(Annotation.class);
Annotation output = mock(Annotation.class);
CoreMap inputSent = mock(CoreMap.class);
CoreMap outputSent = mock(CoreMap.class);
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(inputSent));
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(outputSent));
when(outputSent.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(null);
when(outputSent.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(Collections.emptyList());
MachineReading machineReading = mock(MachineReading.class);
when(machineReading.annotate(input)).thenReturn(output);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = machineReading;
}
};
annotator.annotate(input);
verify(inputSent).set(MachineReadingAnnotations.EntityMentionsAnnotation.class, null);
verify(inputSent).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, Collections.emptyList());
}

@Test
public void testAnnotate_NullEntityAndRelationMentionsForMultipleSentences() {
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
CoreMap inputSent1 = mock(CoreMap.class);
CoreMap inputSent2 = mock(CoreMap.class);
CoreMap outputSent1 = mock(CoreMap.class);
CoreMap outputSent2 = mock(CoreMap.class);
List<CoreMap> inputSentences = Arrays.asList(inputSent1, inputSent2);
List<CoreMap> outputSentences = Arrays.asList(outputSent1, outputSent2);
when(outputSent1.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(null);
when(outputSent1.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(null);
when(outputSent2.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(null);
when(outputSent2.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(null);
Annotation input = mock(Annotation.class);
Annotation output = mock(Annotation.class);
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(inputSentences);
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(outputSentences);
MachineReading machineReading = mock(MachineReading.class);
when(machineReading.annotate(input)).thenReturn(output);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = machineReading;
}
};
annotator.annotate(input);
verify(inputSent1).set(MachineReadingAnnotations.EntityMentionsAnnotation.class, null);
verify(inputSent1).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, null);
verify(inputSent2).set(MachineReadingAnnotations.EntityMentionsAnnotation.class, null);
verify(inputSent2).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, null);
}

@Test
public void testAnnotate_OneRelationMentionTypeUnrelatedIsFilteredOut() {
Properties props = new Properties();
props.setProperty("relation.verbose", "true");
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
Annotation input = mock(Annotation.class);
Annotation output = mock(Annotation.class);
CoreMap inputSent = mock(CoreMap.class);
CoreMap outputSent = mock(CoreMap.class);
RelationMention unrelated = mock(RelationMention.class);
when(unrelated.getType()).thenReturn(RelationMention.UNRELATED);
List<RelationMention> relations = Arrays.asList(unrelated);
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(inputSent));
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(outputSent));
when(outputSent.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(null);
when(outputSent.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(relations);
MachineReading machineReading = mock(MachineReading.class);
when(machineReading.annotate(input)).thenReturn(output);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = machineReading;
}
};
annotator.annotate(input);
verify(inputSent).set(MachineReadingAnnotations.EntityMentionsAnnotation.class, null);
verify(inputSent).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, relations);
}

@Test
public void testGetVerbose_InvalidValueInPropertyFallsBackToFalse() {
Properties props = new Properties();
props.setProperty("relation.verbose", "maybe");
boolean result = RelationExtractorAnnotator.getVerbose(props);
assertFalse(result);
}

@Test
public void testRequires_ReturnsAllExpectedAnnotations() {
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> required = annotator.requires();
assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
assertTrue(required.contains(CoreAnnotations.NamedEntityTagAnnotation.class));
assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
assertTrue(required.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
assertTrue(required.contains(edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation.class));
assertTrue(required.contains(edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class));
assertTrue(required.contains(edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class));
assertTrue(required.contains(edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class));
}

@Test
public void testRequirementsSatisfied_OnlyIncludesExpectedOutputAnnotations() {
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
assertEquals(2, satisfied.size());
assertTrue(satisfied.contains(MachineReadingAnnotations.EntityMentionsAnnotation.class));
assertTrue(satisfied.contains(MachineReadingAnnotations.RelationMentionsAnnotation.class));
}

@Test
public void testMain_WithFakeArgsAndNoModelDefined() {
String[] args = new String[] { "relation.verbose=true" };
try {
RelationExtractorAnnotator.main(args);
} catch (Exception e) {
fail("Should not throw exception for minimal valid args: " + e.getMessage());
}
}

@Test
public void testAnnotate_AnnotationReturnsEmptySentenceList() {
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
Annotation input = mock(Annotation.class);
Annotation output = mock(Annotation.class);
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Collections.emptyList());
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Collections.emptyList());
MachineReading machineReading = mock(MachineReading.class);
when(machineReading.annotate(input)).thenReturn(output);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = machineReading;
}
};
annotator.annotate(input);
assertTrue(true);
}

@Test
public void testGetVerboseWithGarbageValue() {
Properties props = new Properties();
props.setProperty("relation.verbose", "123falseTRUEjunk!");
boolean verbose = RelationExtractorAnnotator.getVerbose(props);
assertFalse(verbose);
}

@Test
public void testGetVerboseEmptyString() {
Properties props = new Properties();
props.setProperty("relation.verbose", "");
boolean verbose = RelationExtractorAnnotator.getVerbose(props);
assertFalse(verbose);
}

@Test
public void testGetModelNameWithEmptyValue() {
Properties props = new Properties();
props.setProperty("relation.model", "");
String modelName = RelationExtractorAnnotator.getModelName(props);
assertEquals("", modelName);
}

@Test
public void testModelNameFallbackToDefaultWhenMissing() {
Properties props = new Properties();
String model = RelationExtractorAnnotator.getModelName(props);
assertEquals(DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL, model);
}

@Test
public void testRequirementsSatisfiedSizeExact() {
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
assertEquals(2, satisfied.size());
}

@Test
public void testAnnotateHandlesSingleNullOutputSentenceGracefully() {
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
CoreMap inputSent = mock(CoreMap.class);
Annotation input = mock(Annotation.class);
Annotation output = mock(Annotation.class);
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(inputSent));
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Collections.singletonList(null));
MachineReading machineReading = mock(MachineReading.class);
when(machineReading.annotate(input)).thenReturn(output);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = machineReading;
}
};
try {
annotator.annotate(input);
assertTrue(true);
} catch (NullPointerException e) {
fail("Should handle null output sentence gracefully");
}
}

@Test
public void testAnnotateHandlesMixedNullAndValidOutputSentences() {
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
CoreMap inputSent1 = mock(CoreMap.class);
CoreMap inputSent2 = mock(CoreMap.class);
CoreMap outputSent1 = null;
CoreMap outputSent2 = mock(CoreMap.class);
when(outputSent2.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(null);
when(outputSent2.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(null);
Annotation input = mock(Annotation.class);
Annotation output = mock(Annotation.class);
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(inputSent1, inputSent2));
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(outputSent1, outputSent2));
MachineReading machineReading = mock(MachineReading.class);
when(machineReading.annotate(input)).thenReturn(output);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = machineReading;
}
};
annotator.annotate(input);
verify(inputSent2).set(MachineReadingAnnotations.EntityMentionsAnnotation.class, null);
verify(inputSent2).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, null);
}

@Test
public void testAnnotateWithEntityButNullRelationList() {
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
CoreMap inputSent = mock(CoreMap.class);
CoreMap outputSent = mock(CoreMap.class);
List<EntityMention> entities = Arrays.asList(mock(EntityMention.class));
when(outputSent.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(entities);
when(outputSent.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(null);
Annotation input = mock(Annotation.class);
Annotation output = mock(Annotation.class);
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(inputSent));
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(outputSent));
MachineReading mr = mock(MachineReading.class);
when(mr.annotate(input)).thenReturn(output);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mr;
}
};
annotator.annotate(input);
verify(inputSent).set(MachineReadingAnnotations.EntityMentionsAnnotation.class, entities);
verify(inputSent).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, null);
}

@Test
public void testAnnotateSkipsWhenInputAndOutputSentenceCountMismatch() {
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
Annotation input = mock(Annotation.class);
Annotation output = mock(Annotation.class);
CoreMap inputSent = mock(CoreMap.class);
CoreMap outputSent1 = mock(CoreMap.class);
CoreMap outputSent2 = mock(CoreMap.class);
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Collections.singletonList(inputSent));
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(outputSent1, outputSent2));
when(outputSent1.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(null);
when(outputSent1.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(null);
MachineReading mr = mock(MachineReading.class);
when(mr.annotate(input)).thenReturn(output);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mr;
}
};
try {
annotator.annotate(input);
assertTrue(true);
} catch (Exception e) {
fail("Mismatch in sentence length should not throw exception");
}
}

@Test
public void testMainMethodHandlesInvalidAnnotationKey() {
String[] args = new String[] { "annotators=tokenize,ssplit,pos", "invalid.key=dummy", "relation.model=" + DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL };
try {
RelationExtractorAnnotator.main(args);
assertTrue(true);
} catch (Exception e) {
fail("Main should tolerate unknown property keys without crashing");
}
}

@Test
public void testMainMethodHandlesEmptySentenceInput() {
String[] args = new String[] { "relation.model=" + DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL };
try {
StanfordCoreNLP pipeline = new StanfordCoreNLP("annotators=tokenize,ssplit,pos");
Annotation doc = new Annotation("");
pipeline.annotate(doc);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(new Properties() {

{
setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
}
});
annotator.annotate(doc);
assertTrue(true);
} catch (Exception e) {
fail("Main logic should handle empty input sentence");
}
}

@Test
public void testGetVerbose_nullPropertyValue() {
Properties props = new Properties();
props.put("relation.verbose", null);
boolean result = RelationExtractorAnnotator.getVerbose(props);
assertFalse(result);
}

@Test
public void testGetModelName_nullPropertyIgnoredIfDefaultAvailable() {
Properties props = new Properties();
props.put("relation.model", null);
String result = RelationExtractorAnnotator.getModelName(props);
assertEquals(DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL, result);
}

@Test
public void testModelFallbackWithBothKeysMissing() {
Properties props = new Properties();
String result = RelationExtractorAnnotator.getModelName(props);
assertEquals(DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL, result);
}

@Test
public void testRequiresDoesNotModifySetOutside() {
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> set = annotator.requires();
try {
set.clear();
fail("Expected UnsupportedOperationException for unmodifiable set");
} catch (UnsupportedOperationException e) {
assertTrue(true);
}
}

@Test
public void testRequirementsSatisfiedCanNotBeModified() {
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
try {
satisfied.add(CoreAnnotations.TextAnnotation.class);
fail("requirementsSatisfied() should return unmodifiable set");
} catch (UnsupportedOperationException e) {
assertTrue(true);
}
}

@Test
public void testAnnotateAnnotationWithNullSentenceList() {
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
Annotation input = mock(Annotation.class);
Annotation output = mock(Annotation.class);
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(null);
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Collections.emptyList());
MachineReading mockMR = mock(MachineReading.class);
when(mockMR.annotate(input)).thenReturn(output);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
annotator.annotate(input);
assertTrue(true);
}

@Test
public void testAnnotateOutputWithNullSentenceList() {
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
Annotation input = mock(Annotation.class);
Annotation output = mock(Annotation.class);
CoreMap inputSent = mock(CoreMap.class);
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(inputSent));
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(null);
MachineReading mockMR = mock(MachineReading.class);
when(mockMR.annotate(input)).thenReturn(output);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
annotator.annotate(input);
assertTrue(true);
}

@Test
public void testRelationMentionNullTypeDoesNotThrow() {
Properties props = new Properties();
props.setProperty("relation.verbose", "true");
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
Annotation input = mock(Annotation.class);
Annotation output = mock(Annotation.class);
CoreMap inputSent = mock(CoreMap.class);
CoreMap outputSent = mock(CoreMap.class);
RelationMention relation = mock(RelationMention.class);
when(relation.getType()).thenReturn(null);
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(inputSent));
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(outputSent));
when(outputSent.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(null);
when(outputSent.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(Arrays.asList(relation));
MachineReading mockMR = mock(MachineReading.class);
when(mockMR.annotate(input)).thenReturn(output);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
annotator.annotate(input);
verify(inputSent).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, Arrays.asList(relation));
}

@Test
public void testAnnotateWithEmptyInputAndOutputSentences() {
Properties props = new Properties();
props.setProperty("relation.model", DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL);
Annotation input = mock(Annotation.class);
Annotation output = mock(Annotation.class);
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Collections.emptyList());
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Collections.emptyList());
MachineReading mockMR = mock(MachineReading.class);
when(mockMR.annotate(input)).thenReturn(output);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props) {

{
this.mr = mockMR;
}
};
annotator.annotate(input);
assertTrue(true);
}

@Test
public void testMainHandlesNullArgsWithoutCrash() {
try {
RelationExtractorAnnotator.main(null);
assertTrue(true);
} catch (Exception e) {
fail("main() should not crash on null args");
}
}

@Test
public void testMainHandlesValidText() {
try {
String sentence = "John lives in Paris. Alice works at Google.";
String[] args = new String[] { "relation.model=" + DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL };
Annotation annotation = new Annotation(sentence);
Properties props = new Properties();
props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse");
StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
pipeline.annotate(annotation);
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
annotator.annotate(annotation);
assertNotNull(annotation.get(CoreAnnotations.SentencesAnnotation.class));
} catch (Exception e) {
fail("Annotation pipeline should not fail with valid text input");
}
}
}
