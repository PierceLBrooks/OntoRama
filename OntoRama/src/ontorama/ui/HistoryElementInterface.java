package ontorama.ui;

import ontorama.ontotools.query.Query;

/**
 * @author nataliya
 */
public interface HistoryElementInterface {
	public String getMenuDisplayName();
	public Query getQuery();
	public void displayElement();
}
