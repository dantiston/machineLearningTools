package machineLearningTools;

public abstract class SimilarityMetric extends DistanceMeasure {

	public SimilarityMetric(Document documentToClassify, Document documentToCompare) {
		super(documentToClassify, documentToCompare);
	}

	/**
	 * SimilarityMetric objects are sorted naturally in reverse, 1->0;
	 *
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public final int compareTo(DistanceMeasure anotherMeasure) {
		return -this.distance.compareTo(anotherMeasure.distance);
	}

}
