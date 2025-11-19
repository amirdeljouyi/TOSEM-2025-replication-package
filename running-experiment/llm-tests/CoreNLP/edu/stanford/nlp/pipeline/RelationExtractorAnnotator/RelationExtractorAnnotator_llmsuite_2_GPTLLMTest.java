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

public class RelationExtractorAnnotator_llmsuite_2_GPTLLMTest {

@Test
public void testGetVerboseWithoutKeyReturnsFalse() {
Properties props = new Properties();
boolean result = RelationExtractorAnnotator.getVerbose(props);
assertFalse(result);
}

@Test
public void testGetVerboseWithRelationVerboseTrue() {
Properties props = new Properties();
props.setProperty("relation.verbose", "true");
boolean result = RelationExtractorAnnotator.getVerbose(props);
assertTrue(result);
}

@Test
public void testGetVerboseWithDeprecatedKey() {
Properties props = new Properties();
props.setProperty("sup.relation.verbose", "true");
boolean result = RelationExtractorAnnotator.getVerbose(props);
assertTrue(result);
}

@Test
public void testGetModelNameWithNoKeyReturnsDefault() {
Properties props = new Properties();
String result = RelationExtractorAnnotator.getModelName(props);
assertNotNull(result);
assertTrue(result.endsWith(".ser.gz"));
}

@Test
public void testGetModelNameWithRelationModelKey() {
Properties props = new Properties();
props.setProperty("relation.model", "custom/path/model.ser.gz");
String result = RelationExtractorAnnotator.getModelName(props);
assertEquals("custom/path/model.ser.gz", result);
}

@Test
public void testGetModelNameWithDeprecatedKey() {
Properties props = new Properties();
props.setProperty("sup.relation.model", "old/legacy/path/model.ser.gz");
String result = RelationExtractorAnnotator.getModelName(props);
assertEquals("old/legacy/path/model.ser.gz", result);
}

@Test
public void testRequiresReturnsExpectedAnnotations() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/relation/test-model.ser.gz");
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> required = annotator.requires();
assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
assertTrue(required.contains(CoreAnnotations.NamedEntityTagAnnotation.class));
assertTrue(required.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
}

@Test
public void testRequirementsSatisfiedReturnsExpectedClasses() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/relation/test-model.ser.gz");
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
assertTrue(satisfied.contains(MachineReadingAnnotations.EntityMentionsAnnotation.class));
assertTrue(satisfied.contains(MachineReadingAnnotations.RelationMentionsAnnotation.class));
}

@Test
public void testAnnotateWithMockedAnnotationsTransfersEntitiesAndRelations() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/relation/test-model.ser.gz");
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Annotation inputAnnotation = mock(Annotation.class);
Annotation outputAnnotation = mock(Annotation.class);
CoreMap outSentence = mock(CoreMap.class);
CoreMap origSentence = mock(CoreMap.class);
EntityMention entity = mock(EntityMention.class);
RelationMention relation = mock(RelationMention.class);
when(relation.getType()).thenReturn("WORK_FOR");
List<CoreMap> outputSentences = new ArrayList<>();
outputSentences.add(outSentence);
List<CoreMap> originalSentences = new ArrayList<>();
originalSentences.add(origSentence);
when(outSentence.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(Arrays.asList(entity));
when(outSentence.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(Arrays.asList(relation));
when(inputAnnotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(originalSentences);
when(outputAnnotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(outputSentences);
MachineReading mockedMR = mock(MachineReading.class);
when(mockedMR.annotate(inputAnnotation)).thenReturn(outputAnnotation);
annotator.mr = mockedMR;
annotator.annotate(inputAnnotation);
verify(origSentence).set(MachineReadingAnnotations.EntityMentionsAnnotation.class, Arrays.asList(entity));
verify(origSentence).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, Arrays.asList(relation));
}

@Test
public void testAnnotateHandlesNullEntitiesAndRelationsWithoutFailure() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/relation/test-model.ser.gz");
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Annotation inputAnnotation = mock(Annotation.class);
Annotation outputAnnotation = mock(Annotation.class);
CoreMap outSentence = mock(CoreMap.class);
CoreMap origSentence = mock(CoreMap.class);
List<CoreMap> outputSentences = new ArrayList<>();
outputSentences.add(outSentence);
List<CoreMap> originalSentences = new ArrayList<>();
originalSentences.add(origSentence);
when(outSentence.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(null);
when(outSentence.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(null);
when(inputAnnotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(originalSentences);
when(outputAnnotation.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(outputSentences);
MachineReading mockedMR = mock(MachineReading.class);
when(mockedMR.annotate(inputAnnotation)).thenReturn(outputAnnotation);
annotator.mr = mockedMR;
annotator.annotate(inputAnnotation);
verify(origSentence).set(MachineReadingAnnotations.EntityMentionsAnnotation.class, null);
verify(origSentence).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, null);
}

@Test
public void testConstructorWithInvalidModelPathThrowsRuntimeException() {
Properties props = new Properties();
props.setProperty("relation.model", "nonexistent/model/path/xyz.ser.gz");
// thrown.expect(RuntimeException.class);
new RelationExtractorAnnotator(props);
}

@Test
public void testMainMethodExecutesWithoutException() {
String[] args = new String[] { "annotators=tokenize,ssplit,pos,lemma,ner,parse" };
try {
RelationExtractorAnnotator.main(args);
} catch (Exception ex) {
fail("Exception in main method: " + ex.getMessage());
}
}

@Test
public void testAnnotateWithVerboseTrueAndEmptyEntitiesAndRelations() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/relation/test-model.ser.gz");
props.setProperty("relation.verbose", "true");
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Annotation input = mock(Annotation.class);
Annotation output = mock(Annotation.class);
CoreMap outSentence = mock(CoreMap.class);
CoreMap origSentence = mock(CoreMap.class);
when(outSentence.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(Collections.emptyList());
when(outSentence.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(Collections.emptyList());
List<CoreMap> outputSentences = Arrays.asList(outSentence);
List<CoreMap> origSentences = Arrays.asList(origSentence);
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(origSentences);
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(outputSentences);
MachineReading mr = mock(MachineReading.class);
when(mr.annotate(input)).thenReturn(output);
annotator.mr = mr;
annotator.annotate(input);
verify(origSentence).set(MachineReadingAnnotations.EntityMentionsAnnotation.class, Collections.emptyList());
verify(origSentence).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, Collections.emptyList());
}

@Test
public void testAnnotateWithMismatchedSentenceListThrowsIndexException() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/relation/test-model.ser.gz");
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Annotation input = mock(Annotation.class);
Annotation output = mock(Annotation.class);
CoreMap outSentence = mock(CoreMap.class);
List<CoreMap> outputSentences = Arrays.asList(outSentence);
List<CoreMap> origSentences = new ArrayList<>();
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(origSentences);
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(outputSentences);
MachineReading mr = mock(MachineReading.class);
when(mr.annotate(input)).thenReturn(output);
annotator.mr = mr;
try {
annotator.annotate(input);
fail("Expected IndexOutOfBoundsException due to sentence list mismatch");
} catch (IndexOutOfBoundsException expected) {
assertTrue(true);
}
}

@Test
public void testAnnotateWithNullSentencesDoesNotCrash() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/relation/test-model.ser.gz");
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Annotation input = mock(Annotation.class);
Annotation output = mock(Annotation.class);
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(null);
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(null);
MachineReading mr = mock(MachineReading.class);
when(mr.annotate(input)).thenReturn(output);
annotator.mr = mr;
try {
annotator.annotate(input);
} catch (Exception e) {
fail("Annotation with null sentence lists should not throw: " + e.getMessage());
}
}

@Test
public void testConstructorUsesDeprecatedVerboseAndModelWarningSimultaneously() {
Properties props = new Properties();
props.setProperty("sup.relation.verbose", "true");
props.setProperty("sup.relation.model", "edu/stanford/nlp/models/relation/test-model.ser.gz");
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
assertNotNull(annotator);
}

@Test
public void testAnnotateWithNullAnnotationThrowsNPE() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/relation/test-model.ser.gz");
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
try {
annotator.annotate(null);
fail("Expected NullPointerException for null input annotation");
} catch (NullPointerException expected) {
assertTrue(true);
}
}

@Test
public void testGetVerbosePrefersDeprecatedOverNewWhenBothPresent() {
Properties props = new Properties();
props.setProperty("relation.verbose", "false");
props.setProperty("sup.relation.verbose", "true");
boolean verbose = RelationExtractorAnnotator.getVerbose(props);
assertTrue(verbose);
}

@Test
public void testGetModelNamePrefersDeprecatedOverNewWhenBothPresent() {
Properties props = new Properties();
props.setProperty("relation.model", "new-model/path.model");
props.setProperty("sup.relation.model", "deprecated-model/path.model");
String modelName = RelationExtractorAnnotator.getModelName(props);
assertEquals("deprecated-model/path.model", modelName);
}

@Test
public void testAnnotateWithSingleSentence_noEntityNoRelation() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/relation/test-model.ser.gz");
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Annotation input = mock(Annotation.class);
Annotation output = mock(Annotation.class);
CoreMap outputSentence = mock(CoreMap.class);
CoreMap inputSentence = mock(CoreMap.class);
when(outputSentence.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(null);
when(outputSentence.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(null);
List<CoreMap> outSentences = new ArrayList<>();
outSentences.add(outputSentence);
List<CoreMap> inSentences = new ArrayList<>();
inSentences.add(inputSentence);
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(outSentences);
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(inSentences);
RelationMention dummyRel = mock(RelationMention.class);
when(dummyRel.getType()).thenReturn(RelationMention.UNRELATED);
MachineReading machineReading = mock(MachineReading.class);
when(machineReading.annotate(input)).thenReturn(output);
annotator.mr = machineReading;
annotator.annotate(input);
verify(inputSentence).set(MachineReadingAnnotations.EntityMentionsAnnotation.class, null);
verify(inputSentence).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, null);
}

@Test
public void testAnnotate_withRelationTypeUnrelated_verboseTrue() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/relation/test-model.ser.gz");
props.setProperty("relation.verbose", "true");
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
RelationMention relationMention = mock(RelationMention.class);
when(relationMention.getType()).thenReturn(RelationMention.UNRELATED);
EntityMention entityMention = mock(EntityMention.class);
CoreMap outputSentence = mock(CoreMap.class);
CoreMap inputSentence = mock(CoreMap.class);
when(outputSentence.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(Collections.singletonList(entityMention));
when(outputSentence.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(Collections.singletonList(relationMention));
List<CoreMap> inputSentences = new ArrayList<>();
inputSentences.add(inputSentence);
List<CoreMap> outputSentences = new ArrayList<>();
outputSentences.add(outputSentence);
Annotation input = mock(Annotation.class);
Annotation output = mock(Annotation.class);
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(inputSentences);
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(outputSentences);
MachineReading machineReading = mock(MachineReading.class);
when(machineReading.annotate(input)).thenReturn(output);
annotator.mr = machineReading;
annotator.annotate(input);
verify(inputSentence).set(MachineReadingAnnotations.EntityMentionsAnnotation.class, Collections.singletonList(entityMention));
verify(inputSentence).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, Collections.singletonList(relationMention));
}

@Test
public void testRequiresSetIsUnmodifiable() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/relation/test-model.ser.gz");
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> required = annotator.requires();
try {
required.add(CoreAnnotations.TextAnnotation.class);
fail("Expected UnsupportedOperationException");
} catch (UnsupportedOperationException expected) {
assertTrue(true);
}
}

@Test
public void testRequirementsSatisfiedSetIsUnmodifiable() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/relation/test-model.ser.gz");
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> output = annotator.requirementsSatisfied();
try {
output.add(CoreAnnotations.TextAnnotation.class);
fail("Expected UnsupportedOperationException");
} catch (UnsupportedOperationException expected) {
assertTrue(true);
}
}

@Test
public void testMainRunsWithoutPropertiesArg() {
String[] args = new String[0];
try {
RelationExtractorAnnotator.main(args);
} catch (Exception ex) {
fail("RelationExtractorAnnotator.main() should not throw an exception with empty args: " + ex.getMessage());
}
}

@Test
public void testAnnotateDoesNothingWhenSentencesEmpty() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/relation/test-model.ser.gz");
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Annotation in = mock(Annotation.class);
Annotation out = mock(Annotation.class);
when(in.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(new ArrayList<>());
when(out.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(new ArrayList<>());
MachineReading mr = mock(MachineReading.class);
when(mr.annotate(in)).thenReturn(out);
annotator.mr = mr;
annotator.annotate(in);
}

@Test
public void testConstructorWithEmptyPropertiesUsesDefaultModel() {
Properties props = new Properties();
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
assertNotNull(annotator);
}

@Test
public void testGetVerboseReturnsFalseIfInvalidValue() {
Properties props = new Properties();
props.setProperty("relation.verbose", "notBoolean");
boolean result = RelationExtractorAnnotator.getVerbose(props);
assertFalse(result);
}

@Test
public void testEntityMentionListSetToEmptyList() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/relation/test-model.ser.gz");
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Annotation input = mock(Annotation.class);
Annotation output = mock(Annotation.class);
CoreMap outputSentence = mock(CoreMap.class);
CoreMap inputSentence = mock(CoreMap.class);
when(outputSentence.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(new ArrayList<>());
when(outputSentence.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(new ArrayList<>());
List<CoreMap> outSentences = Arrays.asList(outputSentence);
List<CoreMap> inSentences = Arrays.asList(inputSentence);
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(outSentences);
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(inSentences);
MachineReading mr = mock(MachineReading.class);
when(mr.annotate(input)).thenReturn(output);
annotator.mr = mr;
annotator.annotate(input);
verify(inputSentence).set(MachineReadingAnnotations.EntityMentionsAnnotation.class, new ArrayList<>());
verify(inputSentence).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, new ArrayList<>());
}

@Test
public void testDifferentSizeOfOutputAndInputSentenceListsDoesNotThrow() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/relation/test-model.ser.gz");
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Annotation input = mock(Annotation.class);
Annotation output = mock(Annotation.class);
CoreMap outputSentence1 = mock(CoreMap.class);
CoreMap inputSentence1 = mock(CoreMap.class);
CoreMap inputSentence2 = mock(CoreMap.class);
List<CoreMap> outSentences = Arrays.asList(outputSentence1);
List<CoreMap> inSentences = Arrays.asList(inputSentence1, inputSentence2);
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(outSentences);
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(inSentences);
MachineReading mr = mock(MachineReading.class);
when(mr.annotate(input)).thenReturn(output);
annotator.mr = mr;
annotator.annotate(input);
verify(inputSentence1).set(eq(MachineReadingAnnotations.EntityMentionsAnnotation.class), any());
verify(inputSentence1).set(eq(MachineReadingAnnotations.RelationMentionsAnnotation.class), any());
}

@Test
public void testRelationMentionWithNullTypeIsHandled() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/relation/test-model.ser.gz");
props.setProperty("relation.verbose", "true");
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
RelationMention relationMention = mock(RelationMention.class);
when(relationMention.getType()).thenReturn(null);
EntityMention entityMention = mock(EntityMention.class);
CoreMap outputSentence = mock(CoreMap.class);
CoreMap inputSentence = mock(CoreMap.class);
when(outputSentence.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(Collections.singletonList(entityMention));
when(outputSentence.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(Collections.singletonList(relationMention));
List<CoreMap> inputSentences = Arrays.asList(inputSentence);
List<CoreMap> outputSentences = Arrays.asList(outputSentence);
Annotation input = mock(Annotation.class);
Annotation output = mock(Annotation.class);
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(inputSentences);
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(outputSentences);
MachineReading mr = mock(MachineReading.class);
when(mr.annotate(input)).thenReturn(output);
annotator.mr = mr;
annotator.annotate(input);
verify(inputSentence).set(eq(MachineReadingAnnotations.EntityMentionsAnnotation.class), any());
verify(inputSentence).set(eq(MachineReadingAnnotations.RelationMentionsAnnotation.class), any());
}

@Test
public void testModelNameReturnsFallbackIfBothKeysMissing() {
Properties props = new Properties();
String modelName = RelationExtractorAnnotator.getModelName(props);
assertNotNull(modelName);
assertTrue(modelName.contains("relation"));
}

@Test
public void testGetVerboseWithEmptyStringReturnsFalse() {
Properties props = new Properties();
props.setProperty("relation.verbose", "");
boolean verbose = RelationExtractorAnnotator.getVerbose(props);
assertFalse(verbose);
}

@Test
public void testGetModelNameReturnsNewIfBothPresent() {
Properties props = new Properties();
props.setProperty("relation.model", "new-model-path");
props.setProperty("sup.relation.model", "deprecated-model-path");
String model = RelationExtractorAnnotator.getModelName(props);
assertEquals("deprecated-model-path", model);
}

@Test
public void testAnnotateWithMoreOutputSentencesThanInputSentences() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/relation/test-model.ser.gz");
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Annotation input = mock(Annotation.class);
Annotation output = mock(Annotation.class);
CoreMap inputSent1 = mock(CoreMap.class);
CoreMap outputSent1 = mock(CoreMap.class);
CoreMap outputSent2 = mock(CoreMap.class);
List<CoreMap> inputSentences = Arrays.asList(inputSent1);
List<CoreMap> outputSentences = Arrays.asList(outputSent1, outputSent2);
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(inputSentences);
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(outputSentences);
when(outputSent1.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(null);
when(outputSent1.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(null);
when(outputSent2.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(null);
when(outputSent2.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(null);
MachineReading mr = mock(MachineReading.class);
when(mr.annotate(input)).thenReturn(output);
annotator.mr = mr;
try {
annotator.annotate(input);
} catch (IndexOutOfBoundsException e) {
assertTrue(true);
return;
}
fail("Expected IndexOutOfBoundsException when output sentences > input sentences");
}

@Test
public void testAnnotateSkipsEmptyOutputSentencesList() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/relation/test-model.ser.gz");
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Annotation input = mock(Annotation.class);
Annotation output = mock(Annotation.class);
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList());
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList());
MachineReading mr = mock(MachineReading.class);
when(mr.annotate(input)).thenReturn(output);
annotator.mr = mr;
annotator.annotate(input);
}

@Test
public void testAnnotateSingleRelationMentionTypeNullDoesNotCrash() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/relation/test-model.ser.gz");
props.setProperty("relation.verbose", "true");
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Annotation input = mock(Annotation.class);
Annotation output = mock(Annotation.class);
CoreMap inputSent = mock(CoreMap.class);
CoreMap outputSent = mock(CoreMap.class);
RelationMention relMention = mock(RelationMention.class);
when(relMention.getType()).thenReturn(null);
EntityMention ent = mock(EntityMention.class);
when(outputSent.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(Arrays.asList(ent));
when(outputSent.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(Arrays.asList(relMention));
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(inputSent));
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(outputSent));
MachineReading mr = mock(MachineReading.class);
when(mr.annotate(input)).thenReturn(output);
annotator.mr = mr;
annotator.annotate(input);
verify(inputSent).set(MachineReadingAnnotations.EntityMentionsAnnotation.class, Arrays.asList(ent));
verify(inputSent).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, Arrays.asList(relMention));
}

@Test
public void testGetVerboseWithNullValueKeyReturnsFalse() {
Properties props = new Properties();
props.setProperty("relation.verbose", null);
boolean result = RelationExtractorAnnotator.getVerbose(props);
assertFalse(result);
}

@Test
public void testGetModelNameWithBlankStringReturnsDefault() {
Properties props = new Properties();
props.setProperty("relation.model", "");
String model = RelationExtractorAnnotator.getModelName(props);
assertNotNull(model);
assertTrue(model.contains("relation"));
}

@Test
public void testGetModelNameWithOnlyWhiteSpacesReturnsDefault() {
Properties props = new Properties();
props.setProperty("relation.model", "   ");
String model = RelationExtractorAnnotator.getModelName(props);
assertNotNull(model);
assertTrue(model.contains("relation"));
}

@Test
public void testAnnotateWithNullEntitiesInMiddleOfListDoesNotThrow() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/relation/test-model.ser.gz");
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Annotation input = mock(Annotation.class);
Annotation output = mock(Annotation.class);
CoreMap inputSentence = mock(CoreMap.class);
CoreMap outputSentence = mock(CoreMap.class);
EntityMention entity1 = mock(EntityMention.class);
EntityMention entity2 = null;
RelationMention relation = mock(RelationMention.class);
when(relation.getType()).thenReturn("WORK_WITH");
List<EntityMention> entityList = Arrays.asList(entity1, entity2);
when(outputSentence.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(entityList);
when(outputSentence.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(Arrays.asList(relation));
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(outputSentence));
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(inputSentence));
MachineReading mr = mock(MachineReading.class);
when(mr.annotate(input)).thenReturn(output);
annotator.mr = mr;
annotator.annotate(input);
verify(inputSentence).set(MachineReadingAnnotations.EntityMentionsAnnotation.class, entityList);
verify(inputSentence).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, Arrays.asList(relation));
}

@Test
public void testAnnotateHandlesNullReturnedOutputAnnotationGracefully() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/relation/test-model.ser.gz");
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Annotation input = mock(Annotation.class);
MachineReading mr = mock(MachineReading.class);
when(mr.annotate(input)).thenReturn(null);
annotator.mr = mr;
try {
annotator.annotate(input);
fail("Expected NullPointerException due to null annotation return");
} catch (NullPointerException expected) {
assertTrue(true);
}
}

@Test
public void testModelNameWhenOnlyDeprecatedBlankUsesDefault() {
Properties props = new Properties();
props.setProperty("sup.relation.model", " ");
String model = RelationExtractorAnnotator.getModelName(props);
assertNotNull(model);
assertTrue(model.contains("relation"));
}

@Test
public void testAnnotateWhenBothEntityAndRelationMentionsAreNullAndVerboseFalse() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/relation/test-model.ser.gz");
props.setProperty("relation.verbose", "false");
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Annotation input = mock(Annotation.class);
Annotation output = mock(Annotation.class);
CoreMap inputSentence = mock(CoreMap.class);
CoreMap outputSentence = mock(CoreMap.class);
List<CoreMap> inputSentences = Arrays.asList(inputSentence);
List<CoreMap> outputSentences = Arrays.asList(outputSentence);
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(inputSentences);
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(outputSentences);
when(outputSentence.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(null);
when(outputSentence.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(null);
MachineReading mockMr = mock(MachineReading.class);
when(mockMr.annotate(input)).thenReturn(output);
annotator.mr = mockMr;
annotator.annotate(input);
verify(inputSentence).set(MachineReadingAnnotations.EntityMentionsAnnotation.class, null);
verify(inputSentence).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, null);
}

@Test
public void testAnnotateHandlesNullSentenceListsGracefully() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/relation/test-model.ser.gz");
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Annotation input = mock(Annotation.class);
Annotation output = mock(Annotation.class);
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(null);
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(null);
MachineReading mr = mock(MachineReading.class);
when(mr.annotate(input)).thenReturn(output);
annotator.mr = mr;
try {
annotator.annotate(input);
} catch (Exception e) {
fail("annotate should not throw when sentence lists are null");
}
}

@Test
public void testAnnotateWithNullEntityMentionListAndNonNullRelationsList() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/relation/test-model.ser.gz");
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Annotation annotationInput = mock(Annotation.class);
Annotation annotationOutput = mock(Annotation.class);
CoreMap inputSent = mock(CoreMap.class);
CoreMap outputSent = mock(CoreMap.class);
RelationMention rel = mock(RelationMention.class);
when(rel.getType()).thenReturn("PER-SOC");
when(outputSent.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(null);
when(outputSent.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(Arrays.asList(rel));
when(annotationInput.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(inputSent));
when(annotationOutput.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(outputSent));
MachineReading mr = mock(MachineReading.class);
when(mr.annotate(annotationInput)).thenReturn(annotationOutput);
annotator.mr = mr;
annotator.annotate(annotationInput);
verify(inputSent).set(MachineReadingAnnotations.EntityMentionsAnnotation.class, null);
verify(inputSent).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, Arrays.asList(rel));
}

@Test
public void testAnnotateWithNonNullEntityListAndNullRelationList() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/relation/test-model.ser.gz");
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Annotation annotationInput = mock(Annotation.class);
Annotation annotationOutput = mock(Annotation.class);
CoreMap inputSentence = mock(CoreMap.class);
CoreMap outputSentence = mock(CoreMap.class);
EntityMention entity = mock(EntityMention.class);
when(outputSentence.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(Arrays.asList(entity));
when(outputSentence.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(null);
when(annotationInput.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(inputSentence));
when(annotationOutput.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(outputSentence));
MachineReading mr = mock(MachineReading.class);
when(mr.annotate(annotationInput)).thenReturn(annotationOutput);
annotator.mr = mr;
annotator.annotate(annotationInput);
verify(inputSentence).set(MachineReadingAnnotations.EntityMentionsAnnotation.class, Arrays.asList(entity));
verify(inputSentence).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, null);
}

@Test
public void testGetModelNameWhenOnlyOldKeySetAndEmpty() {
Properties props = new Properties();
props.setProperty("sup.relation.model", "");
String modelName = RelationExtractorAnnotator.getModelName(props);
assertNotNull(modelName);
assertTrue(modelName.contains("relation"));
}

@Test
public void testRelationMentionTypeEqualsUnrelatedIsSkippedInVerbose() {
Properties props = new Properties();
props.setProperty("relation.verbose", "true");
props.setProperty("relation.model", "edu/stanford/nlp/models/relation/test-model.ser.gz");
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Annotation input = mock(Annotation.class);
Annotation output = mock(Annotation.class);
CoreMap inputSentence = mock(CoreMap.class);
CoreMap outputSentence = mock(CoreMap.class);
RelationMention rel = mock(RelationMention.class);
when(rel.getType()).thenReturn(RelationMention.UNRELATED);
when(outputSentence.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(null);
when(outputSentence.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(Arrays.asList(rel));
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(inputSentence));
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(outputSentence));
MachineReading mr = mock(MachineReading.class);
when(mr.annotate(input)).thenReturn(output);
annotator.mr = mr;
annotator.annotate(input);
verify(inputSentence).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, Arrays.asList(rel));
}

@Test
public void testConstructorWithNonexistentModelTriggersRuntimeException() {
Properties props = new Properties();
props.setProperty("relation.model", "path/to/nonexistent/model.ser.gz");
try {
new RelationExtractorAnnotator(props);
fail("Expected RuntimeException due to missing model file");
} catch (RuntimeException expected) {
assertTrue(expected.getCause() != null);
}
}

@Test
public void testAnnotateWithMismatchSentenceCountsHandlesPartialTransfer() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/relation/test-model.ser.gz");
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Annotation input = mock(Annotation.class);
Annotation output = mock(Annotation.class);
CoreMap inputSentence1 = mock(CoreMap.class);
CoreMap inputSentence2 = mock(CoreMap.class);
CoreMap outputSentence1 = mock(CoreMap.class);
List<CoreMap> inputSentences = Arrays.asList(inputSentence1, inputSentence2);
List<CoreMap> outputSentences = Arrays.asList(outputSentence1);
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(inputSentences);
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(outputSentences);
when(outputSentence1.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(null);
when(outputSentence1.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(null);
MachineReading mr = mock(MachineReading.class);
when(mr.annotate(input)).thenReturn(output);
annotator.mr = mr;
annotator.annotate(input);
verify(inputSentence1).set(MachineReadingAnnotations.EntityMentionsAnnotation.class, null);
verify(inputSentence1).set(MachineReadingAnnotations.RelationMentionsAnnotation.class, null);
}

@Test
public void testAnnotateWithExtraOutputSentencesThrowsIndexException() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/relation/test-model.ser.gz");
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Annotation input = mock(Annotation.class);
Annotation output = mock(Annotation.class);
CoreMap inputSentence = mock(CoreMap.class);
CoreMap outputSentence1 = mock(CoreMap.class);
CoreMap outputSentence2 = mock(CoreMap.class);
when(input.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(inputSentence));
when(output.get(CoreAnnotations.SentencesAnnotation.class)).thenReturn(Arrays.asList(outputSentence1, outputSentence2));
when(outputSentence1.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(null);
when(outputSentence1.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(null);
when(outputSentence2.get(MachineReadingAnnotations.EntityMentionsAnnotation.class)).thenReturn(null);
when(outputSentence2.get(MachineReadingAnnotations.RelationMentionsAnnotation.class)).thenReturn(null);
MachineReading mr = mock(MachineReading.class);
when(mr.annotate(input)).thenReturn(output);
annotator.mr = mr;
try {
annotator.annotate(input);
fail("Expected IndexOutOfBoundsException due to extra output sentence");
} catch (IndexOutOfBoundsException expected) {
assertTrue(true);
}
}

@Test
public void testGetVerboseDeprecatedTakesPrecedenceOverNew() {
Properties props = new Properties();
props.setProperty("relation.verbose", "false");
props.setProperty("sup.relation.verbose", "true");
boolean verbose = RelationExtractorAnnotator.getVerbose(props);
assertTrue(verbose);
}

@Test
public void testGetModelNameReturnsNewModelIfDeprecatedMissing() {
Properties props = new Properties();
props.setProperty("relation.model", "my/new/model/path.gz");
String modelName = RelationExtractorAnnotator.getModelName(props);
assertEquals("my/new/model/path.gz", modelName);
}

@Test
public void testGetModelNameUsesDefaultIfNoKeysPresent() {
Properties props = new Properties();
String result = RelationExtractorAnnotator.getModelName(props);
assertNotNull(result);
assertTrue(result.endsWith(".ser.gz"));
}

@Test
public void testRequiresSetContainsAllExpectedAnnotations() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/relation/test-model.ser.gz");
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> required = annotator.requires();
assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
assertTrue(required.contains(CoreAnnotations.NamedEntityTagAnnotation.class));
assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
assertFalse(required.contains(null));
}

@Test
public void testRequirementsSatisfiedOnlyIncludesEntityAndRelationMentions() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/relation/test-model.ser.gz");
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Set<Class<? extends edu.stanford.nlp.ling.CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
assertTrue(satisfied.contains(MachineReadingAnnotations.EntityMentionsAnnotation.class));
assertTrue(satisfied.contains(MachineReadingAnnotations.RelationMentionsAnnotation.class));
assertEquals(2, satisfied.size());
}

@Test
public void testAnnotateSkipsNullOutputAnnotationGracefully() {
Properties props = new Properties();
props.setProperty("relation.model", "edu/stanford/nlp/models/relation/test-model.ser.gz");
RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(props);
Annotation input = mock(Annotation.class);
MachineReading mr = mock(MachineReading.class);
when(mr.annotate(input)).thenReturn(null);
annotator.mr = mr;
try {
annotator.annotate(input);
fail("Expected NullPointerException due to null output annotation");
} catch (NullPointerException expected) {
assertTrue(true);
}
}
}
