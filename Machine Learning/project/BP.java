import java.io.Serializable;

public class BP extends Classifier implements Serializable, OptionHandler {

	private double[][] v;
	private double[][] w;
	private int hiddenLayer = 1;
	private double learningRate = 0.9;
	private double minError = 0.1;
	
	public BP() {
		super();
	}
	
	public BP(String[] options) throws Exception {
		super(options);
		
		this.setOptions(options);
	}
	
	@Override
	public Performance classify(DataSet dataset) throws Exception {
		dataset = dataset.encode(new double[] {0, 1}, -1, false);
		
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
		return (BP) Utils.deepClone(this);
	}
	
	public void setOptions( String[] options ) throws Exception {
	  	super.setOptions(options);
	  	  
	  	for (int i = 0; i < options.length; i++) {
	  		if (options[i].equals("-J")) {
	  			this.hiddenLayer = Integer.parseInt(options[i + 1]);
	  		}
	  	}
	}

	@Override
	public double[] getDistribution(Example example) throws Exception {
		double[] hidden = new double[this.hiddenLayer + 1];
		double[] output = new double[this.w.length];
		
		hidden[this.hiddenLayer] = -1;
		
		for (int i = 0; i < hidden.length - 1; i++) {
			hidden[i] = Utils.sigmoid(Utils.dot(this.v[i], example));
		}
		
		for (int i = 0; i < output.length; i++) {					
			output[i] = Utils.sigmoid(Utils.dot(this.w[i], hidden));
		}
		
		return Utils.hardmax(output);
	}

	@Override
	public void train(DataSet dataset) throws Exception {
		dataset = dataset.encode(new double[] {0, 1}, -1, false);
		
		int epoches = 0;
		int I = dataset.getAttributes().size() - 1;
		int J = this.hiddenLayer + 1;
		int K = dataset.getAttributes().getClassAttribute().size();
		double E = this.minError + 1;
		double[] hidden = new double[J];
		double[] output = new double[K];
		double[] hError = new double[J];
		double[] oError = new double[K];

		this.v = Utils.randomArray(J - 1, I);
		this.w = Utils.randomArray(K, J);
		hidden[J - 1] = -1;
		
		while (E > this.minError) {
			if (epoches == 50000) {
				throw new FailedToConvergeException("Can not converge after 50,000 iterations!");
			}
			
			E = 0.0;
			epoches++;
			
			for (Example e : dataset.getExamples()) {
				for (int i = 0; i < J - 1; i++) {
					hidden[i] = Utils.sigmoid(Utils.dot(this.v[i], e));
				}
				
				for (int i = 0; i < K; i++) {					
					output[i] = Utils.sigmoid(Utils.dot(this.w[i], hidden));
				}

				for (int i = 0; i < K; i++) {
					if (i == e.get(e.size() - 1).intValue()) {
						oError[i] = Math.pow(1.0 - output[i], 2);
						E += oError[i] / 2;
						oError[i] *= output[i];
					}
					else {
						oError[i] = Math.pow(output[i], 2);
						E += oError[i] / 2;
						oError[i] *= output[i] - 1.0;
					}
				}
				
				for (int i = 0; i < J; i++) {
					hError[i] = 0.0;
					
					for (int j = 0; j < K; j++) {
						hError[i] += oError[j] * this.w[j][i];
					}
					
					hError[i] *= hidden[i] * (1.0 - hidden[i]);
				}
				
				for (int i = 0; i < K; i++) {
					for (int j = 0; j < J; j++) {
						this.w[i][j] += this.learningRate * oError[i] * hidden[j];
					}
				}
				
				for (int i = 0; i < J - 1; i++) {
					for (int j = 0; j < I; j++) {
						this.v[i][j] += this.learningRate * hError[i] * e.get(j);
					}
				}
			} 
		}
	}
	
    public static void main( String args[] ) {
        try {
            Evaluator evaluator = new Evaluator(new BP(), args);
            Performance performance = evaluator.evaluate();
            System.out.println( performance );
        } // try
        catch ( Exception e ) {
            System.out.println( e.getMessage() );
            e.printStackTrace();
        } // catch
    } // BP::main

}
