package ontorama.backends.examplesmanager;

import ontorama.conf.DataFormatMapping;
import ontorama.ontotools.source.Source;

/**
 * Holds all details of an example (such as name, ontology root,
 * uri where to find it, parser details, etc).
 * 
 * Copyright:    Copyright (c) DSTC 2001
 * Company: DSTC
 */
public class OntoramaExample {

    /**
     * Name of this example - appears in the Examples Menu in the
     * application.
     */
    private final String name;

    /**
     * Root of the ontology.
     */
    private final String root;

    private final String relativeUri;

    /**
     * If need to group examples - can use subfolders in Menu
     */
    private String menuSubfolder;

    /**
     * Should this example be loaded first.
     */
    private boolean loadFirst = false;

    private final Source dataSource;    

	private final DataFormatMapping dataFormatMapping;

    public OntoramaExample(String name, String root, String relativeUri,
                           Source dataSource, DataFormatMapping dataFormatMapping) {
        this.name = name;
        this.root = root;
        this.relativeUri = relativeUri;
        this.dataSource = dataSource;
        this.dataFormatMapping = dataFormatMapping;
    }

    public String getName() {
        return this.name;
    }

    public String getRoot() {
        return this.root;
    }

    public String getRelativeUri() {
        return this.relativeUri;
    }

    public void setMenuSubfolderName(String subfolderName) {
        this.menuSubfolder = subfolderName;
    }

    public String getMenuSubfolderName() {
        return this.menuSubfolder;
    }

    public Source getDataSource() {
		return dataSource;
	}

	public void setLoadFirst(boolean loadFirst) {
        this.loadFirst = loadFirst;
    }

    public boolean isLoadFirst() {
        return this.loadFirst;
    }

    public String toString() {
        String str = "Example: ";
        str = str + "name=" + this.name;
        str = str + ", root=" + this.root;
        str = str + ", relativeUri=" + this.relativeUri;
        str = str + ", source=" + this.dataSource.getClass().getName();
        return str;
    }

    /**
	 * Returns the dataFormatMapping.
	 */
	public DataFormatMapping getDataFormatMapping() {
		return this.dataFormatMapping;
	}
}