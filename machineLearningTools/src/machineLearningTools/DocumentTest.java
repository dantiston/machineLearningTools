package machineLearningTools;

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
import java.io.FileNotFoundException;
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
import org.junit.rules.ExpectedException;

public class DocumentTest {

	private DataTest test = new DataTest();

	/* ************************
	 *  Document tests
	 * ************************/

	/* Constants */
	// Gold Documents
	private final String goldDocumentBinaryJSONFile = testFile("goldDocumentBinary.json");
	private final String goldDocumentRealJSONFile = testFile("goldDocumentReal.json");
	private final String goldDocumentLabel = "talk.politics.misc";
	private final HashSet<String> goldDocumentWords = new HashSet<String>(Arrays.asList(new String[]{"com", "your", "cheaper"}));
	// Test Documents
	private final String testDocumentBinaryString = "testLabel word1:1 word2:1 word3:1";
	private final String testDocumentBinaryJSONFile = testFile("testDocumentBinary.json");
	private final String testDocumentRealString = "testLabel word1:3 word2:2 word3:1";
	private final String testDocumentRealJSONFile = testFile("testDocumentReal.json");
	private final String testDocumentGoldLabel = "testLabel";
	private final int testGoldID = 4;
	private final int testDocumentGoldSize = 3;
	private final String unstructuredTextFile = testFile("unstructuredTextDocument.txt");

	/* Variables */
	//// Gold Documents
	Data goldData;
	Document goldDocument;
	private Document goldDocument2;
	private Document goldDocument3;
	// Binary
	private String goldDocumentBinaryJSONString;
	private JSONObject goldDocumentBinaryJSON;
	private Integer goldDocumentBinaryID;
	private String goldDocumentBinaryIDString;
	// Real
	private JSONObject goldDocumentRealJSON;
	private String goldDocumentRealIDString;
	private Document goldDocumentRealFromJSON;
	//// Other documents
	private Document otherDocument;
	private Document testDocument;
	private Document testDocumentFromJSON;
	private JSONObject testDocumentJSON;
	private String testDocumentKey;
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

	void setupDocument() {
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
		this.goldDocument = new Document(this.goldData.getDoc(this.sortedDocs.get(0)));
		this.goldDocument2 = new Document(this.goldDocument);
		this.goldDocument3 = new Document(this.goldDocument);
		// otherDocument is the second longest document in the gold Data
		this.otherDocument = new Document(this.goldData.getDoc(this.sortedDocs.get(1)));
		// Load goldDocumentJSON
		try {
			// Construct gold string
			this.goldDocumentBinaryJSONString = "";
			this.reader = new BufferedReader(new FileReader(this.goldDocumentBinaryJSONFile));
			String lineString;
			while ((lineString = this.reader.readLine()) != null) {
				this.goldDocumentBinaryJSONString += lineString;
			}
			this.reader.close();
			// Load Binary-valued JSON
			this.jsonTokener = new JSONTokener(this.goldDocumentBinaryJSONString);
			this.goldDocumentBinaryJSON = (JSONObject)this.jsonTokener.nextValue();
			this.goldDocumentBinaryIDString = this.goldDocumentBinaryJSON.names().getString(0);
			this.goldDocumentBinaryID = Integer.parseInt(this.goldDocumentBinaryIDString);
			// Load Real-valued JSON
			this.jsonTokener = new JSONTokener(new BufferedReader(new FileReader(this.goldDocumentRealJSONFile)));
			this.goldDocumentRealJSON = (JSONObject)this.jsonTokener.nextValue();
			this.goldDocumentRealIDString = this.goldDocumentRealJSON.names().getString(0);
			this.goldDocumentRealFromJSON = new Document(this.goldDocumentRealJSON.getJSONObject(this.goldDocumentRealIDString), this.goldDocumentRealIDString);
			// Load Real-valued JSON test document
			this.jsonTokener = new JSONTokener(new BufferedReader(new FileReader(this.testDocumentRealJSONFile)));
			this.testDocumentRealJSON = (JSONObject)this.jsonTokener.nextValue();
			this.testDocumentRealIDString = this.testDocumentRealJSON.names().getString(0);
			this.testDocumentRealFromJSON = new Document(this.testDocumentRealJSON.getJSONObject(this.testDocumentRealIDString), this.testDocumentRealIDString);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	void setupDocumentWithTestDocument() {
		this.setupDocument();
		try {
			this.jsonTokener = new JSONTokener(new BufferedReader(new FileReader(this.testDocumentBinaryJSONFile)));
			if (this.jsonTokener.more()) {
				this.testDocumentJSON = (JSONObject) this.jsonTokener.nextValue();
				this.testDocumentKey = this.testDocumentJSON.names().getString(0);
				this.testDocumentFromJSON = new Document((JSONObject)this.testDocumentJSON.get(this.testDocumentKey), this.testDocumentKey);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	void setupDocumentSysOutput() {
		this.setupDocumentWithTestDocument();
		this.testDocumentFromJSON.setSysOutput(this.testDocumentProbabilities);
	}

	void setupDocumentUnstructuredDocument() {
		this.setupDocument();
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

	@Test
	public void testDocumentEqualsDifferentValues() {
		this.setupDocument();
		try {
			Document testDocument = new Document(this.goldDocumentRealJSON.getJSONObject(this.goldDocumentRealIDString), this.goldDocumentRealIDString);
			assertFalse(this.goldDocument.equals(testDocument));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testDocumentEqualsNegative() {
		this.setupDocument();
		assertFalse(this.goldDocument.equals(this.otherDocument));
	}

	@Test
	public void testDocumentEqualsReflexive() {
		this.setupDocument();
		assertTrue(this.goldDocument.equals(this.goldDocument));
	}

	@Test
	public void testDocumentEqualsSymmetric() {
		this.setupDocument();
		assertTrue(this.goldDocument.equals(this.goldDocument2));
		assertTrue(this.goldDocument2.equals(this.goldDocument));
	}

	@Test
	public void testDocumentEqualsTransitivePositive() {
		this.setupDocument();
		assertTrue(this.goldDocument.equals(this.goldDocument2));
		assertTrue(this.goldDocument2.equals(this.goldDocument3));
		assertTrue(this.goldDocument3.equals(this.goldDocument));
	}

	@Test
	public void testDocumentEqualsTransitiveNegative() {
		this.setupDocument();
		assertTrue(this.goldDocument.equals(this.goldDocument2));
		assertFalse(this.goldDocument2.equals(this.otherDocument));
		assertFalse(this.otherDocument.equals(this.goldDocument3));
		assertTrue(this.goldDocument3.equals(this.goldDocument));
	}

	@Test
	public void testDocumentEqualsConsistent() {
		this.setupDocument();
		for (int i=0; i<3; i++) {
			if (!this.goldDocument.equals(this.goldDocument2)) {
				fail();
			}
		}
	}

	@Test
	public void testDocumentEqualsNull() {
		this.setupDocument();
		assertFalse(this.goldDocument.equals(null));
	}

	// hashCode tests

	@Test
	public void testDocumentHashCodeReflexive() {
		this.setupDocument();
		assertEquals(this.goldDocument.hashCode(), this.goldDocument.hashCode());
	}

	@Test
	public void testDocumentHashCodeSymmetric() {
		this.setupDocument();
		assertEquals(this.goldDocument.hashCode(), this.goldDocument2.hashCode());
	}

	@Test
	public void testDocumentHashCodeNegative() {
		this.setupDocument();
		assertFalse(this.goldDocument.hashCode() == this.otherDocument.hashCode());
	}

	@Test
	public void testDocumentHashCodeConsistent() {
		this.setupDocument();
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
	public void testDocumentToString() {
		this.setupDocument();
		this.scratch = "{" + this.goldDocument.toString() + "}";
		assertEquals(this.scratch, this.goldDocumentBinaryJSONString);
	}

	@Test
	public void testDocumentSize() {
		this.testDocument = new Document(this.testDocumentBinaryString);
		assertThat(this.testDocument.size(), is(this.testDocumentGoldSize));
	}

	//// Core functionality tests

	/**
	 * Relies on testDocumentConstructorFromString()
	 */
	@Test
	public void testDocumentInitialize() {
		Document.initialize();
		assertTrue(Document.getDocCount() == 0);
		this.testDocument = new Document(this.testDocumentBinaryString);
		assertTrue(Document.getDocCount() == 1);
		Document.initialize();
		assertTrue(Document.getDocCount() == 0);
	}

	// Constructor tests

	/**
	 * Relies on testDocumentConstructorFromJSON()
	 */
	@Test
	public void testDocumentConstructorFromStringBinary() {
		this.setupDocumentWithTestDocument();
		this.testDocument = new Document(this.testDocumentBinaryString);
		assertTrue(this.testDocument.equals(this.testDocumentFromJSON));
	}

	@Test
	public void testDocumentConstructorFromStringRealValued() {
		this.setupDocument();
		Document testDocument = new Document(this.testDocumentRealString);
		assertTrue(testDocument.equals(this.testDocumentRealFromJSON));
	}

	@Test
	public void testDocumentConstructorFromJSONBinary() {
		this.setupDocument();
		try {
			this.testDocument = new Document(this.goldDocumentBinaryJSON.getJSONObject(this.goldDocumentBinaryIDString), this.goldDocumentBinaryIDString);
			assertTrue(this.testDocument.equals(this.goldDocument));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testDocumentConstructorFromJSONRealValued() {
		try {
			this.setupDocument();
			Document testDocument = new Document(this.goldDocumentRealJSON.getJSONObject(this.goldDocumentRealIDString), this.goldDocumentRealIDString);
			assertTrue(testDocument.equals(this.goldDocumentRealFromJSON));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testDocumentConstructorFromJSONMissingFeaturesThrowsException() {
		this.setupDocument();
		try {
			this.jsonTokener = new JSONTokener("{\"8\":{\"label\":\"talk.politics.misc\"}}");
			JSONObject testJSON;
			testJSON = (JSONObject) this.jsonTokener.nextValue();
			this.exception.expect(IllegalArgumentException.class);
			new Document(testJSON.getJSONObject(this.goldDocumentRealIDString), this.goldDocumentRealIDString);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testDocumentConstructorFromJSONMissingLabelThrowsException() {
		this.setupDocument();
		try {
			this.jsonTokener = new JSONTokener("{\"8\":{\"features\":{\"com\":\"5\",\"your\":\"4\",\"cheaper\":\"1\"}}}");
			JSONObject testJSON;
			testJSON = (JSONObject) this.jsonTokener.nextValue();
			this.exception.expect(IllegalArgumentException.class);
			new Document(testJSON.getJSONObject(this.goldDocumentRealIDString), this.goldDocumentRealIDString);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testDocumentConstructorFromDocument() {
		this.setupDocument();
		this.testDocument = new Document(this.goldDocument);
		assertTrue(this.testDocument.equals(this.goldDocument));
	}

	@Test
	public void testDocumentConstructorFromUnstructuredText() {
		this.setupDocumentUnstructuredDocument();
		this.testDocument = new Document(this.unstructuredText, true);
		// Compare the words because this constructor
		// does not instantiate a label
		assertTrue(this.testDocument.getWords().equals(this.goldDocument.getWords()));
	}

	@org.junit.Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void testDocumentConstructorFromNullDocument() {
		Document nullDocument = null;
		this.exception.expect(NullPointerException.class);
		this.testDocument = new Document(nullDocument);
	}

	@Test
	public void testDocumentConstructorFromNullString() {
		this.setupDocument();
		String nullString = null;
		this.exception.expect(NullPointerException.class);
		this.testDocument = new Document(nullString);
	}

	@Test
	public void testDocumentConstructorFromNullUnstructuredText() {
		String nullString = null;
		this.exception.expect(NullPointerException.class);
		this.testDocument = new Document(nullString, true);
	}

	@Test
	public void testDocumentConstructorFromNullJSON() {
		JSONObject nullJSON = null;
		this.exception.expect(NullPointerException.class);
		this.testDocument = new Document(nullJSON, "key1");
	}

	@Test
	public void testDocumentConstructorFromNullJSONKey() {
		this.setupDocument();
		String nullKey = null;
		this.exception.expect(NullPointerException.class);
		this.testDocument = new Document(this.goldDocumentBinaryJSON, nullKey);
	}

	@Test
	public void testDocumentConstructorFromNullJSONBoth() {
		JSONObject nullJSON = null;
		String nullKey = null;
		this.exception.expect(NullPointerException.class);
		this.testDocument = new Document(nullJSON, nullKey);
	}

	@Test
	public void testDocumentConstructorThrowsIllegalArgumentExceptionWithBadFormatting() {
		this.exception.expect(IllegalArgumentException.class);
		new Document("label w1 w2");
	}

	@Test
	public void testDocumentConstructorThrowsNumberFormatExceptionWithBadNumberRepresentation() {
		this.exception.expect(IllegalArgumentException.class);
		new Document("label w1:abc w2:&^%");
	}

	// Setter tests

	/**
	 * Tests setupDocumentSysOutput()
	 * Relies on getSysOutput(), maxKeyByValue()
	 */
	@Test
	public void testDocumentSetSysOutput() {
		this.setupDocumentSysOutput();
		assertThat(this.testDocumentFromJSON.getSysOutput(), is(maxKeyByValue(this.testDocumentProbabilities)));
		this.testDocumentFromJSON.setSysOutput(this.testDocumentProbabilities);
		assertThat(this.testDocumentFromJSON.getSysOutput(), is(maxKeyByValue(this.testDocumentProbabilities)));
	}

	@Test
	public void testSetSysOutputNullThrowsNullPointerException() {
		this.setupDocument();
		HashMap<String, Double> nullMap = null;
		this.exception.expect(NullPointerException.class);
		this.goldDocument.setSysOutput(nullMap);
	}

	@Test
	public void testSetSysOutputEmptyProbabilitiesThrowsIllegalArgumentException() {
		this.setupDocument();
		this.exception.expect(IllegalArgumentException.class);
		this.goldDocument.setSysOutput(new HashMap<String, Double>());
	}

	// Getter tests

	/**
	 * Relies on testDocumentSetSysOutput()
	 */
	@Test
	public void testDocumentGetLabelProb() {
		this.setupDocumentSysOutput();
		for (String label: this.testLabels) {
			if (!this.testDocumentFromJSON.getLabelProb(label).equals(this.testDocumentProbabilities.get(label))) {
				fail();
			}
		}
	}

	@Test
	public void testDocumentGetDocID() {
		this.setupDocumentWithTestDocument();
		assertTrue(this.testDocumentFromJSON.getDocID() == this.testGoldID);
		assertTrue(this.goldDocument.getDocID() == this.goldDocumentBinaryID);
	}

	@Test
	public void testDocumentGetLabel() {
		this.setupDocumentWithTestDocument();
		assertEquals(this.testDocumentFromJSON.getLabel(), this.testDocumentGoldLabel);
		assertEquals(this.goldDocument.getLabel(), this.goldDocumentLabel);
	}

	@Test
	public void testDocumentGetWords() {
		this.setupDocument();
		assertEquals(this.goldDocument.getWords(), this.goldDocumentWords);
	}

	@Test
	public void testDocumentGetSysOutput() {
		this.setupDocumentSysOutput();
		assertThat(this.testDocumentFromJSON.getSysOutput(), is(maxKeyByValue(this.testDocumentProbabilities)));
	}

	/**
	 * Relies on testDocumentInitialize()
	 */
	@Test
	public void testDocumentGetDocCount() {
		Document.initialize();
		assertTrue(Document.getDocCount() == 0);
		this.testDocument = new Document(this.testDocumentBinaryString);
		assertTrue(Document.getDocCount() == 1);
	}
}
