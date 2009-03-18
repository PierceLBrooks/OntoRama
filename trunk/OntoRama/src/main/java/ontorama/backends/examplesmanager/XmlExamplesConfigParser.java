package ontorama.backends.examplesmanager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import ontorama.OntoramaConfig;
import ontorama.conf.ConfigParserException;
import ontorama.conf.DataFormatMapping;
import ontorama.conf.XmlParserAbstract;
import ontorama.ontotools.source.Source;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * Parse config file (xml) that contains examples details
 *              and populate instatiate OntoramaExample objects according
 *              to details found in config file. Build examples list that
 *              can be used by other objects and find 'main example', so
 *              applications knows what example to load first.
 * Copyright:    Copyright (c) DSTC 2001
 * Company: DSTC
 */

public class XmlExamplesConfigParser extends XmlParserAbstract {

    private List<OntoramaExample> examplesList;
    private OntoramaExample mainExample;

    @SuppressWarnings("unchecked")
	public XmlExamplesConfigParser(InputStream in) throws ConfigParserException, IOException {
        this.examplesList = new ArrayList<OntoramaExample>();

        try {
            SAXBuilder builder = new SAXBuilder();

            Document doc = builder.build(in);

            Element rootEl = doc.getRootElement();
            List<Element> exampleElementsList = rootEl.getChildren("example");
            for (Element curEl : exampleElementsList) {
                processExampleElement(curEl);
            }

        } catch (JDOMException e) {
        	throw new ConfigParserException("Failed to parse XML input", e);
        }
    }

    private void processExampleElement(Element element) throws ConfigParserException {
        Attribute nameAttr = element.getAttribute("name");
        checkCompulsoryAttr(nameAttr, "name", "element");
        Attribute rootAttr = element.getAttribute("root");
        checkCompulsoryAttr(rootAttr, "root", "element");
        Attribute loadOnStartAttr = element.getAttribute("loadOnStart");
        
        Attribute dataFormatMappingAttr = element.getAttribute("dataFormatMapping");
        checkCompulsoryAttr(dataFormatMappingAttr, "dataFormatMapping", "example");
        DataFormatMapping dataFormatMapping = OntoramaConfig.getDataFormatMapping(dataFormatMappingAttr.getValue());
        if (dataFormatMapping == null) {
        	throw new ConfigParserException("Couldn't find data format mapping " + dataFormatMappingAttr.getValue() +
        									". It should be defined in corresponding DataFormatMapping config XML file");
        }

        Element sourceElement = element.getChild("source");
        if (sourceElement == null) {
            throw new ConfigParserException("Missing compulsory element 'source' in element 'example'");
        }
        Attribute uriAttr = sourceElement.getAttribute("uri");
        checkCompulsoryAttr(uriAttr, "uri", "source");

        Attribute sourcePackagePathSuffixAttr = sourceElement.getAttribute("sourcePackagePathSuffix");
        checkCompulsoryAttr(sourcePackagePathSuffixAttr, "sourcePackagePathSuffix", "source");

        Source dataSource;
		try {
			dataSource = (Source) Class.forName(sourcePackagePathSuffixAttr.getValue()).newInstance();
		} catch (Exception e) {
			throw new ConfigParserException("Failed to initialise data source class named '" + sourcePackagePathSuffixAttr.getValue() +
					"'", e);
		}
        OntoramaExample example = new OntoramaExample(nameAttr.getValue(), rootAttr.getValue(),
                uriAttr.getValue(), dataSource, dataFormatMapping);
        if ((loadOnStartAttr != null) && (loadOnStartAttr.getValue().equals("yes"))) {
            this.mainExample = example;
            example.setLoadFirst(true);
        }

        this.examplesList.add(example);

        Element displayMenuElement = element.getChild("displayMenu");
        if (displayMenuElement != null) {
            Attribute subfolderNameAttribute = displayMenuElement.getAttribute("subfolder");
            if (subfolderNameAttribute != null) {
                example.setMenuSubfolderName(subfolderNameAttribute.getValue());
            }
        }

    }

    public List<OntoramaExample> getExamplesList() {
        return this.examplesList;
    }

    public OntoramaExample getMainExample() {
        return this.mainExample;
    }

}
