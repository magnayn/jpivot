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

import com.tonbeller.jpivot.olap.model.Property;

/**
 * @author av
 */
public class IgnorePropertiesHierarchyHeaderFactory extends HierarchyHeaderFactory {
  Span previous;

  public Span create(Span span) {
    previous = super.create(span);
    return previous;
  }

  public void visitProperty(Property v) {
    header.setObject(previous.getObject());
  }

  public void visitPropertyHeading(PropertyHeading heading) {
    header.setObject(previous.getObject());
  }

}
