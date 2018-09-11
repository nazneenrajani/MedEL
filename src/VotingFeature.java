import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


public class VotingFeature {

	public static void main(String[] args) throws IOException {
		for(int i =2; i <9;i++){
			Set<String> mv  = new HashSet<String>();
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader("/users/nfrajani/data/mcr_mv"+i));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			String line;
			while((line=br.readLine())!=null){
				mv.add(line);
			}
			br.close();
			BufferedWriter bw = new BufferedWriter(new FileWriter("/users/nfrajani/data/ensemble/finalMCR/output/vote"+i));
			try {
				br = new BufferedReader(new FileReader("/users/nfrajani/data/ensemble/finalMCR/output/mcr_cv"));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			while((line=br.readLine())!=null){
				String[] p = line.split("\t");
				if(mv.contains(p[0]+"\t"+p[1])){
					bw.write("1\n");
				}
				else
					bw.write("0\n");

			}
			br.close();
			bw.close();
		}
	}


}


