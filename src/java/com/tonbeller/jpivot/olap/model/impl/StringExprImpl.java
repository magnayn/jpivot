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
package com.tonbeller.jpivot.olap.model.impl;

import com.tonbeller.jpivot.olap.model.StringExpr;
import com.tonbeller.jpivot.olap.model.Visitor;

public class StringExprImpl implements StringExpr {
  private String value;

  public StringExprImpl() {
  }

  public StringExprImpl(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String string) {
    value = string;
  }
  public void accept(Visitor visitor) {
    visitor.visitStringExpr(this);
  }

}
