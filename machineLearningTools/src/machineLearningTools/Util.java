package machineLearningTools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

/**
 * @author T.J. Trimble
 * machineLearningTools.utils
 *
 * A collection of functions for working with various data
 * structures
 */
public class Util {

	/* *******************
	 * sortedKeysByValue()
	 * *******************/

	/**
	 * sortedKeysByValue <br>
	 * Take a map and return a list of the keys sorted <br>
	 * by the values associated with each key <br>
	 *
	 * Returns ArrayList with only key if given map with one key. <br><br>
	 *
	 * Returns empty ArrayList if given empty map. <br><br>
	 *
	 * @author T.J. Trimble
	 * @param map
	 * @param reverse
	 * @return
	 **/
	public static <K, V extends Comparable<? super V>> ArrayList<K> sortedKeysByValue(final Map<K, V> map, final boolean reverse) {
		if (map == null) {
			throw new NullPointerException();
		}
		if (map.keySet().size() <= 1) {
			if (map.keySet().size() == 1) {
				return new ArrayList<K>(map.keySet());
			}
			return new ArrayList<K>();
		}
		// Put entries in a list
	    ArrayList<Map.Entry<K, V>> entries = new ArrayList<Map.Entry<K, V>>(map.entrySet());
	    // Sort entries by value
	    Comparator<Map.Entry<K, V>> comparator = new Comparator<Map.Entry<K, V>>() {
	    	@Override
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
	    		int result = (o1.getValue()).compareTo(o2.getValue());
	    		if (reverse) {
	    			return -result;
	    		}
	    		return result;
	        }
	    };
	    Collections.sort(entries, comparator);
	    // Get gets from entries
	    ArrayList<K> result = new ArrayList<K>();
	    for (Map.Entry<K, V> entry: entries) {
	    	result.add(entry.getKey());
	    }
	    return result;
	}

	/**
	 * sortedKeysByValue <br>
	 * Reverse parameter defaults to false
	 *
	 * @param map
	 * @return
	 **/
	public static <K, V extends Comparable<? super V>> ArrayList<K> sortedKeysByValue(Map<K, V> map) {
		return sortedKeysByValue(map, false);
	}

	/* ****************
	 * maxKeysByValue()
	 * ****************/

	/**
	 * maxKeyByValue <br>
	 * Returns key with maximum value in map by natural ordering <br>
	 * Returns first item in sorted array from sortedKeysByValue(map)
	 *
	 * Returns only key if map with one key is given. <br>
	 * Returns null if map with no keys is given. <br>
	 *
	 * @param map
	 * @return key with maximum value in map by natural ordering
	 * @throws IllegalArgumentException
	 * @author T.J. Trimble
	 **/
	public static <K, V extends Comparable<? super V>> K maxKeyByValue(Map<K, V> map) {
		if (map == null) {
			throw new NullPointerException();
		}
		if (map.keySet().size() <= 1) {
			if (map.keySet().size() == 1) {
				return map.keySet().iterator().next();
			}
			return null;
		}
		return sortedKeysByValue(map, true).get(0);
	}

	/* ****************
	 * minKeysByValue()
	 * ****************/

	/**
	 * minKeyByValue <br>
	 * Returns key with minimum value in map by natural ordering <br>
	 * Returns last item in sorted array from sortedKeysByValue(map) <br>
	 *
	 * Returns only key if map with one key is given. <br>
	 * Returns null if map with no keys is given. <br>
	 *
	 * @author T.J. Trimble
	 * @param map
	 * @return key with minimum value in map by natural ordering
	 **/
	public static <K, V extends Comparable<? super V>> K minKeyByValue(Map<K, V> map) {
		if (map == null) {
			throw new NullPointerException();
		}
		if (map.keySet().size() <= 1) {
			if (map.keySet().size() == 1) {
				return map.keySet().iterator().next();
			}
			return null;
		}
		return sortedKeysByValue(map).get(0);
	}

	/* ***********
	 * sumValues()
	 * ***********/

	/**
	 * sumValues <br>
	 * Returns the sum of the values in a Map. <br>
	 *
	 * Returns 0.0d if empty map. <br>
	 * Returns only value if only one key. <br>
	 *
	 * @param map
	 * @return
	 **/
	public static <K> double sumValues(Map<K, Double> map) {
		if (map == null) {
			throw new NullPointerException();
		}
		if (map.keySet().size() == 1) {
			return map.values().iterator().next();
		}
		double sum = 0.0d;
		for (double value: map.values()) {
			sum = sum + value;
		}
		return sum;
	}

	public static <K> int sumValues(Map<K, Integer> map) {
		if (map == null) {
			throw new NullPointerException();
		}
		if (map.keySet().size() == 1) {
			return map.values().iterator().next();
		}
		int sum = 0;
		for (Integer value: map.values()) {
			sum = sum + value;
		}
		return sum;
	}

	public static <K> float sumValues(Map<K, Float> map) {
		if (map == null) {
			throw new NullPointerException();
		}
		if (map.keySet().size() == 1) {
			return map.values().iterator().next();
		}
		float sum = 0.0f;
		for (float value: map.values()) {
			sum = sum + value;
		}
		return sum;
	}


	/* ******
	 * join()
	 * ******/

	/**
	 * Joins the string representations of each item in a collection of
	 * values by the specified separator.
	 *
	 * @param values
	 * @param separator
	 * @return
	 */
	public static <T> String join(Collection<T> values, String separator) {
		if (values == null || separator == null) {
			throw new NullPointerException();
		}
		if (values.size() == 0) {
			return "";
		}
		Iterator<T> iterator = values.iterator();
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(iterator.next().toString());
		while (iterator.hasNext()) {
			stringBuilder.append(separator);
			stringBuilder.append(iterator.next().toString());
		}
		return stringBuilder.toString();
	}

	/**
	 * Joins the string representations of each item in a collection of
	 * values.
	 *
	 * @param values
	 * @return
	 */
	public static <T> String join(Collection<T> values) {
		return join(values, "");
	}

	/**
	 * Joins the string representations of each item in a collection of
	 * values by the specified separator.
	 *
	 * @param values
	 * @param separator
	 * @return
	 */
	public static String join(Object[] values, String separator) {
		return join(Arrays.asList(values), separator);
	}

	/**
	 * Joins the string representations of each item in a collection of
	 * values.
	 *
	 * @param values
	 * @return
	 */
	public static String join(Object[] values) {
		return join(values, "");
	}
}