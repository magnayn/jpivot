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
package com.tonbeller.jpivot.test.olap;

import java.util.Arrays;

import com.tonbeller.jpivot.olap.model.Axis;
import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.model.Position;
import com.tonbeller.jpivot.olap.navi.SortRank;

/**
 * Created on 24.10.2002
 * 
 * @author av
 */
public class TestSortRank extends TestExtensionSupport implements SortRank {
  Position sortPosition;
  int sortMode = SortRank.ASC;
  int topBottomCount = 10;
  boolean sorting = true;

  /**
   * returns true, if one of the members is a measure
   */
  public boolean isSortable(Position position) {
    Member[] members = position.getMembers();
    for (int i = 0; i < members.length; i++)
      if (members[i].getLevel().getHierarchy().getDimension().isMeasure())
        return true;
    return false;
  }

  /**
   * @see com.tonbeller.jpivot.olap.navi.SortRank#isCurrentSorting(Position)
   */
  public boolean isCurrentSorting(Position position) {
    if (sortPosition == null)  // no current sort position
      return false;
    return Arrays.equals(sortPosition.getMembers(), position.getMembers());
  }

  /**
   * @see com.tonbeller.jpivot.olap.navi.SortRank#sort(Axis, Position)
   */
  public void sort(Axis membersToSort, Position position) {
    sortPosition = position;
    fireModelChanged();
  }

  /**
   * Returns the sortMode.
   * @return int
   */
  public int getSortMode() {
    return sortMode;
  }

  /**
   * Returns the topBottomCount.
   * @return int
   */
  public int getTopBottomCount() {
    return topBottomCount;
  }

  /**
   * Sets the sortMode.
   * @param sortMode The sortMode to set
   */
  public void setSortMode(int sortMode) {
    this.sortMode = sortMode;
    fireModelChanged();
  }

  /**
   * Sets the topBottomCount.
   * @param topBottomCount The topBottomCount to set
   */
  public void setTopBottomCount(int topBottomCount) {
    this.topBottomCount = topBottomCount;
    fireModelChanged();
  }

  public boolean isSorting() {
    return sorting;
  }

  /**
   * Sets the enabled.
   * @param enabled The enabled to set
   */
  public void setSorting(boolean sorting) {
    this.sorting = sorting;
    fireModelChanged();
  }

}
