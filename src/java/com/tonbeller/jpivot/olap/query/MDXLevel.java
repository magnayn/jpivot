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

/**
 * MDX levels have a depth, root level has depth = 0
 */
public interface MDXLevel extends MDXElement {
  
  /**
   * @return the level's depth
   */
  public int getDepth();

  /**
   * @return true, if the level is "All"
   */
  public boolean isAll();  
  
  /**
   * @return true, if the level is not Bottom level
   */
  public boolean hasChildLevel();
  

} // MDXLevel
