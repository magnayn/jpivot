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
package com.tonbeller.jpivot.olap.model;

import java.util.List;

/**
 * Axis of an OLAP Result.
 * @author av
 */
public interface Axis extends Visitable, Decorator {

  /**
   * return the positions on this axis
   * @see Position
   */
  List getPositions();
  
  /**
   * returns the hierachies that are currently visible on this axis. 
   * 0 = outermost, N = innermost. The order is significant.
   */
  Hierarchy[] getHierarchies();


}
