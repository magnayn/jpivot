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

import com.tonbeller.jpivot.olap.model.impl.MemberImpl;

/**
 * Created on 22.10.2002
 * 
 * @author av
 */
public class TestMember extends MemberImpl {
  TestMember parentMember;
  ArrayList childMember = new ArrayList();
  boolean visible;

  /**
   * Returns the childMember.
   * @return ArrayList
   */
  public ArrayList getChildMember() {
    return childMember;
  }

  /**
   * Returns the parentMember.
   * @return TestMember
   */
  public TestMember getParentMember() {
    return parentMember;
  }

  public void addChildMember(TestMember member) {
    member.setParentMember(this);
    childMember.add(member);
  }

  public boolean hasChildren() {
    return childMember.size() > 0;
  }
  
  /**
   * Sets the parentMember.
   * @param parentMember The parentMember to set
   */
  public void setParentMember(TestMember parentMember) {
    this.parentMember = parentMember;
  }


  /**
   * Returns the visible.
   * @return boolean
   */
  public boolean isVisible() {
    return visible;
  }

  /**
   * Sets the visible.
   * @param visible The visible to set
   */
  public void setVisible(boolean visible) {
    this.visible = visible;
  }

}
