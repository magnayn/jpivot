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

import com.tonbeller.jpivot.olap.model.IntegerExpr;
import com.tonbeller.jpivot.olap.model.Visitor;

public class IntegerExprImpl implements IntegerExpr {
  private int value;

  public IntegerExprImpl(){
  }

  public IntegerExprImpl(int value){
    this.value = value;
  }
  public int getValue() {
    return value;
  }

  public void setValue(int i) {
    value = i;
  }

  public void accept(Visitor visitor) {
    visitor.visitIntegerExpr(this);
  }

}
