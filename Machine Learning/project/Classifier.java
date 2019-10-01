public abstract class Classifier extends Object implements OptionHandler { 
    
    public Classifier() {
    	super();
    }

    public Classifier( String[] options ) throws Exception {
        super();
        
        setOptions(options);
    }

    abstract public Performance classify( DataSet dataset ) throws Exception;
    abstract public int classify( Example example ) throws Exception;
    abstract public Classifier clone();
    abstract public double[] getDistribution( Example example ) throws Exception;

    public void setOptions( String[] options ) throws Exception {
  	  if (options.length == 0) {
		  throw new Exception("Wrong number of options!");
	  }
    }
    
    public String toString() {
    	return "I'm a Classifier!";
    }
    
    abstract public void train( DataSet dataset ) throws Exception;

}
