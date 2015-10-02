package machineLearningClusterers.kMeans;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import machineLearningTools.Data;
import machineLearningTools.Document;
import machineLearningTools.EuclideanDistance;
import machineLearningTools.MachineLearningClusterer;

/**
 *
 *
 *
 * See http://stackoverflow.com/questions/1545606/python-k-means-algorithm
 *
 * @author T.J. Trimble
 */
public class KMeansClusterer extends MachineLearningClusterer {

	private final int k;

	public KMeansClusterer(boolean binarized, int k) {
		super(binarized);
		this.k = k;
	}

	/**
	 * Initializes centroids randomly.
	 *
	 * @param testingData
	 */
	// TODO: make split documents utility function
	// TODO: make seedCentroids() method
	@Override
	public void cluster(Data testingData) {
		// Split documents
		List<Document> sublist;
		boolean completed = false;
		List<Document> documents = testingData.getDocs();
		List<List<Document>> splits = new ArrayList<List<Document>>(this.k);
		int docsPerSplit = testingData.size()/this.k;
		for (int i = 0; i < this.k; i++) {
			if (i < this.k-1) {
				sublist = documents.subList(i*docsPerSplit, (i+1)*docsPerSplit);
			}
			else {
				// Last split gets the rest
				sublist = documents.subList(i*docsPerSplit, documents.size());
			}
			splits.add(i, sublist);
		}
		// Initialize centroids randomly
		ArrayList<Document> centroids = new ArrayList<Document>(this.k);
		Random random = new Random();
		ArrayList<Integer> selected = new ArrayList<Integer>(this.k);
		Integer next;
		for (int i = 0; i < this.k; i++) {
			// Get a unique list of random elements
			next = random.nextInt();
			while (selected.indexOf(next) >= 0) {
				next = random.nextInt();
			}
			centroids.add(documents.get(next));
		}
		// Setup algorithm
		Document closestDoc;
		EuclideanDistance closestDistance;
		EuclideanDistance distance;
		while (!completed) {
			completed = true;
			for (int i=0; i < this.k; i++) {
				// Assign documents
				for (Document doc: testingData.getDocs()) {
					// Get closest document
					// TODO: Assign closest document as document with
					// lowest ID to avoid cycling
					// if (moved) {
					//     completed = false;
					// }
				}
				if (!completed) {
					for (int j=0; j < this.k; j++) {
						// Recompute centroids
						// centroid_MU(cluster_W) = (1/size(cluster_W)) * sum_X<W(X)
					}
				}
			}
//			for (Document doc: testingData.getDocs()) {
//				// Reset values
//				closestDoc = null;
//				closestDistance = null;
//				// Expectation step
//				for (Document centroid: centroids) {
//					distance = new EuclideanDistance(doc, centroid);
//					if (closestDistance == null || distance.getDistance() < closestDistance.getDistance()) {
//						closestDoc = centroid;
//						closestDistance = distance;
//						completed = false;
//					}
//				}
//				// Maximization step
//
			}
		}
	}

	@Override
	protected void outputResults(Data testResult, String trainOrTest) {
		// TODO Auto-generated method stub

	}

}
