public class GaussianEstimator extends Estimator {
	
	  protected Double sum = 0.0;
	  protected Double sumsqr = 0.0;
	  protected final static Double oneOverSqrt2PI = 1.0/Math.sqrt(2.0*Math.PI);

	  public GaussianEstimator() {
		  super();
	  }
	  
	  public void add( Number x ) throws Exception {
		  sum += x.doubleValue();
		  sumsqr += x.doubleValue() * x.doubleValue();
		  n++;
	  }
	  
	  public Double getMean() {
		  return n == 0 ? 0 : sum / n;
	  }
	  
	  public Double getVariance() {
		  return (sumsqr - sum * sum / n) / (n - 1);
	  }
	  
	  public Double getProbability( Number x ) {
		  double mean = this.getMean(), variance = this.getVariance();
		  
		  return oneOverSqrt2PI / Math.sqrt(variance) 
				  * Math.exp(-Math.pow(x.doubleValue() - mean, 2) / 2 / variance);
	  }
	  
}
