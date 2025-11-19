public class RelationFeatureExtractor_3_GPTLLMTest {

// @Test
public void testIsNoun_PositiveCases() {
// assertTrue(RelationFeatureExtractor.isNoun("NN"));
// assertTrue(RelationFeatureExtractor.isNoun("NNS"));
// assertTrue(RelationFeatureExtractor.isNoun("RB"));
// assertTrue(RelationFeatureExtractor.isNoun("WP$"));
}

// @Test
public void testIsNoun_NegativeCase() {
// assertFalse(RelationFeatureExtractor.isNoun("VB"));
}

// @Test
public void testGetLexicalFeaturePartA_ReturnsSourceTokens() throws Exception {
String[] tokens = { "Barack", "Obama", "was", "president" };
// TokenLabelView pos = new TokenLabelView(ViewNames.POS, "test", null, 1.0);
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("test", "1", tokens);
// View mentionView = new View(ViewNames.MENTION, "test", ta, 1.0);
// Constituent source = new Constituent("PER", 1.0, ViewNames.MENTION, ta, 0, 2);
// source.addAttribute("EntityType", "PER");
// Constituent target = new Constituent("TITLE", 1.0, ViewNames.MENTION, ta, 2, 4);
// target.addAttribute("EntityType", "TITLE");
// mentionView.addConstituent(source);
// mentionView.addConstituent(target);
// ta.addView(ViewNames.MENTION, mentionView);
// Relation r = new Relation("rel", source, target, 1.0);
// RelationFeatureExtractor extractor = new RelationFeatureExtractor();
// List<String> features = extractor.getLexicalFeaturePartA(r);
// assertEquals(2, features.size());
// assertEquals("Barack", features.get(0));
// assertEquals("Obama", features.get(1));
}

// @Test
public void testGetLexicalFeaturePartB_ReturnsTargetTokens() throws Exception {
String[] tokens = { "Barack", "Obama", "served", "as", "president" };
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("test", "1", tokens);
// View mentionView = new View(ViewNames.MENTION, "test", ta, 1.0);
// Constituent source = new Constituent("PER", 1.0, ViewNames.MENTION, ta, 0, 2);
// source.addAttribute("EntityType", "PER");
// Constituent target = new Constituent("TITLE", 1.0, ViewNames.MENTION, ta, 4, 5);
// target.addAttribute("EntityType", "TITLE");
// mentionView.addConstituent(source);
// mentionView.addConstituent(target);
// ta.addView(ViewNames.MENTION, mentionView);
// Relation r = new Relation("rel", source, target, 1.0);
// RelationFeatureExtractor extractor = new RelationFeatureExtractor();
// List<String> features = extractor.getLexicalFeaturePartB(r);
// assertEquals(1, features.size());
// assertEquals("president", features.get(0));
}

// @Test
public void testGetCorefTag_TrueWhenEntityIDsMatch() throws Exception {
String[] tokens = { "Alice", "and", "she" };
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("test", "1", tokens);
// View mentionView = new View(ViewNames.MENTION, "test", ta, 1.0);
// Constituent source = new Constituent("PER", 1.0, ViewNames.MENTION, ta, 0, 1);
// Constituent target = new Constituent("PER", 1.0, ViewNames.MENTION, ta, 2, 3);
// source.addAttribute("EntityID", "E1");
// target.addAttribute("EntityID", "E1");
// mentionView.addConstituent(source);
// mentionView.addConstituent(target);
// ta.addView(ViewNames.MENTION, mentionView);
// Relation r = new Relation("coref", source, target, 1.0);
// RelationFeatureExtractor extractor = new RelationFeatureExtractor();
// String result = extractor.getCorefTag(r);
// assertEquals("TRUE", result);
}

// @Test
public void testGetCorefTag_FalseWhenEntityIDsDiffer() throws Exception {
String[] tokens = { "Alice", "met", "Bob" };
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("test", "1", tokens);
// View mentionView = new View(ViewNames.MENTION, "test", ta, 1.0);
// Constituent source = new Constituent("PER", 1.0, ViewNames.MENTION, ta, 0, 1);
// Constituent target = new Constituent("PER", 1.0, ViewNames.MENTION, ta, 2, 3);
// source.addAttribute("EntityID", "E1");
// target.addAttribute("EntityID", "E2");
// mentionView.addConstituent(source);
// mentionView.addConstituent(target);
// ta.addView(ViewNames.MENTION, mentionView);
// Relation r = new Relation("coref", source, target, 1.0);
// RelationFeatureExtractor extractor = new RelationFeatureExtractor();
// String result = extractor.getCorefTag(r);
// assertEquals("FALSE", result);
}

// @Test
public void testGetLexicalFeaturePartC_SingleWordBetweenEntities() throws Exception {
String[] tokens = { "President", "of", "USA" };
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("test", "1", tokens);
// Constituent source = new Constituent("TITLE", 1.0, "mention", ta, 0, 1);
// Constituent target = new Constituent("GPE", 1.0, "mention", ta, 2, 3);
// Relation r = new Relation("singleword", source, target, 1.0);
// RelationFeatureExtractor extractor = new RelationFeatureExtractor();
// List<String> features = extractor.getLexicalFeaturePartC(r);
// assertEquals(1, features.size());
// assertTrue(features.get(0).startsWith("singleword_"));
}

// @Test
public void testGetLexicalFeaturePartC_NoSingleWordBetweenEntities() throws Exception {
String[] tokens = { "President", "Obama" };
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("test", "1", tokens);
// Constituent source = new Constituent("TITLE", 1.0, "mention", ta, 0, 1);
// Constituent target = new Constituent("PER", 1.0, "mention", ta, 1, 2);
// Relation r = new Relation("nosingleword", source, target, 1.0);
// RelationFeatureExtractor extractor = new RelationFeatureExtractor();
// List<String> features = extractor.getLexicalFeaturePartC(r);
// assertEquals(1, features.size());
// assertEquals("No_singleword", features.get(0));
}

// @Test
public void testPatternRecognition_SameHeadSpan() throws Exception {
String[] tokens = { "Obama" };
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("test", "1", tokens);
// Constituent source = new Constituent("PER", 1.0, "view", ta, 0, 1);
// source.addAttribute("EntityType", "PER");
// List<String> features = RelationFeatureExtractor.patternRecognition(source, source);
// assertTrue(features.contains("SAME_SOURCE_TARGET_EXCEPTION"));
}

// @Test
public void testPatternRecognition_SameExtentSpan() throws Exception {
String[] tokens = { "White", "House" };
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("test", "1", tokens);
// Constituent source = new Constituent("ORG", 1.0, "view", ta, 0, 2);
// source.addAttribute("EntityType", "ORG");
// Constituent target = new Constituent("ORG", 1.0, "view", ta, 0, 2);
// target.addAttribute("EntityType", "ORG");
// List<String> features = RelationFeatureExtractor.patternRecognition(source, target);
// assertTrue(features.contains("SAME_SOURCE_TARGET_EXTENT_EXCEPTION"));
}

// @Test
public void testGetMentionFeature_BasicCompositionFeatures() throws Exception {
String[] tokens = { "John", "met", "Mary" };
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("doc", "1", tokens);
// View mentionView = new View(ViewNames.MENTION, "annotator", ta, 1.0);
// Constituent source = new Constituent("PER", 1.0, ViewNames.MENTION, ta, 0, 1);
// source.addAttribute("EntityMentionType", "NAM");
// source.addAttribute("EntityType", "PER");
// Constituent target = new Constituent("PER", 1.0, ViewNames.MENTION, ta, 2, 3);
// target.addAttribute("EntityMentionType", "NAM");
// target.addAttribute("EntityType", "PER");
// mentionView.addConstituent(source);
// mentionView.addConstituent(target);
// ta.addView(ViewNames.MENTION, mentionView);
// Relation r = new Relation("mentionRel", source, target, 1.0);
// RelationFeatureExtractor extractor = new RelationFeatureExtractor();
// List<String> features = extractor.getMentionFeature(r);
// assertTrue(features.contains("source_mtype_PER"));
// assertTrue(features.contains("target_mtype_PER"));
// assertTrue(features.contains("mlvl_NAM_NAM"));
}

// @Test
public void testGetEntityHeadForConstituent_InvalidCharOffset_NegativeSpan() throws Exception {
String[] tokens = { "X", "Y", "Z" };
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("doc", "1", tokens);
// Constituent extent = new Constituent("ORG", 1.0, "MENTION", ta, 0, 1);
// extent.addAttribute("EntityHeadStartCharOffset", "10");
// extent.addAttribute("EntityHeadEndCharOffset", "5");
// Constituent result = RelationFeatureExtractor.getEntityHeadForConstituent(extent, ta, "TEST");
// assertNull(result);
}

// @Test
public void testIsPossessive_WithTokenS() throws Exception {
String[] tokens = { "man", "'s", "hat" };
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("doc", "1", tokens);
// View pos = new TokenLabelView(ViewNames.POS, "pos", ta, 1.0);
// pos.addTokenLabel(0, "NN", 1.0);
// pos.addTokenLabel(1, "POS", 1.0);
// pos.addTokenLabel(2, "NN", 1.0);
// ta.addView(ViewNames.POS, pos);
// Constituent source = new Constituent("PER", 1.0, "mention", ta, 0, 1);
// Constituent target = new Constituent("OBJ", 1.0, "mention", ta, 2, 3);
// Relation r = new Relation("possessive", source, target, 1.0);
// boolean result = RelationFeatureExtractor.isPossessive(r);
// assertTrue(result);
}

// @Test
public void testIsPremodifier_JJModifier() throws Exception {
String[] tokens = { "beautiful", "building" };
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("doc", "1", tokens);
// View pos = new TokenLabelView(ViewNames.POS, "pos", ta, 1.0);
// pos.addTokenLabel(0, "JJ", 1.0);
// pos.addTokenLabel(1, "NN", 1.0);
// ta.addView(ViewNames.POS, pos);
// Constituent source = new Constituent("OBJ", 1.0, "mention", ta, 0, 1);
// Constituent target = new Constituent("OBJ", 1.0, "mention", ta, 1, 2);
// Relation r = new Relation("premodifier", source, target, 1.0);
// boolean result = RelationFeatureExtractor.isPremodifier(r);
// assertTrue(result);
}

// @Test
public void testIsPremodifier_FalseBecauseOfInvalidPOS() throws Exception {
String[] tokens = { "walked", "building" };
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("doc", "1", tokens);
// View pos = new TokenLabelView(ViewNames.POS, "pos", ta, 1.0);
// pos.addTokenLabel(0, "VBD", 1.0);
// pos.addTokenLabel(1, "NN", 1.0);
// ta.addView(ViewNames.POS, pos);
// Constituent source = new Constituent("OBJ", 1.0, "mention", ta, 0, 1);
// Constituent target = new Constituent("OBJ", 1.0, "mention", ta, 1, 2);
// Relation r = new Relation("premodifier", source, target, 1.0);
// boolean result = RelationFeatureExtractor.isPremodifier(r);
// assertFalse(result);
}

// @Test
public void testGetLexicalFeaturePartD_NoBetweenMentions() throws Exception {
String[] tokens = { "A", "B" };
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("doc", "1", tokens);
// Constituent source = new Constituent("X", 1.0, "mention", ta, 0, 1);
// Constituent target = new Constituent("Y", 1.0, "mention", ta, 1, 2);
// Relation r = new Relation("r1", source, target, 1.0);
// RelationFeatureExtractor extractor = new RelationFeatureExtractor();
// List<String> features = extractor.getLexicalFeaturePartD(r);
// assertEquals(1, features.size());
// assertEquals("No_between_features", features.get(0));
}

// @Test
public void testGetLexicalFeaturePartE_WithBoundaryValues() throws Exception {
String[] tokens = { "A", "B", "C", "D", "E" };
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("doc", "1", tokens);
// View sentenceView = ta.getView(ViewNames.SENTENCE);
// View pos = new TokenLabelView(ViewNames.POS, "posgen", ta, 1.0);
// pos.addTokenLabel(0, "NN", 1.0);
// pos.addTokenLabel(1, "NN", 1.0);
// pos.addTokenLabel(2, "NN", 1.0);
// pos.addTokenLabel(3, "NN", 1.0);
// pos.addTokenLabel(4, "NN", 1.0);
// ta.addView(ViewNames.POS, pos);
// Constituent source = new Constituent("ORG", 1.0, "mention", ta, 1, 2);
// Constituent target = new Constituent("GPE", 1.0, "mention", ta, 3, 4);
// Relation r = new Relation("r2", source, target, 1.0);
// RelationFeatureExtractor extractor = new RelationFeatureExtractor();
// List<String> feats = extractor.getLexicalFeaturePartE(r);
// assertTrue(feats.contains("fwM1_A"));
// assertTrue(feats.contains("fwM2_E"));
}

// @Test
public void testGetLexicalFeaturePartF_HeadExtractionNonNull() throws Exception {
String[] tokens = { "New", "York", "City" };
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("doc", "1", tokens);
// Constituent source = new Constituent("GPE", 1.0, "mention", ta, 0, 2);
// Constituent target = new Constituent("GPE", 1.0, "mention", ta, 2, 3);
// source.addAttribute("EntityHeadStartCharOffset", "0");
// source.addAttribute("EntityHeadEndCharOffset", "7");
// target.addAttribute("EntityHeadStartCharOffset", "8");
// target.addAttribute("EntityHeadEndCharOffset", "12");
// Relation r = new Relation("r3", source, target, 1.0);
// RelationFeatureExtractor extractor = new RelationFeatureExtractor();
// List<String> features = extractor.getLexicalFeaturePartF(r);
// assertEquals(3, features.size());
// assertTrue(features.get(0).startsWith("HM1_"));
}

// @Test
public void testGetTemplateFeature_AllFalseReturnsEmpty() throws Exception {
String[] tokens = { "walk", "by" };
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("doc", "1", tokens);
// View pos = new TokenLabelView(ViewNames.POS, "pos", ta, 1.0);
// pos.addTokenLabel(0, "VB", 1.0);
// pos.addTokenLabel(1, "IN", 1.0);
// ta.addView(ViewNames.POS, pos);
// Constituent source = new Constituent("ACT", 1.0, "view", ta, 0, 1);
// Constituent target = new Constituent("REL", 1.0, "view", ta, 1, 2);
// Relation r = new Relation("tmpl", source, target, 1.0);
// RelationFeatureExtractor extractor = new RelationFeatureExtractor();
// List<String> features = extractor.getTemplateFeature(r);
// assertTrue(features.isEmpty());
}

// @Test
public void testOnlyNounBetween_NoTokensBetweenReturnsTrue() throws Exception {
String[] tokens = { "NN1", "NN2" };
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("doc", "1", tokens);
// View pos = new TokenLabelView(ViewNames.POS, "p1", ta, 1.0);
// pos.addTokenLabel(0, "NN", 1.0);
// pos.addTokenLabel(1, "NN", 1.0);
// ta.addView(ViewNames.POS, pos);
// Constituent source = new Constituent("X", 1.0, "view", ta, 0, 1);
// Constituent target = new Constituent("Y", 1.0, "view", ta, 1, 2);
// boolean result = RelationFeatureExtractor.onlyNounBetween(source, target);
// assertTrue(result);
}

// @Test
public void testIsPossessive_TokensWithApostropheS_AlternateStructure() {
String[] tokens = { "John", "'", "s", "car" };
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("doc", "1", tokens);
// View posView = new TokenLabelView(ViewNames.POS, "pos", ta, 1.0);
// posView.addTokenLabel(0, "NNP", 1.0);
// posView.addTokenLabel(1, "''", 1.0);
// posView.addTokenLabel(2, "NN", 1.0);
// posView.addTokenLabel(3, "NN", 1.0);
// ta.addView(ViewNames.POS, posView);
// Constituent source = new Constituent("PER", 1.0, "mention", ta, 0, 1);
// Constituent target = new Constituent("OBJ", 1.0, "mention", ta, 3, 4);
// Relation r = new Relation("rel", source, target, 1.0);
// boolean isPossessive = RelationFeatureExtractor.isPossessive(r);
// assertFalse(isPossessive);
}

// @Test
public void testGetLexicalFeaturePartCC_TokensBetweenHeads() {
String[] tokens = { "CEO", "of", "IBM" };
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("doc", "1", tokens);
// Constituent source = new Constituent("TITLE", 1.0, "mention", ta, 0, 1);
// Constituent target = new Constituent("ORG", 1.0, "mention", ta, 2, 3);
// Relation r = new Relation("rel", source, target, 1.0);
// RelationFeatureExtractor extractor = new RelationFeatureExtractor();
// List<String> features = extractor.getLexicalFeaturePartCC(r);
// assertEquals(1, features.size());
// assertTrue(features.get(0).startsWith("bowbethead_"));
}

// @Test
public void testGetLexicalFeaturePartCC_NoTokensBetweenHeads() {
String[] tokens = { "Dr.", "Smith" };
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("doc", "1", tokens);
// Constituent source = new Constituent("PER", 1.0, "mention", ta, 0, 1);
// Constituent target = new Constituent("PER", 1.0, "mention", ta, 1, 2);
// Relation r = new Relation("rel", source, target, 1.0);
// RelationFeatureExtractor extractor = new RelationFeatureExtractor();
// List<String> features = extractor.getLexicalFeaturePartCC(r);
// assertEquals(0, features.size());
}

// @Test
public void testGetStructualFeature_MidMentionAndMidWordSizeNull_NoCoverage() {
String[] tokens = { "The", "big", "apple" };
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("doc", "1", tokens);
// View mentionView = new View(ViewNames.MENTION, "test", ta, 1.0);
// Constituent source = new Constituent("LOC", 1.0, ViewNames.MENTION, ta, 0, 1);
// source.addAttribute("EntityType", "LOC");
// Constituent target = new Constituent("LOC", 1.0, ViewNames.MENTION, ta, 0, 1);
// target.addAttribute("EntityType", "LOC");
// mentionView.addConstituent(source);
// mentionView.addConstituent(target);
// ta.addView(ViewNames.MENTION, mentionView);
// Relation r = new Relation("struct", source, target, 1.0);
// RelationFeatureExtractor extractor = new RelationFeatureExtractor();
// List<String> features = extractor.getStructualFeature(r);
// assertTrue(features.contains("middle_mention_size_null"));
// assertTrue(features.contains("middle_word_size_null"));
// assertTrue(features.contains("m1_m2_no_coverage"));
}

// @Test
public void testGetStructualFeature_SourceCoversTarget() {
String[] tokens = { "Mr.", "John", "Smith" };
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("doc", "1", tokens);
// View mentionView = new View(ViewNames.MENTION, "test", ta, 1.0);
// Constituent source = new Constituent("PER", 1.0, ViewNames.MENTION, ta, 0, 3);
// source.addAttribute("EnityType", "PER");
// Constituent target = new Constituent("PER", 1.0, ViewNames.MENTION, ta, 1, 2);
// target.addAttribute("EntityType", "PER");
// mentionView.addConstituent(source);
// mentionView.addConstituent(target);
// ta.addView(ViewNames.MENTION, mentionView);
// Relation r = new Relation("struct", source, target, 1.0);
// RelationFeatureExtractor extractor = new RelationFeatureExtractor();
// List<String> features = extractor.getStructualFeature(r);
// assertTrue(features.contains("m2_in_m1"));
}

// @Test
public void testOnlyNounBetween_TokensAreNotNoun() {
String[] tokens = { "bright", "sunny", "day" };
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("doc", "1", tokens);
// View pos = new TokenLabelView(ViewNames.POS, "test", ta, 1.0);
// pos.addTokenLabel(0, "JJ", 1.0);
// pos.addTokenLabel(1, "RB", 1.0);
// pos.addTokenLabel(2, "NN", 1.0);
// ta.addView(ViewNames.POS, pos);
// Constituent front = new Constituent("DESC", 1.0, "mention", ta, 0, 1);
// Constituent back = new Constituent("SUBJ", 1.0, "mention", ta, 2, 3);
// boolean result = RelationFeatureExtractor.onlyNounBetween(front, back);
// assertFalse(result);
}

// @Test
public void testGetTemplateFeature_AllDetected() {
String[] tokens = { "Barack", "Obama", "'s", "former", "advisor" };
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("doc", "1", tokens);
// View pos = new TokenLabelView(ViewNames.POS, "test", ta, 1.0);
// pos.addTokenLabel(0, "NNP", 1.0);
// pos.addTokenLabel(1, "NNP", 1.0);
// pos.addTokenLabel(2, "POS", 1.0);
// pos.addTokenLabel(3, "JJ", 1.0);
// pos.addTokenLabel(4, "NN", 1.0);
// ta.addView(ViewNames.POS, pos);
// Constituent source = new Constituent("PER", 1.0, "mention", ta, 0, 2);
// source.addAttribute("EntityType", "PER");
// Constituent target = new Constituent("TITLE", 1.0, "mention", ta, 3, 5);
// target.addAttribute("EntityType", "TITLE");
// Relation r = new Relation("rel", source, target, 1.0);
// RelationFeatureExtractor extractor = new RelationFeatureExtractor();
// List<String> tags = extractor.getTemplateFeature(r);
// assertTrue(tags.contains("is_possessive_structure") || tags.contains("is_premodifier_structure"));
}

// @Test
public void testIsPreposition_WithMatchingINButHasNounBetween() {
String[] tokens = { "President", "in", "city" };
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("doc", "1", tokens);
// View pos = new TokenLabelView(ViewNames.POS, "pos", ta, 1.0);
// pos.addTokenLabel(0, "NNP", 1.0);
// pos.addTokenLabel(1, "IN", 1.0);
// pos.addTokenLabel(2, "NN", 1.0);
// ta.addView(ViewNames.POS, pos);
// Constituent source = new Constituent("TITLE", 1.0, "mention", ta, 0, 1);
// Constituent target = new Constituent("LOC", 1.0, "mention", ta, 2, 3);
// Relation rel = new Relation("r", source, target, 1.0);
// boolean result = RelationFeatureExtractor.isPreposition(rel);
// assertFalse(result);
}

// @Test
public void testIsFormulaic_WithCommaTokenBetween() {
String[] tokens = { "Chicago", ",", "Illinois" };
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("doc", "1", tokens);
// View pos = new TokenLabelView(ViewNames.POS, "pos", ta, 1.0);
// pos.addTokenLabel(0, "NNP", 1.0);
// pos.addTokenLabel(1, ",", 1.0);
// pos.addTokenLabel(2, "NNP", 1.0);
// ta.addView(ViewNames.POS, pos);
// Constituent source = new Constituent("PER", 1.0, "mention", ta, 0, 1);
// source.addAttribute("EntityType", "GPE");
// Constituent target = new Constituent("PER", 1.0, "mention", ta, 2, 3);
// target.addAttribute("EntityType", "LOC");
// Relation r = new Relation("form", source, target, 1.0);
// boolean result = RelationFeatureExtractor.isFormulaic(r);
// assertTrue(result);
}

// @Test
public void testGetDependencyFeature_DifferentSentenceIds_ShouldReturnEmpty() {
String[] tokens = { "Obama", "is", "President", ".", "Apple", "is", "a", "Company" };
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("doc", "1", tokens);
// View deps = new View(ViewNames.DEPENDENCY_STANFORD, "test", ta, 1.0);
// View posView = new TokenLabelView(ViewNames.POS, "pos", ta, 1.0);
// View annotatedView = new View("RE_ANNOTATED", "ann", ta, 1.0);
for (int i = 0; i < tokens.length; i++) {
// posView.addTokenLabel(i, "NN", 1.0);
// Constituent word = new Constituent("dep", 1.0, ViewNames.DEPENDENCY_STANFORD, ta, i, i + 1);
// word.addAttribute("WORDNETTAG", "dummy");
// deps.addConstituent(word);
// annotatedView.addConstituent(word);
}
// ta.addView(ViewNames.DEPENDENCY_STANFORD, deps);
// ta.addView(ViewNames.POS, posView);
// ta.addView("RE_ANNOTATED", annotatedView);
// Constituent source = new Constituent("PER", 1.0, "mention", ta, 0, 1);
// Constituent target = new Constituent("ORG", 1.0, "mention", ta, 5, 6);
// Relation r = new Relation("rel", source, target, 1.0);
// List<Pair<String, String>> feats = RelationFeatureExtractor.getDependencyFeature(r);
// assertTrue(feats.isEmpty());
}

// @Test
public void testGetDependencyFeature_EmptyParseView_ShouldReturnEmpty() {
String[] tokens = { "Obama", "visited", "Berlin" };
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("doc", "s1", tokens);
// View pos = new TokenLabelView(ViewNames.POS, "pos", ta, 1.0);
for (int i = 0; i < tokens.length; i++) {
// pos.addTokenLabel(i, "NN", 1.0);
}
// View annotated = new View("RE_ANNOTATED", "ann", ta, 1.0);
// ta.addView(ViewNames.POS, pos);
// ta.addView("RE_ANNOTATED", annotated);
// Constituent source = new Constituent("PER", 1.0, "mention", ta, 0, 1);
// Constituent target = new Constituent("LOC", 1.0, "mention", ta, 2, 3);
// Relation r = new Relation("r", source, target, 1.0);
// List<Pair<String, String>> result = RelationFeatureExtractor.getDependencyFeature(r);
// assertTrue(result.isEmpty());
}

// @Test
public void testGetShallowParseFeature_NoTokensBetween_ShouldReturnInclusiveChunks() {
String[] tokens = { "Dr.", "Watson" };
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("doc5", "1", tokens);
// View chunk = new TokenLabelView(ViewNames.SHALLOW_PARSE, "mockChunker", ta, 1.0);
// chunk.addTokenLabel(0, "B-NP", 1.0);
// chunk.addTokenLabel(1, "I-NP", 1.0);
// ta.addView(ViewNames.SHALLOW_PARSE, chunk);
// Constituent source = new Constituent("PER", 1.0, "mention", ta, 0, 1);
// Constituent target = new Constituent("PER", 1.0, "mention", ta, 1, 2);
// Relation r = new Relation("shallow", source, target, 1.0);
// RelationFeatureExtractor extractor = new RelationFeatureExtractor();
// List<Pair<String, String>> feats = extractor.getShallowParseFeature(r);
// assertFalse(feats.isEmpty());
// assertTrue(feats.get(0).getFirst().startsWith("chunker_between_heads_"));
}

// @Test
public void testGetShallowParseFeature_HeadSpanEqual_ShouldReturnEmpty() {
String[] tokens = { "a", "b", "c" };
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("doc", "1", tokens);
// View chunk = new TokenLabelView(ViewNames.SHALLOW_PARSE, "mock", ta, 1.0);
// chunk.addTokenLabel(0, "B-NP", 1.0);
// chunk.addTokenLabel(1, "I-NP", 1.0);
// chunk.addTokenLabel(2, "I-NP", 1.0);
// ta.addView(ViewNames.SHALLOW_PARSE, chunk);
// Constituent s = new Constituent("PER", 1.0, "mention", ta, 1, 2);
// s.addAttribute("IsPredicted", "true");
// Constituent t = new Constituent("LOC", 1.0, "mention", ta, 1, 2);
// t.addAttribute("IsPredicted", "true");
// Relation r = new Relation("nullshallow", s, t, 1.0);
// RelationFeatureExtractor extractor = new RelationFeatureExtractor();
// List<Pair<String, String>> features = extractor.getShallowParseFeature(r);
// assertTrue(features.isEmpty());
}

// @Test
public void testGetMentionFeature_TargetCoversSource() {
String[] tokens = { "Alan", "Turing" };
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("testDoc", "1", tokens);
// View mention = new View(ViewNames.MENTION, "builder", ta, 1.0);
// Constituent source = new Constituent("PER", 1.0, ViewNames.MENTION, ta, 0, 1);
// source.addAttribute("EntityMentionType", "NAM");
// source.addAttribute("EntityType", "PER");
// Constituent target = new Constituent("PER", 1.0, ViewNames.MENTION, ta, 0, 2);
// target.addAttribute("EntityMentionType", "NAM");
// target.addAttribute("EntityType", "PER");
// mention.addConstituent(source);
// mention.addConstituent(target);
// ta.addView(ViewNames.MENTION, mention);
// Relation r = new Relation("cov", source, target, 1.0);
// RelationFeatureExtractor extractor = new RelationFeatureExtractor();
// List<String> features = extractor.getMentionFeature(r);
boolean found = false;
// for (String s : features) {
// if (s.startsWith("mlvl_cont_1")) {
// found = true;
// break;
// }
// }
// assertTrue(found);
}

// @Test
public void testGetEntityHeadForConstituent_TokenSpanOutOfBounds_ReturnsNull() {
String[] tokens = { "A", "B", "C" };
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("doc", "1", tokens);
// Constituent c = new Constituent("ORG", 1.0, "mention", ta, 0, 1);
// c.addAttribute("EntityHeadStartCharOffset", "999");
// c.addAttribute("EntityHeadEndCharOffset", "999");
// Constituent result = RelationFeatureExtractor.getEntityHeadForConstituent(c, ta, "TEST");
// assertNull(result);
}

// @Test
public void testGetEntityHeadForConstituent_InvalidCharOffsetFormat_ReturnsNull() {
String[] tokens = { "x", "y" };
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("doc", "s1", tokens);
// Constituent c = new Constituent("ORG", 1.0, "view", ta, 0, 1);
// c.addAttribute("EntityHeadStartCharOffset", "abc");
// c.addAttribute("EntityHeadEndCharOffset", "12");
// Constituent result = null;
try {
// result = RelationFeatureExtractor.getEntityHeadForConstituent(c, ta, "XYZ");
} catch (Exception e) {
// result = null;
}
// assertNull(result);
}

// @Test
public void testGetLexicalFeaturePartD_MultipleTokensBetweenLeftToRight() {
String[] tokens = { "A", "between", "tokens", "B" };
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("doc", "s1", tokens);
// Constituent source = new Constituent("X", 1.0, "mention", ta, 0, 1);
// Constituent target = new Constituent("Y", 1.0, "mention", ta, 3, 4);
// Relation r = new Relation("rel", source, target, 1.0);
// RelationFeatureExtractor extractor = new RelationFeatureExtractor();
// List<String> features = extractor.getLexicalFeaturePartD(r);
// assertTrue(features.contains("between_first_between"));
// assertTrue(features.contains("between_first_tokens"));
// assertTrue(features.contains("in_between_tokens"));
}

// @Test
public void testGetLexicalFeaturePartD_ReverseOrderMultipleTokens() {
String[] tokens = { "Entity2", "and", "some", "words", "Entity1" };
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("doc", "1", tokens);
// Constituent source = new Constituent("B", 1.0, "mention", ta, 4, 5);
// Constituent target = new Constituent("A", 1.0, "mention", ta, 0, 1);
// Relation r = new Relation("rev", source, target, 1.0);
// RelationFeatureExtractor extractor = new RelationFeatureExtractor();
// List<String> feats = extractor.getLexicalFeaturePartD(r);
// assertTrue(feats.contains("between_first_and"));
// assertTrue(feats.contains("between_first_words"));
// assertTrue(feats.contains("in_between_some"));
}

// @Test
public void testGetStructualFeature_SourceAndTargetSameSpan() {
String[] tokens = { "Entity" };
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("doc", "s1", tokens);
// View mention = new View(ViewNames.MENTION, "test", ta, 1.0);
// Constituent c1 = new Constituent("ORG", 1.0, ViewNames.MENTION, ta, 0, 1);
// c1.addAttribute("EntityType", "ORG");
// Constituent c2 = new Constituent("ORG", 1.0, ViewNames.MENTION, ta, 0, 1);
// c2.addAttribute("EntityType", "ORG");
// mention.addConstituent(c1);
// mention.addConstituent(c2);
// ta.addView(ViewNames.MENTION, mention);
// Relation r = new Relation("r", c1, c2, 1.0);
// RelationFeatureExtractor extractor = new RelationFeatureExtractor();
// List<String> feats = extractor.getStructualFeature(r);
// assertTrue(feats.contains("middle_mention_size_null"));
// assertTrue(feats.contains("middle_word_size_null"));
// assertTrue(feats.contains("m1_m2_no_coverage"));
}

// @Test
public void testIsPreposition_FoundInTo_NoNoun_SuccessfulMatch() {
String[] tokens = { "arrived", "in", "the", "evening" };
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("doc", "1", tokens);
// View pos = new TokenLabelView(ViewNames.POS, "test", ta, 1.0);
// pos.addTokenLabel(0, "VBD", 1.0);
// pos.addTokenLabel(1, "IN", 1.0);
// pos.addTokenLabel(2, "DT", 1.0);
// pos.addTokenLabel(3, "RB", 1.0);
// ta.addView(ViewNames.POS, pos);
// Constituent source = new Constituent("ACT", 1.0, "mention", ta, 0, 1);
// Constituent target = new Constituent("TIME", 1.0, "mention", ta, 3, 4);
// Relation r = new Relation("prepositionCheck", source, target, 1.0);
// boolean result = RelationFeatureExtractor.isPreposition(r);
// assertTrue(result);
}

// @Test
public void testIsPreposition_MissingINAndTooManyNounMidTokens() {
String[] tokens = { "man", "ran", "fast", "through", "streets", "to", "city" };
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("doc", "s1", tokens);
// View pos = new TokenLabelView(ViewNames.POS, "p", ta, 1.0);
// pos.addTokenLabel(0, "NN", 1.0);
// pos.addTokenLabel(1, "VBD", 1.0);
// pos.addTokenLabel(2, "RB", 1.0);
// pos.addTokenLabel(3, "IN", 1.0);
// pos.addTokenLabel(4, "NNS", 1.0);
// pos.addTokenLabel(5, "TO", 1.0);
// pos.addTokenLabel(6, "NN", 1.0);
// ta.addView(ViewNames.POS, pos);
// Constituent source = new Constituent("SUBJ", 1.0, "mention", ta, 0, 1);
// Constituent target = new Constituent("OBJ", 1.0, "mention", ta, 6, 7);
// Relation r = new Relation("prepositionFail", source, target, 1.0);
// boolean result = RelationFeatureExtractor.isPreposition(r);
// assertFalse(result);
}

// @Test
public void testPatternRecognition_CoversPrepPobj_ValidPattern() {
String[] tokens = { "build", "software", "for", "education" };
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("doc", "1", tokens);
// View deps = new View(ViewNames.DEPENDENCY_STANFORD, "test", ta, 1.0);
for (int i = 0; i < tokens.length; i++) {
// Constituent dep = new Constituent(i == 2 ? "prep" : (i == 3 ? "pobj" : "other"), 1.0, ViewNames.DEPENDENCY_STANFORD, ta, i, i + 1);
// dep.addAttribute("WORDNETTAG", "dummy");
// deps.addConstituent(dep);
}
// ta.addView(ViewNames.DEPENDENCY_STANFORD, deps);
// Constituent c1 = new Constituent("OBJ", 1.0, "mention", ta, 1, 2);
// c1.addAttribute("EntityType", "ORG");
// Constituent c2 = new Constituent("TARGET", 1.0, "mention", ta, 3, 4);
// c2.addAttribute("EntityType", "EDU");
// List<String> tags = RelationFeatureExtractor.patternRecognition(c1, c2);
boolean matched = false;
// for (String s : tags) {
// if (s.equals("prep_pobj_dep_structure")) {
// matched = true;
// }
// }
// assertTrue(matched);
}

// @Test
public void testGetLexicalFeaturePartE_SourceAtSentenceStart_TargetAtSentenceEnd() {
String[] tokens = { "Barack", "Obama", "visited", "Berlin" };
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("doc", "1", tokens);
// Constituent source = new Constituent("PER", 1.0, "mention", ta, 0, 1);
// Constituent target = new Constituent("LOC", 1.0, "mention", ta, 3, 4);
// Relation r = new Relation("structure", source, target, 1.0);
// RelationFeatureExtractor extractor = new RelationFeatureExtractor();
// List<String> features = extractor.getLexicalFeaturePartE(r);
// assertTrue(features.contains("fwM1_NULL"));
// assertTrue(features.contains("swM1_NULL"));
// assertTrue(features.contains("fwM2_NULL") || features.contains("fwM2_Berlin"));
}

// @Test
public void testGetLexicalFeaturePartF_EntityHeadsReturnedWithLabels() {
String[] tokens = { "Mr.", "Tom", "Johnson" };
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("doc", "1", tokens);
// Constituent source = new Constituent("PER", 1.0, "mention", ta, 0, 2);
// Constituent target = new Constituent("PER", 1.0, "mention", ta, 2, 3);
// source.addAttribute("EntityHeadStartCharOffset", "0");
// source.addAttribute("EntityHeadEndCharOffset", "3");
// target.addAttribute("EntityHeadStartCharOffset", "9");
// target.addAttribute("EntityHeadEndCharOffset", "15");
// Relation r = new Relation("headFeature", source, target, 1.0);
// RelationFeatureExtractor extractor = new RelationFeatureExtractor();
// List<String> features = extractor.getLexicalFeaturePartF(r);
// assertEquals(3, features.size());
// assertTrue(features.get(0).startsWith("HM1_"));
// assertTrue(features.get(1).startsWith("HM2_"));
// assertTrue(features.get(2).startsWith("HM12_"));
}

// @Test
public void testGetCollocationsFeature_SourceAndTargetHeadAtBounds() {
String[] tokens = { "Dr.", "Alice", "met", "Prof.", "Bob" };
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("doc", "1", tokens);
// Constituent source = new Constituent("PER", 1.0, "mention", ta, 0, 2);
// Constituent target = new Constituent("PER", 1.0, "mention", ta, 3, 5);
// Relation r = new Relation("colloc", source, target, 1.0);
// RelationFeatureExtractor extractor = new RelationFeatureExtractor();
// List<String> features = extractor.getCollocationsFeature(r);
// assertTrue(features.contains("s_m1_p1_Alice") || features.contains("s_m1_p1_"));
// assertTrue(features.contains("t_m1_p1_Bob") || features.contains("t_m1_p1_"));
// assertTrue(features.contains("s_m2_m1_Dr.") || features.contains("s_m2_m1_"));
// assertTrue(features.stream().anyMatch(s -> s.startsWith("s_m1_m1_")));
// assertTrue(features.stream().anyMatch(s -> s.startsWith("t_p1_p1_")));
}

// @Test
public void testGetMentionFeature_SourceAndTargetNestedCoversBothWays() {
String[] tokens = { "United", "States", "President" };
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("doc", "1", tokens);
// View mention = new View(ViewNames.MENTION, "manual", ta, 1.0);
// Constituent source = new Constituent("GPE", 1.0, ViewNames.MENTION, ta, 0, 3);
// Constituent target = new Constituent("GPE", 1.0, ViewNames.MENTION, ta, 1, 2);
// source.addAttribute("EntityMentionType", "NAM");
// source.addAttribute("EntityType", "GPE");
// target.addAttribute("EntityMentionType", "NAM");
// target.addAttribute("EntityType", "GPE");
// mention.addConstituent(source);
// mention.addConstituent(target);
// ta.addView(ViewNames.MENTION, mention);
// Relation r = new Relation("mention", source, target, 1.0);
// RelationFeatureExtractor extractor = new RelationFeatureExtractor();
// List<String> feats = extractor.getMentionFeature(r);
boolean containsNested = false;
// for (String s : feats) {
// if (s.startsWith("mlvl_cont_2_")) {
// containsNested = true;
// }
// }
// assertTrue(containsNested);
}

// @Test
public void testGetDependencyFeature_EmptyPathBetweenTokens() {
String[] tokens = { "Ann", "gave", "Bob" };
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("doc", "1", tokens);
// Constituent s = new Constituent("PER", 1.0, "mention", ta, 0, 1);
// Constituent t = new Constituent("PER", 1.0, "mention", ta, 2, 3);
// View deps = new View(ViewNames.DEPENDENCY_STANFORD, "test", ta, 1.0);
// View pos = new TokenLabelView(ViewNames.POS, "p", ta, 1.0);
// View re = new View("RE_ANNOTATED", "test", ta, 1.0);
for (int i = 0; i < 3; i++) {
// pos.addTokenLabel(i, "NN", 1.0);
// Constituent dep = new Constituent("dep", 1.0, ViewNames.DEPENDENCY_STANFORD, ta, i, i + 1);
// dep.addAttribute("WORDNETTAG", "n");
// deps.addConstituent(dep);
// re.addConstituent(dep);
}
// ta.addView(ViewNames.DEPENDENCY_STANFORD, deps);
// ta.addView(ViewNames.POS, pos);
// ta.addView("RE_ANNOTATED", re);
// Relation r = new Relation("dep", s, t, 1.0);
// List<Pair<String, String>> feats = RelationFeatureExtractor.getDependencyFeature(r);
// assertFalse(feats.isEmpty());
boolean hasPair = false;
// for (Pair<String, String> p : feats) {
// if (p.getFirst().startsWith("pos_tag_")) {
// hasPair = true;
// }
// }
// assertTrue(hasPair);
}

// @Test
public void testPatternRecognition_NoPrepPobjDependency_EmptyExtract() {
String[] tokens = { "Alice", "likes", "Banana" };
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("doc", "1", tokens);
// View deps = new View(ViewNames.DEPENDENCY_STANFORD, "builder", ta, 1.0);
for (int i = 0; i < tokens.length; i++) {
// Constituent c = new Constituent("x", 1.0, ViewNames.DEPENDENCY_STANFORD, ta, i, i + 1);
// deps.addConstituent(c);
}
// ta.addView(ViewNames.DEPENDENCY_STANFORD, deps);
// Constituent s = new Constituent("PERSON", 1.0, "mention", ta, 0, 1);
// s.addAttribute("EntityType", "PER");
// Constituent t = new Constituent("FOOD", 1.0, "mention", ta, 2, 3);
// t.addAttribute("EntityType", "ARG");
// List<String> features = RelationFeatureExtractor.patternRecognition(s, t);
// for (String f : features) {
// assertFalse(f.equals("prep_pobj_dep_structure"));
// }
}

// @Test
public void testGetStructualFeature_MissingMentionViewName_AllFallbacks() {
String[] tokens = { "XYZ", "works", "at", "ACME" };
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("doc", "1", tokens);
// Constituent s = new Constituent("PER", 1.0, "mention", ta, 0, 1);
// s.addAttribute("EntityType", "PER");
// Constituent t = new Constituent("ORG", 1.0, "mention", ta, 3, 4);
// t.addAttribute("EntityType", "ORG");
// Relation r = new Relation("structfallback", s, t, 1.0);
// RelationFeatureExtractor extractor = new RelationFeatureExtractor();
// List<String> feats = extractor.getStructualFeature(r);
// assertTrue(feats.contains("middle_mention_size_0"));
// assertTrue(feats.contains("middle_word_size_2"));
}

// @Test
public void testGetTemplateFeature_FormulaicOnly() {
String[] tokens = { "Washington", ",", "D.C." };
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("doc", "1", tokens);
// View pos = new TokenLabelView(ViewNames.POS, "p", ta, 1.0);
// pos.addTokenLabel(0, "NNP", 1.0);
// pos.addTokenLabel(1, ",", 1.0);
// pos.addTokenLabel(2, "NNP", 1.0);
// ta.addView(ViewNames.POS, pos);
// Constituent source = new Constituent("GPE", 1.0, "mention", ta, 0, 1);
// source.addAttribute("EntityType", "GPE");
// Constituent target = new Constituent("GPE", 1.0, "mention", ta, 2, 3);
// target.addAttribute("EntityType", "GPE");
// Relation r = new Relation("r", source, target, 1.0);
// RelationFeatureExtractor extractor = new RelationFeatureExtractor();
// List<String> result = extractor.getTemplateFeature(r);
// assertTrue(result.contains("is_formulaic_structure"));
}

// @Test
public void testGetTemplateFeature_PreModifierOnly() {
String[] tokens = { "senior", "engineer" };
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("doc", "1", tokens);
// View pos = new TokenLabelView(ViewNames.POS, "source", ta, 1.0);
// pos.addTokenLabel(0, "JJ", 1.0);
// pos.addTokenLabel(1, "NN", 1.0);
// ta.addView(ViewNames.POS, pos);
// Constituent source = new Constituent("TITLE", 1.0, "mention", ta, 0, 1);
// source.addAttribute("EntityType", "TITLE");
// Constituent target = new Constituent("TITLE", 1.0, "mention", ta, 1, 2);
// target.addAttribute("EntityType", "TITLE");
// Relation r = new Relation("premod", source, target, 1.0);
// RelationFeatureExtractor extractor = new RelationFeatureExtractor();
// List<String> result = extractor.getTemplateFeature(r);
// assertTrue(result.contains("is_premodifier_structure"));
}

// @Test
public void testGetMentionFeature_ConstituentsCoverEachOther_BothFlagsAdded() {
String[] tokens = { "the", "president", "of", "France" };
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("doc", "1", tokens);
// View mention = new View(ViewNames.MENTION, "manual", ta, 1.0);
// Constituent outer = new Constituent("PER", 1.0, ViewNames.MENTION, ta, 0, 4);
// outer.addAttribute("EntityMentionType", "NAM");
// outer.addAttribute("EntityType", "PER");
// Constituent inner = new Constituent("PER", 1.0, ViewNames.MENTION, ta, 1, 2);
// inner.addAttribute("EntityMentionType", "NAM");
// inner.addAttribute("EntityType", "PER");
// mention.addConstituent(outer);
// mention.addConstituent(inner);
// ta.addView(ViewNames.MENTION, mention);
// Relation r = new Relation("mention", outer, inner, 1.0);
// RelationFeatureExtractor extractor = new RelationFeatureExtractor();
// List<String> features = extractor.getMentionFeature(r);
boolean hasCont1 = false;
boolean hasCont2 = false;
// for (String feature : features) {
// if (feature.startsWith("mlvl_cont_1_")) {
// hasCont1 = true;
// }
// if (feature.startsWith("mlvl_cont_2_")) {
// hasCont2 = true;
// }
// }
// assertTrue(hasCont1 || hasCont2);
}

// @Test
public void testGetLexicalFeaturePartCC_SymmetricBetweenHeadExtraction() {
String[] tokens = { "leader", "of", "UN" };
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("doc", "1", tokens);
// Constituent source = new Constituent("TITLE", 1.0, "mention", ta, 0, 1);
// source.addAttribute("EntityHeadStartCharOffset", "0");
// source.addAttribute("EntityHeadEndCharOffset", "6");
// Constituent target = new Constituent("ORG", 1.0, "mention", ta, 2, 3);
// target.addAttribute("EntityHeadStartCharOffset", "9");
// target.addAttribute("EntityHeadEndCharOffset", "11");
// RelationFeatureExtractor extractor = new RelationFeatureExtractor();
// Relation r = new Relation("bowCC", source, target, 1.0);
// List<String> features = extractor.getLexicalFeaturePartCC(r);
// assertTrue(features.size() > 0);
// assertTrue(features.get(0).startsWith("bowbethead_"));
}

// @Test
public void testGetCollocationsFeature_SourceAndTargetHeadAtEdges_HandlesBounds() {
String[] tokens = { "Dr.", "Jane", "Doe", "from", "Stanford" };
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("doc", "id", tokens);
// Constituent source = new Constituent("PER", 1.0, "mention", ta, 0, 3);
// Constituent target = new Constituent("ORG", 1.0, "mention", ta, 4, 5);
// Relation r = new Relation("colloc", source, target, 1.0);
// RelationFeatureExtractor extractor = new RelationFeatureExtractor();
// List<String> features = extractor.getCollocationsFeature(r);
boolean hasEdgeFeature = false;
// for (String feat : features) {
// if (feat.startsWith("s_p1_p1_null") || feat.startsWith("t_p1_p1_")) {
// hasEdgeFeature = true;
// }
// }
// assertTrue(hasEdgeFeature);
}

// @Test
public void testOnlyNounBetween_EmptySpan_ReturnsTrue() {
String[] tokens = { "cat", "dog" };
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("doc", "id", tokens);
// View pos = new TokenLabelView(ViewNames.POS, "test", ta, 1.0);
// pos.addTokenLabel(0, "NN", 1.0);
// pos.addTokenLabel(1, "NN", 1.0);
// ta.addView(ViewNames.POS, pos);
// Constituent source = new Constituent("ANIMAL", 1.0, "mention", ta, 0, 1);
// Constituent target = new Constituent("ANIMAL", 1.0, "mention", ta, 1, 2);
// boolean result = RelationFeatureExtractor.onlyNounBetween(source, target);
// assertTrue(result);
}

// @Test
public void testOnlyNounBetween_NonNounInBetween_ReturnsFalse() {
String[] tokens = { "professor", "quickly", "explained" };
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("doc", "id", tokens);
// View pos = new TokenLabelView(ViewNames.POS, "pos", ta, 1.0);
// pos.addTokenLabel(0, "NN", 1.0);
// pos.addTokenLabel(1, "RB", 1.0);
// pos.addTokenLabel(2, "VBD", 1.0);
// ta.addView(ViewNames.POS, pos);
// Constituent source = new Constituent("PER", 1.0, "mention", ta, 0, 1);
// Constituent target = new Constituent("ACT", 1.0, "mention", ta, 2, 3);
// boolean val = RelationFeatureExtractor.onlyNounBetween(source, target);
// assertFalse(val);
}

// @Test
public void testGetShallowParseFeature_OnlyOneChunkBetweenExtents() {
String[] tokens = { "John", "went", "to", "Paris" };
// TextAnnotation ta = DummyTextAnnotationBuilder.buildTextAnnotation("doc", "id", tokens);
// TokenLabelView chunkView = new TokenLabelView(ViewNames.SHALLOW_PARSE, "chunker", ta, 1.0);
// chunkView.addTokenLabel(0, "B-NP", 1.0);
// chunkView.addTokenLabel(1, "B-VP", 1.0);
// chunkView.addTokenLabel(2, "B-PP", 1.0);
// chunkView.addTokenLabel(3, "B-NP", 1.0);
// ta.addView(ViewNames.SHALLOW_PARSE, chunkView);
// Constituent source = new Constituent("PER", 1.0, "mention", ta, 0, 1);
// Constituent target = new Constituent("LOC", 1.0, "mention", ta, 3, 4);
// Relation r = new Relation("shallowParse", source, target, 1.0);
// RelationFeatureExtractor extractor = new RelationFeatureExtractor();
// List<Pair<String, String>> feats = extractor.getShallowParseFeature(r);
boolean foundBetweenChunks = false;
// for (Pair<String, String> pair : feats) {
// if (pair.getFirst().startsWith("chunker_between_extents")) {
// foundBetweenChunks = true;
// }
// }
// assertTrue(foundBetweenChunks);
}
}
