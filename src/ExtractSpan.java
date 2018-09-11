import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;


public class ExtractSpan {

	public static void main(String[] args) throws IOException {
		HashMap<String,String> span = new HashMap<String,String>();
		BufferedReader br = new BufferedReader(new FileReader("/users/nfrajani/data/mcr_span"));
		String line;
		while((line=br.readLine())!=null){
			String[] p = line.split("\t");
			span.put(p[0], p[1]);
		}
		br.close();
		BufferedWriter bw;
		for(int i=1;i<9;i++){
			bw = new BufferedWriter(new FileWriter("/users/nfrajani/data/ensemble/finalMCR/docsim/"+i));
			br = new BufferedReader(new FileReader("/users/nfrajani/data/ensemble/finalMCR/cv/"+i));
			while((line=br.readLine())!=null){
				String[] p = line.split("\t");
				String s = span.get(p[0]);
				bw.write(p[0]+"\t"+p[1]+"\t"+p[2]+"\t"+s+"\n");
			}
			bw.close();
		}
		br.close();
	}

}
