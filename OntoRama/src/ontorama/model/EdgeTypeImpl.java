/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 18/09/2002
 * Time: 09:10:02
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.model;

public class EdgeTypeImpl implements EdgeType {

    private String _name;
    private String _reverseEdgeName;

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



}
