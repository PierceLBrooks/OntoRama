/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 8/10/2002
 * Time: 11:33:27
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.webkbtools.writer.rdf;

import com.hp.hpl.mesa.rdf.jena.model.Model;
import com.hp.hpl.mesa.rdf.jena.model.RDFException;
import com.hp.hpl.mesa.rdf.jena.model.Resource;
import com.hp.hpl.mesa.rdf.jena.model.Property;
import com.hp.hpl.mesa.rdf.jena.mem.ModelMem;
import com.hp.hpl.mesa.rdf.jena.common.PropertyImpl;
import com.hp.hpl.mesa.rdf.jena.common.ResourceImpl;
import ontorama.webkbtools.util.NoSuchRelationLinkException;
import ontorama.backends.p2p.model.P2PGraph;
import ontorama.backends.p2p.model.P2PEdge;
import ontorama.backends.p2p.model.P2PNode;
import ontorama.model.Edge;
import ontorama.model.Node;
import ontorama.model.EdgeType;
import ontorama.ontologyConfig.RdfMapping;

import java.util.List;
import java.util.Hashtable;
import java.util.Iterator;

public class RdfP2PWriter extends RdfModelWriter {

    //private P2PGraph _p2pGraph;

    protected Model toRDFModel() throws RDFException, NoSuchRelationLinkException {
        Model rdfModel = new ModelMem();

        if ( !(_graph instanceof P2PGraph) ) {
             /// error
            System.err.println("ERROR: expecting P2P Graph");
            System.exit(-1);
        }
        else {
            //_p2pGraph = (P2PGraph) _graph;
            _graph = (P2PGraph) _graph;
        }

        List nodesList = _graph.getNodesList();
        List edgesList = _graph.getEdgesList();

        writeEdges(edgesList, rdfModel);
        //writeNodes(nodesList, rdfModel);

        return rdfModel;
    }

    protected void writeEdges(List edgesList, Model rdfModel) throws RDFException {
        Iterator edgesIterator = edgesList.iterator();
        while (edgesIterator.hasNext()) {
            P2PEdge curEdge = (P2PEdge) edgesIterator.next();
            System.out.println("processing edge " + curEdge);
            if (! curEdge.getAssertionsList().isEmpty()) {
                System.out.println("need reification for edge (assertion) " + curEdge);
                /// will be doing something like the following here
                    //graph.createStatement(res, RDF.value, "value")
                    //       .addProperty(FOO.occursIn, "http://foo");
                //rdfModel.createStatement()
            }
            if (! curEdge.getRejectionsList().isEmpty()) {
                System.out.println("need reificaton for edge (rejection) " + curEdge);
            }

            SimpleTriple triple = writeEdgeIntoModel(curEdge, rdfModel);
            Resource subject = getResource(triple.getSubject());
            Property predicate = triple.getPredicate();
            if (triple.getObject().getIdentifier().equals(triple.getObject().getName())) {
                /// @todo this is not a good test (testing if node should
                // be resource or literal). this probably should be reflected
                // in a node type object
                rdfModel.add(subject, predicate, curEdge.getToNode().getName());
            }
            else {
                Resource object = getResource(triple.getObject());
                rdfModel.add(subject, predicate, object);
            }
        }
    }

    protected Resource getResource(Node node) {
        P2PNode p2pNode = (P2PNode) node;
        if (! p2pNode.getAssertionsList().isEmpty()) {
            System.out.println("node " + p2pNode.getName() + " has assertions");
        }
        if (! p2pNode.getRejectionsList().isEmpty()) {
            System.out.println("node " + p2pNode.getName() + " has rejections");
        }
        if (_nodeToResource.containsKey(node)) {
            return (Resource) _nodeToResource.get(node);
        } else {
            Resource resource = new ResourceImpl(node.getIdentifier());
            _nodeToResource.put(node, resource);
            return resource;
        }
    }



}
