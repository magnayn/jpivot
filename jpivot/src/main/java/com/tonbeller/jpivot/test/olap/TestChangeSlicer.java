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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.model.Position;
import com.tonbeller.jpivot.olap.navi.ChangeSlicer;

/**
 * Created on 09.12.2002
 * 
 * @author av
 */
public class TestChangeSlicer extends TestExtensionSupport implements ChangeSlicer {

  /**
   * @see com.tonbeller.jpivot.olap.navi.ChangeSlicer#getSlicer()
   */
  public Member[] getSlicer() {
    List list = new ArrayList();
    TestAxis axis = model().getSlicer();
    for (Iterator it = axis.getPositions().iterator(); it.hasNext(); ) {
      Position p = (Position)it.next();
      if (p.getMembers().length != 1)
        throw new IllegalArgumentException("slicer position must have exactly one member");
      list.add(p.getMembers()[0]);
    }
    return (Member[])list.toArray(new Member[list.size()]);
  }

  /**
   * @see com.tonbeller.jpivot.olap.navi.ChangeSlicer#setSlicer(Member[])
   */
  public void setSlicer(Member[] members) {
    TestAxis axis = TestOlapModelUtils.createAxis(members);
    model().setSlicer(axis);
    fireModelChanged();
  }

}
