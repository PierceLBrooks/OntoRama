package ontorama.ontotools.parser;


import java.util.List;

import ontorama.model.graph.Node;

public class ParserResult {
    List<Node> nodes;
    List edges;

    public ParserResult(List<Node> nodes, List edges) {
        this.nodes = nodes;
        this.edges = edges;
    }

    public List<Node> getNodesList () {
        return this.nodes;
    }

    public List getEdgesList () {
        return this.edges;
    }

}
