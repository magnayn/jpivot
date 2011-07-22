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
 * Created on 11.11.2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.tonbeller.jpivot.test.olap;

import java.util.ArrayList;
import java.util.List;

import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.navi.MemberDeleter;

/**
 * @author av
 */
public class TestMemberDeleter extends TestExtensionSupport implements MemberDeleter {

  public boolean isDeletable(Member m) {
    return "Measures".equals(m.getLevel().getLabel());
  }

  public void delete(Member m) {
    System.out.println("deleting " + m.getLabel());
    TestHierarchy hier = (TestHierarchy) m.getLevel().getHierarchy();
    TestMember[] tm = hier.getRootMembers();
    List list = new ArrayList();
    for (int i = 0; i < tm.length; i++)
      list.add(tm[i]);
    list.remove(m);
    tm = (TestMember[]) list.toArray(new TestMember[0]);
    hier.setRootMembers(tm);
    // TestOlapModelUtils.rebuildAxis(model(), (TestMember)m);
    super.fireModelChanged();
  }

}
