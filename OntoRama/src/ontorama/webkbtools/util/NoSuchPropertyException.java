package ontorama.webkbtools.util;

import java.util.Enumeration;

import ontorama.OntoramaConfig;
import ontorama.ontologyConfig.ConceptPropertiesDetails;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      DSTC
 * @author
 * @version 1.0
 */

public class NoSuchPropertyException extends Exception {

    private String errorMsg = null;

    public NoSuchPropertyException(String propertyName, Enumeration allProperties) {
        errorMsg = "Property " + propertyName + " doesn't exist\n";
        errorMsg = errorMsg + "available Properties: ";
        while (allProperties.hasMoreElements()) {
            ConceptPropertiesDetails cur = (ConceptPropertiesDetails) allProperties.nextElement();
            errorMsg = errorMsg + cur.getName() + ", ";
        }
    }

    public String getMessage() {
        return errorMsg;
    }
}