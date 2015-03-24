package machineLearningTools;

import static machineLearningTools.MLMath.pseudoEqual;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class EuclideanDistanceTest {

	// Test values
	private EuclideanDistance testMeasure1;
	private EuclideanDistance testMeasure2;
	private EuclideanDistance testMeasure3;
	private EuclideanDistance emptyMeasure;

	// Gold values
	private final Double gold1 = 0.0d;
	private final Double gold2 = 10.0d;
	private final Double gold3 = 9.0d;
	private final Double gold4 = 0.0d;
	private final Document testDocument1 = new RealValuedDocument("label f1:1 f2:3");
	private final Document testDocument2 = new RealValuedDocument("label f1:1 f2:3");
	private final Document testDocument3 = new RealValuedDocument("label f3:1 f4:5");
	private final Document testDocument4 = new RealValuedDocument("label f1:3 f2:3 f3:3");
	private final Document testDocument5 = new RealValuedDocument("label");

	@Before
	public void setupEuclideanDistance() {
		this.testMeasure1 = new EuclideanDistance(this.testDocument1, this.testDocument2);
		this.testMeasure2 = new EuclideanDistance(this.testDocument2, this.testDocument3);
		this.testMeasure3 = new EuclideanDistance(this.testDocument4, this.testDocument5);
		this.emptyMeasure = new EuclideanDistance(this.testDocument5, this.testDocument5);
	}

	@Test
	public void testEuclideanDistance() {
		assertEquals(this.testMeasure1, this.gold1);
		assertEquals(this.testMeasure2, this.gold2);
		assertEquals(this.testMeasure3, this.gold3);
	}

//	@Test
//	public void testEuclideanDistancePerformance() {
//		List<EuclideanDistance> distances = new ArrayList<EuclideanDistance>();
//		for (int i=0; i<1000000; i++) {
//			distances.add(new EuclideanDistance(this.testDocument1, this.testDocument2));
//		}
//	}

	@Test
	public void testCosineSimilarityEmptyDocumentIsZero() {
		assertEquals(this.emptyMeasure, this.gold4);
	}

	@Test
	public void testEuclideanDistanceIsSymmetric() {
		EuclideanDistance measure1 = new EuclideanDistance(this.testDocument3, this.testDocument4);
		EuclideanDistance measure2 = new EuclideanDistance(this.testDocument4, this.testDocument3);
		assertTrue(pseudoEqual(measure1.getDistance(), measure2.getDistance()));
	}

	@Test
	public void testEuclideanDistanceIsReflexive() {
		EuclideanDistance measure1 = new EuclideanDistance(this.testDocument3, this.testDocument3);
		assertTrue(pseudoEqual(measure1.getDistance(), 0.0d));
	}

	@Test(expected=NullPointerException.class)
	public void testEuclideanDistanceNullBothThrowsNull() {
		Document nullDocument = null;
		new EuclideanDistance(nullDocument, nullDocument);
	}

	@Test(expected=NullPointerException.class)
	public void testEuclideanDistanceNullFirstThrowsNull() {
		Document nullDocument = null;
		new EuclideanDistance(this.testDocument1, nullDocument);
	}

	@Test(expected=NullPointerException.class)
	public void testEuclideanDistanceNullSecondThrowsNull() {
		Document nullDocument = null;
		new EuclideanDistance(nullDocument, this.testDocument1);
	}
}
