import java.util.ArrayList;

public class NominalAttribute extends Attribute {

    private ArrayList<String> domain = new ArrayList<String>();

    public NominalAttribute() {
    	super();
    }

    public NominalAttribute( String name ) {
        super(name);
    }

    public void addValue( String value ) {
        domain.add(value);
    }

    public String getValue( int index ) {
    	return domain.get(index);
    }

    public int getIndex( String value ) throws Exception {
        if (!validValue(value)) {
            throw new Exception(value + " is not in " + getName() + "'s domain.");
        }

        return domain.indexOf(value);
    }

    public boolean validValue( String value ) {
        return domain.indexOf(value) != -1;
    }

    public int size() {
        return domain.size();
    }
    
    public boolean equal(Attribute attribute) {
    	if (this.size() != attribute.size() || !this.getName().equals(attribute.getName())) {
    		return false;
    	}
    	
    	for (int i = 0; i < this.size(); i++) {
    		if (! this.getValue(i).equals(((NominalAttribute) attribute).getValue(i))) {
    			return false;
    		}
    	}
    	
    	return true;
    }

    public String toString() {
        StringBuilder str = new StringBuilder(super.toString());
        
        for (String s : domain) {
        	str.append(' ' + s);
        }
        
        return str.toString() + '\n';
    }

}
