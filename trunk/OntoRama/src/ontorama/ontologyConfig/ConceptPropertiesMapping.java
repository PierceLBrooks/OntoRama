package ontorama.ontologyConfig;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class ConceptPropertiesMapping {

    /**
     *
     */
    private String id;

    /**
     *
     */
    private String rdfTag;

    /**
     *
     */
    public ConceptPropertiesMapping (String id, String rdfTag) {
        this.id = id;
        this.rdfTag = rdfTag;
    }

    /**
     *
     */
     public String getId () {
        return this.id;
     }

     /**
      *
      */
      public String getRdfTag () {
        return this.rdfTag;
      }
}