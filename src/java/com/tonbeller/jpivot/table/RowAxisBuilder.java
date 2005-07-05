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

import org.w3c.dom.Element;

import com.tonbeller.jpivot.table.span.SpanCalc;

/**
 * Created on 24.10.2002
 * 
 * @author av
 */
public interface RowAxisBuilder extends AxisBuilder {
  /**
   * return the number of rows needed for the header (corner element)
   */
  
  int getHeaderRowCount();
  /**
   * builds a row for the corner element
   */
  void buildHeaderRow(Element parent, int rowIndex);
  
  /**
   * returns the SpanCalc for header if present. return null otherwise
   */
  SpanCalc getHeaderSpanCalc();
}
