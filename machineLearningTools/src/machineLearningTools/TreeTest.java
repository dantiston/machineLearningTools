package machineLearningTools;

import static machineLearningTools.Testing.testFile;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Before;
import org.junit.Test;

public class TreeTest {

	// Constants
	private final DataTest test = new DataTest();
	private final Set<Integer> subsetDocIDs = new HashSet<Integer>(Arrays.asList(new Integer[]{1, 2, 3, 4, 5}));
	private final String testFeature = "testFeature";
	private final String goldOtherTreeString = "{\"feature\":\"testFeature\",\"docIDs\":[\"1\",\"2\",\"3\",\"4\",\"5\"],\"children\":[]}";
	private final String goldGoldTreeString = "{\"feature\":\"*TOP*\",\"docIDs\":[\"0\",\"1\",\"2\",\"3\",\"4\",\"5\",\"6\",\"7\",\"8\",\"9\",\"10\",\"11\",\"12\",\"13\",\"14\",\"15\",\"17\",\"16\",\"19\",\"18\",\"21\",\"20\",\"23\",\"22\",\"25\",\"24\",\"27\",\"26\",\"29\",\"28\"],\"children\":[]}";
	private final Double totalTestDocs = 5.0d;

	private final String goldTreeJSONFile = testFile("goldTree.json");

	// Variables
	private Tree goldTree;
	private Tree goldTree2;
	private Tree goldTree3;
	private Tree otherTree;
	private Data goldData;
	private Tree tree;
	private HashMap<String, Double> testProbabilities;
	private List<String> testPath;
	private Tree goldTreeFromJSON;

	/* Setup */
	@Before
	public void setupTreeTest() {
		this.test.setupData();
		this.goldData = this.test.test.goldData;
		this.goldTree = new Tree(this.goldData);
		this.goldTree2 = new Tree(this.goldData);
		this.goldTree3 = new Tree(this.goldData);
		this.otherTree = new Tree(this.testFeature, this.subsetDocIDs, 1, this.goldData);
	}

	private void setupRule() {
		this.testProbabilities = new HashMap<String, Double>();
		this.testProbabilities.put("talk.politics.guns", 1.0d);
		this.testPath = Arrays.asList(new String[]{this.testFeature});
	}

	private void setupTreeJSON() {
		try {
			JSONObject json;
			JSONTokener jsonTokener = new JSONTokener(new BufferedReader(new FileReader(this.goldTreeJSONFile)));
			if ((json = (JSONObject) jsonTokener.nextValue()) != null) {
				this.goldTreeFromJSON = new Tree(json, this.goldData);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/* Tests */
	//// Constructors
	/**
	 * Also tests equals()
	 */
	@Test
	public void testTreeConstructorRoot() {
		this.tree = new Tree(this.goldData);
		assertTrue(this.tree.equals(this.goldTree));
		assertTrue(this.tree.getParent() == null);
	}

	/**
	 * Relies on getParent()
	 */
	@Test
	public void testTreeConstructorChild() {
		this.tree = new Tree(this.testFeature, this.subsetDocIDs, 1, this.goldData);
		assertTrue(this.tree.equals(this.otherTree));
		assertTrue(this.tree.getParent() == null);
	}

	/**
	 * Relies on toJSON();
	 */
	@Test
	public void testTreeConstructorJSON() {
		// Make Tree object from JSON
		this.setupTreeJSON();
		// Make gold Tree object
		Tree testTree = new Tree(this.goldData);
		Tree testTreeWith = new Tree("gun", new HashSet<Integer>(Arrays.asList(new Integer[]{0,1,2,3,4,5,6,7})), 1, this.goldData);
		Tree testTreeWithOut = new Tree("!gun", new HashSet<Integer>(Arrays.asList(new Integer[]{8,9,10,11,12,13,14,15,17,16,19,18,21,20,23,22,25,24,27,26,29,28})), 1, this.goldData);
		testTree.addChild(testTreeWith);
		testTree.addChild(testTreeWithOut);
		// Compare
		assertTrue(testTree.equals(this.goldTreeFromJSON));
	}

	// Null checks

	@Test(expected=NullPointerException.class)
	public void testTreeRootConstructorNullThrowsError() {
		this.tree = new Tree(null);
	}

	@Test(expected=NullPointerException.class)
	public void testTreeChildConstructorNullFeatureThrowsError() {
		this.tree = new Tree(null, this.subsetDocIDs, 1, this.goldData);
	}

	@Test(expected=NullPointerException.class)
	public void testTreeChildConstructorNullDocIDsThrowsError() {
		this.tree = new Tree(this.testFeature, null, 1, this.goldData);
	}

	@Test(expected=NullPointerException.class)
	public void testTreeChildConstructorNullDataThrowsError() {
		this.tree = new Tree(this.testFeature, this.subsetDocIDs, 1, null);
	}

	//// Core methods
	@Test
	public void testTreeAddChildToTop() {
		this.tree = new Tree(this.goldData);
		assertTrue(this.tree.getChildren().size() == 0);
		assertTrue(this.tree.getParent() == null);
		Tree child = new Tree(this.testFeature, this.subsetDocIDs, 1, this.goldData);
		assertTrue(child.getParent() == null);
		this.tree.addChild(child);
		assertTrue(child.getParent().equals(this.tree));
		assertTrue(this.tree.getChildren().size() == 1);
		assertTrue(this.tree.getChildren().get(0).equals(child));
	}

	@Test
	public void testTreeAddChildToChild() {
		this.tree = new Tree(this.testFeature, this.subsetDocIDs, 1, this.goldData);
		assertTrue(this.tree.getChildren().size() == 0);
		assertTrue(this.tree.getParent() == null);
		Tree child = new Tree(this.testFeature, this.subsetDocIDs, 1, this.goldData);
		assertTrue(child.getParent() == null);
		this.tree.addChild(child);
		assertTrue(child.getParent().equals(this.tree));
		assertTrue(this.tree.getChildren().size() == 1);
		assertTrue(this.tree.getChildren().get(0).equals(child));
	}

	// Getters

	/**
	 * Relies on addChild()
	 */
	@Test
	public void testTreeGetRules() {
		this.setupRule();
		this.goldTree.addChild(this.otherTree);
		Rule goldRule = new Rule(this.testPath, this.testProbabilities, this.totalTestDocs.intValue());
		ArrayList<Rule> goldList = new ArrayList<Rule>(Arrays.asList(new Rule[]{goldRule}));
		assertTrue(this.goldTree.getRules().get(0).equals(goldList.get(0)));
	}

	@Test
	public void testTreeGetRulesEmpty() {
		assertTrue(this.goldTree.getRules().equals(new ArrayList<Rule>()));
	}

	/**
	 * Relies on addChild()
	 */
	@Test
	public void testTreeGetParent() {
		this.tree = new Tree(this.goldData);
		Tree child = new Tree(this.testFeature, this.subsetDocIDs, 1, this.goldData);
		this.tree.addChild(child);
		assertTrue(child.getParent().equals(this.tree));
	}

	/**
	 * Relies on addChild()
	 */
	@Test
	public void testTreeGetChildren() {
		this.tree = new Tree(this.goldData);
		Tree child = new Tree(this.testFeature, this.subsetDocIDs, 1, this.goldData);
		assertTrue(this.tree.getChildren().size() == 0);
		this.tree.addChild(child);
		assertTrue(this.tree.getChildren().size() == 1);
		assertTrue(this.tree.getChildren().get(0).equals(child));
	}

	@Test
	public void testTreeGetFeature() {
		Tree child = new Tree(this.testFeature, this.subsetDocIDs, 1, this.goldData);
		assertTrue(child.getFeature().equals(this.testFeature));
	}

	@Test
	public void testTreeGetDocIDs() {
		this.tree = new Tree(this.testFeature, this.subsetDocIDs, 1, this.goldData);
		assertTrue(this.tree.getDocIDs().equals(this.subsetDocIDs));
	}

	// Setters

	@Test
	public void testTreeSetParent() {
		this.tree = new Tree(this.testFeature, this.subsetDocIDs, 1, this.goldData);
		this.tree.setParent(this.goldTree);
		assertTrue(this.tree.getParent().equals(this.goldTree));
	}

	//// Basic methods
	// Equals tests
	@Test
	public void testTreeEqualsNegative() {
		assertFalse(this.goldTree.equals(this.otherTree));
	}

	@Test
	public void testTreeEqualsTransitivePositive() {
		assertTrue(this.goldTree.equals(this.goldTree2));
		assertTrue(this.goldTree2.equals(this.goldTree3));
		assertTrue(this.goldTree3.equals(this.goldTree));
	}

	@Test
	public void testTreeEqualsTransitiveNegative() {
		assertTrue(this.goldTree.equals(this.goldTree2));
		assertFalse(this.goldTree2.equals(this.otherTree));
		assertFalse(this.otherTree.equals(this.goldTree3));
		assertTrue(this.goldTree3.equals(this.goldTree));
	}

	@Test
	public void testTreeEqualsReflexive() {
		assertTrue(this.goldTree.equals(this.goldTree));
	}

	@Test
	public void testTreeEqualsSymmetric() {
		assertTrue(this.goldTree.equals(this.goldTree2));
		assertTrue(this.goldTree2.equals(this.goldTree));
	}

	@Test
	public void testTreeEqualsSymmetricConsistent() {
		for (int i=0; i<3; i++) {
			if (!this.goldTree.equals(this.goldTree2)) {
				fail();
			}
		}
	}

	// toString()

	@Test
	public void testTreeToString() {
		assertTrue(this.otherTree.toString().equals(this.goldOtherTreeString));
		assertTrue(this.goldTree.toString().equals(this.goldGoldTreeString));
	}
}
