package ontorama.webkbtools.datamodel;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import java.util.Iterator;

import ontorama.webkbtools.util.NoSuchRelationLinkException;

public interface OntologyType {



  /**
   * Returns an iterator for relation links between the types.
   * Specified by a defined constance
   */
  public Iterator getIterator(int relationLink)throws NoSuchRelationLinkException;

  /**
   * Set Iterator with given relation link
   */
  public void setIterator(OntologyType ontologyType, int relationLink)throws NoSuchRelationLinkException;

  /**
   * sets the type description
   */
  public void setDescription(String description);

  /**
   * gets the type description
   */
  public String getdescription();

  /**
   * get name of this type
   */
  public String getName ();
}