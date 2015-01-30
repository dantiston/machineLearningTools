package machineLearningTools;


/**
 * TokenCounter <br><br>
 *
 * TokenCounter extends Counter class to add functionality to
 * tokenize on whitespace and count tokens in a String.
 *
 * TokenCounter only accepts String objects as keys
 *
 * @param data
 *
 */
public class TokenCounter extends Counter<String> {

	/**
	 *
	 * @param data
	 * @author T.J. Trimble
	 */
	public TokenCounter(String data) {
		if (data == null) {
			throw new NullPointerException();
		}
		if (data.length() == 0) {
			return;
		}
		for (String part: data.split("\\s+")) {
			if (part.getClass().isAssignableFrom(data.getClass())) {
				this.increment(part);
			}
		}
	}
}
