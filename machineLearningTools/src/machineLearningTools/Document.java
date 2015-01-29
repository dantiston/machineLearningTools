package machineLearningTools;

import static machineLearningTools.Util.join;
import static machineLearningTools.Util.maxKeyByValue;
import static machineLearningTools.Util.sortedKeysByValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

/** *********************************************************
 * Document
 * @author T.J. Trimble
 * A class for storing documents consisting of a set of words
 * and their associated label
 *
 ** *********************************************************/
/**
 *
 *
 *
 * @author T.J. Trimble
 */
public class Document implements Comparable<Document> {
	// Core Members
	private final int docID;
	private final String label;
	private Map<String, Integer> wordCounts;

	// Method Members
	private String sysOutput;
	private Map<String, Double> labelProbs;
	private StringBuilder stringBuilder;
	private String tempLabel;

	// Class Members
	static int docCount;

	/**
	 * Constructor for processing unstructured data. String is split
	 * on whitespace and tokens are counted.
	 *
	 * @param data Unstructured string to count on whitespace.
	 * @param unstructuredFlag Pass as true to process data. Pass as false throws error.
	 *
	 * @see TokenCounter
	 *
	 * @author T.J. Trimble
	 */
	Document(final String data, final boolean unstructuredFlag) {
		if (data == null) {
			throw new NullPointerException();
		}
		if (unstructuredFlag == false) {
			throw new IllegalArgumentException("To process structured data in the Document constructor, do not pass in a boolean parameter. To process unstructured data, the boolean parameter must be true.");
		}
		this.docID = Document.docCount++;
		this.label = "<unknown>";
		this.wordCounts = new TokenCounter(data);
	}

	/**
	 * Read in a String with a label and a set of words and
	 * create a Document object
	 * @param data
	 *
	 */
	Document(final String data) {
		if (data == null) {
			throw new NullPointerException();
		}
		// Initializations
		String[] valuePart;
		this.docID = Document.docCount++;
		this.wordCounts = new HashMap<String, Integer>();
		// Split and process data
		String[] parts = data.split("\\s+");
		this.label = parts[0];
		for (String value: Arrays.copyOfRange(parts, 1, parts.length)) {
			valuePart = value.split("[:]", 2);
			// Check if valuePart is correct length
			if (valuePart.length != 2) {
				throw new IllegalArgumentException(String.format("Value %s in Document constructor is not properly formatted. Missing \":\" denoting count. To process unstructured data, pass in the boolean unstructured flag. %s = %s", value, join(parts, ","), join(valuePart, ",")));
			}
			String word = valuePart[0];
			int count;
			try {
				count = Integer.parseInt(valuePart[1]);
			} catch (NumberFormatException e) {
				throw new NumberFormatException(String.format("Value %s in Document constructor failed to generate an integer representation.", value));
			}
			// For now, assume data file is well formed
			// with unique pairings
			this.wordCounts.put(word, count);
		}
	}

	/**
	 * Read in a JSONObject with a label and a set of words. <br>
	 * jsonObject parameter is the JSONObject, while the key <br>
	 * parameter is the JSON key as a string. <br><br>
	 *
	 * If JSONObject feature is a list instead of a map, features <br>
	 * will be initialized to 1. Note that this assumes values in <br>
	 * "features" is a set, not a bag (i.e. their values are unique). <br><br>
	 *
	 * A JSON representation of a Document must have an ID key, a label, <br>
	 * and a (possibly empty) set of features. e.g.: <br><br>
	 *
	 * "8":{"label":"label1","features":{"the":"2","dog":"1","cat":"1","like":"1"}}
	 *
	 * @param jsonObject
	 * @param key
	 */
	Document(final JSONObject jsonObject, final String key) {
		if (jsonObject == null || key == null) {
			throw new NullPointerException();
		}
		JSONObject json;
		String feature;
		Integer value;
		this.wordCounts = new HashMap<String, Integer>();
		// Get docID
		this.docID = Integer.parseInt(key);
		// Get label
		if (jsonObject.has("label")) {
			try {
				this.tempLabel = jsonObject.getString("label");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		else {
			throw new IllegalArgumentException("Constructing a Document object from JSON requires a \"label\" object");
		}
		this.label = this.tempLabel;
		// Get features
		if (jsonObject.has("features")) {
			try {
				json = jsonObject.getJSONObject("features");
				if (json.names() != null) {
					for (int i=0; i<json.names().length(); i++) {
						feature = json.names().getString(i);
						if (feature != null) {
							value = (json.getString(feature) != null) ? Integer.parseInt(json.getString(feature)) : 1;
							this.wordCounts.put(feature, value);
						}
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		else {
			throw new IllegalArgumentException("Constructing a Document object from JSON requires a \"features\" object");
		}
	}


	/**
	 * For creating copies of documents.
	 *
	 * @param document
	 */
	Document(final Document document) {
		this.docID = document.docID;
		Document.docCount++;
		this.label = document.label;
		this.wordCounts = document.wordCounts;
	}

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
	 * The label is set as the given label. <br><br>
	 *
	 * For testing.
	 *
	 * @param label
	 * @param probabilities
	 */
	void setSysOutput(final String label, final Map<String, Double> probabilities) {
		if (label == null || probabilities == null) {
			throw new NullPointerException("Document.setSysOutput received a null parameter!");
		}
		this.sysOutput = label;
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
			System.err.println("Document.setSysOutput received a null Rule!");
			throw new NullPointerException();
		}
		this.sysOutput = rule.getLabel();
		this.labelProbs = rule.getProbabilities();
	}

	/**
	 * Return true iff this document contains a given word/feature
	 * @param word
	 * @return
	 */
	public boolean contains(final String word) {
		if (word == null) {

		}
		return this.getWords().contains(word);
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
	 * Return this document's words/features
	 * @return this document's words/features
	 */
	public Set<String> getWords() {
		return (this.wordCounts != null) ? this.wordCounts.keySet() : new HashSet<String>();
	}

	/**
	 * Return this document's word/feature counts
	 * @return this document's word/feature counts
	 */
	public Map<String, Integer> getWordCounts() {
		return (this.wordCounts != null) ? this.wordCounts : new HashMap<String, Integer>();
	}

	/**
	 * Alias for getWords()
	 * Return this document's words/features
	 * @see #getWords()
	 * @return this document's words/features
	 */
	public Set<String> getFeatures() {
		return this.getWords();
	}

	/**
	 * Alias for getFeatureCounts()
	 * Return this document's word/feature counts
	 * @see #getWordCounts()
	 * @return this document's word/feature counts
	 */
	public Map<String, Integer> getFeatureCounts() {
		return this.getWordCounts();
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

	public Double getLabelProb(final String systemLabel) {
		if (this.labelProbs == null) {
			System.err.println("Trying to access label probabilities with getLabelProb() before setting system output");
			System.err.println(String.format("docID: %s; trueLabel: %s; systemOutput: %s", this.docID, this.label, systemLabel));
			throw new NullPointerException();
		}
		if (systemLabel == null) {
			throw new NullPointerException("systemLabel parameter is null at Document#getLabelProb(systemLabel)");
		}
		if (this.labelProbs.containsKey(systemLabel)) {
			return this.labelProbs.get(systemLabel);
		}
		return 0.0d;
	}

	public int getFeatCount(final String feature) {
		if (feature == null) {
			throw new NullPointerException("feature parameter is null at Document#getFeatCount(systemLabel)");
		}
		int result = this.wordCounts.get(feature);
		return result;
	}

	/**
	 * Resets the document count to 0
	 * Make sure to do this before starting a new Data object!
	 */
	static void initialize() {
		Document.docCount = 0;
	}

	/**
	 * @return JSON compatible string representation
	 * {"DocID":{"label":"XYZ", "features":{"XYZ":"1","ABC":"1"}}}
	 */
	@Override
	public String toString() {
		this.stringBuilder = new StringBuilder();
		// Get docID
		this.stringBuilder.append("\"");
		this.stringBuilder.append(this.docID);
		this.stringBuilder.append("\":{");
		// Get label
		this.stringBuilder.append("\"label\":\"");
		this.stringBuilder.append(this.label);
		this.stringBuilder.append("\",");
		// Get features
		this.stringBuilder.append("\"features\":{");
		int i = 0;
		for (String feature: this.wordCounts.keySet()) {
			this.stringBuilder.append("\"");
			this.stringBuilder.append(feature);
			this.stringBuilder.append("\":\"");
			this.stringBuilder.append(this.wordCounts.get(feature));
			this.stringBuilder.append("\"");
			if (i != this.wordCounts.size()-1) {
				this.stringBuilder.append(",");
			}
			i++;
		}
		this.stringBuilder.append("}"); // Close features
		this.stringBuilder.append("}"); // Close document
		return this.stringBuilder.toString();
	}

	@Override
	public boolean equals(final Object obj) {
		// Basic checks
		if (obj == this) {
			return true;
		}
		if (obj == null || (obj.getClass() != this.getClass())) {
			return false;
		}
		if (this.hashCode() != obj.hashCode()) {
			return false;
		}
		Document doc = null;
		try {
			doc = (Document)obj;
		}
		catch (ClassCastException e) {
			return false;
		}
		// Check docID, label
		if ((this.docID != doc.docID) || (!this.label.equals(doc.label))) {
			return false;
		}
		//// Check words
		// Check key length
		Set<String> tempSet = this.wordCounts.keySet();
		if (tempSet.size() != doc.getWords().size()) {
			return false;
		}
		// Check keys
		tempSet.removeAll(doc.getWords());
		if (tempSet.size() != 0) {
			return false;
		}
		// Check values
		for (String label: this.wordCounts.keySet()) {
			if (!this.wordCounts.get(label).equals(doc.wordCounts.get(label))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 59;
		int result = 7;
		// Add docID value
		result = prime * (result + (31 * this.docID));
		// Add value representing label
		result = result + (prime * this.label.hashCode());
		// Add value representing keys + values
		for (String key: this.wordCounts.keySet()) {
			result += key.hashCode();
			result += (11 * this.wordCounts.get(key));
		}
		return result;
	}

	/**
	 * return number of unique words/features in document
	 * @return number of unique words/features in document
	 */
	protected int size() {
		return this.wordCounts.size();
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
		StringBuilder stringBuilder = new StringBuilder();
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
}