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
/*
 * Used by old memento (MDX Generation version 2)
 * HHTASK: remove, when old Bookmarks are replaced
 */
package com.tonbeller.jpivot.mondrian;

import java.io.Serializable;

/**
 * Java Bean object to hold the state of MondrianMemberSet.
 * Used for serialization of MondrianModel via MondrianMemento.
 * Referenced by MondrianQuaxBean.
 */
public class MondrianMemberSetBean implements Serializable {
  int type;
  String level;
  String[] memberList;
  String[] drillDownList;

  /**
   * Get level.
   * @return level
   */
  public String getLevel() {
    return level;
  }

  /**
   * Get memberList.
   * @return memberList
   */
  public String[] getMemberList() {
    return memberList;
  }

  /**
   * Get type.
   * @return type
   */
  public int getType() {
    return type;
  }

  /**
   * Set level.
   * @param string
   */
  public void setLevel(String level) {
    this.level = level;
  }

  /**
   * Set memberList.
   * @param memberList
   */
  public void setMemberList(String[] memberList) {
    this.memberList = memberList;
  }

  /**
   * Set type.
   * @param i
   */
  public void setType(int type) {
    this.type = type;
  }

  /**
   * Get drillDownList.
   * @return drillDownList
   */
  public String[] getDrillDownList() {
    if (drillDownList != null)
      return drillDownList;
    else
      return new String[0];
  }

  /**
   * Set drillDownList.
   * @param drillDownList
   */
  public void setDrillDownList(String[] drillDownList) {
    this.drillDownList = drillDownList;
  }

} // End MondrianMemberSetBean
