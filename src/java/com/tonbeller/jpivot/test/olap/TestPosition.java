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

import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.model.impl.PositionImpl;

/**
 * Created on 02.12.2002
 * 
 * @author av
 */
public class TestPosition extends PositionImpl {
  TestAxis axis;
  public TestPosition(TestAxis axis) {
    this.axis = axis;
  }
  /**
   * Returns the axis.
   * @return TestAxis
   */
  public TestAxis getAxis() {
    return axis;
  }

  /**
   * Sets the axis.
   * @param axis The axis to set
   */
  public void setAxis(TestAxis axis) {
    this.axis = axis;
  }

  public boolean contains(Member m) {
    Member[] members = super.getMembers();
    for (int i = 0; i < members.length; i++)
      if (m.equals(members[i]))
        return true;
    return false;
  }

  public boolean membersEqual(TestPosition that) {
    Member[] m1 = this.getMembers();
    Member[] m2 = that.getMembers();
    if (m1.length != m2.length)
      return false;
    for (int i = 0; i < m1.length; i++)
      if (!m1[i].equals(m2[i]))
        return false;
    return true;
  }
  
  public int indexOf(Member m) {
    Member[] members = super.getMembers();
    for (int i = 0; i < members.length; i++)
      if (m.equals(members[i]))
        return i;
    return -1;
  }

  public Object clone() {
    try {
      TestPosition p = (TestPosition)super.clone();
      p.setMembers((Member[])getMembers().clone());
      return p;
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
      return null;
    }
  }
}
