package com.med.el_ensemble.ensemble;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class COAggregate {

	public static void main(String[] args) throws IOException {
		HashMap<Integer, Integer> sysrank = new HashMap<Integer,Integer>();
		sysrank.put(0,6); //cfv
		sysrank.put(1,4); //syn
		sysrank.put(2,2); //mmap
		sysrank.put(3,5);//cmap
		sysrank.put(4,3); //ctakes
		sysrank.put(5,7); //vb
		sysrank.put(6,1); //gb
		//sysrank.put(6,8); //nb
		//String sys = "vbbmcr#1";
		int nsys =7;
		HashMap<String,Double> offsets = new HashMap<String,Double>();
		BufferedReader br;
		try{
			br = new BufferedReader(new FileReader("/users/nfrajani/data/ensemble/finalMCR/output/main"));
			BufferedWriter bw = new BufferedWriter(new FileWriter("/users11/nfrajani/data/ensemble/finalMCR/output/o"));
			String line;
			double[] conf = new double[nsys];
			try {
				while((line=br.readLine())!=null){
					String[] p = line.split(",");
					int count = 0;
					for(int i=0; i< nsys;i++){
						conf[i] = Double.parseDouble(p[i]);
						if(conf[i]==1.0)
							count++;
					}
					if(count>1){
						for(int i=0; i< nsys;i++){
							if(conf[i]==1.0)
								conf[i] = 1.0/sysrank.get(i);
							bw.write(conf[i]+",");
						}
					}
						else{
							for(int i=0; i< nsys;i++){
								bw.write(conf[i]+",");
							}
						}
						bw.write("\n");
					}
					br.close();
					//br = new BufferedReader(new FileReader("/users/nfrajani/data/ensemble/output/mcr_cv"));
					//BufferedWriter bw = new BufferedWriter(new FileWriter("/users11/nfrajani/data/ensemble/constrainedoptimization/"+sys));
					/*				while((line=br.readLine())!=null){
					if(offsets.containsKey(line)){
						bw.write(offsets.get(line).toString());
						bw.write("\n");
					}
					else
						bw.write("0.0\n");
				}	*/			
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}

		}
	}
