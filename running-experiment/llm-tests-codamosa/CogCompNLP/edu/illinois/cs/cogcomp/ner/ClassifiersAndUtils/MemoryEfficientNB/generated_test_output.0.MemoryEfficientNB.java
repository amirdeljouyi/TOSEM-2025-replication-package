import org.junit.Test;
import static org.junit.Assert.*;

public class GeneratedTest {

@Test
public void test1()
{
    Document doc1 = new Document();
    doc1.classID = 1;
    Document doc2 = new Document();
    doc2.classID = 2;
    DocumentCollection testCollection = new DocumentCollection();
    testCollection.docs = new Vector<>();
    testCollection.docs.add(doc1);
    testCollection.docs.add(doc2);
    MemoryEfficientNB classifier = new MemoryEfficientNB() {
        @Override
        public int classify(Document doc, int unused) {
            return doc.classID;
        }
    };
    double acc = classifier.getAcc(testCollection);
    assertEquals(1.0, acc, 1.0E-5);
}

@Test
public void test2()
{
    MemoryEfficientNB nb = new MemoryEfficientNB();
    Vector<HashMap<Integer, Integer>> fidCounts = new Vector<HashMap<Integer, Integer>>();
    HashMap<Integer, Integer> class0Map = new HashMap<Integer, Integer>();
    class0Map.put(42, 10);
    fidCounts.add(class0Map);
    nb.fidCounts = fidCounts;
    nb.weights = new double[]{ 20.0 };
    MemoryEfficientNB.smooth = 0.1;
    nb.map = new MemoryEfficientNB.FeatureMap();
    nb.map.dim = 100;
    double result = nb.getFidProb(42, 0);
    assertEquals(0.45, result, 1.0E-6);
}

@Test
public void test3()
{
    nb.sampleSize = 10.0;
    nb.classCounts = new double[]{ 2.0, 3.0, 5.0 };
    double result = nb.getPrior(1);
    assertEquals(0.3, result, 1.0E-6);
}

@Test
public void test4()
{
    MemoryEfficientNB classifier = new MemoryEfficientNB();
    classifier.classesN = 2;
    classifier.map = new HashMap<>();
    MemoryEfficientNB testClassifier = new MemoryEfficientNB() {
        {
            classesN = 2;
            map = new HashMap<>();
        }

        @Override
        protected double getPrior(int classIndex) {
            return classIndex == 0 ? 0.6 : 0.4;
        }

        @Override
        protected double getFidProb(int fid, int classIndex) {
            if (classIndex == 0) {
                return fid == 1 ? 0.8 : 0.5;
            } else {
                return fid == 1 ? 0.3 : 0.6;
            }
        }
    };
    Document mockDoc = new Document() {
        @Override
        public int[] getActiveFid(Map<?, ?> map) {
            return new int[]{ 1, 2 };
        }
    };
    double[] confidences = testClassifier.getPredictionConfidence(mockDoc);
    assertNotNull(confidences);
    assertEquals(2, confidences.length);
    double sum = confidences[0] + confidences[1];
    assertEquals(1.0, sum, 1.0E-6);
    assertTrue(confidences[0] > confidences[1]);
}

@Test
public void test5()
{
    MemoryEfficientNB nb = new MemoryEfficientNB();
    nb.classesN = 2;
    Vector<String> topWordsClass0 = new Vector<>();
    topWordsClass0.add("entity1");
    topWordsClass0.add("entity2");
    Vector<String> topWordsClass1 = new Vector<>();
    topWordsClass1.add("entity2");
    topWordsClass1.add("entity3");
    CharacteristicWords mockWordsClass0 = new CharacteristicWords("", null, null);
    mockWordsClass0.topWords = topWordsClass0;
    CharacteristicWords mockWordsClass1 = new CharacteristicWords("", null, null);
    mockWordsClass1.topWords = topWordsClass1;
    MemoryEfficientNB.logger = Logger.getLogger("test");
    nb.getTopPmiWords = new MemoryEfficientNB.TopPmiWordFetcher() {
        private int callCount = 0;

        public CharacteristicWords getTopPmiWords(int classIndex, int maxWords, double conf, int minApp) {
            callCount++;
            if (callCount == 1) {
                return mockWordsClass0;
            }
            return mockWordsClass1;
        }
    };
    Hashtable<String, Integer> result = nb.getTopPmiWords(5, 0.5, 1);
    assertEquals(3, result.size());
    assertTrue(result.containsKey("entity1"));
    assertTrue(result.containsKey("entity2"));
    assertTrue(result.containsKey("entity3"));
    assertEquals(Integer.valueOf(1), result.get("entity1"));
    assertEquals(Integer.valueOf(1), result.get("entity2"));
    assertEquals(Integer.valueOf(1), result.get("entity3"));
}

@Test
public void test6()
{
    MemoryEfficientNB classifier = new MemoryEfficientNB();
    try {
        Field classesNField = MemoryEfficientNB.class.getDeclaredField("classesN");
        classesNField.setAccessible(true);
        classesNField.setInt(classifier, 3);
        Field methodNameField = MemoryEfficientNB.class.getDeclaredField("methodName");
        methodNameField.setAccessible(true);
        methodNameField.set(classifier, "Pred");
        Field getPredictionConfidenceMethod = MemoryEfficientNB.class.getDeclaredMethod("getPredictionConfidence", Document.class).getDeclaringClass().getDeclaredField("getPredictionConfidence");
        getPredictionConfidenceMethod.setAccessible(true);
    } catch (Exception e) {
        fail("Reflection setup failed: " + e.getMessage());
    }
    Document dummyDoc = new Document();
    MemoryEfficientNB classifierWithMockedConfidence = new MemoryEfficientNB() {
        @Override
        protected double[] getPredictionConfidence(Document d) {
            return new double[]{ 0.2, 0.0, 0.5 };
        }
    };
    try {
        Field classesNField = MemoryEfficientNB.class.getDeclaredField("classesN");
        classesNField.setAccessible(true);
        classesNField.setInt(classifierWithMockedConfidence, 3);
        Field methodNameField = MemoryEfficientNB.class.getDeclaredField("methodName");
        methodNameField.setAccessible(true);
        methodNameField.set(classifierWithMockedConfidence, "Pred");
    } catch (Exception e) {
        fail("Reflection for subclass failed: " + e.getMessage());
    }
    String result = classifierWithMockedConfidence.getExtendedFeatures(dummyDoc);
    assertEquals("Pred0(0.2) Pred2(0.5) ", result);
}

@Test
public void test7()
{
}
{
    CharacteristicWords words = new CharacteristicWords();
    words.topWords = new Vector<>();
    if (classIndex == 0) {
        words.topWords.add("apple");
        words.topWords.add("banana");
    } else if (classIndex == 1) {
        words.topWords.add("banana");
        words.topWords.add("cherry");
    }
    return words;
}
{
    this.classesN = 2;
}

@Test
public void test8()
{
    MemoryEfficientNB nb = new MemoryEfficientNB();
    nb.sampleSize = 2;
    nb.weights = new double[]{ 0.1, 0.2 };
    nb.classCounts = new double[]{ 1.0, 2.0 };
    nb.wordCounts = new double[]{ 5.0, 6.0 };
    nb.fidCount = 1;
    nb.map = new MockFeatureMap(2);
    Vector<Hashtable<Integer, Double>> fidCounts = new Vector<>();
    Hashtable<Integer, Double> map1 = new Hashtable<>();
    map1.put(101, 1.1);
    fidCounts.add(map1);
    nb.fidCounts = fidCounts;
    File tempFile = File.createTempFile("nb_test", null);
    String pathWithoutExtension = tempFile.getAbsolutePath();
    tempFile.delete();
    nb.save(pathWithoutExtension);
    BufferedReader reader = new BufferedReader(new FileReader(pathWithoutExtension));
    assertEquals("2", reader.readLine());
    assertEquals("2", reader.readLine());
    assertEquals("0.1", reader.readLine());
    assertEquals("0.2", reader.readLine());
    assertEquals("1.0", reader.readLine());
    assertEquals("2.0", reader.readLine());
    assertEquals("5.0", reader.readLine());
    assertEquals("6.0", reader.readLine());
    assertEquals("1", reader.readLine());
    assertEquals("1", reader.readLine());
    assertEquals("1", reader.readLine());
    assertEquals("101", reader.readLine());
    assertEquals("1.1", reader.readLine());
    reader.close();
    new File(pathWithoutExtension).delete();
    new File(pathWithoutExtension + ".nb.featuremap").delete();
}
