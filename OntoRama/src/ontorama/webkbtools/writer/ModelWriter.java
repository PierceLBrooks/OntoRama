package ontorama.webkbtools.writer;

import ontorama.model.Graph;

import java.io.Writer;

/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 4/10/2002
 * Time: 09:21:30
 * To change this template use Options | File Templates.
 */
public interface ModelWriter {
    public void write(Graph graph, Writer out);
}
