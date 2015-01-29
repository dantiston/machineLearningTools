package machineLearningTools;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/** *********************************************************
 * Counter
 *
 * HashMap wrapper from keyType to integer for counting with
 * automatic initialization in the case of a missing key
 *
 * @param <T> key type
 * @author T.J. Trimble
 *
 ** *********************************************************/
public class Counter<T> extends AbstractCounter<T> {
	private HashMap<T, Integer> values = new HashMap<T, Integer>();

	/**
	 * Basic constructor to instantiate empty Counter object
	 */
	public Counter() {}

	/**
	 * Constructor for Counter with values filled in with contents
	 * of the collection.
	 *
	 * @param data
	 */
	public Counter(final Collection<T> data) {
		if (data == null) {
			throw new NullPointerException();
		}
		Iterator<T> it = data.iterator();
		while (it.hasNext()) {
			this.increment(it.next());
		}
	}

	/**
	 * Constructor for Counter with values filled in with contents
	 * of the array.
	 *
	 * @param data
	 */
	public Counter(final T[] data) {
		if (data == null) {
			throw new NullPointerException();
		}
		for (T element: data) {
			this.increment(element);
		}
	}

	// Core methods

	/**
	 * Increment the value associated with the key
	 *
	 * @param key
	 */
	public void increment(final T key) {
		try {
			this.values.put(key, this.values.get(key)+1);
		}
		catch (NullPointerException e) {
			this.values.put(key, 1);
		}
	}

	/**
	 * Initialize key to 0
	 * If key already has a value, set back to 0
	 * @param key
	 */
	public void initialize(final T key) {
		this.values.put(key, 0);
	}

	/**
	 * Get value associated with key
	 * @param key
	 * @return value associated with key
	 */
	@Override
	public Integer get(Object key) {
		try {
			@SuppressWarnings("unchecked")
			T thisKey = (T)key;
			if (!this.values.containsKey(key)) {
				this.values.put(thisKey, 0);
			}
			return this.values.get(thisKey);
		}
		catch (ClassCastException e) {
			return null;
		}
	}

	// Other methods

	@Override
	public String toString() {
		return this.values.toString();
	}

	/**
	 * Counter implements equals using the equals method of the
	 * inner map
	 * @return
	 */
	@Override
	public boolean equals(Object obj) {
		return this.values.equals(obj);
	}

	// General Map methods

	/**
	 * Return boolean if counter contains key
	 * @param key
	 * @return if counter contains key
	 */
	@Override
	public boolean containsKey(Object key) {
		return this.values.containsKey(key);
	}

	/**
	 * Return keys of counter
	 * @return keys of counter
	 */
	@Override
	public Set<T> keySet() {
		return this.values.keySet();
	}

	@Override
	public Set<java.util.Map.Entry<T, Integer>> entrySet() {
		return this.values.entrySet();
	}

	@Override
	public Integer remove(Object key) {
		return this.values.remove(key);
	}

	@Override
	public boolean isEmpty() {
		return this.values.isEmpty();
	}

	@Override
	public int size() {
		return this.values.size();
	}
}