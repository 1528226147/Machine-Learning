import java.io.Serializable;
import java.util.Arrays;

public class MBW extends Classifier implements OptionHandler, Serializable {
	
	private int[] class2binary = {-1, 1};
	private boolean vote = false;
	private boolean linearEncoding = true;
	private double alpha = 1.5;
	private double beta = 0.5;
	private double theta = 1.0;
	private double M = 1.0;
	private double[] u;
	private double defaultU = 2.0;
	private double[] v;
	private double defaultV = 1.0;
	private int c = 0;
	private int Z = 0;
	private double[] averageU;
	private double[] averageV;
	
	public MBW() {
		super();
	}
	
	public MBW(String[] options) throws Exception {
		super(options);
		
		this.setOptions(options);
	}
	
	@Override
	public Performance classify(DataSet dataset) throws Exception {
		dataset = dataset.encode(new double[] {0, +1}, 1, this.linearEncoding);
		dataset.normalization();
		
	  	Examples examples = dataset.getExamples();
	  	Performance performance = new Performance(dataset.getAttributes());
	  	
  		for (Example e : examples) {
  			performance.add(
  					e.get(e.size() - 1).intValue(),
  					this.getDistribution(e));
  		}
	  	
	  	return performance;
	}

	@Override
	public int classify(Example example) throws Exception {
		return Utils.maxIndex(this.getDistribution(example));
	}

	@Override
	public Classifier clone() {
		return (MBW) Utils.deepClone(this);
	}
	
	public void setOptions( String[] options ) throws Exception {
		super.setOptions(options);
	  	  
	    for (int i = 0; i < options.length; i++) {
	  		if (options[i].equals("-v")) {
	  			this.vote = true;
	  		}
	  	}
	}

	@Override
	public double[] getDistribution(Example example) throws Exception {
		return this.score(example) > 0 ? new double[] {0.0, 1.0} : new double[] {1.0, 0.0};
	}

	@Override
	public void train(DataSet dataset) throws Exception {
		dataset = dataset.encode(new double[] {0, 1}, 1, this.linearEncoding);
		dataset.normalization();
		
		int classIndex = dataset.getAttributes().getClassIndex();
		Examples examples = dataset.getExamples();
		Example e;
		int y;
		
		this.u = new double[dataset.getAttributes().size() - 1];
		this.v = new double[dataset.getAttributes().size() - 1];
		this.averageU = new double[dataset.getAttributes().size() - 1];
		this.averageV = new double[dataset.getAttributes().size() - 1];
		Arrays.fill(this.u, this.defaultU);
		Arrays.fill(this.v, this.defaultV);
		
		for (int i = 0; i < examples.size(); i++) {
			e = examples.get(i);
			y = this.class2binary[e.get(classIndex).intValue()];
			
			if (y * this.score(e) <= M) {
				if (this.vote) {
					for (int j = 0; j < this.u.length; j++) {
						this.averageU[j] += this.u[j] * this.c;
						this.averageV[j] += this.v[j] * this.c;
					}
				}
				
				this.Z += this.c;
				this.c = 0;
				
				for (int j = 0; j < this.u.length; j++) {
					if (e.get(j) == 0) {
						continue;
					}
					if (y > 0) {
						this.u[j] *= this.alpha * (1 + e.get(j));
						this.v[j] *= this.beta * (1 - e.get(j));
					}
					else {
						this.u[j] *= this.beta * (1 - e.get(j));
						this.v[j] *= this.alpha * (1 + e.get(j));
					}
				}
			}
			else {
				this.c++;
			}
		}
		
		this.Z += this.c;
		
		if (vote) {
			for (int i = 0; i < this.u.length; i++) {
				this.averageU[i] = (this.averageU[i] + this.u[i] * this.c) / this.Z;
				this.averageV[i] = (this.averageV[i] + this.v[i] * this.c) / this.Z;
			}
			
			this.u = this.averageU;
			this.v = this.averageV;
		}
	}
	
	private double score(Example e) {
		return Utils.dot(this.u, e) - Utils.dot(this.v, e) - this.theta;
	}
    
    public static void main( String args[] ) {
        try {
            Evaluator evaluator = new Evaluator(new MBW(), args);
            Performance performance = evaluator.evaluate();
            System.out.println( performance.getAvgF1() );
        } // try
        catch ( Exception e ) {
            System.out.println( e.getMessage() );
            e.printStackTrace();
        } // catch
    } // MBW::main

}
