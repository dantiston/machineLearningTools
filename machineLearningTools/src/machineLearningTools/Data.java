package machineLearningTools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Data <br>
 *
 * Sentences with associated IDs <br><br>
 * Specifically, an unordered collection of labeled documents,
 * where each document contains a label and a mapping of
 * words/features and (if applicable) their associated counts.
 *
 * Includes methods for reading data from a file and accessing
 * documents by document ID
 *
 * @author T.J. Trimble
 ** *********************************************************/

public class Data {
	// TODO: split into binary (current) and real-valued data
	// NOTE: if data in input file is real-valued, store
	// real values, else, store binary. Then, create two
	// subtypes which differ only in data accessors, where
	// one returns binary values and the other real values?

	protected HashSet<String> allLabels;
	protected HashSet<String> allFeatures;

	private final HashMap<Integer, Document> data;

	/**
	 * Load data from a file and construct a Data object
	 *
	 * @param trainingDataFileName
	 */
	public Data(final String trainingDataFileName) {
		if (trainingDataFileName == null) {
			throw new NullPointerException();
		}
		this.data = this.readDataFromFile(trainingDataFileName);
	}

	/**
	 * Load data from a JSONObject and construct a Data object
	 *
	 * @param trainingDataJSON
	 */
	public Data(final JSONObject trainingDataJSON) {
		if (trainingDataJSON == null) {
			throw new NullPointerException();
		}
		this.data = this.readDataFromJSON(trainingDataJSON);
	}

	/**
	 * Create an empty Data object.
	 *
	 * For testing.
	 *
	 * @author T.J. Trimble
	 */
	Data() {
		this.data = new HashMap<Integer, Document>(0);
	}

	/**
	 * Read in a file of documents,
	 * each specified with a label and a set of features
	 * @param dataFileName
	 */
	private HashMap<Integer, Document> readDataFromFile(final String dataFileName) {
		Document.initialize();
		HashMap<Integer, Document> result = new HashMap<Integer, Document>();
		this.allLabels = new HashSet<String>();
		String lineString;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(dataFileName));
			while ((lineString = reader.readLine()) != null) {
				Document doc = new Document(lineString);
				result.put(doc.getDocID(), doc);
				this.allLabels.add(doc.getLabel());
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Error reading data from file.");
			System.exit(1);
		}
		return result;
	}

	/**
	 * Read in a JSON object of documents,
	 * each specified with a label and a set of features
	 * @param trainingDataJSON
	 */
	private HashMap<Integer, Document> readDataFromJSON(final JSONObject Json) {
		Document.initialize();
		this.allLabels = new HashSet<String>();
		HashMap<Integer, Document> result = new HashMap<Integer, Document>();
		String key;
		Document doc;
		for (int i=0; i<Json.names().length(); i++) {
			try {
				key = Json.names().getString(i);
				doc = new Document((JSONObject)Json.get(key), key);
				result.put(doc.getDocID(), doc);
				this.allLabels.add(doc.getLabel());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * Calculate the entropy of the features in the Documents in this Data object
	 *
	 * @return double representation of entropy of data
	 *
	 * @author T.J. Trimble
	 */
	public Double getEntropy(final Collection<Integer> docIDs) {
		Double entropy = 0.0d;
		if (docIDs.size() > 1) {
			double probability;
			Counter<String> labelCounts = new Counter<String>();
			// Get counts of each label
			for (int id: docIDs) {
				labelCounts.increment(this.getLabel(id));
			}
			//// Calculate entropy
			// Get sum of P(label)*log(P(label))
			for (String label: labelCounts.keySet()) {
				probability = labelCounts.get(label)/((double)docIDs.size());
				entropy += probability * (Math.log(probability)/MLMath.log2);
			}
			// Entropy is negative sum
			entropy = -entropy;
		}
		return entropy;
	}

	/**
	 * Set the system output label for the given document. The label
	 * is set as the String with the highest probability in the
	 * probabilities Map parameter.
	 *
	 * @param docID
	 * @param label
	 * @param probabilities
	 */
	public void setSysOutput(final int docID, final Map<String, Double> probabilities) {
		if (probabilities == null) {
			throw new NullPointerException("Data.setSysOutput received a null probabilities parameter!");
		}
		this.data.get(docID).setSysOutput(probabilities);
	}

	/**
	 * Set the system output for the given document. The label
	 * is set as the given label.
	 *
	 * @param docID
	 * @param label
	 * @param probabilities
	 */
	void setSysOutput(final int docID, final String label, final Map<String, Double> probabilities) {
		if (probabilities == null) {
			throw new NullPointerException("Data.setSysOutput received a null probabilities parameter!");
		}
		this.data.get(docID).setSysOutput(label, probabilities);
	}

	/**
	 * Return the label of the document with the specified ID
	 * @param id
	 * @return label of document with id
	 */
	public String getLabel(final int id) {
		return this.data.get(id).getLabel();
	}

	/**
	 * Return the document with the specified ID
	 * @param id
	 * @return document with the specified ID
	 */
	public Document getDoc(final int id) {
		return this.data.get(id);
	}

	/**
	 * Return the features in the document specified by docID
	 * @param docID
	 * @return features in the specified document
	 */
	public Set<String> getFeatures(final int docID) {
		return this.data.get(docID).getWords();
	}

	/**
	 * Return the data stored in this object
	 * @return data stored in this object
	 */
	public HashMap<Integer, Document> getData() {
		return this.data;
	}

	/**
	 * Return the Documents stored in this object as an ArrayList.
	 * Note that the Documents are stored in a hashed environment,
	 * so the ordering of these is arbitrary. Sorting this list
	 * produces the Documents sorted by document ID
	 *
	 * @return the Documents stored in this object as an ArrayList
	 */
	public ArrayList<Document> getDocs() {
		ArrayList<Document> result = new ArrayList<Document>();
		for (Document document: this.data.values()) {
			result.add(document);
		}
		return result;
	}

	/**
	 * Return a set of the IDs of all the documents stored in this object
	 * @return Set of IDs
	 */
	public Set<Integer> getIDs() {
		return this.data.keySet();
	}

	/**
	 * Return a Set of all the features in all the Documents
	 * in this Data object
	 *
	 * @return
	 */
	public Set<String> getAllFeatures() {
		if (this.allFeatures == null) {
			this.allFeatures = new HashSet<String>();
			for (Document document: this.data.values()) {
				this.allFeatures.addAll(document.getFeatures());
			}
		}
		return this.allFeatures;
	}

	/**
	 * Returns number of documents in object
	 * @return number of documents in object
	 */
	public int size() {
		return this.data.size();
	}

	/**
	 * @return JSON compatible string representation
	 *
	 * {DOC1_JSON, DOC2_JSON}
	 * TODO: Once real valued features are implemented, change this
	 */
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		int i = 0;
		int size = this.data.keySet().size();
		// Sort IDs
		ArrayList<Document> documents = new ArrayList<Document>(this.data.values());
		Collections.sort(documents);
		// Build result
		stringBuilder.append("{");
		for (Document doc: documents) {
			stringBuilder.append(doc.toString());
			if (i != size-1) {
				stringBuilder.append(",");
			}
			i++;
		}
		stringBuilder.append("}");
		return stringBuilder.toString();
	}

	/**
	 * Writes the system output probabilities for each document, first
	 * converting the probabilities from log10. <br><br>
	 *
	 * Uses the following format: <br>
	 *
	 * <b>instanceID true_label class1 prob1 class2 prob2 ...</b>
	 *
	 * @return
	 */
	public String getFormattedSystemOutput(boolean convertLogProbabilities) {
		StringBuilder stringBuilder = new StringBuilder();
		ArrayList<Integer> sortedKeys = new ArrayList<Integer>(this.data.keySet());
		Collections.sort(sortedKeys);
		for (int docID: sortedKeys) {
			stringBuilder.append(this.data.get(docID).getFormattedSystemOutput(convertLogProbabilities));
			stringBuilder.append("\n");
		}
		return stringBuilder.toString();
	}

	/**
	 * Writes the system output probabilities for each document, first
	 * converting the probabilities from log10. <br><br>
	 *
	 * Uses the following format: <br>
	 *
	 * <b>instanceID true_label class1 prob1 class2 prob2 ...</b>
	 *
	 * @param convertFromLogProbs
	 * @return
	 */
	public String getFormattedSystemOutput() {
		return this.getFormattedSystemOutput(false);
	}

	/**
	 * Return all labels sorted by probability
	 * @return
	 */
	public HashSet<String> getAllLabels() {
		return this.allLabels;
	}

	/**
	 * equals
	 * @Author T.J. Trimble
	 * Compares two Data objects by comparing their HashCodes.
	 *
	 * NOTE: Because the Data class HashCode is calculated as
	 * the sum of the HashCodes of its Documents, and each
	 * Document's HashCode is calculated based on its label
	 * and its words, this is a sort of "weak equivalence",
	 * in that two Data objects are considered equal if they
	 * have weakly equivalent Documents, irrespective of
	 * their order. Also note that because Documents are
	 * unordered collections of binary word features, two
	 * different texts can have the same Document
	 * representation.
	 *
	 * However, because the current implementation is only
	 * concerned with algorithms that utilize bags of words,
	 * this equivalency between two Documents is intended,
	 * and subsequently, the equivalency between Data objects.
	 *
	 * Because of this, two different Documents and
	 * subsequently Data objects that are equivalent will
	 * collapse in a hash.
	 *
	 * Additionally, because it's simply the sum of the
	 * Document's hash codes, it is possible that two unequal
	 * Data objects have equal hash codes, and subsequently
	 * are evaluated as equal. This method, and the Document's
	 * hash code method, utilize prime coefficients to avoid
	 * this, but it is still possible. Hash with caution.
	 *
	 * TODO: Update this note for real-valued Documents
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (this.hashCode() == obj.hashCode()) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * hashCode
	 * @author T.J. Trimble
	 *
	 * Calculates the hash code of this Data object
	 * as the sum of the hash codes of this object's
	 * Documents! Note that this is a "weak equivalence"
	 * in that Document hash codes are calculated based on
	 * a binarized bag of words, so two different texts
	 * can produce equal Document objects.
	 *
	 * Additionally, because it's simply the sum of the
	 * Document's hash codes, it is possible that two unequal
	 * Data objects have equal hash codes, and subsequently
	 * are evaluated as equal. This method, and the Document's
	 * hash code method, utilize prime coefficients to avoid
	 * this, but it is still possible. Hash with caution.
	 *
	 * @see this{@link #equals(Object)}
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 0;
		for (int docID: this.data.keySet()) {
			result += (prime * this.data.get(docID).hashCode());
		}
		return result;
	}
}