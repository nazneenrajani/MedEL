package com.med.el_ensemble.similarity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Word2VecSimilarity {

	public static void main(String[] args) throws IOException {
		File file=new File("/users11/nfrajani/word2vec/200features_1minwords_10context.syn0.npy");
		// Find the size
		int size=(int) file.length();
		// Create a buffer big enough to hold the file
		byte[] contents=new byte[size];
		// Create an input stream from the file object
		FileInputStream in=new FileInputStream(file);
		// Read it all
		in.read(contents);
		in.close();
		String line = new String(contents);
		System.out.println(line);
	}

}
