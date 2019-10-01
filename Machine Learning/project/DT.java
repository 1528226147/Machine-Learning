import java.io.Serializable;
import java.util.ArrayList;

public class DT extends Classifier implements Serializable, OptionHandler {

  protected Attributes attributes;
  protected Node root;
  
  private boolean postPrune = true;

  public DT() {
	  super();
  }
  
  public DT( String[] options ) throws Exception {
	  super(options);
	  
	  this.setOptions(options);
  }
  
  public Performance classify( DataSet dataset ) throws Exception {
  	Examples examples = dataset.getExamples();
  	Performance performance = new Performance(dataset.getAttributes());
  	
  	for (Example e : examples) {
  		performance.add(
  				(e.get(dataset.getAttributes().getClassIndex())).intValue(), 
  				this.getDistribution(e));
  	}
  	
  	return performance;
  }

  @Override
  public Classifier clone() {
	  return (DT) Utils.deepClone(this);
  }
  
  public int classify( Example example ) throws Exception {
	  return Utils.maxIndex(this.getDistribution(example));
  }
  
  public double[] getDistribution( Example example ) throws Exception {
	  return this.getDistribution(root, example);
  }
  
  public void prune() throws Exception {
	  this.prune(root);
  }
  
  public void setOptions( String[] options ) throws Exception {
  	  super.setOptions(options);
  	  
  	  for (int i = 0; i < options.length; i++) {
  		  if (options[i].equals("-u")) {
  			  this.postPrune = false;
  		  }
  	  }
  }
  
  public void train( DataSet ds ) throws Exception {
	  this.root = this.train_aux(ds);
	  
	  if (this.postPrune) {
		  this.prune();
	  }
  }

  private double[] getDistribution(Node node, Example example) throws Exception {
	  if (node.isLeaf()) {
		  return this.getDistribution(node);
	  }
	  
	  Node child = node.children.get(example.get(node.attribute).intValue());
	  
	  if (child.isEmpty()) {
		  return this.getDistribution(node);
	  }
	  
	  return this.getDistribution(child, example);
  }
  
  // helper function : get probability distribution on the specific node
  private double[] getDistribution(Node node) {
	  int sum = 0;
	  double[] dist = new double[node.classCounts.length];
	  
	  for (int i = 0; i < node.classCounts.length; i++) {
		  sum += node.classCounts[i];
		  
		  dist[i] = 0.01;
	  }
	  
	  for (int i = 0; i < node.classCounts.length; i++) {
		  dist[i] += (double) node.classCounts[i] / sum;
	  }
	  
	  return Utils.normalization(dist);
  }
  
  private double prune( Node node ) throws Exception {  
	  double sumError = 0.0;
	  double error = node.getError();
	  boolean allLeaf = true;
	  
	  if (node.isLeaf()) {
		  return error;
	  }
	  
	  for (Node child : node.children) {
		  if (!child.isEmpty()) {
			  sumError += this.prune(child);
			  
			  if (!child.isLeaf()) {
				  allLeaf = false;
			  }
		  }
	  }

	  if (error < sumError && allLeaf) {
		  node.children.clear();
	  }
	  
	  return error;
  }
  
  private Node train_aux( DataSet ds ) throws Exception {
	  Node node = new Node(ds.getExamples().getClassCounts());
	  
	  if (ds.homogeneous() || ds.getExamples().size() <= 3) {
		  return node;
	  }
	  
	  node.attribute = ds.getBestSplittingAttribute();
	  
	  ArrayList<DataSet> datasets = ds.splitOnAttribute(node.attribute);
	  
	  for (DataSet dataset : datasets) {
		  if (dataset.isEmpty()) {
			  Node leaf = new Node();
			  
			  leaf.label = ds.getMajorityClassLabel();
			  node.children.add(leaf);
		  }
		  else {
			  node.children.add(this.train_aux(dataset));
		  }
	  }
	  
	  return node;
  }
  
  public static void main( String args[] ) {
      try {
          Evaluator evaluator = new Evaluator(new DT(), args);
          Performance performance = evaluator.evaluate();
          System.out.println( performance );
      } // try
      catch ( Exception e ) {
          System.out.println( e.getMessage() );
          e.printStackTrace();
      } // catch
  } // DT::main

}
