package machineLearningClassifiers.NaiveBayesClassifier;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.HashSet;

import machineLearningTools.ConfusionMatrix;
import machineLearningTools.Counter;
import machineLearningTools.Data;
import machineLearningTools.Document;
import machineLearningTools.MachineLearningClassifier;
import machineLearningTools.NestedCounter;
import machineLearningTools.NestedDictionary;

/**
 * NaiveBayesClassifier
 *
 * NaiveBayesClassifier has methods for training, classifying,
 *   and testing, as well as generating output documents for these <br>
 *
 * <b>Input:</b> <br>
 * 	<b>Data:</b> training, testing <br>
 * 	<b>Parameters:</b> <br><br>
 * 		<b>class_prior_delta:</b> Smoothing factor on class probabilities P(C). <br>
 * 			Use this to help smooth for unknown data labels.<br>
 *		<b>cond_prob_delta:</b> Smoothing factor on conditional probabilities P(f|C). <br>
 *			Use this to help smooth for unknown feature->class pairs, <br>
 *			including unknown words.<br>
 *   	<b>modelFile:</b> filename to output model file containing
 *   		paths to leaves with associated labels->probabilities <br>
 *   	<b>sysOutput:</b> filename to output system output containing
 *   		a sorted list of instances with label to probabilities <br>
 *   	<b>useBinarizedFeatures:</b> boolean flag to tell the classifier <br>
 *   		to ignore count values of features and treat all features <br>
 *   		as binary.<br><br>
 * <b>Output:</b> <br>
 * 	<b>stdout:</b> confusion matrices over training and testing data <br>
 * 	<b>Files:</b> <br>
 * 		<b>modelFile:</b> file containing paths to leaves with
 * 			number of instances at leaf and number of instances
 * 			per label at leaf as probabilities <br>
 *   	<b>sysOutput:</b> file containing a sorted list of instances
 *   		with label to probabilities mappings <br><br>
 *
 * This classifier implements the MachineLearningClassifier interface
 *
 * @author T.J. Trimble
 */
public class NaiveBayesClassifier implements MachineLearningClassifier {

	// Parameters
	private final Double classDelta;
	private final Double condDelta;
	private final String sysOutputFile;
	private final String modelFile;
	private Boolean useBinarizedFeatures;

	// Values
	private Data trainingData;
	private HashMap<String, Double> classProbs;
	private NestedDictionary<String, Double> featLogProbs;
	private NestedDictionary<String, Double> featProbs;
	private Double condDeltaSum;
	private Integer featureCount;
	private HashSet<String> allFeatures;

	// Method values
	private Counter<String> classCounts;
	private NestedCounter<String> featPerClassCounts;
	private NestedDictionary<String, Integer> featuresPerClass;
	private Counter<String> featureCountPerClass;
	private HashMap<String, Double> logReciprocalFeatToClassProbs;
	private Double probability;
	private HashMap<String, Double> logProbDenominators;
	private HashMap<String, Double> probDenominators;

	/**
	 * Construct a NaiveBayesClassifier object with the given parameters.
	 *
	 * @param classDelta
	 * @param condDelta
	 * @param sysOutputFile
	 * @param modelFile
	 * @author T.J. Trimble
	 */
	public NaiveBayesClassifier(Double classDelta, Double condDelta, String sysOutputFile, String modelFile) {
		if (classDelta == null || condDelta == null || sysOutputFile == null || modelFile == null) {
			throw new NullPointerException("NaivesBayesClassifier constructor passed null parameter;");
		}
		this.classDelta = classDelta;
		this.condDelta = condDelta;
		this.sysOutputFile = sysOutputFile;
		this.modelFile = modelFile;
		this.useBinarizedFeatures = false;
	}

	public NaiveBayesClassifier(Double classDelta, Double condDelta, String sysOutputFile, String modelFile, Boolean useBinarizedFeatures) {
		this(classDelta, condDelta, sysOutputFile, modelFile);
		this.useBinarizedFeatures = useBinarizedFeatures;
	}

	/**
	 * train(trainingDataFileName) calculates the P(C) and P(F|C) for
	 * each class C, feature F, and document D. Probabilities are
	 * specified in real number form and in log base 10. <br><br>
	 *
	 * P(C) is calculated in the following fashion:<br>
	 * <b>P(C.i) = (classDelta + count(C.i))/((count(C)*classDelta) + SUM.i(count(C.i)))</b><br><br>
	 *
	 * In the binary case, P(F.t|C.i) is calculated in the following fashion:<br>
	 * <b>P(F.t|C.i) = (condDelta + count(F.t, C.i))/((count(C)*condDelta) + count(C.i))</b><br><br>
	 *
	 * In the multinomial case, P(F.t|C.i) is calculated in the following fashion:<br>
	 * <b>P(F.t|C.i) = (condDelta + SUM.j(N.jt*P(C.i|D.j)))/((condDelta*count(V)) + SUM.k->V(SUM.l->D(N.kl*P(C.i|D.j))))</b><br>
	 *
	 * @param trainingDataFileName
	 */
	@Override
	public void train(String trainingDataFileName) {
		if (trainingDataFileName == null) {
			throw new NullPointerException("trainingDataFileName is null at NaiveBayesClassifier#train(trainingDataFileName);");
		}
		//// Constants:
		// count(C): number of classes
		// count(C.i): number of documents with class C.i
		// SUM.i(count(C.i)): number of documents
		// count(V): number of features
		// count(C)*classDelta
		// count(C)*condDelta

		// SUM.k->V(SUM.l->D(N.kl*P(C.i|D.j))):
		//   if document D.j is NOT labeled C.i: = 0
		//   else count of feature in all documents: = SUM.k(SUM.j(N.j))

		//// Per-feature values:
		// count(F.t, C.i): count of feature per class
		// N.jt: count of feature per document
		// P(C.i|D.j): 1 if document D.j is labeled with class C.i else 0

		// SUM.j(N.jt*P(C.i|D.j)) =
		//	 if document D.j is NOT labeled C.i: = 0
		//   else count of feature in all documents: = SUM.j(N.j)

		// Probability of Class C.i such that document D
		//	 is NOT labeled C.i = 1/count(F)
		//	 This means, only calculate probability for the labeled class

		//// Initializations
		this.trainingData = new Data(trainingDataFileName);
		final Integer docCount = this.trainingData.size();

		this.classProbs = new HashMap<String, Double>(); // P(C)
		this.featLogProbs = new NestedDictionary<String, Double>(); // P(F|C)

		this.classCounts = new Counter<String>(); // Total number of documents with a given label; label->docCount
		this.featureCountPerClass = new Counter<String>(); // Count of features in documents per class; label->featCount;
		// TODO: featureCountPerClass could be calculated by summing the values of each of the inner Counters per outer key; f2(class->feature->count) = class->count

		this.featPerClassCounts = new NestedCounter<String>(); // Count of documents (binary-valued feature counts) that have a given feature per class; class->feature->count
		this.featuresPerClass = new NestedDictionary<String, Integer>(); // Real-valued count of times feature occurs in documents per class; class->feature->count;

		//// Read documents and calculate P(C), P(F|C)
		// Read documents
		Integer newCount;
		this.allFeatures = new HashSet<String>();
		for (Document document: this.trainingData.getDocs()) {
			this.classCounts.increment(document.getLabel());
			for (String feature: document.getWords()) {
				this.featPerClassCounts.increment(document.getLabel(), feature);
				// Add value to featurePerClass
				if (this.featuresPerClass.hasValueAt(document.getLabel(), feature)) {
					newCount = (this.featuresPerClass.get(document.getLabel(), feature)+document.getFeatCount(feature));
					this.featuresPerClass.put(document.getLabel(), feature, newCount);
				}
				else {
					this.featuresPerClass.put(document.getLabel(), feature, document.getFeatCount(feature));
				}
				this.featureCountPerClass.increment(document.getLabel());
				this.allFeatures.add(feature);
			}
		}

		//// Constants
		// Calculate denominator values
		Double classDenominator = Math.log10((this.classDelta*this.classCounts.size()) + docCount);
		// denominatorCounter is either the feature counts per class or the total class counts
		Counter<String> denominatorCounter = (this.useBinarizedFeatures) ? this.classCounts : this.featureCountPerClass;
		this.logProbDenominators = new HashMap<String, Double>();
		this.probDenominators = new HashMap<String, Double>();
		for (String label: this.trainingData.getAllLabels()) {
			this.condDeltaSum = this.condDelta * (this.useBinarizedFeatures ? this.classCounts.size() : this.allFeatures.size());
			this.probability = this.condDeltaSum + (double)denominatorCounter.get(label);
			this.logProbDenominators.put(label, Math.log10(this.probability));
			this.probDenominators.put(label, this.probability);
		}

		// Calculate P(C), P(F|C)
		Double classProbability;
		for (String label: this.trainingData.getAllLabels()) {
			// Calculate classProbability
			classProbability = Math.log10(this.classDelta + this.classCounts.get(label)) - classDenominator;
			this.classProbs.put(label, classProbability);
			// Calculate featProbability
			if (this.useBinarizedFeatures) {
				for (String feature: this.trainingData.getAllFeatures()) {
					this.featLogProbs.put(label, feature, this.calculateBinaryFeatureProb(label, feature));
				}
			}
			else {
				for (String feature: this.trainingData.getAllFeatures()) {
					this.featLogProbs.put(label, feature, this.calculateRealFeatureProb(label, feature));
				}
			}
		}

		// Create model file
		this.writeModelFile(this.modelFile);
	}

	/**
	 * Calculate P(F|C) using the binary Bernoulli equation as follows:<br><br>
	 *
	 * <b>P(F.t|C.i) = (condDelta + count(F.t, C.i))/((condDelta*classCount) + count(C.i)) -> <br>
	 * log((condDelta + count(F.t, C.i))) - log(((condDelta*classCount) + count(C.i)))</b>
	 *
	 * @param label
	 * @param feature
	 * @return
	 */
	private Double calculateBinaryFeatureProb(String label, String feature) {
		if (label == null || feature == null) {
			throw new NullPointerException("A parameter is null at NaiveBayesClassifier#calculateBinaryFeatureProb();");
		}
		if (this.featProbs == null) {
			this.featProbs = new NestedDictionary<String, Double>();
		}
		// Save prob
		this.featProbs.put(label, feature, ((this.condDelta + (double)this.featPerClassCounts.get(label, feature))/this.probDenominators.get(label)));
		// Return log prob
		return (Math.log10(this.condDelta + (double)this.featPerClassCounts.get(label, feature)) - this.logProbDenominators.get(label));
	}

	/**
	 * Calculate P(F|C) using the multinomial Naive Bayes model as follows:<br><br>
	 *
	 * <b>P(F.t|C.i) = (condDelta + SUM.j(N.jt*P(C.i|D.j)))/((condDelta * count(V)) + SUM.k->V(SUM.l->D(N.kl*P(C.i|D.j)))) -> <br>
	 * log((condDelta + SUM.j(N.jt*P(C.i|D.j)))) - log(((condDelta * count(V)) + SUM.k->V(SUM.l->D(N.kl*P(C.i|D.j)))))</b>
	 *
	 * @param label
	 * @param feature
	 * @return
	 */
	private Double calculateRealFeatureProb(String label, String feature) {
		if (label == null || feature == null) {
			throw new NullPointerException("A parameter is null at NaiveBayesClassifier#calculateRealFeatureProb();");
		}
		this.featureCount = this.featuresPerClass.get(label, feature);
		if (this.featureCount == null) {
			this.featureCount = 0;
		}
		return (Math.log10(this.condDelta + (double)this.featureCount) - this.logProbDenominators.get(label));
	}

	/**
	 * Load in a modelFile in the following format:
	 *
	 * class1 class1Prob class1LogProb
	 * class1 class1Prob class1LogProb
	 * ...
	 * feat1 class1 feat1Class1Prob feat1Class1LogProb
	 * feat2 class1 feat2Class1Prob feat2Class1LogProb
	 * ...
	 * feat1 class2 feat1Class2Prob feat1Class2LogProb
	 * feat2 class2 feat2Class2Prob feat2Class2LogProb
	 * ...
	 *
	 * @param modelFile
	 */
	@Override
	public void train(BufferedReader modelFileBuffer) {
		if (modelFileBuffer == null) {
			throw new NullPointerException("modelFile is null at NaiveBayesClassifier#train(modelFileBuffer);");
		}
		// Initialize training values
		this.classProbs = new HashMap<String, Double>();
		this.featLogProbs = new NestedDictionary<String, Double>();
		try {
			String line;
			String[] parts;
			boolean foundFeats = true;
			int lineCount = 0;
			while ((line = modelFileBuffer.readLine()) != null) {
				if (line.startsWith("//")) {
					continue; // Skip comment lines
				}
				parts = line.split("\\s+");
				if (parts.length == 3) {
					if (foundFeats) {
						throw new IllegalArgumentException(String.format("Model file contains improperly formatted feature line at line %s.", lineCount));
					}
					// Found class probs: read in label and logProb
					this.classProbs.put(parts[0], Double.parseDouble(parts[2]));
				}
				else if (parts.length == 4) {
					foundFeats = true;
					// Found feat probs
					this.featProbs.put(parts[1], parts[0], Double.parseDouble(parts[2]));
					this.featLogProbs.put(parts[1], parts[0], Double.parseDouble(parts[3]));
				}
				else {
					throw new IllegalArgumentException(String.format("Model file contains improperly formatted feature line at line %s.", lineCount));
				}
				lineCount++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * classify(data) calculates the most probable class C for each document
	 * given the training data by calculating the argmax of class C
	 * over the sum of the log probabilities of the feature k given
	 * the class C <br><br>
	 *
	 * In the binary case, class C is determined for a document D
	 * with features F by the following calculation:
	 *
	 * ￼￼￼￼classify (x) -> <br>
	 * = MUL.k(P(f.k|c))
	 * = MUL.k->featuresInDoc(P(f.k|c)) * MUL.k->featuresNotInDoc(1 - P(f.k|c)) ->
	 * = argmax.c P(c) * SUM.k->featuresInDoc(log(P(f.k | c))) * (1 - SUM.k->featuresNotInDoc(log(P(f.k | c))))
	 *
	 * In the multinomial case, class C is determined for a document D
	 * with features F by the following calculation:
	 *
	 * classify (x) -> <br>
	 * = classify (f.1, .., f.d) -> <br>
	 * = argmax.c P(c|x) -> <br>
	 * = argmax.c P(x|c) P(c) -> <br>
	 * = argmax.c P(c) MUL.k P(f.k | c) -> <br>
	 * = argmax.c P(c) SUM.k log(P(f.k | c)) <br>
	 *
	 * @param testingData
	 * @return
	 */
	@Override
	public void classify(Data testingData) {
		if (testingData == null) {
			throw new NullPointerException("testingData is null at NaiveBayesClassifier#classify(testingData);");
		}
		HashMap<String, Double> probabilities;
		// Calculate constants
		if (this.useBinarizedFeatures) {
			// Calculate SUM.k(log(1 - P(w.k|c.j)))
			// i.e. the log reciprocal probability of the feature given the class
			this.logReciprocalFeatToClassProbs = new HashMap<String, Double>();
			for (String label: this.trainingData.getAllLabels()) {
				this.probability = 0.0d;
				for (String feature: this.allFeatures) {
					this.probability += Math.log10(1.0d - this.featProbs.get(label, feature));
				}
				this.logReciprocalFeatToClassProbs.put(label, this.probability);
			}
		}
		// Classify documents
		for (Document document: testingData.getDocs()) {
			if (this.useBinarizedFeatures) {
				probabilities = this.classifyBinary(document);
			}
			else {
				probabilities = this.classifyReal(document);
			}
			document.setSysOutput(probabilities);
		}
	}

	/**
	 * Calculate the probability of each label given the training data
	 * and the document. Note that this method does not actually
	 * choose a label, but rather calculates the probability of each.
	 * The argmax is found in Document.setSysOutput()
	 *
	 * Use the binary Naive Bayes classification method as follows: <br>
	 *
	 * classify (x) -> <br>
	 * = argmax.c P(d.i, c.j) -> <br>
	 * = argmax.c P(c.j) * MUL.k->featsInDoc(P(f.k|c.j)) * MUL.k->featsNotInDoc(1 - P(f.k|c.j)) -> <br>
	 * = argmax.c P(c.j) * MUL.k->featsInDoc(P(f.k|c.j)) * (MUL.k(1 - P(f.k|c.j) / MUL.k->featsInDoc(1 - P(f.k|c.j))) -> <br>
	 * = argmax.c P(c.j) * MUL.k->featsInDoc(P(f.k|c.j)/(1 - P(f.k|c.j))) * MUL.k(1 - P(f.k|c.j)) -> <br>
	 * = argmax.c log(P(c.j)) + SUM.k->featsInDoc(log(P(f.k|c.j)) - log(1 - P(f.k|c.j))) + SUM.k(log(1 - P(f.k|c.j))))<br>
	 *
	 * @param document
	 * @return
	 */
	private HashMap<String, Double> classifyBinary(Document document) {
		HashMap<String, Double> result = new HashMap<String, Double>();
		for (String label: this.trainingData.getAllLabels()) {
			this.probability = 0.0d;
			for (String feature: document.getFeatures()) {
				this.probability += (this.featLogProbs.safeGet(label, feature, 0.0d) - Math.log10(1 - this.featProbs.safeGet(label, feature, 0.0d)));
			}
			this.probability += this.classProbs.get(label);
			this.probability += this.logReciprocalFeatToClassProbs.get(label);
			result.put(label, this.probability);
		}
		return result;
	}

	/**
	 * Calculate the probability of each label given the training data
	 * and the document. Use the multinomial Naive Bayes classification
	 * method as follows: <br>
	 *
	 * classify (x) -> <br>
	 * = classify (f.1, .., f.d) -> <br>
	 * = argmax.c P(c.j|x) -> <br>
	 * = argmax.c P(x|c.j) P(c.j) -> <br>
	 * = argmax.c P(c.j) MUL.k P(f.k | c.j)^N.ik -> <br>
	 * = argmax.c log(P(c.j)) + SUM.k (log(P(f.k | c.j)^N.ik)) -> <br>
	 * = argmax.c log(P(c.j)) + SUM.k (N.ik * log(P(f.k | c.j))) -> <br>
	 *
	 * @param document
	 * @return
	 */
	private HashMap<String, Double> classifyReal(Document document) {
		HashMap<String, Double> result = new HashMap<String, Double>();
		for (String label: this.trainingData.getAllLabels()) {
			this.probability = 0.0d;
			for (String feature: document.getFeatures()) {
				this.probability += (document.getFeatCount(feature) * this.featLogProbs.safeGet(label, feature, 0.0d));
			}
			this.probability += this.classProbs.get(label);
			result.put(label, this.probability);
		}
		return result;
	}

	/**
	 * Classify each document in the testing data stored in the file at the
	 * parameter testingDataFileName and compare with gold label.
	 *
	 *
	 * @param testingDataFileName
	 * @param testingLabel
	 */
	@Override
	public void test(String testingDataFileName, String testingLabel) {
		if (testingDataFileName == null || testingLabel == null) {
			throw new NullPointerException("null parameter passed to NaiveBayesClassifier#test(testingDataFileName, testingLabel);");
		}
		Data testData = new Data(testingDataFileName);
		this.classify(testData);
		this.outputResults(testData, testingLabel);
	}

	/**
	 * Output the system output file to the specified file
	 * and the confusion matrix to stdout.
	 *
	 * @param testResult
	 * @throws IOException
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
			System.err.println("Failed to write results at NaiveBayesClassifier#outputResults(testResult, trainOrTest). Check your system output filename and system setup.");
			e.printStackTrace();
		}
		// Output confusion matrix to stdout
		System.out.println(new ConfusionMatrix(testResult, trainOrTest));
	}

	/**
	 * Shortcut to open and close specified file to write model file.
	 *
	 * @param modelOutputFile
	 *
	 * @see writeModelFile
	 */
	private void writeModelFile(String modelOutputFile) {
		try {
			BufferedWriter modelOutput = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.modelFile), "utf-8"));
			this.writeModelFile(modelOutput);
			modelOutput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Write the model file for this classifier with the following format:
	 *
	 * class1 class1Prob class1LogProb
	 * class1 class1Prob class1LogProb
	 * ...
	 * feat1 class1 feat1Class1Prob feat1Class1LogProb
	 * feat2 class1 feat2Class1Prob feat2Class1LogProb
	 * ...
	 * feat1 class2 feat1Class2Prob feat1Class2LogProb
	 * feat2 class2 feat2Class2Prob feat2Class2LogProb
	 * ...
	 *
	 * @param modelOutput
	 * @throws IOException
	 */
	public void writeModelFile(BufferedWriter modelOutput) throws IOException {
		if (modelOutput == null) {
			throw new NullPointerException("modelOutput paramter at NaiveBayesClassifier#writeModelFile() is null.");
		}
		// Check if model file is ready to be written
		if (this.classProbs == null || this.featLogProbs == null) {
			System.err.println("NaiveBayesClassifier#writeModelFile() called before training completed;");
			System.exit(-1);
		}
		// Write class probabilities
		for (String label: this.classProbs.keySet()) {
			modelOutput.write(String.format("%s %s %s%n", label, Math.pow(10, this.classProbs.get(label)), this.classProbs.get(label)));
		}
		// Write feature probabilities
		for (String label: this.featLogProbs.outerKeySet()) {
			for (String feature: this.featLogProbs.get(label).keySet()) {
				modelOutput.write(String.format("%s %s %s %s%n", feature, label, Math.pow(10, this.featLogProbs.get(label, feature)), this.featLogProbs.get(label, feature)));
			}
		}
	}

	/**
	 * Writes each document's system output with the following format: <br>
	 *
	 * <b>instanceID true_label class1 prob1 class2 prob2 ...</b>
	 *
	 * @param testResult
	 * @param sysOutput
	 * @throws IOException
	 */
	public void writeSysOutput(Data testResult, BufferedWriter sysOutput) throws IOException {
		if (testResult == null || sysOutput == null) {
			throw new NullPointerException();
		}
		sysOutput.write(testResult.getFormattedSystemOutput(true));
	}

	// Getters

	HashMap<String, Double> getClassProbs() {
		return this.classProbs;
	}

	NestedDictionary<String, Double> getFeatProbs() {
		return this.featLogProbs;
	}
}
