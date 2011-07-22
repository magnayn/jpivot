/*
 * ====================================================================
 * This software is subject to the terms of the Common Public License
 * Agreement, available at the following URL:
 *   http://www.opensource.org/licenses/cpl.html .
 * Copyright (C) 2003-2004 TONBELLER AG.
 * All Rights Reserved.
 * You must accept the terms of that agreement to use this software.
 * ====================================================================
 *
 * 
 */
package com.tonbeller.jpivot.olap.query;

import java.io.Serializable;

/**
 * Java Bean object to hold the state of an XMLA MDX session.
 * Contains parts of XMLA_Model and subordinate objects.
 */
public abstract class Memento implements Serializable {

  private String mdxQuery;
  
  boolean axesSwapped = false;

  // sorting  
  int topBottomCount = 0;
  int sortMode = 0;
  boolean sorting = false;
  String[] sortPosMembers = null;
  int quaxToSort = 0; // this is the Quax to be sorted
 
  private QuaxBean[] quaxes;

  boolean useQuax = false; // since version 3 (Mondrian)

  /**
   * Get mdxQuery.
   * @return mdxQuery
   */
  public String getMdxQuery() {
    return mdxQuery;
  }

  /**
   * Set mdxQuery.
   * @param string
   */
  public void setMdxQuery(String mdxQuery) {
    this.mdxQuery = mdxQuery;
  }

  /**
   * Get axesSwapped.
   * @return axesSwapped
   */
  public boolean isAxesSwapped() {
    return axesSwapped;
  }

  /**
   * Set axesSwapped.
   * @param axesSwapped
   */
  public void setAxesSwapped(boolean axesSwapped) {
    this.axesSwapped = axesSwapped;
  }

  /**
   * @return count for Top/Bottom function
   */
  public int getTopBottomCount() {
    return topBottomCount;
  }

  /**
   * set count for Top/Bottom function
   * @param i
   */
  public void setTopBottomCount(int topBottomCount) {
    this.topBottomCount = topBottomCount;
  }

  /**
   * @return sort mode
   */
  public int getSortMode() {
    return sortMode;
  }

  /**
   * set sort mode
   * @param sortMode
   */
  public void setSortMode(int sortMode) {
    this.sortMode = sortMode;
  }

  /**
   * @return sorting
   */
  public boolean isSorting() {
    return sorting;
  }

  /**
   * set sorting true/false
   * @param sorting
   */
  public void setSorting(boolean sorting) {
    this.sorting = sorting;
  }

  /**
   * @return sort position members unique names
   */
  public String[] getSortPosMembers() {
    return sortPosMembers;
  }
  
  /**
   * @return ordinal of quax to sort
   */
  public int getQuaxToSort() {
    return quaxToSort;
  }

  /**
   * set ordinal of quax to sort
   * @param quaxToSort ordinal of quax
   */
  public void setQuaxToSort(int quaxToSort) {
    this.quaxToSort = quaxToSort;
  }


  /**
   * set sort position members unique names
   * @param members unique names
   */
  public void setSortPosMembers(String[] members) {
    sortPosMembers = members;
  }

  /**
   * @return quaxes
   */
  public QuaxBean[] getQuaxes() {
    return quaxes;
  }

  /**
   * set quaxes
   * @param beans
   */
  public void setQuaxes(QuaxBean[] beans) {
    quaxes = beans;
  }

  /**
   * @return
   */
  public boolean isUseQuax() {
    return useQuax;
  }

  /**
   * @param b
   */
  public void setUseQuax(boolean b) {
    useQuax = b;
  }

  public abstract int getVersion();

  public abstract void setVersion(int i);

} // Memento
