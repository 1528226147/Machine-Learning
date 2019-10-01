import java.util.Random;

public class Evaluator implements OptionHandler {

  private long seed = 2026875034;
  private Random random;
  private int folds = 10;
  private Classifier classifier;
  private TrainTestSets tts;
  private double holdOut = 0;

  public Evaluator() {
	  random = new Random(seed);
  }
  
  public Evaluator( Classifier classifier, String[] options ) throws Exception {
	  this.classifier = classifier;
	  this.random = new Random(seed);
	                            
	  this.tts = new TrainTestSets(options);
	  this.classifier.setOptions(options);
	  this.setOptions(options);
  }
  
  public Performance evaluate() throws Exception {
	  DataSet train = tts.getTrainingSet();
	  DataSet test = tts.getTestingSet();
	  
	  if (train == null) {
		  throw new Exception("TrainSet is null!");
	  }
	  
	  if (test == null) {
		  if (this.holdOut == 0.0) {
			  return this.crossValidation(train);
		  }
		  else {
			  return this.holdOutValidation(train);
		  }
	  }
	  else {
		  Classifier c = classifier.clone();

		  c.train(train);
		  
		  return c.classify(test);
	  }
  }
  
  public long getSeed() {
	  return this.seed;
  }
  
  public void setOptions( String args[] ) throws Exception {
	  if (args.length == 0) {
		  throw new Exception("Wrong number of options!");
	  }
	  
	  for (int i = 0; i < args.length; i += 2) {
		  if (args[i].equals("-x")) {
			  this.folds = Integer.parseInt(args[i + 1]);
		  }
		  
		  if (args[i].equals("-s")) {
			  this.setSeed(Long.parseLong(args[i + 1]));
		  }
		  
		  if (args[i].equals("-p")) {
			  this.holdOut = Double.parseDouble(args[i + 1]);
		  }
	  }
  }
  
  public void setSeed( long seed ) {
	  this.seed = seed;
	  
	  random.setSeed(seed);
  }
  
  private Performance crossValidation(DataSet train) throws Exception {
	  TrainTestSets temp;
	  Classifier c;
	  Performance p;
	  
	  train.setFolds(this.folds);
	  train.setRandom(this.random);
	  
	  // get a new classifier
	  c = this.classifier.clone();
	  
	  // get traintestset
	  temp = train.getCVSets(0);
	  
	  if (temp.getTrainingSet() == null || temp.getTestingSet() == null) {
		  throw new Exception("Set is null during cross validation!");
	  }
	  
	  c.train(temp.getTrainingSet());
	  p = c.classify(temp.getTestingSet());
	  
	  for (int i = 1; i < this.folds; i++) {
		  // get a new classifier
		  c = this.classifier.clone();
		  
		  // get traintestset
		  temp = train.getCVSets(i);
		  
		  if (temp.getTrainingSet() == null || temp.getTestingSet() == null) {
			  throw new Exception("Set is null during cross validation!");
		  }
		  
		  c.train(temp.getTrainingSet());
		  p.add(c.classify(temp.getTestingSet()));
	  }
	  
	  return p;
  }
  
  private Performance holdOutValidation(DataSet train) throws Exception {
	  Classifier c = this.classifier.clone();
	  
	  train.setRandom(random);
	  
	  TrainTestSets tts = train.getHOSets(holdOut);
	  
	  if (tts.getTrainingSet() == null || tts.getTestingSet() == null) {
		  throw new Exception("Set is null during hold-out validation!");
	  }
	  
	  c.train(tts.getTrainingSet());
	  return c.classify(tts.getTestingSet());
  }
  
}
