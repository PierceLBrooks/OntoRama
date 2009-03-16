package ontorama.backends.examplesmanager;

import ontorama.conf.DataFormatMapping;

/**
 * Description:  Hold all details of an example (such as name, ontology root,
 *              uri where to find it, parser details, etc).
 * Copyright:    Copyright (c) DSTC 2001
 * Company: DSTC
 * @author  nataliya
 * @version 1.0
 */


public class OntoramaExample {

    /**
     * name of this example - appers in the Examples Menu in the
     * MainApp
     */
    private String name;

    /**
     * ontology root
     */
    private String root;

    /**
     * uri
     */
    private String relativeUri;


    /**
     *
     */
    private String sourcePackagePathSuffix;

    /**
     * if need to group examples - can use subfolders in Menu
     */
    private String menuSubfolder;

    /**
     * if this example should be loaded first.
     */
    private boolean loadFirst = false;

	private DataFormatMapping dataFormatMapping;    

    /**
     *
     */
    public OntoramaExample(String name, String root, String relativeUri,
                           String sourcePackagePathSuffix, DataFormatMapping dataFormatMapping) {
        this.name = name;
        this.root = root;
        this.relativeUri = relativeUri;
        this.sourcePackagePathSuffix = sourcePackagePathSuffix;
        this.dataFormatMapping = dataFormatMapping;
    }

    /**
     *
     */
    public String getName() {
        return this.name;
    }

    /**
     *
     */
    public String getRoot() {
        return this.root;
    }

    /**
     *
     */
    public String getRelativeUri() {
        return this.relativeUri;
    }

    /**
     *
     */
    public void setMenuSubfolderName(String subfolderName) {
        this.menuSubfolder = subfolderName;
    }

    /**
     *
     */
    public String getMenuSubfolderName() {
        return this.menuSubfolder;
    }

    /**
     *
     */
    public String getSourcePackagePathSuffix() {
        return this.sourcePackagePathSuffix;
    }

    /**
     *
     */
    public void setLoadFirst(boolean loadFirst) {
        this.loadFirst = loadFirst;
    }

    /**
     *
     */
    public boolean isLoadFirst() {
        return this.loadFirst;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public void setRelativeUri(String relativeUri) {
        this.relativeUri = relativeUri;
    }
    
    


    /**
     *
     */
    public String toString() {
        String str = "Example: ";
        str = str + "name=" + this.name;
        str = str + ", root=" + this.root;
        str = str + ", relativeUri=" + this.relativeUri;
        str = str + ", sourcePackagePathSuffix=" + this.sourcePackagePathSuffix;
        return str;
    }
	/**
	 * Returns the dataFormatMapping.
	 * @return DataFormatMapping
	 */
	public DataFormatMapping getDataFormatMapping() {
		return this.dataFormatMapping;
	}

}