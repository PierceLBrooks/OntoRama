/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 22/08/2002
 * Time: 12:32:55
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.webkbtools.query.parser.cgkb;

import ontorama.webkbtools.query.parser.Parser;
import ontorama.webkbtools.query.parser.ParserResult;
import ontorama.webkbtools.query.Query;
import ontorama.webkbtools.util.ParserException;
import ontorama.webkbtools.util.NoSuchRelationLinkException;
import ontorama.webkbtools.inputsource.*;
import ontorama.ontologyConfig.RelationLinkDetails;
import ontorama.OntoramaConfig;
import ontorama.model.GraphNode;
import ontorama.model.EdgeImpl;
import ontorama.model.Edge;

import java.util.*;
import java.io.*;
import java.security.AccessControlException;

public class CgKbCsvParser implements Parser {

    private Hashtable _nodes;
    private List _edges;

    /**
     * @param reader
     * @return
     * @throws ParserException
     * @throws AccessControlException
     */
    public ParserResult getResult(Reader reader) throws ParserException, AccessControlException {
        _nodes = new Hashtable();
        _edges = new LinkedList();
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
                    //tok = tok.replaceAll(quoteStr, new String());
                    //System.out.println("count = " + count + ", tok = ." + tok + ".");
                    if (count == 0) {
                        tokens[0] = tok;
                    }
                    if (count == 2) {
                        tokens[1] = tok;
                    }
                    if (count == 4) {
                        tokens[2] = tok;
                    }
                    //tokens[count] = tok;
                    count++;
                }
                processLineTokens(tokens);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new ParserException(e.getMessage());
        }
        return ( new ParserResult(new LinkedList(_nodes.values()), _edges));
    }


    private void processLineTokens(String[] tokens) throws ParserException {
        String obj1 = tokens[0];
        String rel = tokens[1];
        String obj2 = tokens[2];
        String shortNameObj1 = obj1;
        String shortNameObj2 = obj2;

        try {
            RelationLinkDetails[] relationLinksConfigArray = OntoramaConfig.getRelationLinkDetails();
            GraphNode fromNode = getNodeForName(shortNameObj1,  obj1);
            GraphNode toNode = getNodeForName(shortNameObj2, obj2);
            Edge edge = null;
            for (int i = 0; i < relationLinksConfigArray.length; i++) {
                if (relationLinksConfigArray[i] == null) {
                    continue;
                }
                RelationLinkDetails relationLinkDetails = relationLinksConfigArray[i];
                if (rel.equals(relationLinkDetails.getLinkName())) {
                    edge = new EdgeImpl(fromNode, toNode, relationLinkDetails);
                } else if (rel.equals(relationLinkDetails.getReversedLinkName())) {
                    edge = new EdgeImpl(fromNode, toNode, relationLinkDetails);
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
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private GraphNode getNodeForName (String shortName, String fullName) {
        GraphNode node = (GraphNode) _nodes.get(shortName);
        if (node == null) {
            node = new GraphNode(shortName);
            _nodes.put(shortName, node);
            node.setFullName(fullName);
        }
        return node;
    }

    private String stripFullName (String fullName) {
        String result = "";

        String suffix = null;
        String prefix = null;
        int ind1 = fullName.indexOf("<");
        int ind2 = fullName.indexOf("(");
        if (ind1 != -1) {
            suffix = fullName.substring(ind1, fullName.length());
            prefix = fullName.substring(0, ind1 - 1);
        }
        else if (ind2 != -1) {
            suffix = fullName.substring(ind2, fullName.length());
            prefix = fullName.substring(0, ind2 - 1);
        }
        else {
            prefix = fullName;
        }

        if (suffix != null) {
            if (prefix.endsWith(".")) {
                prefix = prefix.substring(0,prefix.length());
                suffix = "." + suffix;
            }
        }

        int ind = prefix.lastIndexOf(".");
        if (ind == -1) {
            return fullName;
        }
        result = prefix.substring(ind, prefix.length());

        if (suffix != null) {
            result = result + suffix;
        }
        System.out.println("fullName = " + fullName + ", shortName = " + result);
        return result;
    }

    public static void main(String[] args) {
        try {
            Source source = new JarSource();
            SourceResult sr = source.getSourceResult("examples/cgkb/test.cgkb", new Query("KVO"));
            Reader r = sr.getReader();

            Parser parser = new CgKbCsvParser();
            parser.getResult(r);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

    }

}
