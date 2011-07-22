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

import com.tonbeller.jpivot.table.span.Span;

/**
 * creates an element in a table axis.
 * 
 * @author av
 */
public interface SpanBuilder extends PartBuilder {
  interface SBContext {
    void setCaption(Element elem, String label);
    void addClickable(String href, String label);
  }
  Element build(SBContext sbctx, Span span, boolean even);
}
