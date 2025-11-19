package org.evosuite.llm;

import org.evosuite.Properties;
import org.evosuite.utils.LoggingUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class StaticPoolGeneration {

    String[] lines;

    public StaticPoolGeneration() {
        String llmPool = "";

        if(Properties.LLM_STATIC_CONSTANT_POOL_APPROACH == Properties.LLMGenerationApproach.API){
            LLMHandler llmHandler = new LLMHandler();
            llmPool = llmHandler.generatePool();
        } else if (Properties.LLM_STATIC_CONSTANT_POOL_APPROACH == Properties.LLMGenerationApproach.FILE){
            try {
                // Directory where the test files are stored
                Path directoryPath = Paths.get(Properties.LLM_TEST_POOL_FILE_DIRECTORY);
                Path targetFilePath = getPath(directoryPath);

                LoggingUtils.getEvoLogger().info("Looking for test file at: " + targetFilePath);

                llmPool = new String(Files.readAllBytes(targetFilePath));
            } catch (IOException e) {
                LoggingUtils.getEvoLogger().error("Error reading the file: " + e.getMessage());
            }
        }
        LoggingUtils.getEvoLogger().info("LLM Pool is: " + llmPool);
        this.lines = llmPool.split("\\R");
    }

    private static @NotNull Path getPath(Path directoryPath) {
        String className = Properties.LLM_TEST_POOL_FILE_DIRECTORY.substring(Properties.LLM_TEST_POOL_FILE_DIRECTORY.lastIndexOf("/") + 1);

        // Build the expected file name based on attempt number
        String targetFileName = className  + "_pool_StaticPool.txt";

        // Resolve the full path to the file
        return directoryPath.resolve(targetFileName);
    }

    public List<String> getLines(){
        return Arrays.asList(lines);
    }
}
