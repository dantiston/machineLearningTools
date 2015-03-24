package machineLearningTools;

import static machineLearningTools.Util.maxKeyByValue;
import static machineLearningTools.Util.sortedKeysByValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An abstract class for storing documents consisting of
 * a set of words and their associated label
 *
 * @author T.J. Trimble
 *
 ** *********************************************************/
public abstract class Document implements Comparable<Document> {

	// Core members
	protected int docID;
	protected String label;
	protected String sysOutput;
	protected Map<String, Double> labelProbs;

	// Method Members
	protected StringBuilder stringBuilder;
	protected String tempLabel;
	protected Double magnitude; // For Cosine Similarity, etc.

	// Static Members
	static int docCount;
	static String unstructuredError = "To process unstructured data, pass in the boolean unstructured flag.";

	// Static methods

	/**
	 * Resets the document count to 0. <br>
	 * Make sure to do this before starting a new Data object!
	 */
	static void initialize() {
		Document.docCount = 0;
	}

	// Abstract methods

	/**
	 * Return this document's words/features
	 *
	 * @return this document's words/features
	 */
	public abstract Set<String> getWords();

	/**
	 * Alias for getWords()
	 * Return this document's words/features
	 *
	 * @see #getWords()
	 * @return this document's words/features
	 */
	public abstract Set<String> getFeatures();

	/**
	 * Return count of each word.
	 *
	 * @return count of each word
	 */
	public abstract Map<String, Integer> getWordCounts();

	/**
	 * Alias for getFeatureCounts()
	 * Return this document's word/feature counts
	 *
	 * @see #getWordCounts()
	 * @return this document's word/feature counts
	 */
	public abstract Map<String, Integer> getFeatureCounts();

	/**
	 * The magnitude is defined as: <br><br>
	 *
	 * sqrt(sum.k(f.ik^2))
	 *
	 * @return the magnitude of this document object
	 */
	public abstract Double getMagnitude();

	/**
	 * Returns the count of a given feature iff it is present in
	 * this document, else returns 0 <br><br>
	 *
	 * @param feature
	 * @return
	 */
	public abstract int getFeatCount(final String feature);

	/**
	 * Return the number of features associated with this Document object.
	 *
	 * @return the number of features associated with this Document object
	 */
	public abstract int size();


	// Concrete methods

	/**
	 * Set the System Output label for the document object. <br>
	 * This document's label is calculated as the maximum probability
	 * given in the probabilities parameter.
	 *
	 * @param probabilities
	 *
	 * @throws IllegalArgumentException if probabilities map is empty
	 */
	public void setSysOutput(final Map<String, Double> probabilities) {
		if (probabilities == null) {
			throw new NullPointerException("Document#setSysOutput received a null probabilities parameter!");
		}
		if (probabilities.size() <= 0) {
			throw new IllegalArgumentException("Document#setSysOutput received an empty probabilities parameter!");
		}
		this.sysOutput = maxKeyByValue(probabilities);
		this.labelProbs = probabilities;
	}


	/**
	 * Set the System Output label for the document object. <br>
	 * This document's label is calculated using the values in the
	 * given Rule object.
	 *
	 * @param rule
	 */
	public void setSysOutput(final Rule rule) {
		if (rule == null) {
			throw new NullPointerException("Document#setSysOutput received a null Rule!");
		}
		this.sysOutput = rule.getLabel();
		this.labelProbs = rule.getProbabilities();
	}

	/**
	 * Set the System Output label for the document object. <br>
	 * This document's label is calculated by taking the most common
	 * label in the given topK list.
	 *
	 * @param topK
	 */
	public void setSysOutput(List<Document> topK) {
		if (topK == null) {
			throw new NullPointerException("Document#setSysOutput received a null parameter: topK!");
		}
		final HashMap<String, Double> probabilities = new HashMap<String, Double>();
		final Double toAdd = 1.0d/topK.size();
		Double probability;
		// Calculate the probability of each label
		for (Document document: topK) {
			try {
				probability = probabilities.get(document.getLabel())+toAdd;
			} catch (NullPointerException e) {
				probability = toAdd;
			}
			probabilities.put(document.getLabel(), probability);
		}
		this.setSysOutput(probabilities);
	}

	/**
	 * Set the System Output label for the document object. <br>
	 * The label is set as the given label. <br><br>
	 *
	 * For testing.
	 *
	 * @param label
	 * @param probabilities
	 */
	protected void setSysOutput(final String label, final Map<String, Double> probabilities) {
		if (label == null || probabilities == null) {
			throw new NullPointerException("Document#setSysOutput received a null parameter!");
		}
		this.sysOutput = label;
		this.labelProbs = probabilities;
	}

	/**
	 * Return this document's ID
	 * @return this document's ID
	 */
	public int getDocID() {
		return this.docID;
	}

	/**
	 * Return this document's label
	 * @return this document's label
	 */
	public String getLabel() {
		return this.label;
	}

	/**
	 * Return the label assigned by the system to this document
	 * @return the label assigned by the system to this document
	 */
	public String getSysOutput() {
		return this.sysOutput;
	}

	/**
	 * Returns the current document count on the Document class
	 */
	static int getDocCount() {
		return Document.docCount;
	}

	/**
	 * Return the probability of a given label for this
	 * Document's specified system output.
	 *
	 * @param systemLabel
	 * @return
	 */
	public Double getLabelProb(final String systemLabel) {
		if (systemLabel == null) {
			throw new NullPointerException("systemLabel parameter is null at Document#getLabelProb(systemLabel)");
		}
		if (this.labelProbs == null) {
			System.err.println("Trying to access label probabilities with getLabelProb() before setting system output");
			System.err.println(String.format("docID: %s; trueLabel: %s; systemOutput: %s", this.docID, this.label, systemLabel));
			throw new NullPointerException();
		}
		if (this.labelProbs.containsKey(systemLabel)) {
			return this.labelProbs.get(systemLabel);
		}
		return 0.0d;
	}

	/**
	 * Writes this document's system output with the following format: <br>
	 *
	 * <b>instanceID true_label class1 prob1 class2 prob2 ...</b>
	 *
	 * @param convertLogProbabilities if probabilities are log probabilities, then convert to non-log probabilities
	 *
	 * @return
	 */
	public String getFormattedSystemOutput(boolean convertLogProbabilities) {
		// Sort classes by probabilities
		// Return sorted classes
		StringBuilder stringBuilder = new StringBuilder(this.labelProbs.size()+1);
		stringBuilder.append(String.format("Document:%s %s", this.docID, this.label));
		Map<String, Double> probabilities = new HashMap<String, Double>(this.labelProbs);
		// Convert log probs
		if (convertLogProbabilities) {
			for (String label: probabilities.keySet()) {
				// Make sure probability is a log prob, i.e. probability < 0
				if (probabilities.get(label) < 0) {
					probabilities.put(label, Math.pow(10, probabilities.get(label)));
				} else {
					throw new IllegalArgumentException("Document#getFormattedSystemOutput() passed true convertLogProbabilities flag for non-log probabilities.");
				}
			}
		}
		// Output formatting
		ArrayList<String> sortedLabels = sortedKeysByValue(probabilities, true);
		for (String label: sortedLabels) {
			stringBuilder.append(String.format(" %s %s", label, probabilities.get(label)));
		}
		return stringBuilder.toString();
	}

	/**
	 * Return true iff this document contains a given word/feature
	 * @param word
	 * @return
	 */
	public boolean contains(final String word) {
		if (word == null) {
			throw new NullPointerException("Document#contains received a null parameter: word!");
		}
		return this.getWords().contains(word);
	}

	/**
	 * Sort documents based on document IDs,
	 * which is, sort documents based on their order
	 * in the input file.
	 * @param otherDoc
	 * @return
	 */
	@Override
	public int compareTo(final Document otherDoc) {
		if (this.docID < otherDoc.docID) {
			return -1;
		}
		else if (this.docID > otherDoc.docID) {
			return 1;
		}
		else {
			return 0;
		}
	}
}