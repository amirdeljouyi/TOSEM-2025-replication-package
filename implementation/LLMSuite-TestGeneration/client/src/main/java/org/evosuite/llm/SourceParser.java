package org.evosuite.llm;

import org.evosuite.Properties;
import org.evosuite.utils.LoggingUtils;
import spoon.Launcher;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;

import java.util.ArrayList;
import java.util.List;

public class SourceParser {

    List<CtType<?>> allClasses;
    ArrayList<CtMethod<?>> allMethods = new ArrayList<>();

    public SourceParser() {
        LoggingUtils.getEvoLogger().info("LLM Source Directory is :" + Properties.LLM_SOURCE_DIRECTORY + ".java");

        final Launcher launcher = new Launcher();
        launcher.addInputResource(Properties.LLM_SOURCE_DIRECTORY + ".java");
        launcher.buildModel();
        launcher.getEnvironment().setComplianceLevel(11);
        Factory spoon = launcher.getFactory();
        //        CtModel model = spoon.getModel();
        allClasses = spoon.Type().getAll();
//        LoggingUtils.getEvoLogger().info("All classes: " + allClasses.size() + allClasses);

        retrieveAllMethods();
    }

    private void retrieveAllMethods(){
        for (CtType<?> ctType : allClasses) {
            allMethods.addAll(ctType.getAllMethods());
        }
        LoggingUtils.getEvoLogger().info("All methods: " + allMethods.size() + allMethods);
    }

    public String getClassJavaDoc(){
        StringBuilder sb = new StringBuilder();
        for (CtType<?> ctType : allClasses) {
            sb.append("The Javadoc for the class of " + ctType.getQualifiedName() + " is:\n");
            sb.append(ctType.getDocComment());
        }
        return sb.toString();
    }

    public String getMethodBody(String methodName){
        for (CtMethod<?> method : allMethods) {
            if (method.getSimpleName().equals(methodName)) {
                return method.toString();
            }
        }
        return null;
    }
}
