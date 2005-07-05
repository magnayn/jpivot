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
 * 
 * @author av
 */
public interface SpanConfig extends SpanDirections {

  /**
   * computes the preferred span direction of a span. On a column-axis, for example,
   * the Hierarchies may be horizontally spanned, while the Members are vertically.
   */
  int chooseSpanDirection(Span span);

  /**
   * determines whether or not two <code>Span</code>s are equal. Equal Spans may be
   * merged to a single span
   */
  boolean equals(Span span1, Span span2);

}
