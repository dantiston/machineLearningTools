package machineLearningTools;

import static machineLearningTools.Testing.testFile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class DataTest {

	MachineLearningToolsTest test = new MachineLearningToolsTest();

	/* ************************
	 *  Data tests
	 * ************************/

	/* Constants */
	private final String testVectorFile = testFile("example.vectors.txt");
	private final String testVectorJsonFile = testFile("example.vectors.json");
	private final String otherVectorJsonFile = testFile("other.vectors.json");

	/* Variables */
	private JSONObject goldDataJson;
	private JSONObject otherDataJson;
	private BufferedReader reader;
	private JSONTokener jsonTokener;

	/* Setup */

	/**
	 * setupData
	 * @author T.J. Trimble
	 * Load gold data structure from stored JSON
	 */
	public void setupData() {
		//// Load gold data structure using JSON
		this.goldDataJson = new JSONObject();
		this.otherDataJson = new JSONObject();
		this.setupJsonData();
		this.test.goldData = new Data(this.goldDataJson);
		this.test.goldData2 = new Data(this.goldDataJson);
		this.test.goldData3 = new Data(this.goldDataJson);
		this.test.otherData = new Data(this.otherDataJson);
	}

	public void setupJsonData() {
		try {
			// Load gold Data
			this.reader = new BufferedReader(new FileReader(this.testVectorJsonFile));
			this.jsonTokener = new JSONTokener(this.reader);
			if (this.jsonTokener.more()) {
				this.goldDataJson = (JSONObject) this.jsonTokener.nextValue();
			}
			this.reader.close();
			// Load other Data
			this.reader = new BufferedReader(new FileReader(this.otherVectorJsonFile));
			this.jsonTokener = new JSONTokener(this.reader);
			if (this.jsonTokener.more()) {
				this.otherDataJson = (JSONObject) this.jsonTokener.nextValue();
			}
			this.reader.close();
		} catch (IOException e) {
			System.err.append(String.format("File \"%s\" not found!%n", this.testVectorJsonFile));
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/* Tests */

	// equals tests

	@Test
	public void testDataEqualsNegative() {
		this.setupData();
		assertFalse(this.test.goldData.equals(this.test.otherData));
	}

	@Test
	public void testDataEqualsReflexive() {
		this.setupData();
		assertTrue(this.test.goldData.equals(this.test.goldData));
	}

	@Test
	public void testDataEqualsSymmetric() {
		this.setupData();
		assertTrue(this.test.goldData.equals(this.test.goldData2));
		assertTrue(this.test.goldData2.equals(this.test.goldData));
	}

	@Test
	public void testDataEqualsTransitivePositive() {
		this.setupData();
		assertTrue(this.test.goldData.equals(this.test.goldData2));
		assertTrue(this.test.goldData2.equals(this.test.goldData3));
		assertTrue(this.test.goldData3.equals(this.test.goldData));
	}

	@Test
	public void testDataEqualsTransitiveNegative() {
		this.setupData();
		assertTrue(this.test.goldData.equals(this.test.goldData2));
		assertFalse(this.test.goldData2.equals(this.test.otherData));
		assertFalse(this.test.otherData.equals(this.test.goldData3));
		assertTrue(this.test.goldData3.equals(this.test.goldData));
	}

	@Test
	public void testDataEqualsConsistent() {
		this.setupData();
		for (int i=0; i<3; i++) {
			assertTrue(this.test.goldData.equals(this.test.goldData2));
		}
	}

	@Test
	public void testDataEqualsNull() {
		this.setupData();
		assertFalse(this.test.goldData.equals(null));
	}

	// hashCode tests

	@Test
	public void testDataHashCodeReflexive() {
		this.setupData();
		assertEquals(this.test.goldData.hashCode(), this.test.goldData.hashCode());
	}

	@Test
	public void testDataHashCodeSymmetric() {
		this.setupData();
		assertEquals(this.test.goldData.hashCode(), this.test.goldData2.hashCode());
	}

	@Test
	public void testDataHashCodeNegative() {
		this.setupData();
		assertFalse(this.test.goldData.hashCode() == this.test.otherData.hashCode());
	}

	@Test
	public void testDataHashCodeConsistent() {
		this.setupData();
		int gold = this.test.goldData.hashCode();
		int current;
		for (int i=0; i<5; i++) {
			current = this.test.goldData.hashCode();
			if (current != gold) {
				fail();
			}
		}
	}

	// Core Functionality Tests

	/**
	 * This test relies on readDataFromJSON()
	 * and readDataFromFile()
	 */
	@Test
	public void testDataConstructorBinaryValued() {
		this.setupData();
		assertTrue(this.test.goldData != null);
		// Load data
		Data systemData = new Data(this.testVectorFile);
		// Make sure system loads data equivalent to gold data
		assertTrue(systemData.equals(this.test.goldData));
	}

//	@Ignore
//	@Test
//	public void testDataConstructorRealValued() {
//
//	}

	/**
	 * Rule for testDataConstructorNullThrowsException()
	 * @see testDataConstructorNullThrowsException()
	 */
	@org.junit.Rule
	public ExpectedException exception = ExpectedException.none();

	/**
	 * Test that Data constructor passed a null
	 * throws a NullPointerException error
	 */
	@SuppressWarnings("unused")
	@Test
	public void testDataJSONConstructorNullThrowsException() {
		JSONObject nullJSON = null;
		this.exception.expect(NullPointerException.class);
		Data systemData = new Data(nullJSON);
	}

	/**
	 * Test that Data constructor passed a null
	 * throws a NullPointerException error
	 */
	@SuppressWarnings("unused")
	@Test
	public void testDataStringConstructorNullThrowsException() {
		String nullFileName = null;
		this.exception.expect(NullPointerException.class);
		Data systemData = new Data(nullFileName);
	}


	/**
	 * This test relies on readDataJSON()
	 */
	@Test
	public void testDataReadDataFromFile() {
		// Load data from file
		Data systemData = new Data(this.testVectorFile);
		// Convert data to JSON using (gold) org.json library
		this.setupJsonData();
		Data jsonData = new Data(this.goldDataJson);
		// Compare to gold JSON
		assertTrue(systemData.equals(jsonData));
	}

	@Test
	public void testDataReadDataJSON() {
		// Load data from gold JSON
		this.setupJsonData();
		Data jsonData = new Data(this.goldDataJson);
		// Output JSON using (gold) org.json library
		JSONTokener jsonTokener = new JSONTokener(jsonData.toString());
		JSONObject systemJSON = null;
		try {
			systemJSON = (JSONObject) jsonTokener.nextValue();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// Compare JSON representations
		assertTrue(systemJSON.toString().equals(this.goldDataJson.toString()));
	}

	@Test
	public void testDataSetSysOutput() {
		this.setupData();
		this.test.goldData.setSysOutput(1, "testClass1", this.test.testDocumentProbabilities);
		assertEquals(this.test.goldData.getDoc(1).getSysOutput(), "testClass1");
		assertTrue(this.test.goldData.getDoc(1).getLabelProb("key1").equals(0.33d));
		this.test.goldData.setSysOutput(1, "testClass2", this.test.testDocumentProbabilities);
		assertEquals(this.test.goldData.getDoc(1).getSysOutput(), "testClass2");
	}

	@Test
	public void testDataGetEntropy() {
		assertTrue(this.test.infoGainGoldData.getEntropy(this.test.goldWith) == this.test.goldWithEnt);
		assertTrue(this.test.infoGainGoldData.getEntropy(this.test.goldWithOut) == this.test.goldWithOutEnt);
	}

	@Test
	public void testDataGetLabel() {
		this.setupData();
		assertEquals(this.test.goldData.getDoc(1).getLabel(), this.test.goldDoc1Label);
	}

	@Test
	public void testDataGetAllLabels() {
		this.setupData();
		assertEquals(this.test.goldData.getAllLabels(), this.test.goldLabels);
	}

}
