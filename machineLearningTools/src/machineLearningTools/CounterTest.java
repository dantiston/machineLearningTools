package machineLearningTools;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

/**
 * @author T.J. Trimble
 *
 */
public class CounterTest {


	/* ************************
	 *  Counter tests
	 * ************************/

	// Contants
	private final String key1 = "key1";
	private final String key2 = "key2";
	private final String goldCounterString = "{key2=1, key1=1}";
	private final Integer incrementTo = 10;

	// Variables
	private Counter<String> counter;
	private Counter<Integer> intCounter;
	private Counter<Document> docCounter;

	/* Setup */

	/**
	 * Create new Counter object
	 */
	@Before
	public void setUpCounterTest() {
		this.counter = new Counter<String>();
	}

	/* Tests */
	//// Constructors
	// Basic Constructor

	@Test
	public void testCounterBasicConstructorFunctions() {
		this.counter = new Counter<String>();
		this.counter.increment(this.key1);
		assertTrue(this.counter.get(this.key1).equals(1));
	}

	// Collection Constructor

	/**
	 * Tests creating a Counter object from a collection
	 * Relies on testCounterKeySet()
	 */
	@Test
	public void testCounterConstructorWithCollection() {
		HashSet<String> collection = new HashSet<String>(Arrays.asList(new String[]{"com","your","cheaper"}));
		this.counter = new Counter<String>(collection);
		assertTrue(this.counter.keySet().equals(collection));
	}

	/**
	 * Test that Collection Counter constructor passed a null
	 * throws a NullPointerException error
	 */
	@Test(expected=NullPointerException.class)
	public void testCounterConstructorWithCollectionNullThrowsError() {
		Collection<String> collection = null;
		this.counter = new Counter<String>(collection);
	}

	@Test
	public void testCounterConstructorReturnsEmptyWithEmptyCollection() {
		HashSet<String> collection = new HashSet<String>();
		this.counter = new Counter<String>(collection);
		assertTrue(this.counter.size() == 0);
	}

	// Array Constructor

	/**
	 * Tests creating a Counter object from a String array
	 * Relies on testCounterKeySet()
	 */
	@Test
	public void testCounterConstructorWithTypeArray() {
		String[] collection = "I like to go to the store.".split("\\s+");
		HashSet<String> hashSet = new HashSet<String>(Arrays.asList(collection));
		this.counter = new Counter<String>(collection);
		assertTrue(this.counter.keySet().equals(hashSet));
	}

	@Test(expected=NullPointerException.class)
	public void testCounterConstructorWithArrayNullThrowsError() {
		String[] collection = null;
		this.counter = new Counter<String>(collection);
	}

	@Test
	public void testCounterConstructorReturnsEmptyWithEmptyArray() {
		String[] collection = new String[]{};
		this.counter = new Counter<String>(collection);
		assertTrue(this.counter.size() == 0);
	}

	//// Core Methods

	/**
	 * Test Counter get method initializes key to 0
	 */
	@Test
	public void testCounterGetEmpty() {
		// Test that value is not in Counter
		assertFalse(this.counter.containsKey(this.key1));
		// Get value
		assertThat(this.counter.get(this.key1), is(0));
	}

	/**
	 * Test Counter get method
	 */
	@Test
	public void testCounterGet() {
		// Test that value is not in Counter
		assertFalse(this.counter.containsKey(this.key1));
		// Increment value once
		this.counter.increment(this.key1);
		// Get value
		assertThat(this.counter.get(this.key1), is(1));
	}

	/**
	 * Basic test for Counter increment
	 */
	@Test
	public void testCounterIncrement() {
		for (int i=0; i<this.incrementTo; i++) {
			this.counter.increment(this.key1);
		}
		assertThat(this.counter.get(this.key1), is(this.incrementTo));
	}

	/**
	 * Basic test for Counter initialize
	 */
	@Test
	public void testCounterInitialize() {
		// Test that value is not in Counter
		assertFalse(this.counter.containsKey(this.key1));
		// Initialize
		this.counter.initialize(this.key1);
		// Test that value is now in Counter
		assertThat(this.counter.get(this.key1), is(0));
	}

	/**
	 * Test that reinitializing a Counter key resets it to 0
	 */
	@Test
	public void testCounterInitializeOverwrite() {
		// Increment key to 1
		this.counter.increment(this.key1);
		assertThat(this.counter.get(this.key1), is(1));
		// Reset key to 0
		this.counter.initialize(this.key1);
		assertThat(this.counter.get(this.key1), is(0));
	}

	/**
	 * Test Counter containsKey method
	 */
	@Test
	public void testCounterContainsKey() {
		// Make sure key is not in Counter
		assertFalse(this.counter.containsKey(this.key1));
		// Add key to Counter
		this.counter.increment(this.key1);
		// Test that key is now in Counter
		assertTrue(this.counter.containsKey(this.key1));
	}

	/**
	 * Test Counter keySet method
	 */
	@Test
	public void testCounterKeySet() {
		// Initialize target keySet
		Set<String> gold = new HashSet<String>();
		// Check if keySet is empty
		assertEquals(this.counter.keySet(), gold);
		// Add to sets
		this.counter.increment(this.key1);
		gold.add(this.key1);
		// Check if keySet is correct
		assertEquals(this.counter.keySet(), gold);
	}

	@Test
	public void testCounterIntKey() {
		this.intCounter = new Counter<Integer>();
		this.intCounter.increment(1);
		assertThat(this.intCounter.get(1), is(1));
	}

	@Test
	public void testCounterCustomKey() {
		BinaryValuedDocumentTest docTest = new BinaryValuedDocumentTest();
		docTest.setupDocument();
		this.docCounter = new Counter<Document>();
		this.docCounter.increment(docTest.goldDocument);
		assertThat(this.docCounter.get(docTest.goldDocument), is(1));
	}

	@Test
	public void testCounterToString() {
		assertTrue(this.counter.toString().equals("{}"));
		this.counter.increment(this.key1);
		this.counter.increment(this.key2);
		assertTrue(this.counter.toString().equals(this.goldCounterString));
	}

	@Test
	public void testCounterToStringEmpty() {
		assertTrue(this.counter.toString().equals("{}"));
	}

}
