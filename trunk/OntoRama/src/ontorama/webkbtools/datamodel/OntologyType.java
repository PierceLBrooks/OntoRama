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

import ontorama.webkbtools.util.NoSuchRelationLinkException;
import ontorama.webkbtools.util.NoSuchPropertyException;

/**
 * Model an Ontology Type with all it's properties,
 * such as type name, type description, type's children and parents and any
 * other relation links this type may have with other ontology types.
 *
 * Relation Link is any valid ontology link describing relationship between
 * two types (such as Supertype(>), Subtype(<)).
 *
 * All valid relation links should be defined in ontorama.OntoramaConfig.
 * @see ontorama.OntoramaConfig
 */
public interface OntologyType {
  /**
   * Returns an iterator for relation links between the types.
   * Relation links are specified by a defined constance
   * @param relationLink
   * @return    Iterator   returns Iterator of OntologyTypes related to
   *                    this OntologyType by relationLink
   * @throws    NoSuchRelationLinkException
   */
  public Iterator getIterator(int relationLink)throws NoSuchRelationLinkException;


  /**
   * Add given OntologyType with given relation link
   */
  void addRelationType (OntologyType ontologyType, int relationLink)throws NoSuchRelationLinkException;

  /**
   * Remove all relations of type relationLink
   *
   * For example: if we have type1 connected to type2 and type3 with relationLink=1,
   * and type1 connected to type4 with relationLink=2:
   * <pre>
   * type1--+
   *        +--type2 (relLink=1)
   *        +--type3 (relLink=1)
   *        +--type4 (relLink=2)
   * </pre>
   *  Then, if we called type1.removeRelation(1), we would end up with
   * type1 connected to type4 with relationLink=2:
   * <pre>
   * type1--+
   *        +--type4 (relLink=2)
   * </pre>
   *
   * @param relationLink
   */
  public void removeRelation (int relationLink) throws NoSuchRelationLinkException ;

  /**
   * Remove one relation for given ontology type and relation link
   *
   * For example: if we have type1 connected to type2 and type3 with relationLink=1,
   * and type1 connected to type4 with relationLink=2:
   * <pre>
   * type1--+
   *        +--type2 (relLink=1)
   *        +--type3 (relLink=1)
   *        +--type4 (relLink=2)
   * </pre>
   *  Then, if we called type1.removeRelation(type3,1), we would end up with
   * type1 connected to type4 with relationLink=2:
   * <pre>
   * type1--+
   *        +--type2 (relLink=1)
   *        +--type4 (relLink=2)
   * </pre>
   *
   * @param type - type to which we wish to remove connection to
   * @param relationLink
   */
  public void removeRelation (OntologyType type, int relationLink) throws NoSuchRelationLinkException ;

  /**
  *
  */
  public void addTypeProperty (String propertyName, String propertyValue) throws NoSuchPropertyException;

  /**
   *
   */
   public List getTypeProperty (String propertyName) throws NoSuchPropertyException;


  /**
   * Check if given type is already listed with given relation link
   * @param ontologyType, relationLink
   * @return true if type ontologyType is listed in this type with
   *          relation link relationLink
   * @throws    NoSuchRelationLinkException
   */
  public boolean isRelationType (OntologyType ontologyType, int relationLink) throws NoSuchRelationLinkException ;

  /**
   * get name of this type
   */
  public String getName ();

  /**
   * set an alternative name for this type.
   * For example, in RDF input, we would get something like: 'http://www.webkb.org/theKB.rdf/wn#Cat'
   * as a full name for type 'wn#Cat'.
   */
   public void setFullName (String fullName);

  /**
   * get an alternative name for this type.
   */
  public String getFullName ();

}