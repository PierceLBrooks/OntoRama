package ontorama.ontotools.parser.cgkb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import ontorama.OntoramaConfig;
import ontorama.backends.Backend;
import ontorama.model.graph.Edge;
import ontorama.model.graph.EdgeType;
import ontorama.model.graph.Node;
import ontorama.ontotools.NoSuchRelationLinkException;
import ontorama.ontotools.ParserException;
import ontorama.ontotools.parser.Parser;
import ontorama.ontotools.parser.ParserResult;

public class CgKbCsvParser implements Parser {
	
	private Backend _backend = OntoramaConfig.getBackend();

    private Map<String, Node> _nodes;
    private List<Edge> _edges;

    public ParserResult getResult(Reader reader) throws ParserException, AccessControlException {
        _nodes = new Hashtable<String, Node>();
        _edges = new ArrayList<Edge>();
        BufferedReader br = new BufferedReader(reader);
        try {
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = new String[3];
                int count = 0;
                char quoteChar = '"';
                String quoteStr = new Character(quoteChar).toString();
                System.out.println(line);
                StringTokenizer st = new StringTokenizer(line, quoteStr);
                while (st.hasMoreTokens()) {
                    String tok = st.nextToken();
                    tok = tok.trim();
                    if (count == 0) {
                        tokens[0] = tok;
                    }
                    if (count == 2) {
                        tokens[1] = tok;
                    }
                    if (count == 4) {
                        tokens[2] = tok;
                    }
                    count++;
                }
                processLineTokens(tokens);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new ParserException(e.getMessage());
        }
        return ( new ParserResult(new ArrayList<Node>(_nodes.values()), _edges));
    }


    private void processLineTokens(String[] tokens) throws ParserException {
        String obj1 = tokens[0];
        String rel = tokens[1];
        String obj2 = tokens[2];
        String shortNameObj1 = obj1;
        String shortNameObj2 = obj2;

        try {
            Iterator<EdgeType> edgeTypesIterator = OntoramaConfig.getEdgeTypesSet().iterator();
            ontorama.model.graph.Node fromNode = getNodeForName(shortNameObj1,  obj1);
            ontorama.model.graph.Node toNode = getNodeForName(shortNameObj2, obj2);
            ontorama.model.graph.Edge edge = null;
            while (edgeTypesIterator.hasNext()) {
                ontorama.model.graph.EdgeType edgeType = edgeTypesIterator.next();
                if (rel.equals(edgeType.getName())) {
                    edge = _backend.createEdge(fromNode, toNode, edgeType);
                } else if (rel.equals(edgeType.getReverseEdgeName())) {
                    edge = _backend.createEdge(fromNode, toNode, edgeType);
                }
            }
            if (edge == null) {
                throw new ParserException ("Attribute name '" + rel + "' describes unknown Relation Link. Check config.xml for declared Relation Links");
            }
            if (!_edges.contains(edge)) {
                 _edges.add(edge);
            }
        }
        catch (NoSuchRelationLinkException e) {
        	throw new ParserException("Could not process line tokens", e);
        }
    }

    private Node getNodeForName (String shortName, String nodeIdentifier) {
        Node node = _nodes.get(shortName);
        if (node == null) {
        	node = _backend.createNode(shortName, nodeIdentifier);
            _nodes.put(shortName, node);
            node.setIdentifier(nodeIdentifier);
        }
        return node;
    }

}
