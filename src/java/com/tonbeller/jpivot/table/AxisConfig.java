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
package com.tonbeller.jpivot.table;

import com.tonbeller.jpivot.table.span.PropertyConfig;
import com.tonbeller.jpivot.table.span.SpanDirections;

/**
 * exposes properties to configure an axis. A facade that simplifies
 * the configuration of an axis.
 * @author av
 */

public interface AxisConfig extends SpanDirections {
  /**
   * controls whether or not the parents of the member will be displayed
   */
  boolean isShowParentMembers();
  void setShowParentMembers(boolean showParentMembers);

  /**
   * controls whether or not a member should be indented 
   */
  boolean isMemberIndent();
  void setMemberIndent(boolean memberIndent);

  /**
   * no axis header will be created
   */
  static final int NO_HEADER = 0;

  /**
   * the hierarchies will be shown as axis headers
   */
  static final int HIERARCHY_HEADER = 1;

  /**
   * the levels will be shown as axis headers
   */
  static final int LEVEL_HEADER = 2;

  /**
   * position header on rowaxis only: shows both, hierarchy and level headers
   */
  static final int HIERARCHY_LEVEL_HEADER = 3;

  /**
   * the headers will be mixed with the members of the axis
   */
  int getHierarchyHeader();
  void setHierarchyHeader(int hierarchyHeader);

  /**
   * controls the generation of spans for member elements
   */
  int getMemberSpan();
  void setMemberSpan(int span);

  /**
   * controls the generation of spans for header elements
   */
  int getHeaderSpan();
  void setHeaderSpan(int span);

  PropertyConfig getPropertyConfig();
}
