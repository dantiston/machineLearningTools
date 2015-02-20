package machineLearningTools;

/**
 * CosineSimilarity
 *
 * Calculates the similarity between two documents using Cosine similarity,
 * as defined below: <br><br>
 *
 * CosineSimilarity(D.i, D.j) = <br>
 * sum.k(f.ik, f.jk)/(sqrt(sum.k(a.ik^2))*sqrt(sum.k(a.jk^2)))
 *
 * @author T.J. Trimble
 */
public class CosineSimilarity extends SimilarityMetric {

	public CosineSimilarity(Document documentToClassify, Document documentToCompare) {
		super(documentToClassify, documentToCompare);
		if (documentToClassify == null || documentToCompare == null) {
			throw new NullPointerException("CosineSimilarity constructor received null parameter.");
		}
		this.distance = 0.0d;
		if (documentToCompare.getMagnitude() == 0 || documentToClassify.getMagnitude() == 0) {
			// If either document is empty, then their similarity is 0
			return;
		}
		for (String feature: documentToClassify.getFeatures()) {
			this.distance += (documentToClassify.getFeatCount(feature)*documentToCompare.getFeatCount(feature));
		}
		this.distance /= (documentToCompare.getMagnitude()*documentToClassify.getMagnitude());
	}
}
