package org.evosuite.llm;

import org.evosuite.utils.LoggingUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;

import java.util.ArrayList;
import java.util.List;

public class SpoonTest {
    @Test
    public void parseArrays() {
        String dummyClass = getString();
        CtClass<?> ctClass = Launcher.parseClass(dummyClass);

        for (CtMethod<?> ctMethod : ctClass.getMethods()) {
            CtBlock<?> body = ctMethod.getBody();
            if (body == null) continue; // Avoid potential NullPointerException

            for (CtStatement statement : body.getStatements()) {
                System.out.println(("Processing statement: " + statement + " (" + statement.getClass() + ")"));
                if (statement instanceof CtLocalVariable<?>) {
                    CtLocalVariable<?> localVariable = (CtLocalVariable<?>) statement;
                    CtExpression<?> assignment = localVariable.getAssignment();

                    System.out.println("Assignment type: " + assignment.getClass());
                    System.out.println("Assignment type: " + assignment.getType());


                    if(assignment instanceof CtNewArray) {
                        CtNewArray<?> newArray = (CtNewArray<?>) assignment;
                        System.out.println((int) newArray.getType().getSimpleName().chars().filter(ch -> ch == '[').count());
                        System.out.println("Elements: " + newArray.getElements());
                        System.out.println("getDimensionExpressions: " + getArrayDimensions(newArray));
                    }
                }

            }
        }


    }

    private static @NotNull String getString() {
        String generatedCode =
                "        String[][] sentences2 = new String[2][3]; \n"+
                "        String[][][] sentences3 = {{{\"A1\"}, {\"B1\"}, {\"C1\"}}, {{\"D1\"}, {\"E1\"}, {\"F1\"}}}; \n"+
                "        WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.newlineSplitter(sentences);\n" +
                "        // Act\n" +
                "        List<CoreMap> coreMaps = annotator.annotate();\n" +
                "        // Assert\n" +
                "        assertEquals(2, coreMaps.size());\n" +
                "        assertTrue(coreMaps.get(0).containsKey(\"text\"));\n" +
                "        assertTrue(coreMaps.get(1).containsKey(\"text\"));";

        return "class DummyClass { public static void main (){" + generatedCode + " }}";
    }

    public static List<Integer> getArrayDimensions(CtNewArray<?> array) {
        List<Integer> dimensions = new ArrayList<>();

        // Case 1: If declared with explicit dimensions (e.g., new String[3][4])
        if (!array.getDimensionExpressions().isEmpty()) {
            for (CtExpression<Integer> expr : array.getDimensionExpressions()) {
                try {
                    dimensions.add(Integer.parseInt(expr.toString())); // Extract size
                } catch (NumberFormatException e) {
                    dimensions.add(-1); // Unknown size (e.g., variable reference)
                }
            }
            return dimensions;
        }

        // Case 2: If initialized using nested elements (e.g., {{ "a", "b" }, { "c" }})
        List<Integer> inferredDimensions = new ArrayList<>();
        CtNewArray<?> current = array;

        while (current != null && !current.getElements().isEmpty()) {
            inferredDimensions.add(current.getElements().size()); // Number of elements in this dimension

            // Check if first element is also an array (nested case)
            if (current.getElements().get(0) instanceof CtNewArray<?>) {
                current = (CtNewArray<?>) current.getElements().get(0);
            } else {
                break; // No further nested arrays
            }
        }

        return inferredDimensions;
    }
}
