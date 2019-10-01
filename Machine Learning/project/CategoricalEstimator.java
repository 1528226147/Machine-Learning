import java.util.ArrayList;
import java.util.Collections;

public class CategoricalEstimator extends Estimator {
	
  protected ArrayList<Integer> dist;

  public CategoricalEstimator() {
	  super();
	  
	  dist = new ArrayList<Integer>();
  }
  
  // number of categories
  public CategoricalEstimator( Integer k ) {
	  super();
	  
	  dist = new ArrayList<Integer>(Collections.nCopies(k, 0));
  }
  
  public void add( Number x ) throws Exception {
	  dist.set(x.intValue(), dist.get(x.intValue()) + 1);
	  this.n++;
  }
  
  // add one smoothing
  public Double getProbability( Number x ) {
	  return (double) (dist.get(x.intValue()) + 1) / (n + dist.size());
  }
  
}
