package machineLearningTools;

import static machineLearningTools.Util.join;

import java.util.ArrayList;
import java.util.Collections;

/** *********************************************************
 * ConfusionMatrix
 * @author T.J. Trimble
 *
 * Class for creating, loading, and formatting confusion matrices
 *
 * TODO: Add Precision, Recall, F-score, and accuracy options
 ** *********************************************************/
public class ConfusionMatrix extends NestedCounter<String> {
	// Core values
	private String label;

	/**
	 * Construct a ConfusionMatrix object using the Data parameter
	 * by comparing each of the Data's Documents' true labels
	 * and system labels. This fills the in the confusion matrix.
	 *
	 * @param testResult
	 * @param trainOrTest
	 * @author T.J. Trimble
	 */
	public ConfusionMatrix(final Data data, final String trainOrTest) {
		if (data == null || trainOrTest == null) {
			throw new NullPointerException();
		}
		this.label = trainOrTest;
		for (Document document: data.getDocs()) {
			this.increment(document.getLabel(), document.getSysOutput());
		}
	}

	/**
	 * Return label of confusion matrix
	 * @return label member
	 */
	public String getLabel() {
		return this.label;
	}

	/**
	 * Return a string representation of the confusion matrix
	 * object with the keys sorted naturally (i.e. alphabetically)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (this.isEmpty()) {
			return String.format("Confusion Matrix for %s not utilized.", this.label);
		}
		StringBuilder stringBuilder = new StringBuilder();

		/* Sort Labels */
		ArrayList<String> sortedKeys = new ArrayList<String>(this.getAllKeys());
		Collections.sort(sortedKeys);

		/* Header */
		stringBuilder.append("Confusion matrix for the ");
		stringBuilder.append(this.label);
		stringBuilder.append("ing data:\nrow is the truth, column is the system output\n\n");

		/* Row labels */
		stringBuilder.append("\t");
		stringBuilder.append(join(sortedKeys, " "));
		stringBuilder.append("\n");

		/* Results */
		Integer value;
		Double total = 0.0d;
		Double correct = 0.0d;
		for (String label1: sortedKeys) {
			stringBuilder.append(label1);
			for (String label2: sortedKeys) {
				value = this.get(label1, label2);
				stringBuilder.append("\t");
				stringBuilder.append(value);
				total += value;
				if (label1 == label2) {
					correct += value;
				}
			}
			stringBuilder.append("\n");
		}
		stringBuilder.append(String.format("%n%sing accuracy = %f%n%n", this.label, correct/total));
		return stringBuilder.toString();
	}
}
