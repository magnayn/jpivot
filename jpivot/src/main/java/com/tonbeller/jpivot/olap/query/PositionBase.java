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

import java.util.List;

import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.model.Position;
import com.tonbeller.jpivot.olap.model.Visitor;

/**
 * base class for both Mondrian and XMLA Position
 */
public class PositionBase implements Position {

  protected Member[] members;

  // cellList, parent and number are temp variables used by hierarchize sort
  public List cellList = null;
  public PositionBase parent = null;
  public int number; 

  /* 
   * @return array of members
   * @see com.tonbeller.jpivot.olap.model.Position#getMembers()
   */
  public Member[] getMembers() {
    return members;
  }
  

  /* (non-Javadoc)
   * @see com.tonbeller.jpivot.olap.model.Visitable#accept
   */
  public void accept(Visitor visitor) {
    visitor.visitPosition(this);
  }

  /*
   * @see com.tonbeller.jpivot.olap.model.Decorator#getRootDecoree()
   */
  public Object getRootDecoree() {
    return this;
  }

} // PositionBase
