import static library.Helpers.println;
import static library.Helpers.updateHashMap;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
/**
 * @author Jin Zhe (A0086894H)
 */
public class sctest {
  String word1;
  String word2;
  String test_file;
  String answer_file;
  Model model;
    
  public sctest(String word1, String word2, String test_file, String model_file,
                String answer_file) {
    this.word1 = word1;
    this.word2 = word2;
    this.test_file = test_file;
    this.answer_file = answer_file;
    this.model = Model.loadModel(model_file);
  }
  
  public void run() {
//    println(model); //   check if model is loaded correctly
    evaluateAnswers();
    println("Output written to " + answer_file);
  }
  
  public void evaluateAnswers() {
    Features features = model.features;
    Double[] w = model.weightVector;
    try {
      PrintWriter af = new PrintWriter(answer_file);
      
      /* process each line of test data */
      BufferedReader bf = new BufferedReader(new FileReader(test_file));
      String line;
      while ((line = bf.readLine()) != null) {
        String[] tokens = line.split("\\s");
        String id = tokens[0]; // test instance id
        
        /* populate surroundingWordsMap */
        HashSet<String> surroundingWordsSet = Features.getSurroundingWordsSet(tokens);
        HashMap<String, Integer> surroundingWordsMap = new HashMap<String, Integer> (); // Key: term, Value: count
        for (String word: surroundingWordsSet)
          updateHashMap(surroundingWordsMap, word);
        
        /* get collocation */
        int lip = Features.getLeftIndicatorPosition(tokens);
        String collocation = Features.getCollocation(tokens, lip, false);

        /* get feature vector */
        Integer[] x = features.getFeatureVector(surroundingWordsMap, collocation);
        
        /* get classification and write to output */
        String classification = Features.getClassification(x, w, word1, word2);
        af.write(id + "\t" + classification + "\n");
      }
      
      bf.close();
      af.close();
    } catch (IOException e) {
      e.printStackTrace();
    }    
  }
  
  public static void main(String[] args) {
    if (args.length != 5) {
      System.err.println("Usage: java sctest <word1> <word2> <test_file> <model_file> <answer_file>");
      System.exit(-1);
    }
    else {
      long startTime = System.currentTimeMillis();
      sctest test = new sctest(args[0], args[1], args[2], args[3], args[4]);
      test.run();
      long endTime   = System.currentTimeMillis();
      println("Execution time: " + (endTime - startTime) + "ms");
    }
  }

}