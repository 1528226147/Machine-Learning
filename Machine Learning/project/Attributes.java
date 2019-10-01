import java.util.ArrayList;
import java.util.Scanner;

public class Attributes {

  private ArrayList<Attribute> attributes = new ArrayList<Attribute>();
  private boolean hasNumericAttributes = false;
  private int classIndex;

  public void add( Attribute attribute ) {
      this.attributes.add(attribute);
	  this.classIndex = attributes.size() - 1;
      
	  if (attribute.size() == -1) {
		  this.hasNumericAttributes = true;
	  }
  }
  
  public void add(int index, Attribute attribute) {
	  this.attributes.add(index, attribute);
	  this.classIndex = attributes.size() - 1;
	      
	  if (attribute.size() == -1) {
		  this.hasNumericAttributes = true;
	  }
	}

  public int getClassIndex() {
      return this.classIndex;
  }

  public boolean getHasNumericAttributes() {
      return this.hasNumericAttributes;
  }

  public Attribute get( int i ) {
      return this.attributes.get(i);
  }

  public Attribute getClassAttribute() {
      return this.attributes.get(this.classIndex);
  }

  public int getIndex( String name ) throws Exception {
      for (int i = 0; i < this.size(); i++) {
          if (this.attributes.get(i).getName().equals(name)) {
              return i;
          }
      }

      throw new Exception("Attribute " + name + " doesn't exist.");
  }

  public int size() {
      return this.attributes.size();
  }

  public void parse( Scanner scanner ) throws Exception {
	  Attribute a = AttributeFactory.make(scanner);
	  
	  this.add(a);
  }

  public void setClassIndex( int classIndex ) throws Exception {
      if (classIndex < 0 || classIndex >= attributes.size()) {
          throw new Exception("classIndex " + classIndex + " is out of bounds!");
      }

      this.classIndex = classIndex;
  }
  
  public boolean equal(Attributes attributes) {
	  if (attributes.size() != this.size()) {
		  return false;
	  }
	  
	  for (int i = 0; i < this.size(); i++) {
		  if (!(attributes.get(i).equal(this.get(i)))) {
			  return false;
		  }
	  }
	  
	  return true;
  }

  public String toString() {
      StringBuilder str = new StringBuilder();

      for (Attribute a : this.attributes) {
          str.append(a.toString());
      }

      return str.toString() + '\n';
  }

}
