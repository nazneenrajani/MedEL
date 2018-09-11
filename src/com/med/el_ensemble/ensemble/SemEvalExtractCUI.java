package com.med.el_ensemble.ensemble;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * @author nfrajani
 *
 */

public class SemEvalExtractCUI {
	public static void main(String[] args) {
		List<String> offsets = new ArrayList<String>();
		List<Integer> linebreak = new ArrayList<Integer>();
		BufferedReader br;
		try{
			br = new BufferedReader(new FileReader("/users11/nfrajani/data/semeval/annotations/19791-003873-DISCHARGE_SUMMARY.pipe.txt"));
			String line;
			try {
				while((line=br.readLine())!=null){
					String off;
					String[] splits = line.split("\\|\\|");
					if(splits.length==5){
						if(!splits[2].startsWith("CUI")){
							off = splits[2]+"~"+splits[splits.length-2]+"~"+splits[splits.length-1];
							offsets.add(off);
						}
					}
					else{
						if(!splits[2].startsWith("CUI")){
							off = splits[2];
							int j = splits.length-4;
							for(int i =j; i <splits.length;i++){
								off = off+"~"+splits[i];
							}
							offsets.add(off);
						}
					}
				}
				br.close();
				br = new BufferedReader(new FileReader("/users11/nfrajani/data/semeval/sources/19791-003873-DISCHARGE_SUMMARY.txt"));
				BufferedWriter bw = new BufferedWriter(new FileWriter("/users11/nfrajani/data/semeval/out4.txt"));
				String doc="";
				while((line=br.readLine())!=null){
					linebreak.add(doc.length());
					doc+="\n"+line;
				}
				br.close();
				int count=0;
				for(String s: offsets){
					count++;
					String context;
					String[] splits = s.split("~");
					String span ="";
					int start=0,end=0;
					if(splits.length>3){
						boolean acrossParas=true;
						for(int i=1;i<splits.length;i=i+2){
							span +=doc.substring(Integer.parseInt(splits[i])+1,Integer.parseInt(splits[i+1])+1)+" ";	
						}
						int ws = Integer.parseInt(splits[1])+1;
						int we = Integer.parseInt(splits[splits.length-1])+1;
						for(int j=0;j<linebreak.size()-1;j++){
							if(linebreak.get(j)<=ws&&we<=linebreak.get(j+1)){
								start = linebreak.get(j);
								end = linebreak.get(j+1);
								acrossParas=false;
								break;
							}
						}
						//Certain spans would be across multiple paragraphs
						if(acrossParas==true){
							for(int j=0;j<linebreak.size()-1;j++){
								if(linebreak.get(j)<ws&&we<linebreak.get(j+2)){
									start = linebreak.get(j);
									end = linebreak.get(j+2);
									break;
								}
							}
						}
						context = doc.substring(start+1, end);
						context.replaceAll("\n", "");
						context.replaceAll("\t", "");
						bw.write(splits[0]+"\t"+context.trim()+"\t"+span.trim()+"\n");
					}
					else{
						span +=doc.substring(Integer.parseInt(splits[1])+1,Integer.parseInt(splits[2])+1);
						int ws = Integer.parseInt(splits[1])+1;
						int we = Integer.parseInt(splits[2])+1;
						for(int j=0;j<linebreak.size()-1;j++){
							if(linebreak.get(j)<=ws&&we<=linebreak.get(j+1)){
								start = linebreak.get(j);
								end = linebreak.get(j+1);
								break;
							}
						}
						context = doc.substring(start+1, end);
						context = context.replaceAll("\n", " ");
						context.replaceAll("\t", "");
						bw.write(splits[0]+"\t"+context.trim()+"\t"+span.trim()+"\n");
					}
				}
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
	}

}
