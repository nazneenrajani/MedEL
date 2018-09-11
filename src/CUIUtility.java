import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class CUIUtility {
	static Map<String,List<String>> cui = new HashMap<String,List<String>>();
	static ArrayList<String> rels = new ArrayList<String>();
	public static void main(String[] args) throws IOException {
		//extractRel();
		//extractCUIThreshold();
		//putCUIThreshold();
		//bgcm();
		w2v();

	}
	private static void w2v() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("/users/nfrajani/word2vec/t"));
		BufferedReader br1 = new BufferedReader(new FileReader("/users/nfrajani/UMLS/id"));
		BufferedWriter bw;
		bw = new BufferedWriter(new FileWriter("/users/nfrajani/word2vec/dict"));
		String line;
		while((line=br.readLine())!=null){
			String id = br1.readLine();
			String t = line.trim().replaceAll("^\\s+","");
			String l = t.trim().replaceAll("\\[", "");
			String out = l.replaceAll(" +", ",");
			bw.write(id+"\t"+out);
			bw.write("\n");
		}
		br.close();
		br1.close();
		bw.close();
		
	}
	private static void bgcm() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("/users/nfrajani/data/latestEMRA/output/main"));
		BufferedWriter bw;
		bw = new BufferedWriter(new FileWriter("/users/nfrajani/data/latestEMRA/output/bgcm_emra"));
		String line;
		while((line=br.readLine())!=null){
			String[] p = line.split(",");
			for(int i =0;i<8;i++){
				if(p[i].equals("0.0"))
					bw.write("1,");
				else
					bw.write("2,");
			}
			bw.write("\n");
		}
		br.close();
		bw.close();

	}
	private static void putCUIThreshold() throws IOException {
		HashMap<String,String> span = new HashMap<String,String>();
		BufferedReader br = new BufferedReader(new FileReader("/users/nfrajani/tensorflow-socher-ntn-master/data/UMLS/thresh"));
		String line;
		while((line=br.readLine())!=null){
			String[] p = line.split("\t");
			span.put(p[0], p[1]);
		}
		br.close();
		BufferedWriter bw;
		bw = new BufferedWriter(new FileWriter("/users/nfrajani/data/ensemble/finalMCR/output/thresh"));
		br = new BufferedReader(new FileReader("/users/nfrajani/data/ensemble/finalMCR/output/cuis"));
		while((line=br.readLine())!=null){
			if(span.containsKey(line)){
				String s = span.get(line);
				bw.write(s+"\n");
			}
			else{
				bw.write("0.0\n");
			}
		}
		bw.close();
		br.close();

	}
	private static void extractCUIThreshold() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("/users11/nfrajani/tensorflow-socher-ntn-master/data/UMLS/tmp"));
		BufferedWriter bw = new BufferedWriter(new FileWriter("/users11/nfrajani/tensorflow-socher-ntn-master/data/UMLS/thresh"));
		String line;
		String origKey = "C0000726";
		double thresh = 0.0;
		int count=0;
		while((line=br.readLine())!=null){
			//System.out.println(line.trim());
			String[] parts = line.split("\t");
			String key = parts[0];
			if(origKey.equals(key)){
				thresh+=Double.parseDouble(parts[3]);
				count++;
			}
			else{
				thresh = thresh/count;
				count=1;
				bw.write(origKey+"\t"+thresh+"\n");
				origKey=key;
				thresh =0.0;
			}
		}
		br.close();
		bw.close();

	}
	private static void extractRel() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("/users11/nfrajani/UMLS/rel.tsv"));
		String line;
		while((line=br.readLine())!=null){
			//System.out.println(line.trim());
			String[] parts = line.split("\t");
			if(!cui.containsKey(parts[0])){
				List<String> tmp = new ArrayList<String>();
				tmp.add(parts[1]+"\t"+parts[2]);
				cui.put(parts[0],tmp);
			}
			else{
				List<String> tmp = cui.get(parts[0]);
				boolean flag = false;
				for(String s: tmp){
					String[] p = s.split("\t");
					if(p[0].equals(parts[1])){
						flag = true;
						break;
					}
				}
				if(flag==false){
					tmp.add(parts[1]+"\t"+parts[2]);
					cui.put(parts[0],tmp);
				}
			}
		}
		br.close();
		br = new BufferedReader(new FileReader("/users11/nfrajani/UMLS/tt"));
		while((line=br.readLine())!=null){
			//String[] parts = line.split("\t");
			if(cui.containsKey(line.trim())){
				for(String s: cui.get(line.trim()))
					rels.add(line+"\t"+s);
			}
		}
		br.close();
		System.out.println(rels.size());

		BufferedWriter bw = new BufferedWriter(new FileWriter("/users11/nfrajani/UMLS/ty"));
		for(String s: rels){
			bw.write(s+"\n");
		}
		bw.close();

	}

}
