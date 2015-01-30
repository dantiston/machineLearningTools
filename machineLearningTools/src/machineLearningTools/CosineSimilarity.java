package machineLearningTools;

/**
 *
 *
 *
 * @author T.J. Trimble
 */
public class CosineSimilarity extends SimilarityMetric {

	public CosineSimilarity(Document documentToClassify, Document documentToCompare) {
		super(documentToClassify, documentToCompare);
		if (documentToClassify == null || documentToCompare == null) {
			throw new NullPointerException("CosineSimilarity constructor received null parameter.");
		}
		Double distance = 0.0d;

		this.distance = distance;
	}
}
