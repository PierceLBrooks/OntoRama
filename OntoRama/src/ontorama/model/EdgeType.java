/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 18/09/2002
 * Time: 09:09:47
 * To change template for new interface use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.model;

public interface EdgeType {
    public String getName();
    public String getReverseEdgeName();
    public void setReverseEdgeName(String reverseEdgeName);
    public String getNamespace();
    public void setNamespace (String namespace);
}
