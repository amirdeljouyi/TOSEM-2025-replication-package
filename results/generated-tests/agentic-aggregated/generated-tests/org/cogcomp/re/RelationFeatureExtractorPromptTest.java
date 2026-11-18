package org.cogcomp.re;

import edu.illinois.cs.cogcomp.core.datastructures.IntPair;
import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Relation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.SpanLabelView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TokenLabelView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TreeView;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class RelationFeatureExtractorPromptTest {

    @Test
    public void nounClassificationRecognizesAllDocumentedFamilies() {
        assertTrue(RelationFeatureExtractor.isNoun("NN"));
        assertTrue(RelationFeatureExtractor.isNoun("NNS"));
        assertTrue(RelationFeatureExtractor.isNoun("NNP"));
        assertTrue(RelationFeatureExtractor.isNoun("RB"));
        assertTrue(RelationFeatureExtractor.isNoun("RBR"));
        assertTrue(RelationFeatureExtractor.isNoun("WP"));
        assertTrue(RelationFeatureExtractor.isNoun("WP$"));
        assertFalse(RelationFeatureExtractor.isNoun("JJ"));
        assertFalse(RelationFeatureExtractor.isNoun("IN"));
        assertFalse(RelationFeatureExtractor.isNoun(""));
    }

    @Test(expected = NullPointerException.class)
    public void nounClassificationRejectsNull() {
        RelationFeatureExtractor.isNoun(null);
    }

    @Test
    public void predictedAndUnannotatedMentionsRemainTheirOwnHeads() {
        TextAnnotation ta = new TextAnnotation("c", "d", "New York",
                new IntPair[]{new IntPair(0, 3), new IntPair(4, 8)},
                new String[]{"New", "York"}, new int[]{2});
        Constituent predicted = new Constituent("GPE", "MENTION", ta, 0, 2);
        predicted.addAttribute("IsPredicted", "true");
        Constituent plain = new Constituent("GPE", "MENTION", ta, 0, 2);

        assertSame(predicted,
                RelationFeatureExtractor.getEntityHeadForConstituent(predicted, ta, "HEAD"));
        assertSame(plain,
                RelationFeatureExtractor.getEntityHeadForConstituent(plain, ta, "HEAD"));
    }

    @Test
    public void tokenSpanHeadMetadataProducesNewHeadAndCopiesAttributes() {
        TextAnnotation ta = new TextAnnotation("c", "d", "the United Nations",
                new IntPair[]{new IntPair(0, 3), new IntPair(4, 10), new IntPair(11, 18)},
                new String[]{"the", "United", "Nations"}, new int[]{3});
        Constituent extent = new Constituent("ORG", "MENTION", ta, 0, 3);
        extent.addAttribute("EntityHeadStartSpan", "1");
        extent.addAttribute("EntityHeadEndSpan", "3");
        extent.addAttribute("EntityType", "ORG");

        Constituent head = RelationFeatureExtractor.getEntityHeadForConstituent(extent, ta, "HEADS");

        assertNotSame(extent, head);
        assertEquals(1, head.getStartSpan());
        assertEquals(3, head.getEndSpan());
        assertEquals("United Nations", head.toString());
        assertEquals("HEADS", head.getViewName());
        assertEquals("ORG", head.getAttribute("EntityType"));
    }

    @Test
    public void characterOffsetMetadataMapsToTokensAndCopiesAttributes() {
        TextAnnotation ta = new TextAnnotation("c", "d", "President Barack Obama spoke",
                new IntPair[]{new IntPair(0, 9), new IntPair(10, 16),
                        new IntPair(17, 22), new IntPair(23, 28)},
                new String[]{"President", "Barack", "Obama", "spoke"}, new int[]{4});
        Constituent extent = new Constituent("PER", "MENTION", ta, 0, 3);
        extent.addAttribute("EntityHeadStartCharOffset", "10");
        extent.addAttribute("EntityHeadEndCharOffset", "22");
        extent.addAttribute("EntityID", "E7");

        Constituent head = RelationFeatureExtractor.getEntityHeadForConstituent(extent, ta, "ACE_HEAD");

        assertEquals(1, head.getStartSpan());
        assertEquals(3, head.getEndSpan());
        assertEquals("Barack Obama", head.toString());
        assertEquals("E7", head.getAttribute("EntityID"));
        assertEquals("ACE_HEAD", head.getViewName());
    }

    @Test
    public void outOfRangeCharacterHeadReturnsNull() {
        TextAnnotation ta = new TextAnnotation("c", "d", "Alice left",
                new IntPair[]{new IntPair(0, 5), new IntPair(6, 10)},
                new String[]{"Alice", "left"}, new int[]{2});
        Constituent extent = new Constituent("PER", "MENTION", ta, 0, 1);
        extent.addAttribute("EntityHeadStartCharOffset", "5");
        extent.addAttribute("EntityHeadEndCharOffset", "6");

        assertNull(RelationFeatureExtractor.getEntityHeadForConstituent(extent, ta, "HEAD"));
    }

    @Test
    public void lexicalMentionBagsRespectSourceAndTargetExtents() {
        TextAnnotation ta = new TextAnnotation("c", "d", "Dr. Alice joined Acme Corp.",
                new IntPair[]{new IntPair(0, 3), new IntPair(4, 9), new IntPair(10, 16),
                        new IntPair(17, 21), new IntPair(22, 27)},
                new String[]{"Dr.", "Alice", "joined", "Acme", "Corp."}, new int[]{5});
        Constituent source = new Constituent("PER", "MENTION", ta, 0, 2);
        Constituent target = new Constituent("ORG", "MENTION", ta, 3, 5);
        Relation relation = new Relation("works-for", source, target, 1.0);
        RelationFeatureExtractor extractor = new RelationFeatureExtractor();

        assertEquals(Arrays.asList("Dr.", "Alice"), extractor.getLexicalFeaturePartA(relation));
        assertEquals(Arrays.asList("Acme", "Corp."), extractor.getLexicalFeaturePartB(relation));
    }

    @Test
    public void singleWordFeatureHandlesBothOrdersAndNonSingletonGaps() {
        TextAnnotation ta = new TextAnnotation("c", "d", "Alice at Acme in Paris",
                new IntPair[]{new IntPair(0, 5), new IntPair(6, 8), new IntPair(9, 13),
                        new IntPair(14, 16), new IntPair(17, 22)},
                new String[]{"Alice", "at", "Acme", "in", "Paris"}, new int[]{5});
        Constituent alice = new Constituent("PER", "MENTION", ta, 0, 1);
        Constituent acme = new Constituent("ORG", "MENTION", ta, 2, 3);
        Constituent paris = new Constituent("GPE", "MENTION", ta, 4, 5);
        Relation forward = new Relation("r", alice, acme, 1.0);
        Relation reversed = new Relation("r", acme, alice, 1.0);
        Relation wide = new Relation("r", alice, paris, 1.0);
        RelationFeatureExtractor extractor = new RelationFeatureExtractor();

        assertEquals(Collections.singletonList("singleword_at"),
                extractor.getLexicalFeaturePartC(forward));
        assertEquals(Collections.singletonList("singleword_at"),
                extractor.getLexicalFeaturePartC(reversed));
        assertEquals(Collections.singletonList("No_singleword"),
                extractor.getLexicalFeaturePartC(wide));
    }

    @Test
    public void wordsBetweenHeadsUseHeadMetadataAndArgumentOrder() {
        TextAnnotation ta = new TextAnnotation("c", "d", "the president of large Acme Corporation",
                new IntPair[]{new IntPair(0, 3), new IntPair(4, 13), new IntPair(14, 16),
                        new IntPair(17, 22), new IntPair(23, 27), new IntPair(28, 39)},
                new String[]{"the", "president", "of", "large", "Acme", "Corporation"}, new int[]{6});
        Constituent person = new Constituent("PER", "MENTION", ta, 0, 2);
        person.addAttribute("EntityHeadStartSpan", "1");
        person.addAttribute("EntityHeadEndSpan", "2");
        Constituent company = new Constituent("ORG", "MENTION", ta, 4, 6);
        company.addAttribute("EntityHeadStartSpan", "4");
        company.addAttribute("EntityHeadEndSpan", "5");
        Relation forward = new Relation("r", person, company, 1.0);
        Relation reverse = new Relation("r", company, person, 1.0);
        RelationFeatureExtractor extractor = new RelationFeatureExtractor();

        assertEquals(Arrays.asList("bowbethead_of", "bowbethead_large"),
                extractor.getLexicalFeaturePartCC(forward));
        assertEquals(Arrays.asList("bowbethead_of", "bowbethead_large"),
                extractor.getLexicalFeaturePartCC(reverse));
    }

    @Test
    public void betweenMentionFeaturesIncludeEdgesAndInteriorWords() {
        TextAnnotation ta = new TextAnnotation("c", "d", "Alice was appointed by Acme",
                new IntPair[]{new IntPair(0, 5), new IntPair(6, 9), new IntPair(10, 19),
                        new IntPair(20, 22), new IntPair(23, 27)},
                new String[]{"Alice", "was", "appointed", "by", "Acme"}, new int[]{5});
        Constituent alice = new Constituent("PER", "MENTION", ta, 0, 1);
        Constituent acme = new Constituent("ORG", "MENTION", ta, 4, 5);
        Relation forward = new Relation("r", alice, acme, 1.0);
        Relation reverse = new Relation("r", acme, alice, 1.0);
        Relation adjacent = new Relation("r", alice,
                new Constituent("TITLE", "MENTION", ta, 1, 2), 1.0);
        RelationFeatureExtractor extractor = new RelationFeatureExtractor();

        List<String> expected = Arrays.asList(
                "between_first_was", "between_first_by", "in_between_appointed");
        assertEquals(expected, extractor.getLexicalFeaturePartD(forward));
        assertEquals(expected, extractor.getLexicalFeaturePartD(reverse));
        assertEquals(Collections.singletonList("No_between_features"),
                extractor.getLexicalFeaturePartD(adjacent));
    }

    @Test
    public void sentenceBoundaryContextProducesTokensAndNullMarkers() {
        TextAnnotation ta = new TextAnnotation("c", "d", "Yesterday Alice met Bob outside",
                new IntPair[]{new IntPair(0, 9), new IntPair(10, 15), new IntPair(16, 19),
                        new IntPair(20, 23), new IntPair(24, 31)},
                new String[]{"Yesterday", "Alice", "met", "Bob", "outside"}, new int[]{5});
        Constituent alice = new Constituent("PER", "MENTION", ta, 1, 2);
        Constituent bob = new Constituent("PER", "MENTION", ta, 3, 4);
        Relation relation = new Relation("r", alice, bob, 1.0);
        RelationFeatureExtractor extractor = new RelationFeatureExtractor();

        assertEquals(Arrays.asList("fwM1_Yesterday", "swM1_NULL", "fwM2_outside", "swM2_NULL"),
                extractor.getLexicalFeaturePartE(relation));

        Constituent first = new Constituent("DATE", "MENTION", ta, 0, 1);
        Constituent last = new Constituent("LOC", "MENTION", ta, 4, 5);
        assertEquals(Arrays.asList("fwM1_NULL", "swM1_NULL", "fwM2_NULL", "swM2_NULL"),
                extractor.getLexicalFeaturePartE(new Relation("r", first, last, 1.0)));
    }

    @Test
    public void headFormsAndCollocationsReflectInternalHeadPosition() {
        TextAnnotation ta = new TextAnnotation("c", "d", "former President Obama met the Acme chief",
                new IntPair[]{new IntPair(0, 6), new IntPair(7, 16), new IntPair(17, 22),
                        new IntPair(23, 26), new IntPair(27, 30), new IntPair(31, 35),
                        new IntPair(36, 41)},
                new String[]{"former", "President", "Obama", "met", "the", "Acme", "chief"}, new int[]{7});
        Constituent person = new Constituent("PER", "MENTION", ta, 0, 3);
        person.addAttribute("EntityHeadStartSpan", "2");
        person.addAttribute("EntityHeadEndSpan", "3");
        Constituent org = new Constituent("ORG", "MENTION", ta, 4, 7);
        org.addAttribute("EntityHeadStartSpan", "5");
        org.addAttribute("EntityHeadEndSpan", "6");
        Relation relation = new Relation("r", person, org, 1.0);
        RelationFeatureExtractor extractor = new RelationFeatureExtractor();

        assertEquals(Arrays.asList("HM1_Obama", "HM2_Acme", "HM12_Obama_Acme"),
                extractor.getLexicalFeaturePartF(relation));
        assertEquals(Arrays.asList(
                        "s_m1_p1_PresidentObama", "s_m2_m1_formerPresident", "s_p1_p2_",
                        "s_m1_m1_President", "s_p1_p1_null",
                        "t_m1_p1_theAcmechief", "t_m2_m1_the", "t_p1_p2_chief",
                        "t_m1_m1_the", "t_p1_p1_chief"),
                extractor.getCollocationsFeature(relation));
    }

    @Test
    public void possessiveRecognizesApostropheSInsideContainingMention() {
        TextAnnotation ta = new TextAnnotation("c", "d", "Alice 's company",
                new IntPair[]{new IntPair(0, 5), new IntPair(6, 8), new IntPair(9, 16)},
                new String[]{"Alice", "'s", "company"}, new int[]{3});
        TokenLabelView pos = new TokenLabelView("POS", ta);
        pos.addTokenLabel(0, "NNP", 1.0);
        pos.addTokenLabel(1, "POS", 1.0);
        pos.addTokenLabel(2, "NN", 1.0);
        ta.addView("POS", pos);
        Constituent alice = new Constituent("PER", "MENTION", ta, 0, 1);
        alice.addAttribute("IsPredicted", "true");
        Constituent phrase = new Constituent("ORG", "MENTION", ta, 0, 3);
        phrase.addAttribute("EntityHeadStartSpan", "2");
        phrase.addAttribute("EntityHeadEndSpan", "3");

        assertTrue(RelationFeatureExtractor.isPossessive(
                new Relation("poss", alice, phrase, 1.0)));
    }

    @Test
    public void disjointMentionsAreNotPossessiveEvenWithPossessiveTagNearby() {
        TextAnnotation ta = new TextAnnotation("c", "d", "Alice 's company",
                new IntPair[]{new IntPair(0, 5), new IntPair(6, 8), new IntPair(9, 16)},
                new String[]{"Alice", "'s", "company"}, new int[]{3});
        TokenLabelView pos = new TokenLabelView("POS", ta);
        pos.addTokenLabel(0, "NNP", 1.0);
        pos.addTokenLabel(1, "POS", 1.0);
        pos.addTokenLabel(2, "NN", 1.0);
        ta.addView("POS", pos);
        Constituent alice = new Constituent("PER", "MENTION", ta, 0, 1);
        alice.addAttribute("IsPredicted", "true");
        Constituent company = new Constituent("ORG", "MENTION", ta, 2, 3);
        company.addAttribute("IsPredicted", "true");

        assertFalse(RelationFeatureExtractor.isPossessive(
                new Relation("poss", alice, company, 1.0)));
    }

    @Test
    public void prepositionRequiresPrepositionWithoutInterveningNoun() {
        TextAnnotation ta = new TextAnnotation("c", "d", "Alice works at Acme",
                new IntPair[]{new IntPair(0, 5), new IntPair(6, 11), new IntPair(12, 14),
                        new IntPair(15, 19)},
                new String[]{"Alice", "works", "at", "Acme"}, new int[]{4});
        TokenLabelView pos = new TokenLabelView("POS", ta);
        pos.addTokenLabel(0, "NNP", 1.0);
        pos.addTokenLabel(1, "VBZ", 1.0);
        pos.addTokenLabel(2, "IN", 1.0);
        pos.addTokenLabel(3, "NNP", 1.0);
        ta.addView("POS", pos);
        Constituent alice = new Constituent("PER", "MENTION", ta, 0, 1);
        alice.addAttribute("IsPredicted", "true");
        Constituent acme = new Constituent("ORG", "MENTION", ta, 3, 4);
        acme.addAttribute("IsPredicted", "true");
        Relation relation = new Relation("r", alice, acme, 1.0);

        assertTrue(RelationFeatureExtractor.isPreposition(relation));

        TextAnnotation blockedTa = new TextAnnotation("c", "d", "Alice manager at Acme",
                new IntPair[]{new IntPair(0, 5), new IntPair(6, 13), new IntPair(14, 16),
                        new IntPair(17, 21)},
                new String[]{"Alice", "manager", "at", "Acme"}, new int[]{4});
        TokenLabelView blockedPos = new TokenLabelView("POS", blockedTa);
        blockedPos.addTokenLabel(0, "NNP", 1.0);
        blockedPos.addTokenLabel(1, "NN", 1.0);
        blockedPos.addTokenLabel(2, "IN", 1.0);
        blockedPos.addTokenLabel(3, "NNP", 1.0);
        blockedTa.addView("POS", blockedPos);
        Constituent blockedAlice = new Constituent("PER", "MENTION", blockedTa, 0, 1);
        blockedAlice.addAttribute("IsPredicted", "true");
        Constituent blockedAcme = new Constituent("ORG", "MENTION", blockedTa, 3, 4);
        blockedAcme.addAttribute("IsPredicted", "true");
        assertFalse(RelationFeatureExtractor.isPreposition(
                new Relation("r", blockedAlice, blockedAcme, 1.0)));
    }

    @Test
    public void onlyNounBetweenAcceptsEmptyAndNounOnlyIntervals() {
        TextAnnotation ta = new TextAnnotation("c", "d", "Acme research lab director",
                new IntPair[]{new IntPair(0, 4), new IntPair(5, 13), new IntPair(14, 17),
                        new IntPair(18, 26)},
                new String[]{"Acme", "research", "lab", "director"}, new int[]{4});
        TokenLabelView pos = new TokenLabelView("POS", ta);
        pos.addTokenLabel(0, "NNP", 1.0);
        pos.addTokenLabel(1, "NN", 1.0);
        pos.addTokenLabel(2, "NN", 1.0);
        pos.addTokenLabel(3, "NN", 1.0);
        ta.addView("POS", pos);
        Constituent acme = new Constituent("ORG", "MENTION", ta, 0, 1);
        Constituent director = new Constituent("PER", "MENTION", ta, 3, 4);
        Constituent adjacent = new Constituent("TITLE", "MENTION", ta, 1, 2);

        assertTrue(RelationFeatureExtractor.onlyNounBetween(acme, director));
        assertTrue(RelationFeatureExtractor.onlyNounBetween(acme, adjacent));

        TextAnnotation ta2 = new TextAnnotation("c", "d", "Acme senior director",
                new IntPair[]{new IntPair(0, 4), new IntPair(5, 11), new IntPair(12, 20)},
                new String[]{"Acme", "senior", "director"}, new int[]{3});
        TokenLabelView pos2 = new TokenLabelView("POS", ta2);
        pos2.addTokenLabel(0, "NNP", 1.0);
        pos2.addTokenLabel(1, "JJ", 1.0);
        pos2.addTokenLabel(2, "NN", 1.0);
        ta2.addView("POS", pos2);
        assertFalse(RelationFeatureExtractor.onlyNounBetween(
                new Constituent("ORG", "MENTION", ta2, 0, 1),
                new Constituent("PER", "MENTION", ta2, 2, 3)));
    }

    @Test
    public void formulaicStructureUsesEntityTypesAndInterveningPos() {
        TextAnnotation ta = new TextAnnotation("c", "d", "Alice executive Acme",
                new IntPair[]{new IntPair(0, 5), new IntPair(6, 15), new IntPair(16, 20)},
                new String[]{"Alice", "executive", "Acme"}, new int[]{3});
        TokenLabelView pos = new TokenLabelView("POS", ta);
        pos.addTokenLabel(0, "NNP", 1.0);
        pos.addTokenLabel(1, "NN", 1.0);
        pos.addTokenLabel(2, "NNP", 1.0);
        ta.addView("POS", pos);
        Constituent alice = new Constituent("PER", "MENTION", ta, 0, 1);
        alice.addAttribute("IsPredicted", "true");
        alice.addAttribute("EntityType", "PER");
        Constituent acme = new Constituent("ORG", "MENTION", ta, 2, 3);
        acme.addAttribute("IsPredicted", "true");
        acme.addAttribute("EntityType", "ORG");

        assertTrue(RelationFeatureExtractor.isFormulaic(
                new Relation("r", alice, acme, 1.0)));

        acme.addAttribute("EntityType", "PER");
        assertFalse(RelationFeatureExtractor.isFormulaic(
                new Relation("r", alice, acme, 1.0)));
    }

    @Test
    public void premodifierRecognizesHeadInsideOverlappingLargerExtent() {
        TextAnnotation ta = new TextAnnotation("c", "d", "former Acme director",
                new IntPair[]{new IntPair(0, 6), new IntPair(7, 11), new IntPair(12, 20)},
                new String[]{"former", "Acme", "director"}, new int[]{3});
        TokenLabelView pos = new TokenLabelView("POS", ta);
        pos.addTokenLabel(0, "JJ", 1.0);
        pos.addTokenLabel(1, "NNP", 1.0);
        pos.addTokenLabel(2, "NN", 1.0);
        ta.addView("POS", pos);
        Constituent acme = new Constituent("ORG", "MENTION", ta, 1, 2);
        acme.addAttribute("IsPredicted", "true");
        Constituent full = new Constituent("PER", "MENTION", ta, 0, 3);
        full.addAttribute("EntityHeadStartSpan", "2");
        full.addAttribute("EntityHeadEndSpan", "3");

        assertTrue(RelationFeatureExtractor.isPremodifier(
                new Relation("r", acme, full, 1.0)));
    }

    @Test
    public void structuralFeaturesCountMiddleMentionsAndReportCoverage() {
        TextAnnotation ta = new TextAnnotation("c", "d", "Alice manager at Acme",
                new IntPair[]{new IntPair(0, 5), new IntPair(6, 13), new IntPair(14, 16),
                        new IntPair(17, 21)},
                new String[]{"Alice", "manager", "at", "Acme"}, new int[]{4});
        SpanLabelView mentions = new SpanLabelView("MENTION", ta);
        mentions.addSpanLabel(0, 1, "PER", 1.0);
        mentions.addSpanLabel(1, 2, "TITLE", 1.0);
        mentions.addSpanLabel(3, 4, "ORG", 1.0);
        ta.addView("MENTION", mentions);
        Constituent alice = new Constituent("PER", "MENTION", ta, 0, 1);
        alice.addAttribute("EnityType", "PER");
        alice.addAttribute("EntityType", "PER");
        Constituent acme = new Constituent("ORG", "MENTION", ta, 3, 4);
        acme.addAttribute("EntityType", "ORG");
        RelationFeatureExtractor extractor = new RelationFeatureExtractor();

        assertEquals(Arrays.asList("middle_mention_size_1", "middle_word_size_2",
                        "m1_m2_no_coverage", "cb1_PER_ORG_m1_m2_no_coverage"),
                extractor.getStructualFeature(new Relation("r", alice, acme, 1.0)));

        Constituent outer = new Constituent("ORG", "MENTION", ta, 0, 4);
        outer.addAttribute("EnityType", "ORG");
        outer.addAttribute("EntityType", "ORG");
        assertEquals(Arrays.asList("middle_mention_size_null", "middle_word_size_null",
                        "m2_in_m1", "cb1_ORG_PER_m2_in_m1"),
                extractor.getStructualFeature(new Relation("r", outer, alice, 1.0)));
    }

    @Test
    public void mentionFeaturesCombineTypesLevelsContainmentAndCoreference() {
        TextAnnotation ta = new TextAnnotation("c", "d", "President Alice",
                new IntPair[]{new IntPair(0, 9), new IntPair(10, 15)},
                new String[]{"President", "Alice"}, new int[]{2});
        Constituent outer = new Constituent("PER", "MENTION", ta, 0, 2);
        outer.addAttribute("EntityMentionType", "NOM");
        outer.addAttribute("EntityType", "PER");
        outer.addAttribute("EntityID", "E1");
        Constituent inner = new Constituent("PER", "MENTION", ta, 1, 2);
        inner.addAttribute("EntityMentionType", "NAM");
        inner.addAttribute("EntityType", "PER");
        inner.addAttribute("EntityID", "E1");
        Relation relation = new Relation("coref", outer, inner, 1.0);
        RelationFeatureExtractor extractor = new RelationFeatureExtractor();

        assertEquals(Arrays.asList("source_mtype_PER", "target_mtype_PER", "mlvl_NOM_NAM",
                        "mt_PER_PER", "mlvl_mt_NOM_PER_NAM_PER", "mlvl_cont_2_NOM_NAM_True"),
                extractor.getMentionFeature(relation));
        assertEquals("TRUE", extractor.getCorefTag(relation));

        inner.addAttribute("EntityID", "E2");
        assertEquals("FALSE", extractor.getCorefTag(relation));
    }

    @Test
    public void shallowParseFeaturesDescribeHeadAndExtentIntervals() {
        TextAnnotation ta = new TextAnnotation("c", "d", "Alice works at Acme",
                new IntPair[]{new IntPair(0, 5), new IntPair(6, 11), new IntPair(12, 14),
                        new IntPair(15, 19)},
                new String[]{"Alice", "works", "at", "Acme"}, new int[]{4});
        SpanLabelView chunks = new SpanLabelView("SHALLOW_PARSE", ta);
        chunks.addSpanLabel(0, 1, "NP", 1.0);
        chunks.addSpanLabel(1, 2, "VP", 1.0);
        chunks.addSpanLabel(2, 3, "PP", 1.0);
        chunks.addSpanLabel(3, 4, "NP", 1.0);
        ta.addView("SHALLOW_PARSE", chunks);
        Constituent alice = new Constituent("PER", "MENTION", ta, 0, 1);
        alice.addAttribute("IsPredicted", "true");
        Constituent acme = new Constituent("ORG", "MENTION", ta, 3, 4);
        acme.addAttribute("IsPredicted", "true");
        List<Pair<String, String>> features = new RelationFeatureExtractor().getShallowParseFeature(
                new Relation("r", alice, acme, 1.0));

        assertEquals(2, features.size());
        assertEquals("chunker_between_heads_0", features.get(0).getFirst());
        assertEquals("VP", features.get(0).getSecond());
        assertEquals("chunker_between_extents_0", features.get(1).getFirst());
        assertEquals("VP", features.get(1).getSecond());
    }

    @Test
    public void shallowParseAndPatternRecognitionShortCircuitSameMentions() {
        TextAnnotation ta = new TextAnnotation("c", "d", "New York",
                new IntPair[]{new IntPair(0, 3), new IntPair(4, 8)},
                new String[]{"New", "York"}, new int[]{2});
        SpanLabelView chunks = new SpanLabelView("SHALLOW_PARSE", ta);
        chunks.addSpanLabel(0, 2, "NP", 1.0);
        ta.addView("SHALLOW_PARSE", chunks);
        Constituent source = new Constituent("GPE", "MENTION", ta, 0, 2);
        source.addAttribute("EntityHeadStartSpan", "1");
        source.addAttribute("EntityHeadEndSpan", "2");
        Constituent targetSameHead = new Constituent("LOC", "MENTION", ta, 1, 2);
        targetSameHead.addAttribute("IsPredicted", "true");

        assertTrue(new RelationFeatureExtractor().getShallowParseFeature(
                new Relation("r", source, targetSameHead, 1.0)).isEmpty());
        assertEquals(Collections.singletonList("SAME_SOURCE_TARGET_EXCEPTION"),
                RelationFeatureExtractor.patternRecognition(source, targetSameHead));

        Constituent sameExtentStart = new Constituent("GPE", "MENTION", ta, 0, 1);
        sameExtentStart.addAttribute("IsPredicted", "true");
        assertEquals(Collections.singletonList("SAME_SOURCE_TARGET_EXTENT_EXCEPTION"),
                RelationFeatureExtractor.patternRecognition(source, sameExtentStart));
    }

    @Test
    public void dependencyFeaturesAreEmptyWhenParserHasNoNodesForHeads() {
        TextAnnotation ta = new TextAnnotation("c", "d", "Alice Acme",
                new IntPair[]{new IntPair(0, 5), new IntPair(6, 10)},
                new String[]{"Alice", "Acme"}, new int[]{2});
        ta.addView("DEPENDENCY_STANFORD", new TreeView("DEPENDENCY_STANFORD", ta));
        ta.addView("RE_ANNOTATED", new SpanLabelView("RE_ANNOTATED", ta));
        ta.addView("POS", new TokenLabelView("POS", ta));
        Constituent alice = new Constituent("PER", "MENTION", ta, 0, 1);
        alice.addAttribute("IsPredicted", "true");
        Constituent acme = new Constituent("ORG", "MENTION", ta, 1, 2);
        acme.addAttribute("IsPredicted", "true");

        assertTrue(RelationFeatureExtractor.getDependencyFeature(
                new Relation("r", alice, acme, 1.0)).isEmpty());
    }

    @Test
    public void templateFeatureAggregatesFormulaicAndPrepositionSignals() {
        TextAnnotation ta = new TextAnnotation("c", "d", "Alice at Acme",
                new IntPair[]{new IntPair(0, 5), new IntPair(6, 8), new IntPair(9, 13)},
                new String[]{"Alice", "at", "Acme"}, new int[]{3});
        TokenLabelView pos = new TokenLabelView("POS", ta);
        pos.addTokenLabel(0, "NNP", 1.0);
        pos.addTokenLabel(1, "IN", 1.0);
        pos.addTokenLabel(2, "NNP", 1.0);
        ta.addView("POS", pos);
        Constituent alice = new Constituent("PER", "MENTION", ta, 0, 1);
        alice.addAttribute("IsPredicted", "true");
        alice.addAttribute("EntityType", "PER");
        Constituent acme = new Constituent("ORG", "MENTION", ta, 2, 3);
        acme.addAttribute("IsPredicted", "true");
        acme.addAttribute("EntityType", "ORG");

        assertEquals(Arrays.asList("is_formulaic_structure", "is_preposition_structure"),
                new RelationFeatureExtractor().getTemplateFeature(
                        new Relation("r", alice, acme, 1.0)));
    }
}
