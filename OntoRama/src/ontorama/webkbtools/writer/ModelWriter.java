package ontorama.ontotools.writer;

import java.io.Writer;

import ontorama.model.graph.Graph;

/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 4/10/2002
 * Time: 09:21:30
 * To change this template use Options | File Templates.
 */
public interface ModelWriter {
    public void write(ontorama.model.graph.Graph graph, Writer out) throws ModelWriterException;
}
