package com.ir.searchengine.score;

/**
 * https://makarandtapaswi.wordpress.com/2012/07/02/intuition-behind-average-precision-and-map/
 * 
 * @author amit
 *
 */
public class PrecisionRecallCalculator {

	/**
	 * Takes a list of average precisions and calculates the average. Has a
	 * overloaded method that internally calls <b>calculateAveragePrecision(int[]
	 * relevantDocIds, int[] retrievedDocIds)</b>.
	 * 
	 * @param averagePrecisions
	 * @return
	 */
	public double calculateMAP(double[] averagePrecisions) {
		double sumAveragePrecisions = 0;

		for (int i = 0; i < averagePrecisions.length; i++) {
			sumAveragePrecisions += averagePrecisions[i];
		}

		return sumAveragePrecisions / (double) averagePrecisions.length;
	}

	public double calculateAveragePrecision(int[] relevantDocIds, int[] retrievedDocIds) {

		double averagePrecision = 0.0;
		double relevantCount = 0;

		for (int i = 0; i < retrievedDocIds.length; i++) {

			if (ifAinB(retrievedDocIds[i], relevantDocIds)) {
				relevantCount++;
				averagePrecision += (double)relevantCount / (double)(i + 1.0);
			}
		}

		return averagePrecision;

	}

	private boolean ifAinB(int a, int[] b) {
		for (int t : b)
			if (a == t)
				return true;

		return false;
	}

}
