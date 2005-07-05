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

/**
 * @author av
 */
public interface SwapAxes extends Extension {
  /**
   * name of the Extension for lookup
   */
  public static final String ID = "swapAxes";

  /**
   * @return true if axes can be swapped, i.e. if the result is 
   * 2 dimensional
   */
  boolean canSwapAxes();
  
  /**
   * swaps the axes
   */
  void setSwapAxes(boolean swap);
  
  boolean isSwapAxes();
}
