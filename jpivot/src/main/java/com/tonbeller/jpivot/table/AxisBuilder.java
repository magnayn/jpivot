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
 * builds the row or column headings
 * 
 * @author av
 */
public interface AxisBuilder extends PartBuilder {

  /**
   * returns the number of rows. A row axis will return positionCount, a column axis
   * will return hierarchyCount
   */
  int getRowCount();

  /**
   * returns the number of columns. A row axis will return hierarchyCount, a column axis
   * will return positionCount
   */
  int getColumnCount();

  /**
   * appends one &lt;row-heading&gt; or multiple &gt;column-heading&lt; 
   * elements to parent.
   * @param parent a &lt;row&gt; element containing headings (and cells)
   * @param rowIndex - index of the row to build
   */
  void buildRow(Element parent, int rowIndex);

  /**
   * @see SpanBuilder
   */
  SpanBuilder getSpanBuilder();

  /**
   * @see SpanBuilder
   */
  void setSpanBuilder(SpanBuilder spanBuilder);
  
  SpanCalc getSpanCalc();
  
  AxisConfig getAxisConfig();
  
  AxisHeaderBuilder getAxisHeaderBuilder();
  
  void setAxisHeaderBuilder(AxisHeaderBuilder axisHeaderBuilder); 
}

