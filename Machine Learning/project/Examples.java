import java.util.ArrayList;
import java.util.Scanner;

public class Examples extends ArrayList<Example> {

  private Attributes attributes;
  private int[] classCounts;

  public Examples( Attributes attributes ) {
	  super();

      this.attributes =  attributes;
      this.classCounts = new int[attributes.getClassAttribute().size()];
  }
  
  public void parse( Scanner scanner ) throws Exception {
	  while (scanner.hasNextLine()) {
		  String line = scanner.nextLine();

		  if (line.equals("")) {
			  continue;
		  }

		  Scanner s = new Scanner(line);
		  Example e = new Example();

		  for (int i = 0; i < attributes.size(); i++) {
			  if (!s.hasNext()) {
				  s.close();

				  throw new Exception("Too many attributes for the example!");
			  }

			  Attribute attribute = attributes.get(i);

			  if (attribute.size() != -1) {
				  e.add((double) ((NominalAttribute) attribute).getIndex(s.next()));
			  }
			  else {
				  double value = s.nextDouble();
				  if (((NumericAttribute) attribute).validValue(value)) {
					  e.add(value);
				  }
			  }
		  }
		  
		  this.add(e);

		  s.close();
	  }
  }

  public String toString() {
	StringBuilder str = new StringBuilder("@examples\n\n");

	for (int i = 0; i < this.size(); i++) {
		Example e = this.get(i);

		for (int j = 0; j < this.attributes.size(); j++) {
			Attribute attribute = this.attributes.get(j);

			if (attribute.size() != -1) {
				str.append(((NominalAttribute) attribute).getValue(e.get(j).intValue()) + ' ');
			}
			else {
				str.append(e.get(j)).append(' ');
			}
		}

		str.append('\n');
	}

	return str.toString();
  }

  public int[] getClassCounts() {  
	  return this.classCounts;
  }
  
  public void setClassCounts(int[] classCounts) {
	  this.classCounts = classCounts;
  }
  
  public boolean add( Example example ) {
	  super.add(example);
	  
	  if (example.size() != 0) {
		  classCounts[example.get(this.attributes.getClassIndex()).intValue()]++;
	  }
	  
	  return true;
  }

}
