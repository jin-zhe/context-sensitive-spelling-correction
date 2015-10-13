import static library.Helpers.println;

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
    println(model);
  }
  
  public static void main(String[] args) {
    if (args.length != 5) {
      System.err.println("Usage: java sctest <word1> <word2> <test_file> <model_file> <answer_file>");
      System.exit(-1);
    }
    else {
      sctest test = new sctest(args[0], args[1], args[2], args[3], args[4]);
      test.run();
    }
  }

}
