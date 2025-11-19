package org.evosuite.ga.metaheuristics.mosa;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CodaMOSATest {

    @Test
    void testParseDescriptor1() {
        String result = CodaMOSA.parseDescriptor("extractBestParses(III)Ljava/util/List;");
        assertEquals("extractBestParses(3)", result);
    }

    @Test
    void testParseDescriptor2() {
        String result = CodaMOSA.parseDescriptor("parse(Ljava/util/List;)Z");
        assertEquals("parse(1)", result);
    }

    @Test
    void testParseDescriptor3() {
        String result = CodaMOSA.parseDescriptor("lazyNext(Ledu/stanford/nlp/util/PriorityQueue;Ledu/stanford/nlp/parser/lexparser/ExhaustivePCFGParser$Derivation;I)V");
        assertEquals("lazyNext(3)", result);
    }
}