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
package com.tonbeller.jpivot.mondrian;

import com.tonbeller.jpivot.olap.query.PositionBase;

/**
 * MondrianPosition is an adapter class for the Mondrian Position.  
 */
public class MondrianPosition extends PositionBase {

  mondrian.olap.Position monPosition;
  MondrianModel model;
  private int iAxis; // Axis ordinal for result axis

  /**
   * Constructor
   * create the array of members
   * @param monPosition corresponding Mondrian Position
   * @param model MondrianModel
   */
  MondrianPosition(mondrian.olap.Position monPosition, int iAxis, MondrianModel model) {
    super();
    this.monPosition = monPosition;
    this.model = model;
    this.iAxis = iAxis;
    // extract the members
    mondrian.olap.Member[] monMembers = monPosition.getMembers();
    members = new MondrianMember[monMembers.length];
    for (int j = 0; j < monMembers.length; j++) {
      members[j] = model.lookupMemberByUName(monMembers[j].getUniqueName());
    }
  }

  /**
   * get the Mondrian Members for this Axis Position
   * @return Array of Mondrian members
   */
  mondrian.olap.Member[] getMonMembers() {
    return monPosition.getMembers();
  }

  /**
   * Returns the iAxis.
   * @return int
   */
  int getAxis() {
    return iAxis;
  }

} // MondrianPosition
