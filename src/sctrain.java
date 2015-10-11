import static library.Helpers.println;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class sctrain {
	String word1;	// class 1
	String word2;	// class 2
	String train_file;
	String model_file;
	Features features;
	
	public sctrain(String word1, String word2, String train_file, String model_file) {
		this.word1 = word1;
		this.word2 = word2;
		this.train_file = train_file;
		this.model_file = model_file;
	}
	
	public void run() throws IOException {
		preprocess();
		println(features);
	}
	
	/**
	 * preprocess using the training files and do feature selection
	 * */
	public void preprocess() throws IOException {
		HashSet<String> surroundingWordsSet = new HashSet<String> ();
		HashSet<String> collocationsSet = new HashSet<String> ();
		
		/* process each line of training data */
		BufferedReader bf = new BufferedReader(new FileReader(train_file));
		String line;
		while ((line = bf.readLine()) != null) {
			String[] tokens = line.split(" ");
			String lineNumber = tokens[0];
			
			/* populate surroundingWordsSet */
			ArrayList<String> surroundingWords = Features.getSurroundingWords(tokens);
			for (String word: surroundingWords) surroundingWordsSet.add(word);
			
			/* populate collocationSet */
			int leftIndicatorPosition = Features.getLeftIndicatorPosition(tokens);
			collocationsSet.add(Features.getCollocation(tokens, leftIndicatorPosition));
		}
		bf.close();
		features = new Features(surroundingWordsSet, collocationsSet);
	}
	
	/* train using the training file */
	
	public static void main(String[] args) throws IOException {
		if (args.length != 4) {
			System.err.println("Usage: java sctrain <word1> <word2> <train_file> <model_file>");
			System.exit(-1);
		}
		else {
			sctrain sc = new sctrain(args[0], args[1], args[2], args[3]);
			sc.run();
		}
	}
}