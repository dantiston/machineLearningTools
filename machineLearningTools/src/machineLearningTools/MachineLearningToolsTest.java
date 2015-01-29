package machineLearningTools;

import static machineLearningTools.Testing.testFile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class MachineLearningToolsTest {

	/* ************************
	 *  Universal Definitions
	 * ************************/

	/* Constants */
	final String testName = "unitTest";
	final String key1 = "key1";
	final String key2 = "key2";
	final int incrementTo = 10;
	final String goldDoc1Label = "talk.politics.guns";
	final HashSet<String> goldLabels = new HashSet<String>(Arrays.asList(new String[]{"talk.politics.guns", "talk.politics.misc", "talk.politics.mideast"}));
	final HashMap<String, Double> testDocumentProbabilities = this.makeProbabilityMap();
	final HashSet<String> testLabels = new HashSet<String>(Arrays.asList(new String[]{"key1", "key2", "key3"}));

	final Data infoGainGoldData = new Data(testFile("goldInfoGainData.txt"));
	final double goldGain = 0.15183550136234136d;
	final HashSet<Integer> goldWith = new HashSet<Integer>(Arrays.asList(new Integer[]{6,7,8,9,10,11,12}));
	final HashSet<Integer> goldWithOut = new HashSet<Integer>(Arrays.asList(new Integer[]{0,1,2,3,4,5,13}));
	final double goldTopEnt = 0.9402859586706309d;
	final double goldWithEnt = 0.9852281360342516d;
	final double goldWithOutEnt = 0.5916727785823275d;

	/* Variables */
	public Data goldData;
	public Data goldData2;
	public Data goldData3;
	public Data otherData;

	public HashMap<String, Double> makeProbabilityMap() {
		HashMap<String, Double> result = new HashMap<String, Double>();
		result.put("key1", 0.33d);
		result.put("key2", 0.33d);
		result.put("key3", 0.33d);
		return result;
	}

}
