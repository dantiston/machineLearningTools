package machineLearningClassifiers.NaiveBayesClassifier;

import static machineLearningTools.MLMath.pseudoEqual;
import static machineLearningTools.Testing.testFile;
import static org.junit.Assert.fail;

import java.util.HashMap;

import machineLearningTools.NestedDictionary;

import org.junit.Before;
import org.junit.Test;

public class NaiveBayesClassifierTest {

	/* Univerals */
	//// Constants
	// Parameters
	private final Double classDelta = 0.1d;
	private final Double condDelta = 0.1d;
	private final String sysOutputFile = "nb.output.txt";
	private final String modelFile = "nb.model.txt";
	private final String trainingFile = testFile("example.real.vectors.txt");

	// Gold values
	private final HashMap<String, Double> goldClassProbs = this.makeClassProbs();
	private final NestedDictionary<String, Double> goldFeatProbsBinary = this.makeFeatProbsBinary();
	private final NestedDictionary<String, Double> goldFeatProbsReal = this.makeFeatProbsReal();

	// Variables
	private NaiveBayesClassifier binaryTrainer;
	private NaiveBayesClassifier realTrainer;

	private HashMap<String, Double> makeClassProbs() {
		HashMap<String, Double> result = new HashMap<String, Double>();
		Double probability = Math.log10(1.0d/3.0d);
		result.put("talk.politics.misc", probability);
		result.put("talk.politics.guns", probability);
		result.put("talk.politics.mideast", probability);
		return result;
	}

	private NestedDictionary<String, Double> makeFeatProbsBinary() {
		NestedDictionary<String, Double> result = new NestedDictionary<String, Double>();
		// talk.politics.misc
		result.put("talk.politics.misc", "waste", -2.012837224705172d);
		result.put("talk.politics.misc", "com", -0.2275073896944052d);
		result.put("talk.politics.misc", "how", -0.4000533679854368d);
		result.put("talk.politics.misc", "your", -0.6906179299712529d);
		result.put("talk.politics.misc", "cheaper", -0.9714445395469472d);
		result.put("talk.politics.misc", "israel", -2.012837224705172d);
		result.put("talk.politics.misc", "gun", -2.012837224705172d);
		// talk.politics.mideast
		result.put("talk.politics.mideast", "waste", -0.9714445395469472d);
		result.put("talk.politics.mideast", "com", -2.012837224705172d);
		result.put("talk.politics.mideast", "how", -0.9714445395469472d);
		result.put("talk.politics.mideast", "your", -2.012837224705172d);
		result.put("talk.politics.mideast", "cheaper", -2.012837224705172d);
		result.put("talk.politics.mideast", "israel", -0.30526704860723586d);
		result.put("talk.politics.mideast", "gun", -2.012837224705172d);
		// talk.politics.guns
		result.put("talk.politics.guns", "waste", -2.012837224705172d);
		result.put("talk.politics.guns", "com", -0.9714445395469472d);
		result.put("talk.politics.guns", "how", -2.012837224705172d);
		result.put("talk.politics.guns", "your", -0.9714445395469472d);
		result.put("talk.politics.guns", "cheaper", -2.012837224705172d);
		result.put("talk.politics.guns", "israel", -2.012837224705172d);
		result.put("talk.politics.guns", "gun", -0.10435220582652249d);
		return result;
	}

	private NestedDictionary<String, Double> makeFeatProbsReal() {
		NestedDictionary<String, Double> result = new NestedDictionary<String, Double>();
		// talk.politics.misc
		result.put("talk.politics.misc", "waste", -2.1367205671564067d);
		result.put("talk.politics.misc", "com", -0.35139073214563965d);
		result.put("talk.politics.misc", "how", -0.5239367104366712d);
		result.put("talk.politics.misc", "your", -0.8145012724224874d);
		result.put("talk.politics.misc", "cheaper", -1.0953278819981815d);
		result.put("talk.politics.misc", "israel", -2.1367205671564067d);
		result.put("talk.politics.misc", "gun", -2.1367205671564067d);
		// talk.politics.mideast
		result.put("talk.politics.mideast", "waste", -0.8450980400142568d);
		result.put("talk.politics.mideast", "com", -1.8864907251724818d);
		result.put("talk.politics.mideast", "how", -0.8450980400142568d);
		result.put("talk.politics.mideast", "your", -1.8864907251724818d);
		result.put("talk.politics.mideast", "cheaper", -1.8864907251724818d);
		result.put("talk.politics.mideast", "israel", -0.17892054907454547d);
		result.put("talk.politics.mideast", "gun", -1.8864907251724818d);
		// talk.politics.guns
		result.put("talk.politics.guns", "waste", -2.0293837776852097d);
		result.put("talk.politics.guns", "com", -0.9879910925269847d);
		result.put("talk.politics.guns", "how", -2.0293837776852097d);
		result.put("talk.politics.guns", "your", -0.9879910925269847d);
		result.put("talk.politics.guns", "cheaper", -2.0293837776852097d);
		result.put("talk.politics.guns", "israel", -2.0293837776852097d);
		result.put("talk.politics.guns", "gun", -0.12089875880655998d);
		return result;
	}

	/* Setup */
	@Before
	public void setupNaiveBayes() {
		this.binaryTrainer = new NaiveBayesClassifier(this.classDelta, this.condDelta, this.sysOutputFile, this.modelFile, true);
		this.realTrainer = new NaiveBayesClassifier(this.classDelta, this.condDelta, this.sysOutputFile, this.modelFile);
	}

	/* Tests */
//	@Test
//	public void testNaiveBayesClassifierTrainClassProbs() {
//		this.binaryTrainer.train(this.trainingFile);
//		// Check if not the same number of labels
//		if (this.binaryTrainer.getClassProbs().keySet().size() != this.goldClassProbs.size()) {
//			fail();
//		}
//		for (String label: this.binaryTrainer.getClassProbs().keySet()) {
//			if (!pseudoEqual(this.binaryTrainer.getClassProbs().get(label), this.goldClassProbs.get(label))) {
//				fail();
//			}
//		}
//	}
//
	@Test
	public void testNaiveBayesClassifierTrainFeatProbsBinary() {
		this.binaryTrainer.train(this.trainingFile);
		// Check if not the same number of outer labels
		if (this.binaryTrainer.getFeatProbs().outerKeySet().size() != this.goldFeatProbsBinary.outerKeySet().size()) {
			fail();
		}
		for (String label: this.binaryTrainer.getFeatProbs().outerKeySet()) {
			// Check if not the same number of inner labels
			if (this.binaryTrainer.getFeatProbs().get(label).keySet().size() != this.goldFeatProbsBinary.get(label).keySet().size()) {
				fail();
			}
			for (String feature: this.binaryTrainer.getFeatProbs().get(label).keySet()) {
				if (!pseudoEqual(this.binaryTrainer.getFeatProbs().get(label, feature), this.goldFeatProbsBinary.get(label, feature))) {
					fail();
				}
			}
		}
	}

	@Test
	public void testNaiveBayesClassifierTrainFeatProbsReal() {
		this.realTrainer.train(this.trainingFile);
		// Check if not the same number of outer labels
		System.out.println(this.realTrainer.getFeatLogProbs());
		System.out.println(this.goldFeatProbsReal);
		if (this.realTrainer.getFeatLogProbs().outerKeySet().size() != this.goldFeatProbsReal.outerKeySet().size()) {
			fail();
		}
		for (String label: this.realTrainer.getFeatProbs().outerKeySet()) {
			// Check if not the same number of inner labels
			if (this.realTrainer.getFeatProbs().get(label).keySet().size() != this.goldFeatProbsReal.get(label).keySet().size()) {
				fail();
			}
			for (String feature: this.realTrainer.getFeatProbs().get(label).keySet()) {
				if (!pseudoEqual(this.realTrainer.getFeatProbs().get(label, feature), this.goldFeatProbsReal.get(label, feature))) {
					fail();
				}
			}
		}
	}

//	@Test
//	public void testNaiveBayesClassifierClassifyBinary() {
//		this.binaryTrainer.train(this.trainingFile);
//		Data testData = new BinaryValuedData(this.trainingFile);
//		this.binaryTrainer.classify(testData);
//		// Make sure each document has a system label after classify
//		for (Document document: testData.getDocs()) {
//			if (document.getSysOutput() == null) {
//				fail();
//			}
//		}
//	}

//	/**
//	 * This test may be too stringest. Does sum(P(F.k|C.i)) = 1?
//	 *
//	 */
//	@Test
//	public void testNaiveBayesClassifierBinaryModelSumsToOne() {
//		Double probSum;
//		this.binaryTrainer.train(this.trainingFile);
//		// classProbs should sum to 1
//		probSum = 0.0d;
//		for (String label: this.binaryTrainer.getClassProbs().keySet()) {
//			probSum += Math.pow(10, this.binaryTrainer.getClassProbs().get(label));
//		}
//		assertTrue(pseudoEqual(1.0d, probSum));
//		// featurePerClassProbs should sum to 1
//		for (String label: this.binaryTrainer.getFeatProbs().outerKeySet()) {
//			probSum = 0.0d;
//			for (String feature: this.binaryTrainer.getFeatProbs().get(label).keySet()) {
//				probSum += Math.pow(10, this.binaryTrainer.getFeatProbs().get(label, feature));
//			}
//			assertTrue(pseudoEqual(1.0d, probSum));
//		}
//	}
//
//	@Test
//	public void testNaiveBayesClassifierRealModelSumsToOne() {
//		Double probSum;
//		this.realTrainer.train(this.trainingFile);
//		// classProbs should sum to 1
//		probSum = 0.0d;
//		for (String label: this.realTrainer.getClassProbs().keySet()) {
//			probSum += Math.pow(10, this.realTrainer.getClassProbs().get(label));
//		}
//		assertTrue(pseudoEqual(1.0d, probSum));
//		// featurePerClassProbs should sum to 1
//		for (String label: this.realTrainer.getFeatProbs().outerKeySet()) {
//			probSum = 0.0d;
//			for (String feature: this.realTrainer.getFeatProbs().get(label).keySet()) {
//				probSum += Math.pow(10, this.realTrainer.getFeatProbs().get(label, feature));
//			}
//			assertTrue(pseudoEqual(1.0d, probSum));
//		}
//	}
}
