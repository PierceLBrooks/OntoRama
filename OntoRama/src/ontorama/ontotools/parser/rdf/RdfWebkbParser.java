package ontorama.ontotools.parser.rdf;

import java.io.Reader;
import java.security.AccessControlException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ontorama.OntoramaConfig;
import ontorama.model.graph.Edge;
import ontorama.model.graph.EdgeType;
import ontorama.model.graph.Node;
import ontorama.ontotools.NoSuchRelationLinkException;
import ontorama.ontotools.ParserException;
import ontorama.ontotools.parser.ParserResult;

/**
 * <p>Title: </p>
 * <p>Description:
 * WebkbRdfParser - should behave the same way as RdfDamlParser except for
 * the treatment of relation link 'uri'.
 * Also, nodes are renamed to follow WebKB schema (instead of 'wn#OntoRama' use 'wn#onto_rama').
 * </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 * @author nataliya
 * @version 1.0
 */

public class RdfWebkbParser extends RdfDamlParser {

    /**
     * keys - original node name,
     * values  - new name
     */
    private Hashtable namesMapping = new Hashtable();

    private List _nodesList;
    private List _edgesList;

    /**
     * name of url relation link (special case)
     */
    private String urlLinkName = "url";
    
    private Hashtable namespacesMapping = new Hashtable();


    /**
     * Rewrite Iterator returned by the super class to a new
     * Iterator with NodeImpl names in the different format
     * (Get rid of RDF capitalization).
     *
     * First, we will build a mapping of old names to new names.
     * Then, process every NodeImpl and create a new one with a new
     * name and change all relation links so they include new
     * named graph nodes. (this is the reason for the first step - name
     * conversion is better achieved if there is already a mapping for names).
     *
     */
    public ParserResult getResult (Reader reader) throws ParserException, AccessControlException {
    	initNamespacesHashtable();
        namesMapping = new Hashtable();

        ParserResult pr = super.getResult(reader);

        _nodesList = pr.getNodesList();
        _edgesList = pr.getEdgesList();

        mapNewNames(pr.getNodesList(), pr.getEdgesList());

        rewriteNodeNames();
        return pr;
    }
    
    /**
     * Since WebKB is switched to declaring namespaces, we get wrong term
     * identifiers in some cases. For example, we get type like 'daml-
     * ont#domain' instead of 'daml#domain'. This happens when we parse 
     * RDF and strip URL's. Full URL will be something like 'http://www.daml.
     * org/2000/10/daml-ont#domain' and then we strip everything before last
     * slalsh in order to get term identifier. 
     * We will have to make sure we rewrite prefix in term indentifier to
     * a prefix webkb is using for this namespace.
     * 
     * @todo this is a hack - need to work out some other way. If webkb adds
     * some new namespaces, our approach here may not work.
     * 
     * Namespaces used in webkb:
     * 
     * rdf "http://www.w3.org/1999/02/22-rdf-syntax-ns#"
     * rdfs "http://www.w3.org/TR/1999/PR-rdf-schema-19990303#"
     * dc "http://purl.org/metadata/dublin_core#"
     * daml "http://www.daml.org/2000/10/daml-ont#"
     * webkb  "http://meganesia. int.gu.edu.au/~phmartin/WebKB2/kb/theKB_terms.rdf/"
     * 
     */
    private void initNamespacesHashtable() {
    	namespacesMapping.put("22-rdf-syntax-ns","rdf");
    	namespacesMapping.put("PR-rdf-schema-19990303","rdfs");
    	namespacesMapping.put("rdf-schema","rdfs");
    	namespacesMapping.put("dublin_core","dc");
    	namespacesMapping.put("daml-ont","daml");
    }

    /**
     * write out hashtable mapping old names to new
     * In WebKB case we need to be aware of relation link 'url' -
     * it doesn't need uri to be stripped
     */
    protected void mapNewNames(List nodesList, List edgesList) {

        Iterator nodesIterator = nodesList.iterator();
        while (nodesIterator.hasNext()) {
            Node cur = (Node) nodesIterator.next();
            mapNewName(cur);
        }


        Iterator it = edgesList.iterator();
        while (it.hasNext()) {
            Edge curEdge = (Edge) it.next();
            Node fromNode = curEdge.getFromNode();
            Node toNode = curEdge.getToNode();
            //System.out.println("cur edge = " + curEdge);
            mapNewName(fromNode);

            EdgeType edgeType = curEdge.getEdgeType();
            if (edgeType.getName().equals(urlLinkName)) {
                toNode.setName(toNode.getIdentifier());
                namesMapping.put(toNode, toNode.getIdentifier());
            }
            else {
                mapNewName(toNode);
            }

        }

    }

    /**
     * find a new name for the given graph node.
     * First, check if this name is already in the hashtable, if not - then
     * get a new name and put it into the hashtable.
     */
    protected void mapNewName(Node origNode) {
        String newTypeName = (String) namesMapping.get(origNode.getName());
        if (newTypeName == null) {
            newTypeName = createNewNameForType(origNode);
            //System.out.println("new name for " + origType.getName() + " is " + newTypeName);
            namesMapping.put(origNode.getName(), newTypeName);
        }
    }

    /**
     * rewrite graph node
     * Go through all graph nodes and for each:
    * Create new graph node that mirrors given graph node, the
    * only difference is type naming - all names in the new graph node
    * are in the new format.
     */
    protected void rewriteNodeNames() {

        Iterator edgesIterator = _edgesList.iterator();
        while (edgesIterator.hasNext()) {
            Edge curEdge = (Edge) edgesIterator.next();
            //System.out.println("cur edge = " + curEdge);

            Node fromNode = curEdge.getFromNode();
            if (!nodeNameIsAlreadyChanged(fromNode.getName())) {
                String fromNodeNewName = (String) namesMapping.get(fromNode.getName());
                fromNode.setName(fromNodeNewName);
            }
            Node toNode = curEdge.getToNode();
            if (!nodeNameIsAlreadyChanged(toNode.getName())) {
                String toNodeNewName = (String) namesMapping.get(toNode.getName());
                toNode.setName(toNodeNewName);
            }
            //System.out.println("updated edge = " + curEdge);
        }

        // this is just in case we had some nodes not attached to _graphEdges.
        Iterator nodesIterator = _nodesList.iterator();
        while (nodesIterator.hasNext()) {
            Node curNode = (Node) nodesIterator.next();
            if (!nodeNameIsAlreadyChanged(curNode.getName())) {
                String newNodeName = (String) namesMapping.get(curNode.getName());
                curNode.setName(newNodeName);
            }
        }
    }

    private boolean nodeNameIsAlreadyChanged (String nodeName) {
        if (namesMapping.containsValue(nodeName)) {
            return true;
        }
        return false;
    }

    /**
     *
     * Assumptions:
     * - if there is no hash character in the string, we are assuming
     * that this string doesn't need to be processed and just return it
     * - if string equals 'rdf-schema#Class', it shouldn't be reformatted,
     * just return it.
     */
    protected String createNewNameForType(Node node) {

        String typeName = node.getName();

        if (typeName.equals("rdf-schema#Class")) {
            return typeName;
        }

        List synonyms = new LinkedList();

        String typeNamePreffix = null;
        String typeNameSuffix = null;
        int hashIndex = typeName.indexOf("#");
        if (hashIndex == -1) {
            typeNamePreffix = null;
            typeNameSuffix = typeName;
        } else {
            typeNamePreffix = typeName.substring(0, hashIndex);
            /// do our hack with namespaces
            if (namespacesMapping.get(typeNamePreffix) != null) {
            	String replacementPrefix = (String) namespacesMapping.get(typeNamePreffix);
            	typeNamePreffix = replacementPrefix;
            }
           
            typeNameSuffix = typeName.substring(hashIndex + 1, typeName.length());
        }

        try {
            EdgeType edgeType = OntoramaConfig.getEdgeType("synonym");
            Iterator it = _edgesList.iterator();
            while (it.hasNext()) {
                Edge edge = (Edge) it.next();
                if (edge.getEdgeType().equals(edgeType)) {
                    if (edge.getFromNode().equals(node)) {
                        synonyms.add(edge.getToNode().getName());
                    }
                }
            }
        } catch (NoSuchRelationLinkException e) {
            e.printStackTrace();
            System.out.println("edgeType 'synonym' doesn't exist. Check config.xml file ");
            System.out.println("NoSuchRelationLinkException: " + e);
            System.exit(-1);
        }

        if ( (synonyms != null) && (!synonyms.isEmpty())) {
            typeNameSuffix = (String) synonyms.iterator().next();
        } else {
            typeNameSuffix = reformatString(typeNameSuffix);
        }
        String res = typeNamePreffix + "#" + typeNameSuffix;
        if (typeNamePreffix == null) {
            res = typeNameSuffix;
        }
        return res;
    }


    /**
     * rewrite string that has java-style names, for
     * example: typeName, into string of words separated
     * by undescores, for example: type_name
     *
     */
    protected String reformatString(String in) {
        String res = "";

        if (in.length() == -1) {
            return in;
        }

        if (checkIfAllCharsAreCapitalized(in)) {
            return in;
        }

        for (int i = 0; i < in.length(); i++) {
            char ch = in.charAt(i);
            Character chObj = new Character(ch);
            if (Character.isUpperCase(ch)) {
                if (i == 0) {
                    // need this so we don't end up with something like
                    // "_true_cat" instead of "true_cat"
                    res = res + (chObj.toString()).toLowerCase();
                } else {
                    res = res + "_" + (chObj.toString()).toLowerCase();
                }
            } else {
                res = res + chObj.toString();
            }
        }
        return res;
    }

    /**
     * check if all chars in given string are capitalized.
     * need this check to assolate cases like "ADSL", etc.
     */
    protected boolean checkIfAllCharsAreCapitalized(String str) {
        boolean allCapitals = true;
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            Character chObj = new Character(ch);
            if (Character.isLowerCase(ch)) {
                allCapitals = false;
            }
        }
        return allCapitals;
    }


}