import org.junit.Test;
import static org.junit.Assert.*;

public class GeneratedTest {

@Test
public void test1()
{
    MemoryEfficientNB nb = new MemoryEfficientNB();
    Vector<HashMap<Integer, Double>> fidCounts = new Vector<HashMap<Integer, Double>>();
    HashMap<Integer, Double> class0Map = new HashMap<Integer, Double>();
    class0Map.put(42, 10.0);
    fidCounts.add(class0Map);
    nb.fidCounts = fidCounts;
    nb.weights = new double[]{ 20.0 };
    nb.map = new MemoryEfficientNB.FeatureMap();
    nb.map.dim = 100;
    MemoryEfficientNB.smooth = 0.1;
    double result = nb.getFidProb(42, 0);
    assertEquals(0.45, result, 1.0E-6);
}

@Test
public void test2()
{
    MemoryEfficientNB nb = new MemoryEfficientNB();
    nb.classCounts = new double[]{ 4.0, 6.0, 10.0 };
    nb.sampleSize = 20.0;
    double result = nb.getPrior(1);
    assertEquals(0.3, result, 1.0E-6);
}

@Test
public void test3()
{
    MemoryEfficientNB nb = new MemoryEfficientNB();
    nb.classesN = 2;
    nb.map = new Object();
    MemoryEfficientNB spy = new MemoryEfficientNB() {
        {
            classesN = 2;
            map = new Object();
        }

        @Override
        public double getPrior(int classIndex) {
            if (classIndex == 0) {
                return 0.6;
            } else {
                return 0.4;
            }
        }

        @Override
        public double getFidProb(int fid, int classIndex) {
            if ((fid == 100) && (classIndex == 0)) {
                return 0.8;
            }
            if ((fid == 100) && (classIndex == 1)) {
                return 0.3;
            }
            return 0.1;
        }
    };
    Document doc = new Document() {
        @Override
        public int[] getActiveFid(Object ignoredMap) {
            return new int[]{ 100 };
        }
    };
    double[] result = spy.getPredictionConfidence(doc);
    assertEquals(2, result.length);
    double logProb0 = Math.log(0.6) + Math.log(0.8);
    double logProb1 = Math.log(0.4) + Math.log(0.3);
    double maxLogProb = Math.max(logProb0, logProb1);
    double exp0 = Math.exp(logProb0 - maxLogProb);
    double exp1 = Math.exp(logProb1 - maxLogProb);
    double denom = exp0 + exp1;
    double expected0 = exp0 / denom;
    double expected1 = exp1 / denom;
    assertEquals(expected0, result[0], 1.0E-6);
    assertEquals(expected1, result[1], 1.0E-6);
}

@Test
public void test4()
{
}
{
    CharacteristicWords words = new CharacteristicWords();
    if (classId == 0) {
        words.topWords = new Vector<>();
        words.topWords.add("entity1");
        words.topWords.add("commonWord");
    } else if (classId == 1) {
        words.topWords = new Vector<>();
        words.topWords.add("entity2");
        words.topWords.add("commonWord");
    }
    return words;
}
{
    this.classesN = 2;
}

@Test
public void test5()
{
}
{
    return new double[]{ 0.2, 0.5, 0.3 };
}
{
    this.classesN = 3;
}

@Test
public void test6()
{
    expected result;
}
{
    return new double[]{ 0.75, 0.0, 0.25 };
}
{
    classesN = 3;
    methodName = "conf_";
}

@Test
public void test7()
{
    MemoryEfficientNB nb = new MemoryEfficientNB();
    nb.classesN = 2;
    CharacteristicWords wordsClass0 = new CharacteristicWords();
    wordsClass0.topWords = new Vector<>();
    wordsClass0.topWords.add("apple");
    wordsClass0.topWords.add("banana");
    CharacteristicWords wordsClass1 = new CharacteristicWords();
    wordsClass1.topWords = new Vector<>();
    wordsClass1.topWords.add("banana");
    wordsClass1.topWords.add("carrot");
    MemoryEfficientNB.logger = Logger.getAnonymousLogger();
    nb.getTopPmiWords = new MemoryEfficientNB.TopPmiWordsFunction() {
        @Override
        public CharacteristicWords apply(int classIndex, int maxWordsPerClass, double confThres, int minAppThres) {
            return classIndex == 0 ? wordsClass0 : wordsClass1;
        }
    };
    Hashtable<String, Integer> result = nb.getTopPmiWords(5, 0.1, 1);
    assertEquals(3, result.size());
    assertTrue(result.containsKey("apple"));
    assertTrue(result.containsKey("banana"));
    assertTrue(result.containsKey("carrot"));
    assertEquals(Integer.valueOf(1), result.get("apple"));
    assertEquals(Integer.valueOf(1), result.get("banana"));
    assertEquals(Integer.valueOf(1), result.get("carrot"));
}

@Test
public void test8()
{
    MemoryEfficientNB nb = new MemoryEfficientNB();
    nb.sampleSize = 2;
    nb.weights = new double[]{ 1.1, 2.2 };
    nb.classCounts = new double[]{ 3.3, 4.4 };
    nb.wordCounts = new double[]{ 5.5, 6.6 };
    nb.map = new FeatureMap();
    nb.map.dim = 2;
    nb.fidCount = 7;
    Hashtable<Integer, Double> h1 = new Hashtable<>();
    h1.put(10, 1.0);
    Hashtable<Integer, Double> h2 = new Hashtable<>();
    h2.put(20, 2.0);
    nb.fidCounts = new Vector<>();
    nb.fidCounts.add(h1);
    nb.fidCounts.add(h2);
    String baseFilePath = "test_output";
    String expectedFeatureMapPath = baseFilePath + ".nb.featuremap";
    nb.save(baseFilePath);
    File outputFile = new File(baseFilePath);
    assertTrue(outputFile.exists());
    BufferedReader reader = new BufferedReader(new FileReader(outputFile));
    assertEquals("2", reader.readLine());
    assertEquals("2", reader.readLine());
    assertEquals("1.1", reader.readLine());
    assertEquals("2.2", reader.readLine());
    assertEquals("3.3", reader.readLine());
    assertEquals("4.4", reader.readLine());
    assertEquals("5.5", reader.readLine());
    assertEquals("6.6", reader.readLine());
    assertEquals("7", reader.readLine());
    assertEquals("2", reader.readLine());
    assertEquals("1", reader.readLine());
    assertEquals("10", reader.readLine());
    assertEquals("1.0", reader.readLine());
    assertEquals("1", reader.readLine());
    assertEquals("20", reader.readLine());
    assertEquals("2.0", reader.readLine());
    reader.close();
    assertTrue(new File(expectedFeatureMapPath).exists());
    outputFile.delete();
    new File(expectedFeatureMapPath).delete();
}
