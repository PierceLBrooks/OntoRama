package ontorama.util.validate.schema;

import org.jdom.input.SAXBuilder;
import org.jdom.Document;
import org.jdom.JDOMException;

public class Validator {

	public Validator (String schemaLoc, String instanceLoc) {
		System.out.println("shema file: " + schemaLoc);
		System.out.println("instance file: " + instanceLoc);

		try {

			SAXBuilder builder = new SAXBuilder("org.apache.xerces.parsers.SAXParser", true);
			builder.setFeature("http://apache.org/xml/features/validation/schema", true);
			builder.setProperty( 
				"http://apache.org/xml/properties/schema/external-schemaLocation",
                    		schemaLoc);
			Document doc = builder.build(instanceLoc);
		}
		catch (JDOMException jdomExc) {
			System.err.println("Error: " + jdomExc.getMessage());
			System.exit(-1);
		}
		
	}

	public static void main (String[] args) {
		String usage = "Validator schemaFileLocation InstanceFileLocation";
		if (args.length != 2) {
			System.err.println("Usage:\n" + usage);
			System.exit(-1);
		}
		new Validator(args[0], args[1]);

	}

}
