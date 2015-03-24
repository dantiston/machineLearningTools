package machineLearningTools;

import static machineLearningTools.MLMath.informationGain;
import static machineLearningTools.MLMath.pseudoEqual;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class MLMathTest {

	private RealValuedDataTest realValuedDataTest = new RealValuedDataTest();
	private MachineLearningToolsTest test = new MachineLearningToolsTest();

	// Constants
	private final Double threshold = 0.000000001d;

	// Variables
	private Double withEnt;
	private Integer withSize;
	private Double withOutEnt;
	private Integer withOutSize;
	private Double topEnt;
	private Data goldData;

	// Logarithm Tests

	private boolean checkLog(Integer base, Integer power) {
		Double raisedValue = Math.pow(base, power);
	    if ((power - MLMath.log(raisedValue, base)) > this.threshold) {
	    	return false;
	    }
	    if ((power > 0) && (((power-1) - MLMath.log(raisedValue-1, base)) > this.threshold)) {
	    	return false;
	    }
	    return true;
	}

	@Test
	public void testLogarithm() {
		for (int base=2; base<1000; base++) {
			int maxPow = (int) (Math.log(Integer.MAX_VALUE) / Math.log(base));
	        for (int pow=0; pow<=maxPow; pow++) {
	        	if (!this.checkLog(base, pow)) {
	        		System.out.format("Failed at %s, %s%n", base, pow);
	        		fail();
	        	}
	        }
		}
	}

	@Test(expected=ArithmeticException.class)
	public void testLogarithmThrowsArithmeticException() {
		assertTrue(MLMath.log(-1d, 2) == 2);
	}

	@Test
	public void testLogBase2Functions() {
		assertTrue((MLMath.log2 - Math.log(2)) < this.threshold);
	}

	// Comparison tests

	public void testPseudoEqualDoublePositive1() {
		assertTrue(pseudoEqual(0.0000000001d, 0.0000000002d));
	}

	public void testPseudoEqualDoublePositive2() {
		assertTrue(pseudoEqual(0.0000000001d, 0.00000009d));
	}

	public void testPseudoEqualDoublePositive3() {
		assertTrue(pseudoEqual(12.0000000001d, 12.00000009d));
	}

	public void testPseudoEqualDoubleNegative1() {
		assertFalse(pseudoEqual(0.0000000001d, 0.000001d));
	}

	public void testPseudoEqualDoubleNegative2() {
		assertFalse(pseudoEqual(1.1d, 1.3d));
	}

	// Information Gain Tests

	private void setupInformationGain() {
		this.withEnt = this.test.infoGainGoldBinaryData.getEntropy(this.test.goldWith);
		this.withSize = this.test.goldWith.size();
		this.withOutEnt = this.test.infoGainGoldBinaryData.getEntropy(this.test.goldWithOut);
		this.withOutSize = this.test.goldWithOut.size();
		this.topEnt = this.test.infoGainGoldBinaryData.getEntropy(this.test.infoGainGoldBinaryData.getIDs());
		// Set up Data
		this.realValuedDataTest.setupData();
		this.goldData = this.realValuedDataTest.test.goldData;
	}

	@Test
	public void testInformationGain() {
		this.setupInformationGain();
		Double difference = (informationGain(this.withEnt, (double)this.withSize, this.withOutEnt, (double)this.withOutSize, this.topEnt) - this.test.goldGain);
		assertTrue(Math.abs(difference) < this.threshold);
	}

	@Test
	public void testInformationGainWithCastSignature() {
		this.setupInformationGain();
		Double difference = (informationGain(this.withEnt, this.withSize, this.withOutEnt, this.withOutSize, this.topEnt) - this.test.goldGain);
		assertTrue(Math.abs(difference) < this.threshold);
	}

	@Test
	public void testInformationGainWithEntropyNoTopSignature() {
		this.setupInformationGain();
		Double difference = (informationGain(this.test.goldWith, this.test.goldWithOut, this.topEnt, this.goldData) - this.test.goldGain);
		assertTrue(Math.abs(difference) < this.threshold);
	}

	@Test
	public void testInformationGainWithEntropyAndTopSignature() {
		this.setupInformationGain();
		Double difference = (informationGain(this.test.goldWith, this.test.goldWithOut, this.test.infoGainGoldBinaryData.getIDs(), this.goldData) - this.test.goldGain);
		assertTrue(Math.abs(difference) < this.threshold);
	}

}
