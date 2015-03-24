package machineLearningTools;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class NestedCounterTest {

	// Contants
	private final String key1 = "key1";
	private final String key2 = "key2";
	private final String key3 = "key3";
	private final String key4 = "key4";
	private final String goldCounterString = "{key3={key4=1}, key1={key2=1}}";
	private final Integer incrementTo = 10;

	// Variables
	private NestedCounter<String> counter;
	private NestedCounter<Integer> intCounter;
	private NestedCounter<Document> docCounter;

	/* Setup */

	/**
	 * Create new Counter object
	 */
	@Before
	public void setUpNestedCounterTest() {
		this.counter = new NestedCounter<String>();
	}

	/* Tests */
	//// Constructors
	// Basic Constructor

	@Test
	public void testNestedCounterBasicConstructor() {
		this.counter = new NestedCounter<String>();
		this.counter.increment(this.key1, this.key2);
		assertTrue(this.counter.get(this.key1, this.key2).equals(1));
	}

	//// Core Methods
	/**
	 * Test Counter get method initializes key to 0
	 */
	@Test
	public void testNestedCounterGetEmpty() {
		// Test that value is not in Counter
		assertFalse(this.counter.containsValueAt(this.key1, this.key2));
		// Get value
		assertThat(this.counter.get(this.key1, this.key2), is(0));
	}

	/**
	 * Test Counter get method
	 */
	@Test
	public void testNestedCounterGet() {
		// Test that value is not in Counter
		assertFalse(this.counter.containsValueAt(this.key1, this.key2));
		// Increment value once
		this.counter.increment(this.key1, this.key2);
		// Get value
		assertThat(this.counter.get(this.key1, this.key2), is(1));
	}

	/**
	 * Basic test for NestedCounter increment
	 */
	@Test
	public void testNestedCounterIncrement() {
		for (int i=0; i<this.incrementTo; i++) {
			this.counter.increment(this.key1, this.key2);
		}
		assertThat(this.counter.get(this.key1, this.key2), is(this.incrementTo));
	}

	/**
	 * Basic test for NestedCounter initialize
	 */
	@Test
	public void testNestedCounterInitialize() {
		// Test that value is not in Counter
		assertFalse(this.counter.containsValueAt(this.key1, this.key2));
		// Initialize
		this.counter.initialize(this.key1, this.key2);
		// Test that value is now in Counter
		assertThat(this.counter.get(this.key1, this.key2), is(0));
	}

	/**
	 * Test that reinitializing a NestedCounter key resets it to 0
	 */
	@Test
	public void testNestedCounterInitializeOverwrite() {
		// Increment key to 1
		this.counter.increment(this.key1, this.key2);
		assertThat(this.counter.get(this.key1, this.key2), is(1));
		// Reset key to 0
		this.counter.initialize(this.key1, this.key2);
		assertThat(this.counter.get(this.key1, this.key2), is(0));
	}

	/**
	 * Test NestedCounter containsValueAt method
	 */
	@Test
	public void testNestedCounterContainsValueAt() {
		// Make sure key is not in Counter
		assertFalse(this.counter.containsValueAt(this.key1, this.key2));
		// Add key to Counter
		this.counter.increment(this.key1, this.key2);
		// Test that key is now in Counter
		assertTrue(this.counter.containsValueAt(this.key1, this.key2));
	}

	/**
	 * Test NestedCounter keySet method
	 */
	@Test
	public void testNestedCounterKeySet() {
		// Initialize target keySet
		Set<String> gold = new HashSet<String>();
		// Check if keySet is empty
		assertEquals(this.counter.keySet(), gold);
		// Add to sets
		this.counter.increment(this.key1, this.key2);
		gold.add(this.key1);
		// Check if keySet is correct
		assertEquals(this.counter.keySet(), gold);
	}

	@Test
	public void testNestedCounterIntKey() {
		this.intCounter = new NestedCounter<Integer>();
		this.intCounter.increment(1, 2);
		assertThat(this.intCounter.get(1, 2), is(1));
	}

	@Test
	public void testNestedCounterCustomKey() {
		BinaryValuedDocumentTest docTest = new BinaryValuedDocumentTest();
		docTest.setupDocument();
		Document doc2 = new RealValuedDocument("This is a test document.", true);
		this.docCounter = new NestedCounter<Document>();
		this.docCounter.increment(docTest.goldDocument, doc2);
		assertThat(this.docCounter.get(docTest.goldDocument, doc2), is(1));
	}

	@Test
	public void testNestedCounterToString() {
		assertTrue(this.counter.toString().equals("{}"));
		this.counter.increment(this.key1, this.key2);
		this.counter.increment(this.key3, this.key4);
		assertTrue(this.counter.toString().equals(this.goldCounterString));
	}

	@Test
	public void testNestedCounterToStringEmpty() {
		assertTrue(this.counter.toString().equals("{}"));
	}
}
