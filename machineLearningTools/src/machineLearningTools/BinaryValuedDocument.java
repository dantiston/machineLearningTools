package machineLearningTools;

import static machineLearningTools.Util.join;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

/** *********************************************************
 * <b>BinaryValuedDocument<b><br>
 *
 * A class for storing documents consisting of a set of words
 * and their associated label.
 *
 * @author T.J. Trimble
 ** *********************************************************/
public class BinaryValuedDocument extends Document implements Comparable<Document> {

	// Core Members
	private final Set<String> features;

	/**
	 * Constructor for processing unstructured data. String is split
	 * on whitespace and tokens are put into a set.
	 *
	 * @param data Unstructured string to count on whitespace.
	 * @param unstructuredFlag Pass as true to process data. Pass as false throws error.
	 *
	 * @author T.J. Trimble
	 */
	BinaryValuedDocument(final String data, final boolean unstructuredFlag) {
		if (data == null) {
			throw new NullPointerException();
		}
		if (unstructuredFlag == false) {
			throw new IllegalArgumentException("To process structured data in the Document constructor, do not pass in a boolean parameter. To process unstructured data, the boolean parameter must be true.");
		}
		this.docID = Document.docCount++;
		this.label = "<unknown>";
		this.features = new HashSet<String>();
		this.features.addAll(Arrays.asList(data.split("\\s+")));
	}

	/**
	 * Read in a String with a label and a set of words and
	 * create a Document object
	 * @param data
	 *
	 */
	BinaryValuedDocument(final String data) {
		if (data == null) {
			throw new NullPointerException();
		}
		// Initializations
		String word;
		String[] valuePart;
		this.docID = Document.docCount++;
		// Split and process data
		String[] parts = data.split("\\s+");
		this.label = parts[0];
		this.features = new HashSet<String>(parts.length-1);
		if (parts.length > 1) {
			for (String value: Arrays.copyOfRange(parts, 1, parts.length)) {
				valuePart = value.split(":", 2);
				// Check if valuePart is correct length
				if (valuePart.length != 2) {
					throw new IllegalArgumentException(String.format("Value %s in Document constructor is not properly formatted. Missing \":\" denoting count. %s (%s -> %s)", value, BinaryValuedDocument.unstructuredError, join(parts, ","), join(valuePart, ",")));
				}
				word = valuePart[0];
				if (this.features.contains(word)) {
					throw new IllegalArgumentException(String.format("Vector %s contains a non-unique word->count pairing. %s", data, BinaryValuedDocument.unstructuredError));
				}
				this.features.add(word);
			}
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
	BinaryValuedDocument(final JSONObject jsonObject, final String key) {
		if (jsonObject == null || key == null) {
			throw new NullPointerException();
		}
		JSONObject json;
		String feature;
		this.features = new HashSet<String>();
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
							this.features.add(feature);
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
	BinaryValuedDocument(final Document document) {
		this.docID = document.docID;
		Document.docCount++;
		this.label = document.label;
		this.features = document.getWords();
	}

	/**
	 * The magnitude is defined as: <br><br>
	 *
	 * sqrt(sum.k(f.ik^2))
	 *
	 * @return the magnitude of this document object
	 */
	@Override
	public Double getMagnitude() {
		if (this.magnitude == null) {
			this.magnitude = Math.sqrt(this.features.size());
		}
		return this.magnitude;
	}

	/**
	 * Return this document's words/features
	 * @return this document's words/features
	 */
	@Override
	public Set<String> getWords() {
		return (this.features != null) ? this.features : new HashSet<String>();
	}

	/**
	 * Included for compatibility reasons. Returns a Map from each word
	 * to 1
	 *
	 * @return this document's word/feature counts
	 */
	@Override
	public Map<String, Integer> getWordCounts() {
		HashMap<String, Integer> result = new HashMap<String, Integer>(this.features.size());
		for (String feature: this.features) {
			result.put(feature, 1);
		}
		return result;
	}

	/**
	 * Alias for getWords()
	 * Return this document's words/features
	 *
	 * @see #getWords()
	 * @return this document's words/features
	 */
	@Override
	public Set<String> getFeatures() {
		return this.getWords();
	}

	/**
	 * Alias for getFeatureCounts()
	 * Return this document's word/feature counts
	 *
	 * @see #getWordCounts()
	 * @return this document's word/feature counts
	 */
	@Override
	public Map<String, Integer> getFeatureCounts() {
		return this.getWordCounts();
	}

	/**
	 * Returns 1 iff the given feature is present in
	 * this document, else returns 0 <br><br>
	 *
	 * Included for compatibility reasons.
	 *
	 * @param feature
	 * @return
	 */
	@Override
	public int getFeatCount(final String feature) {
		if (feature == null) {
			throw new NullPointerException("feature parameter is null at Document#getFeatCount(systemLabel)");
		}
		return (this.features.contains(feature)) ? 1 : 0;
	}

	/**
	 * return number of unique words/features in document
	 * @return number of unique words/features in document
	 */
	@Override
	public int size() {
		return this.features.size();
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
		for (String feature: this.features) {
			this.stringBuilder.append("\"");
			this.stringBuilder.append(feature);
			this.stringBuilder.append("\":\"1\"");
			if (i != this.features.size()-1) {
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
		BinaryValuedDocument doc = null;
		try {
			doc = (BinaryValuedDocument)obj;
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
		if (this.size() != doc.size()) {
			return false;
		}
		// Check keys
		Set<String> tempSet = new HashSet<String>(this.features);
		tempSet.removeAll(doc.getWords());
		if (tempSet.size() != 0) {
			return false;
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
		for (String feature: this.features) {
			result += feature.hashCode();
		}
		return result;
	}
}