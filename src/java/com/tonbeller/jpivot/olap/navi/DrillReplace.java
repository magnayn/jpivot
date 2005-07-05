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
package com.tonbeller.jpivot.olap.navi;

import com.tonbeller.jpivot.core.Extension;
import com.tonbeller.jpivot.olap.model.Hierarchy;
import com.tonbeller.jpivot.olap.model.Member;

/**
 * Substitues the members of a hierarchy with the children of a member.
 * Example: A table shows the continents "America", "Asia", "Europe" etc. DrillReplace
 * for "Europe" will replace the continents with the countries of "Europe".
 * @author av
 */
public interface DrillReplace extends Extension {
  /**
   * name of the Extension for lookup
   */
  public static final String ID = "drillReplace";

  /**
   * drill down is possible if <code>member</code> has children
   */
  boolean canDrillDown(Member member);
  
  /**
   * drill up is possible if not all members of the top level 
   * hierarchy are shown.
   */
  boolean canDrillUp(Hierarchy hier);
  
  /**
   * replaces the members. Let <code>H</code> be the hierarchy
   * that member belongs to. Then drillDown will replace all members from <code>H</code>
   * that are currently visible with the children of <code>member</code>.
   */
  void drillDown(Member member);
  
  /**
   * replaces all visible members of hier with the members of the
   * next higher level.
   */
  void drillUp(Hierarchy hier);

}
