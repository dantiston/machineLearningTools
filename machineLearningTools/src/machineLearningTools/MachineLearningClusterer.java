package machineLearningTools;

import java.io.IOException;

/**
 * Clusterer abstract class defines basic clusterer
 * methods to be implemented for any given clusterer.
 *
 * @author T.J. Trimble
 */
public abstract class MachineLearningClusterer {

	// Members

	protected final boolean binarized;

	// Constructors

	/**
	 * Basic constructor specifies the global binarized attribute.
	 *
	 * @param binarized
	 * @author T.J. Trimble
	 */
	public MachineLearningClusterer(boolean binarized) {
		this.binarized = binarized;
	}

	// Public methods

	/**
	 * MachineLearningClusterer#cluster() should execute
	 * the clustering algorithm on the testingData data
	 * object parameter. MachineLearningClusterer#cluster() takes in
	 * a testingData object and should use setSysOutput() to
	 * set the system output for each Document object.
	 *
	 * MachineLearningClusterer#cluster() is a function in
	 * order to enable classifiers to take in multiple data
	 * sets (such as train, development, and test) and then
	 * classify each one.
	 *
	 * @param testingData
	 * @author T.J. Trimble
	 */
	public abstract void cluster(Data testingData);

	/**
	 * MachineLearningClusterer#test() should take in a data filename,
	 * load the data, cluster() each data instance, and execute
	 * outputResults()
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
		this.cluster(testData);
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
