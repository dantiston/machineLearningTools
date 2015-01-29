package machineLearningClassifiers.KNNClassifier;

import java.io.BufferedReader;

import machineLearningTools.Data;
import machineLearningTools.MachineLearningClassifier;

/**
 * KNNClassifier
 *
 * KNNClassifier has methods for classifying and testing,
 *   as well as generating output documents <br>
 *
 * <b>Input:</b> <br>
 * 	<b>Data:</b> training, testing <br>
 * 	<b>Parameters:</b> <br><br>
 *   	<b>K:</b> number of neighbors to consider when classifying <br>
 *      <b>simFunction:</b>
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
public class KNNClassifier implements MachineLearningClassifier {

	/**
	 *
	 * @author T.J. Trimble
	 */
	public KNNClassifier(Integer k, String simFunction) {

	}

	/**
	 *
	 *
	 * @see machineLearningTools.MachineLearningClassifier#train(java.lang.String)
	 */
	@Override
	public void train(String trainingDataFileName) {
		return;
	}

	/**
	 *
	 *
	 * @see machineLearningTools.MachineLearningClassifier#train(java.io.BufferedReader)
	 */
	@Override
	public void train(BufferedReader modelFile) {
		return;
	}

	/**
	 *
	 *
	 * @see machineLearningTools.MachineLearningClassifier#test(java.lang.String, java.lang.String)
	 */
	@Override
	public void test(String testingDataFileName, String testingLabel) {

	}

	/**
	 *
	 *
	 * @see machineLearningTools.MachineLearningClassifier#classify(machineLearningTools.Data)
	 */
	@Override
	public void classify(Data testingData) {

	}

	/**
	 *
	 *
	 * @see machineLearningTools.MachineLearningClassifier#outputResults(machineLearningTools.Data, java.lang.String)
	 */
	@Override
	public void outputResults(Data testResult, String trainOrTest) {

	}

}
