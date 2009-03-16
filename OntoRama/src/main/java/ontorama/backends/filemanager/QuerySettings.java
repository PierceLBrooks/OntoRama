package ontorama.backends.filemanager;

/**
 * @author nataliya
 */
public class QuerySettings {

	private String parserPackage;
	private String sourceUri;

	public QuerySettings(String parserPackage, String sourceUri) {
		this.parserPackage = parserPackage;
		this.sourceUri = sourceUri;
	}

	public String getParserPackageName () {
		return this.parserPackage;
	}

	public String getSourceUri () {
		return this.sourceUri;
	}

}
