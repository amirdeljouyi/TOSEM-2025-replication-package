package org.evosuite.llm.parser;

import com.examples.with.different.packagename.classpath.Foo;
import org.evosuite.TestGenerationContext;
import org.evosuite.classpath.ClassPathHandler;
import org.evosuite.classpath.ResourceList;
import org.evosuite.testcase.TestCase;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.factory.Factory;

import java.util.*;

import static org.evosuite.llm.parser.ParserFromTextual.getArrayDimensions;
import static org.junit.jupiter.api.Assertions.*;

class ParserFromTextualTest {


    private static String getString() {
        String generatedCode =
//                "        String[] sentences1 = new String[2]; \n"
//                "        String[][] sentences2 = new String[2][3]; \n" +
//                        "        String[][][] sentences3 = {{{\"A1\"}, {\"B1\"}, {\"C1\"}}, {{\"D1\"}, {\"E1\"}, {\"F1\"}}}; \n" +
                "        WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.newlineSplitter(sentences);\n" +
                        "        // Act\n" +
                        "        List<CoreMap> coreMaps = annotator.annotate();\n" +
                        "        // Assert\n" +
                        "        assertEquals(2, coreMaps.size());\n" +
                        "        assertTrue(coreMaps.get(0).containsKey(\"text\"));\n" +
                        "        assertTrue(coreMaps.get(1).containsKey(\"text\"));";

        return "class DummyClass { public static void main (){" + generatedCode + " }}";
    }

    private static String getSimpleStringArrays() {
        String generatedCode =
                "        String sentences = \"Hello\"; \n"
                        + "        String[] sentences1 = {\"Hello\"}; \n"
                        + "        String[] sentences2 = new String[]{ \"This is the first sentence.\", \"This is the second sentence.\" };\n"
                        + "        String a = new String(new char[10000])\n"
//                        + "        String[][] sentences3 = new String[][]{{\"Hello\"}{\"World\"}}; \n"

                        + "         assertEquals(sentences, sentences1, sentences2);"
                        + "         assertEquals(sentences, sentences1[0]);"
//                + "java.util.Arrays.asList(sentences1);\n"
                ;

        return "class DummyClass { public static void main (){" + generatedCode + " }}";
    }

    private static String getGenericTest() {
        String generatedCode = "        ArrayList<String> arrayList = new ArrayList<String>();\n"
                + "        ArrayList<String> arrayList3 = null;\n"
                + "        ArrayList<?> arrayList2 = new ArrayList<?>();\n"
                + "       arrayList.add(\"Amir\");\n"
                + "       arrayList.size();";

        return "class DummyClass { public static void main (){" + generatedCode + " }}";
    }

    private static String getStaticClassTest() {
        String generatedCode = "        Class<?> clazz = java.util.ArrayList.class;\n";

        return "class DummyClass { public static void main (){" + generatedCode + " }}";
    }

    private static String getMockTest() {
        String generatedCode = "        ArrayList<String> mockedList = Mockito.mock(java.util.ArrayList.class);\n" +
                "        ArrayList<String> mockedList1 = mock(java.util.ArrayList.class);\n" +
                "        // Stubbing behavior\n" +
                "        when(mockedList.size()).thenReturn(5);\n" +
                "        when(mockedList.get(0)).thenReturn(\"Mocked Value\");";

        return "class DummyClass { public static void main (){" + generatedCode + " }}";
    }

    private static String getPrimitiveTypes() {
        String generatedCode = "        boolean first = true;\n" +
                "        boolean second = false;\n" +
                "        boolean result = java.util.Objects.equals(first, second);" +
                "        int i1 = 1;\n" +
                "        int i2 = 2;\n" +
                "        boolean result2 = java.util.Objects.equals(i1, i2);\n);";

        return "class DummyClass { public static void main (){" + generatedCode + " }}";
    }

    private static String getInvocationList() {
        String generatedCode = "      ArrayList arrayList = new ArrayList<>();\n" +
                "        arrayList.retainAll(new ArrayList<>());";

        return "class DummyClass { public static void main (){" + generatedCode + " }}";
    }


    @Test
    void parseAssignmentStatementTest() {
        // Initialize Spoon factory
        Launcher launcher = new Launcher();
        Factory factory = launcher.getFactory();
        CtCodeSnippetStatement pre_statement = factory.createCodeSnippetStatement("String[][] sentences = new String[2][3]");
        CtStatement statement = pre_statement.compile();
        System.out.println("1st Statement: " + statement);
        CtNewArray<?> firstAssignment = (CtNewArray<?>) ((CtLocalVariable<?>) statement).getAssignment();
        System.out.println("1st Assignment: " + firstAssignment);

        CtCodeSnippetStatement second_pre_statement = factory.createCodeSnippetStatement("String[][] sentences = new String[2][3];sentences[1][2] = \"Hello Spoon!\"");
        CtAssignment<?, ?> second_statement = second_pre_statement.compile();

        System.out.println("2nd Statement: " + second_statement);


        CtArrayWrite<?> second_assigned = (CtArrayWrite<?>) second_statement.getAssigned();
        CtExpression<?> second_assignment = second_statement.getAssignment();

        System.out.println("Target: " + second_assigned.getTarget().getClass());

        System.out.println("assigned: " + second_assigned.getClass() + " " + second_assigned.getIndexExpression());
        System.out.println("assignment: " + second_assignment.getClass());
        System.out.println("index expression: " + second_assigned.getIndexExpression());

        List<Integer> expected = new ArrayList<Integer>();
        expected.add(1);
        expected.add(2);
        assertEquals(expected, getArrayDimensions(second_assigned));
    }

    @Test
    void regexFindAllParenthesis() {
        String className = "java.lang.String[][]";
        className = className.replaceAll("(\\[\\])+$", "");
        assertEquals("java.lang.String", className);
    }

    @Test
    void regexCountAllParenthesis() {
        String className = "java.lang.String[][]";
        assertEquals(2, className.split("\\[\\]", -1).length - 1);
    }

    @Test
    void regexCountOneParenthesis() {
        String className = "java.lang.String[]";
        assertEquals(1, className.split("\\[\\]", -1).length - 1);
    }

    @Test
    void parseArrayStatementTest() {
        String dummyClass = getSimpleStringArrays();
        CtClass<?> ctClass = Launcher.parseClass(dummyClass);

        ParserFromTextual parser = new ParserFromTextual();
        parser.parseTestSnippet(ctClass);
//        System.out.println(logWatcher);
    }

    @Test
    void parseGenericStatementTest() {
        String dummyClass = getGenericTest();
        CtClass<?> ctClass = Launcher.parseClass(dummyClass);


        ParserFromTextual parser = new ParserFromTextual();
        parser.parseTestSnippet(ctClass);
//        System.out.println(logWatcher);
    }

    @Test
    void parseStaticFieldClassTest() {
        String dummyClass = getStaticClassTest();
        CtClass<?> ctClass = Launcher.parseClass(dummyClass);

        ParserFromTextual parser = new ParserFromTextual();
        TestCase testCase = parser.parseTestSnippet(ctClass);
        assertEquals("Class<ArrayList> class0 = ArrayList.class;\n", testCase.toCode());
    }

    @Test
    void parseMockTest() {
        String dummyClass = getMockTest();
        CtClass<?> ctClass = Launcher.parseClass(dummyClass);

        ParserFromTextual parser = new ParserFromTextual();
        TestCase testCase = parser.parseTestSnippet(ctClass);
    }

    @Test
    void resolveInvocationAndArrayUsageArg() {
        String dummyClass = getPrimitiveTypes();
        CtClass<?> ctClass = Launcher.parseClass(dummyClass);

        ParserFromTextual parser = new ParserFromTextual();
        TestCase testCase = parser.parseTestSnippet(ctClass);
    }

    @Test
    void resolvePrimitiveTypesTest() {
        String dummyClass = getInvocationList();
        CtClass<?> ctClass = Launcher.parseClass(dummyClass);

        ParserFromTextual parser = new ParserFromTextual();
        TestCase testCase = parser.parseTestSnippet(ctClass);
    }

    @Test
    void resolveGenericConverter() {
//        ResourceList.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT()).resetCache();
        ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();

        String generatedCode = "        Foo foo0 = new Foo();\n" +
                "        String hi = Foo.example(\"A\");\n" +
                "        List<String> arrayList0 = new ArrayList<>();\n" +
                "        arrayList0.add(hi);\n" +
                "        List<String> ab = foo0.set(Foo.AnotherFoo.class, arrayList0);\n" +
                "        List<String> ab = foo0.set(Foo.AnotherFoo.class, arrayList0);\n" +
                "        ab.get(0).length();\n" +
                "        boolean empty = ab.isEmpty();\n" +
                "        int i = 1;\n" +
                "        int abc = foo0.set(Foo.AnotherIntFoo.class, i);" +
                "        String c = Boolean.toString(empty);\n" +
                "        arrayList0.add(c)\n" +
                "        List<String> list = Arrays.asList(c, c, c);" +
                "        list.size();" +
                "        System.out.println(c);";

        String dummyClass = "class DummyClass { public static void main (){" + generatedCode + " }}";
        boolean b = true;
        String c = Boolean.toString(b);
        CtClass<?> ctClass = Launcher.parseClass(dummyClass);

        ParserFromTextual parser = new ParserFromTextual();
        TestCase testCase = parser.parseTestSnippet(ctClass);

        assertTrue(testCase.isValid());

        String hi = Foo.example("A");
        Foo foo0 = new Foo();
        ArrayList<String> arrayList0 = new ArrayList<>();
        arrayList0.add(hi);
        arrayList0.get(0).length();
        List<String> ab = foo0.set(Foo.AnotherFoo.class, arrayList0);
        int i = 1;
        int abc = foo0.set(Foo.AnotherIntFoo.class, i);
        boolean empty = ab.isEmpty();
        System.out.println(empty);
//
//        Foo foo0 = new Foo();
//        ArrayList<String> arrayList0 = new ArrayList<String>();
//        Class<Foo.AnotherFoo> class0 = Foo.AnotherFoo.class;
//        foo0.set((Class<? extends Foo.Key<List<String>>>) class0, arrayList0);
//        Assert.assertEquals("com.examples.with.different.packagename.classpath.Foo$AnotherFoo",
//                ResourceList.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT()).hasCandidateAllPossibilities(Foo.AnotherFoo.class.getSimpleName()));    }
    }


    @Test
    void resolveGenericConverter2() {
        ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();

        String generatedCode = "        Foo foo0 = new Foo();\n" +
                "        Map<?, ?> ab = foo0.set(Foo.AnotherMapFoo.class, new HashMap<>());\n" +
                "        List<Foo> fooList = Arrays.asList(foo0, null);\n" +
                "        try {\n" +
                "            ab.get(0);\n" +
                "        } catch (Exception e){\n" +
                "            e.printStackTrace();\n" +
                "        }\n";

        String dummyClass = "class DummyClass { public static void main (){" + generatedCode + " }}";
        CtClass<?> ctClass = Launcher.parseClass(dummyClass);

        ParserFromTextual parser = new ParserFromTextual();
        TestCase testCase = parser.parseTestSnippet(ctClass);

        assertTrue(testCase.isValid());

        Foo foo0 = new Foo();
        Map<?, ?> ab = foo0.set(Foo.AnotherMapFoo.class, new HashMap<>());
        List<Foo> fooList = Arrays.asList(foo0, null);
        boolean empty = ab.isEmpty();

        try {
            ab.get(0);
        } catch (Exception e){
            e.printStackTrace();
        }
        System.out.println(empty);

    }

    @Test
    void resolveMethod() {
        ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();

        String generatedCode = "                Foo foo0 = new Foo();\n" +
                "        foo0.set(Foo.AnotherStringFoo.class, \"Hi\");\n" +
                "        foo0.set(Foo.AnotherIntFoo.class, 1);";
        String dummyClass = "class DummyClass { public static void main (){" + generatedCode + " }}";
        CtClass<?> ctClass = Launcher.parseClass(dummyClass);

        ParserFromTextual parser = new ParserFromTextual();
        TestCase testCase = parser.parseTestSnippet(ctClass);

        assertTrue(testCase.isValid());

        Foo foo0 = new Foo();
        foo0.set(Foo.AnotherStringFoo.class, "Hi");
        foo0.set(Foo.AnotherIntFoo.class, 1);
    }

    @Test
    void resolveVarArgsArray() {
        ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();

        Arrays.asList(new Foo[]{new Foo(), new Foo()});

        String generatedCode = "Arrays.asList(new Foo[]{new Foo(), new Foo()});";
        String dummyClass = "class DummyClass { public static void main (){" + generatedCode + " }}";
        CtClass<?> ctClass = Launcher.parseClass(dummyClass);

        ParserFromTextual parser = new ParserFromTextual();
        TestCase testCase = parser.parseTestSnippet(ctClass);

        System.out.println(testCase.toCode());

        assertTrue(testCase.isValid());
    }

}