package machineLearningClassifiers.DecisionTreeClassifier;

import static machineLearningTools.Testing.testFile;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import machineLearningTools.Data;
import machineLearningTools.Rule;
import machineLearningTools.Tree;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class DecisionTreeClassifierTest {

	/**
	 * Exception catcher
	 */
	@org.junit.Rule
	public ExpectedException exception = ExpectedException.none();

	//// Constants
	private String dataFile = testFile("train.vectors.min.txt");

	// Gold values
	private final String treeJSONFile = testFile("goldClassifierTree.json");
	private final String rulesJSONFile = testFile("goldRules.json");

	// Parameters
	private final int maxDepth = 5;
	private final Double minGain = 0.1d;
	private final String sysOutputFile = testFile("dt.sysOutput.test.txt");
	private final String modelFile = testFile("dt.modelFile.test.txt");

	//// Variables
	private DecisionTreeClassifier classifier;
	// JSON gold objects
	private Tree treeFromJSON;
	private ArrayList<Rule> rulesFromJSON;


	/* *****
	 * Setup
	 * *****/
	@Before
	public void setup() {
		this.classifier = new DecisionTreeClassifier(this.maxDepth, this.minGain, this.sysOutputFile, this.modelFile);
	}

	private void setupTesting() {
		this.classifier.train(this.dataFile);
	}

	private void setupJSONRules() {
		try {
			this.rulesFromJSON = new ArrayList<Rule>();
			//// Open gold Rule objects from JSON
			// Convert JSON to Rule objects
			JSONTokener jsonTokener = new JSONTokener(new BufferedReader(new FileReader(this.rulesJSONFile)));
			JSONArray rulesJSON = (JSONArray) jsonTokener.nextValue();
			if (rulesJSON != null) {
				for (int i=0; i<rulesJSON.length(); i++) {
					this.rulesFromJSON.add(new Rule(rulesJSON.getJSONObject(i)));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void setupJSONTree() {
		this.setupTesting();
		try {
			// Open gold Tree object from JSON
			JSONTokener jsonTokener = new JSONTokener(new BufferedReader(new FileReader(this.treeJSONFile)));
			JSONObject json;
			if ((json = (JSONObject)jsonTokener.nextValue()) != null) {
				this.treeFromJSON = new Tree(json, this.classifier.getData());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/* ******************
	 * DTclassifier Tests
	 * ******************/

	@Test
	public void testDecisionTreeClassifierConstructorNullParametersThrowsNullException() {
		@SuppressWarnings("unused")
		DecisionTreeClassifier testClassifier;
		this.exception.expect(NullPointerException.class);
		testClassifier = new DecisionTreeClassifier(this.maxDepth, null, this.sysOutputFile, this.modelFile);
		this.exception.expect(NullPointerException.class);
		testClassifier = new DecisionTreeClassifier(this.maxDepth, this.minGain, null, this.modelFile);
		this.exception.expect(NullPointerException.class);
		testClassifier = new DecisionTreeClassifier(this.maxDepth, this.minGain, this.sysOutputFile, null);
	}

	@Test
	public void testDecisionTreeClassifierConstructorIllegalParameterThrowsException() {
		@SuppressWarnings("unused")
		DecisionTreeClassifier testClassifier;
		this.exception.expect(IllegalArgumentException.class);
		testClassifier = new DecisionTreeClassifier(-1, this.minGain, this.sysOutputFile, this.modelFile);
		this.exception.expect(IllegalArgumentException.class);
		testClassifier = new DecisionTreeClassifier(0, this.minGain, this.sysOutputFile, this.modelFile);
		this.exception.expect(IllegalArgumentException.class);
		testClassifier = new DecisionTreeClassifier(this.maxDepth, -0.1d, this.sysOutputFile, this.modelFile);
	}

//	@Test
//	public void testDecisionTreeClassifierConstructorMissingFile() {
//
//	}

	@Test
	public void testDecisionTreeClassifierTrain() {
		this.setupJSONTree();
		this.setupJSONRules();
		// Do training
		this.classifier.train(this.dataFile);
		// Test for tree
		assertTrue(this.classifier.getTree() != null);
		assertTrue(this.classifier.getTree().equals(this.treeFromJSON));
		// Test for rules
		assertTrue(this.classifier.getRules() != null);
		ArrayList<Rule> rules = this.classifier.getRules();
		Collections.sort(rules);
		assertTrue(rules.equals(this.rulesFromJSON));
	}

	@Test
	public void testDecisionTreeClassifierCalculateTreeTop() {
		fail();
	}

	@Test
	public void testDecisionTreeClassifierCalculateTreeRest() {
		fail();
	}

	@Test
	public void testDecisionTreeClassifierClassify() {
		this.setupTesting();
		// Classify data
		this.classifier.classify(this.classifier.getData());
		// Make sure each Document has a non-null system output
		for (Integer docID: this.classifier.getData().getIDs()) {
			if (this.classifier.getData().getDoc(docID).getSysOutput() == null) {
				fail();
			}
		}
		// Make sure documents classified properly given the gold tree
	}

	@Test
	public void testDecisionTreeClassifierClassifyNullDataThrowsException() {
		this.setupTesting();
		Data nullData = null;
		this.exception.expect(NullPointerException.class);
		this.classifier.classify(nullData);
	}

	@Test
	public void testDecisionTreeClassifierClassifyCallBeforeTrainThrowsException() {
		this.exception.expect(NullPointerException.class);
		this.classifier.classify(this.classifier.getData());
	}

	@Test
	public void testDecisionTreeClassifierTest() {
		fail();
	}

	@Test
	public void testDecisionTreeClassifierOutputResults() {
		fail();
	}

	@Test
	public void testDecisionTreeClassifierWriteModelFile() {
		fail();
	}

	@Test
	public void testDecisionTreeClassifierWriteSysOutput() {
		fail();
	}

	@Test
	public void testDecisionTreeClassifierGetData() {
		fail();
	}

}
