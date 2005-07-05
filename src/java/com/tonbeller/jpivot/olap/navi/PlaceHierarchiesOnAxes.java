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
import com.tonbeller.jpivot.olap.model.Axis;
import com.tonbeller.jpivot.olap.model.Hierarchy;

/**
 * Allows to place hierarchies on the visible query axes. 
 * <p>
 * Use Case.
 * <ul>
 *   <li>The GUI will examine the result of the olap query to find out
 *   which hierarchies are currently displayed on what axes. It will use
 *   Axis.getHierarchies() for this
 *   
 *   <li>Then it will find out what Hierarchies exist by calling
 *   OlapModel.getDimensions() and Dimension.getHierarchies().
 * 
 *   <li>The Information will be presented to the user and he will be allowed to
 *   change the mapping between axes and hierarchies.
 * 
 *   <li>For every Hierarchy that the user selected for display on an axis,
 *   the GUI will call createMemberExpression().
 *   
 *   <li>For each axis the system will build the the array of 
 *   memberExpressions and call setAxis once.
 * 
 * </ul>
 * @author av
 */

public interface PlaceHierarchiesOnAxes extends Extension {
 
  /**
   * name of the Extension for lookup
   */
  public static final String ID = "hierarchiesOnAxes";
  
  /**
   * finds the expression that generates members for <code>hier</code>.
   * If <code>hier</code> is used in the query, the expression from the
   * query should be returned, so the navigation position remains unchanged.
   * If the hierarchy is not used in the query, a new expression is created
   * that selects the root members of the hierarchy.
   * 
   * @return a member expression
   */
  Object createMemberExpression(Hierarchy hier);

  /**
   * sets the MemberExpressions for an Axis. The axis definition is 
   * replaced by the memberExpressions. 
   */
  void setQueryAxis(Axis target, Object[] memberExpressions);
  
  /**
   * set the "expand All member" flag
   * if this flag is set and an "All" memper is put onto an axis,
   *  the children of the All member will be added as well.
   */
  void setExpandAllMember(boolean expandAllMember);
  
  /**
   * return the "expand All member" flag
   */
  boolean getExpandAllMember();
  
}
