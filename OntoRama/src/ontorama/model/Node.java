/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Sep 16, 2002
 * Time: 12:08:52 PM
 */
package ontorama.model;

import java.util.List;
import java.net.URI;

public interface Node {
    public String getName();
    public void setName(String name);

    public String getIdentifier();
    public void setIdentifier(String identifier);

    public boolean hasClones();
    public List getClones();
    public void addClone (Node node);
    public Node makeClone();
    public void addClones (List clones);

    public int getDepth();
    public void setDepth(int depth);

    public boolean getFoldedState();
    public void setFoldState(boolean isFolded);

    public URI getCreatorUri ();
    public void setCreatorUri(URI creatorUri);

}
