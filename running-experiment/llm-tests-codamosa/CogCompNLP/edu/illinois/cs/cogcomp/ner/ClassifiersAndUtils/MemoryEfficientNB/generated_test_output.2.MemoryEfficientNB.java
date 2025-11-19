import org.junit.Test;
import static org.junit.Assert.*;

public class GeneratedTest {

@Test
public void test1()
{
    MemoryEfficientNB nb = new MemoryEfficientNB();
    int fid = 5;
    int classId = 0;
    double smooth = 0.1;
    int fidCountValue = 4;
    double weight = 10.0;
    MemoryEfficientNB.smooth = smooth;
    Vector<HashMap<Integer, Integer>> fidCounts = new Vector<>();
    HashMap<Integer, Integer> classMap = new HashMap<>();
    classMap.put(fid, fidCountValue);
    fidCounts.add(classMap);
    nb.fidCounts = fidCounts;
    nb.weights = new double[]{ weight };
    nb.map = new FakeMap();
    nb.map.dim = 100;
    double expected = ((1 - smooth) * fidCountValue) / weight;
    double result = nb.getFidProb(fid, classId);
    assertEquals(expected, result, 1.0E-6);
}

@Test
public void test2()
{
    nb.sampleSize = 10;
    nb.classCounts = new double[]{ 3.0, 5.0, 2.0 };
    double result = nb.getPrior(1);
    assertEquals(0.5, result, 1.0E-4);
}

@Test
public void test3()
{
    MemoryEfficientNB classifier = new MemoryEfficientNB();
    classifier.classesN = 2;
    classifier.map = new Object();
    Document mockDoc = mock(Document.class);
    when(mockDoc.getActiveFid(any())).thenReturn(new int[]{ 0, 1 });
    MemoryEfficientNB spyClassifier = spy(classifier);
    doReturn(0.6).when(spyClassifier).getPrior(0);
    doReturn(0.4).when(spyClassifier).getPrior(1);
    doReturn(0.8).when(spyClassifier).getFidProb(0, 0);
    doReturn(0.5).when(spyClassifier).getFidProb(1, 0);
    doReturn(0.3).when(spyClassifier).getFidProb(0, 1);
    doReturn(0.7).when(spyClassifier).getFidProb(1, 1);
    double[] conf = spyClassifier.getPredictionConfidence(mockDoc);
    assertEquals(2, conf.length);
    assertTrue(conf[0] > conf[1]);
    assertEquals(1.0, conf[0] + conf[1], 1.0E-6);
}

@Test
public void test4()
{
    MemoryEfficientNB nb = spy(new MemoryEfficientNB());
    Field field = MemoryEfficientNB.class.getDeclaredField("classesN");
    field.setAccessible(true);
    field.setInt(nb, 2);
    Field logField = MemoryEfficientNB.class.getDeclaredField("logger");
    logField.setAccessible(true);
    logField.set(null, Logger.getAnonymousLogger());
    CharacteristicWords words0 = new CharacteristicWords();
    words0.topWords = new Vector<>();
    words0.topWords.add("apple");
    words0.topWords.add("banana");
    CharacteristicWords words1 = new CharacteristicWords();
    words1.topWords = new Vector<>();
    words1.topWords.add("banana");
    words1.topWords.add("carrot");
    doReturn(words0).when(nb).getTopPmiWords(0, 5, 0.5, 2);
    doReturn(words1).when(nb).getTopPmiWords(1, 5, 0.5, 2);
    Hashtable<String, Integer> result = nb.getTopPmiWords(5, 0.5, 2);
    assertEquals(3, result.size());
    assertTrue(result.containsKey("apple"));
    assertTrue(result.containsKey("banana"));
    assertTrue(result.containsKey("carrot"));
    assertEquals(Integer.valueOf(1), result.get("apple"));
    assertEquals(Integer.valueOf(1), result.get("banana"));
    assertEquals(Integer.valueOf(1), result.get("carrot"));
}

@Test
public void test5()
{
    expected result;
}
{
    return new double[]{ 0.8, 0.0, 0.5 };
}
{
    classesN = 3;
    methodName = "label";
}

@Test
public void test6()
{
    MemoryEfficientNB nb = new MemoryEfficientNB();
    nb.classesN = 2;
    nb.getTopPmiWords = new MemoryEfficientNB.GetTopPmiWordsInterface() {
        @Override
        public CharacteristicWords apply(int classIndex, int maxWordsPerClass, double confThres, int minAppThres) {
            CharacteristicWords words = new CharacteristicWords();
            if (classIndex == 0) {
                words.topWords = new Vector<>();
                words.topWords.add("alpha");
                words.topWords.add("beta");
            } else {
                words.topWords = new Vector<>();
                words.topWords.add("beta");
                words.topWords.add("gamma");
            }
            return words;
        }
    };
    Hashtable<String, Integer> result = nb.getTopPmiWords(5, 0.5, 2);
    assertEquals(3, result.size());
    assertTrue(result.containsKey("alpha"));
    assertTrue(result.containsKey("beta"));
    assertTrue(result.containsKey("gamma"));
    assertEquals(((Integer) (1)), result.get("alpha"));
    assertEquals(((Integer) (1)), result.get("beta"));
    assertEquals(((Integer) (1)), result.get("gamma"));
}

@Test
public void test7()
{
    MemoryEfficientNB nb = new MemoryEfficientNB();
    nb.sampleSize = 2;
    nb.weights = new double[]{ 1.0, 2.0 };
    nb.classCounts = new double[]{ 3.0, 4.0 };
    nb.map = new TestFeatureMap();
    nb.map.dim = 2;
    nb.wordCounts = new double[]{ 5.0, 6.0 };
    nb.fidCount = 1;
    Hashtable<Integer, Double> h = new Hashtable<>();
    h.put(42, 7.5);
    Vector<Hashtable<Integer, Double>> vector = new Vector<>();
    vector.add(h);
    nb.fidCounts = vector;
    File tempFile = File.createTempFile("test_nb_output", ".txt");
    tempFile.deleteOnExit();
    String filePath = tempFile.getAbsolutePath();
    nb.save(filePath);
    BufferedReader reader = new BufferedReader(new FileReader(filePath));
    assertEquals("2", reader.readLine());
    assertEquals("2", reader.readLine());
    assertEquals("1.0", reader.readLine());
    assertEquals("2.0", reader.readLine());
    assertEquals("3.0", reader.readLine());
    assertEquals("4.0", reader.readLine());
    assertEquals("5.0", reader.readLine());
    assertEquals("6.0", reader.readLine());
    assertEquals("1", reader.readLine());
    assertEquals("1", reader.readLine());
    assertEquals("1", reader.readLine());
    assertEquals("42", reader.readLine());
    assertEquals("7.5", reader.readLine());
    reader.close();
    File featureMapFile = new File(filePath + ".nb.featuremap");
    assertTrue(featureMapFile.exists());
    featureMapFile.deleteOnExit();
}
