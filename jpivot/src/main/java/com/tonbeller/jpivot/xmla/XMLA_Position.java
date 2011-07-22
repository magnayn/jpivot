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
package com.tonbeller.jpivot.xmla;

import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.query.PositionBase;

/**
 * XMLA Position
 */
public class XMLA_Position extends PositionBase {

  int axisOrdinal;

  /**
   * 
   * @param axisOrdinal
   */
  protected XMLA_Position(int axisOrdinal) {
    super();
    this.axisOrdinal = axisOrdinal;
  }

  /**
    * Sets the members.
    * @param members The members to set
    */
  public void setMembers(Member[] members) {
    this.members = members;
  }

  /**
   * Sets the axisOrdinal.
   * @param axisOrdinal The axisOrdinal to set
   */
  public void setAxisOrdinal(int axisOrdinal) {
    this.axisOrdinal = axisOrdinal;
  }

  /**
   * Returns the axisOrdinal.
   * @return int
   */
  public int getAxisOrdinal() {
    return axisOrdinal;
  }

  /**
   * 
   * @param other
   * @return boolean
   */
  public boolean isEquivalent(XMLA_Position other) {
    // same positions, if members are equal
    Member[] othermembers = other.getMembers();
    int nMembers = members.length;
    if (othermembers.length != nMembers)
      return false;
    for (int i = 0; i < nMembers; i++) {
      if (!othermembers[i].getLabel().equals(members[i].getLabel()))
        return false;
    }
    return true;
  }

} // XMLA_Position
