package machineLearningTools;

import static machineLearningTools.Testing.testFile;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestingTest {

	@Test
	public void testTestFile() {
		assertTrue(testFile("testFile").equals("testDocuments/testFile"));
	}

	@Test(expected=NullPointerException.class)
	public void testTestFileNullThrows() {
		testFile(null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testTestFileEmptyThrows() {
		testFile("");
	}

}
