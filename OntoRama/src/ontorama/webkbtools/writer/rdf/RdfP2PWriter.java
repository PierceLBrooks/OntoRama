/* * Created by IntelliJ IDEA. * User: nataliya * Date: 8/10/2002 * Time: 11:33:27 * To change template for new class use * Code Style | Class Templates options (Tools | IDE Options). */package ontorama.webkbtools.writer.rdf;import java.net.URI;import java.util.Iterator;import java.util.List;import ontorama.backends.p2p.model.P2PEdge;import ontorama.backends.p2p.model.P2PGraph;import ontorama.backends.p2p.model.P2PNode;import ontorama.model.graph.Node;import ontorama.webkbtools.NoSuchRelationLinkException;import com.hp.hpl.mesa.rdf.jena.common.PropertyImpl;import com.hp.hpl.mesa.rdf.jena.common.ResourceImpl;import com.hp.hpl.mesa.rdf.jena.mem.ModelMem;import com.hp.hpl.mesa.rdf.jena.model.Model;import com.hp.hpl.mesa.rdf.jena.model.Property;import com.hp.hpl.mesa.rdf.jena.model.RDFException;import com.hp.hpl.mesa.rdf.jena.model.Resource;import com.hp.hpl.mesa.rdf.jena.model.Statement;public class RdfP2PWriter extends RdfModelWriter {    private String _ontoP2P_namespace = "http://www.kvocentral.com/ontoP2P#";    private Model _rdfModel;    Property _propertyAssert;    Property _propertyReject;    protected Model toRDFModel() throws RDFException, NoSuchRelationLinkException {        _rdfModel = new ModelMem();        _propertyAssert = new PropertyImpl(_ontoP2P_namespace, "asserted");        _propertyReject = new PropertyImpl(_ontoP2P_namespace, "rejected");        if ( !(_graph instanceof P2PGraph) ) {             /// error            System.err.println("ERROR: expecting P2P Graph");            System.exit(-1);        }        else {            _graph = (P2PGraph) _graph;        }        List nodesList = _graph.getNodesList();        List edgesList = _graph.getEdgesList();        writeEdges(edgesList, _rdfModel);        writeNodes(nodesList, _rdfModel);        return _rdfModel;    }    protected void writeEdges(List edgesList, Model rdfModel) throws RDFException {        Iterator edgesIterator = edgesList.iterator();        _propertyAssert = rdfModel.createProperty(_ontoP2P_namespace, "asserted");        _propertyReject = rdfModel.createProperty(_ontoP2P_namespace, "rejected");        while (edgesIterator.hasNext()) {            Object obj = edgesIterator.next();            System.out.println("obj = " + obj + ", class = " + obj.getClass());            P2PEdge curEdge = (P2PEdge) obj;            //P2PEdge curEdge = (P2PEdge) edgesIterator.next();            SimpleTriple triple = writeEdgeIntoModel(curEdge, rdfModel);            Resource subject = getResource(triple.getSubject());            Property predicate = triple.getPredicate();            if ( (! curEdge.getAssertionsList().isEmpty()) ||  (! curEdge.getRejectionsList().isEmpty()) ) {                Resource object = getResource(triple.getObject());                Statement reificationStatement = rdfModel.createStatement(subject, predicate, object);                Iterator assertionsIt = curEdge.getAssertionsList().iterator();                while (assertionsIt.hasNext()) {                    URI asserter = (URI) assertionsIt.next();                    rdfModel.add(reificationStatement, _propertyAssert, asserter.toString());                }                Iterator rejectionsIt = curEdge.getRejectionsList().iterator();                while (rejectionsIt.hasNext()) {                    URI rejector = (URI) rejectionsIt.next();                    rdfModel.add(reificationStatement, _propertyReject, rejector.toString());                }            }            else {                if (triple.getObject().getIdentifier().equals(triple.getObject().getName())) {                    /// @todo this is not a good test (testing if node should                    // be resource or literal). this probably should be reflected                    // in a node type object                    rdfModel.add(subject, predicate, curEdge.getToNode().getName());                }                else {                    Resource object = getResource(triple.getObject());                    rdfModel.add(subject, predicate, object);                }            }        }    }    private void writeNodes(List nodesList, Model rdfModel) throws RDFException {        Iterator it = nodesList.iterator();        while (it.hasNext()) {            ontorama.model.graph.Node node = (ontorama.model.graph.Node) it.next();            Resource resource = getResource(node);            P2PNode p2pNode = (P2PNode) node;            Iterator assertions = p2pNode.getAssertionsList().iterator();            while (assertions.hasNext()) {                URI asserter = (URI) assertions.next();                _rdfModel.add(resource, _propertyAssert, asserter.toString());            }            Iterator rejections = p2pNode.getRejectionsList().iterator();            while (rejections.hasNext()) {                URI rejector = (URI) rejections.next();                _rdfModel.add(resource, _propertyReject, rejector.toString());            }        }    }    protected Resource getResource(ontorama.model.graph.Node node) {        P2PNode p2pNode = (P2PNode) node;        if (_nodeToResource.containsKey(p2pNode)) {            return (Resource) _nodeToResource.get(node);        } else {            Resource resource = new ResourceImpl(node.getIdentifier());            _nodeToResource.put(node, resource);            return resource;        }    }}