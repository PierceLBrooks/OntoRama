package ontorama.backends.filemanager;

import ontorama.backends.Menu;

/**
 * @author henrika
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class FileMenu extends Menu{
    private FileBackend fileBackend = null;
    
    public FileMenu(FileBackend fb){
        this.fileBackend = fb;   
    }
    
     public void showMenu(){
            System.out.println("-----------------");
            System.out.println("File Menu");
            System.out.println("a => Open File"); 
            System.out.println("b => Save"); 
            System.out.println("");
            System.out.println("c => Close Menu"); 
            
            System.out.print(">");
            
            char charIn = readChar(); 
            String lineIn = null;           
                     
            switch(charIn) {
                case 'a' :
                        System.out.println("Open File");
                        System.out.print("Name of the file to open (include path):");
                        lineIn = readLine();
                        if (lineIn.length() == 0) {
                            System.out.println("You did not write a name including the path");
                        } else {
                            this.fileBackend.loadFile(lineIn);
                        }
                    break;
              case 'b' :
                        System.out.println("Save File");
                        System.out.print("Name of the file to save (include path):");
                        lineIn = readLine();
                        if (lineIn.length() == 0) {
                            System.out.println("You did not write a name including the path");
                        } else {
                            this.fileBackend.saveFile(lineIn);
                        }
                        break;
                        
              case 'h':
                        break;
           
            }
        }   
}