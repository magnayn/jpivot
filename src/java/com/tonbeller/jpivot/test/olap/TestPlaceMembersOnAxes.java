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

import com.tonbeller.jpivot.olap.model.Hierarchy;
import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.model.Position;
import com.tonbeller.jpivot.olap.navi.PlaceMembersOnAxes;

/**
 * Created on 13.12.2002
 * 
 * @author av
 */
public class TestPlaceMembersOnAxes extends TestPlaceHierarchiesOnAxes implements PlaceMembersOnAxes {

  /**
   * @see com.tonbeller.jpivot.olap.navi.PlaceMembersOnAxes#createMemberExpression(List)
   */
  public Object createMemberExpression(List members) {
    return TestOlapModelUtils.createAxis(members);
  }

  /**
   * @see com.tonbeller.jpivot.olap.navi.PlaceMembersOnAxes#findVisibleMembers(Hierarchy)
   */
  public List findVisibleMembers(Hierarchy hier) {
    List list = new ArrayList();
    
    TestAxis[] axes = model().getAxes();
    for (int i = 0; i < axes.length; i++) {
      TestAxis axis = axes[i];
      // optimize: check against axis.getHierarchies()
      for (Iterator it = axis.getPositions().iterator(); it.hasNext();) {
        Position p = (Position)it.next();
        Member[] members = p.getMembers();
        // optimize find the position of the member once
        for (int j = 0; j < members.length; j++) {
          if (members[j].getLevel().getHierarchy().equals(hier))
            list.add(members[j]);
        }
      }
    }
    return list;
  }

}
