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
import java.util.List;
import java.util.Map;
import java.util.Set;


public class ELConstrainedOptimization {
	static Map<String,Map<String,Double>> cuilinks;
	static Map<String,Set<String>> umlslinks;
	static Map<String,Integer> sysrank;
	static List<String> cuis;

	public ELConstrainedOptimization(){
		cuilinks = new HashMap<String,Map<String,Double>>();
		umlslinks = new HashMap<String,Set<String>>();
		sysrank = new HashMap<String,Integer>();
		sysrank.put("cfv",1);
		sysrank.put("cfv_syn",2);
		sysrank.put("Metamap",3);
		sysrank.put("ConceptMapper",4);
		sysrank.put("Ctakes",5);
		sysrank.put("vbbmcr#1",6);
		sysrank.put("gbmcr#1",7);
		sysrank.put("nbmcr#1",8);
	}
	//String year, int nsys, String key, String file, String query)
	public static void main(String[] args) throws IOException {
		ELConstrainedOptimization co = new ELConstrainedOptimization();
		BufferedReader featureReader = null;
		try {
			featureReader = new BufferedReader (new FileReader("/users/nfrajani/data/ensemble/mcr/cv/cv_all"));
		} catch (FileNotFoundException e) {
			System.exit (1);
		}
		String line;
		while ((line = featureReader.readLine()) != null) {
			String[] prov = line.split("\t");
			if(!umlslinks.containsKey(prov[0])){
				Set<String> tmp = new HashSet<String>();
				tmp.add(prov[2]);
				umlslinks.put(prov[0],tmp);
			}
			else{
				Set<String> tmp = umlslinks.get(prov[0]);
				tmp.add(prov[2]);
				umlslinks.put(prov[0],tmp);
			}
			String uniq = prov[0]+"~"+prov[2];
			if(!cuilinks.containsKey(uniq)){
				Map<String,Double> sysconf = new HashMap<String,Double>();
				sysconf.put(prov[1], Double.parseDouble(prov[3]));
				cuilinks.put(uniq,sysconf);
			}
			else{
				Map<String,Double> sysconf = cuilinks.get(uniq);
				sysconf.put(prov[1], Double.parseDouble(prov[3]));
				cuilinks.put(uniq,sysconf);
			}

		}
		featureReader.close();
		//for (int i=721;i<=3171;i++)
			//co.optimize(i);
		co.write();
	}

	public void optimize(int n) throws IOException {
		BufferedWriter br = new BufferedWriter(new FileWriter("/users11/nfrajani/data/ensemble/constrainedoptimization/mcr_cv_co"+n+".m"));
		int x_counter=0;boolean flag,start=true;
		String a ="A=[";
		String x = "x0=[";
		String x1 = "x1=[";
		for(int j=0;j<n;j++){
			a = a+"1 ";
			x = x+"0;";
			x1 = x1+"1;";
		}
		a= a.trim()+"];";
		x=x.substring(0, x.length()-1)+"];";
		x1=x1.substring(0, x1.length()-1)+"];";
		br.write(a); br.write("\n");
		br.write(x); br.write("\n");	
		br.write(x1); br.write("\n");
		for(String key:cuilinks.keySet()){
			x_counter=0;flag=false;
			String[] fields = key.split("~");
			Set<String> tmp = umlslinks.get(fields[0]);
			if(tmp.size()==0){
				br.write("exit;");
				continue;
			}
			for(String k:tmp){
				double b=0.28847;
				x_counter++;
				Map<String,Double> sysconf = cuilinks.get(fields[0]+"~"+k);
				for(String sys: sysconf.keySet()){
					double w =0;
					w = 1/(double)sysrank.get(sys);
					//result+= w*(x-sysconf.get(sys))*(x-sysconf.get(sys));
					if(tmp.size()==n){
						if(n==1)
							System.out.println(fields[0]);
						if(flag){
							br.write("+"+w+"*(x("+x_counter+")-"+sysconf.get(sys)+")*(x("+x_counter+")-"+sysconf.get(sys)+")");
						}
						else{
							if(start){
								start = false;
								br.write("b="+b*n+";");br.write("\n");
								br.write("f = @(x) "+w+"*(x("+x_counter+")-"+sysconf.get(sys)+")*(x("+x_counter+")-"+sysconf.get(sys)+")");
							}
							else{
								br.write(";[x,fval] = fmincon(f,x0,A,b,[],[],x0,x1);");br.write("\n");
								br.write("dlmwrite('s15_"+n+"',x','-append');");br.write("\n");
								br.write("b="+b*n+";");br.write("\n");
								br.write("f = @(x) "+w+"*(x("+x_counter+")-"+sysconf.get(sys)+")*(x("+x_counter+")-"+sysconf.get(sys)+")");
							}
							flag = true;
						}
						//bw.write(fields[0]+"\t"+fields[1]+"\t"+k+"\t"+w+"\t"+sysconf.get(sys)+"\n");
						//System.out.println(fields[0]+"\t"+fields[1]+"\t"+k+"\t"+w+"\t"+sysconf.get(sys));
					}
				}
			}

		}
		br.write(";[x,fval] = fmincon(f,x0,A,b,[],[],x0,x1);");br.write("\n");
		br.write("dlmwrite('s15_"+n+"',x','-append');");br.write("\n");
		br.write("exit;");
		br.close();
	}

	public void write() throws IOException {
		for(int i=1;i<=3171;i++){
			System.out.println(i);
			File f = new File("/users11/nfrajani/data/ensemble/constrainedoptimization/s15_"+i);
			if(f.exists()){
				BufferedWriter br = new BufferedWriter(new FileWriter("/users11/nfrajani/data/ensemble/constrainedoptimization/sconf15_"+i));
				BufferedReader bw = new BufferedReader(new FileReader(f));
				String line; List<String> maxi = new ArrayList<String>();Map<String,String> sysmap;
				while((line=bw.readLine())!=null){
					maxi.add(line);
				}
				bw.close();
				int counter=0;
				for(String key:cuilinks.keySet()){
					sysmap = new HashMap<String,String>();
					String[] fields = key.split("~");
					Set<String> tmp = umlslinks.get(fields[0]);
					if(tmp.size()==i){
						String l = maxi.get(0);
						String[] s = l.split(",");
						counter++;int n =0;
						for(String k:tmp){
							sysmap.put(fields[0]+"~"+k, s[n]);
							n++;
						}
						for(String k:tmp){
							Map<String,Double> sysconf = cuilinks.get(fields[0]+"~"+k);
							for(String sys: sysconf.keySet()){
								br.write(fields[0]+"\t"+k+"\t"+sys+"\t"+sysmap.get(fields[0]+"~"+k));
								br.write("\n");
								//System.out.println(fields[0]+"\t"+fields[1]+"\t"+k+"\t"+sys);
								//temp.add(fields[0]+"\t"+fields[1]+"\t"+k+"\t"+sys);
							}
						}
					}	
				}
				br.close();
				System.out.println(counter);
			}
		}
	}
	public void optimize_list(int n) throws IOException {
		BufferedWriter br = new BufferedWriter(new FileWriter("/users11/nfrajani/data/ensemble/constrainedoptimization/ol_"+n+".m"));
		int x_counter=0;boolean flag,start=true;
		String a ="A=[";
		String x = "x0=[";
		String x1 = "x1=[";
		for(int j=0;j<n;j++){
			a = a+"1 ";
			x = x+"0;";
			x1 = x1+"1;";
		}
		a= a.trim()+"];";
		x=x.substring(0, x.length()-1)+"];";
		x1=x1.substring(0, x1.length()-1)+"];";
		br.write(a); br.write("\n");
		br.write(x); br.write("\n");
		br.write(x1); br.write("\n");
		for(String key:cuilinks.keySet()){
			x_counter=0;flag=false;
			String[] fields = key.split("~");
			Set<String> tmp = umlslinks.get(fields[0]+"~"+fields[1]);
			if(tmp.isEmpty()){
				continue;
			}
			for(String k:tmp){
				double b = 4.88;
				x_counter++;
				Map<String,Double> sysconf = cuilinks.get(fields[0]+"~"+fields[1]+"~"+k);
				for(String sys: sysconf.keySet()){
					double w = 1/(double)sysrank.get(sys);
					if(tmp.size()==n){
						if(flag){
							br.write("+"+w+"*(x("+x_counter+")-"+sysconf.get(sys)+")*(x("+x_counter+")-"+sysconf.get(sys)+")");
						}
						else{
							if(start){
								start = false;
								br.write("b="+tmp.size()*b+";");br.write("\n");
								br.write("f = @(x) "+w+"*(x("+x_counter+")-"+sysconf.get(sys)+")*(x("+x_counter+")-"+sysconf.get(sys)+")");
							}
							else{
								br.write(";[x,fval] = fmincon(f,x0,A,b,[],[],x0,x1);");br.write("\n");
								br.write("dlmwrite('l_"+n+"',x','-append');");br.write("\n");
								br.write("b="+tmp.size()*b+";");br.write("\n");
								br.write("f = @(x) "+w+"*(x("+x_counter+")-"+sysconf.get(sys)+")*(x("+x_counter+")-"+sysconf.get(sys)+")");
							}
							flag = true;
						}
						//                              br.write(fields[0]+"\t"+fields[1]+"\t"+k+"\t"+sys+"\n");
						//if((sysconf.get(sys)*(1/(double)sysrank_14.get(sys)))>0.5) //0.5
						//System.out.println(fields[0]+"\t"+fields[1]+"\toptimized\t*\t"+k+"\t*\t1");
						//System.out.println(fields[0]+"\t"+fields[1]+"\t"+k+"\t"+w+"\t"+sysconf.get(sys));
					}
				}
			}

		}
		br.write(";[x,fval] = fmincon(f,x0,A,b,[],[],x0,x1);");br.write("\n");
		br.write("dlmwrite('l_"+n+"',x','-append');");br.write("\n");
		br.write("exit;");
		br.close();
	}
}
