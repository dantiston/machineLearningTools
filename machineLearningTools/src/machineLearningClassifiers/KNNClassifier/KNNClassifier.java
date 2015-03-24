package machineLearningClassifiers.KNNClassifier;

import static machineLearningTools.Util.sortedKeysByValue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import machineLearningTools.ConfusionMatrix;
import machineLearningTools.CosineSimilarity;
import machineLearningTools.Data;
import machineLearningTools.Document;
import machineLearningTools.EuclideanDistance;
import machineLearningTools.MachineLearningClassifier;
import machineLearningTools.SymmetricMeasure;

/**
 * KNNClassifier
 *
 * KNNClassifier has methods for classifying and testing,
 *   as well as generating output documents <br>
 *
 * KNNClassifier assumes the similarity function is symmetrical,
 *   that is, f(A, B) == f(B, A)
 *
 * <b>Input:</b> <br>
 * 	<b>Data:</b> training, testing <br>
 * 	<b>Parameters:</b> <br><br>
 *   	<b>K:</b> number of neighbors to consider when classifying <br>
 *      <b>simFunction:</b><br>
 *      	[1,e,[Ee]uclidean] Use Euclidean distance as the measure between vectors <br>
 *      	[2,c,[Cc]osine] Use Cosine similarity as the measure between vectors <br>
 *   	<b>sysOutput:</b> filename to output system output containing
 *   		a sorted list of instances with label to probabilities <br>
 * <b>Output:</b> <br>
 * 	<b>stdout:</b> confusion matrices over training and testing data <br>
 * 	<b>Files:</b> <br>
 *   	<b>sysOutput:</b> file containing a sorted list of instances
 *   		with label to probabilities mappings <br><br>
 *
 * This classifier implements the MachineLearningClassifier interface
 *
 * @author T.J. Trimble
 */
public class KNNClassifier extends MachineLearningClassifier {

	private Data trainingData;

	private final String simFunction;
	private final Integer Kvalue;
	private final String sysOutputFile;
	private HashMap<Document, SymmetricMeasure> distances;

	private static final List<String> euclideanOptions = Arrays.asList(new String[]{"1","e","euclidean"});
	private static final List<String> cosineOptions = Arrays.asList(new String[]{"2","c","cosine"});

	/**
	 * Construct a KNNClassifier object with the given parameters.
	 *
	 * <b>Parameters:</b> <br><br>
	 *   	<b>K:</b> number of neighbors to consider when classifying <br>
	 *      <b>simFunction:</b><br>
	 *      	[1,e,[Ee]uclidean] Use Euclidean distance as the measure between vectors <br>
	 *      	[2,c,[Cc]osine] Use Cosine similarity as the measure between vectors <br>
 	 *   	<b>sysOutput:</b> filename to output system output containing
 	 *   		a sorted list of instances with label to probabilities <br>
	 *
	 * @author T.J. Trimble
	 * @param sysOutputFile
	 */
	public KNNClassifier(Integer Kvalue, String simFunction, String sysOutputFile, boolean binarized) {
		super(binarized);
		this.Kvalue = Kvalue;
		this.sysOutputFile = sysOutputFile;
		this.simFunction = simFunction.toLowerCase();
		// Verify similarity function
		if (!(KNNClassifier.euclideanOptions.contains(simFunction) || KNNClassifier.cosineOptions.contains(simFunction))) {
			throw new IllegalArgumentException("simFunction parameter of KNNClassifier must be one of {1,E,e,[Ee]uclidean,2,C,e,[Cc]osine}");
		}
	}

	/**
	 * Construct a KNNClassifier object with the given parameters.
	 *
	 * <b>Parameters:</b> <br><br>
	 *   	<b>K:</b> number of neighbors to consider when classifying <br>
	 *      <b>simFunction:</b><br>
	 *      	[1,e,[Ee]uclidean] Use Euclidean distance as the measure between vectors <br>
	 *      	[2,c,[Cc]osine] Use Cosine similarity as the measure between vectors <br>
 	 *   	<b>sysOutput:</b> filename to output system output containing
 	 *   		a sorted list of instances with label to probabilities <br>
	 *
	 * @author T.J. Trimble
	 * @param sysOutputFile
	 */
	public KNNClassifier(Integer Kvalue, String simFunction, String sysOutputFile) {
		this(Kvalue, simFunction, sysOutputFile, false);
	}

	/**
	 * Classify each document in the given testingData by majority vote
	 * of the K most similar documents in the training data.
	 *
	 * @see machineLearningTools.MachineLearningClassifier#classify(machineLearningTools.Data)
	 */
	@Override
	public void classify(Data testingData) {
		// Initializations
		List<Document> topK;
		Integer mapSize = testingData.size();
		// Classify documents
		for (Document documentToClassify: testingData.getDocs()) {
			this.distances = new HashMap<Document, SymmetricMeasure>(mapSize);
			for (Document documentToCompare: this.trainingData.getDocs()) {
				if (documentToClassify.getDocID() == documentToCompare.getDocID()) {
					continue; // Don't compare document to itself
				}
				// Calculate distances
				this.distances.put(documentToCompare, this.getDistance(documentToClassify, documentToCompare));
			}
			// Sort distances and get K nearest neighbors
			topK = sortedKeysByValue(this.distances).subList(0, this.Kvalue);
			documentToClassify.setSysOutput(topK);
		}
	}

	/**
	 * Factory-style method to return the proper DistanceMeasure object
	 * based on the Constructor specifications.
	 *
	 * @param documentToClassify
	 * @param documentToCompare
	 * @return Properly subtype of DistanceMeasure
	 */
	private SymmetricMeasure getDistance(Document documentToClassify, Document documentToCompare) {
		if (KNNClassifier.euclideanOptions.contains(this.simFunction)) {
			return new EuclideanDistance(documentToClassify, documentToCompare);
		}
		else if (KNNClassifier.cosineOptions.contains(this.simFunction)) {
			return new CosineSimilarity(documentToClassify, documentToCompare);
		}
		else {
			throw new IllegalArgumentException("KNNClassifier object not constructed properly!");
		}
	}

	/**
	 * Prints a confusion matrix of the system results to stdout.
	 *
	 * @see machineLearningTools.MachineLearningClassifier#outputResults(machineLearningTools.Data, java.lang.String)
	 */
	@Override
	public void outputResults(Data testResult, String trainOrTest) {
		if (testResult == null || trainOrTest == null) {
			throw new NullPointerException();
		}
		try {
			BufferedWriter sysOutput = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.sysOutputFile), "utf-8"));
			this.writeSysOutput(testResult, sysOutput);
			sysOutput.close();
		} catch (IOException e) {
			System.err.println("Failed to write results at KNNClassifier#outputResults(testResult, trainOrTest). Check your system output filename and system setup.");
			e.printStackTrace();
		}
		// Output confusion matrix to stdout
		System.out.println(new ConfusionMatrix(testResult, trainOrTest));
	}

	/**
	 * Writes the system output for each vector.
	 *
	 * @param testResult
	 * @param sysOutput
	 * @throws IOException
	 */
	private void writeSysOutput(Data testResult, BufferedWriter sysOutput) throws IOException {
		if (testResult == null || sysOutput == null) {
			throw new NullPointerException();
		}
		sysOutput.write(testResult.getFormattedSystemOutput());
	}

	/**
	 * Set the training data for this KNNClassifier object to use in testing.
	 * Note KNNClassifier does not train a model.
	 *
	 * @see machineLearningTools.MachineLearningClassifier#train(java.lang.String)
	 */
	@Override
	public void train(String trainingDataFileName) {
		this.trainingData = this.getData(trainingDataFileName);
	}

	/**
	 * Because KNNClassifier does not train a model, this method is not
	 * supported.
	 *
	 * @see machineLearningTools.MachineLearningClassifier#train(java.io.BufferedReader)
	 */
	@Override
	public void train(BufferedReader modelFile) {
		throw new UnsupportedOperationException("Because KNNClassifier does not train a model, KNNClassifier#train(modelFile) this method is not supported.");
	}

}
