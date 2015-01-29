package machineLearningTools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

/** *********************************************************
 * ConfusionMatrix
 * @author T.J. Trimble
 *
 * Class for creating, loading, and formatting confusion matrices
 *
 * TODO: Add Precision, Recall, F-score, and accuracy options
 ** *********************************************************/
public class ConfusionMatrixArchive {
	private HashMap<String, HashMap<String, Integer>> matrix;
	private HashSet<String> keys; // Keep track of all labels in case of asymmetric labels
	private String label;
	private Double score;
	private Integer counter;
	private Integer total;
	private Double correct;
	private StringBuilder stringBuilder;
	private Integer value;

	/**
	 * Construct a ConfusionMatrix object using the Data parameter
	 * by comparing each of the Data's Documents' true labels
	 * and system labels. This fills the in the confusion matrix.
	 *
	 * @param testResult
	 * @param trainOrTest
	 * @author T.J. Trimble
	 */
	public ConfusionMatrixArchive(final Data data, final String trainOrTest) {
		if (data == null || trainOrTest == null) {
			throw new NullPointerException();
		}
		this.label = trainOrTest;
		this.keys = new HashSet<String>();
		this.matrix = new HashMap<String, HashMap<String, Integer>>();
		for (Document document: data.getDocs()) {
			this.increment(document.getLabel(), document.getSysOutput());
		}
	}

	/**
	 * Increment value at key1, key2 by 1
	 *
	 * @param key1
	 * @param key2
	 */
	public void increment(final String key1, final String key2) {
		if (this.matrix.containsKey(key1)) {
			if (this.matrix.get(key1).containsKey(key2)) {
				this.matrix.get(key1).put(key2, this.matrix.get(key1).get(key2)+1);
			}
			else {
				// Contains key 1 -> HashMap,
				// but key1->HashMap doesn't contain key2, so
				// put key2 in key1->HashMap with default of 1
				this.matrix.get(key1).put(key2, 1);
			}
		}
		else {
			// Doesn't contain either key: add key 1 and key 2 and set to 1
			HashMap<String, Integer> inner = new HashMap<String, Integer>();
			inner.put(key2, 1);
			this.matrix.put(key1, inner);
		}
		this.keys.add(key1);
		this.keys.add(key2);
	}

	/**
	 * Return value at key1, key2
	 * Requesting a key that is not in the matrix initializes
	 * a 0 value for that key in the Matrix.
	 * @param key1
	 * @param key2
	 * @return
	 */
	public Integer get(final String key1, final String key2) {
		if (!this.matrix.containsKey(key1)) {
			HashMap<String, Integer> inner = new HashMap<String, Integer>();
			inner.put(key2, 0);
			this.matrix.put(key1, inner);
		}
		else if (!this.matrix.get(key1).containsKey(key2)) {
			this.matrix.get(key1).put(key2, 0);
		}
		this.keys.add(key1);
		this.keys.add(key2);
		return this.matrix.get(key1).get(key2);
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
		/* Initializations */
		this.stringBuilder = new StringBuilder();
		this.total = 0;
		this.correct = 0.0d;

		/* Sort Labels */
		ArrayList<String> sortedKeys = new ArrayList<String>(this.keys);
		Collections.sort(sortedKeys);

		/* Header */
		this.stringBuilder.append("Confusion matrix for the ");
		this.stringBuilder.append(this.label);
		this.stringBuilder.append("ing data:\nrow is the truth, column is the system output\n\n");

		/* Row labels */
		this.stringBuilder.append("\t");
		this.counter = 1;
		for (String label: sortedKeys) {
			this.stringBuilder.append(label);
			if (this.counter < this.keys.size()) {
				this.stringBuilder.append(" ");
			}
			this.counter++;
		}
		this.stringBuilder.append("\n");

		/* Results */
		for (String label1: sortedKeys) {
			this.stringBuilder.append(label1);
			for (String label2: sortedKeys) {
				this.value = 0; // Reset just in case
				this.value = this.matrix.get(label1).get(label2);
				if (this.value == null) {
					this.value = 0;
				}
				this.stringBuilder.append("\t");
				this.stringBuilder.append(this.value);
				this.total += this.value;
				if (label1 == label2) {
					this.correct += this.value;
				}
			}
			this.stringBuilder.append("\n");
		}
		this.score = this.correct/this.total;
		this.stringBuilder.append(String.format("%n%sing accuracy = %f%n%n", this.label, this.score));
		return this.stringBuilder.toString();
	}
}
