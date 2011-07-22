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
 * Level of a Hierarchy. 
 * <p>
 * To support "ragged hierarchies" or "parent-child hierarchies" no 
 * assuptions are made regarding the number, ordering or hierarchy of Levels 
 * instances.
 * 
 * @author av
 */
public interface Level extends Expression, Displayable, Visitable, Decorator {
  /**
   * get the Hierarchy to which this Level belongs
   */
  Hierarchy getHierarchy();
  /**
   * @deprecated use ExpressionParser instead
   * get the unique name
   */
  //String getUniqueName();
}
