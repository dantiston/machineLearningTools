package machineLearningTools;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A 2 dimensional matrix for integer counting. Automatically adds keys
 * in the case that a key to key mapping is missing.
 *
 * @param <T> key type
 *
 * @author T.J. Trimble
 */
public class NestedCounter<T> extends AbstractCounter<T> {
	private Counter<List<T>> values = new Counter<List<T>>();
	private HashSet<T> allKeys = new HashSet<T>();
	private HashMap<T, HashSet<T>> keyMap = new HashMap<T, HashSet<T>>();

	// Core methods

	/**
	 * Combine two keys together to get key for Counter.
	 *
	 * @param key1
	 * @param key2
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<T> getHash(T key1, T key2) {
		return Arrays.asList(key1, key2);
	}

	/**
	 * Add keys to keyMap for toString()
	 *
	 * @param key1
	 * @param key2
	 */
	private void addKeys(T key1, T key2) {
		try {
			this.keyMap.get(key1).add(key2);
		} catch (NullPointerException e) {
			HashSet<T> inner = new HashSet<T>();
			inner.add(key2);
			this.keyMap.put(key1, inner);
		}
		this.allKeys.add(key1);
		this.allKeys.add(key2);
	}

	/**
	 * Increment the value at key1, key2 by 1.<br>
	 * If value does not exist at key1, key2, initialize a value to 1.
	 *
	 * @param key1
	 * @param key2
	 */
	public void increment(final T key1, final T key2) {
		this.values.increment(this.getHash(key1, key2));
		this.addKeys(key1, key2);
	}

	/**
	 * Initialize the value at key1, key2 to 0.<br>
	 * If value exists at key1, key2, reset to 0.
	 *
	 * @param key1
	 * @param key2
	 */
	public void initialize(final T key1, final T key2) {
		this.values.initialize(this.getHash(key1, key2));
		this.addKeys(key1, key2);
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
		this.addKeys(key1, key2);
		return this.values.get(this.getHash(key1, key2));
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
		return this.values.containsKey(this.getHash(key1, key2));
	}

	/**
	 * Returns true iff key is either a key1 or key2
	 *
	 * @param key1
	 * @param key2
	 * @return
	 */
	@Override
	public boolean containsKey(Object key) {
		return this.allKeys.contains(key);
	}

	// General map methods
	/**
	 *
	 * Returns a set of the outer keys
	 *
	 * @see java.util.Map#keySet()
	 */
	@Override
	public Set<T> keySet() {
		return this.keyMap.keySet();
	}

	@Override
	public boolean isEmpty() {
		return this.values.isEmpty();
	}

	@Override
	public int size() {
		return this.keyMap.keySet().size();
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

	/* (non-Javadoc)
	 *
	 * Constructs nested hashmap representation of flat solution
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("{");
		int i = 1;
		int j;
		for (T key1: this.keyMap.keySet()) {
			j = 1;
			stringBuilder.append(String.format("%s={", key1));
			for (T key2: this.keyMap.get(key1)) {
				stringBuilder.append(String.format("%s=%s", key2, this.values.get(this.getHash(key1, key2))));
				if (j < this.keyMap.get(key1).size()) {
					stringBuilder.append(", ");
				}
				j++;
			}
			stringBuilder.append("}");
			if (i < this.keyMap.keySet().size()) {
				stringBuilder.append(", ");
			}
			i++;
		}
		stringBuilder.append("}");
		return stringBuilder.toString();
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
