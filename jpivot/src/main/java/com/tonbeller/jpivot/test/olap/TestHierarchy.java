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

import com.tonbeller.jpivot.olap.model.impl.HierarchyImpl;

/**
 * Created on 22.10.2002
 * 
 * @author av
 */
public class TestHierarchy extends HierarchyImpl {
  TestMember[] rootMembers;

  /**
   * Returns the rootMembers.
   * @return TestMember[]
   */
  public TestMember[] getRootMembers() {
    return rootMembers;
  }

  /**
   * Sets the rootMembers.
   * @param rootMembers The rootMembers to set
   */
  public void setRootMembers(TestMember[] rootMembers) {
    this.rootMembers = rootMembers;
  }

}
