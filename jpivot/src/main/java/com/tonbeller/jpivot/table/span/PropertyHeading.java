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

import com.tonbeller.jpivot.olap.model.Displayable;
import com.tonbeller.jpivot.olap.model.Visitor;

public class PropertyHeading implements Displayable {
  String label;

  public PropertyHeading(String label) {
    this.label = label;
  }

  public void accept(Visitor visitor) {
    ((SpanVisitor)visitor).visitPropertyHeading(this);
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String string) {
    label = string;
  }

}
