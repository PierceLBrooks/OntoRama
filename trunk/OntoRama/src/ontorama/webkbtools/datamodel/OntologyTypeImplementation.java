package ontorama.webkbtools.datamodel;



/**
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
 * Implements interface OntologyType using LinkedList as Iterator.
 * Assumptions: OntologyType can't have two types that have the same
 * names in it's Iterator
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
   * Description for this Ontology Type.
   */
  String typeDescription;

  /**
   * Creator for this Ontology Type
   */
   private String typeCreator;

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
   * Returns an iterator on ontology types for relation links between the types
   * specified by a given relation link constance
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
   * @param ontologyType, relationLink
   * @throws    NoSuchRelationLinkException
   * @todo: not sure if checking isRelationType is expensive or not. Check this!
   */
  public void addRelationType (OntologyType ontologyType, int relationLink) throws NoSuchRelationLinkException {
    if(relationLink < 0 || relationLink > OntoramaConfig.MAXTYPELINK) {
      throw new NoSuchRelationLinkException(relationLink, OntoramaConfig.MAXTYPELINK);
    }
    // check if it's already listed, then don't need to add
    // if it's not listed - add to the iterator
    if ( ! isRelationType (ontologyType, relationLink) ) {
      relationshipTypes[relationLink].add(ontologyType);
    }
  }

  /**
   *
   */
    public void removeRelation (int relationLink) throws NoSuchRelationLinkException {
        if(relationLink < 0 || relationLink > OntoramaConfig.MAXTYPELINK) {
            throw new NoSuchRelationLinkException(relationLink, OntoramaConfig.MAXTYPELINK);
        }
        relationshipTypes[relationLink] = new LinkedList();
    }

  /**
   * Check if given type is already listed with given relation link
   * @param ontologyType, relationLink
   * @return true if type ontologyType is listed in this type with
   *          relation link relationLink
   * @throws    NoSuchRelationLinkException
   */
  public boolean isRelationType (OntologyType ontologyType, int relationLink)
                        throws NoSuchRelationLinkException {
      Iterator it = this.getIterator(relationLink);
      while (it.hasNext()) {
        OntologyType ot = (OntologyType) it.next();
        //if ( (ot.getName()).equals(ontologyType.getName())) {
        if ( ot.equals(ontologyType) ) {
          return true;
        }
      }
      return false;
  }


  /**
   * Sets the type description
   * @param description - description String for this type
   */
  public void setDescription(String description) {
    typeDescription = description;
  }

  /**
   * Gets the type description
   * @param -
   * @return  description string for this type
   */
  public String getDescription() {
    return typeDescription;
  }

  /**
   * Set type creator
   * @param creatorStr
   */
   public void setCreator (String creatorStr) {
    this.typeCreator = creatorStr;
   }

   /**
    * Get type creator
    * @return   creator string
    */
    public String getCreator () {
        return this.typeCreator;
    }


  /**
   * get name of this type
   * @param -
   * @return  type name string
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
          //str = str + "relation link: " + count + ", types: " + "\n";
          while (relationOntTypesIterator.hasNext()) {
            OntologyType ot = (OntologyTypeImplementation) relationOntTypesIterator.next();
			//if (ot.getName) {
                //str = str + "\t- " + ot.getName();
				//str = str + "relation link: " + count + ", types: " + "\n";
            	str = str + "- " + ot.getName() + " ( relation link: " + count + ") " + "\n";
			//}
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
