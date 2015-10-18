import static library.Helpers.P;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * @author Jin Zhe (A0086894H)
 */
public class Features implements Serializable{
	/**
   * Generated serialization ID
   */
  private static final long serialVersionUID = 440984463827695422L;
  /* offset constants for collocation */
	public static final int C_i = 1;
	public static final int C_j = 2;
	/* stopwords  */
	public static final String STOPWORD_LIST = "./resources/stopwd.txt";
	public static HashSet<String> stopwords;
	/* instance variables */
	public boolean ignoreStopwords;             // if stopwords are to be ignore
	public int dfThreshold;                     // minimum df to qualify as a feature
	public ArrayList<String> surroundingWords;	// features list for surrounding words
	public ArrayList<String> collocations;			// features list for collocation sequences
	int vectorLength;									// total length of feature vector
	
	/* ########################### instance methods ########################### */
	
	/**
	 * Constructor
   * @param surroundingWordsMap key: word, value: df
   * @param collocationsMap     key: word, value: df
   * @param ignoreStopwords     toggle to indicate stopwording
   * @param dfThreshold         df filter
	 */
	public Features(HashMap<String, Integer> surroundingWordsMap,
									HashMap<String, Integer> collocationsMap,
									boolean ignoreStopwords, int dfThreshold) {
		this.ignoreStopwords = ignoreStopwords;
		this.dfThreshold = dfThreshold;

    /* populate surroundingWords ands collocations features list */
    this.surroundingWords = featureSelection(surroundingWordsMap);
    this.collocations = featureSelection(collocationsMap);
		
		vectorLength = surroundingWords.size() + collocations.size();
	}
	
	/**
	 * gets the feature list from the given map by applying stopwording and
	 * df threshold filters
	 * @param map  key: word, value: df
	 * @return     feature list produced from the map
	 */
	private ArrayList<String> featureSelection(HashMap<String, Integer> map) {
	  ArrayList<String> featureList = new ArrayList<String> (map.size());
	   for (Map.Entry<String, Integer> entry : map.entrySet()) {
	      String word = entry.getKey();
	      int df = entry.getValue();
	      if (ignoreStopwords && isStopWord(word)) continue;
	      if (df < dfThreshold) continue;
	      featureList.add(word);
	    }
	   return featureList;
	}
	
	/**
	 * returns the feature vector given the features
	 * @param surroundingWordsMap	surrounding words feature
	 * @param collocationsMap			collocation feature
	 * @return                    feature vector
	 */
	public Integer[] getFeatureVector(HashMap<String, Integer> surroundingWordsMap,
																    String collocation) {
	  Integer[] featureVector = new Integer[vectorLength];
		/* populate feature vector from surrounding words */
		for (int i=0; i<surroundingWords.size(); i++) {
			String word = surroundingWords.get(i);
			if (surroundingWordsMap.containsKey(word))
				featureVector[i] = surroundingWordsMap.get(word);
			else featureVector[i] = 0;
		}
		
		/* populate feature vector from collocations */
		int offset = surroundingWords.size();
		for (int i=0; i<collocations.size(); i++) {
			String feature = collocations.get(i);
			if (collocation.equals(feature)) featureVector[offset + i] = 1;
			else featureVector[offset + i] = 0;
		}
		return featureVector;
	}
	
	public String toString() {
		return  vectorLength + " features\nSurrounding words:\n" +
		        surroundingWords + "\n" + "Collocations:\n" + collocations;
	}
	
	/* ############################ class methods  ############################ */
	
	public static String getClassification(Integer[] featureVector, Double[] weightVector,
	                                       String word1, String word2) {
	  double prob1 = P(1, featureVector, weightVector);
	  return (prob1 > 0.5)? word1: word2;
	}
	
	private static void loadStopwords() {
		stopwords = new HashSet<String>();
		try {
      BufferedReader bf = new BufferedReader(new FileReader(STOPWORD_LIST));
      String line;
      while ((line = bf.readLine()) != null) stopwords.add(line);
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
	public static HashSet<String> getSurroundingWordsSet(String[] tokens) throws IOException {
		HashSet<String> surroundingWordsSet = new HashSet<String> ();
		for (int i=1; i<tokens.length; i++) {
			String token = tokens[i];
			/* if ">>", we skip to the token after "<<" */
			if (isIndicator(token)) {
				if (isIndicator(tokens[i+1])) i += 2;				// if testing line tokens
				else if (isIndicator(tokens[i+2])) i += 3;	// if training line tokens
			}
			if (isPunctuation(token)) continue;
			else surroundingWordsSet.add(token.toLowerCase());
		}
		return surroundingWordsSet;
	}
	
	/**
	 * given tokens list and origin position, get the collocation string defined
	 * by C_i and C_j
	 * @param tokens	 tokens list
	 * @param lip      left indicator position
	 * @param isTrain  if tokens is a training sentence (otherwise test)
	 * @return         collocation sequence
	 */
	@SuppressWarnings("unused")
  public static String getCollocation(String[] tokens, int lip, boolean isTrain) {
		StringBuffer sb = new StringBuffer();
		int i; 
		int j;
		/* if collocation on LHS */
		if (C_j < 0) {
	    i = lip + C_i;
	    j = lip + C_j;
		}
		/* else collocation on RHS */
		else {
		  int rip;
		  if (isTrain) rip = lip + 2;
		  else rip = lip + 1;
      i = rip + C_i;
      j = rip + C_j;		  
		}
		for (int k=i; k<=j; k++) {
			if (k <= 0 || k>=tokens.length) sb.append("#");  // if out of sentence bounds
			else sb.append(tokens[k].toLowerCase());         // else add token to collocation
			if (k != j) sb.append(" ");						           // append space delimiter if not last word
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