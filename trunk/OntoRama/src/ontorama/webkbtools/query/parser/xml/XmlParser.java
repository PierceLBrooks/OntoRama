
package ontorama.webkbtools.query.parser.xml;

/**
 * Title:        OntoRama
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      DSTC
 * @author
 * @version 1.0
 */


import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;

import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Iterator;
import java.io.Reader;

import ontorama.webkbtools.query.parser.Parser;
import ontorama.webkbtools.datamodel.OntologyType;
import ontorama.webkbtools.datamodel.OntologyTypeImplementation;
import ontorama.OntoramaConfig;
import ontorama.webkbtools.util.NoSuchRelationLinkException;
import ontorama.webkbtools.util.ParserException;




public class XmlParser implements Parser {

  /**
   * Hashtable to hold all OntologyTypes that we are creating
   */
  private Hashtable ontHash;

  public XmlParser () {
    ontHash = new Hashtable();

  }

  public Iterator getOntologyTypeIterator (Reader reader) throws ParserException {
    try {
      SAXBuilder builder = new SAXBuilder();
      // Create the document
      Document doc = builder.build(reader);
      List nodes = doc.getRootElement().getChildren("node");
      Iterator it = nodes.iterator();
      while(it.hasNext()) {
          Element element = (Element) it.next();
          parseElements(element);
      }
      //Node root = (Node)this.nodes.get( doc.getRootElement().getAttributeValue("top") );
      //if( root != null ) {
      //    Collection collection = this.nodes.values();
      //    graph = new Graph( collection, root );
      //}
    } catch (Exception e) {
        System.out.println("Exception: " + e.getMessage());
        e.printStackTrace();
    }
    return ontHash.values().iterator();
  }

    /**
     * Read elements into hashtable
     * @todo    replace int 1 in folowing statements:
     * type.isRelationType(subType, 1) and type.addRelationType(subType, 1)
     * with something more meaninfull.
     */
    private void parseElements (Element element) throws NoSuchRelationLinkException {

        // find node with name
        String typeName = element.getAttributeValue("name");
        OntologyType type = (OntologyTypeImplementation) ontHash.get (typeName);
        // if not found --> create one
        if( type == null ) {
            type = new OntologyTypeImplementation(typeName);
            // add child to hashtable
            ontHash.put(typeName, type );
        }

        List childrenElements = element.getChildren("child");
        Iterator it = childrenElements.iterator();

        while(it.hasNext()) {
            Element childElement = (Element) it.next();
            // find node with name of child
            String subTypeName = childElement.getAttributeValue("name");
            OntologyType subType = (OntologyTypeImplementation) ontHash.get( subTypeName );
            // if not found --> create one
            if( subType == null ) {
                subType = new OntologyTypeImplementation (subTypeName);
                // add child to hashtable
                ontHash.put(subTypeName, subType );
            }
            // add child to outer node
            //if (! type.isRelationType(subType, OntoramaConfig.SUBTYPE)) {
            //  type.addRelationType(subType, OntoramaConfig.SUBTYPE);
            //}
            if (! type.isRelationType(subType, 1)) {
              type.addRelationType(subType, 1);
            }



            //if( !subType.hasParent( type ) ){
            /*
            if ( ! subType.isRelationType(type, OntoramaConfig.SUPERTYPE) ) {
              subType.addRelationType(type,OntoramaConfig.SUPERTYPE);
            }
            */
        }
    }

}