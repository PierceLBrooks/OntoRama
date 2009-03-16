/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 18/09/2002
 * Time: 09:10:02
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.model.graph;


public class EdgeTypeImpl implements EdgeType {

    private String _name;
    private String _reverseEdgeName;
    private String _namespace;

    public EdgeTypeImpl(String name) {
        _name = name;
    }

    public String getName() {
        return _name;
    }

    public String getReverseEdgeName() {
        return _reverseEdgeName;
    }

    public void setReverseEdgeName(String reverseEdgeName) {
        _reverseEdgeName = reverseEdgeName;
    }

    public void setNamespace (String namespace) {
        _namespace = namespace;
    }

    public String getNamespace() {
        return _namespace;
    }

    public String toString() {
        String str = "EdgeType: ";
        str = str + " name = " + _name + ", reversedEdgeName = " + _reverseEdgeName;
        return str;
    }



}
