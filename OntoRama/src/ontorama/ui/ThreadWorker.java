package ontorama.ui;

/**
 * @author nataliya
 * Created on 4/04/2003
 *
 */
public abstract class ThreadWorker extends Thread {

	/**
	 * status message
	 */
	String _status = "";

	/**
	 * flag to indicate if task is finished
	 */
	boolean _finished;

	boolean _interrupted;

	/**
	 * initialise all vars
	 */
	protected void initAll() {
		_finished = false;
		_interrupted = false;
	}
    
	/**
	 * get current status message
	 */
	public String getMessage() {
		return _status;
	}

	/**
	 * get object that has been built
	 */
	public abstract Object getResult();

	/**
	 * return flag indicating if task has been finished
	 */
	public boolean done() {
		return _finished;
	}

	/**
	 * return flag indicating if task has been stopped
	 */
	public boolean isStopped() {
		return _interrupted;
	}

	/**
	 * stop this thread
	 */
	public void stopProcess() {
		System.out.println("\nThreadWorker::stopProcess()");
		interrupt();
		_interrupted = true;
	}

	
}
