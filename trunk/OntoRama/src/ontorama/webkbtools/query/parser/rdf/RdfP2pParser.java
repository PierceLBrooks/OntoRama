package ontorama.webkbtools.query.parser.rdf;

import com.hp.hpl.jena.rdf.query.QueryResults;
import com.hp.hpl.jena.rdf.query.ResultBinding;
import com.hp.hpl.mesa.rdf.jena.common.PropertyImpl;
import com.hp.hpl.mesa.rdf.jena.mem.ModelMem;
import com.hp.hpl.mesa.rdf.jena.model.*;

import ontorama.OntoramaConfig;
import ontorama.backends.p2p.model.*;
import ontorama.model.*;
import ontorama.ontologyConfig.RdfMapping;
import ontorama.webkbtools.query.parser.Parser;
import ontorama.webkbtools.query.parser.ParserResult;
import ontorama.webkbtools.util.NoSuchRelationLinkException;
import ontorama.webkbtools.util.ParserException;

import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.AccessControlException;
import java.util.*;

/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 27/09/2002
 * Time: 14:44:55
 * To change this template use Options | File Templates.
 */
public class RdfP2pParser implements Parser {
    /**
     * Hashtable to hold all Graph Nodes that we have created
     * keys - strings - graph node names
     * values - graph nodes
     */
    protected Hashtable _nodesHash;
    protected List _edgesList;

    private static final String _namespace_ontoP2P_suffix = "ontoP2P#";
    private static final String _namespace_rdf_suffix = "rdf-syntax-ns#";

    private static final String _rdf_tag_asserted = "asserted";
    private static final String _rdf_tag_rejected = "rejected";

    private Property _assertedProp;
    private Property _rejectedProp;
    private Property _rdfValueProp;

    private List _processedAnonymousNodes;

    /**
     * Constructor
     */
    public RdfP2pParser() {
        _nodesHash = new Hashtable();
        _edgesList = new LinkedList();
        _processedAnonymousNodes = new LinkedList();
    }

    public ParserResult getResult(Reader reader) throws ParserException, AccessControlException {
        try {
            Model model = new ModelMem();
            model.read(reader, "");

            NsIterator nsIterator = model.listNameSpaces();
            while (nsIterator.hasNext()) {
                String namespace =  nsIterator.next();
                if (namespace.endsWith(_namespace_ontoP2P_suffix)) {
                    String namespace_ontoP2P = namespace;
                    _assertedProp = new PropertyImpl(namespace_ontoP2P + _rdf_tag_asserted);
                    _rejectedProp = new PropertyImpl(namespace_ontoP2P + _rdf_tag_rejected);
                }
                if (namespace.endsWith(_namespace_rdf_suffix)) {
                    String namespace_rdf = namespace;
                    _rdfValueProp = new PropertyImpl((namespace_rdf + "value"));
                }
            }

            System.out.println("--------------testing 1---------------------");
            StmtIterator testIt = model.listReifiedStatements();
            while (testIt.hasNext()) {
                Statement next = testIt.next();
                System.out.println("reified statement: " + next);
            }

            System.out.println("--------------testing 2---------------------");
            testIt = model.listStatements();
            while (testIt.hasNext()) {
                Statement next = testIt.next();
                System.out.println("statement is reified: " + next.isReified());
            }

            ResIterator subjectsIt = model.listSubjects();
            while (subjectsIt.hasNext()) {
                System.out.println("-----------------------------------------------");
                Resource resource = subjectsIt.next();
                System.out.println("subject: " + resource + ", isAnon = " + resource.isAnon());
                StmtIterator it = resource.listProperties();
                while (it.hasNext()) {
                    Statement st = it.next();
                    System.out.println("---" + st);
                    System.out.println("statement is reified = " + st.isReified());
                    if (st.getPredicate().toString().endsWith("rdf-syntax-ns#type")) {
                        /// @todo hack: should find a better way to deal with this.
                        continue;
                    }
                    if (resource.isAnon()) {
                        System.out.println("SUBJECT IS ANONYMOUS");
                        if (_processedAnonymousNodes.contains(resource)) {
                            System.out.println("this resource is already processed");
                            continue;
                        }
                        processAnonymousSubject(st, model);
                        _processedAnonymousNodes.add(resource);
                    }
                    else {
                        System.out.println("RESOURCE IS NOT ANONYMOUS");
                        processStatement(st);
                    }
                }
            }
        } catch (AccessControlException secExc) {
            throw secExc;
        } catch (RDFException e) {
            e.printStackTrace();
            throw new ParserException("Error in parsing RDF: " + e.getMessage());
        } catch (RDFError err) {
            err.printStackTrace();
            throw new ParserException("Couldn't parse returned RDF data. Parser error: " + err.getMessage());
        }
        catch (NoSuchRelationLinkException relExc) {
            relExc.printStackTrace();
            throw new ParserException("Unrecognized EdgeType: " + relExc.getMessage());
        }
        catch (URISyntaxException uriExc) {
            uriExc.printStackTrace();
            throw new ParserException("Found invalid Asserter or Rejector URI in source :" + uriExc.getMessage());
        }
        ParserResult result = new ParserResult(new LinkedList(_nodesHash.values()), _edgesList);
        return result;
    }

    protected void processStatement (Statement st) throws NoSuchRelationLinkException, URISyntaxException {
        Resource subject = st.getSubject();
        Property predicate = st.getPredicate();
        RDFNode object = st.getObject();

        /// assumption is that we already processed this statement if it contains
        // anonymous object - it would have been processed in processAnonymousSubject()
        // method because we query for this anonymous node there.
        /// @todo looks like a hack ;) perhaps better to 'cross off' statements off a list once they are processed.
        if (_processedAnonymousNodes.contains(object)) {
            System.out.println("this object is already processed as anonymous");
            return;
        }

        String predicateStr = predicate.toString();

        if (predicateStr.endsWith(_rdf_tag_asserted)) {
            P2PNode subjectNode = getNodeForName(subject.toString());
            subjectNode.addAssertion(new URI(object.toString()));
            return;
        }
        if (predicateStr.endsWith(_rdf_tag_rejected)) {
            P2PNode subjectNode = getNodeForName(subject.toString());
            subjectNode.addRejection(new URI(object.toString()));
            return;
        }

        P2PEdge edge = mapEdgeIntoModel(subject.toString(), predicate.toString(), object.toString());
        addEdgeToEdgesList(edge);
    }

    private void processAnonymousSubject(Statement st, Model model) throws NoSuchRelationLinkException,
                                            URISyntaxException {
        RDFNode object = st.getObject();

        // var 'x' is our anonymous node

        String mainNodeName = findNameForAnonymousSubject(object, model);
        com.hp.hpl.jena.rdf.query.Query query;
        QueryResults result;
        List resBindList;
        Iterator resBindIterator;

        System.out.println("anonymous node is mapped to " + mainNodeName);

        String subject1VariableName = "subject1";
        String predicate1VariableName = "predicate1";
        String predicate2VariableName = "predicate2";
        String object2VariableName = "object2";
        String queryStr3 = "SELECT ?x  WHERE \n";
        queryStr3 = queryStr3 + "(? " + subject1VariableName + ", ?" + predicate1VariableName + ", ?x), \n";
        queryStr3 = queryStr3 + "(?x, ?" + predicate2VariableName + ", ?" + object2VariableName + "), \n";
        if (object instanceof Resource) {
            queryStr3 = queryStr3 +  "(?x, ?y, <" + object + ">) ";
        }
        else if (object instanceof Literal) {
            queryStr3 = queryStr3 +  "(?x, ?y, \"" + object + "\")";
        }
        query = new com.hp.hpl.jena.rdf.query.Query(queryStr3);
        result = query.exec(queryStr3, model);
        resBindList = result.getAll();
        resBindIterator = resBindList.iterator();
        System.out.println("***");
        while (resBindIterator.hasNext()) {
            ResultBinding cur = (ResultBinding) resBindIterator.next();
            String subject1 = cur.getValue(subject1VariableName).toString();
            String predicate1 = cur.getValue(predicate1VariableName).toString();
            String predicate2 = cur.getValue(predicate2VariableName).toString();
            String object2 = cur.getValue(object2VariableName).toString();
            mapReifiedStatementIntoModel(subject1, predicate1, mainNodeName, predicate2, object2);
        }
        System.out.println("***");
    }

    private String findNameForAnonymousSubject(RDFNode object, Model model) {
        String mainNodeName = null;
        String queryStr1 = "SELECT ?x WHERE \n";
        queryStr1 = queryStr1 + " (?x, <" + _rdfValueProp + ">, ?z), \n";
        if (object instanceof Resource) {
            queryStr1 = queryStr1 +  "(?x, ?y, <" + object + ">) ";
        }
        else if (object instanceof Literal) {
            queryStr1 = queryStr1 +  "(?x, ?y, \"" + object + "\")";
        }
        com.hp.hpl.jena.rdf.query.Query query = new com.hp.hpl.jena.rdf.query.Query(queryStr1);
        QueryResults result = query.exec(queryStr1, model);
        List resBindList = result.getAll();
        Iterator resBindIterator = resBindList.iterator();
        while (resBindIterator.hasNext()) {
            ResultBinding cur = (ResultBinding) resBindIterator.next();
            mainNodeName = cur.getValue("z").toString();
        }
        return mainNodeName;
    }


    /**
     * first statement is reified statement, second statement is a statement ABOUT the first statement.
     * @param subject1
     * @param predicate1
     * @param object1
     * @param predicate2
     * @param object2
     * @throws NoSuchRelationLinkException
     */
    private void mapReifiedStatementIntoModel(String subject1, String predicate1, String object1,
                                            String predicate2, String object2)
                                            throws NoSuchRelationLinkException, URISyntaxException {
        System.out.println(subject1 + ", -> " + predicate1 + " -> " + object1);
        System.out.println(object1 + ", -> " + predicate2 + " -> " + object2);

        P2PEdge edge = mapEdgeIntoModel(subject1, predicate1, object1);

        if (predicate2.toString().endsWith("value")) {
            // we already processed this one above in findNameForAnonymousSubject() method
            return;
        }
        if (predicate2.endsWith(_rdf_tag_asserted)) {
            edge.addAssertion(new URI(object2));
        }
        if (predicate2.endsWith(_rdf_tag_rejected)) {
            edge.addRejection(new URI(object2));
        }
        addEdgeToEdgesList(edge);
    }

    private P2PEdge mapEdgeIntoModel(String subjectStr, String predicateStr, String objectStr) throws NoSuchRelationLinkException {
        EdgeTypeExtended edgeTypeExt = getEdgeTypeForRdfTag(predicateStr);
        EdgeType edgeType = edgeTypeExt.getEdgeType();
        P2PNode node1 = getNodeForName(subjectStr);
        P2PNode node2 = getNodeForName(objectStr);
        P2PEdge edge = null;
        if (!edgeTypeExt.getIsReverse()) {
            edge = findEdgeInEdgesList(subjectStr, objectStr, edgeType);
            if (edge == null) {
                edge = new P2PEdgeImpl(node1, node2, edgeType);
            }
        }
        else {
            edge = findEdgeInEdgesList(objectStr, subjectStr, edgeType);
            if (edge == null ) {
                edge = new P2PEdgeImpl(node2, node1, edgeType);
            }
        }
        return edge;
    }

    private void addEdgeToEdgesList (P2PEdge edge ) {
        P2PEdge foundEdge = findEdgeInEdgesList(edge.getFromNode().getName(), edge.getToNode().getName(), edge.getEdgeType());
        if (foundEdge == null ) {
            _edgesList.add(edge);
        }
    }


    private P2PNode getNodeForName (String nodeName) {
        if (_nodesHash.containsKey(nodeName)) {
            return (P2PNode) _nodesHash.get(nodeName);
        }
        else {
            P2PNode node = new P2PNodeImpl(nodeName, nodeName);
            _nodesHash.put(nodeName, node);
            return node;
        }
    }

    private EdgeTypeExtended getEdgeTypeForRdfTag (String predicateStr) throws NoSuchRelationLinkException {
        List ontologyRelationRdfMapping = OntoramaConfig.getRelationRdfMapping();

        Iterator ontologyRelationRdfMappingIterator = ontologyRelationRdfMapping.iterator();
        while (ontologyRelationRdfMappingIterator.hasNext()) {
            RdfMapping rdfMapping = (RdfMapping) ontologyRelationRdfMappingIterator.next();
            Iterator mappingTagsIterator = rdfMapping.getRdfTags().iterator();
            while (mappingTagsIterator.hasNext()) {
                String mappingTag = (String) mappingTagsIterator.next();
                if (predicateStr.endsWith(mappingTag)) {
                    String mappingType = rdfMapping.getType();
                    EdgeType edgeType = OntoramaConfig.getEdgeType(mappingType);
                        if (mappingType.equals(edgeType.getName())) {
                            return new EdgeTypeExtended(edgeType, false);
                        } else if (mappingType.equals(edgeType.getReverseEdgeName())) {
                            return new EdgeTypeExtended(edgeType, true);
                        } else {
                            // ERROR
                            // throw exception here
                            System.out.println("Dont' know about property '" + predicateStr + "'");
                            System.exit(-1);
                        }
                }
            }
        }
        return null;
    }

    private P2PEdge findEdgeInEdgesList(String fromNodeName, String toNodeName, EdgeType edgeType) {
        Iterator it = _edgesList.iterator();
        while (it.hasNext()) {
            P2PEdge cur = (P2PEdge) it.next();
            if (cur.getEdgeType().equals(edgeType)) {
                Node fromNode = cur.getFromNode();
                Node toNode = cur.getToNode();
                if (fromNode.getName().equals(fromNodeName)) {
                    if (toNode.getName().equals(toNodeName)) {
                        return cur;
                    }
                }
            }
        }
        return null;
    }

    private class EdgeTypeExtended {
        private EdgeType type;
        private boolean isReverse = false;

        public EdgeTypeExtended (EdgeType type, boolean isReverse) {
            this.type = type;
            this.isReverse = isReverse;
        }

        public EdgeType getEdgeType () {
            return this.type;
        }

        public boolean getIsReverse() {
            return this.isReverse;
        }
    }



}
