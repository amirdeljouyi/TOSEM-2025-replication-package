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

import org.evosuite.Properties;
import org.evosuite.ga.ChromosomeFactory;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.ga.comparators.OnlyCrowdingComparator;
import org.evosuite.ga.metaheuristics.mosa.structural.MultiCriteriaManager;
import org.evosuite.ga.operators.ranking.CrowdingDistance;
import org.evosuite.llm.LLMTestCaseGeneration;
import org.evosuite.llm.SourceParser;
import org.evosuite.runtime.classhandling.ClassResetter;
import org.evosuite.symbolic.MethodComparator;
import org.evosuite.symbolic.PathCondition;
import org.evosuite.symbolic.dse.ConcolicExecutorImpl;
import org.evosuite.symbolic.dse.DSEStatistics;
import org.evosuite.symbolic.expr.Constraint;
import org.evosuite.symbolic.solver.SolverResult;
import org.evosuite.symbolic.solver.SolverUtils;
import org.evosuite.testcase.*;
import org.evosuite.utils.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

/**
 * Implementation of the DynaMOSA (Many Objective Sorting Algorithm) described in the paper
 * "Automated Test Case Generation as a Many-Objective Optimisation Problem with Dynamic Selection
 * of the Targets".
 *
 * @author Annibale Panichella, Fitsum M. Kifetew, Paolo Tonella
 */
public class LLMDynaMOSA extends AbstractMOSA {

    private static final long serialVersionUID = 146182080947267628L;

    private static final Logger logger = LoggerFactory.getLogger(LLMDynaMOSA.class);

    /**
     * Manager to determine the test goals to consider at each generation
     */
    protected MultiCriteriaManager goalsManager = null;

    protected CrowdingDistance<TestChromosome> distance = new CrowdingDistance<>();

    private int countStable = 0;

    private int previousNumCoveredGoals = 0;
    private int numCoveredGoals = 0;
    private List<String> llmClasses = new ArrayList<>();
    private int llmUsedIndex = 0;

    /**
     * Constructor based on the abstract class {@link AbstractMOSA}.
     *
     * @param factory
     */
    public LLMDynaMOSA(ChromosomeFactory<TestChromosome> factory) {

        super(factory);

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
        } else {
            offspringPopulation = this.breedNextGeneration();
        }

        // Create the union of parents and offspring
        List<TestChromosome> union = new ArrayList<>(this.population.size() + offspringPopulation.size());
        union.addAll(this.population);
        union.addAll(offspringPopulation);

        // Ranking the union
        logger.debug("Union Size = {}", union.size());

        // Ranking the union using the best rank algorithm (modified version of the non dominated
        // sorting algorithm)
        this.rankingFunction.computeRankingAssignment(union, this.goalsManager.getCurrentGoals());

        // let's form the next population using "preference sorting and non-dominated sorting" on the
        // updated set of goals
        int remain = Math.max(Properties.POPULATION, this.rankingFunction.getSubfront(0).size());
        int index = 0;
        this.population.clear();

        // Obtain the first front
        List<TestChromosome> front = this.rankingFunction.getSubfront(index);

        // Successively iterate through the fronts (starting with the first non-dominated front)
        // and insert their members into the population for the next generation. This is done until
        // all fronts have been processed or we hit a front that is too big to fit into the next
        // population as a whole.
        while ((remain > 0) && (remain >= front.size()) && !front.isEmpty()) {
            // Assign crowding distance to individuals
            this.distance.fastEpsilonDominanceAssignment(front, this.goalsManager.getCurrentGoals());

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

        // In case the population for the next generation has not been filled up completely yet,
        // we insert the best individuals from the current front (the one that was too big to fit
        // entirely) until there are no more free places left. To this end, and in an effort to
        // promote diversity, we consider those individuals with a higher crowding distance as
        // being better.
        if (remain > 0 && !front.isEmpty()) { // front contains individuals to insert
            this.distance.fastEpsilonDominanceAssignment(front, this.goalsManager.getCurrentGoals());
            front.sort(new OnlyCrowdingComparator<>());
            for (int k = 0; k < remain; k++) {
                this.population.add(front.get(k));
            }
        }

        this.currentIteration++;
        //logger.debug("N. fronts = {}", ranking.getNumberOfSubfronts());
        //logger.debug("1* front size = {}", ranking.getSubfront(0).size());
        logger.debug("Covered goals = {}", goalsManager.getCoveredGoals().size());
        logger.debug("Current goals = {}", goalsManager.getCurrentGoals().size());
        logger.debug("Uncovered goals = {}", goalsManager.getUncoveredGoals().size());
    }

    private boolean continueLLM() {
        if (Properties.LLM_TEST_GENERATION_APPROACH == Properties.LLMGenerationApproach.API) {
            return llmUsedIndex < Properties.LLM_TEST_GENERATION_TIMES;
        } else if(Properties.LLM_TEST_GENERATION_APPROACH == Properties.LLMGenerationApproach.FILE) {
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
        logger.debug("executing generateSolution function");

        // Set up the targets to cover, which are initially free of any control dependencies.
        // We are trying to optimize for multiple targets at the same time.
        this.goalsManager = new MultiCriteriaManager(this.fitnessFunctions);

        LoggingUtils.getEvoLogger().info("* Initial Number of Goals in DynaMOSA = " + this.goalsManager.getCurrentGoals().size() + " / " + this.getUncoveredGoals().size());

        logger.debug("Initial Number of Goals = " + this.goalsManager.getCurrentGoals().size());

//        this.initializePopulationByLLM();

        if (this.population.isEmpty()) {
            // Initialize the population by creating solutions at random.
            this.initializePopulation();
        }

        // Compute the fitness for each population member, update the coverage information and the
        // set of goals to cover. Finally, update the archive.
        // this.calculateFitness(); // Not required, already done by this.initializePopulation();

        // Calculate dominance ranks and crowding distance. This is required to decide which
        // individuals should be used for mutation and crossover in the first iteration of the main
        // search loop.
        this.rankingFunction.computeRankingAssignment(this.population, this.goalsManager.getCurrentGoals());
        for (int i = 0; i < this.rankingFunction.getNumberOfSubfronts(); i++) {
            this.distance.fastEpsilonDominanceAssignment(this.rankingFunction.getSubfront(i), this.goalsManager.getCurrentGoals());
        }

        // Evolve the population generation by generation until all gaols have been covered or the
        // search budget has been consumed.

        this.updateNumberOfCoveredGoals();
        while (!isFinished() && this.goalsManager.getUncoveredGoals().size() > 0) {
            this.evolve();
            this.notifyIteration();
            this.updateNumberOfCoveredGoals();
        }

//        int beforeLLM = this.getNumberOfCoveredGoals();
//        LoggingUtils.getEvoLogger().info("Before executing LLMPopulation function, the population has been stabled: " + this.currentIteration + " " + beforeLLM);
//        this.generateLLMPopulation();
//        LoggingUtils.getEvoLogger().info("After executing LLMPopulation function: " + this.getNumberOfCoveredGoals() + " Increased covered goals: " + (this.getNumberOfCoveredGoals() - beforeLLM));
//
//        while (!isFinished() && this.goalsManager.getUncoveredGoals().size() > 0) {
//            this.evolve();
//            this.notifyIteration();
//        }

        this.notifySearchFinished();
    }

    protected List<TestChromosome> generateLLMPopulation() {
        LoggingUtils.getEvoLogger().info("executing LLMPopulation function, " + llmUsedIndex );

//        this.currentIteration++;

        LLMTestCaseGeneration llmGeneration = new LLMTestCaseGeneration();
        List<TestChromosome> llmTestCases = new ArrayList<>();

        double fitnessBeforeAddingDefaultTest = this.getBestIndividual().getFitness();
        LoggingUtils.getEvoLogger().info("Fitness before adding LLM test case:" + fitnessBeforeAddingDefaultTest);

        if (Properties.LLM_TEST_GENERATION_APPROACH == Properties.LLMGenerationApproach.API) {
            final Class<?> targetClass = Properties.getTargetClassAndDontInitialise();

            List<Method> targetMethods = getTargetMethods(targetClass);
            targetMethods.sort(new MethodComparator());
            logger.debug("Found " + targetMethods.size() + " as entry points for LLM Population function");

            SourceParser sourceParser = new SourceParser();

            for (int i = 0; i < Properties.LLM_TEST_GENERATION_BUDGET; i++) {
                for (Method entryMethod : targetMethods) {
                    TestCase testCase = llmGeneration.buildTestCaseByAPI(sourceParser, entryMethod);

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

    public void initializePopulationByLLM() {
        this.notifySearchStarted();
        this.currentIteration = 0;

        // Create a random parent population P0
        this.generateLLMPopulation();
        this.rankingFunction.computeRankingAssignment(this.population, this.goalsManager.getCurrentGoals());
        for (int i = 0; i < this.rankingFunction.getNumberOfSubfronts(); i++) {
            this.distance.fastEpsilonDominanceAssignment(this.rankingFunction.getSubfront(i), this.goalsManager.getCurrentGoals());
        }
        this.updateNumberOfCoveredGoals();
        LoggingUtils.getEvoLogger().info("After executing LLMPopulation function: " + this.getNumberOfCoveredGoals());

        // Determine fitness
        this.calculateFitness();
        this.notifyIteration();
    }


    private boolean stabled() {
        if (this.previousNumCoveredGoals == this.getNumberOfCoveredGoals()) {

            // TODO: Check Branch Distance
//            for(TestFitnessFunction: this.goalsManager.getCurrentGoals()){
//
//            }
            this.countStable++;
        } else {
            this.countStable = 0;
            return false;
        }
        int stabledBudget = Properties.LLM_STABLED_BUDGET;
        return this.countStable > stabledBudget;
    }


    /**
     * Calculates the fitness for the given individual. Also updates the list of targets to cover,
     * as well as the population of best solutions in the archive.
     *
     * @param c the chromosome whose fitness to compute
     */
    @Override
    protected void calculateFitness(TestChromosome c) {
        if (!isFinished()) {
            // this also updates the archive and the targets
            this.goalsManager.calculateFitness(c, this);
            this.notifyEvaluation(c);
        }
    }

    @Override
    public List<? extends FitnessFunction<TestChromosome>> getFitnessFunctions() {
        List<TestFitnessFunction> testFitnessFunctions = new ArrayList<>(goalsManager.getCoveredGoals());
        testFitnessFunctions.addAll(goalsManager.getUncoveredGoals());
        return testFitnessFunctions;
    }

    private static List<Method> getTargetMethods(Class<?> targetClass) {
        Method[] declaredMethods = targetClass.getDeclaredMethods();
        List<Method> targetMethods = new LinkedList<>();
        for (Method m : declaredMethods) {

            if (Modifier.isPrivate(m.getModifiers())) {
                continue;
            }

//                if (m.getName().equals(ClassResetter.STATIC_RESET)) {
//                    continue;
//                }

            targetMethods.add(m);
        }
        return targetMethods;
    }

    public void updateNumberOfCoveredGoals() {
        this.previousNumCoveredGoals = this.numCoveredGoals;
        this.numCoveredGoals = this.getNumberOfCoveredGoals();
    }

}
