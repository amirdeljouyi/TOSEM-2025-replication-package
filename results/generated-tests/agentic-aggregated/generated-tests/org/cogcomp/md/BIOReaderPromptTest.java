package org.cogcomp.md;

import edu.illinois.cs.cogcomp.lbjava.parse.Parser;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class BIOReaderPromptTest {

    @Test
    public void defaultConstructorCreatesReader() {
        BIOReader reader = new BIOReader();

        assertNotNull(reader);
        assertTrue(reader instanceof Parser);
    }

    @Test
    public void closeOnDefaultReaderReturnsNormally() {
        BIOReader reader = new BIOReader();

        reader.close();
    }

    @Test
    public void closeIsIdempotent() {
        BIOReader reader = new BIOReader();

        reader.close();
        reader.close();
        reader.close();
    }

    @Test
    public void resetOnDefaultReaderReturnsNormally() {
        BIOReader reader = new BIOReader();

        reader.reset();
    }

    @Test
    public void repeatedResetOnDefaultReaderReturnsNormally() {
        BIOReader reader = new BIOReader();

        reader.reset();
        reader.reset();
        reader.reset();
    }

    @Test
    public void lifecycleMethodsWorkThroughParserInterface() {
        BIOReader reader = new BIOReader();
        Parser parser = reader;

        parser.reset();
        parser.close();

        assertSame(reader, parser);
    }

    @Test(expected = NullPointerException.class)
    public void closeDoesNotInitializeDefaultReaderTokenSequence() {
        BIOReader reader = new BIOReader();

        reader.close();
        reader.next();
    }

    @Test(expected = NullPointerException.class)
    public void resetDoesNotInitializeDefaultReaderTokenSequence() {
        BIOReader reader = new BIOReader();

        reader.reset();
        reader.next();
    }

    @Test(expected = NullPointerException.class)
    public void resetThenCloseStillLeavesNextUninitialized() {
        BIOReader reader = new BIOReader();

        reader.reset();
        reader.close();
        reader.next();
    }

    @Test(expected = NullPointerException.class)
    public void nextOnUnconfiguredReaderFailsInsteadOfReturningPhantomToken() {
        BIOReader reader = new BIOReader();

        reader.next();
    }

    @Test(expected = NullPointerException.class)
    public void nextThroughParserOnUnconfiguredReaderFails() {
        BIOReader reader = new BIOReader();
        Parser parser = reader;

        parser.next();
    }

    @Test(expected = NullPointerException.class)
    public void getTextAnnotationsOnUnconfiguredReaderRejectsMissingMode() {
        BIOReader reader = new BIOReader();

        reader.getTextAnnotations();
    }

    @Test(expected = NullPointerException.class)
    public void resetAndCloseDoNotSupplyConfigurationForAnnotationReading() {
        BIOReader reader = new BIOReader();
        reader.reset();
        reader.close();

        reader.getTextAnnotations();
    }

    @Test(expected = NullPointerException.class)
    public void nullModeIsRejectedBeforeCorpusAccess() {
        new BIOReader("unused", null, "ALL", Boolean.TRUE);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void modeWithoutIndicatorIsRejected() {
        new BIOReader("unused", "ColumnFormat", "ALL", Boolean.TRUE);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void emptyModeIsRejected() {
        new BIOReader("unused", "", "NAM", Boolean.FALSE);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void modeWithTrailingSeparatorIsRejectedBecauseIndicatorIsEmpty() {
        new BIOReader("unused", "ACE05-", "NAM", Boolean.TRUE);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void missingIndicatorIsDetectedBeforeNullPath() {
        new BIOReader(null, "ERE", "ALL", Boolean.FALSE);
    }

    @Test(expected = NullPointerException.class)
    public void nullPathIsRejectedAfterSyntacticallyValidMode() {
        new BIOReader(null, "ColumnFormat-TRAIN", "ALL", Boolean.TRUE);
    }

    @Test(expected = NullPointerException.class)
    public void nullPathIsRejectedRegardlessOfTrainingIndicatorValue() {
        new BIOReader(null, "ColumnFormat-EVAL", "NAM", Boolean.FALSE);
    }

    @Test(expected = NullPointerException.class)
    public void columnFormatRequiresDirectoryRatherThanRegularFile() {
        File sourceFile = new File("BIOReader.java");
        assertTrue(sourceFile.isFile());

        new BIOReader(sourceFile.getAbsolutePath(), "ColumnFormat-TRAIN", "ALL", Boolean.TRUE);
    }

    @Test(expected = NullPointerException.class)
    public void columnFormatRejectsNonexistentDirectory() {
        File absent = new File("bio-reader-absent-directory");
        assertTrue(!absent.exists());

        new BIOReader(absent.getAbsolutePath(), "ColumnFormat-EVAL", "NAM", Boolean.FALSE);
    }

    @Test(expected = NullPointerException.class)
    public void parserReferenceExposesSameFailureForUninitializedNext() {
        Parser parser = new BIOReader();

        parser.next();
    }

    @Test
    public void closeHasNoReturnValueAndLeavesReaderUsableForFurtherLifecycleCalls() {
        BIOReader reader = new BIOReader();

        reader.close();
        reader.reset();
        reader.close();

        assertSame(reader, reader);
    }
}
