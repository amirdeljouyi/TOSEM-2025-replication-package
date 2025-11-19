package org.evosuite.llm.parser;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.checkerframework.checker.units.qual.A;
import org.evosuite.setup.TestCluster;
import org.evosuite.symbolic.TestCaseBuilder;
import org.evosuite.testcase.DefaultTestCase;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.variable.ArrayReference;
import org.evosuite.testcase.variable.VariableReference;
import org.evosuite.testcase.variable.VariableReferenceImpl;
import org.evosuite.utils.LoggingUtils;
import org.evosuite.utils.generic.GenericClass;
import org.evosuite.utils.generic.GenericClassFactory;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.code.CtFieldReadImpl;
import spoon.support.reflect.code.CtTypeAccessImpl;

import java.lang.reflect.*;
import java.util.*;


/**
 * Parses Java test snippets from LLM using Spoon's AST and converts them into EvoSuite test cases.
 *
 * <p>This class processes method calls, variable assignments, constructor calls,
 * and array operations, translating them into EvoSuite-compatible test cases.</p>
 *
 * @author Amir Deljouyi
 */
public class ParserFromTextual implements Parser {

    /**
     * Stores variable references and names encountered during parsing
     */
    private Map<String, VariableReference> variableReferences = new HashMap<>();

    /**
     * Keeps track of inline variable positions
     */
    private int position = 0;

    /**
     * Singleton instance of TestCluster for resolving classes
     */
    private final TestCluster testCluster = TestCluster.getInstance();

    /**
     * Builder for constructing test cases in EvoSuite Format
     */
    private TestCaseBuilder builder;

    /**
     * Debug flag to enable detailed logging
     */
    private static final boolean DEBUG = true;


    public ParserFromTextual() {
        builder = new TestCaseBuilder();
    }

    /**
     * Parses a given test snippet represented as a Spoon `CtClass<?>` and converts it into a TestCase.
     */
    @Override
    public TestCase parseTestSnippet(CtClass<?> ctClass) {
        for (CtMethod<?> ctMethod : ctClass.getMethods()) {
            builder = new TestCaseBuilder();
            CtBlock<?> body = ctMethod.getBody();
            if (body == null) continue; // Avoid NullPointerException
            for (CtStatement statement : body.getStatements()) {
                try {
                    processStatement(statement);
                } catch (Exception e) {
                    LoggingUtils.getEvoLogger().warn("Could Not Handle the Statement " + statement, e);
                }
            }
        }

        DefaultTestCase testcase = null;
        try {
            testcase = builder.getDefaultTestCase();
            LoggingUtils.getEvoLogger().info("Generated Test Case: " + testcase.toCode());
        } catch (Exception e) {
            LoggingUtils.getEvoLogger().warn("Could Not Handle this test, " + ctClass, e);
        }

        return testcase;
    }

    public ArrayList<TestCase> parseTestClass(CtClass<?> ctClass) {
        ArrayList<TestCase> testCases = new ArrayList<>();
        for (CtMethod<?> ctMethod : ctClass.getMethods()) {
            reset();

            LoggingUtils.getEvoLogger().info("LLM Generated test is: " + ctMethod);

            CtBlock<?> body = ctMethod.getBody();
            if (body == null) continue; // Avoid NullPointerException

            for (CtStatement statement : body.getStatements()) {
                try {
                    processStatement(statement);
                } catch (IllegalArgumentException e) {
                    LoggingUtils.getEvoLogger().warn("Could Not Handle the statement " + statement, e);
                }
            }

            try {
                LoggingUtils.getEvoLogger().info("Generated Test Case: " + builder.getDefaultTestCase().toCode());
                if (!builder.getDefaultTestCase().isEmpty()) {
                    testCases.add(builder.getDefaultTestCase());
                }
            } catch (Exception e) {
                LoggingUtils.getEvoLogger().warn("Could Not Handle the Method " + body, e);
            }
        }
        return testCases;
    }

    private void reset() {
        builder = new TestCaseBuilder();
        variableReferences = new HashMap<>();
        position = 0;
    }

    /**
     * Identifies and processes different types of statements in the test snippet.
     */
    private void processStatement(CtStatement statement) {
        LoggingUtils.getEvoLogger().info("Processing statement: " + statement + " (" + statement.getClass() + ")");


        if (statement instanceof CtLocalVariable<?>) {
            handleLocalVariable((CtLocalVariable<?>) statement);
        } else if (statement instanceof CtInvocation<?>) {
            handleInvocation((CtInvocation<?>) statement);
        } else if (statement instanceof CtAssignment<?, ?>) {
            handleAssignment((CtAssignment<?, ?>) statement);
        } else if (statement instanceof CtTry) {
            CtTry tryStatement = ((CtTry) statement);
            for (CtStatement tryIndStatement : tryStatement.getBody().getStatements()) {
                processStatement(tryIndStatement);
            }
        } else {
            LoggingUtils.getEvoLogger().warn("Unsupported statement type: " + statement + " (" + statement.getClass() + ")");
        }

    }


    /**
     * Handles parsing for local variable declarations.
     */
    private void handleLocalVariable(CtLocalVariable<?> variable) {
        CtExpression<?> assignment = variable.getAssignment();
        CtLocalVariableReference<?> reference = variable.getReference();
        if (assignment == null) return;

        LoggingUtils.getEvoLogger().info("Local Variable, Assignment: " + assignment + " Reference: " + reference + " Reference Type is: " + reference.getType());
        VariableReference var = resolveExpressionStatement(assignment, reference.getType());
        if (var != null) {
            GenericClass<?> clazz = resolveGenericClass(reference.getType());
            if (clazz != null) {
                var.setType(clazz.getType());
            }
            addVariable(variable.getSimpleName(), var);
        }
    }

    /**
     * Handles method invocation statements.
     */
    private void handleInvocation(CtInvocation<?> invocation) {
        resolveInlineExpressionStatement(invocation);
    }

    /**
     * Handles parsing for assignment statements.
     */
    private void handleAssignment(CtAssignment<?, ?> assignment) {
        CtExpression<?> assigned = assignment.getAssigned();
        CtExpression<?> ctAssignment = assignment.getAssignment();

        LoggingUtils.getEvoLogger().info("Assigned: " + assigned + " " + assigned.getClass());

        if (assigned instanceof CtArrayWrite) {
            parseAssignmentStatement((CtArrayWrite<?>) assigned, ctAssignment);
        } else if (assigned instanceof CtVariableWrite) {
            parseVarAssignmentStatement((CtVariableWrite<?>) assigned, ctAssignment);
        } else {
            LoggingUtils.getEvoLogger().info("IT HAS NOT BEEN SUPPORTED YET: " + assignment);
        }
    }

    protected void parseAssignmentStatement(CtArrayWrite<?> ctAssigned, CtExpression<?> ctAssignment) {
        LoggingUtils.getEvoLogger().info("Assigned : " + ctAssigned + " Assigned Type " + ctAssigned.getClass() + " " + ctAssigned.getType() + " Assignment:" + ctAssignment);
        LoggingUtils.getEvoLogger().info("target : " + ctAssigned.getTarget() + " " + ctAssigned.getIndexExpression().getClass());


        List<Integer> indexSize = getArrayDimensions(ctAssigned);

        VariableReference targetArray = findVariable(ctAssigned.getTarget().toString());
        ArrayReference arrayVar;

        if (targetArray instanceof ArrayReference)
            arrayVar = (ArrayReference) targetArray;
        else
            return;

        buildArrayAssignment(ctAssignment, arrayVar, indexSize);
    }

    protected void parseVarAssignmentStatement(CtVariableWrite<?> ctAssigned, CtExpression<?> ctAssignment) {
        LoggingUtils.getEvoLogger().info("Assigned : " + ctAssigned + " Assigned Type " + ctAssigned.getClass() + " " + ctAssigned.getType() + " Assignment:" + ctAssignment);

        VariableReference targetVar = findVariable(ctAssigned.getVariable().toString());

        VariableReference var = resolveInlineExpressionStatement(ctAssignment);

        if (var == null) return;

        builder.appendAssignment(targetVar, var);
    }

    /**
     * Resolves an expression into a VariableReference.
     *
     * @param expression The Spoon AST expression to resolve.
     * @param inline     If true, stores the result as an inline variable.
     * @return The resolved VariableReference or null if unsupported.
     */
    private <T> VariableReference resolveExpressionStatement(CtExpression<?> expression, T type, boolean inline) {
        VariableReference var = null;

        if (expression instanceof CtLiteral<?>) {
            var = parseLiteral((CtLiteral<?>) expression, type);
        } else if (expression instanceof CtConstructorCall<?>) {
            var = parseConstructorCall((CtConstructorCall<?>) expression);
        } else if (expression instanceof CtInvocation<?>) {
            var = parseMethodInvocation((CtInvocation<?>) expression);
        } else if (expression instanceof CtNewArray<?>) {
            var = parseArrayStatement((CtNewArray<?>) expression);
        } else if (expression instanceof CtFieldReadImpl<?>) {
            var = parseFieldRead((CtFieldReadImpl<?>) expression);
        } else if (expression instanceof CtArrayRead) {
            var = parseArrayRead((CtArrayRead<?>) expression);
        } else if (expression instanceof CtVariableRead<?>) {
            return findVariable(((CtVariableRead<?>) expression).getVariable().getSimpleName());
        } else {
            LoggingUtils.getEvoLogger().info("IT HAS NOT BEEN SUPPORTED YET1: " + expression + " " + expression.getClass() + " " + type);
        }

        return (inline && var != null) ? addVariable(var) : var;
    }

    private VariableReference resolveExpressionStatement(CtExpression<?> expression, boolean inline) {
        return resolveExpressionStatement(expression, expression.getType(), inline);
    }

    private VariableReference resolveExpressionStatement(CtExpression<?> expression, CtTypeReference<?> type) {
        return resolveExpressionStatement(expression, type, false);
    }

    /**
     * Resolves an expression into an inline VariableReference with adding inline variables.
     */
    private VariableReference resolveInlineExpressionStatement(CtExpression<?> expression) {
        return resolveExpressionStatement(expression, true);
    }

    private VariableReference resolveInlineExpressionStatement(CtExpression<?> expression, Type type) {
        return resolveExpressionStatement(expression, type, true);
    }

    private <T> VariableReference parseLiteral(CtLiteral<?> literal, T type) {
        Object value = literal.getValue();
        return value == null ? parseNullStatement(type) : parsePrimitiveType(literal);
    }

    private <T> VariableReference parseNullStatement(T type) {
        Type classType;
        System.out.println("TYPE:" + type + " " + type.getClass());
        if (type instanceof CtTypeReference) {
            GenericClass<?> clazz = resolveGenericClass((CtTypeReference<?>) type);
            if (clazz == null) return null;
            classType = clazz.getType();
        } else if (type instanceof Type) {
            classType = (Type) type;
        } else {
            return null;
        }

        return builder.appendNull(classType);
    }

    private VariableReference parsePrimitiveType(CtExpression<?> assignment) {
        Object value = ((CtLiteral<?>) assignment).getValue();
        if (value instanceof String) return builder.appendStringPrimitive((String) value);
        else if (Integer.class.isInstance(value)) return builder.appendIntPrimitive((Integer) value);
        else if (Double.class.isInstance(value)) return builder.appendDoublePrimitive((Double) value);
        else if (Short.class.isInstance(value)) return builder.appendShortPrimitive((Short) value);
        else if (Float.class.isInstance(value)) return builder.appendFloatPrimitive((Float) value);
        else if (Long.class.isInstance(value)) return builder.appendLongPrimitive((Long) value);
        else if (value instanceof Boolean) return builder.appendBooleanPrimitive((Boolean) value);
        else if (boolean.class.isInstance(value)) return builder.appendBooleanPrimitive((boolean) value);
        else if (value instanceof Character) return builder.appendCharPrimitive((Character) value);
        else if (value instanceof char[]) return builder.appendCharPrimitive(((char[]) value)[0]);

        LoggingUtils.getEvoLogger().info("IT HAS NOT BEEN SUPPORTED YET: " + assignment);

        return null;
    }

    private VariableReference parseConstructorCall(CtConstructorCall<?> constructorCall) {
        LoggingUtils.getEvoLogger().info("constructorCall is: " + constructorCall);

        GenericClass<?> clazz = resolveGenericClass(constructorCall.getType());
        if (clazz == null) return null;
        LoggingUtils.getEvoLogger().info("Resolved class: " + clazz);

        Constructor<?> matched = constructorMatches(clazz, constructorCall);
        if (matched == null) return null;
        LoggingUtils.getEvoLogger().info("Resolved Method: " + matched);

        List<CtExpression<?>> args = constructorCall.getArguments();
        VariableReference[] parameters = parseParameters(args, matched.getParameterTypes(), matched.isVarArgs());
        LoggingUtils.getEvoLogger().info("Resolved Parameters: " + Arrays.toString(parameters));

        // TODO: we can try with others candidate
        if (parameters.length != matched.getParameterCount()) {
            LoggingUtils.getEvoLogger().warn("The Number of resolved parameters are " + parameters.length + " but the numbers should be " + matched.getParameterCount());
            return null;
        }

        return builder.appendConstructor(matched, clazz, parameters);
    }

    private Constructor<?> constructorMatches(GenericClass<?> clazz, CtConstructorCall<?> constructorCall) {
        CtTypeReference<?> typeReference = constructorCall.getType();
        LoggingUtils.getEvoLogger().info("ClassName: " + typeReference.getQualifiedName());

        try {
            for (Constructor<?> constructor : clazz.getRawClass().getConstructors()) {
                constructor.setAccessible(true);
                LoggingUtils.getEvoLogger().info("constructor: " + constructor);

                if (areConstructorParametersMatching(constructor, constructorCall)) {
                    LoggingUtils.getEvoLogger().info("Resolved constructor: " + constructor);
                    return constructor;
                }
            }

            for (Constructor<?> constructor : clazz.getRawClass().getConstructors()) {
                constructor.setAccessible(true);

                if (areConstructorParametersMatchingLooseTypeChecking(constructor, constructorCall)) {
                    LoggingUtils.getEvoLogger().info("Resolved constructor With Loose Type Checking: " + constructor);
                    return constructor;
                }
            }

        } catch (NoClassDefFoundError e) {
            LoggingUtils.getEvoLogger().warn("Could not resolve constructor: " + constructorCall, e);
        }

        return null;
    }

    private VariableReference parseMethodInvocation(CtInvocation<?> invocation) {
        CtExpression<?> target = invocation.getTarget();
        CtExecutableReference<?> method = invocation.getExecutable();
        LoggingUtils.getEvoLogger().info("invocation is: " + invocation + " target is: " + target + " method is: " + method);

        // Mock Method
        VariableReference var = parseMockMethod(invocation);
        if (var != null)
            return var;

        // Static Method
        var = parseStaticMethod(invocation);
        if (var != null)
            return var;

        // Instance Method
        var = parseInstanceMethod(invocation);
        if (var != null)
            return var;

        parseAssertionsBody(invocation);
        return null;
    }

    private VariableReference parseMockMethod(CtInvocation<?> invocation) {
        CtExpression<?> target = invocation.getTarget();

        // Ensure if it has a target it would be Mockito
        if (target instanceof CtTypeAccessImpl<?>) {
            CtTypeAccessImpl<?> accessedType = (CtTypeAccessImpl<?>) target;
            // Check if the accessed type is "Mockito"
            if (!"Mockito".equals(accessedType.getAccessedType().getSimpleName()))
                return null;
        }

        String methodName = invocation.getExecutable().getSimpleName();

        switch (methodName) {
            case "mock":
                List<CtExpression<?>> args = invocation.getArguments();
                CtFieldRead<?> classUnderMock = (CtFieldRead<?>) args.get(0);

                // Parse arguments and handle the "mock" method
//                VariableReference[] parameters = parseParameters(args);
//
//                if (parameters.length == 0) return null;
//                System.out.println("p: " + parameters[0]);

                GenericClass<?> clazz = resolveGenericClass(classUnderMock.getVariable().getDeclaringType());

                if (clazz == null) return null;

//                System.out.println("c: " + clazz);
//                System.out.println("m: " + classUnderMock.getVariable().getDeclaringType());
                return this.builder.appendFunctionalMockMethod(clazz);

            case "when":
                // Skip handling "when"
                break;

            default:
                // Handle other methods if necessary
                break;
        }

        return null;
    }

    private VariableReference parseStaticMethod(CtInvocation<?> invocation) {
        /*  Example1: invocation is: Arrays.asList("This", "is", "a", "sample", "sentence") target is: Arrays method is: asList(java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String)
            Arg to Pars is: Arrays void class spoon.support.reflect.code.CtTypeAccessImpl
            Example2: Tokenizer tokenizer = TokenizerAnnotator.getTokenizer(reader);
        */

        CtExpression<?> ctCallee = invocation.getTarget();

        if (!(ctCallee instanceof CtTypeAccessImpl)) return null;

        CtTypeAccessImpl<?> ctClass = (CtTypeAccessImpl<?>) ctCallee;

        if (ctClass.getAccessedType().getSimpleName().equals("Assert")) return null;

        if (DEBUG)
            LoggingUtils.getEvoLogger().info("Static Method is with this callee: " + ctClass + " type: " + ctClass.getType() + " accessed type: " + ctClass.getAccessedType());

        Class<?> clazz = resolveClass(ctClass.getAccessedType());

        if (clazz == null) return null;

        LoggingUtils.getEvoLogger().info("Resolved Static Class is: " + clazz);

        try {
            return buildMethod(null, clazz, invocation);
        } catch (IllegalArgumentException exception) {
            // It would be probably non-static method called as static, and try to init an object first
            LoggingUtils.getEvoLogger().warn("Static Method Exception: ", exception);

            // Try with a new constructor with zero-parameter
            Constructor<?> constructor = findConstructorWithZeroArgument(clazz);
            if (constructor == null) return null;

            VariableReference var = builder.appendConstructor(constructor);
            addVariable(var);

            return buildMethod(var, clazz, invocation);
        }
    }

    private VariableReference parseInstanceMethod(CtInvocation<?> invocation) {
        LoggingUtils.getEvoLogger().info("* parseInstanceMethod: " + invocation);

        VariableReference callee = findCallee(invocation);

        if (callee == null) return null;
        LoggingUtils.getEvoLogger().info("* callee is: " + callee + " callee.getVariableClass(): " + callee.getVariableClass());

        return buildMethod(callee, callee.getVariableClass(), invocation);
    }

    private VariableReference findCallee(CtInvocation<?> invocation) {
        CtExpression<?> target = invocation.getTarget();
//        System.out.println("target is: " + target + " " + target.getType() + " " + target.getClass());

        if (target.getType() == null && !(target instanceof CtInvocation<?>)) {
            return null;
        }

        return resolveInlineExpressionStatement(target);

    }

    private void parseAssertionsBody(CtInvocation<?> invocation) {
        LoggingUtils.getEvoLogger().info("* parseAssertBody: " + invocation);
        String methodName = invocation.getExecutable().getSimpleName();

        if (methodName.contains("assert")) {
            parseParameters(invocation.getArguments());
        }
    }

    private VariableReference buildMethod(VariableReference
                                                  callee, Class<?> methodClazz, CtInvocation<?> invocation) {
        // 1. parse method a
        // 2. parse args (b,c,...)
        // 3. return the desired a(b,c,...)

        // parse method
        Method method = resolveMethod(methodClazz, invocation);

        LoggingUtils.getEvoLogger().info("resolved method: " + method);

        if (method == null) {
            return null;
        }

        List<CtExpression<?>> args = invocation.getArguments();

        VariableReference[] parameters = parseParameters(args, method.getParameterTypes(), method.isVarArgs());

        for (VariableReference var : parameters) {
            LoggingUtils.getEvoLogger().info("Parameter: " + var);
        }

        // TODO: we can try with others candidate
        if (parameters.length != method.getParameterCount()) {
            LoggingUtils.getEvoLogger().warn("The Number of resolved parameters are " + parameters.length + " but the numbers should be " + method.getParameterCount());
            return null;
        }

        LoggingUtils.getEvoLogger().info("Resolved method: " + method);

        return builder.appendMethod(callee, method, parameters);
    }

    /**
     * Parses an array declaration or initialization.
     */
    private VariableReference parseArrayStatement(CtNewArray<?> newArray) {
        List<CtExpression<Integer>> dimensionExpressions = newArray.getDimensionExpressions();
        List<CtExpression<?>> elements = newArray.getElements();
        List<Integer> arrayDimension = getArrayDimensions(newArray);

        // Log information about the array statement
        LoggingUtils.getEvoLogger().info(String.format(
                "Parsing array statement -> Dimensions: %s, Elements: %s, Type: %s",
                dimensionExpressions, elements, newArray.getType()
        ));

        // Resolve array class type
        Class<?> arrayClass = resolveClass(newArray.getType());

        int[] dimensionsArray = arrayDimension.stream().mapToInt(Integer::intValue).toArray();
        ArrayReference arrVar = builder.appendArrayStmt(arrayClass, dimensionsArray);

        // Handle array assignment case
        if (!elements.isEmpty()) {
            initializeArrayFromCtNewArray(newArray, arrVar, new ArrayList<>());
        }
        return arrVar;
    }

    private void initializeArrayFromCtNewArray(CtNewArray<?> newArray, ArrayReference
            arrVar, List<Integer> indexes) {
        // Get elements from CtNewArray
        List<CtExpression<?>> elements = newArray.getElements();

        if (elements.isEmpty()) {
            return; // Empty array
        }

        // Check if elements are nested CtNewArray (multi-dimensional array)
        if (elements.get(0) instanceof CtNewArray) {
            // right now EvoSuite doesn't support it!!!!

            int size = elements.size();
            for (int i = 0; i < size; i++) {
                indexes.add(i);
                initializeArrayFromCtNewArray((CtNewArray<?>) elements.get(i), arrVar, indexes);
            }
        } else {
            // Base case: it's a 1D array
            for (int i = 0; i < elements.size(); i++) {
                CtExpression<?> element = elements.get(i);
                indexes.add(i);
                buildArrayAssignment(element, arrVar, indexes);
                indexes.remove(0);
            }
        }
    }

    private VariableReference parseArrayRead(CtArrayRead<?> arrayRead) {
        if (getArrayDimension(arrayRead) == null)
            return null;

        int index = getArrayDimension(arrayRead);
        VariableReference arrVar = findVariable(arrayRead.getTarget().toString());

        if (!(arrVar instanceof ArrayReference))
            return null;

        // Log information about the array statement
        LoggingUtils.getEvoLogger().info(String.format(
                "Parsing array statement -> Dimensions: %s, Elements: %s, Type: %s",
                index, arrVar, arrVar.getType()
        ));

        return builder.appendAssignment((ArrayReference) arrVar, index);
    }

    private VariableReference parseVarArgsArray(Class<?> parameterType, CtExpression<?>... args) {
        ArrayReference arrVar;

        if(args[0] == null || args[0].getType() == null){
            arrVar = builder.appendArrayStmt(parameterType, args.length);
        } else if (args[0].getType().getSimpleName().equals("<nulltype>")) {
            arrVar = builder.appendArrayStmt(parameterType, args.length);
        } else {

            GenericClass<?> clazz = resolveGenericClass(args[0].getType(), true);

            // 2 Dimensional Array
            if (args[0] instanceof CtNewArray) {
                LoggingUtils.getEvoLogger().info("2 Dimensional VarArgs");

                CtNewArray<?> array = (CtNewArray<?>) args[0];
                int arraySize = array.getElements().size();
                int size = args.length;

                if (clazz != null) {
                    arrVar = builder.appendArrayStmt(clazz.getType(), size, arraySize);
                    LoggingUtils.getEvoLogger().info("parameterType: " + parameterType + " " + arrVar.getType() + " " + clazz.getTypeName());
                } else {
                    arrVar = builder.appendArrayStmt(parameterType, size, arraySize);
                    LoggingUtils.getEvoLogger().info("parameterType: " + parameterType + " " + arrVar.getType());
                }

                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < arraySize; j++) {
                        CtNewArray<?> currentArr = (CtNewArray<?>) args[i];
                        ArrayList<Integer> indexes = new ArrayList<>();
                        indexes.add(i);
                        indexes.add(j);

                        CtExpression<?> element = currentArr.getElements().get(j);
                        LoggingUtils.getEvoLogger().info("Element:" + element + " i: " + i + " j: " + j);
                        buildArrayAssignment(element, arrVar, indexes);
                    }
                }
            } else {
                if (clazz != null) {
                    arrVar = builder.appendArrayStmt(clazz.getType(), args.length);
                    LoggingUtils.getEvoLogger().info("parameterType: " + parameterType + " " + arrVar.getType() + " " + clazz.getTypeName());
                } else {
                    arrVar = builder.appendArrayStmt(parameterType, args.length);
                }

                for (int i = 0; i < args.length; i++) {
                    CtExpression<?> element = args[i];
                    buildArrayAssignment(element, arrVar, i);
                }
            }
        }

        return arrVar;
    }

    private void buildArrayAssignment(CtExpression<?> ctAssignment, ArrayReference
            arrayVar, List<Integer> indexes) {
        VariableReference var = (ctAssignment.getType() == null || ctAssignment.getType().getSimpleName().isEmpty() || "<nulltype>".equalsIgnoreCase(ctAssignment.getType().getSimpleName()))
                ? resolveInlineExpressionStatement(ctAssignment, arrayVar.getComponentType())
                : resolveInlineExpressionStatement(ctAssignment);

        LoggingUtils.getEvoLogger().info("ArrayVar: " + arrayVar + " indexes: " + indexes + " var: " + var);

        if (var == null) return;

        builder.appendAssignment(arrayVar, indexes, var);
    }

    private void buildArrayAssignment(CtExpression<?> ctAssignment, ArrayReference arrayVar, int indexes) {
        VariableReference var = (ctAssignment.getType() == null || ctAssignment.getType().getSimpleName().isEmpty() || "<nulltype>".equalsIgnoreCase(ctAssignment.getType().getSimpleName()))
                ? resolveInlineExpressionStatement(ctAssignment, arrayVar.getComponentType())
                : resolveInlineExpressionStatement(ctAssignment);

        if (var == null) return;

        builder.appendAssignment(arrayVar, indexes, var);
    }

    /**
     * Parses a field read expression (handles both instance and static field accesses).
     */
    private VariableReference parseFieldRead(CtFieldReadImpl<?> ctField) {
        LoggingUtils.getEvoLogger().info("Resolved field: " + ctField + " target: " + ctField.getTarget() + " variable: " + ctField.getVariable());

        if (ctField.getTarget() == null) {
            return null; // Field access without a target is invalid
        }

        // Handles static field access (e.g., ClassName.staticField)
        if (ctField.getTarget() instanceof CtTypeAccessImpl<?>) {
            GenericClass<?> clazz = resolveGenericClass(ctField.getVariable().getDeclaringType());
            if (clazz == null) return null;
            return builder.appendClassPrimitive(clazz.getRawClass());
        }

        // Handles instance field access (e.g., object.field)
//        VariableReference targetVar = resolveExpressionStatement(ctField.getTarget());
//        if (targetVar == null) return null;
//
//        return addVariable(builder.appendAssignment(targetVar, ctField.getVariable().getSimpleName()););
        return null;
    }


    /**
     * Resolve Parameters: including constructor or method parameters
     */
    private VariableReference[] parseParameters(List<CtExpression<?>> args, Class<?>[] parameterTypes,
                                                boolean isVarArgs) {
        List<VariableReference> vars = new ArrayList<>();
        LoggingUtils.getEvoLogger().info("ParamTypes:" + Arrays.toString(parameterTypes));

        if (DEBUG)
            for (CtExpression<?> arg : args) {
                LoggingUtils.getEvoLogger().info("Arg is: " + arg + " " + arg.getType() + " " + arg.getClass().toString());
            }

        for (int i = 0; i < parameterTypes.length; i++) {
            VariableReference var;

            if (isVarArgs && i == parameterTypes.length - 1) {
                System.out.println();
                var = parseVarArgsArray(parameterTypes[i], findVarArgs(args, parameterTypes));
            } else {
                CtExpression<?> arg = args.get(i);
                var = (arg.getType() == null || arg.getType().getSimpleName().isEmpty() || "<nulltype>".equalsIgnoreCase(arg.getType().getSimpleName()))
                        ? resolveInlineExpressionStatement(arg, parameterTypes[i])
                        : resolveInlineExpressionStatement(arg);
            }

            if (var != null) {
                vars.add(var);
                LoggingUtils.getEvoLogger().info("Param Var is: " + var);
            }
        }

        return vars.toArray(new VariableReference[0]);
    }

    private CtExpression<?>[] findVarArgs(List<CtExpression<?>> args, Class<?>[] parameterTypes) {
        int difference = args.size() - parameterTypes.length + 1;
        CtExpression<?>[] varArgs = new CtExpression[difference];
        int index = 0;
        for (int i = parameterTypes.length - 1; i < args.size(); i++) {
            varArgs[index] = args.get(i);
            index++;
        }
        return varArgs;
    }

    private VariableReference[] parseParameters(List<CtExpression<?>> args) {
        return args.stream().map(this::resolveInlineExpressionStatement).filter(Objects::nonNull).toArray(VariableReference[]::new);
    }


    private boolean areConstructorParametersMatching
            (Constructor<?> constructor, CtConstructorCall<?> constructorCall) {
        return areParametersMatching(constructorCall.getArguments(), constructor.getParameterTypes(), constructor.isVarArgs());
    }

    private boolean areConstructorParametersMatchingLooseTypeChecking
            (Constructor<?> constructor, CtConstructorCall<?> constructorCall) {
        return areParametersMatchingLooseTypeChecking(constructorCall.getArguments(), constructor.getParameterTypes(), constructor.isVarArgs());
    }

    private boolean areMethodParametersMatching(Method method, CtInvocation<?> ctInvocation) {
        return areParametersMatching(ctInvocation.getArguments(), method.getParameterTypes(), method.isVarArgs());
    }

    private boolean areMethodParametersMatchingLooseTypeChecking(Method method, CtInvocation<?> ctInvocation) {
        return areParametersMatchingLooseTypeChecking(ctInvocation.getArguments(), method.getParameterTypes(), method.isVarArgs());
    }

    private boolean areParametersMatching(List<CtExpression<?>> ctArgs, Class<?>[] parameterTypes,
                                          boolean isVarArgs) {
        if (ctArgs.size() < parameterTypes.length) return false;
        if (!isVarArgs && ctArgs.size() != parameterTypes.length) return false;

        for (int i = 0; i < parameterTypes.length; i++) {
            CtExpression<?> ctArg = ctArgs.get(i);
            CtTypeReference<?> ctType = ctArg.getType();

            if (DEBUG)
                LoggingUtils.getEvoLogger().info("Arg " + i + ": " + ctArg + " of type " + ctType);

            if (ctType == null) { // If the type is null, assume it's a mismatch
                return false;
            }

            if (stringTypesMatching(ctType, parameterTypes[i])) {
                continue;
            }

            // VarArgs
            if (isVarArgs && parameterTypes[i].isArray() && i == parameterTypes.length - 1) {
                LoggingUtils.getEvoLogger().info("VarArgs: " + parameterTypes[i]);
                if (stringTypesMatchingVarArgs(ctType, parameterTypes[i]))
                    return true;
            }

            if (ctType.getSimpleName().equals("<nulltype>"))
                continue;

            // Load the corresponding class for the CtTypeReference
            Class<?> ctClass = resolveClass(ctType, isVarArgs);
            ctClass = primitiveToObject(ctClass);

            LoggingUtils.getEvoLogger().info("ctClass: " + ctClass);
            if (ctClass == null) return false;

            // Check if the parameter type is assignable from the argument's type
            if (!parameterTypes[i].isAssignableFrom(ctClass)) {
                return false;
            }
        }

        return true;
    }

    private boolean areParametersMatchingLooseTypeChecking(List<CtExpression<?>> ctArgs, Class<?>[]
            parameterTypes, boolean isVarArgs) {
        if (ctArgs.size() < parameterTypes.length) return false;
        if (!isVarArgs && ctArgs.size() != parameterTypes.length) return false;

        for (int i = 0; i < parameterTypes.length; i++) {
            CtExpression<?> ctArg = ctArgs.get(i);
            CtTypeReference<?> ctType = ctArg.getType();

            if (ctType == null) { // If the type is null, assume it's a mismatch
                continue;
            }

            if (stringTypesMatching(ctType, parameterTypes[i])) {
                continue;
            }

            // VarArgs
            if (isVarArgs && parameterTypes[i].isArray() && i == parameterTypes.length - 1) {
                LoggingUtils.getEvoLogger().info("VarArgs: " + parameterTypes[i]);
                if (stringTypesMatchingVarArgs(ctType, parameterTypes[i]))
                    return true;
            }

            if (ctType.getSimpleName().equals("<nulltype>") || ctType.getSimpleName().equals("void"))
                continue;

            // Load the corresponding class for the CtTypeReference
            Class<?> ctClass = resolveClass(ctType, isVarArgs);
            ctClass = primitiveToObject(ctClass);
            LoggingUtils.getEvoLogger().info("ctClass: " + ctClass);

            if (ctClass == null) continue;

            // Check if the parameter type is assignable from the argument's type
            if (!parameterTypes[i].isAssignableFrom(ctClass)) {
                return false;
            }
        }

        return true;
    }

    private Class<?> resolvePrimitveClass(String className) {
        switch (className) {
            case "boolean":
                return boolean.class;
            case "int":
                return int.class;
            case "long":
                return long.class;
            case "double":
                return double.class;
            case "float":
                return float.class;
            case "void":
                return void.class;
            case "byte":
                return byte.class;
            case "short":
                return short.class;
            case "char":
                return char.class;
            default:
                return null;
        }
    }

    private Class<?> primitiveToObject(Class<?> clazz) {
        if (clazz == null)
            return null;

        if (clazz.isPrimitive()) {
            if (clazz.equals(boolean.class)) {
                return Boolean.class;
            } else if (clazz.equals(int.class)) {
                return Integer.class;
            } else if (clazz.equals(long.class)) {
                return Long.class;
            } else if (clazz.equals(double.class)) {
                return Double.class;
            } else if (clazz.equals(float.class)) {
                return Float.class;
            } else if (clazz.equals(byte.class)) {
                return Byte.class;
            } else if (clazz.equals(short.class)) {
                return Short.class;
            } else if (clazz.equals(char.class)) {
                return Character.class;
            } else if (clazz.equals(void.class)) {
                return Void.class;
            }
        }
        return clazz;
    }

    /**
     * Resolve a class
     */
    private Class<?> resolveClass(CtTypeReference<?> typeReference, boolean isVarArgs) {
        String className = typeReference.getQualifiedName();
        String simpleClassName = typeReference.getSimpleName();

        int dimensions = 1;
        if (typeReference.isArray()) {
            dimensions = className.split("\\[\\]", -1).length - 1;

            className = className.replaceAll("(\\[\\])+$", "");
            simpleClassName = simpleClassName.replaceAll("(\\[\\])+$", "");
        }

        Class<?> clazz = resolvePrimitveClass(className);

        if (clazz == null) {
            try {
                clazz = this.testCluster.getClass(className);
            } catch (ClassNotFoundException e1) {
                LoggingUtils.getEvoLogger().warn("Class not found: " + className + ", trying simple name: " + simpleClassName);
                try {
                    clazz = this.testCluster.getClass(simpleClassName);
                } catch (ClassNotFoundException e2) {
                    try {
                        clazz = Class.forName(className);
                    } catch (ClassNotFoundException e3) {
//                    LoggingUtils.getEvoLogger().info("testCluster is: " + testCluster);
                        LoggingUtils.getEvoLogger().warn("Failed to resolve class: " + typeReference + " or " + typeReference.getSimpleName(), e2);
                    }
                }
            }
        }

        // ArrayType
        if (clazz != null && typeReference.isArray()) {
            int[] dimArray = new int[dimensions];
//            System.out.println(dimensions);
            clazz = Array.newInstance(clazz, dimArray).getClass();
        }

        if (clazz != null && isVarArgs) {
            int[] dimArray = new int[1];
            clazz = Array.newInstance(clazz, dimArray).getClass();
        }

        return clazz;
    }

    private Class<?> resolveClass(CtTypeReference<?> typeReference) {
        return resolveClass(typeReference, false);
    }

    private GenericClass<?> resolveGenericClass(CtTypeReference<?> typeReference, boolean isVarArgs) {
        Class<?> clazz = resolveClass(typeReference, isVarArgs);

        // Generic Type
        if (clazz != null && !typeReference.getActualTypeArguments().isEmpty()) {
            ArrayList<Type> parameterizedClasses = new ArrayList<>();
            for (CtTypeReference<?> ctType : typeReference.getActualTypeArguments()) {
                if (ctType.getSimpleName().equals("?") || ctType.getSimpleName().equals("<omitted>")) {
                    parameterizedClasses.add(TypeUtils.wildcardType().withUpperBounds(Object.class).build());
                } else {
                    Class<?> resolvedClass = resolveClass(ctType);
                    if (resolvedClass != null) {
                        parameterizedClasses.add(resolvedClass);
                    }
                }
            }
            if (parameterizedClasses.size() == typeReference.getActualTypeArguments().size()) {
                try {
                    Type type = TypeUtils.parameterize(clazz, parameterizedClasses.toArray(new Type[0]));
                    return GenericClassFactory.get(type);
                } catch (IllegalArgumentException e) {
                    LoggingUtils.getEvoLogger().warn("The number of parameterized were not similar! probably more parametrized types were need");
                }
            }
//            System.out.println("Generic Arguments: " + typeReference.getActualTypeArguments());

            return GenericClassFactory.get(clazz);
        } else if (clazz != null) {
            return GenericClassFactory.get(clazz);
        }

        return null;
    }

    private GenericClass<?> resolveGenericClass(CtTypeReference<?> typeReference) {
        return resolveGenericClass(typeReference, false);
    }

    private Method resolveMethod(Class<?> clazz, CtInvocation<?> invocation) {
        CtExecutableReference<?> ctMethod = invocation.getExecutable();

        for (Method method : clazz.getMethods()) {
//            LoggingUtils.getEvoLogger().info("method match is: " + method + " invocation is: " + ctMethod.getSimpleName() + " varArgs: " + method.isVarArgs());

            if (method.getName().equals(ctMethod.getSimpleName())) {
                if (areMethodParametersMatching(method, invocation)) {
                    return method;
                }
            }
        }

        for (Method method : clazz.getMethods()) {
            if (method.getName().endsWith("." + ctMethod.getSimpleName())) {
                if (areMethodParametersMatching(method, invocation)) {
                    return method;
                }
            }
        }

        for (Method method : clazz.getMethods()) {
            if (method.getName().equals(ctMethod.getSimpleName()) || method.getName().endsWith("." + ctMethod.getSimpleName())) {
                if (areMethodParametersMatchingLooseTypeChecking(method, invocation)) {
                    LoggingUtils.getEvoLogger().info("Resolved Method With Loose Type Checking: " + method);
                    return method;
                }
            }
        }

        return null;
    }

    private boolean stringTypesMatching(CtTypeReference<?> ctType, Class<?> clazz) {
        String simpleTypeName = ctType.getSimpleName();
        String qualifiedTypeName = ctType.getQualifiedName();
        String classQualifiedTypeName = clazz.getTypeName();
        String classSimpleName = clazz.getSimpleName();

        return classQualifiedTypeName.equals(qualifiedTypeName) || classSimpleName.equals(simpleTypeName);
    }

    private boolean stringTypesMatchingVarArgs(CtTypeReference<?> ctType, Class<?> clazz) {
        String simpleTypeName = ctType.getSimpleName();
        String qualifiedTypeName = ctType.getQualifiedName();
        String classQualifiedTypeName = clazz.getTypeName().replaceAll("(\\[\\])+$", "");
        String classSimpleName = clazz.getSimpleName().replaceAll("(\\[\\])+$", "");

        return classQualifiedTypeName.equals(qualifiedTypeName) || classSimpleName.equals(simpleTypeName);
    }

    /**
     * Static Methods such as helper functions
     */
    private static Constructor<?> findConstructorWithZeroArgument(Class<?> clazz) {
        for (Constructor<?> constructor : clazz.getConstructors()) {
            constructor.setAccessible(true);

            if (constructor.getParameterCount() == 0) return constructor;
        }
        return null;
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

    public static Integer getArrayDimension(CtArrayRead<?> array) {
        try {
            return (Integer.parseInt(array.getIndexExpression().toString())); // Extract size
        } catch (NumberFormatException e) {
            return null; // Unknown size (e.g., variable reference)
        }
    }

    public static List<Integer> getArrayDimensions(CtArrayWrite<?> array) {
        List<Integer> dimensions = new ArrayList<>();

        CtExpression<?> current = array;

        while (current instanceof CtArrayAccess) {
            String index = ((CtArrayAccess<?, ?>) current).getIndexExpression().toString();
//            System.out.println(current);

            dimensions.add(Integer.parseInt(index));
            current = ((CtArrayAccess<?, ?>) current).getTarget();
        }

        return Lists.reverse(dimensions);
    }


    /**
     * Handle Variables such as find and adds
     */
    private VariableReference findVariable(String name) {
        for (Map.Entry<String, VariableReference> variable : variableReferences.entrySet()) {
            if (variable.getKey().equals(name)) {
                return variable.getValue();
            }
        }
        return null;
    }

    // add an inline variable
    private VariableReference addVariable(VariableReference var) {
        String name = "INLINE_VARIABLE" + position;
        position++;
        return addVariable(name, var);
    }

    private VariableReference addVariable(String name, VariableReference var) {
        variableReferences.put(name, var);
        LoggingUtils.getEvoLogger().info("Variable added: " + name + " , " + var);
        return var;
    }

    // Haven't been handled yet!
    private VariableReference parseUnaryOperator
    (CtExpression<?> assignment, CtLocalVariableReference<?> reference) {
        LoggingUtils.getEvoLogger().info("IT HAS NOT BEEN SUPPORTED YET: " + assignment);

        return null;
    }
}
