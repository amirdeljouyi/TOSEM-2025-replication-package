package org.evosuite.llm;

import com.apollographql.apollo3.api.ApolloResponse;
import org.evosuite.utils.LoggingUtils;

public class LLMHandler {

    GraphQLClient client;
    public LLMHandler(){
        this.client = new GraphQLClient();
    }

    public String improveUnderstandability(String test){
        LoggingUtils.getEvoLogger().info("** Improve Understandability: " + "** Test Code is: " + test);

        try {
            ApolloResponse<PromptQuery.Data> result = client.promptQuery(test).blockingGet();
            if(result.data == null) {
                LoggingUtils.getEvoLogger().info("** Null Pointer Exception");
                return null;
            }
            String testResponse = result.data.prompt.llmResponse;
            if(testResponse.contains("ERROR:")){
                LoggingUtils.getEvoLogger().info("** Error is: " + result.data.prompt.llmResponse);
                return null;
            }
//            LoggingUtils.getEvoLogger().info("Test Response is: " + testResponse);
            return testResponse;
        } catch (Exception exp) {
            throw new RuntimeException(exp);
        }
    }

    public String parseLLMTest(String testResponse){
        return removeTwoFirstAndLast(testResponse);
    }

    public String suggestTestName(String test){
        ApolloResponse<PromptQuery.Data> result = client.promptSuggestTestNameQuery(test).blockingGet();
        if(result.data == null) {
            LoggingUtils.getEvoLogger().info("** Null Pointer Exception");
            return null;
        }
        String testResponse = result.data.prompt.llmResponse;
        if(testResponse.contains("ERROR:")){
            LoggingUtils.getEvoLogger().info("** Error is: " + result.data.prompt.llmResponse);
            return null;
        }
        LoggingUtils.getEvoLogger().info("refined test name is: " + result.data.prompt.llmResponse);


        return removeParentheses(result.data.prompt.llmResponse);
    }

    public String improveTestData(String test){
        ApolloResponse<PromptQuery.Data> result = client.promptImproveTestDataQuery(test).blockingGet();
        if(result.data == null) {
            LoggingUtils.getEvoLogger().info("** Null Pointer Exception");
            return null;
        }
        String testResponse = result.data.prompt.llmResponse;
        if(testResponse.contains("ERROR:")){
            LoggingUtils.getEvoLogger().info("** Error is: " + result.data.prompt.llmResponse);
            return null;
        }
//        LoggingUtils.getEvoLogger().info("refined test data is: " + result.data.prompt.llmResponse);


        return result.data.prompt.llmResponse;
    }

    public String generateTestCase(String method){
        ApolloResponse<PromptQuery.Data> result = client.promptGenerateTestCaseQuery(method).blockingGet();
        if(result.data == null) {
            LoggingUtils.getEvoLogger().info("** Null Pointer Exception");
            return null;
        }
        String testResponse = result.data.prompt.llmResponse;
        if(testResponse.contains("ERROR:")){
            LoggingUtils.getEvoLogger().info("** Error is: " + result.data.prompt.llmResponse);
            return null;
        }
//        LoggingUtils.getEvoLogger().info("refined test data is: " + result.data.prompt.llmResponse);


        return result.data.prompt.llmResponse;
    }

    public String generateTestCaseCodaMosa(String method){
        ApolloResponse<PromptQuery.Data> result = client.promptGenerateCodaMosaTestCaseQuery(method).blockingGet();
        if(result.data == null) {
            LoggingUtils.getEvoLogger().info("** Null Pointer Exception");
            return null;
        }
        String testResponse = result.data.prompt.llmResponse;
        if(testResponse.contains("ERROR:")){
            LoggingUtils.getEvoLogger().info("** Error is: " + result.data.prompt.llmResponse);
            return null;
        }
//        LoggingUtils.getEvoLogger().info("refined test data is: " + result.data.prompt.llmResponse);


        return result.data.prompt.llmResponse;
    }

    public String generatePool(){
        SourceParser sourceParser = new SourceParser();
        String javadocs = sourceParser.getClassJavaDoc();
        LoggingUtils.getEvoLogger().info("** Javadocs is: " + javadocs);

        ApolloResponse<PromptQuery.Data> result = client.promptPoolQuery(javadocs).blockingGet();
        if(result.data == null) {
            LoggingUtils.getEvoLogger().info("** Null Pointer Exception");
            return null;
        }
        String testResponse = result.data.prompt.llmResponse;
        if(testResponse.contains("ERROR:")){
            LoggingUtils.getEvoLogger().info("** Error is: " + result.data.prompt.llmResponse);
            return null;
        }
//        LoggingUtils.getEvoLogger().info("refined test data is: " + result.data.prompt.llmResponse);


        return result.data.prompt.llmResponse;
    }

    public static String removeTwoFirstAndLast(String str)
    {
        String[] lines = str.split("\\r?\\n");

        // Use a StringBuilder to efficiently build the final string
        StringBuilder result = new StringBuilder();
        // Append lines to the result, skipping the first two and the last one
        for (int i = 3; i < lines.length - 1; i++) {
            result.append(removeTab(lines[i]));
            if (i < lines.length - 2) {
                result.append("\n");
            }
        }

        LoggingUtils.getEvoLogger().info("refined test is: " + result);

        return result.toString();
    }

    public static String removeTab(String str)
    {
        if(str.length() < 5)
            return str;

        // Creating a StringBuilder object
        StringBuilder sb = new StringBuilder(str);

        // Removing the first character
        // of a string
        sb.deleteCharAt(0);
        sb.deleteCharAt(0);
        sb.deleteCharAt(0);
        sb.deleteCharAt(0);

        // Converting StringBuilder into a string
        // and return the modified string
        return sb.toString();
    }

    public static String removeParentheses(String str){
        return str.split("\\(\\)")[0];
    }

}
