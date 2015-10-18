import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
/**
 * @author Jin Zhe (A0086894H)
 */
public class Model implements Serializable{
  /**
   * Generated serialization ID
   */
  private static final long serialVersionUID = -4880396229714002717L;
  Features features;
  Double[] weightVector;
  public Model(Features features, Double[] weightVector) {
    this.features = features;
    this.weightVector = weightVector;
  }
  
  public String toString() {
    return features.toString() + "\n" + "w :" + Arrays.toString(weightVector);
  }
  
  /* ############################ class methods  ############################ */
  
  public static void saveModel(Model model, String path) {
    try {
      FileOutputStream fos = new FileOutputStream(path);
      ObjectOutputStream oos = new ObjectOutputStream(fos);
      oos.writeObject(model);
      oos.close();
      fos.close();
    } catch (IOException e){
        e.printStackTrace();
    }
  }
  
  public static Model loadModel(String path) {
    Model model;
    try{
      FileInputStream fis = new FileInputStream(path);
      ObjectInputStream ois = new ObjectInputStream(fis);
      model = (Model) ois.readObject(); // suppress warning
      ois.close();
      return model;
    }
    catch(Exception ex) {
      ex.printStackTrace();
      return null;
    } 
  }
}