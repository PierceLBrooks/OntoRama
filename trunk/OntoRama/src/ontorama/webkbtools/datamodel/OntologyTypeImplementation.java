package ontorama.webkbtools.datamodel;



/**
 * Title:
 * Description:
 * Copyright:    Copyright DSTC (c) 2001
 * Company:
 * @author
 * @version 1.0
 */
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.LinkedList;

import ontorama.OntoramaConfig;
import ontorama.webkbtools.util.NoSuchRelationLinkException;

/**
 * Implements interface OntologyType using LinkedList as Iterator
 */

public class OntologyTypeImplementation implements OntologyType {
  /**
   * each element of this array is list of types corresponding
   * to a relationship link. Array indexes are corresponding to
   * constants describing relation links.
   * For example: to find a list of type's supertypes:
   * List superType = relationshipTypes[SUPERTYPE]
   */
  private LinkedList[] relationshipTypes = new LinkedList[OntoramaConfig.MAXTYPELINK + 1];

  /**
   * Name of this Ontology Type.
   */
  String typeName;

  /**
   * Descriptin for this Ontology Type.
   */
  String typeDescription;

  /**
   * Create new OntologyTypeImplementation
   * @param typeName
   */
  public OntologyTypeImplementation(String typeName) {
    this.typeName = typeName;
    initRelationshipTypes();
  }

  /**
   * Initialise relationshipTypes array.
   */
  private void initRelationshipTypes() {
    int count = 0;
    while(count <= OntoramaConfig.MAXTYPELINK) {
      relationshipTypes[count] = new LinkedList();
      count++;
    }
  }

  /**
   * Returns an iterator for relation links between the types
   * specified by a defined constance
   * @param relationLink
   * @return    Iterator of relation links
   * @throws    NoSuchRelationLinkException
   */
  public Iterator getIterator(int relationLink) throws NoSuchRelationLinkException{
    if(relationLink < 0 || relationLink > OntoramaConfig.MAXTYPELINK) {
      throw new NoSuchRelationLinkException(relationLink, OntoramaConfig.MAXTYPELINK);
    }
    return relationshipTypes[relationLink].iterator();
  }


  /**
   * Add given Ontology type with given relation link
   */
  public void addRelationType (OntologyType ontologyType, int relationLink) throws NoSuchRelationLinkException {
    if(relationLink < 0 || relationLink > OntoramaConfig.MAXTYPELINK) {
      throw new NoSuchRelationLinkException(relationLink, OntoramaConfig.MAXTYPELINK);
    }
    relationshipTypes[relationLink].add(ontologyType);
  }

  /**
   * Build LinkedList for given relationLink
   */

  /**
   * Sets the type description
   */
  public void setDescription(String description) {
    typeDescription = description;
  }

  /**
   * Gets the type description
   */
  public String getDescription() {
    return typeDescription;
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
    String str = "name: " + typeName + "\n";
    str = str + "description: " + typeDescription + "\n";
    int count = 0;
    while(count <= OntoramaConfig.MAXTYPELINK) {
      try {
          Iterator relationOntTypesIterator = this.getIterator(count);
          str = str + "relation link: " + count + ", types: " + "\n";
          while (relationOntTypesIterator.hasNext()) {
            OntologyType ot = (OntologyTypeImplementation) relationOntTypesIterator.next();
            str = str + ot.getName() + "\n";
          }
      }
      catch (NoSuchRelationLinkException e) {
        System.err.println("NoSuchRelationLinkException: " + e.getMessage());
        System.exit(1);
      }
      count++;
    }
    return str;
  }
}