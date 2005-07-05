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
import com.tonbeller.jpivot.olap.model.impl.PositionSpan;
import com.tonbeller.jpivot.olap.navi.DrillExpandPosition;

/**
 * Created on 03.12.2002
 * 
 * @author av
 */
public class TestDrillExpandPosition extends TestExtensionSupport implements DrillExpandPosition {

  public boolean canExpand(Position position, Member member) {
    return canExpand((TestPosition)position, (TestMember)member);
  }

  public boolean canCollapse(Position position, Member member) {
    return canCollapse((TestPosition)position, (TestMember)member);
  }

  public void expand(Position position, Member member) {
    expand((TestPosition)position, (TestMember)member);
    fireModelChanged();
  }

  public void collapse(Position position, Member member) {
    collapse((TestPosition)position, (TestMember)member);
    fireModelChanged();
  }


  void expand(TestPosition position, TestMember member) {
    List childPositions = new ArrayList();
    TestAxis axis = position.getAxis();
    int memberIndex = position.indexOf(member);

    PositionSpan ps = new PositionSpan(axis, position, member);
    List span = axis.getPositions().subList(ps.getStartIndex(), ps.getEndIndex());
    for (Iterator mi = member.getChildMember().iterator(); mi.hasNext();) {
      TestMember child = (TestMember) mi.next();
      for (Iterator pi = span.iterator(); pi.hasNext();) {
        Member[] members = ((Position)pi.next()).getMembers();
        members = (Member[])members.clone();
        TestPosition p = new TestPosition(axis);
        members[memberIndex] = child;
        p.setMembers(members);
        childPositions.add(p);
      }
    }
    axis.getPositions().addAll(ps.getEndIndex(), childPositions);
  }



  /**
   * removes all direct children
   */
  void collapse(TestPosition position, TestMember member) {
    List list = getDescendantPositions(position, member);
    int memberIndex = position.indexOf(member);
    for (Iterator it = list.iterator(); it.hasNext();) {
      TestPosition p = (TestPosition)it.next();
      ((TestMember)p.getMembers()[memberIndex]).setVisible(false);
    }
    
    position.getAxis().getPositions().removeAll(list);
  }


  boolean canExpand(TestPosition position, TestMember member) {
    if (!member.hasChildren())
      return false;
    return getDescendantPositions(position, member).size() == 0;
  }


  boolean canCollapse(TestPosition position, TestMember member) {
    if (!member.hasChildren())
      return false;
    return getDescendantPositions(position, member).size() > 0;
  }
  
  /**
   * returns next positions, that contain descendants of member
   */
  List getDescendantPositions(TestPosition position, TestMember member) {
    TestAxis axis = position.getAxis();
    PositionSpan ps = new PositionSpan(axis, position, member);
    Iterator iter = axis.getPositions().listIterator(ps.getEndIndex());
    int memberIndex = position.indexOf(member);
    List descendantPositions = new ArrayList();
    
    loop:while (iter.hasNext()) {
      Position p = (Position)iter.next();
      TestMember candidate = (TestMember)p.getMembers()[memberIndex];
      if (candidate.getRootDistance() <= member.getRootDistance())
        break loop;
      descendantPositions.add(p);
    }
    return descendantPositions;
    
  }
  
}
