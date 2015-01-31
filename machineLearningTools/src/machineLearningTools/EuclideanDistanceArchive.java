package machineLearningTools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class EuclideanDistanceArchive extends DistanceMetric {
	private static NestedDictionary<Integer, Double> featureCountValues = new NestedDictionary<Integer, Double>();
//	private static NestedDictionary<Integer, Double> documentValues = new NestedDictionary<Integer, Double>();

	public EuclideanDistanceArchive(Document documentToClassify, Document documentToCompare) {
		super(documentToClassify, documentToCompare);
		if (documentToClassify == null || documentToCompare == null) {
			throw new NullPointerException("EuclideanDistance constructor received null parameter.");
		}
		// Check if Documents have been compared before
//		if (EuclideanDistance.documentValues.hasValueAt(documentToClassify.getDocID(), documentToCompare.getDocID())) {
//			this.distance = EuclideanDistance.documentValues.get(documentToClassify.getDocID(), documentToCompare.getDocID());
//			return;
//		}
		// Else, compare documents:
		this.distance = 0.0d;
		ArrayList<Integer> docCounts = new ArrayList<Integer>(2);
		HashSet<String> features = new HashSet<String>(documentToClassify.size()+documentToCompare.size());
		features.addAll(documentToClassify.getFeatures());
		features.addAll(documentToCompare.getFeatures());
		for (String feature: features) {
			docCounts.add(documentToClassify.getFeatCount(feature));
			docCounts.add(documentToCompare.getFeatCount(feature));
			Collections.sort(docCounts);
			if (!EuclideanDistanceArchive.featureCountValues.hasValueAt(docCounts.get(0), docCounts.get(1))) {
				EuclideanDistanceArchive.featureCountValues.put(docCounts.get(0), docCounts.get(1), Math.pow(docCounts.get(0) - docCounts.get(1), 2));
			}
			this.distance += EuclideanDistanceArchive.featureCountValues.get(docCounts.get(0), docCounts.get(1));
		}
		this.distance = Math.sqrt(this.distance);
	}

}
