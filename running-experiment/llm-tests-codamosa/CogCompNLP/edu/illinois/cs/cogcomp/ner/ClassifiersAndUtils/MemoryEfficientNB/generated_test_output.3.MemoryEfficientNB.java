import org.junit.Test;
import static org.junit.Assert.*;

public class GeneratedTest {

@Test
public void test1()
{
    MemoryEfficientNB nb = new MemoryEfficientNB();
    MemoryEfficientNB.smooth = 0.1;
    nb.weights = new double[]{ 10.0 };
    Vector<Map<Integer, Integer>> fidCounts = new Vector<>();
    Map<Integer, Integer> class0Map = new HashMap<>();
    class0Map.put(5, 2);
    fidCounts.add(class0Map);
    nb.fidCounts = fidCounts;
    nb.map = new Object() {
        public int dim = 100;
    };
    double result = nb.getFidProb(5, 0);
    assertEquals(0.18, result, 1.0E-6);
}

@Test
public void test2()
{
    MemoryEfficientNB nb = new MemoryEfficientNB();
    nb.sampleSize = 10.0;
    nb.classCounts = new double[3];
    nb.classCounts[1] = 4.0;
    double result = nb.getPrior(1);
    assertEquals(0.4, result, 1.0E-6);
}

@Test
public void test3()
{
    MemoryEfficientNB nb = new MemoryEfficientNB();
    nb.classesN = 2;
    nb.map = null;
    Document mockDoc = mock(Document.class);
    when(mockDoc.getActiveFid(null)).thenReturn(new int[]{ 10 });
    MemoryEfficientNB spyNb = spy(nb);
    doReturn(0.6).when(spyNb).getPrior(0);
    doReturn(0.4).when(spyNb).getPrior(1);
    doReturn(0.5).when(spyNb).getFidProb(10, 0);
    doReturn(0.2).when(spyNb).getFidProb(10, 1);
    double[] result = spyNb.getPredictionConfidence(mockDoc);
}

@Test
public void test4()
{
    MemoryEfficientNB nb = new MemoryEfficientNB();
    nb.classesN = 1;
    StringStatisticsUtils.CharacteristicWords mockWords = new StringStatisticsUtils.CharacteristicWords();
    mockWords.topWords = new Vector<>();
    mockWords.topWords.add("entity");
    MemoryEfficientNB.logger = Logger.getLogger("testLogger");
    nb.getTopPmiWords = new MemoryEfficientNB() {
        @Override
        public CharacteristicWords getTopPmiWords(int classId, int maxWordsPerClass, double confThres, int minAppThres) {
            return mockWords;
        }
    }.getTopPmiWords;
    Hashtable<String, Integer> result = nb.getTopPmiWords(5, 0.0, 1);
    assertEquals(1, result.size());
    assertTrue(result.containsKey("entity"));
    assertEquals(Integer.valueOf(1), result.get("entity"));
}

@Test
public void test5()
{
    expected result;
}
{
    return new double[]{ 0.8, 0.0, 0.55 };
}
{
    classesN = 3;
    methodName = "confScore";
}

@Test
public void test6()
{
}
{
    CharacteristicWords words = new CharacteristicWords();
    words.topWords = new Vector<>();
    if (classId == 0) {
        words.topWords.add("apple");
        words.topWords.add("banana");
    } else if (classId == 1) {
        words.topWords.add("banana");
        words.topWords.add("carrot");
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
    nb.weights = new double[]{ 0.5, 1.5 };
    nb.classCounts = new double[]{ 2.0, 3.0 };
    nb.wordCounts = new double[]{ 1.0, 2.0, 3.0 };
    nb.fidCount = 2;
    nb.map = new FeatureMap();
    nb.map.dim = 3;
    File tempDir = new File(System.getProperty("java.io.tmpdir"));
    File baseFile = File.createTempFile("nb_save_test", null, tempDir);
    String baseFilePath = baseFile.getAbsolutePath();
    baseFile.delete();
    nb.fidCounts = new Vector<>();
    Hashtable<Integer, Double> table1 = new Hashtable<>();
    table1.put(10, 1.0);
    nb.fidCounts.add(table1);
    Hashtable<Integer, Double> table2 = new Hashtable<>();
    table2.put(20, 2.0);
    table2.put(30, 3.0);
    nb.fidCounts.add(table2);
    nb.save(baseFilePath);
    File savedFile = new File(baseFilePath);
    BufferedReader reader = new BufferedReader(new FileReader(savedFile));
    assertEquals("2", reader.readLine());
    assertEquals("2", reader.readLine());
    assertEquals("0.5", reader.readLine());
    assertEquals("1.5", reader.readLine());
    assertEquals("2.0", reader.readLine());
    assertEquals("3.0", reader.readLine());
    assertEquals("1.0", reader.readLine());
    assertEquals("2.0", reader.readLine());
    assertEquals("3.0", reader.readLine());
    assertEquals("2", reader.readLine());
    assertEquals("2", reader.readLine());
    assertEquals("1", reader.readLine());
    assertEquals("10", reader.readLine());
    assertEquals("1.0", reader.readLine());
    assertEquals("2", reader.readLine());
    assertEquals("20", reader.readLine());
    assertEquals("2.0", reader.readLine());
    assertEquals("30", reader.readLine());
    assertEquals("3.0", reader.readLine());
    reader.close();
    savedFile.delete();
    new File(baseFilePath + ".nb.featuremap").delete();
}
