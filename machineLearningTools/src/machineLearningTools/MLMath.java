package machineLearningTools;

import java.util.Collection;
import java.util.HashMap;

/**
 * Collection of basic mathematical functions for Machine Learning
 *
 * @author T.J. Trimble
 */
public class MLMath {

	// Logarithms

	private static HashMap<Integer, Double> logs = new HashMap<Integer, Double>();
	// Initiate log(2) automatically
	static {
		MLMath.logs.put(2, Math.log(2));
	}
	public static double log2 = MLMath.logs.get(2);

	/**
	 * @author T.J. Trimble
	 *
	 * Calculate log with a given base
	 *
	 * @param value
	 * @param base
	 * @return
	 */
	public static double log(double value, int base) throws ArithmeticException {
		if (value <= 0 || base <= 0) {
			throw new ArithmeticException();
		}
		if (!MLMath.logs.containsKey(base)) {
			MLMath.logs.put(base, Math.log(base));
		}
		// Tests if integer
		return (Math.log(value)/MLMath.logs.get(base));
	}

	/**
	 * @author T.J. Trimble
	 *
	 * Calculate log base 2
	 *
	 * @param value
	 * @return
	 */
	public static double log(Double value) {
		return MLMath.log(value, 2);
	}

	// Comparisons

	public static double threshold = 0.000001d;

	/**
	 * Returns true if value1 is almost equal to value2. <br>
	 * Specifically, returns true iff abs(value1 - value2) > 0.000001
	 *
	 * @param value1
	 * @param value2
	 * @return
	 */
	public static boolean pseudoEqual(double value1, double value2) {
		return Math.abs(value1 - value2) < threshold;
	}

	// Information Gain

	/**
	 * Calculate information gain with given parameters
	 *
	 * Information Gain (I): <br>
	 * I(Y | X) = H(Y) - H(Y|X) -> <br>
	 * H(Y) - AverageH(X,Y) -> <br>
	 * H(Y) - ((H(X1)*(len(X1)/len(X))+(H(X2)*(len(X2)/len(X)))) -> <br>
	 * H(Y) - (((H(X1)*len(X1))+(H(X2)*len(X2)))/len(X)) <br><br>
	 *
	 * @author T.J. Trimble
	 * @param withEnt
	 * @param withSize
	 * @param withOutEnt
	 * @param withOutSize
	 * @param topEnt
	 *
	 * @return Double representation of information gain
	 */
	public static double informationGain(double withEnt, double withSize, double withOutEnt, double withOutSize, double topEnt) {
		double gain = 0.0d;
		double topSize = withSize + withOutSize;
		if ((topSize > 0) && (withSize > 0) && (withOutSize > 0)) {
			gain = (topEnt - (((withEnt*withSize) + (withOutEnt*withOutSize))/topSize));
		}
		return gain;
	}

	/**
	 * Shortcut to cast size parameters to Doubles
	 *
	 * @author T.J. Trimble
	 * @param withEnt
	 * @param withSize
	 * @param withOutEnt
	 * @param withOutSize
	 * @param topEnt
	 * @return
	 */
	public static double informationGain(double withEnt, int withSize, double withOutEnt, int withOutSize, double topEnt) {
		return informationGain(withEnt, (double)withSize, withOutEnt, (double)withOutSize, topEnt);
	}

	/**
	 * @author T.J. Trimble
	 *
	 * Shortcut to calculate entropy of splits automatically.
	 *
	 * @param docIDsWithFeature
	 * @param docIDsWithOutFeature
	 * @param data
	 * @return
	 */
	public static double informationGain(Collection<Integer> docIDsWithFeature, Collection<Integer> docIDsWithOutFeature, double topEnt, Data data) {
		return informationGain(data.getEntropy(docIDsWithFeature), docIDsWithFeature.size(), data.getEntropy(docIDsWithOutFeature), docIDsWithOutFeature.size(), topEnt);
	}

	/**
	 * @author T.J. Trimble
	 *
	 * Shortcut to calculate entropy of splits automatically.
	 *
	 * @param docIDsWithFeature
	 * @param docIDsWithOutFeature
	 * @param data
	 * @return
	 */
//	public static double informationGain(int[] docIDsWithFeature, int[] docIDsWithOutFeature, double topEnt, Data data) {
//		return informationGain(data.getEntropy(docIDsWithFeature), docIDsWithFeature.length, data.getEntropy(docIDsWithOutFeature), docIDsWithOutFeature.length, topEnt);
//	}

	/**
	 * @author T.J. Trimble
	 *
	 * Shortcut to calculate entropy of splits automatically.
	 *
	 * Note that this method calculates the entropy for topDocIDs.
	 * If this has already been calculated, use a method which passes
	 * this value in.
	 *
	 * @param docIDsWithFeature
	 * @param docIDsWithOutFeature
	 * @param topDocIDs
	 * @param data
	 * @return Double representation of information gain
	 */
	public static double informationGain(Collection<Integer> docIDsWithFeature, Collection<Integer> docIDsWithOutFeature, Collection<Integer> topDocIDs, Data data) {
		return informationGain(data.getEntropy(docIDsWithFeature), docIDsWithFeature.size(), data.getEntropy(docIDsWithOutFeature), docIDsWithOutFeature.size(), data.getEntropy(topDocIDs));
	}

	/**
	 * @author T.J. Trimble
	 *
	 * Shortcut to calculate entropy of splits automatically.
	 *
	 * Note that this method calculates the entropy for topDocIDs.
	 * If this has already been calculated, use a method which passes
	 * this value in.
	 *
	 * @param docIDsWithFeature
	 * @param docIDsWithOutFeature
	 * @param topDocIDs
	 * @param data
	 * @return Double representation of information gain
	 */
//	public static double informationGain(int[] docIDsWithFeature, int[] docIDsWithOutFeature, int[] topDocIDs, Data data) {
//		return informationGain(data.getEntropy(docIDsWithFeature), docIDsWithFeature.length, data.getEntropy(docIDsWithOutFeature), docIDsWithOutFeature.length, data.getEntropy(topDocIDs));
//	}
}
