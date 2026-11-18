package edu.stanford.nlp.pipeline;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.*;

public class CleanXmlAnnotatorPromptTest {

  @Test
  public void defaultConstructorRemovesTagsAndUnescapesText() {
    CoreLabel open = new CoreLabel(); open.setWord("<p>");
    CoreLabel first = new CoreLabel(); first.setWord("Tom &amp; Jerry");
    CoreLabel close = new CoreLabel(); close.setWord("</p>");
    List<CoreLabel> result = new CleanXmlAnnotator().process(Arrays.asList(open, first, close));

    assertEquals(1, result.size());
    assertSame(first, result.get(0));
    assertEquals("Tom & Jerry", result.get(0).word());
    assertEquals(Collections.singletonList("p"), result.get(0).get(CoreAnnotations.XmlContextAnnotation.class));
  }

  @Test
  public void selectedElementDropsOutsideAndNonmatchingElementText() {
    CoreLabel outside1 = new CoreLabel(); outside1.setWord("outside");
    CoreLabel openSkip = new CoreLabel(); openSkip.setWord("<skip>");
    CoreLabel skipped = new CoreLabel(); skipped.setWord("hidden");
    CoreLabel closeSkip = new CoreLabel(); closeSkip.setWord("</skip>");
    CoreLabel openKeep = new CoreLabel(); openKeep.setWord("<keep>");
    CoreLabel kept = new CoreLabel(); kept.setWord("visible");
    CoreLabel closeKeep = new CoreLabel(); closeKeep.setWord("</keep>");
    CoreLabel outside2 = new CoreLabel(); outside2.setWord("outside2");

    List<CoreLabel> result = new CleanXmlAnnotator("keep", "", null, true).process(
        Arrays.asList(outside1, openSkip, skipped, closeSkip, openKeep, kept, closeKeep, outside2));

    assertEquals(Collections.singletonList(kept), result);
    assertEquals(Collections.singletonList("keep"), kept.get(CoreAnnotations.XmlContextAnnotation.class));
  }

  @Test
  public void nullXmlPatternKeepsAllTextButStillRemovesTags() {
    CoreLabel before = new CoreLabel(); before.setWord("before");
    CoreLabel open = new CoreLabel(); open.setWord("<x>");
    CoreLabel inside = new CoreLabel(); inside.setWord("inside");
    CoreLabel close = new CoreLabel(); close.setWord("</x>");
    CoreLabel after = new CoreLabel(); after.setWord("after");

    List<CoreLabel> result = new CleanXmlAnnotator(null, "p", null, false).process(
        Arrays.asList(before, open, inside, close, after));

    assertEquals(Arrays.asList(before, inside, after), result);
    assertEquals(Collections.emptyList(), before.get(CoreAnnotations.XmlContextAnnotation.class));
    assertEquals(Collections.emptyList(), inside.get(CoreAnnotations.XmlContextAnnotation.class));
  }

  @Test
  public void nestedContextsAreRecordedAndSharedSafely() {
    CoreLabel a = new CoreLabel(); a.setWord("<a>");
    CoreLabel one = new CoreLabel(); one.setWord("one");
    CoreLabel b = new CoreLabel(); b.setWord("<b>");
    CoreLabel two = new CoreLabel(); two.setWord("two");
    CoreLabel three = new CoreLabel(); three.setWord("three");
    CoreLabel closeB = new CoreLabel(); closeB.setWord("</b>");
    CoreLabel four = new CoreLabel(); four.setWord("four");
    CoreLabel closeA = new CoreLabel(); closeA.setWord("</a>");

    List<CoreLabel> result = new CleanXmlAnnotator().process(
        Arrays.asList(a, one, b, two, three, closeB, four, closeA));

    assertEquals(Arrays.asList(one, two, three, four), result);
    assertEquals(Collections.singletonList("a"), one.get(CoreAnnotations.XmlContextAnnotation.class));
    assertEquals(Arrays.asList("a", "b"), two.get(CoreAnnotations.XmlContextAnnotation.class));
    assertSame(two.get(CoreAnnotations.XmlContextAnnotation.class), three.get(CoreAnnotations.XmlContextAnnotation.class));
    assertEquals(Collections.singletonList("a"), four.get(CoreAnnotations.XmlContextAnnotation.class));
    try {
      two.get(CoreAnnotations.XmlContextAnnotation.class).add("bad");
      fail("XML context must be immutable");
    } catch (UnsupportedOperationException expected) {
      assertEquals(Arrays.asList("a", "b"), two.get(CoreAnnotations.XmlContextAnnotation.class));
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void strictModeRejectsUnmatchedClosingTag() {
    CoreLabel close = new CoreLabel(); close.setWord("</orphan>");
    new CleanXmlAnnotator(".*", "", null, false).process(Collections.singletonList(close));
  }

  @Test(expected = IllegalArgumentException.class)
  public void strictModeRejectsMismatchedNesting() {
    CoreLabel openA = new CoreLabel(); openA.setWord("<a>");
    CoreLabel openB = new CoreLabel(); openB.setWord("<b>");
    CoreLabel closeA = new CoreLabel(); closeA.setWord("</a>");
    new CleanXmlAnnotator(".*", "", null, false).process(Arrays.asList(openA, openB, closeA));
  }

  @Test(expected = IllegalArgumentException.class)
  public void strictModeRejectsUnclosedTag() {
    CoreLabel open = new CoreLabel(); open.setWord("<open>");
    CoreLabel text = new CoreLabel(); text.setWord("text");
    new CleanXmlAnnotator(".*", "", null, false).process(Arrays.asList(open, text));
  }

  @Test
  public void flawedModeRecoversFromMismatchedAndUnmatchedTags() {
    CoreLabel openA = new CoreLabel(); openA.setWord("<a>");
    CoreLabel openB = new CoreLabel(); openB.setWord("<b>");
    CoreLabel text = new CoreLabel(); text.setWord("text");
    CoreLabel closeA = new CoreLabel(); closeA.setWord("</a>");
    CoreLabel orphan = new CoreLabel(); orphan.setWord("</orphan>");

    List<CoreLabel> result = new CleanXmlAnnotator(".*", "", null, true).process(
        Arrays.asList(openA, openB, text, closeA, orphan));

    assertEquals(Collections.singletonList(text), result);
    assertEquals(Arrays.asList("a", "b"), text.get(CoreAnnotations.XmlContextAnnotation.class));
  }

  @Test
  public void sentenceEndingAndSingleSentenceTagsMarkBoundaries() {
    Properties properties = new Properties();
    properties.setProperty("clean.sentenceendingtags", "p|br");
    properties.setProperty("clean.singlesentencetags", "headline");
    CoreLabel p = new CoreLabel(); p.setWord("<p>");
    CoreLabel first = new CoreLabel(); first.setWord("first");
    CoreLabel br = new CoreLabel(); br.setWord("<br/>");
    CoreLabel closeP = new CoreLabel(); closeP.setWord("</p>");
    CoreLabel headline = new CoreLabel(); headline.setWord("<headline>");
    CoreLabel title1 = new CoreLabel(); title1.setWord("Big");
    CoreLabel title2 = new CoreLabel(); title2.setWord("News");
    CoreLabel closeHeadline = new CoreLabel(); closeHeadline.setWord("</headline>");

    List<CoreLabel> result = new CleanXmlAnnotator(properties).process(
        Arrays.asList(p, first, br, closeP, headline, title1, title2, closeHeadline));

    assertEquals(Arrays.asList(first, title1, title2), result);
    assertEquals(Boolean.TRUE, first.get(CoreAnnotations.ForcedSentenceEndAnnotation.class));
    assertEquals(Boolean.TRUE, title1.get(CoreAnnotations.ForcedSentenceUntilEndAnnotation.class));
    assertNull(title2.get(CoreAnnotations.ForcedSentenceUntilEndAnnotation.class));
    assertEquals(Boolean.TRUE, title2.get(CoreAnnotations.ForcedSentenceEndAnnotation.class));
  }

  @Test
  public void annotateExtractsDocumentTextMetadataAndReindexesTokens() {
    Properties properties = new Properties();
    properties.setProperty("clean.docAnnotations", "docID=doc[id],doctype=doc[kind],docsourcetype=doc[source]");
    CoreLabel doc = new CoreLabel(); doc.setWord("<doc id=\"D-7\" kind=\"article\" source=\"wire\">");
    CoreLabel idOpen = new CoreLabel(); idOpen.setWord("<docid>");
    CoreLabel id = new CoreLabel(); id.setWord("content-id");
    CoreLabel idClose = new CoreLabel(); idClose.setWord("</docid>");
    CoreLabel dateOpen = new CoreLabel(); dateOpen.setWord("<date>");
    CoreLabel date = new CoreLabel(); date.setWord("2026-10-18");
    CoreLabel dateClose = new CoreLabel(); dateClose.setWord("</date>");
    CoreLabel typeOpen = new CoreLabel(); typeOpen.setWord("<doctype>");
    CoreLabel type = new CoreLabel(); type.setWord("news wire");
    CoreLabel typeClose = new CoreLabel(); typeClose.setWord("</doctype>");
    CoreLabel body = new CoreLabel(); body.setWord("Body");
    CoreLabel closeDoc = new CoreLabel(); closeDoc.setWord("</doc>");
    Annotation annotation = new Annotation();
    annotation.set(CoreAnnotations.TokensAnnotation.class,
        Arrays.asList(doc, idOpen, id, idClose, dateOpen, date, dateClose, typeOpen, type, typeClose, body, closeDoc));

    new CleanXmlAnnotator(properties).annotate(annotation);

    List<CoreLabel> result = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(Arrays.asList(id, date, type, body), result);
    assertEquals("content-id", annotation.get(CoreAnnotations.DocIDAnnotation.class));
    assertEquals("news wire", annotation.get(CoreAnnotations.DocTypeAnnotation.class));
    assertEquals("wire", annotation.get(CoreAnnotations.DocSourceTypeAnnotation.class));
    assertEquals("2026-10-18", annotation.get(CoreAnnotations.DocDateAnnotation.class));
    assertEquals(Integer.valueOf(0), id.get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals(Integer.valueOf(1), id.get(CoreAnnotations.TokenEndAnnotation.class));
    assertEquals(Integer.valueOf(3), body.get(CoreAnnotations.TokenBeginAnnotation.class));
    assertEquals(Integer.valueOf(4), body.get(CoreAnnotations.TokenEndAnnotation.class));
  }

  @Test
  public void documentContentMetadataUsesOriginalTextOffsets() {
    String text = "<date>October 18, 2026</date>";
    CoreLabel open = new CoreLabel(); open.setWord("<date>"); open.setBeginPosition(0); open.setEndPosition(6);
    CoreLabel october = new CoreLabel(); october.setWord("October"); october.setBeginPosition(6); october.setEndPosition(13);
    CoreLabel day = new CoreLabel(); day.setWord("18"); day.setBeginPosition(14); day.setEndPosition(16);
    CoreLabel year = new CoreLabel(); year.setWord("2026"); year.setBeginPosition(18); year.setEndPosition(22);
    CoreLabel close = new CoreLabel(); close.setWord("</date>"); close.setBeginPosition(22); close.setEndPosition(29);
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(open, october, day, year, close));

    new CleanXmlAnnotator().annotate(annotation);

    assertEquals("October 18, 2026", annotation.get(CoreAnnotations.DocDateAnnotation.class));
    assertEquals(Arrays.asList(october, day, year), annotation.get(CoreAnnotations.TokensAnnotation.class));
  }

  @Test
  public void tokenAttributeAnnotationsNestAndRestoreOuterValue() {
    Properties properties = new Properties();
    properties.setProperty("clean.tokenAnnotations", "author=span[who]");
    CoreLabel outer = new CoreLabel(); outer.setWord("<span who=\"outer\">");
    CoreLabel a = new CoreLabel(); a.setWord("A");
    CoreLabel inner = new CoreLabel(); inner.setWord("<span who=\"inner\">");
    CoreLabel b = new CoreLabel(); b.setWord("B");
    CoreLabel closeInner = new CoreLabel(); closeInner.setWord("</span>");
    CoreLabel c = new CoreLabel(); c.setWord("C");
    CoreLabel closeOuter = new CoreLabel(); closeOuter.setWord("</span>");
    CoreLabel d = new CoreLabel(); d.setWord("D");

    List<CoreLabel> result = new CleanXmlAnnotator(properties).process(
        Arrays.asList(outer, a, inner, b, closeInner, c, closeOuter, d));

    assertEquals(Arrays.asList(a, b, c, d), result);
    assertEquals("outer", a.get(CoreAnnotations.AuthorAnnotation.class));
    assertEquals("inner", b.get(CoreAnnotations.AuthorAnnotation.class));
    assertEquals("outer", c.get(CoreAnnotations.AuthorAnnotation.class));
    assertNull(d.get(CoreAnnotations.AuthorAnnotation.class));
  }

  @Test
  public void discourseTagsAssignSpeakerUtteranceAndMentionData() {
    Properties properties = new Properties();
    properties.setProperty("clean.turntags", "turn");
    properties.setProperty("clean.speakertags", "speaker");
    CoreLabel turn = new CoreLabel(); turn.setWord("<turn>");
    CoreLabel speaker = new CoreLabel(); speaker.setWord("<speaker>");
    CoreLabel jane = new CoreLabel(); jane.setWord("Jane");
    CoreLabel doe = new CoreLabel(); doe.setWord("Doe");
    CoreLabel closeSpeaker = new CoreLabel(); closeSpeaker.setWord("</speaker>");
    CoreLabel says = new CoreLabel(); says.setWord("Hello");
    CoreLabel closeTurn = new CoreLabel(); closeTurn.setWord("</turn>");
    CoreLabel narration = new CoreLabel(); narration.setWord("Narration");

    List<CoreLabel> result = new CleanXmlAnnotator(properties).process(
        Arrays.asList(turn, speaker, jane, doe, closeSpeaker, says, closeTurn, narration));

    assertEquals(Arrays.asList(jane, doe, says, narration), result);
    assertEquals(Integer.valueOf(1), jane.get(CoreAnnotations.UtteranceAnnotation.class));
    assertEquals("Jane Doe", jane.get(CoreAnnotations.SpeakerAnnotation.class));
    assertEquals("Jane Doe", doe.get(CoreAnnotations.SpeakerAnnotation.class));
    assertNotNull(jane.get(CoreAnnotations.MentionTokenAnnotation.class));
    assertNotNull(doe.get(CoreAnnotations.MentionTokenAnnotation.class));
    assertEquals("Jane Doe", says.get(CoreAnnotations.SpeakerAnnotation.class));
    assertEquals(Integer.valueOf(1), says.get(CoreAnnotations.UtteranceAnnotation.class));
    assertNull(narration.get(CoreAnnotations.SpeakerAnnotation.class));
    assertNull(narration.get(CoreAnnotations.UtteranceAnnotation.class));
    assertEquals(Boolean.TRUE, says.get(CoreAnnotations.ForcedSentenceEndAnnotation.class));
  }

  @Test
  public void sectionCapturesAttributesTextDateQuoteAndOffsets() {
    Properties properties = new Properties();
    properties.setProperty("clean.sectiontags", "post");
    properties.setProperty("clean.sectionAnnotations", "author=post[user],sectionDate=date");
    properties.setProperty("clean.quotetags", "quote");
    properties.setProperty("clean.quoteauthorattributes", "by,author");
    CoreLabel post = new CoreLabel(); post.setWord("<post user=\"Alice\">"); post.setBeginPosition(0); post.setEndPosition(19);
    CoreLabel dateOpen = new CoreLabel(); dateOpen.setWord("<date>"); dateOpen.setBeginPosition(19); dateOpen.setEndPosition(25);
    CoreLabel date = new CoreLabel(); date.setWord("Monday"); date.setBeginPosition(25); date.setEndPosition(31);
    CoreLabel dateClose = new CoreLabel(); dateClose.setWord("</date>"); dateClose.setBeginPosition(31); dateClose.setEndPosition(38);
    CoreLabel quoteOpen = new CoreLabel(); quoteOpen.setWord("<quote by=\"Bob\">"); quoteOpen.setBeginPosition(38); quoteOpen.setEndPosition(54);
    CoreLabel quoted = new CoreLabel(); quoted.setWord("Hi"); quoted.setBeginPosition(54); quoted.setEndPosition(56);
    CoreLabel quoteClose = new CoreLabel(); quoteClose.setWord("</quote>"); quoteClose.setBeginPosition(56); quoteClose.setEndPosition(64);
    CoreLabel closePost = new CoreLabel(); closePost.setWord("</post>"); closePost.setBeginPosition(64); closePost.setEndPosition(71);
    Annotation annotation = new Annotation();
    annotation.set(CoreAnnotations.TokensAnnotation.class,
        Arrays.asList(post, dateOpen, date, dateClose, quoteOpen, quoted, quoteClose, closePost));

    new CleanXmlAnnotator(properties).annotate(annotation);

    List<CoreMap> sections = annotation.get(CoreAnnotations.SectionsAnnotation.class);
    assertEquals(1, sections.size());
    CoreMap section = sections.get(0);
    assertEquals(Integer.valueOf(25), section.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertEquals(Integer.valueOf(56), section.get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
    assertEquals("Alice", section.get(CoreAnnotations.AuthorAnnotation.class));
    assertEquals("Monday", section.get(CoreAnnotations.SectionDateAnnotation.class));
    assertSame(post, section.get(CoreAnnotations.SectionTagAnnotation.class));
    assertEquals(Integer.valueOf(12), section.get(CoreAnnotations.SectionAuthorCharacterOffsetBeginAnnotation.class));
    assertEquals(Integer.valueOf(17), section.get(CoreAnnotations.SectionAuthorCharacterOffsetEndAnnotation.class));
    assertNotNull(date.get(CoreAnnotations.SectionStartAnnotation.class));
    assertNull(quoted.get(CoreAnnotations.SectionStartAnnotation.class));
    assertEquals(Boolean.TRUE, quoted.get(CoreAnnotations.ForcedSentenceEndAnnotation.class));
    assertEquals("post", quoted.get(CoreAnnotations.SectionEndAnnotation.class));
    List<CoreMap> quotes = section.get(CoreAnnotations.QuotesAnnotation.class);
    assertEquals(1, quotes.size());
    assertEquals(Integer.valueOf(38), quotes.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertEquals(Integer.valueOf(64), quotes.get(0).get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
    assertEquals("Bob", quotes.get(0).get(CoreAnnotations.AuthorAnnotation.class));
  }

  @Test
  public void emptySectionProducesSentinelOffsets() {
    Properties properties = new Properties();
    properties.setProperty("clean.sectiontags", "post");
    CoreLabel open = new CoreLabel(); open.setWord("<post>"); open.setBeginPosition(4); open.setEndPosition(10);
    CoreLabel image = new CoreLabel(); image.setWord("<img/>"); image.setBeginPosition(10); image.setEndPosition(16);
    CoreLabel close = new CoreLabel(); close.setWord("</post>"); close.setBeginPosition(16); close.setEndPosition(23);
    Annotation annotation = new Annotation();
    annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(open, image, close));

    new CleanXmlAnnotator(properties).annotate(annotation);

    CoreMap section = annotation.get(CoreAnnotations.SectionsAnnotation.class).get(0);
    assertEquals(Integer.valueOf(-1), section.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertEquals(Integer.valueOf(-1), section.get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
    assertTrue(annotation.get(CoreAnnotations.TokensAnnotation.class).isEmpty());
  }

  @Test
  public void discardedSectionTokenDoesNotBecomeSectionStart() {
    Properties properties = new Properties();
    properties.setProperty("clean.sectiontags", "post");
    properties.setProperty("clean.ssplitDiscardTokens", "\\*NL\\*");
    CoreLabel open = new CoreLabel(); open.setWord("<post>"); open.setBeginPosition(0); open.setEndPosition(6);
    CoreLabel newline = new CoreLabel(); newline.setWord("*NL*"); newline.setBeginPosition(6); newline.setEndPosition(10);
    CoreLabel content = new CoreLabel(); content.setWord("content"); content.setBeginPosition(10); content.setEndPosition(17);
    CoreLabel close = new CoreLabel(); close.setWord("</post>"); close.setBeginPosition(17); close.setEndPosition(24);
    Annotation annotation = new Annotation();
    annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(open, newline, content, close));

    new CleanXmlAnnotator(properties).annotate(annotation);

    CoreMap section = annotation.get(CoreAnnotations.SectionsAnnotation.class).get(0);
    assertEquals(Integer.valueOf(10), section.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertNull(newline.get(CoreAnnotations.SectionStartAnnotation.class));
    assertNotNull(content.get(CoreAnnotations.SectionStartAnnotation.class));
  }

  @Test
  public void invertibleTokenWhitespaceAbsorbsRemovedMarkup() {
    CoreLabel first = new CoreLabel(); first.setWord("A"); first.setOriginalText("A"); first.setBefore(""); first.setAfter("");
    CoreLabel tag = new CoreLabel(); tag.setWord("<b>"); tag.setOriginalText("<b>"); tag.setBefore(" "); tag.setAfter("");
    CoreLabel second = new CoreLabel(); second.setWord("B"); second.setOriginalText("B"); second.setBefore(" "); second.setAfter("");
    CoreLabel close = new CoreLabel(); close.setWord("</b>"); close.setOriginalText("</b>"); close.setBefore(""); close.setAfter(" tail");

    List<CoreLabel> result = new CleanXmlAnnotator().process(Arrays.asList(first, tag, second, close));

    assertEquals(Arrays.asList(first, second), result);
    assertEquals(" <b> ", second.before());
    assertEquals(" <b> ", first.after());
    assertEquals("</b> tail", second.after());
  }

  @Test
  public void annotateWithoutTokensIsNoOp() {
    Annotation annotation = new Annotation("plain text");
    new CleanXmlAnnotator().annotate(annotation);
    assertFalse(annotation.containsKey(CoreAnnotations.TokensAnnotation.class));
    assertNull(annotation.get(CoreAnnotations.SectionsAnnotation.class));
  }

  @Test
  public void emptyTokenListIsHandledAndCreatesEmptySectionsList() {
    Annotation annotation = new Annotation();
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<CoreLabel>());
    new CleanXmlAnnotator().annotate(annotation);
    assertTrue(annotation.get(CoreAnnotations.TokensAnnotation.class).isEmpty());
    assertTrue(annotation.get(CoreAnnotations.SectionsAnnotation.class).isEmpty());
  }

  @Test
  public void requirementContractsAreExposed() {
    CleanXmlAnnotator annotator = new CleanXmlAnnotator();
    assertEquals(Collections.singleton(CoreAnnotations.TokensAnnotation.class), annotator.requires());
    assertTrue(annotator.requirementsSatisfied().isEmpty());
  }

  @Test(expected = IllegalArgumentException.class)
  public void malformedAnnotationMappingIsRejected() {
    Properties properties = new Properties();
    properties.setProperty("clean.tokenAnnotations", "author");
    new CleanXmlAnnotator(properties);
  }

  @Test(expected = IllegalArgumentException.class)
  public void unknownAnnotationKeyIsRejected() {
    CleanXmlAnnotator annotator = new CleanXmlAnnotator();
    annotator.setSectionAnnotationPatterns("definitelyUnknownKey=post[user]");
  }

  @Test(expected = IllegalArgumentException.class)
  public void tokenAnnotationRequiresAnAttributePattern() {
    CleanXmlAnnotator annotator = new CleanXmlAnnotator();
    annotator.setTokenAnnotationPatterns("author=post");
  }

  @Test
  public void settersReplacePriorConfigurationAndMatchCaseInsensitively() {
    CleanXmlAnnotator annotator = new CleanXmlAnnotator();
    annotator.setTokenAnnotationPatterns("author=ITEM[OWNER]");
    annotator.setDiscourseTags(null, null);
    annotator.setDocIdTagMatcher(null);
    annotator.setDocTypeTagMatcher(null);
    annotator.setSingleSentenceTagMatcher(null);
    annotator.setSectionTagMatcher(null);
    annotator.setQuoteTagMatcher(null);
    annotator.setSsplitDiscardTokensMatcher(null);
    CoreLabel open = new CoreLabel(); open.setWord("<item owner=\"Casey\">");
    CoreLabel text = new CoreLabel(); text.setWord("text");
    CoreLabel close = new CoreLabel(); close.setWord("</item>");

    List<CoreLabel> result = annotator.process(Arrays.asList(open, text, close));

    assertEquals(Collections.singletonList(text), result);
    assertEquals("Casey", text.get(CoreAnnotations.AuthorAnnotation.class));
  }
}
