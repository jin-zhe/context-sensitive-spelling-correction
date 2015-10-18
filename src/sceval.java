import static library.Helpers.println;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
/**
 * @author Jin Zhe (A0086894H)
 */
public class sceval {
  String answer_file;
  String test_file;
  
  public sceval(String answer_file, String test_file) {
    this.answer_file = answer_file;
    this.test_file = test_file;
  }
  
  public void evaluate() {
    try {
      BufferedReader bf_ans = new BufferedReader(new FileReader(answer_file));
      BufferedReader bf_test = new BufferedReader(new FileReader(test_file));
      String line_ans;  // line from answer file
      String line_test; // line from test file
      int length = 0;   // length of both files
      int match = 0;    // number of matching lines
      /* iterate each line in files */
      while ((line_ans = bf_ans.readLine()) != null &&
             (line_test = bf_test.readLine()) != null) {
        if (line_ans.equals(line_test)) match++;
        length++;
      }
      bf_ans.close();
      bf_test.close();
      println("Accuracy: " + (float) match/length);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public static void main(String[] args) {
    if (args.length != 2) {
      System.err.println("Usage: java sceval <answer_file> <test_file>");
      System.exit(-1);
    }
    else {
      sceval eval = new sceval(args[0], args[1]);
      eval.evaluate();
    }
  }
}