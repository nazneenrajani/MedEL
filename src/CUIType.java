import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.TreeSet;

import com.ibm.watsonmd.tools.umls.ConceptBrowserAPI;


public class CUIType {

	public static void main(String[] args) throws IOException {
		HashMap<String,Integer> cuitype = new HashMap<String,Integer>();
		try {
			BufferedReader br = new BufferedReader(new FileReader("/users/nfrajani/data/latestEMRA/output3/all"));
			String line;
			int count =-1;
			while((line=br.readLine())!=null){
				String cui =line.split("\t")[1].trim();
				TreeSet<String> semanticTypes = ConceptBrowserAPI.cui2sty(cui);
				if(semanticTypes != null){
					for(String st : semanticTypes){
						if(!cuitype.containsKey(ConceptBrowserAPI.tui2abbr(st))){
							count++;
							cuitype.put(ConceptBrowserAPI.tui2abbr(st), count);
						}
					}
				} 
			}
			br.close();
			System.out.println(cuitype.size());
			BufferedWriter bw = new BufferedWriter(new FileWriter("/users/nfrajani/data/latestEMRA/output3/type"));
			br = new BufferedReader(new FileReader("/users/nfrajani/data/latestEMRA/output3/emra"));
			while((line=br.readLine())!=null){
				int[] ctype = new int[cuitype.size()];
				String cui =line.split("\t")[1].trim();
				TreeSet<String> semanticTypes = ConceptBrowserAPI.cui2sty(cui);
				if(semanticTypes != null){
					for(String st : semanticTypes){
						int i = cuitype.get(ConceptBrowserAPI.tui2abbr(st));
						ctype[i] = 1;
						if(i==87)
							System.out.println(line);
					}
				}
				bw.write(line+"\t");
				for (int j =0; j <ctype.length;j++){
					bw.write(ctype[j]+",");
				}
				bw.write("\n");
			}
			bw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		
	}

}
/*StringBuffer semType = new StringBuffer();
TreeSet<String> semanticTypes = ConceptBrowserAPI.cui2sty(cui);
if(semanticTypes != null){
	for(String st : semanticTypes){
		if(semType.length() > 0){
			semType.append("|");
		}
		semType.append(ConceptBrowserAPI.tui2abbr(st));
		System.out.println(semType);
	}
} 
}*/