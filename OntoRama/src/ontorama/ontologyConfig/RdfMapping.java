package ontorama.ontologyConfig;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class RdfMapping {
    /**
     *
     */
    private int id;

    /**
     *
     */
    private String type;

    /**
     *
     */
    private String rdfTag;

    /**
     *
     */
    public RdfMapping(int id, String type, String rdfTag) {
        this.id = id;
        this.type = type;
        this.rdfTag = rdfTag;
    }

    /**
     *
     */
    public int getId () {
        return this.id;
    }

    /**
     *
     */
    public String getType () {
        return this.type;
    }

    /**
     *
     */
    public String getRdfTag () {
        return this.rdfTag;
    }
}