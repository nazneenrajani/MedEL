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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class Utility {

	public static String[] REOutputs;
	static int nsys = 8;
	public static void main(String[] args) {
		REOutputs = new String[nsys];
		getFiles("/users/nfrajani/data/ensemble/mcr/dev");
		LinkedHashMap<String,String> offsets = new LinkedHashMap<String,String>();
		BufferedReader br;
		try{
			br = new BufferedReader(new FileReader("/users/nfrajani/data/ensemble/w2vsim_dev"));
			String line;
			try {
				while((line=br.readLine())!=null){
					String[] p = line.split("\t");
					offsets.put(p[0]+"~"+p[1],p[2]);
				}
				br.close();
				ArrayList<String> ind = new ArrayList<String>(offsets.keySet());
				double[][] sim = new double[offsets.size()][nsys];
				for (int i =0;i<offsets.size();i++){
					for(int j=0;j<nsys;j++){
						sim[i][j] = 0.0;
					}
				}
				BufferedWriter bw = new BufferedWriter(new FileWriter("/users11/nfrajani/data/ensemble/sys_w2vsim_dev"));
				for(int i =0; i <nsys;i++){
					br = new BufferedReader(new FileReader(REOutputs[i])); //emra_3notes_gt_reconciled.tsv	
					while((line=br.readLine())!=null){
						String[] p = line.split("\t");
						String key = p[0]+"~"+p[2];
						if(!offsets.containsKey(key))
							System.out.println(key);
						else{
							sim[ind.indexOf(key)][i] = Double.parseDouble(offsets.get(key));
						}
					}
					br.close();
				}
					for (int i =0;i<offsets.size();i++){
						for(int j=0;j<nsys;j++){
							bw.write(sim[i][j]+",");
						}
						bw.write("\n");
					}
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}

		}
		public static void getFiles(String path){
			System.out.println(path);
			File folder = new File(path);
			File[] listOfFiles = folder.listFiles();
			Arrays.sort(listOfFiles);
			int k=0;
			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isFile()) {
					REOutputs[k] = path+"/"+listOfFiles[i].getName();
					System.out.println(REOutputs[k]);
					k++;
				}
			}
		}
	}
