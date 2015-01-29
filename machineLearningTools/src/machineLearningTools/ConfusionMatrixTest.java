package machineLearningTools;

import static machineLearningTools.Testing.testFile;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ConfusionMatrixTest {

	MachineLearningToolsTest test = new MachineLearningToolsTest();
	DataTest dataTest = new DataTest();

	/**
	 * Exception handler
	 */
	@org.junit.Rule
	public ExpectedException exception = ExpectedException.none();

	/* ************************
	 *  ConfusionMatrix tests
	 * ************************/

	// Constants
	private final String goldMatrixFile = testFile("goldMatrix.txt");
	private final String goldConfusionMatrixDataFile = testFile("goldConfusionMatrix.vectors.txt");

	// Variables
	private ConfusionMatrix confusionMatrix;
	private String goldMatrix;
	private BufferedReader confusionMatrixReader;
	private StringBuilder confusionMatrixStringBuilder;
	private String lineString;
	private Data confusionMatrixData;

	public void setupConfusionMatrix() {
		this.setupConfusionMatrixData();
		this.confusionMatrix = new ConfusionMatrix(this.confusionMatrixData, this.test.testName);
		this.confusionMatrixStringBuilder = new StringBuilder();
		// Load gold confusion matrix string
		try {
			this.confusionMatrixReader = new BufferedReader(new FileReader(this.goldMatrixFile));
			while ((this.lineString = this.confusionMatrixReader.readLine()) != null) {
				this.confusionMatrixStringBuilder.append(this.lineString);
				this.confusionMatrixStringBuilder.append("\n");
			}
			this.goldMatrix = this.confusionMatrixStringBuilder.toString().trim();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setupConfusionMatrixData() {
		this.confusionMatrixData = new Data(this.goldConfusionMatrixDataFile);
		this.confusionMatrixData.setSysOutput(0, this.test.key1, this.test.testDocumentProbabilities);
		this.confusionMatrixData.setSysOutput(1, this.test.key2, this.test.testDocumentProbabilities);
		this.confusionMatrixData.setSysOutput(2, this.test.key1, this.test.testDocumentProbabilities);
		this.confusionMatrixData.setSysOutput(3, this.test.key2, this.test.testDocumentProbabilities);
	}

	/* Tests */

	// Constructor

	/**
	 * Test constructor for ConfusionMatrix by getting label
	 * Also tests getLabel
	 */
	@Test
	public void testConfusionMatrixConstructor() {
		this.setupConfusionMatrix();
		assertThat(this.confusionMatrix.getLabel(), is(this.test.testName));
	}

	@Test
	public void testConfusionMatrixConstructorNullBothThrowsException() {
		@SuppressWarnings("unused")
		ConfusionMatrix confusionMatrix;
		this.setupConfusionMatrixData();
		this.exception.expect(NullPointerException.class);
		confusionMatrix = new ConfusionMatrix(null, null);
	}

	@Test
	public void testConfusionMatrixConstructorNullDataThrowsException() {
		@SuppressWarnings("unused")
		ConfusionMatrix confusionMatrix;
		this.exception.expect(NullPointerException.class);
		confusionMatrix = new ConfusionMatrix(null, "testLabel");
	}

	@Test
	public void testConfusionMatrixConstructorNullLabelThrowsException() {
		@SuppressWarnings("unused")
		ConfusionMatrix confusionMatrix;
		this.setupConfusionMatrixData();
		this.exception.expect(NullPointerException.class);
		confusionMatrix = new ConfusionMatrix(this.confusionMatrixData, null);
	}

	// Core Methods

	/**
	 * Test ConfusionMatrix increment method
	 *
	 * Note that because the ConfusionMatrix constructor in
	 * setupConfusionMatrix() sets up a ConfusionMatrix object
	 * with each slot specified to 1 (as per goldConfusionMatrix.vectors.txt),
	 * this method checks that each slot is filled with this.incrementTo+1
	 */
	@Test
	public void testConfusionMatrixIncrement() {
		this.setupConfusionMatrix();
		for (int i=0; i<this.test.incrementTo; i++) {
			this.confusionMatrix.increment(this.test.key1, this.test.key1);
			this.confusionMatrix.increment(this.test.key1, this.test.key2);
			this.confusionMatrix.increment(this.test.key2, this.test.key1);
			this.confusionMatrix.increment(this.test.key2, this.test.key2);
		}
		assertThat(this.confusionMatrix.get(this.test.key1, this.test.key1), is(this.test.incrementTo+1));
		assertThat(this.confusionMatrix.get(this.test.key1, this.test.key2), is(this.test.incrementTo+1));
		assertThat(this.confusionMatrix.get(this.test.key2, this.test.key1), is(this.test.incrementTo+1));
		assertThat(this.confusionMatrix.get(this.test.key2, this.test.key2), is(this.test.incrementTo+1));
	}

	// Getters

	/**
	 * Test ConfusionMatrix get method
	 *
	 * Note that because the ConfusionMatrix constructor in
	 * setupConfusionMatrix() sets up a ConfusionMatrix object
	 * with each slot specified to 1 (as per goldConfusionMatrix.vectors.txt),
	 * this method checks that the slot is filled with 2
	 */
	@Test
	public void testConfusionMatrixGet() {
		this.setupConfusionMatrix();
		assertThat(this.confusionMatrix.get(this.test.key1, this.test.key2), is(1));
		this.confusionMatrix.increment(this.test.key1, this.test.key2);
		assertThat(this.confusionMatrix.get(this.test.key1, this.test.key2), is(2));
	}

	// Basic methods

	@Test
	public void testConfusionMatrixToString() {
		this.setupConfusionMatrix();
		// Add values to confusion matrix, one at each spot;
		assertTrue(this.confusionMatrix.toString().trim().equals(this.goldMatrix));
	}

	@Test
	public void testConfusionMatrixToStringEmpty() {
		fail("need to implement; have to create an empty Data object");
	}

}
