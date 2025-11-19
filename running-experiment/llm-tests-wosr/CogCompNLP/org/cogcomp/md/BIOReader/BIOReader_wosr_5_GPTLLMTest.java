public class BIOReader_wosr_5_GPTLLMTest { 

 @Test
    public void testDefaultConstructor() {
        BIOReader reader = new BIOReader();
        assertNotNull(reader);
    }
@Test(expected = RuntimeException.class)
    public void testConstructorWithInvalidPath() {
        new BIOReader("invalid/path/to/data", "ACE05-TRAIN", "NAM", true);
    }
@Test
    public void testConstructorWithColumnFormatBIO() {
        String path = "src/test/resources/column_format_sample";
        Parser parser = null;
        try {
            parser = new BIOReader(path, "ColumnFormat-TRAIN", "ALL", true);
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }

        assertNotNull(parser);
        Object token = parser.next();
        assertNotNull(token);
        assertTrue(token instanceof Constituent);
    }
@Test
    public void testConstructorWithColumnFormatBIOLU() {
        String path = "src/test/resources/column_format_sample";
        BIOReader reader = null;
        try {
            reader = new BIOReader(path, "ColumnFormat-EVAL", "ALL", false);
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
        assertNotNull(reader);
        Object obj1 = reader.next();
        assertNotNull(obj1);
        assertTrue(obj1 instanceof Constituent);
        Object obj2 = reader.next();
        Object obj3 = reader.next();
        Object obj4 = reader.next();
        while (reader.next() != null) {
            
        }
        reader.reset();
        Object resetObj = reader.next();
        assertNotNull(resetObj);
    }
@Test
    public void testNextReturnsNullWhenExhausted() {
        String path = "src/test/resources/column_format_sample";
        BIOReader reader = null;
        try {
            reader = new BIOReader(path, "ColumnFormat-TRAIN", "ALL", true);
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
        Object current = reader.next();
        while (current != null) {
            current = reader.next();
        }
        Object afterNull = reader.next();
        assertNull(afterNull);
    }
@Test
    public void testResetResetsIndex() {
        String path = "src/test/resources/column_format_sample";
        BIOReader reader = null;
        try {
            reader = new BIOReader(path, "ColumnFormat-TRAIN", "NAM", true);
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }

        Object first = reader.next();
        assertNotNull(first);
        Object second = reader.next();
        assertNotNull(second);
        reader.reset();
        Object resetFirst = reader.next();
        assertNotNull(resetFirst);
        assertEquals(((Constituent) first).getSurfaceForm(), ((Constituent) resetFirst).getSurfaceForm());
    }
@Test
    public void testGetTextAnnotationsUnknownMode() {
        BIOReader reader = new BIOReader();
        reader.id = "dummy";
        try {
            java.lang.reflect.Field modeField = BIOReader.class.getDeclaredField("_mode");
            modeField.setAccessible(true);
            modeField.set(reader, "UNKNOWNMODE");

            java.lang.reflect.Method method = BIOReader.class.getDeclaredMethod("getTextAnnotations");
            method.setAccessible(true);
            java.util.List<TextAnnotation> result = (java.util.List<TextAnnotation>) method.invoke(reader);
            assertNotNull(result);
            assertTrue(result.isEmpty());
        } catch (Exception e) {
            fail("Reflection failed: " + e.getMessage());
        }
    }
@Test
    public void testBIOViewAttributes() {
        String path = "src/test/resources/column_format_sample";
        BIOReader reader = null;
        try {
            reader = new BIOReader(path, "ColumnFormat-TRAIN", "ALL", true);
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
        Object token = reader.next();
        assertTrue(token instanceof Constituent);
        Constituent c = (Constituent) token;
        assertNotNull(c.getAttribute("BIO"));
        String label = c.getAttribute("BIO");
        assertTrue(label.equals("O") || label.startsWith("B-") || label.startsWith("I-"));
    }
@Test
    public void testEntityMentionTypeAttribute() {
        String path = "src/test/resources/column_format_sample";
        BIOReader reader = null;
        try {
            reader = new BIOReader(path, "ColumnFormat-TRAIN", "ALL", true);
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
        Constituent c = null;
        while ((c = (Constituent) reader.next()) != null) {
            String bio = c.getAttribute("BIO");
            if (!bio.equals("O")) {
                assertNotNull(c.getAttribute("EntityMentionType"));
            }
        }
    }
@Test
    public void testWordNetAttributes() {
        String path = "src/test/resources/column_format_sample";
        BIOReader reader = null;
        try {
            reader = new BIOReader(path, "ColumnFormat-TRAIN", "ALL", true);
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
        Object obj = reader.next();
        assertNotNull(obj);
        Constituent c = (Constituent) obj;
        if (!c.toString().contains("http")) {
            assertNotNull(c.getAttribute("WORDNETTAG"));
            assertNotNull(c.getAttribute("WORDNETHYM"));
        } else {
            assertEquals(",", c.getAttribute("WORDNETTAG"));
            assertEquals(",", c.getAttribute("WORDNETHYM"));
        }
    }
@Test
    public void testIsTrainingAttribute() {
        String path = "src/test/resources/column_format_sample";
        BIOReader reader = null;
        try {
            reader = new BIOReader(path, "ColumnFormat-TRAIN", "ALL", true);
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
        Constituent c = (Constituent) reader.next();
        assertEquals("true", c.getAttribute("isTraining"));

        reader = new BIOReader(path, "ColumnFormat-EVAL", "ALL", true);
        c = (Constituent) reader.next();
        assertEquals("false", c.getAttribute("isTraining"));
    } 
}