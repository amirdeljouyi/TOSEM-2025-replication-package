package org.evosuite.llm;

import org.evosuite.Properties;
import org.evosuite.llm.parser.ParserFromTextual;
import org.evosuite.testcase.TestCase;
import org.evosuite.utils.LoggingUtils;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class LLMTestCaseGeneration {
    ParserFromTextual parser = new ParserFromTextual();
    int testIndex = 0;

    public LLMTestCaseGeneration() {
    }

    public TestCase buildTestCaseByAPI(SourceParser sourceParser, Method targetMethod) {
        LLMValidator llmValidator = new LLMValidator();

        LoggingUtils.getEvoLogger().info("Target Method is: " + targetMethod.toString());
        LoggingUtils.getEvoLogger().info("Method Name is: " + targetMethod.getName());
        String body = sourceParser.getMethodBody(targetMethod.getName());

        LoggingUtils.getEvoLogger().info("Method Body is: " + body);

        CtClass<?> parsedGeneratedTestCase = llmValidator.generateTestCase(body);

        if (parsedGeneratedTestCase != null) {
            LoggingUtils.getEvoLogger().info("Generated test case is: " + parsedGeneratedTestCase);
            TestCase generatedTestCase = parser.parseTestSnippet(parsedGeneratedTestCase);
            if (generatedTestCase != null && !generatedTestCase.isEmpty() && generatedTestCase.isValid()) {
                return generatedTestCase;
            }
        }

        return null;
    }

    public TestCase buildTestCaseByAPICodaMosa(SourceParser sourceParser, Method targetMethod, int attempt) {
        LLMValidator llmValidator = new LLMValidator();

        LoggingUtils.getEvoLogger().info("Target Method is: " + targetMethod.toString());
        LoggingUtils.getEvoLogger().info("Method Name is: " + targetMethod.getName());
        String body = sourceParser.getMethodBody(targetMethod.getName());
        String promptBody = "method " + targetMethod.getName() + " of class " + targetMethod.getDeclaringClass().getName() + ".\n method under test is:" + body;

        LoggingUtils.getEvoLogger().info("Prompt Body is: " + promptBody);

        CtClass<?> parsedGeneratedTestCase = llmValidator.generateTestCaseCodaMosa(promptBody);

        if (parsedGeneratedTestCase != null) {
            LoggingUtils.getEvoLogger().info("Generated test case is: " + parsedGeneratedTestCase);
            TestCase generatedTestCase = parser.parseTestSnippet(parsedGeneratedTestCase);
            if (generatedTestCase != null && !generatedTestCase.isEmpty() && generatedTestCase.isValid()) {
                testIndex++;

                StringBuilder sb = new StringBuilder();
                sb.append("\n@Test\n");
                sb.append("public void test").append(testIndex).append("()\n");

                for (CtMethod<?> method : parsedGeneratedTestCase.getMethods()) {
                    if (method.getBody() != null) {
                        sb.append(method.getBody().toString()).append("\n");
                    }
                }

                try {

                    Path outputDir = Paths.get(Properties.OUTPUT_DIR);
                    Files.createDirectories(outputDir);

                    Path path = outputDir.resolve("generated_test_output." + attempt + "."  + Properties.getTargetClassAndDontInitialise().getSimpleName() + ".java");

                    LoggingUtils.getEvoLogger().info("Write test cases in: " + path.toAbsolutePath());

                    // If file doesn't exist, write header
                    if (!Files.exists(path)) {
                        String header = "import org.junit.Test;\n" +
                                "import static org.junit.Assert.*;\n\n" +
                                "public class GeneratedTest {\n";
                        Files.write(path, header.getBytes(),
                                StandardOpenOption.CREATE,
                                StandardOpenOption.APPEND
                        );
                    }

                    // Append test method
                    Files.write(path, sb.toString().getBytes(),
                            StandardOpenOption.CREATE,
                            StandardOpenOption.APPEND
                    );

                    LoggingUtils.getEvoLogger().info("Test method appended to file: " + path.toAbsolutePath());
                } catch (IOException e) {
                    LoggingUtils.getEvoLogger().error("Failed to write test body to file: " + e.getMessage());
                }

                return generatedTestCase;
            }
        }

        return null;
    }

    public ArrayList<TestCase> buildTestCasesFromFile(String content) {
        LoggingUtils.getEvoLogger().info("LLM Content is: " + content);
        CtClass<?> ctClass = Launcher.parseClass(content);
        return parser.parseTestClass(ctClass);
    }
}
