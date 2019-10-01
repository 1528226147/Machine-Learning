public class TrainTestSets implements OptionHandler {

  protected DataSet train;
  protected DataSet test;

  public TrainTestSets() {}

  public TrainTestSets( String [] options ) throws Exception{
	  setOptions(options);
  }

  public TrainTestSets( DataSet train, DataSet test ) {
      setTrainingSet(train);
      setTestingSet(test);
  }

  public DataSet getTrainingSet() {
      return train;
  }

  public DataSet getTestingSet() {
      return test;
  }

  public void setTrainingSet( DataSet train ) {
      this.train = train;
  }

  public void setTestingSet( DataSet test ) {
      this.test = test;
  }

  public void setOptions( String[] options ) throws Exception {
	  if (options.length == 0) {
		  throw new Exception("Wrong number of options!");
	  }
	  
	  for (int i = 0; i < options.length; i++) {
		  if (options[i].equals("-t")) {
			  train = new DataSet();
			  train.load(options[i + 1]);
		  }
		  else if (options[i].equals("-T")) {
			  test = new DataSet();
			  test.load(options[i + 1]);
		  }
	  }
  }

  public String toString() {
	  StringBuilder str = new StringBuilder();
	  
      if (train != null) {
    	  str.append(train.toString());
      }
      
      if (test != null) {
    	  str.append(test.toString());
      }
      
      return str.toString();
  }

}
