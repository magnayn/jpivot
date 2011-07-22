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

import java.util.List;

import com.tonbeller.jpivot.olap.model.Hierarchy;

/**
 * allows to place a set of members on a visible query axis.
 * @author av
 */

public interface PlaceMembersOnAxes extends PlaceHierarchiesOnAxes {
  /**
   * name of the Extension for lookup
   */
  static final String ID = "membersOnAxes";
  
  /**
   * creates an expression that selects <code>members</code>. In MDX this 
   * would be an enumeration of the members within <code>{ ... }</code>
   * <p>
   * If a memberExpression returned by this method is placed on an axis, sorting
   * should be disabled. If sorting was not disabled, the explicit order or the
   * members would not be visible to the user.
   * @param members a List of Members 
   * @return a member expression that can be used with super.setAxis
   * @see PlaceHierarchiesOnAxes#setQueryAxis
   * @see com.tonbeller.jpivot.olap.model.Member
   */
  Object createMemberExpression(List members);
  
  /**
   * collects all members from the visible axes in the result. If no members of the hierarchy
   * are on a visible axis, returns an empty list.
   * @param hier the Hierarchy
   * @return A list of Members
   * @see com.tonbeller.jpivot.olap.model.Member
   */
  List findVisibleMembers(Hierarchy hier);
  
}
