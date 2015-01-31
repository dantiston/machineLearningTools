package machineLearningTools;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

public class TokenCounterTest {

	// Constants
	private final String testString = "I like to go to the store. I like to go to the store. I like to go to the store.";
	private final String testKey = "go";
	private final Integer testKeyGoldCount = 3;
	private final String goldTokenCounterString = "{to=6, the=3, like=3, I=3, store.=3, go=3}";

	// Variables
	private HashMap<String, Integer> goldMap;
	private HashMap<String, Integer> goldMap2;

	@Before
	public void setupTokenCounter() {
		this.goldMap = new HashMap<String, Integer>();
		this.goldMap.put("to", 6);
		this.goldMap.put("the", 3);
		this.goldMap.put("like", 3);
		this.goldMap.put("I", 3);
		this.goldMap.put("store.", 3);
		this.goldMap.put("go", 3);
		this.goldMap2 = new HashMap<String, Integer>(this.goldMap);
		this.goldMap2.put("go", 4);
	}

	/**
	 * Tests creating a Counter object from a string
	 */
	@Test
	public void testTokenCounterConstructor() {
		TokenCounter testCounter = new TokenCounter(this.testString);
		assertTrue(testCounter.equals(this.goldMap));
	}

	@Test(expected=NullPointerException.class)
	public void testTokenCounterConstructorNull() {
		String testString = null;
		@SuppressWarnings("unused")
		TokenCounter counter = new TokenCounter(testString);
	}

	@Test
	public void testTokenCounterConstructorEmpty() {
		String testString = "";
		TokenCounter counter = new TokenCounter(testString);
		assertTrue(counter.keySet().size() == 0);
	}

	@Test
	public void testTokenCounterConstructorOne() {
		String testString = "hello";
		TokenCounter counter = new TokenCounter(testString);
		assertTrue(counter.keySet().size() == 1);
		assertTrue(counter.get(testString) == 1);
	}

	@Test
	public void testTokenCounterGet() {
		TokenCounter testCounter = new TokenCounter(this.testString);
		assertTrue(testCounter.get(this.testKey).equals(this.testKeyGoldCount));
	}

	@Test
	public void testTokenCounterIncrement() {
		TokenCounter testCounter = new TokenCounter(this.testString);
		assertTrue(testCounter.equals(this.goldMap));
		testCounter.increment(this.testKey);
		assertTrue(testCounter.equals(this.goldMap2));
	}

	@Test
	public void testTokenCounterInitialize() {
		TokenCounter testCounter = new TokenCounter(this.testString);
		assertTrue(testCounter.equals(this.goldMap));
		testCounter.initialize(this.testKey);
		assertTrue(testCounter.get(this.testKey).equals(0));
	}

	/**
	 * Might fail on different systems? Depends on hashCode tie breaking
	 */
	@Test
	public void testCounterToString() {
		TokenCounter testCounter = new TokenCounter(this.testString);
		assertTrue(testCounter.toString().equals(this.goldTokenCounterString));
	}
}
