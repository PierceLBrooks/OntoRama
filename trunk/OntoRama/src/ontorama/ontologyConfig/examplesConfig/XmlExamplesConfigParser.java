package ontorama.ontologyConfig.examplesConfig;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.*;
import java.util.List;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;

import org.jdom.*;
import org.jdom.input.*;
//import org.jdom.output.*;

import ontorama.util.Debug;
import ontorama.ontologyConfig.ConfigParserException;
import ontorama.ontologyConfig.XmlParserAbstract;

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

public class XmlExamplesConfigParser extends XmlParserAbstract{

    private LinkedList examplesList;
    private OntoramaExample mainExample;


    /**
     *
     */
     private Debug debug = new Debug(false);


    public XmlExamplesConfigParser(InputStream in) throws ConfigParserException, IOException {
	System.out.println("XmlExamplesConfigParser");
        this.examplesList = new LinkedList();

        try {
            SAXBuilder builder = new SAXBuilder();

            Document doc = builder.build(in);

            Element rootEl = doc.getRootElement();
            List exampleElementsList = rootEl.getChildren("example");
            Iterator exampleElementsIterator = exampleElementsList.iterator();
            while (exampleElementsIterator.hasNext()) {
              Element curEl = (Element) exampleElementsIterator.next();
              processExampleElement(curEl);
            }

        }
        catch (JDOMException e) {
            System.out.println("JDOMException: " + e);
            System.exit(-1);
        }
    }

    /**
     *
     */
    private void processExampleElement (Element element) throws ConfigParserException {
      Attribute nameAttr = element.getAttribute("name");
      checkCompulsoryAttr(nameAttr, "name", "element");
      Attribute rootAttr = element.getAttribute("root");
      checkCompulsoryAttr(rootAttr, "root", "element");
      Attribute loadOnStartAttr = element.getAttribute("loadOnStart");

      Element sourceElement = element.getChild("source");
      if (sourceElement == null) {
        throw new ConfigParserException("Missing compulsory element 'source' in element 'example'");
      }
      Attribute relativeUriAttr = sourceElement.getAttribute("relativeUri");
      checkCompulsoryAttr(relativeUriAttr,"relativeUri","source");

      Element queryOutputFormatElement = element.getChild("queryOutputFormat");
      if (queryOutputFormatElement == null) {
        throw new ConfigParserException("Missing compulsory element 'queryOutputFormat' in element 'example'");
      }

      Element parserPackagePathSuffixElement = element.getChild("parserPackagePathSuffix");
      if (parserPackagePathSuffixElement == null) {
        throw new ConfigParserException("Missing compulsory element 'parserPackagePathSuffixElement' in element 'example'");
      }

      OntoramaExample example = new OntoramaExample(nameAttr.getValue(), rootAttr.getValue(),
                              relativeUriAttr.getValue(), queryOutputFormatElement.getText(),
                              parserPackagePathSuffixElement.getText());
      if ( (loadOnStartAttr != null) && (loadOnStartAttr.getValue().equals("yes")) ) {
        this.mainExample = example;
        example.setLoadFirst(true);
      }
      this.examplesList.add(example);

//      OntoramaExample example = new OntoramaExample(nameAttr.getValue(), rootAttr.getValue(),
//                              relativeUriAttr.getValue(), queryOutputFormatElement.getText(),
//                              parserPackagePathSuffixElement.getText());
//      this.examplesList.add(example);
//      if ( (loadOnStartAttr != null) && (loadOnStartAttr.getValue().equals("yes")) ) {
//        this.mainExample = example;
//      }

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
    public List getExamplesList () {
      return this.examplesList;
    }

    /**
     *
     */
    public OntoramaExample getMainExample() {
      return this.mainExample;
    }

}
