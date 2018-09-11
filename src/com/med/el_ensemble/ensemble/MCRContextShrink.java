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

public class MCRContextShrink {
	public static void main(String[] args) throws IOException {
		List<String> offsets = new ArrayList<String>();
		List<Integer> linebreak = new ArrayList<Integer>();
		BufferedReader br;
		String line;
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
		br = new BufferedReader(new FileReader("/users11/nfrajani/data/mcr2014experiment.tsv"));
		BufferedWriter bw = new BufferedWriter(new FileWriter("/users11/nfrajani/data/mcr_out"));
		while((line=br.readLine())!=null){
			String[] parts = line.split("\t");
			String[] sentences = parts[1].split("\\. ");
			String span = parts[2].replaceAll("\\.", "");
			span = span.replaceAll(",","");
			//System.out.println(span);
			String[] words = span.split(" ");
			boolean flag = false;
			for(int j =0; j<sentences.length;j++){
				int count =0;
				String context = sentences[j].replaceAll("\\.", "");
				context = context.replaceAll(",","");
				System.out.println(context);
				for(int i =0; i <words.length;i++){
					if(context.contains(words[i])){
						count++;
					}
				}
				if(count == words.length){
					bw.write(parts[0]+"\t"+sentences[j]+"\t"+parts[2]+"\n");
					flag = true;
					break;
				}
			}
			
			if(flag == false){
				bw.write(parts[0]+"\t"+parts[2]+"\t"+parts[2]+"\n");
			}
		}
		br.close();
		bw.close();
	}
}
