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


/**
 * @author nfrajani
 *
 */
public class BaseEnsemble {
	static int nsys =3;
	static String[] ELOutputs;
	static boolean majority_voting = true;
	static boolean intersectionOutput = true;
	static boolean unionOutput = true;
	public static void main(String[] args) throws IOException{
		ELOutputs = new String[nsys];
		getFilesFromDir("/users11/nfrajani/data/latestEMRA/mcrunion"); //i2b2/ensemble
		HashMap<String,HashMap<String,ArrayList<Double>>> ensemble = new HashMap<String,HashMap<String,ArrayList<Double>>>();
		//Get files from directory and ensure the order of model outputs for both train and test is same
		BufferedReader br = null;
		for(int i=0; i<nsys;i++){
			System.out.println(ELOutputs[i]);
			try {
				br = new BufferedReader(new FileReader(ELOutputs[i]));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			String line;
			while((line=br.readLine())!=null){
				//System.out.println(line);
				String[] cols = line.split("\t");
				double conf = 0.0;
				conf = Double.parseDouble(cols[2]);
				String key = cols[0];
				String cui = cols[1];
				if(!ensemble.containsKey(key)){
					HashMap<String,ArrayList<Double>> cuiMap = new HashMap<String,ArrayList<Double>>();
					ArrayList<Double> confScore = new ArrayList<Double>();
					for(int j =0; j <nsys;j++)
						confScore.add(0.0);
					confScore.set(i, conf);
					cuiMap.put(cui, confScore);
					ensemble.put(key, cuiMap);
				}
				else{
					HashMap<String,ArrayList<Double>> cuiMap = ensemble.get(key);
					if(!cuiMap.containsKey(cui)){
						ArrayList<Double> confScore = new ArrayList<Double>();
						for(int j =0; j <nsys;j++)
							confScore.add(0.0);
						confScore.set(i, conf);
						cuiMap.put(cui, confScore);
						ensemble.put(key, cuiMap);
					}
					else{
						ArrayList<Double> confScore = cuiMap.get(cui);
						confScore.set(i, conf);
						cuiMap.put(cui, confScore);
						ensemble.put(key, cuiMap);
					}
				}
			}
		}
		if(majority_voting==true)
			majorityVoting(ensemble);
		if(intersectionOutput==true)
			intersection(ensemble);
		if(unionOutput==true)
			union(ensemble);
	}

	public static void majorityVoting(HashMap<String,HashMap<String,ArrayList<Double>>> ensemble) throws IOException{
			BufferedWriter bw=null;
			try {
				bw = new BufferedWriter(new FileWriter("/users11/nfrajani/data/mv"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			for(String qid: ensemble.keySet()){
				for(String cui:ensemble.get(qid).keySet()){
					ArrayList<Double> confScore = ensemble.get(qid).get(cui);
					int vote =0;
					for(int i=0; i <confScore.size();i++){
						if(confScore.get(i)>0.0)
							vote++;
					}
					//if(vote>=Math.ceil(nsys/2.0)){
						if(vote>=3){
						bw.write(qid+"\t"+cui+"\n");
					}
				}
			}
			bw.close();
		}
	public static void intersection(HashMap<String,HashMap<String,ArrayList<Double>>> ensemble) throws IOException{
		BufferedWriter bw=null;
		try {
			bw = new BufferedWriter(new FileWriter("/users11/nfrajani/data/mcr_int.txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(String qid: ensemble.keySet()){
			for(String cui:ensemble.get(qid).keySet()){
				ArrayList<Double> confScore = ensemble.get(qid).get(cui);
				boolean isZero=false;
				for(int i=0; i <confScore.size();i++){
					if(confScore.get(i)==0.0){
						isZero=true;
						break;
					}
				}
				if(isZero==false){
					bw.write(qid+"\t"+cui+"\n");
				}
			}
		}
		bw.close();
	}
	public static void union(HashMap<String,HashMap<String,ArrayList<Double>>> ensemble) throws IOException{
		BufferedWriter bw=null;
		try {
			bw = new BufferedWriter(new FileWriter("/users11/nfrajani/data/union"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(String qid: ensemble.keySet()){
			for(String cui:ensemble.get(qid).keySet()){
				ArrayList<Double> confScore = ensemble.get(qid).get(cui);
				boolean isZero=true;
				for(int i=0; i <confScore.size();i++){
					if(confScore.get(i)>0.0){
						isZero=false;
						break;
					}
				}
				if(isZero==false){
					bw.write(qid+"\t"+cui+"\n");
				}
			}
		}
		bw.close();
	}
	public static void getFilesFromDir(String path){
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		int k=0;
		Arrays.sort(listOfFiles);
		for(int i=0;i<listOfFiles.length;i++){
			if(listOfFiles[i].isFile()){
				//System.out.println(listOfFiles[i].getName());
				ELOutputs[k] = path+"/"+listOfFiles[i].getName();
				k++;
			}
		}
	}
}
