package library;

public class Helpers {
	
	/**
	 * returns the dot product between vector x and vector y
	 * @param x	
	 * @param y
	 * @return
	 */
	public static long dot(int[] x, int[] y) {
		long dotProduct = 0;
		for (int i=0; i<x.length; i++) {
			dotProduct += x[i] * y[i];
		}
		return dotProduct;
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