package ontorama.backends.examplesmanager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ontorama.OntoramaConfig;
import ontorama.conf.ConfigParserException;
import ontorama.conf.DataFormatMapping;
import ontorama.conf.XmlParserAbstract;
import ontorama.util.Debug;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * Title:
 * Description: Parse config file (xml) that contains examples details
 *              and populate instatiate OntoramaExample objects according
 *              to details found in config file. Build examples list that
 *              can be used by other objects and find 'main example', so
 *              applications knows what example to load first.
 * Copyright:    Copyright (c) DSTC 2001
 * Company: DSTC
 * @author  nataliya
 * @version 1.0
 */

public class XmlExamplesConfigParser extends XmlParserAbstract {

    private LinkedList examplesList;
    private OntoramaExample mainExample;


    /**
     *
     */
    private Debug debug = new Debug(false);


    public XmlExamplesConfigParser(InputStream in) throws ConfigParserException, IOException {
        if (OntoramaConfig.VERBOSE) {
            System.out.println("XmlExamplesConfigParser");
        }
        this.examplesList = new LinkedList();

        try {
            SAXBuilder builder = new SAXBuilder();

            Document doc = builder.build(in);

            Element rootEl = doc.getRootElement();
            List exampleElementsList = rootEl.getChildren("example");
            Iterator exampleElementsIterator = exampleElementsList.iterator();
            while (exampleElementsIterator.hasNext()) {
                Element curEl = (Element) exampleElementsIterator.next();
                //System.out.println("processing example element: " + curEl);
                processExampleElement(curEl);
            }

        } catch (JDOMException e) {
            System.out.println("JDOMException: " + e);
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     *
     */
    private void processExampleElement(Element element) throws ConfigParserException {
        Attribute nameAttr = element.getAttribute("name");
        checkCompulsoryAttr(nameAttr, "name", "element");
        //System.out.println("processing example element with name: " + nameAttr);
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

        OntoramaExample example = new OntoramaExample(nameAttr.getValue(), rootAttr.getValue(),
                uriAttr.getValue(), sourcePackagePathSuffixAttr.getValue(),
                dataFormatMapping);
        if ((loadOnStartAttr != null) && (loadOnStartAttr.getValue().equals("yes"))) {
            this.mainExample = example;
            example.setLoadFirst(true);
        }

        this.examplesList.add(example);

        Element displayMenuElement = element.getChild("displayMenu");
        //System.out.println("element name = " + nameAttr.getValue() + ", displayMenuElement = " + displayMenuElement);
        if (displayMenuElement != null) {
            Attribute subfolderNameAttribute = displayMenuElement.getAttribute("subfolder");
            if (subfolderNameAttribute != null) {
                example.setMenuSubfolderName(subfolderNameAttribute.getValue());
            }
        }

    }

    /**
     *
     */
    public List getExamplesList() {
        return this.examplesList;
    }

    /**
     *
     */
    public OntoramaExample getMainExample() {
        //System.out.println("returning example = " + this.mainExample);
        return this.mainExample;
    }

}
