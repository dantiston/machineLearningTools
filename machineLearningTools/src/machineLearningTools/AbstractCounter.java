package machineLearningTools;

import java.util.Collection;
import java.util.Map;

/**
 * AbstractCounter
 *
 * Defines an implementation of a HashMap wrapper from keyType to integer
 * for counting with automatic initialization in the case of a missing key
 *
 * Specific implementations must specify the parameters of values
 *
 * @param <T>
 *
 * @author T.J. Trimble
 */
public abstract class AbstractCounter<T> implements Map<T, Integer> {

	// Specific implementations must specify values parameters
	@SuppressWarnings({ "rawtypes", "unused" })
	private Map values;

	// Unsupported Map methods

	@Override
	public void clear() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsValue(Object value) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Integer put(T key, Integer value) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void putAll(Map<? extends T, ? extends Integer> m) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<Integer> values() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}
}
