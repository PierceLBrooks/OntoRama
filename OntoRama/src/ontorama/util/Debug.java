
package ontorama.util;

public class Debug {
    /**
     *
     */
    private boolean debug = false;

    /**
     *
     */
    public Debug(boolean debug) {
        this.debug = debug;
    }

    /**
     *
     */
    public void message(String className, String methodName, String message) {
        if (debug) {
            System.out.println(className + ", method " + methodName + ": " + message);
        }
    }

    /**
     *
     */
    public void message(String message) {
        if (debug) {
            System.out.println(message);
        }
    }
}