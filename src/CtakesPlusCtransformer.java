import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class CtakesPlusCtransformer {

		static Map<String,String> cui = new HashMap<String,String>();
		static ArrayList<String> rels = new ArrayList<String>();
		public static void main(String[] args) throws IOException {
			BufferedReader br = new BufferedReader(new FileReader("/users11/nfrajani/data/semeval/scmap"));
			String line;
			while((line=br.readLine())!=null){
				//System.out.println(line.trim());
				String[] parts = line.split("\t");
				cui.put(parts[0],parts[1]);
			}
			br.close();
			br = new BufferedReader(new FileReader("/users11/nfrajani/data/semeval/sctransformer"));
			while((line=br.readLine())!=null){
				String[] parts = line.split("\t");
				if(cui.containsKey(parts[0])){
					System.out.println(line);
				}
			}
			br.close();
			
/*			BufferedWriter bw = new BufferedWriter(new FileWriter("/users11/nfrajani/UMLS/mcr_cv_rel_cuis"));
			for(String s: rels){
				bw.write(s+"\n");
			}
			bw.close();*/
		}


}
