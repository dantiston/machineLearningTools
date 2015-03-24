package machineLearningTools;

import static machineLearningTools.MLMath.pseudoEqual;
import static machineLearningTools.Testing.testFile;
import static machineLearningTools.Util.maxKeyByValue;
import static machineLearningTools.Util.sortedKeysByValue;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Test;

public class RealValuedDocumentTest {

	private RealValuedDataTest test = new RealValuedDataTest();

	/* ************************
	 *  Document tests
	 * ************************/

	/* Constants */
	// Gold Documents
	private final String goldDocumentRealJSONFile = testFile("goldDocumentReal.json");
	private final String goldDocumentLabel = "talk.politics.misc";
	private final int goldDocumentRealID = 8;
	private final HashSet<String> goldDocumentWords = new HashSet<String>(Arrays.asList(new String[]{"com", "your", "cheaper"}));
	// Test Documents
	private final String testDocumentRealString = "testLabel word1:3 word2:2 word3:1";
	private final String testDocumentRealJSONFile = testFile("testDocumentReal.json");
	private final String testDocumentGoldLabel = "testLabel";
	private final int testGoldID = 4;
	private final int testDocumentGoldSize = 3;
	private final String unstructuredTextFile = testFile("unstructuredTextDocument.txt");
	private final Double goldMagnitude = Math.sqrt(14); // sqrt(3^2 + 2^2 + 1^2)

	/* Variables */
	//// Gold Documents
	Data goldData;
	Document goldDocument;
	Document goldDocument2;
	private Document goldDocument3;
	// Real
	private JSONObject goldDocumentRealJSON;
	private String goldDocumentRealJSONString;
	private String goldDocumentRealIDString;
	private Document goldDocumentRealFromJSON;
	//// Other documents
	Document otherDocument;
	private Document testDocument;
	private HashMap<String, Double> testDocumentProbabilities;
	private HashSet<String> testLabels;
	private String unstructuredText;
	// Test document from JSON
	private JSONObject testDocumentRealJSON;
	private String testDocumentRealIDString;
	private Document testDocumentRealFromJSON;
	//// Scratch Variables
	private HashMap<Integer, Integer> docSizes;
	private ArrayList<Integer> sortedDocs;
	private String scratch;
	private BufferedReader reader;
	private JSONTokener jsonTokener;

	void setupRealValuedDocument() {
		// Setup Data
		this.test.setupData();
		this.goldData = this.test.test.goldData;
		this.testDocumentProbabilities = this.test.test.testDocumentProbabilities;
		this.testLabels = this.test.test.testLabels;
		// Setup Document
		this.docSizes = new HashMap<Integer, Integer>();
		for (Integer docID: this.goldData.getIDs()) {
			this.docSizes.put(docID, this.goldData.getDoc(docID).size());
		}
		// goldDocument is the longest document in the gold Data
		this.sortedDocs = sortedKeysByValue(this.docSizes, true);
		this.goldDocument = new RealValuedDocument(this.goldData.getDoc(this.sortedDocs.get(0)));
		this.goldDocument2 = new RealValuedDocument(this.goldDocument);
		this.goldDocument3 = new RealValuedDocument(this.goldDocument);
		// otherDocument is the second longest document in the gold Data
		this.otherDocument = new RealValuedDocument(this.goldData.getDoc(this.sortedDocs.get(1)));
		// Load goldDocumentJSON
		try {
			// Construct gold string
			this.goldDocumentRealJSONString = "";
			this.reader = new BufferedReader(new FileReader(this.goldDocumentRealJSONFile));
			String lineString;
			while ((lineString = this.reader.readLine()) != null) {
				this.goldDocumentRealJSONString += lineString;
			}
			this.reader.close();
			// Load Real-valued JSON
			this.jsonTokener = new JSONTokener(new BufferedReader(new FileReader(this.goldDocumentRealJSONFile)));
			this.goldDocumentRealJSON = (JSONObject)this.jsonTokener.nextValue();
			this.goldDocumentRealIDString = this.goldDocumentRealJSON.names().getString(0);
			this.goldDocumentRealFromJSON = new RealValuedDocument(this.goldDocumentRealJSON.getJSONObject(this.goldDocumentRealIDString), this.goldDocumentRealIDString);
			// Load Real-valued JSON test document
			this.jsonTokener = new JSONTokener(new BufferedReader(new FileReader(this.testDocumentRealJSONFile)));
			this.testDocumentRealJSON = (JSONObject)this.jsonTokener.nextValue();
			this.testDocumentRealIDString = this.testDocumentRealJSON.names().getString(0);
			this.testDocumentRealFromJSON = new RealValuedDocument(this.testDocumentRealJSON.getJSONObject(this.testDocumentRealIDString), this.testDocumentRealIDString);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	void setupRealValuedDocumentSysOutput() {
		this.setupRealValuedDocument();
		this.testDocumentRealFromJSON.setSysOutput(this.testDocumentProbabilities);
	}

	void setupRealValuedDocumentUnstructuredDocument() {
		this.setupRealValuedDocument();
		try {
			this.reader = new BufferedReader(new FileReader(this.unstructuredTextFile));
			this.unstructuredText = ""; // reset
			String lineString;
			while ((lineString = this.reader.readLine()) != null) {
				this.unstructuredText += lineString;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* Tests */
	// equals tests

	/**
	 * This test might be too loose.
	 *
	 */
	@Test
	public void testRealValuedDocumentEqualsDifferentValues() {
		this.setupRealValuedDocument();
		try {
			Document testDocument = new RealValuedDocument(this.goldDocumentRealJSON.getJSONObject(this.goldDocumentRealIDString), this.goldDocumentRealIDString);
			assertFalse(this.otherDocument.equals(testDocument));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testRealValuedDocumentEqualsNegative() {
		this.setupRealValuedDocument();
		assertFalse(this.goldDocument.equals(this.otherDocument));
	}

	@Test
	public void testRealValuedDocumentEqualsReflexive() {
		this.setupRealValuedDocument();
		assertTrue(this.goldDocument.equals(this.goldDocument));
	}

	@Test
	public void testRealValuedDocumentEqualsSymmetric() {
		this.setupRealValuedDocument();
		assertTrue(this.goldDocument.equals(this.goldDocument2));
		assertTrue(this.goldDocument2.equals(this.goldDocument));
	}

	@Test
	public void testRealValuedDocumentEqualsTransitivePositive() {
		this.setupRealValuedDocument();
		assertTrue(this.goldDocument.equals(this.goldDocument2));
		assertTrue(this.goldDocument2.equals(this.goldDocument3));
		assertTrue(this.goldDocument3.equals(this.goldDocument));
	}

	@Test
	public void testRealValuedDocumentEqualsTransitiveNegative() {
		this.setupRealValuedDocument();
		assertTrue(this.goldDocument.equals(this.goldDocument2));
		assertFalse(this.goldDocument2.equals(this.otherDocument));
		assertFalse(this.otherDocument.equals(this.goldDocument3));
		assertTrue(this.goldDocument3.equals(this.goldDocument));
	}

	@Test
	public void testRealValuedDocumentEqualsConsistent() {
		this.setupRealValuedDocument();
		for (int i=0; i<3; i++) {
			if (!this.goldDocument.equals(this.goldDocument2)) {
				fail();
			}
		}
	}

	@Test
	public void testRealValuedDocumentEqualsNull() {
		this.setupRealValuedDocument();
		assertFalse(this.goldDocument.equals(null));
	}

	// hashCode tests

	@Test
	public void testRealValuedDocumentHashCodeReflexive() {
		this.setupRealValuedDocument();
		assertEquals(this.goldDocument.hashCode(), this.goldDocument.hashCode());
	}

	@Test
	public void testRealValuedDocumentHashCodeSymmetric() {
		this.setupRealValuedDocument();
		assertEquals(this.goldDocument.hashCode(), this.goldDocument2.hashCode());
	}

	@Test
	public void testRealValuedDocumentHashCodeNegative() {
		this.setupRealValuedDocument();
		assertFalse(this.goldDocument.hashCode() == this.otherDocument.hashCode());
	}

	@Test
	public void testRealValuedDocumentHashCodeConsistent() {
		this.setupRealValuedDocument();
		int gold = this.goldDocument.hashCode();
		int current;
		for (int i=0; i<5; i++) {
			current = this.goldDocument.hashCode();
			if (current != gold) {
				fail();
			}
		}
	}

	// Basic functionality tests

	/**
	 * @author T.J. Trimble
	 * Gold JSON is a JSON object, document returns key:value pair
	 * so, the document toString must be put into an object (add {})
	 */
	@Test
	public void testRealValuedDocumentToString() {
		this.setupRealValuedDocument();
		this.scratch = "{" + this.goldDocument.toString() + "}";
		assertEquals(this.scratch, this.goldDocumentRealJSONString);
	}

	@Test
	public void testRealValuedDocumentSize() {
		this.testDocument = new RealValuedDocument(this.testDocumentRealString);
		assertThat(this.testDocument.size(), is(this.testDocumentGoldSize));
	}

	//// Core functionality tests

	/**
	 * Relies on testDocumentConstructorFromString()
	 */
	@Test
	public void testRealValuedDocumentInitialize() {
		Document.initialize();
		assertTrue(Document.getDocCount() == 0);
		this.testDocument = new RealValuedDocument(this.testDocumentRealString);
		assertTrue(Document.getDocCount() == 1);
		Document.initialize();
		assertTrue(Document.getDocCount() == 0);
	}

	//// Constructor tests

	// Constructor from string tests
	/**
	 * Relies on testDocumentConstructorFromJSON()
	 */
	@Test
	public void testRealValuedDocumentConstructorFromStringBinary() {
		this.setupRealValuedDocument();
		this.testDocument = new RealValuedDocument(this.testDocumentRealString);
		assertTrue(this.testDocument.equals(this.testDocumentRealFromJSON));
	}

	@Test
	public void testRealValuedDocumentConstructorFromStringRealValued() {
		this.setupRealValuedDocument();
		Document testDocument = new RealValuedDocument(this.testDocumentRealString);
		assertTrue(testDocument.equals(this.testDocumentRealFromJSON));
	}

	@Test(expected=IllegalArgumentException.class)
	public void testRealValuedDocumentConstructorFromStringThrowsForNoColon() {
		this.testDocument = new RealValuedDocument("label feat");
	}

	@Test(expected=IllegalArgumentException.class)
	public void testRealValuedDocumentConstructorFromStringThrowsForDuplicateFeatures() {
		this.testDocument = new RealValuedDocument("label feat:1 feat:2");
	}

	@Test(expected=IllegalArgumentException.class)
	public void testRealValuedDocumentConstructorFromStringThrowsIllegalArgumentExceptionWithNoColon() {
		new RealValuedDocument("label w1 w2");
	}

	@Test(expected=NumberFormatException.class)
	public void testRealValuedDocumentConstructorFromStringThrowsNumberFormatExceptionWithBadNumberRepresentation() {
		new RealValuedDocument("label w1:abc w2:&^%");
	}

	@Test(expected=NullPointerException.class)
	public void testRealValuedDocumentConstructorFromNullString() {
		String nullString = null;
		this.testDocument = new RealValuedDocument(nullString);
	}

	// Constructor from JSON tests

	@Test
	public void testRealValuedDocumentConstructorFromJSONRealValued() {
		try {
			this.setupRealValuedDocument();
			Document testDocument = new RealValuedDocument(this.goldDocumentRealJSON.getJSONObject(this.goldDocumentRealIDString), this.goldDocumentRealIDString);
			assertTrue(testDocument.equals(this.goldDocumentRealFromJSON));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Test(expected=IllegalArgumentException.class)
	public void testRealValuedDocumentConstructorFromJSONMissingFeaturesThrowsException() {
		this.setupRealValuedDocument();
		try {
			this.jsonTokener = new JSONTokener("{\"8\":{\"label\":\"talk.politics.misc\"}}");
			JSONObject testJSON;
			testJSON = (JSONObject) this.jsonTokener.nextValue();
			new RealValuedDocument(testJSON.getJSONObject(this.goldDocumentRealIDString), this.goldDocumentRealIDString);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Test(expected=IllegalArgumentException.class)
	public void testRealValuedDocumentConstructorFromJSONMissingLabelThrowsException() {
		this.setupRealValuedDocument();
		try {
			this.jsonTokener = new JSONTokener("{\"8\":{\"features\":{\"com\":\"5\",\"your\":\"4\",\"cheaper\":\"1\"}}}");
			JSONObject testJSON;
			testJSON = (JSONObject) this.jsonTokener.nextValue();
			new RealValuedDocument(testJSON.getJSONObject(this.goldDocumentRealIDString), this.goldDocumentRealIDString);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Test(expected=NullPointerException.class)
	public void testRealValuedDocumentConstructorFromNullJSON() {
		JSONObject nullJSON = null;
		this.testDocument = new RealValuedDocument(nullJSON, "key1");
	}

	@Test(expected=NullPointerException.class)
	public void testRealValuedDocumentConstructorFromNullJSONKey() {
		this.setupRealValuedDocument();
		String nullKey = null;
		this.testDocument = new RealValuedDocument(this.goldDocumentRealJSON, nullKey);
	}

	@Test(expected=NullPointerException.class)
	public void testRealValuedDocumentConstructorFromNullJSONBoth() {
		JSONObject nullJSON = null;
		String nullKey = null;
		this.testDocument = new RealValuedDocument(nullJSON, nullKey);
	}

	// Constructor from Document tests

	@Test
	public void testRealValuedDocumentConstructorFromDocument() {
		this.setupRealValuedDocument();
		this.testDocument = new RealValuedDocument(this.goldDocument);
		assertTrue(this.testDocument.equals(this.goldDocument));
	}

	@Test(expected=NullPointerException.class)
	public void testRealValuedDocumentConstructorFromNullDocument() {
		Document nullDocument = null;
		this.testDocument = new RealValuedDocument(nullDocument);
	}

	// Constructor from Unstructured text tests

	@Test
	public void testRealValuedDocumentConstructorFromUnstructuredText() {
		this.setupRealValuedDocumentUnstructuredDocument();
		this.testDocument = new RealValuedDocument(this.unstructuredText, true);
		// Compare the words because this constructor
		// does not instantiate a label
		assertTrue(this.testDocument.getWords().equals(this.goldDocument.getWords()));
	}

	@Test(expected=NullPointerException.class)
	public void testRealValuedDocumentConstructorFromNullUnstructuredText() {
		String nullString = null;
		this.testDocument = new RealValuedDocument(nullString, true);
	}

	// Setter tests

	/**
	 * Tests setupDocumentSysOutput()
	 * Relies on getSysOutput(), maxKeyByValue()
	 */
	@Test
	public void testRealValuedDocumentSetSysOutput() {
		this.setupRealValuedDocumentSysOutput();
		assertThat(this.testDocumentRealFromJSON.getSysOutput(), is(maxKeyByValue(this.testDocumentProbabilities)));
		this.testDocumentRealFromJSON.setSysOutput(this.testDocumentProbabilities);
		assertThat(this.testDocumentRealFromJSON.getSysOutput(), is(maxKeyByValue(this.testDocumentProbabilities)));
	}

	@Test(expected=NullPointerException.class)
	public void testSetSysOutputNullThrowsNullPointerException() {
		this.setupRealValuedDocument();
		HashMap<String, Double> nullMap = null;
		this.goldDocument.setSysOutput(nullMap);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testSetSysOutputEmptyProbabilitiesThrowsIllegalArgumentException() {
		this.setupRealValuedDocument();
		this.goldDocument.setSysOutput(new HashMap<String, Double>());
	}

	// Getter tests

	/**
	 * Relies on testDocumentSetSysOutput()
	 */
	@Test
	public void testRealValuedDocumentGetLabelProb() {
		this.setupRealValuedDocumentSysOutput();
		for (String label: this.testLabels) {
			if (!this.testDocumentRealFromJSON.getLabelProb(label).equals(this.testDocumentProbabilities.get(label))) {
				fail();
			}
		}
	}

	@Test
	public void testRealValuedDocumentGetDocID() {
		this.setupRealValuedDocument();
		assertTrue(this.testDocumentRealFromJSON.getDocID() == this.testGoldID);
		assertTrue(this.goldDocument.getDocID() == this.goldDocumentRealID);
	}

	@Test
	public void testRealValuedDocumentGetLabel() {
		this.setupRealValuedDocument();
		assertEquals(this.testDocumentRealFromJSON.getLabel(), this.testDocumentGoldLabel);
		assertEquals(this.goldDocument.getLabel(), this.goldDocumentLabel);
	}

	@Test
	public void testRealValuedDocumentGetWords() {
		this.setupRealValuedDocument();
		assertEquals(this.goldDocument.getWords(), this.goldDocumentWords);
	}

	@Test
	public void testRealValuedDocumentGetSysOutput() {
		this.setupRealValuedDocumentSysOutput();
		assertThat(this.testDocumentRealFromJSON.getSysOutput(), is(maxKeyByValue(this.testDocumentProbabilities)));
	}

	/**
	 * Relies on testDocumentInitialize()
	 */
	@Test
	public void testRealValuedDocumentGetDocCount() {
		Document.initialize();
		assertTrue(Document.getDocCount() == 0);
		this.testDocument = new RealValuedDocument(this.testDocumentRealString);
		assertTrue(Document.getDocCount() == 1);
	}

	@Test
	public void testRealValuedDocumentGetMagnitude() {
		this.testDocument = new RealValuedDocument(this.testDocumentRealString);
		assertTrue(pseudoEqual(this.testDocument.getMagnitude(), this.goldMagnitude));
	}
}
