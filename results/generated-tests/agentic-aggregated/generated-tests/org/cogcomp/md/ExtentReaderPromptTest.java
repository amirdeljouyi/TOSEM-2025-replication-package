package org.cogcomp.md;

import edu.illinois.cs.cogcomp.core.datastructures.IntPair;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Relation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class ExtentReaderPromptTest {

    @Test
    public void getIdRemovesUnixDirectorySeparators() throws Exception {
        ExtentReader reader = new ExtentReader("/data/train/fold/0/", "IGNORED") {
            @Override
            public List<TextAnnotation> getTextAnnotations() {
                return Collections.emptyList();
            }

            @Override
            public List<Relation> getPairs() {
                return Collections.emptyList();
            }
        };

        assertEquals("datatrainfold0", reader.getId());
    }

    @Test
    public void getIdRemovesWindowsDirectorySeparators() throws Exception {
        ExtentReader reader = new ExtentReader("C:\\corpora\\ace\\train", "IGNORED") {
            @Override
            public List<TextAnnotation> getTextAnnotations() {
                return Collections.emptyList();
            }

            @Override
            public List<Relation> getPairs() {
                return Collections.emptyList();
            }
        };

        assertEquals("C:corporaacetrain", reader.getId());
    }

    @Test
    public void getIdRemovesMixedRepeatedSeparatorsOnly() throws Exception {
        ExtentReader reader = new ExtentReader("//root\\\\a-b_c.1/", "IGNORED") {
            @Override
            public List<TextAnnotation> getTextAnnotations() {
                return Collections.emptyList();
            }

            @Override
            public List<Relation> getPairs() {
                return Collections.emptyList();
            }
        };

        assertEquals("roota-b_c.1", reader.getId());
    }

    @Test
    public void getIdPreservesWhitespaceAndUnicode() throws Exception {
        ExtentReader reader = new ExtentReader(" résumé /資料 ", "IGNORED") {
            @Override
            public List<TextAnnotation> getTextAnnotations() {
                return Collections.emptyList();
            }

            @Override
            public List<Relation> getPairs() {
                return Collections.emptyList();
            }
        };

        assertEquals(" résumé 資料 ", reader.getId());
    }

    @Test
    public void getIdAllowsEmptyPath() throws Exception {
        ExtentReader reader = new ExtentReader("", "IGNORED") {
            @Override
            public List<TextAnnotation> getTextAnnotations() {
                return Collections.emptyList();
            }

            @Override
            public List<Relation> getPairs() {
                return Collections.emptyList();
            }
        };

        assertEquals("", reader.getId());
    }

    @Test
    public void unsupportedCorpusProducesNoTextAnnotations() throws Exception {
        ExtentReader reader = new ExtentReader("unused", "NOT_A_CORPUS") {
            @Override
            public List<Relation> getPairs() {
                return Collections.emptyList();
            }
        };

        List<TextAnnotation> annotations = reader.getTextAnnotations();

        assertNotNull(annotations);
        assertTrue(annotations.isEmpty());
    }

    @Test
    public void corpusSelectionIsCaseSensitive() throws Exception {
        ExtentReader reader = new ExtentReader("unused", "ace") {
            @Override
            public List<Relation> getPairs() {
                return Collections.emptyList();
            }
        };

        assertTrue(reader.getTextAnnotations().isEmpty());
    }

    @Test
    public void malformedCombinedCorpusWithoutSegmentsFailsFast() throws Exception {
        try {
            new ExtentReader("unused", "COMBINED");
            fail("A COMBINED corpus name without its required segments must fail");
        } catch (ArrayIndexOutOfBoundsException expected) {
            assertNotNull(expected);
        }
    }

    @Test
    public void malformedCombinedCorpusWithNonNumericFoldFailsFast() throws Exception {
        try {
            new ExtentReader("unused", "COMBINED-ACE-TRAIN-not-a-number");
            fail("A nonnumeric COMBINED fold must fail");
        } catch (NumberFormatException expected) {
            assertEquals("For input string: \"not\"", expected.getMessage());
        }
    }

    @Test
    public void emptyPairSequenceReturnsNullEveryTime() throws Exception {
        ExtentReader reader = new ExtentReader("unused", "IGNORED") {
            @Override
            public List<TextAnnotation> getTextAnnotations() {
                return Collections.emptyList();
            }

            @Override
            public List<Relation> getPairs() {
                return Collections.emptyList();
            }
        };

        assertNull(reader.next());
        assertNull(reader.next());
        reader.reset();
        assertNull(reader.next());
    }

    @Test
    public void nextReturnsSingleRelationThenEndMarker() throws Exception {
        TextAnnotation ta = new TextAnnotation("corpus", "doc", "Alice arrived",
                new IntPair[] {new IntPair(0, 5), new IntPair(6, 13)},
                new String[] {"Alice", "arrived"}, new int[] {2});
        Constituent source = new Constituent("token", "test", ta, 0, 1);
        Constituent target = new Constituent("person", "test", ta, 0, 1);
        Relation relation = new Relation("true", source, target, 1.0);
        ExtentReader reader = new ExtentReader("unused", "IGNORED") {
            @Override
            public List<TextAnnotation> getTextAnnotations() {
                return Collections.emptyList();
            }

            @Override
            public List<Relation> getPairs() {
                return Collections.singletonList(relation);
            }
        };

        assertSame(relation, reader.next());
        assertNull(reader.next());
        assertNull(reader.next());
    }

    @Test
    public void nextPreservesRelationOrderAndObjectIdentity() throws Exception {
        TextAnnotation ta = new TextAnnotation("corpus", "doc", "The president spoke",
                new IntPair[] {new IntPair(0, 3), new IntPair(4, 13), new IntPair(14, 19)},
                new String[] {"The", "president", "spoke"}, new int[] {3});
        Constituent head = new Constituent("PER", "heads", ta, 1, 2);
        Relation left = new Relation("true", new Constituent("token", "tokens-copy", ta, 0, 1), head, 0.8);
        Relation right = new Relation("false", new Constituent("token", "tokens-copy", ta, 2, 3), head, 0.9);
        ExtentReader reader = new ExtentReader("unused", "IGNORED") {
            @Override
            public List<TextAnnotation> getTextAnnotations() {
                return Collections.singletonList(ta);
            }

            @Override
            public List<Relation> getPairs() {
                return Arrays.asList(left, right);
            }
        };

        assertSame(left, reader.next());
        assertSame(right, reader.next());
        assertNull(reader.next());
    }

    @Test
    public void resetBeforeReadingKeepsSequenceAtBeginning() throws Exception {
        TextAnnotation ta = new TextAnnotation("", "", "one",
                new IntPair[] {new IntPair(0, 3)}, new String[] {"one"}, new int[] {1});
        Constituent token = new Constituent("token", "test", ta, 0, 1);
        Relation relation = new Relation("true", token, token, 1.0);
        ExtentReader reader = new ExtentReader("unused", "IGNORED") {
            @Override
            public List<TextAnnotation> getTextAnnotations() {
                return Collections.emptyList();
            }

            @Override
            public List<Relation> getPairs() {
                return Collections.singletonList(relation);
            }
        };

        reader.reset();

        assertSame(relation, reader.next());
        assertNull(reader.next());
    }

    @Test
    public void resetInMiddleRewindsEntireSequence() throws Exception {
        TextAnnotation ta = new TextAnnotation("", "", "one two",
                new IntPair[] {new IntPair(0, 3), new IntPair(4, 7)},
                new String[] {"one", "two"}, new int[] {2});
        Constituent firstToken = new Constituent("token", "test", ta, 0, 1);
        Constituent secondToken = new Constituent("token", "test", ta, 1, 2);
        Relation first = new Relation("first", firstToken, secondToken, 1.0);
        Relation second = new Relation("second", secondToken, firstToken, 1.0);
        ExtentReader reader = new ExtentReader("unused", "IGNORED") {
            @Override
            public List<TextAnnotation> getTextAnnotations() {
                return Collections.emptyList();
            }

            @Override
            public List<Relation> getPairs() {
                return Arrays.asList(first, second);
            }
        };

        assertSame(first, reader.next());
        reader.reset();
        assertSame(first, reader.next());
        assertSame(second, reader.next());
        assertNull(reader.next());
    }

    @Test
    public void resetAfterExhaustionMakesRelationsAvailableAgain() throws Exception {
        TextAnnotation ta = new TextAnnotation("", "", "one",
                new IntPair[] {new IntPair(0, 3)}, new String[] {"one"}, new int[] {1});
        Constituent token = new Constituent("token", "test", ta, 0, 1);
        Relation relation = new Relation("true", token, token, 1.0);
        ExtentReader reader = new ExtentReader("unused", "IGNORED") {
            @Override
            public List<TextAnnotation> getTextAnnotations() {
                return Collections.emptyList();
            }

            @Override
            public List<Relation> getPairs() {
                return Collections.singletonList(relation);
            }
        };

        assertSame(relation, reader.next());
        assertNull(reader.next());
        reader.reset();

        assertSame(relation, reader.next());
        assertNull(reader.next());
    }

    @Test
    public void repeatedResetIsIdempotent() throws Exception {
        TextAnnotation ta = new TextAnnotation("", "", "one",
                new IntPair[] {new IntPair(0, 3)}, new String[] {"one"}, new int[] {1});
        Constituent token = new Constituent("token", "test", ta, 0, 1);
        Relation relation = new Relation("true", token, token, 1.0);
        ExtentReader reader = new ExtentReader("unused", "IGNORED") {
            @Override
            public List<TextAnnotation> getTextAnnotations() {
                return Collections.emptyList();
            }

            @Override
            public List<Relation> getPairs() {
                return Collections.singletonList(relation);
            }
        };

        assertSame(relation, reader.next());
        reader.reset();
        reader.reset();

        assertSame(relation, reader.next());
    }

    @Test
    public void closeDoesNotConsumeOrDisableParser() throws Exception {
        TextAnnotation ta = new TextAnnotation("", "", "one",
                new IntPair[] {new IntPair(0, 3)}, new String[] {"one"}, new int[] {1});
        Constituent token = new Constituent("token", "test", ta, 0, 1);
        Relation relation = new Relation("true", token, token, 1.0);
        ExtentReader reader = new ExtentReader("unused", "IGNORED") {
            @Override
            public List<TextAnnotation> getTextAnnotations() {
                return Collections.emptyList();
            }

            @Override
            public List<Relation> getPairs() {
                return Collections.singletonList(relation);
            }
        };

        reader.close();
        reader.close();

        assertSame(relation, reader.next());
        reader.close();
        reader.reset();
        assertSame(relation, reader.next());
    }

    @Test
    public void nullRelationIsReturnedAndIndexStillAdvances() throws Exception {
        TextAnnotation ta = new TextAnnotation("", "", "one",
                new IntPair[] {new IntPair(0, 3)}, new String[] {"one"}, new int[] {1});
        Constituent token = new Constituent("token", "test", ta, 0, 1);
        Relation relation = new Relation("after-null", token, token, 1.0);
        ExtentReader reader = new ExtentReader("unused", "IGNORED") {
            @Override
            public List<TextAnnotation> getTextAnnotations() {
                return Collections.emptyList();
            }

            @Override
            public List<Relation> getPairs() {
                return Arrays.asList(null, relation);
            }
        };

        assertNull(reader.next());
        assertSame(relation, reader.next());
        assertNull(reader.next());
    }

    @Test
    public void constructorRetainsPairListReturnedByGetPairs() throws Exception {
        TextAnnotation ta = new TextAnnotation("", "", "one two",
                new IntPair[] {new IntPair(0, 3), new IntPair(4, 7)},
                new String[] {"one", "two"}, new int[] {2});
        Constituent firstToken = new Constituent("token", "test", ta, 0, 1);
        Constituent secondToken = new Constituent("token", "test", ta, 1, 2);
        Relation first = new Relation("first", firstToken, secondToken, 1.0);
        Relation appended = new Relation("appended", secondToken, firstToken, 1.0);
        List<Relation> pairs = new ArrayList<>();
        pairs.add(first);
        ExtentReader reader = new ExtentReader("unused", "IGNORED") {
            @Override
            public List<TextAnnotation> getTextAnnotations() {
                return Collections.emptyList();
            }

            @Override
            public List<Relation> getPairs() {
                return pairs;
            }
        };

        pairs.add(appended);

        assertSame(first, reader.next());
        assertSame(appended, reader.next());
        assertNull(reader.next());
    }

    @Test
    public void removingUnreadPairFromReturnedListChangesEndPosition() throws Exception {
        TextAnnotation ta = new TextAnnotation("", "", "one two",
                new IntPair[] {new IntPair(0, 3), new IntPair(4, 7)},
                new String[] {"one", "two"}, new int[] {2});
        Constituent firstToken = new Constituent("token", "test", ta, 0, 1);
        Constituent secondToken = new Constituent("token", "test", ta, 1, 2);
        Relation first = new Relation("first", firstToken, secondToken, 1.0);
        Relation removed = new Relation("removed", secondToken, firstToken, 1.0);
        List<Relation> pairs = new ArrayList<>(Arrays.asList(first, removed));
        ExtentReader reader = new ExtentReader("unused", "IGNORED") {
            @Override
            public List<TextAnnotation> getTextAnnotations() {
                return Collections.emptyList();
            }

            @Override
            public List<Relation> getPairs() {
                return pairs;
            }
        };

        assertSame(first, reader.next());
        pairs.remove(removed);

        assertNull(reader.next());
    }

    @Test
    public void twoArgumentConstructorPropagatesTextAnnotationFailure() throws Exception {
        IOException failure = null;
        try {
            new ExtentReader("unused", "IGNORED") {
                    @Override
                    public List<TextAnnotation> getTextAnnotations() throws IOException {
                        throw new IOException("synthetic input failure");
                    }

                    @Override
                    public List<Relation> getPairs() {
                        fail("pair generation must not run after annotation failure");
                        return Collections.emptyList();
                    }
                };
            fail("The two-argument constructor must propagate IOException");
        } catch (IOException expected) {
            failure = expected;
        }

        assertNotNull(failure);
        assertEquals("synthetic input failure", failure.getMessage());
    }

    @Test
    public void oneArgumentConstructorWrapsTextAnnotationFailure() {
        RuntimeException failure = null;
        try {
            new ExtentReader("unused") {
                    @Override
                    public List<TextAnnotation> getTextAnnotations() throws IOException {
                        throw new IOException("synthetic input failure");
                    }

                    @Override
                    public List<Relation> getPairs() {
                        fail("pair generation must not run after annotation failure");
                        return Collections.emptyList();
                    }
                };
            fail("The one-argument constructor must wrap annotation failures");
        } catch (RuntimeException expected) {
            failure = expected;
        }

        assertNotNull(failure);
        assertEquals("TextAnnotation generation failed", failure.getMessage());
        assertTrue(failure.getCause() instanceof IOException);
        assertEquals("synthetic input failure", failure.getCause().getMessage());
    }

    @Test
    public void oneArgumentConstructorWrapsPairGenerationFailure() {
        IllegalStateException cause = new IllegalStateException("synthetic pair failure");
        RuntimeException failure = null;
        try {
            new ExtentReader("unused") {
                    @Override
                    public List<TextAnnotation> getTextAnnotations() {
                        return Collections.emptyList();
                    }

                    @Override
                    public List<Relation> getPairs() {
                        throw cause;
                    }
                };
            fail("The one-argument constructor must wrap pair failures");
        } catch (RuntimeException expected) {
            failure = expected;
        }

        assertNotNull(failure);
        assertEquals("TextAnnotation generation failed", failure.getMessage());
        assertSame(cause, failure.getCause());
    }

    @Test
    public void twoArgumentConstructorDoesNotWrapPairGenerationFailure() throws Exception {
        IllegalStateException cause = new IllegalStateException("synthetic pair failure");
        IllegalStateException failure = null;
        try {
            new ExtentReader("unused", "IGNORED") {
                    @Override
                    public List<TextAnnotation> getTextAnnotations() {
                        return Collections.emptyList();
                    }

                    @Override
                    public List<Relation> getPairs() {
                        throw cause;
                    }
                };
            fail("The two-argument constructor must propagate pair failures directly");
        } catch (IllegalStateException expected) {
            failure = expected;
        }

        assertNotNull(failure);
        assertSame(cause, failure);
    }
}
