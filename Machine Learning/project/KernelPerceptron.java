import java.io.Serializable;

public class KernelPerceptron extends Classifier implements Serializable, OptionHandler {

	private int[] alpha;
	private int[] class2binary = {-1, 1};
	private boolean calibration = false;
	private double gamma;
	private double d0;
	private DataSet dataset;
	
	public KernelPerceptron() {
		super();
	}
	
	public KernelPerceptron(String[] options) throws Exception {
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
		return Utils.maxIndex(this.getDistribution(example));
	}

	@Override
	public Classifier clone() {
		return (KernelPerceptron) Utils.deepClone(this);
	} 

	@Override
	public double[] getDistribution(Example example) throws Exception {
		double y = 0.0;
		int classIndex = this.dataset.getAttributes().getClassIndex();
		Example e;
		
		for (int i = 0; i < this.dataset.getExamples().size(); i++) {
			e = this.dataset.getExamples().get(i);

			y += this.quadraticKernel(Utils.dot(e, example, e.size() - 1)) * this.class2binary[e.get(classIndex).intValue()] * this.alpha[i];
		}
		
		if (!this.calibration) {
			return y > 0 ? new double[] {0.0, 1.0} : new double[] {1.0, 0.0};
		}
		else {
			y = 1 / (1 + Math.exp(-this.gamma * (y - this.d0)));
			return new double[] {1 - y, y};
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
		dataset = dataset.encode(new double[] {-1, +1}, -1, false);
		this.dataset = dataset;
		
		boolean converge = false;
		int classIndex = dataset.getAttributes().getClassIndex();
		Examples examples = dataset.getExamples();
		Example e;
		double sum;
		int epoches = 0;
		
		this.alpha = new int[dataset.getExamples().size()];
		
		while (!converge) {
			if (epoches == 50000) {
				break;
			}
			
			converge = true;
			epoches++;
			
			for (int i = 0; i < examples.size(); i++) {
				e = examples.get(i);
				sum = 0.0;
				
				for (int j = 0; j < examples.size(); j++) {
					sum += this.quadraticKernel(Utils.dot(examples.get(j), e, e.size() - 1)) * this.alpha[j] * this.class2binary[examples.get(j).get(classIndex).intValue()];
				}
				
				if (this.class2binary[e.get(classIndex).intValue()] * sum <= 0) {
					this.alpha[i]++;
					converge = false;
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
				temp = 0.0;
				
				for (int j = 0; j < examples.size(); j++) {
					temp += this.quadraticKernel(Utils.dot(examples.get(j), e, e.size() - 1)) * this.alpha[j] * this.class2binary[examples.get(j).get(classIndex).intValue()];
				}

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
			variance = (posSumSqr + negSumSqr - (posSum * posSum + negSum * negSum) / (pos + neg)) / (pos + neg);
			
			this.gamma = (posMean - negMean) / variance;
			this.d0 = (posMean + negMean) / 2;
		}
	}
	
	private double quadraticKernel(double x) {
		return x * x;
	}
	
    public static void main( String args[] ) {
        try {
            Evaluator evaluator = new Evaluator(new KernelPerceptron(), args);
            Performance performance = evaluator.evaluate();
            System.out.println( performance );
        } // try
        catch ( Exception e ) {
            System.out.println( e.getMessage() );
            e.printStackTrace();
        } // catch
    } // KernelPerceptron::main

}
