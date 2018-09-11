import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


public class AverageCorrectCUIS {

	public static void main(String[] args) {
		HashMap<String,int[]> cui = new HashMap<String,int[]>();
		Set<String> c = new HashSet<String>();
		BufferedReader br;
		try{
			br = new BufferedReader(new FileReader("/users/nfrajani/data/ensemble/mcr/cv/cv_all"));
			String line;
			try {
				while((line=br.readLine())!=null){
					String[] parts = line.split("\t");
					String qid = parts[0];
					//int score = Integer.parseInt(parts[2]);
					if(cui.containsKey(qid)){
						//if(!c.contains(qid+"~"+parts[1])){
							c.add(qid+"~"+parts[1]);
							int[] tmp = cui.get(qid);
							//if(score>0)
								//tmp[0]++;
							tmp[1]++;
							cui.put(qid, tmp);
						//}
					}
					else{
						c.add(qid+"~"+parts[1]);
						int[] tmp = new int[2];
						//if(score>0)
							//tmp[0]++;
						tmp[1]++;
						cui.put(qid, tmp);
					}
				}
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		double avg =0.0;
		int max =0;
		String t ="";
		for(String p : cui.keySet()){
			int[] value = cui.get(p);
			if(value[1]>max){
				max = value[1];
				t = p;
			}
			avg += value[0]*1.0/value[1];
		}
		System.out.println(avg/cui.size());
		System.out.println(avg);
		
		System.out.println(max+" "+t);
	}
}
