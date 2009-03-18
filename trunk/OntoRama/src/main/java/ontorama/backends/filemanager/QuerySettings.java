package ontorama.backends.filemanager;

import ontorama.ontotools.parser.Parser;

public class QuerySettings {

	private Parser parser;
	private String sourceUri;

	public QuerySettings(Parser parser, String sourceUri) {
		this.parser = parser;
		this.sourceUri = sourceUri;
	}

	public Parser getParser() {
		return parser;
	}
	
	public String getSourceUri () {
		return this.sourceUri;
	}
}
