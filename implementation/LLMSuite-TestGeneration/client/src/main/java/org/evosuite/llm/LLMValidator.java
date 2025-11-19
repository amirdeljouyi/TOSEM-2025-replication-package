package org.evosuite.llm;

import org.evosuite.utils.LoggingUtils;
import spoon.Launcher;
import spoon.SpoonException;
import spoon.reflect.declaration.CtClass;

public class LLMValidator {
    private final LLMHandler llm = new LLMHandler();

    private String improveUnderstandability(String code, int i) {
        if (i == 5)
            return code;

        String improvedCode = llm.improveUnderstandability(code);

        if (improvedCode == null) {
            return improveUnderstandability(code, i + 1);
        }

        String dummyClass = "class DummyClass { public static void main (){" + improvedCode + " }}";
        try {
            CtClass<?> ctClass = Launcher.parseClass(dummyClass);
            LoggingUtils.getEvoLogger().info("** It was a parseable test case! ");
            return improvedCode;
        } catch (SpoonException e) {
            return improveUnderstandability(code, i + 1);
        }
    }

    public String improveUnderstandability(String code) {
        return improveUnderstandability(code, 0);
    }


    private CtClass<?> improveTestData(String code, int i) {
        if (i == 5)
            return null;

        String improvedCode = llm.improveTestData(code);
        if (improvedCode == null) {
            return improveTestData(code, i + 1);
        }

        String dummyClass = "class DummyClass { public static void main (){" + improvedCode + " }}";
        try {
            CtClass<?> ctClass = Launcher.parseClass(dummyClass);
            LoggingUtils.getEvoLogger().info("** It was a parseable test case! ");
            return ctClass;
        } catch (SpoonException e) {
            return improveTestData(code, i + 1);
        }
    }

    public CtClass<?> improveTestData(String code) {
        return improveTestData(code, 0);
    }

    public CtClass<?> generateTestCase(String code){
        return generateTestCase(code, 0);
    }

    public CtClass<?> generateTestCase(String code, int i) {
        if (i == 5)
            return null;

        String generatedCode = llm.generateTestCase(code);
        if (generatedCode == null) {
            return generateTestCase(code, i + 1);
        }

        String dummyClass = "class DummyClass { public static void main (){" + generatedCode + " }}";
        try {
            CtClass<?> ctClass = Launcher.parseClass(dummyClass);
            LoggingUtils.getEvoLogger().info("** It was a parseable test case! ");
            return ctClass;
        } catch (SpoonException e) {
            return generateTestCase(code, i + 1);
        }
    }


    public CtClass<?> generateTestCaseCodaMosa(String code){
        return generateTestCaseCodaMosa(code, 0);
    }

    public CtClass<?> generateTestCaseCodaMosa(String code, int i) {
        if (i == 5)
            return null;

        String generatedCode = llm.generateTestCaseCodaMosa(code);
        if (generatedCode == null) {
            return generateTestCaseCodaMosa(code, i + 1);
        }

        String dummyClass = "class DummyClass { public static void main (){" + generatedCode + " }}";
        try {
            CtClass<?> ctClass = Launcher.parseClass(dummyClass);
            LoggingUtils.getEvoLogger().info("** It was a parseable test case! ");
            return ctClass;
        } catch (SpoonException e) {
            return generateTestCase(code, i + 1);
        }
    }

    private String suggestTestName(String code, int i){
        if (i == 5)
            return null;

        String suggestedTestName = llm.suggestTestName(code);
        if (suggestedTestName == null) {
            return suggestTestName(code, i + 1);
        }

        return suggestedTestName;
    }

    public String suggestTestName(String code){
        return suggestTestName(code, 0);
    }
}
