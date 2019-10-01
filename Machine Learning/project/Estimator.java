public abstract class Estimator extends Object {
  protected int n = 0; // number of samples
  
  public Estimator() {
	  super();
  }
  
  public Integer getN() {
	  return n;
  }
  
  abstract public void add( Number x ) throws Exception;
  
  abstract public Double getProbability( Number x );

}
