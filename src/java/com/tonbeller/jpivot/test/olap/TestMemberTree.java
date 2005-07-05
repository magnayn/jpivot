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

import java.util.List;

import com.tonbeller.jpivot.core.ExtensionSupport;
import com.tonbeller.jpivot.olap.model.Hierarchy;
import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.navi.MemberTree;

/**
 * Created on 22.10.2002
 * 
 * @author av
 */
public class TestMemberTree extends ExtensionSupport implements MemberTree {

  /**
   * @see com.tonbeller.jpivot.olap.navi.MemberTree#getRootMembers(Hierarchy)
   */
  public Member[] getRootMembers(Hierarchy hier) {
    return ((TestHierarchy)hier).getRootMembers();
  }

  /**
   * @see com.tonbeller.jpivot.olap.navi.MemberTree#hasChildren(Member)
   */
  public boolean hasChildren(Member member) {
    return ((TestMember)member).hasChildren();
  }

  /**
   * @see com.tonbeller.jpivot.olap.navi.MemberTree#getChildren(Member)
   */
  public Member[] getChildren(Member member) {
    List list = ((TestMember)member).getChildMember();
    return (Member[])list.toArray(new TestMember[list.size()]);
  }

  /**
   * @see com.tonbeller.jpivot.olap.navi.MemberTree#getParent(Member)
   */
  public Member getParent(Member member) {
    return ((TestMember)member).getParentMember();
  }

}
