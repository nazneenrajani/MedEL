
public class WordIdf {
	public WordIdf(int word_id,double word_idf){
		this.word_id= word_id;
		this.word_idf= word_idf;
	}

	public int getWord_id() {
		return word_id;
	}
	public void setWord_id(int word_id) {
		this.word_id = word_id;
	}
	public double getWord_idf() {
		return word_idf;
	}
	public void setWord_idf(double word_idf) {
		this.word_idf = word_idf;
	}

	public int word_id;
	public double word_idf;
	
}
