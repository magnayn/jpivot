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
package com.tonbeller.jpivot.table.span;

/**
 * @author av
 */
public interface SpanDirections {
  /** 
   * dont combine spans
   */
  static final int NO_SPAN = 0;
  
  /**
   * combine spans on hierarchies
   */
  static final int HIERARCHY_SPAN = 1;
  
  /**
   * combine spans on positions
   */
  static final int POSITION_SPAN = 2;
  
  /**
   * combine spans on hierarchy if possible and, if not, on positions
   */
  static final int HIERARCHY_THEN_POSITION_SPAN = 3;

  /**
   * combine spans on positions if possible and, if not, on hierarchies
   */
  static final int POSITION_THEN_HIERARCHY_SPAN = 4;

}
