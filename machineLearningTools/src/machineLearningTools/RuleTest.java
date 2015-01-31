package machineLearningTools;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Before;
import org.junit.Test;

public class RuleTest {

	//// Constants
	// Basic values
	private final Integer testDocCount = 5;
	private final Double testDocCountDouble = (double)this.testDocCount;

	private final String feat1 = "feat1";
	private final String feat2 = "feat2";
	private final String negfeat2 = "!feat2";
	private final String feat3 = "feat3";

	private final String label1 = "label1";
	private final String label2 = "label2";
	private final HashSet<String> goldLabels = new HashSet<String>(Arrays.asList(new String[]{this.label1, this.label2}));
	private final Double probability1 = 2.0d/this.testDocCountDouble;

	// Maximum values
	private final String maxLabel = this.label2;
	private final Double maxProbability = 3.0d/this.testDocCountDouble;

	// Gold values
	private final ArrayList<String> testPath1 = new ArrayList<String>(Arrays.asList(new String[]{this.feat1, this.feat2}));
	private final ArrayList<String> testPath2 = new ArrayList<String>(Arrays.asList(new String[]{this.feat1, this.negfeat2}));
	private final HashMap<String, Double> testProbabilities = this.makeProbabilities();

	private final HashSet<String> goldMatchFeatures1 = new HashSet<String>(Arrays.asList(new String[]{this.feat1, this.feat2, this.feat3}));
	private final HashSet<String> goldNoMatchFeatures1 = new HashSet<String>(Arrays.asList(new String[]{this.feat1, this.feat3}));

	private final String goldFormattedProbabilities = "label2 0.6 label1 0.4";
	private final String goldString1 = "feat1&feat2";
	private final String goldString2 = "feat1&!feat2";

	private final String goldTestRule1JSON = "{\"path\":[\"feat1\",\"feat2\"],\"docCount\":\"5\",\"probabilities\":{\"label1\":\"0.4\",\"label2\":\"0.6\"}}";

	private ArrayList<Rule> goldRuleList;

	// Variables
	private Rule testRule1;
	private Rule testRule2;
	private Rule goldRule1;
	private Rule goldRule2;
	private Rule goldRule3;

	private HashMap<String, Double> makeProbabilities() {
		HashMap<String, Double> result = new HashMap<String, Double>();
		result.put(this.label1, this.probability1);
		result.put(this.maxLabel, this.maxProbability);
		return result;
	}

	/* Setup */

	@Before
	public void setupRule() {
		this.testRule1 = new Rule(this.testPath1, this.testProbabilities, this.testDocCount);
		this.testRule2 = new Rule(this.testPath2, this.testProbabilities, this.testDocCount);

		this.goldRule1 = new Rule(this.testPath1, this.testProbabilities, this.testDocCount);
		this.goldRule2 = new Rule(this.testPath1, this.testProbabilities, this.testDocCount);
		this.goldRule3 = new Rule(this.testPath1, this.testProbabilities, this.testDocCount);
	}

	private void setupCompareTo() {
		this.goldRuleList = new ArrayList<Rule>();
		this.goldRuleList.add(this.testRule2);
		this.goldRuleList.add(this.testRule1);
	}

	/* Tests */

	// Constructor tests

	@Test
	public void testRuleConstructor() {
		// Test sets path, probabilities, and docCount
		assertTrue(this.testRule1.getPath().equals(this.testPath1));
		assertTrue(this.testRule1.getProbabilities().equals(this.testProbabilities));
		assertTrue(this.testRule1.getDocCount().equals(this.testDocCount));
		// Tests gets maximum label, probability
		assertTrue(this.testRule1.getLabel().equals(this.maxLabel));
		assertTrue(this.testRule1.getMaxProb().equals(this.maxProbability));
		// Tests adds to Rule.allLabels()
		assertTrue(Rule.getAllLabels().equals(this.goldLabels));
	}

	@Test
	public void testRuleConstructorFromJSON() {
		try {
			// Load JSON
			JSONTokener jsonTokener = new JSONTokener(this.goldTestRule1JSON);
			JSONObject testRule1JSON;
			testRule1JSON = (JSONObject)jsonTokener.nextValue();
			Rule testRule1FromJSON = new Rule(testRule1JSON);
			//// Test constructed Rule
			// Test sets path, probabilities, and docCount
			assertTrue(testRule1FromJSON.getPath().equals(this.testPath1));
			assertTrue(testRule1FromJSON.getProbabilities().equals(this.testProbabilities));
			assertTrue(testRule1FromJSON.getDocCount().equals(this.testDocCount));
			// Tests gets maximum label, probability
			assertTrue(testRule1FromJSON.getLabel().equals(this.maxLabel));
			assertTrue(testRule1FromJSON.getMaxProb().equals(this.maxProbability));
			// Tests adds to Rule.allLabels()
			assertTrue(Rule.getAllLabels().equals(this.goldLabels));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// Core functionality tests

	@Test
	public void testRuleAccepts() {
		assertTrue(this.testRule1.accepts(this.goldMatchFeatures1));
		assertFalse(this.testRule1.accepts(this.goldNoMatchFeatures1));
		assertTrue(this.testRule2.accepts(this.goldNoMatchFeatures1));
		assertFalse(this.testRule2.accepts(this.goldMatchFeatures1));
	}

	// Getter tests

	@Test
	public void testRuleGetFormattedProbabilities() {
		assertTrue(this.testRule1.getFormattedProbabilities().equals(this.goldFormattedProbabilities));
	}

	@Test
	public void testRuleGetLabel() {
		assertTrue(this.testRule1.getLabel().equals(this.maxLabel));
	}

	@Test
	public void testRuleGetProbabilities() {
		assertTrue(this.testRule1.getProbabilities().equals(this.testProbabilities));
	}

	@Test
	public void testRuleGetDocCount() {
		assertTrue(this.testRule1.getDocCount().equals(this.testDocCount));
	}

	@Test
	public void testRuleGetPath() {
		assertTrue(this.testRule1.getPath().equals(this.testPath1));
		assertTrue(this.testRule2.getPath().equals(this.testPath2));
	}

	// Precondition tests

	/**
	 * Tests that Rule constructor throws exception if any of
	 * its arguments are null
	 */
	@Test(expected=NullPointerException.class)
	public void testRuleNullPathThrows() {
		@SuppressWarnings("unused")
		Rule rule = new Rule(null, this.testProbabilities, this.testDocCount);

	}

	@Test(expected=NullPointerException.class)
	public void testRuleNullProbabilitiesThrows() {
		@SuppressWarnings("unused")
		Rule rule = new Rule(this.testPath1, null, this.testDocCount);
	}

	@Test(expected=NullPointerException.class)
	public void testRuleNullDocCountThrows() {
		@SuppressWarnings("unused")
		Rule rule = new Rule(this.testPath1, this.testProbabilities, null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testRuleEmptyPathThrowsIllegalArgument() {
		ArrayList<String> emptyRules = new ArrayList<String>();
		@SuppressWarnings("unused")
		Rule rule = new Rule(emptyRules, this.testProbabilities, this.testDocCount);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testRuleZeroDocCountThrowsIllegalArgument0() {
		@SuppressWarnings("unused")
		Rule rule = new Rule(this.testPath1, this.testProbabilities, 0);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testRuleZeroDocCountThrowsIllegalArgumentNegative() {
		@SuppressWarnings("unused")
		Rule rule = new Rule(this.testPath1, this.testProbabilities, -1);
	}

	/**
	 * Tests that Rule constructor throws exception if its probability
	 * map is empty
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testRuleEmptyProbabilityThrowsIllegalArgument() {
		HashMap<String, Double> emptyProbs = new HashMap<String, Double>();
		@SuppressWarnings("unused")
		Rule rule = new Rule(this.testPath1, emptyProbs, this.testDocCount);
	}

	/**
	 * Tests that Rule constructor throws exception if its probabilities
	 * don't sum to 1
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testRuleNot1ProbabilityThrowsIllegalArgument() {
		HashMap<String, Double> probs = new HashMap<String, Double>();
		probs.put(this.label1, 0.3d);
		probs.put(this.label2, 0.3d);
		@SuppressWarnings("unused")
		Rule rule = new Rule(this.testPath1, probs, this.testDocCount);
	}

	////Basic method tests

	// Equals

	@Test
	public void testRuleEqualsPositive() {
		assertTrue(this.goldRule1.equals(this.goldRule2));
	}

	@Test
	public void testRuleEqualsNegative() {
		assertFalse(this.testRule1.equals(this.testRule2));
	}

	@Test
	public void testRuleEqualsReflexive() {
		assertTrue(this.goldRule1.equals(this.goldRule1));
	}

	@Test
	public void testRuleEqualsSymmetric() {
		assertTrue(this.goldRule1.equals(this.goldRule2));
		assertTrue(this.goldRule2.equals(this.goldRule1));
	}

	@Test
	public void testRuleEqualsTransitivePositive() {
		assertTrue(this.goldRule1.equals(this.goldRule2));
		assertTrue(this.goldRule2.equals(this.goldRule3));
		assertTrue(this.goldRule3.equals(this.goldRule1));
	}

	@Test
	public void testRuleEqualsTransitiveNegative() {
		assertTrue(this.goldRule1.equals(this.goldRule2));
		assertFalse(this.goldRule2.equals(this.testRule2));
		assertFalse(this.testRule2.equals(this.goldRule3));
		assertTrue(this.goldRule3.equals(this.goldRule1));
	}

	@Test
	public void testRuleEqualsConsistent() {
		for (int i=0; i<3; i++) {
			if (!this.goldRule1.equals(this.goldRule2)) {
				fail();
			}
		}
	}

	@Test
	public void testRuleEqualsNull() {
		assertFalse(this.goldRule1.equals(null));
	}

	// toString

	@Test
	public void testRuleToString() {
		assertTrue(this.testRule1.toString().equals(this.goldString1));
		assertTrue(this.testRule2.toString().equals(this.goldString2));
	}

	@Test
	public void testRuleToJSON() {
		assertTrue(this.testRule1.toJSON().equals(this.goldTestRule1JSON));
	}

	// compareTo

	@Test
	public void testRuleCompareTo() {
		this.setupCompareTo();
		ArrayList<Rule> testRules = new ArrayList<Rule>();
		testRules.add(this.testRule1);
		testRules.add(this.testRule2);
		Collections.sort(testRules);
		assertTrue(testRules.equals(this.goldRuleList));
	}

	@Test
	public void testRuleCompareToOne() {
		this.setupCompareTo();
		ArrayList<Rule> testRules = new ArrayList<Rule>();
		testRules.add(this.testRule2);
		Collections.sort(testRules);
		assertTrue(testRules.equals(this.goldRuleList.subList(0, 1)));
	}

	@Test
	public void testRuleCompareToEmpty() {
		this.setupCompareTo();
		ArrayList<Rule> testRules = new ArrayList<Rule>();
		Collections.sort(testRules);
		assertTrue(testRules.equals(new ArrayList<Rule>()));
	}
}