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

import com.tonbeller.jpivot.core.ExtensionSupport;
import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.navi.DrillExpandMember;

/**
 * @author hh
 *
 */
public class DrillExpandMemberExt extends ExtensionSupport implements DrillExpandMember {
  /**
    * Constructor sets ID
    */
  public DrillExpandMemberExt() {
    super.setId(DrillExpandMember.ID);
  }

  /**
   * @see com.tonbeller.jpivot.olap.navi.DrillExpand#canExpand(Member)
   * @param member the membber to be checked for potential expansion
   * @return true if the member can be expanded
   */
  public boolean canExpand(Member member) {
    QueryAdapter.QueryAdapterHolder model = (QueryAdapter.QueryAdapterHolder) getModel();
    return model.getQueryAdapter().canExpand(member);
  }

  /**
   * @see com.tonbeller.jpivot.olap.navi.DrillExpand#canCollapse(Member)
   * @param member member to be expanded
   * @return true if the member can be collapsed
   */
  public boolean canCollapse(Member member) {
    QueryAdapter.QueryAdapterHolder model = (QueryAdapter.QueryAdapterHolder) getModel();
    return model.getQueryAdapter().canCollapse(member);
  }

  /**
   * @see com.tonbeller.jpivot.olap.navi.DrillExpand#expand(Member)
   * @param member member to be expanded
   */
  public void expand(Member member) {
    QueryAdapter.QueryAdapterHolder model = (QueryAdapter.QueryAdapterHolder) getModel();
    model.getQueryAdapter().expand(member);
  }

  /**
   * @see com.tonbeller.jpivot.olap.navi.DrillExpand#collapse(Member)
   * @param member member to be collapsed
   */
  public void collapse(Member member) {
    QueryAdapter.QueryAdapterHolder model = (QueryAdapter.QueryAdapterHolder) getModel();
    model.getQueryAdapter().collapse(member);
  }

} // End DrillExpandMemberExt
