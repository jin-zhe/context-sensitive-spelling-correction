package library;
/**
 * @author Jin Zhe (A0086894H)
 */
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

public class Helpers {
  
  /**
   * updates a count hashmap with the given word 
   * @param map   hashmap to be updated
   * @param word  word to be added
   */
  public static void updateHashMap(HashMap<String, Integer> map, String word) {
    if (!map.containsKey(word))
      map.put(word, 1);
    else {
      map.put(word, 1 + map.get(word));
    }
  }
  
  /**
   * returns the probability of class y given featureVector and weightVector
   * @param klass             class to be evaluated
   * @param featureVector     feature vector
   * @param weightVector      weight vector
   * @return                  probability that featureVector corresponds to the given class
   */
  public static double P(int y, Integer[] featureVector, Double[] weightVector) {
    long exponent = -dot(featureVector, weightVector);
    double term = Math.pow(Math.E, exponent);
    double denominator = 1 + term;
    /* if positive class (class 1) */
    if (y == 1) return 1 / denominator;
    /* else if negative class (class 2) */
    else return term / denominator;
  }
	
  /**
   * determines the integer class representation of the given word
   * @param word  word class to be determined
   * @param word1 positive class
   * @param word2 negative class
   * @return      1 if word is the positive class, 0 otherwise
   */
  public static int y(String word, String word1, String word2) {
    return (word.equals(word1))? 1: 0;
  }
  
	/**
	 * returns the dot product between feature and weight vector
	 * @param featureVector	
	 * @param weightVector
	 * @return
	 */
	public static long dot(Integer[] featureVector, Double[] weightVector) {
		long dotProduct = 0;
		for (int i=0; i<featureVector.length; i++) {
			dotProduct += featureVector[i] * weightVector[i];
		}
		return dotProduct;
	}
	
	 /**
   * writes lines to given output path to disk
   * @param lines   array of lines
   * @param outPath absolute output path
   */
  public static void writeFile(String[] lines, String outPath) {
    try {
      PrintWriter pw = new PrintWriter(outPath);
      for (String line: lines) pw.write(line);
      pw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
	
	/**
	 * same as Arrays.copyOfRange in 1.6
	 * @param srcArr
	 * @param start
	 * @param end
	 * @return
	 */
	public String[] copyOfRange(String[] srcArr, int start, int end){
		int length = (end > srcArr.length)? srcArr.length-start: end-start;
		String[] dstArr = new String[length];
		System.arraycopy(srcArr, start, dstArr, 0, length);
		return dstArr;
	}
	
	public static void println(Object obj) {
		System.out.println(obj.toString());
	}
}