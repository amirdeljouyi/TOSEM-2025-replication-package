package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.ie.machinereading.structure.EntityMention;
import edu.stanford.nlp.ie.machinereading.structure.MachineReadingAnnotations;
import edu.stanford.nlp.ie.machinereading.structure.RelationMention;
import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import static org.junit.Assert.*;

public class RelationExtractorAnnotatorPromptTest {

  @Test
  public void getVerboseDefaultsToFalse() {
    Properties properties = new Properties();

    assertFalse(RelationExtractorAnnotator.getVerbose(properties));
  }

  @Test
  public void getVerboseReadsCurrentPropertyCaseInsensitively() {
    Properties properties = new Properties();
    properties.setProperty("relation.verbose", "TrUe");

    assertTrue(RelationExtractorAnnotator.getVerbose(properties));
  }

  @Test
  public void getVerboseTreatsNonBooleanTextAsFalse() {
    Properties properties = new Properties();
    properties.setProperty("relation.verbose", "yes");

    assertFalse(RelationExtractorAnnotator.getVerbose(properties));
  }

  @Test
  public void getVerboseLegacyPropertyTakesPrecedence() {
    Properties properties = new Properties();
    properties.setProperty("sup.relation.verbose", "true");
    properties.setProperty("relation.verbose", "false");

    assertTrue(RelationExtractorAnnotator.getVerbose(properties));
  }

  @Test
  public void getVerboseLegacyFalseOverridesCurrentTrue() {
    Properties properties = new Properties();
    properties.setProperty("sup.relation.verbose", "false");
    properties.setProperty("relation.verbose", "true");

    assertFalse(RelationExtractorAnnotator.getVerbose(properties));
  }

  @Test
  public void getModelNameDefaultsToBundledModelPath() {
    Properties properties = new Properties();

    assertEquals(DefaultPaths.DEFAULT_SUP_RELATION_EX_RELATION_MODEL,
        RelationExtractorAnnotator.getModelName(properties));
  }

  @Test
  public void getModelNameReadsCurrentPropertyVerbatim() {
    Properties properties = new Properties();
    properties.setProperty("relation.model", "models/custom relation.ser");

    assertEquals("models/custom relation.ser", RelationExtractorAnnotator.getModelName(properties));
  }

  @Test
  public void getModelNameAllowsExplicitEmptyValue() {
    Properties properties = new Properties();
    properties.setProperty("relation.model", "");

    assertEquals("", RelationExtractorAnnotator.getModelName(properties));
  }

  @Test
  public void getModelNameLegacyPropertyTakesPrecedence() {
    Properties properties = new Properties();
    properties.setProperty("sup.relation.model", "legacy.ser");
    properties.setProperty("relation.model", "current.ser");

    assertEquals("legacy.ser", RelationExtractorAnnotator.getModelName(properties));
  }

  @Test
  public void requiresDeclaresCompletePipelineInputContract() {
    Properties properties = new Properties();
    String modelJar = new File(System.getProperty("user.home"),
        ".m2/repository/edu/stanford/nlp/stanford-corenlp/4.5.7/stanford-corenlp-4.5.7-models.jar").toURI().toString();
    properties.setProperty("relation.model", "jar:" + modelJar
        + "!/edu/stanford/nlp/models/supervised_relation_extractor/roth_relation_model_pipelineNER.ser");
    RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(properties);
    Set<Class<? extends CoreAnnotation>> required = annotator.requires();

    assertEquals(8, required.size());
    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.SentencesAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.PartOfSpeechAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.NamedEntityTagAnnotation.class));
    assertTrue(required.contains(TreeCoreAnnotations.TreeAnnotation.class));
    assertTrue(required.contains(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class));
    assertTrue(required.contains(SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class));
    assertTrue(required.contains(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void requiresReturnsAnUnmodifiableSet() {
    Properties properties = new Properties();
    String modelJar = new File(System.getProperty("user.home"),
        ".m2/repository/edu/stanford/nlp/stanford-corenlp/4.5.7/stanford-corenlp-4.5.7-models.jar").toURI().toString();
    properties.setProperty("relation.model", "jar:" + modelJar
        + "!/edu/stanford/nlp/models/supervised_relation_extractor/roth_relation_model_pipelineNER.ser");
    RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(properties);

    annotator.requires().clear();
  }

  @Test
  public void requirementsSatisfiedDeclaresBothProducedAnnotations() {
    Properties properties = new Properties();
    String modelJar = new File(System.getProperty("user.home"),
        ".m2/repository/edu/stanford/nlp/stanford-corenlp/4.5.7/stanford-corenlp-4.5.7-models.jar").toURI().toString();
    properties.setProperty("relation.model", "jar:" + modelJar
        + "!/edu/stanford/nlp/models/supervised_relation_extractor/roth_relation_model_pipelineNER.ser");
    RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(properties);
    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();

    assertEquals(2, satisfied.size());
    assertTrue(satisfied.contains(MachineReadingAnnotations.EntityMentionsAnnotation.class));
    assertTrue(satisfied.contains(MachineReadingAnnotations.RelationMentionsAnnotation.class));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void requirementsSatisfiedReturnsAnUnmodifiableSet() {
    Properties properties = new Properties();
    String modelJar = new File(System.getProperty("user.home"),
        ".m2/repository/edu/stanford/nlp/stanford-corenlp/4.5.7/stanford-corenlp-4.5.7-models.jar").toURI().toString();
    properties.setProperty("relation.model", "jar:" + modelJar
        + "!/edu/stanford/nlp/models/supervised_relation_extractor/roth_relation_model_pipelineNER.ser");
    RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(properties);

    annotator.requirementsSatisfied().add(CoreAnnotations.TextAnnotation.class);
  }

  @Test
  public void constructorWrapsMissingModelFailure() {
    Properties properties = new Properties();
    properties.setProperty("relation.model", "missing/model/that/cannot/exist.ser");

    try {
      new RelationExtractorAnnotator(properties);
      fail("Expected constructor to reject a missing model");
    } catch (RuntimeException expected) {
      assertNotNull(expected.getCause());
      assertFalse(expected.getCause() instanceof RuntimeException);
    }
  }

  @Test
  public void constructorLoadsSerializedModelFromUrl() {
    Properties properties = new Properties();
    String modelJar = new File(System.getProperty("user.home"),
        ".m2/repository/edu/stanford/nlp/stanford-corenlp/4.5.7/stanford-corenlp-4.5.7-models.jar").toURI().toString();
    properties.setProperty("relation.model", "jar:" + modelJar
        + "!/edu/stanford/nlp/models/supervised_relation_extractor/roth_relation_model_pipelineNER.ser");

    RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(properties);

    assertNotNull(annotator);
    assertEquals(8, annotator.requires().size());
  }

  @Test
  public void annotateTransfersEmptyEntityAndRelationListsAndPreservesSentence() {
    Properties properties = new Properties();
    String modelJar = new File(System.getProperty("user.home"),
        ".m2/repository/edu/stanford/nlp/stanford-corenlp/4.5.7/stanford-corenlp-4.5.7-models.jar").toURI().toString();
    properties.setProperty("relation.model", "jar:" + modelJar
        + "!/edu/stanford/nlp/models/supervised_relation_extractor/roth_relation_model_pipelineNER.ser");
    RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(properties);
    CoreLabel token = new CoreLabel();
    token.setWord("runs");
    token.setValue("runs");
    token.setTag("VBZ");
    token.setNER("O");
    token.setIndex(1);
    Annotation sentence = new Annotation("runs");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, Tree.valueOf("(ROOT (S (VBZ runs)))"));
    List<RelationMention> oldRelations = new ArrayList<>();
    sentence.set(MachineReadingAnnotations.RelationMentionsAnnotation.class, oldRelations);
    Annotation document = new Annotation("runs");
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(document);

    assertSame(sentence, document.get(CoreAnnotations.SentencesAnnotation.class).get(0));
    assertEquals("runs", sentence.get(CoreAnnotations.TextAnnotation.class));
    assertNotNull(sentence.get(MachineReadingAnnotations.EntityMentionsAnnotation.class));
    assertTrue(sentence.get(MachineReadingAnnotations.EntityMentionsAnnotation.class).isEmpty());
    assertNotNull(sentence.get(MachineReadingAnnotations.RelationMentionsAnnotation.class));
    assertTrue(sentence.get(MachineReadingAnnotations.RelationMentionsAnnotation.class).isEmpty());
    assertNotSame(oldRelations, sentence.get(MachineReadingAnnotations.RelationMentionsAnnotation.class));
  }

  @Test
  public void annotateConvertsPersonNerTagIntoEntityMention() {
    Properties properties = new Properties();
    String modelJar = new File(System.getProperty("user.home"),
        ".m2/repository/edu/stanford/nlp/stanford-corenlp/4.5.7/stanford-corenlp-4.5.7-models.jar").toURI().toString();
    properties.setProperty("relation.model", "jar:" + modelJar
        + "!/edu/stanford/nlp/models/supervised_relation_extractor/roth_relation_model_pipelineNER.ser");
    RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(properties);
    CoreLabel token = new CoreLabel();
    token.setWord("Alice");
    token.setValue("Alice");
    token.setTag("NNP");
    token.setNER("PERSON");
    token.setIndex(1);
    Annotation sentence = new Annotation("Alice");
    sentence.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    sentence.set(TreeCoreAnnotations.TreeAnnotation.class, Tree.valueOf("(ROOT (S (NNP Alice)))"));
    Annotation document = new Annotation("Alice");
    document.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));

    annotator.annotate(document);

    List<EntityMention> entities = sentence.get(MachineReadingAnnotations.EntityMentionsAnnotation.class);
    assertEquals(1, entities.size());
    assertEquals("PEOPLE", entities.get(0).getType());
    assertEquals(0, entities.get(0).getExtentTokenStart());
    assertEquals(1, entities.get(0).getExtentTokenEnd());
    assertEquals(0, entities.get(0).getSyntacticHeadTokenPosition());
    assertTrue(sentence.get(MachineReadingAnnotations.RelationMentionsAnnotation.class).isEmpty());
  }

  @Test
  public void annotateProcessesMultipleSentencesIndependently() {
    Properties properties = new Properties();
    String modelJar = new File(System.getProperty("user.home"),
        ".m2/repository/edu/stanford/nlp/stanford-corenlp/4.5.7/stanford-corenlp-4.5.7-models.jar").toURI().toString();
    properties.setProperty("relation.model", "jar:" + modelJar
        + "!/edu/stanford/nlp/models/supervised_relation_extractor/roth_relation_model_pipelineNER.ser");
    RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(properties);
    CoreLabel alice = new CoreLabel();
    alice.setWord("Alice");
    alice.setValue("Alice");
    alice.setTag("NNP");
    alice.setNER("PERSON");
    alice.setIndex(1);
    Annotation first = new Annotation("Alice");
    first.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(alice));
    first.set(TreeCoreAnnotations.TreeAnnotation.class, Tree.valueOf("(ROOT (S (NNP Alice)))"));
    CoreLabel acme = new CoreLabel();
    acme.setWord("Acme");
    acme.setValue("Acme");
    acme.setTag("NNP");
    acme.setNER("ORGANIZATION");
    acme.setIndex(1);
    Annotation second = new Annotation("Acme");
    second.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(acme));
    second.set(TreeCoreAnnotations.TreeAnnotation.class, Tree.valueOf("(ROOT (S (NNP Acme)))"));
    Annotation document = new Annotation("Alice. Acme.");
    document.set(CoreAnnotations.SentencesAnnotation.class, Arrays.asList(first, second));

    annotator.annotate(document);

    List<EntityMention> firstEntities = first.get(MachineReadingAnnotations.EntityMentionsAnnotation.class);
    List<EntityMention> secondEntities = second.get(MachineReadingAnnotations.EntityMentionsAnnotation.class);
    assertEquals(1, firstEntities.size());
    assertEquals("PEOPLE", firstEntities.get(0).getType());
    assertEquals(1, secondEntities.size());
    assertEquals("ORGANIZATION", secondEntities.get(0).getType());
    assertNotSame(firstEntities, secondEntities);
    assertTrue(first.get(MachineReadingAnnotations.RelationMentionsAnnotation.class).isEmpty());
    assertTrue(second.get(MachineReadingAnnotations.RelationMentionsAnnotation.class).isEmpty());
  }

  @Test
  public void annotateDocumentWithoutSentencesLeavesItUnchanged() {
    Properties properties = new Properties();
    String modelJar = new File(System.getProperty("user.home"),
        ".m2/repository/edu/stanford/nlp/stanford-corenlp/4.5.7/stanford-corenlp-4.5.7-models.jar").toURI().toString();
    properties.setProperty("relation.model", "jar:" + modelJar
        + "!/edu/stanford/nlp/models/supervised_relation_extractor/roth_relation_model_pipelineNER.ser");
    RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(properties);
    Annotation document = new Annotation("unannotated");

    annotator.annotate(document);

    assertEquals("unannotated", document.get(CoreAnnotations.TextAnnotation.class));
    assertNull(document.get(CoreAnnotations.SentencesAnnotation.class));
  }

  @Test(expected = NullPointerException.class)
  public void annotateRejectsNullDocument() {
    Properties properties = new Properties();
    String modelJar = new File(System.getProperty("user.home"),
        ".m2/repository/edu/stanford/nlp/stanford-corenlp/4.5.7/stanford-corenlp-4.5.7-models.jar").toURI().toString();
    properties.setProperty("relation.model", "jar:" + modelJar
        + "!/edu/stanford/nlp/models/supervised_relation_extractor/roth_relation_model_pipelineNER.ser");
    RelationExtractorAnnotator annotator = new RelationExtractorAnnotator(properties);

    annotator.annotate(null);
  }
}
