/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Sep 16, 2002
 * Time: 11:50:51 AM
 * To change template for new interface use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.model;

import ontorama.ontologyConfig.RelationLinkDetails;

public interface Edge {
    NodeImpl getFromNode();

    NodeImpl getToNode();

    RelationLinkDetails getEdgeType();
}
