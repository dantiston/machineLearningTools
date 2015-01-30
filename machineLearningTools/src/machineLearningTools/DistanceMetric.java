package machineLearningTools;

public abstract class DistanceMetric extends DistanceMeasure {

	public DistanceMetric(Document documentToClassify, Document documentToCompare) {
		super(documentToClassify, documentToCompare);
	}

	/**
	 * DistanceMetric objects are sorted naturally, 0->1;
	 *
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public final int compareTo(DistanceMeasure anotherMeasure) {
		return this.distance.compareTo(anotherMeasure.distance);
	}

}
