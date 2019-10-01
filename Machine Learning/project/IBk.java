import java.io.Serializable;
import java.util.Arrays;

public class IBk extends Classifier implements Serializable, OptionHandler {
	
    protected DataSet dataset;
    protected Scaler scaler;
    protected int k = 3;

    public IBk() {
    	super();
    	
    	scaler = new Scaler();
    }
    
    public IBk( String[] options ) throws Exception {
        super(options);
        
        scaler = new Scaler();
        this.setOptions(options);
    }
    
    public Performance classify( DataSet dataset ) throws Exception {
    	Examples examples = dataset.getExamples();
    	Performance performance = new Performance(this.dataset.getAttributes());
    	
    	for (Example e : examples) {
    		performance.add(
    				(e.get(dataset.getAttributes().getClassIndex())).intValue(), 
    				this.getDistribution(scaler.scale(e)));
    	}
    	
    	return performance;
    }
    
    public int classify( Example query ) throws Exception {
    	return Utils.maxIndex(this.getDistribution(scaler.scale(query)));
    }
    
    public Classifier clone() {
    	return (IBk) Utils.deepClone(this);
    }
    
    public double[] getDistribution( Example query ) throws Exception {
    	Attributes attributes = dataset.getAttributes();
    	Examples examples = dataset.getExamples();
    	int classIndex = attributes.getClassIndex();
    	
    	double[] distances = new double[this.k];	// distances of k nearest neighbors
    	int[] classLabels = new int[this.k];		// class labels of k nearest neighbors
    	int sz = 0;									// the number of nearest neighbors we now have
    	double distance;							// current example's distance to the query
    	
    	
    	// Calculate the distances
    	for (Example e : examples) {
    		distance = 0;
    		
        	for (int i = 0; i < e.size(); i++) {
        		// If the index is classIndex then continue
        		if (i == attributes.getClassIndex()) {
        			continue;
        		}
        		
        		// Numeric Attribute
        		if (attributes.get(i).size() == -1) {
        			distance += (e.get(i) - query.get(i)) * (e.get(i) - query.get(i));
        		}
        		// Nominal Attribute
        		else if (!e.get(i).equals(query.get(i))) {
            		distance += 1;	
        		}
        	}
        	
        	distance = Math.sqrt(distance);
        	
        	// if there are already k nearest neighbors
        	if (sz == this.k) {
        		// find the neighbor with max distances among those k neighbors
        		int index = Utils.maxIndex(distances);
        		
        		// check if current example is closer than the neighbor
        		// if so, replace the neighbor with current example
        		if (distances[index] > distance) {
        			distances[index] = distance;
        			classLabels[index] = e.get(classIndex).intValue();
        		}
        	}
        	else {
        		distances[sz] = distance;
        		classLabels[sz++] = e.get(classIndex).intValue();
        	}
    	}
    	
    	double[] distribution = new double[attributes.getClassAttribute().size()];
    	double proportion = 1 / (double) this.k;
    	
    	// assign a small value to all probability distribution
    	Arrays.fill(distribution, 0.01);
    	
    	for (int classLabel : classLabels) {
    		distribution[classLabel] += proportion;
    	}
    	
    	// normalize the distribution and return
    	return Utils.normalization(distribution);
    }

    public void setK( int k ) throws Exception {
    	if (k == 0) {
    		throw new Exception("Can't set k to 0!");
    	}
    	
        this.k = k;
    }

    public void setOptions( String[] options ) throws Exception {
    	super.setOptions(options);
         
        for (int i = 0; i < options.length; i++) {
        	if (options[i].equals("-k")) {
        		try {
					this.setK(Integer.parseInt(options[i + 1]));
				} catch (Exception e) {
					e.printStackTrace();
				}
        	}
        }
    }

    public void train( DataSet dataset ) throws Exception {
    	this.scaler.configure(dataset);
    	this.dataset = scaler.scale(dataset);
    }
    
    public static void main( String args[] ) {
        try {
            Evaluator evaluator = new Evaluator(new IBk(), args);
            Performance performance = evaluator.evaluate();
            System.out.println( performance );
        } // try
        catch ( Exception e ) {
            System.out.println( e.getMessage() );
            e.printStackTrace();
        } // catch
    } // IBk::main
    
}
