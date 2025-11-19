package org.evosuite.testcase.variable.name;

import com.apollographql.apollo3.api.ApolloResponse;
import org.apache.commons.lang3.CharUtils;
import org.evosuite.llm.GraphQLClient;
import org.evosuite.llm.PromptQuery;
import org.evosuite.testcase.variable.ArrayReference;
import org.evosuite.testcase.variable.VariableReference;
import org.evosuite.utils.LoggingUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A name strategy that is based on variable type.
 * <br>
 * <b>Examples:</b> integer0, string0, string1, ...
 *
 * @author Afonso Oliveira
 */
public class LLMBasedVariableNameStrategy extends AbstractVariableNameStrategy {

    protected final Map<String, Integer> nextIndices = new ConcurrentHashMap<>();
    GraphQLClient client = new GraphQLClient();

    HeuristicsVariableNameStrategy heuristicsVariableNameStrategy = new HeuristicsVariableNameStrategy();

    @Override
    public String createNameForVariable(VariableReference var) {
        LoggingUtils.getEvoLogger().info(" ** Suggest name for: " + var);
        String suggestName = heuristicsVariableNameStrategy.createNameForVariable(var);
        String variableName = getLLMSuggestName(suggestName);
        return getIndexIncludingFirstAppearance(variableName);
    }

    public String getLLMSuggestName(String suggestName){
        ApolloResponse<PromptQuery.Data> result = client.promptImproveVariableQuery(suggestName).blockingGet();
        if(result.data == null) {
            LoggingUtils.getEvoLogger().info("** Null Pointer Exception");
            return suggestName;
        }
        String testResponse = result.data.prompt.llmResponse;
        if(testResponse.contains("ERROR:")){
            LoggingUtils.getEvoLogger().info("** Error is: " + result.data.prompt.llmResponse);
            return suggestName;
        }

        return testResponse;
    }

    /**
     * Returns the variable name + the number of repetitions counting from 0.
     * i.e. If the variable appears only once in the test, it is named as variable0.
     *
     * Mainly used for Type-Based Renaming Strategy (traditional naming in EvoSuite).
     *
     * @return String
     */
    private String getIndexIncludingFirstAppearance(String variableName) {
        if (!nextIndices.containsKey(variableName)) {
            nextIndices.put(variableName, 0);
        }
        int index = nextIndices.get(variableName);
        nextIndices.put(variableName, index + 1);
        return variableName += index;
    }
    public void addVariableInformation(Map<String, Map<VariableReference, String>> information){
        //If needed any information about types
    }

}
