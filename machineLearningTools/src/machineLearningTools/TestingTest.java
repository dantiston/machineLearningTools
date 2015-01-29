package machineLearningTools;

import static machineLearningTools.Testing.testFile;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TestingTest {

	@org.junit.Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void testTestFile() {
		assertTrue(testFile("testFile").equals("testDocuments/testFile"));
	}

	@Test
	public void testTestFileNullThrows() {
		this.exception.expect(NullPointerException.class);
		testFile(null);
	}

	@Test
	public void testTestFileEmptyThrows() {
		this.exception.expect(IllegalArgumentException.class);
		testFile("");
	}

}
