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

import java.util.HashSet;
import java.util.Iterator;

import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.navi.DrillExpandMember;

/**
 * Created on 22.10.2002
 * 
 * @author av
 */
public class TestDrillExpandMember extends TestExtensionSupport implements DrillExpandMember {

  HashSet expanded = new HashSet();

  protected TestOlapModel model() {
    return (TestOlapModel) super.getModel();
  }

  /**
   * @see com.tonbeller.jpivot.olap.navi.DrillExpand#canExpand(Member)
   */
  public boolean canExpand(Member member) {
    TestMember tm = (TestMember) member;
    return tm.hasChildren() && !expanded.contains(member);
  }

  /**
   * @see com.tonbeller.jpivot.olap.navi.DrillExpand#canCollapse(Member)
   */
  public boolean canCollapse(Member member) {
    TestMember tm = (TestMember) member;
    return tm.hasChildren() && expanded.contains(member);
  }

  /**
   * @see com.tonbeller.jpivot.olap.navi.DrillExpand#expand(Member)
   */

  public void expand(Member member) {
    expanded.add(member);
    TestMember tm = (TestMember) member;
    for (Iterator it = tm.getChildMember().iterator(); it.hasNext();)
       ((TestMember) it.next()).setVisible(true);
    TestOlapModelUtils.rebuildAxis(model(), tm);
    fireModelChanged();
  }

  public void collapse(Member member) {
    recurseCollapse((TestMember) member);
    TestOlapModelUtils.rebuildAxis(model(), (TestMember)member);
    fireModelChanged();
  }


  private void recurseCollapse(TestMember tm) {
    if (!expanded.contains(tm))
      return;
    expanded.remove(tm);
    for (Iterator it = tm.getChildMember().iterator(); it.hasNext();) {
      TestMember child = (TestMember) it.next();
      recurseCollapse(child);
      child.setVisible(false);
    }
  }


}
