package com.med.el_ensemble.ensemble;

import java.io.IOException;

public class EnsembleCaller {

	public static void main(String[] args) throws IOException {
		int nsys = 8;
		/*String devDir = "/users/nfrajani/data/ensemble/finalMCR/dev";
		String devKey = "/users/nfrajani/data/ensemble/finalMCR/aic/mcrkey_dev";
		String dev_out_file = "/users/nfrajani/data/ensemble/finalMCR/output/mcr_dev";
		String dev_feature_file = "/users/nfrajani/data/ensemble/finalMCR/output/mcr_dev.arff";
		
		EnsembleFeatureExtractor fe = new EnsembleFeatureExtractor(nsys);
		fe.getFiles(devDir);
		for(int sys=0; sys<nsys;sys++){
			fe.getFeatures(nsys, fe.REOutputs[sys], sys);
		}
		fe.getGroundtruth(devKey);
		fe.writeOutput(nsys,dev_feature_file,dev_out_file);*/
		
		String cvDir = "/users/nfrajani/data/latestEMRA/ensemble3";
		String cvKey = "/users/nfrajani/data/emra_complete_gt.tsv";
		String cv_out_file = "/users/nfrajani/data/latestEMRA/output3/emra";
		String cv_feature_file = "/users/nfrajani/data/latestEMRA/output3/emra_3.arff";
		EnsembleFeatureExtractor fe2 = new EnsembleFeatureExtractor(nsys);
		fe2.getFiles(cvDir);
		for(int sys=0; sys<nsys;sys++){
			fe2.getFeatures(nsys, fe2.REOutputs[sys], sys);
		}
		fe2.getGroundtruth(cvKey);
		fe2.writeOutput(nsys,cv_feature_file,cv_out_file);
		
	}

}
