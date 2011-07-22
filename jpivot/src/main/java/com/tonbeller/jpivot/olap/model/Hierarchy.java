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



/**
 * Hierarchy of a dimension
 * @see Dimension
 * @author av
 */
public interface Hierarchy extends Expression, Displayable, Visitable, Decorator {
  /**
   * get the Dimension to which this Hierarchy belongs.
   */
  Dimension getDimension();
  
  /**
   * get the Levels of this Hierarchy. Returns null, if this is a ragged hierarchy
   * that does not support levels.
   */
  Level[] getLevels();
  
  boolean hasAll();
}
