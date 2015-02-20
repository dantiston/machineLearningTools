package machineLearningTools;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class NestedDictionaryTest {

	// Contants
	private final String key1 = "key1";
	private final String key2 = "key2";
	private final String key3 = "key3";
	private final String key4 = "key4";
	private final String goldNestedDictString = "{key3={key4=1}, key1={key2=1}}";
	private final Integer incrementTo = 10;

	// Variables
	private NestedDictionary<String, Integer> stringToIntDict;
	private NestedDictionary<Integer, Integer> intToIntDict;
	private NestedDictionary<Document, Integer> docToIntDict;
	private NestedDictionary<String, String> stringToStringDict;
	private NestedDictionary<Integer, String> intToStringDict;
	private NestedDictionary<Document, String> docToStringDict;

	/* Setup */

	@Before
	public void setupNestedDictionary() {
		this.stringToIntDict = new NestedDictionary<String, Integer>();
		this.intToIntDict = new NestedDictionary<Integer, Integer>();
		this.docToIntDict = new NestedDictionary<Document, Integer>();
		this.stringToStringDict = new NestedDictionary<String, String>();
		this.intToStringDict = new NestedDictionary<Integer, String>();
		this.docToStringDict = new NestedDictionary<Document, String>();

		this.stringToIntDict.put(this.key1, this.key2, 1);
		this.stringToIntDict.put(this.key3, this.key4, 1);
	}


	/* Tests */

	// Constructors

	// Getters

	@Test
	public void testNestedDictionaryGetOneKey() {
		assertTrue(this.stringToIntDict.get(this.key1, this.key2).equals(1));
		assertTrue(this.stringToIntDict.get(this.key3, this.key4).equals(1));
	}

	@Test(expected=NullPointerException.class)
	public void testNestedDictionaryGetOneKeyNullThrowsException() {
		this.stringToIntDict.get(null, this.key2);
	}

	@Test(expected=NullPointerException.class)
	public void testNestedDictionaryGetOneKeyMissingThrowsException() {
		this.stringToIntDict.get("cheese", this.key2);
	}

	// Core methods

	@Test
	public void testNestedDictionaryToString() {
		assertTrue(this.stringToIntDict.toString().equals(this.goldNestedDictString));
	}

}
