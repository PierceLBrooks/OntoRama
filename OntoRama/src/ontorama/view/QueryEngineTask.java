package ontorama.view;

/** Uses a SwingWorker to perform a time-consuming (and utterly fake) task. */

import ontorama.model.Graph;
import ontorama.model.NoTypeFoundInResultSetException;
import ontorama.webkbtools.query.Query;
import ontorama.webkbtools.query.QueryEngine;
import ontorama.webkbtools.query.QueryResult;
import ontorama.webkbtools.util.NoSuchRelationLinkException;
import ontorama.webkbtools.util.ParserException;

import java.io.IOException;


public class QueryEngineTask {
    private int _lengthOfTask;
    private int _current;
    private String _statMessage;
    private Graph _graph;
    private Query _query;
    private boolean _isDone;

    SwingWorker _worker;

    /**
     *
     */
    public QueryEngineTask(Query query) {
        //Compute length of task...
        //In a real program, this would figure out
        //the number of bytes to read or whatever.
        _query = query;
        init();
    }

    /**
     *
     */
    private void init() {
        _statMessage = "";
        _graph = null;
        _lengthOfTask = 100;
        _current = 0;
        _isDone = false;
    }

    /**
     * Called from ProgressBarDemo to start the task.
     */
    void go() {
        _worker = new SwingWorker() {
            public Object construct() {
                return new QueryEngineRunner();
            }
        };
        _worker.start();
    }

    /**
     * Called from ProgressBarDemo to find out how much work needs
     * to be done.
     */
    int getLengthOfTask() {
        return _lengthOfTask;
    }

    /**
     * Called from ProgressBarDemo to find out how much has been done.
     */
    int getCurrent() {
        return _current;
    }

    /**
     *
     */
    void stop() {
        // _current = _lengthOfTask;
        System.out.println("--- thread: stop()");
        _isDone = true;
        _graph = null;
        _worker.interrupt();
    }

    /**
     * Called from ProgressBarDemo to find out if the task has completed.
     */
    boolean done() {
        if (_isDone) {
            return true;
        }
        return false;
    }

    /**
     *
     */
    String getMessage() {
        return _statMessage;
    }

    /**
     *
     */
    private void printStatus() {
        System.out.println("..." + _statMessage + ", _graph = " + _graph);
    }

    /**
     *
     */
    public Graph getGraph() {
        return _graph;
    }

    /**
     *
     */
    class QueryEngineRunner {
        public QueryEngineRunner() {
            try {
                _statMessage = "getting ontology data from the source";
                printStatus();
                _current = 30;
                QueryEngine queryEngine = new QueryEngine(_query);

                _statMessage = "ontology data is read, building data structures";
                _current = 60;
                printStatus();
                QueryResult queryResult = queryEngine.getQueryResult();

                _statMessage = "building graph";
                _current = 90;
                printStatus();
                _graph = new Graph(queryResult);

                _statMessage = "graph is built";
                _current = 98;
                //_finished = true;

                printStatus();

                _isDone = true;
                System.out.println("DONE! ");

                _current = 100;
                //System.out.println(graph.printXml());
            } catch (NoTypeFoundInResultSetException noTypeExc) {
                System.err.println(noTypeExc);
                OntoRamaApp.showErrorDialog(noTypeExc.getMessage());
            } catch (NoSuchRelationLinkException noRelExc) {
                System.err.println(noRelExc);
                OntoRamaApp.showErrorDialog(noRelExc.getMessage());
            } catch (IOException ioExc) {
                System.out.println(ioExc);
                ioExc.printStackTrace();
                OntoRamaApp.showErrorDialog(ioExc.getMessage());
            } catch (ParserException parserExc) {
                System.out.println(parserExc);
                parserExc.printStackTrace();
                OntoRamaApp.showErrorDialog(parserExc.getMessage());
            } catch (ClassNotFoundException classExc) {
                System.out.println(classExc);
                classExc.printStackTrace();
                OntoRamaApp.showErrorDialog("Sorry, couldn't find one of the classes you specified in config.xml");
            } catch (IllegalAccessException iae) {
                System.out.println(iae);
                iae.printStackTrace();
                OntoRamaApp.showErrorDialog(iae.getMessage());
            } catch (InstantiationException instExc) {
                System.out.println(instExc);
                instExc.printStackTrace();
                OntoRamaApp.showErrorDialog(instExc.getMessage());
            } catch (Exception e) {
                System.err.println();
                e.printStackTrace();
                OntoRamaApp.showErrorDialog("Unable to build graph: " + e.getMessage());
            }
        }

    }
}
