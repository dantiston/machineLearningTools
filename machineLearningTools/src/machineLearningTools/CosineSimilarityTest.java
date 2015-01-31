package machineLearningTools;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class CosineSimilarityTest {

	// Test values
	private CosineSimilarity testMeasure1;
	private CosineSimilarity testMeasure2;
	private CosineSimilarity testMeasure3;

	// Gold values
	private final Double gold1 = 0.0d;
	private final Double gold2 = 6.0d;
	private final Double gold3 = Math.sqrt(27);
	private final Document testDocument1 = new Document("label f1:1 f2:3");
	private final Document testDocument2 = new Document("label f1:1 f2:3");
	private final Document testDocument3 = new Document("label f3:1 f4:5");
	private final Document testDocument4 = new Document("label f1:3 f2:3 f3:3");
	private final Document testDocument5 = new Document("label");

	@Before
	public void setupCosineSimilarity() {
		this.testMeasure1 = new CosineSimilarity(this.testDocument1, this.testDocument2);
		this.testMeasure2 = new CosineSimilarity(this.testDocument2, this.testDocument3);
		this.testMeasure3 = new CosineSimilarity(this.testDocument4, this.testDocument5);
	}

	@Test
	public void testCosineSimilarity() {
		assertEquals(this.testMeasure1, this.gold1);
		assertEquals(this.testMeasure2, this.gold2);
		assertEquals(this.testMeasure3, this.gold3);
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
