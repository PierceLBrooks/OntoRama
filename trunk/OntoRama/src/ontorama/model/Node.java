/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Sep 16, 2002
 * Time: 12:08:52 PM
 * To change template for new interface use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.model;

import ontorama.webkbtools.util.NoSuchPropertyException;

import java.util.Iterator;
import java.util.List;

public interface Node {
    String getName();

    void setName(String name);

    String getFullName();

    void setFullName(String fullName);

    boolean hasClones();

    Iterator getClones();

    int getDepth();

    void setProperty(String propertyName, List propertyValue)
            throws NoSuchPropertyException;

    List getProperty(String propertyName) throws NoSuchPropertyException;
}
