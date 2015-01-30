package machineLearningTools;

import java.util.HashSet;

/**
 * EuclideanDistance
 *
 * Calculates the distance between two documents using Euclidean distance,
 * as defined below: <br><br>
 *
 * EuclideanDistance(D.i, D.j) = <br>
 * sqrt(sum.k((f.ik - f.jk)^2)))
 *
 * @author T.J. Trimble
 */
public class EuclideanDistance extends DistanceMetric {

	public EuclideanDistance(Document documentToClassify, Document documentToCompare) {
		super(documentToClassify, documentToCompare);
		if (documentToClassify == null || documentToCompare == null) {
			throw new NullPointerException("EuclideanDistance constructor received null parameter.");
		}
		Double distance = 0.0d;
		HashSet<String> features = new HashSet<String>(documentToClassify.size()+documentToCompare.size());
		features.addAll(documentToClassify.getFeatures());
		features.addAll(documentToCompare.getFeatures());
		for (String feature: features) {
			distance += Math.pow(documentToClassify.getFeatCount(feature) - documentToCompare.getFeatCount(feature), 2);
		}
		this.distance = Math.sqrt(distance);
	}
}
