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

import com.tonbeller.jpivot.olap.model.BooleanExpr;
import com.tonbeller.jpivot.olap.model.Visitor;

public class BooleanExprImpl implements BooleanExpr {
  private boolean value;

  public BooleanExprImpl() {
  }

  public BooleanExprImpl(boolean value) {
    this.value = value;
  }

  public boolean getValue() {
    return value;
  }

  public void setValue(boolean b) {
    value = b;
  }

  public void accept(Visitor visitor) {
    visitor.visitBooleanExpr(this);
  }

}
