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

public class RealValuedDataTest {

	MachineLearningToolsTest test = new MachineLearningToolsTest();

	/* Constants */
	private final String testVectorFile = testFile("example.real.vectors.txt");
	private final String testVectorJsonFile = testFile("example.real.vectors.json");
	private final String otherVectorJsonFile = testFile("other.real.vectors.json");

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
		this.test.goldData = new RealValuedData(this.goldDataJson); // testVectorJsonFile
		this.test.goldData2 = new RealValuedData(this.goldDataJson);
		this.test.goldData3 = new RealValuedData(this.goldDataJson);
		this.test.otherData = new RealValuedData(this.otherDataJson);
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
	public void testRealValuedDataEqualsNegative() {
		this.setupData();
		assertFalse(this.test.goldData.equals(this.test.otherData));
	}

	@Test
	public void testRealValuedDataEqualsReflexive() {
		this.setupData();
		assertTrue(this.test.goldData.equals(this.test.goldData));
	}

	@Test
	public void testRealValuedDataEqualsSymmetric() {
		this.setupData();
		assertTrue(this.test.goldData.equals(this.test.goldData2));
		assertTrue(this.test.goldData2.equals(this.test.goldData));
	}

	@Test
	public void testRealValuedDataEqualsTransitivePositive() {
		this.setupData();
		assertTrue(this.test.goldData.equals(this.test.goldData2));
		assertTrue(this.test.goldData2.equals(this.test.goldData3));
		assertTrue(this.test.goldData3.equals(this.test.goldData));
	}

	@Test
	public void testRealValuedDataEqualsTransitiveNegative() {
		this.setupData();
		assertTrue(this.test.goldData.equals(this.test.goldData2));
		assertFalse(this.test.goldData2.equals(this.test.otherData));
		assertFalse(this.test.otherData.equals(this.test.goldData3));
		assertTrue(this.test.goldData3.equals(this.test.goldData));
	}

	@Test
	public void testRealValuedDataEqualsConsistent() {
		this.setupData();
		for (int i=0; i<3; i++) {
			assertTrue(this.test.goldData.equals(this.test.goldData2));
		}
	}

	@Test
	public void testRealValuedDataEqualsNull() {
		this.setupData();
		assertFalse(this.test.goldData.equals(null));
	}

	// hashCode tests

	@Test
	public void testRealValuedDataHashCodeReflexive() {
		this.setupData();
		assertEquals(this.test.goldData.hashCode(), this.test.goldData.hashCode());
	}

	@Test
	public void testRealValuedDataHashCodeSymmetric() {
		this.setupData();
		assertEquals(this.test.goldData.hashCode(), this.test.goldData2.hashCode());
	}

	@Test
	public void testRealValuedDataHashCodeNegative() {
		this.setupData();
		assertFalse(this.test.goldData.hashCode() == this.test.otherData.hashCode());
	}

	@Test
	public void testRealValuedDataHashCodeConsistent() {
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
	public void testRealValuedDataConstructor() {
		this.setupData();
		assertTrue(this.test.goldData != null);
		// Load data
		Data systemData = new RealValuedData(this.testVectorFile); // != testVectorJsonFile
		// Make sure system loads data equivalent to gold data
		assertTrue(systemData.equals(this.test.goldData));
	}

	/**
	 * This test relies on readDataFromJSON()
	 * and readDataFromFile()
	 */
	@Test
	public void testDataConstructorEmpty() {
		Data emptyData = new RealValuedData();
		assertEquals(emptyData.size(), 0);
	}

	/**
	 * Test that Data constructor passed a null
	 * throws a NullPointerException error
	 */
	@SuppressWarnings("unused")
	@Test(expected=NullPointerException.class)
	public void testDataJSONConstructorNullThrowsException() {
		JSONObject nullJSON = null;
		Data systemData = new RealValuedData(nullJSON);
	}

	/**
	 * Test that Data constructor passed a null
	 * throws a NullPointerException error
	 */
	@SuppressWarnings("unused")
	@Test(expected=NullPointerException.class)
	public void testDataStringConstructorNullThrowsException() {
		String nullFileName = null;
		Data systemData = new RealValuedData(nullFileName);
	}


	/**
	 * This test relies on readDataJSON()
	 */
	@Test
	public void testRealValuedDataReadDataFromFile() {
		// Load data from file
		Data systemData = new RealValuedData(this.testVectorFile);
		// Convert data to JSON using (gold) org.json library
		this.setupJsonData();
		Data jsonData = new RealValuedData(this.goldDataJson);
		// Compare to gold JSON
		assertTrue(systemData.equals(jsonData));
	}

	@Test
	public void testRealValuedDataReadDataJSON() {
		this.setupData();
		this.setupJsonData();
		// Load data from gold JSON
		Data jsonData = new RealValuedData(this.goldDataJson);
		assertTrue(jsonData.equals(this.test.goldData));
		// Output JSON using (gold) org.json library
		JSONTokener jsonTokener = new JSONTokener(jsonData.toString());
		JSONObject systemJSON = null;
		try {
			systemJSON = (JSONObject) jsonTokener.nextValue();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// Compare string representations
		assertTrue(systemJSON.toString().equals(this.goldDataJson.toString()));
	}

	@Test
	public void testRealValuedDataSetSysOutput() {
		this.setupData();
		this.test.goldData.setSysOutput(1, "testClass1", this.test.testDocumentProbabilities);
		assertEquals(this.test.goldData.getDoc(1).getSysOutput(), "testClass1");
		assertTrue(this.test.goldData.getDoc(1).getLabelProb("key1").equals(0.33d));
		this.test.goldData.setSysOutput(1, "testClass2", this.test.testDocumentProbabilities);
		assertEquals(this.test.goldData.getDoc(1).getSysOutput(), "testClass2");
	}

	@Test
	public void testRealValuedDataGetEntropy() {
		assertTrue(this.test.infoGainGoldRealData.getEntropy(this.test.goldWith) == this.test.goldWithEnt);
		assertTrue(this.test.infoGainGoldRealData.getEntropy(this.test.goldWithOut) == this.test.goldWithOutEnt);
	}

	@Test
	public void testRealValuedDataGetLabel() {
		this.setupData();
		assertEquals(this.test.goldData.getDoc(1).getLabel(), this.test.goldDoc1Label);
	}

	@Test
	public void testRealValuedDataGetAllLabels() {
		this.setupData();
		assertEquals(this.test.goldData.getAllLabels(), this.test.goldLabels);
	}

	@Test
	public void testRealValuedDataGetAllFeatures() {
		this.setupData();
		assertEquals(this.test.goldData.getAllFeatures(), this.test.goldFeatures);
	}

}
