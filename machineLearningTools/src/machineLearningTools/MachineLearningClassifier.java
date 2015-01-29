package machineLearningTools;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Classifier interface defines basic classifier
 * methods to be implemented for any given classifier
 *
 * @author T.J. Trimble
 */
public interface MachineLearningClassifier {

	// Public methods

	/**
	 * @author T.J. Trimble <br>
	 *
	 * train(String) should take in a data filename, load the data,
	 * train the appropriate model, and output the model file.
	 *
	 * @param trainingDataFileName
	 */
	public void train(String trainingDataFileName);

	/**
	 * @author T.J. Trimble <br>
	 *
	 * train(BufferedReader) should take a BufferedReader object
	 * and read in a modelFile in substitution for training.
	 *
	 * @param modelFile
	 */
	public void train(BufferedReader modelFile);

	/**
	 * @author T.J. Trimble <br>
	 *
	 * test() should take in a data filename, load the data,
	 * classify() each data instance, and execute outputResults()
	 *
	 * @param testingDataFileName
	 * @param testingLabel
	 */
	public void test(String testingDataFileName, String testingLabel);

	/**
	 * @author T.J. Trimble <br>
	 *
	 * classify() should execute the classification algorithm
	 * based on the model created in train(). classify() takes in
	 * a testingData object and should use setSysOutput() to
	 * set the system output for each Document object.
	 *
	 * Classify is a function in order to enable classifiers to
	 * take in multiple data sets (such as train, development,
	 * and test) and then classify each one.
	 *
	 * @param testingData
	 * @return
	 */
	public void classify(Data testingData);

	// Package methods
	/**
	 * @author T.J. Trimble <br>
	 *
	 * outputResults() saves the of the designated Data object to
	 * a file specified in the classifier constructor and prints
	 * a confusion matrix with the trainOrTest label.
	 *
	 * @param testResult
	 * @param trainOrTest
	 * @throws IOException
	 */
	void outputResults(Data testResult, String trainOrTest);

}
