package org.cogcomp.md;

import edu.illinois.cs.cogcomp.core.datastructures.IntPair;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import org.junit.Test;

import static org.junit.Assert.*;

public class MentionAnnotatorPromptTest {

    @Test
    public void defaultConstructorDeclaresMentionViewAndPosDependency() {
        MentionAnnotator annotator = new MentionAnnotator();

        assertEquals("MENTION", annotator.getViewName());
        assertArrayEquals(new String[] {"POS"}, annotator.getRequiredViews());
        assertFalse(annotator.isInitialized());
    }

    @Test
    public void modeConstructorRemainsLazyForAceType() {
        MentionAnnotator annotator = new MentionAnnotator("ACE_TYPE");

        assertEquals("MENTION", annotator.getViewName());
        assertArrayEquals(new String[] {"POS"}, annotator.getRequiredViews());
        assertFalse(annotator.isInitialized());
    }

    @Test
    public void booleanConstructorRemainsLazyWhenRequested() {
        MentionAnnotator annotator = new MentionAnnotator(true, "ERE_NONTYPE");

        assertEquals("MENTION", annotator.getViewName());
        assertArrayEquals(new String[] {"POS"}, annotator.getRequiredViews());
        assertFalse(annotator.isInitialized());
    }

    @Test
    public void explicitModelPathConstructorDeclaresSamePublicContract() {
        MentionAnnotator annotator = new MentionAnnotator(
                "models/name", "models/nominal", "models/pronoun", "models/extent", "custom");

        assertEquals("MENTION", annotator.getViewName());
        assertArrayEquals(new String[] {"POS"}, annotator.getRequiredViews());
        assertFalse(annotator.isInitialized());
    }

    @Test
    public void missingBothHeadAttributesReturnsNull() {
        TextAnnotation text = new TextAnnotation("corpus", "doc", "Ada wrote code",
                new IntPair[] {new IntPair(0, 3), new IntPair(4, 9), new IntPair(10, 14)},
                new String[] {"Ada", "wrote", "code"}, new int[] {3});
        Constituent extent = new Constituent("NAM", "MENTION", text, 0, 3);

        assertNull(MentionAnnotator.getHeadConstituent(extent, "HEAD"));
    }

    @Test
    public void missingHeadStartAttributeReturnsNull() {
        TextAnnotation text = new TextAnnotation("corpus", "doc", "the engineer",
                new IntPair[] {new IntPair(0, 3), new IntPair(4, 12)},
                new String[] {"the", "engineer"}, new int[] {2});
        Constituent extent = new Constituent("NOM", "MENTION", text, 0, 2);
        extent.addAttribute("EntityHeadEndSpan", "2");

        assertNull(MentionAnnotator.getHeadConstituent(extent, "HEAD"));
    }

    @Test
    public void missingHeadEndAttributeReturnsNull() {
        TextAnnotation text = new TextAnnotation("corpus", "doc", "she arrived",
                new IntPair[] {new IntPair(0, 3), new IntPair(4, 11)},
                new String[] {"she", "arrived"}, new int[] {2});
        Constituent extent = new Constituent("PRO", "MENTION", text, 0, 1);
        extent.addAttribute("EntityHeadStartSpan", "0");

        assertNull(MentionAnnotator.getHeadConstituent(extent, "HEAD"));
    }

    @Test
    public void extractsSingleTokenHeadFromMultiTokenExtent() {
        TextAnnotation text = new TextAnnotation("news", "42", "the senior engineer arrived",
                new IntPair[] {new IntPair(0, 3), new IntPair(4, 10), new IntPair(11, 19),
                        new IntPair(20, 27)},
                new String[] {"the", "senior", "engineer", "arrived"}, new int[] {4});
        Constituent extent = new Constituent("PERSON", "MENTION", text, 0, 3);
        extent.addAttribute("EntityHeadStartSpan", "2");
        extent.addAttribute("EntityHeadEndSpan", "3");

        Constituent head = MentionAnnotator.getHeadConstituent(extent, "HEAD");

        assertNotNull(head);
        assertEquals(2, head.getStartSpan());
        assertEquals(3, head.getEndSpan());
        assertEquals("engineer", head.toString());
    }

    @Test
    public void extractsMultiTokenHeadAtBeginningOfExtent() {
        TextAnnotation text = new TextAnnotation("news", "43", "New York officials spoke",
                new IntPair[] {new IntPair(0, 3), new IntPair(4, 8), new IntPair(9, 18),
                        new IntPair(19, 24)},
                new String[] {"New", "York", "officials", "spoke"}, new int[] {4});
        Constituent extent = new Constituent("GPE", "MENTION", text, 0, 3);
        extent.addAttribute("EntityHeadStartSpan", "0");
        extent.addAttribute("EntityHeadEndSpan", "2");

        Constituent head = MentionAnnotator.getHeadConstituent(extent, "ENTITY_HEAD");

        assertEquals(0, head.getStartSpan());
        assertEquals(2, head.getEndSpan());
        assertEquals("New York", head.toString());
    }

    @Test
    public void preservesLabelRequestedViewAndTextAnnotationIdentity() {
        TextAnnotation text = new TextAnnotation("ace", "doc-7", "President Ada spoke",
                new IntPair[] {new IntPair(0, 9), new IntPair(10, 13), new IntPair(14, 19)},
                new String[] {"President", "Ada", "spoke"}, new int[] {3});
        Constituent extent = new Constituent("PER", "FULL_EXTENT", text, 0, 2);
        extent.addAttribute("EntityHeadStartSpan", "1");
        extent.addAttribute("EntityHeadEndSpan", "2");

        Constituent head = MentionAnnotator.getHeadConstituent(extent, "CUSTOM_HEAD_VIEW");

        assertEquals("PER", head.getLabel());
        assertEquals("CUSTOM_HEAD_VIEW", head.getViewName());
        assertSame(text, head.getTextAnnotation());
        assertNotSame(extent, head);
    }

    @Test
    public void copiesHeadMetadataAndAdditionalAttributes() {
        TextAnnotation text = new TextAnnotation("ere", "doc-8", "an experienced pilot",
                new IntPair[] {new IntPair(0, 2), new IntPair(3, 14), new IntPair(15, 20)},
                new String[] {"an", "experienced", "pilot"}, new int[] {3});
        Constituent extent = new Constituent("PERSON", "MENTION", text, 0, 3);
        extent.addAttribute("EntityHeadStartSpan", "2");
        extent.addAttribute("EntityHeadEndSpan", "3");
        extent.addAttribute("EntityMentionType", "NOM");
        extent.addAttribute("source", "joint-inference");

        Constituent head = MentionAnnotator.getHeadConstituent(extent, "HEAD");

        assertEquals("2", head.getAttribute("EntityHeadStartSpan"));
        assertEquals("3", head.getAttribute("EntityHeadEndSpan"));
        assertEquals("NOM", head.getAttribute("EntityMentionType"));
        assertEquals("joint-inference", head.getAttribute("source"));
        assertEquals(4, head.getAttributeKeys().size());
    }

    @Test
    public void returnedHeadHasIndependentAttributeStorage() {
        TextAnnotation text = new TextAnnotation("ace", "doc-9", "Dr Ada",
                new IntPair[] {new IntPair(0, 2), new IntPair(3, 6)},
                new String[] {"Dr", "Ada"}, new int[] {2});
        Constituent extent = new Constituent("PER", "MENTION", text, 0, 2);
        extent.addAttribute("EntityHeadStartSpan", "1");
        extent.addAttribute("EntityHeadEndSpan", "2");
        extent.addAttribute("confidence-band", "high");

        Constituent head = MentionAnnotator.getHeadConstituent(extent, "HEAD");
        head.addAttribute("confidence-band", "reviewed");
        head.addAttribute("head-only", "true");

        assertEquals("high", extent.getAttribute("confidence-band"));
        assertNull(extent.getAttribute("head-only"));
        assertEquals("reviewed", head.getAttribute("confidence-band"));
    }

    @Test
    public void headMayEqualEntireExtent() {
        TextAnnotation text = new TextAnnotation("ace", "doc-10", "Acme Corporation",
                new IntPair[] {new IntPair(0, 4), new IntPair(5, 16)},
                new String[] {"Acme", "Corporation"}, new int[] {2});
        Constituent extent = new Constituent("ORG", "MENTION", text, 0, 2);
        extent.addAttribute("EntityHeadStartSpan", "0");
        extent.addAttribute("EntityHeadEndSpan", "2");

        Constituent head = MentionAnnotator.getHeadConstituent(extent, "HEAD");

        assertEquals(0, head.getStartSpan());
        assertEquals(2, head.getEndSpan());
        assertEquals("Acme Corporation", head.toString());
    }

    @Test
    public void zeroLengthHeadSpanIsRetained() {
        TextAnnotation text = new TextAnnotation("synthetic", "doc-11", "one two three",
                new IntPair[] {new IntPair(0, 3), new IntPair(4, 7), new IntPair(8, 13)},
                new String[] {"one", "two", "three"}, new int[] {3});
        Constituent extent = new Constituent("EMPTY", "MENTION", text, 0, 3);
        extent.addAttribute("EntityHeadStartSpan", "1");
        extent.addAttribute("EntityHeadEndSpan", "1");

        Constituent head = MentionAnnotator.getHeadConstituent(extent, "HEAD");

        assertEquals(1, head.getStartSpan());
        assertEquals(1, head.getEndSpan());
        assertEquals(0, head.getNumberOfTokens());
        assertEquals("", head.toString());
    }

    @Test
    public void requestedEmptyViewNameIsUsedVerbatim() {
        TextAnnotation text = new TextAnnotation("synthetic", "doc-12", "Ada",
                new IntPair[] {new IntPair(0, 3)}, new String[] {"Ada"}, new int[] {1});
        Constituent extent = new Constituent("PER", "MENTION", text, 0, 1);
        extent.addAttribute("EntityHeadStartSpan", "0");
        extent.addAttribute("EntityHeadEndSpan", "1");

        Constituent head = MentionAnnotator.getHeadConstituent(extent, "");

        assertEquals("", head.getViewName());
        assertEquals("Ada", head.toString());
    }

    @Test(expected = NumberFormatException.class)
    public void nonNumericHeadStartIsRejected() {
        TextAnnotation text = new TextAnnotation("bad", "doc-13", "Ada spoke",
                new IntPair[] {new IntPair(0, 3), new IntPair(4, 9)},
                new String[] {"Ada", "spoke"}, new int[] {2});
        Constituent extent = new Constituent("PER", "MENTION", text, 0, 1);
        extent.addAttribute("EntityHeadStartSpan", "first");
        extent.addAttribute("EntityHeadEndSpan", "1");

        MentionAnnotator.getHeadConstituent(extent, "HEAD");
    }

    @Test(expected = NumberFormatException.class)
    public void nonNumericHeadEndIsRejected() {
        TextAnnotation text = new TextAnnotation("bad", "doc-14", "Ada spoke",
                new IntPair[] {new IntPair(0, 3), new IntPair(4, 9)},
                new String[] {"Ada", "spoke"}, new int[] {2});
        Constituent extent = new Constituent("PER", "MENTION", text, 0, 1);
        extent.addAttribute("EntityHeadStartSpan", "0");
        extent.addAttribute("EntityHeadEndSpan", "1.0");

        MentionAnnotator.getHeadConstituent(extent, "HEAD");
    }

    @Test(expected = NumberFormatException.class)
    public void whitespacePaddedHeadOffsetIsRejected() {
        TextAnnotation text = new TextAnnotation("bad", "doc-15", "Ada spoke",
                new IntPair[] {new IntPair(0, 3), new IntPair(4, 9)},
                new String[] {"Ada", "spoke"}, new int[] {2});
        Constituent extent = new Constituent("PER", "MENTION", text, 0, 1);
        extent.addAttribute("EntityHeadStartSpan", " 0");
        extent.addAttribute("EntityHeadEndSpan", "1");

        MentionAnnotator.getHeadConstituent(extent, "HEAD");
    }

    @Test(expected = NullPointerException.class)
    public void nullExtentIsRejected() {
        MentionAnnotator.getHeadConstituent(null, "HEAD");
    }
}
