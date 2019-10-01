import java.io.Serializable;
import java.util.ArrayList;

public class Scaler extends Object implements Serializable {

  private Attributes attributes;
  private ArrayList<Double> mins;
  private ArrayList<Double> maxs;

  public Scaler() {
	  super();
	  
	  this.mins = new ArrayList<Double>();
	  this.maxs = new ArrayList<Double>();
  }
  
  public void configure( DataSet ds ) throws Exception {
	  attributes = ds.getAttributes();
	  
	  if (attributes.getHasNumericAttributes()) {
		  Attribute attr;
		  Examples examples = ds.getExamples();
		  double min;
		  double max;
		  
		  for (int i = 0; i < attributes.size(); i++) {
			  attr = attributes.get(i);
			  
			  if (attr.size() == -1) {
				  max = Double.NEGATIVE_INFINITY;
				  min = Double.POSITIVE_INFINITY;
				  
				  for (Example e : examples) {
					  min = Double.min(min, e.get(i));
					  max = Double.max(max, e.get(i));
				  }
				  
				  mins.add(min);
				  maxs.add(max);
			  }
		  }
	  }	  
  }
  public DataSet scale( DataSet ds ) throws Exception {
	  if (ds.getHasNumericAttributes()) {
		  Examples examples = ds.getExamples();
		  
		  for (Example e : examples) {
			  e = this.scale(e);
		  }
	  }
	  
	  return ds;
  }
  public Example scale( Example example ) throws Exception {
	  if (attributes.getHasNumericAttributes()) {
		  Attribute attr;
		  int index = 0;
		  double normalizedValue;
		  
		  for (int i = 0; i < attributes.size(); i++) {
			  attr = attributes.get(i);
			  
			  if (attr.size() == -1) {	
				  if (index >= maxs.size()) {
					  throw new Exception("The number of NumericAttribute doesn't match!");
				  }
				  
				  if (example.get(i) > maxs.get(index)) {
					  normalizedValue = 1.0;
				  }
				  else if (example.get(i) < mins.get(index)) {
					  normalizedValue = 0.0;
				  }
				  else {
					  normalizedValue = (example.get(i) - mins.get(index)) / (maxs.get(index) - mins.get(index));
				  }
				  
				  example.set(i, normalizedValue);
				  index++;
			  }
		  }
	  }
	  
	  return example;
  }
  
}
