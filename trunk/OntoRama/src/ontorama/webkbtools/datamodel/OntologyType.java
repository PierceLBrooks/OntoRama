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

/**
 * Interface for OntologyType.
 * Ontology Type should model an ontology type with all it's properties,
 * such as type name, type description, type's children and parents and any
 * other relation links this type may have with other ontology types.
 * Relation Link is any valid ontology link describing relationship between
 * two types (such as Supertype(>), Subtype(<)).
 * All valid relation links should be defined in ontorama.OntoramaConfig.
 */
public interface OntologyType {

  /**
   * Returns an iterator for relation links between the types.
   * Specified by a defined constance
   * @param relationLink
   * @return    Iterator   returns Iterator of OntologyTypes related to
   *                    this OntologyType by relationLink
   * @throws    NoSuchRelationLinkException
   */
  public Iterator getIterator(int relationLink)throws NoSuchRelationLinkException;

  /**
   * Add given OntologyType with given relation link
   */
  public void addRelationType (OntologyType ontologyType, int relationLink)throws NoSuchRelationLinkException;

  /**
   * Sets the type description
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