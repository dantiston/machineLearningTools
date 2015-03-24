package machineLearningTools;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Classifier abstract class defines basic classifier
 * methods to be implemented for any given classifier.
 *
 * @author T.J. Trimble
 */
public abstract class MachineLearningClassifier {

	// Members

	protected final boolean binarized;

	// Constructors

	/**
	 * Basic constructor specifies the global binarized attribute.
	 *
	 * @param binarized
	 * @author T.J. Trimble
	 */
	public MachineLearningClassifier(boolean binarized) {
		this.binarized = binarized;
	}

	// Public methods

	/**
	 * MachineLearningClassifier#train(String) should take in
	 * a data filename, load the data, train the appropriate model,
	 * and output the model file.
	 *
	 * @param trainingDataFileName
	 * @author T.J. Trimble
	 */
	public abstract void train(String trainingDataFileName);

	/**
	 * train(BufferedReader) should take a BufferedReader object
	 * and read in a modelFile in substitution for training.
	 *
	 * @param modelFile
	 * @author T.J. Trimble
	 */
	public void train(BufferedReader modelFile) {

	}

	/**
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
	 * @author T.J. Trimble
	 */
	public abstract void classify(Data testingData);

	/**
	 * test() should take in a data filename, load the data,
	 * classify() each data instance, and execute outputResults()
	 *
	 * @param testingDataFileName
	 * @param testingLabel
	 * @author T.J. Trimble
	 */
	public void test(String testingDataFileName, String testingLabel) {
		if (testingDataFileName == null || testingLabel == null) {
			throw new NullPointerException();
		}
		Data testData = this.getData(testingDataFileName);
		this.classify(testData);
		this.outputResults(testData, testingLabel);
	}

	// Protected methods
	/**
	 * outputResults() saves the of the designated Data object to
	 * a file specified in the classifier constructor and prints
	 * a confusion matrix with the trainOrTest label.
	 *
	 * @param testResult
	 * @param trainOrTest
	 * @throws IOException
	 * @author T.J. Trimble
	 */
	protected abstract void outputResults(Data testResult, String trainOrTest);

	/**
	 * getData() returns a Data object according to the constructor
	 * specifications.
	 *
	 * @param testingDataFileName
	 * @return
	 */
	protected Data getData(final String testingDataFileName) {
		if (this.binarized) {
			return new BinaryValuedData(testingDataFileName);
		}
		else {
			return new RealValuedData(testingDataFileName);
		}
	}
}
