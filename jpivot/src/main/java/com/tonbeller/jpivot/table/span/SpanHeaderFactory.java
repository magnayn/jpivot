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
 * creates a "header" span from a "member" span. The created span describes the original one,
 * e.g. a Level or Hierarchy describes a Member. The new Spans are
 * mixed within the hierarchy of the table axis.
 *
 * @author av
 */
public interface SpanHeaderFactory {

  /**
   * creates a new header <code>Span</code> for <code>span</code>
   */
  Span create(Span span);
}
