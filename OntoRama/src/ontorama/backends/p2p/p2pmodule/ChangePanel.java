package ontorama.backends.p2p.p2pmodule;

import java.util.Enumeration;
import java.util.Vector;

/**
 * @author henrika
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */

//public class ChangePanel extends JPanel(){
public class ChangePanel{
    Vector tmpPanel = null;
    
    public ChangePanel(){
     tmpPanel = new Vector();   
    }
  
    public void addChange(String change, String peerName){
        tmpPanel.add(change + " by: " + peerName); 
    }

    public void empty(){
        tmpPanel.clear();
    }
    
    //This method should be removed when we use a panel instead
    public void show(){
        Enumeration enum = tmpPanel.elements();
        
        System.out.println("Change Panel");
        System.out.println("------------");
        boolean anyChanges = false;
        while (enum.hasMoreElements()){
        	anyChanges = true;
            System.out.println(enum.nextElement());   
        }
        
        if (!anyChanges) {
	        System.out.println("No changes has been received");        	
        }
    }
}
