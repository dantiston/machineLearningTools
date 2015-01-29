package machineLearningTools;

import java.util.HashMap;
import java.util.Set;

/**
 * NestedDictionary
 *
 * Two dimensional sparse matrix style Map. Uses hashed keys
 * for efficient lookup and insertion.
 *
 * Implements Map
 *
 * @author T.J. Trimble
 */
public class NestedDictionary<K, V> {
	private HashMap<K, HashMap<K, V>> values = new HashMap<K, HashMap<K, V>>();
	private HashMap<K, V> inner;

	/**
	 * Get value at position key1, key2
	 *
	 * @param key1
	 * @param key2
	 * @return
	 */
	public V get(K key1, K key2) {
		return this.values.get(key1).get(key2);
	}

	/**
	 * Get the inner HashMap associated with the key parameter
	 *
	 * @param key
	 * @return
	 */
	public HashMap<K, V> get(K key) {
		return this.values.get(key);
	}

	/**
	 * Return the non-null value at key1, key2, else return
	 * the defaultReturnValue parameter.
	 *
	 * @param key1
	 * @param key2
	 * @return
	 */
	public V safeGet(K key1, K key2, V defaultReturnValue) {
		if (this.hasValueAt(key1, key2)) {
			return this.get(key1, key2);
		}
		return defaultReturnValue;
	}

	/**
	 * Puts parameter value into NestedDictionary at position key1, key2.
	 *
	 * If key1 not in dictionary, adds key1, key2, and value to dictionary.
	 *
	 * @param key1
	 * @param key2
	 * @param value
	 */
	public void put(K key1, K key2, V value) {
		if (this.values.containsKey(key1)) {
			this.values.get(key1).put(key2, value);
		}
		else {
			this.inner = new HashMap<K, V>();
			this.inner.put(key2, value);
			this.values.put(key1, this.inner);
		}
	}

	/**
	 * Returns keySet of outer keys;
	 *
	 * @return
	 */
	public Set<K> outerKeySet() {
		return this.values.keySet();
	}

	/**
	 * Returns true iff there exists a value at key1->key2
	 *
	 * @param label
	 * @param feature
	 * @return
	 */
	public boolean hasValueAt(K key1, K key2) {
		if (this.values.containsKey(key1) && this.values.get(key1).containsKey(key2)) {
			return true;
		}
		return false;
	}

	// General methods

	@Override
	public String toString() {
		return this.values.toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		// Basic checks
		if (obj == this) {
			return true;
		}
		if (obj == null || (obj.getClass() != this.getClass())) {
			return false;
		}
		NestedDictionary<K, V> dict = null;
		try {
			dict = (NestedDictionary<K, V>)obj;
		}
		catch (ClassCastException e) {
			System.err.println("Failed at casting!");
			return false;
		}
		// Check outer keys
		if (!this.outerKeySet().equals(dict.outerKeySet())) {
			System.err.println("Failed at keySet!");
			return false;
		}
		// Check inner dictionaries
		for (K key: this.outerKeySet()) {
			if (!this.get(key).equals(dict.get(key))) {
				System.err.println(String.format("Failed at %s!", key));
				return false;
			}
		}
		return true;
	}
}
