import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CUIRelationEmbedding {

	public static void main(String[] args) throws IOException{
		//relToVec();
		relToCUI();
		
	}

	private static void relToCUI() throws IOException {
		List<String> cui = new ArrayList<String>();
		Map<String,String> vect = new HashMap<String,String>();
		BufferedReader br = new BufferedReader(new FileReader("/users/nfrajani/data/ensemble/finalMCR/output/rel"));
		BufferedWriter bw = new BufferedWriter(new FileWriter("/users/nfrajani/data/ensemble/finalMCR/output/rel_cuis"));
		String line;
		while((line=br.readLine())!=null){
			String[] p = line.split("\t");
			vect.put(p[0], p[1]);
		}
		br.close();
		br = new BufferedReader(new FileReader("/users/nfrajani/data/ensemble/finalMCR/output/cuis"));
		while((line=br.readLine())!=null){
			if(vect.containsKey(line)){
				String v = vect.get(line);
				bw.write(line+"\t"+v+"\n");
			}
		}
		br.close();
		bw.close();
	}

	private static void relToVec() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("/users/nfrajani/data/word_vectors_mcr"));
		BufferedWriter bw = new BufferedWriter(new FileWriter("/users/nfrajani/data/wv_mcr"));
		String line;
		int count =0;
		while((line=br.readLine())!=null){
			bw.write(line+"\t");
			count++;
			if(count%200==0)
				bw.write("\n");
		}
		bw.close();
		br.close();
		
	}
}

