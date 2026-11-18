package edu.illinois.cs.cogcomp.comma.datastructures;

import edu.illinois.cs.cogcomp.core.datastructures.IntPair;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.PredicateArgumentView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Relation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.SpanLabelView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TokenLabelView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TreeView;
import edu.illinois.cs.cogcomp.core.datastructures.trees.Tree;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

public class CommaPromptTest {

    @Test
    public void metadataLabelsAndAnnotatedTextAreExposed() throws Exception {
        TextAnnotation automatic = new TextAnnotation("news", "doc-17", "Alice , however , stayed",
                Collections.nCopies(5, new IntPair(0, 1)).toArray(new IntPair[0]),
                new String[] {"Alice", ",", "however", ",", "stayed"}, new int[] {5});
        TextAnnotation gold = new TextAnnotation("gold", "gold-17", "Alice , however , stayed",
                Collections.nCopies(5, new IntPair(0, 1)).toArray(new IntPair[0]),
                new String[] {"Alice", ",", "however", ",", "stayed"}, new int[] {5});
        CommaSRLSentence sentence = new CommaSRLSentence(automatic, gold);
        Comma comma = new Comma(1, sentence, Arrays.asList("Discourse", "Other"));

        assertEquals(1, comma.getPosition());
        assertEquals("Discourse", comma.getLabel());
        assertEquals(Arrays.asList("Discourse", "Other"), comma.getLabels());
        assertSame(sentence, comma.getSentence());
        assertSame(automatic, comma.getTextAnnotation(false));
        assertSame(gold, comma.getTextAnnotation(true));
        assertEquals("1 gold-17", comma.getCommaID());
        assertEquals("Alice ,[Discourse,Other,] however , stayed", comma.getAnnotatedText());
    }

    @Test
    public void wordLookupsHandleBothBoundariesAndOrdinaryDistances() {
        TextAnnotation ta = new TextAnnotation("c", "words", "Before very , after",
                Collections.nCopies(4, new IntPair(0, 1)).toArray(new IntPair[0]),
                new String[] {"Before", "very", ",", "after"}, new int[] {4});
        CommaSRLSentence sentence = new CommaSRLSentence(ta, ta);
        Comma comma = new Comma(2, sentence, Collections.singletonList("Other"));

        assertEquals("very", comma.getWordToLeft(1));
        assertEquals("Before", comma.getWordToLeft(2));
        assertEquals("$$$", comma.getWordToLeft(3));
        assertEquals("after", comma.getWordToRight(1));
        assertEquals("###", comma.getWordToRight(2));
        assertEquals("###", comma.getWordToRight(100));
    }

    @Test(expected = NullPointerException.class)
    public void unlabeledCommaRejectsSingularLabelLookup() {
        TextAnnotation ta = new TextAnnotation("c", "unlabeled", "A , B",
                Collections.nCopies(3, new IntPair(0, 1)).toArray(new IntPair[0]),
                new String[] {"A", ",", "B"}, new int[] {3});
        CommaSRLSentence sentence = new CommaSRLSentence(ta, ta);
        Comma comma = new Comma(1, sentence);

        comma.getLabel();
    }

    @Test
    public void posLookupsSwitchBetweenAutomaticAndGoldAndLexicalizeThe() {
        TextAnnotation automatic = new TextAnnotation("c", "auto", "We old , the plan",
                Collections.nCopies(5, new IntPair(0, 1)).toArray(new IntPair[0]),
                new String[] {"We", "old", ",", "the", "plan"}, new int[] {5});
        TextAnnotation gold = new TextAnnotation("c", "gold", "We old , the plan",
                Collections.nCopies(5, new IntPair(0, 1)).toArray(new IntPair[0]),
                new String[] {"We", "old", ",", "the", "plan"}, new int[] {5});
        TokenLabelView automaticPos = new TokenLabelView(ViewNames.POS, automatic);
        automaticPos.addTokenLabel(0, "PRP", 1.0);
        automaticPos.addTokenLabel(1, "DT", 1.0);
        automaticPos.addTokenLabel(2, ",", 1.0);
        automaticPos.addTokenLabel(3, "DT", 1.0);
        automaticPos.addTokenLabel(4, "NN", 1.0);
        automatic.addView(ViewNames.POS, automaticPos);
        TokenLabelView goldPos = new TokenLabelView(ViewNames.POS, gold);
        goldPos.addTokenLabel(0, "NNP", 1.0);
        goldPos.addTokenLabel(1, "JJ", 1.0);
        goldPos.addTokenLabel(2, ",", 1.0);
        goldPos.addTokenLabel(3, "NN", 1.0);
        goldPos.addTokenLabel(4, "VB", 1.0);
        gold.addView(ViewNames.POS, goldPos);
        Comma comma = new Comma(2, new CommaSRLSentence(automatic, gold),
                Collections.singletonList("Other"));

        Comma.useGoldFeatures(false);
        assertEquals("DT-the", comma.getPOSToLeft(1));
        assertEquals("DT-the", comma.getPOSToRight(1));
        assertEquals("PRP", comma.getPOSToLeft(2));
        assertEquals("NN", comma.getPOSToRight(2));
        Comma.useGoldFeatures(true);
        assertEquals("JJ", comma.getPOSToLeft(1));
        assertEquals("NN", comma.getPOSToRight(1));
        assertEquals("NNP", comma.getPOSToLeft(2));
        assertEquals("VB", comma.getPOSToRight(2));
        Comma.useGoldFeatures(false);
    }

    @Test
    public void posLookupReturnsEmptyLabelBeyondTheLeftBoundary() {
        TextAnnotation ta = new TextAnnotation("c", "pos-boundary", ", word",
                Collections.nCopies(2, new IntPair(0, 1)).toArray(new IntPair[0]),
                new String[] {",", "word"}, new int[] {2});
        TokenLabelView pos = new TokenLabelView(ViewNames.POS, ta);
        pos.addTokenLabel(0, ",", 1.0);
        pos.addTokenLabel(1, "NN", 1.0);
        ta.addView(ViewNames.POS, pos);
        Comma comma = new Comma(0, new CommaSRLSentence(ta, ta),
                Collections.singletonList("Other"));

        Comma.useGoldFeatures(false);
        assertEquals("", comma.getPOSToLeft(1));
    }

    @Test
    public void chunksAreOrderedAndInvalidDistancesReturnNull() {
        TextAnnotation ta = new TextAnnotation("c", "chunks", "The cat , quite quickly left .",
                Collections.nCopies(7, new IntPair(0, 1)).toArray(new IntPair[0]),
                new String[] {"The", "cat", ",", "quite", "quickly", "left", "."},
                new int[] {7});
        SpanLabelView chunks = new SpanLabelView(ViewNames.SHALLOW_PARSE, ta);
        Constituent left = chunks.addSpanLabel(0, 2, "NP", 1.0);
        Constituent rightSecond = chunks.addSpanLabel(5, 6, "VP", 1.0);
        Constituent rightFirst = chunks.addSpanLabel(3, 5, "ADVP", 1.0);
        ta.addView(ViewNames.SHALLOW_PARSE, chunks);
        Comma comma = new Comma(2, new CommaSRLSentence(ta, ta),
                Collections.singletonList("Other"));

        assertSame(left, comma.getChunkToLeftOfComma(1));
        assertSame(rightFirst, comma.getChunkToRightOfComma(1));
        assertSame(rightSecond, comma.getChunkToRightOfComma(2));
        assertNull(comma.getChunkToLeftOfComma(0));
        assertNull(comma.getChunkToLeftOfComma(2));
        assertNull(comma.getChunkToRightOfComma(-1));
        assertNull(comma.getChunkToRightOfComma(3));
    }

    @Test
    public void namedEntityAndNotationUseCoverageThresholds() {
        TextAnnotation ta = new TextAnnotation("c", "ner", "New York , thrives .",
                Collections.nCopies(5, new IntPair(0, 1)).toArray(new IntPair[0]),
                new String[] {"New", "York", ",", "thrives", "."}, new int[] {5});
        SpanLabelView ner = new SpanLabelView(ViewNames.NER_CONLL, "test", ta, 1.0, true);
        ner.addSpanLabel(0, 2, "LOCATION", 1.0);
        ner.addSpanLabel(0, 1, "MISC", 1.0);
        ta.addView(ViewNames.NER_CONLL, ner);
        Comma comma = new Comma(2, new CommaSRLSentence(ta, ta),
                Collections.singletonList("Other"));
        Constituent phrase = new Constituent("NP-SBJ", "custom", ta, 0, 2);
        Constituent uncovered = new Constituent("VP", "custom", ta, 3, 4);

        assertEquals("+LOCATION", comma.getNamedEntityTag(phrase));
        assertEquals("", comma.getNamedEntityTag(uncovered));
        assertEquals("NP-SBJ-+LOCATION", comma.getNotation(phrase));
        assertEquals("NP-+LOCATION", comma.getStrippedNotation(phrase));
        assertEquals("NULL", comma.getNotation(null));
        assertEquals("NULL", comma.getStrippedNotation(null));
    }

    @Test
    public void parseTreeSupportsCommaAndParentSiblingNavigation() {
        TextAnnotation ta = new TextAnnotation("c", "parse", "Alice , laughed .",
                Collections.nCopies(4, new IntPair(0, 1)).toArray(new IntPair[0]),
                new String[] {"Alice", ",", "laughed", "."}, new int[] {4});
        TreeView parse = new TreeView(ViewNames.PARSE_STANFORD, ta);
        parse.setParseTree(0, Tree.readTreeFromString(
                "(S (NP (NNP Alice)) (, ,) (VP (VBD laughed)) (. .))"));
        ta.addView(ViewNames.PARSE_STANFORD, parse);
        ta.addView(ViewNames.NER_CONLL, new SpanLabelView(ViewNames.NER_CONLL, ta));
        Comma comma = new Comma(1, new CommaSRLSentence(ta, ta),
                Collections.singletonList("Other"));

        Comma.useGoldFeatures(false);
        Constituent commaPhrase = comma.getCommaConstituentFromTree(parse);
        assertNotNull(commaPhrase);
        assertEquals(",", commaPhrase.getLabel());
        assertEquals("NP", comma.getPhraseToLeftOfComma(1).getLabel());
        assertEquals("VP", comma.getPhraseToRightOfComma(1).getLabel());
        assertSame(commaPhrase, comma.getSiblingToLeft(0, commaPhrase, parse));
        assertSame(commaPhrase, comma.getSiblingToRight(-2, commaPhrase, parse));
        assertNull(comma.getPhraseToLeftOfComma(2));
        assertNull(comma.getPhraseToRightOfComma(3));
        assertSame(parse.getRootConstituent(0), comma.getPhraseToLeftOfParent(0));
        assertNull(comma.getPhraseToLeftOfParent(1));
        assertNull(comma.getPhraseToRightOfParent(1));
        assertEquals(7, comma.getSiblingPhraseNgrams().length);
        assertEquals("N1NULLI0", comma.getSiblingPhraseNgrams()[0]);
        assertEquals(13, comma.getParentSiblingPhraseNgrams().length);
        assertEquals("N1NULLI0", comma.getParentSiblingPhraseNgrams()[0]);
    }

    @Test
    public void siblingCommasAndHeadFollowTheSharedParseParent() {
        TextAnnotation ta = new TextAnnotation("c", "siblings", "A , B , C .",
                Collections.nCopies(6, new IntPair(0, 1)).toArray(new IntPair[0]),
                new String[] {"A", ",", "B", ",", "C", "."}, new int[] {6});
        TreeView parse = new TreeView(ViewNames.PARSE_STANFORD, ta);
        parse.setParseTree(0, Tree.readTreeFromString(
                "(S (NP (NN A)) (, ,) (NP (NN B)) (, ,) (VP (VB C)) (. .))"));
        ta.addView(ViewNames.PARSE_STANFORD, parse);
        CommaSRLSentence sentence = new CommaSRLSentence(ta, ta);
        Comma first = sentence.getCommas().get(0);
        Comma second = sentence.getCommas().get(1);

        Comma.useGoldFeatures(false);
        assertTrue(first.isSibling(second));
        assertTrue(second.isSibling(first));
        assertEquals(2, first.getSiblingCommas().size());
        assertSame(first, first.getSiblingCommas().get(0));
        assertSame(second, first.getSiblingCommas().get(1));
        assertSame(first, second.getSiblingCommaHead());
    }

    @Test
    public void dependencyMethodsReportOnlyRelationsCrossingTheComma() {
        TextAnnotation ta = new TextAnnotation("c", "deps", "left side , right edge",
                Collections.nCopies(5, new IntPair(0, 1)).toArray(new IntPair[0]),
                new String[] {"left", "side", ",", "right", "edge"}, new int[] {5});
        TreeView dependencies = new TreeView(ViewNames.DEPENDENCY_STANFORD, ta);
        Constituent left = new Constituent("left-node", ViewNames.DEPENDENCY_STANFORD, ta, 0, 1);
        Constituent nearLeft = new Constituent("near-left", ViewNames.DEPENDENCY_STANFORD, ta, 1, 2);
        Constituent right = new Constituent("right-node", ViewNames.DEPENDENCY_STANFORD, ta, 3, 4);
        dependencies.addConstituent(left);
        dependencies.addConstituent(nearLeft);
        dependencies.addConstituent(right);
        dependencies.addRelation(new Relation("outward", left, right, 1.0));
        dependencies.addRelation(new Relation("inward", right, nearLeft, 1.0));
        dependencies.addRelation(new Relation("left-only", left, nearLeft, 1.0));
        ta.addView(ViewNames.DEPENDENCY_STANFORD, dependencies);
        Comma comma = new Comma(2, new CommaSRLSentence(ta, ta),
                Collections.singletonList("Other"));

        assertArrayEquals(new String[] {"outward"}, comma.getLeftToRightDependencies());
        assertArrayEquals(new String[] {"inward"}, comma.getRightToLeftDependencies());
    }

    @Test
    public void containingSrlsCombineVerbNominalAndPrepositionViews() {
        TextAnnotation ta = new TextAnnotation("c", "srl", "They spoke , after lunch .",
                Collections.nCopies(6, new IntPair(0, 1)).toArray(new IntPair[0]),
                new String[] {"They", "spoke", ",", "after", "lunch", "."}, new int[] {6});
        PredicateArgumentView verbs = new PredicateArgumentView(ViewNames.SRL_VERB, ta);
        Constituent spoke = new Constituent("Predicate", ViewNames.SRL_VERB, ta, 1, 2);
        spoke.addAttribute(PredicateArgumentView.LemmaIdentifier, "speak");
        Constituent verbArg = new Constituent("Argument", ViewNames.SRL_VERB, ta, 3, 5);
        verbs.addPredicateArguments(spoke, Collections.singletonList(verbArg),
                new String[] {"A1"}, new double[] {1.0});
        ta.addView(ViewNames.SRL_VERB, verbs);
        PredicateArgumentView nominals = new PredicateArgumentView(ViewNames.SRL_NOM, ta);
        Constituent lunch = new Constituent("Predicate", ViewNames.SRL_NOM, ta, 4, 5);
        Constituent nominalArg = new Constituent("Argument", ViewNames.SRL_NOM, ta, 0, 2);
        nominals.addPredicateArguments(lunch, Collections.singletonList(nominalArg),
                new String[] {"A0"}, new double[] {1.0});
        ta.addView(ViewNames.SRL_NOM, nominals);
        PredicateArgumentView prepositions = new PredicateArgumentView(ViewNames.SRL_PREP, ta);
        Constituent after = new Constituent("Predicate", ViewNames.SRL_PREP, ta, 3, 4);
        Constituent prepArg = new Constituent("Argument", ViewNames.SRL_PREP, ta, 4, 5);
        prepositions.addPredicateArguments(after, Collections.singletonList(prepArg),
                new String[] {"OBJ"}, new double[] {1.0});
        ta.addView(ViewNames.SRL_PREP, prepositions);
        Comma comma = new Comma(2, new CommaSRLSentence(ta, ta),
                Collections.singletonList("Other"));

        Comma.useGoldFeatures(false);
        assertEquals(Arrays.asList("speakA1", "afterOBJ"), comma.getContainingSRLs());
    }

    @Test
    public void wordAndChunkNgramsPreserveWindowOrderAndNullSlots() {
        TextAnnotation ta = new TextAnnotation("c", "ngrams", "One small , clear example .",
                Collections.nCopies(6, new IntPair(0, 1)).toArray(new IntPair[0]),
                new String[] {"One", "small", ",", "clear", "example", "."}, new int[] {6});
        SpanLabelView chunks = new SpanLabelView(ViewNames.SHALLOW_PARSE, ta);
        chunks.addSpanLabel(0, 2, "NP", 1.0);
        chunks.addSpanLabel(3, 5, "NP", 1.0);
        ta.addView(ViewNames.SHALLOW_PARSE, chunks);
        SpanLabelView ner = new SpanLabelView(ViewNames.NER_CONLL, ta);
        ta.addView(ViewNames.NER_CONLL, ner);
        Comma comma = new Comma(2, new CommaSRLSentence(ta, ta),
                Collections.singletonList("Other"));

        assertArrayEquals(new String[] {"N1OneI0", "N1smallI1", "N1clearI2", "N1exampleI3"},
                comma.getWordNgrams());
        assertArrayEquals(new String[] {"N1NULLI0", "N1NP-I1", "N1NP-I2", "N1NULLI3",
                        "N2NULL@NP-I0", "N2NP-@NP-I1", "N2NP-@NULLI2"},
                comma.getChunkNgrams());
    }

    @Test
    public void posNgramsCoverEverySizeFromTwoThroughFive() {
        TextAnnotation ta = new TextAnnotation("c", "pos-ngrams", "a b c d e , f g h i j",
                Collections.nCopies(11, new IntPair(0, 1)).toArray(new IntPair[0]),
                new String[] {"a", "b", "c", "d", "e", ",", "f", "g", "h", "i", "j"},
                new int[] {11});
        TokenLabelView pos = new TokenLabelView(ViewNames.POS, ta);
        pos.addTokenLabel(0, "P0", 1.0);
        pos.addTokenLabel(1, "P1", 1.0);
        pos.addTokenLabel(2, "P2", 1.0);
        pos.addTokenLabel(3, "P3", 1.0);
        pos.addTokenLabel(4, "P4", 1.0);
        pos.addTokenLabel(5, ",", 1.0);
        pos.addTokenLabel(6, "P6", 1.0);
        pos.addTokenLabel(7, "P7", 1.0);
        pos.addTokenLabel(8, "P8", 1.0);
        pos.addTokenLabel(9, "P9", 1.0);
        pos.addTokenLabel(10, "P10", 1.0);
        ta.addView(ViewNames.POS, pos);
        Comma comma = new Comma(5, new CommaSRLSentence(ta, ta),
                Collections.singletonList("Other"));

        Comma.useGoldFeatures(false);
        String[] result = comma.getPOSNgrams();
        assertEquals(30, result.length);
        assertEquals("N2P0@P1I0", result[0]);
        assertEquals("N2P9@P10I8", result[8]);
        assertEquals("N3P0@P1@P2I0", result[9]);
        assertEquals("N4P0@P1@P2@P3I0", result[17]);
        assertEquals("N5P6@P7@P8@P9@P10I5", result[29]);
    }

    @Test
    public void bayraktarPatternUsesTheSelectedParseTree() {
        TextAnnotation ta = new TextAnnotation("c", "bayraktar", "Alice , and Bob left .",
                Collections.nCopies(6, new IntPair(0, 1)).toArray(new IntPair[0]),
                new String[] {"Alice", ",", "and", "Bob", "left", "."}, new int[] {6});
        TreeView parse = new TreeView(ViewNames.PARSE_STANFORD, ta);
        parse.setParseTree(0, Tree.readTreeFromString(
                "(S (NP (NNP Alice)) (, ,) (CC and) (NP (NNP Bob)) (VP (VBD left)) (. .))"));
        ta.addView(ViewNames.PARSE_STANFORD, parse);
        Comma comma = new Comma(1, new CommaSRLSentence(ta, ta),
                Collections.singletonList("Other"));

        Comma.useGoldFeatures(false);
        assertEquals("S --> NP , A NP VP .", comma.getBayraktarPattern(parse));
        assertEquals(comma.getBayraktarPattern(parse), comma.getBayraktarPattern());
    }

    @Test
    public void commaRoundTripsThroughJavaSerialization() throws Exception {
        TextAnnotation ta = new TextAnnotation("corpus", "serialized", "Save , then load",
                Collections.nCopies(4, new IntPair(0, 1)).toArray(new IntPair[0]),
                new String[] {"Save", ",", "then", "load"}, new int[] {4});
        Comma original = new Comma(1, new CommaSRLSentence(ta, ta),
                Collections.singletonList("Result"));
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream output = new ObjectOutputStream(bytes);
        output.writeObject(original);
        output.close();
        ObjectInputStream input = new ObjectInputStream(
                new ByteArrayInputStream(bytes.toByteArray()));
        Comma restored = (Comma) input.readObject();
        input.close();

        assertEquals(original.getPosition(), restored.getPosition());
        assertEquals(original.getLabels(), restored.getLabels());
        assertEquals(original.getCommaID(), restored.getCommaID());
        assertEquals(original.getAnnotatedText(), restored.getAnnotatedText());
    }
}
