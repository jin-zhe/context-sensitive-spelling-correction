import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Features implements Serializable{
	/**
   * Generated serialization ID
   */
  private static final long serialVersionUID = 440984463827695422L;
  /* offset constants for collocation */
	public static final int C_i = -2;
	public static final int C_j = -1;
	/* stopwords  */
	public static final String STOPWORD_LIST = "./resources/stopwords.txt";
	public static HashSet<String> stopwords;
	/* instance variables */
	public String[] surroundingWords;	// features list for surrounding words
	public String[] collocations;			// features list for collocation sequences
	int vectorLength;									// total length of feature vector
	
	/* ########################### instance methods ########################### */
	
	/**
	 * Constructor
	 * @param surroundingWordsSet
	 * @param collocationsSet
	 */
	public Features(HashSet<String> surroundingWordsSet,
									HashSet<String> collocationsSet) {
		surroundingWords = new String[surroundingWordsSet.size()];
		collocations = new String[collocationsSet.size()];
		
		/* populate surroundingWords features list */
		int i = 0;
		for (String str: surroundingWordsSet) {
			surroundingWords[i] = str;
			i++;
		}
		
		/* populate collocations features list */
		i = 0;
		for (String str: collocationsSet) {
			collocations[i] = str;
			i++;
		}
		
		vectorLength = surroundingWords.length + collocations.length;
	}
	
	/**
	 * returns the feature vector given the features
	 * @param surroundingWordsMap	surrounding words feature
	 * @param collocationsMap			collocation feature
	 * @return
	 */
	public Integer[] getFeatureVector(HashMap<String, Integer> surroundingWordsMap,
																    String collocation) {
	  Integer[] featureVector = new Integer[vectorLength];
		/* populate feature vector from surrounding words */
		for (int i=0; i<surroundingWords.length; i++) {
			String word = surroundingWords[i];
			if (surroundingWordsMap.containsKey(word))
				featureVector[i] = surroundingWordsMap.get(word);
			else featureVector[i] = 0;
		}
		
		/* populate feature vector from collocations */
		int offset = surroundingWords.length;
		for (int i=0; i<collocations.length; i++) {
			String feature = collocations[i];
			if (collocation.equals(feature)) featureVector[offset + i] = 1;
			else featureVector[offset + i] = 0;
		}
		return featureVector;
	}
	
	public String toString() {
		return  vectorLength + " features\nSurrounding words:\n" +
		        Arrays.toString(surroundingWords) + "\n" + "Collocations:\n" +
		        Arrays.toString(collocations);
	}
	
	/* ############################ class methods  ############################ */
	
	private static void loadStopwords() {
		stopwords = new HashSet<String>();
		try {
      BufferedReader bf = new BufferedReader(new FileReader(STOPWORD_LIST));
      String line;
      while ((line = bf.readLine()) != null) {
        stopwords.add(line.split("'")[0]);
      }
      bf.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
	}
	
	/**
	 * tests if a given word token is a stopword
	 * @param word	word token to be tested
	 * @return			true if given word is a stopword, else false
	 * @throws IOException
	 */
	public static boolean isStopWord(String word) {
		if (stopwords == null) loadStopwords();
		return stopwords.contains(word);
	}
	
	/**
	 * tests if a given token is a punctuation token
	 * @param token	token to be tested
	 * @return			true if token is a punctuation token, else false
	 */
	public static boolean isPunctuation(String token) {
		Pattern pattern = Pattern.compile("[\\p{Punct}]");
		Matcher m = pattern.matcher(token);
		return m.find();
	}
	
	/**
	 * get the index of the left indicator for the confusable word
	 * @param tokens	list of tokens
	 * @return				the index of left indicator
	 */
	public static int getLeftIndicatorPosition(String[] tokens) {
		for (int i=0; i<tokens.length; i++) {
			if (tokens[i].equals(">>")) return i;
		}
		return -1;
	}
	
	/**
	 * given the tokens list, return the list of terms surrounding the confusable
	 * word
	 * @param tokens
	 * @return	list of tokens
	 */
	public static ArrayList<String> getSurroundingWords(String[] tokens) throws IOException {
		ArrayList<String> surroundingWords = new ArrayList<String> ();
		for (int i=1; i<tokens.length; i++) {
			String token = tokens[i];
			/* if ">>", we skip to the token after "<<" */
			if (isIndicator(token)) {
				if (isIndicator(tokens[i+1])) i += 2;				// if testing line tokens
				else if (isIndicator(tokens[i+2])) i += 3;	// if training line tokens
			}
			/* only add non-stop words and non-punctuations to surrounding words */
			else if (!isStopWord(token) && !isPunctuation(token))
				surroundingWords.add(token.toLowerCase());
		}
		return surroundingWords;
	}
	
	/**
	 * given tokens list and origin position, get the collocation string defined
	 * by C_i and C_j
	 * @param tokens	tokens list
	 * @param pos
	 * @return
	 */
	public static String getCollocation(String[] tokens, int pos) {
		StringBuffer sb = new StringBuffer();
		int i = pos + C_i;
		int j = pos + C_j;
		for (int k=i; k<=j; k++) {
			if (k <= 0) sb.append("#");								// if out of sentence bounds
			else sb.append(tokens[k].toLowerCase());	// else add token to collocation
			if (k != j) sb.append(" ");								// append space delimiter if not last word
		}
		return sb.toString();
	}
	
	/**
	 * checks if a given token is an indicator symbol
	 * @param token	token to be checked
	 * @return			true if token is an indicator symbol false otherwise
	 */
	public static boolean isIndicator(String token) {
		return token.equals(">>") || token.equals("<<");
	}
}