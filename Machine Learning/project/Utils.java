import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.stream.DoubleStream;

public class Utils {

  public static int maxIndex( double[] p ) {
	double max = 0;
  	int maxIndex = 0;

  	for (int i = 0; i < p.length; i++) {
  		if (p[i] > max) {
  			maxIndex = i;
  			max = p[i];
  		}
  	}

  	return maxIndex;
  }

  // https://alvinalexander.com/java/java-deep-clone-example-source-code
  public static Object deepClone(Object obj) {
	  try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);

			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);

			return ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
  }

  // give a double array, normalize its values so they will add up to 1
  public static double[] normalization(double[] distribution)
  {
	  double sum = DoubleStream.of(distribution).sum();

	  for (int i = 0; i < distribution.length; i++)
	  {
		  distribution[i] /= sum;
	  }

	  return distribution;
  }

  public static double u25( int n, int x ) {
	  double z2 = 0.6925 * 0.6925;
	  return (x + 0.5 + z2 / 2 + 0.6925 * Math.sqrt((x + 0.5) * (1 - (x + 0.5) / n) + z2 / 4)) 
			  / (n + z2);
  }
  
  public static double[] unitLength (double[] array) {
	  double sum = 0.0;
	  
	  for (int i = 0; i < array.length; i++) {
		  sum += array[i] * array[i];
	  }
	  
	  sum = Math.sqrt(sum);
	  
	  for (int i = 0; i < array.length; i++) {
		  array[i] /= sum;
	  }
	  
	  return array;
  }
  
  public static double dot(double[] x, double[] y) {
	  double dot = 0.0;
	  
	  for (int i = 0; i < x.length; i++) {
		  dot += x[i] * y[i];
	  }
	  
	  return dot;
  }
  
  public static double dot(double[] x, ArrayList<Double> y) {
	  double dot = 0.0;
	  
	  for (int i = 0; i < x.length; i++) {
		  dot += x[i] * y.get(i);
	  }
	  
	  return dot;
  }
  
  public static double dot(ArrayList<Double> x, ArrayList<Double> y, int z) {
	  double dot = 0.0;
	  
	  for (int i = 0; i < z; i++) {
		  dot += x.get(i) * y.get(i);
	  }
	  
	  return dot;
  }
  
  public static double sigmoid(double x) {
	  return 1.0 / (1.0 + Math.exp(-x));
  }
  
  public static double[][] randomArray(int x, int y) {
	  double[][] array = new double[x][y];
	  
	  for (int i = 0; i < x; i++) {
		  for (int j = 0; j < y; j++) {
			  array[i][j] = Math.random() - 0.5;
		  }
	  }
	  
	  return array;
  }
  
  public static double[] hardmax(double[] x) {
	  int index = maxIndex(x);
	  double[] dist = new double[x.length];
	  
	  dist[index] = 1;
	  
	  return dist;
  }

}
