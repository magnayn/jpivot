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
 * Created on 29.10.2002
 * 
 * @author av
 */
public class NoSpanConfig implements SpanConfig {

  /**
   * returns NO_SPAN for all cells.
   */
  public int chooseSpanDirection(Span span) {
    return SpanConfig.NO_SPAN;
  }
  
  /**
   * returns true, if the objects returned by getObject() are equal.
   */
  public boolean equals(Span span1, Span span2) {
    return span1.getObject().equals(span2.getObject());
  }

}
