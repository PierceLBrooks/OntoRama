/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 18/09/2002
 * Time: 09:10:02
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.model;

public class EdgeType implements EdgeTypeInterface {

    private String _name;

    public EdgeType(String name) {
        _name = name;
    }

    public String getName() {
        return _name;
    }


}
