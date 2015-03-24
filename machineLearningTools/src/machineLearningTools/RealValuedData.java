package machineLearningTools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Data <br>
 *
 * Sentences with associated IDs <br><br>
 * Specifically, an unordered collection of labeled documents,
 * where each document contains a label and an unordered mapping
 * of words/features and their associated counts.<br><br>
 *
 * Includes methods for reading data from a file and accessing
 * documents by document ID
 *
 * @author T.J. Trimble
 ** *********************************************************/

public class RealValuedData extends Data {

	public RealValuedData(final String trainingDataFileName) {
		super(trainingDataFileName);
	}

	public RealValuedData(JSONObject trainingDataJSON) {
		super(trainingDataJSON);
	}

	RealValuedData() {
		super();
	}

	/**
	 * Read in a file of documents,
	 * each specified with a label and a set of features
	 *
	 * @param dataFileName
	 */
	@Override
	protected HashMap<Integer, Document> readDataFromFile(final String dataFileName) {
		Document.initialize();
		HashMap<Integer, Document> result = new HashMap<Integer, Document>();
		String lineString;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(dataFileName));
			RealValuedDocument doc;
			while ((lineString = reader.readLine()) != null) {
				doc = new RealValuedDocument(lineString);
				result.put(doc.getDocID(), doc);
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
	 *
	 * @param trainingDataJSON
	 */
	@Override
	protected HashMap<Integer, Document> readDataFromJSON(final JSONObject json) {
		Document.initialize();
		HashMap<Integer, Document> result = new HashMap<Integer, Document>();
		String key;
		RealValuedDocument doc;
		for (int i=0; i<json.names().length(); i++) {
			try {
				key = json.names().getString(i);
				doc = new RealValuedDocument(json.getJSONObject(key), key);
				result.put(doc.getDocID(), doc);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
}