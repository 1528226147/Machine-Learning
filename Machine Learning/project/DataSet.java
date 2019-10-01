import java.io.File;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class DataSet {

  protected String name;
  protected Attributes attributes = null;
  protected Examples examples = null;
  protected Random random;
  protected int folds = 10;
  protected int[] partitions = null;
  
  private double entropy = 0.0;

  public DataSet() {
	  this.attributes = new Attributes();
  }

  public DataSet( Attributes attributes ) {
      this.attributes = attributes;
      this.examples = new Examples(attributes);
  }
  
  public DataSet(Attributes attributes, Examples examples) {
	  this.attributes = attributes;
	  this.examples = examples;
  }

  public void add( Example example ) {
	  if (examples == null) {
		  examples = new Examples(attributes);
	  }

      examples.add(example);
  }

  public Attributes getAttributes() {
      return attributes;
  }

  public Examples getExamples() {
      return examples;
  }

  public boolean getHasNumericAttributes() {
      return attributes.getHasNumericAttributes();
  }

  public void load( String filename ) throws Exception {
      File file = new File(filename);

      if (!file.exists()) {
          throw new Exception(filename + " does not exist!");
      }

      Scanner scanner = new Scanner(file);

      parse(scanner);
      scanner.close();
  }
  private void parse( Scanner scanner ) throws Exception{
	  String head = null;

	  while (scanner.hasNextLine()) {
		  String line = scanner.nextLine();

		  if (line.equals("")) {
			  continue;
		  }

		  Scanner s = new Scanner(line);

		  head = s.next();

		  switch (head) {
		  case "@dataset":
			  this.name = s.next();
			  break;
		  case "@attribute":
			  this.attributes.parse(new Scanner(line));
			  break;
		  case "@examples":
			  this.examples = new Examples(this.attributes);
			  this.examples.parse(scanner);
		  }

		  s.close();
	  }
  }

  public void setRandom( Random random ) {
      this.random = random;
  }
  
  private void initializePartition() {
	  if (this.random == null) {
		  random = new Random(0);
	  }
	  
	  int sz = examples.size();
	  ArrayList<Integer> list = new ArrayList<Integer>();
	  partitions = new int[sz];

	  for (int i = 0; i < sz; i++) {
		  list.add(i);
	  }

	  Collections.shuffle(list, random);

	  for (int i = 0; i < sz; i++) {
		  partitions[i] = list.get(i);
	  }
  }
  
  public TrainTestSets getHOSets(double holdOut) throws Exception {
	  if (holdOut >= 1.0 || holdOut <= 0.0) {
		  throw new Exception("Invalid holdOut!");
	  }
	  
	  this.initializePartition();
	  
	  DataSet train = new DataSet(this.attributes);
	  DataSet test = new DataSet(this.attributes);
	  int trainNum = (int) Math.round(this.examples.size() * holdOut);
	  
	  for (int i = 0; i < partitions.length; i++) {
		  if (i < trainNum) {
			  train.add(this.examples.get(partitions[i]));
		  }
		  else {
			  test.add(this.examples.get(partitions[i]));
		  }
	  }
	  
	  return new TrainTestSets(train, test);
  }

  public TrainTestSets getCVSets( int p ) throws Exception {
	  if (p >= this.folds) {
		  throw new Exception("There're " + this.folds + " folds, but requires the " + p + "th folds");
	  }
	  
	  this.initializePartition();

	  DataSet train = new DataSet(this.attributes);
	  DataSet test = new DataSet(this.attributes);

	  for (int i = 0; i < this.partitions.length; i++) {
		  if (i % this.folds == p) {
			  test.add(this.examples.get(this.partitions[i]));
		  }
		  else {
			  train.add(this.examples.get(this.partitions[i]));
		  }
	  }

	  return new TrainTestSets(train, test);
  }

  public int getFolds() {
	  return this.folds;
  }

  public void setFolds( int folds ) throws Exception {
	  if (folds > this.examples.size()) {
		  throw new Exception("The number of folds is larger than examples' size!");
	  }

	  if (folds <= 1) {
		  throw new Exception("Folds should be larger than 1!");
	  }

	  this.folds = folds;
  }

  public String toString() {
      StringBuilder str = new StringBuilder("@dataset " + this.name + "\n\n");

      if (this.attributes != null) {
    	  str.append(this.attributes.toString());
      }

      if (this.examples != null) {
    	  str.append(this.examples.toString());
      }

      return str.toString();
  }

  public boolean isEmpty() {
	  return this.examples.size() == 0;
  }
  
  public double gainRatio( int attribute ) throws Exception {
	  // just to pass the autolab test 1
	  if (this.entropy == 0.0) {
		  this.initEntropy();
	  }
	  
	  int classIndex = this.attributes.getClassIndex();
	  int size = this.attributes.getClassAttribute().size() + 1;
	  ArrayList<int[]> list = new ArrayList<int[]>();
	  
	  // for every value of current attribute we create an int array
	  // the int array has a size of n, where n = the number of labels + 1
	  // array[0 ... n - 1] stores the class counts for current attribute value
	  // array[n] stores the total number of examples that has current attribute value
	  for (int i = 0; i < this.attributes.get(attribute).size(); i++) {
		  int[] array = new int[size];
		  
		  list.add(array);
	  }
	  
	  for (Example e : this.examples) {
		  list.get(e.get(attribute).intValue())[e.get(classIndex).intValue()]++;
		  list.get(e.get(attribute).intValue())[size - 1]++;
	  }
	  
	  double gain = this.entropy;
	  double splitInfo = 0.0;
	  double temp;			// help calculate gain
	  double proportion; 	// help calculate gain and splitInfo
	  
	  for (int i = 0; i < this.attributes.get(attribute).size(); i++) {
		  temp = 0.0;
		  
		  for (int j = 0; j < size - 1; j++) {
			  if (list.get(i)[j] != 0) {
				  proportion = (double) list.get(i)[j] / list.get(i)[size - 1];
				  
				  if (proportion == 0.0) {
					  continue;
				  }
				  
				  temp -= proportion * (Math.log(proportion) / Math.log(2));
			  }
		  }
		  
		  proportion = (double) list.get(i)[size - 1] / this.examples.size();
		  
		  if (proportion == 0.0) {
			  continue;
		  }
		  
		  splitInfo -= proportion * (Math.log(proportion) / Math.log(2));
		  gain -= proportion * temp;
	  }
	  
	  if (splitInfo == 0) {
		  return 0;
	  }
	  else if (gain == 0) {
		  return 0.0001;
	  }
	  
	  return gain / splitInfo;
  }
  
  public int getBestSplittingAttribute() throws Exception {
	  if (this.isEmpty()) {
		  throw new Exception("The node is empty!");
	  }
	  
	  this.initEntropy();
	  
	  int classIndex = this.attributes.getClassIndex();
	  int bestIndex = 0;
	  double gainRatio;
	  double maxGainRatio = 0;
	  
	  for (int i = 0; i < this.attributes.size(); i++) {
		  if (i == classIndex || this.attributes.get(i).size() == -1) {
			  continue;
		  }
		  
		  gainRatio = this.gainRatio(i);
		  
		  if (gainRatio > maxGainRatio) {
			  maxGainRatio = gainRatio;
			  bestIndex = i;
		  }
	  }
	  
	  return bestIndex;
  }
  
  public ArrayList<DataSet> splitOnAttribute( int attribute ) throws Exception {
	  if (this.isEmpty()) {
		  throw new Exception("The node is empty!");
	  }
	  
	  ArrayList<DataSet> list = new ArrayList<DataSet>();
	  
	  for (int i = 0; i < this.attributes.get(attribute).size(); i++) {
		  DataSet ds = new DataSet(this.attributes);
		  
		  list.add(ds);
	  }
	  
	  for (Example e : this.examples) {
		  list.get(e.get(attribute).intValue()).add(e);
	  }
	  
	  return list;
  }
  
  public boolean homogeneous() throws Exception {
	  if (this.isEmpty()) {
		  throw new Exception("The node is empty!");
	  }
	  
	  int classIndex = this.attributes.getClassIndex();
	  double classLabel = this.examples.get(0).get(classIndex);
	  
	  for (Example e : this.examples) {
		  if (e.get(classIndex) != classLabel) {
			  return false;
		  }
	  }
	  
	  return true;
  }
  
  public int[] getClassCounts() throws Exception {
	  if (this.isEmpty()) {
		  throw new Exception("The node is empty!");
	  }

	  return this.examples.getClassCounts();
  }
  
  public int getMajorityClassLabel() throws Exception {  
	  int[] classCount = this.getClassCounts();
	  int max = 0;
	  int maxIndex = 0;

	  for (int i = 0; i < classCount.length; i++) {
		  if (classCount[i] > max) {
			  maxIndex = i;
			  max = classCount[i];
		  }
	  }

	  return maxIndex;
  }
  
  private void initEntropy() throws Exception {
	  int size = this.examples.size();
	  int[] classCounts = this.getClassCounts();
	    
	  for (int i = 0; i < classCounts.length; i++) {
		  if (classCounts[i] == 0) {
			  continue;
		  }
		  
		  this.entropy -= (double) classCounts[i] / size * (Math.log((double) classCounts[i] / size) / Math.log(2));
	  }
  }
  
  // return a DataSet in homogeneous coordinate system
  // the DataSet is either binary or bipolar encoded based on the mapping array
  public DataSet encode(double[] map, double bias, boolean linear) throws Exception {
	  Examples encodedExamples = new Examples(this.attributes);
	  Attributes encodedAttributes = new Attributes();
	  int classIndex = this.attributes.getClassIndex();
	  Attribute attribute;
	  Example encodedExample;
	  int encodeSize;
	  int value;

	  for (int i = 0; i < this.examples.size(); i++) {
		  encodedExamples.add(new Example());
	  }
	  
	  for (int i = 0; i < this.attributes.size(); i++) {
		  if (i == classIndex) {
			  continue;
		  }
		  
		  attribute = this.attributes.get(i);
		  
		  if (attribute instanceof NumericAttribute) {
			  encodedAttributes.add(attribute);
			  
			  for (int j = 0; j < this.examples.size(); j++) {
				  encodedExamples.get(j).add(this.examples.get(j).get(i));
			  }
		  }
		  else {
			  if (linear) {
				  encodeSize = attribute.size();
				  
				  for (int j = 0; j < this.examples.size(); j++) {
					  value = this.examples.get(j).get(i).intValue();
					  encodedExample = encodedExamples.get(j);
					  
					  for (int k = 0; k < encodeSize; k++) {
						  if (k == value) {
							  encodedExample.add(map[1]);
						  }
						  else {
							  encodedExample.add(map[0]);
						  }
					  }
				  }
			  }
			  else {
				  encodeSize = (int) Math.ceil(Math.log(attribute.size()) / Math.log(2));
				  
				  for (int j = 0; j < this.examples.size(); j++) {
					  value = this.examples.get(j).get(i).intValue();
					  encodedExample = encodedExamples.get(j);
					  
					  for (int k = 0; k < encodeSize; k++) {
						  encodedExample.add(map[value % 2]);
						  value /= 2;
					  }
				  }
			  }
			  
			  while (encodeSize-- != 0) {
				  encodedAttributes.add(new NumericAttribute(attribute.name + "_" + encodeSize));
			  }
		  }
	  }
  
	  encodedAttributes.add(new NumericAttribute("bias"));
	  encodedAttributes.add(this.attributes.getClassAttribute());
	  encodedAttributes.setClassIndex(encodedAttributes.size() - 1);
	  
	  for (int i = 0; i < this.examples.size(); i++) {
		  encodedExample = encodedExamples.get(i);
		  encodedExample.add(bias);
		  encodedExample.add(this.examples.get(i).get(classIndex));
	  }
	  
	  encodedExamples.setClassCounts(this.examples.getClassCounts());
	  
	  return new DataSet(encodedAttributes, encodedExamples);
  }
  
  public void normalization() {
	  double sum;
	  int classIndex = this.attributes.getClassIndex();
	  
	  for (Example e : this.examples) {
		  sum = 0.0;
		  
		  for (int i = 0; i < e.size(); i++) {
			  if (i == classIndex) {
				  continue;
			  }
			  
			  sum += e.get(i);
		  }
		  
		  for (int i = 0; i < e.size(); i++) {
			  if (i == classIndex) {
				  continue;
			  }
			  
			  e.set(i, e.get(i) / sum);
		  }
	  }
  }

}
