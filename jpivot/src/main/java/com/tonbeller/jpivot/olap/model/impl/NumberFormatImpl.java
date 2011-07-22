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
/**
 * Created on 24.03.2003
 */
package com.tonbeller.jpivot.olap.model.impl;

import com.tonbeller.jpivot.olap.model.NumberFormat;

/**
 * @author av
 */
public class NumberFormatImpl implements NumberFormat {
  boolean grouping = true;
  int fractionDigits = 2;
  boolean percent = false;
  
  /**
   * @return int
   */
  public int getFractionDigits() {
    return fractionDigits;
  }

  /**
   * @return boolean
   */
  public boolean isGrouping() {
    return grouping;
  }

  /**
   * Sets the fractionDigits.
   * @param fractionDigits The fractionDigits to set
   */
  public void setFractionDigits(int fractionDigits) {
    this.fractionDigits = fractionDigits;
  }

  /**
   * Sets the grouping.
   * @param grouping The grouping to set
   */
  public void setGrouping(boolean grouping) {
    this.grouping = grouping;
  }

  /**
   * @see com.tonbeller.jpivot.olap.model.NumberFormat#isPercent()
   */
  public boolean isPercent() {
    return percent;
  }

}
