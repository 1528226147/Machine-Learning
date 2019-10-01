import java.io.Serializable;
import java.util.ArrayList;

public class NaiveBayes extends Classifier implements Serializable, OptionHandler {
	
  protected Attributes attributes;
  protected CategoricalEstimator classDistribution;
  protected ArrayList< ArrayList<Estimator> > classConditionalDistributions;

  public NaiveBayes() {
	  super();
	  
	  classConditionalDistributions = new ArrayList<ArrayList<Estimator>>();
  }
  
  public NaiveBayes( String[] options ) throws Exception {
	  super(options);
	  
	  this.setOptions(options);
	  classConditionalDistributions = new ArrayList<ArrayList<Estimator>>();
  }
  
  public Performance classify( DataSet dataSet ) throws Exception {
	  Examples examples = dataSet.getExamples();
	  Performance performance = new Performance(this.attributes);

	  for (Example e : examples) {
		  performance.add(
				  (e.get(dataSet.getAttributes().getClassIndex())).intValue(), 
				  this.getDistribution(e));
	  }

	  return performance;
  }
  
  public int classify( Example example ) throws Exception {
	  return Utils.maxIndex(this.getDistribution(example));
  }
  
  public Classifier clone() {
	  return (NaiveBayes) Utils.deepClone(this);
  }
  
  public double[] getDistribution( Example example ) throws Exception {
	  double[] distribution = new double[attributes.getClassAttribute().size()];
	  int classIndex = attributes.getClassIndex();
	  ArrayList<Estimator> arrayList;
	  double a = Double.NEGATIVE_INFINITY; // a is used for log-sum-exp trick
	  
	  // calculate the distribution using log-sum-exp trick
	  // assume log(0) = -20
	  for (int i = 0; i < distribution.length; i++) {
		  // assign distribution to prior probablity for each class
		  distribution[i] = Math.max(-20, Math.log(classDistribution.getProbability(i)));
		  arrayList = classConditionalDistributions.get(i);
		  
		  for (int j = 0; j < example.size(); j++) {
			  // if the index is class index then continue
			  if (j == classIndex) {
				  continue;
			  }
			  distribution[i] += Math.max(-20, Math.log(arrayList.get(j).getProbability(example.get(j))));
		  }

		  a = Math.max(a, distribution[i]);
	  }
	  
	  for (int i = 0; i < distribution.length; i++) {
		  distribution[i] = Math.exp(Math.max(-20, distribution[i] - a));
	  }
	  
	  return Utils.normalization(distribution);
  }
  
  public void setOptions( String[] options ) throws Exception {
  	  super.setOptions(options);
  }
  
  public void train( DataSet dataset ) throws Exception {
	  attributes = dataset.getAttributes();
	  
	  int classNum = attributes.getClassAttribute().size();
	  int classIndex = attributes.getClassIndex();
	  ArrayList<Estimator> estimators;
	  Estimator estimator;
	  Attribute attr;
	  
	  // prior probability distribution
	  classDistribution = new CategoricalEstimator(classNum);
	  
	  // for each class create an array list of estimators
	  for (int i = 0; i < classNum; i++) {
		  estimators = new ArrayList<Estimator>();
		  
		  // for each attribute create an estimator
		  for (int j = 0; j < attributes.size(); j++) {
			  attr = attributes.get(j);

			  if (attr.size() == -1) {
				  estimator = new GaussianEstimator();
			  }
			  else {
				 estimator = new CategoricalEstimator(attr.size()); 
			  }
			  
			  estimators.add(estimator);
		  }
		  
		  classConditionalDistributions.add(estimators);
	  }
	  
	  // process all the examples
	  for (Example e : dataset.getExamples()) {
		  classDistribution.add(e.get(classIndex));
		  
		  estimators = classConditionalDistributions.get(e.get(classIndex).intValue());

		  for (int i = 0; i < e.size(); i++) {
			  // the class label estimator will not be trained
			  if (i == classIndex) {
				  continue;
			  }
			  
			  estimators.get(i).add(e.get(i));
		  }
	  }
  }
  
  public static void main( String args[] ) {
      try {
          Evaluator evaluator = new Evaluator(new NaiveBayes(), args);
          Performance performance = evaluator.evaluate();
          System.out.println( performance );
      } // try
      catch ( Exception e ) {
          System.out.println( e.getMessage() );
          e.printStackTrace();
      } // catch
  } // NaiveBayes::main

}
