package ontorama.conf;

import java.util.LinkedList;
import java.util.List;

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
    private int _id;

    /**
     *
     */
    private String _type;

    /**
     *
     */
    private String _rdfTag;
    private List _rdfTagsList;

    /**
     *
     */
    public RdfMapping(int id, String type, String rdfTag) {
        _id = id;
        _type = type;
        _rdfTag = rdfTag;
        _rdfTagsList = new LinkedList();
        _rdfTagsList.add(_rdfTag);
    }

    /**
     *
     */
    public int getId() {
        return _id;
    }

    /**
     *
     */
    public String getType() {
        return _type;
    }

    /**
     *
     */
    public List getRdfTags() {
        return _rdfTagsList;
    }

    /**
     *
     */
    public void addRdfTag(String tag) {
        _rdfTagsList.add(tag);
    }
}