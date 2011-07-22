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

import org.apache.log4j.Logger;

import com.tonbeller.jpivot.core.ExtensionSupport;
import com.tonbeller.jpivot.core.ModelSupport;
import com.tonbeller.jpivot.olap.model.Axis;
import com.tonbeller.jpivot.olap.model.Dimension;
import com.tonbeller.jpivot.olap.model.Hierarchy;
import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.model.OlapUtils;
import com.tonbeller.jpivot.olap.model.Position;
import com.tonbeller.jpivot.olap.navi.SortRank;
import com.tonbeller.jpivot.olap.query.QueryAdapter.QueryAdapterHolder;

/**
 * @author hh
 *
 * Implementation base of the Sort Extension
 */
public abstract class SortRankBase
  extends ExtensionSupport
  implements SortRank, QuaxChangeListener {

  protected int topBottomCount = 10;
  protected int sortMode = com.tonbeller.jpivot.olap.navi.SortRank.ASC;
  protected boolean sorting = false;
  protected Member[] sortPosMembers = null;
  protected Quax quaxToSort = null; // this is the Quax to be sorted

  static final private int STATE_NONE = 0;
  static final private int STATE_TOP = 1;
  static final private int STATE_BOTTOM = 2;

  static Logger logger = Logger.getLogger(SortRankBase.class);

  public SortRankBase() {
    super.setId(SortRank.ID);
  }

  /**
   * implement QuaxChangeListener
   */
  public void quaxChanged(Quax quax, Object source, boolean changedByNavi) {
    // if the axis to sort (normaly *not* the measures)
    //  was changed by the Navi GUI, we want to switch sorting off
    if (quax != quaxToSort)
      return;
    if (!changedByNavi)
      return;

    if (!sorting)
      return;

    boolean logDebug = logger.isDebugEnabled();
    if (logDebug)
      logger.debug("Quax changed by navi - switch sorting off");

    sorting = false;
    ModelSupport model = (ModelSupport) this.getModel();
    model.fireModelChanged();
  }

  /**
   * @see com.tonbeller.jpivot.olap.navi.SortRank#getTopBottomCount()
   * @return top/bottom count
   */
  public int getTopBottomCount() {
    return topBottomCount;
  }

  /**
   * @see com.tonbeller.jpivot.mondrian.olap.navi.SortRank#setTopBottomCount(int)
   * @param top/bottom count
   */
  public void setTopBottomCount(int topBottomCount) {

    boolean logInfo = logger.isInfoEnabled();

    if (this.topBottomCount == topBottomCount)
      return;
    if (logInfo)
      logger.info("change topBottomCount from " + this.topBottomCount + " to " + topBottomCount);
    this.topBottomCount = topBottomCount;

    if (sorting
      && sortPosMembers != null
      && (sortMode == SortRank.TOPCOUNT || sortMode == SortRank.BOTTOMCOUNT)) {
      ((ModelSupport) getModel()).fireModelChanged();
    }
  }

  /**
   * @see com.tonbeller.jpivot.olap.navi.SortRank#getSortMode()
   * @return sort mode (ASC,DESC,BASC,BDESC,TOPCOUNT,BOTTOMCOUNT)
   */
  public int getSortMode() {
    return sortMode;
  }

  /**
   * @see com.tonbeller.jpivot.olap.navi.SortRank#setSortMode(int)
   * @param sort mode (ASC,DESC,BASC,BDESC)
   */
  public void setSortMode(int sortMode) {
    if (this.sortMode == sortMode)
      return;

    boolean logInfo = logger.isInfoEnabled();
    if (logInfo)
      logger.info("change topBottomCount from " + this.sortMode + " to " + sortMode);
    this.sortMode = sortMode;
    if (sorting && sortPosMembers != null) {
      ((ModelSupport) getModel()).fireModelChanged();
    }
  }

  /**
   * @see com.tonbeller.jpivot.olap.navi.SortRank#isCurrentSorting(Position)
   * @param position to be checked
   * @return true, if position is the current sorting position
   */
  public boolean isCurrentSorting(Position position) {
    if (!sorting || sortPosMembers == null) // not sorting
      return false;
    else
      return OlapUtils.compareMembers(sortPosMembers, position.getMembers());
  }

  /**
   * returns true, if one of the members is a measure
   * @param position the position to check for sortability
   * @return true, if the position is sortable
   * @see com.tonbeller.jpivot.olap.navi.SortRank#isSortable(Position)
   */
  public boolean isSortable(Position position) {
    Member[] members = position.getMembers();
    for (int i = 0; i < members.length; i++)
      if (members[i].getLevel().getHierarchy().getDimension().isMeasure())
        return true;
    return false;
  }

  /**
   * @see com.tonbeller.jpivot.olap.navi.SortRank#sort(Axis, Position)
   * @param membersToSort Axis containing the members to be sorted
   * @param position Position on "other axis" defining the members by which
   *                  the membersToSort are sorted
   */
  public void sort(Axis membersToSort, Position position) {

    boolean logInfo = logger.isInfoEnabled();

    // if the axis to sort does not contain any positions - sorting is not posssible
    if (membersToSort.getPositions().isEmpty()) {
      logger.warn("reject sort, the axis to be sorted is empty");
      sorting = false;
      return;
    }

    QueryAdapter.QueryAdapterHolder model = (QueryAdapter.QueryAdapterHolder) getModel();
    sortPosMembers = position.getMembers();

    // find the axis to sort
    Hierarchy[] hiers = membersToSort.getHierarchies();
    Dimension dim = hiers[0].getDimension();
    quaxToSort = model.getQueryAdapter().findQuax(dim);
    if (quaxToSort == null) {
      logger.warn("reject sort, the Quax is null");
      sorting = false;
      return;
    }

    if (logInfo) {
      String poString = "";
      Member[] members = position.getMembers();
      for (int i = 0; i < members.length; i++) {
        if (i > 0)
          poString += " ";
        poString += ((MDXElement) members[i]).getUniqueName();
      }
      logger.info("change Sort Position " + poString + " iAxisToSort=" + quaxToSort.getOrdinal());
    }

    ((ModelSupport) getModel()).fireModelChanged();
  }

  /**
   */
  public boolean isSorting() {
    return sorting;
  }

  /**
   * @task support for natural sorting
   */
  public void setSorting(boolean sorting) {
    if (sorting == this.sorting)
      return;

    boolean logInfo = logger.isInfoEnabled();
    if (logInfo)
      logger.info("change sorting to " + sorting);

    this.sorting = sorting;
    ((ModelSupport) getModel()).fireModelChanged();
  }

  /**
   * reset to initial state
   */
  public void reset() {

    boolean logDebug = logger.isDebugEnabled();
    if (logDebug)
      logger.debug("SortRank set to initial state");

    topBottomCount = 10;
    sortMode = com.tonbeller.jpivot.olap.navi.SortRank.ASC;
    sorting = false;
    sortPosMembers = null;

    // we want to know, whenever a quax is changed
    Quax[] quaxes = ((QueryAdapter.QueryAdapterHolder) getModel()).getQueryAdapter().getQuaxes();
    for (int i = 0; i < quaxes.length; i++) {
      quaxes[i].addChangeListener(this);
    }

  }

  /**
   * @return
   */
  public Member[] getSortPosMembers() {
    return sortPosMembers;
  }

  /**
   * @param members
   */
  public void setSortPosMembers(Member[] sortPosMembers) {
    this.sortPosMembers = sortPosMembers;
  }

  /**
   * apply sort to query must be implemented for specific olap sources 
   */
  public abstract void addSortToQuery();

  /**
   * @return ordinal of quax to sort
   */
  public int getQuaxToSort() {
    return quaxToSort.getOrdinal();
  }

  /**
   * @return ordinal of quax to sort, if sorting is active
   */
  public int activeQuaxToSort() {
    if (sorting && sortPosMembers != null)
      return quaxToSort.getOrdinal();
    else
      return -1;
  }

  /**
   * set quax to sort
   * @parameter ordinal of quax to sort
   */
  public void setQuaxToSort(int ordinal) {
    QueryAdapter.QueryAdapterHolder model = (QueryAdapterHolder) getModel();
    quaxToSort = model.getQueryAdapter().getQuaxes()[ordinal];
  }

  /**
   * @return true, if there is a sort for the query
   */
  public boolean isSortOnQuery() {
    return sorting && sortPosMembers != null;
  }

} // End SortRankBase
