import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class CUIUtility2 {
	static Map<String,String> cui = new HashMap<String,String>();
	static ArrayList<String> rels = new ArrayList<String>();
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("/users11/nfrajani/data/emra_3notes.tsv"));//i2b2/output/i2b2_100_train_no_drugs.tsv
		String line;
		while((line=br.readLine())!=null){
			//System.out.println(line.trim());
			String[] parts = line.split("\t");
				cui.put(parts[0],parts[1]); //2
		}
		br.close();
		br = new BufferedReader(new FileReader("/users11/nfrajani/data/latestEMRA/output2/u"));
		while((line=br.readLine())!=null){
			//String[] parts = line.split("\t");
					rels.add(line+"\t"+cui.get(line.trim()));
			}
		br.close();
		System.out.println(rels.size());

		BufferedWriter bw = new BufferedWriter(new FileWriter("/users11/nfrajani/data/latestEMRA/output2/s"));
		for(String s: rels){
			bw.write(s+"\n");
		}
		bw.close();
	}

}
