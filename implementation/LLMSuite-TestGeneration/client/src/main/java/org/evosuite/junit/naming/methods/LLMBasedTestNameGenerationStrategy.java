/*
 * Copyright (C) 2010-2018 Gordon Fraser, Andrea Arcuri and EvoSuite
 * contributors
 *
 * This file is part of EvoSuite.
 *
 * EvoSuite is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3.0 of the License, or
 * (at your option) any later version.
 *
 * EvoSuite is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with EvoSuite. If not, see <http://www.gnu.org/licenses/>.
 */
package org.evosuite.junit.naming.methods;


import org.evosuite.llm.LLMValidator;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.execution.ExecutionResult;

import java.util.*;

public class LLMBasedTestNameGenerationStrategy implements TestNameGenerationStrategy {

    private final Map<TestCase, String> testToName = new LinkedHashMap<>();

    private final Map<String, Set<String>> methodCount = new LinkedHashMap<>();

    private final CoverageGoalTestNameGenerationStrategy coverageGoalStrategy;

    private final LLMValidator llm = new LLMValidator();

    public static final int MAX_CHARS = 70;
    public static final int RECURSIVE_LIMIT = 3;

    public LLMBasedTestNameGenerationStrategy(List<TestCase> testCases, List<ExecutionResult> results) {
        coverageGoalStrategy = new CoverageGoalTestNameGenerationStrategy(testCases, results);
        generateDefaultNames(testCases);
    }

    /**
     * This assumes all goals are already saved in the tests
     *
     * @param testCases
     */
    public LLMBasedTestNameGenerationStrategy(List<TestCase> testCases) {
        coverageGoalStrategy = new CoverageGoalTestNameGenerationStrategy(testCases);
        generateDefaultNames(testCases);
    }

    private String getLLMTestName(TestCase test, String code) {
        String suggestedTestName = llm.suggestTestName(code);
        // if the suggested name was null use the default name of EVOSUITE
        if (suggestedTestName == null)
            return testToName.get(test);

        return fixAmbiguousTestNames(test, code, suggestedTestName, 0);
    }

    private String getDescriptiveLLMTestName(TestCase test, String code) {
        String suggestedTestName = llm.suggestTestName(code);
        // if the suggested name was null use the default name of EVOSUITE
        if (suggestedTestName == null)
            return testToName.get(test);

        return suggestedTestName;
    }


    private void generateDefaultNames(List<TestCase> testCases) {
        for (TestCase testCase : testCases) {
            testToName.put(testCase, coverageGoalStrategy.getName(testCase));
        }
    }

    /**
     * There may be tests with the same calculated name, in which suggest another test name
     */
    private String fixAmbiguousTestNames(TestCase test, String code, String suggestedName, int recursive) {
        if (recursive == RECURSIVE_LIMIT)
            return testToName.get(test);

        for (Map.Entry<TestCase, String> entry : testToName.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(suggestedName) && !entry.getKey().equals(test)) {
                return fixAmbiguousTestNames(test, code, getDescriptiveLLMTestName(test, code), recursive + 1);
            }
        }
        return suggestedName;
    }

    @Override
    public String getName(TestCase test) {
        return testToName.get(test);
    }

    @Override
    public String getName(TestCase test, String code) {
        return getLLMTestName(test, code);
    }
}

