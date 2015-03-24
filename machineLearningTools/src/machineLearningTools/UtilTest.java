package machineLearningTools;

import static machineLearningTools.Util.join;
import static machineLearningTools.Util.maxKeyByValue;
import static machineLearningTools.Util.minKeyByValue;
import static machineLearningTools.Util.sortedKeysByValue;
import static machineLearningTools.Util.sumValues;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class UtilTest {

	/* ***********
	 *  Universals
	 * ***********/

	// Constants
	private final int goldMax = 9;
	private final int goldMin = 0;
	private final String goldMaxKey = "9";
	private final String goldMinKey = "0";
	private final String testDocumentString = "testLabel word1:1 word2:1 word3:1";

	// Set up gold unstructured text
	private final String testUnstructuredText = "Go home. Go";
	private final HashMap<String, Integer> goldUnstructuredTextCounts = new HashMap<String, Integer>();
	private final String goldUnstructuredTextMax = "Go";
	private final String goldUnstructuredTextMin = "home.";
	private final ArrayList<String> goldUnstructuredTextOrderForward = new ArrayList<String>(Arrays.asList(new String[]{"Go", "home."}));
	private final ArrayList<String> goldUnstructuredTextOrderReverse = new ArrayList<String>(Arrays.asList(new String[]{"home.", "Go"}));

	// Set up gold values for sumValues
	private HashMap<String, Double> testSumValuesMapDouble;
	private final double gold1 = 1.0d;
	private final double goldSum = 2.0d;

	/* ************************
	 *  sortedKeysByValue tests
	 * ************************/

	// Variables
	private HashMap<String, String> stringMap;
	private HashMap<String, Integer> integerMap;
	private HashMap<String, Document> docMap;
	private ArrayList<String> goldOrderForward;
	private ArrayList<String> goldOrderReverse;

	/* Setup */

	/**
	 * Set up basic values for tests
	 */
	@Before
	public void setUpSortedKeysByValue() {
		// Initialize goldOrder
		this.goldOrderForward = new ArrayList<String>();
		// Add values to goldOrder
		for (Integer i=0; i<10; i++) {
			this.goldOrderForward.add(i.toString());
		}
		// Create gold reversed list
		this.goldOrderReverse = new ArrayList<String>(this.goldOrderForward);
		Collections.sort(this.goldOrderReverse, Collections.reverseOrder());
		// Initialize Maps
		this.stringMap = new HashMap<String, String>();
		this.integerMap = new HashMap<String, Integer>();
		this.docMap = new HashMap<String, Document>();
		Document.initialize();
		// Add values to maps
		for (Integer i=this.goldMin; i<=this.goldMax; i++) {
			this.stringMap.put(i.toString(), i.toString());
			this.integerMap.put(i.toString(), i);
			this.docMap.put(i.toString(), new RealValuedDocument(this.testDocumentString));
		}
		// Set up gold unstructured text counts
		this.goldUnstructuredTextCounts.put("Go", 2);
		this.goldUnstructuredTextCounts.put("home.", 1);
	}

	/* Tests */

	/**
	 * Test that sortedKeysByValue() throws NullPointerException
	 */
	@Test(expected=NullPointerException.class)
	public void testSortedKeysByValueNull() {
		Map<String, Integer> nullMap = null;
		sortedKeysByValue(nullMap);
	}

	/**
	 * Test that sortedKeysByValue returns empty list if given empty map
	 */
	@Test
	public void testSortedKeysByValueEmpty() {
		HashMap<String, Integer> emptyMap = new HashMap<String, Integer>();
		ArrayList<String> emptyList = new ArrayList<String>();
		assertEquals(sortedKeysByValue(emptyMap), emptyList);
	}

	/**
	 * Test that sortedKeysByValue returns empty list if given empty map
	 */
	@Test
	public void testSortedKeysByValueOnlyOneKey() {
		HashMap<String, Integer> oneMap = new HashMap<String, Integer>();
		oneMap.put(this.goldMaxKey, 1);
		ArrayList<String> oneList = new ArrayList<String>();
		oneList.add(this.goldMaxKey);
		assertEquals(sortedKeysByValue(oneMap), oneList);
	}

	/**
	 * Test sortedKeysByValue on string values with regular direction
	 */
	@Test
	public void testSortedKeysByValueStringForward() {
		assertEquals(sortedKeysByValue(this.stringMap), this.goldOrderForward);
	}

	/**
	 * Test sortedKeysByValue on string values with reverse direction
	 */
	@Test
	public void testSortedKeysByValueStringReverse() {
		assertEquals(sortedKeysByValue(this.stringMap, true), this.goldOrderReverse);
	}

	/**
	 * Test sortedKeysByValue on integer values with regular direction
	 */
	@Test
	public void testSortedKeysByValueIntegerForward() {
		assertEquals(sortedKeysByValue(this.integerMap), this.goldOrderForward);
	}

	/**
	 * Test sortedKeysByValue on integer values with reverse direction
	 */
	@Test
	public void testSortedKeysByValueIntegerReverse() {
		assertEquals(sortedKeysByValue(this.integerMap, true), this.goldOrderReverse);
	}

	/**
	 * Test sortedKeysByValue on TokenCounter in forward direction
	 */
	@Test
	public void testSortedKeysByValueCounterForward() {
		TokenCounter counter = new TokenCounter(this.testUnstructuredText);
		assertTrue(counter.equals(this.goldUnstructuredTextCounts));
		assertTrue(sortedKeysByValue(counter, true).equals(this.goldUnstructuredTextOrderForward));
	}

	/**
	 * Test sortedKeysByValue on TokenCounter in reverse direction
	 */
	@Test
	public void testSortedKeysByValueCounterReverse() {
		TokenCounter counter = new TokenCounter(this.testUnstructuredText);
		assertTrue(counter.equals(this.goldUnstructuredTextCounts));
		assertTrue(sortedKeysByValue(counter).equals(this.goldUnstructuredTextOrderReverse));
	}

	/* ********************
	 *  maxKeyByValue tests
	 * ********************/

	@Test
	public void testMaxKeyByValueInteger() {
		assertTrue(maxKeyByValue(this.integerMap).equals(this.goldMaxKey));
	}

	@Test
	public void testMaxKeyByValueString() {
		assertTrue(maxKeyByValue(this.stringMap).equals(this.goldMaxKey));
	}

	@Test
	public void testMaxKeyByValueOther() {
		assertTrue(maxKeyByValue(this.docMap).equals(this.goldMaxKey));
	}

	/**
	 * Test that maxKeyByValue() throws NullPointerException
	 */
	@Test(expected=NullPointerException.class)
	public void testMaxKeyByValueNull() {
		maxKeyByValue(null);
	}

	/**
	 * Test that maxKeyByValue() works with TokenCounter class
	 */
	@Test
	public void testMaxKeyByValueCounter() {
		TokenCounter counter = new TokenCounter(this.testUnstructuredText);
		assertEquals(maxKeyByValue(counter), this.goldUnstructuredTextMax);
	}

	/**
	 * Test that maxKeysByValue returns null if given empty map
	 */
	@Test
	public void testMaxKeysByValueEmpty() {
		HashMap<String, Integer> emptyMap = new HashMap<String, Integer>();
		assertEquals(maxKeyByValue(emptyMap), null);
	}

	/**
	 * Test that maxKeysByValue returns empty list if given empty map
	 */
	@Test
	public void testMaxKeysByValueOnlyOneKey() {
		HashMap<String, Integer> oneMap = new HashMap<String, Integer>();
		oneMap.put(this.goldMaxKey, 1);
		assertEquals(maxKeyByValue(oneMap), this.goldMaxKey);
	}

	/* ********************
	 *  minKeyByValue tests
	 * ********************/

	@Test
	public void testMinKeyByValueInteger() {
		assertTrue(minKeyByValue(this.integerMap).equals(this.goldMinKey));
	}

	@Test
	public void testMinKeyByValueString() {
		assertTrue(minKeyByValue(this.stringMap).equals(this.goldMinKey));
	}

	@Test
	public void testMinKeyByValueOther() {
		assertTrue(minKeyByValue(this.docMap).equals(this.goldMinKey));
	}

	/**
	 * Test that minKeyByValue() throws NullPointerException
	 */
	@Test(expected=NullPointerException.class)
	public void testMinKeyByValueNull() {
		minKeyByValue(null);
	}

	/**
	 * Test that minKeyByValue() works with TokenCounter class
	 */
	@Test
	public void testMinKeyByValueCounter() {
		TokenCounter counter = new TokenCounter(this.testUnstructuredText);
		assertEquals(minKeyByValue(counter), this.goldUnstructuredTextMin);
	}

	/**
	 * Test that minKeysByValue returns empty list if given empty map
	 */
	@Test
	public void testMinKeysByValueEmpty() {
		HashMap<String, Integer> emptyMap = new HashMap<String, Integer>();
		assertEquals(minKeyByValue(emptyMap), null);
	}

	/**
	 * Test that minKeysByValue returns empty list if given empty map
	 */
	@Test
	public void testMinKeysByValueOnlyOneKey() {
		HashMap<String, Integer> oneMap = new HashMap<String, Integer>();
		oneMap.put(this.goldMaxKey, 1);
		assertEquals(minKeyByValue(oneMap), this.goldMaxKey);
	}

	/* ****************
	 *  sumValues tests
	 * ****************/

	@Test
	public void testSumValuesDouble() {
		this.testSumValuesMapDouble = new HashMap<String, Double>();
		this.testSumValuesMapDouble.put("key1", this.gold1);
		this.testSumValuesMapDouble.put("key2", this.gold1);
		assertTrue(sumValues(this.testSumValuesMapDouble) == this.goldSum);
	}

	@Test
	public void testSumValuesInteger() {
		HashMap<String, Integer> testSumValuesMapInteger = new HashMap<String, Integer>();
		testSumValuesMapInteger.put("key1", 1);
		testSumValuesMapInteger.put("key2", 1);
		assertTrue(sumValues(testSumValuesMapInteger) == 2);
	}

	@Test
	public void testSumValuesFloat() {
		HashMap<String, Float> testSumValuesMapFloat = new HashMap<String, Float>();
		testSumValuesMapFloat.put("key1", 1.0f);
		testSumValuesMapFloat.put("key2", 1.0f);
		assertTrue(sumValues(testSumValuesMapFloat) == 2.0f);
	}

	@Test
	public void testSumValuesEmpty() {
		this.testSumValuesMapDouble = new HashMap<String, Double>();
		assertThat(sumValues(this.testSumValuesMapDouble), is(0.0d));
	}

	@Test
	public void testSumValuesOne() {
		this.testSumValuesMapDouble = new HashMap<String, Double>();
		this.testSumValuesMapDouble.put("key1", this.gold1);
		assertTrue(sumValues(this.testSumValuesMapDouble) == this.gold1);
	}

	/* **********
	 * join tests
	 * **********/

	private final String[] goldJoinInputArray = new String[]{"1","2","3"};
	private final List<String> goldJoinInputList = Arrays.asList(this.goldJoinInputArray);
	private final String goldJoinSeparator = ",";
	private final String goldJoinResult0 = "";
	private final String goldJoinResult1 = "1";
	private final String goldJoinResult2 = "1,2";
	private final String goldJoinResult3 = "1,2,3";
	private final String goldJoinResultNoSeparator2 = "12";
	private final String goldJoinResultNoSeparator3 = "123";

	// With separator

	@Test
	public void testJoinWithSeparatorThree() {
		assertTrue(join(this.goldJoinInputList, this.goldJoinSeparator).equals(this.goldJoinResult3));
	}

	@Test
	public void testJoinWithSeparatorTwo() {
		assertTrue(join(this.goldJoinInputList.subList(0, 2), this.goldJoinSeparator).equals(this.goldJoinResult2));
	}

	@Test
	public void testJoinWithSeparatorOne() {
		assertTrue(join(this.goldJoinInputList.subList(0, 1), this.goldJoinSeparator).equals(this.goldJoinResult1));
	}

	@Test
	public void testJoinWithSeparatorEmpty() {
		assertTrue(join(this.goldJoinInputList.subList(0, 0), this.goldJoinSeparator).equals(this.goldJoinResult0));
	}

	@Test(expected=NullPointerException.class)
	public void testJoinWithSeparatorNullValuesThrowsNull() {
		ArrayList<String> nullList = null;
		join(nullList, this.goldJoinSeparator);
	}

	@Test(expected=NullPointerException.class)
	public void testJoinWithNullSeparatorThrowsNull() {
		String nullString = null;
		join(this.goldJoinInputList, nullString);
	}

	// Without Separator

	@Test
	public void testJoinWithOutSeparatorThree() {
		assertTrue(join(this.goldJoinInputList).equals(this.goldJoinResultNoSeparator3));
	}

	@Test
	public void testJoinWithOutSeparatorTwo() {
		assertTrue(join(this.goldJoinInputList.subList(0, 2)).equals(this.goldJoinResultNoSeparator2));
	}

	@Test
	public void testJoinWithOutSeparatorOne() {
		assertTrue(join(this.goldJoinInputList.subList(0, 1)).equals(this.goldJoinResult1));
	}

	@Test
	public void testJoinWithOutSeparatorEmpty() {
		assertTrue(join(this.goldJoinInputList.subList(0, 0)).equals(this.goldJoinResult0));
	}

	@Test(expected=NullPointerException.class)
	public void testJoinWithOutSeparatorNullValuesThrowsNull() {
		ArrayList<String> nullList = null;
		join(nullList);
	}

	// Shortcut tests

	@Test
	public void testJoinArrayWithSeparator() {
		assertTrue(join(this.goldJoinInputArray, this.goldJoinSeparator).equals(this.goldJoinResult3));
	}

	@Test
	public void testJoinArrayWithOutSeparator() {
		assertTrue(join(this.goldJoinInputArray).equals(this.goldJoinResultNoSeparator3));
	}
}
