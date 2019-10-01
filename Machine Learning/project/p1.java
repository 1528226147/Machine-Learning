import java.io.BufferedWriter;
import java.io.FileWriter;

public class p1 {

  /*
   * A main method that takes the name of training and testing examples
   * from the command line, reads them in, and prints them to the
   * console.
   */

    public static void main( String args[] ) {
        try {
            TrainTestSets tts = new TrainTestSets();
            tts.setOptions( args );
            BufferedWriter writer = new BufferedWriter(new FileWriter("../dataset/nursery-binary-class.mff"));
            writer.write(tts.toString());
            writer.close();
        } // try
        catch ( Exception e ) {
            System.out.println( e.getMessage() );
            e.printStackTrace();
        } // catch
    } // p1::main

} // p1 class
