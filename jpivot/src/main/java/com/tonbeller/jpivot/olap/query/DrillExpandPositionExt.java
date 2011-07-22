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

import com.tonbeller.jpivot.core.ExtensionSupport;
import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.model.Position;
import com.tonbeller.jpivot.olap.navi.DrillExpandPosition;

/**
 * @author hh
 */
public class DrillExpandPositionExt extends ExtensionSupport implements DrillExpandPosition {

  /**
   * Constructor sets ID
   */
  public DrillExpandPositionExt() {
    super.setId(DrillExpandPosition.ID);
  }

  /**
   * @see com.tonbeller.jpivot.olap.navi.DrillExpand#canExpand(Member)
   * @param position position to be expanded
   * @param member member to be expanded
   * @return true if the member can be expanded
   */
  public boolean canExpand(Position position, Member member) {
    QueryAdapter.QueryAdapterHolder model = (QueryAdapter.QueryAdapterHolder) getModel();
    Member[] pathMembers = memberPath(position, member);
    return model.getQueryAdapter().canExpand(pathMembers);
  }

  /**
   * @see com.tonbeller.jpivot.olap.navi.DrillExpand#canCollapse(Member)
   * @param position position to be expanded
   * @return true if the member can be expanded
   */
  public boolean canCollapse(Position position, Member member) {
    QueryAdapter.QueryAdapterHolder model = (QueryAdapter.QueryAdapterHolder) getModel();
    Member[] pathMembers = memberPath(position, member);
    return model.getQueryAdapter().canCollapse(pathMembers);

  }

  /**
   * @see com.tonbeller.jpivot.olap.navi.DrillExpand#expand(Member)
   * @param position position to be expanded
   * @param member member to be expanded
   */
  public void expand(Position position, Member member) {

    QueryAdapter.QueryAdapterHolder model = (QueryAdapter.QueryAdapterHolder) getModel();
    Member[] pathMembers = memberPath(position, member);
    model.getQueryAdapter().expand(pathMembers);
  }

  /**
   * @see com.tonbeller.jpivot.olap.navi.DrillExpand#expand(Member)
   * @param position position to be collapsed
   * @param position member to be collapsed
   */
  public void collapse(Position position, Member member) {
    QueryAdapter.QueryAdapterHolder model = (QueryAdapter.QueryAdapterHolder) getModel();
    Member[] pathMembers = memberPath(position, member);
    model.getQueryAdapter().collapse(pathMembers);
  }

  /**
    * determine path to member
    * @param position
    * @param member
    * @return path to Member
    */
  private Member[] memberPath(Position position, Member member) {
    Member[] posMembers = position.getMembers();
    int pathlen = 0;
    for (int i = 0; i < posMembers.length; i++) {
      if (posMembers[i].equals(member)) {
        pathlen = i + 1;
        break;
      }
    }
    if (pathlen == 0)
      return null; // should not occur
    Member[] pathMembers = new Member[pathlen];
    for (int i = 0; i < pathlen; i++) {
      pathMembers[i] = posMembers[i];
    }
    return pathMembers;
  }

} // End DrillExpandPositionExt
