package library;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class Helpers {
  
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
   * Writes data to given output path to disk
   * @param data    byte array for data
   * @param outPath absolute output path
   */
  public static void writeFile(byte[] data, String outPath) {
    try {
      FileOutputStream fos = new FileOutputStream(outPath); 
      fos.write(data);
      fos.close();
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