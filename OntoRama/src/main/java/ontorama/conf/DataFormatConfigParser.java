package ontorama.conf;

import java.io.InputStream;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

import ontorama.ontotools.parser.Parser;
import ontorama.ontotools.writer.ModelWriter;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class DataFormatConfigParser extends XmlParserAbstract {
	
	private static final String _nameAttrName = "name";
	private static final String _extensionAttrName = "extension";
	private static final String _parserAttrName = "parser";
	private static final String _writerAttrName = "writer";
	
	private final List<DataFormatMapping> _mappings;
	
	@SuppressWarnings("unchecked")
	public DataFormatConfigParser (InputStream in) throws ConfigParserException {
		_mappings = new ArrayList<DataFormatMapping>();
		try {
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(in);
			Element root = doc.getRootElement();
			List<Element> children = root.getChildren();
			Iterator<Element> it = children.iterator();
			while (it.hasNext()) {
				Element element = it.next();
				DataFormatMapping dataFormatMapping = processElement(element);
				_mappings.add(dataFormatMapping);
			}
		}
		catch (JDOMException e) {
			throw new ConfigParserException("Error parsing DataFormatMapping configuration: ",e);
		}	
	}
	
	public List<DataFormatMapping> getDataFormatMappings () {
		return _mappings;
	}
	
	private DataFormatMapping processElement (Element element) throws ConfigParserException {
		Attribute nameAttr = element.getAttribute(_nameAttrName);
		checkCompulsoryAttr(nameAttr, _nameAttrName, element.getName());
		
		Attribute extensionAttr = element.getAttribute(_extensionAttrName);
		checkCompulsoryAttr(extensionAttr, _extensionAttrName, element.getName());
		
		Attribute parserAttr = element.getAttribute(_parserAttrName);
		checkCompulsoryAttr(parserAttr, _parserAttrName, element.getName());

        Parser parser;
		try {
			parser = (Parser) Class.forName(parserAttr.getValue()).newInstance();
		} catch (Exception e) {
			throw new ConfigParserException("Failed to initialise parser class named '" + parserAttr.getValue() +
					"'", e);
		}
		DataFormatMapping dataFormatMapping = new DataFormatMapping(nameAttr.getValue(), extensionAttr.getValue(), parser);
		
		Attribute writerAttr = element.getAttribute(_writerAttrName);
		if (writerAttr != null) {
			String writerAttrValue = writerAttr.getValue();
			if ( (!writerAttrValue.equals("null")) && (writerAttrValue.length() != 0) ) {
			    ModelWriter writer;
                try {
                    writer = (ModelWriter) Class.forName(writerAttrValue).newInstance();
                } catch (Exception e) {
                    throw new ConfigParserException("Can not create model writer class '" + writerAttrValue + "'", e);
                }
                dataFormatMapping.setWriter(writer);
			}
		}
		return dataFormatMapping;
	}
}
