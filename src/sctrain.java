import static library.Helpers.P;
import static library.Helpers.println;
import static library.Helpers.y;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class sctrain {
	String word1;	// class 1 (1)
	String word2;	// class 2 (0)
	String train_file; 
	String model_file;
	Features features;
	static final Random rand = new Random();
	
	public sctrain(String word1, String word2, String train_file, String model_file) {
	  this.word1 = word1;
		this.word2 = word2;
		this.train_file = train_file;
		this.model_file = model_file;
	}
	
	public void run() {
		preprocess();
		println("######## feature selection completed!");
		println(features);
		ArrayList<Pair<Integer[], Integer>> trainingInstances = buildTrainingInstances();
		println("######## " + trainingInstances.size() + " training instances created!");
//		println(trainingInstances.get(1).two); // check
		Double[] weightVector = stochasticGradientAscent(trainingInstances, 10, 0.01);
		println("w:" + Arrays.toString(weightVector));
		Model model = new Model (features, weightVector);
		Model.saveModel(model, model_file);
	}
	
	/**
	 * preprocess using the training files and do feature selection
   * @throws IOException
	 */
	public void preprocess() {
	  HashSet<String> surroundingWordsSet = new HashSet<String> ();
		HashSet<String> collocationsSet = new HashSet<String> ();
		try {
  		/* process each line of training data */
  		BufferedReader bf = new BufferedReader(new FileReader(train_file));
  		String line;
  		while ((line = bf.readLine()) != null) {
  			String[] tokens = line.split("\\s");
  			
  			/* populate surroundingWordsSet */
  			ArrayList<String> surroundingWords = Features.getSurroundingWords(tokens);
  			for (String word: surroundingWords) surroundingWordsSet.add(word);
  			
  			/* populate collocationSet */
  			int leftIndicatorPosition = Features.getLeftIndicatorPosition(tokens);
  			collocationsSet.add(Features.getCollocation(tokens, leftIndicatorPosition));
  		}
      bf.close();
		} catch (IOException e) {
      e.printStackTrace();
    }
		features = new Features(surroundingWordsSet, collocationsSet);
	}
	
	/**
	 * build a list of training instances
	 * @throws IOException
	 */
	public ArrayList<Pair<Integer[], Integer>> buildTrainingInstances() {
	  ArrayList<Pair<Integer[], Integer>> trainingInstances = new ArrayList<Pair<Integer[], Integer>>();
	  try {
  	  /* process each line of training data */
      BufferedReader bf = new BufferedReader(new FileReader(train_file));
      String line;
      while ((line = bf.readLine()) != null) {
        String[] tokens = line.split("\\s");
        
        /* populate surroundingWordsMap */
        ArrayList<String> surroundingWords = Features.getSurroundingWords(tokens);
        HashMap<String, Integer> surroundingWordsMap = new HashMap<String, Integer> (); // Key: term, Value: count
        for (String word: surroundingWords) {
          if (!surroundingWordsMap.containsKey(word))
            surroundingWordsMap.put(word, 1);
          else
            surroundingWordsMap.put(word, surroundingWordsMap.get(word) + 1);
        }
        
        /* get collocation */
        int leftIndicatorPosition = Features.getLeftIndicatorPosition(tokens);
        String collocation = Features.getCollocation(tokens, leftIndicatorPosition);
        
        /* get class */
        String confuseWord = tokens[leftIndicatorPosition + 1];
        int y = y(confuseWord, word1, word2);
        
        /* get feature vector */
        Integer[] featureVector = features.getFeatureVector(surroundingWordsMap, collocation);
        
        /* add new training instance to trainingInstances */
        trainingInstances.add(new Pair<Integer[], Integer>(featureVector, y));
      }
      bf.close();
	  } catch (IOException e) {
      e.printStackTrace();
    }
    return trainingInstances;
	}
	
	/**
   * run logistic regression and derive the weights vector
   * @return
   */
  public Double[] stochasticGradientAscent(ArrayList<Pair<Integer[], Integer>> trainingInstances,
                                           int stepSize, double threshold) {
    Double[] weightVector = new Double[features.vectorLength];
    
    /* initialize weightVector to all 0's */
    for (int i=0; i<weightVector.length; i++) weightVector[i] = 0.0;
    
    /* conduct gradient ascent */
    double ascent = 0.0;
    do {
      ascent = 0.0;
      Double[] newWeightVector = new Double[weightVector.length];

      /* for every value in weight vector */
      for (int i=0; i<weightVector.length; i++) {
        int q = trainingInstances.size();
        double accumulator = 0.0;
        /* for a random training instance */
        int j = rand.nextInt(q);
        Pair<Integer[], Integer> instance = trainingInstances.get(j);
        Integer[] x_j = instance.one;
        Integer y_j = instance.two;
        accumulator += x_j[i] * (y_j - P(1, x_j, weightVector));
        ascent += accumulator * accumulator; // sum of squares
        newWeightVector[i] = weightVector[i] + stepSize * accumulator;
      }
      ascent = Math.sqrt(ascent);      // take euclidean distance
      weightVector = newWeightVector;  // update weight vector
      println("Ascent: " + ascent);    // check ascent
    } while (ascent > threshold);

    return weightVector;
  }
	
	/**
	 * run logistic regression and derive the weights vector
	 * @return
	 */
	public Double[] batchGradientAscent(ArrayList<Pair<Integer[], Integer>> trainingInstances,
	                                    int stepSize, double threshold) {
	  Double[] weightVector = new Double[features.vectorLength];
	  
	  /* initialize weightVector to all 0's */
	  for (int i=0; i<weightVector.length; i++) weightVector[i] = 0.0;
	  
	  /* conduct gradient ascent */
	  double ascent = 0.0;
	  do {
	    ascent = 0.0;
	    Double[] newWeightVector = new Double[weightVector.length];

	    /* for every value in weight vector */
	    for (int i=0; i<weightVector.length; i++) {
	      int q = trainingInstances.size();
	      double accumulator = 0.0;
	      /* for every training instance */
	      for (int j=0; j<q; j++) {
	        Pair<Integer[], Integer> instance = trainingInstances.get(j);
	        Integer[] x_j = instance.one;
	        Integer y_j = instance.two;
	        accumulator += x_j[i] * (y_j - P(1, x_j, weightVector));
	        ascent += (accumulator/q) * (accumulator/q); // sum of squares
	      }
	      newWeightVector[i] = weightVector[i] + stepSize * accumulator / q;
	    }
	    ascent = Math.sqrt(ascent);      // take euclidean distance
	    weightVector = newWeightVector;  // update weight vector
	    println("Ascent: " + ascent);    // check ascent
	  } while (ascent > threshold);

	  return weightVector;
	}
	
	public static void main(String[] args) throws IOException {
		if (args.length != 4) {
			System.err.println("Usage: java sctrain <word1> <word2> <train_file> <model_file>");
			System.exit(-1);
		}
		else {
			sctrain train = new sctrain(args[0], args[1], args[2], args[3]);
			train.run();
		}
	}
}