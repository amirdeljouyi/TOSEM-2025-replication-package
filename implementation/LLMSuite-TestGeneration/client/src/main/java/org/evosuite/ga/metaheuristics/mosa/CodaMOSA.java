/*
 * Copyright (C) 2010-2018 Gordon Fraser, Andrea Arcuri and EvoSuite
 * contributors
 *
 * This file is part of EvoSuite.
 *
 * EvoSuite is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3.0 of the License, or
 * (at your option) any later version.
 *
 * EvoSuite is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with EvoSuite. If not, see <http://www.gnu.org/licenses/>.
 */
package org.evosuite.ga.metaheuristics.mosa;

import org.evosuite.ClientProcess;
import org.evosuite.Properties;
import org.evosuite.ga.ChromosomeFactory;
import org.evosuite.ga.comparators.OnlyCrowdingComparator;
import org.evosuite.ga.operators.ranking.CrowdingDistance;
import org.evosuite.ga.operators.selection.BestKSelection;
import org.evosuite.ga.operators.selection.RandomKSelection;
import org.evosuite.ga.operators.selection.RankSelection;
import org.evosuite.ga.operators.selection.SelectionFunction;
import org.evosuite.llm.LLMTestCaseGeneration;
import org.evosuite.llm.SourceParser;
import org.evosuite.rmi.ClientServices;
import org.evosuite.rmi.service.ClientNodeLocal;
import org.evosuite.statistics.RuntimeVariable;
import org.evosuite.symbolic.MethodComparator;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.TestFitnessFunction;
import org.evosuite.utils.Listener;
import org.evosuite.utils.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;

/**
 * Implementation of CodaMosa for Java".
 *
 * @author Amir Deljouyi
 */
public class CodaMOSA extends AbstractMOSA {

    private static final long serialVersionUID = 146182080947267628L;

    private static final Logger logger = LoggerFactory.getLogger(CodaMOSA.class);

    /**
     * immigrant groups from neighbouring client
     */
    private final ConcurrentLinkedQueue<List<TestChromosome>> immigrants =
            new ConcurrentLinkedQueue<>();

    private final SelectionFunction<TestChromosome> emigrantsSelection;

    /**
     * Crowding distance measure to use
     */
    protected CrowdingDistance<TestChromosome> distance = new CrowdingDistance<>();

    private int countStable = 0;

    private int previousNumCoveredGoals = 0;
    private int numCoveredGoals = 0;
    private List<String> llmClasses = new ArrayList<>();
    private int llmUsedIndex = 0;
    int stabledBudget = Properties.LLM_STABLED_BUDGET;

    /**
     * Constructor based on the abstract class {@link AbstractMOSA}
     *
     * @param factory
     */
    public CodaMOSA(ChromosomeFactory<TestChromosome> factory) {
        super(factory);

        switch (Properties.EMIGRANT_SELECTION_FUNCTION) {
            case RANK:
                this.emigrantsSelection = new RankSelection<>();
                break;
            case RANDOMK:
                this.emigrantsSelection = new RandomKSelection<>();
                break;
            default:
                this.emigrantsSelection = new BestKSelection<>();
        }

        if (Properties.LLM_TEST_GENERATION_APPROACH == Properties.LLMGenerationApproach.FILES) {

            Path dirPath = Paths.get(Properties.LLM_TEST_FILE_SOURCE_DIRECTORY);
            LoggingUtils.getEvoLogger().info("Directory path is: " + dirPath);

            // Check if the path is a directory
            if (!Files.isDirectory(dirPath)) {
                LoggingUtils.getEvoLogger().error("Provided path is not a directory.");
            }

            // Process all files in the directory
            try (Stream<Path> paths = Files.list(dirPath)) {
                paths.filter(Files::isRegularFile)
                        .filter(path -> path.toString().endsWith(".java"))
                        .forEach(filePath -> {
                            try {
                                String content = new String(Files.readAllBytes(filePath));
                                LoggingUtils.getEvoLogger().info("Reading file: " + filePath);
                                LoggingUtils.getEvoLogger().info("Content: " + content);

                                llmClasses.add(content);
                            } catch (IOException e) {
                                LoggingUtils.getEvoLogger().error("Error reading file: " + filePath + " - " + e.getMessage());
                            }
                        });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (Properties.LLM_TEST_GENERATION_APPROACH == Properties.LLMGenerationApproach.FILE) {
            try {
                // Directory where the test files are stored
                Path directoryPath = Paths.get(Properties.LLM_TEST_FILE_SOURCE_DIRECTORY);

                // Get the class name from another property or variable if available
                String className = Properties.getTargetClassAndDontInitialise().getSimpleName();

                // Build the expected file name based on attempt number
                String targetFileName = className + "_" + Properties.ATTEMPT + "_GPTLLMTest.java";

                // Resolve the full path to the file
                Path targetFilePath = directoryPath.resolve(targetFileName);

                LoggingUtils.getEvoLogger().info("Looking for test file at: " + targetFilePath);

                String content = new String(Files.readAllBytes(targetFilePath));
                LoggingUtils.getEvoLogger().info("LLM Content is: " + content);
                llmClasses.add(content);


            } catch (IOException e) {
                LoggingUtils.getEvoLogger().error("Error reading the file: " + e.getMessage());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void evolve() {

        // Generate offspring, compute their fitness, update the archive and coverage goals.
        List<TestChromosome> offspringPopulation;

        if (stabled() && continueLLM()) {
            int beforeLLM = this.getNumberOfCoveredGoals();
            LoggingUtils.getEvoLogger().info("Before executing LLMPopulation function, the population has been stabled: " + this.currentIteration + " " + beforeLLM);
            offspringPopulation = this.generateLLMPopulation();
            int afterLLM = this.getNumberOfCoveredGoals();
            if (beforeLLM == afterLLM) {
                stabledBudget *= 2;
            }
        } else {
            offspringPopulation = this.breedNextGeneration();
        }

        // Create the union of parents and offSpring
        List<TestChromosome> union = new ArrayList<>();
        union.addAll(this.population);
        union.addAll(offspringPopulation);

        // for parallel runs: integrate possible immigrants
        if (Properties.NUM_PARALLEL_CLIENTS > 1 && !immigrants.isEmpty()) {
            union.addAll(immigrants.poll());
        }

        Set<TestFitnessFunction> uncoveredGoals = this.getUncoveredGoals();

        // Ranking the union
        logger.debug("Union Size =" + union.size());
        // Ranking the union using the best rank algorithm (modified version of the non dominated sorting algorithm)
        this.rankingFunction.computeRankingAssignment(union, uncoveredGoals);

        int remain = this.population.size();
        int index = 0;
        List<TestChromosome> front = null;
        this.population.clear();

        // Obtain the next front
        front = this.rankingFunction.getSubfront(index);

        while ((remain > 0) && (remain >= front.size()) && !front.isEmpty()) {
            // Assign crowding distance to individuals
            this.distance.fastEpsilonDominanceAssignment(front, uncoveredGoals);
            // Add the individuals of this front
            this.population.addAll(front);

            // Decrement remain
            remain = remain - front.size();

            // Obtain the next front
            index++;
            if (remain > 0) {
                front = this.rankingFunction.getSubfront(index);
            }
        }

        // Remain is less than front(index).size, insert only the best one
        if (remain > 0 && !front.isEmpty()) { // front contains individuals to insert
            this.distance.fastEpsilonDominanceAssignment(front, uncoveredGoals);
            front.sort(new OnlyCrowdingComparator<>());
            for (int k = 0; k < remain; k++) {
                this.population.add(front.get(k));
            }

            remain = 0;
        }

        // for parallel runs: collect best k individuals for migration
        if (Properties.NUM_PARALLEL_CLIENTS > 1 && Properties.MIGRANTS_ITERATION_FREQUENCY > 0) {
            if ((currentIteration + 1) % Properties.MIGRANTS_ITERATION_FREQUENCY == 0 && !this.population.isEmpty()) {
                HashSet<TestChromosome> emigrants = new HashSet<>(emigrantsSelection.select(this.population,
                        Properties.MIGRANTS_COMMUNICATION_RATE));
                ClientServices.<TestChromosome>getInstance().getClientNode().emigrate(emigrants);
            }
        }

        this.currentIteration++;
    }

    private boolean stabled() {
        if (this.previousNumCoveredGoals == this.getNumberOfCoveredGoals()) {
            this.countStable++;
        } else {
            this.countStable = 0;
            return false;
        }
        return this.countStable > this.stabledBudget;
    }

    private boolean continueLLM() {
        if (Properties.LLM_TEST_GENERATION_APPROACH == Properties.LLMGenerationApproach.API) {
            return llmUsedIndex < Properties.LLM_TEST_GENERATION_TIMES;
        } else if (Properties.LLM_TEST_GENERATION_APPROACH == Properties.LLMGenerationApproach.FILE) {
            return llmUsedIndex == 0;
        } else {
            return llmClasses.size() > llmUsedIndex;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void generateSolution() {
//        for(TestFitnessFunction tf: this.getUncoveredGoals()){
//            tf.getTargetMethod()
//        }

        logger.info("executing generateSolution function");

        // keep track of covered goals
        this.fitnessFunctions.forEach(this::addUncoveredGoal);

        // initialize population
        if (this.population.isEmpty()) {
            this.initializePopulation();
        }

        // Calculate dominance ranks and crowding distance
        this.rankingFunction.computeRankingAssignment(this.population, this.getUncoveredGoals());
        for (int i = 0; i < this.rankingFunction.getNumberOfSubfronts(); i++) {
            this.distance.fastEpsilonDominanceAssignment(this.rankingFunction.getSubfront(i), this.getUncoveredGoals());
        }

        final ClientNodeLocal<TestChromosome> clientNode =
                ClientServices.<TestChromosome>getInstance().getClientNode();

        Listener<Set<TestChromosome>> listener = null;
        if (Properties.NUM_PARALLEL_CLIENTS > 1) {
            listener = event -> immigrants.add(new LinkedList<>(event));
            clientNode.addListener(listener);
        }

        // TODO add here dynamic stopping condition
        this.updateNumberOfCoveredGoals();
        while (!this.isFinished() && this.getNumberOfUncoveredGoals() > 0) {
            this.evolve();
            this.notifyIteration();
            this.updateNumberOfCoveredGoals();
        }

        if (Properties.NUM_PARALLEL_CLIENTS > 1) {
            clientNode.deleteListener(listener);

            if (ClientProcess.DEFAULT_CLIENT_NAME.equals(ClientProcess.getIdentifier())) {
                //collect all end result test cases
                Set<Set<TestChromosome>> collectedSolutions = clientNode.getBestSolutions();

                logger.debug(ClientProcess.DEFAULT_CLIENT_NAME + ": Received " + collectedSolutions.size() + " solution sets");
                for (Set<TestChromosome> solution : collectedSolutions) {
                    for (TestChromosome t : solution) {
                        this.calculateFitness(t);
                    }
                }
            } else {
                //send end result test cases to Client-0
                Set<TestChromosome> solutionsSet = new HashSet<>(getSolutions());
                logger.debug(ClientProcess.getPrettyPrintIdentifier() + "Sending " + solutionsSet.size()
                        + " solutions to " + ClientProcess.DEFAULT_CLIENT_NAME);
                clientNode.sendBestSolution(solutionsSet);
            }
        }

        // storing the time needed to reach the maximum coverage
        clientNode.trackOutputVariable(RuntimeVariable.Time2MaxCoverage,
                this.budgetMonitor.getTime2MaxCoverage());
        this.notifySearchFinished();
    }

    protected List<TestChromosome> generateLLMPopulation() {
        LoggingUtils.getEvoLogger().info("executing LLMPopulation function, " + llmUsedIndex);

//        this.currentIteration++;

        LLMTestCaseGeneration llmGeneration = new LLMTestCaseGeneration();
        List<TestChromosome> llmTestCases = new ArrayList<>();

        double fitnessBeforeAddingDefaultTest = this.getBestIndividual().getFitness();
        LoggingUtils.getEvoLogger().info("Fitness before adding LLM test case:" + fitnessBeforeAddingDefaultTest);

        if (Properties.LLM_TEST_GENERATION_APPROACH == Properties.LLMGenerationApproach.API) {
            final Class<?> targetClass = Properties.getTargetClassAndDontInitialise();

            List<Method> targetMethods = getTargetMethods(targetClass);
            targetMethods.sort(new MethodComparator());
            LoggingUtils.getEvoLogger().info("Found " + targetMethods.size() + " as entry points for LLM Population function");

            SourceParser sourceParser = new SourceParser();

            for (int i = 0; i < Properties.LLM_TEST_GENERATION_BUDGET; i++) {
                for (Method entryMethod : targetMethods) {
                    TestCase testCase = llmGeneration.buildTestCaseByAPICodaMosa(sourceParser, entryMethod, llmUsedIndex);

                    if (testCase != null) {
                        TestChromosome generatedChromosome = new TestChromosome();
                        generatedChromosome.setTestCase(testCase);
                        this.calculateFitness(generatedChromosome);

                        fitnessFunctions.forEach(generatedChromosome::addFitness);
                        llmTestCases.add(generatedChromosome);
                    }
                }
            }
        } else {
            for (TestCase testCase : llmGeneration.buildTestCasesFromFile(llmClasses.get(llmUsedIndex))) {
                TestChromosome generatedChromosome = new TestChromosome();
                if (testCase != null) {
                    generatedChromosome.setTestCase(testCase);
                    this.calculateFitness(generatedChromosome);

                    fitnessFunctions.forEach(generatedChromosome::addFitness);
                    llmTestCases.add(generatedChromosome);
                }
            }
        }

        llmUsedIndex++;

        return llmTestCases;
    }

    public static String parseDescriptor(String descriptor) {
        int parenStart = descriptor.indexOf('(');
        int parenEnd = descriptor.indexOf(')');

        String methodName = descriptor.substring(0, parenStart);
        String argsDescriptor = descriptor.substring(parenStart + 1, parenEnd);

        int argCount = 0;
        for (int i = 0; i < argsDescriptor.length(); i++) {
            char c = argsDescriptor.charAt(i);
            if (c == 'L') {
                while (i < argsDescriptor.length() && argsDescriptor.charAt(i) != ';') {
                    i++; // Skip class type
                }
                argCount++;
            } else if (c == '[') {
                continue; // skip, arrays handled by next type char
            } else {
                argCount++; // primitive types like I, Z, etc.
            }
        }

        System.out.println("Method Name: " + methodName);
        System.out.println("Number of Arguments: " + argCount);
        return methodName + "(" + argCount + ")";
    }

    private List<Method> getTargetMethods(Class<?> targetClass) {
        Set<String> uncoveredMethods = new HashSet<>();
        for (TestFitnessFunction tf : this.getUncoveredGoals()) {
            uncoveredMethods.add(parseDescriptor(tf.getTargetMethod()));
        }



        Method[] declaredMethods = targetClass.getDeclaredMethods();
        List<Method> targetMethods = new LinkedList<>();
        for (Method m : declaredMethods) {
            if (Modifier.isPrivate(m.getModifiers())) {
                continue;
            }

            String methodName = m.getName() + "(" + m.getParameterTypes().length + ")";
            if (uncoveredMethods.contains(methodName)) {
                targetMethods.add(m);
            }
        }
        return targetMethods;
    }

    public void updateNumberOfCoveredGoals() {
        this.previousNumCoveredGoals = this.numCoveredGoals;
        this.numCoveredGoals = this.getNumberOfCoveredGoals();
    }
}
