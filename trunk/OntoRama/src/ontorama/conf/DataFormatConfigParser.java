package ontorama.conf;

import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ontorama.ui.ErrorPopupMessage;
import ontorama.ui.OntoRamaApp;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * @author nataliya
 */
public class DataFormatConfigParser extends XmlParserAbstract {
	
	private static final String _nameAttrName = "name";
	private static final String _extensionAttrName = "extension";
	private static final String _parserAttrName = "parser";
	private static final String _writerAttrName = "writer";
	
	private List _mappings;
	
	public DataFormatConfigParser (InputStream in) throws ConfigParserException {
		_mappings = new LinkedList();
		try {
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(in);
			Element root = doc.getRootElement();
			List children = root.getChildren();
			Iterator it = children.iterator();
			while (it.hasNext()) {
				Element element = (Element) it.next();
				DataFormatMapping dataFormatMapping = processElement(element);
				//System.out.println("DataFormatConfigParser:: mapping = " + dataFormatMapping);
				_mappings.add(dataFormatMapping);
			}
		}
		catch (JDOMException e) {
			String message = "Error parsing DataFormatMapping configuration: " + e.getMessage();
			new ErrorPopupMessage(message, OntoRamaApp.getMainFrame());
			System.err.println(message);
			e.printStackTrace();
		}	
	}
	
	public List getDataFormatMappings () {
		return _mappings;
	}
	
	private DataFormatMapping processElement (Element element) throws ConfigParserException {
		Attribute nameAttr = element.getAttribute(_nameAttrName);
		checkCompulsoryAttr(nameAttr, _nameAttrName, element.getName());
		
		Attribute extensionAttr = element.getAttribute(_extensionAttrName);
		checkCompulsoryAttr(extensionAttr, _extensionAttrName, element.getName());
		
		Attribute parserAttr = element.getAttribute(_parserAttrName);
		checkCompulsoryAttr(parserAttr, _parserAttrName, element.getName());

		DataFormatMapping dataFormatMapping = new DataFormatMapping(nameAttr.getValue(), extensionAttr.getValue(), parserAttr.getValue());
		
		Attribute writerAttr = element.getAttribute(_writerAttrName);
		if (writerAttr != null) {
			String writerAttrValue = writerAttr.getValue();
			if ( (!writerAttrValue.equals("null")) && (writerAttrValue.length() != 0) ) {
				dataFormatMapping.setWriterName(writerAttrValue);
			}
		}
		return dataFormatMapping;
	}
}
