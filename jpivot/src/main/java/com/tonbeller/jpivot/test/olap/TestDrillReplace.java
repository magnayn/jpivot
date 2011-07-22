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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.tonbeller.jpivot.olap.model.Hierarchy;
import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.model.Position;
import com.tonbeller.jpivot.olap.navi.DrillReplace;

/**
 * Created on 03.12.2002
 * 
 * @author av
 */
public class TestDrillReplace extends TestExtensionSupport implements DrillReplace {

  /**
   * @see com.tonbeller.jpivot.olap.navi.DrillReplace#canDrillDown(Member)
   */
  public boolean canDrillDown(Member member) {
    return ((TestMember)member).hasChildren();
  }

  /**
   * @see com.tonbeller.jpivot.olap.navi.DrillReplace#canDrillUp(Hierarchy)
   */
  public boolean canDrillUp(Hierarchy hier) {
    TestAxis axis = TestOlapModelUtils.findAxis(model(), hier);
    int memberIndex = TestOlapModelUtils.indexOf(axis.getHierarchies(), hier);
    for (Iterator it = axis.getPositions().iterator(); it.hasNext();) {
      Member m = ((Position)it.next()).getMembers()[memberIndex];
      if (m.getRootDistance() > 0)
        return true;
    }
    return false;
  }

  /**
   * @see com.tonbeller.jpivot.olap.navi.DrillReplace#drillDown(Member)
   */
  public void drillDown(Member member) {
    TestMember tm = (TestMember)member;
    TestOlapModelUtils.setVisible(tm.getChildMember());
    TestOlapModelUtils.rebuildAxis(model(), (TestMember)member);
    fireModelChanged();
  }

  /**
   * @see com.tonbeller.jpivot.olap.navi.DrillReplace#drillUp(Hierarchy)
   */
  public void drillUp(Hierarchy hier) {
    Set parents = new HashSet();
    List memberList = new ArrayList();
    TestAxis axis = TestOlapModelUtils.findAxis(model(), hier);
    int memberIndex = TestOlapModelUtils.indexOf(axis.getHierarchies(), hier);
    loop: for (Iterator it = axis.getPositions().iterator(); it.hasNext();) {
      TestPosition position = (TestPosition)it.next();
      TestMember member = (TestMember)position.getMembers()[memberIndex];
      
      TestMember parent = member.getParentMember();
      if (parent == null)
        continue loop; // this was a root member

      // avoid duplicate parents (and children)
      Object key = parent.getParentMember();
      if (key == null) // root
        key = hier;
      if (parents.contains(key))
        continue loop;
      parents.add(key);

      // add children of grandpa      
      TestMember grandpa = parent.getParentMember();
      if (grandpa == null) {
        Member[] members = ((TestHierarchy)hier).getRootMembers();
        memberList.addAll(Arrays.asList(members));
      }
      else 
        memberList.addAll(grandpa.getChildMember());
    }
    
    TestOlapModelUtils.setVisible(memberList);
    TestOlapModelUtils.rebuildAxis(model(), axis);
    fireModelChanged();

  }

}
