package machineLearningTools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A tree class for storing decision trees, with pointers
 * to parents and children
 *
 * @author T.J. Trimble
 */
public class Tree {

	private Tree parent;
	private String feature;
	private int depth; // TODO: Make final
	private final ArrayList<Tree> children;
	private final Collection<Integer> docIDs;
	private final Data data;

	/**
	 * Create a top-level tree, assigning the feature to *TOP*
	 * and setting up the document IDs and Data object appropriately.
	 *
	 * @param data
	 * @author T.J. Trimble
	 */
	public Tree(Data data) throws IllegalArgumentException {
		if (data == null) {
			throw new NullPointerException();
		}
		this.feature = "*TOP*";
		this.children = new ArrayList<Tree>();
		this.docIDs = data.getIDs();
		this.data = data;
		this.depth = 0;
	}

	/**
	 * Create a Tree object with the given feature and docIDs referring
	 * to the given Data object.
	 *
	 * @param feature
	 * @param docIDs
	 * @param depth
	 * @param data
	 */
	public Tree(String feature, Collection<Integer> docIDs, int depth, Data data) {
		if (feature == null || docIDs == null || data == null) {
			throw new NullPointerException();
		}
		this.feature = feature;
		this.docIDs = docIDs;
		this.depth = depth;
		this.data = data;
		this.children = new ArrayList<Tree>();
	}

	/**
	 * Create a Tree object from the given JSON referencing
	 * the given Data object.
	 *
	 * @param jsonObject
	 * @author T.J. Trimble
	 */
	public Tree(final JSONObject jsonObject, final Data data) throws IllegalArgumentException {
		if (jsonObject == null || data == null) {
			throw new NullPointerException();
		}
		// Initializations
		this.data = data;
		this.docIDs = new HashSet<Integer>();
		this.children = new ArrayList<Tree>();
		//// Read JSON
		// Read in attributes
		try {
			if (jsonObject.has("feature")) {
				this.feature = jsonObject.getString("feature");
			}
			else {
				throw new IllegalArgumentException("feature field not found in Tree JSON;");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			if (jsonObject.has("docIDs")) {
				JSONArray docIDsJSON = jsonObject.getJSONArray("docIDs");
				for (int i=0; i < docIDsJSON.length(); i++) {
					this.docIDs.add(docIDsJSON.getInt(i));
				}
			}
			else {
				throw new IllegalArgumentException("docIDs field not found in Tree JSON;");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			if (jsonObject.has("depth")) {
				this.depth = jsonObject.getInt("depth");
			}
			else {
				throw new IllegalArgumentException("depth field not found in Tree JSON;");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// Read in children
		try {
			if (jsonObject.has("children")) {
				JSONArray childrenJSON = jsonObject.getJSONArray("children");
				for (int i=0; i < childrenJSON.length(); i++) {
					this.children.add(new Tree(childrenJSON.getJSONObject(i), this.data));
				}
			}
			else {
				throw new IllegalArgumentException("children field not found in Tree JSON;");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Link the given Tree object child to this Tree object such that
	 * the child is the child of this object and this object is the
	 * child's parent.
	 *
	 * @param child
	 */
	public void addChild(Tree child) {
		this.children.add(child);
		child.setParent(this);
	}

	/**
	 * Initiate a recursive, depth-first search of the tree
	 * to return an ArrayList of ArrayLists with the path
	 * of features from the *TOP* to each leaf.
	 *
	 * If node does not have any children, this returns empty.
	 *
	 * @return
	 */
	public ArrayList<Rule> getRules() {
		if (this.getChildren().size() == 0) {
			return new ArrayList<Rule>();
		}
		ArrayList<Rule> paths = new ArrayList<Rule>();
		ArrayList<String> currentPath = new ArrayList<String>();
		return this.getRules(paths, currentPath);
	}

	/**
	 * Recursive function to calculate the paths down
	 * the decision tree. Leaves are represented as a Rule
	 * object, with the path as an ArrayList and a HashMap
	 * of the probability of each label.
	 *
	 * @param paths
	 * @param currentPath
	 * @return
	 */
	private ArrayList<Rule> getRules(ArrayList<Rule> paths, ArrayList<String> currentPath) {
		currentPath.add(this.feature);
		if (this.children.size() == 0) {
			currentPath.remove(0); // remove *TOP*
			Counter<String> labelCounts = new Counter<String>();
			HashMap<String, Double> labelProbabilities = new HashMap<String, Double>();
			Double labelCount = (double) this.docIDs.size();
			for (Integer docID: this.docIDs) {
				labelCounts.increment(this.data.getLabel(docID));
			}
			for (String label: labelCounts.keySet()) {
				labelProbabilities.put(label, labelCounts.get(label)/labelCount);
			}
			Rule result = new Rule(currentPath, labelProbabilities, this.docIDs.size());
			paths.add(result);
		}
		else {
			for (Tree child: this.children) {
				if (child != null) {
					ArrayList<String> newPath = new ArrayList<String>(currentPath);
					paths = child.getRules(paths, newPath);
				}
			}
		}
		return paths;
	}

	// Getters

	/**
	 * @return the parent Tree object of the current Tree
	 */
	public Tree getParent() {
		return this.parent;
	}

	/**
	 * @return the ArrayList object containing each child Tree
	 * of the current Tree
	 */
	public ArrayList<Tree> getChildren() {
		return this.children;
	}

	/**
	 * Return the current Tree's feature
	 * @return the current Tree's feature
	 */
	public String getFeature() {
		return this.feature;
	}

	/**
	 * Return the current Tree's doc IDs
	 * @return the current Tree's doc IDs
	 */
	public Collection<Integer> getDocIDs() {
		return this.docIDs;
	}

	/**
	 * Return the current Tree's depth as set by the constructor
	 *
	 * @return the current Tree's depth
	 */
	public Integer getDepth() {
		return this.depth;
	}

	/**
	 * Return the current Tree's Data object
	 * @return the current Tree's Data object
	 */
	public Data getData() {
		return this.data;
	}

	// Setters

	/**
	 * Set the parent of the current Tree object. Used
	 * in addChild();
	 *
	 * @param tree
	 */
	void setParent(Tree tree) {
		if (tree == null) {
			throw new NullPointerException();
		}
		if (this.parent != null) {
			throw new IllegalArgumentException("Setting a parent to a Tree object that already has a parent;");
		}
		this.parent = tree;
	}

	// Basic methods

	@Override
	public boolean equals(Object obj) {
		//// Basic checks
		if (obj == this) {
			return true;
		}
		if (obj == null || (obj.getClass() != this.getClass())) {
			return false;
		}
		Tree otherTree = null;
		try {
			otherTree = (Tree)obj;
		}
		catch (ClassCastException e) {
			return false;
		}
		//// Core tests
		if (!otherTree.getFeature().equals(this.getFeature())) {
			return false;
		}
		if (!otherTree.getDocIDs().equals(this.getDocIDs())) {
			return false;
		}
		// This should recursively check all of the children
		if (!otherTree.getChildren().equals(this.getChildren())) {
			return false;
		}
		if (!otherTree.getData().equals(this.getData())) {
			return false;
		}
		return true;
	}

	/*
	 * toString() builds String representation in JSON format for
	 * this Tree and all of its children
	 */
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		// Add feature
		stringBuilder.append(String.format("{\"feature\":\"%s\",", this.feature));
		// Add document IDs
		stringBuilder.append("\"docIDs\":[");
		int i = 1;
		for (Integer docID: this.docIDs) {
			stringBuilder.append("\""+docID+"\"");
			if (i < this.docIDs.size()) {
				stringBuilder.append(",");
				i++;
			}
		}
		stringBuilder.append("],\"children\":[");
		// Recursively add strings for children
		int counter = 1;
		for (Tree tree: this.children) {
			stringBuilder.append(tree.toString());
			if (counter < this.children.size()) {
				stringBuilder.append(",");
			}
			counter++;
		}
		stringBuilder.append("]}"); // close children and Tree
		return stringBuilder.toString();
	}

//	/**
//	 * Create a top-level tree, assigning the feature to *TOP*, setting
//	 * the parent to NULL, and setting up the document IDs and Data object
//	 * appropriately. Only one top-level tree can be constructed.
//	 *
//	 * @param docCollection
//	 * @author T.J. Trimble
//	 */
//	public Tree(Data data) throws IllegalArgumentException {
//		if (Tree.rooted == true) {
//			throw new IllegalArgumentException("Trying to construct a second root Tree object. Fatal error;");
//		}
//		if (data == null) {
//			throw new NullPointerException();
//		}
//		this.parent = null;
//		this.feature = "*TOP*";
//		this.children = new ArrayList<Tree>();
//		this.docIDs = data.getIDs();
//		this.data = data;
//		Tree.rooted = true;
//	}
}
