import org.junit.Test;
import static org.junit.Assert.*;

public class GeneratedTest {

@Test
public void test1()
{
    MemoryEfficientNB nb = new MemoryEfficientNB();
    MemoryEfficientNB.smooth = 0.1;
    Vector<Map<Integer, Integer>> fidCounts = new Vector<>();
    Map<Integer, Integer> class0Map = new HashMap<>();
    class0Map.put(5, 10);
    fidCounts.add(class0Map);
    nb.fidCounts = fidCounts;
    nb.weights = new double[]{ 20.0 };
    nb.map = new Object() {
        public int dim = 100;
    };
    double expected = ((1 - 0.1) * 10) / 20.0;
    double actual = nb.getFidProb(5, 0);
    assertEquals(expected, actual, 1.0E-6);
}

@Test
public void test2()
{
    MemoryEfficientNB nb = new MemoryEfficientNB();
    nb.sampleSize = 10.0;
    nb.classCounts = new double[]{ 2.0, 3.0, 5.0 };
    double result = nb.getPrior(1);
    assertEquals(0.3, result, 1.0E-4);
}

@Test
public void test3()
{
    MemoryEfficientNB classifier = new MemoryEfficientNB();
    classifier.classesN = 2;
    classifier.map = null;
    classifier.getPrior = new IntFunction<Double>() {
        public Double apply(int classIndex) {
            return classIndex == 0 ? 0.6 : 0.4;
        }
    };
    classifier.getFidProb = new BiFunction<Integer, Integer, Double>() {
        public Double apply(Integer fid, Integer classIndex) {
            if ((fid == 10) && (classIndex == 0)) {
                return 0.8;
            }
            if ((fid == 10) && (classIndex == 1)) {
                return 0.2;
            }
            return 1.0;
        }
    };
    Document doc = new Document() {
        public int[] getActiveFid(Object ignored) {
            return new int[]{ 10 };
        }
    };
    double[] confidence = classifier.getPredictionConfidence(doc);
    assertEquals(2, confidence.length);
    assertTrue(confidence[0] > confidence[1]);
    assertEquals(1.0, confidence[0] + confidence[1], 1.0E-6);
}

@Test
public void test4()
{
    nb.classesN = 2;
    MemoryEfficientNB.logger = Logger.getLogger("test");
    MemoryEfficientNB spyNb = spy(nb);
    CharacteristicWords words0 = new CharacteristicWords();
    words0.topWords = new Vector<>();
    words0.topWords.add("apple");
    words0.topWords.add("banana");
    CharacteristicWords words1 = new CharacteristicWords();
    words1.topWords = new Vector<>();
    words1.topWords.add("banana");
    words1.topWords.add("cherry");
    doReturn(words0).when(spyNb).getTopPmiWords(0, 3, 0.5, 2);
    doReturn(words1).when(spyNb).getTopPmiWords(1, 3, 0.5, 2);
    Hashtable<String, Integer> result = spyNb.getTopPmiWords(3, 0.5, 2);
    assertEquals(3, result.size());
    assertTrue(result.containsKey("apple"));
    assertTrue(result.containsKey("banana"));
    assertTrue(result.containsKey("cherry"));
    assertEquals(Integer.valueOf(1), result.get("apple"));
    assertEquals(Integer.valueOf(1), result.get("banana"));
    assertEquals(Integer.valueOf(1), result.get("cherry"));
}

@Test
public void test5()
{
}
{
    return new double[]{ 0.75, 0.0, 0.25 };
}
{
    classesN = 3;
    methodName = "class";
}

@Test
public void test6()
{
}
{
    CharacteristicWords words = new CharacteristicWords();
    words.topWords = new Vector<>();
    if (classIndex == 0) {
        words.topWords.add("alpha");
        words.topWords.add("beta");
    } else if (classIndex == 1) {
        words.topWords.add("beta");
        words.topWords.add("gamma");
    }
    return words;
}
{
    this.classesN = 2;
}

@Test
public void test7()
{
    MemoryEfficientNB nb = new MemoryEfficientNB();
    nb.sampleSize = 2;
    nb.weights = new double[]{ 1.1, 2.2 };
    nb.classCounts = new double[]{ 3.3, 4.4 };
    nb.wordCounts = new double[]{ 5.5, 6.6 };
    nb.fidCount = 1;
    nb.fidCounts = new Vector<>();
    Hashtable<Integer, Double> table = new Hashtable<>();
    table.put(42, 7.7);
    nb.fidCounts.add(table);
    nb.map = new MemoryEfficientNB.FeatureMap() {
        public int dim = 2;

        public void save(String filePath) {
            try (final OutFile out = new OutFile(filePath)) {
                out.println("feature_map_saved");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };
    String tempFilePath = "test_output";
    nb.save(tempFilePath);
    File savedFileMain = new File(tempFilePath);
    try (final BufferedReader reader = new BufferedReader(new FileReader(savedFileMain))) {
        assertEquals("2", reader.readLine());
        assertEquals("2", reader.readLine());
        assertEquals("1.1", reader.readLine());
        assertEquals("2.2", reader.readLine());
        assertEquals("3.3", reader.readLine());
        assertEquals("4.4", reader.readLine());
        assertEquals("5.5", reader.readLine());
        assertEquals("6.6", reader.readLine());
        assertEquals("1", reader.readLine());
        assertEquals("1", reader.readLine());
        assertEquals("1", reader.readLine());
        assertEquals("42", reader.readLine());
        assertEquals("7.7", reader.readLine());
    }
    File featureMapFile = new File(tempFilePath + ".nb.featuremap");
    try (final BufferedReader reader = new BufferedReader(new FileReader(featureMapFile))) {
        assertEquals("feature_map_saved", reader.readLine());
    }
    savedFileMain.delete();
    featureMapFile.delete();
}
