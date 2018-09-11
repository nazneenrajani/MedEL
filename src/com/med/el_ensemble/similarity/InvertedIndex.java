package com.med.el_ensemble.similarity;

import java.io.*;
import java.io.ObjectInputStream.GetField;
import java.util.*;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import com.ibm.watsonmd.el_ensemble.similarity.CosineDocumentSimilarity;


/**
 * An inverted index for vector-space information retrieval. Contains
 * methods for creating an inverted index from a set of documents
 * and retrieving ranked matches to queries using standard TF/IDF
 * weighting and cosine similarity.
 *
 */

public class InvertedIndex {
	static String[] REOutputs;
	/** The maximum number of retrieved documents for a query to present to the user
	 * at a time */

	public static final int MAX_RETRIEVALS = 100000;
	public static final int nsys = 8;
	public static final String type = "supervised";
	private static final String FILENAME_FIELD = "CUI";
	private static final String FIELD_CONTENT = "description";

	/** The directory from which the indexed documents come. */
	public File dirFile = null;

	/** Whether tokens should be stemmed with Porter stemmer */
	public boolean stem = false;
	public static List<String> uniq_keys;


	/** Default Constructor for subclasses */
	public InvertedIndex() {}

	/** Create an inverted index of the documents in a directory.
	 * @param dirFile The directory of files to index.
	 * @param docType The type of documents to index (See docType in DocumentIterator) 
	 * @param stem Whether tokens should be stemmed with Porter stemmer.
	 */
	public InvertedIndex(File dirFile, boolean stem) {
		this.dirFile = dirFile;
		this.stem = stem;
		REOutputs = new String[nsys];
	}

	public static void getFiles(String path){
		System.out.println(path);
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		Arrays.sort(listOfFiles);
		int k=0;
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				REOutputs[k] = path+"/"+listOfFiles[i].getName();
				System.out.println(REOutputs[k]);
				k++;
			}
		}
	}


	public static void main(String[] args) throws IOException, ParseException {	
		//boolean stem = true;
		//String dirName= "";
		//InvertedIndex index = new InvertedIndex(new File(dirName), stem);
		//createIndex("/users/nfrajani/UMLS/files_id","/users/nfrajani/UMLS/UMLS_index");
		String type = "cv";
		boolean hasContext =false;
		searchIndex("/users/nfrajani/UMLS/UMLS_index", hasContext,type);
	}


	public static void searchIndex(String indexDir, boolean hasContext, String type) throws IOException, ParseException {
		REOutputs = new String[nsys];
		Directory directory = FSDirectory.open(new File(indexDir));
		DirectoryReader dr = DirectoryReader.open(directory);
		IndexSearcher indexSearcher = new IndexSearcher(dr);

		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_48);
		QueryParser queryParser = new QueryParser(Version.LUCENE_48, FIELD_CONTENT, analyzer);
		getFiles("/users/nfrajani/data/latestEMRA/ensemble/");
		HashMap<String, String> span_context = new HashMap<String,String>();
		HashMap<String, String> span = new HashMap<String,String>();
		if(hasContext==true){
			try {
				BufferedReader br = new BufferedReader(new FileReader("/users/nfrajani/data/latestEMRA/output/context")); //zjhufinal //ensemble/keys/mcr_"+type+"_context //i2b2/output/i2b2_context
				String line;
				while((line=br.readLine())!=null){
					String[] l = line.split("\t");
					span_context.put(l[0],l[1]);
				}
				br.close();
				System.out.println(span_context.size());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		else {
			try {
				BufferedReader br = new BufferedReader(new FileReader("/users/nfrajani/data/latestEMRA/output3/span")); //zjhufinal //ensemble/keys/mcr_"+type+"_context //i2b2/output/i2b2_span
				String line;
				while((line=br.readLine())!=null){
					String[] l = line.split("\t");
					span.put(l[0],l[1]);
				}
				br.close();
				System.out.println(span.size());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		uniq_keys = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader("/users/nfrajani/data/latestEMRA/output3/emra")); //zjhufinal //i2b2/output3/i2b2_3
			String line;
			while((line=br.readLine())!=null){
				String[] l = line.split("\t");
				uniq_keys.add(l[0]+"~"+l[1]); //2,4
				//System.out.println(l[0]+"~"+l[1]);
			}
			br.close();
			System.out.println(uniq_keys.size());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		//System.out.println("KEYS");
		Map<String, HashMap<Integer, Double>> query_result = new HashMap<String,HashMap<Integer,Double>>();
		BufferedWriter bw = new BufferedWriter(new FileWriter("/users/nfrajani/data/latestEMRA/output3/docsim")); //i2b2/output3/docsim
		for(int sys=0; sys<nsys;sys++){
			try {
				BufferedReader br = new BufferedReader(new FileReader(REOutputs[sys]));
				String line;
				while((line=br.readLine())!=null){
					String[] parts = line.split("\t");
					String key = parts[0]+"~"+parts[1];
					///System.out.println(key);
					String cui = parts[1]; //2
					String context ="";
					if(hasContext==true)
						context = span_context.get(parts[0]);
					else
						context = span.get(parts[0]);
					Query query = queryParser.parse(cui);
					TopDocs td = indexSearcher.search(query,1);
					for ( ScoreDoc scoreDoc : td.scoreDocs ) {
						Document doc = indexSearcher.doc( scoreDoc.doc);
						File f = new File( doc.get( FILENAME_FIELD ) );
						BufferedReader br1 = new BufferedReader(new FileReader("/users/nfrajani/UMLS/files_id/"+f));
						String line1;
						line1 = br1.readLine();
						double score =0.0;
						//System.out.println(line1+"~"+context);
						if(context.matches(">?<?\\+?-?/?\\d+")){
							score =0.0;
							//System.out.println(line1+"~"+context);
						}
						else
							score = new CosineDocumentSimilarity(line1.toLowerCase(), context.toLowerCase()).getCosineSimilarity();
						if(!query_result.containsKey(key)){
							HashMap<Integer, Double> tmp = new HashMap<Integer, Double>();
							tmp.put(sys, score);
							query_result.put(key,tmp);
						}
						else{
							HashMap<Integer, Double> tmp = query_result.get(key);
							tmp.put(sys, score);
							query_result.put(key,tmp);
						}
						br1.close();	
					}
				}
				br.close();
			}catch (FileNotFoundException e) { 
				e.printStackTrace();
			}
		}
		for(int i=0;i<uniq_keys.size();i++){
			double[] s = new double[nsys];
			if(query_result.containsKey(uniq_keys.get(i))){
				HashMap<Integer,Double> temp = query_result.get(uniq_keys.get(i));
				for(int k:temp.keySet()){
					s[k] = temp.get(k);
				}
			}
			for(int j =0; j<s.length;j++)
				bw.write(s[j]+",");
			bw.write("\n");
		}
		bw.close();
	}


	private static void createIndex(String dirToIndex, String indexDir) throws IOException {
		File dirName = new File(dirToIndex);
		File[] files = dirName.listFiles();
		SimpleFSDirectory fsd = new SimpleFSDirectory(new File(indexDir));
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_48);
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_48, analyzer);
		config.setOpenMode(OpenMode.CREATE_OR_APPEND);
		IndexWriter indexWriter = new IndexWriter(fsd, config);
		for (File file : files) {
			Document document = new Document();
			// String path = file.getCanonicalPath();
			//byte[] bytes = path.getBytes();
			TextField field = new TextField(FILENAME_FIELD, file.getName(),Field.Store.YES);
			document.add(field);
			Reader reader = new FileReader(file);
			TextField fr = new TextField(FIELD_CONTENT,reader);
			document.add(fr);
			indexWriter.addDocument(document);
		}
		//indexWriter.forceMergeDeletes();
		System.out.println("Total number of indexed documents: " +indexWriter.numDocs());
		indexWriter.close();

	}
}
