package ontorama.view;

import java.awt.event.*;

import java.io.IOException;

import ontorama.model.Graph;
import ontorama.model.NoTypeFoundInResultSetException;


import ontorama.webkbtools.query.Query;
import ontorama.webkbtools.query.QueryEngine;
import ontorama.webkbtools.query.QueryResult;

import ontorama.webkbtools.util.*;


/**
 * <p>Title: </p>
 * <p>Description: Using given query get a graph for this query on a
 * separate thread.
 * </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 * @author nataliya
 * @version 1.0
 */

public class QueryEngineThread extends Thread {

  /**
   *
   */
  private Query _query;

  /**
   *
   */
  private Graph _graph;

  /**
   * status message
   */
  private String _status = "";

  /**
   * level of progress.
   * may be used by progress bar
   */
  private int _current = 0;

  /**
   * flag to indicate if task is finished
   */
  private boolean _finished = false;

  /**
   * Constructor.
   */
  public QueryEngineThread (Query query ) {
    super();
    _query = query;
    initAll();
  }

  /**
   * initialise all vars
   */
  private void initAll () {
    _current = 0;
    _finished = false;
    _graph = null;
    printStatus();
  }

  /**
   * Overriting Thread run() method. This method will do all the work.
   *
   * @todo not sure of mechanism for interruting thread is correct.
   * Problem: once the thread is interrupted, it propagates to QueryEngine, etc.
   * However, these processes don't stop because they are designed by java to
   * stop gracefully (finish whatever they are doing). In our case we don't want
   * that - we want to stop a long process at once. To achieve this: we modified
   * Source interface and WebKB2Source code to throw CacelledQueryException.
   * Source checks if it's thread has been interrupted and if it was - it throws
   * CancelledQueryException. This insures that all processes are stopped immediately.
   * Not sure if this is a hack, because it's a bit unintuative: this class
   * interrupts the thread and at the same time gets an exception that has been
   * caused by the interruption. Also, this approach is very dependant on Source
   * implementation: if Source is performing a few tasks in order to do its job,
   * each task would have to check if thread is interrupted and throw
   * the exception. this is relying on the developer too much.
   */
  public void run() {
      initAll();
      try {
          _status = "getting ontology data from the source";
          //printStatus();
          _current = 30;
          QueryEngine queryEngine = new QueryEngine (_query);

          _status = "ontology data is read, building data structures";
          _current = 60;
          //printStatus();
          QueryResult queryResult = queryEngine.getQueryResult();

          _status = "building graph";
          _current = 90;
          //printStatus();
          _graph = new Graph(queryResult);

          _status = "graph is built";
      }
      catch (NoTypeFoundInResultSetException noTypeExc) {
          System.err.println(noTypeExc);
          OntoRamaApp.showErrorDialog(noTypeExc.getMessage());
      }
      catch (NoSuchRelationLinkException noRelExc) {
          System.err.println(noRelExc);
          OntoRamaApp.showErrorDialog(noRelExc.getMessage());
      }
      catch (IOException ioExc) {
        System.out.println(ioExc);
        ioExc.printStackTrace();
        OntoRamaApp.showErrorDialog(ioExc.getMessage());
      }
      catch (ParserException parserExc) {
        System.out.println(parserExc);
        parserExc.printStackTrace();
        OntoRamaApp.showErrorDialog(parserExc.getMessage());
      }
      catch (ClassNotFoundException classExc) {
        System.out.println(classExc);
        classExc.printStackTrace();
        OntoRamaApp.showErrorDialog("Sorry, couldn't find one of the classes you specified in config.xml");
      }
      catch (IllegalAccessException iae) {
        System.out.println(iae);
        iae.printStackTrace();
        OntoRamaApp.showErrorDialog(iae.getMessage());
      }
      catch (InstantiationException instExc) {
        System.out.println(instExc);
        instExc.printStackTrace();
        OntoRamaApp.showErrorDialog(instExc.getMessage());
      }
      catch (NoSuchTypeInQueryResult noSuchTypeExc) {
        System.err.println(noSuchTypeExc);
        noSuchTypeExc.printStackTrace();
        OntoRamaApp.showErrorDialog(noSuchTypeExc.getMessage());
      }
      catch (SourceException sourceExc) {
        System.err.println(sourceExc);
        sourceExc.printStackTrace();
        OntoRamaApp.showErrorDialog(sourceExc.getMessage());
      }
      catch (NoSuchPropertyException noSuchPropExc) {
        System.err.println(noSuchPropExc);
        noSuchPropExc.printStackTrace();
        OntoRamaApp.showErrorDialog(noSuchPropExc.getMessage());
      }
      catch (CancelledQueryException cancelledExc) {
        if (! isInterrupted()) {
          System.err.println("got CancelledQueryException when didn't try to interrupt the query!");
          cancelledExc.printStackTrace();
          OntoRamaApp.showErrorDialog("Unexpected exception: query was cancelled");
        }
      }
      catch (Exception e) {
          System.err.println();
          e.printStackTrace();
          OntoRamaApp.showErrorDialog("Unable to build graph, unexpected error.");
      }
      _finished = true;
      _current = 98;
      printStatus();
      System.out.println("DONE! " );
      //System.out.println(graph.printXml());
  }

  /**
   * get current status message
   */
  public String getMessage () {
    return _status;
  }

  /**
   * get current progress level
   */
  public int getCurrent() {
    return _current;
  }

  /**
   * get graph that has been built
   */
   public Graph getGraph() {
    return _graph;
   }

   /**
    * return flag indicating if task has been finished
    */
   public boolean done () {
    return _finished;
   }
   
   /**
    * return flag indicating if task has been stopped
    */
  public boolean isStopped () {
  	return isInterrupted();
  }

   /**
    * stop this thread
    */
   public void stopProcess () {
    interrupt();
    _graph = null;
    //_finished = true;
    //System.out.println("\n\nafter interrupt: isInterrupted()  = " + isInterrupted() );
   }

   /**
    * pring current status (debugging method)
    */
   private void printStatus () {
    System.out.println("..." + _status + ", _finised = " + _finished + ", _graph = " + _graph);
   }
}