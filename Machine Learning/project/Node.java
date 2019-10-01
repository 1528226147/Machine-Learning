import java.util.ArrayList;

public class Node {

  public int attribute = -1;
  public int label = -1;
  public int[] classCounts = null;
  public ArrayList<Node> children = new ArrayList<Node>();

  Node() {}
  
  Node( int[] classCounts ) {
	  this.classCounts = classCounts;
	  
	  int max = 0;
	  
	  for (int i = 0; i < classCounts.length; i++) {
		  if (classCounts[i] > max) {
			  max = classCounts[i];
			  this.label = i;
		  }
	  }
  }
  
  public boolean isLeaf() {
	  return this.children.isEmpty();
  }
  
  public boolean isEmpty() {
	  return classCounts == null;
  }
  
  public double getError() {
	  int n = 0, x;
	  
	  for (int i = 0; i < classCounts.length; i++) {
		  n += classCounts[i];
	  }
	  
	  x = n - classCounts[label];
	  
	  return n * Utils.u25(n, x);
  }

}
