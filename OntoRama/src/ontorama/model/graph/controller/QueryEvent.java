/*
 * Created by IntelliJ IDEA.
 * User: johang
 * Date: 1/08/2002
 * Time: 16:14:47
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.model.graph.controller;


public class QueryEvent extends NodeEvent {
    public QueryEvent(ontorama.model.graph.Node subject) {
        super(subject);
        System.out.println("new QueryEvent()");
    }
}
