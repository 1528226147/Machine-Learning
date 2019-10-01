import java.util.ArrayList;
import java.util.Collections;

public class Performance extends Object {

  private Attributes attributes;
  private int[][] confusionMatrix;	// [actual][predict]
  private int corrects = 0;		
  private double sum = 0.0;
  private double sumSqr = 0.0;
  private int c;                	// number of classes
  private int n = 0;            	// number of predictions
  private int m = 0;            	// number of additions
  private double sumAUC = 0.0;
  private double sumSqrAUC = 0.0;
  private ArrayList<Pair> list;		// stores the prediction results for calculate AUC
  private double sumF1 = 0.0;
  private double sumSqrF1 = 0.0;

  public Performance( Attributes attributes ) throws Exception {
	  this.attributes = attributes;
	  this.c = attributes.getClassAttribute().size();
	  this.confusionMatrix = new int[c][c];
	  
	  this.list = new ArrayList<Pair>();
  }
  
  public void add( int actual, double[] prediction ) {
	  int maxIndex = Utils.maxIndex(prediction);
	  
	  this.list.add(new Pair(actual, prediction[0]));
	  this.confusionMatrix[actual][maxIndex]++;
	  this.n++;
	  
	  if (maxIndex == actual) {
		  this.corrects++;
	  }
  }
  
  public void add(int actual, double[] prediction, double threshold) {
	  int predict = prediction[1] >= threshold ? 1 : 0;
	  
	  this.list.add(new Pair(actual, prediction[0]));
	  this.confusionMatrix[actual][predict]++;
	  this.n++;
	  
	  if (predict == actual) {
		  this.corrects++;
	  }
  }
  
  public void add( Performance p ) throws Exception {
	  if (!this.attributes.equal(p.attributes)) {
		  throw new Exception("Performances don't match!");
	  }
	  
	  for (int i = 0; i < this.c; i++) {
		  for (int j = 0; j < this.c; j++) {
			  this.confusionMatrix[i][j] += p.confusionMatrix[i][j];
		  }
	  }
	  
	  // m == 0 means the performance haven't been added with other performance
	  // therefore, its sum and sumsqr need to be calculate before adding
	  if (this.m == 0) {
		  this.sum = this.getAccuracy();
		  this.sumSqr = this.sum * this.sum;
		  
		  this.sumAUC = this.getAUC();
		  this.sumSqrAUC = this.sumAUC * this.sumAUC;
		  
		  this.sumF1 = this.getF1();
		  this.sumSqrF1 = this.sumF1 * this.sumF1;
	  }
	  
	  if (p.m == 0) {
		  p.sum = p.getAccuracy();
		  p.sumSqr = p.sum * p.sum;
		  
		  p.sumAUC = p.getAUC();
		  p.sumSqrAUC = p.sumAUC * p.sumAUC;
		  
		  p.sumF1 = p.getF1();
		  p.sumSqrF1 = p.sumF1 * p.sumF1;
	  }
	  
	  this.list.addAll(p.list);
	  this.corrects += p.corrects;
	  this.sum += p.sum;
	  this.sumSqr += p.sumSqr;
	  this.n += p.n;
	  this.m += p.m + 1;
	  this.sumAUC += p.sumAUC;
	  this.sumSqrAUC += p.sumSqrAUC;
	  this.sumF1 += p.sumF1;
	  this.sumSqrF1 += p.sumSqrF1;
  }
  
  public double getAccuracy() {
	  return n == 0 ? 0 : (double) this.corrects / this.n;
  }
  
  public double getSDAcc() {
	  // m is the number of addition
	  // so the number of performances will be m + 1
	  return Math.sqrt((this.sumSqr - (this.sum * this.sum / (this.m + 1))) / this.m);
  }
  
  public double getAUC() {
	  double H = 0.0;
	  double prevProb = 0.0; // the probability of previous positive node
	  int prev = 0;			 // the number of previous positive nodes that have same probability
	  int positive = 0;		 // the number of positive nodes
	  
	  Collections.sort(this.list);
	  
	  for (int i = 0; i < list.size(); i++) {
		  // if current node has positive label
		  if (this.list.get(i).getLabel() == 0) {
			  positive++;
			  
			  // if the current node has same probability with the previous node
			  if (prevProb == this.list.get(i).getProbability()) {
				  prev++;
			  }
			  else {
				  prev = 1;
				  prevProb = this.list.get(i).getProbability();
			  }
		  }
		  // if current node has negative label
		  // and its probability is same as previous positive node
		  else if (prevProb == this.list.get(i).getProbability()) {
			  H += positive - 0.5 * prev;
		  }
		  else {
			  H += positive;
		  }
	  }
	  
	  return H / (positive * (this.list.size() - positive));
  }
  
  public double getSDAUC() {
	  if (this.m == 0) {
		  return this.getAUC();
	  }
	  
	  return Math.sqrt((this.sumSqrAUC - (this.sumAUC * this.sumAUC / (this.m + 1))) / this.m);
  }
  
  public double getF1() {
	  if (this.confusionMatrix[1][1] == 0) {
		  return 0;
	  }
	  
	  double precision = (double) this.confusionMatrix[1][1] / (this.confusionMatrix[0][1] + this.confusionMatrix[1][1]);
	  double recall = (double) this.confusionMatrix[1][1] / (this.confusionMatrix[1][0] + this.confusionMatrix[1][1]);
	  
	  return 2 * precision * recall / (precision + recall);
  }
  
  public double getAvgF1() {
	  if (this.m == 0) {
		  return this.getF1();
	  }
	  
	  return this.sumF1 / (this.m + 1);
  }
  
  public String toString() {
	  return "The AUC is " + (double) Math.round(this.getAUC() * 100) / 100 
			  + "\nThe Accuracy is " + (double) Math.round(this.getAccuracy() * 100) / 100;
  }

}
