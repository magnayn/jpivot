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
 * Cell of an OLAP result. Cells may have properties for alerting, e.g.
 * a cell may have an "arrow" property, thats value is some code
 * for an trend arrow.
 * @author av
 */
public interface Cell extends PropertyHolder, Visitable, Decorator {
  /**
   * return the value of the cell
   */
  Object getValue();
  
  /**
   * If the cells value represents a java.lang.Number, return its format. Returns null otherwise.
   * @see getValue()
   */
  NumberFormat getFormat();

  /**
   * return the formatted value of the cell
   */
  String getFormattedValue();
  
  /**
   * true if the cell is null
   */
  boolean isNull();
}
