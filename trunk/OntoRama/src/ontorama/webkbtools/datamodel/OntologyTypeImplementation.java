package ontorama.webkbtools.datamodel;


/**
 * Copyright:    Copyright DSTC (c) 2001
 * Company: DSTC
 * @author
 * @version 1.0
 */

import ontorama.OntoramaConfig;
import ontorama.webkbtools.util.NoSuchPropertyException;
import ontorama.webkbtools.util.NoSuchRelationLinkException;

import java.util.*;

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
     * keys - string, property name
     * values - linked list of property values
     */
    private Hashtable typeProperties = new Hashtable();

    /**
     * Name of this Ontology Type.
     */
    String typeName;

    /**
     * Full/Alternative name of this Ontology Type
     */
    String typeFullName;

    /**
     * Create new OntologyTypeImplementation
     * @param typeName
     */
    public OntologyTypeImplementation(String typeName) {
        this(typeName, typeName);
    }

    /**
     * Create new OntologyTypeImplementation
     * @param typeName, typeFullName
     */
    public OntologyTypeImplementation(String typeName, String typeFullName) {
        this.typeName = typeName;
        this.typeFullName = typeFullName;
        initRelationshipTypes();
        initConceptProperties();
    }

    /**
     * Initialise relationshipTypes array.
     */
    private void initRelationshipTypes() {
        int count = 0;
        while (count <= OntoramaConfig.MAXTYPELINK) {
            relationshipTypes[count] = new LinkedList();
            count++;
        }
    }

    /**
     *
     */
    private void initConceptProperties() {
        Enumeration conceptPropertiesConfig = OntoramaConfig.getConceptPropertiesTable().keys();
        while (conceptPropertiesConfig.hasMoreElements()) {
            String propName = (String) conceptPropertiesConfig.nextElement();
            typeProperties.put(propName, new LinkedList());
        }
    }


    /**
     * Returns an iterator on ontology types for relation links between the types
     * specified by a given relation link constance
     * @param relationLink
     * @return    Iterator of relation links
     * @throws    NoSuchRelationLinkException
     */
    public Iterator getIterator(int relationLink) throws NoSuchRelationLinkException {
        if (relationLink < 0 || relationLink > OntoramaConfig.MAXTYPELINK) {
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
    public void addRelationType(OntologyType ontologyType, int relationLink) throws NoSuchRelationLinkException {
        if (relationLink < 0 || relationLink > OntoramaConfig.MAXTYPELINK) {
            throw new NoSuchRelationLinkException(relationLink, OntoramaConfig.MAXTYPELINK);
        }
        // check if it's already listed, then don't need to add
        // if it's not listed - add to the iterator
        if (!isRelationType(ontologyType, relationLink)) {
            relationshipTypes[relationLink].add(ontologyType);
        }
    }

    /**
     *
     */
    public void removeRelation(int relationLink) throws NoSuchRelationLinkException {
        if (relationLink < 0 || relationLink > OntoramaConfig.MAXTYPELINK) {
            throw new NoSuchRelationLinkException(relationLink, OntoramaConfig.MAXTYPELINK);
        }
        relationshipTypes[relationLink] = new LinkedList();
    }

    /**
     *
     */
    public void removeRelation(OntologyType type, int relationLink) throws NoSuchRelationLinkException {
        if (relationLink < 0 || relationLink > OntoramaConfig.MAXTYPELINK) {
            throw new NoSuchRelationLinkException(relationLink, OntoramaConfig.MAXTYPELINK);
        }
        Iterator it = this.getIterator(relationLink);
        LinkedList result = new LinkedList();
        while (it.hasNext()) {
            OntologyType cur = (OntologyType) it.next();
            if (cur.equals(type)) {
                // remove this one
                continue;
            }
            result.add(cur);
        }
        relationshipTypes[relationLink] = result;
    }

    /**
     * Check if given type is already listed with given relation link
     * @param ontologyType, relationLink
     * @return true if type ontologyType is listed in this type with
     *          relation link relationLink
     * @throws    NoSuchRelationLinkException
     */
    public boolean isRelationType(OntologyType ontologyType, int relationLink)
            throws NoSuchRelationLinkException {
        Iterator it = this.getIterator(relationLink);
        while (it.hasNext()) {
            OntologyType ot = (OntologyType) it.next();
            //if ( (ot.getName()).equals(ontologyType.getName())) {
            if (ot.equals(ontologyType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Set type property specified by given property name
     */
    public void addTypeProperty(String propertyName, String propertyValue) throws NoSuchPropertyException {
        //System.out.println("addTypeProperty, propertyName = '" + propertyName + "', propertyValue = '" + propertyValue + "'");
        if (OntoramaConfig.getConceptPropertiesTable().containsKey(propertyName)) {
            List l = this.getTypeProperty(propertyName);
            if (l.contains(propertyValue)) {
                // already is in the list
                return;
            }
            l.add(propertyValue);
            typeProperties.put(propertyName, l);
        } else {
            throw new NoSuchPropertyException(propertyName, OntoramaConfig.getConceptPropertiesTable().keys());
        }
    }

    /**
     *
     */
    public List getTypeProperty(String propertyName) throws NoSuchPropertyException {
        if (OntoramaConfig.getConceptPropertiesTable().containsKey(propertyName)) {
            return (List) typeProperties.get(propertyName);
        } else {
            throw new NoSuchPropertyException(propertyName, OntoramaConfig.getConceptPropertiesTable().keys());
        }

    }

    /**
     * get name of this type
     * @param -
     * @return  type name string
     */
    public String getName() {
        return typeName;
    }

    /**
     *
     */
    public void setFullName(String fullName) {
        typeFullName = fullName;
    }

    /**
     *
     */
    public String getFullName() {
        return typeFullName;
    }


    /**
     * toString method
     */
    public String toString() {
        String str = "NAME: " + typeName;
        str = str + "; full name = " + typeFullName;
        try {
            Enumeration conceptProperties = OntoramaConfig.getConceptPropertiesTable().keys();
            str = str + "; PROPERTIES: ";
            while (conceptProperties.hasMoreElements()) {
                String propName = (String) conceptProperties.nextElement();
                List cur = this.getTypeProperty(propName);
                str = str + propName + ": " + cur;
                if (conceptProperties.hasMoreElements()) {
                    str = str + ", ";
                }
            }
            Iterator relLinks = OntoramaConfig.getRelationLinksSet().iterator();
            str = str + "; RELATIONS: ";
            while (relLinks.hasNext()) {
                Integer relLink = (Integer) relLinks.next();
                Iterator relatedTypes = this.getIterator(relLink.intValue());
                while (relatedTypes.hasNext()) {
                    OntologyType relatedType = (OntologyType) relatedTypes.next();
                    //str = str + "... " + relatedType.getName() + " ( relation link: " + relLink.intValue() + ") " + "\n";
                    str = str + relatedType.getName() + " (" + relLink.intValue() + ")";
                    if (relatedTypes.hasNext()) {
                        str = str + ", ";
                    }
                }
            }
        } catch (NoSuchRelationLinkException e) {
            System.err.println("NoSuchRelationLinkException: " + e.getMessage());
            System.exit(1);
        } catch (NoSuchPropertyException e1) {
            System.err.println("NoSuchPropertyException: " + e1.getMessage());
            System.exit(1);
        }

        return str;
    }
}
