package machineLearningTools;

/**
 * DistanceMeasure
 *
 * Calculate the distance between two Document objects using some
 * distance measure. Distance should be measured between the two Document
 * objects based on their features.
 *
 * Distance is defined as a measure from A to B where more
 * distance represents more distinct values. DistanceMeasure subclasses
 * might also use similarity measures, and subsequently all DistanceMeasure
 * subclasses should implement the Comparable interface and define
 * how they are compared.
 *
 * @author T.J. Trimble
 */
public abstract class DistanceMeasure implements Comparable<DistanceMeasure> {

	protected Double distance;

	/**
	 * Subclasses of DistanceMeasure should calculate the distance
	 * in the constructor.
	 *
	 * @param documentToClassify
	 * @param documentToCompare
	 * @author T.J. Trimble
	 */
	public DistanceMeasure(Document documentToClassify, Document documentToCompare) {};

	/**
	 * Return the distance between two documents.
	 *
	 * @return
	 */
	public final Double getDistance() {
		return this.distance;
	}

	// Basic methods

	@Override
	public abstract int compareTo(DistanceMeasure anotherMeasure);

	@Override
	public String toString() {
		return "<"+this.distance.toString()+">";
	}

	@Override
	public boolean equals(Object otherObject) {
		return this.distance.equals(otherObject);
	}

}
