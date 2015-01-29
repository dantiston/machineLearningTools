package machineLearningTools;

/**
 * @author T.J. Trimble
 *
 * Collection of methods useful for testing Machine Learning code
 *
 */
public class Testing {

	/**
	 * @author T.J. Trimble
	 *
	 * Format string with "testDocuments/" prefix
	 *
	 * @param name
	 * @throws IllegalArgumentException for empty string
	 * @return
	 */
	public static String testFile(String name) throws IllegalArgumentException {
		if (name == null) {
			throw new NullPointerException();
		}
		if (name.length() <= 0) {
			throw new IllegalArgumentException();
		}
		return "testDocuments/"+name;
	}

}