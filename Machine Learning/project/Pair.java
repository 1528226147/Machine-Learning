public class Pair implements Comparable<Pair> {

	private double probability;
	private int label;
	
	public Pair (int label, double probability) {
		this.label = label;
		this.probability = (double) Math.round(probability * 1000) / 1000;
	}
	
	@Override
	public int compareTo(Pair o) {
		if (this.probability > o.probability) {
			return -1;
		}
		else if (this.probability == o.probability) {
			if (this.label > o.label) {
				return 1;
			}
			else if (this.label == o.label){
				return 0;
			}
			
			return -1;
		}
		
		return 1;
	}
	
	public double getProbability() {
		return this.probability;
	}
	
	public int getLabel() {
		return this.label;
	}
	
}
