package ontorama.webkbtools.query.parser.rdf;

import ontorama.webkbtools.query.parser.Parser;
import ontorama.webkbtools.query.parser.ParserResult;
import ontorama.webkbtools.util.ParserException;
import ontorama.webkbtools.util.NoSuchRelationLinkException;
import ontorama.model.EdgeType;
import ontorama.OntoramaConfig;
import ontorama.ontologyConfig.RdfMapping;

import java.util.*;
import java.io.Reader;
import java.security.AccessControlException;

import com.hp.hpl.mesa.rdf.jena.model.*;
import com.hp.hpl.mesa.rdf.jena.model.Property;
import com.hp.hpl.mesa.rdf.jena.mem.ModelMem;
import com.hp.hpl.mesa.rdf.jena.common.SelectorImpl;
import com.hp.hpl.mesa.rdf.jena.common.PropertyImpl;
import com.hp.hpl.jena.rdf.query.QueryResults;
import com.hp.hpl.jena.rdf.query.ResultBinding;
import com.hp.hpl.jena.rdf.query.Value;

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

            ResIterator subjectsIt = model.listSubjects();
            while (subjectsIt.hasNext()) {
                System.out.println("-----------------------------------------------");
                Resource res = subjectsIt.next();
                System.out.println("subject: " + res + ", isAnon = " + res.isAnon());
                StmtIterator it = res.listProperties();
                while (it.hasNext()) {
                    Statement st = it.next();
                    System.out.println("---" + st);
                    if (res.isAnon()) {
                        if (_processedAnonymousNodes.contains(res)) {
                            System.out.println("this anon. node is already processed");
                            continue;
                        }
                        processAnonymousNode(st, model);
                        _processedAnonymousNodes.add(res);
                    }
                    else {
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
        ParserResult result = new ParserResult(new LinkedList(_nodesHash.values()), _edgesList);
        return result;
    }

    private void processStatement (Statement st) {
        Resource subject = st.getSubject();
        Property predicate = st.getPredicate();
        RDFNode object = st.getObject();

    }

    private void processAnonymousNode(Statement st, Model model) throws NoSuchRelationLinkException {
        RDFNode object = st.getObject();

        // var 'x' is our anonymous node

        String mainNodeName = findNameForAnonymousNode(object, model);
        com.hp.hpl.jena.rdf.query.Query query;
        QueryResults result;
        List resBindList;
        Iterator resBindIterator;

        System.out.println("RESULT: ");
        System.out.println("Node = " + mainNodeName);

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
        while (resBindIterator.hasNext()) {
            ResultBinding cur = (ResultBinding) resBindIterator.next();
//            System.out.println("***");
            System.out.println(cur.getValue(subject1VariableName) + " -> " + cur.getValue(predicate1VariableName) + " -> " + cur.getValue("x"));
            System.out.println(cur.getValue("x") + " -> " + cur.getValue(predicate2VariableName) + " -> " + cur.getValue(object2VariableName));
            mapTripleIntoGraphModel(cur.getValue(subject1VariableName).toString(),cur.getValue(predicate1VariableName).toString(),cur.getValue("x").toString());
            mapTripleIntoGraphModel(cur.getValue("x").toString(), cur.getValue(predicate2VariableName).toString(), cur.getValue(object2VariableName).toString());
        }
    }

    private void mapTripleIntoGraphModel(String subjectStr, String predicateStr, String objectStr) throws NoSuchRelationLinkException {
        if (predicateStr.toString().endsWith("value")) {
            // we already processed this one above in findNameForAnonymousNode() method
            return;
        }
        if (predicateStr.endsWith(_rdf_tag_asserted)) {
            //
            return;
        }
        if (predicateStr.endsWith(_rdf_tag_rejected)) {
            //
            return;
        }
        EdgeTypeExtended edgeTypeExt = getEdgeTypeForRdfTag(predicateStr);
        EdgeType edgeType = edgeTypeExt.getEdgeType();
        if (!edgeTypeExt.getIsReverse()) {

        }
        else {

        }
    }

    private String findNameForAnonymousNode(RDFNode object, Model model) {
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
