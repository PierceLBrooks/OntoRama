package ontorama.ontotools.parser.rdf;

import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.AccessControlException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ontorama.OntoramaConfig;
import ontorama.backends.p2p.P2PGlobals;
import ontorama.backends.p2p.model.P2PEdge;
import ontorama.backends.p2p.model.P2PNode;
import ontorama.conf.RdfMapping;
import ontorama.model.graph.EdgeType;
import ontorama.ontotools.NoSuchRelationLinkException;
import ontorama.ontotools.ParserException;
import ontorama.ontotools.parser.Parser;
import ontorama.ontotools.parser.ParserResult;

import com.hp.hpl.jena.rdf.query.QueryResults;
import com.hp.hpl.jena.rdf.query.ResultBinding;
import com.hp.hpl.mesa.rdf.jena.common.PropertyImpl;
import com.hp.hpl.mesa.rdf.jena.common.ResourceImpl;
import com.hp.hpl.mesa.rdf.jena.mem.ModelMem;
import com.hp.hpl.mesa.rdf.jena.model.Literal;
import com.hp.hpl.mesa.rdf.jena.model.Model;
import com.hp.hpl.mesa.rdf.jena.model.NsIterator;
import com.hp.hpl.mesa.rdf.jena.model.Property;
import com.hp.hpl.mesa.rdf.jena.model.RDFError;
import com.hp.hpl.mesa.rdf.jena.model.RDFException;
import com.hp.hpl.mesa.rdf.jena.model.RDFNode;
import com.hp.hpl.mesa.rdf.jena.model.ResIterator;
import com.hp.hpl.mesa.rdf.jena.model.Resource;
import com.hp.hpl.mesa.rdf.jena.model.Statement;
import com.hp.hpl.mesa.rdf.jena.model.StmtIterator;

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

    private static final String _namespace_rdf_suffix = "rdf-syntax-ns#";

    private static final String _rdf_tag_asserted = "asserted";
    private static final String _rdf_tag_rejected = "rejected";

    private Property _assertedProp;
    private Property _rejectedProp;
    private Property _rdfValueProp;
    private Property _rdfTypeProp;
    private Property _rdfSubjectProp;
    private Property _rdfPredicateProp;
    private Property _rdfObjectProp;
    private RDFNode _rdfStatementObject;

    private List _statementsList;

    /**
     * Constructor
     */
    public RdfP2pParser() {
		init();
        _statementsList = new LinkedList();
    }
    
    private void init() {
        _nodesHash = new Hashtable();
        _edgesList = new LinkedList();
    }

    public ParserResult getResult(Reader reader) throws ParserException, AccessControlException {
    	init();
        try {
            Model model = new ModelMem();
            model.read(reader, "");

            StmtIterator stIt= model.listStatements();
            while (stIt.hasNext()) {
                Statement next = stIt.next();
                _statementsList.add(next);
            }

        	String namespace_ontoP2P = P2PGlobals.ontoP2P_namespace;
        	_assertedProp = new PropertyImpl(namespace_ontoP2P + _rdf_tag_asserted);
        	_rejectedProp = new PropertyImpl(namespace_ontoP2P + _rdf_tag_rejected);

            NsIterator nsIterator = model.listNameSpaces();
            while (nsIterator.hasNext()) {
                String namespace =  nsIterator.next();
                if (namespace.endsWith(_namespace_rdf_suffix)) {
                    String namespace_rdf = namespace;
                    _rdfValueProp = new PropertyImpl((namespace_rdf + "value"));
                    _rdfTypeProp = new PropertyImpl((namespace_rdf + "type"));
                    _rdfSubjectProp = new PropertyImpl(namespace_rdf + "subject");
                    _rdfPredicateProp = new PropertyImpl(namespace_rdf + "predicate");
                    _rdfObjectProp = new PropertyImpl(namespace_rdf + "object");
                    _rdfStatementObject = new ResourceImpl(namespace_rdf + "Statement");
                }
            }
            processAllAnonymousSubjectStatements(model);
            processAllReificationStatements(model);
            processRemainingStatements(model);
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

    /**
     * first process all anonymous subjects and cross corresponding
     * statements off the list.
     *
     * @param model
     * @throws RDFException
     * @throws NoSuchRelationLinkException
     * @throws URISyntaxException
     */
    private void processAllAnonymousSubjectStatements(Model model) throws RDFException, NoSuchRelationLinkException, URISyntaxException {
        ResIterator subjectsIt = model.listSubjects();
        while (subjectsIt.hasNext()) {
            Resource resource = subjectsIt.next();
            StmtIterator it = resource.listProperties();
            while (it.hasNext()) {
                Statement st = it.next();
                if (!_statementsList.contains(st)) {
                    /// we are checking because statement may already have been processed
                    /// via processAnonymousSubject method.
                    //System.out.println("this statement is already processed");
                    continue;
                }
                if (resource.isAnon()) {
                    processAnonymousSubject(st, model);
                }
            }
        }
    }

    private void processAllReificationStatements(Model model) throws RDFException,
                                    NoSuchRelationLinkException, URISyntaxException {
        ResIterator subjectsIt = model.listSubjectsWithProperty(_rdfTypeProp);
        while (subjectsIt.hasNext()) {
            Resource resource = subjectsIt.next();
            SimpleTriple triple = null;
            boolean resourceIsReified = false;
            StmtIterator it = resource.listProperties();
            while (it.hasNext()) {
                Statement st = it.next();
                if (!_statementsList.contains(st)) {
                    continue;
                }
                if (st.getObject().toString().endsWith("Class"))  {
                    /// @todo hack: should find a better way to deal with this.
                    _statementsList.remove(st);
                    continue;
                }
                if (st.getObject().equals(_rdfStatementObject)) {
                    triple = new SimpleTriple();
                    resourceIsReified = true;
                    _statementsList.remove(st);
                }
                if (! resourceIsReified) {
                    continue;
                    //break;
                }
                if (st.getPredicate().equals(_rdfSubjectProp)) {
                    triple.setSubject(st.getObject());
                }
                if (st.getPredicate().equals(_rdfPredicateProp)) {
                    triple.setPredicate(st.getObject());
                }
                if (st.getPredicate().equals(_rdfObjectProp)) {
                    triple.setObject(st.getObject());
                }
                if (st.getPredicate().equals(_assertedProp)) {
                    triple.addAssertion(new URI(st.getObject().toString()));
                }
                if (st.getPredicate().equals(_rejectedProp)) {
                    triple.addRejection(new URI(st.getObject().toString()));
                }
                _statementsList.remove(st);
            }
            if (resourceIsReified) {
                mapSimpleTripleIntoModel(triple);
            }
        }
    }



    private void processRemainingStatements(Model model) throws RDFException, NoSuchRelationLinkException, URISyntaxException {
        ResIterator subjectsIt = model.listSubjects();
        while (subjectsIt.hasNext()) {
            Resource resource = subjectsIt.next();
            StmtIterator it = resource.listProperties();
            while (it.hasNext()) {
                Statement st = it.next();
                if (!_statementsList.contains(st)) {
                    continue;
                }
                processStatement(st);
            }
        }
    }

    protected void processStatement (Statement st) throws NoSuchRelationLinkException, URISyntaxException {
        Resource subject = st.getSubject();
        Property predicate = st.getPredicate();
        RDFNode object = st.getObject();
        
        
        //System.out.println(subject + " -> " + predicate + " -> " + object);
        

        String predicateStr = predicate.toString().trim();
        String subjectStr = subject.toString().trim();
        String objectStr = object.toString().trim();

        _statementsList.remove(st);

        if (predicateStr.endsWith(_rdf_tag_asserted)) {
            P2PNode subjectNode = getNodeForName(subjectStr);
            subjectNode.addAssertion(new URI(objectStr));
            return;
        }
        if (predicateStr.endsWith(_rdf_tag_rejected)) {
            P2PNode subjectNode = getNodeForName(subjectStr);
            subjectNode.addRejection(new URI(objectStr));
            return;
        }
		if (predicate.getLocalName().equalsIgnoreCase("comment")) {
			P2PNode subjectNode = getNodeForName(subjectStr);
			subjectNode.setDescription(objectStr);
        	return;
		}

        P2PEdge edge = mapEdgeIntoModel(subjectStr, predicateStr, objectStr);
        edge.getEdgeType().setNamespace(predicate.getNameSpace());
        addEdgeToEdgesList(edge);

    }

    private void mapSimpleTripleIntoModel(SimpleTriple triple) throws NoSuchRelationLinkException {
        P2PNode fromNode = getNodeForName(triple.getSubject().toString());
        P2PNode toNode = getNodeForName(triple.getObject().toString());
        P2PEdge edge = mapEdgeIntoModel(triple.getSubject().toString(), triple.getPredicate().toString(), triple.getObject().toString());
        Iterator assertions = triple.getAssertions().iterator();
        while (assertions.hasNext()) {
            URI next = (URI) assertions.next();
            edge.addAssertion(next);
        }
        Iterator rejections = triple.getRejections().iterator();
        while (rejections.hasNext()) {
            URI next = (URI) rejections.next();
            edge.addRejection(next);
        }
        addEdgeToEdgesList(edge);
    }

    private void processAnonymousSubject(Statement st, Model model) throws NoSuchRelationLinkException,
                                            URISyntaxException {
        RDFNode object = st.getObject();
        Resource anonymousResource = st.getSubject();

        // var 'x' is our anonymous node

        String mainNodeName = findNameForAnonymousSubject(object, model);
        com.hp.hpl.jena.rdf.query.Query query;
        QueryResults result;
        List resBindList;
        Iterator resBindIterator;

        String s1 = "subject1";
        String p1 = "predicate1";
        String p2 = "predicate2";
        String o3 = "object2";
        String queryStr3 = "SELECT ?x  WHERE \n";
        queryStr3 = queryStr3 + "(? " + s1 + ", ?" + p1 + ", ?x), \n";
        queryStr3 = queryStr3 + "(?x, ?" + p2 + ", ?" + o3 + "), \n";
        if (object instanceof Resource) {
            queryStr3 = queryStr3 +  "(?x, ?y, <" + object + ">) ";
        }
        else if (object instanceof Literal) {
            queryStr3 = queryStr3 +  "(?x, ?y, \"" + object + "\")";
        }
        query = new com.hp.hpl.jena.rdf.query.Query(queryStr3);
        result =  com.hp.hpl.jena.rdf.query.Query.exec(queryStr3, model);
        resBindList = result.getAll();
        resBindIterator = resBindList.iterator();
        while (resBindIterator.hasNext()) {
            ResultBinding cur = (ResultBinding) resBindIterator.next();
            String subject1 = cur.getValue(s1).toString();
            String predicate1 = cur.getValue(p1).toString();
            String predicate2 = cur.getValue(p2).toString();
            String object2 = cur.getValue(o3).toString();
            mapReifiedStatementIntoModel(subject1, predicate1, mainNodeName, predicate2, object2);
            Statement st1 = getStatementFromStatementsList(subject1, predicate1, anonymousResource.toString());
            if (st1 != null) {
                _statementsList.remove(st1);
            }
            Statement st2 = getStatementFromStatementsList(anonymousResource.toString(), predicate2, object2);
            if (st2 != null) {
                _statementsList.remove(st2);
            }
        }
        _statementsList.remove(st);
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
        QueryResults result = com.hp.hpl.jena.rdf.query.Query.exec(queryStr1, model);
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
        //System.out.println(subject1 + ", -> " + predicate1 + " -> " + object1);
        //System.out.println(object1 + ", -> " + predicate2 + " -> " + object2);

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

    private Statement getStatementFromStatementsList (String subject, String predicate, String object) {
        Iterator it = _statementsList.iterator();
        while (it.hasNext()) {
            Statement curStatement = (Statement) it.next();
            if (curStatement.getSubject().toString().equals(subject)) {
                if (curStatement.getPredicate().toString().equals(predicate)) {
                    if (curStatement.getObject().toString().equals(object)) {
                        return curStatement;
                    }
                }
            }
        }
        return null;
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
                edge = (P2PEdge) OntoramaConfig.getBackend().createEdge(node1, node2, edgeType);
            }
        }
        else {
            edge = findEdgeInEdgesList(objectStr, subjectStr, edgeType);
            if (edge == null ) {
                edge = (P2PEdge) OntoramaConfig.getBackend().createEdge(node2, node1, edgeType);
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
            P2PNode node = (P2PNode) OntoramaConfig.getBackend().createNode(nodeName, nodeName);
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
                P2PNode fromNode = (P2PNode) cur.getFromNode();
                P2PNode toNode = (P2PNode) cur.getToNode();
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

    private class SimpleTriple {
        private RDFNode subject;
        private RDFNode predicate;
        private RDFNode object;
        private List assertions;
        private List rejections;

        public SimpleTriple () {
            assertions = new LinkedList();
            rejections = new LinkedList();
        }

        public RDFNode getSubject() {
            return subject;
        }

        public void setSubject(RDFNode subject) {
            this.subject = subject;
        }

        public RDFNode getPredicate() {
            return predicate;
        }

        public void setPredicate(RDFNode predicate) {
            this.predicate = predicate;
        }

        public RDFNode getObject() {
            return object;
        }

        public void setObject(RDFNode object) {
            this.object = object;
        }

        public void addAssertion (URI uri) {
            this.assertions.add(uri);
        }

        public void addRejection (URI uri) {
            this.rejections.add(uri);
        }

        public List getAssertions() {
            return assertions;
        }

        public List getRejections() {
            return rejections;
        }
    }



}
