package ontorama.util;

import java.util.List;

import ontorama.OntoramaConfig;
import ontorama.backends.Backend;
import ontorama.backends.examplesmanager.ExamplesBackend;
import ontorama.backends.examplesmanager.OntoramaExample;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 * @author nataliya
 * @version 1.0
 */

public class TestingUtils {

    /**
     *
     */
    public static OntoramaExample getExampleByName(String exampleName) {
    	Backend backend = OntoramaConfig.getBackend();
    	ExamplesBackend examplesBackend = (ExamplesBackend) backend;
        List examplesList = examplesBackend.getExamplesList();
        OntoramaExample result = null;
        for (int i = 0; i < examplesList.size(); i++) {
            OntoramaExample curExample = (OntoramaExample) examplesList.get(i);
            //System.out.println("cur example = " + curExample.getName());
            if (curExample.getName().equals(exampleName)) {
                result = curExample;
                //System.out.println("FOUND");
            }
        }
        if (result == null) {
            System.err.println("couldn't find example for exampleName = '" + exampleName + "' in the corresponding examples config file");
            System.exit(-1);
        }
        return result;
    }

}