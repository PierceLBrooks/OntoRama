package ontorama.backends;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author henrika
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class Menu {
 //This is a strande solution, but it will be changed when we uses JMenus instead.
   protected static char readChar() {
        char retVal;
        try {
            // read stdin into the byte array.
            retVal = (char) System.in.read();
            System.in.read();
            System.in.read();
        } catch (Exception e) {
            // if there is a failure, print an error
            e.printStackTrace();
            retVal = 'w';
        }
        return retVal;
    }
    
    protected static String readLine() {
        try {
            BufferedReader stdin = 
                new BufferedReader(new InputStreamReader(System.in));
            return stdin.readLine();
        } catch (Exception e) {
            // if there is a failure, print an error
            e.printStackTrace();
            return "";
        }
        
    }
}
