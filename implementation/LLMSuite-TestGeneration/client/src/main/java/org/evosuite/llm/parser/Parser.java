package org.evosuite.llm.parser;

import org.evosuite.testcase.TestCase;
import spoon.reflect.declaration.CtClass;

public interface Parser {

    TestCase parseTestSnippet(CtClass<?> ctClass);
}
