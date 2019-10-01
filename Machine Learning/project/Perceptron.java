import java.io.Serializable;

public class Perceptron extends Classifier implements Serializable, OptionHandler {
	
	private double[] weights;
	private double learningRate = 0.9;
	private int[] class2binary = {-1, 1};
	private double gamma;
	private double d0;
	private boolean calibration = false;
	
	public Perceptron() {
		super();
	}
	
	public Perceptron(String[] options) throws Exception {
		super(options);
		
		this.setOptions(options);
	}
	
	@Override
	public Performance classify(DataSet dataset) throws Exception {
		dataset = dataset.encode(new double[] {-1, +1}, -1, false);
		
	  	Examples examples = dataset.getExamples();
	  	Performance performance = new Performance(dataset.getAttributes());
	  	
	  	if (this.calibration) {
	  		double threshold = 1 / (1 + Math.exp(-this.gamma * (-this.d0)));
	  		
	  		for (Example e : examples) {
		  		performance.add(
		  				e.get(e.size() - 1).intValue(),
		  				this.getDistribution(e),
		  				threshold);
		  	}
	  	}
	  	else {
	  		for (Example e : examples) {
	  			performance.add(
	  					e.get(e.size() - 1).intValue(),
	  					this.getDistribution(e));
	  		}
	  	}
	  	
	  	return performance;
	}

	@Override
	public int classify(Example example) throws Exception {
		if (this.calibration) {
			double threshold = 1 / (1 + Math.exp(-this.gamma * (-this.d0)));
			
			return this.getDistribution(example)[1] >= threshold ? 1 : 0;
		}
		
		return Utils.maxIndex(this.getDistribution(example));
	}

	@Override
	public Classifier clone() {
		return (Perceptron) Utils.deepClone(this);
	}

	@Override
	public double[] getDistribution(Example example) throws Exception {
		double d = Utils.dot(this.weights, example);
		
		if (!this.calibration) {
			return d > 0 ? new double[] {0.0, 1.0} : new double[] {1.0, 0.0};
		}
		else {
			d = 1 / (1 + Math.exp(-this.gamma * (d - this.d0)));
			return new double[] {1 - d, d};
		}
	}
	
	public void setOptions( String[] options ) throws Exception {
	  	super.setOptions(options);
	  	  
	  	for (int i = 0; i < options.length; i++) {
	  		if (options[i].equals("-fc")) {
	  			this.calibration = true;
	  		}
	  	}
	}

	@Override
	public void train(DataSet dataset) throws Exception {
		dataset = dataset.encode(new double[] {-1.0, 1.0}, -1, false);
		
		boolean converge = false;
		int epoches = 0;
		int classIndex = dataset.getAttributes().getClassIndex();
		Examples examples = dataset.getExamples();
		Example e;

		this.weights = new double[dataset.getAttributes().size() - 1];
		
		while (!converge) {
			if (epoches == 50000) {
				break;
			}
			
			converge = true;
			epoches++;
			
			for (int i = 0; i < examples.size(); i++) {
				e = examples.get(i);
				
				if (this.class2binary[e.get(classIndex).intValue()] * Utils.dot(this.weights, e) <= 0) {
					converge = false;
					
					for (int j = 0; j < this.weights.length; j++) {
						this.weights[j] += this.learningRate * this.class2binary[e.get(classIndex).intValue()] * e.get(j);
					}
				}
			}
		}
		
		if (this.calibration) {
			double posSum = 0.0;
			double posSumSqr = 0.0;
			double posMean;
			int pos = 0;
			double negSum = 0.0;
			double negSumSqr = 0.0;
			double negMean;
			int neg = 0;
			double variance;
			double temp;
			
			for (int i = 0; i < examples.size(); i++) {
				e = examples.get(i);
				temp = Utils.dot(this.weights, e);
				
				if (this.class2binary[e.get(classIndex).intValue()] == -1) {
					negSum += temp;
					negSumSqr += temp * temp;
					neg++;
				}
				else {
					posSum += temp;
					posSumSqr += temp * temp;
					pos++;
				}
			}
			
			posMean = posSum / pos;
			negMean = negSum / neg;
			// variance = (posSumSqr + negSumSqr - posSum * posSum / pos - negSum * negSum / neg) / (pos + neg - 1);
			variance = (posSumSqr + negSumSqr - (posSum * posSum + negSum * negSum) / (pos + neg)) / (pos + neg - 1);

			this.gamma = (posMean - negMean) / variance;
			this.d0 = (posMean + negMean) / 2;
		}
	}
    
    public static void main( String args[] ) {
        try {
            Evaluator evaluator = new Evaluator(new Perceptron(), args);
            Performance performance = evaluator.evaluate();
            System.out.println( performance );
        } // try
        catch ( Exception e ) {
            System.out.println( e.getMessage() );
            e.printStackTrace();
        } // catch
    } // Perceptron::main

}
