import java.util.Scanner;

public class AttributeFactory extends Object {

	public static Attribute make( Scanner scanner ) throws Exception {
		scanner.next(); 					// "@attribute"
		String name = scanner.next(); 		// name of the attribute
		String content = scanner.next();	// the first word after the name
		
		if (content.equals("numeric")) {
			NumericAttribute attribute = new NumericAttribute(name);
			
			return attribute;
		}
		else {
			NominalAttribute attribute = new NominalAttribute(name);
			
			attribute.addValue(content);
			
			while (scanner.hasNext()) {
				attribute.addValue(scanner.next());
			}
			
			return attribute;
		}
    }

}
