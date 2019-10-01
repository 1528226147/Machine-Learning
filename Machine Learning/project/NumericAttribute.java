public class NumericAttribute extends Attribute {

  public NumericAttribute() {
	  super();
  }

  public NumericAttribute( String name ) {
      super(name);
  }

  public boolean validValue( Double value ) {
      return true;
  }
  
  public boolean equal(Attribute attribute) {
  	if (this.size() != attribute.size() || !this.name.equals(attribute.getName())) {
  		return false;
  	}
  	
  	return true;
  }
  
  public String toString() {
	  return super.toString() + " numeric\n";
  }

}
