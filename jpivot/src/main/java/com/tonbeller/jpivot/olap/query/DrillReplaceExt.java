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
import com.tonbeller.jpivot.olap.model.Hierarchy;
import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.navi.DrillReplace;

/**
 * @author hh
 *
 */
public class DrillReplaceExt extends ExtensionSupport implements DrillReplace {

  /**
   * Constructor sets ID
   */
  public DrillReplaceExt() {
    super.setId(DrillReplace.ID);
  }

  /**
   * drill down is possible if <code>member</code> has children
   */
  public boolean canDrillDown(Member member) {
    QueryAdapter.QueryAdapterHolder model = (QueryAdapter.QueryAdapterHolder) getModel();
    return model.getQueryAdapter().canDrillDown(member);
  }

  /**
   * drill up is possible if not all members of the top level 
   * hierarchy are shown.
   */
  public boolean canDrillUp(Hierarchy hier) {
    QueryAdapter.QueryAdapterHolder model = (QueryAdapter.QueryAdapterHolder) getModel();
    return model.getQueryAdapter().canDrillUp(hier);
  }

  /**
   * replaces the members. Let <code>H</code> be the hierarchy
   * that member belongs to. Then drillDown will replace all members from <code>H</code>
   * that are currently visible with the children of <code>member</code>.
   */
  public void drillDown(Member member) {
    QueryAdapter.QueryAdapterHolder model = (QueryAdapter.QueryAdapterHolder) getModel();
    model.getQueryAdapter().drillDown(member);
  }

  /**
   * replaces all visible members of hier with the members of the
   * next higher level.
   */
  public void drillUp(Hierarchy hier) {
    QueryAdapter.QueryAdapterHolder model = (QueryAdapter.QueryAdapterHolder) getModel();
    model.getQueryAdapter().drillUp(hier);
  }

} // End DrillReplaceExt
