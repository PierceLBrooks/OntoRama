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

import java.util.List;

public interface Node {
    public String getName();

    public void setName(String name);

    public String getFullName();

    public void setFullName(String fullName);

    public boolean hasClones();

    public List getClones();

    public void addClone (Node node);

    public Node makeClone() throws NoSuchPropertyException;

    public void addClones (List clones);

    public int getDepth();
    public void setDepth(int depth);

    public boolean getFoldedState();

    public void setFoldState(boolean isFolded);

}
