package ontorama.view;

import ontorama.ontologyConfig.examplesConfig.OntoramaExample;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) DSTC 2002</p>
 * <p>Company: DSTC</p>
 * @version 1.0
 */

public class HistoryElement {

  private String menuDisplayName;
  private String termName;
  private OntoramaExample example;

  /**
   *
   */
  public HistoryElement(String menuDisplayName, String termName, OntoramaExample example) {
    this.menuDisplayName = menuDisplayName;
    this.termName = termName;
    this.example = example;
  }

  /**
   *
   */
  public String getMenuDisplayName () {
    return this.menuDisplayName;
  }

  /**
   *
   */
  public String getTermName () {
    return this.termName;
  }

  /**
   *
   */
  public OntoramaExample getExample () {
    return this.example;
  }
}