package com.sijo.util;


import java.util.ArrayList;

public class DistanceCal {
	String longerSound = "baseFeature";
	public static int loopCounter = 0;

	float distance = 0;
	float smallDistance = 0;
	float bigDistance = 0;
	
	float similar = 0;

	public float getSimilar() {
		return smallDistance;
	}

	public DistanceCal(ArrayList<FeatureExtractor.TarsosDSP_MFCC> baseFeature, ArrayList<FeatureExtractor.TarsosDSP_MFCC> newFeature) {
		if (baseFeature.size() < newFeature.size()) {
			longerSound = "newFeature"; // �� ����� ���Ѵ�.
			caculateEuclid(baseFeature, newFeature);
		} else {
			caculateEuclid(newFeature, baseFeature);
		}
	}

	public void caculateEuclid(ArrayList<FeatureExtractor.TarsosDSP_MFCC> sizeSmall, ArrayList<FeatureExtractor.TarsosDSP_MFCC> sizeBig) {
		loopCounter = sizeBig.size() - sizeSmall.size() + 1;

		double [][] dist;
		dist = new double[sizeSmall.size()][sizeBig.size()];

		double [][] dtw;
		dtw = new double[sizeSmall.size()+1][sizeBig.size()+1];

		for(int j =0; j< sizeSmall.size(); j++){
			for(int i =0; i<sizeBig.size(); i++){

				for (int k = 0; k < sizeSmall.get(0).getCoefficient().size(); k++) {
					distance += (Math.pow(Math.abs((sizeBig.get(i).getCoefficient().get(k) - sizeSmall.get(j).getCoefficient().get(k))), 2));
				}

				dist[j][i] = Math.sqrt(distance);

			}
		}

		for(int i =1; i<=sizeBig.size(); i++) {
			dtw[0][i] = 100000;
		}
		for(int i =1; i<=sizeSmall.size(); i++) {
			dtw[i][0] = 100000;
		}
		dtw[0][0] = 0;





		for(int j =1; j<= sizeSmall.size(); j++){
			for(int i =1; i<=sizeBig.size(); i++){

				dtw[j][i] = dist[j-1][i-1] + Math.min(dtw[j - 1][i - 1], Math.min(dtw[j][i - 1], dtw[j - 1][i]));

			}
		}




		for (int k = 0; k < loopCounter; k++) {
			for (int i = 0; i < sizeSmall.size() / 2; i++) {
				for (int j = 0; j < sizeSmall.get(0).getCoefficient().size(); j++) {
					distance += (Math.pow(Math.abs((sizeBig.get(i + k).getCoefficient().get(j) - sizeSmall.get(i).getCoefficient().get(j))), 2));
				}
			}

			if (smallDistance == 0) {
				smallDistance = distance;
				bigDistance = distance;
			}
			if (distance < smallDistance)
				smallDistance = distance;
			if (distance > bigDistance)
				bigDistance = distance;

			distance = 0;

		}

		similar = (float) dtw[sizeSmall.size()][sizeBig.size()];
		//System.out.println(smallDistance);
		//System.out.println(similar);
        //System.out.println(bigDistance);
	}
}
