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
import java.util.List;
import java.util.LinkedList;

import ontorama.OntoramaConfig;
import ontorama.webkbtools.util.NoSuchRelationLinkException;

public class OntologyTypeImplementation implements OntologyType {

  /**
   * each element of this array is list of types corresponding
   * to a relationship link. Array indexes are corresponding to
   * constants describing relation links.
   * For example: to find a list of type's supertypes:
   * List superType = relationshipTypes[SUPERTYPE]
   */
  private LinkedList[] relationshipTypes = new LinkedList[OntoramaConfig.MAXTYPELINK + 1];

  String typeName;
  String typeDescription;

  public OntologyTypeImplementation(String typeName) {
    this.typeName = typeName;
    initRelationshipTypes();
  }

  /**
   * method to initialise relationshipTypes array
   */
  private void initRelationshipTypes() {
    int count = 0;
    while(count <= OntoramaConfig.MAXTYPELINK) {
      relationshipTypes[count] = new LinkedList();
      count++;
    }
  }

  /**
   * Returns an iterator for relation links between the types.
   * Specified by a defined constance
   */
  public Iterator getIterator(int relationLink) throws NoSuchRelationLinkException{
    if(relationLink < 0 || relationLink > OntoramaConfig.MAXTYPELINK) {
      throw new NoSuchRelationLinkException(relationLink, OntoramaConfig.MAXTYPELINK);
    }
    return relationshipTypes[relationLink].iterator();
  }

  /**
   * Set Iterator with given relation link
   */
  public void setIterator(OntologyType ontologyType, int relationLink) throws NoSuchRelationLinkException {
    if(relationLink < 0 || relationLink > OntoramaConfig.MAXTYPELINK) {
      throw new NoSuchRelationLinkException(relationLink, OntoramaConfig.MAXTYPELINK);
    }
    relationshipTypes[relationLink].add(ontologyType);
  }

  /**
   * Build LinkedList for given relationLink
   */
  //public void

  /**
   * sets the type description
   */
  public void setDescription(String description) {

  }

  /**
   * gets the type description
   */
  public String getdescription() {

    return "";
  }

  /**
   * get name of this type
   */
  public String getName () {
    return typeName;
  }

  /**
   * toString method
   */
  public String toString () {
    String str = "name: " + typeName;
    return str;
  }

}