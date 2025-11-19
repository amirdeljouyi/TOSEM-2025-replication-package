package org.evosuite.llm;

import org.evosuite.ClientProcess;
import org.evosuite.llm.parser.ParserFromTestCase;
import org.evosuite.testcase.ConstantInliner;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.statements.Statement;
import org.evosuite.testsuite.TestSuiteChromosome;
import org.evosuite.utils.LoggingUtils;
import spoon.reflect.declaration.CtClass;

import java.util.ArrayList;

public class Refinement {

    private TestSuiteChromosome testSuite;
    private final LLMHandler llm = new LLMHandler();

    ConstantInliner inliner = new ConstantInliner();

    public Refinement(TestSuiteChromosome testSuite) {
        this.testSuite = testSuite;
    }

    public void refine() {
        ArrayList<TestCase> improvedTests = new ArrayList<>();

        for (TestChromosome test : this.testSuite.getTestChromosomes()) {

            TestCase testCase = test.getTestCase();
            testCase.toCode();
            for (int i = 0; i < testCase.size(); i++) {
                Statement statement = testCase.getStatement(i);
                LoggingUtils.getEvoLogger().info("statement is: " + statement + " type is: " + statement.getClass() + " return type " + statement.getReturnType());
                LoggingUtils.getEvoLogger().info("statement code is: " + statement.getCode());
            }

            String oldTestData = testCase.toCode();
            LoggingUtils.getEvoLogger().info("test data is: " + oldTestData);
            LLMValidator llmValidator = new LLMValidator();
            CtClass<?> parsedImprovedTestData = llmValidator.improveTestData(oldTestData);

            if (parsedImprovedTestData != null) {
                LoggingUtils.getEvoLogger().info("Improved test data is: " + parsedImprovedTestData);
                ParserFromTestCase parser = new ParserFromTestCase(testCase);
                TestCase improvedTestCase = parser.parseTestSnippet(parsedImprovedTestData);

                if (!improvedTestCase.isEmpty()) {
                    TestChromosome improvedTestChromosome = new TestChromosome();
                    improvedTestChromosome.setTestCase(improvedTestCase);
                    improvedTests.add(improvedTestCase);
                }
            }
        }

        for (TestCase test : improvedTests) {
            this.testSuite.addTest(test);
        }

        inliner.inline(this.testSuite);

        LoggingUtils.getEvoLogger().info("* test suite is" + ClientProcess.getPrettyPrintIdentifier() + testSuite);
    }

    private String appendLines(ArrayList<String> lines) {
        if (lines.size() == 0)
            return null;
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < lines.size() - 1; i++) {
            result.append(lines.get(i));
            result.append(System.getProperty("line.separator"));
        }
        result.append(lines.get(lines.size() - 1));
        return result.toString();
    }
}
