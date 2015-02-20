package machineLearningTools;

import static machineLearningTools.MLMath.pseudoEqual;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class CosineSimilarityTest {

	// Test values
	private CosineSimilarity testMeasure1;
	private CosineSimilarity testMeasure2;
	private CosineSimilarity testMeasure3;
	private CosineSimilarity testMeasure4;
	private CosineSimilarity emptyMeasure;

	// Gold values
	private final Document testDocument1 = new Document("label f1:1 f2:3");
	private final Document testDocument2 = new Document("label f1:1 f2:3");
	private final Document testDocument3 = new Document("label f3:1 f4:5");
	private final Document testDocument4 = new Document("label f1:3 f2:3 f3:3");
	private final Document testDocument5 = new Document("label");
	private final Double gold1 = 1.0d;
	private final Double gold2 = 0.0d;
	private final Double gold3 = (3.0d/(this.testDocument3.getMagnitude()*this.testDocument4.getMagnitude()));
	private final Double gold4 = 0.0d;

	@Before
	public void setupCosineSimilarity() {
		this.testMeasure1 = new CosineSimilarity(this.testDocument1, this.testDocument2);
		this.testMeasure2 = new CosineSimilarity(this.testDocument2, this.testDocument3);
		this.testMeasure3 = new CosineSimilarity(this.testDocument3, this.testDocument4);
		this.testMeasure4 = new CosineSimilarity(this.testDocument4, this.testDocument5);
		this.emptyMeasure = new CosineSimilarity(this.testDocument5, this.testDocument5);
	}

	@Test
	public void testCosineSimilarity() {
		assertTrue(pseudoEqual(this.testMeasure1.getDistance(), this.gold1));
		assertTrue(pseudoEqual(this.testMeasure2.getDistance(), this.gold2));
		assertTrue(pseudoEqual(this.testMeasure3.getDistance(), this.gold3));
		assertTrue(pseudoEqual(this.testMeasure4.getDistance(), this.gold4));
	}

	@Test
	public void testCosineSimilarityEmptyDocumentIsZero() {
		assertEquals(this.emptyMeasure, this.gold4);
	}

	@Test
	public void testCosineSimilarityIsSymmetrical() {
		CosineSimilarity measure1 = new CosineSimilarity(this.testDocument3, this.testDocument4);
		CosineSimilarity measure2 = new CosineSimilarity(this.testDocument4, this.testDocument3);
		assertTrue(pseudoEqual(measure1.getDistance(), measure2.getDistance()));
	}

	@Test(expected=NullPointerException.class)
	public void testCosineSimilarityNullBothThrowsNull() {
		Document nullDocument = null;
		new EuclideanDistance(nullDocument, nullDocument);
	}

	@Test(expected=NullPointerException.class)
	public void testCosineSimilarityNullFirstThrowsNull() {
		Document nullDocument = null;
		new EuclideanDistance(this.testDocument1, nullDocument);
	}

	@Test(expected=NullPointerException.class)
	public void testCosineSimilarityNullSecondThrowsNull() {
		Document nullDocument = null;
		new EuclideanDistance(nullDocument, this.testDocument1);
	}
}
