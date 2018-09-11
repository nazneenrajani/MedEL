package com.med.el_ensemble.ensemble;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author nfrajani
 *
 */
public class EnsembleFeatureExtractor {
	int numSystems;
	String[] REOutputs;
	static Map<String,double[]> fextractions_confs;
	static Map<String,Integer> fextractions_target;

	public EnsembleFeatureExtractor(int nsys){
		numSystems = nsys;
		REOutputs = new String[numSystems];
		fextractions_confs = new HashMap<String,double[]>();
		fextractions_target = new HashMap<String,Integer>();
	}

	public void getFiles(String path){
		System.out.println(path);
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		Arrays.sort(listOfFiles);
		int k=0;
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				REOutputs[k] = path+"/"+listOfFiles[i].getName();
				//System.out.println(REOutputs[k]);
				k++;
			}
		}
	}

	public void writeOutput(int num, String feature_file, String out_file) throws IOException{
		BufferedWriter bw = new BufferedWriter(new FileWriter(out_file));
		BufferedWriter bfeatures = new BufferedWriter(new FileWriter(feature_file));
		bfeatures.write("@relation entity_linking\n");
		bfeatures.write("\n");
		for(int i=0;i<numSystems;i++){
			int tmp=i+1;
			bfeatures.write("@attribute conf_"+tmp+" numeric\n");
		}
		bfeatures.write("@attribute target {w,c}\n");
		bfeatures.write("\n");
		bfeatures.write("@data\n");
		for(String key : fextractions_confs.keySet()){
			if(!fextractions_target.containsKey(key))
				continue;
			double[] confs = fextractions_confs.get(key);
			String conf_str = "";
			for(int i =0;i<confs.length;i++){
				conf_str += confs[i]+",";
			}
			conf_str = conf_str.trim();
			String[] parts = key.split("~");
			String out_str = parts[0]+"\t" + parts[1];
			bw.write(out_str+"\n");
			if(fextractions_target.get(key)==1)
				bfeatures.write(conf_str+"c"+"\n");
			else
				bfeatures.write(conf_str+"w"+"\n");
	}
	bw.close();
	bfeatures.close();
}

public void getFeatures(int nsys, String file, int index) throws IOException {
	System.out.println(file);
	BufferedReader featureReader = null;
	try {
		featureReader = new BufferedReader (new FileReader(file));
		String line;
		while((line=featureReader.readLine())!=null){
			String[] parts = line.split("\t");
			if(parts.length<3)
				System.err.println("Not enough columns in "+ line);
			else{
				String key = parts[0]+"~"+parts[1];
				double conf = Double.parseDouble(parts[2]);
				if(!fextractions_confs.containsKey(key)){
					double[] scores = new double[nsys];
					for(int i =0;i<nsys;i++)
						scores[i]=0.0;
					scores[index] = conf;
					fextractions_confs.put(key, scores);
				}
				else{
					double[] scores = fextractions_confs.get(key);
					scores[index] = conf;
					fextractions_confs.put(key, scores);
				}
			}
		}
	} catch (FileNotFoundException e) {
		System.exit (1);
	}
	featureReader.close();
}

public void getGroundtruth(String gtKey) throws IOException {
	BufferedReader gtReader =null;
	try {
		gtReader = new BufferedReader (new FileReader(gtKey));
		String line;
		while((line=gtReader.readLine())!=null){
			String[] parts = line.split("\t");
			if(parts.length<3){
				System.err.println("Not enough columns in "+ line);
			}
			else{
				String key = parts[0]+"~"+parts[1];
				int score =Integer.parseInt(parts[2]);
				fextractions_target.put(key, score);
			}
		}
	} catch (FileNotFoundException e) {
		System.out.println(e);
		System.exit (1);
	}
	gtReader.close();

}
}
