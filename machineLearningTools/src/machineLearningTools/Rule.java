package machineLearningTools;

import static machineLearningTools.Util.sortedKeysByValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Store label and path of features, along with probabilities for
 * other labels. The probabilities must sum to 1. Match the rule to
 * a document with the accept() method. <br>
 * Includes methods for formatting to a String the path & probabilities.
 *
 * @author T.J. Trimble
 */
public class Rule implements Comparable<Rule> {

	private List<String> path;
	private String maxLabel;
	private Integer docCount;
	private Map<String, Double> probabilities;
	private Map<String, Double> probabilitiesForAllLabels;
	private Double maximum;

	private final static HashSet<String> allLabels = new HashSet<String>();
	private final static Double threshold = 0.0001d;

	/**
	 * Rule constructor to make a new Rule object.
	 *
	 * Path is ordered set of feature specifications (represented as
	 * a List), where a feature with "!" prepended indicates the rule
	 * matches if the tested feature set does not include that feature,
	 * and vice versa.
	 *
	 * Probabilities is a Map from label to probability. The values
	 * of this map must sum to 1. In the output representation, every
	 * label added to a Rule is represented with missing labels
	 * represented with a zero probability.
	 *
	 * docCount is the number of documents associated with this rule
	 * in the training data. This is not used in any calculations, but
	 * rather is for reference only.
	 *
	 * Preconditions:
	 *  each parameter must be non-null;
	 *  path must be of at least length 1;
	 *  docCount must be non-negative;
	 *  probabilities must contain at least one label and sum to 1;
	 *    each value of probabilities must be non-negative;
	 *
	 * @param path
	 * @param probabilities
	 * @param docCount
	 * @throws IllegalArgumentException
	 * @author T.J. Trimble
	 */
	public Rule(List<String> path, Map<String, Double> probabilities, Integer docCount) throws IllegalArgumentException {
		// Check preconditions
		if (path == null || probabilities == null || docCount == null) {
			throw new NullPointerException();
		}
		if (path.size() < 1) {
			throw new IllegalArgumentException("Path parameter must contain at least one feature;");
		}
		if (docCount < 0) {
			throw new IllegalArgumentException("DocCount parameter must be at least 0;");
		}
		if (probabilities.keySet().size() < 1) {
			throw new IllegalArgumentException("Probabilities parameter must contain at least one label;");
		}
		Double probabilitiesSum = 0.0d;
		for (Double value: probabilities.values()) {
			if (value < 0 || value > 1) {
				throw new IllegalArgumentException("Values of the probabilities parameter must be between 0 and 1;");
			}
			probabilitiesSum += value;
		}
		if ((1.0d - probabilitiesSum) > Rule.threshold) {
			throw new IllegalArgumentException("Values of probabilities parameter must sum to 1;");
		}
		// Assign values
		this.path = path;
		this.probabilities = probabilities;
		this.docCount = docCount;
		Rule.allLabels.addAll(this.probabilities.keySet());
		// Get maximum probability
		this.maximum = 0.0d;
		Double current;
		for (String key: this.probabilities.keySet()) {
			current = probabilities.get(key);
			if (current != null && current > this.maximum) {
				this.maxLabel = key;
				this.maximum = this.probabilities.get(key);
			}
		}
	}

	public Rule(final JSONObject jsonObject) {
		if (jsonObject == null) {
			throw new NullPointerException();
		}
		//// Read JSON
		// Get path
		JSONArray values;
		String feature;
		if (jsonObject.has("path")) {
			this.path = new ArrayList<String>();
			try {
				values = jsonObject.getJSONArray("path");
				for (int i=0; i < values.length(); i++) {
					feature = values.getString(i);
					if (feature != null) {
						this.path.add(feature);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		// Get probabilities
		JSONObject json;
		String label;
		Double probability;
		if (jsonObject.has("probabilities")) {
			this.probabilities = new HashMap<String, Double>();
			try {
				json = jsonObject.getJSONObject("probabilities");
				if (json.names() != null) {
					for (int i=0; i < json.names().length(); i++) {
						label = json.names().getString(i);
						probability = json.getDouble(label);
						if (label != null && probability != null) {
							this.probabilities.put(label, probability);
						}
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if (jsonObject.has("docCount")) {
			try {
				this.docCount = jsonObject.getInt("docCount");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		Rule.allLabels.addAll(this.probabilities.keySet());
		// Get maximum probability
		this.maximum = 0.0d;
		Double current;
		for (String key: this.probabilities.keySet()) {
			current = this.probabilities.get(key);
			if (current != null && current > this.maximum) {
				this.maxLabel = key;
				this.maximum = this.probabilities.get(key);
			}
		}
	}

	/**
	 * Shortcut to accept Document object's features
	 *
	 * @param document
	 * @see accepts(HashSet features)
	 * @return
	 */
	public boolean accepts(Document document) {
		return this.accepts(document.getFeatures());
	}

	/**
	 * accepts() returns whether the current Rule object accepts
	 * the given Collection or not. accepts() returns true iff
	 * all the positive features specified in the rule are present
	 * and all the negative features are not present. This method uses
	 * the contains() method of the Collection, so for large amounts of
	 * features, it is best to use a hashed collection.
	 *
	 * @param features
	 * @return
	 */
	public boolean accepts(Collection<String> features) {
		Boolean matched = true;
		// Rule matches if no feature broken
		for (String feature: this.getPath()) {
			// If rule has positive feature, rule does not match if
			// feature not in Document
			if ((!feature.startsWith("!") && !features.contains(feature))) {
				matched = false;
				break;
			}
			// If rule has negative feature, rule does not match if
			// feature in Document
			if ((feature.startsWith("!") && features.contains(feature.substring(1)))) {
				matched = false;
				break;
			}
		}
		return matched;
	}

	/**
	 * Returns a string representation of the probabilities associated
	 * with this Rule object by joining the label and its associated
	 * probability with spaces.
	 *
	 * @return
	 */
	public String getFormattedProbabilities() {
		// Initializations
		StringBuilder result = new StringBuilder();
		// Sort probabilities (high->low)
		ArrayList<String> sortedKeys = sortedKeysByValue(this.getProbabilities(), true);
		// Format string
		Double probability;
		for (String label: sortedKeys) {
			probability = this.probabilities.get(label);
			probability = probability != null ? probability : 0.0d;
			result.append(String.format("%s %s ", label, probability));
		}
		return result.toString().trim();
	}

	/**
	 * Returns probabilities associated with this Rule object
	 *
	 * @return probabilities
	 */
	public Map<String, Double> getProbabilities() {
		if (this.probabilitiesForAllLabels == null) {
			this.probabilitiesForAllLabels = new HashMap<String, Double>(this.probabilities);
			for (String label: Rule.allLabels) {
				if (!this.probabilitiesForAllLabels.containsKey(label)) {
					this.probabilitiesForAllLabels.put(label, 0.0d);
				}
			}
		}
		return this.probabilitiesForAllLabels;
	}

	/**
	 * Returns the label with the highest probability for this Rule object
	 *
	 * @return the label with the highest probability
	 */
	public String getLabel() {
		return this.maxLabel;
	}

	/**
	 * Returns the specified docCount for this object.
	 *
	 * @return the specified docCount for this object.
	 */
	public Integer getDocCount() {
		return this.docCount;
	}

	/**
	 * Returns the specified path for this object.
	 * For testing.
	 *
	 * @return the specified path for this object.
	 */
	List<String> getPath() {
		return this.path;
	}

	/**
	 * Return the maximum probability associated with this rule.
	 * For testing.
	 *
	 * @return the maximum probability associated with this rule.
	 */
	Double getMaxProb() {
		return this.maximum;
	}

	/**
	 * Return the JSON representation of this Rule object
	 * {"path":["XYZ", "XYZ", "XYZ"], "docCount":"XYZ", "probabilities":{"XYZ":"1","ABC":"1"}}
	 *
	 * @return the JSON representation of this Rule object
	 */
	public String toJSON() {
		int i;
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("{");
		// add path
		stringBuilder.append("\"path\":[");
		i = 1;
		for (String feature: this.path) {
			stringBuilder.append(String.format("\"%s\"", feature));
			if (i < this.path.size()) {
				stringBuilder.append(",");
			}
			i++;
		}
		stringBuilder.append("],");
		// add docCount
		stringBuilder.append(String.format("\"docCount\":\"%s\",", this.docCount));
		// add probabilities
		stringBuilder.append("\"probabilities\":{");
		i = 1;
		for (String label: this.probabilities.keySet()) {
			stringBuilder.append(String.format("\"%s\":\"%s\"", label, this.probabilities.get(label)));
			if (i < this.probabilities.keySet().size()) {
				stringBuilder.append(",");
			}
			i++;
		}
		stringBuilder.append("}}");
		return stringBuilder.toString();
	}

	// Basic methods

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		for (int i=0; i < this.path.size(); i++) {
			result.append(this.path.get(i));
			if (i != this.path.size()-1) {
				result.append("&");
			}
		}
		return result.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		Rule other = (Rule) obj;
		if (this.docCount == null) {
			if (other.docCount != null) {
				return false;
			}
		} else if (!this.docCount.equals(other.docCount)) {
			return false;
		}
		if (this.maxLabel == null) {
			if (other.maxLabel != null) {
				return false;
			}
		} else if (!this.maxLabel.equals(other.maxLabel)) {
			return false;
		}
		if (this.maximum == null) {
			if (other.maximum != null) {
				return false;
			}
		} else if (!this.maximum.equals(other.maximum)) {
			return false;
		}
		if (this.path == null) {
			if (other.path != null) {
				return false;
			}
		} else if (!this.path.equals(other.path)) {
			return false;
		}
		if (this.probabilities == null) {
			if (other.probabilities != null) {
				return false;
			}
		} else if (!this.probabilities.equals(other.probabilities)) {
			return false;
		}
		return true;
	}

	/**
	 * Rules are sorted by their String representations alphabetically,
	 * resulting in rules being grouped similarly.
	 *
	 * @param otherRule
	 * @return
	 */
	@Override
	public int compareTo(Rule otherRule) {
		return this.toString().compareTo(otherRule.toString());
	}

	// Static methods

	/**
	 * Return all labels specified for all Rule objects so far.
	 * For testing.
	 *
	 * @return all labels specified for all Rule objects so far.
	 */
	static HashSet<String> getAllLabels() {
		return Rule.allLabels;
	}
}
