public class Attribute extends Object{

    protected String name;

    public Attribute() {
    	super();
    	name = "";
    }

    public Attribute( String name ) {
        this.name = name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public int size() {
        return -1;
    }
    
    public boolean equal(Attribute attribute) {
    	return this.name.equals(attribute.getName());
    }

    public String toString() {
        return "@attribute " + this.getName();
    }

}
