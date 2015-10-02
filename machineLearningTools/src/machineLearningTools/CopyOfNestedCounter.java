package machineLearningTools;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * A 2 by 2 matrix for integer counting. Automatically adds keys
 * in the case that
 *
 * @param <T> key type
 *
 * @author T.J. Trimble
 */
public class CopyOfNestedCounter<T> extends AbstractCounter<T> {
	private HashMap<T, Counter<T>> values = new HashMap<T, Counter<T>>();
	private HashSet<T> allKeys = new HashSet<T>();
	private Counter<T> inner;

	// Core methods

	/**
	 * Increment the value at key1, key2 by 1.<br>
	 * If value does not exist at key1, key2, initialize a value to 1.
	 *
	 * @param key1
	 * @param key2
	 */
	public void increment(final T key1, final T key2) {
		try {
			this.values.get(key1).increment(key2);
		}
		catch (NullPointerException e) {
			this.inner = new Counter<T>();
			this.inner.increment(key2);
			this.values.put(key1, this.inner);
			// Only do this if it is known that values doesn't contain key1
			this.allKeys.add(key1);
		}
		this.allKeys.add(key2);
	}

	/**
	 * Initialize the value at key1, key2 to 0.<br>
	 * If value exists at key1, key2, reset to 0.
	 *
	 * @param key1
	 * @param key2
	 */
	public void initialize(final T key1, final T key2) {
		try {
			this.values.get(key1).initialize(key2);
		}
		catch (NullPointerException e) {
			this.inner = new Counter<T>();
			this.inner.initialize(key2);
			this.values.put(key1, this.inner);
			// Only do this if it is known that values doesn't contain key1
			this.allKeys.add(key1);
		}
		this.allKeys.add(key2);
	}

	/**
	 * Get the value at the specified key1->key2 mapping, <br>
	 * initializing a 0 value at key1->key2 if no value exists
	 *
	 * @param key1
	 * @param key2
	 * @return
	 */
	public Integer get(final T key1, final T key2) {
		// relies on initialize()
		// Check if Counter contains key1->key2 mapping
		try {
			return this.values.get(key1).get(key2);
		}
		catch (NullPointerException e) {
			// Counter does not contain key1->key2 mapping, initialize
			this.initialize(key1, key2);
		}
		return this.values.get(key1).get(key2);
	}

	// Other core methods
	/**
	 * Returns true iff key1->key2 mapping exists
	 *
	 * @param key1
	 * @param key2
	 * @return
	 */
	public boolean containsValueAt(final T key1, final T key2) {
		if (this.values.containsKey(key1) && this.values.get(key1).containsKey(key2)) {
			return true;
		}
		return false;
	}

	/**
	 * Returns true if key is either a key1 or key2
	 *
	 * @param key1
	 * @param key2
	 * @return
	 */
	@Override
	public boolean containsKey(Object key) {
		return this.allKeys.contains(key);
	}

	// Getters
	protected HashSet<T> getAllKeys() {
		return this.allKeys;
	}

	public Set<T> outerKeySet() {
		return this.values.keySet();
	}

	// General map methods
	@Override
	public Set<T> keySet() {
		return this.allKeys;
	}

	@Override
	public boolean isEmpty() {
		return this.values.isEmpty();
	}

	@Override
	public int size() {
		return this.values.size();
	}

	// General methods

	/**
	 * Counter implements equals using the equals method of the
	 * inner map
	 * @return
	 */
	@Override
	public boolean equals(Object obj) {
		return this.values.equals(obj);
	}

	@Override
	public String toString() {
		return this.values.toString();
	}

	// Unsupported Map methods
	@Override
	public Integer get(Object key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Integer remove(Object key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<java.util.Map.Entry<T, Integer>> entrySet() {
		throw new UnsupportedOperationException();
	}

}
