package com.med.el_ensemble.ensemble;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * @author nfrajani
 *
 */
public class ELScorer {
	public static void CUILevelScorer(String gtfile, String evalfile,HashMap<String,HashMap<String,Integer>> gtentries, HashMap<String, ArrayList<String>> evalentries, String sys) throws IOException{
		BufferedWriter bw = new BufferedWriter(new FileWriter("/users/nfrajani/data/ngt/emra/"+sys));
		int tp = 0, fp =0 ,tn =0, fn=0;
		Set<String> extractions = new HashSet<String>();
		for(String evalkey: evalentries.keySet()){
			if(!gtentries.containsKey(evalkey)){
				System.err.println("No GT for this entry "+evalkey);
			}
			else{
				ArrayList<String> evallist = evalentries.get(evalkey);
				for(int i =0; i <evallist.size();i++){
					HashMap<String,Integer> gtval = gtentries.get(evalkey);
					if(gtval.containsKey(evallist.get(i))){
						extractions.add(evalkey+"~"+evallist.get(i));
						int score = gtval.get(evallist.get(i));
						if(score==1){
							tp++;
							//System.err.println(evalkey+"\t"+evallist.get(i));
						}
						else 
							fp++;
					}
					else{
						//fp++;
						bw.write(evalkey+"\t"+evallist.get(i)+"\t0\n");
						System.err.println(evalkey+"\t"+evallist.get(i)+"\t0");
					}
				}
			}
		}
		//System.out.println("TP = "+tp);
		//System.out.println("FP = "+fp);
		for(String key: gtentries.keySet()){
			for(String cui: gtentries.get(key).keySet()){
				if(!extractions.contains(key+"~"+cui)){					
					if(gtentries.get(key).get(cui).equals(1)){
						fn++;
						//System.out.println(key+"\t"+cui);
					}
				}
			}
		}
		bw.close();
		//System.out.println("FN = "+fn);
		double precision = tp*1.0 /(tp+fp);
		double recall = tp*1.0/ (tp+fn);
		double f1 = 2.0*precision*recall/(precision+recall);
		System.out.println("CUI level Score");
		System.out.println("Precision\tRecall\tF1");
		System.out.println(String.format("%.4g", precision)+"\t"+String.format("%.4g", recall)+"\t"+String.format("%.4g", f1));
	}
	public static void FactorLevelScorer(String gtfile, String evalfile,HashMap<String,HashMap<String,Integer>> gtentries, HashMap<String, ArrayList<String>> evalentries, int gttp){
		int tp = 0, fp =0 , fn=0;
		Set<String> factors = new HashSet<String>();
		for(String evalkey: evalentries.keySet()){
			boolean istp = false;
			if(!gtentries.containsKey(evalkey)){
				//System.err.println("No GT for this entry "+evalkey);
			}
			else{
				ArrayList<String> evallist = evalentries.get(evalkey);
				for(int i =0; i <evallist.size();i++){
					HashMap<String,Integer> gtval = gtentries.get(evalkey);
					if(gtval.containsKey(evallist.get(i))){
						factors.add(evalkey);
						int score = gtval.get(evallist.get(i));
						if(score==1){
							istp=true;
							break;
						}
					}
					else{
						//fp++;
						//System.err.println(evalkey+"\t"+evallist.get(i)+"\t0");

					}
				}
				if(istp==true){
					tp++;
					//System.err.println(evalkey);
				}
				else{
					fp++;
					//System.err.println(evalkey);
				}
			}
		}
		//System.out.println("TP = "+tp);
		//System.out.println("FP = "+fp);
		for(String key: gtentries.keySet()){
			for(String cui: gtentries.get(key).keySet()){
				if(!factors.contains(key)){
					//System.out.println(gtentries.get(key).get(cui));
					if(gtentries.get(key).get(cui)==1){
						fn++;
						//System.err.println(key +"\t"+cui);
						break;
					}
				}
			}
		}
		//System.out.println("FN = "+fn);
		double precision = tp*1.0 /(tp+fp);
		double recall = tp*1.0/ gttp;
		double f1 = 2.0*precision*recall/(precision+recall);
		System.out.println("Factor level Score");
		System.out.println("Precision\tRecall\tF1");
		System.out.println(String.format("%.4g", precision)+"\t"+String.format("%.4g", recall)+"\t"+String.format("%.4g", f1));
	}
	public static void QuantumLevelScorer(String gtfile, String evalfile,HashMap<String,HashMap<String,Integer>> gtentries, HashMap<String, ArrayList<String>> evalentries, int gttp){
		double tp = 0.0, fp =0.0 , fn=0;
		Map<String,Integer> factors = new HashMap<String,Integer>();
		for(String evalkey: evalentries.keySet()){
			int ccount=0, icount=0, count=0;
			if(!gtentries.containsKey(evalkey)){
				//System.err.println("No GT for this entry "+evalkey);
			}
			else{
				//System.out.println(evalkey);
				ArrayList<String> evallist = evalentries.get(evalkey);
				for(int i =0; i <evallist.size();i++){
					HashMap<String,Integer> gtval = gtentries.get(evalkey);
					if(gtval.containsKey(evallist.get(i))){
						count++;
						int score = gtval.get(evallist.get(i));
						if(score==1)
							ccount++;
						else
							icount++;
					}
					else{
						//fp++;
						//System.err.println(evalkey+"\t"+evallist.get(i)+"\t0");
					}
				}
				if(count>0){
					tp = tp+(ccount*1.0/count);
					fp= fp+ (icount*1.0/count);
				}
				factors.put(evalkey,count);
			}
		}

		double tpfn =0.0;
		for(String key: gtentries.keySet()){
			int gt = 0;
			for(String cui: gtentries.get(key).keySet()){
				if(gtentries.get(key).get(cui)==1){
					gt++;

				}
			}
			if(factors.containsKey(key)){
				//System.out.println("system correct "+ tmp.get(0));
				//System.out.println("system total "+ tmp.get(1));
				//System.out.println("gt "+ gt);
				//System.out.println("total "+ factors.get(key));
				//if((gt/factors.get(key)) > 1.0)
					//tpfn = tpfn + 1.0;
				//else
					tpfn = tpfn + (gt*1.0/factors.get(key));
			}
			else{
				tpfn = tpfn + gt*1.0;
				//System.err.println("this case");
			}
		}
		//System.out.println("TP = "+ tp);
		//System.out.println("TPFN = "+tpfn);
		double precision = tp*1.0 /(tp+fp);
		double recall = tp*1.0/(tpfn);
		double f1 = 2.0*precision*recall/(precision+recall);
		System.out.println("Quantum Score");
		System.out.println("Precision\tRecall\tF1");
		System.out.println(String.format("%.4g", precision)+"\t"+String.format("%.4g", recall)+"\t"+String.format("%.4g", f1));
	}
	public static void main(String[] args) throws IOException {
		boolean isSingle = true;
		if(isSingle ==true){
			String sys = "p";
			boolean CUIScorer = true;
			boolean FactorScorer = true;
			boolean QuantumScorer =true;
			HashMap<String,HashMap<String,Integer>> gtentries= new HashMap<String,HashMap<String,Integer>>();
			HashMap<String,ArrayList<String>> evalentries= new HashMap<String,ArrayList<String>>();
			HashMap<String,ArrayList<String>> bic = new HashMap<String,ArrayList<String>>();
			Set<String> uniq = new HashSet<String>();
			Set<String> twoscore = new HashSet<String>();
			String gtfile = "/users/nfrajani/data/emra_complete_gt.tsv";//ensemble/keys/bic/mcrkey_cv"; //args[0] //
			String evalfile ="/users/nfrajani/data/latestEMRA/output2/"+sys;//ensemble/output/p";//ensemble/output/tmp"; //args[1]
			boolean isStrict = true;//Boolean.parseBoolean(args[2]);
			try {
				BufferedReader brgt = new BufferedReader(new FileReader(gtfile));
				String line;
				try {
					while((line=brgt.readLine())!=null){
						String[] parts = line.split("\t");
						String gtkey = parts[0]; // QID-span
						int score = Integer.parseInt(parts[2]);
						if(score == 1 || score == 2)
							uniq.add(gtkey);
						if(!gtentries.containsKey(gtkey)){
							HashMap<String,Integer> gtval = new HashMap<String,Integer>();
							if(isStrict==false){
								if(score==2)
									gtval.put(parts[1],1);
								else
									gtval.put(parts[1],score);
							}
							else{
								if(score==2){
									gtval.put(parts[1],1);
									twoscore.add(parts[0]);
								}
								else{
									if(score==1 && !twoscore.contains(parts[0])){
										gtval.put(parts[1],score);
										if(!bic.containsKey(parts[0])){
											ArrayList<String> tmp = new ArrayList<String>();
											tmp.add(parts[1]);
											bic.put(parts[0],tmp);
										}
										else{
											ArrayList<String> tmp = bic.get(parts[0]);
											tmp.add(parts[1]);
											bic.put(parts[0],tmp);
										}
									}
									else
										gtval.put(parts[1],score);
								}
							}
							//gtval.put(parts[1],Integer.parseInt(parts[2])); // CUI ~ Score
							gtentries.put(gtkey, gtval);
						}
						else{
							HashMap<String,Integer> gtval = gtentries.get(gtkey);
							if(isStrict==false){
								if(score==2)
									gtval.put(parts[1],1);
								else
									gtval.put(parts[1],score);
							}
							else{
								if(score==2){
									if(!twoscore.contains(parts[0])){
										if(bic.containsKey(parts[0])){
											ArrayList<String> tmp = bic.get(parts[0]);
											for(Iterator<String> itr = tmp.iterator();itr.hasNext();){
												String str = itr.next();
												gtval.put(str,0);
												itr.remove();
											}
											bic.remove(parts[0]);
											gtval.put(parts[1], 1);
										}
										else{
											twoscore.add(parts[0]);
											gtval.put(parts[1], 1);
										}
									}
									else{
										twoscore.add(parts[0]);
										gtval.put(parts[1], 1);
									}
								}
								else{
									if(score==1){
										if(twoscore.contains(parts[0]))
											gtval.put(parts[1],0);
										else if(bic.containsKey(parts[0])){
											ArrayList<String> tmp = bic.get(parts[0]);
											tmp.add(parts[1]);
											bic.put(parts[0], tmp);
											gtval.put(parts[1],score);	
										}
										else{ 
											ArrayList<String> tmp = new ArrayList<String>();
											tmp.add(parts[1]);
											bic.put(parts[0],tmp);
											gtval.put(parts[1],score);
										}	
									}
									else
										gtval.put(parts[1],score);
								}
								//gtval.put(parts[1],Integer.parseInt(parts[2])); // CUI ~ Score
								gtentries.put(gtkey, gtval);
							}
						}
					}
					brgt.close();
					System.out.println(gtentries.size());
					BufferedReader breval = new BufferedReader(new FileReader(evalfile));
					while((line=breval.readLine())!=null){
						//System.out.println(line);
						String[] parts = line.split("\t");
						String evalkey = parts[0]; // QID ~ span
						if(!evalentries.containsKey(evalkey)){
							ArrayList<String> cuis = new ArrayList<String>();
							cuis.add(parts[1]);
							evalentries.put(evalkey, cuis);
						}
						else{
							ArrayList<String> cuis = evalentries.get(evalkey);
							cuis.add(parts[1]);
							evalentries.put(evalkey, cuis);
						}
					}
					breval.close();

				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			System.out.println("-----------------------------------------------");
			int gttp = uniq.size();
			System.out.println("GTTP = "+gttp);
			if(CUIScorer==true)
				CUILevelScorer(gtfile, evalfile,gtentries,evalentries, sys);
			if(FactorScorer==true)
				FactorLevelScorer(gtfile, evalfile, gtentries, evalentries, gttp);
			if(QuantumScorer==true)
				QuantumLevelScorer(gtfile, evalfile, gtentries, evalentries, gttp);
			/*try {
				BufferedWriter bw = new BufferedWriter(new FileWriter("/users/nfrajani/data/ensemble/finalMCR/mcr_complete_gt_strict.tsv"));
				for(String s: gtentries.keySet()){
					for(String t: gtentries.get(s).keySet()){
						bw.write(s+"\t"+t+"\t"+gtentries.get(s).get(t)+"\n");
					}
				}
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		}
		else{
			List<String> systems = new ArrayList<String>();
			systems.add("gbmcr3");
			systems.add("nbmcr3");
			systems.add("vbmcr3");
			systems.add("cfv");
			//systems.add("cfv_syn");
			systems.add("ctakes");
			systems.add("mmap");
			//systems.add("cmap");
			systems.add("cmap_ctransformer");
			systems.add("str1");
			for(String sys: systems){
				System.out.println("----------------------------------------------------");
				System.out.println(sys);
				boolean CUIScorer = true;
				boolean FactorScorer = true;
				boolean QuantumScorer =true;
				HashMap<String,HashMap<String,Integer>> gtentries= new HashMap<String,HashMap<String,Integer>>();
				HashMap<String,ArrayList<String>> evalentries= new HashMap<String,ArrayList<String>>();
				HashMap<String,ArrayList<String>> bic = new HashMap<String,ArrayList<String>>();
				Set<String> uniq = new HashSet<String>();
				Set<String> twoscore = new HashSet<String>();
				String gtfile = "/users/nfrajani/data/emra_complete_gt.tsv";//data/emra_gt_final.tsv";///ensemble/mcr_complete_gt.tsv";//ensemble/keys/bic/mcrkey_cv"; //args[0] //
				String evalfile ="/users/nfrajani/data/latestEMRA/s"+sys;//ensemble/output/p";//ensemble/output/tmp"; //args[1]
				boolean isStrict = false;//Boolean.parseBoolean(args[2]);
				try {
					BufferedReader brgt = new BufferedReader(new FileReader(gtfile));
					String line;
					try {
						while((line=brgt.readLine())!=null){
							String[] parts = line.split("\t");
							String gtkey = parts[0]; // QID-span
							int score = Integer.parseInt(parts[2]);
							if(score == 1 || score == 2)
								uniq.add(gtkey);
							if(!gtentries.containsKey(gtkey)){
								HashMap<String,Integer> gtval = new HashMap<String,Integer>();
								if(isStrict==false){
									if(score==2)
										gtval.put(parts[1],1);
									else
										gtval.put(parts[1],score);
								}
								else{
									if(score==2){
										gtval.put(parts[1],1);
										twoscore.add(parts[0]);
									}
									else{
										if(score==1 && !twoscore.contains(parts[0])){
											gtval.put(parts[1],score);
											if(!bic.containsKey(parts[0])){
												ArrayList<String> tmp = new ArrayList<String>();
												tmp.add(parts[1]);
												bic.put(parts[0],tmp);
											}
											else{
												ArrayList<String> tmp = bic.get(parts[0]);
												tmp.add(parts[1]);
												bic.put(parts[0],tmp);
											}
										}
										else
											gtval.put(parts[1],score);
									}
								}
								//gtval.put(parts[1],Integer.parseInt(parts[2])); // CUI ~ Score
								gtentries.put(gtkey, gtval);
							}
							else{
								HashMap<String,Integer> gtval = gtentries.get(gtkey);
								if(isStrict==false){
									if(score==2)
										gtval.put(parts[1],1);
									else
										gtval.put(parts[1],score);
								}
								else{
									if(score==2){
										if(!twoscore.contains(parts[0])){
											if(bic.containsKey(parts[0])){
												ArrayList<String> tmp = bic.get(parts[0]);
												for(Iterator<String> itr = tmp.iterator();itr.hasNext();){
													String str = itr.next();
													gtval.put(str,0);
													itr.remove();
												}
												bic.remove(parts[0]);
												gtval.put(parts[1], 1);
											}
											else{
												twoscore.add(parts[0]);
												gtval.put(parts[1], 1);
											}
										}
										else{
											twoscore.add(parts[0]);
											gtval.put(parts[1], 1);
										}
									}
									else{
										if(score==1){
											if(twoscore.contains(parts[0]))
												gtval.put(parts[1],0);
											else if(bic.containsKey(parts[0])){
												ArrayList<String> tmp = bic.get(parts[0]);
												tmp.add(parts[1]);
												bic.put(parts[0], tmp);
												gtval.put(parts[1],score);	
											}
											else{ 
												ArrayList<String> tmp = new ArrayList<String>();
												tmp.add(parts[1]);
												bic.put(parts[0],tmp);
												gtval.put(parts[1],score);
											}	
										}
										else
											gtval.put(parts[1],score);
									}
									//gtval.put(parts[1],Integer.parseInt(parts[2])); // CUI ~ Score
									gtentries.put(gtkey, gtval);
								}
							}
						}
						brgt.close();
						System.out.println(gtentries.size());
						BufferedReader breval = new BufferedReader(new FileReader(evalfile));
						while((line=breval.readLine())!=null){
							//System.out.println(line);
							String[] parts = line.split("\t");
							String evalkey = parts[0]; // QID ~ span
							if(!evalentries.containsKey(evalkey)){
								ArrayList<String> cuis = new ArrayList<String>();
								cuis.add(parts[1]);
								evalentries.put(evalkey, cuis);
							}
							else{
								ArrayList<String> cuis = evalentries.get(evalkey);
								cuis.add(parts[1]);
								evalentries.put(evalkey, cuis);
							}
						}
						breval.close();

					} catch (IOException e) {
						e.printStackTrace();
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				System.out.println("-----------------------------------------------");
				int gttp = uniq.size();
				System.out.println("GTTP = "+gttp);
				if(CUIScorer==true)
					CUILevelScorer(gtfile, evalfile,gtentries,evalentries, sys);
				if(FactorScorer==true)
					FactorLevelScorer(gtfile, evalfile, gtentries, evalentries, gttp);
				if(QuantumScorer==true)
					QuantumLevelScorer(gtfile, evalfile, gtentries, evalentries, gttp);

				/// print the stricter file version
				/*		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("/users/nfrajani/data/mcr2014experiment_gt_strict.tsv"));
			for(String s: gtentries.keySet()){
				for(String t: gtentries.get(s).keySet()){
					bw.write(s+"\t"+t+"\t"+gtentries.get(s).get(t)+"\n");
				}
			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
			}
		}
	}
}
