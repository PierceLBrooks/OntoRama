package ontorama.ontologyConfig;

import org.jdom.Attribute;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) DSTC 2002</p>
 * <p>Company: DSTc</p>
 * @version 1.0
 */

public class XmlParserAbstract {

    /**
     *
     */
    public void checkCompulsoryAttr(Attribute attr, String attrName, String elementName)
                                                                throws ConfigParserException {
        if (attr == null) {
            throw new ConfigParserException("Missing compulsory Attribute '" + attrName + "' in Element '" + elementName + "'");
        }
        if (attr.getValue().trim().equals("")) {
            throw new ConfigParserException("Attribute '" + attrName + "' in Element '" + elementName + "' can't be empty");
        }
    }

}